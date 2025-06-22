package errorhandling;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.atn.ATNConfigSet;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.Utils;

/*
 * Class implements 
 */
public class ParseErrorListener  extends BaseErrorListener
{
    private final List<SyntaxError> syntaxErrors = new ArrayList<>();

    public ParseErrorListener()
    {
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