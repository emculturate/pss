package sql.walker;

import org.junit.Assert;
import org.junit.Test;

import access.Snippet;
import access.SqlParserAccess;
import errorhandling.ParseDiagnostic;

public class SqlEventWalkerDatetimeBoundarySliceTests extends AbstractSqlParseEventWalkerTest {

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
	public void coreTimeSliceInWhereCollectsFilters() {
		SqlParserAccess parser = parseEndpoint("select a.id from events a where time_slice(a.created_at, 15, minute, start) > DATE '2026-01-01'", "SQL");
		Assert.assertEquals(parser.getAllDiagnostics().toString(), 1, parser.getAllDiagnostics().size());
		assertDiagnosticAtPosition(parser.getSnippet(),
				"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
				ParseDiagnostic.Severity.WARNING,
				"snowflake_time_slice_expression at (l:1 c:32) is recognized as Snowflake-specific grammar.",
				null, 1, 32);
		assertOutputs(parser,
				"{SQL={select={1={column={name=id, table_ref=a}}}, from={table={alias=a, table=events}}, where={condition={left={datetime_function={function_name=time_slice, value={column={name=created_at, table_ref=a}}, slice_length={literal=15}, level={canonical=MINUTE, text=minute, form=KEYWORD}, boundary={canonical=START, text=start, form=KEYWORD}}}, right={source_type=DATE, literal=2026-01-01}, operator=>}}}}",
				"[id]",
				"{}",
				"{events={created_at=[[@10,43:43='a',<419>,1:43]], id=[[@1,7:7='a',<419>,1:7]]}}",
				"{query0={id=[[@3,9:10='id',<419>,1:9]]}}",
				"{def_query0={query_dictionary={id=[[@3,9:10='id',<419>,1:9]]}, table_dictionary={events={created_at=[[@10,43:43='a',<419>,1:43]], id=[[@1,7:7='a',<419>,1:7]]}}, filters=[{name=created_at, table_ref=a}], interface={id=[{name=id, table_ref=a}]}, table_alias={a=events}}}",
				"null");
	}

	@Test
	public void coreLastDayInHavingCollectsGroupedAndFilterDependencies() {
		SqlParserAccess parser = parseEndpoint("select a.account_id, count(*) as total from events a group by a.account_id having max(last_day(a.created_at)) > DATE '2026-01-01'", "SQL");
		Assert.assertEquals(parser.getAllDiagnostics().toString(), 1, parser.getAllDiagnostics().size());
		assertDiagnosticAtPosition(parser.getSnippet(),
				"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
				ParseDiagnostic.Severity.WARNING,
				"snowflake_last_day_expression at (l:1 c:86) is recognized as Snowflake-specific grammar.",
				null, 1, 86);
		assertOutputs(parser,
				"{SQL={select={1={column={name=account_id, table_ref=a}}, 2={function={function_name=COUNT, qualifier=null, parameters=*}, alias=total}}, having={condition={left={function={function_name=max, qualifier=null, parameters={datetime_function={function_name=last_day, value={column={name=created_at, table_ref=a}}}}}}, right={source_type=DATE, literal=2026-01-01}, operator=>}}, from={table={alias=a, table=events}}, groupby={1={column={name=account_id, table_ref=a}}}}}",
				"[total, account_id]",
				"{}",
				"{events={account_id=[[@1,7:7='a',<419>,1:7], [@16,62:62='a',<419>,1:62]], created_at=[[@24,95:95='a',<419>,1:95]], *=[[@7,27:27='*',<291>,1:27]]}}",
				"{query0={total=[[@10,33:37='total',<419>,1:33]], account_id=[[@3,9:18='account_id',<419>,1:9]]}}",
				"{def_query0={query_dictionary={total=[[@10,33:37='total',<419>,1:33]], account_id=[[@3,9:18='account_id',<419>,1:9]]}, table_dictionary={events={account_id=[[@1,7:7='a',<419>,1:7], [@16,62:62='a',<419>,1:62]], created_at=[[@24,95:95='a',<419>,1:95]], *=[[@7,27:27='*',<291>,1:27]]}}, grouped_by=[{name=account_id, table_ref=a}], filters=[{name=created_at, table_ref=a}], interface={total=[], account_id=[{name=account_id, table_ref=a}]}, table_alias={a=events}}}",
				"null");
	}

	@Test
	public void coreNextDayInOrderByTracksOrderedBy() {
		SqlParserAccess parser = parseEndpoint("select next_day(a.created_at, monday) as next_mon from events a order by next_mon", "SQL");
		Assert.assertEquals(parser.getAllDiagnostics().toString(), 1, parser.getAllDiagnostics().size());
		assertDiagnosticAtPosition(parser.getSnippet(),
				"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
				ParseDiagnostic.Severity.WARNING,
				"snowflake_next_day_expression at (l:1 c:7) is recognized as Snowflake-specific grammar.",
				null, 1, 7);
		assertOutputs(parser,
				"{SQL={select={1={alias=next_mon, datetime_function={function_name=next_day, value={column={name=created_at, table_ref=a}}, weekday={canonical=MONDAY, text=monday, form=KEYWORD}}}}, orderby={1={null_order=null, predicand={column={name=next_mon, table_ref=null}}, sort_order=ASC}}, from={table={alias=a, table=events}}}}",
				"[next_mon]",
				"{}",
				"{events={created_at=[[@3,16:16='a',<419>,1:16]]}}",
				"{query0={next_mon=[[@10,41:48='next_mon',<419>,1:41], [@16,73:80='next_mon',<419>,1:73]]}}",
				"{def_query0={query_dictionary={next_mon=[[@10,41:48='next_mon',<419>,1:41], [@16,73:80='next_mon',<419>,1:73]]}, table_dictionary={events={created_at=[[@3,16:16='a',<419>,1:16]]}}, ordered_by=[{name=next_mon, table_ref=query0}], interface={next_mon=[{name=created_at, table_ref=a}]}, table_alias={a=events}}}",
				"null");
	}

	@Test
	public void corePreviousDayInWindowTracksWindowBuckets() {
		SqlParserAccess parser = parseEndpoint("select a.id from events a qualify row_number() over (partition by previous_day(a.created_at, 'friday') order by a.id) = 1", "SQL");
		Assert.assertEquals(parser.getAllDiagnostics().toString(), 1, parser.getAllDiagnostics().size());
		assertDiagnosticAtPosition(parser.getSnippet(),
				"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
				ParseDiagnostic.Severity.WARNING,
				"snowflake_previous_day_expression at (l:1 c:66) is recognized as Snowflake-specific grammar.",
				null, 1, 66);
		assertOutputs(parser,
				"{SQL={select={1={column={name=id, table_ref=a}}}, from={table={alias=a, table=events}}, qualify={condition={left={window_function={over={partition_by={1={datetime_function={function_name=previous_day, value={column={name=created_at, table_ref=a}}, weekday={canonical=FRIDAY, text=friday, form=STRING}}}}, orderby={1={null_order=null, predicand={column={name=id, table_ref=a}}, sort_order=ASC}}}, function={function_name=row_number, parameters=null}}}, right={literal=1}, operator==}}}}",
				"[id]",
				"{}",
				"{events={created_at=[[@17,79:79='a',<419>,1:79]], id=[[@1,7:7='a',<419>,1:7], [@25,112:112='a',<419>,1:112]]}}",
				"{query0={id=[[@3,9:10='id',<419>,1:9]]}}",
				"{def_query0={window_ordered_by=[{name=id, table_ref=a}], query_dictionary={id=[[@3,9:10='id',<419>,1:9]]}, table_dictionary={events={created_at=[[@17,79:79='a',<419>,1:79]], id=[[@1,7:7='a',<419>,1:7], [@25,112:112='a',<419>,1:112]]}}, window_partition_by=[{name=created_at, table_ref=a}], filters=[{name=created_at, table_ref=a}, {name=id, table_ref=a}], interface={id=[{name=id, table_ref=a}]}, table_alias={a=events}}}",
				"null");
	}

	@Test
	public void coreTimeSliceAcrossCteAndDerivedProducerConsumer() {
		SqlParserAccess parser = parseEndpoint("with base as (select time_slice(e.created_at, 30, minute, 'end') as bucket from events e) select d.bucket from (select b.bucket from base b) d", "SQL");
		Assert.assertEquals(parser.getAllDiagnostics().toString(), 1, parser.getAllDiagnostics().size());
		assertDiagnosticAtPosition(parser.getSnippet(),
				"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
				ParseDiagnostic.Severity.WARNING,
				"snowflake_time_slice_expression at (l:1 c:21) is recognized as Snowflake-specific grammar.",
				null, 1, 21);
		assertOutputs(parser,
				"{SQL={with={1={cte={select={1={alias=bucket, datetime_function={function_name=time_slice, value={column={name=created_at, table_ref=e}}, slice_length={literal=30}, level={canonical=MINUTE, text=minute, form=KEYWORD}, boundary={canonical=END, text=end, form=STRING}}}}, from={table={alias=e, table=events}}}, alias=base}}, query={select={1={column={name=bucket, table_ref=d}}}, from={table={alias=d, query={select={1={column={name=bucket, table_ref=b}}}, from={table={alias=b, table=base}}}}}}}}",
				"[bucket]",
				"{}",
				"{events={created_at=[[@7,32:32='e',<419>,1:32]]}}",
				"{query0={bucket=[[@18,68:73='bucket',<419>,1:68], [@30,119:119='b',<419>,1:119]]}, query1={bucket=[[@32,121:126='bucket',<419>,1:121], [@24,97:97='d',<419>,1:97]]}, query2={bucket=[[@26,99:104='bucket',<419>,1:99]]}}",
				"{def_query2={context_list={base=query0}, query_dictionary={bucket=[[@26,99:104='bucket',<419>,1:99]]}, def_query1={context_list={base=query0, b=query0}, query_dictionary={bucket=[[@32,121:126='bucket',<419>,1:121], [@24,97:97='d',<419>,1:97]]}, interface={bucket=[{name=bucket, table_ref=b}]}, table_alias={b=query0, base=query0}}, def_query0={query_dictionary={bucket=[[@18,68:73='bucket',<419>,1:68], [@30,119:119='b',<419>,1:119]]}, table_dictionary={events={created_at=[[@7,32:32='e',<419>,1:32]]}}, interface={bucket=[{name=created_at, table_ref=e}]}, table_alias={e=events}}, interface={bucket=[{name=bucket, table_ref=d}]}, table_alias={d=query1, base=query0}}}",
				"null");
	}

	@Test
	public void malformedTimeSliceInvalidLevelFatal() {
		SqlParserAccess parser = parseEndpoint("select time_slice(a.created_at, 15, bogus, start) as bucket from events a", "SQL");
		Assert.assertEquals(parser.getAllDiagnostics().toString(), 2, parser.getAllDiagnostics().size());
		assertDiagnosticAtPosition(parser.getSnippet(),
				"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
				ParseDiagnostic.Severity.WARNING,
				"snowflake_time_slice_expression at (l:1 c:7) is recognized as Snowflake-specific grammar.",
				null, 1, 7);
		assertFatalDiagnosticAtPositionWithFullMessage(parser.getSnippet(),
				"INVALID_DATETIME_PART",
				"Invalid date/time part bogus for TIME_SLICE at (l:1 c:36).",
				"bogus",
				1,
				36);
		assertOutputs(parser,
				"{SQL={select={1={alias=bucket, datetime_function={function_name=time_slice, value={column={name=created_at, table_ref=a}}, slice_length={literal=15}, level={canonical=BOGUS, text=bogus, form=KEYWORD}, boundary={canonical=START, text=start, form=KEYWORD}}}}, from={table={alias=a, table=events}}}}",
				"[bucket]",
				"{}",
				"{events={created_at=[[@3,18:18='a',<419>,1:18]]}}",
				"{query0={bucket=[[@14,53:58='bucket',<419>,1:53]]}}",
				"{def_query0={query_dictionary={bucket=[[@14,53:58='bucket',<419>,1:53]]}, table_dictionary={events={created_at=[[@3,18:18='a',<419>,1:18]]}}, interface={bucket=[{name=created_at, table_ref=a}]}, table_alias={a=events}}}",
				"null");
	}

	@Test
	public void malformedTimeSliceInvalidBoundaryFatal() {
		SqlParserAccess parser = parseEndpoint("select time_slice(a.created_at, 15, minute, middle) as bucket from events a", "SQL");
		Assert.assertEquals(parser.getAllDiagnostics().toString(), 2, parser.getAllDiagnostics().size());
		assertDiagnosticAtPosition(parser.getSnippet(),
				"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
				ParseDiagnostic.Severity.WARNING,
				"snowflake_time_slice_expression at (l:1 c:7) is recognized as Snowflake-specific grammar.",
				null, 1, 7);
		assertFatalDiagnosticAtPositionWithFullMessage(parser.getSnippet(),
				"INVALID_DATETIME_PART",
				"Invalid date/time part middle for TIME_SLICE at (l:1 c:44).",
				"middle",
				1,
				44);
		assertOutputs(parser,
				"{SQL={select={1={alias=bucket, datetime_function={function_name=time_slice, value={column={name=created_at, table_ref=a}}, slice_length={literal=15}, level={canonical=MINUTE, text=minute, form=KEYWORD}, boundary={canonical=MIDDLE, text=middle, form=KEYWORD}}}}, from={table={alias=a, table=events}}}}",
				"[bucket]",
				"{}",
				"{events={created_at=[[@3,18:18='a',<419>,1:18]]}}",
				"{query0={bucket=[[@14,55:60='bucket',<419>,1:55]]}}",
				"{def_query0={query_dictionary={bucket=[[@14,55:60='bucket',<419>,1:55]]}, table_dictionary={events={created_at=[[@3,18:18='a',<419>,1:18]]}}, interface={bucket=[{name=created_at, table_ref=a}]}, table_alias={a=events}}}",
				"null");
	}

	@Test
	public void malformedLastDayInvalidLevelFatal() {
		SqlParserAccess parser = parseEndpoint("select last_day(a.created_at, bogus) as month_end from events a", "SQL");
		Assert.assertEquals(parser.getAllDiagnostics().toString(), 2, parser.getAllDiagnostics().size());
		assertDiagnosticAtPosition(parser.getSnippet(),
				"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
				ParseDiagnostic.Severity.WARNING,
				"snowflake_last_day_expression at (l:1 c:7) is recognized as Snowflake-specific grammar.",
				null, 1, 7);
		assertFatalDiagnosticAtPositionWithFullMessage(parser.getSnippet(),
				"INVALID_DATETIME_PART",
				"Invalid date/time part bogus for LAST_DAY at (l:1 c:30).",
				"bogus",
				1,
				30);
		assertOutputs(parser,
				"{SQL={select={1={alias=month_end, datetime_function={function_name=last_day, value={column={name=created_at, table_ref=a}}, level={canonical=BOGUS, text=bogus, form=KEYWORD}}}}, from={table={alias=a, table=events}}}}",
				"[month_end]",
				"{}",
				"{events={created_at=[[@3,16:16='a',<419>,1:16]]}}",
				"{query0={month_end=[[@10,40:48='month_end',<419>,1:40]]}}",
				"{def_query0={query_dictionary={month_end=[[@10,40:48='month_end',<419>,1:40]]}, table_dictionary={events={created_at=[[@3,16:16='a',<419>,1:16]]}}, interface={month_end=[{name=created_at, table_ref=a}]}, table_alias={a=events}}}",
				"null");
	}

	@Test
	public void malformedNextDayInvalidWeekdayFatal() {
		SqlParserAccess parser = parseEndpoint("select next_day(a.created_at, moonday) as next_mon from events a", "SQL");
		Assert.assertEquals(parser.getAllDiagnostics().toString(), 2, parser.getAllDiagnostics().size());
		assertDiagnosticAtPosition(parser.getSnippet(),
				"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
				ParseDiagnostic.Severity.WARNING,
				"snowflake_next_day_expression at (l:1 c:7) is recognized as Snowflake-specific grammar.",
				null, 1, 7);
		assertFatalDiagnosticAtPositionWithFullMessage(parser.getSnippet(),
				"INVALID_DATETIME_PART",
				"Invalid date/time part moonday for NEXT_DAY at (l:1 c:30).",
				"moonday",
				1,
				30);
		assertOutputs(parser,
				"{SQL={select={1={alias=next_mon, datetime_function={function_name=next_day, value={column={name=created_at, table_ref=a}}, weekday={canonical=MOONDAY, text=moonday, form=KEYWORD}}}}, from={table={alias=a, table=events}}}}",
				"[next_mon]",
				"{}",
				"{events={created_at=[[@3,16:16='a',<419>,1:16]]}}",
				"{query0={next_mon=[[@10,42:49='next_mon',<419>,1:42]]}}",
				"{def_query0={query_dictionary={next_mon=[[@10,42:49='next_mon',<419>,1:42]]}, table_dictionary={events={created_at=[[@3,16:16='a',<419>,1:16]]}}, interface={next_mon=[{name=created_at, table_ref=a}]}, table_alias={a=events}}}",
				"null");
	}

	@Test
	public void malformedPreviousDayInvalidWeekdayFatal() {
		SqlParserAccess parser = parseEndpoint("select previous_day(a.created_at, 'fryday') as prev_day from events a", "SQL");
		Assert.assertEquals(parser.getAllDiagnostics().toString(), 2, parser.getAllDiagnostics().size());
		assertDiagnosticAtPosition(parser.getSnippet(),
				"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
				ParseDiagnostic.Severity.WARNING,
				"snowflake_previous_day_expression at (l:1 c:7) is recognized as Snowflake-specific grammar.",
				null, 1, 7);
		assertFatalDiagnosticAtPositionWithFullMessage(parser.getSnippet(),
				"INVALID_DATETIME_PART",
				"Invalid date/time part 'fryday' for PREVIOUS_DAY at (l:1 c:34).",
				"'fryday'",
				1,
				34);
		assertOutputs(parser,
				"{SQL={select={1={alias=prev_day, datetime_function={function_name=previous_day, value={column={name=created_at, table_ref=a}}, weekday={canonical=FRYDAY, text=fryday, form=STRING}}}}, from={table={alias=a, table=events}}}}",
				"[prev_day]",
				"{}",
				"{events={created_at=[[@3,20:20='a',<419>,1:20]]}}",
				"{query0={prev_day=[[@10,47:54='prev_day',<419>,1:47]]}}",
				"{def_query0={query_dictionary={prev_day=[[@10,47:54='prev_day',<419>,1:47]]}, table_dictionary={events={created_at=[[@3,20:20='a',<419>,1:20]]}}, interface={prev_day=[{name=created_at, table_ref=a}]}, table_alias={a=events}}}",
				"null");
	}

	@Test
	public void malformedTimeSliceAmbiguousValueDiagnostics() {
		SqlParserAccess parser = parseEndpoint("select time_slice(created_at, 15, minute, start) as bucket from events a join accounts b on a.account_id = b.id", "SQL");
		Assert.assertEquals(parser.getAllDiagnostics().toString(), 3, parser.getAllDiagnostics().size());
		assertDiagnosticAtPosition(parser.getSnippet(),
				"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
				ParseDiagnostic.Severity.WARNING,
				"snowflake_time_slice_expression at (l:1 c:7) is recognized as Snowflake-specific grammar.",
				null, 1, 7);
		assertDiagnosticAtPosition(parser.getSnippet(), "AMBIGUOUS_COLUMN_REFERENCE",
				ParseDiagnostic.Severity.SEVERE_WARNING,
				"Ambiguous column reference 'created_at' at (l:1 c:18). Possible sources: [accounts, events]",
				"created_at", 1, 18);
		assertDiagnosticAtPosition(parser.getSnippet(), "UNRESOLVED_UNQUALIFIED_COLUMNS",
				ParseDiagnostic.Severity.ERROR,
				"created_at",
				"created_at", 1, 18);
		assertOutputs(parser,
				"{SQL={select={1={alias=bucket, datetime_function={function_name=time_slice, value={column={name=created_at, table_ref=null}}, slice_length={literal=15}, level={canonical=MINUTE, text=minute, form=KEYWORD}, boundary={canonical=START, text=start, form=KEYWORD}}}}, from={join={1={table={alias=a, table=events}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}}}",
				"[bucket]",
				"{}",
				"{accounts={id=[[@24,107:107='b',<419>,1:107]]}, events={account_id=[[@20,92:92='a',<419>,1:92]]}}",
				"{query0={bucket=[[@12,52:57='bucket',<419>,1:52]]}}",
				"{def_query0={query_dictionary={bucket=[[@12,52:57='bucket',<419>,1:52]]}, table_dictionary={accounts={id=[[@24,107:107='b',<419>,1:107]]}, events={account_id=[[@20,92:92='a',<419>,1:92]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}], interface={bucket=[{name=created_at, table_ref=null}]}, table_alias={a=events, b=accounts}}}",
				"null");
	}

	@Test
	public void malformedTimeSliceMissingDerivedOutputFatal() {
		SqlParserAccess parser = parseEndpoint("select time_slice(d.created_at, 15, minute, start) as bucket from (select e.created_at as created_out from events e) d", "SQL");
		Assert.assertEquals(parser.getAllDiagnostics().toString(), 2, parser.getAllDiagnostics().size());
		assertDiagnosticAtPosition(parser.getSnippet(), "STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
				ParseDiagnostic.Severity.WARNING,
				"snowflake_time_slice_expression at (l:1 c:7) is recognized as Snowflake-specific grammar.",
				null, 1, 7);
		assertFatalDiagnosticAtPositionWithFullMessage(parser.getSnippet(),
				"QUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIAS",
				"Qualified column 'created_at' at (l:1 c:18) was not found in output interface of query alias 'd'.",
				"d.created_at",
				1,
				18);
		assertOutputs(parser,
				"{SQL={select={1={alias=bucket, datetime_function={function_name=time_slice, value={column={name=created_at, table_ref=d}}, slice_length={literal=15}, level={canonical=MINUTE, text=minute, form=KEYWORD}, boundary={canonical=START, text=start, form=KEYWORD}}}}, from={table={alias=d, query={select={1={column={name=created_at, table_ref=e}, alias=created_out}}, from={table={alias=e, table=events}}}}}}}",
				"[bucket]",
				"{}",
				"{events={created_at=[[@18,74:74='e',<419>,1:74]]}}",
				"{query0={created_out=[[@22,90:100='created_out',<419>,1:90]]}, query1={bucket=[[@14,54:59='bucket',<419>,1:54]]}}",
				"{def_query1={query_dictionary={bucket=[[@14,54:59='bucket',<419>,1:54]]}, def_query0={query_dictionary={created_out=[[@22,90:100='created_out',<419>,1:90]]}, table_dictionary={events={created_at=[[@18,74:74='e',<419>,1:74]]}}, interface={created_out=[{name=created_at, table_ref=e}]}, table_alias={e=events}}, interface={bucket=[{name=created_at, table_ref=d}]}, table_alias={d=query0}}}",
				"null");
	}

	@Test
	public void regressionDmlInsertPlacementForTimeSlice() {
		SqlParserAccess parser = parseEndpoint("insert into archive_events (bucket) select time_slice(e.created_at, 15, minute, start) as bucket from events e", "SQL");
		Assert.assertEquals(parser.getAllDiagnostics().toString(), 1, parser.getAllDiagnostics().size());
		assertDiagnosticAtPosition(parser.getSnippet(), "STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
				ParseDiagnostic.Severity.WARNING,
				"snowflake_time_slice_expression at (l:1 c:43) is recognized as Snowflake-specific grammar.",
				null, 1, 43);
		assertOutputs(parser,
				"{SQL={insert={preamble=insert_into, from={from={table={alias=e, table=events}}, select={1={alias=bucket, datetime_function={function_name=time_slice, value={column={name=created_at, table_ref=e}}, slice_length={literal=15}, level={canonical=MINUTE, text=minute, form=KEYWORD}, boundary={canonical=START, text=start, form=KEYWORD}}}}}, target_table={table={alias=null, table=archive_events}}, columns={1={column={name=bucket, table_ref=null}}}}}}",
				"[bucket]",
				"{}",
				"{archive_events={bucket=[[@4,28:33='bucket',<419>,1:28]]}, events={created_at=[[@9,54:54='e',<419>,1:54]]}}",
				"{query0={bucket=[[@20,90:95='bucket',<419>,1:90]]}, insert1={bucket=[[@4,28:33='bucket',<419>,1:28]]}}",
				"{def_insert1={query_dictionary={bucket=[[@4,28:33='bucket',<419>,1:28]]}, table_dictionary={archive_events={bucket=[[@4,28:33='bucket',<419>,1:28]]}}, def_query0={query_dictionary={bucket=[[@20,90:95='bucket',<419>,1:90]]}, table_dictionary={events={created_at=[[@9,54:54='e',<419>,1:54]]}}, interface={bucket=[{name=created_at, table_ref=e}]}, table_alias={e=events}}, interface={bucket=[{name=bucket, table_ref=query0}]}}}",
				"null");
	}

	@Test
	public void regressionDmlUpdatePlacementForLastDay() {
		SqlParserAccess parser = parseEndpoint("update events e set bucket = last_day(e.created_at)", "SQL");
		Assert.assertEquals(parser.getAllDiagnostics().toString(), 1, parser.getAllDiagnostics().size());
		assertDiagnosticAtPosition(parser.getSnippet(), "STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
				ParseDiagnostic.Severity.WARNING,
				"snowflake_last_day_expression at (l:1 c:29) is recognized as Snowflake-specific grammar.",
				null, 1, 29);
		assertOutputs(parser,
				"{SQL={update={table={alias=e, table=events}, assignments={1={set={column={name=bucket, table_ref=null}}, to={datetime_function={function_name=last_day, value={column={name=created_at, table_ref=e}}}}}}}}}",
				"[bucket]",
				"{}",
				"{events={}}",
				"{update0={bucket=[[@4,20:25='bucket',<419>,1:20]]}}",
				"{def_update0={assignments={bucket=[{name=created_at, table_ref=e}]}, table_dictionary={events={}}, update_dictionary={bucket=[[@4,20:25='bucket',<419>,1:20]]}, target_table={events={bucket=[[@4,20:25='bucket',<419>,1:20]], created_at=[[@8,38:38='e',<419>,1:38]]}}, table_alias={e=events}, lhs_unresolved_columns={bucket={column={name=bucket, table_ref=null}, locations=[[@4,20:25='bucket',<419>,1:20]]}}}}",
				"null");
	}

	@Test
	public void regressionDmlDeletePlacementForNextDay() {
		SqlParserAccess parser = parseEndpoint("delete from events e where next_day(e.created_at, monday) > DATE '2026-01-01'", "SQL");
		Assert.assertEquals(parser.getAllDiagnostics().toString(), 1, parser.getAllDiagnostics().size());
		assertDiagnosticAtPosition(parser.getSnippet(), "STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
				ParseDiagnostic.Severity.WARNING,
				"snowflake_next_day_expression at (l:1 c:27) is recognized as Snowflake-specific grammar.",
				null, 1, 27);
		assertOutputs(parser,
				"{SQL={delete={table={alias=e, table=events}, where={condition={left={datetime_function={function_name=next_day, value={column={name=created_at, table_ref=e}}, weekday={canonical=MONDAY, text=monday, form=KEYWORD}}}, right={source_type=DATE, literal=2026-01-01}, operator=>}}}}}",
				"[]",
				"{}",
				"{events={created_at=[[@7,36:36='e',<419>,1:36]]}}",
				"{}",
				"{def_delete0={query_dictionary={}, table_dictionary={events={created_at=[[@7,36:36='e',<419>,1:36]]}}, filters=[{name=created_at, table_ref=e}], interface=null, table_alias={e=events}}}",
				"null");
	}

	@Test
	public void regressionSchemaQualifiedSameNameUdfStaysGenericForTimeSlice() {
		SqlParserAccess parser = parseEndpoint("select custom.time_slice(day_col, 15, minute, start) as udf_bucket from events a", "SQL");
		Assert.assertEquals(parser.getAllDiagnostics().toString(), 0, parser.getAllDiagnostics().size());
		assertOutputs(parser,
				"{SQL={select={1={function={schema=custom, parameters={1={column={name=day_col, table_ref=null}}, 2={literal=15}, 3={column={name=minute, table_ref=null}}, 4={column={name=start, table_ref=null}}}, function_name=time_slice}, alias=udf_bucket}}, from={table={alias=a, table=events}}}}",
				"[udf_bucket]",
				"{}",
				"{events={day_col=[[@5,25:31='day_col',<419>,1:25]], start=[[@11,46:50='start',<419>,1:46]], minute=[[@9,38:43='minute',<114>,1:38]]}}",
				"{query0={udf_bucket=[[@14,56:65='udf_bucket',<419>,1:56]]}}",
				"{def_query0={query_dictionary={udf_bucket=[[@14,56:65='udf_bucket',<419>,1:56]]}, table_dictionary={events={day_col=[[@5,25:31='day_col',<419>,1:25]], start=[[@11,46:50='start',<419>,1:46]], minute=[[@9,38:43='minute',<114>,1:38]]}}, interface={udf_bucket=[{name=day_col, table_ref=events}, {name=minute, table_ref=events}, {name=start, table_ref=events}]}, table_alias={a=events}}}",
				"null");
	}

	@Test
	public void regressionScriptResetAndAffinityIsolationForBoundaryFamily() {
		String script = "select time_slice(a.created_at, 15, minute, start) as bucket from events a;"
				+ " select previous_day(e.created_at, 'friday') as prev_day from events e";
		SqlParserAccess parser = parseEndpoint(script, "SCRIPT");
		Assert.assertEquals(parser.getAllDiagnostics().toString(), 2, parser.getAllDiagnostics().size());
		assertDiagnosticAtPosition(parser.getSnippet(), "STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
				ParseDiagnostic.Severity.WARNING,
				"snowflake_time_slice_expression at (l:1 c:7) is recognized as Snowflake-specific grammar.",
				null, 1, 7);
		assertDiagnosticAtPosition(parser.getSnippet(), "STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
				ParseDiagnostic.Severity.WARNING,
				"snowflake_previous_day_expression at (l:1 c:83) is recognized as Snowflake-specific grammar.",
				null, 1, 83);
		assertOutputs(parser,
				"{SCRIPT={1={select={1={alias=bucket, datetime_function={function_name=time_slice, value={column={name=created_at, table_ref=a}}, slice_length={literal=15}, level={canonical=MINUTE, text=minute, form=KEYWORD}, boundary={canonical=START, text=start, form=KEYWORD}}}}, from={table={alias=a, table=events}}}, 2={select={1={alias=prev_day, datetime_function={function_name=previous_day, value={column={name=created_at, table_ref=e}}, weekday={canonical=FRIDAY, text=friday, form=STRING}}}}, from={table={alias=e, table=events}}}}}",
				"[]",
				"{SCRIPT={1={}, 2={}}}",
				"{SCRIPT={1={events={created_at=[[@3,18:18='a',<419>,1:18]]}}, 2={events={created_at=[[@22,96:96='e',<419>,1:96]]}}}}",
				"{SCRIPT={1={query0={bucket=[[@14,54:59='bucket',<419>,1:54]]}}, 2={query0={prev_day=[[@29,123:130='prev_day',<419>,1:123]]}}}}",
				"{SCRIPT={1={def_query0={query_dictionary={bucket=[[@14,54:59='bucket',<419>,1:54]]}, table_dictionary={events={created_at=[[@3,18:18='a',<419>,1:18]]}}, interface={bucket=[{name=created_at, table_ref=a}]}, table_alias={a=events}}}, 2={def_query0={query_dictionary={prev_day=[[@29,123:130='prev_day',<419>,1:123]]}, table_dictionary={events={created_at=[[@22,96:96='e',<419>,1:96]]}}, interface={prev_day=[{name=created_at, table_ref=e}]}, table_alias={e=events}}}}}",
				"{SCRIPT={1={queryInterface=[bucket], Walker Warnings=[snowflake_time_slice_expression at (l:1 c:7) is recognized as Snowflake-specific grammar., snowflake_previous_day_expression at (l:1 c:83) is recognized as Snowflake-specific grammar.]}, 2={queryInterface=[prev_day], Walker Warnings=[snowflake_time_slice_expression at (l:1 c:7) is recognized as Snowflake-specific grammar., snowflake_previous_day_expression at (l:1 c:83) is recognized as Snowflake-specific grammar.]}}}");
	}

	@Test
	public void regressionMixedAffinityFatalForBoundaryFamily() {
		SqlParserAccess parser = parseEndpoint("select time_slice(created_at, 15, minute, start), date_part(dow from order_date) from orders", "SQL");
		Assert.assertEquals(parser.getAllDiagnostics().toString(), 2, parser.getAllDiagnostics().size());
		assertDiagnosticAtPosition(parser.getSnippet(), "STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
				ParseDiagnostic.Severity.WARNING,
				"snowflake_time_slice_expression at (l:1 c:7) is recognized as Snowflake-specific grammar.",
				null, 1, 7);
		assertFatalDiagnosticAtPosition(parser.getSnippet(),
				"STATEMENT_MIXED_DIALECT_GRAMMAR",
				"Statement mixes Snowflake-only and PostgreSQL-only grammar at (l:1 c:50); unlikely to run on either engine.",
				null,
				1,
				50);
		assertOutputs(parser,
				"{SQL={select={1={datetime_function={function_name=time_slice, value={column={name=created_at, table_ref=null}}, slice_length={literal=15}, level={canonical=MINUTE, text=minute, form=KEYWORD}, boundary={canonical=START, text=start, form=KEYWORD}}}, 2={datetime_function={function_name=date_part, part_form=KEYWORD, part=dow, source={column={name=order_date, table_ref=null}}}}}, from={table={alias=null, table=orders}}}}",
				"[unnamed_1, unnamed_0]",
				"{}",
				"{orders={order_date=[[@16,69:78='order_date',<419>,1:69]], created_at=[[@3,18:27='created_at',<419>,1:18]]}}",
				"{query0={unnamed_1=[[@17,79:79=')',<288>,1:79]], unnamed_0=[[@10,47:47=')',<288>,1:47]]}}",
				"{def_query0={query_dictionary={unnamed_1=[[@17,79:79=')',<288>,1:79]], unnamed_0=[[@10,47:47=')',<288>,1:47]]}, table_dictionary={orders={order_date=[[@16,69:78='order_date',<419>,1:69]], created_at=[[@3,18:27='created_at',<419>,1:18]]}}, interface={unnamed_1=[{name=order_date, table_ref=orders}], unnamed_0=[{name=created_at, table_ref=orders}]}}}",
				"null");
	}
}
