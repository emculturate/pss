package sql.walker;

import sql.SQLSelectParserParser;

/** Regenerate inline expectations for {@link SqlEventWalkerGroupByGroupingSetsTests}. */
public class GroupByGroupingSetsGoldenCaptureOnce extends AbstractSqlParseEventWalkerTest {

	private static final String[][] CASES = {
			{ "groupByTwoColumnsCommaTable", "SELECT a, b, SUM(c) AS s FROM tab1 GROUP BY a, b" },
			{ "groupByParenthesizedPairTable", "SELECT a, b, SUM(c) AS s FROM tab1 GROUP BY (a, b)" },
			{ "groupByRollupTwoColumnsTable", "SELECT a, b, SUM(c) AS s FROM tab1 GROUP BY ROLLUP(a, b)" },
			{ "groupByRollupCompositeAndSingleTable",
					"SELECT a, b, c, SUM(d) AS s FROM tab1 GROUP BY ROLLUP((a, b), c)" },
			{ "groupByCubeSingleColumnTable", "SELECT a, SUM(b) AS s FROM tab1 GROUP BY CUBE(a)" },
			{ "groupByCubeTwoColumnsTable", "SELECT a, b, SUM(c) AS s FROM tab1 GROUP BY CUBE(a, b)" },
			{ "groupByGroupingSetsTwoSetsTable",
					"SELECT a, SUM(b) AS s FROM tab1 GROUP BY GROUPING SETS ((a), (a, b))" },
			{ "groupByGroupingSetsThreeOperandsTable",
					"SELECT a, b, c, SUM(d) AS s FROM tab1 GROUP BY GROUPING SETS ((a), (b), (c))" },
			{ "groupByRollupThreeOperandsTable",
					"SELECT a, b, c, SUM(d) AS s FROM tab1 GROUP BY ROLLUP((a, b), c, d)" },
			{ "groupByMixedCommaAndParenthesizedSetTable",
					"SELECT a, b, c, SUM(d) AS s FROM tab1 GROUP BY a, (b, c)" },
			{ "groupByRollupFourOperandsTable",
					"SELECT a, b, c, d, SUM(e) AS s FROM tab1 GROUP BY ROLLUP(a, b, c, d)" },
			{ "groupByCubeParenthesizedPairTable", "SELECT a, b, SUM(c) AS s FROM tab1 GROUP BY CUBE((a, b))" },
			{ "groupByGroupingSetsFourSetsTable",
					"SELECT a, b, SUM(c) AS s FROM tab1 GROUP BY GROUPING SETS ((a), (b), (a, b), (b, a))" },
			{ "groupByTwoColumnsSubquery",
					"SELECT x, y, SUM(z) AS s FROM (SELECT a AS x, b AS y, c AS z FROM tab1) q GROUP BY x, y" },
			{ "groupByParenthesizedPairSubquery",
					"SELECT x, y, SUM(z) AS s FROM (SELECT a AS x, b AS y, c AS z FROM tab1) q GROUP BY (x, y)" },
			{ "groupByRollupTwoColumnsSubquery",
					"SELECT x, y, SUM(z) AS s FROM (SELECT a AS x, b AS y, c AS z FROM tab1) q GROUP BY ROLLUP(x, y)" },
			{ "groupByRollupCompositeSubquery",
					"SELECT x, y, w, SUM(z) AS s FROM (SELECT a AS x, b AS y, c AS w, d AS z FROM tab1) q GROUP BY ROLLUP((x, y), w)" },
			{ "groupByCubeSubquery",
					"SELECT x, SUM(y) AS s FROM (SELECT a AS x, b AS y FROM tab1) q GROUP BY CUBE(x)" },
			{ "groupByGroupingSetsSubquery",
					"SELECT x, y, SUM(z) AS s FROM (SELECT a AS x, b AS y, c AS z FROM tab1) q GROUP BY GROUPING SETS ((x, y), (x))" },
			{ "groupByRollupThreeOperandsSubquery",
					"SELECT p, q, r, SUM(s) AS tot FROM (SELECT a AS p, b AS q, c AS r, d AS s FROM tab1) v GROUP BY ROLLUP((p, q), r, s)" },
			{ "groupByMixedCommaParenSubquery",
					"SELECT p, q, r, SUM(s) AS tot FROM (SELECT a AS p, b AS q, c AS r, d AS s FROM tab1) v GROUP BY p, (q, r)" },
			{ "groupByGroupingSetsFourSetsSubquery",
					"SELECT x, y, SUM(z) AS s FROM (SELECT a AS x, b AS y, c AS z FROM tab1) q GROUP BY GROUPING SETS ((x), (y), (x, y), (y, x))" },
			{ "groupByAllTable", "SELECT a, b, SUM(c) AS s FROM tab1 GROUP BY ALL" },
			{ "groupByDistinctTwoColumnsTable", "SELECT a, b, SUM(c) AS s FROM tab1 GROUP BY DISTINCT a, b" },
			{ "groupByDistinctTwoColumnsSubquery",
					"SELECT x, y, SUM(z) AS s FROM (SELECT a AS x, b AS y, c AS z FROM tab1) q GROUP BY DISTINCT x, y" },
	};

	public static void main(String[] args) throws Exception {
		GroupByGroupingSetsGoldenCaptureOnce runner = new GroupByGroupingSetsGoldenCaptureOnce();
		for (String[] c : CASES) {
			String name = c[0];
			String query = c[1];
			try {
				SQLSelectParserParser parser = runner.parse(query);
				SqlParseEventWalker extractor = runner.runParsertest(query, parser);
				emit(name, "QUERY", query);
				emit(name, "AST", extractor.getAsTree().toString());
				emit(name, "Interface", extractor.getInterface().toString());
				emit(name, "Substitutions", extractor.getSubstitutionsMap().toString());
				emit(name, "TableDictionary", extractor.getTableColumnDictionaryMap().toString());
				emit(name, "QueryColumnDictionary", extractor.getQueryColumnDictionaryMap().toString());
				emit(name, "SymbolTable", extractor.getSymbolTable().toString());
			} catch (Throwable ex) {
				System.err.println("SKIP|" + name + "|" + ex.getMessage());
			}
		}
	}

	private static void emit(String method, String field, String value) {
		System.out.println("GOLDEN|" + method + "|" + field + "|" + value);
	}
}
