package sql.walker;

import sql.SQLSelectParserParser;

/** Regenerate inline expectations for T2.1 {@code table_argument_literal} exemplars. */
public class TableFunctionArgumentLiteralGoldenCaptureOnce extends AbstractSqlParseEventWalkerTest {

	private static final String[][] CASES = {
			{ "inferSchemaLiteralsAndNumericT2_1Test",
					"SELECT * FROM TABLE(INFER_SCHEMA("
							+ "LOCATION => '@db.schema.stage/path/', "
							+ "FILE_FORMAT => 'JSON', "
							+ "MAX_FILE_COUNT => 10, "
							+ "KIND => 'JSON'))" },
			{ "flattenModeArrayLiteralT2_1Test",
					"SELECT * FROM TABLE(FLATTEN("
							+ "input => PARSE_JSON('[1,2]'), "
							+ "mode => 'ARRAY')) f" },
			{ "flattenPathStringLiteralT2_1Test",
					"SELECT * FROM TABLE(FLATTEN("
							+ "input => PARSE_JSON('{\"a\":1}'), "
							+ "path => 'a')) f" },
	};

	public static void main(String[] args) throws Exception {
		TableFunctionArgumentLiteralGoldenCaptureOnce runner = new TableFunctionArgumentLiteralGoldenCaptureOnce();
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
