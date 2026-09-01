package sql.factory;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import errorhandling.ParseErrorCollector;
import errorhandling.ParseErrorListener;
import sql.SQLSelectParserLexer;
import sql.SQLSelectParserParser;

/**
 * SQLSelectParserFactory.java
 * This factory class is responsible for creating instances of SQLSelectParserParser.
 * It prepares the parser with the provided SQL query string and sets up error handling.
 * 
 * The parser can be used to parse SQL SELECT statements and collect syntax errors.
 */
public class SQLSelectParserFactory {

	public SQLSelectParserFactory () {
		
	}

	/**
	 * Builds a bespoke SQLSelectParserParser instance for the given SQL query string.
	 * Does not run the parser, but prepares it for parsing.
	 * 
	 * @param query The SQL query string to parse.
	 * @return An instance of SQLSelectParserParser ready to parse the provided query.
	 */
	public  SQLSelectParserParser buildParser(final String query) {
		CharStream qryStream = CharStreams.fromString(query);
    
    	SQLSelectParserLexer lexer = new SQLSelectParserLexer(qryStream);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		SQLSelectParserParser parser = new SQLSelectParserParser(tokens);
		
		// There can be multiple ErrorListeners in the Parser. Each one can handle errors differently.
		// Here we add a custom error listener to collect syntax errors.
		// This allows us to gather all syntax errors encountered during parsing.
		// The ParseErrorListener will collect errors and store them in a list.
		// You can retrieve the errors later using the getSyntaxErrors() method.
		ParseErrorListener errorListener = new ParseErrorListener(false,false,false);
        try {
			parser.addErrorListener(errorListener);
		//	System.out.println("Added custom listener: " + errorListener.getClass().getName() + " with hashCode: " + errorListener.hashCode());
		} catch (Exception e) {
			System.err.println("EXCEPTION when adding listener: " + e.getMessage());
			e.printStackTrace();
		}

		// There is only one ErrorCollector in the Parser.
		// It is used to collect parse errors and provide a way to recover from them
		// while the Parser is working through the text (not implemented yet).
		// The ParseErrorCollector is a custom error strategy that only collects parse errors.
		// It does not handle errors in any specific way, but allows the parser to continue parsing
		// even if there are syntax errors.
		// This is useful for collecting all errors in a single pass, rather than stopping at the first error.
		// The ParseErrorCollector is not used by default, but can be set as the error handler for the parser.
		// It is not a listener, but an error strategy that can be used to recover
		// from errors and continue parsing.

		// You can retrieve the errors later using the getErrorList() method.
		// This class partially implements the ANTLRErrorStrategy interface and ignores 
		// the methods to handle errors.

		// Create a different class if you need to recover from errors and inject context
		// sesnsitive changes to what the parser does when it encounters an error.
		ParseErrorCollector errorCollector = new ParseErrorCollector();
		parser.setErrorHandler(errorCollector);		
		return parser;
	}

}
