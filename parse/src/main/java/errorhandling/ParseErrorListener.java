package errorhandling;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Map;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.atn.ATNConfigSet;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.Interval;
import org.antlr.v4.runtime.misc.Utils;

/*
 * Class implements 
 */
public class ParseErrorListener extends BaseErrorListener
{
    private final List<ParseDiagnostic> diagnostics = new ArrayList<>();

    private boolean showAmbiguities = false;
    private boolean showFullContext = false;
    private boolean showContextSensitivity = false;

    public ParseErrorListener()
    {
    }

    public ParseErrorListener(boolean showAmbiguities, boolean showFullContext, boolean showContextSensitivity)
    {
        this.showAmbiguities = showAmbiguities;
        this.showFullContext = showFullContext;
        this.showContextSensitivity = showContextSensitivity;
    }

    public void setShowAmbiguities(boolean showAmbiguities)
    {
        this.showAmbiguities = showAmbiguities;
    }
    public void setShowFullContext(boolean showFullContext)
    {
        this.showFullContext = showFullContext;
    }
    public void setShowContextSensitivity(boolean showContextSensitivity)
    {
        this.showContextSensitivity = showContextSensitivity;
    }

    public List<ParseDiagnostic> getDiagnostics() {
        return diagnostics;
    }

    private void addDiagnostic(ParseDiagnostic.Severity severity, String code, String message,
                       Integer line, Integer pos, String ruleName, String tokenText,
                       boolean recoverable, String exceptionType) {
        addDiagnostic(severity, code, message, line, pos, ruleName, tokenText, recoverable, exceptionType, null);
    }

    private void addDiagnostic(ParseDiagnostic.Severity severity, String code, String message,
                       Integer line, Integer pos, String ruleName, String tokenText,
                       boolean recoverable, String exceptionType, Map<String, String> details) {
        diagnostics.add(new ParseDiagnostic(
                severity, code, message, line, pos,
            "ParseErrorListener", ruleName, tokenText,
            recoverable, "parse.listener", exceptionType, details
        ));
    }

    private Token safeTokenAt(Parser recognizer, int index) {
        try {
            TokenStream ts = recognizer.getInputStream();
            if (ts == null) return null;
            return ts.get(index);
        } catch (Exception ex) {
            return null;
        }
    }

    private String currentRuleName(Parser recognizer) {
        List<String> stack = recognizer.getRuleInvocationStack();
        return (stack == null || stack.isEmpty()) ? null : stack.get(0);
    }

    @Override
    public void reportAmbiguity(Parser recognizer,
                            DFA dfa,
                            int startIndex,
                            int stopIndex,
                            boolean exact,
                            BitSet ambigAlts,
                            ATNConfigSet configs)
    {
        if (!showAmbiguities) return;

        TokenStream tokens = recognizer.getInputStream();
        String text = (tokens == null) ? null : tokens.getText(Interval.of(startIndex, stopIndex));
        String msg = "Ambiguity" + (exact ? " (exact)" : "") + ": " + Utils.escapeWhitespace(text, false);

        Token token = safeTokenAt(recognizer, startIndex);
        int line = token != null ? token.getLine() : -1;
        int pos  = token != null ? token.getCharPositionInLine() : -1;

        addDiagnostic(
            ParseDiagnostic.Severity.WARNING,
            "AMBIGUITY",
            msg,
            line >= 0 ? line : null,
            pos >= 0 ? pos : null,
            currentRuleName(recognizer),
            token != null ? token.getText() : null,
            true,
            null
        );
    }

    @Override
    public void reportAttemptingFullContext(Parser recognizer,
                                        DFA dfa,
                                        int startIndex,
                                        int stopIndex,
                                        BitSet conflictingAlts,
                                        ATNConfigSet configs)
    {
        if (!showFullContext) return;

        TokenStream tokens = recognizer.getInputStream();
        String text = (tokens == null) ? null : tokens.getText(Interval.of(startIndex, stopIndex));
        String msg = "Attempting full context: " + Utils.escapeWhitespace(text, false);

        Token token = safeTokenAt(recognizer, startIndex);
        int line = token != null ? token.getLine() : -1;
        int pos  = token != null ? token.getCharPositionInLine() : -1;

        addDiagnostic(
            ParseDiagnostic.Severity.WARNING,
            "FULL_CONTEXT",
            msg,
            line >= 0 ? line : null,
            pos >= 0 ? pos : null,
            currentRuleName(recognizer),
            token != null ? token.getText() : null,
            true,
            null
        );
    }

    @Override
    public void reportContextSensitivity(Parser recognizer,
                                     DFA dfa,
                                     int startIndex,
                                     int stopIndex,
                                     int prediction,
                                     ATNConfigSet configs)
    {
        if (!showContextSensitivity) return;

        TokenStream tokens = recognizer.getInputStream();
        String text = (tokens == null) ? null : tokens.getText(Interval.of(startIndex, stopIndex));
        String msg = "Context sensitivity: " + Utils.escapeWhitespace(text, false);

        Token token = safeTokenAt(recognizer, startIndex);
        int line = token != null ? token.getLine() : -1;
        int pos  = token != null ? token.getCharPositionInLine() : -1;

        addDiagnostic(
            ParseDiagnostic.Severity.WARNING,
            "CONTEXT_SENSITIVITY",
            msg,
            line >= 0 ? line : null,
            pos >= 0 ? pos : null,
            currentRuleName(recognizer),
            token != null ? token.getText() : null,
            true,
            null
        );
    }

    @Override
    public void syntaxError(Recognizer<?, ?> recognizer,
                            Object offendingSymbol,
                            int line, int charPositionInLine,
                            String msg, RecognitionException e)
    {
        // ANTLR's raw `msg` is omitted: the diagnostic message is built from location,
        // token, snippet, and parser rule so REPORT_ERROR / SYNTAX_ERROR stay short and stable.
        Parser parser = recognizer instanceof Parser p ? p : null;
        Token token = offendingSymbol instanceof Token t ? t : null;
        ParseSyntaxErrorContext context = ParseSyntaxErrorContext.capture(parser, token);
        String errorMessage = context.formatFatalMessage();
        String tokenText = token == null
                ? (offendingSymbol == null ? null : offendingSymbol.toString())
                : token.getText();
        addDiagnostic(ParseDiagnostic.Severity.FATAL, "SYNTAX_ERROR", errorMessage,
            line, charPositionInLine, context.parserRule(), tokenText, false,
            e == null ? null : e.getClass().getSimpleName(),
            context.toDetails());
    }

    @Override
    public String toString()
    {
        return Utils.join(diagnostics.iterator(), "\n");
    }
}