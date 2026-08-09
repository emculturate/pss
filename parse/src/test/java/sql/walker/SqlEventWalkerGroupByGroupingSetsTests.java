package sql.walker;

import java.io.InputStream;
import java.util.Properties;

import org.junit.Assert;
import org.junit.Test;

import sql.SQLSelectParserParser;

/**
 * GROUP BY / ROLLUP / CUBE / GROUPING SETS AST shapes ({@code set}, {@code rollup}, {@code cube},
 * {@code grouping_sets}) and {@code grouped_by} clause harvesting.
 */
public class SqlEventWalkerGroupByGroupingSetsTests extends AbstractSqlParseEventWalkerTest {

	static String[][] allCases() {
		return CASES;
	}

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
	};

	@Test
	public void allGroupByGroupingSetAstGoldens() {
		for (String[] testCase : CASES) {
			assertGroupByCase(testCase[0], testCase[1]);
		}
	}

	private void assertGroupByCase(String method, String query) {
		SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
		String ast = extractor.getAsTree().toString();
		Assert.assertFalse("AST should not leak rule Type keys: " + method, ast.contains("Type="));
		Assert.assertTrue("AST should contain groupby: " + method, ast.contains("groupby="));
		String sym = extractor.getSymbolTable().toString();
		Assert.assertTrue("Symbol table should list grouped_by: " + method, sym.contains("grouped_by=["));
		if (!method.contains("EmptySet")) {
			Assert.assertFalse("grouped_by should not be empty for: " + method, sym.contains("grouped_by=[]"));
		}
		Assert.assertEquals("AST golden mismatch: " + method, GOLDENS.getProperty(method), ast);
	}

	private static final Properties GOLDENS = loadGoldens();

	private static Properties loadGoldens() {
		Properties properties = new Properties();
		try (InputStream in = SqlEventWalkerGroupByGroupingSetsTests.class
				.getResourceAsStream("/sql/walker/groupby_grouping_ast_goldens.properties")) {
			if (in == null) {
				throw new IllegalStateException("Missing groupby_grouping_ast_goldens.properties");
			}
			properties.load(in);
		} catch (Exception ex) {
			throw new IllegalStateException("Failed to load GROUP BY goldens", ex);
		}
		return properties;
	}
}
