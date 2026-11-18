package sql.walker;

import org.junit.Assert;
import org.junit.Test;

import access.Snippet;
import access.SqlParserAccess;
import errorhandling.ParseDiagnostic;

public class SqlEventWalkerDatetimeBoundarySliceTests extends AbstractSqlParseEventWalkerTest {

	private String lastSql;
	private String lastEndpoint;

	private SqlParserAccess parseEndpoint(String sql, String endpoint) {
		SqlParserAccess parser = new SqlParserAccess(false, false, false);
		parser.executeTheParse(sql, endpoint);
		Assert.assertEquals(sql, 0, parser.getParser().getNumberOfSyntaxErrors());
		lastSql = sql;
		lastEndpoint = endpoint;
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
		Assert.assertNotNull("Expected parse SQL to be captured before assertions", lastSql);
		Assert.assertNotNull("Expected parse endpoint to be captured before assertions", lastEndpoint);
		Assert.assertEquals("AST is wrong", ast, snippet.getSqlAbstractTree().toString());
		Assert.assertEquals("Interface is wrong", outputInterface, snippet.getQueryInterface().toString());
		Assert.assertEquals("Substitution List is wrong", substitutions, snippet.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", tableDictionary, snippet.getTableDictionary().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", queryDictionary, snippet.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", symbolTable, snippet.getSymbolTable().toString());
		Assert.assertEquals("Array Output Collectors are wrong", arrayOutputCollectors, String.valueOf(snippet.getArrayOutputCollectorsMap()));
		assertExtractorOutputs(ast, outputInterface, substitutions, tableDictionary, queryDictionary, symbolTable, arrayOutputCollectors);
	}

	private void assertExtractorOutputs(
			String ast,
			String outputInterface,
			String substitutions,
			String tableDictionary,
			String queryDictionary,
			String symbolTable,
			String arrayOutputCollectors) {
		SqlParseEventWalker extractor;
		if ("SQL".equals(lastEndpoint)) {
			extractor = runParsertest(lastSql, parse(lastSql));
		} else if ("SCRIPT".equals(lastEndpoint)) {
			extractor = runScriptParsertest(lastSql, parse(lastSql));
		} else {
			Assert.fail("Unsupported endpoint for extractor assertions: " + lastEndpoint);
			return;
		}

		Assert.assertEquals("Extractor AST is wrong", ast, extractor.getAsTree().toString());
		Assert.assertEquals("Extractor interface is wrong", outputInterface, extractor.getInterface().toString());
		Assert.assertEquals("Extractor substitution list is wrong", substitutions, extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Extractor table dictionary is wrong", tableDictionary, extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Extractor query column dictionary is wrong", queryDictionary, extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Extractor symbol table is wrong", symbolTable, extractor.getSymbolTable().toString());
		if (!"SCRIPT".equals(lastEndpoint)) {
			Assert.assertEquals("Extractor array output collectors are wrong", arrayOutputCollectors,
					String.valueOf(extractor.getSnippet().getArrayOutputCollectorsMap()));
		}
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
				"{events={created_at=[[@10,43:43='a',<452>,1:43]], id=[[@1,7:7='a',<452>,1:7]]}}",
				"{query0={id=[[@3,9:10='id',<452>,1:9]]}}",
				"{def_query0={query_dictionary={id=[[@3,9:10='id',<452>,1:9]]}, table_dictionary={events={created_at=[[@10,43:43='a',<452>,1:43]], id=[[@1,7:7='a',<452>,1:7]]}}, filters=[{name=created_at, table_ref=a}], interface={id=[{name=id, table_ref=a}]}, table_alias={a=events}}}",
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
				"{events={account_id=[[@1,7:7='a',<452>,1:7], [@16,62:62='a',<452>,1:62]], created_at=[[@24,95:95='a',<452>,1:95]], *=[[@7,27:27='*',<291>,1:27]]}}",
				"{query0={total=[[@10,33:37='total',<452>,1:33]], account_id=[[@3,9:18='account_id',<452>,1:9]]}}",
				"{def_query0={query_dictionary={total=[[@10,33:37='total',<452>,1:33]], account_id=[[@3,9:18='account_id',<452>,1:9]]}, table_dictionary={events={account_id=[[@1,7:7='a',<452>,1:7], [@16,62:62='a',<452>,1:62]], created_at=[[@24,95:95='a',<452>,1:95]], *=[[@7,27:27='*',<291>,1:27]]}}, grouped_by=[{name=account_id, table_ref=a}], filters=[{name=created_at, table_ref=a}], interface={total=[], account_id=[{name=account_id, table_ref=a}]}, table_alias={a=events}}}",
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
				"{events={created_at=[[@3,16:16='a',<452>,1:16]]}}",
				"{query0={next_mon=[[@10,41:48='next_mon',<452>,1:41], [@16,73:80='next_mon',<452>,1:73]]}}",
				"{def_query0={query_dictionary={next_mon=[[@10,41:48='next_mon',<452>,1:41], [@16,73:80='next_mon',<452>,1:73]]}, table_dictionary={events={created_at=[[@3,16:16='a',<452>,1:16]]}}, ordered_by=[{name=next_mon, table_ref=query0}], interface={next_mon=[{name=created_at, table_ref=a}]}, table_alias={a=events}}}",
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
				"{events={created_at=[[@17,79:79='a',<452>,1:79]], id=[[@1,7:7='a',<452>,1:7], [@25,112:112='a',<452>,1:112]]}}",
				"{query0={id=[[@3,9:10='id',<452>,1:9]]}}",
				"{def_query0={window_ordered_by=[{name=id, table_ref=a}], query_dictionary={id=[[@3,9:10='id',<452>,1:9]]}, table_dictionary={events={created_at=[[@17,79:79='a',<452>,1:79]], id=[[@1,7:7='a',<452>,1:7], [@25,112:112='a',<452>,1:112]]}}, window_partition_by=[{name=created_at, table_ref=a}], filters=[{name=created_at, table_ref=a}, {name=id, table_ref=a}], interface={id=[{name=id, table_ref=a}]}, table_alias={a=events}}}",
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
				"{events={created_at=[[@7,32:32='e',<452>,1:32]]}}",
				"{query0={bucket=[[@18,68:73='bucket',<452>,1:68], [@30,119:119='b',<452>,1:119]]}, query1={bucket=[[@32,121:126='bucket',<452>,1:121], [@24,97:97='d',<452>,1:97]]}, query2={bucket=[[@26,99:104='bucket',<452>,1:99]]}}",
				"{def_query2={context_list={base=query0}, query_dictionary={bucket=[[@26,99:104='bucket',<452>,1:99]]}, def_query1={context_list={base=query0, b=query0}, query_dictionary={bucket=[[@32,121:126='bucket',<452>,1:121], [@24,97:97='d',<452>,1:97]]}, interface={bucket=[{name=bucket, table_ref=b}]}, table_alias={b=query0, base=query0}}, def_query0={query_dictionary={bucket=[[@18,68:73='bucket',<452>,1:68], [@30,119:119='b',<452>,1:119]]}, table_dictionary={events={created_at=[[@7,32:32='e',<452>,1:32]]}}, interface={bucket=[{name=created_at, table_ref=e}]}, table_alias={e=events}}, interface={bucket=[{name=bucket, table_ref=d}]}, table_alias={d=query1, base=query0}}}",
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
				"{events={created_at=[[@3,18:18='a',<452>,1:18]]}}",
				"{query0={bucket=[[@14,53:58='bucket',<452>,1:53]]}}",
				"{def_query0={query_dictionary={bucket=[[@14,53:58='bucket',<452>,1:53]]}, table_dictionary={events={created_at=[[@3,18:18='a',<452>,1:18]]}}, interface={bucket=[{name=created_at, table_ref=a}]}, table_alias={a=events}}}",
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
				"{events={created_at=[[@3,18:18='a',<452>,1:18]]}}",
				"{query0={bucket=[[@14,55:60='bucket',<452>,1:55]]}}",
				"{def_query0={query_dictionary={bucket=[[@14,55:60='bucket',<452>,1:55]]}, table_dictionary={events={created_at=[[@3,18:18='a',<452>,1:18]]}}, interface={bucket=[{name=created_at, table_ref=a}]}, table_alias={a=events}}}",
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
				"{events={created_at=[[@3,16:16='a',<452>,1:16]]}}",
				"{query0={month_end=[[@10,40:48='month_end',<452>,1:40]]}}",
				"{def_query0={query_dictionary={month_end=[[@10,40:48='month_end',<452>,1:40]]}, table_dictionary={events={created_at=[[@3,16:16='a',<452>,1:16]]}}, interface={month_end=[{name=created_at, table_ref=a}]}, table_alias={a=events}}}",
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
				"{events={created_at=[[@3,16:16='a',<452>,1:16]]}}",
				"{query0={next_mon=[[@10,42:49='next_mon',<452>,1:42]]}}",
				"{def_query0={query_dictionary={next_mon=[[@10,42:49='next_mon',<452>,1:42]]}, table_dictionary={events={created_at=[[@3,16:16='a',<452>,1:16]]}}, interface={next_mon=[{name=created_at, table_ref=a}]}, table_alias={a=events}}}",
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
				"{events={created_at=[[@3,20:20='a',<452>,1:20]]}}",
				"{query0={prev_day=[[@10,47:54='prev_day',<452>,1:47]]}}",
				"{def_query0={query_dictionary={prev_day=[[@10,47:54='prev_day',<452>,1:47]]}, table_dictionary={events={created_at=[[@3,20:20='a',<452>,1:20]]}}, interface={prev_day=[{name=created_at, table_ref=a}]}, table_alias={a=events}}}",
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
				"{accounts={id=[[@24,107:107='b',<452>,1:107]]}, events={account_id=[[@20,92:92='a',<452>,1:92]]}}",
				"{query0={bucket=[[@12,52:57='bucket',<452>,1:52]]}}",
				"{def_query0={query_dictionary={bucket=[[@12,52:57='bucket',<452>,1:52]]}, table_dictionary={accounts={id=[[@24,107:107='b',<452>,1:107]]}, events={account_id=[[@20,92:92='a',<452>,1:92]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}], interface={bucket=[{name=created_at, table_ref=null}]}, table_alias={a=events, b=accounts}}}",
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
				"{events={created_at=[[@18,74:74='e',<452>,1:74]]}}",
				"{query0={created_out=[[@22,90:100='created_out',<452>,1:90]]}, query1={bucket=[[@14,54:59='bucket',<452>,1:54]]}}",
				"{def_query1={query_dictionary={bucket=[[@14,54:59='bucket',<452>,1:54]]}, def_query0={query_dictionary={created_out=[[@22,90:100='created_out',<452>,1:90]]}, table_dictionary={events={created_at=[[@18,74:74='e',<452>,1:74]]}}, interface={created_out=[{name=created_at, table_ref=e}]}, table_alias={e=events}}, interface={bucket=[{name=created_at, table_ref=d}]}, table_alias={d=query0}}}",
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
				"{archive_events={bucket=[[@4,28:33='bucket',<452>,1:28]]}, events={created_at=[[@9,54:54='e',<452>,1:54]]}}",
				"{query0={bucket=[[@20,90:95='bucket',<452>,1:90]]}, insert1={bucket=[[@4,28:33='bucket',<452>,1:28]]}}",
				"{def_insert1={query_dictionary={bucket=[[@4,28:33='bucket',<452>,1:28]]}, table_dictionary={archive_events={bucket=[[@4,28:33='bucket',<452>,1:28]]}}, def_query0={query_dictionary={bucket=[[@20,90:95='bucket',<452>,1:90]]}, table_dictionary={events={created_at=[[@9,54:54='e',<452>,1:54]]}}, interface={bucket=[{name=created_at, table_ref=e}]}, table_alias={e=events}}, interface={bucket=[{name=bucket, table_ref=query0}]}}}",
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
				"{update0={bucket=[[@4,20:25='bucket',<452>,1:20]]}}",
				"{def_update0={assignments={bucket=[{name=created_at, table_ref=e}]}, table_dictionary={events={}}, update_dictionary={bucket=[[@4,20:25='bucket',<452>,1:20]]}, target_table={events={bucket=[[@4,20:25='bucket',<452>,1:20]], created_at=[[@8,38:38='e',<452>,1:38]]}}, table_alias={e=events}, lhs_unresolved_columns={bucket={column={name=bucket, table_ref=null}, locations=[[@4,20:25='bucket',<452>,1:20]]}}}}",
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
				"{events={created_at=[[@7,36:36='e',<452>,1:36]]}}",
				"{}",
				"{def_delete0={query_dictionary={}, table_dictionary={events={created_at=[[@7,36:36='e',<452>,1:36]]}}, filters=[{name=created_at, table_ref=e}], interface=null, table_alias={e=events}}}",
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
				"{events={day_col=[[@5,25:31='day_col',<452>,1:25]], start=[[@11,46:50='start',<452>,1:46]], minute=[[@9,38:43='minute',<114>,1:38]]}}",
				"{query0={udf_bucket=[[@14,56:65='udf_bucket',<452>,1:56]]}}",
				"{def_query0={query_dictionary={udf_bucket=[[@14,56:65='udf_bucket',<452>,1:56]]}, table_dictionary={events={day_col=[[@5,25:31='day_col',<452>,1:25]], start=[[@11,46:50='start',<452>,1:46]], minute=[[@9,38:43='minute',<114>,1:38]]}}, interface={udf_bucket=[{name=day_col, table_ref=events}, {name=minute, table_ref=events}, {name=start, table_ref=events}]}, table_alias={a=events}}}",
				"null");
	}

	@Test
	public void regressionNestedTimeSliceInsideLastDayCollectsOnlyValueAndLengthLineage() {
		SqlParserAccess parser = parseEndpoint(
				"select last_day(time_slice(a.created_at, a.slice_len, day), month) as month_bucket from events a",
				"SQL");
		Assert.assertEquals(parser.getAllDiagnostics().toString(), 1, parser.getAllDiagnostics().size());
		assertDiagnosticAtPosition(parser.getSnippet(), "STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
				ParseDiagnostic.Severity.WARNING,
				"snowflake_time_slice_expression at (l:1 c:16) is recognized as Snowflake-specific grammar.",
				null, 1, 16);
		assertOutputs(parser,
				"{SQL={select={1={alias=month_bucket, datetime_function={function_name=last_day, value={datetime_function={function_name=time_slice, value={column={name=created_at, table_ref=a}}, slice_length={column={name=slice_len, table_ref=a}}, level={canonical=DAY, text=day, form=KEYWORD}}}, level={canonical=MONTH, text=month, form=KEYWORD}}}}, from={table={alias=a, table=events}}}}",
				"[month_bucket]",
				"{}",
				"{events={created_at=[[@5,27:27='a',<452>,1:27]], slice_len=[[@9,41:41='a',<452>,1:41]]}}",
				"{query0={month_bucket=[[@19,70:81='month_bucket',<452>,1:70]]}}",
				"{def_query0={query_dictionary={month_bucket=[[@19,70:81='month_bucket',<452>,1:70]]}, table_dictionary={events={created_at=[[@5,27:27='a',<452>,1:27]], slice_len=[[@9,41:41='a',<452>,1:41]]}}, interface={month_bucket=[{name=created_at, table_ref=a}, {name=slice_len, table_ref=a}]}, table_alias={a=events}}}",
				"null");
	}

	@Test
	public void regressionNestedNextDayAroundTimeSlicePreservesNestedLineageIsolation() {
		SqlParserAccess parser = parseEndpoint(
				"select next_day(time_slice(a.created_at, a.slice_len, day), friday) as next_bucket from events a",
				"SQL");
		Assert.assertEquals(parser.getAllDiagnostics().toString(), 1, parser.getAllDiagnostics().size());
		assertDiagnosticAtPosition(parser.getSnippet(), "STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
				ParseDiagnostic.Severity.WARNING,
				"snowflake_time_slice_expression at (l:1 c:16) is recognized as Snowflake-specific grammar.",
				null, 1, 16);
		assertOutputs(parser,
				"{SQL={select={1={alias=next_bucket, datetime_function={function_name=next_day, value={datetime_function={function_name=time_slice, value={column={name=created_at, table_ref=a}}, slice_length={column={name=slice_len, table_ref=a}}, level={canonical=DAY, text=day, form=KEYWORD}}}, weekday={canonical=FRIDAY, text=friday, form=KEYWORD}}}}, from={table={alias=a, table=events}}}}",
				"[next_bucket]",
				"{}",
				"{events={created_at=[[@5,27:27='a',<452>,1:27]], slice_len=[[@9,41:41='a',<452>,1:41]]}}",
				"{query0={next_bucket=[[@19,71:81='next_bucket',<452>,1:71]]}}",
				"{def_query0={query_dictionary={next_bucket=[[@19,71:81='next_bucket',<452>,1:71]]}, table_dictionary={events={created_at=[[@5,27:27='a',<452>,1:27]], slice_len=[[@9,41:41='a',<452>,1:41]]}}, interface={next_bucket=[{name=created_at, table_ref=a}, {name=slice_len, table_ref=a}]}, table_alias={a=events}}}",
				"null");
	}

	@Test
	public void regressionSiblingBoundaryFunctionsShareScopeWithoutPartSlotLeakage() {
		SqlParserAccess parser = parseEndpoint(
				"select time_slice(a.created_at, a.slice_len, minute, start) as sliced, next_day(a.created_at, friday) as next_bucket, previous_day(a.created_at, 'monday') as prev_bucket from events a",
				"SQL");
		Assert.assertEquals(parser.getAllDiagnostics().toString(), 1, parser.getAllDiagnostics().size());
		assertDiagnosticAtPosition(parser.getSnippet(), "STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
				ParseDiagnostic.Severity.WARNING,
				"snowflake_time_slice_expression at (l:1 c:7) is recognized as Snowflake-specific grammar.",
				null, 1, 7);
		assertOutputs(parser,
				"{SQL={select={1={alias=sliced, datetime_function={function_name=time_slice, value={column={name=created_at, table_ref=a}}, slice_length={column={name=slice_len, table_ref=a}}, level={canonical=MINUTE, text=minute, form=KEYWORD}, boundary={canonical=START, text=start, form=KEYWORD}}}, 2={alias=next_bucket, datetime_function={function_name=next_day, value={column={name=created_at, table_ref=a}}, weekday={canonical=FRIDAY, text=friday, form=KEYWORD}}}, 3={alias=prev_bucket, datetime_function={function_name=previous_day, value={column={name=created_at, table_ref=a}}, weekday={canonical=MONDAY, text=monday, form=STRING}}}}, from={table={alias=a, table=events}}}}",
				"[prev_bucket, sliced, next_bucket]",
				"{}",
				"{events={created_at=[[@3,18:18='a',<452>,1:18], [@20,80:80='a',<452>,1:80], [@31,131:131='a',<452>,1:131]], slice_len=[[@7,32:32='a',<452>,1:32]]}}",
				"{query0={prev_bucket=[[@38,158:168='prev_bucket',<452>,1:158]], sliced=[[@16,63:68='sliced',<452>,1:63]], next_bucket=[[@27,105:115='next_bucket',<452>,1:105]]}}",
				"{def_query0={query_dictionary={prev_bucket=[[@38,158:168='prev_bucket',<452>,1:158]], sliced=[[@16,63:68='sliced',<452>,1:63]], next_bucket=[[@27,105:115='next_bucket',<452>,1:105]]}, table_dictionary={events={created_at=[[@3,18:18='a',<452>,1:18], [@20,80:80='a',<452>,1:80], [@31,131:131='a',<452>,1:131]], slice_len=[[@7,32:32='a',<452>,1:32]]}}, interface={prev_bucket=[{name=created_at, table_ref=a}], sliced=[{name=created_at, table_ref=a}, {name=slice_len, table_ref=a}], next_bucket=[{name=created_at, table_ref=a}]}, table_alias={a=events}}}",
				"null");
	}

	@Test
	public void regressionSiblingValidSubstitutionSlotsCollectWithoutKeywordLeakage() {
		SqlParserAccess parser = parseEndpoint(
				"select time_slice(a.created_at, <slice length>, day) as sliced, next_day(<next value>, friday) as next_bucket from events a",
				"SQL");
		Assert.assertEquals(parser.getAllDiagnostics().toString(), 1, parser.getAllDiagnostics().size());
		assertDiagnosticAtPosition(parser.getSnippet(), "STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
				ParseDiagnostic.Severity.WARNING,
				"snowflake_time_slice_expression at (l:1 c:7) is recognized as Snowflake-specific grammar.",
				null, 1, 7);
		assertOutputs(parser,
				"{SQL={select={1={alias=sliced, datetime_function={function_name=time_slice, value={column={name=created_at, table_ref=a}}, slice_length={substitution={name=<slice length>, type=predicand}}, level={canonical=DAY, text=day, form=KEYWORD}}}, 2={alias=next_bucket, datetime_function={function_name=next_day, value={substitution={name=<next value>, type=predicand}}, weekday={canonical=FRIDAY, text=friday, form=KEYWORD}}}}, from={table={alias=a, table=events}}}}",
				"[sliced, next_bucket]",
				"{<next value>=predicand, <slice length>=predicand}",
				"{events={created_at=[[@3,18:18='a',<452>,1:18]]}}",
				"{query0={sliced=[[@12,56:61='sliced',<452>,1:56]], next_bucket=[[@21,98:108='next_bucket',<452>,1:98]]}}",
				"{def_query0={query_dictionary={sliced=[[@12,56:61='sliced',<452>,1:56]], next_bucket=[[@21,98:108='next_bucket',<452>,1:98]]}, table_dictionary={events={created_at=[[@3,18:18='a',<452>,1:18]]}}, interface={sliced=[{name=created_at, table_ref=a}, {name=<slice length>, type=predicand}], next_bucket=[{name=<next value>, type=predicand}]}, table_alias={a=events}}}",
				"null");
	}

	@Test
	public void regressionSetOpUnionAllTimeSliceBranchIsolation() {
		SqlParserAccess parser = parseEndpoint(
				"select time_slice(a.created_at, a.slice_len, day) as bucket, a.id from events a union all select time_slice(b.created_at, b.slice_len, day) as bucket, b.id from events_archive b",
				"SQL");
		Assert.assertEquals(parser.getAllDiagnostics().toString(), 1, parser.getAllDiagnostics().size());
		assertDiagnosticAtPosition(parser.getSnippet(), "STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
				ParseDiagnostic.Severity.WARNING,
				"snowflake_time_slice_expression at (l:1 c:7) is recognized as Snowflake-specific grammar.",
				null, 1, 7);
		assertOutputs(parser,
				"{SQL={union={1={select={1={alias=bucket, datetime_function={function_name=time_slice, value={column={name=created_at, table_ref=a}}, slice_length={column={name=slice_len, table_ref=a}}, level={canonical=DAY, text=day, form=KEYWORD}}}, 2={column={name=id, table_ref=a}}}, from={table={alias=a, table=events}}}, 2={union={qualifier=all, operator=union}}, 3={select={1={alias=bucket, datetime_function={function_name=time_slice, value={column={name=created_at, table_ref=b}}, slice_length={column={name=slice_len, table_ref=b}}, level={canonical=DAY, text=day, form=KEYWORD}}}, 2={column={name=id, table_ref=b}}}, from={table={alias=b, table=events_archive}}}}}}",
				"[bucket, id]",
				"{}",
				"{events_archive={created_at=[[@27,108:108='b',<452>,1:108]], id=[[@40,151:151='b',<452>,1:151]], slice_len=[[@31,122:122='b',<452>,1:122]]}, events={created_at=[[@3,18:18='a',<452>,1:18]], id=[[@16,61:61='a',<452>,1:61]], slice_len=[[@7,32:32='a',<452>,1:32]]}}",
				"{query0={bucket=[[@14,53:58='bucket',<452>,1:53]], id=[[@18,63:64='id',<452>,1:63]]}, query1={bucket=[[@38,143:148='bucket',<452>,1:143]], id=[[@42,153:154='id',<452>,1:153]]}}",
				"{def_union2={def_query1={query_dictionary={bucket=[[@38,143:148='bucket',<452>,1:143]], id=[[@42,153:154='id',<452>,1:153]]}, table_dictionary={events_archive={created_at=[[@27,108:108='b',<452>,1:108]], id=[[@40,151:151='b',<452>,1:151]], slice_len=[[@31,122:122='b',<452>,1:122]]}}, setop=UNION, interface={bucket=[{name=created_at, table_ref=b}, {name=slice_len, table_ref=b}], id=[{name=id, table_ref=b}]}, table_alias={b=events_archive}}, def_query0={query_dictionary={bucket=[[@14,53:58='bucket',<452>,1:53]], id=[[@18,63:64='id',<452>,1:63]]}, table_dictionary={events={created_at=[[@3,18:18='a',<452>,1:18]], id=[[@16,61:61='a',<452>,1:61]], slice_len=[[@7,32:32='a',<452>,1:32]]}}, interface={bucket=[{name=created_at, table_ref=a}, {name=slice_len, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}, interface={bucket=query_column, id=query_column}}}",
				"null");
	}

	@Test
	public void regressionSetOpIntersectLastDayBranchIsolation() {
		SqlParserAccess parser = parseEndpoint(
				"select last_day(a.created_at, month) as month_end, a.id from events a intersect select last_day(b.created_at, month) as month_end, b.id from events_archive b",
				"SQL");
		Assert.assertEquals(parser.getAllDiagnostics().toString(), 1, parser.getAllDiagnostics().size());
		assertDiagnosticAtPosition(parser.getSnippet(), "STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
				ParseDiagnostic.Severity.WARNING,
				"snowflake_last_day_expression at (l:1 c:7) is recognized as Snowflake-specific grammar.",
				null, 1, 7);
		assertOutputs(parser,
				"{SQL={intersect={1={select={1={alias=month_end, datetime_function={function_name=last_day, value={column={name=created_at, table_ref=a}}, level={canonical=MONTH, text=month, form=KEYWORD}}}, 2={column={name=id, table_ref=a}}}, from={table={alias=a, table=events}}}, 2={intersect={qualifier=null, operator=intersect}}, 3={select={1={alias=month_end, datetime_function={function_name=last_day, value={column={name=created_at, table_ref=b}}, level={canonical=MONTH, text=month, form=KEYWORD}}}, 2={column={name=id, table_ref=b}}}, from={table={alias=b, table=events_archive}}}}}}",
				"[month_end, id]",
				"{}",
				"{events_archive={created_at=[[@22,96:96='b',<452>,1:96]], id=[[@31,131:131='b',<452>,1:131]]}, events={created_at=[[@3,16:16='a',<452>,1:16]], id=[[@12,51:51='a',<452>,1:51]]}}",
				"{query0={month_end=[[@10,40:48='month_end',<452>,1:40]], id=[[@14,53:54='id',<452>,1:53]]}, query1={month_end=[[@29,120:128='month_end',<452>,1:120]], id=[[@33,133:134='id',<452>,1:133]]}}",
				"{def_intersect2={def_query1={query_dictionary={month_end=[[@29,120:128='month_end',<452>,1:120]], id=[[@33,133:134='id',<452>,1:133]]}, table_dictionary={events_archive={created_at=[[@22,96:96='b',<452>,1:96]], id=[[@31,131:131='b',<452>,1:131]]}}, setop=INTERSECTION, interface={month_end=[{name=created_at, table_ref=b}], id=[{name=id, table_ref=b}]}, table_alias={b=events_archive}}, def_query0={query_dictionary={month_end=[[@10,40:48='month_end',<452>,1:40]], id=[[@14,53:54='id',<452>,1:53]]}, table_dictionary={events={created_at=[[@3,16:16='a',<452>,1:16]], id=[[@12,51:51='a',<452>,1:51]]}}, interface={month_end=[{name=created_at, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}, interface={month_end=query_column, id=query_column}}}",
				"null");
	}

	@Test
	public void regressionSetOpExceptNextPreviousDayBranchIsolation() {
		SqlParserAccess parser = parseEndpoint(
				"select next_day(a.created_at, friday) as next_run, a.id from events a except select previous_day(b.created_at, 'friday') as next_run, b.id from events_archive b",
				"SQL");
		Assert.assertEquals(parser.getAllDiagnostics().toString(), 1, parser.getAllDiagnostics().size());
		assertDiagnosticAtPosition(parser.getSnippet(), "STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
				ParseDiagnostic.Severity.WARNING,
				"snowflake_next_day_expression at (l:1 c:7) is recognized as Snowflake-specific grammar.",
				null, 1, 7);
		assertOutputs(parser,
				"{SQL={union={1={select={1={alias=next_run, datetime_function={function_name=next_day, value={column={name=created_at, table_ref=a}}, weekday={canonical=FRIDAY, text=friday, form=KEYWORD}}}, 2={column={name=id, table_ref=a}}}, from={table={alias=a, table=events}}}, 2={union={qualifier=null, operator=except}}, 3={select={1={alias=next_run, datetime_function={function_name=previous_day, value={column={name=created_at, table_ref=b}}, weekday={canonical=FRIDAY, text=friday, form=STRING}}}, 2={column={name=id, table_ref=b}}}, from={table={alias=b, table=events_archive}}}}}}",
				"[next_run, id]",
				"{}",
				"{events_archive={created_at=[[@22,97:97='b',<452>,1:97]], id=[[@31,134:134='b',<452>,1:134]]}, events={created_at=[[@3,16:16='a',<452>,1:16]], id=[[@12,51:51='a',<452>,1:51]]}}",
				"{query0={next_run=[[@10,41:48='next_run',<452>,1:41]], id=[[@14,53:54='id',<452>,1:53]]}, query1={next_run=[[@29,124:131='next_run',<452>,1:124]], id=[[@33,136:137='id',<452>,1:136]]}}",
				"{def_union2={def_query1={query_dictionary={next_run=[[@29,124:131='next_run',<452>,1:124]], id=[[@33,136:137='id',<452>,1:136]]}, table_dictionary={events_archive={created_at=[[@22,97:97='b',<452>,1:97]], id=[[@31,134:134='b',<452>,1:134]]}}, setop=EXCEPT, interface={next_run=[{name=created_at, table_ref=b}], id=[{name=id, table_ref=b}]}, table_alias={b=events_archive}}, def_query0={query_dictionary={next_run=[[@10,41:48='next_run',<452>,1:41]], id=[[@14,53:54='id',<452>,1:53]]}, table_dictionary={events={created_at=[[@3,16:16='a',<452>,1:16]], id=[[@12,51:51='a',<452>,1:51]]}}, interface={next_run=[{name=created_at, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}, interface={next_run=query_column, id=query_column}}}",
				"null");
	}

	@Test
	public void regressionMultiLayerRenameChainForTimeSlice() {
		SqlParserAccess parser = parseEndpoint(
				"with q0 as (select time_slice(e.created_at, e.slice_len, day) as s1, e.id from events e), q1 as (select q.s1 as s2, q.id from q0 q), q2 as (select r.s2 as bucket, r.id from q1 r) select t.bucket, t.id from q2 t",
				"SQL");
		Assert.assertEquals(parser.getAllDiagnostics().toString(), 1, parser.getAllDiagnostics().size());
		assertDiagnosticAtPosition(parser.getSnippet(), "STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
				ParseDiagnostic.Severity.WARNING,
				"snowflake_time_slice_expression at (l:1 c:19) is recognized as Snowflake-specific grammar.",
				null, 1, 19);
		assertOutputs(parser,
				"{SQL={with={1={cte={select={1={alias=s1, datetime_function={function_name=time_slice, value={column={name=created_at, table_ref=e}}, slice_length={column={name=slice_len, table_ref=e}}, level={canonical=DAY, text=day, form=KEYWORD}}}, 2={column={name=id, table_ref=e}}}, from={table={alias=e, table=events}}}, alias=q0}, 2={cte={select={1={column={name=s1, table_ref=q}, alias=s2}, 2={column={name=id, table_ref=q}}}, from={table={alias=q, table=q0}}}, alias=q1}, 3={cte={select={1={column={name=s2, table_ref=r}, alias=bucket}, 2={column={name=id, table_ref=r}}}, from={table={alias=r, table=q1}}}, alias=q2}}, query={select={1={column={name=bucket, table_ref=t}}, 2={column={name=id, table_ref=t}}}, from={table={alias=t, table=q2}}}}}",
				"[bucket, id]",
				"{}",
				"{events={created_at=[[@7,30:30='e',<452>,1:30]], id=[[@20,69:69='e',<452>,1:69]], slice_len=[[@11,44:44='e',<452>,1:44]]}}",
				"{query0={s1=[[@18,65:66='s1',<452>,1:65], [@32,104:104='q',<452>,1:104]], id=[[@22,71:72='id',<452>,1:71], [@38,116:116='q',<452>,1:116]]}, query1={id=[[@40,118:119='id',<452>,1:118], [@56,163:163='r',<452>,1:163]], s2=[[@36,112:113='s2',<452>,1:112], [@50,147:147='r',<452>,1:147]]}, query2={bucket=[[@54,155:160='bucket',<452>,1:155], [@64,186:186='t',<452>,1:186]], id=[[@58,165:166='id',<452>,1:165], [@68,196:196='t',<452>,1:196]]}, query3={bucket=[[@66,188:193='bucket',<452>,1:188]], id=[[@70,198:199='id',<452>,1:198]]}}",
				"{def_query3={context_list={q0=query0, q1=query1, q2=query2, t=query2}, query_dictionary={bucket=[[@66,188:193='bucket',<452>,1:188]], id=[[@70,198:199='id',<452>,1:198]]}, def_query1={context_list={q0=query0, q=query0}, query_dictionary={id=[[@40,118:119='id',<452>,1:118], [@56,163:163='r',<452>,1:163]], s2=[[@36,112:113='s2',<452>,1:112], [@50,147:147='r',<452>,1:147]]}, interface={id=[{name=id, table_ref=q}], s2=[{name=s1, table_ref=q}]}, table_alias={q=query0, q0=query0}}, def_query0={query_dictionary={id=[[@22,71:72='id',<452>,1:71], [@38,116:116='q',<452>,1:116]], s1=[[@18,65:66='s1',<452>,1:65], [@32,104:104='q',<452>,1:104]]}, table_dictionary={events={created_at=[[@7,30:30='e',<452>,1:30]], id=[[@20,69:69='e',<452>,1:69]], slice_len=[[@11,44:44='e',<452>,1:44]]}}, interface={id=[{name=id, table_ref=e}], s1=[{name=created_at, table_ref=e}, {name=slice_len, table_ref=e}]}, table_alias={e=events}}, interface={bucket=[{name=bucket, table_ref=t}], id=[{name=id, table_ref=t}]}, table_alias={q1=query1, q2=query2, t=query2, q0=query0}, def_query2={context_list={q0=query0, q1=query1, r=query1}, query_dictionary={bucket=[[@54,155:160='bucket',<452>,1:155], [@64,186:186='t',<452>,1:186]], id=[[@58,165:166='id',<452>,1:165], [@68,196:196='t',<452>,1:196]]}, interface={bucket=[{name=s2, table_ref=r}], id=[{name=id, table_ref=r}]}, table_alias={q1=query1, r=query1, q0=query0}}}}",
				"null");
	}

	@Test
	public void regressionMultiLayerSetOpDerivedForBoundaryFamily() {
		SqlParserAccess parser = parseEndpoint(
				"with q0 as (select next_day(e.created_at, friday) as run_day, e.id from events e) select d.run_day, d.id from (select q.run_day, q.id from q0 q union all select q.run_day, q.id from q0 q) d",
				"SQL");
		Assert.assertEquals(parser.getAllDiagnostics().toString(), 1, parser.getAllDiagnostics().size());
		assertDiagnosticAtPosition(parser.getSnippet(), "STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
				ParseDiagnostic.Severity.WARNING,
				"snowflake_next_day_expression at (l:1 c:19) is recognized as Snowflake-specific grammar.",
				null, 1, 19);
		assertOutputs(parser,
				"{SQL={with={1={cte={select={1={alias=run_day, datetime_function={function_name=next_day, value={column={name=created_at, table_ref=e}}, weekday={canonical=FRIDAY, text=friday, form=KEYWORD}}}, 2={column={name=id, table_ref=e}}}, from={table={alias=e, table=events}}}, alias=q0}}, query={select={1={column={name=run_day, table_ref=d}}, 2={column={name=id, table_ref=d}}}, from={table={alias=d, query={union={1={select={1={column={name=run_day, table_ref=q}}, 2={column={name=id, table_ref=q}}}, from={table={alias=q, table=q0}}}, 2={union={qualifier=all, operator=union}}, 3={select={1={column={name=run_day, table_ref=q}}, 2={column={name=id, table_ref=q}}}, from={table={alias=q, table=q0}}}}}}}}}}",
				"[run_day, id]",
				"{}",
				"{events={created_at=[[@7,28:28='e',<452>,1:28]], id=[[@16,62:62='e',<452>,1:62]]}}",
				"{union3={run_day=[[@24,89:89='d',<452>,1:89]], id=[[@28,100:100='d',<452>,1:100]]}, query4={run_day=[[@26,91:97='run_day',<452>,1:91]], id=[[@30,102:103='id',<452>,1:102]]}, query0={run_day=[[@14,53:59='run_day',<452>,1:53], [@34,118:118='q',<452>,1:118], [@47,161:161='q',<452>,1:161]], id=[[@18,64:65='id',<452>,1:64], [@38,129:129='q',<452>,1:129], [@51,172:172='q',<452>,1:172]]}, query1={run_day=[[@36,120:126='run_day',<452>,1:120]], id=[[@40,131:132='id',<452>,1:131]]}, query2={run_day=[[@49,163:169='run_day',<452>,1:163]], id=[[@53,174:175='id',<452>,1:174]]}}",
				"{def_query4={def_union3={context_list={q0=query0}, query_dictionary={run_day=[[@24,89:89='d',<452>,1:89]], id=[[@28,100:100='d',<452>,1:100]]}, def_query1={context_list={q0=query0, q=query0}, query_dictionary={run_day=[[@36,120:126='run_day',<452>,1:120]], id=[[@40,131:132='id',<452>,1:131]]}, interface={run_day=[{name=run_day, table_ref=q}], id=[{name=id, table_ref=q}]}, table_alias={q=query0, q0=query0}}, interface={run_day=query_column, id=query_column}, table_alias={q0=query0}, def_query2={context_list={q0=query0, q=query0}, query_dictionary={run_day=[[@49,163:169='run_day',<452>,1:163]], id=[[@53,174:175='id',<452>,1:174]]}, setop=UNION, interface={run_day=[{name=run_day, table_ref=q}], id=[{name=id, table_ref=q}]}, table_alias={q=query0, q0=query0}}}, context_list={q0=query0}, query_dictionary={run_day=[[@26,91:97='run_day',<452>,1:91]], id=[[@30,102:103='id',<452>,1:102]]}, def_query0={query_dictionary={run_day=[[@14,53:59='run_day',<452>,1:53], [@34,118:118='q',<452>,1:118], [@47,161:161='q',<452>,1:161]], id=[[@18,64:65='id',<452>,1:64], [@38,129:129='q',<452>,1:129], [@51,172:172='q',<452>,1:172]]}, table_dictionary={events={created_at=[[@7,28:28='e',<452>,1:28]], id=[[@16,62:62='e',<452>,1:62]]}}, interface={run_day=[{name=created_at, table_ref=e}], id=[{name=id, table_ref=e}]}, table_alias={e=events}}, interface={run_day=[{name=run_day, table_ref=d}], id=[{name=id, table_ref=d}]}, table_alias={d=union3, q0=query0}}}",
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
				"{SCRIPT={1={events={created_at=[[@3,18:18='a',<452>,1:18]]}}, 2={events={created_at=[[@22,96:96='e',<452>,1:96]]}}}}",
				"{SCRIPT={1={query0={bucket=[[@14,54:59='bucket',<452>,1:54]]}}, 2={query0={prev_day=[[@29,123:130='prev_day',<452>,1:123]]}}}}",
				"{SCRIPT={1={def_query0={query_dictionary={bucket=[[@14,54:59='bucket',<452>,1:54]]}, table_dictionary={events={created_at=[[@3,18:18='a',<452>,1:18]]}}, interface={bucket=[{name=created_at, table_ref=a}]}, table_alias={a=events}}}, 2={def_query0={query_dictionary={prev_day=[[@29,123:130='prev_day',<452>,1:123]]}, table_dictionary={events={created_at=[[@22,96:96='e',<452>,1:96]]}}, interface={prev_day=[{name=created_at, table_ref=e}]}, table_alias={e=events}}}}}",
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
				"{orders={order_date=[[@16,69:78='order_date',<452>,1:69]], created_at=[[@3,18:27='created_at',<452>,1:18]]}}",
				"{query0={unnamed_1=[[@17,79:79=')',<288>,1:79]], unnamed_0=[[@10,47:47=')',<288>,1:47]]}}",
				"{def_query0={query_dictionary={unnamed_1=[[@17,79:79=')',<288>,1:79]], unnamed_0=[[@10,47:47=')',<288>,1:47]]}, table_dictionary={orders={order_date=[[@16,69:78='order_date',<452>,1:69]], created_at=[[@3,18:27='created_at',<452>,1:18]]}}, interface={unnamed_1=[{name=order_date, table_ref=orders}], unnamed_0=[{name=created_at, table_ref=orders}]}}}",
				"null");
	}
}
