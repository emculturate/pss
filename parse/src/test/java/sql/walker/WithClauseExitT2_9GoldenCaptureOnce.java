package sql.walker;

import sql.SQLSelectParserParser;

/** Regenerate inline expectations for T2.9 {@code exitWith_clause} exemplars. */
public class WithClauseExitT2_9GoldenCaptureOnce extends AbstractSqlParseEventWalkerTest {

	private static final String[][] CASES = {
			{ "withClauseThreeChainedCtesT2_9Test",
					"WITH a AS (SELECT 1 AS x), b AS (SELECT x FROM a), c AS (SELECT x FROM b) SELECT x FROM c" },
			{ "withClauseSubstitutionTupleCteT2_9Test",
					"WITH staged AS <stg_src> SELECT 1 AS n FROM staged" },
			{ "withClauseNestedCteBodyT2_9Test",
					"WITH outer_cte AS (WITH inner_cte AS (SELECT 1 AS n) SELECT n FROM inner_cte) SELECT n FROM outer_cte" },
	};

	public static void main(String[] args) throws Exception {
		WithClauseExitT2_9GoldenCaptureOnce runner = new WithClauseExitT2_9GoldenCaptureOnce();
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
