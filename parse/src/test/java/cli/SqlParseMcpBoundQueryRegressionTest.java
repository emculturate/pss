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

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Regression for bound-query CSV rows that previously failed in RMCP's MCP JSON-RPC path:
 * <ul>
 *   <li>129 — ~74k ASCII ({@code Unterminated string} from char-based frame truncation under bulk load)</li>
 *   <li>Sequential bulk run mixing row 129 with Unicode rows</li>
 * </ul>
 *
 * <p>Non-ASCII rows: {@link SqlParseMcpBoundQueryUnicodeRegressionTest}.
 * Fixtures: {@code src/test/resources/mcp/bound-query/csv-row-&lt;n&gt;.sql}
 */
public class SqlParseMcpBoundQueryRegressionTest {

    private static final String ENDPOINT = "SQL";
    private static final int LARGE_ASCII_ROW = 129;

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private PrintStream originalOut;
    private PrintStream originalErr;
    private InputStream originalIn;

    @Before
    public void setUpStreams() {
        originalOut = System.out;
        originalErr = System.err;
        originalIn = System.in;
        outContent.reset();
        errContent.reset();
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }

    @After
    public void restoreStreams() {
        System.setOut(originalOut);
        System.setErr(originalErr);
        System.setIn(originalIn);
    }

    @Test(timeout = 120_000)
    public void row129_parseSqlJsonRpc_largeAscii_reachesParser() throws IOException {
        String sql = McpBoundQueryFixtures.sqlForRow(LARGE_ASCII_ROW);
        assertTrue("row 129 fixture should be large", sql.length() > 70_000);
        assertFalse("row 129 fixture should be ASCII", containsNonAscii(sql));

        byte[] request = McpTestFraming.parseSqlRequest("129", ENDPOINT, sql);
        System.setIn(new ByteArrayInputStream(request));
        SqlParseMCP.main(new String[0]);

        assertMcpTransportSucceeded(McpTestFraming.firstJsonRpcResponse(outContent.toByteArray()), "129", outContent.size());
    }

    @Test(timeout = 120_000)
    public void row129_parseSqlStream_largeAscii_reachesParser() throws IOException {
        String sql = McpBoundQueryFixtures.sqlForRow(LARGE_ASCII_ROW);
        byte[] sqlBytes = sql.getBytes(StandardCharsets.UTF_8);
        byte[] request = McpTestFraming.parseSqlStreamRequest("129-stream", ENDPOINT, sqlBytes);
        System.setIn(new ByteArrayInputStream(request));
        SqlParseMCP.main(new String[0]);

        assertMcpTransportSucceeded(McpTestFraming.firstJsonRpcResponse(outContent.toByteArray()), "129-stream", outContent.size());
    }

    @Test(timeout = 300_000)
    public void boundQueryRows_sequentialBulkRun_noFramingOrGsonFailures() throws IOException {
        int[] allRows = {LARGE_ASCII_ROW, 314, 315, 475, 476, 1814, 1827, 1828};
        ByteArrayOutputStream combined = new ByteArrayOutputStream();
        List<String> expectedIds = new ArrayList<>();

        for (int row : allRows) {
            String id = "bulk-" + row;
            expectedIds.add(id);
            String sql = McpBoundQueryFixtures.sqlForRow(row);
            combined.write(McpTestFraming.parseSqlRequest(id, ENDPOINT, sql));
        }

        System.setIn(new ByteArrayInputStream(combined.toByteArray()));
        SqlParseMCP.main(new String[0]);

        List<JsonObject> responses = McpTestFraming.allJsonRpcResponses(outContent.toByteArray());
        assertEquals("one JSON-RPC response per request", expectedIds.size(), responses.size());

        for (int index = 0; index < expectedIds.size(); index++) {
            assertMcpTransportSucceeded(responses.get(index), expectedIds.get(index), outContent.size());
        }
    }

    static void assertMcpTransportSucceeded(JsonObject response, String expectedId, int outputBytes) {
        assertNotNull("expected JSON-RPC response; stdout bytes=" + outputBytes, response);
        assertEquals(expectedId, response.get("id").getAsString());
        assertNull("JSON-RPC transport error: " + describeError(response), response.get("error"));
        assertNotNull("expected tool result payload", response.get("result"));

        JsonObject result = response.getAsJsonObject("result");
        if (result.has("errors") && result.get("errors").isJsonArray()) {
            for (JsonElement element : result.getAsJsonArray("errors")) {
                String message = element.getAsJsonObject().get("message").getAsString();
                assertFalse("Gson/framing failure leaked into parse errors: " + message,
                        message.contains("Unterminated string"));
                assertFalse(message.contains("MalformedJsonException"));
            }
        }
    }

    private static boolean containsNonAscii(String text) {
        return text.chars().anyMatch(ch -> ch > 127);
    }

    private static String describeError(JsonObject response) {
        if (response == null || !response.has("error")) {
            return "";
        }
        JsonObject error = response.getAsJsonObject("error");
        return error.get("message").getAsString();
    }
}
