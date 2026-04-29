package sql.walker;
import org.junit.Assert;
import org.junit.Test;

import access.Snippet;
import errorhandling.ParseDiagnostic;
import sql.SQLSelectParserParser;

public class SqlInventorySubqueriesAndClauseSemanticsTests extends AbstractSqlParseEventWalkerTest {

	@Test
	public void nestedSubqueryWithColumnsV0() {
		final String query = " SELECT F4.col1 as last FROM "
			+   "\n  (select x as col1, y as col2 from third) F4";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=col1, table_ref=F4}, alias=last}}, from={table={alias=F4, query={select={1={column={name=x, table_ref=null}, alias=col1}, 2={column={name=y, table_ref=null}, alias=col2}}, from={table={alias=null, table=third}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[last]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{third={x=[[@9,40:40='x',<335>,2:10]], y=[[@13,51:51='y',<335>,2:21]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={col2=[[@15,56:59='col2',<335>,2:26]], col1=[[@11,45:48='col1',<335>,2:15], [@1,8:9='F4',<335>,1:8]]}, query1={last=[[@5,19:22='last',<101>,1:19]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query1={query_dictionary={last=[[@5,19:22='last',<101>,1:19]]}, table_dictionary={}, def_query0={query_dictionary={col2=[[@15,56:59='col2',<335>,2:26]], col1=[[@11,45:48='col1',<335>,2:15], [@1,8:9='F4',<335>,1:8]]}, table_dictionary={third={x=[[@9,40:40='x',<335>,2:10]], y=[[@13,51:51='y',<335>,2:21]]}}, interface={col2=[{name=y, table_ref=third}], col1=[{name=x, table_ref=third}]}}, interface={last=[{name=col1, table_ref=F4}]}, table_alias={F4=query0}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void nestedSubqueryWithWildcardV0() {
		final String query = " SELECT F4.col1 as last FROM "
			+   "\n  (select * from third) F4";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=col1, table_ref=F4}, alias=last}}, from={table={alias=F4, query={select={1={column={name=*, table_ref=*}}}, from={table={alias=null, table=third}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[last]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{third={*=[[@9,40:40='*',<289>,2:10]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@9,40:40='*',<289>,2:10]], col1=[[@1,8:9='F4',<335>,1:8]]}, query1={last=[[@5,19:22='last',<101>,1:19]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query1={query_dictionary={last=[[@5,19:22='last',<101>,1:19]]}, table_dictionary={}, def_query0={query_dictionary={*=[[@9,40:40='*',<289>,2:10]], col1=[[@1,8:9='F4',<335>,1:8]]}, table_dictionary={third={*=[[@9,40:40='*',<289>,2:10]]}}, interface={*=[{name=*, table_ref=*}]}}, interface={last=[{name=col1, table_ref=F4}]}, table_alias={F4=query0}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void nestedSelectStarV1() {
		final String query = " SELECT F4.col1 FROM "
			+	"\n (select * from " 
			+   "\n  (select * from third) as T3) F4";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=col1, table_ref=F4}}}, from={table={alias=F4, query={select={1={column={name=*, table_ref=*}}}, from={table={alias=T3, query={select={1={column={name=*, table_ref=*}}}, from={table={alias=null, table=third}}}}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[col1]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{third={*=[[@11,49:49='*',<289>,3:10]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@11,49:49='*',<289>,3:10]]}, query1={*=[[@7,31:31='*',<289>,2:9]], col1=[[@1,8:9='F4',<335>,1:8]]}, query2={col1=[[@3,11:14='col1',<335>,1:11]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query2={query_dictionary={col1=[[@3,11:14='col1',<335>,1:11]]}, table_dictionary={}, def_query1={query_dictionary={*=[[@7,31:31='*',<289>,2:9]], col1=[[@1,8:9='F4',<335>,1:8]]}, table_dictionary={}, def_query0={query_dictionary={*=[[@11,49:49='*',<289>,3:10]]}, table_dictionary={third={*=[[@11,49:49='*',<289>,3:10]]}}, interface={*=[{name=*, table_ref=*}]}}, interface={*=[{name=*, table_ref=*}]}, table_alias={T3=query0}}, interface={col1=[{name=col1, table_ref=F4}]}, table_alias={F4=query1}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void nestedSelectStarV2() {
		final String query = " SELECT col1 FROM "
			+	"\n (select * from " 
			+   "\n  (select * from third) as T3) F4";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=col1, table_ref=null}}}, from={table={alias=F4, query={select={1={column={name=*, table_ref=*}}}, from={table={alias=T3, query={select={1={column={name=*, table_ref=*}}}, from={table={alias=null, table=third}}}}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[col1]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{third={*=[[@9,46:46='*',<289>,3:10]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@9,46:46='*',<289>,3:10]]}, query1={*=[[@5,28:28='*',<289>,2:9]]}, query2={col1=[[@1,8:11='col1',<335>,1:8]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query2={query_dictionary={col1=[[@1,8:11='col1',<335>,1:8]]}, table_dictionary={}, def_query1={query_dictionary={*=[[@5,28:28='*',<289>,2:9]]}, table_dictionary={}, def_query0={query_dictionary={*=[[@9,46:46='*',<289>,3:10]]}, table_dictionary={third={*=[[@9,46:46='*',<289>,3:10]]}}, interface={*=[{name=*, table_ref=*}]}}, interface={*=[{name=*, table_ref=*}]}, table_alias={T3=query0}}, interface={col1=[{name=col1, table_ref=query1}]}, table_alias={F4=query1}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void nestedSelectStarV3() {
		final String query = " SELECT col1 FROM "
			+	"\n (select * from " 
			+   "\n  (select col1, col2 from third) as T3 where T3.col3 is null) F4";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		//assertNoFatalErrors(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=col1, table_ref=null}}}, from={table={alias=F4, query={select={1={column={name=*, table_ref=*}}}, from={table={alias=T3, query={select={1={column={name=col1, table_ref=null}}, 2={column={name=col2, table_ref=null}}}, from={table={alias=null, table=third}}}}}, where={condition={left={column={name=col3, table_ref=T3}}, operator=is null}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[col1]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{third={col2=[[@11,52:55='col2',<335>,3:16]], col1=[[@9,46:49='col1',<335>,3:10]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@5,28:28='*',<289>,2:9]], col2=[[@11,52:55='col2',<335>,3:16]], col1=[[@9,46:49='col1',<335>,3:10]]}, query1={*=[[@5,28:28='*',<289>,2:9]]}, query2={col1=[[@1,8:11='col1',<335>,1:8]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertTrue("Symbol Table should contain query alias mapping T3=query0",
				extractor.getSymbolTable().toString().contains("T3=query0"));
		Assert.assertTrue("Symbol Table should keep query1 filter with qualified reference",
				extractor.getSymbolTable().toString().contains("filters=[{name=col3, table_ref=T3}]"));

		Snippet snippet = extractor.getSnippet();
		assertFatalDiagnosticAtPosition(snippet, "QUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIAS",
				"Qualified column 'col3'",
				"T3.col3", 3, 45);
		assertFatalDiagnosticCount(snippet,
				"QUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIAS",
				"Qualified column 'col3'",
				"T3.col3",
				1);
			}


	@Test
	public void nestedSelectStarV4() {
		final String query = " SELECT F4.col1 FROM "
			+	"\n (select * from " 
			+   "\n  (select col1, col2 from third) as T3) F4";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=col1, table_ref=F4}}}, from={table={alias=F4, query={select={1={column={name=*, table_ref=*}}}, from={table={alias=T3, query={select={1={column={name=col1, table_ref=null}}, 2={column={name=col2, table_ref=null}}}, from={table={alias=null, table=third}}}}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[col1]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{third={col2=[[@13,55:58='col2',<335>,3:16]], col1=[[@11,49:52='col1',<335>,3:10]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@7,31:31='*',<289>,2:9]], col2=[[@13,55:58='col2',<335>,3:16]], col1=[[@11,49:52='col1',<335>,3:10]]}, query1={*=[[@7,31:31='*',<289>,2:9]], col1=[[@1,8:9='F4',<335>,1:8]]}, query2={col1=[[@3,11:14='col1',<335>,1:11]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query2={query_dictionary={col1=[[@3,11:14='col1',<335>,1:11]]}, table_dictionary={}, def_query1={query_dictionary={*=[[@7,31:31='*',<289>,2:9]], col1=[[@1,8:9='F4',<335>,1:8]]}, table_dictionary={}, def_query0={query_dictionary={*=[[@7,31:31='*',<289>,2:9]], col2=[[@13,55:58='col2',<335>,3:16]], col1=[[@11,49:52='col1',<335>,3:10]]}, table_dictionary={third={col2=[[@13,55:58='col2',<335>,3:16]], col1=[[@11,49:52='col1',<335>,3:10]]}}, interface={col2=[{name=col2, table_ref=third}], col1=[{name=col1, table_ref=third}]}}, interface={*=[{name=*, table_ref=*}]}, table_alias={T3=query0}}, interface={col1=[{name=col1, table_ref=F4}]}, table_alias={F4=query1}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void nestedSelectStarV5() {
		final String query = " SELECT T3.col1 FROM "
			+	"\n (select * from " 
			+   "\n  (select * from third) as T3) F4";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=col1, table_ref=T3}}}, from={table={alias=F4, query={select={1={column={name=*, table_ref=*}}}, from={table={alias=T3, query={select={1={column={name=*, table_ref=*}}}, from={table={alias=null, table=third}}}}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[col1]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{third={*=[[@11,49:49='*',<289>,3:10]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@11,49:49='*',<289>,3:10]]}, query1={*=[[@7,31:31='*',<289>,2:9]]}, query2={col1=[[@3,11:14='col1',<335>,1:11]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query2={query_dictionary={col1=[[@3,11:14='col1',<335>,1:11]]}, table_dictionary={}, def_query1={query_dictionary={*=[[@7,31:31='*',<289>,2:9]]}, table_dictionary={}, def_query0={query_dictionary={*=[[@11,49:49='*',<289>,3:10]]}, table_dictionary={third={*=[[@11,49:49='*',<289>,3:10]]}}, interface={*=[{name=*, table_ref=*}]}}, interface={*=[{name=*, table_ref=*}]}, table_alias={T3=query0}}, interface={col1=[{name=col1, table_ref=T3}]}, table_alias={F4=query1}}}",
				extractor.getSymbolTable().toString());

		Snippet snippet = extractor.getSnippet();
		assertFatalDiagnosticAtPosition(snippet, "QUALIFIED_COLUMN_NOT_FOUND_IN_TABLE",
				"Source Table not found for Column 'col1' at (l:1 c:11). No alias or table called 'T3'.",
				"col1", 1, 11);
		assertFatalDiagnosticCount(snippet, "QUALIFIED_COLUMN_NOT_FOUND_IN_TABLE", "Source Table not found for Column 'col1'", "col1", 1);
	}


	@Test
	public void nestedSelectStarV6() {
		final String query = " SELECT T3.col1 FROM "
			+	"\n (select * from " 
			+   "\n  (select col1, col2 from third) as T3) F4";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=col1, table_ref=T3}}}, from={table={alias=F4, query={select={1={column={name=*, table_ref=*}}}, from={table={alias=T3, query={select={1={column={name=col1, table_ref=null}}, 2={column={name=col2, table_ref=null}}}, from={table={alias=null, table=third}}}}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[col1]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{third={col2=[[@13,55:58='col2',<335>,3:16]], col1=[[@11,49:52='col1',<335>,3:10]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@7,31:31='*',<289>,2:9]], col2=[[@13,55:58='col2',<335>,3:16]], col1=[[@11,49:52='col1',<335>,3:10]]}, query1={*=[[@7,31:31='*',<289>,2:9]]}, query2={col1=[[@3,11:14='col1',<335>,1:11]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query2={query_dictionary={col1=[[@3,11:14='col1',<335>,1:11]]}, table_dictionary={}, def_query1={query_dictionary={*=[[@7,31:31='*',<289>,2:9]]}, table_dictionary={}, def_query0={query_dictionary={*=[[@7,31:31='*',<289>,2:9]], col2=[[@13,55:58='col2',<335>,3:16]], col1=[[@11,49:52='col1',<335>,3:10]]}, table_dictionary={third={col2=[[@13,55:58='col2',<335>,3:16]], col1=[[@11,49:52='col1',<335>,3:10]]}}, interface={col2=[{name=col2, table_ref=third}], col1=[{name=col1, table_ref=third}]}}, interface={*=[{name=*, table_ref=*}]}, table_alias={T3=query0}}, interface={col1=[{name=col1, table_ref=T3}]}, table_alias={F4=query1}}}",
				extractor.getSymbolTable().toString());

		Snippet snippet = extractor.getSnippet();
		assertFatalDiagnosticAtPosition(snippet, "QUALIFIED_COLUMN_NOT_FOUND_IN_TABLE",
				"Source Table not found for Column 'col1' at (l:1 c:11). No alias or table called 'T3'.",
				"col1", 1, 11);
		assertFatalDiagnosticCount(snippet, "QUALIFIED_COLUMN_NOT_FOUND_IN_TABLE", "Source Table not found for Column 'col1'", "col1", 1);
	}


	@Test
	public void nestedSelectStarV7() {
		final String query = " SELECT * FROM "
			+	"\n (select T3.col1 from " 
			+   "\n  (select * from third) as T3) F4";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=*, table_ref=*}}}, from={table={alias=F4, query={select={1={column={name=col1, table_ref=T3}}}, from={table={alias=T3, query={select={1={column={name=*, table_ref=*}}}, from={table={alias=null, table=third}}}}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{third={*=[[@11,49:49='*',<289>,3:10]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@11,49:49='*',<289>,3:10]], col1=[[@5,25:26='T3',<335>,2:9]]}, query1={*=[[@1,8:8='*',<289>,1:8]], col1=[[@7,28:31='col1',<335>,2:12]]}, query2={*=[[@1,8:8='*',<289>,1:8]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query2={query_dictionary={*=[[@1,8:8='*',<289>,1:8]]}, table_dictionary={}, def_query1={query_dictionary={*=[[@1,8:8='*',<289>,1:8]], col1=[[@7,28:31='col1',<335>,2:12]]}, table_dictionary={}, def_query0={query_dictionary={*=[[@11,49:49='*',<289>,3:10]], col1=[[@5,25:26='T3',<335>,2:9]]}, table_dictionary={third={*=[[@11,49:49='*',<289>,3:10]]}}, interface={*=[{name=*, table_ref=*}]}}, interface={col1=[{name=col1, table_ref=T3}]}, table_alias={T3=query0}}, interface={*=[{name=*, table_ref=*}]}, table_alias={F4=query1}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void subqueryDictionaryExtensionJoinClauseV1() {
		final String query = " SELECT F4.col1 as last, t.col2 as tcol2, t.col3 FROM "
			+ "\n  (select * from third) F4"
			+ "\n  join fourth t on F4.col1 = t.col2 and F4.col4 = t.col5";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=col1, table_ref=F4}, alias=last}, 2={column={name=col2, table_ref=t}, alias=tcol2}, 3={column={name=col3, table_ref=t}}}, from={join={1={table={alias=F4, query={select={1={column={name=*, table_ref=*}}}, from={table={alias=null, table=third}}}}}, 2={join=join, on={and={1={condition={left={column={name=col1, table_ref=F4}}, right={column={name=col2, table_ref=t}}, operator==}}, 2={condition={left={column={name=col4, table_ref=F4}}, right={column={name=col5, table_ref=t}}, operator==}}}}}, 3={table={alias=t, table=fourth}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[last, tcol2, col3]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{third={*=[[@19,65:65='*',<289>,2:10]]}, fourth={col5=[[@40,132:132='t',<335>,3:50]], col2=[[@7,25:25='t',<335>,1:25], [@32,111:111='t',<335>,3:29]], col3=[[@13,42:42='t',<335>,1:42]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@19,65:65='*',<289>,2:10]], col4=[[@36,122:123='F4',<335>,3:40]], col1=[[@1,8:9='F4',<335>,1:8], [@28,101:102='F4',<335>,3:19]]}, query1={last=[[@5,19:22='last',<101>,1:19]], tcol2=[[@11,35:39='tcol2',<335>,1:35]], col3=[[@15,44:47='col3',<335>,1:44]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query1={query_dictionary={last=[[@5,19:22='last',<101>,1:19]], tcol2=[[@11,35:39='tcol2',<335>,1:35]], col3=[[@15,44:47='col3',<335>,1:44]]}, table_dictionary={fourth={col5=[[@40,132:132='t',<335>,3:50]], col2=[[@7,25:25='t',<335>,1:25], [@32,111:111='t',<335>,3:29]], col3=[[@13,42:42='t',<335>,1:42]]}}, def_query0={query_dictionary={*=[[@19,65:65='*',<289>,2:10]], col4=[[@36,122:123='F4',<335>,3:40]], col1=[[@1,8:9='F4',<335>,1:8], [@28,101:102='F4',<335>,3:19]]}, table_dictionary={third={*=[[@19,65:65='*',<289>,2:10]]}}, interface={*=[{name=*, table_ref=*}]}}, filters=[{name=col1, table_ref=F4}, {name=col2, table_ref=t}, {name=col4, table_ref=F4}, {name=col5, table_ref=t}], interface={last=[{name=col1, table_ref=F4}], tcol2=[{name=col2, table_ref=t}], col3=[{name=col3, table_ref=t}]}, table_alias={t=fourth, F4=query0}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void subqueryDictionaryExtensionWhereClauseV2() {
		final String query = " SELECT F4.col1 as last, t.col2 as tcol2, t.col3 FROM "
			+ "\n  (select * from third) F4"
			+ "\n  join fourth t on F4.col1 = t.col2 and F4.col4 = t.col5"
			+ "\n where F4.col6 = t.col7 and F4.col1 = t.col2 and F4.col8 > 0";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=col1, table_ref=F4}, alias=last}, 2={column={name=col2, table_ref=t}, alias=tcol2}, 3={column={name=col3, table_ref=t}}}, from={join={1={table={alias=F4, query={select={1={column={name=*, table_ref=*}}}, from={table={alias=null, table=third}}}}}, 2={join=join, on={and={1={condition={left={column={name=col1, table_ref=F4}}, right={column={name=col2, table_ref=t}}, operator==}}, 2={condition={left={column={name=col4, table_ref=F4}}, right={column={name=col5, table_ref=t}}, operator==}}}}}, 3={table={alias=t, table=fourth}}}}, where={and={1={condition={left={column={name=col6, table_ref=F4}}, right={column={name=col7, table_ref=t}}, operator==}}, 2={condition={left={column={name=col1, table_ref=F4}}, right={column={name=col2, table_ref=t}}, operator==}}, 3={condition={left={column={name=col8, table_ref=F4}}, right={literal=0}, operator=>}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[last, tcol2, col3]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{third={*=[[@19,65:65='*',<289>,2:10]]}, fourth={col7=[[@48,156:156='t',<335>,4:17]], col5=[[@40,132:132='t',<335>,3:50]], col2=[[@7,25:25='t',<335>,1:25], [@32,111:111='t',<335>,3:29], [@56,177:177='t',<335>,4:38]], col3=[[@13,42:42='t',<335>,1:42]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={col8=[[@60,188:189='F4',<335>,4:49]], col6=[[@44,146:147='F4',<335>,4:7]], *=[[@19,65:65='*',<289>,2:10]], col4=[[@36,122:123='F4',<335>,3:40]], col1=[[@1,8:9='F4',<335>,1:8], [@28,101:102='F4',<335>,3:19], [@52,167:168='F4',<335>,4:28]]}, query1={last=[[@5,19:22='last',<101>,1:19]], tcol2=[[@11,35:39='tcol2',<335>,1:35]], col3=[[@15,44:47='col3',<335>,1:44]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query1={query_dictionary={last=[[@5,19:22='last',<101>,1:19]], tcol2=[[@11,35:39='tcol2',<335>,1:35]], col3=[[@15,44:47='col3',<335>,1:44]]}, table_dictionary={fourth={col7=[[@48,156:156='t',<335>,4:17]], col5=[[@40,132:132='t',<335>,3:50]], col2=[[@7,25:25='t',<335>,1:25], [@32,111:111='t',<335>,3:29], [@56,177:177='t',<335>,4:38]], col3=[[@13,42:42='t',<335>,1:42]]}}, def_query0={query_dictionary={col8=[[@60,188:189='F4',<335>,4:49]], col6=[[@44,146:147='F4',<335>,4:7]], *=[[@19,65:65='*',<289>,2:10]], col4=[[@36,122:123='F4',<335>,3:40]], col1=[[@1,8:9='F4',<335>,1:8], [@28,101:102='F4',<335>,3:19], [@52,167:168='F4',<335>,4:28]]}, table_dictionary={third={*=[[@19,65:65='*',<289>,2:10]]}}, interface={*=[{name=*, table_ref=*}]}}, filters=[{name=col1, table_ref=F4}, {name=col2, table_ref=t}, {name=col4, table_ref=F4}, {name=col5, table_ref=t}, {name=col6, table_ref=F4}, {name=col7, table_ref=t}, {name=col8, table_ref=F4}], interface={last=[{name=col1, table_ref=F4}], tcol2=[{name=col2, table_ref=t}], col3=[{name=col3, table_ref=t}]}, table_alias={t=fourth, F4=query0}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void subqueryDictionaryExtensionHavingClauseV3() {
		final String query = " SELECT F4.col1 as last, t.col2 as tcol2, t.col3 FROM "
			+ "\n  (select * from third) F4"
			+ "\n  join fourth t on F4.col1 = t.col2 and F4.col4 = t.col5"
			+ "\n having F4.col6 = t.col7 and F4.col1 = t.col2 and F4.col8 > 0";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=col1, table_ref=F4}, alias=last}, 2={column={name=col2, table_ref=t}, alias=tcol2}, 3={column={name=col3, table_ref=t}}}, having={and={1={condition={left={column={name=col6, table_ref=F4}}, right={column={name=col7, table_ref=t}}, operator==}}, 2={condition={left={column={name=col1, table_ref=F4}}, right={column={name=col2, table_ref=t}}, operator==}}, 3={condition={left={column={name=col8, table_ref=F4}}, right={literal=0}, operator=>}}}}, from={join={1={table={alias=F4, query={select={1={column={name=*, table_ref=*}}}, from={table={alias=null, table=third}}}}}, 2={join=join, on={and={1={condition={left={column={name=col1, table_ref=F4}}, right={column={name=col2, table_ref=t}}, operator==}}, 2={condition={left={column={name=col4, table_ref=F4}}, right={column={name=col5, table_ref=t}}, operator==}}}}}, 3={table={alias=t, table=fourth}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[last, tcol2, col3]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{third={*=[[@19,65:65='*',<289>,2:10]]}, fourth={col7=[[@48,157:157='t',<335>,4:18]], col5=[[@40,132:132='t',<335>,3:50]], col2=[[@7,25:25='t',<335>,1:25], [@32,111:111='t',<335>,3:29], [@56,178:178='t',<335>,4:39]], col3=[[@13,42:42='t',<335>,1:42]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={col8=[[@60,189:190='F4',<335>,4:50]], col6=[[@44,147:148='F4',<335>,4:8]], *=[[@19,65:65='*',<289>,2:10]], col4=[[@36,122:123='F4',<335>,3:40]], col1=[[@1,8:9='F4',<335>,1:8], [@28,101:102='F4',<335>,3:19], [@52,168:169='F4',<335>,4:29]]}, query1={last=[[@5,19:22='last',<101>,1:19]], tcol2=[[@11,35:39='tcol2',<335>,1:35]], col3=[[@15,44:47='col3',<335>,1:44]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query1={query_dictionary={last=[[@5,19:22='last',<101>,1:19]], tcol2=[[@11,35:39='tcol2',<335>,1:35]], col3=[[@15,44:47='col3',<335>,1:44]]}, table_dictionary={fourth={col7=[[@48,157:157='t',<335>,4:18]], col5=[[@40,132:132='t',<335>,3:50]], col2=[[@7,25:25='t',<335>,1:25], [@32,111:111='t',<335>,3:29], [@56,178:178='t',<335>,4:39]], col3=[[@13,42:42='t',<335>,1:42]]}}, def_query0={query_dictionary={col8=[[@60,189:190='F4',<335>,4:50]], col6=[[@44,147:148='F4',<335>,4:8]], *=[[@19,65:65='*',<289>,2:10]], col4=[[@36,122:123='F4',<335>,3:40]], col1=[[@1,8:9='F4',<335>,1:8], [@28,101:102='F4',<335>,3:19], [@52,168:169='F4',<335>,4:29]]}, table_dictionary={third={*=[[@19,65:65='*',<289>,2:10]]}}, interface={*=[{name=*, table_ref=*}]}}, filters=[{name=col1, table_ref=F4}, {name=col2, table_ref=t}, {name=col4, table_ref=F4}, {name=col5, table_ref=t}, {name=col6, table_ref=F4}, {name=col7, table_ref=t}, {name=col8, table_ref=F4}], interface={last=[{name=col1, table_ref=F4}], tcol2=[{name=col2, table_ref=t}], col3=[{name=col3, table_ref=t}]}, table_alias={t=fourth, F4=query0}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void subqueryDictionaryExtensionQualifyClauseV4() {
		final String query = " SELECT F4.col1 as last, t.col2 as tcol2, t.col3 FROM "
			+ "\n  (select * from third) F4"
			+ "\n  join fourth t on F4.col1 = t.col2 and F4.col4 = t.col5"
			+ "\n where F4.col6 = t.col7 and F4.col1 = t.col2 and F4.col8 > 0"
			+ "\n qualify F4.col9 = t.col10 and F4.col8 > 1";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=col1, table_ref=F4}, alias=last}, 2={column={name=col2, table_ref=t}, alias=tcol2}, 3={column={name=col3, table_ref=t}}}, from={join={1={table={alias=F4, query={select={1={column={name=*, table_ref=*}}}, from={table={alias=null, table=third}}}}}, 2={join=join, on={and={1={condition={left={column={name=col1, table_ref=F4}}, right={column={name=col2, table_ref=t}}, operator==}}, 2={condition={left={column={name=col4, table_ref=F4}}, right={column={name=col5, table_ref=t}}, operator==}}}}}, 3={table={alias=t, table=fourth}}}}, where={and={1={condition={left={column={name=col6, table_ref=F4}}, right={column={name=col7, table_ref=t}}, operator==}}, 2={condition={left={column={name=col1, table_ref=F4}}, right={column={name=col2, table_ref=t}}, operator==}}, 3={condition={left={column={name=col8, table_ref=F4}}, right={literal=0}, operator=>}}}}, qualify={and={1={condition={left={column={name=col9, table_ref=F4}}, right={column={name=col10, table_ref=t}}, operator==}}, 2={condition={left={column={name=col8, table_ref=F4}}, right={literal=1}, operator=>}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[last, tcol2, col3]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{third={*=[[@19,65:65='*',<289>,2:10]]}, fourth={col10=[[@70,219:219='t',<335>,5:19]], col7=[[@48,156:156='t',<335>,4:17]], col5=[[@40,132:132='t',<335>,3:50]], col2=[[@7,25:25='t',<335>,1:25], [@32,111:111='t',<335>,3:29], [@56,177:177='t',<335>,4:38]], col3=[[@13,42:42='t',<335>,1:42]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={col8=[[@60,188:189='F4',<335>,4:49], [@74,231:232='F4',<335>,5:31]], col9=[[@66,209:210='F4',<335>,5:9]], col6=[[@44,146:147='F4',<335>,4:7]], *=[[@19,65:65='*',<289>,2:10]], col4=[[@36,122:123='F4',<335>,3:40]], col1=[[@1,8:9='F4',<335>,1:8], [@28,101:102='F4',<335>,3:19], [@52,167:168='F4',<335>,4:28]]}, query1={last=[[@5,19:22='last',<101>,1:19]], tcol2=[[@11,35:39='tcol2',<335>,1:35]], col3=[[@15,44:47='col3',<335>,1:44]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query1={query_dictionary={last=[[@5,19:22='last',<101>,1:19]], tcol2=[[@11,35:39='tcol2',<335>,1:35]], col3=[[@15,44:47='col3',<335>,1:44]]}, table_dictionary={fourth={col10=[[@70,219:219='t',<335>,5:19]], col7=[[@48,156:156='t',<335>,4:17]], col5=[[@40,132:132='t',<335>,3:50]], col2=[[@7,25:25='t',<335>,1:25], [@32,111:111='t',<335>,3:29], [@56,177:177='t',<335>,4:38]], col3=[[@13,42:42='t',<335>,1:42]]}}, def_query0={query_dictionary={col8=[[@60,188:189='F4',<335>,4:49], [@74,231:232='F4',<335>,5:31]], col9=[[@66,209:210='F4',<335>,5:9]], col6=[[@44,146:147='F4',<335>,4:7]], *=[[@19,65:65='*',<289>,2:10]], col4=[[@36,122:123='F4',<335>,3:40]], col1=[[@1,8:9='F4',<335>,1:8], [@28,101:102='F4',<335>,3:19], [@52,167:168='F4',<335>,4:28]]}, table_dictionary={third={*=[[@19,65:65='*',<289>,2:10]]}}, interface={*=[{name=*, table_ref=*}]}}, filters=[{name=col1, table_ref=F4}, {name=col2, table_ref=t}, {name=col4, table_ref=F4}, {name=col5, table_ref=t}, {name=col6, table_ref=F4}, {name=col7, table_ref=t}, {name=col8, table_ref=F4}, {name=col9, table_ref=F4}, {name=col10, table_ref=t}], interface={last=[{name=col1, table_ref=F4}], tcol2=[{name=col2, table_ref=t}], col3=[{name=col3, table_ref=t}]}, table_alias={t=fourth, F4=query0}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void subqueryDictionaryExtensionAggregateGroupByV5() {
		final String query = " SELECT F4.col1 as last, t.col2 as tcol2, t.col3, sum(F4.col11) as total_col11 FROM "
			+ "\n  (select * from third) F4"
			+ "\n  join fourth t on F4.col1 = t.col2 and F4.col4 = t.col5"
			+ "\n where F4.col6 = t.col7 and F4.col1 = t.col2 and F4.col8 > 0"
			+ "\n group by F4.col1, t.col2, t.col3";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=col1, table_ref=F4}, alias=last}, 2={column={name=col2, table_ref=t}, alias=tcol2}, 3={column={name=col3, table_ref=t}}, 4={function={function_name=sum, qualifier=null, parameters={column={name=col11, table_ref=F4}}}, alias=total_col11}}, from={join={1={table={alias=F4, query={select={1={column={name=*, table_ref=*}}}, from={table={alias=null, table=third}}}}}, 2={join=join, on={and={1={condition={left={column={name=col1, table_ref=F4}}, right={column={name=col2, table_ref=t}}, operator==}}, 2={condition={left={column={name=col4, table_ref=F4}}, right={column={name=col5, table_ref=t}}, operator==}}}}}, 3={table={alias=t, table=fourth}}}}, where={and={1={condition={left={column={name=col6, table_ref=F4}}, right={column={name=col7, table_ref=t}}, operator==}}, 2={condition={left={column={name=col1, table_ref=F4}}, right={column={name=col2, table_ref=t}}, operator==}}, 3={condition={left={column={name=col8, table_ref=F4}}, right={literal=0}, operator=>}}}}, groupby={1={column={name=col1, table_ref=F4}}, 2={column={name=col2, table_ref=t}}, 3={column={name=col3, table_ref=t}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[last, total_col11, tcol2, col3]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{third={*=[[@28,95:95='*',<289>,2:10]]}, fourth={col7=[[@57,186:186='t',<335>,4:17]], col5=[[@49,162:162='t',<335>,3:50]], col2=[[@7,25:25='t',<335>,1:25], [@41,141:141='t',<335>,3:29], [@65,207:207='t',<335>,4:38], [@80,249:249='t',<335>,5:19]], col3=[[@13,42:42='t',<335>,1:42], [@84,257:257='t',<335>,5:27]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={col8=[[@69,218:219='F4',<335>,4:49]], col11=[[@19,54:55='F4',<335>,1:54]], col6=[[@53,176:177='F4',<335>,4:7]], *=[[@28,95:95='*',<289>,2:10]], col4=[[@45,152:153='F4',<335>,3:40]], col1=[[@1,8:9='F4',<335>,1:8], [@37,131:132='F4',<335>,3:19], [@61,197:198='F4',<335>,4:28], [@76,240:241='F4',<335>,5:10]]}, query1={last=[[@5,19:22='last',<101>,1:19]], total_col11=[[@24,67:77='total_col11',<335>,1:67]], tcol2=[[@11,35:39='tcol2',<335>,1:35]], col3=[[@15,44:47='col3',<335>,1:44]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query1={query_dictionary={last=[[@5,19:22='last',<101>,1:19]], total_col11=[[@24,67:77='total_col11',<335>,1:67]], tcol2=[[@11,35:39='tcol2',<335>,1:35]], col3=[[@15,44:47='col3',<335>,1:44]]}, table_dictionary={fourth={col7=[[@57,186:186='t',<335>,4:17]], col5=[[@49,162:162='t',<335>,3:50]], col2=[[@7,25:25='t',<335>,1:25], [@41,141:141='t',<335>,3:29], [@65,207:207='t',<335>,4:38], [@80,249:249='t',<335>,5:19]], col3=[[@13,42:42='t',<335>,1:42], [@84,257:257='t',<335>,5:27]]}}, grouped_by=[{name=col1, table_ref=F4}, {name=col2, table_ref=t}, {name=col3, table_ref=t}], def_query0={query_dictionary={col8=[[@69,218:219='F4',<335>,4:49]], col11=[[@19,54:55='F4',<335>,1:54]], col6=[[@53,176:177='F4',<335>,4:7]], *=[[@28,95:95='*',<289>,2:10]], col4=[[@45,152:153='F4',<335>,3:40]], col1=[[@1,8:9='F4',<335>,1:8], [@37,131:132='F4',<335>,3:19], [@61,197:198='F4',<335>,4:28], [@76,240:241='F4',<335>,5:10]]}, table_dictionary={third={*=[[@28,95:95='*',<289>,2:10]]}}, interface={*=[{name=*, table_ref=*}]}}, filters=[{name=col1, table_ref=F4}, {name=col2, table_ref=t}, {name=col4, table_ref=F4}, {name=col5, table_ref=t}, {name=col6, table_ref=F4}, {name=col7, table_ref=t}, {name=col8, table_ref=F4}], interface={last=[{name=col1, table_ref=F4}], total_col11=[{name=col11, table_ref=F4}], tcol2=[{name=col2, table_ref=t}], col3=[{name=col3, table_ref=t}]}, table_alias={t=fourth, F4=query0}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void subqueryDictionaryExtensionOrderByV6() {
		final String query = " SELECT F4.col1 as last, t.col2 as tcol2, t.col3 FROM "
			+ "\n  (select * from third) F4"
			+ "\n  join fourth t on F4.col1 = t.col2 and F4.col4 = t.col5"
			+ "\n where F4.col6 = t.col7 and F4.col1 = t.col2 and F4.col8 > 0"
			+ "\n order by t.col3 desc, F4.col1 asc, F4.col12 desc";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=col1, table_ref=F4}, alias=last}, 2={column={name=col2, table_ref=t}, alias=tcol2}, 3={column={name=col3, table_ref=t}}}, orderby={1={null_order=null, predicand={column={name=col3, table_ref=t}}, sort_order=desc}, 2={null_order=null, predicand={column={name=col1, table_ref=F4}}, sort_order=asc}, 3={null_order=null, predicand={column={name=col12, table_ref=F4}}, sort_order=desc}}, from={join={1={table={alias=F4, query={select={1={column={name=*, table_ref=*}}}, from={table={alias=null, table=third}}}}}, 2={join=join, on={and={1={condition={left={column={name=col1, table_ref=F4}}, right={column={name=col2, table_ref=t}}, operator==}}, 2={condition={left={column={name=col4, table_ref=F4}}, right={column={name=col5, table_ref=t}}, operator==}}}}}, 3={table={alias=t, table=fourth}}}}, where={and={1={condition={left={column={name=col6, table_ref=F4}}, right={column={name=col7, table_ref=t}}, operator==}}, 2={condition={left={column={name=col1, table_ref=F4}}, right={column={name=col2, table_ref=t}}, operator==}}, 3={condition={left={column={name=col8, table_ref=F4}}, right={literal=0}, operator=>}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[last, tcol2, col3]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{third={*=[[@19,65:65='*',<289>,2:10]]}, fourth={col7=[[@48,156:156='t',<335>,4:17]], col5=[[@40,132:132='t',<335>,3:50]], col2=[[@7,25:25='t',<335>,1:25], [@32,111:111='t',<335>,3:29], [@56,177:177='t',<335>,4:38]], col3=[[@13,42:42='t',<335>,1:42], [@67,210:210='t',<335>,5:10]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={col12=[[@77,236:237='F4',<335>,5:36]], col8=[[@60,188:189='F4',<335>,4:49]], col6=[[@44,146:147='F4',<335>,4:7]], *=[[@19,65:65='*',<289>,2:10]], col4=[[@36,122:123='F4',<335>,3:40]], col1=[[@1,8:9='F4',<335>,1:8], [@28,101:102='F4',<335>,3:19], [@52,167:168='F4',<335>,4:28], [@72,223:224='F4',<335>,5:23]]}, query1={last=[[@5,19:22='last',<101>,1:19]], tcol2=[[@11,35:39='tcol2',<335>,1:35]], col3=[[@15,44:47='col3',<335>,1:44]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query1={query_dictionary={last=[[@5,19:22='last',<101>,1:19]], tcol2=[[@11,35:39='tcol2',<335>,1:35]], col3=[[@15,44:47='col3',<335>,1:44]]}, table_dictionary={fourth={col7=[[@48,156:156='t',<335>,4:17]], col5=[[@40,132:132='t',<335>,3:50]], col2=[[@7,25:25='t',<335>,1:25], [@32,111:111='t',<335>,3:29], [@56,177:177='t',<335>,4:38]], col3=[[@13,42:42='t',<335>,1:42], [@67,210:210='t',<335>,5:10]]}}, def_query0={query_dictionary={col12=[[@77,236:237='F4',<335>,5:36]], col8=[[@60,188:189='F4',<335>,4:49]], col6=[[@44,146:147='F4',<335>,4:7]], *=[[@19,65:65='*',<289>,2:10]], col4=[[@36,122:123='F4',<335>,3:40]], col1=[[@1,8:9='F4',<335>,1:8], [@28,101:102='F4',<335>,3:19], [@52,167:168='F4',<335>,4:28], [@72,223:224='F4',<335>,5:23]]}, table_dictionary={third={*=[[@19,65:65='*',<289>,2:10]]}}, interface={*=[{name=*, table_ref=*}]}}, ordered_by=[{name=col3, table_ref=t}, {name=col1, table_ref=F4}, {name=col12, table_ref=F4}], filters=[{name=col1, table_ref=F4}, {name=col2, table_ref=t}, {name=col4, table_ref=F4}, {name=col5, table_ref=t}, {name=col6, table_ref=F4}, {name=col7, table_ref=t}, {name=col8, table_ref=F4}], interface={last=[{name=col1, table_ref=F4}], tcol2=[{name=col2, table_ref=t}], col3=[{name=col3, table_ref=t}]}, table_alias={t=fourth, F4=query0}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void subqueryDictionaryExtensionJoinClauseV11() {
		final String query = " SELECT F4.col1 as last, t.col2 as tcol2, t.col3 FROM "
			+ "\n  (select a as col1, col4 from third) F4"
			+ "\n  join fourth t on F4.col1 = t.col2 and F4.col4 = t.col5";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=col1, table_ref=F4}, alias=last}, 2={column={name=col2, table_ref=t}, alias=tcol2}, 3={column={name=col3, table_ref=t}}}, from={join={1={table={alias=F4, query={select={1={column={name=a, table_ref=null}, alias=col1}, 2={column={name=col4, table_ref=null}}}, from={table={alias=null, table=third}}}}}, 2={join=join, on={and={1={condition={left={column={name=col1, table_ref=F4}}, right={column={name=col2, table_ref=t}}, operator==}}, 2={condition={left={column={name=col4, table_ref=F4}}, right={column={name=col5, table_ref=t}}, operator==}}}}}, 3={table={alias=t, table=fourth}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[last, tcol2, col3]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{third={a=[[@19,65:65='a',<335>,2:10]], col4=[[@23,76:79='col4',<335>,2:21]]}, fourth={col5=[[@44,146:146='t',<335>,3:50]], col2=[[@7,25:25='t',<335>,1:25], [@36,125:125='t',<335>,3:29]], col3=[[@13,42:42='t',<335>,1:42]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={col4=[[@23,76:79='col4',<335>,2:21], [@40,136:137='F4',<335>,3:40]], col1=[[@21,70:73='col1',<335>,2:15], [@1,8:9='F4',<335>,1:8], [@32,115:116='F4',<335>,3:19]]}, query1={last=[[@5,19:22='last',<101>,1:19]], tcol2=[[@11,35:39='tcol2',<335>,1:35]], col3=[[@15,44:47='col3',<335>,1:44]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query1={query_dictionary={last=[[@5,19:22='last',<101>,1:19]], tcol2=[[@11,35:39='tcol2',<335>,1:35]], col3=[[@15,44:47='col3',<335>,1:44]]}, table_dictionary={fourth={col5=[[@44,146:146='t',<335>,3:50]], col2=[[@7,25:25='t',<335>,1:25], [@36,125:125='t',<335>,3:29]], col3=[[@13,42:42='t',<335>,1:42]]}}, def_query0={query_dictionary={col4=[[@23,76:79='col4',<335>,2:21], [@40,136:137='F4',<335>,3:40]], col1=[[@21,70:73='col1',<335>,2:15], [@1,8:9='F4',<335>,1:8], [@32,115:116='F4',<335>,3:19]]}, table_dictionary={third={a=[[@19,65:65='a',<335>,2:10]], col4=[[@23,76:79='col4',<335>,2:21]]}}, interface={col4=[{name=col4, table_ref=third}], col1=[{name=a, table_ref=third}]}}, filters=[{name=col1, table_ref=F4}, {name=col2, table_ref=t}, {name=col4, table_ref=F4}, {name=col5, table_ref=t}], interface={last=[{name=col1, table_ref=F4}], tcol2=[{name=col2, table_ref=t}], col3=[{name=col3, table_ref=t}]}, table_alias={t=fourth, F4=query0}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void subqueryDictionaryExtensionWhereClauseV12() {
		final String query = " SELECT F4.col1 as last, t.col2 as tcol2, t.col3 FROM "
			+ "\n  (select a as col1, b as col4, col6, col8 from third) F4"
			+ "\n  join fourth t on F4.col1 = t.col2 and F4.col4 = t.col5"
			+ "\n where F4.col6 = t.col7 and F4.col1 = t.col2 and F4.col8 > 0";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=col1, table_ref=F4}, alias=last}, 2={column={name=col2, table_ref=t}, alias=tcol2}, 3={column={name=col3, table_ref=t}}}, from={join={1={table={alias=F4, query={select={1={column={name=a, table_ref=null}, alias=col1}, 2={column={name=b, table_ref=null}, alias=col4}, 3={column={name=col6, table_ref=null}}, 4={column={name=col8, table_ref=null}}}, from={table={alias=null, table=third}}}}}, 2={join=join, on={and={1={condition={left={column={name=col1, table_ref=F4}}, right={column={name=col2, table_ref=t}}, operator==}}, 2={condition={left={column={name=col4, table_ref=F4}}, right={column={name=col5, table_ref=t}}, operator==}}}}}, 3={table={alias=t, table=fourth}}}}, where={and={1={condition={left={column={name=col6, table_ref=F4}}, right={column={name=col7, table_ref=t}}, operator==}}, 2={condition={left={column={name=col1, table_ref=F4}}, right={column={name=col2, table_ref=t}}, operator==}}, 3={condition={left={column={name=col8, table_ref=F4}}, right={literal=0}, operator=>}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[last, tcol2, col3]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{third={a=[[@19,65:65='a',<335>,2:10]], b=[[@23,76:76='b',<335>,2:21]], col8=[[@29,93:96='col8',<335>,2:38]], col6=[[@27,87:90='col6',<335>,2:32]]}, fourth={col7=[[@58,187:187='t',<335>,4:17]], col5=[[@50,163:163='t',<335>,3:50]], col2=[[@7,25:25='t',<335>,1:25], [@42,142:142='t',<335>,3:29], [@66,208:208='t',<335>,4:38]], col3=[[@13,42:42='t',<335>,1:42]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={col8=[[@29,93:96='col8',<335>,2:38], [@70,219:220='F4',<335>,4:49]], col6=[[@27,87:90='col6',<335>,2:32], [@54,177:178='F4',<335>,4:7]], col4=[[@25,81:84='col4',<335>,2:26], [@46,153:154='F4',<335>,3:40]], col1=[[@21,70:73='col1',<335>,2:15], [@1,8:9='F4',<335>,1:8], [@38,132:133='F4',<335>,3:19], [@62,198:199='F4',<335>,4:28]]}, query1={last=[[@5,19:22='last',<101>,1:19]], tcol2=[[@11,35:39='tcol2',<335>,1:35]], col3=[[@15,44:47='col3',<335>,1:44]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query1={query_dictionary={last=[[@5,19:22='last',<101>,1:19]], tcol2=[[@11,35:39='tcol2',<335>,1:35]], col3=[[@15,44:47='col3',<335>,1:44]]}, table_dictionary={fourth={col7=[[@58,187:187='t',<335>,4:17]], col5=[[@50,163:163='t',<335>,3:50]], col2=[[@7,25:25='t',<335>,1:25], [@42,142:142='t',<335>,3:29], [@66,208:208='t',<335>,4:38]], col3=[[@13,42:42='t',<335>,1:42]]}}, def_query0={query_dictionary={col8=[[@29,93:96='col8',<335>,2:38], [@70,219:220='F4',<335>,4:49]], col6=[[@27,87:90='col6',<335>,2:32], [@54,177:178='F4',<335>,4:7]], col4=[[@25,81:84='col4',<335>,2:26], [@46,153:154='F4',<335>,3:40]], col1=[[@21,70:73='col1',<335>,2:15], [@1,8:9='F4',<335>,1:8], [@38,132:133='F4',<335>,3:19], [@62,198:199='F4',<335>,4:28]]}, table_dictionary={third={a=[[@19,65:65='a',<335>,2:10]], b=[[@23,76:76='b',<335>,2:21]], col8=[[@29,93:96='col8',<335>,2:38]], col6=[[@27,87:90='col6',<335>,2:32]]}}, interface={col8=[{name=col8, table_ref=third}], col6=[{name=col6, table_ref=third}], col4=[{name=b, table_ref=third}], col1=[{name=a, table_ref=third}]}}, filters=[{name=col1, table_ref=F4}, {name=col2, table_ref=t}, {name=col4, table_ref=F4}, {name=col5, table_ref=t}, {name=col6, table_ref=F4}, {name=col7, table_ref=t}, {name=col8, table_ref=F4}], interface={last=[{name=col1, table_ref=F4}], tcol2=[{name=col2, table_ref=t}], col3=[{name=col3, table_ref=t}]}, table_alias={t=fourth, F4=query0}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void subqueryDictionaryExtensionHavingClauseV13() {
		final String query = " SELECT F4.col1 as last, t.col2 as tcol2, t.col3 FROM "
			+ "\n  (select a as col1, b as col4, col6, col8 from third) F4"
			+ "\n  join fourth t on F4.col1 = t.col2 and F4.col4 = t.col5"
			+ "\n having F4.col6 = t.col7 and F4.col1 = t.col2 and F4.col8 > 0";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=col1, table_ref=F4}, alias=last}, 2={column={name=col2, table_ref=t}, alias=tcol2}, 3={column={name=col3, table_ref=t}}}, having={and={1={condition={left={column={name=col6, table_ref=F4}}, right={column={name=col7, table_ref=t}}, operator==}}, 2={condition={left={column={name=col1, table_ref=F4}}, right={column={name=col2, table_ref=t}}, operator==}}, 3={condition={left={column={name=col8, table_ref=F4}}, right={literal=0}, operator=>}}}}, from={join={1={table={alias=F4, query={select={1={column={name=a, table_ref=null}, alias=col1}, 2={column={name=b, table_ref=null}, alias=col4}, 3={column={name=col6, table_ref=null}}, 4={column={name=col8, table_ref=null}}}, from={table={alias=null, table=third}}}}}, 2={join=join, on={and={1={condition={left={column={name=col1, table_ref=F4}}, right={column={name=col2, table_ref=t}}, operator==}}, 2={condition={left={column={name=col4, table_ref=F4}}, right={column={name=col5, table_ref=t}}, operator==}}}}}, 3={table={alias=t, table=fourth}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[last, tcol2, col3]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{third={a=[[@19,65:65='a',<335>,2:10]], b=[[@23,76:76='b',<335>,2:21]], col8=[[@29,93:96='col8',<335>,2:38]], col6=[[@27,87:90='col6',<335>,2:32]]}, fourth={col7=[[@58,188:188='t',<335>,4:18]], col5=[[@50,163:163='t',<335>,3:50]], col2=[[@7,25:25='t',<335>,1:25], [@42,142:142='t',<335>,3:29], [@66,209:209='t',<335>,4:39]], col3=[[@13,42:42='t',<335>,1:42]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={col8=[[@29,93:96='col8',<335>,2:38], [@70,220:221='F4',<335>,4:50]], col6=[[@27,87:90='col6',<335>,2:32], [@54,178:179='F4',<335>,4:8]], col4=[[@25,81:84='col4',<335>,2:26], [@46,153:154='F4',<335>,3:40]], col1=[[@21,70:73='col1',<335>,2:15], [@1,8:9='F4',<335>,1:8], [@38,132:133='F4',<335>,3:19], [@62,199:200='F4',<335>,4:29]]}, query1={last=[[@5,19:22='last',<101>,1:19]], tcol2=[[@11,35:39='tcol2',<335>,1:35]], col3=[[@15,44:47='col3',<335>,1:44]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query1={query_dictionary={last=[[@5,19:22='last',<101>,1:19]], tcol2=[[@11,35:39='tcol2',<335>,1:35]], col3=[[@15,44:47='col3',<335>,1:44]]}, table_dictionary={fourth={col7=[[@58,188:188='t',<335>,4:18]], col5=[[@50,163:163='t',<335>,3:50]], col2=[[@7,25:25='t',<335>,1:25], [@42,142:142='t',<335>,3:29], [@66,209:209='t',<335>,4:39]], col3=[[@13,42:42='t',<335>,1:42]]}}, def_query0={query_dictionary={col8=[[@29,93:96='col8',<335>,2:38], [@70,220:221='F4',<335>,4:50]], col6=[[@27,87:90='col6',<335>,2:32], [@54,178:179='F4',<335>,4:8]], col4=[[@25,81:84='col4',<335>,2:26], [@46,153:154='F4',<335>,3:40]], col1=[[@21,70:73='col1',<335>,2:15], [@1,8:9='F4',<335>,1:8], [@38,132:133='F4',<335>,3:19], [@62,199:200='F4',<335>,4:29]]}, table_dictionary={third={a=[[@19,65:65='a',<335>,2:10]], b=[[@23,76:76='b',<335>,2:21]], col8=[[@29,93:96='col8',<335>,2:38]], col6=[[@27,87:90='col6',<335>,2:32]]}}, interface={col8=[{name=col8, table_ref=third}], col6=[{name=col6, table_ref=third}], col4=[{name=b, table_ref=third}], col1=[{name=a, table_ref=third}]}}, filters=[{name=col1, table_ref=F4}, {name=col2, table_ref=t}, {name=col4, table_ref=F4}, {name=col5, table_ref=t}, {name=col6, table_ref=F4}, {name=col7, table_ref=t}, {name=col8, table_ref=F4}], interface={last=[{name=col1, table_ref=F4}], tcol2=[{name=col2, table_ref=t}], col3=[{name=col3, table_ref=t}]}, table_alias={t=fourth, F4=query0}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void subqueryDictionaryExtensionQualifyClauseV14() {
		final String query = " SELECT F4.col1 as last, t.col2 as tcol2, t.col3 FROM "
			+ "\n  (select a as col1, b as col4, col6, col8, col9 from third) F4"
			+ "\n  join fourth t on F4.col1 = t.col2 and F4.col4 = t.col5"
			+ "\n where F4.col6 = t.col7 and F4.col1 = t.col2 and F4.col8 > 0"
			+ "\n qualify F4.col9 = t.col10 and F4.col8 > 1";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=col1, table_ref=F4}, alias=last}, 2={column={name=col2, table_ref=t}, alias=tcol2}, 3={column={name=col3, table_ref=t}}}, from={join={1={table={alias=F4, query={select={1={column={name=a, table_ref=null}, alias=col1}, 2={column={name=b, table_ref=null}, alias=col4}, 3={column={name=col6, table_ref=null}}, 4={column={name=col8, table_ref=null}}, 5={column={name=col9, table_ref=null}}}, from={table={alias=null, table=third}}}}}, 2={join=join, on={and={1={condition={left={column={name=col1, table_ref=F4}}, right={column={name=col2, table_ref=t}}, operator==}}, 2={condition={left={column={name=col4, table_ref=F4}}, right={column={name=col5, table_ref=t}}, operator==}}}}}, 3={table={alias=t, table=fourth}}}}, where={and={1={condition={left={column={name=col6, table_ref=F4}}, right={column={name=col7, table_ref=t}}, operator==}}, 2={condition={left={column={name=col1, table_ref=F4}}, right={column={name=col2, table_ref=t}}, operator==}}, 3={condition={left={column={name=col8, table_ref=F4}}, right={literal=0}, operator=>}}}}, qualify={and={1={condition={left={column={name=col9, table_ref=F4}}, right={column={name=col10, table_ref=t}}, operator==}}, 2={condition={left={column={name=col8, table_ref=F4}}, right={literal=1}, operator=>}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[last, tcol2, col3]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{third={a=[[@19,65:65='a',<335>,2:10]], b=[[@23,76:76='b',<335>,2:21]], col8=[[@29,93:96='col8',<335>,2:38]], col9=[[@31,99:102='col9',<335>,2:44]], col6=[[@27,87:90='col6',<335>,2:32]]}, fourth={col10=[[@82,256:256='t',<335>,5:19]], col7=[[@60,193:193='t',<335>,4:17]], col5=[[@52,169:169='t',<335>,3:50]], col2=[[@7,25:25='t',<335>,1:25], [@44,148:148='t',<335>,3:29], [@68,214:214='t',<335>,4:38]], col3=[[@13,42:42='t',<335>,1:42]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={col8=[[@29,93:96='col8',<335>,2:38], [@72,225:226='F4',<335>,4:49], [@86,268:269='F4',<335>,5:31]], col9=[[@31,99:102='col9',<335>,2:44], [@78,246:247='F4',<335>,5:9]], col6=[[@27,87:90='col6',<335>,2:32], [@56,183:184='F4',<335>,4:7]], col4=[[@25,81:84='col4',<335>,2:26], [@48,159:160='F4',<335>,3:40]], col1=[[@21,70:73='col1',<335>,2:15], [@1,8:9='F4',<335>,1:8], [@40,138:139='F4',<335>,3:19], [@64,204:205='F4',<335>,4:28]]}, query1={last=[[@5,19:22='last',<101>,1:19]], tcol2=[[@11,35:39='tcol2',<335>,1:35]], col3=[[@15,44:47='col3',<335>,1:44]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query1={query_dictionary={last=[[@5,19:22='last',<101>,1:19]], tcol2=[[@11,35:39='tcol2',<335>,1:35]], col3=[[@15,44:47='col3',<335>,1:44]]}, table_dictionary={fourth={col10=[[@82,256:256='t',<335>,5:19]], col7=[[@60,193:193='t',<335>,4:17]], col5=[[@52,169:169='t',<335>,3:50]], col2=[[@7,25:25='t',<335>,1:25], [@44,148:148='t',<335>,3:29], [@68,214:214='t',<335>,4:38]], col3=[[@13,42:42='t',<335>,1:42]]}}, def_query0={query_dictionary={col8=[[@29,93:96='col8',<335>,2:38], [@72,225:226='F4',<335>,4:49], [@86,268:269='F4',<335>,5:31]], col9=[[@31,99:102='col9',<335>,2:44], [@78,246:247='F4',<335>,5:9]], col6=[[@27,87:90='col6',<335>,2:32], [@56,183:184='F4',<335>,4:7]], col4=[[@25,81:84='col4',<335>,2:26], [@48,159:160='F4',<335>,3:40]], col1=[[@21,70:73='col1',<335>,2:15], [@1,8:9='F4',<335>,1:8], [@40,138:139='F4',<335>,3:19], [@64,204:205='F4',<335>,4:28]]}, table_dictionary={third={a=[[@19,65:65='a',<335>,2:10]], b=[[@23,76:76='b',<335>,2:21]], col8=[[@29,93:96='col8',<335>,2:38]], col9=[[@31,99:102='col9',<335>,2:44]], col6=[[@27,87:90='col6',<335>,2:32]]}}, interface={col8=[{name=col8, table_ref=third}], col9=[{name=col9, table_ref=third}], col6=[{name=col6, table_ref=third}], col4=[{name=b, table_ref=third}], col1=[{name=a, table_ref=third}]}}, filters=[{name=col1, table_ref=F4}, {name=col2, table_ref=t}, {name=col4, table_ref=F4}, {name=col5, table_ref=t}, {name=col6, table_ref=F4}, {name=col7, table_ref=t}, {name=col8, table_ref=F4}, {name=col9, table_ref=F4}, {name=col10, table_ref=t}], interface={last=[{name=col1, table_ref=F4}], tcol2=[{name=col2, table_ref=t}], col3=[{name=col3, table_ref=t}]}, table_alias={t=fourth, F4=query0}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void subqueryDictionaryExtensionAggregateGroupByV15() {
		final String query = " SELECT F4.col1 as last, t.col2 as tcol2, t.col3, sum(F4.col11) as total_col11 FROM "
			+ "\n  (select a as col1, b as col4, col6, col8, col11 from third) F4"
			+ "\n  join fourth t on F4.col1 = t.col2 and F4.col4 = t.col5"
			+ "\n where F4.col6 = t.col7 and F4.col1 = t.col2 and F4.col8 > 0"
			+ "\n group by F4.col1, t.col2, t.col3";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=col1, table_ref=F4}, alias=last}, 2={column={name=col2, table_ref=t}, alias=tcol2}, 3={column={name=col3, table_ref=t}}, 4={function={function_name=sum, qualifier=null, parameters={column={name=col11, table_ref=F4}}}, alias=total_col11}}, from={join={1={table={alias=F4, query={select={1={column={name=a, table_ref=null}, alias=col1}, 2={column={name=b, table_ref=null}, alias=col4}, 3={column={name=col6, table_ref=null}}, 4={column={name=col8, table_ref=null}}, 5={column={name=col11, table_ref=null}}}, from={table={alias=null, table=third}}}}}, 2={join=join, on={and={1={condition={left={column={name=col1, table_ref=F4}}, right={column={name=col2, table_ref=t}}, operator==}}, 2={condition={left={column={name=col4, table_ref=F4}}, right={column={name=col5, table_ref=t}}, operator==}}}}}, 3={table={alias=t, table=fourth}}}}, where={and={1={condition={left={column={name=col6, table_ref=F4}}, right={column={name=col7, table_ref=t}}, operator==}}, 2={condition={left={column={name=col1, table_ref=F4}}, right={column={name=col2, table_ref=t}}, operator==}}, 3={condition={left={column={name=col8, table_ref=F4}}, right={literal=0}, operator=>}}}}, groupby={1={column={name=col1, table_ref=F4}}, 2={column={name=col2, table_ref=t}}, 3={column={name=col3, table_ref=t}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[last, total_col11, tcol2, col3]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{third={a=[[@28,95:95='a',<335>,2:10]], b=[[@32,106:106='b',<335>,2:21]], col8=[[@38,123:126='col8',<335>,2:38]], col11=[[@40,129:133='col11',<335>,2:44]], col6=[[@36,117:120='col6',<335>,2:32]]}, fourth={col7=[[@69,224:224='t',<335>,4:17]], col5=[[@61,200:200='t',<335>,3:50]], col2=[[@7,25:25='t',<335>,1:25], [@53,179:179='t',<335>,3:29], [@77,245:245='t',<335>,4:38], [@92,287:287='t',<335>,5:19]], col3=[[@13,42:42='t',<335>,1:42], [@96,295:295='t',<335>,5:27]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={col8=[[@38,123:126='col8',<335>,2:38], [@81,256:257='F4',<335>,4:49]], col11=[[@40,129:133='col11',<335>,2:44], [@19,54:55='F4',<335>,1:54]], col6=[[@36,117:120='col6',<335>,2:32], [@65,214:215='F4',<335>,4:7]], col4=[[@34,111:114='col4',<335>,2:26], [@57,190:191='F4',<335>,3:40]], col1=[[@30,100:103='col1',<335>,2:15], [@1,8:9='F4',<335>,1:8], [@49,169:170='F4',<335>,3:19], [@73,235:236='F4',<335>,4:28], [@88,278:279='F4',<335>,5:10]]}, query1={last=[[@5,19:22='last',<101>,1:19]], total_col11=[[@24,67:77='total_col11',<335>,1:67]], tcol2=[[@11,35:39='tcol2',<335>,1:35]], col3=[[@15,44:47='col3',<335>,1:44]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query1={query_dictionary={last=[[@5,19:22='last',<101>,1:19]], total_col11=[[@24,67:77='total_col11',<335>,1:67]], tcol2=[[@11,35:39='tcol2',<335>,1:35]], col3=[[@15,44:47='col3',<335>,1:44]]}, table_dictionary={fourth={col7=[[@69,224:224='t',<335>,4:17]], col5=[[@61,200:200='t',<335>,3:50]], col2=[[@7,25:25='t',<335>,1:25], [@53,179:179='t',<335>,3:29], [@77,245:245='t',<335>,4:38], [@92,287:287='t',<335>,5:19]], col3=[[@13,42:42='t',<335>,1:42], [@96,295:295='t',<335>,5:27]]}}, grouped_by=[{name=col1, table_ref=F4}, {name=col2, table_ref=t}, {name=col3, table_ref=t}], def_query0={query_dictionary={col8=[[@38,123:126='col8',<335>,2:38], [@81,256:257='F4',<335>,4:49]], col11=[[@40,129:133='col11',<335>,2:44], [@19,54:55='F4',<335>,1:54]], col6=[[@36,117:120='col6',<335>,2:32], [@65,214:215='F4',<335>,4:7]], col4=[[@34,111:114='col4',<335>,2:26], [@57,190:191='F4',<335>,3:40]], col1=[[@30,100:103='col1',<335>,2:15], [@1,8:9='F4',<335>,1:8], [@49,169:170='F4',<335>,3:19], [@73,235:236='F4',<335>,4:28], [@88,278:279='F4',<335>,5:10]]}, table_dictionary={third={a=[[@28,95:95='a',<335>,2:10]], b=[[@32,106:106='b',<335>,2:21]], col8=[[@38,123:126='col8',<335>,2:38]], col11=[[@40,129:133='col11',<335>,2:44]], col6=[[@36,117:120='col6',<335>,2:32]]}}, interface={col8=[{name=col8, table_ref=third}], col11=[{name=col11, table_ref=third}], col6=[{name=col6, table_ref=third}], col4=[{name=b, table_ref=third}], col1=[{name=a, table_ref=third}]}}, filters=[{name=col1, table_ref=F4}, {name=col2, table_ref=t}, {name=col4, table_ref=F4}, {name=col5, table_ref=t}, {name=col6, table_ref=F4}, {name=col7, table_ref=t}, {name=col8, table_ref=F4}], interface={last=[{name=col1, table_ref=F4}], total_col11=[{name=col11, table_ref=F4}], tcol2=[{name=col2, table_ref=t}], col3=[{name=col3, table_ref=t}]}, table_alias={t=fourth, F4=query0}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void subqueryDictionaryExtensionOrderByV16() {
		final String query = " SELECT F4.col1 as last, t.col2 as tcol2, t.col3 FROM "
			+ "\n  (select a as col1, b as col4, col6, col8, col12 from third) F4"
			+ "\n  join fourth t on F4.col1 = t.col2 and F4.col4 = t.col5"
			+ "\n where F4.col6 = t.col7 and F4.col1 = t.col2 and F4.col8 > 0"
			+ "\n order by t.col3 desc, F4.col1 asc, F4.col12 desc";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=col1, table_ref=F4}, alias=last}, 2={column={name=col2, table_ref=t}, alias=tcol2}, 3={column={name=col3, table_ref=t}}}, orderby={1={null_order=null, predicand={column={name=col3, table_ref=t}}, sort_order=desc}, 2={null_order=null, predicand={column={name=col1, table_ref=F4}}, sort_order=asc}, 3={null_order=null, predicand={column={name=col12, table_ref=F4}}, sort_order=desc}}, from={join={1={table={alias=F4, query={select={1={column={name=a, table_ref=null}, alias=col1}, 2={column={name=b, table_ref=null}, alias=col4}, 3={column={name=col6, table_ref=null}}, 4={column={name=col8, table_ref=null}}, 5={column={name=col12, table_ref=null}}}, from={table={alias=null, table=third}}}}}, 2={join=join, on={and={1={condition={left={column={name=col1, table_ref=F4}}, right={column={name=col2, table_ref=t}}, operator==}}, 2={condition={left={column={name=col4, table_ref=F4}}, right={column={name=col5, table_ref=t}}, operator==}}}}}, 3={table={alias=t, table=fourth}}}}, where={and={1={condition={left={column={name=col6, table_ref=F4}}, right={column={name=col7, table_ref=t}}, operator==}}, 2={condition={left={column={name=col1, table_ref=F4}}, right={column={name=col2, table_ref=t}}, operator==}}, 3={condition={left={column={name=col8, table_ref=F4}}, right={literal=0}, operator=>}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[last, tcol2, col3]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{third={a=[[@19,65:65='a',<335>,2:10]], b=[[@23,76:76='b',<335>,2:21]], col8=[[@29,93:96='col8',<335>,2:38]], col12=[[@31,99:103='col12',<335>,2:44]], col6=[[@27,87:90='col6',<335>,2:32]]}, fourth={col7=[[@60,194:194='t',<335>,4:17]], col5=[[@52,170:170='t',<335>,3:50]], col2=[[@7,25:25='t',<335>,1:25], [@44,149:149='t',<335>,3:29], [@68,215:215='t',<335>,4:38]], col3=[[@13,42:42='t',<335>,1:42], [@79,248:248='t',<335>,5:10]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={col8=[[@29,93:96='col8',<335>,2:38], [@72,226:227='F4',<335>,4:49]], col12=[[@31,99:103='col12',<335>,2:44], [@89,274:275='F4',<335>,5:36]], col6=[[@27,87:90='col6',<335>,2:32], [@56,184:185='F4',<335>,4:7]], col4=[[@25,81:84='col4',<335>,2:26], [@48,160:161='F4',<335>,3:40]], col1=[[@21,70:73='col1',<335>,2:15], [@1,8:9='F4',<335>,1:8], [@40,139:140='F4',<335>,3:19], [@64,205:206='F4',<335>,4:28], [@84,261:262='F4',<335>,5:23]]}, query1={last=[[@5,19:22='last',<101>,1:19]], tcol2=[[@11,35:39='tcol2',<335>,1:35]], col3=[[@15,44:47='col3',<335>,1:44]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query1={query_dictionary={last=[[@5,19:22='last',<101>,1:19]], tcol2=[[@11,35:39='tcol2',<335>,1:35]], col3=[[@15,44:47='col3',<335>,1:44]]}, table_dictionary={fourth={col7=[[@60,194:194='t',<335>,4:17]], col5=[[@52,170:170='t',<335>,3:50]], col2=[[@7,25:25='t',<335>,1:25], [@44,149:149='t',<335>,3:29], [@68,215:215='t',<335>,4:38]], col3=[[@13,42:42='t',<335>,1:42], [@79,248:248='t',<335>,5:10]]}}, def_query0={query_dictionary={col8=[[@29,93:96='col8',<335>,2:38], [@72,226:227='F4',<335>,4:49]], col12=[[@31,99:103='col12',<335>,2:44], [@89,274:275='F4',<335>,5:36]], col6=[[@27,87:90='col6',<335>,2:32], [@56,184:185='F4',<335>,4:7]], col4=[[@25,81:84='col4',<335>,2:26], [@48,160:161='F4',<335>,3:40]], col1=[[@21,70:73='col1',<335>,2:15], [@1,8:9='F4',<335>,1:8], [@40,139:140='F4',<335>,3:19], [@64,205:206='F4',<335>,4:28], [@84,261:262='F4',<335>,5:23]]}, table_dictionary={third={a=[[@19,65:65='a',<335>,2:10]], b=[[@23,76:76='b',<335>,2:21]], col8=[[@29,93:96='col8',<335>,2:38]], col12=[[@31,99:103='col12',<335>,2:44]], col6=[[@27,87:90='col6',<335>,2:32]]}}, interface={col8=[{name=col8, table_ref=third}], col12=[{name=col12, table_ref=third}], col6=[{name=col6, table_ref=third}], col4=[{name=b, table_ref=third}], col1=[{name=a, table_ref=third}]}}, ordered_by=[{name=col3, table_ref=t}, {name=col1, table_ref=F4}, {name=col12, table_ref=F4}], filters=[{name=col1, table_ref=F4}, {name=col2, table_ref=t}, {name=col4, table_ref=F4}, {name=col5, table_ref=t}, {name=col6, table_ref=F4}, {name=col7, table_ref=t}, {name=col8, table_ref=F4}], interface={last=[{name=col1, table_ref=F4}], tcol2=[{name=col2, table_ref=t}], col3=[{name=col3, table_ref=t}]}, table_alias={t=fourth, F4=query0}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void subqueryDictionaryExtensionJoinClauseMissingQualifiedV21() {
		final String query = " SELECT F4.col1 as last, t.col2 as tcol2, t.col3 FROM "
			+ "\n  (select a as col1, col4 from third) F4"
			+ "\n  join fourth t on F4.col1 = t.col2 and F4.col4 = t.col5 and F4.col99 = t.col2";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={col4=[[@23,76:79='col4',<335>,2:21], [@40,136:137='F4',<335>,3:40]], col1=[[@21,70:73='col1',<335>,2:15], [@1,8:9='F4',<335>,1:8], [@32,115:116='F4',<335>,3:19]]}, query1={last=[[@5,19:22='last',<101>,1:19]], tcol2=[[@11,35:39='tcol2',<335>,1:35]], col3=[[@15,44:47='col3',<335>,1:44]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query1={query_dictionary={last=[[@5,19:22='last',<101>,1:19]], tcol2=[[@11,35:39='tcol2',<335>,1:35]], col3=[[@15,44:47='col3',<335>,1:44]]}, table_dictionary={fourth={col5=[[@44,146:146='t',<335>,3:50]], col2=[[@7,25:25='t',<335>,1:25], [@36,125:125='t',<335>,3:29], [@52,168:168='t',<335>,3:72]], col3=[[@13,42:42='t',<335>,1:42]]}}, def_query0={query_dictionary={col4=[[@23,76:79='col4',<335>,2:21], [@40,136:137='F4',<335>,3:40]], col1=[[@21,70:73='col1',<335>,2:15], [@1,8:9='F4',<335>,1:8], [@32,115:116='F4',<335>,3:19]]}, table_dictionary={third={a=[[@19,65:65='a',<335>,2:10]], col4=[[@23,76:79='col4',<335>,2:21]]}}, interface={col4=[{name=col4, table_ref=third}], col1=[{name=a, table_ref=third}]}}, filters=[{name=col1, table_ref=F4}, {name=col2, table_ref=t}, {name=col4, table_ref=F4}, {name=col5, table_ref=t}, {name=col99, table_ref=F4}], interface={last=[{name=col1, table_ref=F4}], tcol2=[{name=col2, table_ref=t}], col3=[{name=col3, table_ref=t}]}, table_alias={t=fourth, F4=query0}}}",
				extractor.getSymbolTable().toString());

		Assert.assertFalse("Query0 dictionary should not include missing F4.col99",
				extractor.getQueryColumnDictionaryMap().toString().contains("col99"));

		Snippet snippet = extractor.getSnippet();
		assertFatalDiagnosticCount(
				snippet,
				"QUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIAS",
				"Qualified column 'col99'",
				null,
				1);
	}


	@Test
	public void subqueryDictionaryExtensionWhereClauseMissingQualifiedV22() {
		final String query = " SELECT F4.col1 as last, t.col2 as tcol2, t.col3 FROM "
			+ "\n  (select a as col1, b as col4, col6, col8 from third) F4"
			+ "\n  join fourth t on F4.col1 = t.col2 and F4.col4 = t.col5"
			+ "\n where F4.col6 = t.col7 and F4.col1 = t.col2 and F4.col8 > 0 and F4.col99 > 0";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={col8=[[@29,93:96='col8',<335>,2:38], [@70,219:220='F4',<335>,4:49]], col6=[[@27,87:90='col6',<335>,2:32], [@54,177:178='F4',<335>,4:7]], col4=[[@25,81:84='col4',<335>,2:26], [@46,153:154='F4',<335>,3:40]], col1=[[@21,70:73='col1',<335>,2:15], [@1,8:9='F4',<335>,1:8], [@38,132:133='F4',<335>,3:19], [@62,198:199='F4',<335>,4:28]]}, query1={last=[[@5,19:22='last',<101>,1:19]], tcol2=[[@11,35:39='tcol2',<335>,1:35]], col3=[[@15,44:47='col3',<335>,1:44]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query1={query_dictionary={last=[[@5,19:22='last',<101>,1:19]], tcol2=[[@11,35:39='tcol2',<335>,1:35]], col3=[[@15,44:47='col3',<335>,1:44]]}, table_dictionary={fourth={col7=[[@58,187:187='t',<335>,4:17]], col5=[[@50,163:163='t',<335>,3:50]], col2=[[@7,25:25='t',<335>,1:25], [@42,142:142='t',<335>,3:29], [@66,208:208='t',<335>,4:38]], col3=[[@13,42:42='t',<335>,1:42]]}}, def_query0={query_dictionary={col8=[[@29,93:96='col8',<335>,2:38], [@70,219:220='F4',<335>,4:49]], col6=[[@27,87:90='col6',<335>,2:32], [@54,177:178='F4',<335>,4:7]], col4=[[@25,81:84='col4',<335>,2:26], [@46,153:154='F4',<335>,3:40]], col1=[[@21,70:73='col1',<335>,2:15], [@1,8:9='F4',<335>,1:8], [@38,132:133='F4',<335>,3:19], [@62,198:199='F4',<335>,4:28]]}, table_dictionary={third={a=[[@19,65:65='a',<335>,2:10]], b=[[@23,76:76='b',<335>,2:21]], col8=[[@29,93:96='col8',<335>,2:38]], col6=[[@27,87:90='col6',<335>,2:32]]}}, interface={col8=[{name=col8, table_ref=third}], col6=[{name=col6, table_ref=third}], col4=[{name=b, table_ref=third}], col1=[{name=a, table_ref=third}]}}, filters=[{name=col1, table_ref=F4}, {name=col2, table_ref=t}, {name=col4, table_ref=F4}, {name=col5, table_ref=t}, {name=col6, table_ref=F4}, {name=col7, table_ref=t}, {name=col8, table_ref=F4}, {name=col99, table_ref=F4}], interface={last=[{name=col1, table_ref=F4}], tcol2=[{name=col2, table_ref=t}], col3=[{name=col3, table_ref=t}]}, table_alias={t=fourth, F4=query0}}}",
				extractor.getSymbolTable().toString());

		Assert.assertFalse("Query0 dictionary should not include missing F4.col99",
				extractor.getQueryColumnDictionaryMap().toString().contains("col99"));

		Snippet snippet = extractor.getSnippet();
		assertFatalDiagnosticCount(
				snippet,
				"QUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIAS",
				"Qualified column 'col99'",
				null,
				1);
	}


	@Test
	public void subqueryDictionaryExtensionHavingClauseMissingQualifiedV23() {
		final String query = " SELECT F4.col1 as last, t.col2 as tcol2, t.col3 FROM "
			+ "\n  (select a as col1, b as col4, col6, col8 from third) F4"
			+ "\n  join fourth t on F4.col1 = t.col2 and F4.col4 = t.col5"
			+ "\n having F4.col6 = t.col7 and F4.col1 = t.col2 and F4.col8 > 0 and F4.col99 > 0";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={col8=[[@29,93:96='col8',<335>,2:38], [@70,220:221='F4',<335>,4:50]], col6=[[@27,87:90='col6',<335>,2:32], [@54,178:179='F4',<335>,4:8]], col4=[[@25,81:84='col4',<335>,2:26], [@46,153:154='F4',<335>,3:40]], col1=[[@21,70:73='col1',<335>,2:15], [@1,8:9='F4',<335>,1:8], [@38,132:133='F4',<335>,3:19], [@62,199:200='F4',<335>,4:29]]}, query1={last=[[@5,19:22='last',<101>,1:19]], tcol2=[[@11,35:39='tcol2',<335>,1:35]], col3=[[@15,44:47='col3',<335>,1:44]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query1={query_dictionary={last=[[@5,19:22='last',<101>,1:19]], tcol2=[[@11,35:39='tcol2',<335>,1:35]], col3=[[@15,44:47='col3',<335>,1:44]]}, table_dictionary={fourth={col7=[[@58,188:188='t',<335>,4:18]], col5=[[@50,163:163='t',<335>,3:50]], col2=[[@7,25:25='t',<335>,1:25], [@42,142:142='t',<335>,3:29], [@66,209:209='t',<335>,4:39]], col3=[[@13,42:42='t',<335>,1:42]]}}, def_query0={query_dictionary={col8=[[@29,93:96='col8',<335>,2:38], [@70,220:221='F4',<335>,4:50]], col6=[[@27,87:90='col6',<335>,2:32], [@54,178:179='F4',<335>,4:8]], col4=[[@25,81:84='col4',<335>,2:26], [@46,153:154='F4',<335>,3:40]], col1=[[@21,70:73='col1',<335>,2:15], [@1,8:9='F4',<335>,1:8], [@38,132:133='F4',<335>,3:19], [@62,199:200='F4',<335>,4:29]]}, table_dictionary={third={a=[[@19,65:65='a',<335>,2:10]], b=[[@23,76:76='b',<335>,2:21]], col8=[[@29,93:96='col8',<335>,2:38]], col6=[[@27,87:90='col6',<335>,2:32]]}}, interface={col8=[{name=col8, table_ref=third}], col6=[{name=col6, table_ref=third}], col4=[{name=b, table_ref=third}], col1=[{name=a, table_ref=third}]}}, filters=[{name=col1, table_ref=F4}, {name=col2, table_ref=t}, {name=col4, table_ref=F4}, {name=col5, table_ref=t}, {name=col6, table_ref=F4}, {name=col7, table_ref=t}, {name=col8, table_ref=F4}, {name=col99, table_ref=F4}], interface={last=[{name=col1, table_ref=F4}], tcol2=[{name=col2, table_ref=t}], col3=[{name=col3, table_ref=t}]}, table_alias={t=fourth, F4=query0}}}",
				extractor.getSymbolTable().toString());

		Assert.assertFalse("Query0 dictionary should not include missing F4.col99",
				extractor.getQueryColumnDictionaryMap().toString().contains("col99"));

		Snippet snippet = extractor.getSnippet();
		assertFatalDiagnosticCount(
				snippet,
				"QUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIAS",
				"Qualified column 'col99'",
				null,
				1);
	}


	@Test
	public void subqueryDictionaryExtensionQualifyClauseMissingQualifiedV24() {
		final String query = " SELECT F4.col1 as last, t.col2 as tcol2, t.col3 FROM "
			+ "\n  (select a as col1, b as col4, col6, col8, col9 from third) F4"
			+ "\n  join fourth t on F4.col1 = t.col2 and F4.col4 = t.col5"
			+ "\n where F4.col6 = t.col7 and F4.col1 = t.col2 and F4.col8 > 0"
			+ "\n qualify F4.col9 = t.col10 and F4.col8 > 1 and F4.col99 > 1";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={col8=[[@29,93:96='col8',<335>,2:38], [@72,225:226='F4',<335>,4:49], [@86,268:269='F4',<335>,5:31]], col9=[[@31,99:102='col9',<335>,2:44], [@78,246:247='F4',<335>,5:9]], col6=[[@27,87:90='col6',<335>,2:32], [@56,183:184='F4',<335>,4:7]], col4=[[@25,81:84='col4',<335>,2:26], [@48,159:160='F4',<335>,3:40]], col1=[[@21,70:73='col1',<335>,2:15], [@1,8:9='F4',<335>,1:8], [@40,138:139='F4',<335>,3:19], [@64,204:205='F4',<335>,4:28]]}, query1={last=[[@5,19:22='last',<101>,1:19]], tcol2=[[@11,35:39='tcol2',<335>,1:35]], col3=[[@15,44:47='col3',<335>,1:44]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query1={query_dictionary={last=[[@5,19:22='last',<101>,1:19]], tcol2=[[@11,35:39='tcol2',<335>,1:35]], col3=[[@15,44:47='col3',<335>,1:44]]}, table_dictionary={fourth={col10=[[@82,256:256='t',<335>,5:19]], col7=[[@60,193:193='t',<335>,4:17]], col5=[[@52,169:169='t',<335>,3:50]], col2=[[@7,25:25='t',<335>,1:25], [@44,148:148='t',<335>,3:29], [@68,214:214='t',<335>,4:38]], col3=[[@13,42:42='t',<335>,1:42]]}}, def_query0={query_dictionary={col8=[[@29,93:96='col8',<335>,2:38], [@72,225:226='F4',<335>,4:49], [@86,268:269='F4',<335>,5:31]], col9=[[@31,99:102='col9',<335>,2:44], [@78,246:247='F4',<335>,5:9]], col6=[[@27,87:90='col6',<335>,2:32], [@56,183:184='F4',<335>,4:7]], col4=[[@25,81:84='col4',<335>,2:26], [@48,159:160='F4',<335>,3:40]], col1=[[@21,70:73='col1',<335>,2:15], [@1,8:9='F4',<335>,1:8], [@40,138:139='F4',<335>,3:19], [@64,204:205='F4',<335>,4:28]]}, table_dictionary={third={a=[[@19,65:65='a',<335>,2:10]], b=[[@23,76:76='b',<335>,2:21]], col8=[[@29,93:96='col8',<335>,2:38]], col9=[[@31,99:102='col9',<335>,2:44]], col6=[[@27,87:90='col6',<335>,2:32]]}}, interface={col8=[{name=col8, table_ref=third}], col9=[{name=col9, table_ref=third}], col6=[{name=col6, table_ref=third}], col4=[{name=b, table_ref=third}], col1=[{name=a, table_ref=third}]}}, filters=[{name=col1, table_ref=F4}, {name=col2, table_ref=t}, {name=col4, table_ref=F4}, {name=col5, table_ref=t}, {name=col6, table_ref=F4}, {name=col7, table_ref=t}, {name=col8, table_ref=F4}, {name=col9, table_ref=F4}, {name=col10, table_ref=t}, {name=col99, table_ref=F4}], interface={last=[{name=col1, table_ref=F4}], tcol2=[{name=col2, table_ref=t}], col3=[{name=col3, table_ref=t}]}, table_alias={t=fourth, F4=query0}}}",
				extractor.getSymbolTable().toString());

		Assert.assertFalse("Query0 dictionary should not include missing F4.col99",
				extractor.getQueryColumnDictionaryMap().toString().contains("col99"));

		Snippet snippet = extractor.getSnippet();
		assertFatalDiagnosticCount(
				snippet,
				"QUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIAS",
				"Qualified column 'col99'",
				null,
				1);
	}


	@Test
	public void subqueryDictionaryExtensionAggregateGroupByMissingQualifiedV25() {
		final String query = " SELECT F4.col1 as last, t.col2 as tcol2, t.col3, sum(F4.col11) as total_col11 FROM "
			+ "\n  (select a as col1, b as col4, col6, col8, col11 from third) F4"
			+ "\n  join fourth t on F4.col1 = t.col2 and F4.col4 = t.col5"
			+ "\n where F4.col6 = t.col7 and F4.col1 = t.col2 and F4.col8 > 0"
			+ "\n group by F4.col1, t.col2, t.col3, F4.col99";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={col8=[[@38,123:126='col8',<335>,2:38], [@81,256:257='F4',<335>,4:49]], col11=[[@40,129:133='col11',<335>,2:44], [@19,54:55='F4',<335>,1:54]], col6=[[@36,117:120='col6',<335>,2:32], [@65,214:215='F4',<335>,4:7]], col4=[[@34,111:114='col4',<335>,2:26], [@57,190:191='F4',<335>,3:40]], col1=[[@30,100:103='col1',<335>,2:15], [@1,8:9='F4',<335>,1:8], [@49,169:170='F4',<335>,3:19], [@73,235:236='F4',<335>,4:28], [@88,278:279='F4',<335>,5:10]]}, query1={last=[[@5,19:22='last',<101>,1:19]], total_col11=[[@24,67:77='total_col11',<335>,1:67]], tcol2=[[@11,35:39='tcol2',<335>,1:35]], col3=[[@15,44:47='col3',<335>,1:44]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query1={query_dictionary={last=[[@5,19:22='last',<101>,1:19]], total_col11=[[@24,67:77='total_col11',<335>,1:67]], tcol2=[[@11,35:39='tcol2',<335>,1:35]], col3=[[@15,44:47='col3',<335>,1:44]]}, table_dictionary={fourth={col7=[[@69,224:224='t',<335>,4:17]], col5=[[@61,200:200='t',<335>,3:50]], col2=[[@7,25:25='t',<335>,1:25], [@53,179:179='t',<335>,3:29], [@77,245:245='t',<335>,4:38], [@92,287:287='t',<335>,5:19]], col3=[[@13,42:42='t',<335>,1:42], [@96,295:295='t',<335>,5:27]]}}, grouped_by=[{name=col1, table_ref=F4}, {name=col2, table_ref=t}, {name=col3, table_ref=t}, {name=col99, table_ref=F4}], def_query0={query_dictionary={col8=[[@38,123:126='col8',<335>,2:38], [@81,256:257='F4',<335>,4:49]], col11=[[@40,129:133='col11',<335>,2:44], [@19,54:55='F4',<335>,1:54]], col6=[[@36,117:120='col6',<335>,2:32], [@65,214:215='F4',<335>,4:7]], col4=[[@34,111:114='col4',<335>,2:26], [@57,190:191='F4',<335>,3:40]], col1=[[@30,100:103='col1',<335>,2:15], [@1,8:9='F4',<335>,1:8], [@49,169:170='F4',<335>,3:19], [@73,235:236='F4',<335>,4:28], [@88,278:279='F4',<335>,5:10]]}, table_dictionary={third={a=[[@28,95:95='a',<335>,2:10]], b=[[@32,106:106='b',<335>,2:21]], col8=[[@38,123:126='col8',<335>,2:38]], col11=[[@40,129:133='col11',<335>,2:44]], col6=[[@36,117:120='col6',<335>,2:32]]}}, interface={col8=[{name=col8, table_ref=third}], col11=[{name=col11, table_ref=third}], col6=[{name=col6, table_ref=third}], col4=[{name=b, table_ref=third}], col1=[{name=a, table_ref=third}]}}, filters=[{name=col1, table_ref=F4}, {name=col2, table_ref=t}, {name=col4, table_ref=F4}, {name=col5, table_ref=t}, {name=col6, table_ref=F4}, {name=col7, table_ref=t}, {name=col8, table_ref=F4}], interface={last=[{name=col1, table_ref=F4}], total_col11=[{name=col11, table_ref=F4}], tcol2=[{name=col2, table_ref=t}], col3=[{name=col3, table_ref=t}]}, table_alias={t=fourth, F4=query0}}}",
				extractor.getSymbolTable().toString());

		Assert.assertFalse("Query0 dictionary should not include missing F4.col99",
				extractor.getQueryColumnDictionaryMap().toString().contains("col99"));

		Snippet snippet = extractor.getSnippet();
		assertFatalDiagnosticCount(
				snippet,
				"QUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIAS",
				"Qualified column 'col99'",
				null,
				1);
	}


	@Test
	public void subqueryDictionaryExtensionOrderByMissingQualifiedV26() {
		final String query = " SELECT F4.col1 as last, t.col2 as tcol2, t.col3 FROM "
			+ "\n  (select a as col1, b as col4, col6, col8, col12 from third) F4"
			+ "\n  join fourth t on F4.col1 = t.col2 and F4.col4 = t.col5"
			+ "\n where F4.col6 = t.col7 and F4.col1 = t.col2 and F4.col8 > 0"
			+ "\n order by t.col3 desc, F4.col1 asc, F4.col12 desc, F4.col99 desc";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={col8=[[@29,93:96='col8',<335>,2:38], [@72,226:227='F4',<335>,4:49]], col12=[[@31,99:103='col12',<335>,2:44], [@89,274:275='F4',<335>,5:36]], col6=[[@27,87:90='col6',<335>,2:32], [@56,184:185='F4',<335>,4:7]], col4=[[@25,81:84='col4',<335>,2:26], [@48,160:161='F4',<335>,3:40]], col1=[[@21,70:73='col1',<335>,2:15], [@1,8:9='F4',<335>,1:8], [@40,139:140='F4',<335>,3:19], [@64,205:206='F4',<335>,4:28], [@84,261:262='F4',<335>,5:23]]}, query1={last=[[@5,19:22='last',<101>,1:19]], tcol2=[[@11,35:39='tcol2',<335>,1:35]], col3=[[@15,44:47='col3',<335>,1:44]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query1={query_dictionary={last=[[@5,19:22='last',<101>,1:19]], tcol2=[[@11,35:39='tcol2',<335>,1:35]], col3=[[@15,44:47='col3',<335>,1:44]]}, table_dictionary={fourth={col7=[[@60,194:194='t',<335>,4:17]], col5=[[@52,170:170='t',<335>,3:50]], col2=[[@7,25:25='t',<335>,1:25], [@44,149:149='t',<335>,3:29], [@68,215:215='t',<335>,4:38]], col3=[[@13,42:42='t',<335>,1:42], [@79,248:248='t',<335>,5:10]]}}, def_query0={query_dictionary={col8=[[@29,93:96='col8',<335>,2:38], [@72,226:227='F4',<335>,4:49]], col12=[[@31,99:103='col12',<335>,2:44], [@89,274:275='F4',<335>,5:36]], col6=[[@27,87:90='col6',<335>,2:32], [@56,184:185='F4',<335>,4:7]], col4=[[@25,81:84='col4',<335>,2:26], [@48,160:161='F4',<335>,3:40]], col1=[[@21,70:73='col1',<335>,2:15], [@1,8:9='F4',<335>,1:8], [@40,139:140='F4',<335>,3:19], [@64,205:206='F4',<335>,4:28], [@84,261:262='F4',<335>,5:23]]}, table_dictionary={third={a=[[@19,65:65='a',<335>,2:10]], b=[[@23,76:76='b',<335>,2:21]], col8=[[@29,93:96='col8',<335>,2:38]], col12=[[@31,99:103='col12',<335>,2:44]], col6=[[@27,87:90='col6',<335>,2:32]]}}, interface={col8=[{name=col8, table_ref=third}], col12=[{name=col12, table_ref=third}], col6=[{name=col6, table_ref=third}], col4=[{name=b, table_ref=third}], col1=[{name=a, table_ref=third}]}}, ordered_by=[{name=col3, table_ref=t}, {name=col1, table_ref=F4}, {name=col12, table_ref=F4}, {name=col99, table_ref=F4}], filters=[{name=col1, table_ref=F4}, {name=col2, table_ref=t}, {name=col4, table_ref=F4}, {name=col5, table_ref=t}, {name=col6, table_ref=F4}, {name=col7, table_ref=t}, {name=col8, table_ref=F4}], interface={last=[{name=col1, table_ref=F4}], tcol2=[{name=col2, table_ref=t}], col3=[{name=col3, table_ref=t}]}, table_alias={t=fourth, F4=query0}}}",
				extractor.getSymbolTable().toString());

		Assert.assertFalse("Query0 dictionary should not include missing F4.col99",
				extractor.getQueryColumnDictionaryMap().toString().contains("col99"));

		Snippet snippet = extractor.getSnippet();
		assertFatalDiagnosticCount(
				snippet,
				"QUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIAS",
				"Qualified column 'col99'",
				null,
				1);
	}


	@Test
	public void subqueryDictionaryExtensionJoinClauseSubqueryJoinV31() {
		final String query = " SELECT F4.col1 as last, t.col2 as tcol2, t.col3 FROM "
			+ "\n  (select a as col1, col4 from third) F4"
			+ "\n  join (select * from fourth) as t on F4.col1 = t.col2 and F4.col4 = t.col5";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=col1, table_ref=F4}, alias=last}, 2={column={name=col2, table_ref=t}, alias=tcol2}, 3={column={name=col3, table_ref=t}}}, from={join={1={table={alias=F4, query={select={1={column={name=a, table_ref=null}, alias=col1}, 2={column={name=col4, table_ref=null}}}, from={table={alias=null, table=third}}}}}, 2={join=join, on={and={1={condition={left={column={name=col1, table_ref=F4}}, right={column={name=col2, table_ref=t}}, operator==}}, 2={condition={left={column={name=col4, table_ref=F4}}, right={column={name=col5, table_ref=t}}, operator==}}}}}, 3={table={alias=t, query={select={1={column={name=*, table_ref=*}}}, from={table={alias=null, table=fourth}}}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[last, tcol2, col3]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{third={a=[[@19,65:65='a',<335>,2:10]], col4=[[@23,76:79='col4',<335>,2:21]]}, fourth={*=[[@31,111:111='*',<289>,3:15]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={col4=[[@23,76:79='col4',<335>,2:21], [@46,155:156='F4',<335>,3:59]], *=[[@31,111:111='*',<289>,3:15]], col1=[[@21,70:73='col1',<335>,2:15], [@1,8:9='F4',<335>,1:8], [@38,134:135='F4',<335>,3:38]]}, query1={*=[[@31,111:111='*',<289>,3:15]], col5=[[@50,165:165='t',<335>,3:69]], col2=[[@7,25:25='t',<335>,1:25], [@42,144:144='t',<335>,3:48]], col3=[[@13,42:42='t',<335>,1:42]]}, query2={last=[[@5,19:22='last',<101>,1:19]], tcol2=[[@11,35:39='tcol2',<335>,1:35]], col3=[[@15,44:47='col3',<335>,1:44]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query2={query_dictionary={last=[[@5,19:22='last',<101>,1:19]], tcol2=[[@11,35:39='tcol2',<335>,1:35]], col3=[[@15,44:47='col3',<335>,1:44]]}, table_dictionary={}, def_query1={query_dictionary={*=[[@31,111:111='*',<289>,3:15]], col5=[[@50,165:165='t',<335>,3:69]], col2=[[@7,25:25='t',<335>,1:25], [@42,144:144='t',<335>,3:48]], col3=[[@13,42:42='t',<335>,1:42]]}, table_dictionary={fourth={*=[[@31,111:111='*',<289>,3:15]]}}, interface={*=[{name=*, table_ref=*}]}}, def_query0={query_dictionary={col4=[[@23,76:79='col4',<335>,2:21], [@46,155:156='F4',<335>,3:59]], *=[[@31,111:111='*',<289>,3:15]], col1=[[@21,70:73='col1',<335>,2:15], [@1,8:9='F4',<335>,1:8], [@38,134:135='F4',<335>,3:38]]}, table_dictionary={third={a=[[@19,65:65='a',<335>,2:10]], col4=[[@23,76:79='col4',<335>,2:21]]}}, interface={col4=[{name=col4, table_ref=third}], col1=[{name=a, table_ref=third}]}}, filters=[{name=col1, table_ref=F4}, {name=col2, table_ref=t}, {name=col4, table_ref=F4}, {name=col5, table_ref=t}], interface={last=[{name=col1, table_ref=F4}], tcol2=[{name=col2, table_ref=t}], col3=[{name=col3, table_ref=t}]}, table_alias={t=query1, F4=query0}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void subqueryDictionaryExtensionWhereClauseSubqueryJoinV32() {
		final String query = " SELECT F4.col1 as last, t.col2 as tcol2, t.col3 FROM "
			+ "\n  (select a as col1, b as col4, col6, col8 from third) F4"
			+ "\n  join (select * from fourth) as t on F4.col1 = t.col2 and F4.col4 = t.col5"
			+ "\n where F4.col6 = t.col7 and F4.col1 = t.col2 and F4.col8 > 0";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=col1, table_ref=F4}, alias=last}, 2={column={name=col2, table_ref=t}, alias=tcol2}, 3={column={name=col3, table_ref=t}}}, from={join={1={table={alias=F4, query={select={1={column={name=a, table_ref=null}, alias=col1}, 2={column={name=b, table_ref=null}, alias=col4}, 3={column={name=col6, table_ref=null}}, 4={column={name=col8, table_ref=null}}}, from={table={alias=null, table=third}}}}}, 2={join=join, on={and={1={condition={left={column={name=col1, table_ref=F4}}, right={column={name=col2, table_ref=t}}, operator==}}, 2={condition={left={column={name=col4, table_ref=F4}}, right={column={name=col5, table_ref=t}}, operator==}}}}}, 3={table={alias=t, query={select={1={column={name=*, table_ref=*}}}, from={table={alias=null, table=fourth}}}}}}}, where={and={1={condition={left={column={name=col6, table_ref=F4}}, right={column={name=col7, table_ref=t}}, operator==}}, 2={condition={left={column={name=col1, table_ref=F4}}, right={column={name=col2, table_ref=t}}, operator==}}, 3={condition={left={column={name=col8, table_ref=F4}}, right={literal=0}, operator=>}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[last, tcol2, col3]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{third={a=[[@19,65:65='a',<335>,2:10]], b=[[@23,76:76='b',<335>,2:21]], col8=[[@29,93:96='col8',<335>,2:38]], col6=[[@27,87:90='col6',<335>,2:32]]}, fourth={*=[[@37,128:128='*',<289>,3:15]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={col8=[[@29,93:96='col8',<335>,2:38], [@76,238:239='F4',<335>,4:49]], col6=[[@27,87:90='col6',<335>,2:32], [@60,196:197='F4',<335>,4:7]], col4=[[@25,81:84='col4',<335>,2:26], [@52,172:173='F4',<335>,3:59]], *=[[@37,128:128='*',<289>,3:15]], col1=[[@21,70:73='col1',<335>,2:15], [@1,8:9='F4',<335>,1:8], [@44,151:152='F4',<335>,3:38], [@68,217:218='F4',<335>,4:28]]}, query1={col7=[[@64,206:206='t',<335>,4:17]], *=[[@37,128:128='*',<289>,3:15]], col5=[[@56,182:182='t',<335>,3:69]], col2=[[@7,25:25='t',<335>,1:25], [@48,161:161='t',<335>,3:48], [@72,227:227='t',<335>,4:38]], col3=[[@13,42:42='t',<335>,1:42]]}, query2={last=[[@5,19:22='last',<101>,1:19]], tcol2=[[@11,35:39='tcol2',<335>,1:35]], col3=[[@15,44:47='col3',<335>,1:44]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query2={query_dictionary={last=[[@5,19:22='last',<101>,1:19]], tcol2=[[@11,35:39='tcol2',<335>,1:35]], col3=[[@15,44:47='col3',<335>,1:44]]}, table_dictionary={}, def_query1={query_dictionary={col7=[[@64,206:206='t',<335>,4:17]], *=[[@37,128:128='*',<289>,3:15]], col5=[[@56,182:182='t',<335>,3:69]], col2=[[@7,25:25='t',<335>,1:25], [@48,161:161='t',<335>,3:48], [@72,227:227='t',<335>,4:38]], col3=[[@13,42:42='t',<335>,1:42]]}, table_dictionary={fourth={*=[[@37,128:128='*',<289>,3:15]]}}, interface={*=[{name=*, table_ref=*}]}}, def_query0={query_dictionary={col8=[[@29,93:96='col8',<335>,2:38], [@76,238:239='F4',<335>,4:49]], col6=[[@27,87:90='col6',<335>,2:32], [@60,196:197='F4',<335>,4:7]], col4=[[@25,81:84='col4',<335>,2:26], [@52,172:173='F4',<335>,3:59]], *=[[@37,128:128='*',<289>,3:15]], col1=[[@21,70:73='col1',<335>,2:15], [@1,8:9='F4',<335>,1:8], [@44,151:152='F4',<335>,3:38], [@68,217:218='F4',<335>,4:28]]}, table_dictionary={third={a=[[@19,65:65='a',<335>,2:10]], b=[[@23,76:76='b',<335>,2:21]], col8=[[@29,93:96='col8',<335>,2:38]], col6=[[@27,87:90='col6',<335>,2:32]]}}, interface={col8=[{name=col8, table_ref=third}], col6=[{name=col6, table_ref=third}], col4=[{name=b, table_ref=third}], col1=[{name=a, table_ref=third}]}}, filters=[{name=col1, table_ref=F4}, {name=col2, table_ref=t}, {name=col4, table_ref=F4}, {name=col5, table_ref=t}, {name=col6, table_ref=F4}, {name=col7, table_ref=t}, {name=col8, table_ref=F4}], interface={last=[{name=col1, table_ref=F4}], tcol2=[{name=col2, table_ref=t}], col3=[{name=col3, table_ref=t}]}, table_alias={t=query1, F4=query0}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void subqueryDictionaryExtensionQualifyClauseMissingUnqualifiedV33() {
		final String query = " SELECT F4.col1 as last, t.col2 as tcol2, t.col3 FROM "
			+ "\n  (select a as col1, b as col4, col6, col8, col9 from third) F4"
			+ "\n  join fourth t on F4.col1 = t.col2 and F4.col4 = t.col5"
			+ "\n where F4.col6 = t.col7 and F4.col1 = t.col2 and F4.col8 > 0"
			+ "\n qualify F4.col9 = t.col10 and F4.col8 > 1 and missing > 1";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=col1, table_ref=F4}, alias=last}, 2={column={name=col2, table_ref=t}, alias=tcol2}, 3={column={name=col3, table_ref=t}}}, from={join={1={table={alias=F4, query={select={1={column={name=a, table_ref=null}, alias=col1}, 2={column={name=b, table_ref=null}, alias=col4}, 3={column={name=col6, table_ref=null}}, 4={column={name=col8, table_ref=null}}, 5={column={name=col9, table_ref=null}}}, from={table={alias=null, table=third}}}}}, 2={join=join, on={and={1={condition={left={column={name=col1, table_ref=F4}}, right={column={name=col2, table_ref=t}}, operator==}}, 2={condition={left={column={name=col4, table_ref=F4}}, right={column={name=col5, table_ref=t}}, operator==}}}}}, 3={table={alias=t, table=fourth}}}}, where={and={1={condition={left={column={name=col6, table_ref=F4}}, right={column={name=col7, table_ref=t}}, operator==}}, 2={condition={left={column={name=col1, table_ref=F4}}, right={column={name=col2, table_ref=t}}, operator==}}, 3={condition={left={column={name=col8, table_ref=F4}}, right={literal=0}, operator=>}}}}, qualify={and={1={condition={left={column={name=col9, table_ref=F4}}, right={column={name=col10, table_ref=t}}, operator==}}, 2={condition={left={column={name=col8, table_ref=F4}}, right={literal=1}, operator=>}}, 3={condition={left={column={name=missing, table_ref=null}}, right={literal=1}, operator=>}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[last, tcol2, col3]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{third={a=[[@19,65:65='a',<335>,2:10]], b=[[@23,76:76='b',<335>,2:21]], col8=[[@29,93:96='col8',<335>,2:38]], col9=[[@31,99:102='col9',<335>,2:44]], col6=[[@27,87:90='col6',<335>,2:32]]}, fourth={col10=[[@82,256:256='t',<335>,5:19]], missing=[[@92,284:290='missing',<335>,5:47]], col7=[[@60,193:193='t',<335>,4:17]], col5=[[@52,169:169='t',<335>,3:50]], col2=[[@7,25:25='t',<335>,1:25], [@44,148:148='t',<335>,3:29], [@68,214:214='t',<335>,4:38]], col3=[[@13,42:42='t',<335>,1:42]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={col8=[[@29,93:96='col8',<335>,2:38], [@72,225:226='F4',<335>,4:49], [@86,268:269='F4',<335>,5:31]], col9=[[@31,99:102='col9',<335>,2:44], [@78,246:247='F4',<335>,5:9]], col6=[[@27,87:90='col6',<335>,2:32], [@56,183:184='F4',<335>,4:7]], col4=[[@25,81:84='col4',<335>,2:26], [@48,159:160='F4',<335>,3:40]], col1=[[@21,70:73='col1',<335>,2:15], [@1,8:9='F4',<335>,1:8], [@40,138:139='F4',<335>,3:19], [@64,204:205='F4',<335>,4:28]]}, query1={last=[[@5,19:22='last',<101>,1:19]], tcol2=[[@11,35:39='tcol2',<335>,1:35]], col3=[[@15,44:47='col3',<335>,1:44]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query1={query_dictionary={last=[[@5,19:22='last',<101>,1:19]], tcol2=[[@11,35:39='tcol2',<335>,1:35]], col3=[[@15,44:47='col3',<335>,1:44]]}, table_dictionary={fourth={col10=[[@82,256:256='t',<335>,5:19]], missing=[[@92,284:290='missing',<335>,5:47]], col7=[[@60,193:193='t',<335>,4:17]], col5=[[@52,169:169='t',<335>,3:50]], col2=[[@7,25:25='t',<335>,1:25], [@44,148:148='t',<335>,3:29], [@68,214:214='t',<335>,4:38]], col3=[[@13,42:42='t',<335>,1:42]]}}, def_query0={query_dictionary={col8=[[@29,93:96='col8',<335>,2:38], [@72,225:226='F4',<335>,4:49], [@86,268:269='F4',<335>,5:31]], col9=[[@31,99:102='col9',<335>,2:44], [@78,246:247='F4',<335>,5:9]], col6=[[@27,87:90='col6',<335>,2:32], [@56,183:184='F4',<335>,4:7]], col4=[[@25,81:84='col4',<335>,2:26], [@48,159:160='F4',<335>,3:40]], col1=[[@21,70:73='col1',<335>,2:15], [@1,8:9='F4',<335>,1:8], [@40,138:139='F4',<335>,3:19], [@64,204:205='F4',<335>,4:28]]}, table_dictionary={third={a=[[@19,65:65='a',<335>,2:10]], b=[[@23,76:76='b',<335>,2:21]], col8=[[@29,93:96='col8',<335>,2:38]], col9=[[@31,99:102='col9',<335>,2:44]], col6=[[@27,87:90='col6',<335>,2:32]]}}, interface={col8=[{name=col8, table_ref=third}], col9=[{name=col9, table_ref=third}], col6=[{name=col6, table_ref=third}], col4=[{name=b, table_ref=third}], col1=[{name=a, table_ref=third}]}}, filters=[{name=col1, table_ref=F4}, {name=col2, table_ref=t}, {name=col4, table_ref=F4}, {name=col5, table_ref=t}, {name=col6, table_ref=F4}, {name=col7, table_ref=t}, {name=col8, table_ref=F4}, {name=col9, table_ref=F4}, {name=col10, table_ref=t}, {name=missing, table_ref=fourth}], interface={last=[{name=col1, table_ref=F4}], tcol2=[{name=col2, table_ref=t}], col3=[{name=col3, table_ref=t}]}, table_alias={t=fourth, F4=query0}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void subqueryDictionaryExtensionAggregateGroupByMissingUnqualifiedV34() {
		final String query = " SELECT F4.col1 as last, t.col2 as tcol2, t.col3, sum(F4.col11) as total_col11 FROM "
			+ "\n  (select a as col1, b as col4, col6, col8, col11 from third) F4"
			+ "\n  join fourth t on F4.col1 = t.col2 and F4.col4 = t.col5"
			+ "\n where F4.col6 = t.col7 and F4.col1 = t.col2 and F4.col8 > 0"
			+ "\n group by F4.col1, t.col2, t.col3, missing";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=col1, table_ref=F4}, alias=last}, 2={column={name=col2, table_ref=t}, alias=tcol2}, 3={column={name=col3, table_ref=t}}, 4={function={function_name=sum, qualifier=null, parameters={column={name=col11, table_ref=F4}}}, alias=total_col11}}, from={join={1={table={alias=F4, query={select={1={column={name=a, table_ref=null}, alias=col1}, 2={column={name=b, table_ref=null}, alias=col4}, 3={column={name=col6, table_ref=null}}, 4={column={name=col8, table_ref=null}}, 5={column={name=col11, table_ref=null}}}, from={table={alias=null, table=third}}}}}, 2={join=join, on={and={1={condition={left={column={name=col1, table_ref=F4}}, right={column={name=col2, table_ref=t}}, operator==}}, 2={condition={left={column={name=col4, table_ref=F4}}, right={column={name=col5, table_ref=t}}, operator==}}}}}, 3={table={alias=t, table=fourth}}}}, where={and={1={condition={left={column={name=col6, table_ref=F4}}, right={column={name=col7, table_ref=t}}, operator==}}, 2={condition={left={column={name=col1, table_ref=F4}}, right={column={name=col2, table_ref=t}}, operator==}}, 3={condition={left={column={name=col8, table_ref=F4}}, right={literal=0}, operator=>}}}}, groupby={1={column={name=col1, table_ref=F4}}, 2={column={name=col2, table_ref=t}}, 3={column={name=col3, table_ref=t}}, 4={column={name=missing, table_ref=null}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[last, total_col11, tcol2, col3]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{third={a=[[@28,95:95='a',<335>,2:10]], b=[[@32,106:106='b',<335>,2:21]], col8=[[@38,123:126='col8',<335>,2:38]], col11=[[@40,129:133='col11',<335>,2:44]], col6=[[@36,117:120='col6',<335>,2:32]]}, fourth={missing=[[@100,303:309='missing',<335>,5:35]], col7=[[@69,224:224='t',<335>,4:17]], col5=[[@61,200:200='t',<335>,3:50]], col2=[[@7,25:25='t',<335>,1:25], [@53,179:179='t',<335>,3:29], [@77,245:245='t',<335>,4:38], [@92,287:287='t',<335>,5:19]], col3=[[@13,42:42='t',<335>,1:42], [@96,295:295='t',<335>,5:27]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={col8=[[@38,123:126='col8',<335>,2:38], [@81,256:257='F4',<335>,4:49]], col11=[[@40,129:133='col11',<335>,2:44], [@19,54:55='F4',<335>,1:54]], col6=[[@36,117:120='col6',<335>,2:32], [@65,214:215='F4',<335>,4:7]], col4=[[@34,111:114='col4',<335>,2:26], [@57,190:191='F4',<335>,3:40]], col1=[[@30,100:103='col1',<335>,2:15], [@1,8:9='F4',<335>,1:8], [@49,169:170='F4',<335>,3:19], [@73,235:236='F4',<335>,4:28], [@88,278:279='F4',<335>,5:10]]}, query1={last=[[@5,19:22='last',<101>,1:19]], total_col11=[[@24,67:77='total_col11',<335>,1:67]], tcol2=[[@11,35:39='tcol2',<335>,1:35]], col3=[[@15,44:47='col3',<335>,1:44]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query1={query_dictionary={last=[[@5,19:22='last',<101>,1:19]], total_col11=[[@24,67:77='total_col11',<335>,1:67]], tcol2=[[@11,35:39='tcol2',<335>,1:35]], col3=[[@15,44:47='col3',<335>,1:44]]}, table_dictionary={fourth={missing=[[@100,303:309='missing',<335>,5:35]], col7=[[@69,224:224='t',<335>,4:17]], col5=[[@61,200:200='t',<335>,3:50]], col2=[[@7,25:25='t',<335>,1:25], [@53,179:179='t',<335>,3:29], [@77,245:245='t',<335>,4:38], [@92,287:287='t',<335>,5:19]], col3=[[@13,42:42='t',<335>,1:42], [@96,295:295='t',<335>,5:27]]}}, grouped_by=[{name=col1, table_ref=F4}, {name=col2, table_ref=t}, {name=col3, table_ref=t}, {name=missing, table_ref=fourth}], def_query0={query_dictionary={col8=[[@38,123:126='col8',<335>,2:38], [@81,256:257='F4',<335>,4:49]], col11=[[@40,129:133='col11',<335>,2:44], [@19,54:55='F4',<335>,1:54]], col6=[[@36,117:120='col6',<335>,2:32], [@65,214:215='F4',<335>,4:7]], col4=[[@34,111:114='col4',<335>,2:26], [@57,190:191='F4',<335>,3:40]], col1=[[@30,100:103='col1',<335>,2:15], [@1,8:9='F4',<335>,1:8], [@49,169:170='F4',<335>,3:19], [@73,235:236='F4',<335>,4:28], [@88,278:279='F4',<335>,5:10]]}, table_dictionary={third={a=[[@28,95:95='a',<335>,2:10]], b=[[@32,106:106='b',<335>,2:21]], col8=[[@38,123:126='col8',<335>,2:38]], col11=[[@40,129:133='col11',<335>,2:44]], col6=[[@36,117:120='col6',<335>,2:32]]}}, interface={col8=[{name=col8, table_ref=third}], col11=[{name=col11, table_ref=third}], col6=[{name=col6, table_ref=third}], col4=[{name=b, table_ref=third}], col1=[{name=a, table_ref=third}]}}, filters=[{name=col1, table_ref=F4}, {name=col2, table_ref=t}, {name=col4, table_ref=F4}, {name=col5, table_ref=t}, {name=col6, table_ref=F4}, {name=col7, table_ref=t}, {name=col8, table_ref=F4}], interface={last=[{name=col1, table_ref=F4}], total_col11=[{name=col11, table_ref=F4}], tcol2=[{name=col2, table_ref=t}], col3=[{name=col3, table_ref=t}]}, table_alias={t=fourth, F4=query0}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void subqueryDictionaryExtensionOrderByMissingUnqualifiedV35() {
		final String query = " SELECT F4.col1 as last, t.col2 as tcol2, t.col3 FROM "
			+ "\n  (select a as col1, b as col4, col6, col8, col12 from third) F4"
			+ "\n  join fourth t on F4.col1 = t.col2 and F4.col4 = t.col5"
			+ "\n where F4.col6 = t.col7 and F4.col1 = t.col2 and F4.col8 > 0"
			+ "\n order by t.col3 desc, F4.col1 asc, F4.col12 desc, missing desc";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=col1, table_ref=F4}, alias=last}, 2={column={name=col2, table_ref=t}, alias=tcol2}, 3={column={name=col3, table_ref=t}}}, orderby={1={null_order=null, predicand={column={name=col3, table_ref=t}}, sort_order=desc}, 2={null_order=null, predicand={column={name=col1, table_ref=F4}}, sort_order=asc}, 3={null_order=null, predicand={column={name=col12, table_ref=F4}}, sort_order=desc}, 4={null_order=null, predicand={column={name=missing, table_ref=null}}, sort_order=desc}}, from={join={1={table={alias=F4, query={select={1={column={name=a, table_ref=null}, alias=col1}, 2={column={name=b, table_ref=null}, alias=col4}, 3={column={name=col6, table_ref=null}}, 4={column={name=col8, table_ref=null}}, 5={column={name=col12, table_ref=null}}}, from={table={alias=null, table=third}}}}}, 2={join=join, on={and={1={condition={left={column={name=col1, table_ref=F4}}, right={column={name=col2, table_ref=t}}, operator==}}, 2={condition={left={column={name=col4, table_ref=F4}}, right={column={name=col5, table_ref=t}}, operator==}}}}}, 3={table={alias=t, table=fourth}}}}, where={and={1={condition={left={column={name=col6, table_ref=F4}}, right={column={name=col7, table_ref=t}}, operator==}}, 2={condition={left={column={name=col1, table_ref=F4}}, right={column={name=col2, table_ref=t}}, operator==}}, 3={condition={left={column={name=col8, table_ref=F4}}, right={literal=0}, operator=>}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[last, tcol2, col3]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{third={a=[[@19,65:65='a',<335>,2:10]], b=[[@23,76:76='b',<335>,2:21]], col8=[[@29,93:96='col8',<335>,2:38]], col12=[[@31,99:103='col12',<335>,2:44]], col6=[[@27,87:90='col6',<335>,2:32]]}, fourth={missing=[[@94,289:295='missing',<335>,5:51]], col7=[[@60,194:194='t',<335>,4:17]], col5=[[@52,170:170='t',<335>,3:50]], col2=[[@7,25:25='t',<335>,1:25], [@44,149:149='t',<335>,3:29], [@68,215:215='t',<335>,4:38]], col3=[[@13,42:42='t',<335>,1:42], [@79,248:248='t',<335>,5:10]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={col8=[[@29,93:96='col8',<335>,2:38], [@72,226:227='F4',<335>,4:49]], col12=[[@31,99:103='col12',<335>,2:44], [@89,274:275='F4',<335>,5:36]], col6=[[@27,87:90='col6',<335>,2:32], [@56,184:185='F4',<335>,4:7]], col4=[[@25,81:84='col4',<335>,2:26], [@48,160:161='F4',<335>,3:40]], col1=[[@21,70:73='col1',<335>,2:15], [@1,8:9='F4',<335>,1:8], [@40,139:140='F4',<335>,3:19], [@64,205:206='F4',<335>,4:28], [@84,261:262='F4',<335>,5:23]]}, query1={last=[[@5,19:22='last',<101>,1:19]], tcol2=[[@11,35:39='tcol2',<335>,1:35]], col3=[[@15,44:47='col3',<335>,1:44]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query1={query_dictionary={last=[[@5,19:22='last',<101>,1:19]], tcol2=[[@11,35:39='tcol2',<335>,1:35]], col3=[[@15,44:47='col3',<335>,1:44]]}, table_dictionary={fourth={missing=[[@94,289:295='missing',<335>,5:51]], col7=[[@60,194:194='t',<335>,4:17]], col5=[[@52,170:170='t',<335>,3:50]], col2=[[@7,25:25='t',<335>,1:25], [@44,149:149='t',<335>,3:29], [@68,215:215='t',<335>,4:38]], col3=[[@13,42:42='t',<335>,1:42], [@79,248:248='t',<335>,5:10]]}}, def_query0={query_dictionary={col8=[[@29,93:96='col8',<335>,2:38], [@72,226:227='F4',<335>,4:49]], col12=[[@31,99:103='col12',<335>,2:44], [@89,274:275='F4',<335>,5:36]], col6=[[@27,87:90='col6',<335>,2:32], [@56,184:185='F4',<335>,4:7]], col4=[[@25,81:84='col4',<335>,2:26], [@48,160:161='F4',<335>,3:40]], col1=[[@21,70:73='col1',<335>,2:15], [@1,8:9='F4',<335>,1:8], [@40,139:140='F4',<335>,3:19], [@64,205:206='F4',<335>,4:28], [@84,261:262='F4',<335>,5:23]]}, table_dictionary={third={a=[[@19,65:65='a',<335>,2:10]], b=[[@23,76:76='b',<335>,2:21]], col8=[[@29,93:96='col8',<335>,2:38]], col12=[[@31,99:103='col12',<335>,2:44]], col6=[[@27,87:90='col6',<335>,2:32]]}}, interface={col8=[{name=col8, table_ref=third}], col12=[{name=col12, table_ref=third}], col6=[{name=col6, table_ref=third}], col4=[{name=b, table_ref=third}], col1=[{name=a, table_ref=third}]}}, ordered_by=[{name=col3, table_ref=t}, {name=col1, table_ref=F4}, {name=col12, table_ref=F4}, {name=missing, table_ref=fourth}], filters=[{name=col1, table_ref=F4}, {name=col2, table_ref=t}, {name=col4, table_ref=F4}, {name=col5, table_ref=t}, {name=col6, table_ref=F4}, {name=col7, table_ref=t}, {name=col8, table_ref=F4}], interface={last=[{name=col1, table_ref=F4}], tcol2=[{name=col2, table_ref=t}], col3=[{name=col3, table_ref=t}]}, table_alias={t=fourth, F4=query0}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void subqueryDictionaryExtensionQualifyClauseMissingUnqualifiedAmbiguousV36() {
		final String query = " SELECT F4.col1 as last, t.col2 as tcol2, t.col3 FROM "
			+ "\n  (select a as col1, b as col4, col6, col8, col9 from third) F4"
			+ "\n  join fourth t on F4.col1 = t.col2 and F4.col4 = t.col5"
			+ "\n  join fifth f on F4.col1 = f.col2"
			+ "\n where F4.col6 = t.col7 and F4.col1 = t.col2 and F4.col8 > 0"
			+ "\n qualify F4.col9 = t.col10 and F4.col8 > 1 and missing > 1";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		Snippet snippet = extractor.getSnippet();
		assertFatalDiagnosticCount(
				snippet,
				"QUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIAS",
				null,
				null,
				0);
		assertDiagnosticCountBySeverity(
				snippet,
				"UNRESOLVED_UNQUALIFIED_COLUMNS",
				ParseDiagnostic.Severity.ERROR,
				null,
				"missing",
				1);
		assertDiagnosticCountBySeverity(
				snippet,
				"AMBIGUOUS_COLUMN_REFERENCE",
				ParseDiagnostic.Severity.SEVERE_WARNING,
				null,
				"missing",
				1);
	}


	@Test
	public void subqueryDictionaryExtensionAggregateGroupByMissingUnqualifiedAmbiguousV37() {
		final String query = " SELECT F4.col1 as last, t.col2 as tcol2, t.col3, sum(F4.col11) as total_col11 FROM "
			+ "\n  (select a as col1, b as col4, col6, col8, col11 from third) F4"
			+ "\n  join fourth t on F4.col1 = t.col2 and F4.col4 = t.col5"
			+ "\n  join fifth f on F4.col1 = f.col2"
			+ "\n where F4.col6 = t.col7 and F4.col1 = t.col2 and F4.col8 > 0"
			+ "\n group by F4.col1, t.col2, t.col3, missing";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		Snippet snippet = extractor.getSnippet();
		assertFatalDiagnosticCount(
				snippet,
				"QUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIAS",
				null,
				null,
				0);
		assertDiagnosticCountBySeverity(
				snippet,
				"UNRESOLVED_UNQUALIFIED_COLUMNS",
				ParseDiagnostic.Severity.ERROR,
				null,
				"missing",
				1);
		assertDiagnosticCountBySeverity(
				snippet,
				"AMBIGUOUS_COLUMN_REFERENCE",
				ParseDiagnostic.Severity.SEVERE_WARNING,
				null,
				"missing",
				1);
	}


	@Test
	public void subqueryDictionaryExtensionOrderByMissingUnqualifiedAmbiguousV38() {
		final String query = " SELECT F4.col1 as last, t.col2 as tcol2, t.col3 FROM "
			+ "\n  (select a as col1, b as col4, col6, col8, col12 from third) F4"
			+ "\n  join fourth t on F4.col1 = t.col2 and F4.col4 = t.col5"
			+ "\n  join fifth f on F4.col1 = f.col2"
			+ "\n where F4.col6 = t.col7 and F4.col1 = t.col2 and F4.col8 > 0"
			+ "\n order by t.col3 desc, F4.col1 asc, F4.col12 desc, missing desc";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		Snippet snippet = extractor.getSnippet();
		assertFatalDiagnosticCount(
				snippet,
				"QUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIAS",
				null,
				null,
				0);
		assertDiagnosticCountBySeverity(
				snippet,
				"UNRESOLVED_UNQUALIFIED_COLUMNS",
				ParseDiagnostic.Severity.ERROR,
				null,
				"missing",
				1);
		assertDiagnosticCountBySeverity(
				snippet,
				"AMBIGUOUS_COLUMN_REFERENCE",
				ParseDiagnostic.Severity.SEVERE_WARNING,
				null,
				"missing",
				1);
	}


	@Test
	public void unionWithDuplicateColumnNameTest() {
		final String query = "SELECT 'Guide' AS app_name,  category, is_active, nk, rank, desc, student " + 
				"FROM  <Guide> AS Guide_Student_Conditions " + 
				"\n UNION ALL " + 
				"\n SELECT 'Nav' AS app_name, category, is_active, nk, rank, desc, student " + 
				"FROM <NAV> AS  Nav_Student_Conditions";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={union={1={select={1={alias=app_name, literal='Guide'}, 2={column={name=category, table_ref=null}}, 3={column={name=is_active, table_ref=null}}, 4={column={name=nk, table_ref=null}}, 5={column={name=rank, table_ref=null}}, 6={column={name=desc, table_ref=null}}, 7={column={name=student, table_ref=null}}}, from={table={alias=Guide_Student_Conditions, substitution={name=<Guide>, type=tuple}}}}, 2={union={qualifier=ALL, operator=UNION}}, 3={select={1={alias=app_name, literal='Nav'}, 2={column={name=category, table_ref=null}}, 3={column={name=is_active, table_ref=null}}, 4={column={name=nk, table_ref=null}}, 5={column={name=rank, table_ref=null}}, 6={column={name=desc, table_ref=null}}, 7={column={name=student, table_ref=null}}}, from={table={alias=Nav_Student_Conditions, substitution={name=<NAV>, type=tuple}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[app_name, is_active, student, rank, category, nk, desc]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<Guide>=tuple, <NAV>=tuple}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{<Guide>={is_active=[[@7,39:47='is_active',<335>,1:39]], student=[[@15,66:72='student',<335>,1:66]], rank=[[@11,54:57='rank',<127>,1:54]], category=[[@5,29:36='category',<335>,1:29]], nk=[[@9,50:51='nk',<335>,1:50]], desc=[[@13,60:63='desc',<76>,1:60]]}, <NAV>={is_active=[[@29,166:174='is_active',<335>,3:37]], student=[[@37,193:199='student',<335>,3:64]], rank=[[@33,181:184='rank',<127>,3:52]], category=[[@27,156:163='category',<335>,3:27]], nk=[[@31,177:178='nk',<335>,3:48]], desc=[[@35,187:190='desc',<76>,3:58]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={app_name=[[@3,18:25='app_name',<335>,1:18]], is_active=[[@7,39:47='is_active',<335>,1:39]], student=[[@15,66:72='student',<335>,1:66]], rank=[[@11,54:57='rank',<127>,1:54]], category=[[@5,29:36='category',<335>,1:29]], nk=[[@9,50:51='nk',<335>,1:50]], desc=[[@13,60:63='desc',<76>,1:60]]}, query1={app_name=[[@25,146:153='app_name',<335>,3:17]], is_active=[[@29,166:174='is_active',<335>,3:37]], student=[[@37,193:199='student',<335>,3:64]], rank=[[@33,181:184='rank',<127>,3:52]], category=[[@27,156:163='category',<335>,3:27]], nk=[[@31,177:178='nk',<335>,3:48]], desc=[[@35,187:190='desc',<76>,3:58]]}}",
						extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{union2={query0={query_dictionary={app_name=[[@3,18:25='app_name',<335>,1:18]], is_active=[[@7,39:47='is_active',<335>,1:39]], student=[[@15,66:72='student',<335>,1:66]], rank=[[@11,54:57='rank',<127>,1:54]], category=[[@5,29:36='category',<335>,1:29]], nk=[[@9,50:51='nk',<335>,1:50]], desc=[[@13,60:63='desc',<76>,1:60]]}, table_dictionary={<Guide>={is_active=[[@7,39:47='is_active',<335>,1:39]], student=[[@15,66:72='student',<335>,1:66]], rank=[[@11,54:57='rank',<127>,1:54]], category=[[@5,29:36='category',<335>,1:29]], nk=[[@9,50:51='nk',<335>,1:50]], desc=[[@13,60:63='desc',<76>,1:60]]}}, interface={app_name=[], is_active=[{name=is_active, table_ref=<Guide>}], student=[{name=student, table_ref=<Guide>}], rank=[{name=rank, table_ref=<Guide>}], category=[{name=category, table_ref=<Guide>}], nk=[{name=nk, table_ref=<Guide>}], desc=[{name=desc, table_ref=<Guide>}]}, table_alias={Guide_Student_Conditions=<Guide>}}, interface={app_name=query_column, is_active=query_column, student=query_column, rank=query_column, category=query_column, nk=query_column, desc=query_column}, query1={query_dictionary={app_name=[[@25,146:153='app_name',<335>,3:17]], is_active=[[@29,166:174='is_active',<335>,3:37]], student=[[@37,193:199='student',<335>,3:64]], rank=[[@33,181:184='rank',<127>,3:52]], category=[[@27,156:163='category',<335>,3:27]], nk=[[@31,177:178='nk',<335>,3:48]], desc=[[@35,187:190='desc',<76>,3:58]]}, table_dictionary={<NAV>={is_active=[[@29,166:174='is_active',<335>,3:37]], student=[[@37,193:199='student',<335>,3:64]], rank=[[@33,181:184='rank',<127>,3:52]], category=[[@27,156:163='category',<335>,3:27]], nk=[[@31,177:178='nk',<335>,3:48]], desc=[[@35,187:190='desc',<76>,3:58]]}}, interface={app_name=[], is_active=[{name=is_active, table_ref=<NAV>}], student=[{name=student, table_ref=<NAV>}], rank=[{name=rank, table_ref=<NAV>}], category=[{name=category, table_ref=<NAV>}], nk=[{name=nk, table_ref=<NAV>}], desc=[{name=desc, table_ref=<NAV>}]}, table_alias={Nav_Student_Conditions=<NAV>}}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void multipleUnionWithDuplicateColumnNameTest() {
		final String query = "SELECT 'Guide' AS app_name,  category, is_active, nk, rank, desc, student " + 
				"FROM  <Guide> AS Guide_Student_Conditions " + 
				"\n UNION ALL " + 
				"\n SELECT 'Nav' AS app_name, category, is_active, nk, rank, desc, student " + 
				"FROM <NAV> AS  Nav_Student_Conditions " +
				"\n UNION ALL " + 
				"\n SELECT 'Impl' AS app_name, category, is_active, nk, rank, desc, student " + 
				"FROM <IMPL> AS  IMPL_Student_Conditions";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={union={1={select={1={alias=app_name, literal='Guide'}, 2={column={name=category, table_ref=null}}, 3={column={name=is_active, table_ref=null}}, 4={column={name=nk, table_ref=null}}, 5={column={name=rank, table_ref=null}}, 6={column={name=desc, table_ref=null}}, 7={column={name=student, table_ref=null}}}, from={table={alias=Guide_Student_Conditions, substitution={name=<Guide>, type=tuple}}}}, 2={union={qualifier=ALL, operator=UNION}}, 3={select={1={alias=app_name, literal='Nav'}, 2={column={name=category, table_ref=null}}, 3={column={name=is_active, table_ref=null}}, 4={column={name=nk, table_ref=null}}, 5={column={name=rank, table_ref=null}}, 6={column={name=desc, table_ref=null}}, 7={column={name=student, table_ref=null}}}, from={table={alias=Nav_Student_Conditions, substitution={name=<NAV>, type=tuple}}}}, 4={union={qualifier=ALL, operator=UNION}}, 5={select={1={alias=app_name, literal='Impl'}, 2={column={name=category, table_ref=null}}, 3={column={name=is_active, table_ref=null}}, 4={column={name=nk, table_ref=null}}, 5={column={name=rank, table_ref=null}}, 6={column={name=desc, table_ref=null}}, 7={column={name=student, table_ref=null}}}, from={table={alias=IMPL_Student_Conditions, substitution={name=<IMPL>, type=tuple}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[app_name, is_active, student, rank, category, nk, desc]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<Guide>=tuple, <NAV>=tuple, <IMPL>=tuple}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{<Guide>={is_active=[[@7,39:47='is_active',<335>,1:39]], student=[[@15,66:72='student',<335>,1:66]], rank=[[@11,54:57='rank',<127>,1:54]], category=[[@5,29:36='category',<335>,1:29]], nk=[[@9,50:51='nk',<335>,1:50]], desc=[[@13,60:63='desc',<76>,1:60]]}, <NAV>={is_active=[[@29,166:174='is_active',<335>,3:37]], student=[[@37,193:199='student',<335>,3:64]], rank=[[@33,181:184='rank',<127>,3:52]], category=[[@27,156:163='category',<335>,3:27]], nk=[[@31,177:178='nk',<335>,3:48]], desc=[[@35,187:190='desc',<76>,3:58]]}, <IMPL>={is_active=[[@51,290:298='is_active',<335>,5:38]], student=[[@59,317:323='student',<335>,5:65]], rank=[[@55,305:308='rank',<127>,5:53]], category=[[@49,280:287='category',<335>,5:28]], nk=[[@53,301:302='nk',<335>,5:49]], desc=[[@57,311:314='desc',<76>,5:59]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={app_name=[[@3,18:25='app_name',<335>,1:18]], is_active=[[@7,39:47='is_active',<335>,1:39]], student=[[@15,66:72='student',<335>,1:66]], rank=[[@11,54:57='rank',<127>,1:54]], category=[[@5,29:36='category',<335>,1:29]], nk=[[@9,50:51='nk',<335>,1:50]], desc=[[@13,60:63='desc',<76>,1:60]]}, query1={app_name=[[@25,146:153='app_name',<335>,3:17]], is_active=[[@29,166:174='is_active',<335>,3:37]], student=[[@37,193:199='student',<335>,3:64]], rank=[[@33,181:184='rank',<127>,3:52]], category=[[@27,156:163='category',<335>,3:27]], nk=[[@31,177:178='nk',<335>,3:48]], desc=[[@35,187:190='desc',<76>,3:58]]}, query2={app_name=[[@47,270:277='app_name',<335>,5:18]], is_active=[[@51,290:298='is_active',<335>,5:38]], student=[[@59,317:323='student',<335>,5:65]], rank=[[@55,305:308='rank',<127>,5:53]], category=[[@49,280:287='category',<335>,5:28]], nk=[[@53,301:302='nk',<335>,5:49]], desc=[[@57,311:314='desc',<76>,5:59]]}}",
						extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{union3={query0={query_dictionary={app_name=[[@3,18:25='app_name',<335>,1:18]], is_active=[[@7,39:47='is_active',<335>,1:39]], student=[[@15,66:72='student',<335>,1:66]], rank=[[@11,54:57='rank',<127>,1:54]], category=[[@5,29:36='category',<335>,1:29]], nk=[[@9,50:51='nk',<335>,1:50]], desc=[[@13,60:63='desc',<76>,1:60]]}, table_dictionary={<Guide>={is_active=[[@7,39:47='is_active',<335>,1:39]], student=[[@15,66:72='student',<335>,1:66]], rank=[[@11,54:57='rank',<127>,1:54]], category=[[@5,29:36='category',<335>,1:29]], nk=[[@9,50:51='nk',<335>,1:50]], desc=[[@13,60:63='desc',<76>,1:60]]}}, interface={app_name=[], is_active=[{name=is_active, table_ref=<Guide>}], student=[{name=student, table_ref=<Guide>}], rank=[{name=rank, table_ref=<Guide>}], category=[{name=category, table_ref=<Guide>}], nk=[{name=nk, table_ref=<Guide>}], desc=[{name=desc, table_ref=<Guide>}]}, table_alias={Guide_Student_Conditions=<Guide>}}, interface={app_name=query_column, is_active=query_column, student=query_column, rank=query_column, category=query_column, nk=query_column, desc=query_column}, query1={query_dictionary={app_name=[[@25,146:153='app_name',<335>,3:17]], is_active=[[@29,166:174='is_active',<335>,3:37]], student=[[@37,193:199='student',<335>,3:64]], rank=[[@33,181:184='rank',<127>,3:52]], category=[[@27,156:163='category',<335>,3:27]], nk=[[@31,177:178='nk',<335>,3:48]], desc=[[@35,187:190='desc',<76>,3:58]]}, table_dictionary={<NAV>={is_active=[[@29,166:174='is_active',<335>,3:37]], student=[[@37,193:199='student',<335>,3:64]], rank=[[@33,181:184='rank',<127>,3:52]], category=[[@27,156:163='category',<335>,3:27]], nk=[[@31,177:178='nk',<335>,3:48]], desc=[[@35,187:190='desc',<76>,3:58]]}}, interface={app_name=[], is_active=[{name=is_active, table_ref=<NAV>}], student=[{name=student, table_ref=<NAV>}], rank=[{name=rank, table_ref=<NAV>}], category=[{name=category, table_ref=<NAV>}], nk=[{name=nk, table_ref=<NAV>}], desc=[{name=desc, table_ref=<NAV>}]}, table_alias={Nav_Student_Conditions=<NAV>}}, query2={query_dictionary={app_name=[[@47,270:277='app_name',<335>,5:18]], is_active=[[@51,290:298='is_active',<335>,5:38]], student=[[@59,317:323='student',<335>,5:65]], rank=[[@55,305:308='rank',<127>,5:53]], category=[[@49,280:287='category',<335>,5:28]], nk=[[@53,301:302='nk',<335>,5:49]], desc=[[@57,311:314='desc',<76>,5:59]]}, table_dictionary={<IMPL>={is_active=[[@51,290:298='is_active',<335>,5:38]], student=[[@59,317:323='student',<335>,5:65]], rank=[[@55,305:308='rank',<127>,5:53]], category=[[@49,280:287='category',<335>,5:28]], nk=[[@53,301:302='nk',<335>,5:49]], desc=[[@57,311:314='desc',<76>,5:59]]}}, interface={app_name=[], is_active=[{name=is_active, table_ref=<IMPL>}], student=[{name=student, table_ref=<IMPL>}], rank=[{name=rank, table_ref=<IMPL>}], category=[{name=category, table_ref=<IMPL>}], nk=[{name=nk, table_ref=<IMPL>}], desc=[{name=desc, table_ref=<IMPL>}]}, table_alias={IMPL_Student_Conditions=<IMPL>}}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void intersectWithDuplicateColumnNameTest() {
		final String query = "SELECT 'Guide' AS app_name,  category, is_active, nk, rank, desc, student " + 
				"FROM  <Guide> AS Guide_Student_Conditions " + 
				"\n intersect " + 
				"\n SELECT 'Nav' AS app_name, category, is_active, nk, rank, desc, student " + 
				"FROM <NAV> AS  Nav_Student_Conditions";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={intersect={1={select={1={alias=app_name, literal='Guide'}, 2={column={name=category, table_ref=null}}, 3={column={name=is_active, table_ref=null}}, 4={column={name=nk, table_ref=null}}, 5={column={name=rank, table_ref=null}}, 6={column={name=desc, table_ref=null}}, 7={column={name=student, table_ref=null}}}, from={table={alias=Guide_Student_Conditions, substitution={name=<Guide>, type=tuple}}}}, 2={intersect={qualifier=null, operator=intersect}}, 3={select={1={alias=app_name, literal='Nav'}, 2={column={name=category, table_ref=null}}, 3={column={name=is_active, table_ref=null}}, 4={column={name=nk, table_ref=null}}, 5={column={name=rank, table_ref=null}}, 6={column={name=desc, table_ref=null}}, 7={column={name=student, table_ref=null}}}, from={table={alias=Nav_Student_Conditions, substitution={name=<NAV>, type=tuple}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[app_name, is_active, student, rank, category, nk, desc]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<Guide>=tuple, <NAV>=tuple}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{<Guide>={is_active=[[@7,39:47='is_active',<335>,1:39]], student=[[@15,66:72='student',<335>,1:66]], rank=[[@11,54:57='rank',<127>,1:54]], category=[[@5,29:36='category',<335>,1:29]], nk=[[@9,50:51='nk',<335>,1:50]], desc=[[@13,60:63='desc',<76>,1:60]]}, <NAV>={is_active=[[@28,166:174='is_active',<335>,3:37]], student=[[@36,193:199='student',<335>,3:64]], rank=[[@32,181:184='rank',<127>,3:52]], category=[[@26,156:163='category',<335>,3:27]], nk=[[@30,177:178='nk',<335>,3:48]], desc=[[@34,187:190='desc',<76>,3:58]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={app_name=[[@3,18:25='app_name',<335>,1:18]], is_active=[[@7,39:47='is_active',<335>,1:39]], student=[[@15,66:72='student',<335>,1:66]], rank=[[@11,54:57='rank',<127>,1:54]], category=[[@5,29:36='category',<335>,1:29]], nk=[[@9,50:51='nk',<335>,1:50]], desc=[[@13,60:63='desc',<76>,1:60]]}, query1={app_name=[[@24,146:153='app_name',<335>,3:17]], is_active=[[@28,166:174='is_active',<335>,3:37]], student=[[@36,193:199='student',<335>,3:64]], rank=[[@32,181:184='rank',<127>,3:52]], category=[[@26,156:163='category',<335>,3:27]], nk=[[@30,177:178='nk',<335>,3:48]], desc=[[@34,187:190='desc',<76>,3:58]]}}",
						extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{intersect2={query0={query_dictionary={app_name=[[@3,18:25='app_name',<335>,1:18]], is_active=[[@7,39:47='is_active',<335>,1:39]], student=[[@15,66:72='student',<335>,1:66]], rank=[[@11,54:57='rank',<127>,1:54]], category=[[@5,29:36='category',<335>,1:29]], nk=[[@9,50:51='nk',<335>,1:50]], desc=[[@13,60:63='desc',<76>,1:60]]}, table_dictionary={<Guide>={is_active=[[@7,39:47='is_active',<335>,1:39]], student=[[@15,66:72='student',<335>,1:66]], rank=[[@11,54:57='rank',<127>,1:54]], category=[[@5,29:36='category',<335>,1:29]], nk=[[@9,50:51='nk',<335>,1:50]], desc=[[@13,60:63='desc',<76>,1:60]]}}, interface={app_name=[], is_active=[{name=is_active, table_ref=<Guide>}], student=[{name=student, table_ref=<Guide>}], rank=[{name=rank, table_ref=<Guide>}], category=[{name=category, table_ref=<Guide>}], nk=[{name=nk, table_ref=<Guide>}], desc=[{name=desc, table_ref=<Guide>}]}, table_alias={Guide_Student_Conditions=<Guide>}}, interface={app_name=query_column, is_active=query_column, student=query_column, rank=query_column, category=query_column, nk=query_column, desc=query_column}, query1={query_dictionary={app_name=[[@24,146:153='app_name',<335>,3:17]], is_active=[[@28,166:174='is_active',<335>,3:37]], student=[[@36,193:199='student',<335>,3:64]], rank=[[@32,181:184='rank',<127>,3:52]], category=[[@26,156:163='category',<335>,3:27]], nk=[[@30,177:178='nk',<335>,3:48]], desc=[[@34,187:190='desc',<76>,3:58]]}, table_dictionary={<NAV>={is_active=[[@28,166:174='is_active',<335>,3:37]], student=[[@36,193:199='student',<335>,3:64]], rank=[[@32,181:184='rank',<127>,3:52]], category=[[@26,156:163='category',<335>,3:27]], nk=[[@30,177:178='nk',<335>,3:48]], desc=[[@34,187:190='desc',<76>,3:58]]}}, interface={app_name=[], is_active=[{name=is_active, table_ref=<NAV>}], student=[{name=student, table_ref=<NAV>}], rank=[{name=rank, table_ref=<NAV>}], category=[{name=category, table_ref=<NAV>}], nk=[{name=nk, table_ref=<NAV>}], desc=[{name=desc, table_ref=<NAV>}]}, table_alias={Nav_Student_Conditions=<NAV>}}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void unionWithMismatchColumnCountsAndNamesTest() {
		final String query = "SELECT rank, desc, student " + 
				"FROM  <Guide> AS Guide_Student_Conditions " + 
				"\n union " + 
				"\n SELECT 'Nav' AS app_name, category, is_active, nk " + 
				"FROM <NAV> AS  Nav_Student_Conditions";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		Snippet snippet = extractor.getSnippet();
		assertFatalDiagnosticCount(
				snippet,
				"SET_OPERATION_INTERFACE_COLUMN_COUNT_MISMATCH",
				"UNION has different column counts. Expected 3 columns (rank, desc, student) at (l:1 c:7) but there were 4 (app_name, category, is_active, nk) at (l:3 c:17)",
				null,
				1);
		
		Assert.assertEquals("AST is wrong", "{SQL={union={1={select={1={column={name=rank, table_ref=null}}, 2={column={name=desc, table_ref=null}}, 3={column={name=student, table_ref=null}}}, from={table={alias=Guide_Student_Conditions, substitution={name=<Guide>, type=tuple}}}}, 2={union={qualifier=null, operator=union}}, 3={select={1={alias=app_name, literal='Nav'}, 2={column={name=category, table_ref=null}}, 3={column={name=is_active, table_ref=null}}, 4={column={name=nk, table_ref=null}}}, from={table={alias=Nav_Student_Conditions, substitution={name=<NAV>, type=tuple}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[student, rank, desc]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<Guide>=tuple, <NAV>=tuple}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{<Guide>={student=[[@5,19:25='student',<335>,1:19]], rank=[[@1,7:10='rank',<127>,1:7]], desc=[[@3,13:16='desc',<76>,1:13]]}, <NAV>={is_active=[[@18,115:123='is_active',<335>,3:37]], category=[[@16,105:112='category',<335>,3:27]], nk=[[@20,126:127='nk',<335>,3:48]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={student=[[@5,19:25='student',<335>,1:19]], rank=[[@1,7:10='rank',<127>,1:7]], desc=[[@3,13:16='desc',<76>,1:13]]}, query1={app_name=[[@14,95:102='app_name',<335>,3:17]], is_active=[[@18,115:123='is_active',<335>,3:37]], category=[[@16,105:112='category',<335>,3:27]], nk=[[@20,126:127='nk',<335>,3:48]]}}",
						extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{union2={query0={query_dictionary={student=[[@5,19:25='student',<335>,1:19]], rank=[[@1,7:10='rank',<127>,1:7]], desc=[[@3,13:16='desc',<76>,1:13]]}, table_dictionary={<Guide>={student=[[@5,19:25='student',<335>,1:19]], rank=[[@1,7:10='rank',<127>,1:7]], desc=[[@3,13:16='desc',<76>,1:13]]}}, interface={student=[{name=student, table_ref=<Guide>}], rank=[{name=rank, table_ref=<Guide>}], desc=[{name=desc, table_ref=<Guide>}]}, table_alias={Guide_Student_Conditions=<Guide>}}, interface={student=query_column, rank=query_column, desc=query_column}, query1={query_dictionary={app_name=[[@14,95:102='app_name',<335>,3:17]], is_active=[[@18,115:123='is_active',<335>,3:37]], category=[[@16,105:112='category',<335>,3:27]], nk=[[@20,126:127='nk',<335>,3:48]]}, table_dictionary={<NAV>={is_active=[[@18,115:123='is_active',<335>,3:37]], category=[[@16,105:112='category',<335>,3:27]], nk=[[@20,126:127='nk',<335>,3:48]]}}, interface={app_name=[], is_active=[{name=is_active, table_ref=<NAV>}], category=[{name=category, table_ref=<NAV>}], nk=[{name=nk, table_ref=<NAV>}]}, table_alias={Nav_Student_Conditions=<NAV>}}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void intersectionWithMismatchColumnCountsAndNamesTest() {
		final String query = "SELECT rank, desc, student " + 
				"FROM  <Guide> AS Guide_Student_Conditions " + 
				"\n intersect " + 
				"\n SELECT 'Nav' AS app_name, category, is_active, nk " + 
				"FROM <NAV> AS  Nav_Student_Conditions";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		Snippet snippet = extractor.getSnippet();
		assertFatalDiagnosticCount(
				snippet,
				"SET_OPERATION_INTERFACE_COLUMN_COUNT_MISMATCH",
				"INTERSECTION has different column counts. Expected 3 columns (rank, desc, student) at (l:1 c:7) but there were 4 (app_name, category, is_active, nk) at (l:3 c:17)",
				null,
				1);
		
		Assert.assertEquals("AST is wrong", "{SQL={intersect={1={select={1={column={name=rank, table_ref=null}}, 2={column={name=desc, table_ref=null}}, 3={column={name=student, table_ref=null}}}, from={table={alias=Guide_Student_Conditions, substitution={name=<Guide>, type=tuple}}}}, 2={intersect={qualifier=null, operator=intersect}}, 3={select={1={alias=app_name, literal='Nav'}, 2={column={name=category, table_ref=null}}, 3={column={name=is_active, table_ref=null}}, 4={column={name=nk, table_ref=null}}}, from={table={alias=Nav_Student_Conditions, substitution={name=<NAV>, type=tuple}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[student, rank, desc]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<Guide>=tuple, <NAV>=tuple}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{<Guide>={student=[[@5,19:25='student',<335>,1:19]], rank=[[@1,7:10='rank',<127>,1:7]], desc=[[@3,13:16='desc',<76>,1:13]]}, <NAV>={is_active=[[@18,119:127='is_active',<335>,3:37]], category=[[@16,109:116='category',<335>,3:27]], nk=[[@20,130:131='nk',<335>,3:48]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={student=[[@5,19:25='student',<335>,1:19]], rank=[[@1,7:10='rank',<127>,1:7]], desc=[[@3,13:16='desc',<76>,1:13]]}, query1={app_name=[[@14,99:106='app_name',<335>,3:17]], is_active=[[@18,119:127='is_active',<335>,3:37]], category=[[@16,109:116='category',<335>,3:27]], nk=[[@20,130:131='nk',<335>,3:48]]}}",
						extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{intersect2={query0={query_dictionary={student=[[@5,19:25='student',<335>,1:19]], rank=[[@1,7:10='rank',<127>,1:7]], desc=[[@3,13:16='desc',<76>,1:13]]}, table_dictionary={<Guide>={student=[[@5,19:25='student',<335>,1:19]], rank=[[@1,7:10='rank',<127>,1:7]], desc=[[@3,13:16='desc',<76>,1:13]]}}, interface={student=[{name=student, table_ref=<Guide>}], rank=[{name=rank, table_ref=<Guide>}], desc=[{name=desc, table_ref=<Guide>}]}, table_alias={Guide_Student_Conditions=<Guide>}}, interface={student=query_column, rank=query_column, desc=query_column}, query1={query_dictionary={app_name=[[@14,99:106='app_name',<335>,3:17]], is_active=[[@18,119:127='is_active',<335>,3:37]], category=[[@16,109:116='category',<335>,3:27]], nk=[[@20,130:131='nk',<335>,3:48]]}, table_dictionary={<NAV>={is_active=[[@18,119:127='is_active',<335>,3:37]], category=[[@16,109:116='category',<335>,3:27]], nk=[[@20,130:131='nk',<335>,3:48]]}}, interface={app_name=[], is_active=[{name=is_active, table_ref=<NAV>}], category=[{name=category, table_ref=<NAV>}], nk=[{name=nk, table_ref=<NAV>}]}, table_alias={Nav_Student_Conditions=<NAV>}}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void intersectWithDuplicateColumnNameTestv2() {
		final String query = "SELECT 'Guide' AS app_name,  category, is_active, nk, rank, desc, student " + 
				"FROM  <Guide> AS Guide_Student_Conditions " + 
				"\n intersect " + 
				"\n SELECT 'Nav' AS app_name, category, is_active, nk, rank, desc, student " + 
				"FROM <NAV> AS  Nav_Student_Conditions";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={intersect={1={select={1={alias=app_name, literal='Guide'}, 2={column={name=category, table_ref=null}}, 3={column={name=is_active, table_ref=null}}, 4={column={name=nk, table_ref=null}}, 5={column={name=rank, table_ref=null}}, 6={column={name=desc, table_ref=null}}, 7={column={name=student, table_ref=null}}}, from={table={alias=Guide_Student_Conditions, substitution={name=<Guide>, type=tuple}}}}, 2={intersect={qualifier=null, operator=intersect}}, 3={select={1={alias=app_name, literal='Nav'}, 2={column={name=category, table_ref=null}}, 3={column={name=is_active, table_ref=null}}, 4={column={name=nk, table_ref=null}}, 5={column={name=rank, table_ref=null}}, 6={column={name=desc, table_ref=null}}, 7={column={name=student, table_ref=null}}}, from={table={alias=Nav_Student_Conditions, substitution={name=<NAV>, type=tuple}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[app_name, is_active, student, rank, category, nk, desc]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<Guide>=tuple, <NAV>=tuple}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{<Guide>={is_active=[[@7,39:47='is_active',<335>,1:39]], student=[[@15,66:72='student',<335>,1:66]], rank=[[@11,54:57='rank',<127>,1:54]], category=[[@5,29:36='category',<335>,1:29]], nk=[[@9,50:51='nk',<335>,1:50]], desc=[[@13,60:63='desc',<76>,1:60]]}, <NAV>={is_active=[[@28,166:174='is_active',<335>,3:37]], student=[[@36,193:199='student',<335>,3:64]], rank=[[@32,181:184='rank',<127>,3:52]], category=[[@26,156:163='category',<335>,3:27]], nk=[[@30,177:178='nk',<335>,3:48]], desc=[[@34,187:190='desc',<76>,3:58]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={app_name=[[@3,18:25='app_name',<335>,1:18]], is_active=[[@7,39:47='is_active',<335>,1:39]], student=[[@15,66:72='student',<335>,1:66]], rank=[[@11,54:57='rank',<127>,1:54]], category=[[@5,29:36='category',<335>,1:29]], nk=[[@9,50:51='nk',<335>,1:50]], desc=[[@13,60:63='desc',<76>,1:60]]}, query1={app_name=[[@24,146:153='app_name',<335>,3:17]], is_active=[[@28,166:174='is_active',<335>,3:37]], student=[[@36,193:199='student',<335>,3:64]], rank=[[@32,181:184='rank',<127>,3:52]], category=[[@26,156:163='category',<335>,3:27]], nk=[[@30,177:178='nk',<335>,3:48]], desc=[[@34,187:190='desc',<76>,3:58]]}}",
						extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{intersect2={query0={query_dictionary={app_name=[[@3,18:25='app_name',<335>,1:18]], is_active=[[@7,39:47='is_active',<335>,1:39]], student=[[@15,66:72='student',<335>,1:66]], rank=[[@11,54:57='rank',<127>,1:54]], category=[[@5,29:36='category',<335>,1:29]], nk=[[@9,50:51='nk',<335>,1:50]], desc=[[@13,60:63='desc',<76>,1:60]]}, table_dictionary={<Guide>={is_active=[[@7,39:47='is_active',<335>,1:39]], student=[[@15,66:72='student',<335>,1:66]], rank=[[@11,54:57='rank',<127>,1:54]], category=[[@5,29:36='category',<335>,1:29]], nk=[[@9,50:51='nk',<335>,1:50]], desc=[[@13,60:63='desc',<76>,1:60]]}}, interface={app_name=[], is_active=[{name=is_active, table_ref=<Guide>}], student=[{name=student, table_ref=<Guide>}], rank=[{name=rank, table_ref=<Guide>}], category=[{name=category, table_ref=<Guide>}], nk=[{name=nk, table_ref=<Guide>}], desc=[{name=desc, table_ref=<Guide>}]}, table_alias={Guide_Student_Conditions=<Guide>}}, interface={app_name=query_column, is_active=query_column, student=query_column, rank=query_column, category=query_column, nk=query_column, desc=query_column}, query1={query_dictionary={app_name=[[@24,146:153='app_name',<335>,3:17]], is_active=[[@28,166:174='is_active',<335>,3:37]], student=[[@36,193:199='student',<335>,3:64]], rank=[[@32,181:184='rank',<127>,3:52]], category=[[@26,156:163='category',<335>,3:27]], nk=[[@30,177:178='nk',<335>,3:48]], desc=[[@34,187:190='desc',<76>,3:58]]}, table_dictionary={<NAV>={is_active=[[@28,166:174='is_active',<335>,3:37]], student=[[@36,193:199='student',<335>,3:64]], rank=[[@32,181:184='rank',<127>,3:52]], category=[[@26,156:163='category',<335>,3:27]], nk=[[@30,177:178='nk',<335>,3:48]], desc=[[@34,187:190='desc',<76>,3:58]]}}, interface={app_name=[], is_active=[{name=is_active, table_ref=<NAV>}], student=[{name=student, table_ref=<NAV>}], rank=[{name=rank, table_ref=<NAV>}], category=[{name=category, table_ref=<NAV>}], nk=[{name=nk, table_ref=<NAV>}], desc=[{name=desc, table_ref=<NAV>}]}, table_alias={Nav_Student_Conditions=<NAV>}}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void simpleMultipleUnionParseTest() {

		final String query = " SELECT first FROM third " + " union select third from fifth "
				+ " union select fourth from sixth " + " union select seventh from eighth ";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={union={1={select={1={column={name=first, table_ref=null}}}, from={table={alias=null, table=third}}}, 2={union={qualifier=null, operator=union}}, 3={select={1={column={name=third, table_ref=null}}}, from={table={alias=null, table=fifth}}}, 4={union={qualifier=null, operator=union}}, 5={select={1={column={name=fourth, table_ref=null}}}, from={table={alias=null, table=sixth}}}, 6={union={qualifier=null, operator=union}}, 7={select={1={column={name=seventh, table_ref=null}}}, from={table={alias=null, table=eighth}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[first]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{sixth={fourth=[[@11,70:75='fourth',<335>,1:70]]}, third={first=[[@1,8:12='first',<87>,1:8]]}, eighth={seventh=[[@16,102:108='seventh',<335>,1:102]]}, fifth={third=[[@6,39:43='third',<335>,1:39]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={first=[[@1,8:12='first',<87>,1:8]]}, query1={third=[[@6,39:43='third',<335>,1:39]]}, query2={fourth=[[@11,70:75='fourth',<335>,1:70]]}, query3={seventh=[[@16,102:108='seventh',<335>,1:102]]}}",
						extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{union4={query0={query_dictionary={first=[[@1,8:12='first',<87>,1:8]]}, table_dictionary={third={first=[[@1,8:12='first',<87>,1:8]]}}, interface={first=[{name=first, table_ref=third}]}}, interface={first=query_column}, query1={query_dictionary={third=[[@6,39:43='third',<335>,1:39]]}, table_dictionary={fifth={third=[[@6,39:43='third',<335>,1:39]]}}, interface={third=[{name=third, table_ref=fifth}]}}, query2={query_dictionary={fourth=[[@11,70:75='fourth',<335>,1:70]]}, table_dictionary={sixth={fourth=[[@11,70:75='fourth',<335>,1:70]]}}, interface={fourth=[{name=fourth, table_ref=sixth}]}}, query3={query_dictionary={seventh=[[@16,102:108='seventh',<335>,1:102]]}, table_dictionary={eighth={seventh=[[@16,102:108='seventh',<335>,1:102]]}}, interface={seventh=[{name=seventh, table_ref=eighth}]}}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void simpleMultipleIntersectParseTest() {

		final String query = " SELECT first FROM third " + " intersect select third from fifth "
				+ " intersect select fourth from sixth " + " intersect select seventh from eighth ";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={intersect={1={select={1={column={name=first, table_ref=null}}}, from={table={alias=null, table=third}}}, 2={intersect={qualifier=null, operator=intersect}}, 3={select={1={column={name=third, table_ref=null}}}, from={table={alias=null, table=fifth}}}, 4={intersect={qualifier=null, operator=intersect}}, 5={select={1={column={name=fourth, table_ref=null}}}, from={table={alias=null, table=sixth}}}, 6={intersect={qualifier=null, operator=intersect}}, 7={select={1={column={name=seventh, table_ref=null}}}, from={table={alias=null, table=eighth}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[first]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{sixth={fourth=[[@11,78:83='fourth',<335>,1:78]]}, third={first=[[@1,8:12='first',<87>,1:8]]}, eighth={seventh=[[@16,114:120='seventh',<335>,1:114]]}, fifth={third=[[@6,43:47='third',<335>,1:43]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={first=[[@1,8:12='first',<87>,1:8]]}, query1={third=[[@6,43:47='third',<335>,1:43]]}, query2={fourth=[[@11,78:83='fourth',<335>,1:78]]}, query3={seventh=[[@16,114:120='seventh',<335>,1:114]]}}",
						extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{intersect4={query0={query_dictionary={first=[[@1,8:12='first',<87>,1:8]]}, table_dictionary={third={first=[[@1,8:12='first',<87>,1:8]]}}, interface={first=[{name=first, table_ref=third}]}}, interface={first=query_column}, query1={query_dictionary={third=[[@6,43:47='third',<335>,1:43]]}, table_dictionary={fifth={third=[[@6,43:47='third',<335>,1:43]]}}, interface={third=[{name=third, table_ref=fifth}]}}, query2={query_dictionary={fourth=[[@11,78:83='fourth',<335>,1:78]]}, table_dictionary={sixth={fourth=[[@11,78:83='fourth',<335>,1:78]]}}, interface={fourth=[{name=fourth, table_ref=sixth}]}}, query3={query_dictionary={seventh=[[@16,114:120='seventh',<335>,1:114]]}, table_dictionary={eighth={seventh=[[@16,114:120='seventh',<335>,1:114]]}}, interface={seventh=[{name=seventh, table_ref=eighth}]}}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void simpleMultipleUnion1ParseTest() {
		// adding some complexity to what otherwise is a repeat of an earlier test
		final String query = " SELECT item as first FROM third " 
			+ "\n union select x second from "
			+ "\n (select x from ninth) fifth "
			+ "\n union select fourth from sixth " 
			+ "\n union select seventh from eighth ";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={union={1={select={1={column={name=item, table_ref=null}, alias=first}}, from={table={alias=null, table=third}}}, 2={union={qualifier=null, operator=union}}, 3={select={1={column={name=x, table_ref=null}, alias=second}}, from={table={alias=fifth, query={select={1={column={name=x, table_ref=null}}}, from={table={alias=null, table=ninth}}}}}}, 4={union={qualifier=null, operator=union}}, 5={select={1={column={name=fourth, table_ref=null}}}, from={table={alias=null, table=sixth}}}, 6={union={qualifier=null, operator=union}}, 7={select={1={column={name=seventh, table_ref=null}}}, from={table={alias=null, table=eighth}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[first]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{sixth={fourth=[[@20,107:112='fourth',<335>,4:14]]}, third={item=[[@1,8:11='item',<335>,1:8]]}, ninth={x=[[@13,72:72='x',<335>,3:9]]}, eighth={seventh=[[@25,140:146='seventh',<335>,5:14]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query4={seventh=[[@25,140:146='seventh',<335>,5:14]]}, query0={first=[[@3,16:20='first',<87>,1:16]]}, query1={x=[[@13,72:72='x',<335>,3:9]]}, query2={second=[[@9,50:55='second',<134>,2:16]]}, query3={fourth=[[@20,107:112='fourth',<335>,4:14]]}}",
						extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{union5={query4={query_dictionary={seventh=[[@25,140:146='seventh',<335>,5:14]]}, table_dictionary={eighth={seventh=[[@25,140:146='seventh',<335>,5:14]]}}, interface={seventh=[{name=seventh, table_ref=eighth}]}}, query0={query_dictionary={first=[[@3,16:20='first',<87>,1:16]]}, table_dictionary={third={item=[[@1,8:11='item',<335>,1:8]]}}, interface={first=[{name=item, table_ref=third}]}}, interface={first=query_column}, query2={query_dictionary={second=[[@9,50:55='second',<134>,2:16]]}, table_dictionary={}, def_query1={query_dictionary={x=[[@13,72:72='x',<335>,3:9]]}, table_dictionary={ninth={x=[[@13,72:72='x',<335>,3:9]]}}, interface={x=[{name=x, table_ref=ninth}]}}, interface={second=[{name=x, table_ref=query1}]}, table_alias={fifth=query1}}, query3={query_dictionary={fourth=[[@20,107:112='fourth',<335>,4:14]]}, table_dictionary={sixth={fourth=[[@20,107:112='fourth',<335>,4:14]]}}, interface={fourth=[{name=fourth, table_ref=sixth}]}}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void simpleMultipleIntersect1ParseTest() {

		final String query = " SELECT x first FROM third " + " intersect select y second from fifth "
				+ " intersect select z fourth from sixth " + " intersect select omega seventh from eighth ";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={intersect={1={select={1={column={name=x, table_ref=null}, alias=first}}, from={table={alias=null, table=third}}}, 2={intersect={qualifier=null, operator=intersect}}, 3={select={1={column={name=y, table_ref=null}, alias=second}}, from={table={alias=null, table=fifth}}}, 4={intersect={qualifier=null, operator=intersect}}, 5={select={1={column={name=z, table_ref=null}, alias=fourth}}, from={table={alias=null, table=sixth}}}, 6={intersect={qualifier=null, operator=intersect}}, 7={select={1={column={name=omega, table_ref=null}, alias=seventh}}, from={table={alias=null, table=eighth}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[first]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{sixth={z=[[@13,83:83='z',<335>,1:83]]}, third={x=[[@1,8:8='x',<335>,1:8]]}, eighth={omega=[[@19,121:125='omega',<335>,1:121]]}, fifth={y=[[@7,45:45='y',<335>,1:45]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={first=[[@2,10:14='first',<87>,1:10]]}, query1={second=[[@8,47:52='second',<134>,1:47]]}, query2={fourth=[[@14,85:90='fourth',<335>,1:85]]}, query3={seventh=[[@20,127:133='seventh',<335>,1:127]]}}",
						extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{intersect4={query0={query_dictionary={first=[[@2,10:14='first',<87>,1:10]]}, table_dictionary={third={x=[[@1,8:8='x',<335>,1:8]]}}, interface={first=[{name=x, table_ref=third}]}}, interface={first=query_column}, query1={query_dictionary={second=[[@8,47:52='second',<134>,1:47]]}, table_dictionary={fifth={y=[[@7,45:45='y',<335>,1:45]]}}, interface={second=[{name=y, table_ref=fifth}]}}, query2={query_dictionary={fourth=[[@14,85:90='fourth',<335>,1:85]]}, table_dictionary={sixth={z=[[@13,83:83='z',<335>,1:83]]}}, interface={fourth=[{name=z, table_ref=sixth}]}}, query3={query_dictionary={seventh=[[@20,127:133='seventh',<335>,1:127]]}, table_dictionary={eighth={omega=[[@19,121:125='omega',<335>,1:121]]}}, interface={seventh=[{name=omega, table_ref=eighth}]}}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void simpleUnionIntersectParseTest() {

		final String query = " SELECT first FROM third " + " union select third from fifth "
				+ " intersect select fourth from sixth " + " union select seventh from eighth ";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={intersect={1={union={1={select={1={column={name=first, table_ref=null}}}, from={table={alias=null, table=third}}}, 2={union={qualifier=null, operator=union}}, 3={select={1={column={name=third, table_ref=null}}}, from={table={alias=null, table=fifth}}}}}, 2={intersect={qualifier=null, operator=intersect}}, 3={union={1={select={1={column={name=fourth, table_ref=null}}}, from={table={alias=null, table=sixth}}}, 2={union={qualifier=null, operator=union}}, 3={select={1={column={name=seventh, table_ref=null}}}, from={table={alias=null, table=eighth}}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[first]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{sixth={fourth=[[@11,74:79='fourth',<335>,1:74]]}, third={first=[[@1,8:12='first',<87>,1:8]]}, eighth={seventh=[[@16,106:112='seventh',<335>,1:106]]}, fifth={third=[[@6,39:43='third',<335>,1:39]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query4={seventh=[[@16,106:112='seventh',<335>,1:106]]}, query0={first=[[@1,8:12='first',<87>,1:8]]}, query1={third=[[@6,39:43='third',<335>,1:39]]}, query3={fourth=[[@11,74:79='fourth',<335>,1:74]]}}",
						extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{intersect6={union5={query4={query_dictionary={seventh=[[@16,106:112='seventh',<335>,1:106]]}, table_dictionary={eighth={seventh=[[@16,106:112='seventh',<335>,1:106]]}}, interface={seventh=[{name=seventh, table_ref=eighth}]}}, interface={fourth=query_column}, query3={query_dictionary={fourth=[[@11,74:79='fourth',<335>,1:74]]}, table_dictionary={sixth={fourth=[[@11,74:79='fourth',<335>,1:74]]}}, interface={fourth=[{name=fourth, table_ref=sixth}]}}}, union2={query0={query_dictionary={first=[[@1,8:12='first',<87>,1:8]]}, table_dictionary={third={first=[[@1,8:12='first',<87>,1:8]]}}, interface={first=[{name=first, table_ref=third}]}}, interface={first=query_column}, query1={query_dictionary={third=[[@6,39:43='third',<335>,1:39]]}, table_dictionary={fifth={third=[[@6,39:43='third',<335>,1:39]]}}, interface={third=[{name=third, table_ref=fifth}]}}}, interface={first=union_column}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void nestedUnionIntersectAAParseTest() {

		final String query = " SELECT first FROM ( " + "  select third from fifth "
				+ " intersect select fourth from sixth ) aa " + " union select seventh from eighth ";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={union={1={select={1={column={name=first, table_ref=null}}}, from={table={alias=aa, query={intersect={1={select={1={column={name=third, table_ref=null}}}, from={table={alias=null, table=fifth}}}, 2={intersect={qualifier=null, operator=intersect}}, 3={select={1={column={name=fourth, table_ref=null}}}, from={table={alias=null, table=sixth}}}}}}}}, 2={union={qualifier=null, operator=union}}, 3={select={1={column={name=seventh, table_ref=null}}}, from={table={alias=null, table=eighth}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[first]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{sixth={fourth=[[@10,65:70='fourth',<335>,1:65]]}, eighth={seventh=[[@17,102:108='seventh',<335>,1:102]]}, fifth={third=[[@5,30:34='third',<335>,1:30]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query4={seventh=[[@17,102:108='seventh',<335>,1:102]]}, query0={third=[[@5,30:34='third',<335>,1:30]]}, query1={fourth=[[@10,65:70='fourth',<335>,1:65]]}, query3={first=[[@1,8:12='first',<87>,1:8]]}}",
						extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{union5={query4={query_dictionary={seventh=[[@17,102:108='seventh',<335>,1:102]]}, table_dictionary={eighth={seventh=[[@17,102:108='seventh',<335>,1:102]]}}, interface={seventh=[{name=seventh, table_ref=eighth}]}}, interface={first=query_column}, query3={query_dictionary={first=[[@1,8:12='first',<87>,1:8]]}, table_dictionary={}, def_intersect2={query0={query_dictionary={third=[[@5,30:34='third',<335>,1:30]]}, table_dictionary={fifth={third=[[@5,30:34='third',<335>,1:30]]}}, interface={third=[{name=third, table_ref=fifth}]}}, interface={third=query_column}, query1={query_dictionary={fourth=[[@10,65:70='fourth',<335>,1:65]]}, table_dictionary={sixth={fourth=[[@10,65:70='fourth',<335>,1:65]]}}, interface={fourth=[{name=fourth, table_ref=sixth}]}}}, interface={first=[{name=first, table_ref=null}]}, table_alias={aa=intersect2}}}}",
				extractor.getSymbolTable().toString());

		Snippet snippet = extractor.getSnippet();
		assertDiagnosticAtPosition(snippet, "UNRESOLVED_UNQUALIFIED_COLUMNS", ParseDiagnostic.Severity.ERROR,
				"Unresolved unqualified column reference(s): [first (l:1 c:8)]",
				"first", 1, 8);
	}


	@Test
	public void nestedUnionIntersectParseTest() {

		final String query = " SELECT first FROM ( " + "  select third from fifth "
				+ " intersect select fourth from sixth ) " + " union select seventh from eighth ";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={union={1={select={1={column={name=first, table_ref=null}}}, from={intersect={1={select={1={column={name=third, table_ref=null}}}, from={table={alias=null, table=fifth}}}, 2={intersect={qualifier=null, operator=intersect}}, 3={select={1={column={name=fourth, table_ref=null}}}, from={table={alias=null, table=sixth}}}}}}, 2={union={qualifier=null, operator=union}}, 3={select={1={column={name=seventh, table_ref=null}}}, from={table={alias=null, table=eighth}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[first]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{sixth={fourth=[[@10,65:70='fourth',<335>,1:65]]}, eighth={seventh=[[@16,99:105='seventh',<335>,1:99]]}, fifth={third=[[@5,30:34='third',<335>,1:30]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query4={seventh=[[@16,99:105='seventh',<335>,1:99]]}, query0={third=[[@5,30:34='third',<335>,1:30]]}, query1={fourth=[[@10,65:70='fourth',<335>,1:65]]}, query3={first=[[@1,8:12='first',<87>,1:8]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{union5={query4={query_dictionary={seventh=[[@16,99:105='seventh',<335>,1:99]]}, table_dictionary={eighth={seventh=[[@16,99:105='seventh',<335>,1:99]]}}, interface={seventh=[{name=seventh, table_ref=eighth}]}}, interface={first=query_column}, query3={intersect2={query0={query_dictionary={third=[[@5,30:34='third',<335>,1:30]]}, table_dictionary={fifth={third=[[@5,30:34='third',<335>,1:30]]}}, interface={third=[{name=third, table_ref=fifth}]}}, interface={third=query_column}, query1={query_dictionary={fourth=[[@10,65:70='fourth',<335>,1:65]]}, table_dictionary={sixth={fourth=[[@10,65:70='fourth',<335>,1:65]]}}, interface={fourth=[{name=fourth, table_ref=sixth}]}}}, query_dictionary={first=[[@1,8:12='first',<87>,1:8]]}, table_dictionary={}, interface={first=[{name=first, table_ref=null}]}}}}",
				extractor.getSymbolTable().toString());

		Snippet snippet = extractor.getSnippet();
		assertDiagnosticAtPosition(snippet, "UNRESOLVED_UNQUALIFIED_COLUMNS", ParseDiagnostic.Severity.ERROR,
				"Unresolved unqualified column reference(s): [first (l:1 c:8)]",
				"first", 1, 8);
	}


	@Test
	public void unionAllTest() {
		final String query = "SELECT * from tab1 " + 
				" UNION ALL " + 
				"SELECT * from tab2";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={union={1={select={1={column={name=*, table_ref=*}}}, from={table={alias=null, table=tab1}}}, 2={union={qualifier=ALL, operator=UNION}}, 3={select={1={column={name=*, table_ref=*}}}, from={table={alias=null, table=tab2}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={*=[[@1,7:7='*',<289>,1:7]]}, tab2={*=[[@7,37:37='*',<289>,1:37]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@1,7:7='*',<289>,1:7]]}, query1={*=[[@7,37:37='*',<289>,1:37]]}}",
						extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{union2={query0={query_dictionary={*=[[@1,7:7='*',<289>,1:7]]}, table_dictionary={tab1={*=[[@1,7:7='*',<289>,1:7]]}}, interface={*=[{name=*, table_ref=*}]}}, interface={*=query_column}, query1={query_dictionary={*=[[@7,37:37='*',<289>,1:37]]}, table_dictionary={tab2={*=[[@7,37:37='*',<289>,1:37]]}}, interface={*=[{name=*, table_ref=*}]}}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void intersectAllTest() {
		final String query = "SELECT * from tab1 " + 
				" INTERSECT ALL " + 
				"SELECT * from tab2";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={intersect={1={select={1={column={name=*, table_ref=*}}}, from={table={alias=null, table=tab1}}}, 2={intersect={qualifier=ALL, operator=INTERSECT}}, 3={select={1={column={name=*, table_ref=*}}}, from={table={alias=null, table=tab2}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={*=[[@1,7:7='*',<289>,1:7]]}, tab2={*=[[@7,41:41='*',<289>,1:41]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@1,7:7='*',<289>,1:7]]}, query1={*=[[@7,41:41='*',<289>,1:41]]}}",
						extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{intersect2={query0={query_dictionary={*=[[@1,7:7='*',<289>,1:7]]}, table_dictionary={tab1={*=[[@1,7:7='*',<289>,1:7]]}}, interface={*=[{name=*, table_ref=*}]}}, interface={*=query_column}, query1={query_dictionary={*=[[@7,41:41='*',<289>,1:41]]}, table_dictionary={tab2={*=[[@7,41:41='*',<289>,1:41]]}}, interface={*=[{name=*, table_ref=*}]}}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void unionDistinctTest() {
		final String query = "SELECT * from tab1 " + 
				" UNION distinct " + 
				"SELECT * from tab2";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={union={1={select={1={column={name=*, table_ref=*}}}, from={table={alias=null, table=tab1}}}, 2={union={qualifier=distinct, operator=UNION}}, 3={select={1={column={name=*, table_ref=*}}}, from={table={alias=null, table=tab2}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={*=[[@1,7:7='*',<289>,1:7]]}, tab2={*=[[@7,42:42='*',<289>,1:42]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@1,7:7='*',<289>,1:7]]}, query1={*=[[@7,42:42='*',<289>,1:42]]}}",
						extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{union2={query0={query_dictionary={*=[[@1,7:7='*',<289>,1:7]]}, table_dictionary={tab1={*=[[@1,7:7='*',<289>,1:7]]}}, interface={*=[{name=*, table_ref=*}]}}, interface={*=query_column}, query1={query_dictionary={*=[[@7,42:42='*',<289>,1:42]]}, table_dictionary={tab2={*=[[@7,42:42='*',<289>,1:42]]}}, interface={*=[{name=*, table_ref=*}]}}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void intersectDistinctTest() {
		final String query = "SELECT * from tab1 " + 
				" INTERSECT distinct " + 
				"SELECT * from tab2";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={intersect={1={select={1={column={name=*, table_ref=*}}}, from={table={alias=null, table=tab1}}}, 2={intersect={qualifier=distinct, operator=INTERSECT}}, 3={select={1={column={name=*, table_ref=*}}}, from={table={alias=null, table=tab2}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={*=[[@1,7:7='*',<289>,1:7]]}, tab2={*=[[@7,46:46='*',<289>,1:46]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@1,7:7='*',<289>,1:7]]}, query1={*=[[@7,46:46='*',<289>,1:46]]}}",
						extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{intersect2={query0={query_dictionary={*=[[@1,7:7='*',<289>,1:7]]}, table_dictionary={tab1={*=[[@1,7:7='*',<289>,1:7]]}}, interface={*=[{name=*, table_ref=*}]}}, interface={*=query_column}, query1={query_dictionary={*=[[@7,46:46='*',<289>,1:46]]}, table_dictionary={tab2={*=[[@7,46:46='*',<289>,1:46]]}}, interface={*=[{name=*, table_ref=*}]}}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void queryWithIntersectSubqueryTest() {
		final String query = "SELECT * from (select * from problem intersect select * from other) tab2";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=*, table_ref=*}}}, from={table={alias=tab2, query={intersect={1={select={1={column={name=*, table_ref=*}}}, from={table={alias=null, table=problem}}}, 2={intersect={qualifier=null, operator=intersect}}, 3={select={1={column={name=*, table_ref=*}}}, from={table={alias=null, table=other}}}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{other={*=[[@10,54:54='*',<289>,1:54]]}, problem={*=[[@5,22:22='*',<289>,1:22]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@5,22:22='*',<289>,1:22]]}, query1={*=[[@10,54:54='*',<289>,1:54]]}, query3={*=[[@1,7:7='*',<289>,1:7]]}}",
						extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query3={query_dictionary={*=[[@1,7:7='*',<289>,1:7]]}, table_dictionary={}, def_intersect2={query0={query_dictionary={*=[[@5,22:22='*',<289>,1:22]]}, table_dictionary={problem={*=[[@5,22:22='*',<289>,1:22]]}}, interface={*=[{name=*, table_ref=*}]}}, interface={*=query_column}, query1={query_dictionary={*=[[@10,54:54='*',<289>,1:54]]}, table_dictionary={other={*=[[@10,54:54='*',<289>,1:54]]}}, interface={*=[{name=*, table_ref=*}]}}}, interface={*=[{name=*, table_ref=*}]}, table_alias={tab2=intersect2}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void unionWithSubqueryP1Test() {
			final String query = "SELECT * from <tuple> tab1 " + 
				" UNION ALL " + 
				"SELECT * from (select * from problem) tab2";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={union={1={select={1={column={name=*, table_ref=*}}}, from={table={alias=tab1, substitution={name=<tuple>, type=tuple}}}}, 2={union={qualifier=ALL, operator=UNION}}, 3={select={1={column={name=*, table_ref=*}}}, from={table={alias=tab2, query={select={1={column={name=*, table_ref=*}}}, from={table={alias=null, table=problem}}}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<tuple>=tuple}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{problem={*=[[@12,60:60='*',<289>,1:60]]}, <tuple>={*=[[@1,7:7='*',<289>,1:7]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@1,7:7='*',<289>,1:7]]}, query1={*=[[@12,60:60='*',<289>,1:60]]}, query2={*=[[@8,45:45='*',<289>,1:45]]}}",
						extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{union3={query0={query_dictionary={*=[[@1,7:7='*',<289>,1:7]]}, table_dictionary={<tuple>={*=[[@1,7:7='*',<289>,1:7]]}}, interface={*=[{name=*, table_ref=*}]}, table_alias={tab1=<tuple>}}, interface={*=query_column}, query2={query_dictionary={*=[[@8,45:45='*',<289>,1:45]]}, table_dictionary={}, def_query1={query_dictionary={*=[[@12,60:60='*',<289>,1:60]]}, table_dictionary={problem={*=[[@12,60:60='*',<289>,1:60]]}}, interface={*=[{name=*, table_ref=*}]}}, interface={*=[{name=*, table_ref=*}]}, table_alias={tab2=query1}}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void unionWithSubqueryWithSubqueryTest() {
			final String query = "SELECT * from <tuple> tab1 " + 
				" UNION ALL " + 
				"SELECT * from (select * from (select * from answer) problem) tab2";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={union={1={select={1={column={name=*, table_ref=*}}}, from={table={alias=tab1, substitution={name=<tuple>, type=tuple}}}}, 2={union={qualifier=ALL, operator=UNION}}, 3={select={1={column={name=*, table_ref=*}}}, from={table={alias=tab2, query={select={1={column={name=*, table_ref=*}}}, from={table={alias=problem, query={select={1={column={name=*, table_ref=*}}}, from={table={alias=null, table=answer}}}}}}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<tuple>=tuple}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{answer={*=[[@16,75:75='*',<289>,1:75]]}, <tuple>={*=[[@1,7:7='*',<289>,1:7]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@1,7:7='*',<289>,1:7]]}, query1={*=[[@16,75:75='*',<289>,1:75]]}, query2={*=[[@12,60:60='*',<289>,1:60]]}, query3={*=[[@8,45:45='*',<289>,1:45]]}}",
						extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{union4={query0={query_dictionary={*=[[@1,7:7='*',<289>,1:7]]}, table_dictionary={<tuple>={*=[[@1,7:7='*',<289>,1:7]]}}, interface={*=[{name=*, table_ref=*}]}, table_alias={tab1=<tuple>}}, interface={*=query_column}, query3={query_dictionary={*=[[@8,45:45='*',<289>,1:45]]}, table_dictionary={}, interface={*=[{name=*, table_ref=*}]}, table_alias={tab2=query2}, def_query2={query_dictionary={*=[[@12,60:60='*',<289>,1:60]]}, table_dictionary={}, def_query1={query_dictionary={*=[[@16,75:75='*',<289>,1:75]]}, table_dictionary={answer={*=[[@16,75:75='*',<289>,1:75]]}}, interface={*=[{name=*, table_ref=*}]}}, interface={*=[{name=*, table_ref=*}]}, table_alias={problem=query1}}}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void intersectWithSubqueryWithIntersectSubqueryTest() {
			final String query = "SELECT * from tab1 " + 
				" intersect " + 
				"SELECT * from (select * from  answer intersect select * from problem) tab2";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={intersect={1={select={1={column={name=*, table_ref=*}}}, from={table={alias=null, table=tab1}}}, 2={intersect={qualifier=null, operator=intersect}}, 3={select={1={column={name=*, table_ref=*}}}, from={table={alias=tab2, query={intersect={1={select={1={column={name=*, table_ref=*}}}, from={table={alias=null, table=answer}}}, 2={intersect={qualifier=null, operator=intersect}}, 3={select={1={column={name=*, table_ref=*}}}, from={table={alias=null, table=problem}}}}}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{problem={*=[[@15,84:84='*',<289>,1:84]]}, answer={*=[[@10,52:52='*',<289>,1:52]]}, tab1={*=[[@1,7:7='*',<289>,1:7]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query4={*=[[@6,37:37='*',<289>,1:37]]}, query0={*=[[@1,7:7='*',<289>,1:7]]}, query1={*=[[@10,52:52='*',<289>,1:52]]}, query2={*=[[@15,84:84='*',<289>,1:84]]}}",
						extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{intersect5={query0={query_dictionary={*=[[@1,7:7='*',<289>,1:7]]}, table_dictionary={tab1={*=[[@1,7:7='*',<289>,1:7]]}}, interface={*=[{name=*, table_ref=*}]}}, interface={*=query_column}, query4={query_dictionary={*=[[@6,37:37='*',<289>,1:37]]}, table_dictionary={}, def_intersect3={interface={*=query_column}, query1={query_dictionary={*=[[@10,52:52='*',<289>,1:52]]}, table_dictionary={answer={*=[[@10,52:52='*',<289>,1:52]]}}, interface={*=[{name=*, table_ref=*}]}}, query2={query_dictionary={*=[[@15,84:84='*',<289>,1:84]]}, table_dictionary={problem={*=[[@15,84:84='*',<289>,1:84]]}}, interface={*=[{name=*, table_ref=*}]}}}, interface={*=[{name=*, table_ref=*}]}, table_alias={tab2=intersect3}}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void unionWithSubqueryWithUnionSubqueryTest() {
		final String query = "SELECT * from tab1 " + 
				" UNION " + 
				"SELECT * from (select * from  answer union select * from problem) tab2";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={union={1={select={1={column={name=*, table_ref=*}}}, from={table={alias=null, table=tab1}}}, 2={union={qualifier=null, operator=UNION}}, 3={select={1={column={name=*, table_ref=*}}}, from={table={alias=tab2, query={union={1={select={1={column={name=*, table_ref=*}}}, from={table={alias=null, table=answer}}}, 2={union={qualifier=null, operator=union}}, 3={select={1={column={name=*, table_ref=*}}}, from={table={alias=null, table=problem}}}}}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{problem={*=[[@15,76:76='*',<289>,1:76]]}, answer={*=[[@10,48:48='*',<289>,1:48]]}, tab1={*=[[@1,7:7='*',<289>,1:7]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query4={*=[[@6,33:33='*',<289>,1:33]]}, query0={*=[[@1,7:7='*',<289>,1:7]]}, query1={*=[[@10,48:48='*',<289>,1:48]]}, query2={*=[[@15,76:76='*',<289>,1:76]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{union5={query4={def_union3={interface={*=query_column}, query1={query_dictionary={*=[[@10,48:48='*',<289>,1:48]]}, table_dictionary={answer={*=[[@10,48:48='*',<289>,1:48]]}}, interface={*=[{name=*, table_ref=*}]}}, query2={query_dictionary={*=[[@15,76:76='*',<289>,1:76]]}, table_dictionary={problem={*=[[@15,76:76='*',<289>,1:76]]}}, interface={*=[{name=*, table_ref=*}]}}}, query_dictionary={*=[[@6,33:33='*',<289>,1:33]]}, table_dictionary={}, interface={*=[{name=*, table_ref=*}]}, table_alias={tab2=union3}}, query0={query_dictionary={*=[[@1,7:7='*',<289>,1:7]]}, table_dictionary={tab1={*=[[@1,7:7='*',<289>,1:7]]}}, interface={*=[{name=*, table_ref=*}]}}, interface={*=query_column}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void intersectWithSubqueryWithUnionSubqueryTest() {
			final String query = "SELECT * from tab1 " + 
				" intersect " + 
				"SELECT * from (select * from  answer union select * from problem) tab2";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={intersect={1={select={1={column={name=*, table_ref=*}}}, from={table={alias=null, table=tab1}}}, 2={intersect={qualifier=null, operator=intersect}}, 3={select={1={column={name=*, table_ref=*}}}, from={table={alias=tab2, query={union={1={select={1={column={name=*, table_ref=*}}}, from={table={alias=null, table=answer}}}, 2={union={qualifier=null, operator=union}}, 3={select={1={column={name=*, table_ref=*}}}, from={table={alias=null, table=problem}}}}}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{problem={*=[[@15,80:80='*',<289>,1:80]]}, answer={*=[[@10,52:52='*',<289>,1:52]]}, tab1={*=[[@1,7:7='*',<289>,1:7]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query4={*=[[@6,37:37='*',<289>,1:37]]}, query0={*=[[@1,7:7='*',<289>,1:7]]}, query1={*=[[@10,52:52='*',<289>,1:52]]}, query2={*=[[@15,80:80='*',<289>,1:80]]}}",
						extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{intersect5={query0={query_dictionary={*=[[@1,7:7='*',<289>,1:7]]}, table_dictionary={tab1={*=[[@1,7:7='*',<289>,1:7]]}}, interface={*=[{name=*, table_ref=*}]}}, interface={*=query_column}, query4={def_union3={interface={*=query_column}, query1={query_dictionary={*=[[@10,52:52='*',<289>,1:52]]}, table_dictionary={answer={*=[[@10,52:52='*',<289>,1:52]]}}, interface={*=[{name=*, table_ref=*}]}}, query2={query_dictionary={*=[[@15,80:80='*',<289>,1:80]]}, table_dictionary={problem={*=[[@15,80:80='*',<289>,1:80]]}}, interface={*=[{name=*, table_ref=*}]}}}, query_dictionary={*=[[@6,37:37='*',<289>,1:37]]}, table_dictionary={}, interface={*=[{name=*, table_ref=*}]}, table_alias={tab2=union3}}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void unionWithSubqueryWithIntersectSubqueryTest() {
			final String query = "SELECT * from tab1 " + 
				" UNION " + 
				"SELECT * from (select * from  answer intersect select * from problem) tab2";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={union={1={select={1={column={name=*, table_ref=*}}}, from={table={alias=null, table=tab1}}}, 2={union={qualifier=null, operator=UNION}}, 3={select={1={column={name=*, table_ref=*}}}, from={table={alias=tab2, query={intersect={1={select={1={column={name=*, table_ref=*}}}, from={table={alias=null, table=answer}}}, 2={intersect={qualifier=null, operator=intersect}}, 3={select={1={column={name=*, table_ref=*}}}, from={table={alias=null, table=problem}}}}}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{problem={*=[[@15,80:80='*',<289>,1:80]]}, answer={*=[[@10,48:48='*',<289>,1:48]]}, tab1={*=[[@1,7:7='*',<289>,1:7]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query4={*=[[@6,33:33='*',<289>,1:33]]}, query0={*=[[@1,7:7='*',<289>,1:7]]}, query1={*=[[@10,48:48='*',<289>,1:48]]}, query2={*=[[@15,80:80='*',<289>,1:80]]}}",
						extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{union5={query4={query_dictionary={*=[[@6,33:33='*',<289>,1:33]]}, table_dictionary={}, def_intersect3={interface={*=query_column}, query1={query_dictionary={*=[[@10,48:48='*',<289>,1:48]]}, table_dictionary={answer={*=[[@10,48:48='*',<289>,1:48]]}}, interface={*=[{name=*, table_ref=*}]}}, query2={query_dictionary={*=[[@15,80:80='*',<289>,1:80]]}, table_dictionary={problem={*=[[@15,80:80='*',<289>,1:80]]}}, interface={*=[{name=*, table_ref=*}]}}}, interface={*=[{name=*, table_ref=*}]}, table_alias={tab2=intersect3}}, query0={query_dictionary={*=[[@1,7:7='*',<289>,1:7]]}, table_dictionary={tab1={*=[[@1,7:7='*',<289>,1:7]]}}, interface={*=[{name=*, table_ref=*}]}}, interface={*=query_column}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void selectWithUnionSubqueryTest() {
		final String query = "SELECT * from (select * from  answer union select * from problem) tab2";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=*, table_ref=*}}}, from={table={alias=tab2, query={union={1={select={1={column={name=*, table_ref=*}}}, from={table={alias=null, table=answer}}}, 2={union={qualifier=null, operator=union}}, 3={select={1={column={name=*, table_ref=*}}}, from={table={alias=null, table=problem}}}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{problem={*=[[@10,50:50='*',<289>,1:50]]}, answer={*=[[@5,22:22='*',<289>,1:22]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@5,22:22='*',<289>,1:22]]}, query1={*=[[@10,50:50='*',<289>,1:50]]}, query3={*=[[@1,7:7='*',<289>,1:7]]}}",
						extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query3={def_union2={query0={query_dictionary={*=[[@5,22:22='*',<289>,1:22]]}, table_dictionary={answer={*=[[@5,22:22='*',<289>,1:22]]}}, interface={*=[{name=*, table_ref=*}]}}, interface={*=query_column}, query1={query_dictionary={*=[[@10,50:50='*',<289>,1:50]]}, table_dictionary={problem={*=[[@10,50:50='*',<289>,1:50]]}}, interface={*=[{name=*, table_ref=*}]}}}, query_dictionary={*=[[@1,7:7='*',<289>,1:7]]}, table_dictionary={}, interface={*=[{name=*, table_ref=*}]}, table_alias={tab2=union2}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void ilikeAnyWithEscapePredicateSubqueryTest() {
		// Item 95 - add support for PostgresSQL escape character syntax in iLike Any clauses
		final String query = "SELECT * FROM scbcrse aa  WHERE subj_code ilike any ('AA%', 'BB%') escape '_'";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=*, table_ref=*}}}, from={table={alias=aa, table=scbcrse}}, where={ilike_any={item={column={name=subj_code, table_ref=null}}, not_like_any_list={list={1={literal='AA%'}, 2={literal='BB%'}}}, escape='_'}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{scbcrse={subj_code=[[@6,32:40='subj_code',<335>,1:32]], *=[[@1,7:7='*',<289>,1:7]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@1,7:7='*',<289>,1:7]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={query_dictionary={*=[[@1,7:7='*',<289>,1:7]]}, table_dictionary={scbcrse={subj_code=[[@6,32:40='subj_code',<335>,1:32]], *=[[@1,7:7='*',<289>,1:7]]}}, filters=[{name=subj_code, table_ref=scbcrse}], interface={*=[{name=*, table_ref=*}]}, table_alias={aa=scbcrse}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void iLikeAnyInListVariableSubqueryTest() {
		// Item 101 - add support for ILike Any clauses
		final String query = "SELECT * FROM scbcrse aa  WHERE subj_code ilIke any <variable> ";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=*, table_ref=*}}}, from={table={alias=aa, table=scbcrse}}, where={ilike_any={item={column={name=subj_code, table_ref=null}}, like_any_list={substitution={name=<variable>, type=in_list}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<variable>=in_list}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{scbcrse={subj_code=[[@6,32:40='subj_code',<335>,1:32]], *=[[@1,7:7='*',<289>,1:7]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@1,7:7='*',<289>,1:7]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={query_dictionary={*=[[@1,7:7='*',<289>,1:7]]}, table_dictionary={scbcrse={subj_code=[[@6,32:40='subj_code',<335>,1:32]], *=[[@1,7:7='*',<289>,1:7]]}}, filters=[{name=subj_code, table_ref=scbcrse}], interface={*=[{name=*, table_ref=*}]}, table_alias={aa=scbcrse}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void scalarSubqueriesSymbolTableTestV1() {
		String query = " select a aa, (select max(D) from ee where 1=1) dd from tab1" +
		" where a in (select c from ff where 1=1) ";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=a, table_ref=null}, alias=aa}, 2={lookup={from={table={alias=null, table=ee}}, where={condition={left={literal=1}, right={literal=1}, operator==}}, select={1={function={function_name=max, qualifier=null, parameters={column={name=D, table_ref=null}}}}}, query=query0}, alias=dd}}, from={table={alias=null, table=tab1}}, where={in={item={column={name=a, table_ref=null}}, in_list={select={1={column={name=c, table_ref=null}}}, from={table={alias=null, table=ff}}, where={condition={left={literal=1}, right={literal=1}, operator==}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[aa, dd]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{ee={D=[[@8,26:26='D',<335>,1:26]]}, ff={c=[[@25,80:80='c',<335>,1:80]]}, tab1={a=[[@1,8:8='a',<335>,1:8], [@21,67:67='a',<335>,1:67]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query4={aa=[[@2,10:11='aa',<335>,1:10]], dd=[[@17,48:49='dd',<335>,1:48]]}, query0={unnamed_0=[[@9,27:27=')',<286>,1:27]]}, query2={c=[[@25,80:80='c',<335>,1:80]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query4={query_dictionary={aa=[[@2,10:11='aa',<335>,1:10]], dd=[[@17,48:49='dd',<335>,1:48]]}, table_dictionary={tab1={a=[[@1,8:8='a',<335>,1:8], [@21,67:67='a',<335>,1:67]]}}, def_query0={query_dictionary={unnamed_0=[[@9,27:27=')',<286>,1:27]]}, table_dictionary={ee={D=[[@8,26:26='D',<335>,1:26]]}}, filters=[], interface={unnamed_0=[{name=D, table_ref=ee}]}}, filters=[{name=a, table_ref=tab1}, {query=query2}], interface={aa=[{name=a, table_ref=tab1}], dd=[{query=query0}]}, def_query2={query_dictionary={c=[[@25,80:80='c',<335>,1:80]]}, table_dictionary={ff={c=[[@25,80:80='c',<335>,1:80]]}}, filters=[], interface={c=[{name=c, table_ref=ff}]}}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void scalarSubqueriesSymbolTableTestV2() {
		String query = " select tab1.a aa, (select max(D) from ee where 1=1) dd from tab1" +
		" join tab2 bb on tab1.a = (select max(c) from ff where 1=1) ";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=a, table_ref=tab1}, alias=aa}, 2={lookup={from={table={alias=null, table=ee}}, where={condition={left={literal=1}, right={literal=1}, operator==}}, select={1={function={function_name=max, qualifier=null, parameters={column={name=D, table_ref=null}}}}}, query=query0}, alias=dd}}, from={join={1={table={alias=null, table=tab1}}, 2={join=join, on={condition={left={column={name=a, table_ref=tab1}}, right={select={1={function={function_name=max, qualifier=null, parameters={column={name=c, table_ref=null}}}}}, from={table={alias=null, table=ff}}, where={condition={left={literal=1}, right={literal=1}, operator==}}}, operator==}}}, 3={table={alias=bb, table=tab2}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[aa, dd]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{ee={D=[[@10,31:31='D',<335>,1:31]]}, ff={c=[[@34,103:103='c',<335>,1:103]]}, tab1={a=[[@1,8:11='tab1',<335>,1:8], [@26,82:85='tab1',<335>,1:82]]}, tab2={}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query4={aa=[[@4,15:16='aa',<335>,1:15]], dd=[[@19,53:54='dd',<335>,1:53]]}, query0={unnamed_0=[[@11,32:32=')',<286>,1:32]]}, query2={unnamed_1=[[@35,104:104=')',<286>,1:104]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query4={query_dictionary={aa=[[@4,15:16='aa',<335>,1:15]], dd=[[@19,53:54='dd',<335>,1:53]]}, table_dictionary={tab1={a=[[@1,8:11='tab1',<335>,1:8], [@26,82:85='tab1',<335>,1:82]]}, tab2={}}, def_query0={query_dictionary={unnamed_0=[[@11,32:32=')',<286>,1:32]]}, table_dictionary={ee={D=[[@10,31:31='D',<335>,1:31]]}}, filters=[], interface={unnamed_0=[{name=D, table_ref=ee}]}}, filters=[{name=a, table_ref=tab1}, {query=query2}], interface={aa=[{name=a, table_ref=tab1}], dd=[{query=query0}]}, table_alias={bb=tab2}, def_query2={query_dictionary={unnamed_1=[[@35,104:104=')',<286>,1:104]]}, table_dictionary={ff={c=[[@34,103:103='c',<335>,1:103]]}}, filters=[], interface={unnamed_1=[{name=c, table_ref=ff}]}}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void scalarSubqueriesSymbolTableTestV3() {
		String query = " select a aa, (select max(D) from ee where 1=1) dd from tab1" +
		" group by a having a > (select max(c) from ff where 1=1) ";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=a, table_ref=null}, alias=aa}, 2={lookup={from={table={alias=null, table=ee}}, where={condition={left={literal=1}, right={literal=1}, operator==}}, select={1={function={function_name=max, qualifier=null, parameters={column={name=D, table_ref=null}}}}}, query=query0}, alias=dd}}, having={condition={left={column={name=a, table_ref=null}}, right={select={1={function={function_name=max, qualifier=null, parameters={column={name=c, table_ref=null}}}}}, from={table={alias=null, table=ff}}, where={condition={left={literal=1}, right={literal=1}, operator==}}}, operator=>}}, from={table={alias=null, table=tab1}}, groupby={1={column={name=a, table_ref=null}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[aa, dd]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{ee={D=[[@8,26:26='D',<335>,1:26]]}, ff={c=[[@30,95:95='c',<335>,1:95]]}, tab1={a=[[@1,8:8='a',<335>,1:8], [@22,70:70='a',<335>,1:70], [@24,79:79='a',<335>,1:79]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query4={aa=[[@2,10:11='aa',<335>,1:10]], dd=[[@17,48:49='dd',<335>,1:48]]}, query0={unnamed_0=[[@9,27:27=')',<286>,1:27]]}, query2={unnamed_1=[[@31,96:96=')',<286>,1:96]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query4={query_dictionary={aa=[[@2,10:11='aa',<335>,1:10]], dd=[[@17,48:49='dd',<335>,1:48]]}, table_dictionary={tab1={a=[[@1,8:8='a',<335>,1:8], [@22,70:70='a',<335>,1:70], [@24,79:79='a',<335>,1:79]]}}, grouped_by=[{name=a, table_ref=tab1}], def_query0={query_dictionary={unnamed_0=[[@9,27:27=')',<286>,1:27]]}, table_dictionary={ee={D=[[@8,26:26='D',<335>,1:26]]}}, filters=[], interface={unnamed_0=[{name=D, table_ref=ee}]}}, filters=[{name=a, table_ref=tab1}, {query=query2}], interface={aa=[{name=a, table_ref=tab1}], dd=[{query=query0}]}, def_query2={query_dictionary={unnamed_1=[[@31,96:96=')',<286>,1:96]]}, table_dictionary={ff={c=[[@30,95:95='c',<335>,1:95]]}}, filters=[], interface={unnamed_1=[{name=c, table_ref=ff}]}}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void scalarSubqueriesSymbolTableTestV4() {
		String query = " select a aa, (select max(D) from ee where 1=1) dd from tab1" +
		" group by a, (select max(c) from ff where 1=1) ";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=a, table_ref=null}, alias=aa}, 2={lookup={from={table={alias=null, table=ee}}, where={condition={left={literal=1}, right={literal=1}, operator==}}, select={1={function={function_name=max, qualifier=null, parameters={column={name=D, table_ref=null}}}}}, query=query0}, alias=dd}}, from={table={alias=null, table=tab1}}, groupby={1={column={name=a, table_ref=null}}, 2={select={1={function={function_name=max, qualifier=null, parameters={column={name=c, table_ref=null}}}}}, from={table={alias=null, table=ff}}, where={condition={left={literal=1}, right={literal=1}, operator==}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[aa, dd]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{ee={D=[[@8,26:26='D',<335>,1:26]]}, ff={c=[[@28,85:85='c',<335>,1:85]]}, tab1={a=[[@1,8:8='a',<335>,1:8], [@22,70:70='a',<335>,1:70]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query4={aa=[[@2,10:11='aa',<335>,1:10]], dd=[[@17,48:49='dd',<335>,1:48]]}, query0={unnamed_0=[[@9,27:27=')',<286>,1:27]]}, query2={unnamed_1=[[@29,86:86=')',<286>,1:86]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query4={query_dictionary={aa=[[@2,10:11='aa',<335>,1:10]], dd=[[@17,48:49='dd',<335>,1:48]]}, table_dictionary={tab1={a=[[@1,8:8='a',<335>,1:8], [@22,70:70='a',<335>,1:70]]}}, grouped_by=[{name=a, table_ref=tab1}, {query=query2}], def_query0={query_dictionary={unnamed_0=[[@9,27:27=')',<286>,1:27]]}, table_dictionary={ee={D=[[@8,26:26='D',<335>,1:26]]}}, filters=[], interface={unnamed_0=[{name=D, table_ref=ee}]}}, interface={aa=[{name=a, table_ref=tab1}], dd=[{query=query0}]}, def_query2={query_dictionary={unnamed_1=[[@29,86:86=')',<286>,1:86]]}, table_dictionary={ff={c=[[@28,85:85='c',<335>,1:85]]}}, filters=[], interface={unnamed_1=[{name=c, table_ref=ff}]}}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void scalarSubqueriesSymbolTableTestV5() {
		String query = " select a aa, (select max(D) from ee where 1=1) dd from tab1" +
		" order by (select max(c) from ff where 1=1) ";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=a, table_ref=null}, alias=aa}, 2={lookup={from={table={alias=null, table=ee}}, where={condition={left={literal=1}, right={literal=1}, operator==}}, select={1={function={function_name=max, qualifier=null, parameters={column={name=D, table_ref=null}}}}}, query=query0}, alias=dd}}, orderby={1={null_order=null, predicand={select={1={function={function_name=max, qualifier=null, parameters={column={name=c, table_ref=null}}}}}, from={table={alias=null, table=ff}}, where={condition={left={literal=1}, right={literal=1}, operator==}}}, sort_order=ASC}}, from={table={alias=null, table=tab1}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[aa, dd]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{ee={D=[[@8,26:26='D',<335>,1:26]]}, ff={c=[[@26,82:82='c',<335>,1:82]]}, tab1={a=[[@1,8:8='a',<335>,1:8]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query4={aa=[[@2,10:11='aa',<335>,1:10]], dd=[[@17,48:49='dd',<335>,1:48]]}, query0={unnamed_0=[[@9,27:27=')',<286>,1:27]]}, query2={unnamed_1=[[@27,83:83=')',<286>,1:83]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query4={query_dictionary={aa=[[@2,10:11='aa',<335>,1:10]], dd=[[@17,48:49='dd',<335>,1:48]]}, table_dictionary={tab1={a=[[@1,8:8='a',<335>,1:8]]}}, def_query0={query_dictionary={unnamed_0=[[@9,27:27=')',<286>,1:27]]}, table_dictionary={ee={D=[[@8,26:26='D',<335>,1:26]]}}, filters=[], interface={unnamed_0=[{name=D, table_ref=ee}]}}, ordered_by=[{query=query2}], interface={aa=[{name=a, table_ref=tab1}], dd=[{query=query0}]}, def_query2={query_dictionary={unnamed_1=[[@27,83:83=')',<286>,1:83]]}, table_dictionary={ff={c=[[@26,82:82='c',<335>,1:82]]}}, filters=[], interface={unnamed_1=[{name=c, table_ref=ff}]}}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void scalarSubqueriesSymbolTableTestV6() {
		String query = " select a aa, (select max(D) from ee where 1=1) dd from tab1" +
		" qualify a > (select max(c) from ff where 1=1) ";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=a, table_ref=null}, alias=aa}, 2={lookup={from={table={alias=null, table=ee}}, where={condition={left={literal=1}, right={literal=1}, operator==}}, select={1={function={function_name=max, qualifier=null, parameters={column={name=D, table_ref=null}}}}}, query=query0}, alias=dd}}, from={table={alias=null, table=tab1}}, qualify={condition={left={column={name=a, table_ref=null}}, right={select={1={function={function_name=max, qualifier=null, parameters={column={name=c, table_ref=null}}}}}, from={table={alias=null, table=ff}}, where={condition={left={literal=1}, right={literal=1}, operator==}}}, operator=>}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[aa, dd]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{ee={D=[[@8,26:26='D',<335>,1:26]]}, ff={c=[[@27,85:85='c',<335>,1:85]]}, tab1={a=[[@1,8:8='a',<335>,1:8], [@21,69:69='a',<335>,1:69]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query4={aa=[[@2,10:11='aa',<335>,1:10]], dd=[[@17,48:49='dd',<335>,1:48]]}, query0={unnamed_0=[[@9,27:27=')',<286>,1:27]]}, query2={unnamed_1=[[@28,86:86=')',<286>,1:86]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query4={query_dictionary={aa=[[@2,10:11='aa',<335>,1:10]], dd=[[@17,48:49='dd',<335>,1:48]]}, table_dictionary={tab1={a=[[@1,8:8='a',<335>,1:8], [@21,69:69='a',<335>,1:69]]}}, def_query0={query_dictionary={unnamed_0=[[@9,27:27=')',<286>,1:27]]}, table_dictionary={ee={D=[[@8,26:26='D',<335>,1:26]]}}, filters=[], interface={unnamed_0=[{name=D, table_ref=ee}]}}, filters=[{name=a, table_ref=tab1}, {query=query2}], interface={aa=[{name=a, table_ref=tab1}], dd=[{query=query0}]}, def_query2={query_dictionary={unnamed_1=[[@28,86:86=')',<286>,1:86]]}, table_dictionary={ff={c=[[@27,85:85='c',<335>,1:85]]}}, filters=[], interface={unnamed_1=[{name=c, table_ref=ff}]}}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void scalarSubqueriesCorrelatedSubquerySymbolTableTest() {
		String query = " select a aa, (select max(D) from ee where ee.x = tab1.x) dd from tab1" +
		" where a in (select c from ff where ff.y = tab1.y) ";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=a, table_ref=null}, alias=aa}, 2={lookup={from={table={alias=null, table=ee}}, where={condition={left={column={name=x, table_ref=ee}}, right={column={name=x, table_ref=tab1}}, operator==}}, select={1={function={function_name=max, qualifier=null, parameters={column={name=D, table_ref=null}}}}}, query=query0}, alias=dd}}, from={table={alias=null, table=tab1}}, where={in={item={column={name=a, table_ref=null}}, in_list={select={1={column={name=c, table_ref=null}}}, from={table={alias=null, table=ff}}, where={condition={left={column={name=y, table_ref=ff}}, right={column={name=y, table_ref=tab1}}, operator==}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[aa, dd]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{ee={D=[[@8,26:26='D',<335>,1:26]], x=[[@13,43:44='ee',<335>,1:43]]}, ff={c=[[@29,90:90='c',<335>,1:90]], y=[[@33,106:107='ff',<335>,1:106]]}, tab1={y=[[@37,113:116='tab1',<335>,1:113]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query4={aa=[[@2,10:11='aa',<335>,1:10]], dd=[[@21,58:59='dd',<335>,1:58]]}, query0={unnamed_0=[[@9,27:27=')',<286>,1:27]]}, query2={c=[[@29,90:90='c',<335>,1:90]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query4={query_dictionary={aa=[[@2,10:11='aa',<335>,1:10]], dd=[[@21,58:59='dd',<335>,1:58]]}, table_dictionary={tab1={}}, def_query0={query_dictionary={unnamed_0=[[@9,27:27=')',<286>,1:27]]}, table_dictionary={ee={D=[[@8,26:26='D',<335>,1:26]], x=[[@13,43:44='ee',<335>,1:43]]}}, filters=[{name=x, table_ref=ee}, {name=x, table_ref=tab1}], interface={unnamed_0=[{name=D, table_ref=ee}]}}, filters=[{name=a, table_ref=null}, {query=query2}], interface={aa=[{name=a, table_ref=null}], dd=[{query=query0}]}, def_query2={query_dictionary={c=[[@29,90:90='c',<335>,1:90]]}, table_dictionary={ff={c=[[@29,90:90='c',<335>,1:90]], y=[[@33,106:107='ff',<335>,1:106]]}}, filters=[{name=y, table_ref=ff}, {name=y, table_ref=tab1}], interface={c=[{name=c, table_ref=ff}]}}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void multipleScalarAndOtherSubqueriesSymbolTableTest() {
		String query = " select tab1.a aa, (select max(D) from ee) max_D, (select min(D) from ee) min_D,  kk.w from tab1" +
		" join (select w from jj) kk" +
		" where a in (select c from ff) ";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=a, table_ref=tab1}, alias=aa}, 2={lookup={from={table={alias=null, table=ee}}, select={1={function={function_name=max, qualifier=null, parameters={column={name=D, table_ref=null}}}}}, query=query0}, alias=max_D}, 3={lookup={from={table={alias=null, table=ee}}, select={1={function={function_name=min, qualifier=null, parameters={column={name=D, table_ref=null}}}}}, query=query2}, alias=min_D}, 4={column={name=w, table_ref=kk}}}, from={join={1={table={alias=null, table=tab1}}, 2={join=join}, 3={table={alias=kk, query={select={1={column={name=w, table_ref=null}}}, from={table={alias=null, table=jj}}}}}}}, where={in={item={column={name=a, table_ref=null}}, in_list={select={1={column={name=c, table_ref=null}}}, from={table={alias=null, table=ff}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[aa, max_D, min_D, w]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{ee={D=[[@10,31:31='D',<335>,1:31], [@21,62:62='D',<335>,1:62]]}, jj={w=[[@36,110:110='w',<335>,1:110]]}, ff={c=[[@46,143:143='c',<335>,1:143]]}, tab1={a=[[@1,8:11='tab1',<335>,1:8], [@42,130:130='a',<335>,1:130]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query4={w=[[@36,110:110='w',<335>,1:110], [@28,82:83='kk',<335>,1:82]]}, query5={c=[[@46,143:143='c',<335>,1:143]]}, query7={aa=[[@4,15:16='aa',<335>,1:15]], max_D=[[@15,43:47='max_D',<335>,1:43]], min_D=[[@26,74:78='min_D',<335>,1:74]], w=[[@30,85:85='w',<335>,1:85]]}, query0={unnamed_0=[[@11,32:32=')',<286>,1:32]]}, query2={unnamed_1=[[@22,63:63=')',<286>,1:63]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query7={query_dictionary={aa=[[@4,15:16='aa',<335>,1:15]], max_D=[[@15,43:47='max_D',<335>,1:43]], min_D=[[@26,74:78='min_D',<335>,1:74]], w=[[@30,85:85='w',<335>,1:85]]}, table_dictionary={tab1={a=[[@1,8:11='tab1',<335>,1:8], [@42,130:130='a',<335>,1:130]]}}, def_query0={query_dictionary={unnamed_0=[[@11,32:32=')',<286>,1:32]]}, table_dictionary={ee={D=[[@10,31:31='D',<335>,1:31], [@21,62:62='D',<335>,1:62]]}}, interface={unnamed_0=[{name=D, table_ref=ee}]}}, filters=[{name=a, table_ref=tab1}, {query=query5}], def_query5={query_dictionary={c=[[@46,143:143='c',<335>,1:143]]}, table_dictionary={ff={c=[[@46,143:143='c',<335>,1:143]]}}, interface={c=[{name=c, table_ref=ff}]}}, interface={aa=[{name=a, table_ref=tab1}], max_D=[{query=query0}], min_D=[{query=query2}], w=[{name=w, table_ref=kk}]}, def_query4={query_dictionary={w=[[@36,110:110='w',<335>,1:110], [@28,82:83='kk',<335>,1:82]]}, table_dictionary={jj={w=[[@36,110:110='w',<335>,1:110]]}}, interface={w=[{name=w, table_ref=jj}]}}, table_alias={kk=query4}, def_query2={query_dictionary={unnamed_1=[[@22,63:63=')',<286>,1:63]]}, table_dictionary={ee={D=[[@21,62:62='D',<335>,1:62]]}}, interface={unnamed_1=[{name=D, table_ref=ee}]}}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void nestedFormulaSubqueriesUseQueryRefsInInterfaceAndFiltersTest() {
		// coalesce with two scalar subqueries (query0, query2) produces {query=queryN} refs in dd interface.
		// WHERE uses a scalar subquery (query4) in an IN predicate against another subquery (query6),
		// both of which appear as {query=queryN} entries in filters.
		String query = " select a aa, coalesce(emptyCol,(select max(D) from ee where 1=1),(select min(D) from gg)) dd from tab1"
				+ " where (select min(b) as bb from tab3) in (select c from ff where 1=1) ";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=a, table_ref=null}, alias=aa}, 2={function={parameters={1={column={name=emptyCol, table_ref=null}}, 2={select={1={function={function_name=max, qualifier=null, parameters={column={name=D, table_ref=null}}}}}, from={table={alias=null, table=ee}}, where={condition={left={literal=1}, right={literal=1}, operator==}}}, 3={select={1={function={function_name=min, qualifier=null, parameters={column={name=D, table_ref=null}}}}}, from={table={alias=null, table=gg}}}}, function_name=coalesce}, alias=dd}}, from={table={alias=null, table=tab1}}, where={in={item={select={1={function={function_name=min, qualifier=null, parameters={column={name=b, table_ref=null}}}, alias=bb}}, from={table={alias=null, table=tab3}}}, in_list={select={1={column={name=c, table_ref=null}}}, from={table={alias=null, table=ff}}, where={condition={left={literal=1}, right={literal=1}, operator==}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[aa, dd]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{ee={D=[[@12,44:44='D',<335>,1:44]]}, gg={D=[[@26,78:78='D',<335>,1:78]]}, ff={c=[[@50,153:153='c',<335>,1:153]]}, tab3={b=[[@40,122:122='b',<335>,1:122]]}, tab1={a=[[@1,8:8='a',<335>,1:8]], emptyCol=[[@6,23:30='emptyCol',<335>,1:23]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query8={aa=[[@2,10:11='aa',<335>,1:10]], dd=[[@32,91:92='dd',<335>,1:91]]}, query4={bb=[[@43,128:129='bb',<335>,1:128]]}, query6={c=[[@50,153:153='c',<335>,1:153]]}, query0={unnamed_0=[[@13,45:45=')',<286>,1:45]]}, query2={unnamed_1=[[@27,79:79=')',<286>,1:79]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query8={def_query6={query_dictionary={c=[[@50,153:153='c',<335>,1:153]]}, table_dictionary={ff={c=[[@50,153:153='c',<335>,1:153]]}}, filters=[], interface={c=[{name=c, table_ref=ff}]}}, query_dictionary={aa=[[@2,10:11='aa',<335>,1:10]], dd=[[@32,91:92='dd',<335>,1:91]]}, table_dictionary={tab1={a=[[@1,8:8='a',<335>,1:8]], emptyCol=[[@6,23:30='emptyCol',<335>,1:23]]}}, def_query0={query_dictionary={unnamed_0=[[@13,45:45=')',<286>,1:45]]}, table_dictionary={ee={D=[[@12,44:44='D',<335>,1:44]]}}, filters=[], interface={unnamed_0=[{name=D, table_ref=ee}]}}, filters=[{query=query4}, {query=query6}], interface={aa=[{name=a, table_ref=tab1}], dd=[{query=query0}, {query=query2}]}, def_query4={query_dictionary={bb=[[@43,128:129='bb',<335>,1:128]]}, table_dictionary={tab3={b=[[@40,122:122='b',<335>,1:122]]}}, interface={bb=[{name=b, table_ref=tab3}]}}, def_query2={query_dictionary={unnamed_1=[[@27,79:79=')',<286>,1:79]]}, table_dictionary={gg={D=[[@26,78:78='D',<335>,1:78]]}}, interface={unnamed_1=[{name=D, table_ref=gg}]}}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void selectWhereExistsCorrelatedSubquery() {
		String query = "SELECT s.stvmajr_code AS concentration_code," +
			" s.stvmajr_desc AS concentration_desc, 'T' AS active_ind" +
			" FROM bnr_stvmajr s WHERE s.stvmajr_valid_concentratn_ind = 'Y'" +
			" AND NOT EXISTS ( SELECT 1 FROM cat_concentration c" +
    		" WHERE c.concentration_code = s.stvmajr_code )";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=stvmajr_code, table_ref=s}, alias=concentration_code}, 2={column={name=stvmajr_desc, table_ref=s}, alias=concentration_desc}, 3={alias=active_ind, literal='T'}}, from={table={alias=s, table=bnr_stvmajr}}, where={and={1={condition={left={column={name=stvmajr_valid_concentratn_ind, table_ref=s}}, right={literal='Y'}, operator==}}, 2={NOT={exists={select={1={literal=1}}, from={table={alias=c, table=cat_concentration}}, where={condition={left={column={name=concentration_code, table_ref=c}}, right={column={name=stvmajr_code, table_ref=s}}, operator==}}, operator=EXISTS}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[concentration_desc, concentration_code, active_ind]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{cat_concentration={concentration_code=[[@35,221:221='c',<335>,1:221]]}, bnr_stvmajr={stvmajr_code=[[@39,244:244='s',<335>,1:244]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={unnamed_0=[[@30,188:188='1',<298>,1:188]]}, query2={concentration_desc=[[@11,63:80='concentration_desc',<335>,1:63]], concentration_code=[[@5,25:42='concentration_code',<335>,1:25]], active_ind=[[@15,90:99='active_ind',<335>,1:90]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query2={query_dictionary={concentration_desc=[[@11,63:80='concentration_desc',<335>,1:63]], concentration_code=[[@5,25:42='concentration_code',<335>,1:25]], active_ind=[[@15,90:99='active_ind',<335>,1:90]]}, table_dictionary={bnr_stvmajr={stvmajr_code=[[@39,244:244='s',<335>,1:244]]}}, def_query0={query_dictionary={unnamed_0=[[@30,188:188='1',<298>,1:188]]}, table_dictionary={cat_concentration={concentration_code=[[@35,221:221='c',<335>,1:221]]}}, filters=[{name=concentration_code, table_ref=c}, {name=stvmajr_code, table_ref=s}], interface={unnamed_0=[]}, table_alias={c=cat_concentration}}, filters=[{name=stvmajr_valid_concentratn_ind, table_ref=s}], interface={concentration_desc=[{name=stvmajr_desc, table_ref=s}], concentration_code=[{name=stvmajr_code, table_ref=s}], active_ind=[]}, exists1=query0, table_alias={s=bnr_stvmajr}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void selectWhereVariableExists() {
		String query = "SELECT s.stvmajr_code AS concentration_code," +
			" s.stvmajr_desc AS concentration_desc, 'T' AS active_ind" +
			" FROM bnr_stvmajr s WHERE s.stvmajr_valid_concentratn_ind = 'Y'" +
			" AND NOT EXISTS <table_variable>";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=stvmajr_code, table_ref=s}, alias=concentration_code}, 2={column={name=stvmajr_desc, table_ref=s}, alias=concentration_desc}, 3={alias=active_ind, literal='T'}}, from={table={alias=s, table=bnr_stvmajr}}, where={and={1={condition={left={column={name=stvmajr_valid_concentratn_ind, table_ref=s}}, right={literal='Y'}, operator==}}, 2={NOT={exists={substitution={name=<table_variable>, type=tuple}, operator=EXISTS}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[concentration_desc, concentration_code, active_ind]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<table_variable>=tuple}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{bnr_stvmajr={stvmajr_desc=[[@7,45:45='s',<335>,1:45]], stvmajr_valid_concentratn_ind=[[@20,126:126='s',<335>,1:126]], stvmajr_code=[[@1,7:7='s',<335>,1:7]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={concentration_desc=[[@11,63:80='concentration_desc',<335>,1:63]], concentration_code=[[@5,25:42='concentration_code',<335>,1:25]], active_ind=[[@15,90:99='active_ind',<335>,1:90]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={query_dictionary={concentration_desc=[[@11,63:80='concentration_desc',<335>,1:63]], concentration_code=[[@5,25:42='concentration_code',<335>,1:25]], active_ind=[[@15,90:99='active_ind',<335>,1:90]]}, table_dictionary={bnr_stvmajr={stvmajr_desc=[[@7,45:45='s',<335>,1:45]], stvmajr_valid_concentratn_ind=[[@20,126:126='s',<335>,1:126]], stvmajr_code=[[@1,7:7='s',<335>,1:7]]}}, filters=[{name=stvmajr_valid_concentratn_ind, table_ref=s}], interface={concentration_desc=[{name=stvmajr_desc, table_ref=s}], concentration_code=[{name=stvmajr_code, table_ref=s}], active_ind=[]}, table_alias={s=bnr_stvmajr}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void havingScalarSubqueryComparisonTest() {
		String query = "SELECT e.dept, SUM(e.salary) AS total_salary " +
			"FROM employees e " +
			"GROUP BY e.dept " +
			"HAVING SUM(e.salary) > (SELECT AVG(salary) FROM employees)";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=dept, table_ref=e}}, 2={function={function_name=SUM, qualifier=null, parameters={column={name=salary, table_ref=e}}}, alias=total_salary}}, having={condition={left={function={function_name=SUM, qualifier=null, parameters={column={name=salary, table_ref=e}}}}, right={select={1={function={function_name=AVG, qualifier=null, parameters={column={name=salary, table_ref=null}}}}}, from={table={alias=null, table=employees}}}, operator=>}}, from={table={alias=e, table=employees}}, groupby={1={column={name=dept, table_ref=e}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[total_salary, dept]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{employees={dept=[[@1,7:7='e',<335>,1:7], [@18,71:71='e',<335>,1:71]], salary=[[@33,113:118='salary',<335>,1:113], [@7,19:19='e',<335>,1:19], [@24,89:89='e',<335>,1:89]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={unnamed_0=[[@34,119:119=')',<286>,1:119]]}, query2={total_salary=[[@12,32:43='total_salary',<335>,1:32]], dept=[[@3,9:12='dept',<335>,1:9]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query2={query_dictionary={total_salary=[[@12,32:43='total_salary',<335>,1:32]], dept=[[@3,9:12='dept',<335>,1:9]]}, table_dictionary={employees={dept=[[@1,7:7='e',<335>,1:7], [@18,71:71='e',<335>,1:71]], salary=[[@7,19:19='e',<335>,1:19], [@24,89:89='e',<335>,1:89]]}}, grouped_by=[{name=dept, table_ref=e}], def_query0={query_dictionary={unnamed_0=[[@34,119:119=')',<286>,1:119]]}, table_dictionary={employees={salary=[[@33,113:118='salary',<335>,1:113], [@7,19:19='e',<335>,1:19], [@24,89:89='e',<335>,1:89]]}}, interface={unnamed_0=[{name=salary, table_ref=employees}]}}, filters=[{name=salary, table_ref=e}, {query=query0}], interface={total_salary=[{name=salary, table_ref=e}], dept=[{name=dept, table_ref=e}]}, table_alias={e=employees}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void havingExistsCorrelatedSubqueryTest() {
		// EXISTS subquery in HAVING, correlated on customer_id
		String query = "SELECT e.customer_id, COUNT(*) AS order_count \n" +
			"FROM customers e \n" +
			"GROUP BY e.customer_id \n" +
			"HAVING EXISTS (SELECT 1 FROM orders o WHERE o.customer_id = e.customer_id)";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=customer_id, table_ref=e}}, 2={function={function_name=COUNT, qualifier=null, parameters=*}, alias=order_count}}, having={exists={select={1={literal=1}}, from={table={alias=o, table=orders}}, where={condition={left={column={name=customer_id, table_ref=o}}, right={column={name=customer_id, table_ref=e}}, operator==}}, operator=EXISTS}}, from={table={alias=e, table=customers}}, groupby={1={column={name=customer_id, table_ref=e}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[order_count, customer_id]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{orders={customer_id=[[@28,133:133='o',<335>,4:44]]}, customers={customer_id=[[@32,149:149='e',<335>,4:60]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={unnamed_0=[[@23,111:111='1',<298>,4:22]]}, query2={order_count=[[@10,34:44='order_count',<335>,1:34]], customer_id=[[@3,9:19='customer_id',<335>,1:9]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query2={query_dictionary={order_count=[[@10,34:44='order_count',<335>,1:34]], customer_id=[[@3,9:19='customer_id',<335>,1:9]]}, table_dictionary={customers={customer_id=[[@32,149:149='e',<335>,4:60]]}}, grouped_by=[{name=customer_id, table_ref=e}], def_query0={query_dictionary={unnamed_0=[[@23,111:111='1',<298>,4:22]]}, table_dictionary={orders={customer_id=[[@28,133:133='o',<335>,4:44]]}}, filters=[{name=customer_id, table_ref=o}, {name=customer_id, table_ref=e}], interface={unnamed_0=[]}, table_alias={o=orders}}, filters=[], interface={order_count=[], customer_id=[{name=customer_id, table_ref=e}]}, exists1=query0, table_alias={e=customers}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void selectWhereScalarConditionCorrelatedSubquery() {
		String query = "SELECT s.stvmajr_code AS concentration_code," +
			" s.stvmajr_desc AS concentration_desc, 'T' AS active_ind" +
			" FROM bnr_stvmajr s WHERE " +
			" s.stvmajr_code > ( SELECT max(c.concentration_code) FROM cat_concentration c" +
    		" WHERE c.concentration_code != s.stvmajr_code )";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=stvmajr_code, table_ref=s}, alias=concentration_code}, 2={column={name=stvmajr_desc, table_ref=s}, alias=concentration_desc}, 3={alias=active_ind, literal='T'}}, from={table={alias=s, table=bnr_stvmajr}}, where={condition={left={column={name=stvmajr_code, table_ref=s}}, right={select={1={function={function_name=max, qualifier=null, parameters={column={name=concentration_code, table_ref=c}}}}}, from={table={alias=c, table=cat_concentration}}, where={condition={left={column={name=concentration_code, table_ref=c}}, right={column={name=stvmajr_code, table_ref=s}}, operator=!=}}}, operator=>}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[concentration_desc, concentration_code, active_ind]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
				//{ee={D=[@8,26:26='D',<335>,1:26]}, ff={c=[@21,70:70='c',<335>,1:70]}, tab1={a=[@1,8:8='a',<335>,1:8]}}
		Assert.assertEquals("Table Dictionary is wrong", "{cat_concentration={concentration_code=[[@28,157:157='c',<335>,1:157], [@36,210:210='c',<335>,1:210]]}, bnr_stvmajr={stvmajr_code=[[@40,234:234='s',<335>,1:234]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={unnamed_0=[[@31,177:177=')',<286>,1:177]]}, query2={concentration_desc=[[@11,63:80='concentration_desc',<335>,1:63]], concentration_code=[[@5,25:42='concentration_code',<335>,1:25]], active_ind=[[@15,90:99='active_ind',<335>,1:90]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query2={query_dictionary={concentration_desc=[[@11,63:80='concentration_desc',<335>,1:63]], concentration_code=[[@5,25:42='concentration_code',<335>,1:25]], active_ind=[[@15,90:99='active_ind',<335>,1:90]]}, table_dictionary={bnr_stvmajr={stvmajr_code=[[@40,234:234='s',<335>,1:234]]}}, def_query0={query_dictionary={unnamed_0=[[@31,177:177=')',<286>,1:177]]}, table_dictionary={cat_concentration={concentration_code=[[@28,157:157='c',<335>,1:157], [@36,210:210='c',<335>,1:210]]}}, filters=[{name=concentration_code, table_ref=c}, {name=stvmajr_code, table_ref=s}], interface={unnamed_0=[{name=concentration_code, table_ref=c}]}, table_alias={c=cat_concentration}}, filters=[{name=stvmajr_code, table_ref=s}, {query=query0}], interface={concentration_desc=[{name=stvmajr_desc, table_ref=s}], concentration_code=[{name=stvmajr_code, table_ref=s}], active_ind=[]}, table_alias={s=bnr_stvmajr}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void selectOrderByScalarCorrelatedSubquery() {
		String query = "SELECT s.stvmajr_code AS concentration_code," +
			" s.stvmajr_desc AS concentration_desc, 'T' AS active_ind" +
			" FROM bnr_stvmajr s order by ( SELECT min(c.code) FROM cat_concentration c" +
    		" WHERE c.concentration_code = s.stvmajr_code )";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=stvmajr_code, table_ref=s}, alias=concentration_code}, 2={column={name=stvmajr_desc, table_ref=s}, alias=concentration_desc}, 3={alias=active_ind, literal='T'}}, orderby={1={null_order=null, predicand={select={1={function={function_name=min, qualifier=null, parameters={column={name=code, table_ref=c}}}}}, from={table={alias=c, table=cat_concentration}}, where={condition={left={column={name=concentration_code, table_ref=c}}, right={column={name=stvmajr_code, table_ref=s}}, operator==}}}, sort_order=ASC}}, from={table={alias=s, table=bnr_stvmajr}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[concentration_desc, concentration_code, active_ind]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
				//{ee={D=[@8,26:26='D',<335>,1:26]}, ff={c=[@21,70:70='c',<335>,1:70]}, tab1={a=[@1,8:8='a',<335>,1:8]}}
		Assert.assertEquals("Table Dictionary is wrong", "{cat_concentration={code=[[@25,142:142='c',<335>,1:142]], concentration_code=[[@33,181:181='c',<335>,1:181]]}, bnr_stvmajr={stvmajr_code=[[@37,204:204='s',<335>,1:204]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={unnamed_0=[[@28,148:148=')',<286>,1:148]]}, query2={concentration_desc=[[@11,63:80='concentration_desc',<335>,1:63]], concentration_code=[[@5,25:42='concentration_code',<335>,1:25]], active_ind=[[@15,90:99='active_ind',<335>,1:90]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query2={query_dictionary={concentration_desc=[[@11,63:80='concentration_desc',<335>,1:63]], concentration_code=[[@5,25:42='concentration_code',<335>,1:25]], active_ind=[[@15,90:99='active_ind',<335>,1:90]]}, table_dictionary={bnr_stvmajr={stvmajr_code=[[@37,204:204='s',<335>,1:204]]}}, def_query0={query_dictionary={unnamed_0=[[@28,148:148=')',<286>,1:148]]}, table_dictionary={cat_concentration={code=[[@25,142:142='c',<335>,1:142]], concentration_code=[[@33,181:181='c',<335>,1:181]]}}, filters=[{name=concentration_code, table_ref=c}, {name=stvmajr_code, table_ref=s}], interface={unnamed_0=[{name=code, table_ref=c}]}, table_alias={c=cat_concentration}}, ordered_by=[{query=query0}], interface={concentration_desc=[{name=stvmajr_desc, table_ref=s}], concentration_code=[{name=stvmajr_code, table_ref=s}], active_ind=[]}, table_alias={s=bnr_stvmajr}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void subqueryParseTest() {
		// traps that there's no COURSES table in the from statement
		final String query = "SELECT aa.scbcrse_coll_code FROM scbcrse aa "
				+ " WHERE aa.scbcrse_subj_code = courses.subj_code " + " AND aa.scbcrse_crse_numb = courses.crse_numb "
				+ " AND aa.scbcrse_eff_term = ( " + " SELECT MAX(scbcrse_eff_term) " + " FROM scbcrse "
				+ " WHERE scbcrse_subj_code = courses.subj_code " + " AND scbcrse_crse_numb = courses.crse_numb "
				+ " AND scbcrse_eff_term <= courses.term) ";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		Snippet snippet = extractor.getSnippet();
		assertFatalDiagnosticCount(snippet,
				"QUALIFIED_COLUMN_NOT_FOUND_IN_TABLE",
				"Source Table not found for Column 'subj_code' at (l:1 c:74), (l:1 c:238). No alias or table called 'courses'.",
				null,
				1);
		assertFatalDiagnosticCount(snippet,
				"QUALIFIED_COLUMN_NOT_FOUND_IN_TABLE",
				"Source Table not found for Column 'crse_numb' at (l:1 c:120), (l:1 c:281). No alias or table called 'courses'.",
				null,
				1);
		assertFatalDiagnosticCount(snippet,
				"QUALIFIED_COLUMN_NOT_FOUND_IN_TABLE",
				"Source Table not found for Column 'term' at (l:1 c:324). No alias or table called 'courses'.",
				null,
				1);

		assertFatalDiagnosticCount(snippet,
				"QUALIFIED_COLUMN_NOT_FOUND_IN_TABLE",
				null,
				null,
				3);
		assertFatalDiagnosticCount(snippet,
				null,
				null,
				null,
				3);
	}


	@Test
	public void selectItemSubqueryStatementParseTest() {
		final String query = " SELECT first_item,( " + " SELECT item " + " FROM sgbstdn "
				+ " WHERE sgbstdn_levl_code = 'US' " + " ) AS INTERNATIONAL_IND " + " FROM sgbstdn ";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
	}


	@Test
	public void selectWithBasicTest() {
		String sql = "with first as (select a from mulch), " 
			+ "\n second as (select b from lawn) "
			+ "\n select * from first, second where first.a = second.b";
		final SQLSelectParserParser parser = parse(sql);
			SqlParseEventWalker extractor = runParsertest(sql, parser);
		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={with={1={cte={select={1={column={name=a, table_ref=null}}}, from={table={alias=null, table=mulch}}}, alias=first}, 2={cte={select={1={column={name=b, table_ref=null}}}, from={table={alias=null, table=lawn}}}, alias=second}}, query={select={1={column={name=*, table_ref=*}}}, from={join={1={table={alias=null, table=first}}, 2={table={alias=null, table=second}}}}, where={condition={left={column={name=a, table_ref=first}}, right={column={name=b, table_ref=second}}, operator==}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{lawn={b=[[@14,57:57='b',<335>,2:19]]}, first={a=[[@25,106:110='first',<87>,3:35]], *=[[@19,79:79='*',<289>,3:8]]}, mulch={a=[[@5,22:22='a',<335>,1:22]]}, second={b=[[@29,116:121='second',<134>,3:45]], *=[[@19,79:79='*',<289>,3:8]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={a=[[@5,22:22='a',<335>,1:22]], *=[[@19,79:79='*',<289>,3:8]]}, query1={b=[[@14,57:57='b',<335>,2:19]], *=[[@19,79:79='*',<289>,3:8]]}, query2={*=[[@19,79:79='*',<289>,3:8]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query2={query_dictionary={*=[[@19,79:79='*',<289>,3:8]]}, table_dictionary={first={a=[[@25,106:110='first',<87>,3:35]], *=[[@19,79:79='*',<289>,3:8]]}, second={b=[[@29,116:121='second',<134>,3:45]], *=[[@19,79:79='*',<289>,3:8]]}}, def_query1={query_dictionary={b=[[@14,57:57='b',<335>,2:19]], *=[[@19,79:79='*',<289>,3:8]]}, table_dictionary={lawn={b=[[@14,57:57='b',<335>,2:19]]}}, interface={b=[{name=b, table_ref=lawn}]}}, def_query0={query_dictionary={a=[[@5,22:22='a',<335>,1:22]], *=[[@19,79:79='*',<289>,3:8]]}, table_dictionary={mulch={a=[[@5,22:22='a',<335>,1:22]]}}, interface={a=[{name=a, table_ref=mulch}]}}, filters=[{name=a, table_ref=first}, {name=b, table_ref=second}], interface={*=[{name=*, table_ref=*}]}, table_alias={first=query0, second=query1}}}",
				extractor.getSymbolTable().toString());
}


	@Test
	public void selectWithUnionTest() {
		String sql = "with first as (select a from mulch union select b from clay), " 
			+ "\n second as (select b from lawn) "
			+ "\n select * from first, second where first.a = second.b";
		final SQLSelectParserParser parser = parse(sql);
			SqlParseEventWalker extractor = runParsertest(sql, parser);
		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={with={1={cte={union={1={select={1={column={name=a, table_ref=null}}}, from={table={alias=null, table=mulch}}}, 2={union={qualifier=null, operator=union}}, 3={select={1={column={name=b, table_ref=null}}}, from={table={alias=null, table=clay}}}}}, alias=first}, 2={cte={select={1={column={name=b, table_ref=null}}}, from={table={alias=null, table=lawn}}}, alias=second}}, query={select={1={column={name=*, table_ref=*}}}, from={join={1={table={alias=null, table=first}}, 2={table={alias=null, table=second}}}}, where={condition={left={column={name=a, table_ref=first}}, right={column={name=b, table_ref=second}}, operator==}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{lawn={b=[[@19,82:82='b',<335>,2:19]]}, first={a=[[@30,131:135='first',<87>,3:35]], *=[[@24,104:104='*',<289>,3:8]]}, mulch={a=[[@5,22:22='a',<335>,1:22]]}, clay={b=[[@10,48:48='b',<335>,1:48]]}, second={b=[[@34,141:146='second',<134>,3:45]], *=[[@24,104:104='*',<289>,3:8]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query4={*=[[@24,104:104='*',<289>,3:8]]}, query0={a=[[@5,22:22='a',<335>,1:22]], *=[[@24,104:104='*',<289>,3:8]]}, query1={b=[[@10,48:48='b',<335>,1:48]], *=[[@24,104:104='*',<289>,3:8]]}, query3={b=[[@19,82:82='b',<335>,2:19]], *=[[@24,104:104='*',<289>,3:8]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query4={query_dictionary={*=[[@24,104:104='*',<289>,3:8]]}, def_union2={query0={query_dictionary={a=[[@5,22:22='a',<335>,1:22]], *=[[@24,104:104='*',<289>,3:8]]}, table_dictionary={mulch={a=[[@5,22:22='a',<335>,1:22]]}}, interface={a=[{name=a, table_ref=mulch}]}}, interface={a=query_column}, query1={query_dictionary={b=[[@10,48:48='b',<335>,1:48]], *=[[@24,104:104='*',<289>,3:8]]}, table_dictionary={clay={b=[[@10,48:48='b',<335>,1:48]]}}, interface={b=[{name=b, table_ref=clay}]}}}, table_dictionary={first={a=[[@30,131:135='first',<87>,3:35]], *=[[@24,104:104='*',<289>,3:8]]}, second={b=[[@34,141:146='second',<134>,3:45]], *=[[@24,104:104='*',<289>,3:8]]}}, filters=[{name=a, table_ref=first}, {name=b, table_ref=second}], interface={*=[{name=*, table_ref=*}]}, table_alias={first=union2, second=query3}, def_query3={query_dictionary={b=[[@19,82:82='b',<335>,2:19]], *=[[@24,104:104='*',<289>,3:8]]}, table_dictionary={lawn={b=[[@19,82:82='b',<335>,2:19]]}}, interface={b=[{name=b, table_ref=lawn}]}}}}",
				extractor.getSymbolTable().toString());
}


	@Test
	public void selectWithSameSubqueriesTest() {
		String sql = "select * from (select a from mulch union select b from clay) first, (select b from lawn) second where first.a = second.b";
		final SQLSelectParserParser parser = parse(sql);
			SqlParseEventWalker extractor = runParsertest(sql, parser);
		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=*, table_ref=*}}}, from={join={1={table={alias=first, query={union={1={select={1={column={name=a, table_ref=null}}}, from={table={alias=null, table=mulch}}}, 2={union={qualifier=null, operator=union}}, 3={select={1={column={name=b, table_ref=null}}}, from={table={alias=null, table=clay}}}}}}}, 2={table={alias=second, query={select={1={column={name=b, table_ref=null}}}, from={table={alias=null, table=lawn}}}}}}}, where={condition={left={column={name=a, table_ref=first}}, right={column={name=b, table_ref=second}}, operator==}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{lawn={b=[[@18,76:76='b',<335>,1:76]]}, mulch={a=[[@5,22:22='a',<335>,1:22]]}, clay={b=[[@10,48:48='b',<335>,1:48]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query4={*=[[@1,7:7='*',<289>,1:7]]}, query0={a=[[@5,22:22='a',<335>,1:22]], *=[[@1,7:7='*',<289>,1:7]]}, query1={b=[[@10,48:48='b',<335>,1:48]], *=[[@1,7:7='*',<289>,1:7]]}, query3={b=[[@18,76:76='b',<335>,1:76], [@28,112:117='second',<134>,1:112]], *=[[@1,7:7='*',<289>,1:7]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query4={def_union2={query0={query_dictionary={a=[[@5,22:22='a',<335>,1:22]], *=[[@1,7:7='*',<289>,1:7]]}, table_dictionary={mulch={a=[[@5,22:22='a',<335>,1:22]]}}, interface={a=[{name=a, table_ref=mulch}]}}, interface={a=query_column}, query1={query_dictionary={b=[[@10,48:48='b',<335>,1:48]], *=[[@1,7:7='*',<289>,1:7]]}, table_dictionary={clay={b=[[@10,48:48='b',<335>,1:48]]}}, interface={b=[{name=b, table_ref=clay}]}}}, query_dictionary={*=[[@1,7:7='*',<289>,1:7]]}, table_dictionary={}, filters=[{name=a, table_ref=first}, {name=b, table_ref=second}], interface={*=[{name=*, table_ref=*}]}, def_query3={query_dictionary={b=[[@18,76:76='b',<335>,1:76], [@28,112:117='second',<134>,1:112]], *=[[@1,7:7='*',<289>,1:7]]}, table_dictionary={lawn={b=[[@18,76:76='b',<335>,1:76]]}}, interface={b=[{name=b, table_ref=lawn}]}}, table_alias={first=union2, second=query3}}}",
				extractor.getSymbolTable().toString());
}


	@Test
	public void selectWithMultipleSimpleUnqualifiedReferencesCTEV1() {
		String sql = "WITH " 
			+ "\n upsert AS  (select stvmajr_code AS major_code, stvmajr_desc AS major_desc, 'T' AS active_ind"
			+ " from bnr_stvmajr where stvmajr_valid_concentratn_ind = 'Y' "
			+ " and not exists (select 1 from cat_concentration where cat_concentration.major_code = bnr_stvmajr.stvmajr_code)), "
			+ " upsert2 AS (select stvmajr_code AS t2_major_code, stvmajr_desc AS t2_major_desc, 'T' AS active_ind from tab2)"
			+ "\n Select  major_code, t2_major_code, major_desc,  active_ind FROM upsert join upsert2 on upsert.major_code = upsert2.t2_major_code";
		final SQLSelectParserParser parser = parse(sql);
		
		SqlParseEventWalker extractor = runParsertest(sql, parser);
		assertNoFatalErrors(extractor);
		Snippet snippet = extractor.getSnippet();
		assertDiagnosticCountBySeverity(snippet, "UNRESOLVED_UNQUALIFIED_COLUMNS", ParseDiagnostic.Severity.ERROR, null, null, 1);
		assertDiagnosticCountBySeverity(snippet, "AMBIGUOUS_COLUMN_REFERENCE", ParseDiagnostic.Severity.SEVERE_WARNING, null, null, 4);
	}


	@Test
	public void selectWithMultipleSimpleUnqualifiedReferencesCTEV2() {
		String sql = "WITH " 
			+ "\n aaa AS  (select col1 from tab1), "
			+ "\n bbb AS (select col2 from tab2)"
			+ "\n Select  col1, col2 FROM aaa join bbb on aaa.col1 = bbb.col2";
		final SQLSelectParserParser parser = parse(sql);
		
		SqlParseEventWalker extractor = runParsertest(sql, parser);
		assertNoFatalErrors(extractor);
		Snippet snippet = extractor.getSnippet();
		assertDiagnosticCountBySeverity(snippet, "UNRESOLVED_UNQUALIFIED_COLUMNS", ParseDiagnostic.Severity.ERROR, null, null, 1);
		assertDiagnosticCountBySeverity(snippet, "AMBIGUOUS_COLUMN_REFERENCE", ParseDiagnostic.Severity.SEVERE_WARNING, null, null, 2);
	}


	@Test
	public void queryAndUnionUnqualifiedReferencesCTEV3() {
		String sql = "WITH " 
			+ "\n aaa AS  (select col1 from tab1), "
			+ "\n bbb AS (select col1 from  answer union select col2 from problem)"
			+ "\n Select  col1, col2 FROM aaa join bbb on aaa.col1 = bbb.col2";
		final SQLSelectParserParser parser = parse(sql);
		
		SqlParseEventWalker extractor = runParsertest(sql, parser);
		assertNoFatalErrors(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={with={1={cte={select={1={column={name=col1, table_ref=null}}}, from={table={alias=null, table=tab1}}}, alias=aaa}, 2={cte={union={1={select={1={column={name=col1, table_ref=null}}}, from={table={alias=null, table=answer}}}, 2={union={qualifier=null, operator=union}}, 3={select={1={column={name=col2, table_ref=null}}}, from={table={alias=null, table=problem}}}}}, alias=bbb}}, query={select={1={column={name=col1, table_ref=null}}, 2={column={name=col2, table_ref=null}}}, from={join={1={table={alias=null, table=aaa}}, 2={join=join, on={condition={left={column={name=col1, table_ref=aaa}}, right={column={name=col2, table_ref=bbb}}, operator==}}}, 3={table={alias=null, table=bbb}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[col2, col1]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{aaa={col1=[[@32,148:150='aaa',<335>,4:41]]}, problem={col2=[[@19,88:91='col2',<335>,3:47]]}, bbb={col2=[[@36,159:161='bbb',<335>,4:52]]}, answer={col1=[[@14,57:60='col1',<335>,3:16]]}, tab1={col1=[[@5,23:26='col1',<335>,2:17]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query4={col2=[[@26,122:125='col2',<335>,4:15]], col1=[[@24,116:119='col1',<335>,4:9]]}, query0={col1=[[@5,23:26='col1',<335>,2:17]]}, query1={col1=[[@14,57:60='col1',<335>,3:16]]}, query2={col2=[[@19,88:91='col2',<335>,3:47]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query4={def_union3={interface={col1=query_column}, query1={query_dictionary={col1=[[@14,57:60='col1',<335>,3:16]]}, table_dictionary={answer={col1=[[@14,57:60='col1',<335>,3:16]]}}, interface={col1=[{name=col1, table_ref=answer}]}}, query2={query_dictionary={col2=[[@19,88:91='col2',<335>,3:47]]}, table_dictionary={problem={col2=[[@19,88:91='col2',<335>,3:47]]}}, interface={col2=[{name=col2, table_ref=problem}]}}}, query_dictionary={col2=[[@26,122:125='col2',<335>,4:15]], col1=[[@24,116:119='col1',<335>,4:9]]}, table_dictionary={aaa={col1=[[@32,148:150='aaa',<335>,4:41]]}, bbb={col2=[[@36,159:161='bbb',<335>,4:52]]}}, def_query0={query_dictionary={col1=[[@5,23:26='col1',<335>,2:17]]}, table_dictionary={tab1={col1=[[@5,23:26='col1',<335>,2:17]]}}, interface={col1=[{name=col1, table_ref=tab1}]}}, filters=[{name=col1, table_ref=aaa}, {name=col2, table_ref=bbb}], interface={col2=[{name=col2, table_ref=null}], col1=[{name=col1, table_ref=null}]}, table_alias={aaa=query0, bbb=union3}}}",
				extractor.getSymbolTable().toString());

		Snippet snippet = extractor.getSnippet();
		assertDiagnosticCountBySeverity(snippet, "UNRESOLVED_UNQUALIFIED_COLUMNS", ParseDiagnostic.Severity.ERROR, null, null, 1);
		assertDiagnosticCountBySeverity(snippet, "AMBIGUOUS_COLUMN_REFERENCE", ParseDiagnostic.Severity.SEVERE_WARNING, null, null, 2);
	}


	@Test
	public void unionAndQueryUnqualifiedReferencesCTEV4() {
		String sql = "WITH " 
			+ "\n aaa AS  (select col1 from  answer union select col2 from problem), "
			+ "\n bbb AS (select col2 from tab2)"
			+ "\n Select  col1, col2 FROM aaa join bbb on aaa.col1 = bbb.col2";
		final SQLSelectParserParser parser = parse(sql);
		
		SqlParseEventWalker extractor = runParsertest(sql, parser);
		assertNoFatalErrors(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={with={1={cte={union={1={select={1={column={name=col1, table_ref=null}}}, from={table={alias=null, table=answer}}}, 2={union={qualifier=null, operator=union}}, 3={select={1={column={name=col2, table_ref=null}}}, from={table={alias=null, table=problem}}}}}, alias=aaa}, 2={cte={select={1={column={name=col2, table_ref=null}}}, from={table={alias=null, table=tab2}}}, alias=bbb}}, query={select={1={column={name=col1, table_ref=null}}, 2={column={name=col2, table_ref=null}}}, from={join={1={table={alias=null, table=aaa}}, 2={join=join, on={condition={left={column={name=col1, table_ref=aaa}}, right={column={name=col2, table_ref=bbb}}, operator==}}}, 3={table={alias=null, table=bbb}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[col2, col1]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{aaa={col1=[[@32,148:150='aaa',<335>,4:41]]}, problem={col2=[[@10,54:57='col2',<335>,2:48]]}, bbb={col2=[[@36,159:161='bbb',<335>,4:52]]}, answer={col1=[[@5,23:26='col1',<335>,2:17]]}, tab2={col2=[[@19,91:94='col2',<335>,3:16]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query4={col2=[[@26,122:125='col2',<335>,4:15]], col1=[[@24,116:119='col1',<335>,4:9]]}, query0={col1=[[@5,23:26='col1',<335>,2:17]]}, query1={col2=[[@10,54:57='col2',<335>,2:48]]}, query3={col2=[[@19,91:94='col2',<335>,3:16]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query4={query_dictionary={col2=[[@26,122:125='col2',<335>,4:15]], col1=[[@24,116:119='col1',<335>,4:9]]}, def_union2={query0={query_dictionary={col1=[[@5,23:26='col1',<335>,2:17]]}, table_dictionary={answer={col1=[[@5,23:26='col1',<335>,2:17]]}}, interface={col1=[{name=col1, table_ref=answer}]}}, interface={col1=query_column}, query1={query_dictionary={col2=[[@10,54:57='col2',<335>,2:48]]}, table_dictionary={problem={col2=[[@10,54:57='col2',<335>,2:48]]}}, interface={col2=[{name=col2, table_ref=problem}]}}}, table_dictionary={aaa={col1=[[@32,148:150='aaa',<335>,4:41]]}, bbb={col2=[[@36,159:161='bbb',<335>,4:52]]}}, filters=[{name=col1, table_ref=aaa}, {name=col2, table_ref=bbb}], interface={col2=[{name=col2, table_ref=null}], col1=[{name=col1, table_ref=null}]}, table_alias={aaa=union2, bbb=query3}, def_query3={query_dictionary={col2=[[@19,91:94='col2',<335>,3:16]]}, table_dictionary={tab2={col2=[[@19,91:94='col2',<335>,3:16]]}}, interface={col2=[{name=col2, table_ref=tab2}]}}}}",
				extractor.getSymbolTable().toString());

				Snippet snippet = extractor.getSnippet();
		assertDiagnosticCountBySeverity(snippet, "UNRESOLVED_UNQUALIFIED_COLUMNS", ParseDiagnostic.Severity.ERROR, null, null, 1);
		assertDiagnosticCountBySeverity(snippet, "AMBIGUOUS_COLUMN_REFERENCE", ParseDiagnostic.Severity.SEVERE_WARNING, null, null, 2);
	}


	@Test
	public void queryAndIntersectUnqualifiedReferencesCTEV5() {
		String sql = "WITH " 
			+ "\n aaa AS  (select col1 from tab1), "
			+ "\n bbb AS (select col1 from  answer intersect select col2 from problem)"
			+ "\n Select  col1, col2 FROM aaa join bbb on aaa.col1 = bbb.col2";
		final SQLSelectParserParser parser = parse(sql);
		
		SqlParseEventWalker extractor = runParsertest(sql, parser);
		assertNoFatalErrors(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={with={1={cte={select={1={column={name=col1, table_ref=null}}}, from={table={alias=null, table=tab1}}}, alias=aaa}, 2={cte={intersect={1={select={1={column={name=col1, table_ref=null}}}, from={table={alias=null, table=answer}}}, 2={intersect={qualifier=null, operator=intersect}}, 3={select={1={column={name=col2, table_ref=null}}}, from={table={alias=null, table=problem}}}}}, alias=bbb}}, query={select={1={column={name=col1, table_ref=null}}, 2={column={name=col2, table_ref=null}}}, from={join={1={table={alias=null, table=aaa}}, 2={join=join, on={condition={left={column={name=col1, table_ref=aaa}}, right={column={name=col2, table_ref=bbb}}, operator==}}}, 3={table={alias=null, table=bbb}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[col2, col1]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{aaa={col1=[[@32,152:154='aaa',<335>,4:41]]}, problem={col2=[[@19,92:95='col2',<335>,3:51]]}, bbb={col2=[[@36,163:165='bbb',<335>,4:52]]}, answer={col1=[[@14,57:60='col1',<335>,3:16]]}, tab1={col1=[[@5,23:26='col1',<335>,2:17]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query4={col2=[[@26,126:129='col2',<335>,4:15]], col1=[[@24,120:123='col1',<335>,4:9]]}, query0={col1=[[@5,23:26='col1',<335>,2:17]]}, query1={col1=[[@14,57:60='col1',<335>,3:16]]}, query2={col2=[[@19,92:95='col2',<335>,3:51]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query4={query_dictionary={col2=[[@26,126:129='col2',<335>,4:15]], col1=[[@24,120:123='col1',<335>,4:9]]}, table_dictionary={aaa={col1=[[@32,152:154='aaa',<335>,4:41]]}, bbb={col2=[[@36,163:165='bbb',<335>,4:52]]}}, def_intersect3={interface={col1=query_column}, query1={query_dictionary={col1=[[@14,57:60='col1',<335>,3:16]]}, table_dictionary={answer={col1=[[@14,57:60='col1',<335>,3:16]]}}, interface={col1=[{name=col1, table_ref=answer}]}}, query2={query_dictionary={col2=[[@19,92:95='col2',<335>,3:51]]}, table_dictionary={problem={col2=[[@19,92:95='col2',<335>,3:51]]}}, interface={col2=[{name=col2, table_ref=problem}]}}}, def_query0={query_dictionary={col1=[[@5,23:26='col1',<335>,2:17]]}, table_dictionary={tab1={col1=[[@5,23:26='col1',<335>,2:17]]}}, interface={col1=[{name=col1, table_ref=tab1}]}}, filters=[{name=col1, table_ref=aaa}, {name=col2, table_ref=bbb}], interface={col2=[{name=col2, table_ref=null}], col1=[{name=col1, table_ref=null}]}, table_alias={aaa=query0, bbb=intersect3}}}",
				extractor.getSymbolTable().toString());

				Snippet snippet = extractor.getSnippet();
		assertDiagnosticCountBySeverity(snippet, "UNRESOLVED_UNQUALIFIED_COLUMNS", ParseDiagnostic.Severity.ERROR, null, null, 1);
		assertDiagnosticCountBySeverity(snippet, "AMBIGUOUS_COLUMN_REFERENCE", ParseDiagnostic.Severity.SEVERE_WARNING, null, null, 2);
	}


	@Test
	public void intersectAndQueryUnqualifiedReferencesCTEV6() {
		String sql = "WITH " 
			+ "\n aaa AS  (select col1 from  answer intersect select col2 from problem), "
			+ "\n bbb AS (select col2 from tab2)"
			+ "\n Select  col1, col2 FROM aaa join bbb on aaa.col1 = bbb.col2";
		final SQLSelectParserParser parser = parse(sql);
		
		SqlParseEventWalker extractor = runParsertest(sql, parser);
		assertNoFatalErrors(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={with={1={cte={intersect={1={select={1={column={name=col1, table_ref=null}}}, from={table={alias=null, table=answer}}}, 2={intersect={qualifier=null, operator=intersect}}, 3={select={1={column={name=col2, table_ref=null}}}, from={table={alias=null, table=problem}}}}}, alias=aaa}, 2={cte={select={1={column={name=col2, table_ref=null}}}, from={table={alias=null, table=tab2}}}, alias=bbb}}, query={select={1={column={name=col1, table_ref=null}}, 2={column={name=col2, table_ref=null}}}, from={join={1={table={alias=null, table=aaa}}, 2={join=join, on={condition={left={column={name=col1, table_ref=aaa}}, right={column={name=col2, table_ref=bbb}}, operator==}}}, 3={table={alias=null, table=bbb}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[col2, col1]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{aaa={col1=[[@32,152:154='aaa',<335>,4:41]]}, problem={col2=[[@10,58:61='col2',<335>,2:52]]}, bbb={col2=[[@36,163:165='bbb',<335>,4:52]]}, answer={col1=[[@5,23:26='col1',<335>,2:17]]}, tab2={col2=[[@19,95:98='col2',<335>,3:16]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query4={col2=[[@26,126:129='col2',<335>,4:15]], col1=[[@24,120:123='col1',<335>,4:9]]}, query0={col1=[[@5,23:26='col1',<335>,2:17]]}, query1={col2=[[@10,58:61='col2',<335>,2:52]]}, query3={col2=[[@19,95:98='col2',<335>,3:16]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query4={query_dictionary={col2=[[@26,126:129='col2',<335>,4:15]], col1=[[@24,120:123='col1',<335>,4:9]]}, table_dictionary={aaa={col1=[[@32,152:154='aaa',<335>,4:41]]}, bbb={col2=[[@36,163:165='bbb',<335>,4:52]]}}, def_intersect2={query0={query_dictionary={col1=[[@5,23:26='col1',<335>,2:17]]}, table_dictionary={answer={col1=[[@5,23:26='col1',<335>,2:17]]}}, interface={col1=[{name=col1, table_ref=answer}]}}, interface={col1=query_column}, query1={query_dictionary={col2=[[@10,58:61='col2',<335>,2:52]]}, table_dictionary={problem={col2=[[@10,58:61='col2',<335>,2:52]]}}, interface={col2=[{name=col2, table_ref=problem}]}}}, filters=[{name=col1, table_ref=aaa}, {name=col2, table_ref=bbb}], interface={col2=[{name=col2, table_ref=null}], col1=[{name=col1, table_ref=null}]}, table_alias={aaa=intersect2, bbb=query3}, def_query3={query_dictionary={col2=[[@19,95:98='col2',<335>,3:16]]}, table_dictionary={tab2={col2=[[@19,95:98='col2',<335>,3:16]]}}, interface={col2=[{name=col2, table_ref=tab2}]}}}}",
				extractor.getSymbolTable().toString());

				Snippet snippet = extractor.getSnippet();
		assertDiagnosticCountBySeverity(snippet, "UNRESOLVED_UNQUALIFIED_COLUMNS", ParseDiagnostic.Severity.ERROR, null, null, 1);
		assertDiagnosticCountBySeverity(snippet, "AMBIGUOUS_COLUMN_REFERENCE", ParseDiagnostic.Severity.SEVERE_WARNING, null, null, 2);
	}


	@Test
	public void unionAndIntersectUnqualifiedReferencesCTEV7() {
		String sql = "WITH " 
			+ "\n aaa AS  (select col1 from  problem2 union select xyz from problem3), "
			+ "\n bbb AS (select col1 from  answer intersect select col2 from problem)"
			+ "\n Select  col1, col2 FROM aaa join bbb on aaa.col1 = bbb.col2";
		final SQLSelectParserParser parser = parse(sql);
		
		SqlParseEventWalker extractor = runParsertest(sql, parser);
		assertNoFatalErrors(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={with={1={cte={union={1={select={1={column={name=col1, table_ref=null}}}, from={table={alias=null, table=problem2}}}, 2={union={qualifier=null, operator=union}}, 3={select={1={column={name=xyz, table_ref=null}}}, from={table={alias=null, table=problem3}}}}}, alias=aaa}, 2={cte={intersect={1={select={1={column={name=col1, table_ref=null}}}, from={table={alias=null, table=answer}}}, 2={intersect={qualifier=null, operator=intersect}}, 3={select={1={column={name=col2, table_ref=null}}}, from={table={alias=null, table=problem}}}}}, alias=bbb}}, query={select={1={column={name=col1, table_ref=null}}, 2={column={name=col2, table_ref=null}}}, from={join={1={table={alias=null, table=aaa}}, 2={join=join, on={condition={left={column={name=col1, table_ref=aaa}}, right={column={name=col2, table_ref=bbb}}, operator==}}}, 3={table={alias=null, table=bbb}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[col2, col1]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{aaa={col1=[[@37,188:190='aaa',<335>,4:41]]}, problem={col2=[[@24,128:131='col2',<335>,3:51]]}, bbb={col2=[[@41,199:201='bbb',<335>,4:52]]}, answer={col1=[[@19,93:96='col1',<335>,3:16]]}, problem2={col1=[[@5,23:26='col1',<335>,2:17]]}, problem3={xyz=[[@10,56:58='xyz',<335>,2:50]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query4={col2=[[@24,128:131='col2',<335>,3:51]]}, query6={col2=[[@31,162:165='col2',<335>,4:15]], col1=[[@29,156:159='col1',<335>,4:9]]}, query0={col1=[[@5,23:26='col1',<335>,2:17]]}, query1={xyz=[[@10,56:58='xyz',<335>,2:50]]}, query3={col1=[[@19,93:96='col1',<335>,3:16]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query6={query_dictionary={col2=[[@31,162:165='col2',<335>,4:15]], col1=[[@29,156:159='col1',<335>,4:9]]}, def_union2={query0={query_dictionary={col1=[[@5,23:26='col1',<335>,2:17]]}, table_dictionary={problem2={col1=[[@5,23:26='col1',<335>,2:17]]}}, interface={col1=[{name=col1, table_ref=problem2}]}}, interface={col1=query_column}, query1={query_dictionary={xyz=[[@10,56:58='xyz',<335>,2:50]]}, table_dictionary={problem3={xyz=[[@10,56:58='xyz',<335>,2:50]]}}, interface={xyz=[{name=xyz, table_ref=problem3}]}}}, table_dictionary={aaa={col1=[[@37,188:190='aaa',<335>,4:41]]}, bbb={col2=[[@41,199:201='bbb',<335>,4:52]]}}, filters=[{name=col1, table_ref=aaa}, {name=col2, table_ref=bbb}], def_intersect5={interface={col1=query_column}, query4={query_dictionary={col2=[[@24,128:131='col2',<335>,3:51]]}, table_dictionary={problem={col2=[[@24,128:131='col2',<335>,3:51]]}}, interface={col2=[{name=col2, table_ref=problem}]}}, query3={query_dictionary={col1=[[@19,93:96='col1',<335>,3:16]]}, table_dictionary={answer={col1=[[@19,93:96='col1',<335>,3:16]]}}, interface={col1=[{name=col1, table_ref=answer}]}}}, interface={col2=[{name=col2, table_ref=null}], col1=[{name=col1, table_ref=null}]}, table_alias={aaa=union2, bbb=intersect5}}}",
				extractor.getSymbolTable().toString());

				Snippet snippet = extractor.getSnippet();
		assertDiagnosticCountBySeverity(snippet, "UNRESOLVED_UNQUALIFIED_COLUMNS", ParseDiagnostic.Severity.ERROR, null, null, 1);
		assertDiagnosticCountBySeverity(snippet, "AMBIGUOUS_COLUMN_REFERENCE", ParseDiagnostic.Severity.SEVERE_WARNING, null, null, 2);
	}


	@Test
	public void intersectAndUnionUnqualifiedReferencesCTEV8() {
		String sql = "WITH " 
			+ "\n aaa AS  (select col1 from  problem2 intersect select xyz from problem3), "
			+ "\n bbb AS (select col1 from  answer union select col2 from problem)"
			+ "\n Select  col1, col2 FROM aaa join bbb on aaa.col1 = bbb.col2";
		final SQLSelectParserParser parser = parse(sql);
		
		SqlParseEventWalker extractor = runParsertest(sql, parser);
		assertNoFatalErrors(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={with={1={cte={intersect={1={select={1={column={name=col1, table_ref=null}}}, from={table={alias=null, table=problem2}}}, 2={intersect={qualifier=null, operator=intersect}}, 3={select={1={column={name=xyz, table_ref=null}}}, from={table={alias=null, table=problem3}}}}}, alias=aaa}, 2={cte={union={1={select={1={column={name=col1, table_ref=null}}}, from={table={alias=null, table=answer}}}, 2={union={qualifier=null, operator=union}}, 3={select={1={column={name=col2, table_ref=null}}}, from={table={alias=null, table=problem}}}}}, alias=bbb}}, query={select={1={column={name=col1, table_ref=null}}, 2={column={name=col2, table_ref=null}}}, from={join={1={table={alias=null, table=aaa}}, 2={join=join, on={condition={left={column={name=col1, table_ref=aaa}}, right={column={name=col2, table_ref=bbb}}, operator==}}}, 3={table={alias=null, table=bbb}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[col2, col1]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{aaa={col1=[[@37,188:190='aaa',<335>,4:41]]}, problem={col2=[[@24,128:131='col2',<335>,3:47]]}, bbb={col2=[[@41,199:201='bbb',<335>,4:52]]}, answer={col1=[[@19,97:100='col1',<335>,3:16]]}, problem2={col1=[[@5,23:26='col1',<335>,2:17]]}, problem3={xyz=[[@10,60:62='xyz',<335>,2:54]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query4={col2=[[@24,128:131='col2',<335>,3:47]]}, query6={col2=[[@31,162:165='col2',<335>,4:15]], col1=[[@29,156:159='col1',<335>,4:9]]}, query0={col1=[[@5,23:26='col1',<335>,2:17]]}, query1={xyz=[[@10,60:62='xyz',<335>,2:54]]}, query3={col1=[[@19,97:100='col1',<335>,3:16]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query6={query_dictionary={col2=[[@31,162:165='col2',<335>,4:15]], col1=[[@29,156:159='col1',<335>,4:9]]}, table_dictionary={aaa={col1=[[@37,188:190='aaa',<335>,4:41]]}, bbb={col2=[[@41,199:201='bbb',<335>,4:52]]}}, def_intersect2={query0={query_dictionary={col1=[[@5,23:26='col1',<335>,2:17]]}, table_dictionary={problem2={col1=[[@5,23:26='col1',<335>,2:17]]}}, interface={col1=[{name=col1, table_ref=problem2}]}}, interface={col1=query_column}, query1={query_dictionary={xyz=[[@10,60:62='xyz',<335>,2:54]]}, table_dictionary={problem3={xyz=[[@10,60:62='xyz',<335>,2:54]]}}, interface={xyz=[{name=xyz, table_ref=problem3}]}}}, filters=[{name=col1, table_ref=aaa}, {name=col2, table_ref=bbb}], interface={col2=[{name=col2, table_ref=null}], col1=[{name=col1, table_ref=null}]}, table_alias={aaa=intersect2, bbb=union5}, def_union5={query4={query_dictionary={col2=[[@24,128:131='col2',<335>,3:47]]}, table_dictionary={problem={col2=[[@24,128:131='col2',<335>,3:47]]}}, interface={col2=[{name=col2, table_ref=problem}]}}, interface={col1=query_column}, query3={query_dictionary={col1=[[@19,97:100='col1',<335>,3:16]]}, table_dictionary={answer={col1=[[@19,97:100='col1',<335>,3:16]]}}, interface={col1=[{name=col1, table_ref=answer}]}}}}}",
				extractor.getSymbolTable().toString());

				Snippet snippet = extractor.getSnippet();
		assertDiagnosticCountBySeverity(snippet, "UNRESOLVED_UNQUALIFIED_COLUMNS", ParseDiagnostic.Severity.ERROR, null, null, 1);
		assertDiagnosticCountBySeverity(snippet, "AMBIGUOUS_COLUMN_REFERENCE", ParseDiagnostic.Severity.SEVERE_WARNING, null, null, 2);
	}


	@Test
	public void unionAndValuesUnqualifiedReferencesCTEV9() {
		String sql = "WITH " 
			+ "\n aaa AS  (select col1 from  answer union select col2 from problem), "
			+ "\n bbb AS ((values (2, 1, 'bbb'), (3, 94, 'bbb')) as ValueSource2 (col1, col2, col3))"
			+ "\n Select  col1, col2 FROM aaa join bbb on aaa.col1 = bbb.col2";
		final SQLSelectParserParser parser = parse(sql);
		
		SqlParseEventWalker extractor = runParsertest(sql, parser);
		assertNoFatalErrors(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={with={1={cte={union={1={select={1={column={name=col1, table_ref=null}}}, from={table={alias=null, table=answer}}}, 2={union={qualifier=null, operator=union}}, 3={select={1={column={name=col2, table_ref=null}}}, from={table={alias=null, table=problem}}}}}, alias=aaa}, 2={cte={values={columns={1={column={name=col1, table_ref=null}}, 2={column={name=col2, table_ref=null}}, 3={column={name=col3, table_ref=null}}}, alias=ValueSource2, matrix={1={row={1={literal=2}, 2={literal=1}, 3={literal='bbb'}}}, 2={row={1={literal=3}, 2={literal=94}, 3={literal='bbb'}}}}}}, alias=bbb}}, query={select={1={column={name=col1, table_ref=null}}, 2={column={name=col2, table_ref=null}}}, from={join={1={table={alias=null, table=aaa}}, 2={join=join, on={condition={left={column={name=col1, table_ref=aaa}}, right={column={name=col2, table_ref=bbb}}, operator==}}}, 3={table={alias=null, table=bbb}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[col2, col1]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{aaa={col1=[[@55,200:202='aaa',<335>,4:41]]}, problem={col2=[[@10,54:57='col2',<335>,2:48]]}, bbb={col2=[[@59,211:213='bbb',<335>,4:52]]}, answer={col1=[[@5,23:26='col1',<335>,2:17]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query4={col2=[[@49,174:177='col2',<335>,4:15]], col1=[[@47,168:171='col1',<335>,4:9]]}, query0={col1=[[@5,23:26='col1',<335>,2:17]]}, query1={col2=[[@10,54:57='col2',<335>,2:48]]}, values3={col2=[[@41,146:149='col2',<335>,3:71]], col3=[[@43,152:155='col3',<335>,3:77]], col1=[[@39,140:143='col1',<335>,3:65]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query4={query_dictionary={col2=[[@49,174:177='col2',<335>,4:15]], col1=[[@47,168:171='col1',<335>,4:9]]}, def_union2={query0={query_dictionary={col1=[[@5,23:26='col1',<335>,2:17]]}, table_dictionary={answer={col1=[[@5,23:26='col1',<335>,2:17]]}}, interface={col1=[{name=col1, table_ref=answer}]}}, interface={col1=query_column}, query1={query_dictionary={col2=[[@10,54:57='col2',<335>,2:48]]}, table_dictionary={problem={col2=[[@10,54:57='col2',<335>,2:48]]}}, interface={col2=[{name=col2, table_ref=problem}]}}}, table_dictionary={aaa={col1=[[@55,200:202='aaa',<335>,4:41]]}, bbb={col2=[[@59,211:213='bbb',<335>,4:52]]}}, def_values3={query_dictionary={col2=[[@41,146:149='col2',<335>,3:71]], col3=[[@43,152:155='col3',<335>,3:77]], col1=[[@39,140:143='col1',<335>,3:65]]}, table_dictionary={}, interface={col2=[], col3=[], col1=[]}}, filters=[{name=col1, table_ref=aaa}, {name=col2, table_ref=bbb}], interface={col2=[{name=col2, table_ref=null}], col1=[{name=col1, table_ref=null}]}, table_alias={aaa=union2, bbb=values3}}}",
				extractor.getSymbolTable().toString());

				Snippet snippet = extractor.getSnippet();
		assertDiagnosticCountBySeverity(snippet, "UNRESOLVED_UNQUALIFIED_COLUMNS", ParseDiagnostic.Severity.ERROR, null, null, 1);
		assertDiagnosticCountBySeverity(snippet, "AMBIGUOUS_COLUMN_REFERENCE", ParseDiagnostic.Severity.SEVERE_WARNING, null, null, 2);
	}


	@Test
	public void valuesAndIntersectUnqualifiedReferencesCTEV10() {
		String sql = "WITH " 
			+ "\n aaa AS  ((values (2, 1, 'aaa'), (3, 94, 'aaa')) as ValueSource2 (col1, col2, col3)), "
			+ "\n bbb AS (select col2 from  problem2 intersect select xyz from problem3)"
			+ "\n Select  col1, col2 FROM aaa join bbb on aaa.col1 = bbb.col2";
		final SQLSelectParserParser parser = parse(sql);
		
		SqlParseEventWalker extractor = runParsertest(sql, parser);
		assertNoFatalErrors(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={with={1={cte={values={columns={1={column={name=col1, table_ref=null}}, 2={column={name=col2, table_ref=null}}, 3={column={name=col3, table_ref=null}}}, alias=ValueSource2, matrix={1={row={1={literal=2}, 2={literal=1}, 3={literal='aaa'}}}, 2={row={1={literal=3}, 2={literal=94}, 3={literal='aaa'}}}}}}, alias=aaa}, 2={cte={intersect={1={select={1={column={name=col2, table_ref=null}}}, from={table={alias=null, table=problem2}}}, 2={intersect={qualifier=null, operator=intersect}}, 3={select={1={column={name=xyz, table_ref=null}}}, from={table={alias=null, table=problem3}}}}}, alias=bbb}}, query={select={1={column={name=col1, table_ref=null}}, 2={column={name=col2, table_ref=null}}}, from={join={1={table={alias=null, table=aaa}}, 2={join=join, on={condition={left={column={name=col1, table_ref=aaa}}, right={column={name=col2, table_ref=bbb}}, operator==}}}, 3={table={alias=null, table=bbb}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[col2, col1]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{aaa={col1=[[@55,206:208='aaa',<335>,4:41]]}, bbb={col2=[[@59,217:219='bbb',<335>,4:52]]}, problem2={col2=[[@37,109:112='col2',<335>,3:16]]}, problem3={xyz=[[@42,146:148='xyz',<335>,3:53]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{values0={col2=[[@27,78:81='col2',<335>,2:72]], col3=[[@29,84:87='col3',<335>,2:78]], col1=[[@25,72:75='col1',<335>,2:66]]}, query4={col2=[[@49,180:183='col2',<335>,4:15]], col1=[[@47,174:177='col1',<335>,4:9]]}, query1={col2=[[@37,109:112='col2',<335>,3:16]]}, query2={xyz=[[@42,146:148='xyz',<335>,3:53]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query4={query_dictionary={col2=[[@49,180:183='col2',<335>,4:15]], col1=[[@47,174:177='col1',<335>,4:9]]}, table_dictionary={aaa={col1=[[@55,206:208='aaa',<335>,4:41]]}, bbb={col2=[[@59,217:219='bbb',<335>,4:52]]}}, def_values0={query_dictionary={col2=[[@27,78:81='col2',<335>,2:72]], col3=[[@29,84:87='col3',<335>,2:78]], col1=[[@25,72:75='col1',<335>,2:66]]}, table_dictionary={}, interface={col2=[], col3=[], col1=[]}}, def_intersect3={interface={col2=query_column}, query1={query_dictionary={col2=[[@37,109:112='col2',<335>,3:16]]}, table_dictionary={problem2={col2=[[@37,109:112='col2',<335>,3:16]]}}, interface={col2=[{name=col2, table_ref=problem2}]}}, query2={query_dictionary={xyz=[[@42,146:148='xyz',<335>,3:53]]}, table_dictionary={problem3={xyz=[[@42,146:148='xyz',<335>,3:53]]}}, interface={xyz=[{name=xyz, table_ref=problem3}]}}}, filters=[{name=col1, table_ref=aaa}, {name=col2, table_ref=bbb}], interface={col2=[{name=col2, table_ref=null}], col1=[{name=col1, table_ref=null}]}, table_alias={aaa=values0, bbb=intersect3}}}",
				extractor.getSymbolTable().toString());

				Snippet snippet = extractor.getSnippet();
		assertDiagnosticCountBySeverity(snippet, "UNRESOLVED_UNQUALIFIED_COLUMNS", ParseDiagnostic.Severity.ERROR, null, null, 1);
		assertDiagnosticCountBySeverity(snippet, "AMBIGUOUS_COLUMN_REFERENCE", ParseDiagnostic.Severity.SEVERE_WARNING, null, null, 2);
	}


	@Test
	public void valuesAndValuesUnqualifiedReferencesCTEV11() {
		String sql = "WITH " 
			+ "\n aaa AS  ((values (1, 2, 'aaa'), (92, 3, 'aaa')) as ValueSource1 (col1, col2, col3)), "
			+ "\n bbb AS ((values (2, 1, 'bbb'), (3, 94, 'bbb')) as ValueSource2 (col1, col2, col3))"
			+ "\n Select  col1, col2 FROM aaa join bbb on aaa.col1 = bbb.col2";
		final SQLSelectParserParser parser = parse(sql);
		
		SqlParseEventWalker extractor = runParsertest(sql, parser);
		assertNoFatalErrors(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={with={1={cte={values={columns={1={column={name=col1, table_ref=null}}, 2={column={name=col2, table_ref=null}}, 3={column={name=col3, table_ref=null}}}, alias=ValueSource1, matrix={1={row={1={literal=1}, 2={literal=2}, 3={literal='aaa'}}}, 2={row={1={literal=92}, 2={literal=3}, 3={literal='aaa'}}}}}}, alias=aaa}, 2={cte={values={columns={1={column={name=col1, table_ref=null}}, 2={column={name=col2, table_ref=null}}, 3={column={name=col3, table_ref=null}}}, alias=ValueSource2, matrix={1={row={1={literal=2}, 2={literal=1}, 3={literal='bbb'}}}, 2={row={1={literal=3}, 2={literal=94}, 3={literal='bbb'}}}}}}, alias=bbb}}, query={select={1={column={name=col1, table_ref=null}}, 2={column={name=col2, table_ref=null}}}, from={join={1={table={alias=null, table=aaa}}, 2={join=join, on={condition={left={column={name=col1, table_ref=aaa}}, right={column={name=col2, table_ref=bbb}}, operator==}}}, 3={table={alias=null, table=bbb}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[col2, col1]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{aaa={col1=[[@73,218:220='aaa',<335>,4:41]]}, bbb={col2=[[@77,229:231='bbb',<335>,4:52]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{values0={col2=[[@27,78:81='col2',<335>,2:72]], col3=[[@29,84:87='col3',<335>,2:78]], col1=[[@25,72:75='col1',<335>,2:66]]}, values1={col2=[[@59,164:167='col2',<335>,3:71]], col3=[[@61,170:173='col3',<335>,3:77]], col1=[[@57,158:161='col1',<335>,3:65]]}, query2={col2=[[@67,192:195='col2',<335>,4:15]], col1=[[@65,186:189='col1',<335>,4:9]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query2={query_dictionary={col2=[[@67,192:195='col2',<335>,4:15]], col1=[[@65,186:189='col1',<335>,4:9]]}, table_dictionary={aaa={col1=[[@73,218:220='aaa',<335>,4:41]]}, bbb={col2=[[@77,229:231='bbb',<335>,4:52]]}}, def_values1={query_dictionary={col2=[[@59,164:167='col2',<335>,3:71]], col3=[[@61,170:173='col3',<335>,3:77]], col1=[[@57,158:161='col1',<335>,3:65]]}, table_dictionary={}, interface={col2=[], col3=[], col1=[]}}, def_values0={query_dictionary={col2=[[@27,78:81='col2',<335>,2:72]], col3=[[@29,84:87='col3',<335>,2:78]], col1=[[@25,72:75='col1',<335>,2:66]]}, table_dictionary={}, interface={col2=[], col3=[], col1=[]}}, filters=[{name=col1, table_ref=aaa}, {name=col2, table_ref=bbb}], interface={col2=[{name=col2, table_ref=null}], col1=[{name=col1, table_ref=null}]}, table_alias={aaa=values0, bbb=values1}}}",
				extractor.getSymbolTable().toString());

				Snippet snippet = extractor.getSnippet();
		assertDiagnosticCountBySeverity(snippet, "UNRESOLVED_UNQUALIFIED_COLUMNS", ParseDiagnostic.Severity.ERROR, null, null, 1);
		assertDiagnosticCountBySeverity(snippet, "AMBIGUOUS_COLUMN_REFERENCE", ParseDiagnostic.Severity.SEVERE_WARNING, null, null, 2);
	}


	@Test
	public void queryAndSubstitutionUnqualifiedReferencesCTEV12() {
		String sql = "WITH " 
			+ "\n aaa AS  (select col1 from tab1), "
			+ "\n bbb AS <substitution_2>"
			+ "\n Select  col1, col2 FROM aaa join bbb on aaa.col1 = bbb.col2";
		final SQLSelectParserParser parser = parse(sql);
		
		SqlParseEventWalker extractor = runParsertest(sql, parser);
		assertNoFatalErrors(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={with={1={cte={select={1={column={name=col1, table_ref=null}}}, from={table={alias=null, table=tab1}}}, alias=aaa}, 2={cte={substitution={name=<substitution_2>, type=tuple}}, alias=bbb}}, query={select={1={column={name=col1, table_ref=null}}, 2={column={name=col2, table_ref=null}}}, from={join={1={table={alias=null, table=aaa}}, 2={join=join, on={condition={left={column={name=col1, table_ref=aaa}}, right={column={name=col2, table_ref=bbb}}, operator==}}}, 3={table={alias=null, table=bbb}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[col2, col1]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<substitution_2>=tuple}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{aaa={col1=[[@22,107:109='aaa',<335>,4:41]]}, bbb={col2=[[@26,118:120='bbb',<335>,4:52]]}, tab1={col1=[[@5,23:26='col1',<335>,2:17]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={col1=[[@5,23:26='col1',<335>,2:17]]}, query2={col2=[[@16,81:84='col2',<335>,4:15]], col1=[[@14,75:78='col1',<335>,4:9]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query2={query_dictionary={col2=[[@16,81:84='col2',<335>,4:15]], col1=[[@14,75:78='col1',<335>,4:9]]}, table_dictionary={aaa={col1=[[@22,107:109='aaa',<335>,4:41]]}, bbb={col2=[[@26,118:120='bbb',<335>,4:52]]}}, def_query0={query_dictionary={col1=[[@5,23:26='col1',<335>,2:17]]}, table_dictionary={tab1={col1=[[@5,23:26='col1',<335>,2:17]]}}, interface={col1=[{name=col1, table_ref=tab1}]}}, filters=[{name=col1, table_ref=aaa}, {name=col2, table_ref=bbb}], interface={col2=[{name=col2, table_ref=null}], col1=[{name=col1, table_ref=null}]}, table_alias={aaa=query0, bbb=query1}}}",
				extractor.getSymbolTable().toString());

		Snippet snippet = extractor.getSnippet();
		assertDiagnosticCountBySeverity(snippet, "UNRESOLVED_UNQUALIFIED_COLUMNS", ParseDiagnostic.Severity.ERROR, null, null, 1);
		assertDiagnosticCountBySeverity(snippet, "AMBIGUOUS_COLUMN_REFERENCE", ParseDiagnostic.Severity.SEVERE_WARNING, null, null, 2);
	}


	@Test
	public void substitutionAndQueryUnqualifiedReferencesCTEV13() {
		String sql = "WITH " 
			+ "\n aaa AS <substitution_1>, "
			+ "\n bbb AS (select col2 from tab2)"
			+ "\n Select  col1, col2 FROM aaa join bbb on aaa.col1 = bbb.col2";
		final SQLSelectParserParser parser = parse(sql);		
		SqlParseEventWalker extractor = runParsertest(sql, parser);

		Assert.assertEquals("AST is wrong", "{SQL={with={1={cte={substitution={name=<substitution_1>, type=tuple}}, alias=aaa}, 2={cte={select={1={column={name=col2, table_ref=null}}}, from={table={alias=null, table=tab2}}}, alias=bbb}}, query={select={1={column={name=col1, table_ref=null}}, 2={column={name=col2, table_ref=null}}}, from={join={1={table={alias=null, table=aaa}}, 2={join=join, on={condition={left={column={name=col1, table_ref=aaa}}, right={column={name=col2, table_ref=bbb}}, operator==}}}, 3={table={alias=null, table=bbb}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[col2, col1]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<substitution_1>=tuple}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{aaa={col1=[[@22,106:108='aaa',<335>,4:41]]}, bbb={col2=[[@26,117:119='bbb',<335>,4:52]]}, tab2={col2=[[@9,49:52='col2',<335>,3:16]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query1={col2=[[@9,49:52='col2',<335>,3:16]]}, query2={col2=[[@16,80:83='col2',<335>,4:15]], col1=[[@14,74:77='col1',<335>,4:9]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query2={query_dictionary={col2=[[@16,80:83='col2',<335>,4:15]], col1=[[@14,74:77='col1',<335>,4:9]]}, table_dictionary={aaa={col1=[[@22,106:108='aaa',<335>,4:41]]}, bbb={col2=[[@26,117:119='bbb',<335>,4:52]]}}, def_query1={query_dictionary={col2=[[@9,49:52='col2',<335>,3:16]]}, table_dictionary={tab2={col2=[[@9,49:52='col2',<335>,3:16]]}}, interface={col2=[{name=col2, table_ref=tab2}]}}, filters=[{name=col1, table_ref=aaa}, {name=col2, table_ref=bbb}], interface={col2=[{name=col2, table_ref=null}], col1=[{name=col1, table_ref=null}]}, table_alias={aaa=query0, bbb=query1}}}",
				extractor.getSymbolTable().toString());

		assertNoFatalErrors(extractor);
		Snippet snippet = extractor.getSnippet();
		assertDiagnosticCountBySeverity(snippet, "UNRESOLVED_UNQUALIFIED_COLUMNS", ParseDiagnostic.Severity.ERROR, null, null, 1);
		assertDiagnosticCountBySeverity(snippet, "AMBIGUOUS_COLUMN_REFERENCE", ParseDiagnostic.Severity.SEVERE_WARNING, null, null, 2);
	}


	@Test
	public void substitutionAndSubstitutionUnqualifiedReferencesCTEV14() {
		String sql = "WITH " 
			+ "\n aaa AS  <substitution_1>, "
			+ "\n bbb AS <substitution_2>"
			+ "\n Select  col1, col2 FROM aaa join bbb on aaa.col1 = bbb.col2";
		final SQLSelectParserParser parser = parse(sql);	
		SqlParseEventWalker extractor = runParsertest(sql, parser);
		assertNoFatalErrors(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={with={1={cte={substitution={name=<substitution_1>, type=tuple}}, alias=aaa}, 2={cte={substitution={name=<substitution_2>, type=tuple}}, alias=bbb}}, query={select={1={column={name=col1, table_ref=null}}, 2={column={name=col2, table_ref=null}}}, from={join={1={table={alias=null, table=aaa}}, 2={join=join, on={condition={left={column={name=col1, table_ref=aaa}}, right={column={name=col2, table_ref=bbb}}, operator==}}}, 3={table={alias=null, table=bbb}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[col2, col1]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<substitution_1>=tuple, <substitution_2>=tuple}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{aaa={col1=[[@17,100:102='aaa',<335>,4:41]]}, bbb={col2=[[@21,111:113='bbb',<335>,4:52]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query2={col2=[[@11,74:77='col2',<335>,4:15]], col1=[[@9,68:71='col1',<335>,4:9]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query2={query_dictionary={col2=[[@11,74:77='col2',<335>,4:15]], col1=[[@9,68:71='col1',<335>,4:9]]}, table_dictionary={aaa={col1=[[@17,100:102='aaa',<335>,4:41]]}, bbb={col2=[[@21,111:113='bbb',<335>,4:52]]}}, filters=[{name=col1, table_ref=aaa}, {name=col2, table_ref=bbb}], interface={col2=[{name=col2, table_ref=null}], col1=[{name=col1, table_ref=null}]}, table_alias={aaa=query0, bbb=query1}}}",
				extractor.getSymbolTable().toString());

		Snippet snippet = extractor.getSnippet();
		assertDiagnosticCountBySeverity(snippet, "UNRESOLVED_UNQUALIFIED_COLUMNS", ParseDiagnostic.Severity.ERROR, null, null, 1);
		assertDiagnosticCountBySeverity(snippet, "AMBIGUOUS_COLUMN_REFERENCE", ParseDiagnostic.Severity.SEVERE_WARNING, null, null, 2);
	}


	@Test
	public void correlatedCTEStatementsUnqualifiedReferencesCTEV14() {
		String sql = "WITH " 
			+ "\n aaa AS  (select col1 from tab1), "
			+ "\n bbb AS (select col2 from tab2 where col1 = aaa.col1)"
			+ "\n Select  col1, col2 FROM aaa join bbb on aaa.col1 = bbb.col2";
		final SQLSelectParserParser parser = parse(sql);
		
		SqlParseEventWalker extractor = runParsertest(sql, parser);
		assertNoFatalErrors(extractor);
		Snippet snippet = extractor.getSnippet();
		assertDiagnosticCountBySeverity(snippet, "UNRESOLVED_UNQUALIFIED_COLUMNS", ParseDiagnostic.Severity.ERROR, null, null, 1);
		assertDiagnosticCountBySeverity(snippet, "AMBIGUOUS_COLUMN_REFERENCE", ParseDiagnostic.Severity.SEVERE_WARNING, null, null, 2);
	}


	@Test
	public void sameTableDifferentSchemaUnqualifiedReferencesCTEV15() {
		String sql = "WITH " 
			+ "\n aaa AS  (select col1 from sc1.tab1), "
			+ "\n bbb AS (select col2 from sc2.tab1 where col1 = aaa.col1)"
			+ "\n Select  col1, col2 FROM aaa join bbb on aaa.col1 = bbb.col2";
		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runParsertest(sql, parser);

		Assert.assertEquals("AST is wrong", "{SQL={with={1={cte={select={1={column={name=col1, table_ref=null}}}, from={table={schema=sc1, alias=null, table=tab1}}}, alias=aaa}, 2={cte={select={1={column={name=col2, table_ref=null}}}, from={table={schema=sc2, alias=null, table=tab1}}, where={condition={left={column={name=col1, table_ref=null}}, right={column={name=col1, table_ref=aaa}}, operator==}}}, alias=bbb}}, query={select={1={column={name=col1, table_ref=null}}, 2={column={name=col2, table_ref=null}}}, from={join={1={table={alias=null, table=aaa}}, 2={join=join, on={condition={left={column={name=col1, table_ref=aaa}}, right={column={name=col2, table_ref=bbb}}, operator==}}}, 3={table={alias=null, table=bbb}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[col2, col1]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{aaa={col1=[[@37,144:146='aaa',<335>,4:41]]}, sc1.tab1={col1=[[@5,23:26='col1',<335>,2:17]]}, bbb={col2=[[@41,155:157='bbb',<335>,4:52]]}, sc2.tab1={col2=[[@16,61:64='col2',<335>,3:16]], col1=[[@22,86:89='col1',<335>,3:41]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={col1=[[@5,23:26='col1',<335>,2:17]]}, query1={col2=[[@16,61:64='col2',<335>,3:16]]}, query2={col2=[[@31,118:121='col2',<335>,4:15]], col1=[[@29,112:115='col1',<335>,4:9]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query2={query_dictionary={col2=[[@31,118:121='col2',<335>,4:15]], col1=[[@29,112:115='col1',<335>,4:9]]}, table_dictionary={aaa={col1=[[@37,144:146='aaa',<335>,4:41]]}, bbb={col2=[[@41,155:157='bbb',<335>,4:52]]}}, unresolved_column={aaa.col1={column={name=col1, table_ref=aaa}, locations=[[@24,93:95='aaa',<335>,3:48], [@37,144:146='aaa',<335>,4:41]]}}, def_query1={query_dictionary={col2=[[@16,61:64='col2',<335>,3:16]]}, table_dictionary={sc2.tab1={col2=[[@16,61:64='col2',<335>,3:16]], col1=[[@22,86:89='col1',<335>,3:41]]}}, filters=[{name=col1, table_ref=sc2.tab1}, {name=col1, table_ref=aaa}], interface={col2=[{name=col2, table_ref=sc2.tab1}]}}, def_query0={query_dictionary={col1=[[@5,23:26='col1',<335>,2:17]]}, table_dictionary={sc1.tab1={col1=[[@5,23:26='col1',<335>,2:17]]}}, interface={col1=[{name=col1, table_ref=sc1.tab1}]}}, filters=[{name=col1, table_ref=aaa}, {name=col2, table_ref=bbb}], interface={col2=[{name=col2, table_ref=null}], col1=[{name=col1, table_ref=null}]}, table_alias={aaa=query0, bbb=query1}}}",
				extractor.getSymbolTable().toString());

		assertNoFatalErrors(extractor);
		Snippet snippet = extractor.getSnippet();
		assertDiagnosticCountBySeverity(snippet, "UNRESOLVED_UNQUALIFIED_COLUMNS", ParseDiagnostic.Severity.ERROR, null, null, 1);
		assertDiagnosticCountBySeverity(snippet, "AMBIGUOUS_COLUMN_REFERENCE", ParseDiagnostic.Severity.SEVERE_WARNING, null, null, 2);
	}


	@Test
	public void normalMultiTableTest() {
			
		final String query = " select source.col1, target.col2 from (select col1 from tab1) as source join (select col2 from tab2) as target";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
				
		Assert.assertEquals("Interface is wrong", "[col2, col1]", 
					extractor.getInterface().toString());
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=col1, table_ref=source}}, 2={column={name=col2, table_ref=target}}}, from={join={1={table={alias=source, query={select={1={column={name=col1, table_ref=null}}}, from={table={alias=null, table=tab1}}}}}, 2={join=join}, 3={table={alias=target, query={select={1={column={name=col2, table_ref=null}}}, from={table={alias=null, table=tab2}}}}}}}}}",
					extractor.getAsTree().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
					extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={col1=[[@11,46:49='col1',<335>,1:46]]}, tab2={col2=[[@20,85:88='col2',<335>,1:85]]}}",
					extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={col1=[[@11,46:49='col1',<335>,1:46], [@1,8:13='source',<335>,1:8]]}, query1={col2=[[@20,85:88='col2',<335>,1:85], [@5,21:26='target',<335>,1:21]]}, query2={col2=[[@7,28:31='col2',<335>,1:28]], col1=[[@3,15:18='col1',<335>,1:15]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query2={query_dictionary={col2=[[@7,28:31='col2',<335>,1:28]], col1=[[@3,15:18='col1',<335>,1:15]]}, table_dictionary={}, def_query1={query_dictionary={col2=[[@20,85:88='col2',<335>,1:85], [@5,21:26='target',<335>,1:21]]}, table_dictionary={tab2={col2=[[@20,85:88='col2',<335>,1:85]]}}, interface={col2=[{name=col2, table_ref=tab2}]}}, def_query0={query_dictionary={col1=[[@11,46:49='col1',<335>,1:46], [@1,8:13='source',<335>,1:8]]}, table_dictionary={tab1={col1=[[@11,46:49='col1',<335>,1:46]]}}, interface={col1=[{name=col1, table_ref=tab1}]}}, interface={col2=[{name=col2, table_ref=target}], col1=[{name=col1, table_ref=source}]}, table_alias={source=query0, target=query1}}}",
					extractor.getSymbolTable().toString());
	}


	@Test
	public void multipleValuesStatementWithAsClauseInSelectTest() {
			
		final String query = " select * from (values (1, 2, 'aaa')) as source join (values (92, 3, 'aaa')) as target ";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
			
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=*, table_ref=*}}}, from={join={1={values={alias=source, matrix={1={row={1={literal=1}, 2={literal=2}, 3={literal='aaa'}}}}}}, 2={join=join}, 3={values={alias=target, matrix={1={row={1={literal=92}, 2={literal=3}, 3={literal='aaa'}}}}}}}}}}",
					extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]", 
					extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
					extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{}",
					extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{values0={*=[[@1,8:8='*',<289>,1:8]], $1=[[@5,23:23='(',<285>,1:23], [@18,61:61='(',<285>,1:61]], $2=[[@5,23:23='(',<285>,1:23], [@18,61:61='(',<285>,1:61]], $3=[[@5,23:23='(',<285>,1:23], [@18,61:61='(',<285>,1:61]]}, values1={*=[[@1,8:8='*',<289>,1:8]], $1=[[@18,61:61='(',<285>,1:61]], $2=[[@18,61:61='(',<285>,1:61]], $3=[[@18,61:61='(',<285>,1:61]]}, query2={*=[[@1,8:8='*',<289>,1:8]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query2={query_dictionary={*=[[@1,8:8='*',<289>,1:8]]}, def_values1={query_dictionary={*=[[@1,8:8='*',<289>,1:8]], $1=[[@18,61:61='(',<285>,1:61]], $2=[[@18,61:61='(',<285>,1:61]], $3=[[@18,61:61='(',<285>,1:61]]}, table_dictionary={}, interface={$1=[], $2=[], $3=[]}}, table_dictionary={}, def_values0={query_dictionary={*=[[@1,8:8='*',<289>,1:8]], $1=[[@5,23:23='(',<285>,1:23], [@18,61:61='(',<285>,1:61]], $2=[[@5,23:23='(',<285>,1:23], [@18,61:61='(',<285>,1:61]], $3=[[@5,23:23='(',<285>,1:23], [@18,61:61='(',<285>,1:61]]}, table_dictionary={}, interface={$1=[], $2=[], $3=[]}}, interface={*=[{name=*, table_ref=*}]}, table_alias={source=values0, target=values1}}}",
					extractor.getSymbolTable().toString());
	}


	@Test
	public void multipleValuesStatementWithColumnAliasesClauseInSelectTest() {
		final String query = " select source.col1, target.col2 from (values (1, 2, 'aaa')) as source (col1, col2, col3) join (values (92, 3, 'aaa')) as target (c1, c3, c2)";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		Snippet snippet = extractor.getSnippet();
		assertFatalDiagnosticAtPosition(snippet,
				"QUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIAS",
				"Qualified column 'col2' at (l:1 c:21) was not found in output interface of query alias 'target'.",
				"target.col2",
				1,
				21);
		assertFatalDiagnosticCount(snippet,
				"QUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIAS",
				"Qualified column 'col2' at (l:1 c:21) was not found in output interface of query alias 'target'.",
				"target.col2",
				1);
			
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=col1, table_ref=source}}, 2={column={name=col2, table_ref=target}}}, from={join={1={values={columns={1={column={name=col1, table_ref=null}}, 2={column={name=col2, table_ref=null}}, 3={column={name=col3, table_ref=null}}}, alias=source, matrix={1={row={1={literal=1}, 2={literal=2}, 3={literal='aaa'}}}}}}, 2={join=join}, 3={values={columns={1={column={name=c1, table_ref=null}}, 2={column={name=c3, table_ref=null}}, 3={column={name=c2, table_ref=null}}}, alias=target, matrix={1={row={1={literal=92}, 2={literal=3}, 3={literal='aaa'}}}}}}}}}}",
					extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[col2, col1]", 
					extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
					extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{}",
					extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{values0={col2=[[@24,78:81='col2',<335>,1:78]], col3=[[@26,84:87='col3',<335>,1:84]], col1=[[@22,72:75='col1',<335>,1:72], [@1,8:13='source',<335>,1:8]]}, values1={c3=[[@44,134:135='c3',<335>,1:134]], c1=[[@42,130:131='c1',<335>,1:130]], c2=[[@46,138:139='c2',<335>,1:138]]}, query2={col2=[[@7,28:31='col2',<335>,1:28]], col1=[[@3,15:18='col1',<335>,1:15]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query2={query_dictionary={col2=[[@7,28:31='col2',<335>,1:28]], col1=[[@3,15:18='col1',<335>,1:15]]}, def_values1={query_dictionary={c3=[[@44,134:135='c3',<335>,1:134]], c1=[[@42,130:131='c1',<335>,1:130]], c2=[[@46,138:139='c2',<335>,1:138]]}, table_dictionary={}, interface={c3=[], c1=[], c2=[]}}, table_dictionary={}, def_values0={query_dictionary={col2=[[@24,78:81='col2',<335>,1:78]], col3=[[@26,84:87='col3',<335>,1:84]], col1=[[@22,72:75='col1',<335>,1:72], [@1,8:13='source',<335>,1:8]]}, table_dictionary={}, interface={col2=[], col3=[], col1=[]}}, interface={col2=[{name=col2, table_ref=target}], col1=[{name=col1, table_ref=source}]}, table_alias={source=values0, target=values1}}}",
					extractor.getSymbolTable().toString());
	}


	@Test
	public void valuesStatementAloneSelectTest() {
			
		final String query = " select * from (values (1, 2, 'aaa'), (92, 3, 'aaa'))";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
			
		Assert.assertEquals("Interface is wrong", "[*]", 
					extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
					extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{}",
					extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{values0={*=[[@1,8:8='*',<289>,1:8]], $1=[[@5,23:23='(',<285>,1:23], [@13,38:38='(',<285>,1:38]], $2=[[@5,23:23='(',<285>,1:23], [@13,38:38='(',<285>,1:38]], $3=[[@5,23:23='(',<285>,1:23], [@13,38:38='(',<285>,1:38]]}, query1={*=[[@1,8:8='*',<289>,1:8]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query1={query_dictionary={*=[[@1,8:8='*',<289>,1:8]]}, table_dictionary={}, def_values0={query_dictionary={*=[[@1,8:8='*',<289>,1:8]], $1=[[@5,23:23='(',<285>,1:23], [@13,38:38='(',<285>,1:38]], $2=[[@5,23:23='(',<285>,1:23], [@13,38:38='(',<285>,1:38]], $3=[[@5,23:23='(',<285>,1:23], [@13,38:38='(',<285>,1:38]]}, table_dictionary={}, interface={$1=[], $2=[], $3=[]}}, interface={*=[{name=*, table_ref=*}]}}}",
					extractor.getSymbolTable().toString());
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=*, table_ref=*}}}, from={values={matrix={1={row={1={literal=1}, 2={literal=2}, 3={literal='aaa'}}}, 2={row={1={literal=92}, 2={literal=3}, 3={literal='aaa'}}}}}}}}",
					extractor.getAsTree().toString());
	}


	@Test
	public void valuesStatementWithAsClauseInSelectTest() {
			
		final String query = " select * from (values (1, 2, 'aaa'), (92, 3, 'aaa')) as source";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
			
		Assert.assertEquals("Interface is wrong", "[*]", 
					extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
					extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{}",
					extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{values0={*=[[@1,8:8='*',<289>,1:8]], $1=[[@5,23:23='(',<285>,1:23], [@13,38:38='(',<285>,1:38]], $2=[[@5,23:23='(',<285>,1:23], [@13,38:38='(',<285>,1:38]], $3=[[@5,23:23='(',<285>,1:23], [@13,38:38='(',<285>,1:38]]}, query1={*=[[@1,8:8='*',<289>,1:8]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query1={query_dictionary={*=[[@1,8:8='*',<289>,1:8]]}, table_dictionary={}, def_values0={query_dictionary={*=[[@1,8:8='*',<289>,1:8]], $1=[[@5,23:23='(',<285>,1:23], [@13,38:38='(',<285>,1:38]], $2=[[@5,23:23='(',<285>,1:23], [@13,38:38='(',<285>,1:38]], $3=[[@5,23:23='(',<285>,1:23], [@13,38:38='(',<285>,1:38]]}, table_dictionary={}, interface={$1=[], $2=[], $3=[]}}, interface={*=[{name=*, table_ref=*}]}, table_alias={source=values0}}}",
					extractor.getSymbolTable().toString());
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=*, table_ref=*}}}, from={values={alias=source, matrix={1={row={1={literal=1}, 2={literal=2}, 3={literal='aaa'}}}, 2={row={1={literal=92}, 2={literal=3}, 3={literal='aaa'}}}}}}}}",
					extractor.getAsTree().toString());
	}


	@Test
	public void valuesStatementWithAsClauseAndColumnsInSelectTest() {
			
		final String query1 = " select * from (values (1, 2, 'aaa'), (92, 3, 'aaa')) as src (col1, col2, col3)";
		final SQLSelectParserParser parser1 = parse(query1);
		SqlParseEventWalker extractor = runParsertest(query1, parser1);
		assertNoWalkerDiagnostics(extractor);

		// final String query2 = " select * from (select a as col1, b as col2, c as col3 from values) as src";
		// final SQLSelectParserParser parser2 = parse(query2);
		// SqlParseEventWalker extractor2 = runParsertest(query2, parser2);
		// assertNoWalkerDiagnostics(extractor2);
			
		Assert.assertEquals("Interface is wrong", "[*]", 
					extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
					extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{}",
					extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{values0={*=[[@1,8:8='*',<289>,1:8]], col2=[[@26,68:71='col2',<335>,1:68]], col3=[[@28,74:77='col3',<335>,1:74]], col1=[[@24,62:65='col1',<335>,1:62]]}, query1={*=[[@1,8:8='*',<289>,1:8]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query1={query_dictionary={*=[[@1,8:8='*',<289>,1:8]]}, table_dictionary={}, def_values0={query_dictionary={*=[[@1,8:8='*',<289>,1:8]], col2=[[@26,68:71='col2',<335>,1:68]], col3=[[@28,74:77='col3',<335>,1:74]], col1=[[@24,62:65='col1',<335>,1:62]]}, table_dictionary={}, interface={col2=[], col3=[], col1=[]}}, interface={*=[{name=*, table_ref=*}]}, table_alias={src=values0}}}",
					extractor.getSymbolTable().toString());
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=*, table_ref=*}}}, from={values={columns={1={column={name=col1, table_ref=null}}, 2={column={name=col2, table_ref=null}}, 3={column={name=col3, table_ref=null}}}, alias=src, matrix={1={row={1={literal=1}, 2={literal=2}, 3={literal='aaa'}}}, 2={row={1={literal=92}, 2={literal=3}, 3={literal='aaa'}}}}}}}}",
					extractor.getAsTree().toString());
	}

}
