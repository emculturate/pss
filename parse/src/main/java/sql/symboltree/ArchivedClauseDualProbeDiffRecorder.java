package sql.symboltree;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import astwalkers.SqlASTWalkerHelper;

import static mumble.MumbleConstants.MUMBLE_FILTERS_KEY;
import static mumble.MumbleConstants.MUMBLE_GROUPED_BY_KEY;
import static mumble.MumbleConstants.MUMBLE_ORDERED_BY_KEY;
import static mumble.MumbleConstants.MUMBLE_QUERY_DICTIONARY_KEY;

/**
 * Phase C0 instrumentation: records what the scope-tree archived-clause probe pass
 * ({@code probeArchivedScopeClauseColumnsOnScopeTree}, {@code materializeResolved=false})
 * mutates relative to the post-convert probe snapshot and relative to its own pre-pass state.
 *
 * Enable with {@code -Dpss.archivedClause.dualProbeDiff=true} or call
 * {@link SqlParseSymbolTreeHelper#setArchivedClauseDualProbeDiffRecording(boolean)} before walking.
 */
public final class ArchivedClauseDualProbeDiffRecorder {

	public static final String SYSPROP = "pss.archivedClause.dualProbeDiff";

	public enum DiffKind {
		/** {@code table_ref} binding changed during scope-tree probe. */
		SCOPE_TREE_TABLE_REF,
		/** Query dictionary column entry changed during scope-tree probe. */
		SCOPE_TREE_QUERY_DICTIONARY,
		/** Clause binding differed between post-convert publish and pre-scope-tree probe. */
		CONVERT_TO_PRE_SCOPE_TREE_CLAUSE,
		/** Query dictionary differed between post-convert publish and pre-scope-tree probe. */
		CONVERT_TO_PRE_SCOPE_TREE_QUERY_DICTIONARY
	}

	public static final class DiffEntry {
		public final DiffKind kind;
		public final String scopePath;
		public final String clauseKey;
		public final String detail;

		public DiffEntry(DiffKind kind, String scopePath, String clauseKey, String detail) {
			this.kind = kind;
			this.scopePath = scopePath;
			this.clauseKey = clauseKey;
			this.detail = detail;
		}

		@Override
		public String toString() {
			return kind + " " + scopePath
					+ (clauseKey == null || clauseKey.isBlank() ? "" : " " + clauseKey)
					+ ": " + detail;
		}
	}

	public static final class Snapshot {
		private final Map<String, List<String>> clauseEntries;
		private final Map<String, String> queryDictionary;

		private Snapshot(
				Map<String, List<String>> clauseEntries,
				Map<String, String> queryDictionary) {
			this.clauseEntries = clauseEntries;
			this.queryDictionary = queryDictionary;
		}
	}

	private boolean enabled;
	private final List<DiffEntry> diffs = new ArrayList<>();
	private final Map<String, Snapshot> postConvertSnapshots = new LinkedHashMap<>();
	private int scopeTreeProbeInvocations;

	public boolean isEnabled() {
		return enabled || Boolean.parseBoolean(System.getProperty(SYSPROP, "false"));
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public void clear() {
		diffs.clear();
		postConvertSnapshots.clear();
		scopeTreeProbeInvocations = 0;
	}

	public List<DiffEntry> getDiffs() {
		return List.copyOf(diffs);
	}

	public int getScopeTreeProbeInvocations() {
		return scopeTreeProbeInvocations;
	}

	public int getPostConvertSnapshotCount() {
		return postConvertSnapshots.size();
	}

	public String formatReport() {
		StringBuilder report = new StringBuilder();
		report.append("Archived clause dual-probe instrumentation:")
				.append(" postConvertSnapshots=")
				.append(postConvertSnapshots.size())
				.append(", scopeTreeProbeInvocations=")
				.append(scopeTreeProbeInvocations)
				.append('\n');
		if (diffs.isEmpty()) {
			report.append("Archived clause dual-probe diff: no mutations recorded.");
			return report.toString();
		}

		Map<DiffKind, Integer> counts = new LinkedHashMap<>();
		for (DiffEntry entry : diffs) {
			counts.merge(entry.kind, 1, Integer::sum);
		}

		report.append("Archived clause dual-probe diff report (").append(diffs.size()).append(" entries):\n");
		for (Map.Entry<DiffKind, Integer> countEntry : counts.entrySet()) {
			report.append("  ").append(countEntry.getKey()).append(": ").append(countEntry.getValue()).append('\n');
		}
		for (DiffEntry entry : diffs) {
			report.append("  - ").append(entry).append('\n');
		}
		return report.toString();
	}

	public void indexPublishedScopeTree(
			String rootPath,
			Map<String, Object> scopeRoot,
			SqlASTWalkerHelper walker,
			SqlParseSymbolTreeHelper helper) {
		if (!isEnabled() || scopeRoot == null) {
			return;
		}
		indexScopeTreeRecursive(rootPath, scopeRoot, walker, helper);
	}

	/** Indexes post-convert snapshots for nodes not already recorded (union finalize before publish). */
	public void ensurePostConvertIndexed(
			String rootPath,
			Map<String, Object> scopeRoot,
			SqlASTWalkerHelper walker,
			SqlParseSymbolTreeHelper helper) {
		if (!isEnabled() || scopeRoot == null) {
			return;
		}
		indexScopeTreeRecursive(rootPath, scopeRoot, walker, helper);
	}

	public void recordScopeTreeProbe(
			String scopePath,
			Snapshot before,
			Snapshot after) {
		if (!isEnabled()) {
			return;
		}
		scopeTreeProbeInvocations++;
		recordScopeTreeProbeForGateCase();
		diffClauseSnapshots(DiffKind.SCOPE_TREE_TABLE_REF, scopePath, before, after);
		diffQueryDictionarySnapshots(
				DiffKind.SCOPE_TREE_QUERY_DICTIONARY,
				scopePath,
				before,
				after);

		Snapshot postConvert = postConvertSnapshots.get(scopePath);
		if (postConvert != null) {
			diffClauseSnapshots(DiffKind.CONVERT_TO_PRE_SCOPE_TREE_CLAUSE, scopePath, postConvert, before);
			diffQueryDictionarySnapshots(
					DiffKind.CONVERT_TO_PRE_SCOPE_TREE_QUERY_DICTIONARY,
					scopePath,
					postConvert,
					before);
		}
	}

	public Snapshot captureScopeSnapshot(
			Map<String, Object> scopePayload,
			SqlASTWalkerHelper walker,
			SqlParseSymbolTreeHelper helper) {
		Map<String, List<String>> clauseEntries = new LinkedHashMap<>();
		captureClauseList(clauseEntries, MUMBLE_FILTERS_KEY, scopePayload, walker);
		captureClauseList(clauseEntries, MUMBLE_GROUPED_BY_KEY, scopePayload, walker);
		captureClauseList(clauseEntries, MUMBLE_ORDERED_BY_KEY, scopePayload, walker);
		return new Snapshot(clauseEntries, captureQueryDictionary(scopePayload, walker));
	}

	@SuppressWarnings("unchecked")
	private void indexScopeTreeRecursive(
			String scopePath,
			Map<String, Object> scopeRoot,
			SqlASTWalkerHelper walker,
			SqlParseSymbolTreeHelper helper) {
		Snapshot snapshot = captureScopeSnapshot(scopeRoot, walker, helper);
		if (postConvertSnapshots.putIfAbsent(scopePath, snapshot) == null) {
			recordPostConvertSnapshotForGateCase();
		}

		for (Map.Entry<String, Object> entry : scopeRoot.entrySet()) {
			String nestedKey = entry.getKey();
			if (nestedKey == null || !(entry.getValue() instanceof Map<?, ?>)) {
				continue;
			}
			if (!nestedKey.startsWith("def_")
					&& !nestedKey.startsWith("query")
					&& !nestedKey.startsWith("union")
					&& !nestedKey.startsWith("intersect")) {
				continue;
			}
			indexScopeTreeRecursive(
					scopePath + "/" + nestedKey,
					(Map<String, Object>) entry.getValue(),
					walker,
					helper);
		}
	}

	@SuppressWarnings("unchecked")
	private void captureClauseList(
			Map<String, List<String>> clauseEntries,
			String clauseKey,
			Map<String, Object> scopePayload,
			SqlASTWalkerHelper walker) {
		if (scopePayload == null) {
			return;
		}
		Object listObj = scopePayload.get(clauseKey);
		if (!(listObj instanceof ArrayList<?> list)) {
			return;
		}

		ArrayList<String> formatted = new ArrayList<String>();
		for (int index = 0; index < list.size(); index++) {
			Object refObj = list.get(index);
			String columnName = walker.extractReferenceNameFromInterfaceEntry(refObj);
			String tableRef = walker.extractReferenceTableRefFromInterfaceEntry(refObj);
			Integer[] location = walker.getLineAndCharacterFromEntry(refObj);
			String locationSuffix = (location != null && location[0] != null)
					? "@" + location[0] + ":" + location[1]
					: "";
			formatted.add(index + "|" + nullToEmpty(columnName) + "|" + nullToEmpty(tableRef) + locationSuffix);
		}
		clauseEntries.put(clauseKey, formatted);
	}

	@SuppressWarnings("unchecked")
	private Map<String, String> captureQueryDictionary(
			Map<String, Object> scopePayload,
			SqlASTWalkerHelper walker) {
		Map<String, String> summary = new LinkedHashMap<>();
		if (scopePayload == null) {
			return summary;
		}
		Object dictionaryObj = scopePayload.get(MUMBLE_QUERY_DICTIONARY_KEY);
		if (!(dictionaryObj instanceof Map<?, ?> dictionaryMap)) {
			return summary;
		}
		for (Map.Entry<?, ?> entry : dictionaryMap.entrySet()) {
			if (entry.getKey() == null) {
				continue;
			}
			String columnName = entry.getKey().toString();
			summary.put(
					columnName,
					walker.formatAllLocationsForEntryInline(entry.getValue()));
		}
		return summary;
	}

	private void diffClauseSnapshots(
			DiffKind kind,
			String scopePath,
			Snapshot before,
			Snapshot after) {
		if (before == null || after == null) {
			return;
		}
		for (String clauseKey : unionKeys(before.clauseEntries.keySet(), after.clauseEntries.keySet())) {
			List<String> beforeEntries = before.clauseEntries.getOrDefault(clauseKey, List.of());
			List<String> afterEntries = after.clauseEntries.getOrDefault(clauseKey, List.of());
			int max = Math.max(beforeEntries.size(), afterEntries.size());
			for (int index = 0; index < max; index++) {
				String beforeEntry = index < beforeEntries.size() ? beforeEntries.get(index) : null;
				String afterEntry = index < afterEntries.size() ? afterEntries.get(index) : null;
				if (Objects.equals(beforeEntry, afterEntry)) {
					continue;
				}
				recordDiff(new DiffEntry(
						kind,
						scopePath,
						clauseKey,
						"[" + index + "] " + beforeEntry + " -> " + afterEntry));
			}
		}
	}

	private void diffQueryDictionarySnapshots(
			DiffKind kind,
			String scopePath,
			Snapshot before,
			Snapshot after) {
		if (before == null || after == null) {
			return;
		}
		for (String columnName : unionKeys(before.queryDictionary.keySet(), after.queryDictionary.keySet())) {
			String beforeSummary = before.queryDictionary.get(columnName);
			String afterSummary = after.queryDictionary.get(columnName);
			if (Objects.equals(beforeSummary, afterSummary)) {
				continue;
			}
			recordDiff(new DiffEntry(
					kind,
					scopePath,
					MUMBLE_QUERY_DICTIONARY_KEY,
					columnName + ": " + beforeSummary + " -> " + afterSummary));
		}
	}

	private void recordDiff(DiffEntry entry) {
		diffs.add(entry);
		GateCaseAccumulator gateCase = activeGateCase.get();
		if (gateCase != null) {
			gateCase.diffs.add(entry);
		}
	}

	private void recordScopeTreeProbeForGateCase() {
		GateCaseAccumulator gateCase = activeGateCase.get();
		if (gateCase != null) {
			gateCase.scopeTreeProbeInvocations++;
		}
	}

	private void recordPostConvertSnapshotForGateCase() {
		GateCaseAccumulator gateCase = activeGateCase.get();
		if (gateCase != null) {
			gateCase.postConvertSnapshotCount++;
		}
	}

	private static Iterable<String> unionKeys(Iterable<String> left, Iterable<String> right) {
		LinkedHashMap<String, Boolean> keys = new LinkedHashMap<>();
		for (String key : left) {
			keys.put(key, Boolean.TRUE);
		}
		for (String key : right) {
			keys.put(key, Boolean.TRUE);
		}
		return keys.keySet();
	}

	private static String nullToEmpty(String value) {
		return value == null ? "" : value;
	}

	// --- Gate-session aggregation (C1 full-gate runs) ---

	public static final class GateCaseResult {
		public final String caseName;
		public final int scopeTreeProbeInvocations;
		public final int postConvertSnapshotCount;
		public final List<DiffEntry> diffs;

		private GateCaseResult(
				String caseName,
				int scopeTreeProbeInvocations,
				int postConvertSnapshotCount,
				List<DiffEntry> diffs) {
			this.caseName = caseName;
			this.scopeTreeProbeInvocations = scopeTreeProbeInvocations;
			this.postConvertSnapshotCount = postConvertSnapshotCount;
			this.diffs = diffs;
		}

		public int diffCount() {
			return diffs.size();
		}

		public boolean hasMutations() {
			return !diffs.isEmpty();
		}
	}

	private static final class GateCaseAccumulator {
		private final String caseName;
		private int scopeTreeProbeInvocations;
		private int postConvertSnapshotCount;
		private final List<DiffEntry> diffs = new ArrayList<>();

		private GateCaseAccumulator(String caseName) {
			this.caseName = caseName;
		}

		private GateCaseResult toResult() {
			return new GateCaseResult(
					caseName,
					scopeTreeProbeInvocations,
					postConvertSnapshotCount,
					List.copyOf(diffs));
		}
	}

	private static final ThreadLocal<GateCaseAccumulator> activeGateCase = new ThreadLocal<>();
	private static final List<GateCaseResult> gateSessionResults = new ArrayList<>();

	public static void resetGateSession() {
		gateSessionResults.clear();
		activeGateCase.remove();
	}

	public static void beginGateCase(String caseName) {
		activeGateCase.set(new GateCaseAccumulator(caseName));
	}

	public static GateCaseResult endGateCase() {
		GateCaseAccumulator accumulator = activeGateCase.get();
		activeGateCase.remove();
		if (accumulator == null) {
			return null;
		}
		GateCaseResult result = accumulator.toResult();
		gateSessionResults.add(result);
		return result;
	}

	public static List<GateCaseResult> getGateSessionResults() {
		return List.copyOf(gateSessionResults);
	}

	public static String formatGateSessionReport() {
		StringBuilder report = new StringBuilder();
		int totalCases = gateSessionResults.size();
		int casesWithProbe2 = 0;
		int casesWithMutations = 0;
		int totalProbe2Invocations = 0;
		int totalDiffs = 0;

		for (GateCaseResult result : gateSessionResults) {
			totalProbe2Invocations += result.scopeTreeProbeInvocations;
			totalDiffs += result.diffCount();
			if (result.scopeTreeProbeInvocations > 0) {
				casesWithProbe2++;
			}
			if (result.hasMutations()) {
				casesWithMutations++;
			}
		}

		report.append("=== Phase C1 gate-session dual-probe report ===\n");
		report.append("Cases run: ").append(totalCases).append('\n');
		report.append("Cases with probe 2 invocations: ").append(casesWithProbe2).append('\n');
		report.append("Total probe 2 invocations: ").append(totalProbe2Invocations).append('\n');
		report.append("Cases with SCOPE_TREE_* or CONVERT_TO_PRE_SCOPE_TREE_* mutations: ")
				.append(casesWithMutations)
				.append('\n');
		report.append("Total mutation entries: ").append(totalDiffs).append('\n');

		if (casesWithMutations > 0) {
			report.append("\nMutating cases:\n");
			for (GateCaseResult result : gateSessionResults) {
				if (!result.hasMutations()) {
					continue;
				}
				report.append("  ").append(result.caseName)
						.append(" (probe2=").append(result.scopeTreeProbeInvocations)
						.append(", diffs=").append(result.diffCount())
						.append(")\n");
				for (DiffEntry entry : result.diffs) {
					report.append("    - ").append(entry).append('\n');
				}
			}
		} else {
			report.append("\nNo probe-2 mutations across the gate session.\n");
		}

		return report.toString();
	}
}
