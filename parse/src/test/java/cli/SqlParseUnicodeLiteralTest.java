package cli;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class SqlParseUnicodeLiteralTest {
    private static final String LITERAL =
            "'A\u2013B\u2014C\u2212D\u00a0E\u202fF\uFE0FG caf\u00e9 \u2019 \u201c\u201d \uff21\uff22'";
    private final ByteArrayOutputStream output = new ByteArrayOutputStream();
    private InputStream originalIn;
    private PrintStream originalOut;

    @Before
    public void captureStreams() {
        originalIn = System.in;
        originalOut = System.out;
        System.setOut(new PrintStream(output, true, StandardCharsets.UTF_8));
    }

    @After
    public void restoreStreams() {
        System.setIn(originalIn);
        System.setOut(originalOut);
    }

    @Test
    public void sqlPreservesUnicodeLiteralsAcrossTransports() {
        assertAcrossTransports("SQL", "select " + LITERAL + " as x", LITERAL);
    }

    @Test
    public void queryPreservesUnicodeLiteralsAcrossTransports() {
        assertAcrossTransports("QUERY", "select " + LITERAL + " as x", LITERAL);
    }

    @Test
    public void predicandPreservesUnicodeLiteralsAcrossTransports() {
        assertAcrossTransports("PREDICAND", LITERAL, LITERAL);
    }

    @Test
    public void inListPreservesUnicodeLiteralsAcrossTransports() {
        assertAcrossTransports("IN_LIST", "(" + LITERAL + ")", LITERAL);
    }

    @Test
    public void escapedQuotesPreserveUnicodeAcrossTransports() {
        String literal = "'O''Brien \u2013 can\\'t \u00a0 \uFE0F'";
        assertAcrossTransports("PREDICAND", literal, literal);
    }

    @Test
    public void base64RequestPreservesUnicodeLiteral() {
        String encoded = Base64.getEncoder().encodeToString(LITERAL.getBytes(StandardCharsets.UTF_8));
        String params = "{\"endPoint\":\"PREDICAND\",\"sqlTextEncoding\":\"base64\",\"sqlText\":\""
                + encoded + "\"}";
        JsonObject response = runMcp(McpTestFraming.jsonRpcFrame("base64", "tool/parseSql", params));
        assertResult(response.getAsJsonObject("result"), expectedTree("PREDICAND", LITERAL));
    }

    @Test
    public void sqlNormalizesSyntaxWithoutChangingLiteralAcrossTransports() {
        String sql = "select\u00a0" + LITERAL + " as x -- author's \u2014\n";
        JsonObject expected = expectedTree("SQL", LITERAL);

        assertResult(runMcp(McpTestFraming.parseSqlRequest("json", "SQL", sql))
                .getAsJsonObject("result"), expected);
        assertResult(runMcp(McpTestFraming.parseSqlStreamRequest(
                "stream", "SQL", sql.getBytes(StandardCharsets.UTF_8)))
                .getAsJsonObject("result"), expected);
        output.reset();
        SqlParseBulk.run("SQL", new ByteArrayInputStream(sql.getBytes(StandardCharsets.UTF_8)));
        assertResult(JsonParser.parseString(output.toString(StandardCharsets.UTF_8)).getAsJsonObject(), expected);
    }

    @Test
    public void normalizerDistinguishesLiteralsFromCommentsAndIdentifiers() {
        String sql = "select \"author's\u2013name\", 'O''Brien \u2013 \uFE0F', "
                + "'can\\'t \u00a0go' /* author's \u2014 */\u202f-- author's \u2212\n"
                + "where 3 \u2212 1 = 2\uFE0F";
        String expected = "select \"author's-name\", 'O''Brien \u2013 \uFE0F', "
                + "'can\\'t \u00a0go' /* author's - */ -- author's -\n"
                + "where 3 - 1 = 2";
        assertEquals(expected, SqlParseMcpSupport.normalizeSqlTextForMcp(sql));
    }

    @Test
    public void normalizedDashesStartCommentsWithoutConsumingFollowingLiteral() {
        String sql = "select 1 \u2013\u2014 author's note\n, '\u2013'";
        String expected = "select 1 -- author's note\n, '\u2013'";
        assertEquals(expected, SqlParseMcpSupport.normalizeSqlTextForMcp(sql));
    }

    private void assertAcrossTransports(String endpoint, String sql, String literal) {
        JsonObject expected = expectedTree(endpoint, literal);
        assertResult(SqlParseMcpSupport.parseAndFormat(endpoint, sql), expected);

        JsonObject rpc = runMcp(McpTestFraming.parseSqlRequest("json", endpoint, sql));
        assertEquals("json", rpc.get("id").getAsString());
        assertResult(rpc.getAsJsonObject("result"), expected);

        JsonObject stream = runMcp(McpTestFraming.parseSqlStreamRequest(
                "stream", endpoint, sql.getBytes(StandardCharsets.UTF_8)));
        assertEquals("stream", stream.get("id").getAsString());
        assertResult(stream.getAsJsonObject("result"), expected);

        output.reset();
        SqlParseBulk.run(endpoint, new ByteArrayInputStream(sql.getBytes(StandardCharsets.UTF_8)));
        JsonObject bulk = JsonParser.parseString(output.toString(StandardCharsets.UTF_8)).getAsJsonObject();
        assertEquals("sql-bulk", bulk.get("transport").getAsString());
        assertResult(bulk, expected);
    }

    private JsonObject runMcp(byte[] request) {
        output.reset();
        System.setIn(new ByteArrayInputStream(request));
        SqlParseMCP.main(null);
        JsonObject response = McpTestFraming.firstJsonRpcResponse(output.toByteArray());
        assertNotNull(response);
        return response;
    }

    private static void assertResult(JsonObject result, JsonObject expectedTree) {
        assertTrue(result.get("ok").getAsBoolean());
        assertFalse(result.get("hasFatalErrors").getAsBoolean());
        assertEquals(new JsonArray(), result.getAsJsonArray("messages"));
        assertEquals(expectedTree, result.getAsJsonObject("parse").getAsJsonObject("sqlTree"));
    }

    private static JsonObject expectedTree(String endpoint, String literal) {
        JsonObject literalNode = new JsonObject();
        literalNode.addProperty("literal", literal);
        JsonObject contents = literalNode;
        if ("SQL".equals(endpoint) || "QUERY".equals(endpoint)) {
            literalNode.addProperty("alias", "x");
            JsonObject item = new JsonObject();
            item.add("1", literalNode);
            contents = new JsonObject();
            contents.add("select", item);
        } else if ("IN_LIST".equals(endpoint)) {
            JsonObject item = new JsonObject();
            item.add("1", literalNode);
            contents = new JsonObject();
            contents.add("list", item);
        }
        JsonObject tree = new JsonObject();
        tree.add(endpoint, contents);
        return tree;
    }
}
