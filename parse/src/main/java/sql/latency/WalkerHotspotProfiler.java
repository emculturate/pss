package sql.latency;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.LongAdder;

/**
 * Opt-in walker call counters for Phase 2.8 set-op / convert-egress profiling (Phase B) and
 * {@code SqlParseEventWalker} exit / column-resolution profiling (Phase C1).
 *
 * <p>Enable with {@code -Dpss.walker.hotspot.profile=true}, run a probe or timing test,
 * then inspect printed counters. Compare two runs (e.g. N=10 vs N=50) and look for keys whose
 * count ratio is closer to N² than to N — those methods are prime optimization targets.
 *
 * <p>Key families (Phase C1):
 * <ul>
 *   <li>{@code walkerExit_<rule>} — {@code SqlParseEventWalker} listener exits</li>
 *   <li>{@code columnCapture_*} — unresolved column registration during AST walk</li>
 *   <li>{@code columnArchive_*} — clause dependency harvest (WHERE / GROUP BY / ORDER BY, …)</li>
 *   <li>{@code columnResolution_<round>[_qualified|_unqualified]} — convert-egress resolution rounds</li>
 *   <li>{@code hotspot_<name>} / {@code hotspotNanos_<name>} — Phase C2.1 helper-path timing</li>
 * </ul>
 */
public final class WalkerHotspotProfiler {

	/** System property: {@code pss.walker.hotspot.profile=true} */
	public static final boolean ENABLED =
			Boolean.parseBoolean(System.getProperty("pss.walker.hotspot.profile", "false"));

	/**
	 * When profiling is enabled, accumulate per-grammar-rule nanoseconds in {@link #exitEveryRule}
	 * (C1.2 lightweight substitute for JFR on set-op probes).
	 */
	public static final boolean RULE_EXIT_TIMING =
			ENABLED || Boolean.parseBoolean(System.getProperty("pss.walker.hotspot.ruleTiming", "false"));

	private static final ConcurrentHashMap<String, LongAdder> COUNTERS = new ConcurrentHashMap<>();
	private static final List<Long> CONVERT_ELAPSED_MS =
			Collections.synchronizedList(new ArrayList<>());
	private static final List<Integer> CONVERT_GLOBAL_TABLE_DICT_SIZES =
			Collections.synchronizedList(new ArrayList<>());
	private static final List<Integer> CONVERT_LOCAL_TABLE_DICT_SIZES =
			Collections.synchronizedList(new ArrayList<>());
	private static final List<Integer> CONVERT_VISIBLE_QUERY_SOURCE_SIZES =
			Collections.synchronizedList(new ArrayList<>());

	private WalkerHotspotProfiler() {
	}

	public static void hit(String key) {
		if (!ENABLED || key == null) {
			return;
		}
		COUNTERS.computeIfAbsent(key, ignored -> new LongAdder()).increment();
	}

	/** Phase 2.8-C1: {@code SqlParseEventWalker} rule exit (or helper finalize) counter. */
	public static void hitWalkerExit(String ruleName) {
		if (!ENABLED || ruleName == null || ruleName.isBlank()) {
			return;
		}
		hit("walkerExit_" + ruleName);
	}

	/**
	 * C1.1: try-with-resources scope — counts {@code walkerExit_<rule>} and accumulates
	 * {@code walkerExitNanos_<rule>} for wall-time attribution.
	 */
	public static WalkerExitScope walkerExitScope(String ruleName) {
		return new WalkerExitScope(ruleName);
	}

	public static final class WalkerExitScope implements AutoCloseable {
		private final String ruleName;
		private final long startNanos;

		private WalkerExitScope(String ruleName) {
			this.ruleName = ruleName;
			this.startNanos = ENABLED ? System.nanoTime() : 0L;
			hitWalkerExit(ruleName);
		}

		@Override
		public void close() {
			if (!ENABLED || ruleName == null || startNanos == 0L) {
				return;
			}
			add("walkerExitNanos_" + ruleName, System.nanoTime() - startNanos);
		}
	}

	/**
	 * C2.1: try-with-resources scope for helper / AST infrastructure paths ({@code hotspot_<name>},
	 * {@code hotspotNanos_<name>}).
	 */
	public static HotspotScope hotspotScope(String name) {
		return new HotspotScope(name);
	}

	public static final class HotspotScope implements AutoCloseable {
		private final String name;
		private final long startNanos;

		private HotspotScope(String name) {
			this.name = name;
			this.startNanos = ENABLED ? System.nanoTime() : 0L;
			hit("hotspot_" + name);
		}

		@Override
		public void close() {
			if (!ENABLED || name == null || startNanos == 0L) {
				return;
			}
			add("hotspotNanos_" + name, System.nanoTime() - startNanos);
		}
	}

	/** C1.2: begin timing one ANTLR rule exit in {@code exitEveryRule}. */
	public static long ruleExitBegin() {
		return RULE_EXIT_TIMING ? System.nanoTime() : 0L;
	}

	/** C1.2: record grammar-rule exit duration (rule name from {@code SQLSelectParserParser#ruleNames}). */
	public static void ruleExitEnd(int ruleIndex, long startNanos, String[] ruleNames) {
		if (!RULE_EXIT_TIMING || startNanos == 0L || ruleNames == null) {
			return;
		}
		if (ruleIndex < 0 || ruleIndex >= ruleNames.length) {
			return;
		}
		String ruleName = ruleNames[ruleIndex];
		if (ruleName == null || ruleName.isBlank()) {
			return;
		}
		hit("ruleExit_" + ruleName);
		add("ruleExitNanos_" + ruleName, System.nanoTime() - startNanos);
	}

	/**
	 * Phase 2.8-C1: column ref registered during AST walk ({@code collectUnresolvedColumnReference}).
	 */
	public static void hitColumnCapture(boolean qualified) {
		hit("columnCapture_collectUnresolved");
		hit(qualified ? "columnCapture_qualified" : "columnCapture_unqualified");
	}

	/** Phase 2.8-C1: clause column refs harvested into archived scope lists. */
	public static void hitColumnArchive(String clauseSymbolKey) {
		hit("columnArchive_captureClauseDependencies");
		if (clauseSymbolKey != null && !clauseSymbolKey.isBlank()) {
			hit("columnArchive_" + clauseSymbolKey);
		}
	}

	/**
	 * Phase 2.8-C1: one convert-egress resolution attempt tagged by round
	 * ({@code archivedClause}, {@code interfaceLoopQualified}, …).
	 */
	public static void hitColumnResolutionRound(String resolutionRound, boolean qualified) {
		if (resolutionRound == null || resolutionRound.isBlank()) {
			return;
		}
		hit("columnResolution_round_" + resolutionRound);
		hit("columnResolution_" + resolutionRound + (qualified ? "_qualified" : "_unqualified"));
	}

	/** Accumulate a non-unit count (e.g. ancestor levels scanned in one call). */
	public static void add(String key, long delta) {
		if (!ENABLED || key == null || delta <= 0L) {
			return;
		}
		COUNTERS.computeIfAbsent(key, ignored -> new LongAdder()).add(delta);
	}

	public static long convertBegin() {
		return ENABLED ? System.nanoTime() : 0L;
	}

	/**
	 * Records one {@code convertSymbolTableToTableDictionary} invocation duration and scope sizes
	 * at exit (global walker table dictionary, local frame table dictionary, visible query refs).
	 */
	public static void convertEnd(
			long startNanos,
			int globalTableDictSize,
			int localTableDictSize,
			int visibleQuerySourceCount) {
		if (!ENABLED || startNanos == 0L) {
			return;
		}
		long elapsedNanos = System.nanoTime() - startNanos;
		long elapsedMs = elapsedNanos / 1_000_000L;
		long elapsedMicros = elapsedNanos / 1_000L;
		CONVERT_ELAPSED_MS.add(elapsedMs);
		CONVERT_GLOBAL_TABLE_DICT_SIZES.add(globalTableDictSize);
		CONVERT_LOCAL_TABLE_DICT_SIZES.add(localTableDictSize);
		CONVERT_VISIBLE_QUERY_SOURCE_SIZES.add(visibleQuerySourceCount);
		hit("convertTiming_samples");
		add("convertTiming_totalMs", elapsedMs);
		add("convertTiming_totalMicros", elapsedMicros);
		add("convertTiming_globalTableDictSizeSum", globalTableDictSize);
		add("convertTiming_localTableDictSizeSum", localTableDictSize);
		add("convertTiming_visibleQuerySourceSizeSum", visibleQuerySourceCount);
	}

	public static void reset() {
		COUNTERS.clear();
		CONVERT_ELAPSED_MS.clear();
		CONVERT_GLOBAL_TABLE_DICT_SIZES.clear();
		CONVERT_LOCAL_TABLE_DICT_SIZES.clear();
		CONVERT_VISIBLE_QUERY_SOURCE_SIZES.clear();
	}

	public static Map<String, Long> snapshot() {
		List<String> keys = new ArrayList<>(COUNTERS.keySet());
		Collections.sort(keys);
		LinkedHashMap<String, Long> snap = new LinkedHashMap<>();
		for (String key : keys) {
			LongAdder adder = COUNTERS.get(key);
			if (adder != null) {
				snap.put(key, adder.sum());
			}
		}
		return snap;
	}

	public static void report(String label) {
		if (!ENABLED) {
			System.out.println("WALKER_HOTSPOT disabled — pass -Dpss.walker.hotspot.profile=true");
			return;
		}
		System.out.println("=== WALKER_HOTSPOT " + label + " ===");
		for (Map.Entry<String, Long> entry : snapshot().entrySet()) {
			System.out.println("  " + entry.getKey() + "=" + entry.getValue());
		}
		reportConvertBreakdown(label);
		reportWalkerExitTiming(label, 25);
		reportHotspotTiming(label, 25);
		if (RULE_EXIT_TIMING) {
			reportRuleExitTiming(label, 40);
		}
	}

	public static void reportConvertBreakdown(String label) {
		if (!ENABLED || CONVERT_ELAPSED_MS.isEmpty()) {
			return;
		}
		List<Long> durations = new ArrayList<>(CONVERT_ELAPSED_MS);
		Collections.sort(durations);
		long totalMs = 0L;
		for (long ms : durations) {
			totalMs += ms;
		}
		long minMs = durations.get(0);
		long maxMs = durations.get(durations.size() - 1);
		long p50Ms = durations.get(durations.size() / 2);
		long p90Ms = durations.get(Math.min(durations.size() - 1, (int) Math.ceil(durations.size() * 0.9) - 1));

		int sampleCount = durations.size();
		int headCount = Math.min(5, sampleCount);
		int tailCount = Math.min(5, sampleCount);
		long headSum = 0L;
		for (int i = 0; i < headCount; i++) {
			headSum += durations.get(i);
		}
		long tailSum = 0L;
		for (int i = sampleCount - tailCount; i < sampleCount; i++) {
			tailSum += durations.get(i);
		}
		double headAvg = headCount == 0 ? 0.0d : headSum * 1.0d / headCount;
		double tailAvg = tailCount == 0 ? 0.0d : tailSum * 1.0d / tailCount;
		double tailOverHead = headAvg == 0.0d ? 0.0d : tailAvg / headAvg;

		int firstGlobalSize = CONVERT_GLOBAL_TABLE_DICT_SIZES.isEmpty()
				? 0
				: CONVERT_GLOBAL_TABLE_DICT_SIZES.get(0);
		int lastGlobalSize = CONVERT_GLOBAL_TABLE_DICT_SIZES.isEmpty()
				? 0
				: CONVERT_GLOBAL_TABLE_DICT_SIZES.get(CONVERT_GLOBAL_TABLE_DICT_SIZES.size() - 1);
		int firstVisibleQuerySources = CONVERT_VISIBLE_QUERY_SOURCE_SIZES.isEmpty()
				? 0
				: CONVERT_VISIBLE_QUERY_SOURCE_SIZES.get(0);
		int lastVisibleQuerySources = CONVERT_VISIBLE_QUERY_SOURCE_SIZES.isEmpty()
				? 0
				: CONVERT_VISIBLE_QUERY_SOURCE_SIZES.get(CONVERT_VISIBLE_QUERY_SOURCE_SIZES.size() - 1);

		System.out.println("=== WALKER_HOTSPOT convert breakdown " + label + " ===");
		System.out.println(
				"  samples=" + sampleCount
						+ " totalMs=" + totalMs
						+ " totalMicros=" + snapshot().getOrDefault("convertTiming_totalMicros", 0L)
						+ " avgMs=" + String.format("%.1f", totalMs * 1.0d / sampleCount)
						+ " minMs=" + minMs
						+ " p50Ms=" + p50Ms
						+ " p90Ms=" + p90Ms
						+ " maxMs=" + maxMs);
		System.out.println(
				"  first5avgMs=" + String.format("%.1f", headAvg)
						+ " last5avgMs=" + String.format("%.1f", tailAvg)
						+ " last5/first5=" + String.format("%.2f", tailOverHead));
		System.out.println(
				"  globalTableDictSize first=" + firstGlobalSize
						+ " last=" + lastGlobalSize
						+ " visibleQuerySources first=" + firstVisibleQuerySources
						+ " last=" + lastVisibleQuerySources);
	}

	/**
	 * C1.1: ranked walker listener exits by accumulated nanoseconds ({@code walkerExitNanos_*}).
	 */
	public static void reportWalkerExitTiming(String label, int topN) {
		if (!ENABLED) {
			return;
		}
		Map<String, Long> snap = snapshot();
		List<Map.Entry<String, Long>> ranked = new ArrayList<>();
		long totalNanos = 0L;
		for (Map.Entry<String, Long> entry : snap.entrySet()) {
			String key = entry.getKey();
			if (!key.startsWith("walkerExitNanos_")) {
				continue;
			}
			long nanos = entry.getValue();
			totalNanos += nanos;
			ranked.add(Map.entry(key.substring("walkerExitNanos_".length()), nanos));
		}
		ranked.sort(Comparator.comparingLong((Map.Entry<String, Long> e) -> e.getValue()).reversed());
		System.out.println("=== WALKER_HOTSPOT walker exit timing " + label + " (top " + topN + ") ===");
		System.out.println("  totalWalkerExitNanos=" + totalNanos
				+ " totalWalkerExitMs=" + String.format("%.1f", totalNanos / 1_000_000.0d));
		int limit = Math.min(topN, ranked.size());
		for (int i = 0; i < limit; i++) {
			Map.Entry<String, Long> entry = ranked.get(i);
			String rule = entry.getKey();
			long nanos = entry.getValue();
			long hits = snap.getOrDefault("walkerExit_" + rule, 0L);
			double avgMicros = hits == 0L ? 0.0d : (nanos / 1_000.0d) / hits;
			double pct = totalNanos == 0L ? 0.0d : (nanos * 100.0d) / totalNanos;
			System.out.println(
					"  " + rule
							+ " totalMs=" + String.format("%.2f", nanos / 1_000_000.0d)
							+ " hits=" + hits
							+ " avgMicros=" + String.format("%.1f", avgMicros)
							+ " pct=" + String.format("%.1f", pct));
		}
	}

	/**
	 * C1.2: ranked ANTLR grammar rule exits from {@code exitEveryRule} ({@code ruleExitNanos_*}).
	 */
	public static void reportRuleExitTiming(String label, int topN) {
		if (!RULE_EXIT_TIMING) {
			return;
		}
		Map<String, Long> snap = snapshot();
		List<Map.Entry<String, Long>> ranked = new ArrayList<>();
		long totalNanos = 0L;
		for (Map.Entry<String, Long> entry : snap.entrySet()) {
			String key = entry.getKey();
			if (!key.startsWith("ruleExitNanos_")) {
				continue;
			}
			long nanos = entry.getValue();
			totalNanos += nanos;
			ranked.add(Map.entry(key.substring("ruleExitNanos_".length()), nanos));
		}
		ranked.sort(Comparator.comparingLong((Map.Entry<String, Long> e) -> e.getValue()).reversed());
		System.out.println("=== WALKER_HOTSPOT grammar rule exit timing " + label + " (top " + topN + ") ===");
		System.out.println("  totalRuleExitNanos=" + totalNanos
				+ " totalRuleExitMs=" + String.format("%.1f", totalNanos / 1_000_000.0d));
		int limit = Math.min(topN, ranked.size());
		for (int i = 0; i < limit; i++) {
			Map.Entry<String, Long> entry = ranked.get(i);
			String rule = entry.getKey();
			long nanos = entry.getValue();
			long hits = snap.getOrDefault("ruleExit_" + rule, 0L);
			double avgMicros = hits == 0L ? 0.0d : (nanos / 1_000.0d) / hits;
			double pct = totalNanos == 0L ? 0.0d : (nanos * 100.0d) / totalNanos;
			System.out.println(
					"  " + rule
							+ " totalMs=" + String.format("%.2f", nanos / 1_000_000.0d)
							+ " hits=" + hits
							+ " avgMicros=" + String.format("%.1f", avgMicros)
							+ " pct=" + String.format("%.1f", pct));
		}
	}

	/**
	 * C2.1: ranked helper / infrastructure paths ({@code hotspotNanos_*}).
	 */
	public static void reportHotspotTiming(String label, int topN) {
		if (!ENABLED) {
			return;
		}
		Map<String, Long> snap = snapshot();
		List<Map.Entry<String, Long>> ranked = new ArrayList<>();
		long totalNanos = 0L;
		for (Map.Entry<String, Long> entry : snap.entrySet()) {
			String key = entry.getKey();
			if (!key.startsWith("hotspotNanos_")) {
				continue;
			}
			long nanos = entry.getValue();
			totalNanos += nanos;
			ranked.add(Map.entry(key.substring("hotspotNanos_".length()), nanos));
		}
		ranked.sort(Comparator.comparingLong((Map.Entry<String, Long> e) -> e.getValue()).reversed());
		System.out.println("=== WALKER_HOTSPOT helper hotspot timing " + label + " (top " + topN + ") ===");
		System.out.println("  totalHotspotNanos=" + totalNanos
				+ " totalHotspotMs=" + String.format("%.1f", totalNanos / 1_000_000.0d));
		int limit = Math.min(topN, ranked.size());
		for (int i = 0; i < limit; i++) {
			Map.Entry<String, Long> entry = ranked.get(i);
			String name = entry.getKey();
			long nanos = entry.getValue();
			long hits = snap.getOrDefault("hotspot_" + name, 0L);
			double avgMicros = hits == 0L ? 0.0d : (nanos / 1_000.0d) / hits;
			double pct = totalNanos == 0L ? 0.0d : (nanos * 100.0d) / totalNanos;
			System.out.println(
					"  " + name
							+ " totalMs=" + String.format("%.2f", nanos / 1_000_000.0d)
							+ " hits=" + hits
							+ " avgMicros=" + String.format("%.1f", avgMicros)
							+ " pct=" + String.format("%.1f", pct));
		}
	}

	public static void reportHotspotTimingScaling(
			String smallLabel,
			Map<String, Long> smallCounts,
			String largeLabel,
			Map<String, Long> largeCounts,
			int topN) {
		if (!ENABLED) {
			return;
		}
		System.out.println(
				"=== WALKER_HOTSPOT helper hotspot timing scaling " + smallLabel + " -> " + largeLabel + " ===");
		List<String> names = new ArrayList<>();
		for (String key : largeCounts.keySet()) {
			if (key.startsWith("hotspotNanos_")) {
				names.add(key.substring("hotspotNanos_".length()));
			}
		}
		for (String key : smallCounts.keySet()) {
			if (key.startsWith("hotspotNanos_")) {
				String name = key.substring("hotspotNanos_".length());
				if (!names.contains(name)) {
					names.add(name);
				}
			}
		}
		List<Map.Entry<String, Double>> ratios = new ArrayList<>();
		for (String name : names) {
			long smallNanos = smallCounts.getOrDefault("hotspotNanos_" + name, 0L);
			long largeNanos = largeCounts.getOrDefault("hotspotNanos_" + name, 0L);
			if (smallNanos == 0L && largeNanos == 0L) {
				continue;
			}
			double ratio = smallNanos == 0L ? Double.POSITIVE_INFINITY : (largeNanos * 1.0d) / smallNanos;
			ratios.add(Map.entry(name, ratio));
		}
		ratios.sort(Comparator.comparingDouble((Map.Entry<String, Double> e) -> e.getValue()).reversed());
		int limit = Math.min(topN, ratios.size());
		for (int i = 0; i < limit; i++) {
			Map.Entry<String, Double> entry = ratios.get(i);
			String name = entry.getKey();
			long smallNanos = smallCounts.getOrDefault("hotspotNanos_" + name, 0L);
			long largeNanos = largeCounts.getOrDefault("hotspotNanos_" + name, 0L);
			long smallHits = smallCounts.getOrDefault("hotspot_" + name, 0L);
			long largeHits = largeCounts.getOrDefault("hotspot_" + name, 0L);
			String ratioText = Double.isInfinite(entry.getValue())
					? "inf"
					: String.format("%.2f", entry.getValue());
			System.out.println(
					"  " + name
							+ "  nanosRatio=" + ratioText
							+ "  hitsRatio="
							+ (smallHits == 0L ? "inf" : String.format("%.2f", largeHits * 1.0d / smallHits))
							+ "  " + smallLabel + "Ms=" + String.format("%.2f", smallNanos / 1_000_000.0d)
							+ "  " + largeLabel + "Ms=" + String.format("%.2f", largeNanos / 1_000_000.0d));
		}
	}

	/**
	 * Compares walker-exit nanosecond totals between two runs (e.g. N10 vs N50). Surfaces rules
	 * whose total time scales superlinearly even when hit counts scale linearly.
	 */
	public static void reportWalkerExitTimingScaling(
			String smallLabel,
			Map<String, Long> smallCounts,
			String largeLabel,
			Map<String, Long> largeCounts,
			int topN) {
		if (!ENABLED) {
			return;
		}
		System.out.println(
				"=== WALKER_HOTSPOT walker exit timing scaling " + smallLabel + " -> " + largeLabel + " ===");
		List<String> rules = new ArrayList<>();
		for (String key : largeCounts.keySet()) {
			if (key.startsWith("walkerExitNanos_")) {
				rules.add(key.substring("walkerExitNanos_".length()));
			}
		}
		for (String key : smallCounts.keySet()) {
			if (key.startsWith("walkerExitNanos_")) {
				String rule = key.substring("walkerExitNanos_".length());
				if (!rules.contains(rule)) {
					rules.add(rule);
				}
			}
		}
		List<Map.Entry<String, Double>> ratios = new ArrayList<>();
		for (String rule : rules) {
			long smallNanos = smallCounts.getOrDefault("walkerExitNanos_" + rule, 0L);
			long largeNanos = largeCounts.getOrDefault("walkerExitNanos_" + rule, 0L);
			if (smallNanos == 0L && largeNanos == 0L) {
				continue;
			}
			double ratio = smallNanos == 0L ? Double.POSITIVE_INFINITY : (largeNanos * 1.0d) / smallNanos;
			ratios.add(Map.entry(rule, ratio));
		}
		ratios.sort(Comparator.comparingDouble((Map.Entry<String, Double> e) -> e.getValue()).reversed());
		int limit = Math.min(topN, ratios.size());
		for (int i = 0; i < limit; i++) {
			Map.Entry<String, Double> entry = ratios.get(i);
			String rule = entry.getKey();
			long smallNanos = smallCounts.getOrDefault("walkerExitNanos_" + rule, 0L);
			long largeNanos = largeCounts.getOrDefault("walkerExitNanos_" + rule, 0L);
			long smallHits = smallCounts.getOrDefault("walkerExit_" + rule, 0L);
			long largeHits = largeCounts.getOrDefault("walkerExit_" + rule, 0L);
			String ratioText = Double.isInfinite(entry.getValue())
					? "inf"
					: String.format("%.2f", entry.getValue());
			System.out.println(
					"  " + rule
							+ "  nanosRatio=" + ratioText
							+ "  hitsRatio="
							+ (smallHits == 0L ? "inf" : String.format("%.2f", largeHits * 1.0d / smallHits))
							+ "  " + smallLabel + "Ms=" + String.format("%.2f", smallNanos / 1_000_000.0d)
							+ "  " + largeLabel + "Ms=" + String.format("%.2f", largeNanos / 1_000_000.0d));
		}
	}

	/**
	 * Prints per-key scaling ratios between two snapshots (large / small). Keys are sorted by
	 * descending ratio so the worst superlinear offenders float to the top.
	 */
	public static void reportScaling(
			String smallLabel,
			Map<String, Long> smallCounts,
			String largeLabel,
			Map<String, Long> largeCounts) {
		if (!ENABLED) {
			return;
		}
		System.out.println("=== WALKER_HOTSPOT scaling " + smallLabel + " -> " + largeLabel + " ===");
		List<String> keys = new ArrayList<>(largeCounts.keySet());
		keys.addAll(smallCounts.keySet());
		keys = keys.stream().distinct().sorted().toList();

		List<Map.Entry<String, Double>> ratios = new ArrayList<>();
		for (String key : keys) {
			long small = smallCounts.getOrDefault(key, 0L);
			long large = largeCounts.getOrDefault(key, 0L);
			if (small == 0L && large == 0L) {
				continue;
			}
			double ratio = small == 0L ? Double.POSITIVE_INFINITY : (large * 1.0d) / small;
			ratios.add(Map.entry(key, ratio));
		}
		ratios.sort(Comparator.comparingDouble((Map.Entry<String, Double> e) -> e.getValue()).reversed());

		for (Map.Entry<String, Double> entry : ratios) {
			String key = entry.getKey();
			long small = smallCounts.getOrDefault(key, 0L);
			long large = largeCounts.getOrDefault(key, 0L);
			String ratioText = Double.isInfinite(entry.getValue())
					? "inf"
					: String.format("%.2f", entry.getValue());
			System.out.println(
					"  " + key
							+ "  " + smallLabel + "=" + small
							+ "  " + largeLabel + "=" + large
							+ "  ratio=" + ratioText);
		}
	}

	/**
	 * At fixed N, compares distinct-table vs shared-table probe snapshots. {@code sharedOverDistinct}
	 * &gt; 1 means shared-table mode is more expensive for that counter.
	 */
	public static void reportTableModeComparison(
			String nLabel,
			long distinctElapsedMs,
			Map<String, Long> distinctCounts,
			long sharedElapsedMs,
			Map<String, Long> sharedCounts) {
		if (!ENABLED) {
			return;
		}
		double wallRatio = distinctElapsedMs == 0L ? 0.0d : (sharedElapsedMs * 1.0d) / distinctElapsedMs;
		System.out.println("=== WALKER_HOTSPOT table mode " + nLabel + " (shared / distinct) ===");
		System.out.println(
				"  wallElapsedMs distinct=" + distinctElapsedMs
						+ " shared=" + sharedElapsedMs
						+ " sharedOverDistinct=" + String.format("%.2f", wallRatio));

		List<String> keys = new ArrayList<>(distinctCounts.keySet());
		keys.addAll(sharedCounts.keySet());
		keys = keys.stream().distinct().sorted().toList();

		List<Map.Entry<String, Double>> ratios = new ArrayList<>();
		for (String key : keys) {
			long distinct = distinctCounts.getOrDefault(key, 0L);
			long shared = sharedCounts.getOrDefault(key, 0L);
			if (distinct == 0L && shared == 0L) {
				continue;
			}
			double ratio = distinct == 0L ? Double.POSITIVE_INFINITY : (shared * 1.0d) / distinct;
			ratios.add(Map.entry(key, ratio));
		}
		ratios.sort(Comparator.comparingDouble((Map.Entry<String, Double> e) -> e.getValue()).reversed());

		for (Map.Entry<String, Double> entry : ratios) {
			String key = entry.getKey();
			long distinct = distinctCounts.getOrDefault(key, 0L);
			long shared = sharedCounts.getOrDefault(key, 0L);
			String ratioText = Double.isInfinite(entry.getValue())
					? "inf"
					: String.format("%.2f", entry.getValue());
			System.out.println(
					"  " + key
							+ "  distinct=" + distinct
							+ "  shared=" + shared
							+ "  sharedOverDistinct=" + ratioText);
		}
	}

	/**
	 * Compares N-scaling under distinct vs shared table modes. Flags keys whose shared-mode
	 * N50/N10 ratio diverges from distinct-mode (possible disproportionate win/loss).
	 */
	public static void reportDisproportionateTableModeScaling(
			Map<String, Long> distinctN10,
			Map<String, Long> distinctN50,
			Map<String, Long> sharedN10,
			Map<String, Long> sharedN50,
			double divergenceThreshold) {
		if (!ENABLED) {
			return;
		}
		System.out.println(
				"=== WALKER_HOTSPOT disproportionate scaling (shared vs distinct N-ratio, threshold="
						+ divergenceThreshold
						+ ") ===");
		List<String> keys = new ArrayList<>(distinctN50.keySet());
		keys.addAll(sharedN50.keySet());
		keys = keys.stream().distinct().sorted().toList();

		List<Map.Entry<String, Double>> divergences = new ArrayList<>();
		for (String key : keys) {
			long d10 = distinctN10.getOrDefault(key, 0L);
			long d50 = distinctN50.getOrDefault(key, 0L);
			long s10 = sharedN10.getOrDefault(key, 0L);
			long s50 = sharedN50.getOrDefault(key, 0L);
			if ((d10 == 0L && d50 == 0L) || (s10 == 0L && s50 == 0L)) {
				continue;
			}
			double distinctRatio = d10 == 0L ? Double.POSITIVE_INFINITY : (d50 * 1.0d) / d10;
			double sharedRatio = s10 == 0L ? Double.POSITIVE_INFINITY : (s50 * 1.0d) / s10;
			if (Double.isInfinite(distinctRatio) || Double.isInfinite(sharedRatio)) {
				continue;
			}
			double divergence = sharedRatio - distinctRatio;
			if (Math.abs(divergence) >= divergenceThreshold) {
				divergences.add(Map.entry(key, divergence));
			}
		}
		divergences.sort(Comparator.comparingDouble((Map.Entry<String, Double> e) -> Math.abs(e.getValue()))
				.reversed());

		for (Map.Entry<String, Double> entry : divergences) {
			String key = entry.getKey();
			long d10 = distinctN10.getOrDefault(key, 0L);
			long d50 = distinctN50.getOrDefault(key, 0L);
			long s10 = sharedN10.getOrDefault(key, 0L);
			long s50 = sharedN50.getOrDefault(key, 0L);
			double distinctRatio = d10 == 0L ? 0.0d : d50 * 1.0d / d10;
			double sharedRatio = s10 == 0L ? 0.0d : s50 * 1.0d / s10;
			System.out.println(
					"  " + key
							+ "  distinctRatio=" + String.format("%.2f", distinctRatio)
							+ "  sharedRatio=" + String.format("%.2f", sharedRatio)
							+ "  sharedMinusDistinct=" + String.format("%.2f", entry.getValue())
							+ "  (d10=" + d10 + " d50=" + d50 + " s10=" + s10 + " s50=" + s50 + ")");
		}
	}
}
