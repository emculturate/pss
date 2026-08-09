package sql.walker;

import sql.SQLSelectParserParser;

/** Regenerate inline expectations for T2.6 {@code exitSubquery} exemplars. */
public class SubqueryExitT2_6GoldenCaptureOnce extends AbstractSqlParseEventWalkerTest {

	private static final String[][] CASES = {
			{ "subqueryScalarInSelectListT2_6Test",
					"SELECT (SELECT max(x) FROM t2) AS m FROM t1" },
			{ "subqueryScalarInWhereT2_6Test",
					"SELECT a FROM t1 WHERE a > (SELECT max(b) FROM t2)" },
			{ "subqueryExistsPredicateT2_6Test",
					"SELECT a FROM t1 WHERE EXISTS (SELECT 1 FROM t2 WHERE t2.id = t1.id)" },
			{ "subqueryInPredicateT2_6Test",
					"SELECT a FROM t1 WHERE a IN (SELECT b FROM t2)" },
			{ "subqueryFromDerivedTableT2_6Test",
					"SELECT s.x FROM (SELECT a AS x FROM t2) s" },
			{ "subqueryInsertSelectSourceT2_6Test",
					"INSERT INTO dst SELECT * FROM (SELECT a, b FROM src) q" },
			{ "subquerySetOpMemberT2_6Test",
					"(SELECT a FROM t1) UNION (SELECT a FROM t2)" },
			{ "subqueryQuantifiedCompareT2_6Test",
					"SELECT a FROM t1 WHERE a > ALL (SELECT b FROM t2)" },
	};

	public static void main(String[] args) throws Exception {
		SubqueryExitT2_6GoldenCaptureOnce runner = new SubqueryExitT2_6GoldenCaptureOnce();
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
