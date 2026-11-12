package sql.walker;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import access.SqlParserAccess;
import errorhandling.ParseDiagnostic;
import sql.SQLSelectParserParser;
import sql.grammar.DatetimePartRegistry;

public class SqlEventWalkerDatetimeFunctionPlacementTests extends AbstractSqlParseEventWalkerTest {

	@Test
	public void timeaddAndTimestampaddRetainTheirOwnInvocationsAndExpressionArguments() {
		final String query = "select timeadd(hr, a.offset_hours, a.start_time) as shifted_time, timestampadd(day, a.offset_days + 1, a.created_at) as shifted_timestamp from events a";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=shifted_time, datetime_function={function_name=timeadd, level={canonical=HOUR, text=hr, form=IDENTIFIER}, increment={column={name=offset_hours, table_ref=a}}, value={column={name=start_time, table_ref=a}}}}, 2={alias=shifted_timestamp, datetime_function={function_name=timestampadd, level={canonical=DAY, text=day, form=KEYWORD}, increment={calc={left={column={name=offset_days, table_ref=a}}, right={literal=1}, operator=+}}, value={column={name=created_at, table_ref=a}}}}}, from={table={alias=a, table=events}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[shifted_time, shifted_timestamp]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{events={start_time=[[@9,35:35='a',<419>,1:35]], created_at=[[@26,103:103='a',<419>,1:103]], offset_hours=[[@5,19:19='a',<419>,1:19]], offset_days=[[@20,84:84='a',<419>,1:84]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={shifted_timestamp=[[@31,120:136='shifted_timestamp',<419>,1:120]], shifted_time=[[@14,52:63='shifted_time',<419>,1:52]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={shifted_time=[[@14,52:63='shifted_time',<419>,1:52]], shifted_timestamp=[[@31,120:136='shifted_timestamp',<419>,1:120]]}, table_dictionary={events={start_time=[[@9,35:35='a',<419>,1:35]], created_at=[[@26,103:103='a',<419>,1:103]], offset_hours=[[@5,19:19='a',<419>,1:19]], offset_days=[[@20,84:84='a',<419>,1:84]]}}, interface={shifted_time=[{name=offset_hours, table_ref=a}, {name=start_time, table_ref=a}], shifted_timestamp=[{name=offset_days, table_ref=a}, {name=created_at, table_ref=a}]}, table_alias={a=events}}}", extractor.getSymbolTable().toString());
	}

	@Test
	public void timeaddAndTimestampaddReportInvalidPartsAtTheirSource() {
		String[][] cases = {{"TIMEADD", "15"}, {"TIMESTAMPADD", "20"}};
		for (String[] testCase : cases) {
			String invocation = testCase[0];
			int position = Integer.parseInt(testCase[1]);
			String query = "select " + invocation + "(bogus, 1, a.ts) as shifted from events a";
			SqlParserAccess parser = new SqlParserAccess(false, false, false);
			parser.executeTheParse(query, "SQL");
			Assert.assertEquals(query, 0, parser.getParser().getNumberOfSyntaxErrors());
			Assert.assertEquals(query + ": " + parser.getAllDiagnostics(), 1, parser.getAllDiagnostics().size());
			assertFatalDiagnosticAtPositionWithFullMessage(parser.getSnippet(), "INVALID_DATETIME_PART",
					"Invalid date/time part bogus for " + invocation + " at (l:1 c:" + position + ").",
					"bogus", 1, position);
			Assert.assertEquals("bogus", findFatalDiagnosticByCodeAndFragment(
					parser.getSnippet(), "INVALID_DATETIME_PART", "bogus").tokenText());
			Assert.assertFalse(parser.getSnippet().getSymbolTable().toString().contains("bogus"));
		}
	}

	@Test
	public void dateaddHasNamedArgumentsAndPartMetadata() {
		final String query = "select dateadd(year, 1, a.created_at) as shifted from events a";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=shifted, datetime_function={function_name=dateadd, level={canonical=YEAR, text=year, form=KEYWORD}, increment={literal=1}, value={column={name=created_at, table_ref=a}}}}}, from={table={alias=a, table=events}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[shifted]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{events={created_at=[[@7,24:24='a',<419>,1:24]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={shifted=[[@12,41:47='shifted',<419>,1:41]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={shifted=[[@12,41:47='shifted',<419>,1:41]]}, table_dictionary={events={created_at=[[@7,24:24='a',<419>,1:24]]}}, interface={shifted=[{name=created_at, table_ref=a}]}, table_alias={a=events}}}", extractor.getSymbolTable().toString());
	}

	@Test
	public void dateaddResolvesUnqualifiedValueAgainstOneSource() {
		final String query = "select dateadd(year, 1, created_at) as shifted from events a";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=shifted, datetime_function={function_name=dateadd, level={canonical=YEAR, text=year, form=KEYWORD}, increment={literal=1}, value={column={name=created_at, table_ref=null}}}}}, from={table={alias=a, table=events}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[shifted]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{events={created_at=[[@7,24:33='created_at',<419>,1:24]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={shifted=[[@10,39:45='shifted',<419>,1:39]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={shifted=[[@10,39:45='shifted',<419>,1:39]]}, table_dictionary={events={created_at=[[@7,24:33='created_at',<419>,1:24]]}}, interface={shifted=[{name=created_at, table_ref=events}]}, table_alias={a=events}}}", extractor.getSymbolTable().toString());
	}

	@Test
	public void dateaddCollectsJoinedFormulaSourcesAndJoinFilters() {
		final String query = "select dateadd(day, a.offset_days + b.extra_days * 2, DATE '2026-01-01') as shifted from events a join accounts b on a.account_id = b.id";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=shifted, datetime_function={function_name=dateadd, level={canonical=DAY, text=day, form=KEYWORD}, increment={calc={left={column={name=offset_days, table_ref=a}}, right={calc={left={column={name=extra_days, table_ref=b}}, right={literal=2}, operator=*}}, operator=+}}, value={source_type=DATE, literal=2026-01-01}}}}, from={join={1={table={alias=a, table=events}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[shifted]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{accounts={extra_days=[[@9,36:36='b',<419>,1:36]], id=[[@31,132:132='b',<419>,1:132]]}, events={account_id=[[@27,117:117='a',<419>,1:117]], offset_days=[[@5,20:20='a',<419>,1:20]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={shifted=[[@19,76:82='shifted',<419>,1:76]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={shifted=[[@19,76:82='shifted',<419>,1:76]]}, table_dictionary={accounts={extra_days=[[@9,36:36='b',<419>,1:36]], id=[[@31,132:132='b',<419>,1:132]]}, events={account_id=[[@27,117:117='a',<419>,1:117]], offset_days=[[@5,20:20='a',<419>,1:20]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}], interface={shifted=[{name=offset_days, table_ref=a}, {name=extra_days, table_ref=b}]}, table_alias={a=events, b=accounts}}}", extractor.getSymbolTable().toString());
	}

	@Test
	public void dateaddCollectsArgumentsFromDifferentJoinedSources() {
		final String query = "select dateadd(day, a.offset_days, b.created_at) as shifted from events a join accounts b on a.account_id = b.id";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=shifted, datetime_function={function_name=dateadd, level={canonical=DAY, text=day, form=KEYWORD}, increment={column={name=offset_days, table_ref=a}}, value={column={name=created_at, table_ref=b}}}}}, from={join={1={table={alias=a, table=events}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[shifted]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{accounts={created_at=[[@9,35:35='b',<419>,1:35]], id=[[@26,108:108='b',<419>,1:108]]}, events={account_id=[[@22,93:93='a',<419>,1:93]], offset_days=[[@5,20:20='a',<419>,1:20]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={shifted=[[@14,52:58='shifted',<419>,1:52]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={shifted=[[@14,52:58='shifted',<419>,1:52]]}, table_dictionary={accounts={created_at=[[@9,35:35='b',<419>,1:35]], id=[[@26,108:108='b',<419>,1:108]]}, events={account_id=[[@22,93:93='a',<419>,1:93]], offset_days=[[@5,20:20='a',<419>,1:20]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}], interface={shifted=[{name=offset_days, table_ref=a}, {name=created_at, table_ref=b}]}, table_alias={a=events, b=accounts}}}", extractor.getSymbolTable().toString());
	}

	@Test
	public void dateaddCollectsJoinedSourcesInBothArgumentFormulas() {
		final String query = "select dateadd(day, a.offset_days + b.extra_days * 2, b.created_at + (a.base_days + b.adjust_days)) as shifted from events a join accounts b on a.account_id = b.id";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=shifted, datetime_function={function_name=dateadd, level={canonical=DAY, text=day, form=KEYWORD}, increment={calc={left={column={name=offset_days, table_ref=a}}, right={calc={left={column={name=extra_days, table_ref=b}}, right={literal=2}, operator=*}}, operator=+}}, value={calc={left={column={name=created_at, table_ref=b}}, right={parentheses={calc={left={column={name=base_days, table_ref=a}}, right={column={name=adjust_days, table_ref=b}}, operator=+}}}, operator=+}}}}}, from={join={1={table={alias=a, table=events}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[shifted]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{accounts={adjust_days=[[@24,84:84='b',<419>,1:84]], created_at=[[@15,54:54='b',<419>,1:54]], extra_days=[[@9,36:36='b',<419>,1:36]], id=[[@42,159:159='b',<419>,1:159]]}, events={base_days=[[@20,70:70='a',<419>,1:70]], account_id=[[@38,144:144='a',<419>,1:144]], offset_days=[[@5,20:20='a',<419>,1:20]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={shifted=[[@30,103:109='shifted',<419>,1:103]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={shifted=[[@30,103:109='shifted',<419>,1:103]]}, table_dictionary={accounts={adjust_days=[[@24,84:84='b',<419>,1:84]], created_at=[[@15,54:54='b',<419>,1:54]], extra_days=[[@9,36:36='b',<419>,1:36]], id=[[@42,159:159='b',<419>,1:159]]}, events={base_days=[[@20,70:70='a',<419>,1:70]], account_id=[[@38,144:144='a',<419>,1:144]], offset_days=[[@5,20:20='a',<419>,1:20]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}], interface={shifted=[{name=offset_days, table_ref=a}, {name=extra_days, table_ref=b}, {name=created_at, table_ref=b}, {name=base_days, table_ref=a}, {name=adjust_days, table_ref=b}]}, table_alias={a=events, b=accounts}}}", extractor.getSymbolTable().toString());
	}

	@Test
	public void dateaddMatrixLiteralIncrementQualifiedColumnValue() {
		final String query = "select dateadd(day, 2, a.created_at) as shifted from events a";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=shifted, datetime_function={function_name=dateadd, level={canonical=DAY, text=day, form=KEYWORD}, increment={literal=2}, value={column={name=created_at, table_ref=a}}}}}, from={table={alias=a, table=events}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[shifted]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{events={created_at=[[@7,23:23='a',<419>,1:23]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={shifted=[[@12,40:46='shifted',<419>,1:40]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={shifted=[[@12,40:46='shifted',<419>,1:40]]}, table_dictionary={events={created_at=[[@7,23:23='a',<419>,1:23]]}}, interface={shifted=[{name=created_at, table_ref=a}]}, table_alias={a=events}}}", extractor.getSymbolTable().toString());
	}

	@Test
	public void dateaddMatrixLiteralIncrementUnqualifiedColumnValue() {
		final String query = "select dateadd(day, 2, created_at) as shifted from events a";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=shifted, datetime_function={function_name=dateadd, level={canonical=DAY, text=day, form=KEYWORD}, increment={literal=2}, value={column={name=created_at, table_ref=null}}}}}, from={table={alias=a, table=events}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[shifted]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{events={created_at=[[@7,23:32='created_at',<419>,1:23]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={shifted=[[@10,38:44='shifted',<419>,1:38]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={shifted=[[@10,38:44='shifted',<419>,1:38]]}, table_dictionary={events={created_at=[[@7,23:32='created_at',<419>,1:23]]}}, interface={shifted=[{name=created_at, table_ref=events}]}, table_alias={a=events}}}", extractor.getSymbolTable().toString());
	}

	@Test
	public void dateaddMatrixLiteralIncrementQualifiedFormulaValue() {
		final String query = "select dateadd(day, 2, a.created_at + (a.base_days + a.adjust_days)) as shifted from events a";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=shifted, datetime_function={function_name=dateadd, level={canonical=DAY, text=day, form=KEYWORD}, increment={literal=2}, value={calc={left={column={name=created_at, table_ref=a}}, right={parentheses={calc={left={column={name=base_days, table_ref=a}}, right={column={name=adjust_days, table_ref=a}}, operator=+}}}, operator=+}}}}}, from={table={alias=a, table=events}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[shifted]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{events={base_days=[[@12,39:39='a',<419>,1:39]], adjust_days=[[@16,53:53='a',<419>,1:53]], created_at=[[@7,23:23='a',<419>,1:23]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={shifted=[[@22,72:78='shifted',<419>,1:72]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={shifted=[[@22,72:78='shifted',<419>,1:72]]}, table_dictionary={events={base_days=[[@12,39:39='a',<419>,1:39]], adjust_days=[[@16,53:53='a',<419>,1:53]], created_at=[[@7,23:23='a',<419>,1:23]]}}, interface={shifted=[{name=created_at, table_ref=a}, {name=base_days, table_ref=a}, {name=adjust_days, table_ref=a}]}, table_alias={a=events}}}", extractor.getSymbolTable().toString());
	}

	@Test
	public void dateaddMatrixLiteralIncrementUnqualifiedFormulaValue() {
		final String query = "select dateadd(day, 2, created_at + (base_days + adjust_days)) as shifted from events a";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=shifted, datetime_function={function_name=dateadd, level={canonical=DAY, text=day, form=KEYWORD}, increment={literal=2}, value={calc={left={column={name=created_at, table_ref=null}}, right={parentheses={calc={left={column={name=base_days, table_ref=null}}, right={column={name=adjust_days, table_ref=null}}, operator=+}}}, operator=+}}}}}, from={table={alias=a, table=events}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[shifted]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{events={base_days=[[@10,37:45='base_days',<419>,1:37]], adjust_days=[[@12,49:59='adjust_days',<419>,1:49]], created_at=[[@7,23:32='created_at',<419>,1:23]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={shifted=[[@16,66:72='shifted',<419>,1:66]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={shifted=[[@16,66:72='shifted',<419>,1:66]]}, table_dictionary={events={base_days=[[@10,37:45='base_days',<419>,1:37]], adjust_days=[[@12,49:59='adjust_days',<419>,1:49]], created_at=[[@7,23:32='created_at',<419>,1:23]]}}, interface={shifted=[{name=created_at, table_ref=events}, {name=base_days, table_ref=events}, {name=adjust_days, table_ref=events}]}, table_alias={a=events}}}", extractor.getSymbolTable().toString());
	}

	@Test
	public void dateaddMatrixQualifiedColumnIncrementLiteralValue() {
		final String query = "select dateadd(day, a.offset_days, DATE '2026-01-01') as shifted from events a";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=shifted, datetime_function={function_name=dateadd, level={canonical=DAY, text=day, form=KEYWORD}, increment={column={name=offset_days, table_ref=a}}, value={source_type=DATE, literal=2026-01-01}}}}, from={table={alias=a, table=events}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[shifted]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{events={offset_days=[[@5,20:20='a',<419>,1:20]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={shifted=[[@13,57:63='shifted',<419>,1:57]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={shifted=[[@13,57:63='shifted',<419>,1:57]]}, table_dictionary={events={offset_days=[[@5,20:20='a',<419>,1:20]]}}, interface={shifted=[{name=offset_days, table_ref=a}]}, table_alias={a=events}}}", extractor.getSymbolTable().toString());
	}

	@Test
	public void dateaddMatrixQualifiedColumnIncrementQualifiedColumnValue() {
		final String query = "select dateadd(day, a.offset_days, a.created_at) as shifted from events a";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=shifted, datetime_function={function_name=dateadd, level={canonical=DAY, text=day, form=KEYWORD}, increment={column={name=offset_days, table_ref=a}}, value={column={name=created_at, table_ref=a}}}}}, from={table={alias=a, table=events}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[shifted]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{events={created_at=[[@9,35:35='a',<419>,1:35]], offset_days=[[@5,20:20='a',<419>,1:20]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={shifted=[[@14,52:58='shifted',<419>,1:52]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={shifted=[[@14,52:58='shifted',<419>,1:52]]}, table_dictionary={events={created_at=[[@9,35:35='a',<419>,1:35]], offset_days=[[@5,20:20='a',<419>,1:20]]}}, interface={shifted=[{name=offset_days, table_ref=a}, {name=created_at, table_ref=a}]}, table_alias={a=events}}}", extractor.getSymbolTable().toString());
	}

	@Test
	public void dateaddMatrixQualifiedColumnIncrementUnqualifiedColumnValue() {
		final String query = "select dateadd(day, a.offset_days, created_at) as shifted from events a";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=shifted, datetime_function={function_name=dateadd, level={canonical=DAY, text=day, form=KEYWORD}, increment={column={name=offset_days, table_ref=a}}, value={column={name=created_at, table_ref=null}}}}}, from={table={alias=a, table=events}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[shifted]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{events={created_at=[[@9,35:44='created_at',<419>,1:35]], offset_days=[[@5,20:20='a',<419>,1:20]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={shifted=[[@12,50:56='shifted',<419>,1:50]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={shifted=[[@12,50:56='shifted',<419>,1:50]]}, table_dictionary={events={created_at=[[@9,35:44='created_at',<419>,1:35]], offset_days=[[@5,20:20='a',<419>,1:20]]}}, interface={shifted=[{name=offset_days, table_ref=a}, {name=created_at, table_ref=events}]}, table_alias={a=events}}}", extractor.getSymbolTable().toString());
	}

	@Test
	public void dateaddMatrixQualifiedColumnIncrementQualifiedFormulaValue() {
		final String query = "select dateadd(day, a.offset_days, a.created_at + (a.base_days + a.adjust_days)) as shifted from events a";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=shifted, datetime_function={function_name=dateadd, level={canonical=DAY, text=day, form=KEYWORD}, increment={column={name=offset_days, table_ref=a}}, value={calc={left={column={name=created_at, table_ref=a}}, right={parentheses={calc={left={column={name=base_days, table_ref=a}}, right={column={name=adjust_days, table_ref=a}}, operator=+}}}, operator=+}}}}}, from={table={alias=a, table=events}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[shifted]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{events={base_days=[[@14,51:51='a',<419>,1:51]], adjust_days=[[@18,65:65='a',<419>,1:65]], created_at=[[@9,35:35='a',<419>,1:35]], offset_days=[[@5,20:20='a',<419>,1:20]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={shifted=[[@24,84:90='shifted',<419>,1:84]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={shifted=[[@24,84:90='shifted',<419>,1:84]]}, table_dictionary={events={base_days=[[@14,51:51='a',<419>,1:51]], adjust_days=[[@18,65:65='a',<419>,1:65]], created_at=[[@9,35:35='a',<419>,1:35]], offset_days=[[@5,20:20='a',<419>,1:20]]}}, interface={shifted=[{name=offset_days, table_ref=a}, {name=created_at, table_ref=a}, {name=base_days, table_ref=a}, {name=adjust_days, table_ref=a}]}, table_alias={a=events}}}", extractor.getSymbolTable().toString());
	}

	@Test
	public void dateaddMatrixQualifiedColumnIncrementUnqualifiedFormulaValue() {
		final String query = "select dateadd(day, a.offset_days, created_at + (base_days + adjust_days)) as shifted from events a";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=shifted, datetime_function={function_name=dateadd, level={canonical=DAY, text=day, form=KEYWORD}, increment={column={name=offset_days, table_ref=a}}, value={calc={left={column={name=created_at, table_ref=null}}, right={parentheses={calc={left={column={name=base_days, table_ref=null}}, right={column={name=adjust_days, table_ref=null}}, operator=+}}}, operator=+}}}}}, from={table={alias=a, table=events}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[shifted]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{events={base_days=[[@12,49:57='base_days',<419>,1:49]], adjust_days=[[@14,61:71='adjust_days',<419>,1:61]], created_at=[[@9,35:44='created_at',<419>,1:35]], offset_days=[[@5,20:20='a',<419>,1:20]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={shifted=[[@18,78:84='shifted',<419>,1:78]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={shifted=[[@18,78:84='shifted',<419>,1:78]]}, table_dictionary={events={base_days=[[@12,49:57='base_days',<419>,1:49]], adjust_days=[[@14,61:71='adjust_days',<419>,1:61]], created_at=[[@9,35:44='created_at',<419>,1:35]], offset_days=[[@5,20:20='a',<419>,1:20]]}}, interface={shifted=[{name=offset_days, table_ref=a}, {name=created_at, table_ref=events}, {name=base_days, table_ref=events}, {name=adjust_days, table_ref=events}]}, table_alias={a=events}}}", extractor.getSymbolTable().toString());
	}

	@Test
	public void dateaddMatrixUnqualifiedColumnIncrementLiteralValue() {
		final String query = "select dateadd(day, offset_days, DATE '2026-01-01') as shifted from events a";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=shifted, datetime_function={function_name=dateadd, level={canonical=DAY, text=day, form=KEYWORD}, increment={column={name=offset_days, table_ref=null}}, value={source_type=DATE, literal=2026-01-01}}}}, from={table={alias=a, table=events}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[shifted]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{events={offset_days=[[@5,20:30='offset_days',<419>,1:20]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={shifted=[[@11,55:61='shifted',<419>,1:55]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={shifted=[[@11,55:61='shifted',<419>,1:55]]}, table_dictionary={events={offset_days=[[@5,20:30='offset_days',<419>,1:20]]}}, interface={shifted=[{name=offset_days, table_ref=events}]}, table_alias={a=events}}}", extractor.getSymbolTable().toString());
	}

	@Test
	public void dateaddMatrixUnqualifiedColumnIncrementQualifiedColumnValue() {
		final String query = "select dateadd(day, offset_days, a.created_at) as shifted from events a";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=shifted, datetime_function={function_name=dateadd, level={canonical=DAY, text=day, form=KEYWORD}, increment={column={name=offset_days, table_ref=null}}, value={column={name=created_at, table_ref=a}}}}}, from={table={alias=a, table=events}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[shifted]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{events={created_at=[[@7,33:33='a',<419>,1:33]], offset_days=[[@5,20:30='offset_days',<419>,1:20]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={shifted=[[@12,50:56='shifted',<419>,1:50]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={shifted=[[@12,50:56='shifted',<419>,1:50]]}, table_dictionary={events={created_at=[[@7,33:33='a',<419>,1:33]], offset_days=[[@5,20:30='offset_days',<419>,1:20]]}}, interface={shifted=[{name=offset_days, table_ref=events}, {name=created_at, table_ref=a}]}, table_alias={a=events}}}", extractor.getSymbolTable().toString());
	}

	@Test
	public void dateaddMatrixUnqualifiedColumnIncrementUnqualifiedColumnValue() {
		final String query = "select dateadd(day, offset_days, created_at) as shifted from events a";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=shifted, datetime_function={function_name=dateadd, level={canonical=DAY, text=day, form=KEYWORD}, increment={column={name=offset_days, table_ref=null}}, value={column={name=created_at, table_ref=null}}}}}, from={table={alias=a, table=events}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[shifted]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{events={created_at=[[@7,33:42='created_at',<419>,1:33]], offset_days=[[@5,20:30='offset_days',<419>,1:20]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={shifted=[[@10,48:54='shifted',<419>,1:48]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={shifted=[[@10,48:54='shifted',<419>,1:48]]}, table_dictionary={events={created_at=[[@7,33:42='created_at',<419>,1:33]], offset_days=[[@5,20:30='offset_days',<419>,1:20]]}}, interface={shifted=[{name=offset_days, table_ref=events}, {name=created_at, table_ref=events}]}, table_alias={a=events}}}", extractor.getSymbolTable().toString());
	}

	@Test
	public void dateaddMatrixUnqualifiedColumnIncrementQualifiedFormulaValue() {
		final String query = "select dateadd(day, offset_days, a.created_at + (a.base_days + a.adjust_days)) as shifted from events a";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=shifted, datetime_function={function_name=dateadd, level={canonical=DAY, text=day, form=KEYWORD}, increment={column={name=offset_days, table_ref=null}}, value={calc={left={column={name=created_at, table_ref=a}}, right={parentheses={calc={left={column={name=base_days, table_ref=a}}, right={column={name=adjust_days, table_ref=a}}, operator=+}}}, operator=+}}}}}, from={table={alias=a, table=events}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[shifted]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{events={base_days=[[@12,49:49='a',<419>,1:49]], adjust_days=[[@16,63:63='a',<419>,1:63]], created_at=[[@7,33:33='a',<419>,1:33]], offset_days=[[@5,20:30='offset_days',<419>,1:20]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={shifted=[[@22,82:88='shifted',<419>,1:82]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={shifted=[[@22,82:88='shifted',<419>,1:82]]}, table_dictionary={events={base_days=[[@12,49:49='a',<419>,1:49]], adjust_days=[[@16,63:63='a',<419>,1:63]], created_at=[[@7,33:33='a',<419>,1:33]], offset_days=[[@5,20:30='offset_days',<419>,1:20]]}}, interface={shifted=[{name=offset_days, table_ref=events}, {name=created_at, table_ref=a}, {name=base_days, table_ref=a}, {name=adjust_days, table_ref=a}]}, table_alias={a=events}}}", extractor.getSymbolTable().toString());
	}

	@Test
	public void dateaddMatrixUnqualifiedColumnIncrementUnqualifiedFormulaValue() {
		final String query = "select dateadd(day, offset_days, created_at + (base_days + adjust_days)) as shifted from events a";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=shifted, datetime_function={function_name=dateadd, level={canonical=DAY, text=day, form=KEYWORD}, increment={column={name=offset_days, table_ref=null}}, value={calc={left={column={name=created_at, table_ref=null}}, right={parentheses={calc={left={column={name=base_days, table_ref=null}}, right={column={name=adjust_days, table_ref=null}}, operator=+}}}, operator=+}}}}}, from={table={alias=a, table=events}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[shifted]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{events={base_days=[[@10,47:55='base_days',<419>,1:47]], adjust_days=[[@12,59:69='adjust_days',<419>,1:59]], created_at=[[@7,33:42='created_at',<419>,1:33]], offset_days=[[@5,20:30='offset_days',<419>,1:20]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={shifted=[[@16,76:82='shifted',<419>,1:76]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={shifted=[[@16,76:82='shifted',<419>,1:76]]}, table_dictionary={events={base_days=[[@10,47:55='base_days',<419>,1:47]], adjust_days=[[@12,59:69='adjust_days',<419>,1:59]], created_at=[[@7,33:42='created_at',<419>,1:33]], offset_days=[[@5,20:30='offset_days',<419>,1:20]]}}, interface={shifted=[{name=offset_days, table_ref=events}, {name=created_at, table_ref=events}, {name=base_days, table_ref=events}, {name=adjust_days, table_ref=events}]}, table_alias={a=events}}}", extractor.getSymbolTable().toString());
	}

	@Test
	public void dateaddMatrixQualifiedFormulaIncrementLiteralValue() {
		final String query = "select dateadd(day, a.offset_days + a.extra_days * 2, DATE '2026-01-01') as shifted from events a";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=shifted, datetime_function={function_name=dateadd, level={canonical=DAY, text=day, form=KEYWORD}, increment={calc={left={column={name=offset_days, table_ref=a}}, right={calc={left={column={name=extra_days, table_ref=a}}, right={literal=2}, operator=*}}, operator=+}}, value={source_type=DATE, literal=2026-01-01}}}}, from={table={alias=a, table=events}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[shifted]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{events={extra_days=[[@9,36:36='a',<419>,1:36]], offset_days=[[@5,20:20='a',<419>,1:20]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={shifted=[[@19,76:82='shifted',<419>,1:76]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={shifted=[[@19,76:82='shifted',<419>,1:76]]}, table_dictionary={events={extra_days=[[@9,36:36='a',<419>,1:36]], offset_days=[[@5,20:20='a',<419>,1:20]]}}, interface={shifted=[{name=offset_days, table_ref=a}, {name=extra_days, table_ref=a}]}, table_alias={a=events}}}", extractor.getSymbolTable().toString());
	}

	@Test
	public void dateaddMatrixQualifiedFormulaIncrementQualifiedColumnValue() {
		final String query = "select dateadd(day, a.offset_days + a.extra_days * 2, a.created_at) as shifted from events a";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=shifted, datetime_function={function_name=dateadd, level={canonical=DAY, text=day, form=KEYWORD}, increment={calc={left={column={name=offset_days, table_ref=a}}, right={calc={left={column={name=extra_days, table_ref=a}}, right={literal=2}, operator=*}}, operator=+}}, value={column={name=created_at, table_ref=a}}}}}, from={table={alias=a, table=events}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[shifted]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{events={created_at=[[@15,54:54='a',<419>,1:54]], extra_days=[[@9,36:36='a',<419>,1:36]], offset_days=[[@5,20:20='a',<419>,1:20]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={shifted=[[@20,71:77='shifted',<419>,1:71]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={shifted=[[@20,71:77='shifted',<419>,1:71]]}, table_dictionary={events={created_at=[[@15,54:54='a',<419>,1:54]], extra_days=[[@9,36:36='a',<419>,1:36]], offset_days=[[@5,20:20='a',<419>,1:20]]}}, interface={shifted=[{name=offset_days, table_ref=a}, {name=extra_days, table_ref=a}, {name=created_at, table_ref=a}]}, table_alias={a=events}}}", extractor.getSymbolTable().toString());
	}

	@Test
	public void dateaddMatrixQualifiedFormulaIncrementUnqualifiedColumnValue() {
		final String query = "select dateadd(day, a.offset_days + a.extra_days * 2, created_at) as shifted from events a";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=shifted, datetime_function={function_name=dateadd, level={canonical=DAY, text=day, form=KEYWORD}, increment={calc={left={column={name=offset_days, table_ref=a}}, right={calc={left={column={name=extra_days, table_ref=a}}, right={literal=2}, operator=*}}, operator=+}}, value={column={name=created_at, table_ref=null}}}}}, from={table={alias=a, table=events}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[shifted]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{events={created_at=[[@15,54:63='created_at',<419>,1:54]], extra_days=[[@9,36:36='a',<419>,1:36]], offset_days=[[@5,20:20='a',<419>,1:20]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={shifted=[[@18,69:75='shifted',<419>,1:69]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={shifted=[[@18,69:75='shifted',<419>,1:69]]}, table_dictionary={events={created_at=[[@15,54:63='created_at',<419>,1:54]], extra_days=[[@9,36:36='a',<419>,1:36]], offset_days=[[@5,20:20='a',<419>,1:20]]}}, interface={shifted=[{name=offset_days, table_ref=a}, {name=extra_days, table_ref=a}, {name=created_at, table_ref=events}]}, table_alias={a=events}}}", extractor.getSymbolTable().toString());
	}

	@Test
	public void dateaddMatrixQualifiedFormulaIncrementQualifiedFormulaValue() {
		final String query = "select dateadd(day, a.offset_days + a.extra_days * 2, a.created_at + (a.base_days + a.adjust_days)) as shifted from events a";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=shifted, datetime_function={function_name=dateadd, level={canonical=DAY, text=day, form=KEYWORD}, increment={calc={left={column={name=offset_days, table_ref=a}}, right={calc={left={column={name=extra_days, table_ref=a}}, right={literal=2}, operator=*}}, operator=+}}, value={calc={left={column={name=created_at, table_ref=a}}, right={parentheses={calc={left={column={name=base_days, table_ref=a}}, right={column={name=adjust_days, table_ref=a}}, operator=+}}}, operator=+}}}}}, from={table={alias=a, table=events}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[shifted]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{events={base_days=[[@20,70:70='a',<419>,1:70]], adjust_days=[[@24,84:84='a',<419>,1:84]], created_at=[[@15,54:54='a',<419>,1:54]], extra_days=[[@9,36:36='a',<419>,1:36]], offset_days=[[@5,20:20='a',<419>,1:20]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={shifted=[[@30,103:109='shifted',<419>,1:103]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={shifted=[[@30,103:109='shifted',<419>,1:103]]}, table_dictionary={events={base_days=[[@20,70:70='a',<419>,1:70]], adjust_days=[[@24,84:84='a',<419>,1:84]], created_at=[[@15,54:54='a',<419>,1:54]], extra_days=[[@9,36:36='a',<419>,1:36]], offset_days=[[@5,20:20='a',<419>,1:20]]}}, interface={shifted=[{name=offset_days, table_ref=a}, {name=extra_days, table_ref=a}, {name=created_at, table_ref=a}, {name=base_days, table_ref=a}, {name=adjust_days, table_ref=a}]}, table_alias={a=events}}}", extractor.getSymbolTable().toString());
	}

	@Test
	public void dateaddMatrixQualifiedFormulaIncrementUnqualifiedFormulaValue() {
		final String query = "select dateadd(day, a.offset_days + a.extra_days * 2, created_at + (base_days + adjust_days)) as shifted from events a";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=shifted, datetime_function={function_name=dateadd, level={canonical=DAY, text=day, form=KEYWORD}, increment={calc={left={column={name=offset_days, table_ref=a}}, right={calc={left={column={name=extra_days, table_ref=a}}, right={literal=2}, operator=*}}, operator=+}}, value={calc={left={column={name=created_at, table_ref=null}}, right={parentheses={calc={left={column={name=base_days, table_ref=null}}, right={column={name=adjust_days, table_ref=null}}, operator=+}}}, operator=+}}}}}, from={table={alias=a, table=events}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[shifted]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{events={base_days=[[@18,68:76='base_days',<419>,1:68]], adjust_days=[[@20,80:90='adjust_days',<419>,1:80]], created_at=[[@15,54:63='created_at',<419>,1:54]], extra_days=[[@9,36:36='a',<419>,1:36]], offset_days=[[@5,20:20='a',<419>,1:20]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={shifted=[[@24,97:103='shifted',<419>,1:97]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={shifted=[[@24,97:103='shifted',<419>,1:97]]}, table_dictionary={events={base_days=[[@18,68:76='base_days',<419>,1:68]], adjust_days=[[@20,80:90='adjust_days',<419>,1:80]], created_at=[[@15,54:63='created_at',<419>,1:54]], extra_days=[[@9,36:36='a',<419>,1:36]], offset_days=[[@5,20:20='a',<419>,1:20]]}}, interface={shifted=[{name=offset_days, table_ref=a}, {name=extra_days, table_ref=a}, {name=created_at, table_ref=events}, {name=base_days, table_ref=events}, {name=adjust_days, table_ref=events}]}, table_alias={a=events}}}", extractor.getSymbolTable().toString());
	}

	@Test
	public void dateaddMatrixUnqualifiedFormulaIncrementLiteralValue() {
		final String query = "select dateadd(day, offset_days + extra_days * 2, DATE '2026-01-01') as shifted from events a";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=shifted, datetime_function={function_name=dateadd, level={canonical=DAY, text=day, form=KEYWORD}, increment={calc={left={column={name=offset_days, table_ref=null}}, right={calc={left={column={name=extra_days, table_ref=null}}, right={literal=2}, operator=*}}, operator=+}}, value={source_type=DATE, literal=2026-01-01}}}}, from={table={alias=a, table=events}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[shifted]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{events={offset_days=[[@5,20:30='offset_days',<419>,1:20]], extra_days=[[@7,34:43='extra_days',<419>,1:34]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={shifted=[[@15,72:78='shifted',<419>,1:72]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={shifted=[[@15,72:78='shifted',<419>,1:72]]}, table_dictionary={events={offset_days=[[@5,20:30='offset_days',<419>,1:20]], extra_days=[[@7,34:43='extra_days',<419>,1:34]]}}, interface={shifted=[{name=offset_days, table_ref=events}, {name=extra_days, table_ref=events}]}, table_alias={a=events}}}", extractor.getSymbolTable().toString());
	}

	@Test
	public void dateaddMatrixUnqualifiedFormulaIncrementQualifiedColumnValue() {
		final String query = "select dateadd(day, offset_days + extra_days * 2, a.created_at) as shifted from events a";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=shifted, datetime_function={function_name=dateadd, level={canonical=DAY, text=day, form=KEYWORD}, increment={calc={left={column={name=offset_days, table_ref=null}}, right={calc={left={column={name=extra_days, table_ref=null}}, right={literal=2}, operator=*}}, operator=+}}, value={column={name=created_at, table_ref=a}}}}}, from={table={alias=a, table=events}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[shifted]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{events={created_at=[[@11,50:50='a',<419>,1:50]], offset_days=[[@5,20:30='offset_days',<419>,1:20]], extra_days=[[@7,34:43='extra_days',<419>,1:34]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={shifted=[[@16,67:73='shifted',<419>,1:67]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={shifted=[[@16,67:73='shifted',<419>,1:67]]}, table_dictionary={events={created_at=[[@11,50:50='a',<419>,1:50]], offset_days=[[@5,20:30='offset_days',<419>,1:20]], extra_days=[[@7,34:43='extra_days',<419>,1:34]]}}, interface={shifted=[{name=offset_days, table_ref=events}, {name=extra_days, table_ref=events}, {name=created_at, table_ref=a}]}, table_alias={a=events}}}", extractor.getSymbolTable().toString());
	}

	@Test
	public void dateaddMatrixUnqualifiedFormulaIncrementUnqualifiedColumnValue() {
		final String query = "select dateadd(day, offset_days + extra_days * 2, created_at) as shifted from events a";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=shifted, datetime_function={function_name=dateadd, level={canonical=DAY, text=day, form=KEYWORD}, increment={calc={left={column={name=offset_days, table_ref=null}}, right={calc={left={column={name=extra_days, table_ref=null}}, right={literal=2}, operator=*}}, operator=+}}, value={column={name=created_at, table_ref=null}}}}}, from={table={alias=a, table=events}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[shifted]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{events={created_at=[[@11,50:59='created_at',<419>,1:50]], offset_days=[[@5,20:30='offset_days',<419>,1:20]], extra_days=[[@7,34:43='extra_days',<419>,1:34]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={shifted=[[@14,65:71='shifted',<419>,1:65]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={shifted=[[@14,65:71='shifted',<419>,1:65]]}, table_dictionary={events={created_at=[[@11,50:59='created_at',<419>,1:50]], offset_days=[[@5,20:30='offset_days',<419>,1:20]], extra_days=[[@7,34:43='extra_days',<419>,1:34]]}}, interface={shifted=[{name=offset_days, table_ref=events}, {name=extra_days, table_ref=events}, {name=created_at, table_ref=events}]}, table_alias={a=events}}}", extractor.getSymbolTable().toString());
	}

	@Test
	public void dateaddMatrixUnqualifiedFormulaIncrementQualifiedFormulaValue() {
		final String query = "select dateadd(day, offset_days + extra_days * 2, a.created_at + (a.base_days + a.adjust_days)) as shifted from events a";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=shifted, datetime_function={function_name=dateadd, level={canonical=DAY, text=day, form=KEYWORD}, increment={calc={left={column={name=offset_days, table_ref=null}}, right={calc={left={column={name=extra_days, table_ref=null}}, right={literal=2}, operator=*}}, operator=+}}, value={calc={left={column={name=created_at, table_ref=a}}, right={parentheses={calc={left={column={name=base_days, table_ref=a}}, right={column={name=adjust_days, table_ref=a}}, operator=+}}}, operator=+}}}}}, from={table={alias=a, table=events}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[shifted]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{events={base_days=[[@16,66:66='a',<419>,1:66]], adjust_days=[[@20,80:80='a',<419>,1:80]], created_at=[[@11,50:50='a',<419>,1:50]], offset_days=[[@5,20:30='offset_days',<419>,1:20]], extra_days=[[@7,34:43='extra_days',<419>,1:34]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={shifted=[[@26,99:105='shifted',<419>,1:99]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={shifted=[[@26,99:105='shifted',<419>,1:99]]}, table_dictionary={events={base_days=[[@16,66:66='a',<419>,1:66]], adjust_days=[[@20,80:80='a',<419>,1:80]], created_at=[[@11,50:50='a',<419>,1:50]], offset_days=[[@5,20:30='offset_days',<419>,1:20]], extra_days=[[@7,34:43='extra_days',<419>,1:34]]}}, interface={shifted=[{name=offset_days, table_ref=events}, {name=extra_days, table_ref=events}, {name=created_at, table_ref=a}, {name=base_days, table_ref=a}, {name=adjust_days, table_ref=a}]}, table_alias={a=events}}}", extractor.getSymbolTable().toString());
	}

	@Test
	public void dateaddMatrixUnqualifiedFormulaIncrementUnqualifiedFormulaValue() {
		final String query = "select dateadd(day, offset_days + extra_days * 2, created_at + (base_days + adjust_days)) as shifted from events a";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=shifted, datetime_function={function_name=dateadd, level={canonical=DAY, text=day, form=KEYWORD}, increment={calc={left={column={name=offset_days, table_ref=null}}, right={calc={left={column={name=extra_days, table_ref=null}}, right={literal=2}, operator=*}}, operator=+}}, value={calc={left={column={name=created_at, table_ref=null}}, right={parentheses={calc={left={column={name=base_days, table_ref=null}}, right={column={name=adjust_days, table_ref=null}}, operator=+}}}, operator=+}}}}}, from={table={alias=a, table=events}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[shifted]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{events={base_days=[[@14,64:72='base_days',<419>,1:64]], adjust_days=[[@16,76:86='adjust_days',<419>,1:76]], created_at=[[@11,50:59='created_at',<419>,1:50]], offset_days=[[@5,20:30='offset_days',<419>,1:20]], extra_days=[[@7,34:43='extra_days',<419>,1:34]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={shifted=[[@20,93:99='shifted',<419>,1:93]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={shifted=[[@20,93:99='shifted',<419>,1:93]]}, table_dictionary={events={base_days=[[@14,64:72='base_days',<419>,1:64]], adjust_days=[[@16,76:86='adjust_days',<419>,1:76]], created_at=[[@11,50:59='created_at',<419>,1:50]], offset_days=[[@5,20:30='offset_days',<419>,1:20]], extra_days=[[@7,34:43='extra_days',<419>,1:34]]}}, interface={shifted=[{name=offset_days, table_ref=events}, {name=extra_days, table_ref=events}, {name=created_at, table_ref=events}, {name=base_days, table_ref=events}, {name=adjust_days, table_ref=events}]}, table_alias={a=events}}}", extractor.getSymbolTable().toString());
	}

	@Test
	public void dateaddReportsAmbiguousUnqualifiedValue() {
		String query = "select dateadd(year, 1, created_at) as shifted from events a join accounts b on a.account_id = b.id";
		SqlParserAccess parser = new SqlParserAccess(false, false, false);
		parser.executeTheParse(query, "SQL");
		Assert.assertEquals(0, parser.getParser().getNumberOfSyntaxErrors());
		Assert.assertEquals(parser.getAllDiagnostics().toString(), 2, parser.getAllDiagnostics().size());
		assertDiagnosticAtPosition(parser.getSnippet(), "AMBIGUOUS_COLUMN_REFERENCE",
				ParseDiagnostic.Severity.SEVERE_WARNING,
				"Ambiguous column reference 'created_at' at (l:1 c:24). Possible sources: [accounts, events]",
				"created_at", 1, 24);
		assertDiagnosticAtPosition(parser.getSnippet(), "UNRESOLVED_UNQUALIFIED_COLUMNS",
				ParseDiagnostic.Severity.ERROR,
				"Unresolved unqualified column reference(s): [created_at [(l:1 c:24)]]",
				"created_at", 1, 24);
		ParseDiagnostic unresolved = findDiagnosticByCodeAndFragmentAndSeverity(parser.getSnippet(),
				"UNRESOLVED_UNQUALIFIED_COLUMNS", ParseDiagnostic.Severity.ERROR, "created_at");
		Assert.assertEquals("Unresolved unqualified column reference(s): [created_at [(l:1 c:24)]]", unresolved.message());
		Assert.assertEquals("created_at", unresolved.tokenText());
		Assert.assertEquals(Integer.valueOf(1), unresolved.line());
		Assert.assertEquals(Integer.valueOf(24), unresolved.charPositionInLine());
		Assert.assertEquals("{accounts={id=[[@22,95:95='b',<419>,1:95]]}, events={account_id=[[@18,80:80='a',<419>,1:80]]}}",
				parser.getSnippet().getTableDictionary().toString());
	}

	@Test
	public void dateaddReportsAmbiguousUnqualifiedIncrement() {
		String query = "select dateadd(day, offset_days, a.ts) as shifted from events a join accounts b on a.account_id = b.id";
		SqlParserAccess parser = new SqlParserAccess(false, false, false);
		parser.executeTheParse(query, "SQL");
		Assert.assertEquals(0, parser.getParser().getNumberOfSyntaxErrors());
		Assert.assertEquals(parser.getAllDiagnostics().toString(), 2, parser.getAllDiagnostics().size());
		assertDiagnosticAtPosition(parser.getSnippet(), "AMBIGUOUS_COLUMN_REFERENCE",
				ParseDiagnostic.Severity.SEVERE_WARNING,
				"Ambiguous column reference 'offset_days' at (l:1 c:20). Possible sources: [accounts, events]",
				"offset_days", 1, 20);
		ParseDiagnostic ambiguity = findDiagnosticByCodeAndFragmentAndSeverity(parser.getSnippet(),
				"AMBIGUOUS_COLUMN_REFERENCE", ParseDiagnostic.Severity.SEVERE_WARNING, "offset_days");
		Assert.assertEquals("Ambiguous column reference 'offset_days' at (l:1 c:20). Possible sources: [accounts, events]", ambiguity.message());
		Assert.assertEquals("offset_days", ambiguity.tokenText());
		assertDiagnosticAtPosition(parser.getSnippet(), "UNRESOLVED_UNQUALIFIED_COLUMNS",
				ParseDiagnostic.Severity.ERROR,
				"Unresolved unqualified column reference(s): [offset_days [(l:1 c:20)]]",
				"offset_days", 1, 20);
		ParseDiagnostic unresolved = findDiagnosticByCodeAndFragmentAndSeverity(parser.getSnippet(),
				"UNRESOLVED_UNQUALIFIED_COLUMNS", ParseDiagnostic.Severity.ERROR, "offset_days");
		Assert.assertEquals("Unresolved unqualified column reference(s): [offset_days [(l:1 c:20)]]", unresolved.message());
		Assert.assertEquals("offset_days", unresolved.tokenText());
		Assert.assertEquals(Integer.valueOf(1), unresolved.line());
		Assert.assertEquals(Integer.valueOf(20), unresolved.charPositionInLine());
		Assert.assertEquals("{accounts={id=[[@24,98:98='b',<419>,1:98]]}, events={account_id=[[@20,83:83='a',<419>,1:83]], ts=[[@7,33:33='a',<419>,1:33]]}}",
				parser.getSnippet().getTableDictionary().toString());
		Assert.assertEquals("{def_query0={query_dictionary={shifted=[[@12,42:48='shifted',<419>,1:42]]}, table_dictionary={accounts={id=[[@24,98:98='b',<419>,1:98]]}, events={account_id=[[@20,83:83='a',<419>,1:83]], ts=[[@7,33:33='a',<419>,1:33]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}], interface={shifted=[{name=offset_days, table_ref=null}, {name=ts, table_ref=a}]}, table_alias={a=events, b=accounts}}}",
				parser.getSnippet().getSymbolTable().toString());
	}

	@Test
	public void dateaddReportsAmbiguousColumnsInBothArguments() {
		String query = "select dateadd(day, offset_days, created_at) as shifted from events a join accounts b on a.account_id = b.id";
		SqlParserAccess parser = new SqlParserAccess(false, false, false);
		parser.executeTheParse(query, "SQL");
		Assert.assertEquals(0, parser.getParser().getNumberOfSyntaxErrors());
		Assert.assertEquals(parser.getAllDiagnostics().toString(), 3, parser.getAllDiagnostics().size());
		assertDiagnosticAtPosition(parser.getSnippet(), "AMBIGUOUS_COLUMN_REFERENCE", ParseDiagnostic.Severity.SEVERE_WARNING,
				"Ambiguous column reference 'offset_days' at (l:1 c:20). Possible sources: [accounts, events]", "offset_days", 1, 20);
		assertDiagnosticAtPosition(parser.getSnippet(), "AMBIGUOUS_COLUMN_REFERENCE", ParseDiagnostic.Severity.SEVERE_WARNING,
				"Ambiguous column reference 'created_at' at (l:1 c:33). Possible sources: [accounts, events]", "created_at", 1, 33);
		ParseDiagnostic incrementAmbiguity = findDiagnosticByCodeAndFragmentAndSeverity(parser.getSnippet(),
				"AMBIGUOUS_COLUMN_REFERENCE", ParseDiagnostic.Severity.SEVERE_WARNING, "offset_days");
		Assert.assertEquals("Ambiguous column reference 'offset_days' at (l:1 c:20). Possible sources: [accounts, events]", incrementAmbiguity.message());
		Assert.assertEquals("offset_days", incrementAmbiguity.tokenText());
		ParseDiagnostic valueAmbiguity = findDiagnosticByCodeAndFragmentAndSeverity(parser.getSnippet(),
				"AMBIGUOUS_COLUMN_REFERENCE", ParseDiagnostic.Severity.SEVERE_WARNING, "created_at");
		Assert.assertEquals("Ambiguous column reference 'created_at' at (l:1 c:33). Possible sources: [accounts, events]", valueAmbiguity.message());
		Assert.assertEquals("created_at", valueAmbiguity.tokenText());
		assertDiagnosticAtPosition(parser.getSnippet(), "UNRESOLVED_UNQUALIFIED_COLUMNS", ParseDiagnostic.Severity.ERROR,
				null, "offset_days", 1, 20);
		assertDiagnosticAtPosition(parser.getSnippet(), "UNRESOLVED_UNQUALIFIED_COLUMNS", ParseDiagnostic.Severity.ERROR,
				null, "created_at", 1, 33);
		ParseDiagnostic unresolved = findDiagnosticByCodeAndFragmentAndSeverity(parser.getSnippet(),
				"UNRESOLVED_UNQUALIFIED_COLUMNS", ParseDiagnostic.Severity.ERROR, "created_at");
		Assert.assertEquals("Unresolved unqualified column reference(s): [created_at [(l:1 c:33)], offset_days [(l:1 c:20)]]", unresolved.message());
		Assert.assertEquals("created_at, offset_days", unresolved.tokenText());
		Assert.assertEquals(Integer.valueOf(1), unresolved.line());
		Assert.assertEquals(Integer.valueOf(33), unresolved.charPositionInLine());
		Assert.assertEquals("{def_query0={query_dictionary={shifted=[[@10,48:54='shifted',<419>,1:48]]}, table_dictionary={accounts={id=[[@22,104:104='b',<419>,1:104]]}, events={account_id=[[@18,89:89='a',<419>,1:89]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}], interface={shifted=[{name=offset_days, table_ref=null}, {name=created_at, table_ref=null}]}, table_alias={a=events, b=accounts}}}",
				parser.getSnippet().getSymbolTable().toString());
	}

	@Test
	public void dateaddResolvesMixedQualificationInIncrementFormula() {
		final String query = "select dateadd(day, a.offset_days + extra_days, a.created_at) as shifted from events a";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=shifted, datetime_function={function_name=dateadd, level={canonical=DAY, text=day, form=KEYWORD}, increment={calc={left={column={name=offset_days, table_ref=a}}, right={column={name=extra_days, table_ref=null}}, operator=+}}, value={column={name=created_at, table_ref=a}}}}}, from={table={alias=a, table=events}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[shifted]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{events={created_at=[[@11,48:48='a',<419>,1:48]], offset_days=[[@5,20:20='a',<419>,1:20]], extra_days=[[@9,36:45='extra_days',<419>,1:36]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={shifted=[[@16,65:71='shifted',<419>,1:65]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={shifted=[[@16,65:71='shifted',<419>,1:65]]}, table_dictionary={events={created_at=[[@11,48:48='a',<419>,1:48]], offset_days=[[@5,20:20='a',<419>,1:20]], extra_days=[[@9,36:45='extra_days',<419>,1:36]]}}, interface={shifted=[{name=offset_days, table_ref=a}, {name=extra_days, table_ref=events}, {name=created_at, table_ref=a}]}, table_alias={a=events}}}", extractor.getSymbolTable().toString());
	}

	@Test
	public void dateaddResolvesMixedQualificationInValueFormula() {
		final String query = "select dateadd(day, 2, a.created_at + (base_days + a.adjust_days)) as shifted from events a";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=shifted, datetime_function={function_name=dateadd, level={canonical=DAY, text=day, form=KEYWORD}, increment={literal=2}, value={calc={left={column={name=created_at, table_ref=a}}, right={parentheses={calc={left={column={name=base_days, table_ref=null}}, right={column={name=adjust_days, table_ref=a}}, operator=+}}}, operator=+}}}}}, from={table={alias=a, table=events}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[shifted]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{events={base_days=[[@12,39:47='base_days',<419>,1:39]], adjust_days=[[@14,51:51='a',<419>,1:51]], created_at=[[@7,23:23='a',<419>,1:23]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={shifted=[[@20,70:76='shifted',<419>,1:70]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={shifted=[[@20,70:76='shifted',<419>,1:70]]}, table_dictionary={events={base_days=[[@12,39:47='base_days',<419>,1:39]], adjust_days=[[@14,51:51='a',<419>,1:51]], created_at=[[@7,23:23='a',<419>,1:23]]}}, interface={shifted=[{name=created_at, table_ref=a}, {name=base_days, table_ref=events}, {name=adjust_days, table_ref=a}]}, table_alias={a=events}}}", extractor.getSymbolTable().toString());
	}

	@Test
	public void dateaddResolvesMixedQualificationInBothArgumentFormulas() {
		final String query = "select dateadd(day, a.offset_days + extra_days, a.created_at + (base_days + a.adjust_days)) as shifted from events a";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=shifted, datetime_function={function_name=dateadd, level={canonical=DAY, text=day, form=KEYWORD}, increment={calc={left={column={name=offset_days, table_ref=a}}, right={column={name=extra_days, table_ref=null}}, operator=+}}, value={calc={left={column={name=created_at, table_ref=a}}, right={parentheses={calc={left={column={name=base_days, table_ref=null}}, right={column={name=adjust_days, table_ref=a}}, operator=+}}}, operator=+}}}}}, from={table={alias=a, table=events}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[shifted]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{events={base_days=[[@16,64:72='base_days',<419>,1:64]], adjust_days=[[@18,76:76='a',<419>,1:76]], created_at=[[@11,48:48='a',<419>,1:48]], offset_days=[[@5,20:20='a',<419>,1:20]], extra_days=[[@9,36:45='extra_days',<419>,1:36]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={shifted=[[@24,95:101='shifted',<419>,1:95]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={shifted=[[@24,95:101='shifted',<419>,1:95]]}, table_dictionary={events={base_days=[[@16,64:72='base_days',<419>,1:64]], adjust_days=[[@18,76:76='a',<419>,1:76]], created_at=[[@11,48:48='a',<419>,1:48]], offset_days=[[@5,20:20='a',<419>,1:20]], extra_days=[[@9,36:45='extra_days',<419>,1:36]]}}, interface={shifted=[{name=offset_days, table_ref=a}, {name=extra_days, table_ref=events}, {name=created_at, table_ref=a}, {name=base_days, table_ref=events}, {name=adjust_days, table_ref=a}]}, table_alias={a=events}}}", extractor.getSymbolTable().toString());
	}

	@Test
	public void dateaddReportsOnlyAmbiguousTermInIncrementFormula() {
		String query = "select dateadd(day, a.offset_days + extra_days, a.created_at) as shifted from events a join accounts b on a.account_id = b.id";
		SqlParserAccess parser = new SqlParserAccess(false, false, false);
		parser.executeTheParse(query, "SQL");
		Assert.assertEquals(0, parser.getParser().getNumberOfSyntaxErrors());
		Assert.assertEquals(parser.getAllDiagnostics().toString(), 2, parser.getAllDiagnostics().size());
		assertDiagnosticAtPosition(parser.getSnippet(), "AMBIGUOUS_COLUMN_REFERENCE", ParseDiagnostic.Severity.SEVERE_WARNING,
				"Ambiguous column reference 'extra_days' at (l:1 c:36). Possible sources: [accounts, events]", "extra_days", 1, 36);
		ParseDiagnostic ambiguity = findDiagnosticByCodeAndFragmentAndSeverity(parser.getSnippet(),
				"AMBIGUOUS_COLUMN_REFERENCE", ParseDiagnostic.Severity.SEVERE_WARNING, "extra_days");
		Assert.assertEquals("Ambiguous column reference 'extra_days' at (l:1 c:36). Possible sources: [accounts, events]", ambiguity.message());
		Assert.assertEquals("extra_days", ambiguity.tokenText());
		assertDiagnosticAtPosition(parser.getSnippet(), "UNRESOLVED_UNQUALIFIED_COLUMNS", ParseDiagnostic.Severity.ERROR,
				"Unresolved unqualified column reference(s): [extra_days [(l:1 c:36)]]", "extra_days", 1, 36);
		ParseDiagnostic unresolved = findDiagnosticByCodeAndFragmentAndSeverity(parser.getSnippet(),
				"UNRESOLVED_UNQUALIFIED_COLUMNS", ParseDiagnostic.Severity.ERROR, "extra_days");
		Assert.assertEquals("Unresolved unqualified column reference(s): [extra_days [(l:1 c:36)]]", unresolved.message());
		Assert.assertEquals("extra_days", unresolved.tokenText());
		Assert.assertEquals(Integer.valueOf(1), unresolved.line());
		Assert.assertEquals(Integer.valueOf(36), unresolved.charPositionInLine());
		Assert.assertEquals("{def_query0={query_dictionary={shifted=[[@16,65:71='shifted',<419>,1:65]]}, table_dictionary={accounts={id=[[@28,121:121='b',<419>,1:121]]}, events={account_id=[[@24,106:106='a',<419>,1:106]], created_at=[[@11,48:48='a',<419>,1:48]], offset_days=[[@5,20:20='a',<419>,1:20]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}], interface={shifted=[{name=offset_days, table_ref=a}, {name=extra_days, table_ref=null}, {name=created_at, table_ref=a}]}, table_alias={a=events, b=accounts}}}",
				parser.getSnippet().getSymbolTable().toString());
	}

	@Test
	public void dateaddReportsOnlyAmbiguousTermInValueFormula() {
		String query = "select dateadd(day, 2, a.created_at + (base_days + a.adjust_days)) as shifted from events a join accounts b on a.account_id = b.id";
		SqlParserAccess parser = new SqlParserAccess(false, false, false);
		parser.executeTheParse(query, "SQL");
		Assert.assertEquals(0, parser.getParser().getNumberOfSyntaxErrors());
		Assert.assertEquals(parser.getAllDiagnostics().toString(), 2, parser.getAllDiagnostics().size());
		assertDiagnosticAtPosition(parser.getSnippet(), "AMBIGUOUS_COLUMN_REFERENCE", ParseDiagnostic.Severity.SEVERE_WARNING,
				"Ambiguous column reference 'base_days' at (l:1 c:39). Possible sources: [accounts, events]", "base_days", 1, 39);
		ParseDiagnostic ambiguity = findDiagnosticByCodeAndFragmentAndSeverity(parser.getSnippet(),
				"AMBIGUOUS_COLUMN_REFERENCE", ParseDiagnostic.Severity.SEVERE_WARNING, "base_days");
		Assert.assertEquals("Ambiguous column reference 'base_days' at (l:1 c:39). Possible sources: [accounts, events]", ambiguity.message());
		Assert.assertEquals("base_days", ambiguity.tokenText());
		assertDiagnosticAtPosition(parser.getSnippet(), "UNRESOLVED_UNQUALIFIED_COLUMNS", ParseDiagnostic.Severity.ERROR,
				"Unresolved unqualified column reference(s): [base_days [(l:1 c:39)]]", "base_days", 1, 39);
		ParseDiagnostic unresolved = findDiagnosticByCodeAndFragmentAndSeverity(parser.getSnippet(),
				"UNRESOLVED_UNQUALIFIED_COLUMNS", ParseDiagnostic.Severity.ERROR, "base_days");
		Assert.assertEquals("Unresolved unqualified column reference(s): [base_days [(l:1 c:39)]]", unresolved.message());
		Assert.assertEquals("base_days", unresolved.tokenText());
		Assert.assertEquals(Integer.valueOf(1), unresolved.line());
		Assert.assertEquals(Integer.valueOf(39), unresolved.charPositionInLine());
		Assert.assertEquals("{def_query0={query_dictionary={shifted=[[@20,70:76='shifted',<419>,1:70]]}, table_dictionary={accounts={id=[[@32,126:126='b',<419>,1:126]]}, events={account_id=[[@28,111:111='a',<419>,1:111]], adjust_days=[[@14,51:51='a',<419>,1:51]], created_at=[[@7,23:23='a',<419>,1:23]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}], interface={shifted=[{name=created_at, table_ref=a}, {name=base_days, table_ref=null}, {name=adjust_days, table_ref=a}]}, table_alias={a=events, b=accounts}}}",
				parser.getSnippet().getSymbolTable().toString());
	}

	@Test
	public void dateaddReportsOnlyAmbiguousTermsInBothArgumentFormulas() {
		String query = "select dateadd(day, a.offset_days + extra_days, a.created_at + (base_days + a.adjust_days)) as shifted from events a join accounts b on a.account_id = b.id";
		SqlParserAccess parser = new SqlParserAccess(false, false, false);
		parser.executeTheParse(query, "SQL");
		Assert.assertEquals(0, parser.getParser().getNumberOfSyntaxErrors());
		Assert.assertEquals(parser.getAllDiagnostics().toString(), 3, parser.getAllDiagnostics().size());
		assertDiagnosticAtPosition(parser.getSnippet(), "AMBIGUOUS_COLUMN_REFERENCE", ParseDiagnostic.Severity.SEVERE_WARNING,
				"Ambiguous column reference 'extra_days' at (l:1 c:36). Possible sources: [accounts, events]", "extra_days", 1, 36);
		assertDiagnosticAtPosition(parser.getSnippet(), "AMBIGUOUS_COLUMN_REFERENCE", ParseDiagnostic.Severity.SEVERE_WARNING,
				"Ambiguous column reference 'base_days' at (l:1 c:64). Possible sources: [accounts, events]", "base_days", 1, 64);
		ParseDiagnostic incrementAmbiguity = findDiagnosticByCodeAndFragmentAndSeverity(parser.getSnippet(),
				"AMBIGUOUS_COLUMN_REFERENCE", ParseDiagnostic.Severity.SEVERE_WARNING, "extra_days");
		Assert.assertEquals("Ambiguous column reference 'extra_days' at (l:1 c:36). Possible sources: [accounts, events]", incrementAmbiguity.message());
		Assert.assertEquals("extra_days", incrementAmbiguity.tokenText());
		ParseDiagnostic valueAmbiguity = findDiagnosticByCodeAndFragmentAndSeverity(parser.getSnippet(),
				"AMBIGUOUS_COLUMN_REFERENCE", ParseDiagnostic.Severity.SEVERE_WARNING, "base_days");
		Assert.assertEquals("Ambiguous column reference 'base_days' at (l:1 c:64). Possible sources: [accounts, events]", valueAmbiguity.message());
		Assert.assertEquals("base_days", valueAmbiguity.tokenText());
		assertDiagnosticAtPosition(parser.getSnippet(), "UNRESOLVED_UNQUALIFIED_COLUMNS", ParseDiagnostic.Severity.ERROR,
				null, "extra_days", 1, 36);
		assertDiagnosticAtPosition(parser.getSnippet(), "UNRESOLVED_UNQUALIFIED_COLUMNS", ParseDiagnostic.Severity.ERROR,
				null, "base_days", 1, 64);
		ParseDiagnostic unresolved = findDiagnosticByCodeAndFragmentAndSeverity(parser.getSnippet(),
				"UNRESOLVED_UNQUALIFIED_COLUMNS", ParseDiagnostic.Severity.ERROR, "base_days");
		Assert.assertEquals("Unresolved unqualified column reference(s): [base_days [(l:1 c:64)], extra_days [(l:1 c:36)]]", unresolved.message());
		Assert.assertEquals("base_days, extra_days", unresolved.tokenText());
		Assert.assertEquals(Integer.valueOf(1), unresolved.line());
		Assert.assertEquals(Integer.valueOf(64), unresolved.charPositionInLine());
		Assert.assertEquals("{def_query0={query_dictionary={shifted=[[@24,95:101='shifted',<419>,1:95]]}, table_dictionary={accounts={id=[[@36,151:151='b',<419>,1:151]]}, events={account_id=[[@32,136:136='a',<419>,1:136]], adjust_days=[[@18,76:76='a',<419>,1:76]], created_at=[[@11,48:48='a',<419>,1:48]], offset_days=[[@5,20:20='a',<419>,1:20]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}], interface={shifted=[{name=offset_days, table_ref=a}, {name=extra_days, table_ref=null}, {name=created_at, table_ref=a}, {name=base_days, table_ref=null}, {name=adjust_days, table_ref=a}]}, table_alias={a=events, b=accounts}}}",
				parser.getSnippet().getSymbolTable().toString());
	}

	@Test
	public void dateaddResolvesUniqueDerivedTableExports() {
		final String query = "select dateadd(day, offset_days, created_at) as shifted from (select offset_days from events) a join (select created_at from accounts) b on 1 = 1";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=shifted, datetime_function={function_name=dateadd, level={canonical=DAY, text=day, form=KEYWORD}, increment={column={name=offset_days, table_ref=null}}, value={column={name=created_at, table_ref=null}}}}}, from={join={1={table={alias=a, query={select={1={column={name=offset_days, table_ref=null}}}, from={table={alias=null, table=events}}}}}, 2={join=join, on={condition={left={literal=1}, right={literal=1}, operator==}}}, 3={table={alias=b, query={select={1={column={name=created_at, table_ref=null}}}, from={table={alias=null, table=accounts}}}}}}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[shifted]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{accounts={created_at=[[@22,109:118='created_at',<419>,1:109]]}, events={offset_days=[[@14,69:79='offset_days',<419>,1:69]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={offset_days=[[@14,69:79='offset_days',<419>,1:69], [@5,20:30='offset_days',<419>,1:20]]}, query1={created_at=[[@22,109:118='created_at',<419>,1:109], [@7,33:42='created_at',<419>,1:33]]}, query2={shifted=[[@10,48:54='shifted',<419>,1:48]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query2={query_dictionary={shifted=[[@10,48:54='shifted',<419>,1:48]]}, def_query1={query_dictionary={created_at=[[@22,109:118='created_at',<419>,1:109], [@7,33:42='created_at',<419>,1:33]]}, table_dictionary={accounts={created_at=[[@22,109:118='created_at',<419>,1:109]]}}, interface={created_at=[{name=created_at, table_ref=accounts}]}}, def_query0={query_dictionary={offset_days=[[@14,69:79='offset_days',<419>,1:69], [@5,20:30='offset_days',<419>,1:20]]}, table_dictionary={events={offset_days=[[@14,69:79='offset_days',<419>,1:69]]}}, interface={offset_days=[{name=offset_days, table_ref=events}]}}, filters=[], interface={shifted=[{name=offset_days, table_ref=query0}, {name=created_at, table_ref=query1}]}, table_alias={a=query0, b=query1}}}", extractor.getSymbolTable().toString());
	}

	@Test
	public void dateaddResolvesUniqueCteExports() {
		final String query = "with e as (select offset_days from events), ac as (select created_at from accounts) select dateadd(day, offset_days, created_at) as shifted from e a join ac b on 1 = 1";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={with={1={cte={select={1={column={name=offset_days, table_ref=null}}}, from={table={alias=null, table=events}}}, alias=e}, 2={cte={select={1={column={name=created_at, table_ref=null}}}, from={table={alias=null, table=accounts}}}, alias=ac}}, query={select={1={alias=shifted, datetime_function={function_name=dateadd, level={canonical=DAY, text=day, form=KEYWORD}, increment={column={name=offset_days, table_ref=null}}, value={column={name=created_at, table_ref=null}}}}}, from={join={1={table={alias=a, table=e}}, 2={join=join, on={condition={left={literal=1}, right={literal=1}, operator==}}}, 3={table={alias=b, table=ac}}}}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[shifted]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{accounts={created_at=[[@14,58:67='created_at',<419>,1:58]]}, events={offset_days=[[@5,18:28='offset_days',<419>,1:18]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={offset_days=[[@5,18:28='offset_days',<419>,1:18], [@23,104:114='offset_days',<419>,1:104]]}, query1={created_at=[[@14,58:67='created_at',<419>,1:58], [@25,117:126='created_at',<419>,1:117]]}, query2={shifted=[[@28,132:138='shifted',<419>,1:132]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query2={context_list={e=query0, ac=query1, a=query0, b=query1}, query_dictionary={shifted=[[@28,132:138='shifted',<419>,1:132]]}, def_query1={context_list={e=query0}, query_dictionary={created_at=[[@14,58:67='created_at',<419>,1:58], [@25,117:126='created_at',<419>,1:117]]}, table_dictionary={accounts={created_at=[[@14,58:67='created_at',<419>,1:58]]}}, interface={created_at=[{name=created_at, table_ref=accounts}]}, table_alias={e=query0}}, def_query0={query_dictionary={offset_days=[[@5,18:28='offset_days',<419>,1:18], [@23,104:114='offset_days',<419>,1:104]]}, table_dictionary={events={offset_days=[[@5,18:28='offset_days',<419>,1:18]]}}, interface={offset_days=[{name=offset_days, table_ref=events}]}}, filters=[], interface={shifted=[{name=offset_days, table_ref=query0}, {name=created_at, table_ref=query1}]}, table_alias={a=query0, ac=query1, b=query1, e=query0}}}", extractor.getSymbolTable().toString());
	}

	@Test
	public void dateaddReportsOverlappingDerivedTableExports() {
		String query = "select dateadd(day, offset_days, created_at) as shifted from (select offset_days, created_at from events) a join (select offset_days, created_at from accounts) b on 1 = 1";
		SqlParserAccess parser = new SqlParserAccess(false, false, false);
		parser.executeTheParse(query, "SQL");
		Assert.assertEquals(0, parser.getParser().getNumberOfSyntaxErrors());
		Assert.assertEquals(parser.getAllDiagnostics().toString(), 3, parser.getAllDiagnostics().size());
		assertDiagnosticAtPosition(parser.getSnippet(), "AMBIGUOUS_COLUMN_REFERENCE", ParseDiagnostic.Severity.SEVERE_WARNING,
				"Ambiguous column reference 'offset_days' at (l:1 c:20). Possible sources: [query0, query1]", "offset_days", 1, 20);
		assertDiagnosticAtPosition(parser.getSnippet(), "AMBIGUOUS_COLUMN_REFERENCE", ParseDiagnostic.Severity.SEVERE_WARNING,
				"Ambiguous column reference 'created_at' at (l:1 c:33). Possible sources: [query0, query1]", "created_at", 1, 33);
		ParseDiagnostic increment = findDiagnosticByCodeAndFragmentAndSeverity(parser.getSnippet(),
				"AMBIGUOUS_COLUMN_REFERENCE", ParseDiagnostic.Severity.SEVERE_WARNING, "offset_days");
		Assert.assertEquals("Ambiguous column reference 'offset_days' at (l:1 c:20). Possible sources: [query0, query1]", increment.message());
		Assert.assertEquals("offset_days", increment.tokenText());
		ParseDiagnostic value = findDiagnosticByCodeAndFragmentAndSeverity(parser.getSnippet(),
				"AMBIGUOUS_COLUMN_REFERENCE", ParseDiagnostic.Severity.SEVERE_WARNING, "created_at");
		Assert.assertEquals("Ambiguous column reference 'created_at' at (l:1 c:33). Possible sources: [query0, query1]", value.message());
		Assert.assertEquals("created_at", value.tokenText());
		assertDiagnosticAtPosition(parser.getSnippet(), "UNRESOLVED_UNQUALIFIED_COLUMNS", ParseDiagnostic.Severity.ERROR,
				null, "offset_days", 1, 20);
		assertDiagnosticAtPosition(parser.getSnippet(), "UNRESOLVED_UNQUALIFIED_COLUMNS", ParseDiagnostic.Severity.ERROR,
				null, "created_at", 1, 33);
		ParseDiagnostic unresolved = findDiagnosticByCodeAndFragmentAndSeverity(parser.getSnippet(),
				"UNRESOLVED_UNQUALIFIED_COLUMNS", ParseDiagnostic.Severity.ERROR, "created_at");
		Assert.assertEquals("Unresolved unqualified column reference(s): [created_at [(l:1 c:33)], offset_days [(l:1 c:20)]]", unresolved.message());
		Assert.assertEquals("created_at, offset_days", unresolved.tokenText());
		Assert.assertEquals(Integer.valueOf(1), unresolved.line());
		Assert.assertEquals(Integer.valueOf(33), unresolved.charPositionInLine());
		Assert.assertEquals("{def_query2={query_dictionary={shifted=[[@10,48:54='shifted',<419>,1:48]]}, def_query1={query_dictionary={created_at=[[@26,134:143='created_at',<419>,1:134]], offset_days=[[@24,121:131='offset_days',<419>,1:121]]}, table_dictionary={accounts={created_at=[[@26,134:143='created_at',<419>,1:134]], offset_days=[[@24,121:131='offset_days',<419>,1:121]]}}, interface={created_at=[{name=created_at, table_ref=accounts}], offset_days=[{name=offset_days, table_ref=accounts}]}}, def_query0={query_dictionary={created_at=[[@16,82:91='created_at',<419>,1:82]], offset_days=[[@14,69:79='offset_days',<419>,1:69]]}, table_dictionary={events={created_at=[[@16,82:91='created_at',<419>,1:82]], offset_days=[[@14,69:79='offset_days',<419>,1:69]]}}, interface={created_at=[{name=created_at, table_ref=events}], offset_days=[{name=offset_days, table_ref=events}]}}, filters=[], interface={shifted=[{name=offset_days, table_ref=null}, {name=created_at, table_ref=null}]}, table_alias={a=query0, b=query1}}}",
				parser.getSnippet().getSymbolTable().toString());
	}

	@Test
	public void dateaddReportsOverlappingCteExports() {
		String query = "with e as (select offset_days, created_at from events), ac as (select offset_days, created_at from accounts) select dateadd(day, offset_days, created_at) as shifted from e a join ac b on 1 = 1";
		SqlParserAccess parser = new SqlParserAccess(false, false, false);
		parser.executeTheParse(query, "SQL");
		Assert.assertEquals(0, parser.getParser().getNumberOfSyntaxErrors());
		Assert.assertEquals(parser.getAllDiagnostics().toString(), 3, parser.getAllDiagnostics().size());
		assertDiagnosticAtPosition(parser.getSnippet(), "AMBIGUOUS_COLUMN_REFERENCE", ParseDiagnostic.Severity.SEVERE_WARNING,
				"Ambiguous column reference 'offset_days' at (l:1 c:129). Possible sources: [query0, query1]", "offset_days", 1, 129);
		assertDiagnosticAtPosition(parser.getSnippet(), "AMBIGUOUS_COLUMN_REFERENCE", ParseDiagnostic.Severity.SEVERE_WARNING,
				"Ambiguous column reference 'created_at' at (l:1 c:142). Possible sources: [query0, query1]", "created_at", 1, 142);
		ParseDiagnostic increment = findDiagnosticByCodeAndFragmentAndSeverity(parser.getSnippet(),
				"AMBIGUOUS_COLUMN_REFERENCE", ParseDiagnostic.Severity.SEVERE_WARNING, "offset_days");
		Assert.assertEquals("Ambiguous column reference 'offset_days' at (l:1 c:129). Possible sources: [query0, query1]", increment.message());
		Assert.assertEquals("offset_days", increment.tokenText());
		ParseDiagnostic value = findDiagnosticByCodeAndFragmentAndSeverity(parser.getSnippet(),
				"AMBIGUOUS_COLUMN_REFERENCE", ParseDiagnostic.Severity.SEVERE_WARNING, "created_at");
		Assert.assertEquals("Ambiguous column reference 'created_at' at (l:1 c:142). Possible sources: [query0, query1]", value.message());
		Assert.assertEquals("created_at", value.tokenText());
		assertDiagnosticAtPosition(parser.getSnippet(), "UNRESOLVED_UNQUALIFIED_COLUMNS", ParseDiagnostic.Severity.ERROR,
				null, "offset_days", 1, 129);
		assertDiagnosticAtPosition(parser.getSnippet(), "UNRESOLVED_UNQUALIFIED_COLUMNS", ParseDiagnostic.Severity.ERROR,
				null, "created_at", 1, 142);
		ParseDiagnostic unresolved = findDiagnosticByCodeAndFragmentAndSeverity(parser.getSnippet(),
				"UNRESOLVED_UNQUALIFIED_COLUMNS", ParseDiagnostic.Severity.ERROR, "created_at");
		Assert.assertEquals("Unresolved unqualified column reference(s): [created_at [(l:1 c:142)], offset_days [(l:1 c:129)]]", unresolved.message());
		Assert.assertEquals("created_at, offset_days", unresolved.tokenText());
		Assert.assertEquals(Integer.valueOf(1), unresolved.line());
		Assert.assertEquals(Integer.valueOf(142), unresolved.charPositionInLine());
		Assert.assertEquals("{def_query2={context_list={e=query0, ac=query1, a=query0, b=query1}, query_dictionary={shifted=[[@32,157:163='shifted',<419>,1:157]]}, def_query1={context_list={e=query0}, query_dictionary={created_at=[[@18,83:92='created_at',<419>,1:83]], offset_days=[[@16,70:80='offset_days',<419>,1:70]]}, table_dictionary={accounts={created_at=[[@18,83:92='created_at',<419>,1:83]], offset_days=[[@16,70:80='offset_days',<419>,1:70]]}}, interface={created_at=[{name=created_at, table_ref=accounts}], offset_days=[{name=offset_days, table_ref=accounts}]}, table_alias={e=query0}}, def_query0={query_dictionary={created_at=[[@7,31:40='created_at',<419>,1:31]], offset_days=[[@5,18:28='offset_days',<419>,1:18]]}, table_dictionary={events={created_at=[[@7,31:40='created_at',<419>,1:31]], offset_days=[[@5,18:28='offset_days',<419>,1:18]]}}, interface={created_at=[{name=created_at, table_ref=events}], offset_days=[{name=offset_days, table_ref=events}]}}, filters=[], interface={shifted=[{name=offset_days, table_ref=null}, {name=created_at, table_ref=null}]}, table_alias={a=query0, ac=query1, b=query1, e=query0}}}",
				parser.getSnippet().getSymbolTable().toString());
	}

	@Test
	public void dateaddRejectsHiddenUnderlyingDerivedColumn() {
		String query = "select dateadd(day, 1, a.created_at) as shifted from (select created_at as visible_ts from events) a";
		SqlParserAccess parser = new SqlParserAccess(false, false, false);
		parser.executeTheParse(query, "SQL");
		Assert.assertEquals(0, parser.getParser().getNumberOfSyntaxErrors());
		Assert.assertEquals(parser.getAllDiagnostics().toString(), 1, parser.getAllDiagnostics().size());
		assertFatalDiagnosticAtPositionWithFullMessage(parser.getSnippet(), "QUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIAS",
				"Qualified column 'created_at' at (l:1 c:23) was not found in output interface of query alias 'a'.",
				"a.created_at", 1, 23);
		Assert.assertEquals("a.created_at", findFatalDiagnosticByCodeAndFragment(parser.getSnippet(),
				"QUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIAS", "a.created_at").tokenText());
		Assert.assertEquals("{def_query1={query_dictionary={shifted=[[@12,40:46='shifted',<419>,1:40]]}, def_query0={query_dictionary={visible_ts=[[@18,75:84='visible_ts',<419>,1:75]]}, table_dictionary={events={created_at=[[@16,61:70='created_at',<419>,1:61]]}}, interface={visible_ts=[{name=created_at, table_ref=events}]}}, interface={shifted=[{name=created_at, table_ref=a}]}, table_alias={a=query0}}}",
				parser.getSnippet().getSymbolTable().toString());
	}

	@Test
	public void dateaddRejectsMissingDerivedTableOutput() {
		String query = "select dateadd(day, 1, a.missing_ts) as shifted from (select created_at from events) a";
		SqlParserAccess parser = new SqlParserAccess(false, false, false);
		parser.executeTheParse(query, "SQL");
		Assert.assertEquals(0, parser.getParser().getNumberOfSyntaxErrors());
		Assert.assertEquals(parser.getAllDiagnostics().toString(), 1, parser.getAllDiagnostics().size());
		assertFatalDiagnosticAtPositionWithFullMessage(parser.getSnippet(), "QUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIAS",
				"Qualified column 'missing_ts' at (l:1 c:23) was not found in output interface of query alias 'a'.",
				"a.missing_ts", 1, 23);
		Assert.assertEquals("a.missing_ts", findFatalDiagnosticByCodeAndFragment(parser.getSnippet(),
				"QUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIAS", "a.missing_ts").tokenText());
		Assert.assertEquals("{def_query1={query_dictionary={shifted=[[@12,40:46='shifted',<419>,1:40]]}, def_query0={query_dictionary={created_at=[[@16,61:70='created_at',<419>,1:61]]}, table_dictionary={events={created_at=[[@16,61:70='created_at',<419>,1:61]]}}, interface={created_at=[{name=created_at, table_ref=events}]}}, interface={shifted=[{name=missing_ts, table_ref=a}]}, table_alias={a=query0}}}",
				parser.getSnippet().getSymbolTable().toString());
	}

	@Test
	public void dateaddRejectsMissingCteOutput() {
		String query = "with e as (select created_at from events) select dateadd(day, 1, a.missing_ts) as shifted from e a";
		SqlParserAccess parser = new SqlParserAccess(false, false, false);
		parser.executeTheParse(query, "SQL");
		Assert.assertEquals(0, parser.getParser().getNumberOfSyntaxErrors());
		Assert.assertEquals(parser.getAllDiagnostics().toString(), 1, parser.getAllDiagnostics().size());
		assertFatalDiagnosticAtPositionWithFullMessage(parser.getSnippet(), "QUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIAS",
				"Qualified column 'missing_ts' at (l:1 c:65) was not found in output interface of query alias 'a'.",
				"a.missing_ts", 1, 65);
		Assert.assertEquals("a.missing_ts", findFatalDiagnosticByCodeAndFragment(parser.getSnippet(),
				"QUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIAS", "a.missing_ts").tokenText());
		Assert.assertEquals("{def_query1={context_list={e=query0, a=query0}, query_dictionary={shifted=[[@21,82:88='shifted',<419>,1:82]]}, def_query0={query_dictionary={created_at=[[@5,18:27='created_at',<419>,1:18]]}, table_dictionary={events={created_at=[[@5,18:27='created_at',<419>,1:18]]}}, interface={created_at=[{name=created_at, table_ref=events}]}}, interface={shifted=[{name=missing_ts, table_ref=a}]}, table_alias={a=query0, e=query0}}}",
				parser.getSnippet().getSymbolTable().toString());
	}

	@Test
	public void dateaddRejectsUnknownTableAlias() {
		String query = "select dateadd(day, 1, z.created_at) as shifted from events a";
		SqlParserAccess parser = new SqlParserAccess(false, false, false);
		parser.executeTheParse(query, "SQL");
		Assert.assertEquals(0, parser.getParser().getNumberOfSyntaxErrors());
		Assert.assertEquals(parser.getAllDiagnostics().toString(), 1, parser.getAllDiagnostics().size());
		assertFatalDiagnosticAtPositionWithFullMessage(parser.getSnippet(), "QUALIFIED_COLUMN_NOT_FOUND_IN_TABLE",
				"Source Table not found for Column 'created_at' at (l:1 c:40). No alias or table called 'z'.",
				"created_at", 1, 40);
		Assert.assertEquals("created_at", findFatalDiagnosticByCodeAndFragment(parser.getSnippet(),
				"QUALIFIED_COLUMN_NOT_FOUND_IN_TABLE", "created_at").tokenText());
		Assert.assertEquals("{def_query0={query_dictionary={shifted=[[@12,40:46='shifted',<419>,1:40]]}, table_dictionary={events={}}, interface={shifted=[{name=created_at, table_ref=z}]}, table_alias={a=events}}}",
				parser.getSnippet().getSymbolTable().toString());
	}

	@Test
	public void dateaddMissingQualifiedFormulaSubstitutionsRemainFatalWithoutJoinExtension() {
		String query = "select dateadd(day, a.offset_days + (x.<increment> + 1), a.created_at + (y.<days> + 2)) as shifted from events a";
		SqlParserAccess parser = new SqlParserAccess(false, false, false);
		parser.executeTheParse(query, "SQL");
		Assert.assertEquals(0, parser.getParser().getNumberOfSyntaxErrors());
		Assert.assertEquals(parser.getAllDiagnostics().toString(), 2, parser.getAllDiagnostics().size());
		Assert.assertEquals(parser.getAllDiagnostics().toString(), 2, parser.getSnippet().getFatalErrorCount());
		Assert.assertEquals(parser.getAllDiagnostics().toString(), 2,
				parser.getSnippet().getDiagnosticCountByCodeAndSeverity("QUALIFIED_COLUMN_NOT_FOUND_IN_TABLE", ParseDiagnostic.Severity.FATAL));
		assertFatalDiagnosticAtPositionWithFullMessage(parser.getSnippet(), "QUALIFIED_COLUMN_NOT_FOUND_IN_TABLE",
				"Source Table not found for Column '<increment>' at (l:1 c:37). No alias or table called 'x'.",
				"<increment>", 1, 37);
		assertFatalDiagnosticAtPositionWithFullMessage(parser.getSnippet(), "QUALIFIED_COLUMN_NOT_FOUND_IN_TABLE",
				"Source Table not found for Column '<days>' at (l:1 c:73). No alias or table called 'y'.",
				"<days>", 1, 73);
		Assert.assertEquals("{<days>=column, <increment>=column}", parser.getSnippet().getSubstitutionsMap().toString());
		Assert.assertEquals("{def_query0={query_dictionary={shifted=[[@30,91:97='shifted',<419>,1:91]]}, table_dictionary={events={created_at=[[@17,57:57='a',<419>,1:57]], offset_days=[[@5,20:20='a',<419>,1:20]]}}, interface={shifted=[{name=offset_days, table_ref=a}, {substitution={name=<increment>, type=column}, table_ref=x}, {name=created_at, table_ref=a}, {substitution={name=<days>, type=column}, table_ref=y}]}, table_alias={a=events}}}",
				parser.getSnippet().getSymbolTable().toString());
	}

	@Test
	public void dateaddMissingQualifiedFormulaSubstitutionsDeferToJoinExtension() {
		String query = "select dateadd(day, a.offset_days + (x.<increment> + 1), a.created_at + (y.<days> + 2)) as shifted from events a <extension>";
		SqlParserAccess parser = new SqlParserAccess(false, false, false);
		parser.executeTheParse(query, "SQL");
		Assert.assertEquals(0, parser.getParser().getNumberOfSyntaxErrors());
		Assert.assertEquals(parser.getAllDiagnostics().toString(), 3, parser.getAllDiagnostics().size());
		Assert.assertEquals(parser.getAllDiagnostics().toString(), 0, parser.getSnippet().getFatalErrorCount());
		Assert.assertEquals(parser.getAllDiagnostics().toString(), 2,
				parser.getSnippet().getDiagnosticCountByCodeAndSeverity("QUALIFIED_COLUMN_NOT_FOUND_IN_TABLE", ParseDiagnostic.Severity.SEVERE_WARNING));
		Assert.assertEquals(parser.getAllDiagnostics().toString(), 1,
				parser.getSnippet().getDiagnosticCountByCodeAndSeverity("JOIN_EXTENSION_REQUIRED", ParseDiagnostic.Severity.SEVERE_WARNING));
		String incrementMessage = "Source Table not found for Column '<increment>' at (l:1 c:37). No alias or table called 'x'.";
		String valueMessage = "Source Table not found for Column '<days>' at (l:1 c:73). No alias or table called 'y'.";
		String extensionMessage = "Join extension variable '<extension>' at (l:1 c:113) is not optional: 2 table/column reference(s)"
				+ " not defined in the query (x.<increment>, y.<days>) must be satisfied by the substituted join extension snippet.";
		assertDiagnosticAtPosition(parser.getSnippet(), "QUALIFIED_COLUMN_NOT_FOUND_IN_TABLE", ParseDiagnostic.Severity.SEVERE_WARNING,
				incrementMessage, "<increment>", 1, 37);
		assertDiagnosticAtPosition(parser.getSnippet(), "QUALIFIED_COLUMN_NOT_FOUND_IN_TABLE", ParseDiagnostic.Severity.SEVERE_WARNING,
				valueMessage, "<days>", 1, 73);
		assertDiagnosticAtPosition(parser.getSnippet(), "JOIN_EXTENSION_REQUIRED", ParseDiagnostic.Severity.SEVERE_WARNING,
				extensionMessage, "<extension>", 1, 113);
		Assert.assertEquals(incrementMessage, findDiagnosticByCodeAndFragmentAndSeverity(parser.getSnippet(),
				"QUALIFIED_COLUMN_NOT_FOUND_IN_TABLE", ParseDiagnostic.Severity.SEVERE_WARNING, "<increment>").message());
		Assert.assertEquals(valueMessage, findDiagnosticByCodeAndFragmentAndSeverity(parser.getSnippet(),
				"QUALIFIED_COLUMN_NOT_FOUND_IN_TABLE", ParseDiagnostic.Severity.SEVERE_WARNING, "<days>").message());
		Assert.assertEquals(extensionMessage, findDiagnosticByCodeAndFragmentAndSeverity(parser.getSnippet(),
				"JOIN_EXTENSION_REQUIRED", ParseDiagnostic.Severity.SEVERE_WARNING, "<extension>").message());
		Assert.assertEquals("{SQL={select={1={alias=shifted, datetime_function={function_name=dateadd, level={canonical=DAY, text=day, form=KEYWORD}, increment={calc={left={column={name=offset_days, table_ref=a}}, right={parentheses={calc={left={column={substitution={name=<increment>, type=column}, table_ref=x}}, right={literal=1}, operator=+}}}, operator=+}}, value={calc={left={column={name=created_at, table_ref=a}}, right={parentheses={calc={left={column={substitution={name=<days>, type=column}, table_ref=y}}, right={literal=2}, operator=+}}}, operator=+}}}}}, from={extension={substitution={name=<extension>, type=join_extension}}, table={alias=a, table=events}}}}", parser.getSnippet().getSqlAbstractTree().toString());
		Assert.assertEquals("[shifted]", parser.getSnippet().getQueryInterface().toString());
		Assert.assertEquals("{<days>=column, <extension>=join_extension, <increment>=column}", parser.getSnippet().getSubstitutionsMap().toString());
		Assert.assertEquals("{events={created_at=[[@17,57:57='a',<419>,1:57]], offset_days=[[@5,20:20='a',<419>,1:20]]}}", parser.getSnippet().getTableDictionary().toString());
		Assert.assertEquals("{query0={shifted=[[@30,91:97='shifted',<419>,1:91]]}}", parser.getSnippet().getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("{def_query0={query_dictionary={shifted=[[@30,91:97='shifted',<419>,1:91]]}, table_dictionary={events={created_at=[[@17,57:57='a',<419>,1:57]], offset_days=[[@5,20:20='a',<419>,1:20]]}}, interface={shifted=[{name=offset_days, table_ref=a}, {substitution={name=<increment>, type=column}, table_ref=x}, {name=created_at, table_ref=a}, {substitution={name=<days>, type=column}, table_ref=y}]}, table_alias={a=events}}}", parser.getSnippet().getSymbolTable().toString());
	}

	@Test
	public void dateaddWithLiteralArgumentsHasNoSourceDependencies() {
		final String query = "select dateadd(day, 2, DATE '2026-01-01') as shifted";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=shifted, datetime_function={function_name=dateadd, level={canonical=DAY, text=day, form=KEYWORD}, increment={literal=2}, value={source_type=DATE, literal=2026-01-01}}}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[shifted]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={shifted=[[@11,45:51='shifted',<419>,1:45]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={shifted=[[@11,45:51='shifted',<419>,1:45]]}, interface={shifted=[]}}}", extractor.getSymbolTable().toString());
	}

	@Test
	public void dateaddAcceptsEveryCanonicalLevel() {
		final String query = "select dateadd(year, 1, a.ts) as y, dateadd(quarter, 1, a.ts) as q, dateadd(month, 1, a.ts) as mo, dateadd(week, 1, a.ts) as w, dateadd(day, 1, a.ts) as d, dateadd(hour, 1, a.ts) as h, dateadd(minute, 1, a.ts) as mi, dateadd(second, 1, a.ts) as s, dateadd(millisecond, 1, a.ts) as ms, dateadd(microsecond, 1, a.ts) as us, dateadd(nanosecond, 1, a.ts) as ns from events a";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={11={alias=ns, datetime_function={function_name=dateadd, level={canonical=NANOSECOND, text=nanosecond, form=IDENTIFIER}, increment={literal=1}, value={column={name=ts, table_ref=a}}}}, 1={alias=y, datetime_function={function_name=dateadd, level={canonical=YEAR, text=year, form=KEYWORD}, increment={literal=1}, value={column={name=ts, table_ref=a}}}}, 2={alias=q, datetime_function={function_name=dateadd, level={canonical=QUARTER, text=quarter, form=KEYWORD}, increment={literal=1}, value={column={name=ts, table_ref=a}}}}, 3={alias=mo, datetime_function={function_name=dateadd, level={canonical=MONTH, text=month, form=KEYWORD}, increment={literal=1}, value={column={name=ts, table_ref=a}}}}, 4={alias=w, datetime_function={function_name=dateadd, level={canonical=WEEK, text=week, form=KEYWORD}, increment={literal=1}, value={column={name=ts, table_ref=a}}}}, 5={alias=d, datetime_function={function_name=dateadd, level={canonical=DAY, text=day, form=KEYWORD}, increment={literal=1}, value={column={name=ts, table_ref=a}}}}, 6={alias=h, datetime_function={function_name=dateadd, level={canonical=HOUR, text=hour, form=KEYWORD}, increment={literal=1}, value={column={name=ts, table_ref=a}}}}, 7={alias=mi, datetime_function={function_name=dateadd, level={canonical=MINUTE, text=minute, form=KEYWORD}, increment={literal=1}, value={column={name=ts, table_ref=a}}}}, 8={alias=s, datetime_function={function_name=dateadd, level={canonical=SECOND, text=second, form=KEYWORD}, increment={literal=1}, value={column={name=ts, table_ref=a}}}}, 9={alias=ms, datetime_function={function_name=dateadd, level={canonical=MILLISECOND, text=millisecond, form=IDENTIFIER}, increment={literal=1}, value={column={name=ts, table_ref=a}}}}, 10={alias=us, datetime_function={function_name=dateadd, level={canonical=MICROSECOND, text=microsecond, form=IDENTIFIER}, increment={literal=1}, value={column={name=ts, table_ref=a}}}}}, from={table={alias=a, table=events}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[q, mo, s, d, ns, ms, w, h, y, mi, us]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{events={ts=[[@7,24:24='a',<419>,1:24], [@20,56:56='a',<419>,1:56], [@33,86:86='a',<419>,1:86], [@46,116:116='a',<419>,1:116], [@59,144:144='a',<419>,1:144], [@72,173:173='a',<419>,1:173], [@85,204:204='a',<419>,1:204], [@98,236:236='a',<419>,1:236], [@111,272:272='a',<419>,1:272], [@124,309:309='a',<419>,1:309], [@137,345:345='a',<419>,1:345]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={q=[[@25,65:65='q',<419>,1:65]], mo=[[@38,95:96='mo',<419>,1:95]], s=[[@103,245:245='s',<419>,1:245]], d=[[@64,153:153='d',<419>,1:153]], ns=[[@142,354:355='ns',<419>,1:354]], ms=[[@116,281:282='ms',<419>,1:281]], w=[[@51,125:125='w',<419>,1:125]], h=[[@77,182:182='h',<419>,1:182]], y=[[@12,33:33='y',<419>,1:33]], mi=[[@90,213:214='mi',<419>,1:213]], us=[[@129,318:319='us',<419>,1:318]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={q=[[@25,65:65='q',<419>,1:65]], mo=[[@38,95:96='mo',<419>,1:95]], s=[[@103,245:245='s',<419>,1:245]], d=[[@64,153:153='d',<419>,1:153]], ns=[[@142,354:355='ns',<419>,1:354]], ms=[[@116,281:282='ms',<419>,1:281]], w=[[@51,125:125='w',<419>,1:125]], h=[[@77,182:182='h',<419>,1:182]], y=[[@12,33:33='y',<419>,1:33]], mi=[[@90,213:214='mi',<419>,1:213]], us=[[@129,318:319='us',<419>,1:318]]}, table_dictionary={events={ts=[[@7,24:24='a',<419>,1:24], [@20,56:56='a',<419>,1:56], [@33,86:86='a',<419>,1:86], [@46,116:116='a',<419>,1:116], [@59,144:144='a',<419>,1:144], [@72,173:173='a',<419>,1:173], [@85,204:204='a',<419>,1:204], [@98,236:236='a',<419>,1:236], [@111,272:272='a',<419>,1:272], [@124,309:309='a',<419>,1:309], [@137,345:345='a',<419>,1:345]]}}, interface={q=[{name=ts, table_ref=a}], mo=[{name=ts, table_ref=a}], s=[{name=ts, table_ref=a}], d=[{name=ts, table_ref=a}], ns=[{name=ts, table_ref=a}], ms=[{name=ts, table_ref=a}], w=[{name=ts, table_ref=a}], h=[{name=ts, table_ref=a}], y=[{name=ts, table_ref=a}], mi=[{name=ts, table_ref=a}], us=[{name=ts, table_ref=a}]}, table_alias={a=events}}}", extractor.getSymbolTable().toString());
	}

	@Test
	public void dateaddRegistryMatchesSnowflakeDocumentedPartsAndAliases() {
		// https://docs.snowflake.com/en/sql-reference/functions-date-time#supported-date-and-time-parts
		Map<String, List<String>> expected = Map.ofEntries(
				Map.entry("YEAR", List.of("year", "y", "yy", "yyy", "yyyy", "yr", "years", "yrs")),
				Map.entry("QUARTER", List.of("quarter", "q", "qtr", "qtrs", "quarters")),
				Map.entry("MONTH", List.of("month", "mm", "mon", "mons", "months")),
				Map.entry("WEEK", List.of("week", "w", "wk", "weekofyear", "woy", "wy")),
				Map.entry("DAY", List.of("day", "d", "dd", "days", "dayofmonth")),
				Map.entry("HOUR", List.of("hour", "h", "hh", "hr", "hours", "hrs")),
				Map.entry("MINUTE", List.of("minute", "m", "mi", "min", "minutes", "mins")),
				Map.entry("SECOND", List.of("second", "s", "sec", "seconds", "secs")),
				Map.entry("MILLISECOND", List.of("millisecond", "ms", "msec", "milliseconds")),
				Map.entry("MICROSECOND", List.of("microsecond", "us", "usec", "microseconds")),
				Map.entry("NANOSECOND", List.of("nanosecond", "ns", "nsec", "nanosec", "nsecond", "nanoseconds", "nanosecs", "nseconds")));
		Assert.assertEquals(expected.keySet(), Set.copyOf(DatetimePartRegistry.canonicalValues()));
		for (Map.Entry<String, List<String>> part : expected.entrySet()) {
			Assert.assertEquals(part.getKey(), Set.copyOf(part.getValue()),
					Set.copyOf(DatetimePartRegistry.aliasesForCanonical(part.getKey()).orElseThrow()));
			for (String alias : part.getValue()) {
				Assert.assertEquals(alias, part.getKey(), DatetimePartRegistry.findCanonical(alias).orElseThrow());
			}
		}
	}

	@Test
	public void dateaddAcceptsEveryYearSpelling() {
		final String query = "select dateadd(year, 1, a.ts) as part1, dateadd(y, 1, a.ts) as part2, dateadd(yy, 1, a.ts) as part3, dateadd(yyy, 1, a.ts) as part4, dateadd(yyyy, 1, a.ts) as part5, dateadd(yr, 1, a.ts) as part6, dateadd(years, 1, a.ts) as part7, dateadd(yrs, 1, a.ts) as part8 from events a";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=part1, datetime_function={function_name=dateadd, level={canonical=YEAR, text=year, form=KEYWORD}, increment={literal=1}, value={column={name=ts, table_ref=a}}}}, 2={alias=part2, datetime_function={function_name=dateadd, level={canonical=YEAR, text=y, form=IDENTIFIER}, increment={literal=1}, value={column={name=ts, table_ref=a}}}}, 3={alias=part3, datetime_function={function_name=dateadd, level={canonical=YEAR, text=yy, form=IDENTIFIER}, increment={literal=1}, value={column={name=ts, table_ref=a}}}}, 4={alias=part4, datetime_function={function_name=dateadd, level={canonical=YEAR, text=yyy, form=IDENTIFIER}, increment={literal=1}, value={column={name=ts, table_ref=a}}}}, 5={alias=part5, datetime_function={function_name=dateadd, level={canonical=YEAR, text=yyyy, form=IDENTIFIER}, increment={literal=1}, value={column={name=ts, table_ref=a}}}}, 6={alias=part6, datetime_function={function_name=dateadd, level={canonical=YEAR, text=yr, form=IDENTIFIER}, increment={literal=1}, value={column={name=ts, table_ref=a}}}}, 7={alias=part7, datetime_function={function_name=dateadd, level={canonical=YEAR, text=years, form=IDENTIFIER}, increment={literal=1}, value={column={name=ts, table_ref=a}}}}, 8={alias=part8, datetime_function={function_name=dateadd, level={canonical=YEAR, text=yrs, form=IDENTIFIER}, increment={literal=1}, value={column={name=ts, table_ref=a}}}}}, from={table={alias=a, table=events}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[part1, part2, part3, part4, part5, part6, part7, part8]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{events={ts=[[@7,24:24='a',<419>,1:24], [@20,54:54='a',<419>,1:54], [@33,85:85='a',<419>,1:85], [@46,117:117='a',<419>,1:117], [@59,150:150='a',<419>,1:150], [@72,181:181='a',<419>,1:181], [@85,215:215='a',<419>,1:215], [@98,247:247='a',<419>,1:247]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={part1=[[@12,33:37='part1',<419>,1:33]], part2=[[@25,63:67='part2',<419>,1:63]], part3=[[@38,94:98='part3',<419>,1:94]], part4=[[@51,126:130='part4',<419>,1:126]], part5=[[@64,159:163='part5',<419>,1:159]], part6=[[@77,190:194='part6',<419>,1:190]], part7=[[@90,224:228='part7',<419>,1:224]], part8=[[@103,256:260='part8',<419>,1:256]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={part1=[[@12,33:37='part1',<419>,1:33]], part2=[[@25,63:67='part2',<419>,1:63]], part3=[[@38,94:98='part3',<419>,1:94]], part4=[[@51,126:130='part4',<419>,1:126]], part5=[[@64,159:163='part5',<419>,1:159]], part6=[[@77,190:194='part6',<419>,1:190]], part7=[[@90,224:228='part7',<419>,1:224]], part8=[[@103,256:260='part8',<419>,1:256]]}, table_dictionary={events={ts=[[@7,24:24='a',<419>,1:24], [@20,54:54='a',<419>,1:54], [@33,85:85='a',<419>,1:85], [@46,117:117='a',<419>,1:117], [@59,150:150='a',<419>,1:150], [@72,181:181='a',<419>,1:181], [@85,215:215='a',<419>,1:215], [@98,247:247='a',<419>,1:247]]}}, interface={part1=[{name=ts, table_ref=a}], part2=[{name=ts, table_ref=a}], part3=[{name=ts, table_ref=a}], part4=[{name=ts, table_ref=a}], part5=[{name=ts, table_ref=a}], part6=[{name=ts, table_ref=a}], part7=[{name=ts, table_ref=a}], part8=[{name=ts, table_ref=a}]}, table_alias={a=events}}}", extractor.getSymbolTable().toString());
	}

	@Test
	public void dateaddAcceptsEveryQuarterSpelling() {
		final String query = "select dateadd(quarter, 1, a.ts) as part1, dateadd(q, 1, a.ts) as part2, dateadd(qtr, 1, a.ts) as part3, dateadd(qtrs, 1, a.ts) as part4, dateadd(quarters, 1, a.ts) as part5 from events a";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=part1, datetime_function={function_name=dateadd, level={canonical=QUARTER, text=quarter, form=KEYWORD}, increment={literal=1}, value={column={name=ts, table_ref=a}}}}, 2={alias=part2, datetime_function={function_name=dateadd, level={canonical=QUARTER, text=q, form=IDENTIFIER}, increment={literal=1}, value={column={name=ts, table_ref=a}}}}, 3={alias=part3, datetime_function={function_name=dateadd, level={canonical=QUARTER, text=qtr, form=IDENTIFIER}, increment={literal=1}, value={column={name=ts, table_ref=a}}}}, 4={alias=part4, datetime_function={function_name=dateadd, level={canonical=QUARTER, text=qtrs, form=IDENTIFIER}, increment={literal=1}, value={column={name=ts, table_ref=a}}}}, 5={alias=part5, datetime_function={function_name=dateadd, level={canonical=QUARTER, text=quarters, form=IDENTIFIER}, increment={literal=1}, value={column={name=ts, table_ref=a}}}}}, from={table={alias=a, table=events}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[part1, part2, part3, part4, part5]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{events={ts=[[@7,27:27='a',<419>,1:27], [@20,57:57='a',<419>,1:57], [@33,89:89='a',<419>,1:89], [@46,122:122='a',<419>,1:122], [@59,159:159='a',<419>,1:159]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={part3=[[@38,98:102='part3',<419>,1:98]], part4=[[@51,131:135='part4',<419>,1:131]], part5=[[@64,168:172='part5',<419>,1:168]], part1=[[@12,36:40='part1',<419>,1:36]], part2=[[@25,66:70='part2',<419>,1:66]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={part1=[[@12,36:40='part1',<419>,1:36]], part2=[[@25,66:70='part2',<419>,1:66]], part3=[[@38,98:102='part3',<419>,1:98]], part4=[[@51,131:135='part4',<419>,1:131]], part5=[[@64,168:172='part5',<419>,1:168]]}, table_dictionary={events={ts=[[@7,27:27='a',<419>,1:27], [@20,57:57='a',<419>,1:57], [@33,89:89='a',<419>,1:89], [@46,122:122='a',<419>,1:122], [@59,159:159='a',<419>,1:159]]}}, interface={part1=[{name=ts, table_ref=a}], part2=[{name=ts, table_ref=a}], part3=[{name=ts, table_ref=a}], part4=[{name=ts, table_ref=a}], part5=[{name=ts, table_ref=a}]}, table_alias={a=events}}}", extractor.getSymbolTable().toString());
	}

	@Test
	public void dateaddAcceptsEveryMonthSpelling() {
		final String query = "select dateadd(month, 1, a.ts) as part1, dateadd(mm, 1, a.ts) as part2, dateadd(mon, 1, a.ts) as part3, dateadd(mons, 1, a.ts) as part4, dateadd(months, 1, a.ts) as part5 from events a";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=part1, datetime_function={function_name=dateadd, level={canonical=MONTH, text=month, form=KEYWORD}, increment={literal=1}, value={column={name=ts, table_ref=a}}}}, 2={alias=part2, datetime_function={function_name=dateadd, level={canonical=MONTH, text=mm, form=IDENTIFIER}, increment={literal=1}, value={column={name=ts, table_ref=a}}}}, 3={alias=part3, datetime_function={function_name=dateadd, level={canonical=MONTH, text=mon, form=IDENTIFIER}, increment={literal=1}, value={column={name=ts, table_ref=a}}}}, 4={alias=part4, datetime_function={function_name=dateadd, level={canonical=MONTH, text=mons, form=IDENTIFIER}, increment={literal=1}, value={column={name=ts, table_ref=a}}}}, 5={alias=part5, datetime_function={function_name=dateadd, level={canonical=MONTH, text=months, form=IDENTIFIER}, increment={literal=1}, value={column={name=ts, table_ref=a}}}}}, from={table={alias=a, table=events}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[part1, part2, part3, part4, part5]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{events={ts=[[@7,25:25='a',<419>,1:25], [@20,56:56='a',<419>,1:56], [@33,88:88='a',<419>,1:88], [@46,121:121='a',<419>,1:121], [@59,156:156='a',<419>,1:156]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={part3=[[@38,97:101='part3',<419>,1:97]], part4=[[@51,130:134='part4',<419>,1:130]], part5=[[@64,165:169='part5',<419>,1:165]], part1=[[@12,34:38='part1',<419>,1:34]], part2=[[@25,65:69='part2',<419>,1:65]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={part1=[[@12,34:38='part1',<419>,1:34]], part2=[[@25,65:69='part2',<419>,1:65]], part3=[[@38,97:101='part3',<419>,1:97]], part4=[[@51,130:134='part4',<419>,1:130]], part5=[[@64,165:169='part5',<419>,1:165]]}, table_dictionary={events={ts=[[@7,25:25='a',<419>,1:25], [@20,56:56='a',<419>,1:56], [@33,88:88='a',<419>,1:88], [@46,121:121='a',<419>,1:121], [@59,156:156='a',<419>,1:156]]}}, interface={part1=[{name=ts, table_ref=a}], part2=[{name=ts, table_ref=a}], part3=[{name=ts, table_ref=a}], part4=[{name=ts, table_ref=a}], part5=[{name=ts, table_ref=a}]}, table_alias={a=events}}}", extractor.getSymbolTable().toString());
	}

	@Test
	public void dateaddAcceptsEveryWeekSpelling() {
		final String query = "select dateadd(week, 1, a.ts) as part1, dateadd(w, 1, a.ts) as part2, dateadd(wk, 1, a.ts) as part3, dateadd(weekofyear, 1, a.ts) as part4, dateadd(woy, 1, a.ts) as part5, dateadd(wy, 1, a.ts) as part6 from events a";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=part1, datetime_function={function_name=dateadd, level={canonical=WEEK, text=week, form=KEYWORD}, increment={literal=1}, value={column={name=ts, table_ref=a}}}}, 2={alias=part2, datetime_function={function_name=dateadd, level={canonical=WEEK, text=w, form=IDENTIFIER}, increment={literal=1}, value={column={name=ts, table_ref=a}}}}, 3={alias=part3, datetime_function={function_name=dateadd, level={canonical=WEEK, text=wk, form=IDENTIFIER}, increment={literal=1}, value={column={name=ts, table_ref=a}}}}, 4={alias=part4, datetime_function={function_name=dateadd, level={canonical=WEEK, text=weekofyear, form=KEYWORD}, increment={literal=1}, value={column={name=ts, table_ref=a}}}}, 5={alias=part5, datetime_function={function_name=dateadd, level={canonical=WEEK, text=woy, form=IDENTIFIER}, increment={literal=1}, value={column={name=ts, table_ref=a}}}}, 6={alias=part6, datetime_function={function_name=dateadd, level={canonical=WEEK, text=wy, form=IDENTIFIER}, increment={literal=1}, value={column={name=ts, table_ref=a}}}}}, from={table={alias=a, table=events}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[part1, part2, part3, part4, part5, part6]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{events={ts=[[@7,24:24='a',<419>,1:24], [@20,54:54='a',<419>,1:54], [@33,85:85='a',<419>,1:85], [@46,124:124='a',<419>,1:124], [@59,156:156='a',<419>,1:156], [@72,187:187='a',<419>,1:187]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={part3=[[@38,94:98='part3',<419>,1:94]], part4=[[@51,133:137='part4',<419>,1:133]], part5=[[@64,165:169='part5',<419>,1:165]], part6=[[@77,196:200='part6',<419>,1:196]], part1=[[@12,33:37='part1',<419>,1:33]], part2=[[@25,63:67='part2',<419>,1:63]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={part1=[[@12,33:37='part1',<419>,1:33]], part2=[[@25,63:67='part2',<419>,1:63]], part3=[[@38,94:98='part3',<419>,1:94]], part4=[[@51,133:137='part4',<419>,1:133]], part5=[[@64,165:169='part5',<419>,1:165]], part6=[[@77,196:200='part6',<419>,1:196]]}, table_dictionary={events={ts=[[@7,24:24='a',<419>,1:24], [@20,54:54='a',<419>,1:54], [@33,85:85='a',<419>,1:85], [@46,124:124='a',<419>,1:124], [@59,156:156='a',<419>,1:156], [@72,187:187='a',<419>,1:187]]}}, interface={part1=[{name=ts, table_ref=a}], part2=[{name=ts, table_ref=a}], part3=[{name=ts, table_ref=a}], part4=[{name=ts, table_ref=a}], part5=[{name=ts, table_ref=a}], part6=[{name=ts, table_ref=a}]}, table_alias={a=events}}}", extractor.getSymbolTable().toString());
	}

	@Test
	public void dateaddAcceptsEveryDaySpelling() {
		final String query = "select dateadd(day, 1, a.ts) as part1, dateadd(d, 1, a.ts) as part2, dateadd(dd, 1, a.ts) as part3, dateadd(days, 1, a.ts) as part4, dateadd(dayofmonth, 1, a.ts) as part5 from events a";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=part1, datetime_function={function_name=dateadd, level={canonical=DAY, text=day, form=KEYWORD}, increment={literal=1}, value={column={name=ts, table_ref=a}}}}, 2={alias=part2, datetime_function={function_name=dateadd, level={canonical=DAY, text=d, form=IDENTIFIER}, increment={literal=1}, value={column={name=ts, table_ref=a}}}}, 3={alias=part3, datetime_function={function_name=dateadd, level={canonical=DAY, text=dd, form=IDENTIFIER}, increment={literal=1}, value={column={name=ts, table_ref=a}}}}, 4={alias=part4, datetime_function={function_name=dateadd, level={canonical=DAY, text=days, form=IDENTIFIER}, increment={literal=1}, value={column={name=ts, table_ref=a}}}}, 5={alias=part5, datetime_function={function_name=dateadd, level={canonical=DAY, text=dayofmonth, form=KEYWORD}, increment={literal=1}, value={column={name=ts, table_ref=a}}}}}, from={table={alias=a, table=events}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[part1, part2, part3, part4, part5]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{events={ts=[[@7,23:23='a',<419>,1:23], [@20,53:53='a',<419>,1:53], [@33,84:84='a',<419>,1:84], [@46,117:117='a',<419>,1:117], [@59,156:156='a',<419>,1:156]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={part3=[[@38,93:97='part3',<419>,1:93]], part4=[[@51,126:130='part4',<419>,1:126]], part5=[[@64,165:169='part5',<419>,1:165]], part1=[[@12,32:36='part1',<419>,1:32]], part2=[[@25,62:66='part2',<419>,1:62]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={part1=[[@12,32:36='part1',<419>,1:32]], part2=[[@25,62:66='part2',<419>,1:62]], part3=[[@38,93:97='part3',<419>,1:93]], part4=[[@51,126:130='part4',<419>,1:126]], part5=[[@64,165:169='part5',<419>,1:165]]}, table_dictionary={events={ts=[[@7,23:23='a',<419>,1:23], [@20,53:53='a',<419>,1:53], [@33,84:84='a',<419>,1:84], [@46,117:117='a',<419>,1:117], [@59,156:156='a',<419>,1:156]]}}, interface={part1=[{name=ts, table_ref=a}], part2=[{name=ts, table_ref=a}], part3=[{name=ts, table_ref=a}], part4=[{name=ts, table_ref=a}], part5=[{name=ts, table_ref=a}]}, table_alias={a=events}}}", extractor.getSymbolTable().toString());
	}

	@Test
	public void dateaddAcceptsEveryHourSpelling() {
		final String query = "select dateadd(hour, 1, a.ts) as part1, dateadd(h, 1, a.ts) as part2, dateadd(hh, 1, a.ts) as part3, dateadd(hr, 1, a.ts) as part4, dateadd(hours, 1, a.ts) as part5, dateadd(hrs, 1, a.ts) as part6 from events a";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=part1, datetime_function={function_name=dateadd, level={canonical=HOUR, text=hour, form=KEYWORD}, increment={literal=1}, value={column={name=ts, table_ref=a}}}}, 2={alias=part2, datetime_function={function_name=dateadd, level={canonical=HOUR, text=h, form=IDENTIFIER}, increment={literal=1}, value={column={name=ts, table_ref=a}}}}, 3={alias=part3, datetime_function={function_name=dateadd, level={canonical=HOUR, text=hh, form=IDENTIFIER}, increment={literal=1}, value={column={name=ts, table_ref=a}}}}, 4={alias=part4, datetime_function={function_name=dateadd, level={canonical=HOUR, text=hr, form=IDENTIFIER}, increment={literal=1}, value={column={name=ts, table_ref=a}}}}, 5={alias=part5, datetime_function={function_name=dateadd, level={canonical=HOUR, text=hours, form=IDENTIFIER}, increment={literal=1}, value={column={name=ts, table_ref=a}}}}, 6={alias=part6, datetime_function={function_name=dateadd, level={canonical=HOUR, text=hrs, form=IDENTIFIER}, increment={literal=1}, value={column={name=ts, table_ref=a}}}}}, from={table={alias=a, table=events}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[part1, part2, part3, part4, part5, part6]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{events={ts=[[@7,24:24='a',<419>,1:24], [@20,54:54='a',<419>,1:54], [@33,85:85='a',<419>,1:85], [@46,116:116='a',<419>,1:116], [@59,150:150='a',<419>,1:150], [@72,182:182='a',<419>,1:182]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={part3=[[@38,94:98='part3',<419>,1:94]], part4=[[@51,125:129='part4',<419>,1:125]], part5=[[@64,159:163='part5',<419>,1:159]], part6=[[@77,191:195='part6',<419>,1:191]], part1=[[@12,33:37='part1',<419>,1:33]], part2=[[@25,63:67='part2',<419>,1:63]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={part1=[[@12,33:37='part1',<419>,1:33]], part2=[[@25,63:67='part2',<419>,1:63]], part3=[[@38,94:98='part3',<419>,1:94]], part4=[[@51,125:129='part4',<419>,1:125]], part5=[[@64,159:163='part5',<419>,1:159]], part6=[[@77,191:195='part6',<419>,1:191]]}, table_dictionary={events={ts=[[@7,24:24='a',<419>,1:24], [@20,54:54='a',<419>,1:54], [@33,85:85='a',<419>,1:85], [@46,116:116='a',<419>,1:116], [@59,150:150='a',<419>,1:150], [@72,182:182='a',<419>,1:182]]}}, interface={part1=[{name=ts, table_ref=a}], part2=[{name=ts, table_ref=a}], part3=[{name=ts, table_ref=a}], part4=[{name=ts, table_ref=a}], part5=[{name=ts, table_ref=a}], part6=[{name=ts, table_ref=a}]}, table_alias={a=events}}}", extractor.getSymbolTable().toString());
	}

	@Test
	public void dateaddAcceptsEveryMinuteSpelling() {
		final String query = "select dateadd(minute, 1, a.ts) as part1, dateadd(m, 1, a.ts) as part2, dateadd(mi, 1, a.ts) as part3, dateadd(min, 1, a.ts) as part4, dateadd(minutes, 1, a.ts) as part5, dateadd(mins, 1, a.ts) as part6 from events a";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=part1, datetime_function={function_name=dateadd, level={canonical=MINUTE, text=minute, form=KEYWORD}, increment={literal=1}, value={column={name=ts, table_ref=a}}}}, 2={alias=part2, datetime_function={function_name=dateadd, level={canonical=MINUTE, text=m, form=IDENTIFIER}, increment={literal=1}, value={column={name=ts, table_ref=a}}}}, 3={alias=part3, datetime_function={function_name=dateadd, level={canonical=MINUTE, text=mi, form=IDENTIFIER}, increment={literal=1}, value={column={name=ts, table_ref=a}}}}, 4={alias=part4, datetime_function={function_name=dateadd, level={canonical=MINUTE, text=min, form=IDENTIFIER}, increment={literal=1}, value={column={name=ts, table_ref=a}}}}, 5={alias=part5, datetime_function={function_name=dateadd, level={canonical=MINUTE, text=minutes, form=IDENTIFIER}, increment={literal=1}, value={column={name=ts, table_ref=a}}}}, 6={alias=part6, datetime_function={function_name=dateadd, level={canonical=MINUTE, text=mins, form=IDENTIFIER}, increment={literal=1}, value={column={name=ts, table_ref=a}}}}}, from={table={alias=a, table=events}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[part1, part2, part3, part4, part5, part6]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{events={ts=[[@7,26:26='a',<419>,1:26], [@20,56:56='a',<419>,1:56], [@33,87:87='a',<419>,1:87], [@46,119:119='a',<419>,1:119], [@59,155:155='a',<419>,1:155], [@72,188:188='a',<419>,1:188]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={part3=[[@38,96:100='part3',<419>,1:96]], part4=[[@51,128:132='part4',<419>,1:128]], part5=[[@64,164:168='part5',<419>,1:164]], part6=[[@77,197:201='part6',<419>,1:197]], part1=[[@12,35:39='part1',<419>,1:35]], part2=[[@25,65:69='part2',<419>,1:65]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={part1=[[@12,35:39='part1',<419>,1:35]], part2=[[@25,65:69='part2',<419>,1:65]], part3=[[@38,96:100='part3',<419>,1:96]], part4=[[@51,128:132='part4',<419>,1:128]], part5=[[@64,164:168='part5',<419>,1:164]], part6=[[@77,197:201='part6',<419>,1:197]]}, table_dictionary={events={ts=[[@7,26:26='a',<419>,1:26], [@20,56:56='a',<419>,1:56], [@33,87:87='a',<419>,1:87], [@46,119:119='a',<419>,1:119], [@59,155:155='a',<419>,1:155], [@72,188:188='a',<419>,1:188]]}}, interface={part1=[{name=ts, table_ref=a}], part2=[{name=ts, table_ref=a}], part3=[{name=ts, table_ref=a}], part4=[{name=ts, table_ref=a}], part5=[{name=ts, table_ref=a}], part6=[{name=ts, table_ref=a}]}, table_alias={a=events}}}", extractor.getSymbolTable().toString());
	}

	@Test
	public void dateaddAcceptsEverySecondSpelling() {
		final String query = "select dateadd(second, 1, a.ts) as part1, dateadd(s, 1, a.ts) as part2, dateadd(sec, 1, a.ts) as part3, dateadd(seconds, 1, a.ts) as part4, dateadd(secs, 1, a.ts) as part5 from events a";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=part1, datetime_function={function_name=dateadd, level={canonical=SECOND, text=second, form=KEYWORD}, increment={literal=1}, value={column={name=ts, table_ref=a}}}}, 2={alias=part2, datetime_function={function_name=dateadd, level={canonical=SECOND, text=s, form=IDENTIFIER}, increment={literal=1}, value={column={name=ts, table_ref=a}}}}, 3={alias=part3, datetime_function={function_name=dateadd, level={canonical=SECOND, text=sec, form=IDENTIFIER}, increment={literal=1}, value={column={name=ts, table_ref=a}}}}, 4={alias=part4, datetime_function={function_name=dateadd, level={canonical=SECOND, text=seconds, form=IDENTIFIER}, increment={literal=1}, value={column={name=ts, table_ref=a}}}}, 5={alias=part5, datetime_function={function_name=dateadd, level={canonical=SECOND, text=secs, form=IDENTIFIER}, increment={literal=1}, value={column={name=ts, table_ref=a}}}}}, from={table={alias=a, table=events}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[part1, part2, part3, part4, part5]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{events={ts=[[@7,26:26='a',<419>,1:26], [@20,56:56='a',<419>,1:56], [@33,88:88='a',<419>,1:88], [@46,124:124='a',<419>,1:124], [@59,157:157='a',<419>,1:157]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={part3=[[@38,97:101='part3',<419>,1:97]], part4=[[@51,133:137='part4',<419>,1:133]], part5=[[@64,166:170='part5',<419>,1:166]], part1=[[@12,35:39='part1',<419>,1:35]], part2=[[@25,65:69='part2',<419>,1:65]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={part1=[[@12,35:39='part1',<419>,1:35]], part2=[[@25,65:69='part2',<419>,1:65]], part3=[[@38,97:101='part3',<419>,1:97]], part4=[[@51,133:137='part4',<419>,1:133]], part5=[[@64,166:170='part5',<419>,1:166]]}, table_dictionary={events={ts=[[@7,26:26='a',<419>,1:26], [@20,56:56='a',<419>,1:56], [@33,88:88='a',<419>,1:88], [@46,124:124='a',<419>,1:124], [@59,157:157='a',<419>,1:157]]}}, interface={part1=[{name=ts, table_ref=a}], part2=[{name=ts, table_ref=a}], part3=[{name=ts, table_ref=a}], part4=[{name=ts, table_ref=a}], part5=[{name=ts, table_ref=a}]}, table_alias={a=events}}}", extractor.getSymbolTable().toString());
	}

	@Test
	public void dateaddAcceptsEveryMillisecondSpelling() {
		final String query = "select dateadd(millisecond, 1, a.ts) as part1, dateadd(ms, 1, a.ts) as part2, dateadd(msec, 1, a.ts) as part3, dateadd(milliseconds, 1, a.ts) as part4 from events a";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=part1, datetime_function={function_name=dateadd, level={canonical=MILLISECOND, text=millisecond, form=IDENTIFIER}, increment={literal=1}, value={column={name=ts, table_ref=a}}}}, 2={alias=part2, datetime_function={function_name=dateadd, level={canonical=MILLISECOND, text=ms, form=IDENTIFIER}, increment={literal=1}, value={column={name=ts, table_ref=a}}}}, 3={alias=part3, datetime_function={function_name=dateadd, level={canonical=MILLISECOND, text=msec, form=IDENTIFIER}, increment={literal=1}, value={column={name=ts, table_ref=a}}}}, 4={alias=part4, datetime_function={function_name=dateadd, level={canonical=MILLISECOND, text=milliseconds, form=KEYWORD}, increment={literal=1}, value={column={name=ts, table_ref=a}}}}}, from={table={alias=a, table=events}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[part1, part2, part3, part4]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{events={ts=[[@7,31:31='a',<419>,1:31], [@20,62:62='a',<419>,1:62], [@33,95:95='a',<419>,1:95], [@46,136:136='a',<419>,1:136]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={part3=[[@38,104:108='part3',<419>,1:104]], part4=[[@51,145:149='part4',<419>,1:145]], part1=[[@12,40:44='part1',<419>,1:40]], part2=[[@25,71:75='part2',<419>,1:71]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={part1=[[@12,40:44='part1',<419>,1:40]], part2=[[@25,71:75='part2',<419>,1:71]], part3=[[@38,104:108='part3',<419>,1:104]], part4=[[@51,145:149='part4',<419>,1:145]]}, table_dictionary={events={ts=[[@7,31:31='a',<419>,1:31], [@20,62:62='a',<419>,1:62], [@33,95:95='a',<419>,1:95], [@46,136:136='a',<419>,1:136]]}}, interface={part1=[{name=ts, table_ref=a}], part2=[{name=ts, table_ref=a}], part3=[{name=ts, table_ref=a}], part4=[{name=ts, table_ref=a}]}, table_alias={a=events}}}", extractor.getSymbolTable().toString());
	}

	@Test
	public void dateaddAcceptsEveryMicrosecondSpelling() {
		final String query = "select dateadd(microsecond, 1, a.ts) as part1, dateadd(us, 1, a.ts) as part2, dateadd(usec, 1, a.ts) as part3, dateadd(microseconds, 1, a.ts) as part4 from events a";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=part1, datetime_function={function_name=dateadd, level={canonical=MICROSECOND, text=microsecond, form=IDENTIFIER}, increment={literal=1}, value={column={name=ts, table_ref=a}}}}, 2={alias=part2, datetime_function={function_name=dateadd, level={canonical=MICROSECOND, text=us, form=IDENTIFIER}, increment={literal=1}, value={column={name=ts, table_ref=a}}}}, 3={alias=part3, datetime_function={function_name=dateadd, level={canonical=MICROSECOND, text=usec, form=IDENTIFIER}, increment={literal=1}, value={column={name=ts, table_ref=a}}}}, 4={alias=part4, datetime_function={function_name=dateadd, level={canonical=MICROSECOND, text=microseconds, form=KEYWORD}, increment={literal=1}, value={column={name=ts, table_ref=a}}}}}, from={table={alias=a, table=events}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[part1, part2, part3, part4]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{events={ts=[[@7,31:31='a',<419>,1:31], [@20,62:62='a',<419>,1:62], [@33,95:95='a',<419>,1:95], [@46,136:136='a',<419>,1:136]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={part3=[[@38,104:108='part3',<419>,1:104]], part4=[[@51,145:149='part4',<419>,1:145]], part1=[[@12,40:44='part1',<419>,1:40]], part2=[[@25,71:75='part2',<419>,1:71]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={part1=[[@12,40:44='part1',<419>,1:40]], part2=[[@25,71:75='part2',<419>,1:71]], part3=[[@38,104:108='part3',<419>,1:104]], part4=[[@51,145:149='part4',<419>,1:145]]}, table_dictionary={events={ts=[[@7,31:31='a',<419>,1:31], [@20,62:62='a',<419>,1:62], [@33,95:95='a',<419>,1:95], [@46,136:136='a',<419>,1:136]]}}, interface={part1=[{name=ts, table_ref=a}], part2=[{name=ts, table_ref=a}], part3=[{name=ts, table_ref=a}], part4=[{name=ts, table_ref=a}]}, table_alias={a=events}}}", extractor.getSymbolTable().toString());
	}

	@Test
	public void dateaddAcceptsEveryNanosecondSpelling() {
		final String query = "select dateadd(nanosecond, 1, a.ts) as part1, dateadd(ns, 1, a.ts) as part2, dateadd(nsec, 1, a.ts) as part3, dateadd(nanosec, 1, a.ts) as part4, dateadd(nsecond, 1, a.ts) as part5, dateadd(nanoseconds, 1, a.ts) as part6, dateadd(nanosecs, 1, a.ts) as part7, dateadd(nseconds, 1, a.ts) as part8 from events a";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=part1, datetime_function={function_name=dateadd, level={canonical=NANOSECOND, text=nanosecond, form=IDENTIFIER}, increment={literal=1}, value={column={name=ts, table_ref=a}}}}, 2={alias=part2, datetime_function={function_name=dateadd, level={canonical=NANOSECOND, text=ns, form=IDENTIFIER}, increment={literal=1}, value={column={name=ts, table_ref=a}}}}, 3={alias=part3, datetime_function={function_name=dateadd, level={canonical=NANOSECOND, text=nsec, form=IDENTIFIER}, increment={literal=1}, value={column={name=ts, table_ref=a}}}}, 4={alias=part4, datetime_function={function_name=dateadd, level={canonical=NANOSECOND, text=nanosec, form=IDENTIFIER}, increment={literal=1}, value={column={name=ts, table_ref=a}}}}, 5={alias=part5, datetime_function={function_name=dateadd, level={canonical=NANOSECOND, text=nsecond, form=IDENTIFIER}, increment={literal=1}, value={column={name=ts, table_ref=a}}}}, 6={alias=part6, datetime_function={function_name=dateadd, level={canonical=NANOSECOND, text=nanoseconds, form=IDENTIFIER}, increment={literal=1}, value={column={name=ts, table_ref=a}}}}, 7={alias=part7, datetime_function={function_name=dateadd, level={canonical=NANOSECOND, text=nanosecs, form=IDENTIFIER}, increment={literal=1}, value={column={name=ts, table_ref=a}}}}, 8={alias=part8, datetime_function={function_name=dateadd, level={canonical=NANOSECOND, text=nseconds, form=IDENTIFIER}, increment={literal=1}, value={column={name=ts, table_ref=a}}}}}, from={table={alias=a, table=events}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[part1, part2, part3, part4, part5, part6, part7, part8]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{events={ts=[[@7,30:30='a',<419>,1:30], [@20,61:61='a',<419>,1:61], [@33,94:94='a',<419>,1:94], [@46,130:130='a',<419>,1:130], [@59,166:166='a',<419>,1:166], [@72,206:206='a',<419>,1:206], [@85,243:243='a',<419>,1:243], [@98,280:280='a',<419>,1:280]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={part1=[[@12,39:43='part1',<419>,1:39]], part2=[[@25,70:74='part2',<419>,1:70]], part3=[[@38,103:107='part3',<419>,1:103]], part4=[[@51,139:143='part4',<419>,1:139]], part5=[[@64,175:179='part5',<419>,1:175]], part6=[[@77,215:219='part6',<419>,1:215]], part7=[[@90,252:256='part7',<419>,1:252]], part8=[[@103,289:293='part8',<419>,1:289]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={part1=[[@12,39:43='part1',<419>,1:39]], part2=[[@25,70:74='part2',<419>,1:70]], part3=[[@38,103:107='part3',<419>,1:103]], part4=[[@51,139:143='part4',<419>,1:139]], part5=[[@64,175:179='part5',<419>,1:175]], part6=[[@77,215:219='part6',<419>,1:215]], part7=[[@90,252:256='part7',<419>,1:252]], part8=[[@103,289:293='part8',<419>,1:289]]}, table_dictionary={events={ts=[[@7,30:30='a',<419>,1:30], [@20,61:61='a',<419>,1:61], [@33,94:94='a',<419>,1:94], [@46,130:130='a',<419>,1:130], [@59,166:166='a',<419>,1:166], [@72,206:206='a',<419>,1:206], [@85,243:243='a',<419>,1:243], [@98,280:280='a',<419>,1:280]]}}, interface={part1=[{name=ts, table_ref=a}], part2=[{name=ts, table_ref=a}], part3=[{name=ts, table_ref=a}], part4=[{name=ts, table_ref=a}], part5=[{name=ts, table_ref=a}], part6=[{name=ts, table_ref=a}], part7=[{name=ts, table_ref=a}], part8=[{name=ts, table_ref=a}]}, table_alias={a=events}}}", extractor.getSymbolTable().toString());
	}

	@Test
	public void dateaddCanonicalizesPartsWithoutLosingSpellingOrForm() {
		final String query = "select DaTeAdD(yEaR, 1, a.ts) as y, DaTeAdD(mm, 1, a.ts) as mo, DaTeAdD('mOn', 1, a.ts) as mon, DaTeAdD(dayofmonth, 1, a.ts) as d, DaTeAdD(m, 1, a.ts) as mi, DaTeAdD(milliseconds, 1, a.ts) as ms, DaTeAdD(ns, 1, a.ts) as ns from events a";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=y, datetime_function={function_name=DaTeAdD, level={canonical=YEAR, text=yEaR, form=KEYWORD}, increment={literal=1}, value={column={name=ts, table_ref=a}}}}, 2={alias=mo, datetime_function={function_name=DaTeAdD, level={canonical=MONTH, text=mm, form=IDENTIFIER}, increment={literal=1}, value={column={name=ts, table_ref=a}}}}, 3={alias=mon, datetime_function={function_name=DaTeAdD, level={canonical=MONTH, text=mOn, form=STRING}, increment={literal=1}, value={column={name=ts, table_ref=a}}}}, 4={alias=d, datetime_function={function_name=DaTeAdD, level={canonical=DAY, text=dayofmonth, form=KEYWORD}, increment={literal=1}, value={column={name=ts, table_ref=a}}}}, 5={alias=mi, datetime_function={function_name=DaTeAdD, level={canonical=MINUTE, text=m, form=IDENTIFIER}, increment={literal=1}, value={column={name=ts, table_ref=a}}}}, 6={alias=ms, datetime_function={function_name=DaTeAdD, level={canonical=MILLISECOND, text=milliseconds, form=KEYWORD}, increment={literal=1}, value={column={name=ts, table_ref=a}}}}, 7={alias=ns, datetime_function={function_name=DaTeAdD, level={canonical=NANOSECOND, text=ns, form=IDENTIFIER}, increment={literal=1}, value={column={name=ts, table_ref=a}}}}}, from={table={alias=a, table=events}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[mo, d, ns, ms, y, mi, mon]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{events={ts=[[@7,24:24='a',<419>,1:24], [@20,51:51='a',<419>,1:51], [@33,82:82='a',<419>,1:82], [@46,119:119='a',<419>,1:119], [@59,145:145='a',<419>,1:145], [@72,183:183='a',<419>,1:183], [@85,211:211='a',<419>,1:211]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={mo=[[@25,60:61='mo',<419>,1:60]], d=[[@51,128:128='d',<419>,1:128]], ns=[[@90,220:221='ns',<419>,1:220]], ms=[[@77,192:193='ms',<419>,1:192]], y=[[@12,33:33='y',<419>,1:33]], mi=[[@64,154:155='mi',<419>,1:154]], mon=[[@38,91:93='mon',<419>,1:91]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={mo=[[@25,60:61='mo',<419>,1:60]], d=[[@51,128:128='d',<419>,1:128]], ns=[[@90,220:221='ns',<419>,1:220]], ms=[[@77,192:193='ms',<419>,1:192]], y=[[@12,33:33='y',<419>,1:33]], mi=[[@64,154:155='mi',<419>,1:154]], mon=[[@38,91:93='mon',<419>,1:91]]}, table_dictionary={events={ts=[[@7,24:24='a',<419>,1:24], [@20,51:51='a',<419>,1:51], [@33,82:82='a',<419>,1:82], [@46,119:119='a',<419>,1:119], [@59,145:145='a',<419>,1:145], [@72,183:183='a',<419>,1:183], [@85,211:211='a',<419>,1:211]]}}, interface={mo=[{name=ts, table_ref=a}], d=[{name=ts, table_ref=a}], ns=[{name=ts, table_ref=a}], ms=[{name=ts, table_ref=a}], y=[{name=ts, table_ref=a}], mi=[{name=ts, table_ref=a}], mon=[{name=ts, table_ref=a}]}, table_alias={a=events}}}", extractor.getSymbolTable().toString());
	}

	@Test
	public void dateaddDistinguishesRealYearAndDayColumnsFromLevels() {
		final String query = "select dateadd(year, a.year + 1, a.ts) as annual, dateadd(day, a.day + 1, a.ts) as daily from events a";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=annual, datetime_function={function_name=dateadd, level={canonical=YEAR, text=year, form=KEYWORD}, increment={calc={left={column={name=year, table_ref=a}}, right={literal=1}, operator=+}}, value={column={name=ts, table_ref=a}}}}, 2={alias=daily, datetime_function={function_name=dateadd, level={canonical=DAY, text=day, form=KEYWORD}, increment={calc={left={column={name=day, table_ref=a}}, right={literal=1}, operator=+}}, value={column={name=ts, table_ref=a}}}}}, from={table={alias=a, table=events}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[daily, annual]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{events={year=[[@5,21:21='a',<419>,1:21]], day=[[@22,63:63='a',<419>,1:63]], ts=[[@11,33:33='a',<419>,1:33], [@28,74:74='a',<419>,1:74]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={annual=[[@16,42:47='annual',<419>,1:42]], daily=[[@33,83:87='daily',<419>,1:83]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={daily=[[@33,83:87='daily',<419>,1:83]], annual=[[@16,42:47='annual',<419>,1:42]]}, table_dictionary={events={year=[[@5,21:21='a',<419>,1:21]], day=[[@22,63:63='a',<419>,1:63]], ts=[[@11,33:33='a',<419>,1:33], [@28,74:74='a',<419>,1:74]]}}, interface={daily=[{name=day, table_ref=a}, {name=ts, table_ref=a}], annual=[{name=year, table_ref=a}, {name=ts, table_ref=a}]}, table_alias={a=events}}}", extractor.getSymbolTable().toString());
	}

	@Test
	public void dateaddRetainsNumericEdgeExpressionShapes() {
		final String query = "select dateadd(day, 0, a.ts) as zeroed, dateadd(day, -2, a.ts) as negative, dateadd(day, -a.offset_days, a.ts) as negated, dateadd(day, (a.offset_days + b.delta) * 2, a.ts) as calculated from events a join adjustments b on a.id = b.id";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=zeroed, datetime_function={function_name=dateadd, level={canonical=DAY, text=day, form=KEYWORD}, increment={literal=0}, value={column={name=ts, table_ref=a}}}}, 2={alias=negative, datetime_function={function_name=dateadd, level={canonical=DAY, text=day, form=KEYWORD}, increment={calc={left={literal=-1}, right={literal=2}, operator=*}}, value={column={name=ts, table_ref=a}}}}, 3={alias=negated, datetime_function={function_name=dateadd, level={canonical=DAY, text=day, form=KEYWORD}, increment={calc={left={literal=-1}, right={column={name=offset_days, table_ref=a}}, operator=*}}, value={column={name=ts, table_ref=a}}}}, 4={alias=calculated, datetime_function={function_name=dateadd, level={canonical=DAY, text=day, form=KEYWORD}, increment={calc={left={parentheses={calc={left={column={name=offset_days, table_ref=a}}, right={column={name=delta, table_ref=b}}, operator=+}}}, right={literal=2}, operator=*}}, value={column={name=ts, table_ref=a}}}}}, from={join={1={table={alias=a, table=events}}, 2={join=join, on={condition={left={column={name=id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=adjustments}}}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[negative, negated, zeroed, calculated]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{adjustments={delta=[[@53,153:153='b',<419>,1:153]], id=[[@77,230:230='b',<419>,1:230]]}, events={offset_days=[[@33,90:90='a',<419>,1:90], [@49,137:137='a',<419>,1:137]], id=[[@73,223:223='a',<419>,1:223]], ts=[[@7,23:23='a',<419>,1:23], [@21,57:57='a',<419>,1:57], [@37,105:105='a',<419>,1:105], [@60,167:167='a',<419>,1:167]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={negative=[[@26,66:73='negative',<419>,1:66]], negated=[[@42,114:120='negated',<419>,1:114]], zeroed=[[@12,32:37='zeroed',<419>,1:32]], calculated=[[@65,176:185='calculated',<419>,1:176]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={negative=[[@26,66:73='negative',<419>,1:66]], negated=[[@42,114:120='negated',<419>,1:114]], zeroed=[[@12,32:37='zeroed',<419>,1:32]], calculated=[[@65,176:185='calculated',<419>,1:176]]}, table_dictionary={adjustments={delta=[[@53,153:153='b',<419>,1:153]], id=[[@77,230:230='b',<419>,1:230]]}, events={offset_days=[[@33,90:90='a',<419>,1:90], [@49,137:137='a',<419>,1:137]], id=[[@73,223:223='a',<419>,1:223]], ts=[[@7,23:23='a',<419>,1:23], [@21,57:57='a',<419>,1:57], [@37,105:105='a',<419>,1:105], [@60,167:167='a',<419>,1:167]]}}, filters=[{name=id, table_ref=a}, {name=id, table_ref=b}], interface={negative=[{name=ts, table_ref=a}], negated=[{name=offset_days, table_ref=a}, {name=ts, table_ref=a}], zeroed=[{name=ts, table_ref=a}], calculated=[{name=offset_days, table_ref=a}, {name=delta, table_ref=b}, {name=ts, table_ref=a}]}, table_alias={a=events, b=adjustments}}}", extractor.getSymbolTable().toString());
	}

	@Test
	public void dateaddKeepsRepeatedReferencesAndSiblingLineageSeparate() {
		final String query = "select dateadd(day, a.offset_days + a.offset_days, a.ts + a.offset_days) as first_shift, dateadd(hour, b.hours + b.hours, b.ts + b.hours) as second_shift from events a join adjustments b on a.id = b.id";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=first_shift, datetime_function={function_name=dateadd, level={canonical=DAY, text=day, form=KEYWORD}, increment={calc={left={column={name=offset_days, table_ref=a}}, right={column={name=offset_days, table_ref=a}}, operator=+}}, value={calc={left={column={name=ts, table_ref=a}}, right={column={name=offset_days, table_ref=a}}, operator=+}}}}, 2={alias=second_shift, datetime_function={function_name=dateadd, level={canonical=HOUR, text=hour, form=KEYWORD}, increment={calc={left={column={name=hours, table_ref=b}}, right={column={name=hours, table_ref=b}}, operator=+}}, value={calc={left={column={name=ts, table_ref=b}}, right={column={name=hours, table_ref=b}}, operator=+}}}}}, from={join={1={table={alias=a, table=events}}, 2={join=join, on={condition={left={column={name=id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=adjustments}}}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[first_shift, second_shift]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{adjustments={hours=[[@28,103:103='b',<419>,1:103], [@32,113:113='b',<419>,1:113], [@40,129:129='b',<419>,1:129]], id=[[@57,197:197='b',<419>,1:197]], ts=[[@36,122:122='b',<419>,1:122]]}, events={offset_days=[[@5,20:20='a',<419>,1:20], [@9,36:36='a',<419>,1:36], [@17,58:58='a',<419>,1:58]], id=[[@53,190:190='a',<419>,1:190]], ts=[[@13,51:51='a',<419>,1:51]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={first_shift=[[@22,76:86='first_shift',<419>,1:76]], second_shift=[[@45,141:152='second_shift',<419>,1:141]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={first_shift=[[@22,76:86='first_shift',<419>,1:76]], second_shift=[[@45,141:152='second_shift',<419>,1:141]]}, table_dictionary={adjustments={hours=[[@28,103:103='b',<419>,1:103], [@32,113:113='b',<419>,1:113], [@40,129:129='b',<419>,1:129]], id=[[@57,197:197='b',<419>,1:197]], ts=[[@36,122:122='b',<419>,1:122]]}, events={offset_days=[[@5,20:20='a',<419>,1:20], [@9,36:36='a',<419>,1:36], [@17,58:58='a',<419>,1:58]], id=[[@53,190:190='a',<419>,1:190]], ts=[[@13,51:51='a',<419>,1:51]]}}, filters=[{name=id, table_ref=a}, {name=id, table_ref=b}], interface={first_shift=[{name=offset_days, table_ref=a}, {name=ts, table_ref=a}], second_shift=[{name=hours, table_ref=b}, {name=ts, table_ref=b}]}, table_alias={a=events, b=adjustments}}}", extractor.getSymbolTable().toString());
	}

	@Test
	public void dateaddRetainsNestedCallsInsideOrdinaryFunctions() {
		final String query = "select coalesce(a.ts, dateadd(day, 1, a.ts)) as fallback, greatest(dateadd(day, a.offset_days, a.ts), dateadd(hour, b.hours, b.ts)) as latest from events a join adjustments b on a.id = b.id";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={function={parameters={1={column={name=ts, table_ref=a}}, 2={datetime_function={function_name=dateadd, level={canonical=DAY, text=day, form=KEYWORD}, increment={literal=1}, value={column={name=ts, table_ref=a}}}}}, function_name=coalesce}, alias=fallback}, 2={function={parameters={1={datetime_function={function_name=dateadd, level={canonical=DAY, text=day, form=KEYWORD}, increment={column={name=offset_days, table_ref=a}}, value={column={name=ts, table_ref=a}}}}, 2={datetime_function={function_name=dateadd, level={canonical=HOUR, text=hour, form=KEYWORD}, increment={column={name=hours, table_ref=b}}, value={column={name=ts, table_ref=b}}}}}, function_name=greatest}, alias=latest}}, from={join={1={table={alias=a, table=events}}, 2={join=join, on={condition={left={column={name=id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=adjustments}}}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[fallback, latest]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{adjustments={hours=[[@40,116:116='b',<419>,1:116]], id=[[@62,185:185='b',<419>,1:185]], ts=[[@44,125:125='b',<419>,1:125]]}, events={offset_days=[[@27,80:80='a',<419>,1:80]], id=[[@58,178:178='a',<419>,1:178]], ts=[[@3,16:16='a',<419>,1:16], [@13,38:38='a',<419>,1:38], [@31,95:95='a',<419>,1:95]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={fallback=[[@19,48:55='fallback',<419>,1:48]], latest=[[@50,135:140='latest',<419>,1:135]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={fallback=[[@19,48:55='fallback',<419>,1:48]], latest=[[@50,135:140='latest',<419>,1:135]]}, table_dictionary={adjustments={hours=[[@40,116:116='b',<419>,1:116]], id=[[@62,185:185='b',<419>,1:185]], ts=[[@44,125:125='b',<419>,1:125]]}, events={offset_days=[[@27,80:80='a',<419>,1:80]], id=[[@58,178:178='a',<419>,1:178]], ts=[[@3,16:16='a',<419>,1:16], [@13,38:38='a',<419>,1:38], [@31,95:95='a',<419>,1:95]]}}, filters=[{name=id, table_ref=a}, {name=id, table_ref=b}], interface={fallback=[{name=ts, table_ref=a}], latest=[{name=offset_days, table_ref=a}, {name=ts, table_ref=a}, {name=hours, table_ref=b}, {name=ts, table_ref=b}]}, table_alias={a=events, b=adjustments}}}", extractor.getSymbolTable().toString());
	}

	@Test
	public void dateaddRetainsOrdinaryFunctionsInsideBothArguments() {
		final String query = "select dateadd(day, abs(a.days + <inc>), a.ts) as numeric_inner, dateadd(day, 1, coalesce(a.ts, a.<fallback>)) as date_inner, dateadd(day, abs(a.days + <inc2>), coalesce(a.ts, a.<fallback2>)) as both_inner from events a";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=numeric_inner, datetime_function={function_name=dateadd, level={canonical=DAY, text=day, form=KEYWORD}, increment={function={parameters={1={calc={left={column={name=days, table_ref=a}}, right={substitution={name=<inc>, type=predicand}}, operator=+}}}, function_name=abs}}, value={column={name=ts, table_ref=a}}}}, 2={alias=date_inner, datetime_function={function_name=dateadd, level={canonical=DAY, text=day, form=KEYWORD}, increment={literal=1}, value={function={parameters={1={column={name=ts, table_ref=a}}, 2={column={substitution={name=<fallback>, type=column}, table_ref=a}}}, function_name=coalesce}}}}, 3={alias=both_inner, datetime_function={function_name=dateadd, level={canonical=DAY, text=day, form=KEYWORD}, increment={function={parameters={1={calc={left={column={name=days, table_ref=a}}, right={substitution={name=<inc2>, type=predicand}}, operator=+}}}, function_name=abs}}, value={function={parameters={1={column={name=ts, table_ref=a}}, 2={column={substitution={name=<fallback2>, type=column}, table_ref=a}}}, function_name=coalesce}}}}}, from={table={alias=a, table=events}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[date_inner, numeric_inner, both_inner]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<fallback2>=column, <inc>=predicand, <inc2>=predicand, <fallback>=column}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{events={<fallback2>=[[@60,176:176='a',<419>,1:176]], days=[[@7,24:24='a',<419>,1:24], [@47,143:143='a',<419>,1:143]], <fallback>=[[@33,96:96='a',<419>,1:96]], ts=[[@14,41:41='a',<419>,1:41], [@29,90:90='a',<419>,1:90], [@56,170:170='a',<419>,1:170]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={numeric_inner=[[@19,50:62='numeric_inner',<419>,1:50]], both_inner=[[@66,195:204='both_inner',<419>,1:195]], date_inner=[[@39,114:123='date_inner',<419>,1:114]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={date_inner=[[@39,114:123='date_inner',<419>,1:114]], numeric_inner=[[@19,50:62='numeric_inner',<419>,1:50]], both_inner=[[@66,195:204='both_inner',<419>,1:195]]}, table_dictionary={events={<fallback2>=[[@60,176:176='a',<419>,1:176]], days=[[@7,24:24='a',<419>,1:24], [@47,143:143='a',<419>,1:143]], <fallback>=[[@33,96:96='a',<419>,1:96]], ts=[[@14,41:41='a',<419>,1:41], [@29,90:90='a',<419>,1:90], [@56,170:170='a',<419>,1:170]]}}, interface={date_inner=[{name=ts, table_ref=a}, {substitution={name=<fallback>, type=column}, table_ref=a}], numeric_inner=[{name=days, table_ref=a}, {name=<inc>, type=predicand}, {name=ts, table_ref=a}], both_inner=[{name=days, table_ref=a}, {name=<inc2>, type=predicand}, {name=ts, table_ref=a}, {substitution={name=<fallback2>, type=column}, table_ref=a}]}, table_alias={a=events}}}", extractor.getSymbolTable().toString());
	}

	@Test
	public void dateaddRetainsIndependentNestedDateaddMetadataAndInputs() {
		final String query = "select dateadd(month, 2, dateadd(day, 3, a.created_at)) as simple, dateadd(month, a.outer_days + <outer>, dateadd(day, a.inner_days + <inner>, a.created_at)) as nested from events a";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=simple, datetime_function={function_name=dateadd, level={canonical=MONTH, text=month, form=KEYWORD}, increment={literal=2}, value={datetime_function={function_name=dateadd, level={canonical=DAY, text=day, form=KEYWORD}, increment={literal=3}, value={column={name=created_at, table_ref=a}}}}}}, 2={alias=nested, datetime_function={function_name=dateadd, level={canonical=MONTH, text=month, form=KEYWORD}, increment={calc={left={column={name=outer_days, table_ref=a}}, right={substitution={name=<outer>, type=predicand}}, operator=+}}, value={datetime_function={function_name=dateadd, level={canonical=DAY, text=day, form=KEYWORD}, increment={calc={left={column={name=inner_days, table_ref=a}}, right={substitution={name=<inner>, type=predicand}}, operator=+}}, value={column={name=created_at, table_ref=a}}}}}}}, from={table={alias=a, table=events}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[simple, nested]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<inner>=predicand, <outer>=predicand}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{events={outer_days=[[@25,82:82='a',<419>,1:82]], created_at=[[@13,41:41='a',<419>,1:41], [@41,143:143='a',<419>,1:143]], inner_days=[[@35,119:119='a',<419>,1:119]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={nested=[[@47,161:166='nested',<419>,1:161]], simple=[[@19,59:64='simple',<419>,1:59]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={simple=[[@19,59:64='simple',<419>,1:59]], nested=[[@47,161:166='nested',<419>,1:161]]}, table_dictionary={events={outer_days=[[@25,82:82='a',<419>,1:82]], created_at=[[@13,41:41='a',<419>,1:41], [@41,143:143='a',<419>,1:143]], inner_days=[[@35,119:119='a',<419>,1:119]]}}, interface={simple=[{name=created_at, table_ref=a}], nested=[{name=outer_days, table_ref=a}, {name=<outer>, type=predicand}, {name=inner_days, table_ref=a}, {name=<inner>, type=predicand}, {name=created_at, table_ref=a}]}, table_alias={a=events}}}", extractor.getSymbolTable().toString());
	}

	@Test
	public void dateaddRetainsCaseBranchesAndCaseArguments() {
		final String query = "select case when a.flag = 1 then dateadd(day, a.days, a.ts) else dateadd(hour, 2, a.backup_ts) end as branched, dateadd(day, case when a.flag = 1 then a.days else 2 end, case when a.active = 1 then a.ts else a.backup_ts end) as arguments from events a";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=branched, case={clauses={1={then={datetime_function={function_name=dateadd, level={canonical=DAY, text=day, form=KEYWORD}, increment={column={name=days, table_ref=a}}, value={column={name=ts, table_ref=a}}}}, when={condition={left={column={name=flag, table_ref=a}}, right={literal=1}, operator==}}}}, else={datetime_function={function_name=dateadd, level={canonical=HOUR, text=hour, form=KEYWORD}, increment={literal=2}, value={column={name=backup_ts, table_ref=a}}}}}}, 2={alias=arguments, datetime_function={function_name=dateadd, level={canonical=DAY, text=day, form=KEYWORD}, increment={case={clauses={1={then={column={name=days, table_ref=a}}, when={condition={left={column={name=flag, table_ref=a}}, right={literal=1}, operator==}}}}, else={literal=2}}}, value={case={clauses={1={then={column={name=ts, table_ref=a}}, when={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}, else={column={name=backup_ts, table_ref=a}}}}}}}, from={table={alias=a, table=events}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[arguments, branched]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{events={flag=[[@3,17:17='a',<419>,1:17], [@42,135:135='a',<419>,1:135]], backup_ts=[[@28,82:82='a',<419>,1:82], [@67,208:208='a',<419>,1:208]], active=[[@57,180:180='a',<419>,1:180]], days=[[@13,46:46='a',<419>,1:46], [@48,151:151='a',<419>,1:151]], ts=[[@17,54:54='a',<419>,1:54], [@63,198:198='a',<419>,1:198]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={branched=[[@34,102:109='branched',<419>,1:102]], arguments=[[@73,228:236='arguments',<419>,1:228]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={arguments=[[@73,228:236='arguments',<419>,1:228]], branched=[[@34,102:109='branched',<419>,1:102]]}, table_dictionary={events={flag=[[@3,17:17='a',<419>,1:17], [@42,135:135='a',<419>,1:135]], backup_ts=[[@28,82:82='a',<419>,1:82], [@67,208:208='a',<419>,1:208]], active=[[@57,180:180='a',<419>,1:180]], days=[[@13,46:46='a',<419>,1:46], [@48,151:151='a',<419>,1:151]], ts=[[@17,54:54='a',<419>,1:54], [@63,198:198='a',<419>,1:198]]}}, interface={arguments=[{name=days, table_ref=a}, {name=flag, table_ref=a}, {name=ts, table_ref=a}, {name=active, table_ref=a}, {name=backup_ts, table_ref=a}], branched=[{name=days, table_ref=a}, {name=ts, table_ref=a}, {name=flag, table_ref=a}, {name=backup_ts, table_ref=a}]}, table_alias={a=events}}}", extractor.getSymbolTable().toString());
	}

	@Test
	public void dateaddDerivedProducerRetainsQualifiedValueLineage() {
		final String query = "select d.shifted as result from (select dateadd(year, 1, a.created_at) as shifted from events a) d";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=shifted, table_ref=d}, alias=result}}, from={table={alias=d, query={select={1={alias=shifted, datetime_function={function_name=dateadd, level={canonical=YEAR, text=year, form=KEYWORD}, increment={literal=1}, value={column={name=created_at, table_ref=a}}}}}, from={table={alias=a, table=events}}}}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[result]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{events={created_at=[[@15,57:57='a',<419>,1:57]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={shifted=[[@20,74:80='shifted',<419>,1:74], [@1,7:7='d',<419>,1:7]]}, query1={result=[[@5,20:25='result',<419>,1:20]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query1={query_dictionary={result=[[@5,20:25='result',<419>,1:20]]}, def_query0={query_dictionary={shifted=[[@20,74:80='shifted',<419>,1:74], [@1,7:7='d',<419>,1:7]]}, table_dictionary={events={created_at=[[@15,57:57='a',<419>,1:57]]}}, interface={shifted=[{name=created_at, table_ref=a}]}, table_alias={a=events}}, interface={result=[{name=shifted, table_ref=d}]}, table_alias={d=query0}}}", extractor.getSymbolTable().toString());

		final String expectedQualifiedDefQuery1DefQuery0QueryDictionary_0 = "{shifted=[[@20,74:80='shifted',<419>,1:74], [@1,7:7='d',<419>,1:7]]}";
		assertSymbolTreePathEquals(extractor.getSymbolTable(), "def_query1.def_query0.query_dictionary", expectedQualifiedDefQuery1DefQuery0QueryDictionary_0);
		final String expectedQualifiedDefQuery1DefQuery0TableDictionary_1 = "{events={created_at=[[@15,57:57='a',<419>,1:57]]}}";
		assertSymbolTreePathEquals(extractor.getSymbolTable(), "def_query1.def_query0.table_dictionary", expectedQualifiedDefQuery1DefQuery0TableDictionary_1);
		final String expectedQualifiedDefQuery1DefQuery0Interface_2 = "{shifted=[{name=created_at, table_ref=a}]}";
		assertSymbolTreePathEquals(extractor.getSymbolTable(), "def_query1.def_query0.interface", expectedQualifiedDefQuery1DefQuery0Interface_2);
		final String expectedQualifiedDefQuery1DefQuery0TableAlias_3 = "{a=events}";
		assertSymbolTreePathEquals(extractor.getSymbolTable(), "def_query1.def_query0.table_alias", expectedQualifiedDefQuery1DefQuery0TableAlias_3);
		final String expectedQualifiedDefQuery1Interface_4 = "{result=[{name=shifted, table_ref=d}]}";
		assertSymbolTreePathEquals(extractor.getSymbolTable(), "def_query1.interface", expectedQualifiedDefQuery1Interface_4);
		final String expectedQualifiedDefQuery1TableAlias_5 = "{d=query0}";
		assertSymbolTreePathEquals(extractor.getSymbolTable(), "def_query1.table_alias", expectedQualifiedDefQuery1TableAlias_5);
	}

	@Test
	public void dateaddDerivedProducerResolvesUnqualifiedValueLineage() {
		final String query = "select d.shifted as result from (select dateadd(year, 1, created_at) as shifted from events a) d";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=shifted, table_ref=d}, alias=result}}, from={table={alias=d, query={select={1={alias=shifted, datetime_function={function_name=dateadd, level={canonical=YEAR, text=year, form=KEYWORD}, increment={literal=1}, value={column={name=created_at, table_ref=null}}}}}, from={table={alias=a, table=events}}}}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[result]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{events={created_at=[[@15,57:66='created_at',<419>,1:57]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={shifted=[[@18,72:78='shifted',<419>,1:72], [@1,7:7='d',<419>,1:7]]}, query1={result=[[@5,20:25='result',<419>,1:20]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query1={query_dictionary={result=[[@5,20:25='result',<419>,1:20]]}, def_query0={query_dictionary={shifted=[[@18,72:78='shifted',<419>,1:72], [@1,7:7='d',<419>,1:7]]}, table_dictionary={events={created_at=[[@15,57:66='created_at',<419>,1:57]]}}, interface={shifted=[{name=created_at, table_ref=events}]}, table_alias={a=events}}, interface={result=[{name=shifted, table_ref=d}]}, table_alias={d=query0}}}", extractor.getSymbolTable().toString());

		final String expectedUnqualifiedDefQuery1DefQuery0QueryDictionary_0 = "{shifted=[[@18,72:78='shifted',<419>,1:72], [@1,7:7='d',<419>,1:7]]}";
		assertSymbolTreePathEquals(extractor.getSymbolTable(), "def_query1.def_query0.query_dictionary", expectedUnqualifiedDefQuery1DefQuery0QueryDictionary_0);
		final String expectedUnqualifiedDefQuery1DefQuery0TableDictionary_1 = "{events={created_at=[[@15,57:66='created_at',<419>,1:57]]}}";
		assertSymbolTreePathEquals(extractor.getSymbolTable(), "def_query1.def_query0.table_dictionary", expectedUnqualifiedDefQuery1DefQuery0TableDictionary_1);
		final String expectedUnqualifiedDefQuery1DefQuery0Interface_2 = "{shifted=[{name=created_at, table_ref=events}]}";
		assertSymbolTreePathEquals(extractor.getSymbolTable(), "def_query1.def_query0.interface", expectedUnqualifiedDefQuery1DefQuery0Interface_2);
		final String expectedUnqualifiedDefQuery1DefQuery0TableAlias_3 = "{a=events}";
		assertSymbolTreePathEquals(extractor.getSymbolTable(), "def_query1.def_query0.table_alias", expectedUnqualifiedDefQuery1DefQuery0TableAlias_3);
		final String expectedUnqualifiedDefQuery1Interface_4 = "{result=[{name=shifted, table_ref=d}]}";
		assertSymbolTreePathEquals(extractor.getSymbolTable(), "def_query1.interface", expectedUnqualifiedDefQuery1Interface_4);
		final String expectedUnqualifiedDefQuery1TableAlias_5 = "{d=query0}";
		assertSymbolTreePathEquals(extractor.getSymbolTable(), "def_query1.table_alias", expectedUnqualifiedDefQuery1TableAlias_5);
	}

	@Test
	public void dateaddDerivedProducerRetainsJoinedFormulaAndFilters() {
		final String query = "select d.shifted as result from (select dateadd(day, a.offset_days + b.extra_days * 2, DATE '2026-01-01') as shifted from events a join accounts b on a.account_id = b.id) d";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=shifted, table_ref=d}, alias=result}}, from={table={alias=d, query={select={1={alias=shifted, datetime_function={function_name=dateadd, level={canonical=DAY, text=day, form=KEYWORD}, increment={calc={left={column={name=offset_days, table_ref=a}}, right={calc={left={column={name=extra_days, table_ref=b}}, right={literal=2}, operator=*}}, operator=+}}, value={source_type=DATE, literal=2026-01-01}}}}, from={join={1={table={alias=a, table=events}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}}}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[result]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{accounts={extra_days=[[@17,69:69='b',<419>,1:69]], id=[[@39,165:165='b',<419>,1:165]]}, events={account_id=[[@35,150:150='a',<419>,1:150]], offset_days=[[@13,53:53='a',<419>,1:53]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={shifted=[[@27,109:115='shifted',<419>,1:109], [@1,7:7='d',<419>,1:7]]}, query1={result=[[@5,20:25='result',<419>,1:20]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query1={query_dictionary={result=[[@5,20:25='result',<419>,1:20]]}, def_query0={query_dictionary={shifted=[[@27,109:115='shifted',<419>,1:109], [@1,7:7='d',<419>,1:7]]}, table_dictionary={accounts={extra_days=[[@17,69:69='b',<419>,1:69]], id=[[@39,165:165='b',<419>,1:165]]}, events={account_id=[[@35,150:150='a',<419>,1:150]], offset_days=[[@13,53:53='a',<419>,1:53]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}], interface={shifted=[{name=offset_days, table_ref=a}, {name=extra_days, table_ref=b}]}, table_alias={a=events, b=accounts}}, interface={result=[{name=shifted, table_ref=d}]}, table_alias={d=query0}}}", extractor.getSymbolTable().toString());

		final String expectedJoinedDefQuery1DefQuery0QueryDictionary_0 = "{shifted=[[@27,109:115='shifted',<419>,1:109], [@1,7:7='d',<419>,1:7]]}";
		assertSymbolTreePathEquals(extractor.getSymbolTable(), "def_query1.def_query0.query_dictionary", expectedJoinedDefQuery1DefQuery0QueryDictionary_0);
		final String expectedJoinedDefQuery1DefQuery0TableDictionary_1 = "{accounts={extra_days=[[@17,69:69='b',<419>,1:69]], id=[[@39,165:165='b',<419>,1:165]]}, events={account_id=[[@35,150:150='a',<419>,1:150]], offset_days=[[@13,53:53='a',<419>,1:53]]}}";
		assertSymbolTreePathEquals(extractor.getSymbolTable(), "def_query1.def_query0.table_dictionary", expectedJoinedDefQuery1DefQuery0TableDictionary_1);
		final String expectedJoinedDefQuery1DefQuery0Filters_2 = "[{name=account_id, table_ref=a}, {name=id, table_ref=b}]";
		assertSymbolTreePathEquals(extractor.getSymbolTable(), "def_query1.def_query0.filters", expectedJoinedDefQuery1DefQuery0Filters_2);
		final String expectedJoinedDefQuery1DefQuery0Interface_3 = "{shifted=[{name=offset_days, table_ref=a}, {name=extra_days, table_ref=b}]}";
		assertSymbolTreePathEquals(extractor.getSymbolTable(), "def_query1.def_query0.interface", expectedJoinedDefQuery1DefQuery0Interface_3);
		final String expectedJoinedDefQuery1DefQuery0TableAlias_4 = "{a=events, b=accounts}";
		assertSymbolTreePathEquals(extractor.getSymbolTable(), "def_query1.def_query0.table_alias", expectedJoinedDefQuery1DefQuery0TableAlias_4);
		final String expectedJoinedDefQuery1Interface_5 = "{result=[{name=shifted, table_ref=d}]}";
		assertSymbolTreePathEquals(extractor.getSymbolTable(), "def_query1.interface", expectedJoinedDefQuery1Interface_5);
		final String expectedJoinedDefQuery1TableAlias_6 = "{d=query0}";
		assertSymbolTreePathEquals(extractor.getSymbolTable(), "def_query1.table_alias", expectedJoinedDefQuery1TableAlias_6);
	}

	@Test
	public void dateaddReportsInvalidLevelsAtTheirSourceWithoutCollectingColumns() {
		String[] invalidParts = {"bogus", "'bogus'", "''", "'engine''part'", "epoch", "yaer"};
		for (String part : invalidParts) {
			String query = "select dateadd(" + part + ", 1, a.ts) as shifted from events a";
			SqlParserAccess parser = new SqlParserAccess(false, false, false);
			parser.executeTheParse(query, "SQL");
			Assert.assertEquals(query, 0, parser.getParser().getNumberOfSyntaxErrors());
			Assert.assertEquals(query + ": " + parser.getAllDiagnostics(), 1, parser.getAllDiagnostics().size());
			assertFatalDiagnosticAtPositionWithFullMessage(parser.getSnippet(), "INVALID_DATETIME_PART",
					"Invalid date/time part " + part + " for DATEADD at (l:1 c:15).", part, 1, 15);
			Assert.assertEquals(part, findFatalDiagnosticByCodeAndFragment(
					parser.getSnippet(), "INVALID_DATETIME_PART", part).tokenText());
			Assert.assertFalse(parser.getSnippet().getSymbolTable().toString().contains(part));
		}
	}

	@Test
	public void dateaddRejectsEpochSecondWhileExtractAcceptsIt() {
		SqlParserAccess dateadd = new SqlParserAccess(false, false, false);
		dateadd.executeTheParse("select dateadd(epoch_second, 1, a.ts) as shifted from events a", "SQL");
		Assert.assertEquals(0, dateadd.getParser().getNumberOfSyntaxErrors());
		Assert.assertEquals(dateadd.getAllDiagnostics().toString(), 1, dateadd.getAllDiagnostics().size());
		assertFatalDiagnosticAtPositionWithFullMessage(dateadd.getSnippet(), "INVALID_DATETIME_PART",
				"Invalid date/time part epoch_second for DATEADD at (l:1 c:15).", "epoch_second", 1, 15);
		Assert.assertEquals("{def_query0={query_dictionary={shifted=[[@12,41:47='shifted',<419>,1:41]]}, table_dictionary={events={ts=[[@7,32:32='a',<419>,1:32]]}}, interface={shifted=[{name=ts, table_ref=a}]}, table_alias={a=events}}}",
				dateadd.getSnippet().getSymbolTable().toString());

		SqlParserAccess extract = new SqlParserAccess(false, false, false);
		extract.executeTheParse("select extract(epoch_second from a.ts) as seconds from events a", "SQL");
		Assert.assertEquals(0, extract.getParser().getNumberOfSyntaxErrors());
		Assert.assertEquals(extract.getAllDiagnostics().toString(), 1, extract.getAllDiagnostics().size());
		Assert.assertEquals(0, extract.getSnippet().getFatalErrorCount());
		String warning = "EXTRACT/DATE_PART option 'epoch_second' at (l:1 c:7) is recognized as Snowflake-specific grammar.";
		assertDiagnosticAtPosition(extract.getSnippet(), "STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
				ParseDiagnostic.Severity.WARNING, warning, null, 1, 7);
		Assert.assertEquals(warning, findDiagnosticByCodeAndFragmentAndSeverity(extract.getSnippet(),
				"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR", ParseDiagnostic.Severity.WARNING, "epoch_second").message());
		Assert.assertEquals("{SQL={select={1={extract={part_form=KEYWORD, part=epoch_second, source={column={name=ts, table_ref=a}}}, alias=seconds}}, from={table={alias=a, table=events}}}}",
				extract.getSnippet().getSqlAbstractTree().toString());
		Assert.assertEquals("{def_query0={query_dictionary={seconds=[[@10,42:48='seconds',<419>,1:42]]}, table_dictionary={events={ts=[[@5,33:33='a',<419>,1:33]]}}, interface={seconds=[{name=ts, table_ref=a}]}, table_alias={a=events}}}",
				extract.getSnippet().getSymbolTable().toString());
	}

	@Test
	public void dateaddReportsInvalidInnerOrOuterLevelWithoutAffectingValidSibling() {
		String[] queries = {
				"select dateadd(bogus, 1, dateadd(day, 2, a.ts)) as outer_bad, dateadd(hour, 1, a.ts) as good from events a",
				"select dateadd(day, 1, dateadd(bogus, 2, a.ts)) as inner_bad, dateadd(hour, 1, a.ts) as good from events a"
		};
		int[] positions = {15, 31};
		for (int i = 0; i < queries.length; i++) {
			SqlParserAccess parser = new SqlParserAccess(false, false, false);
			parser.executeTheParse(queries[i], "SQL");
			Assert.assertEquals(queries[i], 0, parser.getParser().getNumberOfSyntaxErrors());
			Assert.assertEquals(parser.getAllDiagnostics().toString(), 1, parser.getAllDiagnostics().size());
			assertFatalDiagnosticAtPositionWithFullMessage(parser.getSnippet(), "INVALID_DATETIME_PART",
					"Invalid date/time part bogus for DATEADD at (l:1 c:" + positions[i] + ").",
					"bogus", 1, positions[i]);
			Assert.assertTrue(parser.getSnippet().getSqlAbstractTree().toString().contains(
					"alias=good, datetime_function={function_name=dateadd, level={canonical=HOUR, text=hour, form=KEYWORD}"));
			Assert.assertFalse(parser.getSnippet().getSymbolTable().toString().contains("bogus"));
		}
	}

	@Test
	public void dateaddReportsBothInvalidNestedLevelsAtTheirOwnPositions() {
		String query = "select dateadd(bogus, 1, dateadd(wrong, 2, a.ts)) as shifted from events a";
		SqlParserAccess parser = new SqlParserAccess(false, false, false);
		parser.executeTheParse(query, "SQL");
		Assert.assertEquals(0, parser.getParser().getNumberOfSyntaxErrors());
		Assert.assertEquals(parser.getAllDiagnostics().toString(), 2, parser.getAllDiagnostics().size());
		assertFatalDiagnosticAtPositionWithFullMessage(parser.getSnippet(), "INVALID_DATETIME_PART",
				"Invalid date/time part bogus for DATEADD at (l:1 c:15).", "bogus", 1, 15);
		assertFatalDiagnosticAtPositionWithFullMessage(parser.getSnippet(), "INVALID_DATETIME_PART",
				"Invalid date/time part wrong for DATEADD at (l:1 c:33).", "wrong", 1, 33);
		Assert.assertEquals("{def_query0={query_dictionary={shifted=[[@19,53:59='shifted',<419>,1:53]]}, table_dictionary={events={ts=[[@13,43:43='a',<419>,1:43]]}}, interface={shifted=[{name=ts, table_ref=a}]}, table_alias={a=events}}}",
				parser.getSnippet().getSymbolTable().toString());
	}

	@Test
	public void dateaddReportsInvalidNestedLevelAtTheInnerInvocation() {
		String query = "select dateadd(day, 1, dateadd(bogus, 2, a.ts)) as shifted from events a";
		SqlParserAccess parser = new SqlParserAccess(false, false, false);
		parser.executeTheParse(query, "SQL");
		Assert.assertEquals(query, 0, parser.getParser().getNumberOfSyntaxErrors());
		Assert.assertEquals(parser.getAllDiagnostics().toString(), 1, parser.getAllDiagnostics().size());
		assertFatalDiagnosticAtPositionWithFullMessage(parser.getSnippet(), "INVALID_DATETIME_PART",
				"Invalid date/time part bogus for DATEADD at (l:1 c:31).", "bogus", 1, 31);
		Assert.assertEquals("bogus", findFatalDiagnosticByCodeAndFragment(
				parser.getSnippet(), "INVALID_DATETIME_PART", "bogus").tokenText());
		Assert.assertFalse(parser.getSnippet().getSymbolTable().toString().contains("bogus"));
	}

	@Test
	public void dateaddRetainsNestedExpressionsAndARealYearColumn() {
		final String query = "select coalesce(dateadd(year, a.year + 1, dateadd(day, 2, a.ts)), a.ts) as shifted from events a";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={function={parameters={1={datetime_function={function_name=dateadd, level={canonical=YEAR, text=year, form=KEYWORD}, increment={calc={left={column={name=year, table_ref=a}}, right={literal=1}, operator=+}}, value={datetime_function={function_name=dateadd, level={canonical=DAY, text=day, form=KEYWORD}, increment={literal=2}, value={column={name=ts, table_ref=a}}}}}}, 2={column={name=ts, table_ref=a}}}, function_name=coalesce}, alias=shifted}}, from={table={alias=a, table=events}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[shifted]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{events={year=[[@7,30:30='a',<419>,1:30]], ts=[[@19,58:58='a',<419>,1:58], [@25,66:66='a',<419>,1:66]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={shifted=[[@30,75:81='shifted',<419>,1:75]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={shifted=[[@30,75:81='shifted',<419>,1:75]]}, table_dictionary={events={year=[[@7,30:30='a',<419>,1:30]], ts=[[@19,58:58='a',<419>,1:58], [@25,66:66='a',<419>,1:66]]}}, interface={shifted=[{name=year, table_ref=a}, {name=ts, table_ref=a}]}, table_alias={a=events}}}", extractor.getSymbolTable().toString());
	}

	@Test
	public void dateaddRetainsPredicandSubstitutionInIncrementOnly() {
		final String query = "select dateadd(day, <increment>, a.created_at) as shifted from events a";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=shifted, datetime_function={function_name=dateadd, level={canonical=DAY, text=day, form=KEYWORD}, increment={substitution={name=<increment>, type=predicand}}, value={column={name=created_at, table_ref=a}}}}}, from={table={alias=a, table=events}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[shifted]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<increment>=predicand}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{events={created_at=[[@7,33:33='a',<419>,1:33]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={shifted=[[@12,50:56='shifted',<419>,1:50]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={shifted=[[@12,50:56='shifted',<419>,1:50]]}, table_dictionary={events={created_at=[[@7,33:33='a',<419>,1:33]]}}, interface={shifted=[{name=<increment>, type=predicand}, {name=created_at, table_ref=a}]}, table_alias={a=events}}}", extractor.getSymbolTable().toString());
	}

	@Test
	public void dateaddRetainsPredicandSubstitutionInValueOnly() {
		final String query = "select dateadd(day, a.offset_days, <date value>) as shifted from events a";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=shifted, datetime_function={function_name=dateadd, level={canonical=DAY, text=day, form=KEYWORD}, increment={column={name=offset_days, table_ref=a}}, value={substitution={name=<date value>, type=predicand}}}}}, from={table={alias=a, table=events}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[shifted]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<date value>=predicand}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{events={offset_days=[[@5,20:20='a',<419>,1:20]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={shifted=[[@12,52:58='shifted',<419>,1:52]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={shifted=[[@12,52:58='shifted',<419>,1:52]]}, table_dictionary={events={offset_days=[[@5,20:20='a',<419>,1:20]]}}, interface={shifted=[{name=offset_days, table_ref=a}, {name=<date value>, type=predicand}]}, table_alias={a=events}}}", extractor.getSymbolTable().toString());
	}

	@Test
	public void dateaddExpressionArgumentsRetainPredicandSubstitutions() {
		final String query = "select dateadd(year, <increment>, <date value>) as shifted";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=shifted, datetime_function={function_name=dateadd, level={canonical=YEAR, text=year, form=KEYWORD}, increment={substitution={name=<increment>, type=predicand}}, value={substitution={name=<date value>, type=predicand}}}}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[shifted]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<date value>=predicand, <increment>=predicand}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={shifted=[[@10,51:57='shifted',<419>,1:51]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={shifted=[[@10,51:57='shifted',<419>,1:51]]}, interface={shifted=[{name=<increment>, type=predicand}, {name=<date value>, type=predicand}]}}}", extractor.getSymbolTable().toString());
	}

	@Test
	public void dateaddRetainsQualifiedColumnSubstitutionInIncrementOnly() {
		final String query = "select dateadd(day, a.<increment column>, b.created_at) as shifted from events a join accounts b on a.account_id = b.id";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=shifted, datetime_function={function_name=dateadd, level={canonical=DAY, text=day, form=KEYWORD}, increment={column={substitution={name=<increment column>, type=column}, table_ref=a}}, value={column={name=created_at, table_ref=b}}}}}, from={join={1={table={alias=a, table=events}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[shifted]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<increment column>=column}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{accounts={created_at=[[@9,42:42='b',<419>,1:42]], id=[[@26,115:115='b',<419>,1:115]]}, events={<increment column>=[[@5,20:20='a',<419>,1:20]], account_id=[[@22,100:100='a',<419>,1:100]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={shifted=[[@14,59:65='shifted',<419>,1:59]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={shifted=[[@14,59:65='shifted',<419>,1:59]]}, table_dictionary={accounts={created_at=[[@9,42:42='b',<419>,1:42]], id=[[@26,115:115='b',<419>,1:115]]}, events={<increment column>=[[@5,20:20='a',<419>,1:20]], account_id=[[@22,100:100='a',<419>,1:100]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}], interface={shifted=[{substitution={name=<increment column>, type=column}, table_ref=a}, {name=created_at, table_ref=b}]}, table_alias={a=events, b=accounts}}}", extractor.getSymbolTable().toString());
	}

	@Test
	public void dateaddRetainsQualifiedColumnSubstitutionInValueOnly() {
		final String query = "select dateadd(day, a.offset_days, b.<date column>) as shifted from events a join accounts b on a.account_id = b.id";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=shifted, datetime_function={function_name=dateadd, level={canonical=DAY, text=day, form=KEYWORD}, increment={column={name=offset_days, table_ref=a}}, value={column={substitution={name=<date column>, type=column}, table_ref=b}}}}}, from={join={1={table={alias=a, table=events}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[shifted]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<date column>=column}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{accounts={<date column>=[[@9,35:35='b',<419>,1:35]], id=[[@26,111:111='b',<419>,1:111]]}, events={account_id=[[@22,96:96='a',<419>,1:96]], offset_days=[[@5,20:20='a',<419>,1:20]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={shifted=[[@14,55:61='shifted',<419>,1:55]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={shifted=[[@14,55:61='shifted',<419>,1:55]]}, table_dictionary={accounts={<date column>=[[@9,35:35='b',<419>,1:35]], id=[[@26,111:111='b',<419>,1:111]]}, events={account_id=[[@22,96:96='a',<419>,1:96]], offset_days=[[@5,20:20='a',<419>,1:20]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}], interface={shifted=[{name=offset_days, table_ref=a}, {substitution={name=<date column>, type=column}, table_ref=b}]}, table_alias={a=events, b=accounts}}}", extractor.getSymbolTable().toString());
	}

	@Test
	public void dateaddRetainsQualifiedColumnSubstitutionsAcrossJoinedSources() {
		final String query = "select dateadd(day, a.<increment column>, b.<date column>) as shifted from events a join accounts b on a.account_id = b.id";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=shifted, datetime_function={function_name=dateadd, level={canonical=DAY, text=day, form=KEYWORD}, increment={column={substitution={name=<increment column>, type=column}, table_ref=a}}, value={column={substitution={name=<date column>, type=column}, table_ref=b}}}}}, from={join={1={table={alias=a, table=events}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[shifted]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<increment column>=column, <date column>=column}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{accounts={<date column>=[[@9,42:42='b',<419>,1:42]], id=[[@26,118:118='b',<419>,1:118]]}, events={<increment column>=[[@5,20:20='a',<419>,1:20]], account_id=[[@22,103:103='a',<419>,1:103]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={shifted=[[@14,62:68='shifted',<419>,1:62]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={shifted=[[@14,62:68='shifted',<419>,1:62]]}, table_dictionary={accounts={<date column>=[[@9,42:42='b',<419>,1:42]], id=[[@26,118:118='b',<419>,1:118]]}, events={<increment column>=[[@5,20:20='a',<419>,1:20]], account_id=[[@22,103:103='a',<419>,1:103]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}], interface={shifted=[{substitution={name=<increment column>, type=column}, table_ref=a}, {substitution={name=<date column>, type=column}, table_ref=b}]}, table_alias={a=events, b=accounts}}}", extractor.getSymbolTable().toString());
	}

	@Test
	public void dateaddRetainsQualifiedColumnSubstitutionsInBothArguments() {
		final String query = "select dateadd(day, a.<increment column>, a.<date column>) as shifted from events a";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=shifted, datetime_function={function_name=dateadd, level={canonical=DAY, text=day, form=KEYWORD}, increment={column={substitution={name=<increment column>, type=column}, table_ref=a}}, value={column={substitution={name=<date column>, type=column}, table_ref=a}}}}}, from={table={alias=a, table=events}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[shifted]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<increment column>=column, <date column>=column}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{events={<increment column>=[[@5,20:20='a',<419>,1:20]], <date column>=[[@9,42:42='a',<419>,1:42]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={shifted=[[@14,62:68='shifted',<419>,1:62]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={shifted=[[@14,62:68='shifted',<419>,1:62]]}, table_dictionary={events={<increment column>=[[@5,20:20='a',<419>,1:20]], <date column>=[[@9,42:42='a',<419>,1:42]]}}, interface={shifted=[{substitution={name=<increment column>, type=column}, table_ref=a}, {substitution={name=<date column>, type=column}, table_ref=a}]}, table_alias={a=events}}}", extractor.getSymbolTable().toString());
	}

	@Test
	public void dateaddKeepsMixedSubstitutionKindsAcrossArguments() {
		final String query = "select dateadd(day, <increment>, a.<date column>) as shifted from events a";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=shifted, datetime_function={function_name=dateadd, level={canonical=DAY, text=day, form=KEYWORD}, increment={substitution={name=<increment>, type=predicand}}, value={column={substitution={name=<date column>, type=column}, table_ref=a}}}}}, from={table={alias=a, table=events}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[shifted]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<date column>=column, <increment>=predicand}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{events={<date column>=[[@7,33:33='a',<419>,1:33]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={shifted=[[@12,53:59='shifted',<419>,1:53]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={shifted=[[@12,53:59='shifted',<419>,1:53]]}, table_dictionary={events={<date column>=[[@7,33:33='a',<419>,1:33]]}}, interface={shifted=[{name=<increment>, type=predicand}, {substitution={name=<date column>, type=column}, table_ref=a}]}, table_alias={a=events}}}", extractor.getSymbolTable().toString());
	}

	@Test
	public void dateaddKeepsReverseMixedSubstitutionKindsAcrossArguments() {
		final String query = "select dateadd(day, a.<increment column>, <date value>) as shifted from events a";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=shifted, datetime_function={function_name=dateadd, level={canonical=DAY, text=day, form=KEYWORD}, increment={column={substitution={name=<increment column>, type=column}, table_ref=a}}, value={substitution={name=<date value>, type=predicand}}}}}, from={table={alias=a, table=events}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[shifted]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<date value>=predicand, <increment column>=column}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{events={<increment column>=[[@5,20:20='a',<419>,1:20]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={shifted=[[@12,59:65='shifted',<419>,1:59]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={shifted=[[@12,59:65='shifted',<419>,1:59]]}, table_dictionary={events={<increment column>=[[@5,20:20='a',<419>,1:20]]}}, interface={shifted=[{substitution={name=<increment column>, type=column}, table_ref=a}, {name=<date value>, type=predicand}]}, table_alias={a=events}}}", extractor.getSymbolTable().toString());
	}

	@Test
	public void dateaddRetainsPredicandSubstitutionsInsideIncrementFormula() {
		final String query = "select dateadd(day, a.offset_days + <delta> + <extra>, a.created_at) as shifted from events a";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=shifted, datetime_function={function_name=dateadd, level={canonical=DAY, text=day, form=KEYWORD}, increment={calc={left={calc={left={column={name=offset_days, table_ref=a}}, right={substitution={name=<delta>, type=predicand}}, operator=+}}, right={substitution={name=<extra>, type=predicand}}, operator=+}}, value={column={name=created_at, table_ref=a}}}}}, from={table={alias=a, table=events}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[shifted]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<extra>=predicand, <delta>=predicand}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{events={created_at=[[@13,55:55='a',<419>,1:55]], offset_days=[[@5,20:20='a',<419>,1:20]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={shifted=[[@18,72:78='shifted',<419>,1:72]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={shifted=[[@18,72:78='shifted',<419>,1:72]]}, table_dictionary={events={created_at=[[@13,55:55='a',<419>,1:55]], offset_days=[[@5,20:20='a',<419>,1:20]]}}, interface={shifted=[{name=offset_days, table_ref=a}, {name=<delta>, type=predicand}, {name=<extra>, type=predicand}, {name=created_at, table_ref=a}]}, table_alias={a=events}}}", extractor.getSymbolTable().toString());
	}

	@Test
	public void dateaddRetainsColumnSubstitutionsInsideIncrementFormula() {
		final String query = "select dateadd(day, a.offset_days + a.<delta column> + a.<extra column>, a.created_at) as shifted from events a";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=shifted, datetime_function={function_name=dateadd, level={canonical=DAY, text=day, form=KEYWORD}, increment={calc={left={calc={left={column={name=offset_days, table_ref=a}}, right={column={substitution={name=<delta column>, type=column}, table_ref=a}}, operator=+}}, right={column={substitution={name=<extra column>, type=column}, table_ref=a}}, operator=+}}, value={column={name=created_at, table_ref=a}}}}}, from={table={alias=a, table=events}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[shifted]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<delta column>=column, <extra column>=column}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{events={<delta column>=[[@9,36:36='a',<419>,1:36]], created_at=[[@17,73:73='a',<419>,1:73]], <extra column>=[[@13,55:55='a',<419>,1:55]], offset_days=[[@5,20:20='a',<419>,1:20]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={shifted=[[@22,90:96='shifted',<419>,1:90]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={shifted=[[@22,90:96='shifted',<419>,1:90]]}, table_dictionary={events={<delta column>=[[@9,36:36='a',<419>,1:36]], created_at=[[@17,73:73='a',<419>,1:73]], <extra column>=[[@13,55:55='a',<419>,1:55]], offset_days=[[@5,20:20='a',<419>,1:20]]}}, interface={shifted=[{name=offset_days, table_ref=a}, {substitution={name=<delta column>, type=column}, table_ref=a}, {substitution={name=<extra column>, type=column}, table_ref=a}, {name=created_at, table_ref=a}]}, table_alias={a=events}}}", extractor.getSymbolTable().toString());
	}

	@Test
	public void dateaddRetainsPredicandSubstitutionsInsideValueFormula() {
		final String query = "select dateadd(day, 2, a.created_at + (<days> + <extra days>)) as shifted from events a";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=shifted, datetime_function={function_name=dateadd, level={canonical=DAY, text=day, form=KEYWORD}, increment={literal=2}, value={calc={left={column={name=created_at, table_ref=a}}, right={parentheses={calc={left={substitution={name=<days>, type=predicand}}, right={substitution={name=<extra days>, type=predicand}}, operator=+}}}, operator=+}}}}}, from={table={alias=a, table=events}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[shifted]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<extra days>=predicand, <days>=predicand}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{events={created_at=[[@7,23:23='a',<419>,1:23]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={shifted=[[@18,66:72='shifted',<419>,1:66]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={shifted=[[@18,66:72='shifted',<419>,1:66]]}, table_dictionary={events={created_at=[[@7,23:23='a',<419>,1:23]]}}, interface={shifted=[{name=created_at, table_ref=a}, {name=<days>, type=predicand}, {name=<extra days>, type=predicand}]}, table_alias={a=events}}}", extractor.getSymbolTable().toString());
	}

	@Test
	public void dateaddRetainsColumnSubstitutionsInsideValueFormula() {
		final String query = "select dateadd(day, 2, a.created_at + (a.<days column> + a.<extra days column>)) as shifted from events a";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=shifted, datetime_function={function_name=dateadd, level={canonical=DAY, text=day, form=KEYWORD}, increment={literal=2}, value={calc={left={column={name=created_at, table_ref=a}}, right={parentheses={calc={left={column={substitution={name=<days column>, type=column}, table_ref=a}}, right={column={substitution={name=<extra days column>, type=column}, table_ref=a}}, operator=+}}}, operator=+}}}}}, from={table={alias=a, table=events}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[shifted]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<days column>=column, <extra days column>=column}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{events={created_at=[[@7,23:23='a',<419>,1:23]], <days column>=[[@12,39:39='a',<419>,1:39]], <extra days column>=[[@16,57:57='a',<419>,1:57]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={shifted=[[@22,84:90='shifted',<419>,1:84]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={shifted=[[@22,84:90='shifted',<419>,1:84]]}, table_dictionary={events={created_at=[[@7,23:23='a',<419>,1:23]], <days column>=[[@12,39:39='a',<419>,1:39]], <extra days column>=[[@16,57:57='a',<419>,1:57]]}}, interface={shifted=[{name=created_at, table_ref=a}, {substitution={name=<days column>, type=column}, table_ref=a}, {substitution={name=<extra days column>, type=column}, table_ref=a}]}, table_alias={a=events}}}", extractor.getSymbolTable().toString());
	}

	@Test
	public void dateaddRetainsMixedSubstitutionsInsideBothArgumentFormulas() {
		final String query = "select dateadd(day, a.offset_days + <delta> + a.<delta column>, a.created_at + (<days> + a.<days column>)) as shifted from events a";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=shifted, datetime_function={function_name=dateadd, level={canonical=DAY, text=day, form=KEYWORD}, increment={calc={left={calc={left={column={name=offset_days, table_ref=a}}, right={substitution={name=<delta>, type=predicand}}, operator=+}}, right={column={substitution={name=<delta column>, type=column}, table_ref=a}}, operator=+}}, value={calc={left={column={name=created_at, table_ref=a}}, right={parentheses={calc={left={substitution={name=<days>, type=predicand}}, right={column={substitution={name=<days column>, type=column}, table_ref=a}}, operator=+}}}, operator=+}}}}}, from={table={alias=a, table=events}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[shifted]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<delta column>=column, <days>=predicand, <days column>=column, <delta>=predicand}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{events={<delta column>=[[@11,46:46='a',<419>,1:46]], created_at=[[@15,64:64='a',<419>,1:64]], offset_days=[[@5,20:20='a',<419>,1:20]], <days column>=[[@22,89:89='a',<419>,1:89]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={shifted=[[@28,110:116='shifted',<419>,1:110]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={shifted=[[@28,110:116='shifted',<419>,1:110]]}, table_dictionary={events={<delta column>=[[@11,46:46='a',<419>,1:46]], created_at=[[@15,64:64='a',<419>,1:64]], offset_days=[[@5,20:20='a',<419>,1:20]], <days column>=[[@22,89:89='a',<419>,1:89]]}}, interface={shifted=[{name=offset_days, table_ref=a}, {name=<delta>, type=predicand}, {substitution={name=<delta column>, type=column}, table_ref=a}, {name=created_at, table_ref=a}, {name=<days>, type=predicand}, {substitution={name=<days column>, type=column}, table_ref=a}]}, table_alias={a=events}}}", extractor.getSymbolTable().toString());
	}

	@Test
	public void dateaddKeepsMixedSubstitutionKindsInsideIncrementFormula() {
		final String query = "select dateadd(day, a.<increment column> + <extra>, a.ts) as shifted from events a";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=shifted, datetime_function={function_name=dateadd, level={canonical=DAY, text=day, form=KEYWORD}, increment={calc={left={column={substitution={name=<increment column>, type=column}, table_ref=a}}, right={substitution={name=<extra>, type=predicand}}, operator=+}}, value={column={name=ts, table_ref=a}}}}}, from={table={alias=a, table=events}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[shifted]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<increment column>=column, <extra>=predicand}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{events={<increment column>=[[@5,20:20='a',<419>,1:20]], ts=[[@11,52:52='a',<419>,1:52]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={shifted=[[@16,61:67='shifted',<419>,1:61]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={shifted=[[@16,61:67='shifted',<419>,1:61]]}, table_dictionary={events={<increment column>=[[@5,20:20='a',<419>,1:20]], ts=[[@11,52:52='a',<419>,1:52]]}}, interface={shifted=[{substitution={name=<increment column>, type=column}, table_ref=a}, {name=<extra>, type=predicand}, {name=ts, table_ref=a}]}, table_alias={a=events}}}", extractor.getSymbolTable().toString());
	}

	@Test
	public void qualifiedDateaddRemainsAGenericFunction() {
		final String query = "select custom.DaTeAdD(a.part, 1, a.ts) as shifted from events a";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={function={schema=custom, parameters={1={column={name=part, table_ref=a}}, 2={literal=1}, 3={column={name=ts, table_ref=a}}}, function_name=DaTeAdD}, alias=shifted}}, from={table={alias=a, table=events}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[shifted]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{events={part=[[@5,22:22='a',<419>,1:22]], ts=[[@11,33:33='a',<419>,1:33]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={shifted=[[@16,42:48='shifted',<419>,1:42]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={shifted=[[@16,42:48='shifted',<419>,1:42]]}, table_dictionary={events={part=[[@5,22:22='a',<419>,1:22]], ts=[[@11,33:33='a',<419>,1:33]]}}, interface={shifted=[{name=part, table_ref=a}, {name=ts, table_ref=a}]}, table_alias={a=events}}}", extractor.getSymbolTable().toString());
	}
}
