package sql.walker;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.antlr.v4.runtime.Token;

/**
 * Owns script statement sequence and per-statement snapshot accumulation.
 */
public final class ScriptParseAccumulator {
	public static final class StatementLineRange {
		private final int startLine;
		private final int endLine;

		public StatementLineRange(int startLine, int endLine) {
			this.startLine = startLine;
			this.endLine = endLine;
		}

		public int startLine() {
			return startLine;
		}

		public int endLine() {
			return endLine;
		}
	}

	private int scriptStatementSequence;
	private final LinkedHashMap<String, Object> scriptStatementTableDictionaries;
	private final LinkedHashMap<String, Object> scriptStatementQueryDictionaries;
	private final LinkedHashMap<String, Object> scriptStatementSubstitutions;
	private final LinkedHashMap<String, Object> scriptStatementArrayOutputs;
	private final LinkedHashMap<String, StatementLineRange> scriptStatementLineRanges;

	public ScriptParseAccumulator() {
		this.scriptStatementTableDictionaries = new LinkedHashMap<String, Object>();
		this.scriptStatementQueryDictionaries = new LinkedHashMap<String, Object>();
		this.scriptStatementSubstitutions = new LinkedHashMap<String, Object>();
		this.scriptStatementArrayOutputs = new LinkedHashMap<String, Object>();
		this.scriptStatementLineRanges = new LinkedHashMap<String, StatementLineRange>();
		reset();
	}

	public void reset() {
		scriptStatementSequence = 0;
		scriptStatementTableDictionaries.clear();
		scriptStatementQueryDictionaries.clear();
		scriptStatementSubstitutions.clear();
		scriptStatementArrayOutputs.clear();
		scriptStatementLineRanges.clear();
	}

	public int nextStatementSequence() {
		scriptStatementSequence += 1;
		return scriptStatementSequence;
	}

	public void recordLineRange(int statementSequence, Token start, Token stop) {
		int startLine = (start == null) ? -1 : start.getLine();
		int endLine = (stop == null || stop.getLine() <= 0) ? startLine : stop.getLine();
		if (startLine > 0) {
			scriptStatementLineRanges.put(Integer.toString(statementSequence), new StatementLineRange(startLine, endLine));
		}
	}

	public void captureStatementSnapshot(
			int statementSequence,
			Map<String, Object> tableDictionary,
			Map<String, Object> queryColumnDictionary,
			Map<String, Object> substitutions,
			Set<String> queryInterface) {
		String statementKey = Integer.toString(statementSequence);
		scriptStatementTableDictionaries.put(statementKey, deepCopyMap(tableDictionary));
		scriptStatementQueryDictionaries.put(statementKey, deepCopyMap(queryColumnDictionary));
		scriptStatementSubstitutions.put(statementKey, deepCopyMap(substitutions));

		LinkedHashMap<String, Object> arrayOutputMap = new LinkedHashMap<String, Object>();
		arrayOutputMap.put("queryInterface", new ArrayList<String>(queryInterface));
		scriptStatementArrayOutputs.put(statementKey, arrayOutputMap);
	}

	public boolean hasLineRanges() {
		return !scriptStatementLineRanges.isEmpty();
	}

	public String findStatementKeyForLine(int line) {
		for (Map.Entry<String, StatementLineRange> entry : scriptStatementLineRanges.entrySet()) {
			StatementLineRange range = entry.getValue();
			if (range != null && line >= range.startLine() && line <= range.endLine()) {
				return entry.getKey();
			}
		}
		return null;
	}

	public ScriptParseSnapshot snapshot() {
		return new ScriptParseSnapshot(
				scriptStatementTableDictionaries,
				scriptStatementQueryDictionaries,
				scriptStatementSubstitutions,
				scriptStatementArrayOutputs,
				scriptStatementLineRanges);
	}

	@SuppressWarnings("unchecked")
	private LinkedHashMap<String, Object> deepCopyMap(Map<String, Object> sourceMap) {
		if (sourceMap == null) {
			return new LinkedHashMap<String, Object>();
		}

		LinkedHashMap<String, Object> copy = new LinkedHashMap<String, Object>();
		for (Map.Entry<String, Object> entry : sourceMap.entrySet()) {
			Object value = entry.getValue();
			if (value instanceof Map<?, ?>) {
				copy.put(entry.getKey(), deepCopyMap((Map<String, Object>) value));
			} else if (value instanceof List<?> valueListObj) {
				copy.put(entry.getKey(), new ArrayList<Object>((List<Object>) valueListObj));
			} else if (value instanceof Set<?> valueSetObj) {
				copy.put(entry.getKey(), new LinkedHashSet<Object>((Set<Object>) valueSetObj));
			} else {
				copy.put(entry.getKey(), value);
			}
		}
		return copy;
	}
}
