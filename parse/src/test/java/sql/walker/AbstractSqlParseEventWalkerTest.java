package sql.walker;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.junit.Assert;

import access.Snippet;
import errorhandling.ParseDiagnostic;
import errorhandling.ParseErrorCollector;
import errorhandling.ParseErrorListener;
import sql.SQLSelectParserParser;
import sql.SQLSelectParserParser.Column_valueContext;
import sql.SQLSelectParserParser.Condition_valueContext;
import sql.SQLSelectParserParser.DdlContext;
import sql.SQLSelectParserParser.Delete_end_pointContext;
import sql.SQLSelectParserParser.In_list_predicate_valueContext;
import sql.SQLSelectParserParser.Join_extension_valueContext;
import sql.SQLSelectParserParser.Predicand_valueContext;
import sql.SQLSelectParserParser.Query_valueContext;
import sql.SQLSelectParserParser.ScriptContext;
import sql.SQLSelectParserParser.SqlContext;
import sql.SQLSelectParserParser.Sql_statementContext;
import sql.SQLSelectParserParser.Truncate_end_pointContext;
import sql.SQLSelectParserParser.Tuple_valueContext;
import sql.SQLSelectParserParser.Update_end_pointContext;
import sql.SQLSelectParserParser.Values_statement_endContext;
import sql.factory.SQLSelectParserFactory;

public abstract class AbstractSqlParseEventWalkerTest {

	protected void assertUnresolvedUnknownColumnsDiagnostic(
			Snippet snippet,
			int expectedLine,
			int expectedCharPositionInLine,
			ParseDiagnostic.Severity expectedSeverity,
			String expectedColumnNameInMessage) {
		Assert.assertNotNull("Snippet should not be null", snippet);
		Assert.assertNotNull("Diagnostic list should not be null", snippet.getParserDiagnosticList());

		ParseDiagnostic unresolvedUnknown = null;
		for (ParseDiagnostic diagnostic : snippet.getParserDiagnosticList()) {
			if (diagnostic != null && "UNRESOLVED_UNQUALIFIED_COLUMNS".equals(diagnostic.code())) {
				unresolvedUnknown = diagnostic;
				break;
			}
		}

		Assert.assertNotNull("Expected unresolved unknown columns diagnostic", unresolvedUnknown);
		Assert.assertEquals("Unexpected diagnostic severity", expectedSeverity,
				unresolvedUnknown.severity());
		Assert.assertNotNull("Expected diagnostic line", unresolvedUnknown.line());
		Assert.assertNotNull("Expected diagnostic character position", unresolvedUnknown.charPositionInLine());
		Assert.assertEquals("Unexpected diagnostic line", Integer.valueOf(expectedLine), unresolvedUnknown.line());
		Assert.assertEquals("Unexpected diagnostic character position", Integer.valueOf(expectedCharPositionInLine),
				unresolvedUnknown.charPositionInLine());
		Assert.assertTrue("Diagnostic message should include unknown column " + expectedColumnNameInMessage,
				unresolvedUnknown.message() != null
						&& unresolvedUnknown.message().contains(expectedColumnNameInMessage));
		Assert.assertTrue("Diagnostic token text should include unknown column " + expectedColumnNameInMessage,
				unresolvedUnknown.tokenText() != null
						&& unresolvedUnknown.tokenText().contains(expectedColumnNameInMessage));
	}

	protected ParseDiagnostic findFatalDiagnosticByCodeAndFragment(
			Snippet snippet,
			String code,
			String fragment) {
		Assert.assertNotNull("Snippet should not be null", snippet);
		Assert.assertNotNull("Diagnostic list should not be null", snippet.getParserDiagnosticList());

		for (ParseDiagnostic diagnostic : snippet.getParserDiagnosticList()) {
			if (diagnostic == null || !ParseDiagnostic.Severity.FATAL.equals(diagnostic.severity())) {
				continue;
			}
			if (!code.equals(diagnostic.code())) {
				continue;
			}
			if (fragment != null
					&& ((diagnostic.tokenText() == null || !diagnostic.tokenText().contains(fragment))
							&& (diagnostic.message() == null || !diagnostic.message().contains(fragment)))) {
				continue;
			}
			return diagnostic;
		}

		return null;
	}

	protected ParseDiagnostic findDiagnosticByCodeAndFragmentAndSeverity(
			Snippet snippet,
			String code,
			ParseDiagnostic.Severity severity,
			String fragment) {
		Assert.assertNotNull("Snippet should not be null", snippet);
		Assert.assertNotNull("Diagnostic list should not be null", snippet.getParserDiagnosticList());

		for (ParseDiagnostic diagnostic : snippet.getParserDiagnosticList()) {
			if (diagnostic == null || !severity.equals(diagnostic.severity())) {
				continue;
			}
			if (!code.equals(diagnostic.code())) {
				continue;
			}
			if (fragment != null
					&& ((diagnostic.tokenText() == null || !diagnostic.tokenText().contains(fragment))
							&& (diagnostic.message() == null || !diagnostic.message().contains(fragment)))) {
				continue;
			}
			return diagnostic;
		}

		return null;
	}

	protected void assertFatalDiagnosticAtPosition(
			Snippet snippet,
			String code,
			String expectedMessageFragment,
			String expectedTokenFragment,
			int expectedLine,
			int expectedCharPositionInLine) {
		String searchFragment = expectedTokenFragment != null ? expectedTokenFragment : expectedMessageFragment;
		ParseDiagnostic diagnostic = findFatalDiagnosticByCodeAndFragment(snippet, code, searchFragment);
		Assert.assertNotNull("Expected fatal diagnostic with code " + code, diagnostic);
		Assert.assertNotNull("Expected diagnostic line", diagnostic.line());
		Assert.assertNotNull("Expected diagnostic character position", diagnostic.charPositionInLine());
		Assert.assertEquals("Unexpected diagnostic line", Integer.valueOf(expectedLine), diagnostic.line());
		Assert.assertEquals("Unexpected diagnostic character position",
				Integer.valueOf(expectedCharPositionInLine), diagnostic.charPositionInLine());

		if (expectedMessageFragment != null) {
			Assert.assertTrue(
					"Diagnostic message should contain '" + expectedMessageFragment + "'",
					diagnostic.message() != null && diagnostic.message().contains(expectedMessageFragment));
		}
	}

	protected void assertDiagnosticAtPosition(
			Snippet snippet,
			String code,
			ParseDiagnostic.Severity severity,
			String expectedMessageFragment,
			String expectedTokenFragment,
			int expectedLine,
			int expectedCharPositionInLine) {
		String searchFragment = expectedTokenFragment != null ? expectedTokenFragment : expectedMessageFragment;
		ParseDiagnostic diagnostic = findDiagnosticByCodeAndFragmentAndSeverity(snippet, code, severity, searchFragment);
		Assert.assertNotNull("Expected diagnostic with code " + code + " and severity " + severity, diagnostic);
		Assert.assertNotNull("Expected diagnostic line", diagnostic.line());
		Assert.assertNotNull("Expected diagnostic character position", diagnostic.charPositionInLine());
		Assert.assertEquals("Unexpected diagnostic line", Integer.valueOf(expectedLine), diagnostic.line());
		Assert.assertEquals("Unexpected diagnostic character position",
				Integer.valueOf(expectedCharPositionInLine), diagnostic.charPositionInLine());

		if (expectedMessageFragment != null) {
			if ("UNRESOLVED_UNQUALIFIED_COLUMNS".equals(code)) {
				return;
			}
			Assert.assertTrue(
					"Diagnostic message should contain '" + expectedMessageFragment + "'",
					diagnostic.message() != null && diagnostic.message().contains(expectedMessageFragment));
		}
	}

	protected int countFatalDiagnostics(
			Snippet snippet,
			String expectedCode,
			String expectedMessageFragment,
			String expectedTokenFragment) {
		Assert.assertNotNull("Snippet should not be null", snippet);
		Assert.assertNotNull("Diagnostic list should not be null", snippet.getParserDiagnosticList());
		int count = 0;
		for (ParseDiagnostic diagnostic : snippet.getParserDiagnosticList()) {
			if (diagnostic == null || !ParseDiagnostic.Severity.FATAL.equals(diagnostic.severity())) {
				continue;
			}
			if (expectedCode != null && !expectedCode.equals(diagnostic.code())) {
				continue;
			}
			if (expectedMessageFragment != null
					&& (diagnostic.message() == null || !diagnostic.message().contains(expectedMessageFragment))) {
				continue;
			}
			if (expectedTokenFragment != null
					&& (diagnostic.tokenText() == null || !diagnostic.tokenText().contains(expectedTokenFragment))) {
				continue;
			}
			count++;
		}

		return count;
	}

	protected int countDiagnosticsBySeverity(
			Snippet snippet,
			String expectedCode,
			ParseDiagnostic.Severity severity,
			String expectedMessageFragment,
			String expectedTokenFragment) {
		Assert.assertNotNull("Snippet should not be null", snippet);
		Assert.assertNotNull("Diagnostic list should not be null", snippet.getParserDiagnosticList());

		int count = 0;
		for (ParseDiagnostic diagnostic : snippet.getParserDiagnosticList()) {
			if (diagnostic == null || !severity.equals(diagnostic.severity())) {
				continue;
			}
			if (expectedCode != null && !expectedCode.equals(diagnostic.code())) {
				continue;
			}
			if (expectedMessageFragment != null
					&& (diagnostic.message() == null || !diagnostic.message().contains(expectedMessageFragment))) {
				continue;
			}
			if (expectedTokenFragment != null
					&& (diagnostic.tokenText() == null || !diagnostic.tokenText().contains(expectedTokenFragment))) {
				continue;
			}
			count++;
		}

		return count;
	}

	protected void assertFatalDiagnosticCount(
			Snippet snippet,
			String expectedCode,
			String expectedMessageFragment,
			String expectedTokenFragment,
			int expectedCount) {
		int actualCount = countFatalDiagnostics(snippet, expectedCode, expectedMessageFragment, expectedTokenFragment);
		Assert.assertEquals(
				"Unexpected fatal diagnostic count for code=" + expectedCode
						+ " messageFragment=" + expectedMessageFragment
						+ " tokenFragment=" + expectedTokenFragment,
				expectedCount,
				actualCount);
	}

	protected void assertDiagnosticCountBySeverity(
			Snippet snippet,
			String expectedCode,
			ParseDiagnostic.Severity severity,
			String expectedMessageFragment,
			String expectedTokenFragment,
			int expectedCount) {
		int actualCount = countDiagnosticsBySeverity(
				snippet,
				expectedCode,
				severity,
				expectedMessageFragment,
				expectedTokenFragment);
		Assert.assertEquals(
				"Unexpected diagnostic count for code=" + expectedCode
						+ " severity=" + severity
						+ " messageFragment=" + expectedMessageFragment
						+ " tokenFragment=" + expectedTokenFragment,
				expectedCount,
				actualCount);
	}

	/**
	 * Asserts the exact set of diagnostics matching {@code code} and {@code severity},
	 * formatted as a sorted, newline-delimited list so each diagnostic is visible at a glance:
	 * <pre>token=&lt;tokenText&gt; line=&lt;L&gt; char=&lt;C&gt; code=&lt;CODE&gt; severity=&lt;SEV&gt;</pre>
	 * Diagnostics are sorted by line then character position for deterministic comparison.
	 */
	protected void assertDiagnosticListByCodeAndSeverity(
			Snippet snippet,
			String code,
			ParseDiagnostic.Severity severity,
			String expectedSummary) {
		Assert.assertNotNull("Snippet should not be null", snippet);
		Assert.assertNotNull("Diagnostic list should not be null", snippet.getParserDiagnosticList());

		List<ParseDiagnostic> matches = new ArrayList<>();
		for (ParseDiagnostic d : snippet.getParserDiagnosticList()) {
			if (d != null && code.equals(d.code()) && severity.equals(d.severity())) {
				matches.add(d);
			}
		}
		matches.sort(Comparator
				.comparingInt((ParseDiagnostic d) -> d.line() == null ? 0 : d.line())
				.thenComparingInt(d -> d.charPositionInLine() == null ? 0 : d.charPositionInLine()));

		StringBuilder actual = new StringBuilder();
		for (ParseDiagnostic d : matches) {
			if (actual.length() > 0) actual.append("\n");
			actual.append("token=").append(d.tokenText())
			      .append(" line=").append(d.line())
			      .append(" char=").append(d.charPositionInLine())
			      .append(" code=").append(d.code())
			      .append(" severity=").append(d.severity());
		}
		Assert.assertEquals(
				"Diagnostic list for code=" + code + " severity=" + severity,
				expectedSummary,
				actual.toString());
	}

	protected void assertParserErrorsContainExactly(
			ParseErrorCollector parseErrorCollector,
			String... expectedErrors) {
		Assert.assertNotNull("ParseErrorCollector should not be null", parseErrorCollector);
		List<String> actualErrors = parseErrorCollector.getErrorList();
		Assert.assertNotNull("Parser error list should not be null", actualErrors);
		Assert.assertEquals(
				"Unexpected parser error count",
				expectedErrors.length,
				actualErrors.size());

		for (String expectedError : expectedErrors) {
			Assert.assertTrue(
					"Expected parser error entry not found: " + expectedError,
					actualErrors.contains(expectedError));
		}
	}

	// *****************

		
	// *****************************
	// COMMON TEST METHODS

	protected void assertNoFatalErrors(SqlParseEventWalker extractor) {
		Snippet snippet = extractor.getSnippet();
		Assert.assertEquals("Expected no errors but got some: " + snippet.getFatalErrorStringList(), 
				0, snippet.getFatalErrorStringList().size());
	}

	protected void assertNoWalkerDiagnostics(SqlParseEventWalker extractor) {
		Snippet snippet = extractor.getSnippet();
		Assert.assertEquals(
				"Unexpected FATAL diagnostics from Walker/WalkerHelper: "
						+ snippet.getErrorStringList(ParseDiagnostic.Severity.FATAL),
				0,
				snippet.getDiagnosticCountBySeverity(ParseDiagnostic.Severity.FATAL));
		Assert.assertEquals(
				"Unexpected ERROR diagnostics from Walker/WalkerHelper: "
						+ snippet.getErrorStringList(ParseDiagnostic.Severity.ERROR),
				0,
				snippet.getDiagnosticCountBySeverity(ParseDiagnostic.Severity.ERROR));
		Assert.assertEquals(
				"Unexpected SEVERE_WARNING diagnostics from Walker/WalkerHelper: "
						+ snippet.getErrorStringList(ParseDiagnostic.Severity.SEVERE_WARNING),
				0,
				snippet.getDiagnosticCountBySeverity(ParseDiagnostic.Severity.SEVERE_WARNING));
		Assert.assertEquals(
				"Unexpected WARNING diagnostics from Walker/WalkerHelper: "
						+ snippet.getErrorStringList(ParseDiagnostic.Severity.WARNING),
				0,
				snippet.getDiagnosticCountBySeverity(ParseDiagnostic.Severity.WARNING));
		Assert.assertEquals(
				"Unexpected INFO diagnostics from Walker/WalkerHelper: "
						+ snippet.getErrorStringList(ParseDiagnostic.Severity.INFO),
				0,
				snippet.getDiagnosticCountBySeverity(ParseDiagnostic.Severity.INFO));
	}

	protected static final class ParserRunResult {
		private final SqlParseEventWalker extractor;
		private final int parserErrorCount;
		private final List<String> parserErrors;
		private final List<ParseDiagnostic> listenerDiagnostics;
		private final Exception failure;

		private ParserRunResult(
				SqlParseEventWalker extractor,
				int parserErrorCount,
				List<String> parserErrors,
				List<ParseDiagnostic> listenerDiagnostics,
				Exception failure) {
			this.extractor = extractor;
			this.parserErrorCount = parserErrorCount;
			this.parserErrors = parserErrors;
			this.listenerDiagnostics = listenerDiagnostics;
			this.failure = failure;
		}

		public SqlParseEventWalker getExtractor() {
			return extractor;
		}

		public int getParserErrorCount() {
			return parserErrorCount;
		}

		public List<String> getParserErrors() {
			return parserErrors;
		}

		public List<ParseDiagnostic> getListenerDiagnostics() {
			return listenerDiagnostics;
		}

		public Exception getFailure() {
			return failure;
		}
	}

	protected List<ParseDiagnostic> collectParserListenerDiagnostics(SQLSelectParserParser parser) {
		List<ParseDiagnostic> diagnostics = new ArrayList<>();
		List<?> listeners = parser.getErrorListeners();
		for (Object listener : listeners) {
			if (listener instanceof ParseErrorListener parseErrorListener) {
				diagnostics.addAll(parseErrorListener.getDiagnostics());
			}
		}
		return diagnostics;
	}

	protected ParserRunResult runSQLParsertestAllowErrors(final String query, final SQLSelectParserParser parser,
			HashMap<String, String> entityMap, HashMap<String, Map<String, String>> attributeMap) {
		SqlParseEventWalker extractor = null;
		int parserErrorCount = 0;
		List<String> parserErrors = new ArrayList<>();
		List<ParseDiagnostic> listenerDiagnostics;
		Exception failure = null;

		try {
			SqlContext tree = parser.sql();
			ParseErrorCollector collector = (ParseErrorCollector) parser.getErrorHandler();
			if (collector != null) {
				parserErrorCount = collector.getErrorCount();
				parserErrors.addAll(collector.getErrorList());
			}

			listenerDiagnostics = collectParserListenerDiagnostics(parser);
			extractor = runAnyParsertest(query, parser, tree, entityMap, attributeMap, true);
		} catch (Exception ex) {
			failure = ex;
			ParseErrorCollector collector = (ParseErrorCollector) parser.getErrorHandler();
			if (collector != null) {
				parserErrorCount = collector.getErrorCount();
				parserErrors.addAll(collector.getErrorList());
			}
			listenerDiagnostics = collectParserListenerDiagnostics(parser);
		}

		return new ParserRunResult(extractor, parserErrorCount, parserErrors, listenerDiagnostics, failure);
	}

	protected SqlParseEventWalker runParsertest(final String query, final SQLSelectParserParser parser) {
		return runSQLParsertest(query, parser, null, null);
	}

	protected SqlParseEventWalker runSQLParsertest(final String query, final SQLSelectParserParser parser,
			HashMap<String, String> entityMap, HashMap<String, Map<String, String>> attributeMap) {
		try {
			System.out.println();
			// There should be zero errors
			SqlContext tree = parser.sql();
			ParseErrorCollector v = (ParseErrorCollector) parser.getErrorHandler();
			
	        final int numErrors = v.getErrorCount();
			Assert.assertEquals("Expected no failures with " + query + " but got " + v.getErrorList(), 
				0, numErrors);

			return runAnyParsertest(query, parser, tree, entityMap, attributeMap, true);

		} catch (RecognitionException e) {
			System.err.println("Exception parsing eqn: " + query);
			System.err.println("Recognition Exception: " + e.getMessage());
			ParseErrorCollector v = (ParseErrorCollector) parser.getErrorHandler();
			System.err.println("Parse errors: " + v.getErrorList());
				// check for Syntax Errors Captured by the Listeners
				List<?> listeners = parser.getErrorListeners();
				for (Object listener : listeners) {
					if (listener instanceof ParseErrorListener parseErrorListener){
						System.out.println(listener.getClass().getName() + " Diagnostics: " + parseErrorListener.getDiagnostics());
					}
				}
			}
		return null;
	}


	protected SqlParseEventWalker runColumnParsertest(final String query, final SQLSelectParserParser parser) {
		try {
			System.out.println();
			// There should be zero errors
			Column_valueContext tree = parser.column_value();
			ParseErrorCollector v = (ParseErrorCollector) parser.getErrorHandler();
			
	        final int numErrors = v.getErrorCount();
			Assert.assertEquals("Expected no failures with " + query + " but got " + v.getErrorList(), 
				0, numErrors);

			return runAnyParsertest(query, parser, tree, null, null, true);

		} catch (RecognitionException e) {
			System.err.println("Exception parsing eqn: " + query);
			System.err.println("Recognition Exception: " + e.getMessage());
			ParseErrorCollector v = (ParseErrorCollector) parser.getErrorHandler();
			System.err.println(v.getErrorList());
		}
		return null;
	}


	protected SqlParseEventWalker runPredicandParsertest(final String query, final SQLSelectParserParser parser) {
		try {
			System.out.println();
			// There should be zero errors
			Predicand_valueContext tree = parser.predicand_value();
			ParseErrorCollector v = (ParseErrorCollector) parser.getErrorHandler();
			
	        final int numErrors = v.getErrorCount();
			Assert.assertEquals("Expected no failures with " + query + " but got " + v.getErrorList(), 
				0, numErrors);

			return runAnyParsertest(query, parser, tree, null, null, true);

		} catch (RecognitionException e) {
			System.err.println("Exception parsing eqn: " + query);
			System.err.println("Recognition Exception: " + e.getMessage());
			ParseErrorCollector v = (ParseErrorCollector) parser.getErrorHandler();
			System.err.println(v.getErrorList());
		}
		return null;
	}


	
	protected SqlParseEventWalker runInListPredicateParsertest(final String query, final SQLSelectParserParser parser) {
		try {
			System.out.println();
			// There should be zero errors
			In_list_predicate_valueContext tree = parser.in_list_predicate_value();
			ParseErrorCollector v = (ParseErrorCollector) parser.getErrorHandler();
			
	        final int numErrors = v.getErrorCount();
			Assert.assertEquals("Expected no failures with " + query + " but got " + v.getErrorList(), 
				0, numErrors);

			return runAnyParsertest(query, parser, tree, null, null, true);

		} catch (RecognitionException e) {
			System.err.println("Exception parsing eqn: " + query);
			System.err.println("Recognition Exception: " + e.getMessage());
			ParseErrorCollector v = (ParseErrorCollector) parser.getErrorHandler();
			System.err.println(v.getErrorList());
		}
		return null;
		}
	

	protected SqlParseEventWalker runConditionParsertest(final String query, final SQLSelectParserParser parser) {
		try {
			System.out.println();
			// There should be zero errors
			Condition_valueContext tree = parser.condition_value();
			ParseErrorCollector v = (ParseErrorCollector) parser.getErrorHandler();
			
			final int numErrors = v.getErrorCount();
			Assert.assertEquals("Expected no failures with " + query + " but got " + v.getErrorList(), 
					0, numErrors);
		
			return runAnyParsertest(query, parser, tree, null, null, true);

		} catch (RecognitionException e) {
			System.err.println("Exception parsing eqn: " + query);
			System.err.println("Recognition Exception: " + e.getMessage());
			ParseErrorCollector v = (ParseErrorCollector) parser.getErrorHandler();
			System.err.println(v.getErrorList());
		}
		return null;
	}
	
	protected SqlParseEventWalker runTupleParsertest(final String query, final SQLSelectParserParser parser) {
		try {
			System.out.println();
			// There should be zero errors
			Tuple_valueContext tree = parser.tuple_value();
			ParseErrorCollector v = (ParseErrorCollector) parser.getErrorHandler();
			
			final int numErrors = v.getErrorCount();
			Assert.assertEquals("Expected no failures with " + query + " but got " + v.getErrorList(), 
				0, numErrors);
	
			return runAnyParsertest(query, parser, tree, null, null, true);

		} catch (RecognitionException e) {
			System.err.println("Exception parsing eqn: " + query);
			System.err.println("Recognition Exception: " + e.getMessage());
			ParseErrorCollector v = (ParseErrorCollector) parser.getErrorHandler();
			System.err.println(v.getErrorList());
		}
		return null;
	}
	
	
		
	protected SqlParseEventWalker runValuesStatementEndParsertest(final String query, final SQLSelectParserParser parser) {
		try {
			System.out.println();
			// There should be zero errors
			Values_statement_endContext tree = parser.values_statement_end();
			ParseErrorCollector v = (ParseErrorCollector) parser.getErrorHandler();
			
			final int numErrors = v.getErrorCount();
			Assert.assertEquals("Expected no failures with " + query + " but got " + v.getErrorList(), 
				0, numErrors);
		
			return runAnyParsertest(query, parser, tree, null, null, true);

		} catch (RecognitionException e) {
			System.err.println("Exception parsing eqn: " + query);
			System.err.println("Recognition Exception: " + e.getMessage());
			ParseErrorCollector v = (ParseErrorCollector) parser.getErrorHandler();
			System.err.println(v.getErrorList());
		}
		return null;
	}	

	protected SqlParseEventWalker runUpdateEndPointParsertest(final String query, final SQLSelectParserParser parser) {
		try {
			System.out.println();
			// There should be zero errors
			Update_end_pointContext tree = parser.update_end_point();
			ParseErrorCollector v = (ParseErrorCollector) parser.getErrorHandler();

			final int numErrors = v.getErrorCount();
			Assert.assertEquals("Expected no failures with " + query + " but got " + v.getErrorList(),
				0, numErrors);

			return runAnyParsertest(query, parser, tree, null, null, true);

		} catch (RecognitionException e) {
			System.err.println("Exception parsing eqn: " + query);
			System.err.println("Recognition Exception: " + e.getMessage());
			ParseErrorCollector v = (ParseErrorCollector) parser.getErrorHandler();
			System.err.println(v.getErrorList());
		}
		return null;
	}

	protected SqlParseEventWalker runDeleteEndPointParsertest(final String query, final SQLSelectParserParser parser) {
		try {
			System.out.println();
			// There should be zero errors
			Delete_end_pointContext tree = parser.delete_end_point();
			ParseErrorCollector v = (ParseErrorCollector) parser.getErrorHandler();

			final int numErrors = v.getErrorCount();
			Assert.assertEquals("Expected no failures with " + query + " but got " + v.getErrorList(),
				0, numErrors);

			return runAnyParsertest(query, parser, tree, null, null, true);

		} catch (RecognitionException e) {
			System.err.println("Exception parsing eqn: " + query);
			System.err.println("Recognition Exception: " + e.getMessage());
			ParseErrorCollector v = (ParseErrorCollector) parser.getErrorHandler();
			System.err.println(v.getErrorList());
		}
		return null;
	}

	protected SqlParseEventWalker runTruncateEndPointParsertest(final String query, final SQLSelectParserParser parser) {
		try {
			System.out.println();
			// There should be zero errors
			Truncate_end_pointContext tree = parser.truncate_end_point();
			ParseErrorCollector v = (ParseErrorCollector) parser.getErrorHandler();

			final int numErrors = v.getErrorCount();
			Assert.assertEquals("Expected no failures with " + query + " but got " + v.getErrorList(),
				0, numErrors);

			return runAnyParsertest(query, parser, tree, null, null, true);

		} catch (RecognitionException e) {
			System.err.println("Exception parsing eqn: " + query);
			System.err.println("Recognition Exception: " + e.getMessage());
			ParseErrorCollector v = (ParseErrorCollector) parser.getErrorHandler();
			System.err.println(v.getErrorList());
		}
		return null;
	}
	
		
	protected SqlParseEventWalker runQueryParsertest(final String query, final SQLSelectParserParser parser) {
		try {
			System.out.println();
			// There should be zero errors
			Query_valueContext tree = parser.query_value();
			ParseErrorCollector v = (ParseErrorCollector) parser.getErrorHandler();
			
			final int numErrors = v.getErrorCount();
			Assert.assertEquals("Expected no failures with " + query + " but got " + v.getErrorList(), 
				0, numErrors);
		
			return runAnyParsertest(query, parser, tree, null, null, true);

		} catch (RecognitionException e) {
			System.err.println("Exception parsing eqn: " + query);
			System.err.println("Recognition Exception: " + e.getMessage());
			ParseErrorCollector v = (ParseErrorCollector) parser.getErrorHandler();
			System.err.println(v.getErrorList());
		}
		return null;
	}
		
	protected SqlParseEventWalker runJoinExtensionParsertest(final String query, final SQLSelectParserParser parser) {
		try {
			System.out.println();
			// There should be zero errors
			Join_extension_valueContext tree = parser.join_extension_value();
			ParseErrorCollector v = (ParseErrorCollector) parser.getErrorHandler();
			
			final int numErrors = v.getErrorCount();
			Assert.assertEquals("Expected no failures with " + query + " but got " + v.getErrorList(), 
				0, numErrors);
		
			return runAnyParsertest(query, parser, tree, null, null, false);

		} catch (RecognitionException e) {
			System.err.println("Exception parsing eqn: " + query);
			System.err.println("Recognition Exception: " + e.getMessage());
			ParseErrorCollector v = (ParseErrorCollector) parser.getErrorHandler();
			System.err.println(v.getErrorList());
		}
		return null;
	}

	protected SqlParseEventWalker runScriptParsertest(final String query, final SQLSelectParserParser parser) {
		try {
			System.out.println();
			// There should be zero errors
			ScriptContext tree = parser.script();
			ParseErrorCollector v = (ParseErrorCollector) parser.getErrorHandler();

			final int numErrors = v.getErrorCount();
			Assert.assertEquals("Expected no failures with " + query + " but got " + v.getErrorList(),
					0, numErrors);

			return runAnyParsertest(query, parser, tree, null, null, true);

		} catch (RecognitionException e) {
			System.err.println("Exception parsing eqn: " + query);
			System.err.println("Recognition Exception: " + e.getMessage());
			ParseErrorCollector v = (ParseErrorCollector) parser.getErrorHandler();
			System.err.println(v.getErrorList());
		}
		return null;
	}

	protected SqlParseEventWalker runDdlParsertest(final String query, final SQLSelectParserParser parser) {
		try {
			System.out.println();
			// There should be zero errors
			DdlContext tree = parser.ddl();
			ParseErrorCollector v = (ParseErrorCollector) parser.getErrorHandler();

			final int numErrors = v.getErrorCount();
			Assert.assertEquals("Expected no failures with " + query + " but got " + v.getErrorList(),
					0, numErrors);

			return runAnyParsertest(query, parser, tree, null, null, true);

		} catch (RecognitionException e) {
			System.err.println("Exception parsing eqn: " + query);
			System.err.println("Recognition Exception: " + e.getMessage());
			ParseErrorCollector v = (ParseErrorCollector) parser.getErrorHandler();
			System.err.println(v.getErrorList());
		}
		return null;
	}
	

	protected SqlParseEventWalker runAnyParsertest(final String query, final SQLSelectParserParser parser, 
		ParserRuleContext tree,
		HashMap<String, String> entityMap, HashMap<String, Map<String, String>> attributeMap,
		boolean getInterface) {
		try {	
			SqlParseEventWalker extractor = new SqlParseEventWalker();
			if (entityMap != null)
				extractor.setEntityTableNameMap(entityMap);
			if (attributeMap != null)
				extractor.setAttributeColumnMap(attributeMap);

			// walk the tree and extract the SQL USING THE CUSTOM Extractor
			ParseTreeWalker.DEFAULT.walk(extractor, tree);
			System.out.println("Result: " + extractor.getAsTree());
			if (getInterface) {
				System.out.println("Interface: " + extractor.getInterface());
			} else {
				System.out.println("No Interface requested");
			}
			System.out.println("Symbol Tree: " + extractor.getSymbolTable());
			System.out.println("Table Dictionary: " + extractor.getTableColumnDictionaryMap());
			System.out.println("Query Column Dictionary: " + extractor.getQueryColumnDictionaryMap());
			System.out.println("Substitution Variables: " + extractor.getSubstitutionsMap());

			ParseErrorCollector v = (ParseErrorCollector) parser.getErrorHandler();
			System.out.println("Parser Errors: " + v.getErrorList());

			Snippet snippet = extractor.getSnippet();
			HashMap<String, Object> enrichedArrayCollectors = enrichArrayCollectorsForTest(
					extractor.getArrayOutputCollectorsMap(), parser, snippet, tree);
			System.out.println("Array Output Collectors: " + enrichedArrayCollectors);
			System.out.println("Walker Fatal Errors: " + snippet.getFatalErrorStringList());
			System.out.println("Walker Non Fatal Errors: " + snippet.getErrorStringList(ParseDiagnostic.Severity.ERROR));
			System.out.println("Walker Severe Warnings: " + snippet.getErrorStringList(ParseDiagnostic.Severity.SEVERE_WARNING));
			System.out.println("Walker Warnings: " + snippet.getErrorStringList(ParseDiagnostic.Severity.WARNING));
			System.out.println("Walker Info: " + snippet.getErrorStringList(ParseDiagnostic.Severity.INFO));
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
			System.err.println("Recognition Exception: " + e.getMessage());

			ParseErrorCollector v = (ParseErrorCollector) parser.getErrorHandler();
			System.out.println("Parser Errors: " + v.getErrorList());

			// check for Syntax Errors Captured by the Listeners
			List<?> listeners = parser.getErrorListeners();
			for (Object listener : listeners) {
				if (listener instanceof ParseErrorListener parseErrorListener){
					System.out.println(listener.getClass().getName() + " Diagnostics: " + parseErrorListener.getDiagnostics());
				}
			}
			
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	private HashMap<String, Object> enrichArrayCollectorsForTest(
			HashMap<String, Object> baseCollectors,
			SQLSelectParserParser parser,
			Snippet snippet,
			ParserRuleContext tree) {
		if (baseCollectors == null || baseCollectors.isEmpty()) {
			return baseCollectors;
		}

		Map<String, StatementLineRange> statementLineRanges = getScriptStatementLineRanges(tree);

		HashMap<String, Object> enriched = new LinkedHashMap<>();
		for (Map.Entry<String, Object> topEntry : baseCollectors.entrySet()) {
			Object topValue = topEntry.getValue();
			if (!(topValue instanceof Map<?, ?> topMapObj)) {
				enriched.put(topEntry.getKey(), topValue);
				continue;
			}

			Map<String, Object> topMap = (Map<String, Object>) topMapObj;
			boolean hasNumberedChildren = false;
			for (Map.Entry<String, Object> childEntry : topMap.entrySet()) {
				if (childEntry.getKey() != null
						&& childEntry.getKey().matches("\\d+")
						&& childEntry.getValue() instanceof Map<?, ?>) {
					hasNumberedChildren = true;
					break;
				}
			}

			if (hasNumberedChildren) {
				HashMap<String, Object> enrichedTopMap = new LinkedHashMap<>();
				for (Map.Entry<String, Object> childEntry : topMap.entrySet()) {
					String childKey = childEntry.getKey();
					Object childValue = childEntry.getValue();
					if (childKey != null
							&& childKey.matches("\\d+")
							&& childValue instanceof Map<?, ?> childMapObj) {
						StatementLineRange range = statementLineRanges.get(childKey);
						HashMap<String, Object> diagnosticsPayload = buildDiagnosticsPayloadForRange(parser, snippet, range);
						HashMap<String, Object> queryEntry = new LinkedHashMap<>((Map<String, Object>) childMapObj);
						if (isEmptyValue(queryEntry.get("queryInterface"))) {
							queryEntry.remove("queryInterface");
						}
						for (Map.Entry<String, Object> payloadEntry : diagnosticsPayload.entrySet()) {
							if (!queryEntry.containsKey(payloadEntry.getKey())
									&& !isEmptyValue(payloadEntry.getValue())) {
								queryEntry.put(payloadEntry.getKey(), payloadEntry.getValue());
							}
						}
						queryEntry.entrySet().removeIf(entry -> isEmptyValue(entry.getValue()));
						if (!queryEntry.isEmpty()) {
							enrichedTopMap.put(childKey, queryEntry);
						}
					} else {
						enrichedTopMap.put(childKey, childValue);
					}
				}
				enriched.put(topEntry.getKey(), enrichedTopMap);
			} else {
				HashMap<String, Object> diagnosticsPayload = buildDiagnosticsPayloadForRange(parser, snippet, null);
				HashMap<String, Object> singleEntry = new LinkedHashMap<>(topMap);
				if (isEmptyValue(singleEntry.get("queryInterface"))) {
					singleEntry.remove("queryInterface");
				}
				for (Map.Entry<String, Object> payloadEntry : diagnosticsPayload.entrySet()) {
					if (!singleEntry.containsKey(payloadEntry.getKey())
							&& !isEmptyValue(payloadEntry.getValue())) {
						singleEntry.put(payloadEntry.getKey(), payloadEntry.getValue());
					}
				}
				singleEntry.entrySet().removeIf(entry -> isEmptyValue(entry.getValue()));
				enriched.put(topEntry.getKey(), singleEntry);
			}
		}

		return enriched;
	}

	private HashMap<String, Object> buildDiagnosticsPayloadForRange(
			SQLSelectParserParser parser,
			Snippet snippet,
			StatementLineRange range) {
		HashMap<String, Object> payload = new LinkedHashMap<>();

		List<ParseDiagnostic> mergedDiagnostics = (snippet == null) ? null : snippet.getParserDiagnosticList();
		List<String> parserErrors = collectDiagnosticMessages(
				mergedDiagnostics,
				Set.of(ParseDiagnostic.Severity.FATAL, ParseDiagnostic.Severity.ERROR, ParseDiagnostic.Severity.SEVERE_WARNING),
				range,
				true,
				false);
		putIfNotEmpty(payload, "ParserErrors", parserErrors);

		putIfNotEmpty(payload, "Walker Fatal Errors", collectDiagnosticMessages(
				mergedDiagnostics,
				Set.of(ParseDiagnostic.Severity.FATAL),
				range,
				false,
				true));
		putIfNotEmpty(payload, "Walker Non Fatal Errors", collectDiagnosticMessages(
				mergedDiagnostics,
				Set.of(ParseDiagnostic.Severity.ERROR),
				range,
				false,
				true));
		putIfNotEmpty(payload, "Walker Severe Warnings", collectDiagnosticMessages(
				mergedDiagnostics,
				Set.of(ParseDiagnostic.Severity.SEVERE_WARNING),
				range,
				false,
				true));
		putIfNotEmpty(payload, "Walker Warnings", collectDiagnosticMessages(
				mergedDiagnostics,
				Set.of(ParseDiagnostic.Severity.WARNING),
				range,
				false,
				true));
		putIfNotEmpty(payload, "Walker Info", collectDiagnosticMessages(
				mergedDiagnostics,
				Set.of(ParseDiagnostic.Severity.INFO),
				range,
				false,
				true));

		List<ParseDiagnostic> listenerDiagnostics = new ArrayList<>();
		if (parser != null) {
			List<?> listeners = parser.getErrorListeners();
			for (Object listener : listeners) {
				if (listener instanceof ParseErrorListener parseErrorListener
						&& parseErrorListener.getDiagnostics() != null
						&& !parseErrorListener.getDiagnostics().isEmpty()) {
					listenerDiagnostics.addAll(parseErrorListener.getDiagnostics());
				}
			}
		}
		putIfNotEmpty(payload, "errorhandling.ParseErrorListener found Diagnostics",
				filterDiagnosticsByRange(listenerDiagnostics, range));

		return payload;
	}

	private List<String> collectDiagnosticMessages(
			List<ParseDiagnostic> diagnostics,
			Set<ParseDiagnostic.Severity> severities,
			StatementLineRange range,
			boolean parserOnly,
			boolean walkerOnly) {
		List<String> messages = new ArrayList<>();
		if (diagnostics == null || diagnostics.isEmpty()) {
			return messages;
		}
		for (ParseDiagnostic diagnostic : diagnostics) {
			if (diagnostic == null) {
				continue;
			}
			if (severities != null && !severities.contains(diagnostic.severity())) {
				continue;
			}
			if (parserOnly && !isParserDiagnostic(diagnostic)) {
				continue;
			}
			if (walkerOnly && !isWalkerDiagnostic(diagnostic)) {
				continue;
			}
			if (!matchesRange(diagnostic, range)) {
				continue;
			}
			String message = diagnostic.message();
			if (message != null && !message.isBlank() && !messages.contains(message)) {
				messages.add(message);
			}
		}
		return messages;
	}

	private List<ParseDiagnostic> filterDiagnosticsByRange(
			List<ParseDiagnostic> diagnostics,
			StatementLineRange range) {
		List<ParseDiagnostic> filtered = new ArrayList<>();
		if (diagnostics == null || diagnostics.isEmpty()) {
			return filtered;
		}
		for (ParseDiagnostic diagnostic : diagnostics) {
			if (diagnostic != null && matchesRange(diagnostic, range)) {
				filtered.add(diagnostic);
			}
		}
		return filtered;
	}

	private boolean matchesRange(ParseDiagnostic diagnostic, StatementLineRange range) {
		if (diagnostic == null || range == null) {
			return true;
		}
		Integer line = diagnostic.line();
		if (line == null || line <= 0) {
			return false;
		}
		return line >= range.startLine && line <= range.endLine;
	}

	private boolean isParserDiagnostic(ParseDiagnostic diagnostic) {
		if (diagnostic == null) {
			return false;
		}
		String source = diagnostic.source();
		return "ParseErrorCollector".equals(source)
				|| "ParseErrorListener".equals(source)
				|| "SqlParserAccess".equals(source)
				|| "SqlParseMCP".equals(source);
	}

	private boolean isWalkerDiagnostic(ParseDiagnostic diagnostic) {
		if (diagnostic == null) {
			return false;
		}
		String source = diagnostic.source();
		return source != null && source.contains("SqlASTWalker");
	}

	private Map<String, StatementLineRange> getScriptStatementLineRanges(ParserRuleContext tree) {
		if (!(tree instanceof ScriptContext scriptCtx)) {
			return Map.of();
		}
		Map<String, StatementLineRange> ranges = new LinkedHashMap<>();
		List<Sql_statementContext> statements = scriptCtx.sql_statement();
		for (int i = 0; i < statements.size(); i++) {
			Sql_statementContext statement = statements.get(i);
			if (statement == null) {
				continue;
			}
			Token start = statement.getStart();
			Token stop = statement.getStop();
			if (start == null) {
				continue;
			}
			int startLine = start.getLine();
			int endLine = (stop == null || stop.getLine() <= 0) ? startLine : stop.getLine();
			ranges.put(Integer.toString(i + 1), new StatementLineRange(startLine, endLine));
		}
		return ranges;
	}

	private static final class StatementLineRange {
		private final int startLine;
		private final int endLine;

		private StatementLineRange(int startLine, int endLine) {
			this.startLine = startLine;
			this.endLine = endLine;
		}
	}

	private void putIfNotEmpty(HashMap<String, Object> target, String key, Object value) {
		if (target == null || key == null || isEmptyValue(value)) {
			return;
		}
		target.put(key, value);
	}

	private boolean isEmptyValue(Object value) {
		if (value == null) {
			return true;
		}
		if (value instanceof String stringValue) {
			return stringValue.isBlank();
		}
		if (value instanceof List<?> listValue) {
			return listValue.isEmpty();
		}
		if (value instanceof Map<?, ?> mapValue) {
			return mapValue.isEmpty();
		}
		return false;
	}

	protected List<String> runExpectSQLParserFailuretest(final String query, final SQLSelectParserParser parser) {
		List<String> errorList = new ArrayList<>();
		try {
			System.out.println();
			// There should be parser errors
			SqlContext tree = parser.sql();

			// check for Syntax Errors Captured by the Listeners
			List<?> listeners = parser.getErrorListeners();
			for (Object listener : listeners) {
				if (listener instanceof ParseErrorListener parseErrorListener){
					for (ParseDiagnostic item : parseErrorListener.getDiagnostics()) {
						errorList.add(item.toString());
					}					
					System.out.println(listener.getClass().getName() 
						+ " found Diagnostics: " 
						+ parseErrorListener.getDiagnostics());
							}}
			// check for Syntax Errors Captured by the ParseErrorCollector
			ParseErrorCollector v = (ParseErrorCollector) parser.getErrorHandler();
			errorList.addAll(v.getErrorList());
			int numErrors = v.getErrorCount();
			System.out.println("Expected Syntax Failures for: " + query);
			System.out.println("There were "+ numErrors + " errors: "+ v.getErrorList());

			Assert.assertNotEquals("Expected " + numErrors + " for " + query, 0, numErrors);
		} catch (RecognitionException e) {
			System.err.println("No Errors Found When There Should Have Been: " + query);
		}
		return errorList;
	}



	/*
	 * method generates a parser object for each test, then passes the query to it.
	 * This is to ensure that the parser is always fresh and does not carry over
	 * state from previous tests.
	 * The returned parser is used to parse the query and extract the
	 */
	protected static final SQLSelectParserParser parse(final String query) {

		SQLSelectParserFactory factory = new SQLSelectParserFactory();
		return factory.buildParser(query);
	}

}
