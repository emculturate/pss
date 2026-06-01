package sql.walker;
import org.junit.Assert;
import org.junit.Test;

import access.Snippet;
import errorhandling.ParseDiagnostic;
import sql.SQLSelectParserParser;

public class SqlEventWalkerCoreSelectFromAliasingTests extends AbstractSqlParseEventWalkerTest {

	@Test
	public void concatenationFormulaTest() {
		//ITEM 24 - the concatenated elements work when in parentheses, otherwise grammar is indeterminate
		// Adding syntax trapping to the Event Walker allows us to report the location of the problem to the user
		// Hence the original formulation of this error where the second element of the concatenation was not surrounded by parentheses 
		// is NOT a valid format and therefore does not need to be tested. Have modified this test into its correct syntactic format
		// instead.
		final String query = "SELECT substr(strm, 1, 2) || (substr(strm, 3, 1) + 1) || substr(strm, 4,1)"
				+ " from tab1";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={concatenate={1={function={parameters={1={column={name=strm, table_ref=null}}, 2={literal=1}, 3={literal=2}}, function_name=substr}}, 2={parentheses={calc={left={function={parameters={1={column={name=strm, table_ref=null}}, 2={literal=3}, 3={literal=1}}, function_name=substr}}, right={literal=1}, operator=+}}}, 3={function={parameters={1={column={name=strm, table_ref=null}}, 2={literal=4}, 3={literal=1}}, function_name=substr}}}}}, from={table={alias=null, table=tab1}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[unnamed_0]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={strm=[[@3,14:17='strm',<381>,1:14], [@13,37:40='strm',<381>,1:37], [@25,64:67='strm',<381>,1:64]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={unnamed_0=[[@30,73:73=')',<288>,1:73]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={query_dictionary={unnamed_0=[[@30,73:73=')',<288>,1:73]]}, table_dictionary={tab1={strm=[[@3,14:17='strm',<381>,1:14], [@13,37:40='strm',<381>,1:37], [@25,64:67='strm',<381>,1:64]]}}, interface={unnamed_0=[{name=strm, table_ref=tab1}]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void basicSelectList1Test() {
		final String query = " SELECT * FROM tab1"; 

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=*, table_ref=*}}}, from={table={alias=null, table=tab1}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={*=[[@1,8:8='*',<291>,1:8]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@1,8:8='*',<291>,1:8]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={query_dictionary={*=[[@1,8:8='*',<291>,1:8]]}, table_dictionary={tab1={*=[[@1,8:8='*',<291>,1:8]]}}, interface={*=[{name=*, table_ref=*}]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void basicSelectList2Test() {
		final String query = " SELECT a,b,c FROM tab1"; 

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=a, table_ref=null}}, 2={column={name=b, table_ref=null}}, 3={column={name=c, table_ref=null}}}, from={table={alias=null, table=tab1}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[a, b, c]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={a=[[@1,8:8='a',<381>,1:8]], b=[[@3,10:10='b',<381>,1:10]], c=[[@5,12:12='c',<381>,1:12]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={a=[[@1,8:8='a',<381>,1:8]], b=[[@3,10:10='b',<381>,1:10]], c=[[@5,12:12='c',<381>,1:12]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={query_dictionary={a=[[@1,8:8='a',<381>,1:8]], b=[[@3,10:10='b',<381>,1:10]], c=[[@5,12:12='c',<381>,1:12]]}, table_dictionary={tab1={a=[[@1,8:8='a',<381>,1:8]], b=[[@3,10:10='b',<381>,1:10]], c=[[@5,12:12='c',<381>,1:12]]}}, interface={a=[{name=a, table_ref=tab1}], b=[{name=b, table_ref=tab1}], c=[{name=c, table_ref=tab1}]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void basicSelectList3Test() {
		final String query = " SELECT 1 + 2 as a,(1+2) b, (d) as c FROM tab1"; 

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=a, calc={left={literal=1}, right={literal=2}, operator=+}}, 2={parentheses={calc={left={literal=1}, right={literal=2}, operator=+}}, alias=b}, 3={parentheses={column={name=d, table_ref=null}}, alias=c}}, from={table={alias=null, table=tab1}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[a, b, c]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={d=[[@15,29:29='d',<381>,1:29]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={a=[[@5,17:17='a',<381>,1:17]], b=[[@12,25:25='b',<381>,1:25]], c=[[@18,35:35='c',<381>,1:35]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={query_dictionary={a=[[@5,17:17='a',<381>,1:17]], b=[[@12,25:25='b',<381>,1:25]], c=[[@18,35:35='c',<381>,1:35]]}, table_dictionary={tab1={d=[[@15,29:29='d',<381>,1:29]]}}, interface={a=[], b=[], c=[{name=d, table_ref=tab1}]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void basicSelectTableNameV1Test() {
		final String query = " SELECT * FROM tab1"; 

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=*, table_ref=*}}}, from={table={alias=null, table=tab1}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={*=[[@1,8:8='*',<291>,1:8]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@1,8:8='*',<291>,1:8]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={query_dictionary={*=[[@1,8:8='*',<291>,1:8]]}, table_dictionary={tab1={*=[[@1,8:8='*',<291>,1:8]]}}, interface={*=[{name=*, table_ref=*}]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void basicSelectTableNameV2Test() {
		final String query = " SELECT * FROM schema.tab1"; 

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=*, table_ref=*}}}, from={table={schema=schema, alias=null, table=tab1}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{schema.tab1={*=[[@1,8:8='*',<291>,1:8]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@1,8:8='*',<291>,1:8]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={query_dictionary={*=[[@1,8:8='*',<291>,1:8]]}, table_dictionary={schema.tab1={*=[[@1,8:8='*',<291>,1:8]]}}, interface={*=[{name=*, table_ref=*}]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void basicSelectTableNameV3Test() {
		final String query = " SELECT * FROM dbname.schema.tab1"; 

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=*, table_ref=*}}}, from={table={schema=schema, dbname=dbname, alias=null, table=tab1}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{dbname.schema.tab1={*=[[@1,8:8='*',<291>,1:8]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@1,8:8='*',<291>,1:8]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={query_dictionary={*=[[@1,8:8='*',<291>,1:8]]}, table_dictionary={dbname.schema.tab1={*=[[@1,8:8='*',<291>,1:8]]}}, interface={*=[{name=*, table_ref=*}]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void basicSelectQuotedTableNameV1Test() {
		final String query = " SELECT * FROM \"PROD-9384e59c-5236-4842-aae8-4ae7a89e4fae\".panto.\"667_7460\""; 

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=*, table_ref=*}}}, from={table={schema=panto, dbname=\"PROD-9384e59c-5236-4842-aae8-4ae7a89e4fae\", alias=null, table=\"667_7460\"}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{\"PROD-9384e59c-5236-4842-aae8-4ae7a89e4fae\".panto.\"667_7460\"={*=[[@1,8:8='*',<291>,1:8]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@1,8:8='*',<291>,1:8]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={query_dictionary={*=[[@1,8:8='*',<291>,1:8]]}, table_dictionary={\"PROD-9384e59c-5236-4842-aae8-4ae7a89e4fae\".panto.\"667_7460\"={*=[[@1,8:8='*',<291>,1:8]]}}, interface={*=[{name=*, table_ref=*}]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void basicSelectDistinctQualifierListTest() {
		final String query = " SELECT distinct a,b,c FROM tab1"; 

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=a, table_ref=null}}, 2={column={name=b, table_ref=null}}, 3={column={name=c, table_ref=null}}}, qualifier=distinct, from={table={alias=null, table=tab1}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[a, b, c]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={a=[[@2,17:17='a',<381>,1:17]], b=[[@4,19:19='b',<381>,1:19]], c=[[@6,21:21='c',<381>,1:21]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={a=[[@2,17:17='a',<381>,1:17]], b=[[@4,19:19='b',<381>,1:19]], c=[[@6,21:21='c',<381>,1:21]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={query_dictionary={a=[[@2,17:17='a',<381>,1:17]], b=[[@4,19:19='b',<381>,1:19]], c=[[@6,21:21='c',<381>,1:21]]}, table_dictionary={tab1={a=[[@2,17:17='a',<381>,1:17]], b=[[@4,19:19='b',<381>,1:19]], c=[[@6,21:21='c',<381>,1:21]]}}, interface={a=[{name=a, table_ref=tab1}], b=[{name=b, table_ref=tab1}], c=[{name=c, table_ref=tab1}]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void basicSelectDistinctListWithEmbeddedAllListQualifierTest() {
		final String query = " SELECT distinct a,b,c FROM (select all b,c from tab2) tab1"; 

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=a, table_ref=null}}, 2={column={name=b, table_ref=null}}, 3={column={name=c, table_ref=null}}}, qualifier=distinct, from={table={alias=tab1, query={select={1={column={name=b, table_ref=null}}, 2={column={name=c, table_ref=null}}}, qualifier=all, from={table={alias=null, table=tab2}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[a, b, c]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab2={b=[[@11,40:40='b',<381>,1:40]], c=[[@13,42:42='c',<381>,1:42]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={b=[[@11,40:40='b',<381>,1:40]], c=[[@13,42:42='c',<381>,1:42]]}, query1={a=[[@2,17:17='a',<381>,1:17]], b=[[@4,19:19='b',<381>,1:19]], c=[[@6,21:21='c',<381>,1:21]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query1={query_dictionary={a=[[@2,17:17='a',<381>,1:17]], b=[[@4,19:19='b',<381>,1:19]], c=[[@6,21:21='c',<381>,1:21]]}, table_dictionary={}, def_query0={query_dictionary={b=[[@11,40:40='b',<381>,1:40]], c=[[@13,42:42='c',<381>,1:42]]}, table_dictionary={tab2={b=[[@11,40:40='b',<381>,1:40]], c=[[@13,42:42='c',<381>,1:42]]}}, interface={b=[{name=b, table_ref=tab2}], c=[{name=c, table_ref=tab2}]}}, interface={a=[{name=a, table_ref=null}], b=[{name=b, table_ref=query0}], c=[{name=c, table_ref=query0}]}, table_alias={tab1=query0}}}",
				extractor.getSymbolTable().toString());
				
		Snippet snippet = extractor.getSnippet();
		Assert.assertEquals("Expected no fatal errors but got: " + snippet.getFatalErrorStringList(), 
				0, snippet.getFatalErrorStringList().size());
		assertDiagnosticCountBySeverity(snippet, "UNRESOLVED_UNQUALIFIED_COLUMNS", ParseDiagnostic.Severity.ERROR, null, "a", 1);
		}


	@Test
	public void basicSelectListAliasing1Test() {
		final String query = " SELECT a as x,b as y,c as z FROM tab1"; 

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=a, table_ref=null}, alias=x}, 2={column={name=b, table_ref=null}, alias=y}, 3={column={name=c, table_ref=null}, alias=z}}, from={table={alias=null, table=tab1}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[x, y, z]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={a=[[@1,8:8='a',<381>,1:8]], b=[[@5,15:15='b',<381>,1:15]], c=[[@9,22:22='c',<381>,1:22]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={x=[[@3,13:13='x',<381>,1:13]], y=[[@7,20:20='y',<381>,1:20]], z=[[@11,27:27='z',<381>,1:27]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={query_dictionary={x=[[@3,13:13='x',<381>,1:13]], y=[[@7,20:20='y',<381>,1:20]], z=[[@11,27:27='z',<381>,1:27]]}, table_dictionary={tab1={a=[[@1,8:8='a',<381>,1:8]], b=[[@5,15:15='b',<381>,1:15]], c=[[@9,22:22='c',<381>,1:22]]}}, interface={x=[{name=a, table_ref=tab1}], y=[{name=b, table_ref=tab1}], z=[{name=c, table_ref=tab1}]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void basicSelectListNumericPrefixAliasingTest() {
		final String query = " SELECT a as 01_x,b as 02_y,c as 999_z FROM tab1"; 

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=a, table_ref=null}, alias=01_x}, 2={column={name=b, table_ref=null}, alias=02_y}, 3={column={name=c, table_ref=null}, alias=999_z}}, from={table={alias=null, table=tab1}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[02_y, 999_z, 01_x]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={a=[[@1,8:8='a',<381>,1:8]], b=[[@5,18:18='b',<381>,1:18]], c=[[@9,28:28='c',<381>,1:28]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={02_y=[[@7,23:26='02_y',<384>,1:23]], 999_z=[[@11,33:37='999_z',<384>,1:33]], 01_x=[[@3,13:16='01_x',<384>,1:13]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={query_dictionary={02_y=[[@7,23:26='02_y',<384>,1:23]], 999_z=[[@11,33:37='999_z',<384>,1:33]], 01_x=[[@3,13:16='01_x',<384>,1:13]]}, table_dictionary={tab1={a=[[@1,8:8='a',<381>,1:8]], b=[[@5,18:18='b',<381>,1:18]], c=[[@9,28:28='c',<381>,1:28]]}}, interface={02_y=[{name=b, table_ref=tab1}], 999_z=[{name=c, table_ref=tab1}], 01_x=[{name=a, table_ref=tab1}]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void basicSelectListQuotedNumericPrefixColumnTest() {
		final String query = " SELECT \"09_a\" as 01_x, \"22_b\" as 02_y,\"36_c\" as \"999_z\" FROM \"99tab1\""; 

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=\"09_a\", table_ref=null}, alias=01_x}, 2={column={name=\"22_b\", table_ref=null}, alias=02_y}, 3={column={name=\"36_c\", table_ref=null}, alias=\"999_z\"}}, from={table={alias=null, table=\"99tab1\"}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[\"999_z\", 02_y, 01_x]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{\"99tab1\"={\"22_b\"=[[@5,24:29='\"22_b\"',<385>,1:24]], \"09_a\"=[[@1,8:13='\"09_a\"',<385>,1:8]], \"36_c\"=[[@9,39:44='\"36_c\"',<385>,1:39]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={\"999_z\"=[[@11,49:55='\"999_z\"',<385>,1:49]], 02_y=[[@7,34:37='02_y',<384>,1:34]], 01_x=[[@3,18:21='01_x',<384>,1:18]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={query_dictionary={\"999_z\"=[[@11,49:55='\"999_z\"',<385>,1:49]], 02_y=[[@7,34:37='02_y',<384>,1:34]], 01_x=[[@3,18:21='01_x',<384>,1:18]]}, table_dictionary={\"99tab1\"={\"22_b\"=[[@5,24:29='\"22_b\"',<385>,1:24]], \"09_a\"=[[@1,8:13='\"09_a\"',<385>,1:8]], \"36_c\"=[[@9,39:44='\"36_c\"',<385>,1:39]]}}, interface={\"999_z\"=[{name=\"36_c\", table_ref=\"99tab1\"}], 02_y=[{name=\"22_b\", table_ref=\"99tab1\"}], 01_x=[{name=\"09_a\", table_ref=\"99tab1\"}]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void real1SelectListNumericPrefixAliasingTest() {
		final String query = "SELECT sub.Degree_Code AS 01_DEGREE_CD, sub.Degree_Name AS 02_DEGREE_NAME, "
		+ "sub.f1 FROM (SELECT t.f1, t.* FROM pantoresultprod.hive_result_pit_5223_164728_46090704 t) sub"; 

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=Degree_Code, table_ref=sub}, alias=01_DEGREE_CD}, 2={column={name=Degree_Name, table_ref=sub}, alias=02_DEGREE_NAME}, 3={column={name=f1, table_ref=sub}}}, from={table={alias=sub, query={select={1={column={name=f1, table_ref=t}}, 2={column={name=*, table_ref=t}}}, from={table={schema=pantoresultprod, alias=t, table=hive_result_pit_5223_164728_46090704}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[02_DEGREE_NAME, f1, 01_DEGREE_CD]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{pantoresultprod.hive_result_pit_5223_164728_46090704={*=[[@23,101:101='t',<381>,1:101]], f1=[[@19,95:95='t',<381>,1:95]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={Degree_Code=[[@1,7:9='sub',<381>,1:7]], Degree_Name=[[@7,40:42='sub',<381>,1:40]], *=[[@25,103:103='*',<291>,1:103]], f1=[[@21,97:98='f1',<381>,1:97], [@13,75:77='sub',<381>,1:75]]}, query1={02_DEGREE_NAME=[[@11,59:72='02_DEGREE_NAME',<384>,1:59]], f1=[[@15,79:80='f1',<381>,1:79]], 01_DEGREE_CD=[[@5,26:37='01_DEGREE_CD',<384>,1:26]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query1={query_dictionary={02_DEGREE_NAME=[[@11,59:72='02_DEGREE_NAME',<384>,1:59]], f1=[[@15,79:80='f1',<381>,1:79]], 01_DEGREE_CD=[[@5,26:37='01_DEGREE_CD',<384>,1:26]]}, table_dictionary={}, def_query0={query_dictionary={Degree_Code=[[@1,7:9='sub',<381>,1:7]], Degree_Name=[[@7,40:42='sub',<381>,1:40]], *=[[@25,103:103='*',<291>,1:103]], f1=[[@21,97:98='f1',<381>,1:97], [@13,75:77='sub',<381>,1:75]]}, table_dictionary={pantoresultprod.hive_result_pit_5223_164728_46090704={*=[[@23,101:101='t',<381>,1:101]], f1=[[@19,95:95='t',<381>,1:95]]}}, interface={*=[{name=*, table_ref=t}], f1=[{name=f1, table_ref=t}]}, table_alias={t=pantoresultprod.hive_result_pit_5223_164728_46090704}}, interface={02_DEGREE_NAME=[{name=Degree_Name, table_ref=sub}], f1=[{name=f1, table_ref=sub}], 01_DEGREE_CD=[{name=Degree_Code, table_ref=sub}]}, table_alias={sub=query0}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void nestedQueryDemoTest() {
		// 3 scalar queries and 1 join subquery
		// Good Predicand, Inlist and subquery tests for fixing the symbol table collection
		final String query = "select tab1.a aa, (select max(D) from ee) max_D, (select min(D) from ee) min_D,  kk.w" 
				+ " from tab1  join (select w from jj) kk where a in (select c from ff)";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=a, table_ref=tab1}, alias=aa}, 2={lookup={from={table={alias=null, table=ee}}, select={1={function={function_name=max, qualifier=null, parameters={column={name=D, table_ref=null}}}}}, query=query0}, alias=max_D}, 3={lookup={from={table={alias=null, table=ee}}, select={1={function={function_name=min, qualifier=null, parameters={column={name=D, table_ref=null}}}}}, query=query2}, alias=min_D}, 4={column={name=w, table_ref=kk}}}, from={join={1={table={alias=null, table=tab1}}, 2={join=join}, 3={table={alias=kk, query={select={1={column={name=w, table_ref=null}}}, from={table={alias=null, table=jj}}}}}}}, where={in={item={column={name=a, table_ref=null}}, in_list={select={1={column={name=c, table_ref=null}}}, from={table={alias=null, table=ff}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[aa, max_D, min_D, w]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{ee={D=[[@10,30:30='D',<381>,1:30], [@21,61:61='D',<381>,1:61]]}, jj={w=[[@36,110:110='w',<381>,1:110]]}, ff={c=[[@46,143:143='c',<381>,1:143]]}, tab1={a=[[@1,7:10='tab1',<381>,1:7], [@42,130:130='a',<381>,1:130]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query4={w=[[@36,110:110='w',<381>,1:110], [@28,81:82='kk',<381>,1:81]]}, query5={c=[[@46,143:143='c',<381>,1:143]]}, query7={aa=[[@4,14:15='aa',<381>,1:14]], max_D=[[@15,42:46='max_D',<381>,1:42]], min_D=[[@26,73:77='min_D',<381>,1:73]], w=[[@30,84:84='w',<381>,1:84]]}, query0={unnamed_0=[[@11,31:31=')',<288>,1:31]]}, query2={unnamed_1=[[@22,62:62=')',<288>,1:62]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query7={query_dictionary={aa=[[@4,14:15='aa',<381>,1:14]], max_D=[[@15,42:46='max_D',<381>,1:42]], min_D=[[@26,73:77='min_D',<381>,1:73]], w=[[@30,84:84='w',<381>,1:84]]}, table_dictionary={tab1={a=[[@1,7:10='tab1',<381>,1:7], [@42,130:130='a',<381>,1:130]]}}, def_query0={query_dictionary={unnamed_0=[[@11,31:31=')',<288>,1:31]]}, table_dictionary={ee={D=[[@10,30:30='D',<381>,1:30], [@21,61:61='D',<381>,1:61]]}}, interface={unnamed_0=[{name=D, table_ref=ee}]}}, filters=[{name=a, table_ref=tab1}, {query=query5}], def_query5={query_dictionary={c=[[@46,143:143='c',<381>,1:143]]}, table_dictionary={ff={c=[[@46,143:143='c',<381>,1:143]]}}, interface={c=[{name=c, table_ref=ff}]}}, interface={aa=[{name=a, table_ref=tab1}], max_D=[{query=query0}], min_D=[{query=query2}], w=[{name=w, table_ref=kk}]}, def_query4={query_dictionary={w=[[@36,110:110='w',<381>,1:110], [@28,81:82='kk',<381>,1:81]]}, table_dictionary={jj={w=[[@36,110:110='w',<381>,1:110]]}}, interface={w=[{name=w, table_ref=jj}]}}, table_alias={kk=query4}, def_query2={query_dictionary={unnamed_1=[[@22,62:62=')',<288>,1:62]]}, table_dictionary={ee={D=[[@21,61:61='D',<381>,1:61]]}}, interface={unnamed_1=[{name=D, table_ref=ee}]}}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void real2SelectListNumericPrefixAliasingTest() {
		final String query = "SELECT sub.College_Code AS 01_College_Cd, sub.College_Name AS 02_College_Name "
		+" FROM (SELECT t.* FROM pantoresultprod.hive_result_pit_6875_220752_46090864 t) sub"; 

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=College_Code, table_ref=sub}, alias=01_College_Cd}, 2={column={name=College_Name, table_ref=sub}, alias=02_College_Name}}, from={table={alias=sub, query={select={1={column={name=*, table_ref=t}}}, from={table={schema=pantoresultprod, alias=t, table=hive_result_pit_6875_220752_46090864}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[02_College_Name, 01_College_Cd]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{pantoresultprod.hive_result_pit_6875_220752_46090864={*=[[@15,92:92='t',<381>,1:92]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={College_Name=[[@7,42:44='sub',<381>,1:42]], *=[[@17,94:94='*',<291>,1:94]], College_Code=[[@1,7:9='sub',<381>,1:7]]}, query1={02_College_Name=[[@11,62:76='02_College_Name',<384>,1:62]], 01_College_Cd=[[@5,27:39='01_College_Cd',<384>,1:27]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query1={query_dictionary={02_College_Name=[[@11,62:76='02_College_Name',<384>,1:62]], 01_College_Cd=[[@5,27:39='01_College_Cd',<384>,1:27]]}, table_dictionary={}, def_query0={query_dictionary={College_Name=[[@7,42:44='sub',<381>,1:42]], *=[[@17,94:94='*',<291>,1:94]], College_Code=[[@1,7:9='sub',<381>,1:7]]}, table_dictionary={pantoresultprod.hive_result_pit_6875_220752_46090864={*=[[@15,92:92='t',<381>,1:92]]}}, interface={*=[{name=*, table_ref=t}]}, table_alias={t=pantoresultprod.hive_result_pit_6875_220752_46090864}}, interface={02_College_Name=[{name=College_Name, table_ref=sub}], 01_College_Cd=[{name=College_Code, table_ref=sub}]}, table_alias={sub=query0}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void real3SelectListNumericPrefixAliasingTest() {
		final String query = "SELECT sub.Course_Registration_Code AS 01_COURSE_REGISTRATION_CD, "
		+" sub.Course_Registration_Description AS 02_COURSE_REGISTRATION_DESC FROM "
		+" (SELECT t.* FROM pantoresultprod.hive_result_pit_5223_164727_46090703 t) sub"; 

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=Course_Registration_Code, table_ref=sub}, alias=01_COURSE_REGISTRATION_CD}, 2={column={name=Course_Registration_Description, table_ref=sub}, alias=02_COURSE_REGISTRATION_DESC}}, from={table={alias=sub, query={select={1={column={name=*, table_ref=t}}}, from={table={schema=pantoresultprod, alias=t, table=hive_result_pit_5223_164727_46090703}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[02_COURSE_REGISTRATION_DESC, 01_COURSE_REGISTRATION_CD]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{pantoresultprod.hive_result_pit_5223_164727_46090703={*=[[@15,148:148='t',<381>,1:148]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={Course_Registration_Description=[[@7,67:69='sub',<381>,1:67]], *=[[@17,150:150='*',<291>,1:150]], Course_Registration_Code=[[@1,7:9='sub',<381>,1:7]]}, query1={02_COURSE_REGISTRATION_DESC=[[@11,106:132='02_COURSE_REGISTRATION_DESC',<384>,1:106]], 01_COURSE_REGISTRATION_CD=[[@5,39:63='01_COURSE_REGISTRATION_CD',<384>,1:39]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query1={query_dictionary={02_COURSE_REGISTRATION_DESC=[[@11,106:132='02_COURSE_REGISTRATION_DESC',<384>,1:106]], 01_COURSE_REGISTRATION_CD=[[@5,39:63='01_COURSE_REGISTRATION_CD',<384>,1:39]]}, table_dictionary={}, def_query0={query_dictionary={Course_Registration_Description=[[@7,67:69='sub',<381>,1:67]], *=[[@17,150:150='*',<291>,1:150]], Course_Registration_Code=[[@1,7:9='sub',<381>,1:7]]}, table_dictionary={pantoresultprod.hive_result_pit_5223_164727_46090703={*=[[@15,148:148='t',<381>,1:148]]}}, interface={*=[{name=*, table_ref=t}]}, table_alias={t=pantoresultprod.hive_result_pit_5223_164727_46090703}}, interface={02_COURSE_REGISTRATION_DESC=[{name=Course_Registration_Description, table_ref=sub}], 01_COURSE_REGISTRATION_CD=[{name=Course_Registration_Code, table_ref=sub}]}, table_alias={sub=query0}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void real4SelectListNumericPrefixAliasingTest() {
		final String query = "SELECT Course_Registration_Code AS 01_COURSE_REGISTRATION_CD, "
		+" Course_Registration_Description AS 02_COURSE_REGISTRATION_DESC FROM "
		+" (SELECT t.* FROM pantoresultprod.hive_result_pit_5223_164727_46090703 t) sub"; 

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=Course_Registration_Code, table_ref=null}, alias=01_COURSE_REGISTRATION_CD}, 2={column={name=Course_Registration_Description, table_ref=null}, alias=02_COURSE_REGISTRATION_DESC}}, from={table={alias=sub, query={select={1={column={name=*, table_ref=t}}}, from={table={schema=pantoresultprod, alias=t, table=hive_result_pit_5223_164727_46090703}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[02_COURSE_REGISTRATION_DESC, 01_COURSE_REGISTRATION_CD]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{pantoresultprod.hive_result_pit_5223_164727_46090703={*=[[@11,140:140='t',<381>,1:140]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@13,142:142='*',<291>,1:142]]}, query1={02_COURSE_REGISTRATION_DESC=[[@7,98:124='02_COURSE_REGISTRATION_DESC',<384>,1:98]], 01_COURSE_REGISTRATION_CD=[[@3,35:59='01_COURSE_REGISTRATION_CD',<384>,1:35]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query1={query_dictionary={02_COURSE_REGISTRATION_DESC=[[@7,98:124='02_COURSE_REGISTRATION_DESC',<384>,1:98]], 01_COURSE_REGISTRATION_CD=[[@3,35:59='01_COURSE_REGISTRATION_CD',<384>,1:35]]}, table_dictionary={}, def_query0={query_dictionary={*=[[@13,142:142='*',<291>,1:142]]}, table_dictionary={pantoresultprod.hive_result_pit_5223_164727_46090703={*=[[@11,140:140='t',<381>,1:140]]}}, interface={*=[{name=*, table_ref=t}]}, table_alias={t=pantoresultprod.hive_result_pit_5223_164727_46090703}}, interface={02_COURSE_REGISTRATION_DESC=[{name=Course_Registration_Description, table_ref=query0}], 01_COURSE_REGISTRATION_CD=[{name=Course_Registration_Code, table_ref=query0}]}, table_alias={sub=query0}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void simpleVariableName1Test() {
		final String query = " SELECT a.<simple>, a.<with blanks in name> FROM tab1 as a"; 

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={substitution={name=<simple>, type=column}, table_ref=a}}, 2={column={substitution={name=<with blanks in name>, type=column}, table_ref=a}}}, from={table={alias=a, table=tab1}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[<with blanks in name>, <simple>]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<with blanks in name>=column, <simple>=column}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={<with blanks in name>=[[@5,20:20='a',<381>,1:20]], <simple>=[[@1,8:8='a',<381>,1:8]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={<with blanks in name>=[[@7,22:42='<with blanks in name>',<327>,1:22]], <simple>=[[@3,10:17='<simple>',<327>,1:10]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={query_dictionary={<with blanks in name>=[[@7,22:42='<with blanks in name>',<327>,1:22]], <simple>=[[@3,10:17='<simple>',<327>,1:10]]}, table_dictionary={tab1={<with blanks in name>=[[@5,20:20='a',<381>,1:20]], <simple>=[[@1,8:8='a',<381>,1:8]]}}, interface={<with blanks in name>=[{substitution={name=<with blanks in name>, type=column}, table_ref=a}], <simple>=[{substitution={name=<simple>, type=column}, table_ref=a}]}, table_alias={a=tab1}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void simpleVariableNameWithDotTest() {
		final String query = " SELECT a.<simple>, a.<with.dots.in.name> FROM tab1 as a"; 

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={substitution={name=<simple>, type=column}, table_ref=a}}, 2={column={substitution={name=<with.dots.in.name>, type=column}, table_ref=a}}}, from={table={alias=a, table=tab1}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[<simple>, <with.dots.in.name>]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<simple>=column, <with.dots.in.name>=column}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={<simple>=[[@1,8:8='a',<381>,1:8]], <with.dots.in.name>=[[@5,20:20='a',<381>,1:20]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={<simple>=[[@3,10:17='<simple>',<327>,1:10]], <with.dots.in.name>=[[@7,22:40='<with.dots.in.name>',<327>,1:22]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={query_dictionary={<simple>=[[@3,10:17='<simple>',<327>,1:10]], <with.dots.in.name>=[[@7,22:40='<with.dots.in.name>',<327>,1:22]]}, table_dictionary={tab1={<simple>=[[@1,8:8='a',<381>,1:8]]}}, interface={<simple>=[{substitution={name=<simple>, type=column}, table_ref=a}], <with.dots.in.name>=[{substitution={name=<with.dots.in.name>, type=column}, table_ref=a}]}, table_alias={a=tab1}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void simpleVariableNameWithDashTest() {
		final String query = " SELECT a.<simple>, a.<with-dash-in - name> FROM tab1 as a"; 

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={substitution={name=<simple>, type=column}, table_ref=a}}, 2={column={substitution={name=<with-dash-in - name>, type=column}, table_ref=a}}}, from={table={alias=a, table=tab1}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[<simple>, <with-dash-in - name>]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<simple>=column, <with-dash-in - name>=column}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={<simple>=[[@1,8:8='a',<381>,1:8]], <with-dash-in - name>=[[@5,20:20='a',<381>,1:20]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={<simple>=[[@3,10:17='<simple>',<327>,1:10]], <with-dash-in - name>=[[@7,22:42='<with-dash-in - name>',<327>,1:22]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={query_dictionary={<simple>=[[@3,10:17='<simple>',<327>,1:10]], <with-dash-in - name>=[[@7,22:42='<with-dash-in - name>',<327>,1:22]]}, table_dictionary={tab1={<simple>=[[@1,8:8='a',<381>,1:8]], <with-dash-in - name>=[[@5,20:20='a',<381>,1:20]]}}, interface={<simple>=[{substitution={name=<simple>, type=column}, table_ref=a}], <with-dash-in - name>=[{substitution={name=<with-dash-in - name>, type=column}, table_ref=a}]}, table_alias={a=tab1}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void extendedVariableName1Test() {
		final String query = " SELECT a.<[simple]>, a.<[DOMAIN].[ENTITY].[ATTRIBUTE]>, a.<[another].[item]> FROM <[DOMAIN].[ENTITY]>  as a "; 

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={substitution={name=<[simple]>, parts={1=[simple]}, type=column}, table_ref=a}}, 2={column={substitution={name=<[DOMAIN].[ENTITY].[ATTRIBUTE]>, parts={1=[DOMAIN], 2=[ENTITY], 3=[ATTRIBUTE]}, type=column}, table_ref=a}}, 3={column={substitution={name=<[another].[item]>, parts={1=[another], 2=[item]}, type=column}, table_ref=a}}}, from={table={alias=a, substitution={name=<[DOMAIN].[ENTITY]>, parts={1=[DOMAIN], 2=[ENTITY]}, type=tuple}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[<[another].[item]>, <[DOMAIN].[ENTITY].[ATTRIBUTE]>, <[simple]>]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<[another].[item]>=column, <[DOMAIN].[ENTITY].[ATTRIBUTE]>=column, <[DOMAIN].[ENTITY]>=tuple, <[simple]>=column}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{<[DOMAIN].[ENTITY]>={<[another].[item]>=[[@9,57:57='a',<381>,1:57]], <[DOMAIN].[ENTITY].[ATTRIBUTE]>=[[@5,22:22='a',<381>,1:22]], <[simple]>=[[@1,8:8='a',<381>,1:8]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={<[another].[item]>=[[@11,59:76='<[another].[item]>',<328>,1:59]], <[DOMAIN].[ENTITY].[ATTRIBUTE]>=[[@7,24:54='<[DOMAIN].[ENTITY].[ATTRIBUTE]>',<328>,1:24]], <[simple]>=[[@3,10:19='<[simple]>',<328>,1:10]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={query_dictionary={<[another].[item]>=[[@11,59:76='<[another].[item]>',<328>,1:59]], <[DOMAIN].[ENTITY].[ATTRIBUTE]>=[[@7,24:54='<[DOMAIN].[ENTITY].[ATTRIBUTE]>',<328>,1:24]], <[simple]>=[[@3,10:19='<[simple]>',<328>,1:10]]}, table_dictionary={<[DOMAIN].[ENTITY]>={<[simple]>=[[@1,8:8='a',<381>,1:8]]}}, interface={<[another].[item]>=[{substitution={name=<[another].[item]>, parts={1=[another], 2=[item]}, type=column}, table_ref=a}], <[DOMAIN].[ENTITY].[ATTRIBUTE]>=[{substitution={name=<[DOMAIN].[ENTITY].[ATTRIBUTE]>, parts={1=[DOMAIN], 2=[ENTITY], 3=[ATTRIBUTE]}, type=column}, table_ref=a}], <[simple]>=[{substitution={name=<[simple]>, parts={1=[simple]}, type=column}, table_ref=a}]}, table_alias={a=<[DOMAIN].[ENTITY]>}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void extendedVariableNameWithDots2Test() {
		final String query = " SELECT  a.<[PREFIX.DOMAIN.SUFFIX].[ENTITY.SUFFIX].[Prefix.ATTRIBUTE]> FROM <[DOMAIN].[ENTITY]>  as a "; 

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={substitution={name=<[PREFIX.DOMAIN.SUFFIX].[ENTITY.SUFFIX].[Prefix.ATTRIBUTE]>, parts={1=[PREFIX.DOMAIN.SUFFIX].[ENTITY.SUFFIX].[Prefix.ATTRIBUTE]}, type=column}, table_ref=a}}}, from={table={alias=a, substitution={name=<[DOMAIN].[ENTITY]>, parts={1=[DOMAIN], 2=[ENTITY]}, type=tuple}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[<[PREFIX.DOMAIN.SUFFIX].[ENTITY.SUFFIX].[Prefix.ATTRIBUTE]>]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<[DOMAIN].[ENTITY]>=tuple, <[PREFIX.DOMAIN.SUFFIX].[ENTITY.SUFFIX].[Prefix.ATTRIBUTE]>=column}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{<[DOMAIN].[ENTITY]>={<[PREFIX.DOMAIN.SUFFIX].[ENTITY.SUFFIX].[Prefix.ATTRIBUTE]>=[[@1,9:9='a',<381>,1:9]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={<[PREFIX.DOMAIN.SUFFIX].[ENTITY.SUFFIX].[Prefix.ATTRIBUTE]>=[[@3,11:69='<[PREFIX.DOMAIN.SUFFIX].[ENTITY.SUFFIX].[Prefix.ATTRIBUTE]>',<328>,1:11]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={query_dictionary={<[PREFIX.DOMAIN.SUFFIX].[ENTITY.SUFFIX].[Prefix.ATTRIBUTE]>=[[@3,11:69='<[PREFIX.DOMAIN.SUFFIX].[ENTITY.SUFFIX].[Prefix.ATTRIBUTE]>',<328>,1:11]]}, table_dictionary={<[DOMAIN].[ENTITY]>={}}, interface={<[PREFIX.DOMAIN.SUFFIX].[ENTITY.SUFFIX].[Prefix.ATTRIBUTE]>=[{substitution={name=<[PREFIX.DOMAIN.SUFFIX].[ENTITY.SUFFIX].[Prefix.ATTRIBUTE]>, parts={1=[PREFIX.DOMAIN.SUFFIX].[ENTITY.SUFFIX].[Prefix.ATTRIBUTE]}, type=column}, table_ref=a}]}, table_alias={a=<[DOMAIN].[ENTITY]>}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void extendedVariableNameWithDashTest() {
		final String query = " SELECT  a.<[PREFIX-DOMAIN-SUFFIX].[ENTITY-SUFFIX].[Prefix-ATTRIBUTE]> FROM <[DOMAIN].[ENTITY]>  as a "; 

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={substitution={name=<[PREFIX-DOMAIN-SUFFIX].[ENTITY-SUFFIX].[Prefix-ATTRIBUTE]>, parts={1=[PREFIX-DOMAIN-SUFFIX], 2=[ENTITY-SUFFIX], 3=[Prefix-ATTRIBUTE]}, type=column}, table_ref=a}}}, from={table={alias=a, substitution={name=<[DOMAIN].[ENTITY]>, parts={1=[DOMAIN], 2=[ENTITY]}, type=tuple}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[<[PREFIX-DOMAIN-SUFFIX].[ENTITY-SUFFIX].[Prefix-ATTRIBUTE]>]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<[DOMAIN].[ENTITY]>=tuple, <[PREFIX-DOMAIN-SUFFIX].[ENTITY-SUFFIX].[Prefix-ATTRIBUTE]>=column}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{<[DOMAIN].[ENTITY]>={<[PREFIX-DOMAIN-SUFFIX].[ENTITY-SUFFIX].[Prefix-ATTRIBUTE]>=[[@1,9:9='a',<381>,1:9]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={<[PREFIX-DOMAIN-SUFFIX].[ENTITY-SUFFIX].[Prefix-ATTRIBUTE]>=[[@3,11:69='<[PREFIX-DOMAIN-SUFFIX].[ENTITY-SUFFIX].[Prefix-ATTRIBUTE]>',<328>,1:11]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={query_dictionary={<[PREFIX-DOMAIN-SUFFIX].[ENTITY-SUFFIX].[Prefix-ATTRIBUTE]>=[[@3,11:69='<[PREFIX-DOMAIN-SUFFIX].[ENTITY-SUFFIX].[Prefix-ATTRIBUTE]>',<328>,1:11]]}, table_dictionary={<[DOMAIN].[ENTITY]>={}}, interface={<[PREFIX-DOMAIN-SUFFIX].[ENTITY-SUFFIX].[Prefix-ATTRIBUTE]>=[{substitution={name=<[PREFIX-DOMAIN-SUFFIX].[ENTITY-SUFFIX].[Prefix-ATTRIBUTE]>, parts={1=[PREFIX-DOMAIN-SUFFIX], 2=[ENTITY-SUFFIX], 3=[Prefix-ATTRIBUTE]}, type=column}, table_ref=a}]}, table_alias={a=<[DOMAIN].[ENTITY]>}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void extendedVariableNamePopulationSubnamerTest() {
		// ITEM : Add Population Qualifier to Tuple/table variables 
		final String query = " SELECT  a.col FROM <[schema].[entity].{pop1}>  as a "; 

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=col, table_ref=a}}}, from={table={alias=a, substitution={name=<[schema].[entity].{pop1}>, parts={1=[schema], 2=[entity], 3={pop1}}, type=tuple}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[col]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<[schema].[entity].{pop1}>=tuple}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{<[schema].[entity].{pop1}>={col=[[@1,9:9='a',<381>,1:9]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={col=[[@3,11:13='col',<381>,1:11]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={query_dictionary={col=[[@3,11:13='col',<381>,1:11]]}, table_dictionary={<[schema].[entity].{pop1}>={col=[[@1,9:9='a',<381>,1:9]]}}, interface={col=[{name=col, table_ref=a}]}, table_alias={a=<[schema].[entity].{pop1}>}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void extendedVariableNamePopulationQualifierTest() {
		// ITEM : Add Population Qualifier to Tuple/table variables 
		final String query = " SELECT  a.col FROM <[schema].[entity].{pop1}.[Current Batch]>  as a "; 

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=col, table_ref=a}}}, from={table={alias=a, substitution={name=<[schema].[entity].{pop1}.[Current Batch]>, parts={1=[schema], 2=[entity], 3={pop1}, 4=[Current Batch]}, type=tuple}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[col]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<[schema].[entity].{pop1}.[Current Batch]>=tuple}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{<[schema].[entity].{pop1}.[Current Batch]>={col=[[@1,9:9='a',<381>,1:9]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={col=[[@3,11:13='col',<381>,1:11]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={query_dictionary={col=[[@3,11:13='col',<381>,1:11]]}, table_dictionary={<[schema].[entity].{pop1}.[Current Batch]>={col=[[@1,9:9='a',<381>,1:9]]}}, interface={col=[{name=col, table_ref=a}]}, table_alias={a=<[schema].[entity].{pop1}.[Current Batch]>}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void entityVariableNamePopulationSubnameTest() {
		// ITEM : Add Population Qualifier to Tuple/table variables 
		final String query = " SELECT  a.col FROM <[entity].{pop1}>  as a "; 

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=col, table_ref=a}}}, from={table={alias=a, substitution={name=<[entity].{pop1}>, parts={1=[entity], 2={pop1}}, type=tuple}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[col]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<[entity].{pop1}>=tuple}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{<[entity].{pop1}>={col=[[@1,9:9='a',<381>,1:9]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={col=[[@3,11:13='col',<381>,1:11]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={query_dictionary={col=[[@3,11:13='col',<381>,1:11]]}, table_dictionary={<[entity].{pop1}>={col=[[@1,9:9='a',<381>,1:9]]}}, interface={col=[{name=col, table_ref=a}]}, table_alias={a=<[entity].{pop1}>}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void entityVariableNamePopulationQualifierTest() {
		// ITEM : Add Population Qualifier to Tuple/table variables 
		final String query = " SELECT  a.col FROM <[entity].{pop1}.[Current Batch]>  as a "; 

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=col, table_ref=a}}}, from={table={alias=a, substitution={name=<[entity].{pop1}.[Current Batch]>, parts={1=[entity], 2={pop1}, 3=[Current Batch]}, type=tuple}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[col]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<[entity].{pop1}.[Current Batch]>=tuple}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{<[entity].{pop1}.[Current Batch]>={col=[[@1,9:9='a',<381>,1:9]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={col=[[@3,11:13='col',<381>,1:11]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={query_dictionary={col=[[@3,11:13='col',<381>,1:11]]}, table_dictionary={<[entity].{pop1}.[Current Batch]>={col=[[@1,9:9='a',<381>,1:9]]}}, interface={col=[{name=col, table_ref=a}]}, table_alias={a=<[entity].{pop1}.[Current Batch]>}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void handlingQualifiedColumnWithExistingTableAliasDoesNotRequireKnownColumn() {
		final String query = " SELECT F4.col1 FROM <tuple1> as T3 union join fourth as F4";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=col1, table_ref=F4}}}, from={join={1={table={alias=T3, substitution={name=<tuple1>, type=tuple}}}, 2={join=unionjoin}, 3={table={alias=F4, table=fourth}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[col1]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<tuple1>=tuple}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertTrue("Table Dictionary should contain resolved column under fourth",
				extractor.getTableColumnDictionaryMap().toString().contains("fourth={col1=["));
		Assert.assertTrue("Query Column Dictionary should contain col1",
				extractor.getQueryColumnDictionaryMap().toString().contains("query0={col1=["));
		Assert.assertTrue("Symbol Table should include resolved qualified column under table_dictionary.fourth",
				extractor.getSymbolTable().toString().contains("table_dictionary={<tuple1>={}, fourth={col1=["));
	}


	@Test
	public void getSimpleColumnVariableTest() {
		// Column Variable Test
		String query = " select cec.<simple column> " + 
				"	from <[Enrollment Services].[Client Entering Class]> cec";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
				
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={substitution={name=<simple column>, type=column}, table_ref=cec}}}, from={table={alias=cec, substitution={name=<[Enrollment Services].[Client Entering Class]>, parts={1=[Enrollment Services], 2=[Client Entering Class]}, type=tuple}}}}}",
						extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[<simple column>]", 
						extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<[Enrollment Services].[Client Entering Class]>=tuple, <simple column>=column}", 
						extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{<[Enrollment Services].[Client Entering Class]>={<simple column>=[[@1,8:10='cec',<381>,1:8]]}}",
						extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={<simple column>=[[@3,12:26='<simple column>',<327>,1:12]]}}",
						extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={query_dictionary={<simple column>=[[@3,12:26='<simple column>',<327>,1:12]]}, table_dictionary={<[Enrollment Services].[Client Entering Class]>={<simple column>=[[@1,8:10='cec',<381>,1:8]]}}, interface={<simple column>=[{substitution={name=<simple column>, type=column}, table_ref=cec}]}, table_alias={cec=<[Enrollment Services].[Client Entering Class]>}}}",
						extractor.getSymbolTable().toString());
	}

	@Test
	public void getSubstitutionColumnVariableV1Test() {
		// Column Variable Test
		String query = " select cec.<select column> " + 
				"\n	from <[Enrollment Services].[Client Entering Class]> cec " +
				"\n	where cec.<where column> = 'abc' and cec.non_variable_col is not null";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
				
		// broken purposefully until we come back to generate a suite of tests around variable column recognition
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={substitution={name=<select column>, type=column}, table_ref=cec}}}, from={table={alias=cec, substitution={name=<[Enrollment Services].[Client Entering Class]>, parts={1=[Enrollment Services], 2=[Client Entering Class]}, type=tuple}}}, where={and={1={condition={left={column={substitution={name=<where column>, type=column}, table_ref=cec}}, right={literal='abc'}, operator==}}, 2={condition={left={column={name=non_variable_col, table_ref=cec}}, operator=is not null}}}}}}",
						extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[<select column>]", 
						extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<[Enrollment Services].[Client Entering Class]>=tuple, <select column>=column, <where column>=column}", 
						extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{<[Enrollment Services].[Client Entering Class]>={<select column>=[[@1,8:10='cec',<381>,1:8]], non_variable_col=[[@14,126:128='cec',<381>,3:38]], <where column>=[[@8,95:97='cec',<381>,3:7]]}}",
						extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={<select column>=[[@3,12:26='<select column>',<327>,1:12]]}}",
						extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={query_dictionary={<select column>=[[@3,12:26='<select column>',<327>,1:12]]}, table_dictionary={<[Enrollment Services].[Client Entering Class]>={<select column>=[[@1,8:10='cec',<381>,1:8]], non_variable_col=[[@14,126:128='cec',<381>,3:38]], <where column>=[[@8,95:97='cec',<381>,3:7]]}}, filters=[{substitution={name=<where column>, type=column}, table_ref=cec}, {name=non_variable_col, table_ref=cec}], interface={<select column>=[{substitution={name=<select column>, type=column}, table_ref=cec}]}, table_alias={cec=<[Enrollment Services].[Client Entering Class]>}}}",
						extractor.getSymbolTable().toString());
	}


	@Test
	public void getSubstitutionColumnVariableV2GroupByQualifiedColumnReferencesTest() {
			// Column Variable Test - GROUP BY variant
			String query = " select cec.<select column>, count(distinct cec.agg_col) " +
					"\nfrom <[Enrollment Services].[Client Entering Class]> cec " +
					"\ngroup by cec.non_variable_col, cec.<where column>";
			final SQLSelectParserParser parser = parse(query);
			SqlParseEventWalker extractor = runParsertest(query, parser);
			assertNoWalkerDiagnostics(extractor);

			// broken purposefully until we come back to generate a suite of tests around variable column recognition
			Assert.assertEquals("AST is wrong", "{SQL={select={1={column={substitution={name=<select column>, type=column}, table_ref=cec}}, 2={function={function_name=count, qualifier=distinct, parameters={column={name=agg_col, table_ref=cec}}}}}, from={table={alias=cec, substitution={name=<[Enrollment Services].[Client Entering Class]>, parts={1=[Enrollment Services], 2=[Client Entering Class]}, type=tuple}}}, groupby={1={column={name=non_variable_col, table_ref=cec}}, 2={column={substitution={name=<where column>, type=column}, table_ref=cec}}}}}",
							extractor.getAsTree().toString());
			Assert.assertEquals("Interface is wrong", "[<select column>, unnamed_0]",
							extractor.getInterface().toString());
			Assert.assertEquals("Substitution List is wrong", "{<[Enrollment Services].[Client Entering Class]>=tuple, <select column>=column, <where column>=column}",
							extractor.getSubstitutionsMap().toString());
			Assert.assertEquals("Table Dictionary is wrong", "{<[Enrollment Services].[Client Entering Class]>={agg_col=[[@8,44:46='cec',<381>,1:44]], <select column>=[[@1,8:10='cec',<381>,1:8]], non_variable_col=[[@17,125:127='cec',<381>,3:9]], <where column>=[[@21,147:149='cec',<381>,3:31]]}}",
							extractor.getTableColumnDictionaryMap().toString());
			Assert.assertEquals("Query Column Dictionary is wrong", "{query0={<select column>=[[@3,12:26='<select column>',<327>,1:12]], unnamed_0=[[@11,55:55=')',<288>,1:55]]}}",
							extractor.getQueryColumnDictionaryMap().toString());
			Assert.assertEquals("Symbol Table is wrong", "{query0={query_dictionary={<select column>=[[@3,12:26='<select column>',<327>,1:12]], unnamed_0=[[@11,55:55=')',<288>,1:55]]}, table_dictionary={<[Enrollment Services].[Client Entering Class]>={agg_col=[[@8,44:46='cec',<381>,1:44]], <select column>=[[@1,8:10='cec',<381>,1:8]], non_variable_col=[[@17,125:127='cec',<381>,3:9]], <where column>=[[@21,147:149='cec',<381>,3:31]]}}, grouped_by=[{name=non_variable_col, table_ref=cec}, {substitution={name=<where column>, type=column}, table_ref=cec}], interface={<select column>=[{substitution={name=<select column>, type=column}, table_ref=cec}], unnamed_0=[{name=agg_col, table_ref=cec}]}, table_alias={cec=<[Enrollment Services].[Client Entering Class]>}}}",
							extractor.getSymbolTable().toString());
		}

		@Test
		public void getSubstitutionColumnVariableV3OrderByQualifiedColumnReferencesTest() {
			// Column Variable Test - ORDER BY variant
			String query = " select cec.<select column> " +
					"\nfrom <[Enrollment Services].[Client Entering Class]> cec " +
					"\norder by cec.non_variable_col, cec.<where column>";
			final SQLSelectParserParser parser = parse(query);
			SqlParseEventWalker extractor = runParsertest(query, parser);
			assertNoWalkerDiagnostics(extractor);

			// broken purposefully until we come back to generate a suite of tests around variable column recognition
			Assert.assertEquals("AST is wrong", "{SQL={select={1={column={substitution={name=<select column>, type=column}, table_ref=cec}}}, orderby={1={null_order=null, predicand={column={name=non_variable_col, table_ref=cec}}, sort_order=ASC}, 2={null_order=null, predicand={column={substitution={name=<where column>, type=column}, table_ref=cec}}, sort_order=ASC}}, from={table={alias=cec, substitution={name=<[Enrollment Services].[Client Entering Class]>, parts={1=[Enrollment Services], 2=[Client Entering Class]}, type=tuple}}}}}",
							extractor.getAsTree().toString());
			Assert.assertEquals("Interface is wrong", "[<select column>]",
							extractor.getInterface().toString());
			Assert.assertEquals("Substitution List is wrong", "{<[Enrollment Services].[Client Entering Class]>=tuple, <select column>=column, <where column>=column}",
							extractor.getSubstitutionsMap().toString());
			Assert.assertEquals("Table Dictionary is wrong", "{<[Enrollment Services].[Client Entering Class]>={<select column>=[[@1,8:10='cec',<381>,1:8]], non_variable_col=[[@9,96:98='cec',<381>,3:9]], <where column>=[[@13,118:120='cec',<381>,3:31]]}}",
							extractor.getTableColumnDictionaryMap().toString());
			Assert.assertEquals("Query Column Dictionary is wrong", "{query0={<select column>=[[@3,12:26='<select column>',<327>,1:12]]}}",
							extractor.getQueryColumnDictionaryMap().toString());
			Assert.assertEquals("Symbol Table is wrong", "{query0={query_dictionary={<select column>=[[@3,12:26='<select column>',<327>,1:12]]}, table_dictionary={<[Enrollment Services].[Client Entering Class]>={<select column>=[[@1,8:10='cec',<381>,1:8]], non_variable_col=[[@9,96:98='cec',<381>,3:9]], <where column>=[[@13,118:120='cec',<381>,3:31]]}}, ordered_by=[{name=non_variable_col, table_ref=cec}, {substitution={name=<where column>, type=column}, table_ref=cec}], interface={<select column>=[{substitution={name=<select column>, type=column}, table_ref=cec}]}, table_alias={cec=<[Enrollment Services].[Client Entering Class]>}}}",
							extractor.getSymbolTable().toString());
		}

		@Test
		public void getSubstitutionColumnVariableV4HavingQualifiedColumnReferencesTest() {
			// Column Variable Test - HAVING variant
			String query = " select cec.<select column>, count(distinct cec.agg_col) " +
					"\nfrom <[Enrollment Services].[Client Entering Class]> cec " +
					"\ngroup by cec.<select column> " +
					"\nhaving cec.non_variable_col is not null and cec.<where column> is not null";
			final SQLSelectParserParser parser = parse(query);
			SqlParseEventWalker extractor = runParsertest(query, parser);
			assertNoWalkerDiagnostics(extractor);

			// broken purposefully until we come back to generate a suite of tests around variable column recognition
			Assert.assertEquals("AST is wrong", "{SQL={select={1={column={substitution={name=<select column>, type=column}, table_ref=cec}}, 2={function={function_name=count, qualifier=distinct, parameters={column={name=agg_col, table_ref=cec}}}}}, having={and={1={condition={left={column={name=non_variable_col, table_ref=cec}}, operator=is not null}}, 2={condition={left={column={substitution={name=<where column>, type=column}, table_ref=cec}}, operator=is not null}}}}, from={table={alias=cec, substitution={name=<[Enrollment Services].[Client Entering Class]>, parts={1=[Enrollment Services], 2=[Client Entering Class]}, type=tuple}}}, groupby={1={column={substitution={name=<select column>, type=column}, table_ref=cec}}}}}",
							extractor.getAsTree().toString());
			Assert.assertEquals("Interface is wrong", "[<select column>, unnamed_0]",
							extractor.getInterface().toString());
			Assert.assertEquals("Substitution List is wrong", "{<[Enrollment Services].[Client Entering Class]>=tuple, <select column>=column, <where column>=column}",
							extractor.getSubstitutionsMap().toString());
			Assert.assertEquals("Table Dictionary is wrong", "{<[Enrollment Services].[Client Entering Class]>={agg_col=[[@8,44:46='cec',<381>,1:44]], <select column>=[[@1,8:10='cec',<381>,1:8], [@17,125:127='cec',<381>,3:9]], non_variable_col=[[@21,153:155='cec',<381>,4:7]], <where column>=[[@28,190:192='cec',<381>,4:44]]}}",
							extractor.getTableColumnDictionaryMap().toString());
			Assert.assertEquals("Query Column Dictionary is wrong", "{query0={<select column>=[[@3,12:26='<select column>',<327>,1:12]], unnamed_0=[[@11,55:55=')',<288>,1:55]]}}",
							extractor.getQueryColumnDictionaryMap().toString());
			Assert.assertEquals("Symbol Table is wrong", "{query0={query_dictionary={<select column>=[[@3,12:26='<select column>',<327>,1:12]], unnamed_0=[[@11,55:55=')',<288>,1:55]]}, table_dictionary={<[Enrollment Services].[Client Entering Class]>={agg_col=[[@8,44:46='cec',<381>,1:44]], <select column>=[[@1,8:10='cec',<381>,1:8], [@17,125:127='cec',<381>,3:9]], non_variable_col=[[@21,153:155='cec',<381>,4:7]], <where column>=[[@28,190:192='cec',<381>,4:44]]}}, grouped_by=[{substitution={name=<select column>, type=column}, table_ref=cec}], filters=[{name=non_variable_col, table_ref=cec}, {substitution={name=<where column>, type=column}, table_ref=cec}], interface={<select column>=[{substitution={name=<select column>, type=column}, table_ref=cec}], unnamed_0=[{name=agg_col, table_ref=cec}]}, table_alias={cec=<[Enrollment Services].[Client Entering Class]>}}}",
							extractor.getSymbolTable().toString());
		}

		@Test
		public void getSubstitutionColumnVariableV5QualifyQualifiedColumnReferencesTest() {
			// Column Variable Test - QUALIFY variant
			String query = " select cec.<select column> " +
					"\nfrom <[Enrollment Services].[Client Entering Class]> cec " +
					"\nqualify row_number() over (partition by cec.non_variable_col, cec.<where column> order by cec.non_variable_col) = 1";
			final SQLSelectParserParser parser = parse(query);
			SqlParseEventWalker extractor = runParsertest(query, parser);
			assertNoWalkerDiagnostics(extractor);

			// broken purposefully until we come back to generate a suite of tests around variable column recognition
			Assert.assertEquals("AST is wrong", "{SQL={select={1={column={substitution={name=<select column>, type=column}, table_ref=cec}}}, from={table={alias=cec, substitution={name=<[Enrollment Services].[Client Entering Class]>, parts={1=[Enrollment Services], 2=[Client Entering Class]}, type=tuple}}}, qualify={condition={left={window_function={over={partition_by={1={column={name=non_variable_col, table_ref=cec}}, 2={column={substitution={name=<where column>, type=column}, table_ref=cec}}}, orderby={1={null_order=null, predicand={column={name=non_variable_col, table_ref=cec}}, sort_order=ASC}}}, function={function_name=row_number, parameters=null}}}, right={literal=1}, operator==}}}}",
							extractor.getAsTree().toString());
			Assert.assertEquals("Interface is wrong", "[<select column>]",
							extractor.getInterface().toString());
			Assert.assertEquals("Substitution List is wrong", "{<[Enrollment Services].[Client Entering Class]>=tuple, <select column>=column, <where column>=column}",
							extractor.getSubstitutionsMap().toString());
			Assert.assertEquals("Table Dictionary is wrong", "{<[Enrollment Services].[Client Entering Class]>={<select column>=[[@1,8:10='cec',<381>,1:8]], non_variable_col=[[@15,127:129='cec',<381>,3:40], [@24,177:179='cec',<381>,3:90]], <where column>=[[@19,149:151='cec',<381>,3:62]]}}",
							extractor.getTableColumnDictionaryMap().toString());
			Assert.assertEquals("Query Column Dictionary is wrong", "{query0={<select column>=[[@3,12:26='<select column>',<327>,1:12]]}}",
							extractor.getQueryColumnDictionaryMap().toString());
			Assert.assertEquals("Symbol Table is wrong", "{query0={query_dictionary={<select column>=[[@3,12:26='<select column>',<327>,1:12]]}, table_dictionary={<[Enrollment Services].[Client Entering Class]>={<select column>=[[@1,8:10='cec',<381>,1:8]], non_variable_col=[[@15,127:129='cec',<381>,3:40], [@24,177:179='cec',<381>,3:90]], <where column>=[[@19,149:151='cec',<381>,3:62]]}}, filters=[{name=non_variable_col, table_ref=cec}, {substitution={name=<where column>, type=column}, table_ref=cec}], interface={<select column>=[{substitution={name=<select column>, type=column}, table_ref=cec}]}, table_alias={cec=<[Enrollment Services].[Client Entering Class]>}}}",
							extractor.getSymbolTable().toString());
		}

		@Test
		public void getSubstitutionColumnVariableV6SecondJoinOnQualifiedColumnReferencesTest() {
			// Column Variable Test - second JOIN ON variant
			String query = " select cec.<select column> " +
					"\nfrom <[Enrollment Services].[Client Entering Class]> cec " +
					"\njoin <[Enrollment Services].[Client Entering Class]> cec2 on cec.non_variable_col = cec2.non_variable_col " +
					"\njoin <[Enrollment Services].[Client Entering Class]> cec3 on cec.<where column> = cec3.<where column>";
			final SQLSelectParserParser parser = parse(query);
			SqlParseEventWalker extractor = runParsertest(query, parser);
			assertNoWalkerDiagnostics(extractor);

			// broken purposefully until we come back to generate a suite of tests around variable column recognition
			Assert.assertEquals("AST is wrong", "{SQL={select={1={column={substitution={name=<select column>, type=column}, table_ref=cec}}}, from={join={1={table={alias=cec, substitution={name=<[Enrollment Services].[Client Entering Class]>, parts={1=[Enrollment Services], 2=[Client Entering Class]}, type=tuple}}}, 2={join=join, on={condition={left={column={name=non_variable_col, table_ref=cec}}, right={column={name=non_variable_col, table_ref=cec2}}, operator==}}}, 3={table={alias=cec2, substitution={name=<[Enrollment Services].[Client Entering Class]>, parts={1=[Enrollment Services], 2=[Client Entering Class]}, type=tuple}}}, 4={join=join, on={condition={left={column={substitution={name=<where column>, type=column}, table_ref=cec}}, right={column={substitution={name=<where column>, type=column}, table_ref=cec3}}, operator==}}}, 5={table={alias=cec3, substitution={name=<[Enrollment Services].[Client Entering Class]>, parts={1=[Enrollment Services], 2=[Client Entering Class]}, type=tuple}}}}}}}",
							extractor.getAsTree().toString());
			Assert.assertEquals("Interface is wrong", "[<select column>]",
							extractor.getInterface().toString());
			Assert.assertEquals("Substitution List is wrong", "{<[Enrollment Services].[Client Entering Class]>=tuple, <select column>=column, <where column>=column}",
							extractor.getSubstitutionsMap().toString());
			Assert.assertEquals("Table Dictionary is wrong", "{<[Enrollment Services].[Client Entering Class]>={<select column>=[[@1,8:10='cec',<381>,1:8]], non_variable_col=[[@11,148:150='cec',<381>,3:61], [@15,171:174='cec2',<381>,3:84]], <where column>=[[@26,276:279='cec3',<381>,4:82], [@22,255:257='cec',<381>,4:61]]}}",
							extractor.getTableColumnDictionaryMap().toString());
			Assert.assertEquals("Query Column Dictionary is wrong", "{query0={<select column>=[[@3,12:26='<select column>',<327>,1:12]]}}",
							extractor.getQueryColumnDictionaryMap().toString());
			Assert.assertEquals("Symbol Table is wrong", "{query0={query_dictionary={<select column>=[[@3,12:26='<select column>',<327>,1:12]]}, table_dictionary={<[Enrollment Services].[Client Entering Class]>={<select column>=[[@1,8:10='cec',<381>,1:8]], non_variable_col=[[@11,148:150='cec',<381>,3:61], [@15,171:174='cec2',<381>,3:84]], <where column>=[[@26,276:279='cec3',<381>,4:82], [@22,255:257='cec',<381>,4:61]]}}, filters=[{name=non_variable_col, table_ref=cec}, {name=non_variable_col, table_ref=cec2}, {substitution={name=<where column>, type=column}, table_ref=cec}, {substitution={name=<where column>, type=column}, table_ref=cec3}], interface={<select column>=[{substitution={name=<select column>, type=column}, table_ref=cec}]}, table_alias={cec=<[Enrollment Services].[Client Entering Class]>, cec3=<[Enrollment Services].[Client Entering Class]>, cec2=<[Enrollment Services].[Client Entering Class]>}}}",
							extractor.getSymbolTable().toString());
		}

		@Test
		public void getSubstitutionColumnVariableV7SelfUnionSecondSubqueryQualifiedColumnReferencesTest() {
			// Column Variable Test - self-UNION variant
			String query = " select cec.<select column> " +
					"\nfrom <[Enrollment Services].[Client Entering Class]> cec " +
					"\nwhere cec.non_variable_col is not null and cec.<where column> = 'abc' " +
					"\nunion " +
					"\nselect cec.<select column> " +
					"\nfrom <[Enrollment Services].[Client Entering Class]> cec " +
					"\nwhere cec.non_variable_col is not null and cec.<where column> = 'abc'";
			final SQLSelectParserParser parser = parse(query);
			SqlParseEventWalker extractor = runParsertest(query, parser);
			assertNoWalkerDiagnostics(extractor);

			// broken purposefully until we come back to generate a suite of tests around variable column recognition
			Assert.assertEquals("AST is wrong", "{SQL={union={1={select={1={column={substitution={name=<select column>, type=column}, table_ref=cec}}}, from={table={alias=cec, substitution={name=<[Enrollment Services].[Client Entering Class]>, parts={1=[Enrollment Services], 2=[Client Entering Class]}, type=tuple}}}, where={and={1={condition={left={column={name=non_variable_col, table_ref=cec}}, operator=is not null}}, 2={condition={left={column={substitution={name=<where column>, type=column}, table_ref=cec}}, right={literal='abc'}, operator==}}}}}, 2={union={qualifier=null, operator=union}}, 3={select={1={column={substitution={name=<select column>, type=column}, table_ref=cec}}}, from={table={alias=cec, substitution={name=<[Enrollment Services].[Client Entering Class]>, parts={1=[Enrollment Services], 2=[Client Entering Class]}, type=tuple}}}, where={and={1={condition={left={column={name=non_variable_col, table_ref=cec}}, operator=is not null}}, 2={condition={left={column={substitution={name=<where column>, type=column}, table_ref=cec}}, right={literal='abc'}, operator==}}}}}}}}",
							extractor.getAsTree().toString());
			Assert.assertEquals("Interface is wrong", "[<select column>]",
							extractor.getInterface().toString());
			Assert.assertEquals("Substitution List is wrong", "{<[Enrollment Services].[Client Entering Class]>=tuple, <select column>=column, <where column>=column}",
							extractor.getSubstitutionsMap().toString());
			Assert.assertEquals("Table Dictionary is wrong", "{<[Enrollment Services].[Client Entering Class]>={<select column>=[[@1,8:10='cec',<381>,1:8], [@22,172:174='cec',<381>,5:7]], non_variable_col=[[@8,93:95='cec',<381>,3:6], [@29,257:259='cec',<381>,7:6]], <where column>=[[@15,130:132='cec',<381>,3:43], [@36,294:296='cec',<381>,7:43]]}}",
							extractor.getTableColumnDictionaryMap().toString());
			Assert.assertEquals("Query Column Dictionary is wrong", "{query0={<select column>=[[@3,12:26='<select column>',<327>,1:12]]}, query1={<select column>=[[@24,176:190='<select column>',<327>,5:11]]}}",
							extractor.getQueryColumnDictionaryMap().toString());
			Assert.assertEquals("Symbol Table is wrong", "{union2={query0={query_dictionary={<select column>=[[@3,12:26='<select column>',<327>,1:12]]}, table_dictionary={<[Enrollment Services].[Client Entering Class]>={<select column>=[[@1,8:10='cec',<381>,1:8], [@22,172:174='cec',<381>,5:7]], non_variable_col=[[@8,93:95='cec',<381>,3:6], [@29,257:259='cec',<381>,7:6]], <where column>=[[@15,130:132='cec',<381>,3:43], [@36,294:296='cec',<381>,7:43]]}}, filters=[{name=non_variable_col, table_ref=cec}, {substitution={name=<where column>, type=column}, table_ref=cec}], interface={<select column>=[{substitution={name=<select column>, type=column}, table_ref=cec}]}, table_alias={cec=<[Enrollment Services].[Client Entering Class]>}}, interface={<select column>=query_column}, query1={query_dictionary={<select column>=[[@24,176:190='<select column>',<327>,5:11]]}, table_dictionary={<[Enrollment Services].[Client Entering Class]>={<select column>=[[@22,172:174='cec',<381>,5:7]], non_variable_col=[[@29,257:259='cec',<381>,7:6]], <where column>=[[@36,294:296='cec',<381>,7:43]]}}, filters=[{name=non_variable_col, table_ref=cec}, {substitution={name=<where column>, type=column}, table_ref=cec}], interface={<select column>=[{substitution={name=<select column>, type=column}, table_ref=cec}]}, table_alias={cec=<[Enrollment Services].[Client Entering Class]>}}}}",
							extractor.getSymbolTable().toString());
		}

		@Test
		public void getSubstitutionColumnVariableV8SelfIntersectionSecondSubqueryQualifiedColumnReferencesTest() {
			// Column Variable Test - self-INTERSECT variant
			String query = " select cec.<select column> " +
					"\nfrom <[Enrollment Services].[Client Entering Class]> cec " +
					"\nwhere cec.non_variable_col is not null and cec.<where column> = 'abc' " +
					"\nintersect " +
					"\nselect cec.<select column> " +
					"\nfrom <[Enrollment Services].[Client Entering Class]> cec " +
					"\nwhere cec.non_variable_col is not null and cec.<where column> = 'abc'";
			final SQLSelectParserParser parser = parse(query);
			SqlParseEventWalker extractor = runParsertest(query, parser);
			assertNoWalkerDiagnostics(extractor);

			// broken purposefully until we come back to generate a suite of tests around variable column recognition
			Assert.assertEquals("AST is wrong", "{SQL={intersect={1={select={1={column={substitution={name=<select column>, type=column}, table_ref=cec}}}, from={table={alias=cec, substitution={name=<[Enrollment Services].[Client Entering Class]>, parts={1=[Enrollment Services], 2=[Client Entering Class]}, type=tuple}}}, where={and={1={condition={left={column={name=non_variable_col, table_ref=cec}}, operator=is not null}}, 2={condition={left={column={substitution={name=<where column>, type=column}, table_ref=cec}}, right={literal='abc'}, operator==}}}}}, 2={intersect={qualifier=null, operator=intersect}}, 3={select={1={column={substitution={name=<select column>, type=column}, table_ref=cec}}}, from={table={alias=cec, substitution={name=<[Enrollment Services].[Client Entering Class]>, parts={1=[Enrollment Services], 2=[Client Entering Class]}, type=tuple}}}, where={and={1={condition={left={column={name=non_variable_col, table_ref=cec}}, operator=is not null}}, 2={condition={left={column={substitution={name=<where column>, type=column}, table_ref=cec}}, right={literal='abc'}, operator==}}}}}}}}",
							extractor.getAsTree().toString());
			Assert.assertEquals("Interface is wrong", "[<select column>]",
							extractor.getInterface().toString());
			Assert.assertEquals("Substitution List is wrong", "{<[Enrollment Services].[Client Entering Class]>=tuple, <select column>=column, <where column>=column}",
							extractor.getSubstitutionsMap().toString());
			Assert.assertEquals("Table Dictionary is wrong", "{<[Enrollment Services].[Client Entering Class]>={<select column>=[[@1,8:10='cec',<381>,1:8], [@22,176:178='cec',<381>,5:7]], non_variable_col=[[@8,93:95='cec',<381>,3:6], [@29,261:263='cec',<381>,7:6]], <where column>=[[@15,130:132='cec',<381>,3:43], [@36,298:300='cec',<381>,7:43]]}}",
							extractor.getTableColumnDictionaryMap().toString());
			Assert.assertEquals("Query Column Dictionary is wrong", "{query0={<select column>=[[@3,12:26='<select column>',<327>,1:12]]}, query1={<select column>=[[@24,180:194='<select column>',<327>,5:11]]}}",
							extractor.getQueryColumnDictionaryMap().toString());
			Assert.assertEquals("Symbol Table is wrong", "{intersect2={query0={query_dictionary={<select column>=[[@3,12:26='<select column>',<327>,1:12]]}, table_dictionary={<[Enrollment Services].[Client Entering Class]>={<select column>=[[@1,8:10='cec',<381>,1:8], [@22,176:178='cec',<381>,5:7]], non_variable_col=[[@8,93:95='cec',<381>,3:6], [@29,261:263='cec',<381>,7:6]], <where column>=[[@15,130:132='cec',<381>,3:43], [@36,298:300='cec',<381>,7:43]]}}, filters=[{name=non_variable_col, table_ref=cec}, {substitution={name=<where column>, type=column}, table_ref=cec}], interface={<select column>=[{substitution={name=<select column>, type=column}, table_ref=cec}]}, table_alias={cec=<[Enrollment Services].[Client Entering Class]>}}, interface={<select column>=query_column}, query1={query_dictionary={<select column>=[[@24,180:194='<select column>',<327>,5:11]]}, table_dictionary={<[Enrollment Services].[Client Entering Class]>={<select column>=[[@22,176:178='cec',<381>,5:7]], non_variable_col=[[@29,261:263='cec',<381>,7:6]], <where column>=[[@36,298:300='cec',<381>,7:43]]}}, filters=[{name=non_variable_col, table_ref=cec}, {substitution={name=<where column>, type=column}, table_ref=cec}], interface={<select column>=[{substitution={name=<select column>, type=column}, table_ref=cec}]}, table_alias={cec=<[Enrollment Services].[Client Entering Class]>}}}}",
							extractor.getSymbolTable().toString());
		}

		@Test
		public void getSubstitutionColumnVariableV9CteWrappedWhereVariantWithJoinOnSelectColumnTest() {
			// Column Variable Test - V1 wrapped as a CTE with outer wildcard select
			String query = "with wrapped as ( " +
					" select cec.<select column> " +
					"\nfrom <[Enrollment Services].[Client Entering Class]> cec " +
					"\nwhere cec.<where column> = 'abc' and cec.non_variable_col is not null " +
					") " +
					"\nselect cec.<select column>, d.dummy_col2 from wrapped cec join dummy_table d on cec.<select column> = d.dummy_col";
			final SQLSelectParserParser parser = parse(query);
			SqlParseEventWalker extractor = runParsertest(query, parser);
			assertNoWalkerDiagnostics(extractor);

			// broken purposefully until we come back to generate a suite of tests around variable column recognition
			Assert.assertEquals("AST is wrong", "{SQL={with={1={cte={select={1={column={substitution={name=<select column>, type=column}, table_ref=cec}}}, from={table={alias=cec, substitution={name=<[Enrollment Services].[Client Entering Class]>, parts={1=[Enrollment Services], 2=[Client Entering Class]}, type=tuple}}}, where={and={1={condition={left={column={substitution={name=<where column>, type=column}, table_ref=cec}}, right={literal='abc'}, operator==}}, 2={condition={left={column={name=non_variable_col, table_ref=cec}}, operator=is not null}}}}}, alias=wrapped}}, query={select={1={column={substitution={name=<select column>, type=column}, table_ref=cec}}, 2={column={name=dummy_col2, table_ref=d}}}, from={join={1={table={alias=cec, table=wrapped}}, 2={join=join, on={condition={left={column={substitution={name=<select column>, type=column}, table_ref=cec}}, right={column={name=dummy_col, table_ref=d}}, operator==}}}, 3={table={alias=d, table=dummy_table}}}}}}}",
							extractor.getAsTree().toString());
			Assert.assertEquals("Interface is wrong", "[<select column>, dummy_col2]",
							extractor.getInterface().toString());
			Assert.assertEquals("Substitution List is wrong", "{<[Enrollment Services].[Client Entering Class]>=tuple, <select column>=column, <where column>=column}",
							extractor.getSubstitutionsMap().toString());
			Assert.assertEquals("Table Dictionary is wrong", "{<[Enrollment Services].[Client Entering Class]>={<select column>=[[@5,26:28='cec',<381>,1:26], [@26,185:187='cec',<381>,4:7], [@40,258:260='cec',<381>,4:80]], non_variable_col=[[@18,142:144='cec',<381>,3:37]], <where column>=[[@12,111:113='cec',<381>,3:6]]}, dummy_table={dummy_col=[[@44,280:280='d',<381>,4:102]], dummy_col2=[[@30,206:206='d',<381>,4:28]]}}",
							extractor.getTableColumnDictionaryMap().toString());
			Assert.assertEquals("Query Column Dictionary is wrong", "{query0={<select column>=[[@7,30:44='<select column>',<327>,1:30], [@26,185:187='cec',<381>,4:7], [@40,258:260='cec',<381>,4:80]]}, query1={<select column>=[[@28,189:203='<select column>',<327>,4:11]], dummy_col2=[[@32,208:217='dummy_col2',<381>,4:30]]}}",
							extractor.getQueryColumnDictionaryMap().toString());
			Assert.assertEquals("Symbol Table is wrong", "{query1={query_dictionary={<select column>=[[@28,189:203='<select column>',<327>,4:11]], dummy_col2=[[@32,208:217='dummy_col2',<381>,4:30]]}, table_dictionary={dummy_table={dummy_col=[[@44,280:280='d',<381>,4:102]], dummy_col2=[[@30,206:206='d',<381>,4:28]]}}, cte_list={wrapped=query0, cec=query0}, def_query0={query_dictionary={<select column>=[[@7,30:44='<select column>',<327>,1:30], [@26,185:187='cec',<381>,4:7], [@40,258:260='cec',<381>,4:80]]}, table_dictionary={<[Enrollment Services].[Client Entering Class]>={<select column>=[[@5,26:28='cec',<381>,1:26], [@26,185:187='cec',<381>,4:7], [@40,258:260='cec',<381>,4:80]], non_variable_col=[[@18,142:144='cec',<381>,3:37]], <where column>=[[@12,111:113='cec',<381>,3:6]]}}, filters=[{substitution={name=<where column>, type=column}, table_ref=cec}, {name=non_variable_col, table_ref=cec}], interface={<select column>=[{substitution={name=<select column>, type=column}, table_ref=cec}]}, table_alias={cec=<[Enrollment Services].[Client Entering Class]>}}, filters=[{substitution={name=<select column>, type=column}, table_ref=cec}, {name=dummy_col, table_ref=d}], interface={<select column>=[{substitution={name=<select column>, type=column}, table_ref=cec}], dummy_col2=[{name=dummy_col2, table_ref=d}]}, table_alias={wrapped=query0}}}",
							extractor.getSymbolTable().toString());
		}

		@Test
		public void getSubstitutionColumnVariableV10CteWrappedGroupByVariantWithJoinOnSelectColumnTest() {
			// Column Variable Test - V2 wrapped as a CTE with outer wildcard select
			String query = "with wrapped as ( " +
					" select cec.<select column>, count(distinct cec.agg_col) " +
					"\nfrom <[Enrollment Services].[Client Entering Class]> cec " +
					"\ngroup by cec.non_variable_col, cec.<where column> " +
					") " +
					"\nselect cec.<select column>, d.dummy_col2 from wrapped cec join dummy_table d on cec.<select column> = d.dummy_col";
			final SQLSelectParserParser parser = parse(query);
			SqlParseEventWalker extractor = runParsertest(query, parser);
			assertNoWalkerDiagnostics(extractor);

			// broken purposefully until we come back to generate a suite of tests around variable column recognition
			Assert.assertEquals("AST is wrong", "{SQL={with={1={cte={select={1={column={substitution={name=<select column>, type=column}, table_ref=cec}}, 2={function={function_name=count, qualifier=distinct, parameters={column={name=agg_col, table_ref=cec}}}}}, from={table={alias=cec, substitution={name=<[Enrollment Services].[Client Entering Class]>, parts={1=[Enrollment Services], 2=[Client Entering Class]}, type=tuple}}}, groupby={1={column={name=non_variable_col, table_ref=cec}}, 2={column={substitution={name=<where column>, type=column}, table_ref=cec}}}}, alias=wrapped}}, query={select={1={column={substitution={name=<select column>, type=column}, table_ref=cec}}, 2={column={name=dummy_col2, table_ref=d}}}, from={join={1={table={alias=cec, table=wrapped}}, 2={join=join, on={condition={left={column={substitution={name=<select column>, type=column}, table_ref=cec}}, right={column={name=dummy_col, table_ref=d}}, operator==}}}, 3={table={alias=d, table=dummy_table}}}}}}}",
							extractor.getAsTree().toString());
			Assert.assertEquals("Interface is wrong", "[<select column>, dummy_col2]",
							extractor.getInterface().toString());
			Assert.assertEquals("Substitution List is wrong", "{<[Enrollment Services].[Client Entering Class]>=tuple, <select column>=column, <where column>=column}",
							extractor.getSubstitutionsMap().toString());
			Assert.assertEquals("Table Dictionary is wrong", "{<[Enrollment Services].[Client Entering Class]>={agg_col=[[@12,62:64='cec',<381>,1:62]], <select column>=[[@5,26:28='cec',<381>,1:26], [@30,194:196='cec',<381>,4:7], [@44,267:269='cec',<381>,4:80]], non_variable_col=[[@21,143:145='cec',<381>,3:9]], <where column>=[[@25,165:167='cec',<381>,3:31]]}, dummy_table={dummy_col=[[@48,289:289='d',<381>,4:102]], dummy_col2=[[@34,215:215='d',<381>,4:28]]}}",
							extractor.getTableColumnDictionaryMap().toString());
			Assert.assertEquals("Query Column Dictionary is wrong", "{query0={<select column>=[[@7,30:44='<select column>',<327>,1:30], [@30,194:196='cec',<381>,4:7], [@44,267:269='cec',<381>,4:80]], unnamed_0=[[@15,73:73=')',<288>,1:73]]}, query1={<select column>=[[@32,198:212='<select column>',<327>,4:11]], dummy_col2=[[@36,217:226='dummy_col2',<381>,4:30]]}}",
							extractor.getQueryColumnDictionaryMap().toString());
			Assert.assertEquals("Symbol Table is wrong", "{query1={query_dictionary={<select column>=[[@32,198:212='<select column>',<327>,4:11]], dummy_col2=[[@36,217:226='dummy_col2',<381>,4:30]]}, table_dictionary={dummy_table={dummy_col=[[@48,289:289='d',<381>,4:102]], dummy_col2=[[@34,215:215='d',<381>,4:28]]}}, cte_list={wrapped=query0, cec=query0}, def_query0={query_dictionary={<select column>=[[@7,30:44='<select column>',<327>,1:30], [@30,194:196='cec',<381>,4:7], [@44,267:269='cec',<381>,4:80]], unnamed_0=[[@15,73:73=')',<288>,1:73]]}, table_dictionary={<[Enrollment Services].[Client Entering Class]>={agg_col=[[@12,62:64='cec',<381>,1:62]], <select column>=[[@5,26:28='cec',<381>,1:26], [@30,194:196='cec',<381>,4:7], [@44,267:269='cec',<381>,4:80]], non_variable_col=[[@21,143:145='cec',<381>,3:9]], <where column>=[[@25,165:167='cec',<381>,3:31]]}}, grouped_by=[{name=non_variable_col, table_ref=cec}, {substitution={name=<where column>, type=column}, table_ref=cec}], interface={<select column>=[{substitution={name=<select column>, type=column}, table_ref=cec}], unnamed_0=[{name=agg_col, table_ref=cec}]}, table_alias={cec=<[Enrollment Services].[Client Entering Class]>}}, filters=[{substitution={name=<select column>, type=column}, table_ref=cec}, {name=dummy_col, table_ref=d}], interface={<select column>=[{substitution={name=<select column>, type=column}, table_ref=cec}], dummy_col2=[{name=dummy_col2, table_ref=d}]}, table_alias={wrapped=query0}}}",
							extractor.getSymbolTable().toString());
		}

		@Test
		public void getSubstitutionColumnVariableV11CteWrappedOrderByVariantWithJoinOnSelectColumnTest() {
			// Column Variable Test - V3 wrapped as a CTE with outer wildcard select
			String query = "with wrapped as ( " +
					" select cec.<select column> " +
					"\nfrom <[Enrollment Services].[Client Entering Class]> cec " +
					"\norder by cec.non_variable_col, cec.<where column> " +
					") " +
					"\nselect cec.<select column>, d.dummy_col2 from wrapped cec join dummy_table d on cec.<select column> = d.dummy_col";
			final SQLSelectParserParser parser = parse(query);
			SqlParseEventWalker extractor = runParsertest(query, parser);
			assertNoWalkerDiagnostics(extractor);

			// broken purposefully until we come back to generate a suite of tests around variable column recognition
			Assert.assertEquals("AST is wrong", "{SQL={with={1={cte={select={1={column={substitution={name=<select column>, type=column}, table_ref=cec}}}, orderby={1={null_order=null, predicand={column={name=non_variable_col, table_ref=cec}}, sort_order=ASC}, 2={null_order=null, predicand={column={substitution={name=<where column>, type=column}, table_ref=cec}}, sort_order=ASC}}, from={table={alias=cec, substitution={name=<[Enrollment Services].[Client Entering Class]>, parts={1=[Enrollment Services], 2=[Client Entering Class]}, type=tuple}}}}, alias=wrapped}}, query={select={1={column={substitution={name=<select column>, type=column}, table_ref=cec}}, 2={column={name=dummy_col2, table_ref=d}}}, from={join={1={table={alias=cec, table=wrapped}}, 2={join=join, on={condition={left={column={substitution={name=<select column>, type=column}, table_ref=cec}}, right={column={name=dummy_col, table_ref=d}}, operator==}}}, 3={table={alias=d, table=dummy_table}}}}}}}",
							extractor.getAsTree().toString());
			Assert.assertEquals("Interface is wrong", "[<select column>, dummy_col2]",
							extractor.getInterface().toString());
			Assert.assertEquals("Substitution List is wrong", "{<[Enrollment Services].[Client Entering Class]>=tuple, <select column>=column, <where column>=column}",
							extractor.getSubstitutionsMap().toString());
			Assert.assertEquals("Table Dictionary is wrong", "{<[Enrollment Services].[Client Entering Class]>={<select column>=[[@5,26:28='cec',<381>,1:26], [@22,165:167='cec',<381>,4:7], [@36,238:240='cec',<381>,4:80]], non_variable_col=[[@13,114:116='cec',<381>,3:9]], <where column>=[[@17,136:138='cec',<381>,3:31]]}, dummy_table={dummy_col=[[@40,260:260='d',<381>,4:102]], dummy_col2=[[@26,186:186='d',<381>,4:28]]}}",
							extractor.getTableColumnDictionaryMap().toString());
			Assert.assertEquals("Query Column Dictionary is wrong", "{query0={<select column>=[[@7,30:44='<select column>',<327>,1:30], [@22,165:167='cec',<381>,4:7], [@36,238:240='cec',<381>,4:80]]}, query1={<select column>=[[@24,169:183='<select column>',<327>,4:11]], dummy_col2=[[@28,188:197='dummy_col2',<381>,4:30]]}}",
							extractor.getQueryColumnDictionaryMap().toString());
			Assert.assertEquals("Symbol Table is wrong", "{query1={query_dictionary={<select column>=[[@24,169:183='<select column>',<327>,4:11]], dummy_col2=[[@28,188:197='dummy_col2',<381>,4:30]]}, table_dictionary={dummy_table={dummy_col=[[@40,260:260='d',<381>,4:102]], dummy_col2=[[@26,186:186='d',<381>,4:28]]}}, cte_list={wrapped=query0, cec=query0}, def_query0={query_dictionary={<select column>=[[@7,30:44='<select column>',<327>,1:30], [@22,165:167='cec',<381>,4:7], [@36,238:240='cec',<381>,4:80]]}, table_dictionary={<[Enrollment Services].[Client Entering Class]>={<select column>=[[@5,26:28='cec',<381>,1:26], [@22,165:167='cec',<381>,4:7], [@36,238:240='cec',<381>,4:80]], non_variable_col=[[@13,114:116='cec',<381>,3:9]], <where column>=[[@17,136:138='cec',<381>,3:31]]}}, ordered_by=[{name=non_variable_col, table_ref=cec}, {substitution={name=<where column>, type=column}, table_ref=cec}], interface={<select column>=[{substitution={name=<select column>, type=column}, table_ref=cec}]}, table_alias={cec=<[Enrollment Services].[Client Entering Class]>}}, filters=[{substitution={name=<select column>, type=column}, table_ref=cec}, {name=dummy_col, table_ref=d}], interface={<select column>=[{substitution={name=<select column>, type=column}, table_ref=cec}], dummy_col2=[{name=dummy_col2, table_ref=d}]}, table_alias={wrapped=query0}}}",
							extractor.getSymbolTable().toString());
		}

		@Test
		public void getSubstitutionColumnVariableV12CteWrappedHavingVariantWithJoinOnSelectColumnTest() {
			// Column Variable Test - V4 wrapped as a CTE with outer wildcard select
			String query = "with wrapped as ( " +
					" select cec.<select column>, count(distinct cec.agg_col) " +
					"\nfrom <[Enrollment Services].[Client Entering Class]> cec " +
					"\ngroup by cec.non_variable_col, cec.<where column> " +
					"\nhaving cec.non_variable_col is not null and cec.<where column> is not null " +
					") " +
					"\nselect cec.<select column>, d.dummy_col2 from wrapped cec join dummy_table d on cec.<select column> = d.dummy_col";
			final SQLSelectParserParser parser = parse(query);
			SqlParseEventWalker extractor = runParsertest(query, parser);
			assertNoWalkerDiagnostics(extractor);

			// broken purposefully until we come back to generate a suite of tests around variable column recognition
			Assert.assertEquals("AST is wrong", "{SQL={with={1={cte={select={1={column={substitution={name=<select column>, type=column}, table_ref=cec}}, 2={function={function_name=count, qualifier=distinct, parameters={column={name=agg_col, table_ref=cec}}}}}, having={and={1={condition={left={column={name=non_variable_col, table_ref=cec}}, operator=is not null}}, 2={condition={left={column={substitution={name=<where column>, type=column}, table_ref=cec}}, operator=is not null}}}}, from={table={alias=cec, substitution={name=<[Enrollment Services].[Client Entering Class]>, parts={1=[Enrollment Services], 2=[Client Entering Class]}, type=tuple}}}, groupby={1={column={name=non_variable_col, table_ref=cec}}, 2={column={substitution={name=<where column>, type=column}, table_ref=cec}}}}, alias=wrapped}}, query={select={1={column={substitution={name=<select column>, type=column}, table_ref=cec}}, 2={column={name=dummy_col2, table_ref=d}}}, from={join={1={table={alias=cec, table=wrapped}}, 2={join=join, on={condition={left={column={substitution={name=<select column>, type=column}, table_ref=cec}}, right={column={name=dummy_col, table_ref=d}}, operator==}}}, 3={table={alias=d, table=dummy_table}}}}}}}",
							extractor.getAsTree().toString());
			Assert.assertEquals("Interface is wrong", "[<select column>, dummy_col2]",
							extractor.getInterface().toString());
			Assert.assertEquals("Substitution List is wrong", "{<[Enrollment Services].[Client Entering Class]>=tuple, <select column>=column, <where column>=column}",
							extractor.getSubstitutionsMap().toString());
			Assert.assertEquals("Table Dictionary is wrong", "{<[Enrollment Services].[Client Entering Class]>={agg_col=[[@12,62:64='cec',<381>,1:62]], <select column>=[[@5,26:28='cec',<381>,1:26], [@44,270:272='cec',<381>,5:7], [@58,343:345='cec',<381>,5:80]], non_variable_col=[[@21,143:145='cec',<381>,3:9], [@29,192:194='cec',<381>,4:7]], <where column>=[[@25,165:167='cec',<381>,3:31], [@36,229:231='cec',<381>,4:44]]}, dummy_table={dummy_col=[[@62,365:365='d',<381>,5:102]], dummy_col2=[[@48,291:291='d',<381>,5:28]]}}",
							extractor.getTableColumnDictionaryMap().toString());
			Assert.assertEquals("Query Column Dictionary is wrong", "{query0={<select column>=[[@7,30:44='<select column>',<327>,1:30], [@44,270:272='cec',<381>,5:7], [@58,343:345='cec',<381>,5:80]], unnamed_0=[[@15,73:73=')',<288>,1:73]]}, query1={<select column>=[[@46,274:288='<select column>',<327>,5:11]], dummy_col2=[[@50,293:302='dummy_col2',<381>,5:30]]}}",
							extractor.getQueryColumnDictionaryMap().toString());
			Assert.assertEquals("Symbol Table is wrong", "{query1={query_dictionary={<select column>=[[@46,274:288='<select column>',<327>,5:11]], dummy_col2=[[@50,293:302='dummy_col2',<381>,5:30]]}, table_dictionary={dummy_table={dummy_col=[[@62,365:365='d',<381>,5:102]], dummy_col2=[[@48,291:291='d',<381>,5:28]]}}, cte_list={wrapped=query0, cec=query0}, def_query0={query_dictionary={<select column>=[[@7,30:44='<select column>',<327>,1:30], [@44,270:272='cec',<381>,5:7], [@58,343:345='cec',<381>,5:80]], unnamed_0=[[@15,73:73=')',<288>,1:73]]}, table_dictionary={<[Enrollment Services].[Client Entering Class]>={agg_col=[[@12,62:64='cec',<381>,1:62]], <select column>=[[@5,26:28='cec',<381>,1:26], [@44,270:272='cec',<381>,5:7], [@58,343:345='cec',<381>,5:80]], non_variable_col=[[@21,143:145='cec',<381>,3:9], [@29,192:194='cec',<381>,4:7]], <where column>=[[@25,165:167='cec',<381>,3:31], [@36,229:231='cec',<381>,4:44]]}}, grouped_by=[{name=non_variable_col, table_ref=cec}, {substitution={name=<where column>, type=column}, table_ref=cec}], filters=[{name=non_variable_col, table_ref=cec}, {substitution={name=<where column>, type=column}, table_ref=cec}], interface={<select column>=[{substitution={name=<select column>, type=column}, table_ref=cec}], unnamed_0=[{name=agg_col, table_ref=cec}]}, table_alias={cec=<[Enrollment Services].[Client Entering Class]>}}, filters=[{substitution={name=<select column>, type=column}, table_ref=cec}, {name=dummy_col, table_ref=d}], interface={<select column>=[{substitution={name=<select column>, type=column}, table_ref=cec}], dummy_col2=[{name=dummy_col2, table_ref=d}]}, table_alias={wrapped=query0}}}",
							extractor.getSymbolTable().toString());
		}

		@Test
		public void getSubstitutionColumnVariableV13CteWrappedQualifyVariantWithJoinOnSelectColumnTest() {
			// Column Variable Test - V5 wrapped as a CTE with outer wildcard select
			String query = "with wrapped as ( " +
					" select cec.<select column> " +
					"\nfrom <[Enrollment Services].[Client Entering Class]> cec " +
					"\nqualify row_number() over (partition by cec.non_variable_col, cec.<where column> order by cec.non_variable_col) = 1 " +
					") " +
					"\nselect cec.<select column>, d.dummy_col2 from wrapped cec join dummy_table d on cec.<select column> = d.dummy_col";
			final SQLSelectParserParser parser = parse(query);
			SqlParseEventWalker extractor = runParsertest(query, parser);
			assertNoWalkerDiagnostics(extractor);

			// broken purposefully until we come back to generate a suite of tests around variable column recognition
			Assert.assertEquals("AST is wrong", "{SQL={with={1={cte={select={1={column={substitution={name=<select column>, type=column}, table_ref=cec}}}, from={table={alias=cec, substitution={name=<[Enrollment Services].[Client Entering Class]>, parts={1=[Enrollment Services], 2=[Client Entering Class]}, type=tuple}}}, qualify={condition={left={window_function={over={partition_by={1={column={name=non_variable_col, table_ref=cec}}, 2={column={substitution={name=<where column>, type=column}, table_ref=cec}}}, orderby={1={null_order=null, predicand={column={name=non_variable_col, table_ref=cec}}, sort_order=ASC}}}, function={function_name=row_number, parameters=null}}}, right={literal=1}, operator==}}}, alias=wrapped}}, query={select={1={column={substitution={name=<select column>, type=column}, table_ref=cec}}, 2={column={name=dummy_col2, table_ref=d}}}, from={join={1={table={alias=cec, table=wrapped}}, 2={join=join, on={condition={left={column={substitution={name=<select column>, type=column}, table_ref=cec}}, right={column={name=dummy_col, table_ref=d}}, operator==}}}, 3={table={alias=d, table=dummy_table}}}}}}}",
							extractor.getAsTree().toString());
			Assert.assertEquals("Interface is wrong", "[<select column>, dummy_col2]",
							extractor.getInterface().toString());
			Assert.assertEquals("Substitution List is wrong", "{<[Enrollment Services].[Client Entering Class]>=tuple, <select column>=column, <where column>=column}",
							extractor.getSubstitutionsMap().toString());
			Assert.assertEquals("Table Dictionary is wrong", "{<[Enrollment Services].[Client Entering Class]>={<select column>=[[@5,26:28='cec',<381>,1:26], [@36,231:233='cec',<381>,4:7], [@50,304:306='cec',<381>,4:80]], non_variable_col=[[@19,145:147='cec',<381>,3:40], [@28,195:197='cec',<381>,3:90]], <where column>=[[@23,167:169='cec',<381>,3:62]]}, dummy_table={dummy_col=[[@54,326:326='d',<381>,4:102]], dummy_col2=[[@40,252:252='d',<381>,4:28]]}}",
							extractor.getTableColumnDictionaryMap().toString());
			Assert.assertEquals("Query Column Dictionary is wrong", "{query0={<select column>=[[@7,30:44='<select column>',<327>,1:30], [@36,231:233='cec',<381>,4:7], [@50,304:306='cec',<381>,4:80]]}, query1={<select column>=[[@38,235:249='<select column>',<327>,4:11]], dummy_col2=[[@42,254:263='dummy_col2',<381>,4:30]]}}",
							extractor.getQueryColumnDictionaryMap().toString());
			Assert.assertEquals("Symbol Table is wrong", "{query1={query_dictionary={<select column>=[[@38,235:249='<select column>',<327>,4:11]], dummy_col2=[[@42,254:263='dummy_col2',<381>,4:30]]}, table_dictionary={dummy_table={dummy_col=[[@54,326:326='d',<381>,4:102]], dummy_col2=[[@40,252:252='d',<381>,4:28]]}}, cte_list={wrapped=query0, cec=query0}, def_query0={query_dictionary={<select column>=[[@7,30:44='<select column>',<327>,1:30], [@36,231:233='cec',<381>,4:7], [@50,304:306='cec',<381>,4:80]]}, table_dictionary={<[Enrollment Services].[Client Entering Class]>={<select column>=[[@5,26:28='cec',<381>,1:26], [@36,231:233='cec',<381>,4:7], [@50,304:306='cec',<381>,4:80]], non_variable_col=[[@19,145:147='cec',<381>,3:40], [@28,195:197='cec',<381>,3:90]], <where column>=[[@23,167:169='cec',<381>,3:62]]}}, filters=[{name=non_variable_col, table_ref=cec}, {substitution={name=<where column>, type=column}, table_ref=cec}], interface={<select column>=[{substitution={name=<select column>, type=column}, table_ref=cec}]}, table_alias={cec=<[Enrollment Services].[Client Entering Class]>}}, filters=[{substitution={name=<select column>, type=column}, table_ref=cec}, {name=dummy_col, table_ref=d}], interface={<select column>=[{substitution={name=<select column>, type=column}, table_ref=cec}], dummy_col2=[{name=dummy_col2, table_ref=d}]}, table_alias={wrapped=query0}}}",
							extractor.getSymbolTable().toString());
		}

		@Test
		public void getSubstitutionColumnVariableV14CteWrappedSecondJoinOnVariantWithJoinOnSelectColumnTest() {
			// Column Variable Test - V6 wrapped as a CTE with outer wildcard select
			String query = "with wrapped as ( " +
					" select cec.<select column> " +
					"\nfrom <[Enrollment Services].[Client Entering Class]> cec " +
					"\njoin <[Enrollment Services].[Client Entering Class]> cec2 on cec.non_variable_col = cec2.non_variable_col " +
					"\njoin <[Enrollment Services].[Client Entering Class]> cec3 on cec.<where column> = cec3.<where column> " +
					") " +
					"\nselect cec.<select column>, d.dummy_col2 from wrapped cec join dummy_table d on cec.<select column> = d.dummy_col";
			final SQLSelectParserParser parser = parse(query);
			SqlParseEventWalker extractor = runParsertest(query, parser);
			assertNoWalkerDiagnostics(extractor);

			// broken purposefully until we come back to generate a suite of tests around variable column recognition
			Assert.assertEquals("AST is wrong", "{SQL={with={1={cte={select={1={column={substitution={name=<select column>, type=column}, table_ref=cec}}}, from={join={1={table={alias=cec, substitution={name=<[Enrollment Services].[Client Entering Class]>, parts={1=[Enrollment Services], 2=[Client Entering Class]}, type=tuple}}}, 2={join=join, on={condition={left={column={name=non_variable_col, table_ref=cec}}, right={column={name=non_variable_col, table_ref=cec2}}, operator==}}}, 3={table={alias=cec2, substitution={name=<[Enrollment Services].[Client Entering Class]>, parts={1=[Enrollment Services], 2=[Client Entering Class]}, type=tuple}}}, 4={join=join, on={condition={left={column={substitution={name=<where column>, type=column}, table_ref=cec}}, right={column={substitution={name=<where column>, type=column}, table_ref=cec3}}, operator==}}}, 5={table={alias=cec3, substitution={name=<[Enrollment Services].[Client Entering Class]>, parts={1=[Enrollment Services], 2=[Client Entering Class]}, type=tuple}}}}}}, alias=wrapped}}, query={select={1={column={substitution={name=<select column>, type=column}, table_ref=cec}}, 2={column={name=dummy_col2, table_ref=d}}}, from={join={1={table={alias=cec, table=wrapped}}, 2={join=join, on={condition={left={column={substitution={name=<select column>, type=column}, table_ref=cec}}, right={column={name=dummy_col, table_ref=d}}, operator==}}}, 3={table={alias=d, table=dummy_table}}}}}}}",
							extractor.getAsTree().toString());
			Assert.assertEquals("Interface is wrong", "[<select column>, dummy_col2]",
							extractor.getInterface().toString());
			Assert.assertEquals("Substitution List is wrong", "{<[Enrollment Services].[Client Entering Class]>=tuple, <select column>=column, <where column>=column}",
							extractor.getSubstitutionsMap().toString());
			Assert.assertEquals("Table Dictionary is wrong", "{<[Enrollment Services].[Client Entering Class]>={<select column>=[[@5,26:28='cec',<381>,1:26], [@35,324:326='cec',<381>,5:7], [@49,397:399='cec',<381>,5:80]], non_variable_col=[[@15,166:168='cec',<381>,3:61], [@19,189:192='cec2',<381>,3:84]], <where column>=[[@30,294:297='cec3',<381>,4:82], [@26,273:275='cec',<381>,4:61]]}, dummy_table={dummy_col=[[@53,419:419='d',<381>,5:102]], dummy_col2=[[@39,345:345='d',<381>,5:28]]}}",
							extractor.getTableColumnDictionaryMap().toString());
			Assert.assertEquals("Query Column Dictionary is wrong", "{query0={<select column>=[[@7,30:44='<select column>',<327>,1:30], [@35,324:326='cec',<381>,5:7], [@49,397:399='cec',<381>,5:80]]}, query1={<select column>=[[@37,328:342='<select column>',<327>,5:11]], dummy_col2=[[@41,347:356='dummy_col2',<381>,5:30]]}}",
							extractor.getQueryColumnDictionaryMap().toString());
			Assert.assertEquals("Symbol Table is wrong", "{query1={query_dictionary={<select column>=[[@37,328:342='<select column>',<327>,5:11]], dummy_col2=[[@41,347:356='dummy_col2',<381>,5:30]]}, table_dictionary={dummy_table={dummy_col=[[@53,419:419='d',<381>,5:102]], dummy_col2=[[@39,345:345='d',<381>,5:28]]}}, cte_list={wrapped=query0, cec=query0}, def_query0={query_dictionary={<select column>=[[@7,30:44='<select column>',<327>,1:30], [@35,324:326='cec',<381>,5:7], [@49,397:399='cec',<381>,5:80]]}, table_dictionary={<[Enrollment Services].[Client Entering Class]>={<select column>=[[@5,26:28='cec',<381>,1:26], [@35,324:326='cec',<381>,5:7], [@49,397:399='cec',<381>,5:80]], non_variable_col=[[@15,166:168='cec',<381>,3:61], [@19,189:192='cec2',<381>,3:84]], <where column>=[[@30,294:297='cec3',<381>,4:82], [@26,273:275='cec',<381>,4:61]]}}, filters=[{name=non_variable_col, table_ref=cec}, {name=non_variable_col, table_ref=cec2}, {substitution={name=<where column>, type=column}, table_ref=cec}, {substitution={name=<where column>, type=column}, table_ref=cec3}], interface={<select column>=[{substitution={name=<select column>, type=column}, table_ref=cec}]}, table_alias={cec=<[Enrollment Services].[Client Entering Class]>, cec3=<[Enrollment Services].[Client Entering Class]>, cec2=<[Enrollment Services].[Client Entering Class]>}}, filters=[{substitution={name=<select column>, type=column}, table_ref=cec}, {name=dummy_col, table_ref=d}], interface={<select column>=[{substitution={name=<select column>, type=column}, table_ref=cec}], dummy_col2=[{name=dummy_col2, table_ref=d}]}, table_alias={wrapped=query0}}}",
							extractor.getSymbolTable().toString());
		}

		@Test
		public void getSubstitutionColumnVariableV15CteWrappedSelfUnionVariantWithJoinOnSelectColumnTest() {
			// Column Variable Test - V7 wrapped as a CTE with outer wildcard select
			String query = "with wrapped as ( " +
					" select cec.<select column> " +
					"\nfrom <[Enrollment Services].[Client Entering Class]> cec " +
					"\nwhere cec.non_variable_col is not null and cec.<where column> = 'abc' " +
					"\nunion " +
					"\nselect cec.<select column> " +
					"\nfrom <[Enrollment Services].[Client Entering Class]> cec " +
					"\nwhere cec.non_variable_col is not null and cec.<where column> = 'abc' " +
					") " +
					"\nselect cec.<select column>, d.dummy_col2 from wrapped cec join dummy_table d on cec.<select column> = d.dummy_col";
			final SQLSelectParserParser parser = parse(query);
			SqlParseEventWalker extractor = runParsertest(query, parser);
			assertNoWalkerDiagnostics(extractor);

			// broken purposefully until we come back to generate a suite of tests around variable column recognition
			Assert.assertEquals("AST is wrong", "{SQL={with={1={cte={union={1={select={1={column={substitution={name=<select column>, type=column}, table_ref=cec}}}, from={table={alias=cec, substitution={name=<[Enrollment Services].[Client Entering Class]>, parts={1=[Enrollment Services], 2=[Client Entering Class]}, type=tuple}}}, where={and={1={condition={left={column={name=non_variable_col, table_ref=cec}}, operator=is not null}}, 2={condition={left={column={substitution={name=<where column>, type=column}, table_ref=cec}}, right={literal='abc'}, operator==}}}}}, 2={union={qualifier=null, operator=union}}, 3={select={1={column={substitution={name=<select column>, type=column}, table_ref=cec}}}, from={table={alias=cec, substitution={name=<[Enrollment Services].[Client Entering Class]>, parts={1=[Enrollment Services], 2=[Client Entering Class]}, type=tuple}}}, where={and={1={condition={left={column={name=non_variable_col, table_ref=cec}}, operator=is not null}}, 2={condition={left={column={substitution={name=<where column>, type=column}, table_ref=cec}}, right={literal='abc'}, operator==}}}}}}}, alias=wrapped}}, query={select={1={column={substitution={name=<select column>, type=column}, table_ref=cec}}, 2={column={name=dummy_col2, table_ref=d}}}, from={join={1={table={alias=cec, table=wrapped}}, 2={join=join, on={condition={left={column={substitution={name=<select column>, type=column}, table_ref=cec}}, right={column={name=dummy_col, table_ref=d}}, operator==}}}, 3={table={alias=d, table=dummy_table}}}}}}}",
							extractor.getAsTree().toString());
			Assert.assertEquals("Interface is wrong", "[<select column>, dummy_col2]",
							extractor.getInterface().toString());
			Assert.assertEquals("Substitution List is wrong", "{<[Enrollment Services].[Client Entering Class]>=tuple, <select column>=column, <where column>=column}",
							extractor.getSubstitutionsMap().toString());
			Assert.assertEquals("Table Dictionary is wrong", "{<[Enrollment Services].[Client Entering Class]>={<select column>=[[@5,26:28='cec',<381>,1:26], [@26,190:192='cec',<381>,5:7], [@47,349:351='cec',<381>,8:7], [@61,422:424='cec',<381>,8:80]], non_variable_col=[[@12,111:113='cec',<381>,3:6], [@33,275:277='cec',<381>,7:6]], <where column>=[[@19,148:150='cec',<381>,3:43], [@40,312:314='cec',<381>,7:43]]}, dummy_table={dummy_col=[[@65,444:444='d',<381>,8:102]], dummy_col2=[[@51,370:370='d',<381>,8:28]]}}",
							extractor.getTableColumnDictionaryMap().toString());
			Assert.assertEquals("Query Column Dictionary is wrong", "{query0={<select column>=[[@7,30:44='<select column>',<327>,1:30]]}, query1={<select column>=[[@28,194:208='<select column>',<327>,5:11]]}, query3={<select column>=[[@49,353:367='<select column>',<327>,8:11]], dummy_col2=[[@53,372:381='dummy_col2',<381>,8:30]]}}",
							extractor.getQueryColumnDictionaryMap().toString());
			Assert.assertEquals("Symbol Table is wrong", "{query3={query_dictionary={<select column>=[[@49,353:367='<select column>',<327>,8:11]], dummy_col2=[[@53,372:381='dummy_col2',<381>,8:30]]}, def_union2={query0={query_dictionary={<select column>=[[@7,30:44='<select column>',<327>,1:30]]}, table_dictionary={<[Enrollment Services].[Client Entering Class]>={<select column>=[[@5,26:28='cec',<381>,1:26], [@26,190:192='cec',<381>,5:7], [@47,349:351='cec',<381>,8:7], [@61,422:424='cec',<381>,8:80]], non_variable_col=[[@12,111:113='cec',<381>,3:6], [@33,275:277='cec',<381>,7:6]], <where column>=[[@19,148:150='cec',<381>,3:43], [@40,312:314='cec',<381>,7:43]]}}, filters=[{name=non_variable_col, table_ref=cec}, {substitution={name=<where column>, type=column}, table_ref=cec}], interface={<select column>=[{substitution={name=<select column>, type=column}, table_ref=cec}]}, table_alias={cec=<[Enrollment Services].[Client Entering Class]>}}, interface={<select column>=query_column}, query1={query_dictionary={<select column>=[[@28,194:208='<select column>',<327>,5:11]]}, table_dictionary={<[Enrollment Services].[Client Entering Class]>={<select column>=[[@26,190:192='cec',<381>,5:7]], non_variable_col=[[@33,275:277='cec',<381>,7:6]], <where column>=[[@40,312:314='cec',<381>,7:43]]}}, filters=[{name=non_variable_col, table_ref=cec}, {substitution={name=<where column>, type=column}, table_ref=cec}], interface={<select column>=[{substitution={name=<select column>, type=column}, table_ref=cec}]}, table_alias={cec=<[Enrollment Services].[Client Entering Class]>}}}, table_dictionary={dummy_table={dummy_col=[[@65,444:444='d',<381>,8:102]], dummy_col2=[[@51,370:370='d',<381>,8:28]]}}, cte_list={wrapped=union2, cec=union2}, filters=[{substitution={name=<select column>, type=column}, table_ref=cec}, {name=dummy_col, table_ref=d}], interface={<select column>=[{substitution={name=<select column>, type=column}, table_ref=cec}], dummy_col2=[{name=dummy_col2, table_ref=d}]}, table_alias={wrapped=union2}}}",
							extractor.getSymbolTable().toString());
		}

		@Test
		public void getSubstitutionColumnVariableV16CteWrappedSelfIntersectionVariantWithJoinOnSelectColumnTest() {
			// Column Variable Test - V8 wrapped as a CTE with outer wildcard select
			String query = "with wrapped as ( " +
					" select cec.<select column> " +
					"\nfrom <[Enrollment Services].[Client Entering Class]> cec " +
					"\nwhere cec.non_variable_col is not null and cec.<where column> = 'abc' " +
					"\nintersect " +
					"\nselect cec.<select column> " +
					"\nfrom <[Enrollment Services].[Client Entering Class]> cec " +
					"\nwhere cec.non_variable_col is not null and cec.<where column> = 'abc' " +
					") " +
					"\nselect cec.<select column>, d.dummy_col2 from wrapped cec join dummy_table d on cec.<select column> = d.dummy_col";
			final SQLSelectParserParser parser = parse(query);
			SqlParseEventWalker extractor = runParsertest(query, parser);
			assertNoWalkerDiagnostics(extractor);

			// broken purposefully until we come back to generate a suite of tests around variable column recognition
			Assert.assertEquals("AST is wrong", "{SQL={with={1={cte={intersect={1={select={1={column={substitution={name=<select column>, type=column}, table_ref=cec}}}, from={table={alias=cec, substitution={name=<[Enrollment Services].[Client Entering Class]>, parts={1=[Enrollment Services], 2=[Client Entering Class]}, type=tuple}}}, where={and={1={condition={left={column={name=non_variable_col, table_ref=cec}}, operator=is not null}}, 2={condition={left={column={substitution={name=<where column>, type=column}, table_ref=cec}}, right={literal='abc'}, operator==}}}}}, 2={intersect={qualifier=null, operator=intersect}}, 3={select={1={column={substitution={name=<select column>, type=column}, table_ref=cec}}}, from={table={alias=cec, substitution={name=<[Enrollment Services].[Client Entering Class]>, parts={1=[Enrollment Services], 2=[Client Entering Class]}, type=tuple}}}, where={and={1={condition={left={column={name=non_variable_col, table_ref=cec}}, operator=is not null}}, 2={condition={left={column={substitution={name=<where column>, type=column}, table_ref=cec}}, right={literal='abc'}, operator==}}}}}}}, alias=wrapped}}, query={select={1={column={substitution={name=<select column>, type=column}, table_ref=cec}}, 2={column={name=dummy_col2, table_ref=d}}}, from={join={1={table={alias=cec, table=wrapped}}, 2={join=join, on={condition={left={column={substitution={name=<select column>, type=column}, table_ref=cec}}, right={column={name=dummy_col, table_ref=d}}, operator==}}}, 3={table={alias=d, table=dummy_table}}}}}}}",
							extractor.getAsTree().toString());
			Assert.assertEquals("Interface is wrong", "[<select column>, dummy_col2]",
							extractor.getInterface().toString());
			Assert.assertEquals("Substitution List is wrong", "{<[Enrollment Services].[Client Entering Class]>=tuple, <select column>=column, <where column>=column}",
							extractor.getSubstitutionsMap().toString());
			Assert.assertEquals("Table Dictionary is wrong", "{<[Enrollment Services].[Client Entering Class]>={<select column>=[[@5,26:28='cec',<381>,1:26], [@26,194:196='cec',<381>,5:7], [@47,353:355='cec',<381>,8:7], [@61,426:428='cec',<381>,8:80]], non_variable_col=[[@12,111:113='cec',<381>,3:6], [@33,279:281='cec',<381>,7:6]], <where column>=[[@19,148:150='cec',<381>,3:43], [@40,316:318='cec',<381>,7:43]]}, dummy_table={dummy_col=[[@65,448:448='d',<381>,8:102]], dummy_col2=[[@51,374:374='d',<381>,8:28]]}}",
							extractor.getTableColumnDictionaryMap().toString());
			Assert.assertEquals("Query Column Dictionary is wrong", "{query0={<select column>=[[@7,30:44='<select column>',<327>,1:30]]}, query1={<select column>=[[@28,198:212='<select column>',<327>,5:11]]}, query3={<select column>=[[@49,357:371='<select column>',<327>,8:11]], dummy_col2=[[@53,376:385='dummy_col2',<381>,8:30]]}}",
							extractor.getQueryColumnDictionaryMap().toString());
			Assert.assertEquals("Symbol Table is wrong", "{query3={query_dictionary={<select column>=[[@49,357:371='<select column>',<327>,8:11]], dummy_col2=[[@53,376:385='dummy_col2',<381>,8:30]]}, table_dictionary={dummy_table={dummy_col=[[@65,448:448='d',<381>,8:102]], dummy_col2=[[@51,374:374='d',<381>,8:28]]}}, def_intersect2={query0={query_dictionary={<select column>=[[@7,30:44='<select column>',<327>,1:30]]}, table_dictionary={<[Enrollment Services].[Client Entering Class]>={<select column>=[[@5,26:28='cec',<381>,1:26], [@26,194:196='cec',<381>,5:7], [@47,353:355='cec',<381>,8:7], [@61,426:428='cec',<381>,8:80]], non_variable_col=[[@12,111:113='cec',<381>,3:6], [@33,279:281='cec',<381>,7:6]], <where column>=[[@19,148:150='cec',<381>,3:43], [@40,316:318='cec',<381>,7:43]]}}, filters=[{name=non_variable_col, table_ref=cec}, {substitution={name=<where column>, type=column}, table_ref=cec}], interface={<select column>=[{substitution={name=<select column>, type=column}, table_ref=cec}]}, table_alias={cec=<[Enrollment Services].[Client Entering Class]>}}, interface={<select column>=query_column}, query1={query_dictionary={<select column>=[[@28,198:212='<select column>',<327>,5:11]]}, table_dictionary={<[Enrollment Services].[Client Entering Class]>={<select column>=[[@26,194:196='cec',<381>,5:7]], non_variable_col=[[@33,279:281='cec',<381>,7:6]], <where column>=[[@40,316:318='cec',<381>,7:43]]}}, filters=[{name=non_variable_col, table_ref=cec}, {substitution={name=<where column>, type=column}, table_ref=cec}], interface={<select column>=[{substitution={name=<select column>, type=column}, table_ref=cec}]}, table_alias={cec=<[Enrollment Services].[Client Entering Class]>}}}, cte_list={wrapped=intersect2, cec=intersect2}, filters=[{substitution={name=<select column>, type=column}, table_ref=cec}, {name=dummy_col, table_ref=d}], interface={<select column>=[{substitution={name=<select column>, type=column}, table_ref=cec}], dummy_col2=[{name=dummy_col2, table_ref=d}]}, table_alias={wrapped=intersect2}}}",
							extractor.getSymbolTable().toString());
		}

	@Test
	public void descAsColumnTest() {
		final String query = "SELECT desc from tab1";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=desc, table_ref=null}}}, from={table={alias=null, table=tab1}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[desc]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={desc=[[@1,7:10='desc',<77>,1:7]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={desc=[[@1,7:10='desc',<77>,1:7]]}}",
						extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={query_dictionary={desc=[[@1,7:10='desc',<77>,1:7]]}, table_dictionary={tab1={desc=[[@1,7:10='desc',<77>,1:7]]}}, interface={desc=[{name=desc, table_ref=tab1}]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void ascAsColumnTest() {
		final String query = "SELECT asc from tab1";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=asc, table_ref=null}}}, from={table={alias=null, table=tab1}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[asc]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={asc=[[@1,7:9='asc',<60>,1:7]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={asc=[[@1,7:9='asc',<60>,1:7]]}}",
						extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={query_dictionary={asc=[[@1,7:9='asc',<60>,1:7]]}, table_dictionary={tab1={asc=[[@1,7:9='asc',<60>,1:7]]}}, interface={asc=[{name=asc, table_ref=tab1}]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void rankAsColumnTest() {
		final String query = "SELECT rank from tab1";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=rank, table_ref=null}}}, from={table={alias=null, table=tab1}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[rank]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={rank=[[@1,7:10='rank',<128>,1:7]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={rank=[[@1,7:10='rank',<128>,1:7]]}}",
						extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={query_dictionary={rank=[[@1,7:10='rank',<128>,1:7]]}, table_dictionary={tab1={rank=[[@1,7:10='rank',<128>,1:7]]}}, interface={rank=[{name=rank, table_ref=tab1}]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void variation2ColumnVariableTest() {
		// Qualified column variable should fail because table alias tab2 is not in scope.
		final String query = "SELECT apple, tab2.<other> from tab1";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=apple, table_ref=null}}, 2={column={substitution={name=<other>, type=column}, table_ref=tab2}}}, from={table={alias=null, table=tab1}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[apple, <other>]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<other>=column}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={apple=[[@1,7:11='apple',<381>,1:7]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={apple=[[@1,7:11='apple',<381>,1:7]], <other>=[[@5,19:25='<other>',<327>,1:19]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={query_dictionary={apple=[[@1,7:11='apple',<381>,1:7]], <other>=[[@5,19:25='<other>',<327>,1:19]]}, table_dictionary={tab1={apple=[[@1,7:11='apple',<381>,1:7]]}}, interface={apple=[{name=apple, table_ref=tab1}], <other>=[{substitution={name=<other>, type=column}, table_ref=tab2}]}}}",
				extractor.getSymbolTable().toString());

		Snippet snippet = extractor.getSnippet();
		assertFatalDiagnosticAtPosition(snippet, "QUALIFIED_COLUMN_NOT_FOUND_IN_TABLE",
				"Source Table not found for Column '<other>' at (l:1 c:14). No alias or table called 'tab2'.",
				"<other>", 1, 14);
	}


	@Test
	public void variation3ColumnVariableTest() {
		// Correlated subquery under join
		final String query = "select apple from "
			+" (SELECT apple from tab1 where tab2.<other> > 20) a"
			+ " join tab2 on a.apple = tab2.apple";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=apple, table_ref=null}}}, from={join={1={table={alias=a, query={select={1={column={name=apple, table_ref=null}}}, from={table={alias=null, table=tab1}}, where={condition={left={column={substitution={name=<other>, type=column}, table_ref=tab2}}, right={literal=20}, operator=>}}}}}, 2={join=join, on={condition={left={column={name=apple, table_ref=a}}, right={column={name=apple, table_ref=tab2}}, operator==}}}, 3={table={alias=null, table=tab2}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[apple]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<other>=column}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={apple=[[@5,27:31='apple',<381>,1:27]]}, tab2={apple=[[@23,93:96='tab2',<381>,1:93]], <other>=[[@9,49:52='tab2',<381>,1:49]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={apple=[[@5,27:31='apple',<381>,1:27], [@19,83:83='a',<381>,1:83]]}, query1={apple=[[@1,7:11='apple',<381>,1:7]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query1={query_dictionary={apple=[[@1,7:11='apple',<381>,1:7]]}, table_dictionary={tab2={apple=[[@23,93:96='tab2',<381>,1:93]]}}, def_query0={query_dictionary={apple=[[@5,27:31='apple',<381>,1:27], [@19,83:83='a',<381>,1:83]]}, table_dictionary={tab1={apple=[[@5,27:31='apple',<381>,1:27]]}}, filters=[{substitution={name=<other>, type=column}, table_ref=tab2}], interface={apple=[{name=apple, table_ref=tab1}]}}, filters=[{name=apple, table_ref=a}, {name=apple, table_ref=tab2}], interface={apple=[{name=apple, table_ref=null}]}, table_alias={a=query0}}}",
				extractor.getSymbolTable().toString());

		Snippet snippet = extractor.getSnippet();
		assertDiagnosticCountBySeverity(snippet, "AMBIGUOUS_COLUMN_REFERENCE", ParseDiagnostic.Severity.SEVERE_WARNING, "Ambiguous column reference 'apple'", "apple", 1);
		assertDiagnosticAtPosition(
				snippet,
				"AMBIGUOUS_COLUMN_REFERENCE",
				ParseDiagnostic.Severity.SEVERE_WARNING,
				"Ambiguous column reference 'apple' at (l:1 c:7). Possible sources: [tab2, query0]",
				"apple",
				1,
				7);
	}


	@Test
	public void simpleParseTest() {
		final String query = "SELECT aa.scbcrse_coll_code, aa.* FROM scbcrse as aa, mycrse as courses "
				+ " WHERE not aa.scbcrse_subj_code = courses.subj_code "
				+ " AND (aa.scbcrse_crse_numb = courses.crse_numb " + " or aa.scbcrse_crse_numb = courses.crse_numb) ";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
	}


	@Test
	public void simpleFromStatementTest() {

		final String query = " SELECT * FROM tab1 ";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
	}


	@Test
	public void simpleFromListType1ParseTest() {

		final String query = " SELECT * FROM third, fourth, fifth, sixth ";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
	}


	@Test
	public void simpleFromListType2ParseTest() {

		final String query = " SELECT * FROM third cross join fourth union join fifth natural join sixth ";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
	}


	@Test
	public void arithmeticSimpleParseTest() {
		final String query = "SELECT (6 * 9 - 100 + a)  FROM scbcrse aa " + " WHERE aa.scbcrse_subj_code is not null ";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
	}


	@Test
	public void arithmeticParseTest() {

		final String query = "SELECT -(aa.scbcrse_coll_code * 6 - other) FROM scbcrse aa "
				+ " WHERE aa.scbcrse_subj_code is not null ";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
	}


	@Test
	public void arithmeticRunningAdditionTest() {

		final String query = "SELECT 5+8+9-2+9 FROM scbcrse aa " + " WHERE aa.scbcrse_subj_code is not null ";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
	}


	@Test
	public void arithmeticRunningMultiplicationTest() {

		final String query = "SELECT 5*8*9/2*9 FROM scbcrse aa " + " WHERE aa.scbcrse_subj_code is not null ";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
	}


	@Test
	public void arithmeticRunningMultiplicationWithParenTest() {

		final String query = "SELECT 5*(8*9)/(2*9) FROM scbcrse aa " + " WHERE aa.scbcrse_subj_code is not null ";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
	}


	@Test
	public void arithmeticWithAliasParseTest() {

		final String query = "SELECT -(aa.scbcrse_coll_code * 6 - other) as item FROM scbcrse aa "
				+ " WHERE aa.scbcrse_subj_code is not null ";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
	}


	@Test
	public void nestedSymbolTableConstructionTest() {
		final String query = " SELECT b.att1, b.att2 " + " from (SELECT a.col1 as att1, a.col2 as att2 "
				+ " FROM tab1 as a" + " WHERE a.col1 <> a.col3 " + " ) AS b ";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
	}


	@Test
	public void siblingPredicandSubqueriesCorrelatedSubqueriesParseTest() {
		// Part of this is the problem of correlated subquery. Each predicand is referencing a field from the outer join's 
		// from statement, but we haven't implemented that capability yet.
		final String query = "SELECT " 
				+ " ( "
				+ " SELECT CASE WHEN sgbstdn.sgbstdn_resd_code in ('G') THEN 'Y' ELSE 'N' END "
				+ " FROM sgbstdn " 
				+ "\n JOIN ( " 
				+ " SELECT sgbstdn_pidm, max(sgbstdn_term_code_eff) AS max_term "
				+ " FROM sgbstdn " 
				+ " WHERE sgbstdn_levl_code = 'US' GROUP BY sgbstdn_pidm "
				+ " ) m on sgbstdn.sgbstdn_pidm = m.sgbstdn_pidm and sgbstdn.sgbstdn_term_code_eff = m.max_term "
				+ " WHERE sgbstdn.sgbstdn_pidm = population.spriden_pidm ) AS INTERNATIONAL_IND "
				+ " , \n ( " 
				+ " SELECT SGBSTDN_ADMT_CODE  FROM sgbstdn "
				+ " WHERE sgbstdn_pidm = population.spriden_pidm " 
				+ " AND sgbstdn_levl_code = 'US'			 "
				+ " AND sgbstdn_term_code_eff = " 
				+ "\n ( SELECT max(sgbstdn_term_code_eff) FROM sgbstdn "
				+ " WHERE sgbstdn_pidm = population.spriden_pidm  AND sgbstdn_levl_code = 'US')		 "
				+ " ) AS STUDENT_ADMIT_CD "
				+ " ,\n ( " 
				+ " SELECT CASE WHEN SGBSTDN_STST_CODE = 'AS' THEN 'Y' ELSE 'N' END "
				+ " FROM sgbstdn WHERE sgbstdn_pidm = population.spriden_pidm "
				+ " AND sgbstdn_levl_code = 'US' "
				+ " AND sgbstdn_term_code_eff =  " 
				+ "\n ( SELECT max(sgbstdn_term_code_eff)  FROM sgbstdn "
				+ " WHERE sgbstdn_pidm = population.spriden_pidm  	AND sgbstdn_levl_code = 'US')		 "
				+ " ) AS ACTIVE_IND "
				+ "\n FROM  spriden as population" ;

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
	}


	@Test
	public void jinjaTupleWithAliasTest() {
		String sql =  " SELECT cbsc.contact_key, rsc.cappex_id FROM {{ source('PDP_ALR_V2','rsc__cappex_contacts') }} AS rsc "
	+    "\n INNER JOIN  {{ source('PDP_ALR_V2_CONTACTS','prc__contacts_by_sourcecontacts_current') }} AS cbsc "
	+    "\nON rsc.sourcecontact_id = cbsc.sourcecontact_id "
	+	"\n    QUALIFY ROW_NUMBER() OVER ( PARTITION BY contact_key ORDER BY cbsc.contact_priority ASC) = 1";

		final SQLSelectParserParser parser = parse(sql);
			
		SqlParseEventWalker extractor = runParsertest(sql, parser);
		assertNoFatalErrors(extractor);

				Snippet snippet = extractor.getSnippet();
		assertDiagnosticCountBySeverity(
				snippet,
				"AMBIGUOUS_COLUMN_REFERENCE",
				ParseDiagnostic.Severity.SEVERE_WARNING,
				"Ambiguous column reference 'contact_key' at (l:4 c:45). Possible sources: [{{ source('pdp_alr_v2_contacts','prc__contacts_by_sourcecontacts_current') }}, {{ source('pdp_alr_v2','rsc__cappex_contacts') }}]",
				"contact_key",
				1);
		assertDiagnosticCountBySeverity(
				snippet,
				"UNRESOLVED_UNQUALIFIED_COLUMNS",
				ParseDiagnostic.Severity.ERROR,
				"Unresolved unqualified column reference(s): [contact_key [(l:4 c:45)]]",
				"contact_key",
				1);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=contact_key, table_ref=cbsc}}, 2={column={name=cappex_id, table_ref=rsc}}}, from={join={1={table={alias=rsc, substitution={name={{ source('PDP_ALR_V2','rsc__cappex_contacts') }}, parts={jinja_table={function_name=source, parameters={1={literal='PDP_ALR_V2'}, 2={literal='rsc__cappex_contacts'}}}}, type=tuple}}}, 2={join=INNER, on={condition={left={column={name=sourcecontact_id, table_ref=rsc}}, right={column={name=sourcecontact_id, table_ref=cbsc}}, operator==}}}, 3={table={alias=cbsc, substitution={name={{ source('PDP_ALR_V2_CONTACTS','prc__contacts_by_sourcecontacts_current') }}, parts={jinja_table={function_name=source, parameters={1={literal='PDP_ALR_V2_CONTACTS'}, 2={literal='prc__contacts_by_sourcecontacts_current'}}}}, type=tuple}}}}}, qualify={condition={left={window_function={over={partition_by={1={column={name=contact_key, table_ref=null}}}, orderby={1={null_order=null, predicand={column={name=contact_priority, table_ref=cbsc}}, sort_order=ASC}}}, function={function_name=ROW_NUMBER, parameters=null}}}, right={literal=1}, operator==}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[cappex_id, contact_key]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{{{ source('PDP_ALR_V2','rsc__cappex_contacts') }}=tuple, {{ source('PDP_ALR_V2_CONTACTS','prc__contacts_by_sourcecontacts_current') }}=tuple}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{{{ source('pdp_alr_v2','rsc__cappex_contacts') }}={cappex_id=[[@5,26:28='rsc',<381>,1:26]], sourcecontact_id=[[@32,206:208='rsc',<381>,3:3]]}, {{ source('pdp_alr_v2_contacts','prc__contacts_by_sourcecontacts_current') }}={contact_key=[[@1,8:11='cbsc',<381>,1:8]], sourcecontact_id=[[@36,229:232='cbsc',<381>,3:26]], contact_priority=[[@50,318:321='cbsc',<381>,4:66]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={cappex_id=[[@7,30:38='cappex_id',<381>,1:30]], contact_key=[[@3,13:23='contact_key',<381>,1:13]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={query_dictionary={cappex_id=[[@7,30:38='cappex_id',<381>,1:30]], contact_key=[[@3,13:23='contact_key',<381>,1:13]]}, table_dictionary={{{ source('pdp_alr_v2','rsc__cappex_contacts') }}={cappex_id=[[@5,26:28='rsc',<381>,1:26]], sourcecontact_id=[[@32,206:208='rsc',<381>,3:3]]}, {{ source('pdp_alr_v2_contacts','prc__contacts_by_sourcecontacts_current') }}={contact_key=[[@1,8:11='cbsc',<381>,1:8]], sourcecontact_id=[[@36,229:232='cbsc',<381>,3:26]], contact_priority=[[@50,318:321='cbsc',<381>,4:66]]}}, filters=[{name=sourcecontact_id, table_ref=rsc}, {name=sourcecontact_id, table_ref=cbsc}, {name=contact_key, table_ref=null}, {name=contact_priority, table_ref=cbsc}], interface={cappex_id=[{name=cappex_id, table_ref=rsc}], contact_key=[{name=contact_key, table_ref=cbsc}]}, table_alias={cbsc={{ source('PDP_ALR_V2_CONTACTS','prc__contacts_by_sourcecontacts_current') }}, rsc={{ source('PDP_ALR_V2','rsc__cappex_contacts') }}}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void jinjaTupleSingleSourceUnqualifiedContactKeyTest() {
		String sql =  " SELECT cbsc.contact_key FROM {{ source('PDP_ALR_V2_CONTACTS','prc__contacts_by_sourcecontacts_current') }} AS cbsc "
	+    "\n QUALIFY ROW_NUMBER() OVER ( PARTITION BY contact_key ORDER BY cbsc.contact_priority ASC) = 1";

		final SQLSelectParserParser parser = parse(sql);

		SqlParseEventWalker extractor = runParsertest(sql, parser);
		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=contact_key, table_ref=cbsc}}}, from={table={alias=cbsc, substitution={name={{ source('PDP_ALR_V2_CONTACTS','prc__contacts_by_sourcecontacts_current') }}, parts={jinja_table={function_name=source, parameters={1={literal='PDP_ALR_V2_CONTACTS'}, 2={literal='prc__contacts_by_sourcecontacts_current'}}}}, type=tuple}}}, qualify={condition={left={window_function={over={partition_by={1={column={name=contact_key, table_ref=null}}}, orderby={1={null_order=null, predicand={column={name=contact_priority, table_ref=cbsc}}, sort_order=ASC}}}, function={function_name=ROW_NUMBER, parameters=null}}}, right={literal=1}, operator==}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[contact_key]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{{{ source('PDP_ALR_V2_CONTACTS','prc__contacts_by_sourcecontacts_current') }}=tuple}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{{{ source('pdp_alr_v2_contacts','prc__contacts_by_sourcecontacts_current') }}={contact_key=[[@1,8:11='cbsc',<381>,1:8], [@23,159:169='contact_key',<381>,2:42]], contact_priority=[[@26,180:183='cbsc',<381>,2:63]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={contact_key=[[@3,13:23='contact_key',<381>,1:13]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={query_dictionary={contact_key=[[@3,13:23='contact_key',<381>,1:13]]}, table_dictionary={{{ source('pdp_alr_v2_contacts','prc__contacts_by_sourcecontacts_current') }}={contact_key=[[@1,8:11='cbsc',<381>,1:8], [@23,159:169='contact_key',<381>,2:42]], contact_priority=[[@26,180:183='cbsc',<381>,2:63]]}}, filters=[{name=contact_key, table_ref={{ source('pdp_alr_v2_contacts','prc__contacts_by_sourcecontacts_current') }}}, {name=contact_priority, table_ref=cbsc}], interface={contact_key=[{name=contact_key, table_ref=cbsc}]}, table_alias={cbsc={{ source('PDP_ALR_V2_CONTACTS','prc__contacts_by_sourcecontacts_current') }}}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void informaticaINFunctionStatementTest() {
		final String query = "select in(property,property,0) as colum from dual";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={function={parameters={1={column={name=property, table_ref=null}}, 2={column={name=property, table_ref=null}}, 3={literal=0}}, function_name=in}, alias=colum}}, from={table={alias=null, table=dual}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[colum]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{dual={property=[[@3,10:17='property',<381>,1:10], [@5,19:26='property',<381>,1:19]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={colum=[[@10,34:38='colum',<381>,1:34]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={query_dictionary={colum=[[@10,34:38='colum',<381>,1:34]]}, table_dictionary={dual={property=[[@3,10:17='property',<381>,1:10], [@5,19:26='property',<381>,1:19]]}}, interface={colum=[{name=property, table_ref=dual}]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void informaticaINFunctionConditionStatement1Test() {
		final String query = "select * from dual where in(property,property) in <in_list>";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=*, table_ref=*}}}, from={table={alias=null, table=dual}}, where={in={item={function={parameters={1={column={name=property, table_ref=null}}, 2={column={name=property, table_ref=null}}}, function_name=in}}, in_list={substitution={name=<in_list>, type=in_list}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<in_list>=in_list}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{dual={property=[[@7,28:35='property',<381>,1:28], [@9,37:44='property',<381>,1:37]], *=[[@1,7:7='*',<291>,1:7]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@1,7:7='*',<291>,1:7]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={query_dictionary={*=[[@1,7:7='*',<291>,1:7]]}, table_dictionary={dual={property=[[@7,28:35='property',<381>,1:28], [@9,37:44='property',<381>,1:37]], *=[[@1,7:7='*',<291>,1:7]]}}, filters=[{name=property, table_ref=dual}], interface={*=[{name=*, table_ref=*}]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void informaticaINFunctionConditionStatement2Test() {
		final String query = "select * from dual where in(property,property) in(0, 1)";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=*, table_ref=*}}}, from={table={alias=null, table=dual}}, where={in={item={function={parameters={1={column={name=property, table_ref=null}}, 2={column={name=property, table_ref=null}}}, function_name=in}}, in_list={list={1={literal=0}, 2={literal=1}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{dual={property=[[@7,28:35='property',<381>,1:28], [@9,37:44='property',<381>,1:37]], *=[[@1,7:7='*',<291>,1:7]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@1,7:7='*',<291>,1:7]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={query_dictionary={*=[[@1,7:7='*',<291>,1:7]]}, table_dictionary={dual={property=[[@7,28:35='property',<381>,1:28], [@9,37:44='property',<381>,1:37]], *=[[@1,7:7='*',<291>,1:7]]}}, filters=[{name=property, table_ref=dual}], interface={*=[{name=*, table_ref=*}]}}}",
				extractor.getSymbolTable().toString());
	}

}
