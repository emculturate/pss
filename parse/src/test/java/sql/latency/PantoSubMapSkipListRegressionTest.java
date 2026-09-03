package sql.latency;

import java.io.IOException;
import java.util.List;

import org.junit.Test;

import access.SqlParserAccess;
import access.WalkerWalkExceptionGate;

import astwalkers.AbstractASTWalkerHelper;
import errorhandling.ParseDiagnostic;

import static access.SqlParserAccess.DIAG_AST_WALK_SKIPPED_DUE_TO_PARSE_ERRORS;
import static mumble.SQLParserEndPoints.SQLPARSER_SQL_TREE_KEY;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Regression: former subMap skip-list rows complete through access + diagnostic paths.
 */
public class PantoSubMapSkipListRegressionTest {

    /** Former syntax_corrupt Acquia export representative. */
    private static final int ROW_ACQUIA = 28;

    /** Former legitimate_complex giant CASE row. */
    private static final int ROW_GIANT_CASE = 130;

    @Test
    public void diagnosticService_walkCatchParity_recordsStructuredMisalign() {
        List<ParseDiagnostic> captured = new java.util.ArrayList<>();
        Exception npe = new NullPointerException(
                "Cannot invoke \"java.util.Map.remove(Object)\" because \"subMap\" is null");
        WalkerWalkExceptionGate.recognizeWalkException(
                npe, "ParseLatencyDiagnosticService", captured, List.of());

        assertTrue(captured.stream().anyMatch(d -> d != null
                && AbstractASTWalkerHelper.DIAG_AST_WALKER_STACK_MISALIGN.equals(d.code())
                && d.severity() == ParseDiagnostic.Severity.FATAL
                && "ParseLatencyDiagnosticService".equals(d.source())));
    }

    @Test
    public void diagnosticService_selectFrom_skipsAstWalk() {
        ParseLatencyReport report = ParseLatencyDiagnosticService.diagnose("select from", SQLPARSER_SQL_TREE_KEY);
        assertTrue("walk should be skipped on parse failure", report.walkMs < 200L);
        assertTrue("total should stay fast", report.totalMs < 5_000L);
        assertTrue(report.diagnostics.stream().anyMatch(d -> d != null
                && DIAG_AST_WALK_SKIPPED_DUE_TO_PARSE_ERRORS.equals(d.code())
                && d.severity() == ParseDiagnostic.Severity.WARNING
                && "ParseLatencyDiagnosticService".equals(d.source())));
    }

    @Test
    public void sqlParserAccess_selectFrom_skipsAstWalk() {
        SqlParserAccess access = new SqlParserAccess(false, false, false);
        access.executeTheParse("select from", SQLPARSER_SQL_TREE_KEY);

        for (String fatal : access.getFatalErrorList()) {
            assertFalse("unexpected walker fatal: " + fatal, fatal.contains("subMap"));
        }
        assertTrue(access.getAllDiagnostics().stream().anyMatch(d -> d != null
                && DIAG_AST_WALK_SKIPPED_DUE_TO_PARSE_ERRORS.equals(d.code())));
    }

    @Test
    public void sqlParserAccess_formerSkipRow28_noSubMapFatal() throws IOException {
        String sql = PantoOutstandingSqlFixtures.sqlForCsvRow(ROW_ACQUIA);
        SqlParserAccess access = new SqlParserAccess(false, false, false);
        access.executeTheParse(sql, SQLPARSER_SQL_TREE_KEY);

        for (String fatal : access.getFatalErrorList()) {
            assertFalse("unexpected walker fatal: " + fatal, fatal.contains("subMap"));
        }
    }

    @Test
    public void sqlParserAccess_row130_fullCsvSql_noSubMapFatal() throws IOException {
        String sql = PantoOutstandingSqlFixtures.sqlForCsvRow(ROW_GIANT_CASE);
        SqlParserAccess access = new SqlParserAccess(false, false, false);
        access.executeTheParse(sql, SQLPARSER_SQL_TREE_KEY);

        for (String fatal : access.getFatalErrorList()) {
            assertFalse("unexpected walker fatal: " + fatal, fatal.contains("subMap"));
        }
    }

    @Test
    public void diagnosticService_formerSkipRows_underE3Gate() throws IOException {
        int[] formerSkipRows = {
                28, 30, 31, 32, 41, 130, 314, 315, 1814, 2120,
                4157, 4158, 4163, 4164, 4170, 4171, 5860, 5861, 5862, 5863
        };
        for (int csvRow : formerSkipRows) {
            String sql = PantoOutstandingSqlFixtures.sqlForCsvRow(csvRow);
            ParseLatencyReport report = ParseLatencyDiagnosticService.diagnose(sql, SQLPARSER_SQL_TREE_KEY);
            assertTrue(
                    "csv_row=" + csvRow + " walkMs=" + report.walkMs + " totalMs=" + report.totalMs,
                    report.walkMs < PantoTimeoutCorpusE3GateTest.E3_TIMEOUT_MS
                            && report.totalMs < PantoTimeoutCorpusE3GateTest.E3_TIMEOUT_MS);
        }
    }
}
