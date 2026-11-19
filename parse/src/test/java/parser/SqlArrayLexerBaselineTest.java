package parser;

import java.util.stream.Collectors;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.Trees;
import org.junit.Assert;
import org.junit.Test;

import errorhandling.ParseErrorCollector;
import sql.SQLSelectParserLexer;
import sql.SQLSelectParserParser;
import sql.factory.SQLSelectParserFactory;

public class SqlArrayLexerBaselineTest {

	private void assertTokenNames(String sql, String expected) {
		SQLSelectParserLexer lexer = new SQLSelectParserLexer(CharStreams.fromString(sql));
		lexer.removeErrorListeners();
		lexer.addErrorListener(new BaseErrorListener() {
			@Override
			public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol,
					int line, int charPositionInLine, String message, RecognitionException exception) {
				Assert.fail(line + ":" + charPositionInLine + " " + message);
			}
		});
		String actual = lexer.getAllTokens().stream()
				.map(token -> lexer.getVocabulary().getSymbolicName(token.getType()))
				.collect(Collectors.joining(" "));
		Assert.assertEquals(sql, expected, actual);
	}

	@Test
	public void structuredConstructorLowercaseNameHasDedicatedToken() {
		assertTokenNames(
				"select array_construct_structured(a.x, 1) as arr, a.id from t a where a.id > 0",
				"SELECT ARRAY_CONSTRUCT_STRUCTURED LEFT_PAREN Identifier DOT Identifier COMMA NUMBER "
						+ "RIGHT_PAREN AS Identifier COMMA Identifier DOT Identifier FROM Identifier "
						+ "Identifier WHERE Identifier DOT Identifier GTH NUMBER");
	}

	@Test
	public void structuredConstructorMixedCaseNameHasDedicatedToken() {
		assertTokenNames(
				"select Array_Construct_Structured(a.x, 1) as arr, a.id from t a where a.id > 0",
				"SELECT ARRAY_CONSTRUCT_STRUCTURED LEFT_PAREN Identifier DOT Identifier COMMA NUMBER "
						+ "RIGHT_PAREN AS Identifier COMMA Identifier DOT Identifier FROM Identifier "
						+ "Identifier WHERE Identifier DOT Identifier GTH NUMBER");
	}

	@Test
	public void structuredConstructorKeywordPreservesQualifiedUdfAndColumnNames() {
		String sql = "select custom.ARRAY_CONSTRUCT_STRUCTURED(a.x) as arr, "
				+ "a.ARRAY_CONSTRUCT_STRUCTURED from t a where a.id > 0";
		assertTokenNames(sql,
				"SELECT Identifier DOT ARRAY_CONSTRUCT_STRUCTURED LEFT_PAREN Identifier DOT Identifier "
						+ "RIGHT_PAREN AS Identifier COMMA Identifier DOT ARRAY_CONSTRUCT_STRUCTURED "
						+ "FROM Identifier Identifier WHERE Identifier DOT Identifier GTH NUMBER");
		SQLSelectParserParser parser = new SQLSelectParserFactory().buildParser(sql);
		SQLSelectParserParser.SqlContext tree = parser.sql();
		ParseErrorCollector errors = (ParseErrorCollector) parser.getErrorHandler();
		Assert.assertEquals(sql + ": " + errors.getErrorList(), 0, errors.getErrorCount());
		Assert.assertEquals("[]", errors.getErrorList().toString());
		Assert.assertEquals(0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals(Token.EOF, parser.getCurrentToken().getType());
		Assert.assertEquals(1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_routine_invocation).size());
	}

	@Test
	public void filterLambdaArrowIsOneTokenBesideComparisons() {
		assertTokenNames(
				"select filter(a.tags, x -> x > 0) as tags, a.id from t a where a.id > 0",
				"SELECT FILTER LEFT_PAREN Identifier DOT Identifier COMMA Identifier LAMBDA_ARROW "
						+ "Identifier GTH NUMBER RIGHT_PAREN AS Identifier COMMA Identifier DOT Identifier "
						+ "FROM Identifier Identifier WHERE Identifier DOT Identifier GTH NUMBER");
	}

	@Test
	public void transformLambdaArrowIsOneTokenBesideArithmetic() {
		assertTokenNames(
				"select transform(a.tags, x -> x + 1) as tags, a.id from t a where a.id > 0",
				"SELECT TRANSFORM LEFT_PAREN Identifier DOT Identifier COMMA Identifier LAMBDA_ARROW "
						+ "Identifier PLUS NUMBER RIGHT_PAREN AS Identifier COMMA Identifier DOT Identifier "
						+ "FROM Identifier Identifier WHERE Identifier DOT Identifier GTH NUMBER");
	}

	@Test
	public void reduceLambdaArrowPreservesMinusNamedArgumentsAndComparisons() {
		assertTokenNames(
				"select reduce(a.tags, 0, (acc, x)->acc-x) as total, a.id "
						+ "from t a, lateral flatten(input=>a.tags) f where a.id > 0",
				"SELECT REDUCE LEFT_PAREN Identifier DOT Identifier COMMA NUMBER COMMA LEFT_PAREN "
						+ "Identifier COMMA Identifier RIGHT_PAREN LAMBDA_ARROW Identifier MINUS Identifier "
						+ "RIGHT_PAREN AS Identifier COMMA Identifier DOT Identifier FROM Identifier "
						+ "Identifier COMMA LATERAL FLATTEN LEFT_PAREN INPUT IMPLIES Identifier DOT "
						+ "Identifier RIGHT_PAREN Identifier WHERE Identifier DOT Identifier GTH NUMBER");
	}
}
