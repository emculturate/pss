package cli;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;

import org.junit.After;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class SqlParseMCPTest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;
    private final InputStream originalIn = System.in;

    private static final Gson gson = new Gson();
  

    @Before
    public void setUpStreams() {
        outContent.reset();
        errContent.reset();
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }

    @After
    public void restoreStreamsAndStopService() {
        System.setOut(originalOut);
        System.setErr(originalErr);
        System.setIn(originalIn);
    }

    private void runServiceWithInput(String input) {
        if (!input.endsWith("\n")) {
            input += "\n";
        }
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        SqlParseMCP.main(null); // Run in the same thread
    }

    private String createJsonRpcRequest(String id, String method, String paramsJson) {
        String payload = String.format(
            "{\"jsonrpc\":\"2.0\",\"id\":\"%s\",\"method\":\"%s\",\"params\":%s}",
            id, method, paramsJson
        );
        return "Content-Length: " + payload.length() + "\r\n\r\n" + payload;
    }

    private JsonObject findFirstResultObject(String output) {
        int idx = 0;
        while (idx < output.length()) {
            int start = output.indexOf('{', idx);
            if (start == -1) break;
            int end = output.indexOf('}', start);
            if (end == -1) break;
            try {
                String candidate = output.substring(start, output.lastIndexOf('}') + 1);
                JsonObject obj = gson.fromJson(candidate, JsonObject.class);
                if (obj.has("result")) {
                    // Print any extra text before the result object
                    if (idx < start) {
                        String extra = output.substring(idx, start);
                        if (!extra.trim().isEmpty()) {
                            System.out.println("Extra output before result object: [" + extra.trim() + "]");
                        }
                    }
                    return obj;
                }
            } catch (Exception e) {
                // Ignore and continue
            }
            idx = end + 1;
        }
        return null;
    }

    @Test
    public void testSuccessfulParseSql() throws InterruptedException {
        String params = "{\"endPoint\":\"SQL\",\"sqlText\":\"SELECT * FROM mytable;\"}";
        String request = createJsonRpcRequest("1", "tool/parseSql", params);

        runServiceWithInput(request);

        String output = outContent.toString();
        assertTrue(output.startsWith("Content-Length:"));

        JsonObject responseObj = findFirstResultObject(output);

        assertNotNull("Response should have a result", responseObj.get("result"));

        JsonObject result = responseObj.getAsJsonObject("result");
        assertTrue(result.get("ok").getAsBoolean());
        assertFalse(result.get("hasFatalErrors").getAsBoolean());
        assertNotNull(result.getAsJsonObject("parse"));
        JsonObject parse = result.getAsJsonObject("parse");
        assertTrue(parse.has("sqlTree"));
        assertEquals(
                "{\"SQL\":{\"select\":{\"1\":{\"column\":{\"name\":\"*\",\"table_ref\":\"*\"}}},\"from\":{\"table\":{\"table\":\"mytable\"}}}}",
                parse.get("sqlTree").toString()
        );
        assertTrue(parse.has("symbolTable"));
    	System.out.println("Symbol Table: " + parse.get("symbolTable").toString());
	    assertEquals(
                "{\"query0\":{\"query_dictionary\":{\"*\":[\"[@1,7:7='*',<289>,1:7]\"]},\"table_dictionary\":{\"mytable\":{\"*\":[\"[@1,7:7='*',<289>,1:7]\"]}},\"interface\":{\"*\":[{\"name\":\"*\",\"table_ref\":\"*\"}]}}}",
                parse.get("symbolTable").toString()
        );
        assertTrue(parse.has("tableDictionary"));
        assertEquals(
                "{\"mytable\":{\"*\":[\"[@1,7:7='*',<289>,1:7]\"]}}",
                parse.get("tableDictionary").toString()
        );
        assertTrue(parse.has("queryColumnDictionary"));
        assertEquals(
                "{\"query0\":{\"*\":[\"[@1,7:7='*',<289>,1:7]\"]}}",
                parse.get("queryColumnDictionary").toString()
        );
        assertTrue(parse.has("substitutionsMap"));
        assertEquals(
                "{}",
                parse.get("substitutionsMap").toString()
        );
        assertTrue(parse.has("queryInterface"));
        assertEquals(
                "[\"*\"]",
                parse.get("queryInterface").toString()
        );
        assertTrue(result.has("messages"));
    }

    @Test
    public void testParseSqlWithInvalidParams() throws InterruptedException {
        // Missing 'endPoint' parameter
        String params = "{\"sqlText\":\"SELECT * FROM mytable\"}";
        String request = createJsonRpcRequest("2", "tool/parseSql", params);

        runServiceWithInput(request);

        String output = outContent.toString();
        String jsonResponse = output.substring(output.indexOf("{"));
        JsonObject responseObj = gson.fromJson(jsonResponse, JsonObject.class);

        // Assert error response
        assertNotNull(responseObj.get("error"));
        JsonObject error = responseObj.getAsJsonObject("error");
        assertEquals(-32602, error.get("code").getAsInt()); // Invalid params
        assertTrue(error.get("message").getAsString().contains("Parameters must include 'sqlText' and 'endPoint'"));
    }

    @Test
    public void testParseSqlWithMissingSqlText() throws InterruptedException {
        // Missing 'sqlText' parameter
        String params = "{\"endPoint\":\"sql\"}";
        String request = createJsonRpcRequest("3", "tool/parseSql", params);

        runServiceWithInput(request);

        String output = outContent.toString();
        String jsonResponse = output.substring(output.indexOf("{"));
        JsonObject responseObj = gson.fromJson(jsonResponse, JsonObject.class);

        // Assert error response
        assertNotNull(responseObj.get("error"));
        JsonObject error = responseObj.getAsJsonObject("error");
        assertEquals(-32602, error.get("code").getAsInt()); // Invalid params
        assertTrue(error.get("message").getAsString().contains("Parameters must include 'sqlText' and 'endPoint'"));
    }

    @Test
    public void testParseSqlWithSyntaxError() throws InterruptedException {
        // Syntactically incorrect SQL
        String params = "{\"endPoint\":\"SQL\",\"sqlText\":\"select obj join from tab1\"}";
        String request = createJsonRpcRequest("4", "tool/parseSql", params);

        runServiceWithInput(request);

        String output = outContent.toString();
        JsonObject responseObj = findFirstResultObject(output);

        assertEquals("2.0", responseObj.get("jsonrpc").getAsString());
        assertEquals("4", responseObj.get("id").getAsString());
        assertNotNull("Response should have a result", responseObj.get("result"));

        JsonObject result = responseObj.getAsJsonObject("result");
        assertFalse(result.get("ok").getAsBoolean());
        assertTrue(result.get("hasFatalErrors").getAsBoolean());
        assertNotNull(result.getAsJsonObject("errors"));
        JsonObject errors = result.getAsJsonObject("errors");
        assertTrue(errors.get("fatalErrorCount").getAsInt() > 0);
        assertTrue(errors.get("errors").isJsonArray());
        System.out.println("Captured errors: " + errors.get("errors").toString());
        assertEquals(
            "[\"Line 1:11 - null - unexpected input: 'join'\",\"Exception when walking the parse tree: Cannot invoke \\\"java.util.Map.remove(Object)\\\" because \\\"subMap\\\" is null\"]",
            errors.get("errors").toString());
    }

   
}
