package sql.latency;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.StringJoiner;

import org.junit.Ignore;
import org.junit.Test;

import static mumble.SQLParserEndPoints.SQLPARSER_SQL_TREE_KEY;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Phase 2.8-E3 corpus gate — all {@code timeout_513} rows (74) plus named bucket canaries
 * must finish under the 90s RMCP kill.
 *
 * <p><b>What this gate measures:</b> wall-clock timing on the opt-in
 * {@link ParseLatencyDiagnosticService#diagnose(String, String)} path (LL parse + walk +
 * finalize). It does <em>not</em> assert zero walker FATALs and must not be used for
 * accept/reject decisions.
 *
 * <p><b>Not production parsing:</b> {@code SqlParserAccess} is the correctness path.
 * {@code ParseLatencyDiagnosticService} is available for future timing investigations only.
 * Use {@link ParseLatencyDiagnosticService#diagnoseWithSllProbe(String, String)} when
 * comparing SLL vs LL prediction cost — never to gate production behavior.
 *
 * <p>SQL fixtures: {@code parse/docs/rmcp-handoff/5.1.3-panto-outstanding/sql/csv-row-&lt;n&gt;.sql}
 *
 * <p><b>Manual only:</b> excluded from default {@code mvn test} / package builds (~80s).
 * Run: {@code mvn -pl parse -Dtest=PantoTimeoutCorpusE3GateTest test}
 */
@Ignore("Manual — 74-row E3 timing gate; mvn -pl parse -Dtest=PantoTimeoutCorpusE3GateTest test")
public class PantoTimeoutCorpusE3GateTest {

    static final long E3_TIMEOUT_MS = PantoLatencyGateConstants.E3_TIMEOUT_MS;

    /** 2.8-1 — giant constant-row {@code UNION} lookup (~248 branches). */
    static final int[] E3_BUCKET_2_8_1 = {475, 476, 1827, 1828};

    /** 2.8-2 canary — PCM convert {@code UNION ALL} slices. */
    static final int E3_BUCKET_2_8_2_CANARY = 1837;

    /** Non-UNION guardrails (row 130 formerly on subMap skip list). */
    static final int[] E3_NON_UNION_CANARIES = {5261, 4647, 130};

    @Test
    public void e3RunnableCorpus_allRowsUnder90s() throws IOException {
        List<Integer> csvRows = PantoOutstandingSqlFixtures.timeout513CorpusRows();
        long maxWalkMs = 0;
        long maxTotalMs = 0;
        int maxWalkRow = -1;
        int maxTotalRow = -1;
        int withFatals = 0;
        List<String> failures = new ArrayList<>();

        for (int csvRow : csvRows) {
            RowGateResult result = gateRow(csvRow);
            if (result.walkerFatalCount() > 0 && !PantoCorpusExclusionList.isExcluded(csvRow)) {
                withFatals++;
            }
            if (result.walkMs() > maxWalkMs) {
                maxWalkMs = result.walkMs();
                maxWalkRow = csvRow;
            }
            if (result.totalMs() > maxTotalMs) {
                maxTotalMs = result.totalMs();
                maxTotalRow = csvRow;
            }
            if (!result.passed()) {
                failures.add(result.failureMessage());
            }
        }

        System.out.printf(Locale.ROOT,
                "E3_CORPUS_SUMMARY rows=%d skipList=%d exclusions=%d maxWalkMs=%d (csv_row=%d) maxTotalMs=%d (csv_row=%d) boundQueryRowsWithFatals=%d%n",
                csvRows.size(),
                PantoCorpusSkipList.subMapWalkerCsvRows().size(),
                PantoCorpusExclusionList.excludedCsvRows().size(),
                maxWalkMs,
                maxWalkRow,
                maxTotalMs,
                maxTotalRow,
                withFatals);

        if (!failures.isEmpty()) {
            StringJoiner joiner = new StringJoiner(System.lineSeparator());
            failures.forEach(joiner::add);
            assertTrue("E3 corpus failures:\n" + joiner, failures.isEmpty());
        }
    }

    @Test
    public void e3NamedCanaries_under90s() throws Exception {
        for (int csvRow : E3_BUCKET_2_8_1) {
            assertNamedCanary("2.8-1", csvRow);
        }
        assertNamedCanary("2.8-2", E3_BUCKET_2_8_2_CANARY);
        for (int csvRow : E3_NON_UNION_CANARIES) {
            assertNamedCanary("non-UNION", csvRow);
        }
    }

    @Test
    public void timeout513CorpusIndex_matchesFrozenSqlFixtures() throws IOException {
        List<Integer> csvRows = PantoOutstandingSqlFixtures.timeout513CorpusRows();
        assertEquals("expected 74 timeout_513 rows", 74, csvRows.size());
        for (int csvRow : csvRows) {
            assertTrue("missing csv-row-" + csvRow + ".sql",
                    PantoOutstandingSqlFixtures.sqlForCsvRow(csvRow).length() > 0);
        }
    }

    private static void assertNamedCanary(String bucket, int csvRow) throws IOException {
        RowGateResult result = gateRow(csvRow);
        System.out.printf(Locale.ROOT,
                "E3_CANARY bucket=%s csv_row=%d walkMs=%d totalMs=%d walkerFatal=%d%n",
                bucket,
                csvRow,
                result.walkMs(),
                result.totalMs(),
                result.walkerFatalCount());
        assertTrue(result.failureMessage(), result.passed());
    }

    private static RowGateResult gateRow(int csvRow) throws IOException {
        String sql = PantoOutstandingSqlFixtures.sqlForCsvRow(csvRow);
        ParseLatencyReport report = ParseLatencyDiagnosticService.diagnose(sql, SQLPARSER_SQL_TREE_KEY);
        return new RowGateResult(csvRow, report);
    }

    private record RowGateResult(int csvRow, ParseLatencyReport report) {
        long walkMs() {
            return report.walkMs;
        }

        long totalMs() {
            return report.totalMs;
        }

        int walkerFatalCount() {
            return report.walkerFatalCount;
        }

        boolean passed() {
            return report.walkMs < E3_TIMEOUT_MS && report.totalMs < E3_TIMEOUT_MS;
        }

        String failureMessage() {
            if (passed()) {
                return "";
            }
            return String.format(Locale.ROOT,
                    "csv_row=%d walkMs=%d totalMs=%d walkerFatal=%d parseErr=%d (E3 gate: under %dms)",
                    csvRow,
                    report.walkMs,
                    report.totalMs,
                    report.walkerFatalCount,
                    report.parseErrorCount,
                    E3_TIMEOUT_MS);
        }
    }
}
