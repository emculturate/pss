package sql.walker;

import org.junit.Assert;
import org.junit.Test;

import access.Snippet;
import access.SqlParserAccess;
import errorhandling.ParseDiagnostic;

public class SqlEventWalkerDatetimeTruncExtractTests extends AbstractSqlParseEventWalkerTest {

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
				"{events={created_at=[[@12,48:48='a',<459>,1:48]], id=[[@1,7:7='a',<459>,1:7]]}}",
				"{query0={id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={created_at=[[@12,48:48='a',<459>,1:48]], id=[[@1,7:7='a',<459>,1:7]]}}, filters=[{name=created_at, table_ref=a}], interface={id=[{name=id, table_ref=a}]}, table_alias={a=events}}}",
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
				"{events={created_at=[[@3,13:13='a',<459>,1:13], [@25,90:90='a',<459>,1:90], [@36,126:126='a',<459>,1:126]], *=[[@14,49:49='*',<291>,1:49]]}}",
				"{query0={bucket=[[@10,35:40='bucket',<459>,1:35]], total=[[@17,55:59='total',<459>,1:55]]}}",
				"{def_query0={query_dictionary={bucket=[[@10,35:40='bucket',<459>,1:35]], total=[[@17,55:59='total',<459>,1:55]]}, table_dictionary={events={created_at=[[@3,13:13='a',<459>,1:13], [@25,90:90='a',<459>,1:90], [@36,126:126='a',<459>,1:126]], *=[[@14,49:49='*',<291>,1:49]]}}, grouped_by=[{name=created_at, table_ref=a}], filters=[{name=created_at, table_ref=a}], interface={bucket=[{name=created_at, table_ref=a}], total=[]}, table_alias={a=events}}}",
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
				"{events={created_at=[[@5,23:23='a',<459>,1:23], [@20,86:86='a',<459>,1:86]]}}",
				"{query0={bucket=[[@10,40:45='bucket',<459>,1:40], [@25,101:106='bucket',<459>,1:101]]}}",
				"{def_query0={query_dictionary={bucket=[[@10,40:45='bucket',<459>,1:40], [@25,101:106='bucket',<459>,1:101]]}, table_dictionary={events={created_at=[[@5,23:23='a',<459>,1:23], [@20,86:86='a',<459>,1:86]]}}, ordered_by=[{name=created_at, table_ref=a}, {name=bucket, table_ref=query0}], interface={bucket=[{name=created_at, table_ref=a}]}, table_alias={a=events}}}",
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
				"{events={created_at=[[@17,72:72='a',<459>,1:72], [@27,106:106='a',<459>,1:106]], id=[[@1,7:7='a',<459>,1:7]]}}",
				"{query0={id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={window_ordered_by=[{name=created_at, table_ref=a}], query_dictionary={id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={created_at=[[@17,72:72='a',<459>,1:72], [@27,106:106='a',<459>,1:106]], id=[[@1,7:7='a',<459>,1:7]]}}, window_partition_by=[{name=created_at, table_ref=a}], filters=[{name=created_at, table_ref=a}], interface={id=[{name=id, table_ref=a}]}, table_alias={a=events}}}",
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
				"{events={created_at=[[@9,37:37='e',<459>,1:37]], id=[[@16,62:62='e',<459>,1:62]]}}",
				"{query0={bucket=[[@14,54:59='bucket',<459>,1:54], [@30,111:111='b',<459>,1:111]], id=[[@18,64:65='id',<459>,1:64]]}, query1={bucket=[[@32,113:118='bucket',<459>,1:113], [@24,89:89='d',<459>,1:89]]}, query2={bucket=[[@26,91:96='bucket',<459>,1:91]]}}",
				"{def_query2={context_list={base=query0}, query_dictionary={bucket=[[@26,91:96='bucket',<459>,1:91]]}, def_query1={context_list={base=query0, b=query0}, query_dictionary={bucket=[[@32,113:118='bucket',<459>,1:113], [@24,89:89='d',<459>,1:89]]}, interface={bucket=[{name=bucket, table_ref=b}]}, table_alias={b=query0, base=query0}}, def_query0={query_dictionary={bucket=[[@14,54:59='bucket',<459>,1:54], [@30,111:111='b',<459>,1:111]], id=[[@18,64:65='id',<459>,1:64]]}, table_dictionary={events={created_at=[[@9,37:37='e',<459>,1:37]], id=[[@16,62:62='e',<459>,1:62]]}}, interface={bucket=[{name=created_at, table_ref=e}], id=[{name=id, table_ref=e}]}, table_alias={e=events}}, interface={bucket=[{name=bucket, table_ref=d}]}, table_alias={d=query1, base=query0}}}",
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
				"{events={created_at=[[@5,25:25='a',<459>,1:25]]}}",
				"{query0={bucket=[[@10,42:47='bucket',<459>,1:42]]}}",
				"{def_query0={query_dictionary={bucket=[[@10,42:47='bucket',<459>,1:42]]}, table_dictionary={events={created_at=[[@5,25:25='a',<459>,1:25]]}}, interface={bucket=[{name=created_at, table_ref=a}]}, table_alias={a=events}}}",
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
				"{events={created_at=[[@3,13:13='a',<459>,1:13]]}}",
				"{query0={bucket=[[@10,37:42='bucket',<459>,1:37]]}}",
				"{def_query0={query_dictionary={bucket=[[@10,37:42='bucket',<459>,1:37]]}, table_dictionary={events={created_at=[[@3,13:13='a',<459>,1:13]]}}, interface={bucket=[{name=created_at, table_ref=a}]}, table_alias={a=events}}}",
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
				"{accounts={id=[[@20,93:93='b',<459>,1:93]]}, events={account_id=[[@16,78:78='a',<459>,1:78]]}}",
				"{query0={bucket=[[@8,38:43='bucket',<459>,1:38]]}}",
				"{def_query0={query_dictionary={bucket=[[@8,38:43='bucket',<459>,1:38]]}, table_dictionary={accounts={id=[[@20,93:93='b',<459>,1:93]]}, events={account_id=[[@16,78:78='a',<459>,1:78]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}], interface={bucket=[{name=created_at, table_ref=null}]}, table_alias={a=events, b=accounts}}}",
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
				"{accounts={id=[[@20,88:88='b',<459>,1:88]]}, events={account_id=[[@16,73:73='a',<459>,1:73]]}}",
				"{query0={bucket=[[@8,33:38='bucket',<459>,1:33]]}}",
				"{def_query0={query_dictionary={bucket=[[@8,33:38='bucket',<459>,1:33]]}, table_dictionary={accounts={id=[[@20,88:88='b',<459>,1:88]]}, events={account_id=[[@16,73:73='a',<459>,1:73]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}], interface={bucket=[{name=created_at, table_ref=null}]}, table_alias={a=events, b=accounts}}}",
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
				"{events={created_at=[[@14,60:60='e',<459>,1:60]]}}",
				"{query0={created_out=[[@18,76:86='created_out',<459>,1:76]]}, query1={bucket=[[@10,40:45='bucket',<459>,1:40]]}}",
				"{def_query1={query_dictionary={bucket=[[@10,40:45='bucket',<459>,1:40]]}, def_query0={query_dictionary={created_out=[[@18,76:86='created_out',<459>,1:76]]}, table_dictionary={events={created_at=[[@14,60:60='e',<459>,1:60]]}}, interface={created_out=[{name=created_at, table_ref=e}]}, table_alias={e=events}}, interface={bucket=[{name=created_at, table_ref=d}]}, table_alias={d=query0}}}",
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
				"{events={created_at=[[@14,55:55='e',<459>,1:55]]}}",
				"{query0={created_at=[[@16,57:66='created_at',<459>,1:57]]}, query1={bucket=[[@10,35:40='bucket',<459>,1:35]]}}",
				"{def_query1={query_dictionary={bucket=[[@10,35:40='bucket',<459>,1:35]]}, def_query0={query_dictionary={created_at=[[@16,57:66='created_at',<459>,1:57]]}, table_dictionary={events={created_at=[[@14,55:55='e',<459>,1:55]]}}, interface={created_at=[{name=created_at, table_ref=e}]}, table_alias={e=events}}, interface={bucket=[{name=missing_ts, table_ref=d}]}, table_alias={d=query0}}}",
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
				"{query0={rounded=[[@10,26:32='rounded',<459>,1:26]]}}",
				"{def_query0={query_dictionary={rounded=[[@10,26:32='rounded',<459>,1:26]]}, table_dictionary={events={}}, interface={rounded=[]}}}",
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
				"{events={created_at=[[@3,13:13='a',<459>,1:13]]}}",
				"{query0={maybe_generic=[[@8,30:42='maybe_generic',<459>,1:30]]}}",
				"{def_query0={query_dictionary={maybe_generic=[[@8,30:42='maybe_generic',<459>,1:30]]}, table_dictionary={events={created_at=[[@3,13:13='a',<459>,1:13]]}}, interface={maybe_generic=[{name=created_at, table_ref=a}]}, table_alias={a=events}}}",
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
				"{archive_events={bucket=[[@4,28:33='bucket',<459>,1:28]]}, events={created_at=[[@11,59:59='e',<459>,1:59]]}}",
				"{query0={bucket=[[@16,76:81='bucket',<459>,1:76]]}, insert1={bucket=[[@4,28:33='bucket',<459>,1:28]]}}",
				"{def_insert1={query_dictionary={bucket=[[@4,28:33='bucket',<459>,1:28]]}, table_dictionary={archive_events={bucket=[[@4,28:33='bucket',<459>,1:28]]}}, def_query0={query_dictionary={bucket=[[@16,76:81='bucket',<459>,1:76]]}, table_dictionary={events={created_at=[[@11,59:59='e',<459>,1:59]]}}, interface={bucket=[{name=created_at, table_ref=e}]}, table_alias={e=events}}, interface={bucket=[{name=bucket, table_ref=query0}]}}}",
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
				"{update0={bucket=[[@4,20:25='bucket',<459>,1:20]]}}",
				"{def_update0={assignments={bucket=[{name=created_at, table_ref=e}]}, table_dictionary={events={}}, update_dictionary={bucket=[[@4,20:25='bucket',<459>,1:20]]}, target_table={events={bucket=[[@4,20:25='bucket',<459>,1:20]], created_at=[[@8,35:35='e',<459>,1:35]]}}, table_alias={e=events}, lhs_unresolved_columns={bucket={column={name=bucket, table_ref=null}, locations=[[@4,20:25='bucket',<459>,1:20]]}}}}",
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
				"{events={created_at=[[@9,53:53='e',<459>,1:53]]}}",
				"{}",
				"{def_delete0={query_dictionary={}, table_dictionary={events={created_at=[[@9,53:53='e',<459>,1:53]]}}, filters=[{name=created_at, table_ref=e}], interface=null, table_alias={e=events}}}",
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
				"{events={created_at=[[@7,30:30='a',<459>,1:30]], day=[[@5,25:27='day',<73>,1:25]]}}",
				"{query0={udf_bucket=[[@12,47:56='udf_bucket',<459>,1:47]]}}",
				"{def_query0={query_dictionary={udf_bucket=[[@12,47:56='udf_bucket',<459>,1:47]]}, table_dictionary={events={created_at=[[@7,30:30='a',<459>,1:30]], day=[[@5,25:27='day',<73>,1:25]]}}, interface={udf_bucket=[{name=day, table_ref=events}, {name=created_at, table_ref=a}]}, table_alias={a=events}}}",
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
				"{SCRIPT={1={events={created_at=[[@5,23:23='a',<459>,1:23]]}}, 2={events={created_at=[[@18,75:75='e',<459>,1:75]]}}}}",
				"{SCRIPT={1={query0={bucket=[[@10,40:45='bucket',<459>,1:40]]}}, 2={query0={bucket=[[@25,97:102='bucket',<459>,1:97]]}}}}",
				"{SCRIPT={1={def_query0={query_dictionary={bucket=[[@10,40:45='bucket',<459>,1:40]]}, table_dictionary={events={created_at=[[@5,23:23='a',<459>,1:23]]}}, interface={bucket=[{name=created_at, table_ref=a}]}, table_alias={a=events}}}, 2={def_query0={query_dictionary={bucket=[[@25,97:102='bucket',<459>,1:97]]}, table_dictionary={events={created_at=[[@18,75:75='e',<459>,1:75]]}}, interface={bucket=[{name=created_at, table_ref=e}]}, table_alias={e=events}}}}}",
				"{SCRIPT={1={queryInterface=[bucket], Walker Warnings=[snowflake_date_trunc_expression at (l:1 c:7) is recognized as Snowflake-specific grammar., snowflake_datetime_trunc_expression at (l:1 c:69) is recognized as Snowflake-specific grammar.]}, 2={queryInterface=[bucket], Walker Warnings=[snowflake_date_trunc_expression at (l:1 c:7) is recognized as Snowflake-specific grammar., snowflake_datetime_trunc_expression at (l:1 c:69) is recognized as Snowflake-specific grammar.]}}}");
	}

	@Test
	public void truncExtractR01NestedExtractInsideDateTruncValueV0Test() {
		SqlParserAccess parser = parseEndpoint("select date_trunc(day, extract(day from a.created_at)) as bucket from events a", "SQL");
		assertOutputs(parser,
				"{SQL={select={1={alias=bucket, datetime_function={function_name=date_trunc, level={canonical=DAY, text=day, form=KEYWORD}, value={datetime_function={function_name=extract, part_form=KEYWORD, part=day, source={column={name=created_at, table_ref=a}}}}}}}, from={table={alias=a, table=events}}}}",
				"[bucket]",
				"{}",
				"{events={created_at=[[@9,40:40='a',<459>,1:40]]}}",
				"{query0={bucket=[[@15,58:63='bucket',<459>,1:58]]}}",
				"{def_query0={query_dictionary={bucket=[[@15,58:63='bucket',<459>,1:58]]}, table_dictionary={events={created_at=[[@9,40:40='a',<459>,1:40]]}}, interface={bucket=[{name=created_at, table_ref=a}]}, table_alias={a=events}}}",
				"null");
	}

	@Test
	public void truncExtractR02NestedDatePartInsideTruncValueV0Test() {
		SqlParserAccess parser = parseEndpoint("select trunc(date_part(day, a.created_at), day) as bucket from events a", "SQL");
		assertOutputs(parser,
				"{SQL={select={1={alias=bucket, datetime_function={function_name=trunc, level={canonical=DAY, text=day, form=KEYWORD}, value={datetime_function={function_name=date_part, part_form=KEYWORD, part=day, source={column={name=created_at, table_ref=a}}}}}}}, from={table={alias=a, table=events}}}}",
				"[bucket]",
				"{}",
				"{events={created_at=[[@7,28:28='a',<459>,1:28]]}}",
				"{query0={bucket=[[@15,51:56='bucket',<459>,1:51]]}}",
				"{def_query0={query_dictionary={bucket=[[@15,51:56='bucket',<459>,1:51]]}, table_dictionary={events={created_at=[[@7,28:28='a',<459>,1:28]]}}, interface={bucket=[{name=created_at, table_ref=a}]}, table_alias={a=events}}}",
				"null");
	}

	@Test
	public void truncExtractR03NestedDateTruncInsideExtractSourceV0Test() {
		SqlParserAccess parser = parseEndpoint("select extract(day from date_trunc(day, a.created_at)) as day_part from events a", "SQL");
		assertOutputs(parser,
				"{SQL={select={1={alias=day_part, datetime_function={function_name=extract, part_form=KEYWORD, part=day, source={datetime_function={function_name=date_trunc, level={canonical=DAY, text=day, form=KEYWORD}, value={column={name=created_at, table_ref=a}}}}}}}, from={table={alias=a, table=events}}}}",
				"[day_part]",
				"{}",
				"{events={created_at=[[@9,40:40='a',<459>,1:40]]}}",
				"{query0={day_part=[[@15,58:65='day_part',<459>,1:58]]}}",
				"{def_query0={query_dictionary={day_part=[[@15,58:65='day_part',<459>,1:58]]}, table_dictionary={events={created_at=[[@9,40:40='a',<459>,1:40]]}}, interface={day_part=[{name=created_at, table_ref=a}]}, table_alias={a=events}}}",
				"null");
	}

	@Test
	public void truncExtractR04NestedTruncInsideDatePartSourceV0Test() {
		SqlParserAccess parser = parseEndpoint("select date_part(day, trunc(a.created_at, day)) as day_part from events a", "SQL");
		assertOutputs(parser,
				"{SQL={select={1={alias=day_part, datetime_function={function_name=date_part, part_form=KEYWORD, part=day, source={datetime_function={function_name=trunc, level={canonical=DAY, text=day, form=KEYWORD}, value={column={name=created_at, table_ref=a}}}}}}}, from={table={alias=a, table=events}}}}",
				"[day_part]",
				"{}",
				"{events={created_at=[[@7,28:28='a',<459>,1:28]]}}",
				"{query0={day_part=[[@15,51:58='day_part',<459>,1:51]]}}",
				"{def_query0={query_dictionary={day_part=[[@15,51:58='day_part',<459>,1:51]]}, table_dictionary={events={created_at=[[@7,28:28='a',<459>,1:28]]}}, interface={day_part=[{name=created_at, table_ref=a}]}, table_alias={a=events}}}",
				"null");
	}

	@Test
	public void truncExtractR05SiblingDateTruncAndExtractIndependentV0Test() {
		SqlParserAccess parser = parseEndpoint("select date_trunc(day, a.created_at) as b1, extract(day from a.created_at) as p1 from events a", "SQL");
		assertOutputs(parser,
				"{SQL={select={1={alias=b1, datetime_function={function_name=date_trunc, level={canonical=DAY, text=day, form=KEYWORD}, value={column={name=created_at, table_ref=a}}}}, 2={alias=p1, datetime_function={function_name=extract, part_form=KEYWORD, part=day, source={column={name=created_at, table_ref=a}}}}}, from={table={alias=a, table=events}}}}",
				"[p1, b1]",
				"{}",
				"{events={created_at=[[@5,23:23='a',<459>,1:23], [@16,61:61='a',<459>,1:61]]}}",
				"{query0={p1=[[@21,78:79='p1',<459>,1:78]], b1=[[@10,40:41='b1',<459>,1:40]]}}",
				"{def_query0={query_dictionary={p1=[[@21,78:79='p1',<459>,1:78]], b1=[[@10,40:41='b1',<459>,1:40]]}, table_dictionary={events={created_at=[[@5,23:23='a',<459>,1:23], [@16,61:61='a',<459>,1:61]]}}, interface={p1=[{name=created_at, table_ref=a}], b1=[{name=created_at, table_ref=a}]}, table_alias={a=events}}}",
				"null");
	}

	@Test
	public void truncExtractR06SiblingTruncAndDatePartIndependentV0Test() {
		SqlParserAccess parser = parseEndpoint("select trunc(a.created_at, day) as b1, date_part(day, a.created_at) as p1 from events a", "SQL");
		assertOutputs(parser,
				"{SQL={select={1={alias=b1, datetime_function={function_name=trunc, level={canonical=DAY, text=day, form=KEYWORD}, value={column={name=created_at, table_ref=a}}}}, 2={alias=p1, datetime_function={function_name=date_part, part_form=KEYWORD, part=day, source={column={name=created_at, table_ref=a}}}}}, from={table={alias=a, table=events}}}}",
				"[p1, b1]",
				"{}",
				"{events={created_at=[[@3,13:13='a',<459>,1:13], [@16,54:54='a',<459>,1:54]]}}",
				"{query0={p1=[[@21,71:72='p1',<459>,1:71]], b1=[[@10,35:36='b1',<459>,1:35]]}}",
				"{def_query0={query_dictionary={p1=[[@21,71:72='p1',<459>,1:71]], b1=[[@10,35:36='b1',<459>,1:35]]}, table_dictionary={events={created_at=[[@3,13:13='a',<459>,1:13], [@16,54:54='a',<459>,1:54]]}}, interface={p1=[{name=created_at, table_ref=a}], b1=[{name=created_at, table_ref=a}]}, table_alias={a=events}}}",
				"null");
	}

	@Test
	public void truncExtractR07NestedDatePartArithmeticInsideDateTruncV0Test() {
		SqlParserAccess parser = parseEndpoint("select date_trunc(day, date_part(day, a.created_at) + 1) as bucket from events a", "SQL");
		assertOutputs(parser,
				"{SQL={select={1={alias=bucket, datetime_function={function_name=date_trunc, level={canonical=DAY, text=day, form=KEYWORD}, value={calc={left={datetime_function={function_name=date_part, part_form=KEYWORD, part=day, source={column={name=created_at, table_ref=a}}}}, right={literal=1}, operator=+}}}}}, from={table={alias=a, table=events}}}}",
				"[bucket]",
				"{}",
				"{events={created_at=[[@9,38:38='a',<459>,1:38]]}}",
				"{query0={bucket=[[@17,60:65='bucket',<459>,1:60]]}}",
				"{def_query0={query_dictionary={bucket=[[@17,60:65='bucket',<459>,1:60]]}, table_dictionary={events={created_at=[[@9,38:38='a',<459>,1:38]]}}, interface={bucket=[{name=created_at, table_ref=a}]}, table_alias={a=events}}}",
				"null");
	}

	@Test
	public void truncExtractR08NestedExtractArithmeticInsideTruncV0Test() {
		SqlParserAccess parser = parseEndpoint("select trunc(extract(day from a.created_at) + 1, day) as bucket from events a", "SQL");
		assertOutputs(parser,
				"{SQL={select={1={alias=bucket, datetime_function={function_name=trunc, level={canonical=DAY, text=day, form=KEYWORD}, value={calc={left={datetime_function={function_name=extract, part_form=KEYWORD, part=day, source={column={name=created_at, table_ref=a}}}}, right={literal=1}, operator=+}}}}}, from={table={alias=a, table=events}}}}",
				"[bucket]",
				"{}",
				"{events={created_at=[[@7,30:30='a',<459>,1:30]]}}",
				"{query0={bucket=[[@17,57:62='bucket',<459>,1:57]]}}",
				"{def_query0={query_dictionary={bucket=[[@17,57:62='bucket',<459>,1:57]]}, table_dictionary={events={created_at=[[@7,30:30='a',<459>,1:30]]}}, interface={bucket=[{name=created_at, table_ref=a}]}, table_alias={a=events}}}",
				"null");
	}

	@Test
	public void truncExtractR09NestedDateTruncWithinCaseFeedingExtractV0Test() {
		SqlParserAccess parser = parseEndpoint("select extract(day from case when a.flag = 1 then date_trunc(day, a.created_at) else a.created_at end) as day_part from events a", "SQL");
		assertOutputs(parser,
				"{SQL={select={1={alias=day_part, datetime_function={function_name=extract, part_form=KEYWORD, part=day, source={case={clauses={1={then={datetime_function={function_name=date_trunc, level={canonical=DAY, text=day, form=KEYWORD}, value={column={name=created_at, table_ref=a}}}}, when={condition={left={column={name=flag, table_ref=a}}, right={literal=1}, operator==}}}}, else={column={name=created_at, table_ref=a}}}}}}}, from={table={alias=a, table=events}}}}",
				"[day_part]",
				"{}",
				"{events={flag=[[@7,34:34='a',<459>,1:34]], created_at=[[@17,66:66='a',<459>,1:66], [@22,85:85='a',<459>,1:85]]}}",
				"{query0={day_part=[[@28,106:113='day_part',<459>,1:106]]}}",
				"{def_query0={query_dictionary={day_part=[[@28,106:113='day_part',<459>,1:106]]}, table_dictionary={events={flag=[[@7,34:34='a',<459>,1:34]], created_at=[[@17,66:66='a',<459>,1:66], [@22,85:85='a',<459>,1:85]]}}, interface={day_part=[{name=created_at, table_ref=a}, {name=flag, table_ref=a}]}, table_alias={a=events}}}",
				"null");
	}

	@Test
	public void truncExtractR10NestedTruncWithinCaseFeedingDatePartV0Test() {
		SqlParserAccess parser = parseEndpoint("select date_part(day, case when a.flag = 1 then trunc(a.created_at, day) else a.created_at end) as day_part from events a", "SQL");
		assertOutputs(parser,
				"{SQL={select={1={alias=day_part, datetime_function={function_name=date_part, part_form=KEYWORD, part=day, source={case={clauses={1={then={datetime_function={function_name=trunc, level={canonical=DAY, text=day, form=KEYWORD}, value={column={name=created_at, table_ref=a}}}}, when={condition={left={column={name=flag, table_ref=a}}, right={literal=1}, operator==}}}}, else={column={name=created_at, table_ref=a}}}}}}}, from={table={alias=a, table=events}}}}",
				"[day_part]",
				"{}",
				"{events={flag=[[@7,32:32='a',<459>,1:32]], created_at=[[@15,54:54='a',<459>,1:54], [@22,78:78='a',<459>,1:78]]}}",
				"{query0={day_part=[[@28,99:106='day_part',<459>,1:99]]}}",
				"{def_query0={query_dictionary={day_part=[[@28,99:106='day_part',<459>,1:99]]}, table_dictionary={events={flag=[[@7,32:32='a',<459>,1:32]], created_at=[[@15,54:54='a',<459>,1:54], [@22,78:78='a',<459>,1:78]]}}, interface={day_part=[{name=created_at, table_ref=a}, {name=flag, table_ref=a}]}, table_alias={a=events}}}",
				"null");
	}

	@Test
	public void truncExtractR11DatePartFromDateTruncFromFormNestedV0Test() {
		SqlParserAccess parser = parseEndpoint("select date_part(day from date_trunc(day, a.created_at)) as day_part from events a", "SQL");
		assertOutputs(parser,
				"{SQL={select={1={alias=day_part, datetime_function={function_name=date_part, part_form=KEYWORD, part=day, source={datetime_function={function_name=date_trunc, level={canonical=DAY, text=day, form=KEYWORD}, value={column={name=created_at, table_ref=a}}}}}}}, from={table={alias=a, table=events}}}}",
				"[day_part]",
				"{}",
				"{events={created_at=[[@9,42:42='a',<459>,1:42]]}}",
				"{query0={day_part=[[@15,60:67='day_part',<459>,1:60]]}}",
				"{def_query0={query_dictionary={day_part=[[@15,60:67='day_part',<459>,1:60]]}, table_dictionary={events={created_at=[[@9,42:42='a',<459>,1:42]]}}, interface={day_part=[{name=created_at, table_ref=a}]}, table_alias={a=events}}}",
				"null");
	}

	@Test
	public void truncExtractR12ExtractFromTruncNestedV0Test() {
		SqlParserAccess parser = parseEndpoint("select extract(day from trunc(a.created_at, day)) as day_part from events a", "SQL");
		assertOutputs(parser,
				"{SQL={select={1={alias=day_part, datetime_function={function_name=extract, part_form=KEYWORD, part=day, source={datetime_function={function_name=trunc, level={canonical=DAY, text=day, form=KEYWORD}, value={column={name=created_at, table_ref=a}}}}}}}, from={table={alias=a, table=events}}}}",
				"[day_part]",
				"{}",
				"{events={created_at=[[@7,30:30='a',<459>,1:30]]}}",
				"{query0={day_part=[[@15,53:60='day_part',<459>,1:53]]}}",
				"{def_query0={query_dictionary={day_part=[[@15,53:60='day_part',<459>,1:53]]}, table_dictionary={events={created_at=[[@7,30:30='a',<459>,1:30]]}}, interface={day_part=[{name=created_at, table_ref=a}]}, table_alias={a=events}}}",
				"null");
	}

	@Test
	public void truncExtractR13SiblingDateTruncAliasReuseOrderByV0Test() {
		SqlParserAccess parser = parseEndpoint("select date_trunc(day, a.created_at) as bucket, date_trunc(day, a.updated_at) as bucket2 from events a order by bucket, bucket2", "SQL");
		assertOutputs(parser,
				"{SQL={select={1={alias=bucket, datetime_function={function_name=date_trunc, level={canonical=DAY, text=day, form=KEYWORD}, value={column={name=created_at, table_ref=a}}}}, 2={alias=bucket2, datetime_function={function_name=date_trunc, level={canonical=DAY, text=day, form=KEYWORD}, value={column={name=updated_at, table_ref=a}}}}}, orderby={1={null_order=null, predicand={column={name=bucket, table_ref=null}}, sort_order=ASC}, 2={null_order=null, predicand={column={name=bucket2, table_ref=null}}, sort_order=ASC}}, from={table={alias=a, table=events}}}}",
				"[bucket, bucket2]",
				"{}",
				"{events={updated_at=[[@16,64:64='a',<459>,1:64]], created_at=[[@5,23:23='a',<459>,1:23]]}}",
				"{query0={bucket=[[@10,40:45='bucket',<459>,1:40], [@27,112:117='bucket',<459>,1:112]], bucket2=[[@21,81:87='bucket2',<459>,1:81], [@29,120:126='bucket2',<459>,1:120]]}}",
				"{def_query0={query_dictionary={bucket=[[@10,40:45='bucket',<459>,1:40], [@27,112:117='bucket',<459>,1:112]], bucket2=[[@21,81:87='bucket2',<459>,1:81], [@29,120:126='bucket2',<459>,1:120]]}, table_dictionary={events={updated_at=[[@16,64:64='a',<459>,1:64]], created_at=[[@5,23:23='a',<459>,1:23]]}}, ordered_by=[{name=bucket, table_ref=query0}, {name=bucket2, table_ref=query0}], interface={bucket=[{name=created_at, table_ref=a}], bucket2=[{name=updated_at, table_ref=a}]}, table_alias={a=events}}}",
				"null");
	}

	@Test
	public void truncExtractR14SiblingExtractAliasReuseOrderByV0Test() {
		SqlParserAccess parser = parseEndpoint("select extract(day from a.created_at) as d1, extract(day from a.updated_at) as d2 from events a order by d1, d2", "SQL");
		assertOutputs(parser,
				"{SQL={select={1={alias=d1, datetime_function={function_name=extract, part_form=KEYWORD, part=day, source={column={name=created_at, table_ref=a}}}}, 2={alias=d2, datetime_function={function_name=extract, part_form=KEYWORD, part=day, source={column={name=updated_at, table_ref=a}}}}}, orderby={1={null_order=null, predicand={column={name=d1, table_ref=null}}, sort_order=ASC}, 2={null_order=null, predicand={column={name=d2, table_ref=null}}, sort_order=ASC}}, from={table={alias=a, table=events}}}}",
				"[d1, d2]",
				"{}",
				"{events={updated_at=[[@16,62:62='a',<459>,1:62]], created_at=[[@5,24:24='a',<459>,1:24]]}}",
				"{query0={d1=[[@10,41:42='d1',<459>,1:41], [@27,105:106='d1',<459>,1:105]], d2=[[@21,79:80='d2',<459>,1:79], [@29,109:110='d2',<459>,1:109]]}}",
				"{def_query0={query_dictionary={d1=[[@10,41:42='d1',<459>,1:41], [@27,105:106='d1',<459>,1:105]], d2=[[@21,79:80='d2',<459>,1:79], [@29,109:110='d2',<459>,1:109]]}, table_dictionary={events={updated_at=[[@16,62:62='a',<459>,1:62]], created_at=[[@5,24:24='a',<459>,1:24]]}}, ordered_by=[{name=d1, table_ref=query0}, {name=d2, table_ref=query0}], interface={d1=[{name=created_at, table_ref=a}], d2=[{name=updated_at, table_ref=a}]}, table_alias={a=events}}}",
				"null");
	}

	@Test
	public void truncExtractR15NestedDoubleDateTruncV0Test() {
		SqlParserAccess parser = parseEndpoint("select date_trunc(day, date_trunc(day, a.created_at)) as bucket from events a", "SQL");
		assertOutputs(parser,
				"{SQL={select={1={alias=bucket, datetime_function={function_name=date_trunc, level={canonical=DAY, text=day, form=KEYWORD}, value={datetime_function={function_name=date_trunc, level={canonical=DAY, text=day, form=KEYWORD}, value={column={name=created_at, table_ref=a}}}}}}}, from={table={alias=a, table=events}}}}",
				"[bucket]",
				"{}",
				"{events={created_at=[[@9,39:39='a',<459>,1:39]]}}",
				"{query0={bucket=[[@15,57:62='bucket',<459>,1:57]]}}",
				"{def_query0={query_dictionary={bucket=[[@15,57:62='bucket',<459>,1:57]]}, table_dictionary={events={created_at=[[@9,39:39='a',<459>,1:39]]}}, interface={bucket=[{name=created_at, table_ref=a}]}, table_alias={a=events}}}",
				"null");
	}

	@Test
	public void truncExtractR16NestedDoubleTruncV0Test() {
		SqlParserAccess parser = parseEndpoint("select trunc(trunc(a.created_at, day), day) as bucket from events a", "SQL");
		assertOutputs(parser,
				"{SQL={select={1={alias=bucket, datetime_function={function_name=trunc, level={canonical=DAY, text=day, form=KEYWORD}, value={datetime_function={function_name=trunc, level={canonical=DAY, text=day, form=KEYWORD}, value={column={name=created_at, table_ref=a}}}}}}}, from={table={alias=a, table=events}}}}",
				"[bucket]",
				"{}",
				"{events={created_at=[[@5,19:19='a',<459>,1:19]]}}",
				"{query0={bucket=[[@15,47:52='bucket',<459>,1:47]]}}",
				"{def_query0={query_dictionary={bucket=[[@15,47:52='bucket',<459>,1:47]]}, table_dictionary={events={created_at=[[@5,19:19='a',<459>,1:19]]}}, interface={bucket=[{name=created_at, table_ref=a}]}, table_alias={a=events}}}",
				"null");
	}

	@Test
	public void truncExtractR17NestedMixedChainDatePartExtractTruncV0Test() {
		SqlParserAccess parser = parseEndpoint("select trunc(date_part(day, extract(day from a.created_at)), day) as bucket from events a", "SQL");
		assertOutputs(parser,
				"{SQL={select={1={alias=bucket, datetime_function={function_name=trunc, level={canonical=DAY, text=day, form=KEYWORD}, value={datetime_function={function_name=date_part, part_form=KEYWORD, part=day, source={datetime_function={function_name=extract, part_form=KEYWORD, part=day, source={column={name=created_at, table_ref=a}}}}}}}}}, from={table={alias=a, table=events}}}}",
				"[bucket]",
				"{}",
				"{events={created_at=[[@11,45:45='a',<459>,1:45]]}}",
				"{query0={bucket=[[@20,69:74='bucket',<459>,1:69]]}}",
				"{def_query0={query_dictionary={bucket=[[@20,69:74='bucket',<459>,1:69]]}, table_dictionary={events={created_at=[[@11,45:45='a',<459>,1:45]]}}, interface={bucket=[{name=created_at, table_ref=a}]}, table_alias={a=events}}}",
				"null");
	}

	@Test
	public void truncExtractR18SiblingMixedAllFamiliesV0Test() {
		SqlParserAccess parser = parseEndpoint("select date_trunc(day, a.created_at) as b1, trunc(a.updated_at, day) as b2, extract(day from a.created_at) as p1, date_part(day, a.updated_at) as p2 from events a", "SQL");
		assertOutputs(parser,
				"{SQL={select={1={alias=b1, datetime_function={function_name=date_trunc, level={canonical=DAY, text=day, form=KEYWORD}, value={column={name=created_at, table_ref=a}}}}, 2={alias=b2, datetime_function={function_name=trunc, level={canonical=DAY, text=day, form=KEYWORD}, value={column={name=updated_at, table_ref=a}}}}, 3={alias=p1, datetime_function={function_name=extract, part_form=KEYWORD, part=day, source={column={name=created_at, table_ref=a}}}}, 4={alias=p2, datetime_function={function_name=date_part, part_form=KEYWORD, part=day, source={column={name=updated_at, table_ref=a}}}}}, from={table={alias=a, table=events}}}}",
				"[b2, p1, p2, b1]",
				"{}",
				"{events={updated_at=[[@14,50:50='a',<459>,1:50], [@38,129:129='a',<459>,1:129]], created_at=[[@5,23:23='a',<459>,1:23], [@27,93:93='a',<459>,1:93]]}}",
				"{query0={b2=[[@21,72:73='b2',<459>,1:72]], p1=[[@32,110:111='p1',<459>,1:110]], p2=[[@43,146:147='p2',<459>,1:146]], b1=[[@10,40:41='b1',<459>,1:40]]}}",
				"{def_query0={query_dictionary={b2=[[@21,72:73='b2',<459>,1:72]], p1=[[@32,110:111='p1',<459>,1:110]], p2=[[@43,146:147='p2',<459>,1:146]], b1=[[@10,40:41='b1',<459>,1:40]]}, table_dictionary={events={updated_at=[[@14,50:50='a',<459>,1:50], [@38,129:129='a',<459>,1:129]], created_at=[[@5,23:23='a',<459>,1:23], [@27,93:93='a',<459>,1:93]]}}, interface={b2=[{name=updated_at, table_ref=a}], p1=[{name=created_at, table_ref=a}], p2=[{name=updated_at, table_ref=a}], b1=[{name=created_at, table_ref=a}]}, table_alias={a=events}}}",
				"null");
	}

	@Test
	public void truncExtractR19SetOpUnionBranchIsolationDateTruncV0Test() {
		SqlParserAccess parser = parseEndpoint("select date_trunc(day, a.created_at) as bucket, a.id from events a union all select date_trunc(day, b.created_at) as bucket, b.id from events_archive b", "SQL");
		assertOutputs(parser,
				"{SQL={union={1={select={1={alias=bucket, datetime_function={function_name=date_trunc, level={canonical=DAY, text=day, form=KEYWORD}, value={column={name=created_at, table_ref=a}}}}, 2={column={name=id, table_ref=a}}}, from={table={alias=a, table=events}}}, 2={union={qualifier=all, operator=union}}, 3={select={1={alias=bucket, datetime_function={function_name=date_trunc, level={canonical=DAY, text=day, form=KEYWORD}, value={column={name=created_at, table_ref=b}}}}, 2={column={name=id, table_ref=b}}}, from={table={alias=b, table=events_archive}}}}}}",
				"[bucket, id]",
				"{}",
				"{events_archive={created_at=[[@25,100:100='b',<459>,1:100]], id=[[@32,125:125='b',<459>,1:125]]}, events={created_at=[[@5,23:23='a',<459>,1:23]], id=[[@12,48:48='a',<459>,1:48]]}}",
				"{query0={bucket=[[@10,40:45='bucket',<459>,1:40]], id=[[@14,50:51='id',<459>,1:50]]}, query1={bucket=[[@30,117:122='bucket',<459>,1:117]], id=[[@34,127:128='id',<459>,1:127]]}}",
				"{def_union2={def_query1={query_dictionary={bucket=[[@30,117:122='bucket',<459>,1:117]], id=[[@34,127:128='id',<459>,1:127]]}, table_dictionary={events_archive={created_at=[[@25,100:100='b',<459>,1:100]], id=[[@32,125:125='b',<459>,1:125]]}}, setop=UNION, interface={bucket=[{name=created_at, table_ref=b}], id=[{name=id, table_ref=b}]}, table_alias={b=events_archive}}, def_query0={query_dictionary={bucket=[[@10,40:45='bucket',<459>,1:40]], id=[[@14,50:51='id',<459>,1:50]]}, table_dictionary={events={created_at=[[@5,23:23='a',<459>,1:23]], id=[[@12,48:48='a',<459>,1:48]]}}, interface={bucket=[{name=created_at, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}, interface={bucket=query_column, id=query_column}}}",
				"null");
	}

	@Test
	public void truncExtractR20SetOpIntersectBranchIsolationTruncV0Test() {
		SqlParserAccess parser = parseEndpoint("select trunc(a.created_at, day) as bucket, a.id from events a intersect select trunc(b.created_at, day) as bucket, b.id from events_archive b", "SQL");
		assertOutputs(parser,
				"{SQL={intersect={1={select={1={alias=bucket, datetime_function={function_name=trunc, level={canonical=DAY, text=day, form=KEYWORD}, value={column={name=created_at, table_ref=a}}}}, 2={column={name=id, table_ref=a}}}, from={table={alias=a, table=events}}}, 2={intersect={qualifier=null, operator=intersect}}, 3={select={1={alias=bucket, datetime_function={function_name=trunc, level={canonical=DAY, text=day, form=KEYWORD}, value={column={name=created_at, table_ref=b}}}}, 2={column={name=id, table_ref=b}}}, from={table={alias=b, table=events_archive}}}}}}",
				"[bucket, id]",
				"{}",
				"{events_archive={created_at=[[@22,85:85='b',<459>,1:85]], id=[[@31,115:115='b',<459>,1:115]]}, events={created_at=[[@3,13:13='a',<459>,1:13]], id=[[@12,43:43='a',<459>,1:43]]}}",
				"{query0={bucket=[[@10,35:40='bucket',<459>,1:35]], id=[[@14,45:46='id',<459>,1:45]]}, query1={bucket=[[@29,107:112='bucket',<459>,1:107]], id=[[@33,117:118='id',<459>,1:117]]}}",
				"{def_intersect2={def_query1={query_dictionary={bucket=[[@29,107:112='bucket',<459>,1:107]], id=[[@33,117:118='id',<459>,1:117]]}, table_dictionary={events_archive={created_at=[[@22,85:85='b',<459>,1:85]], id=[[@31,115:115='b',<459>,1:115]]}}, setop=INTERSECTION, interface={bucket=[{name=created_at, table_ref=b}], id=[{name=id, table_ref=b}]}, table_alias={b=events_archive}}, def_query0={query_dictionary={bucket=[[@10,35:40='bucket',<459>,1:35]], id=[[@14,45:46='id',<459>,1:45]]}, table_dictionary={events={created_at=[[@3,13:13='a',<459>,1:13]], id=[[@12,43:43='a',<459>,1:43]]}}, interface={bucket=[{name=created_at, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}, interface={bucket=query_column, id=query_column}}}",
				"null");
	}

	@Test
	public void truncExtractR21SetOpExceptBranchIsolationExtractDatePartV0Test() {
		SqlParserAccess parser = parseEndpoint("select extract(day from a.created_at) as part_value, a.id from events a except select date_part(day, b.created_at) as part_value, b.id from events_archive b", "SQL");
		assertOutputs(parser,
				"{SQL={union={1={select={1={alias=part_value, datetime_function={function_name=extract, part_form=KEYWORD, part=day, source={column={name=created_at, table_ref=a}}}}, 2={column={name=id, table_ref=a}}}, from={table={alias=a, table=events}}}, 2={union={qualifier=null, operator=except}}, 3={select={1={alias=part_value, datetime_function={function_name=date_part, part_form=KEYWORD, part=day, source={column={name=created_at, table_ref=b}}}}, 2={column={name=id, table_ref=b}}}, from={table={alias=b, table=events_archive}}}}}}",
				"[part_value, id]",
				"{}",
				"{events_archive={created_at=[[@24,101:101='b',<459>,1:101]], id=[[@31,130:130='b',<459>,1:130]]}, events={created_at=[[@5,24:24='a',<459>,1:24]], id=[[@12,53:53='a',<459>,1:53]]}}",
				"{query0={part_value=[[@10,41:50='part_value',<459>,1:41]], id=[[@14,55:56='id',<459>,1:55]]}, query1={part_value=[[@29,118:127='part_value',<459>,1:118]], id=[[@33,132:133='id',<459>,1:132]]}}",
				"{def_union2={def_query1={query_dictionary={part_value=[[@29,118:127='part_value',<459>,1:118]], id=[[@33,132:133='id',<459>,1:132]]}, table_dictionary={events_archive={created_at=[[@24,101:101='b',<459>,1:101]], id=[[@31,130:130='b',<459>,1:130]]}}, setop=EXCEPT, interface={part_value=[{name=created_at, table_ref=b}], id=[{name=id, table_ref=b}]}, table_alias={b=events_archive}}, def_query0={query_dictionary={part_value=[[@10,41:50='part_value',<459>,1:41]], id=[[@14,55:56='id',<459>,1:55]]}, table_dictionary={events={created_at=[[@5,24:24='a',<459>,1:24]], id=[[@12,53:53='a',<459>,1:53]]}}, interface={part_value=[{name=created_at, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}, interface={part_value=query_column, id=query_column}}}",
				"null");
	}

	@Test
	public void truncExtractR22NestedSetOpMixedFamilyIsolationV0Test() {
		SqlParserAccess parser = parseEndpoint("(select date_trunc(day, a.created_at) as bucket, a.id from events a union select trunc(b.created_at, day) as bucket, b.id from events_archive b) except select date_trunc(day, c.created_at) as bucket, c.id from events c", "SQL");
		assertOutputs(parser,
				"{SQL={union={1={union={1={select={1={alias=bucket, datetime_function={function_name=date_trunc, level={canonical=DAY, text=day, form=KEYWORD}, value={column={name=created_at, table_ref=a}}}}, 2={column={name=id, table_ref=a}}}, from={table={alias=a, table=events}}}, 2={union={qualifier=null, operator=union}}, 3={select={1={alias=bucket, datetime_function={function_name=trunc, level={canonical=DAY, text=day, form=KEYWORD}, value={column={name=created_at, table_ref=b}}}}, 2={column={name=id, table_ref=b}}}, from={table={alias=b, table=events_archive}}}}}, 2={union={qualifier=null, operator=except}}, 3={select={1={alias=bucket, datetime_function={function_name=date_trunc, level={canonical=DAY, text=day, form=KEYWORD}, value={column={name=created_at, table_ref=c}}}}, 2={column={name=id, table_ref=c}}}, from={table={alias=c, table=events}}}}}}",
				"[bucket, id]",
				"{}",
				"{events_archive={created_at=[[@23,87:87='b',<459>,1:87]], id=[[@32,117:117='b',<459>,1:117]]}, events={created_at=[[@6,24:24='a',<459>,1:24], [@45,175:175='c',<459>,1:175]], id=[[@13,49:49='a',<459>,1:49], [@52,200:200='c',<459>,1:200]]}}",
				"{query0={bucket=[[@11,41:46='bucket',<459>,1:41]], id=[[@15,51:52='id',<459>,1:51]]}, query1={bucket=[[@30,109:114='bucket',<459>,1:109]], id=[[@34,119:120='id',<459>,1:119]]}, query3={bucket=[[@50,192:197='bucket',<459>,1:192]], id=[[@54,202:203='id',<459>,1:202]]}}",
				"{def_union4={def_union2={def_query1={query_dictionary={bucket=[[@30,109:114='bucket',<459>,1:109]], id=[[@34,119:120='id',<459>,1:119]]}, table_dictionary={events_archive={created_at=[[@23,87:87='b',<459>,1:87]], id=[[@32,117:117='b',<459>,1:117]]}}, setop=UNION, interface={bucket=[{name=created_at, table_ref=b}], id=[{name=id, table_ref=b}]}, table_alias={b=events_archive}}, def_query0={query_dictionary={bucket=[[@11,41:46='bucket',<459>,1:41]], id=[[@15,51:52='id',<459>,1:51]]}, table_dictionary={events={created_at=[[@6,24:24='a',<459>,1:24], [@45,175:175='c',<459>,1:175]], id=[[@13,49:49='a',<459>,1:49], [@52,200:200='c',<459>,1:200]]}}, interface={bucket=[{name=created_at, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}, interface={bucket=query_column, id=query_column}}, interface={bucket=query_column, id=query_column}, def_query3={query_dictionary={bucket=[[@50,192:197='bucket',<459>,1:192]], id=[[@54,202:203='id',<459>,1:202]]}, table_dictionary={events={created_at=[[@45,175:175='c',<459>,1:175]], id=[[@52,200:200='c',<459>,1:200]]}}, setop=EXCEPT, interface={bucket=[{name=created_at, table_ref=c}], id=[{name=id, table_ref=c}]}, table_alias={c=events}}}}",
				"null");
	}

	@Test
	public void truncExtractR23CteTwoLayerRenameDateTruncPropagationV0Test() {
		SqlParserAccess parser = parseEndpoint("with base as (select date_trunc(day, e.created_at) as b1, e.id from events e), mid as (select b.b1 as bucket, b.id from base b) select m.bucket from mid m", "SQL");
		assertOutputs(parser,
				"{SQL={with={1={cte={select={1={alias=b1, datetime_function={function_name=date_trunc, level={canonical=DAY, text=day, form=KEYWORD}, value={column={name=created_at, table_ref=e}}}}, 2={column={name=id, table_ref=e}}}, from={table={alias=e, table=events}}}, alias=base}, 2={cte={select={1={column={name=b1, table_ref=b}, alias=bucket}, 2={column={name=id, table_ref=b}}}, from={table={alias=b, table=base}}}, alias=mid}}, query={select={1={column={name=bucket, table_ref=m}}}, from={table={alias=m, table=mid}}}}}",
				"[bucket]",
				"{}",
				"{events={created_at=[[@9,37:37='e',<459>,1:37]], id=[[@16,58:58='e',<459>,1:58]]}}",
				"{query0={id=[[@18,60:61='id',<459>,1:60], [@34,110:110='b',<459>,1:110]], b1=[[@14,54:55='b1',<459>,1:54], [@28,94:94='b',<459>,1:94]]}, query1={bucket=[[@32,102:107='bucket',<459>,1:102], [@42,135:135='m',<459>,1:135]], id=[[@36,112:113='id',<459>,1:112]]}, query2={bucket=[[@44,137:142='bucket',<459>,1:137]]}}",
				"{def_query2={context_list={base=query0, mid=query1, m=query1}, query_dictionary={bucket=[[@44,137:142='bucket',<459>,1:137]]}, def_query1={context_list={base=query0, b=query0}, query_dictionary={bucket=[[@32,102:107='bucket',<459>,1:102], [@42,135:135='m',<459>,1:135]], id=[[@36,112:113='id',<459>,1:112]]}, interface={bucket=[{name=b1, table_ref=b}], id=[{name=id, table_ref=b}]}, table_alias={b=query0, base=query0}}, def_query0={query_dictionary={id=[[@18,60:61='id',<459>,1:60], [@34,110:110='b',<459>,1:110]], b1=[[@14,54:55='b1',<459>,1:54], [@28,94:94='b',<459>,1:94]]}, table_dictionary={events={created_at=[[@9,37:37='e',<459>,1:37]], id=[[@16,58:58='e',<459>,1:58]]}}, interface={id=[{name=id, table_ref=e}], b1=[{name=created_at, table_ref=e}]}, table_alias={e=events}}, interface={bucket=[{name=bucket, table_ref=m}]}, table_alias={mid=query1, m=query1, base=query0}}}",
				"null");
	}

	@Test
	public void truncExtractR24CteThreeLayerRenameTruncPropagationV0Test() {
		SqlParserAccess parser = parseEndpoint("with q0 as (select trunc(e.created_at, day) as b1, e.id from events e), q1 as (select q.b1 as b2, q.id from q0 q), q2 as (select r.b2 as bucket, r.id from q1 r) select s.bucket from q2 s", "SQL");
		assertOutputs(parser,
				"{SQL={with={1={cte={select={1={alias=b1, datetime_function={function_name=trunc, level={canonical=DAY, text=day, form=KEYWORD}, value={column={name=created_at, table_ref=e}}}}, 2={column={name=id, table_ref=e}}}, from={table={alias=e, table=events}}}, alias=q0}, 2={cte={select={1={column={name=b1, table_ref=q}, alias=b2}, 2={column={name=id, table_ref=q}}}, from={table={alias=q, table=q0}}}, alias=q1}, 3={cte={select={1={column={name=b2, table_ref=r}, alias=bucket}, 2={column={name=id, table_ref=r}}}, from={table={alias=r, table=q1}}}, alias=q2}}, query={select={1={column={name=bucket, table_ref=s}}}, from={table={alias=s, table=q2}}}}}",
				"[bucket]",
				"{}",
				"{events={created_at=[[@7,25:25='e',<459>,1:25]], id=[[@16,51:51='e',<459>,1:51]]}}",
				"{query0={id=[[@18,53:54='id',<459>,1:53], [@34,98:98='q',<459>,1:98]], b1=[[@14,47:48='b1',<459>,1:47], [@28,86:86='q',<459>,1:86]]}, query1={b2=[[@32,94:95='b2',<459>,1:94], [@46,129:129='r',<459>,1:129]], id=[[@36,100:101='id',<459>,1:100], [@52,145:145='r',<459>,1:145]]}, query2={bucket=[[@50,137:142='bucket',<459>,1:137], [@60,168:168='s',<459>,1:168]], id=[[@54,147:148='id',<459>,1:147]]}, query3={bucket=[[@62,170:175='bucket',<459>,1:170]]}}",
				"{def_query3={context_list={q0=query0, q1=query1, q2=query2, s=query2}, query_dictionary={bucket=[[@62,170:175='bucket',<459>,1:170]]}, def_query1={context_list={q0=query0, q=query0}, query_dictionary={b2=[[@32,94:95='b2',<459>,1:94], [@46,129:129='r',<459>,1:129]], id=[[@36,100:101='id',<459>,1:100], [@52,145:145='r',<459>,1:145]]}, interface={b2=[{name=b1, table_ref=q}], id=[{name=id, table_ref=q}]}, table_alias={q=query0, q0=query0}}, def_query0={query_dictionary={id=[[@18,53:54='id',<459>,1:53], [@34,98:98='q',<459>,1:98]], b1=[[@14,47:48='b1',<459>,1:47], [@28,86:86='q',<459>,1:86]]}, table_dictionary={events={created_at=[[@7,25:25='e',<459>,1:25]], id=[[@16,51:51='e',<459>,1:51]]}}, interface={id=[{name=id, table_ref=e}], b1=[{name=created_at, table_ref=e}]}, table_alias={e=events}}, interface={bucket=[{name=bucket, table_ref=s}]}, table_alias={q1=query1, q2=query2, s=query2, q0=query0}, def_query2={context_list={q0=query0, q1=query1, r=query1}, query_dictionary={bucket=[[@50,137:142='bucket',<459>,1:137], [@60,168:168='s',<459>,1:168]], id=[[@54,147:148='id',<459>,1:147]]}, interface={bucket=[{name=b2, table_ref=r}], id=[{name=id, table_ref=r}]}, table_alias={q1=query1, r=query1, q0=query0}}}}",
				"null");
	}

	@Test
	public void truncExtractR25DerivedThenCteRenameExtractPropagationV0Test() {
		SqlParserAccess parser = parseEndpoint("with base as (select d.day_part as p1 from (select extract(day from e.created_at) as day_part from events e) d) select b.p1 from base b", "SQL");
		assertOutputs(parser,
				"{SQL={with={1={cte={select={1={column={name=day_part, table_ref=d}, alias=p1}}, from={table={alias=d, query={select={1={alias=day_part, datetime_function={function_name=extract, part_form=KEYWORD, part=day, source={column={name=created_at, table_ref=e}}}}}, from={table={alias=e, table=events}}}}}}, alias=base}}, query={select={1={column={name=p1, table_ref=b}}}, from={table={alias=b, table=base}}}}}",
				"[p1]",
				"{}",
				"{events={created_at=[[@17,68:68='e',<459>,1:68]]}}",
				"{query0={day_part=[[@22,85:92='day_part',<459>,1:85], [@5,21:21='d',<459>,1:21]]}, query1={p1=[[@9,35:36='p1',<459>,1:35], [@30,119:119='b',<459>,1:119]]}, query2={p1=[[@32,121:122='p1',<459>,1:121]]}}",
				"{def_query2={context_list={base=query1, b=query1}, query_dictionary={p1=[[@32,121:122='p1',<459>,1:121]]}, def_query1={query_dictionary={p1=[[@9,35:36='p1',<459>,1:35], [@30,119:119='b',<459>,1:119]]}, def_query0={query_dictionary={day_part=[[@22,85:92='day_part',<459>,1:85], [@5,21:21='d',<459>,1:21]]}, table_dictionary={events={created_at=[[@17,68:68='e',<459>,1:68]]}}, interface={day_part=[{name=created_at, table_ref=e}]}, table_alias={e=events}}, interface={p1=[{name=day_part, table_ref=d}]}, table_alias={d=query0}}, interface={p1=[{name=p1, table_ref=b}]}, table_alias={b=query1, base=query1}}}",
				"null");
	}

	@Test
	public void truncExtractR26CteDerivedSetOpDatePartPropagationV0Test() {
		SqlParserAccess parser = parseEndpoint("with q0 as (select date_part(day, e.created_at) as p1, e.id from events e), q1 as (select p.p1 as part_value, p.id from q0 p) select d.part_value, d.id from (select q.part_value, q.id from q1 q union all select q.part_value, q.id from q1 q) d", "SQL");
		assertOutputs(parser,
				"{SQL={with={1={cte={select={1={alias=p1, datetime_function={function_name=date_part, part_form=KEYWORD, part=day, source={column={name=created_at, table_ref=e}}}}, 2={column={name=id, table_ref=e}}}, from={table={alias=e, table=events}}}, alias=q0}, 2={cte={select={1={column={name=p1, table_ref=p}, alias=part_value}, 2={column={name=id, table_ref=p}}}, from={table={alias=p, table=q0}}}, alias=q1}}, query={select={1={column={name=part_value, table_ref=d}}, 2={column={name=id, table_ref=d}}}, from={table={alias=d, query={union={1={select={1={column={name=part_value, table_ref=q}}, 2={column={name=id, table_ref=q}}}, from={table={alias=q, table=q1}}}, 2={union={qualifier=all, operator=union}}, 3={select={1={column={name=part_value, table_ref=q}}, 2={column={name=id, table_ref=q}}}, from={table={alias=q, table=q1}}}}}}}}}}",
				"[part_value, id]",
				"{}",
				"{events={created_at=[[@9,34:34='e',<459>,1:34]], id=[[@16,55:55='e',<459>,1:55]]}}",
				"{union4={part_value=[[@42,133:133='d',<459>,1:133]], id=[[@46,147:147='d',<459>,1:147]]}, query5={part_value=[[@44,135:144='part_value',<459>,1:135]], id=[[@48,149:150='id',<459>,1:149]]}, query0={p1=[[@14,51:52='p1',<459>,1:51], [@28,90:90='p',<459>,1:90]], id=[[@18,57:58='id',<459>,1:57], [@34,110:110='p',<459>,1:110]]}, query1={part_value=[[@32,98:107='part_value',<459>,1:98], [@52,165:165='q',<459>,1:165], [@65,211:211='q',<459>,1:211]], id=[[@36,112:113='id',<459>,1:112], [@56,179:179='q',<459>,1:179], [@69,225:225='q',<459>,1:225]]}, query2={part_value=[[@54,167:176='part_value',<459>,1:167]], id=[[@58,181:182='id',<459>,1:181]]}, query3={part_value=[[@67,213:222='part_value',<459>,1:213]], id=[[@71,227:228='id',<459>,1:227]]}}",
				"{def_query5={context_list={q0=query0, q1=query1}, def_union4={context_list={q0=query0, q1=query1}, query_dictionary={part_value=[[@42,133:133='d',<459>,1:133]], id=[[@46,147:147='d',<459>,1:147]]}, interface={part_value=query_column, id=query_column}, table_alias={q1=query1, q0=query0}, def_query3={context_list={q0=query0, q1=query1, q=query1}, query_dictionary={part_value=[[@67,213:222='part_value',<459>,1:213]], id=[[@71,227:228='id',<459>,1:227]]}, setop=UNION, interface={part_value=[{name=part_value, table_ref=q}], id=[{name=id, table_ref=q}]}, table_alias={q1=query1, q=query1, q0=query0}}, def_query2={context_list={q0=query0, q1=query1, q=query1}, query_dictionary={part_value=[[@54,167:176='part_value',<459>,1:167]], id=[[@58,181:182='id',<459>,1:181]]}, interface={part_value=[{name=part_value, table_ref=q}], id=[{name=id, table_ref=q}]}, table_alias={q1=query1, q=query1, q0=query0}}}, query_dictionary={part_value=[[@44,135:144='part_value',<459>,1:135]], id=[[@48,149:150='id',<459>,1:149]]}, def_query1={context_list={q0=query0, p=query0}, query_dictionary={part_value=[[@32,98:107='part_value',<459>,1:98], [@52,165:165='q',<459>,1:165], [@65,211:211='q',<459>,1:211]], id=[[@36,112:113='id',<459>,1:112], [@56,179:179='q',<459>,1:179], [@69,225:225='q',<459>,1:225]]}, interface={part_value=[{name=p1, table_ref=p}], id=[{name=id, table_ref=p}]}, table_alias={p=query0, q0=query0}}, def_query0={query_dictionary={p1=[[@14,51:52='p1',<459>,1:51], [@28,90:90='p',<459>,1:90]], id=[[@18,57:58='id',<459>,1:57], [@34,110:110='p',<459>,1:110]]}, table_dictionary={events={created_at=[[@9,34:34='e',<459>,1:34]], id=[[@16,55:55='e',<459>,1:55]]}}, interface={p1=[{name=created_at, table_ref=e}], id=[{name=id, table_ref=e}]}, table_alias={e=events}}, interface={part_value=[{name=part_value, table_ref=d}], id=[{name=id, table_ref=d}]}, table_alias={q1=query1, d=union4, q0=query0}}}",
				"null");
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
				"{orders={order_date=[[@12,57:66='order_date',<459>,1:57]], ts=[[@5,33:34='ts',<459>,1:33]]}}",
				"{query0={unnamed_1=[[@13,67:67=')',<288>,1:67]], unnamed_0=[[@6,35:35=')',<288>,1:35]]}}",
				"{def_query0={query_dictionary={unnamed_1=[[@13,67:67=')',<288>,1:67]], unnamed_0=[[@6,35:35=')',<288>,1:35]]}, table_dictionary={orders={order_date=[[@12,57:66='order_date',<459>,1:57]], ts=[[@5,33:34='ts',<459>,1:33]]}}, interface={unnamed_1=[{name=order_date, table_ref=orders}], unnamed_0=[{name=ts, table_ref=orders}]}}}",
				"null");
	}
}
