package sql.walker;

import org.junit.Ignore;
import org.junit.Test;

import sql.SQLSelectParserParser;
import sql.walker.SetOpTimingProbeFixtures.BranchTableMode;

/**
 * Phase 2.8-S9 hypothesis probe — same convert-egress shape as
 * {@link SqlEventWalkerSetOpTimingProbePreSolutionTests}, but with an optional
 * {@link BranchTableMode#SHARED_SINGLE_TABLE} variant to isolate repeated global
 * {@code table_dictionary} merge cost.
 *
 * <p>Run:
 * {@code mvn -pl parse -Dtest=SqlEventWalkerSetOpTimingProbeSharedTableComparisonTests#setOpTimingProbeDistinctVsSharedTableComparisonTest test}
 */
public class SqlEventWalkerSetOpTimingProbeSharedTableComparisonTests extends AbstractSqlParseEventWalkerTest {

	private static final String UNION_ALL = "UNION ALL";
	private static final String INTERSECT = "INTERSECT";

	private long measureMillis(
			String label,
			String setOpKeyword,
			int joinerCount,
			int selectColumnCount,
			BranchTableMode tableMode) {
		int orderByColumnCount = SetOpTimingProbeFixtures.orderByCountForSelectCount(selectColumnCount);
		final String query = SetOpTimingProbeFixtures.buildQuery(
				setOpKeyword, joinerCount, selectColumnCount, orderByColumnCount, tableMode);
		final long startNanos = System.nanoTime();
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		final long elapsedMs = (System.nanoTime() - startNanos) / 1_000_000L;
		assertNoFatalErrors(extractor);
		System.out.println(
				"SHARED_TABLE_TIMING label=" + label
						+ " tableMode=" + tableMode
						+ " setOp=" + setOpKeyword.replace(' ', '_')
						+ " joiners=" + joinerCount
						+ " branches=" + (joinerCount + 1)
						+ " selectCols=" + selectColumnCount
						+ " orderByCols=" + orderByColumnCount
						+ " elapsedMs=" + elapsedMs);
		return elapsedMs;
	}

	private void compareDistinctVsShared(String label, String setOpKeyword, int joiners, int selectCols) {
		long distinctMs = measureMillis(label, setOpKeyword, joiners, selectCols, BranchTableMode.DISTINCT_PER_BRANCH);
		long sharedMs = measureMillis(label, setOpKeyword, joiners, selectCols, BranchTableMode.SHARED_SINGLE_TABLE);
		double ratio = distinctMs == 0L ? 0.0d : (sharedMs * 1.0d) / distinctMs;
		System.out.println(
				"SHARED_TABLE_RATIO label=" + label
						+ " setOp=" + setOpKeyword.replace(' ', '_')
						+ " joiners=" + joiners
						+ " selectCols=" + selectCols
						+ " distinctMs=" + distinctMs
						+ " sharedMs=" + sharedMs
						+ " sharedOverDistinct=" + String.format("%.2f", ratio));
	}

	@Test
	@Ignore("Manual S9 comparison — distinct vs shared FROM table per branch; enable for profiling")
	public void setOpTimingProbeDistinctVsSharedTableComparisonTest() {
		System.out.println("=== DISTINCT vs SHARED TABLE (M=20) ===");
		for (int joiners : new int[] {10, 25, 50}) {
			compareDistinctVsShared("M20_N" + joiners, UNION_ALL, joiners, 20);
			compareDistinctVsShared("M20_N" + joiners, INTERSECT, joiners, 20);
		}

		System.out.println("=== DISTINCT vs SHARED TABLE (N=50, sweep M) ===");
		for (int selectCols : new int[] {6, 10, 20}) {
			compareDistinctVsShared("N50_M" + selectCols, UNION_ALL, 50, selectCols);
			compareDistinctVsShared("N50_M" + selectCols, INTERSECT, 50, selectCols);
		}
	}
}
