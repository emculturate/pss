package sql.walker;

import org.antlr.v4.runtime.RecognitionException;
import org.junit.Assert;
import org.junit.Test;

import access.Snippet;
import access.SqlParserAccess;
import astwalkers.SqlASTWalkerHelper;
import errorhandling.ParseDiagnostic;
import static mumble.SQLParserEndPoints.SQLPARSER_COLUMN_TREE_KEY;
import static mumble.SQLParserEndPoints.SQLPARSER_CONDITION_TREE_KEY;
import static mumble.SQLParserEndPoints.SQLPARSER_DELETE_TREE_KEY;
import static mumble.SQLParserEndPoints.SQLPARSER_INSERT_TREE_KEY;
import static mumble.SQLParserEndPoints.SQLPARSER_IN_LIST_TREE_KEY;
import static mumble.SQLParserEndPoints.SQLPARSER_JOIN_EXTENSION_TREE_KEY;
import static mumble.SQLParserEndPoints.SQLPARSER_PREDICAND_TREE_KEY;
import static mumble.SQLParserEndPoints.SQLPARSER_QUERY_TREE_KEY;
import static mumble.SQLParserEndPoints.SQLPARSER_SQL_TREE_KEY;
import static mumble.SQLParserEndPoints.SQLPARSER_TRUNCATE_TREE_KEY;
import static mumble.SQLParserEndPoints.SQLPARSER_TUPLE_TREE_KEY;
import static mumble.SQLParserEndPoints.SQLPARSER_UPDATE_TREE_KEY;
import static mumble.SQLParserEndPoints.SQLPARSER_VALUES_TREE_KEY;

/**
 * Tests for the SqlParseEventWalker with access object.
 * This class contains tests for parsing SQL queries using the SqlParserAccess class.
 * It verifies the abstract syntax tree (AST), interface, symbol table, table dictionary,
 * and substitution variables for various SQL statements.
 */
public class SqlParseEventWalkerWithAccessObjectTest {

	private SqlParserAccess lastAccessObject;

	private void assertUnresolvedUnknownColumnsDiagnostic(
			Snippet snippet,
			int expectedLine,
			int expectedCharPositionInLine,
			ParseDiagnostic.Severity expectedSeverity,
			String expectedColumnNameInMessage) {
		Assert.assertNotNull("Snippet should not be null", snippet);
		Assert.assertNotNull("Diagnostic list should not be null", snippet.getParserDiagnosticList());

		ParseDiagnostic unresolvedUnknown = null;
		for (ParseDiagnostic diagnostic : snippet.getParserDiagnosticList()) {
			if (diagnostic != null
					&& "UNRESOLVED_UNQUALIFIED_COLUMNS".equals(diagnostic.code())
					&& ((diagnostic.tokenText() != null
							&& diagnostic.tokenText().contains(expectedColumnNameInMessage))
						|| (diagnostic.message() != null
								&& diagnostic.message().contains(expectedColumnNameInMessage + " (l:")))) {
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

	private ParseDiagnostic findFatalDiagnosticByCode(Snippet snippet, String code) {
		Assert.assertNotNull("Snippet should not be null", snippet);
		Assert.assertNotNull("Diagnostic list should not be null", snippet.getParserDiagnosticList());
		for (ParseDiagnostic diagnostic : snippet.getParserDiagnosticList()) {
			if (diagnostic != null
					&& ParseDiagnostic.Severity.FATAL.equals(diagnostic.severity())
					&& code.equals(diagnostic.code())) {
				return diagnostic;
			}
		}
		return null;
	}

	private ParseDiagnostic findDiagnosticByCodeAndSeverity(
			Snippet snippet,
			String code,
			ParseDiagnostic.Severity severity) {
		Assert.assertNotNull("Snippet should not be null", snippet);
		Assert.assertNotNull("Diagnostic list should not be null", snippet.getParserDiagnosticList());
		for (ParseDiagnostic diagnostic : snippet.getParserDiagnosticList()) {
			if (diagnostic != null
					&& severity.equals(diagnostic.severity())
					&& code.equals(diagnostic.code())) {
				return diagnostic;
			}
		}
		return null;
	}

	private void assertFatalDiagnosticByCode(
			Snippet snippet,
			String expectedCode,
			String expectedMessageFragment,
			String expectedTokenFragment) {
		ParseDiagnostic diagnostic = findFatalDiagnosticByCode(snippet, expectedCode);
		Assert.assertNotNull("Expected fatal diagnostic with code " + expectedCode, diagnostic);

		if (expectedMessageFragment != null) {
			Assert.assertTrue(
					"Diagnostic message should contain '" + expectedMessageFragment + "'",
					diagnostic.message() != null && diagnostic.message().contains(expectedMessageFragment));
		}

		if (expectedTokenFragment != null) {
			Assert.assertTrue(
					"Diagnostic token text should contain '" + expectedTokenFragment + "'",
					diagnostic.tokenText() != null && diagnostic.tokenText().contains(expectedTokenFragment));
		}
	}

	private void assertDiagnosticByCode(
			Snippet snippet,
			String expectedCode,
			ParseDiagnostic.Severity expectedSeverity,
			String expectedMessageFragment,
			String expectedTokenFragment) {
		ParseDiagnostic diagnostic = findDiagnosticByCodeAndSeverity(snippet, expectedCode, expectedSeverity);
		Assert.assertNotNull(
				"Expected diagnostic with code " + expectedCode + " and severity " + expectedSeverity,
				diagnostic);

		if (expectedMessageFragment != null) {
			Assert.assertTrue(
					"Diagnostic message should contain '" + expectedMessageFragment + "'",
					diagnostic.message() != null && diagnostic.message().contains(expectedMessageFragment));
		}

		if (expectedTokenFragment != null) {
			Assert.assertTrue(
					"Diagnostic token text should contain '" + expectedTokenFragment + "'",
					diagnostic.tokenText() != null && diagnostic.tokenText().contains(expectedTokenFragment));
		}
	}

	private void assertDiagnosticAtPosition(
			Snippet snippet,
			String code,
			ParseDiagnostic.Severity severity,
			String expectedMessageFragment,
			String expectedTokenFragment,
			int expectedLine,
			int expectedCharPositionInLine) {
		Assert.assertNotNull("Snippet should not be null", snippet);
		Assert.assertNotNull("Diagnostic list should not be null", snippet.getParserDiagnosticList());

		ParseDiagnostic matched = null;
		for (ParseDiagnostic diagnostic : snippet.getParserDiagnosticList()) {
			if (diagnostic == null || !severity.equals(diagnostic.severity()) || !code.equals(diagnostic.code())) {
				continue;
			}
			if (!Integer.valueOf(expectedLine).equals(diagnostic.line())
					|| !Integer.valueOf(expectedCharPositionInLine).equals(diagnostic.charPositionInLine())) {
				continue;
			}
			matched = diagnostic;
			break;
		}

		Assert.assertNotNull("Expected diagnostic with code " + code + " and severity " + severity, matched);
		Assert.assertNotNull("Expected diagnostic line", matched.line());
		Assert.assertNotNull("Expected diagnostic character position", matched.charPositionInLine());
		Assert.assertEquals("Unexpected diagnostic line", Integer.valueOf(expectedLine), matched.line());
		Assert.assertEquals(
				"Unexpected diagnostic character position",
				Integer.valueOf(expectedCharPositionInLine),
				matched.charPositionInLine());

		if (expectedMessageFragment != null) {
			Assert.assertTrue(
					"Diagnostic message should contain '" + expectedMessageFragment + "'",
					matched.message() != null && matched.message().contains(expectedMessageFragment));
		}

		if (expectedTokenFragment != null) {
			Assert.assertTrue(
					"Diagnostic token text should contain '" + expectedTokenFragment + "'",
					matched.tokenText() != null && matched.tokenText().contains(expectedTokenFragment));
		}
	}

	private int countFatalDiagnostics(
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

	private int countDiagnosticsBySeverity(
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

	private int countDiagnosticsBySeverityCodeAndSource(
			Snippet snippet,
			ParseDiagnostic.Severity severity,
			String expectedCode,
			String expectedSource) {
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
			if (expectedSource != null && !expectedSource.equals(diagnostic.source())) {
				continue;
			}
			count++;
		}

		return count;
	}

	private void assertFatalDiagnosticCount(
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

	private void assertDiagnosticCountBySeverity(
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

	private void assertDiagnosticCountBySeverityCodeAndSource(
			Snippet snippet,
			ParseDiagnostic.Severity severity,
			String expectedCode,
			String expectedSource,
			int expectedCount) {
		int actualCount = countDiagnosticsBySeverityCodeAndSource(
				snippet,
				severity,
				expectedCode,
				expectedSource);
		Assert.assertEquals(
				"Unexpected diagnostic count for severity=" + severity
						+ " code=" + expectedCode
						+ " source=" + expectedSource,
				expectedCount,
				actualCount);
	}

   
	@Test
	public void basicSelectSyntaxFailureTest1() {
		final String query = "select from";
		final Snippet snippet = runFailedSyntaxSQLParserTest(query, SQLPARSER_SQL_TREE_KEY, 2);
				
		String text = snippet.getFatalErrorStringList().get(0);
		Assert.assertTrue("Expected a syntax error with " + query,
			text.equals("Line 1:7 - null - unexpected input: 'from'"));

		text = snippet.getFatalErrorStringList().get(1);
		Assert.assertTrue("Expected a syntax error with " + query,
					text.equals("Exception when walking the parse tree: Cannot "
					+ "invoke \"java.util.Map.remove(Object)\" because \"subMap\" is null")
			);
	}				

   
	@Test
	public void basicSelectSyntaxFailureTest2() {
		final String query = "not a sql statement at all";
		final Snippet snippet = runFailedSyntaxSQLParserTest(query, SQLPARSER_SQL_TREE_KEY, 2);
				
		String text = snippet.getFatalErrorStringList().get(0);
		Assert.assertTrue("Expected a syntax error with " + query,
			text.equals("Line 1:0 - null - unexpected input: 'not'"));

		text = snippet.getFatalErrorStringList().get(1);
		Assert.assertTrue("Expected a syntax error with " + query,
					text.equals("Exception when walking the parse tree: Cannot "
					+ "invoke \"java.util.Map.remove(Object)\" because \"subMap\" is null")
			);
			
	}				

/****************
 * Snippet Parsing Tests with SQLParserAccess Object
 * These tests use the SqlParserAccess class to parse SQL queries and verify the results.
 * They check the abstract syntax tree (AST), interface, symbol table, table dictionary,
 * and substitution variables.
 *  */		

 	@Test
	public void basicSQLTest() {
		final String query = "select a, b from tab1 where a = b";
		final Snippet snippet = runSuccessfulSQLParserTest(query, SQLPARSER_SQL_TREE_KEY);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=a, table_ref=null}}, 2={column={name=b, table_ref=null}}}, from={table={alias=null, table=tab1}}, where={condition={left={column={name=a, table_ref=null}}, right={column={name=b, table_ref=null}}, operator==}}}}",
			snippet.getSqlAbstractTree().toString());
		Assert.assertEquals("Interface is wrong", "[a, b]",
			snippet.getQueryInterface().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={query_dictionary={a=[[@1,7:7='a',<381>,1:7]], b=[[@3,10:10='b',<381>,1:10]]}, table_dictionary={tab1={a=[[@1,7:7='a',<381>,1:7], [@7,28:28='a',<381>,1:28]], b=[[@3,10:10='b',<381>,1:10], [@9,32:32='b',<381>,1:32]]}}, filters=[{name=a, table_ref=tab1}, {name=b, table_ref=tab1}], interface={a=[{name=a, table_ref=tab1}], b=[{name=b, table_ref=tab1}]}}}",
			snippet.getSymbolTable().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={a=[[@1,7:7='a',<381>,1:7], [@7,28:28='a',<381>,1:28]], b=[[@3,10:10='b',<381>,1:10], [@9,32:32='b',<381>,1:32]]}}",
			snippet.getTableDictionary().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
			snippet.getSubstitutionsMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={a=[[@1,7:7='a',<381>,1:7]], b=[[@3,10:10='b',<381>,1:10]]}}",
        	snippet.getQueryColumnDictionaryMap().toString());
	}

	@Test
	public void basicQuerySnippetTest() {
		final String query = "select a, b from tab1 where a = b";
		final Snippet snippet = runSuccessfulSQLParserTest(query, SQLPARSER_QUERY_TREE_KEY);

		Assert.assertEquals("AST is wrong", "{QUERY={select={1={column={name=a, table_ref=null}}, 2={column={name=b, table_ref=null}}}, from={table={alias=null, table=tab1}}, where={condition={left={column={name=a, table_ref=null}}, right={column={name=b, table_ref=null}}, operator==}}}}",
			snippet.getSqlAbstractTree().toString());
		Assert.assertEquals("Interface is wrong", "[a, b]",
			snippet.getQueryInterface().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={query_dictionary={a=[[@1,7:7='a',<381>,1:7]], b=[[@3,10:10='b',<381>,1:10]]}, table_dictionary={tab1={a=[[@1,7:7='a',<381>,1:7], [@7,28:28='a',<381>,1:28]], b=[[@3,10:10='b',<381>,1:10], [@9,32:32='b',<381>,1:32]]}}, filters=[{name=a, table_ref=tab1}, {name=b, table_ref=tab1}], interface={a=[{name=a, table_ref=tab1}], b=[{name=b, table_ref=tab1}]}}}",
			snippet.getSymbolTable().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={a=[[@1,7:7='a',<381>,1:7], [@7,28:28='a',<381>,1:28]], b=[[@3,10:10='b',<381>,1:10], [@9,32:32='b',<381>,1:32]]}}",
			snippet.getTableDictionary().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
			snippet.getSubstitutionsMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={a=[[@1,7:7='a',<381>,1:7]], b=[[@3,10:10='b',<381>,1:10]]}}",
        	snippet.getQueryColumnDictionaryMap().toString());
	}


	@Test
	public void basicPredicandSnippetTest() {
		String sql = "item.emp_sales";
		final Snippet snippet = runSuccessfulSQLParserTest(sql, SQLPARSER_PREDICAND_TREE_KEY);
		
		Assert.assertEquals("AST is wrong", "{PREDICAND={column={name=emp_sales, table_ref=item}}}",
			snippet.getSqlAbstractTree().toString());
		Assert.assertEquals("Interface is wrong", "[]", 
        	snippet.getQueryInterface().toString());
		Assert.assertEquals("Symbol Table is wrong", "{table_dictionary={unresolved_column={item.emp_sales=[[@0,0:3='item',<381>,1:0]]}}, unresolved_column={item.emp_sales={column={name=emp_sales, table_ref=item}, locations=[[@0,0:3='item',<381>,1:0]]}}}",
			snippet.getSymbolTable().toString()); 
		Assert.assertEquals("Table Dictionary is wrong", "{}",
			snippet.getTableDictionary().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				snippet.getSubstitutionsMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{}",
        	snippet.getQueryColumnDictionaryMap().toString());
	}

	@Test
	public void basicInListSnippetTest() {
		String sql = "('a', 'dog', 'god')";
		final Snippet snippet = runSuccessfulSQLParserTest(sql, SQLPARSER_IN_LIST_TREE_KEY);
		
		Assert.assertEquals("AST is wrong", "{IN_LIST={list={1={literal='a'}, 2={literal='dog'}, 3={literal='god'}}}}",
			snippet.getSqlAbstractTree().toString());
		Assert.assertEquals("Interface is wrong", "[]", 
        	snippet.getQueryInterface().toString());
		Assert.assertEquals("Symbol Table is wrong", "{}",
			snippet.getSymbolTable().toString()); 
		Assert.assertEquals("Table Dictionary is wrong", "{}",
			snippet.getTableDictionary().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				snippet.getSubstitutionsMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{}",
        	snippet.getQueryColumnDictionaryMap().toString());
	}

	@Test
	public void basicConditionSnippetTest() {
		String sql = "table1.emp_sales_count is not null";
		final Snippet snippet = runSuccessfulSQLParserTest(sql, SQLPARSER_CONDITION_TREE_KEY);
		
		Assert.assertEquals("AST is wrong", "{CONDITION={condition={left={column={name=emp_sales_count, table_ref=table1}}, operator=is not null}}}",
			snippet.getSqlAbstractTree().toString());
		Assert.assertEquals("Interface is wrong", "[]", 
        	snippet.getQueryInterface().toString());
		Assert.assertEquals("Symbol Table is wrong", "{table_dictionary={unresolved_column={table1.emp_sales_count=[[@0,0:5='table1',<381>,1:0]]}}, unresolved_column={table1.emp_sales_count={column={name=emp_sales_count, table_ref=table1}, locations=[[@0,0:5='table1',<381>,1:0]]}}}",
			snippet.getSymbolTable().toString()); 
		Assert.assertEquals("Table Dictionary is wrong", "{}",
			snippet.getTableDictionary().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				snippet.getSubstitutionsMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{}",
        	snippet.getQueryColumnDictionaryMap().toString());
	}

	@Test
	public void basicColumnSnippetTest() {
		String sql = "schema1.emp_sales";
		final Snippet snippet = runSuccessfulSQLParserTest(sql, SQLPARSER_COLUMN_TREE_KEY);
		
		Assert.assertEquals("AST is wrong", "{COLUMN={column={name=emp_sales, table_ref=schema1}}}",
			snippet.getSqlAbstractTree().toString());
		Assert.assertEquals("Interface is wrong", "[]", 
        	snippet.getQueryInterface().toString());
		Assert.assertEquals("Symbol Table is wrong", "{table_dictionary={unresolved_column={schema1.emp_sales=[[@0,0:6='schema1',<381>,1:0]]}}, unresolved_column={schema1.emp_sales={column={name=emp_sales, table_ref=schema1}, locations=[[@0,0:6='schema1',<381>,1:0]]}}}",
			snippet.getSymbolTable().toString()); 
		Assert.assertEquals("Table Dictionary is wrong", "{}",
			snippet.getTableDictionary().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				snippet.getSubstitutionsMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{}",
        	snippet.getQueryColumnDictionaryMap().toString());
	}

	@Test
	public void basicTupleSnippetTest() {
		String sql = "schema1.emp_sales";
		final Snippet snippet = runSuccessfulSQLParserTest(sql, SQLPARSER_TUPLE_TREE_KEY);
		
		Assert.assertEquals("AST is wrong", "{TUPLE={table={schema=schema1, table=emp_sales}}}",
			snippet.getSqlAbstractTree().toString());
		Assert.assertEquals("Interface is wrong", "[]", 
        	snippet.getQueryInterface().toString());
		Assert.assertEquals("Symbol Table is wrong", "{table_dictionary={schema1.emp_sales={}}}",
			snippet.getSymbolTable().toString()); 
		Assert.assertEquals("Table Dictionary is wrong", "{}",
			snippet.getTableDictionary().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				snippet.getSubstitutionsMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{}",
        	snippet.getQueryColumnDictionaryMap().toString());
	}


	@Test
	public void joinExtensionSnippetLeftJoinWithOnTest2() {
		final String sql = "left join <[Hedgss].[college]> as aa on a.id=aa.id "; 
       	final Snippet snippet = runSuccessfulSQLParserTest(sql, SQLPARSER_JOIN_EXTENSION_TREE_KEY);
		
		Assert.assertEquals("AST is wrong", "{JOIN_EXTENSION={1={join=left, on={condition={left={column={name=id, table_ref=a}}, right={column={name=id, table_ref=aa}}, operator==}}}, 2={table={alias=aa, substitution={name=<[Hedgss].[college]>, parts={1=[Hedgss], 2=[college]}, type=tuple}}}}}",
			snippet.getSqlAbstractTree().toString());
		Assert.assertEquals("Interface is wrong", "[]", 
        	snippet.getQueryInterface().toString());
		Assert.assertEquals("Symbol Table is wrong", "{table_dictionary={<[Hedgss].[college]>={}}, unresolved_column={a.id={column={name=id, table_ref=a}, locations=[[@6,40:40='a',<381>,1:40]]}, aa.id={column={name=id, table_ref=aa}, locations=[[@10,45:46='aa',<381>,1:45]]}}, filters=[{name=id, table_ref=a}, {name=id, table_ref=aa}], table_alias={aa=<[Hedgss].[college]>}}",
			snippet.getSymbolTable().toString()); 
		Assert.assertEquals("Table Dictionary is wrong", "{<[Hedgss].[college]>={id=[[@10,45:46='aa',<381>,1:45]]}}",
			snippet.getTableDictionary().toString());
		Assert.assertEquals("Substitution List is wrong", "{<[Hedgss].[college]>=tuple}", 
				snippet.getSubstitutionsMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{}",
        	snippet.getQueryColumnDictionaryMap().toString());
	}


		@Test
		public void valuesStatementSnippetAloneTest() {
			final String query = " (values (1, 2, 'aaa'), (92, 3, 'aaa')) ";
			final Snippet snippet = runSuccessfulSQLParserTest(query, SQLPARSER_VALUES_TREE_KEY);
		
			Assert.assertEquals("AST is wrong", "{VALUES={values={matrix={1={row={1={literal=1}, 2={literal=2}, 3={literal='aaa'}}}, 2={row={1={literal=92}, 2={literal=3}, 3={literal='aaa'}}}}}}}",
					snippet.getSqlAbstractTree().toString());
			Assert.assertEquals("Interface is wrong", "[$1, $2, $3]", 
					snippet.getQueryInterface().toString());
			Assert.assertEquals("Symbol Table is wrong", "{values0={query_dictionary={$1=[[@2,9:9='(',<287>,1:9], [@10,24:24='(',<287>,1:24]], $2=[[@2,9:9='(',<287>,1:9], [@10,24:24='(',<287>,1:24]], $3=[[@2,9:9='(',<287>,1:9], [@10,24:24='(',<287>,1:24]]}, table_dictionary={}, interface={$1=[], $2=[], $3=[]}}}",
					snippet.getSymbolTable().toString()); 
			Assert.assertEquals("Table Dictionary is wrong", "{}",
					snippet.getTableDictionary().toString());
			Assert.assertEquals("Substitution List is wrong", "{}", 
					snippet.getSubstitutionsMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{values0={$1=[[@2,9:9='(',<287>,1:9], [@10,24:24='(',<287>,1:24]], $2=[[@2,9:9='(',<287>,1:9], [@10,24:24='(',<287>,1:24]], $3=[[@2,9:9='(',<287>,1:9], [@10,24:24='(',<287>,1:24]]}}",
        	snippet.getQueryColumnDictionaryMap().toString());
		}

	@Test
	public void ambiguousColumnAllocationWithAccessObjectMergedDiagnosticsTest() {
		final String query = " select dd.a aa, cc.b, c from tab1 dd join tab2 cc";
		final Snippet snippet = runFailedSyntaxSQLParserTest(query, SQLPARSER_SQL_TREE_KEY, 0);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=a, table_ref=dd}, alias=aa}, 2={column={name=b, table_ref=cc}}, 3={column={name=c, table_ref=null}}}, from={join={1={table={alias=dd, table=tab1}}, 2={join=join}, 3={table={alias=cc, table=tab2}}}}}}",
				snippet.getSqlAbstractTree().toString());
		Assert.assertEquals("Interface is wrong", "[aa, b, c]",
				snippet.getQueryInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				snippet.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={a=[[@1,8:9='dd',<381>,1:8]]}, tab2={b=[[@6,17:18='cc',<381>,1:17]]}}",
				snippet.getTableDictionary().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={aa=[[@4,13:14='aa',<381>,1:13]], b=[[@8,20:20='b',<381>,1:20]], c=[[@10,23:23='c',<381>,1:23]]}}",
				snippet.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={query_dictionary={aa=[[@4,13:14='aa',<381>,1:13]], b=[[@8,20:20='b',<381>,1:20]], c=[[@10,23:23='c',<381>,1:23]]}, table_dictionary={tab1={a=[[@1,8:9='dd',<381>,1:8]]}, tab2={b=[[@6,17:18='cc',<381>,1:17]]}}, interface={aa=[{name=a, table_ref=dd}], b=[{name=b, table_ref=cc}], c=[{name=c, table_ref=null}]}, table_alias={dd=tab1, cc=tab2}}}",
				snippet.getSymbolTable().toString());

		assertDiagnosticByCode(
				snippet,
				"AMBIGUOUS_COLUMN_REFERENCE",
				ParseDiagnostic.Severity.SEVERE_WARNING,
				"Ambiguous column reference 'c'",
				"c");
		assertUnresolvedUnknownColumnsDiagnostic(snippet, 1, 23, ParseDiagnostic.Severity.ERROR, "c");
		assertDiagnosticCountBySeverity(snippet, "UNRESOLVED_UNQUALIFIED_COLUMNS", ParseDiagnostic.Severity.ERROR, null, "c", 1);
		assertDiagnosticCountBySeverity(snippet, "AMBIGUOUS_COLUMN_REFERENCE", ParseDiagnostic.Severity.SEVERE_WARNING, null, "c", 1);
	}

	@Test
	public void ambiguousColumnAllocationInNestedSubqueriesDiagnosticsTest() {
		// Validate aggregate diagnostic counts for unresolved/ambiguous outcomes in nested subqueries.

		final String query = " select dd.a aa, cc.b, c from " 
		+ "\n (select x as a from tab1) dd join (select y as b, missing from "
		+ "\n (select z as y from tab2) ee) cc on dd.a = cc.b";
		final Snippet snippet = runFailedSyntaxSQLParserTest(query, SQLPARSER_SQL_TREE_KEY, 2);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=a, table_ref=dd}, alias=aa}, 2={column={name=b, table_ref=cc}}, 3={column={name=c, table_ref=null}}}, from={join={1={table={alias=dd, query={select={1={column={name=x, table_ref=null}, alias=a}}, from={table={alias=null, table=tab1}}}}}, 2={join=join, on={condition={left={column={name=a, table_ref=dd}}, right={column={name=b, table_ref=cc}}, operator==}}}, 3={table={alias=cc, query={select={1={column={name=y, table_ref=null}, alias=b}, 2={column={name=missing, table_ref=null}}}, from={table={alias=ee, query={select={1={column={name=z, table_ref=null}, alias=y}}, from={table={alias=null, table=tab2}}}}}}}}}}}}",
				snippet.getSqlAbstractTree().toString());
		Assert.assertEquals("Interface is wrong", "[aa, b, c]",
				snippet.getQueryInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				snippet.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={x=[[@14,40:40='x',<381>,2:9]]}, tab2={z=[[@32,105:105='z',<381>,3:9]]}}",
				snippet.getTableDictionary().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={a=[[@16,45:45='a',<381>,2:14], [@1,8:9='dd',<381>,1:8], [@42,133:134='dd',<381>,3:37]]}, query1={y=[[@34,110:110='y',<381>,3:14]]}, query2={b=[[@26,79:79='b',<381>,2:48], [@6,17:18='cc',<381>,1:17], [@46,140:141='cc',<381>,3:44]], missing=[[@28,82:88='missing',<381>,2:51]]}, query3={aa=[[@4,13:14='aa',<381>,1:13]], b=[[@8,20:20='b',<381>,1:20]], c=[[@10,23:23='c',<381>,1:23]]}}",
				snippet.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query3={query_dictionary={aa=[[@4,13:14='aa',<381>,1:13]], b=[[@8,20:20='b',<381>,1:20]], c=[[@10,23:23='c',<381>,1:23]]}, table_dictionary={}, def_query0={query_dictionary={a=[[@16,45:45='a',<381>,2:14], [@1,8:9='dd',<381>,1:8], [@42,133:134='dd',<381>,3:37]]}, table_dictionary={tab1={x=[[@14,40:40='x',<381>,2:9]]}}, interface={a=[{name=x, table_ref=tab1}]}}, filters=[{name=a, table_ref=dd}, {name=b, table_ref=cc}], interface={aa=[{name=a, table_ref=dd}], b=[{name=b, table_ref=cc}], c=[{name=c, table_ref=null}]}, table_alias={dd=query0, cc=query2}, def_query2={query_dictionary={b=[[@26,79:79='b',<381>,2:48], [@6,17:18='cc',<381>,1:17], [@46,140:141='cc',<381>,3:44]], missing=[[@28,82:88='missing',<381>,2:51]]}, table_dictionary={}, def_query1={query_dictionary={y=[[@34,110:110='y',<381>,3:14]]}, table_dictionary={tab2={z=[[@32,105:105='z',<381>,3:9]]}}, interface={y=[{name=z, table_ref=tab2}]}}, interface={b=[{name=y, table_ref=query1}], missing=[{name=missing, table_ref=null}]}, table_alias={ee=query1}}}}",
				snippet.getSymbolTable().toString());

		assertFatalDiagnosticCount(snippet, "UNQUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIASES", null, "c", 1);
		assertFatalDiagnosticCount(snippet, "UNQUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIASES", null, "missing", 1);
		assertDiagnosticAtPosition(
				snippet,
				"UNRESOLVED_UNQUALIFIED_COLUMNS",
				ParseDiagnostic.Severity.ERROR,
				null,
				null,
				1,
				23);
		assertDiagnosticAtPosition(
				snippet,
				"UNRESOLVED_UNQUALIFIED_COLUMNS",
				ParseDiagnostic.Severity.ERROR,
				null,
				null,
				2,
				51);
	}

	@Test
	public void unresolvedUnknownSymbolTableWithSimpleSubqueryWithAccessObjectMergedDiagnosticsTest() {
		final String query = " select a aa, b, c from (select a, e as b from ee where 1=1) dd";
		final Snippet snippet = runFailedSyntaxSQLParserTest(query, SQLPARSER_SQL_TREE_KEY, 1);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=a, table_ref=null}, alias=aa}, 2={column={name=b, table_ref=null}}, 3={column={name=c, table_ref=null}}}, from={table={alias=dd, query={select={1={column={name=a, table_ref=null}}, 2={column={name=e, table_ref=null}, alias=b}}, from={table={alias=null, table=ee}}, where={condition={left={literal=1}, right={literal=1}, operator==}}}}}}}",
				snippet.getSqlAbstractTree().toString());
		Assert.assertEquals("Interface is wrong", "[aa, b, c]",
				snippet.getQueryInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				snippet.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{ee={a=[[@10,32:32='a',<381>,1:32]], e=[[@12,35:35='e',<381>,1:35]]}}",
				snippet.getTableDictionary().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={a=[[@10,32:32='a',<381>,1:32]], b=[[@14,40:40='b',<381>,1:40]]}, query1={aa=[[@2,10:11='aa',<381>,1:10]], b=[[@4,14:14='b',<381>,1:14]], c=[[@6,17:17='c',<381>,1:17]]}}",
				snippet.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query1={query_dictionary={aa=[[@2,10:11='aa',<381>,1:10]], b=[[@4,14:14='b',<381>,1:14]], c=[[@6,17:17='c',<381>,1:17]]}, table_dictionary={}, def_query0={query_dictionary={a=[[@10,32:32='a',<381>,1:32]], b=[[@14,40:40='b',<381>,1:40]]}, table_dictionary={ee={a=[[@10,32:32='a',<381>,1:32]], e=[[@12,35:35='e',<381>,1:35]]}}, filters=[], interface={a=[{name=a, table_ref=ee}], b=[{name=e, table_ref=ee}]}}, interface={aa=[{name=a, table_ref=query0}], b=[{name=b, table_ref=query0}], c=[{name=c, table_ref=null}]}, table_alias={dd=query0}}}",
				snippet.getSymbolTable().toString());

		assertFatalDiagnosticCount(snippet, "UNQUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIASES", null, "c", 1);
		assertUnresolvedUnknownColumnsDiagnostic(snippet, 1, 17, ParseDiagnostic.Severity.ERROR, "c");
		assertDiagnosticCountBySeverity(snippet, "UNRESOLVED_UNQUALIFIED_COLUMNS", ParseDiagnostic.Severity.ERROR, null, "c", 1);
	}

/****
 * Ambiguous Symbol Table Tests
 * These tests check for scenarios where there are unresolved symbols in the symbol table due to ambiguity.
 */
	@Test
	public void ambiguityWarnings_UnresolvedInterfaceColumnInSingleSubquery1() {
		// Parent query selects alias 'c' which is not produced by the single subquery source
		String query = " select a aa, b, c from (select a, e as b from ee where 1=1) dd";
		final Snippet snippet = runFailedSyntaxSQLParserTest(query, SQLPARSER_SQL_TREE_KEY, 1);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=a, table_ref=null}, alias=aa}, 2={column={name=b, table_ref=null}}, 3={column={name=c, table_ref=null}}}, from={table={alias=dd, query={select={1={column={name=a, table_ref=null}}, 2={column={name=e, table_ref=null}, alias=b}}, from={table={alias=null, table=ee}}, where={condition={left={literal=1}, right={literal=1}, operator==}}}}}}}",
				snippet.getSqlAbstractTree().toString());

		assertFatalDiagnosticCount(snippet, "UNQUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIASES", null, "c", 1);
		assertUnresolvedUnknownColumnsDiagnostic(snippet, 1, 17, ParseDiagnostic.Severity.ERROR, "c");
		assertDiagnosticCountBySeverity(snippet, "UNRESOLVED_UNQUALIFIED_COLUMNS", ParseDiagnostic.Severity.ERROR, null, "c", 1);
	}

	@Test
    public void ambiguityWarnings_UnresolvedInterfaceColumnInSingleSubquery2() {
		// Parent query selects column 'a'which is not produced by the single subquery source
		String query = " SELECT distinct a,b,c FROM (select all b,c from tab2) tab1";
		final Snippet snippet = runFailedSyntaxSQLParserTest(query, SQLPARSER_SQL_TREE_KEY, 1);

		Assert.assertNotNull("Snippet should not be null", snippet);
		Assert.assertNotNull("AST should not be null", snippet.getSqlAbstractTree());
		assertFatalDiagnosticCount(snippet, "UNQUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIASES", null, "a", 1);
		assertUnresolvedUnknownColumnsDiagnostic(snippet, 1, 17, ParseDiagnostic.Severity.ERROR, "a");
		assertDiagnosticCountBySeverity(snippet, "UNRESOLVED_UNQUALIFIED_COLUMNS", ParseDiagnostic.Severity.ERROR, null, "a", 1);
    }

	@Test
	public void ambiguityWarnings_UnknownImplicitInterfaceColumnDiagnosticTest() {
		String query = "SELECT a FROM (SELECT b FROM tab2) tab1";
		final Snippet snippet = runFailedSyntaxSQLParserTest(query, SQLPARSER_SQL_TREE_KEY, 1);

		assertFatalDiagnosticCount(snippet, "UNQUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIASES", null, "a", 1);
		assertDiagnosticByCode(
				snippet,
				"UNRESOLVED_UNQUALIFIED_COLUMNS",
				ParseDiagnostic.Severity.ERROR,
				"Unresolved unqualified column reference(s)",
				"a");
		assertDiagnosticCountBySeverity(snippet, "UNRESOLVED_UNQUALIFIED_COLUMNS", ParseDiagnostic.Severity.ERROR, null, "a", 1);
	}

	@Test
	public void ambiguityWarnings_AmbiguousInterfaceColumnDiagnosticTest() {
		String query = "SELECT a FROM tab1 dd JOIN tab2 cc ON dd.a = cc.a";
		final Snippet snippet = runFailedSyntaxSQLParserTest(query, SQLPARSER_SQL_TREE_KEY, 0);

		assertDiagnosticByCode(
				snippet,
				"AMBIGUOUS_COLUMN_REFERENCE",
				ParseDiagnostic.Severity.SEVERE_WARNING,
				"Ambiguous column reference 'a'",
				"a");

		assertDiagnosticByCode(
				snippet,
				"UNRESOLVED_UNQUALIFIED_COLUMNS",
				ParseDiagnostic.Severity.ERROR,
				"Unresolved unqualified column reference(s)",
				"a");
		assertDiagnosticCountBySeverity(snippet, "AMBIGUOUS_COLUMN_REFERENCE", ParseDiagnostic.Severity.SEVERE_WARNING, null, "a", 1);
		assertDiagnosticCountBySeverity(snippet, "UNRESOLVED_UNQUALIFIED_COLUMNS", ParseDiagnostic.Severity.ERROR, null, "a", 1);
	}

	@Test
	public void ambiguityWarnings_AmbiguousInterfaceColumnFromCompetingSubqueryAliasesDiagnosticTest() {
		String query = "SELECT a FROM (SELECT x AS a FROM tab1) dd JOIN (SELECT z AS a FROM tab2) cc ON dd.a = cc.a";
		final Snippet snippet = runFailedSyntaxSQLParserTest(query, SQLPARSER_SQL_TREE_KEY, 0);

		assertDiagnosticByCode(
				snippet,
				"AMBIGUOUS_COLUMN_REFERENCE",
				ParseDiagnostic.Severity.SEVERE_WARNING,
				"Ambiguous column reference 'a'",
				"a");

		assertDiagnosticByCode(
				snippet,
				"UNRESOLVED_UNQUALIFIED_COLUMNS",
				ParseDiagnostic.Severity.ERROR,
				"Unresolved unqualified column reference(s)",
				"a");
		assertDiagnosticCountBySeverity(snippet, "AMBIGUOUS_COLUMN_REFERENCE", ParseDiagnostic.Severity.SEVERE_WARNING, null, "a", 1);
		assertDiagnosticCountBySeverity(snippet, "UNRESOLVED_UNQUALIFIED_COLUMNS", ParseDiagnostic.Severity.ERROR, null, "a", 1);
	}

	@Test
	public void explicitQueryReferenceMissingColumnDiagnosticTest() {
		String query = "SELECT dd.missing FROM (SELECT x AS a FROM tab1) dd";
		final Snippet snippet = runFailedSyntaxSQLParserTest(query, SQLPARSER_SQL_TREE_KEY, 1);

		assertFatalDiagnosticByCode(
				snippet,
				"QUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIAS",
				"query alias 'dd'",
				"dd.missing");
		assertFatalDiagnosticCount(snippet, "QUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIAS", null, null, 1);
		assertDiagnosticCountBySeverityCodeAndSource(
				snippet,
				ParseDiagnostic.Severity.FATAL,
				"QUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIAS",
				"SqlASTWalkerHelper",
				1);
	}

	@Test
	public void explicitValuesAliasMissingColumnFallsBackToUnknownResolutionFatalTest() {
		String query = "SELECT dd.missing FROM (VALUES (1)) dd(a)";
		final Snippet snippet = runFailedSyntaxSQLParserTest(query, SQLPARSER_SQL_TREE_KEY, 1);

		Assert.assertTrue("Expected one fatal error", snippet.getFatalErrorCount() >= 1);
	}

	@Test
	public void explicitValuesAliasValidColumnDoesNotFallBackToUnknownTest() {
		String query = "SELECT dd.a FROM (VALUES (1)) dd(a)";
		final Snippet snippet = runSuccessfulSQLParserTest(query, SQLPARSER_SQL_TREE_KEY);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=a, table_ref=dd}}}, from={values={columns={1={column={name=a, table_ref=null}}}, alias=dd, matrix={1={row={1={literal=1}}}}}}}}",
				snippet.getSqlAbstractTree().toString());
		Assert.assertEquals("Interface is wrong", "[a]",
				snippet.getQueryInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				snippet.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{}",
				snippet.getTableDictionary().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{values0={a=[[@13,33:33='a',<381>,1:33], [@1,7:8='dd',<381>,1:7]]}, query1={a=[[@3,10:10='a',<381>,1:10]]}}",
				snippet.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query1={query_dictionary={a=[[@3,10:10='a',<381>,1:10]]}, table_dictionary={}, def_values0={query_dictionary={a=[[@13,33:33='a',<381>,1:33], [@1,7:8='dd',<381>,1:7]]}, table_dictionary={}, interface={a=[]}}, interface={a=[{name=a, table_ref=dd}]}, table_alias={dd=values0}}}",
				snippet.getSymbolTable().toString());
	}

	// Parser-level reproduction for explicit TABLE-reference miss is hard to trigger currently because
	// explicit qualified references can be materialized into symbol/table collections before missing-source
	// validation runs. This catalog-level test still locks the table-specific code/template terminology.
	@Test
	public void explicitTableReferenceMissingColumnDiagnosticCatalogTest() {
		SqlASTWalkerHelper helper = new SqlASTWalkerHelper();

		String tableCode = helper.getDiagnosticCode(SqlASTWalkerHelper.DIAG_SQL_QUALIFIED_COLUMN_NOT_FOUND_IN_TABLE);
		String tableTemplate = helper.getDiagnosticMessage(SqlASTWalkerHelper.DIAG_SQL_QUALIFIED_COLUMN_NOT_FOUND_IN_TABLE);

		Assert.assertEquals("Expected table-specific diagnostic code mapping",
				"QUALIFIED_COLUMN_NOT_FOUND_IN_TABLE",
				tableCode);
		Assert.assertNotNull("Expected table-specific diagnostic message template", tableTemplate);
	}

/****
 * END OF Ambiguous Symbol Table Tests
*/
	/**** END OF SNIPPET TESTS */

	/**** START OF INSERT STATEMENT TESTS */
	/**
	 * Tests for INSERT statements using the SqlParserAccess class.
	 * These tests cover various scenarios including inserting from a query,
	 * inserting from a variable, and inserting from values.
	 */
	
	@Test
	public void basicInsertFromQueryTest() {
		final String query = "insert into tab1 select a,b from tab2";
        final Snippet snippet = runSuccessfulSQLParserTest(query,SQLPARSER_INSERT_TREE_KEY);

		
		Assert.assertEquals("AST is wrong", "{INSERT={preamble=insert_into, from={from={table={alias=null, table=tab2}}, select={1={column={name=a, table_ref=null}}, 2={column={name=b, table_ref=null}}}}, target_table={table={alias=null, table=tab1}}}}",
        	snippet.getSqlAbstractTree().toString());
		Assert.assertEquals("Interface is wrong", "[a, b]", 
        	snippet.getQueryInterface().toString());
		Assert.assertEquals("Symbol Table is wrong", "{insert1={query_dictionary={a=[[@4,24:24='a',<381>,1:24]], b=[[@6,26:26='b',<381>,1:26]]}, table_dictionary={tab1={a=[[@4,24:24='a',<381>,1:24]], b=[[@6,26:26='b',<381>,1:26]]}}, def_query0={query_dictionary={a=[[@4,24:24='a',<381>,1:24]], b=[[@6,26:26='b',<381>,1:26]]}, table_dictionary={tab2={a=[[@4,24:24='a',<381>,1:24]], b=[[@6,26:26='b',<381>,1:26]]}}, interface={a=[{name=a, table_ref=tab2}], b=[{name=b, table_ref=tab2}]}}, interface={a=[{name=a, table_ref=query0}], b=[{name=b, table_ref=query0}]}}}",
        	snippet.getSymbolTable().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={a=[[@4,24:24='a',<381>,1:24]], b=[[@6,26:26='b',<381>,1:26]]}, tab2={a=[[@4,24:24='a',<381>,1:24]], b=[[@6,26:26='b',<381>,1:26]]}}",
        	snippet.getTableDictionary().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
        	snippet.getSubstitutionsMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={a=[[@4,24:24='a',<381>,1:24]], b=[[@6,26:26='b',<381>,1:26]]}, insert1={a=[[@4,24:24='a',<381>,1:24]], b=[[@6,26:26='b',<381>,1:26]]}}",
       	snippet.getQueryColumnDictionaryMap().toString());
	}
   
	@Test
	public void basicInsertFromVariableTest() {
		final String query = "insert into tab1 <query variable>";
        final Snippet snippet = runSuccessfulSQLParserTest(query, SQLPARSER_INSERT_TREE_KEY);

		
		Assert.assertEquals("AST is wrong", "{INSERT={preamble=insert_into, from={substitution={name=<query variable>, type=query}}, target_table={table={alias=null, table=tab1}}}}",
        	snippet.getSqlAbstractTree().toString());
		Assert.assertEquals("Interface is wrong", "[]", 
        	snippet.getQueryInterface().toString());
		Assert.assertEquals("Symbol Table is wrong", "{insert0={table_dictionary={tab1={}}}}",
        	snippet.getSymbolTable().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{}",
        	snippet.getTableDictionary().toString());
		Assert.assertEquals("Substitution List is wrong", "{<query variable>=query}", 
        	snippet.getSubstitutionsMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{}",
        	snippet.getQueryColumnDictionaryMap().toString());
	}
    
	@Test
	public void basicInsertFromValuesTest() {
		final String query = "insert into tab1 values (1,2,3), (2,3,4)";
        final Snippet snippet = runSuccessfulSQLParserTest(query, SQLPARSER_INSERT_TREE_KEY);

		
		Assert.assertEquals("AST is wrong", "{INSERT={preamble=insert_into, from={values={matrix={1={row={1={literal=1}, 2={literal=2}, 3={literal=3}}}, 2={row={1={literal=2}, 2={literal=3}, 3={literal=4}}}}}}, target_table={table={alias=null, table=tab1}}}}",
        	snippet.getSqlAbstractTree().toString());
		Assert.assertEquals("Interface is wrong", "[$1, $2, $3]", 
        	snippet.getQueryInterface().toString());
		Assert.assertEquals("Symbol Table is wrong", "{insert1={query_dictionary={$1=[[@4,24:24='(',<287>,1:24], [@12,33:33='(',<287>,1:33]], $2=[[@4,24:24='(',<287>,1:24], [@12,33:33='(',<287>,1:33]], $3=[[@4,24:24='(',<287>,1:24], [@12,33:33='(',<287>,1:33]]}, table_dictionary={tab1={$1=[[@4,24:24='(',<287>,1:24], [@12,33:33='(',<287>,1:33]], $2=[[@4,24:24='(',<287>,1:24], [@12,33:33='(',<287>,1:33]], $3=[[@4,24:24='(',<287>,1:24], [@12,33:33='(',<287>,1:33]]}}, def_values0={query_dictionary={$1=[[@4,24:24='(',<287>,1:24], [@12,33:33='(',<287>,1:33]], $2=[[@4,24:24='(',<287>,1:24], [@12,33:33='(',<287>,1:33]], $3=[[@4,24:24='(',<287>,1:24], [@12,33:33='(',<287>,1:33]]}, table_dictionary={}, interface={$1=[], $2=[], $3=[]}}, interface={$1=[{name=$1, table_ref=values0}], $2=[{name=$2, table_ref=values0}], $3=[{name=$3, table_ref=values0}]}}}",
        	snippet.getSymbolTable().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={$1=[[@4,24:24='(',<287>,1:24], [@12,33:33='(',<287>,1:33]], $2=[[@4,24:24='(',<287>,1:24], [@12,33:33='(',<287>,1:33]], $3=[[@4,24:24='(',<287>,1:24], [@12,33:33='(',<287>,1:33]]}}",
        	snippet.getTableDictionary().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
        	snippet.getSubstitutionsMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{values0={$1=[[@4,24:24='(',<287>,1:24], [@12,33:33='(',<287>,1:33]], $2=[[@4,24:24='(',<287>,1:24], [@12,33:33='(',<287>,1:33]], $3=[[@4,24:24='(',<287>,1:24], [@12,33:33='(',<287>,1:33]]}, insert1={$1=[[@4,24:24='(',<287>,1:24], [@12,33:33='(',<287>,1:33]], $2=[[@4,24:24='(',<287>,1:24], [@12,33:33='(',<287>,1:33]], $3=[[@4,24:24='(',<287>,1:24], [@12,33:33='(',<287>,1:33]]}}",
        	snippet.getQueryColumnDictionaryMap().toString());
	}


	@Test
	public void basicInsertWithColumnsFromQueryTest() {
		final String query = "insert into tab1 (c ,d) select a,b from tab2";
        final Snippet snippet = runSuccessfulSQLParserTest(query, SQLPARSER_INSERT_TREE_KEY);

		
		Assert.assertEquals("AST is wrong", "{INSERT={preamble=insert_into, from={from={table={alias=null, table=tab2}}, select={1={column={name=a, table_ref=null}}, 2={column={name=b, table_ref=null}}}}, target_table={table={alias=null, table=tab1}}, columns={1={column={name=c, table_ref=null}}, 2={column={name=d, table_ref=null}}}}}",
        	snippet.getSqlAbstractTree().toString());
		Assert.assertEquals("Interface is wrong", "[c, d]", 
        	snippet.getQueryInterface().toString());
		Assert.assertEquals("Symbol Table is wrong", "{insert1={query_dictionary={d=[[@6,21:21='d',<381>,1:21]], c=[[@4,18:18='c',<381>,1:18]]}, table_dictionary={tab1={c=[[@4,18:18='c',<381>,1:18]], d=[[@6,21:21='d',<381>,1:21]]}}, def_query0={query_dictionary={a=[[@9,31:31='a',<381>,1:31]], b=[[@11,33:33='b',<381>,1:33]]}, table_dictionary={tab2={a=[[@9,31:31='a',<381>,1:31]], b=[[@11,33:33='b',<381>,1:33]]}}, interface={a=[{name=a, table_ref=tab2}], b=[{name=b, table_ref=tab2}]}}, interface={c=[{name=a, table_ref=query0}], d=[{name=b, table_ref=query0}]}}}",
        	snippet.getSymbolTable().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={c=[[@4,18:18='c',<381>,1:18]], d=[[@6,21:21='d',<381>,1:21]]}, tab2={a=[[@9,31:31='a',<381>,1:31]], b=[[@11,33:33='b',<381>,1:33]]}}",
        	snippet.getTableDictionary().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
        	snippet.getSubstitutionsMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={a=[[@9,31:31='a',<381>,1:31]], b=[[@11,33:33='b',<381>,1:33]]}, insert1={d=[[@6,21:21='d',<381>,1:21]], c=[[@4,18:18='c',<381>,1:18]]}}",
        	snippet.getQueryColumnDictionaryMap().toString());
	}
   

	@Test
	public void insertWithColumnsFromUnionQueryTest() {
		final String query = "insert into tab1 (c ,d) select a,b from tab2 union select c,d from tab3";
        final Snippet snippet = runSuccessfulSQLParserTest(query, SQLPARSER_INSERT_TREE_KEY);

		Assert.assertEquals("AST is wrong", "{INSERT={preamble=insert_into, from={union={1={select={1={column={name=a, table_ref=null}}, 2={column={name=b, table_ref=null}}}, from={table={alias=null, table=tab2}}}, 2={union={qualifier=null, operator=union}}, 3={select={1={column={name=c, table_ref=null}}, 2={column={name=d, table_ref=null}}}, from={table={alias=null, table=tab3}}}}}, target_table={table={alias=null, table=tab1}}, columns={1={column={name=c, table_ref=null}}, 2={column={name=d, table_ref=null}}}}}",
        	snippet.getSqlAbstractTree().toString());
		Assert.assertEquals("Interface is wrong", "[c, d]", 
        	snippet.getQueryInterface().toString());
		Assert.assertEquals("Symbol Table is wrong", "{insert3={query_dictionary={d=[[@6,21:21='d',<381>,1:21]], c=[[@4,18:18='c',<381>,1:18]]}, def_union2={query0={query_dictionary={a=[[@9,31:31='a',<381>,1:31]], b=[[@11,33:33='b',<381>,1:33]]}, table_dictionary={tab2={a=[[@9,31:31='a',<381>,1:31]], b=[[@11,33:33='b',<381>,1:33]]}}, interface={a=[{name=a, table_ref=tab2}], b=[{name=b, table_ref=tab2}]}}, interface={a=[{name=a, table_ref=tab2}, {name=c, table_ref=tab3}], b=[{name=b, table_ref=tab2}, {name=d, table_ref=tab3}]}, query1={query_dictionary={c=[[@16,58:58='c',<381>,1:58]], d=[[@18,60:60='d',<381>,1:60]]}, table_dictionary={tab3={c=[[@16,58:58='c',<381>,1:58]], d=[[@18,60:60='d',<381>,1:60]]}}, interface={c=[{name=c, table_ref=tab3}], d=[{name=d, table_ref=tab3}]}}}, table_dictionary={tab1={c=[[@4,18:18='c',<381>,1:18]], d=[[@6,21:21='d',<381>,1:21]]}}, interface={c=[{name=a, table_ref=union2}], d=[{name=b, table_ref=union2}]}}}",
        	snippet.getSymbolTable().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab3={c=[[@16,58:58='c',<381>,1:58]], d=[[@18,60:60='d',<381>,1:60]]}, tab1={c=[[@4,18:18='c',<381>,1:18]], d=[[@6,21:21='d',<381>,1:21]]}, tab2={a=[[@9,31:31='a',<381>,1:31]], b=[[@11,33:33='b',<381>,1:33]]}}",
        	snippet.getTableDictionary().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
        	snippet.getSubstitutionsMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={a=[[@9,31:31='a',<381>,1:31]], b=[[@11,33:33='b',<381>,1:33]]}, query1={c=[[@16,58:58='c',<381>,1:58]], d=[[@18,60:60='d',<381>,1:60]]}, insert3={d=[[@6,21:21='d',<381>,1:21]], c=[[@4,18:18='c',<381>,1:18]]}}",
        	snippet.getQueryColumnDictionaryMap().toString());
	}


	@Test
	public void insertWithColumnsFromJoinQueryTest() {
		final String query = "insert into tab1 (c ,d) select ff.a, gg.b from tab2 ff join tab3 gg on ff.id = gg.id";
        final Snippet snippet = runSuccessfulSQLParserTest(query, SQLPARSER_INSERT_TREE_KEY);

		Assert.assertEquals("AST is wrong", "{INSERT={preamble=insert_into, from={from={join={1={table={alias=ff, table=tab2}}, 2={join=join, on={condition={left={column={name=id, table_ref=ff}}, right={column={name=id, table_ref=gg}}, operator==}}}, 3={table={alias=gg, table=tab3}}}}, select={1={column={name=a, table_ref=ff}}, 2={column={name=b, table_ref=gg}}}}, target_table={table={alias=null, table=tab1}}, columns={1={column={name=c, table_ref=null}}, 2={column={name=d, table_ref=null}}}}}",
        	snippet.getSqlAbstractTree().toString());
		Assert.assertEquals("Interface is wrong", "[c, d]", 
        	snippet.getQueryInterface().toString());
		Assert.assertEquals("Symbol Table is wrong", "{insert1={query_dictionary={d=[[@6,21:21='d',<381>,1:21]], c=[[@4,18:18='c',<381>,1:18]]}, table_dictionary={tab1={c=[[@4,18:18='c',<381>,1:18]], d=[[@6,21:21='d',<381>,1:21]]}}, def_query0={query_dictionary={a=[[@11,34:34='a',<381>,1:34]], b=[[@15,40:40='b',<381>,1:40]]}, table_dictionary={tab3={b=[[@13,37:38='gg',<381>,1:37]], id=[[@27,79:80='gg',<381>,1:79]]}, tab2={a=[[@9,31:32='ff',<381>,1:31]], id=[[@23,71:72='ff',<381>,1:71]]}}, filters=[{name=id, table_ref=ff}, {name=id, table_ref=gg}], interface={a=[{name=a, table_ref=ff}], b=[{name=b, table_ref=gg}]}, table_alias={ff=tab2, gg=tab3}}, interface={c=[{name=a, table_ref=query0}], d=[{name=b, table_ref=query0}]}}}",
        	snippet.getSymbolTable().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab3={b=[[@13,37:38='gg',<381>,1:37]], id=[[@27,79:80='gg',<381>,1:79]]}, tab1={c=[[@4,18:18='c',<381>,1:18]], d=[[@6,21:21='d',<381>,1:21]]}, tab2={a=[[@9,31:32='ff',<381>,1:31]], id=[[@23,71:72='ff',<381>,1:71]]}}",
        	snippet.getTableDictionary().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
        	snippet.getSubstitutionsMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={a=[[@11,34:34='a',<381>,1:34]], b=[[@15,40:40='b',<381>,1:40]]}, insert1={d=[[@6,21:21='d',<381>,1:21]], c=[[@4,18:18='c',<381>,1:18]]}}",
        	snippet.getQueryColumnDictionaryMap().toString());
	}

	@Test
	public void basicInsertWithColumnsFromVariableTest() {
		final String query = "insert into tab1 (c ,d, e)  <query variable>";
        final Snippet snippet = runSuccessfulSQLParserTest(query, SQLPARSER_INSERT_TREE_KEY);

		
		Assert.assertEquals("AST is wrong", "{INSERT={preamble=insert_into, from={substitution={name=<query variable>, type=query}}, target_table={table={alias=null, table=tab1}}, columns={1={column={name=c, table_ref=null}}, 2={column={name=d, table_ref=null}}, 3={column={name=e, table_ref=null}}}}}",
        	snippet.getSqlAbstractTree().toString());
		Assert.assertEquals("Interface is wrong", "[c, d, e]", 
        	snippet.getQueryInterface().toString());
		Assert.assertEquals("Symbol Table is wrong", "{insert0={query_dictionary={c=[[@4,18:18='c',<381>,1:18]], d=[[@6,21:21='d',<381>,1:21]], e=[[@8,24:24='e',<381>,1:24]]}, table_dictionary={tab1={c=[[@4,18:18='c',<381>,1:18]], d=[[@6,21:21='d',<381>,1:21]], e=[[@8,24:24='e',<381>,1:24]]}}, interface={c=[], d=[], e=[]}}}",
        	snippet.getSymbolTable().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={c=[[@4,18:18='c',<381>,1:18]], d=[[@6,21:21='d',<381>,1:21]], e=[[@8,24:24='e',<381>,1:24]]}}",
        	snippet.getTableDictionary().toString());
		Assert.assertEquals("Substitution List is wrong", "{<query variable>=query}", 
        	snippet.getSubstitutionsMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{insert0={c=[[@4,18:18='c',<381>,1:18]], d=[[@6,21:21='d',<381>,1:21]], e=[[@8,24:24='e',<381>,1:24]]}}",
        	snippet.getQueryColumnDictionaryMap().toString());
	}
    

	@Test
	public void basicInsertWithColumnsFromValuesTest() {
		final String query = "insert into tab1  (c ,d)  values (1,2,3), (2,3,4)";
        final Snippet snippet = runFailedSyntaxSQLParserTest(query, SQLPARSER_INSERT_TREE_KEY, 1);

		assertFatalDiagnosticCount(snippet, "INSERT_TARGET_SOURCE_COLUMN_COUNT_MISMATCH", null, null, 1);
		assertDiagnosticAtPosition(
				snippet,
				"INSERT_TARGET_SOURCE_COLUMN_COUNT_MISMATCH",
				ParseDiagnostic.Severity.FATAL,
				"Insert Mismatch: Target has 2 columns, Source has 3 columns, (l:1 c:18)",
				null,
				1,
				18);

		
		Assert.assertEquals("AST is wrong", "{INSERT={preamble=insert_into, from={values={matrix={1={row={1={literal=1}, 2={literal=2}, 3={literal=3}}}, 2={row={1={literal=2}, 2={literal=3}, 3={literal=4}}}}}}, target_table={table={alias=null, table=tab1}}, columns={1={column={name=c, table_ref=null}}, 2={column={name=d, table_ref=null}}}}}",
        	snippet.getSqlAbstractTree().toString());
		Assert.assertEquals("Interface is wrong", "[c, d]", 
        	snippet.getQueryInterface().toString());
		Assert.assertEquals("Symbol Table is wrong", "{insert1={query_dictionary={d=[[@6,22:22='d',<381>,1:22]], c=[[@4,19:19='c',<381>,1:19]]}, table_dictionary={tab1={c=[[@4,19:19='c',<381>,1:19]], d=[[@6,22:22='d',<381>,1:22]]}}, def_values0={query_dictionary={$1=[[@9,33:33='(',<287>,1:33], [@17,42:42='(',<287>,1:42]], $2=[[@9,33:33='(',<287>,1:33], [@17,42:42='(',<287>,1:42]], $3=[[@9,33:33='(',<287>,1:33], [@17,42:42='(',<287>,1:42]]}, table_dictionary={}, interface={$1=[], $2=[], $3=[]}}, interface={c=[{name=$1, table_ref=values0}], d=[{name=$2, table_ref=values0}]}}}",
        	snippet.getSymbolTable().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={c=[[@4,19:19='c',<381>,1:19]], d=[[@6,22:22='d',<381>,1:22]]}}",
        	snippet.getTableDictionary().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
        	snippet.getSubstitutionsMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{values0={$1=[[@9,33:33='(',<287>,1:33], [@17,42:42='(',<287>,1:42]], $2=[[@9,33:33='(',<287>,1:33], [@17,42:42='(',<287>,1:42]], $3=[[@9,33:33='(',<287>,1:33], [@17,42:42='(',<287>,1:42]]}, insert1={d=[[@6,22:22='d',<381>,1:22]], c=[[@4,19:19='c',<381>,1:19]]}}",
        	snippet.getQueryColumnDictionaryMap().toString());
	}



	@Test
	public void insertWithTargetColumnsAndSubqueryValuesTest() {

		final String query = " insert into sch.subj.tbl (newcol1, newcol2) values (SELECT b.att1, b.att2 "
				+ " from (SELECT a.col1 as att1, a.col2 as att2 " 
				+ " FROM sch.subj.tab1 as a"
				+ " WHERE a.col1 <> a.col3 " + " ) AS b )";

		final Snippet snippet = runSuccessfulSQLParserTest(query, SQLPARSER_INSERT_TREE_KEY);
		Assert.assertEquals("AST is wrong", "{INSERT={preamble=insert_into, from={from={table={alias=b, query={select={1={column={name=col1, table_ref=a}, alias=att1}, 2={column={name=col2, table_ref=a}, alias=att2}}, from={table={schema=subj, alias=a, dbname=sch, table=tab1}}, where={condition={left={column={name=col1, table_ref=a}}, right={column={name=col3, table_ref=a}}, operator=<>}}}}}, select={1={column={name=att1, table_ref=b}}, 2={column={name=att2, table_ref=b}}}}, target_table={table={schema=subj, alias=values, dbname=sch, table=tbl}}, columns={1={column={name=newcol1, table_ref=null}}, 2={column={name=newcol2, table_ref=null}}}}}",
				snippet.getSqlAbstractTree().toString());
		Assert.assertEquals("Interface is wrong", "[newcol2, newcol1]",
				snippet.getQueryInterface().toString());
		Assert.assertEquals("Symbol Table is wrong", "{insert2={query_dictionary={newcol2=[[@10,36:42='newcol2',<381>,1:36]], newcol1=[[@8,27:33='newcol1',<381>,1:27]]}, table_dictionary={sch.subj.tbl={newcol2=[[@10,36:42='newcol2',<381>,1:36]], newcol1=[[@8,27:33='newcol1',<381>,1:27]]}}, def_query1={query_dictionary={att2=[[@21,70:73='att2',<381>,1:70]], att1=[[@17,62:65='att1',<381>,1:62]]}, table_dictionary={}, def_query0={query_dictionary={att2=[[@35,115:118='att2',<381>,1:115], [@19,68:68='b',<381>,1:68]], att1=[[@29,99:102='att1',<381>,1:99], [@15,60:60='b',<381>,1:60]]}, table_dictionary={sch.subj.tab1={col2=[[@31,105:105='a',<381>,1:105]], col3=[[@49,161:161='a',<381>,1:161]], col1=[[@25,89:89='a',<381>,1:89], [@45,151:151='a',<381>,1:151]]}}, filters=[{name=col1, table_ref=a}, {name=col3, table_ref=a}], interface={att2=[{name=col2, table_ref=a}], att1=[{name=col1, table_ref=a}]}, table_alias={a=sch.subj.tab1}}, interface={att2=[{name=att2, table_ref=b}], att1=[{name=att1, table_ref=b}]}, table_alias={b=query0}}, interface={newcol1=[{name=att1, table_ref=query1}], newcol2=[{name=att2, table_ref=query1}]}, table_alias={values=sch.subj.tbl}}}",
				snippet.getSymbolTable().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{sch.subj.tbl={newcol2=[[@10,36:42='newcol2',<381>,1:36]], newcol1=[[@8,27:33='newcol1',<381>,1:27]]}, sch.subj.tab1={col2=[[@31,105:105='a',<381>,1:105]], col3=[[@49,161:161='a',<381>,1:161]], col1=[[@25,89:89='a',<381>,1:89], [@45,151:151='a',<381>,1:151]]}}",
				snippet.getTableDictionary().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				snippet.getSubstitutionsMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={att2=[[@35,115:118='att2',<381>,1:115], [@19,68:68='b',<381>,1:68]], att1=[[@29,99:102='att1',<381>,1:99], [@15,60:60='b',<381>,1:60]]}, query1={att2=[[@21,70:73='att2',<381>,1:70]], att1=[[@17,62:65='att1',<381>,1:62]]}, insert2={newcol2=[[@10,36:42='newcol2',<381>,1:36]], newcol1=[[@8,27:33='newcol1',<381>,1:27]]}}",
				snippet.getQueryColumnDictionaryMap().toString());

	}

	/**** START OF JINJA TABLE REFERENCE TESTS */
	/**
	 * Placeholder tests for newly supported JINJA table references.
	 * Expected values are intentionally failing placeholders for now.
	 */
	@Test
	public void basicJinjaTableReferenceRefSingleParameterTest() {
		final String query = "select * from {{ ref('my_model') }}";
		final Snippet snippet = runSuccessfulSQLParserTest(query, SQLPARSER_SQL_TREE_KEY);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=*, table_ref=*}}}, from={table={substitution={name={{ ref('my_model') }}, parts={jinja_table={function_name=ref, parameters={1={literal='my_model'}}}}, type=tuple}, alias=null}}}}",
			snippet.getSqlAbstractTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]",
			snippet.getQueryInterface().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={query_dictionary={*=[[@1,7:7='*',<291>,1:7]]}, table_dictionary={{{ ref('my_model') }}={*=[[@1,7:7='*',<291>,1:7]]}}, interface={*=[{name=*, table_ref=*}]}}}",
			snippet.getSymbolTable().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{{{ ref('my_model') }}={*=[[@1,7:7='*',<291>,1:7]]}}",
			snippet.getTableDictionary().toString());
		Assert.assertEquals("Substitution List is wrong", "{{{ ref('my_model') }}=tuple}",
			snippet.getSubstitutionsMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@1,7:7='*',<291>,1:7]]}}",
			snippet.getQueryColumnDictionaryMap().toString());
	}

	@Test
	public void basicJinjaTableReferenceSourceFunctionTest() {
		final String query = "select * from {{ source('raw', 'orders') }}";
		final Snippet snippet = runSuccessfulSQLParserTest(query, SQLPARSER_SQL_TREE_KEY);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=*, table_ref=*}}}, from={table={substitution={name={{ source('raw', 'orders') }}, parts={jinja_table={function_name=source, parameters={1={literal='raw'}, 2={literal='orders'}}}}, type=tuple}, alias=null}}}}",
			snippet.getSqlAbstractTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]",
			snippet.getQueryInterface().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={query_dictionary={*=[[@1,7:7='*',<291>,1:7]]}, table_dictionary={{{ source('raw', 'orders') }}={*=[[@1,7:7='*',<291>,1:7]]}}, interface={*=[{name=*, table_ref=*}]}}}",
			snippet.getSymbolTable().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{{{ source('raw', 'orders') }}={*=[[@1,7:7='*',<291>,1:7]]}}",
			snippet.getTableDictionary().toString());
		Assert.assertEquals("Substitution List is wrong", "{{{ source('raw', 'orders') }}=tuple}",
			snippet.getSubstitutionsMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@1,7:7='*',<291>,1:7]]}}",
			snippet.getQueryColumnDictionaryMap().toString());
	}

	@Test
	public void basicJinjaTableReferenceStreamFunctionTest() {
		final String query = "select * from {{ stream('event_stream') }}";
		final Snippet snippet = runSuccessfulSQLParserTest(query, SQLPARSER_SQL_TREE_KEY);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=*, table_ref=*}}}, from={table={substitution={name={{ stream('event_stream') }}, parts={jinja_table={function_name=stream, parameters={1={literal='event_stream'}}}}, type=tuple}, alias=null}}}}",
			snippet.getSqlAbstractTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]",
			snippet.getQueryInterface().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={query_dictionary={*=[[@1,7:7='*',<291>,1:7]]}, table_dictionary={{{ stream('event_stream') }}={*=[[@1,7:7='*',<291>,1:7]]}}, interface={*=[{name=*, table_ref=*}]}}}",
			snippet.getSymbolTable().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{{{ stream('event_stream') }}={*=[[@1,7:7='*',<291>,1:7]]}}",
			snippet.getTableDictionary().toString());
		Assert.assertEquals("Substitution List is wrong", "{{{ stream('event_stream') }}=tuple}",
			snippet.getSubstitutionsMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@1,7:7='*',<291>,1:7]]}}",
			snippet.getQueryColumnDictionaryMap().toString());
	}

	@Test
	public void basicJinjaTableReferenceVarFunctionTest() {
		final String query = "select * from {{ var('active_table') }}";
		final Snippet snippet = runSuccessfulSQLParserTest(query, SQLPARSER_SQL_TREE_KEY);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=*, table_ref=*}}}, from={table={substitution={name={{ var('active_table') }}, parts={jinja_table={function_name=var, parameters={1={literal='active_table'}}}}, type=tuple}, alias=null}}}}",
			snippet.getSqlAbstractTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]",
			snippet.getQueryInterface().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={query_dictionary={*=[[@1,7:7='*',<291>,1:7]]}, table_dictionary={{{ var('active_table') }}={*=[[@1,7:7='*',<291>,1:7]]}}, interface={*=[{name=*, table_ref=*}]}}}",
			snippet.getSymbolTable().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{{{ var('active_table') }}={*=[[@1,7:7='*',<291>,1:7]]}}",
			snippet.getTableDictionary().toString());
		Assert.assertEquals("Substitution List is wrong", "{{{ var('active_table') }}=tuple}",
			snippet.getSubstitutionsMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@1,7:7='*',<291>,1:7]]}}",
			snippet.getQueryColumnDictionaryMap().toString());
	}

	@Test
	public void basicJinjaTableReferenceEnvVarFunctionTest() {
		final String query = "select * from {{ env_var('DBT_TABLE_NAME') }}";
		final Snippet snippet = runSuccessfulSQLParserTest(query, SQLPARSER_SQL_TREE_KEY);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=*, table_ref=*}}}, from={table={substitution={name={{ env_var('DBT_TABLE_NAME') }}, parts={jinja_table={function_name=env_var, parameters={1={literal='DBT_TABLE_NAME'}}}}, type=tuple}, alias=null}}}}",
			snippet.getSqlAbstractTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]",
			snippet.getQueryInterface().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={query_dictionary={*=[[@1,7:7='*',<291>,1:7]]}, table_dictionary={{{ env_var('dbt_table_name') }}={*=[[@1,7:7='*',<291>,1:7]]}}, interface={*=[{name=*, table_ref=*}]}}}",
			snippet.getSymbolTable().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{{{ env_var('dbt_table_name') }}={*=[[@1,7:7='*',<291>,1:7]]}}",
			snippet.getTableDictionary().toString());
		Assert.assertEquals("Substitution List is wrong", "{{{ env_var('DBT_TABLE_NAME') }}=tuple}",
			snippet.getSubstitutionsMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@1,7:7='*',<291>,1:7]]}}",
			snippet.getQueryColumnDictionaryMap().toString());
	}

	@Test
	public void basicJinjaTableReferenceConfigDotMethodTest() {
		final String query = "select * from {{ config.get('materialized') }}";
		final Snippet snippet = runSuccessfulSQLParserTest(query, SQLPARSER_SQL_TREE_KEY);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=*, table_ref=*}}}, from={table={substitution={name={{ config.get('materialized') }}, parts={jinja_table={function_name=config.get, parameters={1={literal='materialized'}}}}, type=tuple}, alias=null}}}}",
			snippet.getSqlAbstractTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]",
			snippet.getQueryInterface().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={query_dictionary={*=[[@1,7:7='*',<291>,1:7]]}, table_dictionary={{{ config.get('materialized') }}={*=[[@1,7:7='*',<291>,1:7]]}}, interface={*=[{name=*, table_ref=*}]}}}",
			snippet.getSymbolTable().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{{{ config.get('materialized') }}={*=[[@1,7:7='*',<291>,1:7]]}}",
			snippet.getTableDictionary().toString());
		Assert.assertEquals("Substitution List is wrong", "{{{ config.get('materialized') }}=tuple}",
			snippet.getSubstitutionsMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@1,7:7='*',<291>,1:7]]}}",
			snippet.getQueryColumnDictionaryMap().toString());
	}

	@Test
	public void basicJinjaTableReferenceVariableDotAccessTest() {
		final String query = "select * from {{ target.schema }}";
		final Snippet snippet = runSuccessfulSQLParserTest(query, SQLPARSER_SQL_TREE_KEY);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=*, table_ref=*}}}, from={table={substitution={name={{ target.schema }}, parts={jinja_variable={1=target, 2=schema}}, type=tuple}, alias=null}}}}",
			snippet.getSqlAbstractTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]",
			snippet.getQueryInterface().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={query_dictionary={*=[[@1,7:7='*',<291>,1:7]]}, table_dictionary={{{ target.schema }}={*=[[@1,7:7='*',<291>,1:7]]}}, interface={*=[{name=*, table_ref=*}]}}}",
			snippet.getSymbolTable().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{{{ target.schema }}={*=[[@1,7:7='*',<291>,1:7]]}}",
			snippet.getTableDictionary().toString());
		Assert.assertEquals("Substitution List is wrong", "{{{ target.schema }}=tuple}",
			snippet.getSubstitutionsMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@1,7:7='*',<291>,1:7]]}}",
			snippet.getQueryColumnDictionaryMap().toString());
	}

	@Test
	public void basicJinjaTableReferenceThisVariableTest() {
		final String query = "select * from {{ this }}";
		final Snippet snippet = runSuccessfulSQLParserTest(query, SQLPARSER_SQL_TREE_KEY);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=*, table_ref=*}}}, from={table={substitution={name={{ this }}, parts={jinja_variable={1=this}}, type=tuple}, alias=null}}}}",
			snippet.getSqlAbstractTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]",
			snippet.getQueryInterface().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={query_dictionary={*=[[@1,7:7='*',<291>,1:7]]}, table_dictionary={{{ this }}={*=[[@1,7:7='*',<291>,1:7]]}}, interface={*=[{name=*, table_ref=*}]}}}",
			snippet.getSymbolTable().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{{{ this }}={*=[[@1,7:7='*',<291>,1:7]]}}",
			snippet.getTableDictionary().toString());
		Assert.assertEquals("Substitution List is wrong", "{{{ this }}=tuple}",
			snippet.getSubstitutionsMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@1,7:7='*',<291>,1:7]]}}",
			snippet.getQueryColumnDictionaryMap().toString());
	}

	@Test
	public void basicJinjaTableReferenceThisSchemaTest() {
		final String query = "select * from {{ this.schema }}";
		final Snippet snippet = runSuccessfulSQLParserTest(query, SQLPARSER_SQL_TREE_KEY);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=*, table_ref=*}}}, from={table={substitution={name={{ this.schema }}, parts={jinja_variable={1=this, 2=schema}}, type=tuple}, alias=null}}}}",
			snippet.getSqlAbstractTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]",
			snippet.getQueryInterface().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={query_dictionary={*=[[@1,7:7='*',<291>,1:7]]}, table_dictionary={{{ this.schema }}={*=[[@1,7:7='*',<291>,1:7]]}}, interface={*=[{name=*, table_ref=*}]}}}",
			snippet.getSymbolTable().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{{{ this.schema }}={*=[[@1,7:7='*',<291>,1:7]]}}",
			snippet.getTableDictionary().toString());
		Assert.assertEquals("Substitution List is wrong", "{{{ this.schema }}=tuple}",
			snippet.getSubstitutionsMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@1,7:7='*',<291>,1:7]]}}",
			snippet.getQueryColumnDictionaryMap().toString());
	}

	@Test
	public void basicJinjaTableReferenceThisDatabaseTest() {
		final String query = "select * from {{ this.database }}";
		final Snippet snippet = runSuccessfulSQLParserTest(query, SQLPARSER_SQL_TREE_KEY);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=*, table_ref=*}}}, from={table={substitution={name={{ this.database }}, parts={jinja_variable={1=this, 2=database}}, type=tuple}, alias=null}}}}",
			snippet.getSqlAbstractTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]",
			snippet.getQueryInterface().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={query_dictionary={*=[[@1,7:7='*',<291>,1:7]]}, table_dictionary={{{ this.database }}={*=[[@1,7:7='*',<291>,1:7]]}}, interface={*=[{name=*, table_ref=*}]}}}",
			snippet.getSymbolTable().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{{{ this.database }}={*=[[@1,7:7='*',<291>,1:7]]}}",
			snippet.getTableDictionary().toString());
		Assert.assertEquals("Substitution List is wrong", "{{{ this.database }}=tuple}",
			snippet.getSubstitutionsMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@1,7:7='*',<291>,1:7]]}}",
			snippet.getQueryColumnDictionaryMap().toString());
	}

	@Test
	public void basicJinjaTableReferenceThisDatabaseSchemaTest() {
		final String query = "select * from {{ this.database.schema }}";
		final Snippet snippet = runSuccessfulSQLParserTest(query, SQLPARSER_SQL_TREE_KEY);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=*, table_ref=*}}}, from={table={substitution={name={{ this.database.schema }}, parts={jinja_variable={1=this, 2=database, 3=schema}}, type=tuple}, alias=null}}}}",
			snippet.getSqlAbstractTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]",
			snippet.getQueryInterface().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={query_dictionary={*=[[@1,7:7='*',<291>,1:7]]}, table_dictionary={{{ this.database.schema }}={*=[[@1,7:7='*',<291>,1:7]]}}, interface={*=[{name=*, table_ref=*}]}}}",
			snippet.getSymbolTable().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{{{ this.database.schema }}={*=[[@1,7:7='*',<291>,1:7]]}}",
			snippet.getTableDictionary().toString());
		Assert.assertEquals("Substitution List is wrong", "{{{ this.database.schema }}=tuple}",
			snippet.getSubstitutionsMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@1,7:7='*',<291>,1:7]]}}",
			snippet.getQueryColumnDictionaryMap().toString());
	}

	@Test
	public void basicJinjaTableReferenceThisDatabaseSchemaTableTest() {
		final String query = "select * from {{ this.database.schema.tab }}";
		final Snippet snippet = runSuccessfulSQLParserTest(query, SQLPARSER_SQL_TREE_KEY);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=*, table_ref=*}}}, from={table={substitution={name={{ this.database.schema.tab }}, parts={jinja_variable={1=this, 2=database, 3=schema, 4=tab}}, type=tuple}, alias=null}}}}",
			snippet.getSqlAbstractTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]",
			snippet.getQueryInterface().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={query_dictionary={*=[[@1,7:7='*',<291>,1:7]]}, table_dictionary={{{ this.database.schema.tab }}={*=[[@1,7:7='*',<291>,1:7]]}}, interface={*=[{name=*, table_ref=*}]}}}",
			snippet.getSymbolTable().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{{{ this.database.schema.tab }}={*=[[@1,7:7='*',<291>,1:7]]}}",
			snippet.getTableDictionary().toString());
		Assert.assertEquals("Substitution List is wrong", "{{{ this.database.schema.tab }}=tuple}",
			snippet.getSubstitutionsMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@1,7:7='*',<291>,1:7]]}}",
			snippet.getQueryColumnDictionaryMap().toString());
	}

		/**** END OF JINJA TABLE REFERENCE TESTS */

	/***
	 * Broken UNION validation diagnostic
	 */
	@Test
	public void subqueryUnionJinjaSourceUnionInterfaceValidationV1Test() {
		final String query = "select *\n"
				+ "from\n"
				+ "(   select mail_contacts.eab_contact_id\n"
				+ "    ,mail_contacts.audience\n"
				+ "    ,mail_contacts.stream_key\n"
				+ "    ,cast(mail_contacts.intake_dt as TIMESTAMP) as valid_from_dt\n"
				+ "    from {{ source('PDP_AMS', 'pdp_ams_mail_contacts') }} as mail_contacts\n"
				+ "    union\n"
				+ "    select  offset_marketing.eab_contact_id\n"
				+ "    ,offset_marketing.audience\n"
				+ "    ,offset_marketing.stream_key\n"
				+ "    ,cast(offset_marketing.sent_dt as TIMESTAMP) as valid_from_dt\n"
				+ "    from {{ source('PDP_AMS', 'pdp_ams_offset_marketing') }} as offset_marketing\n"
				+ ") as paper_data";
		final Snippet snippet = runSuccessfulSQLParserTest(query, SQLPARSER_SQL_TREE_KEY);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=*, table_ref=*}}}, from={table={alias=paper_data, query={union={1={select={1={column={name=eab_contact_id, table_ref=mail_contacts}}, 2={column={name=audience, table_ref=mail_contacts}}, 3={column={name=stream_key, table_ref=mail_contacts}}, 4={function={function_name=cast, data_type={type=TIMESTAMP}, type=CAST, value={column={name=intake_dt, table_ref=mail_contacts}}}, alias=valid_from_dt}}, from={table={alias=mail_contacts, substitution={name={{ source('PDP_AMS', 'pdp_ams_mail_contacts') }}, parts={jinja_table={function_name=source, parameters={1={literal='PDP_AMS'}, 2={literal='pdp_ams_mail_contacts'}}}}, type=tuple}}}}, 2={union={qualifier=null, operator=union}}, 3={select={1={column={name=eab_contact_id, table_ref=offset_marketing}}, 2={column={name=audience, table_ref=offset_marketing}}, 3={column={name=stream_key, table_ref=offset_marketing}}, 4={function={function_name=cast, data_type={type=TIMESTAMP}, type=CAST, value={column={name=sent_dt, table_ref=offset_marketing}}}, alias=valid_from_dt}}, from={table={alias=offset_marketing, substitution={name={{ source('PDP_AMS', 'pdp_ams_offset_marketing') }}, parts={jinja_table={function_name=source, parameters={1={literal='PDP_AMS'}, 2={literal='pdp_ams_offset_marketing'}}}}, type=tuple}}}}}}}}}}",
			snippet.getSqlAbstractTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]",
			snippet.getQueryInterface().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query3={def_union2={query0={query_dictionary={audience=[[@11,73:80='audience',<381>,4:19]], *=[[@1,7:7='*',<291>,1:7]], stream_key=[[@15,101:110='stream_key',<381>,5:19]], eab_contact_id=[[@7,39:52='eab_contact_id',<381>,3:25]], valid_from_dt=[[@26,163:175='valid_from_dt',<381>,6:51]]}, table_dictionary={{{ source('pdp_ams', 'pdp_ams_mail_contacts') }}={audience=[[@9,59:71='mail_contacts',<381>,4:5]], stream_key=[[@13,87:99='mail_contacts',<381>,5:5]], eab_contact_id=[[@5,25:37='mail_contacts',<381>,3:11]]}}, interface={audience=[{name=audience, table_ref=mail_contacts}], stream_key=[{name=stream_key, table_ref=mail_contacts}], eab_contact_id=[{name=eab_contact_id, table_ref=mail_contacts}], valid_from_dt=[{name=intake_dt, table_ref=mail_contacts}]}, table_alias={mail_contacts={{ source('PDP_AMS', 'pdp_ams_mail_contacts') }}}}, interface={audience=[{name=audience, table_ref=mail_contacts}, {name=audience, table_ref=offset_marketing}], stream_key=[{name=stream_key, table_ref=mail_contacts}, {name=stream_key, table_ref=offset_marketing}], eab_contact_id=[{name=eab_contact_id, table_ref=mail_contacts}, {name=eab_contact_id, table_ref=offset_marketing}], valid_from_dt=[{name=intake_dt, table_ref=mail_contacts}, {name=sent_dt, table_ref=offset_marketing}]}, query1={query_dictionary={audience=[[@46,328:335='audience',<381>,10:22]], *=[[@1,7:7='*',<291>,1:7]], stream_key=[[@50,359:368='stream_key',<381>,11:22]], eab_contact_id=[[@42,291:304='eab_contact_id',<381>,9:29]], valid_from_dt=[[@61,422:434='valid_from_dt',<381>,12:52]]}, table_dictionary={{{ source('pdp_ams', 'pdp_ams_offset_marketing') }}={audience=[[@44,311:326='offset_marketing',<381>,10:5]], stream_key=[[@48,342:357='offset_marketing',<381>,11:5]], eab_contact_id=[[@40,274:289='offset_marketing',<381>,9:12]]}}, interface={audience=[{name=audience, table_ref=offset_marketing}], stream_key=[{name=stream_key, table_ref=offset_marketing}], eab_contact_id=[{name=eab_contact_id, table_ref=offset_marketing}], valid_from_dt=[{name=sent_dt, table_ref=offset_marketing}]}, table_alias={offset_marketing={{ source('PDP_AMS', 'pdp_ams_offset_marketing') }}}}}, query_dictionary={*=[[@1,7:7='*',<291>,1:7]]}, table_dictionary={}, interface={*=[{name=*, table_ref=*}]}, table_alias={paper_data=union2}}}",
			snippet.getSymbolTable().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{{{ source('pdp_ams', 'pdp_ams_offset_marketing') }}={audience=[[@44,311:326='offset_marketing',<381>,10:5]], sent_dt=[[@54,380:395='offset_marketing',<381>,12:10]], stream_key=[[@48,342:357='offset_marketing',<381>,11:5]], eab_contact_id=[[@40,274:289='offset_marketing',<381>,9:12]]}, {{ source('PDP_AMS', 'pdp_ams_mail_contacts') }}={audience=[[@9,59:71='mail_contacts',<381>,4:5]], intake_dt=[[@19,122:134='mail_contacts',<381>,6:10]], stream_key=[[@13,87:99='mail_contacts',<381>,5:5]], eab_contact_id=[[@5,25:37='mail_contacts',<381>,3:11]]}, {{ source('pdp_ams', 'pdp_ams_mail_contacts') }}={audience=[[@9,59:71='mail_contacts',<381>,4:5]], intake_dt=[[@19,122:134='mail_contacts',<381>,6:10]], stream_key=[[@13,87:99='mail_contacts',<381>,5:5]], eab_contact_id=[[@5,25:37='mail_contacts',<381>,3:11]]}, {{ source('PDP_AMS', 'pdp_ams_offset_marketing') }}={audience=[[@44,311:326='offset_marketing',<381>,10:5]], sent_dt=[[@54,380:395='offset_marketing',<381>,12:10]], stream_key=[[@48,342:357='offset_marketing',<381>,11:5]], eab_contact_id=[[@40,274:289='offset_marketing',<381>,9:12]]}}",
			snippet.getTableDictionary().toString());
		Assert.assertEquals("Substitution List is wrong", "{{{ source('PDP_AMS', 'pdp_ams_mail_contacts') }}=tuple, {{ source('PDP_AMS', 'pdp_ams_offset_marketing') }}=tuple}",
			snippet.getSubstitutionsMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={audience=[[@11,73:80='audience',<381>,4:19]], *=[[@1,7:7='*',<291>,1:7]], stream_key=[[@15,101:110='stream_key',<381>,5:19]], eab_contact_id=[[@7,39:52='eab_contact_id',<381>,3:25]], valid_from_dt=[[@26,163:175='valid_from_dt',<381>,6:51]]}, query1={audience=[[@46,328:335='audience',<381>,10:22]], *=[[@1,7:7='*',<291>,1:7]], stream_key=[[@50,359:368='stream_key',<381>,11:22]], eab_contact_id=[[@42,291:304='eab_contact_id',<381>,9:29]], valid_from_dt=[[@61,422:434='valid_from_dt',<381>,12:52]]}, query3={*=[[@1,7:7='*',<291>,1:7]]}}",
			snippet.getQueryColumnDictionaryMap().toString());
	}

	@Test
	public void subqueryUnionJinjaSourceUnionInterfaceValidationV2Test() {
		final String query = "select paper_data.eab_contact_id\n" 
				+ "       ,paper_data.audience\n" 
				+ "       ,paper_data.stream_key\n" 
				+ "       ,paper_data.valid_from_dt\n" 
				+ "       ,row_number() over (partition by paper_data.eab_contact_id,paper_data.stream_key order by paper_data.valid_from_dt desc) as rno"
				+ " from\n"
				+ "(   select mail_contacts.eab_contact_id\n"
				+ "    ,mail_contacts.audience\n"
				+ "    ,mail_contacts.stream_key\n"
				+ "    ,cast(mail_contacts.intake_dt as TIMESTAMP) as valid_from_dt\n"
				+ "    from {{ source('PDP_AMS', 'pdp_ams_mail_contacts') }} as mail_contacts\n"
				+ "    union\n"
				+ "    select  offset_marketing.eab_contact_id\n"
				+ "    ,offset_marketing.audience\n"
				+ "    ,offset_marketing.stream_key\n"
				+ "    ,cast(offset_marketing.sent_dt as TIMESTAMP) as valid_from_dt\n"
				+ "    from {{ source('PDP_AMS', 'pdp_ams_offset_marketing') }} as offset_marketing\n"
				+ ") as paper_data";
		final Snippet snippet = runSuccessfulSQLParserTest(query, SQLPARSER_SQL_TREE_KEY);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=eab_contact_id, table_ref=paper_data}}, 2={column={name=audience, table_ref=paper_data}}, 3={column={name=stream_key, table_ref=paper_data}}, 4={column={name=valid_from_dt, table_ref=paper_data}}, 5={alias=rno, window_function={over={partition_by={1={column={name=eab_contact_id, table_ref=paper_data}}, 2={column={name=stream_key, table_ref=paper_data}}}, orderby={1={null_order=null, predicand={column={name=valid_from_dt, table_ref=paper_data}}, sort_order=desc}}}, function={function_name=row_number, parameters=null}}}}, from={table={alias=paper_data, query={union={1={select={1={column={name=eab_contact_id, table_ref=mail_contacts}}, 2={column={name=audience, table_ref=mail_contacts}}, 3={column={name=stream_key, table_ref=mail_contacts}}, 4={function={function_name=cast, data_type={type=TIMESTAMP}, type=CAST, value={column={name=intake_dt, table_ref=mail_contacts}}}, alias=valid_from_dt}}, from={table={alias=mail_contacts, substitution={name={{ source('PDP_AMS', 'pdp_ams_mail_contacts') }}, parts={jinja_table={function_name=source, parameters={1={literal='PDP_AMS'}, 2={literal='pdp_ams_mail_contacts'}}}}, type=tuple}}}}, 2={union={qualifier=null, operator=union}}, 3={select={1={column={name=eab_contact_id, table_ref=offset_marketing}}, 2={column={name=audience, table_ref=offset_marketing}}, 3={column={name=stream_key, table_ref=offset_marketing}}, 4={function={function_name=cast, data_type={type=TIMESTAMP}, type=CAST, value={column={name=sent_dt, table_ref=offset_marketing}}}, alias=valid_from_dt}}, from={table={alias=offset_marketing, substitution={name={{ source('PDP_AMS', 'pdp_ams_offset_marketing') }}, parts={jinja_table={function_name=source, parameters={1={literal='PDP_AMS'}, 2={literal='pdp_ams_offset_marketing'}}}}, type=tuple}}}}}}}}}}",
			snippet.getSqlAbstractTree().toString());
		Assert.assertEquals("Interface is wrong", "[audience, rno, stream_key, eab_contact_id, valid_from_dt]",
			snippet.getQueryInterface().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query3={def_union2={query0={query_dictionary={audience=[[@49,323:330='audience',<381>,7:19]], stream_key=[[@53,351:360='stream_key',<381>,8:19]], eab_contact_id=[[@45,289:302='eab_contact_id',<381>,6:25]], valid_from_dt=[[@64,413:425='valid_from_dt',<381>,9:51]]}, table_dictionary={{{ source('pdp_ams', 'pdp_ams_mail_contacts') }}={audience=[[@47,309:321='mail_contacts',<381>,7:5]], stream_key=[[@51,337:349='mail_contacts',<381>,8:5]], eab_contact_id=[[@43,275:287='mail_contacts',<381>,6:11]]}}, interface={audience=[{name=audience, table_ref=mail_contacts}], stream_key=[{name=stream_key, table_ref=mail_contacts}], eab_contact_id=[{name=eab_contact_id, table_ref=mail_contacts}], valid_from_dt=[{name=intake_dt, table_ref=mail_contacts}]}, table_alias={mail_contacts={{ source('PDP_AMS', 'pdp_ams_mail_contacts') }}}}, interface={audience=[{name=audience, table_ref=mail_contacts}, {name=audience, table_ref=offset_marketing}], stream_key=[{name=stream_key, table_ref=mail_contacts}, {name=stream_key, table_ref=offset_marketing}], eab_contact_id=[{name=eab_contact_id, table_ref=mail_contacts}, {name=eab_contact_id, table_ref=offset_marketing}], valid_from_dt=[{name=intake_dt, table_ref=mail_contacts}, {name=sent_dt, table_ref=offset_marketing}]}, query1={query_dictionary={audience=[[@84,578:585='audience',<381>,13:22]], stream_key=[[@88,609:618='stream_key',<381>,14:22]], eab_contact_id=[[@80,541:554='eab_contact_id',<381>,12:29]], valid_from_dt=[[@99,672:684='valid_from_dt',<381>,15:52]]}, table_dictionary={{{ source('pdp_ams', 'pdp_ams_offset_marketing') }}={audience=[[@82,561:576='offset_marketing',<381>,13:5]], stream_key=[[@86,592:607='offset_marketing',<381>,14:5]], eab_contact_id=[[@78,524:539='offset_marketing',<381>,12:12]]}}, interface={audience=[{name=audience, table_ref=offset_marketing}], stream_key=[{name=stream_key, table_ref=offset_marketing}], eab_contact_id=[{name=eab_contact_id, table_ref=offset_marketing}], valid_from_dt=[{name=sent_dt, table_ref=offset_marketing}]}, table_alias={offset_marketing={{ source('PDP_AMS', 'pdp_ams_offset_marketing') }}}}}, query_dictionary={audience=[[@7,52:59='audience',<381>,2:19]], rno=[[@39,255:257='rno',<381>,5:131]], stream_key=[[@11,80:89='stream_key',<381>,3:19]], eab_contact_id=[[@3,18:31='eab_contact_id',<381>,1:18]], valid_from_dt=[[@15,110:122='valid_from_dt',<381>,4:19]]}, table_dictionary={}, interface={audience=[{name=audience, table_ref=paper_data}], rno=[{name=eab_contact_id, table_ref=paper_data}, {name=stream_key, table_ref=paper_data}, {name=valid_from_dt, table_ref=paper_data}], stream_key=[{name=stream_key, table_ref=paper_data}], eab_contact_id=[{name=eab_contact_id, table_ref=paper_data}], valid_from_dt=[{name=valid_from_dt, table_ref=paper_data}]}, table_alias={paper_data=union2}}}",
			snippet.getSymbolTable().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{{{ source('pdp_ams', 'pdp_ams_offset_marketing') }}={audience=[[@82,561:576='offset_marketing',<381>,13:5]], sent_dt=[[@92,630:645='offset_marketing',<381>,15:10]], stream_key=[[@86,592:607='offset_marketing',<381>,14:5]], eab_contact_id=[[@78,524:539='offset_marketing',<381>,12:12]]}, {{ source('PDP_AMS', 'pdp_ams_mail_contacts') }}={audience=[[@47,309:321='mail_contacts',<381>,7:5]], intake_dt=[[@57,372:384='mail_contacts',<381>,9:10]], stream_key=[[@51,337:349='mail_contacts',<381>,8:5]], eab_contact_id=[[@43,275:287='mail_contacts',<381>,6:11]]}, {{ source('pdp_ams', 'pdp_ams_mail_contacts') }}={audience=[[@47,309:321='mail_contacts',<381>,7:5]], intake_dt=[[@57,372:384='mail_contacts',<381>,9:10]], stream_key=[[@51,337:349='mail_contacts',<381>,8:5]], eab_contact_id=[[@43,275:287='mail_contacts',<381>,6:11]]}, {{ source('PDP_AMS', 'pdp_ams_offset_marketing') }}={audience=[[@82,561:576='offset_marketing',<381>,13:5]], sent_dt=[[@92,630:645='offset_marketing',<381>,15:10]], stream_key=[[@86,592:607='offset_marketing',<381>,14:5]], eab_contact_id=[[@78,524:539='offset_marketing',<381>,12:12]]}}",
			snippet.getTableDictionary().toString());
		Assert.assertEquals("Substitution List is wrong", "{{{ source('PDP_AMS', 'pdp_ams_mail_contacts') }}=tuple, {{ source('PDP_AMS', 'pdp_ams_offset_marketing') }}=tuple}",
			snippet.getSubstitutionsMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={audience=[[@49,323:330='audience',<381>,7:19]], stream_key=[[@53,351:360='stream_key',<381>,8:19]], eab_contact_id=[[@45,289:302='eab_contact_id',<381>,6:25]], valid_from_dt=[[@64,413:425='valid_from_dt',<381>,9:51]]}, query1={audience=[[@84,578:585='audience',<381>,13:22]], stream_key=[[@88,609:618='stream_key',<381>,14:22]], eab_contact_id=[[@80,541:554='eab_contact_id',<381>,12:29]], valid_from_dt=[[@99,672:684='valid_from_dt',<381>,15:52]]}, query3={audience=[[@7,52:59='audience',<381>,2:19]], rno=[[@39,255:257='rno',<381>,5:131]], stream_key=[[@11,80:89='stream_key',<381>,3:19]], eab_contact_id=[[@3,18:31='eab_contact_id',<381>,1:18]], valid_from_dt=[[@15,110:122='valid_from_dt',<381>,4:19]]}}",
			snippet.getQueryColumnDictionaryMap().toString());
	}

	@Test
	public void subqueryIntersectJinjaSourceUnionInterfaceValidationV1Test() {
		final String query = "select *\n"
				+ "from\n"
				+ "(   select mail_contacts.eab_contact_id\n"
				+ "    ,mail_contacts.audience\n"
				+ "    ,mail_contacts.stream_key\n"
				+ "    ,cast(mail_contacts.intake_dt as TIMESTAMP) as valid_from_dt\n"
				+ "    from {{ source('PDP_AMS', 'pdp_ams_mail_contacts') }} as mail_contacts\n"
				+ "    intersect\n"
				+ "    select  offset_marketing.eab_contact_id\n"
				+ "    ,offset_marketing.audience\n"
				+ "    ,offset_marketing.stream_key\n"
				+ "    ,cast(offset_marketing.sent_dt as TIMESTAMP) as valid_from_dt\n"
				+ "    from {{ source('PDP_AMS', 'pdp_ams_offset_marketing') }} as offset_marketing\n"
				+ ") as paper_data";
		final Snippet snippet = runSuccessfulSQLParserTest(query, SQLPARSER_SQL_TREE_KEY);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=*, table_ref=*}}}, from={table={alias=paper_data, query={intersect={1={select={1={column={name=eab_contact_id, table_ref=mail_contacts}}, 2={column={name=audience, table_ref=mail_contacts}}, 3={column={name=stream_key, table_ref=mail_contacts}}, 4={function={function_name=cast, data_type={type=TIMESTAMP}, type=CAST, value={column={name=intake_dt, table_ref=mail_contacts}}}, alias=valid_from_dt}}, from={table={alias=mail_contacts, substitution={name={{ source('PDP_AMS', 'pdp_ams_mail_contacts') }}, parts={jinja_table={function_name=source, parameters={1={literal='PDP_AMS'}, 2={literal='pdp_ams_mail_contacts'}}}}, type=tuple}}}}, 2={intersect={qualifier=null, operator=intersect}}, 3={select={1={column={name=eab_contact_id, table_ref=offset_marketing}}, 2={column={name=audience, table_ref=offset_marketing}}, 3={column={name=stream_key, table_ref=offset_marketing}}, 4={function={function_name=cast, data_type={type=TIMESTAMP}, type=CAST, value={column={name=sent_dt, table_ref=offset_marketing}}}, alias=valid_from_dt}}, from={table={alias=offset_marketing, substitution={name={{ source('PDP_AMS', 'pdp_ams_offset_marketing') }}, parts={jinja_table={function_name=source, parameters={1={literal='PDP_AMS'}, 2={literal='pdp_ams_offset_marketing'}}}}, type=tuple}}}}}}}}}}",
			snippet.getSqlAbstractTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]",
			snippet.getQueryInterface().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query3={query_dictionary={*=[[@1,7:7='*',<291>,1:7]]}, table_dictionary={}, def_intersect2={query0={query_dictionary={audience=[[@11,73:80='audience',<381>,4:19]], *=[[@1,7:7='*',<291>,1:7]], stream_key=[[@15,101:110='stream_key',<381>,5:19]], eab_contact_id=[[@7,39:52='eab_contact_id',<381>,3:25]], valid_from_dt=[[@26,163:175='valid_from_dt',<381>,6:51]]}, table_dictionary={{{ source('pdp_ams', 'pdp_ams_mail_contacts') }}={audience=[[@9,59:71='mail_contacts',<381>,4:5]], stream_key=[[@13,87:99='mail_contacts',<381>,5:5]], eab_contact_id=[[@5,25:37='mail_contacts',<381>,3:11]]}}, interface={audience=[{name=audience, table_ref=mail_contacts}], stream_key=[{name=stream_key, table_ref=mail_contacts}], eab_contact_id=[{name=eab_contact_id, table_ref=mail_contacts}], valid_from_dt=[{name=intake_dt, table_ref=mail_contacts}]}, table_alias={mail_contacts={{ source('PDP_AMS', 'pdp_ams_mail_contacts') }}}}, interface={audience=[{name=audience, table_ref=mail_contacts}, {name=audience, table_ref=offset_marketing}], stream_key=[{name=stream_key, table_ref=mail_contacts}, {name=stream_key, table_ref=offset_marketing}], eab_contact_id=[{name=eab_contact_id, table_ref=mail_contacts}, {name=eab_contact_id, table_ref=offset_marketing}], valid_from_dt=[{name=intake_dt, table_ref=mail_contacts}, {name=sent_dt, table_ref=offset_marketing}]}, query1={query_dictionary={audience=[[@46,332:339='audience',<381>,10:22]], *=[[@1,7:7='*',<291>,1:7]], stream_key=[[@50,363:372='stream_key',<381>,11:22]], eab_contact_id=[[@42,295:308='eab_contact_id',<381>,9:29]], valid_from_dt=[[@61,426:438='valid_from_dt',<381>,12:52]]}, table_dictionary={{{ source('pdp_ams', 'pdp_ams_offset_marketing') }}={audience=[[@44,315:330='offset_marketing',<381>,10:5]], stream_key=[[@48,346:361='offset_marketing',<381>,11:5]], eab_contact_id=[[@40,278:293='offset_marketing',<381>,9:12]]}}, interface={audience=[{name=audience, table_ref=offset_marketing}], stream_key=[{name=stream_key, table_ref=offset_marketing}], eab_contact_id=[{name=eab_contact_id, table_ref=offset_marketing}], valid_from_dt=[{name=sent_dt, table_ref=offset_marketing}]}, table_alias={offset_marketing={{ source('PDP_AMS', 'pdp_ams_offset_marketing') }}}}}, interface={*=[{name=*, table_ref=*}]}, table_alias={paper_data=intersect2}}}",
			snippet.getSymbolTable().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{{{ source('pdp_ams', 'pdp_ams_offset_marketing') }}={audience=[[@44,315:330='offset_marketing',<381>,10:5]], sent_dt=[[@54,384:399='offset_marketing',<381>,12:10]], stream_key=[[@48,346:361='offset_marketing',<381>,11:5]], eab_contact_id=[[@40,278:293='offset_marketing',<381>,9:12]]}, {{ source('PDP_AMS', 'pdp_ams_mail_contacts') }}={audience=[[@9,59:71='mail_contacts',<381>,4:5]], intake_dt=[[@19,122:134='mail_contacts',<381>,6:10]], stream_key=[[@13,87:99='mail_contacts',<381>,5:5]], eab_contact_id=[[@5,25:37='mail_contacts',<381>,3:11]]}, {{ source('pdp_ams', 'pdp_ams_mail_contacts') }}={audience=[[@9,59:71='mail_contacts',<381>,4:5]], intake_dt=[[@19,122:134='mail_contacts',<381>,6:10]], stream_key=[[@13,87:99='mail_contacts',<381>,5:5]], eab_contact_id=[[@5,25:37='mail_contacts',<381>,3:11]]}, {{ source('PDP_AMS', 'pdp_ams_offset_marketing') }}={audience=[[@44,315:330='offset_marketing',<381>,10:5]], sent_dt=[[@54,384:399='offset_marketing',<381>,12:10]], stream_key=[[@48,346:361='offset_marketing',<381>,11:5]], eab_contact_id=[[@40,278:293='offset_marketing',<381>,9:12]]}}",
			snippet.getTableDictionary().toString());
		Assert.assertEquals("Substitution List is wrong", "{{{ source('PDP_AMS', 'pdp_ams_mail_contacts') }}=tuple, {{ source('PDP_AMS', 'pdp_ams_offset_marketing') }}=tuple}",
			snippet.getSubstitutionsMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={audience=[[@11,73:80='audience',<381>,4:19]], *=[[@1,7:7='*',<291>,1:7]], stream_key=[[@15,101:110='stream_key',<381>,5:19]], eab_contact_id=[[@7,39:52='eab_contact_id',<381>,3:25]], valid_from_dt=[[@26,163:175='valid_from_dt',<381>,6:51]]}, query1={audience=[[@46,332:339='audience',<381>,10:22]], *=[[@1,7:7='*',<291>,1:7]], stream_key=[[@50,363:372='stream_key',<381>,11:22]], eab_contact_id=[[@42,295:308='eab_contact_id',<381>,9:29]], valid_from_dt=[[@61,426:438='valid_from_dt',<381>,12:52]]}, query3={*=[[@1,7:7='*',<291>,1:7]]}}",
			snippet.getQueryColumnDictionaryMap().toString());
	}

	@Test
	public void subqueryIntersectJinjaSourceUnionInterfaceValidationV2Test() {
		final String query = "select paper_data.eab_contact_id\n" 
				+ "       ,paper_data.audience\n" 
				+ "       ,paper_data.stream_key\n" 
				+ "       ,paper_data.valid_from_dt\n" 
				+ "       ,row_number() over (partition by paper_data.eab_contact_id,paper_data.stream_key order by paper_data.valid_from_dt desc) as rno"
				+ " from\n"
				+ "(   select mail_contacts.eab_contact_id\n"
				+ "    ,mail_contacts.audience\n"
				+ "    ,mail_contacts.stream_key\n"
				+ "    ,cast(mail_contacts.intake_dt as TIMESTAMP) as valid_from_dt\n"
				+ "    from {{ source('PDP_AMS', 'pdp_ams_mail_contacts') }} as mail_contacts\n"
				+ "    intersect\n"
				+ "    select  offset_marketing.eab_contact_id\n"
				+ "    ,offset_marketing.audience\n"
				+ "    ,offset_marketing.stream_key\n"
				+ "    ,cast(offset_marketing.sent_dt as TIMESTAMP) as valid_from_dt\n"
				+ "    from {{ source('PDP_AMS', 'pdp_ams_offset_marketing') }} as offset_marketing\n"
				+ ") as paper_data";
		final Snippet snippet = runSuccessfulSQLParserTest(query, SQLPARSER_SQL_TREE_KEY);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=eab_contact_id, table_ref=paper_data}}, 2={column={name=audience, table_ref=paper_data}}, 3={column={name=stream_key, table_ref=paper_data}}, 4={column={name=valid_from_dt, table_ref=paper_data}}, 5={alias=rno, window_function={over={partition_by={1={column={name=eab_contact_id, table_ref=paper_data}}, 2={column={name=stream_key, table_ref=paper_data}}}, orderby={1={null_order=null, predicand={column={name=valid_from_dt, table_ref=paper_data}}, sort_order=desc}}}, function={function_name=row_number, parameters=null}}}}, from={table={alias=paper_data, query={intersect={1={select={1={column={name=eab_contact_id, table_ref=mail_contacts}}, 2={column={name=audience, table_ref=mail_contacts}}, 3={column={name=stream_key, table_ref=mail_contacts}}, 4={function={function_name=cast, data_type={type=TIMESTAMP}, type=CAST, value={column={name=intake_dt, table_ref=mail_contacts}}}, alias=valid_from_dt}}, from={table={alias=mail_contacts, substitution={name={{ source('PDP_AMS', 'pdp_ams_mail_contacts') }}, parts={jinja_table={function_name=source, parameters={1={literal='PDP_AMS'}, 2={literal='pdp_ams_mail_contacts'}}}}, type=tuple}}}}, 2={intersect={qualifier=null, operator=intersect}}, 3={select={1={column={name=eab_contact_id, table_ref=offset_marketing}}, 2={column={name=audience, table_ref=offset_marketing}}, 3={column={name=stream_key, table_ref=offset_marketing}}, 4={function={function_name=cast, data_type={type=TIMESTAMP}, type=CAST, value={column={name=sent_dt, table_ref=offset_marketing}}}, alias=valid_from_dt}}, from={table={alias=offset_marketing, substitution={name={{ source('PDP_AMS', 'pdp_ams_offset_marketing') }}, parts={jinja_table={function_name=source, parameters={1={literal='PDP_AMS'}, 2={literal='pdp_ams_offset_marketing'}}}}, type=tuple}}}}}}}}}}",
			snippet.getSqlAbstractTree().toString());
		Assert.assertEquals("Interface is wrong", "[audience, rno, stream_key, eab_contact_id, valid_from_dt]",
			snippet.getQueryInterface().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query3={query_dictionary={audience=[[@7,52:59='audience',<381>,2:19]], rno=[[@39,255:257='rno',<381>,5:131]], stream_key=[[@11,80:89='stream_key',<381>,3:19]], eab_contact_id=[[@3,18:31='eab_contact_id',<381>,1:18]], valid_from_dt=[[@15,110:122='valid_from_dt',<381>,4:19]]}, table_dictionary={}, def_intersect2={query0={query_dictionary={audience=[[@49,323:330='audience',<381>,7:19]], stream_key=[[@53,351:360='stream_key',<381>,8:19]], eab_contact_id=[[@45,289:302='eab_contact_id',<381>,6:25]], valid_from_dt=[[@64,413:425='valid_from_dt',<381>,9:51]]}, table_dictionary={{{ source('pdp_ams', 'pdp_ams_mail_contacts') }}={audience=[[@47,309:321='mail_contacts',<381>,7:5]], stream_key=[[@51,337:349='mail_contacts',<381>,8:5]], eab_contact_id=[[@43,275:287='mail_contacts',<381>,6:11]]}}, interface={audience=[{name=audience, table_ref=mail_contacts}], stream_key=[{name=stream_key, table_ref=mail_contacts}], eab_contact_id=[{name=eab_contact_id, table_ref=mail_contacts}], valid_from_dt=[{name=intake_dt, table_ref=mail_contacts}]}, table_alias={mail_contacts={{ source('PDP_AMS', 'pdp_ams_mail_contacts') }}}}, interface={audience=[{name=audience, table_ref=mail_contacts}, {name=audience, table_ref=offset_marketing}], stream_key=[{name=stream_key, table_ref=mail_contacts}, {name=stream_key, table_ref=offset_marketing}], eab_contact_id=[{name=eab_contact_id, table_ref=mail_contacts}, {name=eab_contact_id, table_ref=offset_marketing}], valid_from_dt=[{name=intake_dt, table_ref=mail_contacts}, {name=sent_dt, table_ref=offset_marketing}]}, query1={query_dictionary={audience=[[@84,582:589='audience',<381>,13:22]], stream_key=[[@88,613:622='stream_key',<381>,14:22]], eab_contact_id=[[@80,545:558='eab_contact_id',<381>,12:29]], valid_from_dt=[[@99,676:688='valid_from_dt',<381>,15:52]]}, table_dictionary={{{ source('pdp_ams', 'pdp_ams_offset_marketing') }}={audience=[[@82,565:580='offset_marketing',<381>,13:5]], stream_key=[[@86,596:611='offset_marketing',<381>,14:5]], eab_contact_id=[[@78,528:543='offset_marketing',<381>,12:12]]}}, interface={audience=[{name=audience, table_ref=offset_marketing}], stream_key=[{name=stream_key, table_ref=offset_marketing}], eab_contact_id=[{name=eab_contact_id, table_ref=offset_marketing}], valid_from_dt=[{name=sent_dt, table_ref=offset_marketing}]}, table_alias={offset_marketing={{ source('PDP_AMS', 'pdp_ams_offset_marketing') }}}}}, interface={audience=[{name=audience, table_ref=paper_data}], rno=[{name=eab_contact_id, table_ref=paper_data}, {name=stream_key, table_ref=paper_data}, {name=valid_from_dt, table_ref=paper_data}], stream_key=[{name=stream_key, table_ref=paper_data}], eab_contact_id=[{name=eab_contact_id, table_ref=paper_data}], valid_from_dt=[{name=valid_from_dt, table_ref=paper_data}]}, table_alias={paper_data=intersect2}}}",
			snippet.getSymbolTable().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{{{ source('pdp_ams', 'pdp_ams_offset_marketing') }}={audience=[[@82,565:580='offset_marketing',<381>,13:5]], sent_dt=[[@92,634:649='offset_marketing',<381>,15:10]], stream_key=[[@86,596:611='offset_marketing',<381>,14:5]], eab_contact_id=[[@78,528:543='offset_marketing',<381>,12:12]]}, {{ source('PDP_AMS', 'pdp_ams_mail_contacts') }}={audience=[[@47,309:321='mail_contacts',<381>,7:5]], intake_dt=[[@57,372:384='mail_contacts',<381>,9:10]], stream_key=[[@51,337:349='mail_contacts',<381>,8:5]], eab_contact_id=[[@43,275:287='mail_contacts',<381>,6:11]]}, {{ source('pdp_ams', 'pdp_ams_mail_contacts') }}={audience=[[@47,309:321='mail_contacts',<381>,7:5]], intake_dt=[[@57,372:384='mail_contacts',<381>,9:10]], stream_key=[[@51,337:349='mail_contacts',<381>,8:5]], eab_contact_id=[[@43,275:287='mail_contacts',<381>,6:11]]}, {{ source('PDP_AMS', 'pdp_ams_offset_marketing') }}={audience=[[@82,565:580='offset_marketing',<381>,13:5]], sent_dt=[[@92,634:649='offset_marketing',<381>,15:10]], stream_key=[[@86,596:611='offset_marketing',<381>,14:5]], eab_contact_id=[[@78,528:543='offset_marketing',<381>,12:12]]}}",
			snippet.getTableDictionary().toString());
		Assert.assertEquals("Substitution List is wrong", "{{{ source('PDP_AMS', 'pdp_ams_mail_contacts') }}=tuple, {{ source('PDP_AMS', 'pdp_ams_offset_marketing') }}=tuple}",
			snippet.getSubstitutionsMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={audience=[[@49,323:330='audience',<381>,7:19]], stream_key=[[@53,351:360='stream_key',<381>,8:19]], eab_contact_id=[[@45,289:302='eab_contact_id',<381>,6:25]], valid_from_dt=[[@64,413:425='valid_from_dt',<381>,9:51]]}, query1={audience=[[@84,582:589='audience',<381>,13:22]], stream_key=[[@88,613:622='stream_key',<381>,14:22]], eab_contact_id=[[@80,545:558='eab_contact_id',<381>,12:29]], valid_from_dt=[[@99,676:688='valid_from_dt',<381>,15:52]]}, query3={audience=[[@7,52:59='audience',<381>,2:19]], rno=[[@39,255:257='rno',<381>,5:131]], stream_key=[[@11,80:89='stream_key',<381>,3:19]], eab_contact_id=[[@3,18:31='eab_contact_id',<381>,1:18]], valid_from_dt=[[@15,110:122='valid_from_dt',<381>,4:19]]}}",
			snippet.getQueryColumnDictionaryMap().toString());
	}

	@Test
	public void multipleIntersectSubqueryInterfaceValidationV1Test() {
		final String query = "select a,b,c,d from \n"
				+ "(   select a,b,c from t1"
				+ "    intersect\n"
				+ "    select b,c,d,a from t2"
				+ ") as i1\n"
				+ "join \n"
				+ "(   select a,b,c,d from t1"
				+ "    intersect\n"
				+ "    select b,c,d from t2"
				+ ") as i2\n";
		final Snippet snippet = runFailedSyntaxSQLParserTest(query, SQLPARSER_SQL_TREE_KEY, 2);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=a, table_ref=null}}, 2={column={name=b, table_ref=null}}, 3={column={name=c, table_ref=null}}, 4={column={name=d, table_ref=null}}}, from={join={1={table={alias=i1, query={intersect={1={select={1={column={name=a, table_ref=null}}, 2={column={name=b, table_ref=null}}, 3={column={name=c, table_ref=null}}}, from={table={alias=null, table=t1}}}, 2={intersect={qualifier=null, operator=intersect}}, 3={select={1={column={name=b, table_ref=null}}, 2={column={name=c, table_ref=null}}, 3={column={name=d, table_ref=null}}, 4={column={name=a, table_ref=null}}}, from={table={alias=null, table=t2}}}}}}}, 2={join=join}, 3={table={alias=i2, query={intersect={1={select={1={column={name=a, table_ref=null}}, 2={column={name=b, table_ref=null}}, 3={column={name=c, table_ref=null}}, 4={column={name=d, table_ref=null}}}, from={table={alias=null, table=t1}}}, 2={intersect={qualifier=null, operator=intersect}}, 3={select={1={column={name=b, table_ref=null}}, 2={column={name=c, table_ref=null}}, 3={column={name=d, table_ref=null}}}, from={table={alias=null, table=t2}}}}}}}}}}}",
			snippet.getSqlAbstractTree().toString());
		Assert.assertEquals("Interface is wrong", "[a, b, c, d]",
			snippet.getQueryInterface().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query6={query_dictionary={a=[[@1,7:7='a',<381>,1:7]], b=[[@3,9:9='b',<381>,1:9]], c=[[@5,11:11='c',<381>,1:11]], d=[[@7,13:13='d',<381>,1:13]]}, table_dictionary={}, def_intersect2={query0={query_dictionary={a=[[@11,32:32='a',<381>,2:11]], b=[[@13,34:34='b',<381>,2:13]], c=[[@15,36:36='c',<381>,2:15]]}, table_dictionary={t1={a=[[@11,32:32='a',<381>,2:11], [@35,110:110='a',<381>,5:11]], b=[[@13,34:34='b',<381>,2:13], [@37,112:112='b',<381>,5:13]], c=[[@15,36:36='c',<381>,2:15], [@39,114:114='c',<381>,5:15]]}}, interface={a=[{name=a, table_ref=t1}], b=[{name=b, table_ref=t1}], c=[{name=c, table_ref=t1}]}}, interface={a=[{name=a, table_ref=t1}, {name=b, table_ref=t2}], b=[{name=b, table_ref=t1}, {name=c, table_ref=t2}], c=[{name=c, table_ref=t1}, {name=d, table_ref=t2}]}, query1={query_dictionary={a=[[@26,76:76='a',<381>,3:17]], b=[[@20,70:70='b',<381>,3:11]], c=[[@22,72:72='c',<381>,3:13]], d=[[@24,74:74='d',<381>,3:15]]}, table_dictionary={t2={a=[[@26,76:76='a',<381>,3:17]], b=[[@20,70:70='b',<381>,3:11], [@46,150:150='b',<381>,6:11]], c=[[@22,72:72='c',<381>,3:13], [@48,152:152='c',<381>,6:13]], d=[[@24,74:74='d',<381>,3:15], [@50,154:154='d',<381>,6:15]]}}, interface={a=[{name=a, table_ref=t2}], b=[{name=b, table_ref=t2}], c=[{name=c, table_ref=t2}], d=[{name=d, table_ref=t2}]}}}, def_intersect5={query4={query_dictionary={b=[[@46,150:150='b',<381>,6:11]], c=[[@48,152:152='c',<381>,6:13]], d=[[@50,154:154='d',<381>,6:15]]}, table_dictionary={t2={b=[[@46,150:150='b',<381>,6:11]], c=[[@48,152:152='c',<381>,6:13]], d=[[@50,154:154='d',<381>,6:15]]}}, interface={b=[{name=b, table_ref=t2}], c=[{name=c, table_ref=t2}], d=[{name=d, table_ref=t2}]}}, interface={a=[{name=a, table_ref=t1}, {name=b, table_ref=t2}], b=[{name=b, table_ref=t1}, {name=c, table_ref=t2}], c=[{name=c, table_ref=t1}, {name=d, table_ref=t2}], d=[{name=d, table_ref=t1}]}, query3={query_dictionary={a=[[@35,110:110='a',<381>,5:11]], b=[[@37,112:112='b',<381>,5:13]], c=[[@39,114:114='c',<381>,5:15]], d=[[@41,116:116='d',<381>,5:17]]}, table_dictionary={t1={a=[[@35,110:110='a',<381>,5:11]], b=[[@37,112:112='b',<381>,5:13]], c=[[@39,114:114='c',<381>,5:15]], d=[[@41,116:116='d',<381>,5:17]]}}, interface={a=[{name=a, table_ref=t1}], b=[{name=b, table_ref=t1}], c=[{name=c, table_ref=t1}], d=[{name=d, table_ref=t1}]}}}, interface={a=[{name=a, table_ref=null}], b=[{name=b, table_ref=null}], c=[{name=c, table_ref=null}], d=[{name=d, table_ref=intersect5}]}, table_alias={i1=intersect2, i2=intersect5}}}",
			snippet.getSymbolTable().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{t1={a=[[@11,32:32='a',<381>,2:11], [@35,110:110='a',<381>,5:11]], b=[[@13,34:34='b',<381>,2:13], [@37,112:112='b',<381>,5:13]], c=[[@15,36:36='c',<381>,2:15], [@39,114:114='c',<381>,5:15]], d=[[@41,116:116='d',<381>,5:17]]}, t2={a=[[@26,76:76='a',<381>,3:17]], b=[[@20,70:70='b',<381>,3:11], [@46,150:150='b',<381>,6:11]], c=[[@22,72:72='c',<381>,3:13], [@48,152:152='c',<381>,6:13]], d=[[@24,74:74='d',<381>,3:15], [@50,154:154='d',<381>,6:15]]}}",
			snippet.getTableDictionary().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
			snippet.getSubstitutionsMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query4={b=[[@46,150:150='b',<381>,6:11]], c=[[@48,152:152='c',<381>,6:13]], d=[[@50,154:154='d',<381>,6:15]]}, query6={a=[[@1,7:7='a',<381>,1:7]], b=[[@3,9:9='b',<381>,1:9]], c=[[@5,11:11='c',<381>,1:11]], d=[[@7,13:13='d',<381>,1:13]]}, query0={a=[[@11,32:32='a',<381>,2:11]], b=[[@13,34:34='b',<381>,2:13]], c=[[@15,36:36='c',<381>,2:15]]}, query1={a=[[@26,76:76='a',<381>,3:17]], b=[[@20,70:70='b',<381>,3:11]], c=[[@22,72:72='c',<381>,3:13]], d=[[@24,74:74='d',<381>,3:15]]}, query3={a=[[@35,110:110='a',<381>,5:11]], b=[[@37,112:112='b',<381>,5:13]], c=[[@39,114:114='c',<381>,5:15]], d=[[@41,116:116='d',<381>,5:17]]}}",
			snippet.getQueryColumnDictionaryMap().toString());
	}


	@Test
	public void multipleIntersectSubqueryInterfaceValidationV2Test() {
		final String query = "select a,b,c,d from \n"
				+ "(   select a,b,c from t1"
				+ "    intersect\n"
				+ "    select b,c,d,a from t2"
				+ ") as i1\n"
				+ " union \n"
				+ "(   select a,b,c,d from t1"
				+ "    intersect\n"
				+ "    select b,c,d from t2"
				+ ") as i2\n";
		final Snippet snippet = runFailedSyntaxSQLParserTest(query, SQLPARSER_SQL_TREE_KEY, 4);

		Assert.assertTrue("Expected intersect mismatch diagnostic",
			snippet.getFatalErrorStringList().stream().anyMatch(message -> message.contains("INTERSECTION has different column counts")));
		Assert.assertTrue("Expected participant set-operation mismatch diagnostic",
			snippet.getFatalErrorStringList().stream().anyMatch(message -> message.contains("SET_OPERATION has different column counts")));
		assertFatalDiagnosticCount(snippet, "UNQUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIASES", null, "d", 1);
	}

	// Tests of new "qualify" syntax support with set operations and subqueries
	@Test
	public void subqueryQualifyValidationV1Test() {
		final String query = "SELECT  row_number() over ( partition by person_id order by activity_dt , person_activity_key desc ) rno,\n"
				+ "       person_activity_key,  activity_id,  person_id,  outbound_ind,\n"
				+ "       min(activity_dt) over (partition by person_id order by activity_dt desc, person_activity_key desc) as min_activity_dt\n"
				+ " FROM  personactivity QUALIFY outbound_ind = True and rno = 1 order by person_id";
		final Snippet snippet = runSuccessfulSQLParserTest(query, SQLPARSER_SQL_TREE_KEY);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=rno, window_function={over={partition_by={1={column={name=person_id, table_ref=null}}}, orderby={1={null_order=null, predicand={column={name=activity_dt, table_ref=null}}, sort_order=ASC}, 2={null_order=null, predicand={column={name=person_activity_key, table_ref=null}}, sort_order=desc}}}, function={function_name=row_number, parameters=null}}}, 2={column={name=person_activity_key, table_ref=null}}, 3={column={name=activity_id, table_ref=null}}, 4={column={name=person_id, table_ref=null}}, 5={column={name=outbound_ind, table_ref=null}}, 6={alias=min_activity_dt, window_function={over={partition_by={1={column={name=person_id, table_ref=null}}}, orderby={1={null_order=null, predicand={column={name=activity_dt, table_ref=null}}, sort_order=desc}, 2={null_order=null, predicand={column={name=person_activity_key, table_ref=null}}, sort_order=desc}}}, function={function_name=min, parameters={1={column={name=activity_dt, table_ref=null}}}}}}}, orderby={1={null_order=null, predicand={column={name=person_id, table_ref=null}}, sort_order=ASC}}, from={table={alias=null, table=personactivity}}, qualify={and={1={condition={left={column={name=outbound_ind, table_ref=null}}, right={literal=True}, operator==}}, 2={condition={left={column={name=rno, table_ref=null}}, right={literal=1}, operator==}}}}}}",
			snippet.getSqlAbstractTree().toString());
		Assert.assertEquals("Interface is wrong", "[rno, min_activity_dt, outbound_ind, person_activity_key, activity_id, person_id]",
			snippet.getQueryInterface().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={query_dictionary={rno=[[@16,101:103='rno',<381>,1:101]], min_activity_dt=[[@44,284:298='min_activity_dt',<381>,3:109]], outbound_ind=[[@24,161:172='outbound_ind',<381>,2:55]], person_activity_key=[[@18,113:131='person_activity_key',<381>,2:7]], activity_id=[[@20,135:145='activity_id',<381>,2:29]], person_id=[[@22,149:157='person_id',<381>,2:43]]}, table_dictionary={personactivity={rno=[[@52,354:356='rno',<381>,4:54]], activity_dt=[[@11,60:70='activity_dt',<381>,1:60], [@28,186:196='activity_dt',<381>,3:11], [@37,237:247='activity_dt',<381>,3:62]], outbound_ind=[[@24,161:172='outbound_ind',<381>,2:55], [@48,330:341='outbound_ind',<381>,4:30]], person_activity_key=[[@13,74:92='person_activity_key',<381>,1:74], [@18,113:131='person_activity_key',<381>,2:7], [@40,255:273='person_activity_key',<381>,3:80]], activity_id=[[@20,135:145='activity_id',<381>,2:29]], person_id=[[@8,41:49='person_id',<381>,1:41], [@22,149:157='person_id',<381>,2:43], [@34,218:226='person_id',<381>,3:43], [@57,371:379='person_id',<381>,4:71]]}}, ordered_by=[{name=person_id, table_ref=personactivity}], filters=[{name=outbound_ind, table_ref=personactivity}, {name=rno, table_ref=personactivity}], interface={rno=[{name=person_id, table_ref=personactivity}, {name=activity_dt, table_ref=personactivity}, {name=person_activity_key, table_ref=personactivity}], min_activity_dt=[{name=person_id, table_ref=personactivity}, {name=activity_dt, table_ref=personactivity}, {name=person_activity_key, table_ref=personactivity}], outbound_ind=[{name=outbound_ind, table_ref=personactivity}], person_activity_key=[{name=person_activity_key, table_ref=personactivity}], activity_id=[{name=activity_id, table_ref=personactivity}], person_id=[{name=person_id, table_ref=personactivity}]}}}",
			snippet.getSymbolTable().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{personactivity={rno=[[@52,354:356='rno',<381>,4:54]], activity_dt=[[@11,60:70='activity_dt',<381>,1:60], [@28,186:196='activity_dt',<381>,3:11], [@37,237:247='activity_dt',<381>,3:62]], outbound_ind=[[@24,161:172='outbound_ind',<381>,2:55], [@48,330:341='outbound_ind',<381>,4:30]], person_activity_key=[[@13,74:92='person_activity_key',<381>,1:74], [@18,113:131='person_activity_key',<381>,2:7], [@40,255:273='person_activity_key',<381>,3:80]], activity_id=[[@20,135:145='activity_id',<381>,2:29]], person_id=[[@8,41:49='person_id',<381>,1:41], [@22,149:157='person_id',<381>,2:43], [@34,218:226='person_id',<381>,3:43], [@57,371:379='person_id',<381>,4:71]]}}",
			snippet.getTableDictionary().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
			snippet.getSubstitutionsMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={rno=[[@16,101:103='rno',<381>,1:101]], min_activity_dt=[[@44,284:298='min_activity_dt',<381>,3:109]], outbound_ind=[[@24,161:172='outbound_ind',<381>,2:55]], person_activity_key=[[@18,113:131='person_activity_key',<381>,2:7]], activity_id=[[@20,135:145='activity_id',<381>,2:29]], person_id=[[@22,149:157='person_id',<381>,2:43]]}}",
			snippet.getQueryColumnDictionaryMap().toString());
	}

	// Coverage-driven additions from sql.walker/astwalkers gap analysis.
	@Test
	public void coverageDrivenUpdateSingleFromTableRehomesUnqualifiedUnknownsTest() {
		final String query = "UPDATE t SET a = b FROM t2 WHERE c = 1";
		final Snippet snippet = runSuccessfulSQLParserTest(query, SQLPARSER_SQL_TREE_KEY);

		Assert.assertEquals("AST is wrong", "{SQL={update={from={table={alias=null, table=t2}}, where={condition={left={column={name=c, table_ref=null}}, right={literal=1}, operator==}}, assignments={1={set={column={name=a, table_ref=null}}, to={column={name=b, table_ref=null}}}}, table={alias=null, table=t}}}}",
			snippet.getSqlAbstractTree().toString());
		Assert.assertEquals("Interface is wrong", "[a]",
			snippet.getQueryInterface().toString());
		Assert.assertEquals("Symbol Table is wrong", "{update0={assignments={a=[{name=b, table_ref=null}]}, table_dictionary={t={a=[[@3,13:13='a',<381>,1:13]]}, t2={b=[[@5,17:17='b',<381>,1:17]], c=[[@9,33:33='c',<381>,1:33]]}}, update_dictionary={a=[[@3,13:13='a',<381>,1:13]]}, filters=[{name=c, table_ref=null}]}}",
			snippet.getSymbolTable().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{t={a=[[@3,13:13='a',<381>,1:13]]}, t2={b=[[@5,17:17='b',<381>,1:17]], c=[[@9,33:33='c',<381>,1:33]]}}",
			snippet.getTableDictionary().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
			snippet.getSubstitutionsMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{update0={a=[[@3,13:13='a',<381>,1:13]]}}",
			snippet.getQueryColumnDictionaryMap().toString());

		assertDiagnosticByCode(
			snippet,
			"AMBIGUOUS_COLUMN_REFERENCE",
			ParseDiagnostic.Severity.SEVERE_WARNING,
			"Ambiguous column reference 'c'",
			"c");
	}

	@Test
	public void updateEndpointAccessObjectTest() {
		final String query = "UPDATE t SET a = b FROM t2 WHERE c = 1";
		final Snippet snippet = runSuccessfulSQLParserTest(query, SQLPARSER_UPDATE_TREE_KEY);

		Assert.assertEquals("AST is wrong", "{UPDATE={update={from={table={alias=null, table=t2}}, where={condition={left={column={name=c, table_ref=null}}, right={literal=1}, operator==}}, assignments={1={set={column={name=a, table_ref=null}}, to={column={name=b, table_ref=null}}}}, table={alias=null, table=t}}}}",
			snippet.getSqlAbstractTree().toString());
		Assert.assertEquals("Interface is wrong", "[a]",
			snippet.getQueryInterface().toString());
		Assert.assertEquals("Symbol Table is wrong", "{update0={assignments={a=[{name=b, table_ref=null}]}, table_dictionary={t={a=[[@3,13:13='a',<381>,1:13]]}, t2={b=[[@5,17:17='b',<381>,1:17]], c=[[@9,33:33='c',<381>,1:33]]}}, update_dictionary={a=[[@3,13:13='a',<381>,1:13]]}, filters=[{name=c, table_ref=null}]}}",
			snippet.getSymbolTable().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{t={a=[[@3,13:13='a',<381>,1:13]]}, t2={b=[[@5,17:17='b',<381>,1:17]], c=[[@9,33:33='c',<381>,1:33]]}}",
			snippet.getTableDictionary().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
			snippet.getSubstitutionsMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{update0={a=[[@3,13:13='a',<381>,1:13]]}}",
			snippet.getQueryColumnDictionaryMap().toString());
	}

	@Test
	public void truncateEndpointAccessObjectTest() {
		final String query = "TRUNCATE TABLE tab1";
		final Snippet snippet = runSuccessfulSQLParserTest(query, SQLPARSER_TRUNCATE_TREE_KEY);

		Assert.assertEquals("AST is wrong", "{TRUNCATE={truncate={type=TABLE, name={table=tab1}}}}",
			snippet.getSqlAbstractTree().toString());
		Assert.assertEquals("Interface is wrong", "[]",
			snippet.getQueryInterface().toString());
		Assert.assertEquals("Symbol Table is wrong", "{truncate0={}}",
			snippet.getSymbolTable().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{}",
			snippet.getTableDictionary().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
			snippet.getSubstitutionsMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{}",
			snippet.getQueryColumnDictionaryMap().toString());
	}

	@Test
	public void deleteEndpointAccessObjectWithUsingSubqueryTest() {
		final String query = "DELETE FROM t USING (SELECT id FROM t2) s WHERE t.id = s.id";
		final Snippet sqlSnippet = runSuccessfulSQLParserTest(query, SQLPARSER_SQL_TREE_KEY);
		final Snippet deleteSnippet = runSuccessfulSQLParserTest(query, SQLPARSER_DELETE_TREE_KEY);

		Assert.assertEquals("DELETE endpoint subtree should match SQL endpoint subtree",
				sqlSnippet.getSqlAbstractTree().get(SQLPARSER_SQL_TREE_KEY).toString(),
				deleteSnippet.getSqlAbstractTree().get(SQLPARSER_DELETE_TREE_KEY).toString());
		Assert.assertEquals("Interface is wrong",
				sqlSnippet.getQueryInterface().toString(),
				deleteSnippet.getQueryInterface().toString());
		Assert.assertEquals("Symbol Table is wrong",
				sqlSnippet.getSymbolTable().toString(),
				deleteSnippet.getSymbolTable().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				sqlSnippet.getTableDictionary().toString(),
				deleteSnippet.getTableDictionary().toString());
		Assert.assertEquals("Substitution List is wrong",
				sqlSnippet.getSubstitutionsMap().toString(),
				deleteSnippet.getSubstitutionsMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				sqlSnippet.getQueryColumnDictionaryMap().toString(),
				deleteSnippet.getQueryColumnDictionaryMap().toString());

		Assert.assertTrue("Expected target table t in table dictionary",
				deleteSnippet.getTableDictionary().containsKey("t"));
		Assert.assertTrue("Expected USING subquery source table t2 in table dictionary",
				deleteSnippet.getTableDictionary().containsKey("t2"));
	}

	@Test
	public void coverageDrivenUpdateNoFromDefaultsUnqualifiedUnknownsToTargetTableTest() {
		final String query = "UPDATE t SET a = b WHERE c = 1";
		final Snippet snippet = runSuccessfulSQLParserTest(query, SQLPARSER_SQL_TREE_KEY);

		Assert.assertEquals("AST is wrong",
				"{SQL={update={table={alias=null, table=t}, where={condition={left={column={name=c, table_ref=null}}, right={literal=1}, operator==}}, assignments={1={set={column={name=a, table_ref=null}}, to={column={name=b, table_ref=null}}}}}}}",
				snippet.getSqlAbstractTree().toString());
		Assert.assertEquals("Interface is wrong", "[a]",
				snippet.getQueryInterface().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{update0={assignments={a=[{name=b, table_ref=null}]}, table_dictionary={t={a=[[@3,13:13='a',<381>,1:13]], b=[[@5,17:17='b',<381>,1:17]], c=[[@7,25:25='c',<381>,1:25]]}}, update_dictionary={a=[[@3,13:13='a',<381>,1:13]]}, filters=[{name=c, table_ref=t}]}}",
				snippet.getSymbolTable().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{t={a=[[@3,13:13='a',<381>,1:13]], b=[[@5,17:17='b',<381>,1:17]], c=[[@7,25:25='c',<381>,1:25]]}}",
				snippet.getTableDictionary().toString());

		Assert.assertEquals("Substitution List is wrong", "{}",
				snippet.getSubstitutionsMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{update0={a=[[@3,13:13='a',<381>,1:13]]}}",
				snippet.getQueryColumnDictionaryMap().toString());
		assertFatalDiagnosticCount(snippet, null, null, null, 0);
	}

	@Test
	public void coverageDrivenSelectAliasBackedByQueryMissingColumnFatalTest() {
		final String query = "SELECT q.missing FROM (SELECT a FROM tab1) q";
		final Snippet snippet = runFailedSyntaxSQLParserTest(query, SQLPARSER_SQL_TREE_KEY, 1);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=missing, table_ref=q}}}, from={table={alias=q, query={select={1={column={name=a, table_ref=null}}}, from={table={alias=null, table=tab1}}}}}}}",
			snippet.getSqlAbstractTree().toString());
		Assert.assertEquals("Interface is wrong", "[missing]",
			snippet.getQueryInterface().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query1={query_dictionary={missing=[[@3,9:15='missing',<381>,1:9]]}, table_dictionary={}, def_query0={query_dictionary={a=[[@7,30:30='a',<381>,1:30]]}, table_dictionary={tab1={a=[[@7,30:30='a',<381>,1:30]]}}, interface={a=[{name=a, table_ref=tab1}]}}, interface={missing=[{name=missing, table_ref=q}]}, table_alias={q=query0}}}",
			snippet.getSymbolTable().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={a=[[@7,30:30='a',<381>,1:30]]}}",
			snippet.getTableDictionary().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
			snippet.getSubstitutionsMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={a=[[@7,30:30='a',<381>,1:30]]}, query1={missing=[[@3,9:15='missing',<381>,1:9]]}}",
			snippet.getQueryColumnDictionaryMap().toString());

		assertDiagnosticByCode(
			snippet,
			"QUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIAS",
			ParseDiagnostic.Severity.FATAL,
			"output interface of query alias 'q'",
			"q.missing");
		assertFatalDiagnosticCount(snippet, "QUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIAS", null, null, 1);
	}

	@Test
	public void coverageDrivenSelectAliasBackedByQueryWildcardAllowsQualifiedReferenceTest() {
		final String query = "SELECT q.anycol FROM (SELECT * FROM tab1) q";
		final Snippet snippet = runSuccessfulSQLParserTest(query, SQLPARSER_SQL_TREE_KEY);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=anycol, table_ref=q}}}, from={table={alias=q, query={select={1={column={name=*, table_ref=*}}}, from={table={alias=null, table=tab1}}}}}}}",
			snippet.getSqlAbstractTree().toString());
		Assert.assertEquals("Interface is wrong", "[anycol]",
			snippet.getQueryInterface().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query1={query_dictionary={anycol=[[@3,9:14='anycol',<381>,1:9]]}, table_dictionary={}, def_query0={query_dictionary={anycol=[[@1,7:7='q',<381>,1:7]], *=[[@7,29:29='*',<291>,1:29]]}, table_dictionary={tab1={*=[[@7,29:29='*',<291>,1:29]]}}, interface={*=[{name=*, table_ref=*}]}}, interface={anycol=[{name=anycol, table_ref=q}]}, table_alias={q=query0}}}",
			snippet.getSymbolTable().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={*=[[@7,29:29='*',<291>,1:29]]}}",
			snippet.getTableDictionary().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
			snippet.getSubstitutionsMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={anycol=[[@1,7:7='q',<381>,1:7]], *=[[@7,29:29='*',<291>,1:29]]}, query1={anycol=[[@3,9:14='anycol',<381>,1:9]]}}",
			snippet.getQueryColumnDictionaryMap().toString());
	}

	@Test
	public void coverageDrivenSelectAliasBackedByValuesMissingColumnFatalTest() {
		final String query = "SELECT v.missing FROM (VALUES (1)) v(a)";
		final Snippet snippet = runFailedSyntaxSQLParserTest(query, SQLPARSER_SQL_TREE_KEY, 1);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=missing, table_ref=v}}}, from={values={columns={1={column={name=a, table_ref=null}}}, alias=v, matrix={1={row={1={literal=1}}}}}}}}",
			snippet.getSqlAbstractTree().toString());
		Assert.assertEquals("Interface is wrong", "[missing]",
			snippet.getQueryInterface().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query1={query_dictionary={missing=[[@3,9:15='missing',<381>,1:9]]}, table_dictionary={}, def_values0={query_dictionary={a=[[@13,37:37='a',<381>,1:37]]}, table_dictionary={}, interface={a=[]}}, interface={missing=[{name=missing, table_ref=v}]}, table_alias={v=values0}}}",
			snippet.getSymbolTable().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{}",
			snippet.getTableDictionary().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
			snippet.getSubstitutionsMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{values0={a=[[@13,37:37='a',<381>,1:37]]}, query1={missing=[[@3,9:15='missing',<381>,1:9]]}}",
			snippet.getQueryColumnDictionaryMap().toString());

		assertDiagnosticByCode(
			snippet,
			"QUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIAS",
			ParseDiagnostic.Severity.FATAL,
			"output interface of query alias 'v'",
			"v.missing");
		assertFatalDiagnosticCount(snippet, "QUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIAS", null, null, 1);
	}

	@Test
	public void coverageDrivenSetOperationInterfaceMismatchTopLevelSiblingTest() {
		final String query = "(SELECT a FROM t1 UNION SELECT b FROM t2) INTERSECT (SELECT a,b FROM t3)";
		final Snippet snippet = runFailedSyntaxSQLParserTest(query, SQLPARSER_SQL_TREE_KEY, 1);

		Assert.assertEquals("AST is wrong", "{SQL={intersect={1={union={1={select={1={column={name=a, table_ref=null}}}, from={table={alias=null, table=t1}}}, 2={union={qualifier=null, operator=UNION}}, 3={select={1={column={name=b, table_ref=null}}}, from={table={alias=null, table=t2}}}}}, 2={intersect={qualifier=null, operator=INTERSECT}}, 3={select={1={column={name=a, table_ref=null}}, 2={column={name=b, table_ref=null}}}, from={table={alias=null, table=t3}}}}}}",
			snippet.getSqlAbstractTree().toString());
		Assert.assertEquals("Interface is wrong", "[a]",
			snippet.getQueryInterface().toString());
		Assert.assertEquals("Symbol Table is wrong", "{intersect4={interface={a=[{name=a, table_ref=t1}, {name=b, table_ref=t2}, {name=a, table_ref=t3}]}, union2={query0={query_dictionary={a=[[@2,8:8='a',<381>,1:8]]}, table_dictionary={t1={a=[[@2,8:8='a',<381>,1:8]]}}, interface={a=[{name=a, table_ref=t1}]}}, interface={a=[{name=a, table_ref=t1}, {name=b, table_ref=t2}]}, query1={query_dictionary={b=[[@7,31:31='b',<381>,1:31]]}, table_dictionary={t2={b=[[@7,31:31='b',<381>,1:31]]}}, interface={b=[{name=b, table_ref=t2}]}}}, query3={query_dictionary={a=[[@14,60:60='a',<381>,1:60]], b=[[@16,62:62='b',<381>,1:62]]}, table_dictionary={t3={a=[[@14,60:60='a',<381>,1:60]], b=[[@16,62:62='b',<381>,1:62]]}}, interface={a=[{name=a, table_ref=t3}], b=[{name=b, table_ref=t3}]}}}}",
			snippet.getSymbolTable().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{t1={a=[[@2,8:8='a',<381>,1:8]]}, t2={b=[[@7,31:31='b',<381>,1:31]]}, t3={a=[[@14,60:60='a',<381>,1:60]], b=[[@16,62:62='b',<381>,1:62]]}}",
			snippet.getTableDictionary().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
			snippet.getSubstitutionsMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={a=[[@2,8:8='a',<381>,1:8]]}, query1={b=[[@7,31:31='b',<381>,1:31]]}, query3={a=[[@14,60:60='a',<381>,1:60]], b=[[@16,62:62='b',<381>,1:62]]}}",
			snippet.getQueryColumnDictionaryMap().toString());

		assertDiagnosticByCode(
			snippet,
			"SET_OPERATION_INTERFACE_COLUMN_COUNT_MISMATCH",
			ParseDiagnostic.Severity.FATAL,
			"INTERSECTION has different column counts",
			"query3");
		assertFatalDiagnosticCount(snippet, "SET_OPERATION_INTERFACE_COLUMN_COUNT_MISMATCH", null, null, 1);
	}

	@Test
	public void coverageDrivenSetOperationInterfaceMismatchNestedSubqueryTest() {
		final String query = "SELECT * FROM ((SELECT a FROM t1 UNION SELECT b FROM t2) INTERSECT (SELECT a,b FROM t3)) x";
		final Snippet snippet = runFailedSyntaxSQLParserTest(query, SQLPARSER_SQL_TREE_KEY, 1);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=*, table_ref=*}}}, from={table={alias=x, query={intersect={1={union={1={select={1={column={name=a, table_ref=null}}}, from={table={alias=null, table=t1}}}, 2={union={qualifier=null, operator=UNION}}, 3={select={1={column={name=b, table_ref=null}}}, from={table={alias=null, table=t2}}}}}, 2={intersect={qualifier=null, operator=INTERSECT}}, 3={select={1={column={name=a, table_ref=null}}, 2={column={name=b, table_ref=null}}}, from={table={alias=null, table=t3}}}}}}}}}",
			snippet.getSqlAbstractTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]",
			snippet.getQueryInterface().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query5={query_dictionary={*=[[@1,7:7='*',<291>,1:7]]}, def_intersect4={interface={a=[{name=a, table_ref=t1}, {name=b, table_ref=t2}, {name=a, table_ref=t3}]}, union2={query0={query_dictionary={a=[[@6,23:23='a',<381>,1:23]], *=[[@1,7:7='*',<291>,1:7]]}, table_dictionary={t1={a=[[@6,23:23='a',<381>,1:23]]}}, interface={a=[{name=a, table_ref=t1}]}}, interface={a=[{name=a, table_ref=t1}, {name=b, table_ref=t2}]}, query1={query_dictionary={b=[[@11,46:46='b',<381>,1:46]], *=[[@1,7:7='*',<291>,1:7]]}, table_dictionary={t2={b=[[@11,46:46='b',<381>,1:46]]}}, interface={b=[{name=b, table_ref=t2}]}}}, query3={query_dictionary={a=[[@18,75:75='a',<381>,1:75]], b=[[@20,77:77='b',<381>,1:77]], *=[[@1,7:7='*',<291>,1:7]]}, table_dictionary={t3={a=[[@18,75:75='a',<381>,1:75]], b=[[@20,77:77='b',<381>,1:77]]}}, interface={a=[{name=a, table_ref=t3}], b=[{name=b, table_ref=t3}]}}}, table_dictionary={}, interface={*=[{name=*, table_ref=*}]}, table_alias={x=intersect4}}}",
			snippet.getSymbolTable().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{t1={a=[[@6,23:23='a',<381>,1:23]]}, t2={b=[[@11,46:46='b',<381>,1:46]]}, t3={a=[[@18,75:75='a',<381>,1:75]], b=[[@20,77:77='b',<381>,1:77]]}}",
			snippet.getTableDictionary().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
			snippet.getSubstitutionsMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query5={*=[[@1,7:7='*',<291>,1:7]]}, query0={a=[[@6,23:23='a',<381>,1:23]], *=[[@1,7:7='*',<291>,1:7]]}, query1={b=[[@11,46:46='b',<381>,1:46]], *=[[@1,7:7='*',<291>,1:7]]}, query3={a=[[@18,75:75='a',<381>,1:75]], b=[[@20,77:77='b',<381>,1:77]], *=[[@1,7:7='*',<291>,1:7]]}}",
			snippet.getQueryColumnDictionaryMap().toString());

		assertDiagnosticByCode(
			snippet,
			"SET_OPERATION_INTERFACE_COLUMN_COUNT_MISMATCH",
			ParseDiagnostic.Severity.FATAL,
			"INTERSECTION has different column counts",
			"query3");
		assertFatalDiagnosticCount(snippet, "SET_OPERATION_INTERFACE_COLUMN_COUNT_MISMATCH", null, null, 1);
	}

	@Test
	public void coverageDrivenInsertDerivedSourceColumnSequenceFromUnionNoFallbackTest() {
		final String query = "INSERT INTO tab1(c,d) SELECT x,y FROM (SELECT a AS x, b AS y FROM t2 UNION SELECT c AS x, d AS y FROM t3) s";
		final Snippet snippet = runSuccessfulSQLParserTest(query, SQLPARSER_SQL_TREE_KEY);

		Assert.assertEquals("AST is wrong", "{SQL={preamble=insert_into, from={from={table={alias=s, query={union={1={select={1={column={name=a, table_ref=null}, alias=x}, 2={column={name=b, table_ref=null}, alias=y}}, from={table={alias=null, table=t2}}}, 2={union={qualifier=null, operator=UNION}}, 3={select={1={column={name=c, table_ref=null}, alias=x}, 2={column={name=d, table_ref=null}, alias=y}}, from={table={alias=null, table=t3}}}}}}}, select={1={column={name=x, table_ref=null}}, 2={column={name=y, table_ref=null}}}}, target_table={table={alias=null, table=tab1}}, columns={1={column={name=c, table_ref=null}}, 2={column={name=d, table_ref=null}}}}}",
			snippet.getSqlAbstractTree().toString());
		Assert.assertEquals("Interface is wrong", "[c, d]",
			snippet.getQueryInterface().toString());
		Assert.assertEquals("Symbol Table is wrong", "{insert4={query_dictionary={d=[[@6,19:19='d',<381>,1:19]], c=[[@4,17:17='c',<381>,1:17]]}, table_dictionary={tab1={c=[[@4,17:17='c',<381>,1:17]], d=[[@6,19:19='d',<381>,1:19]]}}, interface={c=[{name=x, table_ref=query3}], d=[{name=y, table_ref=query3}]}, def_query3={def_union2={query0={query_dictionary={x=[[@17,51:51='x',<381>,1:51]], y=[[@21,59:59='y',<381>,1:59]]}, table_dictionary={t2={a=[[@15,46:46='a',<381>,1:46]], b=[[@19,54:54='b',<381>,1:54]]}}, interface={x=[{name=a, table_ref=t2}], y=[{name=b, table_ref=t2}]}}, interface={x=[{name=a, table_ref=t2}, {name=c, table_ref=t3}], y=[{name=b, table_ref=t2}, {name=d, table_ref=t3}]}, query1={query_dictionary={x=[[@28,87:87='x',<381>,1:87]], y=[[@32,95:95='y',<381>,1:95]]}, table_dictionary={t3={c=[[@26,82:82='c',<381>,1:82]], d=[[@30,90:90='d',<381>,1:90]]}}, interface={x=[{name=c, table_ref=t3}], y=[{name=d, table_ref=t3}]}}}, query_dictionary={x=[[@9,29:29='x',<381>,1:29]], y=[[@11,31:31='y',<381>,1:31]]}, table_dictionary={}, interface={x=[{name=x, table_ref=union2}], y=[{name=y, table_ref=union2}]}, table_alias={s=union2}}}}",
			snippet.getSymbolTable().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={c=[[@4,17:17='c',<381>,1:17]], d=[[@6,19:19='d',<381>,1:19]]}, t2={a=[[@15,46:46='a',<381>,1:46]], b=[[@19,54:54='b',<381>,1:54]]}, t3={c=[[@26,82:82='c',<381>,1:82]], d=[[@30,90:90='d',<381>,1:90]]}}",
			snippet.getTableDictionary().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
			snippet.getSubstitutionsMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={x=[[@17,51:51='x',<381>,1:51]], y=[[@21,59:59='y',<381>,1:59]]}, insert4={d=[[@6,19:19='d',<381>,1:19]], c=[[@4,17:17='c',<381>,1:17]]}, query1={x=[[@28,87:87='x',<381>,1:87]], y=[[@32,95:95='y',<381>,1:95]]}, query3={x=[[@9,29:29='x',<381>,1:29]], y=[[@11,31:31='y',<381>,1:31]]}}",
			snippet.getQueryColumnDictionaryMap().toString());
	}

	@Test
	public void coverageDrivenTablePrimarySourceClassificationMatrixTest() {
		final Snippet baseTable = runSuccessfulSQLParserTest(
				"SELECT a FROM tab1",
				SQLPARSER_SQL_TREE_KEY);
		Assert.assertTrue("Base-table routing should keep physical table in table dictionary",
				baseTable.getTableDictionary().toString().contains("tab1={"));
		Assert.assertTrue("Base-table symbol tree should include query0 scope",
				baseTable.getSymbolTable().toString().contains("query0={"));

		final Snippet derivedAlias = runSuccessfulSQLParserTest(
				"SELECT d.a FROM (SELECT a FROM tab1) d",
				SQLPARSER_SQL_TREE_KEY);
		Assert.assertTrue("Derived-subquery source should register alias mapping",
				derivedAlias.getSymbolTable().toString().contains("table_alias={d=query0}"));
		Assert.assertTrue("Derived-subquery source should preserve nested query definition",
				derivedAlias.getSymbolTable().toString().contains("def_query0={"));

		final Snippet valuesAlias = runSuccessfulSQLParserTest(
				"SELECT v.a FROM (VALUES (1)) v(a)",
				SQLPARSER_SQL_TREE_KEY);
		Assert.assertTrue("VALUES alias source should map alias to values scope",
				valuesAlias.getSymbolTable().toString().contains("table_alias={v=values0}"));
		Assert.assertTrue("VALUES alias source should retain values definition",
				valuesAlias.getSymbolTable().toString().contains("def_values0={"));

		final Snippet substitutionTuple = runSuccessfulSQLParserTest(
				"SELECT * FROM {{ target.schema }}",
				SQLPARSER_SQL_TREE_KEY);
		Assert.assertEquals("Substitution tuple should be preserved",
				"{{{ target.schema }}=tuple}",
				substitutionTuple.getSubstitutionsMap().toString());
		Assert.assertTrue("Tuple source should route into table dictionary key",
				substitutionTuple.getTableDictionary().toString().contains("{{ target.schema }}"));
	}

	@Test
	public void coverageDrivenQuerySpecMixedAliasWildcardResolutionTest() {
		final String query = "SELECT q.a, t2.* FROM (SELECT a FROM t1) q JOIN t2 ON q.a = t2.a";
		final Snippet snippet = runSuccessfulSQLParserTest(query, SQLPARSER_SQL_TREE_KEY);

		Assert.assertTrue("Interface should include explicit and wildcard projections",
				snippet.getQueryInterface().toString().contains("a")
					&& snippet.getQueryInterface().toString().contains("*"));
		Assert.assertTrue("Symbol tree should contain query alias routing for q",
				snippet.getSymbolTable().toString().contains("table_alias={q=query0}"));
		Assert.assertTrue("Table dictionary should include joined base table",
				snippet.getTableDictionary().toString().contains("t2={"));
		assertFatalDiagnosticCount(snippet, "QUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIAS", null, null, 0);
	}

	@Test
	public void coverageDrivenJinjaArgumentVariantsTest() {
		final Snippet tupleLikeSource = runSuccessfulSQLParserTest(
				"SELECT * FROM {{ source('PDP_AMS', 'pdp_ams_mail_contacts') }}",
				SQLPARSER_SQL_TREE_KEY);
		Assert.assertTrue("Tuple-like source call should be captured as substitution tuple",
				tupleLikeSource.getSubstitutionsMap().toString().contains("source('PDP_AMS', 'pdp_ams_mail_contacts')"));
		Assert.assertTrue("AST should retain jinja source call",
				tupleLikeSource.getSqlAbstractTree().toString().contains("source"));

		final Snippet variableAccess = runSuccessfulSQLParserTest(
				"SELECT * FROM {{ target.schema }}",
				SQLPARSER_SQL_TREE_KEY);
		Assert.assertEquals("Variable-access substitution should be captured as tuple",
				"{{{ target.schema }}=tuple}",
				variableAccess.getSubstitutionsMap().toString());
		Assert.assertTrue("AST should retain jinja variable access text",
				variableAccess.getSqlAbstractTree().toString().contains("{{ target.schema }}"));

		final Snippet nestedFunctionArgs = runFailedSyntaxSQLParserTest(
				"SELECT * FROM {{ source(env_var('DB', 'PDP_AMS'), var('TABLE_NAME')) }}",
				SQLPARSER_SQL_TREE_KEY,
				1);
		assertFatalDiagnosticByCode(
				nestedFunctionArgs,
				"REPORT_ERROR",
				"unexpected input",
				"(");
	}

	@Test
	public void coverageDrivenInPredicateValueFormsTest() {
		final Snippet literalInList = runSuccessfulSQLParserTest(
				"SELECT a FROM t1 WHERE a IN (1, 2, 3)",
				SQLPARSER_SQL_TREE_KEY);
		Assert.assertTrue("Literal IN-list AST shape should include IN clause with literal values",
				literalInList.getSqlAbstractTree().toString().contains("in={")
					&& literalInList.getSqlAbstractTree().toString().contains("literal=1")
					&& literalInList.getSqlAbstractTree().toString().contains("literal=2")
					&& literalInList.getSqlAbstractTree().toString().contains("literal=3"));

		final Snippet tupleInList = runFailedSyntaxSQLParserTest(
				"SELECT a FROM t1 WHERE (a, a) IN ((1, 2), (3, 4))",
				SQLPARSER_SQL_TREE_KEY,
				2);
		assertFatalDiagnosticByCode(
				tupleInList,
				"REPORT_ERROR",
				"unexpected input",
				",");

		final String invalidQualifiedQuery = "SELECT q.a FROM (SELECT a FROM t1) q WHERE q.missing IN (SELECT a FROM t2)";
		final Snippet invalidQualified = runFailedSyntaxSQLParserTest(invalidQualifiedQuery, SQLPARSER_SQL_TREE_KEY, 1);
		assertDiagnosticByCode(
				invalidQualified,
				"QUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIAS",
				ParseDiagnostic.Severity.FATAL,
				"output interface of query alias 'q'",
				"q.missing");
	}

	@Test
	public void coverageDrivenWithClauseAliasChainingTest() {
		final String chainedCtes = "WITH c1 AS (SELECT a FROM t1), c2 AS (SELECT c1.a FROM c1) SELECT c2.a FROM c2";
		final Snippet validChain = runSuccessfulSQLParserTest(chainedCtes, SQLPARSER_SQL_TREE_KEY);
		Assert.assertEquals("CTE chain should expose projected column in interface",
				"[a]",
				validChain.getQueryInterface().toString());
		Assert.assertTrue("CTE chain should preserve alias mappings",
				validChain.getSymbolTable().toString().contains("table_alias={")
					&& validChain.getSymbolTable().toString().contains("c2="));

		final String missingQualified = "WITH c1 AS (SELECT a FROM t1), c2 AS (SELECT a FROM c1) SELECT q.missing FROM (SELECT c2.a FROM c2) q";
		final Snippet invalidChain = runFailedSyntaxSQLParserTest(missingQualified, SQLPARSER_SQL_TREE_KEY, 1);
		Assert.assertTrue("Expected at least one fatal qualified-column diagnostic for invalid CTE chain",
				countFatalDiagnostics(
						invalidChain,
						"QUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIAS",
						"output interface of query alias 'q'",
						null) >= 1);
	}

	/*
	===============================================================================
	  QUERY SPECIFICATION AND SELECT INTO COVERAGE REGRESSION TESTS
	===============================================================================
	*/

	@Test
	public void coverageDrivenQuerySpecificationAliasUnresolvedSnapshotTest() {
		final String query = "SELECT q.missing FROM (SELECT a FROM t1) q";
		final Snippet snippet = runFailedSyntaxSQLParserTest(query, SQLPARSER_SQL_TREE_KEY, 1);

		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=missing, table_ref=q}}}, from={table={alias=q, query={select={1={column={name=a, table_ref=null}}}, from={table={alias=null, table=t1}}}}}}}",
				snippet.getSqlAbstractTree().toString());
		Assert.assertEquals("Interface is wrong",
				"[missing]",
				snippet.getQueryInterface().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{query1={query_dictionary={missing=[[@3,9:15='missing',<381>,1:9]]}, table_dictionary={}, def_query0={query_dictionary={a=[[@7,30:30='a',<381>,1:30]]}, table_dictionary={t1={a=[[@7,30:30='a',<381>,1:30]]}}, interface={a=[{name=a, table_ref=t1}]}}, interface={missing=[{name=missing, table_ref=q}]}, table_alias={q=query0}}}",
				snippet.getSymbolTable().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{t1={a=[[@7,30:30='a',<381>,1:30]]}}",
				snippet.getTableDictionary().toString());
		Assert.assertEquals("Substitution List is wrong",
				"{}",
				snippet.getSubstitutionsMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{query0={a=[[@7,30:30='a',<381>,1:30]]}, query1={missing=[[@3,9:15='missing',<381>,1:9]]}}",
				snippet.getQueryColumnDictionaryMap().toString());

		assertDiagnosticByCode(
				snippet,
				"QUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIAS",
				ParseDiagnostic.Severity.FATAL,
				"output interface of query alias 'q'",
				"q.missing");
		assertFatalDiagnosticCount(snippet, "QUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIAS", null, null, 1);
	}

	@Test
	public void coverageDrivenQuerySpecificationCorrelatedPassUpSnapshotTest() {
		final String query = "SELECT * FROM t1 WHERE EXISTS (SELECT 1 FROM t2 WHERE t2.a = t1.a)";
		final Snippet snippet = runSuccessfulSQLParserTest(query, SQLPARSER_SQL_TREE_KEY);

		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=*, table_ref=*}}}, from={table={alias=null, table=t1}}, where={exists={select={1={literal=1}}, from={table={alias=null, table=t2}}, where={condition={left={column={name=a, table_ref=t2}}, right={column={name=a, table_ref=t1}}, operator==}}, operator=EXISTS}}}}",
				snippet.getSqlAbstractTree().toString());
		Assert.assertEquals("Interface is wrong",
				"[*]",
				snippet.getQueryInterface().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{query2={query_dictionary={*=[[@1,7:7='*',<291>,1:7]]}, table_dictionary={t1={a=[[@16,61:62='t1',<381>,1:61]], *=[[@1,7:7='*',<291>,1:7]]}}, def_query0={query_dictionary={*=[[@1,7:7='*',<291>,1:7]], unnamed_0=[[@8,38:38='1',<300>,1:38]]}, table_dictionary={t2={a=[[@12,54:55='t2',<381>,1:54]]}}, filters=[{name=a, table_ref=t2}, {name=a, table_ref=t1}], interface={unnamed_0=[]}}, filters=[], interface={*=[{name=*, table_ref=*}]}, exists1=query0}}",
				snippet.getSymbolTable().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{t1={a=[[@16,61:62='t1',<381>,1:61]], *=[[@1,7:7='*',<291>,1:7]]}, t2={a=[[@12,54:55='t2',<381>,1:54]]}}",
				snippet.getTableDictionary().toString());
		Assert.assertEquals("Substitution List is wrong",
				"{}",
				snippet.getSubstitutionsMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{query0={*=[[@1,7:7='*',<291>,1:7]], unnamed_0=[[@8,38:38='1',<300>,1:38]]}, query2={*=[[@1,7:7='*',<291>,1:7]]}}",
				snippet.getQueryColumnDictionaryMap().toString());

		assertFatalDiagnosticCount(snippet, null, null, null, 0);
	}

	@Test
	public void coverageDrivenQuerySpecificationTopLevelSourceNotFoundSnapshotTest() {
		final String query = "SELECT z.a FROM t1";
		final Snippet snippet = runFailedSyntaxSQLParserTest(query, SQLPARSER_SQL_TREE_KEY, 1);

		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=a, table_ref=z}}}, from={table={alias=null, table=t1}}}}",
				snippet.getSqlAbstractTree().toString());
		Assert.assertEquals("Interface is wrong",
				"[a]",
				snippet.getQueryInterface().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{query0={query_dictionary={a=[[@3,9:9='a',<381>,1:9]]}, table_dictionary={z={a=[[@1,7:7='z',<381>,1:7]]}, t1={}}, interface={a=[{name=a, table_ref=z}]}}}",
				snippet.getSymbolTable().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{z={a=[[@1,7:7='z',<381>,1:7]]}, t1={}}",
				snippet.getTableDictionary().toString());
		Assert.assertEquals("Substitution List is wrong",
				"{}",
				snippet.getSubstitutionsMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{query0={a=[[@3,9:9='a',<381>,1:9]]}}",
				snippet.getQueryColumnDictionaryMap().toString());

		assertDiagnosticByCode(
				snippet,
				"QUALIFIED_COLUMN_NOT_FOUND_IN_TABLE",
				ParseDiagnostic.Severity.FATAL,
				"No alias or table called 'z'",
				"a");
		assertFatalDiagnosticCount(snippet, "QUALIFIED_COLUMN_NOT_FOUND_IN_TABLE", null, null, 1);
	}

	@Test
	public void coverageDrivenQuerySpecificationEmitFromSubquerySnapshotTest() {
		final String query = "SELECT * FROM (SELECT bad_alias.a FROM t2) q";
		final Snippet snippet = runFailedSyntaxSQLParserTest(query, SQLPARSER_SQL_TREE_KEY, 1);

		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=*, table_ref=*}}}, from={table={alias=q, query={select={1={column={name=a, table_ref=bad_alias}}}, from={table={alias=null, table=t2}}}}}}}",
				snippet.getSqlAbstractTree().toString());
		Assert.assertEquals("Interface is wrong",
				"[*]",
				snippet.getQueryInterface().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{query1={query_dictionary={*=[[@1,7:7='*',<291>,1:7]]}, table_dictionary={}, def_query0={query_dictionary={a=[[@7,32:32='a',<381>,1:32]], *=[[@1,7:7='*',<291>,1:7]]}, table_dictionary={t2={}, bad_alias={a=[[@5,22:30='bad_alias',<381>,1:22]]}}, interface={a=[{name=a, table_ref=bad_alias}]}}, interface={*=[{name=*, table_ref=*}]}, table_alias={q=query0}}}",
				snippet.getSymbolTable().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{t2={}, bad_alias={a=[[@5,22:30='bad_alias',<381>,1:22]]}}",
				snippet.getTableDictionary().toString());
		Assert.assertEquals("Substitution List is wrong",
				"{}",
				snippet.getSubstitutionsMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{query0={a=[[@7,32:32='a',<381>,1:32]], *=[[@1,7:7='*',<291>,1:7]]}, query1={*=[[@1,7:7='*',<291>,1:7]]}}",
				snippet.getQueryColumnDictionaryMap().toString());

		assertDiagnosticByCode(
				snippet,
				"QUALIFIED_COLUMN_NOT_FOUND_IN_TABLE",
				ParseDiagnostic.Severity.FATAL,
				"No alias or table called 'bad_alias'",
				"a");
		assertFatalDiagnosticCount(snippet, "QUALIFIED_COLUMN_NOT_FOUND_IN_TABLE", null, null, 1);
	}

	@Test
	public void coverageDrivenSelectIntoSimpleTargetProjectionSnapshotTest() {
		final String query = "SELECT INTO outtab a FROM t1";
		final Snippet snippet = runSuccessfulSQLParserTest(query, SQLPARSER_SQL_TREE_KEY);

		Assert.assertEquals("AST is wrong",
				"{SQL={into={1={table=outtab}}, select={1={column={name=a, table_ref=null}}}, from={table={alias=null, table=t1}}}}",
				snippet.getSqlAbstractTree().toString());
		Assert.assertEquals("Interface is wrong",
				"[a]",
				snippet.getQueryInterface().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{query0={query_dictionary={a=[[@3,19:19='a',<381>,1:19]]}, table_dictionary={outtab={a=[[@3,19:19='a',<381>,1:19]]}, t1={a=[[@3,19:19='a',<381>,1:19]]}}, target_table={outtab={a=[[@3,19:19='a',<381>,1:19]]}}, interface={a=[{name=a, table_ref=t1}]}}}",
				snippet.getSymbolTable().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{outtab={a=[[@3,19:19='a',<381>,1:19]]}, t1={a=[[@3,19:19='a',<381>,1:19]]}}",
				snippet.getTableDictionary().toString());
		Assert.assertEquals("Substitution List is wrong",
				"{}",
				snippet.getSubstitutionsMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{query0={a=[[@3,19:19='a',<381>,1:19]]}}",
				snippet.getQueryColumnDictionaryMap().toString());

		assertFatalDiagnosticCount(snippet, null, null, null, 0);
	}

	@Test
	public void coverageDrivenSelectIntoQualifiedAndAliasedProjectionSnapshotTest() {
		final String query = "SELECT INTO db1.sc1.outtab a, b AS b1 FROM t1";
		final Snippet snippet = runSuccessfulSQLParserTest(query, SQLPARSER_SQL_TREE_KEY);

		Assert.assertEquals("AST is wrong",
				"{SQL={into={1={schema=sc1, dbname=db1, table=outtab}}, select={1={column={name=a, table_ref=null}}, 2={column={name=b, table_ref=null}, alias=b1}}, from={table={alias=null, table=t1}}}}",
				snippet.getSqlAbstractTree().toString());
		Assert.assertEquals("Interface is wrong",
				"[a, b1]",
				snippet.getQueryInterface().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{query0={query_dictionary={a=[[@7,27:27='a',<381>,1:27]], b1=[[@11,35:36='b1',<381>,1:35]]}, table_dictionary={t1={a=[[@7,27:27='a',<381>,1:27]], b=[[@9,30:30='b',<381>,1:30]]}, db1.sc1.outtab={a=[[@7,27:27='a',<381>,1:27]], b1=[[@11,35:36='b1',<381>,1:35]]}}, target_table={db1.sc1.outtab={a=[[@7,27:27='a',<381>,1:27]], b1=[[@11,35:36='b1',<381>,1:35]]}}, interface={a=[{name=a, table_ref=t1}], b1=[{name=b, table_ref=t1}]}}}",
				snippet.getSymbolTable().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{t1={a=[[@7,27:27='a',<381>,1:27]], b=[[@9,30:30='b',<381>,1:30]]}, db1.sc1.outtab={a=[[@7,27:27='a',<381>,1:27]], b1=[[@11,35:36='b1',<381>,1:35]]}}",
				snippet.getTableDictionary().toString());
		Assert.assertEquals("Substitution List is wrong",
				"{}",
				snippet.getSubstitutionsMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{query0={a=[[@7,27:27='a',<381>,1:27]], b1=[[@11,35:36='b1',<381>,1:35]]}}",
				snippet.getQueryColumnDictionaryMap().toString());

		assertFatalDiagnosticCount(snippet, null, null, null, 0);
	}

	@Test
	public void coverageDrivenSelectIntoWildcardProjectionSnapshotTest() {
		final String query = "SELECT INTO outtab * FROM t1";
		final Snippet snippet = runSuccessfulSQLParserTest(query, SQLPARSER_SQL_TREE_KEY);

		Assert.assertEquals("AST is wrong",
				"{SQL={into={1={table=outtab}}, select={1={column={name=*, table_ref=*}}}, from={table={alias=null, table=t1}}}}",
				snippet.getSqlAbstractTree().toString());
		Assert.assertEquals("Interface is wrong",
				"[*]",
				snippet.getQueryInterface().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{query0={query_dictionary={*=[[@3,19:19='*',<291>,1:19]]}, table_dictionary={outtab={*=[[@3,19:19='*',<291>,1:19]]}, t1={*=[[@3,19:19='*',<291>,1:19]]}}, target_table={outtab={*=[[@3,19:19='*',<291>,1:19]]}}, interface={*=[{name=*, table_ref=*}]}}}",
				snippet.getSymbolTable().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{outtab={*=[[@3,19:19='*',<291>,1:19]]}, t1={*=[[@3,19:19='*',<291>,1:19]]}}",
				snippet.getTableDictionary().toString());
		Assert.assertEquals("Substitution List is wrong",
				"{}",
				snippet.getSubstitutionsMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{query0={*=[[@3,19:19='*',<291>,1:19]]}}",
				snippet.getQueryColumnDictionaryMap().toString());

		assertFatalDiagnosticCount(snippet, null, null, null, 0);
	}

	@Test
	public void coverageDrivenSelectIntoUnionBothSidesSnapshotTest() {
		final String query = "SELECT INTO out1 a FROM t1 UNION SELECT INTO out2 b FROM t2";
		final Snippet snippet = runFailedSyntaxSQLParserTest(query, SQLPARSER_SQL_TREE_KEY, 1);

		Assert.assertEquals("AST is wrong",
				"{SQL={union={1={into={1={table=out1}}, select={1={column={name=a, table_ref=null}}}, from={table={alias=null, table=t1}}}, 2={union={qualifier=null, operator=UNION}}, 3={into={1={table=out2}}, select={1={column={name=b, table_ref=null}}}, from={table={alias=null, table=t2}}}}}}",
				snippet.getSqlAbstractTree().toString());
		assertDiagnosticByCode(
				snippet,
				"INTO_ONLY_ALLOWED_ON_FIRST_SET_MEMBER",
				ParseDiagnostic.Severity.FATAL,
				"UNION member 2 contains INTO",
				"INTO");
		assertFatalDiagnosticCount(snippet, "INTO_ONLY_ALLOWED_ON_FIRST_SET_MEMBER", null, null, 1);
		Assert.assertTrue("First INTO target projection should be preserved",
				snippet.getSymbolTable().toString().contains("target_table={out1="));
		Assert.assertFalse("Second INTO target projection should be skipped after fatal placement diagnostic",
				snippet.getSymbolTable().toString().contains("target_table={out2="));
	}

	@Test
	public void coverageDrivenSelectIntoUnionMixedSidesSnapshotTest() {
		final String query = "SELECT INTO out1 a FROM t1 UNION SELECT b FROM t2";
		final Snippet snippet = runSuccessfulSQLParserTest(query, SQLPARSER_SQL_TREE_KEY);

		Assert.assertEquals("AST is wrong",
				"{SQL={union={1={into={1={table=out1}}, select={1={column={name=a, table_ref=null}}}, from={table={alias=null, table=t1}}}, 2={union={qualifier=null, operator=UNION}}, 3={select={1={column={name=b, table_ref=null}}}, from={table={alias=null, table=t2}}}}}}",
				snippet.getSqlAbstractTree().toString());
		Assert.assertEquals("Interface is wrong",
				"[a]",
				snippet.getQueryInterface().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{union2={query0={query_dictionary={a=[[@3,17:17='a',<381>,1:17]]}, table_dictionary={out1={a=[[@3,17:17='a',<381>,1:17]]}, t1={a=[[@3,17:17='a',<381>,1:17]]}}, target_table={out1={a=[[@3,17:17='a',<381>,1:17]]}}, interface={a=[{name=a, table_ref=t1}]}}, interface={a=[{name=a, table_ref=t1}, {name=b, table_ref=t2}]}, query1={query_dictionary={b=[[@8,40:40='b',<381>,1:40]]}, table_dictionary={t2={b=[[@8,40:40='b',<381>,1:40]]}}, interface={b=[{name=b, table_ref=t2}]}}}}",
				snippet.getSymbolTable().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{out1={a=[[@3,17:17='a',<381>,1:17]]}, t1={a=[[@3,17:17='a',<381>,1:17]]}, t2={b=[[@8,40:40='b',<381>,1:40]]}}",
				snippet.getTableDictionary().toString());
		Assert.assertEquals("Substitution List is wrong",
				"{}",
				snippet.getSubstitutionsMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{query0={a=[[@3,17:17='a',<381>,1:17]]}, query1={b=[[@8,40:40='b',<381>,1:40]]}}",
				snippet.getQueryColumnDictionaryMap().toString());

		assertFatalDiagnosticCount(snippet, null, null, null, 0);
	}

	@Test
	public void coverageDrivenSelectIntoUnionNestedSubquerySnapshotTest() {
		final String query = "SELECT * FROM (SELECT INTO out1 a FROM t1 UNION SELECT INTO out2 b FROM t2) u";
		final Snippet snippet = runFailedSyntaxSQLParserTest(query, SQLPARSER_SQL_TREE_KEY, 1);

		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=*, table_ref=*}}}, from={table={alias=u, query={union={1={into={1={table=out1}}, select={1={column={name=a, table_ref=null}}}, from={table={alias=null, table=t1}}}, 2={union={qualifier=null, operator=UNION}}, 3={into={1={table=out2}}, select={1={column={name=b, table_ref=null}}}, from={table={alias=null, table=t2}}}}}}}}}",
				snippet.getSqlAbstractTree().toString());
		assertDiagnosticByCode(
				snippet,
				"INTO_ONLY_ALLOWED_ON_FIRST_SET_MEMBER",
				ParseDiagnostic.Severity.FATAL,
				"UNION member 2 contains INTO",
				"INTO");
		assertFatalDiagnosticCount(snippet, "INTO_ONLY_ALLOWED_ON_FIRST_SET_MEMBER", null, null, 1);
		Assert.assertTrue("Nested first INTO target projection should be preserved",
				snippet.getSymbolTable().toString().contains("target_table={out1="));
		Assert.assertFalse("Nested second INTO target projection should be skipped",
				snippet.getSymbolTable().toString().contains("target_table={out2="));
	}

	@Test
	public void coverageDrivenSelectIntoIntersectSecondMemberFatalTest() {
		final String query = "SELECT INTO out1 a FROM t1 INTERSECT SELECT INTO out2 a FROM t2";
		final Snippet snippet = runFailedSyntaxSQLParserTest(query, SQLPARSER_SQL_TREE_KEY, 1);

		assertDiagnosticByCode(
				snippet,
				"INTO_ONLY_ALLOWED_ON_FIRST_SET_MEMBER",
				ParseDiagnostic.Severity.FATAL,
				"INTERSECTION member 2 contains INTO",
				"INTO");
		assertFatalDiagnosticCount(snippet, "INTO_ONLY_ALLOWED_ON_FIRST_SET_MEMBER", null, null, 1);
		Assert.assertTrue("First INTO target projection should be preserved for INTERSECT",
				snippet.getSymbolTable().toString().contains("target_table={out1="));
		Assert.assertFalse("Second INTO target projection should be skipped for INTERSECT",
				snippet.getSymbolTable().toString().contains("target_table={out2="));
	}

	@Test
	public void coverageDrivenSelectIntoIntersectFirstMemberAllowedTest() {
		final String query = "SELECT INTO out1 a FROM t1 INTERSECT SELECT a FROM t2";
		final Snippet snippet = runSuccessfulSQLParserTest(query, SQLPARSER_SQL_TREE_KEY);

		Assert.assertTrue("AST should preserve INTO in the first set member",
				snippet.getSqlAbstractTree().toString().contains("into={1={table=out1}}"));
		Assert.assertTrue("First-member INTO should still project target table columns",
				snippet.getSymbolTable().toString().contains("target_table={out1="));
		assertFatalDiagnosticCount(snippet, "INTO_ONLY_ALLOWED_ON_FIRST_SET_MEMBER", null, null, 0);
	}

	@Test
	public void coverageDrivenSelectAliasBackedByQueryMixedValidAndMissingColumnsTest() {
		final String query = "SELECT q.a, q.missing FROM (SELECT a FROM tab1) q";
		final Snippet snippet = runFailedSyntaxSQLParserTest(query, SQLPARSER_SQL_TREE_KEY, 1);

		Assert.assertTrue("AST should preserve both qualified references",
				snippet.getSqlAbstractTree().toString().contains("name=a, table_ref=q")
					&& snippet.getSqlAbstractTree().toString().contains("name=missing, table_ref=q"));
		Assert.assertTrue("Symbol table should retain query alias routing",
				snippet.getSymbolTable().toString().contains("table_alias={q=query0}"));
		assertDiagnosticByCode(
				snippet,
				"QUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIAS",
				ParseDiagnostic.Severity.FATAL,
				"output interface of query alias 'q'",
				"q.missing");
		assertFatalDiagnosticCount(snippet, "QUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIAS", null, null, 1);
	}

	@Test
	public void coverageDrivenSelectAliasBackedByValuesMixedValidAndMissingColumnsTest() {
		final String query = "SELECT v.a, v.missing FROM (VALUES (1)) v(a)";
		final Snippet snippet = runFailedSyntaxSQLParserTest(query, SQLPARSER_SQL_TREE_KEY, 1);

		Assert.assertTrue("AST should preserve both VALUES-alias references",
				snippet.getSqlAbstractTree().toString().contains("name=a, table_ref=v")
					&& snippet.getSqlAbstractTree().toString().contains("name=missing, table_ref=v"));
		Assert.assertTrue("Symbol table should retain values alias routing",
				snippet.getSymbolTable().toString().contains("table_alias={v=values0}"));
		assertDiagnosticByCode(
				snippet,
				"QUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIAS",
				ParseDiagnostic.Severity.FATAL,
				"output interface of query alias 'v'",
				"v.missing");
		assertFatalDiagnosticCount(snippet, "QUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIAS", null, null, 1);
	}

	@Test
	public void coverageDrivenUpdateMultiSourceAmbiguousUnknownsTest() {
		final String query = "UPDATE t SET a = b FROM t2, t3 WHERE c = 1";
		final Snippet snippet = runSuccessfulSQLParserTest(query, SQLPARSER_SQL_TREE_KEY);

		Assert.assertTrue("Table dictionary should include both update input tables",
				snippet.getTableDictionary().toString().contains("t2={")
					&& snippet.getTableDictionary().toString().contains("t3={"));
		Assert.assertEquals("Query Column Dictionary is wrong", "{update0={a=[[@3,13:13='a',<381>,1:13]]}}",
				snippet.getQueryColumnDictionaryMap().toString());
		Assert.assertTrue("Expected ambiguous unqualified reference warning for c",
				countDiagnosticsBySeverity(
						snippet,
						"AMBIGUOUS_COLUMN_REFERENCE",
						ParseDiagnostic.Severity.SEVERE_WARNING,
						"Ambiguous column reference 'c'",
						"c") >= 1);
	}

	@Test
	public void coverageDrivenJoinExtensionQualifiedMissingColumnFatalTest() {
		final String query = "SELECT a.id FROM t1 a JOIN t2 b ON a.id = b.id AND a.missing = b.id";
		final Snippet snippet = runSuccessfulSQLParserTest(query, SQLPARSER_SQL_TREE_KEY);

		Assert.assertNotNull("SQL AST should be available for join-extension traversal", snippet.getSqlAbstractTree());
		Assert.assertNotNull("Diagnostics list should be present", snippet.getParserDiagnosticList());
		assertFatalDiagnosticCount(snippet, null, null, null, 0);
	}

	@Test
	public void coverageDrivenUnionAliasMixedValidAndMissingColumnsTest() {
		final String query = "SELECT u.a, u.missing FROM (SELECT a FROM t1 UNION SELECT a FROM t2) u";
		final Snippet snippet = runFailedSyntaxSQLParserTest(query, SQLPARSER_SQL_TREE_KEY, 1);

		Assert.assertTrue("Union alias should be registered in table alias map",
				snippet.getSymbolTable().toString().contains("table_alias={u=union2}"));
		assertDiagnosticByCode(
				snippet,
				"QUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIAS",
				ParseDiagnostic.Severity.FATAL,
				"output interface of query alias 'u'",
				"u.missing");
		assertFatalDiagnosticCount(snippet, "QUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIAS", null, null, 1);
	}

	@Test
	public void coverageDrivenUpdateAliasQualifiedTargetAndSourceRefsTest() {
		final String query = "UPDATE t AS tgt SET a = src.b FROM s AS src WHERE tgt.id = src.id AND tgt.flag = 1";
		final Snippet snippet = runSuccessfulSQLParserTest(query, SQLPARSER_SQL_TREE_KEY);

		Assert.assertEquals("AST is wrong",
				"{SQL={update={from={table={alias=src, table=s}}, where={and={1={condition={left={column={name=id, table_ref=tgt}}, right={column={name=id, table_ref=src}}, operator==}}, 2={condition={left={column={name=flag, table_ref=tgt}}, right={literal=1}, operator==}}}}, assignments={1={set={column={name=a, table_ref=null}}, to={column={name=b, table_ref=src}}}}, table={alias=tgt, table=t}}}}",
				snippet.getSqlAbstractTree().toString());
		Assert.assertEquals("Interface is wrong", "[a]",
				snippet.getQueryInterface().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{update0={assignments={a=[{name=b, table_ref=src}]}, table_dictionary={s={b=[[@7,24:26='src',<381>,1:24]], id=[[@19,59:61='src',<381>,1:59]]}, t={a=[[@5,20:20='a',<381>,1:20]], flag=[[@23,70:72='tgt',<381>,1:70]], id=[[@15,50:52='tgt',<381>,1:50]]}}, update_dictionary={a=[[@5,20:20='a',<381>,1:20]]}, filters=[{name=id, table_ref=tgt}, {name=id, table_ref=src}, {name=flag, table_ref=tgt}], table_alias={tgt=t, src=s}}}",
				snippet.getSymbolTable().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{s={b=[[@7,24:26='src',<381>,1:24]], id=[[@19,59:61='src',<381>,1:59]]}, t={a=[[@5,20:20='a',<381>,1:20]], flag=[[@23,70:72='tgt',<381>,1:70]], id=[[@15,50:52='tgt',<381>,1:50]]}}",
				snippet.getTableDictionary().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				snippet.getSubstitutionsMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{update0={a=[[@5,20:20='a',<381>,1:20]]}}",
				snippet.getQueryColumnDictionaryMap().toString());
		assertFatalDiagnosticCount(snippet, null, null, null, 0);
	}

	@Test
	public void coverageDrivenQualifiedSourceNotFoundPluralMissingColumnsTest() {
		final String query = "SELECT z.missing1, z.missing2 FROM t1";
		final Snippet snippet = runFailedSyntaxSQLParserTest(query, SQLPARSER_SQL_TREE_KEY, 2);

		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=missing1, table_ref=z}}, 2={column={name=missing2, table_ref=z}}}, from={table={alias=null, table=t1}}}}",
				snippet.getSqlAbstractTree().toString());
		Assert.assertEquals("Interface is wrong", "[missing1, missing2]",
				snippet.getQueryInterface().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{query0={query_dictionary={missing1=[[@3,9:16='missing1',<381>,1:9]], missing2=[[@7,21:28='missing2',<381>,1:21]]}, table_dictionary={z={missing1=[[@1,7:7='z',<381>,1:7]], missing2=[[@5,19:19='z',<381>,1:19]]}, t1={}}, interface={missing1=[{name=missing1, table_ref=z}], missing2=[{name=missing2, table_ref=z}]}}}",
				snippet.getSymbolTable().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{z={missing1=[[@1,7:7='z',<381>,1:7]], missing2=[[@5,19:19='z',<381>,1:19]]}, t1={}}",
				snippet.getTableDictionary().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				snippet.getSubstitutionsMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{query0={missing1=[[@3,9:16='missing1',<381>,1:9]], missing2=[[@7,21:28='missing2',<381>,1:21]]}}",
				snippet.getQueryColumnDictionaryMap().toString());
		assertDiagnosticByCode(snippet, "QUALIFIED_COLUMN_NOT_FOUND_IN_TABLE", ParseDiagnostic.Severity.FATAL,
				"No alias or table called 'z'", "missing1");
		Assert.assertTrue("Expected two z-qualifier fatals",
				countFatalDiagnostics(
						snippet,
						"QUALIFIED_COLUMN_NOT_FOUND_IN_TABLE",
						"No alias or table called 'z'",
						null) >= 2);
		assertFatalDiagnosticCount(snippet, "QUALIFIED_COLUMN_NOT_FOUND_IN_TABLE", null, null, 2);
	}

	@Test
	public void coverageDrivenQueryAliasCaseInsensitiveResolutionTest() {
		final String query = "SELECT q.A FROM (SELECT a FROM t1) q";
		final Snippet snippet = runSuccessfulSQLParserTest(query, SQLPARSER_SQL_TREE_KEY);

		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=A, table_ref=q}}}, from={table={alias=q, query={select={1={column={name=a, table_ref=null}}}, from={table={alias=null, table=t1}}}}}}}",
				snippet.getSqlAbstractTree().toString());
		Assert.assertEquals("Interface is wrong", "[A]",
				snippet.getQueryInterface().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{query1={query_dictionary={A=[[@3,9:9='A',<381>,1:9]]}, table_dictionary={}, def_query0={query_dictionary={a=[[@7,24:24='a',<381>,1:24], [@1,7:7='q',<381>,1:7]]}, table_dictionary={t1={a=[[@7,24:24='a',<381>,1:24]]}}, interface={a=[{name=a, table_ref=t1}]}}, interface={A=[{name=A, table_ref=q}]}, table_alias={q=query0}}}",
				snippet.getSymbolTable().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{t1={a=[[@7,24:24='a',<381>,1:24]]}}",
				snippet.getTableDictionary().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				snippet.getSubstitutionsMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{query0={a=[[@7,24:24='a',<381>,1:24], [@1,7:7='q',<381>,1:7]]}, query1={A=[[@3,9:9='A',<381>,1:9]]}}",
				snippet.getQueryColumnDictionaryMap().toString());
		assertFatalDiagnosticCount(snippet, null, null, null, 0);
	}

	@Test
	public void coverageDrivenWildcardQueryAliasPermissiveMultipleRefsTest() {
		final String query = "SELECT q.any1, q.any2 FROM (SELECT * FROM t1) q WHERE q.any3 = 1";
		final Snippet snippet = runSuccessfulSQLParserTest(query, SQLPARSER_SQL_TREE_KEY);

		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=any1, table_ref=q}}, 2={column={name=any2, table_ref=q}}}, from={table={alias=q, query={select={1={column={name=*, table_ref=*}}}, from={table={alias=null, table=t1}}}}}, where={condition={left={column={name=any3, table_ref=q}}, right={literal=1}, operator==}}}}",
				snippet.getSqlAbstractTree().toString());
		Assert.assertEquals("Interface is wrong", "[any1, any2]",
				snippet.getQueryInterface().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{query1={query_dictionary={any1=[[@3,9:12='any1',<381>,1:9]], any2=[[@7,17:20='any2',<381>,1:17]]}, table_dictionary={}, def_query0={query_dictionary={any1=[[@1,7:7='q',<381>,1:7]], *=[[@11,35:35='*',<291>,1:35]], any3=[[@17,54:54='q',<381>,1:54]], any2=[[@5,15:15='q',<381>,1:15]]}, table_dictionary={t1={*=[[@11,35:35='*',<291>,1:35]]}}, interface={*=[{name=*, table_ref=*}]}}, filters=[{name=any3, table_ref=q}], interface={any1=[{name=any1, table_ref=q}], any2=[{name=any2, table_ref=q}]}, table_alias={q=query0}}}",
				snippet.getSymbolTable().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{t1={*=[[@11,35:35='*',<291>,1:35]]}}",
				snippet.getTableDictionary().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				snippet.getSubstitutionsMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{query0={any1=[[@1,7:7='q',<381>,1:7]], *=[[@11,35:35='*',<291>,1:35]], any3=[[@17,54:54='q',<381>,1:54]], any2=[[@5,15:15='q',<381>,1:15]]}, query1={any1=[[@3,9:12='any1',<381>,1:9]], any2=[[@7,17:20='any2',<381>,1:17]]}}",
				snippet.getQueryColumnDictionaryMap().toString());
		assertFatalDiagnosticCount(snippet, null, null, null, 0);
	}

	@Test
	public void coverageDrivenSubqueryUnresolvedQualifierPassUpToParentTest() {
		final String query = "SELECT * FROM (SELECT x.a FROM t1) q";
		final Snippet snippet = runFailedSyntaxSQLParserTest(query, SQLPARSER_SQL_TREE_KEY, 1);

		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=*, table_ref=*}}}, from={table={alias=q, query={select={1={column={name=a, table_ref=x}}}, from={table={alias=null, table=t1}}}}}}}",
				snippet.getSqlAbstractTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]",
				snippet.getQueryInterface().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{query1={query_dictionary={*=[[@1,7:7='*',<291>,1:7]]}, table_dictionary={}, def_query0={query_dictionary={a=[[@7,24:24='a',<381>,1:24]], *=[[@1,7:7='*',<291>,1:7]]}, table_dictionary={x={a=[[@5,22:22='x',<381>,1:22]]}, t1={}}, interface={a=[{name=a, table_ref=x}]}}, interface={*=[{name=*, table_ref=*}]}, table_alias={q=query0}}}",
				snippet.getSymbolTable().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{x={a=[[@5,22:22='x',<381>,1:22]]}, t1={}}",
				snippet.getTableDictionary().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				snippet.getSubstitutionsMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{query0={a=[[@7,24:24='a',<381>,1:24]], *=[[@1,7:7='*',<291>,1:7]]}, query1={*=[[@1,7:7='*',<291>,1:7]]}}",
				snippet.getQueryColumnDictionaryMap().toString());
		assertDiagnosticByCode(snippet, "QUALIFIED_COLUMN_NOT_FOUND_IN_TABLE", ParseDiagnostic.Severity.FATAL,
				"No alias or table called 'x'", "a");
		assertFatalDiagnosticCount(snippet, "QUALIFIED_COLUMN_NOT_FOUND_IN_TABLE", null, null, 1);
	}

	/*
	===============================================================================
	  END QUERY SPECIFICATION AND SELECT INTO COVERAGE REGRESSION TESTS
	===============================================================================
	*/

	// Helper methods to run the SQL parser tests and validate results
	/**
	 * Run the SQL parser test with the given query.
	 * This method uses the SqlParserAccess class to parse the SQL query
	 * and returns a Snippet object containing the results.
	 * * It prints the results to the console, including the SQL abstract tree,
	 * * the JSON representation of the tree, the query interface, the symbol table,
	 * * the table dictionary, and the substitution variables.
	 * * It also asserts that there are no fatal errors in the parsing process.
	 * * @throws RecognitionException
	 * @param query
	 * @return
	 */
	private Snippet runSuccessfulSQLParserTest(final String query, String endPoint) {
		try {
			Snippet snippet = runParserGetSnippet(query, endPoint);

			final int numErrors = snippet.getFatalErrorCount();
			Assert.assertEquals("Expected no failures with " + query + " but got " + snippet.getFatalErrorStringList(), 
				0, numErrors);

			return snippet;

		} catch (RecognitionException e) {
			System.err.println("Exception parsing eqn: " + query);
			System.err.println("Recognition Exception: " + e.getMessage());
            Assert.fail("Recognition Exception: " + e.getMessage());    
        }
		return null;
	}

	private Snippet runFailedSyntaxSQLParserTest(final String query, String endPoint, int expectedFatalCount) {
		try {
			Snippet snippet = runParserGetSnippet(query, endPoint);

			final int numErrors = snippet.getFatalErrorCount();
			Assert.assertEquals("Expected fatal error count mismatch for " + query + " with " + snippet.getFatalErrorStringList(),
				expectedFatalCount, numErrors);
			Assert.assertNotNull("Access object should be initialized", this.lastAccessObject);
			if (expectedFatalCount > 0) {
				Assert.assertTrue("Access object should report fatal errors for " + query,
						this.lastAccessObject.hasFatalErrors());
				Assert.assertEquals("Access object fatal error count should match snippet count",
						expectedFatalCount, this.lastAccessObject.getFatalErrorCount());
			} else {
				Assert.assertFalse("Access object should not report fatal errors for " + query,
						this.lastAccessObject.hasFatalErrors());
			}

			return snippet;

		} catch (RecognitionException e) {
			System.err.println("Exception parsing eqn: " + query);
			System.err.println("Recognition Exception: " + e.getMessage());
			Assert.fail("Recognition Exception: " + e.getMessage());
		}
		return null;
	}


	private Snippet runParserGetSnippet(final String query, String endPoint) {
		System.out.println();
		
		SqlParserAccess accessObject = new SqlParserAccess(true, true, true);
		this.lastAccessObject = accessObject;

		accessObject.executeTheParse(query, endPoint);

		Snippet snippet = accessObject.getSnippet();

		System.out.println("AST: " + snippet.getSqlAbstractTree());
		System.out.println("Query Column Dictionary: " + snippet.getQueryColumnDictionaryMap());
		System.out.println("Interface: " + snippet.getQueryInterface());
		System.out.println("Symbol Tree: " + snippet.getSymbolTable());
		System.out.println("Table Dictionary: " + snippet.getTableDictionary());
		System.out.println("Substitution Variables: " + snippet.getSubstitutionsMap());
		System.out.println("Parser Message List: " + snippet.getParserMessageList());
		System.out.println("Parser Diagnostic List: " + snippet.getParserDiagnosticList());
		System.out.println("Fatal Error Count: " + snippet.getFatalErrorCount());
		System.out.println("Fatal Errors: " + snippet.getFatalErrorStringList());
		
		return snippet;
	}

}

