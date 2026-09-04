package errorhandling;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;
import org.junit.Assert;
import org.junit.Test;

import sql.SQLSelectParserLexer;

public class ParseSyntaxErrorContextTest {

	@Test
	public void extractContextSnippetUsesFullShortLine() {
		String sql = "SELECT LISTAGG(channel) WITHIN GROUP (ORDER BY sent_at) FROM t";
		Token within = requireToken(sql, "WITHIN");
		Assert.assertEquals(sql, ParseSyntaxErrorContext.extractContextSnippet(within));
	}

	@Test
	public void extractContextSnippetWindowsLongLine() {
		String padding = "x".repeat(120);
		String sql = padding + " LISTAGG(channel) WITHIN GROUP (ORDER BY sent_at) " + padding;
		Token within = requireToken(sql, "WITHIN");
		String snippet = ParseSyntaxErrorContext.extractContextSnippet(within);
		Assert.assertTrue("Expected leading ellipsis: " + snippet, snippet.startsWith("..."));
		Assert.assertTrue("Expected trailing ellipsis: " + snippet, snippet.endsWith("..."));
		Assert.assertTrue("Expected surrounding SQL: " + snippet, snippet.contains("LISTAGG(channel) WITHIN GROUP"));
		Assert.assertTrue("Window should stay short: " + snippet.length(), snippet.length() < sql.length());
	}

	@Test
	public void classifySyntaxDetectsJinjaDelimiters() {
		Assert.assertEquals(
				ParseSyntaxErrorContext.SYNTAX_CLASS_TEMPLATE_LIKE,
				ParseSyntaxErrorContext.classifySyntax("(", "SELECT * FROM {{ source(env_var('DB')) }}"));
		Assert.assertEquals(
				ParseSyntaxErrorContext.SYNTAX_CLASS_GRAMMAR_GAP,
				ParseSyntaxErrorContext.classifySyntax("from", "select from"));
	}

	private static Token requireToken(String sql, String text) {
		SQLSelectParserLexer lexer = new SQLSelectParserLexer(CharStreams.fromString(sql));
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		tokens.fill();
		for (Token token : tokens.getTokens()) {
			if (token != null && text.equals(token.getText())) {
				return token;
			}
		}
		Assert.fail("Expected token " + text + " in: " + sql);
		return null;
	}
}
