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
				"[shifted]", "{}", "{events={ts=[[@7,23:23='a',<419>,1:23]]}}",
				"{query0={shifted=[[@12,32:38='shifted',<419>,1:32]]}}",
				"{def_query0={query_dictionary={shifted=[[@12,32:38='shifted',<419>,1:32]]}, table_dictionary={events={ts=[[@7,23:23='a',<419>,1:23]]}}, interface={shifted=[{name=ts, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void dateaddTupleSnippet() {
		SqlParserAccess parser = parseSnippet("(select dateadd(day, 1, a.ts) as shifted from events a)", "TUPLE");
		assertSingleSnowflakeDatetimeAddWarning(parser, 1, 8);
		assertOutputs(parser.getSnippet(),
				"{TUPLE={from={table={alias=a, table=events}}, select={1={alias=shifted, datetime_function={function_name=dateadd, level={canonical=DAY, text=day, form=KEYWORD}, increment={literal=1}, value={column={name=ts, table_ref=a}}}}}}}",
				"[shifted]", "{}", "{events={ts=[[@8,24:24='a',<419>,1:24]]}}",
				"{query0={shifted=[[@13,33:39='shifted',<419>,1:33]]}}",
				"{def_query0={query_dictionary={shifted=[[@13,33:39='shifted',<419>,1:33]]}, table_dictionary={events={ts=[[@8,24:24='a',<419>,1:24]]}}, interface={shifted=[{name=ts, table_ref=a}]}, table_alias={a=events}}, table_alias={query0=query0}}");
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
				"[]", "{}", "{accounts={ts=[[@10,35:35='b',<419>,1:35]]}}", "{}",
				"{table_dictionary={accounts={ts=[[@10,35:35='b',<419>,1:35]]}}, filters=[{name=ts, table_ref=b}], table_alias={b=accounts}}");
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
				"[shifted]", "{}", "{events={ts=[[@13,52:52='a',<419>,1:52]]}, target={shifted=[[@4,20:26='shifted',<419>,1:20]]}}",
				"{query0={shifted=[[@18,61:67='shifted',<419>,1:61]]}, insert1={shifted=[[@4,20:26='shifted',<419>,1:20]]}}",
				"{def_insert1={query_dictionary={shifted=[[@4,20:26='shifted',<419>,1:20]]}, table_dictionary={target={shifted=[[@4,20:26='shifted',<419>,1:20]]}}, def_query0={query_dictionary={shifted=[[@18,61:67='shifted',<419>,1:61]]}, table_dictionary={events={ts=[[@13,52:52='a',<419>,1:52]]}}, interface={shifted=[{name=ts, table_ref=a}]}, table_alias={a=events}}, interface={shifted=[{name=shifted, table_ref=query0}]}}}");
	}

	@Test
	public void dateaddUpdateSnippet() {
		SqlParserAccess parser = parseSnippet("update events set ts = dateadd(day, 1, ts)", "UPDATE");
		assertSingleSnowflakeDatetimeAddWarning(parser, 1, 23);
		assertOutputs(parser.getSnippet(),
				"{UPDATE={update={table={alias=null, table=events}, assignments={1={set={column={name=ts, table_ref=null}}, to={datetime_function={function_name=dateadd, level={canonical=DAY, text=day, form=KEYWORD}, increment={literal=1}, value={column={name=ts, table_ref=null}}}}}}}}}",
				"[ts]", "{}", "{events={ts=[[@11,39:40='ts',<419>,1:39]]}}",
				"{update0={ts=[[@3,18:19='ts',<419>,1:18]]}}",
				"{def_update0={assignments={ts=[{name=ts, table_ref=events}]}, table_dictionary={events={ts=[[@11,39:40='ts',<419>,1:39]]}}, update_dictionary={ts=[[@3,18:19='ts',<419>,1:18]]}, target_table={events={ts=[[@3,18:19='ts',<419>,1:18], [@11,39:40='ts',<419>,1:39], [@12,41:41=')',<288>,1:41]]}}, lhs_unresolved_columns={ts={column={name=ts, table_ref=null}, locations=[[@3,18:19='ts',<419>,1:18], [@11,39:40='ts',<419>,1:39], [@12,41:41=')',<288>,1:41]]}}}}");
	}

	@Test
	public void dateaddDeleteSnippet() {
		SqlParserAccess parser = parseSnippet("delete from events where dateadd(day, 1, ts) > DATE '2026-01-01'", "DELETE");
		assertSingleSnowflakeDatetimeAddWarning(parser, 1, 25);
		assertOutputs(parser.getSnippet(),
				"{DELETE={delete={table={alias=null, table=events}, where={condition={left={datetime_function={function_name=dateadd, level={canonical=DAY, text=day, form=KEYWORD}, increment={literal=1}, value={column={name=ts, table_ref=null}}}}, right={source_type=DATE, literal=2026-01-01}, operator=>}}}}}",
				"[]", "{}", "{events={ts=[[@10,41:42='ts',<419>,1:41]]}}", "{}",
				"{def_delete0={query_dictionary={}, table_dictionary={events={ts=[[@10,41:42='ts',<419>,1:41]]}}, filters=[{name=ts, table_ref=events}], interface=null}}");
	}

	@Test
	public void dateaddDdlSnippet() {
		SqlParserAccess parser = parseSnippet("create view v as select dateadd(day, 1, a.ts) as shifted from events a", "DDL");
		assertSingleSnowflakeDatetimeAddWarning(parser, 1, 24);
		assertOutputs(parser.getSnippet(),
				"{DDL={create={type=view, name={table=v}, query={select={1={alias=shifted, datetime_function={function_name=dateadd, level={canonical=DAY, text=day, form=KEYWORD}, increment={literal=1}, value={column={name=ts, table_ref=a}}}}}, from={table={alias=a, table=events}}}}}}",
				"[]", "{}", "{events={ts=[[@11,40:40='a',<419>,1:40]]}}",
				"{query0={shifted=[[@16,49:55='shifted',<419>,1:49]]}}",
				"{def_create1={def_query0={query_dictionary={shifted=[[@16,49:55='shifted',<419>,1:49]]}, table_dictionary={events={ts=[[@11,40:40='a',<419>,1:40]]}}, interface={shifted=[{name=ts, table_ref=a}]}, table_alias={a=events}}}}");
	}

	@Test
	public void dateaddScriptSnippet() {
		SqlParserAccess parser = parseSnippet("select dateadd(day, 1, DATE '2026-01-01') as shifted;", "SCRIPT");
		assertSingleSnowflakeDatetimeAddWarning(parser, 1, 7);
		assertOutputs(parser.getSnippet(),
				"{SCRIPT={1={select={1={alias=shifted, datetime_function={function_name=dateadd, level={canonical=DAY, text=day, form=KEYWORD}, increment={literal=1}, value={source_type=DATE, literal=2026-01-01}}}}}}}",
				"[]", "{SCRIPT={1={}}}", "{SCRIPT={1={}}}",
				"{SCRIPT={1={query0={shifted=[[@11,45:51='shifted',<419>,1:45]]}}}}",
				"{SCRIPT={1={def_query0={query_dictionary={shifted=[[@11,45:51='shifted',<419>,1:45]]}, interface={shifted=[]}}}}}");
	}
}
