package sql.walker;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Immutable snapshot of script statement accumulation state.
 */
public final class ScriptParseSnapshot {
	private final LinkedHashMap<String, Object> statementTableDictionaries;
	private final LinkedHashMap<String, Object> statementQueryDictionaries;
	private final LinkedHashMap<String, Object> statementSubstitutions;
	private final LinkedHashMap<String, Object> statementArrayOutputs;
	private final LinkedHashMap<String, ScriptParseAccumulator.StatementLineRange> statementLineRanges;

	private static final ScriptParseSnapshot EMPTY = new ScriptParseSnapshot(
			new LinkedHashMap<String, Object>(),
			new LinkedHashMap<String, Object>(),
			new LinkedHashMap<String, Object>(),
			new LinkedHashMap<String, Object>(),
			new LinkedHashMap<String, ScriptParseAccumulator.StatementLineRange>());

	public ScriptParseSnapshot(
			Map<String, Object> statementTableDictionaries,
			Map<String, Object> statementQueryDictionaries,
			Map<String, Object> statementSubstitutions,
			Map<String, Object> statementArrayOutputs,
			Map<String, ScriptParseAccumulator.StatementLineRange> statementLineRanges) {
		this.statementTableDictionaries = new LinkedHashMap<String, Object>(statementTableDictionaries);
		this.statementQueryDictionaries = new LinkedHashMap<String, Object>(statementQueryDictionaries);
		this.statementSubstitutions = new LinkedHashMap<String, Object>(statementSubstitutions);
		this.statementArrayOutputs = new LinkedHashMap<String, Object>(statementArrayOutputs);
		this.statementLineRanges = new LinkedHashMap<String, ScriptParseAccumulator.StatementLineRange>(statementLineRanges);
	}

	public static ScriptParseSnapshot empty() {
		return EMPTY;
	}

	public LinkedHashMap<String, Object> statementTableDictionaries() {
		return new LinkedHashMap<String, Object>(statementTableDictionaries);
	}

	public LinkedHashMap<String, Object> statementQueryDictionaries() {
		return new LinkedHashMap<String, Object>(statementQueryDictionaries);
	}

	public LinkedHashMap<String, Object> statementSubstitutions() {
		return new LinkedHashMap<String, Object>(statementSubstitutions);
	}

	public LinkedHashMap<String, Object> statementArrayOutputs() {
		return new LinkedHashMap<String, Object>(statementArrayOutputs);
	}

	public LinkedHashMap<String, ScriptParseAccumulator.StatementLineRange> statementLineRanges() {
		return new LinkedHashMap<String, ScriptParseAccumulator.StatementLineRange>(statementLineRanges);
	}
}
