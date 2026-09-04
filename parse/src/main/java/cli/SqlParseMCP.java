package cli;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

public class SqlParseMCP {

    private static final Gson gson = new GsonBuilder().serializeNulls().create();

    /** Diagnostic when MCP response serialization fails after a successful parse. */
    static final String MCP_RESPONSE_SERIALIZE_ERROR = SqlParseMcpSupport.MCP_RESPONSE_SERIALIZE_ERROR;

    // --- JSON-RPC Message Classes ---

    static class JsonRpcRequest {
        String jsonrpc;
        String method;
        JsonElement params;
        String id;
    }

    static class JsonRpcResponse {
        String jsonrpc = "2.0";
        Object result;
        String id;

        JsonRpcResponse(String id, Object result) {
            this.id = id;
            this.result = result;
        }
    }

    static class JsonRpcError {
        int code;
        String message;

        JsonRpcError(int code, String message) {
            this.code = code;
            this.message = message;
        }
    }

    static class JsonRpcErrorResponse {
        String jsonrpc = "2.0";
        JsonRpcError error;
        String id;

        JsonRpcErrorResponse(String id, int code, String message) {
            this.id = id;
            this.error = new JsonRpcError(code, message);
        }
    }

    // --- MCP Tool Definition ---

    static class Tool {
        String name;
        String description;
        String responseFormat;

        Tool(String name, String description, String responseFormat) {
            this.name = name;
            this.description = description;
            this.responseFormat = responseFormat;
        }
    }

    // --- Request Handlers ---

    private Object handleInitialize(JsonRpcRequest request) {
        return Map.of("tools", getTools());
    }

    private Object handleListTools(JsonRpcRequest request) {
        return getTools();
    }

    private Object handlePing(JsonRpcRequest request) {
        return "pong";
    }

    private List<Tool> getTools() {
        String responseFormat = """
Response format
This tool returns a JSON object with the following structure:

ok (boolean):
  true if the SQL was parsed successfully with no fatal errors.
  false if there were parsing errors or the service failed.

When ok is true, the object includes:
  parse.sqlTree: A structured representation of the SQL query (type, selectList, fromClause, whereClause, etc.). Use this to explain the logical structure of the query to the user (what the query selects, from which tables, and how joins/filters work).
  parse.substitutionsMap: Shows variable names and types which need to be resolved in order to actually run the query. Variable types represent specific locations in a SQL statement and must be filled only with syntactically correct substitutions for that type. A very common variable will be the "tuple" type variable which represents a pseudo-name for a table or subquery representing the data given by the name of the variable (for example mapping <[Panto_JMN_Fulfillment].[academic_period_lkp]> to the logical name academic_period_lkp). Use this to clarify how placeholders or logical names map to an actual table containing academic periods, or to a subquery, view or With statement CTE that contains that type of data.
  parse.tableDictionary: Dictionary of tables referenced in the query. Columns are listed within the table (or tuple object, e.g., view, CTE or tuple substitution variable) Use this when the user asks about which columns are referenced or where in the query they appear.
    parse.queryDictionary: Dictionary of query-level columns referenced and surfaced while building nested query context. Use this to understand how columns are discovered and propagated in nested query scopes.
  parse.symbolTable: Information about tables, aliases, column names introduced at each level of nested subquery. Use this to explain how column names are introduced at each level and act as aliases for column names at deeper levels of the nest.
  parse.queryInterface: Each Select statement has its own set of output columns. This is the interface that the select statement produces when it is run successfully. Use this to describe what attributes of the represented objects are output for each row.
  parse.messages: Additional info about the parse operation. Sometimes the parser can be called with additional flags to report on such things as ambiguities in the grammar, and the number of retries it needed to find a succssful parse.

When ok is false, the object includes:
        hasFatalErrors: If the parser tried and failed to parse something, this flag will be set to true.
        fatalErrorCount: Number of fatal parse errors, if available.
        errors: Flat list of ParseDiagnostic objects describing fatal parse problems (for example syntax errors or unknown identifiers).
        messages: Additional info about the parse operation as ParseDiagnostic objects.

Fatal behavior depends on fatal source:
    - Parser/listener/collector fatal: response includes only errors and messages (no parse object).
    - SQL AST walker fatal: response includes errors and messages, and also includes parse with parsePartial=true.


When ok is false, you should:
    Explain to the user what went wrong based on errors and messages.
  If possible, suggest specific changes to fix the SQL query. For SELECT statements using the endpoint "SQL" the statement must end with a semi-colon. So if you get an error you might try to resubmit after appending a semicolon to the end of the string.

When ok is true, you should:
  Summarize the query in plain language (e.g., what it selects, from which tables or tuples, and any filters).
  you could also describe the complexity of the query, and suggest simplifications.
  Optionally describe:
    How data is renamed at each level of the nested queries, giving an understanding of when certain aliases are created and discarded within the query. This could help lead someone to the place where a problem exists.
    The output columns produced by the query may be a subset of columns used by nested layers. Comparing the output to the nested columns of the symbol table could suggest additional columns that could be surfaced.
    How different kinds of substitution variables might be filled.
""";
        return List.of(
                new Tool(
                        "parseSql",
                        "Parses a SQL string using a specified parser endpoint. "
                                + "For large or Unicode-heavy SQL, prefer parseSqlStream.",
                        responseFormat),
                new Tool(
                        "parseSqlStream",
                        "Parses SQL sent in a second MCP frame as raw UTF-8 bytes (no JSON escaping). "
                                + "Params: endPoint, sqlByteLength. Send frame 2 with Content-Length: sqlByteLength.",
                        responseFormat));
    }

    Object handleParseSql(JsonRpcRequest request) {
        if (request.params == null || !request.params.isJsonObject()) {
            throw new IllegalArgumentException("Parameters must be a JSON object with 'sqlText' and 'endPoint'.");
        }
        JsonObject params = request.params.getAsJsonObject();
        if (!params.has("sqlText") || !params.has("endPoint")) {
            throw new IllegalArgumentException("Parameters must include 'sqlText' and 'endPoint'.");
        }
        String sqlText = SqlParseMcpSupport.normalizeSqlTextForMcp(
                SqlParseMcpSupport.extractSqlTextFromParams(params));
        String endPoint = params.get("endPoint").getAsString();
        JsonObject result = SqlParseMcpSupport.parseAndFormat(endPoint, sqlText);
        result.addProperty("transport", SqlParseMcpSupport.TRANSPORT_JSON_RPC);
        return result;
    }

    Object handleParseSqlStream(JsonRpcRequest request, InputStream in) throws IOException {
        if (request.params == null || !request.params.isJsonObject()) {
            throw new IllegalArgumentException(
                    "Parameters must be a JSON object with 'endPoint' and 'sqlByteLength'.");
        }
        JsonObject params = request.params.getAsJsonObject();
        if (!params.has("endPoint") || !params.has("sqlByteLength")) {
            throw new IllegalArgumentException("Parameters must include 'endPoint' and 'sqlByteLength'.");
        }
        String endPoint = params.get("endPoint").getAsString();
        int sqlByteLength = params.get("sqlByteLength").getAsInt();
        if (sqlByteLength < 0) {
            throw new IllegalArgumentException("sqlByteLength must be non-negative.");
        }

        byte[] sqlFrame = SqlParseMcpFraming.readContentFrame(in);
        if (sqlFrame == null) {
            throw new IOException("Expected second MCP frame with raw SQL body.");
        }
        if (sqlFrame.length != sqlByteLength) {
            throw new IllegalArgumentException(
                    "sqlByteLength mismatch: expected " + sqlByteLength + " bytes, got " + sqlFrame.length);
        }

        String charset = params.has("sqlCharset") ? params.get("sqlCharset").getAsString() : "UTF-8";
        String sqlText = SqlParseMcpSupport.normalizeSqlTextForMcp(
                new String(sqlFrame, charset));
        JsonObject result = SqlParseMcpSupport.parseAndFormat(endPoint, sqlText);
        result.addProperty("transport", "sql-stream");
        return result;
    }

    static String normalizeSqlTextForMcp(String sql) {
        return SqlParseMcpSupport.normalizeSqlTextForMcp(sql);
    }

    static String peekRequestId(String jsonPayload) {
        return SqlParseMcpSupport.peekRequestId(jsonPayload);
    }

    // --- Main Application Loop ---

    public void run() {
        run(System.in);
    }

    void run(InputStream in) {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                byte[] body = SqlParseMcpFraming.readContentFrame(in);
                if (body == null) {
                    break;
                }
                String requestIdHint = SqlParseMcpSupport.peekRequestId(body);

                JsonRpcRequest request = null;
                try {
                    request = SqlParseMcpSupport.parseJsonRpcRequest(body);
                    if (request == null || request.method == null) {
                        throw new JsonSyntaxException("Invalid request format");
                    }

                    Object result;
                    switch (request.method) {
                        case "initialize":
                            result = handleInitialize(request);
                            break;
                        case "listTools":
                            result = handleListTools(request);
                            break;
                        case "ping":
                            result = handlePing(request);
                            break;
                        case "tool/parseSql":
                            result = handleParseSql(request);
                            break;
                        case "tool/parseSqlStream":
                            result = handleParseSqlStream(request, in);
                            break;
                        default:
                            throw new UnsupportedOperationException("Method not found: " + request.method);
                    }
                    writeResponse(new JsonRpcResponse(request.id, result));

                } catch (Exception e) {
                    String requestId = resolveRequestId(request, requestIdHint);
                    int code = (e instanceof IllegalArgumentException) ? -32602 : -32603;
                    String message = e.getMessage();
                    if (message == null || message.isBlank()) {
                        message = SqlParseMcpSupport.unwrapThrowable(e).getClass().getSimpleName();
                    }
                    writeResponse(new JsonRpcErrorResponse(requestId, code, message));
                }
            }
        } catch (IOException e) {
            System.err.println("IO Error in main loop: " + e.getMessage());
        }
    }

    private static String resolveRequestId(JsonRpcRequest request, String requestIdHint) {
        if (request != null && request.id != null) {
            return request.id;
        }
        return requestIdHint;
    }

    private static void writeResponse(Object responseObject) {
        try {
            SqlParseMcpFraming.writeJsonRpcFrame(System.out, gson, responseObject);
        } catch (IOException fatal) {
            System.err.println("MCP could not write JSON-RPC response: " + fatal.getMessage());
        }
    }

    public static void main(String[] args) {
        if (args != null && args.length >= 1 && "--bulk".equals(args[0])) {
            SqlParseBulk.main(args);
            return;
        }
        new SqlParseMCP().run();
    }
}
