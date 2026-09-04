package sql.walker;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.antlr.v4.runtime.Token;

import errorhandling.ParseDiagnostic;
import static mumble.SQLParserEndPoints.SQLPARSER_SCRIPT_TREE_KEY;
import static mumble.SQLParserEndPoints.SQLPARSER_SQL_TREE_KEY;

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
	private final ArrayDeque<Integer> scriptStatementSequenceStack = new ArrayDeque<Integer>();
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
		scriptStatementSequenceStack.clear();
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

	public int beginStatement() {
		int statementSequence = nextStatementSequence();
		scriptStatementSequenceStack.push(statementSequence);
		return statementSequence;
	}

	public boolean hasActiveStatement() {
		return !scriptStatementSequenceStack.isEmpty();
	}

	public int endStatement() {
		if (scriptStatementSequenceStack.isEmpty()) {
			return -1;
		}
		return scriptStatementSequenceStack.pop();
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

	public boolean hasArrayOutputs() {
		return !scriptStatementArrayOutputs.isEmpty();
	}

	/**
	 * Builds the SCRIPT branch of {@code Snippet.arrayOutputCollectorsMap}.
	 */
	public LinkedHashMap<String, Object> buildScriptArrayOutputCollectorsMap() {
		LinkedHashMap<String, Object> collectors = new LinkedHashMap<>();
		collectors.put(SQLPARSER_SCRIPT_TREE_KEY, snapshot().statementArrayOutputs());
		return collectors;
	}

	/**
	 * Builds the SQL (single-statement) branch of {@code Snippet.arrayOutputCollectorsMap}.
	 */
	public LinkedHashMap<String, Object> buildSqlArrayOutputCollectorsMap(Set<String> queryInterface) {
		LinkedHashMap<String, Object> sqlArrays = new LinkedHashMap<>();
		sqlArrays.put("queryInterface", new ArrayList<>(queryInterface));
		LinkedHashMap<String, Object> collectors = new LinkedHashMap<>();
		collectors.put(SQLPARSER_SQL_TREE_KEY, sqlArrays);
		return collectors;
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

	/**
	 * Prefixes walker diagnostics with the SCRIPT statement key and line captured during the walk.
	 */
	public List<ParseDiagnostic> prefixWalkerDiagnostics(List<ParseDiagnostic> diagnostics) {
		if (diagnostics == null || diagnostics.isEmpty() || !hasLineRanges()) {
			return diagnostics;
		}

		List<ParseDiagnostic> updated = new ArrayList<>(diagnostics.size());
		for (ParseDiagnostic diagnostic : diagnostics) {
			if (diagnostic == null
					|| !isPrefixedWalkerSeverity(diagnostic.severity())
					|| diagnostic.source() == null
					|| !diagnostic.source().contains("SqlASTWalker")) {
				updated.add(diagnostic);
				continue;
			}

			Integer line = diagnostic.line();
			if (line == null || line.intValue() <= 0) {
				updated.add(diagnostic);
				continue;
			}

			String statementKey = findStatementKeyForLine(line);
			if (statementKey == null) {
				updated.add(diagnostic);
				continue;
			}

			String prefix = "Statement " + statementKey + " (l:" + line + "): ";
			String message = diagnostic.message();
			if (message == null || message.isBlank() || message.startsWith(prefix)) {
				updated.add(diagnostic);
				continue;
			}

			updated.add(new ParseDiagnostic(
					diagnostic.severity(),
					diagnostic.code(),
					prefix + message,
					diagnostic.line(),
					diagnostic.charPositionInLine(),
					diagnostic.source(),
					diagnostic.ruleName(),
					diagnostic.tokenText(),
					diagnostic.recoverable(),
					diagnostic.phase(),
					diagnostic.exceptionType(),
					diagnostic.details()));
		}

		return updated;
	}

	private static boolean isPrefixedWalkerSeverity(ParseDiagnostic.Severity severity) {
		return severity == ParseDiagnostic.Severity.FATAL
				|| severity == ParseDiagnostic.Severity.ERROR
				|| severity == ParseDiagnostic.Severity.SEVERE_WARNING;
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
