package mumble.sql.template;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.junit.Assert;
import org.junit.Test;

import mumble.sql.Snippet;
import sql.SQLSelectParserLexer;
import sql.SQLSelectParserParser;
import sql.SQLSelectParserParser.SqlContext;
import sql.walker.SqlParseEventWalker;

public class SQLNormalizerTest {

	@Test
	public void basicNormalizerTest() {
		// This test takes a query from the basic test set and confirms that the Snippet object is correctly constructed
		final String query = " SELECT a.*, col2 FROM third a join fourth b on <OnJoinCondition> order by 2,1"; 

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Snippet hold = extractor.getSnippet();
		
//		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=*, table_ref=a}}}, from={join={1={table={alias=a, table=third}}, 2={join=join, on={substitution={name=<OnJoinCondition>, type=condition}}}, 3={table={alias=b, table=fourth}}}}}}",
//				hold.getSqlAbstractTree().toString());
//		Assert.assertEquals("Interface is wrong", "[*]", 
//				hold.getQueryInterface().toString());
//		Assert.assertEquals("Substitution List is wrong", "{<OnJoinCondition>=condition}", 
//				hold.getSubstitutionsMap().toString());
//		Assert.assertEquals("Table Dictionary is wrong", "{third={*=[@1,8:8='a',<210>,1:8]}, fourth={}}",
//				hold.getTableDictionary().toString());
//		Assert.assertEquals("Symbol Table is wrong", "{query0={a=third, b=fourth, third={*=[@1,8:8='a',<210>,1:8]}, fourth={}, interface={*={column={name=*, table_ref=a}}}}}",
//				hold.getSymbolTable().toString());
		
		SQLNormalizer norm = new SQLNormalizer(hold);
		norm.normalize();
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
			System.out.println("Result: " + extractor.getSqlTree());
			System.out.println("Interface: " + extractor.getInterface());
			System.out.println("Symbol Tree: " + extractor.getSymbolTable());
			System.out.println("Table Dictionary: " + extractor.getTableColumnMap());
			System.out.println("Substitution Variables: " + extractor.getSubstitutionsMap());
			return extractor;
		} catch (RecognitionException e) {
			System.err.println("Exception parsing eqn: " + query);
			return null;
		}
	}

	private static final SQLSelectParserParser parse(final String query) {
		CharStream input = new ANTLRInputStream(query);
		SQLSelectParserLexer lexer = new SQLSelectParserLexer(input);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		SQLSelectParserParser parser = new SQLSelectParserParser(tokens);

		return parser;
	}

}
