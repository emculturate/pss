package sql.latency;

import java.io.IOException;

import org.junit.Ignore;
import org.junit.Test;

import access.SqlParserAccess;

import static mumble.SQLParserEndPoints.SQLPARSER_SQL_TREE_KEY;
import static org.junit.Assert.assertEquals;

/** Production-parity between {@link ParseLatencyDiagnosticService} and {@link SqlParserAccess}. */
public class ParseLatencyDiagnosticSllLlParityTest {

    @Test
    public void row28_diagnosticService_matchesAccessFatalCount() throws IOException {
        String sql = PantoOutstandingSqlFixtures.sqlForCsvRow(28);
        ParseLatencyReport diagnostic = ParseLatencyDiagnosticService.diagnose(sql, SQLPARSER_SQL_TREE_KEY);

        SqlParserAccess access = new SqlParserAccess(false, false, false);
        access.executeTheParse(sql, SQLPARSER_SQL_TREE_KEY);

        assertEquals(access.getSnippet().getFatalErrorCount(), diagnostic.walkerFatalCount);
    }

    @Ignore("Manual — SLL probe only; mvn -pl parse -Dtest=ParseLatencyDiagnosticSllLlParityTest#row28_sllProbe_reportsSllParsePhaseFailure test")
    @Test
    public void row28_sllProbe_reportsSllParsePhaseFailure() throws IOException {
        String sql = PantoOutstandingSqlFixtures.sqlForCsvRow(28);
        ParseLatencyReport report = ParseLatencyDiagnosticService.diagnoseWithSllProbe(
                sql, SQLPARSER_SQL_TREE_KEY);
        assertEquals(1, report.llReparseAfterSllFailure);
        assertEquals(0, report.walkerFatalCount);
    }
}
