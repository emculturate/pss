package cli;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.google.gson.JsonObject;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Parameterized MCP CLI regression for bound-query rows with non-ASCII {@code sqlText}.
 */
@RunWith(Parameterized.class)
public class SqlParseMcpBoundQueryUnicodeRegressionTest {

    private static final int[] UNICODE_ROWS = {314, 315, 475, 476, 1814, 1827, 1828};
    private static final String ENDPOINT = "SQL";

    private final int csvRow;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private PrintStream originalOut;
    private PrintStream originalErr;
    private InputStream originalIn;

    public SqlParseMcpBoundQueryUnicodeRegressionTest(int csvRow) {
        this.csvRow = csvRow;
    }

    @Parameters(name = "row-{0}")
    public static List<Object[]> rows() {
        List<Object[]> params = new ArrayList<>();
        for (int row : UNICODE_ROWS) {
            params.add(new Object[] {row});
        }
        return params;
    }

    @Before
    public void setUpStreams() {
        originalOut = System.out;
        originalErr = System.err;
        originalIn = System.in;
        outContent.reset();
        System.setOut(new PrintStream(outContent));
    }

    @After
    public void restoreStreams() {
        System.setOut(originalOut);
        System.setErr(originalErr);
        System.setIn(originalIn);
    }

    @Test
    public void parseSqlJsonRpc_nonAsciiSql_reachesParserWithoutGsonFailure() throws IOException {
        String sql = McpBoundQueryFixtures.sqlForRow(csvRow);
        assertTrue("fixture should contain non-ASCII", containsNonAscii(sql));

        byte[] request = McpTestFraming.parseSqlRequest(String.valueOf(csvRow), ENDPOINT, sql);
        System.setIn(new ByteArrayInputStream(request));
        SqlParseMCP.main(new String[0]);

        SqlParseMcpBoundQueryRegressionTest.assertMcpTransportSucceeded(
                McpTestFraming.firstJsonRpcResponse(outContent.toByteArray()),
                String.valueOf(csvRow),
                outContent.size());
    }

    @Test
    public void parseSqlStream_nonAsciiSql_reachesParserWithoutGsonFailure() throws IOException {
        String sql = McpBoundQueryFixtures.sqlForRow(csvRow);
        byte[] sqlBytes = sql.getBytes(StandardCharsets.UTF_8);
        byte[] request = McpTestFraming.parseSqlStreamRequest(String.valueOf(csvRow), ENDPOINT, sqlBytes);
        System.setIn(new ByteArrayInputStream(request));
        SqlParseMCP.main(new String[0]);

        SqlParseMcpBoundQueryRegressionTest.assertMcpTransportSucceeded(
                McpTestFraming.firstJsonRpcResponse(outContent.toByteArray()),
                String.valueOf(csvRow),
                outContent.size());
    }

    private static boolean containsNonAscii(String text) {
        return text.chars().anyMatch(ch -> ch > 127);
    }
}
