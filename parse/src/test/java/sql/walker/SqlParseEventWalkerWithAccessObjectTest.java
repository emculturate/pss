package sql.walker;

import org.antlr.v4.runtime.RecognitionException;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import access.Snippet;
import access.SqlParserAccess;
import static mumble.SQLParserEndPoints.SQLPARSER_INSERT_TREE_KEY;

public class SqlParseEventWalkerWithAccessObjectTest {

   
	@Test
	public void basicInsertFromQueryTest() {
		final String query = "insert into tab1 select a,b from tab2";
        final Snippet snippet = runSQLParsertest(query);

		
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
        final Snippet snippet = runSQLParsertest(query);

		
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
        final Snippet snippet = runSQLParsertest(query);

		
		Assert.assertEquals("AST is wrong", "{INSERT={preamble=insert_into, values={matrix={1={row={1={literal=1}, 2={literal=2}, 3={literal=3}}}, 2={row={1={literal=2}, 2={literal=3}, 3={literal=4}}}}}, table={table={alias=null, table=tab1}}}}",
        	snippet.getSqlAbstractTree().toString());
		Assert.assertEquals("AST JSON is wrong", "{\"INSERT\":{\"preamble\":\"insert_into\",\"values\":{\"matrix\":{\"1\":{\"row\":{\"1\":{\"literal\":\"1\"},\"2\":{\"literal\":\"2\"},\"3\":{\"literal\":\"3\"}}},\"2\":{\"row\":{\"1\":{\"literal\":\"2\"},\"2\":{\"literal\":\"3\"},\"3\":{\"literal\":\"4\"}}}}},\"table\":{\"table\":{\"table\":\"tab1\"}}}}",
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
	public void basicInsertWithColumnsFromQueryTest() {
		final String query = "insert into tab1 (c ,d) select a,b from tab2";
        final Snippet snippet = runSQLParsertest(query);

		
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
        final Snippet snippet = runSQLParsertest(query);

		
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
        final Snippet snippet = runSQLParsertest(query);

		
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

		final Snippet snippet = runSQLParsertest(query);

		
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
	private Snippet runSQLParsertest(final String query) {
		try {
			System.out.println();
			// There should be zero errors
			SqlParserAccess accessObject = new SqlParserAccess(true, true, true);

			accessObject.executeTheParse(query, SQLPARSER_INSERT_TREE_KEY);

            Snippet snippet = accessObject.getSnippet();

			System.out.println("Result: " + snippet.getSqlAbstractTree());
			System.out.println("Result JSON: " + snippet.getSqlAbstractTreeJson());
			System.out.println("Interface: " + snippet.getQueryInterface());
			System.out.println("Symbol Tree: " + snippet.getSymbolTable());
			System.out.println("Table Dictionary: " + snippet.getTableDictionary());
			System.out.println("Substitution Variables: " + snippet.getSubstitutionsMap());


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


}

