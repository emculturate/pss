package sql.latency;

import java.io.IOException;
import java.util.List;

import org.junit.Test;

import access.SqlParserAccess;

import static mumble.SQLParserEndPoints.SQLPARSER_SQL_TREE_KEY;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Cluster B production regression: ten rows where SLL produced false fatals but
 * {@link SqlParserAccess} (LL) parses with zero walker fatals. Index:
 * {@code cluster-b-sll-regression-rows.json}.
 */
public class ClusterBSllRegressionTest {

    @Test
    public void clusterBIndex_hasTenRows() throws IOException {
        List<Integer> rows = PantoOutstandingSqlFixtures.clusterBRegressionRows();
        assertEquals(10, rows.size());
        assertTrue(rows.contains(605));
        assertTrue(rows.contains(5594));
        assertFalse(rows.contains(4197));
    }

    @Test
    public void clusterB_fixturesPresent() throws IOException {
        for (int csvRow : PantoOutstandingSqlFixtures.clusterBRegressionRows()) {
            String sql = PantoOutstandingSqlFixtures.sqlForCsvRow(csvRow);
            assertTrue("csv_row=" + csvRow + " fixture empty", sql.length() > 100);
        }
    }

    @Test
    public void clusterB_productionAccessPath_zeroFatals() throws IOException {
        for (int csvRow : PantoOutstandingSqlFixtures.clusterBRegressionRows()) {
            String sql = PantoOutstandingSqlFixtures.sqlForCsvRow(csvRow);
            SqlParserAccess access = new SqlParserAccess(false, false, false);
            access.executeTheParse(sql, SQLPARSER_SQL_TREE_KEY);
            assertEquals("csv_row=" + csvRow, 0, access.getSnippet().getFatalErrorCount());
        }
    }

    @Test
    public void clusterB_diagnosticService_matchesAccessWalkerFatals() throws IOException {
        for (int csvRow : PantoOutstandingSqlFixtures.clusterBRegressionRows()) {
            String sql = PantoOutstandingSqlFixtures.sqlForCsvRow(csvRow);
            ParseLatencyReport report = ParseLatencyDiagnosticService.diagnose(sql, SQLPARSER_SQL_TREE_KEY);
            assertEquals(
                    "csv_row=" + csvRow + " diagnostic walkerFatal parity with SqlParserAccess",
                    0,
                    report.walkerFatalCount);
        }
    }
}
