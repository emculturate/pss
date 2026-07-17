package sql.walker;

import org.junit.Assert;
import org.junit.Test;

import access.Snippet;
import access.SqlParserAccess;
import errorhandling.ParseDiagnostic;
import errorhandling.ParseErrorCollector;

import static mumble.SQLParserEndPoints.SQLPARSER_SQL_TREE_KEY;

/**
 * Parser-layer diagnostic code assertions ({@code ParseErrorCollector} / merged snippet).
 */
public class SqlParserDiagnosticTests extends AbstractSqlParseEventWalkerTest {

	private Snippet runParserAccessSnippet(final String query) {
		SqlParserAccess accessObject = new SqlParserAccess(true, true, true);
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
	public void parseErrorCollectorManualErrorDiagnosticTest() {
		ParseErrorCollector collector = new ParseErrorCollector();
		collector.addError("Collector manual error probe");

		assertDiagnosticCountBySeverity(
				snippetFromCollector(collector),
				"MANUAL_ERROR",
				ParseDiagnostic.Severity.ERROR,
				"Collector manual error probe",
				null,
				1);
	}

	@Test
	public void parseErrorCollectorManualFatalDiagnosticTest() {
		ParseErrorCollector collector = new ParseErrorCollector();
		collector.addFatalError("Collector manual fatal probe");

		assertFatalDiagnosticCount(
				snippetFromCollector(collector),
				"MANUAL_FATAL",
				"Collector manual fatal probe",
				null,
				1);
	}

	@Test
	public void parseErrorCollectorManualWarningDiagnosticTest() {
		ParseErrorCollector collector = new ParseErrorCollector();
		collector.addWarning("Collector manual warning probe");

		assertDiagnosticCountBySeverity(
				snippetFromCollector(collector),
				"MANUAL_WARNING",
				ParseDiagnostic.Severity.WARNING,
				"Collector manual warning probe",
				null,
				1);
	}
}
