package sql.walker;

import org.junit.Ignore;
import org.junit.Test;

import sql.SQLSelectParserParser;
import sql.latency.WalkerHotspotProfiler;
import sql.walker.SetOpTimingProbeFixtures.BranchTableMode;

import java.util.Map;

/**
 * Phase 2.8-S9 / C1 hotspot profiler — compares method-call counts at N=10 vs N=50 (M=20 UNION ALL)
 * for <b>both</b> {@link BranchTableMode#DISTINCT_PER_BRANCH} and
 * {@link BranchTableMode#SHARED_SINGLE_TABLE}.
 *
 * <p>Phase 2.8-C1 adds {@code walkerExit_*} (listener exits), {@code columnCapture_*},
 * {@code columnArchive_*}, and {@code columnResolution_<round>[_qualified|_unqualified]} counters.
 *
 * <p>Run combined distinct + shared comparison:
 * <pre>
 *   mvn -pl parse -Dpss.walker.hotspot.profile=true \
 *     -Dtest=SqlEventWalkerSetOpHotspotProfileTest#setOpHotspotProfileDistinctAndSharedN10vsN50 test
 * </pre>
 */
public class SqlEventWalkerSetOpHotspotProfileTest extends AbstractSqlParseEventWalkerTest {

	private static final String UNION_ALL = "UNION ALL";
	private static final int SELECT_COLS = 20;

	private static final class ProbeRun {
		final long elapsedMs;
		final Map<String, Long> counts;

		ProbeRun(long elapsedMs, Map<String, Long> counts) {
			this.elapsedMs = elapsedMs;
			this.counts = counts;
		}
	}

	private ProbeRun runProbe(String label, int joiners, BranchTableMode tableMode) {
		if (!WalkerHotspotProfiler.ENABLED) {
			System.out.println("WALKER_HOTSPOT skipped — pass -Dpss.walker.hotspot.profile=true");
			return new ProbeRun(0L, Map.of());
		}
		WalkerHotspotProfiler.reset();
		int orderByCols = SetOpTimingProbeFixtures.orderByCountForSelectCount(SELECT_COLS);
		String query = SetOpTimingProbeFixtures.buildQuery(
				UNION_ALL, joiners, SELECT_COLS, orderByCols, tableMode);
		long startNanos = System.nanoTime();
		SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		long elapsedMs = (System.nanoTime() - startNanos) / 1_000_000L;
		assertNoFatalErrors(extractor);
		Map<String, Long> counts = WalkerHotspotProfiler.snapshot();
		System.out.println(
				"WALKER_HOTSPOT_RUN label=" + label
						+ " tableMode=" + tableMode
						+ " joiners=" + joiners
						+ " branches=" + (joiners + 1)
						+ " elapsedMs=" + elapsedMs);
		WalkerHotspotProfiler.report(label);
		return new ProbeRun(elapsedMs, counts);
	}

	/**
	 * Primary C1 driver: N-scaling + shared-vs-distinct at N=10 and N=50 in one run so performance
	 * work cannot optimize for only one table mode.
	 */
	@Test
	@Ignore("Manual Phase 2.8 hotspot profile — enable with -Dpss.walker.hotspot.profile=true")
	public void setOpHotspotProfileDistinctAndSharedN10vsN50() {
		ProbeRun distinctN10 = runProbe("distinct_N10_M20", 10, BranchTableMode.DISTINCT_PER_BRANCH);
		ProbeRun distinctN50 = runProbe("distinct_N50_M20", 50, BranchTableMode.DISTINCT_PER_BRANCH);
		ProbeRun sharedN10 = runProbe("shared_N10_M20", 10, BranchTableMode.SHARED_SINGLE_TABLE);
		ProbeRun sharedN50 = runProbe("shared_N50_M20", 50, BranchTableMode.SHARED_SINGLE_TABLE);

		WalkerHotspotProfiler.reportScaling("distinct N10", distinctN10.counts, "distinct N50", distinctN50.counts);
		WalkerHotspotProfiler.reportScaling("shared N10", sharedN10.counts, "shared N50", sharedN50.counts);

		WalkerHotspotProfiler.reportWalkerExitTimingScaling(
				"distinct N10", distinctN10.counts, "distinct N50", distinctN50.counts, 15);
		WalkerHotspotProfiler.reportWalkerExitTimingScaling(
				"shared N10", sharedN10.counts, "shared N50", sharedN50.counts, 15);

		WalkerHotspotProfiler.reportHotspotTimingScaling(
				"distinct N10", distinctN10.counts, "distinct N50", distinctN50.counts, 15);
		WalkerHotspotProfiler.reportHotspotTimingScaling(
				"shared N10", sharedN10.counts, "shared N50", sharedN50.counts, 15);

		WalkerHotspotProfiler.reportTableModeComparison(
				"N10", distinctN10.elapsedMs, distinctN10.counts, sharedN10.elapsedMs, sharedN10.counts);
		WalkerHotspotProfiler.reportTableModeComparison(
				"N50", distinctN50.elapsedMs, distinctN50.counts, sharedN50.elapsedMs, sharedN50.counts);

		WalkerHotspotProfiler.reportDisproportionateTableModeScaling(
				distinctN10.counts,
				distinctN50.counts,
				sharedN10.counts,
				sharedN50.counts,
				0.25d);
	}

	@Test
	@Ignore("Manual Phase 2.8 hotspot profile — distinct tables only")
	public void setOpHotspotProfileDistinctTablesN10vsN50() {
		ProbeRun n10 = runProbe("distinct_N10_M20", 10, BranchTableMode.DISTINCT_PER_BRANCH);
		ProbeRun n50 = runProbe("distinct_N50_M20", 50, BranchTableMode.DISTINCT_PER_BRANCH);
		WalkerHotspotProfiler.reportScaling("N10", n10.counts, "N50", n50.counts);
	}

	@Test
	@Ignore("Manual Phase 2.8 hotspot profile — shared table only")
	public void setOpHotspotProfileSharedTablesN10vsN50() {
		ProbeRun n10 = runProbe("shared_N10_M20", 10, BranchTableMode.SHARED_SINGLE_TABLE);
		ProbeRun n50 = runProbe("shared_N50_M20", 50, BranchTableMode.SHARED_SINGLE_TABLE);
		WalkerHotspotProfiler.reportScaling("N10", n10.counts, "N50", n50.counts);
	}
}
