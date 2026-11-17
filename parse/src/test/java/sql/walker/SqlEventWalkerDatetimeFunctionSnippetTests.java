package sql.walker;

import org.junit.Assert;
import org.junit.Test;

import access.Snippet;
import access.SqlParserAccess;
import errorhandling.ParseDiagnostic;

public class SqlEventWalkerDatetimeFunctionSnippetTests extends AbstractSqlParseEventWalkerTest {

	private SqlParserAccess parseSnippet(String sql, String endpoint) {
		SqlParserAccess parser = new SqlParserAccess(false, false, false);
		parser.executeTheParse(sql, endpoint);
		Assert.assertEquals("Syntax errors in " + endpoint, 0, parser.getParser().getNumberOfSyntaxErrors());
		return parser;
	}

	private void assertOutputs(Snippet snippet, String ast, String outputInterface, String substitutions,
			String tableDictionary, String queryDictionary, String symbolTable) {
		Assert.assertEquals("AST is wrong", ast, snippet.getSqlAbstractTree().toString());
		Assert.assertEquals("Interface is wrong", outputInterface, snippet.getQueryInterface().toString());
		Assert.assertEquals("Substitution List is wrong", substitutions, snippet.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", tableDictionary, snippet.getTableDictionary().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", queryDictionary, snippet.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", symbolTable, snippet.getSymbolTable().toString());
	}

	private void assertSingleSnowflakeDatetimeAddWarning(SqlParserAccess parser, int line, int charPosition) {
		Assert.assertEquals(parser.getAllDiagnostics().toString(), 1, parser.getAllDiagnostics().size());
		assertDiagnosticAtPosition(parser.getSnippet(), "STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
				ParseDiagnostic.Severity.WARNING,
				"snowflake_datetime_add_expression at (l:" + line + " c:" + charPosition
						+ ") is recognized as Snowflake-specific grammar.",
				null, line, charPosition);
	}

	@Test
	public void dateaddPredicandSnippet() {
		SqlParserAccess parser = parseSnippet("DATEADD(day, <increment>, <date value>)", "PREDICAND");
		assertSingleSnowflakeDatetimeAddWarning(parser, 1, 0);
		assertOutputs(parser.getSnippet(),
				"{PREDICAND={datetime_function={function_name=DATEADD, level={canonical=DAY, text=day, form=KEYWORD}, increment={substitution={name=<increment>, type=predicand}}, value={substitution={name=<date value>, type=predicand}}}}}",
				"[]", "{<date value>=predicand, <increment>=predicand}", "{}", "{}", "{}");
	}

	@Test
	public void dateaddConditionSnippet() {
		SqlParserAccess parser = parseSnippet("dateadd(day, <increment>, <date value>) > DATE '2026-01-01'", "CONDITION");
		assertSingleSnowflakeDatetimeAddWarning(parser, 1, 0);
		assertOutputs(parser.getSnippet(),
				"{CONDITION={condition={left={datetime_function={function_name=dateadd, level={canonical=DAY, text=day, form=KEYWORD}, increment={substitution={name=<increment>, type=predicand}}, value={substitution={name=<date value>, type=predicand}}}}, right={source_type=DATE, literal=2026-01-01}, operator=>}}}",
				"[]", "{<date value>=predicand, <increment>=predicand}", "{}", "{}", "{}");
	}

	@Test
	public void dateaddQuerySnippet() {
		SqlParserAccess parser = parseSnippet("select dateadd(day, 1, a.ts) as shifted from events a", "QUERY");
		assertSingleSnowflakeDatetimeAddWarning(parser, 1, 7);
		assertOutputs(parser.getSnippet(),
				"{QUERY={select={1={alias=shifted, datetime_function={function_name=dateadd, level={canonical=DAY, text=day, form=KEYWORD}, increment={literal=1}, value={column={name=ts, table_ref=a}}}}}, from={table={alias=a, table=events}}}}",
				"[shifted]", "{}", "{events={ts=[[@7,23:23='a',<433>,1:23]]}}",
				"{query0={shifted=[[@12,32:38='shifted',<433>,1:32]]}}",
				"{def_query0={query_dictionary={shifted=[[@12,32:38='shifted',<433>,1:32]]}, table_dictionary={events={ts=[[@7,23:23='a',<433>,1:23]]}}, interface={shifted=[{name=ts, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void dateaddTupleSnippet() {
		SqlParserAccess parser = parseSnippet("(select dateadd(day, 1, a.ts) as shifted from events a)", "TUPLE");
		assertSingleSnowflakeDatetimeAddWarning(parser, 1, 8);
		assertOutputs(parser.getSnippet(),
				"{TUPLE={from={table={alias=a, table=events}}, select={1={alias=shifted, datetime_function={function_name=dateadd, level={canonical=DAY, text=day, form=KEYWORD}, increment={literal=1}, value={column={name=ts, table_ref=a}}}}}}}",
				"[shifted]", "{}", "{events={ts=[[@8,24:24='a',<433>,1:24]]}}",
				"{query0={shifted=[[@13,33:39='shifted',<433>,1:33]]}}",
				"{def_query0={query_dictionary={shifted=[[@13,33:39='shifted',<433>,1:33]]}, table_dictionary={events={ts=[[@8,24:24='a',<433>,1:24]]}}, interface={shifted=[{name=ts, table_ref=a}]}, table_alias={a=events}}, table_alias={query0=query0}}");
	}

	@Test
	public void dateaddInListSnippet() {
		SqlParserAccess parser = parseSnippet("(dateadd(day, 1, DATE '2026-01-01'))", "IN_LIST");
		assertSingleSnowflakeDatetimeAddWarning(parser, 1, 1);
		assertOutputs(parser.getSnippet(),
				"{IN_LIST={list={1={datetime_function={function_name=dateadd, level={canonical=DAY, text=day, form=KEYWORD}, increment={literal=1}, value={source_type=DATE, literal=2026-01-01}}}}}}",
				"[]", "{}", "{}", "{}", "{}");
	}

	@Test
	public void dateaddJoinExtensionSnippet() {
		SqlParserAccess parser = parseSnippet("join accounts b on dateadd(day, 1, b.ts) > DATE '2026-01-01'", "JOIN_EXTENSION");
		assertSingleSnowflakeDatetimeAddWarning(parser, 1, 19);
		assertOutputs(parser.getSnippet(),
				"{JOIN_EXTENSION={1={join=join, on={condition={left={datetime_function={function_name=dateadd, level={canonical=DAY, text=day, form=KEYWORD}, increment={literal=1}, value={column={name=ts, table_ref=b}}}}, right={source_type=DATE, literal=2026-01-01}, operator=>}}}, 2={table={alias=b, table=accounts}}}}",
				"[]", "{}", "{accounts={ts=[[@10,35:35='b',<433>,1:35]]}}", "{}",
				"{table_dictionary={accounts={ts=[[@10,35:35='b',<433>,1:35]]}}, filters=[{name=ts, table_ref=b}], table_alias={b=accounts}}");
	}

	@Test
	public void dateaddValuesSnippet() {
		SqlParserAccess parser = parseSnippet("values (dateadd(day, 1, DATE '2026-01-01'))", "VALUES");
		assertSingleSnowflakeDatetimeAddWarning(parser, 1, 8);
		assertOutputs(parser.getSnippet(),
				"{VALUES={values={matrix={1={row={1={datetime_function={function_name=dateadd, level={canonical=DAY, text=day, form=KEYWORD}, increment={literal=1}, value={source_type=DATE, literal=2026-01-01}}}}}}}}}",
				"[$1]", "{}", "{}", "{values0={$1=[[@1,7:7='(',<287>,1:7]]}}",
				"{def_values0={query_dictionary={$1=[[@1,7:7='(',<287>,1:7]]}, interface={$1=[]}}}");
	}

	@Test
	public void dateaddInsertSnippet() {
		SqlParserAccess parser = parseSnippet("insert into target (shifted) select dateadd(day, 1, a.ts) as shifted from events a", "INSERT");
		assertSingleSnowflakeDatetimeAddWarning(parser, 1, 36);
		assertOutputs(parser.getSnippet(),
				"{INSERT={insert={preamble=insert_into, from={from={table={alias=a, table=events}}, select={1={alias=shifted, datetime_function={function_name=dateadd, level={canonical=DAY, text=day, form=KEYWORD}, increment={literal=1}, value={column={name=ts, table_ref=a}}}}}}, target_table={table={alias=null, table=target}}, columns={1={column={name=shifted, table_ref=null}}}}}}",
				"[shifted]", "{}", "{events={ts=[[@13,52:52='a',<433>,1:52]]}, target={shifted=[[@4,20:26='shifted',<433>,1:20]]}}",
				"{query0={shifted=[[@18,61:67='shifted',<433>,1:61]]}, insert1={shifted=[[@4,20:26='shifted',<433>,1:20]]}}",
				"{def_insert1={query_dictionary={shifted=[[@4,20:26='shifted',<433>,1:20]]}, table_dictionary={target={shifted=[[@4,20:26='shifted',<433>,1:20]]}}, def_query0={query_dictionary={shifted=[[@18,61:67='shifted',<433>,1:61]]}, table_dictionary={events={ts=[[@13,52:52='a',<433>,1:52]]}}, interface={shifted=[{name=ts, table_ref=a}]}, table_alias={a=events}}, interface={shifted=[{name=shifted, table_ref=query0}]}}}");
	}

	@Test
	public void dateaddUpdateSnippet() {
		SqlParserAccess parser = parseSnippet("update events set ts = dateadd(day, 1, ts)", "UPDATE");
		assertSingleSnowflakeDatetimeAddWarning(parser, 1, 23);
		assertOutputs(parser.getSnippet(),
				"{UPDATE={update={table={alias=null, table=events}, assignments={1={set={column={name=ts, table_ref=null}}, to={datetime_function={function_name=dateadd, level={canonical=DAY, text=day, form=KEYWORD}, increment={literal=1}, value={column={name=ts, table_ref=null}}}}}}}}}",
				"[ts]", "{}", "{events={ts=[[@11,39:40='ts',<433>,1:39]]}}",
				"{update0={ts=[[@3,18:19='ts',<433>,1:18]]}}",
				"{def_update0={assignments={ts=[{name=ts, table_ref=events}]}, table_dictionary={events={ts=[[@11,39:40='ts',<433>,1:39]]}}, update_dictionary={ts=[[@3,18:19='ts',<433>,1:18]]}, target_table={events={ts=[[@3,18:19='ts',<433>,1:18], [@11,39:40='ts',<433>,1:39], [@12,41:41=')',<288>,1:41]]}}, lhs_unresolved_columns={ts={column={name=ts, table_ref=null}, locations=[[@3,18:19='ts',<433>,1:18], [@11,39:40='ts',<433>,1:39], [@12,41:41=')',<288>,1:41]]}}}}");
	}

	@Test
	public void dateaddDeleteSnippet() {
		SqlParserAccess parser = parseSnippet("delete from events where dateadd(day, 1, ts) > DATE '2026-01-01'", "DELETE");
		assertSingleSnowflakeDatetimeAddWarning(parser, 1, 25);
		assertOutputs(parser.getSnippet(),
				"{DELETE={delete={table={alias=null, table=events}, where={condition={left={datetime_function={function_name=dateadd, level={canonical=DAY, text=day, form=KEYWORD}, increment={literal=1}, value={column={name=ts, table_ref=null}}}}, right={source_type=DATE, literal=2026-01-01}, operator=>}}}}}",
				"[]", "{}", "{events={ts=[[@10,41:42='ts',<433>,1:41]]}}", "{}",
				"{def_delete0={query_dictionary={}, table_dictionary={events={ts=[[@10,41:42='ts',<433>,1:41]]}}, filters=[{name=ts, table_ref=events}], interface=null}}");
	}

	@Test
	public void dateaddDdlSnippet() {
		SqlParserAccess parser = parseSnippet("create view v as select dateadd(day, 1, a.ts) as shifted from events a", "DDL");
		assertSingleSnowflakeDatetimeAddWarning(parser, 1, 24);
		assertOutputs(parser.getSnippet(),
				"{DDL={create={type=view, name={table=v}, query={select={1={alias=shifted, datetime_function={function_name=dateadd, level={canonical=DAY, text=day, form=KEYWORD}, increment={literal=1}, value={column={name=ts, table_ref=a}}}}}, from={table={alias=a, table=events}}}}}}",
				"[]", "{}", "{events={ts=[[@11,40:40='a',<433>,1:40]]}}",
				"{query0={shifted=[[@16,49:55='shifted',<433>,1:49]]}}",
				"{def_create1={def_query0={query_dictionary={shifted=[[@16,49:55='shifted',<433>,1:49]]}, table_dictionary={events={ts=[[@11,40:40='a',<433>,1:40]]}}, interface={shifted=[{name=ts, table_ref=a}]}, table_alias={a=events}}}}");
	}

	@Test
	public void dateaddScriptSnippet() {
		SqlParserAccess parser = parseSnippet("select dateadd(day, 1, DATE '2026-01-01') as shifted;", "SCRIPT");
		assertSingleSnowflakeDatetimeAddWarning(parser, 1, 7);
		assertOutputs(parser.getSnippet(),
				"{SCRIPT={1={select={1={alias=shifted, datetime_function={function_name=dateadd, level={canonical=DAY, text=day, form=KEYWORD}, increment={literal=1}, value={source_type=DATE, literal=2026-01-01}}}}}}}",
				"[]", "{SCRIPT={1={}}}", "{SCRIPT={1={}}}",
				"{SCRIPT={1={query0={shifted=[[@11,45:51='shifted',<433>,1:45]]}}}}",
				"{SCRIPT={1={def_query0={query_dictionary={shifted=[[@11,45:51='shifted',<433>,1:45]]}, interface={shifted=[]}}}}}");
	}

	@Test
	public void datediffPredicandSnippet() {
		SqlParserAccess parser = parseSnippet("DATEDIFF(day, <left value>, <right value>)", "PREDICAND");
		assertOutputs(parser.getSnippet(),
				"{PREDICAND={datetime_function={function_name=DATEDIFF, level={canonical=DAY, text=day, form=KEYWORD}, left={substitution={name=<left value>, type=predicand}}, right={substitution={name=<right value>, type=predicand}}}}}",
				"[]", "{<right value>=predicand, <left value>=predicand}", "{}", "{}", "{}");
	}

	@Test
	public void datediffConditionSnippet() {
		SqlParserAccess parser = parseSnippet("datediff(day, <left value>, <right value>) > 0", "CONDITION");
		assertOutputs(parser.getSnippet(),
				"{CONDITION={condition={left={datetime_function={function_name=datediff, level={canonical=DAY, text=day, form=KEYWORD}, left={substitution={name=<left value>, type=predicand}}, right={substitution={name=<right value>, type=predicand}}}}, right={literal=0}, operator=>}}}",
				"[]", "{<right value>=predicand, <left value>=predicand}", "{}", "{}", "{}");
	}

	@Test
	public void datediffQuerySnippet() {
		SqlParserAccess parser = parseSnippet("select datediff(day, a.start_ts, a.end_ts) as delta from events a", "QUERY");
		assertOutputs(parser.getSnippet(),
				"{QUERY={select={1={alias=delta, datetime_function={function_name=datediff, level={canonical=DAY, text=day, form=KEYWORD}, left={column={name=start_ts, table_ref=a}}, right={column={name=end_ts, table_ref=a}}}}}, from={table={alias=a, table=events}}}}",
				"[delta]", "{}", "{events={start_ts=[[@5,21:21='a',<433>,1:21]], end_ts=[[@9,33:33='a',<433>,1:33]]}}",
				"{query0={delta=[[@14,46:50='delta',<433>,1:46]]}}",
				"{def_query0={query_dictionary={delta=[[@14,46:50='delta',<433>,1:46]]}, table_dictionary={events={start_ts=[[@5,21:21='a',<433>,1:21]], end_ts=[[@9,33:33='a',<433>,1:33]]}}, interface={delta=[{name=start_ts, table_ref=a}, {name=end_ts, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void datediffTupleSnippet() {
		SqlParserAccess parser = parseSnippet("(select datediff(day, a.start_ts, a.end_ts) as delta from events a)", "TUPLE");
		assertOutputs(parser.getSnippet(),
				"{TUPLE={from={table={alias=a, table=events}}, select={1={alias=delta, datetime_function={function_name=datediff, level={canonical=DAY, text=day, form=KEYWORD}, left={column={name=start_ts, table_ref=a}}, right={column={name=end_ts, table_ref=a}}}}}}}",
				"[delta]", "{}", "{events={start_ts=[[@6,22:22='a',<433>,1:22]], end_ts=[[@10,34:34='a',<433>,1:34]]}}",
				"{query0={delta=[[@15,47:51='delta',<433>,1:47]]}}",
				"{def_query0={query_dictionary={delta=[[@15,47:51='delta',<433>,1:47]]}, table_dictionary={events={start_ts=[[@6,22:22='a',<433>,1:22]], end_ts=[[@10,34:34='a',<433>,1:34]]}}, interface={delta=[{name=start_ts, table_ref=a}, {name=end_ts, table_ref=a}]}, table_alias={a=events}}, table_alias={query0=query0}}");
	}

	@Test
	public void datediffInListSnippet() {
		SqlParserAccess parser = parseSnippet("(datediff(day, DATE '2026-01-01', DATE '2026-01-03'))", "IN_LIST");
		assertOutputs(parser.getSnippet(),
				"{IN_LIST={list={1={datetime_function={function_name=datediff, level={canonical=DAY, text=day, form=KEYWORD}, left={source_type=DATE, literal=2026-01-01}, right={source_type=DATE, literal=2026-01-03}}}}}}",
				"[]", "{}", "{}", "{}", "{}");
	}

	@Test
	public void datediffJoinExtensionSnippet() {
		SqlParserAccess parser = parseSnippet("join accounts b on datediff(day, b.start_ts, b.end_ts) > 0", "JOIN_EXTENSION");
		assertOutputs(parser.getSnippet(),
				"{JOIN_EXTENSION={1={join=join, on={condition={left={datetime_function={function_name=datediff, level={canonical=DAY, text=day, form=KEYWORD}, left={column={name=start_ts, table_ref=b}}, right={column={name=end_ts, table_ref=b}}}}, right={literal=0}, operator=>}}}, 2={table={alias=b, table=accounts}}}}",
				"[]", "{}", "{accounts={start_ts=[[@8,33:33='b',<433>,1:33]], end_ts=[[@12,45:45='b',<433>,1:45]]}}",
				"{}",
				"{table_dictionary={accounts={start_ts=[[@8,33:33='b',<433>,1:33]], end_ts=[[@12,45:45='b',<433>,1:45]]}}, filters=[{name=start_ts, table_ref=b}, {name=end_ts, table_ref=b}], table_alias={b=accounts}}");
	}

	@Test
	public void datediffValuesSnippet() {
		SqlParserAccess parser = parseSnippet("values (datediff(day, DATE '2026-01-01', DATE '2026-01-03'))", "VALUES");
		assertOutputs(parser.getSnippet(),
				"{VALUES={values={matrix={1={row={1={datetime_function={function_name=datediff, level={canonical=DAY, text=day, form=KEYWORD}, left={source_type=DATE, literal=2026-01-01}, right={source_type=DATE, literal=2026-01-03}}}}}}}}}",
				"[$1]", "{}", "{}", "{values0={$1=[[@1,7:7='(',<287>,1:7]]}}",
				"{def_values0={query_dictionary={$1=[[@1,7:7='(',<287>,1:7]]}, interface={$1=[]}}}");
	}

	@Test
	public void datediffInsertSnippet() {
		SqlParserAccess parser = parseSnippet("insert into target (delta) select datediff(day, a.start_ts, a.end_ts) as delta from events a", "INSERT");
		assertOutputs(parser.getSnippet(),
				"{INSERT={insert={preamble=insert_into, from={from={table={alias=a, table=events}}, select={1={alias=delta, datetime_function={function_name=datediff, level={canonical=DAY, text=day, form=KEYWORD}, left={column={name=start_ts, table_ref=a}}, right={column={name=end_ts, table_ref=a}}}}}}, target_table={table={alias=null, table=target}}, columns={1={column={name=delta, table_ref=null}}}}}}",
				"[delta]", "{}", "{events={start_ts=[[@11,48:48='a',<433>,1:48]], end_ts=[[@15,60:60='a',<433>,1:60]]}, target={delta=[[@4,20:24='delta',<433>,1:20]]}}",
				"{query0={delta=[[@20,73:77='delta',<433>,1:73]]}, insert1={delta=[[@4,20:24='delta',<433>,1:20]]}}",
				"{def_insert1={query_dictionary={delta=[[@4,20:24='delta',<433>,1:20]]}, table_dictionary={target={delta=[[@4,20:24='delta',<433>,1:20]]}}, def_query0={query_dictionary={delta=[[@20,73:77='delta',<433>,1:73]]}, table_dictionary={events={start_ts=[[@11,48:48='a',<433>,1:48]], end_ts=[[@15,60:60='a',<433>,1:60]]}}, interface={delta=[{name=start_ts, table_ref=a}, {name=end_ts, table_ref=a}]}, table_alias={a=events}}, interface={delta=[{name=delta, table_ref=query0}]}}}");
	}

	@Test
	public void datediffUpdateSnippet() {
		SqlParserAccess parser = parseSnippet("update events set delta = datediff(day, start_ts, end_ts)", "UPDATE");
		assertOutputs(parser.getSnippet(),
				"{UPDATE={update={table={alias=null, table=events}, assignments={1={set={column={name=delta, table_ref=null}}, to={datetime_function={function_name=datediff, level={canonical=DAY, text=day, form=KEYWORD}, left={column={name=start_ts, table_ref=null}}, right={column={name=end_ts, table_ref=null}}}}}}}}}",
				"[delta]", "{}", "{events={start_ts=[[@9,40:47='start_ts',<433>,1:40], [@12,56:56=')',<288>,1:56]], end_ts=[[@11,50:55='end_ts',<433>,1:50], [@12,56:56=')',<288>,1:56]]}}",
				"{update0={delta=[[@3,18:22='delta',<433>,1:18]]}}",
				"{def_update0={assignments={delta=[{name=start_ts, table_ref=events}, {name=end_ts, table_ref=events}]}, table_dictionary={events={start_ts=[[@9,40:47='start_ts',<433>,1:40], [@12,56:56=')',<288>,1:56]], end_ts=[[@11,50:55='end_ts',<433>,1:50], [@12,56:56=')',<288>,1:56]]}}, update_dictionary={delta=[[@3,18:22='delta',<433>,1:18]]}, target_table={events={delta=[[@3,18:22='delta',<433>,1:18]]}}, lhs_unresolved_columns={delta={column={name=delta, table_ref=null}, locations=[[@3,18:22='delta',<433>,1:18]]}}}}");
	}

	@Test
	public void datediffDeleteSnippet() {
		SqlParserAccess parser = parseSnippet("delete from events where datediff(day, start_ts, end_ts) > 0", "DELETE");
		assertOutputs(parser.getSnippet(),
				"{DELETE={delete={table={alias=null, table=events}, where={condition={left={datetime_function={function_name=datediff, level={canonical=DAY, text=day, form=KEYWORD}, left={column={name=start_ts, table_ref=null}}, right={column={name=end_ts, table_ref=null}}}}, right={literal=0}, operator=>}}}}}",
				"[]", "{}", "{events={start_ts=[[@8,39:46='start_ts',<433>,1:39]], end_ts=[[@10,49:54='end_ts',<433>,1:49]]}}",
				"{}",
				"{def_delete0={query_dictionary={}, table_dictionary={events={start_ts=[[@8,39:46='start_ts',<433>,1:39]], end_ts=[[@10,49:54='end_ts',<433>,1:49]]}}, filters=[{name=start_ts, table_ref=events}, {name=end_ts, table_ref=events}], interface=null}}");
	}

	@Test
	public void datediffDdlSnippet() {
		SqlParserAccess parser = parseSnippet("create view v as select datediff(day, a.start_ts, a.end_ts) as delta from events a", "DDL");
		assertOutputs(parser.getSnippet(),
				"{DDL={create={type=view, name={table=v}, query={select={1={alias=delta, datetime_function={function_name=datediff, level={canonical=DAY, text=day, form=KEYWORD}, left={column={name=start_ts, table_ref=a}}, right={column={name=end_ts, table_ref=a}}}}}, from={table={alias=a, table=events}}}}}}",
				"[]", "{}", "{events={start_ts=[[@9,38:38='a',<433>,1:38]], end_ts=[[@13,50:50='a',<433>,1:50]]}}",
				"{query0={delta=[[@18,63:67='delta',<433>,1:63]]}}",
				"{def_create1={def_query0={query_dictionary={delta=[[@18,63:67='delta',<433>,1:63]]}, table_dictionary={events={start_ts=[[@9,38:38='a',<433>,1:38]], end_ts=[[@13,50:50='a',<433>,1:50]]}}, interface={delta=[{name=start_ts, table_ref=a}, {name=end_ts, table_ref=a}]}, table_alias={a=events}}}}");
	}

	@Test
	public void datediffScriptSnippet() {
		SqlParserAccess parser = parseSnippet("select datediff(day, DATE '2026-01-01', DATE '2026-01-03') as delta;", "SCRIPT");
		assertOutputs(parser.getSnippet(),
				"{SCRIPT={1={select={1={alias=delta, datetime_function={function_name=datediff, level={canonical=DAY, text=day, form=KEYWORD}, left={source_type=DATE, literal=2026-01-01}, right={source_type=DATE, literal=2026-01-03}}}}}}}",
				"[]", "{SCRIPT={1={}}}", "{SCRIPT={1={}}}",
				"{SCRIPT={1={query0={delta=[[@12,62:66='delta',<433>,1:62]]}}}}",
				"{SCRIPT={1={def_query0={query_dictionary={delta=[[@12,62:66='delta',<433>,1:62]]}, interface={delta=[]}}}}}");
	}

	@Test
	public void timediffPredicandSnippet() {
		SqlParserAccess parser = parseSnippet("TIMEDIFF(hour, <left value>, <right value>)", "PREDICAND");
		assertOutputs(parser.getSnippet(),
				"{PREDICAND={datetime_function={function_name=TIMEDIFF, level={canonical=HOUR, text=hour, form=KEYWORD}, left={substitution={name=<left value>, type=predicand}}, right={substitution={name=<right value>, type=predicand}}}}}",
				"[]", "{<right value>=predicand, <left value>=predicand}", "{}", "{}", "{}");
	}

	@Test
	public void timestampdiffPredicandSnippet() {
		SqlParserAccess parser = parseSnippet("TIMESTAMPDIFF(hour, <left value>, <right value>)", "PREDICAND");
		assertOutputs(parser.getSnippet(),
				"{PREDICAND={datetime_function={function_name=TIMESTAMPDIFF, level={canonical=HOUR, text=hour, form=KEYWORD}, left={substitution={name=<left value>, type=predicand}}, right={substitution={name=<right value>, type=predicand}}}}}",
				"[]", "{<right value>=predicand, <left value>=predicand}", "{}", "{}", "{}");
	}

	@Test
	public void extractPredicandSnippet() {
		SqlParserAccess parser = parseSnippet("EXTRACT(day FROM <date value>)", "PREDICAND");
		assertOutputs(parser.getSnippet(),
				"{PREDICAND={datetime_function={function_name=EXTRACT, part_form=KEYWORD, part=day, source={substitution={name=<date value>, type=predicand}}}}}",
				"[]", "{<date value>=predicand}", "{}", "{}", "{}");
	}

	@Test
	public void datePartPredicandSnippet() {
		SqlParserAccess parser = parseSnippet("DATE_PART('day', <date value>)", "PREDICAND");
		assertOutputs(parser.getSnippet(),
				"{PREDICAND={datetime_function={function_name=DATE_PART, part_form=STRING, part=day, source={substitution={name=<date value>, type=predicand}}}}}",
				"[]", "{<date value>=predicand}", "{}", "{}", "{}");
	}

	@Test
	public void dateTruncPredicandSnippet() {
		SqlParserAccess parser = parseSnippet("DATE_TRUNC(day, <date value>)", "PREDICAND");
		assertOutputs(parser.getSnippet(),
				"{PREDICAND={datetime_function={function_name=DATE_TRUNC, level={canonical=DAY, text=day, form=KEYWORD}, value={substitution={name=<date value>, type=predicand}}}}}",
				"[]", "{<date value>=predicand}", "{}", "{}", "{}");
	}

	@Test
	public void timeSlicePredicandSnippet() {
		SqlParserAccess parser = parseSnippet("TIME_SLICE(<date value>, <slice length>, day)", "PREDICAND");
		assertOutputs(parser.getSnippet(),
				"{PREDICAND={datetime_function={function_name=TIME_SLICE, value={substitution={name=<date value>, type=predicand}}, slice_length={substitution={name=<slice length>, type=predicand}}, level={canonical=DAY, text=day, form=KEYWORD}}}}",
				"[]", "{<date value>=predicand, <slice length>=predicand}", "{}", "{}", "{}");
	}

	@Test
	public void lastDayPredicandSnippet() {
		SqlParserAccess parser = parseSnippet("LAST_DAY(<date value>, day)", "PREDICAND");
		assertOutputs(parser.getSnippet(),
				"{PREDICAND={datetime_function={function_name=LAST_DAY, value={substitution={name=<date value>, type=predicand}}, level={canonical=DAY, text=day, form=KEYWORD}}}}",
				"[]", "{<date value>=predicand}", "{}", "{}", "{}");
	}

	@Test
	public void nextDayPredicandSnippet() {
		SqlParserAccess parser = parseSnippet("NEXT_DAY(<date value>, friday)", "PREDICAND");
		assertOutputs(parser.getSnippet(),
				"{PREDICAND={datetime_function={function_name=NEXT_DAY, value={substitution={name=<date value>, type=predicand}}, weekday={canonical=FRIDAY, text=friday, form=KEYWORD}}}}",
				"[]", "{<date value>=predicand}", "{}", "{}", "{}");
	}

	@Test
	public void previousDayPredicandSnippet() {
		SqlParserAccess parser = parseSnippet("PREVIOUS_DAY(<date value>, friday)", "PREDICAND");
		assertOutputs(parser.getSnippet(),
				"{PREDICAND={datetime_function={function_name=PREVIOUS_DAY, value={substitution={name=<date value>, type=predicand}}, weekday={canonical=FRIDAY, text=friday, form=KEYWORD}}}}",
				"[]", "{<date value>=predicand}", "{}", "{}", "{}");
	}

	@Test
	public void dateBinPredicandSnippet() {
		SqlParserAccess parser = parseSnippet("DATE_BIN(<stride>, <source value>, <origin value>)", "PREDICAND");
		assertOutputs(parser.getSnippet(),
				"{PREDICAND={function={parameters={1={substitution={name=<stride>, type=predicand}}, 2={substitution={name=<source value>, type=predicand}}, 3={substitution={name=<origin value>, type=predicand}}}, function_name=DATE_BIN}}}",
				"[]", "{<stride>=predicand, <source value>=predicand, <origin value>=predicand}", "{}",
				"{}",
				"{}");
	}

	@Test
	public void dateBinConditionSnippet() {
		SqlParserAccess parser = parseSnippet("date_bin(INTERVAL '1 day', <source value>, <origin value>) > DATE '2026-01-01'", "CONDITION");
		assertOutputs(parser.getSnippet(),
				"{CONDITION={condition={left={function={parameters={1={source_type=INTERVAL, literal=1 day}, 2={substitution={name=<source value>, type=predicand}}, 3={substitution={name=<origin value>, type=predicand}}}, function_name=date_bin}}, right={source_type=DATE, literal=2026-01-01}, operator=>}}}",
				"[]", "{<source value>=predicand, <origin value>=predicand}", "{}",
				"{}",
				"{}");
	}

	@Test
	public void dateBinQuerySnippet() {
		SqlParserAccess parser = parseSnippet("select date_bin(INTERVAL '1 day', a.ts, a.origin_ts) as binned from events a", "QUERY");
		assertOutputs(parser.getSnippet(),
				"{QUERY={select={1={function={parameters={1={source_type=INTERVAL, literal=1 day}, 2={column={name=ts, table_ref=a}}, 3={column={name=origin_ts, table_ref=a}}}, function_name=date_bin}, alias=binned}}, from={table={alias=a, table=events}}}}",
				"[binned]", "{}", "{events={origin_ts=[[@10,40:40='a',<433>,1:40]], ts=[[@6,34:34='a',<433>,1:34]]}}",
				"{query0={binned=[[@15,56:61='binned',<433>,1:56]]}}",
				"{def_query0={query_dictionary={binned=[[@15,56:61='binned',<433>,1:56]]}, table_dictionary={events={origin_ts=[[@10,40:40='a',<433>,1:40]], ts=[[@6,34:34='a',<433>,1:34]]}}, interface={binned=[{name=ts, table_ref=a}, {name=origin_ts, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void dateBinTupleSnippet() {
		SqlParserAccess parser = parseSnippet("(select date_bin(INTERVAL '1 day', a.ts, a.origin_ts) as binned from events a)", "TUPLE");
		assertOutputs(parser.getSnippet(),
				"{TUPLE={from={table={alias=a, table=events}}, select={1={function={parameters={1={source_type=INTERVAL, literal=1 day}, 2={column={name=ts, table_ref=a}}, 3={column={name=origin_ts, table_ref=a}}}, function_name=date_bin}, alias=binned}}}}",
				"[binned]", "{}", "{events={origin_ts=[[@11,41:41='a',<433>,1:41]], ts=[[@7,35:35='a',<433>,1:35]]}}",
				"{query0={binned=[[@16,57:62='binned',<433>,1:57]]}}",
				"{def_query0={query_dictionary={binned=[[@16,57:62='binned',<433>,1:57]]}, table_dictionary={events={origin_ts=[[@11,41:41='a',<433>,1:41]], ts=[[@7,35:35='a',<433>,1:35]]}}, interface={binned=[{name=ts, table_ref=a}, {name=origin_ts, table_ref=a}]}, table_alias={a=events}}, table_alias={query0=query0}}");
	}

	@Test
	public void dateBinInListSnippet() {
		SqlParserAccess parser = parseSnippet("(date_bin(INTERVAL '1 day', DATE '2026-01-02', DATE '2026-01-01'))", "IN_LIST");
		assertOutputs(parser.getSnippet(),
				"{IN_LIST={list={1={function={parameters={1={source_type=INTERVAL, literal=1 day}, 2={source_type=DATE, literal=2026-01-02}, 3={source_type=DATE, literal=2026-01-01}}, function_name=date_bin}}}}}",
				"[]", "{}", "{}",
				"{}",
				"{}");
	}

	@Test
	public void dateBinJoinExtensionSnippet() {
		SqlParserAccess parser = parseSnippet("join accounts b on date_bin(INTERVAL '1 day', b.ts, b.origin_ts) > DATE '2026-01-01'", "JOIN_EXTENSION");
		assertOutputs(parser.getSnippet(),
				"{JOIN_EXTENSION={1={join=join, on={condition={left={function={parameters={1={source_type=INTERVAL, literal=1 day}, 2={column={name=ts, table_ref=b}}, 3={column={name=origin_ts, table_ref=b}}}, function_name=date_bin}}, right={source_type=DATE, literal=2026-01-01}, operator=>}}}, 2={table={alias=b, table=accounts}}}}",
				"[]", "{}", "{accounts={origin_ts=[[@13,52:52='b',<433>,1:52]], ts=[[@9,46:46='b',<433>,1:46]]}}",
				"{}",
				"{table_dictionary={accounts={origin_ts=[[@13,52:52='b',<433>,1:52]], ts=[[@9,46:46='b',<433>,1:46]]}}, filters=[{name=ts, table_ref=b}, {name=origin_ts, table_ref=b}], table_alias={b=accounts}}");
	}

	@Test
	public void dateBinValuesSnippet() {
		SqlParserAccess parser = parseSnippet("values (date_bin(INTERVAL '1 day', DATE '2026-01-02', DATE '2026-01-01'))", "VALUES");
		assertOutputs(parser.getSnippet(),
				"{VALUES={values={matrix={1={row={1={function={parameters={1={source_type=INTERVAL, literal=1 day}, 2={source_type=DATE, literal=2026-01-02}, 3={source_type=DATE, literal=2026-01-01}}, function_name=date_bin}}}}}}}}",
				"[$1]", "{}", "{}",
				"{values0={$1=[[@1,7:7='(',<287>,1:7]]}}",
				"{def_values0={query_dictionary={$1=[[@1,7:7='(',<287>,1:7]]}, interface={$1=[]}}}");
	}

	@Test
	public void dateBinInsertSnippet() {
		SqlParserAccess parser = parseSnippet("insert into target (binned) select date_bin(INTERVAL '1 day', a.ts, a.origin_ts) as binned from events a", "INSERT");
		assertOutputs(parser.getSnippet(),
				"{INSERT={insert={preamble=insert_into, from={from={table={alias=a, table=events}}, select={1={function={parameters={1={source_type=INTERVAL, literal=1 day}, 2={column={name=ts, table_ref=a}}, 3={column={name=origin_ts, table_ref=a}}}, function_name=date_bin}, alias=binned}}}, target_table={table={alias=null, table=target}}, columns={1={column={name=binned, table_ref=null}}}}}}",
				"[binned]", "{}", "{events={origin_ts=[[@16,68:68='a',<433>,1:68]], ts=[[@12,62:62='a',<433>,1:62]]}, target={binned=[[@4,20:25='binned',<433>,1:20]]}}",
				"{query0={binned=[[@21,84:89='binned',<433>,1:84]]}, insert1={binned=[[@4,20:25='binned',<433>,1:20]]}}",
				"{def_insert1={query_dictionary={binned=[[@4,20:25='binned',<433>,1:20]]}, table_dictionary={target={binned=[[@4,20:25='binned',<433>,1:20]]}}, def_query0={query_dictionary={binned=[[@21,84:89='binned',<433>,1:84]]}, table_dictionary={events={origin_ts=[[@16,68:68='a',<433>,1:68]], ts=[[@12,62:62='a',<433>,1:62]]}}, interface={binned=[{name=ts, table_ref=a}, {name=origin_ts, table_ref=a}]}, table_alias={a=events}}, interface={binned=[{name=binned, table_ref=query0}]}}}");
	}

	@Test
	public void dateBinUpdateSnippet() {
		SqlParserAccess parser = parseSnippet("update events set binned_ts = date_bin(INTERVAL '1 day', ts, origin_ts)", "UPDATE");
		assertOutputs(parser.getSnippet(),
				"{UPDATE={update={table={alias=null, table=events}, assignments={1={set={column={name=binned_ts, table_ref=null}}, to={function={parameters={1={source_type=INTERVAL, literal=1 day}, 2={column={name=ts, table_ref=null}}, 3={column={name=origin_ts, table_ref=null}}}, function_name=date_bin}}}}}}}",
				"[binned_ts]", "{}", "{events={origin_ts=[[@12,61:69='origin_ts',<433>,1:61], [@13,70:70=')',<288>,1:70]], ts=[[@10,57:58='ts',<433>,1:57], [@13,70:70=')',<288>,1:70]]}}",
				"{update0={binned_ts=[[@3,18:26='binned_ts',<433>,1:18]]}}",
				"{def_update0={assignments={binned_ts=[{name=ts, table_ref=events}, {name=origin_ts, table_ref=events}]}, table_dictionary={events={origin_ts=[[@12,61:69='origin_ts',<433>,1:61], [@13,70:70=')',<288>,1:70]], ts=[[@10,57:58='ts',<433>,1:57], [@13,70:70=')',<288>,1:70]]}}, update_dictionary={binned_ts=[[@3,18:26='binned_ts',<433>,1:18]]}, target_table={events={binned_ts=[[@3,18:26='binned_ts',<433>,1:18]]}}, lhs_unresolved_columns={binned_ts={column={name=binned_ts, table_ref=null}, locations=[[@3,18:26='binned_ts',<433>,1:18]]}}}}");
	}

	@Test
	public void dateBinDeleteSnippet() {
		SqlParserAccess parser = parseSnippet("delete from events where date_bin(INTERVAL '1 day', ts, origin_ts) > DATE '2026-01-01'", "DELETE");
		assertOutputs(parser.getSnippet(),
				"{DELETE={delete={table={alias=null, table=events}, where={condition={left={function={parameters={1={source_type=INTERVAL, literal=1 day}, 2={column={name=ts, table_ref=null}}, 3={column={name=origin_ts, table_ref=null}}}, function_name=date_bin}}, right={source_type=DATE, literal=2026-01-01}, operator=>}}}}}",
				"[]", "{}", "{events={origin_ts=[[@11,56:64='origin_ts',<433>,1:56]], ts=[[@9,52:53='ts',<433>,1:52]]}}",
				"{}",
				"{def_delete0={query_dictionary={}, table_dictionary={events={origin_ts=[[@11,56:64='origin_ts',<433>,1:56]], ts=[[@9,52:53='ts',<433>,1:52]]}}, filters=[{name=ts, table_ref=events}, {name=origin_ts, table_ref=events}], interface=null}}");
	}

	@Test
	public void dateBinDdlSnippet() {
		SqlParserAccess parser = parseSnippet("create view v as select date_bin(INTERVAL '1 day', a.ts, a.origin_ts) as binned from events a", "DDL");
		assertOutputs(parser.getSnippet(),
				"{DDL={create={type=view, name={table=v}, query={select={1={function={parameters={1={source_type=INTERVAL, literal=1 day}, 2={column={name=ts, table_ref=a}}, 3={column={name=origin_ts, table_ref=a}}}, function_name=date_bin}, alias=binned}}, from={table={alias=a, table=events}}}}}}",
				"[]", "{}", "{events={origin_ts=[[@14,57:57='a',<433>,1:57]], ts=[[@10,51:51='a',<433>,1:51]]}}",
				"{query0={binned=[[@19,73:78='binned',<433>,1:73]]}}",
				"{def_create1={def_query0={query_dictionary={binned=[[@19,73:78='binned',<433>,1:73]]}, table_dictionary={events={origin_ts=[[@14,57:57='a',<433>,1:57]], ts=[[@10,51:51='a',<433>,1:51]]}}, interface={binned=[{name=ts, table_ref=a}, {name=origin_ts, table_ref=a}]}, table_alias={a=events}}}}");
	}

	@Test
	public void dateBinScriptSnippet() {
		SqlParserAccess parser = parseSnippet("select date_bin(INTERVAL '1 day', DATE '2026-01-02', DATE '2026-01-01') as binned;", "SCRIPT");
		assertOutputs(parser.getSnippet(),
				"{SCRIPT={1={select={1={function={parameters={1={source_type=INTERVAL, literal=1 day}, 2={source_type=DATE, literal=2026-01-02}, 3={source_type=DATE, literal=2026-01-01}}, function_name=date_bin}, alias=binned}}}}}",
				"[]", "{SCRIPT={1={}}}", "{SCRIPT={1={}}}",
				"{SCRIPT={1={query0={binned=[[@13,75:80='binned',<433>,1:75]]}}}}",
				"{SCRIPT={1={def_query0={query_dictionary={binned=[[@13,75:80='binned',<433>,1:75]]}, interface={binned=[]}}}}}");
	}

	@Test
	public void agePredicandSnippet() {
		SqlParserAccess parser = parseSnippet("AGE(<left value>, <right value>)", "PREDICAND");
		assertOutputs(parser.getSnippet(),
				"{PREDICAND={function={parameters={1={substitution={name=<left value>, type=predicand}}, 2={substitution={name=<right value>, type=predicand}}}, function_name=AGE}}}",
				"[]", "{<right value>=predicand, <left value>=predicand}", "{}",
				"{}",
				"{}");
	}

	@Test
	public void ageConditionSnippet() {
		SqlParserAccess parser = parseSnippet("age(<left value>, <right value>) is not null", "CONDITION");
		assertOutputs(parser.getSnippet(),
				"{CONDITION={condition={left={function={parameters={1={substitution={name=<left value>, type=predicand}}, 2={substitution={name=<right value>, type=predicand}}}, function_name=age}}, operator=is not null}}}",
				"[]", "{<right value>=predicand, <left value>=predicand}", "{}",
				"{}",
				"{}");
	}

	@Test
	public void ageQuerySnippet() {
		SqlParserAccess parser = parseSnippet("select age(a.end_ts, a.start_ts) as age_delta from events a", "QUERY");
		assertOutputs(parser.getSnippet(),
				"{QUERY={select={1={function={parameters={1={column={name=end_ts, table_ref=a}}, 2={column={name=start_ts, table_ref=a}}}, function_name=age}, alias=age_delta}}, from={table={alias=a, table=events}}}}",
				"[age_delta]", "{}", "{events={start_ts=[[@7,21:21='a',<433>,1:21]], end_ts=[[@3,11:11='a',<433>,1:11]]}}",
				"{query0={age_delta=[[@12,36:44='age_delta',<433>,1:36]]}}",
				"{def_query0={query_dictionary={age_delta=[[@12,36:44='age_delta',<433>,1:36]]}, table_dictionary={events={start_ts=[[@7,21:21='a',<433>,1:21]], end_ts=[[@3,11:11='a',<433>,1:11]]}}, interface={age_delta=[{name=end_ts, table_ref=a}, {name=start_ts, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void ageTupleSnippet() {
		SqlParserAccess parser = parseSnippet("(select age(a.end_ts, a.start_ts) as age_delta from events a)", "TUPLE");
		assertOutputs(parser.getSnippet(),
				"{TUPLE={from={table={alias=a, table=events}}, select={1={function={parameters={1={column={name=end_ts, table_ref=a}}, 2={column={name=start_ts, table_ref=a}}}, function_name=age}, alias=age_delta}}}}",
				"[age_delta]", "{}", "{events={start_ts=[[@8,22:22='a',<433>,1:22]], end_ts=[[@4,12:12='a',<433>,1:12]]}}",
				"{query0={age_delta=[[@13,37:45='age_delta',<433>,1:37]]}}",
				"{def_query0={query_dictionary={age_delta=[[@13,37:45='age_delta',<433>,1:37]]}, table_dictionary={events={start_ts=[[@8,22:22='a',<433>,1:22]], end_ts=[[@4,12:12='a',<433>,1:12]]}}, interface={age_delta=[{name=end_ts, table_ref=a}, {name=start_ts, table_ref=a}]}, table_alias={a=events}}, table_alias={query0=query0}}");
	}

	@Test
	public void ageInListSnippet() {
		SqlParserAccess parser = parseSnippet("(age(TIMESTAMP '2026-01-03 00:00:00', TIMESTAMP '2026-01-01 00:00:00'))", "IN_LIST");
		assertOutputs(parser.getSnippet(),
				"{IN_LIST={list={1={function={parameters={1={source_type=TIMESTAMP, literal=2026-01-03 00:00:00}, 2={source_type=TIMESTAMP, literal=2026-01-01 00:00:00}}, function_name=age}}}}}",
				"[]", "{}", "{}",
				"{}",
				"{}");
	}

	@Test
	public void ageJoinExtensionSnippet() {
		SqlParserAccess parser = parseSnippet("join accounts b on age(b.end_ts, b.start_ts) is not null", "JOIN_EXTENSION");
		assertOutputs(parser.getSnippet(),
				"{JOIN_EXTENSION={1={join=join, on={condition={left={function={parameters={1={column={name=end_ts, table_ref=b}}, 2={column={name=start_ts, table_ref=b}}}, function_name=age}}, operator=is not null}}}, 2={table={alias=b, table=accounts}}}}",
				"[]", "{}", "{accounts={start_ts=[[@10,33:33='b',<433>,1:33]], end_ts=[[@6,23:23='b',<433>,1:23]]}}",
				"{}",
				"{table_dictionary={accounts={start_ts=[[@10,33:33='b',<433>,1:33]], end_ts=[[@6,23:23='b',<433>,1:23]]}}, filters=[{name=end_ts, table_ref=b}, {name=start_ts, table_ref=b}], table_alias={b=accounts}}");
	}

	@Test
	public void ageValuesSnippet() {
		SqlParserAccess parser = parseSnippet("values (age(TIMESTAMP '2026-01-03 00:00:00', TIMESTAMP '2026-01-01 00:00:00'))", "VALUES");
		assertOutputs(parser.getSnippet(),
				"{VALUES={values={matrix={1={row={1={function={parameters={1={source_type=TIMESTAMP, literal=2026-01-03 00:00:00}, 2={source_type=TIMESTAMP, literal=2026-01-01 00:00:00}}, function_name=age}}}}}}}}",
				"[$1]", "{}", "{}",
				"{values0={$1=[[@1,7:7='(',<287>,1:7]]}}",
				"{def_values0={query_dictionary={$1=[[@1,7:7='(',<287>,1:7]]}, interface={$1=[]}}}");
	}

	@Test
	public void ageInsertSnippet() {
		SqlParserAccess parser = parseSnippet("insert into target (age_delta) select age(a.end_ts, a.start_ts) as age_delta from events a", "INSERT");
		assertOutputs(parser.getSnippet(),
				"{INSERT={insert={preamble=insert_into, from={from={table={alias=a, table=events}}, select={1={function={parameters={1={column={name=end_ts, table_ref=a}}, 2={column={name=start_ts, table_ref=a}}}, function_name=age}, alias=age_delta}}}, target_table={table={alias=null, table=target}}, columns={1={column={name=age_delta, table_ref=null}}}}}}",
				"[age_delta]", "{}", "{events={start_ts=[[@13,52:52='a',<433>,1:52]], end_ts=[[@9,42:42='a',<433>,1:42]]}, target={age_delta=[[@4,20:28='age_delta',<433>,1:20]]}}",
				"{query0={age_delta=[[@18,67:75='age_delta',<433>,1:67]]}, insert1={age_delta=[[@4,20:28='age_delta',<433>,1:20]]}}",
				"{def_insert1={query_dictionary={age_delta=[[@4,20:28='age_delta',<433>,1:20]]}, table_dictionary={target={age_delta=[[@4,20:28='age_delta',<433>,1:20]]}}, def_query0={query_dictionary={age_delta=[[@18,67:75='age_delta',<433>,1:67]]}, table_dictionary={events={start_ts=[[@13,52:52='a',<433>,1:52]], end_ts=[[@9,42:42='a',<433>,1:42]]}}, interface={age_delta=[{name=end_ts, table_ref=a}, {name=start_ts, table_ref=a}]}, table_alias={a=events}}, interface={age_delta=[{name=age_delta, table_ref=query0}]}}}");
	}

	@Test
	public void ageUpdateSnippet() {
		SqlParserAccess parser = parseSnippet("update events set age_delta = age(end_ts, start_ts)", "UPDATE");
		assertOutputs(parser.getSnippet(),
				"{UPDATE={update={table={alias=null, table=events}, assignments={1={set={column={name=age_delta, table_ref=null}}, to={function={parameters={1={column={name=end_ts, table_ref=null}}, 2={column={name=start_ts, table_ref=null}}}, function_name=age}}}}}}}",
				"[age_delta]", "{}", "{events={start_ts=[[@9,42:49='start_ts',<433>,1:42], [@10,50:50=')',<288>,1:50]], end_ts=[[@7,34:39='end_ts',<433>,1:34], [@10,50:50=')',<288>,1:50]]}}",
				"{update0={age_delta=[[@3,18:26='age_delta',<433>,1:18]]}}",
				"{def_update0={assignments={age_delta=[{name=end_ts, table_ref=events}, {name=start_ts, table_ref=events}]}, table_dictionary={events={start_ts=[[@9,42:49='start_ts',<433>,1:42], [@10,50:50=')',<288>,1:50]], end_ts=[[@7,34:39='end_ts',<433>,1:34], [@10,50:50=')',<288>,1:50]]}}, update_dictionary={age_delta=[[@3,18:26='age_delta',<433>,1:18]]}, target_table={events={age_delta=[[@3,18:26='age_delta',<433>,1:18]]}}, lhs_unresolved_columns={age_delta={column={name=age_delta, table_ref=null}, locations=[[@3,18:26='age_delta',<433>,1:18]]}}}}");
	}

	@Test
	public void ageDeleteSnippet() {
		SqlParserAccess parser = parseSnippet("delete from events where age(end_ts, start_ts) is not null", "DELETE");
		assertOutputs(parser.getSnippet(),
				"{DELETE={delete={table={alias=null, table=events}, where={condition={left={function={parameters={1={column={name=end_ts, table_ref=null}}, 2={column={name=start_ts, table_ref=null}}}, function_name=age}}, operator=is not null}}}}}",
				"[]", "{}", "{events={start_ts=[[@8,37:44='start_ts',<433>,1:37]], end_ts=[[@6,29:34='end_ts',<433>,1:29]]}}",
				"{}",
				"{def_delete0={query_dictionary={}, table_dictionary={events={start_ts=[[@8,37:44='start_ts',<433>,1:37]], end_ts=[[@6,29:34='end_ts',<433>,1:29]]}}, filters=[{name=end_ts, table_ref=events}, {name=start_ts, table_ref=events}], interface=null}}");
	}

	@Test
	public void ageDdlSnippet() {
		SqlParserAccess parser = parseSnippet("create view v as select age(a.end_ts, a.start_ts) as age_delta from events a", "DDL");
		assertOutputs(parser.getSnippet(),
				"{DDL={create={type=view, name={table=v}, query={select={1={function={parameters={1={column={name=end_ts, table_ref=a}}, 2={column={name=start_ts, table_ref=a}}}, function_name=age}, alias=age_delta}}, from={table={alias=a, table=events}}}}}}",
				"[]", "{}", "{events={start_ts=[[@11,38:38='a',<433>,1:38]], end_ts=[[@7,28:28='a',<433>,1:28]]}}",
				"{query0={age_delta=[[@16,53:61='age_delta',<433>,1:53]]}}",
				"{def_create1={def_query0={query_dictionary={age_delta=[[@16,53:61='age_delta',<433>,1:53]]}, table_dictionary={events={start_ts=[[@11,38:38='a',<433>,1:38]], end_ts=[[@7,28:28='a',<433>,1:28]]}}, interface={age_delta=[{name=end_ts, table_ref=a}, {name=start_ts, table_ref=a}]}, table_alias={a=events}}}}");
	}

	@Test
	public void ageScriptSnippet() {
		SqlParserAccess parser = parseSnippet("select age(TIMESTAMP '2026-01-03 00:00:00', TIMESTAMP '2026-01-01 00:00:00') as age_delta;", "SCRIPT");
		assertOutputs(parser.getSnippet(),
				"{SCRIPT={1={select={1={function={parameters={1={source_type=TIMESTAMP, literal=2026-01-03 00:00:00}, 2={source_type=TIMESTAMP, literal=2026-01-01 00:00:00}}, function_name=age}, alias=age_delta}}}}}",
				"[]", "{SCRIPT={1={}}}", "{SCRIPT={1={}}}",
				"{SCRIPT={1={query0={age_delta=[[@10,80:88='age_delta',<433>,1:80]]}}}}",
				"{SCRIPT={1={def_query0={query_dictionary={age_delta=[[@10,80:88='age_delta',<433>,1:80]]}, interface={age_delta=[]}}}}}");
	}

	@Test
	public void makeTimestampPredicandSnippet() {
		SqlParserAccess parser = parseSnippet("MAKE_TIMESTAMP(<year>, <month>, <day>, <hour>, <minute>, <second>)", "PREDICAND");
		assertOutputs(parser.getSnippet(),
				"{PREDICAND={function={parameters={1={substitution={name=<year>, type=predicand}}, 2={substitution={name=<month>, type=predicand}}, 3={substitution={name=<day>, type=predicand}}, 4={substitution={name=<hour>, type=predicand}}, 5={substitution={name=<minute>, type=predicand}}, 6={substitution={name=<second>, type=predicand}}}, function_name=MAKE_TIMESTAMP}}}",
				"[]", "{<minute>=predicand, <day>=predicand, <month>=predicand, <second>=predicand, <year>=predicand, <hour>=predicand}", "{}",
				"{}",
				"{}");
	}

	@Test
	public void makeTimestampConditionSnippet() {
		SqlParserAccess parser = parseSnippet("make_timestamp(2026, 1, 2, 3, 4, 5) > TIMESTAMP '2026-01-01 00:00:00'", "CONDITION");
		assertOutputs(parser.getSnippet(),
				"{CONDITION={condition={left={function={parameters={1={literal=2026}, 2={literal=1}, 3={literal=2}, 4={literal=3}, 5={literal=4}, 6={literal=5}}, function_name=make_timestamp}}, right={source_type=TIMESTAMP, literal=2026-01-01 00:00:00}, operator=>}}}",
				"[]", "{}", "{}",
				"{}",
				"{}");
	}

	@Test
	public void makeTimestampQuerySnippet() {
		SqlParserAccess parser = parseSnippet("select make_timestamp(a.y, a.m, a.d, a.h, a.mi, a.s) as made_ts from events a", "QUERY");
		assertOutputs(parser.getSnippet(),
				"{QUERY={select={1={function={parameters={1={column={name=y, table_ref=a}}, 2={column={name=m, table_ref=a}}, 3={column={name=d, table_ref=a}}, 4={column={name=h, table_ref=a}}, 5={column={name=mi, table_ref=a}}, 6={column={name=s, table_ref=a}}}, function_name=make_timestamp}, alias=made_ts}}, from={table={alias=a, table=events}}}}",
				"[made_ts]", "{}", "{events={s=[[@23,48:48='a',<433>,1:48]], d=[[@11,32:32='a',<433>,1:32]], h=[[@15,37:37='a',<433>,1:37]], y=[[@3,22:22='a',<433>,1:22]], mi=[[@19,42:42='a',<433>,1:42]], m=[[@7,27:27='a',<433>,1:27]]}}",
				"{query0={made_ts=[[@28,56:62='made_ts',<433>,1:56]]}}",
				"{def_query0={query_dictionary={made_ts=[[@28,56:62='made_ts',<433>,1:56]]}, table_dictionary={events={s=[[@23,48:48='a',<433>,1:48]], d=[[@11,32:32='a',<433>,1:32]], h=[[@15,37:37='a',<433>,1:37]], y=[[@3,22:22='a',<433>,1:22]], mi=[[@19,42:42='a',<433>,1:42]], m=[[@7,27:27='a',<433>,1:27]]}}, interface={made_ts=[{name=y, table_ref=a}, {name=m, table_ref=a}, {name=d, table_ref=a}, {name=h, table_ref=a}, {name=mi, table_ref=a}, {name=s, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void makeTimestampTupleSnippet() {
		SqlParserAccess parser = parseSnippet("(select make_timestamp(a.y, a.m, a.d, a.h, a.mi, a.s) as made_ts from events a)", "TUPLE");
		assertOutputs(parser.getSnippet(),
				"{TUPLE={from={table={alias=a, table=events}}, select={1={function={parameters={1={column={name=y, table_ref=a}}, 2={column={name=m, table_ref=a}}, 3={column={name=d, table_ref=a}}, 4={column={name=h, table_ref=a}}, 5={column={name=mi, table_ref=a}}, 6={column={name=s, table_ref=a}}}, function_name=make_timestamp}, alias=made_ts}}}}",
				"[made_ts]", "{}", "{events={s=[[@24,49:49='a',<433>,1:49]], d=[[@12,33:33='a',<433>,1:33]], h=[[@16,38:38='a',<433>,1:38]], y=[[@4,23:23='a',<433>,1:23]], mi=[[@20,43:43='a',<433>,1:43]], m=[[@8,28:28='a',<433>,1:28]]}}",
				"{query0={made_ts=[[@29,57:63='made_ts',<433>,1:57]]}}",
				"{def_query0={query_dictionary={made_ts=[[@29,57:63='made_ts',<433>,1:57]]}, table_dictionary={events={s=[[@24,49:49='a',<433>,1:49]], d=[[@12,33:33='a',<433>,1:33]], h=[[@16,38:38='a',<433>,1:38]], y=[[@4,23:23='a',<433>,1:23]], mi=[[@20,43:43='a',<433>,1:43]], m=[[@8,28:28='a',<433>,1:28]]}}, interface={made_ts=[{name=y, table_ref=a}, {name=m, table_ref=a}, {name=d, table_ref=a}, {name=h, table_ref=a}, {name=mi, table_ref=a}, {name=s, table_ref=a}]}, table_alias={a=events}}, table_alias={query0=query0}}");
	}

	@Test
	public void makeTimestampInListSnippet() {
		SqlParserAccess parser = parseSnippet("(make_timestamp(2026, 1, 2, 3, 4, 5))", "IN_LIST");
		assertOutputs(parser.getSnippet(),
				"{IN_LIST={list={1={function={parameters={1={literal=2026}, 2={literal=1}, 3={literal=2}, 4={literal=3}, 5={literal=4}, 6={literal=5}}, function_name=make_timestamp}}}}}",
				"[]", "{}", "{}",
				"{}",
				"{}");
	}

	@Test
	public void makeTimestampJoinExtensionSnippet() {
		SqlParserAccess parser = parseSnippet("join accounts b on make_timestamp(b.y, b.m, b.d, b.h, b.mi, b.s) > TIMESTAMP '2026-01-01 00:00:00'", "JOIN_EXTENSION");
		assertOutputs(parser.getSnippet(),
				"{JOIN_EXTENSION={1={join=join, on={condition={left={function={parameters={1={column={name=y, table_ref=b}}, 2={column={name=m, table_ref=b}}, 3={column={name=d, table_ref=b}}, 4={column={name=h, table_ref=b}}, 5={column={name=mi, table_ref=b}}, 6={column={name=s, table_ref=b}}}, function_name=make_timestamp}}, right={source_type=TIMESTAMP, literal=2026-01-01 00:00:00}, operator=>}}}, 2={table={alias=b, table=accounts}}}}",
				"[]", "{}", "{accounts={s=[[@26,60:60='b',<433>,1:60]], d=[[@14,44:44='b',<433>,1:44]], h=[[@18,49:49='b',<433>,1:49]], y=[[@6,34:34='b',<433>,1:34]], mi=[[@22,54:54='b',<433>,1:54]], m=[[@10,39:39='b',<433>,1:39]]}}",
				"{}",
				"{table_dictionary={accounts={s=[[@26,60:60='b',<433>,1:60]], d=[[@14,44:44='b',<433>,1:44]], h=[[@18,49:49='b',<433>,1:49]], y=[[@6,34:34='b',<433>,1:34]], mi=[[@22,54:54='b',<433>,1:54]], m=[[@10,39:39='b',<433>,1:39]]}}, filters=[{name=y, table_ref=b}, {name=m, table_ref=b}, {name=d, table_ref=b}, {name=h, table_ref=b}, {name=mi, table_ref=b}, {name=s, table_ref=b}], table_alias={b=accounts}}");
	}

	@Test
	public void makeTimestampValuesSnippet() {
		SqlParserAccess parser = parseSnippet("values (make_timestamp(2026, 1, 2, 3, 4, 5))", "VALUES");
		assertOutputs(parser.getSnippet(),
				"{VALUES={values={matrix={1={row={1={function={parameters={1={literal=2026}, 2={literal=1}, 3={literal=2}, 4={literal=3}, 5={literal=4}, 6={literal=5}}, function_name=make_timestamp}}}}}}}}",
				"[$1]", "{}", "{}",
				"{values0={$1=[[@1,7:7='(',<287>,1:7]]}}",
				"{def_values0={query_dictionary={$1=[[@1,7:7='(',<287>,1:7]]}, interface={$1=[]}}}");
	}

	@Test
	public void makeTimestampInsertSnippet() {
		SqlParserAccess parser = parseSnippet("insert into target (made_ts) select make_timestamp(a.y, a.m, a.d, a.h, a.mi, a.s) as made_ts from events a", "INSERT");
		assertOutputs(parser.getSnippet(),
				"{INSERT={insert={preamble=insert_into, from={from={table={alias=a, table=events}}, select={1={function={parameters={1={column={name=y, table_ref=a}}, 2={column={name=m, table_ref=a}}, 3={column={name=d, table_ref=a}}, 4={column={name=h, table_ref=a}}, 5={column={name=mi, table_ref=a}}, 6={column={name=s, table_ref=a}}}, function_name=make_timestamp}, alias=made_ts}}}, target_table={table={alias=null, table=target}}, columns={1={column={name=made_ts, table_ref=null}}}}}}",
				"[made_ts]", "{}", "{events={s=[[@29,77:77='a',<433>,1:77]], d=[[@17,61:61='a',<433>,1:61]], h=[[@21,66:66='a',<433>,1:66]], y=[[@9,51:51='a',<433>,1:51]], mi=[[@25,71:71='a',<433>,1:71]], m=[[@13,56:56='a',<433>,1:56]]}, target={made_ts=[[@4,20:26='made_ts',<433>,1:20]]}}",
				"{query0={made_ts=[[@34,85:91='made_ts',<433>,1:85]]}, insert1={made_ts=[[@4,20:26='made_ts',<433>,1:20]]}}",
				"{def_insert1={query_dictionary={made_ts=[[@4,20:26='made_ts',<433>,1:20]]}, table_dictionary={target={made_ts=[[@4,20:26='made_ts',<433>,1:20]]}}, def_query0={query_dictionary={made_ts=[[@34,85:91='made_ts',<433>,1:85]]}, table_dictionary={events={s=[[@29,77:77='a',<433>,1:77]], d=[[@17,61:61='a',<433>,1:61]], h=[[@21,66:66='a',<433>,1:66]], y=[[@9,51:51='a',<433>,1:51]], mi=[[@25,71:71='a',<433>,1:71]], m=[[@13,56:56='a',<433>,1:56]]}}, interface={made_ts=[{name=y, table_ref=a}, {name=m, table_ref=a}, {name=d, table_ref=a}, {name=h, table_ref=a}, {name=mi, table_ref=a}, {name=s, table_ref=a}]}, table_alias={a=events}}, interface={made_ts=[{name=made_ts, table_ref=query0}]}}}");
	}

	@Test
	public void makeTimestampUpdateSnippet() {
		SqlParserAccess parser = parseSnippet("update events set made_ts = make_timestamp(y, m, d, h, mi, s)", "UPDATE");
		assertOutputs(parser.getSnippet(),
				"{UPDATE={update={table={alias=null, table=events}, assignments={1={set={column={name=made_ts, table_ref=null}}, to={function={parameters={1={column={name=y, table_ref=null}}, 2={column={name=m, table_ref=null}}, 3={column={name=d, table_ref=null}}, 4={column={name=h, table_ref=null}}, 5={column={name=mi, table_ref=null}}, 6={column={name=s, table_ref=null}}}, function_name=make_timestamp}}}}}}}",
				"[made_ts]", "{}", "{events={s=[[@17,59:59='s',<433>,1:59], [@18,60:60=')',<288>,1:60]], d=[[@11,49:49='d',<433>,1:49], [@18,60:60=')',<288>,1:60]], h=[[@13,52:52='h',<433>,1:52], [@18,60:60=')',<288>,1:60]], y=[[@7,43:43='y',<433>,1:43], [@18,60:60=')',<288>,1:60]], mi=[[@15,55:56='mi',<433>,1:55], [@18,60:60=')',<288>,1:60]], m=[[@9,46:46='m',<433>,1:46], [@18,60:60=')',<288>,1:60]]}}",
				"{update0={made_ts=[[@3,18:24='made_ts',<433>,1:18]]}}",
				"{def_update0={assignments={made_ts=[{name=y, table_ref=events}, {name=m, table_ref=events}, {name=d, table_ref=events}, {name=h, table_ref=events}, {name=mi, table_ref=events}, {name=s, table_ref=events}]}, table_dictionary={events={s=[[@17,59:59='s',<433>,1:59], [@18,60:60=')',<288>,1:60]], d=[[@11,49:49='d',<433>,1:49], [@18,60:60=')',<288>,1:60]], h=[[@13,52:52='h',<433>,1:52], [@18,60:60=')',<288>,1:60]], y=[[@7,43:43='y',<433>,1:43], [@18,60:60=')',<288>,1:60]], mi=[[@15,55:56='mi',<433>,1:55], [@18,60:60=')',<288>,1:60]], m=[[@9,46:46='m',<433>,1:46], [@18,60:60=')',<288>,1:60]]}}, update_dictionary={made_ts=[[@3,18:24='made_ts',<433>,1:18]]}, target_table={events={made_ts=[[@3,18:24='made_ts',<433>,1:18]]}}, lhs_unresolved_columns={made_ts={column={name=made_ts, table_ref=null}, locations=[[@3,18:24='made_ts',<433>,1:18]]}}}}");
	}

	@Test
	public void makeTimestampDeleteSnippet() {
		SqlParserAccess parser = parseSnippet("delete from events where make_timestamp(y, m, d, h, mi, s) > TIMESTAMP '2026-01-01 00:00:00'", "DELETE");
		assertOutputs(parser.getSnippet(),
				"{DELETE={delete={table={alias=null, table=events}, where={condition={left={function={parameters={1={column={name=y, table_ref=null}}, 2={column={name=m, table_ref=null}}, 3={column={name=d, table_ref=null}}, 4={column={name=h, table_ref=null}}, 5={column={name=mi, table_ref=null}}, 6={column={name=s, table_ref=null}}}, function_name=make_timestamp}}, right={source_type=TIMESTAMP, literal=2026-01-01 00:00:00}, operator=>}}}}}",
				"[]", "{}", "{events={s=[[@16,56:56='s',<433>,1:56]], d=[[@10,46:46='d',<433>,1:46]], h=[[@12,49:49='h',<433>,1:49]], y=[[@6,40:40='y',<433>,1:40]], mi=[[@14,52:53='mi',<433>,1:52]], m=[[@8,43:43='m',<433>,1:43]]}}",
				"{}",
				"{def_delete0={query_dictionary={}, table_dictionary={events={s=[[@16,56:56='s',<433>,1:56]], d=[[@10,46:46='d',<433>,1:46]], h=[[@12,49:49='h',<433>,1:49]], y=[[@6,40:40='y',<433>,1:40]], mi=[[@14,52:53='mi',<433>,1:52]], m=[[@8,43:43='m',<433>,1:43]]}}, filters=[{name=y, table_ref=events}, {name=m, table_ref=events}, {name=d, table_ref=events}, {name=h, table_ref=events}, {name=mi, table_ref=events}, {name=s, table_ref=events}], interface=null}}");
	}

	@Test
	public void makeTimestampDdlSnippet() {
		SqlParserAccess parser = parseSnippet("create view v as select make_timestamp(a.y, a.m, a.d, a.h, a.mi, a.s) as made_ts from events a", "DDL");
		assertOutputs(parser.getSnippet(),
				"{DDL={create={type=view, name={table=v}, query={select={1={function={parameters={1={column={name=y, table_ref=a}}, 2={column={name=m, table_ref=a}}, 3={column={name=d, table_ref=a}}, 4={column={name=h, table_ref=a}}, 5={column={name=mi, table_ref=a}}, 6={column={name=s, table_ref=a}}}, function_name=make_timestamp}, alias=made_ts}}, from={table={alias=a, table=events}}}}}}",
				"[]", "{}", "{events={s=[[@27,65:65='a',<433>,1:65]], d=[[@15,49:49='a',<433>,1:49]], h=[[@19,54:54='a',<433>,1:54]], y=[[@7,39:39='a',<433>,1:39]], mi=[[@23,59:59='a',<433>,1:59]], m=[[@11,44:44='a',<433>,1:44]]}}",
				"{query0={made_ts=[[@32,73:79='made_ts',<433>,1:73]]}}",
				"{def_create1={def_query0={query_dictionary={made_ts=[[@32,73:79='made_ts',<433>,1:73]]}, table_dictionary={events={s=[[@27,65:65='a',<433>,1:65]], d=[[@15,49:49='a',<433>,1:49]], h=[[@19,54:54='a',<433>,1:54]], y=[[@7,39:39='a',<433>,1:39]], mi=[[@23,59:59='a',<433>,1:59]], m=[[@11,44:44='a',<433>,1:44]]}}, interface={made_ts=[{name=y, table_ref=a}, {name=m, table_ref=a}, {name=d, table_ref=a}, {name=h, table_ref=a}, {name=mi, table_ref=a}, {name=s, table_ref=a}]}, table_alias={a=events}}}}");
	}

	@Test
	public void makeTimestampScriptSnippet() {
		SqlParserAccess parser = parseSnippet("select make_timestamp(2026, 1, 2, 3, 4, 5) as made_ts;", "SCRIPT");
		assertOutputs(parser.getSnippet(),
				"{SCRIPT={1={select={1={function={parameters={1={literal=2026}, 2={literal=1}, 3={literal=2}, 4={literal=3}, 5={literal=4}, 6={literal=5}}, function_name=make_timestamp}, alias=made_ts}}}}}",
				"[]", "{SCRIPT={1={}}}", "{SCRIPT={1={}}}",
				"{SCRIPT={1={query0={made_ts=[[@16,46:52='made_ts',<433>,1:46]]}}}}",
				"{SCRIPT={1={def_query0={query_dictionary={made_ts=[[@16,46:52='made_ts',<433>,1:46]]}, interface={made_ts=[]}}}}}");
	}

	@Test
	public void makeDatePredicandSnippet() {
		SqlParserAccess parser = parseSnippet("MAKE_DATE(<year>, <month>, <day>)", "PREDICAND");
		assertOutputs(parser.getSnippet(),
				"{PREDICAND={function={parameters={1={substitution={name=<year>, type=predicand}}, 2={substitution={name=<month>, type=predicand}}, 3={substitution={name=<day>, type=predicand}}}, function_name=MAKE_DATE}}}",
				"[]", "{<day>=predicand, <month>=predicand, <year>=predicand}", "{}",
				"{}",
				"{}");
	}

	@Test
	public void makeTimePredicandSnippet() {
		SqlParserAccess parser = parseSnippet("MAKE_TIME(<hour>, <minute>, <second>)", "PREDICAND");
		assertOutputs(parser.getSnippet(),
				"{PREDICAND={function={parameters={1={substitution={name=<hour>, type=predicand}}, 2={substitution={name=<minute>, type=predicand}}, 3={substitution={name=<second>, type=predicand}}}, function_name=MAKE_TIME}}}",
				"[]", "{<minute>=predicand, <second>=predicand, <hour>=predicand}", "{}",
				"{}",
				"{}");
	}

	@Test
	public void makeTimestamptzPredicandSnippet() {
		SqlParserAccess parser = parseSnippet("MAKE_TIMESTAMPTZ(<year>, <month>, <day>, <hour>, <minute>, <second>)", "PREDICAND");
		assertOutputs(parser.getSnippet(),
				"{PREDICAND={function={parameters={1={substitution={name=<year>, type=predicand}}, 2={substitution={name=<month>, type=predicand}}, 3={substitution={name=<day>, type=predicand}}, 4={substitution={name=<hour>, type=predicand}}, 5={substitution={name=<minute>, type=predicand}}, 6={substitution={name=<second>, type=predicand}}}, function_name=MAKE_TIMESTAMPTZ}}}",
				"[]", "{<minute>=predicand, <day>=predicand, <month>=predicand, <second>=predicand, <year>=predicand, <hour>=predicand}", "{}",
				"{}",
				"{}");
	}

	@Test
	public void makeIntervalPredicandSnippet() {
		SqlParserAccess parser = parseSnippet("MAKE_INTERVAL(<years>, <months>, <weeks>, <days>, <hours>, <mins>, <secs>)", "PREDICAND");
		assertOutputs(parser.getSnippet(),
				"{PREDICAND={function={parameters={1={substitution={name=<years>, type=predicand}}, 2={substitution={name=<months>, type=predicand}}, 3={substitution={name=<weeks>, type=predicand}}, 4={substitution={name=<days>, type=predicand}}, 5={substitution={name=<hours>, type=predicand}}, 6={substitution={name=<mins>, type=predicand}}, 7={substitution={name=<secs>, type=predicand}}}, function_name=MAKE_INTERVAL}}}",
				"[]", "{<months>=predicand, <mins>=predicand, <secs>=predicand, <years>=predicand, <days>=predicand, <hours>=predicand, <weeks>=predicand}", "{}",
				"{}",
				"{}");
	}

	@Test
	public void justifyIntervalPredicandSnippet() {
		SqlParserAccess parser = parseSnippet("JUSTIFY_INTERVAL(<interval value>)", "PREDICAND");
		assertOutputs(parser.getSnippet(),
				"{PREDICAND={function={parameters={1={substitution={name=<interval value>, type=predicand}}}, function_name=JUSTIFY_INTERVAL}}}",
				"[]", "{<interval value>=predicand}", "{}",
				"{}",
				"{}");
	}

	@Test
	public void justifyIntervalConditionSnippet() {
		SqlParserAccess parser = parseSnippet("justify_interval(<interval value>) is not null", "CONDITION");
		assertOutputs(parser.getSnippet(),
				"{CONDITION={condition={left={function={parameters={1={substitution={name=<interval value>, type=predicand}}}, function_name=justify_interval}}, operator=is not null}}}",
				"[]", "{<interval value>=predicand}", "{}",
				"{}",
				"{}");
	}

	@Test
	public void justifyIntervalQuerySnippet() {
		SqlParserAccess parser = parseSnippet("select justify_interval(a.iv) as normalized_iv from events a", "QUERY");
		assertOutputs(parser.getSnippet(),
				"{QUERY={select={1={function={parameters={1={column={name=iv, table_ref=a}}}, function_name=justify_interval}, alias=normalized_iv}}, from={table={alias=a, table=events}}}}",
				"[normalized_iv]", "{}", "{events={iv=[[@3,24:24='a',<433>,1:24]]}}",
				"{query0={normalized_iv=[[@8,33:45='normalized_iv',<433>,1:33]]}}",
				"{def_query0={query_dictionary={normalized_iv=[[@8,33:45='normalized_iv',<433>,1:33]]}, table_dictionary={events={iv=[[@3,24:24='a',<433>,1:24]]}}, interface={normalized_iv=[{name=iv, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void justifyIntervalTupleSnippet() {
		SqlParserAccess parser = parseSnippet("(select justify_interval(a.iv) as normalized_iv from events a)", "TUPLE");
		assertOutputs(parser.getSnippet(),
				"{TUPLE={from={table={alias=a, table=events}}, select={1={function={parameters={1={column={name=iv, table_ref=a}}}, function_name=justify_interval}, alias=normalized_iv}}}}",
				"[normalized_iv]", "{}", "{events={iv=[[@4,25:25='a',<433>,1:25]]}}",
				"{query0={normalized_iv=[[@9,34:46='normalized_iv',<433>,1:34]]}}",
				"{def_query0={query_dictionary={normalized_iv=[[@9,34:46='normalized_iv',<433>,1:34]]}, table_dictionary={events={iv=[[@4,25:25='a',<433>,1:25]]}}, interface={normalized_iv=[{name=iv, table_ref=a}]}, table_alias={a=events}}, table_alias={query0=query0}}");
	}

	@Test
	public void justifyIntervalInListSnippet() {
		SqlParserAccess parser = parseSnippet("(justify_interval(INTERVAL '36 hours'))", "IN_LIST");
		assertOutputs(parser.getSnippet(),
				"{IN_LIST={list={1={function={parameters={1={source_type=INTERVAL, literal=36 hours}}, function_name=justify_interval}}}}}",
				"[]", "{}", "{}",
				"{}",
				"{}");
	}

	@Test
	public void justifyIntervalJoinExtensionSnippet() {
		SqlParserAccess parser = parseSnippet("join accounts b on justify_interval(b.iv) is not null", "JOIN_EXTENSION");
		assertOutputs(parser.getSnippet(),
				"{JOIN_EXTENSION={1={join=join, on={condition={left={function={parameters={1={column={name=iv, table_ref=b}}}, function_name=justify_interval}}, operator=is not null}}}, 2={table={alias=b, table=accounts}}}}",
				"[]", "{}", "{accounts={iv=[[@6,36:36='b',<433>,1:36]]}}",
				"{}",
				"{table_dictionary={accounts={iv=[[@6,36:36='b',<433>,1:36]]}}, filters=[{name=iv, table_ref=b}], table_alias={b=accounts}}");
	}

	@Test
	public void justifyIntervalValuesSnippet() {
		SqlParserAccess parser = parseSnippet("values (justify_interval(INTERVAL '36 hours'))", "VALUES");
		assertOutputs(parser.getSnippet(),
				"{VALUES={values={matrix={1={row={1={function={parameters={1={source_type=INTERVAL, literal=36 hours}}, function_name=justify_interval}}}}}}}}",
				"[$1]", "{}", "{}",
				"{values0={$1=[[@1,7:7='(',<287>,1:7]]}}",
				"{def_values0={query_dictionary={$1=[[@1,7:7='(',<287>,1:7]]}, interface={$1=[]}}}");
	}

	@Test
	public void justifyIntervalInsertSnippet() {
		SqlParserAccess parser = parseSnippet("insert into target (normalized_iv) select justify_interval(a.iv) as normalized_iv from events a", "INSERT");
		assertOutputs(parser.getSnippet(),
				"{INSERT={insert={preamble=insert_into, from={from={table={alias=a, table=events}}, select={1={function={parameters={1={column={name=iv, table_ref=a}}}, function_name=justify_interval}, alias=normalized_iv}}}, target_table={table={alias=null, table=target}}, columns={1={column={name=normalized_iv, table_ref=null}}}}}}",
				"[normalized_iv]", "{}", "{events={iv=[[@9,59:59='a',<433>,1:59]]}, target={normalized_iv=[[@4,20:32='normalized_iv',<433>,1:20]]}}",
				"{query0={normalized_iv=[[@14,68:80='normalized_iv',<433>,1:68]]}, insert1={normalized_iv=[[@4,20:32='normalized_iv',<433>,1:20]]}}",
				"{def_insert1={query_dictionary={normalized_iv=[[@4,20:32='normalized_iv',<433>,1:20]]}, table_dictionary={target={normalized_iv=[[@4,20:32='normalized_iv',<433>,1:20]]}}, def_query0={query_dictionary={normalized_iv=[[@14,68:80='normalized_iv',<433>,1:68]]}, table_dictionary={events={iv=[[@9,59:59='a',<433>,1:59]]}}, interface={normalized_iv=[{name=iv, table_ref=a}]}, table_alias={a=events}}, interface={normalized_iv=[{name=normalized_iv, table_ref=query0}]}}}");
	}

	@Test
	public void justifyIntervalUpdateSnippet() {
		SqlParserAccess parser = parseSnippet("update events set normalized_iv = justify_interval(iv)", "UPDATE");
		assertOutputs(parser.getSnippet(),
				"{UPDATE={update={table={alias=null, table=events}, assignments={1={set={column={name=normalized_iv, table_ref=null}}, to={function={parameters={1={column={name=iv, table_ref=null}}}, function_name=justify_interval}}}}}}}",
				"[normalized_iv]", "{}", "{events={iv=[[@7,51:52='iv',<433>,1:51], [@8,53:53=')',<288>,1:53]]}}",
				"{update0={normalized_iv=[[@3,18:30='normalized_iv',<433>,1:18]]}}",
				"{def_update0={assignments={normalized_iv=[{name=iv, table_ref=events}]}, table_dictionary={events={iv=[[@7,51:52='iv',<433>,1:51], [@8,53:53=')',<288>,1:53]]}}, update_dictionary={normalized_iv=[[@3,18:30='normalized_iv',<433>,1:18]]}, target_table={events={normalized_iv=[[@3,18:30='normalized_iv',<433>,1:18]]}}, lhs_unresolved_columns={normalized_iv={column={name=normalized_iv, table_ref=null}, locations=[[@3,18:30='normalized_iv',<433>,1:18]]}}}}");
	}

	@Test
	public void justifyIntervalDeleteSnippet() {
		SqlParserAccess parser = parseSnippet("delete from events where justify_interval(iv) is not null", "DELETE");
		assertOutputs(parser.getSnippet(),
				"{DELETE={delete={table={alias=null, table=events}, where={condition={left={function={parameters={1={column={name=iv, table_ref=null}}}, function_name=justify_interval}}, operator=is not null}}}}}",
				"[]", "{}", "{events={iv=[[@6,42:43='iv',<433>,1:42]]}}",
				"{}",
				"{def_delete0={query_dictionary={}, table_dictionary={events={iv=[[@6,42:43='iv',<433>,1:42]]}}, filters=[{name=iv, table_ref=events}], interface=null}}");
	}

	@Test
	public void justifyIntervalDdlSnippet() {
		SqlParserAccess parser = parseSnippet("create view v as select justify_interval(a.iv) as normalized_iv from events a", "DDL");
		assertOutputs(parser.getSnippet(),
				"{DDL={create={type=view, name={table=v}, query={select={1={function={parameters={1={column={name=iv, table_ref=a}}}, function_name=justify_interval}, alias=normalized_iv}}, from={table={alias=a, table=events}}}}}}",
				"[]", "{}", "{events={iv=[[@7,41:41='a',<433>,1:41]]}}",
				"{query0={normalized_iv=[[@12,50:62='normalized_iv',<433>,1:50]]}}",
				"{def_create1={def_query0={query_dictionary={normalized_iv=[[@12,50:62='normalized_iv',<433>,1:50]]}, table_dictionary={events={iv=[[@7,41:41='a',<433>,1:41]]}}, interface={normalized_iv=[{name=iv, table_ref=a}]}, table_alias={a=events}}}}");
	}

	@Test
	public void justifyIntervalScriptSnippet() {
		SqlParserAccess parser = parseSnippet("select justify_interval(INTERVAL '36 hours') as normalized_iv;", "SCRIPT");
		assertOutputs(parser.getSnippet(),
				"{SCRIPT={1={select={1={function={parameters={1={source_type=INTERVAL, literal=36 hours}}, function_name=justify_interval}, alias=normalized_iv}}}}}",
				"[]", "{SCRIPT={1={}}}", "{SCRIPT={1={}}}",
				"{SCRIPT={1={query0={normalized_iv=[[@7,48:60='normalized_iv',<433>,1:48]]}}}}",
				"{SCRIPT={1={def_query0={query_dictionary={normalized_iv=[[@7,48:60='normalized_iv',<433>,1:48]]}, interface={normalized_iv=[]}}}}}");
	}

	@Test
	public void justifyDaysPredicandSnippet() {
		SqlParserAccess parser = parseSnippet("JUSTIFY_DAYS(<interval value>)", "PREDICAND");
		assertOutputs(parser.getSnippet(),
				"{PREDICAND={function={parameters={1={substitution={name=<interval value>, type=predicand}}}, function_name=JUSTIFY_DAYS}}}",
				"[]", "{<interval value>=predicand}", "{}",
				"{}",
				"{}");
	}

	@Test
	public void justifyHoursPredicandSnippet() {
		SqlParserAccess parser = parseSnippet("JUSTIFY_HOURS(<interval value>)", "PREDICAND");
		assertOutputs(parser.getSnippet(),
				"{PREDICAND={function={parameters={1={substitution={name=<interval value>, type=predicand}}}, function_name=JUSTIFY_HOURS}}}",
				"[]", "{<interval value>=predicand}", "{}",
				"{}",
				"{}");
	}

	@Test
	public void clockTimestampPredicandSnippet() {
		SqlParserAccess parser = parseSnippet("CLOCK_TIMESTAMP()", "PREDICAND");
		assertOutputs(parser.getSnippet(),
				"{PREDICAND={1={function_name=CLOCK_TIMESTAMP}}}",
				"[]", "{}", "{}",
				"{}",
				"{}");
	}

	@Test
	public void clockTimestampConditionSnippet() {
		SqlParserAccess parser = parseSnippet("clock_timestamp() > TIMESTAMP '2026-01-01 00:00:00'", "CONDITION");
		assertOutputs(parser.getSnippet(),
				"{CONDITION={condition={left={1={function_name=clock_timestamp}}, right={source_type=TIMESTAMP, literal=2026-01-01 00:00:00}, operator=>}}}",
				"[]", "{}", "{}",
				"{}",
				"{}");
	}

	@Test
	public void clockTimestampQuerySnippet() {
		SqlParserAccess parser = parseSnippet("select clock_timestamp() as now_clock from events a", "QUERY");
		assertOutputs(parser.getSnippet(),
				"{QUERY={select={1={1={function_name=clock_timestamp}, alias=now_clock}}, from={table={alias=a, table=events}}}}",
				"[now_clock]", "{}", "{events={}}",
				"{query0={now_clock=[[@5,28:36='now_clock',<433>,1:28]]}}",
				"{def_query0={query_dictionary={now_clock=[[@5,28:36='now_clock',<433>,1:28]]}, table_dictionary={events={}}, interface={now_clock=[]}, table_alias={a=events}}}");
	}

	@Test
	public void clockTimestampTupleSnippet() {
		SqlParserAccess parser = parseSnippet("(select clock_timestamp() as now_clock from events a)", "TUPLE");
		assertOutputs(parser.getSnippet(),
				"{TUPLE={from={table={alias=a, table=events}}, select={1={1={function_name=clock_timestamp}, alias=now_clock}}}}",
				"[now_clock]", "{}", "{events={}}",
				"{query0={now_clock=[[@6,29:37='now_clock',<433>,1:29]]}}",
				"{def_query0={query_dictionary={now_clock=[[@6,29:37='now_clock',<433>,1:29]]}, table_dictionary={events={}}, interface={now_clock=[]}, table_alias={a=events}}, table_alias={query0=query0}}");
	}

	@Test
	public void clockTimestampInListSnippet() {
		SqlParserAccess parser = parseSnippet("(clock_timestamp())", "IN_LIST");
		assertOutputs(parser.getSnippet(),
				"{IN_LIST={list={1={1={function_name=clock_timestamp}}}}}",
				"[]", "{}", "{}",
				"{}",
				"{}");
	}

	@Test
	public void clockTimestampJoinExtensionSnippet() {
		SqlParserAccess parser = parseSnippet("join accounts b on clock_timestamp() > TIMESTAMP '2026-01-01 00:00:00'", "JOIN_EXTENSION");
		assertOutputs(parser.getSnippet(),
				"{JOIN_EXTENSION={1={join=join, on={condition={left={1={function_name=clock_timestamp}}, right={source_type=TIMESTAMP, literal=2026-01-01 00:00:00}, operator=>}}}, 2={table={alias=b, table=accounts}}}}",
				"[]", "{}", "{accounts={}}",
				"{}",
				"{table_dictionary={accounts={}}, filters=[], table_alias={b=accounts}}");
	}

	@Test
	public void clockTimestampValuesSnippet() {
		SqlParserAccess parser = parseSnippet("values (clock_timestamp())", "VALUES");
		assertOutputs(parser.getSnippet(),
				"{VALUES={values={matrix={1={row={1={1={function_name=clock_timestamp}}}}}}}}",
				"[$1]", "{}", "{}",
				"{values0={$1=[[@1,7:7='(',<287>,1:7]]}}",
				"{def_values0={query_dictionary={$1=[[@1,7:7='(',<287>,1:7]]}, interface={$1=[]}}}");
	}

	@Test
	public void clockTimestampInsertSnippet() {
		SqlParserAccess parser = parseSnippet("insert into target (captured_at) select clock_timestamp() as captured_at from events a", "INSERT");
		assertOutputs(parser.getSnippet(),
				"{INSERT={insert={preamble=insert_into, from={from={table={alias=a, table=events}}, select={1={1={function_name=clock_timestamp}, alias=captured_at}}}, target_table={table={alias=null, table=target}}, columns={1={column={name=captured_at, table_ref=null}}}}}}",
				"[captured_at]", "{}", "{events={}, target={captured_at=[[@4,20:30='captured_at',<433>,1:20]]}}",
				"{query0={captured_at=[[@11,61:71='captured_at',<433>,1:61]]}, insert1={captured_at=[[@4,20:30='captured_at',<433>,1:20]]}}",
				"{def_insert1={query_dictionary={captured_at=[[@4,20:30='captured_at',<433>,1:20]]}, table_dictionary={target={captured_at=[[@4,20:30='captured_at',<433>,1:20]]}}, def_query0={query_dictionary={captured_at=[[@11,61:71='captured_at',<433>,1:61]]}, table_dictionary={events={}}, interface={captured_at=[]}, table_alias={a=events}}, interface={captured_at=[{name=captured_at, table_ref=query0}]}}}");
	}

	@Test
	public void clockTimestampUpdateSnippet() {
		SqlParserAccess parser = parseSnippet("update events set captured_at = clock_timestamp()", "UPDATE");
		assertOutputs(parser.getSnippet(),
				"{UPDATE={update={table={alias=null, table=events}, assignments={1={set={column={name=captured_at, table_ref=null}}, to={1={function_name=clock_timestamp}}}}}}}",
				"[captured_at]", "{}", "{events={}}",
				"{update0={captured_at=[[@3,18:28='captured_at',<433>,1:18]]}}",
				"{def_update0={assignments={captured_at=[]}, table_dictionary={events={}}, update_dictionary={captured_at=[[@3,18:28='captured_at',<433>,1:18]]}, target_table={events={captured_at=[[@3,18:28='captured_at',<433>,1:18]]}}, lhs_unresolved_columns={captured_at={column={name=captured_at, table_ref=null}, locations=[[@3,18:28='captured_at',<433>,1:18]]}}}}");
	}

	@Test
	public void clockTimestampDeleteSnippet() {
		SqlParserAccess parser = parseSnippet("delete from events where clock_timestamp() > TIMESTAMP '2026-01-01 00:00:00'", "DELETE");
		assertOutputs(parser.getSnippet(),
				"{DELETE={delete={table={alias=null, table=events}, where={condition={left={1={function_name=clock_timestamp}}, right={source_type=TIMESTAMP, literal=2026-01-01 00:00:00}, operator=>}}}}}",
				"[]", "{}", "{events={}}",
				"{}",
				"{def_delete0={query_dictionary={}, table_dictionary={events={}}, filters=[], interface=null}}");
	}

	@Test
	public void clockTimestampDdlSnippet() {
		SqlParserAccess parser = parseSnippet("create view v as select clock_timestamp() as now_clock from events a", "DDL");
		assertOutputs(parser.getSnippet(),
				"{DDL={create={type=view, name={table=v}, query={select={1={1={function_name=clock_timestamp}, alias=now_clock}}, from={table={alias=a, table=events}}}}}}",
				"[]", "{}", "{events={}}",
				"{query0={now_clock=[[@9,45:53='now_clock',<433>,1:45]]}}",
				"{def_create1={def_query0={query_dictionary={now_clock=[[@9,45:53='now_clock',<433>,1:45]]}, table_dictionary={events={}}, interface={now_clock=[]}, table_alias={a=events}}}}");
	}

	@Test
	public void clockTimestampScriptSnippet() {
		SqlParserAccess parser = parseSnippet("select clock_timestamp() as now_clock;", "SCRIPT");
		assertOutputs(parser.getSnippet(),
				"{SCRIPT={1={select={1={1={function_name=clock_timestamp}, alias=now_clock}}}}}",
				"[]", "{SCRIPT={1={}}}", "{SCRIPT={1={}}}",
				"{SCRIPT={1={query0={now_clock=[[@5,28:36='now_clock',<433>,1:28]]}}}}",
				"{SCRIPT={1={def_query0={query_dictionary={now_clock=[[@5,28:36='now_clock',<433>,1:28]]}, interface={now_clock=[]}}}}}");
	}

	@Test
	public void statementTimestampPredicandSnippet() {
		SqlParserAccess parser = parseSnippet("STATEMENT_TIMESTAMP()", "PREDICAND");
		assertOutputs(parser.getSnippet(),
				"{PREDICAND={1={function_name=STATEMENT_TIMESTAMP}}}",
				"[]", "{}", "{}",
				"{}",
				"{}");
	}

	@Test
	public void transactionTimestampPredicandSnippet() {
		SqlParserAccess parser = parseSnippet("TRANSACTION_TIMESTAMP()", "PREDICAND");
		assertOutputs(parser.getSnippet(),
				"{PREDICAND={1={function_name=TRANSACTION_TIMESTAMP}}}",
				"[]", "{}", "{}",
				"{}",
				"{}");
	}

	@Test
	public void timeofdayPredicandSnippet() {
		SqlParserAccess parser = parseSnippet("TIMEOFDAY()", "PREDICAND");
		assertOutputs(parser.getSnippet(),
				"{PREDICAND={1={function_name=TIMEOFDAY}}}",
				"[]", "{}", "{}",
				"{}",
				"{}");
	}


}
