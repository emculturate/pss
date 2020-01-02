package sql.walker;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;

import sql.SQLSelectParserLexer;
import sql.SQLSelectParserParser;

public class SQLSelectParserFactory {

	public SQLSelectParserFactory () {
		
	}

	public  SQLSelectParserParser buildParser(final String query) {
		CharStream input = new ANTLRInputStream(query);
		SQLSelectParserLexer lexer = new SQLSelectParserLexer(input);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		SQLSelectParserParser parser = new SQLSelectParserParser(tokens);
		
		SQLWalkerErrorListener errorListener = new SQLWalkerErrorListener();
        parser.addErrorListener(errorListener);

		return parser;
	}

}
