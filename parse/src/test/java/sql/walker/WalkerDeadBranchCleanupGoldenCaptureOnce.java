package sql.walker;

import sql.SQLSelectParserParser;

/** Regenerate inline expectations for walker dead-branch cleanup exemplars. */
public class WalkerDeadBranchCleanupGoldenCaptureOnce extends AbstractSqlParseEventWalkerTest {

	private static final String[][] CASES = {
			{ "insertOverwritePreambleDeadBranchCleanupTest",
					"INSERT OVERWRITE INTO staging.dst SELECT score FROM src" },
			{ "jinjaKeywordArgDeadBranchCleanupTest",
					"SELECT * FROM {{ ref('my_model', v=2) }}" },
			{ "castTimestampWithoutTimeZoneStaticTypeDeadBranchCleanupTest",
					"SELECT CAST('x' AS TIMESTAMP WITHOUT TIME ZONE) AS t FROM tab1" },
	};

	public static void main(String[] args) throws Exception {
		WalkerDeadBranchCleanupGoldenCaptureOnce runner = new WalkerDeadBranchCleanupGoldenCaptureOnce();
		for (String[] c : CASES) {
			String name = c[0];
			String query = c[1];
			SQLSelectParserParser parser = runner.parse(query);
			SqlParseEventWalker extractor = runner.runParsertest(query, parser);
			emit(name, "AST", extractor.getAsTree().toString());
			emit(name, "Interface", extractor.getInterface().toString());
			emit(name, "Substitutions", extractor.getSubstitutionsMap().toString());
			emit(name, "TableDictionary", extractor.getTableColumnDictionaryMap().toString());
			emit(name, "QueryColumnDictionary", extractor.getQueryColumnDictionaryMap().toString());
			emit(name, "SymbolTable", extractor.getSymbolTable().toString());
		}
	}

	private static void emit(String method, String field, String value) {
		System.out.println("GOLDEN|" + method + "|" + field + "|" + value);
	}
}
