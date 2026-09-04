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
import errorhandling.ParseSyntaxErrorContext;
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

		assertFatalSyntaxErrorAtPosition(
				snippet,
				"REPORT_ERROR",
				"Line 1:31 - unexpected input: '(' in rule jinja_arg: SELECT * FROM {{ source(env_var('DB', 'PDP_AMS'), var('TABLE_NAME')) }}",
				"(",
				1,
				31,
				"jinja_arg",
				query,
				ParseSyntaxErrorContext.SYNTAX_CLASS_TEMPLATE_LIKE,
				"jinja_arg,jinja_arg_list,jinja_function_call");
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
		assertFatalSyntaxErrorAtPosition(
				snippet,
				"REPORT_ERROR",
				"Line 1:15 - unexpected input: '<' in rule table_source_primary: select 1 from <[Acquia_ALR].[no__contacts].last_delivered> no_contacts",
				"<",
				1,
				15,
				"table_source_primary",
				"select 1 from <[Acquia_ALR].[no__contacts].last_delivered> no_contacts",
				ParseSyntaxErrorContext.SYNTAX_CLASS_GRAMMAR_GAP,
				"table_source_primary,table_primary,table_reference_list");
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
		assertFatalSyntaxErrorAtPosition(
				snippet,
				"REPORT_ERROR",
				"Line 1:7 - unexpected input: 'FROM' in rule select_item: SELECT FROM",
				"FROM",
				1,
				7,
				"select_item",
				"SELECT FROM",
				ParseSyntaxErrorContext.SYNTAX_CLASS_GRAMMAR_GAP,
				"select_item,select_list,query_specification");
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
		parser.removeErrorListeners();
		ParseErrorListener listener = new ParseErrorListener(false, false, false);
		parser.addErrorListener(listener);
		parser.sql();

		assertFatalSyntaxErrorAtPosition(
				snippetFromListenerDiagnostics(listener.getDiagnostics()),
				"SYNTAX_ERROR",
				"Line 1:7 - unexpected input: 'FROM' in rule query_specification: SELECT FROM",
				"FROM",
				1,
				7,
				"query_specification",
				query,
				ParseSyntaxErrorContext.SYNTAX_CLASS_GRAMMAR_GAP,
				"query_specification,set_operation_member,unionized_query");
	}

	@Test
	public void parserReportErrorIncludesSnippetAndRuleTest() {
		final String query = "select from";
		final Snippet snippet = runParserAccessSnippet(query);
		assertFatalSyntaxErrorAtPosition(
				snippet,
				"REPORT_ERROR",
				"Line 1:7 - unexpected input: 'from' in rule select_item: select from",
				"from",
				1,
				7,
				"select_item",
				query,
				ParseSyntaxErrorContext.SYNTAX_CLASS_GRAMMAR_GAP,
				"select_item,select_list,query_specification");
	}
}
