package errorhandling;

import java.util.Map;

/*
 * ParseDiagnostic is a record that represents a diagnostic message generated during parsing and validation.
 * It contains information about the severity of the diagnostic, a code to identify the type of issue,
 * a human-readable message, the line and character position where the issue occurred, the source of the diagnostic,
 * and optional fields for the rule name and token text associated with the issue.
 *
 * <p>For parse-strategy {@code REPORT_ERROR} and listener {@code SYNTAX_ERROR} fatals,
 * {@code ruleName} is the innermost ANTLR production and {@code details} may include
 * {@code contextSnippet}, {@code syntaxClass}, and {@code parserRules}.
 */
public record ParseDiagnostic(
        Severity severity,
        String code,
        String message,
        Integer line,
        Integer charPositionInLine,
        String source,      // ParseErrorListener / ParseErrorCollector
        String ruleName,    // optional
        String tokenText,    // optional
        boolean recoverable,
        String phase,
        String exceptionType,
        Map<String, String> details
) {
    public enum Severity { FATAL, ERROR, SEVERE_WARNING, WARNING, INFO }
}