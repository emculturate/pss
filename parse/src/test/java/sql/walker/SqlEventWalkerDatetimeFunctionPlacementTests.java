package sql.walker;

import org.junit.Assert;
import org.junit.Test;

import access.SqlParserAccess;
import errorhandling.ParseDiagnostic;
import sql.SQLSelectParserParser;

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
	public void dateaddReportsInvalidLevelsAtTheirSourceWithoutCollectingColumns() {
		String[] invalidParts = {"bogus", "'bogus'", "''", "'engine''part'", "epoch"};
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
