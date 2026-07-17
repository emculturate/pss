package sql.walker;

import java.util.List;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.junit.Assert;
import org.junit.Test;

import access.Snippet;
import access.SqlParserAccess;
import errorhandling.ParseDiagnostic;
import errorhandling.ParseErrorCollector;
import errorhandling.ParseErrorListener;
import sql.SQLSelectParserLexer;
import sql.SQLSelectParserParser;

import static mumble.SQLParserEndPoints.SQLPARSER_SQL_TREE_KEY;

/**
 * Parser-layer diagnostic code assertions ({@code ParseErrorCollector}, {@code ParseErrorListener},
 * and merged {@link SqlParserAccess} snippets).
 */
public class SqlParserDiagnosticTests extends AbstractSqlParseEventWalkerTest {

	private Snippet runParserAccessSnippet(final String query) {
		return runParserAccessSnippet(query, true, true, true);
	}

	private Snippet runParserAccessSnippet(
			final String query,
			final boolean showAmbiguities,
			final boolean showFullContext,
			final boolean showContextSensitivity) {
		SqlParserAccess accessObject = new SqlParserAccess(
				showAmbiguities,
				showFullContext,
				showContextSensitivity);
		accessObject.executeTheParse(query, SQLPARSER_SQL_TREE_KEY);
		Snippet snippet = accessObject.getSnippet();
		Assert.assertNotNull("Snippet should not be null", snippet);
		return snippet;
	}

	private Snippet snippetFromCollector(ParseErrorCollector collector) {
		Snippet snippet = new Snippet(null, null, null, null, null, null);
		snippet.setParserDiagnosticList(collector.getDiagnostics());
		return snippet;
	}

	private Snippet snippetFromListenerDiagnostics(List<ParseDiagnostic> diagnostics) {
		Snippet snippet = new Snippet(null, null, null, null, null, null);
		snippet.setParserDiagnosticList(diagnostics);
		return snippet;
	}

	private ParseErrorListener requireParseErrorListener(SQLSelectParserParser parser) {
		for (Object listener : parser.getErrorListeners()) {
			if (listener instanceof ParseErrorListener parseErrorListener) {
				return parseErrorListener;
			}
		}
		Assert.fail("Expected ParseErrorListener on parser");
		return null;
	}

	@Test
	public void parserReportErrorUnexpectedInputDiagnosticTest() {
		final String query = "SELECT * FROM {{ source(env_var('DB', 'PDP_AMS'), var('TABLE_NAME')) }}";
		final Snippet snippet = runParserAccessSnippet(query);

		assertFatalDiagnosticAtPosition(
				snippet,
				"REPORT_ERROR",
				"unexpected input",
				"(",
				1,
				31);
	}

	@Test
	public void parserRecoverInlineInvalidSyntaxNearDiagnosticTest() {
		final String query = " select 1 from <[Acquia_ALR].[no__contacts].last_delivered> no_contacts";
		final Snippet snippet = runParserAccessSnippet(query);

		assertDiagnosticAtPosition(
				snippet,
				"RECOVER_INLINE",
				ParseDiagnostic.Severity.WARNING,
				"Invalid syntax near",
				".",
				1,
				28);
	}

	@Test
	public void parserRecoverMalformedVariableStartDiagnosticTest() {
		final String query = " select 1 from <[Acquia_ALR].[no__contacts].last_delivered> no_contacts";
		final Snippet snippet = runParserAccessSnippet(query);

		assertDiagnosticAtPosition(
				snippet,
				"RECOVER_MALFORMED_VARIABLE_START",
				ParseDiagnostic.Severity.WARNING,
				"Recovering malformed variable identifier start",
				"<",
				1,
				15);
	}

	@Test
	public void parserRecoverSyntaxErrorDiagnosticTest() {
		final Snippet snippet = runParserAccessSnippet("SELECT FROM");

		assertDiagnosticAtPosition(
				snippet,
				"RECOVER",
				ParseDiagnostic.Severity.WARNING,
				"attempting recovery",
				"FROM",
				1,
				7);
	}

	@Test
	public void parseErrorCollectorApplicationIssueErrorDiagnosticTest() {
		ParseErrorCollector collector = new ParseErrorCollector();
		collector.addError("Collector application issue error probe");

		assertDiagnosticCountBySeverity(
				snippetFromCollector(collector),
				"APPLICATION_ISSUE_ERROR",
				ParseDiagnostic.Severity.ERROR,
				"Collector application issue error probe",
				null,
				1);
	}

	@Test
	public void parseErrorCollectorApplicationIssueFatalDiagnosticTest() {
		ParseErrorCollector collector = new ParseErrorCollector();
		collector.addFatalError("Collector application issue fatal probe");

		assertFatalDiagnosticCount(
				snippetFromCollector(collector),
				"APPLICATION_ISSUE_FATAL",
				"Collector application issue fatal probe",
				null,
				1);
	}

	@Test
	public void parseErrorCollectorApplicationIssueWarningDiagnosticTest() {
		ParseErrorCollector collector = new ParseErrorCollector();
		collector.addWarning("Collector application issue warning probe");

		assertDiagnosticCountBySeverity(
				snippetFromCollector(collector),
				"APPLICATION_ISSUE_WARNING",
				ParseDiagnostic.Severity.WARNING,
				"Collector application issue warning probe",
				null,
				1);
	}

	@Test
	public void parserAmbiguityDiagnosticTest() {
		final Snippet snippet = runParserAccessSnippet("SELECT a FROM t1", true, false, false);

		assertDiagnosticAtPosition(
				snippet,
				"AMBIGUITY",
				ParseDiagnostic.Severity.WARNING,
				"Ambiguity",
				"a",
				1,
				7);
	}

	@Test
	public void parserFullContextDiagnosticTest() {
		final Snippet snippet = runParserAccessSnippet("SELECT a FROM t1", false, true, false);

		assertDiagnosticAtPosition(
				snippet,
				"FULL_CONTEXT",
				ParseDiagnostic.Severity.WARNING,
				"Attempting full context",
				"a",
				1,
				7);
	}

	@Test
	public void parserContextSensitivityDiagnosticTest() {
		final String query = "SELECT a FROM t1";
		SqlParserAccess accessObject = new SqlParserAccess(false, false, true);
		accessObject.buildParser(query);
		accessObject.runParser(SQLPARSER_SQL_TREE_KEY);
		SQLSelectParserParser parser = accessObject.getParser();
		ParseErrorListener listener = requireParseErrorListener(parser);
		// ANTLR invokes this after full-context resolution; simulate the same span used for FULL_CONTEXT.
		listener.reportContextSensitivity(parser, null, 0, 4, 0, null);

		assertDiagnosticAtPosition(
				snippetFromListenerDiagnostics(listener.getDiagnostics()),
				"CONTEXT_SENSITIVITY",
				ParseDiagnostic.Severity.WARNING,
				"Context sensitivity",
				"SELECT",
				1,
				0);
	}

	@Test
	public void parserSyntaxErrorDiagnosticTest() {
		final String query = "SELECT FROM";
		SQLSelectParserLexer lexer = new SQLSelectParserLexer(CharStreams.fromString(query));
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		SQLSelectParserParser parser = new SQLSelectParserParser(tokens);
		ParseErrorListener listener = new ParseErrorListener(false, false, false);
		parser.addErrorListener(listener);
		parser.sql();

		assertFatalDiagnosticAtPosition(
				snippetFromListenerDiagnostics(listener.getDiagnostics()),
				"SYNTAX_ERROR",
				"mismatched input",
				"FROM",
				1,
				7);
	}
}
