package sql.walker;

import org.antlr.v4.runtime.RecognitionException;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import access.Snippet;
import access.SqlParserAccess;
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

   
	@Test
	public void basicSelectSyntaxFailureTest1() {
		final String query = "select from";
		final Snippet snippet = runFailedSyntaxSQLParserTest(query, SQLPARSER_SQL_TREE_KEY);
				
		String text = snippet.getFatalErrorStringList().get(0);
		Assert.assertTrue("Expected a syntax error with " + query,
			text.equals("Line 1:7 - null - unexpected input: 'from'"));

		text = snippet.getFatalErrorStringList().get(1);
		Assert.assertTrue("Expected a syntax error with " + query,
				text.equals("Line 1:7 - Syntax error, attempting recovery")
			);

		text = snippet.getFatalErrorStringList().get(2);
		Assert.assertTrue("Expected a syntax error with " + query,
					text.equals("Exception when walking the parse tree: Cannot "
					+ "invoke \"java.util.Map.remove(Object)\" because \"subMap\" is null")
			);
	}				

   
	@Test
	public void basicSelectSyntaxFailureTest2() {
		final String query = "not a sql statement at all";
		final Snippet snippet = runFailedSyntaxSQLParserTest(query, SQLPARSER_SQL_TREE_KEY);
				
		String text = snippet.getFatalErrorStringList().get(0);
		Assert.assertTrue("Expected a syntax error with " + query,
			text.equals("Line 1:0 - null - unexpected input: 'not'"));

		text = snippet.getFatalErrorStringList().get(1);
		Assert.assertTrue("Expected a syntax error with " + query,
				text.equals("Line 1:0 - Syntax error, attempting recovery")
			);

		text = snippet.getFatalErrorStringList().get(2);
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
		Assert.assertEquals("Symbol Table is wrong", "{query0={tab1={a=[@1,7:7='a',<328>,1:7], b=[@3,10:10='b',<328>,1:10]}, interface={a={column={name=a, table_ref=null}}, b={column={name=b, table_ref=null}}}}}",
			snippet.getSymbolTable().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={a=[@1,7:7='a',<328>,1:7], b=[@3,10:10='b',<328>,1:10]}}",
			snippet.getTableDictionary().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
			snippet.getSubstitutionsMap().toString());
	}

	@Test
	public void basicQuerySnippetTest() {
		final String query = "select a, b from tab1 where a = b";
		final Snippet snippet = runSuccessfulSQLParserTest(query, SQLPARSER_QUERY_TREE_KEY);

		Assert.assertEquals("AST is wrong", "{QUERY={select={1={column={name=a, table_ref=null}}, 2={column={name=b, table_ref=null}}}, from={table={alias=null, table=tab1}}, where={condition={left={column={name=a, table_ref=null}}, right={column={name=b, table_ref=null}}, operator==}}}}",
			snippet.getSqlAbstractTree().toString());
		Assert.assertEquals("Interface is wrong", "[a, b]",
			snippet.getQueryInterface().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={tab1={a=[@1,7:7='a',<328>,1:7], b=[@3,10:10='b',<328>,1:10]}, interface={a={column={name=a, table_ref=null}}, b={column={name=b, table_ref=null}}}}}",
			snippet.getSymbolTable().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={a=[@1,7:7='a',<328>,1:7], b=[@3,10:10='b',<328>,1:10]}}",
			snippet.getTableDictionary().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
			snippet.getSubstitutionsMap().toString());
	}


	@Test
	public void basicPredicandSnippetTest() {
		String sql = "item.emp_sales";
		final Snippet snippet = runSuccessfulSQLParserTest(sql, SQLPARSER_PREDICAND_TREE_KEY);
		
		Assert.assertEquals("AST is wrong", "{PREDICAND={column={name=emp_sales, table_ref=item}}}",
			snippet.getSqlAbstractTree().toString());
		Assert.assertEquals("Interface is wrong", "[]", 
        	snippet.getQueryInterface().toString());
		Assert.assertEquals("Symbol Table is wrong", "{item={emp_sales=[@0,0:3='item',<328>,1:0]}}",
			snippet.getSymbolTable().toString()); 
		Assert.assertEquals("Table Dictionary is wrong", "{item={emp_sales=[@0,0:3='item',<328>,1:0]}}",
			snippet.getTableDictionary().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				snippet.getSubstitutionsMap().toString());
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
	}

	@Test
	public void basicConditionSnippetTest() {
		String sql = "table1.emp_sales_count is not null";
		final Snippet snippet = runSuccessfulSQLParserTest(sql, SQLPARSER_CONDITION_TREE_KEY);
		
		Assert.assertEquals("AST is wrong", "{CONDITION={condition={left={column={name=emp_sales_count, table_ref=table1}}, operator=is not null}}}",
			snippet.getSqlAbstractTree().toString());
		Assert.assertEquals("Interface is wrong", "[]", 
        	snippet.getQueryInterface().toString());
		Assert.assertEquals("Symbol Table is wrong", "{table1={emp_sales_count=[@0,0:5='table1',<328>,1:0]}}",
			snippet.getSymbolTable().toString()); 
		Assert.assertEquals("Table Dictionary is wrong", "{table1={emp_sales_count=[@0,0:5='table1',<328>,1:0]}}",
			snippet.getTableDictionary().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				snippet.getSubstitutionsMap().toString());
	}

	@Test
	public void basicColumnSnippetTest() {
		String sql = "schema1.emp_sales";
		final Snippet snippet = runSuccessfulSQLParserTest(sql, SQLPARSER_COLUMN_TREE_KEY);
		
		Assert.assertEquals("AST is wrong", "{COLUMN={column={name=emp_sales, table_ref=schema1}}}",
			snippet.getSqlAbstractTree().toString());
		Assert.assertEquals("Interface is wrong", "[]", 
        	snippet.getQueryInterface().toString());
		Assert.assertEquals("Symbol Table is wrong", "{schema1={emp_sales=[@0,0:6='schema1',<328>,1:0]}}",
			snippet.getSymbolTable().toString()); 
		Assert.assertEquals("Table Dictionary is wrong", "{schema1={emp_sales=[@0,0:6='schema1',<328>,1:0]}}",
			snippet.getTableDictionary().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				snippet.getSubstitutionsMap().toString());
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
	}


	@Test
	public void joinExtensionSnippetLeftJoinWithOnTest2() {
		final String sql = "left join <[Hedgss].[college]> as aa on a.id=aa.id "; 
       	final Snippet snippet = runSuccessfulSQLParserTest(sql, SQLPARSER_JOIN_EXTENSION_TREE_KEY);
		
		Assert.assertEquals("AST is wrong", "{JOIN_EXTENSION={1={join=left, on={condition={left={column={name=id, table_ref=a}}, right={column={name=id, table_ref=aa}}, operator==}}}, 2={table={alias=aa, substitution={name=<[Hedgss].[college]>, parts={1=[Hedgss], 2=[college]}, type=tuple}}}}}",
			snippet.getSqlAbstractTree().toString());
		Assert.assertEquals("Interface is wrong", "[]", 
        	snippet.getQueryInterface().toString());
		Assert.assertEquals("Symbol Table is wrong", "{aa=<[Hedgss].[college]>, <[Hedgss].[college]>={id=[@10,45:46='aa',<328>,1:45]}, a={id=[@6,40:40='a',<328>,1:40]}}",
			snippet.getSymbolTable().toString()); 
		Assert.assertEquals("Table Dictionary is wrong", "{<[Hedgss].[college]>={id=[@10,45:46='aa',<328>,1:45]}, a={id=[@6,40:40='a',<328>,1:40]}}",
			snippet.getTableDictionary().toString());
		Assert.assertEquals("Substitution List is wrong", "{<[Hedgss].[college]>=tuple}", 
				snippet.getSubstitutionsMap().toString());
	}


		@Test
		public void valuesStatementSnippetAloneTest() {
			final String query = " (values (1, 2, 'aaa'), (92, 3, 'aaa')) ";
			final Snippet snippet = runSuccessfulSQLParserTest(query, SQLPARSER_VALUES_TREE_KEY);
		
			Assert.assertEquals("AST is wrong", "{VALUES={values={matrix={1={row={1={literal=1}, 2={literal=2}, 3={literal='aaa'}}}, 2={row={1={literal=92}, 2={literal=3}, 3={literal='aaa'}}}}}}}",
					snippet.getSqlAbstractTree().toString());
			Assert.assertEquals("Interface is wrong", "[$1, $2, $3]", 
					snippet.getQueryInterface().toString());
			Assert.assertEquals("Symbol Table is wrong", "{values0={values={$1=[@2,9:9='(',<285>,1:9], $2=[@2,9:9='(',<285>,1:9], $3=[@2,9:9='(',<285>,1:9]}, interface={$1=[@2,9:9='(',<285>,1:9], $2=[@2,9:9='(',<285>,1:9], $3=[@2,9:9='(',<285>,1:9]}}}",
					snippet.getSymbolTable().toString()); 
			Assert.assertEquals("Table Dictionary is wrong", "{}",
					snippet.getTableDictionary().toString());
			Assert.assertEquals("Substitution List is wrong", "{}", 
					snippet.getSubstitutionsMap().toString());
		}


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
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={tab2={a=[@4,24:24='a',<328>,1:24], b=[@6,26:26='b',<328>,1:26]}, interface={a={column={name=a, table_ref=null}}, b={column={name=b, table_ref=null}}}}, tab1={}, query0={}}",
        	snippet.getSymbolTable().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab2={a=[@4,24:24='a',<328>,1:24], b=[@6,26:26='b',<328>,1:26]}}",
        	snippet.getTableDictionary().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
        	snippet.getSubstitutionsMap().toString());
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
		Assert.assertEquals("Symbol Table is wrong", "{values={$1=[@4,24:24='(',<285>,1:24], $2=[@4,24:24='(',<285>,1:24], $3=[@4,24:24='(',<285>,1:24]}, tab1={}}",
        	snippet.getSymbolTable().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{}",
        	snippet.getTableDictionary().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
        	snippet.getSubstitutionsMap().toString());
	}

	@Ignore 
	@Test
	public void basicInsertWithColumnsFromQueryTest() {
		final String query = "insert into tab1 (c ,d) select a,b from tab2";
        final Snippet snippet = runSuccessfulSQLParserTest(query, SQLPARSER_INSERT_TREE_KEY);

		
		Assert.assertEquals("AST is wrong", "{INSERT={select={1={column={name=a, table_ref=null}}, 2={column={name=b, table_ref=null}}}, from={table={alias=null, table=tab2}}, preamble=insert_into, table={table={alias=null, table=tab1}}}}",
        	snippet.getSqlAbstractTree().toString());
		Assert.assertEquals("AST JSON is wrong", "{\"INSERT\":{\"select\":{\"1\":{\"column\":{\"name\":\"a\"}},\"2\":{\"column\":{\"name\":\"b\"}}},\"from\":{\"table\":{\"table\":\"tab2\"}},\"preamble\":\"insert_into\",\"table\":{\"table\":{\"table\":\"tab1\"}}}}",
        	snippet.getSqlAbstractTreeJson());
		Assert.assertEquals("Interface is wrong", "[a, b]", 
        	snippet.getQueryInterface().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={tab2={a=[@4,24:24='a',<328>,1:24], b=[@6,26:26='b',<328>,1:26]}, interface={a={column={name=a, table_ref=null}}, b={column={name=b, table_ref=null}}}}, tab1={}, query0={}}",
        	snippet.getSymbolTable().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab2={a=[@4,24:24='a',<328>,1:24], b=[@6,26:26='b',<328>,1:26]}}",
        	snippet.getTableDictionary().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
        	snippet.getSubstitutionsMap().toString());
	}
   
	@Ignore 
	@Test
	public void basicInsertWithColumnsFromVariableTest() {
		final String query = "insert into tab1 (c ,d, e)  <tuple variable>";
        final Snippet snippet = runSuccessfulSQLParserTest(query, SQLPARSER_INSERT_TREE_KEY);

		
		Assert.assertEquals("AST is wrong", "{INSERT={substitution={name=<tuple variable>, type=query}, columns={1={column={name=c, table_ref=null}}, 2={column={name=d, table_ref=null}}}, preamble=insert_into, table={table={alias=null, table=tab1}}}}",
        	snippet.getSqlAbstractTree().toString());
		Assert.assertEquals("AST JSON is wrong", "{\"INSERT\":{\"substitution\":{\"name\":\"\\u003ctuple variable\\u003e\",\"type\":\"query\"},\"columns\":{\"1\":{\"column\":{\"name\":\"c\"}},\"2\":{\"column\":{\"name\":\"d\"}}},\"preamble\":\"insert_into\",\"table\":{\"table\":{\"table\":\"tab1\"}}}}",
        	snippet.getSqlAbstractTreeJson());
		Assert.assertEquals("Interface is wrong", "[]", 
        	snippet.getQueryInterface().toString());
		Assert.assertEquals("Symbol Table is wrong", "{<tuple variable>={}, tab1={}}",
        	snippet.getSymbolTable().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{<tuple variable>={}}",
        	snippet.getTableDictionary().toString());
		Assert.assertEquals("Substitution List is wrong", "{<tuple variable>=query}", 
        	snippet.getSubstitutionsMap().toString());
	}
    
	@Ignore 
	@Test
	public void basicInsertWithColumnsFromValuesTest() {
		final String query = "insert into tab1  (c ,d)  values (1,2,3), (2,3,4)";
        final Snippet snippet = runSuccessfulSQLParserTest(query, SQLPARSER_INSERT_TREE_KEY);

		
		Assert.assertEquals("AST is wrong", "{INSERT={values={matrix={1={row={1={literal=1}, 2={literal=2}, 3={literal=3}}}, 2={row={1={literal=2}, 2={literal=3}, 3={literal=4}}}}}, preamble=insert_into, table={table={alias=null, table=tab1}}}}",
        	snippet.getSqlAbstractTree().toString());
		Assert.assertEquals("AST JSON is wrong", "{\"INSERT\":{\"values\":{\"matrix\":{\"1\":{\"row\":{\"1\":{\"literal\":\"1\"},\"2\":{\"literal\":\"2\"},\"3\":{\"literal\":\"3\"}}},\"2\":{\"row\":{\"1\":{\"literal\":\"2\"},\"2\":{\"literal\":\"3\"},\"3\":{\"literal\":\"4\"}}}}},\"preamble\":\"insert_into\",\"table\":{\"table\":{\"table\":\"tab1\"}}}}",
        	snippet.getSqlAbstractTreeJson());
		Assert.assertEquals("Interface is wrong", "[]", 
        	snippet.getQueryInterface().toString());
		Assert.assertEquals("Symbol Table is wrong", "{values0=null, def_values0={interface=null, tab1={}, values={$1=[@4,24:24='(',<285>,1:24], $2=[@4,24:24='(',<285>,1:24], $3=[@4,24:24='(',<285>,1:24]}}, unnamed=values0}",
        	snippet.getSymbolTable().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{}",
        	snippet.getTableDictionary().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
        	snippet.getSubstitutionsMap().toString());
	}

	@Ignore
	@Test
	public void simpleInsertFromQueryTest() {

		final String query = " insert into sch.subj.tbl (newcol1, newcol2) values (SELECT b.att1, b.att2 "
				+ " from (SELECT a.col1 as att1, a.col2 as att2 " 
				+ " FROM sch.subj.tab1 as a"
				+ " WHERE a.col1 <> a.col3 " + " ) AS b )";

		final Snippet snippet = runSuccessfulSQLParserTest(query, SQLPARSER_INSERT_TREE_KEY);

		
		Assert.assertEquals("AST is wrong", "{INSERT={values={matrix={1={row={1={literal=1}, 2={literal=2}, 3={literal=3}}}, 2={row={1={literal=2}, 2={literal=3}, 3={literal=4}}}}}, preamble=insert_into, table={table={alias=null, table=tab1}}}}",
					snippet.getSqlAbstractTree().toString());
		Assert.assertEquals("AST JSON is wrong", "{\"INSERT\":{\"values\":{\"matrix\":{\"1\":{\"row\":{\"1\":{\"literal\":\"1\"},\"2\":{\"literal\":\"2\"},\"3\":{\"literal\":\"3\"}}},\"2\":{\"row\":{\"1\":{\"literal\":\"2\"},\"2\":{\"literal\":\"3\"},\"3\":{\"literal\":\"4\"}}}}},\"preamble\":\"insert_into\",\"table\":{\"table\":{\"table\":\"tab1\"}}}}",
					snippet.getSqlAbstractTreeJson());
		Assert.assertEquals("Interface is wrong", "[]", 
					snippet.getQueryInterface().toString());
		Assert.assertEquals("Symbol Table is wrong", "{values0=null, def_values0={interface=null, tab1={}, values={$1=[@4,24:24='(',<285>,1:24], $2=[@4,24:24='(',<285>,1:24], $3=[@4,24:24='(',<285>,1:24]}}, unnamed=values0}",
					snippet.getSymbolTable().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{}",
					snippet.getTableDictionary().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
					snippet.getSubstitutionsMap().toString());
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

	private Snippet runFailedSyntaxSQLParserTest(final String query, String endPoint) {
		try {
			Snippet snippet = runParserGetSnippet(query, endPoint);

			final int numErrors = snippet.getFatalErrorCount();
			Assert.assertTrue("Expected failures with " + query + " but got " + snippet.getFatalErrorStringList(), 
				numErrors >= 1);
	
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

		accessObject.executeTheParse(query, endPoint);

		Snippet snippet = accessObject.getSnippet();

		System.out.println("Result: " + snippet.getSqlAbstractTree());
		System.out.println("Result JSON: " + snippet.getSqlAbstractTreeJson());
		System.out.println("Interface: " + snippet.getQueryInterface());
		System.out.println("Symbol Tree: " + snippet.getSymbolTable());
		System.out.println("Table Dictionary: " + snippet.getTableDictionary());
		System.out.println("Substitution Variables: " + snippet.getSubstitutionsMap());
		System.out.println("Fatal Errors: " + snippet.getFatalErrorStringList());
		
		return snippet;
	}

}

