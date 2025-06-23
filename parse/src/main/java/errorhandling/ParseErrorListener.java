package errorhandling;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

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
public class ParseErrorListener  extends BaseErrorListener
{
    private final List<SyntaxError> syntaxErrors = new ArrayList<>();

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

    public List<SyntaxError> getSyntaxErrors()
    {
        return syntaxErrors;
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
        if (!showAmbiguities) {
            return; // Skip if we are not showing ambiguities
        }

    // Get input stream and extract the ambiguous text
        TokenStream tokens = recognizer.getInputStream();
        String input = tokens.getText(new Interval(startIndex, stopIndex));
    
    // Get information about the current rule
        String ruleName = "unknown";
        try {
            int ruleIndex = recognizer.getContext().getRuleIndex();
            if (ruleIndex >= 0 && ruleIndex < recognizer.getRuleNames().length) {
                ruleName = recognizer.getRuleNames()[ruleIndex];
            }
        } catch (Exception ex) {
    // Fallback if we can't get the rule name
            ruleName = "unknown_rule";
        }

    // Extract current token where ambiguity was detected
        Token ambiguousToken = tokens.get(startIndex);
        int line = ambiguousToken.getLine();
        int position = ambiguousToken.getCharPositionInLine();
    
    // Convert ambiguous alternatives to readable format
        List<Integer> alternativesList = new ArrayList<>();
        for (int i = 0; i < ambigAlts.size(); i++) {
            if (ambigAlts.get(i)) {
                alternativesList.add(i);
            }
        }
    
        String message = String.format(
            "Ambiguity detected at line %d:%d while parsing '%s' in rule '%s'\n" +
            "Ambiguous alternatives: %s\n" +
            "Input text causing ambiguity: '%s'",
            line, position, 
            ambiguousToken.getText(),
            ruleName,
            alternativesList,
            input
        );
    
        SyntaxError error = new SyntaxError(
            recognizer, 
            ambiguousToken,
            line,
            position,
            message,
            null
        );
    
        syntaxErrors.add(error);
    
    // Optionally print the ambiguity details immediately
        System.out.println("AMBIGUITY DETECTED: " + message);
	}

    @Override
    public void reportAttemptingFullContext(Parser recognizer,
                                      DFA dfa,
                                      int startIndex,
                                      int stopIndex,
                                      BitSet conflictingAlts,
                                      ATNConfigSet configs) {
       if (!showFullContext) {
            return; // Skip if we are not showing Full Context findings
        }
    // Get information about where this happened
        TokenStream tokens = recognizer.getInputStream();
        String input = tokens.getText(new Interval(startIndex, stopIndex));
        Token token = tokens.get(startIndex);
    
        String message = String.format(
            "Full context parsing required at line %d:%d for input '%s' - " +
            "This may indicate grammar inefficiency",
            token.getLine(), token.getCharPositionInLine(), input);
    
    // Store for later analysis - this is often a performance warning, not an error
        SyntaxError warning = new SyntaxError(
            recognizer,
            token,
            token.getLine(),
            token.getCharPositionInLine(),
            message,
            null);
    
        syntaxErrors.add(warning);
    }

    @Override
    public void reportContextSensitivity(Parser recognizer,
                                       DFA dfa,
                                       int startIndex,
                                       int stopIndex,
                                       int prediction,
                                       ATNConfigSet configs) {
     
        if (!showContextSensitivity) {
            return; // Skip if we are not showing Context Sensitivity findings
        }
        // Get information about where this happened
        // This is where the parser had to make a decision based on context
        // and could not resolve it with a single token lookahead.
        // This often indicates a grammar that is too complex or ambiguous.
        TokenStream tokens = recognizer.getInputStream();
        String input = tokens.getText(new Interval(startIndex, stopIndex));
        Token token = tokens.get(startIndex);
        
        String message = String.format(
            "Context sensitivity detected at line %d:%d for input '%s' - " +
            "Parser chose alternative %d after full context analysis",
            token.getLine(), token.getCharPositionInLine(),
            input, prediction);
        
        // This is primarily useful for performance optimization and grammar debugging
        SyntaxError info = new SyntaxError(
            recognizer,
            token,
            token.getLine(),
            token.getCharPositionInLine(),
            message,
            null);
        
        syntaxErrors.add(info);
    }

    @Override
    public void syntaxError(Recognizer<?, ?> recognizer,
                            Object offendingSymbol,
                            int line, int charPositionInLine,
                            String msg, RecognitionException e)
    {
         // Create and collect a SyntaxError object
        SyntaxError error = new SyntaxError(recognizer, offendingSymbol, 
                                line, charPositionInLine, msg, e);
        syntaxErrors.add(error);
}

    @Override
    public String toString()
    {
        return Utils.join(syntaxErrors.iterator(), "\n");
    }
}