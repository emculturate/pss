package sql.walker;

import org.junit.Test;

import sql.SQLSelectParserParser;
import sql.symboltree.ArchivedClauseDualProbeDiffRecorder;

/**
 * Phase C0: measure what the scope-tree archived-clause probe (probe 2) mutates relative to
 * the post-convert probe (probe 1). Informational only — prints a report for dual-probe retirement planning.
 *
 * Run:
 * {@code mvn -Dtest=sql.walker.ArchivedClauseDualProbeDiffTest test}
 *
 * For the full consolidation gate (C1), see {@link ArchivedClauseDualProbeDiffGateTest}.
 */
public class ArchivedClauseDualProbeDiffTest extends AbstractSqlParseEventWalkerTest {

	@Test
	public void reportDualProbeDiffOnConsolidationCanaries() {
		String previousProperty = System.getProperty(ArchivedClauseDualProbeDiffRecorder.SYSPROP);
		System.setProperty(ArchivedClauseDualProbeDiffRecorder.SYSPROP, "true");
		try {
			StringBuilder report = new StringBuilder();
			report.append("=== Phase C0 archived clause dual-probe diff ===\n\n");
			report.append(runCase("nestedQueryDemo", nestedQueryDemoSql()));
			report.append(runCase("nestedQueryDemoWithCte", nestedQueryDemoWithCteSql()));
			report.append(runCase("unaliasedDerivedUnionAllOuterClausesV7", unionAllOuterClausesV7Sql()));
			report.append(runCase("correlatedExistsSubqueryFinalQueryReferencesCteChain",
					correlatedExistsFinalCteChainSql()));
			System.out.println(report);
		} finally {
			if (previousProperty == null) {
				System.clearProperty(ArchivedClauseDualProbeDiffRecorder.SYSPROP);
			} else {
				System.setProperty(ArchivedClauseDualProbeDiffRecorder.SYSPROP, previousProperty);
			}
		}
	}

	private String runCase(String caseName, String query) {
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		ArchivedClauseDualProbeDiffRecorder recorder =
				extractor.getSymbolTreeHelper().getArchivedClauseDualProbeDiffRecorder();
		return "--- " + caseName + " ---\n" + recorder.formatReport() + "\n";
	}

	private static String nestedQueryDemoSql() {
		return "select "
				+ "\n (select max((select avg(t) at from tt where tt.b > tab1.t and tt.e = ee.x2)) mxd "
				+ "\n from ee where ee.x = (select tab1.<y_col>)) max_D,"
				+ "\n tab1.a aa,"
				+ "\n (select min(D) mnd from ee where ee.x = tab1.x  and tt.f = ee.x2) min_D,  kk.w"
				+ "\n from (select w from jj where jj.y = tab1.<z_col> and jj.m > tab2.e3) kk join tab1"
				+ "\n where c in (select c, gg.y gg_y from ff where ff.z = tab1.<w_col>)";
	}

	private static String nestedQueryDemoWithCteSql() {
		return "WITH gg AS (SELECT y FROM gg_src)"
				+ "\n select "
				+ "\n (select max((select avg(t) at from tt where tt.b > tab1.t and tt.e = ee.x2)) mxd "
				+ "\n from ee where ee.x = (select tab1.<y_col>)) max_D,"
				+ "\n tab1.a aa,"
				+ "\n (select min(D) mnd from ee where ee.x = tab1.x  and tt.f = ee.x2) min_D,  kk.w"
				+ "\n from (select w from jj where jj.y = tab1.<z_col> and jj.m > tab2.e3) kk join tab1"
				+ "\n where c in (select c, gg.y gg_y from ff where ff.z = tab1.<w_col>)";
	}

	private static String unionAllOuterClausesV7Sql() {
		return "select a, b, c from ("
				+ " select x a, y b, z c from t1 where p = 1"
				+ " union all"
				+ " select x, y, z from t2 where q = 2"
				+ ") u"
				+ " where a > 0 group by a, b having b < 10 order by c";
	}

	private static String correlatedExistsFinalCteChainSql() {
		return "WITH cec AS (SELECT pa FROM tab1),"
				+ " ceb AS (SELECT pa FROM tab2 WHERE EXISTS (SELECT 1 FROM cec WHERE cec.pa = tab2.pa)),"
				+ " cea AS (SELECT pa FROM tab3 WHERE EXISTS (SELECT 1 FROM ceb WHERE ceb.pa = tab3.pa))"
				+ " SELECT pa FROM cea WHERE EXISTS (SELECT 1 FROM ceb WHERE ceb.pa = cea.pa)";
	}
}
