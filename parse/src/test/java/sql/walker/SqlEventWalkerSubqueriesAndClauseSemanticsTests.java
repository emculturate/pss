package sql.walker;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import access.Snippet;
import errorhandling.ParseDiagnostic;
import sql.SQLSelectParserParser;

public class SqlEventWalkerSubqueriesAndClauseSemanticsTests extends AbstractSqlParseEventWalkerTest {

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
		Assert.assertEquals("Table Dictionary is wrong", "{third={x=[[@9,40:40='x',<381>,2:10]], y=[[@13,51:51='y',<381>,2:21]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={col2=[[@15,56:59='col2',<381>,2:26]], col1=[[@11,45:48='col1',<381>,2:15], [@1,8:9='F4',<381>,1:8]]}, query1={last=[[@5,19:22='last',<102>,1:19]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query1={query_dictionary={last=[[@5,19:22='last',<102>,1:19]]}, table_dictionary={}, def_query0={query_dictionary={col2=[[@15,56:59='col2',<381>,2:26]], col1=[[@11,45:48='col1',<381>,2:15], [@1,8:9='F4',<381>,1:8]]}, table_dictionary={third={x=[[@9,40:40='x',<381>,2:10]], y=[[@13,51:51='y',<381>,2:21]]}}, interface={col2=[{name=y, table_ref=third}], col1=[{name=x, table_ref=third}]}}, interface={last=[{name=col1, table_ref=F4}]}, table_alias={F4=query0}}}",
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
		Assert.assertEquals("Table Dictionary is wrong", "{third={*=[[@9,40:40='*',<291>,2:10]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@9,40:40='*',<291>,2:10]], col1=[[@1,8:9='F4',<381>,1:8]]}, query1={last=[[@5,19:22='last',<102>,1:19]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query1={query_dictionary={last=[[@5,19:22='last',<102>,1:19]]}, table_dictionary={}, def_query0={query_dictionary={*=[[@9,40:40='*',<291>,2:10]], col1=[[@1,8:9='F4',<381>,1:8]]}, table_dictionary={third={*=[[@9,40:40='*',<291>,2:10]]}}, interface={*=[{name=*, table_ref=*}]}}, interface={last=[{name=col1, table_ref=F4}]}, table_alias={F4=query0}}}",
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
		Assert.assertEquals("Table Dictionary is wrong", "{third={*=[[@11,49:49='*',<291>,3:10]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@11,49:49='*',<291>,3:10]]}, query1={*=[[@7,31:31='*',<291>,2:9]], col1=[[@1,8:9='F4',<381>,1:8]]}, query2={col1=[[@3,11:14='col1',<381>,1:11]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query2={query_dictionary={col1=[[@3,11:14='col1',<381>,1:11]]}, table_dictionary={}, def_query1={query_dictionary={*=[[@7,31:31='*',<291>,2:9]], col1=[[@1,8:9='F4',<381>,1:8]]}, table_dictionary={}, def_query0={query_dictionary={*=[[@11,49:49='*',<291>,3:10]]}, table_dictionary={third={*=[[@11,49:49='*',<291>,3:10]]}}, interface={*=[{name=*, table_ref=*}]}}, interface={*=[{name=*, table_ref=*}]}, table_alias={T3=query0}}, interface={col1=[{name=col1, table_ref=F4}]}, table_alias={F4=query1}}}",
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
		Assert.assertEquals("Table Dictionary is wrong", "{third={*=[[@9,46:46='*',<291>,3:10]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@9,46:46='*',<291>,3:10]]}, query1={*=[[@5,28:28='*',<291>,2:9]]}, query2={col1=[[@1,8:11='col1',<381>,1:8]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query2={query_dictionary={col1=[[@1,8:11='col1',<381>,1:8]]}, table_dictionary={}, def_query1={query_dictionary={*=[[@5,28:28='*',<291>,2:9]]}, table_dictionary={}, def_query0={query_dictionary={*=[[@9,46:46='*',<291>,3:10]]}, table_dictionary={third={*=[[@9,46:46='*',<291>,3:10]]}}, interface={*=[{name=*, table_ref=*}]}}, interface={*=[{name=*, table_ref=*}]}, table_alias={T3=query0}}, interface={col1=[{name=col1, table_ref=query1}]}, table_alias={F4=query1}}}",
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
		Assert.assertEquals("Table Dictionary is wrong", "{third={col2=[[@11,52:55='col2',<381>,3:16]], col1=[[@9,46:49='col1',<381>,3:10]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@5,28:28='*',<291>,2:9]], col2=[[@11,52:55='col2',<381>,3:16]], col1=[[@9,46:49='col1',<381>,3:10]]}, query1={*=[[@5,28:28='*',<291>,2:9]]}, query2={col1=[[@1,8:11='col1',<381>,1:8]]}}",
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
		Assert.assertEquals("Table Dictionary is wrong", "{third={col2=[[@13,55:58='col2',<381>,3:16]], col1=[[@11,49:52='col1',<381>,3:10]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@7,31:31='*',<291>,2:9]], col2=[[@13,55:58='col2',<381>,3:16]], col1=[[@11,49:52='col1',<381>,3:10]]}, query1={*=[[@7,31:31='*',<291>,2:9]], col1=[[@1,8:9='F4',<381>,1:8]]}, query2={col1=[[@3,11:14='col1',<381>,1:11]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query2={query_dictionary={col1=[[@3,11:14='col1',<381>,1:11]]}, table_dictionary={}, def_query1={query_dictionary={*=[[@7,31:31='*',<291>,2:9]], col1=[[@1,8:9='F4',<381>,1:8]]}, table_dictionary={}, def_query0={query_dictionary={*=[[@7,31:31='*',<291>,2:9]], col2=[[@13,55:58='col2',<381>,3:16]], col1=[[@11,49:52='col1',<381>,3:10]]}, table_dictionary={third={col2=[[@13,55:58='col2',<381>,3:16]], col1=[[@11,49:52='col1',<381>,3:10]]}}, interface={col2=[{name=col2, table_ref=third}], col1=[{name=col1, table_ref=third}]}}, interface={*=[{name=*, table_ref=*}]}, table_alias={T3=query0}}, interface={col1=[{name=col1, table_ref=F4}]}, table_alias={F4=query1}}}",
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
		Assert.assertEquals("Table Dictionary is wrong", "{third={*=[[@11,49:49='*',<291>,3:10]]}, T3={col1=[[@1,8:9='T3',<381>,1:8]]}, t3={col1=[[@1,8:9='T3',<381>,1:8]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@11,49:49='*',<291>,3:10]]}, query1={*=[[@7,31:31='*',<291>,2:9]]}, query2={col1=[[@3,11:14='col1',<381>,1:11]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query2={query_dictionary={col1=[[@3,11:14='col1',<381>,1:11]]}, table_dictionary={t3={col1=[[@1,8:9='T3',<381>,1:8]]}}, def_query1={query_dictionary={*=[[@7,31:31='*',<291>,2:9]]}, table_dictionary={}, def_query0={query_dictionary={*=[[@11,49:49='*',<291>,3:10]]}, table_dictionary={third={*=[[@11,49:49='*',<291>,3:10]]}}, interface={*=[{name=*, table_ref=*}]}}, interface={*=[{name=*, table_ref=*}]}, table_alias={T3=query0}}, interface={col1=[{name=col1, table_ref=T3}]}, table_alias={F4=query1}}}",
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
		Assert.assertEquals("Table Dictionary is wrong", "{third={col2=[[@13,55:58='col2',<381>,3:16]], col1=[[@11,49:52='col1',<381>,3:10]]}, T3={col1=[[@1,8:9='T3',<381>,1:8]]}, t3={col1=[[@1,8:9='T3',<381>,1:8]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@7,31:31='*',<291>,2:9]], col2=[[@13,55:58='col2',<381>,3:16]], col1=[[@11,49:52='col1',<381>,3:10]]}, query1={*=[[@7,31:31='*',<291>,2:9]]}, query2={col1=[[@3,11:14='col1',<381>,1:11]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query2={query_dictionary={col1=[[@3,11:14='col1',<381>,1:11]]}, table_dictionary={t3={col1=[[@1,8:9='T3',<381>,1:8]]}}, def_query1={query_dictionary={*=[[@7,31:31='*',<291>,2:9]]}, table_dictionary={}, def_query0={query_dictionary={*=[[@7,31:31='*',<291>,2:9]], col2=[[@13,55:58='col2',<381>,3:16]], col1=[[@11,49:52='col1',<381>,3:10]]}, table_dictionary={third={col2=[[@13,55:58='col2',<381>,3:16]], col1=[[@11,49:52='col1',<381>,3:10]]}}, interface={col2=[{name=col2, table_ref=third}], col1=[{name=col1, table_ref=third}]}}, interface={*=[{name=*, table_ref=*}]}, table_alias={T3=query0}}, interface={col1=[{name=col1, table_ref=T3}]}, table_alias={F4=query1}}}",
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
		Assert.assertEquals("Table Dictionary is wrong", "{third={*=[[@11,49:49='*',<291>,3:10]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@11,49:49='*',<291>,3:10]], col1=[[@5,25:26='T3',<381>,2:9]]}, query1={*=[[@1,8:8='*',<291>,1:8]], col1=[[@7,28:31='col1',<381>,2:12]]}, query2={*=[[@1,8:8='*',<291>,1:8]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query2={query_dictionary={*=[[@1,8:8='*',<291>,1:8]]}, table_dictionary={}, def_query1={query_dictionary={*=[[@1,8:8='*',<291>,1:8]], col1=[[@7,28:31='col1',<381>,2:12]]}, table_dictionary={}, def_query0={query_dictionary={*=[[@11,49:49='*',<291>,3:10]], col1=[[@5,25:26='T3',<381>,2:9]]}, table_dictionary={third={*=[[@11,49:49='*',<291>,3:10]]}}, interface={*=[{name=*, table_ref=*}]}}, interface={col1=[{name=col1, table_ref=T3}]}, table_alias={T3=query0}}, interface={*=[{name=*, table_ref=*}]}, table_alias={F4=query1}}}",
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
		Assert.assertEquals("Table Dictionary is wrong", "{third={*=[[@19,65:65='*',<291>,2:10]]}, fourth={col5=[[@40,132:132='t',<381>,3:50]], col2=[[@7,25:25='t',<381>,1:25], [@32,111:111='t',<381>,3:29]], col3=[[@13,42:42='t',<381>,1:42]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@19,65:65='*',<291>,2:10]], col4=[[@36,122:123='F4',<381>,3:40]], col1=[[@1,8:9='F4',<381>,1:8], [@28,101:102='F4',<381>,3:19]]}, query1={last=[[@5,19:22='last',<102>,1:19]], tcol2=[[@11,35:39='tcol2',<381>,1:35]], col3=[[@15,44:47='col3',<381>,1:44]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query1={query_dictionary={last=[[@5,19:22='last',<102>,1:19]], tcol2=[[@11,35:39='tcol2',<381>,1:35]], col3=[[@15,44:47='col3',<381>,1:44]]}, table_dictionary={fourth={col5=[[@40,132:132='t',<381>,3:50]], col2=[[@7,25:25='t',<381>,1:25], [@32,111:111='t',<381>,3:29]], col3=[[@13,42:42='t',<381>,1:42]]}}, def_query0={query_dictionary={*=[[@19,65:65='*',<291>,2:10]], col4=[[@36,122:123='F4',<381>,3:40]], col1=[[@1,8:9='F4',<381>,1:8], [@28,101:102='F4',<381>,3:19]]}, table_dictionary={third={*=[[@19,65:65='*',<291>,2:10]]}}, interface={*=[{name=*, table_ref=*}]}}, filters=[{name=col1, table_ref=F4}, {name=col2, table_ref=t}, {name=col4, table_ref=F4}, {name=col5, table_ref=t}], interface={last=[{name=col1, table_ref=F4}], tcol2=[{name=col2, table_ref=t}], col3=[{name=col3, table_ref=t}]}, table_alias={t=fourth, F4=query0}}}",
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
		Assert.assertEquals("Table Dictionary is wrong", "{third={*=[[@19,65:65='*',<291>,2:10]]}, fourth={col7=[[@48,156:156='t',<381>,4:17]], col5=[[@40,132:132='t',<381>,3:50]], col2=[[@7,25:25='t',<381>,1:25], [@32,111:111='t',<381>,3:29], [@56,177:177='t',<381>,4:38]], col3=[[@13,42:42='t',<381>,1:42]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={col8=[[@60,188:189='F4',<381>,4:49]], col6=[[@44,146:147='F4',<381>,4:7]], *=[[@19,65:65='*',<291>,2:10]], col4=[[@36,122:123='F4',<381>,3:40]], col1=[[@1,8:9='F4',<381>,1:8], [@28,101:102='F4',<381>,3:19], [@52,167:168='F4',<381>,4:28]]}, query1={last=[[@5,19:22='last',<102>,1:19]], tcol2=[[@11,35:39='tcol2',<381>,1:35]], col3=[[@15,44:47='col3',<381>,1:44]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query1={query_dictionary={last=[[@5,19:22='last',<102>,1:19]], tcol2=[[@11,35:39='tcol2',<381>,1:35]], col3=[[@15,44:47='col3',<381>,1:44]]}, table_dictionary={fourth={col7=[[@48,156:156='t',<381>,4:17]], col5=[[@40,132:132='t',<381>,3:50]], col2=[[@7,25:25='t',<381>,1:25], [@32,111:111='t',<381>,3:29], [@56,177:177='t',<381>,4:38]], col3=[[@13,42:42='t',<381>,1:42]]}}, def_query0={query_dictionary={col8=[[@60,188:189='F4',<381>,4:49]], col6=[[@44,146:147='F4',<381>,4:7]], *=[[@19,65:65='*',<291>,2:10]], col4=[[@36,122:123='F4',<381>,3:40]], col1=[[@1,8:9='F4',<381>,1:8], [@28,101:102='F4',<381>,3:19], [@52,167:168='F4',<381>,4:28]]}, table_dictionary={third={*=[[@19,65:65='*',<291>,2:10]]}}, interface={*=[{name=*, table_ref=*}]}}, filters=[{name=col1, table_ref=F4}, {name=col2, table_ref=t}, {name=col4, table_ref=F4}, {name=col5, table_ref=t}, {name=col6, table_ref=F4}, {name=col7, table_ref=t}, {name=col8, table_ref=F4}], interface={last=[{name=col1, table_ref=F4}], tcol2=[{name=col2, table_ref=t}], col3=[{name=col3, table_ref=t}]}, table_alias={t=fourth, F4=query0}}}",
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
		Assert.assertEquals("Table Dictionary is wrong", "{third={*=[[@19,65:65='*',<291>,2:10]]}, fourth={col7=[[@48,157:157='t',<381>,4:18]], col5=[[@40,132:132='t',<381>,3:50]], col2=[[@7,25:25='t',<381>,1:25], [@32,111:111='t',<381>,3:29], [@56,178:178='t',<381>,4:39]], col3=[[@13,42:42='t',<381>,1:42]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={col8=[[@60,189:190='F4',<381>,4:50]], col6=[[@44,147:148='F4',<381>,4:8]], *=[[@19,65:65='*',<291>,2:10]], col4=[[@36,122:123='F4',<381>,3:40]], col1=[[@1,8:9='F4',<381>,1:8], [@28,101:102='F4',<381>,3:19], [@52,168:169='F4',<381>,4:29]]}, query1={last=[[@5,19:22='last',<102>,1:19]], tcol2=[[@11,35:39='tcol2',<381>,1:35]], col3=[[@15,44:47='col3',<381>,1:44]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query1={query_dictionary={last=[[@5,19:22='last',<102>,1:19]], tcol2=[[@11,35:39='tcol2',<381>,1:35]], col3=[[@15,44:47='col3',<381>,1:44]]}, table_dictionary={fourth={col7=[[@48,157:157='t',<381>,4:18]], col5=[[@40,132:132='t',<381>,3:50]], col2=[[@7,25:25='t',<381>,1:25], [@32,111:111='t',<381>,3:29], [@56,178:178='t',<381>,4:39]], col3=[[@13,42:42='t',<381>,1:42]]}}, def_query0={query_dictionary={col8=[[@60,189:190='F4',<381>,4:50]], col6=[[@44,147:148='F4',<381>,4:8]], *=[[@19,65:65='*',<291>,2:10]], col4=[[@36,122:123='F4',<381>,3:40]], col1=[[@1,8:9='F4',<381>,1:8], [@28,101:102='F4',<381>,3:19], [@52,168:169='F4',<381>,4:29]]}, table_dictionary={third={*=[[@19,65:65='*',<291>,2:10]]}}, interface={*=[{name=*, table_ref=*}]}}, filters=[{name=col1, table_ref=F4}, {name=col2, table_ref=t}, {name=col4, table_ref=F4}, {name=col5, table_ref=t}, {name=col6, table_ref=F4}, {name=col7, table_ref=t}, {name=col8, table_ref=F4}], interface={last=[{name=col1, table_ref=F4}], tcol2=[{name=col2, table_ref=t}], col3=[{name=col3, table_ref=t}]}, table_alias={t=fourth, F4=query0}}}",
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
		Assert.assertEquals("Table Dictionary is wrong", "{third={*=[[@19,65:65='*',<291>,2:10]]}, fourth={col10=[[@70,219:219='t',<381>,5:19]], col7=[[@48,156:156='t',<381>,4:17]], col5=[[@40,132:132='t',<381>,3:50]], col2=[[@7,25:25='t',<381>,1:25], [@32,111:111='t',<381>,3:29], [@56,177:177='t',<381>,4:38]], col3=[[@13,42:42='t',<381>,1:42]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={col8=[[@60,188:189='F4',<381>,4:49], [@74,231:232='F4',<381>,5:31]], col9=[[@66,209:210='F4',<381>,5:9]], col6=[[@44,146:147='F4',<381>,4:7]], *=[[@19,65:65='*',<291>,2:10]], col4=[[@36,122:123='F4',<381>,3:40]], col1=[[@1,8:9='F4',<381>,1:8], [@28,101:102='F4',<381>,3:19], [@52,167:168='F4',<381>,4:28]]}, query1={last=[[@5,19:22='last',<102>,1:19]], tcol2=[[@11,35:39='tcol2',<381>,1:35]], col3=[[@15,44:47='col3',<381>,1:44]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query1={query_dictionary={last=[[@5,19:22='last',<102>,1:19]], tcol2=[[@11,35:39='tcol2',<381>,1:35]], col3=[[@15,44:47='col3',<381>,1:44]]}, table_dictionary={fourth={col10=[[@70,219:219='t',<381>,5:19]], col7=[[@48,156:156='t',<381>,4:17]], col5=[[@40,132:132='t',<381>,3:50]], col2=[[@7,25:25='t',<381>,1:25], [@32,111:111='t',<381>,3:29], [@56,177:177='t',<381>,4:38]], col3=[[@13,42:42='t',<381>,1:42]]}}, def_query0={query_dictionary={col8=[[@60,188:189='F4',<381>,4:49], [@74,231:232='F4',<381>,5:31]], col9=[[@66,209:210='F4',<381>,5:9]], col6=[[@44,146:147='F4',<381>,4:7]], *=[[@19,65:65='*',<291>,2:10]], col4=[[@36,122:123='F4',<381>,3:40]], col1=[[@1,8:9='F4',<381>,1:8], [@28,101:102='F4',<381>,3:19], [@52,167:168='F4',<381>,4:28]]}, table_dictionary={third={*=[[@19,65:65='*',<291>,2:10]]}}, interface={*=[{name=*, table_ref=*}]}}, filters=[{name=col1, table_ref=F4}, {name=col2, table_ref=t}, {name=col4, table_ref=F4}, {name=col5, table_ref=t}, {name=col6, table_ref=F4}, {name=col7, table_ref=t}, {name=col8, table_ref=F4}, {name=col9, table_ref=F4}, {name=col10, table_ref=t}], interface={last=[{name=col1, table_ref=F4}], tcol2=[{name=col2, table_ref=t}], col3=[{name=col3, table_ref=t}]}, table_alias={t=fourth, F4=query0}}}",
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
		Assert.assertEquals("Table Dictionary is wrong", "{third={*=[[@28,95:95='*',<291>,2:10]]}, fourth={col7=[[@57,186:186='t',<381>,4:17]], col5=[[@49,162:162='t',<381>,3:50]], col2=[[@7,25:25='t',<381>,1:25], [@41,141:141='t',<381>,3:29], [@65,207:207='t',<381>,4:38], [@80,249:249='t',<381>,5:19]], col3=[[@13,42:42='t',<381>,1:42], [@84,257:257='t',<381>,5:27]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={col8=[[@69,218:219='F4',<381>,4:49]], col11=[[@19,54:55='F4',<381>,1:54]], col6=[[@53,176:177='F4',<381>,4:7]], *=[[@28,95:95='*',<291>,2:10]], col4=[[@45,152:153='F4',<381>,3:40]], col1=[[@1,8:9='F4',<381>,1:8], [@37,131:132='F4',<381>,3:19], [@61,197:198='F4',<381>,4:28], [@76,240:241='F4',<381>,5:10]]}, query1={last=[[@5,19:22='last',<102>,1:19]], total_col11=[[@24,67:77='total_col11',<381>,1:67]], tcol2=[[@11,35:39='tcol2',<381>,1:35]], col3=[[@15,44:47='col3',<381>,1:44]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query1={query_dictionary={last=[[@5,19:22='last',<102>,1:19]], total_col11=[[@24,67:77='total_col11',<381>,1:67]], tcol2=[[@11,35:39='tcol2',<381>,1:35]], col3=[[@15,44:47='col3',<381>,1:44]]}, table_dictionary={fourth={col7=[[@57,186:186='t',<381>,4:17]], col5=[[@49,162:162='t',<381>,3:50]], col2=[[@7,25:25='t',<381>,1:25], [@41,141:141='t',<381>,3:29], [@65,207:207='t',<381>,4:38], [@80,249:249='t',<381>,5:19]], col3=[[@13,42:42='t',<381>,1:42], [@84,257:257='t',<381>,5:27]]}}, grouped_by=[{name=col1, table_ref=F4}, {name=col2, table_ref=t}, {name=col3, table_ref=t}], def_query0={query_dictionary={col8=[[@69,218:219='F4',<381>,4:49]], col11=[[@19,54:55='F4',<381>,1:54]], col6=[[@53,176:177='F4',<381>,4:7]], *=[[@28,95:95='*',<291>,2:10]], col4=[[@45,152:153='F4',<381>,3:40]], col1=[[@1,8:9='F4',<381>,1:8], [@37,131:132='F4',<381>,3:19], [@61,197:198='F4',<381>,4:28], [@76,240:241='F4',<381>,5:10]]}, table_dictionary={third={*=[[@28,95:95='*',<291>,2:10]]}}, interface={*=[{name=*, table_ref=*}]}}, filters=[{name=col1, table_ref=F4}, {name=col2, table_ref=t}, {name=col4, table_ref=F4}, {name=col5, table_ref=t}, {name=col6, table_ref=F4}, {name=col7, table_ref=t}, {name=col8, table_ref=F4}], interface={last=[{name=col1, table_ref=F4}], total_col11=[{name=col11, table_ref=F4}], tcol2=[{name=col2, table_ref=t}], col3=[{name=col3, table_ref=t}]}, table_alias={t=fourth, F4=query0}}}",
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
		Assert.assertEquals("Table Dictionary is wrong", "{third={*=[[@19,65:65='*',<291>,2:10]]}, fourth={col7=[[@48,156:156='t',<381>,4:17]], col5=[[@40,132:132='t',<381>,3:50]], col2=[[@7,25:25='t',<381>,1:25], [@32,111:111='t',<381>,3:29], [@56,177:177='t',<381>,4:38]], col3=[[@13,42:42='t',<381>,1:42], [@67,210:210='t',<381>,5:10]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={col12=[[@77,236:237='F4',<381>,5:36]], col8=[[@60,188:189='F4',<381>,4:49]], col6=[[@44,146:147='F4',<381>,4:7]], *=[[@19,65:65='*',<291>,2:10]], col4=[[@36,122:123='F4',<381>,3:40]], col1=[[@1,8:9='F4',<381>,1:8], [@28,101:102='F4',<381>,3:19], [@52,167:168='F4',<381>,4:28], [@72,223:224='F4',<381>,5:23]]}, query1={last=[[@5,19:22='last',<102>,1:19]], tcol2=[[@11,35:39='tcol2',<381>,1:35]], col3=[[@15,44:47='col3',<381>,1:44]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query1={query_dictionary={last=[[@5,19:22='last',<102>,1:19]], tcol2=[[@11,35:39='tcol2',<381>,1:35]], col3=[[@15,44:47='col3',<381>,1:44]]}, table_dictionary={fourth={col7=[[@48,156:156='t',<381>,4:17]], col5=[[@40,132:132='t',<381>,3:50]], col2=[[@7,25:25='t',<381>,1:25], [@32,111:111='t',<381>,3:29], [@56,177:177='t',<381>,4:38]], col3=[[@13,42:42='t',<381>,1:42], [@67,210:210='t',<381>,5:10]]}}, def_query0={query_dictionary={col12=[[@77,236:237='F4',<381>,5:36]], col8=[[@60,188:189='F4',<381>,4:49]], col6=[[@44,146:147='F4',<381>,4:7]], *=[[@19,65:65='*',<291>,2:10]], col4=[[@36,122:123='F4',<381>,3:40]], col1=[[@1,8:9='F4',<381>,1:8], [@28,101:102='F4',<381>,3:19], [@52,167:168='F4',<381>,4:28], [@72,223:224='F4',<381>,5:23]]}, table_dictionary={third={*=[[@19,65:65='*',<291>,2:10]]}}, interface={*=[{name=*, table_ref=*}]}}, ordered_by=[{name=col3, table_ref=t}, {name=col1, table_ref=F4}, {name=col12, table_ref=F4}], filters=[{name=col1, table_ref=F4}, {name=col2, table_ref=t}, {name=col4, table_ref=F4}, {name=col5, table_ref=t}, {name=col6, table_ref=F4}, {name=col7, table_ref=t}, {name=col8, table_ref=F4}], interface={last=[{name=col1, table_ref=F4}], tcol2=[{name=col2, table_ref=t}], col3=[{name=col3, table_ref=t}]}, table_alias={t=fourth, F4=query0}}}",
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
		Assert.assertEquals("Table Dictionary is wrong", "{third={a=[[@19,65:65='a',<381>,2:10]], col4=[[@23,76:79='col4',<381>,2:21]]}, fourth={col5=[[@44,146:146='t',<381>,3:50]], col2=[[@7,25:25='t',<381>,1:25], [@36,125:125='t',<381>,3:29]], col3=[[@13,42:42='t',<381>,1:42]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={col4=[[@23,76:79='col4',<381>,2:21], [@40,136:137='F4',<381>,3:40]], col1=[[@21,70:73='col1',<381>,2:15], [@1,8:9='F4',<381>,1:8], [@32,115:116='F4',<381>,3:19]]}, query1={last=[[@5,19:22='last',<102>,1:19]], tcol2=[[@11,35:39='tcol2',<381>,1:35]], col3=[[@15,44:47='col3',<381>,1:44]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query1={query_dictionary={last=[[@5,19:22='last',<102>,1:19]], tcol2=[[@11,35:39='tcol2',<381>,1:35]], col3=[[@15,44:47='col3',<381>,1:44]]}, table_dictionary={fourth={col5=[[@44,146:146='t',<381>,3:50]], col2=[[@7,25:25='t',<381>,1:25], [@36,125:125='t',<381>,3:29]], col3=[[@13,42:42='t',<381>,1:42]]}}, def_query0={query_dictionary={col4=[[@23,76:79='col4',<381>,2:21], [@40,136:137='F4',<381>,3:40]], col1=[[@21,70:73='col1',<381>,2:15], [@1,8:9='F4',<381>,1:8], [@32,115:116='F4',<381>,3:19]]}, table_dictionary={third={a=[[@19,65:65='a',<381>,2:10]], col4=[[@23,76:79='col4',<381>,2:21]]}}, interface={col4=[{name=col4, table_ref=third}], col1=[{name=a, table_ref=third}]}}, filters=[{name=col1, table_ref=F4}, {name=col2, table_ref=t}, {name=col4, table_ref=F4}, {name=col5, table_ref=t}], interface={last=[{name=col1, table_ref=F4}], tcol2=[{name=col2, table_ref=t}], col3=[{name=col3, table_ref=t}]}, table_alias={t=fourth, F4=query0}}}",
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
		Assert.assertEquals("Table Dictionary is wrong", "{third={a=[[@19,65:65='a',<381>,2:10]], b=[[@23,76:76='b',<381>,2:21]], col8=[[@29,93:96='col8',<381>,2:38]], col6=[[@27,87:90='col6',<381>,2:32]]}, fourth={col7=[[@58,187:187='t',<381>,4:17]], col5=[[@50,163:163='t',<381>,3:50]], col2=[[@7,25:25='t',<381>,1:25], [@42,142:142='t',<381>,3:29], [@66,208:208='t',<381>,4:38]], col3=[[@13,42:42='t',<381>,1:42]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={col8=[[@29,93:96='col8',<381>,2:38], [@70,219:220='F4',<381>,4:49]], col6=[[@27,87:90='col6',<381>,2:32], [@54,177:178='F4',<381>,4:7]], col4=[[@25,81:84='col4',<381>,2:26], [@46,153:154='F4',<381>,3:40]], col1=[[@21,70:73='col1',<381>,2:15], [@1,8:9='F4',<381>,1:8], [@38,132:133='F4',<381>,3:19], [@62,198:199='F4',<381>,4:28]]}, query1={last=[[@5,19:22='last',<102>,1:19]], tcol2=[[@11,35:39='tcol2',<381>,1:35]], col3=[[@15,44:47='col3',<381>,1:44]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query1={query_dictionary={last=[[@5,19:22='last',<102>,1:19]], tcol2=[[@11,35:39='tcol2',<381>,1:35]], col3=[[@15,44:47='col3',<381>,1:44]]}, table_dictionary={fourth={col7=[[@58,187:187='t',<381>,4:17]], col5=[[@50,163:163='t',<381>,3:50]], col2=[[@7,25:25='t',<381>,1:25], [@42,142:142='t',<381>,3:29], [@66,208:208='t',<381>,4:38]], col3=[[@13,42:42='t',<381>,1:42]]}}, def_query0={query_dictionary={col8=[[@29,93:96='col8',<381>,2:38], [@70,219:220='F4',<381>,4:49]], col6=[[@27,87:90='col6',<381>,2:32], [@54,177:178='F4',<381>,4:7]], col4=[[@25,81:84='col4',<381>,2:26], [@46,153:154='F4',<381>,3:40]], col1=[[@21,70:73='col1',<381>,2:15], [@1,8:9='F4',<381>,1:8], [@38,132:133='F4',<381>,3:19], [@62,198:199='F4',<381>,4:28]]}, table_dictionary={third={a=[[@19,65:65='a',<381>,2:10]], b=[[@23,76:76='b',<381>,2:21]], col8=[[@29,93:96='col8',<381>,2:38]], col6=[[@27,87:90='col6',<381>,2:32]]}}, interface={col8=[{name=col8, table_ref=third}], col6=[{name=col6, table_ref=third}], col4=[{name=b, table_ref=third}], col1=[{name=a, table_ref=third}]}}, filters=[{name=col1, table_ref=F4}, {name=col2, table_ref=t}, {name=col4, table_ref=F4}, {name=col5, table_ref=t}, {name=col6, table_ref=F4}, {name=col7, table_ref=t}, {name=col8, table_ref=F4}], interface={last=[{name=col1, table_ref=F4}], tcol2=[{name=col2, table_ref=t}], col3=[{name=col3, table_ref=t}]}, table_alias={t=fourth, F4=query0}}}",
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
		Assert.assertEquals("Table Dictionary is wrong", "{third={a=[[@19,65:65='a',<381>,2:10]], b=[[@23,76:76='b',<381>,2:21]], col8=[[@29,93:96='col8',<381>,2:38]], col6=[[@27,87:90='col6',<381>,2:32]]}, fourth={col7=[[@58,188:188='t',<381>,4:18]], col5=[[@50,163:163='t',<381>,3:50]], col2=[[@7,25:25='t',<381>,1:25], [@42,142:142='t',<381>,3:29], [@66,209:209='t',<381>,4:39]], col3=[[@13,42:42='t',<381>,1:42]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={col8=[[@29,93:96='col8',<381>,2:38], [@70,220:221='F4',<381>,4:50]], col6=[[@27,87:90='col6',<381>,2:32], [@54,178:179='F4',<381>,4:8]], col4=[[@25,81:84='col4',<381>,2:26], [@46,153:154='F4',<381>,3:40]], col1=[[@21,70:73='col1',<381>,2:15], [@1,8:9='F4',<381>,1:8], [@38,132:133='F4',<381>,3:19], [@62,199:200='F4',<381>,4:29]]}, query1={last=[[@5,19:22='last',<102>,1:19]], tcol2=[[@11,35:39='tcol2',<381>,1:35]], col3=[[@15,44:47='col3',<381>,1:44]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query1={query_dictionary={last=[[@5,19:22='last',<102>,1:19]], tcol2=[[@11,35:39='tcol2',<381>,1:35]], col3=[[@15,44:47='col3',<381>,1:44]]}, table_dictionary={fourth={col7=[[@58,188:188='t',<381>,4:18]], col5=[[@50,163:163='t',<381>,3:50]], col2=[[@7,25:25='t',<381>,1:25], [@42,142:142='t',<381>,3:29], [@66,209:209='t',<381>,4:39]], col3=[[@13,42:42='t',<381>,1:42]]}}, def_query0={query_dictionary={col8=[[@29,93:96='col8',<381>,2:38], [@70,220:221='F4',<381>,4:50]], col6=[[@27,87:90='col6',<381>,2:32], [@54,178:179='F4',<381>,4:8]], col4=[[@25,81:84='col4',<381>,2:26], [@46,153:154='F4',<381>,3:40]], col1=[[@21,70:73='col1',<381>,2:15], [@1,8:9='F4',<381>,1:8], [@38,132:133='F4',<381>,3:19], [@62,199:200='F4',<381>,4:29]]}, table_dictionary={third={a=[[@19,65:65='a',<381>,2:10]], b=[[@23,76:76='b',<381>,2:21]], col8=[[@29,93:96='col8',<381>,2:38]], col6=[[@27,87:90='col6',<381>,2:32]]}}, interface={col8=[{name=col8, table_ref=third}], col6=[{name=col6, table_ref=third}], col4=[{name=b, table_ref=third}], col1=[{name=a, table_ref=third}]}}, filters=[{name=col1, table_ref=F4}, {name=col2, table_ref=t}, {name=col4, table_ref=F4}, {name=col5, table_ref=t}, {name=col6, table_ref=F4}, {name=col7, table_ref=t}, {name=col8, table_ref=F4}], interface={last=[{name=col1, table_ref=F4}], tcol2=[{name=col2, table_ref=t}], col3=[{name=col3, table_ref=t}]}, table_alias={t=fourth, F4=query0}}}",
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
		Assert.assertEquals("Table Dictionary is wrong", "{third={a=[[@19,65:65='a',<381>,2:10]], b=[[@23,76:76='b',<381>,2:21]], col8=[[@29,93:96='col8',<381>,2:38]], col9=[[@31,99:102='col9',<381>,2:44]], col6=[[@27,87:90='col6',<381>,2:32]]}, fourth={col10=[[@82,256:256='t',<381>,5:19]], col7=[[@60,193:193='t',<381>,4:17]], col5=[[@52,169:169='t',<381>,3:50]], col2=[[@7,25:25='t',<381>,1:25], [@44,148:148='t',<381>,3:29], [@68,214:214='t',<381>,4:38]], col3=[[@13,42:42='t',<381>,1:42]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={col8=[[@29,93:96='col8',<381>,2:38], [@72,225:226='F4',<381>,4:49], [@86,268:269='F4',<381>,5:31]], col9=[[@31,99:102='col9',<381>,2:44], [@78,246:247='F4',<381>,5:9]], col6=[[@27,87:90='col6',<381>,2:32], [@56,183:184='F4',<381>,4:7]], col4=[[@25,81:84='col4',<381>,2:26], [@48,159:160='F4',<381>,3:40]], col1=[[@21,70:73='col1',<381>,2:15], [@1,8:9='F4',<381>,1:8], [@40,138:139='F4',<381>,3:19], [@64,204:205='F4',<381>,4:28]]}, query1={last=[[@5,19:22='last',<102>,1:19]], tcol2=[[@11,35:39='tcol2',<381>,1:35]], col3=[[@15,44:47='col3',<381>,1:44]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query1={query_dictionary={last=[[@5,19:22='last',<102>,1:19]], tcol2=[[@11,35:39='tcol2',<381>,1:35]], col3=[[@15,44:47='col3',<381>,1:44]]}, table_dictionary={fourth={col10=[[@82,256:256='t',<381>,5:19]], col7=[[@60,193:193='t',<381>,4:17]], col5=[[@52,169:169='t',<381>,3:50]], col2=[[@7,25:25='t',<381>,1:25], [@44,148:148='t',<381>,3:29], [@68,214:214='t',<381>,4:38]], col3=[[@13,42:42='t',<381>,1:42]]}}, def_query0={query_dictionary={col8=[[@29,93:96='col8',<381>,2:38], [@72,225:226='F4',<381>,4:49], [@86,268:269='F4',<381>,5:31]], col9=[[@31,99:102='col9',<381>,2:44], [@78,246:247='F4',<381>,5:9]], col6=[[@27,87:90='col6',<381>,2:32], [@56,183:184='F4',<381>,4:7]], col4=[[@25,81:84='col4',<381>,2:26], [@48,159:160='F4',<381>,3:40]], col1=[[@21,70:73='col1',<381>,2:15], [@1,8:9='F4',<381>,1:8], [@40,138:139='F4',<381>,3:19], [@64,204:205='F4',<381>,4:28]]}, table_dictionary={third={a=[[@19,65:65='a',<381>,2:10]], b=[[@23,76:76='b',<381>,2:21]], col8=[[@29,93:96='col8',<381>,2:38]], col9=[[@31,99:102='col9',<381>,2:44]], col6=[[@27,87:90='col6',<381>,2:32]]}}, interface={col8=[{name=col8, table_ref=third}], col9=[{name=col9, table_ref=third}], col6=[{name=col6, table_ref=third}], col4=[{name=b, table_ref=third}], col1=[{name=a, table_ref=third}]}}, filters=[{name=col1, table_ref=F4}, {name=col2, table_ref=t}, {name=col4, table_ref=F4}, {name=col5, table_ref=t}, {name=col6, table_ref=F4}, {name=col7, table_ref=t}, {name=col8, table_ref=F4}, {name=col9, table_ref=F4}, {name=col10, table_ref=t}], interface={last=[{name=col1, table_ref=F4}], tcol2=[{name=col2, table_ref=t}], col3=[{name=col3, table_ref=t}]}, table_alias={t=fourth, F4=query0}}}",
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
		Assert.assertEquals("Table Dictionary is wrong", "{third={a=[[@28,95:95='a',<381>,2:10]], b=[[@32,106:106='b',<381>,2:21]], col8=[[@38,123:126='col8',<381>,2:38]], col11=[[@40,129:133='col11',<381>,2:44]], col6=[[@36,117:120='col6',<381>,2:32]]}, fourth={col7=[[@69,224:224='t',<381>,4:17]], col5=[[@61,200:200='t',<381>,3:50]], col2=[[@7,25:25='t',<381>,1:25], [@53,179:179='t',<381>,3:29], [@77,245:245='t',<381>,4:38], [@92,287:287='t',<381>,5:19]], col3=[[@13,42:42='t',<381>,1:42], [@96,295:295='t',<381>,5:27]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={col8=[[@38,123:126='col8',<381>,2:38], [@81,256:257='F4',<381>,4:49]], col11=[[@40,129:133='col11',<381>,2:44], [@19,54:55='F4',<381>,1:54]], col6=[[@36,117:120='col6',<381>,2:32], [@65,214:215='F4',<381>,4:7]], col4=[[@34,111:114='col4',<381>,2:26], [@57,190:191='F4',<381>,3:40]], col1=[[@30,100:103='col1',<381>,2:15], [@1,8:9='F4',<381>,1:8], [@49,169:170='F4',<381>,3:19], [@73,235:236='F4',<381>,4:28], [@88,278:279='F4',<381>,5:10]]}, query1={last=[[@5,19:22='last',<102>,1:19]], total_col11=[[@24,67:77='total_col11',<381>,1:67]], tcol2=[[@11,35:39='tcol2',<381>,1:35]], col3=[[@15,44:47='col3',<381>,1:44]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query1={query_dictionary={last=[[@5,19:22='last',<102>,1:19]], total_col11=[[@24,67:77='total_col11',<381>,1:67]], tcol2=[[@11,35:39='tcol2',<381>,1:35]], col3=[[@15,44:47='col3',<381>,1:44]]}, table_dictionary={fourth={col7=[[@69,224:224='t',<381>,4:17]], col5=[[@61,200:200='t',<381>,3:50]], col2=[[@7,25:25='t',<381>,1:25], [@53,179:179='t',<381>,3:29], [@77,245:245='t',<381>,4:38], [@92,287:287='t',<381>,5:19]], col3=[[@13,42:42='t',<381>,1:42], [@96,295:295='t',<381>,5:27]]}}, grouped_by=[{name=col1, table_ref=F4}, {name=col2, table_ref=t}, {name=col3, table_ref=t}], def_query0={query_dictionary={col8=[[@38,123:126='col8',<381>,2:38], [@81,256:257='F4',<381>,4:49]], col11=[[@40,129:133='col11',<381>,2:44], [@19,54:55='F4',<381>,1:54]], col6=[[@36,117:120='col6',<381>,2:32], [@65,214:215='F4',<381>,4:7]], col4=[[@34,111:114='col4',<381>,2:26], [@57,190:191='F4',<381>,3:40]], col1=[[@30,100:103='col1',<381>,2:15], [@1,8:9='F4',<381>,1:8], [@49,169:170='F4',<381>,3:19], [@73,235:236='F4',<381>,4:28], [@88,278:279='F4',<381>,5:10]]}, table_dictionary={third={a=[[@28,95:95='a',<381>,2:10]], b=[[@32,106:106='b',<381>,2:21]], col8=[[@38,123:126='col8',<381>,2:38]], col11=[[@40,129:133='col11',<381>,2:44]], col6=[[@36,117:120='col6',<381>,2:32]]}}, interface={col8=[{name=col8, table_ref=third}], col11=[{name=col11, table_ref=third}], col6=[{name=col6, table_ref=third}], col4=[{name=b, table_ref=third}], col1=[{name=a, table_ref=third}]}}, filters=[{name=col1, table_ref=F4}, {name=col2, table_ref=t}, {name=col4, table_ref=F4}, {name=col5, table_ref=t}, {name=col6, table_ref=F4}, {name=col7, table_ref=t}, {name=col8, table_ref=F4}], interface={last=[{name=col1, table_ref=F4}], total_col11=[{name=col11, table_ref=F4}], tcol2=[{name=col2, table_ref=t}], col3=[{name=col3, table_ref=t}]}, table_alias={t=fourth, F4=query0}}}",
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
		Assert.assertEquals("Table Dictionary is wrong", "{third={a=[[@19,65:65='a',<381>,2:10]], b=[[@23,76:76='b',<381>,2:21]], col8=[[@29,93:96='col8',<381>,2:38]], col12=[[@31,99:103='col12',<381>,2:44]], col6=[[@27,87:90='col6',<381>,2:32]]}, fourth={col7=[[@60,194:194='t',<381>,4:17]], col5=[[@52,170:170='t',<381>,3:50]], col2=[[@7,25:25='t',<381>,1:25], [@44,149:149='t',<381>,3:29], [@68,215:215='t',<381>,4:38]], col3=[[@13,42:42='t',<381>,1:42], [@79,248:248='t',<381>,5:10]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={col8=[[@29,93:96='col8',<381>,2:38], [@72,226:227='F4',<381>,4:49]], col12=[[@31,99:103='col12',<381>,2:44], [@89,274:275='F4',<381>,5:36]], col6=[[@27,87:90='col6',<381>,2:32], [@56,184:185='F4',<381>,4:7]], col4=[[@25,81:84='col4',<381>,2:26], [@48,160:161='F4',<381>,3:40]], col1=[[@21,70:73='col1',<381>,2:15], [@1,8:9='F4',<381>,1:8], [@40,139:140='F4',<381>,3:19], [@64,205:206='F4',<381>,4:28], [@84,261:262='F4',<381>,5:23]]}, query1={last=[[@5,19:22='last',<102>,1:19]], tcol2=[[@11,35:39='tcol2',<381>,1:35]], col3=[[@15,44:47='col3',<381>,1:44]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query1={query_dictionary={last=[[@5,19:22='last',<102>,1:19]], tcol2=[[@11,35:39='tcol2',<381>,1:35]], col3=[[@15,44:47='col3',<381>,1:44]]}, table_dictionary={fourth={col7=[[@60,194:194='t',<381>,4:17]], col5=[[@52,170:170='t',<381>,3:50]], col2=[[@7,25:25='t',<381>,1:25], [@44,149:149='t',<381>,3:29], [@68,215:215='t',<381>,4:38]], col3=[[@13,42:42='t',<381>,1:42], [@79,248:248='t',<381>,5:10]]}}, def_query0={query_dictionary={col8=[[@29,93:96='col8',<381>,2:38], [@72,226:227='F4',<381>,4:49]], col12=[[@31,99:103='col12',<381>,2:44], [@89,274:275='F4',<381>,5:36]], col6=[[@27,87:90='col6',<381>,2:32], [@56,184:185='F4',<381>,4:7]], col4=[[@25,81:84='col4',<381>,2:26], [@48,160:161='F4',<381>,3:40]], col1=[[@21,70:73='col1',<381>,2:15], [@1,8:9='F4',<381>,1:8], [@40,139:140='F4',<381>,3:19], [@64,205:206='F4',<381>,4:28], [@84,261:262='F4',<381>,5:23]]}, table_dictionary={third={a=[[@19,65:65='a',<381>,2:10]], b=[[@23,76:76='b',<381>,2:21]], col8=[[@29,93:96='col8',<381>,2:38]], col12=[[@31,99:103='col12',<381>,2:44]], col6=[[@27,87:90='col6',<381>,2:32]]}}, interface={col8=[{name=col8, table_ref=third}], col12=[{name=col12, table_ref=third}], col6=[{name=col6, table_ref=third}], col4=[{name=b, table_ref=third}], col1=[{name=a, table_ref=third}]}}, ordered_by=[{name=col3, table_ref=t}, {name=col1, table_ref=F4}, {name=col12, table_ref=F4}], filters=[{name=col1, table_ref=F4}, {name=col2, table_ref=t}, {name=col4, table_ref=F4}, {name=col5, table_ref=t}, {name=col6, table_ref=F4}, {name=col7, table_ref=t}, {name=col8, table_ref=F4}], interface={last=[{name=col1, table_ref=F4}], tcol2=[{name=col2, table_ref=t}], col3=[{name=col3, table_ref=t}]}, table_alias={t=fourth, F4=query0}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void subqueryDictionaryExtensionJoinClauseMissingQualifiedV21() {
		final String query = " SELECT F4.col1 as last, t.col2 as tcol2, t.col3 FROM "
			+ "\n  (select a as col1, col4 from third) F4"
			+ "\n  join fourth t on F4.col1 = t.col2 and F4.col4 = t.col5 and F4.col99 = t.col2";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={col4=[[@23,76:79='col4',<381>,2:21], [@40,136:137='F4',<381>,3:40]], col1=[[@21,70:73='col1',<381>,2:15], [@1,8:9='F4',<381>,1:8], [@32,115:116='F4',<381>,3:19]]}, query1={last=[[@5,19:22='last',<102>,1:19]], tcol2=[[@11,35:39='tcol2',<381>,1:35]], col3=[[@15,44:47='col3',<381>,1:44]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query1={query_dictionary={last=[[@5,19:22='last',<102>,1:19]], tcol2=[[@11,35:39='tcol2',<381>,1:35]], col3=[[@15,44:47='col3',<381>,1:44]]}, table_dictionary={fourth={col5=[[@44,146:146='t',<381>,3:50]], col2=[[@7,25:25='t',<381>,1:25], [@36,125:125='t',<381>,3:29], [@52,168:168='t',<381>,3:72]], col3=[[@13,42:42='t',<381>,1:42]]}}, def_query0={query_dictionary={col4=[[@23,76:79='col4',<381>,2:21], [@40,136:137='F4',<381>,3:40]], col1=[[@21,70:73='col1',<381>,2:15], [@1,8:9='F4',<381>,1:8], [@32,115:116='F4',<381>,3:19]]}, table_dictionary={third={a=[[@19,65:65='a',<381>,2:10]], col4=[[@23,76:79='col4',<381>,2:21]]}}, interface={col4=[{name=col4, table_ref=third}], col1=[{name=a, table_ref=third}]}}, filters=[{name=col1, table_ref=F4}, {name=col2, table_ref=t}, {name=col4, table_ref=F4}, {name=col5, table_ref=t}, {name=col99, table_ref=F4}], interface={last=[{name=col1, table_ref=F4}], tcol2=[{name=col2, table_ref=t}], col3=[{name=col3, table_ref=t}]}, table_alias={t=fourth, F4=query0}}}",
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

		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={col8=[[@29,93:96='col8',<381>,2:38], [@70,219:220='F4',<381>,4:49]], col6=[[@27,87:90='col6',<381>,2:32], [@54,177:178='F4',<381>,4:7]], col4=[[@25,81:84='col4',<381>,2:26], [@46,153:154='F4',<381>,3:40]], col1=[[@21,70:73='col1',<381>,2:15], [@1,8:9='F4',<381>,1:8], [@38,132:133='F4',<381>,3:19], [@62,198:199='F4',<381>,4:28]]}, query1={last=[[@5,19:22='last',<102>,1:19]], tcol2=[[@11,35:39='tcol2',<381>,1:35]], col3=[[@15,44:47='col3',<381>,1:44]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query1={query_dictionary={last=[[@5,19:22='last',<102>,1:19]], tcol2=[[@11,35:39='tcol2',<381>,1:35]], col3=[[@15,44:47='col3',<381>,1:44]]}, table_dictionary={fourth={col7=[[@58,187:187='t',<381>,4:17]], col5=[[@50,163:163='t',<381>,3:50]], col2=[[@7,25:25='t',<381>,1:25], [@42,142:142='t',<381>,3:29], [@66,208:208='t',<381>,4:38]], col3=[[@13,42:42='t',<381>,1:42]]}}, def_query0={query_dictionary={col8=[[@29,93:96='col8',<381>,2:38], [@70,219:220='F4',<381>,4:49]], col6=[[@27,87:90='col6',<381>,2:32], [@54,177:178='F4',<381>,4:7]], col4=[[@25,81:84='col4',<381>,2:26], [@46,153:154='F4',<381>,3:40]], col1=[[@21,70:73='col1',<381>,2:15], [@1,8:9='F4',<381>,1:8], [@38,132:133='F4',<381>,3:19], [@62,198:199='F4',<381>,4:28]]}, table_dictionary={third={a=[[@19,65:65='a',<381>,2:10]], b=[[@23,76:76='b',<381>,2:21]], col8=[[@29,93:96='col8',<381>,2:38]], col6=[[@27,87:90='col6',<381>,2:32]]}}, interface={col8=[{name=col8, table_ref=third}], col6=[{name=col6, table_ref=third}], col4=[{name=b, table_ref=third}], col1=[{name=a, table_ref=third}]}}, filters=[{name=col1, table_ref=F4}, {name=col2, table_ref=t}, {name=col4, table_ref=F4}, {name=col5, table_ref=t}, {name=col6, table_ref=F4}, {name=col7, table_ref=t}, {name=col8, table_ref=F4}, {name=col99, table_ref=F4}], interface={last=[{name=col1, table_ref=F4}], tcol2=[{name=col2, table_ref=t}], col3=[{name=col3, table_ref=t}]}, table_alias={t=fourth, F4=query0}}}",
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

		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={col8=[[@29,93:96='col8',<381>,2:38], [@70,220:221='F4',<381>,4:50]], col6=[[@27,87:90='col6',<381>,2:32], [@54,178:179='F4',<381>,4:8]], col4=[[@25,81:84='col4',<381>,2:26], [@46,153:154='F4',<381>,3:40]], col1=[[@21,70:73='col1',<381>,2:15], [@1,8:9='F4',<381>,1:8], [@38,132:133='F4',<381>,3:19], [@62,199:200='F4',<381>,4:29]]}, query1={last=[[@5,19:22='last',<102>,1:19]], tcol2=[[@11,35:39='tcol2',<381>,1:35]], col3=[[@15,44:47='col3',<381>,1:44]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query1={query_dictionary={last=[[@5,19:22='last',<102>,1:19]], tcol2=[[@11,35:39='tcol2',<381>,1:35]], col3=[[@15,44:47='col3',<381>,1:44]]}, table_dictionary={fourth={col7=[[@58,188:188='t',<381>,4:18]], col5=[[@50,163:163='t',<381>,3:50]], col2=[[@7,25:25='t',<381>,1:25], [@42,142:142='t',<381>,3:29], [@66,209:209='t',<381>,4:39]], col3=[[@13,42:42='t',<381>,1:42]]}}, def_query0={query_dictionary={col8=[[@29,93:96='col8',<381>,2:38], [@70,220:221='F4',<381>,4:50]], col6=[[@27,87:90='col6',<381>,2:32], [@54,178:179='F4',<381>,4:8]], col4=[[@25,81:84='col4',<381>,2:26], [@46,153:154='F4',<381>,3:40]], col1=[[@21,70:73='col1',<381>,2:15], [@1,8:9='F4',<381>,1:8], [@38,132:133='F4',<381>,3:19], [@62,199:200='F4',<381>,4:29]]}, table_dictionary={third={a=[[@19,65:65='a',<381>,2:10]], b=[[@23,76:76='b',<381>,2:21]], col8=[[@29,93:96='col8',<381>,2:38]], col6=[[@27,87:90='col6',<381>,2:32]]}}, interface={col8=[{name=col8, table_ref=third}], col6=[{name=col6, table_ref=third}], col4=[{name=b, table_ref=third}], col1=[{name=a, table_ref=third}]}}, filters=[{name=col1, table_ref=F4}, {name=col2, table_ref=t}, {name=col4, table_ref=F4}, {name=col5, table_ref=t}, {name=col6, table_ref=F4}, {name=col7, table_ref=t}, {name=col8, table_ref=F4}, {name=col99, table_ref=F4}], interface={last=[{name=col1, table_ref=F4}], tcol2=[{name=col2, table_ref=t}], col3=[{name=col3, table_ref=t}]}, table_alias={t=fourth, F4=query0}}}",
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

		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={col8=[[@29,93:96='col8',<381>,2:38], [@72,225:226='F4',<381>,4:49], [@86,268:269='F4',<381>,5:31]], col9=[[@31,99:102='col9',<381>,2:44], [@78,246:247='F4',<381>,5:9]], col6=[[@27,87:90='col6',<381>,2:32], [@56,183:184='F4',<381>,4:7]], col4=[[@25,81:84='col4',<381>,2:26], [@48,159:160='F4',<381>,3:40]], col1=[[@21,70:73='col1',<381>,2:15], [@1,8:9='F4',<381>,1:8], [@40,138:139='F4',<381>,3:19], [@64,204:205='F4',<381>,4:28]]}, query1={last=[[@5,19:22='last',<102>,1:19]], tcol2=[[@11,35:39='tcol2',<381>,1:35]], col3=[[@15,44:47='col3',<381>,1:44]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query1={query_dictionary={last=[[@5,19:22='last',<102>,1:19]], tcol2=[[@11,35:39='tcol2',<381>,1:35]], col3=[[@15,44:47='col3',<381>,1:44]]}, table_dictionary={fourth={col10=[[@82,256:256='t',<381>,5:19]], col7=[[@60,193:193='t',<381>,4:17]], col5=[[@52,169:169='t',<381>,3:50]], col2=[[@7,25:25='t',<381>,1:25], [@44,148:148='t',<381>,3:29], [@68,214:214='t',<381>,4:38]], col3=[[@13,42:42='t',<381>,1:42]]}}, def_query0={query_dictionary={col8=[[@29,93:96='col8',<381>,2:38], [@72,225:226='F4',<381>,4:49], [@86,268:269='F4',<381>,5:31]], col9=[[@31,99:102='col9',<381>,2:44], [@78,246:247='F4',<381>,5:9]], col6=[[@27,87:90='col6',<381>,2:32], [@56,183:184='F4',<381>,4:7]], col4=[[@25,81:84='col4',<381>,2:26], [@48,159:160='F4',<381>,3:40]], col1=[[@21,70:73='col1',<381>,2:15], [@1,8:9='F4',<381>,1:8], [@40,138:139='F4',<381>,3:19], [@64,204:205='F4',<381>,4:28]]}, table_dictionary={third={a=[[@19,65:65='a',<381>,2:10]], b=[[@23,76:76='b',<381>,2:21]], col8=[[@29,93:96='col8',<381>,2:38]], col9=[[@31,99:102='col9',<381>,2:44]], col6=[[@27,87:90='col6',<381>,2:32]]}}, interface={col8=[{name=col8, table_ref=third}], col9=[{name=col9, table_ref=third}], col6=[{name=col6, table_ref=third}], col4=[{name=b, table_ref=third}], col1=[{name=a, table_ref=third}]}}, filters=[{name=col1, table_ref=F4}, {name=col2, table_ref=t}, {name=col4, table_ref=F4}, {name=col5, table_ref=t}, {name=col6, table_ref=F4}, {name=col7, table_ref=t}, {name=col8, table_ref=F4}, {name=col9, table_ref=F4}, {name=col10, table_ref=t}, {name=col99, table_ref=F4}], interface={last=[{name=col1, table_ref=F4}], tcol2=[{name=col2, table_ref=t}], col3=[{name=col3, table_ref=t}]}, table_alias={t=fourth, F4=query0}}}",
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

		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={col8=[[@38,123:126='col8',<381>,2:38], [@81,256:257='F4',<381>,4:49]], col11=[[@40,129:133='col11',<381>,2:44], [@19,54:55='F4',<381>,1:54]], col6=[[@36,117:120='col6',<381>,2:32], [@65,214:215='F4',<381>,4:7]], col4=[[@34,111:114='col4',<381>,2:26], [@57,190:191='F4',<381>,3:40]], col1=[[@30,100:103='col1',<381>,2:15], [@1,8:9='F4',<381>,1:8], [@49,169:170='F4',<381>,3:19], [@73,235:236='F4',<381>,4:28], [@88,278:279='F4',<381>,5:10]]}, query1={last=[[@5,19:22='last',<102>,1:19]], total_col11=[[@24,67:77='total_col11',<381>,1:67]], tcol2=[[@11,35:39='tcol2',<381>,1:35]], col3=[[@15,44:47='col3',<381>,1:44]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query1={query_dictionary={last=[[@5,19:22='last',<102>,1:19]], total_col11=[[@24,67:77='total_col11',<381>,1:67]], tcol2=[[@11,35:39='tcol2',<381>,1:35]], col3=[[@15,44:47='col3',<381>,1:44]]}, table_dictionary={fourth={col7=[[@69,224:224='t',<381>,4:17]], col5=[[@61,200:200='t',<381>,3:50]], col2=[[@7,25:25='t',<381>,1:25], [@53,179:179='t',<381>,3:29], [@77,245:245='t',<381>,4:38], [@92,287:287='t',<381>,5:19]], col3=[[@13,42:42='t',<381>,1:42], [@96,295:295='t',<381>,5:27]]}}, grouped_by=[{name=col1, table_ref=F4}, {name=col2, table_ref=t}, {name=col3, table_ref=t}, {name=col99, table_ref=F4}], def_query0={query_dictionary={col8=[[@38,123:126='col8',<381>,2:38], [@81,256:257='F4',<381>,4:49]], col11=[[@40,129:133='col11',<381>,2:44], [@19,54:55='F4',<381>,1:54]], col6=[[@36,117:120='col6',<381>,2:32], [@65,214:215='F4',<381>,4:7]], col4=[[@34,111:114='col4',<381>,2:26], [@57,190:191='F4',<381>,3:40]], col1=[[@30,100:103='col1',<381>,2:15], [@1,8:9='F4',<381>,1:8], [@49,169:170='F4',<381>,3:19], [@73,235:236='F4',<381>,4:28], [@88,278:279='F4',<381>,5:10]]}, table_dictionary={third={a=[[@28,95:95='a',<381>,2:10]], b=[[@32,106:106='b',<381>,2:21]], col8=[[@38,123:126='col8',<381>,2:38]], col11=[[@40,129:133='col11',<381>,2:44]], col6=[[@36,117:120='col6',<381>,2:32]]}}, interface={col8=[{name=col8, table_ref=third}], col11=[{name=col11, table_ref=third}], col6=[{name=col6, table_ref=third}], col4=[{name=b, table_ref=third}], col1=[{name=a, table_ref=third}]}}, filters=[{name=col1, table_ref=F4}, {name=col2, table_ref=t}, {name=col4, table_ref=F4}, {name=col5, table_ref=t}, {name=col6, table_ref=F4}, {name=col7, table_ref=t}, {name=col8, table_ref=F4}], interface={last=[{name=col1, table_ref=F4}], total_col11=[{name=col11, table_ref=F4}], tcol2=[{name=col2, table_ref=t}], col3=[{name=col3, table_ref=t}]}, table_alias={t=fourth, F4=query0}}}",
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

		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={col8=[[@29,93:96='col8',<381>,2:38], [@72,226:227='F4',<381>,4:49]], col12=[[@31,99:103='col12',<381>,2:44], [@89,274:275='F4',<381>,5:36]], col6=[[@27,87:90='col6',<381>,2:32], [@56,184:185='F4',<381>,4:7]], col4=[[@25,81:84='col4',<381>,2:26], [@48,160:161='F4',<381>,3:40]], col1=[[@21,70:73='col1',<381>,2:15], [@1,8:9='F4',<381>,1:8], [@40,139:140='F4',<381>,3:19], [@64,205:206='F4',<381>,4:28], [@84,261:262='F4',<381>,5:23]]}, query1={last=[[@5,19:22='last',<102>,1:19]], tcol2=[[@11,35:39='tcol2',<381>,1:35]], col3=[[@15,44:47='col3',<381>,1:44]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query1={query_dictionary={last=[[@5,19:22='last',<102>,1:19]], tcol2=[[@11,35:39='tcol2',<381>,1:35]], col3=[[@15,44:47='col3',<381>,1:44]]}, table_dictionary={fourth={col7=[[@60,194:194='t',<381>,4:17]], col5=[[@52,170:170='t',<381>,3:50]], col2=[[@7,25:25='t',<381>,1:25], [@44,149:149='t',<381>,3:29], [@68,215:215='t',<381>,4:38]], col3=[[@13,42:42='t',<381>,1:42], [@79,248:248='t',<381>,5:10]]}}, def_query0={query_dictionary={col8=[[@29,93:96='col8',<381>,2:38], [@72,226:227='F4',<381>,4:49]], col12=[[@31,99:103='col12',<381>,2:44], [@89,274:275='F4',<381>,5:36]], col6=[[@27,87:90='col6',<381>,2:32], [@56,184:185='F4',<381>,4:7]], col4=[[@25,81:84='col4',<381>,2:26], [@48,160:161='F4',<381>,3:40]], col1=[[@21,70:73='col1',<381>,2:15], [@1,8:9='F4',<381>,1:8], [@40,139:140='F4',<381>,3:19], [@64,205:206='F4',<381>,4:28], [@84,261:262='F4',<381>,5:23]]}, table_dictionary={third={a=[[@19,65:65='a',<381>,2:10]], b=[[@23,76:76='b',<381>,2:21]], col8=[[@29,93:96='col8',<381>,2:38]], col12=[[@31,99:103='col12',<381>,2:44]], col6=[[@27,87:90='col6',<381>,2:32]]}}, interface={col8=[{name=col8, table_ref=third}], col12=[{name=col12, table_ref=third}], col6=[{name=col6, table_ref=third}], col4=[{name=b, table_ref=third}], col1=[{name=a, table_ref=third}]}}, ordered_by=[{name=col3, table_ref=t}, {name=col1, table_ref=F4}, {name=col12, table_ref=F4}, {name=col99, table_ref=F4}], filters=[{name=col1, table_ref=F4}, {name=col2, table_ref=t}, {name=col4, table_ref=F4}, {name=col5, table_ref=t}, {name=col6, table_ref=F4}, {name=col7, table_ref=t}, {name=col8, table_ref=F4}], interface={last=[{name=col1, table_ref=F4}], tcol2=[{name=col2, table_ref=t}], col3=[{name=col3, table_ref=t}]}, table_alias={t=fourth, F4=query0}}}",
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
		Assert.assertEquals("Table Dictionary is wrong", "{third={a=[[@19,65:65='a',<381>,2:10]], col4=[[@23,76:79='col4',<381>,2:21]]}, fourth={*=[[@31,111:111='*',<291>,3:15]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={col4=[[@23,76:79='col4',<381>,2:21], [@46,155:156='F4',<381>,3:59]], *=[[@31,111:111='*',<291>,3:15]], col1=[[@21,70:73='col1',<381>,2:15], [@1,8:9='F4',<381>,1:8], [@38,134:135='F4',<381>,3:38]]}, query1={*=[[@31,111:111='*',<291>,3:15]], col5=[[@50,165:165='t',<381>,3:69]], col2=[[@7,25:25='t',<381>,1:25], [@42,144:144='t',<381>,3:48]], col3=[[@13,42:42='t',<381>,1:42]]}, query2={last=[[@5,19:22='last',<102>,1:19]], tcol2=[[@11,35:39='tcol2',<381>,1:35]], col3=[[@15,44:47='col3',<381>,1:44]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query2={query_dictionary={last=[[@5,19:22='last',<102>,1:19]], tcol2=[[@11,35:39='tcol2',<381>,1:35]], col3=[[@15,44:47='col3',<381>,1:44]]}, table_dictionary={}, def_query1={query_dictionary={*=[[@31,111:111='*',<291>,3:15]], col5=[[@50,165:165='t',<381>,3:69]], col2=[[@7,25:25='t',<381>,1:25], [@42,144:144='t',<381>,3:48]], col3=[[@13,42:42='t',<381>,1:42]]}, table_dictionary={fourth={*=[[@31,111:111='*',<291>,3:15]]}}, interface={*=[{name=*, table_ref=*}]}}, def_query0={query_dictionary={col4=[[@23,76:79='col4',<381>,2:21], [@46,155:156='F4',<381>,3:59]], *=[[@31,111:111='*',<291>,3:15]], col1=[[@21,70:73='col1',<381>,2:15], [@1,8:9='F4',<381>,1:8], [@38,134:135='F4',<381>,3:38]]}, table_dictionary={third={a=[[@19,65:65='a',<381>,2:10]], col4=[[@23,76:79='col4',<381>,2:21]]}}, interface={col4=[{name=col4, table_ref=third}], col1=[{name=a, table_ref=third}]}}, filters=[{name=col1, table_ref=F4}, {name=col2, table_ref=t}, {name=col4, table_ref=F4}, {name=col5, table_ref=t}], interface={last=[{name=col1, table_ref=F4}], tcol2=[{name=col2, table_ref=t}], col3=[{name=col3, table_ref=t}]}, table_alias={t=query1, F4=query0}}}",
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
		Assert.assertEquals("Table Dictionary is wrong", "{third={a=[[@19,65:65='a',<381>,2:10]], b=[[@23,76:76='b',<381>,2:21]], col8=[[@29,93:96='col8',<381>,2:38]], col6=[[@27,87:90='col6',<381>,2:32]]}, fourth={*=[[@37,128:128='*',<291>,3:15]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={col8=[[@29,93:96='col8',<381>,2:38], [@76,238:239='F4',<381>,4:49]], col6=[[@27,87:90='col6',<381>,2:32], [@60,196:197='F4',<381>,4:7]], col4=[[@25,81:84='col4',<381>,2:26], [@52,172:173='F4',<381>,3:59]], *=[[@37,128:128='*',<291>,3:15]], col1=[[@21,70:73='col1',<381>,2:15], [@1,8:9='F4',<381>,1:8], [@44,151:152='F4',<381>,3:38], [@68,217:218='F4',<381>,4:28]]}, query1={col7=[[@64,206:206='t',<381>,4:17]], *=[[@37,128:128='*',<291>,3:15]], col5=[[@56,182:182='t',<381>,3:69]], col2=[[@7,25:25='t',<381>,1:25], [@48,161:161='t',<381>,3:48], [@72,227:227='t',<381>,4:38]], col3=[[@13,42:42='t',<381>,1:42]]}, query2={last=[[@5,19:22='last',<102>,1:19]], tcol2=[[@11,35:39='tcol2',<381>,1:35]], col3=[[@15,44:47='col3',<381>,1:44]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query2={query_dictionary={last=[[@5,19:22='last',<102>,1:19]], tcol2=[[@11,35:39='tcol2',<381>,1:35]], col3=[[@15,44:47='col3',<381>,1:44]]}, table_dictionary={}, def_query1={query_dictionary={col7=[[@64,206:206='t',<381>,4:17]], *=[[@37,128:128='*',<291>,3:15]], col5=[[@56,182:182='t',<381>,3:69]], col2=[[@7,25:25='t',<381>,1:25], [@48,161:161='t',<381>,3:48], [@72,227:227='t',<381>,4:38]], col3=[[@13,42:42='t',<381>,1:42]]}, table_dictionary={fourth={*=[[@37,128:128='*',<291>,3:15]]}}, interface={*=[{name=*, table_ref=*}]}}, def_query0={query_dictionary={col8=[[@29,93:96='col8',<381>,2:38], [@76,238:239='F4',<381>,4:49]], col6=[[@27,87:90='col6',<381>,2:32], [@60,196:197='F4',<381>,4:7]], col4=[[@25,81:84='col4',<381>,2:26], [@52,172:173='F4',<381>,3:59]], *=[[@37,128:128='*',<291>,3:15]], col1=[[@21,70:73='col1',<381>,2:15], [@1,8:9='F4',<381>,1:8], [@44,151:152='F4',<381>,3:38], [@68,217:218='F4',<381>,4:28]]}, table_dictionary={third={a=[[@19,65:65='a',<381>,2:10]], b=[[@23,76:76='b',<381>,2:21]], col8=[[@29,93:96='col8',<381>,2:38]], col6=[[@27,87:90='col6',<381>,2:32]]}}, interface={col8=[{name=col8, table_ref=third}], col6=[{name=col6, table_ref=third}], col4=[{name=b, table_ref=third}], col1=[{name=a, table_ref=third}]}}, filters=[{name=col1, table_ref=F4}, {name=col2, table_ref=t}, {name=col4, table_ref=F4}, {name=col5, table_ref=t}, {name=col6, table_ref=F4}, {name=col7, table_ref=t}, {name=col8, table_ref=F4}], interface={last=[{name=col1, table_ref=F4}], tcol2=[{name=col2, table_ref=t}], col3=[{name=col3, table_ref=t}]}, table_alias={t=query1, F4=query0}}}",
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
		Assert.assertEquals("Table Dictionary is wrong", "{third={a=[[@19,65:65='a',<381>,2:10]], b=[[@23,76:76='b',<381>,2:21]], col8=[[@29,93:96='col8',<381>,2:38]], col9=[[@31,99:102='col9',<381>,2:44]], col6=[[@27,87:90='col6',<381>,2:32]]}, fourth={missing=[[@92,284:290='missing',<381>,5:47]], col10=[[@82,256:256='t',<381>,5:19]], col7=[[@60,193:193='t',<381>,4:17]], col5=[[@52,169:169='t',<381>,3:50]], col2=[[@7,25:25='t',<381>,1:25], [@44,148:148='t',<381>,3:29], [@68,214:214='t',<381>,4:38]], col3=[[@13,42:42='t',<381>,1:42]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={col8=[[@29,93:96='col8',<381>,2:38], [@72,225:226='F4',<381>,4:49], [@86,268:269='F4',<381>,5:31]], col9=[[@31,99:102='col9',<381>,2:44], [@78,246:247='F4',<381>,5:9]], col6=[[@27,87:90='col6',<381>,2:32], [@56,183:184='F4',<381>,4:7]], col4=[[@25,81:84='col4',<381>,2:26], [@48,159:160='F4',<381>,3:40]], col1=[[@21,70:73='col1',<381>,2:15], [@1,8:9='F4',<381>,1:8], [@40,138:139='F4',<381>,3:19], [@64,204:205='F4',<381>,4:28]]}, query1={last=[[@5,19:22='last',<102>,1:19]], tcol2=[[@11,35:39='tcol2',<381>,1:35]], col3=[[@15,44:47='col3',<381>,1:44]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query1={query_dictionary={last=[[@5,19:22='last',<102>,1:19]], tcol2=[[@11,35:39='tcol2',<381>,1:35]], col3=[[@15,44:47='col3',<381>,1:44]]}, table_dictionary={fourth={missing=[[@92,284:290='missing',<381>,5:47]], col10=[[@82,256:256='t',<381>,5:19]], col7=[[@60,193:193='t',<381>,4:17]], col5=[[@52,169:169='t',<381>,3:50]], col2=[[@7,25:25='t',<381>,1:25], [@44,148:148='t',<381>,3:29], [@68,214:214='t',<381>,4:38]], col3=[[@13,42:42='t',<381>,1:42]]}}, def_query0={query_dictionary={col8=[[@29,93:96='col8',<381>,2:38], [@72,225:226='F4',<381>,4:49], [@86,268:269='F4',<381>,5:31]], col9=[[@31,99:102='col9',<381>,2:44], [@78,246:247='F4',<381>,5:9]], col6=[[@27,87:90='col6',<381>,2:32], [@56,183:184='F4',<381>,4:7]], col4=[[@25,81:84='col4',<381>,2:26], [@48,159:160='F4',<381>,3:40]], col1=[[@21,70:73='col1',<381>,2:15], [@1,8:9='F4',<381>,1:8], [@40,138:139='F4',<381>,3:19], [@64,204:205='F4',<381>,4:28]]}, table_dictionary={third={a=[[@19,65:65='a',<381>,2:10]], b=[[@23,76:76='b',<381>,2:21]], col8=[[@29,93:96='col8',<381>,2:38]], col9=[[@31,99:102='col9',<381>,2:44]], col6=[[@27,87:90='col6',<381>,2:32]]}}, interface={col8=[{name=col8, table_ref=third}], col9=[{name=col9, table_ref=third}], col6=[{name=col6, table_ref=third}], col4=[{name=b, table_ref=third}], col1=[{name=a, table_ref=third}]}}, filters=[{name=col1, table_ref=F4}, {name=col2, table_ref=t}, {name=col4, table_ref=F4}, {name=col5, table_ref=t}, {name=col6, table_ref=F4}, {name=col7, table_ref=t}, {name=col8, table_ref=F4}, {name=col9, table_ref=F4}, {name=col10, table_ref=t}, {name=missing, table_ref=fourth}], interface={last=[{name=col1, table_ref=F4}], tcol2=[{name=col2, table_ref=t}], col3=[{name=col3, table_ref=t}]}, table_alias={t=fourth, F4=query0}}}",
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
		Assert.assertEquals("Table Dictionary is wrong", "{third={a=[[@28,95:95='a',<381>,2:10]], b=[[@32,106:106='b',<381>,2:21]], col8=[[@38,123:126='col8',<381>,2:38]], col11=[[@40,129:133='col11',<381>,2:44]], col6=[[@36,117:120='col6',<381>,2:32]]}, fourth={missing=[[@100,303:309='missing',<381>,5:35]], col7=[[@69,224:224='t',<381>,4:17]], col5=[[@61,200:200='t',<381>,3:50]], col2=[[@7,25:25='t',<381>,1:25], [@53,179:179='t',<381>,3:29], [@77,245:245='t',<381>,4:38], [@92,287:287='t',<381>,5:19]], col3=[[@13,42:42='t',<381>,1:42], [@96,295:295='t',<381>,5:27]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={col8=[[@38,123:126='col8',<381>,2:38], [@81,256:257='F4',<381>,4:49]], col11=[[@40,129:133='col11',<381>,2:44], [@19,54:55='F4',<381>,1:54]], col6=[[@36,117:120='col6',<381>,2:32], [@65,214:215='F4',<381>,4:7]], col4=[[@34,111:114='col4',<381>,2:26], [@57,190:191='F4',<381>,3:40]], col1=[[@30,100:103='col1',<381>,2:15], [@1,8:9='F4',<381>,1:8], [@49,169:170='F4',<381>,3:19], [@73,235:236='F4',<381>,4:28], [@88,278:279='F4',<381>,5:10]]}, query1={last=[[@5,19:22='last',<102>,1:19]], total_col11=[[@24,67:77='total_col11',<381>,1:67]], tcol2=[[@11,35:39='tcol2',<381>,1:35]], col3=[[@15,44:47='col3',<381>,1:44]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query1={query_dictionary={last=[[@5,19:22='last',<102>,1:19]], total_col11=[[@24,67:77='total_col11',<381>,1:67]], tcol2=[[@11,35:39='tcol2',<381>,1:35]], col3=[[@15,44:47='col3',<381>,1:44]]}, table_dictionary={fourth={missing=[[@100,303:309='missing',<381>,5:35]], col7=[[@69,224:224='t',<381>,4:17]], col5=[[@61,200:200='t',<381>,3:50]], col2=[[@7,25:25='t',<381>,1:25], [@53,179:179='t',<381>,3:29], [@77,245:245='t',<381>,4:38], [@92,287:287='t',<381>,5:19]], col3=[[@13,42:42='t',<381>,1:42], [@96,295:295='t',<381>,5:27]]}}, grouped_by=[{name=col1, table_ref=F4}, {name=col2, table_ref=t}, {name=col3, table_ref=t}, {name=missing, table_ref=fourth}], def_query0={query_dictionary={col8=[[@38,123:126='col8',<381>,2:38], [@81,256:257='F4',<381>,4:49]], col11=[[@40,129:133='col11',<381>,2:44], [@19,54:55='F4',<381>,1:54]], col6=[[@36,117:120='col6',<381>,2:32], [@65,214:215='F4',<381>,4:7]], col4=[[@34,111:114='col4',<381>,2:26], [@57,190:191='F4',<381>,3:40]], col1=[[@30,100:103='col1',<381>,2:15], [@1,8:9='F4',<381>,1:8], [@49,169:170='F4',<381>,3:19], [@73,235:236='F4',<381>,4:28], [@88,278:279='F4',<381>,5:10]]}, table_dictionary={third={a=[[@28,95:95='a',<381>,2:10]], b=[[@32,106:106='b',<381>,2:21]], col8=[[@38,123:126='col8',<381>,2:38]], col11=[[@40,129:133='col11',<381>,2:44]], col6=[[@36,117:120='col6',<381>,2:32]]}}, interface={col8=[{name=col8, table_ref=third}], col11=[{name=col11, table_ref=third}], col6=[{name=col6, table_ref=third}], col4=[{name=b, table_ref=third}], col1=[{name=a, table_ref=third}]}}, filters=[{name=col1, table_ref=F4}, {name=col2, table_ref=t}, {name=col4, table_ref=F4}, {name=col5, table_ref=t}, {name=col6, table_ref=F4}, {name=col7, table_ref=t}, {name=col8, table_ref=F4}], interface={last=[{name=col1, table_ref=F4}], total_col11=[{name=col11, table_ref=F4}], tcol2=[{name=col2, table_ref=t}], col3=[{name=col3, table_ref=t}]}, table_alias={t=fourth, F4=query0}}}",
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
		Assert.assertEquals("Table Dictionary is wrong", "{third={a=[[@19,65:65='a',<381>,2:10]], b=[[@23,76:76='b',<381>,2:21]], col8=[[@29,93:96='col8',<381>,2:38]], col12=[[@31,99:103='col12',<381>,2:44]], col6=[[@27,87:90='col6',<381>,2:32]]}, fourth={missing=[[@94,289:295='missing',<381>,5:51]], col7=[[@60,194:194='t',<381>,4:17]], col5=[[@52,170:170='t',<381>,3:50]], col2=[[@7,25:25='t',<381>,1:25], [@44,149:149='t',<381>,3:29], [@68,215:215='t',<381>,4:38]], col3=[[@13,42:42='t',<381>,1:42], [@79,248:248='t',<381>,5:10]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={col8=[[@29,93:96='col8',<381>,2:38], [@72,226:227='F4',<381>,4:49]], col12=[[@31,99:103='col12',<381>,2:44], [@89,274:275='F4',<381>,5:36]], col6=[[@27,87:90='col6',<381>,2:32], [@56,184:185='F4',<381>,4:7]], col4=[[@25,81:84='col4',<381>,2:26], [@48,160:161='F4',<381>,3:40]], col1=[[@21,70:73='col1',<381>,2:15], [@1,8:9='F4',<381>,1:8], [@40,139:140='F4',<381>,3:19], [@64,205:206='F4',<381>,4:28], [@84,261:262='F4',<381>,5:23]]}, query1={last=[[@5,19:22='last',<102>,1:19]], tcol2=[[@11,35:39='tcol2',<381>,1:35]], col3=[[@15,44:47='col3',<381>,1:44]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query1={query_dictionary={last=[[@5,19:22='last',<102>,1:19]], tcol2=[[@11,35:39='tcol2',<381>,1:35]], col3=[[@15,44:47='col3',<381>,1:44]]}, table_dictionary={fourth={missing=[[@94,289:295='missing',<381>,5:51]], col7=[[@60,194:194='t',<381>,4:17]], col5=[[@52,170:170='t',<381>,3:50]], col2=[[@7,25:25='t',<381>,1:25], [@44,149:149='t',<381>,3:29], [@68,215:215='t',<381>,4:38]], col3=[[@13,42:42='t',<381>,1:42], [@79,248:248='t',<381>,5:10]]}}, def_query0={query_dictionary={col8=[[@29,93:96='col8',<381>,2:38], [@72,226:227='F4',<381>,4:49]], col12=[[@31,99:103='col12',<381>,2:44], [@89,274:275='F4',<381>,5:36]], col6=[[@27,87:90='col6',<381>,2:32], [@56,184:185='F4',<381>,4:7]], col4=[[@25,81:84='col4',<381>,2:26], [@48,160:161='F4',<381>,3:40]], col1=[[@21,70:73='col1',<381>,2:15], [@1,8:9='F4',<381>,1:8], [@40,139:140='F4',<381>,3:19], [@64,205:206='F4',<381>,4:28], [@84,261:262='F4',<381>,5:23]]}, table_dictionary={third={a=[[@19,65:65='a',<381>,2:10]], b=[[@23,76:76='b',<381>,2:21]], col8=[[@29,93:96='col8',<381>,2:38]], col12=[[@31,99:103='col12',<381>,2:44]], col6=[[@27,87:90='col6',<381>,2:32]]}}, interface={col8=[{name=col8, table_ref=third}], col12=[{name=col12, table_ref=third}], col6=[{name=col6, table_ref=third}], col4=[{name=b, table_ref=third}], col1=[{name=a, table_ref=third}]}}, ordered_by=[{name=col3, table_ref=t}, {name=col1, table_ref=F4}, {name=col12, table_ref=F4}, {name=missing, table_ref=fourth}], filters=[{name=col1, table_ref=F4}, {name=col2, table_ref=t}, {name=col4, table_ref=F4}, {name=col5, table_ref=t}, {name=col6, table_ref=F4}, {name=col7, table_ref=t}, {name=col8, table_ref=F4}], interface={last=[{name=col1, table_ref=F4}], tcol2=[{name=col2, table_ref=t}], col3=[{name=col3, table_ref=t}]}, table_alias={t=fourth, F4=query0}}}",
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
		Assert.assertEquals("Table Dictionary is wrong", "{<Guide>={is_active=[[@7,39:47='is_active',<381>,1:39]], student=[[@15,66:72='student',<381>,1:66]], rank=[[@11,54:57='rank',<128>,1:54]], category=[[@5,29:36='category',<381>,1:29]], nk=[[@9,50:51='nk',<381>,1:50]], desc=[[@13,60:63='desc',<77>,1:60]]}, <NAV>={is_active=[[@29,166:174='is_active',<381>,3:37]], student=[[@37,193:199='student',<381>,3:64]], rank=[[@33,181:184='rank',<128>,3:52]], category=[[@27,156:163='category',<381>,3:27]], nk=[[@31,177:178='nk',<381>,3:48]], desc=[[@35,187:190='desc',<77>,3:58]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={app_name=[[@3,18:25='app_name',<381>,1:18]], is_active=[[@7,39:47='is_active',<381>,1:39]], student=[[@15,66:72='student',<381>,1:66]], rank=[[@11,54:57='rank',<128>,1:54]], category=[[@5,29:36='category',<381>,1:29]], nk=[[@9,50:51='nk',<381>,1:50]], desc=[[@13,60:63='desc',<77>,1:60]]}, query1={app_name=[[@25,146:153='app_name',<381>,3:17]], is_active=[[@29,166:174='is_active',<381>,3:37]], student=[[@37,193:199='student',<381>,3:64]], rank=[[@33,181:184='rank',<128>,3:52]], category=[[@27,156:163='category',<381>,3:27]], nk=[[@31,177:178='nk',<381>,3:48]], desc=[[@35,187:190='desc',<77>,3:58]]}}",
						extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{union2={query0={query_dictionary={app_name=[[@3,18:25='app_name',<381>,1:18]], is_active=[[@7,39:47='is_active',<381>,1:39]], student=[[@15,66:72='student',<381>,1:66]], rank=[[@11,54:57='rank',<128>,1:54]], category=[[@5,29:36='category',<381>,1:29]], nk=[[@9,50:51='nk',<381>,1:50]], desc=[[@13,60:63='desc',<77>,1:60]]}, table_dictionary={<Guide>={is_active=[[@7,39:47='is_active',<381>,1:39]], student=[[@15,66:72='student',<381>,1:66]], rank=[[@11,54:57='rank',<128>,1:54]], category=[[@5,29:36='category',<381>,1:29]], nk=[[@9,50:51='nk',<381>,1:50]], desc=[[@13,60:63='desc',<77>,1:60]]}}, interface={app_name=[], is_active=[{name=is_active, table_ref=<Guide>}], student=[{name=student, table_ref=<Guide>}], rank=[{name=rank, table_ref=<Guide>}], category=[{name=category, table_ref=<Guide>}], nk=[{name=nk, table_ref=<Guide>}], desc=[{name=desc, table_ref=<Guide>}]}, table_alias={Guide_Student_Conditions=<Guide>}}, interface={app_name=[], is_active=[{name=is_active, table_ref=<Guide>}, {name=is_active, table_ref=<NAV>}], student=[{name=student, table_ref=<Guide>}, {name=student, table_ref=<NAV>}], rank=[{name=rank, table_ref=<Guide>}, {name=rank, table_ref=<NAV>}], category=[{name=category, table_ref=<Guide>}, {name=category, table_ref=<NAV>}], nk=[{name=nk, table_ref=<Guide>}, {name=nk, table_ref=<NAV>}], desc=[{name=desc, table_ref=<Guide>}, {name=desc, table_ref=<NAV>}]}, query1={query_dictionary={app_name=[[@25,146:153='app_name',<381>,3:17]], is_active=[[@29,166:174='is_active',<381>,3:37]], student=[[@37,193:199='student',<381>,3:64]], rank=[[@33,181:184='rank',<128>,3:52]], category=[[@27,156:163='category',<381>,3:27]], nk=[[@31,177:178='nk',<381>,3:48]], desc=[[@35,187:190='desc',<77>,3:58]]}, table_dictionary={<NAV>={is_active=[[@29,166:174='is_active',<381>,3:37]], student=[[@37,193:199='student',<381>,3:64]], rank=[[@33,181:184='rank',<128>,3:52]], category=[[@27,156:163='category',<381>,3:27]], nk=[[@31,177:178='nk',<381>,3:48]], desc=[[@35,187:190='desc',<77>,3:58]]}}, interface={app_name=[], is_active=[{name=is_active, table_ref=<NAV>}], student=[{name=student, table_ref=<NAV>}], rank=[{name=rank, table_ref=<NAV>}], category=[{name=category, table_ref=<NAV>}], nk=[{name=nk, table_ref=<NAV>}], desc=[{name=desc, table_ref=<NAV>}]}, table_alias={Nav_Student_Conditions=<NAV>}}}}",
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
		Assert.assertEquals("Table Dictionary is wrong", "{<Guide>={is_active=[[@7,39:47='is_active',<381>,1:39]], student=[[@15,66:72='student',<381>,1:66]], rank=[[@11,54:57='rank',<128>,1:54]], category=[[@5,29:36='category',<381>,1:29]], nk=[[@9,50:51='nk',<381>,1:50]], desc=[[@13,60:63='desc',<77>,1:60]]}, <NAV>={is_active=[[@29,166:174='is_active',<381>,3:37]], student=[[@37,193:199='student',<381>,3:64]], rank=[[@33,181:184='rank',<128>,3:52]], category=[[@27,156:163='category',<381>,3:27]], nk=[[@31,177:178='nk',<381>,3:48]], desc=[[@35,187:190='desc',<77>,3:58]]}, <IMPL>={is_active=[[@51,290:298='is_active',<381>,5:38]], student=[[@59,317:323='student',<381>,5:65]], rank=[[@55,305:308='rank',<128>,5:53]], category=[[@49,280:287='category',<381>,5:28]], nk=[[@53,301:302='nk',<381>,5:49]], desc=[[@57,311:314='desc',<77>,5:59]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={app_name=[[@3,18:25='app_name',<381>,1:18]], is_active=[[@7,39:47='is_active',<381>,1:39]], student=[[@15,66:72='student',<381>,1:66]], rank=[[@11,54:57='rank',<128>,1:54]], category=[[@5,29:36='category',<381>,1:29]], nk=[[@9,50:51='nk',<381>,1:50]], desc=[[@13,60:63='desc',<77>,1:60]]}, query1={app_name=[[@25,146:153='app_name',<381>,3:17]], is_active=[[@29,166:174='is_active',<381>,3:37]], student=[[@37,193:199='student',<381>,3:64]], rank=[[@33,181:184='rank',<128>,3:52]], category=[[@27,156:163='category',<381>,3:27]], nk=[[@31,177:178='nk',<381>,3:48]], desc=[[@35,187:190='desc',<77>,3:58]]}, query2={app_name=[[@47,270:277='app_name',<381>,5:18]], is_active=[[@51,290:298='is_active',<381>,5:38]], student=[[@59,317:323='student',<381>,5:65]], rank=[[@55,305:308='rank',<128>,5:53]], category=[[@49,280:287='category',<381>,5:28]], nk=[[@53,301:302='nk',<381>,5:49]], desc=[[@57,311:314='desc',<77>,5:59]]}}",
						extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{union3={query0={query_dictionary={app_name=[[@3,18:25='app_name',<381>,1:18]], is_active=[[@7,39:47='is_active',<381>,1:39]], student=[[@15,66:72='student',<381>,1:66]], rank=[[@11,54:57='rank',<128>,1:54]], category=[[@5,29:36='category',<381>,1:29]], nk=[[@9,50:51='nk',<381>,1:50]], desc=[[@13,60:63='desc',<77>,1:60]]}, table_dictionary={<Guide>={is_active=[[@7,39:47='is_active',<381>,1:39]], student=[[@15,66:72='student',<381>,1:66]], rank=[[@11,54:57='rank',<128>,1:54]], category=[[@5,29:36='category',<381>,1:29]], nk=[[@9,50:51='nk',<381>,1:50]], desc=[[@13,60:63='desc',<77>,1:60]]}}, interface={app_name=[], is_active=[{name=is_active, table_ref=<Guide>}], student=[{name=student, table_ref=<Guide>}], rank=[{name=rank, table_ref=<Guide>}], category=[{name=category, table_ref=<Guide>}], nk=[{name=nk, table_ref=<Guide>}], desc=[{name=desc, table_ref=<Guide>}]}, table_alias={Guide_Student_Conditions=<Guide>}}, interface={app_name=[], is_active=[{name=is_active, table_ref=<Guide>}, {name=is_active, table_ref=<NAV>}, {name=is_active, table_ref=<IMPL>}], student=[{name=student, table_ref=<Guide>}, {name=student, table_ref=<NAV>}, {name=student, table_ref=<IMPL>}], rank=[{name=rank, table_ref=<Guide>}, {name=rank, table_ref=<NAV>}, {name=rank, table_ref=<IMPL>}], category=[{name=category, table_ref=<Guide>}, {name=category, table_ref=<NAV>}, {name=category, table_ref=<IMPL>}], nk=[{name=nk, table_ref=<Guide>}, {name=nk, table_ref=<NAV>}, {name=nk, table_ref=<IMPL>}], desc=[{name=desc, table_ref=<Guide>}, {name=desc, table_ref=<NAV>}, {name=desc, table_ref=<IMPL>}]}, query1={query_dictionary={app_name=[[@25,146:153='app_name',<381>,3:17]], is_active=[[@29,166:174='is_active',<381>,3:37]], student=[[@37,193:199='student',<381>,3:64]], rank=[[@33,181:184='rank',<128>,3:52]], category=[[@27,156:163='category',<381>,3:27]], nk=[[@31,177:178='nk',<381>,3:48]], desc=[[@35,187:190='desc',<77>,3:58]]}, table_dictionary={<NAV>={is_active=[[@29,166:174='is_active',<381>,3:37]], student=[[@37,193:199='student',<381>,3:64]], rank=[[@33,181:184='rank',<128>,3:52]], category=[[@27,156:163='category',<381>,3:27]], nk=[[@31,177:178='nk',<381>,3:48]], desc=[[@35,187:190='desc',<77>,3:58]]}}, interface={app_name=[], is_active=[{name=is_active, table_ref=<NAV>}], student=[{name=student, table_ref=<NAV>}], rank=[{name=rank, table_ref=<NAV>}], category=[{name=category, table_ref=<NAV>}], nk=[{name=nk, table_ref=<NAV>}], desc=[{name=desc, table_ref=<NAV>}]}, table_alias={Nav_Student_Conditions=<NAV>}}, query2={query_dictionary={app_name=[[@47,270:277='app_name',<381>,5:18]], is_active=[[@51,290:298='is_active',<381>,5:38]], student=[[@59,317:323='student',<381>,5:65]], rank=[[@55,305:308='rank',<128>,5:53]], category=[[@49,280:287='category',<381>,5:28]], nk=[[@53,301:302='nk',<381>,5:49]], desc=[[@57,311:314='desc',<77>,5:59]]}, table_dictionary={<IMPL>={is_active=[[@51,290:298='is_active',<381>,5:38]], student=[[@59,317:323='student',<381>,5:65]], rank=[[@55,305:308='rank',<128>,5:53]], category=[[@49,280:287='category',<381>,5:28]], nk=[[@53,301:302='nk',<381>,5:49]], desc=[[@57,311:314='desc',<77>,5:59]]}}, interface={app_name=[], is_active=[{name=is_active, table_ref=<IMPL>}], student=[{name=student, table_ref=<IMPL>}], rank=[{name=rank, table_ref=<IMPL>}], category=[{name=category, table_ref=<IMPL>}], nk=[{name=nk, table_ref=<IMPL>}], desc=[{name=desc, table_ref=<IMPL>}]}, table_alias={IMPL_Student_Conditions=<IMPL>}}}}",
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
		Assert.assertEquals("Table Dictionary is wrong", "{<Guide>={is_active=[[@7,39:47='is_active',<381>,1:39]], student=[[@15,66:72='student',<381>,1:66]], rank=[[@11,54:57='rank',<128>,1:54]], category=[[@5,29:36='category',<381>,1:29]], nk=[[@9,50:51='nk',<381>,1:50]], desc=[[@13,60:63='desc',<77>,1:60]]}, <NAV>={is_active=[[@28,166:174='is_active',<381>,3:37]], student=[[@36,193:199='student',<381>,3:64]], rank=[[@32,181:184='rank',<128>,3:52]], category=[[@26,156:163='category',<381>,3:27]], nk=[[@30,177:178='nk',<381>,3:48]], desc=[[@34,187:190='desc',<77>,3:58]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={app_name=[[@3,18:25='app_name',<381>,1:18]], is_active=[[@7,39:47='is_active',<381>,1:39]], student=[[@15,66:72='student',<381>,1:66]], rank=[[@11,54:57='rank',<128>,1:54]], category=[[@5,29:36='category',<381>,1:29]], nk=[[@9,50:51='nk',<381>,1:50]], desc=[[@13,60:63='desc',<77>,1:60]]}, query1={app_name=[[@24,146:153='app_name',<381>,3:17]], is_active=[[@28,166:174='is_active',<381>,3:37]], student=[[@36,193:199='student',<381>,3:64]], rank=[[@32,181:184='rank',<128>,3:52]], category=[[@26,156:163='category',<381>,3:27]], nk=[[@30,177:178='nk',<381>,3:48]], desc=[[@34,187:190='desc',<77>,3:58]]}}",
						extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{intersect2={query0={query_dictionary={app_name=[[@3,18:25='app_name',<381>,1:18]], is_active=[[@7,39:47='is_active',<381>,1:39]], student=[[@15,66:72='student',<381>,1:66]], rank=[[@11,54:57='rank',<128>,1:54]], category=[[@5,29:36='category',<381>,1:29]], nk=[[@9,50:51='nk',<381>,1:50]], desc=[[@13,60:63='desc',<77>,1:60]]}, table_dictionary={<Guide>={is_active=[[@7,39:47='is_active',<381>,1:39]], student=[[@15,66:72='student',<381>,1:66]], rank=[[@11,54:57='rank',<128>,1:54]], category=[[@5,29:36='category',<381>,1:29]], nk=[[@9,50:51='nk',<381>,1:50]], desc=[[@13,60:63='desc',<77>,1:60]]}}, interface={app_name=[], is_active=[{name=is_active, table_ref=<Guide>}], student=[{name=student, table_ref=<Guide>}], rank=[{name=rank, table_ref=<Guide>}], category=[{name=category, table_ref=<Guide>}], nk=[{name=nk, table_ref=<Guide>}], desc=[{name=desc, table_ref=<Guide>}]}, table_alias={Guide_Student_Conditions=<Guide>}}, interface={app_name=[], is_active=[{name=is_active, table_ref=<Guide>}, {name=is_active, table_ref=<NAV>}], student=[{name=student, table_ref=<Guide>}, {name=student, table_ref=<NAV>}], rank=[{name=rank, table_ref=<Guide>}, {name=rank, table_ref=<NAV>}], category=[{name=category, table_ref=<Guide>}, {name=category, table_ref=<NAV>}], nk=[{name=nk, table_ref=<Guide>}, {name=nk, table_ref=<NAV>}], desc=[{name=desc, table_ref=<Guide>}, {name=desc, table_ref=<NAV>}]}, query1={query_dictionary={app_name=[[@24,146:153='app_name',<381>,3:17]], is_active=[[@28,166:174='is_active',<381>,3:37]], student=[[@36,193:199='student',<381>,3:64]], rank=[[@32,181:184='rank',<128>,3:52]], category=[[@26,156:163='category',<381>,3:27]], nk=[[@30,177:178='nk',<381>,3:48]], desc=[[@34,187:190='desc',<77>,3:58]]}, table_dictionary={<NAV>={is_active=[[@28,166:174='is_active',<381>,3:37]], student=[[@36,193:199='student',<381>,3:64]], rank=[[@32,181:184='rank',<128>,3:52]], category=[[@26,156:163='category',<381>,3:27]], nk=[[@30,177:178='nk',<381>,3:48]], desc=[[@34,187:190='desc',<77>,3:58]]}}, interface={app_name=[], is_active=[{name=is_active, table_ref=<NAV>}], student=[{name=student, table_ref=<NAV>}], rank=[{name=rank, table_ref=<NAV>}], category=[{name=category, table_ref=<NAV>}], nk=[{name=nk, table_ref=<NAV>}], desc=[{name=desc, table_ref=<NAV>}]}, table_alias={Nav_Student_Conditions=<NAV>}}}}",
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
		Assert.assertEquals("Table Dictionary is wrong", "{<Guide>={student=[[@5,19:25='student',<381>,1:19]], rank=[[@1,7:10='rank',<128>,1:7]], desc=[[@3,13:16='desc',<77>,1:13]]}, <NAV>={is_active=[[@18,115:123='is_active',<381>,3:37]], category=[[@16,105:112='category',<381>,3:27]], nk=[[@20,126:127='nk',<381>,3:48]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={student=[[@5,19:25='student',<381>,1:19]], rank=[[@1,7:10='rank',<128>,1:7]], desc=[[@3,13:16='desc',<77>,1:13]]}, query1={app_name=[[@14,95:102='app_name',<381>,3:17]], is_active=[[@18,115:123='is_active',<381>,3:37]], category=[[@16,105:112='category',<381>,3:27]], nk=[[@20,126:127='nk',<381>,3:48]]}}",
						extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{union2={query0={query_dictionary={student=[[@5,19:25='student',<381>,1:19]], rank=[[@1,7:10='rank',<128>,1:7]], desc=[[@3,13:16='desc',<77>,1:13]]}, table_dictionary={<Guide>={student=[[@5,19:25='student',<381>,1:19]], rank=[[@1,7:10='rank',<128>,1:7]], desc=[[@3,13:16='desc',<77>,1:13]]}}, interface={student=[{name=student, table_ref=<Guide>}], rank=[{name=rank, table_ref=<Guide>}], desc=[{name=desc, table_ref=<Guide>}]}, table_alias={Guide_Student_Conditions=<Guide>}}, interface={student=[{name=student, table_ref=<Guide>}, {name=is_active, table_ref=<NAV>}], rank=[{name=rank, table_ref=<Guide>}], desc=[{name=desc, table_ref=<Guide>}, {name=category, table_ref=<NAV>}]}, query1={query_dictionary={app_name=[[@14,95:102='app_name',<381>,3:17]], is_active=[[@18,115:123='is_active',<381>,3:37]], category=[[@16,105:112='category',<381>,3:27]], nk=[[@20,126:127='nk',<381>,3:48]]}, table_dictionary={<NAV>={is_active=[[@18,115:123='is_active',<381>,3:37]], category=[[@16,105:112='category',<381>,3:27]], nk=[[@20,126:127='nk',<381>,3:48]]}}, interface={app_name=[], is_active=[{name=is_active, table_ref=<NAV>}], category=[{name=category, table_ref=<NAV>}], nk=[{name=nk, table_ref=<NAV>}]}, table_alias={Nav_Student_Conditions=<NAV>}}}}",
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
		Assert.assertEquals("Table Dictionary is wrong", "{<Guide>={student=[[@5,19:25='student',<381>,1:19]], rank=[[@1,7:10='rank',<128>,1:7]], desc=[[@3,13:16='desc',<77>,1:13]]}, <NAV>={is_active=[[@18,119:127='is_active',<381>,3:37]], category=[[@16,109:116='category',<381>,3:27]], nk=[[@20,130:131='nk',<381>,3:48]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={student=[[@5,19:25='student',<381>,1:19]], rank=[[@1,7:10='rank',<128>,1:7]], desc=[[@3,13:16='desc',<77>,1:13]]}, query1={app_name=[[@14,99:106='app_name',<381>,3:17]], is_active=[[@18,119:127='is_active',<381>,3:37]], category=[[@16,109:116='category',<381>,3:27]], nk=[[@20,130:131='nk',<381>,3:48]]}}",
						extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{intersect2={query0={query_dictionary={student=[[@5,19:25='student',<381>,1:19]], rank=[[@1,7:10='rank',<128>,1:7]], desc=[[@3,13:16='desc',<77>,1:13]]}, table_dictionary={<Guide>={student=[[@5,19:25='student',<381>,1:19]], rank=[[@1,7:10='rank',<128>,1:7]], desc=[[@3,13:16='desc',<77>,1:13]]}}, interface={student=[{name=student, table_ref=<Guide>}], rank=[{name=rank, table_ref=<Guide>}], desc=[{name=desc, table_ref=<Guide>}]}, table_alias={Guide_Student_Conditions=<Guide>}}, interface={student=[{name=student, table_ref=<Guide>}, {name=is_active, table_ref=<NAV>}], rank=[{name=rank, table_ref=<Guide>}], desc=[{name=desc, table_ref=<Guide>}, {name=category, table_ref=<NAV>}]}, query1={query_dictionary={app_name=[[@14,99:106='app_name',<381>,3:17]], is_active=[[@18,119:127='is_active',<381>,3:37]], category=[[@16,109:116='category',<381>,3:27]], nk=[[@20,130:131='nk',<381>,3:48]]}, table_dictionary={<NAV>={is_active=[[@18,119:127='is_active',<381>,3:37]], category=[[@16,109:116='category',<381>,3:27]], nk=[[@20,130:131='nk',<381>,3:48]]}}, interface={app_name=[], is_active=[{name=is_active, table_ref=<NAV>}], category=[{name=category, table_ref=<NAV>}], nk=[{name=nk, table_ref=<NAV>}]}, table_alias={Nav_Student_Conditions=<NAV>}}}}",
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
		Assert.assertEquals("Table Dictionary is wrong", "{<Guide>={is_active=[[@7,39:47='is_active',<381>,1:39]], student=[[@15,66:72='student',<381>,1:66]], rank=[[@11,54:57='rank',<128>,1:54]], category=[[@5,29:36='category',<381>,1:29]], nk=[[@9,50:51='nk',<381>,1:50]], desc=[[@13,60:63='desc',<77>,1:60]]}, <NAV>={is_active=[[@28,166:174='is_active',<381>,3:37]], student=[[@36,193:199='student',<381>,3:64]], rank=[[@32,181:184='rank',<128>,3:52]], category=[[@26,156:163='category',<381>,3:27]], nk=[[@30,177:178='nk',<381>,3:48]], desc=[[@34,187:190='desc',<77>,3:58]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={app_name=[[@3,18:25='app_name',<381>,1:18]], is_active=[[@7,39:47='is_active',<381>,1:39]], student=[[@15,66:72='student',<381>,1:66]], rank=[[@11,54:57='rank',<128>,1:54]], category=[[@5,29:36='category',<381>,1:29]], nk=[[@9,50:51='nk',<381>,1:50]], desc=[[@13,60:63='desc',<77>,1:60]]}, query1={app_name=[[@24,146:153='app_name',<381>,3:17]], is_active=[[@28,166:174='is_active',<381>,3:37]], student=[[@36,193:199='student',<381>,3:64]], rank=[[@32,181:184='rank',<128>,3:52]], category=[[@26,156:163='category',<381>,3:27]], nk=[[@30,177:178='nk',<381>,3:48]], desc=[[@34,187:190='desc',<77>,3:58]]}}",
						extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{intersect2={query0={query_dictionary={app_name=[[@3,18:25='app_name',<381>,1:18]], is_active=[[@7,39:47='is_active',<381>,1:39]], student=[[@15,66:72='student',<381>,1:66]], rank=[[@11,54:57='rank',<128>,1:54]], category=[[@5,29:36='category',<381>,1:29]], nk=[[@9,50:51='nk',<381>,1:50]], desc=[[@13,60:63='desc',<77>,1:60]]}, table_dictionary={<Guide>={is_active=[[@7,39:47='is_active',<381>,1:39]], student=[[@15,66:72='student',<381>,1:66]], rank=[[@11,54:57='rank',<128>,1:54]], category=[[@5,29:36='category',<381>,1:29]], nk=[[@9,50:51='nk',<381>,1:50]], desc=[[@13,60:63='desc',<77>,1:60]]}}, interface={app_name=[], is_active=[{name=is_active, table_ref=<Guide>}], student=[{name=student, table_ref=<Guide>}], rank=[{name=rank, table_ref=<Guide>}], category=[{name=category, table_ref=<Guide>}], nk=[{name=nk, table_ref=<Guide>}], desc=[{name=desc, table_ref=<Guide>}]}, table_alias={Guide_Student_Conditions=<Guide>}}, interface={app_name=[], is_active=[{name=is_active, table_ref=<Guide>}, {name=is_active, table_ref=<NAV>}], student=[{name=student, table_ref=<Guide>}, {name=student, table_ref=<NAV>}], rank=[{name=rank, table_ref=<Guide>}, {name=rank, table_ref=<NAV>}], category=[{name=category, table_ref=<Guide>}, {name=category, table_ref=<NAV>}], nk=[{name=nk, table_ref=<Guide>}, {name=nk, table_ref=<NAV>}], desc=[{name=desc, table_ref=<Guide>}, {name=desc, table_ref=<NAV>}]}, query1={query_dictionary={app_name=[[@24,146:153='app_name',<381>,3:17]], is_active=[[@28,166:174='is_active',<381>,3:37]], student=[[@36,193:199='student',<381>,3:64]], rank=[[@32,181:184='rank',<128>,3:52]], category=[[@26,156:163='category',<381>,3:27]], nk=[[@30,177:178='nk',<381>,3:48]], desc=[[@34,187:190='desc',<77>,3:58]]}, table_dictionary={<NAV>={is_active=[[@28,166:174='is_active',<381>,3:37]], student=[[@36,193:199='student',<381>,3:64]], rank=[[@32,181:184='rank',<128>,3:52]], category=[[@26,156:163='category',<381>,3:27]], nk=[[@30,177:178='nk',<381>,3:48]], desc=[[@34,187:190='desc',<77>,3:58]]}}, interface={app_name=[], is_active=[{name=is_active, table_ref=<NAV>}], student=[{name=student, table_ref=<NAV>}], rank=[{name=rank, table_ref=<NAV>}], category=[{name=category, table_ref=<NAV>}], nk=[{name=nk, table_ref=<NAV>}], desc=[{name=desc, table_ref=<NAV>}]}, table_alias={Nav_Student_Conditions=<NAV>}}}}",
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
		Assert.assertEquals("Table Dictionary is wrong", "{sixth={fourth=[[@11,70:75='fourth',<381>,1:70]]}, third={first=[[@1,8:12='first',<88>,1:8]]}, eighth={seventh=[[@16,102:108='seventh',<381>,1:102]]}, fifth={third=[[@6,39:43='third',<381>,1:39]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={first=[[@1,8:12='first',<88>,1:8]]}, query1={third=[[@6,39:43='third',<381>,1:39]]}, query2={fourth=[[@11,70:75='fourth',<381>,1:70]]}, query3={seventh=[[@16,102:108='seventh',<381>,1:102]]}}",
						extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{union4={query0={query_dictionary={first=[[@1,8:12='first',<88>,1:8]]}, table_dictionary={third={first=[[@1,8:12='first',<88>,1:8]]}}, interface={first=[{name=first, table_ref=third}]}}, interface={first=[{name=first, table_ref=third}, {name=third, table_ref=fifth}, {name=fourth, table_ref=sixth}, {name=seventh, table_ref=eighth}]}, query1={query_dictionary={third=[[@6,39:43='third',<381>,1:39]]}, table_dictionary={fifth={third=[[@6,39:43='third',<381>,1:39]]}}, interface={third=[{name=third, table_ref=fifth}]}}, query2={query_dictionary={fourth=[[@11,70:75='fourth',<381>,1:70]]}, table_dictionary={sixth={fourth=[[@11,70:75='fourth',<381>,1:70]]}}, interface={fourth=[{name=fourth, table_ref=sixth}]}}, query3={query_dictionary={seventh=[[@16,102:108='seventh',<381>,1:102]]}, table_dictionary={eighth={seventh=[[@16,102:108='seventh',<381>,1:102]]}}, interface={seventh=[{name=seventh, table_ref=eighth}]}}}}",
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
		Assert.assertEquals("Table Dictionary is wrong", "{sixth={fourth=[[@11,78:83='fourth',<381>,1:78]]}, third={first=[[@1,8:12='first',<88>,1:8]]}, eighth={seventh=[[@16,114:120='seventh',<381>,1:114]]}, fifth={third=[[@6,43:47='third',<381>,1:43]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={first=[[@1,8:12='first',<88>,1:8]]}, query1={third=[[@6,43:47='third',<381>,1:43]]}, query2={fourth=[[@11,78:83='fourth',<381>,1:78]]}, query3={seventh=[[@16,114:120='seventh',<381>,1:114]]}}",
						extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{intersect4={query0={query_dictionary={first=[[@1,8:12='first',<88>,1:8]]}, table_dictionary={third={first=[[@1,8:12='first',<88>,1:8]]}}, interface={first=[{name=first, table_ref=third}]}}, interface={first=[{name=first, table_ref=third}, {name=third, table_ref=fifth}, {name=fourth, table_ref=sixth}, {name=seventh, table_ref=eighth}]}, query1={query_dictionary={third=[[@6,43:47='third',<381>,1:43]]}, table_dictionary={fifth={third=[[@6,43:47='third',<381>,1:43]]}}, interface={third=[{name=third, table_ref=fifth}]}}, query2={query_dictionary={fourth=[[@11,78:83='fourth',<381>,1:78]]}, table_dictionary={sixth={fourth=[[@11,78:83='fourth',<381>,1:78]]}}, interface={fourth=[{name=fourth, table_ref=sixth}]}}, query3={query_dictionary={seventh=[[@16,114:120='seventh',<381>,1:114]]}, table_dictionary={eighth={seventh=[[@16,114:120='seventh',<381>,1:114]]}}, interface={seventh=[{name=seventh, table_ref=eighth}]}}}}",
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
		Assert.assertEquals("Table Dictionary is wrong", "{sixth={fourth=[[@20,107:112='fourth',<381>,4:14]]}, third={item=[[@1,8:11='item',<381>,1:8]]}, ninth={x=[[@13,72:72='x',<381>,3:9]]}, eighth={seventh=[[@25,140:146='seventh',<381>,5:14]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query4={seventh=[[@25,140:146='seventh',<381>,5:14]]}, query0={first=[[@3,16:20='first',<88>,1:16]]}, query1={x=[[@13,72:72='x',<381>,3:9]]}, query2={second=[[@9,50:55='second',<135>,2:16]]}, query3={fourth=[[@20,107:112='fourth',<381>,4:14]]}}",
						extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{union5={query4={query_dictionary={seventh=[[@25,140:146='seventh',<381>,5:14]]}, table_dictionary={eighth={seventh=[[@25,140:146='seventh',<381>,5:14]]}}, interface={seventh=[{name=seventh, table_ref=eighth}]}}, query0={query_dictionary={first=[[@3,16:20='first',<88>,1:16]]}, table_dictionary={third={item=[[@1,8:11='item',<381>,1:8]]}}, interface={first=[{name=item, table_ref=third}]}}, interface={first=[{name=item, table_ref=third}, {name=x, table_ref=query1}, {name=fourth, table_ref=sixth}, {name=seventh, table_ref=eighth}]}, query2={query_dictionary={second=[[@9,50:55='second',<135>,2:16]]}, table_dictionary={}, def_query1={query_dictionary={x=[[@13,72:72='x',<381>,3:9]]}, table_dictionary={ninth={x=[[@13,72:72='x',<381>,3:9]]}}, interface={x=[{name=x, table_ref=ninth}]}}, interface={second=[{name=x, table_ref=query1}]}, table_alias={fifth=query1}}, query3={query_dictionary={fourth=[[@20,107:112='fourth',<381>,4:14]]}, table_dictionary={sixth={fourth=[[@20,107:112='fourth',<381>,4:14]]}}, interface={fourth=[{name=fourth, table_ref=sixth}]}}}}",
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
		Assert.assertEquals("Table Dictionary is wrong", "{sixth={z=[[@13,83:83='z',<381>,1:83]]}, third={x=[[@1,8:8='x',<381>,1:8]]}, eighth={omega=[[@19,121:125='omega',<381>,1:121]]}, fifth={y=[[@7,45:45='y',<381>,1:45]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={first=[[@2,10:14='first',<88>,1:10]]}, query1={second=[[@8,47:52='second',<135>,1:47]]}, query2={fourth=[[@14,85:90='fourth',<381>,1:85]]}, query3={seventh=[[@20,127:133='seventh',<381>,1:127]]}}",
						extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{intersect4={query0={query_dictionary={first=[[@2,10:14='first',<88>,1:10]]}, table_dictionary={third={x=[[@1,8:8='x',<381>,1:8]]}}, interface={first=[{name=x, table_ref=third}]}}, interface={first=[{name=x, table_ref=third}, {name=y, table_ref=fifth}, {name=z, table_ref=sixth}, {name=omega, table_ref=eighth}]}, query1={query_dictionary={second=[[@8,47:52='second',<135>,1:47]]}, table_dictionary={fifth={y=[[@7,45:45='y',<381>,1:45]]}}, interface={second=[{name=y, table_ref=fifth}]}}, query2={query_dictionary={fourth=[[@14,85:90='fourth',<381>,1:85]]}, table_dictionary={sixth={z=[[@13,83:83='z',<381>,1:83]]}}, interface={fourth=[{name=z, table_ref=sixth}]}}, query3={query_dictionary={seventh=[[@20,127:133='seventh',<381>,1:127]]}, table_dictionary={eighth={omega=[[@19,121:125='omega',<381>,1:121]]}}, interface={seventh=[{name=omega, table_ref=eighth}]}}}}",
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
		Assert.assertEquals("Table Dictionary is wrong", "{sixth={fourth=[[@11,74:79='fourth',<381>,1:74]]}, third={first=[[@1,8:12='first',<88>,1:8]]}, eighth={seventh=[[@16,106:112='seventh',<381>,1:106]]}, fifth={third=[[@6,39:43='third',<381>,1:39]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query4={seventh=[[@16,106:112='seventh',<381>,1:106]]}, query0={first=[[@1,8:12='first',<88>,1:8]]}, query1={third=[[@6,39:43='third',<381>,1:39]]}, query3={fourth=[[@11,74:79='fourth',<381>,1:74]]}}",
						extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{intersect6={union5={query4={query_dictionary={seventh=[[@16,106:112='seventh',<381>,1:106]]}, table_dictionary={eighth={seventh=[[@16,106:112='seventh',<381>,1:106]]}}, interface={seventh=[{name=seventh, table_ref=eighth}]}}, interface={fourth=[{name=fourth, table_ref=sixth}, {name=seventh, table_ref=eighth}]}, query3={query_dictionary={fourth=[[@11,74:79='fourth',<381>,1:74]]}, table_dictionary={sixth={fourth=[[@11,74:79='fourth',<381>,1:74]]}}, interface={fourth=[{name=fourth, table_ref=sixth}]}}}, union2={query0={query_dictionary={first=[[@1,8:12='first',<88>,1:8]]}, table_dictionary={third={first=[[@1,8:12='first',<88>,1:8]]}}, interface={first=[{name=first, table_ref=third}]}}, interface={first=[{name=first, table_ref=third}, {name=third, table_ref=fifth}]}, query1={query_dictionary={third=[[@6,39:43='third',<381>,1:39]]}, table_dictionary={fifth={third=[[@6,39:43='third',<381>,1:39]]}}, interface={third=[{name=third, table_ref=fifth}]}}}, interface={first=[{name=first, table_ref=third}, {name=third, table_ref=fifth}, {name=fourth, table_ref=sixth}, {name=seventh, table_ref=eighth}]}}}",
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
		Assert.assertEquals("Table Dictionary is wrong", "{sixth={fourth=[[@10,65:70='fourth',<381>,1:65]]}, eighth={seventh=[[@17,102:108='seventh',<381>,1:102]]}, fifth={third=[[@5,30:34='third',<381>,1:30]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query4={seventh=[[@17,102:108='seventh',<381>,1:102]]}, query0={third=[[@5,30:34='third',<381>,1:30]]}, query1={fourth=[[@10,65:70='fourth',<381>,1:65]]}, query3={first=[[@1,8:12='first',<88>,1:8]]}}",
						extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{union5={query4={query_dictionary={seventh=[[@17,102:108='seventh',<381>,1:102]]}, table_dictionary={eighth={seventh=[[@17,102:108='seventh',<381>,1:102]]}}, interface={seventh=[{name=seventh, table_ref=eighth}]}}, interface={first=[{name=first, table_ref=null}, {name=seventh, table_ref=eighth}]}, query3={query_dictionary={first=[[@1,8:12='first',<88>,1:8]]}, table_dictionary={}, def_intersect2={query0={query_dictionary={third=[[@5,30:34='third',<381>,1:30]]}, table_dictionary={fifth={third=[[@5,30:34='third',<381>,1:30]]}}, interface={third=[{name=third, table_ref=fifth}]}}, interface={third=[{name=third, table_ref=fifth}, {name=fourth, table_ref=sixth}]}, query1={query_dictionary={fourth=[[@10,65:70='fourth',<381>,1:65]]}, table_dictionary={sixth={fourth=[[@10,65:70='fourth',<381>,1:65]]}}, interface={fourth=[{name=fourth, table_ref=sixth}]}}}, interface={first=[{name=first, table_ref=null}]}, table_alias={aa=intersect2}}}}",
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
		Assert.assertEquals("Table Dictionary is wrong", "{sixth={fourth=[[@10,65:70='fourth',<381>,1:65]]}, eighth={seventh=[[@16,99:105='seventh',<381>,1:99]]}, fifth={third=[[@5,30:34='third',<381>,1:30]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query4={seventh=[[@16,99:105='seventh',<381>,1:99]]}, query0={third=[[@5,30:34='third',<381>,1:30]]}, query1={fourth=[[@10,65:70='fourth',<381>,1:65]]}, query3={first=[[@1,8:12='first',<88>,1:8]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{union5={query4={query_dictionary={seventh=[[@16,99:105='seventh',<381>,1:99]]}, table_dictionary={eighth={seventh=[[@16,99:105='seventh',<381>,1:99]]}}, interface={seventh=[{name=seventh, table_ref=eighth}]}}, interface={first=[{name=first, table_ref=null}, {name=seventh, table_ref=eighth}]}, query3={intersect2={query0={query_dictionary={third=[[@5,30:34='third',<381>,1:30]]}, table_dictionary={fifth={third=[[@5,30:34='third',<381>,1:30]]}}, interface={third=[{name=third, table_ref=fifth}]}}, interface={third=[{name=third, table_ref=fifth}, {name=fourth, table_ref=sixth}]}, query1={query_dictionary={fourth=[[@10,65:70='fourth',<381>,1:65]]}, table_dictionary={sixth={fourth=[[@10,65:70='fourth',<381>,1:65]]}}, interface={fourth=[{name=fourth, table_ref=sixth}]}}}, query_dictionary={first=[[@1,8:12='first',<88>,1:8]]}, table_dictionary={}, interface={first=[{name=first, table_ref=null}]}}}}",
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
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={*=[[@1,7:7='*',<291>,1:7]]}, tab2={*=[[@7,37:37='*',<291>,1:37]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@1,7:7='*',<291>,1:7]]}, query1={*=[[@7,37:37='*',<291>,1:37]]}}",
						extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{union2={query0={query_dictionary={*=[[@1,7:7='*',<291>,1:7]]}, table_dictionary={tab1={*=[[@1,7:7='*',<291>,1:7]]}}, interface={*=[{name=*, table_ref=*}]}}, interface={*=[{name=*, table_ref=*}, {name=*, table_ref=*}]}, query1={query_dictionary={*=[[@7,37:37='*',<291>,1:37]]}, table_dictionary={tab2={*=[[@7,37:37='*',<291>,1:37]]}}, interface={*=[{name=*, table_ref=*}]}}}}",
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
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={*=[[@1,7:7='*',<291>,1:7]]}, tab2={*=[[@7,41:41='*',<291>,1:41]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@1,7:7='*',<291>,1:7]]}, query1={*=[[@7,41:41='*',<291>,1:41]]}}",
						extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{intersect2={query0={query_dictionary={*=[[@1,7:7='*',<291>,1:7]]}, table_dictionary={tab1={*=[[@1,7:7='*',<291>,1:7]]}}, interface={*=[{name=*, table_ref=*}]}}, interface={*=[{name=*, table_ref=*}, {name=*, table_ref=*}]}, query1={query_dictionary={*=[[@7,41:41='*',<291>,1:41]]}, table_dictionary={tab2={*=[[@7,41:41='*',<291>,1:41]]}}, interface={*=[{name=*, table_ref=*}]}}}}",
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
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={*=[[@1,7:7='*',<291>,1:7]]}, tab2={*=[[@7,42:42='*',<291>,1:42]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@1,7:7='*',<291>,1:7]]}, query1={*=[[@7,42:42='*',<291>,1:42]]}}",
						extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{union2={query0={query_dictionary={*=[[@1,7:7='*',<291>,1:7]]}, table_dictionary={tab1={*=[[@1,7:7='*',<291>,1:7]]}}, interface={*=[{name=*, table_ref=*}]}}, interface={*=[{name=*, table_ref=*}, {name=*, table_ref=*}]}, query1={query_dictionary={*=[[@7,42:42='*',<291>,1:42]]}, table_dictionary={tab2={*=[[@7,42:42='*',<291>,1:42]]}}, interface={*=[{name=*, table_ref=*}]}}}}",
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
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={*=[[@1,7:7='*',<291>,1:7]]}, tab2={*=[[@7,46:46='*',<291>,1:46]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@1,7:7='*',<291>,1:7]]}, query1={*=[[@7,46:46='*',<291>,1:46]]}}",
						extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{intersect2={query0={query_dictionary={*=[[@1,7:7='*',<291>,1:7]]}, table_dictionary={tab1={*=[[@1,7:7='*',<291>,1:7]]}}, interface={*=[{name=*, table_ref=*}]}}, interface={*=[{name=*, table_ref=*}, {name=*, table_ref=*}]}, query1={query_dictionary={*=[[@7,46:46='*',<291>,1:46]]}, table_dictionary={tab2={*=[[@7,46:46='*',<291>,1:46]]}}, interface={*=[{name=*, table_ref=*}]}}}}",
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
		Assert.assertEquals("Table Dictionary is wrong", "{other={*=[[@10,54:54='*',<291>,1:54]]}, problem={*=[[@5,22:22='*',<291>,1:22]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@5,22:22='*',<291>,1:22]]}, query1={*=[[@10,54:54='*',<291>,1:54]]}, query3={*=[[@1,7:7='*',<291>,1:7]]}}",
						extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query3={query_dictionary={*=[[@1,7:7='*',<291>,1:7]]}, table_dictionary={}, def_intersect2={query0={query_dictionary={*=[[@5,22:22='*',<291>,1:22]]}, table_dictionary={problem={*=[[@5,22:22='*',<291>,1:22]]}}, interface={*=[{name=*, table_ref=*}]}}, interface={*=[{name=*, table_ref=*}, {name=*, table_ref=*}]}, query1={query_dictionary={*=[[@10,54:54='*',<291>,1:54]]}, table_dictionary={other={*=[[@10,54:54='*',<291>,1:54]]}}, interface={*=[{name=*, table_ref=*}]}}}, interface={*=[{name=*, table_ref=*}]}, table_alias={tab2=intersect2}}}",
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
		Assert.assertEquals("Table Dictionary is wrong", "{problem={*=[[@12,60:60='*',<291>,1:60]]}, <tuple>={*=[[@1,7:7='*',<291>,1:7]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@1,7:7='*',<291>,1:7]]}, query1={*=[[@12,60:60='*',<291>,1:60]]}, query2={*=[[@8,45:45='*',<291>,1:45]]}}",
						extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{union3={query0={query_dictionary={*=[[@1,7:7='*',<291>,1:7]]}, table_dictionary={<tuple>={*=[[@1,7:7='*',<291>,1:7]]}}, interface={*=[{name=*, table_ref=*}]}, table_alias={tab1=<tuple>}}, interface={*=[{name=*, table_ref=*}, {name=*, table_ref=*}]}, query2={query_dictionary={*=[[@8,45:45='*',<291>,1:45]]}, table_dictionary={}, def_query1={query_dictionary={*=[[@12,60:60='*',<291>,1:60]]}, table_dictionary={problem={*=[[@12,60:60='*',<291>,1:60]]}}, interface={*=[{name=*, table_ref=*}]}}, interface={*=[{name=*, table_ref=*}]}, table_alias={tab2=query1}}}}",
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
		Assert.assertEquals("Table Dictionary is wrong", "{answer={*=[[@16,75:75='*',<291>,1:75]]}, <tuple>={*=[[@1,7:7='*',<291>,1:7]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@1,7:7='*',<291>,1:7]]}, query1={*=[[@16,75:75='*',<291>,1:75]]}, query2={*=[[@12,60:60='*',<291>,1:60]]}, query3={*=[[@8,45:45='*',<291>,1:45]]}}",
						extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{union4={query0={query_dictionary={*=[[@1,7:7='*',<291>,1:7]]}, table_dictionary={<tuple>={*=[[@1,7:7='*',<291>,1:7]]}}, interface={*=[{name=*, table_ref=*}]}, table_alias={tab1=<tuple>}}, interface={*=[{name=*, table_ref=*}, {name=*, table_ref=*}]}, query3={query_dictionary={*=[[@8,45:45='*',<291>,1:45]]}, table_dictionary={}, interface={*=[{name=*, table_ref=*}]}, table_alias={tab2=query2}, def_query2={query_dictionary={*=[[@12,60:60='*',<291>,1:60]]}, table_dictionary={}, def_query1={query_dictionary={*=[[@16,75:75='*',<291>,1:75]]}, table_dictionary={answer={*=[[@16,75:75='*',<291>,1:75]]}}, interface={*=[{name=*, table_ref=*}]}}, interface={*=[{name=*, table_ref=*}]}, table_alias={problem=query1}}}}}",
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
		Assert.assertEquals("Table Dictionary is wrong", "{problem={*=[[@15,84:84='*',<291>,1:84]]}, answer={*=[[@10,52:52='*',<291>,1:52]]}, tab1={*=[[@1,7:7='*',<291>,1:7]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query4={*=[[@6,37:37='*',<291>,1:37]]}, query0={*=[[@1,7:7='*',<291>,1:7]]}, query1={*=[[@10,52:52='*',<291>,1:52]]}, query2={*=[[@15,84:84='*',<291>,1:84]]}}",
						extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{intersect5={query4={query_dictionary={*=[[@6,37:37='*',<291>,1:37]]}, table_dictionary={}, def_intersect3={interface={*=[{name=*, table_ref=*}, {name=*, table_ref=*}]}, query1={query_dictionary={*=[[@10,52:52='*',<291>,1:52]]}, table_dictionary={answer={*=[[@10,52:52='*',<291>,1:52]]}}, interface={*=[{name=*, table_ref=*}]}}, query2={query_dictionary={*=[[@15,84:84='*',<291>,1:84]]}, table_dictionary={problem={*=[[@15,84:84='*',<291>,1:84]]}}, interface={*=[{name=*, table_ref=*}]}}}, interface={*=[{name=*, table_ref=*}]}, table_alias={tab2=intersect3}}, query0={query_dictionary={*=[[@1,7:7='*',<291>,1:7]]}, table_dictionary={tab1={*=[[@1,7:7='*',<291>,1:7]]}}, interface={*=[{name=*, table_ref=*}]}}, interface={*=[{name=*, table_ref=*}, {name=*, table_ref=*}]}}}",
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
		Assert.assertEquals("Table Dictionary is wrong", "{problem={*=[[@15,76:76='*',<291>,1:76]]}, answer={*=[[@10,48:48='*',<291>,1:48]]}, tab1={*=[[@1,7:7='*',<291>,1:7]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query4={*=[[@6,33:33='*',<291>,1:33]]}, query0={*=[[@1,7:7='*',<291>,1:7]]}, query1={*=[[@10,48:48='*',<291>,1:48]]}, query2={*=[[@15,76:76='*',<291>,1:76]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{union5={query4={def_union3={interface={*=[{name=*, table_ref=*}, {name=*, table_ref=*}]}, query1={query_dictionary={*=[[@10,48:48='*',<291>,1:48]]}, table_dictionary={answer={*=[[@10,48:48='*',<291>,1:48]]}}, interface={*=[{name=*, table_ref=*}]}}, query2={query_dictionary={*=[[@15,76:76='*',<291>,1:76]]}, table_dictionary={problem={*=[[@15,76:76='*',<291>,1:76]]}}, interface={*=[{name=*, table_ref=*}]}}}, query_dictionary={*=[[@6,33:33='*',<291>,1:33]]}, table_dictionary={}, interface={*=[{name=*, table_ref=*}]}, table_alias={tab2=union3}}, query0={query_dictionary={*=[[@1,7:7='*',<291>,1:7]]}, table_dictionary={tab1={*=[[@1,7:7='*',<291>,1:7]]}}, interface={*=[{name=*, table_ref=*}]}}, interface={*=[{name=*, table_ref=*}, {name=*, table_ref=*}]}}}",
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
		Assert.assertEquals("Table Dictionary is wrong", "{problem={*=[[@15,80:80='*',<291>,1:80]]}, answer={*=[[@10,52:52='*',<291>,1:52]]}, tab1={*=[[@1,7:7='*',<291>,1:7]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query4={*=[[@6,37:37='*',<291>,1:37]]}, query0={*=[[@1,7:7='*',<291>,1:7]]}, query1={*=[[@10,52:52='*',<291>,1:52]]}, query2={*=[[@15,80:80='*',<291>,1:80]]}}",
						extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{intersect5={query4={def_union3={interface={*=[{name=*, table_ref=*}, {name=*, table_ref=*}]}, query1={query_dictionary={*=[[@10,52:52='*',<291>,1:52]]}, table_dictionary={answer={*=[[@10,52:52='*',<291>,1:52]]}}, interface={*=[{name=*, table_ref=*}]}}, query2={query_dictionary={*=[[@15,80:80='*',<291>,1:80]]}, table_dictionary={problem={*=[[@15,80:80='*',<291>,1:80]]}}, interface={*=[{name=*, table_ref=*}]}}}, query_dictionary={*=[[@6,37:37='*',<291>,1:37]]}, table_dictionary={}, interface={*=[{name=*, table_ref=*}]}, table_alias={tab2=union3}}, query0={query_dictionary={*=[[@1,7:7='*',<291>,1:7]]}, table_dictionary={tab1={*=[[@1,7:7='*',<291>,1:7]]}}, interface={*=[{name=*, table_ref=*}]}}, interface={*=[{name=*, table_ref=*}, {name=*, table_ref=*}]}}}",
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
		Assert.assertEquals("Table Dictionary is wrong", "{problem={*=[[@15,80:80='*',<291>,1:80]]}, answer={*=[[@10,48:48='*',<291>,1:48]]}, tab1={*=[[@1,7:7='*',<291>,1:7]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query4={*=[[@6,33:33='*',<291>,1:33]]}, query0={*=[[@1,7:7='*',<291>,1:7]]}, query1={*=[[@10,48:48='*',<291>,1:48]]}, query2={*=[[@15,80:80='*',<291>,1:80]]}}",
						extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{union5={query4={query_dictionary={*=[[@6,33:33='*',<291>,1:33]]}, table_dictionary={}, def_intersect3={interface={*=[{name=*, table_ref=*}, {name=*, table_ref=*}]}, query1={query_dictionary={*=[[@10,48:48='*',<291>,1:48]]}, table_dictionary={answer={*=[[@10,48:48='*',<291>,1:48]]}}, interface={*=[{name=*, table_ref=*}]}}, query2={query_dictionary={*=[[@15,80:80='*',<291>,1:80]]}, table_dictionary={problem={*=[[@15,80:80='*',<291>,1:80]]}}, interface={*=[{name=*, table_ref=*}]}}}, interface={*=[{name=*, table_ref=*}]}, table_alias={tab2=intersect3}}, query0={query_dictionary={*=[[@1,7:7='*',<291>,1:7]]}, table_dictionary={tab1={*=[[@1,7:7='*',<291>,1:7]]}}, interface={*=[{name=*, table_ref=*}]}}, interface={*=[{name=*, table_ref=*}, {name=*, table_ref=*}]}}}",
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
		Assert.assertEquals("Table Dictionary is wrong", "{problem={*=[[@10,50:50='*',<291>,1:50]]}, answer={*=[[@5,22:22='*',<291>,1:22]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@5,22:22='*',<291>,1:22]]}, query1={*=[[@10,50:50='*',<291>,1:50]]}, query3={*=[[@1,7:7='*',<291>,1:7]]}}",
						extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query3={def_union2={query0={query_dictionary={*=[[@5,22:22='*',<291>,1:22]]}, table_dictionary={answer={*=[[@5,22:22='*',<291>,1:22]]}}, interface={*=[{name=*, table_ref=*}]}}, interface={*=[{name=*, table_ref=*}, {name=*, table_ref=*}]}, query1={query_dictionary={*=[[@10,50:50='*',<291>,1:50]]}, table_dictionary={problem={*=[[@10,50:50='*',<291>,1:50]]}}, interface={*=[{name=*, table_ref=*}]}}}, query_dictionary={*=[[@1,7:7='*',<291>,1:7]]}, table_dictionary={}, interface={*=[{name=*, table_ref=*}]}, table_alias={tab2=union2}}}",
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
		Assert.assertEquals("Table Dictionary is wrong", "{scbcrse={subj_code=[[@6,32:40='subj_code',<381>,1:32]], *=[[@1,7:7='*',<291>,1:7]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@1,7:7='*',<291>,1:7]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={query_dictionary={*=[[@1,7:7='*',<291>,1:7]]}, table_dictionary={scbcrse={subj_code=[[@6,32:40='subj_code',<381>,1:32]], *=[[@1,7:7='*',<291>,1:7]]}}, filters=[{name=subj_code, table_ref=scbcrse}], interface={*=[{name=*, table_ref=*}]}, table_alias={aa=scbcrse}}}",
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
		Assert.assertEquals("Table Dictionary is wrong", "{scbcrse={subj_code=[[@6,32:40='subj_code',<381>,1:32]], *=[[@1,7:7='*',<291>,1:7]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@1,7:7='*',<291>,1:7]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={query_dictionary={*=[[@1,7:7='*',<291>,1:7]]}, table_dictionary={scbcrse={subj_code=[[@6,32:40='subj_code',<381>,1:32]], *=[[@1,7:7='*',<291>,1:7]]}}, filters=[{name=subj_code, table_ref=scbcrse}], interface={*=[{name=*, table_ref=*}]}, table_alias={aa=scbcrse}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void scalarSubqueriesSymbolTableTestV1() {
		String query = " select a aa, (select max(D) from ee where 1=1) dd from tab1" +
		" where a in (select c from ff where 1=1) ";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=a, table_ref=null}, alias=aa}, 2={lookup={from={table={alias=null, table=ee}}, where={condition={left={literal=1}, right={literal=1}, operator==}}, select={1={function={function_name=max, qualifier=null, parameters={column={name=D, table_ref=null}}}}}}, alias=dd}}, from={table={alias=null, table=tab1}}, where={in={item={column={name=a, table_ref=null}}, in_list={select={1={column={name=c, table_ref=null}}}, from={table={alias=null, table=ff}}, where={condition={left={literal=1}, right={literal=1}, operator==}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[aa, dd]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{ee={D=[[@8,26:26='D',<381>,1:26]]}, ff={c=[[@25,80:80='c',<381>,1:80]]}, tab1={a=[[@1,8:8='a',<381>,1:8], [@21,67:67='a',<381>,1:67]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query4={aa=[[@2,10:11='aa',<381>,1:10]], dd=[[@17,48:49='dd',<381>,1:48]]}, query0={unnamed_0=[[@9,27:27=')',<288>,1:27]]}, query2={c=[[@25,80:80='c',<381>,1:80]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query4={query_dictionary={aa=[[@2,10:11='aa',<381>,1:10]], dd=[[@17,48:49='dd',<381>,1:48]]}, table_dictionary={tab1={a=[[@1,8:8='a',<381>,1:8], [@21,67:67='a',<381>,1:67]]}}, dependent_queries={predicand1={query=query0, type=interface}, in_list3={query=query2, type=filters}}, def_query0={query_dictionary={unnamed_0=[[@9,27:27=')',<288>,1:27]]}, table_dictionary={ee={D=[[@8,26:26='D',<381>,1:26]]}}, filters=[], interface={unnamed_0=[{name=D, table_ref=ee}]}}, filters=[{name=a, table_ref=tab1}], interface={aa=[{name=a, table_ref=tab1}], dd=[{name=D, table_ref=null}]}, def_query2={query_dictionary={c=[[@25,80:80='c',<381>,1:80]]}, table_dictionary={ff={c=[[@25,80:80='c',<381>,1:80]]}}, filters=[], interface={c=[{name=c, table_ref=ff}]}}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void scalarSubqueriesSymbolTableTestV2() {
		String query = " select tab1.a aa, (select max(D) from ee where 1=1) dd from tab1" +
		" join tab2 bb on tab1.a = (select max(c) from ff where 1=1) ";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=a, table_ref=tab1}, alias=aa}, 2={lookup={from={table={alias=null, table=ee}}, where={condition={left={literal=1}, right={literal=1}, operator==}}, select={1={function={function_name=max, qualifier=null, parameters={column={name=D, table_ref=null}}}}}}, alias=dd}}, from={join={1={table={alias=null, table=tab1}}, 2={join=join, on={condition={left={column={name=a, table_ref=tab1}}, right={select={1={function={function_name=max, qualifier=null, parameters={column={name=c, table_ref=null}}}}}, from={table={alias=null, table=ff}}, where={condition={left={literal=1}, right={literal=1}, operator==}}}, operator==}}}, 3={table={alias=bb, table=tab2}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[aa, dd]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{ee={D=[[@10,31:31='D',<381>,1:31]]}, ff={c=[[@34,103:103='c',<381>,1:103]]}, tab1={a=[[@1,8:11='tab1',<381>,1:8], [@26,82:85='tab1',<381>,1:82]]}, tab2={}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query4={aa=[[@4,15:16='aa',<381>,1:15]], dd=[[@19,53:54='dd',<381>,1:53]]}, query0={unnamed_0=[[@11,32:32=')',<288>,1:32]]}, query2={unnamed_1=[[@35,104:104=')',<288>,1:104]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query4={query_dictionary={aa=[[@4,15:16='aa',<381>,1:15]], dd=[[@19,53:54='dd',<381>,1:53]]}, table_dictionary={tab1={a=[[@1,8:11='tab1',<381>,1:8], [@26,82:85='tab1',<381>,1:82]]}, tab2={}}, dependent_queries={predicand3={query=query2, type=filters}, predicand1={query=query0, type=interface}}, def_query0={query_dictionary={unnamed_0=[[@11,32:32=')',<288>,1:32]]}, table_dictionary={ee={D=[[@10,31:31='D',<381>,1:31]]}}, filters=[], interface={unnamed_0=[{name=D, table_ref=ee}]}}, filters=[{name=a, table_ref=tab1}], interface={aa=[{name=a, table_ref=tab1}], dd=[{name=D, table_ref=null}]}, table_alias={bb=tab2}, def_query2={query_dictionary={unnamed_1=[[@35,104:104=')',<288>,1:104]]}, table_dictionary={ff={c=[[@34,103:103='c',<381>,1:103]]}}, filters=[], interface={unnamed_1=[{name=c, table_ref=ff}]}}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void scalarSubqueriesSymbolTableTestV3() {
		String query = " select a aa, (select max(D) from ee where 1=1) dd from tab1" +
		" group by a having a > (select max(c) from ff where 1=1) ";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=a, table_ref=null}, alias=aa}, 2={lookup={from={table={alias=null, table=ee}}, where={condition={left={literal=1}, right={literal=1}, operator==}}, select={1={function={function_name=max, qualifier=null, parameters={column={name=D, table_ref=null}}}}}}, alias=dd}}, having={condition={left={column={name=a, table_ref=null}}, right={select={1={function={function_name=max, qualifier=null, parameters={column={name=c, table_ref=null}}}}}, from={table={alias=null, table=ff}}, where={condition={left={literal=1}, right={literal=1}, operator==}}}, operator=>}}, from={table={alias=null, table=tab1}}, groupby={1={column={name=a, table_ref=null}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[aa, dd]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{ee={D=[[@8,26:26='D',<381>,1:26]]}, ff={c=[[@30,95:95='c',<381>,1:95]]}, tab1={a=[[@1,8:8='a',<381>,1:8], [@22,70:70='a',<381>,1:70], [@24,79:79='a',<381>,1:79]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query4={aa=[[@2,10:11='aa',<381>,1:10]], dd=[[@17,48:49='dd',<381>,1:48]]}, query0={unnamed_0=[[@9,27:27=')',<288>,1:27]]}, query2={unnamed_1=[[@31,96:96=')',<288>,1:96]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query4={query_dictionary={aa=[[@2,10:11='aa',<381>,1:10]], dd=[[@17,48:49='dd',<381>,1:48]]}, table_dictionary={tab1={a=[[@1,8:8='a',<381>,1:8], [@22,70:70='a',<381>,1:70], [@24,79:79='a',<381>,1:79]]}}, grouped_by=[{name=a, table_ref=tab1}], dependent_queries={predicand3={query=query2, type=filters}, predicand1={query=query0, type=interface}}, def_query0={query_dictionary={unnamed_0=[[@9,27:27=')',<288>,1:27]]}, table_dictionary={ee={D=[[@8,26:26='D',<381>,1:26]]}}, filters=[], interface={unnamed_0=[{name=D, table_ref=ee}]}}, filters=[{name=a, table_ref=tab1}], interface={aa=[{name=a, table_ref=tab1}], dd=[{name=D, table_ref=null}]}, def_query2={query_dictionary={unnamed_1=[[@31,96:96=')',<288>,1:96]]}, table_dictionary={ff={c=[[@30,95:95='c',<381>,1:95]]}}, filters=[], interface={unnamed_1=[{name=c, table_ref=ff}]}}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void scalarSubqueriesSymbolTableTestV4() {
		String query = " select a aa, (select max(D) from ee where 1=1) dd from tab1" +
		" group by a, (select max(c) from ff where 1=1) ";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=a, table_ref=null}, alias=aa}, 2={lookup={from={table={alias=null, table=ee}}, where={condition={left={literal=1}, right={literal=1}, operator==}}, select={1={function={function_name=max, qualifier=null, parameters={column={name=D, table_ref=null}}}}}}, alias=dd}}, from={table={alias=null, table=tab1}}, groupby={1={column={name=a, table_ref=null}}, 2={select={1={function={function_name=max, qualifier=null, parameters={column={name=c, table_ref=null}}}}}, from={table={alias=null, table=ff}}, where={condition={left={literal=1}, right={literal=1}, operator==}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[aa, dd]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{ee={D=[[@8,26:26='D',<381>,1:26]]}, ff={c=[[@28,85:85='c',<381>,1:85]]}, tab1={a=[[@1,8:8='a',<381>,1:8], [@22,70:70='a',<381>,1:70]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query4={aa=[[@2,10:11='aa',<381>,1:10]], dd=[[@17,48:49='dd',<381>,1:48]]}, query0={unnamed_0=[[@9,27:27=')',<288>,1:27]]}, query2={unnamed_1=[[@29,86:86=')',<288>,1:86]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query4={query_dictionary={aa=[[@2,10:11='aa',<381>,1:10]], dd=[[@17,48:49='dd',<381>,1:48]]}, table_dictionary={tab1={a=[[@1,8:8='a',<381>,1:8], [@22,70:70='a',<381>,1:70]]}}, grouped_by=[{name=a, table_ref=tab1}], dependent_queries={predicand3={query=query2, type=group_by}, predicand1={query=query0, type=interface}}, def_query0={query_dictionary={unnamed_0=[[@9,27:27=')',<288>,1:27]]}, table_dictionary={ee={D=[[@8,26:26='D',<381>,1:26]]}}, filters=[], interface={unnamed_0=[{name=D, table_ref=ee}]}}, interface={aa=[{name=a, table_ref=tab1}], dd=[{name=D, table_ref=null}]}, def_query2={query_dictionary={unnamed_1=[[@29,86:86=')',<288>,1:86]]}, table_dictionary={ff={c=[[@28,85:85='c',<381>,1:85]]}}, filters=[], interface={unnamed_1=[{name=c, table_ref=ff}]}}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void scalarSubqueriesSymbolTableTestV5() {
		String query = " select a aa, (select max(D) from ee where 1=1) dd from tab1" +
		" order by (select max(c) from ff where 1=1) ";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=a, table_ref=null}, alias=aa}, 2={lookup={from={table={alias=null, table=ee}}, where={condition={left={literal=1}, right={literal=1}, operator==}}, select={1={function={function_name=max, qualifier=null, parameters={column={name=D, table_ref=null}}}}}}, alias=dd}}, orderby={1={null_order=null, predicand={select={1={function={function_name=max, qualifier=null, parameters={column={name=c, table_ref=null}}}}}, from={table={alias=null, table=ff}}, where={condition={left={literal=1}, right={literal=1}, operator==}}}, sort_order=ASC}}, from={table={alias=null, table=tab1}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[aa, dd]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{ee={D=[[@8,26:26='D',<381>,1:26]]}, ff={c=[[@26,82:82='c',<381>,1:82]]}, tab1={a=[[@1,8:8='a',<381>,1:8]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query4={aa=[[@2,10:11='aa',<381>,1:10]], dd=[[@17,48:49='dd',<381>,1:48]]}, query0={unnamed_0=[[@9,27:27=')',<288>,1:27]]}, query2={unnamed_1=[[@27,83:83=')',<288>,1:83]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query4={query_dictionary={aa=[[@2,10:11='aa',<381>,1:10]], dd=[[@17,48:49='dd',<381>,1:48]]}, table_dictionary={tab1={a=[[@1,8:8='a',<381>,1:8]]}}, dependent_queries={predicand3={query=query2, type=order_by}, predicand1={query=query0, type=interface}}, def_query0={query_dictionary={unnamed_0=[[@9,27:27=')',<288>,1:27]]}, table_dictionary={ee={D=[[@8,26:26='D',<381>,1:26]]}}, filters=[], interface={unnamed_0=[{name=D, table_ref=ee}]}}, ordered_by=[], interface={aa=[{name=a, table_ref=tab1}], dd=[{name=D, table_ref=null}]}, def_query2={query_dictionary={unnamed_1=[[@27,83:83=')',<288>,1:83]]}, table_dictionary={ff={c=[[@26,82:82='c',<381>,1:82]]}}, filters=[], interface={unnamed_1=[{name=c, table_ref=ff}]}}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void scalarSubqueriesSymbolTableTestV6() {
		String query = " select a aa, (select max(D) from ee where 1=1) dd from tab1" +
		" qualify a > (select max(c) from ff where 1=1) ";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=a, table_ref=null}, alias=aa}, 2={lookup={from={table={alias=null, table=ee}}, where={condition={left={literal=1}, right={literal=1}, operator==}}, select={1={function={function_name=max, qualifier=null, parameters={column={name=D, table_ref=null}}}}}}, alias=dd}}, from={table={alias=null, table=tab1}}, qualify={condition={left={column={name=a, table_ref=null}}, right={select={1={function={function_name=max, qualifier=null, parameters={column={name=c, table_ref=null}}}}}, from={table={alias=null, table=ff}}, where={condition={left={literal=1}, right={literal=1}, operator==}}}, operator=>}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[aa, dd]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{ee={D=[[@8,26:26='D',<381>,1:26]]}, ff={c=[[@27,85:85='c',<381>,1:85]]}, tab1={a=[[@1,8:8='a',<381>,1:8], [@21,69:69='a',<381>,1:69]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query4={aa=[[@2,10:11='aa',<381>,1:10]], dd=[[@17,48:49='dd',<381>,1:48]]}, query0={unnamed_0=[[@9,27:27=')',<288>,1:27]]}, query2={unnamed_1=[[@28,86:86=')',<288>,1:86]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query4={query_dictionary={aa=[[@2,10:11='aa',<381>,1:10]], dd=[[@17,48:49='dd',<381>,1:48]]}, table_dictionary={tab1={a=[[@1,8:8='a',<381>,1:8], [@21,69:69='a',<381>,1:69]]}}, dependent_queries={predicand3={query=query2, type=filters}, predicand1={query=query0, type=interface}}, def_query0={query_dictionary={unnamed_0=[[@9,27:27=')',<288>,1:27]]}, table_dictionary={ee={D=[[@8,26:26='D',<381>,1:26]]}}, filters=[], interface={unnamed_0=[{name=D, table_ref=ee}]}}, filters=[{name=a, table_ref=tab1}], interface={aa=[{name=a, table_ref=tab1}], dd=[{name=D, table_ref=null}]}, def_query2={query_dictionary={unnamed_1=[[@28,86:86=')',<288>,1:86]]}, table_dictionary={ff={c=[[@27,85:85='c',<381>,1:85]]}}, filters=[], interface={unnamed_1=[{name=c, table_ref=ff}]}}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void scalarSubqueriesSymbolTableTestV7() {
		String query = " select a aa, (select max(D) from ee where 1=1) dd from tab1" +
		" where a > (select max(c) from ff where 1=1) ";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=a, table_ref=null}, alias=aa}, 2={lookup={from={table={alias=null, table=ee}}, where={condition={left={literal=1}, right={literal=1}, operator==}}, select={1={function={function_name=max, qualifier=null, parameters={column={name=D, table_ref=null}}}}}}, alias=dd}}, from={table={alias=null, table=tab1}}, where={condition={left={column={name=a, table_ref=null}}, right={select={1={function={function_name=max, qualifier=null, parameters={column={name=c, table_ref=null}}}}}, from={table={alias=null, table=ff}}, where={condition={left={literal=1}, right={literal=1}, operator==}}}, operator=>}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[aa, dd]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{ee={D=[[@8,26:26='D',<381>,1:26]]}, ff={c=[[@27,83:83='c',<381>,1:83]]}, tab1={a=[[@1,8:8='a',<381>,1:8], [@21,67:67='a',<381>,1:67]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query4={aa=[[@2,10:11='aa',<381>,1:10]], dd=[[@17,48:49='dd',<381>,1:48]]}, query0={unnamed_0=[[@9,27:27=')',<288>,1:27]]}, query2={unnamed_1=[[@28,84:84=')',<288>,1:84]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query4={query_dictionary={aa=[[@2,10:11='aa',<381>,1:10]], dd=[[@17,48:49='dd',<381>,1:48]]}, table_dictionary={tab1={a=[[@1,8:8='a',<381>,1:8], [@21,67:67='a',<381>,1:67]]}}, dependent_queries={predicand3={query=query2, type=filters}, predicand1={query=query0, type=interface}}, def_query0={query_dictionary={unnamed_0=[[@9,27:27=')',<288>,1:27]]}, table_dictionary={ee={D=[[@8,26:26='D',<381>,1:26]]}}, filters=[], interface={unnamed_0=[{name=D, table_ref=ee}]}}, filters=[{name=a, table_ref=tab1}], interface={aa=[{name=a, table_ref=tab1}], dd=[{name=D, table_ref=null}]}, def_query2={query_dictionary={unnamed_1=[[@28,84:84=')',<288>,1:84]]}, table_dictionary={ff={c=[[@27,83:83='c',<381>,1:83]]}}, filters=[], interface={unnamed_1=[{name=c, table_ref=ff}]}}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void scalarSubqueriesSymbolTableTestV8() {
		String query = " select a aa, (select max(D) from ee where 1=1) dd from tab1" +
		" where exists (select 1 from ff where ff.c = tab1.a) ";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=a, table_ref=null}, alias=aa}, 2={lookup={from={table={alias=null, table=ee}}, where={condition={left={literal=1}, right={literal=1}, operator==}}, select={1={function={function_name=max, qualifier=null, parameters={column={name=D, table_ref=null}}}}}}, alias=dd}}, from={table={alias=null, table=tab1}}, where={exists={select={1={literal=1}}, from={table={alias=null, table=ff}}, where={condition={left={column={name=c, table_ref=ff}}, right={column={name=a, table_ref=tab1}}, operator==}}, operator=exists}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[aa, dd]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{ee={D=[[@8,26:26='D',<381>,1:26]]}, ff={c=[[@28,98:99='ff',<381>,1:98]]}, tab1={a=[[@1,8:8='a',<381>,1:8], [@32,105:108='tab1',<381>,1:105], [@32,105:108='tab1',<381>,1:105]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query4={aa=[[@2,10:11='aa',<381>,1:10]], dd=[[@17,48:49='dd',<381>,1:48]]}, query0={unnamed_0=[[@9,27:27=')',<288>,1:27]]}, query2={unnamed_1=[[@24,82:82='1',<300>,1:82]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query4={query_dictionary={aa=[[@2,10:11='aa',<381>,1:10]], dd=[[@17,48:49='dd',<381>,1:48]]}, table_dictionary={tab1={a=[[@1,8:8='a',<381>,1:8], [@32,105:108='tab1',<381>,1:105], [@32,105:108='tab1',<381>,1:105]]}}, dependent_queries={predicand1={query=query0, type=interface}, exists3={query=query2, type=filters}}, def_query0={query_dictionary={unnamed_0=[[@9,27:27=')',<288>,1:27]]}, table_dictionary={ee={D=[[@8,26:26='D',<381>,1:26]]}}, filters=[], interface={unnamed_0=[{name=D, table_ref=ee}]}}, filters=[], interface={aa=[{name=a, table_ref=tab1}], dd=[{name=D, table_ref=null}]}, def_query2={query_dictionary={unnamed_1=[[@24,82:82='1',<300>,1:82]]}, table_dictionary={ff={c=[[@28,98:99='ff',<381>,1:98]]}}, filters=[{name=c, table_ref=ff}, {name=a, table_ref=tab1}], interface={unnamed_1=[]}}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void scalarSubqueriesSymbolTableTestV9() {
		String query = " select a aa, (select max(D) from ee where 1=1) dd from tab1" +
		" qualify exists (select 1 from ff where ff.c = tab1.a) ";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=a, table_ref=null}, alias=aa}, 2={lookup={from={table={alias=null, table=ee}}, where={condition={left={literal=1}, right={literal=1}, operator==}}, select={1={function={function_name=max, qualifier=null, parameters={column={name=D, table_ref=null}}}}}}, alias=dd}}, from={table={alias=null, table=tab1}}, qualify={exists={select={1={literal=1}}, from={table={alias=null, table=ff}}, where={condition={left={column={name=c, table_ref=ff}}, right={column={name=a, table_ref=tab1}}, operator==}}, operator=exists}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[aa, dd]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{ee={D=[[@8,26:26='D',<381>,1:26]]}, ff={c=[[@28,100:101='ff',<381>,1:100]]}, tab1={a=[[@1,8:8='a',<381>,1:8], [@32,107:110='tab1',<381>,1:107], [@32,107:110='tab1',<381>,1:107]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query4={aa=[[@2,10:11='aa',<381>,1:10]], dd=[[@17,48:49='dd',<381>,1:48]]}, query0={unnamed_0=[[@9,27:27=')',<288>,1:27]]}, query2={unnamed_1=[[@24,84:84='1',<300>,1:84]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query4={query_dictionary={aa=[[@2,10:11='aa',<381>,1:10]], dd=[[@17,48:49='dd',<381>,1:48]]}, table_dictionary={tab1={a=[[@1,8:8='a',<381>,1:8], [@32,107:110='tab1',<381>,1:107], [@32,107:110='tab1',<381>,1:107]]}}, dependent_queries={predicand1={query=query0, type=interface}, exists3={query=query2, type=filters}}, def_query0={query_dictionary={unnamed_0=[[@9,27:27=')',<288>,1:27]]}, table_dictionary={ee={D=[[@8,26:26='D',<381>,1:26]]}}, filters=[], interface={unnamed_0=[{name=D, table_ref=ee}]}}, filters=[], interface={aa=[{name=a, table_ref=tab1}], dd=[{name=D, table_ref=null}]}, def_query2={query_dictionary={unnamed_1=[[@24,84:84='1',<300>,1:84]]}, table_dictionary={ff={c=[[@28,100:101='ff',<381>,1:100]]}}, filters=[{name=c, table_ref=ff}, {name=a, table_ref=tab1}], interface={unnamed_1=[]}}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void scalarSubqueriesCorrelatedSubquerySymbolTableTest() {
		String query = " select a aa, (select max(D) from ee where ee.x = tab1.x) dd from tab1" +
		" where a in (select c from ff where ff.y = tab1.y) ";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=a, table_ref=null}, alias=aa}, 2={lookup={from={table={alias=null, table=ee}}, where={condition={left={column={name=x, table_ref=ee}}, right={column={name=x, table_ref=tab1}}, operator==}}, select={1={function={function_name=max, qualifier=null, parameters={column={name=D, table_ref=null}}}}}}, alias=dd}}, from={table={alias=null, table=tab1}}, where={in={item={column={name=a, table_ref=null}}, in_list={select={1={column={name=c, table_ref=null}}}, from={table={alias=null, table=ff}}, where={condition={left={column={name=y, table_ref=ff}}, right={column={name=y, table_ref=tab1}}, operator==}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[aa, dd]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{ee={D=[[@8,26:26='D',<381>,1:26]], x=[[@13,43:44='ee',<381>,1:43]]}, ff={c=[[@29,90:90='c',<381>,1:90]], y=[[@33,106:107='ff',<381>,1:106]]}, tab1={a=[[@1,8:8='a',<381>,1:8], [@25,77:77='a',<381>,1:77]], x=[[@17,50:53='tab1',<381>,1:50]], y=[[@37,113:116='tab1',<381>,1:113]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query4={aa=[[@2,10:11='aa',<381>,1:10]], dd=[[@21,58:59='dd',<381>,1:58]]}, query0={unnamed_0=[[@9,27:27=')',<288>,1:27]]}, query2={c=[[@29,90:90='c',<381>,1:90]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query4={query_dictionary={aa=[[@2,10:11='aa',<381>,1:10]], dd=[[@21,58:59='dd',<381>,1:58]]}, table_dictionary={tab1={a=[[@1,8:8='a',<381>,1:8], [@25,77:77='a',<381>,1:77]], x=[[@17,50:53='tab1',<381>,1:50]], y=[[@37,113:116='tab1',<381>,1:113]]}}, dependent_queries={predicand1={query=query0, type=interface}, in_list3={query=query2, type=filters}}, def_query0={query_dictionary={unnamed_0=[[@9,27:27=')',<288>,1:27]]}, table_dictionary={ee={D=[[@8,26:26='D',<381>,1:26]], x=[[@13,43:44='ee',<381>,1:43]]}}, filters=[{name=x, table_ref=ee}, {name=x, table_ref=tab1}], interface={unnamed_0=[{name=D, table_ref=ee}]}}, filters=[{name=a, table_ref=tab1}], interface={aa=[{name=a, table_ref=tab1}], dd=[{name=x, table_ref=ee}, {name=x, table_ref=tab1}, {name=D, table_ref=null}]}, def_query2={query_dictionary={c=[[@29,90:90='c',<381>,1:90]]}, table_dictionary={ff={c=[[@29,90:90='c',<381>,1:90]], y=[[@33,106:107='ff',<381>,1:106]]}}, filters=[{name=y, table_ref=ff}, {name=y, table_ref=tab1}], interface={c=[{name=c, table_ref=ff}]}}}}",
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
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=a, table_ref=tab1}, alias=aa}, 2={lookup={from={table={alias=null, table=ee}}, select={1={function={function_name=max, qualifier=null, parameters={column={name=D, table_ref=null}}}}}}, alias=max_D}, 3={lookup={from={table={alias=null, table=ee}}, select={1={function={function_name=min, qualifier=null, parameters={column={name=D, table_ref=null}}}}}}, alias=min_D}, 4={column={name=w, table_ref=kk}}}, from={join={1={table={alias=null, table=tab1}}, 2={join=join}, 3={table={alias=kk, query={select={1={column={name=w, table_ref=null}}}, from={table={alias=null, table=jj}}}}}}}, where={in={item={column={name=a, table_ref=null}}, in_list={select={1={column={name=c, table_ref=null}}}, from={table={alias=null, table=ff}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[aa, max_D, min_D, w]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{ee={D=[[@10,31:31='D',<381>,1:31], [@21,62:62='D',<381>,1:62]]}, jj={w=[[@36,110:110='w',<381>,1:110]]}, ff={c=[[@46,143:143='c',<381>,1:143]]}, tab1={a=[[@42,130:130='a',<381>,1:130], [@1,8:11='tab1',<381>,1:8], [@1,8:11='tab1',<381>,1:8]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query4={w=[[@36,110:110='w',<381>,1:110], [@28,82:83='kk',<381>,1:82]]}, query5={c=[[@46,143:143='c',<381>,1:143]]}, query7={aa=[[@4,15:16='aa',<381>,1:15]], max_D=[[@15,43:47='max_D',<381>,1:43]], min_D=[[@26,74:78='min_D',<381>,1:74]], w=[[@30,85:85='w',<381>,1:85]]}, query0={unnamed_0=[[@11,32:32=')',<288>,1:32]]}, query2={unnamed_1=[[@22,63:63=')',<288>,1:63]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query7={table_dictionary={tab1={a=[[@42,130:130='a',<381>,1:130], [@1,8:11='tab1',<381>,1:8], [@1,8:11='tab1',<381>,1:8]]}}, def_query0={query_dictionary={unnamed_0=[[@11,32:32=')',<288>,1:32]]}, table_dictionary={ee={D=[[@10,31:31='D',<381>,1:31], [@21,62:62='D',<381>,1:62]]}}, interface={unnamed_0=[{name=D, table_ref=ee}]}}, filters=[{name=a, table_ref=tab1}], def_query5={query_dictionary={c=[[@46,143:143='c',<381>,1:143]]}, table_dictionary={ff={c=[[@46,143:143='c',<381>,1:143]]}}, interface={c=[{name=c, table_ref=ff}]}}, interface={aa=[{name=a, table_ref=tab1}], max_D=[{name=D, table_ref=null}], min_D=[{name=D, table_ref=null}], w=[{name=w, table_ref=kk}]}, def_query4={query_dictionary={w=[[@36,110:110='w',<381>,1:110], [@28,82:83='kk',<381>,1:82]]}, table_dictionary={jj={w=[[@36,110:110='w',<381>,1:110]]}}, interface={w=[{name=w, table_ref=jj}]}}, def_query2={query_dictionary={unnamed_1=[[@22,63:63=')',<288>,1:63]]}, table_dictionary={ee={D=[[@21,62:62='D',<381>,1:62]]}}, interface={unnamed_1=[{name=D, table_ref=ee}]}}, query_dictionary={aa=[[@4,15:16='aa',<381>,1:15]], max_D=[[@15,43:47='max_D',<381>,1:43]], min_D=[[@26,74:78='min_D',<381>,1:74]], w=[[@30,85:85='w',<381>,1:85]]}, dependent_queries={predicand3={query=query2, type=interface}, in_list6={query=query5, type=filters}, predicand1={query=query0, type=interface}}, table_alias={kk=query4}}}",
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
		Assert.assertEquals("Table Dictionary is wrong", "{ee={D=[[@12,44:44='D',<381>,1:44]]}, gg={D=[[@26,78:78='D',<381>,1:78]]}, ff={c=[[@50,153:153='c',<381>,1:153]]}, tab3={b=[[@40,122:122='b',<381>,1:122]]}, tab1={a=[[@1,8:8='a',<381>,1:8]], emptyCol=[[@6,23:30='emptyCol',<381>,1:23]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query8={aa=[[@2,10:11='aa',<381>,1:10]], dd=[[@32,91:92='dd',<381>,1:91]]}, query4={bb=[[@43,128:129='bb',<381>,1:128]]}, query6={c=[[@50,153:153='c',<381>,1:153]]}, query0={unnamed_0=[[@13,45:45=')',<288>,1:45]]}, query2={unnamed_1=[[@27,79:79=')',<288>,1:79]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query8={def_query6={query_dictionary={c=[[@50,153:153='c',<381>,1:153]]}, table_dictionary={ff={c=[[@50,153:153='c',<381>,1:153]]}}, filters=[], interface={c=[{name=c, table_ref=ff}]}}, query_dictionary={aa=[[@2,10:11='aa',<381>,1:10]], dd=[[@32,91:92='dd',<381>,1:91]]}, table_dictionary={tab1={a=[[@1,8:8='a',<381>,1:8]], emptyCol=[[@6,23:30='emptyCol',<381>,1:23]]}}, dependent_queries={predicand3={query=query2, type=interface}, predicand1={query=query0, type=interface}, predicand5={query=query4, type=filters}, in_list7={query=query6, type=filters}}, def_query0={query_dictionary={unnamed_0=[[@13,45:45=')',<288>,1:45]]}, table_dictionary={ee={D=[[@12,44:44='D',<381>,1:44]]}}, filters=[], interface={unnamed_0=[{name=D, table_ref=ee}]}}, filters=[], interface={aa=[{name=a, table_ref=tab1}], dd=[{name=emptyCol, table_ref=tab1}, {name=D, table_ref=tab1}]}, def_query4={query_dictionary={bb=[[@43,128:129='bb',<381>,1:128]]}, table_dictionary={tab3={b=[[@40,122:122='b',<381>,1:122]]}}, interface={bb=[{name=b, table_ref=tab3}]}}, def_query2={query_dictionary={unnamed_1=[[@27,79:79=')',<288>,1:79]]}, table_dictionary={gg={D=[[@26,78:78='D',<381>,1:78]]}}, interface={unnamed_1=[{name=D, table_ref=gg}]}}}}",
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
		Assert.assertEquals("Table Dictionary is wrong", "{cat_concentration={concentration_code=[[@35,221:221='c',<381>,1:221]]}, bnr_stvmajr={stvmajr_desc=[[@7,45:45='s',<381>,1:45]], stvmajr_valid_concentratn_ind=[[@20,126:126='s',<381>,1:126]], stvmajr_code=[[@1,7:7='s',<381>,1:7], [@39,244:244='s',<381>,1:244]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={unnamed_0=[[@30,188:188='1',<300>,1:188]]}, query2={concentration_desc=[[@11,63:80='concentration_desc',<381>,1:63]], concentration_code=[[@5,25:42='concentration_code',<381>,1:25]], active_ind=[[@15,90:99='active_ind',<381>,1:90]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query2={query_dictionary={concentration_desc=[[@11,63:80='concentration_desc',<381>,1:63]], concentration_code=[[@5,25:42='concentration_code',<381>,1:25]], active_ind=[[@15,90:99='active_ind',<381>,1:90]]}, table_dictionary={bnr_stvmajr={stvmajr_desc=[[@7,45:45='s',<381>,1:45]], stvmajr_valid_concentratn_ind=[[@20,126:126='s',<381>,1:126]], stvmajr_code=[[@1,7:7='s',<381>,1:7], [@39,244:244='s',<381>,1:244]]}}, dependent_queries={exists1={query=query0, type=filters}}, def_query0={query_dictionary={unnamed_0=[[@30,188:188='1',<300>,1:188]]}, table_dictionary={cat_concentration={concentration_code=[[@35,221:221='c',<381>,1:221]]}}, filters=[{name=concentration_code, table_ref=c}, {name=stvmajr_code, table_ref=s}], interface={unnamed_0=[]}, table_alias={c=cat_concentration}}, filters=[{name=stvmajr_valid_concentratn_ind, table_ref=s}], interface={concentration_desc=[{name=stvmajr_desc, table_ref=s}], concentration_code=[{name=stvmajr_code, table_ref=s}], active_ind=[]}, table_alias={s=bnr_stvmajr}}}",
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
		Assert.assertEquals("Table Dictionary is wrong", "{bnr_stvmajr={stvmajr_desc=[[@7,45:45='s',<381>,1:45]], stvmajr_valid_concentratn_ind=[[@20,126:126='s',<381>,1:126]], stvmajr_code=[[@1,7:7='s',<381>,1:7]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={concentration_desc=[[@11,63:80='concentration_desc',<381>,1:63]], concentration_code=[[@5,25:42='concentration_code',<381>,1:25]], active_ind=[[@15,90:99='active_ind',<381>,1:90]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={query_dictionary={concentration_desc=[[@11,63:80='concentration_desc',<381>,1:63]], concentration_code=[[@5,25:42='concentration_code',<381>,1:25]], active_ind=[[@15,90:99='active_ind',<381>,1:90]]}, table_dictionary={bnr_stvmajr={stvmajr_desc=[[@7,45:45='s',<381>,1:45]], stvmajr_valid_concentratn_ind=[[@20,126:126='s',<381>,1:126]], stvmajr_code=[[@1,7:7='s',<381>,1:7]]}}, filters=[{name=stvmajr_valid_concentratn_ind, table_ref=s}], interface={concentration_desc=[{name=stvmajr_desc, table_ref=s}], concentration_code=[{name=stvmajr_code, table_ref=s}], active_ind=[]}, table_alias={s=bnr_stvmajr}}}",
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
		Assert.assertEquals("Table Dictionary is wrong", "{employees={dept=[[@1,7:7='e',<381>,1:7], [@18,71:71='e',<381>,1:71]], salary=[[@33,113:118='salary',<381>,1:113], [@7,19:19='e',<381>,1:19], [@24,89:89='e',<381>,1:89]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={unnamed_0=[[@34,119:119=')',<288>,1:119]]}, query2={total_salary=[[@12,32:43='total_salary',<381>,1:32]], dept=[[@3,9:12='dept',<381>,1:9]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query2={query_dictionary={total_salary=[[@12,32:43='total_salary',<381>,1:32]], dept=[[@3,9:12='dept',<381>,1:9]]}, table_dictionary={employees={dept=[[@1,7:7='e',<381>,1:7], [@18,71:71='e',<381>,1:71]], salary=[[@7,19:19='e',<381>,1:19], [@24,89:89='e',<381>,1:89]]}}, grouped_by=[{name=dept, table_ref=e}], dependent_queries={predicand1={query=query0, type=filters}}, def_query0={query_dictionary={unnamed_0=[[@34,119:119=')',<288>,1:119]]}, table_dictionary={employees={salary=[[@33,113:118='salary',<381>,1:113], [@7,19:19='e',<381>,1:19], [@24,89:89='e',<381>,1:89]]}}, interface={unnamed_0=[{name=salary, table_ref=employees}]}}, filters=[{name=salary, table_ref=e}], interface={total_salary=[{name=salary, table_ref=e}], dept=[{name=dept, table_ref=e}]}, table_alias={e=employees}}}",
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
		Assert.assertEquals("Table Dictionary is wrong", "{orders={customer_id=[[@28,133:133='o',<381>,4:44]]}, customers={*=[[@7,28:28='*',<291>,1:28]], customer_id=[[@1,7:7='e',<381>,1:7], [@16,74:74='e',<381>,3:9], [@32,149:149='e',<381>,4:60]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={unnamed_0=[[@23,111:111='1',<300>,4:22]]}, query2={order_count=[[@10,34:44='order_count',<381>,1:34]], customer_id=[[@3,9:19='customer_id',<381>,1:9]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query2={query_dictionary={order_count=[[@10,34:44='order_count',<381>,1:34]], customer_id=[[@3,9:19='customer_id',<381>,1:9]]}, table_dictionary={customers={*=[[@7,28:28='*',<291>,1:28]], customer_id=[[@1,7:7='e',<381>,1:7], [@16,74:74='e',<381>,3:9], [@32,149:149='e',<381>,4:60]]}}, grouped_by=[{name=customer_id, table_ref=e}], dependent_queries={exists1={query=query0, type=filters}}, def_query0={query_dictionary={unnamed_0=[[@23,111:111='1',<300>,4:22]]}, table_dictionary={orders={customer_id=[[@28,133:133='o',<381>,4:44]]}}, filters=[{name=customer_id, table_ref=o}, {name=customer_id, table_ref=e}], interface={unnamed_0=[]}, table_alias={o=orders}}, filters=[], interface={order_count=[], customer_id=[{name=customer_id, table_ref=e}]}, table_alias={e=customers}}}",
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
				//{ee={D=[@8,26:26='D',<381>,1:26]}, ff={c=[@21,70:70='c',<381>,1:70]}, tab1={a=[@1,8:8='a',<381>,1:8]}}
		Assert.assertEquals("Table Dictionary is wrong", "{cat_concentration={concentration_code=[[@28,157:157='c',<381>,1:157], [@36,210:210='c',<381>,1:210]]}, bnr_stvmajr={stvmajr_desc=[[@7,45:45='s',<381>,1:45]], stvmajr_code=[[@1,7:7='s',<381>,1:7], [@20,127:127='s',<381>,1:127], [@40,234:234='s',<381>,1:234]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={unnamed_0=[[@31,177:177=')',<288>,1:177]]}, query2={concentration_desc=[[@11,63:80='concentration_desc',<381>,1:63]], concentration_code=[[@5,25:42='concentration_code',<381>,1:25]], active_ind=[[@15,90:99='active_ind',<381>,1:90]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query2={query_dictionary={concentration_desc=[[@11,63:80='concentration_desc',<381>,1:63]], concentration_code=[[@5,25:42='concentration_code',<381>,1:25]], active_ind=[[@15,90:99='active_ind',<381>,1:90]]}, table_dictionary={bnr_stvmajr={stvmajr_desc=[[@7,45:45='s',<381>,1:45]], stvmajr_code=[[@1,7:7='s',<381>,1:7], [@20,127:127='s',<381>,1:127], [@40,234:234='s',<381>,1:234]]}}, dependent_queries={predicand1={query=query0, type=filters}}, def_query0={query_dictionary={unnamed_0=[[@31,177:177=')',<288>,1:177]]}, table_dictionary={cat_concentration={concentration_code=[[@28,157:157='c',<381>,1:157], [@36,210:210='c',<381>,1:210]]}}, filters=[{name=concentration_code, table_ref=c}, {name=stvmajr_code, table_ref=s}], interface={unnamed_0=[{name=concentration_code, table_ref=c}]}, table_alias={c=cat_concentration}}, filters=[{name=stvmajr_code, table_ref=s}], interface={concentration_desc=[{name=stvmajr_desc, table_ref=s}], concentration_code=[{name=stvmajr_code, table_ref=s}], active_ind=[]}, table_alias={s=bnr_stvmajr}}}",
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
				//{ee={D=[@8,26:26='D',<381>,1:26]}, ff={c=[@21,70:70='c',<381>,1:70]}, tab1={a=[@1,8:8='a',<381>,1:8]}}
		Assert.assertEquals("Table Dictionary is wrong", "{cat_concentration={code=[[@25,142:142='c',<381>,1:142]], concentration_code=[[@33,181:181='c',<381>,1:181]]}, bnr_stvmajr={stvmajr_desc=[[@7,45:45='s',<381>,1:45]], stvmajr_code=[[@1,7:7='s',<381>,1:7], [@37,204:204='s',<381>,1:204]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={unnamed_0=[[@28,148:148=')',<288>,1:148]]}, query2={concentration_desc=[[@11,63:80='concentration_desc',<381>,1:63]], concentration_code=[[@5,25:42='concentration_code',<381>,1:25]], active_ind=[[@15,90:99='active_ind',<381>,1:90]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query2={query_dictionary={concentration_desc=[[@11,63:80='concentration_desc',<381>,1:63]], concentration_code=[[@5,25:42='concentration_code',<381>,1:25]], active_ind=[[@15,90:99='active_ind',<381>,1:90]]}, table_dictionary={bnr_stvmajr={stvmajr_desc=[[@7,45:45='s',<381>,1:45]], stvmajr_code=[[@1,7:7='s',<381>,1:7], [@37,204:204='s',<381>,1:204]]}}, dependent_queries={predicand1={query=query0, type=order_by}}, def_query0={query_dictionary={unnamed_0=[[@28,148:148=')',<288>,1:148]]}, table_dictionary={cat_concentration={code=[[@25,142:142='c',<381>,1:142]], concentration_code=[[@33,181:181='c',<381>,1:181]]}}, filters=[{name=concentration_code, table_ref=c}, {name=stvmajr_code, table_ref=s}], interface={unnamed_0=[{name=code, table_ref=c}]}, table_alias={c=cat_concentration}}, ordered_by=[], interface={concentration_desc=[{name=stvmajr_desc, table_ref=s}], concentration_code=[{name=stvmajr_code, table_ref=s}], active_ind=[]}, table_alias={s=bnr_stvmajr}}}",
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
		Assert.assertEquals("Table Dictionary is wrong", "{lawn={b=[[@14,57:57='b',<381>,2:19], [@29,116:121='second',<135>,3:45]]}, mulch={a=[[@5,22:22='a',<381>,1:22], [@25,106:110='first',<88>,3:35]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={a=[[@5,22:22='a',<381>,1:22], [@25,106:110='first',<88>,3:35]], *=[[@19,79:79='*',<291>,3:8]]}, query1={b=[[@14,57:57='b',<381>,2:19], [@29,116:121='second',<135>,3:45]], *=[[@19,79:79='*',<291>,3:8]]}, query2={*=[[@19,79:79='*',<291>,3:8]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query2={context_list={first=query0, second=query1}, query_dictionary={*=[[@19,79:79='*',<291>,3:8]]}, table_dictionary={}, def_query1={context_list={first=query0}, query_dictionary={b=[[@14,57:57='b',<381>,2:19], [@29,116:121='second',<135>,3:45]], *=[[@19,79:79='*',<291>,3:8]]}, table_dictionary={lawn={b=[[@14,57:57='b',<381>,2:19], [@29,116:121='second',<135>,3:45]]}}, interface={b=[{name=b, table_ref=lawn}]}, table_alias={first=query0}}, def_query0={query_dictionary={a=[[@5,22:22='a',<381>,1:22], [@25,106:110='first',<88>,3:35]], *=[[@19,79:79='*',<291>,3:8]]}, table_dictionary={mulch={a=[[@5,22:22='a',<381>,1:22], [@25,106:110='first',<88>,3:35]]}}, interface={a=[{name=a, table_ref=mulch}]}}, filters=[{name=a, table_ref=first}, {name=b, table_ref=second}], interface={*=[{name=*, table_ref=*}]}, table_alias={first=query0, second=query1}}}",
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
		Assert.assertEquals("Table Dictionary is wrong", "{lawn={b=[[@19,82:82='b',<381>,2:19], [@34,141:146='second',<135>,3:45]]}, mulch={a=[[@5,22:22='a',<381>,1:22]]}, clay={b=[[@10,48:48='b',<381>,1:48]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query4={*=[[@24,104:104='*',<291>,3:8]]}, query0={a=[[@5,22:22='a',<381>,1:22]], *=[[@24,104:104='*',<291>,3:8]]}, query1={b=[[@10,48:48='b',<381>,1:48]], *=[[@24,104:104='*',<291>,3:8]]}, query3={b=[[@19,82:82='b',<381>,2:19], [@34,141:146='second',<135>,3:45]], *=[[@24,104:104='*',<291>,3:8]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query4={context_list={first=union2, second=query3}, query_dictionary={*=[[@24,104:104='*',<291>,3:8]]}, def_union2={query0={query_dictionary={a=[[@5,22:22='a',<381>,1:22]], *=[[@24,104:104='*',<291>,3:8]]}, table_dictionary={mulch={a=[[@5,22:22='a',<381>,1:22]]}}, interface={a=[{name=a, table_ref=mulch}]}}, interface={a=[{name=a, table_ref=mulch}, {name=b, table_ref=clay}]}, query1={query_dictionary={b=[[@10,48:48='b',<381>,1:48]], *=[[@24,104:104='*',<291>,3:8]]}, table_dictionary={clay={b=[[@10,48:48='b',<381>,1:48]]}}, interface={b=[{name=b, table_ref=clay}]}}}, table_dictionary={}, filters=[{name=a, table_ref=first}, {name=b, table_ref=second}], interface={*=[{name=*, table_ref=*}]}, table_alias={first=union2, second=query3}, def_query3={context_list={first=union2}, query_dictionary={b=[[@19,82:82='b',<381>,2:19], [@34,141:146='second',<135>,3:45]], *=[[@24,104:104='*',<291>,3:8]]}, table_dictionary={lawn={b=[[@19,82:82='b',<381>,2:19], [@34,141:146='second',<135>,3:45]]}}, interface={b=[{name=b, table_ref=lawn}]}, table_alias={first=union2}}}}",
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
		Assert.assertEquals("Table Dictionary is wrong", "{lawn={b=[[@18,76:76='b',<381>,1:76]]}, mulch={a=[[@5,22:22='a',<381>,1:22]]}, clay={b=[[@10,48:48='b',<381>,1:48]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query4={*=[[@1,7:7='*',<291>,1:7]]}, query0={a=[[@5,22:22='a',<381>,1:22]], *=[[@1,7:7='*',<291>,1:7]]}, query1={b=[[@10,48:48='b',<381>,1:48]], *=[[@1,7:7='*',<291>,1:7]]}, query3={b=[[@18,76:76='b',<381>,1:76], [@28,112:117='second',<135>,1:112]], *=[[@1,7:7='*',<291>,1:7]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query4={def_union2={query0={query_dictionary={a=[[@5,22:22='a',<381>,1:22]], *=[[@1,7:7='*',<291>,1:7]]}, table_dictionary={mulch={a=[[@5,22:22='a',<381>,1:22]]}}, interface={a=[{name=a, table_ref=mulch}]}}, interface={a=[{name=a, table_ref=mulch}, {name=b, table_ref=clay}]}, query1={query_dictionary={b=[[@10,48:48='b',<381>,1:48]], *=[[@1,7:7='*',<291>,1:7]]}, table_dictionary={clay={b=[[@10,48:48='b',<381>,1:48]]}}, interface={b=[{name=b, table_ref=clay}]}}}, query_dictionary={*=[[@1,7:7='*',<291>,1:7]]}, table_dictionary={}, filters=[{name=a, table_ref=first}, {name=b, table_ref=second}], interface={*=[{name=*, table_ref=*}]}, def_query3={query_dictionary={b=[[@18,76:76='b',<381>,1:76], [@28,112:117='second',<135>,1:112]], *=[[@1,7:7='*',<291>,1:7]]}, table_dictionary={lawn={b=[[@18,76:76='b',<381>,1:76]]}}, interface={b=[{name=b, table_ref=lawn}]}}, table_alias={first=union2, second=query3}}}",
				extractor.getSymbolTable().toString());
}


	@Test
	public void queryOverQueriesMissingUnqualifiedColumnEmitsSpecificFatalAndUnresolved() {
		String sql = "select col3 from (select col1 from tab1) aaa join (select col2 from tab2) bbb on aaa.col1 = bbb.col2";
		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runParsertest(sql, parser);

		Snippet snippet = extractor.getSnippet();
		assertFatalDiagnosticCount(snippet,
				"UNQUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIASES",
				"Unqualified column 'col3'",
				"col3",
				1);
		assertDiagnosticCountBySeverity(snippet,
				"UNRESOLVED_UNQUALIFIED_COLUMNS",
				ParseDiagnostic.Severity.ERROR,
				"col3",
				null,
				1);
	}

	@Test
	public void queryOverQueriesSingleWildcardResolvesUnqualifiedColumn() {
		String sql = "select col3 from (select * from tab1) aaa join (select col2 from tab2) bbb on aaa.col2 = bbb.col2";
		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runParsertest(sql, parser);
		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=col3, table_ref=null}}}, from={join={1={table={alias=aaa, query={select={1={column={name=*, table_ref=*}}}, from={table={alias=null, table=tab1}}}}}, 2={join=join, on={condition={left={column={name=col2, table_ref=aaa}}, right={column={name=col2, table_ref=bbb}}, operator==}}}, 3={table={alias=bbb, query={select={1={column={name=col2, table_ref=null}}}, from={table={alias=null, table=tab2}}}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[col3]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={*=[[@5,25:25='*',<291>,1:25]]}, tab2={col2=[[@13,55:58='col2',<381>,1:55]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@5,25:25='*',<291>,1:25]], col2=[[@19,78:80='aaa',<381>,1:78]]}, query1={col2=[[@13,55:58='col2',<381>,1:55], [@23,89:91='bbb',<381>,1:89]]}, query2={col3=[[@1,7:10='col3',<381>,1:7]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query2={query_dictionary={col3=[[@1,7:10='col3',<381>,1:7]]}, table_dictionary={}, def_query1={query_dictionary={col2=[[@13,55:58='col2',<381>,1:55], [@23,89:91='bbb',<381>,1:89]]}, table_dictionary={tab2={col2=[[@13,55:58='col2',<381>,1:55]]}}, interface={col2=[{name=col2, table_ref=tab2}]}}, def_query0={query_dictionary={*=[[@5,25:25='*',<291>,1:25]], col2=[[@19,78:80='aaa',<381>,1:78]]}, table_dictionary={tab1={*=[[@5,25:25='*',<291>,1:25]]}}, interface={*=[{name=*, table_ref=*}]}}, filters=[{name=col2, table_ref=aaa}, {name=col2, table_ref=bbb}], interface={col3=[{name=col3, table_ref=query0}]}, table_alias={aaa=query0, bbb=query1}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void queryOverQueriesMultipleWildcardsRemainAmbiguous() {
		String sql = "select col3 from (select * from tab1) aaa join (select * from tab2) bbb on aaa.col1 = bbb.col1";
		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runParsertest(sql, parser);

		Snippet snippet = extractor.getSnippet();
		assertFatalDiagnosticCount(snippet, null, null, null, 0);
		assertDiagnosticCountBySeverity(snippet,
				"UNRESOLVED_UNQUALIFIED_COLUMNS",
				ParseDiagnostic.Severity.ERROR,
				null,
				null,
				1);
		assertDiagnosticCountBySeverity(snippet,
				"AMBIGUOUS_COLUMN_REFERENCE",
				ParseDiagnostic.Severity.SEVERE_WARNING,
				null,
				"col3",
				1);
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
		Snippet snippet = extractor.getSnippet();
		assertDiagnosticCountBySeverity(snippet, "UNRESOLVED_UNQUALIFIED_COLUMNS", ParseDiagnostic.Severity.ERROR, null, null, 1);
		assertDiagnosticCountBySeverity(snippet, "AMBIGUOUS_COLUMN_REFERENCE", ParseDiagnostic.Severity.SEVERE_WARNING, "(l:3 c:49)", "active_ind", 1);
	}


	@Test
	public void selectWithMultipleSimpleUnqualifiedReferencesCTEV2() {
		String sql = "WITH " 
			+ "\n aaa AS  (select col1, col3 from tab1), "
			+ "\n bbb AS (select col2, col3 from tab2)"
			+ "\n Select  col1, col2, col3 FROM aaa join bbb on aaa.col1 = bbb.col2";
		final SQLSelectParserParser parser = parse(sql);
		
		SqlParseEventWalker extractor = runParsertest(sql, parser);
		Snippet snippet = extractor.getSnippet();
		assertDiagnosticCountBySeverity(snippet, "UNRESOLVED_UNQUALIFIED_COLUMNS", ParseDiagnostic.Severity.ERROR, "col3", null, 1);
		assertDiagnosticCountBySeverity(snippet, "AMBIGUOUS_COLUMN_REFERENCE", ParseDiagnostic.Severity.SEVERE_WARNING, null, "col3", 1);
	}


	@Test
	public void queryAndUnionUnqualifiedReferencesCTEV3() {
		String sql = "WITH " 
			+ "\n aaa AS  (select col1 from tab1), "
			+ "\n bbb AS (select col1 from  answer union select col2 from problem)"
			+ "\n Select  col1, col2 FROM aaa join bbb on aaa.col1 = bbb.col2";
		final SQLSelectParserParser parser = parse(sql);
		
		SqlParseEventWalker extractor = runParsertest(sql, parser);
		Snippet snippet = extractor.getSnippet();
		assertFatalDiagnosticCount(snippet,
				"QUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIAS",
				"Qualified column 'col2'",
				"bbb.col2",
				1);
		assertFatalDiagnosticCount(snippet,
				"UNQUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIASES",
				"Unqualified column 'col2'",
				"col2",
				1);
		assertFatalDiagnosticCount(snippet,
				null,
				null,
				null,
				2);
		assertDiagnosticCountBySeverity(snippet, "UNRESOLVED_UNQUALIFIED_COLUMNS", ParseDiagnostic.Severity.ERROR, "col2", null, 1);
		assertDiagnosticCountBySeverity(snippet, "AMBIGUOUS_COLUMN_REFERENCE", ParseDiagnostic.Severity.SEVERE_WARNING, null, "col1", 1);
		assertDiagnosticCountBySeverity(snippet, "AMBIGUOUS_COLUMN_REFERENCE", ParseDiagnostic.Severity.SEVERE_WARNING, null, "col2", 0);
	}


	@Test
	public void unionAndQueryUnqualifiedReferencesCTEV4() {
		String sql = "WITH " 
			+ "\n aaa AS  (select col1 from  answer union select col2 from problem), "
			+ "\n bbb AS (select col2 from tab2)"
			+ "\n Select  col1, col2 FROM aaa join bbb on aaa.col1 = bbb.col2";
		final SQLSelectParserParser parser = parse(sql);
		
		SqlParseEventWalker extractor = runParsertest(sql, parser);

		Assert.assertEquals("AST is wrong", "{SQL={with={1={cte={union={1={select={1={column={name=col1, table_ref=null}}}, from={table={alias=null, table=answer}}}, 2={union={qualifier=null, operator=union}}, 3={select={1={column={name=col2, table_ref=null}}}, from={table={alias=null, table=problem}}}}}, alias=aaa}, 2={cte={select={1={column={name=col2, table_ref=null}}}, from={table={alias=null, table=tab2}}}, alias=bbb}}, query={select={1={column={name=col1, table_ref=null}}, 2={column={name=col2, table_ref=null}}}, from={join={1={table={alias=null, table=aaa}}, 2={join=join, on={condition={left={column={name=col1, table_ref=aaa}}, right={column={name=col2, table_ref=bbb}}, operator==}}}, 3={table={alias=null, table=bbb}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[col2, col1]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{problem={col2=[[@10,54:57='col2',<381>,2:48]]}, answer={col1=[[@5,23:26='col1',<381>,2:17]]}, tab2={col2=[[@19,91:94='col2',<381>,3:16], [@36,159:161='bbb',<381>,4:52]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query4={col2=[[@26,122:125='col2',<381>,4:15]], col1=[[@24,116:119='col1',<381>,4:9]]}, query0={col1=[[@5,23:26='col1',<381>,2:17]]}, query1={col2=[[@10,54:57='col2',<381>,2:48]]}, query3={col2=[[@19,91:94='col2',<381>,3:16], [@36,159:161='bbb',<381>,4:52]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query4={context_list={aaa=union2, bbb=query3}, query_dictionary={col2=[[@26,122:125='col2',<381>,4:15]], col1=[[@24,116:119='col1',<381>,4:9]]}, def_union2={query0={query_dictionary={col1=[[@5,23:26='col1',<381>,2:17]]}, table_dictionary={answer={col1=[[@5,23:26='col1',<381>,2:17]]}}, interface={col1=[{name=col1, table_ref=answer}]}}, interface={col1=[{name=col1, table_ref=answer}, {name=col2, table_ref=problem}]}, query1={query_dictionary={col2=[[@10,54:57='col2',<381>,2:48]]}, table_dictionary={problem={col2=[[@10,54:57='col2',<381>,2:48]]}}, interface={col2=[{name=col2, table_ref=problem}]}}}, table_dictionary={}, filters=[{name=col1, table_ref=aaa}, {name=col2, table_ref=bbb}], interface={col2=[{name=col2, table_ref=query3}], col1=[{name=col1, table_ref=union2}]}, table_alias={aaa=union2, bbb=query3}, def_query3={context_list={aaa=union2}, query_dictionary={col2=[[@19,91:94='col2',<381>,3:16], [@36,159:161='bbb',<381>,4:52]]}, table_dictionary={tab2={col2=[[@19,91:94='col2',<381>,3:16], [@36,159:161='bbb',<381>,4:52]]}}, interface={col2=[{name=col2, table_ref=tab2}]}, table_alias={aaa=union2}}}}",
				extractor.getSymbolTable().toString());

				Snippet snippet = extractor.getSnippet();
		assertDiagnosticCountBySeverity(snippet, "UNRESOLVED_UNQUALIFIED_COLUMNS", ParseDiagnostic.Severity.ERROR, null, null, 0);
	}


	@Test
	public void queryAndIntersectUnqualifiedReferencesCTEV5() {
		String sql = "WITH " 
			+ "\n aaa AS  (select col1 from tab1), "
			+ "\n bbb AS (select col1 from  answer intersect select col2 from problem)"
			+ "\n Select  col1, col2 FROM aaa join bbb on aaa.col1 = bbb.col2";
		final SQLSelectParserParser parser = parse(sql);
		
		SqlParseEventWalker extractor = runParsertest(sql, parser);

		Assert.assertEquals("AST is wrong", "{SQL={with={1={cte={select={1={column={name=col1, table_ref=null}}}, from={table={alias=null, table=tab1}}}, alias=aaa}, 2={cte={intersect={1={select={1={column={name=col1, table_ref=null}}}, from={table={alias=null, table=answer}}}, 2={intersect={qualifier=null, operator=intersect}}, 3={select={1={column={name=col2, table_ref=null}}}, from={table={alias=null, table=problem}}}}}, alias=bbb}}, query={select={1={column={name=col1, table_ref=null}}, 2={column={name=col2, table_ref=null}}}, from={join={1={table={alias=null, table=aaa}}, 2={join=join, on={condition={left={column={name=col1, table_ref=aaa}}, right={column={name=col2, table_ref=bbb}}, operator==}}}, 3={table={alias=null, table=bbb}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[col2, col1]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());

				Snippet snippet = extractor.getSnippet();
		assertFatalDiagnosticCount(snippet,
				"QUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIAS",
				"Qualified column 'col2'",
				"bbb.col2",
				1);
		assertFatalDiagnosticCount(snippet,
				"UNQUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIASES",
				"Unqualified column 'col2'",
				"col2",
				1);
		assertFatalDiagnosticCount(snippet,
				null,
				null,
				null,
				2);
		assertDiagnosticCountBySeverity(snippet, "UNRESOLVED_UNQUALIFIED_COLUMNS", ParseDiagnostic.Severity.ERROR, null, null, 1);
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
		Assert.assertEquals("Table Dictionary is wrong", "{problem={col2=[[@10,58:61='col2',<381>,2:52]]}, answer={col1=[[@5,23:26='col1',<381>,2:17]]}, tab2={col2=[[@19,95:98='col2',<381>,3:16], [@36,163:165='bbb',<381>,4:52]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query4={col2=[[@26,126:129='col2',<381>,4:15]], col1=[[@24,120:123='col1',<381>,4:9]]}, query0={col1=[[@5,23:26='col1',<381>,2:17]]}, query1={col2=[[@10,58:61='col2',<381>,2:52]]}, query3={col2=[[@19,95:98='col2',<381>,3:16], [@36,163:165='bbb',<381>,4:52]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query4={context_list={aaa=intersect2, bbb=query3}, query_dictionary={col2=[[@26,126:129='col2',<381>,4:15]], col1=[[@24,120:123='col1',<381>,4:9]]}, table_dictionary={}, def_intersect2={query0={query_dictionary={col1=[[@5,23:26='col1',<381>,2:17]]}, table_dictionary={answer={col1=[[@5,23:26='col1',<381>,2:17]]}}, interface={col1=[{name=col1, table_ref=answer}]}}, interface={col1=[{name=col1, table_ref=answer}, {name=col2, table_ref=problem}]}, query1={query_dictionary={col2=[[@10,58:61='col2',<381>,2:52]]}, table_dictionary={problem={col2=[[@10,58:61='col2',<381>,2:52]]}}, interface={col2=[{name=col2, table_ref=problem}]}}}, filters=[{name=col1, table_ref=aaa}, {name=col2, table_ref=bbb}], interface={col2=[{name=col2, table_ref=query3}], col1=[{name=col1, table_ref=intersect2}]}, table_alias={aaa=intersect2, bbb=query3}, def_query3={context_list={aaa=intersect2}, query_dictionary={col2=[[@19,95:98='col2',<381>,3:16], [@36,163:165='bbb',<381>,4:52]]}, table_dictionary={tab2={col2=[[@19,95:98='col2',<381>,3:16], [@36,163:165='bbb',<381>,4:52]]}}, interface={col2=[{name=col2, table_ref=tab2}]}, table_alias={aaa=intersect2}}}}",
				extractor.getSymbolTable().toString());

				Snippet snippet = extractor.getSnippet();
		assertDiagnosticCountBySeverity(snippet, "UNRESOLVED_UNQUALIFIED_COLUMNS", ParseDiagnostic.Severity.ERROR, null, null, 0);
	}


	@Test
	public void unionAndIntersectUnqualifiedReferencesCTEV7() {
		String sql = "WITH " 
			+ "\n aaa AS  (select col1 from  problem2 union select xyz from problem3), "
			+ "\n bbb AS (select col1 from  answer intersect select col2 from problem)"
			+ "\n Select  col1, col2 FROM aaa join bbb on aaa.col1 = bbb.col2";
		final SQLSelectParserParser parser = parse(sql);
		
		SqlParseEventWalker extractor = runParsertest(sql, parser);

		Assert.assertEquals("AST is wrong", "{SQL={with={1={cte={union={1={select={1={column={name=col1, table_ref=null}}}, from={table={alias=null, table=problem2}}}, 2={union={qualifier=null, operator=union}}, 3={select={1={column={name=xyz, table_ref=null}}}, from={table={alias=null, table=problem3}}}}}, alias=aaa}, 2={cte={intersect={1={select={1={column={name=col1, table_ref=null}}}, from={table={alias=null, table=answer}}}, 2={intersect={qualifier=null, operator=intersect}}, 3={select={1={column={name=col2, table_ref=null}}}, from={table={alias=null, table=problem}}}}}, alias=bbb}}, query={select={1={column={name=col1, table_ref=null}}, 2={column={name=col2, table_ref=null}}}, from={join={1={table={alias=null, table=aaa}}, 2={join=join, on={condition={left={column={name=col1, table_ref=aaa}}, right={column={name=col2, table_ref=bbb}}, operator==}}}, 3={table={alias=null, table=bbb}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[col2, col1]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());

				Snippet snippet = extractor.getSnippet();
		assertFatalDiagnosticCount(snippet,
				"QUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIAS",
				"Qualified column 'col2'",
				"bbb.col2",
				1);
		assertFatalDiagnosticCount(snippet,
				"UNQUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIASES",
				"Unqualified column 'col2'",
				"col2",
				1);
		assertFatalDiagnosticCount(snippet,
				null,
				null,
				null,
				2);
		assertDiagnosticCountBySeverity(snippet, "UNRESOLVED_UNQUALIFIED_COLUMNS", ParseDiagnostic.Severity.ERROR, null, null, 1);
		assertDiagnosticCountBySeverity(snippet, "AMBIGUOUS_COLUMN_REFERENCE", ParseDiagnostic.Severity.SEVERE_WARNING, null, "col1", 1);
		assertDiagnosticCountBySeverity(snippet, "AMBIGUOUS_COLUMN_REFERENCE", ParseDiagnostic.Severity.SEVERE_WARNING, null, "col2", 0);
	}


	@Test
	public void intersectAndUnionUnqualifiedReferencesCTEV8() {
		String sql = "WITH " 
			+ "\n aaa AS  (select col1 from  problem2 intersect select xyz from problem3), "
			+ "\n bbb AS (select col1 from  answer union select col2 from problem)"
			+ "\n Select  col1, col2 FROM aaa join bbb on aaa.col1 = bbb.col2";
		final SQLSelectParserParser parser = parse(sql);
		
		SqlParseEventWalker extractor = runParsertest(sql, parser);

		Assert.assertEquals("AST is wrong", "{SQL={with={1={cte={intersect={1={select={1={column={name=col1, table_ref=null}}}, from={table={alias=null, table=problem2}}}, 2={intersect={qualifier=null, operator=intersect}}, 3={select={1={column={name=xyz, table_ref=null}}}, from={table={alias=null, table=problem3}}}}}, alias=aaa}, 2={cte={union={1={select={1={column={name=col1, table_ref=null}}}, from={table={alias=null, table=answer}}}, 2={union={qualifier=null, operator=union}}, 3={select={1={column={name=col2, table_ref=null}}}, from={table={alias=null, table=problem}}}}}, alias=bbb}}, query={select={1={column={name=col1, table_ref=null}}, 2={column={name=col2, table_ref=null}}}, from={join={1={table={alias=null, table=aaa}}, 2={join=join, on={condition={left={column={name=col1, table_ref=aaa}}, right={column={name=col2, table_ref=bbb}}, operator==}}}, 3={table={alias=null, table=bbb}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[col2, col1]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{problem={col2=[[@24,128:131='col2',<381>,3:47]]}, answer={col1=[[@19,97:100='col1',<381>,3:16]]}, problem2={col1=[[@5,23:26='col1',<381>,2:17]]}, problem3={xyz=[[@10,60:62='xyz',<381>,2:54]]}}",
				extractor.getTableColumnDictionaryMap().toString());

				Snippet snippet = extractor.getSnippet();
		assertFatalDiagnosticCount(snippet,
				"QUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIAS",
				"Qualified column 'col2'",
				"bbb.col2",
				1);
		assertFatalDiagnosticCount(snippet,
				"UNQUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIASES",
				"Unqualified column 'col2'",
				"col2",
				1);
		assertFatalDiagnosticCount(snippet,
				null,
				null,
				null,
				2);
		assertDiagnosticCountBySeverity(snippet, "UNRESOLVED_UNQUALIFIED_COLUMNS", ParseDiagnostic.Severity.ERROR, null, null, 1);
		assertDiagnosticCountBySeverity(snippet, "AMBIGUOUS_COLUMN_REFERENCE", ParseDiagnostic.Severity.SEVERE_WARNING, null, "col1", 1);
		assertDiagnosticCountBySeverity(snippet, "AMBIGUOUS_COLUMN_REFERENCE", ParseDiagnostic.Severity.SEVERE_WARNING, null, "col2", 0);
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
		Assert.assertEquals("Table Dictionary is wrong", "{problem={col2=[[@10,54:57='col2',<381>,2:48]]}, answer={col1=[[@5,23:26='col1',<381>,2:17]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query4={col2=[[@49,174:177='col2',<381>,4:15]], col1=[[@47,168:171='col1',<381>,4:9]]}, query0={col1=[[@5,23:26='col1',<381>,2:17]]}, query1={col2=[[@10,54:57='col2',<381>,2:48]]}, values3={col2=[[@41,146:149='col2',<381>,3:71], [@59,211:213='bbb',<381>,4:52]], col3=[[@43,152:155='col3',<381>,3:77]], col1=[[@39,140:143='col1',<381>,3:65]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query4={context_list={aaa=union2, bbb=values3}, query_dictionary={col2=[[@49,174:177='col2',<381>,4:15]], col1=[[@47,168:171='col1',<381>,4:9]]}, def_union2={query0={query_dictionary={col1=[[@5,23:26='col1',<381>,2:17]]}, table_dictionary={answer={col1=[[@5,23:26='col1',<381>,2:17]]}}, interface={col1=[{name=col1, table_ref=answer}]}}, interface={col1=[{name=col1, table_ref=answer}, {name=col2, table_ref=problem}]}, query1={query_dictionary={col2=[[@10,54:57='col2',<381>,2:48]]}, table_dictionary={problem={col2=[[@10,54:57='col2',<381>,2:48]]}}, interface={col2=[{name=col2, table_ref=problem}]}}}, table_dictionary={}, def_values3={query_dictionary={col2=[[@41,146:149='col2',<381>,3:71], [@59,211:213='bbb',<381>,4:52]], col3=[[@43,152:155='col3',<381>,3:77]], col1=[[@39,140:143='col1',<381>,3:65]]}, table_dictionary={}, interface={col2=[], col3=[], col1=[]}}, filters=[{name=col1, table_ref=aaa}, {name=col2, table_ref=bbb}], interface={col2=[{name=col2, table_ref=values3}], col1=[{name=col1, table_ref=null}]}, table_alias={aaa=union2, bbb=values3}}}",
				extractor.getSymbolTable().toString());

				Snippet snippet = extractor.getSnippet();
		assertDiagnosticCountBySeverity(snippet, "UNRESOLVED_UNQUALIFIED_COLUMNS", ParseDiagnostic.Severity.ERROR, null, null, 1);
		assertDiagnosticCountBySeverity(snippet, "AMBIGUOUS_COLUMN_REFERENCE", ParseDiagnostic.Severity.SEVERE_WARNING, "(l:4 c:9)", "col1", 1);
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
		Assert.assertEquals("Table Dictionary is wrong", "{problem2={col2=[[@37,109:112='col2',<381>,3:16]]}, problem3={xyz=[[@42,146:148='xyz',<381>,3:53]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{values0={col2=[[@27,78:81='col2',<381>,2:72]], col3=[[@29,84:87='col3',<381>,2:78]], col1=[[@25,72:75='col1',<381>,2:66], [@55,206:208='aaa',<381>,4:41]]}, query4={col2=[[@49,180:183='col2',<381>,4:15]], col1=[[@47,174:177='col1',<381>,4:9]]}, query1={col2=[[@37,109:112='col2',<381>,3:16]]}, query2={xyz=[[@42,146:148='xyz',<381>,3:53]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query4={context_list={aaa=values0, bbb=intersect3}, query_dictionary={col2=[[@49,180:183='col2',<381>,4:15]], col1=[[@47,174:177='col1',<381>,4:9]]}, table_dictionary={}, def_values0={query_dictionary={col2=[[@27,78:81='col2',<381>,2:72]], col3=[[@29,84:87='col3',<381>,2:78]], col1=[[@25,72:75='col1',<381>,2:66], [@55,206:208='aaa',<381>,4:41]]}, table_dictionary={}, interface={col2=[], col3=[], col1=[]}}, def_intersect3={context_list={aaa=values0}, interface={col2=[{name=col2, table_ref=problem2}, {name=xyz, table_ref=problem3}]}, query1={context_list={aaa=values0}, query_dictionary={col2=[[@37,109:112='col2',<381>,3:16]]}, table_dictionary={problem2={col2=[[@37,109:112='col2',<381>,3:16]]}}, interface={col2=[{name=col2, table_ref=problem2}]}, table_alias={aaa=values0}}, table_alias={aaa=values0}, query2={context_list={aaa=values0}, query_dictionary={xyz=[[@42,146:148='xyz',<381>,3:53]]}, table_dictionary={problem3={xyz=[[@42,146:148='xyz',<381>,3:53]]}}, interface={xyz=[{name=xyz, table_ref=problem3}]}, table_alias={aaa=values0}}}, filters=[{name=col1, table_ref=aaa}, {name=col2, table_ref=bbb}], interface={col2=[{name=col2, table_ref=null}], col1=[{name=col1, table_ref=values0}]}, table_alias={aaa=values0, bbb=intersect3}}}",
				extractor.getSymbolTable().toString());

				Snippet snippet = extractor.getSnippet();
		assertDiagnosticCountBySeverity(snippet, "UNRESOLVED_UNQUALIFIED_COLUMNS", ParseDiagnostic.Severity.ERROR, null, null, 1);
		assertDiagnosticCountBySeverity(snippet, "AMBIGUOUS_COLUMN_REFERENCE", ParseDiagnostic.Severity.SEVERE_WARNING, "(l:4 c:15)", "col2", 1);
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
		Assert.assertEquals("Table Dictionary is wrong", "{}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{values0={col2=[[@27,78:81='col2',<381>,2:72]], col3=[[@29,84:87='col3',<381>,2:78]], col1=[[@25,72:75='col1',<381>,2:66], [@73,218:220='aaa',<381>,4:41]]}, values1={col2=[[@59,164:167='col2',<381>,3:71], [@77,229:231='bbb',<381>,4:52]], col3=[[@61,170:173='col3',<381>,3:77]], col1=[[@57,158:161='col1',<381>,3:65]]}, query2={col2=[[@67,192:195='col2',<381>,4:15]], col1=[[@65,186:189='col1',<381>,4:9]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query2={context_list={aaa=values0, bbb=values1}, query_dictionary={col2=[[@67,192:195='col2',<381>,4:15]], col1=[[@65,186:189='col1',<381>,4:9]]}, table_dictionary={}, def_values1={query_dictionary={col2=[[@59,164:167='col2',<381>,3:71], [@77,229:231='bbb',<381>,4:52]], col3=[[@61,170:173='col3',<381>,3:77]], col1=[[@57,158:161='col1',<381>,3:65]]}, table_dictionary={}, interface={col2=[], col3=[], col1=[]}}, def_values0={query_dictionary={col2=[[@27,78:81='col2',<381>,2:72]], col3=[[@29,84:87='col3',<381>,2:78]], col1=[[@25,72:75='col1',<381>,2:66], [@73,218:220='aaa',<381>,4:41]]}, table_dictionary={}, interface={col2=[], col3=[], col1=[]}}, filters=[{name=col1, table_ref=aaa}, {name=col2, table_ref=bbb}], interface={col2=[{name=col2, table_ref=null}], col1=[{name=col1, table_ref=null}]}, table_alias={aaa=values0, bbb=values1}}}",
				extractor.getSymbolTable().toString());

				Snippet snippet = extractor.getSnippet();
		assertDiagnosticCountBySeverity(snippet, "UNRESOLVED_UNQUALIFIED_COLUMNS", ParseDiagnostic.Severity.ERROR, null, null, 1);
		assertDiagnosticCountBySeverity(snippet, "AMBIGUOUS_COLUMN_REFERENCE", ParseDiagnostic.Severity.SEVERE_WARNING, null, "col1", 1);
		assertDiagnosticCountBySeverity(snippet, "AMBIGUOUS_COLUMN_REFERENCE", ParseDiagnostic.Severity.SEVERE_WARNING, null, "col2", 1);
	}


	@Test
	public void queryAndSubstitutionUnqualifiedReferencesCTEV12() {
		String sql = "WITH " 
			+ "\n aaa AS  (select col1 from tab1), "
			+ "\n bbb AS <substitution_2>"
			+ "\n Select  col1, col2 FROM aaa join bbb on aaa.col1 = bbb.col2";
		final SQLSelectParserParser parser = parse(sql);
		
		SqlParseEventWalker extractor = runParsertest(sql, parser);

		Assert.assertEquals("AST is wrong", "{SQL={with={1={cte={select={1={column={name=col1, table_ref=null}}}, from={table={alias=null, table=tab1}}}, alias=aaa}, 2={cte={substitution={name=<substitution_2>, type=tuple}}, alias=bbb}}, query={select={1={column={name=col1, table_ref=null}}, 2={column={name=col2, table_ref=null}}}, from={join={1={table={alias=null, table=aaa}}, 2={join=join, on={condition={left={column={name=col1, table_ref=aaa}}, right={column={name=col2, table_ref=bbb}}, operator==}}}, 3={table={alias=null, table=bbb}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[col2, col1]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<substitution_2>=tuple}", 
				extractor.getSubstitutionsMap().toString());

		Snippet snippet = extractor.getSnippet();
		assertFatalDiagnosticCount(snippet,
				"QUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIAS",
				"Qualified column 'col2'",
				"bbb.col2",
				1);
		assertFatalDiagnosticCount(snippet,
				"UNQUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIASES",
				"Unqualified column 'col2'",
				"col2",
				1);
		assertFatalDiagnosticCount(snippet,
				null,
				null,
				null,
				2);
		assertDiagnosticCountBySeverity(snippet, "UNRESOLVED_UNQUALIFIED_COLUMNS", ParseDiagnostic.Severity.ERROR, null, null, 1);
		assertDiagnosticCountBySeverity(snippet, "AMBIGUOUS_COLUMN_REFERENCE", ParseDiagnostic.Severity.SEVERE_WARNING, null, "col1", 0);
		assertDiagnosticCountBySeverity(snippet, "AMBIGUOUS_COLUMN_REFERENCE", ParseDiagnostic.Severity.SEVERE_WARNING, null, "col2", 0);
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
		Assert.assertEquals("Table Dictionary is wrong", "{tab2={col2=[[@9,49:52='col2',<381>,3:16], [@26,117:119='bbb',<381>,4:52]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query1={col2=[[@9,49:52='col2',<381>,3:16], [@26,117:119='bbb',<381>,4:52]]}, query2={col2=[[@16,80:83='col2',<381>,4:15]], col1=[[@14,74:77='col1',<381>,4:9]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query2={context_list={bbb=query1}, query_dictionary={col2=[[@16,80:83='col2',<381>,4:15]], col1=[[@14,74:77='col1',<381>,4:9]]}, table_dictionary={}, def_query1={query_dictionary={col2=[[@9,49:52='col2',<381>,3:16], [@26,117:119='bbb',<381>,4:52]]}, table_dictionary={tab2={col2=[[@9,49:52='col2',<381>,3:16], [@26,117:119='bbb',<381>,4:52]]}}, interface={col2=[{name=col2, table_ref=tab2}]}}, filters=[{name=col1, table_ref=aaa}, {name=col2, table_ref=bbb}], interface={col2=[{name=col2, table_ref=query1}], col1=[{name=col1, table_ref=null}]}, table_alias={bbb=query1}}}",
				extractor.getSymbolTable().toString());

		Snippet snippet = extractor.getSnippet();
		assertFatalDiagnosticCount(snippet,
				"QUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIAS",
				"Qualified column 'col1'",
				"aaa.col1",
				1);
		assertFatalDiagnosticCount(snippet,
				"UNQUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIASES",
				"Unqualified column 'col1'",
				"col1",
				1);
		assertFatalDiagnosticCount(snippet,
				null,
				null,
				null,
				2);
		assertDiagnosticCountBySeverity(snippet, "UNRESOLVED_UNQUALIFIED_COLUMNS", ParseDiagnostic.Severity.ERROR, null, null, 1);
		assertDiagnosticCountBySeverity(snippet, "AMBIGUOUS_COLUMN_REFERENCE", ParseDiagnostic.Severity.SEVERE_WARNING, null, "col1", 0);
		assertDiagnosticCountBySeverity(snippet, "AMBIGUOUS_COLUMN_REFERENCE", ParseDiagnostic.Severity.SEVERE_WARNING, null, "col2", 0);
	}


	@Test
	public void substitutionAndSubstitutionUnqualifiedReferencesCTEV14() {
		String sql = "WITH " 
			+ "\n aaa AS  <substitution_1>, "
			+ "\n bbb AS <substitution_2>"
			+ "\n Select  col1, col2 FROM aaa join bbb on aaa.col1 = bbb.col2";
		final SQLSelectParserParser parser = parse(sql);	
		SqlParseEventWalker extractor = runParsertest(sql, parser);

		Assert.assertEquals("AST is wrong", "{SQL={with={1={cte={substitution={name=<substitution_1>, type=tuple}}, alias=aaa}, 2={cte={substitution={name=<substitution_2>, type=tuple}}, alias=bbb}}, query={select={1={column={name=col1, table_ref=null}}, 2={column={name=col2, table_ref=null}}}, from={join={1={table={alias=null, table=aaa}}, 2={join=join, on={condition={left={column={name=col1, table_ref=aaa}}, right={column={name=col2, table_ref=bbb}}, operator==}}}, 3={table={alias=null, table=bbb}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[col2, col1]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<substitution_1>=tuple, <substitution_2>=tuple}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query2={col2=[[@11,74:77='col2',<381>,4:15]], col1=[[@9,68:71='col1',<381>,4:9]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query2={context_list={}, query_dictionary={col2=[[@11,74:77='col2',<381>,4:15]], col1=[[@9,68:71='col1',<381>,4:9]]}, table_dictionary={}, filters=[{name=col1, table_ref=aaa}, {name=col2, table_ref=bbb}], interface={col2=[{name=col2, table_ref=null}], col1=[{name=col1, table_ref=null}]}, table_alias={aaa=query0, bbb=query1}}}",
				extractor.getSymbolTable().toString());

		Snippet snippet = extractor.getSnippet();
		assertFatalDiagnosticCount(snippet,
				"QUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIAS",
				"Qualified column 'col1'",
				"aaa.col1",
				1);
		assertFatalDiagnosticCount(snippet,
				"QUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIAS",
				"Qualified column 'col2'",
				"bbb.col2",
				1);
		assertFatalDiagnosticCount(snippet,
				"UNQUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIASES",
				"Unqualified column 'col1'",
				"col1",
				1);
		assertFatalDiagnosticCount(snippet,
				"UNQUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIASES",
				"Unqualified column 'col2'",
				"col2",
				1);
		assertFatalDiagnosticCount(snippet,
				null,
				null,
				null,
				4);
		assertDiagnosticCountBySeverity(snippet, "UNRESOLVED_UNQUALIFIED_COLUMNS", ParseDiagnostic.Severity.ERROR, null, null, 1);
		assertDiagnosticCountBySeverity(snippet, "AMBIGUOUS_COLUMN_REFERENCE", ParseDiagnostic.Severity.SEVERE_WARNING, null, "col1", 0);
		assertDiagnosticCountBySeverity(snippet, "AMBIGUOUS_COLUMN_REFERENCE", ParseDiagnostic.Severity.SEVERE_WARNING, null, "col2", 0);
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
		assertNoWalkerDiagnostics(extractor);
		Snippet snippet = extractor.getSnippet();
		assertDiagnosticCountBySeverity(snippet, "UNRESOLVED_UNQUALIFIED_COLUMNS", ParseDiagnostic.Severity.ERROR, null, null, 0);
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
		Assert.assertEquals("Table Dictionary is wrong", "{sc1.tab1={col1=[[@5,23:26='col1',<381>,2:17], [@24,93:95='aaa',<381>,3:48], [@37,144:146='aaa',<381>,4:41]]}, sc2.tab1={col2=[[@16,61:64='col2',<381>,3:16], [@41,155:157='bbb',<381>,4:52]], col1=[[@22,86:89='col1',<381>,3:41]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={col1=[[@5,23:26='col1',<381>,2:17], [@24,93:95='aaa',<381>,3:48], [@37,144:146='aaa',<381>,4:41]]}, query1={col2=[[@16,61:64='col2',<381>,3:16], [@41,155:157='bbb',<381>,4:52]]}, query2={col2=[[@31,118:121='col2',<381>,4:15]], col1=[[@29,112:115='col1',<381>,4:9]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query2={context_list={aaa=query0, bbb=query1}, query_dictionary={col2=[[@31,118:121='col2',<381>,4:15]], col1=[[@29,112:115='col1',<381>,4:9]]}, table_dictionary={}, def_query1={context_list={aaa=query0}, query_dictionary={col2=[[@16,61:64='col2',<381>,3:16], [@41,155:157='bbb',<381>,4:52]]}, table_dictionary={sc2.tab1={col2=[[@16,61:64='col2',<381>,3:16], [@41,155:157='bbb',<381>,4:52]], col1=[[@22,86:89='col1',<381>,3:41]]}}, filters=[{name=col1, table_ref=sc2.tab1}, {name=col1, table_ref=aaa}], interface={col2=[{name=col2, table_ref=sc2.tab1}]}, table_alias={aaa=query0}}, def_query0={query_dictionary={col1=[[@5,23:26='col1',<381>,2:17], [@24,93:95='aaa',<381>,3:48], [@37,144:146='aaa',<381>,4:41]]}, table_dictionary={sc1.tab1={col1=[[@5,23:26='col1',<381>,2:17], [@24,93:95='aaa',<381>,3:48], [@37,144:146='aaa',<381>,4:41]]}}, interface={col1=[{name=col1, table_ref=sc1.tab1}]}}, filters=[{name=col1, table_ref=aaa}, {name=col2, table_ref=bbb}], interface={col2=[{name=col2, table_ref=query1}], col1=[{name=col1, table_ref=query0}]}, table_alias={aaa=query0, bbb=query1}}}",
				extractor.getSymbolTable().toString());

		assertNoFatalErrors(extractor);
		Snippet snippet = extractor.getSnippet();
		assertDiagnosticCountBySeverity(snippet, "UNRESOLVED_UNQUALIFIED_COLUMNS", ParseDiagnostic.Severity.ERROR, null, null, 0);
	}

	@Test
	public void sameTableDifferentSchemaQualifiedReferencesCTEV1() {
		final String sql = "with aaa as (\n"
				+ "  select cs.key, str.product,\n"
				+ "         cs.segment_id, str.first_dt\n"
				+ "  from constr as cs join str on cs.stream_key = str.stream_key\n"
				+ "), union_bbb_ccc as (\n"
				+ "  select bbb.key, bbb.product, bbb.segment_id, bbb.first_dt "
				+ "  from csps as bbb join aaa on bbb.segment_id = aaa.segment_id\n"
				+ "  union\n"
				+ "  select ccc.ccckey, ccc.cccproduct, ccc.cccsegment_id, ccc.first_dt "
				+ "  from csps2 as ccc join aaa on ccc.cccsegment_id = aaa.segment_id\n"
				+ ")\n"
				+ "select ufla.key, ufla.segment_id, aaa.first_dt, ufla.prod "
				+ " from union_bbb_ccc as ufla join aaa "
				+ " on ufla.segment_id = aaa.segment_id";

		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runParsertest(sql, parser);

		Assert.assertEquals("AST is wrong", "{SQL={with={1={cte={select={1={column={name=key, table_ref=cs}}, 2={column={name=product, table_ref=str}}, 3={column={name=segment_id, table_ref=cs}}, 4={column={name=first_dt, table_ref=str}}}, from={join={1={table={alias=cs, table=constr}}, 2={join=join, on={condition={left={column={name=stream_key, table_ref=cs}}, right={column={name=stream_key, table_ref=str}}, operator==}}}, 3={table={alias=null, table=str}}}}}, alias=aaa}, 2={cte={union={1={select={1={column={name=key, table_ref=bbb}}, 2={column={name=product, table_ref=bbb}}, 3={column={name=segment_id, table_ref=bbb}}, 4={column={name=first_dt, table_ref=bbb}}}, from={join={1={table={alias=bbb, table=csps}}, 2={join=join, on={condition={left={column={name=segment_id, table_ref=bbb}}, right={column={name=segment_id, table_ref=aaa}}, operator==}}}, 3={table={alias=null, table=aaa}}}}}, 2={union={qualifier=null, operator=union}}, 3={select={1={column={name=ccckey, table_ref=ccc}}, 2={column={name=cccproduct, table_ref=ccc}}, 3={column={name=cccsegment_id, table_ref=ccc}}, 4={column={name=first_dt, table_ref=ccc}}}, from={join={1={table={alias=ccc, table=csps2}}, 2={join=join, on={condition={left={column={name=cccsegment_id, table_ref=ccc}}, right={column={name=segment_id, table_ref=aaa}}, operator==}}}, 3={table={alias=null, table=aaa}}}}}}}, alias=union_bbb_ccc}}, query={select={1={column={name=key, table_ref=ufla}}, 2={column={name=segment_id, table_ref=ufla}}, 3={column={name=first_dt, table_ref=aaa}}, 4={column={name=prod, table_ref=ufla}}}, from={join={1={table={alias=ufla, table=union_bbb_ccc}}, 2={join=join, on={condition={left={column={name=segment_id, table_ref=ufla}}, right={column={name=segment_id, table_ref=aaa}}, operator==}}}, 3={table={alias=null, table=aaa}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[prod, first_dt, segment_id, key]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{str={product=[[@9,31:33='str',<381>,2:17]], first_dt=[[@17,68:70='str',<381>,3:24], [@110,469:471='aaa',<381>,10:34]], stream_key=[[@31,129:131='str',<381>,4:48]]}, constr={segment_id=[[@13,53:54='cs',<381>,3:9], [@66,274:276='aaa',<381>,6:108], [@97,418:420='aaa',<381>,8:121], [@128,552:554='aaa',<381>,10:117]], key=[[@5,23:24='cs',<381>,2:9]], stream_key=[[@27,113:114='cs',<381>,4:32]]}, csps2={cccsegment_id=[[@79,334:336='ccc',<381>,8:37], [@93,398:400='ccc',<381>,8:101]], cccproduct=[[@75,318:320='ccc',<381>,8:21]], first_dt=[[@83,353:355='ccc',<381>,8:56]], ccckey=[[@71,306:308='ccc',<381>,8:9]]}, csps={product=[[@44,184:186='bbb',<381>,6:18]], first_dt=[[@52,213:215='bbb',<381>,6:47]], segment_id=[[@48,197:199='bbb',<381>,6:31], [@62,257:259='bbb',<381>,6:91]], key=[[@40,175:177='bbb',<381>,6:9]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query4={prod=[[@116,488:491='prod',<381>,10:53]], first_dt=[[@112,473:480='first_dt',<381>,10:38]], segment_id=[[@108,457:466='segment_id',<381>,10:22]], key=[[@104,447:449='key',<381>,10:12]]}, query0={product=[[@11,35:41='product',<381>,2:21]], first_dt=[[@19,72:79='first_dt',<381>,3:28], [@110,469:471='aaa',<381>,10:34]], segment_id=[[@15,56:65='segment_id',<381>,3:12], [@66,274:276='aaa',<381>,6:108], [@97,418:420='aaa',<381>,8:121], [@128,552:554='aaa',<381>,10:117]], key=[[@7,26:28='key',<381>,2:12]]}, query1={product=[[@46,188:194='product',<381>,6:22]], first_dt=[[@54,217:224='first_dt',<381>,6:51]], segment_id=[[@50,201:210='segment_id',<381>,6:35]], key=[[@42,179:181='key',<381>,6:13]]}, query2={cccsegment_id=[[@81,338:350='cccsegment_id',<381>,8:41]], cccproduct=[[@77,322:331='cccproduct',<381>,8:25]], first_dt=[[@85,357:364='first_dt',<381>,8:60]], ccckey=[[@73,310:315='ccckey',<381>,8:13]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query4={def_union3={context_list={aaa=query0}, interface={product=[{name=product, table_ref=bbb}, {name=cccproduct, table_ref=ccc}], first_dt=[{name=first_dt, table_ref=bbb}, {name=first_dt, table_ref=ccc}], segment_id=[{name=segment_id, table_ref=bbb}, {name=cccsegment_id, table_ref=ccc}], key=[{name=key, table_ref=bbb}, {name=ccckey, table_ref=ccc}]}, query1={context_list={aaa=query0}, query_dictionary={product=[[@46,188:194='product',<381>,6:22]], first_dt=[[@54,217:224='first_dt',<381>,6:51]], segment_id=[[@50,201:210='segment_id',<381>,6:35]], key=[[@42,179:181='key',<381>,6:13]]}, table_dictionary={csps={product=[[@44,184:186='bbb',<381>,6:18]], first_dt=[[@52,213:215='bbb',<381>,6:47]], segment_id=[[@48,197:199='bbb',<381>,6:31], [@62,257:259='bbb',<381>,6:91]], key=[[@40,175:177='bbb',<381>,6:9]]}}, filters=[{name=segment_id, table_ref=bbb}, {name=segment_id, table_ref=aaa}], interface={product=[{name=product, table_ref=bbb}], first_dt=[{name=first_dt, table_ref=bbb}], segment_id=[{name=segment_id, table_ref=bbb}], key=[{name=key, table_ref=bbb}]}, table_alias={aaa=query0, bbb=csps}}, table_alias={aaa=query0}, query2={context_list={aaa=query0}, query_dictionary={cccsegment_id=[[@81,338:350='cccsegment_id',<381>,8:41]], cccproduct=[[@77,322:331='cccproduct',<381>,8:25]], first_dt=[[@85,357:364='first_dt',<381>,8:60]], ccckey=[[@73,310:315='ccckey',<381>,8:13]]}, table_dictionary={csps2={cccsegment_id=[[@79,334:336='ccc',<381>,8:37], [@93,398:400='ccc',<381>,8:101]], cccproduct=[[@75,318:320='ccc',<381>,8:21]], first_dt=[[@83,353:355='ccc',<381>,8:56]], ccckey=[[@71,306:308='ccc',<381>,8:9]]}}, filters=[{name=cccsegment_id, table_ref=ccc}, {name=segment_id, table_ref=aaa}], interface={cccsegment_id=[{name=cccsegment_id, table_ref=ccc}], cccproduct=[{name=cccproduct, table_ref=ccc}], first_dt=[{name=first_dt, table_ref=ccc}], ccckey=[{name=ccckey, table_ref=ccc}]}, table_alias={aaa=query0, ccc=csps2}}}, context_list={aaa=query0, union_bbb_ccc=union3, ufla=union3}, query_dictionary={prod=[[@116,488:491='prod',<381>,10:53]], first_dt=[[@112,473:480='first_dt',<381>,10:38]], segment_id=[[@108,457:466='segment_id',<381>,10:22]], key=[[@104,447:449='key',<381>,10:12]]}, table_dictionary={}, def_query0={query_dictionary={product=[[@11,35:41='product',<381>,2:21]], first_dt=[[@19,72:79='first_dt',<381>,3:28], [@110,469:471='aaa',<381>,10:34]], segment_id=[[@15,56:65='segment_id',<381>,3:12], [@66,274:276='aaa',<381>,6:108], [@97,418:420='aaa',<381>,8:121], [@128,552:554='aaa',<381>,10:117]], key=[[@7,26:28='key',<381>,2:12]]}, table_dictionary={constr={segment_id=[[@13,53:54='cs',<381>,3:9], [@66,274:276='aaa',<381>,6:108], [@97,418:420='aaa',<381>,8:121], [@128,552:554='aaa',<381>,10:117]], key=[[@5,23:24='cs',<381>,2:9]], stream_key=[[@27,113:114='cs',<381>,4:32]]}, str={product=[[@9,31:33='str',<381>,2:17]], first_dt=[[@17,68:70='str',<381>,3:24], [@110,469:471='aaa',<381>,10:34]], stream_key=[[@31,129:131='str',<381>,4:48]]}}, filters=[{name=stream_key, table_ref=cs}, {name=stream_key, table_ref=str}], interface={product=[{name=product, table_ref=str}], first_dt=[{name=first_dt, table_ref=str}], segment_id=[{name=segment_id, table_ref=cs}], key=[{name=key, table_ref=cs}]}, table_alias={cs=constr}}, filters=[{name=segment_id, table_ref=ufla}, {name=segment_id, table_ref=aaa}], interface={prod=[{name=prod, table_ref=ufla}], first_dt=[{name=first_dt, table_ref=aaa}], segment_id=[{name=segment_id, table_ref=ufla}], key=[{name=key, table_ref=ufla}]}, table_alias={aaa=query0, union_bbb_ccc=union3}}}",
				extractor.getSymbolTable().toString());

		String symbolSnapshot = extractor.getSymbolTable().toString();
		Assert.assertTrue("Expected query4 scope in symbol table", symbolSnapshot.contains("query4={"));
		Assert.assertTrue(
				"Expected query4 context_list to retain aaa, union_bbb_ccc, and ufla mappings",
				symbolSnapshot.contains("context_list={aaa=query0, union_bbb_ccc=union3, ufla=union3}"));

		Snippet snippet = extractor.getSnippet();
		assertFatalDiagnosticAtPosition(snippet,
				"QUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIAS",
				"Qualified column 'prod' at (l:10 c:48) was not found in output interface of query alias 'ufla'.",
				"ufla.prod",
				10,
				48);
	}


	@Test
	public void intersectAndQueryQualifiedReferencesOutOfSequenceWithAliasesCTEV1() {
		String sql = "WITH " 
			+ "\n bbb AS (select tab2.col2 from tab2), "
			+ "\n aaa AS  (select fff.col1 from  answer as fff intersect select eee.col2 from bbb as eee) "
			+ "\n Select  aaa.col1, bbb.col2 FROM aaa join bbb on aaa.col1 = bbb.col2";
		final SQLSelectParserParser parser = parse(sql);
		
		SqlParseEventWalker extractor = runParsertest(sql, parser);
		assertNoFatalErrors(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={with={1={cte={select={1={column={name=col2, table_ref=tab2}}}, from={table={alias=null, table=tab2}}}, alias=bbb}, 2={cte={intersect={1={select={1={column={name=col1, table_ref=fff}}}, from={table={alias=fff, table=answer}}}, 2={intersect={qualifier=null, operator=intersect}}, 3={select={1={column={name=col2, table_ref=eee}}}, from={table={alias=eee, table=bbb}}}}}, alias=aaa}}, query={select={1={column={name=col1, table_ref=aaa}}, 2={column={name=col2, table_ref=bbb}}}, from={join={1={table={alias=null, table=aaa}}, 2={join=join, on={condition={left={column={name=col1, table_ref=aaa}}, right={column={name=col2, table_ref=bbb}}, operator==}}}, 3={table={alias=null, table=bbb}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[col2, col1]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{answer={col1=[[@16,62:64='fff',<381>,3:17]]}, tab2={col2=[[@5,22:25='tab2',<381>,2:16], [@25,108:110='eee',<381>,3:63], [@38,154:156='bbb',<381>,4:19], [@50,195:197='bbb',<381>,4:60]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query4={col2=[[@40,158:161='col2',<381>,4:23]], col1=[[@36,148:151='col1',<381>,4:13]]}, query0={col2=[[@7,27:30='col2',<381>,2:21], [@25,108:110='eee',<381>,3:63], [@38,154:156='bbb',<381>,4:19], [@50,195:197='bbb',<381>,4:60]]}, query1={col1=[[@18,66:69='col1',<381>,3:21]]}, query2={col2=[[@27,112:115='col2',<381>,3:67]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query4={context_list={bbb=query0, aaa=intersect3}, query_dictionary={col2=[[@40,158:161='col2',<381>,4:23]], col1=[[@36,148:151='col1',<381>,4:13]]}, table_dictionary={}, def_intersect3={context_list={bbb=query0}, interface={col1=[{name=col1, table_ref=fff}, {name=col2, table_ref=eee}]}, query1={context_list={bbb=query0}, query_dictionary={col1=[[@18,66:69='col1',<381>,3:21]]}, table_dictionary={answer={col1=[[@16,62:64='fff',<381>,3:17]]}}, interface={col1=[{name=col1, table_ref=fff}]}, table_alias={bbb=query0, fff=answer}}, table_alias={bbb=query0}, query2={context_list={bbb=query0, eee=query0}, query_dictionary={col2=[[@27,112:115='col2',<381>,3:67]]}, table_dictionary={}, interface={col2=[{name=col2, table_ref=eee}]}, table_alias={bbb=query0, eee=query0}}}, def_query0={query_dictionary={col2=[[@7,27:30='col2',<381>,2:21], [@25,108:110='eee',<381>,3:63], [@38,154:156='bbb',<381>,4:19], [@50,195:197='bbb',<381>,4:60]]}, table_dictionary={tab2={col2=[[@5,22:25='tab2',<381>,2:16], [@25,108:110='eee',<381>,3:63], [@38,154:156='bbb',<381>,4:19], [@50,195:197='bbb',<381>,4:60]]}}, interface={col2=[{name=col2, table_ref=tab2}]}}, filters=[{name=col1, table_ref=aaa}, {name=col2, table_ref=bbb}], interface={col2=[{name=col2, table_ref=bbb}], col1=[{name=col1, table_ref=aaa}]}, table_alias={aaa=intersect3, bbb=query0}}}",
				extractor.getSymbolTable().toString());
	}

	// NESTED WITH TESTS
	@Test
	public void nestedWithExistsCarriesCteListAaaBbbThenCccDddEee() {
		final String sql = "WITH "
				+ "\n aaa_bbb AS ("
				+ "\n   WITH "
				+ "\n     aaa AS (SELECT a1, a2, a3 FROM tab1),"
				+ "\n     bbb AS (SELECT b1, b2, b3 FROM tab2 WHERE EXISTS (SELECT 1 FROM aaa WHERE aaa.a3 = tab2.b3))"
				+ "\n   SELECT aaa.a1 AS a1, bbb.b1 AS b1, bbb.b2 AS b2"
				+ "\n   FROM aaa JOIN bbb ON aaa.a1 = bbb.b1"
				+ "\n ),"
				+ "\n ccc AS (SELECT c1, c2, c3 FROM tab3 WHERE EXISTS (SELECT 1 FROM aaa_bbb WHERE aaa_bbb.b2 = tab3.c2)),"
				+ "\n ddd AS (SELECT d1, d2, d3 FROM tab4 WHERE EXISTS (SELECT 1 FROM ccc WHERE ccc.c3 = tab4.d3)),"
				+ "\n eee AS (SELECT e1, e2, e3 FROM tab5 WHERE EXISTS (SELECT 1 FROM ddd WHERE ddd.d3 = tab5.e3))"
				+ "\n SELECT www.a1, xxx.c2, yyy.d2, zzz.e2"
				+ "\n FROM aaa_bbb AS www"
				+ "\n JOIN ccc AS xxx ON www.a1 = xxx.c1"
				+ "\n JOIN ddd AS yyy ON xxx.c1 = yyy.d1"
				+ "\n JOIN eee AS zzz ON yyy.d1 = zzz.e1";

		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runParsertest(sql, parser);
		assertNoWalkerDiagnostics(extractor);
	}

	@Test
	public void nestedWithExistsCarriesCteListAaaThenBbbCccThenDddEee() {
		final String sql = "WITH "
				+ "\n aaa AS (SELECT a1, a2, a3 FROM tab1),"
				+ "\n bbb_ccc AS ("
				+ "\n   WITH "
				+ "\n     bbb AS (SELECT b1, b2, b3 FROM tab2 WHERE EXISTS (SELECT 1 FROM aaa WHERE aaa.a3 = tab2.b3)),"
				+ "\n     ccc AS (SELECT c1, c2, c3 FROM tab3 WHERE EXISTS (SELECT 1 FROM bbb WHERE bbb.b3 = tab3.c3))"
				+ "\n   SELECT bbb.b1 AS b1, ccc.c1 AS c1, ccc.c2 AS c2"
				+ "\n   FROM bbb JOIN ccc ON bbb.b1 = ccc.c1"
				+ "\n ),"
				+ "\n ddd AS (SELECT d1, d2, d3 FROM tab4 WHERE EXISTS (SELECT 1 FROM bbb_ccc WHERE bbb_ccc.c2 = tab4.d2)),"
				+ "\n eee AS (SELECT e1, e2, e3 FROM tab5 WHERE EXISTS (SELECT 1 FROM ddd WHERE ddd.d3 = tab5.e3))"
				+ "\n SELECT www.a1, xxx.c2, yyy.d2, zzz.e2"
				+ "\n FROM aaa AS www"
				+ "\n JOIN bbb_ccc AS xxx ON www.a1 = xxx.b1"
				+ "\n JOIN ddd AS yyy ON xxx.c1 = yyy.d1"
				+ "\n JOIN eee AS zzz ON yyy.d1 = zzz.e1";

		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runParsertest(sql, parser);
		assertNoWalkerDiagnostics(extractor);
	}

	@Test
	public void nestedWithExistsCarriesCteListAaaBbbThenCccDddThenEee() {
		final String sql = "WITH "
				+ "\n aaa AS (SELECT a1, a2, a3 FROM tab1),"
				+ "\n bbb AS (SELECT b1, b2, b3 FROM tab2 WHERE EXISTS (SELECT 1 FROM aaa WHERE aaa.a3 = tab2.b3)),"
				+ "\n ccc_ddd AS ("
				+ "\n   WITH "
				+ "\n     ccc AS (SELECT c1, c2, c3 FROM tab3 WHERE EXISTS (SELECT 1 FROM bbb WHERE bbb.b3 = tab3.c3)),"
				+ "\n     ddd AS (SELECT d1, d2, d3 FROM tab4 WHERE EXISTS (SELECT 1 FROM ccc WHERE ccc.c3 = tab4.d3))"
				+ "\n   SELECT ccc.c1 AS c1, ddd.d1 AS d1, ddd.d2 AS d2"
				+ "\n   FROM ccc JOIN ddd ON ccc.c1 = ddd.d1"
				+ "\n ),"
				+ "\n eee AS (SELECT e1, e2, e3 FROM tab5 WHERE EXISTS (SELECT 1 FROM ccc_ddd WHERE ccc_ddd.d2 = tab5.e2))"
				+ "\n SELECT www.a1, xxx.b2, yyy.d2, zzz.e2"
				+ "\n FROM aaa AS www"
				+ "\n JOIN bbb AS xxx ON www.a1 = xxx.b1"
				+ "\n JOIN ccc_ddd AS yyy ON xxx.b1 = yyy.c1"
				+ "\n JOIN eee AS zzz ON yyy.d1 = zzz.e1";

		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runParsertest(sql, parser);
		assertNoWalkerDiagnostics(extractor);
	}

	@Test
	public void nestedWithExistsCarriesCteListAaaBbbCccThenDddEee() {
		final String sql = "WITH "
				+ "\n aaa AS (SELECT a1, a2, a3 FROM tab1),"
				+ "\n bbb AS (SELECT b1, b2, b3 FROM tab2 WHERE EXISTS (SELECT 1 FROM aaa WHERE aaa.a3 = tab2.b3)),"
				+ "\n ccc AS (SELECT c1, c2, c3 FROM tab3 WHERE EXISTS (SELECT 1 FROM bbb WHERE bbb.b3 = tab3.c3)),"
				+ "\n ddd_eee AS ("
				+ "\n   WITH "
				+ "\n     ddd AS (SELECT d1, d2, d3 FROM tab4 WHERE EXISTS (SELECT 1 FROM ccc WHERE ccc.c3 = tab4.d3)),"
				+ "\n     eee AS (SELECT e1, e2, e3 FROM tab5 WHERE EXISTS (SELECT 1 FROM ddd WHERE ddd.d3 = tab5.e3))"
				+ "\n   SELECT ddd.d1 AS d1, eee.e1 AS e1, eee.e2 AS e2"
				+ "\n   FROM ddd JOIN eee ON ddd.d1 = eee.e1"
				+ "\n )"
				+ "\n SELECT www.a1, xxx.b2, yyy.c2, zzz.e2"
				+ "\n FROM aaa AS www"
				+ "\n JOIN bbb AS xxx ON www.a1 = xxx.b1"
				+ "\n JOIN ccc AS yyy ON xxx.b1 = yyy.c1"
				+ "\n JOIN ddd_eee AS zzz ON yyy.c1 = zzz.d1";

		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runParsertest(sql, parser);
		assertNoWalkerDiagnostics(extractor);
	}

	@Test
	public void nestedWithInnerJoinAaaBbbThenCccDddEeeParsesWithoutErrors() {
		final String sql = "WITH "
				+ "\n aaa_bbb AS ("
				+ "\n   WITH "
				+ "\n     aaa AS (SELECT a1, a2, a3 FROM tab1),"
				+ "\n     bbb AS (SELECT bbb_src.b1, bbb_src.b2, bbb_src.b3 FROM tab2 AS bbb_src JOIN aaa ON aaa.a3 = bbb_src.b3)"
				+ "\n   SELECT aaa.a1 AS a1, bbb.b1 AS b1, bbb.b2 AS b2"
				+ "\n   FROM aaa JOIN bbb ON aaa.a1 = bbb.b1"
				+ "\n ),"
				+ "\n ccc AS (SELECT tab3.c1, tab3.c2, tab3.c3 FROM tab3 JOIN aaa_bbb ON aaa_bbb.b2 = tab3.c2),"
				+ "\n ddd AS (SELECT tab4.d1, tab4.d2, tab4.d3 FROM tab4 JOIN ccc ON ccc.c3 = tab4.d3),"
				+ "\n eee AS (SELECT tab5.e1, tab5.e2, tab5.e3 FROM tab5 JOIN ddd ON ddd.d3 = tab5.e3)"
				+ "\n SELECT www.a1, xxx.c2, yyy.d2, zzz.e2"
				+ "\n FROM aaa_bbb AS www"
				+ "\n JOIN ccc AS xxx ON www.a1 = xxx.c1"
				+ "\n JOIN ddd AS yyy ON xxx.c1 = yyy.d1"
				+ "\n JOIN eee AS zzz ON yyy.d1 = zzz.e1";

		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runParsertest(sql, parser);
		assertNoWalkerDiagnostics(extractor);
	}

	@Test
	public void nestedWithInnerJoinAaaThenBbbCccThenDddEeeParsesWithoutErrors() {
		final String sql = "WITH "
				+ "\n aaa AS (SELECT a1, a2, a3 FROM tab1),"
				+ "\n bbb_ccc AS ("
				+ "\n   WITH "
				+ "\n     bbb AS (SELECT bbb_src.b1, bbb_src.b2, bbb_src.b3 FROM tab2 AS bbb_src JOIN aaa ON aaa.a3 = bbb_src.b3),"
				+ "\n     ccc AS (SELECT tab3.c1, tab3.c2, tab3.c3 FROM tab3 JOIN bbb ON bbb.b3 = tab3.c3)"
				+ "\n   SELECT bbb.b1 AS b1, ccc.c1 AS c1, ccc.c2 AS c2"
				+ "\n   FROM bbb JOIN ccc ON bbb.b1 = ccc.c1"
				+ "\n ),"
				+ "\n ddd AS (SELECT tab4.d1, tab4.d2, tab4.d3 FROM tab4 JOIN bbb_ccc ON bbb_ccc.c2 = tab4.d2),"
				+ "\n eee AS (SELECT tab5.e1, tab5.e2, tab5.e3 FROM tab5 JOIN ddd ON ddd.d3 = tab5.e3)"
				+ "\n SELECT www.a1, xxx.c2, yyy.d2, zzz.e2"
				+ "\n FROM aaa AS www"
				+ "\n JOIN bbb_ccc AS xxx ON www.a1 = xxx.b1"
				+ "\n JOIN ddd AS yyy ON xxx.c1 = yyy.d1"
				+ "\n JOIN eee AS zzz ON yyy.d1 = zzz.e1";

		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runParsertest(sql, parser);
		assertNoWalkerDiagnostics(extractor);
	}

	@Test
	public void nestedWithInnerJoinAaaBbbThenCccDddThenEeeParsesWithoutErrors() {
		final String sql = "WITH "
				+ "\n aaa AS (SELECT a1, a2, a3 FROM tab1),"
				+ "\n bbb AS (SELECT bbb_src.b1, bbb_src.b2, bbb_src.b3 FROM tab2 AS bbb_src JOIN aaa ON aaa.a3 = bbb_src.b3),"
				+ "\n ccc_ddd AS ("
				+ "\n   WITH "
				+ "\n     ccc AS (SELECT tab3.c1, tab3.c2, tab3.c3 FROM tab3 JOIN bbb ON bbb.b3 = tab3.c3),"
				+ "\n     ddd AS (SELECT tab4.d1, tab4.d2, tab4.d3 FROM tab4 JOIN ccc ON ccc.c3 = tab4.d3)"
				+ "\n   SELECT ccc.c1 AS c1, ddd.d1 AS d1, ddd.d2 AS d2"
				+ "\n   FROM ccc JOIN ddd ON ccc.c1 = ddd.d1"
				+ "\n ),"
				+ "\n eee AS (SELECT tab5.e1, tab5.e2, tab5.e3 FROM tab5 JOIN ccc_ddd ON ccc_ddd.d2 = tab5.e2)"
				+ "\n SELECT www.a1, xxx.b2, yyy.d2, zzz.e2"
				+ "\n FROM aaa AS www"
				+ "\n JOIN bbb AS xxx ON www.a1 = xxx.b1"
				+ "\n JOIN ccc_ddd AS yyy ON xxx.b1 = yyy.c1"
				+ "\n JOIN eee AS zzz ON yyy.d1 = zzz.e1";

		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runParsertest(sql, parser);
		assertNoWalkerDiagnostics(extractor);
	}

	@Test
	public void nestedWithInnerJoinAaaBbbCccThenDddEeeParsesWithoutErrors() {
		final String sql = "WITH "
				+ "\n aaa AS (SELECT a1, a2, a3 FROM tab1),"
				+ "\n bbb AS (SELECT bbb_src.b1, bbb_src.b2, bbb_src.b3 FROM tab2 AS bbb_src JOIN aaa ON aaa.a3 = bbb_src.b3),"
				+ "\n ccc AS (SELECT tab3.c1, tab3.c2, tab3.c3 FROM tab3 JOIN bbb ON bbb.b3 = tab3.c3),"
				+ "\n ddd_eee AS ("
				+ "\n   WITH "
				+ "\n     ddd AS (SELECT tab4.d1, tab4.d2, tab4.d3 FROM tab4 JOIN ccc ON ccc.c3 = tab4.d3),"
				+ "\n     eee AS (SELECT tab5.e1, tab5.e2, tab5.e3 FROM tab5 JOIN ddd ON ddd.d3 = tab5.e3)"
				+ "\n   SELECT ddd.d1 AS d1, eee.e1 AS e1, eee.e2 AS e2"
				+ "\n   FROM ddd JOIN eee ON ddd.d1 = eee.e1"
				+ "\n )"
				+ "\n SELECT www.a1, xxx.b2, yyy.c2, zzz.e2"
				+ "\n FROM aaa AS www"
				+ "\n JOIN bbb AS xxx ON www.a1 = xxx.b1"
				+ "\n JOIN ccc AS yyy ON xxx.b1 = yyy.c1"
				+ "\n JOIN ddd_eee AS zzz ON yyy.c1 = zzz.d1";

		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runParsertest(sql, parser);
		assertNoWalkerDiagnostics(extractor);
	}

	@Test
	public void nestedWithScalarSelectListAaaBbbThenCccDddEeeParsesWithoutErrors() {
		final String sql = "WITH "
				+ "\n aaa_bbb AS ("
				+ "\n   WITH "
				+ "\n     aaa AS (SELECT a1, a2, a3 FROM tab1),"
				+ "\n     bbb AS (SELECT b1, b2, (SELECT max(aaa.a3) FROM aaa) AS b3 FROM tab2)"
				+ "\n   SELECT aaa.a1 AS a1, bbb.b1 AS b1, bbb.b2 AS b2"
				+ "\n   FROM aaa JOIN bbb ON aaa.a1 = bbb.b1"
				+ "\n ),"
				+ "\n ccc AS (SELECT c1, c2, (SELECT max(aaa_bbb.b2) FROM aaa_bbb) AS c3 FROM tab3),"
				+ "\n ddd AS (SELECT d1, d2, (SELECT max(ccc.c3) FROM ccc) AS d3 FROM tab4),"
				+ "\n eee AS (SELECT e1, e2, (SELECT max(ddd.d3) FROM ddd) AS e3 FROM tab5)"
				+ "\n SELECT www.a1, xxx.c2, yyy.d2, zzz.e2"
				+ "\n FROM aaa_bbb AS www"
				+ "\n JOIN ccc AS xxx ON www.a1 = xxx.c1"
				+ "\n JOIN ddd AS yyy ON xxx.c1 = yyy.d1"
				+ "\n JOIN eee AS zzz ON yyy.d1 = zzz.e1";

		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runParsertest(sql, parser);
		assertNoWalkerDiagnostics(extractor);
	}

	@Test
	public void nestedWithScalarSelectListAaaThenBbbCccThenDddEeeParsesWithoutErrors() {
		final String sql = "WITH "
				+ "\n aaa AS (SELECT a1, a2, a3 FROM tab1),"
				+ "\n bbb_ccc AS ("
				+ "\n   WITH "
				+ "\n     bbb AS (SELECT b1, b2, (SELECT max(aaa.a3) FROM aaa) AS b3 FROM tab2),"
				+ "\n     ccc AS (SELECT c1, c2, (SELECT max(bbb.b3) FROM bbb) AS c3 FROM tab3)"
				+ "\n   SELECT bbb.b1 AS b1, ccc.c1 AS c1, ccc.c2 AS c2"
				+ "\n   FROM bbb JOIN ccc ON bbb.b1 = ccc.c1"
				+ "\n ),"
				+ "\n ddd AS (SELECT d1, d2, (SELECT max(bbb_ccc.c2) FROM bbb_ccc) AS d3 FROM tab4),"
				+ "\n eee AS (SELECT e1, e2, (SELECT max(ddd.d3) FROM ddd) AS e3 FROM tab5)"
				+ "\n SELECT www.a1, xxx.c2, yyy.d2, zzz.e2"
				+ "\n FROM aaa AS www"
				+ "\n JOIN bbb_ccc AS xxx ON www.a1 = xxx.b1"
				+ "\n JOIN ddd AS yyy ON xxx.c1 = yyy.d1"
				+ "\n JOIN eee AS zzz ON yyy.d1 = zzz.e1";

		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runParsertest(sql, parser);
		assertNoWalkerDiagnostics(extractor);
	}

	@Test
	public void nestedWithScalarSelectListAaaBbbThenCccDddThenEeeParsesWithoutErrors() {
		final String sql = "WITH "
				+ "\n aaa AS (SELECT a1, a2, a3 FROM tab1),"
				+ "\n bbb AS (SELECT b1, b2, (SELECT max(aaa.a3) FROM aaa) AS b3 FROM tab2),"
				+ "\n ccc_ddd AS ("
				+ "\n   WITH "
				+ "\n     ccc AS (SELECT c1, c2, (SELECT max(bbb.b3) FROM bbb) AS c3 FROM tab3),"
				+ "\n     ddd AS (SELECT d1, d2, (SELECT max(ccc.c3) FROM ccc) AS d3 FROM tab4)"
				+ "\n   SELECT ccc.c1 AS c1, ddd.d1 AS d1, ddd.d2 AS d2"
				+ "\n   FROM ccc JOIN ddd ON ccc.c1 = ddd.d1"
				+ "\n ),"
				+ "\n eee AS (SELECT e1, e2, (SELECT max(ccc_ddd.d2) FROM ccc_ddd) AS e3 FROM tab5)"
				+ "\n SELECT www.a1, xxx.b2, yyy.d2, zzz.e2"
				+ "\n FROM aaa AS www"
				+ "\n JOIN bbb AS xxx ON www.a1 = xxx.b1"
				+ "\n JOIN ccc_ddd AS yyy ON xxx.b1 = yyy.c1"
				+ "\n JOIN eee AS zzz ON yyy.d1 = zzz.e1";

		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runParsertest(sql, parser);
		assertNoWalkerDiagnostics(extractor);
	}

	@Test
	public void nestedWithScalarSelectListAaaBbbCccThenDddEeeParsesWithoutErrors() {
		final String sql = "WITH "
				+ "\n aaa AS (SELECT a1, a2, a3 FROM tab1),"
				+ "\n bbb AS (SELECT b1, b2, (SELECT max(aaa.a3) FROM aaa) AS b3 FROM tab2),"
				+ "\n ccc AS (SELECT c1, c2, (SELECT max(bbb.b3) FROM bbb) AS c3 FROM tab3),"
				+ "\n ddd_eee AS ("
				+ "\n   WITH "
				+ "\n     ddd AS (SELECT d1, d2, (SELECT max(ccc.c3) FROM ccc) AS d3 FROM tab4),"
				+ "\n     eee AS (SELECT e1, e2, (SELECT max(ddd.d3) FROM ddd) AS e3 FROM tab5)"
				+ "\n   SELECT ddd.d1 AS d1, eee.e1 AS e1, eee.e2 AS e2"
				+ "\n   FROM ddd JOIN eee ON ddd.d1 = eee.e1"
				+ "\n )"
				+ "\n SELECT www.a1, xxx.b2, yyy.c2, zzz.e2"
				+ "\n FROM aaa AS www"
				+ "\n JOIN bbb AS xxx ON www.a1 = xxx.b1"
				+ "\n JOIN ccc AS yyy ON xxx.b1 = yyy.c1"
				+ "\n JOIN ddd_eee AS zzz ON yyy.c1 = zzz.d1";

		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runParsertest(sql, parser);
		assertNoWalkerDiagnostics(extractor);
	}

	@Test
	public void nestedWithScalarWhereAaaBbbThenCccDddEeeParsesWithoutErrors() {
		final String sql = "WITH "
				+ "\n aaa_bbb AS ("
				+ "\n   WITH "
				+ "\n     aaa AS (SELECT a1, a2, a3 FROM tab1),"
				+ "\n     bbb AS (SELECT b1, b2, b3 FROM tab2 WHERE b3 = (SELECT max(aaa.a3) FROM aaa))"
				+ "\n   SELECT aaa.a1 AS a1, bbb.b1 AS b1, bbb.b2 AS b2"
				+ "\n   FROM aaa JOIN bbb ON aaa.a1 = bbb.b1"
				+ "\n ),"
				+ "\n ccc AS (SELECT c1, c2, c3 FROM tab3 WHERE c2 = (SELECT max(aaa_bbb.b2) FROM aaa_bbb)),"
				+ "\n ddd AS (SELECT d1, d2, d3 FROM tab4 WHERE d3 = (SELECT max(ccc.c3) FROM ccc)),"
				+ "\n eee AS (SELECT e1, e2, e3 FROM tab5 WHERE e3 = (SELECT max(ddd.d3) FROM ddd))"
				+ "\n SELECT www.a1, xxx.c2, yyy.d2, zzz.e2"
				+ "\n FROM aaa_bbb AS www"
				+ "\n JOIN ccc AS xxx ON www.a1 = xxx.c1"
				+ "\n JOIN ddd AS yyy ON xxx.c1 = yyy.d1"
				+ "\n JOIN eee AS zzz ON yyy.d1 = zzz.e1";

		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runParsertest(sql, parser);
		assertNoWalkerDiagnostics(extractor);
	}

	@Test
	public void nestedWithScalarWhereAaaThenBbbCccThenDddEeeParsesWithoutErrors() {
		final String sql = "WITH "
				+ "\n aaa AS (SELECT a1, a2, a3 FROM tab1),"
				+ "\n bbb_ccc AS ("
				+ "\n   WITH "
				+ "\n     bbb AS (SELECT b1, b2, b3 FROM tab2 WHERE b3 = (SELECT max(aaa.a3) FROM aaa where aaa.a2 is not null)),"
				+ "\n     ccc AS (SELECT c1, c2, c3 FROM tab3 WHERE c3 = (SELECT max(bbb.b3) FROM bbb where aaa.a1 > bbb.b1))"
				+ "\n   SELECT bbb.b1 AS b1, ccc.c1 AS c1, ccc.c2 AS c2"
				+ "\n   FROM bbb JOIN ccc ON bbb.b1 = ccc.c1"
				+ "\n ),"
				+ "\n ddd AS (SELECT d1, d2, d3 FROM tab4 WHERE d2 = (SELECT max(bbb_ccc.c2) FROM bbb_ccc)),"
				+ "\n eee AS (SELECT e1, e2, e3 FROM tab5 WHERE e3 = (SELECT max(ddd.d3) FROM ddd))"
				+ "\n SELECT www.a1, xxx.c2, yyy.d2, zzz.e2"
				+ "\n FROM aaa AS www"
				+ "\n JOIN bbb_ccc AS xxx ON www.a1 = xxx.b1"
				+ "\n JOIN ddd AS yyy ON xxx.c1 = yyy.d1"
				+ "\n JOIN eee AS zzz ON yyy.d1 = zzz.e1";

		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runParsertest(sql, parser);
		assertNoWalkerDiagnostics(extractor);
	}

	@Test
	public void nestedWithScalarWhereAaaBbbThenCccDddThenEeeParsesWithoutErrors() {
		final String sql = "WITH "
				+ "\n aaa AS (SELECT a1, a2, a3 FROM tab1),"
				+ "\n bbb AS (SELECT b1, b2, b3 FROM tab2 WHERE b3 = (SELECT max(aaa.a3) FROM aaa)),"
				+ "\n ccc_ddd AS ("
				+ "\n   WITH "
				+ "\n     ccc AS (SELECT c1, c2, c3 FROM tab3 WHERE c3 = (SELECT max(bbb.b3) FROM bbb)),"
				+ "\n     ddd AS (SELECT d1, d2, d3 FROM tab4 WHERE d3 = (SELECT max(ccc.c3) FROM ccc))"
				+ "\n   SELECT ccc.c1 AS c1, ddd.d1 AS d1, ddd.d2 AS d2"
				+ "\n   FROM ccc JOIN ddd ON ccc.c1 = ddd.d1"
				+ "\n ),"
				+ "\n eee AS (SELECT e1, e2, e3 FROM tab5 WHERE e2 = (SELECT max(ccc_ddd.d2) FROM ccc_ddd))"
				+ "\n SELECT www.a1, xxx.b2, yyy.d2, zzz.e2"
				+ "\n FROM aaa AS www"
				+ "\n JOIN bbb AS xxx ON www.a1 = xxx.b1"
				+ "\n JOIN ccc_ddd AS yyy ON xxx.b1 = yyy.c1"
				+ "\n JOIN eee AS zzz ON yyy.d1 = zzz.e1";

		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runParsertest(sql, parser);
		assertNoWalkerDiagnostics(extractor);
	}

	@Test
	public void nestedWithScalarWhereAaaBbbCccThenDddEeeParsesWithoutErrors() {
		final String sql = "WITH "
				+ "\n aaa AS (SELECT a1, a2, a3 FROM tab1),"
				+ "\n bbb AS (SELECT b1, b2, b3 FROM tab2 WHERE b3 = (SELECT max(aaa.a3) FROM aaa)),"
				+ "\n ccc AS (SELECT c1, c2, c3 FROM tab3 WHERE c3 = (SELECT max(bbb.b3) FROM bbb)),"
				+ "\n ddd_eee AS ("
				+ "\n   WITH "
				+ "\n     ddd AS (SELECT d1, d2, d3 FROM tab4 WHERE d3 = (SELECT max(ccc.c3) FROM ccc)),"
				+ "\n     eee AS (SELECT e1, e2, e3 FROM tab5 WHERE e3 = (SELECT max(ddd.d3) FROM ddd))"
				+ "\n   SELECT ddd.d1 AS d1, eee.e1 AS e1, eee.e2 AS e2"
				+ "\n   FROM ddd JOIN eee ON ddd.d1 = eee.e1"
				+ "\n )"
				+ "\n SELECT www.a1, xxx.b2, yyy.c2, zzz.e2"
				+ "\n FROM aaa AS www"
				+ "\n JOIN bbb AS xxx ON www.a1 = xxx.b1"
				+ "\n JOIN ccc AS yyy ON xxx.b1 = yyy.c1"
				+ "\n JOIN ddd_eee AS zzz ON yyy.c1 = zzz.d1";

		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runParsertest(sql, parser);
		assertNoWalkerDiagnostics(extractor);
	}

	@Test
	public void nestedWithScalarHavingAaaBbbThenCccDddEeeParsesWithoutErrors() {
		final String sql = "WITH "
				+ "\n aaa_bbb AS ("
				+ "\n   WITH "
				+ "\n     aaa AS (SELECT a1, a2, a3 FROM tab1),"
				+ "\n     bbb AS (SELECT b1, b2, max(b3) AS b3 FROM tab2 GROUP BY b1, b2 HAVING max(b3) >= (SELECT max(aaa.a3) FROM aaa))"
				+ "\n   SELECT aaa.a1 AS a1, bbb.b1 AS b1, bbb.b2 AS b2"
				+ "\n   FROM aaa JOIN bbb ON aaa.a1 = bbb.b1"
				+ "\n ),"
				+ "\n ccc AS (SELECT c1, c2, max(c3) AS c3 FROM tab3 GROUP BY c1, c2 HAVING max(c2) >= (SELECT max(aaa_bbb.b2) FROM aaa_bbb)),"
				+ "\n ddd AS (SELECT d1, d2, max(d3) AS d3 FROM tab4 GROUP BY d1, d2 HAVING max(d3) >= (SELECT max(ccc.c3) FROM ccc)),"
				+ "\n eee AS (SELECT e1, e2, max(e3) AS e3 FROM tab5 GROUP BY e1, e2 HAVING max(e3) >= (SELECT max(ddd.d3) FROM ddd))"
				+ "\n SELECT www.a1, xxx.c2, yyy.d2, zzz.e2"
				+ "\n FROM aaa_bbb AS www"
				+ "\n JOIN ccc AS xxx ON www.a1 = xxx.c1"
				+ "\n JOIN ddd AS yyy ON xxx.c1 = yyy.d1"
				+ "\n JOIN eee AS zzz ON yyy.d1 = zzz.e1";

		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runParsertest(sql, parser);
		assertNoWalkerDiagnostics(extractor);
	}

	@Test
	public void nestedWithScalarHavingAaaThenBbbCccThenDddEeeParsesWithoutErrors() {
		final String sql = "WITH "
				+ "\n aaa AS (SELECT a1, a2, a3 FROM tab1),"
				+ "\n bbb_ccc AS ("
				+ "\n   WITH "
				+ "\n     bbb AS (SELECT b1, b2, max(b3) AS b3 FROM tab2 GROUP BY b1, b2 HAVING max(b3) >= (SELECT max(aaa.a3) FROM aaa)),"
				+ "\n     ccc AS (SELECT c1, c2, max(c3) AS c3 FROM tab3 GROUP BY c1, c2 HAVING max(c3) >= (SELECT max(bbb.b3) FROM bbb))"
				+ "\n   SELECT bbb.b1 AS b1, ccc.c1 AS c1, ccc.c2 AS c2"
				+ "\n   FROM bbb JOIN ccc ON bbb.b1 = ccc.c1"
				+ "\n ),"
				+ "\n ddd AS (SELECT d1, d2, max(d3) AS d3 FROM tab4 GROUP BY d1, d2 HAVING max(d2) >= (SELECT max(bbb_ccc.c2) FROM bbb_ccc)),"
				+ "\n eee AS (SELECT e1, e2, max(e3) AS e3 FROM tab5 GROUP BY e1, e2 HAVING max(e3) >= (SELECT max(ddd.d3) FROM ddd))"
				+ "\n SELECT www.a1, xxx.c2, yyy.d2, zzz.e2"
				+ "\n FROM aaa AS www"
				+ "\n JOIN bbb_ccc AS xxx ON www.a1 = xxx.b1"
				+ "\n JOIN ddd AS yyy ON xxx.c1 = yyy.d1"
				+ "\n JOIN eee AS zzz ON yyy.d1 = zzz.e1";

		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runParsertest(sql, parser);
		assertNoWalkerDiagnostics(extractor);
	}

	@Test
	public void nestedWithScalarHavingAaaBbbThenCccDddThenEeeParsesWithoutErrors() {
		final String sql = "WITH "
				+ "\n aaa AS (SELECT a1, a2, a3 FROM tab1),"
				+ "\n bbb AS (SELECT b1, b2, max(b3) AS b3 FROM tab2 GROUP BY b1, b2 HAVING max(b3) >= (SELECT max(aaa.a3) FROM aaa)),"
				+ "\n ccc_ddd AS ("
				+ "\n   WITH "
				+ "\n     ccc AS (SELECT c1, c2, max(c3) AS c3 FROM tab3 GROUP BY c1, c2 HAVING max(c3) >= (SELECT max(bbb.b3) FROM bbb)),"
				+ "\n     ddd AS (SELECT d1, d2, max(d3) AS d3 FROM tab4 GROUP BY d1, d2 HAVING max(d3) >= (SELECT max(ccc.c3) FROM ccc))"
				+ "\n   SELECT ccc.c1 AS c1, ddd.d1 AS d1, ddd.d2 AS d2"
				+ "\n   FROM ccc JOIN ddd ON ccc.c1 = ddd.d1"
				+ "\n ),"
				+ "\n eee AS (SELECT e1, e2, max(e3) AS e3 FROM tab5 GROUP BY e1, e2 HAVING max(e2) >= (SELECT max(ccc_ddd.d2) FROM ccc_ddd))"
				+ "\n SELECT www.a1, xxx.b2, yyy.d2, zzz.e2"
				+ "\n FROM aaa AS www"
				+ "\n JOIN bbb AS xxx ON www.a1 = xxx.b1"
				+ "\n JOIN ccc_ddd AS yyy ON xxx.b1 = yyy.c1"
				+ "\n JOIN eee AS zzz ON yyy.d1 = zzz.e1";

		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runParsertest(sql, parser);
		assertNoWalkerDiagnostics(extractor);
	}

	@Test
	public void nestedWithScalarHavingAaaBbbCccThenDddEeeParsesWithoutErrors() {
		final String sql = "WITH "
				+ "\n aaa AS (SELECT a1, a2, a3 FROM tab1),"
				+ "\n bbb AS (SELECT b1, b2, max(b3) AS b3 FROM tab2 GROUP BY b1, b2 HAVING max(b3) >= (SELECT max(aaa.a3) FROM aaa)),"
				+ "\n ccc AS (SELECT c1, c2, max(c3) AS c3 FROM tab3 GROUP BY c1, c2 HAVING max(c3) >= (SELECT max(bbb.b3) FROM bbb)),"
				+ "\n ddd_eee AS ("
				+ "\n   WITH "
				+ "\n     ddd AS (SELECT d1, d2, max(d3) AS d3 FROM tab4 GROUP BY d1, d2 HAVING max(d3) >= (SELECT max(ccc.c3) FROM ccc)),"
				+ "\n     eee AS (SELECT e1, e2, max(e3) AS e3 FROM tab5 GROUP BY e1, e2 HAVING max(e3) >= (SELECT max(ddd.d3) FROM ddd))"
				+ "\n   SELECT ddd.d1 AS d1, eee.e1 AS e1, eee.e2 AS e2"
				+ "\n   FROM ddd JOIN eee ON ddd.d1 = eee.e1"
				+ "\n )"
				+ "\n SELECT www.a1, xxx.b2, yyy.c2, zzz.e2"
				+ "\n FROM aaa AS www"
				+ "\n JOIN bbb AS xxx ON www.a1 = xxx.b1"
				+ "\n JOIN ccc AS yyy ON xxx.b1 = yyy.c1"
				+ "\n JOIN ddd_eee AS zzz ON yyy.c1 = zzz.d1";

		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runParsertest(sql, parser);
		assertNoWalkerDiagnostics(extractor);
	}

	@Test
	public void nestedWithUnionCarriesCteListAaaBbbThenCccDddEee() {
		final String sql = "WITH "
				+ "\n aaa_bbb AS ("
				+ "\n   WITH "
				+ "\n     aaa AS (SELECT a1, a2, a3 FROM tab1),"
				+ "\n     bbb AS (SELECT b1, b2, b3 FROM tab2 UNION SELECT aaa.a1 AS b1, aaa.a2 AS b2, aaa.a3 AS b3 FROM aaa)"
				+ "\n   SELECT aaa.a1 AS a1, bbb.b1 AS b1, bbb.b2 AS b2"
				+ "\n   FROM aaa JOIN bbb ON aaa.a1 = bbb.b1"
				+ "\n ),"
				+ "\n ccc AS (SELECT c1, c2, c3 FROM tab3 UNION SELECT aaa_bbb.a1 AS c1, aaa_bbb.b2 AS c2, aaa_bbb.b1 AS c3 FROM aaa_bbb),"
				+ "\n ddd AS (SELECT d1, d2, d3 FROM tab4 UNION SELECT ccc.c1 AS d1, ccc.c2 AS d2, ccc.c3 AS d3 FROM ccc),"
				+ "\n eee AS (SELECT e1, e2, e3 FROM tab5 UNION SELECT ddd.d1 AS e1, ddd.d2 AS e2, ddd.d3 AS e3 FROM ddd)"
				+ "\n SELECT www.a1, xxx.c2, yyy.d2, zzz.e2"
				+ "\n FROM aaa_bbb AS www"
				+ "\n JOIN ccc AS xxx ON www.a1 = xxx.c1"
				+ "\n JOIN ddd AS yyy ON xxx.c1 = yyy.d1"
				+ "\n JOIN eee AS zzz ON yyy.d1 = zzz.e1";

		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runParsertest(sql, parser);
		assertNoWalkerDiagnostics(extractor);
	}

	@Test
	public void nestedWithUnionCarriesCteListAaaThenBbbCccThenDddEee() {
		final String sql = "WITH "
				+ "\n aaa AS (SELECT a1, a2, a3 FROM tab1),"
				+ "\n bbb_ccc AS ("
				+ "\n   WITH "
				+ "\n     bbb AS (SELECT b1, b2, b3 FROM tab2 UNION SELECT aaa.a1 AS b1, aaa.a2 AS b2, aaa.a3 AS b3 FROM aaa),"
				+ "\n     ccc AS (SELECT c1, c2, c3 FROM tab3 UNION SELECT bbb.b1 AS c1, bbb.b2 AS c2, bbb.b3 AS c3 FROM bbb)"
				+ "\n   SELECT bbb.b1 AS b1, ccc.c1 AS c1, ccc.c2 AS c2"
				+ "\n   FROM bbb JOIN ccc ON bbb.b1 = ccc.c1"
				+ "\n ),"
				+ "\n ddd AS (SELECT d1, d2, d3 FROM tab4 UNION SELECT bbb_ccc.b1 AS d1, bbb_ccc.c2 AS d2, bbb_ccc.c1 AS d3 FROM bbb_ccc),"
				+ "\n eee AS (SELECT e1, e2, e3 FROM tab5 UNION SELECT ddd.d1 AS e1, ddd.d2 AS e2, ddd.d3 AS e3 FROM ddd)"
				+ "\n SELECT www.a1, xxx.c2, yyy.d2, zzz.e2"
				+ "\n FROM aaa AS www"
				+ "\n JOIN bbb_ccc AS xxx ON www.a1 = xxx.b1"
				+ "\n JOIN ddd AS yyy ON xxx.c1 = yyy.d1"
				+ "\n JOIN eee AS zzz ON yyy.d1 = zzz.e1";

		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runParsertest(sql, parser);
		assertNoWalkerDiagnostics(extractor);
	}

	@Test
	public void nestedWithUnionCarriesCteListAaaBbbThenCccDddThenEee() {
		final String sql = "WITH "
				+ "\n aaa AS (SELECT a1, a2, a3 FROM tab1),"
				+ "\n bbb AS (SELECT b1, b2, b3 FROM tab2 UNION SELECT aaa.a1 AS b1, aaa.a2 AS b2, aaa.a3 AS b3 FROM aaa),"
				+ "\n ccc_ddd AS ("
				+ "\n   WITH "
				+ "\n     ccc AS (SELECT c1, c2, c3 FROM tab3 UNION SELECT bbb.b1 AS c1, bbb.b2 AS c2, bbb.b3 AS c3 FROM bbb),"
				+ "\n     ddd AS (SELECT d1, d2, d3 FROM tab4 UNION SELECT ccc.c1 AS d1, ccc.c2 AS d2, ccc.c3 AS d3 FROM ccc)"
				+ "\n   SELECT ccc.c1 AS c1, ddd.d1 AS d1, ddd.d2 AS d2"
				+ "\n   FROM ccc JOIN ddd ON ccc.c1 = ddd.d1"
				+ "\n ),"
				+ "\n eee AS (SELECT e1, e2, e3 FROM tab5 UNION SELECT ccc_ddd.c1 AS e1, ccc_ddd.d2 AS e2, ccc_ddd.d1 AS e3 FROM ccc_ddd)"
				+ "\n SELECT www.a1, xxx.b2, yyy.d2, zzz.e2"
				+ "\n FROM aaa AS www"
				+ "\n JOIN bbb AS xxx ON www.a1 = xxx.b1"
				+ "\n JOIN ccc_ddd AS yyy ON xxx.b1 = yyy.c1"
				+ "\n JOIN eee AS zzz ON yyy.d1 = zzz.e1";

		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runParsertest(sql, parser);
		assertNoWalkerDiagnostics(extractor);
	}

	@Test
	public void nestedWithUnionCarriesCteListAaaBbbCccThenDddEee() {
		final String sql = "WITH "
				+ "\n aaa AS (SELECT a1, a2, a3 FROM tab1),"
				+ "\n bbb AS (SELECT b1, b2, b3 FROM tab2 UNION SELECT aaa.a1 AS b1, aaa.a2 AS b2, aaa.a3 AS b3 FROM aaa),"
				+ "\n ccc AS (SELECT c1, c2, c3 FROM tab3 UNION SELECT bbb.b1 AS c1, bbb.b2 AS c2, bbb.b3 AS c3 FROM bbb),"
				+ "\n ddd_eee AS ("
				+ "\n   WITH "
				+ "\n     ddd AS (SELECT d1, d2, d3 FROM tab4 UNION SELECT ccc.c1 AS d1, ccc.c2 AS d2, ccc.c3 AS d3 FROM ccc),"
				+ "\n     eee AS (SELECT e1, e2, e3 FROM tab5 UNION SELECT ddd.d1 AS e1, ddd.d2 AS e2, ddd.d3 AS e3 FROM ddd)"
				+ "\n   SELECT ddd.d1 AS d1, eee.e1 AS e1, eee.e2 AS e2"
				+ "\n   FROM ddd JOIN eee ON ddd.d1 = eee.e1"
				+ "\n )"
				+ "\n SELECT www.a1, xxx.b2, yyy.c2, zzz.e2"
				+ "\n FROM aaa AS www"
				+ "\n JOIN bbb AS xxx ON www.a1 = xxx.b1"
				+ "\n JOIN ccc AS yyy ON xxx.b1 = yyy.c1"
				+ "\n JOIN ddd_eee AS zzz ON yyy.c1 = zzz.d1";

		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runParsertest(sql, parser);
		assertNoWalkerDiagnostics(extractor);
	}

	@Test
	public void nestedWithIntersectCarriesCteListAaaBbbThenCccDddEee() {
		final String sql = "WITH "
				+ "\n aaa_bbb AS ("
				+ "\n   WITH "
				+ "\n     aaa AS (SELECT a1, a2, a3 FROM tab1),"
				+ "\n     bbb AS (SELECT b1, b2, b3 FROM tab2 INTERSECT SELECT aaa.a1 AS b1, aaa.a2 AS b2, aaa.a3 AS b3 FROM aaa)"
				+ "\n   SELECT aaa.a1 AS a1, bbb.b1 AS b1, bbb.b2 AS b2"
				+ "\n   FROM aaa JOIN bbb ON aaa.a1 = bbb.b1"
				+ "\n ),"
				+ "\n ccc AS (SELECT c1, c2, c3 FROM tab3 INTERSECT SELECT aaa_bbb.a1 AS c1, aaa_bbb.b2 AS c2, aaa_bbb.b1 AS c3 FROM aaa_bbb),"
				+ "\n ddd AS (SELECT d1, d2, d3 FROM tab4 INTERSECT SELECT ccc.c1 AS d1, ccc.c2 AS d2, ccc.c3 AS d3 FROM ccc),"
				+ "\n eee AS (SELECT e1, e2, e3 FROM tab5 INTERSECT SELECT ddd.d1 AS e1, ddd.d2 AS e2, ddd.d3 AS e3 FROM ddd)"
				+ "\n SELECT www.a1, xxx.c2, yyy.d2, zzz.e2"
				+ "\n FROM aaa_bbb AS www"
				+ "\n JOIN ccc AS xxx ON www.a1 = xxx.c1"
				+ "\n JOIN ddd AS yyy ON xxx.c1 = yyy.d1"
				+ "\n JOIN eee AS zzz ON yyy.d1 = zzz.e1";

		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runParsertest(sql, parser);
		assertNoWalkerDiagnostics(extractor);
	}

	@Test
	public void nestedWithIntersectCarriesCteListAaaThenBbbCccThenDddEee() {
		final String sql = "WITH "
				+ "\n aaa AS (SELECT a1, a2, a3 FROM tab1),"
				+ "\n bbb_ccc AS ("
				+ "\n   WITH "
				+ "\n     bbb AS (SELECT b1, b2, b3 FROM tab2 INTERSECT SELECT aaa.a1 AS b1, aaa.a2 AS b2, aaa.a3 AS b3 FROM aaa),"
				+ "\n     ccc AS (SELECT c1, c2, c3 FROM tab3 INTERSECT SELECT bbb.b1 AS c1, bbb.b2 AS c2, bbb.b3 AS c3 FROM bbb)"
				+ "\n   SELECT bbb.b1 AS b1, ccc.c1 AS c1, ccc.c2 AS c2"
				+ "\n   FROM bbb JOIN ccc ON bbb.b1 = ccc.c1"
				+ "\n ),"
				+ "\n ddd AS (SELECT d1, d2, d3 FROM tab4 INTERSECT SELECT bbb_ccc.b1 AS d1, bbb_ccc.c2 AS d2, bbb_ccc.c1 AS d3 FROM bbb_ccc),"
				+ "\n eee AS (SELECT e1, e2, e3 FROM tab5 INTERSECT SELECT ddd.d1 AS e1, ddd.d2 AS e2, ddd.d3 AS e3 FROM ddd)"
				+ "\n SELECT www.a1, xxx.c2, yyy.d2, zzz.e2"
				+ "\n FROM aaa AS www"
				+ "\n JOIN bbb_ccc AS xxx ON www.a1 = xxx.b1"
				+ "\n JOIN ddd AS yyy ON xxx.c1 = yyy.d1"
				+ "\n JOIN eee AS zzz ON yyy.d1 = zzz.e1";

		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runParsertest(sql, parser);
		assertNoWalkerDiagnostics(extractor);
	}

	@Test
	public void nestedWithIntersectCarriesCteListAaaBbbThenCccDddThenEee() {
		final String sql = "WITH "
				+ "\n aaa AS (SELECT a1, a2, a3 FROM tab1),"
				+ "\n bbb AS (SELECT b1, b2, b3 FROM tab2 INTERSECT SELECT aaa.a1 AS b1, aaa.a2 AS b2, aaa.a3 AS b3 FROM aaa),"
				+ "\n ccc_ddd AS ("
				+ "\n   WITH "
				+ "\n     ccc AS (SELECT c1, c2, c3 FROM tab3 INTERSECT SELECT bbb.b1 AS c1, bbb.b2 AS c2, bbb.b3 AS c3 FROM bbb),"
				+ "\n     ddd AS (SELECT d1, d2, d3 FROM tab4 INTERSECT SELECT ccc.c1 AS d1, ccc.c2 AS d2, ccc.c3 AS d3 FROM ccc)"
				+ "\n   SELECT ccc.c1 AS c1, ddd.d1 AS d1, ddd.d2 AS d2"
				+ "\n   FROM ccc JOIN ddd ON ccc.c1 = ddd.d1"
				+ "\n ),"
				+ "\n eee AS (SELECT e1, e2, e3 FROM tab5 INTERSECT SELECT ccc_ddd.c1 AS e1, ccc_ddd.d2 AS e2, ccc_ddd.d1 AS e3 FROM ccc_ddd)"
				+ "\n SELECT www.a1, xxx.b2, yyy.d2, zzz.e2"
				+ "\n FROM aaa AS www"
				+ "\n JOIN bbb AS xxx ON www.a1 = xxx.b1"
				+ "\n JOIN ccc_ddd AS yyy ON xxx.b1 = yyy.c1"
				+ "\n JOIN eee AS zzz ON yyy.d1 = zzz.e1";

		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runParsertest(sql, parser);
		assertNoWalkerDiagnostics(extractor);
	}

	@Test
	public void nestedWithIntersectCarriesCteListAaaBbbCccThenDddEee() {
		final String sql = "WITH "
				+ "\n aaa AS (SELECT a1, a2, a3 FROM tab1),"
				+ "\n bbb AS (SELECT b1, b2, b3 FROM tab2 INTERSECT SELECT aaa.a1 AS b1, aaa.a2 AS b2, aaa.a3 AS b3 FROM aaa),"
				+ "\n ccc AS (SELECT c1, c2, c3 FROM tab3 INTERSECT SELECT bbb.b1 AS c1, bbb.b2 AS c2, bbb.b3 AS c3 FROM bbb),"
				+ "\n ddd_eee AS ("
				+ "\n   WITH "
				+ "\n     ddd AS (SELECT d1, d2, d3 FROM tab4 INTERSECT SELECT ccc.c1 AS d1, ccc.c2 AS d2, ccc.c3 AS d3 FROM ccc),"
				+ "\n     eee AS (SELECT e1, e2, e3 FROM tab5 INTERSECT SELECT ddd.d1 AS e1, ddd.d2 AS e2, ddd.d3 AS e3 FROM ddd)"
				+ "\n   SELECT ddd.d1 AS d1, eee.e1 AS e1, eee.e2 AS e2"
				+ "\n   FROM ddd JOIN eee ON ddd.d1 = eee.e1"
				+ "\n )"
				+ "\n SELECT www.a1, xxx.b2, yyy.c2, zzz.e2"
				+ "\n FROM aaa AS www"
				+ "\n JOIN bbb AS xxx ON www.a1 = xxx.b1"
				+ "\n JOIN ccc AS yyy ON xxx.b1 = yyy.c1"
				+ "\n JOIN ddd_eee AS zzz ON yyy.c1 = zzz.d1";

		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runParsertest(sql, parser);
		assertNoWalkerDiagnostics(extractor);
	}

	@Test
	public void nestedNestedWithDepth2CarriesCteListsExistsRefsAndAliasInterfaces() {
		final String sql = "WITH "
				+ "\n aaa AS (SELECT a1, a2, a3 FROM tab1),"
				+ "\n bbb_ccc AS ("
				+ "\n   WITH "
				+ "\n     bbb AS (SELECT b1, b2, b3 FROM tab2 WHERE EXISTS (SELECT 1 FROM aaa WHERE aaa.a3 = tab2.b3)),"
				+ "\n     ccc AS (SELECT c1, c2, c3 FROM tab3 WHERE EXISTS (SELECT 1 FROM bbb WHERE bbb.b3 = tab3.c3))"
				+ "\n   SELECT bbb.b1 AS b1, ccc.c1 AS c1, ccc.c2 AS c2"
				+ "\n   FROM bbb JOIN ccc ON bbb.b1 = ccc.c1"
				+ "\n ),"
				+ "\n nested_outer AS ("
				+ "\n   WITH "
				+ "\n     prior_seed AS (SELECT aaa.a1 AS a1, aaa.a2 AS a2, aaa.a3 AS a3 FROM aaa),"
				+ "\n     following_seed AS (SELECT bbb_ccc.c1 AS c1, bbb_ccc.c2 AS c2, bbb_ccc.b1 AS c3 FROM bbb_ccc"
				+ "\n       WHERE EXISTS (SELECT 1 FROM prior_seed WHERE prior_seed.a3 = bbb_ccc.c2)"
				+ "\n         AND EXISTS (SELECT 1 FROM aaa WHERE aaa.a1 = bbb_ccc.c1))"
				+ "\n   SELECT prior_seed.a1 AS a1, following_seed.c2 AS c2, following_seed.c3 AS c3"
				+ "\n   FROM prior_seed JOIN following_seed ON prior_seed.a1 = following_seed.c1"
				+ "\n ),"
				+ "\n ddd AS (SELECT d1, d2, d3 FROM tab4 WHERE EXISTS (SELECT 1 FROM nested_outer WHERE nested_outer.c2 = tab4.d2)),"
				+ "\n eee AS (SELECT e1, e2, e3 FROM tab5 WHERE EXISTS (SELECT 1 FROM ddd WHERE ddd.d3 = tab5.e3))"
				+ "\n SELECT www.a1, xxx.c2, yyy.d2, zzz.e2"
				+ "\n FROM nested_outer AS www"
				+ "\n JOIN bbb_ccc AS xxx ON www.a1 = xxx.b1"
				+ "\n JOIN ddd AS yyy ON xxx.c1 = yyy.d1"
				+ "\n JOIN eee AS zzz ON yyy.d1 = zzz.e1";

		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runParsertest(sql, parser);
		assertNoWalkerDiagnostics(extractor);

		Map<String, Object> top = topScope(extractor, "query21");
		Map<String, Object> def20 = scopeAt(top, "def_query20");
		Map<String, Object> def17 = scopeAt(top, "def_query17");
		Map<String, Object> def14 = scopeAt(top, "def_query14");
		Map<String, Object> def13 = scopeAt(top, "def_query14", "def_query13");
		Map<String, Object> def7_6 = scopeAt(top, "def_query14", "def_query7", "def_query6");
		Map<String, Object> def7_3 = scopeAt(top, "def_query14", "def_query7", "def_query3");

		assertCteEntry(def20, "nested_outer", "query14");
		assertCteEntry(def20, "ddd", "query17");
		assertQueryLink(def20, "exists19", "query18");

		assertCteEntry(def17, "nested_outer", "query14");
		assertQueryLink(def17, "exists16", "query15");

		assertCteEntry(def14, "prior_seed", "query8");
		assertCteEntry(def14, "following_seed", "query13");

		assertCteEntry(def13, "prior_seed", "query8");
		assertQueryLink(def13, "exists10", "query9");
		assertQueryLink(def13, "exists12", "query11");

		assertCteEntry(def7_6, "bbb", "query3");
		assertQueryLink(def7_6, "exists5", "query4");
		assertQueryLink(def7_3, "exists2", "query1");

		assertAliasBoundaryVisibility(extractor.getSymbolTable().toString(), "www", "xxx", "yyy", "zzz");
	}

	@Test
	public void nestedNestedWithDepth3CarriesCteListsAndExistsRefs() {
		final String sql = "WITH "
				+ "\n aaa AS (SELECT a1, a2, a3 FROM tab1),"
				+ "\n mid_nested AS ("
				+ "\n   WITH "
				+ "\n     bbb AS (SELECT b1, b2, b3 FROM tab2 WHERE EXISTS (SELECT 1 FROM aaa WHERE aaa.a3 = tab2.b3)),"
				+ "\n     ccc AS (SELECT c1, c2, c3 FROM tab3 WHERE EXISTS (SELECT 1 FROM bbb WHERE bbb.b3 = tab3.c3)),"
				+ "\n     deep_nested AS ("
				+ "\n       WITH "
				+ "\n         ddd AS (SELECT d1, d2, d3 FROM tab4 WHERE EXISTS (SELECT 1 FROM ccc WHERE ccc.c3 = tab4.d3)"
				+ "\n           AND EXISTS (SELECT 1 FROM aaa WHERE aaa.a2 = tab4.d2)),"
				+ "\n         eee AS (SELECT e1, e2, e3 FROM tab5 WHERE EXISTS (SELECT 1 FROM ddd WHERE ddd.d3 = tab5.e3))"
				+ "\n       SELECT ddd.d1 AS d1, eee.e2 AS e2, eee.e3 AS e3"
				+ "\n       FROM ddd JOIN eee ON ddd.d1 = eee.e1"
				+ "\n     )"
				+ "\n   SELECT bbb.b1 AS b1, ccc.c2 AS c2, deep_nested.e2 AS e2"
				+ "\n   FROM bbb JOIN ccc ON bbb.b1 = ccc.c1"
				+ "\n   JOIN deep_nested ON ccc.c1 = deep_nested.d1"
				+ "\n ),"
				+ "\n fff AS (SELECT f1, f2, f3 FROM tab6 WHERE EXISTS (SELECT 1 FROM mid_nested WHERE mid_nested.e2 = tab6.f2))"
				+ "\n SELECT www.b1, xxx.e2, yyy.f2"
				+ "\n FROM mid_nested AS www"
				+ "\n JOIN mid_nested AS xxx ON www.b1 = xxx.b1"
				+ "\n JOIN fff AS yyy ON xxx.e2 = yyy.f2";

		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runParsertest(sql, parser);
		assertNoWalkerDiagnostics(extractor);

		Map<String, Object> top = topScope(extractor, "query20");
		Map<String, Object> def19 = scopeAt(top, "def_query19");
		Map<String, Object> def16 = scopeAt(top, "def_query16");
		Map<String, Object> def11 = scopeAt(top, "def_query16", "def_query15", "def_query11");
		Map<String, Object> def6 = scopeAt(top, "def_query16", "def_query15", "def_query6");
		Map<String, Object> def3 = scopeAt(top, "def_query16", "def_query15", "def_query3");
		Map<String, Object> def14 = scopeAt(top, "def_query16", "def_query15", "def_query14");

		assertCteEntry(def19, "mid_nested", "query16");
		assertQueryLink(def19, "exists18", "query17");
		assertCteEntry(def16, "deep_nested", "query15");

		assertQueryLink(def11, "exists10", "query9");
		assertQueryLink(def11, "exists8", "query7");
		assertCteEntry(def6, "bbb", "query3");
		assertQueryLink(def6, "exists5", "query4");
		assertQueryLink(def3, "exists2", "query1");
		assertCteEntry(def14, "ddd", "query11");
		assertQueryLink(def14, "exists13", "query12");

		assertAliasBoundaryVisibility(extractor.getSymbolTable().toString(), "www", "xxx", "yyy");
	}

	@Test
	public void nestedNestedWithExistsInAndScalarSubqueriesMapToQueryRefs() {
		final String sql = "WITH "
				+ "\n aaa AS (SELECT a1, a2, a3 FROM tab1),"
				+ "\n bbb_ccc AS ("
				+ "\n   WITH "
				+ "\n     bbb AS ("
				+ "\n       SELECT b1, b2, (SELECT max(aaa.a3) FROM aaa) AS b3"
				+ "\n       FROM tab2"
				+ "\n       WHERE tab2.b2 IN (SELECT a2 FROM aaa)"
				+ "\n     ),"
				+ "\n     ccc AS ("
				+ "\n       SELECT c1, c2, c3"
				+ "\n       FROM tab3"
				+ "\n       WHERE EXISTS (SELECT 1 FROM bbb WHERE bbb.b3 = tab3.c3)"
				+ "\n         AND tab3.c2 IN (SELECT b2 FROM bbb)"
				+ "\n     )"
				+ "\n   SELECT bbb.b1 AS b1, ccc.c1 AS c1, ccc.c2 AS c2, (SELECT min(ccc.c3) FROM ccc) AS c3_min"
				+ "\n   FROM bbb JOIN ccc ON bbb.b1 = ccc.c1"
				+ "\n ),"
				+ "\n nested_outer AS ("
				+ "\n   WITH prior_seed AS (SELECT aaa.a1 AS a1, aaa.a2 AS a2, aaa.a3 AS a3 FROM aaa),"
				+ "\n        following_seed AS (SELECT bbb_ccc.c1 AS c1, bbb_ccc.c2 AS c2, bbb_ccc.c3_min AS c3_min FROM bbb_ccc"
				+ "\n          WHERE EXISTS (SELECT 1 FROM prior_seed WHERE prior_seed.a2 = bbb_ccc.c2)"
				+ "\n            AND EXISTS (SELECT 1 FROM aaa WHERE aaa.a1 = bbb_ccc.c1))"
				+ "\n   SELECT prior_seed.a1 AS a1, following_seed.c2 AS c2, following_seed.c3_min AS c3_min"
				+ "\n   FROM prior_seed JOIN following_seed ON prior_seed.a1 = following_seed.c1"
				+ "\n )"
				+ "\n SELECT www.a1, xxx.c2, yyy.c3_min"
				+ "\n FROM nested_outer AS www"
				+ "\n JOIN bbb_ccc AS xxx ON www.a1 = xxx.b1"
				+ "\n JOIN nested_outer AS yyy ON xxx.c1 = yyy.a1";

		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runParsertest(sql, parser);
		assertNoWalkerDiagnostics(extractor);

		Map<String, Object> top = topScope(extractor, "query21");
		Map<String, Object> def20 = scopeAt(top, "def_query20");
		Map<String, Object> def19 = scopeAt(top, "def_query20", "def_query19");
		Map<String, Object> def13 = scopeAt(top, "def_query20", "def_query13");
		Map<String, Object> def10 = scopeAt(top, "def_query20", "def_query13", "def_query10");
		Map<String, Object> def8 = scopeAt(top, "def_query20", "def_query13", "def_query10", "def_query8");
		Map<String, Object> def5 = scopeAt(top, "def_query20", "def_query13", "def_query5");

		assertCteEntry(def20, "prior_seed", "query14");
		assertCteEntry(def20, "following_seed", "query19");

		assertCteEntry(def19, "prior_seed", "query14");
		assertQueryLink(def19, "exists18", "query17");
		assertQueryLink(def19, "exists16", "query15");

		assertCteEntry(def13, "bbb", "query5");
		assertCteEntry(def13, "ccc", "query10");
		assertCteEntry(def10, "bbb", "query5");
		assertCteEntry(def8, "bbb", "query5");
		assertQueryLink(def10, "exists7", "query6");
		assertQueryLink(def10, "in_list9", "query8");
		assertQueryLink(def13, "predicand12", "query11");
		assertQueryLink(def5, "in_list4", "query3");
		assertQueryLink(def5, "predicand2", "query1");

		assertAliasBoundaryVisibility(extractor.getSymbolTable().toString(), "www", "xxx", "yyy");
	}

	@Test
	public void nestedWithSetQualifiersAaaBbbThenCccDddEeeParsesWithoutErrors() {
		final String sql = "WITH "
				+ "\n aaa_bbb AS ("
				+ "\n   WITH "
				+ "\n     aaa AS (SELECT a1, a2, a3 FROM tab1),"
				+ "\n     bbb AS (SELECT b1, b2, b3 FROM tab2 UNION ALL SELECT aaa.a1 AS b1, aaa.a2 AS b2, aaa.a3 AS b3 FROM aaa)"
				+ "\n   SELECT aaa.a1 AS a1, bbb.b1 AS b1, bbb.b2 AS b2"
				+ "\n   FROM aaa JOIN bbb ON aaa.a1 = bbb.b1"
				+ "\n ),"
				+ "\n ccc AS (SELECT c1, c2, c3 FROM tab3 UNION DISTINCT SELECT aaa_bbb.a1 AS c1, aaa_bbb.b2 AS c2, aaa_bbb.b1 AS c3 FROM aaa_bbb),"
				+ "\n ddd AS (SELECT d1, d2, d3 FROM tab4 INTERSECT DISTINCT SELECT ccc.c1 AS d1, ccc.c2 AS d2, ccc.c3 AS d3 FROM ccc),"
				+ "\n eee AS (SELECT e1, e2, e3 FROM tab5 UNION ALL SELECT ddd.d1 AS e1, ddd.d2 AS e2, ddd.d3 AS e3 FROM ddd)"
				+ "\n SELECT www.a1, xxx.c2, yyy.d2, zzz.e2"
				+ "\n FROM aaa_bbb AS www"
				+ "\n JOIN ccc AS xxx ON www.a1 = xxx.c1"
				+ "\n JOIN ddd AS yyy ON xxx.c1 = yyy.d1"
				+ "\n JOIN eee AS zzz ON yyy.d1 = zzz.e1";

		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runParsertest(sql, parser);
		assertNoWalkerDiagnostics(extractor);
	}

	@Test
	public void nestedWithSetQualifiersAaaThenBbbCccThenDddEeeParsesWithoutErrors() {
		final String sql = "WITH "
				+ "\n aaa AS (SELECT a1, a2, a3 FROM tab1),"
				+ "\n bbb_ccc AS ("
				+ "\n   WITH "
				+ "\n     bbb AS (SELECT b1, b2, b3 FROM tab2 UNION DISTINCT SELECT aaa.a1 AS b1, aaa.a2 AS b2, aaa.a3 AS b3 FROM aaa),"
				+ "\n     ccc AS (SELECT c1, c2, c3 FROM tab3 INTERSECT DISTINCT SELECT bbb.b1 AS c1, bbb.b2 AS c2, bbb.b3 AS c3 FROM bbb)"
				+ "\n   SELECT bbb.b1 AS b1, ccc.c1 AS c1, ccc.c2 AS c2"
				+ "\n   FROM bbb JOIN ccc ON bbb.b1 = ccc.c1"
				+ "\n ),"
				+ "\n ddd AS (SELECT d1, d2, d3 FROM tab4 UNION ALL SELECT bbb_ccc.c1 AS d1, bbb_ccc.c2 AS d2, bbb_ccc.b1 AS d3 FROM bbb_ccc),"
				+ "\n eee AS (SELECT e1, e2, e3 FROM tab5 INTERSECT DISTINCT SELECT ddd.d1 AS e1, ddd.d2 AS e2, ddd.d3 AS e3 FROM ddd)"
				+ "\n SELECT www.a1, xxx.c2, yyy.d2, zzz.e2"
				+ "\n FROM aaa AS www"
				+ "\n JOIN bbb_ccc AS xxx ON www.a1 = xxx.b1"
				+ "\n JOIN ddd AS yyy ON xxx.c1 = yyy.d1"
				+ "\n JOIN eee AS zzz ON yyy.d1 = zzz.e1";

		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runParsertest(sql, parser);
		assertNoWalkerDiagnostics(extractor);
	}

	@Test
	public void nestedWithSetQualifiersAaaBbbThenCccDddThenEeeParsesWithoutErrors() {
		final String sql = "WITH "
				+ "\n aaa AS (SELECT a1, a2, a3 FROM tab1),"
				+ "\n bbb AS (SELECT b1, b2, b3 FROM tab2 UNION ALL SELECT aaa.a1 AS b1, aaa.a2 AS b2, aaa.a3 AS b3 FROM aaa),"
				+ "\n ccc_ddd AS ("
				+ "\n   WITH "
				+ "\n     ccc AS (SELECT c1, c2, c3 FROM tab3 UNION DISTINCT SELECT bbb.b1 AS c1, bbb.b2 AS c2, bbb.b3 AS c3 FROM bbb),"
				+ "\n     ddd AS (SELECT d1, d2, d3 FROM tab4 INTERSECT DISTINCT SELECT ccc.c1 AS d1, ccc.c2 AS d2, ccc.c3 AS d3 FROM ccc)"
				+ "\n   SELECT ccc.c1 AS c1, ddd.d1 AS d1, ddd.d2 AS d2"
				+ "\n   FROM ccc JOIN ddd ON ccc.c1 = ddd.d1"
				+ "\n ),"
				+ "\n eee AS (SELECT e1, e2, e3 FROM tab5 UNION DISTINCT SELECT ccc_ddd.c1 AS e1, ccc_ddd.d2 AS e2, ccc_ddd.d1 AS e3 FROM ccc_ddd)"
				+ "\n SELECT www.a1, xxx.b2, yyy.d2, zzz.e2"
				+ "\n FROM aaa AS www"
				+ "\n JOIN bbb AS xxx ON www.a1 = xxx.b1"
				+ "\n JOIN ccc_ddd AS yyy ON xxx.b1 = yyy.c1"
				+ "\n JOIN eee AS zzz ON yyy.d1 = zzz.e1";

		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runParsertest(sql, parser);
		assertNoWalkerDiagnostics(extractor);
	}

	@Test
	public void nestedWithSetQualifiersAaaBbbCccThenDddEeeParsesWithoutErrors() {
		final String sql = "WITH "
				+ "\n aaa AS (SELECT a1, a2, a3 FROM tab1),"
				+ "\n bbb AS (SELECT b1, b2, b3 FROM tab2 UNION DISTINCT SELECT aaa.a1 AS b1, aaa.a2 AS b2, aaa.a3 AS b3 FROM aaa),"
				+ "\n ccc AS (SELECT c1, c2, c3 FROM tab3 INTERSECT DISTINCT SELECT bbb.b1 AS c1, bbb.b2 AS c2, bbb.b3 AS c3 FROM bbb),"
				+ "\n ddd_eee AS ("
				+ "\n   WITH "
				+ "\n     ddd AS (SELECT d1, d2, d3 FROM tab4 UNION ALL SELECT ccc.c1 AS d1, ccc.c2 AS d2, ccc.c3 AS d3 FROM ccc),"
				+ "\n     eee AS (SELECT e1, e2, e3 FROM tab5 INTERSECT DISTINCT SELECT ddd.d1 AS e1, ddd.d2 AS e2, ddd.d3 AS e3 FROM ddd)"
				+ "\n   SELECT ddd.d1 AS d1, eee.e1 AS e1, eee.e2 AS e2"
				+ "\n   FROM ddd JOIN eee ON ddd.d1 = eee.e1"
				+ "\n )"
				+ "\n SELECT www.a1, xxx.b2, yyy.c2, zzz.e2"
				+ "\n FROM aaa AS www"
				+ "\n JOIN bbb AS xxx ON www.a1 = xxx.b1"
				+ "\n JOIN ccc AS yyy ON xxx.b1 = yyy.c1"
				+ "\n JOIN ddd_eee AS zzz ON yyy.c1 = zzz.d1";

		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runParsertest(sql, parser);
		assertNoWalkerDiagnostics(extractor);
	}

	@Test
	public void nestedWithExistsAliasHeavyExemplarParsesWithoutErrors() {
		final String sql = "WITH "
				+ "\n aaa AS (SELECT kkk.a1, kkk.a2, kkk.a3 FROM tab1 AS kkk),"
				+ "\n bbb_ccc AS ("
				+ "\n   WITH "
				+ "\n     bbb AS (SELECT lll.b1, lll.b2, lll.b3 FROM tab2 AS lll WHERE EXISTS (SELECT 1 FROM aaa AS mmm WHERE mmm.a3 = lll.b3)),"
				+ "\n     ccc AS (SELECT nnn.c1, nnn.c2, nnn.c3 FROM tab3 AS nnn WHERE EXISTS (SELECT 1 FROM bbb AS ooo WHERE ooo.b3 = nnn.c3))"
				+ "\n   SELECT ppp.b1 AS b1, qqq.c1 AS c1, qqq.c2 AS c2"
				+ "\n   FROM bbb AS ppp JOIN ccc AS qqq ON ppp.b1 = qqq.c1"
				+ "\n )"
				+ "\n SELECT rrr.a1, sss.c2"
				+ "\n FROM aaa AS rrr"
				+ "\n JOIN bbb_ccc AS sss ON rrr.a1 = sss.b1";

		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runParsertest(sql, parser);
		assertNoWalkerDiagnostics(extractor);
	}

	@Test
	public void nestedWithUnionAliasHeavyExemplarParsesWithoutErrors() {
		final String sql = "WITH "
				+ "\n aaa AS (SELECT kkk.a1, kkk.a2, kkk.a3 FROM tab1 AS kkk),"
				+ "\n bbb AS (SELECT lll.b1, lll.b2, lll.b3 FROM tab2 AS lll UNION DISTINCT SELECT mmm.a1 AS b1, mmm.a2 AS b2, mmm.a3 AS b3 FROM aaa AS mmm),"
				+ "\n ccc AS (SELECT nnn.c1, nnn.c2, nnn.c3 FROM tab3 AS nnn UNION ALL SELECT ooo.b1 AS c1, ooo.b2 AS c2, ooo.b3 AS c3 FROM bbb AS ooo)"
				+ "\n SELECT ppp.a1, qqq.b2, rrr.c2"
				+ "\n FROM aaa AS ppp"
				+ "\n JOIN bbb AS qqq ON ppp.a1 = qqq.b1"
				+ "\n JOIN ccc AS rrr ON qqq.b1 = rrr.c1";

		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runParsertest(sql, parser);
		assertNoWalkerDiagnostics(extractor);
	}

	@Test
	public void nestedWithIntersectAliasHeavyExemplarParsesWithoutErrors() {
		final String sql = "WITH "
				+ "\n aaa AS (SELECT kkk.a1, kkk.a2, kkk.a3 FROM tab1 AS kkk),"
				+ "\n bbb AS (SELECT lll.b1, lll.b2, lll.b3 FROM tab2 AS lll INTERSECT DISTINCT SELECT mmm.a1 AS b1, mmm.a2 AS b2, mmm.a3 AS b3 FROM aaa AS mmm),"
				+ "\n ccc AS (SELECT nnn.c1, nnn.c2, nnn.c3 FROM tab3 AS nnn INTERSECT DISTINCT SELECT ooo.b1 AS c1, ooo.b2 AS c2, ooo.b3 AS c3 FROM bbb AS ooo)"
				+ "\n SELECT ppp.a1, qqq.b2, rrr.c2"
				+ "\n FROM aaa AS ppp"
				+ "\n JOIN bbb AS qqq ON ppp.a1 = qqq.b1"
				+ "\n JOIN ccc AS rrr ON qqq.b1 = rrr.c1";

		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runParsertest(sql, parser);
		assertNoWalkerDiagnostics(extractor);
	}

	@Test
	public void nestedWithJoinAliasHeavyExemplarParsesWithoutErrors() {
		final String sql = "WITH "
				+ "\n aaa AS (SELECT kkk.a1, kkk.a2, kkk.a3 FROM tab1 AS kkk),"
				+ "\n bbb AS (SELECT lll.b1, lll.b2, lll.b3 FROM tab2 AS lll JOIN aaa AS mmm ON mmm.a3 = lll.b3),"
				+ "\n ccc AS (SELECT nnn.c1, nnn.c2, nnn.c3 FROM tab3 AS nnn JOIN bbb AS ooo ON ooo.b3 = nnn.c3)"
				+ "\n SELECT ppp.a1, qqq.b2, rrr.c2"
				+ "\n FROM aaa AS ppp"
				+ "\n JOIN bbb AS qqq ON ppp.a1 = qqq.b1"
				+ "\n JOIN ccc AS rrr ON qqq.b1 = rrr.c1";

		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runParsertest(sql, parser);
		assertNoWalkerDiagnostics(extractor);
	}

	@Test
	public void nestedWithScalarHavingAliasHeavyExemplarParsesWithoutErrors() {
		final String sql = "WITH "
				+ "\n aaa AS (SELECT kkk.a1, kkk.a2, kkk.a3 FROM tab1 AS kkk),"
				+ "\n bbb AS (SELECT lll.b1, lll.b2, max(lll.b3) AS b3 FROM tab2 AS lll GROUP BY lll.b1, lll.b2"
				+ "\n        HAVING max(lll.b3) >= (SELECT max(mmm.a3) FROM aaa AS mmm)),"
				+ "\n ccc AS (SELECT nnn.c1, nnn.c2, max(nnn.c3) AS c3 FROM tab3 AS nnn GROUP BY nnn.c1, nnn.c2"
				+ "\n        HAVING max(nnn.c3) >= (SELECT max(ooo.b3) FROM bbb AS ooo))"
				+ "\n SELECT ppp.a1, qqq.b2, rrr.c2"
				+ "\n FROM aaa AS ppp"
				+ "\n JOIN bbb AS qqq ON ppp.a1 = qqq.b1"
				+ "\n JOIN ccc AS rrr ON qqq.b1 = rrr.c1";

		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runParsertest(sql, parser);
		assertNoWalkerDiagnostics(extractor);
	}

	@Test
	public void nestedWithScalarWhereAliasHeavyExemplarParsesWithoutErrors() {
		final String sql = "WITH "
				+ "\n aaa AS (SELECT kkk.a1, kkk.a2, kkk.a3 FROM tab1 AS kkk),"
				+ "\n bbb AS (SELECT lll.b1, lll.b2, lll.b3 FROM tab2 AS lll"
				+ "\n        WHERE lll.b3 = (SELECT max(mmm.a3) FROM aaa AS mmm WHERE mmm.a2 IS NOT NULL)),"
				+ "\n ccc AS (SELECT nnn.c1, nnn.c2, nnn.c3 FROM tab3 AS nnn"
				+ "\n        WHERE nnn.c3 = (SELECT max(ooo.b3) FROM bbb AS ooo WHERE ooo.b1 = nnn.c1))"
				+ "\n SELECT ppp.a1, qqq.b2, rrr.c2"
				+ "\n FROM aaa AS ppp"
				+ "\n JOIN bbb AS qqq ON ppp.a1 = qqq.b1"
				+ "\n JOIN ccc AS rrr ON qqq.b1 = rrr.c1";

		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runParsertest(sql, parser);
		assertNoWalkerDiagnostics(extractor);
	}

	@Test
	public void nestedWithScalarSelectListAliasHeavyExemplarParsesWithoutErrors() {
		final String sql = "WITH "
				+ "\n aaa AS (SELECT kkk.a1, kkk.a2, kkk.a3 FROM tab1 AS kkk),"
				+ "\n bbb AS (SELECT lll.b1, lll.b2, (SELECT max(mmm.a3) FROM aaa AS mmm) AS b3 FROM tab2 AS lll),"
				+ "\n ccc AS (SELECT nnn.c1, nnn.c2, (SELECT max(ooo.b3) FROM bbb AS ooo) AS c3 FROM tab3 AS nnn)"
				+ "\n SELECT ppp.a1, qqq.b2, rrr.c2"
				+ "\n FROM aaa AS ppp"
				+ "\n JOIN bbb AS qqq ON ppp.a1 = qqq.b1"
				+ "\n JOIN ccc AS rrr ON qqq.b1 = rrr.c1";

		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runParsertest(sql, parser);
		assertNoWalkerDiagnostics(extractor);
	}

	@Test
	public void nestedWithExistsAliasHeavyValuesSubqueryExemplarParsesWithoutErrors() {
		final String sql = "WITH "
				+ "\n aaa AS (SELECT kkk.a1, kkk.a2, kkk.a3 FROM tab1 AS kkk),"
				+ "\n bbb_ccc AS ("
				+ "\n   WITH "
				+ "\n     bbb AS (SELECT lll.b1, lll.b2, lll.b3 FROM tab2 AS lll WHERE EXISTS (SELECT 1 FROM (VALUES (lll.b3, 902, 903)) AS hhh WHERE lll.b3 IS NOT NULL)),"
				+ "\n     ccc AS (SELECT nnn.c1, nnn.c2, nnn.c3 FROM tab3 AS nnn WHERE EXISTS (SELECT 1 FROM (VALUES (nnn.c3, 904, 905)) AS hhh WHERE nnn.c3 IS NOT NULL))"
				+ "\n   SELECT ppp.b1 AS b1, qqq.c1 AS c1, qqq.c2 AS c2"
				+ "\n   FROM bbb AS ppp JOIN ccc AS qqq ON ppp.b1 = qqq.c1"
				+ "\n )"
				+ "\n SELECT rrr.a1, sss.c2"
				+ "\n FROM aaa AS rrr"
				+ "\n JOIN bbb_ccc AS sss ON rrr.a1 = sss.b1";

		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runParsertest(sql, parser);
		assertNoWalkerDiagnostics(extractor);
	}

	@Test
	public void nestedWithUnionAliasHeavyValuesSubqueryExemplarParsesWithoutErrors() {
		final String sql = "WITH "
				+ "\n aaa AS (SELECT kkk.a1, kkk.a2, kkk.a3 FROM tab1 AS kkk),"
				+ "\n bbb AS (SELECT lll.b1, lll.b2, lll.b3 FROM tab2 AS lll UNION DISTINCT SELECT mmm.a1 AS b1, mmm.a2 AS b2, mmm.a3 AS b3 FROM aaa AS mmm),"
				+ "\n ccc AS (SELECT nnn.c1, nnn.c2, nnn.c3 FROM tab3 AS nnn UNION ALL "
				+ "SELECT ooo.b1 AS c1, ooo.b2 AS c2, ooo.b3 AS c3 FROM bbb AS ooo "
				+ "WHERE EXISTS (SELECT 1 FROM (VALUES (ooo.b3, 906, 907)) AS hhh WHERE ooo.b3 IS NOT NULL))"
				+ "\n SELECT qqq.a1, rrr.b2, sss.c2"
				+ "\n FROM aaa AS qqq"
				+ "\n JOIN bbb AS rrr ON qqq.a1 = rrr.b1"
				+ "\n JOIN ccc AS sss ON rrr.b1 = sss.c1";

		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runParsertest(sql, parser);
		assertNoWalkerDiagnostics(extractor);
	}

	@Test
	public void nestedWithIntersectAliasHeavyValuesSubqueryExemplarParsesWithoutErrors() {
		final String sql = "WITH "
				+ "\n aaa AS (SELECT kkk.a1, kkk.a2, kkk.a3 FROM tab1 AS kkk),"
				+ "\n bbb AS (SELECT lll.b1, lll.b2, lll.b3 FROM tab2 AS lll INTERSECT DISTINCT SELECT mmm.a1 AS b1, mmm.a2 AS b2, mmm.a3 AS b3 FROM aaa AS mmm),"
				+ "\n ccc AS (SELECT nnn.c1, nnn.c2, nnn.c3 FROM tab3 AS nnn INTERSECT DISTINCT "
				+ "SELECT ooo.b1 AS c1, ooo.b2 AS c2, ooo.b3 AS c3 FROM bbb AS ooo "
				+ "WHERE EXISTS (SELECT 1 FROM (VALUES (ooo.b2, 908, 909)) AS hhh WHERE ooo.b2 IS NOT NULL))"
				+ "\n SELECT qqq.a1, rrr.b2, sss.c2"
				+ "\n FROM aaa AS qqq"
				+ "\n JOIN bbb AS rrr ON qqq.a1 = rrr.b1"
				+ "\n JOIN ccc AS sss ON rrr.b1 = sss.c1";

		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runParsertest(sql, parser);
		assertNoWalkerDiagnostics(extractor);
	}

	@Test
	public void nestedWithJoinAliasHeavyValuesSubqueryExemplarParsesWithoutErrors() {
		final String sql = "WITH "
				+ "\n aaa AS (SELECT kkk.a1, kkk.a2, kkk.a3 FROM tab1 AS kkk),"
				+ "\n bbb AS (SELECT lll.b1, lll.b2, lll.b3 FROM tab2 AS lll JOIN aaa AS mmm ON mmm.a3 = lll.b3),"
				+ "\n ccc AS (SELECT nnn.c1, nnn.c2, nnn.c3 FROM tab3 AS nnn JOIN bbb AS ooo "
				+ "ON ooo.b3 = nnn.c3 AND EXISTS (SELECT 1 FROM (VALUES (nnn.c3, 910, 911)) AS hhh WHERE nnn.c3 IS NOT NULL))"
				+ "\n SELECT qqq.a1, rrr.b2, sss.c2"
				+ "\n FROM aaa AS qqq"
				+ "\n JOIN bbb AS rrr ON qqq.a1 = rrr.b1"
				+ "\n JOIN ccc AS sss ON rrr.b1 = sss.c1";

		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runParsertest(sql, parser);
		assertNoWalkerDiagnostics(extractor);
	}

	@Test
	public void nestedWithScalarHavingAliasHeavyValuesSubqueryExemplarParsesWithoutErrors() {
		final String sql = "WITH "
				+ "\n aaa AS (SELECT kkk.a1, kkk.a2, kkk.a3 FROM tab1 AS kkk),"
				+ "\n bbb AS (SELECT lll.b1, lll.b2, max(lll.b3) AS b3 FROM tab2 AS lll GROUP BY lll.b1, lll.b2"
				+ "\n        HAVING max(lll.b3) >= (SELECT max(1) FROM (VALUES (lll.b3, 912, 913)) AS hhh)),"
				+ "\n ccc AS (SELECT nnn.c1, nnn.c2, max(nnn.c3) AS c3 FROM tab3 AS nnn GROUP BY nnn.c1, nnn.c2"
				+ "\n        HAVING max(nnn.c3) >= (SELECT max(1) FROM (VALUES (nnn.c3, 914, 915)) AS hhh))"
				+ "\n SELECT ppp.a1, qqq.b2, rrr.c2"
				+ "\n FROM aaa AS ppp"
				+ "\n JOIN bbb AS qqq ON ppp.a1 = qqq.b1"
				+ "\n JOIN ccc AS rrr ON qqq.b1 = rrr.c1";

		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runParsertest(sql, parser);
		assertNoWalkerDiagnostics(extractor);
	}

	@Test
	public void nestedWithScalarWhereAliasHeavyValuesSubqueryExemplarParsesWithoutErrors() {
		final String sql = "WITH "
				+ "\n aaa AS (SELECT kkk.a1, kkk.a2, kkk.a3 FROM tab1 AS kkk),"
				+ "\n bbb AS (SELECT lll.b1, lll.b2, lll.b3 FROM tab2 AS lll"
				+ "\n        WHERE lll.b3 >= (SELECT max(1) FROM (VALUES (lll.b3, 916, 917)) AS hhh)),"
				+ "\n ccc AS (SELECT nnn.c1, nnn.c2, nnn.c3 FROM tab3 AS nnn"
				+ "\n        WHERE nnn.c3 >= (SELECT max(1) FROM (VALUES (nnn.c3, 918, 919)) AS hhh))"
				+ "\n SELECT ppp.a1, qqq.b2, rrr.c2"
				+ "\n FROM aaa AS ppp"
				+ "\n JOIN bbb AS qqq ON ppp.a1 = qqq.b1"
				+ "\n JOIN ccc AS rrr ON qqq.b1 = rrr.c1";

		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runParsertest(sql, parser);
		assertNoWalkerDiagnostics(extractor);
	}

	@Test
	public void nestedWithScalarSelectListAliasHeavyValuesSubqueryExemplarParsesWithoutErrors() {
		final String sql = "WITH "
				+ "\n aaa AS (SELECT kkk.a1, kkk.a2, kkk.a3 FROM tab1 AS kkk),"
				+ "\n bbb AS (SELECT lll.b1, lll.b2, (SELECT max(1) FROM (VALUES (lll.b3, 920, 921)) AS hhh) AS b3 FROM tab2 AS lll),"
				+ "\n ccc AS (SELECT nnn.c1, nnn.c2, (SELECT max(1) FROM (VALUES (nnn.c3, 922, 923)) AS hhh) AS c3 FROM tab3 AS nnn)"
				+ "\n SELECT ppp.a1, qqq.b2, rrr.c2"
				+ "\n FROM aaa AS ppp"
				+ "\n JOIN bbb AS qqq ON ppp.a1 = qqq.b1"
				+ "\n JOIN ccc AS rrr ON qqq.b1 = rrr.c1";

		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runParsertest(sql, parser);
		assertNoWalkerDiagnostics(extractor);
	}

	@Test
	public void nestedWithExistsAliasHeavyUnnamedValuesRejectsNamedColumnReferences() {
		final String sql = "WITH "
				+ "\n aaa AS (SELECT kkk.a1, kkk.a2, kkk.a3 FROM tab1 AS kkk),"
				+ "\n bbb AS (SELECT lll.b1, lll.b2, lll.b3 FROM tab2 AS lll"
				+ "\n        WHERE EXISTS (SELECT 1 FROM (VALUES (lll.b3, lll.b2, lll.b1)) AS hhh WHERE hhh.h1 = lll.b3))"
				+ "\n SELECT aaa.a1, bbb.b2"
				+ "\n FROM aaa JOIN bbb ON aaa.a1 = bbb.b1";

		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runParsertest(sql, parser);
		Snippet snippet = extractor.getSnippet();
		assertDiagnosticCountBySeverity(snippet,
				"QUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIAS",
				ParseDiagnostic.Severity.FATAL,
				null,
				null,
				1);
	}

	@Test
	public void nestedWithUnionAliasHeavyUnnamedValuesRejectsNamedColumnReferences() {
		final String sql = "WITH "
				+ "\n aaa AS (SELECT kkk.a1, kkk.a2, kkk.a3 FROM tab1 AS kkk),"
				+ "\n bbb AS (SELECT lll.b1, lll.b2, lll.b3 FROM tab2 AS lll UNION DISTINCT SELECT mmm.a1 AS b1, mmm.a2 AS b2, mmm.a3 AS b3 FROM aaa AS mmm),"
				+ "\n ccc AS (SELECT nnn.c1, nnn.c2, nnn.c3 FROM tab3 AS nnn UNION ALL "
				+ "SELECT ooo.b1 AS c1, ooo.b2 AS c2, ooo.b3 AS c3 FROM bbb AS ooo "
				+ "WHERE ooo.b3 = (SELECT max(hhh.h1) FROM (VALUES (ooo.b3, ooo.b2, ooo.b1)) AS hhh))"
				+ "\n SELECT qqq.a1, rrr.b2, sss.c2"
				+ "\n FROM aaa AS qqq"
				+ "\n JOIN bbb AS rrr ON qqq.a1 = rrr.b1"
				+ "\n JOIN ccc AS sss ON rrr.b1 = sss.c1";

		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runParsertest(sql, parser);
		Snippet snippet = extractor.getSnippet();
		assertDiagnosticCountBySeverity(snippet,
				"QUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIAS",
				ParseDiagnostic.Severity.FATAL,
				null,
				null,
				1);
	}

	@Test
	public void nestedWithIntersectAliasHeavyUnnamedValuesRejectsNamedColumnReferences() {
		final String sql = "WITH "
				+ "\n aaa AS (SELECT kkk.a1, kkk.a2, kkk.a3 FROM tab1 AS kkk),"
				+ "\n bbb AS (SELECT lll.b1, lll.b2, lll.b3 FROM tab2 AS lll INTERSECT DISTINCT SELECT mmm.a1 AS b1, mmm.a2 AS b2, mmm.a3 AS b3 FROM aaa AS mmm),"
				+ "\n ccc AS (SELECT nnn.c1, nnn.c2, nnn.c3 FROM tab3 AS nnn INTERSECT DISTINCT "
				+ "SELECT ooo.b1 AS c1, ooo.b2 AS c2, ooo.b3 AS c3 FROM bbb AS ooo "
				+ "WHERE ooo.b2 <= (SELECT max(hhh.h1) FROM (VALUES (ooo.b2, ooo.b1, ooo.b3)) AS hhh))"
				+ "\n SELECT qqq.a1, rrr.b2, sss.c2"
				+ "\n FROM aaa AS qqq"
				+ "\n JOIN bbb AS rrr ON qqq.a1 = rrr.b1"
				+ "\n JOIN ccc AS sss ON rrr.b1 = sss.c1";

		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runParsertest(sql, parser);
		Snippet snippet = extractor.getSnippet();
		assertDiagnosticCountBySeverity(snippet,
				"QUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIAS",
				ParseDiagnostic.Severity.FATAL,
				null,
				null,
				1);
	}

	@Test
	public void nestedWithJoinAliasHeavyUnnamedValuesRejectsNamedColumnReferences() {
		final String sql = "WITH "
				+ "\n aaa AS (SELECT kkk.a1, kkk.a2, kkk.a3 FROM tab1 AS kkk),"
				+ "\n bbb AS (SELECT lll.b1, lll.b2, lll.b3 FROM tab2 AS lll JOIN aaa AS mmm ON mmm.a3 = lll.b3),"
				+ "\n ccc AS (SELECT nnn.c1, nnn.c2, nnn.c3 FROM tab3 AS nnn JOIN bbb AS ooo "
				+ "ON ooo.b3 = (SELECT max(hhh.h1) FROM (VALUES (nnn.c3, nnn.c2, nnn.c1)) AS hhh))"
				+ "\n SELECT qqq.a1, rrr.b2, sss.c2"
				+ "\n FROM aaa AS qqq"
				+ "\n JOIN bbb AS rrr ON qqq.a1 = rrr.b1"
				+ "\n JOIN ccc AS sss ON rrr.b1 = sss.c1";

		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runParsertest(sql, parser);
		Snippet snippet = extractor.getSnippet();
		assertDiagnosticCountBySeverity(snippet,
				"QUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIAS",
				ParseDiagnostic.Severity.FATAL,
				null,
				null,
				1);
	}

	@Test
	public void nestedWithScalarHavingAliasHeavyUnnamedValuesRejectsNamedColumnReferences() {
		final String sql = "WITH "
				+ "\n aaa AS (SELECT kkk.a1, kkk.a2, kkk.a3 FROM tab1 AS kkk),"
				+ "\n bbb AS (SELECT lll.b1, lll.b2, max(lll.b3) AS b3 FROM tab2 AS lll GROUP BY lll.b1, lll.b2"
				+ "\n        HAVING max(lll.b3) >= (SELECT max(hhh.h1) FROM (VALUES (lll.b3, lll.b2, lll.b1)) AS hhh)),"
				+ "\n ccc AS (SELECT nnn.c1, nnn.c2, max(nnn.c3) AS c3 FROM tab3 AS nnn GROUP BY nnn.c1, nnn.c2"
				+ "\n        HAVING max(nnn.c3) >= (SELECT max(hhh.h1) FROM (VALUES (nnn.c3, nnn.c2, nnn.c1)) AS hhh))"
				+ "\n SELECT ppp.a1, qqq.b2, rrr.c2"
				+ "\n FROM aaa AS ppp"
				+ "\n JOIN bbb AS qqq ON ppp.a1 = qqq.b1"
				+ "\n JOIN ccc AS rrr ON qqq.b1 = rrr.c1";

		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runParsertest(sql, parser);
		Snippet snippet = extractor.getSnippet();
		assertDiagnosticCountBySeverity(snippet,
				"QUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIAS",
				ParseDiagnostic.Severity.FATAL,
				null,
				null,
				2);
	}

	@Test
	public void nestedWithScalarWhereAliasHeavyUnnamedValuesRejectsNamedColumnReferences() {
		final String sql = "WITH "
				+ "\n aaa AS (SELECT kkk.a1, kkk.a2, kkk.a3 FROM tab1 AS kkk),"
				+ "\n bbb AS (SELECT lll.b1, lll.b2, lll.b3 FROM tab2 AS lll"
				+ "\n        WHERE lll.b3 = (SELECT max(hhh.h1) FROM (VALUES (lll.b3, lll.b2, lll.b1)) AS hhh)),"
				+ "\n ccc AS (SELECT nnn.c1, nnn.c2, nnn.c3 FROM tab3 AS nnn"
				+ "\n        WHERE nnn.c3 = (SELECT max(hhh.h1) FROM (VALUES (nnn.c3, nnn.c2, nnn.c1)) AS hhh))"
				+ "\n SELECT ppp.a1, qqq.b2, rrr.c2"
				+ "\n FROM aaa AS ppp"
				+ "\n JOIN bbb AS qqq ON ppp.a1 = qqq.b1"
				+ "\n JOIN ccc AS rrr ON qqq.b1 = rrr.c1";

		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runParsertest(sql, parser);
		Snippet snippet = extractor.getSnippet();
		assertDiagnosticCountBySeverity(snippet,
				"QUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIAS",
				ParseDiagnostic.Severity.FATAL,
				null,
				null,
				2);
	}

	@Test
	public void nestedWithScalarSelectListAliasHeavyUnnamedValuesRejectsNamedColumnReferences() {
		final String sql = "WITH "
				+ "\n aaa AS (SELECT kkk.a1, kkk.a2, kkk.a3 FROM tab1 AS kkk),"
				+ "\n bbb AS (SELECT lll.b1, lll.b2, (SELECT max(hhh.h1) FROM (VALUES (lll.b3, lll.b2, lll.b1)) AS hhh) AS b3 FROM tab2 AS lll),"
				+ "\n ccc AS (SELECT nnn.c1, nnn.c2, (SELECT max(hhh.h1) FROM (VALUES (nnn.c3, nnn.c2, nnn.c1)) AS hhh) AS c3 FROM tab3 AS nnn)"
				+ "\n SELECT ppp.a1, qqq.b2, rrr.c2"
				+ "\n FROM aaa AS ppp"
				+ "\n JOIN bbb AS qqq ON ppp.a1 = qqq.b1"
				+ "\n JOIN ccc AS rrr ON qqq.b1 = rrr.c1";

		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runParsertest(sql, parser);
		Snippet snippet = extractor.getSnippet();
		assertDiagnosticCountBySeverity(snippet,
				"QUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIAS",
				ParseDiagnostic.Severity.FATAL,
				null,
				null,
				2);
	}

	@Test
	public void nestedWithExistsAliasHeavyValuesCteVirtualColumnsExemplarParsesWithoutErrors() {
		final String sql = "WITH "
				+ "\n aaa AS ((VALUES (11, 21, 31), (12, 22, 32)) AS hhh(a1, a2, a3)),"
				+ "\n bbb_ccc AS ("
				+ "\n   WITH "
				+ "\n     bbb AS (SELECT lll.b1, lll.b2, lll.b3 FROM tab2 AS lll WHERE EXISTS (SELECT 1 FROM aaa AS mmm WHERE mmm.a3 = lll.b3)),"
				+ "\n     ccc AS (SELECT nnn.c1, nnn.c2, nnn.c3 FROM tab3 AS nnn WHERE EXISTS (SELECT 1 FROM aaa AS vvv WHERE vvv.a1 = nnn.c1))"
				+ "\n   SELECT ppp.b1 AS b1, qqq.c1 AS c1, qqq.c2 AS c2"
				+ "\n   FROM bbb AS ppp JOIN ccc AS qqq ON ppp.b1 = qqq.c1"
				+ "\n )"
				+ "\n SELECT sss.b1, sss.c2, ttt.a2"
				+ "\n FROM bbb_ccc AS sss"
				+ "\n JOIN aaa AS ttt ON ttt.a1 = sss.b1";

		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runParsertest(sql, parser);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong", "{SQL={with={1={cte={values={columns={1={column={name=a1, table_ref=null}}, 2={column={name=a2, table_ref=null}}, 3={column={name=a3, table_ref=null}}}, alias=hhh, matrix={1={row={1={literal=11}, 2={literal=21}, 3={literal=31}}}, 2={row={1={literal=12}, 2={literal=22}, 3={literal=32}}}}}}, alias=aaa}, 2={cte={with={1={cte={select={1={column={name=b1, table_ref=lll}}, 2={column={name=b2, table_ref=lll}}, 3={column={name=b3, table_ref=lll}}}, from={table={alias=lll, table=tab2}}, where={exists={select={1={literal=1}}, from={table={alias=mmm, table=aaa}}, where={condition={left={column={name=a3, table_ref=mmm}}, right={column={name=b3, table_ref=lll}}, operator==}}, operator=EXISTS}}}, alias=bbb}, 2={cte={select={1={column={name=c1, table_ref=nnn}}, 2={column={name=c2, table_ref=nnn}}, 3={column={name=c3, table_ref=nnn}}}, from={table={alias=nnn, table=tab3}}, where={exists={select={1={literal=1}}, from={table={alias=vvv, table=aaa}}, where={condition={left={column={name=a1, table_ref=vvv}}, right={column={name=c1, table_ref=nnn}}, operator==}}, operator=EXISTS}}}, alias=ccc}}, query={select={1={column={name=b1, table_ref=ppp}, alias=b1}, 2={column={name=c1, table_ref=qqq}, alias=c1}, 3={column={name=c2, table_ref=qqq}, alias=c2}}, from={join={1={table={alias=ppp, table=bbb}}, 2={join=JOIN, on={condition={left={column={name=b1, table_ref=ppp}}, right={column={name=c1, table_ref=qqq}}, operator==}}}, 3={table={alias=qqq, table=ccc}}}}}}, alias=bbb_ccc}}, query={select={1={column={name=b1, table_ref=sss}}, 2={column={name=c2, table_ref=sss}}, 3={column={name=a2, table_ref=ttt}}}, from={join={1={table={alias=sss, table=bbb_ccc}}, 2={join=JOIN, on={condition={left={column={name=a1, table_ref=ttt}}, right={column={name=b1, table_ref=sss}}, operator==}}}, 3={table={alias=ttt, table=aaa}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[a2, b1, c2]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab3={c3=[[@88,255:257='nnn',<381>,6:36]], c1=[[@80,239:241='nnn',<381>,6:20], [@109,333:335='nnn',<381>,6:114], [@121,366:368='qqq',<381>,7:24], [@145,440:442='qqq',<381>,8:47]], c2=[[@84,247:249='nnn',<381>,6:28], [@127,380:382='qqq',<381>,7:38]]}, tab2={b2=[[@45,123:125='lll',<381>,5:28]], b3=[[@49,131:133='lll',<381>,5:36], [@70,209:211='lll',<381>,5:114]], b1=[[@41,115:117='lll',<381>,5:20], [@115,352:354='ppp',<381>,7:10], [@141,431:433='ppp',<381>,8:38]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{values0={a1=[[@25,58:59='a1',<381>,2:52], [@105,324:326='vvv',<381>,6:105], [@170,522:524='ttt',<381>,12:20]], a2=[[@27,62:63='a2',<381>,2:56], [@158,474:476='ttt',<381>,10:24]], a3=[[@29,66:67='a3',<381>,2:60], [@66,200:202='mmm',<381>,5:105]]}, query8={a2=[[@160,478:479='a2',<381>,10:28]], b1=[[@152,462:463='b1',<381>,10:12]], c2=[[@156,470:471='c2',<381>,10:20]]}, query4={unnamed_1=[[@99,300:300='1',<300>,6:81]]}, query6={c3=[[@90,259:260='c3',<381>,6:40]], c1=[[@82,243:244='c1',<381>,6:24], [@121,366:368='qqq',<381>,7:24], [@145,440:442='qqq',<381>,8:47]], c2=[[@86,251:252='c2',<381>,6:32], [@127,380:382='qqq',<381>,7:38]]}, query7={c1=[[@125,376:377='c1',<381>,7:34]], b1=[[@119,362:363='b1',<381>,7:20], [@150,458:460='sss',<381>,10:8], [@174,531:533='sss',<381>,12:29]], c2=[[@131,390:391='c2',<381>,7:48], [@154,466:468='sss',<381>,10:16]]}, query1={unnamed_0=[[@60,176:176='1',<300>,5:81]]}, query3={b2=[[@47,127:128='b2',<381>,5:32]], b3=[[@51,135:136='b3',<381>,5:40]], b1=[[@43,119:120='b1',<381>,5:24], [@115,352:354='ppp',<381>,7:10], [@141,431:433='ppp',<381>,8:38]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query8={context_list={aaa=values0, bbb_ccc=query7, sss=query7, ttt=values0}, def_query7={context_list={aaa=values0, bbb=query3, ccc=query6, ppp=query3, qqq=query6}, def_query6={context_list={aaa=values0, bbb=query3}, query_dictionary={c3=[[@90,259:260='c3',<381>,6:40]], c1=[[@82,243:244='c1',<381>,6:24], [@121,366:368='qqq',<381>,7:24], [@145,440:442='qqq',<381>,8:47]], c2=[[@86,251:252='c2',<381>,6:32], [@127,380:382='qqq',<381>,7:38]]}, table_dictionary={tab3={c3=[[@88,255:257='nnn',<381>,6:36]], c1=[[@80,239:241='nnn',<381>,6:20], [@109,333:335='nnn',<381>,6:114], [@121,366:368='qqq',<381>,7:24], [@145,440:442='qqq',<381>,8:47]], c2=[[@84,247:249='nnn',<381>,6:28], [@127,380:382='qqq',<381>,7:38]]}}, dependent_queries={exists5={query=query4, type=filters}}, filters=[], interface={c3=[{name=c3, table_ref=nnn}], c1=[{name=c1, table_ref=nnn}], c2=[{name=c2, table_ref=nnn}]}, def_query4={context_list={aaa=values0, bbb=query3, vvv=values0}, query_dictionary={unnamed_1=[[@99,300:300='1',<300>,6:81]]}, table_dictionary={}, filters=[{name=a1, table_ref=vvv}, {name=c1, table_ref=nnn}], interface={unnamed_1=[]}, table_alias={aaa=values0, bbb=query3, vvv=values0}}, table_alias={aaa=values0, bbb=query3, nnn=tab3}}, table_dictionary={}, def_values0={query_dictionary={a1=[[@25,58:59='a1',<381>,2:52], [@105,324:326='vvv',<381>,6:105], [@170,522:524='ttt',<381>,12:20]], a2=[[@27,62:63='a2',<381>,2:56], [@158,474:476='ttt',<381>,10:24]], a3=[[@29,66:67='a3',<381>,2:60], [@66,200:202='mmm',<381>,5:105]]}, table_dictionary={}, interface={a1=[], a2=[], a3=[]}}, filters=[{name=b1, table_ref=ppp}, {name=c1, table_ref=qqq}], interface={c1=[{name=c1, table_ref=qqq}], b1=[{name=b1, table_ref=ppp}], c2=[{name=c2, table_ref=qqq}]}, def_query3={context_list={aaa=values0}, query_dictionary={b2=[[@47,127:128='b2',<381>,5:32]], b3=[[@51,135:136='b3',<381>,5:40]], b1=[[@43,119:120='b1',<381>,5:24], [@115,352:354='ppp',<381>,7:10], [@141,431:433='ppp',<381>,8:38]]}, table_dictionary={tab2={b2=[[@45,123:125='lll',<381>,5:28]], b3=[[@49,131:133='lll',<381>,5:36], [@70,209:211='lll',<381>,5:114]], b1=[[@41,115:117='lll',<381>,5:20], [@115,352:354='ppp',<381>,7:10], [@141,431:433='ppp',<381>,8:38]]}}, def_query1={context_list={aaa=values0, mmm=values0}, query_dictionary={unnamed_0=[[@60,176:176='1',<300>,5:81]]}, table_dictionary={}, filters=[{name=a3, table_ref=mmm}, {name=b3, table_ref=lll}], interface={unnamed_0=[]}, table_alias={aaa=values0, mmm=values0}}, dependent_queries={exists2={query=query1, type=filters}}, filters=[], interface={b2=[{name=b2, table_ref=lll}], b3=[{name=b3, table_ref=lll}], b1=[{name=b1, table_ref=lll}]}, table_alias={aaa=values0, lll=tab2}}, query_dictionary={c1=[[@125,376:377='c1',<381>,7:34]], b1=[[@119,362:363='b1',<381>,7:20], [@150,458:460='sss',<381>,10:8], [@174,531:533='sss',<381>,12:29]], c2=[[@131,390:391='c2',<381>,7:48], [@154,466:468='sss',<381>,10:16]]}, table_alias={aaa=values0, ccc=query6, bbb=query3}}, query_dictionary={a2=[[@160,478:479='a2',<381>,10:28]], b1=[[@152,462:463='b1',<381>,10:12]], c2=[[@156,470:471='c2',<381>,10:20]]}, table_dictionary={}, def_values0={query_dictionary={a1=[[@25,58:59='a1',<381>,2:52], [@105,324:326='vvv',<381>,6:105], [@170,522:524='ttt',<381>,12:20]], a2=[[@27,62:63='a2',<381>,2:56], [@158,474:476='ttt',<381>,10:24]], a3=[[@29,66:67='a3',<381>,2:60], [@66,200:202='mmm',<381>,5:105]]}, table_dictionary={}, interface={a1=[], a2=[], a3=[]}}, filters=[{name=a1, table_ref=ttt}, {name=b1, table_ref=sss}], interface={a2=[{name=a2, table_ref=ttt}], b1=[{name=b1, table_ref=sss}], c2=[{name=c2, table_ref=sss}]}, table_alias={aaa=values0, bbb_ccc=query7}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void nestedWithExistsAliasHeavyValuesCteVirtualColumnsExemplarParsesWithoutErrorsV2() {
		final String sql = "WITH "
				+ "\n aaa AS ((VALUES (11, 21, 31), (12, 22, 32)) AS hhh(a1, a2, a3)),"
				+ "\n bbb_ccc AS ("
				+ "\n   WITH "
				+ "\n     bbb AS (SELECT lll.b1, lll.b2, lll.b3 FROM tab2 AS lll WHERE EXISTS (SELECT 1 FROM aaa AS mmm WHERE mmm.a3 = lll.b3)),"
				+ "\n     ccc AS (SELECT nnn.c1, nnn.c2, nnn.c3 FROM tab3 AS nnn WHERE EXISTS (SELECT 1 FROM aaa AS vvv WHERE vvv.a1 = nnn.c1))"
				+ "\n   SELECT ppp.b1 AS b1, qqq.c1 AS c1, qqq.c2 AS c2"
				+ "\n   FROM bbb AS ppp JOIN ccc AS qqq ON ppp.b1 = qqq.c1"
				+ "\n )"
				+ "\n SELECT sss.b1, sss.c2, ttt.a2, ttt.missing"
				+ "\n FROM bbb_ccc AS sss"
				+ "\n JOIN aaa AS ttt ON ttt.a1 = sss.b1";

		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runParsertest(sql, parser);
		Snippet snippet = extractor.getSnippet();
		assertFatalDiagnosticAtPosition(snippet,
				"QUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIAS",
				"Qualified column 'missing' at (l:10 c:32) was not found in output interface of query alias 'ttt'.",
				"ttt.missing",
				10,
				32);

				Assert.assertEquals("AST is wrong", "{SQL={with={1={cte={values={columns={1={column={name=a1, table_ref=null}}, 2={column={name=a2, table_ref=null}}, 3={column={name=a3, table_ref=null}}}, alias=hhh, matrix={1={row={1={literal=11}, 2={literal=21}, 3={literal=31}}}, 2={row={1={literal=12}, 2={literal=22}, 3={literal=32}}}}}}, alias=aaa}, 2={cte={with={1={cte={select={1={column={name=b1, table_ref=lll}}, 2={column={name=b2, table_ref=lll}}, 3={column={name=b3, table_ref=lll}}}, from={table={alias=lll, table=tab2}}, where={exists={select={1={literal=1}}, from={table={alias=mmm, table=aaa}}, where={condition={left={column={name=a3, table_ref=mmm}}, right={column={name=b3, table_ref=lll}}, operator==}}, operator=EXISTS}}}, alias=bbb}, 2={cte={select={1={column={name=c1, table_ref=nnn}}, 2={column={name=c2, table_ref=nnn}}, 3={column={name=c3, table_ref=nnn}}}, from={table={alias=nnn, table=tab3}}, where={exists={select={1={literal=1}}, from={table={alias=vvv, table=aaa}}, where={condition={left={column={name=a1, table_ref=vvv}}, right={column={name=c1, table_ref=nnn}}, operator==}}, operator=EXISTS}}}, alias=ccc}}, query={select={1={column={name=b1, table_ref=ppp}, alias=b1}, 2={column={name=c1, table_ref=qqq}, alias=c1}, 3={column={name=c2, table_ref=qqq}, alias=c2}}, from={join={1={table={alias=ppp, table=bbb}}, 2={join=JOIN, on={condition={left={column={name=b1, table_ref=ppp}}, right={column={name=c1, table_ref=qqq}}, operator==}}}, 3={table={alias=qqq, table=ccc}}}}}}, alias=bbb_ccc}}, query={select={1={column={name=b1, table_ref=sss}}, 2={column={name=c2, table_ref=sss}}, 3={column={name=a2, table_ref=ttt}}, 4={column={name=missing, table_ref=ttt}}}, from={join={1={table={alias=sss, table=bbb_ccc}}, 2={join=JOIN, on={condition={left={column={name=a1, table_ref=ttt}}, right={column={name=b1, table_ref=sss}}, operator==}}}, 3={table={alias=ttt, table=aaa}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[a2, missing, b1, c2]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab3={c3=[[@88,255:257='nnn',<381>,6:36]], c1=[[@80,239:241='nnn',<381>,6:20], [@109,333:335='nnn',<381>,6:114], [@121,366:368='qqq',<381>,7:24], [@145,440:442='qqq',<381>,8:47]], c2=[[@84,247:249='nnn',<381>,6:28], [@127,380:382='qqq',<381>,7:38]]}, tab2={b2=[[@45,123:125='lll',<381>,5:28]], b3=[[@49,131:133='lll',<381>,5:36], [@70,209:211='lll',<381>,5:114]], b1=[[@41,115:117='lll',<381>,5:20], [@115,352:354='ppp',<381>,7:10], [@141,431:433='ppp',<381>,8:38]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{values0={a1=[[@25,58:59='a1',<381>,2:52], [@105,324:326='vvv',<381>,6:105], [@174,535:537='ttt',<381>,12:20]], a2=[[@27,62:63='a2',<381>,2:56], [@158,474:476='ttt',<381>,10:24]], a3=[[@29,66:67='a3',<381>,2:60], [@66,200:202='mmm',<381>,5:105]]}, query8={a2=[[@160,478:479='a2',<381>,10:28]], missing=[[@164,486:492='missing',<381>,10:36]], b1=[[@152,462:463='b1',<381>,10:12]], c2=[[@156,470:471='c2',<381>,10:20]]}, query4={unnamed_1=[[@99,300:300='1',<300>,6:81]]}, query6={c3=[[@90,259:260='c3',<381>,6:40]], c1=[[@82,243:244='c1',<381>,6:24], [@121,366:368='qqq',<381>,7:24], [@145,440:442='qqq',<381>,8:47]], c2=[[@86,251:252='c2',<381>,6:32], [@127,380:382='qqq',<381>,7:38]]}, query7={c1=[[@125,376:377='c1',<381>,7:34]], b1=[[@119,362:363='b1',<381>,7:20], [@150,458:460='sss',<381>,10:8], [@178,544:546='sss',<381>,12:29]], c2=[[@131,390:391='c2',<381>,7:48], [@154,466:468='sss',<381>,10:16]]}, query1={unnamed_0=[[@60,176:176='1',<300>,5:81]]}, query3={b2=[[@47,127:128='b2',<381>,5:32]], b3=[[@51,135:136='b3',<381>,5:40]], b1=[[@43,119:120='b1',<381>,5:24], [@115,352:354='ppp',<381>,7:10], [@141,431:433='ppp',<381>,8:38]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query8={context_list={aaa=values0, bbb_ccc=query7, sss=query7, ttt=values0}, def_query7={context_list={aaa=values0, bbb=query3, ccc=query6, ppp=query3, qqq=query6}, def_query6={context_list={aaa=values0, bbb=query3}, query_dictionary={c3=[[@90,259:260='c3',<381>,6:40]], c1=[[@82,243:244='c1',<381>,6:24], [@121,366:368='qqq',<381>,7:24], [@145,440:442='qqq',<381>,8:47]], c2=[[@86,251:252='c2',<381>,6:32], [@127,380:382='qqq',<381>,7:38]]}, table_dictionary={tab3={c3=[[@88,255:257='nnn',<381>,6:36]], c1=[[@80,239:241='nnn',<381>,6:20], [@109,333:335='nnn',<381>,6:114], [@121,366:368='qqq',<381>,7:24], [@145,440:442='qqq',<381>,8:47]], c2=[[@84,247:249='nnn',<381>,6:28], [@127,380:382='qqq',<381>,7:38]]}}, dependent_queries={exists5={query=query4, type=filters}}, filters=[], interface={c3=[{name=c3, table_ref=nnn}], c1=[{name=c1, table_ref=nnn}], c2=[{name=c2, table_ref=nnn}]}, def_query4={context_list={aaa=values0, bbb=query3, vvv=values0}, query_dictionary={unnamed_1=[[@99,300:300='1',<300>,6:81]]}, table_dictionary={}, filters=[{name=a1, table_ref=vvv}, {name=c1, table_ref=nnn}], interface={unnamed_1=[]}, table_alias={aaa=values0, bbb=query3, vvv=values0}}, table_alias={aaa=values0, bbb=query3, nnn=tab3}}, table_dictionary={}, def_values0={query_dictionary={a1=[[@25,58:59='a1',<381>,2:52], [@105,324:326='vvv',<381>,6:105], [@174,535:537='ttt',<381>,12:20]], a2=[[@27,62:63='a2',<381>,2:56], [@158,474:476='ttt',<381>,10:24]], a3=[[@29,66:67='a3',<381>,2:60], [@66,200:202='mmm',<381>,5:105]]}, table_dictionary={}, interface={a1=[], a2=[], a3=[]}}, filters=[{name=b1, table_ref=ppp}, {name=c1, table_ref=qqq}], interface={c1=[{name=c1, table_ref=qqq}], b1=[{name=b1, table_ref=ppp}], c2=[{name=c2, table_ref=qqq}]}, def_query3={context_list={aaa=values0}, query_dictionary={b2=[[@47,127:128='b2',<381>,5:32]], b3=[[@51,135:136='b3',<381>,5:40]], b1=[[@43,119:120='b1',<381>,5:24], [@115,352:354='ppp',<381>,7:10], [@141,431:433='ppp',<381>,8:38]]}, table_dictionary={tab2={b2=[[@45,123:125='lll',<381>,5:28]], b3=[[@49,131:133='lll',<381>,5:36], [@70,209:211='lll',<381>,5:114]], b1=[[@41,115:117='lll',<381>,5:20], [@115,352:354='ppp',<381>,7:10], [@141,431:433='ppp',<381>,8:38]]}}, def_query1={context_list={aaa=values0, mmm=values0}, query_dictionary={unnamed_0=[[@60,176:176='1',<300>,5:81]]}, table_dictionary={}, filters=[{name=a3, table_ref=mmm}, {name=b3, table_ref=lll}], interface={unnamed_0=[]}, table_alias={aaa=values0, mmm=values0}}, dependent_queries={exists2={query=query1, type=filters}}, filters=[], interface={b2=[{name=b2, table_ref=lll}], b3=[{name=b3, table_ref=lll}], b1=[{name=b1, table_ref=lll}]}, table_alias={aaa=values0, lll=tab2}}, query_dictionary={c1=[[@125,376:377='c1',<381>,7:34]], b1=[[@119,362:363='b1',<381>,7:20], [@150,458:460='sss',<381>,10:8], [@178,544:546='sss',<381>,12:29]], c2=[[@131,390:391='c2',<381>,7:48], [@154,466:468='sss',<381>,10:16]]}, table_alias={aaa=values0, ccc=query6, bbb=query3}}, query_dictionary={a2=[[@160,478:479='a2',<381>,10:28]], missing=[[@164,486:492='missing',<381>,10:36]], b1=[[@152,462:463='b1',<381>,10:12]], c2=[[@156,470:471='c2',<381>,10:20]]}, table_dictionary={}, def_values0={query_dictionary={a1=[[@25,58:59='a1',<381>,2:52], [@105,324:326='vvv',<381>,6:105], [@174,535:537='ttt',<381>,12:20]], a2=[[@27,62:63='a2',<381>,2:56], [@158,474:476='ttt',<381>,10:24]], a3=[[@29,66:67='a3',<381>,2:60], [@66,200:202='mmm',<381>,5:105]]}, table_dictionary={}, interface={a1=[], a2=[], a3=[]}}, filters=[{name=a1, table_ref=ttt}, {name=b1, table_ref=sss}], interface={a2=[{name=a2, table_ref=ttt}], missing=[{name=missing, table_ref=ttt}], b1=[{name=b1, table_ref=sss}], c2=[{name=c2, table_ref=sss}]}, table_alias={aaa=values0, bbb_ccc=query7}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void nestedWithUnionAliasHeavyValuesCteVirtualColumnsExemplarParsesWithoutErrors() {
		final String sql = "WITH "
				+ "\n aaa AS (SELECT kkk.a1, kkk.a2, kkk.a3 FROM tab1 AS kkk),"
				+ "\n bbb AS ((VALUES (101, 201, 301), (102, 202, 302)) AS hhh(b1, b2, b3)),"
				+ "\n ccc AS (SELECT nnn.c1, nnn.c2, nnn.c3 FROM tab3 AS nnn UNION ALL SELECT ooo.b1 AS c1, ooo.b2 AS c2, ooo.b3 AS c3 FROM bbb AS ooo)"
				+ "\n SELECT ppp.a1, qqq.b2, rrr.c2"
				+ "\n FROM aaa AS ppp"
				+ "\n JOIN bbb AS qqq ON ppp.a1 = qqq.b1"
				+ "\n JOIN ccc AS rrr ON qqq.b1 = rrr.c1";

		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runParsertest(sql, parser);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong", "{SQL={with={1={cte={select={1={column={name=a1, table_ref=kkk}}, 2={column={name=a2, table_ref=kkk}}, 3={column={name=a3, table_ref=kkk}}}, from={table={alias=kkk, table=tab1}}}, alias=aaa}, 2={cte={values={columns={1={column={name=b1, table_ref=null}}, 2={column={name=b2, table_ref=null}}, 3={column={name=b3, table_ref=null}}}, alias=hhh, matrix={1={row={1={literal=101}, 2={literal=201}, 3={literal=301}}}, 2={row={1={literal=102}, 2={literal=202}, 3={literal=302}}}}}}, alias=bbb}, 3={cte={union={1={select={1={column={name=c1, table_ref=nnn}}, 2={column={name=c2, table_ref=nnn}}, 3={column={name=c3, table_ref=nnn}}}, from={table={alias=nnn, table=tab3}}}, 2={union={qualifier=ALL, operator=UNION}}, 3={select={1={column={name=b1, table_ref=ooo}, alias=c1}, 2={column={name=b2, table_ref=ooo}, alias=c2}, 3={column={name=b3, table_ref=ooo}, alias=c3}}, from={table={alias=ooo, table=bbb}}}}}, alias=ccc}}, query={select={1={column={name=a1, table_ref=ppp}}, 2={column={name=b2, table_ref=qqq}}, 3={column={name=c2, table_ref=rrr}}}, from={join={1={table={alias=ppp, table=aaa}}, 2={join=JOIN, on={condition={left={column={name=a1, table_ref=ppp}}, right={column={name=b1, table_ref=qqq}}, operator==}}}, 3={table={alias=qqq, table=bbb}}, 4={join=JOIN, on={condition={left={column={name=b1, table_ref=qqq}}, right={column={name=c1, table_ref=rrr}}, operator==}}}, 5={table={alias=rrr, table=ccc}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[a1, b2, c2]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab3={c3=[[@66,168:170='nnn',<381>,4:32]], c1=[[@58,152:154='nnn',<381>,4:16]], c2=[[@62,160:162='nnn',<381>,4:24]]}, tab1={a1=[[@5,22:24='kkk',<381>,2:16], [@99,275:277='ppp',<381>,5:8], [@119,335:337='ppp',<381>,7:20]], a2=[[@9,30:32='kkk',<381>,2:24]], a3=[[@13,38:40='kkk',<381>,2:32]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{values1={b2=[[@48,126:127='b2',<381>,3:62], [@82,223:225='ooo',<381>,4:87], [@103,283:285='qqq',<381>,5:16]], b3=[[@50,130:131='b3',<381>,3:66], [@88,237:239='ooo',<381>,4:101]], b1=[[@46,122:123='b1',<381>,3:58], [@76,209:211='ooo',<381>,4:73], [@123,344:346='qqq',<381>,7:29], [@131,371:373='qqq',<381>,8:20]]}, query5={a1=[[@101,279:280='a1',<381>,5:12]], b2=[[@105,287:288='b2',<381>,5:20]], c2=[[@109,295:296='c2',<381>,5:28]]}, query0={a1=[[@7,26:27='a1',<381>,2:20], [@99,275:277='ppp',<381>,5:8], [@119,335:337='ppp',<381>,7:20]], a2=[[@11,34:35='a2',<381>,2:28]], a3=[[@15,42:43='a3',<381>,2:36]]}, query2={c3=[[@68,172:173='c3',<381>,4:36]], c1=[[@60,156:157='c1',<381>,4:20]], c2=[[@64,164:165='c2',<381>,4:28]]}, query3={c3=[[@92,247:248='c3',<381>,4:111]], c1=[[@80,219:220='c1',<381>,4:83]], c2=[[@86,233:234='c2',<381>,4:97]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query5={context_list={aaa=query0, bbb=values1, ccc=union4, ppp=query0, qqq=values1, rrr=union4}, def_union4={context_list={aaa=query0, bbb=values1}, interface={c3=[{name=c3, table_ref=nnn}, {name=b3, table_ref=ooo}], c1=[{name=c1, table_ref=nnn}, {name=b1, table_ref=ooo}], c2=[{name=c2, table_ref=nnn}, {name=b2, table_ref=ooo}]}, table_alias={aaa=query0, bbb=values1}, query2={context_list={aaa=query0, bbb=values1}, query_dictionary={c3=[[@68,172:173='c3',<381>,4:36]], c1=[[@60,156:157='c1',<381>,4:20]], c2=[[@64,164:165='c2',<381>,4:28]]}, table_dictionary={tab3={c3=[[@66,168:170='nnn',<381>,4:32]], c1=[[@58,152:154='nnn',<381>,4:16]], c2=[[@62,160:162='nnn',<381>,4:24]]}}, interface={c3=[{name=c3, table_ref=nnn}], c1=[{name=c1, table_ref=nnn}], c2=[{name=c2, table_ref=nnn}]}, table_alias={aaa=query0, bbb=values1, nnn=tab3}}, query3={context_list={aaa=query0, bbb=values1, ooo=values1}, query_dictionary={c3=[[@92,247:248='c3',<381>,4:111]], c1=[[@80,219:220='c1',<381>,4:83]], c2=[[@86,233:234='c2',<381>,4:97]]}, table_dictionary={}, interface={c3=[{name=b3, table_ref=ooo}], c1=[{name=b1, table_ref=ooo}], c2=[{name=b2, table_ref=ooo}]}, table_alias={aaa=query0, bbb=values1, ooo=values1}}}, query_dictionary={a1=[[@101,279:280='a1',<381>,5:12]], b2=[[@105,287:288='b2',<381>,5:20]], c2=[[@109,295:296='c2',<381>,5:28]]}, table_dictionary={}, def_values1={query_dictionary={b2=[[@48,126:127='b2',<381>,3:62], [@82,223:225='ooo',<381>,4:87], [@103,283:285='qqq',<381>,5:16]], b3=[[@50,130:131='b3',<381>,3:66], [@88,237:239='ooo',<381>,4:101]], b1=[[@46,122:123='b1',<381>,3:58], [@76,209:211='ooo',<381>,4:73], [@123,344:346='qqq',<381>,7:29], [@131,371:373='qqq',<381>,8:20]]}, table_dictionary={}, interface={b2=[], b3=[], b1=[]}}, def_query0={query_dictionary={a1=[[@7,26:27='a1',<381>,2:20], [@99,275:277='ppp',<381>,5:8], [@119,335:337='ppp',<381>,7:20]], a2=[[@11,34:35='a2',<381>,2:28]], a3=[[@15,42:43='a3',<381>,2:36]]}, table_dictionary={tab1={a1=[[@5,22:24='kkk',<381>,2:16], [@99,275:277='ppp',<381>,5:8], [@119,335:337='ppp',<381>,7:20]], a2=[[@9,30:32='kkk',<381>,2:24]], a3=[[@13,38:40='kkk',<381>,2:32]]}}, interface={a1=[{name=a1, table_ref=kkk}], a2=[{name=a2, table_ref=kkk}], a3=[{name=a3, table_ref=kkk}]}, table_alias={kkk=tab1}}, filters=[{name=a1, table_ref=ppp}, {name=b1, table_ref=qqq}, {name=c1, table_ref=rrr}], interface={a1=[{name=a1, table_ref=ppp}], b2=[{name=b2, table_ref=qqq}], c2=[{name=c2, table_ref=rrr}]}, table_alias={aaa=query0, ccc=union4, bbb=values1}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void nestedWithIntersectAliasHeavyValuesCteVirtualColumnsExemplarParsesWithoutErrors() {
		final String sql = "WITH "
				+ "\n aaa AS (SELECT kkk.a1, kkk.a2, kkk.a3 FROM tab1 AS kkk),"
				+ "\n bbb AS (SELECT lll.b1, lll.b2, lll.b3 FROM tab2 AS lll INTERSECT DISTINCT SELECT mmm.a1 AS b1, mmm.a2 AS b2, mmm.a3 AS b3 FROM aaa AS mmm),"
				+ "\n ccc AS ((VALUES (1001, 2001, 3001), (1002, 2002, 3002)) AS hhh(c1, c2, c3))"
				+ "\n SELECT ppp.a1, qqq.b2, rrr.c2"
				+ "\n FROM aaa AS ppp"
				+ "\n JOIN bbb AS qqq ON ppp.a1 = qqq.b1"
				+ "\n JOIN ccc AS rrr ON qqq.b1 = rrr.c1";

		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runParsertest(sql, parser);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong", "{SQL={with={1={cte={select={1={column={name=a1, table_ref=kkk}}, 2={column={name=a2, table_ref=kkk}}, 3={column={name=a3, table_ref=kkk}}}, from={table={alias=kkk, table=tab1}}}, alias=aaa}, 2={cte={intersect={1={select={1={column={name=b1, table_ref=lll}}, 2={column={name=b2, table_ref=lll}}, 3={column={name=b3, table_ref=lll}}}, from={table={alias=lll, table=tab2}}}, 2={intersect={qualifier=DISTINCT, operator=INTERSECT}}, 3={select={1={column={name=a1, table_ref=mmm}, alias=b1}, 2={column={name=a2, table_ref=mmm}, alias=b2}, 3={column={name=a3, table_ref=mmm}, alias=b3}}, from={table={alias=mmm, table=aaa}}}}}, alias=bbb}, 3={cte={values={columns={1={column={name=c1, table_ref=null}}, 2={column={name=c2, table_ref=null}}, 3={column={name=c3, table_ref=null}}}, alias=hhh, matrix={1={row={1={literal=1001}, 2={literal=2001}, 3={literal=3001}}}, 2={row={1={literal=1002}, 2={literal=2002}, 3={literal=3002}}}}}}, alias=ccc}}, query={select={1={column={name=a1, table_ref=ppp}}, 2={column={name=b2, table_ref=qqq}}, 3={column={name=c2, table_ref=rrr}}}, from={join={1={table={alias=ppp, table=aaa}}, 2={join=JOIN, on={condition={left={column={name=a1, table_ref=ppp}}, right={column={name=b1, table_ref=qqq}}, operator==}}}, 3={table={alias=qqq, table=bbb}}, 4={join=JOIN, on={condition={left={column={name=b1, table_ref=qqq}}, right={column={name=c1, table_ref=rrr}}, operator==}}}, 5={table={alias=rrr, table=ccc}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[a1, b2, c2]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={a1=[[@5,22:24='kkk',<381>,2:16], [@44,146:148='mmm',<381>,3:82], [@99,290:292='ppp',<381>,5:8], [@119,350:352='ppp',<381>,7:20]], a2=[[@9,30:32='kkk',<381>,2:24], [@50,160:162='mmm',<381>,3:96]], a3=[[@13,38:40='kkk',<381>,2:32], [@56,174:176='mmm',<381>,3:110]]}, tab2={b2=[[@30,88:90='lll',<381>,3:24]], b3=[[@34,96:98='lll',<381>,3:32]], b1=[[@26,80:82='lll',<381>,3:16]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query5={a1=[[@101,294:295='a1',<381>,5:12]], b2=[[@105,302:303='b2',<381>,5:20]], c2=[[@109,310:311='c2',<381>,5:28]]}, query0={a1=[[@7,26:27='a1',<381>,2:20], [@44,146:148='mmm',<381>,3:82], [@99,290:292='ppp',<381>,5:8], [@119,350:352='ppp',<381>,7:20]], a2=[[@11,34:35='a2',<381>,2:28], [@50,160:162='mmm',<381>,3:96]], a3=[[@15,42:43='a3',<381>,2:36], [@56,174:176='mmm',<381>,3:110]]}, values4={c3=[[@95,277:278='c3',<381>,4:72]], c1=[[@91,269:270='c1',<381>,4:64], [@135,395:397='rrr',<381>,8:29]], c2=[[@93,273:274='c2',<381>,4:68], [@107,306:308='rrr',<381>,5:24]]}, query1={b2=[[@32,92:93='b2',<381>,3:28]], b3=[[@36,100:101='b3',<381>,3:36]], b1=[[@28,84:85='b1',<381>,3:20]]}, query2={b2=[[@54,170:171='b2',<381>,3:106]], b3=[[@60,184:185='b3',<381>,3:120]], b1=[[@48,156:157='b1',<381>,3:92]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query5={context_list={aaa=query0, bbb=intersect3, ccc=values4, ppp=query0, qqq=intersect3, rrr=values4}, query_dictionary={a1=[[@101,294:295='a1',<381>,5:12]], b2=[[@105,302:303='b2',<381>,5:20]], c2=[[@109,310:311='c2',<381>,5:28]]}, table_dictionary={}, def_intersect3={context_list={aaa=query0}, interface={b2=[{name=b2, table_ref=lll}, {name=a2, table_ref=mmm}], b3=[{name=b3, table_ref=lll}, {name=a3, table_ref=mmm}], b1=[{name=b1, table_ref=lll}, {name=a1, table_ref=mmm}]}, query1={context_list={aaa=query0}, query_dictionary={b2=[[@32,92:93='b2',<381>,3:28]], b3=[[@36,100:101='b3',<381>,3:36]], b1=[[@28,84:85='b1',<381>,3:20]]}, table_dictionary={tab2={b2=[[@30,88:90='lll',<381>,3:24]], b3=[[@34,96:98='lll',<381>,3:32]], b1=[[@26,80:82='lll',<381>,3:16]]}}, interface={b2=[{name=b2, table_ref=lll}], b3=[{name=b3, table_ref=lll}], b1=[{name=b1, table_ref=lll}]}, table_alias={aaa=query0, lll=tab2}}, table_alias={aaa=query0}, query2={context_list={aaa=query0, mmm=query0}, query_dictionary={b2=[[@54,170:171='b2',<381>,3:106]], b3=[[@60,184:185='b3',<381>,3:120]], b1=[[@48,156:157='b1',<381>,3:92]]}, table_dictionary={}, interface={b2=[{name=a2, table_ref=mmm}], b3=[{name=a3, table_ref=mmm}], b1=[{name=a1, table_ref=mmm}]}, table_alias={aaa=query0, mmm=query0}}}, def_query0={query_dictionary={a1=[[@7,26:27='a1',<381>,2:20], [@44,146:148='mmm',<381>,3:82], [@99,290:292='ppp',<381>,5:8], [@119,350:352='ppp',<381>,7:20]], a2=[[@11,34:35='a2',<381>,2:28], [@50,160:162='mmm',<381>,3:96]], a3=[[@15,42:43='a3',<381>,2:36], [@56,174:176='mmm',<381>,3:110]]}, table_dictionary={tab1={a1=[[@5,22:24='kkk',<381>,2:16], [@44,146:148='mmm',<381>,3:82], [@99,290:292='ppp',<381>,5:8], [@119,350:352='ppp',<381>,7:20]], a2=[[@9,30:32='kkk',<381>,2:24], [@50,160:162='mmm',<381>,3:96]], a3=[[@13,38:40='kkk',<381>,2:32], [@56,174:176='mmm',<381>,3:110]]}}, interface={a1=[{name=a1, table_ref=kkk}], a2=[{name=a2, table_ref=kkk}], a3=[{name=a3, table_ref=kkk}]}, table_alias={kkk=tab1}}, def_values4={query_dictionary={c3=[[@95,277:278='c3',<381>,4:72]], c1=[[@91,269:270='c1',<381>,4:64], [@135,395:397='rrr',<381>,8:29]], c2=[[@93,273:274='c2',<381>,4:68], [@107,306:308='rrr',<381>,5:24]]}, table_dictionary={}, interface={c3=[], c1=[], c2=[]}}, filters=[{name=a1, table_ref=ppp}, {name=b1, table_ref=qqq}, {name=c1, table_ref=rrr}], interface={a1=[{name=a1, table_ref=ppp}], b2=[{name=b2, table_ref=qqq}], c2=[{name=c2, table_ref=rrr}]}, table_alias={aaa=query0, ccc=values4, bbb=intersect3}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void nestedWithJoinAliasHeavyValuesCteVirtualColumnsExemplarParsesWithoutErrors() {
		final String sql = "WITH "
				+ "\n aaa AS (SELECT kkk.a1, kkk.a2, kkk.a3 FROM tab1 AS kkk),"
				+ "\n bbb AS ((VALUES (401, 501, 601), (402, 502, 602)) AS hhh(b1, b2, b3)),"
				+ "\n ccc AS (SELECT nnn.c1, nnn.c2, nnn.c3 FROM tab3 AS nnn JOIN bbb AS ooo ON ooo.b3 = nnn.c3)"
				+ "\n SELECT ppp.a1, qqq.b2, rrr.c2"
				+ "\n FROM aaa AS ppp"
				+ "\n JOIN bbb AS qqq ON ppp.a1 = qqq.b1"
				+ "\n JOIN ccc AS rrr ON qqq.b1 = rrr.c1";

		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runParsertest(sql, parser);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong", "{SQL={with={1={cte={select={1={column={name=a1, table_ref=kkk}}, 2={column={name=a2, table_ref=kkk}}, 3={column={name=a3, table_ref=kkk}}}, from={table={alias=kkk, table=tab1}}}, alias=aaa}, 2={cte={values={columns={1={column={name=b1, table_ref=null}}, 2={column={name=b2, table_ref=null}}, 3={column={name=b3, table_ref=null}}}, alias=hhh, matrix={1={row={1={literal=401}, 2={literal=501}, 3={literal=601}}}, 2={row={1={literal=402}, 2={literal=502}, 3={literal=602}}}}}}, alias=bbb}, 3={cte={select={1={column={name=c1, table_ref=nnn}}, 2={column={name=c2, table_ref=nnn}}, 3={column={name=c3, table_ref=nnn}}}, from={join={1={table={alias=nnn, table=tab3}}, 2={join=JOIN, on={condition={left={column={name=b3, table_ref=ooo}}, right={column={name=c3, table_ref=nnn}}, operator==}}}, 3={table={alias=ooo, table=bbb}}}}}, alias=ccc}}, query={select={1={column={name=a1, table_ref=ppp}}, 2={column={name=b2, table_ref=qqq}}, 3={column={name=c2, table_ref=rrr}}}, from={join={1={table={alias=ppp, table=aaa}}, 2={join=JOIN, on={condition={left={column={name=a1, table_ref=ppp}}, right={column={name=b1, table_ref=qqq}}, operator==}}}, 3={table={alias=qqq, table=bbb}}, 4={join=JOIN, on={condition={left={column={name=b1, table_ref=qqq}}, right={column={name=c1, table_ref=rrr}}, operator==}}}, 5={table={alias=rrr, table=ccc}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[a1, b2, c2]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab3={c3=[[@66,168:170='nnn',<381>,4:32], [@82,220:222='nnn',<381>,4:84]], c1=[[@58,152:154='nnn',<381>,4:16], [@123,341:343='rrr',<381>,8:29]], c2=[[@62,160:162='nnn',<381>,4:24], [@95,252:254='rrr',<381>,5:24]]}, tab1={a1=[[@5,22:24='kkk',<381>,2:16], [@87,236:238='ppp',<381>,5:8], [@107,296:298='ppp',<381>,7:20]], a2=[[@9,30:32='kkk',<381>,2:24]], a3=[[@13,38:40='kkk',<381>,2:32]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{values1={b2=[[@48,126:127='b2',<381>,3:62], [@91,244:246='qqq',<381>,5:16]], b3=[[@50,130:131='b3',<381>,3:66], [@78,211:213='ooo',<381>,4:75]], b1=[[@46,122:123='b1',<381>,3:58], [@111,305:307='qqq',<381>,7:29], [@119,332:334='qqq',<381>,8:20]]}, query0={a1=[[@7,26:27='a1',<381>,2:20], [@87,236:238='ppp',<381>,5:8], [@107,296:298='ppp',<381>,7:20]], a2=[[@11,34:35='a2',<381>,2:28]], a3=[[@15,42:43='a3',<381>,2:36]]}, query2={c3=[[@68,172:173='c3',<381>,4:36]], c1=[[@60,156:157='c1',<381>,4:20], [@123,341:343='rrr',<381>,8:29]], c2=[[@64,164:165='c2',<381>,4:28], [@95,252:254='rrr',<381>,5:24]]}, query3={a1=[[@89,240:241='a1',<381>,5:12]], b2=[[@93,248:249='b2',<381>,5:20]], c2=[[@97,256:257='c2',<381>,5:28]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query3={context_list={aaa=query0, bbb=values1, ccc=query2, ppp=query0, qqq=values1, rrr=query2}, query_dictionary={a1=[[@89,240:241='a1',<381>,5:12]], b2=[[@93,248:249='b2',<381>,5:20]], c2=[[@97,256:257='c2',<381>,5:28]]}, table_dictionary={}, def_values1={query_dictionary={b2=[[@48,126:127='b2',<381>,3:62], [@91,244:246='qqq',<381>,5:16]], b3=[[@50,130:131='b3',<381>,3:66], [@78,211:213='ooo',<381>,4:75]], b1=[[@46,122:123='b1',<381>,3:58], [@111,305:307='qqq',<381>,7:29], [@119,332:334='qqq',<381>,8:20]]}, table_dictionary={}, interface={b2=[], b3=[], b1=[]}}, def_query0={query_dictionary={a1=[[@7,26:27='a1',<381>,2:20], [@87,236:238='ppp',<381>,5:8], [@107,296:298='ppp',<381>,7:20]], a2=[[@11,34:35='a2',<381>,2:28]], a3=[[@15,42:43='a3',<381>,2:36]]}, table_dictionary={tab1={a1=[[@5,22:24='kkk',<381>,2:16], [@87,236:238='ppp',<381>,5:8], [@107,296:298='ppp',<381>,7:20]], a2=[[@9,30:32='kkk',<381>,2:24]], a3=[[@13,38:40='kkk',<381>,2:32]]}}, interface={a1=[{name=a1, table_ref=kkk}], a2=[{name=a2, table_ref=kkk}], a3=[{name=a3, table_ref=kkk}]}, table_alias={kkk=tab1}}, filters=[{name=a1, table_ref=ppp}, {name=b1, table_ref=qqq}, {name=c1, table_ref=rrr}], interface={a1=[{name=a1, table_ref=ppp}], b2=[{name=b2, table_ref=qqq}], c2=[{name=c2, table_ref=rrr}]}, table_alias={aaa=query0, ccc=query2, bbb=values1}, def_query2={context_list={aaa=query0, bbb=values1, ooo=values1}, query_dictionary={c3=[[@68,172:173='c3',<381>,4:36]], c1=[[@60,156:157='c1',<381>,4:20], [@123,341:343='rrr',<381>,8:29]], c2=[[@64,164:165='c2',<381>,4:28], [@95,252:254='rrr',<381>,5:24]]}, table_dictionary={tab3={c3=[[@66,168:170='nnn',<381>,4:32], [@82,220:222='nnn',<381>,4:84]], c1=[[@58,152:154='nnn',<381>,4:16], [@123,341:343='rrr',<381>,8:29]], c2=[[@62,160:162='nnn',<381>,4:24], [@95,252:254='rrr',<381>,5:24]]}}, filters=[{name=b3, table_ref=ooo}, {name=c3, table_ref=nnn}], interface={c3=[{name=c3, table_ref=nnn}], c1=[{name=c1, table_ref=nnn}], c2=[{name=c2, table_ref=nnn}]}, table_alias={aaa=query0, bbb=values1, ooo=values1, nnn=tab3}}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void nestedWithScalarHavingAliasHeavyValuesCteVirtualColumnsExemplarParsesWithoutErrors() {
		final String sql = "WITH "
				+ "\n aaa AS ((VALUES (31, 41, 51), (32, 42, 52)) AS hhh(a1, a2, a3)),"
				+ "\n bbb AS (SELECT lll.b1, lll.b2, max(lll.b3) AS b3 FROM tab2 AS lll GROUP BY lll.b1, lll.b2"
				+ "\n        HAVING max(lll.b3) >= (SELECT max(mmm.a3) FROM aaa AS mmm)),"
				+ "\n ccc AS (SELECT nnn.c1, nnn.c2, max(nnn.c3) AS c3 FROM tab3 AS nnn GROUP BY nnn.c1, nnn.c2"
				+ "\n        HAVING max(nnn.c3) >= (SELECT max(ooo.b3) FROM bbb AS ooo))"
				+ "\n SELECT qqq.b1, qqq.b2, rrr.c2, ttt.a2"
				+ "\n FROM bbb AS qqq"
				+ "\n JOIN ccc AS rrr ON qqq.b1 = rrr.c1"
				+ "\n JOIN aaa AS ttt ON ttt.a1 = qqq.b1";

		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runParsertest(sql, parser);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong", "{SQL={with={1={cte={values={columns={1={column={name=a1, table_ref=null}}, 2={column={name=a2, table_ref=null}}, 3={column={name=a3, table_ref=null}}}, alias=hhh, matrix={1={row={1={literal=31}, 2={literal=41}, 3={literal=51}}}, 2={row={1={literal=32}, 2={literal=42}, 3={literal=52}}}}}}, alias=aaa}, 2={cte={select={1={column={name=b1, table_ref=lll}}, 2={column={name=b2, table_ref=lll}}, 3={function={function_name=max, qualifier=null, parameters={column={name=b3, table_ref=lll}}}, alias=b3}}, having={condition={left={function={function_name=max, qualifier=null, parameters={column={name=b3, table_ref=lll}}}}, right={select={1={function={function_name=max, qualifier=null, parameters={column={name=a3, table_ref=mmm}}}}}, from={table={alias=mmm, table=aaa}}}, operator=>=}}, from={table={alias=lll, table=tab2}}, groupby={1={column={name=b1, table_ref=lll}}, 2={column={name=b2, table_ref=lll}}}}, alias=bbb}, 3={cte={select={1={column={name=c1, table_ref=nnn}}, 2={column={name=c2, table_ref=nnn}}, 3={function={function_name=max, qualifier=null, parameters={column={name=c3, table_ref=nnn}}}, alias=c3}}, having={condition={left={function={function_name=max, qualifier=null, parameters={column={name=c3, table_ref=nnn}}}}, right={select={1={function={function_name=max, qualifier=null, parameters={column={name=b3, table_ref=ooo}}}}}, from={table={alias=ooo, table=bbb}}}, operator=>=}}, from={table={alias=nnn, table=tab3}}, groupby={1={column={name=c1, table_ref=nnn}}, 2={column={name=c2, table_ref=nnn}}}}, alias=ccc}}, query={select={1={column={name=b1, table_ref=qqq}}, 2={column={name=b2, table_ref=qqq}}, 3={column={name=c2, table_ref=rrr}}, 4={column={name=a2, table_ref=ttt}}}, from={join={1={table={alias=qqq, table=bbb}}, 2={join=JOIN, on={condition={left={column={name=b1, table_ref=qqq}}, right={column={name=c1, table_ref=rrr}}, operator==}}}, 3={table={alias=rrr, table=ccc}}, 4={join=JOIN, on={condition={left={column={name=a1, table_ref=ttt}}, right={column={name=b1, table_ref=qqq}}, operator==}}}, 5={table={alias=ttt, table=aaa}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[b2, a2, b1, c2]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab3={c3=[[@103,268:270='nnn',<381>,5:36], [@125,342:344='nnn',<381>,6:19]], c1=[[@93,248:250='nnn',<381>,5:16], [@115,308:310='nnn',<381>,5:76], [@173,476:478='rrr',<381>,9:29]], c2=[[@97,256:258='nnn',<381>,5:24], [@119,316:318='nnn',<381>,5:84], [@153,415:417='rrr',<381>,7:24]]}, tab2={b2=[[@41,96:98='lll',<381>,3:24], [@63,156:158='lll',<381>,3:84], [@149,407:409='qqq',<381>,7:16]], b3=[[@47,108:110='lll',<381>,3:36], [@69,182:184='lll',<381>,4:19], [@134,365:367='ooo',<381>,6:42]], b1=[[@37,88:90='lll',<381>,3:16], [@59,148:150='lll',<381>,3:76], [@145,399:401='qqq',<381>,7:8], [@169,467:469='qqq',<381>,9:20], [@185,512:514='qqq',<381>,10:29]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{values0={a1=[[@25,58:59='a1',<381>,2:52], [@181,503:505='ttt',<381>,10:20]], a2=[[@27,62:63='a2',<381>,2:56], [@157,423:425='ttt',<381>,7:32]], a3=[[@29,66:67='a3',<381>,2:60], [@78,205:207='mmm',<381>,4:42]]}, query4={unnamed_1=[[@137,371:371=')',<288>,6:48]]}, query6={c3=[[@108,279:280='c3',<381>,5:47]], c1=[[@95,252:253='c1',<381>,5:20], [@173,476:478='rrr',<381>,9:29]], c2=[[@99,260:261='c2',<381>,5:28], [@153,415:417='rrr',<381>,7:24]]}, query7={b2=[[@151,411:412='b2',<381>,7:20]], a2=[[@159,427:428='a2',<381>,7:36]], b1=[[@147,403:404='b1',<381>,7:12]], c2=[[@155,419:420='c2',<381>,7:28]]}, query1={unnamed_0=[[@81,211:211=')',<288>,4:48]]}, query3={b2=[[@43,100:101='b2',<381>,3:28], [@149,407:409='qqq',<381>,7:16]], b3=[[@52,119:120='b3',<381>,3:47], [@134,365:367='ooo',<381>,6:42]], b1=[[@39,92:93='b1',<381>,3:20], [@145,399:401='qqq',<381>,7:8], [@169,467:469='qqq',<381>,9:20], [@185,512:514='qqq',<381>,10:29]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query7={context_list={aaa=values0, bbb=query3, ccc=query6, qqq=query3, rrr=query6, ttt=values0}, query_dictionary={b2=[[@151,411:412='b2',<381>,7:20]], a2=[[@159,427:428='a2',<381>,7:36]], b1=[[@147,403:404='b1',<381>,7:12]], c2=[[@155,419:420='c2',<381>,7:28]]}, def_query6={context_list={aaa=values0, bbb=query3}, query_dictionary={c3=[[@108,279:280='c3',<381>,5:47]], c1=[[@95,252:253='c1',<381>,5:20], [@173,476:478='rrr',<381>,9:29]], c2=[[@99,260:261='c2',<381>,5:28], [@153,415:417='rrr',<381>,7:24]]}, table_dictionary={tab3={c3=[[@103,268:270='nnn',<381>,5:36], [@125,342:344='nnn',<381>,6:19]], c1=[[@93,248:250='nnn',<381>,5:16], [@115,308:310='nnn',<381>,5:76], [@173,476:478='rrr',<381>,9:29]], c2=[[@97,256:258='nnn',<381>,5:24], [@119,316:318='nnn',<381>,5:84], [@153,415:417='rrr',<381>,7:24]]}}, grouped_by=[{name=c1, table_ref=nnn}, {name=c2, table_ref=nnn}], dependent_queries={predicand5={query=query4, type=filters}}, filters=[{name=c3, table_ref=nnn}], interface={c3=[{name=c3, table_ref=nnn}], c1=[{name=c1, table_ref=nnn}], c2=[{name=c2, table_ref=nnn}]}, def_query4={context_list={aaa=values0, bbb=query3, ooo=query3}, query_dictionary={unnamed_1=[[@137,371:371=')',<288>,6:48]]}, table_dictionary={}, interface={unnamed_1=[{name=b3, table_ref=ooo}]}, table_alias={aaa=values0, bbb=query3, ooo=query3}}, table_alias={aaa=values0, bbb=query3, nnn=tab3}}, table_dictionary={}, def_values0={query_dictionary={a1=[[@25,58:59='a1',<381>,2:52], [@181,503:505='ttt',<381>,10:20]], a2=[[@27,62:63='a2',<381>,2:56], [@157,423:425='ttt',<381>,7:32]], a3=[[@29,66:67='a3',<381>,2:60], [@78,205:207='mmm',<381>,4:42]]}, table_dictionary={}, interface={a1=[], a2=[], a3=[]}}, filters=[{name=b1, table_ref=qqq}, {name=c1, table_ref=rrr}, {name=a1, table_ref=ttt}], interface={b2=[{name=b2, table_ref=qqq}], a2=[{name=a2, table_ref=ttt}], b1=[{name=b1, table_ref=qqq}], c2=[{name=c2, table_ref=rrr}]}, table_alias={aaa=values0, ccc=query6, bbb=query3}, def_query3={context_list={aaa=values0}, query_dictionary={b2=[[@43,100:101='b2',<381>,3:28], [@149,407:409='qqq',<381>,7:16]], b3=[[@52,119:120='b3',<381>,3:47], [@134,365:367='ooo',<381>,6:42]], b1=[[@39,92:93='b1',<381>,3:20], [@145,399:401='qqq',<381>,7:8], [@169,467:469='qqq',<381>,9:20], [@185,512:514='qqq',<381>,10:29]]}, table_dictionary={tab2={b2=[[@41,96:98='lll',<381>,3:24], [@63,156:158='lll',<381>,3:84], [@149,407:409='qqq',<381>,7:16]], b3=[[@47,108:110='lll',<381>,3:36], [@69,182:184='lll',<381>,4:19], [@134,365:367='ooo',<381>,6:42]], b1=[[@37,88:90='lll',<381>,3:16], [@59,148:150='lll',<381>,3:76], [@145,399:401='qqq',<381>,7:8], [@169,467:469='qqq',<381>,9:20], [@185,512:514='qqq',<381>,10:29]]}}, grouped_by=[{name=b1, table_ref=lll}, {name=b2, table_ref=lll}], def_query1={context_list={aaa=values0, mmm=values0}, query_dictionary={unnamed_0=[[@81,211:211=')',<288>,4:48]]}, table_dictionary={}, interface={unnamed_0=[{name=a3, table_ref=mmm}]}, table_alias={aaa=values0, mmm=values0}}, dependent_queries={predicand2={query=query1, type=filters}}, filters=[{name=b3, table_ref=lll}], interface={b2=[{name=b2, table_ref=lll}], b3=[{name=b3, table_ref=lll}], b1=[{name=b1, table_ref=lll}]}, table_alias={aaa=values0, lll=tab2}}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void nestedWithScalarWhereAliasHeavyValuesCteVirtualColumnsExemplarParsesWithoutErrors() {
		final String sql = "WITH "
				+ "\n aaa AS (SELECT kkk.a1, kkk.a2, kkk.a3 FROM tab1 AS kkk),"
				+ "\n bbb AS ((VALUES (71, 81, 91), (72, 82, 92)) AS hhh(b1, b2, b3)),"
				+ "\n ccc AS (SELECT nnn.c1, nnn.c2, nnn.c3 FROM tab3 AS nnn"
				+ "\n        WHERE nnn.c3 = (SELECT max(ooo.b3) FROM bbb AS ooo WHERE ooo.b1 = nnn.c1))"
				+ "\n SELECT ppp.a1, qqq.b2, rrr.c2"
				+ "\n FROM aaa AS ppp"
				+ "\n JOIN bbb AS qqq ON ppp.a1 = qqq.b1"
				+ "\n JOIN ccc AS rrr ON qqq.b1 = rrr.c1";

		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runParsertest(sql, parser);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong", "{SQL={with={1={cte={select={1={column={name=a1, table_ref=kkk}}, 2={column={name=a2, table_ref=kkk}}, 3={column={name=a3, table_ref=kkk}}}, from={table={alias=kkk, table=tab1}}}, alias=aaa}, 2={cte={values={columns={1={column={name=b1, table_ref=null}}, 2={column={name=b2, table_ref=null}}, 3={column={name=b3, table_ref=null}}}, alias=hhh, matrix={1={row={1={literal=71}, 2={literal=81}, 3={literal=91}}}, 2={row={1={literal=72}, 2={literal=82}, 3={literal=92}}}}}}, alias=bbb}, 3={cte={select={1={column={name=c1, table_ref=nnn}}, 2={column={name=c2, table_ref=nnn}}, 3={column={name=c3, table_ref=nnn}}}, from={table={alias=nnn, table=tab3}}, where={condition={left={column={name=c3, table_ref=nnn}}, right={select={1={function={function_name=max, qualifier=null, parameters={column={name=b3, table_ref=ooo}}}}}, from={table={alias=ooo, table=bbb}}, where={condition={left={column={name=b1, table_ref=ooo}}, right={column={name=c1, table_ref=nnn}}, operator==}}}, operator==}}}, alias=ccc}}, query={select={1={column={name=a1, table_ref=ppp}}, 2={column={name=b2, table_ref=qqq}}, 3={column={name=c2, table_ref=rrr}}}, from={join={1={table={alias=ppp, table=aaa}}, 2={join=JOIN, on={condition={left={column={name=a1, table_ref=ppp}}, right={column={name=b1, table_ref=qqq}}, operator==}}}, 3={table={alias=qqq, table=bbb}}, 4={join=JOIN, on={condition={left={column={name=b1, table_ref=qqq}}, right={column={name=c1, table_ref=rrr}}, operator==}}}, 5={table={alias=rrr, table=ccc}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[a1, b2, c2]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab3={c3=[[@66,162:164='nnn',<381>,4:32], [@74,200:202='nnn',<381>,5:14]], c1=[[@58,146:148='nnn',<381>,4:16], [@95,260:262='nnn',<381>,5:74], [@137,382:384='rrr',<381>,9:29]], c2=[[@62,154:156='nnn',<381>,4:24], [@109,293:295='rrr',<381>,6:24]]}, tab1={a1=[[@5,22:24='kkk',<381>,2:16], [@101,277:279='ppp',<381>,6:8], [@121,337:339='ppp',<381>,8:20]], a2=[[@9,30:32='kkk',<381>,2:24]], a3=[[@13,38:40='kkk',<381>,2:32]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{values1={b2=[[@48,120:121='b2',<381>,3:56], [@105,285:287='qqq',<381>,6:16]], b3=[[@50,124:125='b3',<381>,3:60], [@82,221:223='ooo',<381>,5:35]], b1=[[@46,116:117='b1',<381>,3:52], [@91,251:253='ooo',<381>,5:65], [@125,346:348='qqq',<381>,8:29], [@133,373:375='qqq',<381>,9:20]]}, query4={c3=[[@68,166:167='c3',<381>,4:36]], c1=[[@60,150:151='c1',<381>,4:20], [@137,382:384='rrr',<381>,9:29]], c2=[[@64,158:159='c2',<381>,4:28], [@109,293:295='rrr',<381>,6:24]]}, query5={a1=[[@103,281:282='a1',<381>,6:12]], b2=[[@107,289:290='b2',<381>,6:20]], c2=[[@111,297:298='c2',<381>,6:28]]}, query0={a1=[[@7,26:27='a1',<381>,2:20], [@101,277:279='ppp',<381>,6:8], [@121,337:339='ppp',<381>,8:20]], a2=[[@11,34:35='a2',<381>,2:28]], a3=[[@15,42:43='a3',<381>,2:36]]}, query2={unnamed_0=[[@85,227:227=')',<288>,5:41]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query5={context_list={aaa=query0, bbb=values1, ccc=query4, ppp=query0, qqq=values1, rrr=query4}, query_dictionary={a1=[[@103,281:282='a1',<381>,6:12]], b2=[[@107,289:290='b2',<381>,6:20]], c2=[[@111,297:298='c2',<381>,6:28]]}, table_dictionary={}, def_values1={query_dictionary={b2=[[@48,120:121='b2',<381>,3:56], [@105,285:287='qqq',<381>,6:16]], b3=[[@50,124:125='b3',<381>,3:60], [@82,221:223='ooo',<381>,5:35]], b1=[[@46,116:117='b1',<381>,3:52], [@91,251:253='ooo',<381>,5:65], [@125,346:348='qqq',<381>,8:29], [@133,373:375='qqq',<381>,9:20]]}, table_dictionary={}, interface={b2=[], b3=[], b1=[]}}, def_query0={query_dictionary={a1=[[@7,26:27='a1',<381>,2:20], [@101,277:279='ppp',<381>,6:8], [@121,337:339='ppp',<381>,8:20]], a2=[[@11,34:35='a2',<381>,2:28]], a3=[[@15,42:43='a3',<381>,2:36]]}, table_dictionary={tab1={a1=[[@5,22:24='kkk',<381>,2:16], [@101,277:279='ppp',<381>,6:8], [@121,337:339='ppp',<381>,8:20]], a2=[[@9,30:32='kkk',<381>,2:24]], a3=[[@13,38:40='kkk',<381>,2:32]]}}, interface={a1=[{name=a1, table_ref=kkk}], a2=[{name=a2, table_ref=kkk}], a3=[{name=a3, table_ref=kkk}]}, table_alias={kkk=tab1}}, filters=[{name=a1, table_ref=ppp}, {name=b1, table_ref=qqq}, {name=c1, table_ref=rrr}], interface={a1=[{name=a1, table_ref=ppp}], b2=[{name=b2, table_ref=qqq}], c2=[{name=c2, table_ref=rrr}]}, def_query4={context_list={aaa=query0, bbb=values1}, query_dictionary={c3=[[@68,166:167='c3',<381>,4:36]], c1=[[@60,150:151='c1',<381>,4:20], [@137,382:384='rrr',<381>,9:29]], c2=[[@64,158:159='c2',<381>,4:28], [@109,293:295='rrr',<381>,6:24]]}, table_dictionary={tab3={c3=[[@66,162:164='nnn',<381>,4:32], [@74,200:202='nnn',<381>,5:14]], c1=[[@58,146:148='nnn',<381>,4:16], [@95,260:262='nnn',<381>,5:74], [@137,382:384='rrr',<381>,9:29]], c2=[[@62,154:156='nnn',<381>,4:24], [@109,293:295='rrr',<381>,6:24]]}}, dependent_queries={predicand3={query=query2, type=filters}}, filters=[{name=c3, table_ref=nnn}], interface={c3=[{name=c3, table_ref=nnn}], c1=[{name=c1, table_ref=nnn}], c2=[{name=c2, table_ref=nnn}]}, table_alias={aaa=query0, bbb=values1, nnn=tab3}, def_query2={context_list={aaa=query0, bbb=values1, ooo=values1}, query_dictionary={unnamed_0=[[@85,227:227=')',<288>,5:41]]}, table_dictionary={}, filters=[{name=b1, table_ref=ooo}, {name=c1, table_ref=nnn}], interface={unnamed_0=[{name=b3, table_ref=ooo}]}, table_alias={aaa=query0, bbb=values1, ooo=values1}}}, table_alias={aaa=query0, ccc=query4, bbb=values1}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void nestedWithScalarSelectListAliasHeavyValuesCteVirtualColumnsExemplarParsesWithoutErrors() {
		final String sql = "WITH "
				+ "\n aaa AS (SELECT kkk.a1, kkk.a2, kkk.a3 FROM tab1 AS kkk),"
				+ "\n bbb AS (SELECT lll.b1, lll.b2, (SELECT max(mmm.a3) FROM aaa AS mmm) AS b3 FROM tab2 AS lll),"
				+ "\n ccc AS ((VALUES (301, 401, 501), (302, 402, 502)) AS hhh(c1, c2, c3))"
				+ "\n SELECT ppp.a1, qqq.b2, rrr.c2"
				+ "\n FROM aaa AS ppp"
				+ "\n JOIN bbb AS qqq ON ppp.a1 = qqq.b1"
				+ "\n JOIN ccc AS rrr ON qqq.b1 = rrr.c1";

		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runParsertest(sql, parser);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong", "{SQL={with={1={cte={select={1={column={name=a1, table_ref=kkk}}, 2={column={name=a2, table_ref=kkk}}, 3={column={name=a3, table_ref=kkk}}}, from={table={alias=kkk, table=tab1}}}, alias=aaa}, 2={cte={select={1={column={name=b1, table_ref=lll}}, 2={column={name=b2, table_ref=lll}}, 3={lookup={from={table={alias=mmm, table=aaa}}, select={1={function={function_name=max, qualifier=null, parameters={column={name=a3, table_ref=mmm}}}}}}, alias=b3}}, from={table={alias=lll, table=tab2}}}, alias=bbb}, 3={cte={values={columns={1={column={name=c1, table_ref=null}}, 2={column={name=c2, table_ref=null}}, 3={column={name=c3, table_ref=null}}}, alias=hhh, matrix={1={row={1={literal=301}, 2={literal=401}, 3={literal=501}}}, 2={row={1={literal=302}, 2={literal=402}, 3={literal=502}}}}}}, alias=ccc}}, query={select={1={column={name=a1, table_ref=ppp}}, 2={column={name=b2, table_ref=qqq}}, 3={column={name=c2, table_ref=rrr}}}, from={join={1={table={alias=ppp, table=aaa}}, 2={join=JOIN, on={condition={left={column={name=a1, table_ref=ppp}}, right={column={name=b1, table_ref=qqq}}, operator==}}}, 3={table={alias=qqq, table=bbb}}, 4={join=JOIN, on={condition={left={column={name=b1, table_ref=qqq}}, right={column={name=c1, table_ref=rrr}}, operator==}}}, 5={table={alias=rrr, table=ccc}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[a1, b2, c2]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={a1=[[@5,22:24='kkk',<381>,2:16], [@87,237:239='ppp',<381>,5:8], [@107,297:299='ppp',<381>,7:20]], a2=[[@9,30:32='kkk',<381>,2:24]], a3=[[@13,38:40='kkk',<381>,2:32], [@38,108:110='mmm',<381>,3:44]]}, tab2={b2=[[@30,88:90='lll',<381>,3:24], [@91,245:247='qqq',<381>,5:16]], b1=[[@26,80:82='lll',<381>,3:16], [@111,306:308='qqq',<381>,7:29], [@119,333:335='qqq',<381>,8:20]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query5={a1=[[@89,241:242='a1',<381>,5:12]], b2=[[@93,249:250='b2',<381>,5:20]], c2=[[@97,257:258='c2',<381>,5:28]]}, query0={a1=[[@7,26:27='a1',<381>,2:20], [@87,237:239='ppp',<381>,5:8], [@107,297:299='ppp',<381>,7:20]], a2=[[@11,34:35='a2',<381>,2:28]], a3=[[@15,42:43='a3',<381>,2:36], [@38,108:110='mmm',<381>,3:44]]}, values4={c3=[[@83,224:225='c3',<381>,4:66]], c1=[[@79,216:217='c1',<381>,4:58], [@123,342:344='rrr',<381>,8:29]], c2=[[@81,220:221='c2',<381>,4:62], [@95,253:255='rrr',<381>,5:24]]}, query1={unnamed_0=[[@41,114:114=')',<288>,3:50]]}, query3={b2=[[@32,92:93='b2',<381>,3:28], [@91,245:247='qqq',<381>,5:16]], b3=[[@48,136:137='b3',<381>,3:72]], b1=[[@28,84:85='b1',<381>,3:20], [@111,306:308='qqq',<381>,7:29], [@119,333:335='qqq',<381>,8:20]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query5={context_list={aaa=query0, bbb=query3, ccc=values4, ppp=query0, qqq=query3, rrr=values4}, query_dictionary={a1=[[@89,241:242='a1',<381>,5:12]], b2=[[@93,249:250='b2',<381>,5:20]], c2=[[@97,257:258='c2',<381>,5:28]]}, table_dictionary={}, def_query0={query_dictionary={a1=[[@7,26:27='a1',<381>,2:20], [@87,237:239='ppp',<381>,5:8], [@107,297:299='ppp',<381>,7:20]], a2=[[@11,34:35='a2',<381>,2:28]], a3=[[@15,42:43='a3',<381>,2:36], [@38,108:110='mmm',<381>,3:44]]}, table_dictionary={tab1={a1=[[@5,22:24='kkk',<381>,2:16], [@87,237:239='ppp',<381>,5:8], [@107,297:299='ppp',<381>,7:20]], a2=[[@9,30:32='kkk',<381>,2:24]], a3=[[@13,38:40='kkk',<381>,2:32], [@38,108:110='mmm',<381>,3:44]]}}, interface={a1=[{name=a1, table_ref=kkk}], a2=[{name=a2, table_ref=kkk}], a3=[{name=a3, table_ref=kkk}]}, table_alias={kkk=tab1}}, def_values4={query_dictionary={c3=[[@83,224:225='c3',<381>,4:66]], c1=[[@79,216:217='c1',<381>,4:58], [@123,342:344='rrr',<381>,8:29]], c2=[[@81,220:221='c2',<381>,4:62], [@95,253:255='rrr',<381>,5:24]]}, table_dictionary={}, interface={c3=[], c1=[], c2=[]}}, filters=[{name=a1, table_ref=ppp}, {name=b1, table_ref=qqq}, {name=c1, table_ref=rrr}], interface={a1=[{name=a1, table_ref=ppp}], b2=[{name=b2, table_ref=qqq}], c2=[{name=c2, table_ref=rrr}]}, table_alias={aaa=query0, ccc=values4, bbb=query3}, def_query3={context_list={aaa=query0}, query_dictionary={b2=[[@32,92:93='b2',<381>,3:28], [@91,245:247='qqq',<381>,5:16]], b3=[[@48,136:137='b3',<381>,3:72]], b1=[[@28,84:85='b1',<381>,3:20], [@111,306:308='qqq',<381>,7:29], [@119,333:335='qqq',<381>,8:20]]}, table_dictionary={tab2={b2=[[@30,88:90='lll',<381>,3:24], [@91,245:247='qqq',<381>,5:16]], b1=[[@26,80:82='lll',<381>,3:16], [@111,306:308='qqq',<381>,7:29], [@119,333:335='qqq',<381>,8:20]]}}, def_query1={context_list={aaa=query0, mmm=query0}, query_dictionary={unnamed_0=[[@41,114:114=')',<288>,3:50]]}, table_dictionary={}, interface={unnamed_0=[{name=a3, table_ref=mmm}]}, table_alias={aaa=query0, mmm=query0}}, dependent_queries={predicand2={query=query1, type=interface}}, interface={b2=[{name=b2, table_ref=lll}], b3=[{name=a3, table_ref=mmm}], b1=[{name=b1, table_ref=lll}]}, table_alias={aaa=query0, lll=tab2}}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void nestedWithExistsAliasHeavySnowflakeTableFunctionColumnsExemplarParsesWithoutErrors() {
		final String sql = "WITH "
				+ "\n aaa AS (SELECT tf.seq, tf.index, tf.value FROM TABLE(SPLIT_TO_TABLE('11,12', ',')) tf),"
				+ "\n bbb_ccc AS ("
				+ "\n   WITH "
				+ "\n     bbb AS (SELECT lll.b1, lll.b2, lll.b3 FROM tab2 AS lll WHERE EXISTS (SELECT 1 FROM aaa AS mmm WHERE mmm.value = lll.b3)),"
				+ "\n     ccc AS (SELECT nnn.c1, nnn.c2, nnn.c3 FROM tab3 AS nnn WHERE EXISTS (SELECT 1 FROM aaa AS vvv WHERE vvv.seq = nnn.c1))"
				+ "\n   SELECT ppp.b1 AS b1, qqq.c1 AS c1, qqq.c2 AS c2"
				+ "\n   FROM bbb AS ppp JOIN ccc AS qqq ON ppp.b1 = qqq.c1"
				+ "\n )"
				+ "\n SELECT sss.b1, sss.c2, ttt.index"
				+ "\n FROM bbb_ccc AS sss"
				+ "\n JOIN aaa AS ttt ON ttt.seq = sss.b1";

		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runParsertest(sql, parser);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong", "{SQL={with={1={cte={select={1={column={name=seq, table_ref=tf}}, 2={column={name=index, table_ref=tf}}, 3={column={name=value, table_ref=tf}}}, from={table={alias=tf, table_function={function={function_name=SPLIT_TO_TABLE, parameters={1={literal='11,12'}, 2={literal=','}}}}}}}, alias=aaa}, 2={cte={with={1={cte={select={1={column={name=b1, table_ref=lll}}, 2={column={name=b2, table_ref=lll}}, 3={column={name=b3, table_ref=lll}}}, from={table={alias=lll, table=tab2}}, where={exists={select={1={literal=1}}, from={table={alias=mmm, table=aaa}}, where={condition={left={column={name=value, table_ref=mmm}}, right={column={name=b3, table_ref=lll}}, operator==}}, operator=EXISTS}}}, alias=bbb}, 2={cte={select={1={column={name=c1, table_ref=nnn}}, 2={column={name=c2, table_ref=nnn}}, 3={column={name=c3, table_ref=nnn}}}, from={table={alias=nnn, table=tab3}}, where={exists={select={1={literal=1}}, from={table={alias=vvv, table=aaa}}, where={condition={left={column={name=seq, table_ref=vvv}}, right={column={name=c1, table_ref=nnn}}, operator==}}, operator=EXISTS}}}, alias=ccc}}, query={select={1={column={name=b1, table_ref=ppp}, alias=b1}, 2={column={name=c1, table_ref=qqq}, alias=c1}, 3={column={name=c2, table_ref=qqq}, alias=c2}}, from={join={1={table={alias=ppp, table=bbb}}, 2={join=JOIN, on={condition={left={column={name=b1, table_ref=ppp}}, right={column={name=c1, table_ref=qqq}}, operator==}}}, 3={table={alias=qqq, table=ccc}}}}}}, alias=bbb_ccc}}, query={select={1={column={name=b1, table_ref=sss}}, 2={column={name=c2, table_ref=sss}}, 3={column={name=index, table_ref=ttt}}}, from={join={1={table={alias=sss, table=bbb_ccc}}, 2={join=JOIN, on={condition={left={column={name=seq, table_ref=ttt}}, right={column={name=b1, table_ref=sss}}, operator==}}}, 3={table={alias=ttt, table=aaa}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[index, b1, c2]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab3={c3=[[@84,281:283='nnn',<381>,6:36]], c1=[[@76,265:267='nnn',<381>,6:20], [@105,360:362='nnn',<381>,6:115], [@117,393:395='qqq',<381>,7:24], [@141,467:469='qqq',<381>,8:47]], c2=[[@80,273:275='nnn',<381>,6:28], [@123,407:409='qqq',<381>,7:38]]}, tab2={b2=[[@41,146:148='lll',<381>,5:28]], b3=[[@45,154:156='lll',<381>,5:36], [@66,235:237='lll',<381>,5:117]], b1=[[@37,138:140='lll',<381>,5:20], [@111,379:381='ppp',<381>,7:10], [@137,458:460='ppp',<381>,8:38]]}, table_function0={index=[[@9,30:31='tf',<381>,2:24], [@154,501:503='ttt',<381>,10:24]], value=[[@13,40:41='tf',<381>,2:34], [@62,223:225='mmm',<381>,5:105]], seq=[[@5,22:23='tf',<381>,2:16], [@101,350:352='vvv',<381>,6:105], [@166,552:554='ttt',<381>,12:20]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query8={index=[[@156,505:509='index',<96>,10:28]], b1=[[@148,489:490='b1',<381>,10:12]], c2=[[@152,497:498='c2',<381>,10:20]]}, query4={unnamed_1=[[@95,326:326='1',<300>,6:81]]}, query6={c3=[[@86,285:286='c3',<381>,6:40]], c1=[[@78,269:270='c1',<381>,6:24], [@117,393:395='qqq',<381>,7:24], [@141,467:469='qqq',<381>,8:47]], c2=[[@82,277:278='c2',<381>,6:32], [@123,407:409='qqq',<381>,7:38]]}, query7={c1=[[@121,403:404='c1',<381>,7:34]], b1=[[@115,389:390='b1',<381>,7:20], [@146,485:487='sss',<381>,10:8], [@170,562:564='sss',<381>,12:30]], c2=[[@127,417:418='c2',<381>,7:48], [@150,493:495='sss',<381>,10:16]]}, query0={index=[[@11,33:37='index',<96>,2:27], [@154,501:503='ttt',<381>,10:24]], value=[[@15,43:47='value',<381>,2:37], [@62,223:225='mmm',<381>,5:105]], seq=[[@7,25:27='seq',<381>,2:19], [@101,350:352='vvv',<381>,6:105], [@166,552:554='ttt',<381>,12:20]]}, query1={unnamed_0=[[@56,199:199='1',<300>,5:81]]}, query3={b2=[[@43,150:151='b2',<381>,5:32]], b3=[[@47,158:159='b3',<381>,5:40]], b1=[[@39,142:143='b1',<381>,5:24], [@111,379:381='ppp',<381>,7:10], [@137,458:460='ppp',<381>,8:38]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query8={context_list={aaa=query0, bbb_ccc=query7, sss=query7, ttt=query0}, def_query7={context_list={aaa=query0, bbb=query3, ccc=query6, ppp=query3, qqq=query6}, def_query6={context_list={aaa=query0, bbb=query3}, query_dictionary={c3=[[@86,285:286='c3',<381>,6:40]], c1=[[@78,269:270='c1',<381>,6:24], [@117,393:395='qqq',<381>,7:24], [@141,467:469='qqq',<381>,8:47]], c2=[[@82,277:278='c2',<381>,6:32], [@123,407:409='qqq',<381>,7:38]]}, table_dictionary={tab3={c3=[[@84,281:283='nnn',<381>,6:36]], c1=[[@76,265:267='nnn',<381>,6:20], [@105,360:362='nnn',<381>,6:115], [@117,393:395='qqq',<381>,7:24], [@141,467:469='qqq',<381>,8:47]], c2=[[@80,273:275='nnn',<381>,6:28], [@123,407:409='qqq',<381>,7:38]]}}, dependent_queries={exists5={query=query4, type=filters}}, filters=[], interface={c3=[{name=c3, table_ref=nnn}], c1=[{name=c1, table_ref=nnn}], c2=[{name=c2, table_ref=nnn}]}, def_query4={context_list={aaa=query0, bbb=query3, vvv=query0}, query_dictionary={unnamed_1=[[@95,326:326='1',<300>,6:81]]}, table_dictionary={}, filters=[{name=seq, table_ref=vvv}, {name=c1, table_ref=nnn}], interface={unnamed_1=[]}, table_alias={aaa=query0, bbb=query3, vvv=query0}}, table_alias={aaa=query0, bbb=query3, nnn=tab3}}, table_dictionary={}, def_query0={query_dictionary={index=[[@11,33:37='index',<96>,2:27], [@154,501:503='ttt',<381>,10:24]], value=[[@15,43:47='value',<381>,2:37], [@62,223:225='mmm',<381>,5:105]], seq=[[@7,25:27='seq',<381>,2:19], [@101,350:352='vvv',<381>,6:105], [@166,552:554='ttt',<381>,12:20]]}, table_dictionary={table_function0={index=[[@9,30:31='tf',<381>,2:24], [@154,501:503='ttt',<381>,10:24]], value=[[@13,40:41='tf',<381>,2:34], [@62,223:225='mmm',<381>,5:105]], seq=[[@5,22:23='tf',<381>,2:16], [@101,350:352='vvv',<381>,6:105], [@166,552:554='ttt',<381>,12:20]]}}, interface={index=[{name=index, table_ref=tf}], value=[{name=value, table_ref=tf}], seq=[{name=seq, table_ref=tf}]}, table_alias={tf=table_function0}}, filters=[{name=b1, table_ref=ppp}, {name=c1, table_ref=qqq}], interface={c1=[{name=c1, table_ref=qqq}], b1=[{name=b1, table_ref=ppp}], c2=[{name=c2, table_ref=qqq}]}, def_query3={context_list={aaa=query0}, query_dictionary={b2=[[@43,150:151='b2',<381>,5:32]], b3=[[@47,158:159='b3',<381>,5:40]], b1=[[@39,142:143='b1',<381>,5:24], [@111,379:381='ppp',<381>,7:10], [@137,458:460='ppp',<381>,8:38]]}, table_dictionary={tab2={b2=[[@41,146:148='lll',<381>,5:28]], b3=[[@45,154:156='lll',<381>,5:36], [@66,235:237='lll',<381>,5:117]], b1=[[@37,138:140='lll',<381>,5:20], [@111,379:381='ppp',<381>,7:10], [@137,458:460='ppp',<381>,8:38]]}}, def_query1={context_list={aaa=query0, mmm=query0}, query_dictionary={unnamed_0=[[@56,199:199='1',<300>,5:81]]}, table_dictionary={}, filters=[{name=value, table_ref=mmm}, {name=b3, table_ref=lll}], interface={unnamed_0=[]}, table_alias={aaa=query0, mmm=query0}}, dependent_queries={exists2={query=query1, type=filters}}, filters=[], interface={b2=[{name=b2, table_ref=lll}], b3=[{name=b3, table_ref=lll}], b1=[{name=b1, table_ref=lll}]}, table_alias={aaa=query0, lll=tab2}}, query_dictionary={c1=[[@121,403:404='c1',<381>,7:34]], b1=[[@115,389:390='b1',<381>,7:20], [@146,485:487='sss',<381>,10:8], [@170,562:564='sss',<381>,12:30]], c2=[[@127,417:418='c2',<381>,7:48], [@150,493:495='sss',<381>,10:16]]}, table_alias={aaa=query0, ccc=query6, bbb=query3}}, query_dictionary={index=[[@156,505:509='index',<96>,10:28]], b1=[[@148,489:490='b1',<381>,10:12]], c2=[[@152,497:498='c2',<381>,10:20]]}, table_dictionary={}, def_query0={query_dictionary={index=[[@11,33:37='index',<96>,2:27], [@154,501:503='ttt',<381>,10:24]], value=[[@15,43:47='value',<381>,2:37], [@62,223:225='mmm',<381>,5:105]], seq=[[@7,25:27='seq',<381>,2:19], [@101,350:352='vvv',<381>,6:105], [@166,552:554='ttt',<381>,12:20]]}, table_dictionary={table_function0={index=[[@9,30:31='tf',<381>,2:24], [@154,501:503='ttt',<381>,10:24]], value=[[@13,40:41='tf',<381>,2:34], [@62,223:225='mmm',<381>,5:105]], seq=[[@5,22:23='tf',<381>,2:16], [@101,350:352='vvv',<381>,6:105], [@166,552:554='ttt',<381>,12:20]]}}, interface={index=[{name=index, table_ref=tf}], value=[{name=value, table_ref=tf}], seq=[{name=seq, table_ref=tf}]}, table_alias={tf=table_function0}}, filters=[{name=seq, table_ref=ttt}, {name=b1, table_ref=sss}], interface={index=[{name=index, table_ref=ttt}], b1=[{name=b1, table_ref=sss}], c2=[{name=c2, table_ref=sss}]}, table_alias={aaa=query0, bbb_ccc=query7}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void nestedWithUnionAliasHeavySnowflakeTableFunctionColumnsExemplarParsesWithoutErrors() {
		final String sql = "WITH "
				+ "\n aaa AS (SELECT kkk.a1, kkk.a2, kkk.a3 FROM tab1 AS kkk),"
				+ "\n bbb AS (SELECT tf.seq AS seq, tf.index AS index, tf.value AS value FROM TABLE(SPLIT_TO_TABLE('101,102', ',')) tf),"
				+ "\n ccc AS (SELECT nnn.c1, nnn.c2, nnn.c3 FROM tab3 AS nnn UNION ALL SELECT ooo.seq AS c1, ooo.index AS c2, ooo.value AS c3 FROM bbb AS ooo)"
				+ "\n SELECT ppp.a1, qqq.index, rrr.c2"
				+ "\n FROM aaa AS ppp"
				+ "\n JOIN bbb AS qqq ON ppp.a1 = qqq.seq"
				+ "\n JOIN ccc AS rrr ON qqq.seq = rrr.c1";

		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runParsertest(sql, parser);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong", "{SQL={with={1={cte={select={1={column={name=a1, table_ref=kkk}}, 2={column={name=a2, table_ref=kkk}}, 3={column={name=a3, table_ref=kkk}}}, from={table={alias=kkk, table=tab1}}}, alias=aaa}, 2={cte={select={1={column={name=seq, table_ref=tf}, alias=seq}, 2={column={name=index, table_ref=tf}, alias=index}, 3={column={name=value, table_ref=tf}, alias=value}}, from={table={alias=tf, table_function={function={function_name=SPLIT_TO_TABLE, parameters={1={literal='101,102'}, 2={literal=','}}}}}}}, alias=bbb}, 3={cte={union={1={select={1={column={name=c1, table_ref=nnn}}, 2={column={name=c2, table_ref=nnn}}, 3={column={name=c3, table_ref=nnn}}}, from={table={alias=nnn, table=tab3}}}, 2={union={qualifier=ALL, operator=UNION}}, 3={select={1={column={name=seq, table_ref=ooo}, alias=c1}, 2={column={name=index, table_ref=ooo}, alias=c2}, 3={column={name=value, table_ref=ooo}, alias=c3}}, from={table={alias=ooo, table=bbb}}}}}, alias=ccc}}, query={select={1={column={name=a1, table_ref=ppp}}, 2={column={name=index, table_ref=qqq}}, 3={column={name=c2, table_ref=rrr}}}, from={join={1={table={alias=ppp, table=aaa}}, 2={join=JOIN, on={condition={left={column={name=a1, table_ref=ppp}}, right={column={name=seq, table_ref=qqq}}, operator==}}}, 3={table={alias=qqq, table=bbb}}, 4={join=JOIN, on={condition={left={column={name=seq, table_ref=qqq}}, right={column={name=c1, table_ref=rrr}}, operator==}}}, 5={table={alias=rrr, table=ccc}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[a1, index, c2]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab3={c3=[[@68,212:214='nnn',<381>,4:32]], c1=[[@60,196:198='nnn',<381>,4:16]], c2=[[@64,204:206='nnn',<381>,4:24]]}, tab1={a1=[[@5,22:24='kkk',<381>,2:16], [@101,326:328='ppp',<381>,5:8], [@121,389:391='ppp',<381>,7:20]], a2=[[@9,30:32='kkk',<381>,2:24]], a3=[[@13,38:40='kkk',<381>,2:32]]}, table_function0={index=[[@32,95:96='tf',<381>,3:31], [@84,268:270='ooo',<381>,4:88], [@105,334:336='qqq',<381>,5:16]], value=[[@38,114:115='tf',<381>,3:50], [@90,285:287='ooo',<381>,4:105]], seq=[[@26,80:81='tf',<381>,3:16], [@78,253:255='ooo',<381>,4:73], [@125,398:400='qqq',<381>,7:29], [@133,426:428='qqq',<381>,8:20]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query5={a1=[[@103,330:331='a1',<381>,5:12]], index=[[@107,338:342='index',<96>,5:20]], c2=[[@111,349:350='c2',<381>,5:31]]}, query0={a1=[[@7,26:27='a1',<381>,2:20], [@101,326:328='ppp',<381>,5:8], [@121,389:391='ppp',<381>,7:20]], a2=[[@11,34:35='a2',<381>,2:28]], a3=[[@15,42:43='a3',<381>,2:36]]}, query1={index=[[@36,107:111='index',<96>,3:43], [@84,268:270='ooo',<381>,4:88], [@105,334:336='qqq',<381>,5:16]], value=[[@42,126:130='value',<381>,3:62], [@90,285:287='ooo',<381>,4:105]], seq=[[@30,90:92='seq',<381>,3:26], [@78,253:255='ooo',<381>,4:73], [@125,398:400='qqq',<381>,7:29], [@133,426:428='qqq',<381>,8:20]]}, query2={c3=[[@70,216:217='c3',<381>,4:36]], c1=[[@62,200:201='c1',<381>,4:20]], c2=[[@66,208:209='c2',<381>,4:28]]}, query3={c3=[[@94,298:299='c3',<381>,4:118]], c1=[[@82,264:265='c1',<381>,4:84]], c2=[[@88,281:282='c2',<381>,4:101]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query5={context_list={aaa=query0, bbb=query1, ccc=union4, ppp=query0, qqq=query1, rrr=union4}, def_union4={context_list={aaa=query0, bbb=query1}, interface={c3=[{name=c3, table_ref=nnn}, {name=value, table_ref=ooo}], c1=[{name=c1, table_ref=nnn}, {name=seq, table_ref=ooo}], c2=[{name=c2, table_ref=nnn}, {name=index, table_ref=ooo}]}, table_alias={aaa=query0, bbb=query1}, query2={context_list={aaa=query0, bbb=query1}, query_dictionary={c3=[[@70,216:217='c3',<381>,4:36]], c1=[[@62,200:201='c1',<381>,4:20]], c2=[[@66,208:209='c2',<381>,4:28]]}, table_dictionary={tab3={c3=[[@68,212:214='nnn',<381>,4:32]], c1=[[@60,196:198='nnn',<381>,4:16]], c2=[[@64,204:206='nnn',<381>,4:24]]}}, interface={c3=[{name=c3, table_ref=nnn}], c1=[{name=c1, table_ref=nnn}], c2=[{name=c2, table_ref=nnn}]}, table_alias={aaa=query0, bbb=query1, nnn=tab3}}, query3={context_list={aaa=query0, bbb=query1, ooo=query1}, query_dictionary={c3=[[@94,298:299='c3',<381>,4:118]], c1=[[@82,264:265='c1',<381>,4:84]], c2=[[@88,281:282='c2',<381>,4:101]]}, table_dictionary={}, interface={c3=[{name=value, table_ref=ooo}], c1=[{name=seq, table_ref=ooo}], c2=[{name=index, table_ref=ooo}]}, table_alias={aaa=query0, bbb=query1, ooo=query1}}}, query_dictionary={a1=[[@103,330:331='a1',<381>,5:12]], index=[[@107,338:342='index',<96>,5:20]], c2=[[@111,349:350='c2',<381>,5:31]]}, table_dictionary={}, def_query1={context_list={aaa=query0}, query_dictionary={index=[[@36,107:111='index',<96>,3:43], [@84,268:270='ooo',<381>,4:88], [@105,334:336='qqq',<381>,5:16]], value=[[@42,126:130='value',<381>,3:62], [@90,285:287='ooo',<381>,4:105]], seq=[[@30,90:92='seq',<381>,3:26], [@78,253:255='ooo',<381>,4:73], [@125,398:400='qqq',<381>,7:29], [@133,426:428='qqq',<381>,8:20]]}, table_dictionary={table_function0={index=[[@32,95:96='tf',<381>,3:31], [@84,268:270='ooo',<381>,4:88], [@105,334:336='qqq',<381>,5:16]], value=[[@38,114:115='tf',<381>,3:50], [@90,285:287='ooo',<381>,4:105]], seq=[[@26,80:81='tf',<381>,3:16], [@78,253:255='ooo',<381>,4:73], [@125,398:400='qqq',<381>,7:29], [@133,426:428='qqq',<381>,8:20]]}}, interface={index=[{name=index, table_ref=tf}], value=[{name=value, table_ref=tf}], seq=[{name=seq, table_ref=tf}]}, table_alias={aaa=query0, tf=table_function0}}, def_query0={query_dictionary={a1=[[@7,26:27='a1',<381>,2:20], [@101,326:328='ppp',<381>,5:8], [@121,389:391='ppp',<381>,7:20]], a2=[[@11,34:35='a2',<381>,2:28]], a3=[[@15,42:43='a3',<381>,2:36]]}, table_dictionary={tab1={a1=[[@5,22:24='kkk',<381>,2:16], [@101,326:328='ppp',<381>,5:8], [@121,389:391='ppp',<381>,7:20]], a2=[[@9,30:32='kkk',<381>,2:24]], a3=[[@13,38:40='kkk',<381>,2:32]]}}, interface={a1=[{name=a1, table_ref=kkk}], a2=[{name=a2, table_ref=kkk}], a3=[{name=a3, table_ref=kkk}]}, table_alias={kkk=tab1}}, filters=[{name=a1, table_ref=ppp}, {name=seq, table_ref=qqq}, {name=c1, table_ref=rrr}], interface={a1=[{name=a1, table_ref=ppp}], index=[{name=index, table_ref=qqq}], c2=[{name=c2, table_ref=rrr}]}, table_alias={aaa=query0, ccc=union4, bbb=query1}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void nestedWithIntersectAliasHeavySnowflakeTableFunctionColumnsExemplarParsesWithoutErrors() {
		final String sql = "WITH "
				+ "\n aaa AS (SELECT kkk.a1, kkk.a2, kkk.a3 FROM tab1 AS kkk),"
				+ "\n bbb AS (SELECT lll.b1, lll.b2, lll.b3 FROM tab2 AS lll INTERSECT DISTINCT SELECT mmm.a1 AS b1, mmm.a2 AS b2, mmm.a3 AS b3 FROM aaa AS mmm),"
				+ "\n ccc AS (SELECT tf.seq, tf.index, tf.value FROM TABLE(SPLIT_TO_TABLE('1001,1002', ',')) tf)"
				+ "\n SELECT ppp.a1, qqq.b2, rrr.index"
				+ "\n FROM aaa AS ppp"
				+ "\n JOIN bbb AS qqq ON ppp.a1 = qqq.b1"
				+ "\n JOIN ccc AS rrr ON qqq.b1 = rrr.seq";

		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runParsertest(sql, parser);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong", "{SQL={with={1={cte={select={1={column={name=a1, table_ref=kkk}}, 2={column={name=a2, table_ref=kkk}}, 3={column={name=a3, table_ref=kkk}}}, from={table={alias=kkk, table=tab1}}}, alias=aaa}, 2={cte={intersect={1={select={1={column={name=b1, table_ref=lll}}, 2={column={name=b2, table_ref=lll}}, 3={column={name=b3, table_ref=lll}}}, from={table={alias=lll, table=tab2}}}, 2={intersect={qualifier=DISTINCT, operator=INTERSECT}}, 3={select={1={column={name=a1, table_ref=mmm}, alias=b1}, 2={column={name=a2, table_ref=mmm}, alias=b2}, 3={column={name=a3, table_ref=mmm}, alias=b3}}, from={table={alias=mmm, table=aaa}}}}}, alias=bbb}, 3={cte={select={1={column={name=seq, table_ref=tf}}, 2={column={name=index, table_ref=tf}}, 3={column={name=value, table_ref=tf}}}, from={table={alias=tf, table_function={function={function_name=SPLIT_TO_TABLE, parameters={1={literal='1001,1002'}, 2={literal=','}}}}}}}, alias=ccc}}, query={select={1={column={name=a1, table_ref=ppp}}, 2={column={name=b2, table_ref=qqq}}, 3={column={name=index, table_ref=rrr}}}, from={join={1={table={alias=ppp, table=aaa}}, 2={join=JOIN, on={condition={left={column={name=a1, table_ref=ppp}}, right={column={name=b1, table_ref=qqq}}, operator==}}}, 3={table={alias=qqq, table=bbb}}, 4={join=JOIN, on={condition={left={column={name=b1, table_ref=qqq}}, right={column={name=seq, table_ref=rrr}}, operator==}}}, 5={table={alias=rrr, table=ccc}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[a1, b2, index]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={a1=[[@5,22:24='kkk',<381>,2:16], [@44,146:148='mmm',<381>,3:82], [@95,305:307='ppp',<381>,5:8], [@115,368:370='ppp',<381>,7:20]], a2=[[@9,30:32='kkk',<381>,2:24], [@50,160:162='mmm',<381>,3:96]], a3=[[@13,38:40='kkk',<381>,2:32], [@56,174:176='mmm',<381>,3:110]]}, tab2={b2=[[@30,88:90='lll',<381>,3:24]], b3=[[@34,96:98='lll',<381>,3:32]], b1=[[@26,80:82='lll',<381>,3:16]]}, table_function0={index=[[@75,229:230='tf',<381>,4:24], [@103,321:323='rrr',<381>,5:24]], value=[[@79,239:240='tf',<381>,4:34]], seq=[[@71,221:222='tf',<381>,4:16], [@131,413:415='rrr',<381>,8:29]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query4={index=[[@77,232:236='index',<96>,4:27], [@103,321:323='rrr',<381>,5:24]], value=[[@81,242:246='value',<381>,4:37]], seq=[[@73,224:226='seq',<381>,4:19], [@131,413:415='rrr',<381>,8:29]]}, query5={a1=[[@97,309:310='a1',<381>,5:12]], b2=[[@101,317:318='b2',<381>,5:20]], index=[[@105,325:329='index',<96>,5:28]]}, query0={a1=[[@7,26:27='a1',<381>,2:20], [@44,146:148='mmm',<381>,3:82], [@95,305:307='ppp',<381>,5:8], [@115,368:370='ppp',<381>,7:20]], a2=[[@11,34:35='a2',<381>,2:28], [@50,160:162='mmm',<381>,3:96]], a3=[[@15,42:43='a3',<381>,2:36], [@56,174:176='mmm',<381>,3:110]]}, query1={b2=[[@32,92:93='b2',<381>,3:28]], b3=[[@36,100:101='b3',<381>,3:36]], b1=[[@28,84:85='b1',<381>,3:20]]}, query2={b2=[[@54,170:171='b2',<381>,3:106]], b3=[[@60,184:185='b3',<381>,3:120]], b1=[[@48,156:157='b1',<381>,3:92]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query5={context_list={aaa=query0, bbb=intersect3, ccc=query4, ppp=query0, qqq=intersect3, rrr=query4}, query_dictionary={a1=[[@97,309:310='a1',<381>,5:12]], b2=[[@101,317:318='b2',<381>,5:20]], index=[[@105,325:329='index',<96>,5:28]]}, table_dictionary={}, def_intersect3={context_list={aaa=query0}, interface={b2=[{name=b2, table_ref=lll}, {name=a2, table_ref=mmm}], b3=[{name=b3, table_ref=lll}, {name=a3, table_ref=mmm}], b1=[{name=b1, table_ref=lll}, {name=a1, table_ref=mmm}]}, query1={context_list={aaa=query0}, query_dictionary={b2=[[@32,92:93='b2',<381>,3:28]], b3=[[@36,100:101='b3',<381>,3:36]], b1=[[@28,84:85='b1',<381>,3:20]]}, table_dictionary={tab2={b2=[[@30,88:90='lll',<381>,3:24]], b3=[[@34,96:98='lll',<381>,3:32]], b1=[[@26,80:82='lll',<381>,3:16]]}}, interface={b2=[{name=b2, table_ref=lll}], b3=[{name=b3, table_ref=lll}], b1=[{name=b1, table_ref=lll}]}, table_alias={aaa=query0, lll=tab2}}, table_alias={aaa=query0}, query2={context_list={aaa=query0, mmm=query0}, query_dictionary={b2=[[@54,170:171='b2',<381>,3:106]], b3=[[@60,184:185='b3',<381>,3:120]], b1=[[@48,156:157='b1',<381>,3:92]]}, table_dictionary={}, interface={b2=[{name=a2, table_ref=mmm}], b3=[{name=a3, table_ref=mmm}], b1=[{name=a1, table_ref=mmm}]}, table_alias={aaa=query0, mmm=query0}}}, def_query0={query_dictionary={a1=[[@7,26:27='a1',<381>,2:20], [@44,146:148='mmm',<381>,3:82], [@95,305:307='ppp',<381>,5:8], [@115,368:370='ppp',<381>,7:20]], a2=[[@11,34:35='a2',<381>,2:28], [@50,160:162='mmm',<381>,3:96]], a3=[[@15,42:43='a3',<381>,2:36], [@56,174:176='mmm',<381>,3:110]]}, table_dictionary={tab1={a1=[[@5,22:24='kkk',<381>,2:16], [@44,146:148='mmm',<381>,3:82], [@95,305:307='ppp',<381>,5:8], [@115,368:370='ppp',<381>,7:20]], a2=[[@9,30:32='kkk',<381>,2:24], [@50,160:162='mmm',<381>,3:96]], a3=[[@13,38:40='kkk',<381>,2:32], [@56,174:176='mmm',<381>,3:110]]}}, interface={a1=[{name=a1, table_ref=kkk}], a2=[{name=a2, table_ref=kkk}], a3=[{name=a3, table_ref=kkk}]}, table_alias={kkk=tab1}}, filters=[{name=a1, table_ref=ppp}, {name=b1, table_ref=qqq}, {name=seq, table_ref=rrr}], interface={a1=[{name=a1, table_ref=ppp}], b2=[{name=b2, table_ref=qqq}], index=[{name=index, table_ref=rrr}]}, def_query4={context_list={aaa=query0, bbb=intersect3}, query_dictionary={index=[[@77,232:236='index',<96>,4:27], [@103,321:323='rrr',<381>,5:24]], value=[[@81,242:246='value',<381>,4:37]], seq=[[@73,224:226='seq',<381>,4:19], [@131,413:415='rrr',<381>,8:29]]}, table_dictionary={table_function0={index=[[@75,229:230='tf',<381>,4:24], [@103,321:323='rrr',<381>,5:24]], value=[[@79,239:240='tf',<381>,4:34]], seq=[[@71,221:222='tf',<381>,4:16], [@131,413:415='rrr',<381>,8:29]]}}, interface={index=[{name=index, table_ref=tf}], value=[{name=value, table_ref=tf}], seq=[{name=seq, table_ref=tf}]}, table_alias={aaa=query0, tf=table_function0, bbb=intersect3}}, table_alias={aaa=query0, ccc=query4, bbb=intersect3}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void nestedWithJoinAliasHeavySnowflakeTableFunctionColumnsExemplarParsesWithoutErrors() {
		final String sql = "WITH "
				+ "\n aaa AS (SELECT kkk.a1, kkk.a2, kkk.a3 FROM tab1 AS kkk),"
				+ "\n bbb AS (SELECT tf.seq, tf.index, tf.value FROM TABLE(SPLIT_TO_TABLE('401,402', ',')) tf),"
				+ "\n ccc AS (SELECT nnn.c1, nnn.c2, nnn.c3 FROM tab3 AS nnn JOIN bbb AS ooo ON ooo.value = nnn.c3)"
				+ "\n SELECT ppp.a1, qqq.index, rrr.c2"
				+ "\n FROM aaa AS ppp"
				+ "\n JOIN bbb AS qqq ON ppp.a1 = qqq.seq"
				+ "\n JOIN ccc AS rrr ON qqq.seq = rrr.c1";

		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runParsertest(sql, parser);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong", "{SQL={with={1={cte={select={1={column={name=a1, table_ref=kkk}}, 2={column={name=a2, table_ref=kkk}}, 3={column={name=a3, table_ref=kkk}}}, from={table={alias=kkk, table=tab1}}}, alias=aaa}, 2={cte={select={1={column={name=seq, table_ref=tf}}, 2={column={name=index, table_ref=tf}}, 3={column={name=value, table_ref=tf}}}, from={table={alias=tf, table_function={function={function_name=SPLIT_TO_TABLE, parameters={1={literal='401,402'}, 2={literal=','}}}}}}}, alias=bbb}, 3={cte={select={1={column={name=c1, table_ref=nnn}}, 2={column={name=c2, table_ref=nnn}}, 3={column={name=c3, table_ref=nnn}}}, from={join={1={table={alias=nnn, table=tab3}}, 2={join=JOIN, on={condition={left={column={name=value, table_ref=ooo}}, right={column={name=c3, table_ref=nnn}}, operator==}}}, 3={table={alias=ooo, table=bbb}}}}}, alias=ccc}}, query={select={1={column={name=a1, table_ref=ppp}}, 2={column={name=index, table_ref=qqq}}, 3={column={name=c2, table_ref=rrr}}}, from={join={1={table={alias=ppp, table=aaa}}, 2={join=JOIN, on={condition={left={column={name=a1, table_ref=ppp}}, right={column={name=seq, table_ref=qqq}}, operator==}}}, 3={table={alias=qqq, table=bbb}}, 4={join=JOIN, on={condition={left={column={name=seq, table_ref=qqq}}, right={column={name=c1, table_ref=rrr}}, operator==}}}, 5={table={alias=rrr, table=ccc}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[a1, index, c2]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab3={c3=[[@62,187:189='nnn',<381>,4:32], [@78,242:244='nnn',<381>,4:87]], c1=[[@54,171:173='nnn',<381>,4:16], [@119,368:370='rrr',<381>,8:30]], c2=[[@58,179:181='nnn',<381>,4:24], [@91,277:279='rrr',<381>,5:27]]}, tab1={a1=[[@5,22:24='kkk',<381>,2:16], [@83,258:260='ppp',<381>,5:8], [@103,321:323='ppp',<381>,7:20]], a2=[[@9,30:32='kkk',<381>,2:24]], a3=[[@13,38:40='kkk',<381>,2:32]]}, table_function0={index=[[@30,88:89='tf',<381>,3:24], [@87,266:268='qqq',<381>,5:16]], value=[[@34,98:99='tf',<381>,3:34], [@74,230:232='ooo',<381>,4:75]], seq=[[@26,80:81='tf',<381>,3:16], [@107,330:332='qqq',<381>,7:29], [@115,358:360='qqq',<381>,8:20]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={a1=[[@7,26:27='a1',<381>,2:20], [@83,258:260='ppp',<381>,5:8], [@103,321:323='ppp',<381>,7:20]], a2=[[@11,34:35='a2',<381>,2:28]], a3=[[@15,42:43='a3',<381>,2:36]]}, query1={index=[[@32,91:95='index',<96>,3:27], [@87,266:268='qqq',<381>,5:16]], value=[[@36,101:105='value',<381>,3:37], [@74,230:232='ooo',<381>,4:75]], seq=[[@28,83:85='seq',<381>,3:19], [@107,330:332='qqq',<381>,7:29], [@115,358:360='qqq',<381>,8:20]]}, query2={c3=[[@64,191:192='c3',<381>,4:36]], c1=[[@56,175:176='c1',<381>,4:20], [@119,368:370='rrr',<381>,8:30]], c2=[[@60,183:184='c2',<381>,4:28], [@91,277:279='rrr',<381>,5:27]]}, query3={a1=[[@85,262:263='a1',<381>,5:12]], index=[[@89,270:274='index',<96>,5:20]], c2=[[@93,281:282='c2',<381>,5:31]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query3={context_list={aaa=query0, bbb=query1, ccc=query2, ppp=query0, qqq=query1, rrr=query2}, query_dictionary={a1=[[@85,262:263='a1',<381>,5:12]], index=[[@89,270:274='index',<96>,5:20]], c2=[[@93,281:282='c2',<381>,5:31]]}, table_dictionary={}, def_query1={context_list={aaa=query0}, query_dictionary={index=[[@32,91:95='index',<96>,3:27], [@87,266:268='qqq',<381>,5:16]], value=[[@36,101:105='value',<381>,3:37], [@74,230:232='ooo',<381>,4:75]], seq=[[@28,83:85='seq',<381>,3:19], [@107,330:332='qqq',<381>,7:29], [@115,358:360='qqq',<381>,8:20]]}, table_dictionary={table_function0={index=[[@30,88:89='tf',<381>,3:24], [@87,266:268='qqq',<381>,5:16]], value=[[@34,98:99='tf',<381>,3:34], [@74,230:232='ooo',<381>,4:75]], seq=[[@26,80:81='tf',<381>,3:16], [@107,330:332='qqq',<381>,7:29], [@115,358:360='qqq',<381>,8:20]]}}, interface={index=[{name=index, table_ref=tf}], value=[{name=value, table_ref=tf}], seq=[{name=seq, table_ref=tf}]}, table_alias={aaa=query0, tf=table_function0}}, def_query0={query_dictionary={a1=[[@7,26:27='a1',<381>,2:20], [@83,258:260='ppp',<381>,5:8], [@103,321:323='ppp',<381>,7:20]], a2=[[@11,34:35='a2',<381>,2:28]], a3=[[@15,42:43='a3',<381>,2:36]]}, table_dictionary={tab1={a1=[[@5,22:24='kkk',<381>,2:16], [@83,258:260='ppp',<381>,5:8], [@103,321:323='ppp',<381>,7:20]], a2=[[@9,30:32='kkk',<381>,2:24]], a3=[[@13,38:40='kkk',<381>,2:32]]}}, interface={a1=[{name=a1, table_ref=kkk}], a2=[{name=a2, table_ref=kkk}], a3=[{name=a3, table_ref=kkk}]}, table_alias={kkk=tab1}}, filters=[{name=a1, table_ref=ppp}, {name=seq, table_ref=qqq}, {name=c1, table_ref=rrr}], interface={a1=[{name=a1, table_ref=ppp}], index=[{name=index, table_ref=qqq}], c2=[{name=c2, table_ref=rrr}]}, table_alias={aaa=query0, ccc=query2, bbb=query1}, def_query2={context_list={aaa=query0, bbb=query1, ooo=query1}, query_dictionary={c3=[[@64,191:192='c3',<381>,4:36]], c1=[[@56,175:176='c1',<381>,4:20], [@119,368:370='rrr',<381>,8:30]], c2=[[@60,183:184='c2',<381>,4:28], [@91,277:279='rrr',<381>,5:27]]}, table_dictionary={tab3={c3=[[@62,187:189='nnn',<381>,4:32], [@78,242:244='nnn',<381>,4:87]], c1=[[@54,171:173='nnn',<381>,4:16], [@119,368:370='rrr',<381>,8:30]], c2=[[@58,179:181='nnn',<381>,4:24], [@91,277:279='rrr',<381>,5:27]]}}, filters=[{name=value, table_ref=ooo}, {name=c3, table_ref=nnn}], interface={c3=[{name=c3, table_ref=nnn}], c1=[{name=c1, table_ref=nnn}], c2=[{name=c2, table_ref=nnn}]}, table_alias={aaa=query0, bbb=query1, ooo=query1, nnn=tab3}}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void nestedWithScalarHavingAliasHeavySnowflakeTableFunctionColumnsExemplarParsesWithoutErrors() {
		final String sql = "WITH "
				+ "\n aaa AS (SELECT tf.seq, tf.index, tf.value FROM TABLE(SPLIT_TO_TABLE('31,32', ',')) tf),"
				+ "\n bbb AS (SELECT lll.b1, lll.b2, max(lll.b3) AS b3 FROM tab2 AS lll GROUP BY lll.b1, lll.b2"
				+ "\n        HAVING max(lll.b3) >= (SELECT max(mmm.value) FROM aaa AS mmm)),"
				+ "\n ccc AS (SELECT nnn.c1, nnn.c2, max(nnn.c3) AS c3 FROM tab3 AS nnn GROUP BY nnn.c1, nnn.c2"
				+ "\n        HAVING max(nnn.c3) >= (SELECT max(ooo.b3) FROM bbb AS ooo))"
				+ "\n SELECT qqq.b1, qqq.b2, rrr.c2, ttt.index"
				+ "\n FROM bbb AS qqq"
				+ "\n JOIN ccc AS rrr ON qqq.b1 = rrr.c1"
				+ "\n JOIN aaa AS ttt ON ttt.seq = qqq.b1";

		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runParsertest(sql, parser);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong", "{SQL={with={1={cte={select={1={column={name=seq, table_ref=tf}}, 2={column={name=index, table_ref=tf}}, 3={column={name=value, table_ref=tf}}}, from={table={alias=tf, table_function={function={function_name=SPLIT_TO_TABLE, parameters={1={literal='31,32'}, 2={literal=','}}}}}}}, alias=aaa}, 2={cte={select={1={column={name=b1, table_ref=lll}}, 2={column={name=b2, table_ref=lll}}, 3={function={function_name=max, qualifier=null, parameters={column={name=b3, table_ref=lll}}}, alias=b3}}, having={condition={left={function={function_name=max, qualifier=null, parameters={column={name=b3, table_ref=lll}}}}, right={select={1={function={function_name=max, qualifier=null, parameters={column={name=value, table_ref=mmm}}}}}, from={table={alias=mmm, table=aaa}}}, operator=>=}}, from={table={alias=lll, table=tab2}}, groupby={1={column={name=b1, table_ref=lll}}, 2={column={name=b2, table_ref=lll}}}}, alias=bbb}, 3={cte={select={1={column={name=c1, table_ref=nnn}}, 2={column={name=c2, table_ref=nnn}}, 3={function={function_name=max, qualifier=null, parameters={column={name=c3, table_ref=nnn}}}, alias=c3}}, having={condition={left={function={function_name=max, qualifier=null, parameters={column={name=c3, table_ref=nnn}}}}, right={select={1={function={function_name=max, qualifier=null, parameters={column={name=b3, table_ref=ooo}}}}}, from={table={alias=ooo, table=bbb}}}, operator=>=}}, from={table={alias=nnn, table=tab3}}, groupby={1={column={name=c1, table_ref=nnn}}, 2={column={name=c2, table_ref=nnn}}}}, alias=ccc}}, query={select={1={column={name=b1, table_ref=qqq}}, 2={column={name=b2, table_ref=qqq}}, 3={column={name=c2, table_ref=rrr}}, 4={column={name=index, table_ref=ttt}}}, from={join={1={table={alias=qqq, table=bbb}}, 2={join=JOIN, on={condition={left={column={name=b1, table_ref=qqq}}, right={column={name=c1, table_ref=rrr}}, operator==}}}, 3={table={alias=rrr, table=ccc}}, 4={join=JOIN, on={condition={left={column={name=seq, table_ref=ttt}}, right={column={name=b1, table_ref=qqq}}, operator==}}}, 5={table={alias=ttt, table=aaa}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[b2, index, b1, c2]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab3={c3=[[@99,294:296='nnn',<381>,5:36], [@121,368:370='nnn',<381>,6:19]], c1=[[@89,274:276='nnn',<381>,5:16], [@111,334:336='nnn',<381>,5:76], [@169,505:507='rrr',<381>,9:29]], c2=[[@93,282:284='nnn',<381>,5:24], [@115,342:344='nnn',<381>,5:84], [@149,441:443='rrr',<381>,7:24]]}, tab2={b2=[[@37,119:121='lll',<381>,3:24], [@59,179:181='lll',<381>,3:84], [@145,433:435='qqq',<381>,7:16]], b3=[[@43,131:133='lll',<381>,3:36], [@65,205:207='lll',<381>,4:19], [@130,391:393='ooo',<381>,6:42]], b1=[[@33,111:113='lll',<381>,3:16], [@55,171:173='lll',<381>,3:76], [@141,425:427='qqq',<381>,7:8], [@165,496:498='qqq',<381>,9:20], [@181,542:544='qqq',<381>,10:30]]}, table_function0={index=[[@9,30:31='tf',<381>,2:24], [@153,449:451='ttt',<381>,7:32]], value=[[@13,40:41='tf',<381>,2:34], [@74,228:230='mmm',<381>,4:42]], seq=[[@5,22:23='tf',<381>,2:16], [@177,532:534='ttt',<381>,10:20]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query4={unnamed_1=[[@133,397:397=')',<288>,6:48]]}, query6={c3=[[@104,305:306='c3',<381>,5:47]], c1=[[@91,278:279='c1',<381>,5:20], [@169,505:507='rrr',<381>,9:29]], c2=[[@95,286:287='c2',<381>,5:28], [@149,441:443='rrr',<381>,7:24]]}, query7={b2=[[@147,437:438='b2',<381>,7:20]], index=[[@155,453:457='index',<96>,7:36]], b1=[[@143,429:430='b1',<381>,7:12]], c2=[[@151,445:446='c2',<381>,7:28]]}, query0={index=[[@11,33:37='index',<96>,2:27], [@153,449:451='ttt',<381>,7:32]], value=[[@15,43:47='value',<381>,2:37], [@74,228:230='mmm',<381>,4:42]], seq=[[@7,25:27='seq',<381>,2:19], [@177,532:534='ttt',<381>,10:20]]}, query1={unnamed_0=[[@77,237:237=')',<288>,4:51]]}, query3={b2=[[@39,123:124='b2',<381>,3:28], [@145,433:435='qqq',<381>,7:16]], b3=[[@48,142:143='b3',<381>,3:47], [@130,391:393='ooo',<381>,6:42]], b1=[[@35,115:116='b1',<381>,3:20], [@141,425:427='qqq',<381>,7:8], [@165,496:498='qqq',<381>,9:20], [@181,542:544='qqq',<381>,10:30]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query7={context_list={aaa=query0, bbb=query3, ccc=query6, qqq=query3, rrr=query6, ttt=query0}, query_dictionary={b2=[[@147,437:438='b2',<381>,7:20]], index=[[@155,453:457='index',<96>,7:36]], b1=[[@143,429:430='b1',<381>,7:12]], c2=[[@151,445:446='c2',<381>,7:28]]}, def_query6={context_list={aaa=query0, bbb=query3}, query_dictionary={c3=[[@104,305:306='c3',<381>,5:47]], c1=[[@91,278:279='c1',<381>,5:20], [@169,505:507='rrr',<381>,9:29]], c2=[[@95,286:287='c2',<381>,5:28], [@149,441:443='rrr',<381>,7:24]]}, table_dictionary={tab3={c3=[[@99,294:296='nnn',<381>,5:36], [@121,368:370='nnn',<381>,6:19]], c1=[[@89,274:276='nnn',<381>,5:16], [@111,334:336='nnn',<381>,5:76], [@169,505:507='rrr',<381>,9:29]], c2=[[@93,282:284='nnn',<381>,5:24], [@115,342:344='nnn',<381>,5:84], [@149,441:443='rrr',<381>,7:24]]}}, grouped_by=[{name=c1, table_ref=nnn}, {name=c2, table_ref=nnn}], dependent_queries={predicand5={query=query4, type=filters}}, filters=[{name=c3, table_ref=nnn}], interface={c3=[{name=c3, table_ref=nnn}], c1=[{name=c1, table_ref=nnn}], c2=[{name=c2, table_ref=nnn}]}, def_query4={context_list={aaa=query0, bbb=query3, ooo=query3}, query_dictionary={unnamed_1=[[@133,397:397=')',<288>,6:48]]}, table_dictionary={}, interface={unnamed_1=[{name=b3, table_ref=ooo}]}, table_alias={aaa=query0, bbb=query3, ooo=query3}}, table_alias={aaa=query0, bbb=query3, nnn=tab3}}, table_dictionary={}, def_query0={query_dictionary={index=[[@11,33:37='index',<96>,2:27], [@153,449:451='ttt',<381>,7:32]], value=[[@15,43:47='value',<381>,2:37], [@74,228:230='mmm',<381>,4:42]], seq=[[@7,25:27='seq',<381>,2:19], [@177,532:534='ttt',<381>,10:20]]}, table_dictionary={table_function0={index=[[@9,30:31='tf',<381>,2:24], [@153,449:451='ttt',<381>,7:32]], value=[[@13,40:41='tf',<381>,2:34], [@74,228:230='mmm',<381>,4:42]], seq=[[@5,22:23='tf',<381>,2:16], [@177,532:534='ttt',<381>,10:20]]}}, interface={index=[{name=index, table_ref=tf}], value=[{name=value, table_ref=tf}], seq=[{name=seq, table_ref=tf}]}, table_alias={tf=table_function0}}, filters=[{name=b1, table_ref=qqq}, {name=c1, table_ref=rrr}, {name=seq, table_ref=ttt}], interface={b2=[{name=b2, table_ref=qqq}], index=[{name=index, table_ref=ttt}], b1=[{name=b1, table_ref=qqq}], c2=[{name=c2, table_ref=rrr}]}, table_alias={aaa=query0, ccc=query6, bbb=query3}, def_query3={context_list={aaa=query0}, query_dictionary={b2=[[@39,123:124='b2',<381>,3:28], [@145,433:435='qqq',<381>,7:16]], b3=[[@48,142:143='b3',<381>,3:47], [@130,391:393='ooo',<381>,6:42]], b1=[[@35,115:116='b1',<381>,3:20], [@141,425:427='qqq',<381>,7:8], [@165,496:498='qqq',<381>,9:20], [@181,542:544='qqq',<381>,10:30]]}, table_dictionary={tab2={b2=[[@37,119:121='lll',<381>,3:24], [@59,179:181='lll',<381>,3:84], [@145,433:435='qqq',<381>,7:16]], b3=[[@43,131:133='lll',<381>,3:36], [@65,205:207='lll',<381>,4:19], [@130,391:393='ooo',<381>,6:42]], b1=[[@33,111:113='lll',<381>,3:16], [@55,171:173='lll',<381>,3:76], [@141,425:427='qqq',<381>,7:8], [@165,496:498='qqq',<381>,9:20], [@181,542:544='qqq',<381>,10:30]]}}, grouped_by=[{name=b1, table_ref=lll}, {name=b2, table_ref=lll}], def_query1={context_list={aaa=query0, mmm=query0}, query_dictionary={unnamed_0=[[@77,237:237=')',<288>,4:51]]}, table_dictionary={}, interface={unnamed_0=[{name=value, table_ref=mmm}]}, table_alias={aaa=query0, mmm=query0}}, dependent_queries={predicand2={query=query1, type=filters}}, filters=[{name=b3, table_ref=lll}], interface={b2=[{name=b2, table_ref=lll}], b3=[{name=b3, table_ref=lll}], b1=[{name=b1, table_ref=lll}]}, table_alias={aaa=query0, lll=tab2}}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void nestedWithScalarWhereAliasHeavySnowflakeTableFunctionColumnsExemplarParsesWithoutErrors() {
		final String sql = "WITH "
				+ "\n aaa AS (SELECT kkk.a1, kkk.a2, kkk.a3 FROM tab1 AS kkk),"
				+ "\n bbb AS (SELECT tf.seq, tf.index, tf.value FROM TABLE(SPLIT_TO_TABLE('71,72', ',')) tf),"
				+ "\n ccc AS (SELECT nnn.c1, nnn.c2, nnn.c3 FROM tab3 AS nnn"
				+ "\n        WHERE nnn.c3 = (SELECT max(ooo.value) FROM bbb AS ooo WHERE ooo.seq = nnn.c1))"
				+ "\n SELECT ppp.a1, qqq.index, rrr.c2"
				+ "\n FROM aaa AS ppp"
				+ "\n JOIN bbb AS qqq ON ppp.a1 = qqq.seq"
				+ "\n JOIN ccc AS rrr ON qqq.seq = rrr.c1";

		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runParsertest(sql, parser);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong", "{SQL={with={1={cte={select={1={column={name=a1, table_ref=kkk}}, 2={column={name=a2, table_ref=kkk}}, 3={column={name=a3, table_ref=kkk}}}, from={table={alias=kkk, table=tab1}}}, alias=aaa}, 2={cte={select={1={column={name=seq, table_ref=tf}}, 2={column={name=index, table_ref=tf}}, 3={column={name=value, table_ref=tf}}}, from={table={alias=tf, table_function={function={function_name=SPLIT_TO_TABLE, parameters={1={literal='71,72'}, 2={literal=','}}}}}}}, alias=bbb}, 3={cte={select={1={column={name=c1, table_ref=nnn}}, 2={column={name=c2, table_ref=nnn}}, 3={column={name=c3, table_ref=nnn}}}, from={table={alias=nnn, table=tab3}}, where={condition={left={column={name=c3, table_ref=nnn}}, right={select={1={function={function_name=max, qualifier=null, parameters={column={name=value, table_ref=ooo}}}}}, from={table={alias=ooo, table=bbb}}, where={condition={left={column={name=seq, table_ref=ooo}}, right={column={name=c1, table_ref=nnn}}, operator==}}}, operator==}}}, alias=ccc}}, query={select={1={column={name=a1, table_ref=ppp}}, 2={column={name=index, table_ref=qqq}}, 3={column={name=c2, table_ref=rrr}}}, from={join={1={table={alias=ppp, table=aaa}}, 2={join=JOIN, on={condition={left={column={name=a1, table_ref=ppp}}, right={column={name=seq, table_ref=qqq}}, operator==}}}, 3={table={alias=qqq, table=bbb}}, 4={join=JOIN, on={condition={left={column={name=seq, table_ref=qqq}}, right={column={name=c1, table_ref=rrr}}, operator==}}}, 5={table={alias=rrr, table=ccc}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[a1, index, c2]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab3={c3=[[@62,185:187='nnn',<381>,4:32], [@70,223:225='nnn',<381>,5:14]], c1=[[@54,169:171='nnn',<381>,4:16], [@91,287:289='nnn',<381>,5:78], [@133,414:416='rrr',<381>,9:30]], c2=[[@58,177:179='nnn',<381>,4:24], [@105,323:325='rrr',<381>,6:27]]}, tab1={a1=[[@5,22:24='kkk',<381>,2:16], [@97,304:306='ppp',<381>,6:8], [@117,367:369='ppp',<381>,8:20]], a2=[[@9,30:32='kkk',<381>,2:24]], a3=[[@13,38:40='kkk',<381>,2:32]]}, table_function0={index=[[@30,88:89='tf',<381>,3:24], [@101,312:314='qqq',<381>,6:16]], value=[[@34,98:99='tf',<381>,3:34], [@78,244:246='ooo',<381>,5:35]], seq=[[@26,80:81='tf',<381>,3:16], [@87,277:279='ooo',<381>,5:68], [@121,376:378='qqq',<381>,8:29], [@129,404:406='qqq',<381>,9:20]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query4={c3=[[@64,189:190='c3',<381>,4:36]], c1=[[@56,173:174='c1',<381>,4:20], [@133,414:416='rrr',<381>,9:30]], c2=[[@60,181:182='c2',<381>,4:28], [@105,323:325='rrr',<381>,6:27]]}, query5={a1=[[@99,308:309='a1',<381>,6:12]], index=[[@103,316:320='index',<96>,6:20]], c2=[[@107,327:328='c2',<381>,6:31]]}, query0={a1=[[@7,26:27='a1',<381>,2:20], [@97,304:306='ppp',<381>,6:8], [@117,367:369='ppp',<381>,8:20]], a2=[[@11,34:35='a2',<381>,2:28]], a3=[[@15,42:43='a3',<381>,2:36]]}, query1={index=[[@32,91:95='index',<96>,3:27], [@101,312:314='qqq',<381>,6:16]], value=[[@36,101:105='value',<381>,3:37], [@78,244:246='ooo',<381>,5:35]], seq=[[@28,83:85='seq',<381>,3:19], [@87,277:279='ooo',<381>,5:68], [@121,376:378='qqq',<381>,8:29], [@129,404:406='qqq',<381>,9:20]]}, query2={unnamed_0=[[@81,253:253=')',<288>,5:44]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query5={context_list={aaa=query0, bbb=query1, ccc=query4, ppp=query0, qqq=query1, rrr=query4}, query_dictionary={a1=[[@99,308:309='a1',<381>,6:12]], index=[[@103,316:320='index',<96>,6:20]], c2=[[@107,327:328='c2',<381>,6:31]]}, table_dictionary={}, def_query1={context_list={aaa=query0}, query_dictionary={index=[[@32,91:95='index',<96>,3:27], [@101,312:314='qqq',<381>,6:16]], value=[[@36,101:105='value',<381>,3:37], [@78,244:246='ooo',<381>,5:35]], seq=[[@28,83:85='seq',<381>,3:19], [@87,277:279='ooo',<381>,5:68], [@121,376:378='qqq',<381>,8:29], [@129,404:406='qqq',<381>,9:20]]}, table_dictionary={table_function0={index=[[@30,88:89='tf',<381>,3:24], [@101,312:314='qqq',<381>,6:16]], value=[[@34,98:99='tf',<381>,3:34], [@78,244:246='ooo',<381>,5:35]], seq=[[@26,80:81='tf',<381>,3:16], [@87,277:279='ooo',<381>,5:68], [@121,376:378='qqq',<381>,8:29], [@129,404:406='qqq',<381>,9:20]]}}, interface={index=[{name=index, table_ref=tf}], value=[{name=value, table_ref=tf}], seq=[{name=seq, table_ref=tf}]}, table_alias={aaa=query0, tf=table_function0}}, def_query0={query_dictionary={a1=[[@7,26:27='a1',<381>,2:20], [@97,304:306='ppp',<381>,6:8], [@117,367:369='ppp',<381>,8:20]], a2=[[@11,34:35='a2',<381>,2:28]], a3=[[@15,42:43='a3',<381>,2:36]]}, table_dictionary={tab1={a1=[[@5,22:24='kkk',<381>,2:16], [@97,304:306='ppp',<381>,6:8], [@117,367:369='ppp',<381>,8:20]], a2=[[@9,30:32='kkk',<381>,2:24]], a3=[[@13,38:40='kkk',<381>,2:32]]}}, interface={a1=[{name=a1, table_ref=kkk}], a2=[{name=a2, table_ref=kkk}], a3=[{name=a3, table_ref=kkk}]}, table_alias={kkk=tab1}}, filters=[{name=a1, table_ref=ppp}, {name=seq, table_ref=qqq}, {name=c1, table_ref=rrr}], interface={a1=[{name=a1, table_ref=ppp}], index=[{name=index, table_ref=qqq}], c2=[{name=c2, table_ref=rrr}]}, def_query4={context_list={aaa=query0, bbb=query1}, query_dictionary={c3=[[@64,189:190='c3',<381>,4:36]], c1=[[@56,173:174='c1',<381>,4:20], [@133,414:416='rrr',<381>,9:30]], c2=[[@60,181:182='c2',<381>,4:28], [@105,323:325='rrr',<381>,6:27]]}, table_dictionary={tab3={c3=[[@62,185:187='nnn',<381>,4:32], [@70,223:225='nnn',<381>,5:14]], c1=[[@54,169:171='nnn',<381>,4:16], [@91,287:289='nnn',<381>,5:78], [@133,414:416='rrr',<381>,9:30]], c2=[[@58,177:179='nnn',<381>,4:24], [@105,323:325='rrr',<381>,6:27]]}}, dependent_queries={predicand3={query=query2, type=filters}}, filters=[{name=c3, table_ref=nnn}], interface={c3=[{name=c3, table_ref=nnn}], c1=[{name=c1, table_ref=nnn}], c2=[{name=c2, table_ref=nnn}]}, table_alias={aaa=query0, bbb=query1, nnn=tab3}, def_query2={context_list={aaa=query0, bbb=query1, ooo=query1}, query_dictionary={unnamed_0=[[@81,253:253=')',<288>,5:44]]}, table_dictionary={}, filters=[{name=seq, table_ref=ooo}, {name=c1, table_ref=nnn}], interface={unnamed_0=[{name=value, table_ref=ooo}]}, table_alias={aaa=query0, bbb=query1, ooo=query1}}}, table_alias={aaa=query0, ccc=query4, bbb=query1}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void nestedWithScalarSelectListAliasHeavySnowflakeTableFunctionColumnsExemplarParsesWithoutErrors() {
		final String sql = "WITH "
				+ "\n aaa AS (SELECT tf.seq, tf.index, tf.value FROM TABLE(SPLIT_TO_TABLE('301,302', ',')) tf),"
				+ "\n bbb AS (SELECT lll.b1, lll.b2, (SELECT max(mmm.value) FROM aaa AS mmm) AS b3 FROM tab2 AS lll),"
				+ "\n ccc AS (SELECT tf.seq, tf.index, tf.value FROM TABLE(SPLIT_TO_TABLE('401,402', ',')) tf)"
				+ "\n SELECT ppp.seq, qqq.b2, rrr.index"
				+ "\n FROM aaa AS ppp"
				+ "\n JOIN bbb AS qqq ON ppp.seq = qqq.b1"
				+ "\n JOIN ccc AS rrr ON qqq.b1 = rrr.seq";

		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runParsertest(sql, parser);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong", "{SQL={with={1={cte={select={1={column={name=seq, table_ref=tf}}, 2={column={name=index, table_ref=tf}}, 3={column={name=value, table_ref=tf}}}, from={table={alias=tf, table_function={function={function_name=SPLIT_TO_TABLE, parameters={1={literal='301,302'}, 2={literal=','}}}}}}}, alias=aaa}, 2={cte={select={1={column={name=b1, table_ref=lll}}, 2={column={name=b2, table_ref=lll}}, 3={lookup={from={table={alias=mmm, table=aaa}}, select={1={function={function_name=max, qualifier=null, parameters={column={name=value, table_ref=mmm}}}}}}, alias=b3}}, from={table={alias=lll, table=tab2}}}, alias=bbb}, 3={cte={select={1={column={name=seq, table_ref=tf}}, 2={column={name=index, table_ref=tf}}, 3={column={name=value, table_ref=tf}}}, from={table={alias=tf, table_function={function={function_name=SPLIT_TO_TABLE, parameters={1={literal='401,402'}, 2={literal=','}}}}}}}, alias=ccc}}, query={select={1={column={name=seq, table_ref=ppp}}, 2={column={name=b2, table_ref=qqq}}, 3={column={name=index, table_ref=rrr}}}, from={join={1={table={alias=ppp, table=aaa}}, 2={join=JOIN, on={condition={left={column={name=seq, table_ref=ppp}}, right={column={name=b1, table_ref=qqq}}, operator==}}}, 3={table={alias=qqq, table=bbb}}, 4={join=JOIN, on={condition={left={column={name=b1, table_ref=qqq}}, right={column={name=seq, table_ref=rrr}}, operator==}}}, 5={table={alias=rrr, table=ccc}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[b2, index, seq]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{table_function1={index=[[@70,218:219='tf',<381>,4:24], [@98,309:311='rrr',<381>,5:25]], value=[[@74,228:229='tf',<381>,4:34]], seq=[[@66,210:211='tf',<381>,4:16], [@126,402:404='rrr',<381>,8:29]]}, tab2={b2=[[@37,121:123='lll',<381>,3:24], [@94,301:303='qqq',<381>,5:17]], b1=[[@33,113:115='lll',<381>,3:16], [@114,366:368='qqq',<381>,7:30], [@122,393:395='qqq',<381>,8:20]]}, table_function0={index=[[@9,30:31='tf',<381>,2:24]], value=[[@13,40:41='tf',<381>,2:34], [@45,141:143='mmm',<381>,3:44]], seq=[[@5,22:23='tf',<381>,2:16], [@90,292:294='ppp',<381>,5:8], [@110,356:358='ppp',<381>,7:20]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query4={index=[[@72,221:225='index',<96>,4:27], [@98,309:311='rrr',<381>,5:25]], value=[[@76,231:235='value',<381>,4:37]], seq=[[@68,213:215='seq',<381>,4:19], [@126,402:404='rrr',<381>,8:29]]}, query5={b2=[[@96,305:306='b2',<381>,5:21]], index=[[@100,313:317='index',<96>,5:29]], seq=[[@92,296:298='seq',<381>,5:12]]}, query0={index=[[@11,33:37='index',<96>,2:27]], value=[[@15,43:47='value',<381>,2:37], [@45,141:143='mmm',<381>,3:44]], seq=[[@7,25:27='seq',<381>,2:19], [@90,292:294='ppp',<381>,5:8], [@110,356:358='ppp',<381>,7:20]]}, query1={unnamed_0=[[@48,150:150=')',<288>,3:53]]}, query3={b2=[[@39,125:126='b2',<381>,3:28], [@94,301:303='qqq',<381>,5:17]], b3=[[@55,172:173='b3',<381>,3:75]], b1=[[@35,117:118='b1',<381>,3:20], [@114,366:368='qqq',<381>,7:30], [@122,393:395='qqq',<381>,8:20]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query5={context_list={aaa=query0, bbb=query3, ccc=query4, ppp=query0, qqq=query3, rrr=query4}, query_dictionary={b2=[[@96,305:306='b2',<381>,5:21]], index=[[@100,313:317='index',<96>,5:29]], seq=[[@92,296:298='seq',<381>,5:12]]}, table_dictionary={}, def_query0={query_dictionary={index=[[@11,33:37='index',<96>,2:27]], value=[[@15,43:47='value',<381>,2:37], [@45,141:143='mmm',<381>,3:44]], seq=[[@7,25:27='seq',<381>,2:19], [@90,292:294='ppp',<381>,5:8], [@110,356:358='ppp',<381>,7:20]]}, table_dictionary={table_function0={index=[[@9,30:31='tf',<381>,2:24]], value=[[@13,40:41='tf',<381>,2:34], [@45,141:143='mmm',<381>,3:44]], seq=[[@5,22:23='tf',<381>,2:16], [@90,292:294='ppp',<381>,5:8], [@110,356:358='ppp',<381>,7:20]]}}, interface={index=[{name=index, table_ref=tf}], value=[{name=value, table_ref=tf}], seq=[{name=seq, table_ref=tf}]}, table_alias={tf=table_function0}}, filters=[{name=seq, table_ref=ppp}, {name=b1, table_ref=qqq}, {name=seq, table_ref=rrr}], interface={b2=[{name=b2, table_ref=qqq}], index=[{name=index, table_ref=rrr}], seq=[{name=seq, table_ref=ppp}]}, def_query4={context_list={aaa=query0, bbb=query3}, query_dictionary={index=[[@72,221:225='index',<96>,4:27], [@98,309:311='rrr',<381>,5:25]], value=[[@76,231:235='value',<381>,4:37]], seq=[[@68,213:215='seq',<381>,4:19], [@126,402:404='rrr',<381>,8:29]]}, table_dictionary={table_function1={index=[[@70,218:219='tf',<381>,4:24], [@98,309:311='rrr',<381>,5:25]], value=[[@74,228:229='tf',<381>,4:34]], seq=[[@66,210:211='tf',<381>,4:16], [@126,402:404='rrr',<381>,8:29]]}}, interface={index=[{name=index, table_ref=tf}], value=[{name=value, table_ref=tf}], seq=[{name=seq, table_ref=tf}]}, table_alias={aaa=query0, tf=table_function1, bbb=query3}}, table_alias={aaa=query0, ccc=query4, bbb=query3}, def_query3={context_list={aaa=query0}, query_dictionary={b2=[[@39,125:126='b2',<381>,3:28], [@94,301:303='qqq',<381>,5:17]], b3=[[@55,172:173='b3',<381>,3:75]], b1=[[@35,117:118='b1',<381>,3:20], [@114,366:368='qqq',<381>,7:30], [@122,393:395='qqq',<381>,8:20]]}, table_dictionary={tab2={b2=[[@37,121:123='lll',<381>,3:24], [@94,301:303='qqq',<381>,5:17]], b1=[[@33,113:115='lll',<381>,3:16], [@114,366:368='qqq',<381>,7:30], [@122,393:395='qqq',<381>,8:20]]}}, def_query1={context_list={aaa=query0, mmm=query0}, query_dictionary={unnamed_0=[[@48,150:150=')',<288>,3:53]]}, table_dictionary={}, interface={unnamed_0=[{name=value, table_ref=mmm}]}, table_alias={aaa=query0, mmm=query0}}, dependent_queries={predicand2={query=query1, type=interface}}, interface={b2=[{name=b2, table_ref=lll}], b3=[{name=value, table_ref=mmm}], b1=[{name=b1, table_ref=lll}]}, table_alias={aaa=query0, lll=tab2}}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void nestedWithExistsAliasHeavyValuesCteVirtualPositionRefsExemplarParsesWithoutErrors() {
		final String sql = "WITH "
				+ "\n aaa AS ((VALUES (11, 21, 31), (12, 22, 32))),"
				+ "\n bbb_ccc AS ("
				+ "\n   WITH "
				+ "\n     bbb AS (SELECT lll.b1, lll.b2, lll.b3 FROM tab2 AS lll WHERE EXISTS (SELECT 1 FROM aaa AS mmm WHERE $3 = lll.b3)),"
				+ "\n     ccc AS (SELECT nnn.c1, nnn.c2, nnn.c3 FROM tab3 AS nnn WHERE EXISTS (SELECT 1 FROM bbb AS ooo WHERE ooo.b3 = nnn.c3))"
				+ "\n   SELECT ppp.b1 AS b1, qqq.c1 AS c1, qqq.c2 AS c2"
				+ "\n   FROM bbb AS ppp JOIN ccc AS qqq ON ppp.b1 = qqq.c1"
				+ "\n )"
				+ "\n SELECT sss.b1, sss.c2, (SELECT max($2) FROM aaa AS ttt) AS a2_virtual"
				+ "\n FROM bbb_ccc AS sss";

		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runParsertest(sql, parser);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong", "{SQL={with={1={cte={values={matrix={1={row={1={literal=11}, 2={literal=21}, 3={literal=31}}}, 2={row={1={literal=12}, 2={literal=22}, 3={literal=32}}}}}}, alias=aaa}, 2={cte={with={1={cte={select={1={column={name=b1, table_ref=lll}}, 2={column={name=b2, table_ref=lll}}, 3={column={name=b3, table_ref=lll}}}, from={table={alias=lll, table=tab2}}, where={exists={select={1={literal=1}}, from={table={alias=mmm, table=aaa}}, where={condition={left={literal=3}, right={column={name=b3, table_ref=lll}}, operator==}}, operator=EXISTS}}}, alias=bbb}, 2={cte={select={1={column={name=c1, table_ref=nnn}}, 2={column={name=c2, table_ref=nnn}}, 3={column={name=c3, table_ref=nnn}}}, from={table={alias=nnn, table=tab3}}, where={exists={select={1={literal=1}}, from={table={alias=ooo, table=bbb}}, where={condition={left={column={name=b3, table_ref=ooo}}, right={column={name=c3, table_ref=nnn}}, operator==}}, operator=EXISTS}}}, alias=ccc}}, query={select={1={column={name=b1, table_ref=ppp}, alias=b1}, 2={column={name=c1, table_ref=qqq}, alias=c1}, 3={column={name=c2, table_ref=qqq}, alias=c2}}, from={join={1={table={alias=ppp, table=bbb}}, 2={join=JOIN, on={condition={left={column={name=b1, table_ref=ppp}}, right={column={name=c1, table_ref=qqq}}, operator==}}}, 3={table={alias=qqq, table=ccc}}}}}}, alias=bbb_ccc}}, query={select={1={column={name=b1, table_ref=sss}}, 2={column={name=c2, table_ref=sss}}, 3={lookup={from={table={alias=ttt, table=aaa}}, select={1={function={function_name=max, qualifier=null, parameters={literal=2}}}}}, alias=a2_virtual}}, from={table={alias=sss, table=bbb_ccc}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[a2_virtual, b1, c2]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab3={c3=[[@77,232:234='nnn',<381>,6:36], [@98,310:312='nnn',<381>,6:114]], c1=[[@69,216:218='nnn',<381>,6:20], [@110,343:345='qqq',<381>,7:24], [@134,417:419='qqq',<381>,8:47]], c2=[[@73,224:226='nnn',<381>,6:28], [@116,357:359='qqq',<381>,7:38]]}, tab2={b2=[[@36,104:106='lll',<381>,5:28]], b3=[[@40,112:114='lll',<381>,5:36], [@59,186:188='lll',<381>,5:110], [@94,301:303='ooo',<381>,6:105]], b1=[[@32,96:98='lll',<381>,5:20], [@104,329:331='ppp',<381>,7:10], [@130,408:410='ppp',<381>,8:38]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{values0={$1=[[@6,23:23='(',<287>,2:17], [@14,37:37='(',<287>,2:31]], $2=[[@6,23:23='(',<287>,2:17], [@14,37:37='(',<287>,2:31]], $3=[[@6,23:23='(',<287>,2:17], [@14,37:37='(',<287>,2:31]]}, query8={unnamed_2=[[@152,465:465=')',<288>,10:38]]}, query4={unnamed_1=[[@88,277:277='1',<300>,6:81]]}, query6={c3=[[@79,236:237='c3',<381>,6:40]], c1=[[@71,220:221='c1',<381>,6:24], [@110,343:345='qqq',<381>,7:24], [@134,417:419='qqq',<381>,8:47]], c2=[[@75,228:229='c2',<381>,6:32], [@116,357:359='qqq',<381>,7:38]]}, query7={c1=[[@114,353:354='c1',<381>,7:34]], b1=[[@108,339:340='b1',<381>,7:20], [@139,435:437='sss',<381>,10:8]], c2=[[@120,367:368='c2',<381>,7:48], [@143,443:445='sss',<381>,10:16]]}, query10={a2_virtual=[[@159,487:496='a2_virtual',<381>,10:60]], b1=[[@141,439:440='b1',<381>,10:12]], c2=[[@145,447:448='c2',<381>,10:20]]}, query1={unnamed_0=[[@51,157:157='1',<300>,5:81]]}, query3={b2=[[@38,108:109='b2',<381>,5:32]], b3=[[@42,116:117='b3',<381>,5:40], [@94,301:303='ooo',<381>,6:105]], b1=[[@34,100:101='b1',<381>,5:24], [@104,329:331='ppp',<381>,7:10], [@130,408:410='ppp',<381>,8:38]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query10={context_list={aaa=values0, bbb_ccc=query7, sss=query7}, def_query8={context_list={aaa=values0, bbb_ccc=query7, ttt=values0}, query_dictionary={unnamed_2=[[@152,465:465=')',<288>,10:38]]}, table_dictionary={}, interface={unnamed_2=[]}, table_alias={aaa=values0, ttt=values0, bbb_ccc=query7}}, def_query7={context_list={aaa=values0, bbb=query3, ccc=query6, ppp=query3, qqq=query6}, def_query6={context_list={aaa=values0, bbb=query3}, query_dictionary={c3=[[@79,236:237='c3',<381>,6:40]], c1=[[@71,220:221='c1',<381>,6:24], [@110,343:345='qqq',<381>,7:24], [@134,417:419='qqq',<381>,8:47]], c2=[[@75,228:229='c2',<381>,6:32], [@116,357:359='qqq',<381>,7:38]]}, table_dictionary={tab3={c3=[[@77,232:234='nnn',<381>,6:36], [@98,310:312='nnn',<381>,6:114]], c1=[[@69,216:218='nnn',<381>,6:20], [@110,343:345='qqq',<381>,7:24], [@134,417:419='qqq',<381>,8:47]], c2=[[@73,224:226='nnn',<381>,6:28], [@116,357:359='qqq',<381>,7:38]]}}, dependent_queries={exists5={query=query4, type=filters}}, filters=[], interface={c3=[{name=c3, table_ref=nnn}], c1=[{name=c1, table_ref=nnn}], c2=[{name=c2, table_ref=nnn}]}, def_query4={context_list={aaa=values0, bbb=query3, ooo=query3}, query_dictionary={unnamed_1=[[@88,277:277='1',<300>,6:81]]}, table_dictionary={}, filters=[{name=b3, table_ref=ooo}, {name=c3, table_ref=nnn}], interface={unnamed_1=[]}, table_alias={aaa=values0, bbb=query3, ooo=query3}}, table_alias={aaa=values0, bbb=query3, nnn=tab3}}, table_dictionary={}, def_values0={query_dictionary={$1=[[@6,23:23='(',<287>,2:17], [@14,37:37='(',<287>,2:31]], $2=[[@6,23:23='(',<287>,2:17], [@14,37:37='(',<287>,2:31]], $3=[[@6,23:23='(',<287>,2:17], [@14,37:37='(',<287>,2:31]]}, table_dictionary={}, interface={$1=[], $2=[], $3=[]}}, filters=[{name=b1, table_ref=ppp}, {name=c1, table_ref=qqq}], interface={c1=[{name=c1, table_ref=qqq}], b1=[{name=b1, table_ref=ppp}], c2=[{name=c2, table_ref=qqq}]}, def_query3={context_list={aaa=values0}, query_dictionary={b2=[[@38,108:109='b2',<381>,5:32]], b3=[[@42,116:117='b3',<381>,5:40], [@94,301:303='ooo',<381>,6:105]], b1=[[@34,100:101='b1',<381>,5:24], [@104,329:331='ppp',<381>,7:10], [@130,408:410='ppp',<381>,8:38]]}, table_dictionary={tab2={b2=[[@36,104:106='lll',<381>,5:28]], b3=[[@40,112:114='lll',<381>,5:36], [@59,186:188='lll',<381>,5:110], [@94,301:303='ooo',<381>,6:105]], b1=[[@32,96:98='lll',<381>,5:20], [@104,329:331='ppp',<381>,7:10], [@130,408:410='ppp',<381>,8:38]]}}, def_query1={context_list={aaa=values0, mmm=values0}, query_dictionary={unnamed_0=[[@51,157:157='1',<300>,5:81]]}, table_dictionary={}, filters=[{name=b3, table_ref=lll}], interface={unnamed_0=[]}, table_alias={aaa=values0, mmm=values0}}, dependent_queries={exists2={query=query1, type=filters}}, filters=[], interface={b2=[{name=b2, table_ref=lll}], b3=[{name=b3, table_ref=lll}], b1=[{name=b1, table_ref=lll}]}, table_alias={aaa=values0, lll=tab2}}, query_dictionary={c1=[[@114,353:354='c1',<381>,7:34]], b1=[[@108,339:340='b1',<381>,7:20], [@139,435:437='sss',<381>,10:8]], c2=[[@120,367:368='c2',<381>,7:48], [@143,443:445='sss',<381>,10:16]]}, table_alias={aaa=values0, ccc=query6, bbb=query3}}, query_dictionary={a2_virtual=[[@159,487:496='a2_virtual',<381>,10:60]], b1=[[@141,439:440='b1',<381>,10:12]], c2=[[@145,447:448='c2',<381>,10:20]]}, table_dictionary={}, def_values0={query_dictionary={$1=[[@6,23:23='(',<287>,2:17], [@14,37:37='(',<287>,2:31]], $2=[[@6,23:23='(',<287>,2:17], [@14,37:37='(',<287>,2:31]], $3=[[@6,23:23='(',<287>,2:17], [@14,37:37='(',<287>,2:31]]}, table_dictionary={}, interface={$1=[], $2=[], $3=[]}}, dependent_queries={predicand9={query=query8, type=interface}}, interface={a2_virtual=[], b1=[{name=b1, table_ref=sss}], c2=[{name=c2, table_ref=sss}]}, table_alias={aaa=values0, bbb_ccc=query7}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void nestedWithUnionAliasHeavyValuesCteVirtualPositionRefsExemplarParsesWithoutErrors() {
		final String sql = "WITH "
				+ "\n aaa AS (SELECT kkk.a1, kkk.a2, kkk.a3 FROM tab1 AS kkk),"
				+ "\n bbb AS ((VALUES (101, 201, 301), (102, 202, 302))),"
				+ "\n ccc AS (SELECT nnn.c1, nnn.c2, nnn.c3 FROM tab3 AS nnn UNION ALL SELECT $1 AS c1, $2 AS c2, $3 AS c3 FROM bbb AS ooo)"
				+ "\n SELECT ppp.a1, rrr.c2, (SELECT max($2) FROM bbb AS sss) AS b2_virtual"
				+ "\n FROM aaa AS ppp"
				+ "\n JOIN ccc AS rrr ON ppp.a1 = rrr.c1";

		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runParsertest(sql, parser);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong", "{SQL={with={1={cte={select={1={column={name=a1, table_ref=kkk}}, 2={column={name=a2, table_ref=kkk}}, 3={column={name=a3, table_ref=kkk}}}, from={table={alias=kkk, table=tab1}}}, alias=aaa}, 2={cte={values={matrix={1={row={1={literal=101}, 2={literal=201}, 3={literal=301}}}, 2={row={1={literal=102}, 2={literal=202}, 3={literal=302}}}}}}, alias=bbb}, 3={cte={union={1={select={1={column={name=c1, table_ref=nnn}}, 2={column={name=c2, table_ref=nnn}}, 3={column={name=c3, table_ref=nnn}}}, from={table={alias=nnn, table=tab3}}}, 2={union={qualifier=ALL, operator=UNION}}, 3={select={1={alias=c1, literal=1}, 2={alias=c2, literal=2}, 3={alias=c3, literal=3}}, from={table={alias=ooo, table=bbb}}}}}, alias=ccc}}, query={select={1={column={name=a1, table_ref=ppp}}, 2={column={name=c2, table_ref=rrr}}, 3={lookup={from={table={alias=sss, table=bbb}}, select={1={function={function_name=max, qualifier=null, parameters={literal=2}}}}}, alias=b2_virtual}}, from={join={1={table={alias=ppp, table=aaa}}, 2={join=JOIN, on={condition={left={column={name=a1, table_ref=ppp}}, right={column={name=c1, table_ref=rrr}}, operator==}}}, 3={table={alias=rrr, table=ccc}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[a1, c2, b2_virtual]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab3={c3=[[@57,149:151='nnn',<381>,4:32]], c1=[[@49,133:135='nnn',<381>,4:16]], c2=[[@53,141:143='nnn',<381>,4:24]]}, tab1={a1=[[@5,22:24='kkk',<381>,2:16], [@84,244:246='ppp',<381>,5:8], [@114,344:346='ppp',<381>,7:20]], a2=[[@9,30:32='kkk',<381>,2:24]], a3=[[@13,38:40='kkk',<381>,2:32]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{values1={$1=[[@27,81:81='(',<287>,3:17], [@35,98:98='(',<287>,3:34]], $2=[[@27,81:81='(',<287>,3:17], [@35,98:98='(',<287>,3:34]], $3=[[@27,81:81='(',<287>,3:17], [@35,98:98='(',<287>,3:34]]}, query5={unnamed_0=[[@97,274:274=')',<288>,5:38]]}, query7={a1=[[@86,248:249='a1',<381>,5:12]], c2=[[@90,256:257='c2',<381>,5:20]], b2_virtual=[[@104,296:305='b2_virtual',<381>,5:60]]}, query0={a1=[[@7,26:27='a1',<381>,2:20], [@84,244:246='ppp',<381>,5:8], [@114,344:346='ppp',<381>,7:20]], a2=[[@11,34:35='a2',<381>,2:28]], a3=[[@15,42:43='a3',<381>,2:36]]}, query2={c3=[[@59,153:154='c3',<381>,4:36]], c1=[[@51,137:138='c1',<381>,4:20]], c2=[[@55,145:146='c2',<381>,4:28]]}, query3={c3=[[@77,216:217='c3',<381>,4:99]], c1=[[@69,196:197='c1',<381>,4:79]], c2=[[@73,206:207='c2',<381>,4:89]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query7={context_list={aaa=query0, bbb=values1, ccc=union4, ppp=query0, rrr=union4}, def_union4={context_list={aaa=query0, bbb=values1}, interface={c3=[{name=c3, table_ref=nnn}], c1=[{name=c1, table_ref=nnn}], c2=[{name=c2, table_ref=nnn}]}, table_alias={aaa=query0, bbb=values1}, query2={context_list={aaa=query0, bbb=values1}, query_dictionary={c3=[[@59,153:154='c3',<381>,4:36]], c1=[[@51,137:138='c1',<381>,4:20]], c2=[[@55,145:146='c2',<381>,4:28]]}, table_dictionary={tab3={c3=[[@57,149:151='nnn',<381>,4:32]], c1=[[@49,133:135='nnn',<381>,4:16]], c2=[[@53,141:143='nnn',<381>,4:24]]}}, interface={c3=[{name=c3, table_ref=nnn}], c1=[{name=c1, table_ref=nnn}], c2=[{name=c2, table_ref=nnn}]}, table_alias={aaa=query0, bbb=values1, nnn=tab3}}, query3={context_list={aaa=query0, bbb=values1, ooo=values1}, query_dictionary={c3=[[@77,216:217='c3',<381>,4:99]], c1=[[@69,196:197='c1',<381>,4:79]], c2=[[@73,206:207='c2',<381>,4:89]]}, table_dictionary={}, interface={c3=[], c1=[], c2=[]}, table_alias={aaa=query0, bbb=values1, ooo=values1}}}, table_dictionary={}, def_values1={query_dictionary={$1=[[@27,81:81='(',<287>,3:17], [@35,98:98='(',<287>,3:34]], $2=[[@27,81:81='(',<287>,3:17], [@35,98:98='(',<287>,3:34]], $3=[[@27,81:81='(',<287>,3:17], [@35,98:98='(',<287>,3:34]]}, table_dictionary={}, interface={$1=[], $2=[], $3=[]}}, def_query0={query_dictionary={a1=[[@7,26:27='a1',<381>,2:20], [@84,244:246='ppp',<381>,5:8], [@114,344:346='ppp',<381>,7:20]], a2=[[@11,34:35='a2',<381>,2:28]], a3=[[@15,42:43='a3',<381>,2:36]]}, table_dictionary={tab1={a1=[[@5,22:24='kkk',<381>,2:16], [@84,244:246='ppp',<381>,5:8], [@114,344:346='ppp',<381>,7:20]], a2=[[@9,30:32='kkk',<381>,2:24]], a3=[[@13,38:40='kkk',<381>,2:32]]}}, interface={a1=[{name=a1, table_ref=kkk}], a2=[{name=a2, table_ref=kkk}], a3=[{name=a3, table_ref=kkk}]}, table_alias={kkk=tab1}}, filters=[{name=a1, table_ref=ppp}, {name=c1, table_ref=rrr}], def_query5={context_list={aaa=query0, bbb=values1, ccc=union4, sss=values1}, query_dictionary={unnamed_0=[[@97,274:274=')',<288>,5:38]]}, table_dictionary={}, interface={unnamed_0=[]}, table_alias={aaa=query0, ccc=union4, sss=values1, bbb=values1}}, interface={a1=[{name=a1, table_ref=ppp}], c2=[{name=c2, table_ref=rrr}], b2_virtual=[]}, query_dictionary={a1=[[@86,248:249='a1',<381>,5:12]], c2=[[@90,256:257='c2',<381>,5:20]], b2_virtual=[[@104,296:305='b2_virtual',<381>,5:60]]}, dependent_queries={predicand6={query=query5, type=interface}}, table_alias={aaa=query0, ccc=union4, bbb=values1}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void nestedWithIntersectAliasHeavyValuesCteVirtualPositionRefsExemplarParsesWithoutErrors() {
		final String sql = "WITH "
				+ "\n aaa AS (SELECT kkk.a1, kkk.a2, kkk.a3 FROM tab1 AS kkk),"
				+ "\n bbb AS (SELECT lll.b1, lll.b2, lll.b3 FROM tab2 AS lll INTERSECT DISTINCT SELECT mmm.a1 AS b1, mmm.a2 AS b2, mmm.a3 AS b3 FROM aaa AS mmm),"
				+ "\n ccc AS ((VALUES (1001, 2001, 3001), (1002, 2002, 3002)))"
				+ "\n SELECT ppp.a1, qqq.b2"
				+ "\n FROM aaa AS ppp"
				+ "\n JOIN bbb AS qqq ON ppp.a1 = qqq.b1"
				+ "\n JOIN ccc AS rrr ON qqq.b1 = $1";

		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runParsertest(sql, parser);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong", "{SQL={with={1={cte={select={1={column={name=a1, table_ref=kkk}}, 2={column={name=a2, table_ref=kkk}}, 3={column={name=a3, table_ref=kkk}}}, from={table={alias=kkk, table=tab1}}}, alias=aaa}, 2={cte={intersect={1={select={1={column={name=b1, table_ref=lll}}, 2={column={name=b2, table_ref=lll}}, 3={column={name=b3, table_ref=lll}}}, from={table={alias=lll, table=tab2}}}, 2={intersect={qualifier=DISTINCT, operator=INTERSECT}}, 3={select={1={column={name=a1, table_ref=mmm}, alias=b1}, 2={column={name=a2, table_ref=mmm}, alias=b2}, 3={column={name=a3, table_ref=mmm}, alias=b3}}, from={table={alias=mmm, table=aaa}}}}}, alias=bbb}, 3={cte={values={matrix={1={row={1={literal=1001}, 2={literal=2001}, 3={literal=3001}}}, 2={row={1={literal=1002}, 2={literal=2002}, 3={literal=3002}}}}}}, alias=ccc}}, query={select={1={column={name=a1, table_ref=ppp}}, 2={column={name=b2, table_ref=qqq}}}, from={join={1={table={alias=ppp, table=aaa}}, 2={join=JOIN, on={condition={left={column={name=a1, table_ref=ppp}}, right={column={name=b1, table_ref=qqq}}, operator==}}}, 3={table={alias=qqq, table=bbb}}, 4={join=JOIN, on={condition={left={column={name=b1, table_ref=qqq}}, right={literal=1}, operator==}}}, 5={table={alias=rrr, table=ccc}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[a1, b2]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={a1=[[@5,22:24='kkk',<381>,2:16], [@44,146:148='mmm',<381>,3:82], [@90,271:273='ppp',<381>,5:8], [@106,323:325='ppp',<381>,7:20]], a2=[[@9,30:32='kkk',<381>,2:24], [@50,160:162='mmm',<381>,3:96]], a3=[[@13,38:40='kkk',<381>,2:32], [@56,174:176='mmm',<381>,3:110]]}, tab2={b2=[[@30,88:90='lll',<381>,3:24]], b3=[[@34,96:98='lll',<381>,3:32]], b1=[[@26,80:82='lll',<381>,3:16]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query5={a1=[[@92,275:276='a1',<381>,5:12]], b2=[[@96,283:284='b2',<381>,5:20]]}, query0={a1=[[@7,26:27='a1',<381>,2:20], [@44,146:148='mmm',<381>,3:82], [@90,271:273='ppp',<381>,5:8], [@106,323:325='ppp',<381>,7:20]], a2=[[@11,34:35='a2',<381>,2:28], [@50,160:162='mmm',<381>,3:96]], a3=[[@15,42:43='a3',<381>,2:36], [@56,174:176='mmm',<381>,3:110]]}, values4={$1=[[@72,222:222='(',<287>,4:17], [@80,242:242='(',<287>,4:37]], $2=[[@72,222:222='(',<287>,4:17], [@80,242:242='(',<287>,4:37]], $3=[[@72,222:222='(',<287>,4:17], [@80,242:242='(',<287>,4:37]]}, query1={b2=[[@32,92:93='b2',<381>,3:28]], b3=[[@36,100:101='b3',<381>,3:36]], b1=[[@28,84:85='b1',<381>,3:20]]}, query2={b2=[[@54,170:171='b2',<381>,3:106]], b3=[[@60,184:185='b3',<381>,3:120]], b1=[[@48,156:157='b1',<381>,3:92]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query5={context_list={aaa=query0, bbb=intersect3, ccc=values4, ppp=query0, qqq=intersect3, rrr=values4}, query_dictionary={a1=[[@92,275:276='a1',<381>,5:12]], b2=[[@96,283:284='b2',<381>,5:20]]}, table_dictionary={}, def_intersect3={context_list={aaa=query0}, interface={b2=[{name=b2, table_ref=lll}, {name=a2, table_ref=mmm}], b3=[{name=b3, table_ref=lll}, {name=a3, table_ref=mmm}], b1=[{name=b1, table_ref=lll}, {name=a1, table_ref=mmm}]}, query1={context_list={aaa=query0}, query_dictionary={b2=[[@32,92:93='b2',<381>,3:28]], b3=[[@36,100:101='b3',<381>,3:36]], b1=[[@28,84:85='b1',<381>,3:20]]}, table_dictionary={tab2={b2=[[@30,88:90='lll',<381>,3:24]], b3=[[@34,96:98='lll',<381>,3:32]], b1=[[@26,80:82='lll',<381>,3:16]]}}, interface={b2=[{name=b2, table_ref=lll}], b3=[{name=b3, table_ref=lll}], b1=[{name=b1, table_ref=lll}]}, table_alias={aaa=query0, lll=tab2}}, table_alias={aaa=query0}, query2={context_list={aaa=query0, mmm=query0}, query_dictionary={b2=[[@54,170:171='b2',<381>,3:106]], b3=[[@60,184:185='b3',<381>,3:120]], b1=[[@48,156:157='b1',<381>,3:92]]}, table_dictionary={}, interface={b2=[{name=a2, table_ref=mmm}], b3=[{name=a3, table_ref=mmm}], b1=[{name=a1, table_ref=mmm}]}, table_alias={aaa=query0, mmm=query0}}}, def_query0={query_dictionary={a1=[[@7,26:27='a1',<381>,2:20], [@44,146:148='mmm',<381>,3:82], [@90,271:273='ppp',<381>,5:8], [@106,323:325='ppp',<381>,7:20]], a2=[[@11,34:35='a2',<381>,2:28], [@50,160:162='mmm',<381>,3:96]], a3=[[@15,42:43='a3',<381>,2:36], [@56,174:176='mmm',<381>,3:110]]}, table_dictionary={tab1={a1=[[@5,22:24='kkk',<381>,2:16], [@44,146:148='mmm',<381>,3:82], [@90,271:273='ppp',<381>,5:8], [@106,323:325='ppp',<381>,7:20]], a2=[[@9,30:32='kkk',<381>,2:24], [@50,160:162='mmm',<381>,3:96]], a3=[[@13,38:40='kkk',<381>,2:32], [@56,174:176='mmm',<381>,3:110]]}}, interface={a1=[{name=a1, table_ref=kkk}], a2=[{name=a2, table_ref=kkk}], a3=[{name=a3, table_ref=kkk}]}, table_alias={kkk=tab1}}, def_values4={query_dictionary={$1=[[@72,222:222='(',<287>,4:17], [@80,242:242='(',<287>,4:37]], $2=[[@72,222:222='(',<287>,4:17], [@80,242:242='(',<287>,4:37]], $3=[[@72,222:222='(',<287>,4:17], [@80,242:242='(',<287>,4:37]]}, table_dictionary={}, interface={$1=[], $2=[], $3=[]}}, filters=[{name=a1, table_ref=ppp}, {name=b1, table_ref=qqq}], interface={a1=[{name=a1, table_ref=ppp}], b2=[{name=b2, table_ref=qqq}]}, table_alias={aaa=query0, ccc=values4, bbb=intersect3}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void nestedWithJoinAliasHeavyValuesCteVirtualPositionRefsExemplarParsesWithoutErrors() {
		final String sql = "WITH "
				+ "\n aaa AS (SELECT kkk.a1, kkk.a2, kkk.a3 FROM tab1 AS kkk),"
				+ "\n bbb AS ((VALUES (401, 501, 601), (402, 502, 602))),"
				+ "\n ccc AS (SELECT nnn.c1, nnn.c2, nnn.c3 FROM tab3 AS nnn JOIN aaa AS ooo ON ooo.a3 = nnn.c3)"
				+ "\n SELECT ppp.a1, rrr.c2"
				+ "\n FROM aaa AS ppp"
				+ "\n JOIN ccc AS rrr ON ppp.a1 = rrr.c1"
				+ "\n JOIN bbb AS qqq ON $1 = ppp.a1";

		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runParsertest(sql, parser);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong", "{SQL={with={1={cte={select={1={column={name=a1, table_ref=kkk}}, 2={column={name=a2, table_ref=kkk}}, 3={column={name=a3, table_ref=kkk}}}, from={table={alias=kkk, table=tab1}}}, alias=aaa}, 2={cte={values={matrix={1={row={1={literal=401}, 2={literal=501}, 3={literal=601}}}, 2={row={1={literal=402}, 2={literal=502}, 3={literal=602}}}}}}, alias=bbb}, 3={cte={select={1={column={name=c1, table_ref=nnn}}, 2={column={name=c2, table_ref=nnn}}, 3={column={name=c3, table_ref=nnn}}}, from={join={1={table={alias=nnn, table=tab3}}, 2={join=JOIN, on={condition={left={column={name=a3, table_ref=ooo}}, right={column={name=c3, table_ref=nnn}}, operator==}}}, 3={table={alias=ooo, table=aaa}}}}}, alias=ccc}}, query={select={1={column={name=a1, table_ref=ppp}}, 2={column={name=c2, table_ref=rrr}}}, from={join={1={table={alias=ppp, table=aaa}}, 2={join=JOIN, on={condition={left={column={name=a1, table_ref=ppp}}, right={column={name=c1, table_ref=rrr}}, operator==}}}, 3={table={alias=rrr, table=ccc}}, 4={join=JOIN, on={condition={left={literal=1}, right={column={name=a1, table_ref=ppp}}, operator==}}}, 5={table={alias=qqq, table=bbb}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[a1, c2]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab3={c3=[[@57,149:151='nnn',<381>,4:32], [@73,201:203='nnn',<381>,4:84]], c1=[[@49,133:135='nnn',<381>,4:16], [@98,278:280='rrr',<381>,7:29]], c2=[[@53,141:143='nnn',<381>,4:24], [@82,225:227='rrr',<381>,5:16]]}, tab1={a1=[[@5,22:24='kkk',<381>,2:16], [@78,217:219='ppp',<381>,5:8], [@94,269:271='ppp',<381>,7:20], [@108,310:312='ppp',<381>,8:25]], a2=[[@9,30:32='kkk',<381>,2:24]], a3=[[@13,38:40='kkk',<381>,2:32], [@69,192:194='ooo',<381>,4:75]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{values1={$1=[[@27,81:81='(',<287>,3:17], [@35,98:98='(',<287>,3:34]], $2=[[@27,81:81='(',<287>,3:17], [@35,98:98='(',<287>,3:34]], $3=[[@27,81:81='(',<287>,3:17], [@35,98:98='(',<287>,3:34]]}, query0={a1=[[@7,26:27='a1',<381>,2:20], [@78,217:219='ppp',<381>,5:8], [@94,269:271='ppp',<381>,7:20], [@108,310:312='ppp',<381>,8:25]], a2=[[@11,34:35='a2',<381>,2:28]], a3=[[@15,42:43='a3',<381>,2:36], [@69,192:194='ooo',<381>,4:75]]}, query2={c3=[[@59,153:154='c3',<381>,4:36]], c1=[[@51,137:138='c1',<381>,4:20], [@98,278:280='rrr',<381>,7:29]], c2=[[@55,145:146='c2',<381>,4:28], [@82,225:227='rrr',<381>,5:16]]}, query3={a1=[[@80,221:222='a1',<381>,5:12]], c2=[[@84,229:230='c2',<381>,5:20]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query3={context_list={aaa=query0, bbb=values1, ccc=query2, ppp=query0, rrr=query2, qqq=values1}, query_dictionary={a1=[[@80,221:222='a1',<381>,5:12]], c2=[[@84,229:230='c2',<381>,5:20]]}, table_dictionary={}, def_values1={query_dictionary={$1=[[@27,81:81='(',<287>,3:17], [@35,98:98='(',<287>,3:34]], $2=[[@27,81:81='(',<287>,3:17], [@35,98:98='(',<287>,3:34]], $3=[[@27,81:81='(',<287>,3:17], [@35,98:98='(',<287>,3:34]]}, table_dictionary={}, interface={$1=[], $2=[], $3=[]}}, def_query0={query_dictionary={a1=[[@7,26:27='a1',<381>,2:20], [@78,217:219='ppp',<381>,5:8], [@94,269:271='ppp',<381>,7:20], [@108,310:312='ppp',<381>,8:25]], a2=[[@11,34:35='a2',<381>,2:28]], a3=[[@15,42:43='a3',<381>,2:36], [@69,192:194='ooo',<381>,4:75]]}, table_dictionary={tab1={a1=[[@5,22:24='kkk',<381>,2:16], [@78,217:219='ppp',<381>,5:8], [@94,269:271='ppp',<381>,7:20], [@108,310:312='ppp',<381>,8:25]], a2=[[@9,30:32='kkk',<381>,2:24]], a3=[[@13,38:40='kkk',<381>,2:32], [@69,192:194='ooo',<381>,4:75]]}}, interface={a1=[{name=a1, table_ref=kkk}], a2=[{name=a2, table_ref=kkk}], a3=[{name=a3, table_ref=kkk}]}, table_alias={kkk=tab1}}, filters=[{name=a1, table_ref=ppp}, {name=c1, table_ref=rrr}], interface={a1=[{name=a1, table_ref=ppp}], c2=[{name=c2, table_ref=rrr}]}, table_alias={aaa=query0, ccc=query2, bbb=values1}, def_query2={context_list={aaa=query0, bbb=values1, ooo=query0}, query_dictionary={c3=[[@59,153:154='c3',<381>,4:36]], c1=[[@51,137:138='c1',<381>,4:20], [@98,278:280='rrr',<381>,7:29]], c2=[[@55,145:146='c2',<381>,4:28], [@82,225:227='rrr',<381>,5:16]]}, table_dictionary={tab3={c3=[[@57,149:151='nnn',<381>,4:32], [@73,201:203='nnn',<381>,4:84]], c1=[[@49,133:135='nnn',<381>,4:16], [@98,278:280='rrr',<381>,7:29]], c2=[[@53,141:143='nnn',<381>,4:24], [@82,225:227='rrr',<381>,5:16]]}}, filters=[{name=a3, table_ref=ooo}, {name=c3, table_ref=nnn}], interface={c3=[{name=c3, table_ref=nnn}], c1=[{name=c1, table_ref=nnn}], c2=[{name=c2, table_ref=nnn}]}, table_alias={aaa=query0, bbb=values1, ooo=query0, nnn=tab3}}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void nestedWithScalarHavingAliasHeavyValuesCteVirtualPositionRefsExemplarParsesWithoutErrors() {
		final String sql = "WITH "
				+ "\n aaa AS ((VALUES (31, 41, 51), (32, 42, 52))),"
				+ "\n bbb AS (SELECT lll.b1, lll.b2, max(lll.b3) AS b3 FROM tab2 AS lll GROUP BY lll.b1, lll.b2"
				+ "\n        HAVING max(lll.b3) >= (SELECT max($3) FROM aaa AS mmm)),"
				+ "\n ccc AS (SELECT nnn.c1, nnn.c2, max(nnn.c3) AS c3 FROM tab3 AS nnn GROUP BY nnn.c1, nnn.c2"
				+ "\n        HAVING max(nnn.c3) >= (SELECT max(ooo.b3) FROM bbb AS ooo))"
				+ "\n SELECT qqq.b1, qqq.b2, rrr.c2, (SELECT max($2) FROM aaa AS ttt) AS a2_virtual"
				+ "\n FROM bbb AS qqq"
				+ "\n JOIN ccc AS rrr ON qqq.b1 = rrr.c1";

		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runParsertest(sql, parser);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong", "{SQL={with={1={cte={values={matrix={1={row={1={literal=31}, 2={literal=41}, 3={literal=51}}}, 2={row={1={literal=32}, 2={literal=42}, 3={literal=52}}}}}}, alias=aaa}, 2={cte={select={1={column={name=b1, table_ref=lll}}, 2={column={name=b2, table_ref=lll}}, 3={function={function_name=max, qualifier=null, parameters={column={name=b3, table_ref=lll}}}, alias=b3}}, having={condition={left={function={function_name=max, qualifier=null, parameters={column={name=b3, table_ref=lll}}}}, right={select={1={function={function_name=max, qualifier=null, parameters={literal=3}}}}, from={table={alias=mmm, table=aaa}}}, operator=>=}}, from={table={alias=lll, table=tab2}}, groupby={1={column={name=b1, table_ref=lll}}, 2={column={name=b2, table_ref=lll}}}}, alias=bbb}, 3={cte={select={1={column={name=c1, table_ref=nnn}}, 2={column={name=c2, table_ref=nnn}}, 3={function={function_name=max, qualifier=null, parameters={column={name=c3, table_ref=nnn}}}, alias=c3}}, having={condition={left={function={function_name=max, qualifier=null, parameters={column={name=c3, table_ref=nnn}}}}, right={select={1={function={function_name=max, qualifier=null, parameters={column={name=b3, table_ref=ooo}}}}}, from={table={alias=ooo, table=bbb}}}, operator=>=}}, from={table={alias=nnn, table=tab3}}, groupby={1={column={name=c1, table_ref=nnn}}, 2={column={name=c2, table_ref=nnn}}}}, alias=ccc}}, query={select={1={column={name=b1, table_ref=qqq}}, 2={column={name=b2, table_ref=qqq}}, 3={column={name=c2, table_ref=rrr}}, 4={lookup={from={table={alias=ttt, table=aaa}}, select={1={function={function_name=max, qualifier=null, parameters={literal=2}}}}}, alias=a2_virtual}}, from={join={1={table={alias=qqq, table=bbb}}, 2={join=JOIN, on={condition={left={column={name=b1, table_ref=qqq}}, right={column={name=c1, table_ref=rrr}}, operator==}}}, 3={table={alias=rrr, table=ccc}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[b2, a2_virtual, b1, c2]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab3={c3=[[@92,245:247='nnn',<381>,5:36], [@114,319:321='nnn',<381>,6:19]], c1=[[@82,225:227='nnn',<381>,5:16], [@104,285:287='nnn',<381>,5:76], [@172,493:495='rrr',<381>,9:29]], c2=[[@86,233:235='nnn',<381>,5:24], [@108,293:295='nnn',<381>,5:84], [@142,392:394='rrr',<381>,7:24]]}, tab2={b2=[[@32,77:79='lll',<381>,3:24], [@54,137:139='lll',<381>,3:84], [@138,384:386='qqq',<381>,7:16]], b3=[[@38,89:91='lll',<381>,3:36], [@60,163:165='lll',<381>,4:19], [@123,342:344='ooo',<381>,6:42]], b1=[[@28,69:71='lll',<381>,3:16], [@50,129:131='lll',<381>,3:76], [@134,376:378='qqq',<381>,7:8], [@168,484:486='qqq',<381>,9:20]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{values0={$1=[[@6,23:23='(',<287>,2:17], [@14,37:37='(',<287>,2:31]], $2=[[@6,23:23='(',<287>,2:17], [@14,37:37='(',<287>,2:31]], $3=[[@6,23:23='(',<287>,2:17], [@14,37:37='(',<287>,2:31]]}, query9={b2=[[@140,388:389='b2',<381>,7:20]], a2_virtual=[[@158,436:445='a2_virtual',<381>,7:68]], b1=[[@136,380:381='b1',<381>,7:12]], c2=[[@144,396:397='c2',<381>,7:28]]}, query4={unnamed_1=[[@126,348:348=')',<288>,6:48]]}, query6={c3=[[@97,256:257='c3',<381>,5:47]], c1=[[@84,229:230='c1',<381>,5:20], [@172,493:495='rrr',<381>,9:29]], c2=[[@88,237:238='c2',<381>,5:28], [@142,392:394='rrr',<381>,7:24]]}, query7={unnamed_2=[[@151,414:414=')',<288>,7:46]]}, query1={unnamed_0=[[@70,188:188=')',<288>,4:44]]}, query3={b2=[[@34,81:82='b2',<381>,3:28], [@138,384:386='qqq',<381>,7:16]], b3=[[@43,100:101='b3',<381>,3:47], [@123,342:344='ooo',<381>,6:42]], b1=[[@30,73:74='b1',<381>,3:20], [@134,376:378='qqq',<381>,7:8], [@168,484:486='qqq',<381>,9:20]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query9={context_list={aaa=values0, bbb=query3, ccc=query6, qqq=query3, rrr=query6}, def_query7={context_list={aaa=values0, bbb=query3, ccc=query6, ttt=values0}, query_dictionary={unnamed_2=[[@151,414:414=')',<288>,7:46]]}, table_dictionary={}, interface={unnamed_2=[]}, table_alias={aaa=values0, ccc=query6, bbb=query3, ttt=values0}}, def_query6={context_list={aaa=values0, bbb=query3}, query_dictionary={c3=[[@97,256:257='c3',<381>,5:47]], c1=[[@84,229:230='c1',<381>,5:20], [@172,493:495='rrr',<381>,9:29]], c2=[[@88,237:238='c2',<381>,5:28], [@142,392:394='rrr',<381>,7:24]]}, table_dictionary={tab3={c3=[[@92,245:247='nnn',<381>,5:36], [@114,319:321='nnn',<381>,6:19]], c1=[[@82,225:227='nnn',<381>,5:16], [@104,285:287='nnn',<381>,5:76], [@172,493:495='rrr',<381>,9:29]], c2=[[@86,233:235='nnn',<381>,5:24], [@108,293:295='nnn',<381>,5:84], [@142,392:394='rrr',<381>,7:24]]}}, grouped_by=[{name=c1, table_ref=nnn}, {name=c2, table_ref=nnn}], dependent_queries={predicand5={query=query4, type=filters}}, filters=[{name=c3, table_ref=nnn}], interface={c3=[{name=c3, table_ref=nnn}], c1=[{name=c1, table_ref=nnn}], c2=[{name=c2, table_ref=nnn}]}, def_query4={context_list={aaa=values0, bbb=query3, ooo=query3}, query_dictionary={unnamed_1=[[@126,348:348=')',<288>,6:48]]}, table_dictionary={}, interface={unnamed_1=[{name=b3, table_ref=ooo}]}, table_alias={aaa=values0, bbb=query3, ooo=query3}}, table_alias={aaa=values0, bbb=query3, nnn=tab3}}, table_dictionary={}, def_values0={query_dictionary={$1=[[@6,23:23='(',<287>,2:17], [@14,37:37='(',<287>,2:31]], $2=[[@6,23:23='(',<287>,2:17], [@14,37:37='(',<287>,2:31]], $3=[[@6,23:23='(',<287>,2:17], [@14,37:37='(',<287>,2:31]]}, table_dictionary={}, interface={$1=[], $2=[], $3=[]}}, filters=[{name=b1, table_ref=qqq}, {name=c1, table_ref=rrr}], interface={b2=[{name=b2, table_ref=qqq}], a2_virtual=[], b1=[{name=b1, table_ref=qqq}], c2=[{name=c2, table_ref=rrr}]}, def_query3={context_list={aaa=values0}, query_dictionary={b2=[[@34,81:82='b2',<381>,3:28], [@138,384:386='qqq',<381>,7:16]], b3=[[@43,100:101='b3',<381>,3:47], [@123,342:344='ooo',<381>,6:42]], b1=[[@30,73:74='b1',<381>,3:20], [@134,376:378='qqq',<381>,7:8], [@168,484:486='qqq',<381>,9:20]]}, table_dictionary={tab2={b2=[[@32,77:79='lll',<381>,3:24], [@54,137:139='lll',<381>,3:84], [@138,384:386='qqq',<381>,7:16]], b3=[[@38,89:91='lll',<381>,3:36], [@60,163:165='lll',<381>,4:19], [@123,342:344='ooo',<381>,6:42]], b1=[[@28,69:71='lll',<381>,3:16], [@50,129:131='lll',<381>,3:76], [@134,376:378='qqq',<381>,7:8], [@168,484:486='qqq',<381>,9:20]]}}, grouped_by=[{name=b1, table_ref=lll}, {name=b2, table_ref=lll}], def_query1={context_list={aaa=values0, mmm=values0}, query_dictionary={unnamed_0=[[@70,188:188=')',<288>,4:44]]}, table_dictionary={}, interface={unnamed_0=[]}, table_alias={aaa=values0, mmm=values0}}, dependent_queries={predicand2={query=query1, type=filters}}, filters=[{name=b3, table_ref=lll}], interface={b2=[{name=b2, table_ref=lll}], b3=[{name=b3, table_ref=lll}], b1=[{name=b1, table_ref=lll}]}, table_alias={aaa=values0, lll=tab2}}, query_dictionary={b2=[[@140,388:389='b2',<381>,7:20]], a2_virtual=[[@158,436:445='a2_virtual',<381>,7:68]], b1=[[@136,380:381='b1',<381>,7:12]], c2=[[@144,396:397='c2',<381>,7:28]]}, dependent_queries={predicand8={query=query7, type=interface}}, table_alias={aaa=values0, ccc=query6, bbb=query3}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void nestedWithScalarWhereAliasHeavyValuesCteVirtualPositionRefsExemplarParsesWithoutErrors() {
		final String sql = "WITH "
				+ "\n aaa AS (SELECT kkk.a1, kkk.a2, kkk.a3 FROM tab1 AS kkk),"
				+ "\n bbb AS ((VALUES (71, 81, 91), (72, 82, 92))),"
				+ "\n ccc AS (SELECT nnn.c1, nnn.c2, nnn.c3 FROM tab3 AS nnn"
				+ "\n        WHERE nnn.c3 = (SELECT max($3) FROM bbb AS ooo WHERE $1 = nnn.c1))"
				+ "\n SELECT ppp.a1, rrr.c2, (SELECT max($2) FROM bbb AS sss) AS b2_virtual"
				+ "\n FROM aaa AS ppp"
				+ "\n JOIN ccc AS rrr ON ppp.a1 = rrr.c1";

		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runParsertest(sql, parser);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong", "{SQL={with={1={cte={select={1={column={name=a1, table_ref=kkk}}, 2={column={name=a2, table_ref=kkk}}, 3={column={name=a3, table_ref=kkk}}}, from={table={alias=kkk, table=tab1}}}, alias=aaa}, 2={cte={values={matrix={1={row={1={literal=71}, 2={literal=81}, 3={literal=91}}}, 2={row={1={literal=72}, 2={literal=82}, 3={literal=92}}}}}}, alias=bbb}, 3={cte={select={1={column={name=c1, table_ref=nnn}}, 2={column={name=c2, table_ref=nnn}}, 3={column={name=c3, table_ref=nnn}}}, from={table={alias=nnn, table=tab3}}, where={condition={left={column={name=c3, table_ref=nnn}}, right={select={1={function={function_name=max, qualifier=null, parameters={literal=3}}}}, from={table={alias=ooo, table=bbb}}, where={condition={left={literal=1}, right={column={name=c1, table_ref=nnn}}, operator==}}}, operator==}}}, alias=ccc}}, query={select={1={column={name=a1, table_ref=ppp}}, 2={column={name=c2, table_ref=rrr}}, 3={lookup={from={table={alias=sss, table=bbb}}, select={1={function={function_name=max, qualifier=null, parameters={literal=2}}}}}, alias=b2_virtual}}, from={join={1={table={alias=ppp, table=aaa}}, 2={join=JOIN, on={condition={left={column={name=a1, table_ref=ppp}}, right={column={name=c1, table_ref=rrr}}, operator==}}}, 3={table={alias=rrr, table=ccc}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[a1, c2, b2_virtual]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab3={c3=[[@57,143:145='nnn',<381>,4:32], [@65,181:183='nnn',<381>,5:14]], c1=[[@49,127:129='nnn',<381>,4:16], [@82,233:235='nnn',<381>,5:66], [@122,359:361='rrr',<381>,8:29]], c2=[[@53,135:137='nnn',<381>,4:24], [@92,258:260='rrr',<381>,6:16]]}, tab1={a1=[[@5,22:24='kkk',<381>,2:16], [@88,250:252='ppp',<381>,6:8], [@118,350:352='ppp',<381>,8:20]], a2=[[@9,30:32='kkk',<381>,2:24]], a3=[[@13,38:40='kkk',<381>,2:32]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{values1={$1=[[@27,81:81='(',<287>,3:17], [@35,95:95='(',<287>,3:31]], $2=[[@27,81:81='(',<287>,3:17], [@35,95:95='(',<287>,3:31]], $3=[[@27,81:81='(',<287>,3:17], [@35,95:95='(',<287>,3:31]]}, query4={c3=[[@59,147:148='c3',<381>,4:36]], c1=[[@51,131:132='c1',<381>,4:20], [@122,359:361='rrr',<381>,8:29]], c2=[[@55,139:140='c2',<381>,4:28], [@92,258:260='rrr',<381>,6:16]]}, query5={unnamed_1=[[@101,280:280=')',<288>,6:38]]}, query7={a1=[[@90,254:255='a1',<381>,6:12]], c2=[[@94,262:263='c2',<381>,6:20]], b2_virtual=[[@108,302:311='b2_virtual',<381>,6:60]]}, query0={a1=[[@7,26:27='a1',<381>,2:20], [@88,250:252='ppp',<381>,6:8], [@118,350:352='ppp',<381>,8:20]], a2=[[@11,34:35='a2',<381>,2:28]], a3=[[@15,42:43='a3',<381>,2:36]]}, query2={unnamed_0=[[@74,204:204=')',<288>,5:37]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query7={context_list={aaa=query0, bbb=values1, ccc=query4, ppp=query0, rrr=query4}, table_dictionary={}, def_values1={query_dictionary={$1=[[@27,81:81='(',<287>,3:17], [@35,95:95='(',<287>,3:31]], $2=[[@27,81:81='(',<287>,3:17], [@35,95:95='(',<287>,3:31]], $3=[[@27,81:81='(',<287>,3:17], [@35,95:95='(',<287>,3:31]]}, table_dictionary={}, interface={$1=[], $2=[], $3=[]}}, def_query0={query_dictionary={a1=[[@7,26:27='a1',<381>,2:20], [@88,250:252='ppp',<381>,6:8], [@118,350:352='ppp',<381>,8:20]], a2=[[@11,34:35='a2',<381>,2:28]], a3=[[@15,42:43='a3',<381>,2:36]]}, table_dictionary={tab1={a1=[[@5,22:24='kkk',<381>,2:16], [@88,250:252='ppp',<381>,6:8], [@118,350:352='ppp',<381>,8:20]], a2=[[@9,30:32='kkk',<381>,2:24]], a3=[[@13,38:40='kkk',<381>,2:32]]}}, interface={a1=[{name=a1, table_ref=kkk}], a2=[{name=a2, table_ref=kkk}], a3=[{name=a3, table_ref=kkk}]}, table_alias={kkk=tab1}}, filters=[{name=a1, table_ref=ppp}, {name=c1, table_ref=rrr}], def_query5={context_list={aaa=query0, bbb=values1, ccc=query4, sss=values1}, query_dictionary={unnamed_1=[[@101,280:280=')',<288>,6:38]]}, table_dictionary={}, interface={unnamed_1=[]}, table_alias={aaa=query0, ccc=query4, sss=values1, bbb=values1}}, interface={a1=[{name=a1, table_ref=ppp}], c2=[{name=c2, table_ref=rrr}], b2_virtual=[]}, def_query4={context_list={aaa=query0, bbb=values1}, query_dictionary={c3=[[@59,147:148='c3',<381>,4:36]], c1=[[@51,131:132='c1',<381>,4:20], [@122,359:361='rrr',<381>,8:29]], c2=[[@55,139:140='c2',<381>,4:28], [@92,258:260='rrr',<381>,6:16]]}, table_dictionary={tab3={c3=[[@57,143:145='nnn',<381>,4:32], [@65,181:183='nnn',<381>,5:14]], c1=[[@49,127:129='nnn',<381>,4:16], [@82,233:235='nnn',<381>,5:66], [@122,359:361='rrr',<381>,8:29]], c2=[[@53,135:137='nnn',<381>,4:24], [@92,258:260='rrr',<381>,6:16]]}}, dependent_queries={predicand3={query=query2, type=filters}}, filters=[{name=c3, table_ref=nnn}], interface={c3=[{name=c3, table_ref=nnn}], c1=[{name=c1, table_ref=nnn}], c2=[{name=c2, table_ref=nnn}]}, table_alias={aaa=query0, bbb=values1, nnn=tab3}, def_query2={context_list={aaa=query0, bbb=values1, ooo=values1}, query_dictionary={unnamed_0=[[@74,204:204=')',<288>,5:37]]}, table_dictionary={}, filters=[{name=c1, table_ref=nnn}], interface={unnamed_0=[]}, table_alias={aaa=query0, bbb=values1, ooo=values1}}}, query_dictionary={a1=[[@90,254:255='a1',<381>,6:12]], c2=[[@94,262:263='c2',<381>,6:20]], b2_virtual=[[@108,302:311='b2_virtual',<381>,6:60]]}, dependent_queries={predicand6={query=query5, type=interface}}, table_alias={aaa=query0, ccc=query4, bbb=values1}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void nestedWithScalarSelectListAliasHeavyValuesCteVirtualPositionRefsExemplarParsesWithoutErrors() {
		final String sql = "WITH "
				+ "\n aaa AS (SELECT kkk.a1, kkk.a2, kkk.a3 FROM tab1 AS kkk),"
				+ "\n bbb AS (SELECT lll.b1, lll.b2, (SELECT max(mmm.a3) FROM aaa AS mmm) AS b3 FROM tab2 AS lll),"
				+ "\n ccc AS ((VALUES (301, 401, 501), (302, 402, 502)))"
				+ "\n SELECT ppp.a1, qqq.b2, (SELECT max($2) FROM ccc AS rrr) AS c2_virtual"
				+ "\n FROM aaa AS ppp"
				+ "\n JOIN bbb AS qqq ON ppp.a1 = qqq.b1";

		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runParsertest(sql, parser);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong", "{SQL={with={1={cte={select={1={column={name=a1, table_ref=kkk}}, 2={column={name=a2, table_ref=kkk}}, 3={column={name=a3, table_ref=kkk}}}, from={table={alias=kkk, table=tab1}}}, alias=aaa}, 2={cte={select={1={column={name=b1, table_ref=lll}}, 2={column={name=b2, table_ref=lll}}, 3={lookup={from={table={alias=mmm, table=aaa}}, select={1={function={function_name=max, qualifier=null, parameters={column={name=a3, table_ref=mmm}}}}}}, alias=b3}}, from={table={alias=lll, table=tab2}}}, alias=bbb}, 3={cte={values={matrix={1={row={1={literal=301}, 2={literal=401}, 3={literal=501}}}, 2={row={1={literal=302}, 2={literal=402}, 3={literal=502}}}}}}, alias=ccc}}, query={select={1={column={name=a1, table_ref=ppp}}, 2={column={name=b2, table_ref=qqq}}, 3={lookup={from={table={alias=rrr, table=ccc}}, select={1={function={function_name=max, qualifier=null, parameters={literal=2}}}}}, alias=c2_virtual}}, from={join={1={table={alias=ppp, table=aaa}}, 2={join=JOIN, on={condition={left={column={name=a1, table_ref=ppp}}, right={column={name=b1, table_ref=qqq}}, operator==}}}, 3={table={alias=qqq, table=bbb}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[a1, b2, c2_virtual]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={a1=[[@5,22:24='kkk',<381>,2:16], [@78,218:220='ppp',<381>,5:8], [@108,318:320='ppp',<381>,7:20]], a2=[[@9,30:32='kkk',<381>,2:24]], a3=[[@13,38:40='kkk',<381>,2:32], [@38,108:110='mmm',<381>,3:44]]}, tab2={b2=[[@30,88:90='lll',<381>,3:24], [@82,226:228='qqq',<381>,5:16]], b1=[[@26,80:82='lll',<381>,3:16], [@112,327:329='qqq',<381>,7:29]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query5={unnamed_1=[[@91,248:248=')',<288>,5:38]]}, query7={a1=[[@80,222:223='a1',<381>,5:12]], b2=[[@84,230:231='b2',<381>,5:20]], c2_virtual=[[@98,270:279='c2_virtual',<381>,5:60]]}, query0={a1=[[@7,26:27='a1',<381>,2:20], [@78,218:220='ppp',<381>,5:8], [@108,318:320='ppp',<381>,7:20]], a2=[[@11,34:35='a2',<381>,2:28]], a3=[[@15,42:43='a3',<381>,2:36], [@38,108:110='mmm',<381>,3:44]]}, values4={$1=[[@60,175:175='(',<287>,4:17], [@68,192:192='(',<287>,4:34]], $2=[[@60,175:175='(',<287>,4:17], [@68,192:192='(',<287>,4:34]], $3=[[@60,175:175='(',<287>,4:17], [@68,192:192='(',<287>,4:34]]}, query1={unnamed_0=[[@41,114:114=')',<288>,3:50]]}, query3={b2=[[@32,92:93='b2',<381>,3:28], [@82,226:228='qqq',<381>,5:16]], b3=[[@48,136:137='b3',<381>,3:72]], b1=[[@28,84:85='b1',<381>,3:20], [@112,327:329='qqq',<381>,7:29]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query7={context_list={aaa=query0, bbb=query3, ccc=values4, ppp=query0, qqq=query3}, table_dictionary={}, def_query0={query_dictionary={a1=[[@7,26:27='a1',<381>,2:20], [@78,218:220='ppp',<381>,5:8], [@108,318:320='ppp',<381>,7:20]], a2=[[@11,34:35='a2',<381>,2:28]], a3=[[@15,42:43='a3',<381>,2:36], [@38,108:110='mmm',<381>,3:44]]}, table_dictionary={tab1={a1=[[@5,22:24='kkk',<381>,2:16], [@78,218:220='ppp',<381>,5:8], [@108,318:320='ppp',<381>,7:20]], a2=[[@9,30:32='kkk',<381>,2:24]], a3=[[@13,38:40='kkk',<381>,2:32], [@38,108:110='mmm',<381>,3:44]]}}, interface={a1=[{name=a1, table_ref=kkk}], a2=[{name=a2, table_ref=kkk}], a3=[{name=a3, table_ref=kkk}]}, table_alias={kkk=tab1}}, def_values4={query_dictionary={$1=[[@60,175:175='(',<287>,4:17], [@68,192:192='(',<287>,4:34]], $2=[[@60,175:175='(',<287>,4:17], [@68,192:192='(',<287>,4:34]], $3=[[@60,175:175='(',<287>,4:17], [@68,192:192='(',<287>,4:34]]}, table_dictionary={}, interface={$1=[], $2=[], $3=[]}}, filters=[{name=a1, table_ref=ppp}, {name=b1, table_ref=qqq}], def_query5={context_list={aaa=query0, bbb=query3, ccc=values4, rrr=values4}, query_dictionary={unnamed_1=[[@91,248:248=')',<288>,5:38]]}, table_dictionary={}, interface={unnamed_1=[]}, table_alias={aaa=query0, ccc=values4, bbb=query3, rrr=values4}}, interface={a1=[{name=a1, table_ref=ppp}], b2=[{name=b2, table_ref=qqq}], c2_virtual=[]}, def_query3={context_list={aaa=query0}, query_dictionary={b2=[[@32,92:93='b2',<381>,3:28], [@82,226:228='qqq',<381>,5:16]], b3=[[@48,136:137='b3',<381>,3:72]], b1=[[@28,84:85='b1',<381>,3:20], [@112,327:329='qqq',<381>,7:29]]}, table_dictionary={tab2={b2=[[@30,88:90='lll',<381>,3:24], [@82,226:228='qqq',<381>,5:16]], b1=[[@26,80:82='lll',<381>,3:16], [@112,327:329='qqq',<381>,7:29]]}}, def_query1={context_list={aaa=query0, mmm=query0}, query_dictionary={unnamed_0=[[@41,114:114=')',<288>,3:50]]}, table_dictionary={}, interface={unnamed_0=[{name=a3, table_ref=mmm}]}, table_alias={aaa=query0, mmm=query0}}, dependent_queries={predicand2={query=query1, type=interface}}, interface={b2=[{name=b2, table_ref=lll}], b3=[{name=a3, table_ref=mmm}], b1=[{name=b1, table_ref=lll}]}, table_alias={aaa=query0, lll=tab2}}, query_dictionary={a1=[[@80,222:223='a1',<381>,5:12]], b2=[[@84,230:231='b2',<381>,5:20]], c2_virtual=[[@98,270:279='c2_virtual',<381>,5:60]]}, dependent_queries={predicand6={query=query5, type=interface}}, table_alias={aaa=query0, ccc=values4, bbb=query3}}}",
				extractor.getSymbolTable().toString());
	}



	@Test
	public void nestedWithExistsAliasHeavyDeleteCteVirtualColumnsExemplarParsesWithoutErrors() {
		final String sql = "WITH "
				+ "\n aaa AS (DELETE FROM hhh AS hhh RETURNING hhh.a1 AS a1, hhh.a2 AS a2, hhh.a3 AS a3),"
				+ "\n bbb_ccc AS ("
				+ "\n   WITH "
				+ "\n     bbb AS (SELECT lll.b1, lll.b2, lll.b3 FROM tab2 AS lll WHERE EXISTS (SELECT 1 FROM aaa AS mmm WHERE mmm.a3 = lll.b3)),"
				+ "\n     ccc AS (SELECT nnn.c1, nnn.c2, nnn.c3 FROM tab3 AS nnn WHERE EXISTS (SELECT 1 FROM aaa AS vvv WHERE vvv.a1 = nnn.c1))"
				+ "\n   SELECT ppp.b1 AS b1, qqq.c1 AS c1, qqq.c2 AS c2"
				+ "\n   FROM bbb AS ppp JOIN ccc AS qqq ON ppp.b1 = qqq.c1"
				+ "\n )"
				+ "\n SELECT sss.b1, sss.c2, ttt.a2"
				+ "\n FROM bbb_ccc AS sss"
				+ "\n JOIN aaa AS ttt ON ttt.a1 = sss.b1"
		;

		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runParsertest(sql, parser);
		Assert.assertEquals("AST is wrong", "{SQL={with={1={cte={delete={table={alias=hhh, table=hhh}, returning={1={column={name=a1, table_ref=hhh}, alias=a1}, 2={column={name=a2, table_ref=hhh}, alias=a2}, 3={column={name=a3, table_ref=hhh}, alias=a3}}}}, alias=aaa}, 2={cte={with={1={cte={select={1={column={name=b1, table_ref=lll}}, 2={column={name=b2, table_ref=lll}}, 3={column={name=b3, table_ref=lll}}}, from={table={alias=lll, table=tab2}}, where={exists={select={1={literal=1}}, from={table={alias=mmm, table=aaa}}, where={condition={left={column={name=a3, table_ref=mmm}}, right={column={name=b3, table_ref=lll}}, operator==}}, operator=EXISTS}}}, alias=bbb}, 2={cte={select={1={column={name=c1, table_ref=nnn}}, 2={column={name=c2, table_ref=nnn}}, 3={column={name=c3, table_ref=nnn}}}, from={table={alias=nnn, table=tab3}}, where={exists={select={1={literal=1}}, from={table={alias=vvv, table=aaa}}, where={condition={left={column={name=a1, table_ref=vvv}}, right={column={name=c1, table_ref=nnn}}, operator==}}, operator=EXISTS}}}, alias=ccc}}, query={select={1={column={name=b1, table_ref=ppp}, alias=b1}, 2={column={name=c1, table_ref=qqq}, alias=c1}, 3={column={name=c2, table_ref=qqq}, alias=c2}}, from={join={1={table={alias=ppp, table=bbb}}, 2={join=JOIN, on={condition={left={column={name=b1, table_ref=ppp}}, right={column={name=c1, table_ref=qqq}}, operator==}}}, 3={table={alias=qqq, table=ccc}}}}}}, alias=bbb_ccc}}, query={select={1={column={name=b1, table_ref=sss}}, 2={column={name=c2, table_ref=sss}}, 3={column={name=a2, table_ref=ttt}}}, from={join={1={table={alias=sss, table=bbb_ccc}}, 2={join=JOIN, on={condition={left={column={name=a1, table_ref=ttt}}, right={column={name=b1, table_ref=sss}}, operator==}}}, 3={table={alias=ttt, table=aaa}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[a2, b1, c2]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab3={c3=[[@84,274:276='nnn',<381>,6:36]], c1=[[@76,258:260='nnn',<381>,6:20], [@105,352:354='nnn',<381>,6:114], [@117,385:387='qqq',<381>,7:24], [@141,459:461='qqq',<381>,8:47]], c2=[[@80,266:268='nnn',<381>,6:28], [@123,399:401='qqq',<381>,7:38]]}, hhh={a1=[[@10,48:50='hhh',<381>,2:42], [@101,343:345='vvv',<381>,6:105], [@166,541:543='ttt',<381>,12:20]], a2=[[@16,62:64='hhh',<381>,2:56], [@154,493:495='ttt',<381>,10:24]], a3=[[@22,76:78='hhh',<381>,2:70], [@62,219:221='mmm',<381>,5:105]]}, tab2={b2=[[@41,142:144='lll',<381>,5:28]], b3=[[@45,150:152='lll',<381>,5:36], [@66,228:230='lll',<381>,5:114]], b1=[[@37,134:136='lll',<381>,5:20], [@111,371:373='ppp',<381>,7:10], [@137,450:452='ppp',<381>,8:38]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query8={a2=[[@156,497:498='a2',<381>,10:28]], b1=[[@148,481:482='b1',<381>,10:12]], c2=[[@152,489:490='c2',<381>,10:20]]}, query4={unnamed_1=[[@95,319:319='1',<300>,6:81]]}, query6={c3=[[@86,278:279='c3',<381>,6:40]], c1=[[@78,262:263='c1',<381>,6:24], [@117,385:387='qqq',<381>,7:24], [@141,459:461='qqq',<381>,8:47]], c2=[[@82,270:271='c2',<381>,6:32], [@123,399:401='qqq',<381>,7:38]]}, query7={c1=[[@121,395:396='c1',<381>,7:34]], b1=[[@115,381:382='b1',<381>,7:20], [@146,477:479='sss',<381>,10:8], [@170,550:552='sss',<381>,12:29]], c2=[[@127,409:410='c2',<381>,7:48], [@150,485:487='sss',<381>,10:16]]}, query1={unnamed_0=[[@56,195:195='1',<300>,5:81]]}, delete0={a1=[[@14,58:59='a1',<381>,2:52], [@101,343:345='vvv',<381>,6:105], [@166,541:543='ttt',<381>,12:20]], a2=[[@20,72:73='a2',<381>,2:66], [@154,493:495='ttt',<381>,10:24]], a3=[[@26,86:87='a3',<381>,2:80], [@62,219:221='mmm',<381>,5:105]]}, query3={b2=[[@43,146:147='b2',<381>,5:32]], b3=[[@47,154:155='b3',<381>,5:40]], b1=[[@39,138:139='b1',<381>,5:24], [@111,371:373='ppp',<381>,7:10], [@137,450:452='ppp',<381>,8:38]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query8={context_list={aaa=delete0, bbb_ccc=query7, sss=query7, ttt=delete0}, def_query7={context_list={aaa=delete0, bbb=query3, ccc=query6, ppp=query3, qqq=query6}, def_query6={context_list={aaa=delete0, bbb=query3}, query_dictionary={c3=[[@86,278:279='c3',<381>,6:40]], c1=[[@78,262:263='c1',<381>,6:24], [@117,385:387='qqq',<381>,7:24], [@141,459:461='qqq',<381>,8:47]], c2=[[@82,270:271='c2',<381>,6:32], [@123,399:401='qqq',<381>,7:38]]}, table_dictionary={tab3={c3=[[@84,274:276='nnn',<381>,6:36]], c1=[[@76,258:260='nnn',<381>,6:20], [@105,352:354='nnn',<381>,6:114], [@117,385:387='qqq',<381>,7:24], [@141,459:461='qqq',<381>,8:47]], c2=[[@80,266:268='nnn',<381>,6:28], [@123,399:401='qqq',<381>,7:38]]}}, dependent_queries={exists5={query=query4, type=filters}}, filters=[], interface={c3=[{name=c3, table_ref=nnn}], c1=[{name=c1, table_ref=nnn}], c2=[{name=c2, table_ref=nnn}]}, def_query4={context_list={aaa=delete0, bbb=query3, vvv=delete0}, query_dictionary={unnamed_1=[[@95,319:319='1',<300>,6:81]]}, table_dictionary={}, filters=[{name=a1, table_ref=vvv}, {name=c1, table_ref=nnn}], interface={unnamed_1=[]}, table_alias={aaa=delete0, bbb=query3, vvv=delete0}}, table_alias={aaa=delete0, bbb=query3, nnn=tab3}}, table_dictionary={delete0={}}, def_delete0={query_dictionary={a1=[[@14,58:59='a1',<381>,2:52], [@101,343:345='vvv',<381>,6:105], [@166,541:543='ttt',<381>,12:20]], a2=[[@20,72:73='a2',<381>,2:66], [@154,493:495='ttt',<381>,10:24]], a3=[[@26,86:87='a3',<381>,2:80], [@62,219:221='mmm',<381>,5:105]]}, table_dictionary={hhh={a1=[[@10,48:50='hhh',<381>,2:42], [@101,343:345='vvv',<381>,6:105], [@166,541:543='ttt',<381>,12:20]], a2=[[@16,62:64='hhh',<381>,2:56], [@154,493:495='ttt',<381>,10:24]], a3=[[@22,76:78='hhh',<381>,2:70], [@62,219:221='mmm',<381>,5:105]]}}, interface={a1=[{name=a1, table_ref=hhh}], a2=[{name=a2, table_ref=hhh}], a3=[{name=a3, table_ref=hhh}]}}, filters=[{name=b1, table_ref=ppp}, {name=c1, table_ref=qqq}], interface={c1=[{name=c1, table_ref=qqq}], b1=[{name=b1, table_ref=ppp}], c2=[{name=c2, table_ref=qqq}]}, def_query3={context_list={aaa=delete0}, query_dictionary={b2=[[@43,146:147='b2',<381>,5:32]], b3=[[@47,154:155='b3',<381>,5:40]], b1=[[@39,138:139='b1',<381>,5:24], [@111,371:373='ppp',<381>,7:10], [@137,450:452='ppp',<381>,8:38]]}, table_dictionary={tab2={b2=[[@41,142:144='lll',<381>,5:28]], b3=[[@45,150:152='lll',<381>,5:36], [@66,228:230='lll',<381>,5:114]], b1=[[@37,134:136='lll',<381>,5:20], [@111,371:373='ppp',<381>,7:10], [@137,450:452='ppp',<381>,8:38]]}}, def_query1={context_list={aaa=delete0, mmm=delete0}, query_dictionary={unnamed_0=[[@56,195:195='1',<300>,5:81]]}, table_dictionary={}, filters=[{name=a3, table_ref=mmm}, {name=b3, table_ref=lll}], interface={unnamed_0=[]}, table_alias={aaa=delete0, mmm=delete0}}, dependent_queries={exists2={query=query1, type=filters}}, filters=[], interface={b2=[{name=b2, table_ref=lll}], b3=[{name=b3, table_ref=lll}], b1=[{name=b1, table_ref=lll}]}, table_alias={aaa=delete0, lll=tab2}}, query_dictionary={c1=[[@121,395:396='c1',<381>,7:34]], b1=[[@115,381:382='b1',<381>,7:20], [@146,477:479='sss',<381>,10:8], [@170,550:552='sss',<381>,12:29]], c2=[[@127,409:410='c2',<381>,7:48], [@150,485:487='sss',<381>,10:16]]}, table_alias={aaa=delete0, ccc=query6, bbb=query3}}, query_dictionary={a2=[[@156,497:498='a2',<381>,10:28]], b1=[[@148,481:482='b1',<381>,10:12]], c2=[[@152,489:490='c2',<381>,10:20]]}, table_dictionary={}, def_delete0={query_dictionary={a1=[[@14,58:59='a1',<381>,2:52], [@101,343:345='vvv',<381>,6:105], [@166,541:543='ttt',<381>,12:20]], a2=[[@20,72:73='a2',<381>,2:66], [@154,493:495='ttt',<381>,10:24]], a3=[[@26,86:87='a3',<381>,2:80], [@62,219:221='mmm',<381>,5:105]]}, table_dictionary={hhh={a1=[[@10,48:50='hhh',<381>,2:42], [@101,343:345='vvv',<381>,6:105], [@166,541:543='ttt',<381>,12:20]], a2=[[@16,62:64='hhh',<381>,2:56], [@154,493:495='ttt',<381>,10:24]], a3=[[@22,76:78='hhh',<381>,2:70], [@62,219:221='mmm',<381>,5:105]]}}, interface={a1=[{name=a1, table_ref=hhh}], a2=[{name=a2, table_ref=hhh}], a3=[{name=a3, table_ref=hhh}]}}, filters=[{name=a1, table_ref=ttt}, {name=b1, table_ref=sss}], interface={a2=[{name=a2, table_ref=ttt}], b1=[{name=b1, table_ref=sss}], c2=[{name=c2, table_ref=sss}]}, table_alias={aaa=delete0, bbb_ccc=query7}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void nestedWithUnionAliasHeavyDeleteCteVirtualColumnsExemplarParsesWithoutErrors() {
		final String sql = "WITH "
				+ "\n aaa AS (SELECT kkk.a1, kkk.a2, kkk.a3 FROM tab1 AS kkk),"
				+ "\n bbb AS (DELETE FROM hhh AS hhh RETURNING hhh.b1 AS b1, hhh.b2 AS b2, hhh.b3 AS b3),"
				+ "\n ccc AS (SELECT nnn.c1, nnn.c2, nnn.c3 FROM tab3 AS nnn UNION ALL SELECT ooo.b1 AS c1, ooo.b2 AS c2, ooo.b3 AS c3 FROM bbb AS ooo)"
				+ "\n SELECT ppp.a1, qqq.b2, rrr.c2"
				+ "\n FROM aaa AS ppp"
				+ "\n JOIN bbb AS qqq ON ppp.a1 = qqq.b1"
				+ "\n JOIN ccc AS rrr ON qqq.b1 = rrr.c1"
		;

		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runParsertest(sql, parser);
		Assert.assertEquals("AST is wrong", "{SQL={with={1={cte={select={1={column={name=a1, table_ref=kkk}}, 2={column={name=a2, table_ref=kkk}}, 3={column={name=a3, table_ref=kkk}}}, from={table={alias=kkk, table=tab1}}}, alias=aaa}, 2={cte={delete={table={alias=hhh, table=hhh}, returning={1={column={name=b1, table_ref=hhh}, alias=b1}, 2={column={name=b2, table_ref=hhh}, alias=b2}, 3={column={name=b3, table_ref=hhh}, alias=b3}}}}, alias=bbb}, 3={cte={union={1={select={1={column={name=c1, table_ref=nnn}}, 2={column={name=c2, table_ref=nnn}}, 3={column={name=c3, table_ref=nnn}}}, from={table={alias=nnn, table=tab3}}}, 2={union={qualifier=ALL, operator=UNION}}, 3={select={1={column={name=b1, table_ref=ooo}, alias=c1}, 2={column={name=b2, table_ref=ooo}, alias=c2}, 3={column={name=b3, table_ref=ooo}, alias=c3}}, from={table={alias=ooo, table=bbb}}}}}, alias=ccc}}, query={select={1={column={name=a1, table_ref=ppp}}, 2={column={name=b2, table_ref=qqq}}, 3={column={name=c2, table_ref=rrr}}}, from={join={1={table={alias=ppp, table=aaa}}, 2={join=JOIN, on={condition={left={column={name=a1, table_ref=ppp}}, right={column={name=b1, table_ref=qqq}}, operator==}}}, 3={table={alias=qqq, table=bbb}}, 4={join=JOIN, on={condition={left={column={name=b1, table_ref=qqq}}, right={column={name=c1, table_ref=rrr}}, operator==}}}, 5={table={alias=rrr, table=ccc}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[a1, b2, c2]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab3={c3=[[@62,181:183='nnn',<381>,4:32]], c1=[[@54,165:167='nnn',<381>,4:16]], c2=[[@58,173:175='nnn',<381>,4:24]]}, hhh={b2=[[@37,120:122='hhh',<381>,3:56], [@78,236:238='ooo',<381>,4:87], [@99,296:298='qqq',<381>,5:16]], b3=[[@43,134:136='hhh',<381>,3:70], [@84,250:252='ooo',<381>,4:101]], b1=[[@31,106:108='hhh',<381>,3:42], [@72,222:224='ooo',<381>,4:73], [@119,357:359='qqq',<381>,7:29], [@127,384:386='qqq',<381>,8:20]]}, tab1={a1=[[@5,22:24='kkk',<381>,2:16], [@95,288:290='ppp',<381>,5:8], [@115,348:350='ppp',<381>,7:20]], a2=[[@9,30:32='kkk',<381>,2:24]], a3=[[@13,38:40='kkk',<381>,2:32]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query5={a1=[[@97,292:293='a1',<381>,5:12]], b2=[[@101,300:301='b2',<381>,5:20]], c2=[[@105,308:309='c2',<381>,5:28]]}, query0={a1=[[@7,26:27='a1',<381>,2:20], [@95,288:290='ppp',<381>,5:8], [@115,348:350='ppp',<381>,7:20]], a2=[[@11,34:35='a2',<381>,2:28]], a3=[[@15,42:43='a3',<381>,2:36]]}, delete1={b2=[[@41,130:131='b2',<381>,3:66], [@78,236:238='ooo',<381>,4:87], [@99,296:298='qqq',<381>,5:16]], b3=[[@47,144:145='b3',<381>,3:80], [@84,250:252='ooo',<381>,4:101]], b1=[[@35,116:117='b1',<381>,3:52], [@72,222:224='ooo',<381>,4:73], [@119,357:359='qqq',<381>,7:29], [@127,384:386='qqq',<381>,8:20]]}, query2={c3=[[@64,185:186='c3',<381>,4:36]], c1=[[@56,169:170='c1',<381>,4:20]], c2=[[@60,177:178='c2',<381>,4:28]]}, query3={c3=[[@88,260:261='c3',<381>,4:111]], c1=[[@76,232:233='c1',<381>,4:83]], c2=[[@82,246:247='c2',<381>,4:97]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query5={context_list={aaa=query0, bbb=delete1, ccc=union4, ppp=query0, qqq=delete1, rrr=union4}, def_delete1={query_dictionary={b2=[[@41,130:131='b2',<381>,3:66], [@78,236:238='ooo',<381>,4:87], [@99,296:298='qqq',<381>,5:16]], b3=[[@47,144:145='b3',<381>,3:80], [@84,250:252='ooo',<381>,4:101]], b1=[[@35,116:117='b1',<381>,3:52], [@72,222:224='ooo',<381>,4:73], [@119,357:359='qqq',<381>,7:29], [@127,384:386='qqq',<381>,8:20]]}, table_dictionary={hhh={b2=[[@37,120:122='hhh',<381>,3:56], [@78,236:238='ooo',<381>,4:87], [@99,296:298='qqq',<381>,5:16]], b3=[[@43,134:136='hhh',<381>,3:70], [@84,250:252='ooo',<381>,4:101]], b1=[[@31,106:108='hhh',<381>,3:42], [@72,222:224='ooo',<381>,4:73], [@119,357:359='qqq',<381>,7:29], [@127,384:386='qqq',<381>,8:20]]}}, interface={b2=[{name=b2, table_ref=hhh}], b3=[{name=b3, table_ref=hhh}], b1=[{name=b1, table_ref=hhh}]}}, def_union4={context_list={aaa=query0, bbb=delete1}, interface={c3=[{name=c3, table_ref=nnn}, {name=b3, table_ref=ooo}], c1=[{name=c1, table_ref=nnn}, {name=b1, table_ref=ooo}], c2=[{name=c2, table_ref=nnn}, {name=b2, table_ref=ooo}]}, table_alias={aaa=query0, bbb=delete1}, query2={context_list={aaa=query0, bbb=delete1}, query_dictionary={c3=[[@64,185:186='c3',<381>,4:36]], c1=[[@56,169:170='c1',<381>,4:20]], c2=[[@60,177:178='c2',<381>,4:28]]}, table_dictionary={tab3={c3=[[@62,181:183='nnn',<381>,4:32]], c1=[[@54,165:167='nnn',<381>,4:16]], c2=[[@58,173:175='nnn',<381>,4:24]]}}, interface={c3=[{name=c3, table_ref=nnn}], c1=[{name=c1, table_ref=nnn}], c2=[{name=c2, table_ref=nnn}]}, table_alias={aaa=query0, bbb=delete1, nnn=tab3}}, query3={context_list={aaa=query0, bbb=delete1, ooo=delete1}, query_dictionary={c3=[[@88,260:261='c3',<381>,4:111]], c1=[[@76,232:233='c1',<381>,4:83]], c2=[[@82,246:247='c2',<381>,4:97]]}, table_dictionary={}, interface={c3=[{name=b3, table_ref=ooo}], c1=[{name=b1, table_ref=ooo}], c2=[{name=b2, table_ref=ooo}]}, table_alias={aaa=query0, bbb=delete1, ooo=delete1}}}, query_dictionary={a1=[[@97,292:293='a1',<381>,5:12]], b2=[[@101,300:301='b2',<381>,5:20]], c2=[[@105,308:309='c2',<381>,5:28]]}, table_dictionary={delete1={}}, def_query0={query_dictionary={a1=[[@7,26:27='a1',<381>,2:20], [@95,288:290='ppp',<381>,5:8], [@115,348:350='ppp',<381>,7:20]], a2=[[@11,34:35='a2',<381>,2:28]], a3=[[@15,42:43='a3',<381>,2:36]]}, table_dictionary={tab1={a1=[[@5,22:24='kkk',<381>,2:16], [@95,288:290='ppp',<381>,5:8], [@115,348:350='ppp',<381>,7:20]], a2=[[@9,30:32='kkk',<381>,2:24]], a3=[[@13,38:40='kkk',<381>,2:32]]}}, interface={a1=[{name=a1, table_ref=kkk}], a2=[{name=a2, table_ref=kkk}], a3=[{name=a3, table_ref=kkk}]}, table_alias={kkk=tab1}}, filters=[{name=a1, table_ref=ppp}, {name=b1, table_ref=qqq}, {name=c1, table_ref=rrr}], interface={a1=[{name=a1, table_ref=ppp}], b2=[{name=b2, table_ref=qqq}], c2=[{name=c2, table_ref=rrr}]}, table_alias={aaa=query0, ccc=union4, bbb=delete1}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void nestedWithIntersectAliasHeavyDeleteCteVirtualColumnsExemplarParsesWithoutErrors() {
		final String sql = "WITH "
				+ "\n aaa AS (SELECT kkk.a1, kkk.a2, kkk.a3 FROM tab1 AS kkk),"
				+ "\n bbb AS (SELECT lll.b1, lll.b2, lll.b3 FROM tab2 AS lll INTERSECT DISTINCT SELECT mmm.a1 AS b1, mmm.a2 AS b2, mmm.a3 AS b3 FROM aaa AS mmm),"
				+ "\n ccc AS (DELETE FROM hhh AS hhh RETURNING hhh.c1 AS c1, hhh.c2 AS c2, hhh.c3 AS c3)"
				+ "\n SELECT ppp.a1, qqq.b2, rrr.c2"
				+ "\n FROM aaa AS ppp"
				+ "\n JOIN bbb AS qqq ON ppp.a1 = qqq.b1"
				+ "\n JOIN ccc AS rrr ON qqq.b1 = rrr.c1"
		;

		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runParsertest(sql, parser);
		Assert.assertEquals("AST is wrong", "{SQL={with={1={cte={select={1={column={name=a1, table_ref=kkk}}, 2={column={name=a2, table_ref=kkk}}, 3={column={name=a3, table_ref=kkk}}}, from={table={alias=kkk, table=tab1}}}, alias=aaa}, 2={cte={intersect={1={select={1={column={name=b1, table_ref=lll}}, 2={column={name=b2, table_ref=lll}}, 3={column={name=b3, table_ref=lll}}}, from={table={alias=lll, table=tab2}}}, 2={intersect={qualifier=DISTINCT, operator=INTERSECT}}, 3={select={1={column={name=a1, table_ref=mmm}, alias=b1}, 2={column={name=a2, table_ref=mmm}, alias=b2}, 3={column={name=a3, table_ref=mmm}, alias=b3}}, from={table={alias=mmm, table=aaa}}}}}, alias=bbb}, 3={cte={delete={table={alias=hhh, table=hhh}, returning={1={column={name=c1, table_ref=hhh}, alias=c1}, 2={column={name=c2, table_ref=hhh}, alias=c2}, 3={column={name=c3, table_ref=hhh}, alias=c3}}}}, alias=ccc}}, query={select={1={column={name=a1, table_ref=ppp}}, 2={column={name=b2, table_ref=qqq}}, 3={column={name=c2, table_ref=rrr}}}, from={join={1={table={alias=ppp, table=aaa}}, 2={join=JOIN, on={condition={left={column={name=a1, table_ref=ppp}}, right={column={name=b1, table_ref=qqq}}, operator==}}}, 3={table={alias=qqq, table=bbb}}, 4={join=JOIN, on={condition={left={column={name=b1, table_ref=qqq}}, right={column={name=c1, table_ref=rrr}}, operator==}}}, 5={table={alias=rrr, table=ccc}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[a1, b2, c2]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{hhh={c3=[[@88,275:277='hhh',<381>,4:70]], c1=[[@76,247:249='hhh',<381>,4:42], [@131,402:404='rrr',<381>,8:29]], c2=[[@82,261:263='hhh',<381>,4:56], [@103,313:315='rrr',<381>,5:24]]}, tab1={a1=[[@5,22:24='kkk',<381>,2:16], [@44,146:148='mmm',<381>,3:82], [@95,297:299='ppp',<381>,5:8], [@115,357:359='ppp',<381>,7:20]], a2=[[@9,30:32='kkk',<381>,2:24], [@50,160:162='mmm',<381>,3:96]], a3=[[@13,38:40='kkk',<381>,2:32], [@56,174:176='mmm',<381>,3:110]]}, tab2={b2=[[@30,88:90='lll',<381>,3:24]], b3=[[@34,96:98='lll',<381>,3:32]], b1=[[@26,80:82='lll',<381>,3:16]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{delete4={c3=[[@92,285:286='c3',<381>,4:80]], c1=[[@80,257:258='c1',<381>,4:52], [@131,402:404='rrr',<381>,8:29]], c2=[[@86,271:272='c2',<381>,4:66], [@103,313:315='rrr',<381>,5:24]]}, query5={a1=[[@97,301:302='a1',<381>,5:12]], b2=[[@101,309:310='b2',<381>,5:20]], c2=[[@105,317:318='c2',<381>,5:28]]}, query0={a1=[[@7,26:27='a1',<381>,2:20], [@44,146:148='mmm',<381>,3:82], [@95,297:299='ppp',<381>,5:8], [@115,357:359='ppp',<381>,7:20]], a2=[[@11,34:35='a2',<381>,2:28], [@50,160:162='mmm',<381>,3:96]], a3=[[@15,42:43='a3',<381>,2:36], [@56,174:176='mmm',<381>,3:110]]}, query1={b2=[[@32,92:93='b2',<381>,3:28]], b3=[[@36,100:101='b3',<381>,3:36]], b1=[[@28,84:85='b1',<381>,3:20]]}, query2={b2=[[@54,170:171='b2',<381>,3:106]], b3=[[@60,184:185='b3',<381>,3:120]], b1=[[@48,156:157='b1',<381>,3:92]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query5={context_list={aaa=query0, bbb=intersect3, ccc=delete4, ppp=query0, qqq=intersect3, rrr=delete4}, def_delete4={query_dictionary={c3=[[@92,285:286='c3',<381>,4:80]], c1=[[@80,257:258='c1',<381>,4:52], [@131,402:404='rrr',<381>,8:29]], c2=[[@86,271:272='c2',<381>,4:66], [@103,313:315='rrr',<381>,5:24]]}, table_dictionary={hhh={c3=[[@88,275:277='hhh',<381>,4:70]], c1=[[@76,247:249='hhh',<381>,4:42], [@131,402:404='rrr',<381>,8:29]], c2=[[@82,261:263='hhh',<381>,4:56], [@103,313:315='rrr',<381>,5:24]]}}, interface={c3=[{name=c3, table_ref=hhh}], c1=[{name=c1, table_ref=hhh}], c2=[{name=c2, table_ref=hhh}]}}, query_dictionary={a1=[[@97,301:302='a1',<381>,5:12]], b2=[[@101,309:310='b2',<381>,5:20]], c2=[[@105,317:318='c2',<381>,5:28]]}, table_dictionary={delete4={}}, def_intersect3={context_list={aaa=query0}, interface={b2=[{name=b2, table_ref=lll}, {name=a2, table_ref=mmm}], b3=[{name=b3, table_ref=lll}, {name=a3, table_ref=mmm}], b1=[{name=b1, table_ref=lll}, {name=a1, table_ref=mmm}]}, query1={context_list={aaa=query0}, query_dictionary={b2=[[@32,92:93='b2',<381>,3:28]], b3=[[@36,100:101='b3',<381>,3:36]], b1=[[@28,84:85='b1',<381>,3:20]]}, table_dictionary={tab2={b2=[[@30,88:90='lll',<381>,3:24]], b3=[[@34,96:98='lll',<381>,3:32]], b1=[[@26,80:82='lll',<381>,3:16]]}}, interface={b2=[{name=b2, table_ref=lll}], b3=[{name=b3, table_ref=lll}], b1=[{name=b1, table_ref=lll}]}, table_alias={aaa=query0, lll=tab2}}, table_alias={aaa=query0}, query2={context_list={aaa=query0, mmm=query0}, query_dictionary={b2=[[@54,170:171='b2',<381>,3:106]], b3=[[@60,184:185='b3',<381>,3:120]], b1=[[@48,156:157='b1',<381>,3:92]]}, table_dictionary={}, interface={b2=[{name=a2, table_ref=mmm}], b3=[{name=a3, table_ref=mmm}], b1=[{name=a1, table_ref=mmm}]}, table_alias={aaa=query0, mmm=query0}}}, def_query0={query_dictionary={a1=[[@7,26:27='a1',<381>,2:20], [@44,146:148='mmm',<381>,3:82], [@95,297:299='ppp',<381>,5:8], [@115,357:359='ppp',<381>,7:20]], a2=[[@11,34:35='a2',<381>,2:28], [@50,160:162='mmm',<381>,3:96]], a3=[[@15,42:43='a3',<381>,2:36], [@56,174:176='mmm',<381>,3:110]]}, table_dictionary={tab1={a1=[[@5,22:24='kkk',<381>,2:16], [@44,146:148='mmm',<381>,3:82], [@95,297:299='ppp',<381>,5:8], [@115,357:359='ppp',<381>,7:20]], a2=[[@9,30:32='kkk',<381>,2:24], [@50,160:162='mmm',<381>,3:96]], a3=[[@13,38:40='kkk',<381>,2:32], [@56,174:176='mmm',<381>,3:110]]}}, interface={a1=[{name=a1, table_ref=kkk}], a2=[{name=a2, table_ref=kkk}], a3=[{name=a3, table_ref=kkk}]}, table_alias={kkk=tab1}}, filters=[{name=a1, table_ref=ppp}, {name=b1, table_ref=qqq}, {name=c1, table_ref=rrr}], interface={a1=[{name=a1, table_ref=ppp}], b2=[{name=b2, table_ref=qqq}], c2=[{name=c2, table_ref=rrr}]}, table_alias={aaa=query0, ccc=delete4, bbb=intersect3}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void nestedWithJoinAliasHeavyDeleteCteVirtualColumnsExemplarParsesWithoutErrors() {
		final String sql = "WITH "
				+ "\n aaa AS (SELECT kkk.a1, kkk.a2, kkk.a3 FROM tab1 AS kkk),"
				+ "\n bbb AS (DELETE FROM hhh AS hhh RETURNING hhh.b1 AS b1, hhh.b2 AS b2, hhh.b3 AS b3),"
				+ "\n ccc AS (SELECT nnn.c1, nnn.c2, nnn.c3 FROM tab3 AS nnn JOIN bbb AS ooo ON ooo.b3 = nnn.c3)"
				+ "\n SELECT ppp.a1, qqq.b2, rrr.c2"
				+ "\n FROM aaa AS ppp"
				+ "\n JOIN bbb AS qqq ON ppp.a1 = qqq.b1"
				+ "\n JOIN ccc AS rrr ON qqq.b1 = rrr.c1"
		;

		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runParsertest(sql, parser);
		Assert.assertEquals("AST is wrong", "{SQL={with={1={cte={select={1={column={name=a1, table_ref=kkk}}, 2={column={name=a2, table_ref=kkk}}, 3={column={name=a3, table_ref=kkk}}}, from={table={alias=kkk, table=tab1}}}, alias=aaa}, 2={cte={delete={table={alias=hhh, table=hhh}, returning={1={column={name=b1, table_ref=hhh}, alias=b1}, 2={column={name=b2, table_ref=hhh}, alias=b2}, 3={column={name=b3, table_ref=hhh}, alias=b3}}}}, alias=bbb}, 3={cte={select={1={column={name=c1, table_ref=nnn}}, 2={column={name=c2, table_ref=nnn}}, 3={column={name=c3, table_ref=nnn}}}, from={join={1={table={alias=nnn, table=tab3}}, 2={join=JOIN, on={condition={left={column={name=b3, table_ref=ooo}}, right={column={name=c3, table_ref=nnn}}, operator==}}}, 3={table={alias=ooo, table=bbb}}}}}, alias=ccc}}, query={select={1={column={name=a1, table_ref=ppp}}, 2={column={name=b2, table_ref=qqq}}, 3={column={name=c2, table_ref=rrr}}}, from={join={1={table={alias=ppp, table=aaa}}, 2={join=JOIN, on={condition={left={column={name=a1, table_ref=ppp}}, right={column={name=b1, table_ref=qqq}}, operator==}}}, 3={table={alias=qqq, table=bbb}}, 4={join=JOIN, on={condition={left={column={name=b1, table_ref=qqq}}, right={column={name=c1, table_ref=rrr}}, operator==}}}, 5={table={alias=rrr, table=ccc}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[a1, b2, c2]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab3={c3=[[@62,181:183='nnn',<381>,4:32], [@78,233:235='nnn',<381>,4:84]], c1=[[@54,165:167='nnn',<381>,4:16], [@119,354:356='rrr',<381>,8:29]], c2=[[@58,173:175='nnn',<381>,4:24], [@91,265:267='rrr',<381>,5:24]]}, hhh={b2=[[@37,120:122='hhh',<381>,3:56], [@87,257:259='qqq',<381>,5:16]], b3=[[@43,134:136='hhh',<381>,3:70], [@74,224:226='ooo',<381>,4:75]], b1=[[@31,106:108='hhh',<381>,3:42], [@107,318:320='qqq',<381>,7:29], [@115,345:347='qqq',<381>,8:20]]}, tab1={a1=[[@5,22:24='kkk',<381>,2:16], [@83,249:251='ppp',<381>,5:8], [@103,309:311='ppp',<381>,7:20]], a2=[[@9,30:32='kkk',<381>,2:24]], a3=[[@13,38:40='kkk',<381>,2:32]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={a1=[[@7,26:27='a1',<381>,2:20], [@83,249:251='ppp',<381>,5:8], [@103,309:311='ppp',<381>,7:20]], a2=[[@11,34:35='a2',<381>,2:28]], a3=[[@15,42:43='a3',<381>,2:36]]}, delete1={b2=[[@41,130:131='b2',<381>,3:66], [@87,257:259='qqq',<381>,5:16]], b3=[[@47,144:145='b3',<381>,3:80], [@74,224:226='ooo',<381>,4:75]], b1=[[@35,116:117='b1',<381>,3:52], [@107,318:320='qqq',<381>,7:29], [@115,345:347='qqq',<381>,8:20]]}, query2={c3=[[@64,185:186='c3',<381>,4:36]], c1=[[@56,169:170='c1',<381>,4:20], [@119,354:356='rrr',<381>,8:29]], c2=[[@60,177:178='c2',<381>,4:28], [@91,265:267='rrr',<381>,5:24]]}, query3={a1=[[@85,253:254='a1',<381>,5:12]], b2=[[@89,261:262='b2',<381>,5:20]], c2=[[@93,269:270='c2',<381>,5:28]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query3={context_list={aaa=query0, bbb=delete1, ccc=query2, ppp=query0, qqq=delete1, rrr=query2}, def_delete1={query_dictionary={b2=[[@41,130:131='b2',<381>,3:66], [@87,257:259='qqq',<381>,5:16]], b3=[[@47,144:145='b3',<381>,3:80], [@74,224:226='ooo',<381>,4:75]], b1=[[@35,116:117='b1',<381>,3:52], [@107,318:320='qqq',<381>,7:29], [@115,345:347='qqq',<381>,8:20]]}, table_dictionary={hhh={b2=[[@37,120:122='hhh',<381>,3:56], [@87,257:259='qqq',<381>,5:16]], b3=[[@43,134:136='hhh',<381>,3:70], [@74,224:226='ooo',<381>,4:75]], b1=[[@31,106:108='hhh',<381>,3:42], [@107,318:320='qqq',<381>,7:29], [@115,345:347='qqq',<381>,8:20]]}}, interface={b2=[{name=b2, table_ref=hhh}], b3=[{name=b3, table_ref=hhh}], b1=[{name=b1, table_ref=hhh}]}}, query_dictionary={a1=[[@85,253:254='a1',<381>,5:12]], b2=[[@89,261:262='b2',<381>,5:20]], c2=[[@93,269:270='c2',<381>,5:28]]}, table_dictionary={delete1={}}, def_query0={query_dictionary={a1=[[@7,26:27='a1',<381>,2:20], [@83,249:251='ppp',<381>,5:8], [@103,309:311='ppp',<381>,7:20]], a2=[[@11,34:35='a2',<381>,2:28]], a3=[[@15,42:43='a3',<381>,2:36]]}, table_dictionary={tab1={a1=[[@5,22:24='kkk',<381>,2:16], [@83,249:251='ppp',<381>,5:8], [@103,309:311='ppp',<381>,7:20]], a2=[[@9,30:32='kkk',<381>,2:24]], a3=[[@13,38:40='kkk',<381>,2:32]]}}, interface={a1=[{name=a1, table_ref=kkk}], a2=[{name=a2, table_ref=kkk}], a3=[{name=a3, table_ref=kkk}]}, table_alias={kkk=tab1}}, filters=[{name=a1, table_ref=ppp}, {name=b1, table_ref=qqq}, {name=c1, table_ref=rrr}], interface={a1=[{name=a1, table_ref=ppp}], b2=[{name=b2, table_ref=qqq}], c2=[{name=c2, table_ref=rrr}]}, table_alias={aaa=query0, ccc=query2, bbb=delete1}, def_query2={context_list={aaa=query0, bbb=delete1, ooo=delete1}, query_dictionary={c3=[[@64,185:186='c3',<381>,4:36]], c1=[[@56,169:170='c1',<381>,4:20], [@119,354:356='rrr',<381>,8:29]], c2=[[@60,177:178='c2',<381>,4:28], [@91,265:267='rrr',<381>,5:24]]}, table_dictionary={tab3={c3=[[@62,181:183='nnn',<381>,4:32], [@78,233:235='nnn',<381>,4:84]], c1=[[@54,165:167='nnn',<381>,4:16], [@119,354:356='rrr',<381>,8:29]], c2=[[@58,173:175='nnn',<381>,4:24], [@91,265:267='rrr',<381>,5:24]]}}, filters=[{name=b3, table_ref=ooo}, {name=c3, table_ref=nnn}], interface={c3=[{name=c3, table_ref=nnn}], c1=[{name=c1, table_ref=nnn}], c2=[{name=c2, table_ref=nnn}]}, table_alias={aaa=query0, bbb=delete1, ooo=delete1, nnn=tab3}}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void nestedWithScalarHavingAliasHeavyDeleteCteVirtualColumnsExemplarParsesWithoutErrors() {
		final String sql = "WITH "
				+ "\n aaa AS (DELETE FROM hhh AS hhh RETURNING hhh.a1 AS a1, hhh.a2 AS a2, hhh.a3 AS a3),"
				+ "\n bbb AS (SELECT lll.b1, lll.b2, max(lll.b3) AS b3 FROM tab2 AS lll GROUP BY lll.b1, lll.b2"
				+ "\n        HAVING max(lll.b3) >= (SELECT max(mmm.a3) FROM aaa AS mmm)),"
				+ "\n ccc AS (SELECT nnn.c1, nnn.c2, max(nnn.c3) AS c3 FROM tab3 AS nnn GROUP BY nnn.c1, nnn.c2"
				+ "\n        HAVING max(nnn.c3) >= (SELECT max(ooo.b3) FROM bbb AS ooo))"
				+ "\n SELECT qqq.b1, qqq.b2, rrr.c2, ttt.a2"
				+ "\n FROM bbb AS qqq"
				+ "\n JOIN ccc AS rrr ON qqq.b1 = rrr.c1"
				+ "\n JOIN aaa AS ttt ON ttt.a1 = qqq.b1"
		;

		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runParsertest(sql, parser);
		Assert.assertEquals("AST is wrong", "{SQL={with={1={cte={delete={table={alias=hhh, table=hhh}, returning={1={column={name=a1, table_ref=hhh}, alias=a1}, 2={column={name=a2, table_ref=hhh}, alias=a2}, 3={column={name=a3, table_ref=hhh}, alias=a3}}}}, alias=aaa}, 2={cte={select={1={column={name=b1, table_ref=lll}}, 2={column={name=b2, table_ref=lll}}, 3={function={function_name=max, qualifier=null, parameters={column={name=b3, table_ref=lll}}}, alias=b3}}, having={condition={left={function={function_name=max, qualifier=null, parameters={column={name=b3, table_ref=lll}}}}, right={select={1={function={function_name=max, qualifier=null, parameters={column={name=a3, table_ref=mmm}}}}}, from={table={alias=mmm, table=aaa}}}, operator=>=}}, from={table={alias=lll, table=tab2}}, groupby={1={column={name=b1, table_ref=lll}}, 2={column={name=b2, table_ref=lll}}}}, alias=bbb}, 3={cte={select={1={column={name=c1, table_ref=nnn}}, 2={column={name=c2, table_ref=nnn}}, 3={function={function_name=max, qualifier=null, parameters={column={name=c3, table_ref=nnn}}}, alias=c3}}, having={condition={left={function={function_name=max, qualifier=null, parameters={column={name=c3, table_ref=nnn}}}}, right={select={1={function={function_name=max, qualifier=null, parameters={column={name=b3, table_ref=ooo}}}}}, from={table={alias=ooo, table=bbb}}}, operator=>=}}, from={table={alias=nnn, table=tab3}}, groupby={1={column={name=c1, table_ref=nnn}}, 2={column={name=c2, table_ref=nnn}}}}, alias=ccc}}, query={select={1={column={name=b1, table_ref=qqq}}, 2={column={name=b2, table_ref=qqq}}, 3={column={name=c2, table_ref=rrr}}, 4={column={name=a2, table_ref=ttt}}}, from={join={1={table={alias=qqq, table=bbb}}, 2={join=JOIN, on={condition={left={column={name=b1, table_ref=qqq}}, right={column={name=c1, table_ref=rrr}}, operator==}}}, 3={table={alias=rrr, table=ccc}}, 4={join=JOIN, on={condition={left={column={name=a1, table_ref=ttt}}, right={column={name=b1, table_ref=qqq}}, operator==}}}, 5={table={alias=ttt, table=aaa}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[b2, a2, b1, c2]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab3={c3=[[@99,287:289='nnn',<381>,5:36], [@121,361:363='nnn',<381>,6:19]], c1=[[@89,267:269='nnn',<381>,5:16], [@111,327:329='nnn',<381>,5:76], [@169,495:497='rrr',<381>,9:29]], c2=[[@93,275:277='nnn',<381>,5:24], [@115,335:337='nnn',<381>,5:84], [@149,434:436='rrr',<381>,7:24]]}, hhh={a1=[[@10,48:50='hhh',<381>,2:42], [@177,522:524='ttt',<381>,10:20]], a2=[[@16,62:64='hhh',<381>,2:56], [@153,442:444='ttt',<381>,7:32]], a3=[[@22,76:78='hhh',<381>,2:70], [@74,224:226='mmm',<381>,4:42]]}, tab2={b2=[[@37,115:117='lll',<381>,3:24], [@59,175:177='lll',<381>,3:84], [@145,426:428='qqq',<381>,7:16]], b3=[[@43,127:129='lll',<381>,3:36], [@65,201:203='lll',<381>,4:19], [@130,384:386='ooo',<381>,6:42]], b1=[[@33,107:109='lll',<381>,3:16], [@55,167:169='lll',<381>,3:76], [@141,418:420='qqq',<381>,7:8], [@165,486:488='qqq',<381>,9:20], [@181,531:533='qqq',<381>,10:29]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query4={unnamed_1=[[@133,390:390=')',<288>,6:48]]}, query6={c3=[[@104,298:299='c3',<381>,5:47]], c1=[[@91,271:272='c1',<381>,5:20], [@169,495:497='rrr',<381>,9:29]], c2=[[@95,279:280='c2',<381>,5:28], [@149,434:436='rrr',<381>,7:24]]}, query7={b2=[[@147,430:431='b2',<381>,7:20]], a2=[[@155,446:447='a2',<381>,7:36]], b1=[[@143,422:423='b1',<381>,7:12]], c2=[[@151,438:439='c2',<381>,7:28]]}, query1={unnamed_0=[[@77,230:230=')',<288>,4:48]]}, delete0={a1=[[@14,58:59='a1',<381>,2:52], [@177,522:524='ttt',<381>,10:20]], a2=[[@20,72:73='a2',<381>,2:66], [@153,442:444='ttt',<381>,7:32]], a3=[[@26,86:87='a3',<381>,2:80], [@74,224:226='mmm',<381>,4:42]]}, query3={b2=[[@39,119:120='b2',<381>,3:28], [@145,426:428='qqq',<381>,7:16]], b3=[[@48,138:139='b3',<381>,3:47], [@130,384:386='ooo',<381>,6:42]], b1=[[@35,111:112='b1',<381>,3:20], [@141,418:420='qqq',<381>,7:8], [@165,486:488='qqq',<381>,9:20], [@181,531:533='qqq',<381>,10:29]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query7={context_list={aaa=delete0, bbb=query3, ccc=query6, qqq=query3, rrr=query6, ttt=delete0}, query_dictionary={b2=[[@147,430:431='b2',<381>,7:20]], a2=[[@155,446:447='a2',<381>,7:36]], b1=[[@143,422:423='b1',<381>,7:12]], c2=[[@151,438:439='c2',<381>,7:28]]}, def_query6={context_list={aaa=delete0, bbb=query3}, query_dictionary={c3=[[@104,298:299='c3',<381>,5:47]], c1=[[@91,271:272='c1',<381>,5:20], [@169,495:497='rrr',<381>,9:29]], c2=[[@95,279:280='c2',<381>,5:28], [@149,434:436='rrr',<381>,7:24]]}, table_dictionary={tab3={c3=[[@99,287:289='nnn',<381>,5:36], [@121,361:363='nnn',<381>,6:19]], c1=[[@89,267:269='nnn',<381>,5:16], [@111,327:329='nnn',<381>,5:76], [@169,495:497='rrr',<381>,9:29]], c2=[[@93,275:277='nnn',<381>,5:24], [@115,335:337='nnn',<381>,5:84], [@149,434:436='rrr',<381>,7:24]]}}, grouped_by=[{name=c1, table_ref=nnn}, {name=c2, table_ref=nnn}], dependent_queries={predicand5={query=query4, type=filters}}, filters=[{name=c3, table_ref=nnn}], interface={c3=[{name=c3, table_ref=nnn}], c1=[{name=c1, table_ref=nnn}], c2=[{name=c2, table_ref=nnn}]}, def_query4={context_list={aaa=delete0, bbb=query3, ooo=query3}, query_dictionary={unnamed_1=[[@133,390:390=')',<288>,6:48]]}, table_dictionary={}, interface={unnamed_1=[{name=b3, table_ref=ooo}]}, table_alias={aaa=delete0, bbb=query3, ooo=query3}}, table_alias={aaa=delete0, bbb=query3, nnn=tab3}}, table_dictionary={delete0={}}, def_delete0={query_dictionary={a1=[[@14,58:59='a1',<381>,2:52], [@177,522:524='ttt',<381>,10:20]], a2=[[@20,72:73='a2',<381>,2:66], [@153,442:444='ttt',<381>,7:32]], a3=[[@26,86:87='a3',<381>,2:80], [@74,224:226='mmm',<381>,4:42]]}, table_dictionary={hhh={a1=[[@10,48:50='hhh',<381>,2:42], [@177,522:524='ttt',<381>,10:20]], a2=[[@16,62:64='hhh',<381>,2:56], [@153,442:444='ttt',<381>,7:32]], a3=[[@22,76:78='hhh',<381>,2:70], [@74,224:226='mmm',<381>,4:42]]}}, interface={a1=[{name=a1, table_ref=hhh}], a2=[{name=a2, table_ref=hhh}], a3=[{name=a3, table_ref=hhh}]}}, filters=[{name=b1, table_ref=qqq}, {name=c1, table_ref=rrr}, {name=a1, table_ref=ttt}], interface={b2=[{name=b2, table_ref=qqq}], a2=[{name=a2, table_ref=ttt}], b1=[{name=b1, table_ref=qqq}], c2=[{name=c2, table_ref=rrr}]}, table_alias={aaa=delete0, ccc=query6, bbb=query3}, def_query3={context_list={aaa=delete0}, query_dictionary={b2=[[@39,119:120='b2',<381>,3:28], [@145,426:428='qqq',<381>,7:16]], b3=[[@48,138:139='b3',<381>,3:47], [@130,384:386='ooo',<381>,6:42]], b1=[[@35,111:112='b1',<381>,3:20], [@141,418:420='qqq',<381>,7:8], [@165,486:488='qqq',<381>,9:20], [@181,531:533='qqq',<381>,10:29]]}, table_dictionary={tab2={b2=[[@37,115:117='lll',<381>,3:24], [@59,175:177='lll',<381>,3:84], [@145,426:428='qqq',<381>,7:16]], b3=[[@43,127:129='lll',<381>,3:36], [@65,201:203='lll',<381>,4:19], [@130,384:386='ooo',<381>,6:42]], b1=[[@33,107:109='lll',<381>,3:16], [@55,167:169='lll',<381>,3:76], [@141,418:420='qqq',<381>,7:8], [@165,486:488='qqq',<381>,9:20], [@181,531:533='qqq',<381>,10:29]]}}, grouped_by=[{name=b1, table_ref=lll}, {name=b2, table_ref=lll}], def_query1={context_list={aaa=delete0, mmm=delete0}, query_dictionary={unnamed_0=[[@77,230:230=')',<288>,4:48]]}, table_dictionary={}, interface={unnamed_0=[{name=a3, table_ref=mmm}]}, table_alias={aaa=delete0, mmm=delete0}}, dependent_queries={predicand2={query=query1, type=filters}}, filters=[{name=b3, table_ref=lll}], interface={b2=[{name=b2, table_ref=lll}], b3=[{name=b3, table_ref=lll}], b1=[{name=b1, table_ref=lll}]}, table_alias={aaa=delete0, lll=tab2}}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void nestedWithScalarWhereAliasHeavyDeleteCteVirtualColumnsExemplarParsesWithoutErrors() {
		final String sql = "WITH "
				+ "\n aaa AS (SELECT kkk.a1, kkk.a2, kkk.a3 FROM tab1 AS kkk),"
				+ "\n bbb AS (DELETE FROM hhh AS hhh RETURNING hhh.b1 AS b1, hhh.b2 AS b2, hhh.b3 AS b3),"
				+ "\n ccc AS (SELECT nnn.c1, nnn.c2, nnn.c3 FROM tab3 AS nnn"
				+ "\n        WHERE nnn.c3 = (SELECT max(ooo.b3) FROM bbb AS ooo WHERE ooo.b1 = nnn.c1))"
				+ "\n SELECT ppp.a1, qqq.b2, rrr.c2"
				+ "\n FROM aaa AS ppp"
				+ "\n JOIN bbb AS qqq ON ppp.a1 = qqq.b1"
				+ "\n JOIN ccc AS rrr ON qqq.b1 = rrr.c1"
		;

		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runParsertest(sql, parser);
		Assert.assertEquals("AST is wrong", "{SQL={with={1={cte={select={1={column={name=a1, table_ref=kkk}}, 2={column={name=a2, table_ref=kkk}}, 3={column={name=a3, table_ref=kkk}}}, from={table={alias=kkk, table=tab1}}}, alias=aaa}, 2={cte={delete={table={alias=hhh, table=hhh}, returning={1={column={name=b1, table_ref=hhh}, alias=b1}, 2={column={name=b2, table_ref=hhh}, alias=b2}, 3={column={name=b3, table_ref=hhh}, alias=b3}}}}, alias=bbb}, 3={cte={select={1={column={name=c1, table_ref=nnn}}, 2={column={name=c2, table_ref=nnn}}, 3={column={name=c3, table_ref=nnn}}}, from={table={alias=nnn, table=tab3}}, where={condition={left={column={name=c3, table_ref=nnn}}, right={select={1={function={function_name=max, qualifier=null, parameters={column={name=b3, table_ref=ooo}}}}}, from={table={alias=ooo, table=bbb}}, where={condition={left={column={name=b1, table_ref=ooo}}, right={column={name=c1, table_ref=nnn}}, operator==}}}, operator==}}}, alias=ccc}}, query={select={1={column={name=a1, table_ref=ppp}}, 2={column={name=b2, table_ref=qqq}}, 3={column={name=c2, table_ref=rrr}}}, from={join={1={table={alias=ppp, table=aaa}}, 2={join=JOIN, on={condition={left={column={name=a1, table_ref=ppp}}, right={column={name=b1, table_ref=qqq}}, operator==}}}, 3={table={alias=qqq, table=bbb}}, 4={join=JOIN, on={condition={left={column={name=b1, table_ref=qqq}}, right={column={name=c1, table_ref=rrr}}, operator==}}}, 5={table={alias=rrr, table=ccc}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[a1, b2, c2]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab3={c3=[[@62,181:183='nnn',<381>,4:32], [@70,219:221='nnn',<381>,5:14]], c1=[[@54,165:167='nnn',<381>,4:16], [@91,279:281='nnn',<381>,5:74], [@133,401:403='rrr',<381>,9:29]], c2=[[@58,173:175='nnn',<381>,4:24], [@105,312:314='rrr',<381>,6:24]]}, hhh={b2=[[@37,120:122='hhh',<381>,3:56], [@101,304:306='qqq',<381>,6:16]], b3=[[@43,134:136='hhh',<381>,3:70], [@78,240:242='ooo',<381>,5:35]], b1=[[@31,106:108='hhh',<381>,3:42], [@87,270:272='ooo',<381>,5:65], [@121,365:367='qqq',<381>,8:29], [@129,392:394='qqq',<381>,9:20]]}, tab1={a1=[[@5,22:24='kkk',<381>,2:16], [@97,296:298='ppp',<381>,6:8], [@117,356:358='ppp',<381>,8:20]], a2=[[@9,30:32='kkk',<381>,2:24]], a3=[[@13,38:40='kkk',<381>,2:32]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query4={c3=[[@64,185:186='c3',<381>,4:36]], c1=[[@56,169:170='c1',<381>,4:20], [@133,401:403='rrr',<381>,9:29]], c2=[[@60,177:178='c2',<381>,4:28], [@105,312:314='rrr',<381>,6:24]]}, query5={a1=[[@99,300:301='a1',<381>,6:12]], b2=[[@103,308:309='b2',<381>,6:20]], c2=[[@107,316:317='c2',<381>,6:28]]}, query0={a1=[[@7,26:27='a1',<381>,2:20], [@97,296:298='ppp',<381>,6:8], [@117,356:358='ppp',<381>,8:20]], a2=[[@11,34:35='a2',<381>,2:28]], a3=[[@15,42:43='a3',<381>,2:36]]}, delete1={b2=[[@41,130:131='b2',<381>,3:66], [@101,304:306='qqq',<381>,6:16]], b3=[[@47,144:145='b3',<381>,3:80], [@78,240:242='ooo',<381>,5:35]], b1=[[@35,116:117='b1',<381>,3:52], [@87,270:272='ooo',<381>,5:65], [@121,365:367='qqq',<381>,8:29], [@129,392:394='qqq',<381>,9:20]]}, query2={unnamed_0=[[@81,246:246=')',<288>,5:41]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query5={context_list={aaa=query0, bbb=delete1, ccc=query4, ppp=query0, qqq=delete1, rrr=query4}, def_delete1={query_dictionary={b2=[[@41,130:131='b2',<381>,3:66], [@101,304:306='qqq',<381>,6:16]], b3=[[@47,144:145='b3',<381>,3:80], [@78,240:242='ooo',<381>,5:35]], b1=[[@35,116:117='b1',<381>,3:52], [@87,270:272='ooo',<381>,5:65], [@121,365:367='qqq',<381>,8:29], [@129,392:394='qqq',<381>,9:20]]}, table_dictionary={hhh={b2=[[@37,120:122='hhh',<381>,3:56], [@101,304:306='qqq',<381>,6:16]], b3=[[@43,134:136='hhh',<381>,3:70], [@78,240:242='ooo',<381>,5:35]], b1=[[@31,106:108='hhh',<381>,3:42], [@87,270:272='ooo',<381>,5:65], [@121,365:367='qqq',<381>,8:29], [@129,392:394='qqq',<381>,9:20]]}}, interface={b2=[{name=b2, table_ref=hhh}], b3=[{name=b3, table_ref=hhh}], b1=[{name=b1, table_ref=hhh}]}}, query_dictionary={a1=[[@99,300:301='a1',<381>,6:12]], b2=[[@103,308:309='b2',<381>,6:20]], c2=[[@107,316:317='c2',<381>,6:28]]}, table_dictionary={delete1={}}, def_query0={query_dictionary={a1=[[@7,26:27='a1',<381>,2:20], [@97,296:298='ppp',<381>,6:8], [@117,356:358='ppp',<381>,8:20]], a2=[[@11,34:35='a2',<381>,2:28]], a3=[[@15,42:43='a3',<381>,2:36]]}, table_dictionary={tab1={a1=[[@5,22:24='kkk',<381>,2:16], [@97,296:298='ppp',<381>,6:8], [@117,356:358='ppp',<381>,8:20]], a2=[[@9,30:32='kkk',<381>,2:24]], a3=[[@13,38:40='kkk',<381>,2:32]]}}, interface={a1=[{name=a1, table_ref=kkk}], a2=[{name=a2, table_ref=kkk}], a3=[{name=a3, table_ref=kkk}]}, table_alias={kkk=tab1}}, filters=[{name=a1, table_ref=ppp}, {name=b1, table_ref=qqq}, {name=c1, table_ref=rrr}], interface={a1=[{name=a1, table_ref=ppp}], b2=[{name=b2, table_ref=qqq}], c2=[{name=c2, table_ref=rrr}]}, def_query4={context_list={aaa=query0, bbb=delete1}, query_dictionary={c3=[[@64,185:186='c3',<381>,4:36]], c1=[[@56,169:170='c1',<381>,4:20], [@133,401:403='rrr',<381>,9:29]], c2=[[@60,177:178='c2',<381>,4:28], [@105,312:314='rrr',<381>,6:24]]}, table_dictionary={tab3={c3=[[@62,181:183='nnn',<381>,4:32], [@70,219:221='nnn',<381>,5:14]], c1=[[@54,165:167='nnn',<381>,4:16], [@91,279:281='nnn',<381>,5:74], [@133,401:403='rrr',<381>,9:29]], c2=[[@58,173:175='nnn',<381>,4:24], [@105,312:314='rrr',<381>,6:24]]}}, dependent_queries={predicand3={query=query2, type=filters}}, filters=[{name=c3, table_ref=nnn}], interface={c3=[{name=c3, table_ref=nnn}], c1=[{name=c1, table_ref=nnn}], c2=[{name=c2, table_ref=nnn}]}, table_alias={aaa=query0, bbb=delete1, nnn=tab3}, def_query2={context_list={aaa=query0, bbb=delete1, ooo=delete1}, query_dictionary={unnamed_0=[[@81,246:246=')',<288>,5:41]]}, table_dictionary={}, filters=[{name=b1, table_ref=ooo}, {name=c1, table_ref=nnn}], interface={unnamed_0=[{name=b3, table_ref=ooo}]}, table_alias={aaa=query0, bbb=delete1, ooo=delete1}}}, table_alias={aaa=query0, ccc=query4, bbb=delete1}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void nestedWithScalarSelectListAliasHeavyDeleteCteVirtualColumnsExemplarParsesWithoutErrors() {
		final String sql = "WITH "
				+ "\n aaa AS (SELECT kkk.a1, kkk.a2, kkk.a3 FROM tab1 AS kkk),"
				+ "\n bbb AS (SELECT lll.b1, lll.b2, (SELECT max(mmm.a3) FROM aaa AS mmm) AS b3 FROM tab2 AS lll),"
				+ "\n ccc AS (DELETE FROM hhh AS hhh RETURNING hhh.c1 AS c1, hhh.c2 AS c2, hhh.c3 AS c3)"
				+ "\n SELECT ppp.a1, qqq.b2, rrr.c2"
				+ "\n FROM aaa AS ppp"
				+ "\n JOIN bbb AS qqq ON ppp.a1 = qqq.b1"
				+ "\n JOIN ccc AS rrr ON qqq.b1 = rrr.c1"
		;

		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runParsertest(sql, parser);
		Assert.assertEquals("AST is wrong", "{SQL={with={1={cte={select={1={column={name=a1, table_ref=kkk}}, 2={column={name=a2, table_ref=kkk}}, 3={column={name=a3, table_ref=kkk}}}, from={table={alias=kkk, table=tab1}}}, alias=aaa}, 2={cte={select={1={column={name=b1, table_ref=lll}}, 2={column={name=b2, table_ref=lll}}, 3={lookup={from={table={alias=mmm, table=aaa}}, select={1={function={function_name=max, qualifier=null, parameters={column={name=a3, table_ref=mmm}}}}}}, alias=b3}}, from={table={alias=lll, table=tab2}}}, alias=bbb}, 3={cte={delete={table={alias=hhh, table=hhh}, returning={1={column={name=c1, table_ref=hhh}, alias=c1}, 2={column={name=c2, table_ref=hhh}, alias=c2}, 3={column={name=c3, table_ref=hhh}, alias=c3}}}}, alias=ccc}}, query={select={1={column={name=a1, table_ref=ppp}}, 2={column={name=b2, table_ref=qqq}}, 3={column={name=c2, table_ref=rrr}}}, from={join={1={table={alias=ppp, table=aaa}}, 2={join=JOIN, on={condition={left={column={name=a1, table_ref=ppp}}, right={column={name=b1, table_ref=qqq}}, operator==}}}, 3={table={alias=qqq, table=bbb}}, 4={join=JOIN, on={condition={left={column={name=b1, table_ref=qqq}}, right={column={name=c1, table_ref=rrr}}, operator==}}}, 5={table={alias=rrr, table=ccc}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[a1, b2, c2]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{hhh={c3=[[@76,228:230='hhh',<381>,4:70]], c1=[[@64,200:202='hhh',<381>,4:42], [@119,355:357='rrr',<381>,8:29]], c2=[[@70,214:216='hhh',<381>,4:56], [@91,266:268='rrr',<381>,5:24]]}, tab1={a1=[[@5,22:24='kkk',<381>,2:16], [@83,250:252='ppp',<381>,5:8], [@103,310:312='ppp',<381>,7:20]], a2=[[@9,30:32='kkk',<381>,2:24]], a3=[[@13,38:40='kkk',<381>,2:32], [@38,108:110='mmm',<381>,3:44]]}, tab2={b2=[[@30,88:90='lll',<381>,3:24], [@87,258:260='qqq',<381>,5:16]], b1=[[@26,80:82='lll',<381>,3:16], [@107,319:321='qqq',<381>,7:29], [@115,346:348='qqq',<381>,8:20]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{delete4={c3=[[@80,238:239='c3',<381>,4:80]], c1=[[@68,210:211='c1',<381>,4:52], [@119,355:357='rrr',<381>,8:29]], c2=[[@74,224:225='c2',<381>,4:66], [@91,266:268='rrr',<381>,5:24]]}, query5={a1=[[@85,254:255='a1',<381>,5:12]], b2=[[@89,262:263='b2',<381>,5:20]], c2=[[@93,270:271='c2',<381>,5:28]]}, query0={a1=[[@7,26:27='a1',<381>,2:20], [@83,250:252='ppp',<381>,5:8], [@103,310:312='ppp',<381>,7:20]], a2=[[@11,34:35='a2',<381>,2:28]], a3=[[@15,42:43='a3',<381>,2:36], [@38,108:110='mmm',<381>,3:44]]}, query1={unnamed_0=[[@41,114:114=')',<288>,3:50]]}, query3={b2=[[@32,92:93='b2',<381>,3:28], [@87,258:260='qqq',<381>,5:16]], b3=[[@48,136:137='b3',<381>,3:72]], b1=[[@28,84:85='b1',<381>,3:20], [@107,319:321='qqq',<381>,7:29], [@115,346:348='qqq',<381>,8:20]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query5={context_list={aaa=query0, bbb=query3, ccc=delete4, ppp=query0, qqq=query3, rrr=delete4}, def_delete4={query_dictionary={c3=[[@80,238:239='c3',<381>,4:80]], c1=[[@68,210:211='c1',<381>,4:52], [@119,355:357='rrr',<381>,8:29]], c2=[[@74,224:225='c2',<381>,4:66], [@91,266:268='rrr',<381>,5:24]]}, table_dictionary={hhh={c3=[[@76,228:230='hhh',<381>,4:70]], c1=[[@64,200:202='hhh',<381>,4:42], [@119,355:357='rrr',<381>,8:29]], c2=[[@70,214:216='hhh',<381>,4:56], [@91,266:268='rrr',<381>,5:24]]}}, interface={c3=[{name=c3, table_ref=hhh}], c1=[{name=c1, table_ref=hhh}], c2=[{name=c2, table_ref=hhh}]}}, query_dictionary={a1=[[@85,254:255='a1',<381>,5:12]], b2=[[@89,262:263='b2',<381>,5:20]], c2=[[@93,270:271='c2',<381>,5:28]]}, table_dictionary={delete4={}}, def_query0={query_dictionary={a1=[[@7,26:27='a1',<381>,2:20], [@83,250:252='ppp',<381>,5:8], [@103,310:312='ppp',<381>,7:20]], a2=[[@11,34:35='a2',<381>,2:28]], a3=[[@15,42:43='a3',<381>,2:36], [@38,108:110='mmm',<381>,3:44]]}, table_dictionary={tab1={a1=[[@5,22:24='kkk',<381>,2:16], [@83,250:252='ppp',<381>,5:8], [@103,310:312='ppp',<381>,7:20]], a2=[[@9,30:32='kkk',<381>,2:24]], a3=[[@13,38:40='kkk',<381>,2:32], [@38,108:110='mmm',<381>,3:44]]}}, interface={a1=[{name=a1, table_ref=kkk}], a2=[{name=a2, table_ref=kkk}], a3=[{name=a3, table_ref=kkk}]}, table_alias={kkk=tab1}}, filters=[{name=a1, table_ref=ppp}, {name=b1, table_ref=qqq}, {name=c1, table_ref=rrr}], interface={a1=[{name=a1, table_ref=ppp}], b2=[{name=b2, table_ref=qqq}], c2=[{name=c2, table_ref=rrr}]}, table_alias={aaa=query0, ccc=delete4, bbb=query3}, def_query3={context_list={aaa=query0}, query_dictionary={b2=[[@32,92:93='b2',<381>,3:28], [@87,258:260='qqq',<381>,5:16]], b3=[[@48,136:137='b3',<381>,3:72]], b1=[[@28,84:85='b1',<381>,3:20], [@107,319:321='qqq',<381>,7:29], [@115,346:348='qqq',<381>,8:20]]}, table_dictionary={tab2={b2=[[@30,88:90='lll',<381>,3:24], [@87,258:260='qqq',<381>,5:16]], b1=[[@26,80:82='lll',<381>,3:16], [@107,319:321='qqq',<381>,7:29], [@115,346:348='qqq',<381>,8:20]]}}, def_query1={context_list={aaa=query0, mmm=query0}, query_dictionary={unnamed_0=[[@41,114:114=')',<288>,3:50]]}, table_dictionary={}, interface={unnamed_0=[{name=a3, table_ref=mmm}]}, table_alias={aaa=query0, mmm=query0}}, dependent_queries={predicand2={query=query1, type=interface}}, interface={b2=[{name=b2, table_ref=lll}], b3=[{name=a3, table_ref=mmm}], b1=[{name=b1, table_ref=lll}]}, table_alias={aaa=query0, lll=tab2}}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void nestedWithDepth2ShadowedParentCteEmitsWarningAndQualifiedAliasFatal() {
		final String sql = "WITH "
				+ "\n aaa AS (SELECT tab1.col1 AS keep_col FROM tab1),"
				+ "\n bbb AS ("
				+ "\n   WITH aaa AS (SELECT tab2.col2 AS local_col FROM tab2)"
				+ "\n   SELECT aaa.keep_col, aaa.missing AS should_fail"
				+ "\n   FROM aaa"
				+ "\n )"
				+ "\n SELECT outer_aaa.keep_col"
				+ "\n FROM aaa AS outer_aaa"
				+ "\n JOIN bbb ON 1 = 1";

		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runParsertest(sql, parser);
		Snippet snippet = extractor.getSnippet();

		assertFatalDiagnosticAtPosition(
				snippet,
				"QUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIAS",
				"Qualified column 'keep_col' at (l:5 c:10) was not found in output interface of query alias 'aaa'.",
				"aaa.keep_col",
				5,
				10);
		assertFatalDiagnosticAtPosition(
				snippet,
				"QUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIAS",
				"Qualified column 'missing' at (l:5 c:24) was not found in output interface of query alias 'aaa'.",
				"aaa.missing",
				5,
				24);
	}

	@Test
	public void nestedWithDepth3SkipLevelShadowEmitsWarningAndQueryAliasFatals() {
		final String sql = "WITH "
				+ "\n aaa AS (SELECT tab1.col1 AS inherited_col FROM tab1),"
				+ "\n outer_mid AS ("
				+ "\n   WITH middle_only AS (SELECT tab2.col2 AS middle_col FROM tab2),"
				+ "\n   leaf_q AS ("
				+ "\n     WITH aaa AS (SELECT tab3.col3 AS local_col FROM tab3)"
				+ "\n     SELECT aaa.inherited_col AS missing_from_shadow,"
				+ "\n            derived_alias.inherited_col AS missing_from_derived"
				+ "\n     FROM aaa"
				+ "\n     JOIN (SELECT tab4.col4 AS other_col FROM tab4) AS derived_alias ON 1 = 1"
				+ "\n   )"
				+ "\n   SELECT *"
				+ "\n   FROM leaf_q"
				+ "\n )"
				+ "\n SELECT *"
				+ "\n FROM outer_mid";

		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runParsertest(sql, parser);
		Snippet snippet = extractor.getSnippet();

		assertDiagnosticCountBySeverity(
				snippet,
				"SHADOWED_PARENT_CTE_NAME",
				ParseDiagnostic.Severity.WARNING,
				"shadows inherited CTE",
				"aaa",
				1);
		assertFatalDiagnosticAtPosition(
				snippet,
				"QUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIAS",
				"Qualified column 'inherited_col' at (l:7 c:12) was not found in output interface of query alias 'aaa'.",
				"aaa.inherited_col",
				7,
				12);
		assertFatalDiagnosticAtPosition(
				snippet,
				"QUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIAS",
				"Qualified column 'inherited_col' at (l:8 c:12) was not found in output interface of query alias 'derived_alias'.",
				"derived_alias.inherited_col",
				8,
				12);
	}

	@Test
	public void nestedWithDepth4SkipLevelShadowWithTableAndSubqueryAliasConflicts() {
		final String sql = "WITH "
				+ "\n aaa AS (SELECT tab1.col1 AS inherited_col FROM tab1),"
				+ "\n lvl2 AS ("
				+ "\n   WITH lvl2_only AS (SELECT tab2.col2 AS lvl2_col FROM tab2),"
				+ "\n   lvl3 AS ("
				+ "\n     WITH lvl3_only AS (SELECT tab3.col3 AS lvl3_col FROM tab3),"
				+ "\n     lvl4 AS ("
				+ "\n       WITH aaa AS (SELECT tab4.col4 AS local_col FROM tab4)"
				+ "\n       SELECT shadow_cte.inherited_col AS missing_from_shadow,"
				+ "\n              aaa.inherited_col AS missing_from_table_alias,"
				+ "\n              derived_alias.inherited_col AS missing_from_derived"
				+ "\n       FROM aaa AS shadow_cte"
				+ "\n       JOIN tab5 AS aaa ON 1 = 1"
				+ "\n       JOIN (SELECT tab6.col6 AS sub_only FROM tab6) AS derived_alias ON 1 = 1"
				+ "\n     )"
				+ "\n     SELECT *"
				+ "\n     FROM lvl4"
				+ "\n   )"
				+ "\n   SELECT *"
				+ "\n   FROM lvl3"
				+ "\n )"
				+ "\n SELECT *"
				+ "\n FROM lvl2";

		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runParsertest(sql, parser);
		Snippet snippet = extractor.getSnippet();

		assertDiagnosticCountBySeverity(
				snippet,
				"SHADOWED_PARENT_CTE_NAME",
				ParseDiagnostic.Severity.WARNING,
				"shadows inherited CTE",
				"aaa",
				1);
		assertFatalDiagnosticCount(
				snippet,
				"QUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIAS",
				"query alias 'shadow_cte'",
				"shadow_cte.inherited_col",
				1);
		assertFatalDiagnosticCount(
				snippet,
				"QUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIAS",
				"query alias 'derived_alias'",
				"derived_alias.inherited_col",
				1);
		// aaa.inherited_col binds to local FROM alias tab5 AS aaa, not the shadowed CTE context_list entry.
	}


	
	@SuppressWarnings("unchecked")
	private Map<String, Object> topScope(SqlParseEventWalker extractor, String topKey) {
		Map<String, Object> symbolTable = extractor.getSymbolTable();
		Assert.assertTrue("Expected top scope key " + topKey, symbolTable.containsKey(topKey));
		return (Map<String, Object>) symbolTable.get(topKey);
	}

	@SuppressWarnings("unchecked")
	private Map<String, Object> scopeAt(Map<String, Object> start, String... keys) {
		Map<String, Object> current = start;
		for (String key : keys) {
			Assert.assertTrue("Expected nested scope " + key, current.containsKey(key));
			current = (Map<String, Object>) current.get(key);
		}
		return current;
	}

	@SuppressWarnings("unchecked")
	private void assertCteEntry(Map<String, Object> scope, String cteName, String expectedQueryRef) {
		Assert.assertTrue("Expected context_list map in scope", scope.containsKey("context_list"));
		Map<String, Object> cteList = (Map<String, Object>) scope.get("context_list");
		Assert.assertNotNull("Expected non-null context_list", cteList);
		Assert.assertEquals("Unexpected CTE mapping for " + cteName, expectedQueryRef, cteList.get(cteName));
	}

	@SuppressWarnings("unchecked")
	private void assertQueryLink(Map<String, Object> scope, String linkKey, String expectedQueryRef) {
		Object dependentQueriesObj = scope.get("dependent_queries");
		if (dependentQueriesObj instanceof Map<?, ?>) {
			Map<String, Object> dependentQueries = (Map<String, Object>) dependentQueriesObj;
			Object linkEntryObj = dependentQueries.get(linkKey);
			if (linkEntryObj instanceof Map<?, ?>) {
				Map<String, Object> linkEntry = (Map<String, Object>) linkEntryObj;
				Assert.assertEquals(
						"Unexpected query link for " + linkKey,
						expectedQueryRef,
						linkEntry.get("query"));
				return;
			}
		}
		// Legacy flat format: existsN=queryM (or predicandN / in_listN) at scope top level.
		Assert.assertEquals("Unexpected query link for " + linkKey, expectedQueryRef, scope.get(linkKey));
	}

	@SuppressWarnings("unchecked")
	private void assertQueryRefAtFilter(Map<String, Object> scope, int filterIndex, String expectedQueryRef) {
		Assert.assertTrue("Expected filters list", scope.containsKey("filters"));
		List<Object> filters = (List<Object>) scope.get("filters");
		Assert.assertTrue("Expected filter index " + filterIndex, filters.size() > filterIndex);
		Map<String, Object> filterEntry = (Map<String, Object>) filters.get(filterIndex);
		Assert.assertEquals("Unexpected query reference in filter", expectedQueryRef, filterEntry.get("query"));
	}

	@SuppressWarnings("unchecked")
	private void assertQueryRefAtInterface(Map<String, Object> scope, String interfaceColumn, int entryIndex,
			String expectedQueryRef) {
		Assert.assertTrue("Expected interface map", scope.containsKey("interface"));
		Map<String, Object> interfaceMap = (Map<String, Object>) scope.get("interface");
		Assert.assertTrue("Expected interface entry for " + interfaceColumn, interfaceMap.containsKey(interfaceColumn));
		List<Object> entries = (List<Object>) interfaceMap.get(interfaceColumn);
		Assert.assertTrue("Expected interface index " + entryIndex + " for " + interfaceColumn,
				entries.size() > entryIndex);
		Map<String, Object> entry = (Map<String, Object>) entries.get(entryIndex);
		Assert.assertEquals("Unexpected query reference in interface for " + interfaceColumn, expectedQueryRef,
				entry.get("query"));
	}

	private void assertAliasBoundaryVisibility(String symbolSnapshot, String... aliases) {
		Assert.assertTrue("Expected top-level interface entries", symbolSnapshot.contains("interface={"));
		Assert.assertTrue("Expected table alias mapping", symbolSnapshot.contains("table_alias={"));
		for (String alias : aliases) {
			Assert.assertTrue(
					"Expected alias visibility for " + alias,
					symbolSnapshot.contains(alias + "=") || symbolSnapshot.contains("table_ref=" + alias));
		}
	}


	// END OF NESTED WITH TESTS

	// OTHER JOIN TYPES AND MULTI-TABLE TESTS
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
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={col1=[[@11,46:49='col1',<381>,1:46]]}, tab2={col2=[[@20,85:88='col2',<381>,1:85]]}}",
					extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={col1=[[@11,46:49='col1',<381>,1:46], [@1,8:13='source',<381>,1:8]]}, query1={col2=[[@20,85:88='col2',<381>,1:85], [@5,21:26='target',<381>,1:21]]}, query2={col2=[[@7,28:31='col2',<381>,1:28]], col1=[[@3,15:18='col1',<381>,1:15]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query2={query_dictionary={col2=[[@7,28:31='col2',<381>,1:28]], col1=[[@3,15:18='col1',<381>,1:15]]}, table_dictionary={}, def_query1={query_dictionary={col2=[[@20,85:88='col2',<381>,1:85], [@5,21:26='target',<381>,1:21]]}, table_dictionary={tab2={col2=[[@20,85:88='col2',<381>,1:85]]}}, interface={col2=[{name=col2, table_ref=tab2}]}}, def_query0={query_dictionary={col1=[[@11,46:49='col1',<381>,1:46], [@1,8:13='source',<381>,1:8]]}, table_dictionary={tab1={col1=[[@11,46:49='col1',<381>,1:46]]}}, interface={col1=[{name=col1, table_ref=tab1}]}}, interface={col2=[{name=col2, table_ref=target}], col1=[{name=col1, table_ref=source}]}, table_alias={source=query0, target=query1}}}",
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
		Assert.assertEquals("Query Column Dictionary is wrong", "{values0={*=[[@1,8:8='*',<291>,1:8]], $1=[[@5,23:23='(',<287>,1:23], [@18,61:61='(',<287>,1:61]], $2=[[@5,23:23='(',<287>,1:23], [@18,61:61='(',<287>,1:61]], $3=[[@5,23:23='(',<287>,1:23], [@18,61:61='(',<287>,1:61]]}, values1={*=[[@1,8:8='*',<291>,1:8]], $1=[[@18,61:61='(',<287>,1:61]], $2=[[@18,61:61='(',<287>,1:61]], $3=[[@18,61:61='(',<287>,1:61]]}, query2={*=[[@1,8:8='*',<291>,1:8]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query2={query_dictionary={*=[[@1,8:8='*',<291>,1:8]]}, def_values1={query_dictionary={*=[[@1,8:8='*',<291>,1:8]], $1=[[@18,61:61='(',<287>,1:61]], $2=[[@18,61:61='(',<287>,1:61]], $3=[[@18,61:61='(',<287>,1:61]]}, table_dictionary={}, interface={$1=[], $2=[], $3=[]}}, table_dictionary={}, def_values0={query_dictionary={*=[[@1,8:8='*',<291>,1:8]], $1=[[@5,23:23='(',<287>,1:23], [@18,61:61='(',<287>,1:61]], $2=[[@5,23:23='(',<287>,1:23], [@18,61:61='(',<287>,1:61]], $3=[[@5,23:23='(',<287>,1:23], [@18,61:61='(',<287>,1:61]]}, table_dictionary={}, interface={$1=[], $2=[], $3=[]}}, interface={*=[{name=*, table_ref=*}]}, table_alias={source=values0, target=values1}}}",
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
		Assert.assertEquals("Query Column Dictionary is wrong", "{values0={col2=[[@24,78:81='col2',<381>,1:78]], col3=[[@26,84:87='col3',<381>,1:84]], col1=[[@22,72:75='col1',<381>,1:72], [@1,8:13='source',<381>,1:8]]}, values1={c3=[[@44,134:135='c3',<381>,1:134]], c1=[[@42,130:131='c1',<381>,1:130]], c2=[[@46,138:139='c2',<381>,1:138]]}, query2={col2=[[@7,28:31='col2',<381>,1:28]], col1=[[@3,15:18='col1',<381>,1:15]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query2={query_dictionary={col2=[[@7,28:31='col2',<381>,1:28]], col1=[[@3,15:18='col1',<381>,1:15]]}, def_values1={query_dictionary={c3=[[@44,134:135='c3',<381>,1:134]], c1=[[@42,130:131='c1',<381>,1:130]], c2=[[@46,138:139='c2',<381>,1:138]]}, table_dictionary={}, interface={c3=[], c1=[], c2=[]}}, table_dictionary={}, def_values0={query_dictionary={col2=[[@24,78:81='col2',<381>,1:78]], col3=[[@26,84:87='col3',<381>,1:84]], col1=[[@22,72:75='col1',<381>,1:72], [@1,8:13='source',<381>,1:8]]}, table_dictionary={}, interface={col2=[], col3=[], col1=[]}}, interface={col2=[{name=col2, table_ref=target}], col1=[{name=col1, table_ref=source}]}, table_alias={source=values0, target=values1}}}",
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
		Assert.assertEquals("Query Column Dictionary is wrong", "{values0={*=[[@1,8:8='*',<291>,1:8]], $1=[[@5,23:23='(',<287>,1:23], [@13,38:38='(',<287>,1:38]], $2=[[@5,23:23='(',<287>,1:23], [@13,38:38='(',<287>,1:38]], $3=[[@5,23:23='(',<287>,1:23], [@13,38:38='(',<287>,1:38]]}, query1={*=[[@1,8:8='*',<291>,1:8]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query1={query_dictionary={*=[[@1,8:8='*',<291>,1:8]]}, table_dictionary={}, def_values0={query_dictionary={*=[[@1,8:8='*',<291>,1:8]], $1=[[@5,23:23='(',<287>,1:23], [@13,38:38='(',<287>,1:38]], $2=[[@5,23:23='(',<287>,1:23], [@13,38:38='(',<287>,1:38]], $3=[[@5,23:23='(',<287>,1:23], [@13,38:38='(',<287>,1:38]]}, table_dictionary={}, interface={$1=[], $2=[], $3=[]}}, interface={*=[{name=*, table_ref=*}]}}}",
					extractor.getSymbolTable().toString());
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=*, table_ref=*}}}, from={values={matrix={1={row={1={literal=1}, 2={literal=2}, 3={literal='aaa'}}}, 2={row={1={literal=92}, 2={literal=3}, 3={literal='aaa'}}}}}}}}",
					extractor.getAsTree().toString());
	}

	@Test
	public void valuesSourceAliasOnlySelectRaisesUnresolvedColumnDiagnosticV1() {
		final String query = " select col1, col2 from (values (100, 1)) as value_src";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		Snippet snippet = extractor.getSnippet();
		assertFatalDiagnosticCount(
				snippet,
				"UNQUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIASES",
				null,
				"col1",
				1);
		assertFatalDiagnosticCount(
				snippet,
				"UNQUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIASES",
				null,
				"col2",
				1);
		assertDiagnosticCountBySeverity(
				snippet,
				"UNRESOLVED_UNQUALIFIED_COLUMNS",
				ParseDiagnostic.Severity.ERROR,
				"col1 [(l:1 c:8)]",
				null,
				1);
		assertDiagnosticCountBySeverity(
				snippet,
				"UNRESOLVED_UNQUALIFIED_COLUMNS",
				ParseDiagnostic.Severity.ERROR,
				"col2 [(l:1 c:14)]",
				null,
				1);
		assertDiagnosticListByCodeAndSeverity(
				snippet,
				"UNRESOLVED_UNQUALIFIED_COLUMNS",
				ParseDiagnostic.Severity.ERROR,
				"token=col2, col1 line=1 char=14 code=UNRESOLVED_UNQUALIFIED_COLUMNS severity=ERROR");

		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=col1, table_ref=null}}, 2={column={name=col2, table_ref=null}}}, from={values={alias=value_src, matrix={1={row={1={literal=100}, 2={literal=1}}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[col2, col1]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{values0={$1=[[@7,32:32='(',<287>,1:32]], $2=[[@7,32:32='(',<287>,1:32]]}, query1={col2=[[@3,14:17='col2',<381>,1:14]], col1=[[@1,8:11='col1',<381>,1:8]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query1={query_dictionary={col2=[[@3,14:17='col2',<381>,1:14]], col1=[[@1,8:11='col1',<381>,1:8]]}, table_dictionary={}, def_values0={query_dictionary={$1=[[@7,32:32='(',<287>,1:32]], $2=[[@7,32:32='(',<287>,1:32]]}, table_dictionary={}, interface={$1=[], $2=[]}}, interface={col2=[{name=col2, table_ref=null}], col1=[{name=col1, table_ref=null}]}, table_alias={value_src=values0}}}",
				extractor.getSymbolTable().toString());
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
		Assert.assertEquals("Query Column Dictionary is wrong", "{values0={*=[[@1,8:8='*',<291>,1:8]], $1=[[@5,23:23='(',<287>,1:23], [@13,38:38='(',<287>,1:38]], $2=[[@5,23:23='(',<287>,1:23], [@13,38:38='(',<287>,1:38]], $3=[[@5,23:23='(',<287>,1:23], [@13,38:38='(',<287>,1:38]]}, query1={*=[[@1,8:8='*',<291>,1:8]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query1={query_dictionary={*=[[@1,8:8='*',<291>,1:8]]}, table_dictionary={}, def_values0={query_dictionary={*=[[@1,8:8='*',<291>,1:8]], $1=[[@5,23:23='(',<287>,1:23], [@13,38:38='(',<287>,1:38]], $2=[[@5,23:23='(',<287>,1:23], [@13,38:38='(',<287>,1:38]], $3=[[@5,23:23='(',<287>,1:23], [@13,38:38='(',<287>,1:38]]}, table_dictionary={}, interface={$1=[], $2=[], $3=[]}}, interface={*=[{name=*, table_ref=*}]}, table_alias={source=values0}}}",
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
		Assert.assertEquals("Query Column Dictionary is wrong", "{values0={*=[[@1,8:8='*',<291>,1:8]], col2=[[@26,68:71='col2',<381>,1:68]], col3=[[@28,74:77='col3',<381>,1:74]], col1=[[@24,62:65='col1',<381>,1:62]]}, query1={*=[[@1,8:8='*',<291>,1:8]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query1={query_dictionary={*=[[@1,8:8='*',<291>,1:8]]}, table_dictionary={}, def_values0={query_dictionary={*=[[@1,8:8='*',<291>,1:8]], col2=[[@26,68:71='col2',<381>,1:68]], col3=[[@28,74:77='col3',<381>,1:74]], col1=[[@24,62:65='col1',<381>,1:62]]}, table_dictionary={}, interface={col2=[], col3=[], col1=[]}}, interface={*=[{name=*, table_ref=*}]}, table_alias={src=values0}}}",
					extractor.getSymbolTable().toString());
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=*, table_ref=*}}}, from={values={columns={1={column={name=col1, table_ref=null}}, 2={column={name=col2, table_ref=null}}, 3={column={name=col3, table_ref=null}}}, alias=src, matrix={1={row={1={literal=1}, 2={literal=2}, 3={literal='aaa'}}}, 2={row={1={literal=92}, 2={literal=3}, 3={literal='aaa'}}}}}}}}",
					extractor.getAsTree().toString());
	}

}
