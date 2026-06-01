package access;

import java.util.List;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.junit.Assert;
import org.junit.Test;

import errorhandling.ParseErrorCollector;
import errorhandling.ParseErrorListener;
import sql.SQLSelectParserLexer;
import sql.SQLSelectParserParser;
import sql.SQLSelectParserParser.SqlContext;
import sql.walker.SqlParseEventWalker;

public class SnippetTest {

	@Test
	public void basicJoinWithOnOnConditionVariableTest() {
		// This test takes a query from the basic test set and confirms that the Snippet object is correctly constructed
		final String query = " SELECT a.* FROM third a join fourth b on <OnJoinCondition> "; 

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=*, table_ref=a}}}, from={join={1={table={alias=a, table=third}}, 2={join=join, on={substitution={name=<OnJoinCondition>, type=condition}}}, 3={table={alias=b, table=fourth}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<OnJoinCondition>=condition}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{third={*=[[@1,8:8='a',<381>,1:8]]}, fourth={}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={query_dictionary={*=[[@3,10:10='*',<291>,1:10]]}, table_dictionary={third={*=[[@1,8:8='a',<381>,1:8]]}, fourth={}}, filters=[], interface={*=[{name=*, table_ref=a}]}, table_alias={a=third, b=fourth}}}",
				extractor.getSymbolTable().toString());
		
		Snippet hold = extractor.getSnippet();
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=*, table_ref=a}}}, from={join={1={table={alias=a, table=third}}, 2={join=join, on={substitution={name=<OnJoinCondition>, type=condition}}}, 3={table={alias=b, table=fourth}}}}}}",
				hold.getSqlAbstractTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]", 
				hold.getQueryInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<OnJoinCondition>=condition}", 
				hold.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{third={*=[[@1,8:8='a',<381>,1:8]]}, fourth={}}",
				hold.getTableDictionary().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={query_dictionary={*=[[@3,10:10='*',<291>,1:10]]}, table_dictionary={third={*=[[@1,8:8='a',<381>,1:8]]}, fourth={}}, filters=[], interface={*=[{name=*, table_ref=a}]}, table_alias={a=third, b=fourth}}}",
				hold.getSymbolTable().toString());
	}


	/**
	 * Run test of the parser from a query
	 * 
	 * @param query
	 * @param parser
	 * @return
	 */
	private SqlParseEventWalker runParsertest(final String query, final SQLSelectParserParser parser)  {
		try {
			System.out.println();
			// There should be zero errors
			SqlContext tree = parser.sql();
			final int numErrors = parser.getNumberOfSyntaxErrors();
			Assert.assertEquals("Expected no failures with " + query, 0, numErrors);

			SqlParseEventWalker extractor = new SqlParseEventWalker();
			ParseTreeWalker.DEFAULT.walk(extractor, tree);
			System.out.println("Result: " + extractor.getAsTree());
			System.out.println("Interface: " + extractor.getInterface());
			System.out.println("Symbol Tree: " + extractor.getSymbolTable());
			System.out.println("Table Dictionary: " + extractor.getTableColumnDictionaryMap());
			System.out.println("Query Column Dictionary: " + extractor.getQueryColumnDictionaryMap());
			System.out.println("Substitution Variables: " + extractor.getSubstitutionsMap());

			Object errorHandler = parser.getErrorHandler();
			if (errorHandler instanceof ParseErrorCollector collector) {
				System.out.println("Parser Errors: " + collector.getErrorList());
			} else {
				String handlerName = (errorHandler == null) ? "null" : errorHandler.getClass().getName();
				System.out.println("Parser Error Handler: " + handlerName
					+ " (syntax errors counted: " + parser.getNumberOfSyntaxErrors() + ")");
			}

			// check for Syntax Errors Captured by the Listeners
			List<?> listeners = parser.getErrorListeners();
 			for (Object listener : listeners) {
				if (listener instanceof ParseErrorListener parseErrorListener){
					System.out.println(listener.getClass().getName() 
					+ " found Diagnostics: " 
					+ parseErrorListener.getDiagnostics());
				}
			}
			return extractor;
		} catch (RecognitionException e) {
			System.err.println("Exception parsing eqn: " + query);
			return null;
		}
	}

	private static final SQLSelectParserParser parse(final String query) {
		CharStream input = CharStreams.fromString(query);
		SQLSelectParserLexer lexer = new SQLSelectParserLexer(input);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		SQLSelectParserParser parser = new SQLSelectParserParser(tokens);

		return parser;
	}

}
