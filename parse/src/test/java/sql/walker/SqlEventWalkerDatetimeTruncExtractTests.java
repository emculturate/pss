package sql.walker;

import org.junit.Assert;
import org.junit.Test;

import access.Snippet;
import access.SqlParserAccess;
import errorhandling.ParseDiagnostic;

public class SqlEventWalkerDatetimeTruncExtractTests extends AbstractSqlParseEventWalkerTest {

	private SqlParserAccess parseEndpoint(String sql, String endpoint) {
		SqlParserAccess parser = new SqlParserAccess(false, false, false);
		parser.executeTheParse(sql, endpoint);
		Assert.assertEquals(sql, 0, parser.getParser().getNumberOfSyntaxErrors());
		return parser;
	}

	private void assertOutputs(
			SqlParserAccess parser,
			String ast,
			String outputInterface,
			String substitutions,
			String tableDictionary,
			String queryDictionary,
			String symbolTable,
			String arrayOutputCollectors) {
		Snippet snippet = parser.getSnippet();
		Assert.assertEquals("AST is wrong", ast, snippet.getSqlAbstractTree().toString());
		Assert.assertEquals("Interface is wrong", outputInterface, snippet.getQueryInterface().toString());
		Assert.assertEquals("Substitution List is wrong", substitutions, snippet.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", tableDictionary, snippet.getTableDictionary().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", queryDictionary, snippet.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", symbolTable, snippet.getSymbolTable().toString());
		Assert.assertEquals("Array Output Collectors are wrong", arrayOutputCollectors, String.valueOf(snippet.getArrayOutputCollectorsMap()));
	}

	@Test
	public void corePlacementDateTruncInWhereCollectsFilters() {
		String query = "select a.id from events a where date_trunc(day, a.created_at) > DATE '2026-01-01'";
		SqlParserAccess parser = parseEndpoint(query, "SQL");
		Assert.assertEquals(parser.getAllDiagnostics().toString(), 1, parser.getAllDiagnostics().size());
		assertDiagnosticAtPosition(parser.getSnippet(),
				"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
				ParseDiagnostic.Severity.WARNING,
				"snowflake_date_trunc_expression at (l:1 c:32) is recognized as Snowflake-specific grammar.",
				null,
				1,
				32);
		assertOutputs(parser,
				"{SQL={select={1={column={name=id, table_ref=a}}}, from={table={alias=a, table=events}}, where={condition={left={datetime_function={function_name=date_trunc, level={canonical=DAY, text=day, form=KEYWORD}, value={column={name=created_at, table_ref=a}}}}, right={source_type=DATE, literal=2026-01-01}, operator=>}}}}",
				"[id]",
				"{}",
				"{events={created_at=[[@12,48:48='a',<419>,1:48]], id=[[@1,7:7='a',<419>,1:7]]}}",
				"{query0={id=[[@3,9:10='id',<419>,1:9]]}}",
				"{def_query0={query_dictionary={id=[[@3,9:10='id',<419>,1:9]]}, table_dictionary={events={created_at=[[@12,48:48='a',<419>,1:48]], id=[[@1,7:7='a',<419>,1:7]]}}, filters=[{name=created_at, table_ref=a}], interface={id=[{name=id, table_ref=a}]}, table_alias={a=events}}}",
				"null");
	}

	@Test
	public void corePlacementTruncInHavingCollectsGroupedAndFilterDependencies() {
		String query = "select trunc(a.created_at, day) as bucket, count(*) as total from events a group by trunc(a.created_at, day) having max(trunc(a.created_at, day)) > DATE '2026-01-01'";
		SqlParserAccess parser = parseEndpoint(query, "SQL");
		Assert.assertEquals(parser.getAllDiagnostics().toString(), 1, parser.getAllDiagnostics().size());
		assertDiagnosticAtPosition(parser.getSnippet(),
				"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
				ParseDiagnostic.Severity.WARNING,
				"snowflake_datetime_trunc_expression at (l:1 c:7) is recognized as Snowflake-specific grammar.",
				null,
				1,
				7);
		assertOutputs(parser,
				"{SQL={select={1={alias=bucket, datetime_function={function_name=trunc, level={canonical=DAY, text=day, form=KEYWORD}, value={column={name=created_at, table_ref=a}}}}, 2={function={function_name=COUNT, qualifier=null, parameters=*}, alias=total}}, having={condition={left={function={function_name=max, qualifier=null, parameters={datetime_function={function_name=trunc, level={canonical=DAY, text=day, form=KEYWORD}, value={column={name=created_at, table_ref=a}}}}}}, right={source_type=DATE, literal=2026-01-01}, operator=>}}, from={table={alias=a, table=events}}, groupby={1={datetime_function={function_name=trunc, level={canonical=DAY, text=day, form=KEYWORD}, value={column={name=created_at, table_ref=a}}}}}}}",
				"[bucket, total]",
				"{}",
				"{events={created_at=[[@3,13:13='a',<419>,1:13], [@25,90:90='a',<419>,1:90], [@36,126:126='a',<419>,1:126]], *=[[@14,49:49='*',<291>,1:49]]}}",
				"{query0={bucket=[[@10,35:40='bucket',<419>,1:35]], total=[[@17,55:59='total',<419>,1:55]]}}",
				"{def_query0={query_dictionary={bucket=[[@10,35:40='bucket',<419>,1:35]], total=[[@17,55:59='total',<419>,1:55]]}, table_dictionary={events={created_at=[[@3,13:13='a',<419>,1:13], [@25,90:90='a',<419>,1:90], [@36,126:126='a',<419>,1:126]], *=[[@14,49:49='*',<291>,1:49]]}}, grouped_by=[{name=created_at, table_ref=a}], filters=[{name=created_at, table_ref=a}], interface={bucket=[{name=created_at, table_ref=a}], total=[]}, table_alias={a=events}}}",
				"null");
	}

	@Test
	public void corePlacementDateTruncInOrderByTracksOrderedBy() {
		String query = "select date_trunc(day, a.created_at) as bucket from events a order by date_trunc(day, a.created_at), bucket";
		SqlParserAccess parser = parseEndpoint(query, "SQL");
		Assert.assertEquals(parser.getAllDiagnostics().toString(), 1, parser.getAllDiagnostics().size());
		assertDiagnosticAtPosition(parser.getSnippet(),
				"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
				ParseDiagnostic.Severity.WARNING,
				"snowflake_date_trunc_expression at (l:1 c:7) is recognized as Snowflake-specific grammar.",
				null,
				1,
				7);
		assertOutputs(parser,
				"{SQL={select={1={alias=bucket, datetime_function={function_name=date_trunc, level={canonical=DAY, text=day, form=KEYWORD}, value={column={name=created_at, table_ref=a}}}}}, orderby={1={null_order=null, predicand={datetime_function={function_name=date_trunc, level={canonical=DAY, text=day, form=KEYWORD}, value={column={name=created_at, table_ref=a}}}}, sort_order=ASC}, 2={null_order=null, predicand={column={name=bucket, table_ref=null}}, sort_order=ASC}}, from={table={alias=a, table=events}}}}",
				"[bucket]",
				"{}",
				"{events={created_at=[[@5,23:23='a',<419>,1:23], [@20,86:86='a',<419>,1:86]]}}",
				"{query0={bucket=[[@10,40:45='bucket',<419>,1:40], [@25,101:106='bucket',<419>,1:101]]}}",
				"{def_query0={query_dictionary={bucket=[[@10,40:45='bucket',<419>,1:40], [@25,101:106='bucket',<419>,1:101]]}, table_dictionary={events={created_at=[[@5,23:23='a',<419>,1:23], [@20,86:86='a',<419>,1:86]]}}, ordered_by=[{name=created_at, table_ref=a}, {name=bucket, table_ref=query0}], interface={bucket=[{name=created_at, table_ref=a}]}, table_alias={a=events}}}",
				"null");
	}

	@Test
	public void corePlacementTruncInWindowTracksWindowBuckets() {
		String query = "select a.id from events a qualify row_number() over (partition by trunc(a.created_at, day) order by trunc(a.created_at, day)) = 1";
		SqlParserAccess parser = parseEndpoint(query, "SQL");
		Assert.assertEquals(parser.getAllDiagnostics().toString(), 1, parser.getAllDiagnostics().size());
		assertDiagnosticAtPosition(parser.getSnippet(),
				"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
				ParseDiagnostic.Severity.WARNING,
				"snowflake_datetime_trunc_expression at (l:1 c:66) is recognized as Snowflake-specific grammar.",
				null,
				1,
				66);
		assertOutputs(parser,
				"{SQL={select={1={column={name=id, table_ref=a}}}, from={table={alias=a, table=events}}, qualify={condition={left={window_function={over={partition_by={1={datetime_function={function_name=trunc, level={canonical=DAY, text=day, form=KEYWORD}, value={column={name=created_at, table_ref=a}}}}}, orderby={1={null_order=null, predicand={datetime_function={function_name=trunc, level={canonical=DAY, text=day, form=KEYWORD}, value={column={name=created_at, table_ref=a}}}}, sort_order=ASC}}}, function={function_name=row_number, parameters=null}}}, right={literal=1}, operator==}}}}",
				"[id]",
				"{}",
				"{events={created_at=[[@17,72:72='a',<419>,1:72], [@27,106:106='a',<419>,1:106]], id=[[@1,7:7='a',<419>,1:7]]}}",
				"{query0={id=[[@3,9:10='id',<419>,1:9]]}}",
				"{def_query0={window_ordered_by=[{name=created_at, table_ref=a}], query_dictionary={id=[[@3,9:10='id',<419>,1:9]]}, table_dictionary={events={created_at=[[@17,72:72='a',<419>,1:72], [@27,106:106='a',<419>,1:106]], id=[[@1,7:7='a',<419>,1:7]]}}, window_partition_by=[{name=created_at, table_ref=a}], filters=[{name=created_at, table_ref=a}], interface={id=[{name=id, table_ref=a}]}, table_alias={a=events}}}",
				"null");
	}

	@Test
	public void corePlacementDateTruncAcrossCteAndDerivedProducerConsumer() {
		String query = "with base as (select date_trunc(day, e.created_at) as bucket, e.id from events e) select d.bucket from (select b.bucket from base b) d";
		SqlParserAccess parser = parseEndpoint(query, "SQL");
		Assert.assertEquals(parser.getAllDiagnostics().toString(), 1, parser.getAllDiagnostics().size());
		assertDiagnosticAtPosition(parser.getSnippet(),
				"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
				ParseDiagnostic.Severity.WARNING,
				"snowflake_date_trunc_expression at (l:1 c:21) is recognized as Snowflake-specific grammar.",
				null,
				1,
				21);
		assertOutputs(parser,
				"{SQL={with={1={cte={select={1={alias=bucket, datetime_function={function_name=date_trunc, level={canonical=DAY, text=day, form=KEYWORD}, value={column={name=created_at, table_ref=e}}}}, 2={column={name=id, table_ref=e}}}, from={table={alias=e, table=events}}}, alias=base}}, query={select={1={column={name=bucket, table_ref=d}}}, from={table={alias=d, query={select={1={column={name=bucket, table_ref=b}}}, from={table={alias=b, table=base}}}}}}}}",
				"[bucket]",
				"{}",
				"{events={created_at=[[@9,37:37='e',<419>,1:37]], id=[[@16,62:62='e',<419>,1:62]]}}",
				"{query0={bucket=[[@14,54:59='bucket',<419>,1:54], [@30,111:111='b',<419>,1:111]], id=[[@18,64:65='id',<419>,1:64]]}, query1={bucket=[[@32,113:118='bucket',<419>,1:113], [@24,89:89='d',<419>,1:89]]}, query2={bucket=[[@26,91:96='bucket',<419>,1:91]]}}",
				"{def_query2={context_list={base=query0}, query_dictionary={bucket=[[@26,91:96='bucket',<419>,1:91]]}, def_query1={context_list={base=query0, b=query0}, query_dictionary={bucket=[[@32,113:118='bucket',<419>,1:113], [@24,89:89='d',<419>,1:89]]}, interface={bucket=[{name=bucket, table_ref=b}]}, table_alias={b=query0, base=query0}}, def_query0={query_dictionary={bucket=[[@14,54:59='bucket',<419>,1:54], [@30,111:111='b',<419>,1:111]], id=[[@18,64:65='id',<419>,1:64]]}, table_dictionary={events={created_at=[[@9,37:37='e',<419>,1:37]], id=[[@16,62:62='e',<419>,1:62]]}}, interface={bucket=[{name=created_at, table_ref=e}], id=[{name=id, table_ref=e}]}, table_alias={e=events}}, interface={bucket=[{name=bucket, table_ref=d}]}, table_alias={d=query1, base=query0}}}",
				"null");
	}

	@Test
	public void malformedInvalidPartFatalForDateTrunc() {
		SqlParserAccess dateTrunc = parseEndpoint("select date_trunc(bogus, a.created_at) as bucket from events a", "SQL");
		Assert.assertEquals(dateTrunc.getAllDiagnostics().toString(), 2, dateTrunc.getAllDiagnostics().size());
		assertDiagnosticAtPosition(dateTrunc.getSnippet(), "STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
				ParseDiagnostic.Severity.WARNING,
				"snowflake_date_trunc_expression at (l:1 c:7) is recognized as Snowflake-specific grammar.",
				null, 1, 7);
		assertFatalDiagnosticAtPositionWithFullMessage(dateTrunc.getSnippet(),
				"INVALID_DATETIME_PART",
				"Invalid date/time part bogus for DATE_TRUNC at (l:1 c:18).",
				"bogus",
				1,
				18);
		assertOutputs(dateTrunc,
				"{SQL={select={1={alias=bucket, datetime_function={function_name=date_trunc, level={canonical=BOGUS, text=bogus, form=KEYWORD}, value={column={name=created_at, table_ref=a}}}}}, from={table={alias=a, table=events}}}}",
				"[bucket]",
				"{}",
				"{events={created_at=[[@5,25:25='a',<419>,1:25]]}}",
				"{query0={bucket=[[@10,42:47='bucket',<419>,1:42]]}}",
				"{def_query0={query_dictionary={bucket=[[@10,42:47='bucket',<419>,1:42]]}, table_dictionary={events={created_at=[[@5,25:25='a',<419>,1:25]]}}, interface={bucket=[{name=created_at, table_ref=a}]}, table_alias={a=events}}}",
				"null");
	}

	@Test
	public void malformedInvalidPartFatalForTrunc() {
		SqlParserAccess trunc = parseEndpoint("select trunc(a.created_at, bogus) as bucket from events a", "SQL");
		Assert.assertEquals(trunc.getAllDiagnostics().toString(), 2, trunc.getAllDiagnostics().size());
		assertDiagnosticAtPosition(trunc.getSnippet(), "STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
				ParseDiagnostic.Severity.WARNING,
				"snowflake_datetime_trunc_expression at (l:1 c:7) is recognized as Snowflake-specific grammar.",
				null, 1, 7);
		assertFatalDiagnosticAtPositionWithFullMessage(trunc.getSnippet(),
				"INVALID_DATETIME_PART",
				"Invalid date/time part bogus for TRUNC at (l:1 c:27).",
				"bogus",
				1,
				27);
		assertOutputs(trunc,
				"{SQL={select={1={alias=bucket, datetime_function={function_name=trunc, level={canonical=BOGUS, text=bogus, form=KEYWORD}, value={column={name=created_at, table_ref=a}}}}}, from={table={alias=a, table=events}}}}",
				"[bucket]",
				"{}",
				"{events={created_at=[[@3,13:13='a',<419>,1:13]]}}",
				"{query0={bucket=[[@10,37:42='bucket',<419>,1:37]]}}",
				"{def_query0={query_dictionary={bucket=[[@10,37:42='bucket',<419>,1:37]]}, table_dictionary={events={created_at=[[@3,13:13='a',<419>,1:13]]}}, interface={bucket=[{name=created_at, table_ref=a}]}, table_alias={a=events}}}",
				"null");
	}

	@Test
	public void malformedAmbiguousAndUnresolvedValueForDateTrunc() {
		SqlParserAccess dateTrunc = parseEndpoint("select date_trunc(day, created_at) as bucket from events a join accounts b on a.account_id = b.id", "SQL");
		Assert.assertEquals(dateTrunc.getAllDiagnostics().toString(), 3, dateTrunc.getAllDiagnostics().size());
		assertDiagnosticAtPosition(dateTrunc.getSnippet(), "STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
				ParseDiagnostic.Severity.WARNING,
				"snowflake_date_trunc_expression at (l:1 c:7) is recognized as Snowflake-specific grammar.",
				null, 1, 7);
		assertDiagnosticAtPosition(dateTrunc.getSnippet(), "AMBIGUOUS_COLUMN_REFERENCE",
				ParseDiagnostic.Severity.SEVERE_WARNING,
				"Ambiguous column reference 'created_at' at (l:1 c:23). Possible sources: [accounts, events]",
				"created_at", 1, 23);
		assertDiagnosticAtPosition(dateTrunc.getSnippet(), "UNRESOLVED_UNQUALIFIED_COLUMNS",
				ParseDiagnostic.Severity.ERROR,
				"created_at",
				"created_at", 1, 23);
		assertOutputs(dateTrunc,
				"{SQL={select={1={alias=bucket, datetime_function={function_name=date_trunc, level={canonical=DAY, text=day, form=KEYWORD}, value={column={name=created_at, table_ref=null}}}}}, from={join={1={table={alias=a, table=events}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}}}",
				"[bucket]",
				"{}",
				"{accounts={id=[[@20,93:93='b',<419>,1:93]]}, events={account_id=[[@16,78:78='a',<419>,1:78]]}}",
				"{query0={bucket=[[@8,38:43='bucket',<419>,1:38]]}}",
				"{def_query0={query_dictionary={bucket=[[@8,38:43='bucket',<419>,1:38]]}, table_dictionary={accounts={id=[[@20,93:93='b',<419>,1:93]]}, events={account_id=[[@16,78:78='a',<419>,1:78]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}], interface={bucket=[{name=created_at, table_ref=null}]}, table_alias={a=events, b=accounts}}}",
				"null");
	}

	@Test
	public void malformedAmbiguousAndUnresolvedValueForTrunc() {
		SqlParserAccess trunc = parseEndpoint("select trunc(created_at, day) as bucket from events a join accounts b on a.account_id = b.id", "SQL");
		Assert.assertEquals(trunc.getAllDiagnostics().toString(), 3, trunc.getAllDiagnostics().size());
		assertDiagnosticAtPosition(trunc.getSnippet(), "STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
				ParseDiagnostic.Severity.WARNING,
				"snowflake_datetime_trunc_expression at (l:1 c:7) is recognized as Snowflake-specific grammar.",
				null, 1, 7);
		assertDiagnosticAtPosition(trunc.getSnippet(), "AMBIGUOUS_COLUMN_REFERENCE",
				ParseDiagnostic.Severity.SEVERE_WARNING,
				"Ambiguous column reference 'created_at' at (l:1 c:13). Possible sources: [accounts, events]",
				"created_at", 1, 13);
		assertDiagnosticAtPosition(trunc.getSnippet(), "UNRESOLVED_UNQUALIFIED_COLUMNS",
				ParseDiagnostic.Severity.ERROR,
				"created_at",
				"created_at", 1, 13);
		assertOutputs(trunc,
				"{SQL={select={1={alias=bucket, datetime_function={function_name=trunc, level={canonical=DAY, text=day, form=KEYWORD}, value={column={name=created_at, table_ref=null}}}}}, from={join={1={table={alias=a, table=events}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}}}",
				"[bucket]",
				"{}",
				"{accounts={id=[[@20,88:88='b',<419>,1:88]]}, events={account_id=[[@16,73:73='a',<419>,1:73]]}}",
				"{query0={bucket=[[@8,33:38='bucket',<419>,1:33]]}}",
				"{def_query0={query_dictionary={bucket=[[@8,33:38='bucket',<419>,1:33]]}, table_dictionary={accounts={id=[[@20,88:88='b',<419>,1:88]]}, events={account_id=[[@16,73:73='a',<419>,1:73]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}], interface={bucket=[{name=created_at, table_ref=null}]}, table_alias={a=events, b=accounts}}}",
				"null");
	}

	@Test
	public void malformedHiddenAndMissingDerivedOutputForDateTrunc() {
		SqlParserAccess dateTrunc = parseEndpoint("select date_trunc(day, d.created_at) as bucket from (select e.created_at as created_out from events e) d", "SQL");
		Assert.assertEquals(dateTrunc.getAllDiagnostics().toString(), 2, dateTrunc.getAllDiagnostics().size());
		assertDiagnosticAtPosition(dateTrunc.getSnippet(), "STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
				ParseDiagnostic.Severity.WARNING,
				"snowflake_date_trunc_expression at (l:1 c:7) is recognized as Snowflake-specific grammar.",
				null, 1, 7);
		assertFatalDiagnosticAtPositionWithFullMessage(dateTrunc.getSnippet(),
				"QUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIAS",
				"Qualified column 'created_at' at (l:1 c:23) was not found in output interface of query alias 'd'.",
				"d.created_at",
				1,
				23);
		assertOutputs(dateTrunc,
				"{SQL={select={1={alias=bucket, datetime_function={function_name=date_trunc, level={canonical=DAY, text=day, form=KEYWORD}, value={column={name=created_at, table_ref=d}}}}}, from={table={alias=d, query={select={1={column={name=created_at, table_ref=e}, alias=created_out}}, from={table={alias=e, table=events}}}}}}}",
				"[bucket]",
				"{}",
				"{events={created_at=[[@14,60:60='e',<419>,1:60]]}}",
				"{query0={created_out=[[@18,76:86='created_out',<419>,1:76]]}, query1={bucket=[[@10,40:45='bucket',<419>,1:40]]}}",
				"{def_query1={query_dictionary={bucket=[[@10,40:45='bucket',<419>,1:40]]}, def_query0={query_dictionary={created_out=[[@18,76:86='created_out',<419>,1:76]]}, table_dictionary={events={created_at=[[@14,60:60='e',<419>,1:60]]}}, interface={created_out=[{name=created_at, table_ref=e}]}, table_alias={e=events}}, interface={bucket=[{name=created_at, table_ref=d}]}, table_alias={d=query0}}}",
				"null");
	}

	@Test
	public void malformedHiddenAndMissingDerivedOutputForTrunc() {
		SqlParserAccess trunc = parseEndpoint("select trunc(d.missing_ts, day) as bucket from (select e.created_at from events e) d", "SQL");
		Assert.assertEquals(trunc.getAllDiagnostics().toString(), 2, trunc.getAllDiagnostics().size());
		assertDiagnosticAtPosition(trunc.getSnippet(), "STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
				ParseDiagnostic.Severity.WARNING,
				"snowflake_datetime_trunc_expression at (l:1 c:7) is recognized as Snowflake-specific grammar.",
				null, 1, 7);
		assertFatalDiagnosticAtPositionWithFullMessage(trunc.getSnippet(),
				"QUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIAS",
				"Qualified column 'missing_ts' at (l:1 c:13) was not found in output interface of query alias 'd'.",
				"d.missing_ts",
				1,
				13);
		assertOutputs(trunc,
				"{SQL={select={1={alias=bucket, datetime_function={function_name=trunc, level={canonical=DAY, text=day, form=KEYWORD}, value={column={name=missing_ts, table_ref=d}}}}}, from={table={alias=d, query={select={1={column={name=created_at, table_ref=e}}}, from={table={alias=e, table=events}}}}}}}",
				"[bucket]",
				"{}",
				"{events={created_at=[[@14,55:55='e',<419>,1:55]]}}",
				"{query0={created_at=[[@16,57:66='created_at',<419>,1:57]]}, query1={bucket=[[@10,35:40='bucket',<419>,1:35]]}}",
				"{def_query1={query_dictionary={bucket=[[@10,35:40='bucket',<419>,1:35]]}, def_query0={query_dictionary={created_at=[[@16,57:66='created_at',<419>,1:57]]}, table_dictionary={events={created_at=[[@14,55:55='e',<419>,1:55]]}}, interface={created_at=[{name=created_at, table_ref=e}]}, table_alias={e=events}}, interface={bucket=[{name=missing_ts, table_ref=d}]}, table_alias={d=query0}}}",
				"null");
	}

	@Test
	public void malformedTruncNumericRemainGenericControl() {
		SqlParserAccess numeric = parseEndpoint("select trunc(42.75, 1) as rounded from events", "SQL");
		Assert.assertEquals(numeric.getAllDiagnostics().toString(), 0, numeric.getAllDiagnostics().size());
		assertOutputs(numeric,
				"{SQL={select={1={function={parameters={1={literal=42.75}, 2={literal=1}}, function_name=trunc}, alias=rounded}}, from={table={alias=null, table=events}}}}",
				"[rounded]",
				"{}",
				"{events={}}",
				"{query0={rounded=[[@10,26:32='rounded',<419>,1:26]]}}",
				"{def_query0={query_dictionary={rounded=[[@10,26:32='rounded',<419>,1:26]]}, table_dictionary={events={}}, interface={rounded=[]}}}",
				"null");
	}

	@Test
	public void malformedTruncSingleArgRemainGenericControl() {
		SqlParserAccess singleArg = parseEndpoint("select trunc(a.created_at) as maybe_generic from events a", "SQL");
		Assert.assertEquals(singleArg.getAllDiagnostics().toString(), 0, singleArg.getAllDiagnostics().size());
		assertOutputs(singleArg,
				"{SQL={select={1={function={parameters={1={column={name=created_at, table_ref=a}}}, function_name=trunc}, alias=maybe_generic}}, from={table={alias=a, table=events}}}}",
				"[maybe_generic]",
				"{}",
				"{events={created_at=[[@3,13:13='a',<419>,1:13]]}}",
				"{query0={maybe_generic=[[@8,30:42='maybe_generic',<419>,1:30]]}}",
				"{def_query0={query_dictionary={maybe_generic=[[@8,30:42='maybe_generic',<419>,1:30]]}, table_dictionary={events={created_at=[[@3,13:13='a',<419>,1:13]]}}, interface={maybe_generic=[{name=created_at, table_ref=a}]}, table_alias={a=events}}}",
				"null");
	}

	@Test
	public void regressionDmlInsertPlacementForDateTrunc() {
		SqlParserAccess insertParse = parseEndpoint("insert into archive_events (bucket) select date_trunc(day, e.created_at) as bucket from events e", "SQL");
		Assert.assertEquals(insertParse.getAllDiagnostics().toString(), 1, insertParse.getAllDiagnostics().size());
		assertDiagnosticAtPosition(insertParse.getSnippet(), "STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
				ParseDiagnostic.Severity.WARNING,
				"snowflake_date_trunc_expression at (l:1 c:43) is recognized as Snowflake-specific grammar.",
				null, 1, 43);
		assertOutputs(insertParse,
				"{SQL={insert={preamble=insert_into, from={from={table={alias=e, table=events}}, select={1={alias=bucket, datetime_function={function_name=date_trunc, level={canonical=DAY, text=day, form=KEYWORD}, value={column={name=created_at, table_ref=e}}}}}}, target_table={table={alias=null, table=archive_events}}, columns={1={column={name=bucket, table_ref=null}}}}}}",
				"[bucket]",
				"{}",
				"{archive_events={bucket=[[@4,28:33='bucket',<419>,1:28]]}, events={created_at=[[@11,59:59='e',<419>,1:59]]}}",
				"{query0={bucket=[[@16,76:81='bucket',<419>,1:76]]}, insert1={bucket=[[@4,28:33='bucket',<419>,1:28]]}}",
				"{def_insert1={query_dictionary={bucket=[[@4,28:33='bucket',<419>,1:28]]}, table_dictionary={archive_events={bucket=[[@4,28:33='bucket',<419>,1:28]]}}, def_query0={query_dictionary={bucket=[[@16,76:81='bucket',<419>,1:76]]}, table_dictionary={events={created_at=[[@11,59:59='e',<419>,1:59]]}}, interface={bucket=[{name=created_at, table_ref=e}]}, table_alias={e=events}}, interface={bucket=[{name=bucket, table_ref=query0}]}}}",
				"null");
	}

	@Test
	public void regressionDmlUpdatePlacementForTrunc() {
		SqlParserAccess updateParse = parseEndpoint("update events e set bucket = trunc(e.created_at, day)", "SQL");
		Assert.assertEquals(updateParse.getAllDiagnostics().toString(), 1, updateParse.getAllDiagnostics().size());
		assertDiagnosticAtPosition(updateParse.getSnippet(), "STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
				ParseDiagnostic.Severity.WARNING,
				"snowflake_datetime_trunc_expression at (l:1 c:29) is recognized as Snowflake-specific grammar.",
				null, 1, 29);
		assertOutputs(updateParse,
				"{SQL={update={table={alias=e, table=events}, assignments={1={set={column={name=bucket, table_ref=null}}, to={datetime_function={function_name=trunc, level={canonical=DAY, text=day, form=KEYWORD}, value={column={name=created_at, table_ref=e}}}}}}}}}",
				"[bucket]",
				"{}",
				"{events={}}",
				"{update0={bucket=[[@4,20:25='bucket',<419>,1:20]]}}",
				"{def_update0={assignments={bucket=[{name=created_at, table_ref=e}]}, table_dictionary={events={}}, update_dictionary={bucket=[[@4,20:25='bucket',<419>,1:20]]}, target_table={events={bucket=[[@4,20:25='bucket',<419>,1:20]], created_at=[[@8,35:35='e',<419>,1:35]]}}, table_alias={e=events}, lhs_unresolved_columns={bucket={column={name=bucket, table_ref=null}, locations=[[@4,20:25='bucket',<419>,1:20]]}}}}",
				"null");
	}

	@Test
	public void regressionDmlDeletePlacementForExtract() {
		SqlParserAccess deleteParse = parseEndpoint("delete from events e where extract(epoch_second from e.created_at) > 0", "SQL");
		Assert.assertEquals(deleteParse.getAllDiagnostics().toString(), 1, deleteParse.getAllDiagnostics().size());
		assertDiagnosticAtPosition(deleteParse.getSnippet(), "STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
				ParseDiagnostic.Severity.WARNING,
				"EXTRACT/DATE_PART option 'epoch_second' at (l:1 c:27) is recognized as Snowflake-specific grammar.",
				null, 1, 27);
		assertOutputs(deleteParse,
				"{SQL={delete={table={alias=e, table=events}, where={condition={left={datetime_function={function_name=extract, part_form=KEYWORD, part=epoch_second, source={column={name=created_at, table_ref=e}}}}, right={literal=0}, operator=>}}}}}",
				"[]",
				"{}",
				"{events={created_at=[[@9,53:53='e',<419>,1:53]]}}",
				"{}",
				"{def_delete0={query_dictionary={}, table_dictionary={events={created_at=[[@9,53:53='e',<419>,1:53]]}}, filters=[{name=created_at, table_ref=e}], interface=null, table_alias={e=events}}}",
				"null");
	}

	@Test
	public void regressionSchemaQualifiedSameNameUdfStaysGeneric() {
		SqlParserAccess parser = parseEndpoint("select custom.date_trunc(day, a.created_at) as udf_bucket from events a", "SQL");
		Assert.assertEquals(parser.getAllDiagnostics().toString(), 0, parser.getAllDiagnostics().size());
		assertOutputs(parser,
				"{SQL={select={1={function={schema=custom, parameters={1={column={name=day, table_ref=null}}, 2={column={name=created_at, table_ref=a}}}, function_name=date_trunc}, alias=udf_bucket}}, from={table={alias=a, table=events}}}}",
				"[udf_bucket]",
				"{}",
				"{events={created_at=[[@7,30:30='a',<419>,1:30]], day=[[@5,25:27='day',<73>,1:25]]}}",
				"{query0={udf_bucket=[[@12,47:56='udf_bucket',<419>,1:47]]}}",
				"{def_query0={query_dictionary={udf_bucket=[[@12,47:56='udf_bucket',<419>,1:47]]}, table_dictionary={events={created_at=[[@7,30:30='a',<419>,1:30]], day=[[@5,25:27='day',<73>,1:25]]}}, interface={udf_bucket=[{name=day, table_ref=events}, {name=created_at, table_ref=a}]}, table_alias={a=events}}}",
				"null");
	}

	@Test
	public void regressionScriptResetForTruncAndDateTrunc() {
		String script = "select date_trunc(day, a.created_at) as bucket from events a; select trunc(e.created_at, day) as bucket from events e";
		SqlParserAccess scriptParser = parseEndpoint(script, "SCRIPT");
		Assert.assertEquals(scriptParser.getAllDiagnostics().toString(), 2, scriptParser.getAllDiagnostics().size());
		assertDiagnosticAtPosition(scriptParser.getSnippet(), "STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
				ParseDiagnostic.Severity.WARNING,
				"snowflake_date_trunc_expression at (l:1 c:7) is recognized as Snowflake-specific grammar.",
				null, 1, 7);
		assertDiagnosticAtPosition(scriptParser.getSnippet(), "STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
				ParseDiagnostic.Severity.WARNING,
				"snowflake_datetime_trunc_expression at (l:1 c:69) is recognized as Snowflake-specific grammar.",
				null, 1, 69);
		assertOutputs(scriptParser,
				"{SCRIPT={1={select={1={alias=bucket, datetime_function={function_name=date_trunc, level={canonical=DAY, text=day, form=KEYWORD}, value={column={name=created_at, table_ref=a}}}}}, from={table={alias=a, table=events}}}, 2={select={1={alias=bucket, datetime_function={function_name=trunc, level={canonical=DAY, text=day, form=KEYWORD}, value={column={name=created_at, table_ref=e}}}}}, from={table={alias=e, table=events}}}}}",
				"[]",
				"{SCRIPT={1={}, 2={}}}",
				"{SCRIPT={1={events={created_at=[[@5,23:23='a',<419>,1:23]]}}, 2={events={created_at=[[@18,75:75='e',<419>,1:75]]}}}}",
				"{SCRIPT={1={query0={bucket=[[@10,40:45='bucket',<419>,1:40]]}}, 2={query0={bucket=[[@25,97:102='bucket',<419>,1:97]]}}}}",
				"{SCRIPT={1={def_query0={query_dictionary={bucket=[[@10,40:45='bucket',<419>,1:40]]}, table_dictionary={events={created_at=[[@5,23:23='a',<419>,1:23]]}}, interface={bucket=[{name=created_at, table_ref=a}]}, table_alias={a=events}}}, 2={def_query0={query_dictionary={bucket=[[@25,97:102='bucket',<419>,1:97]]}, table_dictionary={events={created_at=[[@18,75:75='e',<419>,1:75]]}}, interface={bucket=[{name=created_at, table_ref=e}]}, table_alias={e=events}}}}}",
				"{SCRIPT={1={queryInterface=[bucket], Walker Warnings=[snowflake_date_trunc_expression at (l:1 c:7) is recognized as Snowflake-specific grammar., snowflake_datetime_trunc_expression at (l:1 c:69) is recognized as Snowflake-specific grammar.]}, 2={queryInterface=[bucket], Walker Warnings=[snowflake_date_trunc_expression at (l:1 c:7) is recognized as Snowflake-specific grammar., snowflake_datetime_trunc_expression at (l:1 c:69) is recognized as Snowflake-specific grammar.]}}}");
	}

	@Test
	public void regressionMixedAffinityFatalForExtractAndDatePart() {
		String mixed = "SELECT EXTRACT(epoch_second FROM ts), DATE_PART(DOW FROM order_date) FROM orders";
		SqlParserAccess mixedParser = parseEndpoint(mixed, "SQL");
		Assert.assertEquals(mixedParser.getAllDiagnostics().toString(), 2, mixedParser.getAllDiagnostics().size());
		assertDiagnosticAtPosition(mixedParser.getSnippet(),
				"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
				ParseDiagnostic.Severity.WARNING,
				"EXTRACT/DATE_PART option 'epoch_second' at (l:1 c:7) is recognized as Snowflake-specific grammar.",
				null,
				1,
				7);
		assertFatalDiagnosticAtPosition(mixedParser.getSnippet(),
				"STATEMENT_MIXED_DIALECT_GRAMMAR",
				"Statement mixes Snowflake-only and PostgreSQL-only grammar at (l:1 c:38); unlikely to run on either engine.",
				null,
				1,
				38);
		assertOutputs(mixedParser,
				"{SQL={select={1={datetime_function={function_name=EXTRACT, part_form=KEYWORD, part=epoch_second, source={column={name=ts, table_ref=null}}}}, 2={datetime_function={function_name=DATE_PART, part_form=KEYWORD, part=DOW, source={column={name=order_date, table_ref=null}}}}}, from={table={alias=null, table=orders}}}}",
				"[unnamed_1, unnamed_0]",
				"{}",
				"{orders={order_date=[[@12,57:66='order_date',<419>,1:57]], ts=[[@5,33:34='ts',<419>,1:33]]}}",
				"{query0={unnamed_1=[[@13,67:67=')',<288>,1:67]], unnamed_0=[[@6,35:35=')',<288>,1:35]]}}",
				"{def_query0={query_dictionary={unnamed_1=[[@13,67:67=')',<288>,1:67]], unnamed_0=[[@6,35:35=')',<288>,1:35]]}, table_dictionary={orders={order_date=[[@12,57:66='order_date',<419>,1:57]], ts=[[@5,33:34='ts',<419>,1:33]]}}, interface={unnamed_1=[{name=order_date, table_ref=orders}], unnamed_0=[{name=ts, table_ref=orders}]}}}",
				"null");
	}
}
