package cli;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.junit.After;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

import access.Snippet;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import errorhandling.ParseDiagnostic;

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
        assertTrue(parse.has("queryDictionary"));
        assertEquals(
            "{\"query0\":{\"*\":[\"[@1,7:7='*',<289>,1:7]\"]}}",
            parse.get("queryDictionary").toString()
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
        assertTrue(result.has("fatalErrorCount"));
        assertTrue(result.get("fatalErrorCount").getAsInt() > 0);
        assertTrue(result.has("errors"));
        assertTrue(result.get("errors").isJsonArray());
        assertTrue(result.has("messages"));
        assertTrue(result.get("messages").isJsonArray());
        assertFalse("Parser/listener fatal should not return parse payload", result.has("parse"));
        assertFalse("Parser/listener fatal should not be marked partial", result.has("parsePartial"));

        JsonObject firstFatalDiagnostic = result.getAsJsonArray("errors").get(0).getAsJsonObject();
        assertTrue(firstFatalDiagnostic.has("severity"));
        assertEquals("FATAL", firstFatalDiagnostic.get("severity").getAsString());
        assertTrue(firstFatalDiagnostic.has("code"));
        assertTrue(firstFatalDiagnostic.has("message"));
        assertTrue(firstFatalDiagnostic.has("line"));
        assertTrue(firstFatalDiagnostic.has("charPositionInLine"));
        assertTrue(firstFatalDiagnostic.has("source"));
        assertTrue(firstFatalDiagnostic.has("ruleName"));
        assertTrue(firstFatalDiagnostic.has("tokenText"));
        assertTrue(firstFatalDiagnostic.has("recoverable"));
        assertTrue(firstFatalDiagnostic.has("phase"));
        assertTrue(firstFatalDiagnostic.has("exceptionType"));
        assertTrue(firstFatalDiagnostic.has("details"));

        JsonObject firstMessageDiagnostic = result.getAsJsonArray("messages").get(0).getAsJsonObject();
        assertTrue(firstMessageDiagnostic.has("severity"));
        assertTrue(firstMessageDiagnostic.has("code"));
        assertTrue(firstMessageDiagnostic.has("message"));
    }

    @Test
    public void testRoundTripParseSqlFatalQueryReportsMessagesAndErrors() throws Exception {
        String params = "{\"endPoint\":\"SQL\",\"sqlText\":\"select x.missing from cte where campaign_id is not null\"}";
        String request = createJsonRpcRequest("5", "tool/parseSql", params);

        runServiceWithInput(request);

        String output = outContent.toString();
        JsonObject responseObj = findFirstResultObject(output);

        assertEquals("2.0", responseObj.get("jsonrpc").getAsString());
        assertEquals("5", responseObj.get("id").getAsString());
        assertNotNull("Response should have a result", responseObj.get("result"));

        JsonObject result = responseObj.getAsJsonObject("result");
        assertFalse(result.get("ok").getAsBoolean());
        assertTrue(result.get("hasFatalErrors").getAsBoolean());
        assertTrue(result.has("fatalErrorCount"));
        assertTrue(result.get("fatalErrorCount").getAsInt() > 0);

        assertTrue(result.has("errors"));
        assertTrue(result.get("errors").isJsonArray());
        JsonArray errors = result.getAsJsonArray("errors");
        assertTrue(errors.size() > 0);
        for (int i = 0; i < errors.size(); i++) {
            JsonObject diag = errors.get(i).getAsJsonObject();
            assertEquals("FATAL", diag.get("severity").getAsString());
        }

        assertTrue(result.has("messages"));
        assertTrue(result.get("messages").isJsonArray());
        JsonArray messages = result.getAsJsonArray("messages");
        for (int i = 0; i < messages.size(); i++) {
            JsonObject diag = messages.get(i).getAsJsonObject();
            assertTrue(!"FATAL".equals(diag.get("severity").getAsString()));
        }

        assertTrue("SQL AST walker fatal should include partial parse payload", result.has("parse"));
        assertTrue("SQL AST walker fatal should flag parsePartial", result.has("parsePartial"));
        assertTrue(result.get("parsePartial").getAsBoolean());
        JsonObject parse = result.getAsJsonObject("parse");
        assertTrue(parse.has("symbolTable"));
        assertTrue(parse.has("tableDictionary"));
        assertTrue(parse.has("queryDictionary"));

        // Report returned diagnostics in test output for quick triage in consuming projects.
        System.out.println("Roundtrip fatal query errors: " + errors);
        System.out.println("Roundtrip fatal query messages: " + messages);
    }

    @Test
    public void testRoundTripParseSqlQualifiedMissingSourceReturnsFatal() throws Exception {
        String params = "{\"endPoint\":\"SQL\",\"sqlText\":\"select x.missing, cte.missed from cte\"}";
        String request = createJsonRpcRequest("6", "tool/parseSql", params);

        runServiceWithInput(request);

        String output = outContent.toString();
        JsonObject responseObj = findFirstResultObject(output);

        assertEquals("2.0", responseObj.get("jsonrpc").getAsString());
        assertEquals("6", responseObj.get("id").getAsString());
        assertNotNull("Response should have a result", responseObj.get("result"));

        JsonObject result = responseObj.getAsJsonObject("result");
        assertFalse(result.get("ok").getAsBoolean());
        assertTrue(result.get("hasFatalErrors").getAsBoolean());
        assertTrue(result.has("fatalErrorCount"));
        assertTrue(result.get("fatalErrorCount").getAsInt() > 0);

        assertTrue(result.has("errors"));
        JsonArray errors = result.getAsJsonArray("errors");
        assertTrue(errors.size() > 0);

        boolean foundMissingSourceFatal = false;
        for (int i = 0; i < errors.size(); i++) {
            JsonObject diag = errors.get(i).getAsJsonObject();
            if ("FATAL".equals(diag.get("severity").getAsString())
                    && "QUALIFIED_COLUMN_NOT_FOUND_IN_TABLE".equals(diag.get("code").getAsString())) {
                foundMissingSourceFatal = true;
                break;
            }
        }
        assertTrue("Expected QUALIFIED_COLUMN_NOT_FOUND_IN_TABLE fatal diagnostic", foundMissingSourceFatal);
    }

    @Test
    public void testFormatParseResultCountsFatalDiagnosticsFromMessagesList() throws Exception {
        SqlParseMCP mcp = new SqlParseMCP();
        Snippet snippet = new Snippet(
            new HashMap<>(),
            new HashMap<>(),
            new HashMap<>(),
            new HashMap<>(),
            new HashMap<>(),
            new HashSet<>());

        ParseDiagnostic fatalMessageOnly = new ParseDiagnostic(
            ParseDiagnostic.Severity.FATAL,
            "ANY_FATAL_CODE",
            "Any fatal in messages should drive fatal output",
            9,
            1,
            "SomeSource",
            null,
            "token",
            false,
            "ast-walk",
            null,
            null);

        snippet.setParserDiagnosticList(List.of());
        snippet.setParserMessageList(List.of(fatalMessageOnly));

        Method formatMethod = SqlParseMCP.class.getDeclaredMethod("formatParseResult", Snippet.class);
        formatMethod.setAccessible(true);
        JsonObject result = (JsonObject) formatMethod.invoke(mcp, snippet);

        assertFalse(result.get("ok").getAsBoolean());
        assertTrue(result.get("hasFatalErrors").getAsBoolean());
        assertEquals(1, result.get("fatalErrorCount").getAsInt());

        JsonArray errors = result.getAsJsonArray("errors");
        assertEquals(1, errors.size());
        assertEquals("FATAL", errors.get(0).getAsJsonObject().get("severity").getAsString());
        assertEquals("ANY_FATAL_CODE", errors.get(0).getAsJsonObject().get("code").getAsString());

        JsonArray messages = result.getAsJsonArray("messages");
        assertEquals(0, messages.size());
    }

        @Test
        public void testFormatParseResultNormalizesSyntheticDiagnostics() throws Exception {
        SqlParseMCP mcp = new SqlParseMCP();
        Snippet snippet = new Snippet(
            new HashMap<>(),
            new HashMap<>(),
            new HashMap<>(),
            new HashMap<>(),
            new HashMap<>(),
            new HashSet<>());

        ParseDiagnostic parserError = new ParseDiagnostic(
            ParseDiagnostic.Severity.ERROR,
            "PARSER_ERR",
            "Parser error should be warning",
            1,
            2,
            "ParseErrorCollector",
            null,
            "tok",
            true,
            "parse.strategy",
            null,
            null);

        ParseDiagnostic walkerError = new ParseDiagnostic(
            ParseDiagnostic.Severity.ERROR,
            "WALKER_ERR",
            "Walker probable error",
            2,
            3,
            "SqlASTWalkerHelper",
            null,
            "col",
            true,
            "ast-walk",
            null,
            null);

        ParseDiagnostic walkerSevereWarning = new ParseDiagnostic(
            ParseDiagnostic.Severity.SEVERE_WARNING,
            "WALKER_SEVERE",
            "Walker severe warning should stay a severe warning but be handled as a warning downstream",
            3,
            4,
            "SqlASTWalkerHelper",
            null,
            "col2",
            true,
            "ast-walk",
            null,
            null);

        ParseDiagnostic walkerFatal = new ParseDiagnostic(
            ParseDiagnostic.Severity.FATAL,
            "WALKER_FATAL",
            "Walker fatal should remain fatal",
            4,
            5,
            "SqlASTWalkerHelper",
            null,
            "bad",
            false,
            "ast-walk",
            null,
            null);

        snippet.setParserDiagnosticList(List.of(parserError, walkerError, walkerSevereWarning, walkerFatal));
        snippet.setParserMessageList(List.of(parserError, walkerError, walkerSevereWarning, walkerFatal));

        Method formatMethod = SqlParseMCP.class.getDeclaredMethod("formatParseResult", Snippet.class);
        formatMethod.setAccessible(true);
        JsonObject result = (JsonObject) formatMethod.invoke(mcp, snippet);

        assertFalse(result.get("ok").getAsBoolean());
        assertTrue(result.get("hasFatalErrors").getAsBoolean());
        assertEquals(1, result.get("fatalErrorCount").getAsInt());

        JsonArray errors = result.getAsJsonArray("errors");
        assertEquals(1, errors.size());
        assertEquals("FATAL", errors.get(0).getAsJsonObject().get("severity").getAsString());
        assertEquals("WALKER_FATAL", errors.get(0).getAsJsonObject().get("code").getAsString());

        JsonArray messages = result.getAsJsonArray("messages");
        assertEquals(3, messages.size());
        assertEquals("WARNING", messages.get(0).getAsJsonObject().get("severity").getAsString());
        assertEquals("WARNING", messages.get(1).getAsJsonObject().get("severity").getAsString());
        assertEquals("SEVERE_WARNING", messages.get(2).getAsJsonObject().get("severity").getAsString());
        }

   
}
