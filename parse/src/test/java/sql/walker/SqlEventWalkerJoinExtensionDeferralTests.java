package sql.walker;

import static mumble.SQLParserEndPoints.SQLPARSER_SCRIPT_TREE_KEY;
import static mumble.SQLParserEndPoints.SQLPARSER_SQL_TREE_KEY;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Test;

import access.Snippet;
import access.SqlParserAccess;
import errorhandling.ParseDiagnostic;
import errorhandling.ParseDiagnostic.Severity;

/**
 * Workplan 5.1.4 Entry 14: in a statement with a join extension variable, unresolved qualified
 * table references may be declared by the substituted join snippet, so that statement's
 * QUALIFIED_COLUMN_NOT_FOUND_IN_TABLE fatals become severe warnings plus one JOIN_EXTENSION_REQUIRED
 * severe warning per join extension variable. All other fatals stay fatal.
 */
public class SqlEventWalkerJoinExtensionDeferralTests {

	private static final String QUALIFIED = "QUALIFIED_COLUMN_NOT_FOUND_IN_TABLE";
	private static final String REQUIRED = "JOIN_EXTENSION_REQUIRED";

	private static Snippet parse(String sql, String endpoint) {
		SqlParserAccess access = new SqlParserAccess(false, false, false);
		access.executeTheParse(sql, endpoint);
		Snippet snippet = access.getSnippet();
		Assert.assertNotNull("Snippet should not be null for: " + sql, snippet);
		return snippet;
	}

	private static List<ParseDiagnostic> diagnostics(Snippet snippet, Severity severity, String code) {
		return snippet.getParserDiagnosticList().stream()
				.filter(d -> d != null && d.severity() == severity && code.equals(d.code()))
				.collect(Collectors.toList());
	}

	@Test
	public void missingAliasWithJoinExtensionBecomesSevereWarningTest() {
		Snippet snippet = parse("select a.id, ev.event_ct from t a <extension>", SQLPARSER_SQL_TREE_KEY);

		Assert.assertEquals(snippet.getFatalErrorStringList().toString(), 0, snippet.getFatalErrorCount());
		List<ParseDiagnostic> converted = diagnostics(snippet, Severity.SEVERE_WARNING, QUALIFIED);
		Assert.assertEquals(1, converted.size());
		Assert.assertEquals("Source Table not found for Column 'event_ct' at (l:1 c:16). No alias or table called 'ev'.",
				converted.get(0).message());

		List<ParseDiagnostic> required = diagnostics(snippet, Severity.SEVERE_WARNING, REQUIRED);
		Assert.assertEquals(1, required.size());
		Assert.assertEquals(Integer.valueOf(1), required.get(0).line());
		Assert.assertEquals(Integer.valueOf(34), required.get(0).charPositionInLine());
		Assert.assertEquals("Join extension variable '<extension>' at (l:1 c:34) is not optional: 1 table/column reference(s)"
				+ " not defined in the query (ev.event_ct) must be satisfied by the substituted join extension snippet.",
				required.get(0).message());
	}

	@Test
	public void oneRequiredWarningPerJoinExtensionVariableTest() {
		Snippet snippet = parse(
				"select a.id, ev.x from t a <ext1> where exists (select 1 from u b <ext2> where b.k = qq.k)",
				SQLPARSER_SQL_TREE_KEY);

		Assert.assertEquals(snippet.getFatalErrorStringList().toString(), 0, snippet.getFatalErrorCount());
		Assert.assertEquals(2, diagnostics(snippet, Severity.SEVERE_WARNING, QUALIFIED).size());
		List<String> requiredTokens = diagnostics(snippet, Severity.SEVERE_WARNING, REQUIRED).stream()
				.map(ParseDiagnostic::tokenText)
				.collect(Collectors.toList());
		Assert.assertEquals(List.of("<ext1>", "<ext2>"), requiredTokens);
	}

	@Test
	public void joinExtensionWithoutMissingReferencesAddsNoWarningTest() {
		Snippet snippet = parse("select a.id from t a <extension>", SQLPARSER_SQL_TREE_KEY);

		Assert.assertEquals(0, snippet.getFatalErrorCount());
		Assert.assertEquals(0, snippet.getDiagnosticCountBySeverity(Severity.SEVERE_WARNING));
	}

	@Test
	public void missingAliasWithoutJoinExtensionStaysFatalTest() {
		Snippet snippet = parse("select a.id, zz.k from t a", SQLPARSER_SQL_TREE_KEY);

		Assert.assertEquals(1, diagnostics(snippet, Severity.FATAL, QUALIFIED).size());
		Assert.assertEquals(0, diagnostics(snippet, Severity.SEVERE_WARNING, REQUIRED).size());
	}

	@Test
	public void unrelatedFatalsStayFatalWithJoinExtensionTest() {
		Snippet setOp = parse("select a.id, ev.x from t a <extension> union select b.id, b.k, b.z from u b",
				SQLPARSER_SQL_TREE_KEY);
		Assert.assertEquals(1, setOp.getDiagnosticCountByCodeAndSeverity(
				"SET_OPERATION_INTERFACE_COLUMN_COUNT_MISMATCH", Severity.FATAL));
		Assert.assertEquals(1, setOp.getFatalErrorCount());
		Assert.assertEquals(1, diagnostics(setOp, Severity.SEVERE_WARNING, QUALIFIED).size());

		Snippet duplicate = parse("select a.id, a.id from t a <extension> where ev.k = 1", SQLPARSER_SQL_TREE_KEY);
		Assert.assertEquals(1, duplicate.getDiagnosticCountByCodeAndSeverity("DUPLICATE_INTERFACE_COLUMNS", Severity.FATAL));
		Assert.assertEquals(1, duplicate.getFatalErrorCount());
		Assert.assertEquals(1, diagnostics(duplicate, Severity.SEVERE_WARNING, QUALIFIED).size());
	}

	@Test
	public void scriptStatementsAreEvaluatedIndependentlyTest() {
		Snippet snippet = parse(
				"select a.id, zz.k from t a;\n"
						+ "select b.id from u b <extension>;\n"
						+ "select c.id, yy.k from v c;\n"
						+ "select d.id, ev.k from w d <extension>;",
				SQLPARSER_SCRIPT_TREE_KEY);

		List<Integer> fatalLines = diagnostics(snippet, Severity.FATAL, QUALIFIED).stream()
				.map(ParseDiagnostic::line)
				.collect(Collectors.toList());
		Assert.assertEquals("statements without a join extension keep their fatals", List.of(1, 3), fatalLines);

		List<ParseDiagnostic> converted = diagnostics(snippet, Severity.SEVERE_WARNING, QUALIFIED);
		Assert.assertEquals(1, converted.size());
		Assert.assertEquals(Integer.valueOf(4), converted.get(0).line());

		List<ParseDiagnostic> required = diagnostics(snippet, Severity.SEVERE_WARNING, REQUIRED);
		Assert.assertEquals("only statement 4 has a deferred reference", 1, required.size());
		Assert.assertEquals(Integer.valueOf(4), required.get(0).line());
		Assert.assertTrue(required.get(0).message(), required.get(0).message().contains("(ev.k)"));
	}
}
