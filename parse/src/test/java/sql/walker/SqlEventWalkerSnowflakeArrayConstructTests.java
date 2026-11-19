package sql.walker;

import org.junit.Assert;
import org.junit.Test;

import access.Snippet;
import errorhandling.ParseDiagnostic;
import sql.SQLSelectParserParser;

public class SqlEventWalkerSnowflakeArrayConstructTests extends AbstractSqlParseEventWalkerTest {

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
}
