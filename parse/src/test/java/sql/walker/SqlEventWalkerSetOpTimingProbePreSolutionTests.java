package sql.walker;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import sql.SQLSelectParserParser;

/**
 * Pre-solution set-op timing calibration (Phase 2.8). Run explicitly — not part of CI smoke gate.
 *
 * <p>{@code mvn -pl parse -Dtest=SqlEventWalkerSetOpTimingProbePreSolutionTests#setOpTimingProbePreSolutionCalibrationMatrixTest test}
 */
public class SqlEventWalkerSetOpTimingProbePreSolutionTests extends AbstractSqlParseEventWalkerTest {

	private static final String UNION_ALL = "UNION ALL";
	private static final String INTERSECT = "INTERSECT";
	private static final long ONE_MINUTE_MS = 60_000L;

	private long measureSetOpTimingProbeMillis(String setOpKeyword, int joinerCount, int selectColumnCount) {
		int orderByColumnCount = SetOpTimingProbeFixtures.orderByCountForSelectCount(selectColumnCount);
		final String query = SetOpTimingProbeFixtures.buildQuery(setOpKeyword, joinerCount, selectColumnCount, orderByColumnCount);
		final long startNanos = System.nanoTime();
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		final long elapsedMs = (System.nanoTime() - startNanos) / 1_000_000L;
		assertNoFatalErrors(extractor);
		System.out.println(
				"PRE_SOLUTION_TIMING setOp=" + setOpKeyword.replace(' ', '_')
						+ " joiners=" + joinerCount
						+ " branches=" + (joinerCount + 1)
						+ " selectCols=" + selectColumnCount
						+ " orderByCols=" + orderByColumnCount
						+ " elapsedMs=" + elapsedMs);
		return elapsedMs;
	}

	@Test
	@Ignore("Manual E1 calibration — full N/M grid; run with -Dtest=…#setOpTimingProbeE1CalibrationMatrixTest")
	public void setOpTimingProbeE1CalibrationMatrixTest() {
		System.out.println("=== E1 MATRIX: fixed N=50, sweep M ===");
		for (int selectColumns : new int[] {6, 10, 20, 30, 40, 50}) {
			measureSetOpTimingProbeMillis(UNION_ALL, 50, selectColumns);
			measureSetOpTimingProbeMillis(INTERSECT, 50, selectColumns);
		}

		System.out.println("=== E1 MATRIX: fixed M=20, sweep N ===");
		for (int joiners : new int[] {10, 25, 50, 75, 100, 114, 150}) {
			measureSetOpTimingProbeMillis(UNION_ALL, joiners, 20);
			measureSetOpTimingProbeMillis(INTERSECT, joiners, 20);
		}
	}

	@Test
	@Ignore("Manual calibration — prints pre-solution matrix; run with -Dtest=…#setOpTimingProbePreSolutionCalibrationMatrixTest")
	public void setOpTimingProbePreSolutionCalibrationMatrixTest() {
		System.out.println("=== PRE-SOLUTION MATRIX: fixed N=50, sweep M ===");
		for (int selectColumns : new int[] {6, 10, 20}) {
			measureSetOpTimingProbeMillis(UNION_ALL, 50, selectColumns);
			measureSetOpTimingProbeMillis(INTERSECT, 50, selectColumns);
		}

		System.out.println("=== PRE-SOLUTION MATRIX: fixed M=20, sweep N ===");
		for (int joiners : new int[] {10, 25, 50}) {
			measureSetOpTimingProbeMillis(UNION_ALL, joiners, 20);
			measureSetOpTimingProbeMillis(INTERSECT, joiners, 20);
		}

		System.out.println("=== PRE-SOLUTION BOUNDARY: fixed M=20, sweep N until ~60s ===");
		sweepJoinersUntilOneMinute(UNION_ALL, 20);
		sweepJoinersUntilOneMinute(INTERSECT, 20);

		System.out.println("=== PRE-SOLUTION BOUNDARY: fixed N=50, sweep M until ~60s or M=100 ===");
		sweepSelectColumnsUntilOneMinute(UNION_ALL, 50);
		sweepSelectColumnsUntilOneMinute(INTERSECT, 50);
	}

	private void sweepJoinersUntilOneMinute(String setOpKeyword, int selectColumnCount) {
		int joiners = 50;
		long elapsedMs = 0L;
		while (joiners <= 500) {
			elapsedMs = measureSetOpTimingProbeMillis(setOpKeyword, joiners, selectColumnCount);
			if (elapsedMs >= ONE_MINUTE_MS) {
				System.out.println(
						"PRE_SOLUTION_ONE_MINUTE_N setOp=" + setOpKeyword.replace(' ', '_')
								+ " joiners=" + joiners
								+ " selectCols=" + selectColumnCount
								+ " elapsedMs=" + elapsedMs);
				return;
			}
			if (joiners < 100) {
				joiners += 25;
			} else {
				joiners += 50;
			}
		}
		System.out.println(
				"PRE_SOLUTION_ONE_MINUTE_N setOp=" + setOpKeyword.replace(' ', '_')
						+ " joiners=NOT_REACHED_MAX_500"
						+ " selectCols=" + selectColumnCount
						+ " lastElapsedMs=" + elapsedMs);
	}

	private void sweepSelectColumnsUntilOneMinute(String setOpKeyword, int joiners) {
		long elapsedMs = 0L;
		for (int selectColumns = 20; selectColumns <= 100; selectColumns += 10) {
			elapsedMs = measureSetOpTimingProbeMillis(setOpKeyword, joiners, selectColumns);
			if (elapsedMs >= ONE_MINUTE_MS) {
				System.out.println(
						"PRE_SOLUTION_ONE_MINUTE_M setOp=" + setOpKeyword.replace(' ', '_')
								+ " joiners=" + joiners
								+ " selectCols=" + selectColumns
								+ " elapsedMs=" + elapsedMs);
				return;
			}
		}
		System.out.println(
				"PRE_SOLUTION_ONE_MINUTE_M setOp=" + setOpKeyword.replace(' ', '_')
						+ " joiners=" + joiners
						+ " selectCols=NOT_REACHED_MAX_100"
						+ " lastElapsedMs=" + elapsedMs);
	}

	// ── One-minute extreme regression probes (pre-solution baselines; @Ignore until S6 re-baseline) ──

	@Test
	@Ignore("N=114 ~60s refinement spot-check — run manually after matrix calibration")
	public void setOpTimingProbePreSolutionN114RefinementTest() {
		measureSetOpTimingProbeMillis(UNION_ALL, 114, 20);
		measureSetOpTimingProbeMillis(INTERSECT, 114, 20);
	}

	@Test
	@Ignore("Slow pre-solution one-minute UNION N-bound probe — enable for manual regression")
	public void setOpTimingProbePreSolutionUnionOneMinuteNBoundTest() {
		runOneMinuteProbe(UNION_ALL, PRE_SOLUTION_UNION_ONE_MINUTE_JOINERS, PRE_SOLUTION_ONE_MINUTE_SELECT_COLUMNS);
	}

	@Test
	@Ignore("Slow pre-solution one-minute INTERSECT N-bound probe — enable for manual regression")
	public void setOpTimingProbePreSolutionIntersectOneMinuteNBoundTest() {
		runOneMinuteProbe(INTERSECT, PRE_SOLUTION_INTERSECT_ONE_MINUTE_JOINERS, PRE_SOLUTION_ONE_MINUTE_SELECT_COLUMNS);
	}

	@Test
	@Ignore("Slow pre-solution one-minute UNION M-bound probe — enable for manual regression")
	public void setOpTimingProbePreSolutionUnionOneMinuteMBoundTest() {
		runOneMinuteProbe(UNION_ALL, PRE_SOLUTION_ONE_MINUTE_M_BOUND_JOINERS, PRE_SOLUTION_UNION_ONE_MINUTE_SELECT_COLUMNS);
	}

	@Test
	@Ignore("Slow pre-solution one-minute INTERSECT M-bound probe — enable for manual regression")
	public void setOpTimingProbePreSolutionIntersectOneMinuteMBoundTest() {
		runOneMinuteProbe(INTERSECT, PRE_SOLUTION_ONE_MINUTE_M_BOUND_JOINERS, PRE_SOLUTION_INTERSECT_ONE_MINUTE_SELECT_COLUMNS);
	}

	private void runOneMinuteProbe(String setOpKeyword, int joinerCount, int selectColumnCount) {
		final long elapsedMs = measureSetOpTimingProbeMillis(setOpKeyword, joinerCount, selectColumnCount);
		System.out.println("oneMinuteProbe elapsedMs=" + elapsedMs);
		Assert.assertTrue("expected at least ~45s pre-solution probe", elapsedMs >= 45_000L);
	}

	/** Filled from calibration run — see pre-solution comment block in {@code SqlEventWalkerSubqueriesAndClauseSemanticsTests}. */
	private static final int PRE_SOLUTION_ONE_MINUTE_SELECT_COLUMNS = 20;
	private static final int PRE_SOLUTION_UNION_ONE_MINUTE_JOINERS = 114;
	private static final int PRE_SOLUTION_INTERSECT_ONE_MINUTE_JOINERS = 114;
	private static final int PRE_SOLUTION_ONE_MINUTE_M_BOUND_JOINERS = 50;
	private static final int PRE_SOLUTION_UNION_ONE_MINUTE_SELECT_COLUMNS = 50;
	private static final int PRE_SOLUTION_INTERSECT_ONE_MINUTE_SELECT_COLUMNS = 50;
}
