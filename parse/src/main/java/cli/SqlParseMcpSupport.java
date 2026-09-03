package cli;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

import access.Snippet;
import access.SqlParserAccess;
import errorhandling.ParseDiagnostic;
import errorhandling.ParseSyntaxErrorContext;

/**
 * Shared parse pipeline and MCP result envelope builder. Uses {@link SqlParserAccess} directly
 * (no reflection) so bulk stdin and JSON-RPC transports share one code path.
 */
final class SqlParseMcpSupport {

    static final String MCP_RESPONSE_SERIALIZE_ERROR = "MCP_RESPONSE_SERIALIZE_ERROR";
    static final String TRANSPORT_BULK = "sql-bulk";
    static final String TRANSPORT_JSON_RPC = "json-rpc";

    private static final Gson GSON = new GsonBuilder().serializeNulls().create();

    private SqlParseMcpSupport() {
    }

    static Gson gson() {
        return GSON;
    }

    static JsonObject parseAndFormat(String endPoint, String sqlText) {
        try {
            SqlParserAccess access = new SqlParserAccess(false, false, false);
            access.executeTheParse(sqlText, endPoint);
            return formatParseResultSafely(access.getSnippet());
        } catch (Exception e) {
            return buildMcpInternalErrorResult(e);
        }
    }

    static JsonObject parseAndFormatWithTransport(String endPoint, String sqlText, String transport) {
        JsonObject result = parseAndFormat(endPoint, sqlText);
        result.addProperty("transport", transport);
        return result;
    }

    /**
     * Replace characters that break Gson request deserialization or the lexer:
     * Unicode whitespace → ASCII space; Unicode dashes → '-'; strip U+FE0F variation selectors.
     */
    static String normalizeSqlTextForMcp(String sql) {
        if (sql == null || sql.isEmpty()) {
            return sql;
        }
        StringBuilder normalized = new StringBuilder(sql.length());
        for (int index = 0; index < sql.length(); index++) {
            char ch = sql.charAt(index);
            if (ch == '\uFE0F') {
                continue;
            }
            if (isProblematicWhitespace(ch)) {
                normalized.append(' ');
            } else if (isUnicodeDash(ch)) {
                normalized.append('-');
            } else {
                normalized.append(ch);
            }
        }
        return normalized.toString();
    }

    static String extractSqlTextFromParams(JsonObject params) {
        if (params.has("sqlTextEncoding")
                && "base64".equalsIgnoreCase(params.get("sqlTextEncoding").getAsString())) {
            byte[] decoded = Base64.getDecoder().decode(params.get("sqlText").getAsString());
            return new String(decoded, StandardCharsets.UTF_8);
        }
        return params.get("sqlText").getAsString();
    }

    /** Best-effort id extraction so error responses correlate even when Gson request parse fails. */
    static String peekRequestId(byte[] jsonPayload) {
        if (jsonPayload == null || jsonPayload.length == 0) {
            return null;
        }
        return peekRequestId(new String(jsonPayload, StandardCharsets.UTF_8));
    }

    static String peekRequestId(String jsonPayload) {
        if (jsonPayload == null || jsonPayload.isBlank()) {
            return null;
        }
        try {
            JsonElement root = JsonParser.parseString(jsonPayload);
            if (!root.isJsonObject()) {
                return null;
            }
            JsonElement idElement = root.getAsJsonObject().get("id");
            if (idElement == null || idElement.isJsonNull()) {
                return null;
            }
            if (idElement.isJsonPrimitive()) {
                return idElement.getAsString();
            }
            return idElement.toString();
        } catch (RuntimeException ignored) {
            return null;
        }
    }

    static SqlParseMCP.JsonRpcRequest parseJsonRpcRequest(byte[] body) {
        JsonReader reader = new JsonReader(
                new InputStreamReader(new ByteArrayInputStream(body), StandardCharsets.UTF_8));
        reader.setLenient(true);
        return GSON.fromJson(reader, SqlParseMCP.JsonRpcRequest.class);
    }

    static JsonObject buildMcpInternalErrorResult(Throwable error) {
        Throwable root = unwrapThrowable(error);
        String message = root.getMessage();
        if (message == null || message.isBlank()) {
            message = root.getClass().getSimpleName();
        }
        JsonObject result = new JsonObject();
        result.addProperty("ok", false);
        result.addProperty("hasFatalErrors", true);
        ParseDiagnostic exceptionDiagnostic = new ParseDiagnostic(
                ParseDiagnostic.Severity.FATAL,
                "MCP_INTERNAL_ERROR",
                message,
                null,
                null,
                "SqlParseMCP",
                null,
                null,
                false,
                "mcp.response",
                root.getClass().getSimpleName(),
                null);
        result.addProperty("fatalErrorCount", 1);
        result.add("errors", diagnosticsToJsonArray(List.of(exceptionDiagnostic)));
        result.add("messages", diagnosticsToJsonArray(List.of(exceptionDiagnostic)));
        return result;
    }

    static Throwable unwrapThrowable(Throwable error) {
        Throwable current = error;
        while (current instanceof java.lang.reflect.InvocationTargetException
                && current.getCause() != null) {
            current = current.getCause();
        }
        return current;
    }

    static JsonObject formatParseResult(Snippet snippet) {
        JsonObject result = new JsonObject();

        List<ParseDiagnostic> normalizedDiagnostics = collectNormalizedDiagnosticsForOutput(snippet);
        List<ParseDiagnostic> fatalDiagnostics = normalizedDiagnostics.stream()
                .filter(diagnostic -> diagnostic != null && diagnostic.severity() == ParseDiagnostic.Severity.FATAL)
                .collect(Collectors.toList());
        List<ParseDiagnostic> nonFatalDiagnostics = normalizedDiagnostics.stream()
                .filter(diagnostic -> diagnostic != null && diagnostic.severity() != ParseDiagnostic.Severity.FATAL)
                .collect(Collectors.toList());
        List<ParseDiagnostic> externalNonFatalDiagnostics = fatalDiagnostics.isEmpty()
                ? filterExternalMessagesForNoFatal(nonFatalDiagnostics)
                : nonFatalDiagnostics;

        if (!fatalDiagnostics.isEmpty()) {
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
            result.add("errors", diagnosticsToJsonArray(fatalDiagnostics));
            result.add("messages", diagnosticsToJsonArray(externalNonFatalDiagnostics));

            if (hasSqlAstWalkerFatal && !hasParserFatal && !hasUnknownFatal) {
                result.addProperty("parsePartial", true);
                result.add("parse", buildParsePayload(snippet));
            }
        } else {
            result.addProperty("hasFatalErrors", false);
            result.addProperty("ok", true);
            result.add("parse", buildParsePayload(snippet));
            result.add("messages", diagnosticsToJsonArray(externalNonFatalDiagnostics));
        }
        return result;
    }

    private static JsonObject formatParseResultSafely(Snippet snippet) {
        try {
            return formatParseResult(snippet);
        } catch (Exception serializeError) {
            JsonObject result = new JsonObject();
            result.addProperty("ok", false);
            result.addProperty("hasFatalErrors", true);
            result.addProperty("parsePartial", true);
            result.addProperty("fatalErrorCount", 1);
            Throwable root = unwrapThrowable(serializeError);
            String message = root.getMessage();
            if (message == null || message.isBlank()) {
                message = root.getClass().getSimpleName();
            }
            ParseDiagnostic diagnostic = new ParseDiagnostic(
                    ParseDiagnostic.Severity.FATAL,
                    MCP_RESPONSE_SERIALIZE_ERROR,
                    "Parse completed but MCP could not serialize the full response: " + message,
                    null,
                    null,
                    "SqlParseMCP",
                    null,
                    null,
                    false,
                    "mcp.response",
                    root.getClass().getSimpleName(),
                    null);
            result.add("errors", diagnosticsToJsonArray(List.of(diagnostic)));
            result.add("messages", diagnosticsToJsonArray(List.of()));
            JsonObject partialParse = new JsonObject();
            addParsePayloadFieldSafely(partialParse, "fatalErrorCount", snippet == null ? null : snippet.getFatalErrorCount());
            if (snippet != null) {
                addParsePayloadFieldSafely(partialParse, "fatalErrorStringList", snippet.getFatalErrorStringList());
            }
            result.add("parse", partialParse);
            return result;
        }
    }

    private static void addParsePayloadFieldSafely(JsonObject parseResult, String fieldName, Object value) {
        try {
            parseResult.add(fieldName, pruneNulls(GSON.toJsonTree(value)));
        } catch (RuntimeException ignored) {
            parseResult.add(fieldName, JsonNull.INSTANCE);
        }
    }

    private static boolean isProblematicWhitespace(char ch) {
        if (ch == '\u00A0' || ch == '\uFEFF') {
            return true;
        }
        if (ch >= '\u2000' && ch <= '\u200B') {
            return true;
        }
        return ch == '\u202F' || ch == '\u205F' || ch == '\u3000';
    }

    private static boolean isUnicodeDash(char ch) {
        return (ch >= '\u2010' && ch <= '\u2015') || ch == '\u2212';
    }

    private static JsonArray diagnosticsToJsonArray(List<ParseDiagnostic> diagnostics) {
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
            diagnosticJson.add("line", diagnostic.line() == null ? JsonNull.INSTANCE : GSON.toJsonTree(diagnostic.line()));
            diagnosticJson.add("charPositionInLine",
                    diagnostic.charPositionInLine() == null ? JsonNull.INSTANCE : GSON.toJsonTree(diagnostic.charPositionInLine()));
            addNullableString(diagnosticJson, "source", diagnostic.source());
            addNullableString(diagnosticJson, "ruleName", diagnostic.ruleName());
            addNullableString(diagnosticJson, "tokenText", diagnostic.tokenText());
            diagnosticJson.addProperty("recoverable", diagnostic.recoverable());
            addNullableString(diagnosticJson, "phase", diagnostic.phase());
            addNullableString(diagnosticJson, "exceptionType", diagnostic.exceptionType());
            addSyntaxErrorContextFields(diagnosticJson, diagnostic);
            diagnosticJson.add("details", diagnostic.details() == null ? JsonNull.INSTANCE : GSON.toJsonTree(diagnostic.details()));
            diagnosticsArray.add(diagnosticJson);
        }
        return diagnosticsArray;
    }

    private static void addNullableString(JsonObject object, String propertyName, String value) {
        object.add(propertyName, value == null ? JsonNull.INSTANCE : GSON.toJsonTree(value));
    }

    private static void addSyntaxErrorContextFields(JsonObject diagnosticJson, ParseDiagnostic diagnostic) {
        Map<String, String> details = diagnostic.details();
        if (details == null || details.isEmpty()) {
            return;
        }
        boolean hasSyntaxContext = details.containsKey(ParseSyntaxErrorContext.DETAIL_CONTEXT_SNIPPET)
                || details.containsKey(ParseSyntaxErrorContext.DETAIL_SYNTAX_CLASS);
        if (!hasSyntaxContext) {
            return;
        }
        String contextSnippet = details.get(ParseSyntaxErrorContext.DETAIL_CONTEXT_SNIPPET);
        if (contextSnippet != null) {
            diagnosticJson.addProperty("contextSnippet", contextSnippet);
        }
        String syntaxClass = details.get(ParseSyntaxErrorContext.DETAIL_SYNTAX_CLASS);
        if (syntaxClass != null) {
            diagnosticJson.addProperty("syntaxClass", syntaxClass);
        }
        if (diagnostic.ruleName() != null) {
            diagnosticJson.addProperty("parserRule", diagnostic.ruleName());
        }
    }

    private static boolean isParserSource(String source) {
        return "ParseErrorCollector".equals(source)
                || "ParseErrorListener".equals(source)
                || "SqlParseMCP".equals(source);
    }

    private static boolean isSqlAstWalkerSource(String source) {
        return source != null && source.contains("SqlASTWalker");
    }

    private static boolean isRecoverableParserPredictionWarning(ParseDiagnostic diagnostic) {
        if (diagnostic == null) {
            return false;
        }

        if (!"ParseErrorListener".equals(diagnostic.source()) || !diagnostic.recoverable()) {
            return false;
        }

        String code = diagnostic.code();
        return "AMBIGUITY".equals(code)
                || "FULL_CONTEXT".equals(code)
                || "CONTEXT_SENSITIVITY".equals(code);
    }

    private static List<ParseDiagnostic> filterExternalMessagesForNoFatal(List<ParseDiagnostic> diagnostics) {
        if (diagnostics == null || diagnostics.isEmpty()) {
            return List.of();
        }

        return diagnostics.stream()
                .filter(diagnostic -> !isRecoverableParserPredictionWarning(diagnostic))
                .collect(Collectors.toList());
    }

    private static JsonObject buildParsePayload(Snippet snippet) {
        JsonObject parseResult = new JsonObject();
        addParsePayloadFieldSafely(parseResult, "sqlTree", snippet.getSqlAbstractTree());
        addParsePayloadFieldSafely(parseResult, "symbolTable", snippet.getSymbolTable());
        addParsePayloadFieldSafely(parseResult, "tableDictionary", snippet.getTableDictionary());
        addParsePayloadFieldSafely(parseResult, "queryDictionary", snippet.getQueryColumnDictionaryMap());
        addParsePayloadFieldSafely(parseResult, "substitutionsMap", snippet.getSubstitutionsMap());
        addParsePayloadFieldSafely(parseResult, "queryInterface", snippet.getQueryInterface());
        if (snippet.getArrayOutputCollectorsMap() != null
                && !snippet.getArrayOutputCollectorsMap().isEmpty()) {
            addParsePayloadFieldSafely(parseResult, "arrayOutputCollectors", snippet.getArrayOutputCollectorsMap());
        }
        return parseResult;
    }

    private static ParseDiagnostic normalizeDiagnosticForOutput(ParseDiagnostic diagnostic) {
        if (diagnostic == null) {
            return null;
        }

        ParseDiagnostic.Severity normalizedSeverity = diagnostic.severity();
        String source = diagnostic.source();

        if (isParserSource(source) && normalizedSeverity == ParseDiagnostic.Severity.ERROR) {
            normalizedSeverity = ParseDiagnostic.Severity.WARNING;
        }

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

    private static List<ParseDiagnostic> normalizeDiagnosticsForOutput(List<ParseDiagnostic> diagnostics) {
        if (diagnostics == null || diagnostics.isEmpty()) {
            return List.of();
        }
        return diagnostics.stream()
                .map(SqlParseMcpSupport::normalizeDiagnosticForOutput)
                .filter(diagnostic -> diagnostic != null)
                .collect(Collectors.toList());
    }

    private static JsonElement pruneNulls(JsonElement element) {
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

    private static List<ParseDiagnostic> collectNormalizedDiagnosticsForOutput(Snippet snippet) {
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
}
