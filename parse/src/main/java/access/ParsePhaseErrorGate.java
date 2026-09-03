package access;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.antlr.v4.runtime.Parser;

import errorhandling.ParseDiagnostic;
import errorhandling.ParseErrorCollector;
import errorhandling.ParseErrorListener;

/**
 * Shared policy for skipping AST walks when the parse phase already failed.
 * Recovered partial parse trees mis-align walker stack state ({@code subMap} null).
 */
public final class ParsePhaseErrorGate {

    /** Emitted when AST walk is skipped because the parse phase already failed. */
    public static final String DIAG_AST_WALK_SKIPPED_DUE_TO_PARSE_ERRORS =
            "AST_WALK_SKIPPED_DUE_TO_PARSE_ERRORS";

    private ParsePhaseErrorGate() {
    }

    public static List<ParseDiagnostic> collectDiagnostics(
            ParseErrorCollector collector,
            ParseErrorListener listener) {
        List<ParseDiagnostic> diagnostics = new ArrayList<>();
        if (collector != null) {
            diagnostics.addAll(collector.getDiagnostics());
        }
        if (listener != null) {
            diagnostics.addAll(listener.getDiagnostics());
        }
        return diagnostics;
    }

    public static boolean hasParsePhaseErrors(
            Parser parser,
            ParseErrorCollector collector,
            ParseErrorListener listener) {
        if (parser != null && parser.getNumberOfSyntaxErrors() > 0) {
            return true;
        }
        return findFirstFatalOrError(collector, listener) != null;
    }

    public static ParseDiagnostic findFirstFatalOrError(
            ParseErrorCollector collector,
            ParseErrorListener listener) {
        for (ParseDiagnostic diagnostic : collectDiagnostics(collector, listener)) {
            if (diagnostic == null) {
                continue;
            }
            ParseDiagnostic.Severity severity = diagnostic.severity();
            if (severity == ParseDiagnostic.Severity.FATAL
                    || severity == ParseDiagnostic.Severity.ERROR) {
                return diagnostic;
            }
        }
        return null;
    }

    /**
     * WARNING diagnostic recorded when semantic analysis is skipped after parse-phase failure.
     *
     * @param source diagnostic {@code source} field (e.g. {@code SqlParserAccess},
     *               {@code ParseLatencyDiagnosticService})
     */
    public static ParseDiagnostic astWalkSkippedDueToParseErrors(
            ParseErrorCollector collector,
            ParseErrorListener listener,
            String source) {
        ParseDiagnostic primary = findFirstFatalOrError(collector, listener);
        Integer line = primary == null ? null : primary.line();
        Integer pos = primary == null ? null : primary.charPositionInLine();
        String parseMessage = primary == null ? null : primary.message();
        String parseCode = primary == null ? null : primary.code();

        String message = "Semantic analysis skipped because the SQL did not parse successfully.";
        if (line != null && pos != null) {
            message = String.format(
                    "Semantic analysis skipped because the SQL did not parse successfully (first parse error at line %d, position %d).",
                    line,
                    pos);
        } else if (parseMessage != null && !parseMessage.isBlank()) {
            message = "Semantic analysis skipped because the SQL did not parse successfully: " + parseMessage;
        }

        Map<String, String> details = new LinkedHashMap<>();
        if (parseCode != null && !parseCode.isBlank()) {
            details.put("parseErrorCode", parseCode);
        }
        if (parseMessage != null && !parseMessage.isBlank()) {
            details.put("parseErrorMessage", parseMessage);
        }

        return new ParseDiagnostic(
                ParseDiagnostic.Severity.WARNING,
                DIAG_AST_WALK_SKIPPED_DUE_TO_PARSE_ERRORS,
                message,
                line,
                pos,
                source,
                null,
                primary == null ? null : primary.tokenText(),
                true,
                "ast-walk",
                null,
                details.isEmpty() ? null : details);
    }
}
