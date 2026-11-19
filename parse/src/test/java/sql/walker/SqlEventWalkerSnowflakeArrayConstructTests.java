package sql.walker;

import java.util.Arrays;

import org.antlr.v4.runtime.tree.Trees;
import org.junit.Assert;
import org.junit.Test;

import access.Snippet;
import errorhandling.ParseDiagnostic;
import sql.SQLSelectParserParser;

public class SqlEventWalkerSnowflakeArrayConstructTests extends AbstractSqlParseEventWalkerTest {

	private void assertExactDiagnostics(SqlParseEventWalker extractor, ParseDiagnostic... expected) {
		Assert.assertEquals("Diagnostics are wrong",
				Arrays.asList(expected), extractor.getSnippet().getParserDiagnosticList());
	}

	private SqlParseEventWalker runQuery(String query) {
		final SQLSelectParserParser parser = parse(query);
		return runParsertest(query, parser);
	}

	private void assertOutputs(SqlParseEventWalker extractor, String ast, String outputInterface, String substitutions,
			String tableDictionary, String queryDictionary, String symbolTable) {
		Assert.assertEquals("AST is wrong", ast, extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", outputInterface, extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", substitutions, extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", tableDictionary, extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", queryDictionary, extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", symbolTable, extractor.getSymbolTable().toString());
	}

	@Test
	public void snowflakeArrayConstructSelectFromCsvShapeV0Test() {
		final String query =
				"SELECT md5(to_varchar(array_construct('Custom Contacts', e.email, e.first_name, <Universal Salt>))) AS sourcecontact_id "
						+ "FROM enrollment_services__ccp e";
		final SqlParseEventWalker extractor = runQuery(query);
		final Snippet snippet = extractor.getSnippet();
		Assert.assertEquals(snippet.getParserDiagnosticList().toString(), 1, snippet.getParserDiagnosticList().size());
		assertDiagnosticAtPosition(
				snippet,
				"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
				ParseDiagnostic.Severity.WARNING,
				"routine 'array_construct' at (l:1 c:22) is recognized as Snowflake-specific grammar.",
				null,
				1,
				22);
		assertNoFatalErrors(extractor);
		assertOutputs(
				extractor,
				"{SQL={select={1={function={parameters={1={function={parameters={1={function={parameters={1={literal='Custom Contacts'}, 2={column={name=email, table_ref=e}}, 3={column={name=first_name, table_ref=e}}, 4={substitution={name=<Universal Salt>, type=predicand}}}, function_name=array_construct}}}, function_name=to_varchar}}}, function_name=md5}, alias=sourcecontact_id}}, from={table={alias=e, table=enrollment_services__ccp}}}}",
				"[sourcecontact_id]",
				"{<Universal Salt>=predicand}",
				"{enrollment_services__ccp={first_name=[[@13,66:66='e',<459>,1:66]], email=[[@9,57:57='e',<459>,1:57]]}}",
				"{query0={sourcecontact_id=[[@22,103:118='sourcecontact_id',<459>,1:103]]}}",
				"{def_query0={query_dictionary={sourcecontact_id=[[@22,103:118='sourcecontact_id',<459>,1:103]]}, table_dictionary={enrollment_services__ccp={first_name=[[@13,66:66='e',<459>,1:66]], email=[[@9,57:57='e',<459>,1:57]]}}, interface={sourcecontact_id=[{name=email, table_ref=e}, {name=first_name, table_ref=e}, {name=<Universal Salt>, type=predicand}]}, table_alias={e=enrollment_services__ccp}}}");
	}

	@Test
	public void snowflakeArrayConstructJoinConditionFromCsvShapeV0Test() {
		final String query =
				"SELECT p.sourcecontact_id "
						+ "FROM enrollment_services__ccp e "
						+ "JOIN pdp__custom_contacts p "
						+ "ON md5(to_varchar(array_construct('Custom Contacts', e.email, e.first_name, <Universal Salt>))) = p.sourcecontact_id";
		final SqlParseEventWalker extractor = runQuery(query);
		final Snippet snippet = extractor.getSnippet();
		Assert.assertEquals(snippet.getParserDiagnosticList().toString(), 1, snippet.getParserDiagnosticList().size());
		assertDiagnosticAtPosition(
				snippet,
				"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
				ParseDiagnostic.Severity.WARNING,
				"routine 'array_construct' at (l:1 c:104) is recognized as Snowflake-specific grammar.",
				null,
				1,
				104);
		assertNoFatalErrors(extractor);
		assertOutputs(
				extractor,
				"{SQL={select={1={column={name=sourcecontact_id, table_ref=p}}}, from={join={1={table={alias=e, table=enrollment_services__ccp}}, 2={join=JOIN, on={condition={left={function={parameters={1={function={parameters={1={function={parameters={1={literal='Custom Contacts'}, 2={column={name=email, table_ref=e}}, 3={column={name=first_name, table_ref=e}}, 4={substitution={name=<Universal Salt>, type=predicand}}}, function_name=array_construct}}}, function_name=to_varchar}}}, function_name=md5}}, right={column={name=sourcecontact_id, table_ref=p}}, operator==}}}, 3={table={alias=p, table=pdp__custom_contacts}}}}}}",
				"[sourcecontact_id]",
				"{<Universal Salt>=predicand}",
				"{enrollment_services__ccp={first_name=[[@23,148:148='e',<459>,1:148]], email=[[@19,139:139='e',<459>,1:139]]}, pdp__custom_contacts={sourcecontact_id=[[@1,7:7='p',<459>,1:7], [@32,184:184='p',<459>,1:184]]}}",
				"{query0={sourcecontact_id=[[@3,9:24='sourcecontact_id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={sourcecontact_id=[[@3,9:24='sourcecontact_id',<459>,1:9]]}, table_dictionary={enrollment_services__ccp={first_name=[[@23,148:148='e',<459>,1:148]], email=[[@19,139:139='e',<459>,1:139]]}, pdp__custom_contacts={sourcecontact_id=[[@1,7:7='p',<459>,1:7], [@32,184:184='p',<459>,1:184]]}}, filters=[{name=email, table_ref=e}, {name=first_name, table_ref=e}, {name=<Universal Salt>, type=predicand}, {name=sourcecontact_id, table_ref=p}], interface={sourcecontact_id=[{name=sourcecontact_id, table_ref=p}]}, table_alias={p=pdp__custom_contacts, e=enrollment_services__ccp}}}");
	}

	@Test
	public void snowflakeArrayConstructUnqualifiedColumnsSingleSourceV0Test() {
		final String query = "SELECT array_construct(email, first_name) AS arr FROM enrollment_services__ccp";
		final SqlParseEventWalker extractor = runQuery(query);
		final Snippet snippet = extractor.getSnippet();
		assertDiagnosticAtPosition(
				snippet,
				"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
				ParseDiagnostic.Severity.WARNING,
				"routine 'array_construct' at (l:1 c:7) is recognized as Snowflake-specific grammar.",
				null,
				1,
				7);
		assertOutputs(
				extractor,
				"{SQL={select={1={function={parameters={1={column={name=email, table_ref=null}}, 2={column={name=first_name, table_ref=null}}}, function_name=array_construct}, alias=arr}}, from={table={alias=null, table=enrollment_services__ccp}}}}",
				"[arr]",
				"{}",
				"{enrollment_services__ccp={first_name=[[@5,30:39='first_name',<459>,1:30]], email=[[@3,23:27='email',<459>,1:23]]}}",
				"{query0={arr=[[@8,45:47='arr',<459>,1:45]]}}",
				"{def_query0={query_dictionary={arr=[[@8,45:47='arr',<459>,1:45]]}, table_dictionary={enrollment_services__ccp={first_name=[[@5,30:39='first_name',<459>,1:30]], email=[[@3,23:27='email',<459>,1:23]]}}, interface={arr=[{name=email, table_ref=enrollment_services__ccp}, {name=first_name, table_ref=enrollment_services__ccp}]}}}");
	}

	@Test
	public void snowflakeArrayConstructRepeatedColumnsV0Test() {
		final String query = "SELECT array_construct(e.email, e.email, e.first_name) AS arr FROM enrollment_services__ccp e";
		final SqlParseEventWalker extractor = runQuery(query);
		final Snippet snippet = extractor.getSnippet();
		assertDiagnosticAtPosition(
				snippet,
				"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
				ParseDiagnostic.Severity.WARNING,
				"routine 'array_construct' at (l:1 c:7) is recognized as Snowflake-specific grammar.",
				null,
				1,
				7);
		assertOutputs(
				extractor,
				"{SQL={select={1={function={parameters={1={column={name=email, table_ref=e}}, 2={column={name=email, table_ref=e}}, 3={column={name=first_name, table_ref=e}}}, function_name=array_construct}, alias=arr}}, from={table={alias=e, table=enrollment_services__ccp}}}}",
				"[arr]",
				"{}",
				"{enrollment_services__ccp={first_name=[[@11,41:41='e',<459>,1:41]], email=[[@3,23:23='e',<459>,1:23], [@7,32:32='e',<459>,1:32]]}}",
				"{query0={arr=[[@16,58:60='arr',<459>,1:58]]}}",
				"{def_query0={query_dictionary={arr=[[@16,58:60='arr',<459>,1:58]]}, table_dictionary={enrollment_services__ccp={first_name=[[@11,41:41='e',<459>,1:41]], email=[[@3,23:23='e',<459>,1:23], [@7,32:32='e',<459>,1:32]]}}, interface={arr=[{name=email, table_ref=e}, {name=first_name, table_ref=e}]}, table_alias={e=enrollment_services__ccp}}}");
	}

	@Test
	public void snowflakeArrayConstructInsideCaseV0Test() {
		final String query = "SELECT CASE WHEN e.email IS NOT NULL THEN array_construct(e.email, e.first_name) ELSE array_construct('missing') END AS arr FROM enrollment_services__ccp e";
		final SqlParseEventWalker extractor = runQuery(query);
		final Snippet snippet = extractor.getSnippet();
		assertDiagnosticAtPosition(
				snippet,
				"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
				ParseDiagnostic.Severity.WARNING,
				"routine 'array_construct' at (l:1 c:42) is recognized as Snowflake-specific grammar.",
				null,
				1,
				42);
		assertOutputs(
				extractor,
				"{SQL={select={1={alias=arr, case={clauses={1={then={function={parameters={1={column={name=email, table_ref=e}}, 2={column={name=first_name, table_ref=e}}}, function_name=array_construct}}, when={condition={left={column={name=email, table_ref=e}}, operator=IS NOT NULL}}}}, else={function={parameters={1={literal='missing'}}, function_name=array_construct}}}}}, from={table={alias=e, table=enrollment_services__ccp}}}}",
				"[arr]",
				"{}",
				"{enrollment_services__ccp={first_name=[[@16,67:67='e',<459>,1:67]], email=[[@3,17:17='e',<459>,1:17], [@12,58:58='e',<459>,1:58]]}}",
				"{query0={arr=[[@27,120:122='arr',<459>,1:120]]}}",
				"{def_query0={query_dictionary={arr=[[@27,120:122='arr',<459>,1:120]]}, table_dictionary={enrollment_services__ccp={first_name=[[@16,67:67='e',<459>,1:67]], email=[[@3,17:17='e',<459>,1:17], [@12,58:58='e',<459>,1:58]]}}, interface={arr=[{name=email, table_ref=e}, {name=first_name, table_ref=e}]}, table_alias={e=enrollment_services__ccp}}}");
	}

	@Test
	public void snowflakeArrayConstructDerivedTableConsumerV0Test() {
		final String query = "SELECT d.arr FROM (SELECT array_construct(e.email, e.first_name) AS arr FROM enrollment_services__ccp e) d";
		final SqlParseEventWalker extractor = runQuery(query);
		final Snippet snippet = extractor.getSnippet();
		assertDiagnosticAtPosition(
				snippet,
				"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
				ParseDiagnostic.Severity.WARNING,
				"routine 'array_construct' at (l:1 c:26) is recognized as Snowflake-specific grammar.",
				null,
				1,
				26);
		assertOutputs(
				extractor,
				"{SQL={select={1={column={name=arr, table_ref=d}}}, from={table={alias=d, query={select={1={function={parameters={1={column={name=email, table_ref=e}}, 2={column={name=first_name, table_ref=e}}}, function_name=array_construct}, alias=arr}}, from={table={alias=e, table=enrollment_services__ccp}}}}}}}",
				"[arr]",
				"{}",
				"{enrollment_services__ccp={first_name=[[@13,51:51='e',<459>,1:51]], email=[[@9,42:42='e',<459>,1:42]]}}",
				"{query0={arr=[[@18,68:70='arr',<459>,1:68], [@1,7:7='d',<459>,1:7]]}, query1={arr=[[@3,9:11='arr',<459>,1:9]]}}",
				"{def_query1={query_dictionary={arr=[[@3,9:11='arr',<459>,1:9]]}, def_query0={query_dictionary={arr=[[@18,68:70='arr',<459>,1:68], [@1,7:7='d',<459>,1:7]]}, table_dictionary={enrollment_services__ccp={first_name=[[@13,51:51='e',<459>,1:51]], email=[[@9,42:42='e',<459>,1:42]]}}, interface={arr=[{name=email, table_ref=e}, {name=first_name, table_ref=e}]}, table_alias={e=enrollment_services__ccp}}, interface={arr=[{name=arr, table_ref=d}]}, table_alias={d=query0}}}");
	}

	@Test
	public void snowflakeArrayConstructWherePlacementV0Test() {
		final String query = "SELECT e.email FROM enrollment_services__ccp e WHERE to_varchar(array_construct(e.email, e.first_name)) IS NOT NULL";
		final SqlParseEventWalker extractor = runQuery(query);
		final Snippet snippet = extractor.getSnippet();
		assertDiagnosticAtPosition(
				snippet,
				"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
				ParseDiagnostic.Severity.WARNING,
				"routine 'array_construct' at (l:1 c:64) is recognized as Snowflake-specific grammar.",
				null,
				1,
				64);
		assertOutputs(
				extractor,
				"{SQL={select={1={column={name=email, table_ref=e}}}, from={table={alias=e, table=enrollment_services__ccp}}, where={condition={left={function={parameters={1={function={parameters={1={column={name=email, table_ref=e}}, 2={column={name=first_name, table_ref=e}}}, function_name=array_construct}}}, function_name=to_varchar}}, operator=IS NOT NULL}}}}",
				"[email]",
				"{}",
				"{enrollment_services__ccp={first_name=[[@16,89:89='e',<459>,1:89]], email=[[@1,7:7='e',<459>,1:7], [@12,80:80='e',<459>,1:80]]}}",
				"{query0={email=[[@3,9:13='email',<459>,1:9]]}}",
				"{def_query0={query_dictionary={email=[[@3,9:13='email',<459>,1:9]]}, table_dictionary={enrollment_services__ccp={first_name=[[@16,89:89='e',<459>,1:89]], email=[[@1,7:7='e',<459>,1:7], [@12,80:80='e',<459>,1:80]]}}, filters=[{name=email, table_ref=e}, {name=first_name, table_ref=e}], interface={email=[{name=email, table_ref=e}]}, table_alias={e=enrollment_services__ccp}}}");
	}

	@Test
	public void snowflakeArrayConstructHavingPlacementV0Test() {
		final String query = "SELECT e.email, count(*) AS c FROM enrollment_services__ccp e GROUP BY e.email HAVING to_varchar(array_construct(e.email, e.first_name)) IS NOT NULL";
		final SqlParseEventWalker extractor = runQuery(query);
		final Snippet snippet = extractor.getSnippet();
		assertDiagnosticAtPosition(
				snippet,
				"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
				ParseDiagnostic.Severity.WARNING,
				"routine 'array_construct' at (l:1 c:97) is recognized as Snowflake-specific grammar.",
				null,
				1,
				97);
		assertOutputs(
				extractor,
				"{SQL={select={1={column={name=email, table_ref=e}}, 2={function={function_name=COUNT, qualifier=null, parameters=*}, alias=c}}, having={condition={left={function={parameters={1={function={parameters={1={column={name=email, table_ref=e}}, 2={column={name=first_name, table_ref=e}}}, function_name=array_construct}}}, function_name=to_varchar}}, operator=IS NOT NULL}}, from={table={alias=e, table=enrollment_services__ccp}}, groupby={1={column={name=email, table_ref=e}}}}}",
				"[c, email]",
				"{}",
				"{enrollment_services__ccp={*=[[@7,22:22='*',<291>,1:22]], first_name=[[@28,122:122='e',<459>,1:122]], email=[[@1,7:7='e',<459>,1:7], [@16,71:71='e',<459>,1:71], [@24,113:113='e',<459>,1:113]]}}",
				"{query0={email=[[@3,9:13='email',<459>,1:9]], c=[[@10,28:28='c',<459>,1:28]]}}",
				"{def_query0={query_dictionary={c=[[@10,28:28='c',<459>,1:28]], email=[[@3,9:13='email',<459>,1:9]]}, table_dictionary={enrollment_services__ccp={*=[[@7,22:22='*',<291>,1:22]], first_name=[[@28,122:122='e',<459>,1:122]], email=[[@1,7:7='e',<459>,1:7], [@16,71:71='e',<459>,1:71], [@24,113:113='e',<459>,1:113]]}}, grouped_by=[{name=email, table_ref=e}], filters=[{name=email, table_ref=e}, {name=first_name, table_ref=e}], interface={c=[], email=[{name=email, table_ref=e}]}, table_alias={e=enrollment_services__ccp}}}");
	}

	@Test
	public void snowflakeArrayConstructQualifyPlacementV0Test() {
		final String query = "SELECT e.email, row_number() over (partition by e.email order by e.first_name) AS rn FROM enrollment_services__ccp e QUALIFY to_varchar(array_construct(e.email, e.first_name)) IS NOT NULL";
		final SqlParseEventWalker extractor = runQuery(query);
		final Snippet snippet = extractor.getSnippet();
		assertDiagnosticAtPosition(
				snippet,
				"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
				ParseDiagnostic.Severity.WARNING,
				"routine 'array_construct' at (l:1 c:136) is recognized as Snowflake-specific grammar.",
				null,
				1,
				136);
		assertOutputs(
				extractor,
				"{SQL={select={1={column={name=email, table_ref=e}}, 2={alias=rn, window_function={over={partition_by={1={column={name=email, table_ref=e}}}, orderby={1={null_order=null, predicand={column={name=first_name, table_ref=e}}, sort_order=ASC}}}, function={function_name=row_number, parameters=null}}}}, from={table={alias=e, table=enrollment_services__ccp}}, qualify={condition={left={function={parameters={1={function={parameters={1={column={name=email, table_ref=e}}, 2={column={name=first_name, table_ref=e}}}, function_name=array_construct}}}, function_name=to_varchar}}, operator=IS NOT NULL}}}}",
				"[rn, email]",
				"{}",
				"{enrollment_services__ccp={first_name=[[@17,65:65='e',<459>,1:65], [@35,161:161='e',<459>,1:161]], email=[[@1,7:7='e',<459>,1:7], [@12,48:48='e',<459>,1:48], [@31,152:152='e',<459>,1:152]]}}",
				"{query0={rn=[[@22,82:83='rn',<459>,1:82]], email=[[@3,9:13='email',<459>,1:9]]}}",
				"{def_query0={window_ordered_by=[{name=first_name, table_ref=e}], query_dictionary={rn=[[@22,82:83='rn',<459>,1:82]], email=[[@3,9:13='email',<459>,1:9]]}, table_dictionary={enrollment_services__ccp={first_name=[[@17,65:65='e',<459>,1:65], [@35,161:161='e',<459>,1:161]], email=[[@1,7:7='e',<459>,1:7], [@12,48:48='e',<459>,1:48], [@31,152:152='e',<459>,1:152]]}}, window_partition_by=[{name=email, table_ref=e}], filters=[{name=email, table_ref=e}, {name=first_name, table_ref=e}], interface={rn=[{name=email, table_ref=e}, {name=first_name, table_ref=e}], email=[{name=email, table_ref=e}]}, table_alias={e=enrollment_services__ccp}}}");
	}

	@Test
	public void snowflakeArrayConstructOrderByPlacementV0Test() {
		final String query = "SELECT e.email FROM enrollment_services__ccp e ORDER BY to_varchar(array_construct(e.email, e.first_name))";
		final SqlParseEventWalker extractor = runQuery(query);
		final Snippet snippet = extractor.getSnippet();
		assertDiagnosticAtPosition(
				snippet,
				"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
				ParseDiagnostic.Severity.WARNING,
				"routine 'array_construct' at (l:1 c:67) is recognized as Snowflake-specific grammar.",
				null,
				1,
				67);
		assertOutputs(
				extractor,
				"{SQL={select={1={column={name=email, table_ref=e}}}, orderby={1={null_order=null, predicand={function={parameters={1={function={parameters={1={column={name=email, table_ref=e}}, 2={column={name=first_name, table_ref=e}}}, function_name=array_construct}}}, function_name=to_varchar}}, sort_order=ASC}}, from={table={alias=e, table=enrollment_services__ccp}}}}",
				"[email]",
				"{}",
				"{enrollment_services__ccp={first_name=[[@17,92:92='e',<459>,1:92]], email=[[@1,7:7='e',<459>,1:7], [@13,83:83='e',<459>,1:83]]}}",
				"{query0={email=[[@3,9:13='email',<459>,1:9]]}}",
				"{def_query0={query_dictionary={email=[[@3,9:13='email',<459>,1:9]]}, table_dictionary={enrollment_services__ccp={first_name=[[@17,92:92='e',<459>,1:92]], email=[[@1,7:7='e',<459>,1:7], [@13,83:83='e',<459>,1:83]]}}, ordered_by=[{name=email, table_ref=e}, {name=first_name, table_ref=e}], interface={email=[{name=email, table_ref=e}]}, table_alias={e=enrollment_services__ccp}}}");
	}

	@Test
	public void snowflakeArrayConstructGroupByPlacementV0Test() {
		final String query = "SELECT to_varchar(array_construct(e.email, e.first_name)) AS k, count(*) FROM enrollment_services__ccp e GROUP BY to_varchar(array_construct(e.email, e.first_name))";
		final SqlParseEventWalker extractor = runQuery(query);
		final Snippet snippet = extractor.getSnippet();
		assertDiagnosticAtPosition(
				snippet,
				"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
				ParseDiagnostic.Severity.WARNING,
				"routine 'array_construct' at (l:1 c:18) is recognized as Snowflake-specific grammar.",
				null,
				1,
				18);
		assertOutputs(
				extractor,
				"{SQL={select={1={function={parameters={1={function={parameters={1={column={name=email, table_ref=e}}, 2={column={name=first_name, table_ref=e}}}, function_name=array_construct}}}, function_name=to_varchar}, alias=k}, 2={function={function_name=COUNT, qualifier=null, parameters=*}}}, from={table={alias=e, table=enrollment_services__ccp}}, groupby={1={function={parameters={1={function={parameters={1={column={name=email, table_ref=e}}, 2={column={name=first_name, table_ref=e}}}, function_name=array_construct}}}, function_name=to_varchar}}}}}",
				"[k, unnamed_0]",
				"{}",
				"{enrollment_services__ccp={*=[[@19,70:70='*',<291>,1:70]], first_name=[[@9,43:43='e',<459>,1:43], [@34,150:150='e',<459>,1:150]], email=[[@5,34:34='e',<459>,1:34], [@30,141:141='e',<459>,1:141]]}}",
				"{query0={k=[[@15,61:61='k',<459>,1:61]], unnamed_0=[[@20,71:71=')',<288>,1:71]]}}",
				"{def_query0={query_dictionary={k=[[@15,61:61='k',<459>,1:61]], unnamed_0=[[@20,71:71=')',<288>,1:71]]}, table_dictionary={enrollment_services__ccp={*=[[@19,70:70='*',<291>,1:70]], first_name=[[@9,43:43='e',<459>,1:43], [@34,150:150='e',<459>,1:150]], email=[[@5,34:34='e',<459>,1:34], [@30,141:141='e',<459>,1:141]]}}, grouped_by=[{name=email, table_ref=e}, {name=first_name, table_ref=e}], interface={k=[{name=email, table_ref=e}, {name=first_name, table_ref=e}], unnamed_0=[]}, table_alias={e=enrollment_services__ccp}}}");
	}

	@Test
	public void snowflakeArrayConstructValuesPlacementV0Test() {
		final String query = "VALUES (array_construct('a', 'b'))";
		final SqlParseEventWalker extractor = runQuery(query);
		final Snippet snippet = extractor.getSnippet();
		assertDiagnosticAtPosition(
				snippet,
				"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
				ParseDiagnostic.Severity.WARNING,
				"routine 'array_construct' at (l:1 c:8) is recognized as Snowflake-specific grammar.",
				null,
				1,
				8);
		assertOutputs(
				extractor,
				"{SQL={values={matrix={1={row={1={function={parameters={1={literal='a'}, 2={literal='b'}}, function_name=array_construct}}}}}}}}",
				"[$1]",
				"{}",
				"{}",
				"{values0={$1=[[@1,7:7='(',<287>,1:7]]}}",
				"{def_values0={query_dictionary={$1=[[@1,7:7='(',<287>,1:7]]}, interface={$1=[]}}}");
	}

	@Test
	public void snowflakeArrayConstructInsertPlacementV0Test() {
		final String query = "INSERT INTO target (payload) SELECT to_varchar(array_construct(e.email, e.first_name)) FROM enrollment_services__ccp e";
		final SqlParseEventWalker extractor = runQuery(query);
		final Snippet snippet = extractor.getSnippet();
		assertDiagnosticAtPosition(
				snippet,
				"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
				ParseDiagnostic.Severity.WARNING,
				"routine 'array_construct' at (l:1 c:47) is recognized as Snowflake-specific grammar.",
				null,
				1,
				47);
		assertOutputs(
				extractor,
				"{SQL={insert={preamble=insert_into, from={from={table={alias=e, table=enrollment_services__ccp}}, select={1={function={parameters={1={function={parameters={1={column={name=email, table_ref=e}}, 2={column={name=first_name, table_ref=e}}}, function_name=array_construct}}}, function_name=to_varchar}}}}, target_table={table={alias=null, table=target}}, columns={1={column={name=payload, table_ref=null}}}}}}",
				"[payload]",
				"{}",
				"{enrollment_services__ccp={first_name=[[@15,72:72='e',<459>,1:72]], email=[[@11,63:63='e',<459>,1:63]]}, target={payload=[[@4,20:26='payload',<459>,1:20]]}}",
				"{query0={unnamed_0=[[@19,85:85=')',<288>,1:85]]}, insert1={payload=[[@4,20:26='payload',<459>,1:20]]}}",
				"{def_insert1={query_dictionary={payload=[[@4,20:26='payload',<459>,1:20]]}, table_dictionary={target={payload=[[@4,20:26='payload',<459>,1:20]]}}, def_query0={query_dictionary={unnamed_0=[[@19,85:85=')',<288>,1:85]]}, table_dictionary={enrollment_services__ccp={first_name=[[@15,72:72='e',<459>,1:72]], email=[[@11,63:63='e',<459>,1:63]]}}, interface={unnamed_0=[{name=email, table_ref=e}, {name=first_name, table_ref=e}]}, table_alias={e=enrollment_services__ccp}}, interface={payload=[{name=unnamed_0, table_ref=query0}]}}}");
	}

	@Test
	public void snowflakeArrayConstructUpdatePlacementV0Test() {
		final String query = "UPDATE enrollment_services__ccp SET payload = to_varchar(array_construct(email, first_name))";
		final SqlParseEventWalker extractor = runQuery(query);
		final Snippet snippet = extractor.getSnippet();
		assertDiagnosticAtPosition(
				snippet,
				"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
				ParseDiagnostic.Severity.WARNING,
				"routine 'array_construct' at (l:1 c:57) is recognized as Snowflake-specific grammar.",
				null,
				1,
				57);
		assertOutputs(
				extractor,
				"{SQL={update={table={alias=null, table=enrollment_services__ccp}, assignments={1={set={column={name=payload, table_ref=null}}, to={function={parameters={1={function={parameters={1={column={name=email, table_ref=null}}, 2={column={name=first_name, table_ref=null}}}, function_name=array_construct}}}, function_name=to_varchar}}}}}}}",
				"[payload]",
				"{}",
				"{enrollment_services__ccp={first_name=[[@11,80:89='first_name',<459>,1:80], [@13,91:91=')',<288>,1:91]], email=[[@9,73:77='email',<459>,1:73], [@13,91:91=')',<288>,1:91]]}}",
				"{update0={payload=[[@3,36:42='payload',<459>,1:36]]}}",
				"{def_update0={assignments={payload=[{name=email, table_ref=enrollment_services__ccp}, {name=first_name, table_ref=enrollment_services__ccp}]}, table_dictionary={enrollment_services__ccp={first_name=[[@11,80:89='first_name',<459>,1:80], [@13,91:91=')',<288>,1:91]], email=[[@9,73:77='email',<459>,1:73], [@13,91:91=')',<288>,1:91]]}}, update_dictionary={payload=[[@3,36:42='payload',<459>,1:36]]}, target_table={enrollment_services__ccp={payload=[[@3,36:42='payload',<459>,1:36]]}}, lhs_unresolved_columns={payload={column={name=payload, table_ref=null}, locations=[[@3,36:42='payload',<459>,1:36]]}}}}");
	}

	@Test
	public void snowflakeArrayConstructDeletePlacementV0Test() {
		final String query = "DELETE FROM enrollment_services__ccp WHERE to_varchar(array_construct(email, first_name)) IS NULL";
		final SqlParseEventWalker extractor = runQuery(query);
		final Snippet snippet = extractor.getSnippet();
		assertDiagnosticAtPosition(
				snippet,
				"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
				ParseDiagnostic.Severity.WARNING,
				"routine 'array_construct' at (l:1 c:54) is recognized as Snowflake-specific grammar.",
				null,
				1,
				54);
		assertOutputs(
				extractor,
				"{SQL={delete={table={alias=null, table=enrollment_services__ccp}, where={condition={left={function={parameters={1={function={parameters={1={column={name=email, table_ref=null}}, 2={column={name=first_name, table_ref=null}}}, function_name=array_construct}}}, function_name=to_varchar}}, operator=IS NULL}}}}}",
				"[]",
				"{}",
				"{enrollment_services__ccp={first_name=[[@10,77:86='first_name',<459>,1:77]], email=[[@8,70:74='email',<459>,1:70]]}}",
				"{}",
				"{def_delete0={query_dictionary={}, table_dictionary={enrollment_services__ccp={first_name=[[@10,77:86='first_name',<459>,1:77]], email=[[@8,70:74='email',<459>,1:70]]}}, filters=[{name=email, table_ref=enrollment_services__ccp}, {name=first_name, table_ref=enrollment_services__ccp}], interface=null}}");
	}

	@Test
	public void snowflakeArrayConstructDdlPlacementV0Test() {
		final String query =
				"CREATE VIEW v_arr AS SELECT array_construct(e.email, e.first_name) AS arr FROM enrollment_services__ccp e";
		final SQLSelectParserParser parser = parse(query);
		final SqlParseEventWalker extractor = runDdlParsertest(query, parser);
		final Snippet snippet = extractor.getSnippet();
		assertDiagnosticAtPosition(
				snippet,
				"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
				ParseDiagnostic.Severity.WARNING,
				"routine 'array_construct' at (l:1 c:28) is recognized as Snowflake-specific grammar.",
				null,
				1,
				28);
		assertNoFatalErrors(extractor);
		assertOutputs(
				extractor,
				"{DDL={create={type=VIEW, name={table=v_arr}, query={select={1={function={parameters={1={column={name=email, table_ref=e}}, 2={column={name=first_name, table_ref=e}}}, function_name=array_construct}, alias=arr}}, from={table={alias=e, table=enrollment_services__ccp}}}}}}",
				"[]",
				"{}",
				"{enrollment_services__ccp={first_name=[[@11,53:53='e',<459>,1:53]], email=[[@7,44:44='e',<459>,1:44]]}}",
				"{query0={arr=[[@16,70:72='arr',<459>,1:70]]}}",
				"{def_create1={def_query0={query_dictionary={arr=[[@16,70:72='arr',<459>,1:70]]}, table_dictionary={enrollment_services__ccp={first_name=[[@11,53:53='e',<459>,1:53]], email=[[@7,44:44='e',<459>,1:44]]}}, interface={arr=[{name=email, table_ref=e}, {name=first_name, table_ref=e}]}, table_alias={e=enrollment_services__ccp}}}}");
	}

	@Test
	public void snowflakeArrayConstructScriptStatementScopedWarningV0Test() {
		final String query =
				"SELECT array_construct(email) AS arr FROM enrollment_services__ccp; SELECT email FROM enrollment_services__ccp;";
		final SQLSelectParserParser parser = parse(query);
		final SqlParseEventWalker extractor = runScriptParsertest(query, parser);
		final Snippet snippet = extractor.getSnippet();

		assertDiagnosticCountBySeverity(
				snippet,
				"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
				ParseDiagnostic.Severity.WARNING,
				null,
				null,
				1);
		assertDiagnosticAtPosition(
				snippet,
				"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
				ParseDiagnostic.Severity.WARNING,
				"routine 'array_construct' at (l:1 c:7) is recognized as Snowflake-specific grammar.",
				null,
				1,
				7);
		assertNoFatalErrors(extractor);
		Assert.assertEquals("AST is wrong",
				"{SCRIPT={1={select={1={function={parameters={1={column={name=email, table_ref=null}}}, function_name=array_construct}, alias=arr}}, from={table={alias=null, table=enrollment_services__ccp}}}, 2={select={1={column={name=email, table_ref=null}}}, from={table={alias=null, table=enrollment_services__ccp}}}}}",
				extractor.getAsTree().toString());
	}

	@Test
	public void snowflakeArrayConstructUnresolvedUnqualifiedColumnV0Test() {
		final String query = "SELECT array_construct(email, missing_col) AS arr FROM enrollment_services__ccp";
		final SqlParseEventWalker extractor = runQuery(query);
		final Snippet snippet = extractor.getSnippet();
		assertDiagnosticAtPosition(
				snippet,
				"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
				ParseDiagnostic.Severity.WARNING,
				"routine 'array_construct' at (l:1 c:7) is recognized as Snowflake-specific grammar.",
				null,
				1,
				7);
		assertOutputs(
				extractor,
				"{SQL={select={1={function={parameters={1={column={name=email, table_ref=null}}, 2={column={name=missing_col, table_ref=null}}}, function_name=array_construct}, alias=arr}}, from={table={alias=null, table=enrollment_services__ccp}}}}",
				"[arr]",
				"{}",
				"{enrollment_services__ccp={missing_col=[[@5,30:40='missing_col',<459>,1:30]], email=[[@3,23:27='email',<459>,1:23]]}}",
				"{query0={arr=[[@8,46:48='arr',<459>,1:46]]}}",
				"{def_query0={query_dictionary={arr=[[@8,46:48='arr',<459>,1:46]]}, table_dictionary={enrollment_services__ccp={missing_col=[[@5,30:40='missing_col',<459>,1:30]], email=[[@3,23:27='email',<459>,1:23]]}}, interface={arr=[{name=email, table_ref=enrollment_services__ccp}, {name=missing_col, table_ref=enrollment_services__ccp}]}}}");
	}

	@Test
	public void snowflakeArrayConstructAmbiguousUnqualifiedColumnV0Test() {
		final String query = "SELECT array_construct(id) AS arr FROM tab1 a JOIN tab2 b ON a.id = b.id";
		final SqlParseEventWalker extractor = runQuery(query);
		final Snippet snippet = extractor.getSnippet();
		assertDiagnosticAtPosition(
				snippet,
				"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
				ParseDiagnostic.Severity.WARNING,
				"routine 'array_construct' at (l:1 c:7) is recognized as Snowflake-specific grammar.",
				null,
				1,
				7);
		assertDiagnosticCountBySeverity(snippet, "AMBIGUOUS_COLUMN_REFERENCE", ParseDiagnostic.Severity.SEVERE_WARNING, null, "id", 1);
		assertOutputs(
				extractor,
				"{SQL={select={1={function={parameters={1={column={name=id, table_ref=null}}}, function_name=array_construct}, alias=arr}}, from={join={1={table={alias=a, table=tab1}}, 2={join=JOIN, on={condition={left={column={name=id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=tab2}}}}}}",
				"[arr]",
				"{}",
				"{tab1={id=[[@14,61:61='a',<459>,1:61]]}, tab2={id=[[@18,68:68='b',<459>,1:68]]}}",
				"{query0={arr=[[@6,30:32='arr',<459>,1:30]]}}",
				"{def_query0={query_dictionary={arr=[[@6,30:32='arr',<459>,1:30]]}, table_dictionary={tab1={id=[[@14,61:61='a',<459>,1:61]]}, tab2={id=[[@18,68:68='b',<459>,1:68]]}}, filters=[{name=id, table_ref=a}, {name=id, table_ref=b}], interface={arr=[{name=id, table_ref=null}]}, table_alias={a=tab1, b=tab2}}}");
	}

	@Test
	public void snowflakeArrayConstructQualifiedMissingAliasFatalV0Test() {
		final String query = "SELECT array_construct(z.email) AS arr FROM enrollment_services__ccp e";
		final SqlParseEventWalker extractor = runQuery(query);
		final Snippet snippet = extractor.getSnippet();
		assertDiagnosticAtPosition(
				snippet,
				"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
				ParseDiagnostic.Severity.WARNING,
				"routine 'array_construct' at (l:1 c:7) is recognized as Snowflake-specific grammar.",
				null,
				1,
				7);
		Assert.assertEquals("Expected one fatal walker message", 1, snippet.getFatalErrorStringList().size());
		Assert.assertEquals(
				"Source Table not found for Column 'email' at (l:1 c:35). No alias or table called 'z'.",
				snippet.getFatalErrorStringList().get(0));
		assertOutputs(
				extractor,
				"{SQL={select={1={function={parameters={1={column={name=email, table_ref=z}}}, function_name=array_construct}, alias=arr}}, from={table={alias=e, table=enrollment_services__ccp}}}}",
				"[arr]",
				"{}",
				"{enrollment_services__ccp={}}",
				"{query0={arr=[[@8,35:37='arr',<459>,1:35]]}}",
				"{def_query0={query_dictionary={arr=[[@8,35:37='arr',<459>,1:35]]}, table_dictionary={enrollment_services__ccp={}}, interface={arr=[{name=email, table_ref=z}]}, table_alias={e=enrollment_services__ccp}}}");
	}

	@Test
	public void snowflakeArrayConstructWarningEmittedOncePerStatementV0Test() {
		final String query = "SELECT array_construct(email), array_construct(first_name) FROM enrollment_services__ccp";
		final SqlParseEventWalker extractor = runQuery(query);
		final Snippet snippet = extractor.getSnippet();
		assertDiagnosticCountBySeverity(snippet, "STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR", ParseDiagnostic.Severity.WARNING, null, null, 1);
		assertOutputs(
				extractor,
				"{SQL={select={1={function={parameters={1={column={name=email, table_ref=null}}}, function_name=array_construct}}, 2={function={parameters={1={column={name=first_name, table_ref=null}}}, function_name=array_construct}}}, from={table={alias=null, table=enrollment_services__ccp}}}}",
				"[unnamed_1, unnamed_0]",
				"{}",
				"{enrollment_services__ccp={first_name=[[@8,47:56='first_name',<459>,1:47]], email=[[@3,23:27='email',<459>,1:23]]}}",
				"{query0={unnamed_1=[[@9,57:57=')',<288>,1:57]], unnamed_0=[[@4,28:28=')',<288>,1:28]]}}",
				"{def_query0={query_dictionary={unnamed_1=[[@9,57:57=')',<288>,1:57]], unnamed_0=[[@4,28:28=')',<288>,1:28]]}, table_dictionary={enrollment_services__ccp={first_name=[[@8,47:56='first_name',<459>,1:47]], email=[[@3,23:27='email',<459>,1:23]]}}, interface={unnamed_1=[{name=first_name, table_ref=enrollment_services__ccp}], unnamed_0=[{name=email, table_ref=enrollment_services__ccp}]}}}");
	}

	@Test
	public void snowflakeArrayConstructMixedDialectFatalV0Test() {
		final String query = "SELECT array_construct(t.col1), extract(isodow from t.col1) FROM tab1 t";
		final SqlParseEventWalker extractor = runQuery(query);
		final Snippet snippet = extractor.getSnippet();
		Assert.assertEquals("Expected one fatal (mixed dialect)", 1, snippet.getFatalErrorStringList().size());
		assertDiagnosticAtPosition(
				snippet,
				"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
				ParseDiagnostic.Severity.WARNING,
				"routine 'array_construct' at (l:1 c:7) is recognized as Snowflake-specific grammar.",
				null,
				1,
				7);
		assertFatalDiagnosticAtPosition(
				snippet,
				"STATEMENT_MIXED_DIALECT_GRAMMAR",
				"unlikely to run on either engine",
				null,
				1,
				32);
		assertOutputs(
				extractor,
				"{SQL={select={1={function={parameters={1={column={name=col1, table_ref=t}}}, function_name=array_construct}}, 2={datetime_function={function_name=extract, part_form=KEYWORD, part=isodow, source={column={name=col1, table_ref=t}}}}}, from={table={alias=t, table=tab1}}}}",
				"[unnamed_1, unnamed_0]",
				"{}",
				"{tab1={col1=[[@3,23:23='t',<459>,1:23], [@12,52:52='t',<459>,1:52]]}}",
				"{query0={unnamed_1=[[@15,58:58=')',<288>,1:58]], unnamed_0=[[@6,29:29=')',<288>,1:29]]}}",
				"{def_query0={query_dictionary={unnamed_1=[[@15,58:58=')',<288>,1:58]], unnamed_0=[[@6,29:29=')',<288>,1:29]]}, table_dictionary={tab1={col1=[[@3,23:23='t',<459>,1:23], [@12,52:52='t',<459>,1:52]]}}, interface={unnamed_1=[{name=col1, table_ref=t}], unnamed_0=[{name=col1, table_ref=t}]}, table_alias={t=tab1}}}");
	}

	@Test
	public void snowflakeArrayConstructRegressionOrdinaryRoutineNoSnowflakeWarningV0Test() {
		final String query = "SELECT md5(email) FROM enrollment_services__ccp";
		final SqlParseEventWalker extractor = runQuery(query);
		assertDiagnosticCountBySeverity(extractor.getSnippet(), "STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR", ParseDiagnostic.Severity.WARNING, null, null, 0);
		assertOutputs(
				extractor,
				"{SQL={select={1={function={parameters={1={column={name=email, table_ref=null}}}, function_name=md5}}}, from={table={alias=null, table=enrollment_services__ccp}}}}",
				"[unnamed_0]",
				"{}",
				"{enrollment_services__ccp={email=[[@3,11:15='email',<459>,1:11]]}}",
				"{query0={unnamed_0=[[@4,16:16=')',<288>,1:16]]}}",
				"{def_query0={query_dictionary={unnamed_0=[[@4,16:16=')',<288>,1:16]]}, table_dictionary={enrollment_services__ccp={email=[[@3,11:15='email',<459>,1:11]]}}, interface={unnamed_0=[{name=email, table_ref=enrollment_services__ccp}]}}}");
	}

	@Test
	public void snowflakeArrayConstructCompactSelectPlacementV0Test() {
		final String query = "SELECT array_construct_compact(email, first_name) AS arr FROM enrollment_services__ccp";
		final SqlParseEventWalker extractor = runQuery(query);
		final Snippet snippet = extractor.getSnippet();
		assertDiagnosticAtPosition(
				snippet,
				"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
				ParseDiagnostic.Severity.WARNING,
				"routine 'array_construct_compact' at (l:1 c:7) is recognized as Snowflake-specific grammar.",
				null,
				1,
				7);
		assertNoFatalErrors(extractor);
		assertOutputs(
				extractor,
				"{SQL={select={1={function={parameters={1={column={name=email, table_ref=null}}, 2={column={name=first_name, table_ref=null}}}, function_name=array_construct_compact}, alias=arr}}, from={table={alias=null, table=enrollment_services__ccp}}}}",
				"[arr]",
				"{}",
				"{enrollment_services__ccp={first_name=[[@5,38:47='first_name',<459>,1:38]], email=[[@3,31:35='email',<459>,1:31]]}}",
				"{query0={arr=[[@8,53:55='arr',<459>,1:53]]}}",
				"{def_query0={query_dictionary={arr=[[@8,53:55='arr',<459>,1:53]]}, table_dictionary={enrollment_services__ccp={first_name=[[@5,38:47='first_name',<459>,1:38]], email=[[@3,31:35='email',<459>,1:31]]}}, interface={arr=[{name=email, table_ref=enrollment_services__ccp}, {name=first_name, table_ref=enrollment_services__ccp}]}}}");
	}

	@Test
	public void snowflakeArrayContainsSelectPlacementV0Test() {
		final String query = "SELECT array_contains('a', tags) AS has_a FROM events e";
		final SqlParseEventWalker extractor = runQuery(query);
		final Snippet snippet = extractor.getSnippet();
		assertDiagnosticAtPosition(
				snippet,
				"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
				ParseDiagnostic.Severity.WARNING,
				"routine 'array_contains' at (l:1 c:7) is recognized as Snowflake-specific grammar.",
				null,
				1,
				7);
		assertNoFatalErrors(extractor);
		assertOutputs(
				extractor,
				"{SQL={select={1={function={parameters={1={literal='a'}, 2={column={name=tags, table_ref=null}}}, function_name=array_contains}, alias=has_a}}, from={table={alias=e, table=events}}}}",
				"[has_a]",
				"{}",
				"{events={tags=[[@5,27:30='tags',<459>,1:27]]}}",
				"{query0={has_a=[[@8,36:40='has_a',<459>,1:36]]}}",
				"{def_query0={query_dictionary={has_a=[[@8,36:40='has_a',<459>,1:36]]}, table_dictionary={events={tags=[[@5,27:30='tags',<459>,1:27]]}}, interface={has_a=[{name=tags, table_ref=events}]}, table_alias={e=events}}}");
	}

	@Test
	public void snowflakeArrayUnionAggSelectPlacementV0Test() {
		final String query = "SELECT array_union_agg(tags) AS merged_tags FROM events e";
		final SqlParseEventWalker extractor = runQuery(query);
		final Snippet snippet = extractor.getSnippet();
		assertDiagnosticAtPosition(
				snippet,
				"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
				ParseDiagnostic.Severity.WARNING,
				"routine 'array_union_agg' at (l:1 c:7) is recognized as Snowflake-specific grammar.",
				null,
				1,
				7);
		assertNoFatalErrors(extractor);
		assertOutputs(
				extractor,
				"{SQL={select={1={function={function_name=array_union_agg, qualifier=null, parameters={column={name=tags, table_ref=null}}}, alias=merged_tags}}, from={table={alias=e, table=events}}}}",
				"[merged_tags]",
				"{}",
				"{events={tags=[[@3,23:26='tags',<459>,1:23]]}}",
				"{query0={merged_tags=[[@6,32:42='merged_tags',<459>,1:32]]}}",
				"{def_query0={query_dictionary={merged_tags=[[@6,32:42='merged_tags',<459>,1:32]]}, table_dictionary={events={tags=[[@3,23:26='tags',<459>,1:23]]}}, interface={merged_tags=[{name=tags, table_ref=events}]}, table_alias={e=events}}}");
	}

	@Test
	public void snowflakeArrayUniqueAggSelectPlacementV0Test() {
		final String query = "SELECT array_unique_agg(tags) AS unique_tags FROM events e";
		final SqlParseEventWalker extractor = runQuery(query);
		final Snippet snippet = extractor.getSnippet();
		assertDiagnosticAtPosition(
				snippet,
				"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
				ParseDiagnostic.Severity.WARNING,
				"routine 'array_unique_agg' at (l:1 c:7) is recognized as Snowflake-specific grammar.",
				null,
				1,
				7);
		assertNoFatalErrors(extractor);
		assertOutputs(
				extractor,
				"{SQL={select={1={function={function_name=array_unique_agg, qualifier=null, parameters={column={name=tags, table_ref=null}}}, alias=unique_tags}}, from={table={alias=e, table=events}}}}",
				"[unique_tags]",
				"{}",
				"{events={tags=[[@3,24:27='tags',<459>,1:24]]}}",
				"{query0={unique_tags=[[@6,33:43='unique_tags',<459>,1:33]]}}",
				"{def_query0={query_dictionary={unique_tags=[[@6,33:43='unique_tags',<459>,1:33]]}, table_dictionary={events={tags=[[@3,24:27='tags',<459>,1:24]]}}, interface={unique_tags=[{name=tags, table_ref=events}]}, table_alias={e=events}}}");
	}

	@Test
	public void snowflakeArraySetAggregatesWarningEmittedOncePerStatementV0Test() {
		final String query =
				"SELECT array_union_agg(tags) AS merged_tags, array_unique_agg(tags) AS unique_tags FROM events e";
		final SqlParseEventWalker extractor = runQuery(query);
		assertDiagnosticCountBySeverity(
				extractor.getSnippet(),
				"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
				ParseDiagnostic.Severity.WARNING,
				null,
				null,
				1);
	}

	@Test
	public void snowflakeArrayConstructTrailingCommaSyntaxFatalV0Test() {
		final String query = "SELECT array_construct(email,) AS arr FROM enrollment_services__ccp";
		final SQLSelectParserParser parser = parse(query);
		ParserRunResult runResult = runSQLParsertestAllowErrors(query, parser);
		Assert.assertTrue(
				"Expected parser error at trailing-comma site",
				runResult.getParserErrors().toString().contains("Line 1:29 - unexpected input: ')'"));
	}

	@Test
	public void arC025_array_construct_structuredSelectPlacementV0Test() {
		final String query = "SELECT a.id, array_construct_structured(a.item_one, a.item_two, a.item_three) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_routine_invocation).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_construct_structured' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={parameters={1={column={name=item_one, table_ref=a}}, 2={column={name=item_two, table_ref=a}}, 3={column={name=item_three, table_ref=a}}}, function_name=array_construct_structured}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{}",
				"{events={item_two=[[@11,52:52='a',<459>,1:52]], active=[[@25,114:114='a',<459>,1:114]], id=[[@1,7:7='a',<459>,1:7]], item_three=[[@15,64:64='a',<459>,1:64]], item_one=[[@7,40:40='a',<459>,1:40]]}}",
				"{query0={result_value=[[@20,81:92='result_value',<459>,1:81]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@20,81:92='result_value',<459>,1:81]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={item_two=[[@11,52:52='a',<459>,1:52]], active=[[@25,114:114='a',<459>,1:114]], id=[[@1,7:7='a',<459>,1:7]], item_three=[[@15,64:64='a',<459>,1:64]], item_one=[[@7,40:40='a',<459>,1:40]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=item_one, table_ref=a}, {name=item_two, table_ref=a}, {name=item_three, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC025_array_construct_structuredWherePlacementV0Test() {
		final String query = "SELECT a.id, a.label FROM events a WHERE array_construct_structured(a.item_one, a.item_two, a.item_three) IS NOT NULL AND a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_routine_invocation).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_construct_structured' at (l:1 c:41) is recognized as Snowflake-specific grammar.",
						1, 41, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={column={name=label, table_ref=a}}}, from={table={alias=a, table=events}}, where={and={1={condition={left={function={parameters={1={column={name=item_one, table_ref=a}}, 2={column={name=item_two, table_ref=a}}, 3={column={name=item_three, table_ref=a}}}, function_name=array_construct_structured}}, operator=IS NOT NULL}}, 2={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}}}",
				"[id, label]",
				"{}",
				"{events={item_two=[[@18,80:80='a',<459>,1:80]], active=[[@30,122:122='a',<459>,1:122]], id=[[@1,7:7='a',<459>,1:7]], label=[[@5,13:13='a',<459>,1:13]], item_three=[[@22,92:92='a',<459>,1:92]], item_one=[[@14,68:68='a',<459>,1:68]]}}",
				"{query0={id=[[@3,9:10='id',<459>,1:9]], label=[[@7,15:19='label',<459>,1:15]]}}",
				"{def_query0={query_dictionary={id=[[@3,9:10='id',<459>,1:9]], label=[[@7,15:19='label',<459>,1:15]]}, table_dictionary={events={item_two=[[@18,80:80='a',<459>,1:80]], active=[[@30,122:122='a',<459>,1:122]], id=[[@1,7:7='a',<459>,1:7]], label=[[@5,13:13='a',<459>,1:13]], item_three=[[@22,92:92='a',<459>,1:92]], item_one=[[@14,68:68='a',<459>,1:68]]}}, filters=[{name=item_one, table_ref=a}, {name=item_two, table_ref=a}, {name=item_three, table_ref=a}, {name=active, table_ref=a}], interface={id=[{name=id, table_ref=a}], label=[{name=label, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC025_array_construct_structuredJoinPlacementV0Test() {
		final String query = "SELECT a.id, b.label FROM events a JOIN labels b ON array_construct_structured(a.item_one, a.item_two, a.item_three) = b.ref_value AND a.id = b.event_id WHERE b.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_routine_invocation).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_construct_structured' at (l:1 c:52) is recognized as Snowflake-specific grammar.",
						1, 52, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=a, table=events}}, 2={join=JOIN, on={and={1={condition={left={function={parameters={1={column={name=item_one, table_ref=a}}, 2={column={name=item_two, table_ref=a}}, 3={column={name=item_three, table_ref=a}}}, function_name=array_construct_structured}}, right={column={name=ref_value, table_ref=b}}, operator==}}, 2={condition={left={column={name=id, table_ref=a}}, right={column={name=event_id, table_ref=b}}, operator==}}}}}, 3={table={alias=b, table=labels}}}}, where={condition={left={column={name=active, table_ref=b}}, right={literal=1}, operator==}}}}",
				"[id, label]",
				"{}",
				"{events={item_two=[[@21,91:91='a',<459>,1:91]], id=[[@1,7:7='a',<459>,1:7], [@34,135:135='a',<459>,1:135]], item_three=[[@25,103:103='a',<459>,1:103]], item_one=[[@17,79:79='a',<459>,1:79]]}, labels={ref_value=[[@30,119:119='b',<459>,1:119]], event_id=[[@38,142:142='b',<459>,1:142]], active=[[@42,159:159='b',<459>,1:159]], label=[[@5,13:13='b',<459>,1:13]]}}",
				"{query0={id=[[@3,9:10='id',<459>,1:9]], label=[[@7,15:19='label',<459>,1:15]]}}",
				"{def_query0={query_dictionary={id=[[@3,9:10='id',<459>,1:9]], label=[[@7,15:19='label',<459>,1:15]]}, table_dictionary={events={item_two=[[@21,91:91='a',<459>,1:91]], id=[[@1,7:7='a',<459>,1:7], [@34,135:135='a',<459>,1:135]], item_three=[[@25,103:103='a',<459>,1:103]], item_one=[[@17,79:79='a',<459>,1:79]]}, labels={ref_value=[[@30,119:119='b',<459>,1:119]], event_id=[[@38,142:142='b',<459>,1:142]], active=[[@42,159:159='b',<459>,1:159]], label=[[@5,13:13='b',<459>,1:13]]}}, filters=[{name=item_one, table_ref=a}, {name=item_two, table_ref=a}, {name=item_three, table_ref=a}, {name=ref_value, table_ref=b}, {name=id, table_ref=a}, {name=event_id, table_ref=b}, {name=active, table_ref=b}], interface={id=[{name=id, table_ref=a}], label=[{name=label, table_ref=b}]}, table_alias={a=events, b=labels}}}");
	}

	@Test
	public void arC025_array_construct_structuredHavingPlacementV0Test() {
		final String query = "SELECT a.id, count(*) AS total FROM events a GROUP BY a.id, a.item_one, a.item_two, a.item_three HAVING array_construct_structured(a.item_one, a.item_two, a.item_three) IS NOT NULL";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_routine_invocation).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_construct_structured' at (l:1 c:104) is recognized as Snowflake-specific grammar.",
						1, 104, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={function_name=COUNT, qualifier=null, parameters=*}, alias=total}}, having={condition={left={function={parameters={1={column={name=item_one, table_ref=a}}, 2={column={name=item_two, table_ref=a}}, 3={column={name=item_three, table_ref=a}}}, function_name=array_construct_structured}}, operator=IS NOT NULL}}, from={table={alias=a, table=events}}, groupby={1={column={name=id, table_ref=a}}, 2={column={name=item_one, table_ref=a}}, 3={column={name=item_two, table_ref=a}}, 4={column={name=item_three, table_ref=a}}}}}",
				"[total, id]",
				"{}",
				"{events={item_two=[[@24,72:72='a',<459>,1:72], [@38,143:143='a',<459>,1:143]], *=[[@7,19:19='*',<291>,1:19]], id=[[@1,7:7='a',<459>,1:7], [@16,54:54='a',<459>,1:54]], item_three=[[@28,84:84='a',<459>,1:84], [@42,155:155='a',<459>,1:155]], item_one=[[@20,60:60='a',<459>,1:60], [@34,131:131='a',<459>,1:131]]}}",
				"{query0={total=[[@10,25:29='total',<459>,1:25]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={total=[[@10,25:29='total',<459>,1:25]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={item_two=[[@24,72:72='a',<459>,1:72], [@38,143:143='a',<459>,1:143]], *=[[@7,19:19='*',<291>,1:19]], id=[[@1,7:7='a',<459>,1:7], [@16,54:54='a',<459>,1:54]], item_three=[[@28,84:84='a',<459>,1:84], [@42,155:155='a',<459>,1:155]], item_one=[[@20,60:60='a',<459>,1:60], [@34,131:131='a',<459>,1:131]]}}, grouped_by=[{name=id, table_ref=a}, {name=item_one, table_ref=a}, {name=item_two, table_ref=a}, {name=item_three, table_ref=a}], filters=[{name=item_one, table_ref=a}, {name=item_two, table_ref=a}, {name=item_three, table_ref=a}], interface={total=[], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC025_array_construct_structuredQualifyPlacementV0Test() {
		final String query = "SELECT a.id, row_number() over (partition by a.group_id order by a.id) AS rn FROM events a QUALIFY array_construct_structured(a.item_one, a.item_two, a.item_three) IS NOT NULL AND rn <= 5";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_routine_invocation).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_construct_structured' at (l:1 c:99) is recognized as Snowflake-specific grammar.",
						1, 99, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={alias=rn, window_function={over={partition_by={1={column={name=group_id, table_ref=a}}}, orderby={1={null_order=null, predicand={column={name=id, table_ref=a}}, sort_order=ASC}}}, function={function_name=row_number, parameters=null}}}}, from={table={alias=a, table=events}}, qualify={and={1={condition={left={function={parameters={1={column={name=item_one, table_ref=a}}, 2={column={name=item_two, table_ref=a}}, 3={column={name=item_three, table_ref=a}}}, function_name=array_construct_structured}}, operator=IS NOT NULL}}, 2={condition={left={column={name=rn, table_ref=null}}, right={literal=5}, operator=<=}}}}}}",
				"[id, rn]",
				"{}",
				"{events={item_two=[[@33,138:138='a',<459>,1:138]], group_id=[[@12,45:45='a',<459>,1:45]], id=[[@1,7:7='a',<459>,1:7], [@17,65:65='a',<459>,1:65]], item_three=[[@37,150:150='a',<459>,1:150]], item_one=[[@29,126:126='a',<459>,1:126]]}}",
				"{query0={rn=[[@22,74:75='rn',<459>,1:74], [@45,180:181='rn',<459>,1:180]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={window_ordered_by=[{name=id, table_ref=a}], query_dictionary={id=[[@3,9:10='id',<459>,1:9]], rn=[[@22,74:75='rn',<459>,1:74], [@45,180:181='rn',<459>,1:180]]}, table_dictionary={events={item_two=[[@33,138:138='a',<459>,1:138]], group_id=[[@12,45:45='a',<459>,1:45]], id=[[@1,7:7='a',<459>,1:7], [@17,65:65='a',<459>,1:65]], item_three=[[@37,150:150='a',<459>,1:150]], item_one=[[@29,126:126='a',<459>,1:126]]}}, window_partition_by=[{name=group_id, table_ref=a}], filters=[{name=item_one, table_ref=a}, {name=item_two, table_ref=a}, {name=item_three, table_ref=a}, {name=rn, table_ref=query0}], interface={id=[{name=id, table_ref=a}], rn=[{name=group_id, table_ref=a}, {name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC025_array_construct_structuredOrderByPlacementV0Test() {
		final String query = "SELECT a.id FROM events a WHERE a.active = 1 ORDER BY array_construct_structured(a.item_one, a.item_two, a.item_three), a.id";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_routine_invocation).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_construct_structured' at (l:1 c:54) is recognized as Snowflake-specific grammar.",
						1, 54, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}}, orderby={1={null_order=null, predicand={function={parameters={1={column={name=item_one, table_ref=a}}, 2={column={name=item_two, table_ref=a}}, 3={column={name=item_three, table_ref=a}}}, function_name=array_construct_structured}}, sort_order=ASC}, 2={null_order=null, predicand={column={name=id, table_ref=a}}, sort_order=ASC}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[id]",
				"{}",
				"{events={item_two=[[@21,93:93='a',<459>,1:93]], active=[[@8,32:32='a',<459>,1:32]], id=[[@1,7:7='a',<459>,1:7], [@30,120:120='a',<459>,1:120]], item_three=[[@25,105:105='a',<459>,1:105]], item_one=[[@17,81:81='a',<459>,1:81]]}}",
				"{query0={id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={item_two=[[@21,93:93='a',<459>,1:93]], active=[[@8,32:32='a',<459>,1:32]], id=[[@1,7:7='a',<459>,1:7], [@30,120:120='a',<459>,1:120]], item_three=[[@25,105:105='a',<459>,1:105]], item_one=[[@17,81:81='a',<459>,1:81]]}}, ordered_by=[{name=item_one, table_ref=a}, {name=item_two, table_ref=a}, {name=item_three, table_ref=a}, {name=id, table_ref=a}], filters=[{name=active, table_ref=a}], interface={id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC025_array_construct_structuredValuesPlacementV0Test() {
		final String query = "VALUES (7, array_construct_structured(1, 2, 3))";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_routine_invocation).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_construct_structured' at (l:1 c:11) is recognized as Snowflake-specific grammar.",
						1, 11, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={values={matrix={1={row={1={literal=7}, 2={function={parameters={1={literal=1}, 2={literal=2}, 3={literal=3}}, function_name=array_construct_structured}}}}}}}}",
				"[$1, $2]",
				"{}",
				"{}",
				"{values0={$1=[[@1,7:7='(',<287>,1:7]], $2=[[@1,7:7='(',<287>,1:7]]}}",
				"{def_values0={query_dictionary={$1=[[@1,7:7='(',<287>,1:7]], $2=[[@1,7:7='(',<287>,1:7]]}, interface={$1=[], $2=[]}}}");
	}

	@Test
	public void arC025_array_construct_structuredInsertPlacementV0Test() {
		final String query = "INSERT INTO results (id, result_value) SELECT a.id, array_construct_structured(a.item_one, a.item_two, a.item_three) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_routine_invocation).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_construct_structured' at (l:1 c:52) is recognized as Snowflake-specific grammar.",
						1, 52, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={insert={preamble=insert_into, from={from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}, select={1={column={name=id, table_ref=a}}, 2={function={parameters={1={column={name=item_one, table_ref=a}}, 2={column={name=item_two, table_ref=a}}, 3={column={name=item_three, table_ref=a}}}, function_name=array_construct_structured}, alias=result_value}}}, target_table={table={alias=null, table=results}}, columns={1={column={name=id, table_ref=null}}, 2={column={name=result_value, table_ref=null}}}}}}",
				"[result_value, id]",
				"{}",
				"{results={result_value=[[@6,25:36='result_value',<459>,1:25]], id=[[@4,21:22='id',<459>,1:21]]}, events={item_two=[[@19,91:91='a',<459>,1:91]], active=[[@33,153:153='a',<459>,1:153]], id=[[@9,46:46='a',<459>,1:46]], item_three=[[@23,103:103='a',<459>,1:103]], item_one=[[@15,79:79='a',<459>,1:79]]}}",
				"{query0={result_value=[[@28,120:131='result_value',<459>,1:120]], id=[[@11,48:49='id',<459>,1:48]]}, insert1={result_value=[[@6,25:36='result_value',<459>,1:25]], id=[[@4,21:22='id',<459>,1:21]]}}",
				"{def_insert1={query_dictionary={result_value=[[@6,25:36='result_value',<459>,1:25]], id=[[@4,21:22='id',<459>,1:21]]}, table_dictionary={results={result_value=[[@6,25:36='result_value',<459>,1:25]], id=[[@4,21:22='id',<459>,1:21]]}}, def_query0={query_dictionary={result_value=[[@28,120:131='result_value',<459>,1:120]], id=[[@11,48:49='id',<459>,1:48]]}, table_dictionary={events={item_two=[[@19,91:91='a',<459>,1:91]], active=[[@33,153:153='a',<459>,1:153]], id=[[@9,46:46='a',<459>,1:46]], item_three=[[@23,103:103='a',<459>,1:103]], item_one=[[@15,79:79='a',<459>,1:79]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=item_one, table_ref=a}, {name=item_two, table_ref=a}, {name=item_three, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}, interface={id=[{name=id, table_ref=query0}], result_value=[{name=result_value, table_ref=query0}]}}}");
	}

	@Test
	public void arC025_array_construct_structuredUpdatePlacementV0Test() {
		final String query = "UPDATE events SET result_value = array_construct_structured(item_one, item_two, item_three) WHERE active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_routine_invocation).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_construct_structured' at (l:1 c:33) is recognized as Snowflake-specific grammar.",
						1, 33, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={update={table={alias=null, table=events}, where={condition={left={column={name=active, table_ref=null}}, right={literal=1}, operator==}}, assignments={1={set={column={name=result_value, table_ref=null}}, to={function={parameters={1={column={name=item_one, table_ref=null}}, 2={column={name=item_two, table_ref=null}}, 3={column={name=item_three, table_ref=null}}}, function_name=array_construct_structured}}}}}}}",
				"[result_value]",
				"{}",
				"{events={item_two=[[@9,70:77='item_two',<459>,1:70], [@12,90:90=')',<288>,1:90]], active=[[@14,98:103='active',<459>,1:98]], item_three=[[@11,80:89='item_three',<459>,1:80], [@12,90:90=')',<288>,1:90]], item_one=[[@7,60:67='item_one',<459>,1:60], [@12,90:90=')',<288>,1:90]]}}",
				"{update0={result_value=[[@3,18:29='result_value',<459>,1:18]]}}",
				"{def_update0={assignments={result_value=[{name=item_one, table_ref=events}, {name=item_two, table_ref=events}, {name=item_three, table_ref=events}]}, table_dictionary={events={item_two=[[@9,70:77='item_two',<459>,1:70], [@12,90:90=')',<288>,1:90]], active=[[@14,98:103='active',<459>,1:98]], item_three=[[@11,80:89='item_three',<459>,1:80], [@12,90:90=')',<288>,1:90]], item_one=[[@7,60:67='item_one',<459>,1:60], [@12,90:90=')',<288>,1:90]]}}, update_dictionary={result_value=[[@3,18:29='result_value',<459>,1:18]]}, target_table={events={result_value=[[@3,18:29='result_value',<459>,1:18]]}}, filters=[{name=active, table_ref=events}], lhs_unresolved_columns={result_value={column={name=result_value, table_ref=null}, locations=[[@3,18:29='result_value',<459>,1:18]]}}}}");
	}

	@Test
	public void arC025_array_construct_structuredDeletePlacementV0Test() {
		final String query = "DELETE FROM events WHERE array_construct_structured(item_one, item_two, item_three) IS NULL AND active = 0";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_routine_invocation).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_construct_structured' at (l:1 c:25) is recognized as Snowflake-specific grammar.",
						1, 25, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={delete={table={alias=null, table=events}, where={and={1={condition={left={function={parameters={1={column={name=item_one, table_ref=null}}, 2={column={name=item_two, table_ref=null}}, 3={column={name=item_three, table_ref=null}}}, function_name=array_construct_structured}}, operator=IS NULL}}, 2={condition={left={column={name=active, table_ref=null}}, right={literal=0}, operator==}}}}}}}",
				"[]",
				"{}",
				"{events={item_two=[[@8,62:69='item_two',<459>,1:62]], active=[[@15,96:101='active',<459>,1:96]], item_three=[[@10,72:81='item_three',<459>,1:72]], item_one=[[@6,52:59='item_one',<459>,1:52]]}}",
				"{}",
				"{def_delete0={query_dictionary={}, table_dictionary={events={item_two=[[@8,62:69='item_two',<459>,1:62]], active=[[@15,96:101='active',<459>,1:96]], item_three=[[@10,72:81='item_three',<459>,1:72]], item_one=[[@6,52:59='item_one',<459>,1:52]]}}, filters=[{name=item_one, table_ref=events}, {name=item_two, table_ref=events}, {name=item_three, table_ref=events}, {name=active, table_ref=events}], interface=null}}");
	}

	@Test
	public void arC025_array_construct_structuredDdlPlacementV0Test() {
		final String query = "CREATE VIEW v_array_construct_structured AS SELECT a.id, array_construct_structured(a.item_one, a.item_two, a.item_three) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.DdlContext tree = parser.ddl();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_routine_invocation).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_construct_structured' at (l:1 c:57) is recognized as Snowflake-specific grammar.",
						1, 57, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{DDL={create={type=VIEW, name={table=v_array_construct_structured}, query={select={1={column={name=id, table_ref=a}}, 2={function={parameters={1={column={name=item_one, table_ref=a}}, 2={column={name=item_two, table_ref=a}}, 3={column={name=item_three, table_ref=a}}}, function_name=array_construct_structured}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}}}",
				"[]",
				"{}",
				"{events={item_two=[[@15,96:96='a',<459>,1:96]], active=[[@29,158:158='a',<459>,1:158]], id=[[@5,51:51='a',<459>,1:51]], item_three=[[@19,108:108='a',<459>,1:108]], item_one=[[@11,84:84='a',<459>,1:84]]}}",
				"{query0={result_value=[[@24,125:136='result_value',<459>,1:125]], id=[[@7,53:54='id',<459>,1:53]]}}",
				"{def_create1={def_query0={query_dictionary={result_value=[[@24,125:136='result_value',<459>,1:125]], id=[[@7,53:54='id',<459>,1:53]]}, table_dictionary={events={item_two=[[@15,96:96='a',<459>,1:96]], active=[[@29,158:158='a',<459>,1:158]], id=[[@5,51:51='a',<459>,1:51]], item_three=[[@19,108:108='a',<459>,1:108]], item_one=[[@11,84:84='a',<459>,1:84]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=item_one, table_ref=a}, {name=item_two, table_ref=a}, {name=item_three, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}}");
	}

	@Test
	public void arC025_array_construct_structuredScriptPlacementV0Test() {
		final String query = "SELECT a.id, array_construct_structured(a.item_one, a.item_two, a.item_three) AS result_value FROM events a WHERE a.active = 1;\nSELECT b.id FROM orders b WHERE b.active = 1;\nSELECT c.id, array_construct_structured(c.item_one, c.item_two, c.item_three) AS other_value FROM archive_events c WHERE c.active = 1;";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.ScriptContext tree = parser.script();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 2,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_routine_invocation).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_construct_structured' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null),
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_construct_structured' at (l:3 c:13) is recognized as Snowflake-specific grammar.",
						3, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SCRIPT={1={select={1={column={name=id, table_ref=a}}, 2={function={parameters={1={column={name=item_one, table_ref=a}}, 2={column={name=item_two, table_ref=a}}, 3={column={name=item_three, table_ref=a}}}, function_name=array_construct_structured}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}, 2={select={1={column={name=id, table_ref=b}}}, from={table={alias=b, table=orders}}, where={condition={left={column={name=active, table_ref=b}}, right={literal=1}, operator==}}}, 3={select={1={column={name=id, table_ref=c}}, 2={function={parameters={1={column={name=item_one, table_ref=c}}, 2={column={name=item_two, table_ref=c}}, 3={column={name=item_three, table_ref=c}}}, function_name=array_construct_structured}, alias=other_value}}, from={table={alias=c, table=archive_events}}, where={condition={left={column={name=active, table_ref=c}}, right={literal=1}, operator==}}}}}",
				"[]",
				"{SCRIPT={1={}, 2={}, 3={}}}",
				"{SCRIPT={1={events={item_two=[[@11,52:52='a',<459>,1:52]], active=[[@25,114:114='a',<459>,1:114]], id=[[@1,7:7='a',<459>,1:7]], item_three=[[@15,64:64='a',<459>,1:64]], item_one=[[@7,40:40='a',<459>,1:40]]}}, 2={orders={active=[[@39,160:160='b',<459>,2:32]], id=[[@32,135:135='b',<459>,2:7]]}}, 3={archive_events={item_two=[[@56,226:226='c',<459>,3:52]], active=[[@70,295:295='c',<459>,3:121]], id=[[@46,181:181='c',<459>,3:7]], item_three=[[@60,238:238='c',<459>,3:64]], item_one=[[@52,214:214='c',<459>,3:40]]}}}}",
				"{SCRIPT={1={query0={result_value=[[@20,81:92='result_value',<459>,1:81]], id=[[@3,9:10='id',<459>,1:9]]}}, 2={query0={id=[[@34,137:138='id',<459>,2:9]]}}, 3={query0={other_value=[[@65,255:265='other_value',<459>,3:81]], id=[[@48,183:184='id',<459>,3:9]]}}}}",
				"{SCRIPT={1={def_query0={query_dictionary={result_value=[[@20,81:92='result_value',<459>,1:81]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={item_two=[[@11,52:52='a',<459>,1:52]], active=[[@25,114:114='a',<459>,1:114]], id=[[@1,7:7='a',<459>,1:7]], item_three=[[@15,64:64='a',<459>,1:64]], item_one=[[@7,40:40='a',<459>,1:40]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=item_one, table_ref=a}, {name=item_two, table_ref=a}, {name=item_three, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}, 2={def_query0={query_dictionary={id=[[@34,137:138='id',<459>,2:9]]}, table_dictionary={orders={active=[[@39,160:160='b',<459>,2:32]], id=[[@32,135:135='b',<459>,2:7]]}}, filters=[{name=active, table_ref=b}], interface={id=[{name=id, table_ref=b}]}, table_alias={b=orders}}}, 3={def_query0={query_dictionary={other_value=[[@65,255:265='other_value',<459>,3:81]], id=[[@48,183:184='id',<459>,3:9]]}, table_dictionary={archive_events={item_two=[[@56,226:226='c',<459>,3:52]], active=[[@70,295:295='c',<459>,3:121]], id=[[@46,181:181='c',<459>,3:7]], item_three=[[@60,238:238='c',<459>,3:64]], item_one=[[@52,214:214='c',<459>,3:40]]}}, filters=[{name=active, table_ref=c}], interface={other_value=[{name=item_one, table_ref=c}, {name=item_two, table_ref=c}, {name=item_three, table_ref=c}], id=[{name=id, table_ref=c}]}, table_alias={c=archive_events}}}}}");
	}

	@Test
	public void arC025_array_construct_structuredArg1UnqualifiedV0Test() {
		final String query = "SELECT a.id, array_construct_structured(item_one, a.item_two, a.item_three) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_routine_invocation).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_construct_structured' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={parameters={1={column={name=item_one, table_ref=null}}, 2={column={name=item_two, table_ref=a}}, 3={column={name=item_three, table_ref=a}}}, function_name=array_construct_structured}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{}",
				"{events={item_two=[[@9,50:50='a',<459>,1:50]], active=[[@23,112:112='a',<459>,1:112]], id=[[@1,7:7='a',<459>,1:7]], item_three=[[@13,62:62='a',<459>,1:62]], item_one=[[@7,40:47='item_one',<459>,1:40]]}}",
				"{query0={result_value=[[@18,79:90='result_value',<459>,1:79]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@18,79:90='result_value',<459>,1:79]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={item_two=[[@9,50:50='a',<459>,1:50]], active=[[@23,112:112='a',<459>,1:112]], id=[[@1,7:7='a',<459>,1:7]], item_three=[[@13,62:62='a',<459>,1:62]], item_one=[[@7,40:47='item_one',<459>,1:40]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=item_one, table_ref=events}, {name=item_two, table_ref=a}, {name=item_three, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC025_array_construct_structuredArg1UnresolvedV0Test() {
		final String query = "SELECT 7 AS marker, array_construct_structured(item_one, 2, 3) AS result_value";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_routine_invocation).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_construct_structured' at (l:1 c:20) is recognized as Snowflake-specific grammar.",
						1, 20, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null),
				new ParseDiagnostic(ParseDiagnostic.Severity.ERROR,
						"UNRESOLVED_UNQUALIFIED_COLUMNS",
						"Unresolved unqualified column reference(s): [item_one [(l:1 c:47)]]",
						1, 47, "SqlASTWalkerHelper", null, "item_one", false, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={alias=marker, literal=7}, 2={function={parameters={1={column={name=item_one, table_ref=null}}, 2={literal=2}, 3={literal=3}}, function_name=array_construct_structured}, alias=result_value}}}}",
				"[marker, result_value]",
				"{}",
				"{}",
				"{query0={marker=[[@3,12:17='marker',<459>,1:12]], result_value=[[@14,66:77='result_value',<459>,1:66]]}}",
				"{def_query0={query_dictionary={marker=[[@3,12:17='marker',<459>,1:12]], result_value=[[@14,66:77='result_value',<459>,1:66]]}, interface={marker=[], result_value=[{name=item_one, table_ref=null}]}}}");
	}

	@Test
	public void arC025_array_construct_structuredArg1AmbiguousV0Test() {
		final String query = "SELECT a.id, array_construct_structured(item_one, a.item_two, a.item_three) AS result_value FROM events a JOIN labels b ON a.id = b.event_id WHERE b.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_routine_invocation).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_construct_structured' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null),
				new ParseDiagnostic(ParseDiagnostic.Severity.SEVERE_WARNING,
						"AMBIGUOUS_COLUMN_REFERENCE",
						"Ambiguous column reference 'item_one' at (l:1 c:40). Possible sources: [events, labels]",
						1, 40, "SqlASTWalkerHelper", null, "item_one", true, "ast-walk", null, null),
				new ParseDiagnostic(ParseDiagnostic.Severity.ERROR,
						"UNRESOLVED_UNQUALIFIED_COLUMNS",
						"Unresolved unqualified column reference(s): [item_one [(l:1 c:40)]]",
						1, 40, "SqlASTWalkerHelper", null, "item_one", false, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={parameters={1={column={name=item_one, table_ref=null}}, 2={column={name=item_two, table_ref=a}}, 3={column={name=item_three, table_ref=a}}}, function_name=array_construct_structured}, alias=result_value}}, from={join={1={table={alias=a, table=events}}, 2={join=JOIN, on={condition={left={column={name=id, table_ref=a}}, right={column={name=event_id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=labels}}}}, where={condition={left={column={name=active, table_ref=b}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{}",
				"{events={item_two=[[@9,50:50='a',<459>,1:50]], id=[[@1,7:7='a',<459>,1:7], [@26,123:123='a',<459>,1:123]], item_three=[[@13,62:62='a',<459>,1:62]]}, labels={event_id=[[@30,130:130='b',<459>,1:130]], active=[[@34,147:147='b',<459>,1:147]]}}",
				"{query0={result_value=[[@18,79:90='result_value',<459>,1:79]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@18,79:90='result_value',<459>,1:79]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={item_two=[[@9,50:50='a',<459>,1:50]], id=[[@1,7:7='a',<459>,1:7], [@26,123:123='a',<459>,1:123]], item_three=[[@13,62:62='a',<459>,1:62]]}, labels={event_id=[[@30,130:130='b',<459>,1:130]], active=[[@34,147:147='b',<459>,1:147]]}}, filters=[{name=id, table_ref=a}, {name=event_id, table_ref=b}, {name=active, table_ref=b}], interface={result_value=[{name=item_one, table_ref=null}, {name=item_two, table_ref=a}, {name=item_three, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events, b=labels}}}");
	}

	@Test
	public void arC025_array_construct_structuredArg1MissingAliasV0Test() {
		final String query = "SELECT a.id, array_construct_structured(z.item_one, a.item_two, a.item_three) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_routine_invocation).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_construct_structured' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null),
				new ParseDiagnostic(ParseDiagnostic.Severity.FATAL,
						"QUALIFIED_COLUMN_NOT_FOUND_IN_TABLE",
						"Source Table not found for Column 'item_one' at (l:1 c:81). No alias or table called 'z'.",
						1, 81, "SqlASTWalkerHelper", null, "item_one", false, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={parameters={1={column={name=item_one, table_ref=z}}, 2={column={name=item_two, table_ref=a}}, 3={column={name=item_three, table_ref=a}}}, function_name=array_construct_structured}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{}",
				"{events={item_two=[[@11,52:52='a',<459>,1:52]], active=[[@25,114:114='a',<459>,1:114]], id=[[@1,7:7='a',<459>,1:7]], item_three=[[@15,64:64='a',<459>,1:64]]}}",
				"{query0={result_value=[[@20,81:92='result_value',<459>,1:81]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@20,81:92='result_value',<459>,1:81]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={item_two=[[@11,52:52='a',<459>,1:52]], active=[[@25,114:114='a',<459>,1:114]], id=[[@1,7:7='a',<459>,1:7]], item_three=[[@15,64:64='a',<459>,1:64]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=item_one, table_ref=z}, {name=item_two, table_ref=a}, {name=item_three, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC025_array_construct_structuredArg1HiddenAliasV0Test() {
		final String query = "SELECT q.id, array_construct_structured(a.item_one, q.item_two, q.item_three) AS result_value FROM (SELECT a.id, a.item_one, a.item_two, a.item_three FROM events a WHERE a.active = 1) q WHERE q.id > 0";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_routine_invocation).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_construct_structured' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null),
				new ParseDiagnostic(ParseDiagnostic.Severity.FATAL,
						"QUALIFIED_COLUMN_NOT_FOUND_IN_TABLE",
						"Source Table not found for Column 'item_one' at (l:1 c:81). No alias or table called 'a'.",
						1, 81, "SqlASTWalkerHelper", null, "item_one", false, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=q}}, 2={function={parameters={1={column={name=item_one, table_ref=a}}, 2={column={name=item_two, table_ref=q}}, 3={column={name=item_three, table_ref=q}}}, function_name=array_construct_structured}, alias=result_value}}, from={table={alias=q, query={select={1={column={name=id, table_ref=a}}, 2={column={name=item_one, table_ref=a}}, 3={column={name=item_two, table_ref=a}}, 4={column={name=item_three, table_ref=a}}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}}, where={condition={left={column={name=id, table_ref=q}}, right={literal=0}, operator=>}}}}",
				"[result_value, id]",
				"{}",
				"{events={item_two=[[@32,125:125='a',<459>,1:125]], active=[[@43,170:170='a',<459>,1:170]], id=[[@24,107:107='a',<459>,1:107]], item_three=[[@36,137:137='a',<459>,1:137]], item_one=[[@28,113:113='a',<459>,1:113]]}}",
				"{query0={id=[[@26,109:110='id',<459>,1:109], [@1,7:7='q',<459>,1:7], [@51,192:192='q',<459>,1:192]], item_two=[[@34,127:134='item_two',<459>,1:127], [@11,52:52='q',<459>,1:52]], item_three=[[@38,139:148='item_three',<459>,1:139], [@15,64:64='q',<459>,1:64]], item_one=[[@30,115:122='item_one',<459>,1:115]]}, query1={result_value=[[@20,81:92='result_value',<459>,1:81]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query1={query_dictionary={result_value=[[@20,81:92='result_value',<459>,1:81]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={}, def_query0={query_dictionary={item_two=[[@34,127:134='item_two',<459>,1:127], [@11,52:52='q',<459>,1:52]], id=[[@26,109:110='id',<459>,1:109], [@1,7:7='q',<459>,1:7], [@51,192:192='q',<459>,1:192]], item_three=[[@38,139:148='item_three',<459>,1:139], [@15,64:64='q',<459>,1:64]], item_one=[[@30,115:122='item_one',<459>,1:115]]}, table_dictionary={events={item_two=[[@32,125:125='a',<459>,1:125]], active=[[@43,170:170='a',<459>,1:170]], id=[[@24,107:107='a',<459>,1:107]], item_three=[[@36,137:137='a',<459>,1:137]], item_one=[[@28,113:113='a',<459>,1:113]]}}, filters=[{name=active, table_ref=a}], interface={item_two=[{name=item_two, table_ref=a}], id=[{name=id, table_ref=a}], item_three=[{name=item_three, table_ref=a}], item_one=[{name=item_one, table_ref=a}]}, table_alias={a=events}}, filters=[{name=id, table_ref=q}], interface={result_value=[{name=item_one, table_ref=a}, {name=item_two, table_ref=q}, {name=item_three, table_ref=q}], id=[{name=id, table_ref=q}]}, table_alias={q=query0}}}");
	}

	@Test
	public void arC025_array_construct_structuredArg1PredicandVariableV0Test() {
		final String query = "SELECT a.id, array_construct_structured(<item_one_expr>, a.item_two, a.item_three) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_routine_invocation).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_construct_structured' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={parameters={1={substitution={name=<item_one_expr>, type=predicand}}, 2={column={name=item_two, table_ref=a}}, 3={column={name=item_three, table_ref=a}}}, function_name=array_construct_structured}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{<item_one_expr>=predicand}",
				"{events={item_two=[[@9,57:57='a',<459>,1:57]], active=[[@23,119:119='a',<459>,1:119]], id=[[@1,7:7='a',<459>,1:7]], item_three=[[@13,69:69='a',<459>,1:69]]}}",
				"{query0={result_value=[[@18,86:97='result_value',<459>,1:86]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@18,86:97='result_value',<459>,1:86]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={item_two=[[@9,57:57='a',<459>,1:57]], active=[[@23,119:119='a',<459>,1:119]], id=[[@1,7:7='a',<459>,1:7]], item_three=[[@13,69:69='a',<459>,1:69]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=<item_one_expr>, type=predicand}, {name=item_two, table_ref=a}, {name=item_three, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC025_array_construct_structuredArg1ColumnVariableV0Test() {
		final String query = "SELECT a.id, array_construct_structured(a.<item_one_col>, a.item_two, a.item_three) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_routine_invocation).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_construct_structured' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={parameters={1={column={substitution={name=<item_one_col>, type=column}, table_ref=a}}, 2={column={name=item_two, table_ref=a}}, 3={column={name=item_three, table_ref=a}}}, function_name=array_construct_structured}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{<item_one_col>=column}",
				"{events={item_two=[[@11,58:58='a',<459>,1:58]], <item_one_col>=[[@7,40:40='a',<459>,1:40]], active=[[@25,120:120='a',<459>,1:120]], id=[[@1,7:7='a',<459>,1:7]], item_three=[[@15,70:70='a',<459>,1:70]]}}",
				"{query0={result_value=[[@20,87:98='result_value',<459>,1:87]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@20,87:98='result_value',<459>,1:87]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={item_two=[[@11,58:58='a',<459>,1:58]], <item_one_col>=[[@7,40:40='a',<459>,1:40]], active=[[@25,120:120='a',<459>,1:120]], id=[[@1,7:7='a',<459>,1:7]], item_three=[[@15,70:70='a',<459>,1:70]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{substitution={name=<item_one_col>, type=column}, table_ref=a}, {name=item_two, table_ref=a}, {name=item_three, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC025_array_construct_structuredArg1NullV0Test() {
		final String query = "SELECT a.id, array_construct_structured(NULL, a.item_two, a.item_three) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_routine_invocation).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_construct_structured' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={parameters={1={null_literal=null}, 2={column={name=item_two, table_ref=a}}, 3={column={name=item_three, table_ref=a}}}, function_name=array_construct_structured}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{}",
				"{events={item_two=[[@9,46:46='a',<459>,1:46]], active=[[@23,108:108='a',<459>,1:108]], id=[[@1,7:7='a',<459>,1:7]], item_three=[[@13,58:58='a',<459>,1:58]]}}",
				"{query0={result_value=[[@18,75:86='result_value',<459>,1:75]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@18,75:86='result_value',<459>,1:75]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={item_two=[[@9,46:46='a',<459>,1:46]], active=[[@23,108:108='a',<459>,1:108]], id=[[@1,7:7='a',<459>,1:7]], item_three=[[@13,58:58='a',<459>,1:58]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=item_two, table_ref=a}, {name=item_three, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC025_array_construct_structuredArg2UnqualifiedV0Test() {
		final String query = "SELECT a.id, array_construct_structured(a.item_one, item_two, a.item_three) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_routine_invocation).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_construct_structured' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={parameters={1={column={name=item_one, table_ref=a}}, 2={column={name=item_two, table_ref=null}}, 3={column={name=item_three, table_ref=a}}}, function_name=array_construct_structured}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{}",
				"{events={item_two=[[@11,52:59='item_two',<459>,1:52]], active=[[@23,112:112='a',<459>,1:112]], id=[[@1,7:7='a',<459>,1:7]], item_three=[[@13,62:62='a',<459>,1:62]], item_one=[[@7,40:40='a',<459>,1:40]]}}",
				"{query0={result_value=[[@18,79:90='result_value',<459>,1:79]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@18,79:90='result_value',<459>,1:79]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={item_two=[[@11,52:59='item_two',<459>,1:52]], active=[[@23,112:112='a',<459>,1:112]], id=[[@1,7:7='a',<459>,1:7]], item_three=[[@13,62:62='a',<459>,1:62]], item_one=[[@7,40:40='a',<459>,1:40]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=item_one, table_ref=a}, {name=item_two, table_ref=events}, {name=item_three, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC025_array_construct_structuredArg2UnresolvedV0Test() {
		final String query = "SELECT 7 AS marker, array_construct_structured(1, item_two, 3) AS result_value";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_routine_invocation).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_construct_structured' at (l:1 c:20) is recognized as Snowflake-specific grammar.",
						1, 20, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null),
				new ParseDiagnostic(ParseDiagnostic.Severity.ERROR,
						"UNRESOLVED_UNQUALIFIED_COLUMNS",
						"Unresolved unqualified column reference(s): [item_two [(l:1 c:50)]]",
						1, 50, "SqlASTWalkerHelper", null, "item_two", false, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={alias=marker, literal=7}, 2={function={parameters={1={literal=1}, 2={column={name=item_two, table_ref=null}}, 3={literal=3}}, function_name=array_construct_structured}, alias=result_value}}}}",
				"[marker, result_value]",
				"{}",
				"{}",
				"{query0={marker=[[@3,12:17='marker',<459>,1:12]], result_value=[[@14,66:77='result_value',<459>,1:66]]}}",
				"{def_query0={query_dictionary={marker=[[@3,12:17='marker',<459>,1:12]], result_value=[[@14,66:77='result_value',<459>,1:66]]}, interface={marker=[], result_value=[{name=item_two, table_ref=null}]}}}");
	}

	@Test
	public void arC025_array_construct_structuredArg2AmbiguousV0Test() {
		final String query = "SELECT a.id, array_construct_structured(a.item_one, item_two, a.item_three) AS result_value FROM events a JOIN labels b ON a.id = b.event_id WHERE b.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_routine_invocation).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_construct_structured' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null),
				new ParseDiagnostic(ParseDiagnostic.Severity.SEVERE_WARNING,
						"AMBIGUOUS_COLUMN_REFERENCE",
						"Ambiguous column reference 'item_two' at (l:1 c:52). Possible sources: [events, labels]",
						1, 52, "SqlASTWalkerHelper", null, "item_two", true, "ast-walk", null, null),
				new ParseDiagnostic(ParseDiagnostic.Severity.ERROR,
						"UNRESOLVED_UNQUALIFIED_COLUMNS",
						"Unresolved unqualified column reference(s): [item_two [(l:1 c:52)]]",
						1, 52, "SqlASTWalkerHelper", null, "item_two", false, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={parameters={1={column={name=item_one, table_ref=a}}, 2={column={name=item_two, table_ref=null}}, 3={column={name=item_three, table_ref=a}}}, function_name=array_construct_structured}, alias=result_value}}, from={join={1={table={alias=a, table=events}}, 2={join=JOIN, on={condition={left={column={name=id, table_ref=a}}, right={column={name=event_id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=labels}}}}, where={condition={left={column={name=active, table_ref=b}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{}",
				"{events={id=[[@1,7:7='a',<459>,1:7], [@26,123:123='a',<459>,1:123]], item_three=[[@13,62:62='a',<459>,1:62]], item_one=[[@7,40:40='a',<459>,1:40]]}, labels={event_id=[[@30,130:130='b',<459>,1:130]], active=[[@34,147:147='b',<459>,1:147]]}}",
				"{query0={result_value=[[@18,79:90='result_value',<459>,1:79]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@18,79:90='result_value',<459>,1:79]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={id=[[@1,7:7='a',<459>,1:7], [@26,123:123='a',<459>,1:123]], item_three=[[@13,62:62='a',<459>,1:62]], item_one=[[@7,40:40='a',<459>,1:40]]}, labels={event_id=[[@30,130:130='b',<459>,1:130]], active=[[@34,147:147='b',<459>,1:147]]}}, filters=[{name=id, table_ref=a}, {name=event_id, table_ref=b}, {name=active, table_ref=b}], interface={result_value=[{name=item_one, table_ref=a}, {name=item_two, table_ref=null}, {name=item_three, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events, b=labels}}}");
	}

	@Test
	public void arC025_array_construct_structuredArg2MissingAliasV0Test() {
		final String query = "SELECT a.id, array_construct_structured(a.item_one, z.item_two, a.item_three) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_routine_invocation).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_construct_structured' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null),
				new ParseDiagnostic(ParseDiagnostic.Severity.FATAL,
						"QUALIFIED_COLUMN_NOT_FOUND_IN_TABLE",
						"Source Table not found for Column 'item_two' at (l:1 c:81). No alias or table called 'z'.",
						1, 81, "SqlASTWalkerHelper", null, "item_two", false, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={parameters={1={column={name=item_one, table_ref=a}}, 2={column={name=item_two, table_ref=z}}, 3={column={name=item_three, table_ref=a}}}, function_name=array_construct_structured}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{}",
				"{events={active=[[@25,114:114='a',<459>,1:114]], id=[[@1,7:7='a',<459>,1:7]], item_three=[[@15,64:64='a',<459>,1:64]], item_one=[[@7,40:40='a',<459>,1:40]]}}",
				"{query0={result_value=[[@20,81:92='result_value',<459>,1:81]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@20,81:92='result_value',<459>,1:81]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={active=[[@25,114:114='a',<459>,1:114]], id=[[@1,7:7='a',<459>,1:7]], item_three=[[@15,64:64='a',<459>,1:64]], item_one=[[@7,40:40='a',<459>,1:40]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=item_one, table_ref=a}, {name=item_two, table_ref=z}, {name=item_three, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC025_array_construct_structuredArg2HiddenAliasV0Test() {
		final String query = "SELECT q.id, array_construct_structured(q.item_one, a.item_two, q.item_three) AS result_value FROM (SELECT a.id, a.item_one, a.item_two, a.item_three FROM events a WHERE a.active = 1) q WHERE q.id > 0";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_routine_invocation).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_construct_structured' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null),
				new ParseDiagnostic(ParseDiagnostic.Severity.FATAL,
						"QUALIFIED_COLUMN_NOT_FOUND_IN_TABLE",
						"Source Table not found for Column 'item_two' at (l:1 c:81). No alias or table called 'a'.",
						1, 81, "SqlASTWalkerHelper", null, "item_two", false, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=q}}, 2={function={parameters={1={column={name=item_one, table_ref=q}}, 2={column={name=item_two, table_ref=a}}, 3={column={name=item_three, table_ref=q}}}, function_name=array_construct_structured}, alias=result_value}}, from={table={alias=q, query={select={1={column={name=id, table_ref=a}}, 2={column={name=item_one, table_ref=a}}, 3={column={name=item_two, table_ref=a}}, 4={column={name=item_three, table_ref=a}}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}}, where={condition={left={column={name=id, table_ref=q}}, right={literal=0}, operator=>}}}}",
				"[result_value, id]",
				"{}",
				"{events={item_two=[[@32,125:125='a',<459>,1:125]], active=[[@43,170:170='a',<459>,1:170]], id=[[@24,107:107='a',<459>,1:107]], item_three=[[@36,137:137='a',<459>,1:137]], item_one=[[@28,113:113='a',<459>,1:113]]}}",
				"{query0={id=[[@26,109:110='id',<459>,1:109], [@1,7:7='q',<459>,1:7], [@51,192:192='q',<459>,1:192]], item_two=[[@34,127:134='item_two',<459>,1:127]], item_three=[[@38,139:148='item_three',<459>,1:139], [@15,64:64='q',<459>,1:64]], item_one=[[@30,115:122='item_one',<459>,1:115], [@7,40:40='q',<459>,1:40]]}, query1={result_value=[[@20,81:92='result_value',<459>,1:81]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query1={query_dictionary={result_value=[[@20,81:92='result_value',<459>,1:81]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={}, def_query0={query_dictionary={item_two=[[@34,127:134='item_two',<459>,1:127]], id=[[@26,109:110='id',<459>,1:109], [@1,7:7='q',<459>,1:7], [@51,192:192='q',<459>,1:192]], item_three=[[@38,139:148='item_three',<459>,1:139], [@15,64:64='q',<459>,1:64]], item_one=[[@30,115:122='item_one',<459>,1:115], [@7,40:40='q',<459>,1:40]]}, table_dictionary={events={item_two=[[@32,125:125='a',<459>,1:125]], active=[[@43,170:170='a',<459>,1:170]], id=[[@24,107:107='a',<459>,1:107]], item_three=[[@36,137:137='a',<459>,1:137]], item_one=[[@28,113:113='a',<459>,1:113]]}}, filters=[{name=active, table_ref=a}], interface={item_two=[{name=item_two, table_ref=a}], id=[{name=id, table_ref=a}], item_three=[{name=item_three, table_ref=a}], item_one=[{name=item_one, table_ref=a}]}, table_alias={a=events}}, filters=[{name=id, table_ref=q}], interface={result_value=[{name=item_one, table_ref=q}, {name=item_two, table_ref=a}, {name=item_three, table_ref=q}], id=[{name=id, table_ref=q}]}, table_alias={q=query0}}}");
	}

	@Test
	public void arC025_array_construct_structuredArg2PredicandVariableV0Test() {
		final String query = "SELECT a.id, array_construct_structured(a.item_one, <item_two_expr>, a.item_three) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_routine_invocation).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_construct_structured' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={parameters={1={column={name=item_one, table_ref=a}}, 2={substitution={name=<item_two_expr>, type=predicand}}, 3={column={name=item_three, table_ref=a}}}, function_name=array_construct_structured}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{<item_two_expr>=predicand}",
				"{events={active=[[@23,119:119='a',<459>,1:119]], id=[[@1,7:7='a',<459>,1:7]], item_three=[[@13,69:69='a',<459>,1:69]], item_one=[[@7,40:40='a',<459>,1:40]]}}",
				"{query0={result_value=[[@18,86:97='result_value',<459>,1:86]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@18,86:97='result_value',<459>,1:86]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={active=[[@23,119:119='a',<459>,1:119]], id=[[@1,7:7='a',<459>,1:7]], item_three=[[@13,69:69='a',<459>,1:69]], item_one=[[@7,40:40='a',<459>,1:40]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=item_one, table_ref=a}, {name=<item_two_expr>, type=predicand}, {name=item_three, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC025_array_construct_structuredArg2ColumnVariableV0Test() {
		final String query = "SELECT a.id, array_construct_structured(a.item_one, a.<item_two_col>, a.item_three) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_routine_invocation).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_construct_structured' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={parameters={1={column={name=item_one, table_ref=a}}, 2={column={substitution={name=<item_two_col>, type=column}, table_ref=a}}, 3={column={name=item_three, table_ref=a}}}, function_name=array_construct_structured}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{<item_two_col>=column}",
				"{events={active=[[@25,120:120='a',<459>,1:120]], id=[[@1,7:7='a',<459>,1:7]], <item_two_col>=[[@11,52:52='a',<459>,1:52]], item_three=[[@15,70:70='a',<459>,1:70]], item_one=[[@7,40:40='a',<459>,1:40]]}}",
				"{query0={result_value=[[@20,87:98='result_value',<459>,1:87]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@20,87:98='result_value',<459>,1:87]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={active=[[@25,120:120='a',<459>,1:120]], id=[[@1,7:7='a',<459>,1:7]], <item_two_col>=[[@11,52:52='a',<459>,1:52]], item_three=[[@15,70:70='a',<459>,1:70]], item_one=[[@7,40:40='a',<459>,1:40]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=item_one, table_ref=a}, {substitution={name=<item_two_col>, type=column}, table_ref=a}, {name=item_three, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC025_array_construct_structuredArg2NullV0Test() {
		final String query = "SELECT a.id, array_construct_structured(a.item_one, NULL, a.item_three) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_routine_invocation).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_construct_structured' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={parameters={1={column={name=item_one, table_ref=a}}, 2={null_literal=null}, 3={column={name=item_three, table_ref=a}}}, function_name=array_construct_structured}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{}",
				"{events={active=[[@23,108:108='a',<459>,1:108]], id=[[@1,7:7='a',<459>,1:7]], item_three=[[@13,58:58='a',<459>,1:58]], item_one=[[@7,40:40='a',<459>,1:40]]}}",
				"{query0={result_value=[[@18,75:86='result_value',<459>,1:75]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@18,75:86='result_value',<459>,1:75]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={active=[[@23,108:108='a',<459>,1:108]], id=[[@1,7:7='a',<459>,1:7]], item_three=[[@13,58:58='a',<459>,1:58]], item_one=[[@7,40:40='a',<459>,1:40]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=item_one, table_ref=a}, {name=item_three, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC025_array_construct_structuredArg3UnqualifiedV0Test() {
		final String query = "SELECT a.id, array_construct_structured(a.item_one, a.item_two, item_three) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_routine_invocation).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_construct_structured' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={parameters={1={column={name=item_one, table_ref=a}}, 2={column={name=item_two, table_ref=a}}, 3={column={name=item_three, table_ref=null}}}, function_name=array_construct_structured}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{}",
				"{events={item_two=[[@11,52:52='a',<459>,1:52]], active=[[@23,112:112='a',<459>,1:112]], id=[[@1,7:7='a',<459>,1:7]], item_three=[[@15,64:73='item_three',<459>,1:64]], item_one=[[@7,40:40='a',<459>,1:40]]}}",
				"{query0={result_value=[[@18,79:90='result_value',<459>,1:79]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@18,79:90='result_value',<459>,1:79]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={item_two=[[@11,52:52='a',<459>,1:52]], active=[[@23,112:112='a',<459>,1:112]], id=[[@1,7:7='a',<459>,1:7]], item_three=[[@15,64:73='item_three',<459>,1:64]], item_one=[[@7,40:40='a',<459>,1:40]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=item_one, table_ref=a}, {name=item_two, table_ref=a}, {name=item_three, table_ref=events}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC025_array_construct_structuredArg3UnresolvedV0Test() {
		final String query = "SELECT 7 AS marker, array_construct_structured(1, 2, item_three) AS result_value";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_routine_invocation).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_construct_structured' at (l:1 c:20) is recognized as Snowflake-specific grammar.",
						1, 20, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null),
				new ParseDiagnostic(ParseDiagnostic.Severity.ERROR,
						"UNRESOLVED_UNQUALIFIED_COLUMNS",
						"Unresolved unqualified column reference(s): [item_three [(l:1 c:53)]]",
						1, 53, "SqlASTWalkerHelper", null, "item_three", false, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={alias=marker, literal=7}, 2={function={parameters={1={literal=1}, 2={literal=2}, 3={column={name=item_three, table_ref=null}}}, function_name=array_construct_structured}, alias=result_value}}}}",
				"[marker, result_value]",
				"{}",
				"{}",
				"{query0={marker=[[@3,12:17='marker',<459>,1:12]], result_value=[[@14,68:79='result_value',<459>,1:68]]}}",
				"{def_query0={query_dictionary={marker=[[@3,12:17='marker',<459>,1:12]], result_value=[[@14,68:79='result_value',<459>,1:68]]}, interface={marker=[], result_value=[{name=item_three, table_ref=null}]}}}");
	}

	@Test
	public void arC025_array_construct_structuredArg3AmbiguousV0Test() {
		final String query = "SELECT a.id, array_construct_structured(a.item_one, a.item_two, item_three) AS result_value FROM events a JOIN labels b ON a.id = b.event_id WHERE b.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_routine_invocation).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_construct_structured' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null),
				new ParseDiagnostic(ParseDiagnostic.Severity.SEVERE_WARNING,
						"AMBIGUOUS_COLUMN_REFERENCE",
						"Ambiguous column reference 'item_three' at (l:1 c:64). Possible sources: [events, labels]",
						1, 64, "SqlASTWalkerHelper", null, "item_three", true, "ast-walk", null, null),
				new ParseDiagnostic(ParseDiagnostic.Severity.ERROR,
						"UNRESOLVED_UNQUALIFIED_COLUMNS",
						"Unresolved unqualified column reference(s): [item_three [(l:1 c:64)]]",
						1, 64, "SqlASTWalkerHelper", null, "item_three", false, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={parameters={1={column={name=item_one, table_ref=a}}, 2={column={name=item_two, table_ref=a}}, 3={column={name=item_three, table_ref=null}}}, function_name=array_construct_structured}, alias=result_value}}, from={join={1={table={alias=a, table=events}}, 2={join=JOIN, on={condition={left={column={name=id, table_ref=a}}, right={column={name=event_id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=labels}}}}, where={condition={left={column={name=active, table_ref=b}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{}",
				"{events={item_two=[[@11,52:52='a',<459>,1:52]], id=[[@1,7:7='a',<459>,1:7], [@26,123:123='a',<459>,1:123]], item_one=[[@7,40:40='a',<459>,1:40]]}, labels={event_id=[[@30,130:130='b',<459>,1:130]], active=[[@34,147:147='b',<459>,1:147]]}}",
				"{query0={result_value=[[@18,79:90='result_value',<459>,1:79]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@18,79:90='result_value',<459>,1:79]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={item_two=[[@11,52:52='a',<459>,1:52]], id=[[@1,7:7='a',<459>,1:7], [@26,123:123='a',<459>,1:123]], item_one=[[@7,40:40='a',<459>,1:40]]}, labels={event_id=[[@30,130:130='b',<459>,1:130]], active=[[@34,147:147='b',<459>,1:147]]}}, filters=[{name=id, table_ref=a}, {name=event_id, table_ref=b}, {name=active, table_ref=b}], interface={result_value=[{name=item_one, table_ref=a}, {name=item_two, table_ref=a}, {name=item_three, table_ref=null}], id=[{name=id, table_ref=a}]}, table_alias={a=events, b=labels}}}");
	}

	@Test
	public void arC025_array_construct_structuredArg3MissingAliasV0Test() {
		final String query = "SELECT a.id, array_construct_structured(a.item_one, a.item_two, z.item_three) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_routine_invocation).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_construct_structured' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null),
				new ParseDiagnostic(ParseDiagnostic.Severity.FATAL,
						"QUALIFIED_COLUMN_NOT_FOUND_IN_TABLE",
						"Source Table not found for Column 'item_three' at (l:1 c:81). No alias or table called 'z'.",
						1, 81, "SqlASTWalkerHelper", null, "item_three", false, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={parameters={1={column={name=item_one, table_ref=a}}, 2={column={name=item_two, table_ref=a}}, 3={column={name=item_three, table_ref=z}}}, function_name=array_construct_structured}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{}",
				"{events={item_two=[[@11,52:52='a',<459>,1:52]], active=[[@25,114:114='a',<459>,1:114]], id=[[@1,7:7='a',<459>,1:7]], item_one=[[@7,40:40='a',<459>,1:40]]}}",
				"{query0={result_value=[[@20,81:92='result_value',<459>,1:81]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@20,81:92='result_value',<459>,1:81]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={item_two=[[@11,52:52='a',<459>,1:52]], active=[[@25,114:114='a',<459>,1:114]], id=[[@1,7:7='a',<459>,1:7]], item_one=[[@7,40:40='a',<459>,1:40]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=item_one, table_ref=a}, {name=item_two, table_ref=a}, {name=item_three, table_ref=z}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC025_array_construct_structuredArg3HiddenAliasV0Test() {
		final String query = "SELECT q.id, array_construct_structured(q.item_one, q.item_two, a.item_three) AS result_value FROM (SELECT a.id, a.item_one, a.item_two, a.item_three FROM events a WHERE a.active = 1) q WHERE q.id > 0";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_routine_invocation).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_construct_structured' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null),
				new ParseDiagnostic(ParseDiagnostic.Severity.FATAL,
						"QUALIFIED_COLUMN_NOT_FOUND_IN_TABLE",
						"Source Table not found for Column 'item_three' at (l:1 c:81). No alias or table called 'a'.",
						1, 81, "SqlASTWalkerHelper", null, "item_three", false, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=q}}, 2={function={parameters={1={column={name=item_one, table_ref=q}}, 2={column={name=item_two, table_ref=q}}, 3={column={name=item_three, table_ref=a}}}, function_name=array_construct_structured}, alias=result_value}}, from={table={alias=q, query={select={1={column={name=id, table_ref=a}}, 2={column={name=item_one, table_ref=a}}, 3={column={name=item_two, table_ref=a}}, 4={column={name=item_three, table_ref=a}}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}}, where={condition={left={column={name=id, table_ref=q}}, right={literal=0}, operator=>}}}}",
				"[result_value, id]",
				"{}",
				"{events={item_two=[[@32,125:125='a',<459>,1:125]], active=[[@43,170:170='a',<459>,1:170]], id=[[@24,107:107='a',<459>,1:107]], item_three=[[@36,137:137='a',<459>,1:137]], item_one=[[@28,113:113='a',<459>,1:113]]}}",
				"{query0={id=[[@26,109:110='id',<459>,1:109], [@1,7:7='q',<459>,1:7], [@51,192:192='q',<459>,1:192]], item_two=[[@34,127:134='item_two',<459>,1:127], [@11,52:52='q',<459>,1:52]], item_three=[[@38,139:148='item_three',<459>,1:139]], item_one=[[@30,115:122='item_one',<459>,1:115], [@7,40:40='q',<459>,1:40]]}, query1={result_value=[[@20,81:92='result_value',<459>,1:81]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query1={query_dictionary={result_value=[[@20,81:92='result_value',<459>,1:81]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={}, def_query0={query_dictionary={item_two=[[@34,127:134='item_two',<459>,1:127], [@11,52:52='q',<459>,1:52]], id=[[@26,109:110='id',<459>,1:109], [@1,7:7='q',<459>,1:7], [@51,192:192='q',<459>,1:192]], item_three=[[@38,139:148='item_three',<459>,1:139]], item_one=[[@30,115:122='item_one',<459>,1:115], [@7,40:40='q',<459>,1:40]]}, table_dictionary={events={item_two=[[@32,125:125='a',<459>,1:125]], active=[[@43,170:170='a',<459>,1:170]], id=[[@24,107:107='a',<459>,1:107]], item_three=[[@36,137:137='a',<459>,1:137]], item_one=[[@28,113:113='a',<459>,1:113]]}}, filters=[{name=active, table_ref=a}], interface={item_two=[{name=item_two, table_ref=a}], id=[{name=id, table_ref=a}], item_three=[{name=item_three, table_ref=a}], item_one=[{name=item_one, table_ref=a}]}, table_alias={a=events}}, filters=[{name=id, table_ref=q}], interface={result_value=[{name=item_one, table_ref=q}, {name=item_two, table_ref=q}, {name=item_three, table_ref=a}], id=[{name=id, table_ref=q}]}, table_alias={q=query0}}}");
	}

	@Test
	public void arC025_array_construct_structuredArg3PredicandVariableV0Test() {
		final String query = "SELECT a.id, array_construct_structured(a.item_one, a.item_two, <item_three_expr>) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_routine_invocation).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_construct_structured' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={parameters={1={column={name=item_one, table_ref=a}}, 2={column={name=item_two, table_ref=a}}, 3={substitution={name=<item_three_expr>, type=predicand}}}, function_name=array_construct_structured}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{<item_three_expr>=predicand}",
				"{events={item_two=[[@11,52:52='a',<459>,1:52]], active=[[@23,119:119='a',<459>,1:119]], id=[[@1,7:7='a',<459>,1:7]], item_one=[[@7,40:40='a',<459>,1:40]]}}",
				"{query0={result_value=[[@18,86:97='result_value',<459>,1:86]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@18,86:97='result_value',<459>,1:86]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={item_two=[[@11,52:52='a',<459>,1:52]], active=[[@23,119:119='a',<459>,1:119]], id=[[@1,7:7='a',<459>,1:7]], item_one=[[@7,40:40='a',<459>,1:40]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=item_one, table_ref=a}, {name=item_two, table_ref=a}, {name=<item_three_expr>, type=predicand}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC025_array_construct_structuredArg3ColumnVariableV0Test() {
		final String query = "SELECT a.id, array_construct_structured(a.item_one, a.item_two, a.<item_three_col>) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_routine_invocation).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_construct_structured' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={parameters={1={column={name=item_one, table_ref=a}}, 2={column={name=item_two, table_ref=a}}, 3={column={substitution={name=<item_three_col>, type=column}, table_ref=a}}}, function_name=array_construct_structured}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{<item_three_col>=column}",
				"{events={item_two=[[@11,52:52='a',<459>,1:52]], active=[[@25,120:120='a',<459>,1:120]], id=[[@1,7:7='a',<459>,1:7]], <item_three_col>=[[@15,64:64='a',<459>,1:64]], item_one=[[@7,40:40='a',<459>,1:40]]}}",
				"{query0={result_value=[[@20,87:98='result_value',<459>,1:87]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@20,87:98='result_value',<459>,1:87]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={item_two=[[@11,52:52='a',<459>,1:52]], active=[[@25,120:120='a',<459>,1:120]], id=[[@1,7:7='a',<459>,1:7]], <item_three_col>=[[@15,64:64='a',<459>,1:64]], item_one=[[@7,40:40='a',<459>,1:40]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=item_one, table_ref=a}, {name=item_two, table_ref=a}, {substitution={name=<item_three_col>, type=column}, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC025_array_construct_structuredArg3NullV0Test() {
		final String query = "SELECT a.id, array_construct_structured(a.item_one, a.item_two, NULL) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_routine_invocation).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_construct_structured' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={parameters={1={column={name=item_one, table_ref=a}}, 2={column={name=item_two, table_ref=a}}, 3={null_literal=null}}, function_name=array_construct_structured}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{}",
				"{events={item_two=[[@11,52:52='a',<459>,1:52]], active=[[@23,106:106='a',<459>,1:106]], id=[[@1,7:7='a',<459>,1:7]], item_one=[[@7,40:40='a',<459>,1:40]]}}",
				"{query0={result_value=[[@18,73:84='result_value',<459>,1:73]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@18,73:84='result_value',<459>,1:73]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={item_two=[[@11,52:52='a',<459>,1:52]], active=[[@23,106:106='a',<459>,1:106]], id=[[@1,7:7='a',<459>,1:7]], item_one=[[@7,40:40='a',<459>,1:40]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=item_one, table_ref=a}, {name=item_two, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC025_array_construct_structuredUnqualifiedOperandsV0Test() {
		final String query = "SELECT a.id, array_construct_structured(item_one, item_two, item_three) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_routine_invocation).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_construct_structured' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={parameters={1={column={name=item_one, table_ref=null}}, 2={column={name=item_two, table_ref=null}}, 3={column={name=item_three, table_ref=null}}}, function_name=array_construct_structured}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{}",
				"{events={item_two=[[@9,50:57='item_two',<459>,1:50]], active=[[@19,108:108='a',<459>,1:108]], id=[[@1,7:7='a',<459>,1:7]], item_three=[[@11,60:69='item_three',<459>,1:60]], item_one=[[@7,40:47='item_one',<459>,1:40]]}}",
				"{query0={result_value=[[@14,75:86='result_value',<459>,1:75]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@14,75:86='result_value',<459>,1:75]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={item_two=[[@9,50:57='item_two',<459>,1:50]], active=[[@19,108:108='a',<459>,1:108]], id=[[@1,7:7='a',<459>,1:7]], item_three=[[@11,60:69='item_three',<459>,1:60]], item_one=[[@7,40:47='item_one',<459>,1:40]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=item_one, table_ref=events}, {name=item_two, table_ref=events}, {name=item_three, table_ref=events}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC025_array_construct_structuredAllPredicandVariablesV0Test() {
		final String query = "SELECT a.id, array_construct_structured(<item_one_predicand>, <item_two_predicand>, <item_three_predicand>) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_routine_invocation).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_construct_structured' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={parameters={1={substitution={name=<item_one_predicand>, type=predicand}}, 2={substitution={name=<item_two_predicand>, type=predicand}}, 3={substitution={name=<item_three_predicand>, type=predicand}}}, function_name=array_construct_structured}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{<item_two_predicand>=predicand, <item_one_predicand>=predicand, <item_three_predicand>=predicand}",
				"{events={active=[[@19,144:144='a',<459>,1:144]], id=[[@1,7:7='a',<459>,1:7]]}}",
				"{query0={result_value=[[@14,111:122='result_value',<459>,1:111]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@14,111:122='result_value',<459>,1:111]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={active=[[@19,144:144='a',<459>,1:144]], id=[[@1,7:7='a',<459>,1:7]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=<item_one_predicand>, type=predicand}, {name=<item_two_predicand>, type=predicand}, {name=<item_three_predicand>, type=predicand}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC025_array_construct_structuredAllColumnVariablesV0Test() {
		final String query = "SELECT a.id, array_construct_structured(a.<item_one_column>, a.<item_two_column>, a.<item_three_column>) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_routine_invocation).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_construct_structured' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={parameters={1={column={substitution={name=<item_one_column>, type=column}, table_ref=a}}, 2={column={substitution={name=<item_two_column>, type=column}, table_ref=a}}, 3={column={substitution={name=<item_three_column>, type=column}, table_ref=a}}}, function_name=array_construct_structured}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{<item_one_column>=column, <item_two_column>=column, <item_three_column>=column}",
				"{events={<item_one_column>=[[@7,40:40='a',<459>,1:40]], <item_two_column>=[[@11,61:61='a',<459>,1:61]], active=[[@25,141:141='a',<459>,1:141]], id=[[@1,7:7='a',<459>,1:7]], <item_three_column>=[[@15,82:82='a',<459>,1:82]]}}",
				"{query0={result_value=[[@20,108:119='result_value',<459>,1:108]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@20,108:119='result_value',<459>,1:108]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={<item_one_column>=[[@7,40:40='a',<459>,1:40]], <item_two_column>=[[@11,61:61='a',<459>,1:61]], active=[[@25,141:141='a',<459>,1:141]], id=[[@1,7:7='a',<459>,1:7]], <item_three_column>=[[@15,82:82='a',<459>,1:82]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{substitution={name=<item_one_column>, type=column}, table_ref=a}, {substitution={name=<item_two_column>, type=column}, table_ref=a}, {substitution={name=<item_three_column>, type=column}, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC025_array_construct_structuredNestedExpressionV0Test() {
		final String query = "SELECT a.id, coalesce(array_construct_structured(a.item_one, a.item_two, a.item_three), a.fallback_value) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 2,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_routine_invocation).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_construct_structured' at (l:1 c:22) is recognized as Snowflake-specific grammar.",
						1, 22, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={parameters={1={function={parameters={1={column={name=item_one, table_ref=a}}, 2={column={name=item_two, table_ref=a}}, 3={column={name=item_three, table_ref=a}}}, function_name=array_construct_structured}}, 2={column={name=fallback_value, table_ref=a}}}, function_name=coalesce}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{}",
				"{events={fallback_value=[[@22,88:88='a',<459>,1:88]], item_two=[[@13,61:61='a',<459>,1:61]], active=[[@32,142:142='a',<459>,1:142]], id=[[@1,7:7='a',<459>,1:7]], item_three=[[@17,73:73='a',<459>,1:73]], item_one=[[@9,49:49='a',<459>,1:49]]}}",
				"{query0={result_value=[[@27,109:120='result_value',<459>,1:109]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@27,109:120='result_value',<459>,1:109]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={fallback_value=[[@22,88:88='a',<459>,1:88]], item_two=[[@13,61:61='a',<459>,1:61]], active=[[@32,142:142='a',<459>,1:142]], id=[[@1,7:7='a',<459>,1:7]], item_three=[[@17,73:73='a',<459>,1:73]], item_one=[[@9,49:49='a',<459>,1:49]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=item_one, table_ref=a}, {name=item_two, table_ref=a}, {name=item_three, table_ref=a}, {name=fallback_value, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC025_array_construct_structuredDerivedOutputV0Test() {
		final String query = "SELECT q.id, q.result_value FROM (SELECT a.id, array_construct_structured(a.item_one, a.item_two, a.item_three) AS result_value FROM events a WHERE a.active = 1) q WHERE q.result_value IS NOT NULL ORDER BY q.id";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_routine_invocation).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_construct_structured' at (l:1 c:47) is recognized as Snowflake-specific grammar.",
						1, 47, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=q}}, 2={column={name=result_value, table_ref=q}}}, orderby={1={null_order=null, predicand={column={name=id, table_ref=q}}, sort_order=ASC}}, from={table={alias=q, query={select={1={column={name=id, table_ref=a}}, 2={function={parameters={1={column={name=item_one, table_ref=a}}, 2={column={name=item_two, table_ref=a}}, 3={column={name=item_three, table_ref=a}}}, function_name=array_construct_structured}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}}, where={condition={left={column={name=result_value, table_ref=q}}, operator=IS NOT NULL}}}}",
				"[result_value, id]",
				"{}",
				"{events={item_two=[[@21,86:86='a',<459>,1:86]], active=[[@35,148:148='a',<459>,1:148]], id=[[@11,41:41='a',<459>,1:41]], item_three=[[@25,98:98='a',<459>,1:98]], item_one=[[@17,74:74='a',<459>,1:74]]}}",
				"{query0={result_value=[[@30,115:126='result_value',<459>,1:115], [@5,13:13='q',<459>,1:13], [@43,170:170='q',<459>,1:170]], id=[[@13,43:44='id',<459>,1:43], [@1,7:7='q',<459>,1:7], [@51,206:206='q',<459>,1:206]]}, query1={result_value=[[@7,15:26='result_value',<459>,1:15]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query1={query_dictionary={result_value=[[@7,15:26='result_value',<459>,1:15]], id=[[@3,9:10='id',<459>,1:9]]}, def_query0={query_dictionary={result_value=[[@30,115:126='result_value',<459>,1:115], [@5,13:13='q',<459>,1:13], [@43,170:170='q',<459>,1:170]], id=[[@13,43:44='id',<459>,1:43], [@1,7:7='q',<459>,1:7], [@51,206:206='q',<459>,1:206]]}, table_dictionary={events={item_two=[[@21,86:86='a',<459>,1:86]], active=[[@35,148:148='a',<459>,1:148]], id=[[@11,41:41='a',<459>,1:41]], item_three=[[@25,98:98='a',<459>,1:98]], item_one=[[@17,74:74='a',<459>,1:74]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=item_one, table_ref=a}, {name=item_two, table_ref=a}, {name=item_three, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}, ordered_by=[{name=id, table_ref=q}], filters=[{name=result_value, table_ref=q}], interface={result_value=[{name=result_value, table_ref=q}], id=[{name=id, table_ref=q}]}, table_alias={q=query0}}}");
	}

	@Test
	public void arC025_array_construct_structuredSetOpSiblingIsolationV0Test() {
		final String query = "SELECT a.id AS left_id, array_construct_structured(a.item_one, a.item_two, a.item_three) AS left_value FROM events a WHERE a.active = 1 UNION ALL SELECT b.id AS right_id, b.other_value AS right_value FROM archive_events b WHERE b.archived = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_routine_invocation).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_construct_structured' at (l:1 c:24) is recognized as Snowflake-specific grammar.",
						1, 24, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={union={1={select={1={column={name=id, table_ref=a}, alias=left_id}, 2={function={parameters={1={column={name=item_one, table_ref=a}}, 2={column={name=item_two, table_ref=a}}, 3={column={name=item_three, table_ref=a}}}, function_name=array_construct_structured}, alias=left_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}, 2={union={qualifier=ALL, operator=UNION}}, 3={select={1={column={name=id, table_ref=b}, alias=right_id}, 2={column={name=other_value, table_ref=b}, alias=right_value}}, from={table={alias=b, table=archive_events}}, where={condition={left={column={name=archived, table_ref=b}}, right={literal=1}, operator==}}}}}}",
				"[left_value, left_id]",
				"{}",
				"{archive_events={archived=[[@50,228:228='b',<459>,1:228]], other_value=[[@41,171:171='b',<459>,1:171]], id=[[@35,153:153='b',<459>,1:153]]}, events={item_two=[[@13,63:63='a',<459>,1:63]], active=[[@27,123:123='a',<459>,1:123]], id=[[@1,7:7='a',<459>,1:7]], item_three=[[@17,75:75='a',<459>,1:75]], item_one=[[@9,51:51='a',<459>,1:51]]}}",
				"{query0={left_id=[[@5,15:21='left_id',<459>,1:15]], left_value=[[@22,92:101='left_value',<459>,1:92]]}, query1={right_id=[[@39,161:168='right_id',<459>,1:161]], right_value=[[@45,188:198='right_value',<459>,1:188]]}}",
				"{def_union2={def_query1={query_dictionary={right_id=[[@39,161:168='right_id',<459>,1:161]], right_value=[[@45,188:198='right_value',<459>,1:188]]}, table_dictionary={archive_events={archived=[[@50,228:228='b',<459>,1:228]], other_value=[[@41,171:171='b',<459>,1:171]], id=[[@35,153:153='b',<459>,1:153]]}}, setop=UNION, filters=[{name=archived, table_ref=b}], interface={right_id=[{name=id, table_ref=b}], right_value=[{name=other_value, table_ref=b}]}, table_alias={b=archive_events}}, def_query0={query_dictionary={left_value=[[@22,92:101='left_value',<459>,1:92]], left_id=[[@5,15:21='left_id',<459>,1:15]]}, table_dictionary={events={item_two=[[@13,63:63='a',<459>,1:63]], active=[[@27,123:123='a',<459>,1:123]], id=[[@1,7:7='a',<459>,1:7]], item_three=[[@17,75:75='a',<459>,1:75]], item_one=[[@9,51:51='a',<459>,1:51]]}}, filters=[{name=active, table_ref=a}], interface={left_value=[{name=item_one, table_ref=a}, {name=item_two, table_ref=a}, {name=item_three, table_ref=a}], left_id=[{name=id, table_ref=a}]}, table_alias={a=events}}, interface={left_value=query_column, left_id=query_column}}}");
	}

	@Test
	public void arC025_array_construct_structuredQualifiedUdfNeutralV0Test() {
		final String query = "SELECT a.id, util.array_construct_structured(a.item_one, a.item_two, a.item_three) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_routine_invocation).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={schema=util, parameters={1={column={name=item_one, table_ref=a}}, 2={column={name=item_two, table_ref=a}}, 3={column={name=item_three, table_ref=a}}}, function_name=array_construct_structured}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{}",
				"{events={item_two=[[@13,57:57='a',<459>,1:57]], active=[[@27,119:119='a',<459>,1:119]], id=[[@1,7:7='a',<459>,1:7]], item_three=[[@17,69:69='a',<459>,1:69]], item_one=[[@9,45:45='a',<459>,1:45]]}}",
				"{query0={result_value=[[@22,86:97='result_value',<459>,1:86]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@22,86:97='result_value',<459>,1:86]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={item_two=[[@13,57:57='a',<459>,1:57]], active=[[@27,119:119='a',<459>,1:119]], id=[[@1,7:7='a',<459>,1:7]], item_three=[[@17,69:69='a',<459>,1:69]], item_one=[[@9,45:45='a',<459>,1:45]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=item_one, table_ref=a}, {name=item_two, table_ref=a}, {name=item_three, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC025_array_construct_structuredSameNamedColumnNeutralV0Test() {
		final String query = "SELECT a.id, a.array_construct_structured AS stored_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 0,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_routine_invocation).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={column={name=array_construct_structured, table_ref=a}, alias=stored_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[id, stored_value]",
				"{}",
				"{events={active=[[@14,78:78='a',<459>,1:78]], id=[[@1,7:7='a',<459>,1:7]], array_construct_structured=[[@5,13:13='a',<459>,1:13]]}}",
				"{query0={id=[[@3,9:10='id',<459>,1:9]], stored_value=[[@9,45:56='stored_value',<459>,1:45]]}}",
				"{def_query0={query_dictionary={id=[[@3,9:10='id',<459>,1:9]], stored_value=[[@9,45:56='stored_value',<459>,1:45]]}, table_dictionary={events={active=[[@14,78:78='a',<459>,1:78]], id=[[@1,7:7='a',<459>,1:7]], array_construct_structured=[[@5,13:13='a',<459>,1:13]]}}, filters=[{name=active, table_ref=a}], interface={id=[{name=id, table_ref=a}], stored_value=[{name=array_construct_structured, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC025_array_construct_structuredUppercaseV0Test() {
		final String query = "SELECT a.id, ARRAY_CONSTRUCT_STRUCTURED(a.item_one, a.item_two, a.item_three) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_routine_invocation).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'ARRAY_CONSTRUCT_STRUCTURED' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={parameters={1={column={name=item_one, table_ref=a}}, 2={column={name=item_two, table_ref=a}}, 3={column={name=item_three, table_ref=a}}}, function_name=ARRAY_CONSTRUCT_STRUCTURED}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{}",
				"{events={item_two=[[@11,52:52='a',<459>,1:52]], active=[[@25,114:114='a',<459>,1:114]], id=[[@1,7:7='a',<459>,1:7]], item_three=[[@15,64:64='a',<459>,1:64]], item_one=[[@7,40:40='a',<459>,1:40]]}}",
				"{query0={result_value=[[@20,81:92='result_value',<459>,1:81]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@20,81:92='result_value',<459>,1:81]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={item_two=[[@11,52:52='a',<459>,1:52]], active=[[@25,114:114='a',<459>,1:114]], id=[[@1,7:7='a',<459>,1:7]], item_three=[[@15,64:64='a',<459>,1:64]], item_one=[[@7,40:40='a',<459>,1:40]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=item_one, table_ref=a}, {name=item_two, table_ref=a}, {name=item_three, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC025_array_construct_structuredMixedDialectV0Test() {
		final String query = "SELECT a.id, array_construct_structured(a.item_one, a.item_two, a.item_three) AS result_value, extract(isodow from a.created_at) AS weekday FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_routine_invocation).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_construct_structured' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null),
				new ParseDiagnostic(ParseDiagnostic.Severity.FATAL,
						"STATEMENT_MIXED_DIALECT_GRAMMAR",
						"Statement mixes Snowflake-only and PostgreSQL-only grammar at (l:1 c:95); unlikely to run on either engine.",
						1, 95, "SqlASTWalkerHelper", null, null, false, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={parameters={1={column={name=item_one, table_ref=a}}, 2={column={name=item_two, table_ref=a}}, 3={column={name=item_three, table_ref=a}}}, function_name=array_construct_structured}, alias=result_value}, 3={alias=weekday, datetime_function={function_name=extract, part_form=KEYWORD, part=isodow, source={column={name=created_at, table_ref=a}}}}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[weekday, result_value, id]",
				"{}",
				"{events={item_two=[[@11,52:52='a',<459>,1:52]], active=[[@36,160:160='a',<459>,1:160]], created_at=[[@26,115:115='a',<459>,1:115]], id=[[@1,7:7='a',<459>,1:7]], item_three=[[@15,64:64='a',<459>,1:64]], item_one=[[@7,40:40='a',<459>,1:40]]}}",
				"{query0={weekday=[[@31,132:138='weekday',<459>,1:132]], result_value=[[@20,81:92='result_value',<459>,1:81]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={weekday=[[@31,132:138='weekday',<459>,1:132]], result_value=[[@20,81:92='result_value',<459>,1:81]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={item_two=[[@11,52:52='a',<459>,1:52]], active=[[@36,160:160='a',<459>,1:160]], created_at=[[@26,115:115='a',<459>,1:115]], id=[[@1,7:7='a',<459>,1:7]], item_three=[[@15,64:64='a',<459>,1:64]], item_one=[[@7,40:40='a',<459>,1:40]]}}, filters=[{name=active, table_ref=a}], interface={weekday=[{name=created_at, table_ref=a}], result_value=[{name=item_one, table_ref=a}, {name=item_two, table_ref=a}, {name=item_three, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC025_array_construct_structuredTrailingCommaSyntaxV0Test() {
		final String query = "SELECT array_construct_structured(a.item_one, a.item_two, a.item_three,) AS result_value FROM events a";
		final SQLSelectParserParser parser = parse(query);
		parser.sql();
		final errorhandling.ParseErrorCollector collector =
				(errorhandling.ParseErrorCollector) parser.getErrorHandler();
		Assert.assertEquals("Parser error count is wrong", 3, collector.getErrorCount());
		final java.util.List<ParseDiagnostic> diagnostics = new java.util.ArrayList<>(collector.getDiagnostics());
		diagnostics.addAll(collectParserListenerDiagnostics(parser));
		Assert.assertEquals("Parser diagnostics are wrong", Arrays.asList(
				new ParseDiagnostic(ParseDiagnostic.Severity.FATAL,
						"REPORT_ERROR",
						"Line 1:71 - unexpected input: ')' in rule value_expression: SELECT array_construct_structured(a.item_one, a.item_two, a.item_three,) AS result_value FROM events a",
						1, 71, "ParseErrorCollector", "value_expression", ")", false, "parse.strategy", "NoViableAltException", java.util.Map.of("contextSnippet", "SELECT array_construct_structured(a.item_one, a.item_two, a.item_three,) AS result_value FROM events a", "parserRules", "value_expression,select_item,select_list", "syntaxClass", "GRAMMAR_GAP")),
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"RECOVER",
						"Line 1:71 - Syntax error, attempting recovery",
						1, 71, "ParseErrorCollector", null, ")", true, "parse.strategy", "NoViableAltException", null),
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"RECOVER_INLINE",
						"Line 1:33 - Invalid syntax near '('",
						1, 33, "ParseErrorCollector", null, "(", true, "parse.strategy", null, null)), diagnostics);
	}

	@Test
	public void arC025_array_construct_structuredMissingRightParenthesisSyntaxV0Test() {
		final String query = "SELECT array_construct_structured(a.item_one, a.item_two, a.item_three AS result_value FROM events a";
		final SQLSelectParserParser parser = parse(query);
		parser.sql();
		final errorhandling.ParseErrorCollector collector =
				(errorhandling.ParseErrorCollector) parser.getErrorHandler();
		Assert.assertEquals("Parser error count is wrong", 3, collector.getErrorCount());
		final java.util.List<ParseDiagnostic> diagnostics = new java.util.ArrayList<>(collector.getDiagnostics());
		diagnostics.addAll(collectParserListenerDiagnostics(parser));
		Assert.assertEquals("Parser diagnostics are wrong", Arrays.asList(
				new ParseDiagnostic(ParseDiagnostic.Severity.FATAL,
						"REPORT_ERROR",
						"Line 1:71 - unexpected input: 'AS' in rule value_expression: SELECT array_construct_structured(a.item_one, a.item_two, a.item_three AS result_value FROM events a",
						1, 71, "ParseErrorCollector", "value_expression", "AS", false, "parse.strategy", "NoViableAltException", java.util.Map.of("contextSnippet", "SELECT array_construct_structured(a.item_one, a.item_two, a.item_three AS result_value FROM events a", "parserRules", "value_expression,select_item,select_list", "syntaxClass", "GRAMMAR_GAP")),
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"RECOVER",
						"Line 1:71 - Syntax error, attempting recovery",
						1, 71, "ParseErrorCollector", null, "AS", true, "parse.strategy", "NoViableAltException", null),
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"RECOVER_INLINE",
						"Line 1:33 - Invalid syntax near '('",
						1, 33, "ParseErrorCollector", null, "(", true, "parse.strategy", null, null)), diagnostics);
	}

	@Test
	public void arC025_array_construct_structuredMissingCommaSyntaxV0Test() {
		final String query = "SELECT array_construct_structured(a.item_one a.item_two, a.item_three) AS result_value FROM events a";
		final SQLSelectParserParser parser = parse(query);
		parser.sql();
		final errorhandling.ParseErrorCollector collector =
				(errorhandling.ParseErrorCollector) parser.getErrorHandler();
		Assert.assertEquals("Parser error count is wrong", 3, collector.getErrorCount());
		final java.util.List<ParseDiagnostic> diagnostics = new java.util.ArrayList<>(collector.getDiagnostics());
		diagnostics.addAll(collectParserListenerDiagnostics(parser));
		Assert.assertEquals("Parser diagnostics are wrong", Arrays.asList(
				new ParseDiagnostic(ParseDiagnostic.Severity.FATAL,
						"REPORT_ERROR",
						"Line 1:45 - unexpected input: 'a' in rule value_expression: SELECT array_construct_structured(a.item_one a.item_two, a.item_three) AS result_value FROM events a",
						1, 45, "ParseErrorCollector", "value_expression", "a", false, "parse.strategy", "NoViableAltException", java.util.Map.of("contextSnippet", "SELECT array_construct_structured(a.item_one a.item_two, a.item_three) AS result_value FROM events a", "parserRules", "value_expression,select_item,select_list", "syntaxClass", "GRAMMAR_GAP")),
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"RECOVER",
						"Line 1:45 - Syntax error, attempting recovery",
						1, 45, "ParseErrorCollector", null, "a", true, "parse.strategy", "NoViableAltException", null),
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"RECOVER_INLINE",
						"Line 1:33 - Invalid syntax near '('",
						1, 33, "ParseErrorCollector", null, "(", true, "parse.strategy", null, null)), diagnostics);
	}

	@Test
	public void arC025_array_construct_structuredVariadicArg4UnqualifiedV0Test() {
		final String query = "SELECT a.id, array_construct_structured(a.item_one, a.item_two, a.item_three, tail_value) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_routine_invocation).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_construct_structured' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={parameters={1={column={name=item_one, table_ref=a}}, 2={column={name=item_two, table_ref=a}}, 3={column={name=item_three, table_ref=a}}, 4={column={name=tail_value, table_ref=null}}}, function_name=array_construct_structured}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{}",
				"{events={item_two=[[@11,52:52='a',<459>,1:52]], tail_value=[[@19,78:87='tail_value',<459>,1:78]], active=[[@27,126:126='a',<459>,1:126]], id=[[@1,7:7='a',<459>,1:7]], item_three=[[@15,64:64='a',<459>,1:64]], item_one=[[@7,40:40='a',<459>,1:40]]}}",
				"{query0={result_value=[[@22,93:104='result_value',<459>,1:93]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@22,93:104='result_value',<459>,1:93]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={item_two=[[@11,52:52='a',<459>,1:52]], tail_value=[[@19,78:87='tail_value',<459>,1:78]], active=[[@27,126:126='a',<459>,1:126]], id=[[@1,7:7='a',<459>,1:7]], item_three=[[@15,64:64='a',<459>,1:64]], item_one=[[@7,40:40='a',<459>,1:40]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=item_one, table_ref=a}, {name=item_two, table_ref=a}, {name=item_three, table_ref=a}, {name=tail_value, table_ref=events}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC025_array_construct_structuredVariadicArg4UnresolvedV0Test() {
		final String query = "SELECT 7 AS marker, array_construct_structured(1, 2, 3, tail_value) AS result_value";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_routine_invocation).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_construct_structured' at (l:1 c:20) is recognized as Snowflake-specific grammar.",
						1, 20, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null),
				new ParseDiagnostic(ParseDiagnostic.Severity.ERROR,
						"UNRESOLVED_UNQUALIFIED_COLUMNS",
						"Unresolved unqualified column reference(s): [tail_value [(l:1 c:56)]]",
						1, 56, "SqlASTWalkerHelper", null, "tail_value", false, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={alias=marker, literal=7}, 2={function={parameters={1={literal=1}, 2={literal=2}, 3={literal=3}, 4={column={name=tail_value, table_ref=null}}}, function_name=array_construct_structured}, alias=result_value}}}}",
				"[marker, result_value]",
				"{}",
				"{}",
				"{query0={marker=[[@3,12:17='marker',<459>,1:12]], result_value=[[@16,71:82='result_value',<459>,1:71]]}}",
				"{def_query0={query_dictionary={marker=[[@3,12:17='marker',<459>,1:12]], result_value=[[@16,71:82='result_value',<459>,1:71]]}, interface={marker=[], result_value=[{name=tail_value, table_ref=null}]}}}");
	}

	@Test
	public void arC025_array_construct_structuredVariadicArg4AmbiguousV0Test() {
		final String query = "SELECT a.id, array_construct_structured(a.item_one, a.item_two, a.item_three, tail_value) AS result_value FROM events a JOIN labels b ON a.id = b.event_id WHERE b.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_routine_invocation).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_construct_structured' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null),
				new ParseDiagnostic(ParseDiagnostic.Severity.SEVERE_WARNING,
						"AMBIGUOUS_COLUMN_REFERENCE",
						"Ambiguous column reference 'tail_value' at (l:1 c:78). Possible sources: [events, labels]",
						1, 78, "SqlASTWalkerHelper", null, "tail_value", true, "ast-walk", null, null),
				new ParseDiagnostic(ParseDiagnostic.Severity.ERROR,
						"UNRESOLVED_UNQUALIFIED_COLUMNS",
						"Unresolved unqualified column reference(s): [tail_value [(l:1 c:78)]]",
						1, 78, "SqlASTWalkerHelper", null, "tail_value", false, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={parameters={1={column={name=item_one, table_ref=a}}, 2={column={name=item_two, table_ref=a}}, 3={column={name=item_three, table_ref=a}}, 4={column={name=tail_value, table_ref=null}}}, function_name=array_construct_structured}, alias=result_value}}, from={join={1={table={alias=a, table=events}}, 2={join=JOIN, on={condition={left={column={name=id, table_ref=a}}, right={column={name=event_id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=labels}}}}, where={condition={left={column={name=active, table_ref=b}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{}",
				"{events={item_two=[[@11,52:52='a',<459>,1:52]], id=[[@1,7:7='a',<459>,1:7], [@30,137:137='a',<459>,1:137]], item_three=[[@15,64:64='a',<459>,1:64]], item_one=[[@7,40:40='a',<459>,1:40]]}, labels={event_id=[[@34,144:144='b',<459>,1:144]], active=[[@38,161:161='b',<459>,1:161]]}}",
				"{query0={result_value=[[@22,93:104='result_value',<459>,1:93]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@22,93:104='result_value',<459>,1:93]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={item_two=[[@11,52:52='a',<459>,1:52]], id=[[@1,7:7='a',<459>,1:7], [@30,137:137='a',<459>,1:137]], item_three=[[@15,64:64='a',<459>,1:64]], item_one=[[@7,40:40='a',<459>,1:40]]}, labels={event_id=[[@34,144:144='b',<459>,1:144]], active=[[@38,161:161='b',<459>,1:161]]}}, filters=[{name=id, table_ref=a}, {name=event_id, table_ref=b}, {name=active, table_ref=b}], interface={result_value=[{name=item_one, table_ref=a}, {name=item_two, table_ref=a}, {name=item_three, table_ref=a}, {name=tail_value, table_ref=null}], id=[{name=id, table_ref=a}]}, table_alias={a=events, b=labels}}}");
	}

	@Test
	public void arC025_array_construct_structuredVariadicArg4MissingAliasV0Test() {
		final String query = "SELECT a.id, array_construct_structured(a.item_one, a.item_two, a.item_three, z.tail_value) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_routine_invocation).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_construct_structured' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null),
				new ParseDiagnostic(ParseDiagnostic.Severity.FATAL,
						"QUALIFIED_COLUMN_NOT_FOUND_IN_TABLE",
						"Source Table not found for Column 'tail_value' at (l:1 c:95). No alias or table called 'z'.",
						1, 95, "SqlASTWalkerHelper", null, "tail_value", false, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={parameters={1={column={name=item_one, table_ref=a}}, 2={column={name=item_two, table_ref=a}}, 3={column={name=item_three, table_ref=a}}, 4={column={name=tail_value, table_ref=z}}}, function_name=array_construct_structured}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{}",
				"{events={item_two=[[@11,52:52='a',<459>,1:52]], active=[[@29,128:128='a',<459>,1:128]], id=[[@1,7:7='a',<459>,1:7]], item_three=[[@15,64:64='a',<459>,1:64]], item_one=[[@7,40:40='a',<459>,1:40]]}}",
				"{query0={result_value=[[@24,95:106='result_value',<459>,1:95]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@24,95:106='result_value',<459>,1:95]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={item_two=[[@11,52:52='a',<459>,1:52]], active=[[@29,128:128='a',<459>,1:128]], id=[[@1,7:7='a',<459>,1:7]], item_three=[[@15,64:64='a',<459>,1:64]], item_one=[[@7,40:40='a',<459>,1:40]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=item_one, table_ref=a}, {name=item_two, table_ref=a}, {name=item_three, table_ref=a}, {name=tail_value, table_ref=z}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC025_array_construct_structuredVariadicArg4HiddenAliasV0Test() {
		final String query = "SELECT q.id, array_construct_structured(q.item_one, q.item_two, q.item_three, a.tail_value) AS result_value FROM (SELECT a.id, a.item_one, a.item_two, a.item_three, a.tail_value FROM events a WHERE a.active = 1) q WHERE q.id > 0";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_routine_invocation).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_construct_structured' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null),
				new ParseDiagnostic(ParseDiagnostic.Severity.FATAL,
						"QUALIFIED_COLUMN_NOT_FOUND_IN_TABLE",
						"Source Table not found for Column 'tail_value' at (l:1 c:95). No alias or table called 'a'.",
						1, 95, "SqlASTWalkerHelper", null, "tail_value", false, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=q}}, 2={function={parameters={1={column={name=item_one, table_ref=q}}, 2={column={name=item_two, table_ref=q}}, 3={column={name=item_three, table_ref=q}}, 4={column={name=tail_value, table_ref=a}}}, function_name=array_construct_structured}, alias=result_value}}, from={table={alias=q, query={select={1={column={name=id, table_ref=a}}, 2={column={name=item_one, table_ref=a}}, 3={column={name=item_two, table_ref=a}}, 4={column={name=item_three, table_ref=a}}, 5={column={name=tail_value, table_ref=a}}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}}, where={condition={left={column={name=id, table_ref=q}}, right={literal=0}, operator=>}}}}",
				"[result_value, id]",
				"{}",
				"{events={item_two=[[@36,139:139='a',<459>,1:139]], tail_value=[[@44,165:165='a',<459>,1:165]], active=[[@51,198:198='a',<459>,1:198]], id=[[@28,121:121='a',<459>,1:121]], item_three=[[@40,151:151='a',<459>,1:151]], item_one=[[@32,127:127='a',<459>,1:127]]}}",
				"{query0={tail_value=[[@46,167:176='tail_value',<459>,1:167]], id=[[@30,123:124='id',<459>,1:123], [@1,7:7='q',<459>,1:7], [@59,220:220='q',<459>,1:220]], item_two=[[@38,141:148='item_two',<459>,1:141], [@11,52:52='q',<459>,1:52]], item_three=[[@42,153:162='item_three',<459>,1:153], [@15,64:64='q',<459>,1:64]], item_one=[[@34,129:136='item_one',<459>,1:129], [@7,40:40='q',<459>,1:40]]}, query1={result_value=[[@24,95:106='result_value',<459>,1:95]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query1={query_dictionary={result_value=[[@24,95:106='result_value',<459>,1:95]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={}, def_query0={query_dictionary={item_two=[[@38,141:148='item_two',<459>,1:141], [@11,52:52='q',<459>,1:52]], tail_value=[[@46,167:176='tail_value',<459>,1:167]], id=[[@30,123:124='id',<459>,1:123], [@1,7:7='q',<459>,1:7], [@59,220:220='q',<459>,1:220]], item_three=[[@42,153:162='item_three',<459>,1:153], [@15,64:64='q',<459>,1:64]], item_one=[[@34,129:136='item_one',<459>,1:129], [@7,40:40='q',<459>,1:40]]}, table_dictionary={events={item_two=[[@36,139:139='a',<459>,1:139]], tail_value=[[@44,165:165='a',<459>,1:165]], active=[[@51,198:198='a',<459>,1:198]], id=[[@28,121:121='a',<459>,1:121]], item_three=[[@40,151:151='a',<459>,1:151]], item_one=[[@32,127:127='a',<459>,1:127]]}}, filters=[{name=active, table_ref=a}], interface={item_two=[{name=item_two, table_ref=a}], tail_value=[{name=tail_value, table_ref=a}], id=[{name=id, table_ref=a}], item_three=[{name=item_three, table_ref=a}], item_one=[{name=item_one, table_ref=a}]}, table_alias={a=events}}, filters=[{name=id, table_ref=q}], interface={result_value=[{name=item_one, table_ref=q}, {name=item_two, table_ref=q}, {name=item_three, table_ref=q}, {name=tail_value, table_ref=a}], id=[{name=id, table_ref=q}]}, table_alias={q=query0}}}");
	}

	@Test
	public void arC025_array_construct_structuredVariadicArg4PredicandVariableV0Test() {
		final String query = "SELECT a.id, array_construct_structured(a.item_one, a.item_two, a.item_three, <tail_value_expr>) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_routine_invocation).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_construct_structured' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={parameters={1={column={name=item_one, table_ref=a}}, 2={column={name=item_two, table_ref=a}}, 3={column={name=item_three, table_ref=a}}, 4={substitution={name=<tail_value_expr>, type=predicand}}}, function_name=array_construct_structured}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{<tail_value_expr>=predicand}",
				"{events={item_two=[[@11,52:52='a',<459>,1:52]], active=[[@27,133:133='a',<459>,1:133]], id=[[@1,7:7='a',<459>,1:7]], item_three=[[@15,64:64='a',<459>,1:64]], item_one=[[@7,40:40='a',<459>,1:40]]}}",
				"{query0={result_value=[[@22,100:111='result_value',<459>,1:100]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@22,100:111='result_value',<459>,1:100]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={item_two=[[@11,52:52='a',<459>,1:52]], active=[[@27,133:133='a',<459>,1:133]], id=[[@1,7:7='a',<459>,1:7]], item_three=[[@15,64:64='a',<459>,1:64]], item_one=[[@7,40:40='a',<459>,1:40]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=item_one, table_ref=a}, {name=item_two, table_ref=a}, {name=item_three, table_ref=a}, {name=<tail_value_expr>, type=predicand}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC025_array_construct_structuredVariadicArg4ColumnVariableV0Test() {
		final String query = "SELECT a.id, array_construct_structured(a.item_one, a.item_two, a.item_three, a.<tail_value_col>) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_routine_invocation).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_construct_structured' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={parameters={1={column={name=item_one, table_ref=a}}, 2={column={name=item_two, table_ref=a}}, 3={column={name=item_three, table_ref=a}}, 4={column={substitution={name=<tail_value_col>, type=column}, table_ref=a}}}, function_name=array_construct_structured}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{<tail_value_col>=column}",
				"{events={item_two=[[@11,52:52='a',<459>,1:52]], active=[[@29,134:134='a',<459>,1:134]], <tail_value_col>=[[@19,78:78='a',<459>,1:78]], id=[[@1,7:7='a',<459>,1:7]], item_three=[[@15,64:64='a',<459>,1:64]], item_one=[[@7,40:40='a',<459>,1:40]]}}",
				"{query0={result_value=[[@24,101:112='result_value',<459>,1:101]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@24,101:112='result_value',<459>,1:101]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={item_two=[[@11,52:52='a',<459>,1:52]], active=[[@29,134:134='a',<459>,1:134]], <tail_value_col>=[[@19,78:78='a',<459>,1:78]], id=[[@1,7:7='a',<459>,1:7]], item_three=[[@15,64:64='a',<459>,1:64]], item_one=[[@7,40:40='a',<459>,1:40]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=item_one, table_ref=a}, {name=item_two, table_ref=a}, {name=item_three, table_ref=a}, {substitution={name=<tail_value_col>, type=column}, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC025_array_construct_structuredVariadicArg4NullV0Test() {
		final String query = "SELECT a.id, array_construct_structured(a.item_one, a.item_two, a.item_three, NULL) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_routine_invocation).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_construct_structured' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={parameters={1={column={name=item_one, table_ref=a}}, 2={column={name=item_two, table_ref=a}}, 3={column={name=item_three, table_ref=a}}, 4={null_literal=null}}, function_name=array_construct_structured}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{}",
				"{events={item_two=[[@11,52:52='a',<459>,1:52]], active=[[@27,120:120='a',<459>,1:120]], id=[[@1,7:7='a',<459>,1:7]], item_three=[[@15,64:64='a',<459>,1:64]], item_one=[[@7,40:40='a',<459>,1:40]]}}",
				"{query0={result_value=[[@22,87:98='result_value',<459>,1:87]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@22,87:98='result_value',<459>,1:87]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={item_two=[[@11,52:52='a',<459>,1:52]], active=[[@27,120:120='a',<459>,1:120]], id=[[@1,7:7='a',<459>,1:7]], item_three=[[@15,64:64='a',<459>,1:64]], item_one=[[@7,40:40='a',<459>,1:40]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=item_one, table_ref=a}, {name=item_two, table_ref=a}, {name=item_three, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC025_array_construct_structuredVariadicAllPredicandVariablesV0Test() {
		final String query = "SELECT a.id, array_construct_structured(<item_one_predicand>, <item_two_predicand>, <item_three_predicand>, <tail_value_predicand>) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_routine_invocation).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_construct_structured' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={parameters={1={substitution={name=<item_one_predicand>, type=predicand}}, 2={substitution={name=<item_two_predicand>, type=predicand}}, 3={substitution={name=<item_three_predicand>, type=predicand}}, 4={substitution={name=<tail_value_predicand>, type=predicand}}}, function_name=array_construct_structured}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{<tail_value_predicand>=predicand, <item_two_predicand>=predicand, <item_one_predicand>=predicand, <item_three_predicand>=predicand}",
				"{events={active=[[@21,168:168='a',<459>,1:168]], id=[[@1,7:7='a',<459>,1:7]]}}",
				"{query0={result_value=[[@16,135:146='result_value',<459>,1:135]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@16,135:146='result_value',<459>,1:135]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={active=[[@21,168:168='a',<459>,1:168]], id=[[@1,7:7='a',<459>,1:7]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=<item_one_predicand>, type=predicand}, {name=<item_two_predicand>, type=predicand}, {name=<item_three_predicand>, type=predicand}, {name=<tail_value_predicand>, type=predicand}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC025_array_construct_structuredVariadicAllColumnVariablesV0Test() {
		final String query = "SELECT a.id, array_construct_structured(a.<item_one_column>, a.<item_two_column>, a.<item_three_column>, a.<tail_value_column>) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_routine_invocation).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_construct_structured' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={parameters={1={column={substitution={name=<item_one_column>, type=column}, table_ref=a}}, 2={column={substitution={name=<item_two_column>, type=column}, table_ref=a}}, 3={column={substitution={name=<item_three_column>, type=column}, table_ref=a}}, 4={column={substitution={name=<tail_value_column>, type=column}, table_ref=a}}}, function_name=array_construct_structured}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{<tail_value_column>=column, <item_one_column>=column, <item_two_column>=column, <item_three_column>=column}",
				"{events={<tail_value_column>=[[@19,105:105='a',<459>,1:105]], <item_one_column>=[[@7,40:40='a',<459>,1:40]], <item_two_column>=[[@11,61:61='a',<459>,1:61]], active=[[@29,164:164='a',<459>,1:164]], id=[[@1,7:7='a',<459>,1:7]], <item_three_column>=[[@15,82:82='a',<459>,1:82]]}}",
				"{query0={result_value=[[@24,131:142='result_value',<459>,1:131]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@24,131:142='result_value',<459>,1:131]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={<tail_value_column>=[[@19,105:105='a',<459>,1:105]], <item_one_column>=[[@7,40:40='a',<459>,1:40]], <item_two_column>=[[@11,61:61='a',<459>,1:61]], active=[[@29,164:164='a',<459>,1:164]], id=[[@1,7:7='a',<459>,1:7]], <item_three_column>=[[@15,82:82='a',<459>,1:82]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{substitution={name=<item_one_column>, type=column}, table_ref=a}, {substitution={name=<item_two_column>, type=column}, table_ref=a}, {substitution={name=<item_three_column>, type=column}, table_ref=a}, {substitution={name=<tail_value_column>, type=column}, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC025_array_construct_structuredSingleArgumentV0Test() {
		final String query = "SELECT a.id, array_construct_structured(a.item_one) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_routine_invocation).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_construct_structured' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={parameters={1={column={name=item_one, table_ref=a}}}, function_name=array_construct_structured}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{}",
				"{events={active=[[@17,88:88='a',<459>,1:88]], id=[[@1,7:7='a',<459>,1:7]], item_one=[[@7,40:40='a',<459>,1:40]]}}",
				"{query0={result_value=[[@12,55:66='result_value',<459>,1:55]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@12,55:66='result_value',<459>,1:55]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={active=[[@17,88:88='a',<459>,1:88]], id=[[@1,7:7='a',<459>,1:7]], item_one=[[@7,40:40='a',<459>,1:40]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=item_one, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC025_array_construct_structuredVariadicQualifiedTailV0Test() {
		final String query = "SELECT a.id, array_construct_structured(a.item_one, a.item_two, a.item_three, a.tail_value) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_routine_invocation).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_construct_structured' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={parameters={1={column={name=item_one, table_ref=a}}, 2={column={name=item_two, table_ref=a}}, 3={column={name=item_three, table_ref=a}}, 4={column={name=tail_value, table_ref=a}}}, function_name=array_construct_structured}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{}",
				"{events={item_two=[[@11,52:52='a',<459>,1:52]], tail_value=[[@19,78:78='a',<459>,1:78]], active=[[@29,128:128='a',<459>,1:128]], id=[[@1,7:7='a',<459>,1:7]], item_three=[[@15,64:64='a',<459>,1:64]], item_one=[[@7,40:40='a',<459>,1:40]]}}",
				"{query0={result_value=[[@24,95:106='result_value',<459>,1:95]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@24,95:106='result_value',<459>,1:95]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={item_two=[[@11,52:52='a',<459>,1:52]], tail_value=[[@19,78:78='a',<459>,1:78]], active=[[@29,128:128='a',<459>,1:128]], id=[[@1,7:7='a',<459>,1:7]], item_three=[[@15,64:64='a',<459>,1:64]], item_one=[[@7,40:40='a',<459>,1:40]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=item_one, table_ref=a}, {name=item_two, table_ref=a}, {name=item_three, table_ref=a}, {name=tail_value, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC025_array_construct_structuredEmptyCompatibilityV0Test() {
		final String query = "SELECT a.id, array_construct_structured() AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_routine_invocation).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={1={function_name=array_construct_structured}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{}",
				"{events={active=[[@14,78:78='a',<459>,1:78]], id=[[@1,7:7='a',<459>,1:7]]}}",
				"{query0={result_value=[[@9,45:56='result_value',<459>,1:45]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@9,45:56='result_value',<459>,1:45]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={active=[[@14,78:78='a',<459>,1:78]], id=[[@1,7:7='a',<459>,1:7]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC025_array_construct_structuredAllUntypedNullRuntimeBoundaryV0Test() {
		final String query = "SELECT a.id, array_construct_structured(NULL, NULL, NULL) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_routine_invocation).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_construct_structured' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={parameters={1={null_literal=null}, 2={null_literal=null}, 3={null_literal=null}}, function_name=array_construct_structured}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{}",
				"{events={active=[[@19,94:94='a',<459>,1:94]], id=[[@1,7:7='a',<459>,1:7]]}}",
				"{query0={result_value=[[@14,61:72='result_value',<459>,1:61]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@14,61:72='result_value',<459>,1:61]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={active=[[@19,94:94='a',<459>,1:94]], id=[[@1,7:7='a',<459>,1:7]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC025_array_construct_structuredTypedNullV0Test() {
		final String query = "SELECT a.id, array_construct_structured(CAST(NULL AS INTEGER), a.item_two, NULL) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_routine_invocation).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_construct_structured' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={parameters={1={function={function_name=CAST, data_type={type=INTEGER}, type=CAST, value={null_literal=null}}}, 2={column={name=item_two, table_ref=a}}, 3={null_literal=null}}, function_name=array_construct_structured}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{}",
				"{events={item_two=[[@14,63:63='a',<459>,1:63]], active=[[@26,117:117='a',<459>,1:117]], id=[[@1,7:7='a',<459>,1:7]]}}",
				"{query0={result_value=[[@21,84:95='result_value',<459>,1:84]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@21,84:95='result_value',<459>,1:84]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={item_two=[[@14,63:63='a',<459>,1:63]], active=[[@26,117:117='a',<459>,1:117]], id=[[@1,7:7='a',<459>,1:7]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=item_two, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC025_array_construct_structuredCommonTypeRuntimeBoundaryV0Test() {
		final String query = "SELECT a.id, array_construct_structured(1, '1', NULL) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_routine_invocation).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_construct_structured' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={parameters={1={literal=1}, 2={literal='1'}, 3={null_literal=null}}, function_name=array_construct_structured}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{}",
				"{events={active=[[@19,90:90='a',<459>,1:90]], id=[[@1,7:7='a',<459>,1:7]]}}",
				"{query0={result_value=[[@14,57:68='result_value',<459>,1:57]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@14,57:68='result_value',<459>,1:57]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={active=[[@19,90:90='a',<459>,1:90]], id=[[@1,7:7='a',<459>,1:7]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC025_array_construct_structuredNestedConstructorsV0Test() {
		final String query = "SELECT a.id, array_construct_structured(a.item_one, array_construct_structured(a.item_two, a.item_three), a.item_four) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 2,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_routine_invocation).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_construct_structured' at (l:1 c:52) is recognized as Snowflake-specific grammar.",
						1, 52, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={parameters={1={column={name=item_one, table_ref=a}}, 2={function={parameters={1={column={name=item_two, table_ref=a}}, 2={column={name=item_three, table_ref=a}}}, function_name=array_construct_structured}}, 3={column={name=item_four, table_ref=a}}}, function_name=array_construct_structured}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{}",
				"{events={item_two=[[@13,79:79='a',<459>,1:79]], item_four=[[@22,106:106='a',<459>,1:106]], active=[[@32,155:155='a',<459>,1:155]], id=[[@1,7:7='a',<459>,1:7]], item_three=[[@17,91:91='a',<459>,1:91]], item_one=[[@7,40:40='a',<459>,1:40]]}}",
				"{query0={result_value=[[@27,122:133='result_value',<459>,1:122]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@27,122:133='result_value',<459>,1:122]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={item_two=[[@13,79:79='a',<459>,1:79]], item_four=[[@22,106:106='a',<459>,1:106]], active=[[@32,155:155='a',<459>,1:155]], id=[[@1,7:7='a',<459>,1:7]], item_three=[[@17,91:91='a',<459>,1:91]], item_one=[[@7,40:40='a',<459>,1:40]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=item_one, table_ref=a}, {name=item_two, table_ref=a}, {name=item_three, table_ref=a}, {name=item_four, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC025_array_construct_structuredNestedArraysV0Test() {
		final String query = "SELECT a.id, array_construct_structured([a.item_one, NULL], [a.item_two, a.item_three]) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_routine_invocation).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"array literal at (l:1 c:40) is recognized as Snowflake-specific grammar.",
						1, 40, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={parameters={1={function={function_name=array_construct, parameters={1={column={name=item_one, table_ref=a}}, 2={null_literal=null}}}}, 2={function={function_name=array_construct, parameters={1={column={name=item_two, table_ref=a}}, 2={column={name=item_three, table_ref=a}}}}}}, function_name=array_construct_structured}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{}",
				"{events={item_two=[[@16,61:61='a',<459>,1:61]], active=[[@31,124:124='a',<459>,1:124]], id=[[@1,7:7='a',<459>,1:7]], item_three=[[@20,73:73='a',<459>,1:73]], item_one=[[@8,41:41='a',<459>,1:41]]}}",
				"{query0={result_value=[[@26,91:102='result_value',<459>,1:91]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@26,91:102='result_value',<459>,1:91]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={item_two=[[@16,61:61='a',<459>,1:61]], active=[[@31,124:124='a',<459>,1:124]], id=[[@1,7:7='a',<459>,1:7]], item_three=[[@20,73:73='a',<459>,1:73]], item_one=[[@8,41:41='a',<459>,1:41]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=item_one, table_ref=a}, {name=item_two, table_ref=a}, {name=item_three, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC025_array_construct_structuredRepeatedOperandV0Test() {
		final String query = "SELECT a.id, array_construct_structured(a.item_one, a.item_one, a.item_three) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_routine_invocation).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_construct_structured' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={parameters={1={column={name=item_one, table_ref=a}}, 2={column={name=item_one, table_ref=a}}, 3={column={name=item_three, table_ref=a}}}, function_name=array_construct_structured}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{}",
				"{events={active=[[@25,114:114='a',<459>,1:114]], id=[[@1,7:7='a',<459>,1:7]], item_three=[[@15,64:64='a',<459>,1:64]], item_one=[[@7,40:40='a',<459>,1:40], [@11,52:52='a',<459>,1:52]]}}",
				"{query0={result_value=[[@20,81:92='result_value',<459>,1:81]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@20,81:92='result_value',<459>,1:81]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={active=[[@25,114:114='a',<459>,1:114]], id=[[@1,7:7='a',<459>,1:7]], item_three=[[@15,64:64='a',<459>,1:64]], item_one=[[@7,40:40='a',<459>,1:40], [@11,52:52='a',<459>,1:52]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=item_one, table_ref=a}, {name=item_three, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC025_array_construct_structuredCteRenameChainV0Test() {
		final String query = "WITH seed AS (SELECT a.id, array_construct_structured(a.item_one, a.item_two, a.item_three) AS result_value FROM events a WHERE a.active = 1), renamed AS (SELECT id, result_value AS exported_value FROM seed) SELECT r.id, r.exported_value FROM renamed r WHERE r.exported_value IS NOT NULL";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_routine_invocation).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_construct_structured' at (l:1 c:27) is recognized as Snowflake-specific grammar.",
						1, 27, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={with={1={cte={select={1={column={name=id, table_ref=a}}, 2={function={parameters={1={column={name=item_one, table_ref=a}}, 2={column={name=item_two, table_ref=a}}, 3={column={name=item_three, table_ref=a}}}, function_name=array_construct_structured}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}, alias=seed}, 2={cte={select={1={column={name=id, table_ref=null}}, 2={column={name=result_value, table_ref=null}, alias=exported_value}}, from={table={alias=null, table=seed}}}, alias=renamed}}, query={select={1={column={name=id, table_ref=r}}, 2={column={name=exported_value, table_ref=r}}}, from={table={alias=r, table=renamed}}, where={condition={left={column={name=exported_value, table_ref=r}}, operator=IS NOT NULL}}}}}",
				"[exported_value, id]",
				"{}",
				"{events={item_two=[[@15,66:66='a',<459>,1:66]], active=[[@29,128:128='a',<459>,1:128]], id=[[@5,21:21='a',<459>,1:21]], item_three=[[@19,78:78='a',<459>,1:78]], item_one=[[@11,54:54='a',<459>,1:54]]}}",
				"{query0={result_value=[[@24,95:106='result_value',<459>,1:95], [@42,166:177='result_value',<459>,1:166]], id=[[@7,23:24='id',<459>,1:23], [@40,162:163='id',<459>,1:162]]}, query1={exported_value=[[@44,182:195='exported_value',<459>,1:182], [@53,221:221='r',<459>,1:221], [@60,259:259='r',<459>,1:259]], id=[[@40,162:163='id',<459>,1:162], [@49,215:215='r',<459>,1:215]]}, query2={exported_value=[[@55,223:236='exported_value',<459>,1:223]], id=[[@51,217:218='id',<459>,1:217]]}}",
				"{def_query2={context_list={seed=query0, renamed=query1, r=query1}, query_dictionary={exported_value=[[@55,223:236='exported_value',<459>,1:223]], id=[[@51,217:218='id',<459>,1:217]]}, def_query1={context_list={seed=query0}, query_dictionary={exported_value=[[@44,182:195='exported_value',<459>,1:182], [@53,221:221='r',<459>,1:221], [@60,259:259='r',<459>,1:259]], id=[[@40,162:163='id',<459>,1:162], [@49,215:215='r',<459>,1:215]]}, interface={exported_value=[{name=result_value, table_ref=query0}], id=[{name=id, table_ref=query0}]}, table_alias={seed=query0}}, def_query0={query_dictionary={result_value=[[@24,95:106='result_value',<459>,1:95], [@42,166:177='result_value',<459>,1:166]], id=[[@7,23:24='id',<459>,1:23], [@40,162:163='id',<459>,1:162]]}, table_dictionary={events={item_two=[[@15,66:66='a',<459>,1:66]], active=[[@29,128:128='a',<459>,1:128]], id=[[@5,21:21='a',<459>,1:21]], item_three=[[@19,78:78='a',<459>,1:78]], item_one=[[@11,54:54='a',<459>,1:54]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=item_one, table_ref=a}, {name=item_two, table_ref=a}, {name=item_three, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}, filters=[{name=exported_value, table_ref=r}], interface={exported_value=[{name=exported_value, table_ref=r}], id=[{name=id, table_ref=r}]}, table_alias={r=query1, seed=query0, renamed=query1}}}");
	}

	@Test
	public void arC025_array_construct_structuredCteMissingExportV0Test() {
		final String query = "WITH seed AS (SELECT a.id FROM events a WHERE a.active = 1) SELECT s.id, array_construct_structured(s.item_one, s.item_two, s.item_three) AS result_value FROM seed s";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_routine_invocation).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_construct_structured' at (l:1 c:73) is recognized as Snowflake-specific grammar.",
						1, 73, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null),
				new ParseDiagnostic(ParseDiagnostic.Severity.FATAL,
						"QUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIAS",
						"Qualified column 'item_two' at (l:1 c:112) was not found in output interface of query alias 's'.",
						1, 112, "SqlASTWalkerHelper", null, "s.item_two", false, "ast-walk", null, null),
				new ParseDiagnostic(ParseDiagnostic.Severity.FATAL,
						"QUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIAS",
						"Qualified column 'item_three' at (l:1 c:124) was not found in output interface of query alias 's'.",
						1, 124, "SqlASTWalkerHelper", null, "s.item_three", false, "ast-walk", null, null),
				new ParseDiagnostic(ParseDiagnostic.Severity.FATAL,
						"QUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIAS",
						"Qualified column 'item_one' at (l:1 c:100) was not found in output interface of query alias 's'.",
						1, 100, "SqlASTWalkerHelper", null, "s.item_one", false, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={with={1={cte={select={1={column={name=id, table_ref=a}}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}, alias=seed}}, query={select={1={column={name=id, table_ref=s}}, 2={function={parameters={1={column={name=item_one, table_ref=s}}, 2={column={name=item_two, table_ref=s}}, 3={column={name=item_three, table_ref=s}}}, function_name=array_construct_structured}, alias=result_value}}, from={table={alias=s, table=seed}}}}}",
				"[result_value, id]",
				"{}",
				"{events={active=[[@12,46:46='a',<459>,1:46]], id=[[@5,21:21='a',<459>,1:21]]}}",
				"{query0={id=[[@7,23:24='id',<459>,1:23], [@19,67:67='s',<459>,1:67]]}, query1={result_value=[[@38,141:152='result_value',<459>,1:141]], id=[[@21,69:70='id',<459>,1:69]]}}",
				"{def_query1={context_list={seed=query0, s=query0}, query_dictionary={result_value=[[@38,141:152='result_value',<459>,1:141]], id=[[@21,69:70='id',<459>,1:69]]}, def_query0={query_dictionary={id=[[@7,23:24='id',<459>,1:23], [@19,67:67='s',<459>,1:67]]}, table_dictionary={events={active=[[@12,46:46='a',<459>,1:46]], id=[[@5,21:21='a',<459>,1:21]]}}, filters=[{name=active, table_ref=a}], interface={id=[{name=id, table_ref=a}]}, table_alias={a=events}}, interface={result_value=[{name=item_one, table_ref=s}, {name=item_two, table_ref=s}, {name=item_three, table_ref=s}], id=[{name=id, table_ref=s}]}, table_alias={s=query0, seed=query0}}}");
	}

	@Test
	public void arC025_array_construct_structuredCorrelatedSubqueryV0Test() {
		final String query = "SELECT a.id FROM events a WHERE EXISTS (SELECT b.id FROM orders b WHERE b.event_id = a.id AND array_construct_structured(a.item_one, a.item_two, a.item_three) IS NOT NULL)";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_routine_invocation).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_construct_structured' at (l:1 c:94) is recognized as Snowflake-specific grammar.",
						1, 94, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}}, from={table={alias=a, table=events}}, where={exists={select={1={column={name=id, table_ref=b}}}, from={table={alias=b, table=orders}}, where={and={1={condition={left={column={name=event_id, table_ref=b}}, right={column={name=id, table_ref=a}}, operator==}}, 2={condition={left={function={parameters={1={column={name=item_one, table_ref=a}}, 2={column={name=item_two, table_ref=a}}, 3={column={name=item_three, table_ref=a}}}, function_name=array_construct_structured}}, operator=IS NOT NULL}}}}, operator=EXISTS}}}}",
				"[id]",
				"{}",
				"{orders={event_id=[[@18,72:72='b',<459>,1:72]], id=[[@11,47:47='b',<459>,1:47]]}, events={item_two=[[@32,133:133='a',<459>,1:133]], id=[[@1,7:7='a',<459>,1:7], [@22,85:85='a',<459>,1:85]], item_three=[[@36,145:145='a',<459>,1:145]], item_one=[[@28,121:121='a',<459>,1:121]]}}",
				"{query0={id=[[@13,49:50='id',<459>,1:49]]}, query2={id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query2={query_dictionary={id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={item_two=[[@32,133:133='a',<459>,1:133]], id=[[@1,7:7='a',<459>,1:7], [@22,85:85='a',<459>,1:85]], item_three=[[@36,145:145='a',<459>,1:145]], item_one=[[@28,121:121='a',<459>,1:121]]}}, dependent_queries={exists1={query=query0, type=filters}}, def_query0={query_dictionary={id=[[@13,49:50='id',<459>,1:49]]}, table_dictionary={orders={event_id=[[@18,72:72='b',<459>,1:72]], id=[[@11,47:47='b',<459>,1:47]]}}, filters=[{name=event_id, table_ref=b}, {name=id, table_ref=a}, {name=item_one, table_ref=a}, {name=item_two, table_ref=a}, {name=item_three, table_ref=a}], interface={id=[{name=id, table_ref=b}]}, table_alias={b=orders}}, filters=[], interface={id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC025_array_construct_structuredPREDICANDSnippetV0Test() {
		final String query = "array_construct_structured(<item_one_expr>, <item_two_expr>, <item_three_expr>)";
		final SQLSelectParserParser parser = parse(query);
		final org.antlr.v4.runtime.ParserRuleContext tree = parser.predicand_value();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_routine_invocation).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_construct_structured' at (l:1 c:0) is recognized as Snowflake-specific grammar.",
						1, 0, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{PREDICAND={function={parameters={1={substitution={name=<item_one_expr>, type=predicand}}, 2={substitution={name=<item_two_expr>, type=predicand}}, 3={substitution={name=<item_three_expr>, type=predicand}}}, function_name=array_construct_structured}}}",
				"[]",
				"{<item_one_expr>=predicand, <item_two_expr>=predicand, <item_three_expr>=predicand}",
				"{}",
				"{}",
				"{}");
		final access.SqlParserAccess accessParser = new access.SqlParserAccess(false, false, false);
		accessParser.executeTheParse(query, "PREDICAND");
		Assert.assertEquals("Access syntax errors", 0, accessParser.getParser().getNumberOfSyntaxErrors());
		Assert.assertEquals("Access diagnostics are wrong", Arrays.asList(new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_construct_structured' at (l:1 c:0) is recognized as Snowflake-specific grammar.",
						1, 0, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null)), accessParser.getAllDiagnostics());
		Assert.assertEquals("Access AST is wrong", "{PREDICAND={function={parameters={1={substitution={name=<item_one_expr>, type=predicand}}, 2={substitution={name=<item_two_expr>, type=predicand}}, 3={substitution={name=<item_three_expr>, type=predicand}}}, function_name=array_construct_structured}}}", accessParser.getSnippet().getSqlAbstractTree().toString());
		Assert.assertEquals("Access Interface is wrong", "[]", accessParser.getSnippet().getQueryInterface().toString());
		Assert.assertEquals("Access Substitution List is wrong", "{<item_one_expr>=predicand, <item_two_expr>=predicand, <item_three_expr>=predicand}", accessParser.getSnippet().getSubstitutionsMap().toString());
		Assert.assertEquals("Access Table Dictionary is wrong", "{}", accessParser.getSnippet().getTableDictionary().toString());
		Assert.assertEquals("Access Query Column Dictionary is wrong", "{}", accessParser.getSnippet().getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Access Symbol Table is wrong", "{}", accessParser.getSnippet().getSymbolTable().toString());
	}

	@Test
	public void arC025_array_construct_structuredCONDITIONSnippetV0Test() {
		final String query = "array_construct_structured(<item_one_expr>, <item_two_expr>, <item_three_expr>) IS NOT NULL AND 1 = 1";
		final SQLSelectParserParser parser = parse(query);
		final org.antlr.v4.runtime.ParserRuleContext tree = parser.condition_value();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_routine_invocation).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_construct_structured' at (l:1 c:0) is recognized as Snowflake-specific grammar.",
						1, 0, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{CONDITION={and={1={condition={left={function={parameters={1={substitution={name=<item_one_expr>, type=predicand}}, 2={substitution={name=<item_two_expr>, type=predicand}}, 3={substitution={name=<item_three_expr>, type=predicand}}}, function_name=array_construct_structured}}, operator=IS NOT NULL}}, 2={condition={left={literal=1}, right={literal=1}, operator==}}}}}",
				"[]",
				"{<item_one_expr>=predicand, <item_two_expr>=predicand, <item_three_expr>=predicand}",
				"{}",
				"{}",
				"{}");
		final access.SqlParserAccess accessParser = new access.SqlParserAccess(false, false, false);
		accessParser.executeTheParse(query, "CONDITION");
		Assert.assertEquals("Access syntax errors", 0, accessParser.getParser().getNumberOfSyntaxErrors());
		Assert.assertEquals("Access diagnostics are wrong", Arrays.asList(new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_construct_structured' at (l:1 c:0) is recognized as Snowflake-specific grammar.",
						1, 0, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null)), accessParser.getAllDiagnostics());
		Assert.assertEquals("Access AST is wrong", "{CONDITION={and={1={condition={left={function={parameters={1={substitution={name=<item_one_expr>, type=predicand}}, 2={substitution={name=<item_two_expr>, type=predicand}}, 3={substitution={name=<item_three_expr>, type=predicand}}}, function_name=array_construct_structured}}, operator=IS NOT NULL}}, 2={condition={left={literal=1}, right={literal=1}, operator==}}}}}", accessParser.getSnippet().getSqlAbstractTree().toString());
		Assert.assertEquals("Access Interface is wrong", "[]", accessParser.getSnippet().getQueryInterface().toString());
		Assert.assertEquals("Access Substitution List is wrong", "{<item_one_expr>=predicand, <item_two_expr>=predicand, <item_three_expr>=predicand}", accessParser.getSnippet().getSubstitutionsMap().toString());
		Assert.assertEquals("Access Table Dictionary is wrong", "{}", accessParser.getSnippet().getTableDictionary().toString());
		Assert.assertEquals("Access Query Column Dictionary is wrong", "{}", accessParser.getSnippet().getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Access Symbol Table is wrong", "{}", accessParser.getSnippet().getSymbolTable().toString());
	}

	@Test
	public void arC025_array_construct_structuredQUERYSnippetV0Test() {
		final String query = "SELECT a.id, array_construct_structured(a.item_one, a.item_two, a.item_three) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final org.antlr.v4.runtime.ParserRuleContext tree = parser.query_value();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_routine_invocation).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_construct_structured' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{QUERY={select={1={column={name=id, table_ref=a}}, 2={function={parameters={1={column={name=item_one, table_ref=a}}, 2={column={name=item_two, table_ref=a}}, 3={column={name=item_three, table_ref=a}}}, function_name=array_construct_structured}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{}",
				"{events={item_two=[[@11,52:52='a',<459>,1:52]], active=[[@25,114:114='a',<459>,1:114]], id=[[@1,7:7='a',<459>,1:7]], item_three=[[@15,64:64='a',<459>,1:64]], item_one=[[@7,40:40='a',<459>,1:40]]}}",
				"{query0={result_value=[[@20,81:92='result_value',<459>,1:81]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@20,81:92='result_value',<459>,1:81]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={item_two=[[@11,52:52='a',<459>,1:52]], active=[[@25,114:114='a',<459>,1:114]], id=[[@1,7:7='a',<459>,1:7]], item_three=[[@15,64:64='a',<459>,1:64]], item_one=[[@7,40:40='a',<459>,1:40]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=item_one, table_ref=a}, {name=item_two, table_ref=a}, {name=item_three, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
		final access.SqlParserAccess accessParser = new access.SqlParserAccess(false, false, false);
		accessParser.executeTheParse(query, "QUERY");
		Assert.assertEquals("Access syntax errors", 0, accessParser.getParser().getNumberOfSyntaxErrors());
		Assert.assertEquals("Access diagnostics are wrong", Arrays.asList(new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_construct_structured' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null)), accessParser.getAllDiagnostics());
		Assert.assertEquals("Access AST is wrong", "{QUERY={select={1={column={name=id, table_ref=a}}, 2={function={parameters={1={column={name=item_one, table_ref=a}}, 2={column={name=item_two, table_ref=a}}, 3={column={name=item_three, table_ref=a}}}, function_name=array_construct_structured}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}", accessParser.getSnippet().getSqlAbstractTree().toString());
		Assert.assertEquals("Access Interface is wrong", "[result_value, id]", accessParser.getSnippet().getQueryInterface().toString());
		Assert.assertEquals("Access Substitution List is wrong", "{}", accessParser.getSnippet().getSubstitutionsMap().toString());
		Assert.assertEquals("Access Table Dictionary is wrong", "{events={item_two=[[@11,52:52='a',<459>,1:52]], active=[[@25,114:114='a',<459>,1:114]], id=[[@1,7:7='a',<459>,1:7]], item_three=[[@15,64:64='a',<459>,1:64]], item_one=[[@7,40:40='a',<459>,1:40]]}}", accessParser.getSnippet().getTableDictionary().toString());
		Assert.assertEquals("Access Query Column Dictionary is wrong", "{query0={result_value=[[@20,81:92='result_value',<459>,1:81]], id=[[@3,9:10='id',<459>,1:9]]}}", accessParser.getSnippet().getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Access Symbol Table is wrong", "{def_query0={query_dictionary={result_value=[[@20,81:92='result_value',<459>,1:81]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={item_two=[[@11,52:52='a',<459>,1:52]], active=[[@25,114:114='a',<459>,1:114]], id=[[@1,7:7='a',<459>,1:7]], item_three=[[@15,64:64='a',<459>,1:64]], item_one=[[@7,40:40='a',<459>,1:40]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=item_one, table_ref=a}, {name=item_two, table_ref=a}, {name=item_three, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}", accessParser.getSnippet().getSymbolTable().toString());
	}

	@Test
	public void arC025_array_construct_structuredTUPLESnippetV0Test() {
		final String query = "(SELECT a.id, array_construct_structured(a.item_one, a.item_two, a.item_three) AS result_value FROM events a WHERE a.active = 1)";
		final SQLSelectParserParser parser = parse(query);
		final org.antlr.v4.runtime.ParserRuleContext tree = parser.tuple_value();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_routine_invocation).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_construct_structured' at (l:1 c:14) is recognized as Snowflake-specific grammar.",
						1, 14, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{TUPLE={from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}, select={1={column={name=id, table_ref=a}}, 2={function={parameters={1={column={name=item_one, table_ref=a}}, 2={column={name=item_two, table_ref=a}}, 3={column={name=item_three, table_ref=a}}}, function_name=array_construct_structured}, alias=result_value}}}}",
				"[result_value, id]",
				"{}",
				"{events={item_two=[[@12,53:53='a',<459>,1:53]], active=[[@26,115:115='a',<459>,1:115]], id=[[@2,8:8='a',<459>,1:8]], item_three=[[@16,65:65='a',<459>,1:65]], item_one=[[@8,41:41='a',<459>,1:41]]}}",
				"{query0={result_value=[[@21,82:93='result_value',<459>,1:82]], id=[[@4,10:11='id',<459>,1:10]]}}",
				"{def_query0={query_dictionary={result_value=[[@21,82:93='result_value',<459>,1:82]], id=[[@4,10:11='id',<459>,1:10]]}, table_dictionary={events={item_two=[[@12,53:53='a',<459>,1:53]], active=[[@26,115:115='a',<459>,1:115]], id=[[@2,8:8='a',<459>,1:8]], item_three=[[@16,65:65='a',<459>,1:65]], item_one=[[@8,41:41='a',<459>,1:41]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=item_one, table_ref=a}, {name=item_two, table_ref=a}, {name=item_three, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}, table_alias={query0=query0}}");
		final access.SqlParserAccess accessParser = new access.SqlParserAccess(false, false, false);
		accessParser.executeTheParse(query, "TUPLE");
		Assert.assertEquals("Access syntax errors", 0, accessParser.getParser().getNumberOfSyntaxErrors());
		Assert.assertEquals("Access diagnostics are wrong", Arrays.asList(new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_construct_structured' at (l:1 c:14) is recognized as Snowflake-specific grammar.",
						1, 14, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null)), accessParser.getAllDiagnostics());
		Assert.assertEquals("Access AST is wrong", "{TUPLE={from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}, select={1={column={name=id, table_ref=a}}, 2={function={parameters={1={column={name=item_one, table_ref=a}}, 2={column={name=item_two, table_ref=a}}, 3={column={name=item_three, table_ref=a}}}, function_name=array_construct_structured}, alias=result_value}}}}", accessParser.getSnippet().getSqlAbstractTree().toString());
		Assert.assertEquals("Access Interface is wrong", "[result_value, id]", accessParser.getSnippet().getQueryInterface().toString());
		Assert.assertEquals("Access Substitution List is wrong", "{}", accessParser.getSnippet().getSubstitutionsMap().toString());
		Assert.assertEquals("Access Table Dictionary is wrong", "{events={item_two=[[@12,53:53='a',<459>,1:53]], active=[[@26,115:115='a',<459>,1:115]], id=[[@2,8:8='a',<459>,1:8]], item_three=[[@16,65:65='a',<459>,1:65]], item_one=[[@8,41:41='a',<459>,1:41]]}}", accessParser.getSnippet().getTableDictionary().toString());
		Assert.assertEquals("Access Query Column Dictionary is wrong", "{query0={result_value=[[@21,82:93='result_value',<459>,1:82]], id=[[@4,10:11='id',<459>,1:10]]}}", accessParser.getSnippet().getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Access Symbol Table is wrong", "{def_query0={query_dictionary={result_value=[[@21,82:93='result_value',<459>,1:82]], id=[[@4,10:11='id',<459>,1:10]]}, table_dictionary={events={item_two=[[@12,53:53='a',<459>,1:53]], active=[[@26,115:115='a',<459>,1:115]], id=[[@2,8:8='a',<459>,1:8]], item_three=[[@16,65:65='a',<459>,1:65]], item_one=[[@8,41:41='a',<459>,1:41]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=item_one, table_ref=a}, {name=item_two, table_ref=a}, {name=item_three, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}, table_alias={query0=query0}}", accessParser.getSnippet().getSymbolTable().toString());
	}

	@Test
	public void arC025_array_construct_structuredIN_LISTSnippetV0Test() {
		final String query = "(array_construct_structured(<item_one_expr>, <item_two_expr>, <item_three_expr>), NULL)";
		final SQLSelectParserParser parser = parse(query);
		final org.antlr.v4.runtime.ParserRuleContext tree = parser.in_list_predicate_value();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_routine_invocation).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_construct_structured' at (l:1 c:1) is recognized as Snowflake-specific grammar.",
						1, 1, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{IN_LIST={list={1={function={parameters={1={substitution={name=<item_one_expr>, type=predicand}}, 2={substitution={name=<item_two_expr>, type=predicand}}, 3={substitution={name=<item_three_expr>, type=predicand}}}, function_name=array_construct_structured}}, 2={null_literal=null}}}}",
				"[]",
				"{<item_one_expr>=predicand, <item_two_expr>=predicand, <item_three_expr>=predicand}",
				"{}",
				"{}",
				"{}");
		final access.SqlParserAccess accessParser = new access.SqlParserAccess(false, false, false);
		accessParser.executeTheParse(query, "IN_LIST");
		Assert.assertEquals("Access syntax errors", 0, accessParser.getParser().getNumberOfSyntaxErrors());
		Assert.assertEquals("Access diagnostics are wrong", Arrays.asList(new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_construct_structured' at (l:1 c:1) is recognized as Snowflake-specific grammar.",
						1, 1, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null)), accessParser.getAllDiagnostics());
		Assert.assertEquals("Access AST is wrong", "{IN_LIST={list={1={function={parameters={1={substitution={name=<item_one_expr>, type=predicand}}, 2={substitution={name=<item_two_expr>, type=predicand}}, 3={substitution={name=<item_three_expr>, type=predicand}}}, function_name=array_construct_structured}}, 2={null_literal=null}}}}", accessParser.getSnippet().getSqlAbstractTree().toString());
		Assert.assertEquals("Access Interface is wrong", "[]", accessParser.getSnippet().getQueryInterface().toString());
		Assert.assertEquals("Access Substitution List is wrong", "{<item_one_expr>=predicand, <item_two_expr>=predicand, <item_three_expr>=predicand}", accessParser.getSnippet().getSubstitutionsMap().toString());
		Assert.assertEquals("Access Table Dictionary is wrong", "{}", accessParser.getSnippet().getTableDictionary().toString());
		Assert.assertEquals("Access Query Column Dictionary is wrong", "{}", accessParser.getSnippet().getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Access Symbol Table is wrong", "{}", accessParser.getSnippet().getSymbolTable().toString());
	}

	@Test
	public void arC025_array_construct_structuredJOIN_EXTENSIONSnippetV0Test() {
		final String query = "JOIN events a ON array_construct_structured(a.item_one, a.item_two, a.item_three) IS NOT NULL AND a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final org.antlr.v4.runtime.ParserRuleContext tree = parser.join_extension_value();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_routine_invocation).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_construct_structured' at (l:1 c:17) is recognized as Snowflake-specific grammar.",
						1, 17, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{JOIN_EXTENSION={1={join=JOIN, on={and={1={condition={left={function={parameters={1={column={name=item_one, table_ref=a}}, 2={column={name=item_two, table_ref=a}}, 3={column={name=item_three, table_ref=a}}}, function_name=array_construct_structured}}, operator=IS NOT NULL}}, 2={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}}, 2={table={alias=a, table=events}}}}",
				"[]",
				"{}",
				"{events={item_two=[[@10,56:56='a',<459>,1:56]], active=[[@22,98:98='a',<459>,1:98]], item_three=[[@14,68:68='a',<459>,1:68]], item_one=[[@6,44:44='a',<459>,1:44]]}}",
				"{}",
				"{table_dictionary={events={item_two=[[@10,56:56='a',<459>,1:56]], active=[[@22,98:98='a',<459>,1:98]], item_three=[[@14,68:68='a',<459>,1:68]], item_one=[[@6,44:44='a',<459>,1:44]]}}, filters=[{name=item_one, table_ref=a}, {name=item_two, table_ref=a}, {name=item_three, table_ref=a}, {name=active, table_ref=a}], table_alias={a=events}}");
		final access.SqlParserAccess accessParser = new access.SqlParserAccess(false, false, false);
		accessParser.executeTheParse(query, "JOIN_EXTENSION");
		Assert.assertEquals("Access syntax errors", 0, accessParser.getParser().getNumberOfSyntaxErrors());
		Assert.assertEquals("Access diagnostics are wrong", Arrays.asList(new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_construct_structured' at (l:1 c:17) is recognized as Snowflake-specific grammar.",
						1, 17, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null)), accessParser.getAllDiagnostics());
		Assert.assertEquals("Access AST is wrong", "{JOIN_EXTENSION={1={join=JOIN, on={and={1={condition={left={function={parameters={1={column={name=item_one, table_ref=a}}, 2={column={name=item_two, table_ref=a}}, 3={column={name=item_three, table_ref=a}}}, function_name=array_construct_structured}}, operator=IS NOT NULL}}, 2={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}}, 2={table={alias=a, table=events}}}}", accessParser.getSnippet().getSqlAbstractTree().toString());
		Assert.assertEquals("Access Interface is wrong", "[]", accessParser.getSnippet().getQueryInterface().toString());
		Assert.assertEquals("Access Substitution List is wrong", "{}", accessParser.getSnippet().getSubstitutionsMap().toString());
		Assert.assertEquals("Access Table Dictionary is wrong", "{events={item_two=[[@10,56:56='a',<459>,1:56]], active=[[@22,98:98='a',<459>,1:98]], item_three=[[@14,68:68='a',<459>,1:68]], item_one=[[@6,44:44='a',<459>,1:44]]}}", accessParser.getSnippet().getTableDictionary().toString());
		Assert.assertEquals("Access Query Column Dictionary is wrong", "{}", accessParser.getSnippet().getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Access Symbol Table is wrong", "{table_dictionary={events={item_two=[[@10,56:56='a',<459>,1:56]], active=[[@22,98:98='a',<459>,1:98]], item_three=[[@14,68:68='a',<459>,1:68]], item_one=[[@6,44:44='a',<459>,1:44]]}}, filters=[{name=item_one, table_ref=a}, {name=item_two, table_ref=a}, {name=item_three, table_ref=a}, {name=active, table_ref=a}], table_alias={a=events}}", accessParser.getSnippet().getSymbolTable().toString());
	}

	@Test
	public void arC025_array_construct_structuredVALUESSnippetV0Test() {
		final String query = "VALUES (7, array_construct_structured(1, 2, 3))";
		final SQLSelectParserParser parser = parse(query);
		final org.antlr.v4.runtime.ParserRuleContext tree = parser.values_statement_end();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_routine_invocation).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_construct_structured' at (l:1 c:11) is recognized as Snowflake-specific grammar.",
						1, 11, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{VALUES={values={matrix={1={row={1={literal=7}, 2={function={parameters={1={literal=1}, 2={literal=2}, 3={literal=3}}, function_name=array_construct_structured}}}}}}}}",
				"[$1, $2]",
				"{}",
				"{}",
				"{values0={$1=[[@1,7:7='(',<287>,1:7]], $2=[[@1,7:7='(',<287>,1:7]]}}",
				"{def_values0={query_dictionary={$1=[[@1,7:7='(',<287>,1:7]], $2=[[@1,7:7='(',<287>,1:7]]}, interface={$1=[], $2=[]}}}");
		final access.SqlParserAccess accessParser = new access.SqlParserAccess(false, false, false);
		accessParser.executeTheParse(query, "VALUES");
		Assert.assertEquals("Access syntax errors", 0, accessParser.getParser().getNumberOfSyntaxErrors());
		Assert.assertEquals("Access diagnostics are wrong", Arrays.asList(new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_construct_structured' at (l:1 c:11) is recognized as Snowflake-specific grammar.",
						1, 11, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null)), accessParser.getAllDiagnostics());
		Assert.assertEquals("Access AST is wrong", "{VALUES={values={matrix={1={row={1={literal=7}, 2={function={parameters={1={literal=1}, 2={literal=2}, 3={literal=3}}, function_name=array_construct_structured}}}}}}}}", accessParser.getSnippet().getSqlAbstractTree().toString());
		Assert.assertEquals("Access Interface is wrong", "[$1, $2]", accessParser.getSnippet().getQueryInterface().toString());
		Assert.assertEquals("Access Substitution List is wrong", "{}", accessParser.getSnippet().getSubstitutionsMap().toString());
		Assert.assertEquals("Access Table Dictionary is wrong", "{}", accessParser.getSnippet().getTableDictionary().toString());
		Assert.assertEquals("Access Query Column Dictionary is wrong", "{values0={$1=[[@1,7:7='(',<287>,1:7]], $2=[[@1,7:7='(',<287>,1:7]]}}", accessParser.getSnippet().getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Access Symbol Table is wrong", "{def_values0={query_dictionary={$1=[[@1,7:7='(',<287>,1:7]], $2=[[@1,7:7='(',<287>,1:7]]}, interface={$1=[], $2=[]}}}", accessParser.getSnippet().getSymbolTable().toString());
	}

	@Test
	public void arC025_array_construct_structuredINSERTSnippetV0Test() {
		final String query = "INSERT INTO results (id, result_value) SELECT a.id, array_construct_structured(a.item_one, a.item_two, a.item_three) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final org.antlr.v4.runtime.ParserRuleContext tree = parser.insert_end_point();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_routine_invocation).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_construct_structured' at (l:1 c:52) is recognized as Snowflake-specific grammar.",
						1, 52, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{INSERT={insert={preamble=insert_into, from={from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}, select={1={column={name=id, table_ref=a}}, 2={function={parameters={1={column={name=item_one, table_ref=a}}, 2={column={name=item_two, table_ref=a}}, 3={column={name=item_three, table_ref=a}}}, function_name=array_construct_structured}, alias=result_value}}}, target_table={table={alias=null, table=results}}, columns={1={column={name=id, table_ref=null}}, 2={column={name=result_value, table_ref=null}}}}}}",
				"[result_value, id]",
				"{}",
				"{results={result_value=[[@6,25:36='result_value',<459>,1:25]], id=[[@4,21:22='id',<459>,1:21]]}, events={item_two=[[@19,91:91='a',<459>,1:91]], active=[[@33,153:153='a',<459>,1:153]], id=[[@9,46:46='a',<459>,1:46]], item_three=[[@23,103:103='a',<459>,1:103]], item_one=[[@15,79:79='a',<459>,1:79]]}}",
				"{query0={result_value=[[@28,120:131='result_value',<459>,1:120]], id=[[@11,48:49='id',<459>,1:48]]}, insert1={result_value=[[@6,25:36='result_value',<459>,1:25]], id=[[@4,21:22='id',<459>,1:21]]}}",
				"{def_insert1={query_dictionary={result_value=[[@6,25:36='result_value',<459>,1:25]], id=[[@4,21:22='id',<459>,1:21]]}, table_dictionary={results={result_value=[[@6,25:36='result_value',<459>,1:25]], id=[[@4,21:22='id',<459>,1:21]]}}, def_query0={query_dictionary={result_value=[[@28,120:131='result_value',<459>,1:120]], id=[[@11,48:49='id',<459>,1:48]]}, table_dictionary={events={item_two=[[@19,91:91='a',<459>,1:91]], active=[[@33,153:153='a',<459>,1:153]], id=[[@9,46:46='a',<459>,1:46]], item_three=[[@23,103:103='a',<459>,1:103]], item_one=[[@15,79:79='a',<459>,1:79]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=item_one, table_ref=a}, {name=item_two, table_ref=a}, {name=item_three, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}, interface={id=[{name=id, table_ref=query0}], result_value=[{name=result_value, table_ref=query0}]}}}");
		final access.SqlParserAccess accessParser = new access.SqlParserAccess(false, false, false);
		accessParser.executeTheParse(query, "INSERT");
		Assert.assertEquals("Access syntax errors", 0, accessParser.getParser().getNumberOfSyntaxErrors());
		Assert.assertEquals("Access diagnostics are wrong", Arrays.asList(new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_construct_structured' at (l:1 c:52) is recognized as Snowflake-specific grammar.",
						1, 52, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null)), accessParser.getAllDiagnostics());
		Assert.assertEquals("Access AST is wrong", "{INSERT={insert={preamble=insert_into, from={from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}, select={1={column={name=id, table_ref=a}}, 2={function={parameters={1={column={name=item_one, table_ref=a}}, 2={column={name=item_two, table_ref=a}}, 3={column={name=item_three, table_ref=a}}}, function_name=array_construct_structured}, alias=result_value}}}, target_table={table={alias=null, table=results}}, columns={1={column={name=id, table_ref=null}}, 2={column={name=result_value, table_ref=null}}}}}}", accessParser.getSnippet().getSqlAbstractTree().toString());
		Assert.assertEquals("Access Interface is wrong", "[result_value, id]", accessParser.getSnippet().getQueryInterface().toString());
		Assert.assertEquals("Access Substitution List is wrong", "{}", accessParser.getSnippet().getSubstitutionsMap().toString());
		Assert.assertEquals("Access Table Dictionary is wrong", "{results={result_value=[[@6,25:36='result_value',<459>,1:25]], id=[[@4,21:22='id',<459>,1:21]]}, events={item_two=[[@19,91:91='a',<459>,1:91]], active=[[@33,153:153='a',<459>,1:153]], id=[[@9,46:46='a',<459>,1:46]], item_three=[[@23,103:103='a',<459>,1:103]], item_one=[[@15,79:79='a',<459>,1:79]]}}", accessParser.getSnippet().getTableDictionary().toString());
		Assert.assertEquals("Access Query Column Dictionary is wrong", "{query0={result_value=[[@28,120:131='result_value',<459>,1:120]], id=[[@11,48:49='id',<459>,1:48]]}, insert1={result_value=[[@6,25:36='result_value',<459>,1:25]], id=[[@4,21:22='id',<459>,1:21]]}}", accessParser.getSnippet().getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Access Symbol Table is wrong", "{def_insert1={query_dictionary={result_value=[[@6,25:36='result_value',<459>,1:25]], id=[[@4,21:22='id',<459>,1:21]]}, table_dictionary={results={result_value=[[@6,25:36='result_value',<459>,1:25]], id=[[@4,21:22='id',<459>,1:21]]}}, def_query0={query_dictionary={result_value=[[@28,120:131='result_value',<459>,1:120]], id=[[@11,48:49='id',<459>,1:48]]}, table_dictionary={events={item_two=[[@19,91:91='a',<459>,1:91]], active=[[@33,153:153='a',<459>,1:153]], id=[[@9,46:46='a',<459>,1:46]], item_three=[[@23,103:103='a',<459>,1:103]], item_one=[[@15,79:79='a',<459>,1:79]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=item_one, table_ref=a}, {name=item_two, table_ref=a}, {name=item_three, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}, interface={id=[{name=id, table_ref=query0}], result_value=[{name=result_value, table_ref=query0}]}}}", accessParser.getSnippet().getSymbolTable().toString());
	}

	@Test
	public void arC025_array_construct_structuredUPDATESnippetV0Test() {
		final String query = "UPDATE events SET result_value = array_construct_structured(item_one, item_two, item_three) WHERE active = 1";
		final SQLSelectParserParser parser = parse(query);
		final org.antlr.v4.runtime.ParserRuleContext tree = parser.update_end_point();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_routine_invocation).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_construct_structured' at (l:1 c:33) is recognized as Snowflake-specific grammar.",
						1, 33, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{UPDATE={update={table={alias=null, table=events}, where={condition={left={column={name=active, table_ref=null}}, right={literal=1}, operator==}}, assignments={1={set={column={name=result_value, table_ref=null}}, to={function={parameters={1={column={name=item_one, table_ref=null}}, 2={column={name=item_two, table_ref=null}}, 3={column={name=item_three, table_ref=null}}}, function_name=array_construct_structured}}}}}}}",
				"[result_value]",
				"{}",
				"{events={item_two=[[@9,70:77='item_two',<459>,1:70], [@12,90:90=')',<288>,1:90]], active=[[@14,98:103='active',<459>,1:98]], item_three=[[@11,80:89='item_three',<459>,1:80], [@12,90:90=')',<288>,1:90]], item_one=[[@7,60:67='item_one',<459>,1:60], [@12,90:90=')',<288>,1:90]]}}",
				"{update0={result_value=[[@3,18:29='result_value',<459>,1:18]]}}",
				"{def_update0={assignments={result_value=[{name=item_one, table_ref=events}, {name=item_two, table_ref=events}, {name=item_three, table_ref=events}]}, table_dictionary={events={item_two=[[@9,70:77='item_two',<459>,1:70], [@12,90:90=')',<288>,1:90]], active=[[@14,98:103='active',<459>,1:98]], item_three=[[@11,80:89='item_three',<459>,1:80], [@12,90:90=')',<288>,1:90]], item_one=[[@7,60:67='item_one',<459>,1:60], [@12,90:90=')',<288>,1:90]]}}, update_dictionary={result_value=[[@3,18:29='result_value',<459>,1:18]]}, target_table={events={result_value=[[@3,18:29='result_value',<459>,1:18]]}}, filters=[{name=active, table_ref=events}], lhs_unresolved_columns={result_value={column={name=result_value, table_ref=null}, locations=[[@3,18:29='result_value',<459>,1:18]]}}}}");
		final access.SqlParserAccess accessParser = new access.SqlParserAccess(false, false, false);
		accessParser.executeTheParse(query, "UPDATE");
		Assert.assertEquals("Access syntax errors", 0, accessParser.getParser().getNumberOfSyntaxErrors());
		Assert.assertEquals("Access diagnostics are wrong", Arrays.asList(new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_construct_structured' at (l:1 c:33) is recognized as Snowflake-specific grammar.",
						1, 33, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null)), accessParser.getAllDiagnostics());
		Assert.assertEquals("Access AST is wrong", "{UPDATE={update={table={alias=null, table=events}, where={condition={left={column={name=active, table_ref=null}}, right={literal=1}, operator==}}, assignments={1={set={column={name=result_value, table_ref=null}}, to={function={parameters={1={column={name=item_one, table_ref=null}}, 2={column={name=item_two, table_ref=null}}, 3={column={name=item_three, table_ref=null}}}, function_name=array_construct_structured}}}}}}}", accessParser.getSnippet().getSqlAbstractTree().toString());
		Assert.assertEquals("Access Interface is wrong", "[result_value]", accessParser.getSnippet().getQueryInterface().toString());
		Assert.assertEquals("Access Substitution List is wrong", "{}", accessParser.getSnippet().getSubstitutionsMap().toString());
		Assert.assertEquals("Access Table Dictionary is wrong", "{events={item_two=[[@9,70:77='item_two',<459>,1:70], [@12,90:90=')',<288>,1:90]], active=[[@14,98:103='active',<459>,1:98]], item_three=[[@11,80:89='item_three',<459>,1:80], [@12,90:90=')',<288>,1:90]], item_one=[[@7,60:67='item_one',<459>,1:60], [@12,90:90=')',<288>,1:90]]}}", accessParser.getSnippet().getTableDictionary().toString());
		Assert.assertEquals("Access Query Column Dictionary is wrong", "{update0={result_value=[[@3,18:29='result_value',<459>,1:18]]}}", accessParser.getSnippet().getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Access Symbol Table is wrong", "{def_update0={assignments={result_value=[{name=item_one, table_ref=events}, {name=item_two, table_ref=events}, {name=item_three, table_ref=events}]}, table_dictionary={events={item_two=[[@9,70:77='item_two',<459>,1:70], [@12,90:90=')',<288>,1:90]], active=[[@14,98:103='active',<459>,1:98]], item_three=[[@11,80:89='item_three',<459>,1:80], [@12,90:90=')',<288>,1:90]], item_one=[[@7,60:67='item_one',<459>,1:60], [@12,90:90=')',<288>,1:90]]}}, update_dictionary={result_value=[[@3,18:29='result_value',<459>,1:18]]}, target_table={events={result_value=[[@3,18:29='result_value',<459>,1:18]]}}, filters=[{name=active, table_ref=events}], lhs_unresolved_columns={result_value={column={name=result_value, table_ref=null}, locations=[[@3,18:29='result_value',<459>,1:18]]}}}}", accessParser.getSnippet().getSymbolTable().toString());
	}

	@Test
	public void arC025_array_construct_structuredDELETESnippetV0Test() {
		final String query = "DELETE FROM events WHERE array_construct_structured(item_one, item_two, item_three) IS NULL AND active = 0";
		final SQLSelectParserParser parser = parse(query);
		final org.antlr.v4.runtime.ParserRuleContext tree = parser.delete_end_point();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_routine_invocation).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_construct_structured' at (l:1 c:25) is recognized as Snowflake-specific grammar.",
						1, 25, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{DELETE={delete={table={alias=null, table=events}, where={and={1={condition={left={function={parameters={1={column={name=item_one, table_ref=null}}, 2={column={name=item_two, table_ref=null}}, 3={column={name=item_three, table_ref=null}}}, function_name=array_construct_structured}}, operator=IS NULL}}, 2={condition={left={column={name=active, table_ref=null}}, right={literal=0}, operator==}}}}}}}",
				"[]",
				"{}",
				"{events={item_two=[[@8,62:69='item_two',<459>,1:62]], active=[[@15,96:101='active',<459>,1:96]], item_three=[[@10,72:81='item_three',<459>,1:72]], item_one=[[@6,52:59='item_one',<459>,1:52]]}}",
				"{}",
				"{def_delete0={query_dictionary={}, table_dictionary={events={item_two=[[@8,62:69='item_two',<459>,1:62]], active=[[@15,96:101='active',<459>,1:96]], item_three=[[@10,72:81='item_three',<459>,1:72]], item_one=[[@6,52:59='item_one',<459>,1:52]]}}, filters=[{name=item_one, table_ref=events}, {name=item_two, table_ref=events}, {name=item_three, table_ref=events}, {name=active, table_ref=events}], interface=null}}");
		final access.SqlParserAccess accessParser = new access.SqlParserAccess(false, false, false);
		accessParser.executeTheParse(query, "DELETE");
		Assert.assertEquals("Access syntax errors", 0, accessParser.getParser().getNumberOfSyntaxErrors());
		Assert.assertEquals("Access diagnostics are wrong", Arrays.asList(new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_construct_structured' at (l:1 c:25) is recognized as Snowflake-specific grammar.",
						1, 25, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null)), accessParser.getAllDiagnostics());
		Assert.assertEquals("Access AST is wrong", "{DELETE={delete={table={alias=null, table=events}, where={and={1={condition={left={function={parameters={1={column={name=item_one, table_ref=null}}, 2={column={name=item_two, table_ref=null}}, 3={column={name=item_three, table_ref=null}}}, function_name=array_construct_structured}}, operator=IS NULL}}, 2={condition={left={column={name=active, table_ref=null}}, right={literal=0}, operator==}}}}}}}", accessParser.getSnippet().getSqlAbstractTree().toString());
		Assert.assertEquals("Access Interface is wrong", "[]", accessParser.getSnippet().getQueryInterface().toString());
		Assert.assertEquals("Access Substitution List is wrong", "{}", accessParser.getSnippet().getSubstitutionsMap().toString());
		Assert.assertEquals("Access Table Dictionary is wrong", "{events={item_two=[[@8,62:69='item_two',<459>,1:62]], active=[[@15,96:101='active',<459>,1:96]], item_three=[[@10,72:81='item_three',<459>,1:72]], item_one=[[@6,52:59='item_one',<459>,1:52]]}}", accessParser.getSnippet().getTableDictionary().toString());
		Assert.assertEquals("Access Query Column Dictionary is wrong", "{}", accessParser.getSnippet().getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Access Symbol Table is wrong", "{def_delete0={query_dictionary={}, table_dictionary={events={item_two=[[@8,62:69='item_two',<459>,1:62]], active=[[@15,96:101='active',<459>,1:96]], item_three=[[@10,72:81='item_three',<459>,1:72]], item_one=[[@6,52:59='item_one',<459>,1:52]]}}, filters=[{name=item_one, table_ref=events}, {name=item_two, table_ref=events}, {name=item_three, table_ref=events}, {name=active, table_ref=events}], interface=null}}", accessParser.getSnippet().getSymbolTable().toString());
	}

	@Test
	public void arC025_array_construct_structuredDDLSnippetV0Test() {
		final String query = "CREATE VIEW v_array_construct_structured AS SELECT a.id, array_construct_structured(a.item_one, a.item_two, a.item_three) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final org.antlr.v4.runtime.ParserRuleContext tree = parser.ddl();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_routine_invocation).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_construct_structured' at (l:1 c:57) is recognized as Snowflake-specific grammar.",
						1, 57, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{DDL={create={type=VIEW, name={table=v_array_construct_structured}, query={select={1={column={name=id, table_ref=a}}, 2={function={parameters={1={column={name=item_one, table_ref=a}}, 2={column={name=item_two, table_ref=a}}, 3={column={name=item_three, table_ref=a}}}, function_name=array_construct_structured}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}}}",
				"[]",
				"{}",
				"{events={item_two=[[@15,96:96='a',<459>,1:96]], active=[[@29,158:158='a',<459>,1:158]], id=[[@5,51:51='a',<459>,1:51]], item_three=[[@19,108:108='a',<459>,1:108]], item_one=[[@11,84:84='a',<459>,1:84]]}}",
				"{query0={result_value=[[@24,125:136='result_value',<459>,1:125]], id=[[@7,53:54='id',<459>,1:53]]}}",
				"{def_create1={def_query0={query_dictionary={result_value=[[@24,125:136='result_value',<459>,1:125]], id=[[@7,53:54='id',<459>,1:53]]}, table_dictionary={events={item_two=[[@15,96:96='a',<459>,1:96]], active=[[@29,158:158='a',<459>,1:158]], id=[[@5,51:51='a',<459>,1:51]], item_three=[[@19,108:108='a',<459>,1:108]], item_one=[[@11,84:84='a',<459>,1:84]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=item_one, table_ref=a}, {name=item_two, table_ref=a}, {name=item_three, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}}");
		final access.SqlParserAccess accessParser = new access.SqlParserAccess(false, false, false);
		accessParser.executeTheParse(query, "DDL");
		Assert.assertEquals("Access syntax errors", 0, accessParser.getParser().getNumberOfSyntaxErrors());
		Assert.assertEquals("Access diagnostics are wrong", Arrays.asList(new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_construct_structured' at (l:1 c:57) is recognized as Snowflake-specific grammar.",
						1, 57, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null)), accessParser.getAllDiagnostics());
		Assert.assertEquals("Access AST is wrong", "{DDL={create={type=VIEW, name={table=v_array_construct_structured}, query={select={1={column={name=id, table_ref=a}}, 2={function={parameters={1={column={name=item_one, table_ref=a}}, 2={column={name=item_two, table_ref=a}}, 3={column={name=item_three, table_ref=a}}}, function_name=array_construct_structured}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}}}", accessParser.getSnippet().getSqlAbstractTree().toString());
		Assert.assertEquals("Access Interface is wrong", "[]", accessParser.getSnippet().getQueryInterface().toString());
		Assert.assertEquals("Access Substitution List is wrong", "{}", accessParser.getSnippet().getSubstitutionsMap().toString());
		Assert.assertEquals("Access Table Dictionary is wrong", "{events={item_two=[[@15,96:96='a',<459>,1:96]], active=[[@29,158:158='a',<459>,1:158]], id=[[@5,51:51='a',<459>,1:51]], item_three=[[@19,108:108='a',<459>,1:108]], item_one=[[@11,84:84='a',<459>,1:84]]}}", accessParser.getSnippet().getTableDictionary().toString());
		Assert.assertEquals("Access Query Column Dictionary is wrong", "{query0={result_value=[[@24,125:136='result_value',<459>,1:125]], id=[[@7,53:54='id',<459>,1:53]]}}", accessParser.getSnippet().getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Access Symbol Table is wrong", "{def_create1={def_query0={query_dictionary={result_value=[[@24,125:136='result_value',<459>,1:125]], id=[[@7,53:54='id',<459>,1:53]]}, table_dictionary={events={item_two=[[@15,96:96='a',<459>,1:96]], active=[[@29,158:158='a',<459>,1:158]], id=[[@5,51:51='a',<459>,1:51]], item_three=[[@19,108:108='a',<459>,1:108]], item_one=[[@11,84:84='a',<459>,1:84]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=item_one, table_ref=a}, {name=item_two, table_ref=a}, {name=item_three, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}}", accessParser.getSnippet().getSymbolTable().toString());
	}

	@Test
	public void arC025_array_construct_structuredSCRIPTSnippetV0Test() {
		final String query = "SELECT a.id, array_construct_structured(a.item_one, a.item_two, a.item_three) AS result_value FROM events a WHERE a.active = 1;\nSELECT b.id FROM orders b;\nSELECT a.id, array_construct_structured(a.item_one, a.item_two, a.item_three) AS result_value FROM events a WHERE a.active = 1;";
		final SQLSelectParserParser parser = parse(query);
		final org.antlr.v4.runtime.ParserRuleContext tree = parser.script();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 2,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_routine_invocation).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_construct_structured' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null),
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_construct_structured' at (l:3 c:13) is recognized as Snowflake-specific grammar.",
						3, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SCRIPT={1={select={1={column={name=id, table_ref=a}}, 2={function={parameters={1={column={name=item_one, table_ref=a}}, 2={column={name=item_two, table_ref=a}}, 3={column={name=item_three, table_ref=a}}}, function_name=array_construct_structured}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}, 2={select={1={column={name=id, table_ref=b}}}, from={table={alias=b, table=orders}}}, 3={select={1={column={name=id, table_ref=a}}, 2={function={parameters={1={column={name=item_one, table_ref=a}}, 2={column={name=item_two, table_ref=a}}, 3={column={name=item_three, table_ref=a}}}, function_name=array_construct_structured}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}}",
				"[]",
				"{SCRIPT={1={}, 2={}, 3={}}}",
				"{SCRIPT={1={events={item_two=[[@11,52:52='a',<459>,1:52]], active=[[@25,114:114='a',<459>,1:114]], id=[[@1,7:7='a',<459>,1:7]], item_three=[[@15,64:64='a',<459>,1:64]], item_one=[[@7,40:40='a',<459>,1:40]]}}, 2={orders={id=[[@32,135:135='b',<459>,2:7]]}}, 3={events={item_two=[[@50,207:207='a',<459>,3:52]], active=[[@64,269:269='a',<459>,3:114]], id=[[@40,162:162='a',<459>,3:7]], item_three=[[@54,219:219='a',<459>,3:64]], item_one=[[@46,195:195='a',<459>,3:40]]}}}}",
				"{SCRIPT={1={query0={result_value=[[@20,81:92='result_value',<459>,1:81]], id=[[@3,9:10='id',<459>,1:9]]}}, 2={query0={id=[[@34,137:138='id',<459>,2:9]]}}, 3={query0={result_value=[[@59,236:247='result_value',<459>,3:81]], id=[[@42,164:165='id',<459>,3:9]]}}}}",
				"{SCRIPT={1={def_query0={query_dictionary={result_value=[[@20,81:92='result_value',<459>,1:81]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={item_two=[[@11,52:52='a',<459>,1:52]], active=[[@25,114:114='a',<459>,1:114]], id=[[@1,7:7='a',<459>,1:7]], item_three=[[@15,64:64='a',<459>,1:64]], item_one=[[@7,40:40='a',<459>,1:40]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=item_one, table_ref=a}, {name=item_two, table_ref=a}, {name=item_three, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}, 2={def_query0={query_dictionary={id=[[@34,137:138='id',<459>,2:9]]}, table_dictionary={orders={id=[[@32,135:135='b',<459>,2:7]]}}, interface={id=[{name=id, table_ref=b}]}, table_alias={b=orders}}}, 3={def_query0={query_dictionary={result_value=[[@59,236:247='result_value',<459>,3:81]], id=[[@42,164:165='id',<459>,3:9]]}, table_dictionary={events={item_two=[[@50,207:207='a',<459>,3:52]], active=[[@64,269:269='a',<459>,3:114]], id=[[@40,162:162='a',<459>,3:7]], item_three=[[@54,219:219='a',<459>,3:64]], item_one=[[@46,195:195='a',<459>,3:40]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=item_one, table_ref=a}, {name=item_two, table_ref=a}, {name=item_three, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}}}");
		final access.SqlParserAccess accessParser = new access.SqlParserAccess(false, false, false);
		accessParser.executeTheParse(query, "SCRIPT");
		Assert.assertEquals("Access syntax errors", 0, accessParser.getParser().getNumberOfSyntaxErrors());
		Assert.assertEquals("Access diagnostics are wrong", Arrays.asList(new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_construct_structured' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null),
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_construct_structured' at (l:3 c:13) is recognized as Snowflake-specific grammar.",
						3, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null)), accessParser.getAllDiagnostics());
		Assert.assertEquals("Access AST is wrong", "{SCRIPT={1={select={1={column={name=id, table_ref=a}}, 2={function={parameters={1={column={name=item_one, table_ref=a}}, 2={column={name=item_two, table_ref=a}}, 3={column={name=item_three, table_ref=a}}}, function_name=array_construct_structured}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}, 2={select={1={column={name=id, table_ref=b}}}, from={table={alias=b, table=orders}}}, 3={select={1={column={name=id, table_ref=a}}, 2={function={parameters={1={column={name=item_one, table_ref=a}}, 2={column={name=item_two, table_ref=a}}, 3={column={name=item_three, table_ref=a}}}, function_name=array_construct_structured}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}}", accessParser.getSnippet().getSqlAbstractTree().toString());
		Assert.assertEquals("Access Interface is wrong", "[]", accessParser.getSnippet().getQueryInterface().toString());
		Assert.assertEquals("Access Substitution List is wrong", "{SCRIPT={1={}, 2={}, 3={}}}", accessParser.getSnippet().getSubstitutionsMap().toString());
		Assert.assertEquals("Access Table Dictionary is wrong", "{SCRIPT={1={events={item_two=[[@11,52:52='a',<459>,1:52]], active=[[@25,114:114='a',<459>,1:114]], id=[[@1,7:7='a',<459>,1:7]], item_three=[[@15,64:64='a',<459>,1:64]], item_one=[[@7,40:40='a',<459>,1:40]]}}, 2={orders={id=[[@32,135:135='b',<459>,2:7]]}}, 3={events={item_two=[[@50,207:207='a',<459>,3:52]], active=[[@64,269:269='a',<459>,3:114]], id=[[@40,162:162='a',<459>,3:7]], item_three=[[@54,219:219='a',<459>,3:64]], item_one=[[@46,195:195='a',<459>,3:40]]}}}}", accessParser.getSnippet().getTableDictionary().toString());
		Assert.assertEquals("Access Query Column Dictionary is wrong", "{SCRIPT={1={query0={result_value=[[@20,81:92='result_value',<459>,1:81]], id=[[@3,9:10='id',<459>,1:9]]}}, 2={query0={id=[[@34,137:138='id',<459>,2:9]]}}, 3={query0={result_value=[[@59,236:247='result_value',<459>,3:81]], id=[[@42,164:165='id',<459>,3:9]]}}}}", accessParser.getSnippet().getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Access Symbol Table is wrong", "{SCRIPT={1={def_query0={query_dictionary={result_value=[[@20,81:92='result_value',<459>,1:81]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={item_two=[[@11,52:52='a',<459>,1:52]], active=[[@25,114:114='a',<459>,1:114]], id=[[@1,7:7='a',<459>,1:7]], item_three=[[@15,64:64='a',<459>,1:64]], item_one=[[@7,40:40='a',<459>,1:40]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=item_one, table_ref=a}, {name=item_two, table_ref=a}, {name=item_three, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}, 2={def_query0={query_dictionary={id=[[@34,137:138='id',<459>,2:9]]}, table_dictionary={orders={id=[[@32,135:135='b',<459>,2:7]]}}, interface={id=[{name=id, table_ref=b}]}, table_alias={b=orders}}}, 3={def_query0={query_dictionary={result_value=[[@59,236:247='result_value',<459>,3:81]], id=[[@42,164:165='id',<459>,3:9]]}, table_dictionary={events={item_two=[[@50,207:207='a',<459>,3:52]], active=[[@64,269:269='a',<459>,3:114]], id=[[@40,162:162='a',<459>,3:7]], item_three=[[@54,219:219='a',<459>,3:64]], item_one=[[@46,195:195='a',<459>,3:40]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=item_one, table_ref=a}, {name=item_two, table_ref=a}, {name=item_three, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}}}", accessParser.getSnippet().getSymbolTable().toString());
	}
}
