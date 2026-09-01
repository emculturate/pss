package errorhandling;

import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.ANTLRErrorStrategy;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Token;


/*
 * ParseErrorCollector is an implementation of ANTLRErrorStrategy that collects syntax errors
 * encountered during parsing. It overrides methods to handle errors and recover from them,
 * allowing the parser to continue processing even when errors occur.
 * 
 * The ParseErrorCollector is a custom error strategy that only collects parse errors.
	It does not handle errors in any specific way, but allows the parser to continue parsing
	even if there are syntax errors.
	This is useful for collecting all errors in a single pass, rather than stopping at the first error.
	The ParseErrorCollector is not used by default, but can be set as the error handler for the parser.
	It is not a listener, but an error strategy that can be used to recover
	from errors and continue parsing.

	Inserting this into the parser makes the parser COLLECT any syntax errors but then continue parsing.
	It does not stop at the first error, but collects all errors and allows the parser to continue
	processing the input. This is useful for collecting all errors in a single pass, rather than
	stopping at the first error.
 */
public class ParseErrorCollector implements ANTLRErrorStrategy {

	private final List<String> errorList = new ArrayList<String> ();
	
	public List<String> getErrorList() {
		return errorList;
	}

	public int getErrorCount() {
		return errorList.size();
	}
	
	public void addError(String errorMessage) {
		// This method adds an error message to the error list.
		errorList.add(errorMessage);
	}	
	
	@Override
	public void reset(Parser recognizer) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Token recoverInline(Parser recognizer) throws RecognitionException {
	    // Get the current token
		Token currentToken = recognizer.getCurrentToken();
    
		// Create an error message
		String errorMessage = String.format("Line %d:%d - Invalid syntax near '%s'", 
			currentToken.getLine(), 
			currentToken.getCharPositionInLine(), 
			currentToken.getText());
		errorList.add(errorMessage);
		
		// Return the current token to continue parsing
		return currentToken;
	}

	@Override
	public void recover(Parser recognizer, RecognitionException e) throws RecognitionException {
	    // Add the error to our list
		String errorMessage = String.format("Line %d:%d - Syntax error, attempting recovery", 
        e.getOffendingToken().getLine(),
        e.getOffendingToken().getCharPositionInLine());
    	errorList.add(errorMessage);
    
    	// Consume until we find a token that might get us back on track
    	recognizer.consume();
	}

	@Override
	public void sync(Parser recognizer) throws RecognitionException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean inErrorRecoveryMode(Parser recognizer) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void reportMatch(Parser recognizer) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void reportError(Parser recognizer, RecognitionException e) {
	    String errorMessage = String.format("Line %d:%d - %s", 
        e.getOffendingToken().getLine(),
        e.getOffendingToken().getCharPositionInLine(),
        e.getMessage());
    
    	// Add more context about the error
    	String unexpectedInput = e.getOffendingToken().getText();
    	if (unexpectedInput != null) {
     	   errorMessage += " - unexpected input: '" + unexpectedInput + "'";
    	}
    
    	errorList.add(errorMessage);
	}

}
