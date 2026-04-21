package sql.walker;

import org.antlr.v4.runtime.RecognitionException;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import access.Snippet;
import access.SqlParserAccess;
import astwalkers.SqlASTWalkerHelper;
import errorhandling.ParseDiagnostic;
import static mumble.SQLParserEndPoints.SQLPARSER_COLUMN_TREE_KEY;
import static mumble.SQLParserEndPoints.SQLPARSER_CONDITION_TREE_KEY;
import static mumble.SQLParserEndPoints.SQLPARSER_INSERT_TREE_KEY;
import static mumble.SQLParserEndPoints.SQLPARSER_IN_LIST_TREE_KEY;
import static mumble.SQLParserEndPoints.SQLPARSER_JOIN_EXTENSION_TREE_KEY;
import static mumble.SQLParserEndPoints.SQLPARSER_PREDICAND_TREE_KEY;
import static mumble.SQLParserEndPoints.SQLPARSER_QUERY_TREE_KEY;
import static mumble.SQLParserEndPoints.SQLPARSER_SQL_TREE_KEY;
import static mumble.SQLParserEndPoints.SQLPARSER_TUPLE_TREE_KEY;
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
		Assert.assertEquals("Symbol Table is wrong", "{query0={query_dictionary={a=[[@1,7:7='a',<329>,1:7]], b=[[@3,10:10='b',<329>,1:10]]}, table_dictionary={tab1={a=[[@1,7:7='a',<329>,1:7], [@7,28:28='a',<329>,1:28]], b=[[@3,10:10='b',<329>,1:10], [@9,32:32='b',<329>,1:32]]}}, filters=[{name=a, table_ref=tab1}, {name=b, table_ref=tab1}], interface={a=[{name=a, table_ref=tab1}], b=[{name=b, table_ref=tab1}]}}}",
			snippet.getSymbolTable().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={a=[[@1,7:7='a',<329>,1:7], [@7,28:28='a',<329>,1:28]], b=[[@3,10:10='b',<329>,1:10], [@9,32:32='b',<329>,1:32]]}}",
			snippet.getTableDictionary().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
			snippet.getSubstitutionsMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={a=[[@1,7:7='a',<329>,1:7]], b=[[@3,10:10='b',<329>,1:10]]}}",
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
		Assert.assertEquals("Symbol Table is wrong", "{query0={query_dictionary={a=[[@1,7:7='a',<329>,1:7]], b=[[@3,10:10='b',<329>,1:10]]}, table_dictionary={tab1={a=[[@1,7:7='a',<329>,1:7], [@7,28:28='a',<329>,1:28]], b=[[@3,10:10='b',<329>,1:10], [@9,32:32='b',<329>,1:32]]}}, filters=[{name=a, table_ref=tab1}, {name=b, table_ref=tab1}], interface={a=[{name=a, table_ref=tab1}], b=[{name=b, table_ref=tab1}]}}}",
			snippet.getSymbolTable().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={a=[[@1,7:7='a',<329>,1:7], [@7,28:28='a',<329>,1:28]], b=[[@3,10:10='b',<329>,1:10], [@9,32:32='b',<329>,1:32]]}}",
			snippet.getTableDictionary().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
			snippet.getSubstitutionsMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={a=[[@1,7:7='a',<329>,1:7]], b=[[@3,10:10='b',<329>,1:10]]}}",
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
		Assert.assertEquals("Symbol Table is wrong", "{table_dictionary={unresolved_column={item.emp_sales=[[@0,0:3='item',<329>,1:0]]}}, unresolved_column={item.emp_sales={column={name=emp_sales, table_ref=item}, locations=[[@0,0:3='item',<329>,1:0]]}}}",
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
		Assert.assertEquals("Symbol Table is wrong", "{table_dictionary={unresolved_column={table1.emp_sales_count=[[@0,0:5='table1',<329>,1:0]]}}, unresolved_column={table1.emp_sales_count={column={name=emp_sales_count, table_ref=table1}, locations=[[@0,0:5='table1',<329>,1:0]]}}}",
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
		Assert.assertEquals("Symbol Table is wrong", "{table_dictionary={unresolved_column={schema1.emp_sales=[[@0,0:6='schema1',<329>,1:0]]}}, unresolved_column={schema1.emp_sales={column={name=emp_sales, table_ref=schema1}, locations=[[@0,0:6='schema1',<329>,1:0]]}}}",
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
		Assert.assertEquals("Symbol Table is wrong", "{table_dictionary={<[Hedgss].[college]>={id=[[@10,45:46='aa',<329>,1:45]]}}, unresolved_column={a.id={column={name=id, table_ref=a}, locations=[[@6,40:40='a',<329>,1:40]]}}, filters=[{name=id, table_ref=a}, {name=id, table_ref=aa}], table_alias={aa=<[Hedgss].[college]>}}",
			snippet.getSymbolTable().toString()); 
		Assert.assertEquals("Table Dictionary is wrong", "{<[Hedgss].[college]>={id=[[@10,45:46='aa',<329>,1:45]]}}",
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
			Assert.assertEquals("Symbol Table is wrong", "{values0={query_dictionary={$1=[[@2,9:9='(',<285>,1:9], [@10,24:24='(',<285>,1:24]], $2=[[@2,9:9='(',<285>,1:9], [@10,24:24='(',<285>,1:24]], $3=[[@2,9:9='(',<285>,1:9], [@10,24:24='(',<285>,1:24]]}, table_dictionary={}, interface={$1=[], $2=[], $3=[]}}}",
					snippet.getSymbolTable().toString()); 
			Assert.assertEquals("Table Dictionary is wrong", "{}",
					snippet.getTableDictionary().toString());
			Assert.assertEquals("Substitution List is wrong", "{}", 
					snippet.getSubstitutionsMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{values0={$1=[[@2,9:9='(',<285>,1:9], [@10,24:24='(',<285>,1:24]], $2=[[@2,9:9='(',<285>,1:9], [@10,24:24='(',<285>,1:24]], $3=[[@2,9:9='(',<285>,1:9], [@10,24:24='(',<285>,1:24]]}}",
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
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={a=[[@1,8:9='dd',<329>,1:8]]}, tab2={b=[[@6,17:18='cc',<329>,1:17]]}}",
				snippet.getTableDictionary().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={aa=[[@4,13:14='aa',<329>,1:13]], b=[[@8,20:20='b',<329>,1:20]], c=[[@10,23:23='c',<329>,1:23]]}}",
				snippet.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={query_dictionary={aa=[[@4,13:14='aa',<329>,1:13]], b=[[@8,20:20='b',<329>,1:20]], c=[[@10,23:23='c',<329>,1:23]]}, table_dictionary={tab1={a=[[@1,8:9='dd',<329>,1:8]]}, tab2={b=[[@6,17:18='cc',<329>,1:17]]}}, interface={aa=[{name=a, table_ref=dd}], b=[{name=b, table_ref=cc}], c=[{name=c, table_ref=null}]}, table_alias={dd=tab1, cc=tab2}}}",
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
		final Snippet snippet = runFailedSyntaxSQLParserTest(query, SQLPARSER_SQL_TREE_KEY, 0);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=a, table_ref=dd}, alias=aa}, 2={column={name=b, table_ref=cc}}, 3={column={name=c, table_ref=null}}}, from={join={1={table={alias=dd, query={select={1={column={name=x, table_ref=null}, alias=a}}, from={table={alias=null, table=tab1}}}}}, 2={join=join, on={condition={left={column={name=a, table_ref=dd}}, right={column={name=b, table_ref=cc}}, operator==}}}, 3={table={alias=cc, query={select={1={column={name=y, table_ref=null}, alias=b}, 2={column={name=missing, table_ref=null}}}, from={table={alias=ee, query={select={1={column={name=z, table_ref=null}, alias=y}}, from={table={alias=null, table=tab2}}}}}}}}}}}}",
				snippet.getSqlAbstractTree().toString());
		Assert.assertEquals("Interface is wrong", "[aa, b, c]",
				snippet.getQueryInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				snippet.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={x=[[@14,40:40='x',<329>,2:9]]}, tab2={z=[[@32,105:105='z',<329>,3:9]]}}",
				snippet.getTableDictionary().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={a=[[@16,45:45='a',<329>,2:14]]}, query1={y=[[@34,110:110='y',<329>,3:14]]}, query2={b=[[@26,79:79='b',<329>,2:48]], missing=[[@28,82:88='missing',<329>,2:51]]}, query3={aa=[[@4,13:14='aa',<329>,1:13]], b=[[@8,20:20='b',<329>,1:20]], c=[[@10,23:23='c',<329>,1:23]]}}",
				snippet.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query3={query_dictionary={aa=[[@4,13:14='aa',<329>,1:13]], b=[[@8,20:20='b',<329>,1:20]], c=[[@10,23:23='c',<329>,1:23]]}, table_dictionary={}, def_query0={query_dictionary={a=[[@16,45:45='a',<329>,2:14]]}, table_dictionary={tab1={x=[[@14,40:40='x',<329>,2:9]]}}, interface={a=[{name=x, table_ref=tab1}]}}, filters=[{name=a, table_ref=dd}, {name=b, table_ref=cc}], interface={aa=[{name=a, table_ref=dd}], b=[{name=b, table_ref=cc}], c=[{name=c, table_ref=null}]}, table_alias={dd=query0, cc=query2}, def_query2={query_dictionary={b=[[@26,79:79='b',<329>,2:48]], missing=[[@28,82:88='missing',<329>,2:51]]}, table_dictionary={}, def_query1={query_dictionary={y=[[@34,110:110='y',<329>,3:14]]}, table_dictionary={tab2={z=[[@32,105:105='z',<329>,3:9]]}}, interface={y=[{name=z, table_ref=tab2}]}}, interface={b=[{name=y, table_ref=query1}], missing=[{name=missing, table_ref=null}]}, table_alias={ee=query1}}}}",
				snippet.getSymbolTable().toString());

		assertDiagnosticCountBySeverity(
				snippet,
				"UNRESOLVED_UNQUALIFIED_COLUMNS",
				ParseDiagnostic.Severity.ERROR,
				null,
				null,
				2);
		assertDiagnosticCountBySeverity(
				snippet,
				"AMBIGUOUS_COLUMN_REFERENCE",
				ParseDiagnostic.Severity.SEVERE_WARNING,
				null,
				null,
				0);
	}

	@Test
	public void unresolvedUnknownSymbolTableWithSimpleSubqueryWithAccessObjectMergedDiagnosticsTest() {
		final String query = " select a aa, b, c from (select a, e as b from ee where 1=1) dd";
		final Snippet snippet = runFailedSyntaxSQLParserTest(query, SQLPARSER_SQL_TREE_KEY, 0);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=a, table_ref=null}, alias=aa}, 2={column={name=b, table_ref=null}}, 3={column={name=c, table_ref=null}}}, from={table={alias=dd, query={select={1={column={name=a, table_ref=null}}, 2={column={name=e, table_ref=null}, alias=b}}, from={table={alias=null, table=ee}}, where={condition={left={literal=1}, right={literal=1}, operator==}}}}}}}",
				snippet.getSqlAbstractTree().toString());
		Assert.assertEquals("Interface is wrong", "[aa, b, c]",
				snippet.getQueryInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				snippet.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{ee={a=[[@10,32:32='a',<329>,1:32]], e=[[@12,35:35='e',<329>,1:35]]}}",
				snippet.getTableDictionary().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={a=[[@10,32:32='a',<329>,1:32]], b=[[@14,40:40='b',<329>,1:40]]}, query1={aa=[[@2,10:11='aa',<329>,1:10]], b=[[@4,14:14='b',<329>,1:14]], c=[[@6,17:17='c',<329>,1:17]]}}",
				snippet.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query1={query_dictionary={aa=[[@2,10:11='aa',<329>,1:10]], b=[[@4,14:14='b',<329>,1:14]], c=[[@6,17:17='c',<329>,1:17]]}, table_dictionary={}, def_query0={query_dictionary={a=[[@10,32:32='a',<329>,1:32]], b=[[@14,40:40='b',<329>,1:40]]}, table_dictionary={ee={a=[[@10,32:32='a',<329>,1:32]], e=[[@12,35:35='e',<329>,1:35]]}}, filters=[], interface={a=[{name=a, table_ref=ee}], b=[{name=e, table_ref=ee}]}}, interface={aa=[{name=a, table_ref=query0}], b=[{name=b, table_ref=query0}], c=[{name=c, table_ref=null}]}, table_alias={dd=query0}}}",
				snippet.getSymbolTable().toString());

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
		final Snippet snippet = runFailedSyntaxSQLParserTest(query, SQLPARSER_SQL_TREE_KEY, 0);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=a, table_ref=null}, alias=aa}, 2={column={name=b, table_ref=null}}, 3={column={name=c, table_ref=null}}}, from={table={alias=dd, query={select={1={column={name=a, table_ref=null}}, 2={column={name=e, table_ref=null}, alias=b}}, from={table={alias=null, table=ee}}, where={condition={left={literal=1}, right={literal=1}, operator==}}}}}}}",
				snippet.getSqlAbstractTree().toString());

		assertUnresolvedUnknownColumnsDiagnostic(snippet, 1, 17, ParseDiagnostic.Severity.ERROR, "c");
		assertDiagnosticCountBySeverity(snippet, "UNRESOLVED_UNQUALIFIED_COLUMNS", ParseDiagnostic.Severity.ERROR, null, "c", 1);
	}

	@Test
    public void ambiguityWarnings_UnresolvedInterfaceColumnInSingleSubquery2() {
		// Parent query selects column 'a'which is not produced by the single subquery source
		String query = " SELECT distinct a,b,c FROM (select all b,c from tab2) tab1";
		final Snippet snippet = runFailedSyntaxSQLParserTest(query, SQLPARSER_SQL_TREE_KEY, 0);

		Assert.assertNotNull("Snippet should not be null", snippet);
		Assert.assertNotNull("AST should not be null", snippet.getSqlAbstractTree());
		assertUnresolvedUnknownColumnsDiagnostic(snippet, 1, 17, ParseDiagnostic.Severity.ERROR, "a");
		assertDiagnosticCountBySeverity(snippet, "UNRESOLVED_UNQUALIFIED_COLUMNS", ParseDiagnostic.Severity.ERROR, null, "a", 1);
    }

	@Test
	public void ambiguityWarnings_UnknownImplicitInterfaceColumnDiagnosticTest() {
		String query = "SELECT a FROM (SELECT b FROM tab2) tab1";
		final Snippet snippet = runFailedSyntaxSQLParserTest(query, SQLPARSER_SQL_TREE_KEY, 0);

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
		Assert.assertEquals("Query Column Dictionary is wrong", "{values0={a=[[@13,33:33='a',<329>,1:33]]}, query1={a=[[@3,10:10='a',<329>,1:10]]}}",
				snippet.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query1={query_dictionary={a=[[@3,10:10='a',<329>,1:10]]}, table_dictionary={}, def_values0={query_dictionary={a=[[@13,33:33='a',<329>,1:33]]}, table_dictionary={}, interface={a=[]}}, interface={a=[{name=a, table_ref=dd}]}, table_alias={dd=values0}}}",
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

		
		Assert.assertEquals("AST is wrong", "{INSERT={preamble=insert_into, select={1={column={name=a, table_ref=null}}, 2={column={name=b, table_ref=null}}}, from={table={alias=null, table=tab2}}, table={table={alias=null, table=tab1}}}}",
        	snippet.getSqlAbstractTree().toString());
		Assert.assertEquals("Interface is wrong", "[a, b]", 
        	snippet.getQueryInterface().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={tab2={a=[[@4,24:24='a',<328>,1:24]], b=[[@6,26:26='b',<328>,1:26]]}, interface={a=[{name=a, table_ref=null}], b=[{name=b, table_ref=null}]}}, tab1={}, query0={}}",
        	snippet.getSymbolTable().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab2={a=[[@4,24:24='a',<328>,1:24]], b=[[@6,26:26='b',<328>,1:26]]}}",
        	snippet.getTableDictionary().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
        	snippet.getSubstitutionsMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={a=[[@4,24:24='a',<328>,1:24]], b=[[@6,26:26='b',<328>,1:26]]}}",
       	snippet.getQueryColumnDictionaryMap().toString());
	}
   
	@Ignore
	@Test
	public void basicInsertFromVariableTest() {
		final String query = "insert into tab1 <tuple variable>";
        final Snippet snippet = runSuccessfulSQLParserTest(query, SQLPARSER_INSERT_TREE_KEY);

		
		Assert.assertEquals("AST is wrong", "{INSERT={preamble=insert_into, substitution={name=<tuple variable>, type=query}, table={table={alias=null, table=tab1}}}}",
        	snippet.getSqlAbstractTree().toString());
		Assert.assertEquals("AST JSON is wrong", "{\"INSERT\":{\"preamble\":\"insert_into\",\"substitution\":{\"name\":\"\\u003ctuple variable\\u003e\",\"type\":\"query\"},\"table\":{\"table\":{\"table\":\"tab1\"}}}}",
        	snippet.getSqlAbstractTreeJson());
		Assert.assertEquals("Interface is wrong", "[]", 
        	snippet.getQueryInterface().toString());
		Assert.assertEquals("Symbol Table is wrong", "{<tuple variable>={}, tab1={}}",
        	snippet.getSymbolTable().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{<tuple variable>={}}",
        	snippet.getTableDictionary().toString());
		Assert.assertEquals("Substitution List is wrong", "{<tuple variable>=query}", 
        	snippet.getSubstitutionsMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{}",
        	snippet.getQueryColumnDictionaryMap().toString());
	}
    
	@Ignore
	@Test
	public void basicInsertFromValuesTest() {
		final String query = "insert into tab1 values (1,2,3), (2,3,4)";
        final Snippet snippet = runSuccessfulSQLParserTest(query, SQLPARSER_INSERT_TREE_KEY);

		
		Assert.assertEquals("AST is wrong", "{INSERT={preamble=insert_into, values={matrix={1={row={1={literal=1}, 2={literal=2}, 3={literal=3}}}, 2={row={1={literal=2}, 2={literal=3}, 3={literal=4}}}}}, table={table={alias=null, table=tab1}}}}",
        	snippet.getSqlAbstractTree().toString());
		Assert.assertEquals("AST JSON is wrong", "{\"INSERT\":{\"preamble\":\"insert_into\",\"values\":{\"matrix\":{\"1\":{\"row\":{\"1\":{\"literal\":\"1\"},\"2\":{\"literal\":\"2\"},\"3\":{\"literal\":\"3\"}}},\"2\":{\"row\":{\"1\":{\"literal\":\"2\"},\"2\":{\"literal\":\"3\"},\"3\":{\"literal\":\"4\"}}}}},\"table\":{\"table\":{\"table\":\"tab1\"}}}}",
        	snippet.getSqlAbstractTreeJson());
		Assert.assertEquals("Interface is wrong", "[]", 
        	snippet.getQueryInterface().toString());
		Assert.assertEquals("Symbol Table is wrong", "{values={$1=[[@4,24:24='(',<285>,1:24], [@12,33:33='(',<285>,1:33]], $2=[[@4,24:24='(',<285>,1:24], [@12,33:33='(',<285>,1:33]], $3=[[@4,24:24='(',<285>,1:24], [@12,33:33='(',<285>,1:33]]}, tab1={}}",
        	snippet.getSymbolTable().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{}",
        	snippet.getTableDictionary().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
        	snippet.getSubstitutionsMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{}",
        	snippet.getQueryColumnDictionaryMap().toString());
	}


	@Ignore
	@Test
	public void basicInsertWithColumnsFromQueryTest() {
		final String query = "insert into tab1 (c ,d) select a,b from tab2";
        final Snippet snippet = runSuccessfulSQLParserTest(query, SQLPARSER_INSERT_TREE_KEY);

		
		Assert.assertEquals("AST is wrong", "{INSERT={preamble=insert_into, select={1={column={name=a, table_ref=null}}, 2={column={name=b, table_ref=null}}}, columns={1={column={name=c, table_ref=null}}, 2={column={name=d, table_ref=null}}}, from={table={alias=null, table=tab2}}, table={table={alias=null, table=tab1}}}}",
        	snippet.getSqlAbstractTree().toString());
		Assert.assertEquals("Interface is wrong", "[a, b]", 
        	snippet.getQueryInterface().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={tab2={a=[[@9,31:31='a',<328>,1:31]], b=[[@11,33:33='b',<328>,1:33]]}, interface={a=[{name=a, table_ref=null}], b=[{name=b, table_ref=null}]}}, tab1={}, query0={}, unknown={c=[[@4,18:18='c',<328>,1:18]], d=[[@6,21:21='d',<328>,1:21]]}}",
        	snippet.getSymbolTable().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab2={a=[[@9,31:31='a',<328>,1:31]], b=[[@11,33:33='b',<328>,1:33]]}}",
        	snippet.getTableDictionary().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
        	snippet.getSubstitutionsMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={a=[[@9,31:31='a',<328>,1:31]], b=[[@11,33:33='b',<328>,1:33]]}}",
        	snippet.getQueryColumnDictionaryMap().toString());
	}
   
	@Ignore
	@Test
	public void basicInsertWithColumnsFromVariableTest() {
		final String query = "insert into tab1 (c ,d, e)  <tuple variable>";
        final Snippet snippet = runSuccessfulSQLParserTest(query, SQLPARSER_INSERT_TREE_KEY);

		
		Assert.assertEquals("AST is wrong", "{INSERT={preamble=insert_into, substitution={name=<tuple variable>, type=query}, columns={1={column={name=c, table_ref=null}}, 2={column={name=d, table_ref=null}}, 3={column={name=e, table_ref=null}}}, table={table={alias=null, table=tab1}}}}",
        	snippet.getSqlAbstractTree().toString());
		Assert.assertEquals("Interface is wrong", "[]", 
        	snippet.getQueryInterface().toString());
		Assert.assertEquals("Symbol Table is wrong", "{<tuple variable>={}, tab1={}, unknown={c=[[@4,18:18='c',<328>,1:18]], d=[[@6,21:21='d',<328>,1:21]], e=[[@8,24:24='e',<328>,1:24]]}}",
        	snippet.getSymbolTable().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{<tuple variable>={}}",
        	snippet.getTableDictionary().toString());
		Assert.assertEquals("Substitution List is wrong", "{<tuple variable>=query}", 
        	snippet.getSubstitutionsMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{}",
        	snippet.getQueryColumnDictionaryMap().toString());
	}
    
	 
	@Ignore
	@Test
	public void basicInsertWithColumnsFromValuesTest() {
		final String query = "insert into tab1  (c ,d)  values (1,2,3), (2,3,4)";
        final Snippet snippet = runSuccessfulSQLParserTest(query, SQLPARSER_INSERT_TREE_KEY);

		
		Assert.assertEquals("AST is wrong", "{INSERT={preamble=insert_into, columns={1={column={name=c, table_ref=null}}, 2={column={name=d, table_ref=null}}}, values={matrix={1={row={1={literal=1}, 2={literal=2}, 3={literal=3}}}, 2={row={1={literal=2}, 2={literal=3}, 3={literal=4}}}}}, table={table={alias=null, table=tab1}}}}",
        	snippet.getSqlAbstractTree().toString());
		Assert.assertEquals("Interface is wrong", "[]", 
        	snippet.getQueryInterface().toString());
		Assert.assertEquals("Symbol Table is wrong", "{values={$1=[[@9,33:33='(',<285>,1:33], [@17,42:42='(',<285>,1:42]], $2=[[@9,33:33='(',<285>,1:33], [@17,42:42='(',<285>,1:42]], $3=[[@9,33:33='(',<285>,1:33], [@17,42:42='(',<285>,1:42]]}, tab1={}, unknown={c=[[@4,19:19='c',<328>,1:19]], d=[[@6,22:22='d',<328>,1:22]]}}",
        	snippet.getSymbolTable().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{}",
        	snippet.getTableDictionary().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
        	snippet.getSubstitutionsMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{}",
        	snippet.getQueryColumnDictionaryMap().toString());
	}

	@Ignore
	@Test
	public void simpleFailingInsertFromQueryTest() {

		final String query = " insert into sch.subj.tbl (newcol1, newcol2) values (SELECT b.att1, b.att2 "
				+ " from (SELECT a.col1 as att1, a.col2 as att2 " 
				+ " FROM sch.subj.tab1 as a"
				+ " WHERE a.col1 <> a.col3 " + " ) AS b )";

		final Snippet snippet = runFailedSyntaxSQLParserTest(query, SQLPARSER_INSERT_TREE_KEY, 2);

		
		Assert.assertEquals("AST is wrong", "[Line 1:53 - null - unexpected input: 'SELECT', Exception when walking the parse tree: Cannot invoke \"java.util.Map.remove(Object)\" because \"subMap\" is null]",
					snippet.getFatalErrorStringList().toString());
		Assert.assertEquals("AST JSON is wrong", 2,
					snippet.getFatalErrorCount());

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
		Assert.assertEquals("Symbol Table is wrong", "{query0={query_dictionary={*=[[@1,7:7='*',<289>,1:7]]}, table_dictionary={{{ ref('my_model') }}={*=[[@1,7:7='*',<289>,1:7]]}}, interface={*=[{name=*, table_ref=*}]}}}",
			snippet.getSymbolTable().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{{{ ref('my_model') }}={*=[[@1,7:7='*',<289>,1:7]]}}",
			snippet.getTableDictionary().toString());
		Assert.assertEquals("Substitution List is wrong", "{{{ ref('my_model') }}=tuple}",
			snippet.getSubstitutionsMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@1,7:7='*',<289>,1:7]]}}",
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
		Assert.assertEquals("Symbol Table is wrong", "{query0={query_dictionary={*=[[@1,7:7='*',<289>,1:7]]}, table_dictionary={{{ source('raw', 'orders') }}={*=[[@1,7:7='*',<289>,1:7]]}}, interface={*=[{name=*, table_ref=*}]}}}",
			snippet.getSymbolTable().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{{{ source('raw', 'orders') }}={*=[[@1,7:7='*',<289>,1:7]]}}",
			snippet.getTableDictionary().toString());
		Assert.assertEquals("Substitution List is wrong", "{{{ source('raw', 'orders') }}=tuple}",
			snippet.getSubstitutionsMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@1,7:7='*',<289>,1:7]]}}",
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
		Assert.assertEquals("Symbol Table is wrong", "{query0={query_dictionary={*=[[@1,7:7='*',<289>,1:7]]}, table_dictionary={{{ stream('event_stream') }}={*=[[@1,7:7='*',<289>,1:7]]}}, interface={*=[{name=*, table_ref=*}]}}}",
			snippet.getSymbolTable().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{{{ stream('event_stream') }}={*=[[@1,7:7='*',<289>,1:7]]}}",
			snippet.getTableDictionary().toString());
		Assert.assertEquals("Substitution List is wrong", "{{{ stream('event_stream') }}=tuple}",
			snippet.getSubstitutionsMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@1,7:7='*',<289>,1:7]]}}",
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
		Assert.assertEquals("Symbol Table is wrong", "{query0={query_dictionary={*=[[@1,7:7='*',<289>,1:7]]}, table_dictionary={{{ var('active_table') }}={*=[[@1,7:7='*',<289>,1:7]]}}, interface={*=[{name=*, table_ref=*}]}}}",
			snippet.getSymbolTable().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{{{ var('active_table') }}={*=[[@1,7:7='*',<289>,1:7]]}}",
			snippet.getTableDictionary().toString());
		Assert.assertEquals("Substitution List is wrong", "{{{ var('active_table') }}=tuple}",
			snippet.getSubstitutionsMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@1,7:7='*',<289>,1:7]]}}",
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
		Assert.assertEquals("Symbol Table is wrong", "{query0={query_dictionary={*=[[@1,7:7='*',<289>,1:7]]}, table_dictionary={{{ env_var('dbt_table_name') }}={*=[[@1,7:7='*',<289>,1:7]]}}, interface={*=[{name=*, table_ref=*}]}}}",
			snippet.getSymbolTable().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{{{ env_var('dbt_table_name') }}={*=[[@1,7:7='*',<289>,1:7]]}}",
			snippet.getTableDictionary().toString());
		Assert.assertEquals("Substitution List is wrong", "{{{ env_var('DBT_TABLE_NAME') }}=tuple}",
			snippet.getSubstitutionsMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@1,7:7='*',<289>,1:7]]}}",
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
		Assert.assertEquals("Symbol Table is wrong", "{query0={query_dictionary={*=[[@1,7:7='*',<289>,1:7]]}, table_dictionary={{{ config.get('materialized') }}={*=[[@1,7:7='*',<289>,1:7]]}}, interface={*=[{name=*, table_ref=*}]}}}",
			snippet.getSymbolTable().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{{{ config.get('materialized') }}={*=[[@1,7:7='*',<289>,1:7]]}}",
			snippet.getTableDictionary().toString());
		Assert.assertEquals("Substitution List is wrong", "{{{ config.get('materialized') }}=tuple}",
			snippet.getSubstitutionsMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@1,7:7='*',<289>,1:7]]}}",
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
		Assert.assertEquals("Symbol Table is wrong", "{query0={query_dictionary={*=[[@1,7:7='*',<289>,1:7]]}, table_dictionary={{{ target.schema }}={*=[[@1,7:7='*',<289>,1:7]]}}, interface={*=[{name=*, table_ref=*}]}}}",
			snippet.getSymbolTable().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{{{ target.schema }}={*=[[@1,7:7='*',<289>,1:7]]}}",
			snippet.getTableDictionary().toString());
		Assert.assertEquals("Substitution List is wrong", "{{{ target.schema }}=tuple}",
			snippet.getSubstitutionsMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@1,7:7='*',<289>,1:7]]}}",
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
		Assert.assertEquals("Symbol Table is wrong", "{query0={query_dictionary={*=[[@1,7:7='*',<289>,1:7]]}, table_dictionary={{{ this }}={*=[[@1,7:7='*',<289>,1:7]]}}, interface={*=[{name=*, table_ref=*}]}}}",
			snippet.getSymbolTable().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{{{ this }}={*=[[@1,7:7='*',<289>,1:7]]}}",
			snippet.getTableDictionary().toString());
		Assert.assertEquals("Substitution List is wrong", "{{{ this }}=tuple}",
			snippet.getSubstitutionsMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@1,7:7='*',<289>,1:7]]}}",
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
		Assert.assertEquals("Symbol Table is wrong", "{query0={query_dictionary={*=[[@1,7:7='*',<289>,1:7]]}, table_dictionary={{{ this.schema }}={*=[[@1,7:7='*',<289>,1:7]]}}, interface={*=[{name=*, table_ref=*}]}}}",
			snippet.getSymbolTable().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{{{ this.schema }}={*=[[@1,7:7='*',<289>,1:7]]}}",
			snippet.getTableDictionary().toString());
		Assert.assertEquals("Substitution List is wrong", "{{{ this.schema }}=tuple}",
			snippet.getSubstitutionsMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@1,7:7='*',<289>,1:7]]}}",
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
		Assert.assertEquals("Symbol Table is wrong", "{query0={query_dictionary={*=[[@1,7:7='*',<289>,1:7]]}, table_dictionary={{{ this.database }}={*=[[@1,7:7='*',<289>,1:7]]}}, interface={*=[{name=*, table_ref=*}]}}}",
			snippet.getSymbolTable().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{{{ this.database }}={*=[[@1,7:7='*',<289>,1:7]]}}",
			snippet.getTableDictionary().toString());
		Assert.assertEquals("Substitution List is wrong", "{{{ this.database }}=tuple}",
			snippet.getSubstitutionsMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@1,7:7='*',<289>,1:7]]}}",
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
		Assert.assertEquals("Symbol Table is wrong", "{query0={query_dictionary={*=[[@1,7:7='*',<289>,1:7]]}, table_dictionary={{{ this.database.schema }}={*=[[@1,7:7='*',<289>,1:7]]}}, interface={*=[{name=*, table_ref=*}]}}}",
			snippet.getSymbolTable().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{{{ this.database.schema }}={*=[[@1,7:7='*',<289>,1:7]]}}",
			snippet.getTableDictionary().toString());
		Assert.assertEquals("Substitution List is wrong", "{{{ this.database.schema }}=tuple}",
			snippet.getSubstitutionsMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@1,7:7='*',<289>,1:7]]}}",
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
		Assert.assertEquals("Symbol Table is wrong", "{query0={query_dictionary={*=[[@1,7:7='*',<289>,1:7]]}, table_dictionary={{{ this.database.schema.tab }}={*=[[@1,7:7='*',<289>,1:7]]}}, interface={*=[{name=*, table_ref=*}]}}}",
			snippet.getSymbolTable().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{{{ this.database.schema.tab }}={*=[[@1,7:7='*',<289>,1:7]]}}",
			snippet.getTableDictionary().toString());
		Assert.assertEquals("Substitution List is wrong", "{{{ this.database.schema.tab }}=tuple}",
			snippet.getSubstitutionsMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@1,7:7='*',<289>,1:7]]}}",
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
		Assert.assertEquals("Symbol Table is wrong", "{query3={def_union2={query0={query_dictionary={audience=[[@11,73:80='audience',<329>,4:19]], *=[[@1,7:7='*',<289>,1:7]], stream_key=[[@15,101:110='stream_key',<329>,5:19]], eab_contact_id=[[@7,39:52='eab_contact_id',<329>,3:25]], valid_from_dt=[[@26,163:175='valid_from_dt',<329>,6:51]]}, table_dictionary={{{ source('pdp_ams', 'pdp_ams_mail_contacts') }}={audience=[[@9,59:71='mail_contacts',<329>,4:5]], intake_dt=[[@19,122:134='mail_contacts',<329>,6:10]], stream_key=[[@13,87:99='mail_contacts',<329>,5:5]], eab_contact_id=[[@5,25:37='mail_contacts',<329>,3:11]]}}, interface={audience=[{name=audience, table_ref=mail_contacts}], stream_key=[{name=stream_key, table_ref=mail_contacts}], eab_contact_id=[{name=eab_contact_id, table_ref=mail_contacts}], valid_from_dt=[{name=intake_dt, table_ref=mail_contacts}]}, table_alias={mail_contacts={{ source('PDP_AMS', 'pdp_ams_mail_contacts') }}}}, interface={audience=query_column, stream_key=query_column, eab_contact_id=query_column, valid_from_dt=query_column}, query1={query_dictionary={audience=[[@46,328:335='audience',<329>,10:22]], *=[[@1,7:7='*',<289>,1:7]], stream_key=[[@50,359:368='stream_key',<329>,11:22]], eab_contact_id=[[@42,291:304='eab_contact_id',<329>,9:29]], valid_from_dt=[[@61,422:434='valid_from_dt',<329>,12:52]]}, table_dictionary={{{ source('pdp_ams', 'pdp_ams_offset_marketing') }}={audience=[[@44,311:326='offset_marketing',<329>,10:5]], sent_dt=[[@54,380:395='offset_marketing',<329>,12:10]], stream_key=[[@48,342:357='offset_marketing',<329>,11:5]], eab_contact_id=[[@40,274:289='offset_marketing',<329>,9:12]]}}, interface={audience=[{name=audience, table_ref=offset_marketing}], stream_key=[{name=stream_key, table_ref=offset_marketing}], eab_contact_id=[{name=eab_contact_id, table_ref=offset_marketing}], valid_from_dt=[{name=sent_dt, table_ref=offset_marketing}]}, table_alias={offset_marketing={{ source('PDP_AMS', 'pdp_ams_offset_marketing') }}}}}, query_dictionary={*=[[@1,7:7='*',<289>,1:7]]}, table_dictionary={}, interface={*=[{name=*, table_ref=*}]}, table_alias={paper_data=union2}}}",
			snippet.getSymbolTable().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{{{ source('pdp_ams', 'pdp_ams_offset_marketing') }}={audience=[[@44,311:326='offset_marketing',<329>,10:5]], sent_dt=[[@54,380:395='offset_marketing',<329>,12:10]], stream_key=[[@48,342:357='offset_marketing',<329>,11:5]], eab_contact_id=[[@40,274:289='offset_marketing',<329>,9:12]]}, {{ source('pdp_ams', 'pdp_ams_mail_contacts') }}={audience=[[@9,59:71='mail_contacts',<329>,4:5]], intake_dt=[[@19,122:134='mail_contacts',<329>,6:10]], stream_key=[[@13,87:99='mail_contacts',<329>,5:5]], eab_contact_id=[[@5,25:37='mail_contacts',<329>,3:11]]}}",
			snippet.getTableDictionary().toString());
		Assert.assertEquals("Substitution List is wrong", "{{{ source('PDP_AMS', 'pdp_ams_mail_contacts') }}=tuple, {{ source('PDP_AMS', 'pdp_ams_offset_marketing') }}=tuple}",
			snippet.getSubstitutionsMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={audience=[[@11,73:80='audience',<329>,4:19]], *=[[@1,7:7='*',<289>,1:7]], stream_key=[[@15,101:110='stream_key',<329>,5:19]], eab_contact_id=[[@7,39:52='eab_contact_id',<329>,3:25]], valid_from_dt=[[@26,163:175='valid_from_dt',<329>,6:51]]}, query1={audience=[[@46,328:335='audience',<329>,10:22]], *=[[@1,7:7='*',<289>,1:7]], stream_key=[[@50,359:368='stream_key',<329>,11:22]], eab_contact_id=[[@42,291:304='eab_contact_id',<329>,9:29]], valid_from_dt=[[@61,422:434='valid_from_dt',<329>,12:52]]}, query3={*=[[@1,7:7='*',<289>,1:7]]}}",
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
		Assert.assertEquals("Symbol Table is wrong", "{query3={def_union2={query0={query_dictionary={audience=[[@49,323:330='audience',<329>,7:19]], stream_key=[[@53,351:360='stream_key',<329>,8:19]], eab_contact_id=[[@45,289:302='eab_contact_id',<329>,6:25]], valid_from_dt=[[@64,413:425='valid_from_dt',<329>,9:51]]}, table_dictionary={{{ source('pdp_ams', 'pdp_ams_mail_contacts') }}={audience=[[@47,309:321='mail_contacts',<329>,7:5]], intake_dt=[[@57,372:384='mail_contacts',<329>,9:10]], stream_key=[[@51,337:349='mail_contacts',<329>,8:5]], eab_contact_id=[[@43,275:287='mail_contacts',<329>,6:11]]}}, interface={audience=[{name=audience, table_ref=mail_contacts}], stream_key=[{name=stream_key, table_ref=mail_contacts}], eab_contact_id=[{name=eab_contact_id, table_ref=mail_contacts}], valid_from_dt=[{name=intake_dt, table_ref=mail_contacts}]}, table_alias={mail_contacts={{ source('PDP_AMS', 'pdp_ams_mail_contacts') }}}}, interface={audience=query_column, stream_key=query_column, eab_contact_id=query_column, valid_from_dt=query_column}, query1={query_dictionary={audience=[[@84,578:585='audience',<329>,13:22]], stream_key=[[@88,609:618='stream_key',<329>,14:22]], eab_contact_id=[[@80,541:554='eab_contact_id',<329>,12:29]], valid_from_dt=[[@99,672:684='valid_from_dt',<329>,15:52]]}, table_dictionary={{{ source('pdp_ams', 'pdp_ams_offset_marketing') }}={audience=[[@82,561:576='offset_marketing',<329>,13:5]], sent_dt=[[@92,630:645='offset_marketing',<329>,15:10]], stream_key=[[@86,592:607='offset_marketing',<329>,14:5]], eab_contact_id=[[@78,524:539='offset_marketing',<329>,12:12]]}}, interface={audience=[{name=audience, table_ref=offset_marketing}], stream_key=[{name=stream_key, table_ref=offset_marketing}], eab_contact_id=[{name=eab_contact_id, table_ref=offset_marketing}], valid_from_dt=[{name=sent_dt, table_ref=offset_marketing}]}, table_alias={offset_marketing={{ source('PDP_AMS', 'pdp_ams_offset_marketing') }}}}}, query_dictionary={audience=[[@7,52:59='audience',<329>,2:19]], rno=[[@39,255:257='rno',<329>,5:131]], stream_key=[[@11,80:89='stream_key',<329>,3:19]], eab_contact_id=[[@3,18:31='eab_contact_id',<329>,1:18]], valid_from_dt=[[@15,110:122='valid_from_dt',<329>,4:19]]}, table_dictionary={}, interface={audience=[{name=audience, table_ref=paper_data}], rno=[{name=eab_contact_id, table_ref=paper_data}, {name=stream_key, table_ref=paper_data}, {name=valid_from_dt, table_ref=paper_data}], stream_key=[{name=stream_key, table_ref=paper_data}], eab_contact_id=[{name=eab_contact_id, table_ref=paper_data}], valid_from_dt=[{name=valid_from_dt, table_ref=paper_data}]}, table_alias={paper_data=union2}}}",
			snippet.getSymbolTable().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{{{ source('pdp_ams', 'pdp_ams_offset_marketing') }}={audience=[[@82,561:576='offset_marketing',<329>,13:5]], sent_dt=[[@92,630:645='offset_marketing',<329>,15:10]], stream_key=[[@86,592:607='offset_marketing',<329>,14:5]], eab_contact_id=[[@78,524:539='offset_marketing',<329>,12:12]]}, {{ source('pdp_ams', 'pdp_ams_mail_contacts') }}={audience=[[@47,309:321='mail_contacts',<329>,7:5]], intake_dt=[[@57,372:384='mail_contacts',<329>,9:10]], stream_key=[[@51,337:349='mail_contacts',<329>,8:5]], eab_contact_id=[[@43,275:287='mail_contacts',<329>,6:11]]}}",
			snippet.getTableDictionary().toString());
		Assert.assertEquals("Substitution List is wrong", "{{{ source('PDP_AMS', 'pdp_ams_mail_contacts') }}=tuple, {{ source('PDP_AMS', 'pdp_ams_offset_marketing') }}=tuple}",
			snippet.getSubstitutionsMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={audience=[[@49,323:330='audience',<329>,7:19]], stream_key=[[@53,351:360='stream_key',<329>,8:19]], eab_contact_id=[[@45,289:302='eab_contact_id',<329>,6:25]], valid_from_dt=[[@64,413:425='valid_from_dt',<329>,9:51]]}, query1={audience=[[@84,578:585='audience',<329>,13:22]], stream_key=[[@88,609:618='stream_key',<329>,14:22]], eab_contact_id=[[@80,541:554='eab_contact_id',<329>,12:29]], valid_from_dt=[[@99,672:684='valid_from_dt',<329>,15:52]]}, query3={audience=[[@7,52:59='audience',<329>,2:19]], rno=[[@39,255:257='rno',<329>,5:131]], stream_key=[[@11,80:89='stream_key',<329>,3:19]], eab_contact_id=[[@3,18:31='eab_contact_id',<329>,1:18]], valid_from_dt=[[@15,110:122='valid_from_dt',<329>,4:19]]}}",
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
		Assert.assertEquals("Symbol Table is wrong", "{query3={query_dictionary={*=[[@1,7:7='*',<289>,1:7]]}, table_dictionary={}, def_intersect2={query0={query_dictionary={audience=[[@11,73:80='audience',<329>,4:19]], *=[[@1,7:7='*',<289>,1:7]], stream_key=[[@15,101:110='stream_key',<329>,5:19]], eab_contact_id=[[@7,39:52='eab_contact_id',<329>,3:25]], valid_from_dt=[[@26,163:175='valid_from_dt',<329>,6:51]]}, table_dictionary={{{ source('pdp_ams', 'pdp_ams_mail_contacts') }}={audience=[[@9,59:71='mail_contacts',<329>,4:5]], intake_dt=[[@19,122:134='mail_contacts',<329>,6:10]], stream_key=[[@13,87:99='mail_contacts',<329>,5:5]], eab_contact_id=[[@5,25:37='mail_contacts',<329>,3:11]]}}, interface={audience=[{name=audience, table_ref=mail_contacts}], stream_key=[{name=stream_key, table_ref=mail_contacts}], eab_contact_id=[{name=eab_contact_id, table_ref=mail_contacts}], valid_from_dt=[{name=intake_dt, table_ref=mail_contacts}]}, table_alias={mail_contacts={{ source('PDP_AMS', 'pdp_ams_mail_contacts') }}}}, interface={audience=query_column, stream_key=query_column, eab_contact_id=query_column, valid_from_dt=query_column}, query1={query_dictionary={audience=[[@46,332:339='audience',<329>,10:22]], *=[[@1,7:7='*',<289>,1:7]], stream_key=[[@50,363:372='stream_key',<329>,11:22]], eab_contact_id=[[@42,295:308='eab_contact_id',<329>,9:29]], valid_from_dt=[[@61,426:438='valid_from_dt',<329>,12:52]]}, table_dictionary={{{ source('pdp_ams', 'pdp_ams_offset_marketing') }}={audience=[[@44,315:330='offset_marketing',<329>,10:5]], sent_dt=[[@54,384:399='offset_marketing',<329>,12:10]], stream_key=[[@48,346:361='offset_marketing',<329>,11:5]], eab_contact_id=[[@40,278:293='offset_marketing',<329>,9:12]]}}, interface={audience=[{name=audience, table_ref=offset_marketing}], stream_key=[{name=stream_key, table_ref=offset_marketing}], eab_contact_id=[{name=eab_contact_id, table_ref=offset_marketing}], valid_from_dt=[{name=sent_dt, table_ref=offset_marketing}]}, table_alias={offset_marketing={{ source('PDP_AMS', 'pdp_ams_offset_marketing') }}}}}, interface={*=[{name=*, table_ref=*}]}, table_alias={paper_data=intersect2}}}",
			snippet.getSymbolTable().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{{{ source('pdp_ams', 'pdp_ams_offset_marketing') }}={audience=[[@44,315:330='offset_marketing',<329>,10:5]], sent_dt=[[@54,384:399='offset_marketing',<329>,12:10]], stream_key=[[@48,346:361='offset_marketing',<329>,11:5]], eab_contact_id=[[@40,278:293='offset_marketing',<329>,9:12]]}, {{ source('pdp_ams', 'pdp_ams_mail_contacts') }}={audience=[[@9,59:71='mail_contacts',<329>,4:5]], intake_dt=[[@19,122:134='mail_contacts',<329>,6:10]], stream_key=[[@13,87:99='mail_contacts',<329>,5:5]], eab_contact_id=[[@5,25:37='mail_contacts',<329>,3:11]]}}",
			snippet.getTableDictionary().toString());
		Assert.assertEquals("Substitution List is wrong", "{{{ source('PDP_AMS', 'pdp_ams_mail_contacts') }}=tuple, {{ source('PDP_AMS', 'pdp_ams_offset_marketing') }}=tuple}",
			snippet.getSubstitutionsMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={audience=[[@11,73:80='audience',<329>,4:19]], *=[[@1,7:7='*',<289>,1:7]], stream_key=[[@15,101:110='stream_key',<329>,5:19]], eab_contact_id=[[@7,39:52='eab_contact_id',<329>,3:25]], valid_from_dt=[[@26,163:175='valid_from_dt',<329>,6:51]]}, query1={audience=[[@46,332:339='audience',<329>,10:22]], *=[[@1,7:7='*',<289>,1:7]], stream_key=[[@50,363:372='stream_key',<329>,11:22]], eab_contact_id=[[@42,295:308='eab_contact_id',<329>,9:29]], valid_from_dt=[[@61,426:438='valid_from_dt',<329>,12:52]]}, query3={*=[[@1,7:7='*',<289>,1:7]]}}",
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
		Assert.assertEquals("Symbol Table is wrong", "{query3={query_dictionary={audience=[[@7,52:59='audience',<329>,2:19]], rno=[[@39,255:257='rno',<329>,5:131]], stream_key=[[@11,80:89='stream_key',<329>,3:19]], eab_contact_id=[[@3,18:31='eab_contact_id',<329>,1:18]], valid_from_dt=[[@15,110:122='valid_from_dt',<329>,4:19]]}, table_dictionary={}, def_intersect2={query0={query_dictionary={audience=[[@49,323:330='audience',<329>,7:19]], stream_key=[[@53,351:360='stream_key',<329>,8:19]], eab_contact_id=[[@45,289:302='eab_contact_id',<329>,6:25]], valid_from_dt=[[@64,413:425='valid_from_dt',<329>,9:51]]}, table_dictionary={{{ source('pdp_ams', 'pdp_ams_mail_contacts') }}={audience=[[@47,309:321='mail_contacts',<329>,7:5]], intake_dt=[[@57,372:384='mail_contacts',<329>,9:10]], stream_key=[[@51,337:349='mail_contacts',<329>,8:5]], eab_contact_id=[[@43,275:287='mail_contacts',<329>,6:11]]}}, interface={audience=[{name=audience, table_ref=mail_contacts}], stream_key=[{name=stream_key, table_ref=mail_contacts}], eab_contact_id=[{name=eab_contact_id, table_ref=mail_contacts}], valid_from_dt=[{name=intake_dt, table_ref=mail_contacts}]}, table_alias={mail_contacts={{ source('PDP_AMS', 'pdp_ams_mail_contacts') }}}}, interface={audience=query_column, stream_key=query_column, eab_contact_id=query_column, valid_from_dt=query_column}, query1={query_dictionary={audience=[[@84,582:589='audience',<329>,13:22]], stream_key=[[@88,613:622='stream_key',<329>,14:22]], eab_contact_id=[[@80,545:558='eab_contact_id',<329>,12:29]], valid_from_dt=[[@99,676:688='valid_from_dt',<329>,15:52]]}, table_dictionary={{{ source('pdp_ams', 'pdp_ams_offset_marketing') }}={audience=[[@82,565:580='offset_marketing',<329>,13:5]], sent_dt=[[@92,634:649='offset_marketing',<329>,15:10]], stream_key=[[@86,596:611='offset_marketing',<329>,14:5]], eab_contact_id=[[@78,528:543='offset_marketing',<329>,12:12]]}}, interface={audience=[{name=audience, table_ref=offset_marketing}], stream_key=[{name=stream_key, table_ref=offset_marketing}], eab_contact_id=[{name=eab_contact_id, table_ref=offset_marketing}], valid_from_dt=[{name=sent_dt, table_ref=offset_marketing}]}, table_alias={offset_marketing={{ source('PDP_AMS', 'pdp_ams_offset_marketing') }}}}}, interface={audience=[{name=audience, table_ref=paper_data}], rno=[{name=eab_contact_id, table_ref=paper_data}, {name=stream_key, table_ref=paper_data}, {name=valid_from_dt, table_ref=paper_data}], stream_key=[{name=stream_key, table_ref=paper_data}], eab_contact_id=[{name=eab_contact_id, table_ref=paper_data}], valid_from_dt=[{name=valid_from_dt, table_ref=paper_data}]}, table_alias={paper_data=intersect2}}}",
			snippet.getSymbolTable().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{{{ source('pdp_ams', 'pdp_ams_offset_marketing') }}={audience=[[@82,565:580='offset_marketing',<329>,13:5]], sent_dt=[[@92,634:649='offset_marketing',<329>,15:10]], stream_key=[[@86,596:611='offset_marketing',<329>,14:5]], eab_contact_id=[[@78,528:543='offset_marketing',<329>,12:12]]}, {{ source('pdp_ams', 'pdp_ams_mail_contacts') }}={audience=[[@47,309:321='mail_contacts',<329>,7:5]], intake_dt=[[@57,372:384='mail_contacts',<329>,9:10]], stream_key=[[@51,337:349='mail_contacts',<329>,8:5]], eab_contact_id=[[@43,275:287='mail_contacts',<329>,6:11]]}}",
			snippet.getTableDictionary().toString());
		Assert.assertEquals("Substitution List is wrong", "{{{ source('PDP_AMS', 'pdp_ams_mail_contacts') }}=tuple, {{ source('PDP_AMS', 'pdp_ams_offset_marketing') }}=tuple}",
			snippet.getSubstitutionsMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={audience=[[@49,323:330='audience',<329>,7:19]], stream_key=[[@53,351:360='stream_key',<329>,8:19]], eab_contact_id=[[@45,289:302='eab_contact_id',<329>,6:25]], valid_from_dt=[[@64,413:425='valid_from_dt',<329>,9:51]]}, query1={audience=[[@84,582:589='audience',<329>,13:22]], stream_key=[[@88,613:622='stream_key',<329>,14:22]], eab_contact_id=[[@80,545:558='eab_contact_id',<329>,12:29]], valid_from_dt=[[@99,676:688='valid_from_dt',<329>,15:52]]}, query3={audience=[[@7,52:59='audience',<329>,2:19]], rno=[[@39,255:257='rno',<329>,5:131]], stream_key=[[@11,80:89='stream_key',<329>,3:19]], eab_contact_id=[[@3,18:31='eab_contact_id',<329>,1:18]], valid_from_dt=[[@15,110:122='valid_from_dt',<329>,4:19]]}}",
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
		Assert.assertEquals("Symbol Table is wrong", "{query6={query_dictionary={a=[[@1,7:7='a',<329>,1:7]], b=[[@3,9:9='b',<329>,1:9]], c=[[@5,11:11='c',<329>,1:11]], d=[[@7,13:13='d',<329>,1:13]]}, table_dictionary={}, def_intersect2={query0={query_dictionary={a=[[@11,32:32='a',<329>,2:11]], b=[[@13,34:34='b',<329>,2:13]], c=[[@15,36:36='c',<329>,2:15]]}, table_dictionary={t1={a=[[@11,32:32='a',<329>,2:11], [@35,110:110='a',<329>,5:11]], b=[[@13,34:34='b',<329>,2:13], [@37,112:112='b',<329>,5:13]], c=[[@15,36:36='c',<329>,2:15], [@39,114:114='c',<329>,5:15]]}}, interface={a=[{name=a, table_ref=t1}], b=[{name=b, table_ref=t1}], c=[{name=c, table_ref=t1}]}}, interface={a=query_column, b=query_column, c=query_column}, query1={query_dictionary={a=[[@26,76:76='a',<329>,3:17]], b=[[@20,70:70='b',<329>,3:11]], c=[[@22,72:72='c',<329>,3:13]], d=[[@24,74:74='d',<329>,3:15]]}, table_dictionary={t2={a=[[@26,76:76='a',<329>,3:17]], b=[[@20,70:70='b',<329>,3:11], [@46,150:150='b',<329>,6:11]], c=[[@22,72:72='c',<329>,3:13], [@48,152:152='c',<329>,6:13]], d=[[@24,74:74='d',<329>,3:15], [@50,154:154='d',<329>,6:15]]}}, interface={a=[{name=a, table_ref=t2}], b=[{name=b, table_ref=t2}], c=[{name=c, table_ref=t2}], d=[{name=d, table_ref=t2}]}}}, def_intersect5={interface={a=query_column, b=query_column, c=query_column, d=query_column}, query4={query_dictionary={b=[[@46,150:150='b',<329>,6:11]], c=[[@48,152:152='c',<329>,6:13]], d=[[@50,154:154='d',<329>,6:15]]}, table_dictionary={t2={b=[[@46,150:150='b',<329>,6:11]], c=[[@48,152:152='c',<329>,6:13]], d=[[@50,154:154='d',<329>,6:15]]}}, interface={b=[{name=b, table_ref=t2}], c=[{name=c, table_ref=t2}], d=[{name=d, table_ref=t2}]}}, query3={query_dictionary={a=[[@35,110:110='a',<329>,5:11]], b=[[@37,112:112='b',<329>,5:13]], c=[[@39,114:114='c',<329>,5:15]], d=[[@41,116:116='d',<329>,5:17]]}, table_dictionary={t1={a=[[@35,110:110='a',<329>,5:11]], b=[[@37,112:112='b',<329>,5:13]], c=[[@39,114:114='c',<329>,5:15]], d=[[@41,116:116='d',<329>,5:17]]}}, interface={a=[{name=a, table_ref=t1}], b=[{name=b, table_ref=t1}], c=[{name=c, table_ref=t1}], d=[{name=d, table_ref=t1}]}}}, interface={a=[{name=a, table_ref=null}], b=[{name=b, table_ref=null}], c=[{name=c, table_ref=null}], d=[{name=d, table_ref=intersect5}]}, table_alias={i1=intersect2, i2=intersect5}}}",
			snippet.getSymbolTable().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{t1={a=[[@11,32:32='a',<329>,2:11], [@35,110:110='a',<329>,5:11]], b=[[@13,34:34='b',<329>,2:13], [@37,112:112='b',<329>,5:13]], c=[[@15,36:36='c',<329>,2:15], [@39,114:114='c',<329>,5:15]], d=[[@41,116:116='d',<329>,5:17]]}, t2={a=[[@26,76:76='a',<329>,3:17]], b=[[@20,70:70='b',<329>,3:11], [@46,150:150='b',<329>,6:11]], c=[[@22,72:72='c',<329>,3:13], [@48,152:152='c',<329>,6:13]], d=[[@24,74:74='d',<329>,3:15], [@50,154:154='d',<329>,6:15]]}}",
			snippet.getTableDictionary().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
			snippet.getSubstitutionsMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query4={b=[[@46,150:150='b',<329>,6:11]], c=[[@48,152:152='c',<329>,6:13]], d=[[@50,154:154='d',<329>,6:15]]}, query6={a=[[@1,7:7='a',<329>,1:7]], b=[[@3,9:9='b',<329>,1:9]], c=[[@5,11:11='c',<329>,1:11]], d=[[@7,13:13='d',<329>,1:13]]}, query0={a=[[@11,32:32='a',<329>,2:11]], b=[[@13,34:34='b',<329>,2:13]], c=[[@15,36:36='c',<329>,2:15]]}, query1={a=[[@26,76:76='a',<329>,3:17]], b=[[@20,70:70='b',<329>,3:11]], c=[[@22,72:72='c',<329>,3:13]], d=[[@24,74:74='d',<329>,3:15]]}, query3={a=[[@35,110:110='a',<329>,5:11]], b=[[@37,112:112='b',<329>,5:13]], c=[[@39,114:114='c',<329>,5:15]], d=[[@41,116:116='d',<329>,5:17]]}}",
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
		final Snippet snippet = runFailedSyntaxSQLParserTest(query, SQLPARSER_SQL_TREE_KEY, 3);

		Assert.assertTrue("Expected intersect mismatch diagnostic",
			snippet.getFatalErrorStringList().stream().anyMatch(message -> message.contains("INTERSECTION has different column counts")));
		Assert.assertTrue("Expected participant set-operation mismatch diagnostic",
			snippet.getFatalErrorStringList().stream().anyMatch(message -> message.contains("SET_OPERATION has different column counts")));
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
		Assert.assertEquals("Symbol Table is wrong", "{query0={query_dictionary={rno=[[@16,101:103='rno',<329>,1:101]], min_activity_dt=[[@44,284:298='min_activity_dt',<329>,3:109]], outbound_ind=[[@24,161:172='outbound_ind',<329>,2:55]], person_activity_key=[[@18,113:131='person_activity_key',<329>,2:7]], activity_id=[[@20,135:145='activity_id',<329>,2:29]], person_id=[[@22,149:157='person_id',<329>,2:43]]}, table_dictionary={personactivity={rno=[[@52,354:356='rno',<329>,4:54]], activity_dt=[[@11,60:70='activity_dt',<329>,1:60], [@28,186:196='activity_dt',<329>,3:11], [@37,237:247='activity_dt',<329>,3:62]], outbound_ind=[[@24,161:172='outbound_ind',<329>,2:55], [@48,330:341='outbound_ind',<329>,4:30]], person_activity_key=[[@13,74:92='person_activity_key',<329>,1:74], [@18,113:131='person_activity_key',<329>,2:7], [@40,255:273='person_activity_key',<329>,3:80]], activity_id=[[@20,135:145='activity_id',<329>,2:29]], person_id=[[@8,41:49='person_id',<329>,1:41], [@22,149:157='person_id',<329>,2:43], [@34,218:226='person_id',<329>,3:43], [@57,371:379='person_id',<329>,4:71]]}}, ordered_by=[{name=person_id, table_ref=personactivity}], filters=[{name=outbound_ind, table_ref=personactivity}, {name=rno, table_ref=personactivity}], interface={rno=[{name=person_id, table_ref=personactivity}, {name=activity_dt, table_ref=personactivity}, {name=person_activity_key, table_ref=personactivity}], min_activity_dt=[{name=person_id, table_ref=personactivity}, {name=activity_dt, table_ref=personactivity}, {name=person_activity_key, table_ref=personactivity}], outbound_ind=[{name=outbound_ind, table_ref=personactivity}], person_activity_key=[{name=person_activity_key, table_ref=personactivity}], activity_id=[{name=activity_id, table_ref=personactivity}], person_id=[{name=person_id, table_ref=personactivity}]}}}",
			snippet.getSymbolTable().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{personactivity={rno=[[@52,354:356='rno',<329>,4:54]], activity_dt=[[@11,60:70='activity_dt',<329>,1:60], [@28,186:196='activity_dt',<329>,3:11], [@37,237:247='activity_dt',<329>,3:62]], outbound_ind=[[@24,161:172='outbound_ind',<329>,2:55], [@48,330:341='outbound_ind',<329>,4:30]], person_activity_key=[[@13,74:92='person_activity_key',<329>,1:74], [@18,113:131='person_activity_key',<329>,2:7], [@40,255:273='person_activity_key',<329>,3:80]], activity_id=[[@20,135:145='activity_id',<329>,2:29]], person_id=[[@8,41:49='person_id',<329>,1:41], [@22,149:157='person_id',<329>,2:43], [@34,218:226='person_id',<329>,3:43], [@57,371:379='person_id',<329>,4:71]]}}",
			snippet.getTableDictionary().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
			snippet.getSubstitutionsMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={rno=[[@16,101:103='rno',<329>,1:101]], min_activity_dt=[[@44,284:298='min_activity_dt',<329>,3:109]], outbound_ind=[[@24,161:172='outbound_ind',<329>,2:55]], person_activity_key=[[@18,113:131='person_activity_key',<329>,2:7]], activity_id=[[@20,135:145='activity_id',<329>,2:29]], person_id=[[@22,149:157='person_id',<329>,2:43]]}}",
			snippet.getQueryColumnDictionaryMap().toString());
	}

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

