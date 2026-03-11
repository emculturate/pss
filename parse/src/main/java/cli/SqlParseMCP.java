package cli;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import access.Snippet;
import access.SqlParserAccess;
import errorhandling.ParseDiagnostic;

public class SqlParseMCP {

    private static final Gson gson = new GsonBuilder().serializeNulls().create();

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
        // For now, just return the list of tools as capabilities.
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
        return Collections.singletonList(
            new Tool(
                "parseSql",
                "Parses a SQL string using a specified parser endpoint.",
                responseFormat
            )
        );
    }

    public Object handleParseSql(JsonRpcRequest request) {
        if (request.params == null || !request.params.isJsonObject()) {
            throw new IllegalArgumentException("Parameters must be a JSON object with 'sqlText' and 'endPoint'.");
        }
        JsonObject params = request.params.getAsJsonObject();
        if (!params.has("sqlText") || !params.has("endPoint")) {
            throw new IllegalArgumentException("Parameters must include 'sqlText' and 'endPoint'.");
        }
        String sqlText = params.get("sqlText").getAsString();
        String endPoint = params.get("endPoint").getAsString();

        try {
            SqlParserAccess access = new SqlParserAccess(false, false, false);
            access.executeTheParse(sqlText, endPoint);
            Snippet snippet = access.getSnippet();
            return formatParseResult(snippet);
        } catch (Exception e) {
            // If the parser throws, return a result object with error info
            JsonObject result = new JsonObject();
            result.addProperty("ok", false);
            result.addProperty("hasFatalErrors", true);
            ParseDiagnostic exceptionDiagnostic = new ParseDiagnostic(
                    ParseDiagnostic.Severity.FATAL,
                    "MCP_INTERNAL_ERROR",
                    e.getMessage(),
                    null,
                    null,
                    "SqlParseMCP",
                    null,
                    null,
                    false,
                    "mcp.response",
                    e.getClass().getSimpleName(),
                    null);
            result.addProperty("fatalErrorCount", 1);
            result.add("errors", diagnosticsToJsonArray(List.of(exceptionDiagnostic)));
            result.add("messages", diagnosticsToJsonArray(List.of(exceptionDiagnostic)));
            return result;
        }
    }

    private JsonArray diagnosticsToJsonArray(List<ParseDiagnostic> diagnostics) {
        JsonArray diagnosticsArray = new JsonArray();
        if (diagnostics == null) {
            return diagnosticsArray;
        }

        for (ParseDiagnostic diagnostic : diagnostics) {
            if (diagnostic == null) {
                continue;
            }
            JsonObject diagnosticJson = new JsonObject();
            addNullableString(diagnosticJson, "severity",
                    diagnostic.severity() == null ? null : diagnostic.severity().name());
            addNullableString(diagnosticJson, "code", diagnostic.code());
            addNullableString(diagnosticJson, "message", diagnostic.message());
            diagnosticJson.add("line", diagnostic.line() == null ? JsonNull.INSTANCE : gson.toJsonTree(diagnostic.line()));
            diagnosticJson.add("charPositionInLine",
                    diagnostic.charPositionInLine() == null ? JsonNull.INSTANCE : gson.toJsonTree(diagnostic.charPositionInLine()));
            addNullableString(diagnosticJson, "source", diagnostic.source());
            addNullableString(diagnosticJson, "ruleName", diagnostic.ruleName());
            addNullableString(diagnosticJson, "tokenText", diagnostic.tokenText());
            diagnosticJson.addProperty("recoverable", diagnostic.recoverable());
            addNullableString(diagnosticJson, "phase", diagnostic.phase());
            addNullableString(diagnosticJson, "exceptionType", diagnostic.exceptionType());
            diagnosticJson.add("details", diagnostic.details() == null ? JsonNull.INSTANCE : gson.toJsonTree(diagnostic.details()));
            diagnosticsArray.add(diagnosticJson);
        }
        return diagnosticsArray;
    }

    private void addNullableString(JsonObject object, String propertyName, String value) {
        object.add(propertyName, value == null ? JsonNull.INSTANCE : gson.toJsonTree(value));
    }

    private boolean isParserSource(String source) {
        return "ParseErrorCollector".equals(source)
                || "ParseErrorListener".equals(source)
                || "SqlParseMCP".equals(source);
    }

    private boolean isSqlAstWalkerSource(String source) {
        return source != null && source.contains("SqlASTWalker");
    }

    private JsonObject buildParsePayload(Snippet snippet) {
        JsonObject parseResult = new JsonObject();

        JsonElement jstr = pruneNulls(gson.toJsonTree(snippet.getSqlAbstractTree()));
        parseResult.add("sqlTree", jstr);
        jstr = pruneNulls(gson.toJsonTree(snippet.getSymbolTable()));
        parseResult.add("symbolTable", jstr);
        jstr = pruneNulls(gson.toJsonTree(snippet.getTableDictionary()));
        parseResult.add("tableDictionary", jstr);
        jstr = pruneNulls(gson.toJsonTree(snippet.getQueryColumnDictionaryMap()));
        parseResult.add("queryDictionary", jstr);
        jstr = pruneNulls(gson.toJsonTree(snippet.getSubstitutionsMap()));
        parseResult.add("substitutionsMap", jstr);
        jstr = pruneNulls(gson.toJsonTree(snippet.getQueryInterface()));
        parseResult.add("queryInterface", jstr);

        return parseResult;
    }

    private ParseDiagnostic normalizeDiagnosticForOutput(ParseDiagnostic diagnostic) {
        if (diagnostic == null) {
            return null;
        }

        ParseDiagnostic.Severity normalizedSeverity = diagnostic.severity();
        String source = diagnostic.source();

        // Parser/collector/listener ERROR diagnostics are advisory for MCP consumers.
        if (isParserSource(source) && normalizedSeverity == ParseDiagnostic.Severity.ERROR) {
            normalizedSeverity = ParseDiagnostic.Severity.WARNING;
        }

        // SQL AST walker ERROR diagnostics are probable, not confirmed fatal.
        if (isSqlAstWalkerSource(source)
            && normalizedSeverity == ParseDiagnostic.Severity.ERROR) {
            normalizedSeverity = ParseDiagnostic.Severity.WARNING;
        }

        if (normalizedSeverity == diagnostic.severity()) {
            return diagnostic;
        }

        return new ParseDiagnostic(
                normalizedSeverity,
                diagnostic.code(),
                diagnostic.message(),
                diagnostic.line(),
                diagnostic.charPositionInLine(),
                diagnostic.source(),
                diagnostic.ruleName(),
                diagnostic.tokenText(),
                diagnostic.recoverable(),
                diagnostic.phase(),
                diagnostic.exceptionType(),
                diagnostic.details());
    }

    private List<ParseDiagnostic> normalizeDiagnosticsForOutput(List<ParseDiagnostic> diagnostics) {
        if (diagnostics == null || diagnostics.isEmpty()) {
            return List.of();
        }
        return diagnostics.stream()
                .map(this::normalizeDiagnosticForOutput)
                .filter(diagnostic -> diagnostic != null)
                .collect(Collectors.toList());
    }

    private JsonElement pruneNulls(JsonElement element) {
        if (element == null || element.isJsonNull()) {
            return JsonNull.INSTANCE;
        }
        if (element.isJsonArray()) {
            JsonArray source = element.getAsJsonArray();
            JsonArray prunedArray = new JsonArray();
            for (JsonElement item : source) {
                JsonElement prunedItem = pruneNulls(item);
                if (!prunedItem.isJsonNull()) {
                    prunedArray.add(prunedItem);
                }
            }
            return prunedArray;
        }
        if (element.isJsonObject()) {
            JsonObject source = element.getAsJsonObject();
            JsonObject prunedObject = new JsonObject();
            for (String key : source.keySet()) {
                JsonElement value = pruneNulls(source.get(key));
                if (!value.isJsonNull()) {
                    prunedObject.add(key, value);
                }
            }
            return prunedObject;
        }
        return element;
    }

    private JsonObject formatParseResult(Snippet snippet) {
        JsonObject result = new JsonObject();

        /*
        Add all fields from Snippet:
        tableDictionary=" + tableDictionary
        + ", symbolTable=" + symbolTable + ", substitutionsMap=" + substitutionsMap + ", queryInterface="
        + queryInterface + ", parserMessageList=" + parserMessageList + ", parserMessageStringList="
        + parserMessageStringList + ", fatalErrorCount=" + fatalErrorCount + ", fatalErrorStringList="
        + fatalErrorStringList
         */
        List<ParseDiagnostic> normalizedDiagnostics = collectNormalizedDiagnosticsForOutput(snippet);
        List<ParseDiagnostic> fatalDiagnostics = normalizedDiagnostics.stream()
                .filter(diagnostic -> diagnostic != null && diagnostic.severity() == ParseDiagnostic.Severity.FATAL)
            .collect(Collectors.toList());
        List<ParseDiagnostic> nonFatalDiagnostics = normalizedDiagnostics.stream()
                .filter(diagnostic -> diagnostic != null && diagnostic.severity() != ParseDiagnostic.Severity.FATAL)
            .collect(Collectors.toList());

        if (!fatalDiagnostics.isEmpty()) { // There are Fatal Errors
            boolean hasParserFatal = fatalDiagnostics.stream()
                    .anyMatch(diagnostic -> diagnostic != null && isParserSource(diagnostic.source()));
            boolean hasSqlAstWalkerFatal = fatalDiagnostics.stream()
                    .anyMatch(diagnostic -> diagnostic != null && isSqlAstWalkerSource(diagnostic.source()));
            boolean hasUnknownFatal = fatalDiagnostics.stream()
                    .anyMatch(diagnostic -> diagnostic != null
                            && !isParserSource(diagnostic.source())
                            && !isSqlAstWalkerSource(diagnostic.source()));

            result.addProperty("hasFatalErrors", true);
            result.addProperty("ok", false);
            result.addProperty("fatalErrorCount", fatalDiagnostics.size());
            JsonElement jstr = diagnosticsToJsonArray(fatalDiagnostics);
            result.add("errors", jstr);
            result.add("messages", diagnosticsToJsonArray(nonFatalDiagnostics));

            // If fatal diagnostics are from SQL AST walking only, include partial parse artifacts.
            if (hasSqlAstWalkerFatal && !hasParserFatal && !hasUnknownFatal) {
                result.addProperty("parsePartial", true);
                result.add("parse", buildParsePayload(snippet));
            }
        } else { // There are no Fatal Errors
            result.addProperty("hasFatalErrors", false);
            result.addProperty("ok", true);

            result.add("parse", buildParsePayload(snippet));

            JsonElement jstr = diagnosticsToJsonArray(nonFatalDiagnostics);
            result.add("messages",jstr);
 
        }
        return result;
    }

    private List<ParseDiagnostic> collectNormalizedDiagnosticsForOutput(Snippet snippet) {
        if (snippet == null) {
            return List.of();
        }

        Set<ParseDiagnostic> mergedDiagnostics = new LinkedHashSet<>();
        List<ParseDiagnostic> parserDiagnostics = snippet.getParserDiagnosticList();
        List<ParseDiagnostic> parserMessages = snippet.getParserMessageList();

        if (parserDiagnostics != null) {
            mergedDiagnostics.addAll(parserDiagnostics);
        }
        if (parserMessages != null) {
            mergedDiagnostics.addAll(parserMessages);
        }

        if (mergedDiagnostics.isEmpty()) {
            return List.of();
        }

        return normalizeDiagnosticsForOutput(new ArrayList<>(mergedDiagnostics));
    }

    // --- Main Application Loop ---

    public void run() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            while (!Thread.currentThread().isInterrupted()) {
                // Read headers to find Content-Length
                String line = reader.readLine();
                if (line == null) break; // End of stream

                int contentLength = -1;
                while (line != null && !line.isEmpty()) {
                    if (line.startsWith("Content-Length: ")) {
                        contentLength = Integer.parseInt(line.substring(16).trim());
                    }
                    line = reader.readLine();
                }

                if (contentLength == -1) continue;

                // Read the JSON payload
                char[] buffer = new char[contentLength];
                reader.read(buffer, 0, contentLength);
                String jsonPayload = new String(buffer);

                JsonRpcRequest request = null;
                try {
                    request = gson.fromJson(jsonPayload, JsonRpcRequest.class);
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
                        case "tool/parseSql": // MCP tool invocation convention
                            result = handleParseSql(request);
                            break;
                        default:
                            throw new UnsupportedOperationException("Method not found: " + request.method);
                    }
                    writeResponse(new JsonRpcResponse(request.id, result));

                } catch (Exception e) {
                    String requestId = (request != null) ? request.id : null;
                    // JSON-RPC error codes: -32600 Invalid Request, -32602 Invalid Params, -32603 Internal error
                    int code = (e instanceof IllegalArgumentException) ? -32602 : -32603;
                    writeResponse(new JsonRpcErrorResponse(requestId, code, e.getMessage()));
                }
            }
        } catch (IOException e) {
            // Cannot write error response if stdout is closed, so print to stderr
            System.err.println("IO Error in main loop: " + e.getMessage());
        }
    }

    private static void writeResponse(Object responseObject) {
        String jsonResponse = gson.toJson(responseObject);
        int contentLength = jsonResponse.getBytes().length;
        System.out.print("Content-Length: " + contentLength + "\r\n\r\n");
        System.out.print(jsonResponse);
        System.out.flush(); // <-- This is crucial!
    }

    public static void main(String[] args) {
        SqlParseMCP service = new SqlParseMCP();
        service.run();
    }
}
