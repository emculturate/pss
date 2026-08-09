package sql.walker;

import sql.SQLSelectParserParser;

/** Capture goldens for Tier 1 exit* coverage (T1.5–T1.10). */
public class Tier1ExitGoldenCaptureOnce extends AbstractSqlParseEventWalkerTest {

	public static void main(String[] args) throws Exception {
		String[][] cases = {
				{ "inferSchemaIgnoreCaseFalseTableArgumentBooleanT1_5Test",
						"SELECT * FROM TABLE(INFER_SCHEMA(LOCATION => 's', IGNORE_CASE => FALSE))" },
				{ "inferSchemaFilesArgumentT1_6Test",
						"SELECT * FROM TABLE(INFER_SCHEMA("
								+ "LOCATION => '@db.schema.stage/path/', "
								+ "FILES => ('part-0001.json', 'part-0002.json')))" },
				{ "validateTableFunctionT1_7Test",
						"SELECT * FROM TABLE(VALIDATE(my_db.my_schema.my_table, JOB_ID => '_last'))" },
				{ "groupByRowValuePredicandListT1_8Test",
						"SELECT a, b, SUM(c) FROM tab1 GROUP BY (a, b)" },
				{ "rollupOrdinaryGroupingSetListT1_10Test",
						"SELECT a, b, c, SUM(d) FROM tab1 GROUP BY ROLLUP((a, b), c)" },
		};
		Tier1ExitGoldenCaptureOnce runner = new Tier1ExitGoldenCaptureOnce();
		for (String[] c : cases) {
			String name = c[0];
			String query = c[1];
			SQLSelectParserParser parser = runner.parse(query);
			SqlParseEventWalker extractor = runner.runParsertest(query, parser);
			emit(name, "AST", extractor.getAsTree().toString());
			emit(name, "Interface", extractor.getInterface().toString());
			emit(name, "Substitution", extractor.getSubstitutionsMap().toString());
			emit(name, "TableDictionary", extractor.getTableColumnDictionaryMap().toString());
			emit(name, "QueryDictionary", extractor.getQueryColumnDictionaryMap().toString());
			emit(name, "SymbolTable", extractor.getSymbolTable().toString());
		}
	}

	private static void emit(String method, String field, String value) {
		System.out.println("GOLDEN|" + method + "|" + field + "|" + value);
	}
}
