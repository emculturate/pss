package sql.factory;

import java.util.List;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import errorhandling.ParseErrorCollector;
import errorhandling.ParseErrorListener;
import sql.SQLSelectParserLexer;
import sql.SQLSelectParserParser;

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

		// For debugging - correctly cast to List
    	List<?> listeners = parser.getErrorListeners();
    	System.out.println("Number of error listeners: " + listeners.size());
    	for (Object listener : listeners) {
        	System.out.println("Registered listener: " + listener.getClass().getName() + "  - hashCode: " + listener.hashCode());
    	}
 
		// There is only one ErrorCollector in the Parser.
		// It is used to collect parse errors and provide a way to recover from them.
		// The ParseErrorCollector will collect errors and store them in a list.
		// You can retrieve the errors later using the getErrorList() method.
		// The ParseErrorCollector is used to handle errors that occur during parsing.
		// It implements the ANTLRErrorStrategy interface and provides methods to handle errors.
		// It is used to recover from errors and continue parsing.
		ParseErrorCollector errorCollector = new ParseErrorCollector();
		parser.setErrorHandler(errorCollector);

		// Add this after setting the error handler
		if (parser.getErrorHandler() instanceof ParseErrorCollector) {
    	//	System.out.println("Custom error handler successfully registered!");
    		ParseErrorCollector registeredCollector = (ParseErrorCollector)parser.getErrorHandler();
    	//	System.out.println("Error collector class: " + registeredCollector.getClass().getName());
		} else {
   		 	System.out.println("Warning: Custom error handler not registered correctly!");
    		System.out.println("Current handler is: " + parser.getErrorHandler().getClass().getName());
		}
		
		return parser;
	}

}
