package sql.factory;

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
		ParseErrorListener errorListener = new ParseErrorListener();
        parser.addErrorListener(errorListener);
		Object t = parser.getErrorListeners();

		// There is only one ErrorCollector in the Parser.
		// It is used to collect parse errors and provide a way to recover from them.
		// The ParseErrorCollector will collect errors and store them in a list.
		// You can retrieve the errors later using the getErrorList() method.
		// The ParseErrorCollector is used to handle errors that occur during parsing.
		// It implements the ANTLRErrorStrategy interface and provides methods to handle errors.
		// It is used to recover from errors and continue parsing.
		ParseErrorCollector errorCollector = new ParseErrorCollector();
		parser.setErrorHandler(errorCollector);

		Object v = parser.getErrorHandler();
		
		return parser;
	}

}
