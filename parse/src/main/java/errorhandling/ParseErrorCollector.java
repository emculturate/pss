package errorhandling;

import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.ANTLRErrorStrategy;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.misc.IntervalSet;

import sql.SQLSelectParserParser;


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

	private final List<String> errorList = new ArrayList<>();
	private final List<ParseDiagnostic> diagnostics = new ArrayList<>();
	
	public List<String> getErrorList() {
		return errorList;
	}

	public List<ParseDiagnostic> getDiagnostics() {
		return diagnostics;
	}

	public int getErrorCount() {
		return errorList.size();
	}

	private void addDiagnostic(ParseDiagnostic.Severity severity, String code, String message, Token token,
			boolean recoverable, String exceptionType) {
		Integer line = token == null ? null : token.getLine();
		Integer pos = token == null ? null : token.getCharPositionInLine();
		String tokenText = token == null ? null : token.getText();
		diagnostics.add(new ParseDiagnostic(
				severity,
				code,
				message,
				line,
				pos,
				"ParseErrorCollector",
				null,
				tokenText,
				recoverable,
				"parse.strategy",
				exceptionType,
				null));
	}
	
	public void addError(String errorMessage) {
		// This method adds an error message to the error list.
		errorList.add(errorMessage);
		addDiagnostic(ParseDiagnostic.Severity.ERROR, "MANUAL_ERROR", errorMessage, null, false, null);
	}	

	public void addFatalError(String errorMessage) {
		errorList.add(errorMessage);
		addDiagnostic(ParseDiagnostic.Severity.FATAL, "MANUAL_FATAL", errorMessage, null, false, null);
	}

	public void addWarning(String warningMessage) {
		addDiagnostic(ParseDiagnostic.Severity.WARNING, "MANUAL_WARNING", warningMessage, null, true, null);
	}

	private boolean expectsVariableIdentifierToken(Parser recognizer) {
		if (!(recognizer instanceof SQLSelectParserParser)) {
			return false;
		}

		IntervalSet expectedTokens = recognizer.getExpectedTokens();
		if (expectedTokens == null) {
			return false;
		}

		return expectedTokens.contains(SQLSelectParserParser.Variable_Identifier)
				|| expectedTokens.contains(SQLSelectParserParser.Extended_Variable_Identifier)
				|| expectedTokens.contains(SQLSelectParserParser.Mixed_Variable_Identifier);
	}

	private boolean nextTokenLooksLikeVariableBody(Parser recognizer) {
		TokenStream input = recognizer.getInputStream();
		if (input == null) {
			return false;
		}

		Token next = input.LT(2);
		if (next == null) {
			return false;
		}

		String nextText = next.getText();
		if (nextText == null || nextText.isBlank()) {
			return false;
		}

		char first = nextText.charAt(0);
		return first == '[' || first == '_' || Character.isLetter(first);
	}

	private boolean shouldRecoverMalformedVariableStart(Parser recognizer, Token currentToken) {
		if (recognizer == null || currentToken == null) {
			return false;
		}

		String currentText = currentToken.getText();
		if (!"<".equals(currentText)) {
			return false;
		}

		return expectsVariableIdentifierToken(recognizer) && nextTokenLooksLikeVariableBody(recognizer);
	}
	
	@Override
	public void reset(Parser recognizer) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Token recoverInline(Parser recognizer) throws RecognitionException {
	    // Get the current token
		Token currentToken = recognizer.getCurrentToken();

		if (shouldRecoverMalformedVariableStart(recognizer, currentToken)) {
			String recoveryMessage = String.format(
					"Line %d:%d - Recovering malformed variable identifier start '%s' by skipping one token",
					currentToken.getLine(),
					currentToken.getCharPositionInLine(),
					currentToken.getText());
			errorList.add(recoveryMessage);
			addDiagnostic(ParseDiagnostic.Severity.WARNING,
					"RECOVER_MALFORMED_VARIABLE_START",
					recoveryMessage,
					currentToken,
					true,
					null);

			recognizer.consume();
			return recognizer.getCurrentToken();
		}
    
		// Create an error message
		String errorMessage = String.format("Line %d:%d - Invalid syntax near '%s'", 
			currentToken.getLine(), 
			currentToken.getCharPositionInLine(), 
			currentToken.getText());
		errorList.add(errorMessage);
		addDiagnostic(ParseDiagnostic.Severity.WARNING, "RECOVER_INLINE", errorMessage, currentToken, true, null);
		
		// Return the current token to continue parsing
		return currentToken;
	}

	@Override
	public void recover(Parser recognizer, RecognitionException e) throws RecognitionException {
		Token currentToken = recognizer.getCurrentToken();
		if (shouldRecoverMalformedVariableStart(recognizer, currentToken)) {
			String recoveryMessage = String.format(
					"Line %d:%d - Recovering malformed variable identifier start '%s' by skipping one token",
					currentToken.getLine(),
					currentToken.getCharPositionInLine(),
					currentToken.getText());
			errorList.add(recoveryMessage);
			addDiagnostic(ParseDiagnostic.Severity.WARNING,
					"RECOVER_MALFORMED_VARIABLE_START",
					recoveryMessage,
					currentToken,
					true,
					e == null ? null : e.getClass().getSimpleName());

			recognizer.consume();
			return;
		}

	    // Add the error to our list
		Token offendingToken = e == null ? null : e.getOffendingToken();
		String errorMessage = String.format("Line %d:%d - Syntax error, attempting recovery", 
		offendingToken == null ? -1 : offendingToken.getLine(),
		offendingToken == null ? -1 : offendingToken.getCharPositionInLine());
    	errorList.add(errorMessage);
		addDiagnostic(ParseDiagnostic.Severity.WARNING, "RECOVER", errorMessage, offendingToken, true,
				e == null ? null : e.getClass().getSimpleName());
    
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
		Token offendingToken = e == null ? null : e.getOffendingToken();
	    String errorMessage = String.format("Line %d:%d - %s", 
		offendingToken == null ? -1 : offendingToken.getLine(),
		offendingToken == null ? -1 : offendingToken.getCharPositionInLine(),
		e == null ? "Unknown parser error" : e.getMessage());
    
    	// Add more context about the error
		String unexpectedInput = offendingToken == null ? null : offendingToken.getText();
    	if (unexpectedInput != null) {
     	   errorMessage += " - unexpected input: '" + unexpectedInput + "'";
    	}
    
    	errorList.add(errorMessage);
		addDiagnostic(ParseDiagnostic.Severity.FATAL, "REPORT_ERROR", errorMessage, offendingToken, false,
				e == null ? null : e.getClass().getSimpleName());
	}

}
