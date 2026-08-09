package sql.walker;

import sql.SQLSelectParserParser;

/** Regenerate inline expectations for T2.2 {@code insert_target_table_primary} exemplars. */
public class InsertTargetTablePrimaryGoldenCaptureOnce extends AbstractSqlParseEventWalkerTest {

	private static final String[][] CASES = {
			{ "insertTargetSubstitutionVariableT2_2Test",
					"INSERT INTO <staging_dest> (score, rank_bucket) VALUES (1, 2)" },
			{ "insertTargetJinjaRefT2_2Test",
					"INSERT INTO {{ ref('employees') }} (score) VALUES (100)" },
			{ "insertTargetRelationAliasT2_2Test",
					"INSERT INTO employees AS tgt SELECT score, emp_id FROM perf_feed" },
			{ "insertTargetNoColumnListT2_2Test",
					"INSERT INTO hr.employees SELECT score, emp_id FROM perf_feed" },
	};

	public static void main(String[] args) throws Exception {
		InsertTargetTablePrimaryGoldenCaptureOnce runner = new InsertTargetTablePrimaryGoldenCaptureOnce();
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
