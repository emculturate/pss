package sql.walker;

import org.junit.Ignore;
import org.junit.Test;

import sql.SQLSelectParserParser;
import sql.latency.WalkerHotspotProfiler;
import sql.walker.SetOpTimingProbeFixtures.BranchTableMode;

import java.util.Map;

/**
 * Phase 2.8-S9 hotspot profiler — compares method-call counts at N=10 vs N=50 (M=20 UNION ALL).
 *
 * <p>Run:
 * <pre>
 *   mvn -pl parse -Dpss.walker.hotspot.profile=true \
 *     -Dtest=SqlEventWalkerSetOpHotspotProfileTest#setOpHotspotProfileDistinctTablesN10vsN50 test
 * </pre>
 */
public class SqlEventWalkerSetOpHotspotProfileTest extends AbstractSqlParseEventWalkerTest {

	private static final String UNION_ALL = "UNION ALL";
	private static final int SELECT_COLS = 20;

	private Map<String, Long> runProbe(String label, int joiners, BranchTableMode tableMode) {
		if (!WalkerHotspotProfiler.ENABLED) {
			System.out.println("WALKER_HOTSPOT skipped — pass -Dpss.walker.hotspot.profile=true");
			return Map.of();
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
		return counts;
	}

	@Test
	@Ignore("Manual Phase 2.8 hotspot profile — enable with -Dpss.walker.hotspot.profile=true")
	public void setOpHotspotProfileDistinctTablesN10vsN50() {
		Map<String, Long> n10 = runProbe("distinct_N10_M20", 10, BranchTableMode.DISTINCT_PER_BRANCH);
		Map<String, Long> n50 = runProbe("distinct_N50_M20", 50, BranchTableMode.DISTINCT_PER_BRANCH);
		WalkerHotspotProfiler.reportScaling("N10", n10, "N50", n50);
	}

	@Test
	@Ignore("Manual Phase 2.8 hotspot profile — shared-table control")
	public void setOpHotspotProfileSharedTablesN10vsN50() {
		Map<String, Long> n10 = runProbe("shared_N10_M20", 10, BranchTableMode.SHARED_SINGLE_TABLE);
		Map<String, Long> n50 = runProbe("shared_N50_M20", 50, BranchTableMode.SHARED_SINGLE_TABLE);
		WalkerHotspotProfiler.reportScaling("N10", n10, "N50", n50);
	}
}
