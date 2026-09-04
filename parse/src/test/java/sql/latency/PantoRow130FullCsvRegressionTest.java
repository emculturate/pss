package sql.latency;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import access.SqlParserAccess;

import astwalkers.AbstractASTWalkerHelper;
import errorhandling.ParseDiagnostic;

import static mumble.SQLParserEndPoints.SQLPARSER_SQL_TREE_KEY;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * W4 Part 1 — row 130 regression on frozen handoff SQL ({@code csv-row-130.sql}).
 */
public class PantoRow130FullCsvRegressionTest {

    static final int CSV_ROW = 130;

    private static final long E3_TIMEOUT_MS = PantoLatencyGateConstants.E3_TIMEOUT_MS;

    @Test
    public void frozenSqlFixture_presentAndNonEmpty() throws IOException {
        Path sqlFile = resolveAuthoritativeSqlFile();
        String sql = Files.readString(sqlFile, StandardCharsets.UTF_8);
        assertTrue(sql.length() > 10_000);
        Assert.assertEquals(sql, PantoOutstandingSqlFixtures.sqlForCsvRow(CSV_ROW));
    }

    @Test
    public void sqlParserAccess_fullCsv_noWalkerStackMisalign() throws IOException {
        String sql = PantoOutstandingSqlFixtures.sqlForCsvRow(CSV_ROW);
        SqlParserAccess access = new SqlParserAccess(false, false, false);
        access.executeTheParse(sql, SQLPARSER_SQL_TREE_KEY);

        assertNoWalkerStackMisalign(access.getAllDiagnostics());
        for (String fatal : access.getFatalErrorList()) {
            assertFalse("unexpected raw subMap fatal: " + fatal, fatal.contains("subMap"));
        }
    }

    @Ignore("Manual — row 130 diagnostic timing gate; mvn -pl parse -Dtest=PantoRow130FullCsvRegressionTest#diagnosticService_fullCsv_underE3Gate_noWalkerStackMisalign test")
    @Test
    public void diagnosticService_fullCsv_underE3Gate_noWalkerStackMisalign() throws IOException {
        String sql = PantoOutstandingSqlFixtures.sqlForCsvRow(CSV_ROW);
        ParseLatencyReport report = ParseLatencyDiagnosticService.diagnose(sql, SQLPARSER_SQL_TREE_KEY);

        assertTrue("walkMs=" + report.walkMs, report.walkMs < E3_TIMEOUT_MS);
        assertTrue("totalMs=" + report.totalMs, report.totalMs < E3_TIMEOUT_MS);
        assertNoWalkerStackMisalign(report.diagnostics);
        for (ParseDiagnostic diagnostic : report.diagnostics) {
            if (diagnostic != null
                    && diagnostic.severity() == ParseDiagnostic.Severity.FATAL
                    && diagnostic.message() != null) {
                assertFalse("unexpected raw subMap fatal: " + diagnostic.message(),
                        diagnostic.message().contains("subMap"));
            }
        }
    }

    private static void assertNoWalkerStackMisalign(Iterable<ParseDiagnostic> diagnostics) {
        for (ParseDiagnostic diagnostic : diagnostics) {
            if (diagnostic == null) {
                continue;
            }
            assertFalse("unexpected walker stack mis-align: " + diagnostic,
                    AbstractASTWalkerHelper.DIAG_AST_WALKER_STACK_MISALIGN.equals(diagnostic.code()));
        }
    }

    private static Path resolveAuthoritativeSqlFile() {
        for (Path root : new Path[] {
                Path.of("docs/rmcp-handoff/5.1.3-panto-outstanding/sql"),
                Path.of("parse/docs/rmcp-handoff/5.1.3-panto-outstanding/sql"),
        }) {
            Path file = root.resolve("csv-row-" + CSV_ROW + ".sql");
            if (Files.isRegularFile(file)) {
                return file;
            }
        }
        throw new IllegalStateException("csv-row-130.sql not found in handoff pack");
    }
}
