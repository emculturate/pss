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
		Assert.assertEquals("Symbol Table is wrong", "{query1={query_dictionary={a=[[@2,17:17='a',<381>,1:17]], b=[[@4,19:19='b',<381>,1:19]], c=[[@6,21:21='c',<381>,1:21]]}, def_query0={query_dictionary={b=[[@11,40:40='b',<381>,1:40]], c=[[@13,42:42='c',<381>,1:42]]}, table_dictionary={tab2={b=[[@11,40:40='b',<381>,1:40]], c=[[@13,42:42='c',<381>,1:42]]}}, interface={b=[{name=b, table_ref=tab2}], c=[{name=c, table_ref=tab2}]}}, interface={a=[{name=a, table_ref=null}], b=[{name=b, table_ref=query0}], c=[{name=c, table_ref=query0}]}, table_alias={tab1=query0}}}",
				extractor.getSymbolTable().toString());
				
		Snippet snippet = extractor.getSnippet();
		assertFatalDiagnosticCount(
				snippet,
				"UNQUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIASES",
				null,
				"a",
				1);
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
		Assert.assertEquals("Symbol Table is wrong", "{query1={query_dictionary={02_DEGREE_NAME=[[@11,59:72='02_DEGREE_NAME',<384>,1:59]], f1=[[@15,79:80='f1',<381>,1:79]], 01_DEGREE_CD=[[@5,26:37='01_DEGREE_CD',<384>,1:26]]}, def_query0={query_dictionary={Degree_Code=[[@1,7:9='sub',<381>,1:7]], Degree_Name=[[@7,40:42='sub',<381>,1:40]], *=[[@25,103:103='*',<291>,1:103]], f1=[[@21,97:98='f1',<381>,1:97], [@13,75:77='sub',<381>,1:75]]}, table_dictionary={pantoresultprod.hive_result_pit_5223_164728_46090704={*=[[@23,101:101='t',<381>,1:101]], f1=[[@19,95:95='t',<381>,1:95]]}}, interface={*=[{name=*, table_ref=t}], f1=[{name=f1, table_ref=t}]}, table_alias={t=pantoresultprod.hive_result_pit_5223_164728_46090704}}, interface={02_DEGREE_NAME=[{name=Degree_Name, table_ref=sub}], f1=[{name=f1, table_ref=sub}], 01_DEGREE_CD=[{name=Degree_Code, table_ref=sub}]}, table_alias={sub=query0}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void nestedQueryDemoTest() {
		// 3 scalar queries and 1 join subquery
		// Good Predicand, Inlist and subquery tests for fixing the symbol table collection
		final String query = "select "
		+"\n (select max((select avg(t) at from tt where tt.b > tab1.t and tt.e = ee.x2)) mxd "
		+"\n from ee where ee.x = (select tab1.<y_col>)) max_D,"
		+"\n tab1.a aa,"
		+"\n (select min(D) mnd from ee where ee.x = tab1.x  and tt.f = ee.x2) min_D,  kk.w" 
		+"\n from (select w from jj where jj.y = tab1.<z_col> and jj.m > tab2.e3) kk join tab1"
		+"\n where c in (select c, gg.y gg_y from ff where ff.z = tab1.<w_col>)";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		Snippet snippet = extractor.getSnippet();

		assertFatalDiagnosticAtPosition(
				snippet,
				"QUALIFIED_COLUMN_NOT_FOUND_IN_TABLE",
				"Source Table not found for Column 'e3' at (l:6 c:61). No alias or table called 'tab2'.",
				"e3",
				6,
				61);
		assertFatalDiagnosticAtPosition(
				snippet,
				"QUALIFIED_COLUMN_NOT_FOUND_IN_TABLE",
				"Source Table not found for Column 'y' at (l:7 c:23). No alias or table called 'gg'.",
				"y",
				7,
				23);
		assertFatalDiagnosticAtPosition(
				snippet,
				"QUALIFIED_COLUMN_NOT_FOUND_IN_TABLE",
				"Source Table not found for Column 'f' at (l:5 c:53). No alias or table called 'tt'.",
				"tt",
				5,
				53);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={lookup={from={table={alias=null, table=ee}}, where={condition={left={column={name=x, table_ref=ee}}, right={select={1={column={substitution={name=<y_col>, type=column}, table_ref=tab1}}}}, operator==}}, select={1={function={function_name=max, qualifier=null, parameters={select={1={function={function_name=avg, qualifier=null, parameters={column={name=t, table_ref=null}}}, alias=at}}, from={table={alias=null, table=tt}}, where={and={1={condition={left={column={name=b, table_ref=tt}}, right={column={name=t, table_ref=tab1}}, operator=>}}, 2={condition={left={column={name=e, table_ref=tt}}, right={column={name=x2, table_ref=ee}}, operator==}}}}}}, alias=mxd}}}, alias=max_D}, 2={column={name=a, table_ref=tab1}, alias=aa}, 3={lookup={from={table={alias=null, table=ee}}, where={and={1={condition={left={column={name=x, table_ref=ee}}, right={column={name=x, table_ref=tab1}}, operator==}}, 2={condition={left={column={name=f, table_ref=tt}}, right={column={name=x2, table_ref=ee}}, operator==}}}}, select={1={function={function_name=min, qualifier=null, parameters={column={name=D, table_ref=null}}}, alias=mnd}}}, alias=min_D}, 4={column={name=w, table_ref=kk}}}, from={join={1={table={alias=kk, query={select={1={column={name=w, table_ref=null}}}, from={table={alias=null, table=jj}}, where={and={1={condition={left={column={name=y, table_ref=jj}}, right={column={substitution={name=<z_col>, type=column}, table_ref=tab1}}, operator==}}, 2={condition={left={column={name=m, table_ref=jj}}, right={column={name=e3, table_ref=tab2}}, operator=>}}}}}}}, 2={join=join}, 3={table={alias=null, table=tab1}}}}, where={in={item={column={name=c, table_ref=null}}, in_list={select={1={column={name=c, table_ref=null}}, 2={column={name=y, table_ref=gg}, alias=gg_y}}, from={table={alias=null, table=ff}}, where={condition={left={column={name=z, table_ref=ff}}, right={column={substitution={name=<w_col>, type=column}, table_ref=tab1}}, operator==}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[aa, max_D, min_D, w]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<z_col>=column, <y_col>=column, <w_col>=column}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tt={b=[[@15,53:54='tt',<381>,2:45]], t=[[@9,33:33='t',<381>,2:25]], e=[[@23,71:72='tt',<381>,2:63]]}, ee={D=[[@58,168:168='D',<381>,5:13]], x=[[@36,106:107='ee',<381>,3:15], [@64,189:190='ee',<381>,5:34]], x2=[[@27,78:79='ee',<381>,2:70], [@76,215:216='ee',<381>,5:60]]}, jj={w=[[@88,249:249='w',<381>,6:14]], y=[[@92,265:266='jj',<381>,6:30]], m=[[@100,289:290='jj',<381>,6:54]]}, ff={c=[[@116,338:338='c',<381>,7:20]], z=[[@125,365:366='ff',<381>,7:47]]}, tab1={a=[[@49,144:147='tab1',<381>,4:1]], <z_col>=[[@96,272:275='tab1',<381>,6:37]], c=[[@112,325:325='c',<381>,7:7]], t=[[@19,60:63='tab1',<381>,2:52]], x=[[@68,196:199='tab1',<381>,5:41]], <y_col>=[[@42,121:124='tab1',<381>,3:30]], <w_col>=[[@129,372:375='tab1',<381>,7:54]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query8={w=[[@88,249:249='w',<381>,6:14], [@82,230:231='kk',<381>,5:75]]}, query11={aa=[[@52,151:152='aa',<381>,4:8]], max_D=[[@47,136:140='max_D',<381>,3:45]], min_D=[[@80,222:226='min_D',<381>,5:67]], w=[[@84,233:233='w',<381>,5:78]]}, query9={gg_y=[[@121,346:349='gg_y',<381>,7:28]], c=[[@116,338:338='c',<381>,7:20]]}, query4={mxd=[[@32,86:88='mxd',<381>,2:78]]}, query6={mnd=[[@60,171:173='mnd',<381>,5:16]]}, query0={at=[[@11,36:37='at',<381>,2:28]]}, query2={<y_col>=[[@44,126:132='<y_col>',<327>,3:35]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query11={def_query9={query_dictionary={c=[[@116,338:338='c',<381>,7:20]], gg_y=[[@121,346:349='gg_y',<381>,7:28]]}, table_dictionary={ff={c=[[@116,338:338='c',<381>,7:20]], z=[[@125,365:366='ff',<381>,7:47]]}}, filters=[{name=z, table_ref=ff}, {substitution={name=<w_col>, type=column}, table_ref=tab1}], interface={c=[{name=c, table_ref=ff}], gg_y=[{name=y, table_ref=gg}]}}, def_query8={query_dictionary={w=[[@88,249:249='w',<381>,6:14], [@82,230:231='kk',<381>,5:75]]}, table_dictionary={jj={w=[[@88,249:249='w',<381>,6:14]], y=[[@92,265:266='jj',<381>,6:30]], m=[[@100,289:290='jj',<381>,6:54]]}}, filters=[{name=y, table_ref=jj}, {substitution={name=<z_col>, type=column}, table_ref=tab1}, {name=m, table_ref=jj}, {name=e3, table_ref=tab2}], interface={w=[{name=w, table_ref=jj}]}}, def_query6={query_dictionary={mnd=[[@60,171:173='mnd',<381>,5:16]]}, table_dictionary={ee={D=[[@58,168:168='D',<381>,5:13]], x=[[@64,189:190='ee',<381>,5:34]], x2=[[@76,215:216='ee',<381>,5:60]]}}, filters=[{name=x, table_ref=ee}, {name=x, table_ref=tab1}, {name=f, table_ref=tt}, {name=x2, table_ref=ee}], interface={mnd=[{name=D, table_ref=ee}]}}, table_dictionary={tab1={a=[[@49,144:147='tab1',<381>,4:1]], <z_col>=[[@96,272:275='tab1',<381>,6:37]], c=[[@112,325:325='c',<381>,7:7]], t=[[@19,60:63='tab1',<381>,2:52]], x=[[@68,196:199='tab1',<381>,5:41]], <y_col>=[[@42,121:124='tab1',<381>,3:30]], <w_col>=[[@129,372:375='tab1',<381>,7:54]]}}, filters=[], interface={aa=[{name=a, table_ref=tab1}], max_D=[{name=x, table_ref=ee}, {substitution={name=<y_col>, type=column}, table_ref=tab1}, {name=t, table_ref=null}, {name=b, table_ref=tt}, {name=t, table_ref=tab1}, {name=e, table_ref=tt}, {name=x2, table_ref=ee}], min_D=[{name=x, table_ref=ee}, {name=x, table_ref=tab1}, {name=f, table_ref=tt}, {name=x2, table_ref=ee}, {name=D, table_ref=null}], w=[{name=w, table_ref=kk}]}, def_query4={query_dictionary={mxd=[[@32,86:88='mxd',<381>,2:78]]}, table_dictionary={ee={x=[[@36,106:107='ee',<381>,3:15], [@64,189:190='ee',<381>,5:34]], x2=[[@27,78:79='ee',<381>,2:70], [@76,215:216='ee',<381>,5:60]]}}, dependent_queries={predicand3={query=query2, type=filters}, predicand1={query=query0, type=interface}}, def_query0={query_dictionary={at=[[@11,36:37='at',<381>,2:28]]}, table_dictionary={tt={b=[[@15,53:54='tt',<381>,2:45]], t=[[@9,33:33='t',<381>,2:25]], e=[[@23,71:72='tt',<381>,2:63]]}}, filters=[{name=b, table_ref=tt}, {name=t, table_ref=tab1}, {name=e, table_ref=tt}, {name=x2, table_ref=ee}], interface={at=[{name=t, table_ref=tt}]}}, filters=[{name=x, table_ref=ee}], interface={mxd=[{name=t, table_ref=ee}, {name=b, table_ref=tt}, {name=t, table_ref=tab1}, {name=e, table_ref=tt}, {name=x2, table_ref=ee}]}, def_query2={query_dictionary={<y_col>=[[@44,126:132='<y_col>',<327>,3:35]]}, interface={<y_col>=[{substitution={name=<y_col>, type=column}, table_ref=tab1}]}}}, query_dictionary={aa=[[@52,151:152='aa',<381>,4:8]], max_D=[[@47,136:140='max_D',<381>,3:45]], min_D=[[@80,222:226='min_D',<381>,5:67]], w=[[@84,233:233='w',<381>,5:78]]}, dependent_queries={in_list10={query=query9, type=filters}, predicand7={query=query6, type=interface}, predicand5={query=query4, type=interface}}, table_alias={kk=query8}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void nestedQueryDemoWithCteTest() {
		// Same as nestedQueryDemoTest but wrapped in WITH gg AS (...) so gg.y resolves via CTE context_list.
		// Only difference from nestedQueryDemoTest: gg.y is resolved; tt.f and tab2.e3 remain fatals.
		final String query = "WITH gg AS (SELECT y FROM gg_src)"
		+"\n select "
		+"\n (select max((select avg(t) at from tt where tt.b > tab1.t and tt.e = ee.x2)) mxd "
		+"\n from ee where ee.x = (select tab1.<y_col>)) max_D,"
		+"\n tab1.a aa,"
		+"\n (select min(D) mnd from ee where ee.x = tab1.x  and tt.f = ee.x2) min_D,  kk.w"
		+"\n from (select w from jj where jj.y = tab1.<z_col> and jj.m > tab2.e3) kk join tab1"
		+"\n where c in (select c, gg.y gg_y from ff where ff.z = tab1.<w_col>)";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		Snippet snippet = extractor.getSnippet();

		assertFatalDiagnosticAtPosition(
				snippet,
				"QUALIFIED_COLUMN_NOT_FOUND_IN_TABLE",
				"Source Table not found for Column 'e3' at (l:7 c:61). No alias or table called 'tab2'.",
				"e3",
				7,
				61);
		assertFatalDiagnosticAtPosition(
				snippet,
				"QUALIFIED_COLUMN_NOT_FOUND_IN_TABLE",
				"Source Table not found for Column 'f' at (l:6 c:53). No alias or table called 'tt'.",
				"tt",
				6,
				53);

		Assert.assertEquals("AST is wrong", "{SQL={with={1={cte={select={1={column={name=y, table_ref=null}}}, from={table={alias=null, table=gg_src}}}, alias=gg}}, query={select={1={lookup={from={table={alias=null, table=ee}}, where={condition={left={column={name=x, table_ref=ee}}, right={select={1={column={substitution={name=<y_col>, type=column}, table_ref=tab1}}}}, operator==}}, select={1={function={function_name=max, qualifier=null, parameters={select={1={function={function_name=avg, qualifier=null, parameters={column={name=t, table_ref=null}}}, alias=at}}, from={table={alias=null, table=tt}}, where={and={1={condition={left={column={name=b, table_ref=tt}}, right={column={name=t, table_ref=tab1}}, operator=>}}, 2={condition={left={column={name=e, table_ref=tt}}, right={column={name=x2, table_ref=ee}}, operator==}}}}}}, alias=mxd}}}, alias=max_D}, 2={column={name=a, table_ref=tab1}, alias=aa}, 3={lookup={from={table={alias=null, table=ee}}, where={and={1={condition={left={column={name=x, table_ref=ee}}, right={column={name=x, table_ref=tab1}}, operator==}}, 2={condition={left={column={name=f, table_ref=tt}}, right={column={name=x2, table_ref=ee}}, operator==}}}}, select={1={function={function_name=min, qualifier=null, parameters={column={name=D, table_ref=null}}}, alias=mnd}}}, alias=min_D}, 4={column={name=w, table_ref=kk}}}, from={join={1={table={alias=kk, query={select={1={column={name=w, table_ref=null}}}, from={table={alias=null, table=jj}}, where={and={1={condition={left={column={name=y, table_ref=jj}}, right={column={substitution={name=<z_col>, type=column}, table_ref=tab1}}, operator==}}, 2={condition={left={column={name=m, table_ref=jj}}, right={column={name=e3, table_ref=tab2}}, operator=>}}}}}}}, 2={join=join}, 3={table={alias=null, table=tab1}}}}, where={in={item={column={name=c, table_ref=null}}, in_list={select={1={column={name=c, table_ref=null}}, 2={column={name=y, table_ref=gg}, alias=gg_y}}, from={table={alias=null, table=ff}}, where={condition={left={column={name=z, table_ref=ff}}, right={column={substitution={name=<w_col>, type=column}, table_ref=tab1}}, operator==}}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[aa, max_D, min_D, w]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<z_col>=column, <y_col>=column, <w_col>=column}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tt={b=[[@24,88:89='tt',<381>,3:45]], t=[[@18,68:68='t',<381>,3:25]], e=[[@32,106:107='tt',<381>,3:63]]}, ee={D=[[@67,203:203='D',<381>,6:13]], x=[[@45,141:142='ee',<381>,4:15], [@73,224:225='ee',<381>,6:34]], x2=[[@36,113:114='ee',<381>,3:70], [@85,250:251='ee',<381>,6:60]]}, jj={w=[[@97,284:284='w',<381>,7:14]], y=[[@101,300:301='jj',<381>,7:30]], m=[[@109,324:325='jj',<381>,7:54]]}, ff={c=[[@125,373:373='c',<381>,8:20]], z=[[@134,400:401='ff',<381>,8:47]]}, tab1={a=[[@58,179:182='tab1',<381>,5:1]], <z_col>=[[@105,307:310='tab1',<381>,7:37]], c=[[@121,360:360='c',<381>,8:7]], t=[[@28,95:98='tab1',<381>,3:52]], x=[[@77,231:234='tab1',<381>,6:41]], <y_col>=[[@51,156:159='tab1',<381>,4:30]], <w_col>=[[@138,407:410='tab1',<381>,8:54]]}, gg_src={y=[[@5,19:19='y',<381>,1:19]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query9={w=[[@97,284:284='w',<381>,7:14], [@91,265:266='kk',<381>,6:75]]}, query12={aa=[[@61,186:187='aa',<381>,5:8]], max_D=[[@56,171:175='max_D',<381>,4:45]], min_D=[[@89,257:261='min_D',<381>,6:67]], w=[[@93,268:268='w',<381>,6:78]]}, query5={mxd=[[@41,121:123='mxd',<381>,3:78]]}, query7={mnd=[[@69,206:208='mnd',<381>,6:16]]}, query10={gg_y=[[@130,381:384='gg_y',<381>,8:28]], c=[[@125,373:373='c',<381>,8:20]]}, query0={y=[[@5,19:19='y',<381>,1:19], [@127,376:377='gg',<381>,8:23]]}, query1={at=[[@20,71:72='at',<381>,3:28]]}, query3={<y_col>=[[@53,161:167='<y_col>',<327>,4:35]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query12={def_query9={context_list={gg=query0}, query_dictionary={w=[[@97,284:284='w',<381>,7:14], [@91,265:266='kk',<381>,6:75]]}, table_dictionary={jj={w=[[@97,284:284='w',<381>,7:14]], y=[[@101,300:301='jj',<381>,7:30]], m=[[@109,324:325='jj',<381>,7:54]]}}, filters=[{name=y, table_ref=jj}, {substitution={name=<z_col>, type=column}, table_ref=tab1}, {name=m, table_ref=jj}, {name=e3, table_ref=tab2}], interface={w=[{name=w, table_ref=jj}]}, table_alias={gg=query0}}, context_list={gg=query0}, def_query7={context_list={gg=query0}, query_dictionary={mnd=[[@69,206:208='mnd',<381>,6:16]]}, table_dictionary={ee={D=[[@67,203:203='D',<381>,6:13]], x=[[@73,224:225='ee',<381>,6:34]], x2=[[@85,250:251='ee',<381>,6:60]]}}, filters=[{name=x, table_ref=ee}, {name=x, table_ref=tab1}, {name=f, table_ref=tt}, {name=x2, table_ref=ee}], interface={mnd=[{name=D, table_ref=ee}]}, table_alias={gg=query0}}, def_query10={context_list={gg=query0}, query_dictionary={c=[[@125,373:373='c',<381>,8:20]], gg_y=[[@130,381:384='gg_y',<381>,8:28]]}, table_dictionary={ff={c=[[@125,373:373='c',<381>,8:20]], z=[[@134,400:401='ff',<381>,8:47]]}}, filters=[{name=z, table_ref=ff}, {substitution={name=<w_col>, type=column}, table_ref=tab1}], interface={c=[{name=c, table_ref=ff}], gg_y=[{name=y, table_ref=gg}]}, table_alias={gg=query0}}, table_dictionary={tab1={a=[[@58,179:182='tab1',<381>,5:1]], <z_col>=[[@105,307:310='tab1',<381>,7:37]], c=[[@121,360:360='c',<381>,8:7]], t=[[@28,95:98='tab1',<381>,3:52]], x=[[@77,231:234='tab1',<381>,6:41]], <y_col>=[[@51,156:159='tab1',<381>,4:30]], <w_col>=[[@138,407:410='tab1',<381>,8:54]]}}, def_query0={query_dictionary={y=[[@5,19:19='y',<381>,1:19], [@127,376:377='gg',<381>,8:23]]}, table_dictionary={gg_src={y=[[@5,19:19='y',<381>,1:19]]}}, interface={y=[{name=y, table_ref=gg_src}]}}, filters=[], def_query5={context_list={gg=query0}, query_dictionary={mxd=[[@41,121:123='mxd',<381>,3:78]]}, table_dictionary={ee={x=[[@45,141:142='ee',<381>,4:15], [@73,224:225='ee',<381>,6:34]], x2=[[@36,113:114='ee',<381>,3:70], [@85,250:251='ee',<381>,6:60]]}}, def_query1={context_list={gg=query0}, query_dictionary={at=[[@20,71:72='at',<381>,3:28]]}, table_dictionary={tt={b=[[@24,88:89='tt',<381>,3:45]], t=[[@18,68:68='t',<381>,3:25]], e=[[@32,106:107='tt',<381>,3:63]]}}, filters=[{name=b, table_ref=tt}, {name=t, table_ref=tab1}, {name=e, table_ref=tt}, {name=x2, table_ref=ee}], interface={at=[{name=t, table_ref=tt}]}, table_alias={gg=query0}}, dependent_queries={predicand2={query=query1, type=interface}, predicand4={query=query3, type=filters}}, filters=[{name=x, table_ref=ee}], interface={mxd=[{name=t, table_ref=ee}, {name=b, table_ref=tt}, {name=t, table_ref=tab1}, {name=e, table_ref=tt}, {name=x2, table_ref=ee}]}, def_query3={context_list={gg=query0}, query_dictionary={<y_col>=[[@53,161:167='<y_col>',<327>,4:35]]}, interface={<y_col>=[{substitution={name=<y_col>, type=column}, table_ref=tab1}]}, table_alias={gg=query0}}, table_alias={gg=query0}}, interface={aa=[{name=a, table_ref=tab1}], max_D=[{name=x, table_ref=ee}, {substitution={name=<y_col>, type=column}, table_ref=tab1}, {name=t, table_ref=null}, {name=b, table_ref=tt}, {name=t, table_ref=tab1}, {name=e, table_ref=tt}, {name=x2, table_ref=ee}], min_D=[{name=x, table_ref=ee}, {name=x, table_ref=tab1}, {name=f, table_ref=tt}, {name=x2, table_ref=ee}, {name=D, table_ref=null}], w=[{name=w, table_ref=kk}]}, query_dictionary={aa=[[@61,186:187='aa',<381>,5:8]], max_D=[[@56,171:175='max_D',<381>,4:45]], min_D=[[@89,257:261='min_D',<381>,6:67]], w=[[@93,268:268='w',<381>,6:78]]}, dependent_queries={predicand6={query=query5, type=interface}, predicand8={query=query7, type=interface}, in_list11={query=query10, type=filters}}, table_alias={gg=query0, kk=query9}}}",
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
		Assert.assertEquals("Symbol Table is wrong", "{query1={query_dictionary={02_College_Name=[[@11,62:76='02_College_Name',<384>,1:62]], 01_College_Cd=[[@5,27:39='01_College_Cd',<384>,1:27]]}, def_query0={query_dictionary={College_Name=[[@7,42:44='sub',<381>,1:42]], *=[[@17,94:94='*',<291>,1:94]], College_Code=[[@1,7:9='sub',<381>,1:7]]}, table_dictionary={pantoresultprod.hive_result_pit_6875_220752_46090864={*=[[@15,92:92='t',<381>,1:92]]}}, interface={*=[{name=*, table_ref=t}]}, table_alias={t=pantoresultprod.hive_result_pit_6875_220752_46090864}}, interface={02_College_Name=[{name=College_Name, table_ref=sub}], 01_College_Cd=[{name=College_Code, table_ref=sub}]}, table_alias={sub=query0}}}",
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
		Assert.assertEquals("Symbol Table is wrong", "{query1={query_dictionary={02_COURSE_REGISTRATION_DESC=[[@11,106:132='02_COURSE_REGISTRATION_DESC',<384>,1:106]], 01_COURSE_REGISTRATION_CD=[[@5,39:63='01_COURSE_REGISTRATION_CD',<384>,1:39]]}, def_query0={query_dictionary={Course_Registration_Description=[[@7,67:69='sub',<381>,1:67]], *=[[@17,150:150='*',<291>,1:150]], Course_Registration_Code=[[@1,7:9='sub',<381>,1:7]]}, table_dictionary={pantoresultprod.hive_result_pit_5223_164727_46090703={*=[[@15,148:148='t',<381>,1:148]]}}, interface={*=[{name=*, table_ref=t}]}, table_alias={t=pantoresultprod.hive_result_pit_5223_164727_46090703}}, interface={02_COURSE_REGISTRATION_DESC=[{name=Course_Registration_Description, table_ref=sub}], 01_COURSE_REGISTRATION_CD=[{name=Course_Registration_Code, table_ref=sub}]}, table_alias={sub=query0}}}",
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
		Assert.assertEquals("Symbol Table is wrong", "{query1={query_dictionary={02_COURSE_REGISTRATION_DESC=[[@7,98:124='02_COURSE_REGISTRATION_DESC',<384>,1:98]], 01_COURSE_REGISTRATION_CD=[[@3,35:59='01_COURSE_REGISTRATION_CD',<384>,1:35]]}, def_query0={query_dictionary={*=[[@13,142:142='*',<291>,1:142]]}, table_dictionary={pantoresultprod.hive_result_pit_5223_164727_46090703={*=[[@11,140:140='t',<381>,1:140]]}}, interface={*=[{name=*, table_ref=t}]}, table_alias={t=pantoresultprod.hive_result_pit_5223_164727_46090703}}, interface={02_COURSE_REGISTRATION_DESC=[{name=Course_Registration_Description, table_ref=query0}], 01_COURSE_REGISTRATION_CD=[{name=Course_Registration_Code, table_ref=query0}]}, table_alias={sub=query0}}}",
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
		Assert.assertEquals("Symbol Table is wrong", "{query0={query_dictionary={<simple>=[[@3,10:17='<simple>',<327>,1:10]], <with.dots.in.name>=[[@7,22:40='<with.dots.in.name>',<327>,1:22]]}, table_dictionary={tab1={<simple>=[[@1,8:8='a',<381>,1:8]], <with.dots.in.name>=[[@5,20:20='a',<381>,1:20]]}}, interface={<simple>=[{substitution={name=<simple>, type=column}, table_ref=a}], <with.dots.in.name>=[{substitution={name=<with.dots.in.name>, type=column}, table_ref=a}]}, table_alias={a=tab1}}}",
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
		Assert.assertEquals("Symbol Table is wrong", "{query0={query_dictionary={<[another].[item]>=[[@11,59:76='<[another].[item]>',<328>,1:59]], <[DOMAIN].[ENTITY].[ATTRIBUTE]>=[[@7,24:54='<[DOMAIN].[ENTITY].[ATTRIBUTE]>',<328>,1:24]], <[simple]>=[[@3,10:19='<[simple]>',<328>,1:10]]}, table_dictionary={<[DOMAIN].[ENTITY]>={<[another].[item]>=[[@9,57:57='a',<381>,1:57]], <[DOMAIN].[ENTITY].[ATTRIBUTE]>=[[@5,22:22='a',<381>,1:22]], <[simple]>=[[@1,8:8='a',<381>,1:8]]}}, interface={<[another].[item]>=[{substitution={name=<[another].[item]>, parts={1=[another], 2=[item]}, type=column}, table_ref=a}], <[DOMAIN].[ENTITY].[ATTRIBUTE]>=[{substitution={name=<[DOMAIN].[ENTITY].[ATTRIBUTE]>, parts={1=[DOMAIN], 2=[ENTITY], 3=[ATTRIBUTE]}, type=column}, table_ref=a}], <[simple]>=[{substitution={name=<[simple]>, parts={1=[simple]}, type=column}, table_ref=a}]}, table_alias={a=<[DOMAIN].[ENTITY]>}}}",
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
		Assert.assertEquals("Symbol Table is wrong", "{query0={query_dictionary={<[PREFIX.DOMAIN.SUFFIX].[ENTITY.SUFFIX].[Prefix.ATTRIBUTE]>=[[@3,11:69='<[PREFIX.DOMAIN.SUFFIX].[ENTITY.SUFFIX].[Prefix.ATTRIBUTE]>',<328>,1:11]]}, table_dictionary={<[DOMAIN].[ENTITY]>={<[PREFIX.DOMAIN.SUFFIX].[ENTITY.SUFFIX].[Prefix.ATTRIBUTE]>=[[@1,9:9='a',<381>,1:9]]}}, interface={<[PREFIX.DOMAIN.SUFFIX].[ENTITY.SUFFIX].[Prefix.ATTRIBUTE]>=[{substitution={name=<[PREFIX.DOMAIN.SUFFIX].[ENTITY.SUFFIX].[Prefix.ATTRIBUTE]>, parts={1=[PREFIX.DOMAIN.SUFFIX].[ENTITY.SUFFIX].[Prefix.ATTRIBUTE]}, type=column}, table_ref=a}]}, table_alias={a=<[DOMAIN].[ENTITY]>}}}",
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
		Assert.assertEquals("Symbol Table is wrong", "{query0={query_dictionary={<[PREFIX-DOMAIN-SUFFIX].[ENTITY-SUFFIX].[Prefix-ATTRIBUTE]>=[[@3,11:69='<[PREFIX-DOMAIN-SUFFIX].[ENTITY-SUFFIX].[Prefix-ATTRIBUTE]>',<328>,1:11]]}, table_dictionary={<[DOMAIN].[ENTITY]>={<[PREFIX-DOMAIN-SUFFIX].[ENTITY-SUFFIX].[Prefix-ATTRIBUTE]>=[[@1,9:9='a',<381>,1:9]]}}, interface={<[PREFIX-DOMAIN-SUFFIX].[ENTITY-SUFFIX].[Prefix-ATTRIBUTE]>=[{substitution={name=<[PREFIX-DOMAIN-SUFFIX].[ENTITY-SUFFIX].[Prefix-ATTRIBUTE]>, parts={1=[PREFIX-DOMAIN-SUFFIX], 2=[ENTITY-SUFFIX], 3=[Prefix-ATTRIBUTE]}, type=column}, table_ref=a}]}, table_alias={a=<[DOMAIN].[ENTITY]>}}}",
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
			Assert.assertEquals("Table Dictionary is wrong", "{<[Enrollment Services].[Client Entering Class]>={<select column>=[[@1,8:10='cec',<381>,1:8]], non_variable_col=[[@11,148:150='cec',<381>,3:61], [@15,171:174='cec2',<381>,3:84], [@15,171:174='cec2',<381>,3:84]], <where column>=[[@26,276:279='cec3',<381>,4:82], [@22,255:257='cec',<381>,4:61], [@22,255:257='cec',<381>,4:61]]}, <where column>={}}",
							extractor.getTableColumnDictionaryMap().toString());
			Assert.assertEquals("Query Column Dictionary is wrong", "{query0={<select column>=[[@3,12:26='<select column>',<327>,1:12]]}}",
							extractor.getQueryColumnDictionaryMap().toString());
			Assert.assertEquals("Symbol Table is wrong", "{query0={query_dictionary={<select column>=[[@3,12:26='<select column>',<327>,1:12]]}, table_dictionary={<[Enrollment Services].[Client Entering Class]>={<select column>=[[@1,8:10='cec',<381>,1:8]], non_variable_col=[[@11,148:150='cec',<381>,3:61], [@15,171:174='cec2',<381>,3:84], [@15,171:174='cec2',<381>,3:84]], <where column>=[[@26,276:279='cec3',<381>,4:82], [@22,255:257='cec',<381>,4:61], [@22,255:257='cec',<381>,4:61]]}, <where column>={}}, filters=[{name=non_variable_col, table_ref=cec}, {name=non_variable_col, table_ref=cec2}, {substitution={name=<where column>, type=column}, table_ref=cec}, {substitution={name=<where column>, type=column}, table_ref=cec3}], interface={<select column>=[{substitution={name=<select column>, type=column}, table_ref=cec}]}, table_alias={cec=<[Enrollment Services].[Client Entering Class]>, cec3=<[Enrollment Services].[Client Entering Class]>, cec2=<[Enrollment Services].[Client Entering Class]>}}}",
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
			Assert.assertEquals("Symbol Table is wrong", "{union2={query0={query_dictionary={<select column>=[[@3,12:26='<select column>',<327>,1:12]]}, table_dictionary={<[Enrollment Services].[Client Entering Class]>={<select column>=[[@1,8:10='cec',<381>,1:8], [@22,172:174='cec',<381>,5:7]], non_variable_col=[[@8,93:95='cec',<381>,3:6], [@29,257:259='cec',<381>,7:6]], <where column>=[[@15,130:132='cec',<381>,3:43], [@36,294:296='cec',<381>,7:43]]}}, filters=[{name=non_variable_col, table_ref=cec}, {substitution={name=<where column>, type=column}, table_ref=cec}], interface={<select column>=[{substitution={name=<select column>, type=column}, table_ref=cec}]}, table_alias={cec=<[Enrollment Services].[Client Entering Class]>}}, interface={<select column>=[{table_ref=cec, substitution={name=<select column>, type=column}}, {table_ref=cec, substitution={name=<select column>, type=column}}]}, query1={query_dictionary={<select column>=[[@24,176:190='<select column>',<327>,5:11]]}, table_dictionary={<[Enrollment Services].[Client Entering Class]>={<select column>=[[@22,172:174='cec',<381>,5:7]], non_variable_col=[[@29,257:259='cec',<381>,7:6]], <where column>=[[@36,294:296='cec',<381>,7:43]]}}, filters=[{name=non_variable_col, table_ref=cec}, {substitution={name=<where column>, type=column}, table_ref=cec}], interface={<select column>=[{substitution={name=<select column>, type=column}, table_ref=cec}]}, table_alias={cec=<[Enrollment Services].[Client Entering Class]>}}}}",
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
			Assert.assertEquals("Symbol Table is wrong", "{intersect2={query0={query_dictionary={<select column>=[[@3,12:26='<select column>',<327>,1:12]]}, table_dictionary={<[Enrollment Services].[Client Entering Class]>={<select column>=[[@1,8:10='cec',<381>,1:8], [@22,176:178='cec',<381>,5:7]], non_variable_col=[[@8,93:95='cec',<381>,3:6], [@29,261:263='cec',<381>,7:6]], <where column>=[[@15,130:132='cec',<381>,3:43], [@36,298:300='cec',<381>,7:43]]}}, filters=[{name=non_variable_col, table_ref=cec}, {substitution={name=<where column>, type=column}, table_ref=cec}], interface={<select column>=[{substitution={name=<select column>, type=column}, table_ref=cec}]}, table_alias={cec=<[Enrollment Services].[Client Entering Class]>}}, interface={<select column>=[{table_ref=cec, substitution={name=<select column>, type=column}}, {table_ref=cec, substitution={name=<select column>, type=column}}]}, query1={query_dictionary={<select column>=[[@24,180:194='<select column>',<327>,5:11]]}, table_dictionary={<[Enrollment Services].[Client Entering Class]>={<select column>=[[@22,176:178='cec',<381>,5:7]], non_variable_col=[[@29,261:263='cec',<381>,7:6]], <where column>=[[@36,298:300='cec',<381>,7:43]]}}, filters=[{name=non_variable_col, table_ref=cec}, {substitution={name=<where column>, type=column}, table_ref=cec}], interface={<select column>=[{substitution={name=<select column>, type=column}, table_ref=cec}]}, table_alias={cec=<[Enrollment Services].[Client Entering Class]>}}}}",
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
			Assert.assertEquals("Symbol Table is wrong",
				"{query1={context_list={wrapped=query0, cec=query0}, query_dictionary={<select column>=[[@28,189:203='<select column>',<327>,4:11]], dummy_col2=[[@32,208:217='dummy_col2',<381>,4:30]]}, table_dictionary={<select column>={}, dummy_table={dummy_col=[[@44,280:280='d',<381>,4:102]], dummy_col2=[[@30,206:206='d',<381>,4:28]]}}, def_query0={query_dictionary={<select column>=[[@7,30:44='<select column>',<327>,1:30], [@26,185:187='cec',<381>,4:7], [@40,258:260='cec',<381>,4:80]]}, table_dictionary={<[Enrollment Services].[Client Entering Class]>={<select column>=[[@5,26:28='cec',<381>,1:26], [@26,185:187='cec',<381>,4:7], [@40,258:260='cec',<381>,4:80]], non_variable_col=[[@18,142:144='cec',<381>,3:37]], <where column>=[[@12,111:113='cec',<381>,3:6]]}}, filters=[{substitution={name=<where column>, type=column}, table_ref=cec}, {name=non_variable_col, table_ref=cec}], interface={<select column>=[{substitution={name=<select column>, type=column}, table_ref=cec}]}, table_alias={cec=<[Enrollment Services].[Client Entering Class]>}}, filters=[{substitution={name=<select column>, type=column}, table_ref=cec}, {name=dummy_col, table_ref=d}], interface={<select column>=[{substitution={name=<select column>, type=column}, table_ref=cec}], dummy_col2=[{name=dummy_col2, table_ref=d}]}, table_alias={wrapped=query0}}}",
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
			Assert.assertEquals("Symbol Table is wrong",
				"{query1={context_list={wrapped=query0, cec=query0}, query_dictionary={<select column>=[[@32,198:212='<select column>',<327>,4:11]], dummy_col2=[[@36,217:226='dummy_col2',<381>,4:30]]}, table_dictionary={<select column>={}, dummy_table={dummy_col=[[@48,289:289='d',<381>,4:102]], dummy_col2=[[@34,215:215='d',<381>,4:28]]}}, def_query0={query_dictionary={<select column>=[[@7,30:44='<select column>',<327>,1:30], [@30,194:196='cec',<381>,4:7], [@44,267:269='cec',<381>,4:80]], unnamed_0=[[@15,73:73=')',<288>,1:73]]}, table_dictionary={<[Enrollment Services].[Client Entering Class]>={agg_col=[[@12,62:64='cec',<381>,1:62]], <select column>=[[@5,26:28='cec',<381>,1:26], [@30,194:196='cec',<381>,4:7], [@44,267:269='cec',<381>,4:80]], non_variable_col=[[@21,143:145='cec',<381>,3:9]], <where column>=[[@25,165:167='cec',<381>,3:31]]}}, grouped_by=[{name=non_variable_col, table_ref=cec}, {substitution={name=<where column>, type=column}, table_ref=cec}], interface={<select column>=[{substitution={name=<select column>, type=column}, table_ref=cec}], unnamed_0=[{name=agg_col, table_ref=cec}]}, table_alias={cec=<[Enrollment Services].[Client Entering Class]>}}, filters=[{substitution={name=<select column>, type=column}, table_ref=cec}, {name=dummy_col, table_ref=d}], interface={<select column>=[{substitution={name=<select column>, type=column}, table_ref=cec}], dummy_col2=[{name=dummy_col2, table_ref=d}]}, table_alias={wrapped=query0}}}",
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
			Assert.assertEquals("Symbol Table is wrong",
				"{query1={context_list={wrapped=query0, cec=query0}, query_dictionary={<select column>=[[@24,169:183='<select column>',<327>,4:11]], dummy_col2=[[@28,188:197='dummy_col2',<381>,4:30]]}, table_dictionary={<select column>={}, dummy_table={dummy_col=[[@40,260:260='d',<381>,4:102]], dummy_col2=[[@26,186:186='d',<381>,4:28]]}}, def_query0={query_dictionary={<select column>=[[@7,30:44='<select column>',<327>,1:30], [@22,165:167='cec',<381>,4:7], [@36,238:240='cec',<381>,4:80]]}, table_dictionary={<[Enrollment Services].[Client Entering Class]>={<select column>=[[@5,26:28='cec',<381>,1:26], [@22,165:167='cec',<381>,4:7], [@36,238:240='cec',<381>,4:80]], non_variable_col=[[@13,114:116='cec',<381>,3:9]], <where column>=[[@17,136:138='cec',<381>,3:31]]}}, ordered_by=[{name=non_variable_col, table_ref=cec}, {substitution={name=<where column>, type=column}, table_ref=cec}], interface={<select column>=[{substitution={name=<select column>, type=column}, table_ref=cec}]}, table_alias={cec=<[Enrollment Services].[Client Entering Class]>}}, filters=[{substitution={name=<select column>, type=column}, table_ref=cec}, {name=dummy_col, table_ref=d}], interface={<select column>=[{substitution={name=<select column>, type=column}, table_ref=cec}], dummy_col2=[{name=dummy_col2, table_ref=d}]}, table_alias={wrapped=query0}}}",
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
			Assert.assertEquals("Symbol Table is wrong",
				"{query1={context_list={wrapped=query0, cec=query0}, query_dictionary={<select column>=[[@46,274:288='<select column>',<327>,5:11]], dummy_col2=[[@50,293:302='dummy_col2',<381>,5:30]]}, table_dictionary={<select column>={}, dummy_table={dummy_col=[[@62,365:365='d',<381>,5:102]], dummy_col2=[[@48,291:291='d',<381>,5:28]]}}, def_query0={query_dictionary={<select column>=[[@7,30:44='<select column>',<327>,1:30], [@44,270:272='cec',<381>,5:7], [@58,343:345='cec',<381>,5:80]], unnamed_0=[[@15,73:73=')',<288>,1:73]]}, table_dictionary={<[Enrollment Services].[Client Entering Class]>={agg_col=[[@12,62:64='cec',<381>,1:62]], <select column>=[[@5,26:28='cec',<381>,1:26], [@44,270:272='cec',<381>,5:7], [@58,343:345='cec',<381>,5:80]], non_variable_col=[[@21,143:145='cec',<381>,3:9], [@29,192:194='cec',<381>,4:7]], <where column>=[[@25,165:167='cec',<381>,3:31], [@36,229:231='cec',<381>,4:44]]}}, grouped_by=[{name=non_variable_col, table_ref=cec}, {substitution={name=<where column>, type=column}, table_ref=cec}], filters=[{name=non_variable_col, table_ref=cec}, {substitution={name=<where column>, type=column}, table_ref=cec}], interface={<select column>=[{substitution={name=<select column>, type=column}, table_ref=cec}], unnamed_0=[{name=agg_col, table_ref=cec}]}, table_alias={cec=<[Enrollment Services].[Client Entering Class]>}}, filters=[{substitution={name=<select column>, type=column}, table_ref=cec}, {name=dummy_col, table_ref=d}], interface={<select column>=[{substitution={name=<select column>, type=column}, table_ref=cec}], dummy_col2=[{name=dummy_col2, table_ref=d}]}, table_alias={wrapped=query0}}}",
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
			Assert.assertEquals("Symbol Table is wrong",
				"{query1={context_list={wrapped=query0, cec=query0}, query_dictionary={<select column>=[[@38,235:249='<select column>',<327>,4:11]], dummy_col2=[[@42,254:263='dummy_col2',<381>,4:30]]}, table_dictionary={<select column>={}, dummy_table={dummy_col=[[@54,326:326='d',<381>,4:102]], dummy_col2=[[@40,252:252='d',<381>,4:28]]}}, def_query0={query_dictionary={<select column>=[[@7,30:44='<select column>',<327>,1:30], [@36,231:233='cec',<381>,4:7], [@50,304:306='cec',<381>,4:80]]}, table_dictionary={<[Enrollment Services].[Client Entering Class]>={<select column>=[[@5,26:28='cec',<381>,1:26], [@36,231:233='cec',<381>,4:7], [@50,304:306='cec',<381>,4:80]], non_variable_col=[[@19,145:147='cec',<381>,3:40], [@28,195:197='cec',<381>,3:90]], <where column>=[[@23,167:169='cec',<381>,3:62]]}}, filters=[{name=non_variable_col, table_ref=cec}, {substitution={name=<where column>, type=column}, table_ref=cec}], interface={<select column>=[{substitution={name=<select column>, type=column}, table_ref=cec}]}, table_alias={cec=<[Enrollment Services].[Client Entering Class]>}}, filters=[{substitution={name=<select column>, type=column}, table_ref=cec}, {name=dummy_col, table_ref=d}], interface={<select column>=[{substitution={name=<select column>, type=column}, table_ref=cec}], dummy_col2=[{name=dummy_col2, table_ref=d}]}, table_alias={wrapped=query0}}}",
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
			Assert.assertEquals("Symbol Table is wrong",
				"{query1={context_list={wrapped=query0, cec=query0}, query_dictionary={<select column>=[[@37,328:342='<select column>',<327>,5:11]], dummy_col2=[[@41,347:356='dummy_col2',<381>,5:30]]}, table_dictionary={<select column>={}, dummy_table={dummy_col=[[@53,419:419='d',<381>,5:102]], dummy_col2=[[@39,345:345='d',<381>,5:28]]}}, def_query0={query_dictionary={<select column>=[[@7,30:44='<select column>',<327>,1:30], [@35,324:326='cec',<381>,5:7], [@49,397:399='cec',<381>,5:80]]}, table_dictionary={<[Enrollment Services].[Client Entering Class]>={<select column>=[[@5,26:28='cec',<381>,1:26], [@35,324:326='cec',<381>,5:7], [@49,397:399='cec',<381>,5:80]], non_variable_col=[[@15,166:168='cec',<381>,3:61], [@19,189:192='cec2',<381>,3:84], [@19,189:192='cec2',<381>,3:84]], <where column>=[[@30,294:297='cec3',<381>,4:82], [@26,273:275='cec',<381>,4:61], [@26,273:275='cec',<381>,4:61]]}, <where column>={}}, filters=[{name=non_variable_col, table_ref=cec}, {name=non_variable_col, table_ref=cec2}, {substitution={name=<where column>, type=column}, table_ref=cec}, {substitution={name=<where column>, type=column}, table_ref=cec3}], interface={<select column>=[{substitution={name=<select column>, type=column}, table_ref=cec}]}, table_alias={cec=<[Enrollment Services].[Client Entering Class]>, cec3=<[Enrollment Services].[Client Entering Class]>, cec2=<[Enrollment Services].[Client Entering Class]>}}, filters=[{substitution={name=<select column>, type=column}, table_ref=cec}, {name=dummy_col, table_ref=d}], interface={<select column>=[{substitution={name=<select column>, type=column}, table_ref=cec}], dummy_col2=[{name=dummy_col2, table_ref=d}]}, table_alias={wrapped=query0}}}",
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
			Assert.assertEquals("Symbol Table is wrong",
				"{query3={context_list={wrapped=union2, cec=union2}, query_dictionary={<select column>=[[@49,353:367='<select column>',<327>,8:11]], dummy_col2=[[@53,372:381='dummy_col2',<381>,8:30]]}, def_union2={query0={query_dictionary={<select column>=[[@7,30:44='<select column>',<327>,1:30]]}, table_dictionary={<[Enrollment Services].[Client Entering Class]>={<select column>=[[@5,26:28='cec',<381>,1:26], [@26,190:192='cec',<381>,5:7]], non_variable_col=[[@12,111:113='cec',<381>,3:6], [@33,275:277='cec',<381>,7:6]], <where column>=[[@19,148:150='cec',<381>,3:43], [@40,312:314='cec',<381>,7:43]]}}, filters=[{name=non_variable_col, table_ref=cec}, {substitution={name=<where column>, type=column}, table_ref=cec}], interface={<select column>=[{substitution={name=<select column>, type=column}, table_ref=cec}]}, table_alias={cec=<[Enrollment Services].[Client Entering Class]>}}, interface={<select column>=[{table_ref=cec, substitution={name=<select column>, type=column}}, {table_ref=cec, substitution={name=<select column>, type=column}}]}, query1={query_dictionary={<select column>=[[@28,194:208='<select column>',<327>,5:11]]}, table_dictionary={<[Enrollment Services].[Client Entering Class]>={<select column>=[[@26,190:192='cec',<381>,5:7]], non_variable_col=[[@33,275:277='cec',<381>,7:6]], <where column>=[[@40,312:314='cec',<381>,7:43]]}}, filters=[{name=non_variable_col, table_ref=cec}, {substitution={name=<where column>, type=column}, table_ref=cec}], interface={<select column>=[{substitution={name=<select column>, type=column}, table_ref=cec}]}, table_alias={cec=<[Enrollment Services].[Client Entering Class]>}}}, table_dictionary={<select column>={}, dummy_table={dummy_col=[[@65,444:444='d',<381>,8:102]], dummy_col2=[[@51,370:370='d',<381>,8:28]]}}, filters=[{substitution={name=<select column>, type=column}, table_ref=cec}, {name=dummy_col, table_ref=d}], interface={<select column>=[{substitution={name=<select column>, type=column}, table_ref=cec}], dummy_col2=[{name=dummy_col2, table_ref=d}]}, table_alias={wrapped=union2}}}",
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
			Assert.assertEquals("Symbol Table is wrong",
				"{query3={context_list={wrapped=intersect2, cec=intersect2}, query_dictionary={<select column>=[[@49,357:371='<select column>',<327>,8:11]], dummy_col2=[[@53,376:385='dummy_col2',<381>,8:30]]}, table_dictionary={<select column>={}, dummy_table={dummy_col=[[@65,448:448='d',<381>,8:102]], dummy_col2=[[@51,374:374='d',<381>,8:28]]}}, def_intersect2={query0={query_dictionary={<select column>=[[@7,30:44='<select column>',<327>,1:30]]}, table_dictionary={<[Enrollment Services].[Client Entering Class]>={<select column>=[[@5,26:28='cec',<381>,1:26], [@26,194:196='cec',<381>,5:7]], non_variable_col=[[@12,111:113='cec',<381>,3:6], [@33,279:281='cec',<381>,7:6]], <where column>=[[@19,148:150='cec',<381>,3:43], [@40,316:318='cec',<381>,7:43]]}}, filters=[{name=non_variable_col, table_ref=cec}, {substitution={name=<where column>, type=column}, table_ref=cec}], interface={<select column>=[{substitution={name=<select column>, type=column}, table_ref=cec}]}, table_alias={cec=<[Enrollment Services].[Client Entering Class]>}}, interface={<select column>=[{table_ref=cec, substitution={name=<select column>, type=column}}, {table_ref=cec, substitution={name=<select column>, type=column}}]}, query1={query_dictionary={<select column>=[[@28,198:212='<select column>',<327>,5:11]]}, table_dictionary={<[Enrollment Services].[Client Entering Class]>={<select column>=[[@26,194:196='cec',<381>,5:7]], non_variable_col=[[@33,279:281='cec',<381>,7:6]], <where column>=[[@40,316:318='cec',<381>,7:43]]}}, filters=[{name=non_variable_col, table_ref=cec}, {substitution={name=<where column>, type=column}, table_ref=cec}], interface={<select column>=[{substitution={name=<select column>, type=column}, table_ref=cec}]}, table_alias={cec=<[Enrollment Services].[Client Entering Class]>}}}, filters=[{substitution={name=<select column>, type=column}, table_ref=cec}, {name=dummy_col, table_ref=d}], interface={<select column>=[{substitution={name=<select column>, type=column}, table_ref=cec}], dummy_col2=[{name=dummy_col2, table_ref=d}]}, table_alias={wrapped=intersect2}}}",
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
		
		Assert.assertEquals("Symbol Table is wrong",
				"{query0={query_dictionary={apple=[[@1,7:11='apple',<381>,1:7]], <other>=[[@5,19:25='<other>',<327>,1:19]]}, table_dictionary={tab1={apple=[[@1,7:11='apple',<381>,1:7]]}}, interface={apple=[{name=apple, table_ref=tab1}], <other>=[{substitution={name=<other>, type=column}, table_ref=tab2}]}}}",
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
		
		Assert.assertEquals("Symbol Table is wrong",
				"{query1={query_dictionary={apple=[[@1,7:11='apple',<381>,1:7]]}, table_dictionary={tab2={apple=[[@1,7:11='apple',<381>,1:7], [@23,93:96='tab2',<381>,1:93]], <other>=[[@9,49:52='tab2',<381>,1:49]]}}, def_query0={query_dictionary={apple=[[@5,27:31='apple',<381>,1:27], [@19,83:83='a',<381>,1:83]]}, table_dictionary={tab1={apple=[[@5,27:31='apple',<381>,1:27]]}}, filters=[{substitution={name=<other>, type=column}, table_ref=tab2}], interface={apple=[{name=apple, table_ref=tab1}]}}, filters=[{name=apple, table_ref=a}, {name=apple, table_ref=tab2}], interface={apple=[{name=apple, table_ref=null}]}, table_alias={a=query0}}}",
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
				"Ambiguous column reference 'contact_key' at (l:4 c:45). Possible sources:",
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
		Assert.assertEquals("Table Dictionary is wrong", "{{{ source('pdp_alr_v2','rsc__cappex_contacts') }}={cappex_id=[[@5,26:28='rsc',<381>,1:26]], sourcecontact_id=[[@32,206:208='rsc',<381>,3:3]]}, {{ source('PDP_ALR_V2','rsc__cappex_contacts') }}={cappex_id=[[@5,26:28='rsc',<381>,1:26]], sourcecontact_id=[[@32,206:208='rsc',<381>,3:3]]}, {{ source('pdp_alr_v2_contacts','prc__contacts_by_sourcecontacts_current') }}={contact_key=[[@1,8:11='cbsc',<381>,1:8]], sourcecontact_id=[[@36,229:232='cbsc',<381>,3:26]], contact_priority=[[@50,318:321='cbsc',<381>,4:66]]}, {{ source('PDP_ALR_V2_CONTACTS','prc__contacts_by_sourcecontacts_current') }}={contact_key=[[@1,8:11='cbsc',<381>,1:8]], sourcecontact_id=[[@36,229:232='cbsc',<381>,3:26]], contact_priority=[[@50,318:321='cbsc',<381>,4:66]]}}",
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

		Assert.assertEquals("Symbol Table is wrong",
				"{query0={query_dictionary={contact_key=[[@3,13:23='contact_key',<381>,1:13]]}, table_dictionary={{{ source('pdp_alr_v2_contacts','prc__contacts_by_sourcecontacts_current') }}={contact_key=[[@23,159:169='contact_key',<381>,2:42], [@1,8:11='cbsc',<381>,1:8]], contact_priority=[[@26,180:183='cbsc',<381>,2:63]]}}, filters=[{name=contact_key, table_ref={{ source('pdp_alr_v2_contacts','prc__contacts_by_sourcecontacts_current') }}}, {name=contact_priority, table_ref=cbsc}], interface={contact_key=[{name=contact_key, table_ref=cbsc}]}, table_alias={cbsc={{ source('PDP_ALR_V2_CONTACTS','prc__contacts_by_sourcecontacts_current') }}}}}",
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


	// -------------------------------------------------------------------------
	// Correlated subquery diagnostic tests (predicand / IN / EXISTS)
	// Compare correlation handling consistency across subquery kinds.
	// -------------------------------------------------------------------------
	@Test
	public void correlatedScalarPredicandNestedJoinSubqueryTest() {
		final String query = "SELECT oa.pd1, oa.pd2 FROM tab_a AS oa"
		    + "\nWHERE oa.pd3 = (SELECT ib.pd9 FROM tab_b AS ib"
		    + "\n                JOIN (SELECT ic.pd7 FROM tab_c AS ic"
		    + "\n                      WHERE ic.pd7 = oa.pd1) AS ix"
		    + "\n                ON ix.pd7 = ib.pd6)";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		assertNoFatalErrors(extractor);

		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=pd1, table_ref=oa}}, 2={column={name=pd2, table_ref=oa}}}, from={table={alias=oa, table=tab_a}}, where={condition={left={column={name=pd3, table_ref=oa}}, right={select={1={column={name=pd9, table_ref=ib}}}, from={join={1={table={alias=ib, table=tab_b}}, 2={join=JOIN, on={condition={left={column={name=pd7, table_ref=ix}}, right={column={name=pd6, table_ref=ib}}, operator==}}}, 3={table={alias=ix, query={select={1={column={name=pd7, table_ref=ic}}}, from={table={alias=ic, table=tab_c}}, where={condition={left={column={name=pd7, table_ref=ic}}, right={column={name=pd1, table_ref=oa}}, operator==}}}}}}}}, operator==}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[pd1, pd2]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab_a={pd1=[[@41,176:177='oa',<381>,4:37], [@1,7:8='oa',<381>,1:7]], pd3=[[@13,45:46='oa',<381>,2:6]], pd2=[[@5,15:16='oa',<381>,1:15]]}, tab_b={pd6=[[@52,218:219='ib',<381>,5:28]], pd9=[[@19,62:63='ib',<381>,2:23]]}, tab_c={pd7=[[@29,115:116='ic',<381>,3:29], [@37,167:168='ic',<381>,4:28]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={pd7=[[@31,118:120='pd7',<381>,3:32], [@48,209:210='ix',<381>,5:19]]}, query1={pd9=[[@21,65:67='pd9',<381>,2:26]]}, query3={pd1=[[@3,10:12='pd1',<381>,1:10]], pd2=[[@7,18:20='pd2',<381>,1:18]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query3={query_dictionary={pd1=[[@3,10:12='pd1',<381>,1:10]], pd2=[[@7,18:20='pd2',<381>,1:18]]}, table_dictionary={tab_a={pd1=[[@1,7:8='oa',<381>,1:7], [@41,176:177='oa',<381>,4:37]], pd3=[[@13,45:46='oa',<381>,2:6]], pd2=[[@5,15:16='oa',<381>,1:15]]}}, def_query1={query_dictionary={pd9=[[@21,65:67='pd9',<381>,2:26]]}, table_dictionary={tab_b={pd6=[[@52,218:219='ib',<381>,5:28]], pd9=[[@19,62:63='ib',<381>,2:23]]}}, def_query0={query_dictionary={pd7=[[@31,118:120='pd7',<381>,3:32], [@48,209:210='ix',<381>,5:19]]}, table_dictionary={tab_a={pd1=[[@41,176:177='oa',<381>,4:37], [@1,7:8='oa',<381>,1:7]]}, tab_c={pd7=[[@29,115:116='ic',<381>,3:29], [@37,167:168='ic',<381>,4:28]]}}, filters=[{name=pd7, table_ref=ic}, {name=pd1, table_ref=oa}], interface={pd7=[{name=pd7, table_ref=ic}]}, table_alias={ic=tab_c}}, filters=[{name=pd7, table_ref=ix}, {name=pd6, table_ref=ib}], interface={pd9=[{name=pd9, table_ref=ib}]}, table_alias={ib=tab_b, ix=query0}}, dependent_queries={predicand2={query=query1, type=filters}}, filters=[{name=pd3, table_ref=oa}], interface={pd1=[{name=pd1, table_ref=oa}], pd2=[{name=pd2, table_ref=oa}]}, table_alias={oa=tab_a}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void correlatedScalarPredicandLocalQueryAliasMissingColumnTest() {
		// ix is a local derived-table alias (query0); pd_missing is not in query0's output.
		final String query = "SELECT oa.pd1 FROM tab_a AS oa"
		    + "\nWHERE oa.pd3 = (SELECT ib.pd9 FROM tab_b AS ib"
		    + "\nJOIN (SELECT ic.pd7 FROM tab_c AS ic) AS ix"
		    + "\n        ON ix.pd_missing = ib.pd6)";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		Snippet snippet = extractor.getSnippet();

		assertFatalDiagnosticAtPosition(
				snippet,
				"QUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIAS",
				"Qualified column 'pd_missing' at (l:4 c:11) was not found in output interface of query alias 'ix'.",
				"pd_missing",
				4,
				11);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=pd1, table_ref=oa}}}, from={table={alias=oa, table=tab_a}}, where={condition={left={column={name=pd3, table_ref=oa}}, right={select={1={column={name=pd9, table_ref=ib}}}, from={join={1={table={alias=ib, table=tab_b}}, 2={join=JOIN, on={condition={left={column={name=pd_missing, table_ref=ix}}, right={column={name=pd6, table_ref=ib}}, operator==}}}, 3={table={alias=ix, query={select={1={column={name=pd7, table_ref=ic}}}, from={table={alias=ic, table=tab_c}}}}}}}}, operator==}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[pd1]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab_a={pd1=[[@1,7:8='oa',<381>,1:7]], pd3=[[@9,37:38='oa',<381>,2:6]]}, tab_b={pd6=[[@40,149:150='ib',<381>,4:27]], pd9=[[@15,54:55='ib',<381>,2:23]]}, tab_c={pd7=[[@25,91:92='ic',<381>,3:13]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={pd7=[[@27,94:96='pd7',<381>,3:16]]}, query1={pd9=[[@17,57:59='pd9',<381>,2:26]]}, query3={pd1=[[@3,10:12='pd1',<381>,1:10]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query3={query_dictionary={pd1=[[@3,10:12='pd1',<381>,1:10]]}, table_dictionary={tab_a={pd1=[[@1,7:8='oa',<381>,1:7]], pd3=[[@9,37:38='oa',<381>,2:6]]}}, def_query1={query_dictionary={pd9=[[@17,57:59='pd9',<381>,2:26]]}, table_dictionary={tab_b={pd6=[[@40,149:150='ib',<381>,4:27]], pd9=[[@15,54:55='ib',<381>,2:23]]}}, def_query0={query_dictionary={pd7=[[@27,94:96='pd7',<381>,3:16]]}, table_dictionary={tab_c={pd7=[[@25,91:92='ic',<381>,3:13]]}}, interface={pd7=[{name=pd7, table_ref=ic}]}, table_alias={ic=tab_c}}, filters=[{name=pd_missing, table_ref=ix}, {name=pd6, table_ref=ib}], interface={pd9=[{name=pd9, table_ref=ib}]}, table_alias={ib=tab_b, ix=query0}}, dependent_queries={predicand2={query=query1, type=filters}}, filters=[{name=pd3, table_ref=oa}], interface={pd1=[{name=pd1, table_ref=oa}]}, table_alias={oa=tab_a}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void correlatedScalarPredicandCteFourScenarioResolveAndFatalTest() {
		// Four resolution paths in one scalar predicand inside w2:
		//   ix.pd7 / bb.b1 outer-correlated resolve; ix.pd_local_bad local query-alias fatal;
		//   ib.pd9 / ib.pd6 resolve to tab_b via join alias ib; bb.b_outer_bad tracked but no fatal.
		// Outer w2 WHERE also exercises bb.x1 = w1.a1: external CTE refs materialize on query0.a1
		// only (not tab_a); follow def_query0.interface {a1 -> aa -> tab_a} for physical lineage.
		final String query = "WITH w1 AS ("
		    + "\n  SELECT aa.a1 FROM tab_a AS aa"
		    + "\n),"
		    + "\nw2 AS ("
		    + "\n  SELECT bb.b1 FROM tab_b AS bb"
		    + "\n  WHERE bb.b1 = ("
		    + "\n    SELECT ib.pd9 FROM tab_b AS ib"
		    + "\n    JOIN (SELECT ic.pd7 FROM tab_c AS ic"
		    + "\n            WHERE ic.pd7 = bb.b1) AS ix"
		    + "\n      ON ix.pd7 = ib.pd6"
		    + "\n     AND ix.pd_local_bad = ib.pd6"
		    + "\n    WHERE ib.pd9 = bb.b1 and ib.a1=w1.a1"
		    + "\n      AND ib.pd9 = bb.b_outer_bad"
		    + "\n  ) and bb.x1 = w1.a1"
		    + "\n)"
		    + "\nSELECT w2.b1 FROM w2";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		Snippet snippet = extractor.getSnippet();

		assertFatalDiagnosticAtPosition(
				snippet,
				"QUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIAS",
				"Qualified column 'pd_local_bad' at (l:11 c:9) was not found in output interface of query alias 'ix'.",
				"pd_local_bad",
				11,
				9);
		assertFatalDiagnosticCount(snippet, null, null, null, 1);

		
		Assert.assertEquals("AST is wrong", "{SQL={with={1={cte={select={1={column={name=a1, table_ref=aa}}}, from={table={alias=aa, table=tab_a}}}, alias=w1}, 2={cte={select={1={column={name=b1, table_ref=bb}}}, from={table={alias=bb, table=tab_b}}, where={and={1={condition={left={column={name=b1, table_ref=bb}}, right={select={1={column={name=pd9, table_ref=ib}}}, from={join={1={table={alias=ib, table=tab_b}}, 2={join=JOIN, on={and={1={condition={left={column={name=pd7, table_ref=ix}}, right={column={name=pd6, table_ref=ib}}, operator==}}, 2={condition={left={column={name=pd_local_bad, table_ref=ix}}, right={column={name=pd6, table_ref=ib}}, operator==}}}}}, 3={table={alias=ix, query={select={1={column={name=pd7, table_ref=ic}}}, from={table={alias=ic, table=tab_c}}, where={condition={left={column={name=pd7, table_ref=ic}}, right={column={name=b1, table_ref=bb}}, operator==}}}}}}}, where={and={1={condition={left={column={name=pd9, table_ref=ib}}, right={column={name=b1, table_ref=bb}}, operator==}}, 2={condition={left={column={name=a1, table_ref=ib}}, right={column={name=a1, table_ref=w1}}, operator==}}, 3={condition={left={column={name=pd9, table_ref=ib}}, right={column={name=b_outer_bad, table_ref=bb}}, operator==}}}}}, operator==}}, 2={condition={left={column={name=x1, table_ref=bb}}, right={column={name=a1, table_ref=w1}}, operator==}}}}}, alias=w2}}, query={select={1={column={name=b1, table_ref=w2}}}, from={table={alias=null, table=w2}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[b1]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab_a={a1=[[@5,22:23='aa',<381>,2:9]]}, tab_b={a1=[[@85,310:311='ib',<381>,12:29]], pd6=[[@65,240:241='ib',<381>,10:18], [@73,274:275='ib',<381>,11:27]], pd9=[[@32,117:118='ib',<381>,7:11], [@77,291:292='ib',<381>,12:10], [@93,332:333='ib',<381>,13:10]], b_outer_bad=[[@97,341:342='bb',<381>,13:19]], x1=[[@102,364:365='bb',<381>,14:8]], b1=[[@54,209:210='bb',<381>,9:27], [@18,65:66='bb',<381>,5:9], [@26,96:97='bb',<381>,6:8], [@81,300:301='bb',<381>,12:19]]}, tab_c={pd7=[[@42,158:159='ic',<381>,8:17], [@50,200:201='ic',<381>,9:18]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query4={b1=[[@20,68:69='b1',<381>,5:12], [@111,387:388='w2',<381>,16:7]]}, query5={b1=[[@113,390:391='b1',<381>,16:10]]}, query0={a1=[[@7,25:26='a1',<381>,2:12], [@89,316:317='w1',<381>,12:35], [@106,372:373='w1',<381>,14:16]]}, query1={pd7=[[@44,161:163='pd7',<381>,8:20], [@61,231:232='ix',<381>,10:9]]}, query2={pd9=[[@34,120:122='pd9',<381>,7:14]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query5={context_list={w1=query0, w2=query4}, query_dictionary={b1=[[@113,390:391='b1',<381>,16:10]]}, def_query0={query_dictionary={a1=[[@7,25:26='a1',<381>,2:12], [@89,316:317='w1',<381>,12:35], [@106,372:373='w1',<381>,14:16]]}, table_dictionary={tab_a={a1=[[@5,22:23='aa',<381>,2:9]]}}, interface={a1=[{name=a1, table_ref=aa}]}, table_alias={aa=tab_a}}, interface={b1=[{name=b1, table_ref=w2}]}, def_query4={context_list={w1=query0}, query_dictionary={b1=[[@20,68:69='b1',<381>,5:12], [@111,387:388='w2',<381>,16:7]]}, table_dictionary={tab_b={b_outer_bad=[[@97,341:342='bb',<381>,13:19]], x1=[[@102,364:365='bb',<381>,14:8]], b1=[[@18,65:66='bb',<381>,5:9], [@26,96:97='bb',<381>,6:8], [@54,209:210='bb',<381>,9:27], [@81,300:301='bb',<381>,12:19]]}}, dependent_queries={predicand3={query=query2, type=filters}}, filters=[{name=b1, table_ref=bb}, {name=x1, table_ref=bb}, {name=a1, table_ref=w1}], interface={b1=[{name=b1, table_ref=bb}]}, table_alias={bb=tab_b, w1=query0}, def_query2={context_list={w1=query0}, query_dictionary={pd9=[[@34,120:122='pd9',<381>,7:14]]}, table_dictionary={tab_b={a1=[[@85,310:311='ib',<381>,12:29]], pd6=[[@65,240:241='ib',<381>,10:18], [@73,274:275='ib',<381>,11:27]], pd9=[[@32,117:118='ib',<381>,7:11], [@77,291:292='ib',<381>,12:10], [@93,332:333='ib',<381>,13:10]]}}, def_query1={context_list={w1=query0}, query_dictionary={pd7=[[@44,161:163='pd7',<381>,8:20], [@61,231:232='ix',<381>,10:9]]}, table_dictionary={tab_b={b1=[[@54,209:210='bb',<381>,9:27], [@18,65:66='bb',<381>,5:9], [@26,96:97='bb',<381>,6:8], [@81,300:301='bb',<381>,12:19]]}, tab_c={pd7=[[@42,158:159='ic',<381>,8:17], [@50,200:201='ic',<381>,9:18]]}}, filters=[{name=pd7, table_ref=ic}, {name=b1, table_ref=bb}], interface={pd7=[{name=pd7, table_ref=ic}]}, table_alias={w1=query0, ic=tab_c}}, filters=[{name=pd7, table_ref=ix}, {name=pd6, table_ref=ib}, {name=pd_local_bad, table_ref=ix}, {name=pd9, table_ref=ib}, {name=b1, table_ref=bb}, {name=a1, table_ref=ib}, {name=a1, table_ref=w1}, {name=b_outer_bad, table_ref=bb}], interface={pd9=[{name=pd9, table_ref=ib}]}, table_alias={ib=tab_b, w1=query0, ix=query1}}}, table_alias={w1=query0, w2=query4}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void correlatedScalarPredicandNestedCteFourScenarioResolveAndFatalTest() {
		// Same four-scenario predicand as single-level, nested inside outer_cte > inner_cte.
		final String query = "WITH outer_cte AS ("
		    + "\n  WITH w1 AS ("
		    + "\n    SELECT aa.a1 FROM tab_a AS aa"
		    + "\n  ),"
		    + "\n  inner_cte AS ("
		    + "\n    SELECT bb.b1 FROM tab_b AS bb"
		    + "\n    WHERE bb.b1 = ("
		    + "\n      SELECT ib.pd9 FROM tab_b AS ib"
		    + "\n      JOIN (SELECT ic.pd7 FROM tab_c AS ic"
		    + "\n              WHERE ic.pd7 = bb.b1) AS ix"
		    + "\n        ON ix.pd7 = ib.pd6"
		    + "\n       AND ix.pd_local_bad = ib.pd6"
		    + "\n      WHERE ib.pd9 = bb.b1"
		    + "\n        AND ib.pd9 = bb.b_outer_bad"
		    + "\n    )"
		    + "\n  )"
		    + "\n  SELECT ic.b1 FROM inner_cte AS ic"
		    + "\n)"
		    + "\nSELECT oc.b1 FROM outer_cte AS oc";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		Snippet snippet = extractor.getSnippet();

		assertFatalDiagnosticAtPosition(
				snippet,
				"QUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIAS",
				"Qualified column 'pd_local_bad' at (l:12 c:11) was not found in output interface of query alias 'ix'.",
				"pd_local_bad",
				12,
				11);
		
		Assert.assertEquals("AST is wrong", "{SQL={with={1={cte={with={1={cte={select={1={column={name=a1, table_ref=aa}}}, from={table={alias=aa, table=tab_a}}}, alias=w1}, 2={cte={select={1={column={name=b1, table_ref=bb}}}, from={table={alias=bb, table=tab_b}}, where={condition={left={column={name=b1, table_ref=bb}}, right={select={1={column={name=pd9, table_ref=ib}}}, from={join={1={table={alias=ib, table=tab_b}}, 2={join=JOIN, on={and={1={condition={left={column={name=pd7, table_ref=ix}}, right={column={name=pd6, table_ref=ib}}, operator==}}, 2={condition={left={column={name=pd_local_bad, table_ref=ix}}, right={column={name=pd6, table_ref=ib}}, operator==}}}}}, 3={table={alias=ix, query={select={1={column={name=pd7, table_ref=ic}}}, from={table={alias=ic, table=tab_c}}, where={condition={left={column={name=pd7, table_ref=ic}}, right={column={name=b1, table_ref=bb}}, operator==}}}}}}}, where={and={1={condition={left={column={name=pd9, table_ref=ib}}, right={column={name=b1, table_ref=bb}}, operator==}}, 2={condition={left={column={name=pd9, table_ref=ib}}, right={column={name=b_outer_bad, table_ref=bb}}, operator==}}}}}, operator==}}}, alias=inner_cte}}, query={select={1={column={name=b1, table_ref=ic}}}, from={table={alias=ic, table=inner_cte}}}}, alias=outer_cte}}, query={select={1={column={name=b1, table_ref=oc}}}, from={table={alias=oc, table=outer_cte}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[b1]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab_a={a1=[[@9,46:47='aa',<381>,3:11]]}, tab_b={pd6=[[@69,287:288='ib',<381>,11:20], [@77,323:324='ib',<381>,12:29]], pd9=[[@36,158:159='ib',<381>,8:13], [@81,342:343='ib',<381>,13:12], [@89,369:370='ib',<381>,14:12]], b_outer_bad=[[@93,378:379='bb',<381>,14:21]], b1=[[@58,254:255='bb',<381>,10:29], [@22,102:103='bb',<381>,6:11], [@30,135:136='bb',<381>,7:10], [@85,351:352='bb',<381>,13:21]]}, tab_c={pd7=[[@46,201:202='ic',<381>,9:19], [@54,245:246='ic',<381>,10:20]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query4={b1=[[@24,105:106='b1',<381>,6:14], [@99,412:413='ic',<381>,17:9]]}, query5={b1=[[@101,415:416='b1',<381>,17:12], [@108,448:449='oc',<381>,19:7]]}, query6={b1=[[@110,451:452='b1',<381>,19:10]]}, query0={a1=[[@11,49:50='a1',<381>,3:14]]}, query1={pd7=[[@48,204:206='pd7',<381>,9:22], [@65,278:279='ix',<381>,11:11]]}, query2={pd9=[[@38,161:163='pd9',<381>,8:16]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query6={context_list={outer_cte=query5, oc=query5}, query_dictionary={b1=[[@110,451:452='b1',<381>,19:10]]}, interface={b1=[{name=b1, table_ref=oc}]}, def_query5={context_list={w1=query0, inner_cte=query4, ic=query4}, query_dictionary={b1=[[@101,415:416='b1',<381>,17:12], [@108,448:449='oc',<381>,19:7]]}, def_query0={query_dictionary={a1=[[@11,49:50='a1',<381>,3:14]]}, table_dictionary={tab_a={a1=[[@9,46:47='aa',<381>,3:11]]}}, interface={a1=[{name=a1, table_ref=aa}]}, table_alias={aa=tab_a}}, interface={b1=[{name=b1, table_ref=ic}]}, def_query4={context_list={w1=query0}, query_dictionary={b1=[[@24,105:106='b1',<381>,6:14], [@99,412:413='ic',<381>,17:9]]}, table_dictionary={tab_b={b_outer_bad=[[@93,378:379='bb',<381>,14:21]], b1=[[@22,102:103='bb',<381>,6:11], [@30,135:136='bb',<381>,7:10], [@58,254:255='bb',<381>,10:29], [@85,351:352='bb',<381>,13:21]]}}, dependent_queries={predicand3={query=query2, type=filters}}, filters=[{name=b1, table_ref=bb}], interface={b1=[{name=b1, table_ref=bb}]}, table_alias={bb=tab_b, w1=query0}, def_query2={context_list={w1=query0}, query_dictionary={pd9=[[@38,161:163='pd9',<381>,8:16]]}, table_dictionary={tab_b={pd6=[[@69,287:288='ib',<381>,11:20], [@77,323:324='ib',<381>,12:29]], pd9=[[@36,158:159='ib',<381>,8:13], [@81,342:343='ib',<381>,13:12], [@89,369:370='ib',<381>,14:12]]}}, def_query1={context_list={w1=query0}, query_dictionary={pd7=[[@48,204:206='pd7',<381>,9:22], [@65,278:279='ix',<381>,11:11]]}, table_dictionary={tab_b={b1=[[@58,254:255='bb',<381>,10:29], [@22,102:103='bb',<381>,6:11], [@30,135:136='bb',<381>,7:10], [@85,351:352='bb',<381>,13:21]]}, tab_c={pd7=[[@46,201:202='ic',<381>,9:19], [@54,245:246='ic',<381>,10:20]]}}, filters=[{name=pd7, table_ref=ic}, {name=b1, table_ref=bb}], interface={pd7=[{name=pd7, table_ref=ic}]}, table_alias={w1=query0, ic=tab_c}}, filters=[{name=pd7, table_ref=ix}, {name=pd6, table_ref=ib}, {name=pd_local_bad, table_ref=ix}, {name=pd9, table_ref=ib}, {name=b1, table_ref=bb}, {name=b_outer_bad, table_ref=bb}], interface={pd9=[{name=pd9, table_ref=ib}]}, table_alias={ib=tab_b, w1=query0, ix=query1}}}, table_alias={inner_cte=query4, w1=query0, ic=query4}}, table_alias={oc=query5, outer_cte=query5}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void correlatedScalarPredicandCteUnionBodyFourScenarioResolveAndFatalTest() {
		// Union alias u in outer WHERE; scalar predicand inside tests u.pu1 resolve, u.pu_outer_bad fatal,
		// ix.pd7 resolve, ix.pd_local_bad fatal, plus ib.pd9/pd6 resolve to tab_b via join alias ib.
		final String query = "WITH w1 AS ("
		    + "\n  SELECT aa.a1 FROM tab_a AS aa"
		    + "\n)"
		    + "\nSELECT u.pu1 FROM ("
		    + "\n  SELECT ub.pu1 FROM tab_b AS ub"
		    + "\n  UNION SELECT uc.pu1 FROM tab_c AS uc"
		    + "\n) AS u"
		    + "\nWHERE u.pu1 = ("
		    + "\n  SELECT ib.pd9 FROM tab_b AS ib"
		    + "\n  JOIN (SELECT ic.pd7 FROM tab_c AS ic"
		    + "\n          WHERE ic.pd7 = u.pu1) AS ix"
		    + "\n    ON ix.pd7 = ib.pd6"
		    + "\n   AND ix.pd_local_bad = ib.pd6"
		    + "\n  WHERE ib.pd9 = u.pu1"
		    + "\n    AND ib.pd9 = u.pu_outer_bad"
		    + "\n)";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		Snippet snippet = extractor.getSnippet();

		assertFatalDiagnosticAtPosition(
				snippet,
				"QUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIAS",
				"Qualified column 'pd_local_bad' at (l:13 c:7) was not found in output interface of query alias 'ix'.",
				"pd_local_bad",
				13,
				7);
		assertFatalDiagnosticAtPosition(
				snippet,
				"QUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIAS",
				"Qualified column 'pu_outer_bad' at (l:15 c:17) was not found in output interface of query alias 'u'.",
				"pu_outer_bad",
				15,
				17);
		
		Assert.assertEquals("AST is wrong", "{SQL={with={1={cte={select={1={column={name=a1, table_ref=aa}}}, from={table={alias=aa, table=tab_a}}}, alias=w1}}, query={select={1={column={name=pu1, table_ref=u}}}, from={table={alias=u, query={union={1={select={1={column={name=pu1, table_ref=ub}}}, from={table={alias=ub, table=tab_b}}}, 2={union={qualifier=null, operator=UNION}}, 3={select={1={column={name=pu1, table_ref=uc}}}, from={table={alias=uc, table=tab_c}}}}}}}, where={condition={left={column={name=pu1, table_ref=u}}, right={select={1={column={name=pd9, table_ref=ib}}}, from={join={1={table={alias=ib, table=tab_b}}, 2={join=JOIN, on={and={1={condition={left={column={name=pd7, table_ref=ix}}, right={column={name=pd6, table_ref=ib}}, operator==}}, 2={condition={left={column={name=pd_local_bad, table_ref=ix}}, right={column={name=pd6, table_ref=ib}}, operator==}}}}}, 3={table={alias=ix, query={select={1={column={name=pd7, table_ref=ic}}}, from={table={alias=ic, table=tab_c}}, where={condition={left={column={name=pd7, table_ref=ic}}, right={column={name=pu1, table_ref=u}}, operator==}}}}}}}, where={and={1={condition={left={column={name=pd9, table_ref=ib}}, right={column={name=pu1, table_ref=u}}, operator==}}, 2={condition={left={column={name=pd9, table_ref=ib}}, right={column={name=pu_outer_bad, table_ref=u}}, operator==}}}}}, operator==}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[pu1]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab_a={a1=[[@5,22:23='aa',<381>,2:9]]}, tab_b={pd6=[[@79,288:289='ib',<381>,12:16], [@87,320:321='ib',<381>,13:25]], pd9=[[@46,171:172='ib',<381>,9:9], [@91,335:336='ib',<381>,14:8], [@99,358:359='ib',<381>,15:8]], pu1=[[@20,76:77='ub',<381>,5:9]]}, tab_c={pd7=[[@56,210:211='ic',<381>,10:15], [@64,250:251='ic',<381>,11:16]], pu1=[[@29,115:116='uc',<381>,6:15]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{union3={pu1=[[@68,259:259='u',<381>,11:25], [@14,54:54='u',<381>,4:7], [@40,152:152='u',<381>,8:6], [@95,344:344='u',<381>,14:17]]}, query4={pd7=[[@58,213:215='pd7',<381>,10:18], [@75,279:280='ix',<381>,12:7]]}, query5={pd9=[[@48,174:176='pd9',<381>,9:12]]}, query7={pu1=[[@16,56:58='pu1',<381>,4:9]]}, query0={a1=[[@7,25:26='a1',<381>,2:12]]}, query1={pu1=[[@22,79:81='pu1',<381>,5:12]]}, query2={pu1=[[@31,118:120='pu1',<381>,6:18]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query7={def_union3={context_list={w1=query0}, query_dictionary={pu1=[[@68,259:259='u',<381>,11:25], [@14,54:54='u',<381>,4:7], [@40,152:152='u',<381>,8:6], [@95,344:344='u',<381>,14:17]]}, def_query1={context_list={w1=query0}, query_dictionary={pu1=[[@22,79:81='pu1',<381>,5:12]]}, table_dictionary={tab_b={pu1=[[@20,76:77='ub',<381>,5:9]]}}, interface={pu1=[{name=pu1, table_ref=ub}]}, table_alias={w1=query0, ub=tab_b}}, interface={pu1=[{name=pu1, table_ref=ub}, {name=pu1, table_ref=uc}]}, table_alias={w1=query0}, def_query2={context_list={w1=query0}, query_dictionary={pu1=[[@31,118:120='pu1',<381>,6:18]]}, table_dictionary={tab_c={pu1=[[@29,115:116='uc',<381>,6:15]]}}, interface={pu1=[{name=pu1, table_ref=uc}]}, table_alias={w1=query0, uc=tab_c}}}, context_list={w1=query0}, query_dictionary={pu1=[[@16,56:58='pu1',<381>,4:9]]}, dependent_queries={predicand6={query=query5, type=filters}}, def_query0={query_dictionary={a1=[[@7,25:26='a1',<381>,2:12]]}, table_dictionary={tab_a={a1=[[@5,22:23='aa',<381>,2:9]]}}, interface={a1=[{name=a1, table_ref=aa}]}, table_alias={aa=tab_a}}, filters=[{name=pu1, table_ref=u}], def_query5={context_list={w1=query0}, query_dictionary={pd9=[[@48,174:176='pd9',<381>,9:12]]}, table_dictionary={tab_b={pd6=[[@79,288:289='ib',<381>,12:16], [@87,320:321='ib',<381>,13:25]], pd9=[[@46,171:172='ib',<381>,9:9], [@91,335:336='ib',<381>,14:8], [@99,358:359='ib',<381>,15:8]]}}, filters=[{name=pd7, table_ref=ix}, {name=pd6, table_ref=ib}, {name=pd_local_bad, table_ref=ix}, {name=pd9, table_ref=ib}, {name=pu1, table_ref=u}, {name=pu_outer_bad, table_ref=u}], interface={pd9=[{name=pd9, table_ref=ib}]}, def_query4={context_list={w1=query0}, query_dictionary={pd7=[[@58,213:215='pd7',<381>,10:18], [@75,279:280='ix',<381>,12:7]]}, table_dictionary={tab_c={pd7=[[@56,210:211='ic',<381>,10:15], [@64,250:251='ic',<381>,11:16]]}}, filters=[{name=pd7, table_ref=ic}, {name=pu1, table_ref=u}], interface={pd7=[{name=pd7, table_ref=ic}]}, table_alias={w1=query0, ic=tab_c}}, table_alias={ib=tab_b, w1=query0, ix=query4}}, interface={pu1=[{name=pu1, table_ref=u}]}, table_alias={u=union3, w1=query0}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void simpleWithColumnReferenceTest() {
		final String query = "WITH w2 AS ("
		    + "\n  SELECT bb.b1 FROM tab_b AS bb"
		    + "\n  GROUP BY bb.b_group_bad"
		    + "\n)"
		    + "\nSELECT w2.b1 FROM w2";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		Snippet snippet = extractor.getSnippet();
		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={with={1={cte={select={1={column={name=b1, table_ref=bb}}}, from={table={alias=bb, table=tab_b}}, groupby={1={column={name=b_group_bad, table_ref=bb}}}}, alias=w2}}, query={select={1={column={name=b1, table_ref=w2}}}, from={table={alias=null, table=w2}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[b1]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab_b={b_group_bad=[[@14,56:57='bb',<381>,3:11]], b1=[[@5,22:23='bb',<381>,2:9]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={b1=[[@7,25:26='b1',<381>,2:12], [@19,80:81='w2',<381>,5:7]]}, query1={b1=[[@21,83:84='b1',<381>,5:10]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query1={context_list={w2=query0}, query_dictionary={b1=[[@21,83:84='b1',<381>,5:10]]}, def_query0={query_dictionary={b1=[[@7,25:26='b1',<381>,2:12], [@19,80:81='w2',<381>,5:7]]}, table_dictionary={tab_b={b_group_bad=[[@14,56:57='bb',<381>,3:11]], b1=[[@5,22:23='bb',<381>,2:9]]}}, grouped_by=[{name=b_group_bad, table_ref=bb}], interface={b1=[{name=b1, table_ref=bb}]}, table_alias={bb=tab_b}}, interface={b1=[{name=b1, table_ref=w2}]}, table_alias={w2=query0}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void correlatedScalarPredicandPlainUnionBranchOuterFatalTest() {
		// ua.pu_branch_bad appears in a UNION branch WHERE but ua is outer-correlated; the alias
		// resolves uniquely to tab_a at the enclosing query — no fatal inside the set-op scope.
		final String query = "SELECT ua.pu1 FROM tab_a AS ua"
		    + "\nWHERE ua.pu2 = (SELECT max(sub.pu4) FROM ("
		    + "\nSELECT ub.pu4 FROM tab_b AS ub"
		    + "\n        WHERE ub.pu5 = ua.pu1 AND ua.pu_branch_bad = ua.pu1"
		    + "\nUNION SELECT uc.pu4 FROM tab_c AS uc"
		    + "\n        WHERE uc.pu6 = ua.pu1) AS sub)";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		assertNoFatalErrors(extractor);
	
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=pu1, table_ref=ua}}}, from={table={alias=ua, table=tab_a}}, where={condition={left={column={name=pu2, table_ref=ua}}, right={select={1={function={function_name=max, qualifier=null, parameters={column={name=pu4, table_ref=sub}}}}}, from={table={alias=sub, query={union={1={select={1={column={name=pu4, table_ref=ub}}}, from={table={alias=ub, table=tab_b}}, where={and={1={condition={left={column={name=pu5, table_ref=ub}}, right={column={name=pu1, table_ref=ua}}, operator==}}, 2={condition={left={column={name=pu_branch_bad, table_ref=ua}}, right={column={name=pu1, table_ref=ua}}, operator==}}}}}, 2={union={qualifier=null, operator=UNION}}, 3={select={1={column={name=pu4, table_ref=uc}}}, from={table={alias=uc, table=tab_c}}, where={condition={left={column={name=pu6, table_ref=uc}}, right={column={name=pu1, table_ref=ua}}, operator==}}}}}}}}, operator==}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[pu1]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab_a={pu2=[[@9,37:38='ua',<381>,2:6]], pu1=[[@36,128:129='ua',<381>,4:23], [@44,158:159='ua',<381>,4:53], [@61,225:226='ua',<381>,6:23], [@1,7:8='ua',<381>,1:7]], pu_branch_bad=[[@40,139:140='ua',<381>,4:34]]}, tab_b={pu5=[[@32,119:120='ub',<381>,4:14]], pu4=[[@24,81:82='ub',<381>,3:7]]}, tab_c={pu6=[[@57,216:217='uc',<381>,6:14]], pu4=[[@49,178:179='uc',<381>,5:13]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{union2={pu4=[[@17,58:60='sub',<381>,2:27]]}, query5={pu1=[[@3,10:12='pu1',<381>,1:10]]}, query0={pu4=[[@26,84:86='pu4',<381>,3:10]]}, query1={pu4=[[@51,181:183='pu4',<381>,5:16]]}, query3={unnamed_0=[[@20,65:65=')',<288>,2:34]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query5={query_dictionary={pu1=[[@3,10:12='pu1',<381>,1:10]]}, table_dictionary={tab_a={pu2=[[@9,37:38='ua',<381>,2:6]], pu1=[[@1,7:8='ua',<381>,1:7], [@36,128:129='ua',<381>,4:23], [@44,158:159='ua',<381>,4:53]]}}, dependent_queries={predicand4={query=query3, type=filters}}, filters=[{name=pu2, table_ref=ua}], interface={pu1=[{name=pu1, table_ref=ua}]}, def_query3={def_union2={query_dictionary={pu4=[[@17,58:60='sub',<381>,2:27]]}, def_query1={query_dictionary={pu4=[[@51,181:183='pu4',<381>,5:16]]}, table_dictionary={tab_a={pu1=[[@61,225:226='ua',<381>,6:23]]}, tab_c={pu6=[[@57,216:217='uc',<381>,6:14]], pu4=[[@49,178:179='uc',<381>,5:13]]}}, filters=[{name=pu6, table_ref=uc}, {name=pu1, table_ref=ua}], interface={pu4=[{name=pu4, table_ref=uc}]}, table_alias={uc=tab_c}}, def_query0={query_dictionary={pu4=[[@26,84:86='pu4',<381>,3:10]]}, table_dictionary={tab_a={pu1=[[@36,128:129='ua',<381>,4:23], [@44,158:159='ua',<381>,4:53], [@61,225:226='ua',<381>,6:23], [@1,7:8='ua',<381>,1:7]], pu_branch_bad=[[@40,139:140='ua',<381>,4:34]]}, tab_b={pu5=[[@32,119:120='ub',<381>,4:14]], pu4=[[@24,81:82='ub',<381>,3:7]]}}, filters=[{name=pu5, table_ref=ub}, {name=pu1, table_ref=ua}, {name=pu_branch_bad, table_ref=ua}], interface={pu4=[{name=pu4, table_ref=ub}]}, table_alias={ub=tab_b}}, interface={pu4=[{name=pu4, table_ref=ub}, {name=pu4, table_ref=uc}]}}, query_dictionary={unnamed_0=[[@20,65:65=')',<288>,2:34]]}, interface={unnamed_0=[{name=pu4, table_ref=sub}]}, table_alias={sub=union2}}, table_alias={ua=tab_a}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void correlatedScalarPredicandUnionContextSubqueryTest() {
		final String query = "SELECT ua.pu1 FROM tab_a AS ua"
		    + "\nWHERE ua.pu2 = (SELECT max(sub.pu4) FROM ("
		    + "\nSELECT ub.pu4 FROM tab_b AS ub"
		    + "\n        WHERE ub.pu5 = ua.pu1"
		    + "\nUNION SELECT uc.pu4 FROM tab_c AS uc"
		    + "\n        WHERE uc.pu6 = ua.pu1) AS sub)";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		assertNoFatalErrors(extractor);

		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=pu1, table_ref=ua}}}, from={table={alias=ua, table=tab_a}}, where={condition={left={column={name=pu2, table_ref=ua}}, right={select={1={function={function_name=max, qualifier=null, parameters={column={name=pu4, table_ref=sub}}}}}, from={table={alias=sub, query={union={1={select={1={column={name=pu4, table_ref=ub}}}, from={table={alias=ub, table=tab_b}}, where={condition={left={column={name=pu5, table_ref=ub}}, right={column={name=pu1, table_ref=ua}}, operator==}}}, 2={union={qualifier=null, operator=UNION}}, 3={select={1={column={name=pu4, table_ref=uc}}}, from={table={alias=uc, table=tab_c}}, where={condition={left={column={name=pu6, table_ref=uc}}, right={column={name=pu1, table_ref=ua}}, operator==}}}}}}}}, operator==}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[pu1]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab_a={pu2=[[@9,37:38='ua',<381>,2:6]], pu1=[[@36,128:129='ua',<381>,4:23], [@53,195:196='ua',<381>,6:23], [@1,7:8='ua',<381>,1:7]]}, tab_b={pu5=[[@32,119:120='ub',<381>,4:14]], pu4=[[@24,81:82='ub',<381>,3:7]]}, tab_c={pu6=[[@49,186:187='uc',<381>,6:14]], pu4=[[@41,148:149='uc',<381>,5:13]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{union2={pu4=[[@17,58:60='sub',<381>,2:27]]}, query5={pu1=[[@3,10:12='pu1',<381>,1:10]]}, query0={pu4=[[@26,84:86='pu4',<381>,3:10]]}, query1={pu4=[[@43,151:153='pu4',<381>,5:16]]}, query3={unnamed_0=[[@20,65:65=')',<288>,2:34]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query5={query_dictionary={pu1=[[@3,10:12='pu1',<381>,1:10]]}, table_dictionary={tab_a={pu2=[[@9,37:38='ua',<381>,2:6]], pu1=[[@1,7:8='ua',<381>,1:7], [@36,128:129='ua',<381>,4:23]]}}, dependent_queries={predicand4={query=query3, type=filters}}, filters=[{name=pu2, table_ref=ua}], interface={pu1=[{name=pu1, table_ref=ua}]}, def_query3={def_union2={query_dictionary={pu4=[[@17,58:60='sub',<381>,2:27]]}, def_query1={query_dictionary={pu4=[[@43,151:153='pu4',<381>,5:16]]}, table_dictionary={tab_a={pu1=[[@53,195:196='ua',<381>,6:23]]}, tab_c={pu6=[[@49,186:187='uc',<381>,6:14]], pu4=[[@41,148:149='uc',<381>,5:13]]}}, filters=[{name=pu6, table_ref=uc}, {name=pu1, table_ref=ua}], interface={pu4=[{name=pu4, table_ref=uc}]}, table_alias={uc=tab_c}}, def_query0={query_dictionary={pu4=[[@26,84:86='pu4',<381>,3:10]]}, table_dictionary={tab_a={pu1=[[@36,128:129='ua',<381>,4:23], [@53,195:196='ua',<381>,6:23], [@1,7:8='ua',<381>,1:7]]}, tab_b={pu5=[[@32,119:120='ub',<381>,4:14]], pu4=[[@24,81:82='ub',<381>,3:7]]}}, filters=[{name=pu5, table_ref=ub}, {name=pu1, table_ref=ua}], interface={pu4=[{name=pu4, table_ref=ub}]}, table_alias={ub=tab_b}}, interface={pu4=[{name=pu4, table_ref=ub}, {name=pu4, table_ref=uc}]}}, query_dictionary={unnamed_0=[[@20,65:65=')',<288>,2:34]]}, interface={unnamed_0=[{name=pu4, table_ref=sub}]}, table_alias={sub=union2}}, table_alias={ua=tab_a}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void correlatedScalarPredicandIntersectContextSubqueryTest() {
		final String query = "SELECT oi.oi1,"
		    + "\n       (SELECT max(ii.px1) FROM ("
		    + "\nSELECT id.px1 FROM tab_d AS id"
		    + "\n        WHERE id.px2 = oi.oi1"
		    + "\nINTERSECT SELECT ie.px1 FROM tab_e AS ie"
		    + "\n        WHERE ie.px3 = oi.oi2) AS ii) AS px_max"
		    + "\nFROM tab_o AS oi";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		assertNoFatalErrors(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=oi1, table_ref=oi}}, 2={lookup={from={table={alias=ii, query={intersect={1={select={1={column={name=px1, table_ref=id}}}, from={table={alias=id, table=tab_d}}, where={condition={left={column={name=px2, table_ref=id}}, right={column={name=oi1, table_ref=oi}}, operator==}}}, 2={intersect={qualifier=null, operator=INTERSECT}}, 3={select={1={column={name=px1, table_ref=ie}}}, from={table={alias=ie, table=tab_e}}, where={condition={left={column={name=px3, table_ref=ie}}, right={column={name=oi2, table_ref=oi}}, operator==}}}}}}}, select={1={function={function_name=max, qualifier=null, parameters={column={name=px1, table_ref=ii}}}}}}, alias=px_max}}, from={table={alias=oi, table=tab_o}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[oi1, px_max]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab_o={oi1=[[@1,7:8='oi',<381>,1:7], [@28,103:104='oi',<381>,4:23]], oi2=[[@45,174:175='oi',<381>,6:23]]}, tab_d={px1=[[@16,56:57='id',<381>,3:7]], px2=[[@24,94:95='id',<381>,4:14]]}, tab_e={px1=[[@33,127:128='ie',<381>,5:17]], px3=[[@41,165:166='ie',<381>,6:14]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{intersect2={px1=[[@9,34:35='ii',<381>,2:19]]}, query5={oi1=[[@3,10:12='oi1',<381>,1:10]], px_max=[[@53,192:197='px_max',<381>,6:41]]}, query0={px1=[[@18,59:61='px1',<381>,3:10]]}, query1={px1=[[@35,130:132='px1',<381>,5:20]]}, query3={unnamed_0=[[@12,40:40=')',<288>,2:25]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query5={query_dictionary={oi1=[[@3,10:12='oi1',<381>,1:10]], px_max=[[@53,192:197='px_max',<381>,6:41]]}, table_dictionary={tab_o={oi1=[[@1,7:8='oi',<381>,1:7], [@28,103:104='oi',<381>,4:23]], oi2=[[@45,174:175='oi',<381>,6:23]]}}, dependent_queries={predicand4={query=query3, type=interface}}, interface={oi1=[{name=oi1, table_ref=oi}], px_max=[{name=px1, table_ref=id}, {name=px2, table_ref=id}, {name=oi1, table_ref=oi}, {name=px1, table_ref=ie}, {name=px3, table_ref=ie}, {name=oi2, table_ref=oi}, {name=px1, table_ref=ii}]}, def_query3={query_dictionary={unnamed_0=[[@12,40:40=')',<288>,2:25]]}, def_intersect2={query_dictionary={px1=[[@9,34:35='ii',<381>,2:19]]}, def_query1={query_dictionary={px1=[[@35,130:132='px1',<381>,5:20]]}, table_dictionary={tab_e={px1=[[@33,127:128='ie',<381>,5:17]], px3=[[@41,165:166='ie',<381>,6:14]]}}, filters=[{name=px3, table_ref=ie}, {name=oi2, table_ref=oi}], interface={px1=[{name=px1, table_ref=ie}]}, table_alias={ie=tab_e}}, def_query0={query_dictionary={px1=[[@18,59:61='px1',<381>,3:10]]}, table_dictionary={tab_d={px1=[[@16,56:57='id',<381>,3:7]], px2=[[@24,94:95='id',<381>,4:14]]}}, filters=[{name=px2, table_ref=id}, {name=oi1, table_ref=oi}], interface={px1=[{name=px1, table_ref=id}]}, table_alias={id=tab_d}}, interface={px1=[{name=px1, table_ref=id}, {name=px1, table_ref=ie}]}}, interface={unnamed_0=[{name=px1, table_ref=ii}]}, table_alias={ii=intersect2}}, table_alias={oi=tab_o}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void correlatedScalarPredicandWithNestedInSubqueryTest() {
		final String query = "SELECT sa.sv1 FROM tab_s AS sa"
		    + "\nWHERE (SELECT max(ia.iv1) FROM tab_i AS ia"
		    + "\n        WHERE ia.iv2 IN ("
		    + "\nSELECT jb.jv1 FROM tab_j AS jb"
		    + "\n        WHERE jb.jv2 = sa.sv2)) > sa.sv3";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		assertNoFatalErrors(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=sv1, table_ref=sa}}}, from={table={alias=sa, table=tab_s}}, where={condition={left={select={1={function={function_name=max, qualifier=null, parameters={column={name=iv1, table_ref=ia}}}}}, from={table={alias=ia, table=tab_i}}, where={in={item={column={name=iv2, table_ref=ia}}, in_list={select={1={column={name=jv1, table_ref=jb}}}, from={table={alias=jb, table=tab_j}}, where={condition={left={column={name=jv2, table_ref=jb}}, right={column={name=sv2, table_ref=sa}}, operator==}}}}}}, right={column={name=sv3, table_ref=sa}}, operator=>}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[sv1]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab_j={jv1=[[@28,107:108='jb',<381>,4:7]], jv2=[[@36,145:146='jb',<381>,5:14]]}, tab_s={sv3=[[@46,165:166='sa',<381>,5:34]], sv2=[[@40,154:155='sa',<381>,5:23]], sv1=[[@1,7:8='sa',<381>,1:7]]}, tab_i={iv2=[[@22,88:89='ia',<381>,3:14]], iv1=[[@13,49:50='ia',<381>,2:18]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query4={sv1=[[@3,10:12='sv1',<381>,1:10]]}, query0={jv1=[[@30,110:112='jv1',<381>,4:10]]}, query2={unnamed_0=[[@16,55:55=')',<288>,2:24]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query4={query_dictionary={sv1=[[@3,10:12='sv1',<381>,1:10]]}, table_dictionary={tab_s={sv3=[[@46,165:166='sa',<381>,5:34]], sv2=[[@40,154:155='sa',<381>,5:23]], sv1=[[@1,7:8='sa',<381>,1:7]]}}, dependent_queries={predicand3={query=query2, type=filters}}, filters=[{name=sv3, table_ref=sa}], interface={sv1=[{name=sv1, table_ref=sa}]}, table_alias={sa=tab_s}, def_query2={query_dictionary={unnamed_0=[[@16,55:55=')',<288>,2:24]]}, table_dictionary={tab_i={iv2=[[@22,88:89='ia',<381>,3:14]], iv1=[[@13,49:50='ia',<381>,2:18]]}}, dependent_queries={in_list1={query=query0, type=filters}}, def_query0={query_dictionary={jv1=[[@30,110:112='jv1',<381>,4:10]]}, table_dictionary={tab_j={jv1=[[@28,107:108='jb',<381>,4:7]], jv2=[[@36,145:146='jb',<381>,5:14]]}}, filters=[{name=jv2, table_ref=jb}, {name=sv2, table_ref=sa}], interface={jv1=[{name=jv1, table_ref=jb}]}, table_alias={jb=tab_j}}, filters=[], interface={unnamed_0=[{name=iv1, table_ref=ia}]}, table_alias={ia=tab_i}}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void correlatedScalarPredicandWithNestedExistsSubqueryTest() {
		final String query = "SELECT ea.ev1 FROM tab_e AS ea"
		    + "\nWHERE (SELECT count(ex.ex1) FROM tab_x AS ex"
		    + "\n        WHERE ex.ex2 = ea.ev2 AND EXISTS ("
		    + "\nSELECT 1 FROM tab_y AS ey"
		    + "\n        WHERE ey.ey1 = ex.ex3"
		    + "\n          AND ey.ey2 = ea.ev1)) > 0";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		assertNoFatalErrors(extractor);

		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=ev1, table_ref=ea}}}, from={table={alias=ea, table=tab_e}}, where={condition={left={select={1={function={function_name=count, qualifier=null, parameters={column={name=ex1, table_ref=ex}}}}}, from={table={alias=ex, table=tab_x}}, where={and={1={condition={left={column={name=ex2, table_ref=ex}}, right={column={name=ev2, table_ref=ea}}, operator==}}, 2={exists={select={1={literal=1}}, from={table={alias=ey, table=tab_y}}, where={and={1={condition={left={column={name=ey1, table_ref=ey}}, right={column={name=ex3, table_ref=ex}}, operator==}}, 2={condition={left={column={name=ey2, table_ref=ey}}, right={column={name=ev1, table_ref=ea}}, operator==}}}}, operator=EXISTS}}}}}, right={literal=0}, operator=>}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[ev1]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab_e={ev2=[[@26,99:100='ea',<381>,3:23]], ev1=[[@1,7:8='ea',<381>,1:7], [@51,198:199='ea',<381>,6:23]]}, tab_x={ex3=[[@43,168:169='ex',<381>,5:23]], ex2=[[@22,90:91='ex',<381>,3:14]], ex1=[[@13,51:52='ex',<381>,2:20]]}, tab_y={ey1=[[@39,159:160='ey',<381>,5:14]], ey2=[[@47,189:190='ey',<381>,6:14]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query4={ev1=[[@3,10:12='ev1',<381>,1:10]]}, query0={unnamed_1=[[@33,126:126='1',<300>,4:7]]}, query2={unnamed_0=[[@16,57:57=')',<288>,2:26]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query4={query_dictionary={ev1=[[@3,10:12='ev1',<381>,1:10]]}, table_dictionary={tab_e={ev2=[[@26,99:100='ea',<381>,3:23]], ev1=[[@1,7:8='ea',<381>,1:7], [@51,198:199='ea',<381>,6:23]]}}, dependent_queries={predicand3={query=query2, type=filters}}, filters=[], interface={ev1=[{name=ev1, table_ref=ea}]}, table_alias={ea=tab_e}, def_query2={query_dictionary={unnamed_0=[[@16,57:57=')',<288>,2:26]]}, table_dictionary={tab_x={ex3=[[@43,168:169='ex',<381>,5:23]], ex2=[[@22,90:91='ex',<381>,3:14]], ex1=[[@13,51:52='ex',<381>,2:20]]}}, dependent_queries={exists1={query=query0, type=filters}}, def_query0={query_dictionary={unnamed_1=[[@33,126:126='1',<300>,4:7]]}, table_dictionary={tab_y={ey1=[[@39,159:160='ey',<381>,5:14]], ey2=[[@47,189:190='ey',<381>,6:14]]}}, filters=[{name=ey1, table_ref=ey}, {name=ex3, table_ref=ex}, {name=ey2, table_ref=ey}, {name=ev1, table_ref=ea}], interface={unnamed_1=[]}, table_alias={ey=tab_y}}, filters=[{name=ex2, table_ref=ex}, {name=ev2, table_ref=ea}], interface={unnamed_0=[{name=ex1, table_ref=ex}]}, table_alias={ex=tab_x}}}}",
				extractor.getSymbolTable().toString());
	}
	
	@Test
	public void scalarPredicandFirstCteStandaloneTest() {
		final String query = "WITH c1a AS ("
		    + "\n  SELECT ta.t1c1 FROM tab1 AS ta"
		    + "\n  WHERE ta.t1c1 = (SELECT max(tb.t2c1) FROM tab2 AS tb)"
		    + "\n)"
		    + "\nSELECT c1a.t1c1 FROM c1a";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		assertNoFatalErrors(extractor);
		Assert.assertEquals("AST is wrong", "{SQL={with={1={cte={select={1={column={name=t1c1, table_ref=ta}}}, from={table={alias=ta, table=tab1}}, where={condition={left={column={name=t1c1, table_ref=ta}}, right={select={1={function={function_name=max, qualifier=null, parameters={column={name=t2c1, table_ref=tb}}}}}, from={table={alias=tb, table=tab2}}}, operator==}}}, alias=c1a}}, query={select={1={column={name=t1c1, table_ref=c1a}}}, from={table={alias=null, table=c1a}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[t1c1]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={t1c1=[[@5,23:24='ta',<381>,2:9], [@13,55:56='ta',<381>,3:8]]}, tab2={t2c1=[[@21,77:78='tb',<381>,3:30]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={unnamed_0=[[@24,84:84=')',<288>,3:37]]}, query2={t1c1=[[@7,26:29='t1c1',<381>,2:12], [@32,112:114='c1a',<381>,5:7]]}, query3={t1c1=[[@34,116:119='t1c1',<381>,5:11]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query3={context_list={c1a=query2}, query_dictionary={t1c1=[[@34,116:119='t1c1',<381>,5:11]]}, interface={t1c1=[{name=t1c1, table_ref=c1a}]}, table_alias={c1a=query2}, def_query2={query_dictionary={t1c1=[[@7,26:29='t1c1',<381>,2:12], [@32,112:114='c1a',<381>,5:7]]}, table_dictionary={tab1={t1c1=[[@5,23:24='ta',<381>,2:9], [@13,55:56='ta',<381>,3:8]]}}, dependent_queries={predicand1={query=query0, type=filters}}, def_query0={query_dictionary={unnamed_0=[[@24,84:84=')',<288>,3:37]]}, table_dictionary={tab2={t2c1=[[@21,77:78='tb',<381>,3:30]]}}, interface={unnamed_0=[{name=t2c1, table_ref=tb}]}, table_alias={tb=tab2}}, filters=[{name=t1c1, table_ref=ta}], interface={t1c1=[{name=t1c1, table_ref=ta}]}, table_alias={ta=tab1}}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void correlatedScalarPredicandMiddleCteReferencesFirstCteTest() {
		final String query = "WITH w1 AS (SELECT aa.a1, aa.a2 FROM tab_a AS aa),"
		    + "\nw2 AS (SELECT bb.b1 FROM tab_b AS bb"
		    + "\n       WHERE bb.b1 = (SELECT max(ww.a1) FROM w1 AS ww"
		    + "\n                      WHERE ww.a2 = bb.b2))"
			+"\nSELECT w2.b1 FROM w2";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		Snippet snippet = extractor.getSnippet();
		
		assertNoFatalErrors(extractor);
		Assert.assertEquals("AST is wrong", "{SQL={with={1={cte={select={1={column={name=a1, table_ref=aa}}, 2={column={name=a2, table_ref=aa}}}, from={table={alias=aa, table=tab_a}}}, alias=w1}, 2={cte={select={1={column={name=b1, table_ref=bb}}}, from={table={alias=bb, table=tab_b}}, where={condition={left={column={name=b1, table_ref=bb}}, right={select={1={function={function_name=max, qualifier=null, parameters={column={name=a1, table_ref=ww}}}}}, from={table={alias=ww, table=w1}}, where={condition={left={column={name=a2, table_ref=ww}}, right={column={name=b2, table_ref=bb}}, operator==}}}, operator==}}}, alias=w2}}, query={select={1={column={name=b1, table_ref=w2}}}, from={table={alias=null, table=w2}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[b1]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab_a={a1=[[@5,19:20='aa',<381>,1:19]], a2=[[@9,26:27='aa',<381>,1:26]]}, tab_b={b2=[[@51,178:179='bb',<381>,4:36]], b1=[[@22,65:66='bb',<381>,2:14], [@30,101:102='bb',<381>,3:13]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query4={b1=[[@59,196:197='b1',<381>,5:10]]}, query0={a1=[[@7,22:23='a1',<381>,1:22], [@38,121:122='ww',<381>,3:33]], a2=[[@11,29:30='a2',<381>,1:29], [@47,170:171='ww',<381>,4:28]]}, query1={unnamed_0=[[@41,126:126=')',<288>,3:38]]}, query3={b1=[[@24,68:69='b1',<381>,2:17], [@57,193:194='w2',<381>,5:7]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query4={context_list={w1=query0, w2=query3}, query_dictionary={b1=[[@59,196:197='b1',<381>,5:10]]}, def_query0={query_dictionary={a1=[[@7,22:23='a1',<381>,1:22], [@38,121:122='ww',<381>,3:33]], a2=[[@11,29:30='a2',<381>,1:29], [@47,170:171='ww',<381>,4:28]]}, table_dictionary={tab_a={a1=[[@5,19:20='aa',<381>,1:19]], a2=[[@9,26:27='aa',<381>,1:26]]}}, interface={a1=[{name=a1, table_ref=aa}], a2=[{name=a2, table_ref=aa}]}, table_alias={aa=tab_a}}, interface={b1=[{name=b1, table_ref=w2}]}, table_alias={w1=query0, w2=query3}, def_query3={context_list={w1=query0}, query_dictionary={b1=[[@24,68:69='b1',<381>,2:17], [@57,193:194='w2',<381>,5:7]]}, table_dictionary={tab_b={b2=[[@51,178:179='bb',<381>,4:36]], b1=[[@22,65:66='bb',<381>,2:14], [@30,101:102='bb',<381>,3:13]]}}, def_query1={context_list={w1=query0, ww=query0}, query_dictionary={unnamed_0=[[@41,126:126=')',<288>,3:38]]}, filters=[{name=a2, table_ref=ww}, {name=b2, table_ref=bb}], interface={unnamed_0=[{name=a1, table_ref=ww}]}, table_alias={ww=query0, w1=query0}}, dependent_queries={predicand2={query=query1, type=filters}}, filters=[{name=b1, table_ref=bb}], interface={b1=[{name=b1, table_ref=bb}]}, table_alias={bb=tab_b, w1=query0}}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void correlatedScalarPredicandMiddleCteUnqualifiedColumnDiagnosticLocationTest() {
		final String query = "WITH w1 AS (SELECT aa.a1, aa.a2 FROM tab_a AS aa),"
		    + "\nw2 AS (SELECT bb.b1 FROM (select b1 from tab_b) AS bb"
		    + "\n       WHERE bb.b1 = (SELECT max(ww.a1) FROM w1 AS ww"
		    + "\n                      WHERE ww.a2 = b2))"
		    + "\nSELECT w2.b1 FROM w2";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		Snippet snippet = extractor.getSnippet();

		assertFatalDiagnosticAtPosition(
				snippet,
				"UNQUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIASES",
				"Unqualified column 'b2' at (l:4 c:36) was not found in output interface of any visible query alias [bb, w1].",
				"b2",
				4,
				36);
		Assert.assertEquals("AST is wrong", "{SQL={with={1={cte={select={1={column={name=a1, table_ref=aa}}, 2={column={name=a2, table_ref=aa}}}, from={table={alias=aa, table=tab_a}}}, alias=w1}, 2={cte={select={1={column={name=b1, table_ref=bb}}}, from={table={alias=bb, query={select={1={column={name=b1, table_ref=null}}}, from={table={alias=null, table=tab_b}}}}}, where={condition={left={column={name=b1, table_ref=bb}}, right={select={1={function={function_name=max, qualifier=null, parameters={column={name=a1, table_ref=ww}}}}}, from={table={alias=ww, table=w1}}, where={condition={left={column={name=a2, table_ref=ww}}, right={column={name=b2, table_ref=null}}, operator==}}}, operator==}}}, alias=w2}}, query={select={1={column={name=b1, table_ref=w2}}}, from={table={alias=null, table=w2}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[b1]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab_a={a1=[[@5,19:20='aa',<381>,1:19]], a2=[[@9,26:27='aa',<381>,1:26]]}, tab_b={b1=[[@28,84:85='b1',<381>,2:33]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query4={b1=[[@24,68:69='b1',<381>,2:17], [@60,207:208='w2',<381>,5:7]]}, query5={b1=[[@62,210:211='b1',<381>,5:10]]}, query0={a1=[[@7,22:23='a1',<381>,1:22], [@43,138:139='ww',<381>,3:33]], a2=[[@11,29:30='a2',<381>,1:29], [@52,187:188='ww',<381>,4:28]]}, query1={b1=[[@28,84:85='b1',<381>,2:33], [@22,65:66='bb',<381>,2:14], [@35,118:119='bb',<381>,3:13]]}, query2={unnamed_0=[[@46,143:143=')',<288>,3:38]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query5={context_list={w1=query0, w2=query4}, query_dictionary={b1=[[@62,210:211='b1',<381>,5:10]]}, def_query0={query_dictionary={a1=[[@7,22:23='a1',<381>,1:22], [@43,138:139='ww',<381>,3:33]], a2=[[@11,29:30='a2',<381>,1:29], [@52,187:188='ww',<381>,4:28]]}, table_dictionary={tab_a={a1=[[@5,19:20='aa',<381>,1:19]], a2=[[@9,26:27='aa',<381>,1:26]]}}, interface={a1=[{name=a1, table_ref=aa}], a2=[{name=a2, table_ref=aa}]}, table_alias={aa=tab_a}}, interface={b1=[{name=b1, table_ref=w2}]}, def_query4={context_list={w1=query0}, query_dictionary={b1=[[@24,68:69='b1',<381>,2:17], [@60,207:208='w2',<381>,5:7]]}, def_query1={context_list={w1=query0}, query_dictionary={b1=[[@28,84:85='b1',<381>,2:33], [@22,65:66='bb',<381>,2:14], [@35,118:119='bb',<381>,3:13]]}, table_dictionary={tab_b={b1=[[@28,84:85='b1',<381>,2:33]]}}, interface={b1=[{name=b1, table_ref=tab_b}]}, table_alias={w1=query0}}, dependent_queries={predicand3={query=query2, type=filters}}, filters=[{name=b1, table_ref=bb}], interface={b1=[{name=b1, table_ref=bb}]}, table_alias={bb=query1, w1=query0}, def_query2={context_list={w1=query0, ww=query0}, query_dictionary={unnamed_0=[[@46,143:143=')',<288>,3:38]]}, filters=[{name=a2, table_ref=ww}, {name=b2, table_ref=null}], interface={unnamed_0=[{name=a1, table_ref=ww}]}, table_alias={ww=query0, w1=query0}}}, table_alias={w1=query0, w2=query4}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void correlatedScalarPredicandMiddleCteQualifiedMissingColumnDiagnosticLocationTest() {
		final String query = "WITH w1 AS (SELECT aa.a1, aa.a2 FROM tab_a AS aa),"
		    + "\nw2 AS (SELECT bb.b1 FROM (select b1 from tab_b) AS bb"
		    + "\n       WHERE bb.b1 = (SELECT max(ww.a1) FROM w1 AS ww"
		    + "\n                      WHERE ww.a2 = bb.missing))"
		    + "\nSELECT w2.b1 FROM w2";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		Snippet snippet = extractor.getSnippet();

		assertFatalDiagnosticAtPosition(
				snippet,
				"QUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIAS",
				"Qualified column 'missing' at (l:4 c:36) was not found in output interface of query alias 'bb'.",
				"missing",
				4,
				36);
		assertFatalDiagnosticCount(snippet, null, null, null, 1);
		Assert.assertEquals("AST is wrong", "{SQL={with={1={cte={select={1={column={name=a1, table_ref=aa}}, 2={column={name=a2, table_ref=aa}}}, from={table={alias=aa, table=tab_a}}}, alias=w1}, 2={cte={select={1={column={name=b1, table_ref=bb}}}, from={table={alias=bb, query={select={1={column={name=b1, table_ref=null}}}, from={table={alias=null, table=tab_b}}}}}, where={condition={left={column={name=b1, table_ref=bb}}, right={select={1={function={function_name=max, qualifier=null, parameters={column={name=a1, table_ref=ww}}}}}, from={table={alias=ww, table=w1}}, where={condition={left={column={name=a2, table_ref=ww}}, right={column={name=missing, table_ref=bb}}, operator==}}}, operator==}}}, alias=w2}}, query={select={1={column={name=b1, table_ref=w2}}}, from={table={alias=null, table=w2}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[b1]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab_a={a1=[[@5,19:20='aa',<381>,1:19]], a2=[[@9,26:27='aa',<381>,1:26]]}, tab_b={b1=[[@28,84:85='b1',<381>,2:33]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query4={b1=[[@24,68:69='b1',<381>,2:17], [@62,215:216='w2',<381>,5:7]]}, query5={b1=[[@64,218:219='b1',<381>,5:10]]}, query0={a1=[[@7,22:23='a1',<381>,1:22], [@43,138:139='ww',<381>,3:33]], a2=[[@11,29:30='a2',<381>,1:29], [@52,187:188='ww',<381>,4:28]]}, query1={b1=[[@28,84:85='b1',<381>,2:33], [@22,65:66='bb',<381>,2:14], [@35,118:119='bb',<381>,3:13]]}, query2={unnamed_0=[[@46,143:143=')',<288>,3:38]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query5={context_list={w1=query0, w2=query4}, query_dictionary={b1=[[@64,218:219='b1',<381>,5:10]]}, def_query0={query_dictionary={a1=[[@7,22:23='a1',<381>,1:22], [@43,138:139='ww',<381>,3:33]], a2=[[@11,29:30='a2',<381>,1:29], [@52,187:188='ww',<381>,4:28]]}, table_dictionary={tab_a={a1=[[@5,19:20='aa',<381>,1:19]], a2=[[@9,26:27='aa',<381>,1:26]]}}, interface={a1=[{name=a1, table_ref=aa}], a2=[{name=a2, table_ref=aa}]}, table_alias={aa=tab_a}}, interface={b1=[{name=b1, table_ref=w2}]}, def_query4={context_list={w1=query0}, query_dictionary={b1=[[@24,68:69='b1',<381>,2:17], [@62,215:216='w2',<381>,5:7]]}, def_query1={context_list={w1=query0}, query_dictionary={b1=[[@28,84:85='b1',<381>,2:33], [@22,65:66='bb',<381>,2:14], [@35,118:119='bb',<381>,3:13]]}, table_dictionary={tab_b={b1=[[@28,84:85='b1',<381>,2:33]]}}, interface={b1=[{name=b1, table_ref=tab_b}]}, table_alias={w1=query0}}, dependent_queries={predicand3={query=query2, type=filters}}, filters=[{name=b1, table_ref=bb}], interface={b1=[{name=b1, table_ref=bb}]}, table_alias={bb=query1, w1=query0}, def_query2={context_list={w1=query0, ww=query0}, query_dictionary={unnamed_0=[[@46,143:143=')',<288>,3:38]]}, filters=[{name=a2, table_ref=ww}, {name=missing, table_ref=bb}], interface={unnamed_0=[{name=a1, table_ref=ww}]}, table_alias={ww=query0, w1=query0}}}, table_alias={w1=query0, w2=query4}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void correlatedScalarPredicandLastCteReferencesPriorCtesTest() {
		final String query = "WITH ca AS (SELECT xa.x1, xa.x2 FROM tab_x AS xa),"
		    + "\ncb AS (SELECT yb.y1 FROM tab_y AS yb),"
		    + "\ncc AS (SELECT zc.z1 FROM tab_z AS zc"
		    + "\n       WHERE zc.z2 = (SELECT ca.x1 FROM ca"
		    + "\n                      WHERE ca.x2 = zc.z3))"
		    + "\nSELECT cc.z1 FROM cc";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		Snippet snippet = extractor.getSnippet();

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={with={1={cte={select={1={column={name=x1, table_ref=xa}}, 2={column={name=x2, table_ref=xa}}}, from={table={alias=xa, table=tab_x}}}, alias=ca}, 2={cte={select={1={column={name=y1, table_ref=yb}}}, from={table={alias=yb, table=tab_y}}}, alias=cb}, 3={cte={select={1={column={name=z1, table_ref=zc}}}, from={table={alias=zc, table=tab_z}}, where={condition={left={column={name=z2, table_ref=zc}}, right={select={1={column={name=x1, table_ref=ca}}}, from={table={alias=null, table=ca}}, where={condition={left={column={name=x2, table_ref=ca}}, right={column={name=z3, table_ref=zc}}, operator==}}}, operator==}}}, alias=cc}}, query={select={1={column={name=z1, table_ref=cc}}}, from={table={alias=null, table=cc}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[z1]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab_z={z1=[[@35,104:105='zc',<381>,3:14]], z2=[[@43,140:141='zc',<381>,4:13]], z3=[[@59,206:207='zc',<381>,5:36]]}, tab_x={x1=[[@5,19:20='xa',<381>,1:19]], x2=[[@9,26:27='xa',<381>,1:26]]}, tab_y={y1=[[@22,65:66='yb',<381>,2:14]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query4={z1=[[@37,107:108='z1',<381>,3:17], [@65,221:222='cc',<381>,6:7]]}, query5={z1=[[@67,224:225='z1',<381>,6:10]]}, query0={x1=[[@7,22:23='x1',<381>,1:22], [@49,156:157='ca',<381>,4:29]], x2=[[@11,29:30='x2',<381>,1:29], [@55,198:199='ca',<381>,5:28]]}, query1={y1=[[@24,68:69='y1',<381>,2:17]]}, query2={x1=[[@51,159:160='x1',<381>,4:32]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query5={context_list={ca=query0, cb=query1, cc=query4}, query_dictionary={z1=[[@67,224:225='z1',<381>,6:10]]}, def_query1={context_list={ca=query0}, query_dictionary={y1=[[@24,68:69='y1',<381>,2:17]]}, table_dictionary={tab_y={y1=[[@22,65:66='yb',<381>,2:14]]}}, interface={y1=[{name=y1, table_ref=yb}]}, table_alias={yb=tab_y, ca=query0}}, def_query0={query_dictionary={x1=[[@7,22:23='x1',<381>,1:22], [@49,156:157='ca',<381>,4:29]], x2=[[@11,29:30='x2',<381>,1:29], [@55,198:199='ca',<381>,5:28]]}, table_dictionary={tab_x={x1=[[@5,19:20='xa',<381>,1:19]], x2=[[@9,26:27='xa',<381>,1:26]]}}, interface={x1=[{name=x1, table_ref=xa}], x2=[{name=x2, table_ref=xa}]}, table_alias={xa=tab_x}}, interface={z1=[{name=z1, table_ref=cc}]}, def_query4={context_list={ca=query0, cb=query1}, query_dictionary={z1=[[@37,107:108='z1',<381>,3:17], [@65,221:222='cc',<381>,6:7]]}, table_dictionary={tab_z={z1=[[@35,104:105='zc',<381>,3:14]], z2=[[@43,140:141='zc',<381>,4:13]], z3=[[@59,206:207='zc',<381>,5:36]]}}, dependent_queries={predicand3={query=query2, type=filters}}, filters=[{name=z2, table_ref=zc}], interface={z1=[{name=z1, table_ref=zc}]}, table_alias={zc=tab_z, ca=query0, cb=query1}, def_query2={context_list={ca=query0, cb=query1}, query_dictionary={x1=[[@51,159:160='x1',<381>,4:32]]}, filters=[{name=x2, table_ref=ca}, {name=z3, table_ref=zc}], interface={x1=[{name=x1, table_ref=ca}]}, table_alias={ca=query0, cb=query1}}}, table_alias={cc=query4, ca=query0, cb=query1}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void correlatedScalarPredicandFinalQueryReferencesCteChainTest() {
		final String query = "WITH fa AS (SELECT pa.p1, pa.p2 FROM tab_p AS pa),"
		    + "\nfb AS (SELECT qb.q1 FROM tab_q AS qb)"
		    + "\nSELECT pa.p1,"
		    + "\n       (SELECT max(ff.p2) FROM fa AS ff"
		    + "\n        WHERE ff.p1 = pa.p1) AS p2_max"
		    + "\nFROM fa AS pa JOIN fb ON pa.p1 = fb.q1";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		Snippet snippet = extractor.getSnippet();

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong", "{SQL={with={1={cte={select={1={column={name=p1, table_ref=pa}}, 2={column={name=p2, table_ref=pa}}}, from={table={alias=pa, table=tab_p}}}, alias=fa}, 2={cte={select={1={column={name=q1, table_ref=qb}}}, from={table={alias=qb, table=tab_q}}}, alias=fb}}, query={select={1={column={name=p1, table_ref=pa}}, 2={lookup={from={table={alias=ff, table=fa}}, where={condition={left={column={name=p1, table_ref=ff}}, right={column={name=p1, table_ref=pa}}, operator==}}, select={1={function={function_name=max, qualifier=null, parameters={column={name=p2, table_ref=ff}}}}}}, alias=p2_max}}, from={join={1={table={alias=pa, table=fa}}, 2={join=JOIN, on={condition={left={column={name=p1, table_ref=pa}}, right={column={name=q1, table_ref=fb}}, operator==}}}, 3={table={alias=null, table=fb}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[p1, p2_max]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab_p={p1=[[@5,19:20='pa',<381>,1:19]], p2=[[@9,26:27='pa',<381>,1:26]]}, tab_q={q1=[[@22,65:66='qb',<381>,2:14]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query4={p1=[[@33,99:100='p1',<381>,3:10]], p2_max=[[@57,175:180='p2_max',<381>,5:32]]}, query0={p1=[[@7,22:23='p1',<381>,1:22], [@48,157:158='ff',<381>,5:14], [@31,96:97='pa',<381>,3:7], [@52,165:166='pa',<381>,5:22], [@65,207:208='pa',<381>,6:25]], p2=[[@11,29:30='p2',<381>,1:29], [@39,122:123='ff',<381>,4:19]]}, query1={q1=[[@24,68:69='q1',<381>,2:17], [@69,215:216='fb',<381>,6:33]]}, query2={unnamed_0=[[@42,127:127=')',<288>,4:24]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query4={context_list={fa=query0, fb=query1, pa=query0}, query_dictionary={p1=[[@33,99:100='p1',<381>,3:10]], p2_max=[[@57,175:180='p2_max',<381>,5:32]]}, dependent_queries={predicand3={query=query2, type=interface}}, def_query1={context_list={fa=query0}, query_dictionary={q1=[[@24,68:69='q1',<381>,2:17], [@69,215:216='fb',<381>,6:33]]}, table_dictionary={tab_q={q1=[[@22,65:66='qb',<381>,2:14]]}}, interface={q1=[{name=q1, table_ref=qb}]}, table_alias={qb=tab_q, fa=query0}}, def_query0={query_dictionary={p1=[[@7,22:23='p1',<381>,1:22], [@48,157:158='ff',<381>,5:14], [@31,96:97='pa',<381>,3:7], [@52,165:166='pa',<381>,5:22], [@65,207:208='pa',<381>,6:25]], p2=[[@11,29:30='p2',<381>,1:29], [@39,122:123='ff',<381>,4:19]]}, table_dictionary={tab_p={p1=[[@5,19:20='pa',<381>,1:19]], p2=[[@9,26:27='pa',<381>,1:26]]}}, interface={p1=[{name=p1, table_ref=pa}], p2=[{name=p2, table_ref=pa}]}, table_alias={pa=tab_p}}, filters=[{name=p1, table_ref=pa}, {name=q1, table_ref=fb}], interface={p1=[{name=p1, table_ref=pa}], p2_max=[{name=p1, table_ref=ff}, {name=p1, table_ref=pa}, {name=p2, table_ref=ff}]}, table_alias={pa=query0, fa=query0, fb=query1}, def_query2={context_list={fa=query0, fb=query1, ff=query0}, query_dictionary={unnamed_0=[[@42,127:127=')',<288>,4:24]]}, filters=[{name=p1, table_ref=ff}, {name=p1, table_ref=pa}], interface={unnamed_0=[{name=p2, table_ref=ff}]}, table_alias={ff=query0, fa=query0, fb=query1}}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void correlatedScalarPredicandNestedCteWithOuterRefTest() {
		final String query = "WITH oa AS (SELECT ra.r1 FROM tab_r AS ra),"
		    + "\nob AS (WITH ib AS (SELECT sb.s1 FROM tab_s AS sb)"
		    + "\n       SELECT tb.t1 FROM tab_t AS tb"
		    + "\n       WHERE tb.t2 = (SELECT max(ib.s1) FROM ib"
		    + "\n                      WHERE ib.s1 = oa.r1))"
		    + "\nSELECT ob.t1 FROM ob JOIN oa ON oa.r1 = ob.t1";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		assertNoFatalErrors(extractor);
		Assert.assertEquals("AST is wrong", "{SQL={with={1={cte={select={1={column={name=r1, table_ref=ra}}}, from={table={alias=ra, table=tab_r}}}, alias=oa}, 2={cte={with={1={cte={select={1={column={name=s1, table_ref=sb}}}, from={table={alias=sb, table=tab_s}}}, alias=ib}}, query={select={1={column={name=t1, table_ref=tb}}}, from={table={alias=tb, table=tab_t}}, where={condition={left={column={name=t2, table_ref=tb}}, right={select={1={function={function_name=max, qualifier=null, parameters={column={name=s1, table_ref=ib}}}}}, from={table={alias=null, table=ib}}, where={condition={left={column={name=s1, table_ref=ib}}, right={column={name=r1, table_ref=oa}}, operator==}}}, operator==}}}}, alias=ob}}, query={select={1={column={name=t1, table_ref=ob}}}, from={join={1={table={alias=null, table=ob}}, 2={join=JOIN, on={condition={left={column={name=r1, table_ref=oa}}, right={column={name=t1, table_ref=ob}}, operator==}}}, 3={table={alias=null, table=oa}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[t1]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab_r={r1=[[@5,19:20='ra',<381>,1:19]]}, tab_s={s1=[[@22,70:71='sb',<381>,2:26]]}, tab_t={t1=[[@31,108:109='tb',<381>,3:14]], t2=[[@39,144:145='tb',<381>,4:13]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query4={t1=[[@33,111:112='t1',<381>,3:17], [@64,230:231='ob',<381>,6:7], [@76,263:264='ob',<381>,6:40]]}, query5={t1=[[@66,233:234='t1',<381>,6:10]]}, query0={r1=[[@7,22:23='r1',<381>,1:22], [@58,215:216='oa',<381>,5:36], [@72,255:256='oa',<381>,6:32]]}, query1={s1=[[@24,73:74='s1',<381>,2:29], [@47,164:165='ib',<381>,4:33], [@54,207:208='ib',<381>,5:28]]}, query2={unnamed_0=[[@50,169:169=')',<288>,4:38]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query5={context_list={ob=query4}, query_dictionary={t1=[[@66,233:234='t1',<381>,6:10]]}, filters=[{name=r1, table_ref=oa}, {name=t1, table_ref=ob}], interface={t1=[{name=t1, table_ref=ob}]}, def_query4={context_list={oa=query0, ib=query1}, query_dictionary={t1=[[@33,111:112='t1',<381>,3:17], [@64,230:231='ob',<381>,6:7], [@76,263:264='ob',<381>,6:40]]}, table_dictionary={tab_t={t1=[[@31,108:109='tb',<381>,3:14]], t2=[[@39,144:145='tb',<381>,4:13]]}}, dependent_queries={predicand3={query=query2, type=filters}}, def_query1={context_list={oa=query0}, query_dictionary={s1=[[@24,73:74='s1',<381>,2:29], [@47,164:165='ib',<381>,4:33], [@54,207:208='ib',<381>,5:28]]}, table_dictionary={tab_s={s1=[[@22,70:71='sb',<381>,2:26]]}}, interface={s1=[{name=s1, table_ref=sb}]}, table_alias={oa=query0, sb=tab_s}}, def_query0={query_dictionary={r1=[[@7,22:23='r1',<381>,1:22], [@58,215:216='oa',<381>,5:36], [@72,255:256='oa',<381>,6:32]]}, table_dictionary={tab_r={r1=[[@5,19:20='ra',<381>,1:19]]}}, interface={r1=[{name=r1, table_ref=ra}]}, table_alias={ra=tab_r}}, filters=[{name=t2, table_ref=tb}], interface={t1=[{name=t1, table_ref=tb}]}, table_alias={oa=query0, ib=query1, tb=tab_t}, def_query2={context_list={oa=query0, ib=query1}, query_dictionary={unnamed_0=[[@50,169:169=')',<288>,4:38]]}, filters=[{name=s1, table_ref=ib}, {name=r1, table_ref=oa}], interface={unnamed_0=[{name=s1, table_ref=ib}]}, table_alias={oa=query0, ib=query1}}}, table_alias={oa=query0, ob=query4}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void correlatedInSubqueryNestedJoinSubqueryTest() {
		final String query = "SELECT oa.in1 FROM tab_a AS oa"
		    + "\nWHERE oa.in2 IN (SELECT ib.in9 FROM tab_b AS ib"
		    + "\nJOIN (SELECT ic.in7 FROM tab_c AS ic"
		    + "\n        WHERE ic.in8 = oa.in1) AS ix"
		    + "\n        ON ix.in7 = ib.in6)";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		assertNoFatalErrors(extractor);
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=in1, table_ref=oa}}}, from={table={alias=oa, table=tab_a}}, where={in={item={column={name=in2, table_ref=oa}}, in_list={select={1={column={name=in9, table_ref=ib}}}, from={join={1={table={alias=ib, table=tab_b}}, 2={join=JOIN, on={condition={left={column={name=in7, table_ref=ix}}, right={column={name=in6, table_ref=ib}}, operator==}}}, 3={table={alias=ix, query={select={1={column={name=in7, table_ref=ic}}}, from={table={alias=ic, table=tab_c}}, where={condition={left={column={name=in8, table_ref=ic}}, right={column={name=in1, table_ref=oa}}, operator==}}}}}}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[in1]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab_a={in2=[[@9,37:38='oa',<381>,2:6]], in1=[[@37,139:140='oa',<381>,4:23], [@1,7:8='oa',<381>,1:7]]}, tab_b={in6=[[@48,173:174='ib',<381>,5:20]], in9=[[@15,55:56='ib',<381>,2:24]]}, tab_c={in8=[[@33,130:131='ic',<381>,4:14]], in7=[[@25,92:93='ic',<381>,3:13]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={in7=[[@27,95:97='in7',<381>,3:16], [@44,164:165='ix',<381>,5:11]]}, query1={in9=[[@17,58:60='in9',<381>,2:27]]}, query3={in1=[[@3,10:12='in1',<381>,1:10]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query3={query_dictionary={in1=[[@3,10:12='in1',<381>,1:10]]}, table_dictionary={tab_a={in2=[[@9,37:38='oa',<381>,2:6]], in1=[[@1,7:8='oa',<381>,1:7], [@37,139:140='oa',<381>,4:23]]}}, def_query1={query_dictionary={in9=[[@17,58:60='in9',<381>,2:27]]}, table_dictionary={tab_b={in6=[[@48,173:174='ib',<381>,5:20]], in9=[[@15,55:56='ib',<381>,2:24]]}}, def_query0={query_dictionary={in7=[[@27,95:97='in7',<381>,3:16], [@44,164:165='ix',<381>,5:11]]}, table_dictionary={tab_a={in1=[[@37,139:140='oa',<381>,4:23], [@1,7:8='oa',<381>,1:7]]}, tab_c={in8=[[@33,130:131='ic',<381>,4:14]], in7=[[@25,92:93='ic',<381>,3:13]]}}, filters=[{name=in8, table_ref=ic}, {name=in1, table_ref=oa}], interface={in7=[{name=in7, table_ref=ic}]}, table_alias={ic=tab_c}}, filters=[{name=in7, table_ref=ix}, {name=in6, table_ref=ib}], interface={in9=[{name=in9, table_ref=ib}]}, table_alias={ib=tab_b, ix=query0}}, dependent_queries={in_list2={query=query1, type=filters}}, filters=[], interface={in1=[{name=in1, table_ref=oa}]}, table_alias={oa=tab_a}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void correlatedInSubqueryUnionContextTest() {
		final String query = "SELECT ua.iu1 FROM tab_a AS ua"
		    + "\nWHERE ua.iu2 IN (SELECT sub.iu4 FROM ("
		    + "\nSELECT ub.iu4 FROM tab_b AS ub"
		    + "\n        WHERE ub.iu5 = ua.iu1"
		    + "\nUNION SELECT uc.iu4 FROM tab_c AS uc"
		    + "\n        WHERE uc.iu6 = ua.iu3) AS sub)";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		assertNoFatalErrors(extractor);
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=iu1, table_ref=ua}}}, from={table={alias=ua, table=tab_a}}, where={in={item={column={name=iu2, table_ref=ua}}, in_list={select={1={column={name=iu4, table_ref=sub}}}, from={table={alias=sub, query={union={1={select={1={column={name=iu4, table_ref=ub}}}, from={table={alias=ub, table=tab_b}}, where={condition={left={column={name=iu5, table_ref=ub}}, right={column={name=iu1, table_ref=ua}}, operator==}}}, 2={union={qualifier=null, operator=UNION}}, 3={select={1={column={name=iu4, table_ref=uc}}}, from={table={alias=uc, table=tab_c}}, where={condition={left={column={name=iu6, table_ref=uc}}, right={column={name=iu3, table_ref=ua}}, operator==}}}}}}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[iu1]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab_a={iu1=[[@33,124:125='ua',<381>,4:23], [@1,7:8='ua',<381>,1:7]], iu3=[[@50,191:192='ua',<381>,6:23]], iu2=[[@9,37:38='ua',<381>,2:6]]}, tab_b={iu5=[[@29,115:116='ub',<381>,4:14]], iu4=[[@21,77:78='ub',<381>,3:7]]}, tab_c={iu4=[[@38,144:145='uc',<381>,5:13]], iu6=[[@46,182:183='uc',<381>,6:14]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{union2={iu4=[[@15,55:57='sub',<381>,2:24]]}, query5={iu1=[[@3,10:12='iu1',<381>,1:10]]}, query0={iu4=[[@23,80:82='iu4',<381>,3:10]]}, query1={iu4=[[@40,147:149='iu4',<381>,5:16]]}, query3={iu4=[[@17,59:61='iu4',<381>,2:28]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query5={query_dictionary={iu1=[[@3,10:12='iu1',<381>,1:10]]}, table_dictionary={tab_a={iu1=[[@1,7:8='ua',<381>,1:7], [@33,124:125='ua',<381>,4:23]], iu2=[[@9,37:38='ua',<381>,2:6]]}}, dependent_queries={in_list4={query=query3, type=filters}}, filters=[], interface={iu1=[{name=iu1, table_ref=ua}]}, def_query3={def_union2={query_dictionary={iu4=[[@15,55:57='sub',<381>,2:24]]}, def_query1={query_dictionary={iu4=[[@40,147:149='iu4',<381>,5:16]]}, table_dictionary={tab_a={iu3=[[@50,191:192='ua',<381>,6:23]]}, tab_c={iu4=[[@38,144:145='uc',<381>,5:13]], iu6=[[@46,182:183='uc',<381>,6:14]]}}, filters=[{name=iu6, table_ref=uc}, {name=iu3, table_ref=ua}], interface={iu4=[{name=iu4, table_ref=uc}]}, table_alias={uc=tab_c}}, def_query0={query_dictionary={iu4=[[@23,80:82='iu4',<381>,3:10]]}, table_dictionary={tab_a={iu1=[[@33,124:125='ua',<381>,4:23], [@1,7:8='ua',<381>,1:7]]}, tab_b={iu5=[[@29,115:116='ub',<381>,4:14]], iu4=[[@21,77:78='ub',<381>,3:7]]}}, filters=[{name=iu5, table_ref=ub}, {name=iu1, table_ref=ua}], interface={iu4=[{name=iu4, table_ref=ub}]}, table_alias={ub=tab_b}}, interface={iu4=[{name=iu4, table_ref=ub}, {name=iu4, table_ref=uc}]}}, query_dictionary={iu4=[[@17,59:61='iu4',<381>,2:28]]}, interface={iu4=[{name=iu4, table_ref=sub}]}, table_alias={sub=union2}}, table_alias={ua=tab_a}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void correlatedInSubqueryIntersectContextTest() {
		final String query = "SELECT oi.ix1 FROM tab_o AS oi"
		    + "\nWHERE oi.ix2 IN (SELECT ii.ix9 FROM ("
		    + "\nSELECT id.ix9 FROM tab_d AS id"
		    + "\n        WHERE id.ix3 = oi.ix1"
		    + "\nINTERSECT SELECT ie.ix9 FROM tab_e AS ie"
		    + "\n        WHERE ie.ix4 = oi.ix2) AS ii)";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		assertNoFatalErrors(extractor);
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=ix1, table_ref=oi}}}, from={table={alias=oi, table=tab_o}}, where={in={item={column={name=ix2, table_ref=oi}}, in_list={select={1={column={name=ix9, table_ref=ii}}}, from={table={alias=ii, query={intersect={1={select={1={column={name=ix9, table_ref=id}}}, from={table={alias=id, table=tab_d}}, where={condition={left={column={name=ix3, table_ref=id}}, right={column={name=ix1, table_ref=oi}}, operator==}}}, 2={intersect={qualifier=null, operator=INTERSECT}}, 3={select={1={column={name=ix9, table_ref=ie}}}, from={table={alias=ie, table=tab_e}}, where={condition={left={column={name=ix4, table_ref=ie}}, right={column={name=ix2, table_ref=oi}}, operator==}}}}}}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[ix1]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab_o={ix2=[[@50,194:195='oi',<381>,6:23], [@9,37:38='oi',<381>,2:6]], ix1=[[@33,123:124='oi',<381>,4:23], [@1,7:8='oi',<381>,1:7]]}, tab_d={ix3=[[@29,114:115='id',<381>,4:14]], ix9=[[@21,76:77='id',<381>,3:7]]}, tab_e={ix4=[[@46,185:186='ie',<381>,6:14]], ix9=[[@38,147:148='ie',<381>,5:17]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{intersect2={ix9=[[@15,55:56='ii',<381>,2:24]]}, query5={ix1=[[@3,10:12='ix1',<381>,1:10]]}, query0={ix9=[[@23,79:81='ix9',<381>,3:10]]}, query1={ix9=[[@40,150:152='ix9',<381>,5:20]]}, query3={ix9=[[@17,58:60='ix9',<381>,2:27]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query5={query_dictionary={ix1=[[@3,10:12='ix1',<381>,1:10]]}, table_dictionary={tab_o={ix2=[[@9,37:38='oi',<381>,2:6], [@50,194:195='oi',<381>,6:23]], ix1=[[@1,7:8='oi',<381>,1:7], [@33,123:124='oi',<381>,4:23]]}}, dependent_queries={in_list4={query=query3, type=filters}}, filters=[], interface={ix1=[{name=ix1, table_ref=oi}]}, def_query3={query_dictionary={ix9=[[@17,58:60='ix9',<381>,2:27]]}, def_intersect2={query_dictionary={ix9=[[@15,55:56='ii',<381>,2:24]]}, def_query1={query_dictionary={ix9=[[@40,150:152='ix9',<381>,5:20]]}, table_dictionary={tab_o={ix2=[[@50,194:195='oi',<381>,6:23], [@9,37:38='oi',<381>,2:6]]}, tab_e={ix4=[[@46,185:186='ie',<381>,6:14]], ix9=[[@38,147:148='ie',<381>,5:17]]}}, filters=[{name=ix4, table_ref=ie}, {name=ix2, table_ref=oi}], interface={ix9=[{name=ix9, table_ref=ie}]}, table_alias={ie=tab_e}}, def_query0={query_dictionary={ix9=[[@23,79:81='ix9',<381>,3:10]]}, table_dictionary={tab_o={ix1=[[@33,123:124='oi',<381>,4:23], [@1,7:8='oi',<381>,1:7]]}, tab_d={ix3=[[@29,114:115='id',<381>,4:14]], ix9=[[@21,76:77='id',<381>,3:7]]}}, filters=[{name=ix3, table_ref=id}, {name=ix1, table_ref=oi}], interface={ix9=[{name=ix9, table_ref=id}]}, table_alias={id=tab_d}}, interface={ix9=[{name=ix9, table_ref=id}, {name=ix9, table_ref=ie}]}}, interface={ix9=[{name=ix9, table_ref=ii}]}, table_alias={ii=intersect2}}, table_alias={oi=tab_o}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void correlatedInSubqueryWithNestedScalarPredicandTest() {
		final String query = "SELECT sa.in1 FROM tab_s AS sa"
		    + "\nWHERE sa.in2 IN (SELECT ia.in9 FROM tab_i AS ia"
		    + "\n                 WHERE ia.in3 = ("
		    + "\nSELECT max(jb.jx1) FROM tab_j AS jb"
		    + "\n                 WHERE jb.jx2 = sa.in1))";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		assertNoFatalErrors(extractor);
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=in1, table_ref=sa}}}, from={table={alias=sa, table=tab_s}}, where={in={item={column={name=in2, table_ref=sa}}, in_list={select={1={column={name=in9, table_ref=ia}}}, from={table={alias=ia, table=tab_i}}, where={condition={left={column={name=in3, table_ref=ia}}, right={select={1={function={function_name=max, qualifier=null, parameters={column={name=jx1, table_ref=jb}}}}}, from={table={alias=jb, table=tab_j}}, where={condition={left={column={name=jx2, table_ref=jb}}, right={column={name=in1, table_ref=sa}}, operator==}}}, operator==}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[in1]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab_j={jx1=[[@31,124:125='jb',<381>,4:11]], jx2=[[@40,172:173='jb',<381>,5:23]]}, tab_s={in2=[[@9,37:38='sa',<381>,2:6]], in1=[[@1,7:8='sa',<381>,1:7], [@44,181:182='sa',<381>,5:32]]}, tab_i={in9=[[@15,55:56='ia',<381>,2:24]], in3=[[@23,102:103='ia',<381>,3:23]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query4={in1=[[@3,10:12='in1',<381>,1:10]]}, query0={unnamed_0=[[@34,130:130=')',<288>,4:17]]}, query2={in9=[[@17,58:60='in9',<381>,2:27]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query4={query_dictionary={in1=[[@3,10:12='in1',<381>,1:10]]}, table_dictionary={tab_s={in2=[[@9,37:38='sa',<381>,2:6]], in1=[[@1,7:8='sa',<381>,1:7], [@44,181:182='sa',<381>,5:32]]}}, dependent_queries={in_list3={query=query2, type=filters}}, filters=[], interface={in1=[{name=in1, table_ref=sa}]}, table_alias={sa=tab_s}, def_query2={query_dictionary={in9=[[@17,58:60='in9',<381>,2:27]]}, table_dictionary={tab_i={in9=[[@15,55:56='ia',<381>,2:24]], in3=[[@23,102:103='ia',<381>,3:23]]}}, dependent_queries={predicand1={query=query0, type=filters}}, def_query0={query_dictionary={unnamed_0=[[@34,130:130=')',<288>,4:17]]}, table_dictionary={tab_j={jx1=[[@31,124:125='jb',<381>,4:11]], jx2=[[@40,172:173='jb',<381>,5:23]]}}, filters=[{name=jx2, table_ref=jb}, {name=in1, table_ref=sa}], interface={unnamed_0=[{name=jx1, table_ref=jb}]}, table_alias={jb=tab_j}}, filters=[{name=in3, table_ref=ia}], interface={in9=[{name=in9, table_ref=ia}]}, table_alias={ia=tab_i}}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void correlatedInSubqueryWithNestedExistsTest() {
		final String query = "SELECT ea.in1 FROM tab_e AS ea"
		    + "\nWHERE ea.in2 IN (SELECT ex.in9 FROM tab_x AS ex"
		    + "\n                 WHERE EXISTS ("
		    + "\nSELECT 1 FROM tab_y AS ey"
		    + "\n                 WHERE ey.ey1 = ex.in8"
		    + "\n                   AND ey.ey2 = ea.in1))";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		assertNoFatalErrors(extractor);
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=in1, table_ref=ea}}}, from={table={alias=ea, table=tab_e}}, where={in={item={column={name=in2, table_ref=ea}}, in_list={select={1={column={name=in9, table_ref=ex}}}, from={table={alias=ex, table=tab_x}}, where={exists={select={1={literal=1}}, from={table={alias=ey, table=tab_y}}, where={and={1={condition={left={column={name=ey1, table_ref=ey}}, right={column={name=in8, table_ref=ex}}, operator==}}, 2={condition={left={column={name=ey2, table_ref=ey}}, right={column={name=in1, table_ref=ea}}, operator==}}}}, operator=EXISTS}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[in1]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab_e={in2=[[@9,37:38='ea',<381>,2:6]], in1=[[@1,7:8='ea',<381>,1:7], [@44,208:209='ea',<381>,6:32]]}, tab_x={in8=[[@36,169:170='ex',<381>,5:32]], in9=[[@15,55:56='ex',<381>,2:24]]}, tab_y={ey1=[[@32,160:161='ey',<381>,5:23]], ey2=[[@40,199:200='ey',<381>,6:23]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query4={in1=[[@3,10:12='in1',<381>,1:10]]}, query0={unnamed_0=[[@26,118:118='1',<300>,4:7]]}, query2={in9=[[@17,58:60='in9',<381>,2:27]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query4={query_dictionary={in1=[[@3,10:12='in1',<381>,1:10]]}, table_dictionary={tab_e={in2=[[@9,37:38='ea',<381>,2:6]], in1=[[@1,7:8='ea',<381>,1:7], [@44,208:209='ea',<381>,6:32]]}}, dependent_queries={in_list3={query=query2, type=filters}}, filters=[], interface={in1=[{name=in1, table_ref=ea}]}, table_alias={ea=tab_e}, def_query2={query_dictionary={in9=[[@17,58:60='in9',<381>,2:27]]}, table_dictionary={tab_x={in8=[[@36,169:170='ex',<381>,5:32]], in9=[[@15,55:56='ex',<381>,2:24]]}}, dependent_queries={exists1={query=query0, type=filters}}, def_query0={query_dictionary={unnamed_0=[[@26,118:118='1',<300>,4:7]]}, table_dictionary={tab_y={ey1=[[@32,160:161='ey',<381>,5:23]], ey2=[[@40,199:200='ey',<381>,6:23]]}}, filters=[{name=ey1, table_ref=ey}, {name=in8, table_ref=ex}, {name=ey2, table_ref=ey}, {name=in1, table_ref=ea}], interface={unnamed_0=[]}, table_alias={ey=tab_y}}, filters=[], interface={in9=[{name=in9, table_ref=ex}]}, table_alias={ex=tab_x}}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void correlatedInSubqueryFirstCteStandaloneTest() {
		final String query = "WITH c1a AS ("
		    + "\n  SELECT ta.t1c1 FROM tab1 AS ta"
		    + "\n  WHERE ta.t1c1 IN (SELECT tb.t2c1 FROM tab2 AS tb)"
		    + "\n)"
		    + "\nSELECT c1a.t1c1 FROM c1a";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		assertNoFatalErrors(extractor);
		Assert.assertEquals("AST is wrong", "{SQL={with={1={cte={select={1={column={name=t1c1, table_ref=ta}}}, from={table={alias=ta, table=tab1}}, where={in={item={column={name=t1c1, table_ref=ta}}, in_list={select={1={column={name=t2c1, table_ref=tb}}}, from={table={alias=tb, table=tab2}}}}}}, alias=c1a}}, query={select={1={column={name=t1c1, table_ref=c1a}}}, from={table={alias=null, table=c1a}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[t1c1]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={t1c1=[[@5,23:24='ta',<381>,2:9], [@13,55:56='ta',<381>,3:8]]}, tab2={t2c1=[[@19,74:75='tb',<381>,3:27]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={t2c1=[[@21,77:80='t2c1',<381>,3:30]]}, query2={t1c1=[[@7,26:29='t1c1',<381>,2:12], [@29,108:110='c1a',<381>,5:7]]}, query3={t1c1=[[@31,112:115='t1c1',<381>,5:11]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query3={context_list={c1a=query2}, query_dictionary={t1c1=[[@31,112:115='t1c1',<381>,5:11]]}, interface={t1c1=[{name=t1c1, table_ref=c1a}]}, table_alias={c1a=query2}, def_query2={query_dictionary={t1c1=[[@7,26:29='t1c1',<381>,2:12], [@29,108:110='c1a',<381>,5:7]]}, table_dictionary={tab1={t1c1=[[@5,23:24='ta',<381>,2:9], [@13,55:56='ta',<381>,3:8]]}}, dependent_queries={in_list1={query=query0, type=filters}}, def_query0={query_dictionary={t2c1=[[@21,77:80='t2c1',<381>,3:30]]}, table_dictionary={tab2={t2c1=[[@19,74:75='tb',<381>,3:27]]}}, interface={t2c1=[{name=t2c1, table_ref=tb}]}, table_alias={tb=tab2}}, filters=[], interface={t1c1=[{name=t1c1, table_ref=ta}]}, table_alias={ta=tab1}}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void correlatedInSubqueryMiddleCteReferencesFirstCteTest() {
		final String query = "WITH w1 AS (SELECT aa.a1, aa.a2 FROM tab_a AS aa),"
		    + "\nw2 AS (SELECT bb.b1 FROM tab_b AS bb"
		    + "\n       WHERE bb.b1 IN (SELECT ww.a1 FROM w1 AS ww"
		    + "\n                       WHERE ww.a2 = bb.b2))"
		    + "\nSELECT w2.b1 FROM w2";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		assertNoFatalErrors(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={with={1={cte={select={1={column={name=a1, table_ref=aa}}, 2={column={name=a2, table_ref=aa}}}, from={table={alias=aa, table=tab_a}}}, alias=w1}, 2={cte={select={1={column={name=b1, table_ref=bb}}}, from={table={alias=bb, table=tab_b}}, where={in={item={column={name=b1, table_ref=bb}}, in_list={select={1={column={name=a1, table_ref=ww}}}, from={table={alias=ww, table=w1}}, where={condition={left={column={name=a2, table_ref=ww}}, right={column={name=b2, table_ref=bb}}, operator==}}}}}}, alias=w2}}, query={select={1={column={name=b1, table_ref=w2}}}, from={table={alias=null, table=w2}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[b1]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab_a={a1=[[@5,19:20='aa',<381>,1:19]], a2=[[@9,26:27='aa',<381>,1:26]]}, tab_b={b2=[[@48,175:176='bb',<381>,4:37]], b1=[[@22,65:66='bb',<381>,2:14], [@30,101:102='bb',<381>,3:13]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query4={b1=[[@56,193:194='b1',<381>,5:10]]}, query0={a1=[[@7,22:23='a1',<381>,1:22], [@36,118:119='ww',<381>,3:30]], a2=[[@11,29:30='a2',<381>,1:29], [@44,167:168='ww',<381>,4:29]]}, query1={a1=[[@38,121:122='a1',<381>,3:33]]}, query3={b1=[[@24,68:69='b1',<381>,2:17], [@54,190:191='w2',<381>,5:7]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query4={context_list={w1=query0, w2=query3}, query_dictionary={b1=[[@56,193:194='b1',<381>,5:10]]}, def_query0={query_dictionary={a1=[[@7,22:23='a1',<381>,1:22], [@36,118:119='ww',<381>,3:30]], a2=[[@11,29:30='a2',<381>,1:29], [@44,167:168='ww',<381>,4:29]]}, table_dictionary={tab_a={a1=[[@5,19:20='aa',<381>,1:19]], a2=[[@9,26:27='aa',<381>,1:26]]}}, interface={a1=[{name=a1, table_ref=aa}], a2=[{name=a2, table_ref=aa}]}, table_alias={aa=tab_a}}, interface={b1=[{name=b1, table_ref=w2}]}, table_alias={w1=query0, w2=query3}, def_query3={context_list={w1=query0}, query_dictionary={b1=[[@24,68:69='b1',<381>,2:17], [@54,190:191='w2',<381>,5:7]]}, table_dictionary={tab_b={b2=[[@48,175:176='bb',<381>,4:37]], b1=[[@22,65:66='bb',<381>,2:14], [@30,101:102='bb',<381>,3:13]]}}, def_query1={context_list={w1=query0, ww=query0}, query_dictionary={a1=[[@38,121:122='a1',<381>,3:33]]}, filters=[{name=a2, table_ref=ww}, {name=b2, table_ref=bb}], interface={a1=[{name=a1, table_ref=ww}]}, table_alias={ww=query0, w1=query0}}, dependent_queries={in_list2={query=query1, type=filters}}, filters=[], interface={b1=[{name=b1, table_ref=bb}]}, table_alias={bb=tab_b, w1=query0}}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void correlatedInSubqueryLastCteReferencesPriorCtesTest() {
		final String query = "WITH ca AS (SELECT xa.x1, xa.x2 FROM tab_x AS xa),"
		    + "\ncb AS (SELECT yb.y1 FROM tab_y AS yb),"
		    + "\ncc AS (SELECT zc.z1 FROM tab_z AS zc"
		    + "\n       WHERE zc.z2 IN (SELECT ca.x1 FROM ca"
		    + "\n                       WHERE ca.x2 = zc.z3))"
		    + "\nSELECT cc.z1 FROM cc";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		assertNoFatalErrors(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={with={1={cte={select={1={column={name=x1, table_ref=xa}}, 2={column={name=x2, table_ref=xa}}}, from={table={alias=xa, table=tab_x}}}, alias=ca}, 2={cte={select={1={column={name=y1, table_ref=yb}}}, from={table={alias=yb, table=tab_y}}}, alias=cb}, 3={cte={select={1={column={name=z1, table_ref=zc}}}, from={table={alias=zc, table=tab_z}}, where={in={item={column={name=z2, table_ref=zc}}, in_list={select={1={column={name=x1, table_ref=ca}}}, from={table={alias=null, table=ca}}, where={condition={left={column={name=x2, table_ref=ca}}, right={column={name=z3, table_ref=zc}}, operator==}}}}}}, alias=cc}}, query={select={1={column={name=z1, table_ref=cc}}}, from={table={alias=null, table=cc}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[z1]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab_z={z1=[[@35,104:105='zc',<381>,3:14]], z2=[[@43,140:141='zc',<381>,4:13]], z3=[[@59,208:209='zc',<381>,5:37]]}, tab_x={x1=[[@5,19:20='xa',<381>,1:19]], x2=[[@9,26:27='xa',<381>,1:26]]}, tab_y={y1=[[@22,65:66='yb',<381>,2:14]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query4={z1=[[@37,107:108='z1',<381>,3:17], [@65,223:224='cc',<381>,6:7]]}, query5={z1=[[@67,226:227='z1',<381>,6:10]]}, query0={x1=[[@7,22:23='x1',<381>,1:22], [@49,157:158='ca',<381>,4:30]], x2=[[@11,29:30='x2',<381>,1:29], [@55,200:201='ca',<381>,5:29]]}, query1={y1=[[@24,68:69='y1',<381>,2:17]]}, query2={x1=[[@51,160:161='x1',<381>,4:33]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query5={context_list={ca=query0, cb=query1, cc=query4}, query_dictionary={z1=[[@67,226:227='z1',<381>,6:10]]}, def_query1={context_list={ca=query0}, query_dictionary={y1=[[@24,68:69='y1',<381>,2:17]]}, table_dictionary={tab_y={y1=[[@22,65:66='yb',<381>,2:14]]}}, interface={y1=[{name=y1, table_ref=yb}]}, table_alias={yb=tab_y, ca=query0}}, def_query0={query_dictionary={x1=[[@7,22:23='x1',<381>,1:22], [@49,157:158='ca',<381>,4:30]], x2=[[@11,29:30='x2',<381>,1:29], [@55,200:201='ca',<381>,5:29]]}, table_dictionary={tab_x={x1=[[@5,19:20='xa',<381>,1:19]], x2=[[@9,26:27='xa',<381>,1:26]]}}, interface={x1=[{name=x1, table_ref=xa}], x2=[{name=x2, table_ref=xa}]}, table_alias={xa=tab_x}}, interface={z1=[{name=z1, table_ref=cc}]}, def_query4={context_list={ca=query0, cb=query1}, query_dictionary={z1=[[@37,107:108='z1',<381>,3:17], [@65,223:224='cc',<381>,6:7]]}, table_dictionary={tab_z={z1=[[@35,104:105='zc',<381>,3:14]], z2=[[@43,140:141='zc',<381>,4:13]], z3=[[@59,208:209='zc',<381>,5:37]]}}, dependent_queries={in_list3={query=query2, type=filters}}, filters=[], interface={z1=[{name=z1, table_ref=zc}]}, table_alias={zc=tab_z, ca=query0, cb=query1}, def_query2={context_list={ca=query0, cb=query1}, query_dictionary={x1=[[@51,160:161='x1',<381>,4:33]]}, filters=[{name=x2, table_ref=ca}, {name=z3, table_ref=zc}], interface={x1=[{name=x1, table_ref=ca}]}, table_alias={ca=query0, cb=query1}}}, table_alias={cc=query4, ca=query0, cb=query1}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void correlatedInSubqueryFinalQueryReferencesCteChainTest() {
		final String query = "WITH fa AS (SELECT pa.p1, pa.p2 FROM tab_p AS pa),"
		    + "\nfb AS (SELECT qb.q1 FROM tab_q AS qb)"
		    + "\nSELECT pa.p1 FROM fa AS pa JOIN fb ON pa.p1 = fb.q1"
		    + "\nWHERE pa.p2 IN (SELECT ff.p2 FROM fa AS ff"
		    + "\n                WHERE ff.p1 = pa.p1)";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		assertNoFatalErrors(extractor);
		Assert.assertEquals("AST is wrong", "{SQL={with={1={cte={select={1={column={name=p1, table_ref=pa}}, 2={column={name=p2, table_ref=pa}}}, from={table={alias=pa, table=tab_p}}}, alias=fa}, 2={cte={select={1={column={name=q1, table_ref=qb}}}, from={table={alias=qb, table=tab_q}}}, alias=fb}}, query={select={1={column={name=p1, table_ref=pa}}}, from={join={1={table={alias=pa, table=fa}}, 2={join=JOIN, on={condition={left={column={name=p1, table_ref=pa}}, right={column={name=q1, table_ref=fb}}, operator==}}}, 3={table={alias=null, table=fb}}}}, where={in={item={column={name=p2, table_ref=pa}}, in_list={select={1={column={name=p2, table_ref=ff}}}, from={table={alias=ff, table=fa}}, where={condition={left={column={name=p1, table_ref=ff}}, right={column={name=p1, table_ref=pa}}, operator==}}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[p1]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab_p={p1=[[@5,19:20='pa',<381>,1:19]], p2=[[@9,26:27='pa',<381>,1:26]]}, tab_q={q1=[[@22,65:66='qb',<381>,2:14]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query4={p1=[[@33,99:100='p1',<381>,3:10]]}, query0={p1=[[@7,22:23='p1',<381>,1:22], [@63,206:207='ff',<381>,5:22], [@67,214:215='pa',<381>,5:30], [@31,96:97='pa',<381>,3:7], [@41,127:128='pa',<381>,3:38]], p2=[[@11,29:30='p2',<381>,1:29], [@55,164:165='ff',<381>,4:23], [@49,147:148='pa',<381>,4:6]]}, query1={q1=[[@24,68:69='q1',<381>,2:17], [@45,135:136='fb',<381>,3:46]]}, query2={p2=[[@57,167:168='p2',<381>,4:26]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query4={context_list={fa=query0, fb=query1, pa=query0}, query_dictionary={p1=[[@33,99:100='p1',<381>,3:10]]}, dependent_queries={in_list3={query=query2, type=filters}}, def_query1={context_list={fa=query0}, query_dictionary={q1=[[@24,68:69='q1',<381>,2:17], [@45,135:136='fb',<381>,3:46]]}, table_dictionary={tab_q={q1=[[@22,65:66='qb',<381>,2:14]]}}, interface={q1=[{name=q1, table_ref=qb}]}, table_alias={qb=tab_q, fa=query0}}, def_query0={query_dictionary={p1=[[@7,22:23='p1',<381>,1:22], [@63,206:207='ff',<381>,5:22], [@67,214:215='pa',<381>,5:30], [@31,96:97='pa',<381>,3:7], [@41,127:128='pa',<381>,3:38]], p2=[[@11,29:30='p2',<381>,1:29], [@55,164:165='ff',<381>,4:23], [@49,147:148='pa',<381>,4:6]]}, table_dictionary={tab_p={p1=[[@5,19:20='pa',<381>,1:19]], p2=[[@9,26:27='pa',<381>,1:26]]}}, interface={p1=[{name=p1, table_ref=pa}], p2=[{name=p2, table_ref=pa}]}, table_alias={pa=tab_p}}, filters=[{name=p1, table_ref=pa}, {name=q1, table_ref=fb}], interface={p1=[{name=p1, table_ref=pa}]}, table_alias={pa=query0, fa=query0, fb=query1}, def_query2={context_list={fa=query0, fb=query1, ff=query0}, query_dictionary={p2=[[@57,167:168='p2',<381>,4:26]]}, filters=[{name=p1, table_ref=ff}, {name=p1, table_ref=pa}], interface={p2=[{name=p2, table_ref=ff}]}, table_alias={ff=query0, fa=query0, fb=query1}}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void correlatedInSubqueryNestedCteWithOuterRefTest() {
		final String query = "WITH oa AS (SELECT ra.r1 FROM tab_r AS ra),"
		    + "\nob AS (WITH ib AS (SELECT sb.s1 FROM tab_s AS sb)"
		    + "\n       SELECT tb.t1 FROM tab_t AS tb"
		    + "\n       WHERE tb.t2 IN (SELECT ib.s1 FROM ib"
		    + "\n                       WHERE ib.s1 = oa.r1))"
		    + "\nSELECT ob.t1 FROM ob JOIN oa ON oa.r1 = ob.t1";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		assertNoFatalErrors(extractor);
		Assert.assertEquals("AST is wrong", "{SQL={with={1={cte={select={1={column={name=r1, table_ref=ra}}}, from={table={alias=ra, table=tab_r}}}, alias=oa}, 2={cte={with={1={cte={select={1={column={name=s1, table_ref=sb}}}, from={table={alias=sb, table=tab_s}}}, alias=ib}}, query={select={1={column={name=t1, table_ref=tb}}}, from={table={alias=tb, table=tab_t}}, where={in={item={column={name=t2, table_ref=tb}}, in_list={select={1={column={name=s1, table_ref=ib}}}, from={table={alias=null, table=ib}}, where={condition={left={column={name=s1, table_ref=ib}}, right={column={name=r1, table_ref=oa}}, operator==}}}}}}}, alias=ob}}, query={select={1={column={name=t1, table_ref=ob}}}, from={join={1={table={alias=null, table=ob}}, 2={join=JOIN, on={condition={left={column={name=r1, table_ref=oa}}, right={column={name=t1, table_ref=ob}}, operator==}}}, 3={table={alias=null, table=oa}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[t1]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab_r={r1=[[@5,19:20='ra',<381>,1:19]]}, tab_s={s1=[[@22,70:71='sb',<381>,2:26]]}, tab_t={t1=[[@31,108:109='tb',<381>,3:14]], t2=[[@39,144:145='tb',<381>,4:13]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query4={t1=[[@33,111:112='t1',<381>,3:17], [@61,227:228='ob',<381>,6:7], [@73,260:261='ob',<381>,6:40]]}, query5={t1=[[@63,230:231='t1',<381>,6:10]]}, query0={r1=[[@7,22:23='r1',<381>,1:22], [@55,212:213='oa',<381>,5:37], [@69,252:253='oa',<381>,6:32]]}, query1={s1=[[@24,73:74='s1',<381>,2:29], [@45,161:162='ib',<381>,4:30], [@51,204:205='ib',<381>,5:29]]}, query2={s1=[[@47,164:165='s1',<381>,4:33]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query5={context_list={ob=query4}, query_dictionary={t1=[[@63,230:231='t1',<381>,6:10]]}, filters=[{name=r1, table_ref=oa}, {name=t1, table_ref=ob}], interface={t1=[{name=t1, table_ref=ob}]}, def_query4={context_list={oa=query0, ib=query1}, query_dictionary={t1=[[@33,111:112='t1',<381>,3:17], [@61,227:228='ob',<381>,6:7], [@73,260:261='ob',<381>,6:40]]}, table_dictionary={tab_t={t1=[[@31,108:109='tb',<381>,3:14]], t2=[[@39,144:145='tb',<381>,4:13]]}}, dependent_queries={in_list3={query=query2, type=filters}}, def_query1={context_list={oa=query0}, query_dictionary={s1=[[@24,73:74='s1',<381>,2:29], [@45,161:162='ib',<381>,4:30], [@51,204:205='ib',<381>,5:29]]}, table_dictionary={tab_s={s1=[[@22,70:71='sb',<381>,2:26]]}}, interface={s1=[{name=s1, table_ref=sb}]}, table_alias={oa=query0, sb=tab_s}}, def_query0={query_dictionary={r1=[[@7,22:23='r1',<381>,1:22], [@55,212:213='oa',<381>,5:37], [@69,252:253='oa',<381>,6:32]]}, table_dictionary={tab_r={r1=[[@5,19:20='ra',<381>,1:19]]}}, interface={r1=[{name=r1, table_ref=ra}]}, table_alias={ra=tab_r}}, filters=[], interface={t1=[{name=t1, table_ref=tb}]}, table_alias={oa=query0, ib=query1, tb=tab_t}, def_query2={context_list={oa=query0, ib=query1}, query_dictionary={s1=[[@47,164:165='s1',<381>,4:33]]}, filters=[{name=s1, table_ref=ib}, {name=r1, table_ref=oa}], interface={s1=[{name=s1, table_ref=ib}]}, table_alias={oa=query0, ib=query1}}}, table_alias={oa=query0, ob=query4}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void correlatedExistsSubqueryNestedJoinSubqueryTest() {
		final String query = "SELECT oa.ex1 FROM tab_a AS oa"
		    + "\nWHERE EXISTS (SELECT 1 FROM tab_b AS ib"
		    + "\nJOIN (SELECT ic.ex7 FROM tab_c AS ic"
		    + "\n        WHERE ic.ex8 = oa.ex2) AS ix"
		    + "\n        ON ix.ex7 = ib.ex6)";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		assertNoFatalErrors(extractor);
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=ex1, table_ref=oa}}}, from={table={alias=oa, table=tab_a}}, where={exists={select={1={literal=1}}, from={join={1={table={alias=ib, table=tab_b}}, 2={join=JOIN, on={condition={left={column={name=ex7, table_ref=ix}}, right={column={name=ex6, table_ref=ib}}, operator==}}}, 3={table={alias=ix, query={select={1={column={name=ex7, table_ref=ic}}}, from={table={alias=ic, table=tab_c}}, where={condition={left={column={name=ex8, table_ref=ic}}, right={column={name=ex2, table_ref=oa}}, operator==}}}}}}}, operator=EXISTS}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[ex1]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab_a={ex2=[[@32,131:132='oa',<381>,4:23]], ex1=[[@1,7:8='oa',<381>,1:7]]}, tab_b={ex6=[[@43,165:166='ib',<381>,5:20]]}, tab_c={ex8=[[@28,122:123='ic',<381>,4:14]], ex7=[[@20,84:85='ic',<381>,3:13]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={ex7=[[@22,87:89='ex7',<381>,3:16], [@39,156:157='ix',<381>,5:11]]}, query1={unnamed_0=[[@12,52:52='1',<300>,2:21]]}, query3={ex1=[[@3,10:12='ex1',<381>,1:10]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query3={query_dictionary={ex1=[[@3,10:12='ex1',<381>,1:10]]}, table_dictionary={tab_a={ex1=[[@1,7:8='oa',<381>,1:7]]}}, def_query1={query_dictionary={unnamed_0=[[@12,52:52='1',<300>,2:21]]}, table_dictionary={tab_b={ex6=[[@43,165:166='ib',<381>,5:20]]}}, def_query0={query_dictionary={ex7=[[@22,87:89='ex7',<381>,3:16], [@39,156:157='ix',<381>,5:11]]}, table_dictionary={tab_a={ex2=[[@32,131:132='oa',<381>,4:23]]}, tab_c={ex8=[[@28,122:123='ic',<381>,4:14]], ex7=[[@20,84:85='ic',<381>,3:13]]}}, filters=[{name=ex8, table_ref=ic}, {name=ex2, table_ref=oa}], interface={ex7=[{name=ex7, table_ref=ic}]}, table_alias={ic=tab_c}}, filters=[{name=ex7, table_ref=ix}, {name=ex6, table_ref=ib}], interface={unnamed_0=[]}, table_alias={ib=tab_b, ix=query0}}, dependent_queries={exists2={query=query1, type=filters}}, filters=[], interface={ex1=[{name=ex1, table_ref=oa}]}, table_alias={oa=tab_a}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void correlatedExistsSubqueryUnionContextTest() {
		final String query = "SELECT ua.eu1 FROM tab_a AS ua"
		    + "\nWHERE EXISTS (SELECT 1 FROM ("
		    + "\nSELECT ub.eu4 FROM tab_b AS ub"
		    + "\n        WHERE ub.eu5 = ua.eu1"
		    + "\nUNION SELECT uc.eu4 FROM tab_c AS uc"
		    + "\n        WHERE uc.eu6 = ua.eu2) AS sub"
		    + "\n        WHERE sub.eu4 = ua.eu3)";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		assertNoFatalErrors(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=eu1, table_ref=ua}}}, from={table={alias=ua, table=tab_a}}, where={exists={select={1={literal=1}}, from={table={alias=sub, query={union={1={select={1={column={name=eu4, table_ref=ub}}}, from={table={alias=ub, table=tab_b}}, where={condition={left={column={name=eu5, table_ref=ub}}, right={column={name=eu1, table_ref=ua}}, operator==}}}, 2={union={qualifier=null, operator=UNION}}, 3={select={1={column={name=eu4, table_ref=uc}}}, from={table={alias=uc, table=tab_c}}, where={condition={left={column={name=eu6, table_ref=uc}}, right={column={name=eu2, table_ref=ua}}, operator==}}}}}}}, where={condition={left={column={name=eu4, table_ref=sub}}, right={column={name=eu3, table_ref=ua}}, operator==}}, operator=EXISTS}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[eu1]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab_a={eu1=[[@28,115:116='ua',<381>,4:23], [@1,7:8='ua',<381>,1:7]], eu3=[[@56,221:222='ua',<381>,7:24]], eu2=[[@45,182:183='ua',<381>,6:23]]}, tab_b={eu5=[[@24,106:107='ub',<381>,4:14]], eu4=[[@16,68:69='ub',<381>,3:7]]}, tab_c={eu4=[[@33,135:136='uc',<381>,5:13]], eu6=[[@41,173:174='uc',<381>,6:14]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{union2={eu4=[[@52,211:213='sub',<381>,7:14]]}, query5={eu1=[[@3,10:12='eu1',<381>,1:10]]}, query0={eu4=[[@18,71:73='eu4',<381>,3:10]]}, query1={eu4=[[@35,138:140='eu4',<381>,5:16]]}, query3={unnamed_0=[[@12,52:52='1',<300>,2:21]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query5={query_dictionary={eu1=[[@3,10:12='eu1',<381>,1:10]]}, table_dictionary={tab_a={eu1=[[@1,7:8='ua',<381>,1:7], [@28,115:116='ua',<381>,4:23]], eu3=[[@56,221:222='ua',<381>,7:24]]}}, dependent_queries={exists4={query=query3, type=filters}}, filters=[], interface={eu1=[{name=eu1, table_ref=ua}]}, def_query3={def_union2={query_dictionary={eu4=[[@52,211:213='sub',<381>,7:14]]}, def_query1={query_dictionary={eu4=[[@35,138:140='eu4',<381>,5:16]]}, table_dictionary={tab_a={eu2=[[@45,182:183='ua',<381>,6:23]]}, tab_c={eu4=[[@33,135:136='uc',<381>,5:13]], eu6=[[@41,173:174='uc',<381>,6:14]]}}, filters=[{name=eu6, table_ref=uc}, {name=eu2, table_ref=ua}], interface={eu4=[{name=eu4, table_ref=uc}]}, table_alias={uc=tab_c}}, def_query0={query_dictionary={eu4=[[@18,71:73='eu4',<381>,3:10]]}, table_dictionary={tab_a={eu1=[[@28,115:116='ua',<381>,4:23], [@1,7:8='ua',<381>,1:7]]}, tab_b={eu5=[[@24,106:107='ub',<381>,4:14]], eu4=[[@16,68:69='ub',<381>,3:7]]}}, filters=[{name=eu5, table_ref=ub}, {name=eu1, table_ref=ua}], interface={eu4=[{name=eu4, table_ref=ub}]}, table_alias={ub=tab_b}}, interface={eu4=[{name=eu4, table_ref=ub}, {name=eu4, table_ref=uc}]}}, query_dictionary={unnamed_0=[[@12,52:52='1',<300>,2:21]]}, filters=[{name=eu4, table_ref=sub}, {name=eu3, table_ref=ua}], interface={unnamed_0=[]}, table_alias={sub=union2}}, table_alias={ua=tab_a}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void correlatedExistsSubqueryIntersectContextTest() {
		final String query = "SELECT oi.ex1 FROM tab_o AS oi"
		    + "\nWHERE EXISTS (SELECT 1 FROM ("
		    + "\nSELECT id.ex9 FROM tab_d AS id"
		    + "\n        WHERE id.ex3 = oi.ex1"
		    + "\nINTERSECT SELECT ie.ex9 FROM tab_e AS ie"
		    + "\n        WHERE ie.ex4 = oi.ex2) AS ii"
		    + "\n        WHERE ii.ex9 = oi.ex3)";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		assertNoFatalErrors(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=ex1, table_ref=oi}}}, from={table={alias=oi, table=tab_o}}, where={exists={select={1={literal=1}}, from={table={alias=ii, query={intersect={1={select={1={column={name=ex9, table_ref=id}}}, from={table={alias=id, table=tab_d}}, where={condition={left={column={name=ex3, table_ref=id}}, right={column={name=ex1, table_ref=oi}}, operator==}}}, 2={intersect={qualifier=null, operator=INTERSECT}}, 3={select={1={column={name=ex9, table_ref=ie}}}, from={table={alias=ie, table=tab_e}}, where={condition={left={column={name=ex4, table_ref=ie}}, right={column={name=ex2, table_ref=oi}}, operator==}}}}}}}, where={condition={left={column={name=ex9, table_ref=ii}}, right={column={name=ex3, table_ref=oi}}, operator==}}, operator=EXISTS}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[ex1]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab_o={ex3=[[@56,223:224='oi',<381>,7:23]], ex2=[[@45,186:187='oi',<381>,6:23]], ex1=[[@28,115:116='oi',<381>,4:23], [@1,7:8='oi',<381>,1:7]]}, tab_d={ex3=[[@24,106:107='id',<381>,4:14]], ex9=[[@16,68:69='id',<381>,3:7]]}, tab_e={ex4=[[@41,177:178='ie',<381>,6:14]], ex9=[[@33,139:140='ie',<381>,5:17]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{intersect2={ex9=[[@52,214:215='ii',<381>,7:14]]}, query5={ex1=[[@3,10:12='ex1',<381>,1:10]]}, query0={ex9=[[@18,71:73='ex9',<381>,3:10]]}, query1={ex9=[[@35,142:144='ex9',<381>,5:20]]}, query3={unnamed_0=[[@12,52:52='1',<300>,2:21]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query5={query_dictionary={ex1=[[@3,10:12='ex1',<381>,1:10]]}, table_dictionary={tab_o={ex3=[[@56,223:224='oi',<381>,7:23]], ex1=[[@1,7:8='oi',<381>,1:7], [@28,115:116='oi',<381>,4:23]]}}, dependent_queries={exists4={query=query3, type=filters}}, filters=[], interface={ex1=[{name=ex1, table_ref=oi}]}, def_query3={query_dictionary={unnamed_0=[[@12,52:52='1',<300>,2:21]]}, def_intersect2={query_dictionary={ex9=[[@52,214:215='ii',<381>,7:14]]}, def_query1={query_dictionary={ex9=[[@35,142:144='ex9',<381>,5:20]]}, table_dictionary={tab_o={ex2=[[@45,186:187='oi',<381>,6:23]]}, tab_e={ex4=[[@41,177:178='ie',<381>,6:14]], ex9=[[@33,139:140='ie',<381>,5:17]]}}, filters=[{name=ex4, table_ref=ie}, {name=ex2, table_ref=oi}], interface={ex9=[{name=ex9, table_ref=ie}]}, table_alias={ie=tab_e}}, def_query0={query_dictionary={ex9=[[@18,71:73='ex9',<381>,3:10]]}, table_dictionary={tab_o={ex1=[[@28,115:116='oi',<381>,4:23], [@1,7:8='oi',<381>,1:7]]}, tab_d={ex3=[[@24,106:107='id',<381>,4:14]], ex9=[[@16,68:69='id',<381>,3:7]]}}, filters=[{name=ex3, table_ref=id}, {name=ex1, table_ref=oi}], interface={ex9=[{name=ex9, table_ref=id}]}, table_alias={id=tab_d}}, interface={ex9=[{name=ex9, table_ref=id}, {name=ex9, table_ref=ie}]}}, filters=[{name=ex9, table_ref=ii}, {name=ex3, table_ref=oi}], interface={unnamed_0=[]}, table_alias={ii=intersect2}}, table_alias={oi=tab_o}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void correlatedExistsSubqueryWithNestedScalarPredicandTest() {
		final String query = "SELECT sa.ex1 FROM tab_s AS sa"
		    + "\nWHERE EXISTS (SELECT 1 FROM tab_i AS ia"
		    + "\n              WHERE ia.ex9 = ("
		    + "\nSELECT max(jb.jx1) FROM tab_j AS jb"
		    + "\n              WHERE jb.jx2 = sa.ex2))";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		assertNoFatalErrors(extractor);
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=ex1, table_ref=sa}}}, from={table={alias=sa, table=tab_s}}, where={exists={select={1={literal=1}}, from={table={alias=ia, table=tab_i}}, where={condition={left={column={name=ex9, table_ref=ia}}, right={select={1={function={function_name=max, qualifier=null, parameters={column={name=jx1, table_ref=jb}}}}}, from={table={alias=jb, table=tab_j}}, where={condition={left={column={name=jx2, table_ref=jb}}, right={column={name=ex2, table_ref=sa}}, operator==}}}, operator==}}, operator=EXISTS}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[ex1]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab_j={jx1=[[@26,113:114='jb',<381>,4:11]], jx2=[[@35,158:159='jb',<381>,5:20]]}, tab_s={ex2=[[@39,167:168='sa',<381>,5:29]], ex1=[[@1,7:8='sa',<381>,1:7]]}, tab_i={ex9=[[@18,91:92='ia',<381>,3:20]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query4={ex1=[[@3,10:12='ex1',<381>,1:10]]}, query0={unnamed_1=[[@29,119:119=')',<288>,4:17]]}, query2={unnamed_0=[[@12,52:52='1',<300>,2:21]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query4={query_dictionary={ex1=[[@3,10:12='ex1',<381>,1:10]]}, table_dictionary={tab_s={ex2=[[@39,167:168='sa',<381>,5:29]], ex1=[[@1,7:8='sa',<381>,1:7]]}}, dependent_queries={exists3={query=query2, type=filters}}, filters=[], interface={ex1=[{name=ex1, table_ref=sa}]}, table_alias={sa=tab_s}, def_query2={query_dictionary={unnamed_0=[[@12,52:52='1',<300>,2:21]]}, table_dictionary={tab_i={ex9=[[@18,91:92='ia',<381>,3:20]]}}, dependent_queries={predicand1={query=query0, type=filters}}, def_query0={query_dictionary={unnamed_1=[[@29,119:119=')',<288>,4:17]]}, table_dictionary={tab_j={jx1=[[@26,113:114='jb',<381>,4:11]], jx2=[[@35,158:159='jb',<381>,5:20]]}}, filters=[{name=jx2, table_ref=jb}, {name=ex2, table_ref=sa}], interface={unnamed_1=[{name=jx1, table_ref=jb}]}, table_alias={jb=tab_j}}, filters=[{name=ex9, table_ref=ia}], interface={unnamed_0=[]}, table_alias={ia=tab_i}}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void correlatedExistsSubqueryWithNestedInSubqueryTest() {
		final String query = "SELECT ea.ex1 FROM tab_e AS ea"
		    + "\nWHERE EXISTS (SELECT 1 FROM tab_x AS ex"
		    + "\n              WHERE ex.ex8 IN ("
		    + "\nSELECT ey.ey1 FROM tab_y AS ey"
		    + "\n              WHERE ey.ey2 = ea.ex1))";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		assertNoFatalErrors(extractor);
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=ex1, table_ref=ea}}}, from={table={alias=ea, table=tab_e}}, where={exists={select={1={literal=1}}, from={table={alias=ex, table=tab_x}}, where={in={item={column={name=ex8, table_ref=ex}}, in_list={select={1={column={name=ey1, table_ref=ey}}}, from={table={alias=ey, table=tab_y}}, where={condition={left={column={name=ey2, table_ref=ey}}, right={column={name=ex1, table_ref=ea}}, operator==}}}}}, operator=EXISTS}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[ex1]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab_e={ex1=[[@1,7:8='ea',<381>,1:7], [@36,163:164='ea',<381>,5:29]]}, tab_x={ex8=[[@18,91:92='ex',<381>,3:20]]}, tab_y={ey1=[[@24,110:111='ey',<381>,4:7]], ey2=[[@32,154:155='ey',<381>,5:20]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query4={ex1=[[@3,10:12='ex1',<381>,1:10]]}, query0={ey1=[[@26,113:115='ey1',<381>,4:10]]}, query2={unnamed_0=[[@12,52:52='1',<300>,2:21]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query4={query_dictionary={ex1=[[@3,10:12='ex1',<381>,1:10]]}, table_dictionary={tab_e={ex1=[[@1,7:8='ea',<381>,1:7], [@36,163:164='ea',<381>,5:29]]}}, dependent_queries={exists3={query=query2, type=filters}}, filters=[], interface={ex1=[{name=ex1, table_ref=ea}]}, table_alias={ea=tab_e}, def_query2={query_dictionary={unnamed_0=[[@12,52:52='1',<300>,2:21]]}, table_dictionary={tab_x={ex8=[[@18,91:92='ex',<381>,3:20]]}}, dependent_queries={in_list1={query=query0, type=filters}}, def_query0={query_dictionary={ey1=[[@26,113:115='ey1',<381>,4:10]]}, table_dictionary={tab_y={ey1=[[@24,110:111='ey',<381>,4:7]], ey2=[[@32,154:155='ey',<381>,5:20]]}}, filters=[{name=ey2, table_ref=ey}, {name=ex1, table_ref=ea}], interface={ey1=[{name=ey1, table_ref=ey}]}, table_alias={ey=tab_y}}, filters=[], interface={unnamed_0=[]}, table_alias={ex=tab_x}}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void correlatedExistsSubqueryFirstCteStandaloneTest() {
		final String query = "WITH c1a AS ("
		    + "\n  SELECT ta.t1c1 FROM tab1 AS ta"
		    + "\n  WHERE EXISTS (SELECT 1 FROM tab2 AS tb"
		    + "\n                WHERE tb.t2c1 = ta.t1c1)"
		    + "\n)"
		    + "\nSELECT c1a.t1c1 FROM c1a";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		assertNoFatalErrors(extractor);
		Assert.assertEquals("AST is wrong", "{SQL={with={1={cte={select={1={column={name=t1c1, table_ref=ta}}}, from={table={alias=ta, table=tab1}}, where={exists={select={1={literal=1}}, from={table={alias=tb, table=tab2}}, where={condition={left={column={name=t2c1, table_ref=tb}}, right={column={name=t1c1, table_ref=ta}}, operator==}}, operator=EXISTS}}}, alias=c1a}}, query={select={1={column={name=t1c1, table_ref=c1a}}}, from={table={alias=null, table=c1a}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[t1c1]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={t1c1=[[@5,23:24='ta',<381>,2:9], [@26,120:121='ta',<381>,4:32]]}, tab2={t2c1=[[@22,110:111='tb',<381>,4:22]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={unnamed_0=[[@16,70:70='1',<300>,3:23]]}, query2={t1c1=[[@7,26:29='t1c1',<381>,2:12], [@32,138:140='c1a',<381>,6:7]]}, query3={t1c1=[[@34,142:145='t1c1',<381>,6:11]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query3={context_list={c1a=query2}, query_dictionary={t1c1=[[@34,142:145='t1c1',<381>,6:11]]}, interface={t1c1=[{name=t1c1, table_ref=c1a}]}, table_alias={c1a=query2}, def_query2={query_dictionary={t1c1=[[@7,26:29='t1c1',<381>,2:12], [@32,138:140='c1a',<381>,6:7]]}, table_dictionary={tab1={t1c1=[[@5,23:24='ta',<381>,2:9], [@26,120:121='ta',<381>,4:32]]}}, dependent_queries={exists1={query=query0, type=filters}}, def_query0={query_dictionary={unnamed_0=[[@16,70:70='1',<300>,3:23]]}, table_dictionary={tab2={t2c1=[[@22,110:111='tb',<381>,4:22]]}}, filters=[{name=t2c1, table_ref=tb}, {name=t1c1, table_ref=ta}], interface={unnamed_0=[]}, table_alias={tb=tab2}}, filters=[], interface={t1c1=[{name=t1c1, table_ref=ta}]}, table_alias={ta=tab1}}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void correlatedExistsSubqueryMiddleCteReferencesFirstCteTest() {
		final String query = "WITH w1 AS (SELECT aa.a1, aa.a2 FROM tab_a AS aa),"
		    + "\nw2 AS (SELECT bb.b1 FROM tab_b AS bb"
		    + "\n       WHERE EXISTS (SELECT 1 FROM w1 AS ww"
		    + "\n                     WHERE ww.a1 = bb.b1"
		    + "\n                       AND ww.a2 = bb.b2))"
		    + "\nSELECT w2.b1 FROM w2";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		assertNoFatalErrors(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={with={1={cte={select={1={column={name=a1, table_ref=aa}}, 2={column={name=a2, table_ref=aa}}}, from={table={alias=aa, table=tab_a}}}, alias=w1}, 2={cte={select={1={column={name=b1, table_ref=bb}}}, from={table={alias=bb, table=tab_b}}, where={exists={select={1={literal=1}}, from={table={alias=ww, table=w1}}, where={and={1={condition={left={column={name=a1, table_ref=ww}}, right={column={name=b1, table_ref=bb}}, operator==}}, 2={condition={left={column={name=a2, table_ref=ww}}, right={column={name=b2, table_ref=bb}}, operator==}}}}, operator=EXISTS}}}, alias=w2}}, query={select={1={column={name=b1, table_ref=w2}}}, from={table={alias=null, table=w2}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[b1]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab_a={a1=[[@5,19:20='aa',<381>,1:19]], a2=[[@9,26:27='aa',<381>,1:26]]}, tab_b={b2=[[@51,208:209='bb',<381>,5:35]], b1=[[@22,65:66='bb',<381>,2:14], [@43,167:168='bb',<381>,4:35]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query4={b1=[[@59,226:227='b1',<381>,6:10]]}, query0={a1=[[@7,22:23='a1',<381>,1:22], [@39,159:160='ww',<381>,4:27]], a2=[[@11,29:30='a2',<381>,1:29], [@47,200:201='ww',<381>,5:27]]}, query1={unnamed_0=[[@33,116:116='1',<300>,3:28]]}, query3={b1=[[@24,68:69='b1',<381>,2:17], [@57,223:224='w2',<381>,6:7]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query4={context_list={w1=query0, w2=query3}, query_dictionary={b1=[[@59,226:227='b1',<381>,6:10]]}, def_query0={query_dictionary={a1=[[@7,22:23='a1',<381>,1:22], [@39,159:160='ww',<381>,4:27]], a2=[[@11,29:30='a2',<381>,1:29], [@47,200:201='ww',<381>,5:27]]}, table_dictionary={tab_a={a1=[[@5,19:20='aa',<381>,1:19]], a2=[[@9,26:27='aa',<381>,1:26]]}}, interface={a1=[{name=a1, table_ref=aa}], a2=[{name=a2, table_ref=aa}]}, table_alias={aa=tab_a}}, interface={b1=[{name=b1, table_ref=w2}]}, table_alias={w1=query0, w2=query3}, def_query3={context_list={w1=query0}, query_dictionary={b1=[[@24,68:69='b1',<381>,2:17], [@57,223:224='w2',<381>,6:7]]}, table_dictionary={tab_b={b2=[[@51,208:209='bb',<381>,5:35]], b1=[[@22,65:66='bb',<381>,2:14], [@43,167:168='bb',<381>,4:35]]}}, def_query1={context_list={w1=query0, ww=query0}, query_dictionary={unnamed_0=[[@33,116:116='1',<300>,3:28]]}, filters=[{name=a1, table_ref=ww}, {name=b1, table_ref=bb}, {name=a2, table_ref=ww}, {name=b2, table_ref=bb}], interface={unnamed_0=[]}, table_alias={ww=query0, w1=query0}}, dependent_queries={exists2={query=query1, type=filters}}, filters=[], interface={b1=[{name=b1, table_ref=bb}]}, table_alias={bb=tab_b, w1=query0}}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void correlatedExistsSubqueryLastCteReferencesPriorCtesTest() {
		final String query = "WITH ca AS (SELECT xa.x1, xa.x2 FROM tab_x AS xa),"
		    + "\ncb AS (SELECT yb.y1 FROM tab_y AS yb),"
		    + "\ncc AS (SELECT zc.z1 FROM tab_z AS zc"
		    + "\n       WHERE EXISTS (SELECT 1 FROM ca"
		    + "\n                     WHERE ca.x2 = zc.z3"
		    + "\n                       AND ca.x1 = zc.z2))"
		    + "\nSELECT cc.z1 FROM cc";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		assertNoFatalErrors(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={with={1={cte={select={1={column={name=x1, table_ref=xa}}, 2={column={name=x2, table_ref=xa}}}, from={table={alias=xa, table=tab_x}}}, alias=ca}, 2={cte={select={1={column={name=y1, table_ref=yb}}}, from={table={alias=yb, table=tab_y}}}, alias=cb}, 3={cte={select={1={column={name=z1, table_ref=zc}}}, from={table={alias=zc, table=tab_z}}, where={exists={select={1={literal=1}}, from={table={alias=null, table=ca}}, where={and={1={condition={left={column={name=x2, table_ref=ca}}, right={column={name=z3, table_ref=zc}}, operator==}}, 2={condition={left={column={name=x1, table_ref=ca}}, right={column={name=z2, table_ref=zc}}, operator==}}}}, operator=EXISTS}}}, alias=cc}}, query={select={1={column={name=z1, table_ref=cc}}}, from={table={alias=null, table=cc}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[z1]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab_z={z1=[[@35,104:105='zc',<381>,3:14]], z2=[[@62,241:242='zc',<381>,6:35]], z3=[[@54,200:201='zc',<381>,5:35]]}, tab_x={x1=[[@5,19:20='xa',<381>,1:19]], x2=[[@9,26:27='xa',<381>,1:26]]}, tab_y={y1=[[@22,65:66='yb',<381>,2:14]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query4={z1=[[@37,107:108='z1',<381>,3:17], [@68,256:257='cc',<381>,7:7]]}, query5={z1=[[@70,259:260='z1',<381>,7:10]]}, query0={x1=[[@7,22:23='x1',<381>,1:22], [@58,233:234='ca',<381>,6:27]], x2=[[@11,29:30='x2',<381>,1:29], [@50,192:193='ca',<381>,5:27]]}, query1={y1=[[@24,68:69='y1',<381>,2:17]]}, query2={unnamed_0=[[@46,155:155='1',<300>,4:28]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query5={context_list={ca=query0, cb=query1, cc=query4}, query_dictionary={z1=[[@70,259:260='z1',<381>,7:10]]}, def_query1={context_list={ca=query0}, query_dictionary={y1=[[@24,68:69='y1',<381>,2:17]]}, table_dictionary={tab_y={y1=[[@22,65:66='yb',<381>,2:14]]}}, interface={y1=[{name=y1, table_ref=yb}]}, table_alias={yb=tab_y, ca=query0}}, def_query0={query_dictionary={x1=[[@7,22:23='x1',<381>,1:22], [@58,233:234='ca',<381>,6:27]], x2=[[@11,29:30='x2',<381>,1:29], [@50,192:193='ca',<381>,5:27]]}, table_dictionary={tab_x={x1=[[@5,19:20='xa',<381>,1:19]], x2=[[@9,26:27='xa',<381>,1:26]]}}, interface={x1=[{name=x1, table_ref=xa}], x2=[{name=x2, table_ref=xa}]}, table_alias={xa=tab_x}}, interface={z1=[{name=z1, table_ref=cc}]}, def_query4={context_list={ca=query0, cb=query1}, query_dictionary={z1=[[@37,107:108='z1',<381>,3:17], [@68,256:257='cc',<381>,7:7]]}, table_dictionary={tab_z={z1=[[@35,104:105='zc',<381>,3:14]], z2=[[@62,241:242='zc',<381>,6:35]], z3=[[@54,200:201='zc',<381>,5:35]]}}, dependent_queries={exists3={query=query2, type=filters}}, filters=[], interface={z1=[{name=z1, table_ref=zc}]}, table_alias={zc=tab_z, ca=query0, cb=query1}, def_query2={context_list={ca=query0, cb=query1}, query_dictionary={unnamed_0=[[@46,155:155='1',<300>,4:28]]}, filters=[{name=x2, table_ref=ca}, {name=z3, table_ref=zc}, {name=x1, table_ref=ca}, {name=z2, table_ref=zc}], interface={unnamed_0=[]}, table_alias={ca=query0, cb=query1}}}, table_alias={cc=query4, ca=query0, cb=query1}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void correlatedExistsSubqueryFinalQueryReferencesCteChainTest() {
		final String query = "WITH fa AS (SELECT pa.p1, pa.p2 FROM tab_p AS pa),"
		    + "\nfb AS (SELECT qb.q1 FROM tab_q AS qb)"
		    + "\nSELECT pa.p1 FROM fa AS pa JOIN fb ON pa.p1 = fb.q1"
		    + "\nWHERE EXISTS (SELECT 1 FROM fa AS ff"
		    + "\n              WHERE ff.p1 = pa.p1 AND ff.p2 = pa.p2)";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		assertNoFatalErrors(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={with={1={cte={select={1={column={name=p1, table_ref=pa}}, 2={column={name=p2, table_ref=pa}}}, from={table={alias=pa, table=tab_p}}}, alias=fa}, 2={cte={select={1={column={name=q1, table_ref=qb}}}, from={table={alias=qb, table=tab_q}}}, alias=fb}}, query={select={1={column={name=p1, table_ref=pa}}}, from={join={1={table={alias=pa, table=fa}}, 2={join=JOIN, on={condition={left={column={name=p1, table_ref=pa}}, right={column={name=q1, table_ref=fb}}, operator==}}}, 3={table={alias=null, table=fb}}}}, where={exists={select={1={literal=1}}, from={table={alias=ff, table=fa}}, where={and={1={condition={left={column={name=p1, table_ref=ff}}, right={column={name=p1, table_ref=pa}}, operator==}}, 2={condition={left={column={name=p2, table_ref=ff}}, right={column={name=p2, table_ref=pa}}, operator==}}}}, operator=EXISTS}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[p1]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab_p={p1=[[@5,19:20='pa',<381>,1:19]], p2=[[@9,26:27='pa',<381>,1:26]]}, tab_q={q1=[[@22,65:66='qb',<381>,2:14]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query4={p1=[[@33,99:100='p1',<381>,3:10]]}, query0={p1=[[@7,22:23='p1',<381>,1:22], [@58,198:199='ff',<381>,5:20], [@62,206:207='pa',<381>,5:28], [@31,96:97='pa',<381>,3:7], [@41,127:128='pa',<381>,3:38]], p2=[[@11,29:30='p2',<381>,1:29], [@66,216:217='ff',<381>,5:38], [@70,224:225='pa',<381>,5:46]]}, query1={q1=[[@24,68:69='q1',<381>,2:17], [@45,135:136='fb',<381>,3:46]]}, query2={unnamed_0=[[@52,162:162='1',<300>,4:21]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query4={context_list={fa=query0, fb=query1, pa=query0}, query_dictionary={p1=[[@33,99:100='p1',<381>,3:10]]}, dependent_queries={exists3={query=query2, type=filters}}, def_query1={context_list={fa=query0}, query_dictionary={q1=[[@24,68:69='q1',<381>,2:17], [@45,135:136='fb',<381>,3:46]]}, table_dictionary={tab_q={q1=[[@22,65:66='qb',<381>,2:14]]}}, interface={q1=[{name=q1, table_ref=qb}]}, table_alias={qb=tab_q, fa=query0}}, def_query0={query_dictionary={p1=[[@7,22:23='p1',<381>,1:22], [@58,198:199='ff',<381>,5:20], [@62,206:207='pa',<381>,5:28], [@31,96:97='pa',<381>,3:7], [@41,127:128='pa',<381>,3:38]], p2=[[@11,29:30='p2',<381>,1:29], [@66,216:217='ff',<381>,5:38], [@70,224:225='pa',<381>,5:46]]}, table_dictionary={tab_p={p1=[[@5,19:20='pa',<381>,1:19]], p2=[[@9,26:27='pa',<381>,1:26]]}}, interface={p1=[{name=p1, table_ref=pa}], p2=[{name=p2, table_ref=pa}]}, table_alias={pa=tab_p}}, filters=[{name=p1, table_ref=pa}, {name=q1, table_ref=fb}], interface={p1=[{name=p1, table_ref=pa}]}, table_alias={pa=query0, fa=query0, fb=query1}, def_query2={context_list={fa=query0, fb=query1, ff=query0}, query_dictionary={unnamed_0=[[@52,162:162='1',<300>,4:21]]}, filters=[{name=p1, table_ref=ff}, {name=p1, table_ref=pa}, {name=p2, table_ref=ff}, {name=p2, table_ref=pa}], interface={unnamed_0=[]}, table_alias={ff=query0, fa=query0, fb=query1}}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void correlatedExistsSubqueryNestedCteWithOuterRefTest() {
		final String query = "WITH oa AS (SELECT ra.r1 FROM tab_r AS ra),"
		    + "\nob AS (WITH ib AS (SELECT sb.s1 FROM tab_s AS sb)"
		    + "\n       SELECT tb.t1 FROM tab_t AS tb"
		    + "\n       WHERE EXISTS (SELECT 1 FROM ib"
		    + "\n                     WHERE ib.s1 = oa.r1"
		    + "\n                       AND ib.s1 = tb.t2))"
		    + "\nSELECT ob.t1 FROM ob JOIN oa ON oa.r1 = ob.t1";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		assertNoFatalErrors(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={with={1={cte={select={1={column={name=r1, table_ref=ra}}}, from={table={alias=ra, table=tab_r}}}, alias=oa}, 2={cte={with={1={cte={select={1={column={name=s1, table_ref=sb}}}, from={table={alias=sb, table=tab_s}}}, alias=ib}}, query={select={1={column={name=t1, table_ref=tb}}}, from={table={alias=tb, table=tab_t}}, where={exists={select={1={literal=1}}, from={table={alias=null, table=ib}}, where={and={1={condition={left={column={name=s1, table_ref=ib}}, right={column={name=r1, table_ref=oa}}, operator==}}, 2={condition={left={column={name=s1, table_ref=ib}}, right={column={name=t2, table_ref=tb}}, operator==}}}}, operator=EXISTS}}}}, alias=ob}}, query={select={1={column={name=t1, table_ref=ob}}}, from={join={1={table={alias=null, table=ob}}, 2={join=JOIN, on={condition={left={column={name=r1, table_ref=oa}}, right={column={name=t1, table_ref=ob}}, operator==}}}, 3={table={alias=null, table=oa}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[t1]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab_r={r1=[[@5,19:20='ra',<381>,1:19]]}, tab_s={s1=[[@22,70:71='sb',<381>,2:26]]}, tab_t={t1=[[@31,108:109='tb',<381>,3:14]], t2=[[@58,245:246='tb',<381>,6:35]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query4={t1=[[@33,111:112='t1',<381>,3:17], [@64,260:261='ob',<381>,7:7], [@76,293:294='ob',<381>,7:40]]}, query5={t1=[[@66,263:264='t1',<381>,7:10]]}, query0={r1=[[@7,22:23='r1',<381>,1:22], [@50,204:205='oa',<381>,5:35], [@72,285:286='oa',<381>,7:32]]}, query1={s1=[[@24,73:74='s1',<381>,2:29], [@46,196:197='ib',<381>,5:27], [@54,237:238='ib',<381>,6:27]]}, query2={unnamed_0=[[@42,159:159='1',<300>,4:28]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query5={context_list={ob=query4}, query_dictionary={t1=[[@66,263:264='t1',<381>,7:10]]}, filters=[{name=r1, table_ref=oa}, {name=t1, table_ref=ob}], interface={t1=[{name=t1, table_ref=ob}]}, def_query4={context_list={oa=query0, ib=query1}, query_dictionary={t1=[[@33,111:112='t1',<381>,3:17], [@64,260:261='ob',<381>,7:7], [@76,293:294='ob',<381>,7:40]]}, table_dictionary={tab_t={t1=[[@31,108:109='tb',<381>,3:14]], t2=[[@58,245:246='tb',<381>,6:35]]}}, dependent_queries={exists3={query=query2, type=filters}}, def_query1={context_list={oa=query0}, query_dictionary={s1=[[@24,73:74='s1',<381>,2:29], [@46,196:197='ib',<381>,5:27], [@54,237:238='ib',<381>,6:27]]}, table_dictionary={tab_s={s1=[[@22,70:71='sb',<381>,2:26]]}}, interface={s1=[{name=s1, table_ref=sb}]}, table_alias={oa=query0, sb=tab_s}}, def_query0={query_dictionary={r1=[[@7,22:23='r1',<381>,1:22], [@50,204:205='oa',<381>,5:35], [@72,285:286='oa',<381>,7:32]]}, table_dictionary={tab_r={r1=[[@5,19:20='ra',<381>,1:19]]}}, interface={r1=[{name=r1, table_ref=ra}]}, table_alias={ra=tab_r}}, filters=[], interface={t1=[{name=t1, table_ref=tb}]}, table_alias={oa=query0, ib=query1, tb=tab_t}, def_query2={context_list={oa=query0, ib=query1}, query_dictionary={unnamed_0=[[@42,159:159='1',<300>,4:28]]}, filters=[{name=s1, table_ref=ib}, {name=r1, table_ref=oa}, {name=t2, table_ref=tb}], interface={unnamed_0=[]}, table_alias={oa=query0, ib=query1}}}, table_alias={oa=query0, ob=query4}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void explicitAliasWhereOutputRefTest() {
		final String query = "SELECT ic.pd7 AS xxx FROM tab1 ic WHERE xxx IS NOT NULL";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals(
				"Query Column Dictionary is wrong",
				"{query0={xxx=[[@5,17:19='xxx',<381>,1:17], [@10,40:42='xxx',<381>,1:40]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals(
				"Table Dictionary is wrong",
				"{tab1={pd7=[[@1,7:8='ic',<381>,1:7]]}}",
				extractor.getTableColumnDictionaryMap().toString());
	}

	@Test
	public void explicitAliasWherePhysicalRefTest() {
		final String query = "SELECT ic.pd7 AS xxx FROM tab1 ic WHERE ic.pd7 IS NOT NULL";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals(
				"Query Column Dictionary is wrong",
				"{query0={xxx=[[@5,17:19='xxx',<381>,1:17]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals(
				"Table Dictionary is wrong",
				"{tab1={pd7=[[@1,7:8='ic',<381>,1:7], [@10,40:41='ic',<381>,1:40]]}}",
				extractor.getTableColumnDictionaryMap().toString());
	}

	@Test
	public void implicitOutputWherePhysicalRefTest() {
		final String query = "SELECT ic.pd7 FROM tab_c ic WHERE ic.pd7 = 1";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals(
				"Query Column Dictionary is wrong",
				"{query0={pd7=[[@3,10:12='pd7',<381>,1:10]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals(
				"Table Dictionary is wrong",
				"{tab_c={pd7=[[@1,7:8='ic',<381>,1:7], [@8,34:35='ic',<381>,1:34]]}}",
				extractor.getTableColumnDictionaryMap().toString());
	}
}
