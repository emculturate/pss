package parser;

import java.util.Locale;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.Trees;
import org.junit.Assert;
import org.junit.Test;

import errorhandling.ParseErrorCollector;
import sql.SQLSelectParserLexer;
import sql.SQLSelectParserParser;
import sql.factory.SQLSelectParserFactory;

public class SqlDatetimeLexerBaselineTest {

	private static final String[] DATETIME_FUNCTIONS = {
			"DATEADD", "TIMEADD", "TIMESTAMPADD",
			"DATEDIFF", "TIMEDIFF", "TIMESTAMPDIFF",
			"DATE_TRUNC", "TRUNC", "TIME_SLICE", "LAST_DAY", "NEXT_DAY", "PREVIOUS_DAY",
			"DATE_BIN", "AGE", "MAKE_DATE", "MAKE_TIME", "MAKE_TIMESTAMP",
			"MAKE_TIMESTAMPTZ", "MAKE_INTERVAL",
			"JUSTIFY_DAYS", "JUSTIFY_HOURS", "JUSTIFY_INTERVAL",
			"CLOCK_TIMESTAMP", "STATEMENT_TIMESTAMP", "TRANSACTION_TIMESTAMP", "TIMEOFDAY"
	};

	private SQLSelectParserParser.SqlContext assertParses(String sql) {
		SQLSelectParserParser parser = new SQLSelectParserFactory().buildParser(sql);
		SQLSelectParserParser.SqlContext tree = parser.sql();
		assertNoSyntaxErrors(sql, parser);
		return tree;
	}

	private void assertNoSyntaxErrors(String sql, SQLSelectParserParser parser) {
		ParseErrorCollector errors = (ParseErrorCollector) parser.getErrorHandler();
		Assert.assertEquals(sql + ": " + errors.getErrorList(), 0, errors.getErrorCount());
		Assert.assertEquals(sql, 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals(sql, Token.EOF, parser.getCurrentToken().getType());
	}

	@Test
	public void datetimeNamesHaveDedicatedCaseInsensitiveTokens() {
		for (String name : DATETIME_FUNCTIONS) {
			for (String spelling : new String[] {
					name, name.toLowerCase(Locale.ROOT),
					name.substring(0, 1) + name.substring(1).toLowerCase(Locale.ROOT)
			}) {
				SQLSelectParserLexer lexer = new SQLSelectParserLexer(CharStreams.fromString(spelling));
				Token token = lexer.nextToken();
				Assert.assertEquals(spelling, name,
						lexer.getVocabulary().getSymbolicName(token.getType()));
				Assert.assertEquals(spelling, Token.EOF, lexer.nextToken().getType());
			}
		}
	}

	@Test
	public void datetimeNamesRemainUsableForQualifiedUserFunctions() {
		for (String name : DATETIME_FUNCTIONS) {
			assertParses("select custom." + name + "(a.x) from t a");
		}
	}

	@Test
	public void contextualKeywordsAreAcceptedInIdentifierPositions() {
		for (String name : new String[] {"position", "input", "path"}) {
			assertParses("select " + name + " from t");
			assertParses("select a." + name + " from t a");
			assertParses("select a.x as " + name + " from t a");
			assertParses("select " + name + ".x from t " + name);
			assertParses("with " + name + " as (select 1 as x) select "
					+ name + ".x from " + name);
		}
	}

	@Test
	public void positionCallsKeepTheirDedicatedProduction() {
		for (String arguments : new String[] {"'a' IN a.x", "'a', a.x", "'a', a.x, 2"}) {
			SQLSelectParserParser.SqlContext tree =
					assertParses("select position(" + arguments + ") from t a");
			Assert.assertEquals(arguments, 1,
					Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_position_function).size());
		}
	}

	@Test
	public void contextualKeywordsKeepTheirExistingKeywordRoles() {
		assertParses("select f.value from t a, lateral flatten(input => a.x, path => 'items') f");
		assertParses("select truncate(a.x) from t a");
		assertParses("select truncate(a.x, 2) from t a");
		SQLSelectParserParser parser = new SQLSelectParserFactory().buildParser("truncate table t");
		parser.truncate_end_point();
		assertNoSyntaxErrors("truncate table t", parser);
	}
}
