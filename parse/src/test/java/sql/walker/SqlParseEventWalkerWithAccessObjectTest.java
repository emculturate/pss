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

	private void assertUnresolvedUnknownColumnsFatalDiagnostic(
			Snippet snippet,
			int expectedLine,
			int expectedCharPositionInLine,
			String expectedColumnNameInMessage) {
		assertUnresolvedUnknownColumnsFatalDiagnostic(
				snippet,
				expectedLine,
				expectedCharPositionInLine,
				expectedColumnNameInMessage,
				0,
				null);
	}

	private void assertUnresolvedUnknownColumnsFatalDiagnostic(
			Snippet snippet,
			int expectedLine,
			int expectedCharPositionInLine,
			String expectedColumnNameInMessage,
			int fatalErrorIndex,
			String expectedFatalErrorText) {
		Assert.assertNotNull("Snippet should not be null", snippet);
		Assert.assertNotNull("Diagnostic list should not be null", snippet.getParserDiagnosticList());
		Assert.assertNotNull("Fatal error list should not be null", snippet.getFatalErrorStringList());
		Assert.assertTrue("Fatal error index out of bounds: " + fatalErrorIndex,
				fatalErrorIndex >= 0 && fatalErrorIndex < snippet.getFatalErrorStringList().size());

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
		Assert.assertEquals("Unexpected diagnostic severity", ParseDiagnostic.Severity.FATAL,
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
		if (expectedFatalErrorText != null) {
			String fatalErrorText = snippet.getFatalErrorStringList().get(fatalErrorIndex);
			Assert.assertEquals("Unexpected fatal error text at index " + fatalErrorIndex,
					expectedFatalErrorText, fatalErrorText);
		}
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
		Assert.assertEquals("Symbol Table is wrong", "{query0={tab1={a=[[@1,7:7='a',<328>,1:7], [@7,28:28='a',<328>,1:28]], b=[[@3,10:10='b',<328>,1:10], [@9,32:32='b',<328>,1:32]]}, filters=[{name=a, table_ref=null}, {name=b, table_ref=null}], interface={a=[{name=a, table_ref=null}], b=[{name=b, table_ref=null}]}}}",
			snippet.getSymbolTable().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={a=[[@1,7:7='a',<328>,1:7], [@7,28:28='a',<328>,1:28]], b=[[@3,10:10='b',<328>,1:10], [@9,32:32='b',<328>,1:32]]}}",
			snippet.getTableDictionary().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
			snippet.getSubstitutionsMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={a=[[@1,7:7='a',<328>,1:7]], b=[[@3,10:10='b',<328>,1:10]]}}",
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
		Assert.assertEquals("Symbol Table is wrong", "{query0={tab1={a=[[@1,7:7='a',<328>,1:7], [@7,28:28='a',<328>,1:28]], b=[[@3,10:10='b',<328>,1:10], [@9,32:32='b',<328>,1:32]]}, filters=[{name=a, table_ref=null}, {name=b, table_ref=null}], interface={a=[{name=a, table_ref=null}], b=[{name=b, table_ref=null}]}}}",
			snippet.getSymbolTable().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={a=[[@1,7:7='a',<328>,1:7], [@7,28:28='a',<328>,1:28]], b=[[@3,10:10='b',<328>,1:10], [@9,32:32='b',<328>,1:32]]}}",
			snippet.getTableDictionary().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
			snippet.getSubstitutionsMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={a=[[@1,7:7='a',<328>,1:7]], b=[[@3,10:10='b',<328>,1:10]]}}",
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
		Assert.assertEquals("Symbol Table is wrong", "{item={emp_sales=[[@0,0:3='item',<328>,1:0]]}}",
			snippet.getSymbolTable().toString()); 
		Assert.assertEquals("Table Dictionary is wrong", "{item={emp_sales=[[@0,0:3='item',<328>,1:0]]}}",
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
		Assert.assertEquals("Symbol Table is wrong", "{table1={emp_sales_count=[[@0,0:5='table1',<328>,1:0]]}}",
			snippet.getSymbolTable().toString()); 
		Assert.assertEquals("Table Dictionary is wrong", "{table1={emp_sales_count=[[@0,0:5='table1',<328>,1:0]]}}",
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
		Assert.assertEquals("Symbol Table is wrong", "{schema1={emp_sales=[[@0,0:6='schema1',<328>,1:0]]}}",
			snippet.getSymbolTable().toString()); 
		Assert.assertEquals("Table Dictionary is wrong", "{schema1={emp_sales=[[@0,0:6='schema1',<328>,1:0]]}}",
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
		Assert.assertEquals("Symbol Table is wrong", "{emp_sales={}}",
			snippet.getSymbolTable().toString()); 
		Assert.assertEquals("Table Dictionary is wrong", "{emp_sales={}}",
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
		Assert.assertEquals("Symbol Table is wrong", "{aa=<[Hedgss].[college]>, <[Hedgss].[college]>={id=[[@10,45:46='aa',<328>,1:45]]}, a={id=[[@6,40:40='a',<328>,1:40]]}, filters=[{name=id, table_ref=a}, {name=id, table_ref=aa}]}",
			snippet.getSymbolTable().toString()); 
		Assert.assertEquals("Table Dictionary is wrong", "{<[Hedgss].[college]>={id=[[@10,45:46='aa',<328>,1:45]]}, a={id=[[@6,40:40='a',<328>,1:40]]}}",
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
			Assert.assertEquals("Symbol Table is wrong", "{values0={values={$1=[[@2,9:9='(',<285>,1:9], [@10,24:24='(',<285>,1:24]], $2=[[@2,9:9='(',<285>,1:9], [@10,24:24='(',<285>,1:24]], $3=[[@2,9:9='(',<285>,1:9], [@10,24:24='(',<285>,1:24]]}, interface={$1=[[@2,9:9='(',<285>,1:9], [@10,24:24='(',<285>,1:24]], $2=[[@2,9:9='(',<285>,1:9], [@10,24:24='(',<285>,1:24]], $3=[[@2,9:9='(',<285>,1:9], [@10,24:24='(',<285>,1:24]]}}}",
					snippet.getSymbolTable().toString()); 
			Assert.assertEquals("Table Dictionary is wrong", "{}",
					snippet.getTableDictionary().toString());
			Assert.assertEquals("Substitution List is wrong", "{}", 
					snippet.getSubstitutionsMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{values0=null}",
        	snippet.getQueryColumnDictionaryMap().toString());
		}

	@Test
	public void ambiguousColumnAllocationWithAccessObjectMergedDiagnosticsTest() {
		final String query = " select dd.a aa, cc.b, c from tab1 dd join tab2 cc";
		final Snippet snippet = runFailedSyntaxSQLParserTest(query, SQLPARSER_SQL_TREE_KEY, 1);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=a, table_ref=dd}, alias=aa}, 2={column={name=b, table_ref=cc}}, 3={column={name=c, table_ref=null}}}, from={join={1={table={alias=dd, table=tab1}}, 2={join=join}, 3={table={alias=cc, table=tab2}}}}}}",
				snippet.getSqlAbstractTree().toString());
		Assert.assertEquals("Interface is wrong", "[aa, b, c]",
				snippet.getQueryInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				snippet.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={a=[[@1,8:9='dd',<328>,1:8]]}, tab2={b=[[@6,17:18='cc',<328>,1:17]]}}",
				snippet.getTableDictionary().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={aa=[[@4,13:14='aa',<328>,1:13]], b=[[@8,20:20='b',<328>,1:20]], c=[[@10,23:23='c',<328>,1:23]]}}",
				snippet.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={dd=tab1, cc=tab2, tab1={a=[[@1,8:9='dd',<328>,1:8]]}, tab2={b=[[@6,17:18='cc',<328>,1:17]]}, interface={aa=[{name=a, table_ref=dd}], b=[{name=b, table_ref=cc}], c=[{name=c, table_ref=null}]}, unknown={c=[[@10,23:23='c',<328>,1:23]]}}}",
				snippet.getSymbolTable().toString());

		assertUnresolvedUnknownColumnsFatalDiagnostic(snippet, 1, 23, "c");
		assertFatalDiagnosticCount(snippet, "UNRESOLVED_UNQUALIFIED_COLUMNS", null, "c", 1);
	}

	@Test
	public void ambiguousColumnAllocationInNestedSubqueriesDiagnosticsTest() {
		// Tests collection of fatal errors in two different subqueries where columns are not defined by their input subqueries

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
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={x=[[@14,40:40='x',<328>,2:9]]}, tab2={z=[[@32,105:105='z',<328>,3:9]]}}",
				snippet.getTableDictionary().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={a=[[@16,45:45='a',<328>,2:14]]}, query1={y=[[@34,110:110='y',<328>,3:14]]}, query2={b=[[@26,79:79='b',<328>,2:48]], missing=[[@28,82:88='missing',<328>,2:51]]}, query3={aa=[[@4,13:14='aa',<328>,1:13]], b=[[@8,20:20='b',<328>,1:20]], c=[[@10,23:23='c',<328>,1:23]]}}",
				snippet.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query3={dd=query0, cc=query2, def_query0={tab1={x=[[@14,40:40='x',<328>,2:9]]}, interface={a=[{name=x, table_ref=null}]}}, filters=[{name=a, table_ref=dd}, {name=b, table_ref=cc}], query0={a=[[@1,8:9='dd',<328>,1:8], [@42,133:134='dd',<328>,3:37]]}, interface={aa=[{name=a, table_ref=dd}], b=[{name=b, table_ref=cc}], c=[{name=c, table_ref=null}]}, query2={b=[[@6,17:18='cc',<328>,1:17], [@46,140:141='cc',<328>,3:44]]}, unknown={c=[[@10,23:23='c',<328>,1:23]]}, def_query2={ee=query1, def_query1={tab2={z=[[@32,105:105='z',<328>,3:9]]}, interface={y=[{name=z, table_ref=null}]}}, interface={b=[{name=y, table_ref=null}], missing=[{name=missing, table_ref=null}]}, query1={y=[[@24,74:74='y',<328>,2:43]]}, unknown={missing=[[@28,82:88='missing',<328>,2:51]]}}}}",
				snippet.getSymbolTable().toString());

		assertUnresolvedUnknownColumnsFatalDiagnostic(snippet, 2, 51, "missing");
		assertUnresolvedUnknownColumnsFatalDiagnostic(snippet, 1, 23, "c");
		assertFatalDiagnosticCount(snippet, "UNRESOLVED_UNQUALIFIED_COLUMNS", null, "missing", 1);
		assertFatalDiagnosticCount(snippet, "UNRESOLVED_UNQUALIFIED_COLUMNS", null, "c", 1);
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
		Assert.assertEquals("Table Dictionary is wrong", "{ee={a=[[@10,32:32='a',<328>,1:32]], e=[[@12,35:35='e',<328>,1:35]]}}",
				snippet.getTableDictionary().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={a=[[@10,32:32='a',<328>,1:32]], b=[[@14,40:40='b',<328>,1:40]]}, query1={aa=[[@2,10:11='aa',<328>,1:10]], b=[[@4,14:14='b',<328>,1:14]], c=[[@6,17:17='c',<328>,1:17]]}}",
				snippet.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query1={dd=query0, def_query0={ee={a=[[@10,32:32='a',<328>,1:32]], e=[[@12,35:35='e',<328>,1:35]]}, filters=[], interface={a=[{name=a, table_ref=null}], b=[{name=e, table_ref=null}]}}, query0={a=[[@1,8:8='a',<328>,1:8]], b=[[@4,14:14='b',<328>,1:14]]}, interface={aa=[{name=a, table_ref=null}], b=[{name=b, table_ref=null}], c=[{name=c, table_ref=null}]}, unknown={c=[[@6,17:17='c',<328>,1:17]]}}}",
				snippet.getSymbolTable().toString());

		assertUnresolvedUnknownColumnsFatalDiagnostic(snippet, 1, 17, "c");
		assertFatalDiagnosticCount(snippet, "UNRESOLVED_UNQUALIFIED_COLUMNS", null, "c", 1);
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

		assertUnresolvedUnknownColumnsFatalDiagnostic(snippet, 1, 17, "c");
		assertFatalDiagnosticCount(snippet, "UNRESOLVED_UNQUALIFIED_COLUMNS", null, "c", 1);
	}

	@Test
    public void ambiguityWarnings_UnresolvedInterfaceColumnInSingleSubquery2() {
		// Parent query selects column 'a'which is not produced by the single subquery source
		String query = " SELECT distinct a,b,c FROM (select all b,c from tab2) tab1";
		final Snippet snippet = runFailedSyntaxSQLParserTest(query, SQLPARSER_SQL_TREE_KEY, 1);

		Assert.assertNotNull("Snippet should not be null", snippet);
		Assert.assertNotNull("AST should not be null", snippet.getSqlAbstractTree());
		assertUnresolvedUnknownColumnsFatalDiagnostic(snippet, 1, 17, "a");
		assertFatalDiagnosticCount(snippet, "UNRESOLVED_UNQUALIFIED_COLUMNS", null, "a", 1);
    }

	@Test
	public void ambiguityWarnings_UnknownImplicitInterfaceColumnDiagnosticTest() {
		String query = "SELECT a FROM (SELECT b FROM tab2) tab1";
		final Snippet snippet = runFailedSyntaxSQLParserTest(query, SQLPARSER_SQL_TREE_KEY, 1);

		assertFatalDiagnosticByCode(
				snippet,
				"UNRESOLVED_UNQUALIFIED_COLUMNS",
				"Unresolved unqualified column reference(s)",
				"a");
		assertFatalDiagnosticCount(snippet, "UNRESOLVED_UNQUALIFIED_COLUMNS", null, "a", 1);
	}

	@Test
	public void ambiguityWarnings_AmbiguousInterfaceColumnDiagnosticTest() {
		String query = "SELECT a FROM tab1 dd JOIN tab2 cc ON dd.a = cc.a";
		final Snippet snippet = runFailedSyntaxSQLParserTest(query, SQLPARSER_SQL_TREE_KEY, 1);

		assertFatalDiagnosticByCode(
				snippet,
				"UNRESOLVED_UNQUALIFIED_COLUMNS",
				"Unresolved unqualified column reference(s)",
				"a");
		assertFatalDiagnosticCount(snippet, "UNRESOLVED_UNQUALIFIED_COLUMNS", null, "a", 1);
	}

	@Test
	public void ambiguityWarnings_AmbiguousInterfaceColumnFromCompetingSubqueryAliasesDiagnosticTest() {
		String query = "SELECT a FROM (SELECT x AS a FROM tab1) dd JOIN (SELECT z AS a FROM tab2) cc ON dd.a = cc.a";
		final Snippet snippet = runFailedSyntaxSQLParserTest(query, SQLPARSER_SQL_TREE_KEY, 1);

		assertFatalDiagnosticByCode(
				snippet,
				"UNRESOLVED_UNQUALIFIED_COLUMNS",
				"Unresolved unqualified column reference(s)",
				"a");
	}

	@Test
	public void explicitQueryReferenceMissingColumnDiagnosticTest() {
		String query = "SELECT dd.missing FROM (SELECT x AS a FROM tab1) dd";
		final Snippet snippet = runFailedSyntaxSQLParserTest(query, SQLPARSER_SQL_TREE_KEY, 1);

		assertFatalDiagnosticByCode(
				snippet,
				"REFERENCED_COLUMN_NOT_FOUND_IN_QUERY",
				"not found in indicated query",
				"missing");
		assertFatalDiagnosticCount(snippet, "REFERENCED_COLUMN_NOT_FOUND_IN_QUERY", "indicated query", "missing", 1);
		assertFatalDiagnosticCount(snippet, "REFERENCED_COLUMN_NOT_FOUND_IN_QUERY", null, "missing", 1);
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
		Assert.assertEquals("Query Column Dictionary is wrong", "{values0={a=[[@13,33:33='a',<328>,1:33], [@1,7:8='dd',<328>,1:7]]}, query1={a=[[@3,10:10='a',<328>,1:10]]}}",
				snippet.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query1={dd=values0, values0={dd=values, values={a=[[@7,25:25='(',<285>,1:25]]}, interface={a=[[@7,25:25='(',<285>,1:25]]}}, interface={a=[{name=a, table_ref=dd}]}}}",
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
				"REFERENCED_COLUMN_NOT_FOUND_IN_TABLE",
				tableCode);
		Assert.assertNotNull("Expected table-specific diagnostic message template", tableTemplate);
		Assert.assertTrue("Expected table-specific diagnostic template wording",
				tableTemplate.contains("indicated table"));
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
		Assert.assertEquals("AST JSON is wrong", "{\"INSERT\":{\"preamble\":\"insert_into\",\"select\":{\"1\":{\"column\":{\"name\":\"a\"}},\"2\":{\"column\":{\"name\":\"b\"}}},\"from\":{\"table\":{\"table\":\"tab2\"}},\"table\":{\"table\":{\"table\":\"tab1\"}}}}",
        	snippet.getSqlAbstractTreeJson());
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
			Assert.assertTrue("Access object should report fatal errors for " + query,
					this.lastAccessObject.hasFatalErrors());
			Assert.assertEquals("Access object fatal error count should match snippet count",
				expectedFatalCount, this.lastAccessObject.getFatalErrorCount());

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

