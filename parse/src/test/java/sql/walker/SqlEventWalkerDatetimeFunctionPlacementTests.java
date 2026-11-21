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

	private int invocationOffset(String sql, String invocationName) {
		int offset = sql.toLowerCase().indexOf(invocationName.toLowerCase() + "(");
		Assert.assertTrue("Expected invocation " + invocationName + " in query: " + sql, offset >= 0);
		return offset;
	}

	private void assertSnowflakeGrammarWarningAt(SqlParserAccess parser, String ruleName, int charPosition) {
		String warning = ruleName + " at (l:1 c:" + charPosition + ") is recognized as Snowflake-specific grammar.";
		assertDiagnosticAtPosition(parser.getSnippet(), "STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
				ParseDiagnostic.Severity.WARNING, warning, null, 1, charPosition);
	}

	@Test
	public void timeaddAndTimestampaddRetainTheirOwnInvocationsAndExpressionArguments() {
		final String query = "select timeadd(hr, a.offset_hours, a.start_time) as shifted_time, timestampadd(day, a.offset_days + 1, a.created_at) as shifted_timestamp from events a";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=shifted_time, datetime_function={function_name=timeadd, level={canonical=HOUR, text=hr, form=IDENTIFIER}, increment={column={name=offset_hours, table_ref=a}}, value={column={name=start_time, table_ref=a}}}}, 2={alias=shifted_timestamp, datetime_function={function_name=timestampadd, level={canonical=DAY, text=day, form=KEYWORD}, increment={calc={left={column={name=offset_days, table_ref=a}}, right={literal=1}, operator=+}}, value={column={name=created_at, table_ref=a}}}}}, from={table={alias=a, table=events}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[shifted_time, shifted_timestamp]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{events={start_time=[[@9,35:35='a',<459>,1:35]], created_at=[[@26,103:103='a',<459>,1:103]], offset_hours=[[@5,19:19='a',<459>,1:19]], offset_days=[[@20,84:84='a',<459>,1:84]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={shifted_timestamp=[[@31,120:136='shifted_timestamp',<459>,1:120]], shifted_time=[[@14,52:63='shifted_time',<459>,1:52]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={shifted_time=[[@14,52:63='shifted_time',<459>,1:52]], shifted_timestamp=[[@31,120:136='shifted_timestamp',<459>,1:120]]}, table_dictionary={events={start_time=[[@9,35:35='a',<459>,1:35]], created_at=[[@26,103:103='a',<459>,1:103]], offset_hours=[[@5,19:19='a',<459>,1:19]], offset_days=[[@20,84:84='a',<459>,1:84]]}}, interface={shifted_time=[{name=offset_hours, table_ref=a}, {name=start_time, table_ref=a}], shifted_timestamp=[{name=offset_days, table_ref=a}, {name=created_at, table_ref=a}]}, table_alias={a=events}}}", extractor.getSymbolTable().toString());
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
			Assert.assertEquals(query + ": " + parser.getAllDiagnostics(), 2, parser.getAllDiagnostics().size());
			assertSnowflakeGrammarWarningAt(parser, "snowflake_datetime_add_expression",
					invocationOffset(query, invocation));
			assertFatalDiagnosticAtPositionWithFullMessage(parser.getSnippet(), "INVALID_DATETIME_PART",
					"Invalid date/time part bogus for " + invocation + " at (l:1 c:" + position + ").",
					"bogus", 1, position);
			Assert.assertEquals("bogus", findFatalDiagnosticByCodeAndFragment(
					parser.getSnippet(), "INVALID_DATETIME_PART", "bogus").tokenText());
			Assert.assertFalse(parser.getSnippet().getSymbolTable().toString().contains("bogus"));
		}
	}

	@Test
	public void datediffHappyPathBaselineRetainsLeftRightLineageWithNaturalContext() {
		final String query = "select a.id as event_id, datediff(day, a.created_at, a.resolved_at) as days_open, coalesce(a.status, 'OPEN') as status_label from events a where a.active = 1 and a.resolved_at is not null order by days_open";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=id, table_ref=a}, alias=event_id}, 2={alias=days_open, datetime_function={function_name=datediff, level={canonical=DAY, text=day, form=KEYWORD}, left={column={name=created_at, table_ref=a}}, right={column={name=resolved_at, table_ref=a}}}}, 3={function={parameters={1={column={name=status, table_ref=a}}, 2={literal='OPEN'}}, function_name=coalesce}, alias=status_label}}, orderby={1={null_order=null, predicand={column={name=days_open, table_ref=null}}, sort_order=ASC}}, from={table={alias=a, table=events}}, where={and={1={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}, 2={condition={left={column={name=resolved_at, table_ref=a}}, operator=is not null}}}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[status_label, event_id, days_open]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{events={resolved_at=[[@15,53:53='a',<459>,1:53], [@42,162:162='a',<459>,1:162]], active=[[@36,145:145='a',<459>,1:145]], created_at=[[@11,39:39='a',<459>,1:39]], id=[[@1,7:7='a',<459>,1:7]], status=[[@24,91:91='a',<459>,1:91]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={status_label=[[@31,112:123='status_label',<459>,1:112]], days_open=[[@20,71:79='days_open',<459>,1:71], [@50,197:205='days_open',<459>,1:197]], event_id=[[@5,15:22='event_id',<459>,1:15]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={status_label=[[@31,112:123='status_label',<459>,1:112]], event_id=[[@5,15:22='event_id',<459>,1:15]], days_open=[[@20,71:79='days_open',<459>,1:71], [@50,197:205='days_open',<459>,1:197]]}, table_dictionary={events={resolved_at=[[@15,53:53='a',<459>,1:53], [@42,162:162='a',<459>,1:162]], active=[[@36,145:145='a',<459>,1:145]], created_at=[[@11,39:39='a',<459>,1:39]], id=[[@1,7:7='a',<459>,1:7]], status=[[@24,91:91='a',<459>,1:91]]}}, ordered_by=[{name=days_open, table_ref=query0}], filters=[{name=active, table_ref=a}, {name=resolved_at, table_ref=a}], interface={status_label=[{name=status, table_ref=a}], event_id=[{name=id, table_ref=a}], days_open=[{name=created_at, table_ref=a}, {name=resolved_at, table_ref=a}]}, table_alias={a=events}}}", extractor.getSymbolTable().toString());

		final String expectedDatediffBaselineDefQuery0OrderedBy_0 = "[{name=days_open, table_ref=query0}]";
		assertSymbolTreePathEquals(extractor.getSymbolTable(), "def_query0.ordered_by", expectedDatediffBaselineDefQuery0OrderedBy_0);
		final String expectedDatediffBaselineDefQuery0Interface_1 = "{status_label=[{name=status, table_ref=a}], event_id=[{name=id, table_ref=a}], days_open=[{name=created_at, table_ref=a}, {name=resolved_at, table_ref=a}]}";
		assertSymbolTreePathEquals(extractor.getSymbolTable(), "def_query0.interface", expectedDatediffBaselineDefQuery0Interface_1);
	}

	@Test
	public void timediffHappyPathBaselineRetainsLeftRightLineageWithNaturalContext() {
		final String query = "select u.id as event_id, timediff(minute, u.started_at, u.ended_at) as elapsed_minutes, case when u.ended_at is null then 0 else 1 end as has_end from usage_events u where u.started_at is not null";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=id, table_ref=u}, alias=event_id}, 2={alias=elapsed_minutes, datetime_function={function_name=timediff, level={canonical=MINUTE, text=minute, form=KEYWORD}, left={column={name=started_at, table_ref=u}}, right={column={name=ended_at, table_ref=u}}}}, 3={alias=has_end, case={clauses={1={then={literal=0}, when={condition={left={column={name=ended_at, table_ref=u}}, operator=is null}}}}, else={literal=1}}}}, from={table={alias=u, table=usage_events}}, where={condition={left={column={name=started_at, table_ref=u}}, operator=is not null}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[event_id, elapsed_minutes, has_end]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{usage_events={started_at=[[@11,42:42='u',<459>,1:42], [@40,172:172='u',<459>,1:172]], id=[[@1,7:7='u',<459>,1:7]], ended_at=[[@15,56:56='u',<459>,1:56], [@24,98:98='u',<459>,1:98]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={elapsed_minutes=[[@20,71:85='elapsed_minutes',<459>,1:71]], has_end=[[@35,138:144='has_end',<459>,1:138]], event_id=[[@5,15:22='event_id',<459>,1:15]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={event_id=[[@5,15:22='event_id',<459>,1:15]], elapsed_minutes=[[@20,71:85='elapsed_minutes',<459>,1:71]], has_end=[[@35,138:144='has_end',<459>,1:138]]}, table_dictionary={usage_events={started_at=[[@11,42:42='u',<459>,1:42], [@40,172:172='u',<459>,1:172]], id=[[@1,7:7='u',<459>,1:7]], ended_at=[[@15,56:56='u',<459>,1:56], [@24,98:98='u',<459>,1:98]]}}, filters=[{name=started_at, table_ref=u}], interface={event_id=[{name=id, table_ref=u}], elapsed_minutes=[{name=started_at, table_ref=u}, {name=ended_at, table_ref=u}], has_end=[{name=ended_at, table_ref=u}]}, table_alias={u=usage_events}}}", extractor.getSymbolTable().toString());

		final String expectedTimediffBaselineDefQuery0Filters_0 = "[{name=started_at, table_ref=u}]";
		assertSymbolTreePathEquals(extractor.getSymbolTable(), "def_query0.filters", expectedTimediffBaselineDefQuery0Filters_0);
		final String expectedTimediffBaselineDefQuery0Interface_1 = "{event_id=[{name=id, table_ref=u}], elapsed_minutes=[{name=started_at, table_ref=u}, {name=ended_at, table_ref=u}], has_end=[{name=ended_at, table_ref=u}]}";
		assertSymbolTreePathEquals(extractor.getSymbolTable(), "def_query0.interface", expectedTimediffBaselineDefQuery0Interface_1);
	}

	@Test
	public void timestampdiffHappyPathBaselineRetainsLeftRightLineageWithNaturalContext() {
		final String query = "with base as (select e.id, e.created_at, e.updated_at, e.priority from events e where e.updated_at is not null) select b.id, timestampdiff(hour, b.created_at, b.updated_at) as age_hours, b.priority from base b order by age_hours";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={with={1={cte={select={1={column={name=id, table_ref=e}}, 2={column={name=created_at, table_ref=e}}, 3={column={name=updated_at, table_ref=e}}, 4={column={name=priority, table_ref=e}}}, from={table={alias=e, table=events}}, where={condition={left={column={name=updated_at, table_ref=e}}, operator=is not null}}}, alias=base}}, query={select={1={column={name=id, table_ref=b}}, 2={alias=age_hours, datetime_function={function_name=timestampdiff, level={canonical=HOUR, text=hour, form=KEYWORD}, left={column={name=created_at, table_ref=b}}, right={column={name=updated_at, table_ref=b}}}}, 3={column={name=priority, table_ref=b}}}, orderby={1={null_order=null, predicand={column={name=age_hours, table_ref=null}}, sort_order=ASC}}, from={table={alias=b, table=base}}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[age_hours, id, priority]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{events={updated_at=[[@13,41:41='e',<459>,1:41], [@24,86:86='e',<459>,1:86]], created_at=[[@9,27:27='e',<459>,1:27]], id=[[@5,21:21='e',<459>,1:21]], priority=[[@17,55:55='e',<459>,1:55]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={created_at=[[@11,29:38='created_at',<459>,1:29], [@40,145:145='b',<459>,1:145]], id=[[@7,23:24='id',<459>,1:23], [@32,119:119='b',<459>,1:119]], updated_at=[[@15,43:52='updated_at',<459>,1:43], [@44,159:159='b',<459>,1:159]], priority=[[@19,57:64='priority',<459>,1:57], [@51,187:187='b',<459>,1:187]]}, query1={priority=[[@53,189:196='priority',<459>,1:189]], age_hours=[[@49,176:184='age_hours',<459>,1:176], [@59,219:227='age_hours',<459>,1:219]], id=[[@34,121:122='id',<459>,1:121]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query1={context_list={base=query0, b=query0}, query_dictionary={age_hours=[[@49,176:184='age_hours',<459>,1:176], [@59,219:227='age_hours',<459>,1:219]], id=[[@34,121:122='id',<459>,1:121]], priority=[[@53,189:196='priority',<459>,1:189]]}, def_query0={query_dictionary={updated_at=[[@15,43:52='updated_at',<459>,1:43], [@44,159:159='b',<459>,1:159]], created_at=[[@11,29:38='created_at',<459>,1:29], [@40,145:145='b',<459>,1:145]], id=[[@7,23:24='id',<459>,1:23], [@32,119:119='b',<459>,1:119]], priority=[[@19,57:64='priority',<459>,1:57], [@51,187:187='b',<459>,1:187]]}, table_dictionary={events={updated_at=[[@13,41:41='e',<459>,1:41], [@24,86:86='e',<459>,1:86]], created_at=[[@9,27:27='e',<459>,1:27]], id=[[@5,21:21='e',<459>,1:21]], priority=[[@17,55:55='e',<459>,1:55]]}}, filters=[{name=updated_at, table_ref=e}], interface={updated_at=[{name=updated_at, table_ref=e}], created_at=[{name=created_at, table_ref=e}], id=[{name=id, table_ref=e}], priority=[{name=priority, table_ref=e}]}, table_alias={e=events}}, ordered_by=[{name=age_hours, table_ref=query1}], interface={age_hours=[{name=created_at, table_ref=b}, {name=updated_at, table_ref=b}], id=[{name=id, table_ref=b}], priority=[{name=priority, table_ref=b}]}, table_alias={b=query0, base=query0}}}", extractor.getSymbolTable().toString());

		final String expectedTimestampdiffBaselineDefQuery1DefQuery0Interface_0 = "{updated_at=[{name=updated_at, table_ref=e}], created_at=[{name=created_at, table_ref=e}], id=[{name=id, table_ref=e}], priority=[{name=priority, table_ref=e}]}";
		assertSymbolTreePathEquals(extractor.getSymbolTable(), "def_query1.def_query0.interface", expectedTimestampdiffBaselineDefQuery1DefQuery0Interface_0);
		final String expectedTimestampdiffBaselineDefQuery1Interface_1 = "{age_hours=[{name=created_at, table_ref=b}, {name=updated_at, table_ref=b}], id=[{name=id, table_ref=b}], priority=[{name=priority, table_ref=b}]}";
		assertSymbolTreePathEquals(extractor.getSymbolTable(), "def_query1.interface", expectedTimestampdiffBaselineDefQuery1Interface_1);
	}

	@Test
	public void timediffAliasSampleInHavingWithPredicandSubstitutionV0Test() {
		final String query = "select u.user_id, count(*) as c from usage_events u group by u.user_id having timediff(minute, min(u.started_at), <end time>) > 0";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=user_id, table_ref=u}}, 2={function={function_name=COUNT, qualifier=null, parameters=*}, alias=c}}, having={condition={left={datetime_function={function_name=timediff, level={canonical=MINUTE, text=minute, form=KEYWORD}, left={function={function_name=min, qualifier=null, parameters={column={name=started_at, table_ref=u}}}}, right={substitution={name=<end time>, type=predicand}}}}, right={literal=0}, operator=>}}, from={table={alias=u, table=usage_events}}, groupby={1={column={name=user_id, table_ref=u}}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[c, user_id]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<end time>=predicand}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{usage_events={user_id=[[@1,7:7='u',<459>,1:7], [@16,61:61='u',<459>,1:61]], started_at=[[@26,99:99='u',<459>,1:99]], *=[[@7,24:24='*',<291>,1:24]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={user_id=[[@3,9:15='user_id',<459>,1:9]], c=[[@10,30:30='c',<459>,1:30]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={c=[[@10,30:30='c',<459>,1:30]], user_id=[[@3,9:15='user_id',<459>,1:9]]}, table_dictionary={usage_events={user_id=[[@1,7:7='u',<459>,1:7], [@16,61:61='u',<459>,1:61]], started_at=[[@26,99:99='u',<459>,1:99]], *=[[@7,24:24='*',<291>,1:24]]}}, grouped_by=[{name=user_id, table_ref=u}], filters=[{name=started_at, table_ref=u}, {name=<end time>, type=predicand}], interface={c=[], user_id=[{name=user_id, table_ref=u}]}, table_alias={u=usage_events}}}", extractor.getSymbolTable().toString());
		final String expectedTimediffHavingDefQuery0Filters_0 = "[{name=started_at, table_ref=u}, {name=<end time>, type=predicand}]";
		assertSymbolTreePathEquals(extractor.getSymbolTable(), "def_query0.filters", expectedTimediffHavingDefQuery0Filters_0);
	}

	@Test
	public void timestampdiffAliasSampleOverIntersectCteWithPredicandSubstitutionV0Test() {
		final String query = "with base as (select e.id, e.created_at, e.updated_at from events e intersect select a.id, a.created_at, a.updated_at from archived_events a) select b.id, timestampdiff(hour, b.created_at, <right ts>) as age_hours from base b";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={with={1={cte={intersect={1={select={1={column={name=id, table_ref=e}}, 2={column={name=created_at, table_ref=e}}, 3={column={name=updated_at, table_ref=e}}}, from={table={alias=e, table=events}}}, 2={intersect={qualifier=null, operator=intersect}}, 3={select={1={column={name=id, table_ref=a}}, 2={column={name=created_at, table_ref=a}}, 3={column={name=updated_at, table_ref=a}}}, from={table={alias=a, table=archived_events}}}}}, alias=base}}, query={select={1={column={name=id, table_ref=b}}, 2={alias=age_hours, datetime_function={function_name=timestampdiff, level={canonical=HOUR, text=hour, form=KEYWORD}, left={column={name=created_at, table_ref=b}}, right={substitution={name=<right ts>, type=predicand}}}}}, from={table={alias=b, table=base}}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[age_hours, id]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<right ts>=predicand}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{archived_events={updated_at=[[@29,105:105='a',<459>,1:105]], created_at=[[@25,91:91='a',<459>,1:91]], id=[[@21,85:85='a',<459>,1:85]]}, events={updated_at=[[@13,41:41='e',<459>,1:41]], created_at=[[@9,27:27='e',<459>,1:27]], id=[[@5,21:21='e',<459>,1:21]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{intersect2={created_at=[[@45,175:175='b',<459>,1:175]], id=[[@37,149:149='b',<459>,1:149]]}, query0={updated_at=[[@15,43:52='updated_at',<459>,1:43]], created_at=[[@11,29:38='created_at',<459>,1:29]], id=[[@7,23:24='id',<459>,1:23]]}, query1={updated_at=[[@31,107:116='updated_at',<459>,1:107]], created_at=[[@27,93:102='created_at',<459>,1:93]], id=[[@23,87:88='id',<459>,1:87]]}, query3={age_hours=[[@52,204:212='age_hours',<459>,1:204]], id=[[@39,151:152='id',<459>,1:151]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query3={context_list={base=intersect2, b=intersect2}, query_dictionary={age_hours=[[@52,204:212='age_hours',<459>,1:204]], id=[[@39,151:152='id',<459>,1:151]]}, def_intersect2={query_dictionary={created_at=[[@45,175:175='b',<459>,1:175]], id=[[@37,149:149='b',<459>,1:149]]}, def_query1={query_dictionary={updated_at=[[@31,107:116='updated_at',<459>,1:107]], created_at=[[@27,93:102='created_at',<459>,1:93]], id=[[@23,87:88='id',<459>,1:87]]}, table_dictionary={archived_events={updated_at=[[@29,105:105='a',<459>,1:105]], created_at=[[@25,91:91='a',<459>,1:91]], id=[[@21,85:85='a',<459>,1:85]]}}, setop=INTERSECTION, interface={updated_at=[{name=updated_at, table_ref=a}], created_at=[{name=created_at, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=archived_events}}, def_query0={query_dictionary={updated_at=[[@15,43:52='updated_at',<459>,1:43]], created_at=[[@11,29:38='created_at',<459>,1:29]], id=[[@7,23:24='id',<459>,1:23]]}, table_dictionary={events={updated_at=[[@13,41:41='e',<459>,1:41]], created_at=[[@9,27:27='e',<459>,1:27]], id=[[@5,21:21='e',<459>,1:21]]}}, interface={updated_at=[{name=updated_at, table_ref=e}], created_at=[{name=created_at, table_ref=e}], id=[{name=id, table_ref=e}]}, table_alias={e=events}}, interface={updated_at=query_column, created_at=query_column, id=query_column}}, interface={age_hours=[{name=created_at, table_ref=b}, {name=<right ts>, type=predicand}], id=[{name=id, table_ref=b}]}, table_alias={b=intersect2, base=intersect2}}}", extractor.getSymbolTable().toString());
		final String expectedTimestampdiffCteIntersectDefQuery3Interface_0 = "{age_hours=[{name=created_at, table_ref=b}, {name=<right ts>, type=predicand}], id=[{name=id, table_ref=b}]}";
		assertSymbolTreePathEquals(extractor.getSymbolTable(), "def_query3.interface", expectedTimestampdiffCteIntersectDefQuery3Interface_0);
	}

	@Test
	public void timediffRejectsInvalidLevelAtSourcePositionV0Test() {
		String query = "select timediff(badpart, u.started_at, u.ended_at) as elapsed from usage_events u";
		SqlParserAccess parser = new SqlParserAccess(false, false, false);
		parser.executeTheParse(query, "SQL");
		Assert.assertEquals(0, parser.getParser().getNumberOfSyntaxErrors());
		Assert.assertEquals(parser.getAllDiagnostics().toString(), 2, parser.getAllDiagnostics().size());
		assertSnowflakeGrammarWarningAt(parser, "snowflake_datetime_diff_expression",
				invocationOffset(query, "timediff"));
		assertFatalDiagnosticAtPositionWithFullMessage(parser.getSnippet(), "INVALID_DATETIME_PART",
				"Invalid date/time part badpart for TIMEDIFF at (l:1 c:16).",
				"badpart", 1, 16);
		Assert.assertFalse(parser.getSnippet().getSymbolTable().toString().contains("badpart"));
	}

	@Test
	public void timestampdiffRejectsInvalidLevelAtSourcePositionV0Test() {
		String query = "select timestampdiff(badpart, a.created_at, a.updated_at) as age_hours from events a";
		SqlParserAccess parser = new SqlParserAccess(false, false, false);
		parser.executeTheParse(query, "SQL");
		Assert.assertEquals(0, parser.getParser().getNumberOfSyntaxErrors());
		Assert.assertEquals(parser.getAllDiagnostics().toString(), 2, parser.getAllDiagnostics().size());
		assertSnowflakeGrammarWarningAt(parser, "snowflake_datetime_diff_expression",
				invocationOffset(query, "timestampdiff"));
		assertFatalDiagnosticAtPositionWithFullMessage(parser.getSnippet(), "INVALID_DATETIME_PART",
				"Invalid date/time part badpart for TIMESTAMPDIFF at (l:1 c:21).",
				"badpart", 1, 21);
		Assert.assertFalse(parser.getSnippet().getSymbolTable().toString().contains("badpart"));
	}

	@Test
	public void extractFromFormNowBuildsDatetimeFunctionNode() {
		final String query = "select extract(year from a.created_at) as extracted_year from events a";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=extracted_year, datetime_function={function_name=extract, part_form=KEYWORD, part=year, source={column={name=created_at, table_ref=a}}}}}, from={table={alias=a, table=events}}}}", extractor.getAsTree().toString());
	}

	@Test
	public void datePartFromFormNowBuildsDatetimeFunctionNode() {
		final String query = "select date_part(month from a.created_at) as extracted_month from events a";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=extracted_month, datetime_function={function_name=date_part, part_form=KEYWORD, part=month, source={column={name=created_at, table_ref=a}}}}}, from={table={alias=a, table=events}}}}", extractor.getAsTree().toString());
	}

	@Test
	public void dateTruncNowBuildsDatetimeFunctionNode() {
		final String query = "select date_trunc(day, a.created_at) as truncated_day from events a";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=truncated_day, datetime_function={function_name=date_trunc, level={canonical=DAY, text=day, form=KEYWORD}, value={column={name=created_at, table_ref=a}}}}}, from={table={alias=a, table=events}}}}", extractor.getAsTree().toString());
	}

	@Test
	public void truncDatetimeVariantNowBuildsDatetimeFunctionNode() {
		final String query = "select trunc(a.created_at, day) as truncated_day from events a";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=truncated_day, datetime_function={function_name=trunc, level={canonical=DAY, text=day, form=KEYWORD}, value={column={name=created_at, table_ref=a}}}}}, from={table={alias=a, table=events}}}}", extractor.getAsTree().toString());
	}

	@Test
	public void datediffSetAUnqualifiedOneSourceOperandsResolveNormally() {
		final String query = "select datediff(day, created_at, resolved_at) as delta_days from events";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=delta_days, datetime_function={function_name=datediff, level={canonical=DAY, text=day, form=KEYWORD}, left={column={name=created_at, table_ref=null}}, right={column={name=resolved_at, table_ref=null}}}}}, from={table={alias=null, table=events}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[delta_days]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{events={resolved_at=[[@7,33:43='resolved_at',<459>,1:33]], created_at=[[@5,21:30='created_at',<459>,1:21]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={delta_days=[[@10,49:58='delta_days',<459>,1:49]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={delta_days=[[@10,49:58='delta_days',<459>,1:49]]}, table_dictionary={events={resolved_at=[[@7,33:43='resolved_at',<459>,1:33]], created_at=[[@5,21:30='created_at',<459>,1:21]]}}, interface={delta_days=[{name=created_at, table_ref=events}, {name=resolved_at, table_ref=events}]}}}", extractor.getSymbolTable().toString());
		final String expectedSetAUnqualifiedDefQuery0Interface_0 = "{delta_days=[{name=created_at, table_ref=events}, {name=resolved_at, table_ref=events}]}";
		assertSymbolTreePathEquals(extractor.getSymbolTable(), "def_query0.interface", expectedSetAUnqualifiedDefQuery0Interface_0);
	}

	@Test
	public void diffFamilySetASharedCanonicalLevelsAreCapturedAcrossFunctions() {
		final String query = "select datediff(hr, a.created_at, a.resolved_at) as datediff_hours, timediff(hr, a.created_at, a.resolved_at) as timediff_hours, timestampdiff(hr, a.created_at, a.resolved_at) as timestampdiff_hours from events a";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=datediff_hours, datetime_function={function_name=datediff, level={canonical=HOUR, text=hr, form=IDENTIFIER}, left={column={name=created_at, table_ref=a}}, right={column={name=resolved_at, table_ref=a}}}}, 2={alias=timediff_hours, datetime_function={function_name=timediff, level={canonical=HOUR, text=hr, form=IDENTIFIER}, left={column={name=created_at, table_ref=a}}, right={column={name=resolved_at, table_ref=a}}}}, 3={alias=timestampdiff_hours, datetime_function={function_name=timestampdiff, level={canonical=HOUR, text=hr, form=IDENTIFIER}, left={column={name=created_at, table_ref=a}}, right={column={name=resolved_at, table_ref=a}}}}}, from={table={alias=a, table=events}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[datediff_hours, timediff_hours, timestampdiff_hours]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{events={resolved_at=[[@9,34:34='a',<459>,1:34], [@24,95:95='a',<459>,1:95], [@39,161:161='a',<459>,1:161]], created_at=[[@5,20:20='a',<459>,1:20], [@20,81:81='a',<459>,1:81], [@35,147:147='a',<459>,1:147]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={timediff_hours=[[@29,113:126='timediff_hours',<459>,1:113]], datediff_hours=[[@14,52:65='datediff_hours',<459>,1:52]], timestampdiff_hours=[[@44,179:197='timestampdiff_hours',<459>,1:179]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={datediff_hours=[[@14,52:65='datediff_hours',<459>,1:52]], timediff_hours=[[@29,113:126='timediff_hours',<459>,1:113]], timestampdiff_hours=[[@44,179:197='timestampdiff_hours',<459>,1:179]]}, table_dictionary={events={resolved_at=[[@9,34:34='a',<459>,1:34], [@24,95:95='a',<459>,1:95], [@39,161:161='a',<459>,1:161]], created_at=[[@5,20:20='a',<459>,1:20], [@20,81:81='a',<459>,1:81], [@35,147:147='a',<459>,1:147]]}}, interface={datediff_hours=[{name=created_at, table_ref=a}, {name=resolved_at, table_ref=a}], timediff_hours=[{name=created_at, table_ref=a}, {name=resolved_at, table_ref=a}], timestampdiff_hours=[{name=created_at, table_ref=a}, {name=resolved_at, table_ref=a}]}, table_alias={a=events}}}", extractor.getSymbolTable().toString());
	}

	@Test
	public void diffFamilySetAAliasSpellingNormalizesButPreservesAuthoredText() {
		final String query = "select datediff(mins, a.created_at, a.resolved_at) as delta_minutes from events a";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=delta_minutes, datetime_function={function_name=datediff, level={canonical=MINUTE, text=mins, form=IDENTIFIER}, left={column={name=created_at, table_ref=a}}, right={column={name=resolved_at, table_ref=a}}}}}, from={table={alias=a, table=events}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[delta_minutes]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{events={resolved_at=[[@9,36:36='a',<459>,1:36]], created_at=[[@5,22:22='a',<459>,1:22]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={delta_minutes=[[@14,54:66='delta_minutes',<459>,1:54]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={delta_minutes=[[@14,54:66='delta_minutes',<459>,1:54]]}, table_dictionary={events={resolved_at=[[@9,36:36='a',<459>,1:36]], created_at=[[@5,22:22='a',<459>,1:22]]}}, interface={delta_minutes=[{name=created_at, table_ref=a}, {name=resolved_at, table_ref=a}]}, table_alias={a=events}}}", extractor.getSymbolTable().toString());
	}

	@Test
	public void diffFamilySetAQuotedLevelPreservesStringFormMetadata() {
		final String query = "select datediff('day', a.created_at, a.resolved_at) as delta_days from events a";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=delta_days, datetime_function={function_name=datediff, level={canonical=DAY, text=day, form=STRING}, left={column={name=created_at, table_ref=a}}, right={column={name=resolved_at, table_ref=a}}}}}, from={table={alias=a, table=events}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[delta_days]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{events={resolved_at=[[@9,37:37='a',<459>,1:37]], created_at=[[@5,23:23='a',<459>,1:23]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={delta_days=[[@14,55:64='delta_days',<459>,1:55]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={delta_days=[[@14,55:64='delta_days',<459>,1:55]]}, table_dictionary={events={resolved_at=[[@9,37:37='a',<459>,1:37]], created_at=[[@5,23:23='a',<459>,1:23]]}}, interface={delta_days=[{name=created_at, table_ref=a}, {name=resolved_at, table_ref=a}]}, table_alias={a=events}}}", extractor.getSymbolTable().toString());
	}

	@Test
	public void diffFamilySetAIdentifierLevelPreservesIdentifierFormMetadata() {
		final String query = "select datediff(hr, a.created_at, a.resolved_at) as delta_hours from events a";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=delta_hours, datetime_function={function_name=datediff, level={canonical=HOUR, text=hr, form=IDENTIFIER}, left={column={name=created_at, table_ref=a}}, right={column={name=resolved_at, table_ref=a}}}}}, from={table={alias=a, table=events}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[delta_hours]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{events={resolved_at=[[@9,34:34='a',<459>,1:34]], created_at=[[@5,20:20='a',<459>,1:20]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={delta_hours=[[@14,52:62='delta_hours',<459>,1:52]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={delta_hours=[[@14,52:62='delta_hours',<459>,1:52]]}, table_dictionary={events={resolved_at=[[@9,34:34='a',<459>,1:34]], created_at=[[@5,20:20='a',<459>,1:20]]}}, interface={delta_hours=[{name=created_at, table_ref=a}, {name=resolved_at, table_ref=a}]}, table_alias={a=events}}}", extractor.getSymbolTable().toString());
	}

	@Test
	public void diffFamilySetAInvalidIdentifierLevelProducesFatalAtLevelToken() {
		String query = "select datediff(bogus, a.created_at, a.resolved_at) as delta_days from events a";
		SqlParserAccess parser = new SqlParserAccess(false, false, false);
		parser.executeTheParse(query, "SQL");
		Assert.assertEquals(0, parser.getParser().getNumberOfSyntaxErrors());
		Assert.assertEquals(parser.getAllDiagnostics().toString(), 2, parser.getAllDiagnostics().size());
		assertSnowflakeGrammarWarningAt(parser, "snowflake_datetime_diff_expression",
				invocationOffset(query, "datediff"));
		assertFatalDiagnosticAtPositionWithFullMessage(parser.getSnippet(), "INVALID_DATETIME_PART",
				"Invalid date/time part bogus for DATEDIFF at (l:1 c:16).",
				"bogus", 1, 16);
		Assert.assertFalse(parser.getSnippet().getSymbolTable().toString().contains("bogus"));
	}

	@Test
	public void diffFamilySetAInvalidQuotedLevelProducesFatalAtLevelToken() {
		String query = "select datediff('bogus', a.created_at, a.resolved_at) as delta_days from events a";
		SqlParserAccess parser = new SqlParserAccess(false, false, false);
		parser.executeTheParse(query, "SQL");
		Assert.assertEquals(0, parser.getParser().getNumberOfSyntaxErrors());
		Assert.assertEquals(parser.getAllDiagnostics().toString(), 2, parser.getAllDiagnostics().size());
		assertSnowflakeGrammarWarningAt(parser, "snowflake_datetime_diff_expression",
				invocationOffset(query, "datediff"));
		assertFatalDiagnosticAtPositionWithFullMessage(parser.getSnippet(), "INVALID_DATETIME_PART",
				"Invalid date/time part 'bogus' for DATEDIFF at (l:1 c:16).",
				"bogus", 1, 16);
		Assert.assertFalse(parser.getSnippet().getSymbolTable().toString().contains("bogus"));
	}

	@Test
	public void diffFamilySetAMixedCaseInvocationNameIsPreservedInAst() {
		final String query = "select DaTeDiFf(day, a.created_at, a.resolved_at) as delta_days from events a";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=delta_days, datetime_function={function_name=DaTeDiFf, level={canonical=DAY, text=day, form=KEYWORD}, left={column={name=created_at, table_ref=a}}, right={column={name=resolved_at, table_ref=a}}}}}, from={table={alias=a, table=events}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[delta_days]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{events={resolved_at=[[@9,35:35='a',<459>,1:35]], created_at=[[@5,21:21='a',<459>,1:21]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={delta_days=[[@14,53:62='delta_days',<459>,1:53]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={delta_days=[[@14,53:62='delta_days',<459>,1:53]]}, table_dictionary={events={resolved_at=[[@9,35:35='a',<459>,1:35]], created_at=[[@5,21:21='a',<459>,1:21]]}}, interface={delta_days=[{name=created_at, table_ref=a}, {name=resolved_at, table_ref=a}]}, table_alias={a=events}}}", extractor.getSymbolTable().toString());
	}

	@Test
	public void diffFamilySetALeftRightOrderIsPreservedInAstAndLineage() {
		final String query = "select datediff(day, a.left_ts, a.right_ts) as delta_days from events a";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=delta_days, datetime_function={function_name=datediff, level={canonical=DAY, text=day, form=KEYWORD}, left={column={name=left_ts, table_ref=a}}, right={column={name=right_ts, table_ref=a}}}}}, from={table={alias=a, table=events}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[delta_days]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{events={right_ts=[[@9,32:32='a',<459>,1:32]], left_ts=[[@5,21:21='a',<459>,1:21]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={delta_days=[[@14,47:56='delta_days',<459>,1:47]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={delta_days=[[@14,47:56='delta_days',<459>,1:47]]}, table_dictionary={events={right_ts=[[@9,32:32='a',<459>,1:32]], left_ts=[[@5,21:21='a',<459>,1:21]]}}, interface={delta_days=[{name=left_ts, table_ref=a}, {name=right_ts, table_ref=a}]}, table_alias={a=events}}}", extractor.getSymbolTable().toString());
		final String expectedSetALeftRightDefQuery0Interface_0 = "{delta_days=[{name=left_ts, table_ref=a}, {name=right_ts, table_ref=a}]}";
		assertSymbolTreePathEquals(extractor.getSymbolTable(), "def_query0.interface", expectedSetALeftRightDefQuery0Interface_0);
	}

	@Test
	public void datediffReportsAmbiguousUnqualifiedLeftOperand() {
		String query = "select a.id as event_id, datediff(day, created_at, a.resolved_at) as delta_days from events a join accounts b on a.account_id = b.id where a.active = 1";
		SqlParserAccess parser = new SqlParserAccess(false, false, false);
		parser.executeTheParse(query, "SQL");
		Assert.assertEquals(0, parser.getParser().getNumberOfSyntaxErrors());
		Assert.assertEquals(parser.getAllDiagnostics().toString(), 3, parser.getAllDiagnostics().size());
		assertSnowflakeGrammarWarningAt(parser, "snowflake_datetime_diff_expression",
				invocationOffset(query, "datediff"));
		assertDiagnosticAtPosition(parser.getSnippet(), "AMBIGUOUS_COLUMN_REFERENCE", ParseDiagnostic.Severity.SEVERE_WARNING,
				"Ambiguous column reference 'created_at' at (l:1 c:39). Possible sources: [accounts, events]",
				"created_at", 1, 39);
		assertDiagnosticAtPosition(parser.getSnippet(), "UNRESOLVED_UNQUALIFIED_COLUMNS", ParseDiagnostic.Severity.ERROR,
				"Unresolved unqualified column reference(s): [created_at [(l:1 c:39)]]",
				"created_at", 1, 39);
		Assert.assertEquals("{accounts={id=[[@30,128:128='b',<459>,1:128]]}, events={account_id=[[@26,113:113='a',<459>,1:113]], resolved_at=[[@13,51:51='a',<459>,1:51]], active=[[@34,139:139='a',<459>,1:139]], id=[[@1,7:7='a',<459>,1:7]]}}",
				parser.getSnippet().getTableDictionary().toString());
		Assert.assertEquals("{def_query0={query_dictionary={event_id=[[@5,15:22='event_id',<459>,1:15]], delta_days=[[@18,69:78='delta_days',<459>,1:69]]}, table_dictionary={accounts={id=[[@30,128:128='b',<459>,1:128]]}, events={account_id=[[@26,113:113='a',<459>,1:113]], resolved_at=[[@13,51:51='a',<459>,1:51]], active=[[@34,139:139='a',<459>,1:139]], id=[[@1,7:7='a',<459>,1:7]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}, {name=active, table_ref=a}], interface={event_id=[{name=id, table_ref=a}], delta_days=[{name=created_at, table_ref=null}, {name=resolved_at, table_ref=a}]}, table_alias={a=events, b=accounts}}}",
				parser.getSnippet().getSymbolTable().toString());
	}

	@Test
	public void datediffReportsAmbiguousUnqualifiedRightOperand() {
		String query = "select a.id as event_id, datediff(day, a.created_at, resolved_at) as delta_days from events a join accounts b on a.account_id = b.id where b.enabled = 1";
		SqlParserAccess parser = new SqlParserAccess(false, false, false);
		parser.executeTheParse(query, "SQL");
		Assert.assertEquals(0, parser.getParser().getNumberOfSyntaxErrors());
		Assert.assertEquals(parser.getAllDiagnostics().toString(), 3, parser.getAllDiagnostics().size());
		assertSnowflakeGrammarWarningAt(parser, "snowflake_datetime_diff_expression",
				invocationOffset(query, "datediff"));
		assertDiagnosticAtPosition(parser.getSnippet(), "AMBIGUOUS_COLUMN_REFERENCE", ParseDiagnostic.Severity.SEVERE_WARNING,
				"Ambiguous column reference 'resolved_at' at (l:1 c:53). Possible sources: [accounts, events]",
				"resolved_at", 1, 53);
		assertDiagnosticAtPosition(parser.getSnippet(), "UNRESOLVED_UNQUALIFIED_COLUMNS", ParseDiagnostic.Severity.ERROR,
				"Unresolved unqualified column reference(s): [resolved_at [(l:1 c:53)]]",
				"resolved_at", 1, 53);
		Assert.assertEquals("{accounts={id=[[@30,128:128='b',<459>,1:128]], enabled=[[@34,139:139='b',<459>,1:139]]}, events={account_id=[[@26,113:113='a',<459>,1:113]], created_at=[[@11,39:39='a',<459>,1:39]], id=[[@1,7:7='a',<459>,1:7]]}}",
				parser.getSnippet().getTableDictionary().toString());
		Assert.assertEquals("{def_query0={query_dictionary={event_id=[[@5,15:22='event_id',<459>,1:15]], delta_days=[[@18,69:78='delta_days',<459>,1:69]]}, table_dictionary={accounts={id=[[@30,128:128='b',<459>,1:128]], enabled=[[@34,139:139='b',<459>,1:139]]}, events={account_id=[[@26,113:113='a',<459>,1:113]], created_at=[[@11,39:39='a',<459>,1:39]], id=[[@1,7:7='a',<459>,1:7]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}, {name=enabled, table_ref=b}], interface={event_id=[{name=id, table_ref=a}], delta_days=[{name=created_at, table_ref=a}, {name=resolved_at, table_ref=null}]}, table_alias={a=events, b=accounts}}}",
				parser.getSnippet().getSymbolTable().toString());
	}

	@Test
	public void datediffReportsAmbiguousColumnsInBothOperands() {
		String query = "select a.id as event_id, datediff(day, created_at, resolved_at) as delta_days from events a join accounts b on a.account_id = b.id where a.active = 1";
		SqlParserAccess parser = new SqlParserAccess(false, false, false);
		parser.executeTheParse(query, "SQL");
		Assert.assertEquals(0, parser.getParser().getNumberOfSyntaxErrors());
		Assert.assertEquals(parser.getAllDiagnostics().toString(), 4, parser.getAllDiagnostics().size());
		assertSnowflakeGrammarWarningAt(parser, "snowflake_datetime_diff_expression",
				invocationOffset(query, "datediff"));
		assertDiagnosticAtPosition(parser.getSnippet(), "AMBIGUOUS_COLUMN_REFERENCE", ParseDiagnostic.Severity.SEVERE_WARNING,
				"Ambiguous column reference 'created_at' at (l:1 c:39). Possible sources: [accounts, events]",
				"created_at", 1, 39);
		assertDiagnosticAtPosition(parser.getSnippet(), "AMBIGUOUS_COLUMN_REFERENCE", ParseDiagnostic.Severity.SEVERE_WARNING,
				"Ambiguous column reference 'resolved_at' at (l:1 c:51). Possible sources: [accounts, events]",
				"resolved_at", 1, 51);
		assertDiagnosticAtPosition(parser.getSnippet(), "UNRESOLVED_UNQUALIFIED_COLUMNS", ParseDiagnostic.Severity.ERROR,
				"Unresolved unqualified column reference(s): [resolved_at [(l:1 c:51)], created_at [(l:1 c:39)]]",
				"resolved_at", 1, 51);
		Assert.assertEquals("{accounts={id=[[@28,126:126='b',<459>,1:126]]}, events={account_id=[[@24,111:111='a',<459>,1:111]], active=[[@32,137:137='a',<459>,1:137]], id=[[@1,7:7='a',<459>,1:7]]}}",
				parser.getSnippet().getTableDictionary().toString());
		Assert.assertEquals("{def_query0={query_dictionary={event_id=[[@5,15:22='event_id',<459>,1:15]], delta_days=[[@16,67:76='delta_days',<459>,1:67]]}, table_dictionary={accounts={id=[[@28,126:126='b',<459>,1:126]]}, events={account_id=[[@24,111:111='a',<459>,1:111]], active=[[@32,137:137='a',<459>,1:137]], id=[[@1,7:7='a',<459>,1:7]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}, {name=active, table_ref=a}], interface={event_id=[{name=id, table_ref=a}], delta_days=[{name=created_at, table_ref=null}, {name=resolved_at, table_ref=null}]}, table_alias={a=events, b=accounts}}}",
				parser.getSnippet().getSymbolTable().toString());
	}

	@Test
	public void datediffReportsUnresolvedUnqualifiedLeftOperand() {
		String query = "select datediff(day, missing_left, a.resolved_at) as delta_days, a.id from events a where a.resolved_at is not null";
		SqlParserAccess parser = new SqlParserAccess(false, false, false);
		parser.executeTheParse(query, "SQL");
		Assert.assertEquals(0, parser.getParser().getNumberOfSyntaxErrors());
		Assert.assertEquals(parser.getAllDiagnostics().toString(), 1, parser.getAllDiagnostics().size());
		assertSnowflakeGrammarWarningAt(parser, "snowflake_datetime_diff_expression",
				invocationOffset(query, "datediff"));
		Assert.assertEquals("{events={missing_left=[[@5,21:32='missing_left',<459>,1:21]], resolved_at=[[@7,35:35='a',<459>,1:35], [@21,90:90='a',<459>,1:90]], id=[[@14,65:65='a',<459>,1:65]]}}",
				parser.getSnippet().getTableDictionary().toString());
		Assert.assertEquals("{def_query0={query_dictionary={id=[[@16,67:68='id',<459>,1:67]], delta_days=[[@12,53:62='delta_days',<459>,1:53]]}, table_dictionary={events={missing_left=[[@5,21:32='missing_left',<459>,1:21]], resolved_at=[[@7,35:35='a',<459>,1:35], [@21,90:90='a',<459>,1:90]], id=[[@14,65:65='a',<459>,1:65]]}}, filters=[{name=resolved_at, table_ref=a}], interface={id=[{name=id, table_ref=a}], delta_days=[{name=missing_left, table_ref=events}, {name=resolved_at, table_ref=a}]}, table_alias={a=events}}}",
				parser.getSnippet().getSymbolTable().toString());
	}

	@Test
	public void datediffReportsUnresolvedUnqualifiedRightOperand() {
		String query = "select datediff(day, a.created_at, missing_right) as delta_days, a.id from events a where a.created_at is not null";
		SqlParserAccess parser = new SqlParserAccess(false, false, false);
		parser.executeTheParse(query, "SQL");
		Assert.assertEquals(0, parser.getParser().getNumberOfSyntaxErrors());
		Assert.assertEquals(parser.getAllDiagnostics().toString(), 1, parser.getAllDiagnostics().size());
		assertSnowflakeGrammarWarningAt(parser, "snowflake_datetime_diff_expression",
				invocationOffset(query, "datediff"));
		Assert.assertEquals("{events={created_at=[[@5,21:21='a',<459>,1:21], [@21,90:90='a',<459>,1:90]], missing_right=[[@9,35:47='missing_right',<459>,1:35]], id=[[@14,65:65='a',<459>,1:65]]}}",
				parser.getSnippet().getTableDictionary().toString());
		Assert.assertEquals("{def_query0={query_dictionary={id=[[@16,67:68='id',<459>,1:67]], delta_days=[[@12,53:62='delta_days',<459>,1:53]]}, table_dictionary={events={created_at=[[@5,21:21='a',<459>,1:21], [@21,90:90='a',<459>,1:90]], missing_right=[[@9,35:47='missing_right',<459>,1:35]], id=[[@14,65:65='a',<459>,1:65]]}}, filters=[{name=created_at, table_ref=a}], interface={id=[{name=id, table_ref=a}], delta_days=[{name=created_at, table_ref=a}, {name=missing_right, table_ref=events}]}, table_alias={a=events}}}",
				parser.getSnippet().getSymbolTable().toString());
	}

	@Test
	public void datediffRejectsHiddenUnderlyingDerivedLeftOperand() {
		String query = "select datediff(day, d.created_at, d.resolved_at) as delta_days, d.id from (select e.id, e.created_at as created_out, e.resolved_at from events e) d where d.id > 0";
		SqlParserAccess parser = new SqlParserAccess(false, false, false);
		parser.executeTheParse(query, "SQL");
		Assert.assertEquals(0, parser.getParser().getNumberOfSyntaxErrors());
		Assert.assertEquals(parser.getAllDiagnostics().toString(), 2, parser.getAllDiagnostics().size());
		assertSnowflakeGrammarWarningAt(parser, "snowflake_datetime_diff_expression",
				invocationOffset(query, "datediff"));
		assertFatalDiagnosticAtPositionWithFullMessage(parser.getSnippet(), "QUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIAS",
				"Qualified column 'created_at' at (l:1 c:21) was not found in output interface of query alias 'd'.",
				"d.created_at", 1, 21);
		Assert.assertEquals("{events={resolved_at=[[@32,118:118='e',<459>,1:118]], created_at=[[@26,89:89='e',<459>,1:89]], id=[[@22,83:83='e',<459>,1:83]]}}",
				parser.getSnippet().getTableDictionary().toString());
		Assert.assertEquals("{def_query1={query_dictionary={id=[[@18,67:68='id',<459>,1:67]], delta_days=[[@14,53:62='delta_days',<459>,1:53]]}, def_query0={query_dictionary={created_out=[[@30,105:115='created_out',<459>,1:105]], resolved_at=[[@34,120:130='resolved_at',<459>,1:120], [@9,35:35='d',<459>,1:35]], id=[[@24,85:86='id',<459>,1:85], [@16,65:65='d',<459>,1:65], [@41,155:155='d',<459>,1:155]]}, table_dictionary={events={resolved_at=[[@32,118:118='e',<459>,1:118]], created_at=[[@26,89:89='e',<459>,1:89]], id=[[@22,83:83='e',<459>,1:83]]}}, interface={created_out=[{name=created_at, table_ref=e}], resolved_at=[{name=resolved_at, table_ref=e}], id=[{name=id, table_ref=e}]}, table_alias={e=events}}, filters=[{name=id, table_ref=d}], interface={id=[{name=id, table_ref=d}], delta_days=[{name=created_at, table_ref=d}, {name=resolved_at, table_ref=d}]}, table_alias={d=query0}}}",
				parser.getSnippet().getSymbolTable().toString());
	}

	@Test
	public void datediffRejectsHiddenUnderlyingDerivedRightOperand() {
		String query = "select datediff(day, d.created_out, d.resolved_at) as delta_days, d.id from (select e.id, e.created_at as created_out, e.resolved_at as resolved_out from events e) d where d.id > 0";
		SqlParserAccess parser = new SqlParserAccess(false, false, false);
		parser.executeTheParse(query, "SQL");
		Assert.assertEquals(0, parser.getParser().getNumberOfSyntaxErrors());
		Assert.assertEquals(parser.getAllDiagnostics().toString(), 2, parser.getAllDiagnostics().size());
		assertSnowflakeGrammarWarningAt(parser, "snowflake_datetime_diff_expression",
				invocationOffset(query, "datediff"));
		assertFatalDiagnosticAtPositionWithFullMessage(parser.getSnippet(), "QUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIAS",
				"Qualified column 'resolved_at' at (l:1 c:36) was not found in output interface of query alias 'd'.",
				"d.resolved_at", 1, 36);
		Assert.assertEquals("{events={resolved_at=[[@32,119:119='e',<459>,1:119]], created_at=[[@26,90:90='e',<459>,1:90]], id=[[@22,84:84='e',<459>,1:84]]}}",
				parser.getSnippet().getTableDictionary().toString());
		Assert.assertEquals("{def_query1={query_dictionary={id=[[@18,68:69='id',<459>,1:68]], delta_days=[[@14,54:63='delta_days',<459>,1:54]]}, def_query0={query_dictionary={resolved_out=[[@36,136:147='resolved_out',<459>,1:136]], created_out=[[@30,106:116='created_out',<459>,1:106], [@5,21:21='d',<459>,1:21]], id=[[@24,86:87='id',<459>,1:86], [@16,66:66='d',<459>,1:66], [@43,172:172='d',<459>,1:172]]}, table_dictionary={events={resolved_at=[[@32,119:119='e',<459>,1:119]], created_at=[[@26,90:90='e',<459>,1:90]], id=[[@22,84:84='e',<459>,1:84]]}}, interface={resolved_out=[{name=resolved_at, table_ref=e}], created_out=[{name=created_at, table_ref=e}], id=[{name=id, table_ref=e}]}, table_alias={e=events}}, filters=[{name=id, table_ref=d}], interface={id=[{name=id, table_ref=d}], delta_days=[{name=created_out, table_ref=d}, {name=resolved_at, table_ref=d}]}, table_alias={d=query0}}}",
				parser.getSnippet().getSymbolTable().toString());
	}

	@Test
	public void datediffRejectsMissingCteOutputInRightOperand() {
		String query = "with base as (select e.id, e.created_at, e.status from events e) select datediff(day, b.created_at, b.resolved_at) as delta_days, b.id from base b where b.status is not null";
		SqlParserAccess parser = new SqlParserAccess(false, false, false);
		parser.executeTheParse(query, "SQL");
		Assert.assertEquals(0, parser.getParser().getNumberOfSyntaxErrors());
		Assert.assertEquals(parser.getAllDiagnostics().toString(), 2, parser.getAllDiagnostics().size());
		assertSnowflakeGrammarWarningAt(parser, "snowflake_datetime_diff_expression",
				invocationOffset(query, "datediff"));
		assertFatalDiagnosticAtPositionWithFullMessage(parser.getSnippet(), "QUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIAS",
				"Qualified column 'resolved_at' at (l:1 c:100) was not found in output interface of query alias 'b'.",
				"b.resolved_at", 1, 100);
		Assert.assertEquals("{events={created_at=[[@9,27:27='e',<459>,1:27]], id=[[@5,21:21='e',<459>,1:21]], status=[[@13,41:41='e',<459>,1:41]]}}",
				parser.getSnippet().getTableDictionary().toString());
		Assert.assertEquals("{def_query1={context_list={base=query0, b=query0}, query_dictionary={id=[[@38,132:133='id',<459>,1:132]], delta_days=[[@34,118:127='delta_days',<459>,1:118]]}, def_query0={query_dictionary={created_at=[[@11,29:38='created_at',<459>,1:29], [@25,86:86='b',<459>,1:86]], id=[[@7,23:24='id',<459>,1:23], [@36,130:130='b',<459>,1:130]], status=[[@15,43:48='status',<459>,1:43], [@43,153:153='b',<459>,1:153]]}, table_dictionary={events={created_at=[[@9,27:27='e',<459>,1:27]], id=[[@5,21:21='e',<459>,1:21]], status=[[@13,41:41='e',<459>,1:41]]}}, interface={created_at=[{name=created_at, table_ref=e}], id=[{name=id, table_ref=e}], status=[{name=status, table_ref=e}]}, table_alias={e=events}}, filters=[{name=status, table_ref=b}], interface={id=[{name=id, table_ref=b}], delta_days=[{name=created_at, table_ref=b}, {name=resolved_at, table_ref=b}]}, table_alias={b=query0, base=query0}}}",
				parser.getSnippet().getSymbolTable().toString());
	}



	@Test
	public void diffFamilySetCUnknownAliasLeftV0Test() {
		String query = "select datediff(day, z.created_at, a.resolved_at) as delta_days from events a";
		SqlParserAccess parser = new SqlParserAccess(false, false, false);
		parser.executeTheParse(query, "SQL");
		Assert.assertEquals(0, parser.getParser().getNumberOfSyntaxErrors());
		assertSnowflakeGrammarWarningAt(parser, "snowflake_datetime_diff_expression", invocationOffset(query, "datediff"));
		assertFatalDiagnosticCount(parser.getSnippet(), "QUALIFIED_COLUMN_NOT_FOUND_IN_TABLE",
				"No alias or table called 'z'", null, 1);
		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=delta_days, datetime_function={function_name=datediff, level={canonical=DAY, text=day, form=KEYWORD}, left={column={name=created_at, table_ref=z}}, right={column={name=resolved_at, table_ref=a}}}}}, from={table={alias=a, table=events}}}}", parser.getSnippet().getSqlAbstractTree().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{events={resolved_at=[[@9,35:35='a',<459>,1:35]]}}", parser.getSnippet().getTableDictionary().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={delta_days=[[@14,53:62='delta_days',<459>,1:53]]}}", parser.getSnippet().getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={delta_days=[[@14,53:62='delta_days',<459>,1:53]]}, table_dictionary={events={resolved_at=[[@9,35:35='a',<459>,1:35]]}}, interface={delta_days=[{name=created_at, table_ref=z}, {name=resolved_at, table_ref=a}]}, table_alias={a=events}}}", parser.getSnippet().getSymbolTable().toString());
	}

	@Test
	public void diffFamilySetCUnknownAliasRightV0Test() {
		String query = "select datediff(day, a.created_at, z.resolved_at) as delta_days from events a";
		SqlParserAccess parser = new SqlParserAccess(false, false, false);
		parser.executeTheParse(query, "SQL");
		Assert.assertEquals(0, parser.getParser().getNumberOfSyntaxErrors());
		assertSnowflakeGrammarWarningAt(parser, "snowflake_datetime_diff_expression", invocationOffset(query, "datediff"));
		assertFatalDiagnosticCount(parser.getSnippet(), "QUALIFIED_COLUMN_NOT_FOUND_IN_TABLE",
				"No alias or table called 'z'", null, 1);
		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=delta_days, datetime_function={function_name=datediff, level={canonical=DAY, text=day, form=KEYWORD}, left={column={name=created_at, table_ref=a}}, right={column={name=resolved_at, table_ref=z}}}}}, from={table={alias=a, table=events}}}}", parser.getSnippet().getSqlAbstractTree().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{events={created_at=[[@5,21:21='a',<459>,1:21]]}}", parser.getSnippet().getTableDictionary().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={delta_days=[[@14,53:62='delta_days',<459>,1:53]]}}", parser.getSnippet().getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={delta_days=[[@14,53:62='delta_days',<459>,1:53]]}, table_dictionary={events={created_at=[[@5,21:21='a',<459>,1:21]]}}, interface={delta_days=[{name=created_at, table_ref=a}, {name=resolved_at, table_ref=z}]}, table_alias={a=events}}}", parser.getSnippet().getSymbolTable().toString());
	}

	@Test
	public void diffFamilySetCMissingDerivedExportInRightOperandV0Test() {
		String query = "select datediff(day, d.created_out, d.missing_ts) as delta_days, d.id from (select e.id, e.created_at as created_out, e.resolved_at as resolved_out from events e) d";
		SqlParserAccess parser = new SqlParserAccess(false, false, false);
		parser.executeTheParse(query, "SQL");
		Assert.assertEquals(0, parser.getParser().getNumberOfSyntaxErrors());
		assertSnowflakeGrammarWarningAt(parser, "snowflake_datetime_diff_expression", invocationOffset(query, "datediff"));
		assertFatalDiagnosticAtPositionWithFullMessage(parser.getSnippet(), "QUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIAS",
				"Qualified column 'missing_ts' at (l:1 c:36) was not found in output interface of query alias 'd'.",
				"d.missing_ts", 1, 36);
		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=delta_days, datetime_function={function_name=datediff, level={canonical=DAY, text=day, form=KEYWORD}, left={column={name=created_out, table_ref=d}}, right={column={name=missing_ts, table_ref=d}}}}, 2={column={name=id, table_ref=d}}}, from={table={alias=d, query={select={1={column={name=id, table_ref=e}}, 2={column={name=created_at, table_ref=e}, alias=created_out}, 3={column={name=resolved_at, table_ref=e}, alias=resolved_out}}, from={table={alias=e, table=events}}}}}}}", parser.getSnippet().getSqlAbstractTree().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{events={resolved_at=[[@32,118:118='e',<459>,1:118]], created_at=[[@26,89:89='e',<459>,1:89]], id=[[@22,83:83='e',<459>,1:83]]}}", parser.getSnippet().getTableDictionary().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={resolved_out=[[@36,135:146='resolved_out',<459>,1:135]], created_out=[[@30,105:115='created_out',<459>,1:105], [@5,21:21='d',<459>,1:21]], id=[[@24,85:86='id',<459>,1:85], [@16,65:65='d',<459>,1:65]]}, query1={id=[[@18,67:68='id',<459>,1:67]], delta_days=[[@14,53:62='delta_days',<459>,1:53]]}}", parser.getSnippet().getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query1={query_dictionary={id=[[@18,67:68='id',<459>,1:67]], delta_days=[[@14,53:62='delta_days',<459>,1:53]]}, def_query0={query_dictionary={resolved_out=[[@36,135:146='resolved_out',<459>,1:135]], created_out=[[@30,105:115='created_out',<459>,1:105], [@5,21:21='d',<459>,1:21]], id=[[@24,85:86='id',<459>,1:85], [@16,65:65='d',<459>,1:65]]}, table_dictionary={events={resolved_at=[[@32,118:118='e',<459>,1:118]], created_at=[[@26,89:89='e',<459>,1:89]], id=[[@22,83:83='e',<459>,1:83]]}}, interface={resolved_out=[{name=resolved_at, table_ref=e}], created_out=[{name=created_at, table_ref=e}], id=[{name=id, table_ref=e}]}, table_alias={e=events}}, interface={id=[{name=id, table_ref=d}], delta_days=[{name=created_out, table_ref=d}, {name=missing_ts, table_ref=d}]}, table_alias={d=query0}}}", parser.getSnippet().getSymbolTable().toString());
	}

	@Test
	public void diffFamilySetCOverlappingCteExportsTriggerAmbiguityV0Test() {
		String query = "with b1 as (select e.id, e.created_at, e.resolved_at from events e), b2 as (select a.id, a.created_at, a.resolved_at from accounts a) select datediff(day, created_at, b1.resolved_at) as delta_days, b1.id from b1 join b2 on b1.id = b2.id";
		SqlParserAccess parser = new SqlParserAccess(false, false, false);
		parser.executeTheParse(query, "SQL");
		Assert.assertEquals(0, parser.getParser().getNumberOfSyntaxErrors());
		assertSnowflakeGrammarWarningAt(parser, "snowflake_datetime_diff_expression", invocationOffset(query, "datediff"));
		assertDiagnosticAtPosition(parser.getSnippet(), "AMBIGUOUS_COLUMN_REFERENCE", ParseDiagnostic.Severity.SEVERE_WARNING,
				"Ambiguous column reference 'created_at'", "created_at", 1, 155);
		assertDiagnosticAtPosition(parser.getSnippet(), "UNRESOLVED_UNQUALIFIED_COLUMNS", ParseDiagnostic.Severity.ERROR,
				"Unresolved unqualified column reference(s): [created_at [(l:1 c:155)]]", "created_at", 1, 155);
		Assert.assertEquals("AST is wrong", "{SQL={with={1={cte={select={1={column={name=id, table_ref=e}}, 2={column={name=created_at, table_ref=e}}, 3={column={name=resolved_at, table_ref=e}}}, from={table={alias=e, table=events}}}, alias=b1}, 2={cte={select={1={column={name=id, table_ref=a}}, 2={column={name=created_at, table_ref=a}}, 3={column={name=resolved_at, table_ref=a}}}, from={table={alias=a, table=accounts}}}, alias=b2}}, query={select={1={alias=delta_days, datetime_function={function_name=datediff, level={canonical=DAY, text=day, form=KEYWORD}, left={column={name=created_at, table_ref=null}}, right={column={name=resolved_at, table_ref=b1}}}}, 2={column={name=id, table_ref=b1}}}, from={join={1={table={alias=null, table=b1}}, 2={join=join, on={condition={left={column={name=id, table_ref=b1}}, right={column={name=id, table_ref=b2}}, operator==}}}, 3={table={alias=null, table=b2}}}}}}}", parser.getSnippet().getSqlAbstractTree().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{accounts={resolved_at=[[@33,103:103='a',<459>,1:103]], created_at=[[@29,89:89='a',<459>,1:89]], id=[[@25,83:83='a',<459>,1:83]]}, events={resolved_at=[[@13,39:39='e',<459>,1:39]], created_at=[[@9,25:25='e',<459>,1:25]], id=[[@5,19:19='e',<459>,1:19]]}}", parser.getSnippet().getTableDictionary().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={created_at=[[@11,27:36='created_at',<459>,1:27]], resolved_at=[[@15,41:51='resolved_at',<459>,1:41], [@47,167:168='b1',<459>,1:167]], id=[[@7,21:22='id',<459>,1:21], [@54,198:199='b1',<459>,1:198], [@62,223:224='b1',<459>,1:223]]}, query1={created_at=[[@31,91:100='created_at',<459>,1:91]], resolved_at=[[@35,105:115='resolved_at',<459>,1:105]], id=[[@27,85:86='id',<459>,1:85], [@66,231:232='b2',<459>,1:231]]}, query2={id=[[@56,201:202='id',<459>,1:201]], delta_days=[[@52,186:195='delta_days',<459>,1:186]]}}", parser.getSnippet().getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query2={context_list={b1=query0, b2=query1}, query_dictionary={id=[[@56,201:202='id',<459>,1:201]], delta_days=[[@52,186:195='delta_days',<459>,1:186]]}, def_query1={context_list={b1=query0}, query_dictionary={resolved_at=[[@35,105:115='resolved_at',<459>,1:105]], created_at=[[@31,91:100='created_at',<459>,1:91]], id=[[@27,85:86='id',<459>,1:85], [@66,231:232='b2',<459>,1:231]]}, table_dictionary={accounts={resolved_at=[[@33,103:103='a',<459>,1:103]], created_at=[[@29,89:89='a',<459>,1:89]], id=[[@25,83:83='a',<459>,1:83]]}}, interface={resolved_at=[{name=resolved_at, table_ref=a}], created_at=[{name=created_at, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=accounts, b1=query0}}, def_query0={query_dictionary={resolved_at=[[@15,41:51='resolved_at',<459>,1:41], [@47,167:168='b1',<459>,1:167]], created_at=[[@11,27:36='created_at',<459>,1:27]], id=[[@7,21:22='id',<459>,1:21], [@54,198:199='b1',<459>,1:198], [@62,223:224='b1',<459>,1:223]]}, table_dictionary={events={resolved_at=[[@13,39:39='e',<459>,1:39]], created_at=[[@9,25:25='e',<459>,1:25]], id=[[@5,19:19='e',<459>,1:19]]}}, interface={resolved_at=[{name=resolved_at, table_ref=e}], created_at=[{name=created_at, table_ref=e}], id=[{name=id, table_ref=e}]}, table_alias={e=events}}, filters=[{name=id, table_ref=b1}, {name=id, table_ref=b2}], interface={id=[{name=id, table_ref=b1}], delta_days=[{name=created_at, table_ref=null}, {name=resolved_at, table_ref=b1}]}, table_alias={b2=query1, b1=query0}}}", parser.getSnippet().getSymbolTable().toString());
	}

	@Test
	public void diffFamilySetDPredicandSubstitutionLeftOnlyV0Test() {
		final String query = "select datediff(day, <left value>, a.resolved_at) as delta_days from events a";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=delta_days, datetime_function={function_name=datediff, level={canonical=DAY, text=day, form=KEYWORD}, left={substitution={name=<left value>, type=predicand}}, right={column={name=resolved_at, table_ref=a}}}}}, from={table={alias=a, table=events}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[delta_days]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<left value>=predicand}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{events={resolved_at=[[@7,35:35='a',<459>,1:35]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={delta_days=[[@12,53:62='delta_days',<459>,1:53]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={delta_days=[[@12,53:62='delta_days',<459>,1:53]]}, table_dictionary={events={resolved_at=[[@7,35:35='a',<459>,1:35]]}}, interface={delta_days=[{name=<left value>, type=predicand}, {name=resolved_at, table_ref=a}]}, table_alias={a=events}}}", extractor.getSymbolTable().toString());
	}

	@Test
	public void diffFamilySetDPredicandSubstitutionRightOnlyV0Test() {
		final String query = "select datediff(day, a.created_at, <right value>) as delta_days from events a";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=delta_days, datetime_function={function_name=datediff, level={canonical=DAY, text=day, form=KEYWORD}, left={column={name=created_at, table_ref=a}}, right={substitution={name=<right value>, type=predicand}}}}}, from={table={alias=a, table=events}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[delta_days]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<right value>=predicand}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{events={created_at=[[@5,21:21='a',<459>,1:21]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={delta_days=[[@12,53:62='delta_days',<459>,1:53]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={delta_days=[[@12,53:62='delta_days',<459>,1:53]]}, table_dictionary={events={created_at=[[@5,21:21='a',<459>,1:21]]}}, interface={delta_days=[{name=created_at, table_ref=a}, {name=<right value>, type=predicand}]}, table_alias={a=events}}}", extractor.getSymbolTable().toString());
	}

	@Test
	public void diffFamilySetDPredicandSubstitutionsBothOperandsV0Test() {
		final String query = "select datediff(day, <left value>, <right value>) as delta_days from events a";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=delta_days, datetime_function={function_name=datediff, level={canonical=DAY, text=day, form=KEYWORD}, left={substitution={name=<left value>, type=predicand}}, right={substitution={name=<right value>, type=predicand}}}}}, from={table={alias=a, table=events}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[delta_days]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<right value>=predicand, <left value>=predicand}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{events={}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={delta_days=[[@10,53:62='delta_days',<459>,1:53]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={delta_days=[[@10,53:62='delta_days',<459>,1:53]]}, table_dictionary={events={}}, interface={delta_days=[{name=<left value>, type=predicand}, {name=<right value>, type=predicand}]}, table_alias={a=events}}}", extractor.getSymbolTable().toString());
	}

	@Test
	public void diffFamilySetDColumnSubstitutionLeftOnlyV0Test() {
		final String query = "select datediff(day, a.<left col>, a.resolved_at) as delta_days from events a";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=delta_days, datetime_function={function_name=datediff, level={canonical=DAY, text=day, form=KEYWORD}, left={column={substitution={name=<left col>, type=column}, table_ref=a}}, right={column={name=resolved_at, table_ref=a}}}}}, from={table={alias=a, table=events}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[delta_days]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<left col>=column}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{events={<left col>=[[@5,21:21='a',<459>,1:21]], resolved_at=[[@9,35:35='a',<459>,1:35]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={delta_days=[[@14,53:62='delta_days',<459>,1:53]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={delta_days=[[@14,53:62='delta_days',<459>,1:53]]}, table_dictionary={events={<left col>=[[@5,21:21='a',<459>,1:21]], resolved_at=[[@9,35:35='a',<459>,1:35]]}}, interface={delta_days=[{substitution={name=<left col>, type=column}, table_ref=a}, {name=resolved_at, table_ref=a}]}, table_alias={a=events}}}", extractor.getSymbolTable().toString());
	}

	@Test
	public void diffFamilySetDColumnSubstitutionRightOnlyV0Test() {
		final String query = "select datediff(day, a.created_at, a.<right col>) as delta_days from events a";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=delta_days, datetime_function={function_name=datediff, level={canonical=DAY, text=day, form=KEYWORD}, left={column={name=created_at, table_ref=a}}, right={column={substitution={name=<right col>, type=column}, table_ref=a}}}}}, from={table={alias=a, table=events}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[delta_days]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<right col>=column}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{events={created_at=[[@5,21:21='a',<459>,1:21]], <right col>=[[@9,35:35='a',<459>,1:35]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={delta_days=[[@14,53:62='delta_days',<459>,1:53]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={delta_days=[[@14,53:62='delta_days',<459>,1:53]]}, table_dictionary={events={created_at=[[@5,21:21='a',<459>,1:21]], <right col>=[[@9,35:35='a',<459>,1:35]]}}, interface={delta_days=[{name=created_at, table_ref=a}, {substitution={name=<right col>, type=column}, table_ref=a}]}, table_alias={a=events}}}", extractor.getSymbolTable().toString());
	}

	@Test
	public void diffFamilySetDColumnSubstitutionsBothOperandsV0Test() {
		final String query = "select datediff(day, a.<left col>, a.<right col>) as delta_days from events a";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=delta_days, datetime_function={function_name=datediff, level={canonical=DAY, text=day, form=KEYWORD}, left={column={substitution={name=<left col>, type=column}, table_ref=a}}, right={column={substitution={name=<right col>, type=column}, table_ref=a}}}}}, from={table={alias=a, table=events}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[delta_days]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<left col>=column, <right col>=column}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{events={<left col>=[[@5,21:21='a',<459>,1:21]], <right col>=[[@9,35:35='a',<459>,1:35]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={delta_days=[[@14,53:62='delta_days',<459>,1:53]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={delta_days=[[@14,53:62='delta_days',<459>,1:53]]}, table_dictionary={events={<left col>=[[@5,21:21='a',<459>,1:21]], <right col>=[[@9,35:35='a',<459>,1:35]]}}, interface={delta_days=[{substitution={name=<left col>, type=column}, table_ref=a}, {substitution={name=<right col>, type=column}, table_ref=a}]}, table_alias={a=events}}}", extractor.getSymbolTable().toString());
	}

	@Test
	public void diffFamilySetDMixedPredicandLeftColumnRightV0Test() {
		final String query = "select datediff(day, <left value>, a.<right col>) as delta_days from events a";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=delta_days, datetime_function={function_name=datediff, level={canonical=DAY, text=day, form=KEYWORD}, left={substitution={name=<left value>, type=predicand}}, right={column={substitution={name=<right col>, type=column}, table_ref=a}}}}}, from={table={alias=a, table=events}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[delta_days]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<right col>=column, <left value>=predicand}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{events={<right col>=[[@7,35:35='a',<459>,1:35]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={delta_days=[[@12,53:62='delta_days',<459>,1:53]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={delta_days=[[@12,53:62='delta_days',<459>,1:53]]}, table_dictionary={events={<right col>=[[@7,35:35='a',<459>,1:35]]}}, interface={delta_days=[{name=<left value>, type=predicand}, {substitution={name=<right col>, type=column}, table_ref=a}]}, table_alias={a=events}}}", extractor.getSymbolTable().toString());
	}

	@Test
	public void diffFamilySetDMixedColumnLeftPredicandRightV0Test() {
		final String query = "select datediff(day, a.<left col>, <right value>) as delta_days from events a";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=delta_days, datetime_function={function_name=datediff, level={canonical=DAY, text=day, form=KEYWORD}, left={column={substitution={name=<left col>, type=column}, table_ref=a}}, right={substitution={name=<right value>, type=predicand}}}}}, from={table={alias=a, table=events}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[delta_days]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<left col>=column, <right value>=predicand}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{events={<left col>=[[@5,21:21='a',<459>,1:21]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={delta_days=[[@12,53:62='delta_days',<459>,1:53]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={delta_days=[[@12,53:62='delta_days',<459>,1:53]]}, table_dictionary={events={<left col>=[[@5,21:21='a',<459>,1:21]]}}, interface={delta_days=[{substitution={name=<left col>, type=column}, table_ref=a}, {name=<right value>, type=predicand}]}, table_alias={a=events}}}", extractor.getSymbolTable().toString());
	}

	@Test
	public void diffFamilySetDPredicandInsideLeftFormulaV0Test() {
		final String query = "select datediff(day, a.created_at + <left delta>, a.resolved_at) as delta_days from events a";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=delta_days, datetime_function={function_name=datediff, level={canonical=DAY, text=day, form=KEYWORD}, left={calc={left={column={name=created_at, table_ref=a}}, right={substitution={name=<left delta>, type=predicand}}, operator=+}}, right={column={name=resolved_at, table_ref=a}}}}}, from={table={alias=a, table=events}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[delta_days]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<left delta>=predicand}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{events={resolved_at=[[@11,50:50='a',<459>,1:50]], created_at=[[@5,21:21='a',<459>,1:21]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={delta_days=[[@16,68:77='delta_days',<459>,1:68]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={delta_days=[[@16,68:77='delta_days',<459>,1:68]]}, table_dictionary={events={resolved_at=[[@11,50:50='a',<459>,1:50]], created_at=[[@5,21:21='a',<459>,1:21]]}}, interface={delta_days=[{name=created_at, table_ref=a}, {name=<left delta>, type=predicand}, {name=resolved_at, table_ref=a}]}, table_alias={a=events}}}", extractor.getSymbolTable().toString());
	}

	@Test
	public void diffFamilySetDPredicandInsideRightFormulaV0Test() {
		final String query = "select datediff(day, a.created_at, a.resolved_at + <right delta>) as delta_days from events a";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=delta_days, datetime_function={function_name=datediff, level={canonical=DAY, text=day, form=KEYWORD}, left={column={name=created_at, table_ref=a}}, right={calc={left={column={name=resolved_at, table_ref=a}}, right={substitution={name=<right delta>, type=predicand}}, operator=+}}}}}, from={table={alias=a, table=events}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[delta_days]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<right delta>=predicand}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{events={resolved_at=[[@9,35:35='a',<459>,1:35]], created_at=[[@5,21:21='a',<459>,1:21]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={delta_days=[[@16,69:78='delta_days',<459>,1:69]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={delta_days=[[@16,69:78='delta_days',<459>,1:69]]}, table_dictionary={events={resolved_at=[[@9,35:35='a',<459>,1:35]], created_at=[[@5,21:21='a',<459>,1:21]]}}, interface={delta_days=[{name=created_at, table_ref=a}, {name=resolved_at, table_ref=a}, {name=<right delta>, type=predicand}]}, table_alias={a=events}}}", extractor.getSymbolTable().toString());
	}

	@Test
	public void diffFamilySetDColumnSubstitutionInsideLeftFormulaV0Test() {
		final String query = "select datediff(day, a.created_at + a.<left delta col>, a.resolved_at) as delta_days from events a";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=delta_days, datetime_function={function_name=datediff, level={canonical=DAY, text=day, form=KEYWORD}, left={calc={left={column={name=created_at, table_ref=a}}, right={column={substitution={name=<left delta col>, type=column}, table_ref=a}}, operator=+}}, right={column={name=resolved_at, table_ref=a}}}}}, from={table={alias=a, table=events}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[delta_days]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<left delta col>=column}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{events={resolved_at=[[@13,56:56='a',<459>,1:56]], <left delta col>=[[@9,36:36='a',<459>,1:36]], created_at=[[@5,21:21='a',<459>,1:21]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={delta_days=[[@18,74:83='delta_days',<459>,1:74]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={delta_days=[[@18,74:83='delta_days',<459>,1:74]]}, table_dictionary={events={resolved_at=[[@13,56:56='a',<459>,1:56]], <left delta col>=[[@9,36:36='a',<459>,1:36]], created_at=[[@5,21:21='a',<459>,1:21]]}}, interface={delta_days=[{name=created_at, table_ref=a}, {substitution={name=<left delta col>, type=column}, table_ref=a}, {name=resolved_at, table_ref=a}]}, table_alias={a=events}}}", extractor.getSymbolTable().toString());
	}

	@Test
	public void diffFamilySetDColumnSubstitutionInsideRightFormulaV0Test() {
		final String query = "select datediff(day, a.created_at, a.resolved_at + a.<right delta col>) as delta_days from events a";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=delta_days, datetime_function={function_name=datediff, level={canonical=DAY, text=day, form=KEYWORD}, left={column={name=created_at, table_ref=a}}, right={calc={left={column={name=resolved_at, table_ref=a}}, right={column={substitution={name=<right delta col>, type=column}, table_ref=a}}, operator=+}}}}}, from={table={alias=a, table=events}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[delta_days]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<right delta col>=column}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{events={resolved_at=[[@9,35:35='a',<459>,1:35]], created_at=[[@5,21:21='a',<459>,1:21]], <right delta col>=[[@13,51:51='a',<459>,1:51]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={delta_days=[[@18,75:84='delta_days',<459>,1:75]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={delta_days=[[@18,75:84='delta_days',<459>,1:75]]}, table_dictionary={events={resolved_at=[[@9,35:35='a',<459>,1:35]], created_at=[[@5,21:21='a',<459>,1:21]], <right delta col>=[[@13,51:51='a',<459>,1:51]]}}, interface={delta_days=[{name=created_at, table_ref=a}, {name=resolved_at, table_ref=a}, {substitution={name=<right delta col>, type=column}, table_ref=a}]}, table_alias={a=events}}}", extractor.getSymbolTable().toString());
	}

	@Test
	public void diffFamilySetDMixedSubstitutionsAndColumnsInsideLeftFormulaV0Test() {
		final String query = "select datediff(day, a.created_at + <left delta> + a.<left delta col>, a.resolved_at) as delta_days from events a";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=delta_days, datetime_function={function_name=datediff, level={canonical=DAY, text=day, form=KEYWORD}, left={calc={left={calc={left={column={name=created_at, table_ref=a}}, right={substitution={name=<left delta>, type=predicand}}, operator=+}}, right={column={substitution={name=<left delta col>, type=column}, table_ref=a}}, operator=+}}, right={column={name=resolved_at, table_ref=a}}}}}, from={table={alias=a, table=events}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[delta_days]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<left delta>=predicand, <left delta col>=column}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{events={resolved_at=[[@15,71:71='a',<459>,1:71]], <left delta col>=[[@11,51:51='a',<459>,1:51]], created_at=[[@5,21:21='a',<459>,1:21]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={delta_days=[[@20,89:98='delta_days',<459>,1:89]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={delta_days=[[@20,89:98='delta_days',<459>,1:89]]}, table_dictionary={events={resolved_at=[[@15,71:71='a',<459>,1:71]], <left delta col>=[[@11,51:51='a',<459>,1:51]], created_at=[[@5,21:21='a',<459>,1:21]]}}, interface={delta_days=[{name=created_at, table_ref=a}, {name=<left delta>, type=predicand}, {substitution={name=<left delta col>, type=column}, table_ref=a}, {name=resolved_at, table_ref=a}]}, table_alias={a=events}}}", extractor.getSymbolTable().toString());
	}

	@Test
	public void diffFamilySetDMixedSubstitutionsAndColumnsInsideRightFormulaV0Test() {
		final String query = "select datediff(day, a.created_at, a.resolved_at + <right delta> + a.<right delta col>) as delta_days from events a";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=delta_days, datetime_function={function_name=datediff, level={canonical=DAY, text=day, form=KEYWORD}, left={column={name=created_at, table_ref=a}}, right={calc={left={calc={left={column={name=resolved_at, table_ref=a}}, right={substitution={name=<right delta>, type=predicand}}, operator=+}}, right={column={substitution={name=<right delta col>, type=column}, table_ref=a}}, operator=+}}}}}, from={table={alias=a, table=events}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[delta_days]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<right delta>=predicand, <right delta col>=column}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{events={resolved_at=[[@9,35:35='a',<459>,1:35]], created_at=[[@5,21:21='a',<459>,1:21]], <right delta col>=[[@15,67:67='a',<459>,1:67]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={delta_days=[[@20,91:100='delta_days',<459>,1:91]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={delta_days=[[@20,91:100='delta_days',<459>,1:91]]}, table_dictionary={events={resolved_at=[[@9,35:35='a',<459>,1:35]], created_at=[[@5,21:21='a',<459>,1:21]], <right delta col>=[[@15,67:67='a',<459>,1:67]]}}, interface={delta_days=[{name=created_at, table_ref=a}, {name=resolved_at, table_ref=a}, {name=<right delta>, type=predicand}, {substitution={name=<right delta col>, type=column}, table_ref=a}]}, table_alias={a=events}}}", extractor.getSymbolTable().toString());
	}

	@Test
	public void diffFamilySetDMixedSubstitutionsInBothFormulasV0Test() {
		final String query = "select datediff(day, a.created_at + <left delta> + a.<left delta col>, a.resolved_at + <right delta> + a.<right delta col>) as delta_days from events a";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=delta_days, datetime_function={function_name=datediff, level={canonical=DAY, text=day, form=KEYWORD}, left={calc={left={calc={left={column={name=created_at, table_ref=a}}, right={substitution={name=<left delta>, type=predicand}}, operator=+}}, right={column={substitution={name=<left delta col>, type=column}, table_ref=a}}, operator=+}}, right={calc={left={calc={left={column={name=resolved_at, table_ref=a}}, right={substitution={name=<right delta>, type=predicand}}, operator=+}}, right={column={substitution={name=<right delta col>, type=column}, table_ref=a}}, operator=+}}}}}, from={table={alias=a, table=events}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[delta_days]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<left delta>=predicand, <left delta col>=column, <right delta>=predicand, <right delta col>=column}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{events={resolved_at=[[@15,71:71='a',<459>,1:71]], <left delta col>=[[@11,51:51='a',<459>,1:51]], created_at=[[@5,21:21='a',<459>,1:21]], <right delta col>=[[@21,103:103='a',<459>,1:103]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={delta_days=[[@26,127:136='delta_days',<459>,1:127]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={delta_days=[[@26,127:136='delta_days',<459>,1:127]]}, table_dictionary={events={resolved_at=[[@15,71:71='a',<459>,1:71]], <left delta col>=[[@11,51:51='a',<459>,1:51]], created_at=[[@5,21:21='a',<459>,1:21]], <right delta col>=[[@21,103:103='a',<459>,1:103]]}}, interface={delta_days=[{name=created_at, table_ref=a}, {name=<left delta>, type=predicand}, {substitution={name=<left delta col>, type=column}, table_ref=a}, {name=resolved_at, table_ref=a}, {name=<right delta>, type=predicand}, {substitution={name=<right delta col>, type=column}, table_ref=a}]}, table_alias={a=events}}}", extractor.getSymbolTable().toString());
	}

	@Test
	public void diffFamilySetDJoinedSourceSubstitutionTypingIsolatedV0Test() {
		final String query = "select datediff(day, a.created_at + a.<left delta col>, b.resolved_at + b.<right delta col>) as delta_days from events a join accounts b on a.id = b.event_id";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=delta_days, datetime_function={function_name=datediff, level={canonical=DAY, text=day, form=KEYWORD}, left={calc={left={column={name=created_at, table_ref=a}}, right={column={substitution={name=<left delta col>, type=column}, table_ref=a}}, operator=+}}, right={calc={left={column={name=resolved_at, table_ref=b}}, right={column={substitution={name=<right delta col>, type=column}, table_ref=b}}, operator=+}}}}}, from={join={1={table={alias=a, table=events}}, 2={join=join, on={condition={left={column={name=id, table_ref=a}}, right={column={name=event_id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[delta_days]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<left delta col>=column, <right delta col>=column}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{accounts={event_id=[[@34,147:147='b',<459>,1:147]], resolved_at=[[@13,56:56='b',<459>,1:56]], <right delta col>=[[@17,72:72='b',<459>,1:72]]}, events={<left delta col>=[[@9,36:36='a',<459>,1:36]], created_at=[[@5,21:21='a',<459>,1:21]], id=[[@30,140:140='a',<459>,1:140]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={delta_days=[[@22,96:105='delta_days',<459>,1:96]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={delta_days=[[@22,96:105='delta_days',<459>,1:96]]}, table_dictionary={accounts={event_id=[[@34,147:147='b',<459>,1:147]], resolved_at=[[@13,56:56='b',<459>,1:56]], <right delta col>=[[@17,72:72='b',<459>,1:72]]}, events={<left delta col>=[[@9,36:36='a',<459>,1:36]], created_at=[[@5,21:21='a',<459>,1:21]], id=[[@30,140:140='a',<459>,1:140]]}}, filters=[{name=id, table_ref=a}, {name=event_id, table_ref=b}], interface={delta_days=[{name=created_at, table_ref=a}, {substitution={name=<left delta col>, type=column}, table_ref=a}, {name=resolved_at, table_ref=b}, {substitution={name=<right delta col>, type=column}, table_ref=b}]}, table_alias={a=events, b=accounts}}}", extractor.getSymbolTable().toString());
	}



	@Test
	public void diffFamilySetDMissingQualifiedColumnSubstitutionFatalWithoutJoinExtensionV0Test() {
		String query = "select datediff(day, x.<left col>, a.created_at) as delta_days from events a";
		SqlParserAccess parser = new SqlParserAccess(false, false, false);
		parser.executeTheParse(query, "SQL");
		Assert.assertEquals(0, parser.getParser().getNumberOfSyntaxErrors());
		assertSnowflakeGrammarWarningAt(parser, "snowflake_datetime_diff_expression", invocationOffset(query, "datediff"));
		assertFatalDiagnosticAtPositionWithFullMessage(parser.getSnippet(), "QUALIFIED_COLUMN_NOT_FOUND_IN_TABLE",
				"Source Table not found for Column '<left col>' at (l:1 c:21). No alias or table called 'x'.",
				"<left col>", 1, 21);
		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=delta_days, datetime_function={function_name=datediff, level={canonical=DAY, text=day, form=KEYWORD}, left={column={substitution={name=<left col>, type=column}, table_ref=x}}, right={column={name=created_at, table_ref=a}}}}}, from={table={alias=a, table=events}}}}", parser.getSnippet().getSqlAbstractTree().toString());
		Assert.assertEquals("Substitution List is wrong", "{<left col>=column}", parser.getSnippet().getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{events={created_at=[[@9,35:35='a',<459>,1:35]]}}", parser.getSnippet().getTableDictionary().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={delta_days=[[@14,52:61='delta_days',<459>,1:52]]}}", parser.getSnippet().getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={delta_days=[[@14,52:61='delta_days',<459>,1:52]]}, table_dictionary={events={created_at=[[@9,35:35='a',<459>,1:35]]}}, interface={delta_days=[{substitution={name=<left col>, type=column}, table_ref=x}, {name=created_at, table_ref=a}]}, table_alias={a=events}}}", parser.getSnippet().getSymbolTable().toString());
	}

	@Test
	public void diffFamilySetDSameReferenceDeferredWithJoinExtensionV0Test() {
		String query = "select datediff(day, x.<left col>, a.created_at) as delta_days from events a <extension>";
		SqlParserAccess parser = new SqlParserAccess(false, false, false);
		parser.executeTheParse(query, "SQL");
		Assert.assertEquals(0, parser.getParser().getNumberOfSyntaxErrors());
		assertSnowflakeGrammarWarningAt(parser, "snowflake_datetime_diff_expression", invocationOffset(query, "datediff"));
		Assert.assertEquals(0, parser.getSnippet().getFatalErrorCount());
		Assert.assertEquals(1,
				parser.getSnippet().getDiagnosticCountByCodeAndSeverity("QUALIFIED_COLUMN_NOT_FOUND_IN_TABLE", ParseDiagnostic.Severity.SEVERE_WARNING));
		Assert.assertEquals(1,
				parser.getSnippet().getDiagnosticCountByCodeAndSeverity("JOIN_EXTENSION_REQUIRED", ParseDiagnostic.Severity.SEVERE_WARNING));
		String missingMessage = "Source Table not found for Column '<left col>' at (l:1 c:21). No alias or table called 'x'.";
		String extensionMessage = "Join extension variable '<extension>' at (l:1 c:77) is not optional: 1 table/column reference(s)"
				+ " not defined in the query (x.<left col>) must be satisfied by the substituted join extension snippet.";
		assertDiagnosticAtPosition(parser.getSnippet(), "QUALIFIED_COLUMN_NOT_FOUND_IN_TABLE", ParseDiagnostic.Severity.SEVERE_WARNING,
				missingMessage, "<left col>", 1, 21);
		assertDiagnosticAtPosition(parser.getSnippet(), "JOIN_EXTENSION_REQUIRED", ParseDiagnostic.Severity.SEVERE_WARNING,
				extensionMessage, "<extension>", 1, 77);
		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=delta_days, datetime_function={function_name=datediff, level={canonical=DAY, text=day, form=KEYWORD}, left={column={substitution={name=<left col>, type=column}, table_ref=x}}, right={column={name=created_at, table_ref=a}}}}}, from={extension={substitution={name=<extension>, type=join_extension}}, table={alias=a, table=events}}}}", parser.getSnippet().getSqlAbstractTree().toString());
		Assert.assertEquals("Substitution List is wrong", "{<left col>=column, <extension>=join_extension}", parser.getSnippet().getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{events={created_at=[[@9,35:35='a',<459>,1:35]]}}", parser.getSnippet().getTableDictionary().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={delta_days=[[@14,52:61='delta_days',<459>,1:52]]}}", parser.getSnippet().getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={delta_days=[[@14,52:61='delta_days',<459>,1:52]]}, table_dictionary={events={created_at=[[@9,35:35='a',<459>,1:35]]}}, interface={delta_days=[{substitution={name=<left col>, type=column}, table_ref=x}, {name=created_at, table_ref=a}]}, table_alias={a=events}}}", parser.getSnippet().getSymbolTable().toString());
	}

	@Test
	public void diffFamilySetENestedDiffAsArg1InOrdinaryFunctionV0Test() {
		final String query = "select coalesce(datediff(day, a.created_at, a.resolved_at), 0) as delta_days from events a";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={function={parameters={1={datetime_function={function_name=datediff, level={canonical=DAY, text=day, form=KEYWORD}, left={column={name=created_at, table_ref=a}}, right={column={name=resolved_at, table_ref=a}}}}, 2={literal=0}}, function_name=coalesce}, alias=delta_days}}, from={table={alias=a, table=events}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[delta_days]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{events={resolved_at=[[@11,44:44='a',<459>,1:44]], created_at=[[@7,30:30='a',<459>,1:30]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={delta_days=[[@19,66:75='delta_days',<459>,1:66]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={delta_days=[[@19,66:75='delta_days',<459>,1:66]]}, table_dictionary={events={resolved_at=[[@11,44:44='a',<459>,1:44]], created_at=[[@7,30:30='a',<459>,1:30]]}}, interface={delta_days=[{name=created_at, table_ref=a}, {name=resolved_at, table_ref=a}]}, table_alias={a=events}}}", extractor.getSymbolTable().toString());
	}

	@Test
	public void diffFamilySetENestedDiffAsArg2InOrdinaryFunctionV0Test() {
		final String query = "select greatest(0, datediff(day, a.created_at, a.resolved_at)) as delta_days from events a";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={function={parameters={1={literal=0}, 2={datetime_function={function_name=datediff, level={canonical=DAY, text=day, form=KEYWORD}, left={column={name=created_at, table_ref=a}}, right={column={name=resolved_at, table_ref=a}}}}}, function_name=greatest}, alias=delta_days}}, from={table={alias=a, table=events}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[delta_days]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{events={resolved_at=[[@13,47:47='a',<459>,1:47]], created_at=[[@9,33:33='a',<459>,1:33]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={delta_days=[[@19,66:75='delta_days',<459>,1:66]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={delta_days=[[@19,66:75='delta_days',<459>,1:66]]}, table_dictionary={events={resolved_at=[[@13,47:47='a',<459>,1:47]], created_at=[[@9,33:33='a',<459>,1:33]]}}, interface={delta_days=[{name=created_at, table_ref=a}, {name=resolved_at, table_ref=a}]}, table_alias={a=events}}}", extractor.getSymbolTable().toString());
	}

	@Test
	public void diffFamilySetEOrdinaryFunctionNestedInLeftOperandV0Test() {
		final String query = "select datediff(day, coalesce(a.created_at, a.fallback_created_at), a.resolved_at) as delta_days from events a";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=delta_days, datetime_function={function_name=datediff, level={canonical=DAY, text=day, form=KEYWORD}, left={function={parameters={1={column={name=created_at, table_ref=a}}, 2={column={name=fallback_created_at, table_ref=a}}}, function_name=coalesce}}, right={column={name=resolved_at, table_ref=a}}}}}, from={table={alias=a, table=events}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[delta_days]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{events={fallback_created_at=[[@11,44:44='a',<459>,1:44]], resolved_at=[[@16,68:68='a',<459>,1:68]], created_at=[[@7,30:30='a',<459>,1:30]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={delta_days=[[@21,86:95='delta_days',<459>,1:86]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={delta_days=[[@21,86:95='delta_days',<459>,1:86]]}, table_dictionary={events={fallback_created_at=[[@11,44:44='a',<459>,1:44]], resolved_at=[[@16,68:68='a',<459>,1:68]], created_at=[[@7,30:30='a',<459>,1:30]]}}, interface={delta_days=[{name=created_at, table_ref=a}, {name=fallback_created_at, table_ref=a}, {name=resolved_at, table_ref=a}]}, table_alias={a=events}}}", extractor.getSymbolTable().toString());
	}

	@Test
	public void diffFamilySetEOrdinaryFunctionNestedInRightOperandV0Test() {
		final String query = "select datediff(day, a.created_at, coalesce(a.resolved_at, a.fallback_resolved_at)) as delta_days from events a";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=delta_days, datetime_function={function_name=datediff, level={canonical=DAY, text=day, form=KEYWORD}, left={column={name=created_at, table_ref=a}}, right={function={parameters={1={column={name=resolved_at, table_ref=a}}, 2={column={name=fallback_resolved_at, table_ref=a}}}, function_name=coalesce}}}}}, from={table={alias=a, table=events}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[delta_days]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{events={resolved_at=[[@11,44:44='a',<459>,1:44]], fallback_resolved_at=[[@15,59:59='a',<459>,1:59]], created_at=[[@5,21:21='a',<459>,1:21]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={delta_days=[[@21,87:96='delta_days',<459>,1:87]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={delta_days=[[@21,87:96='delta_days',<459>,1:87]]}, table_dictionary={events={resolved_at=[[@11,44:44='a',<459>,1:44]], fallback_resolved_at=[[@15,59:59='a',<459>,1:59]], created_at=[[@5,21:21='a',<459>,1:21]]}}, interface={delta_days=[{name=created_at, table_ref=a}, {name=resolved_at, table_ref=a}, {name=fallback_resolved_at, table_ref=a}]}, table_alias={a=events}}}", extractor.getSymbolTable().toString());
	}

	@Test
	public void diffFamilySetENestedDiffInLeftOperandWithValidOuterDiffV0Test() {
		final String query = "select datediff(day, datediff(day, a.created_at, a.midpoint_at), a.resolved_at) as delta_days from events a";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=delta_days, datetime_function={function_name=datediff, level={canonical=DAY, text=day, form=KEYWORD}, left={datetime_function={function_name=datediff, level={canonical=DAY, text=day, form=KEYWORD}, left={column={name=created_at, table_ref=a}}, right={column={name=midpoint_at, table_ref=a}}}}, right={column={name=resolved_at, table_ref=a}}}}}, from={table={alias=a, table=events}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[delta_days]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{events={resolved_at=[[@18,65:65='a',<459>,1:65]], midpoint_at=[[@13,49:49='a',<459>,1:49]], created_at=[[@9,35:35='a',<459>,1:35]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={delta_days=[[@23,83:92='delta_days',<459>,1:83]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={delta_days=[[@23,83:92='delta_days',<459>,1:83]]}, table_dictionary={events={resolved_at=[[@18,65:65='a',<459>,1:65]], midpoint_at=[[@13,49:49='a',<459>,1:49]], created_at=[[@9,35:35='a',<459>,1:35]]}}, interface={delta_days=[{name=created_at, table_ref=a}, {name=midpoint_at, table_ref=a}, {name=resolved_at, table_ref=a}]}, table_alias={a=events}}}", extractor.getSymbolTable().toString());
	}

	@Test
	public void diffFamilySetENestedDiffInRightOperandWithValidOuterDiffV0Test() {
		final String query = "select datediff(day, a.created_at, datediff(day, a.midpoint_at, a.resolved_at)) as delta_days from events a";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=delta_days, datetime_function={function_name=datediff, level={canonical=DAY, text=day, form=KEYWORD}, left={column={name=created_at, table_ref=a}}, right={datetime_function={function_name=datediff, level={canonical=DAY, text=day, form=KEYWORD}, left={column={name=midpoint_at, table_ref=a}}, right={column={name=resolved_at, table_ref=a}}}}}}}, from={table={alias=a, table=events}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[delta_days]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{events={resolved_at=[[@17,64:64='a',<459>,1:64]], midpoint_at=[[@13,49:49='a',<459>,1:49]], created_at=[[@5,21:21='a',<459>,1:21]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={delta_days=[[@23,83:92='delta_days',<459>,1:83]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={delta_days=[[@23,83:92='delta_days',<459>,1:83]]}, table_dictionary={events={resolved_at=[[@17,64:64='a',<459>,1:64]], midpoint_at=[[@13,49:49='a',<459>,1:49]], created_at=[[@5,21:21='a',<459>,1:21]]}}, interface={delta_days=[{name=created_at, table_ref=a}, {name=midpoint_at, table_ref=a}, {name=resolved_at, table_ref=a}]}, table_alias={a=events}}}", extractor.getSymbolTable().toString());
	}

	@Test
	public void diffFamilySetEInvalidInnerLevelDoesNotContaminateSiblingDiffV0Test() {
		String query = "select datediff(day, datediff(badpart, a.created_at, a.midpoint_at), a.resolved_at) as invalid_delta, datediff(day, a.created_at, a.resolved_at) as valid_delta from events a";
		SqlParserAccess parser = new SqlParserAccess(false, false, false);
		parser.executeTheParse(query, "SQL");
		Assert.assertEquals(0, parser.getParser().getNumberOfSyntaxErrors());
		Assert.assertEquals(1,
				parser.getSnippet().getDiagnosticCountByCodeAndSeverity("INVALID_DATETIME_PART", ParseDiagnostic.Severity.FATAL));
		assertFatalDiagnosticAtPositionWithFullMessage(parser.getSnippet(), "INVALID_DATETIME_PART",
				"Invalid date/time part badpart for DATEDIFF at (l:1 c:30).",
				"badpart", 1, 30);
		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=invalid_delta, datetime_function={function_name=datediff, level={canonical=DAY, text=day, form=KEYWORD}, left={datetime_function={function_name=datediff, level={canonical=BADPART, text=badpart, form=IDENTIFIER}, left={column={name=created_at, table_ref=a}}, right={column={name=midpoint_at, table_ref=a}}}}, right={column={name=resolved_at, table_ref=a}}}}, 2={alias=valid_delta, datetime_function={function_name=datediff, level={canonical=DAY, text=day, form=KEYWORD}, left={column={name=created_at, table_ref=a}}, right={column={name=resolved_at, table_ref=a}}}}}, from={table={alias=a, table=events}}}}", parser.getSnippet().getSqlAbstractTree().toString());
		Assert.assertEquals("Interface is wrong", "[valid_delta, invalid_delta]", parser.getSnippet().getQueryInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", parser.getSnippet().getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{events={resolved_at=[[@18,69:69='a',<459>,1:69], [@33,130:130='a',<459>,1:130]], midpoint_at=[[@13,53:53='a',<459>,1:53]], created_at=[[@9,39:39='a',<459>,1:39], [@29,116:116='a',<459>,1:116]]}}", parser.getSnippet().getTableDictionary().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={valid_delta=[[@38,148:158='valid_delta',<459>,1:148]], invalid_delta=[[@23,87:99='invalid_delta',<459>,1:87]]}}", parser.getSnippet().getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={valid_delta=[[@38,148:158='valid_delta',<459>,1:148]], invalid_delta=[[@23,87:99='invalid_delta',<459>,1:87]]}, table_dictionary={events={resolved_at=[[@18,69:69='a',<459>,1:69], [@33,130:130='a',<459>,1:130]], midpoint_at=[[@13,53:53='a',<459>,1:53]], created_at=[[@9,39:39='a',<459>,1:39], [@29,116:116='a',<459>,1:116]]}}, interface={valid_delta=[{name=created_at, table_ref=a}, {name=resolved_at, table_ref=a}], invalid_delta=[{name=created_at, table_ref=a}, {name=midpoint_at, table_ref=a}, {name=resolved_at, table_ref=a}]}, table_alias={a=events}}}", parser.getSnippet().getSymbolTable().toString());
	}

	@Test
	public void diffFamilySetERepeatedReferencesPreserveOccurrenceSemanticsV0Test() {
		final String query = "select datediff(day, a.created_at, a.created_at) as zero_delta from events a";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=zero_delta, datetime_function={function_name=datediff, level={canonical=DAY, text=day, form=KEYWORD}, left={column={name=created_at, table_ref=a}}, right={column={name=created_at, table_ref=a}}}}}, from={table={alias=a, table=events}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[zero_delta]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{events={created_at=[[@5,21:21='a',<459>,1:21], [@9,35:35='a',<459>,1:35]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={zero_delta=[[@14,52:61='zero_delta',<459>,1:52]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={zero_delta=[[@14,52:61='zero_delta',<459>,1:52]]}, table_dictionary={events={created_at=[[@5,21:21='a',<459>,1:21], [@9,35:35='a',<459>,1:35]]}}, interface={zero_delta=[{name=created_at, table_ref=a}]}, table_alias={a=events}}}", extractor.getSymbolTable().toString());
	}

	@Test
	public void diffFamilySetEDistinctSiblingDiffOutputsRetainIndependentLineageV0Test() {
		final String query = "select datediff(day, a.created_at, a.resolved_at) as delta_one, datediff(day, a.started_at, a.ended_at) as delta_two from events a";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=delta_one, datetime_function={function_name=datediff, level={canonical=DAY, text=day, form=KEYWORD}, left={column={name=created_at, table_ref=a}}, right={column={name=resolved_at, table_ref=a}}}}, 2={alias=delta_two, datetime_function={function_name=datediff, level={canonical=DAY, text=day, form=KEYWORD}, left={column={name=started_at, table_ref=a}}, right={column={name=ended_at, table_ref=a}}}}}, from={table={alias=a, table=events}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[delta_one, delta_two]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{events={resolved_at=[[@9,35:35='a',<459>,1:35]], created_at=[[@5,21:21='a',<459>,1:21]], started_at=[[@20,78:78='a',<459>,1:78]], ended_at=[[@24,92:92='a',<459>,1:92]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={delta_two=[[@29,107:115='delta_two',<459>,1:107]], delta_one=[[@14,53:61='delta_one',<459>,1:53]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={delta_one=[[@14,53:61='delta_one',<459>,1:53]], delta_two=[[@29,107:115='delta_two',<459>,1:107]]}, table_dictionary={events={resolved_at=[[@9,35:35='a',<459>,1:35]], created_at=[[@5,21:21='a',<459>,1:21]], started_at=[[@20,78:78='a',<459>,1:78]], ended_at=[[@24,92:92='a',<459>,1:92]]}}, interface={delta_one=[{name=created_at, table_ref=a}, {name=resolved_at, table_ref=a}], delta_two=[{name=started_at, table_ref=a}, {name=ended_at, table_ref=a}]}, table_alias={a=events}}}", extractor.getSymbolTable().toString());
	}


	@Test
	public void diffFamilySetFDerivedProducerExportsDiffResultLineageV0Test() {
		final String query = "select d.delta_days from (select datediff(day, e.created_at, e.resolved_at) as delta_days from events e) d";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=delta_days, table_ref=d}}}, from={table={alias=d, query={select={1={alias=delta_days, datetime_function={function_name=datediff, level={canonical=DAY, text=day, form=KEYWORD}, left={column={name=created_at, table_ref=e}}, right={column={name=resolved_at, table_ref=e}}}}}, from={table={alias=e, table=events}}}}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[delta_days]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{events={resolved_at=[[@15,61:61='e',<459>,1:61]], created_at=[[@11,47:47='e',<459>,1:47]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={delta_days=[[@20,79:88='delta_days',<459>,1:79], [@1,7:7='d',<459>,1:7]]}, query1={delta_days=[[@3,9:18='delta_days',<459>,1:9]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query1={query_dictionary={delta_days=[[@3,9:18='delta_days',<459>,1:9]]}, def_query0={query_dictionary={delta_days=[[@20,79:88='delta_days',<459>,1:79], [@1,7:7='d',<459>,1:7]]}, table_dictionary={events={resolved_at=[[@15,61:61='e',<459>,1:61]], created_at=[[@11,47:47='e',<459>,1:47]]}}, interface={delta_days=[{name=created_at, table_ref=e}, {name=resolved_at, table_ref=e}]}, table_alias={e=events}}, interface={delta_days=[{name=delta_days, table_ref=d}]}, table_alias={d=query0}}}", extractor.getSymbolTable().toString());
	}

	@Test
	public void diffFamilySetFDerivedConsumerComputesDiffFromExportsV0Test() {
		final String query = "select datediff(day, d.created_at, d.resolved_at) as delta_days from (select e.created_at, e.resolved_at from events e) d";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=delta_days, datetime_function={function_name=datediff, level={canonical=DAY, text=day, form=KEYWORD}, left={column={name=created_at, table_ref=d}}, right={column={name=resolved_at, table_ref=d}}}}}, from={table={alias=d, query={select={1={column={name=created_at, table_ref=e}}, 2={column={name=resolved_at, table_ref=e}}}, from={table={alias=e, table=events}}}}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[delta_days]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{events={resolved_at=[[@22,91:91='e',<459>,1:91]], created_at=[[@18,77:77='e',<459>,1:77]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={created_at=[[@20,79:88='created_at',<459>,1:79], [@5,21:21='d',<459>,1:21]], resolved_at=[[@24,93:103='resolved_at',<459>,1:93], [@9,35:35='d',<459>,1:35]]}, query1={delta_days=[[@14,53:62='delta_days',<459>,1:53]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query1={query_dictionary={delta_days=[[@14,53:62='delta_days',<459>,1:53]]}, def_query0={query_dictionary={resolved_at=[[@24,93:103='resolved_at',<459>,1:93], [@9,35:35='d',<459>,1:35]], created_at=[[@20,79:88='created_at',<459>,1:79], [@5,21:21='d',<459>,1:21]]}, table_dictionary={events={resolved_at=[[@22,91:91='e',<459>,1:91]], created_at=[[@18,77:77='e',<459>,1:77]]}}, interface={resolved_at=[{name=resolved_at, table_ref=e}], created_at=[{name=created_at, table_ref=e}]}, table_alias={e=events}}, interface={delta_days=[{name=created_at, table_ref=d}, {name=resolved_at, table_ref=d}]}, table_alias={d=query0}}}", extractor.getSymbolTable().toString());
	}

	@Test
	public void diffFamilySetFTwoLayerDerivedRenameChainV0Test() {
		final String query = "select datediff(day, x.created_at, x.resolved_at) as delta_days from (select m.c_out as created_at, m.r_out as resolved_at from (select e.created_at as c_out, e.resolved_at as r_out from events e) m) x";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=delta_days, datetime_function={function_name=datediff, level={canonical=DAY, text=day, form=KEYWORD}, left={column={name=created_at, table_ref=x}}, right={column={name=resolved_at, table_ref=x}}}}}, from={table={alias=x, query={select={1={column={name=c_out, table_ref=m}, alias=created_at}, 2={column={name=r_out, table_ref=m}, alias=resolved_at}}, from={table={alias=m, query={select={1={column={name=created_at, table_ref=e}, alias=c_out}, 2={column={name=resolved_at, table_ref=e}, alias=r_out}}, from={table={alias=e, table=events}}}}}}}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[delta_days]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{events={resolved_at=[[@38,159:159='e',<459>,1:159]], created_at=[[@32,136:136='e',<459>,1:136]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={c_out=[[@36,152:156='c_out',<459>,1:152], [@18,77:77='m',<459>,1:77]], r_out=[[@42,176:180='r_out',<459>,1:176], [@24,100:100='m',<459>,1:100]]}, query1={created_at=[[@22,88:97='created_at',<459>,1:88], [@5,21:21='x',<459>,1:21]], resolved_at=[[@28,111:121='resolved_at',<459>,1:111], [@9,35:35='x',<459>,1:35]]}, query2={delta_days=[[@14,53:62='delta_days',<459>,1:53]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query2={query_dictionary={delta_days=[[@14,53:62='delta_days',<459>,1:53]]}, def_query1={query_dictionary={resolved_at=[[@28,111:121='resolved_at',<459>,1:111], [@9,35:35='x',<459>,1:35]], created_at=[[@22,88:97='created_at',<459>,1:88], [@5,21:21='x',<459>,1:21]]}, def_query0={query_dictionary={r_out=[[@42,176:180='r_out',<459>,1:176], [@24,100:100='m',<459>,1:100]], c_out=[[@36,152:156='c_out',<459>,1:152], [@18,77:77='m',<459>,1:77]]}, table_dictionary={events={resolved_at=[[@38,159:159='e',<459>,1:159]], created_at=[[@32,136:136='e',<459>,1:136]]}}, interface={r_out=[{name=resolved_at, table_ref=e}], c_out=[{name=created_at, table_ref=e}]}, table_alias={e=events}}, interface={resolved_at=[{name=r_out, table_ref=m}], created_at=[{name=c_out, table_ref=m}]}, table_alias={m=query0}}, interface={delta_days=[{name=created_at, table_ref=x}, {name=resolved_at, table_ref=x}]}, table_alias={x=query1}}}", extractor.getSymbolTable().toString());
	}

	@Test
	public void diffFamilySetFCteProducerLineageV0Test() {
		final String query = "with base as (select datediff(day, e.created_at, e.resolved_at) as delta_days from events e) select b.delta_days from base b";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={with={1={cte={select={1={alias=delta_days, datetime_function={function_name=datediff, level={canonical=DAY, text=day, form=KEYWORD}, left={column={name=created_at, table_ref=e}}, right={column={name=resolved_at, table_ref=e}}}}}, from={table={alias=e, table=events}}}, alias=base}}, query={select={1={column={name=delta_days, table_ref=b}}}, from={table={alias=b, table=base}}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[delta_days]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{events={resolved_at=[[@13,49:49='e',<459>,1:49]], created_at=[[@9,35:35='e',<459>,1:35]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={delta_days=[[@18,67:76='delta_days',<459>,1:67], [@24,100:100='b',<459>,1:100]]}, query1={delta_days=[[@26,102:111='delta_days',<459>,1:102]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query1={context_list={base=query0, b=query0}, query_dictionary={delta_days=[[@26,102:111='delta_days',<459>,1:102]]}, def_query0={query_dictionary={delta_days=[[@18,67:76='delta_days',<459>,1:67], [@24,100:100='b',<459>,1:100]]}, table_dictionary={events={resolved_at=[[@13,49:49='e',<459>,1:49]], created_at=[[@9,35:35='e',<459>,1:35]]}}, interface={delta_days=[{name=created_at, table_ref=e}, {name=resolved_at, table_ref=e}]}, table_alias={e=events}}, interface={delta_days=[{name=delta_days, table_ref=b}]}, table_alias={b=query0, base=query0}}}", extractor.getSymbolTable().toString());
	}

	@Test
	public void diffFamilySetFCteConsumerLineageV0Test() {
		final String query = "with base as (select e.created_at, e.resolved_at from events e) select datediff(day, b.created_at, b.resolved_at) as delta_days from base b";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={with={1={cte={select={1={column={name=created_at, table_ref=e}}, 2={column={name=resolved_at, table_ref=e}}}, from={table={alias=e, table=events}}}, alias=base}}, query={select={1={alias=delta_days, datetime_function={function_name=datediff, level={canonical=DAY, text=day, form=KEYWORD}, left={column={name=created_at, table_ref=b}}, right={column={name=resolved_at, table_ref=b}}}}}, from={table={alias=b, table=base}}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[delta_days]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{events={resolved_at=[[@9,35:35='e',<459>,1:35]], created_at=[[@5,21:21='e',<459>,1:21]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={created_at=[[@7,23:32='created_at',<459>,1:23], [@21,85:85='b',<459>,1:85]], resolved_at=[[@11,37:47='resolved_at',<459>,1:37], [@25,99:99='b',<459>,1:99]]}, query1={delta_days=[[@30,117:126='delta_days',<459>,1:117]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query1={context_list={base=query0, b=query0}, query_dictionary={delta_days=[[@30,117:126='delta_days',<459>,1:117]]}, def_query0={query_dictionary={resolved_at=[[@11,37:47='resolved_at',<459>,1:37], [@25,99:99='b',<459>,1:99]], created_at=[[@7,23:32='created_at',<459>,1:23], [@21,85:85='b',<459>,1:85]]}, table_dictionary={events={resolved_at=[[@9,35:35='e',<459>,1:35]], created_at=[[@5,21:21='e',<459>,1:21]]}}, interface={resolved_at=[{name=resolved_at, table_ref=e}], created_at=[{name=created_at, table_ref=e}]}, table_alias={e=events}}, interface={delta_days=[{name=created_at, table_ref=b}, {name=resolved_at, table_ref=b}]}, table_alias={b=query0, base=query0}}}", extractor.getSymbolTable().toString());
	}

	@Test
	public void diffFamilySetFChainedCteRenamePathV0Test() {
		final String query = "with base as (select e.created_at as c1, e.resolved_at as r1 from events e), mid as (select b.c1 as created_at, b.r1 as resolved_at from base b) select datediff(day, m.created_at, m.resolved_at) as delta_days from mid m";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={with={1={cte={select={1={column={name=created_at, table_ref=e}, alias=c1}, 2={column={name=resolved_at, table_ref=e}, alias=r1}}, from={table={alias=e, table=events}}}, alias=base}, 2={cte={select={1={column={name=c1, table_ref=b}, alias=created_at}, 2={column={name=r1, table_ref=b}, alias=resolved_at}}, from={table={alias=b, table=base}}}, alias=mid}}, query={select={1={alias=delta_days, datetime_function={function_name=datediff, level={canonical=DAY, text=day, form=KEYWORD}, left={column={name=created_at, table_ref=m}}, right={column={name=resolved_at, table_ref=m}}}}}, from={table={alias=m, table=mid}}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[delta_days]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{events={resolved_at=[[@11,41:41='e',<459>,1:41]], created_at=[[@5,21:21='e',<459>,1:21]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={c1=[[@9,37:38='c1',<459>,1:37], [@25,92:92='b',<459>,1:92]], r1=[[@15,58:59='r1',<459>,1:58], [@31,112:112='b',<459>,1:112]]}, query1={created_at=[[@29,100:109='created_at',<459>,1:100], [@45,166:166='m',<459>,1:166]], resolved_at=[[@35,120:130='resolved_at',<459>,1:120], [@49,180:180='m',<459>,1:180]]}, query2={delta_days=[[@54,198:207='delta_days',<459>,1:198]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query2={context_list={base=query0, mid=query1, m=query1}, query_dictionary={delta_days=[[@54,198:207='delta_days',<459>,1:198]]}, def_query1={context_list={base=query0, b=query0}, query_dictionary={resolved_at=[[@35,120:130='resolved_at',<459>,1:120], [@49,180:180='m',<459>,1:180]], created_at=[[@29,100:109='created_at',<459>,1:100], [@45,166:166='m',<459>,1:166]]}, interface={resolved_at=[{name=r1, table_ref=b}], created_at=[{name=c1, table_ref=b}]}, table_alias={b=query0, base=query0}}, def_query0={query_dictionary={c1=[[@9,37:38='c1',<459>,1:37], [@25,92:92='b',<459>,1:92]], r1=[[@15,58:59='r1',<459>,1:58], [@31,112:112='b',<459>,1:112]]}, table_dictionary={events={resolved_at=[[@11,41:41='e',<459>,1:41]], created_at=[[@5,21:21='e',<459>,1:21]]}}, interface={c1=[{name=created_at, table_ref=e}], r1=[{name=resolved_at, table_ref=e}]}, table_alias={e=events}}, interface={delta_days=[{name=created_at, table_ref=m}, {name=resolved_at, table_ref=m}]}, table_alias={mid=query1, m=query1, base=query0}}}", extractor.getSymbolTable().toString());
	}

	@Test
	public void diffFamilySetFScalarSubquerySuppliesLeftV0Test() {
		final String query = "select datediff(day, (select max(i.created_at) from history i), a.resolved_at) as delta_days from events a";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=delta_days, datetime_function={function_name=datediff, level={canonical=DAY, text=day, form=KEYWORD}, left={lookup={from={table={alias=i, table=history}}, select={1={function={function_name=max, qualifier=null, parameters={column={name=created_at, table_ref=i}}}}}}}, right={column={name=resolved_at, table_ref=a}}}}}, from={table={alias=a, table=events}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[delta_days]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{history={created_at=[[@9,33:33='i',<459>,1:33]]}, events={resolved_at=[[@18,64:64='a',<459>,1:64]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={unnamed_0=[[@12,45:45=')',<288>,1:45]]}, query2={delta_days=[[@23,82:91='delta_days',<459>,1:82]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query2={query_dictionary={delta_days=[[@23,82:91='delta_days',<459>,1:82]]}, table_dictionary={events={resolved_at=[[@18,64:64='a',<459>,1:64]]}}, dependent_queries={predicand1={query=query0, type=interface}}, def_query0={query_dictionary={unnamed_0=[[@12,45:45=')',<288>,1:45]]}, table_dictionary={history={created_at=[[@9,33:33='i',<459>,1:33]]}}, interface={unnamed_0=[{name=created_at, table_ref=i}]}, table_alias={i=history}}, interface={delta_days=[{name=resolved_at, table_ref=a}]}, table_alias={a=events}}}", extractor.getSymbolTable().toString());
	}

	@Test
	public void diffFamilySetFScalarSubquerySuppliesRightV0Test() {
		final String query = "select datediff(day, a.created_at, (select max(i.resolved_at) from history i)) as delta_days from events a";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=delta_days, datetime_function={function_name=datediff, level={canonical=DAY, text=day, form=KEYWORD}, left={column={name=created_at, table_ref=a}}, right={lookup={from={table={alias=i, table=history}}, select={1={function={function_name=max, qualifier=null, parameters={column={name=resolved_at, table_ref=i}}}}}}}}}}, from={table={alias=a, table=events}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[delta_days]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{history={resolved_at=[[@13,47:47='i',<459>,1:47]]}, events={created_at=[[@5,21:21='a',<459>,1:21]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={unnamed_0=[[@16,60:60=')',<288>,1:60]]}, query2={delta_days=[[@23,82:91='delta_days',<459>,1:82]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query2={query_dictionary={delta_days=[[@23,82:91='delta_days',<459>,1:82]]}, table_dictionary={events={created_at=[[@5,21:21='a',<459>,1:21]]}}, dependent_queries={predicand1={query=query0, type=interface}}, def_query0={query_dictionary={unnamed_0=[[@16,60:60=')',<288>,1:60]]}, table_dictionary={history={resolved_at=[[@13,47:47='i',<459>,1:47]]}}, interface={unnamed_0=[{name=resolved_at, table_ref=i}]}, table_alias={i=history}}, interface={delta_days=[{name=created_at, table_ref=a}]}, table_alias={a=events}}}", extractor.getSymbolTable().toString());
	}

	@Test
	public void diffFamilySetFCorrelatedScalarInLeftV0Test() {
		final String query = "select datediff(day, (select max(i.created_at) from history i where i.event_id = a.id), a.resolved_at) as delta_days from events a";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=delta_days, datetime_function={function_name=datediff, level={canonical=DAY, text=day, form=KEYWORD}, left={lookup={from={table={alias=i, table=history}}, where={condition={left={column={name=event_id, table_ref=i}}, right={column={name=id, table_ref=a}}, operator==}}, select={1={function={function_name=max, qualifier=null, parameters={column={name=created_at, table_ref=i}}}}}}}, right={column={name=resolved_at, table_ref=a}}}}}, from={table={alias=a, table=events}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[delta_days]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{history={event_id=[[@17,68:68='i',<459>,1:68]], created_at=[[@9,33:33='i',<459>,1:33]]}, events={resolved_at=[[@26,88:88='a',<459>,1:88]], id=[[@21,81:81='a',<459>,1:81]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={unnamed_0=[[@12,45:45=')',<288>,1:45]]}, query2={delta_days=[[@31,106:115='delta_days',<459>,1:106]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query2={query_dictionary={delta_days=[[@31,106:115='delta_days',<459>,1:106]]}, table_dictionary={events={resolved_at=[[@26,88:88='a',<459>,1:88]], id=[[@21,81:81='a',<459>,1:81]]}}, dependent_queries={predicand1={query=query0, type=interface}}, def_query0={query_dictionary={unnamed_0=[[@12,45:45=')',<288>,1:45]]}, table_dictionary={history={event_id=[[@17,68:68='i',<459>,1:68]], created_at=[[@9,33:33='i',<459>,1:33]]}}, filters=[{name=event_id, table_ref=i}, {name=id, table_ref=a}], interface={unnamed_0=[{name=created_at, table_ref=i}]}, table_alias={i=history}}, interface={delta_days=[{name=resolved_at, table_ref=a}]}, table_alias={a=events}}}", extractor.getSymbolTable().toString());
	}

	@Test
	public void diffFamilySetFCorrelatedScalarInRightV0Test() {
		final String query = "select datediff(day, a.created_at, (select max(i.resolved_at) from history i where i.event_id = a.id)) as delta_days from events a";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=delta_days, datetime_function={function_name=datediff, level={canonical=DAY, text=day, form=KEYWORD}, left={column={name=created_at, table_ref=a}}, right={lookup={from={table={alias=i, table=history}}, where={condition={left={column={name=event_id, table_ref=i}}, right={column={name=id, table_ref=a}}, operator==}}, select={1={function={function_name=max, qualifier=null, parameters={column={name=resolved_at, table_ref=i}}}}}}}}}}, from={table={alias=a, table=events}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[delta_days]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{history={event_id=[[@21,83:83='i',<459>,1:83]], resolved_at=[[@13,47:47='i',<459>,1:47]]}, events={created_at=[[@5,21:21='a',<459>,1:21]], id=[[@25,96:96='a',<459>,1:96]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={unnamed_0=[[@16,60:60=')',<288>,1:60]]}, query2={delta_days=[[@31,106:115='delta_days',<459>,1:106]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query2={query_dictionary={delta_days=[[@31,106:115='delta_days',<459>,1:106]]}, table_dictionary={events={created_at=[[@5,21:21='a',<459>,1:21]], id=[[@25,96:96='a',<459>,1:96]]}}, dependent_queries={predicand1={query=query0, type=interface}}, def_query0={query_dictionary={unnamed_0=[[@16,60:60=')',<288>,1:60]]}, table_dictionary={history={event_id=[[@21,83:83='i',<459>,1:83]], resolved_at=[[@13,47:47='i',<459>,1:47]]}}, filters=[{name=event_id, table_ref=i}, {name=id, table_ref=a}], interface={unnamed_0=[{name=resolved_at, table_ref=i}]}, table_alias={i=history}}, interface={delta_days=[{name=created_at, table_ref=a}]}, table_alias={a=events}}}", extractor.getSymbolTable().toString());
	}

	@Test
	public void dateaddUsesEarlierSelectAliasForIncrementOperandV0Test() {
		final String query = "select a.offset_days as inc_days, dateadd(day, inc_days, a.created_at) as shifted from events a";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=offset_days, table_ref=a}, alias=inc_days}, 2={alias=shifted, datetime_function={function_name=dateadd, level={canonical=DAY, text=day, form=KEYWORD}, increment={column={name=inc_days, table_ref=null}}, value={column={name=created_at, table_ref=a}}}}}, from={table={alias=a, table=events}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[inc_days, shifted]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{events={created_at=[[@13,57:57='a',<459>,1:57]], offset_days=[[@1,7:7='a',<459>,1:7]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={shifted=[[@18,74:80='shifted',<459>,1:74]], inc_days=[[@5,24:31='inc_days',<459>,1:24], [@11,47:54='inc_days',<459>,1:47]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={inc_days=[[@5,24:31='inc_days',<459>,1:24], [@11,47:54='inc_days',<459>,1:47]], shifted=[[@18,74:80='shifted',<459>,1:74]]}, table_dictionary={events={created_at=[[@13,57:57='a',<459>,1:57]], offset_days=[[@1,7:7='a',<459>,1:7]]}}, interface={inc_days=[{name=offset_days, table_ref=a}], shifted=[{name=inc_days, table_ref=query0}, {name=created_at, table_ref=a}]}, table_alias={a=events}}}", extractor.getSymbolTable().toString());
	}

	@Test
	public void dateaddUsesEarlierSelectAliasesForBothOperandsV0Test() {
		final String query = "select a.offset_days as inc_days, a.created_at as base_ts, dateadd(day, inc_days, base_ts) as shifted from events a";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=offset_days, table_ref=a}, alias=inc_days}, 2={column={name=created_at, table_ref=a}, alias=base_ts}, 3={alias=shifted, datetime_function={function_name=dateadd, level={canonical=DAY, text=day, form=KEYWORD}, increment={column={name=inc_days, table_ref=null}}, value={column={name=base_ts, table_ref=null}}}}}, from={table={alias=a, table=events}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[base_ts, inc_days, shifted]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{events={created_at=[[@7,34:34='a',<459>,1:34]], offset_days=[[@1,7:7='a',<459>,1:7]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={base_ts=[[@11,50:56='base_ts',<459>,1:50], [@19,82:88='base_ts',<459>,1:82]], shifted=[[@22,94:100='shifted',<459>,1:94]], inc_days=[[@5,24:31='inc_days',<459>,1:24], [@17,72:79='inc_days',<459>,1:72]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={base_ts=[[@11,50:56='base_ts',<459>,1:50], [@19,82:88='base_ts',<459>,1:82]], inc_days=[[@5,24:31='inc_days',<459>,1:24], [@17,72:79='inc_days',<459>,1:72]], shifted=[[@22,94:100='shifted',<459>,1:94]]}, table_dictionary={events={created_at=[[@7,34:34='a',<459>,1:34]], offset_days=[[@1,7:7='a',<459>,1:7]]}}, interface={base_ts=[{name=created_at, table_ref=a}], inc_days=[{name=offset_days, table_ref=a}], shifted=[{name=inc_days, table_ref=query0}, {name=base_ts, table_ref=query0}]}, table_alias={a=events}}}", extractor.getSymbolTable().toString());
	}

	@Test
	public void datediffUsesEarlierSelectAliasesForBothOperandsV0Test() {
		final String query = "select a.created_at as start_ts, a.resolved_at as end_ts, datediff(day, start_ts, end_ts) as delta_days from events a";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=created_at, table_ref=a}, alias=start_ts}, 2={column={name=resolved_at, table_ref=a}, alias=end_ts}, 3={alias=delta_days, datetime_function={function_name=datediff, level={canonical=DAY, text=day, form=KEYWORD}, left={column={name=start_ts, table_ref=null}}, right={column={name=end_ts, table_ref=null}}}}}, from={table={alias=a, table=events}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[start_ts, end_ts, delta_days]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{events={resolved_at=[[@7,33:33='a',<459>,1:33]], created_at=[[@1,7:7='a',<459>,1:7]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={start_ts=[[@5,23:30='start_ts',<459>,1:23], [@17,72:79='start_ts',<459>,1:72]], end_ts=[[@11,50:55='end_ts',<459>,1:50], [@19,82:87='end_ts',<459>,1:82]], delta_days=[[@22,93:102='delta_days',<459>,1:93]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={start_ts=[[@5,23:30='start_ts',<459>,1:23], [@17,72:79='start_ts',<459>,1:72]], end_ts=[[@11,50:55='end_ts',<459>,1:50], [@19,82:87='end_ts',<459>,1:82]], delta_days=[[@22,93:102='delta_days',<459>,1:93]]}, table_dictionary={events={resolved_at=[[@7,33:33='a',<459>,1:33]], created_at=[[@1,7:7='a',<459>,1:7]]}}, interface={start_ts=[{name=created_at, table_ref=a}], end_ts=[{name=resolved_at, table_ref=a}], delta_days=[{name=start_ts, table_ref=query0}, {name=end_ts, table_ref=query0}]}, table_alias={a=events}}}", extractor.getSymbolTable().toString());
	}

	@Test
	public void datediffUsesEarlierDatetimeAliasAsOperandV0Test() {
		final String query = "select dateadd(day, 1, a.created_at) as shifted1, datediff(day, a.created_at, shifted1) as delta_days from events a";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=shifted1, datetime_function={function_name=dateadd, level={canonical=DAY, text=day, form=KEYWORD}, increment={literal=1}, value={column={name=created_at, table_ref=a}}}}, 2={alias=delta_days, datetime_function={function_name=datediff, level={canonical=DAY, text=day, form=KEYWORD}, left={column={name=created_at, table_ref=a}}, right={column={name=shifted1, table_ref=null}}}}}, from={table={alias=a, table=events}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[shifted1, delta_days]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{events={created_at=[[@7,23:23='a',<459>,1:23], [@18,64:64='a',<459>,1:64]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={shifted1=[[@12,40:47='shifted1',<459>,1:40], [@22,78:85='shifted1',<459>,1:78]], delta_days=[[@25,91:100='delta_days',<459>,1:91]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={shifted1=[[@12,40:47='shifted1',<459>,1:40], [@22,78:85='shifted1',<459>,1:78]], delta_days=[[@25,91:100='delta_days',<459>,1:91]]}, table_dictionary={events={created_at=[[@7,23:23='a',<459>,1:23], [@18,64:64='a',<459>,1:64]]}}, interface={shifted1=[{name=created_at, table_ref=a}], delta_days=[{name=created_at, table_ref=a}, {name=shifted1, table_ref=query0}]}, table_alias={a=events}}}", extractor.getSymbolTable().toString());
	}

	@Test
	public void diffFamilySetAQuotedLevelPreservedAcrossTimediffAndTimestampdiffV0Test() {
		final String query = "select timediff('day', u.started_at, u.ended_at) as td, timestampdiff('day', u.started_at, u.ended_at) as tsd from usage_events u";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=td, datetime_function={function_name=timediff, level={canonical=DAY, text=day, form=STRING}, left={column={name=started_at, table_ref=u}}, right={column={name=ended_at, table_ref=u}}}}, 2={alias=tsd, datetime_function={function_name=timestampdiff, level={canonical=DAY, text=day, form=STRING}, left={column={name=started_at, table_ref=u}}, right={column={name=ended_at, table_ref=u}}}}}, from={table={alias=u, table=usage_events}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[td, tsd]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{usage_events={started_at=[[@5,23:23='u',<459>,1:23], [@20,77:77='u',<459>,1:77]], ended_at=[[@9,37:37='u',<459>,1:37], [@24,91:91='u',<459>,1:91]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={td=[[@14,52:53='td',<459>,1:52]], tsd=[[@29,106:108='tsd',<459>,1:106]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={td=[[@14,52:53='td',<459>,1:52]], tsd=[[@29,106:108='tsd',<459>,1:106]]}, table_dictionary={usage_events={started_at=[[@5,23:23='u',<459>,1:23], [@20,77:77='u',<459>,1:77]], ended_at=[[@9,37:37='u',<459>,1:37], [@24,91:91='u',<459>,1:91]]}}, interface={td=[{name=started_at, table_ref=u}, {name=ended_at, table_ref=u}], tsd=[{name=started_at, table_ref=u}, {name=ended_at, table_ref=u}]}, table_alias={u=usage_events}}}", extractor.getSymbolTable().toString());
	}

	@Test
	public void diffFamilySetAMixedCaseInvocationsPreservedForTimediffAndTimestampdiffV0Test() {
		final String query = "select TiMeDiFf(day, u.started_at, u.ended_at) as td, TiMeStAmPdIfF(day, u.started_at, u.ended_at) as tsd from usage_events u";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=td, datetime_function={function_name=TiMeDiFf, level={canonical=DAY, text=day, form=KEYWORD}, left={column={name=started_at, table_ref=u}}, right={column={name=ended_at, table_ref=u}}}}, 2={alias=tsd, datetime_function={function_name=TiMeStAmPdIfF, level={canonical=DAY, text=day, form=KEYWORD}, left={column={name=started_at, table_ref=u}}, right={column={name=ended_at, table_ref=u}}}}}, from={table={alias=u, table=usage_events}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[td, tsd]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{usage_events={started_at=[[@5,21:21='u',<459>,1:21], [@20,73:73='u',<459>,1:73]], ended_at=[[@9,35:35='u',<459>,1:35], [@24,87:87='u',<459>,1:87]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={td=[[@14,50:51='td',<459>,1:50]], tsd=[[@29,102:104='tsd',<459>,1:102]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={td=[[@14,50:51='td',<459>,1:50]], tsd=[[@29,102:104='tsd',<459>,1:102]]}, table_dictionary={usage_events={started_at=[[@5,21:21='u',<459>,1:21], [@20,73:73='u',<459>,1:73]], ended_at=[[@9,35:35='u',<459>,1:35], [@24,87:87='u',<459>,1:87]]}}, interface={td=[{name=started_at, table_ref=u}, {name=ended_at, table_ref=u}], tsd=[{name=started_at, table_ref=u}, {name=ended_at, table_ref=u}]}, table_alias={u=usage_events}}}", extractor.getSymbolTable().toString());
	}

	@Test
	public void diffFamilySetAInvalidQuotedLevelProducesFatalForTimediffAndTimestampdiffV0Test() {
		String[][] cases = {{"TIMEDIFF", "usage_events", "u.started_at", "u.ended_at"}, {"TIMESTAMPDIFF", "events", "a.created_at", "a.updated_at"}};
		for (String[] testCase : cases) {
			String invocation = testCase[0];
			String table = testCase[1];
			String left = testCase[2];
			String right = testCase[3];
			String alias = "TIMEDIFF".equals(invocation) ? "u" : "a";
			String query = "select " + invocation + "('bogus', " + left + ", " + right + ") as delta from " + table + " " + alias;
			SqlParserAccess parser = new SqlParserAccess(false, false, false);
			parser.executeTheParse(query, "SQL");
			Assert.assertEquals(query, 0, parser.getParser().getNumberOfSyntaxErrors());
			Assert.assertEquals(query + ": " + parser.getAllDiagnostics(), 2, parser.getAllDiagnostics().size());
			assertSnowflakeGrammarWarningAt(parser, "snowflake_datetime_diff_expression", invocationOffset(query, invocation));
			int partPos = query.toLowerCase().indexOf("'bogus'");
			assertFatalDiagnosticAtPositionWithFullMessage(parser.getSnippet(), "INVALID_DATETIME_PART",
					"Invalid date/time part 'bogus' for " + invocation + " at (l:1 c:" + partPos + ").",
					"bogus", 1, partPos);
			Assert.assertFalse(parser.getSnippet().getSymbolTable().toString().contains("bogus"));
		}
	}

	@Test
	public void diffFamilySetBLeftLRightLV0Test() {
		final String query = "select datediff(day, '2024-01-01', '2024-01-01') as delta_days from events e";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=delta_days, datetime_function={function_name=datediff, level={canonical=DAY, text=day, form=KEYWORD}, left={literal='2024-01-01'}, right={literal='2024-01-01'}}}}, from={table={alias=e, table=events}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[delta_days]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{events={}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={delta_days=[[@10,52:61='delta_days',<459>,1:52]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={delta_days=[[@10,52:61='delta_days',<459>,1:52]]}, table_dictionary={events={}}, interface={delta_days=[]}, table_alias={e=events}}}", extractor.getSymbolTable().toString());
	}

	@Test
	public void diffFamilySetBLeftLRightCQV0Test() {
		final String query = "select datediff(day, '2024-01-01', e.created_at) as delta_days from events e";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=delta_days, datetime_function={function_name=datediff, level={canonical=DAY, text=day, form=KEYWORD}, left={literal='2024-01-01'}, right={column={name=created_at, table_ref=e}}}}}, from={table={alias=e, table=events}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[delta_days]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{events={created_at=[[@7,35:35='e',<459>,1:35]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={delta_days=[[@12,52:61='delta_days',<459>,1:52]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={delta_days=[[@12,52:61='delta_days',<459>,1:52]]}, table_dictionary={events={created_at=[[@7,35:35='e',<459>,1:35]]}}, interface={delta_days=[{name=created_at, table_ref=e}]}, table_alias={e=events}}}", extractor.getSymbolTable().toString());
	}

	@Test
	public void diffFamilySetBLeftLRightCUV0Test() {
		final String query = "select datediff(day, '2024-01-01', created_at) as delta_days from events e";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=delta_days, datetime_function={function_name=datediff, level={canonical=DAY, text=day, form=KEYWORD}, left={literal='2024-01-01'}, right={column={name=created_at, table_ref=null}}}}}, from={table={alias=e, table=events}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[delta_days]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{events={created_at=[[@7,35:44='created_at',<459>,1:35]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={delta_days=[[@10,50:59='delta_days',<459>,1:50]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={delta_days=[[@10,50:59='delta_days',<459>,1:50]]}, table_dictionary={events={created_at=[[@7,35:44='created_at',<459>,1:35]]}}, interface={delta_days=[{name=created_at, table_ref=events}]}, table_alias={e=events}}}", extractor.getSymbolTable().toString());
	}

	@Test
	public void diffFamilySetBLeftLRightFQV0Test() {
		final String query = "select datediff(day, '2024-01-01', dateadd(day, e.offset_days, e.created_at)) as delta_days from events e";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=delta_days, datetime_function={function_name=datediff, level={canonical=DAY, text=day, form=KEYWORD}, left={literal='2024-01-01'}, right={datetime_function={function_name=dateadd, level={canonical=DAY, text=day, form=KEYWORD}, increment={column={name=offset_days, table_ref=e}}, value={column={name=created_at, table_ref=e}}}}}}}, from={table={alias=e, table=events}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[delta_days]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{events={created_at=[[@15,63:63='e',<459>,1:63]], offset_days=[[@11,48:48='e',<459>,1:48]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={delta_days=[[@21,81:90='delta_days',<459>,1:81]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={delta_days=[[@21,81:90='delta_days',<459>,1:81]]}, table_dictionary={events={created_at=[[@15,63:63='e',<459>,1:63]], offset_days=[[@11,48:48='e',<459>,1:48]]}}, interface={delta_days=[{name=offset_days, table_ref=e}, {name=created_at, table_ref=e}]}, table_alias={e=events}}}", extractor.getSymbolTable().toString());
	}

	@Test
	public void diffFamilySetBLeftLRightFUV0Test() {
		final String query = "select datediff(day, '2024-01-01', dateadd(day, offset_days, created_at)) as delta_days from events e";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=delta_days, datetime_function={function_name=datediff, level={canonical=DAY, text=day, form=KEYWORD}, left={literal='2024-01-01'}, right={datetime_function={function_name=dateadd, level={canonical=DAY, text=day, form=KEYWORD}, increment={column={name=offset_days, table_ref=null}}, value={column={name=created_at, table_ref=null}}}}}}}, from={table={alias=e, table=events}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[delta_days]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{events={created_at=[[@13,61:70='created_at',<459>,1:61]], offset_days=[[@11,48:58='offset_days',<459>,1:48]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={delta_days=[[@17,77:86='delta_days',<459>,1:77]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={delta_days=[[@17,77:86='delta_days',<459>,1:77]]}, table_dictionary={events={created_at=[[@13,61:70='created_at',<459>,1:61]], offset_days=[[@11,48:58='offset_days',<459>,1:48]]}}, interface={delta_days=[{name=offset_days, table_ref=events}, {name=created_at, table_ref=events}]}, table_alias={e=events}}}", extractor.getSymbolTable().toString());
	}

	@Test
	public void diffFamilySetBLeftCQRightLV0Test() {
		final String query = "select datediff(day, e.created_at, '2024-01-01') as delta_days from events e";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=delta_days, datetime_function={function_name=datediff, level={canonical=DAY, text=day, form=KEYWORD}, left={column={name=created_at, table_ref=e}}, right={literal='2024-01-01'}}}}, from={table={alias=e, table=events}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[delta_days]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{events={created_at=[[@5,21:21='e',<459>,1:21]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={delta_days=[[@12,52:61='delta_days',<459>,1:52]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={delta_days=[[@12,52:61='delta_days',<459>,1:52]]}, table_dictionary={events={created_at=[[@5,21:21='e',<459>,1:21]]}}, interface={delta_days=[{name=created_at, table_ref=e}]}, table_alias={e=events}}}", extractor.getSymbolTable().toString());
	}

	@Test
	public void diffFamilySetBLeftCQRightCQV0Test() {
		final String query = "select datediff(day, e.created_at, e.created_at) as delta_days from events e";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=delta_days, datetime_function={function_name=datediff, level={canonical=DAY, text=day, form=KEYWORD}, left={column={name=created_at, table_ref=e}}, right={column={name=created_at, table_ref=e}}}}}, from={table={alias=e, table=events}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[delta_days]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{events={created_at=[[@5,21:21='e',<459>,1:21], [@9,35:35='e',<459>,1:35]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={delta_days=[[@14,52:61='delta_days',<459>,1:52]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={delta_days=[[@14,52:61='delta_days',<459>,1:52]]}, table_dictionary={events={created_at=[[@5,21:21='e',<459>,1:21], [@9,35:35='e',<459>,1:35]]}}, interface={delta_days=[{name=created_at, table_ref=e}]}, table_alias={e=events}}}", extractor.getSymbolTable().toString());
	}

	@Test
	public void diffFamilySetBLeftCQRightCUV0Test() {
		final String query = "select datediff(day, e.created_at, created_at) as delta_days from events e";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=delta_days, datetime_function={function_name=datediff, level={canonical=DAY, text=day, form=KEYWORD}, left={column={name=created_at, table_ref=e}}, right={column={name=created_at, table_ref=null}}}}}, from={table={alias=e, table=events}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[delta_days]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{events={created_at=[[@5,21:21='e',<459>,1:21], [@9,35:44='created_at',<459>,1:35]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={delta_days=[[@12,50:59='delta_days',<459>,1:50]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={delta_days=[[@12,50:59='delta_days',<459>,1:50]]}, table_dictionary={events={created_at=[[@5,21:21='e',<459>,1:21], [@9,35:44='created_at',<459>,1:35]]}}, interface={delta_days=[{name=created_at, table_ref=e}, {name=created_at, table_ref=events}]}, table_alias={e=events}}}", extractor.getSymbolTable().toString());
	}

	@Test
	public void diffFamilySetBLeftCQRightFQV0Test() {
		final String query = "select datediff(day, e.created_at, dateadd(day, e.offset_days, e.created_at)) as delta_days from events e";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=delta_days, datetime_function={function_name=datediff, level={canonical=DAY, text=day, form=KEYWORD}, left={column={name=created_at, table_ref=e}}, right={datetime_function={function_name=dateadd, level={canonical=DAY, text=day, form=KEYWORD}, increment={column={name=offset_days, table_ref=e}}, value={column={name=created_at, table_ref=e}}}}}}}, from={table={alias=e, table=events}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[delta_days]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{events={created_at=[[@5,21:21='e',<459>,1:21], [@17,63:63='e',<459>,1:63]], offset_days=[[@13,48:48='e',<459>,1:48]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={delta_days=[[@23,81:90='delta_days',<459>,1:81]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={delta_days=[[@23,81:90='delta_days',<459>,1:81]]}, table_dictionary={events={created_at=[[@5,21:21='e',<459>,1:21], [@17,63:63='e',<459>,1:63]], offset_days=[[@13,48:48='e',<459>,1:48]]}}, interface={delta_days=[{name=created_at, table_ref=e}, {name=offset_days, table_ref=e}]}, table_alias={e=events}}}", extractor.getSymbolTable().toString());
	}

	@Test
	public void diffFamilySetBLeftCQRightFUV0Test() {
		final String query = "select datediff(day, e.created_at, dateadd(day, offset_days, created_at)) as delta_days from events e";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=delta_days, datetime_function={function_name=datediff, level={canonical=DAY, text=day, form=KEYWORD}, left={column={name=created_at, table_ref=e}}, right={datetime_function={function_name=dateadd, level={canonical=DAY, text=day, form=KEYWORD}, increment={column={name=offset_days, table_ref=null}}, value={column={name=created_at, table_ref=null}}}}}}}, from={table={alias=e, table=events}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[delta_days]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{events={created_at=[[@5,21:21='e',<459>,1:21], [@15,61:70='created_at',<459>,1:61]], offset_days=[[@13,48:58='offset_days',<459>,1:48]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={delta_days=[[@19,77:86='delta_days',<459>,1:77]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={delta_days=[[@19,77:86='delta_days',<459>,1:77]]}, table_dictionary={events={created_at=[[@5,21:21='e',<459>,1:21], [@15,61:70='created_at',<459>,1:61]], offset_days=[[@13,48:58='offset_days',<459>,1:48]]}}, interface={delta_days=[{name=created_at, table_ref=e}, {name=offset_days, table_ref=events}, {name=created_at, table_ref=events}]}, table_alias={e=events}}}", extractor.getSymbolTable().toString());
	}

	@Test
	public void diffFamilySetBLeftCURightLV0Test() {
		final String query = "select datediff(day, created_at, '2024-01-01') as delta_days from events e";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=delta_days, datetime_function={function_name=datediff, level={canonical=DAY, text=day, form=KEYWORD}, left={column={name=created_at, table_ref=null}}, right={literal='2024-01-01'}}}}, from={table={alias=e, table=events}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[delta_days]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{events={created_at=[[@5,21:30='created_at',<459>,1:21]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={delta_days=[[@10,50:59='delta_days',<459>,1:50]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={delta_days=[[@10,50:59='delta_days',<459>,1:50]]}, table_dictionary={events={created_at=[[@5,21:30='created_at',<459>,1:21]]}}, interface={delta_days=[{name=created_at, table_ref=events}]}, table_alias={e=events}}}", extractor.getSymbolTable().toString());
	}

	@Test
	public void diffFamilySetBLeftCURightCQV0Test() {
		final String query = "select datediff(day, created_at, e.created_at) as delta_days from events e";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=delta_days, datetime_function={function_name=datediff, level={canonical=DAY, text=day, form=KEYWORD}, left={column={name=created_at, table_ref=null}}, right={column={name=created_at, table_ref=e}}}}}, from={table={alias=e, table=events}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[delta_days]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{events={created_at=[[@7,33:33='e',<459>,1:33], [@5,21:30='created_at',<459>,1:21]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={delta_days=[[@12,50:59='delta_days',<459>,1:50]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={delta_days=[[@12,50:59='delta_days',<459>,1:50]]}, table_dictionary={events={created_at=[[@7,33:33='e',<459>,1:33], [@5,21:30='created_at',<459>,1:21]]}}, interface={delta_days=[{name=created_at, table_ref=events}, {name=created_at, table_ref=e}]}, table_alias={e=events}}}", extractor.getSymbolTable().toString());
	}

	@Test
	public void diffFamilySetBLeftCURightCUV0Test() {
		final String query = "select datediff(day, created_at, created_at) as delta_days from events e";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=delta_days, datetime_function={function_name=datediff, level={canonical=DAY, text=day, form=KEYWORD}, left={column={name=created_at, table_ref=null}}, right={column={name=created_at, table_ref=null}}}}}, from={table={alias=e, table=events}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[delta_days]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{events={created_at=[[@5,21:30='created_at',<459>,1:21], [@7,33:42='created_at',<459>,1:33]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={delta_days=[[@10,48:57='delta_days',<459>,1:48]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={delta_days=[[@10,48:57='delta_days',<459>,1:48]]}, table_dictionary={events={created_at=[[@5,21:30='created_at',<459>,1:21], [@7,33:42='created_at',<459>,1:33]]}}, interface={delta_days=[{name=created_at, table_ref=events}]}, table_alias={e=events}}}", extractor.getSymbolTable().toString());
	}

	@Test
	public void diffFamilySetBLeftCURightFQV0Test() {
		final String query = "select datediff(day, created_at, dateadd(day, e.offset_days, e.created_at)) as delta_days from events e";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=delta_days, datetime_function={function_name=datediff, level={canonical=DAY, text=day, form=KEYWORD}, left={column={name=created_at, table_ref=null}}, right={datetime_function={function_name=dateadd, level={canonical=DAY, text=day, form=KEYWORD}, increment={column={name=offset_days, table_ref=e}}, value={column={name=created_at, table_ref=e}}}}}}}, from={table={alias=e, table=events}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[delta_days]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{events={created_at=[[@15,61:61='e',<459>,1:61], [@5,21:30='created_at',<459>,1:21]], offset_days=[[@11,46:46='e',<459>,1:46]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={delta_days=[[@21,79:88='delta_days',<459>,1:79]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={delta_days=[[@21,79:88='delta_days',<459>,1:79]]}, table_dictionary={events={created_at=[[@15,61:61='e',<459>,1:61], [@5,21:30='created_at',<459>,1:21]], offset_days=[[@11,46:46='e',<459>,1:46]]}}, interface={delta_days=[{name=created_at, table_ref=events}, {name=offset_days, table_ref=e}, {name=created_at, table_ref=e}]}, table_alias={e=events}}}", extractor.getSymbolTable().toString());
	}

	@Test
	public void diffFamilySetBLeftCURightFUV0Test() {
		final String query = "select datediff(day, created_at, dateadd(day, offset_days, created_at)) as delta_days from events e";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=delta_days, datetime_function={function_name=datediff, level={canonical=DAY, text=day, form=KEYWORD}, left={column={name=created_at, table_ref=null}}, right={datetime_function={function_name=dateadd, level={canonical=DAY, text=day, form=KEYWORD}, increment={column={name=offset_days, table_ref=null}}, value={column={name=created_at, table_ref=null}}}}}}}, from={table={alias=e, table=events}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[delta_days]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{events={created_at=[[@5,21:30='created_at',<459>,1:21], [@13,59:68='created_at',<459>,1:59]], offset_days=[[@11,46:56='offset_days',<459>,1:46]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={delta_days=[[@17,75:84='delta_days',<459>,1:75]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={delta_days=[[@17,75:84='delta_days',<459>,1:75]]}, table_dictionary={events={created_at=[[@5,21:30='created_at',<459>,1:21], [@13,59:68='created_at',<459>,1:59]], offset_days=[[@11,46:56='offset_days',<459>,1:46]]}}, interface={delta_days=[{name=created_at, table_ref=events}, {name=offset_days, table_ref=events}]}, table_alias={e=events}}}", extractor.getSymbolTable().toString());
	}

	@Test
	public void diffFamilySetBLeftFQRightLV0Test() {
		final String query = "select datediff(day, dateadd(day, e.offset_days, e.created_at), '2024-01-01') as delta_days from events e";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=delta_days, datetime_function={function_name=datediff, level={canonical=DAY, text=day, form=KEYWORD}, left={datetime_function={function_name=dateadd, level={canonical=DAY, text=day, form=KEYWORD}, increment={column={name=offset_days, table_ref=e}}, value={column={name=created_at, table_ref=e}}}}, right={literal='2024-01-01'}}}}, from={table={alias=e, table=events}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[delta_days]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{events={created_at=[[@13,49:49='e',<459>,1:49]], offset_days=[[@9,34:34='e',<459>,1:34]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={delta_days=[[@21,81:90='delta_days',<459>,1:81]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={delta_days=[[@21,81:90='delta_days',<459>,1:81]]}, table_dictionary={events={created_at=[[@13,49:49='e',<459>,1:49]], offset_days=[[@9,34:34='e',<459>,1:34]]}}, interface={delta_days=[{name=offset_days, table_ref=e}, {name=created_at, table_ref=e}]}, table_alias={e=events}}}", extractor.getSymbolTable().toString());
	}

	@Test
	public void diffFamilySetBLeftFQRightCQV0Test() {
		final String query = "select datediff(day, dateadd(day, e.offset_days, e.created_at), e.created_at) as delta_days from events e";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=delta_days, datetime_function={function_name=datediff, level={canonical=DAY, text=day, form=KEYWORD}, left={datetime_function={function_name=dateadd, level={canonical=DAY, text=day, form=KEYWORD}, increment={column={name=offset_days, table_ref=e}}, value={column={name=created_at, table_ref=e}}}}, right={column={name=created_at, table_ref=e}}}}}, from={table={alias=e, table=events}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[delta_days]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{events={created_at=[[@13,49:49='e',<459>,1:49], [@18,64:64='e',<459>,1:64]], offset_days=[[@9,34:34='e',<459>,1:34]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={delta_days=[[@23,81:90='delta_days',<459>,1:81]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={delta_days=[[@23,81:90='delta_days',<459>,1:81]]}, table_dictionary={events={created_at=[[@13,49:49='e',<459>,1:49], [@18,64:64='e',<459>,1:64]], offset_days=[[@9,34:34='e',<459>,1:34]]}}, interface={delta_days=[{name=offset_days, table_ref=e}, {name=created_at, table_ref=e}]}, table_alias={e=events}}}", extractor.getSymbolTable().toString());
	}

	@Test
	public void diffFamilySetBLeftFQRightCUV0Test() {
		final String query = "select datediff(day, dateadd(day, e.offset_days, e.created_at), created_at) as delta_days from events e";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=delta_days, datetime_function={function_name=datediff, level={canonical=DAY, text=day, form=KEYWORD}, left={datetime_function={function_name=dateadd, level={canonical=DAY, text=day, form=KEYWORD}, increment={column={name=offset_days, table_ref=e}}, value={column={name=created_at, table_ref=e}}}}, right={column={name=created_at, table_ref=null}}}}}, from={table={alias=e, table=events}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[delta_days]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{events={created_at=[[@13,49:49='e',<459>,1:49], [@18,64:73='created_at',<459>,1:64]], offset_days=[[@9,34:34='e',<459>,1:34]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={delta_days=[[@21,79:88='delta_days',<459>,1:79]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={delta_days=[[@21,79:88='delta_days',<459>,1:79]]}, table_dictionary={events={created_at=[[@13,49:49='e',<459>,1:49], [@18,64:73='created_at',<459>,1:64]], offset_days=[[@9,34:34='e',<459>,1:34]]}}, interface={delta_days=[{name=offset_days, table_ref=e}, {name=created_at, table_ref=e}, {name=created_at, table_ref=events}]}, table_alias={e=events}}}", extractor.getSymbolTable().toString());
	}

	@Test
	public void diffFamilySetBLeftFQRightFQV0Test() {
		final String query = "select datediff(day, dateadd(day, e.offset_days, e.created_at), dateadd(day, e.offset_days, e.created_at)) as delta_days from events e";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=delta_days, datetime_function={function_name=datediff, level={canonical=DAY, text=day, form=KEYWORD}, left={datetime_function={function_name=dateadd, level={canonical=DAY, text=day, form=KEYWORD}, increment={column={name=offset_days, table_ref=e}}, value={column={name=created_at, table_ref=e}}}}, right={datetime_function={function_name=dateadd, level={canonical=DAY, text=day, form=KEYWORD}, increment={column={name=offset_days, table_ref=e}}, value={column={name=created_at, table_ref=e}}}}}}}, from={table={alias=e, table=events}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[delta_days]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{events={created_at=[[@13,49:49='e',<459>,1:49], [@26,92:92='e',<459>,1:92]], offset_days=[[@9,34:34='e',<459>,1:34], [@22,77:77='e',<459>,1:77]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={delta_days=[[@32,110:119='delta_days',<459>,1:110]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={delta_days=[[@32,110:119='delta_days',<459>,1:110]]}, table_dictionary={events={created_at=[[@13,49:49='e',<459>,1:49], [@26,92:92='e',<459>,1:92]], offset_days=[[@9,34:34='e',<459>,1:34], [@22,77:77='e',<459>,1:77]]}}, interface={delta_days=[{name=offset_days, table_ref=e}, {name=created_at, table_ref=e}]}, table_alias={e=events}}}", extractor.getSymbolTable().toString());
	}

	@Test
	public void diffFamilySetBLeftFQRightFUV0Test() {
		final String query = "select datediff(day, dateadd(day, e.offset_days, e.created_at), dateadd(day, offset_days, created_at)) as delta_days from events e";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=delta_days, datetime_function={function_name=datediff, level={canonical=DAY, text=day, form=KEYWORD}, left={datetime_function={function_name=dateadd, level={canonical=DAY, text=day, form=KEYWORD}, increment={column={name=offset_days, table_ref=e}}, value={column={name=created_at, table_ref=e}}}}, right={datetime_function={function_name=dateadd, level={canonical=DAY, text=day, form=KEYWORD}, increment={column={name=offset_days, table_ref=null}}, value={column={name=created_at, table_ref=null}}}}}}}, from={table={alias=e, table=events}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[delta_days]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{events={created_at=[[@13,49:49='e',<459>,1:49], [@24,90:99='created_at',<459>,1:90]], offset_days=[[@9,34:34='e',<459>,1:34], [@22,77:87='offset_days',<459>,1:77]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={delta_days=[[@28,106:115='delta_days',<459>,1:106]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={delta_days=[[@28,106:115='delta_days',<459>,1:106]]}, table_dictionary={events={created_at=[[@13,49:49='e',<459>,1:49], [@24,90:99='created_at',<459>,1:90]], offset_days=[[@9,34:34='e',<459>,1:34], [@22,77:87='offset_days',<459>,1:77]]}}, interface={delta_days=[{name=offset_days, table_ref=e}, {name=created_at, table_ref=e}, {name=offset_days, table_ref=events}, {name=created_at, table_ref=events}]}, table_alias={e=events}}}", extractor.getSymbolTable().toString());
	}

	@Test
	public void diffFamilySetBLeftFURightLV0Test() {
		final String query = "select datediff(day, dateadd(day, offset_days, created_at), '2024-01-01') as delta_days from events e";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=delta_days, datetime_function={function_name=datediff, level={canonical=DAY, text=day, form=KEYWORD}, left={datetime_function={function_name=dateadd, level={canonical=DAY, text=day, form=KEYWORD}, increment={column={name=offset_days, table_ref=null}}, value={column={name=created_at, table_ref=null}}}}, right={literal='2024-01-01'}}}}, from={table={alias=e, table=events}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[delta_days]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{events={created_at=[[@11,47:56='created_at',<459>,1:47]], offset_days=[[@9,34:44='offset_days',<459>,1:34]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={delta_days=[[@17,77:86='delta_days',<459>,1:77]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={delta_days=[[@17,77:86='delta_days',<459>,1:77]]}, table_dictionary={events={created_at=[[@11,47:56='created_at',<459>,1:47]], offset_days=[[@9,34:44='offset_days',<459>,1:34]]}}, interface={delta_days=[{name=offset_days, table_ref=events}, {name=created_at, table_ref=events}]}, table_alias={e=events}}}", extractor.getSymbolTable().toString());
	}

	@Test
	public void diffFamilySetBLeftFURightCQV0Test() {
		final String query = "select datediff(day, dateadd(day, offset_days, created_at), e.created_at) as delta_days from events e";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=delta_days, datetime_function={function_name=datediff, level={canonical=DAY, text=day, form=KEYWORD}, left={datetime_function={function_name=dateadd, level={canonical=DAY, text=day, form=KEYWORD}, increment={column={name=offset_days, table_ref=null}}, value={column={name=created_at, table_ref=null}}}}, right={column={name=created_at, table_ref=e}}}}}, from={table={alias=e, table=events}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[delta_days]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{events={created_at=[[@14,60:60='e',<459>,1:60], [@11,47:56='created_at',<459>,1:47]], offset_days=[[@9,34:44='offset_days',<459>,1:34]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={delta_days=[[@19,77:86='delta_days',<459>,1:77]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={delta_days=[[@19,77:86='delta_days',<459>,1:77]]}, table_dictionary={events={created_at=[[@14,60:60='e',<459>,1:60], [@11,47:56='created_at',<459>,1:47]], offset_days=[[@9,34:44='offset_days',<459>,1:34]]}}, interface={delta_days=[{name=offset_days, table_ref=events}, {name=created_at, table_ref=events}, {name=created_at, table_ref=e}]}, table_alias={e=events}}}", extractor.getSymbolTable().toString());
	}

	@Test
	public void diffFamilySetBLeftFURightCUV0Test() {
		final String query = "select datediff(day, dateadd(day, offset_days, created_at), created_at) as delta_days from events e";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=delta_days, datetime_function={function_name=datediff, level={canonical=DAY, text=day, form=KEYWORD}, left={datetime_function={function_name=dateadd, level={canonical=DAY, text=day, form=KEYWORD}, increment={column={name=offset_days, table_ref=null}}, value={column={name=created_at, table_ref=null}}}}, right={column={name=created_at, table_ref=null}}}}}, from={table={alias=e, table=events}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[delta_days]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{events={created_at=[[@11,47:56='created_at',<459>,1:47], [@14,60:69='created_at',<459>,1:60]], offset_days=[[@9,34:44='offset_days',<459>,1:34]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={delta_days=[[@17,75:84='delta_days',<459>,1:75]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={delta_days=[[@17,75:84='delta_days',<459>,1:75]]}, table_dictionary={events={created_at=[[@11,47:56='created_at',<459>,1:47], [@14,60:69='created_at',<459>,1:60]], offset_days=[[@9,34:44='offset_days',<459>,1:34]]}}, interface={delta_days=[{name=offset_days, table_ref=events}, {name=created_at, table_ref=events}]}, table_alias={e=events}}}", extractor.getSymbolTable().toString());
	}

	@Test
	public void diffFamilySetBLeftFURightFQV0Test() {
		final String query = "select datediff(day, dateadd(day, offset_days, created_at), dateadd(day, e.offset_days, e.created_at)) as delta_days from events e";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=delta_days, datetime_function={function_name=datediff, level={canonical=DAY, text=day, form=KEYWORD}, left={datetime_function={function_name=dateadd, level={canonical=DAY, text=day, form=KEYWORD}, increment={column={name=offset_days, table_ref=null}}, value={column={name=created_at, table_ref=null}}}}, right={datetime_function={function_name=dateadd, level={canonical=DAY, text=day, form=KEYWORD}, increment={column={name=offset_days, table_ref=e}}, value={column={name=created_at, table_ref=e}}}}}}}, from={table={alias=e, table=events}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[delta_days]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{events={created_at=[[@22,88:88='e',<459>,1:88], [@11,47:56='created_at',<459>,1:47]], offset_days=[[@18,73:73='e',<459>,1:73], [@9,34:44='offset_days',<459>,1:34]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={delta_days=[[@28,106:115='delta_days',<459>,1:106]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={delta_days=[[@28,106:115='delta_days',<459>,1:106]]}, table_dictionary={events={created_at=[[@22,88:88='e',<459>,1:88], [@11,47:56='created_at',<459>,1:47]], offset_days=[[@18,73:73='e',<459>,1:73], [@9,34:44='offset_days',<459>,1:34]]}}, interface={delta_days=[{name=offset_days, table_ref=events}, {name=created_at, table_ref=events}, {name=offset_days, table_ref=e}, {name=created_at, table_ref=e}]}, table_alias={e=events}}}", extractor.getSymbolTable().toString());
	}

	@Test
	public void diffFamilySetBLeftFURightFUV0Test() {
		final String query = "select datediff(day, dateadd(day, offset_days, created_at), dateadd(day, offset_days, created_at)) as delta_days from events e";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=delta_days, datetime_function={function_name=datediff, level={canonical=DAY, text=day, form=KEYWORD}, left={datetime_function={function_name=dateadd, level={canonical=DAY, text=day, form=KEYWORD}, increment={column={name=offset_days, table_ref=null}}, value={column={name=created_at, table_ref=null}}}}, right={datetime_function={function_name=dateadd, level={canonical=DAY, text=day, form=KEYWORD}, increment={column={name=offset_days, table_ref=null}}, value={column={name=created_at, table_ref=null}}}}}}}, from={table={alias=e, table=events}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[delta_days]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{events={created_at=[[@11,47:56='created_at',<459>,1:47], [@20,86:95='created_at',<459>,1:86]], offset_days=[[@9,34:44='offset_days',<459>,1:34], [@18,73:83='offset_days',<459>,1:73]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={delta_days=[[@24,102:111='delta_days',<459>,1:102]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={delta_days=[[@24,102:111='delta_days',<459>,1:102]]}, table_dictionary={events={created_at=[[@11,47:56='created_at',<459>,1:47], [@20,86:95='created_at',<459>,1:86]], offset_days=[[@9,34:44='offset_days',<459>,1:34], [@18,73:83='offset_days',<459>,1:73]]}}, interface={delta_days=[{name=offset_days, table_ref=events}, {name=created_at, table_ref=events}]}, table_alias={e=events}}}", extractor.getSymbolTable().toString());
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
		Assert.assertEquals("Table Dictionary is wrong", "{events={created_at=[[@7,24:24='a',<459>,1:24]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={shifted=[[@12,41:47='shifted',<459>,1:41]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={shifted=[[@12,41:47='shifted',<459>,1:41]]}, table_dictionary={events={created_at=[[@7,24:24='a',<459>,1:24]]}}, interface={shifted=[{name=created_at, table_ref=a}]}, table_alias={a=events}}}", extractor.getSymbolTable().toString());
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
		Assert.assertEquals("Table Dictionary is wrong", "{events={created_at=[[@7,24:33='created_at',<459>,1:24]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={shifted=[[@10,39:45='shifted',<459>,1:39]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={shifted=[[@10,39:45='shifted',<459>,1:39]]}, table_dictionary={events={created_at=[[@7,24:33='created_at',<459>,1:24]]}}, interface={shifted=[{name=created_at, table_ref=events}]}, table_alias={a=events}}}", extractor.getSymbolTable().toString());
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
		Assert.assertEquals("Table Dictionary is wrong", "{accounts={extra_days=[[@9,36:36='b',<459>,1:36]], id=[[@31,132:132='b',<459>,1:132]]}, events={account_id=[[@27,117:117='a',<459>,1:117]], offset_days=[[@5,20:20='a',<459>,1:20]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={shifted=[[@19,76:82='shifted',<459>,1:76]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={shifted=[[@19,76:82='shifted',<459>,1:76]]}, table_dictionary={accounts={extra_days=[[@9,36:36='b',<459>,1:36]], id=[[@31,132:132='b',<459>,1:132]]}, events={account_id=[[@27,117:117='a',<459>,1:117]], offset_days=[[@5,20:20='a',<459>,1:20]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}], interface={shifted=[{name=offset_days, table_ref=a}, {name=extra_days, table_ref=b}]}, table_alias={a=events, b=accounts}}}", extractor.getSymbolTable().toString());
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
		Assert.assertEquals("Table Dictionary is wrong", "{accounts={created_at=[[@9,35:35='b',<459>,1:35]], id=[[@26,108:108='b',<459>,1:108]]}, events={account_id=[[@22,93:93='a',<459>,1:93]], offset_days=[[@5,20:20='a',<459>,1:20]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={shifted=[[@14,52:58='shifted',<459>,1:52]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={shifted=[[@14,52:58='shifted',<459>,1:52]]}, table_dictionary={accounts={created_at=[[@9,35:35='b',<459>,1:35]], id=[[@26,108:108='b',<459>,1:108]]}, events={account_id=[[@22,93:93='a',<459>,1:93]], offset_days=[[@5,20:20='a',<459>,1:20]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}], interface={shifted=[{name=offset_days, table_ref=a}, {name=created_at, table_ref=b}]}, table_alias={a=events, b=accounts}}}", extractor.getSymbolTable().toString());
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
		Assert.assertEquals("Table Dictionary is wrong", "{accounts={adjust_days=[[@24,84:84='b',<459>,1:84]], created_at=[[@15,54:54='b',<459>,1:54]], extra_days=[[@9,36:36='b',<459>,1:36]], id=[[@42,159:159='b',<459>,1:159]]}, events={base_days=[[@20,70:70='a',<459>,1:70]], account_id=[[@38,144:144='a',<459>,1:144]], offset_days=[[@5,20:20='a',<459>,1:20]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={shifted=[[@30,103:109='shifted',<459>,1:103]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={shifted=[[@30,103:109='shifted',<459>,1:103]]}, table_dictionary={accounts={adjust_days=[[@24,84:84='b',<459>,1:84]], created_at=[[@15,54:54='b',<459>,1:54]], extra_days=[[@9,36:36='b',<459>,1:36]], id=[[@42,159:159='b',<459>,1:159]]}, events={base_days=[[@20,70:70='a',<459>,1:70]], account_id=[[@38,144:144='a',<459>,1:144]], offset_days=[[@5,20:20='a',<459>,1:20]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}], interface={shifted=[{name=offset_days, table_ref=a}, {name=extra_days, table_ref=b}, {name=created_at, table_ref=b}, {name=base_days, table_ref=a}, {name=adjust_days, table_ref=b}]}, table_alias={a=events, b=accounts}}}", extractor.getSymbolTable().toString());
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
		Assert.assertEquals("Table Dictionary is wrong", "{events={created_at=[[@7,23:23='a',<459>,1:23]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={shifted=[[@12,40:46='shifted',<459>,1:40]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={shifted=[[@12,40:46='shifted',<459>,1:40]]}, table_dictionary={events={created_at=[[@7,23:23='a',<459>,1:23]]}}, interface={shifted=[{name=created_at, table_ref=a}]}, table_alias={a=events}}}", extractor.getSymbolTable().toString());
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
		Assert.assertEquals("Table Dictionary is wrong", "{events={created_at=[[@7,23:32='created_at',<459>,1:23]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={shifted=[[@10,38:44='shifted',<459>,1:38]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={shifted=[[@10,38:44='shifted',<459>,1:38]]}, table_dictionary={events={created_at=[[@7,23:32='created_at',<459>,1:23]]}}, interface={shifted=[{name=created_at, table_ref=events}]}, table_alias={a=events}}}", extractor.getSymbolTable().toString());
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
		Assert.assertEquals("Table Dictionary is wrong", "{events={base_days=[[@12,39:39='a',<459>,1:39]], adjust_days=[[@16,53:53='a',<459>,1:53]], created_at=[[@7,23:23='a',<459>,1:23]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={shifted=[[@22,72:78='shifted',<459>,1:72]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={shifted=[[@22,72:78='shifted',<459>,1:72]]}, table_dictionary={events={base_days=[[@12,39:39='a',<459>,1:39]], adjust_days=[[@16,53:53='a',<459>,1:53]], created_at=[[@7,23:23='a',<459>,1:23]]}}, interface={shifted=[{name=created_at, table_ref=a}, {name=base_days, table_ref=a}, {name=adjust_days, table_ref=a}]}, table_alias={a=events}}}", extractor.getSymbolTable().toString());
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
		Assert.assertEquals("Table Dictionary is wrong", "{events={base_days=[[@10,37:45='base_days',<459>,1:37]], adjust_days=[[@12,49:59='adjust_days',<459>,1:49]], created_at=[[@7,23:32='created_at',<459>,1:23]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={shifted=[[@16,66:72='shifted',<459>,1:66]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={shifted=[[@16,66:72='shifted',<459>,1:66]]}, table_dictionary={events={base_days=[[@10,37:45='base_days',<459>,1:37]], adjust_days=[[@12,49:59='adjust_days',<459>,1:49]], created_at=[[@7,23:32='created_at',<459>,1:23]]}}, interface={shifted=[{name=created_at, table_ref=events}, {name=base_days, table_ref=events}, {name=adjust_days, table_ref=events}]}, table_alias={a=events}}}", extractor.getSymbolTable().toString());
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
		Assert.assertEquals("Table Dictionary is wrong", "{events={offset_days=[[@5,20:20='a',<459>,1:20]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={shifted=[[@13,57:63='shifted',<459>,1:57]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={shifted=[[@13,57:63='shifted',<459>,1:57]]}, table_dictionary={events={offset_days=[[@5,20:20='a',<459>,1:20]]}}, interface={shifted=[{name=offset_days, table_ref=a}]}, table_alias={a=events}}}", extractor.getSymbolTable().toString());
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
		Assert.assertEquals("Table Dictionary is wrong", "{events={created_at=[[@9,35:35='a',<459>,1:35]], offset_days=[[@5,20:20='a',<459>,1:20]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={shifted=[[@14,52:58='shifted',<459>,1:52]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={shifted=[[@14,52:58='shifted',<459>,1:52]]}, table_dictionary={events={created_at=[[@9,35:35='a',<459>,1:35]], offset_days=[[@5,20:20='a',<459>,1:20]]}}, interface={shifted=[{name=offset_days, table_ref=a}, {name=created_at, table_ref=a}]}, table_alias={a=events}}}", extractor.getSymbolTable().toString());
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
		Assert.assertEquals("Table Dictionary is wrong", "{events={created_at=[[@9,35:44='created_at',<459>,1:35]], offset_days=[[@5,20:20='a',<459>,1:20]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={shifted=[[@12,50:56='shifted',<459>,1:50]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={shifted=[[@12,50:56='shifted',<459>,1:50]]}, table_dictionary={events={created_at=[[@9,35:44='created_at',<459>,1:35]], offset_days=[[@5,20:20='a',<459>,1:20]]}}, interface={shifted=[{name=offset_days, table_ref=a}, {name=created_at, table_ref=events}]}, table_alias={a=events}}}", extractor.getSymbolTable().toString());
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
		Assert.assertEquals("Table Dictionary is wrong", "{events={base_days=[[@14,51:51='a',<459>,1:51]], adjust_days=[[@18,65:65='a',<459>,1:65]], created_at=[[@9,35:35='a',<459>,1:35]], offset_days=[[@5,20:20='a',<459>,1:20]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={shifted=[[@24,84:90='shifted',<459>,1:84]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={shifted=[[@24,84:90='shifted',<459>,1:84]]}, table_dictionary={events={base_days=[[@14,51:51='a',<459>,1:51]], adjust_days=[[@18,65:65='a',<459>,1:65]], created_at=[[@9,35:35='a',<459>,1:35]], offset_days=[[@5,20:20='a',<459>,1:20]]}}, interface={shifted=[{name=offset_days, table_ref=a}, {name=created_at, table_ref=a}, {name=base_days, table_ref=a}, {name=adjust_days, table_ref=a}]}, table_alias={a=events}}}", extractor.getSymbolTable().toString());
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
		Assert.assertEquals("Table Dictionary is wrong", "{events={base_days=[[@12,49:57='base_days',<459>,1:49]], adjust_days=[[@14,61:71='adjust_days',<459>,1:61]], created_at=[[@9,35:44='created_at',<459>,1:35]], offset_days=[[@5,20:20='a',<459>,1:20]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={shifted=[[@18,78:84='shifted',<459>,1:78]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={shifted=[[@18,78:84='shifted',<459>,1:78]]}, table_dictionary={events={base_days=[[@12,49:57='base_days',<459>,1:49]], adjust_days=[[@14,61:71='adjust_days',<459>,1:61]], created_at=[[@9,35:44='created_at',<459>,1:35]], offset_days=[[@5,20:20='a',<459>,1:20]]}}, interface={shifted=[{name=offset_days, table_ref=a}, {name=created_at, table_ref=events}, {name=base_days, table_ref=events}, {name=adjust_days, table_ref=events}]}, table_alias={a=events}}}", extractor.getSymbolTable().toString());
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
		Assert.assertEquals("Table Dictionary is wrong", "{events={offset_days=[[@5,20:30='offset_days',<459>,1:20]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={shifted=[[@11,55:61='shifted',<459>,1:55]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={shifted=[[@11,55:61='shifted',<459>,1:55]]}, table_dictionary={events={offset_days=[[@5,20:30='offset_days',<459>,1:20]]}}, interface={shifted=[{name=offset_days, table_ref=events}]}, table_alias={a=events}}}", extractor.getSymbolTable().toString());
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
		Assert.assertEquals("Table Dictionary is wrong", "{events={created_at=[[@7,33:33='a',<459>,1:33]], offset_days=[[@5,20:30='offset_days',<459>,1:20]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={shifted=[[@12,50:56='shifted',<459>,1:50]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={shifted=[[@12,50:56='shifted',<459>,1:50]]}, table_dictionary={events={created_at=[[@7,33:33='a',<459>,1:33]], offset_days=[[@5,20:30='offset_days',<459>,1:20]]}}, interface={shifted=[{name=offset_days, table_ref=events}, {name=created_at, table_ref=a}]}, table_alias={a=events}}}", extractor.getSymbolTable().toString());
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
		Assert.assertEquals("Table Dictionary is wrong", "{events={created_at=[[@7,33:42='created_at',<459>,1:33]], offset_days=[[@5,20:30='offset_days',<459>,1:20]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={shifted=[[@10,48:54='shifted',<459>,1:48]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={shifted=[[@10,48:54='shifted',<459>,1:48]]}, table_dictionary={events={created_at=[[@7,33:42='created_at',<459>,1:33]], offset_days=[[@5,20:30='offset_days',<459>,1:20]]}}, interface={shifted=[{name=offset_days, table_ref=events}, {name=created_at, table_ref=events}]}, table_alias={a=events}}}", extractor.getSymbolTable().toString());
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
		Assert.assertEquals("Table Dictionary is wrong", "{events={base_days=[[@12,49:49='a',<459>,1:49]], adjust_days=[[@16,63:63='a',<459>,1:63]], created_at=[[@7,33:33='a',<459>,1:33]], offset_days=[[@5,20:30='offset_days',<459>,1:20]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={shifted=[[@22,82:88='shifted',<459>,1:82]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={shifted=[[@22,82:88='shifted',<459>,1:82]]}, table_dictionary={events={base_days=[[@12,49:49='a',<459>,1:49]], adjust_days=[[@16,63:63='a',<459>,1:63]], created_at=[[@7,33:33='a',<459>,1:33]], offset_days=[[@5,20:30='offset_days',<459>,1:20]]}}, interface={shifted=[{name=offset_days, table_ref=events}, {name=created_at, table_ref=a}, {name=base_days, table_ref=a}, {name=adjust_days, table_ref=a}]}, table_alias={a=events}}}", extractor.getSymbolTable().toString());
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
		Assert.assertEquals("Table Dictionary is wrong", "{events={base_days=[[@10,47:55='base_days',<459>,1:47]], adjust_days=[[@12,59:69='adjust_days',<459>,1:59]], created_at=[[@7,33:42='created_at',<459>,1:33]], offset_days=[[@5,20:30='offset_days',<459>,1:20]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={shifted=[[@16,76:82='shifted',<459>,1:76]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={shifted=[[@16,76:82='shifted',<459>,1:76]]}, table_dictionary={events={base_days=[[@10,47:55='base_days',<459>,1:47]], adjust_days=[[@12,59:69='adjust_days',<459>,1:59]], created_at=[[@7,33:42='created_at',<459>,1:33]], offset_days=[[@5,20:30='offset_days',<459>,1:20]]}}, interface={shifted=[{name=offset_days, table_ref=events}, {name=created_at, table_ref=events}, {name=base_days, table_ref=events}, {name=adjust_days, table_ref=events}]}, table_alias={a=events}}}", extractor.getSymbolTable().toString());
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
		Assert.assertEquals("Table Dictionary is wrong", "{events={extra_days=[[@9,36:36='a',<459>,1:36]], offset_days=[[@5,20:20='a',<459>,1:20]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={shifted=[[@19,76:82='shifted',<459>,1:76]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={shifted=[[@19,76:82='shifted',<459>,1:76]]}, table_dictionary={events={extra_days=[[@9,36:36='a',<459>,1:36]], offset_days=[[@5,20:20='a',<459>,1:20]]}}, interface={shifted=[{name=offset_days, table_ref=a}, {name=extra_days, table_ref=a}]}, table_alias={a=events}}}", extractor.getSymbolTable().toString());
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
		Assert.assertEquals("Table Dictionary is wrong", "{events={created_at=[[@15,54:54='a',<459>,1:54]], extra_days=[[@9,36:36='a',<459>,1:36]], offset_days=[[@5,20:20='a',<459>,1:20]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={shifted=[[@20,71:77='shifted',<459>,1:71]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={shifted=[[@20,71:77='shifted',<459>,1:71]]}, table_dictionary={events={created_at=[[@15,54:54='a',<459>,1:54]], extra_days=[[@9,36:36='a',<459>,1:36]], offset_days=[[@5,20:20='a',<459>,1:20]]}}, interface={shifted=[{name=offset_days, table_ref=a}, {name=extra_days, table_ref=a}, {name=created_at, table_ref=a}]}, table_alias={a=events}}}", extractor.getSymbolTable().toString());
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
		Assert.assertEquals("Table Dictionary is wrong", "{events={created_at=[[@15,54:63='created_at',<459>,1:54]], extra_days=[[@9,36:36='a',<459>,1:36]], offset_days=[[@5,20:20='a',<459>,1:20]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={shifted=[[@18,69:75='shifted',<459>,1:69]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={shifted=[[@18,69:75='shifted',<459>,1:69]]}, table_dictionary={events={created_at=[[@15,54:63='created_at',<459>,1:54]], extra_days=[[@9,36:36='a',<459>,1:36]], offset_days=[[@5,20:20='a',<459>,1:20]]}}, interface={shifted=[{name=offset_days, table_ref=a}, {name=extra_days, table_ref=a}, {name=created_at, table_ref=events}]}, table_alias={a=events}}}", extractor.getSymbolTable().toString());
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
		Assert.assertEquals("Table Dictionary is wrong", "{events={base_days=[[@20,70:70='a',<459>,1:70]], adjust_days=[[@24,84:84='a',<459>,1:84]], created_at=[[@15,54:54='a',<459>,1:54]], extra_days=[[@9,36:36='a',<459>,1:36]], offset_days=[[@5,20:20='a',<459>,1:20]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={shifted=[[@30,103:109='shifted',<459>,1:103]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={shifted=[[@30,103:109='shifted',<459>,1:103]]}, table_dictionary={events={base_days=[[@20,70:70='a',<459>,1:70]], adjust_days=[[@24,84:84='a',<459>,1:84]], created_at=[[@15,54:54='a',<459>,1:54]], extra_days=[[@9,36:36='a',<459>,1:36]], offset_days=[[@5,20:20='a',<459>,1:20]]}}, interface={shifted=[{name=offset_days, table_ref=a}, {name=extra_days, table_ref=a}, {name=created_at, table_ref=a}, {name=base_days, table_ref=a}, {name=adjust_days, table_ref=a}]}, table_alias={a=events}}}", extractor.getSymbolTable().toString());
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
		Assert.assertEquals("Table Dictionary is wrong", "{events={base_days=[[@18,68:76='base_days',<459>,1:68]], adjust_days=[[@20,80:90='adjust_days',<459>,1:80]], created_at=[[@15,54:63='created_at',<459>,1:54]], extra_days=[[@9,36:36='a',<459>,1:36]], offset_days=[[@5,20:20='a',<459>,1:20]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={shifted=[[@24,97:103='shifted',<459>,1:97]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={shifted=[[@24,97:103='shifted',<459>,1:97]]}, table_dictionary={events={base_days=[[@18,68:76='base_days',<459>,1:68]], adjust_days=[[@20,80:90='adjust_days',<459>,1:80]], created_at=[[@15,54:63='created_at',<459>,1:54]], extra_days=[[@9,36:36='a',<459>,1:36]], offset_days=[[@5,20:20='a',<459>,1:20]]}}, interface={shifted=[{name=offset_days, table_ref=a}, {name=extra_days, table_ref=a}, {name=created_at, table_ref=events}, {name=base_days, table_ref=events}, {name=adjust_days, table_ref=events}]}, table_alias={a=events}}}", extractor.getSymbolTable().toString());
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
		Assert.assertEquals("Table Dictionary is wrong", "{events={offset_days=[[@5,20:30='offset_days',<459>,1:20]], extra_days=[[@7,34:43='extra_days',<459>,1:34]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={shifted=[[@15,72:78='shifted',<459>,1:72]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={shifted=[[@15,72:78='shifted',<459>,1:72]]}, table_dictionary={events={offset_days=[[@5,20:30='offset_days',<459>,1:20]], extra_days=[[@7,34:43='extra_days',<459>,1:34]]}}, interface={shifted=[{name=offset_days, table_ref=events}, {name=extra_days, table_ref=events}]}, table_alias={a=events}}}", extractor.getSymbolTable().toString());
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
		Assert.assertEquals("Table Dictionary is wrong", "{events={created_at=[[@11,50:50='a',<459>,1:50]], offset_days=[[@5,20:30='offset_days',<459>,1:20]], extra_days=[[@7,34:43='extra_days',<459>,1:34]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={shifted=[[@16,67:73='shifted',<459>,1:67]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={shifted=[[@16,67:73='shifted',<459>,1:67]]}, table_dictionary={events={created_at=[[@11,50:50='a',<459>,1:50]], offset_days=[[@5,20:30='offset_days',<459>,1:20]], extra_days=[[@7,34:43='extra_days',<459>,1:34]]}}, interface={shifted=[{name=offset_days, table_ref=events}, {name=extra_days, table_ref=events}, {name=created_at, table_ref=a}]}, table_alias={a=events}}}", extractor.getSymbolTable().toString());
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
		Assert.assertEquals("Table Dictionary is wrong", "{events={created_at=[[@11,50:59='created_at',<459>,1:50]], offset_days=[[@5,20:30='offset_days',<459>,1:20]], extra_days=[[@7,34:43='extra_days',<459>,1:34]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={shifted=[[@14,65:71='shifted',<459>,1:65]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={shifted=[[@14,65:71='shifted',<459>,1:65]]}, table_dictionary={events={created_at=[[@11,50:59='created_at',<459>,1:50]], offset_days=[[@5,20:30='offset_days',<459>,1:20]], extra_days=[[@7,34:43='extra_days',<459>,1:34]]}}, interface={shifted=[{name=offset_days, table_ref=events}, {name=extra_days, table_ref=events}, {name=created_at, table_ref=events}]}, table_alias={a=events}}}", extractor.getSymbolTable().toString());
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
		Assert.assertEquals("Table Dictionary is wrong", "{events={base_days=[[@16,66:66='a',<459>,1:66]], adjust_days=[[@20,80:80='a',<459>,1:80]], created_at=[[@11,50:50='a',<459>,1:50]], offset_days=[[@5,20:30='offset_days',<459>,1:20]], extra_days=[[@7,34:43='extra_days',<459>,1:34]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={shifted=[[@26,99:105='shifted',<459>,1:99]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={shifted=[[@26,99:105='shifted',<459>,1:99]]}, table_dictionary={events={base_days=[[@16,66:66='a',<459>,1:66]], adjust_days=[[@20,80:80='a',<459>,1:80]], created_at=[[@11,50:50='a',<459>,1:50]], offset_days=[[@5,20:30='offset_days',<459>,1:20]], extra_days=[[@7,34:43='extra_days',<459>,1:34]]}}, interface={shifted=[{name=offset_days, table_ref=events}, {name=extra_days, table_ref=events}, {name=created_at, table_ref=a}, {name=base_days, table_ref=a}, {name=adjust_days, table_ref=a}]}, table_alias={a=events}}}", extractor.getSymbolTable().toString());
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
		Assert.assertEquals("Table Dictionary is wrong", "{events={base_days=[[@14,64:72='base_days',<459>,1:64]], adjust_days=[[@16,76:86='adjust_days',<459>,1:76]], created_at=[[@11,50:59='created_at',<459>,1:50]], offset_days=[[@5,20:30='offset_days',<459>,1:20]], extra_days=[[@7,34:43='extra_days',<459>,1:34]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={shifted=[[@20,93:99='shifted',<459>,1:93]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={shifted=[[@20,93:99='shifted',<459>,1:93]]}, table_dictionary={events={base_days=[[@14,64:72='base_days',<459>,1:64]], adjust_days=[[@16,76:86='adjust_days',<459>,1:76]], created_at=[[@11,50:59='created_at',<459>,1:50]], offset_days=[[@5,20:30='offset_days',<459>,1:20]], extra_days=[[@7,34:43='extra_days',<459>,1:34]]}}, interface={shifted=[{name=offset_days, table_ref=events}, {name=extra_days, table_ref=events}, {name=created_at, table_ref=events}, {name=base_days, table_ref=events}, {name=adjust_days, table_ref=events}]}, table_alias={a=events}}}", extractor.getSymbolTable().toString());
	}

	@Test
	public void dateaddReportsAmbiguousUnqualifiedValue() {
		String query = "select dateadd(year, 1, created_at) as shifted from events a join accounts b on a.account_id = b.id";
		SqlParserAccess parser = new SqlParserAccess(false, false, false);
		parser.executeTheParse(query, "SQL");
		Assert.assertEquals(0, parser.getParser().getNumberOfSyntaxErrors());
		Assert.assertEquals(parser.getAllDiagnostics().toString(), 3, parser.getAllDiagnostics().size());
		assertSnowflakeGrammarWarningAt(parser, "snowflake_datetime_add_expression",
				invocationOffset(query, "dateadd"));
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
		Assert.assertEquals("{accounts={id=[[@22,95:95='b',<459>,1:95]]}, events={account_id=[[@18,80:80='a',<459>,1:80]]}}",
				parser.getSnippet().getTableDictionary().toString());
	}

	@Test
	public void dateaddReportsAmbiguousUnqualifiedIncrement() {
		String query = "select dateadd(day, offset_days, a.ts) as shifted from events a join accounts b on a.account_id = b.id";
		SqlParserAccess parser = new SqlParserAccess(false, false, false);
		parser.executeTheParse(query, "SQL");
		Assert.assertEquals(0, parser.getParser().getNumberOfSyntaxErrors());
		Assert.assertEquals(parser.getAllDiagnostics().toString(), 3, parser.getAllDiagnostics().size());
		assertSnowflakeGrammarWarningAt(parser, "snowflake_datetime_add_expression",
				invocationOffset(query, "dateadd"));
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
		Assert.assertEquals("{accounts={id=[[@24,98:98='b',<459>,1:98]]}, events={account_id=[[@20,83:83='a',<459>,1:83]], ts=[[@7,33:33='a',<459>,1:33]]}}",
				parser.getSnippet().getTableDictionary().toString());
		Assert.assertEquals("{def_query0={query_dictionary={shifted=[[@12,42:48='shifted',<459>,1:42]]}, table_dictionary={accounts={id=[[@24,98:98='b',<459>,1:98]]}, events={account_id=[[@20,83:83='a',<459>,1:83]], ts=[[@7,33:33='a',<459>,1:33]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}], interface={shifted=[{name=offset_days, table_ref=null}, {name=ts, table_ref=a}]}, table_alias={a=events, b=accounts}}}",
				parser.getSnippet().getSymbolTable().toString());
	}

	@Test
	public void dateaddReportsAmbiguousColumnsInBothArguments() {
		String query = "select dateadd(day, offset_days, created_at) as shifted from events a join accounts b on a.account_id = b.id";
		SqlParserAccess parser = new SqlParserAccess(false, false, false);
		parser.executeTheParse(query, "SQL");
		Assert.assertEquals(0, parser.getParser().getNumberOfSyntaxErrors());
		Assert.assertEquals(parser.getAllDiagnostics().toString(), 4, parser.getAllDiagnostics().size());
		assertSnowflakeGrammarWarningAt(parser, "snowflake_datetime_add_expression",
				invocationOffset(query, "dateadd"));
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
		Assert.assertEquals("{def_query0={query_dictionary={shifted=[[@10,48:54='shifted',<459>,1:48]]}, table_dictionary={accounts={id=[[@22,104:104='b',<459>,1:104]]}, events={account_id=[[@18,89:89='a',<459>,1:89]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}], interface={shifted=[{name=offset_days, table_ref=null}, {name=created_at, table_ref=null}]}, table_alias={a=events, b=accounts}}}",
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
		Assert.assertEquals("Table Dictionary is wrong", "{events={created_at=[[@11,48:48='a',<459>,1:48]], offset_days=[[@5,20:20='a',<459>,1:20]], extra_days=[[@9,36:45='extra_days',<459>,1:36]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={shifted=[[@16,65:71='shifted',<459>,1:65]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={shifted=[[@16,65:71='shifted',<459>,1:65]]}, table_dictionary={events={created_at=[[@11,48:48='a',<459>,1:48]], offset_days=[[@5,20:20='a',<459>,1:20]], extra_days=[[@9,36:45='extra_days',<459>,1:36]]}}, interface={shifted=[{name=offset_days, table_ref=a}, {name=extra_days, table_ref=events}, {name=created_at, table_ref=a}]}, table_alias={a=events}}}", extractor.getSymbolTable().toString());
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
		Assert.assertEquals("Table Dictionary is wrong", "{events={base_days=[[@12,39:47='base_days',<459>,1:39]], adjust_days=[[@14,51:51='a',<459>,1:51]], created_at=[[@7,23:23='a',<459>,1:23]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={shifted=[[@20,70:76='shifted',<459>,1:70]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={shifted=[[@20,70:76='shifted',<459>,1:70]]}, table_dictionary={events={base_days=[[@12,39:47='base_days',<459>,1:39]], adjust_days=[[@14,51:51='a',<459>,1:51]], created_at=[[@7,23:23='a',<459>,1:23]]}}, interface={shifted=[{name=created_at, table_ref=a}, {name=base_days, table_ref=events}, {name=adjust_days, table_ref=a}]}, table_alias={a=events}}}", extractor.getSymbolTable().toString());
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
		Assert.assertEquals("Table Dictionary is wrong", "{events={base_days=[[@16,64:72='base_days',<459>,1:64]], adjust_days=[[@18,76:76='a',<459>,1:76]], created_at=[[@11,48:48='a',<459>,1:48]], offset_days=[[@5,20:20='a',<459>,1:20]], extra_days=[[@9,36:45='extra_days',<459>,1:36]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={shifted=[[@24,95:101='shifted',<459>,1:95]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={shifted=[[@24,95:101='shifted',<459>,1:95]]}, table_dictionary={events={base_days=[[@16,64:72='base_days',<459>,1:64]], adjust_days=[[@18,76:76='a',<459>,1:76]], created_at=[[@11,48:48='a',<459>,1:48]], offset_days=[[@5,20:20='a',<459>,1:20]], extra_days=[[@9,36:45='extra_days',<459>,1:36]]}}, interface={shifted=[{name=offset_days, table_ref=a}, {name=extra_days, table_ref=events}, {name=created_at, table_ref=a}, {name=base_days, table_ref=events}, {name=adjust_days, table_ref=a}]}, table_alias={a=events}}}", extractor.getSymbolTable().toString());
	}

	@Test
	public void dateaddReportsOnlyAmbiguousTermInIncrementFormula() {
		String query = "select dateadd(day, a.offset_days + extra_days, a.created_at) as shifted from events a join accounts b on a.account_id = b.id";
		SqlParserAccess parser = new SqlParserAccess(false, false, false);
		parser.executeTheParse(query, "SQL");
		Assert.assertEquals(0, parser.getParser().getNumberOfSyntaxErrors());
		Assert.assertEquals(parser.getAllDiagnostics().toString(), 3, parser.getAllDiagnostics().size());
		assertSnowflakeGrammarWarningAt(parser, "snowflake_datetime_add_expression",
				invocationOffset(query, "dateadd"));
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
		Assert.assertEquals("{def_query0={query_dictionary={shifted=[[@16,65:71='shifted',<459>,1:65]]}, table_dictionary={accounts={id=[[@28,121:121='b',<459>,1:121]]}, events={account_id=[[@24,106:106='a',<459>,1:106]], created_at=[[@11,48:48='a',<459>,1:48]], offset_days=[[@5,20:20='a',<459>,1:20]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}], interface={shifted=[{name=offset_days, table_ref=a}, {name=extra_days, table_ref=null}, {name=created_at, table_ref=a}]}, table_alias={a=events, b=accounts}}}",
				parser.getSnippet().getSymbolTable().toString());
	}

	@Test
	public void dateaddReportsOnlyAmbiguousTermInValueFormula() {
		String query = "select dateadd(day, 2, a.created_at + (base_days + a.adjust_days)) as shifted from events a join accounts b on a.account_id = b.id";
		SqlParserAccess parser = new SqlParserAccess(false, false, false);
		parser.executeTheParse(query, "SQL");
		Assert.assertEquals(0, parser.getParser().getNumberOfSyntaxErrors());
		Assert.assertEquals(parser.getAllDiagnostics().toString(), 3, parser.getAllDiagnostics().size());
		assertSnowflakeGrammarWarningAt(parser, "snowflake_datetime_add_expression",
				invocationOffset(query, "dateadd"));
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
		Assert.assertEquals("{def_query0={query_dictionary={shifted=[[@20,70:76='shifted',<459>,1:70]]}, table_dictionary={accounts={id=[[@32,126:126='b',<459>,1:126]]}, events={account_id=[[@28,111:111='a',<459>,1:111]], adjust_days=[[@14,51:51='a',<459>,1:51]], created_at=[[@7,23:23='a',<459>,1:23]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}], interface={shifted=[{name=created_at, table_ref=a}, {name=base_days, table_ref=null}, {name=adjust_days, table_ref=a}]}, table_alias={a=events, b=accounts}}}",
				parser.getSnippet().getSymbolTable().toString());
	}

	@Test
	public void dateaddReportsOnlyAmbiguousTermsInBothArgumentFormulas() {
		String query = "select dateadd(day, a.offset_days + extra_days, a.created_at + (base_days + a.adjust_days)) as shifted from events a join accounts b on a.account_id = b.id";
		SqlParserAccess parser = new SqlParserAccess(false, false, false);
		parser.executeTheParse(query, "SQL");
		Assert.assertEquals(0, parser.getParser().getNumberOfSyntaxErrors());
		Assert.assertEquals(parser.getAllDiagnostics().toString(), 4, parser.getAllDiagnostics().size());
		assertSnowflakeGrammarWarningAt(parser, "snowflake_datetime_add_expression",
				invocationOffset(query, "dateadd"));
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
		Assert.assertEquals("{def_query0={query_dictionary={shifted=[[@24,95:101='shifted',<459>,1:95]]}, table_dictionary={accounts={id=[[@36,151:151='b',<459>,1:151]]}, events={account_id=[[@32,136:136='a',<459>,1:136]], adjust_days=[[@18,76:76='a',<459>,1:76]], created_at=[[@11,48:48='a',<459>,1:48]], offset_days=[[@5,20:20='a',<459>,1:20]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}], interface={shifted=[{name=offset_days, table_ref=a}, {name=extra_days, table_ref=null}, {name=created_at, table_ref=a}, {name=base_days, table_ref=null}, {name=adjust_days, table_ref=a}]}, table_alias={a=events, b=accounts}}}",
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
		Assert.assertEquals("Table Dictionary is wrong", "{accounts={created_at=[[@22,109:118='created_at',<459>,1:109]]}, events={offset_days=[[@14,69:79='offset_days',<459>,1:69]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={offset_days=[[@14,69:79='offset_days',<459>,1:69], [@5,20:30='offset_days',<459>,1:20]]}, query1={created_at=[[@22,109:118='created_at',<459>,1:109], [@7,33:42='created_at',<459>,1:33]]}, query2={shifted=[[@10,48:54='shifted',<459>,1:48]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query2={query_dictionary={shifted=[[@10,48:54='shifted',<459>,1:48]]}, def_query1={query_dictionary={created_at=[[@22,109:118='created_at',<459>,1:109], [@7,33:42='created_at',<459>,1:33]]}, table_dictionary={accounts={created_at=[[@22,109:118='created_at',<459>,1:109]]}}, interface={created_at=[{name=created_at, table_ref=accounts}]}}, def_query0={query_dictionary={offset_days=[[@14,69:79='offset_days',<459>,1:69], [@5,20:30='offset_days',<459>,1:20]]}, table_dictionary={events={offset_days=[[@14,69:79='offset_days',<459>,1:69]]}}, interface={offset_days=[{name=offset_days, table_ref=events}]}}, filters=[], interface={shifted=[{name=offset_days, table_ref=query0}, {name=created_at, table_ref=query1}]}, table_alias={a=query0, b=query1}}}", extractor.getSymbolTable().toString());
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
		Assert.assertEquals("Table Dictionary is wrong", "{accounts={created_at=[[@14,58:67='created_at',<459>,1:58]]}, events={offset_days=[[@5,18:28='offset_days',<459>,1:18]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={offset_days=[[@5,18:28='offset_days',<459>,1:18], [@23,104:114='offset_days',<459>,1:104]]}, query1={created_at=[[@14,58:67='created_at',<459>,1:58], [@25,117:126='created_at',<459>,1:117]]}, query2={shifted=[[@28,132:138='shifted',<459>,1:132]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query2={context_list={e=query0, ac=query1, a=query0, b=query1}, query_dictionary={shifted=[[@28,132:138='shifted',<459>,1:132]]}, def_query1={context_list={e=query0}, query_dictionary={created_at=[[@14,58:67='created_at',<459>,1:58], [@25,117:126='created_at',<459>,1:117]]}, table_dictionary={accounts={created_at=[[@14,58:67='created_at',<459>,1:58]]}}, interface={created_at=[{name=created_at, table_ref=accounts}]}, table_alias={e=query0}}, def_query0={query_dictionary={offset_days=[[@5,18:28='offset_days',<459>,1:18], [@23,104:114='offset_days',<459>,1:104]]}, table_dictionary={events={offset_days=[[@5,18:28='offset_days',<459>,1:18]]}}, interface={offset_days=[{name=offset_days, table_ref=events}]}}, filters=[], interface={shifted=[{name=offset_days, table_ref=query0}, {name=created_at, table_ref=query1}]}, table_alias={a=query0, ac=query1, b=query1, e=query0}}}", extractor.getSymbolTable().toString());
	}

	@Test
	public void dateaddReportsOverlappingDerivedTableExports() {
		String query = "select dateadd(day, offset_days, created_at) as shifted from (select offset_days, created_at from events) a join (select offset_days, created_at from accounts) b on 1 = 1";
		SqlParserAccess parser = new SqlParserAccess(false, false, false);
		parser.executeTheParse(query, "SQL");
		Assert.assertEquals(0, parser.getParser().getNumberOfSyntaxErrors());
		Assert.assertEquals(parser.getAllDiagnostics().toString(), 4, parser.getAllDiagnostics().size());
		assertSnowflakeGrammarWarningAt(parser, "snowflake_datetime_add_expression",
				invocationOffset(query, "dateadd"));
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
		Assert.assertEquals("{def_query2={query_dictionary={shifted=[[@10,48:54='shifted',<459>,1:48]]}, def_query1={query_dictionary={created_at=[[@26,134:143='created_at',<459>,1:134]], offset_days=[[@24,121:131='offset_days',<459>,1:121]]}, table_dictionary={accounts={created_at=[[@26,134:143='created_at',<459>,1:134]], offset_days=[[@24,121:131='offset_days',<459>,1:121]]}}, interface={created_at=[{name=created_at, table_ref=accounts}], offset_days=[{name=offset_days, table_ref=accounts}]}}, def_query0={query_dictionary={created_at=[[@16,82:91='created_at',<459>,1:82]], offset_days=[[@14,69:79='offset_days',<459>,1:69]]}, table_dictionary={events={created_at=[[@16,82:91='created_at',<459>,1:82]], offset_days=[[@14,69:79='offset_days',<459>,1:69]]}}, interface={created_at=[{name=created_at, table_ref=events}], offset_days=[{name=offset_days, table_ref=events}]}}, filters=[], interface={shifted=[{name=offset_days, table_ref=null}, {name=created_at, table_ref=null}]}, table_alias={a=query0, b=query1}}}",
				parser.getSnippet().getSymbolTable().toString());
	}

	@Test
	public void dateaddReportsOverlappingCteExports() {
		String query = "with e as (select offset_days, created_at from events), ac as (select offset_days, created_at from accounts) select dateadd(day, offset_days, created_at) as shifted from e a join ac b on 1 = 1";
		SqlParserAccess parser = new SqlParserAccess(false, false, false);
		parser.executeTheParse(query, "SQL");
		Assert.assertEquals(0, parser.getParser().getNumberOfSyntaxErrors());
		Assert.assertEquals(parser.getAllDiagnostics().toString(), 4, parser.getAllDiagnostics().size());
		assertSnowflakeGrammarWarningAt(parser, "snowflake_datetime_add_expression",
				invocationOffset(query, "dateadd"));
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
		Assert.assertEquals("{def_query2={context_list={e=query0, ac=query1, a=query0, b=query1}, query_dictionary={shifted=[[@32,157:163='shifted',<459>,1:157]]}, def_query1={context_list={e=query0}, query_dictionary={created_at=[[@18,83:92='created_at',<459>,1:83]], offset_days=[[@16,70:80='offset_days',<459>,1:70]]}, table_dictionary={accounts={created_at=[[@18,83:92='created_at',<459>,1:83]], offset_days=[[@16,70:80='offset_days',<459>,1:70]]}}, interface={created_at=[{name=created_at, table_ref=accounts}], offset_days=[{name=offset_days, table_ref=accounts}]}, table_alias={e=query0}}, def_query0={query_dictionary={created_at=[[@7,31:40='created_at',<459>,1:31]], offset_days=[[@5,18:28='offset_days',<459>,1:18]]}, table_dictionary={events={created_at=[[@7,31:40='created_at',<459>,1:31]], offset_days=[[@5,18:28='offset_days',<459>,1:18]]}}, interface={created_at=[{name=created_at, table_ref=events}], offset_days=[{name=offset_days, table_ref=events}]}}, filters=[], interface={shifted=[{name=offset_days, table_ref=null}, {name=created_at, table_ref=null}]}, table_alias={a=query0, ac=query1, b=query1, e=query0}}}",
				parser.getSnippet().getSymbolTable().toString());
	}

	@Test
	public void dateaddRejectsHiddenUnderlyingDerivedColumn() {
		String query = "select dateadd(day, 1, a.created_at) as shifted from (select created_at as visible_ts from events) a";
		SqlParserAccess parser = new SqlParserAccess(false, false, false);
		parser.executeTheParse(query, "SQL");
		Assert.assertEquals(0, parser.getParser().getNumberOfSyntaxErrors());
		Assert.assertEquals(parser.getAllDiagnostics().toString(), 2, parser.getAllDiagnostics().size());
		assertSnowflakeGrammarWarningAt(parser, "snowflake_datetime_add_expression",
				invocationOffset(query, "dateadd"));
		assertFatalDiagnosticAtPositionWithFullMessage(parser.getSnippet(), "QUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIAS",
				"Qualified column 'created_at' at (l:1 c:23) was not found in output interface of query alias 'a'.",
				"a.created_at", 1, 23);
		Assert.assertEquals("a.created_at", findFatalDiagnosticByCodeAndFragment(parser.getSnippet(),
				"QUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIAS", "a.created_at").tokenText());
		Assert.assertEquals("{def_query1={query_dictionary={shifted=[[@12,40:46='shifted',<459>,1:40]]}, def_query0={query_dictionary={visible_ts=[[@18,75:84='visible_ts',<459>,1:75]]}, table_dictionary={events={created_at=[[@16,61:70='created_at',<459>,1:61]]}}, interface={visible_ts=[{name=created_at, table_ref=events}]}}, interface={shifted=[{name=created_at, table_ref=a}]}, table_alias={a=query0}}}",
				parser.getSnippet().getSymbolTable().toString());
	}

	@Test
	public void dateaddRejectsMissingDerivedTableOutput() {
		String query = "select dateadd(day, 1, a.missing_ts) as shifted from (select created_at from events) a";
		SqlParserAccess parser = new SqlParserAccess(false, false, false);
		parser.executeTheParse(query, "SQL");
		Assert.assertEquals(0, parser.getParser().getNumberOfSyntaxErrors());
		Assert.assertEquals(parser.getAllDiagnostics().toString(), 2, parser.getAllDiagnostics().size());
		assertSnowflakeGrammarWarningAt(parser, "snowflake_datetime_add_expression",
				invocationOffset(query, "dateadd"));
		assertFatalDiagnosticAtPositionWithFullMessage(parser.getSnippet(), "QUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIAS",
				"Qualified column 'missing_ts' at (l:1 c:23) was not found in output interface of query alias 'a'.",
				"a.missing_ts", 1, 23);
		Assert.assertEquals("a.missing_ts", findFatalDiagnosticByCodeAndFragment(parser.getSnippet(),
				"QUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIAS", "a.missing_ts").tokenText());
		Assert.assertEquals("{def_query1={query_dictionary={shifted=[[@12,40:46='shifted',<459>,1:40]]}, def_query0={query_dictionary={created_at=[[@16,61:70='created_at',<459>,1:61]]}, table_dictionary={events={created_at=[[@16,61:70='created_at',<459>,1:61]]}}, interface={created_at=[{name=created_at, table_ref=events}]}}, interface={shifted=[{name=missing_ts, table_ref=a}]}, table_alias={a=query0}}}",
				parser.getSnippet().getSymbolTable().toString());
	}

	@Test
	public void dateaddRejectsMissingCteOutput() {
		String query = "with e as (select created_at from events) select dateadd(day, 1, a.missing_ts) as shifted from e a";
		SqlParserAccess parser = new SqlParserAccess(false, false, false);
		parser.executeTheParse(query, "SQL");
		Assert.assertEquals(0, parser.getParser().getNumberOfSyntaxErrors());
		Assert.assertEquals(parser.getAllDiagnostics().toString(), 2, parser.getAllDiagnostics().size());
		assertSnowflakeGrammarWarningAt(parser, "snowflake_datetime_add_expression",
				invocationOffset(query, "dateadd"));
		assertFatalDiagnosticAtPositionWithFullMessage(parser.getSnippet(), "QUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIAS",
				"Qualified column 'missing_ts' at (l:1 c:65) was not found in output interface of query alias 'a'.",
				"a.missing_ts", 1, 65);
		Assert.assertEquals("a.missing_ts", findFatalDiagnosticByCodeAndFragment(parser.getSnippet(),
				"QUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIAS", "a.missing_ts").tokenText());
		Assert.assertEquals("{def_query1={context_list={e=query0, a=query0}, query_dictionary={shifted=[[@21,82:88='shifted',<459>,1:82]]}, def_query0={query_dictionary={created_at=[[@5,18:27='created_at',<459>,1:18]]}, table_dictionary={events={created_at=[[@5,18:27='created_at',<459>,1:18]]}}, interface={created_at=[{name=created_at, table_ref=events}]}}, interface={shifted=[{name=missing_ts, table_ref=a}]}, table_alias={a=query0, e=query0}}}",
				parser.getSnippet().getSymbolTable().toString());
	}

	@Test
	public void dateaddRejectsUnknownTableAlias() {
		String query = "select dateadd(day, 1, z.created_at) as shifted from events a";
		SqlParserAccess parser = new SqlParserAccess(false, false, false);
		parser.executeTheParse(query, "SQL");
		Assert.assertEquals(0, parser.getParser().getNumberOfSyntaxErrors());
		Assert.assertEquals(parser.getAllDiagnostics().toString(), 2, parser.getAllDiagnostics().size());
		assertSnowflakeGrammarWarningAt(parser, "snowflake_datetime_add_expression",
				invocationOffset(query, "dateadd"));
		assertFatalDiagnosticAtPositionWithFullMessage(parser.getSnippet(), "QUALIFIED_COLUMN_NOT_FOUND_IN_TABLE",
				"Source Table not found for Column 'created_at' at (l:1 c:40). No alias or table called 'z'.",
				"created_at", 1, 40);
		Assert.assertEquals("created_at", findFatalDiagnosticByCodeAndFragment(parser.getSnippet(),
				"QUALIFIED_COLUMN_NOT_FOUND_IN_TABLE", "created_at").tokenText());
		Assert.assertEquals("{def_query0={query_dictionary={shifted=[[@12,40:46='shifted',<459>,1:40]]}, table_dictionary={events={}}, interface={shifted=[{name=created_at, table_ref=z}]}, table_alias={a=events}}}",
				parser.getSnippet().getSymbolTable().toString());
	}

	@Test
	public void dateaddMissingQualifiedFormulaSubstitutionsRemainFatalWithoutJoinExtension() {
		String query = "select dateadd(day, a.offset_days + (x.<increment> + 1), a.created_at + (y.<days> + 2)) as shifted from events a";
		SqlParserAccess parser = new SqlParserAccess(false, false, false);
		parser.executeTheParse(query, "SQL");
		Assert.assertEquals(0, parser.getParser().getNumberOfSyntaxErrors());
		Assert.assertEquals(parser.getAllDiagnostics().toString(), 3, parser.getAllDiagnostics().size());
		assertSnowflakeGrammarWarningAt(parser, "snowflake_datetime_add_expression",
				invocationOffset(query, "dateadd"));
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
		Assert.assertEquals("{def_query0={query_dictionary={shifted=[[@30,91:97='shifted',<459>,1:91]]}, table_dictionary={events={created_at=[[@17,57:57='a',<459>,1:57]], offset_days=[[@5,20:20='a',<459>,1:20]]}}, interface={shifted=[{name=offset_days, table_ref=a}, {substitution={name=<increment>, type=column}, table_ref=x}, {name=created_at, table_ref=a}, {substitution={name=<days>, type=column}, table_ref=y}]}, table_alias={a=events}}}",
				parser.getSnippet().getSymbolTable().toString());
	}

	@Test
	public void dateaddMissingQualifiedFormulaSubstitutionsDeferToJoinExtension() {
		String query = "select dateadd(day, a.offset_days + (x.<increment> + 1), a.created_at + (y.<days> + 2)) as shifted from events a <extension>";
		SqlParserAccess parser = new SqlParserAccess(false, false, false);
		parser.executeTheParse(query, "SQL");
		Assert.assertEquals(0, parser.getParser().getNumberOfSyntaxErrors());
		Assert.assertEquals(parser.getAllDiagnostics().toString(), 4, parser.getAllDiagnostics().size());
		assertSnowflakeGrammarWarningAt(parser, "snowflake_datetime_add_expression",
				invocationOffset(query, "dateadd"));
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
		Assert.assertEquals("{events={created_at=[[@17,57:57='a',<459>,1:57]], offset_days=[[@5,20:20='a',<459>,1:20]]}}", parser.getSnippet().getTableDictionary().toString());
		Assert.assertEquals("{query0={shifted=[[@30,91:97='shifted',<459>,1:91]]}}", parser.getSnippet().getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("{def_query0={query_dictionary={shifted=[[@30,91:97='shifted',<459>,1:91]]}, table_dictionary={events={created_at=[[@17,57:57='a',<459>,1:57]], offset_days=[[@5,20:20='a',<459>,1:20]]}}, interface={shifted=[{name=offset_days, table_ref=a}, {substitution={name=<increment>, type=column}, table_ref=x}, {name=created_at, table_ref=a}, {substitution={name=<days>, type=column}, table_ref=y}]}, table_alias={a=events}}}", parser.getSnippet().getSymbolTable().toString());
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
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={shifted=[[@11,45:51='shifted',<459>,1:45]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={shifted=[[@11,45:51='shifted',<459>,1:45]]}, interface={shifted=[]}}}", extractor.getSymbolTable().toString());
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
		Assert.assertEquals("Table Dictionary is wrong", "{events={ts=[[@7,24:24='a',<459>,1:24], [@20,56:56='a',<459>,1:56], [@33,86:86='a',<459>,1:86], [@46,116:116='a',<459>,1:116], [@59,144:144='a',<459>,1:144], [@72,173:173='a',<459>,1:173], [@85,204:204='a',<459>,1:204], [@98,236:236='a',<459>,1:236], [@111,272:272='a',<459>,1:272], [@124,309:309='a',<459>,1:309], [@137,345:345='a',<459>,1:345]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={q=[[@25,65:65='q',<459>,1:65]], mo=[[@38,95:96='mo',<459>,1:95]], s=[[@103,245:245='s',<459>,1:245]], d=[[@64,153:153='d',<459>,1:153]], ns=[[@142,354:355='ns',<459>,1:354]], ms=[[@116,281:282='ms',<459>,1:281]], w=[[@51,125:125='w',<459>,1:125]], h=[[@77,182:182='h',<459>,1:182]], y=[[@12,33:33='y',<459>,1:33]], mi=[[@90,213:214='mi',<459>,1:213]], us=[[@129,318:319='us',<459>,1:318]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={q=[[@25,65:65='q',<459>,1:65]], mo=[[@38,95:96='mo',<459>,1:95]], s=[[@103,245:245='s',<459>,1:245]], d=[[@64,153:153='d',<459>,1:153]], ns=[[@142,354:355='ns',<459>,1:354]], ms=[[@116,281:282='ms',<459>,1:281]], w=[[@51,125:125='w',<459>,1:125]], h=[[@77,182:182='h',<459>,1:182]], y=[[@12,33:33='y',<459>,1:33]], mi=[[@90,213:214='mi',<459>,1:213]], us=[[@129,318:319='us',<459>,1:318]]}, table_dictionary={events={ts=[[@7,24:24='a',<459>,1:24], [@20,56:56='a',<459>,1:56], [@33,86:86='a',<459>,1:86], [@46,116:116='a',<459>,1:116], [@59,144:144='a',<459>,1:144], [@72,173:173='a',<459>,1:173], [@85,204:204='a',<459>,1:204], [@98,236:236='a',<459>,1:236], [@111,272:272='a',<459>,1:272], [@124,309:309='a',<459>,1:309], [@137,345:345='a',<459>,1:345]]}}, interface={q=[{name=ts, table_ref=a}], mo=[{name=ts, table_ref=a}], s=[{name=ts, table_ref=a}], d=[{name=ts, table_ref=a}], ns=[{name=ts, table_ref=a}], ms=[{name=ts, table_ref=a}], w=[{name=ts, table_ref=a}], h=[{name=ts, table_ref=a}], y=[{name=ts, table_ref=a}], mi=[{name=ts, table_ref=a}], us=[{name=ts, table_ref=a}]}, table_alias={a=events}}}", extractor.getSymbolTable().toString());
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
		Assert.assertEquals(
				Set.of("DAY", "HOUR", "MICROSECOND", "MILLISECOND", "MINUTE", "MONTH",
						"NANOSECOND", "QUARTER", "SECOND", "WEEK", "WEEK_ISO", "YEAR", "YEAROFWEEKISO"),
				Set.copyOf(DatetimePartRegistry.canonicalValues(DatetimePartRegistry.Family.TRUNCATION)));
		Assert.assertEquals(
				Set.of("DAY", "HOUR", "MINUTE", "MONTH", "QUARTER", "SECOND", "WEEK", "YEAR"),
				Set.copyOf(DatetimePartRegistry.canonicalValues(DatetimePartRegistry.Family.TIME_SLICE)));
		Assert.assertEquals(
				Set.of("MONTH", "QUARTER", "WEEK", "YEAR"),
				Set.copyOf(DatetimePartRegistry.canonicalValues(DatetimePartRegistry.Family.LAST_DAY)));
		Assert.assertEquals("WEEK_ISO",
				DatetimePartRegistry.findCanonical(DatetimePartRegistry.Family.TRUNCATION, "weekiso").orElseThrow());
		Assert.assertTrue(DatetimePartRegistry.findCanonical(
				DatetimePartRegistry.Family.LAST_DAY, "hour").isEmpty());
		Assert.assertTrue(DatetimePartRegistry.findCanonical(
				DatetimePartRegistry.Family.TIME_SLICE, "millisecond").isEmpty());
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
		Assert.assertEquals("Table Dictionary is wrong", "{events={ts=[[@7,24:24='a',<459>,1:24], [@20,54:54='a',<459>,1:54], [@33,85:85='a',<459>,1:85], [@46,117:117='a',<459>,1:117], [@59,150:150='a',<459>,1:150], [@72,181:181='a',<459>,1:181], [@85,215:215='a',<459>,1:215], [@98,247:247='a',<459>,1:247]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={part1=[[@12,33:37='part1',<459>,1:33]], part2=[[@25,63:67='part2',<459>,1:63]], part3=[[@38,94:98='part3',<459>,1:94]], part4=[[@51,126:130='part4',<459>,1:126]], part5=[[@64,159:163='part5',<459>,1:159]], part6=[[@77,190:194='part6',<459>,1:190]], part7=[[@90,224:228='part7',<459>,1:224]], part8=[[@103,256:260='part8',<459>,1:256]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={part1=[[@12,33:37='part1',<459>,1:33]], part2=[[@25,63:67='part2',<459>,1:63]], part3=[[@38,94:98='part3',<459>,1:94]], part4=[[@51,126:130='part4',<459>,1:126]], part5=[[@64,159:163='part5',<459>,1:159]], part6=[[@77,190:194='part6',<459>,1:190]], part7=[[@90,224:228='part7',<459>,1:224]], part8=[[@103,256:260='part8',<459>,1:256]]}, table_dictionary={events={ts=[[@7,24:24='a',<459>,1:24], [@20,54:54='a',<459>,1:54], [@33,85:85='a',<459>,1:85], [@46,117:117='a',<459>,1:117], [@59,150:150='a',<459>,1:150], [@72,181:181='a',<459>,1:181], [@85,215:215='a',<459>,1:215], [@98,247:247='a',<459>,1:247]]}}, interface={part1=[{name=ts, table_ref=a}], part2=[{name=ts, table_ref=a}], part3=[{name=ts, table_ref=a}], part4=[{name=ts, table_ref=a}], part5=[{name=ts, table_ref=a}], part6=[{name=ts, table_ref=a}], part7=[{name=ts, table_ref=a}], part8=[{name=ts, table_ref=a}]}, table_alias={a=events}}}", extractor.getSymbolTable().toString());
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
		Assert.assertEquals("Table Dictionary is wrong", "{events={ts=[[@7,27:27='a',<459>,1:27], [@20,57:57='a',<459>,1:57], [@33,89:89='a',<459>,1:89], [@46,122:122='a',<459>,1:122], [@59,159:159='a',<459>,1:159]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={part3=[[@38,98:102='part3',<459>,1:98]], part4=[[@51,131:135='part4',<459>,1:131]], part5=[[@64,168:172='part5',<459>,1:168]], part1=[[@12,36:40='part1',<459>,1:36]], part2=[[@25,66:70='part2',<459>,1:66]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={part1=[[@12,36:40='part1',<459>,1:36]], part2=[[@25,66:70='part2',<459>,1:66]], part3=[[@38,98:102='part3',<459>,1:98]], part4=[[@51,131:135='part4',<459>,1:131]], part5=[[@64,168:172='part5',<459>,1:168]]}, table_dictionary={events={ts=[[@7,27:27='a',<459>,1:27], [@20,57:57='a',<459>,1:57], [@33,89:89='a',<459>,1:89], [@46,122:122='a',<459>,1:122], [@59,159:159='a',<459>,1:159]]}}, interface={part1=[{name=ts, table_ref=a}], part2=[{name=ts, table_ref=a}], part3=[{name=ts, table_ref=a}], part4=[{name=ts, table_ref=a}], part5=[{name=ts, table_ref=a}]}, table_alias={a=events}}}", extractor.getSymbolTable().toString());
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
		Assert.assertEquals("Table Dictionary is wrong", "{events={ts=[[@7,25:25='a',<459>,1:25], [@20,56:56='a',<459>,1:56], [@33,88:88='a',<459>,1:88], [@46,121:121='a',<459>,1:121], [@59,156:156='a',<459>,1:156]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={part3=[[@38,97:101='part3',<459>,1:97]], part4=[[@51,130:134='part4',<459>,1:130]], part5=[[@64,165:169='part5',<459>,1:165]], part1=[[@12,34:38='part1',<459>,1:34]], part2=[[@25,65:69='part2',<459>,1:65]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={part1=[[@12,34:38='part1',<459>,1:34]], part2=[[@25,65:69='part2',<459>,1:65]], part3=[[@38,97:101='part3',<459>,1:97]], part4=[[@51,130:134='part4',<459>,1:130]], part5=[[@64,165:169='part5',<459>,1:165]]}, table_dictionary={events={ts=[[@7,25:25='a',<459>,1:25], [@20,56:56='a',<459>,1:56], [@33,88:88='a',<459>,1:88], [@46,121:121='a',<459>,1:121], [@59,156:156='a',<459>,1:156]]}}, interface={part1=[{name=ts, table_ref=a}], part2=[{name=ts, table_ref=a}], part3=[{name=ts, table_ref=a}], part4=[{name=ts, table_ref=a}], part5=[{name=ts, table_ref=a}]}, table_alias={a=events}}}", extractor.getSymbolTable().toString());
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
		Assert.assertEquals("Table Dictionary is wrong", "{events={ts=[[@7,24:24='a',<459>,1:24], [@20,54:54='a',<459>,1:54], [@33,85:85='a',<459>,1:85], [@46,124:124='a',<459>,1:124], [@59,156:156='a',<459>,1:156], [@72,187:187='a',<459>,1:187]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={part3=[[@38,94:98='part3',<459>,1:94]], part4=[[@51,133:137='part4',<459>,1:133]], part5=[[@64,165:169='part5',<459>,1:165]], part6=[[@77,196:200='part6',<459>,1:196]], part1=[[@12,33:37='part1',<459>,1:33]], part2=[[@25,63:67='part2',<459>,1:63]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={part1=[[@12,33:37='part1',<459>,1:33]], part2=[[@25,63:67='part2',<459>,1:63]], part3=[[@38,94:98='part3',<459>,1:94]], part4=[[@51,133:137='part4',<459>,1:133]], part5=[[@64,165:169='part5',<459>,1:165]], part6=[[@77,196:200='part6',<459>,1:196]]}, table_dictionary={events={ts=[[@7,24:24='a',<459>,1:24], [@20,54:54='a',<459>,1:54], [@33,85:85='a',<459>,1:85], [@46,124:124='a',<459>,1:124], [@59,156:156='a',<459>,1:156], [@72,187:187='a',<459>,1:187]]}}, interface={part1=[{name=ts, table_ref=a}], part2=[{name=ts, table_ref=a}], part3=[{name=ts, table_ref=a}], part4=[{name=ts, table_ref=a}], part5=[{name=ts, table_ref=a}], part6=[{name=ts, table_ref=a}]}, table_alias={a=events}}}", extractor.getSymbolTable().toString());
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
		Assert.assertEquals("Table Dictionary is wrong", "{events={ts=[[@7,23:23='a',<459>,1:23], [@20,53:53='a',<459>,1:53], [@33,84:84='a',<459>,1:84], [@46,117:117='a',<459>,1:117], [@59,156:156='a',<459>,1:156]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={part3=[[@38,93:97='part3',<459>,1:93]], part4=[[@51,126:130='part4',<459>,1:126]], part5=[[@64,165:169='part5',<459>,1:165]], part1=[[@12,32:36='part1',<459>,1:32]], part2=[[@25,62:66='part2',<459>,1:62]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={part1=[[@12,32:36='part1',<459>,1:32]], part2=[[@25,62:66='part2',<459>,1:62]], part3=[[@38,93:97='part3',<459>,1:93]], part4=[[@51,126:130='part4',<459>,1:126]], part5=[[@64,165:169='part5',<459>,1:165]]}, table_dictionary={events={ts=[[@7,23:23='a',<459>,1:23], [@20,53:53='a',<459>,1:53], [@33,84:84='a',<459>,1:84], [@46,117:117='a',<459>,1:117], [@59,156:156='a',<459>,1:156]]}}, interface={part1=[{name=ts, table_ref=a}], part2=[{name=ts, table_ref=a}], part3=[{name=ts, table_ref=a}], part4=[{name=ts, table_ref=a}], part5=[{name=ts, table_ref=a}]}, table_alias={a=events}}}", extractor.getSymbolTable().toString());
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
		Assert.assertEquals("Table Dictionary is wrong", "{events={ts=[[@7,24:24='a',<459>,1:24], [@20,54:54='a',<459>,1:54], [@33,85:85='a',<459>,1:85], [@46,116:116='a',<459>,1:116], [@59,150:150='a',<459>,1:150], [@72,182:182='a',<459>,1:182]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={part3=[[@38,94:98='part3',<459>,1:94]], part4=[[@51,125:129='part4',<459>,1:125]], part5=[[@64,159:163='part5',<459>,1:159]], part6=[[@77,191:195='part6',<459>,1:191]], part1=[[@12,33:37='part1',<459>,1:33]], part2=[[@25,63:67='part2',<459>,1:63]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={part1=[[@12,33:37='part1',<459>,1:33]], part2=[[@25,63:67='part2',<459>,1:63]], part3=[[@38,94:98='part3',<459>,1:94]], part4=[[@51,125:129='part4',<459>,1:125]], part5=[[@64,159:163='part5',<459>,1:159]], part6=[[@77,191:195='part6',<459>,1:191]]}, table_dictionary={events={ts=[[@7,24:24='a',<459>,1:24], [@20,54:54='a',<459>,1:54], [@33,85:85='a',<459>,1:85], [@46,116:116='a',<459>,1:116], [@59,150:150='a',<459>,1:150], [@72,182:182='a',<459>,1:182]]}}, interface={part1=[{name=ts, table_ref=a}], part2=[{name=ts, table_ref=a}], part3=[{name=ts, table_ref=a}], part4=[{name=ts, table_ref=a}], part5=[{name=ts, table_ref=a}], part6=[{name=ts, table_ref=a}]}, table_alias={a=events}}}", extractor.getSymbolTable().toString());
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
		Assert.assertEquals("Table Dictionary is wrong", "{events={ts=[[@7,26:26='a',<459>,1:26], [@20,56:56='a',<459>,1:56], [@33,87:87='a',<459>,1:87], [@46,119:119='a',<459>,1:119], [@59,155:155='a',<459>,1:155], [@72,188:188='a',<459>,1:188]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={part3=[[@38,96:100='part3',<459>,1:96]], part4=[[@51,128:132='part4',<459>,1:128]], part5=[[@64,164:168='part5',<459>,1:164]], part6=[[@77,197:201='part6',<459>,1:197]], part1=[[@12,35:39='part1',<459>,1:35]], part2=[[@25,65:69='part2',<459>,1:65]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={part1=[[@12,35:39='part1',<459>,1:35]], part2=[[@25,65:69='part2',<459>,1:65]], part3=[[@38,96:100='part3',<459>,1:96]], part4=[[@51,128:132='part4',<459>,1:128]], part5=[[@64,164:168='part5',<459>,1:164]], part6=[[@77,197:201='part6',<459>,1:197]]}, table_dictionary={events={ts=[[@7,26:26='a',<459>,1:26], [@20,56:56='a',<459>,1:56], [@33,87:87='a',<459>,1:87], [@46,119:119='a',<459>,1:119], [@59,155:155='a',<459>,1:155], [@72,188:188='a',<459>,1:188]]}}, interface={part1=[{name=ts, table_ref=a}], part2=[{name=ts, table_ref=a}], part3=[{name=ts, table_ref=a}], part4=[{name=ts, table_ref=a}], part5=[{name=ts, table_ref=a}], part6=[{name=ts, table_ref=a}]}, table_alias={a=events}}}", extractor.getSymbolTable().toString());
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
		Assert.assertEquals("Table Dictionary is wrong", "{events={ts=[[@7,26:26='a',<459>,1:26], [@20,56:56='a',<459>,1:56], [@33,88:88='a',<459>,1:88], [@46,124:124='a',<459>,1:124], [@59,157:157='a',<459>,1:157]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={part3=[[@38,97:101='part3',<459>,1:97]], part4=[[@51,133:137='part4',<459>,1:133]], part5=[[@64,166:170='part5',<459>,1:166]], part1=[[@12,35:39='part1',<459>,1:35]], part2=[[@25,65:69='part2',<459>,1:65]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={part1=[[@12,35:39='part1',<459>,1:35]], part2=[[@25,65:69='part2',<459>,1:65]], part3=[[@38,97:101='part3',<459>,1:97]], part4=[[@51,133:137='part4',<459>,1:133]], part5=[[@64,166:170='part5',<459>,1:166]]}, table_dictionary={events={ts=[[@7,26:26='a',<459>,1:26], [@20,56:56='a',<459>,1:56], [@33,88:88='a',<459>,1:88], [@46,124:124='a',<459>,1:124], [@59,157:157='a',<459>,1:157]]}}, interface={part1=[{name=ts, table_ref=a}], part2=[{name=ts, table_ref=a}], part3=[{name=ts, table_ref=a}], part4=[{name=ts, table_ref=a}], part5=[{name=ts, table_ref=a}]}, table_alias={a=events}}}", extractor.getSymbolTable().toString());
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
		Assert.assertEquals("Table Dictionary is wrong", "{events={ts=[[@7,31:31='a',<459>,1:31], [@20,62:62='a',<459>,1:62], [@33,95:95='a',<459>,1:95], [@46,136:136='a',<459>,1:136]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={part3=[[@38,104:108='part3',<459>,1:104]], part4=[[@51,145:149='part4',<459>,1:145]], part1=[[@12,40:44='part1',<459>,1:40]], part2=[[@25,71:75='part2',<459>,1:71]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={part1=[[@12,40:44='part1',<459>,1:40]], part2=[[@25,71:75='part2',<459>,1:71]], part3=[[@38,104:108='part3',<459>,1:104]], part4=[[@51,145:149='part4',<459>,1:145]]}, table_dictionary={events={ts=[[@7,31:31='a',<459>,1:31], [@20,62:62='a',<459>,1:62], [@33,95:95='a',<459>,1:95], [@46,136:136='a',<459>,1:136]]}}, interface={part1=[{name=ts, table_ref=a}], part2=[{name=ts, table_ref=a}], part3=[{name=ts, table_ref=a}], part4=[{name=ts, table_ref=a}]}, table_alias={a=events}}}", extractor.getSymbolTable().toString());
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
		Assert.assertEquals("Table Dictionary is wrong", "{events={ts=[[@7,31:31='a',<459>,1:31], [@20,62:62='a',<459>,1:62], [@33,95:95='a',<459>,1:95], [@46,136:136='a',<459>,1:136]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={part3=[[@38,104:108='part3',<459>,1:104]], part4=[[@51,145:149='part4',<459>,1:145]], part1=[[@12,40:44='part1',<459>,1:40]], part2=[[@25,71:75='part2',<459>,1:71]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={part1=[[@12,40:44='part1',<459>,1:40]], part2=[[@25,71:75='part2',<459>,1:71]], part3=[[@38,104:108='part3',<459>,1:104]], part4=[[@51,145:149='part4',<459>,1:145]]}, table_dictionary={events={ts=[[@7,31:31='a',<459>,1:31], [@20,62:62='a',<459>,1:62], [@33,95:95='a',<459>,1:95], [@46,136:136='a',<459>,1:136]]}}, interface={part1=[{name=ts, table_ref=a}], part2=[{name=ts, table_ref=a}], part3=[{name=ts, table_ref=a}], part4=[{name=ts, table_ref=a}]}, table_alias={a=events}}}", extractor.getSymbolTable().toString());
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
		Assert.assertEquals("Table Dictionary is wrong", "{events={ts=[[@7,30:30='a',<459>,1:30], [@20,61:61='a',<459>,1:61], [@33,94:94='a',<459>,1:94], [@46,130:130='a',<459>,1:130], [@59,166:166='a',<459>,1:166], [@72,206:206='a',<459>,1:206], [@85,243:243='a',<459>,1:243], [@98,280:280='a',<459>,1:280]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={part1=[[@12,39:43='part1',<459>,1:39]], part2=[[@25,70:74='part2',<459>,1:70]], part3=[[@38,103:107='part3',<459>,1:103]], part4=[[@51,139:143='part4',<459>,1:139]], part5=[[@64,175:179='part5',<459>,1:175]], part6=[[@77,215:219='part6',<459>,1:215]], part7=[[@90,252:256='part7',<459>,1:252]], part8=[[@103,289:293='part8',<459>,1:289]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={part1=[[@12,39:43='part1',<459>,1:39]], part2=[[@25,70:74='part2',<459>,1:70]], part3=[[@38,103:107='part3',<459>,1:103]], part4=[[@51,139:143='part4',<459>,1:139]], part5=[[@64,175:179='part5',<459>,1:175]], part6=[[@77,215:219='part6',<459>,1:215]], part7=[[@90,252:256='part7',<459>,1:252]], part8=[[@103,289:293='part8',<459>,1:289]]}, table_dictionary={events={ts=[[@7,30:30='a',<459>,1:30], [@20,61:61='a',<459>,1:61], [@33,94:94='a',<459>,1:94], [@46,130:130='a',<459>,1:130], [@59,166:166='a',<459>,1:166], [@72,206:206='a',<459>,1:206], [@85,243:243='a',<459>,1:243], [@98,280:280='a',<459>,1:280]]}}, interface={part1=[{name=ts, table_ref=a}], part2=[{name=ts, table_ref=a}], part3=[{name=ts, table_ref=a}], part4=[{name=ts, table_ref=a}], part5=[{name=ts, table_ref=a}], part6=[{name=ts, table_ref=a}], part7=[{name=ts, table_ref=a}], part8=[{name=ts, table_ref=a}]}, table_alias={a=events}}}", extractor.getSymbolTable().toString());
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
		Assert.assertEquals("Table Dictionary is wrong", "{events={ts=[[@7,24:24='a',<459>,1:24], [@20,51:51='a',<459>,1:51], [@33,82:82='a',<459>,1:82], [@46,119:119='a',<459>,1:119], [@59,145:145='a',<459>,1:145], [@72,183:183='a',<459>,1:183], [@85,211:211='a',<459>,1:211]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={mo=[[@25,60:61='mo',<459>,1:60]], d=[[@51,128:128='d',<459>,1:128]], ns=[[@90,220:221='ns',<459>,1:220]], ms=[[@77,192:193='ms',<459>,1:192]], y=[[@12,33:33='y',<459>,1:33]], mi=[[@64,154:155='mi',<459>,1:154]], mon=[[@38,91:93='mon',<459>,1:91]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={mo=[[@25,60:61='mo',<459>,1:60]], d=[[@51,128:128='d',<459>,1:128]], ns=[[@90,220:221='ns',<459>,1:220]], ms=[[@77,192:193='ms',<459>,1:192]], y=[[@12,33:33='y',<459>,1:33]], mi=[[@64,154:155='mi',<459>,1:154]], mon=[[@38,91:93='mon',<459>,1:91]]}, table_dictionary={events={ts=[[@7,24:24='a',<459>,1:24], [@20,51:51='a',<459>,1:51], [@33,82:82='a',<459>,1:82], [@46,119:119='a',<459>,1:119], [@59,145:145='a',<459>,1:145], [@72,183:183='a',<459>,1:183], [@85,211:211='a',<459>,1:211]]}}, interface={mo=[{name=ts, table_ref=a}], d=[{name=ts, table_ref=a}], ns=[{name=ts, table_ref=a}], ms=[{name=ts, table_ref=a}], y=[{name=ts, table_ref=a}], mi=[{name=ts, table_ref=a}], mon=[{name=ts, table_ref=a}]}, table_alias={a=events}}}", extractor.getSymbolTable().toString());
	}

	@Test
	public void dateaddPreservesQuotedCanonicalLevelSpelling() {
		final String query = "select DaTeAdD('yEaR', 1, a.ts) as shifted from events a";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=shifted, datetime_function={function_name=DaTeAdD, level={canonical=YEAR, text=yEaR, form=STRING}, increment={literal=1}, value={column={name=ts, table_ref=a}}}}}, from={table={alias=a, table=events}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[shifted]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{events={ts=[[@7,26:26='a',<459>,1:26]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={shifted=[[@12,35:41='shifted',<459>,1:35]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={shifted=[[@12,35:41='shifted',<459>,1:35]]}, table_dictionary={events={ts=[[@7,26:26='a',<459>,1:26]]}}, interface={shifted=[{name=ts, table_ref=a}]}, table_alias={a=events}}}", extractor.getSymbolTable().toString());
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
		Assert.assertEquals("Table Dictionary is wrong", "{events={year=[[@5,21:21='a',<459>,1:21]], day=[[@22,63:63='a',<459>,1:63]], ts=[[@11,33:33='a',<459>,1:33], [@28,74:74='a',<459>,1:74]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={annual=[[@16,42:47='annual',<459>,1:42]], daily=[[@33,83:87='daily',<459>,1:83]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={daily=[[@33,83:87='daily',<459>,1:83]], annual=[[@16,42:47='annual',<459>,1:42]]}, table_dictionary={events={year=[[@5,21:21='a',<459>,1:21]], day=[[@22,63:63='a',<459>,1:63]], ts=[[@11,33:33='a',<459>,1:33], [@28,74:74='a',<459>,1:74]]}}, interface={daily=[{name=day, table_ref=a}, {name=ts, table_ref=a}], annual=[{name=year, table_ref=a}, {name=ts, table_ref=a}]}, table_alias={a=events}}}", extractor.getSymbolTable().toString());
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
		Assert.assertEquals("Table Dictionary is wrong", "{adjustments={delta=[[@53,153:153='b',<459>,1:153]], id=[[@77,230:230='b',<459>,1:230]]}, events={offset_days=[[@33,90:90='a',<459>,1:90], [@49,137:137='a',<459>,1:137]], id=[[@73,223:223='a',<459>,1:223]], ts=[[@7,23:23='a',<459>,1:23], [@21,57:57='a',<459>,1:57], [@37,105:105='a',<459>,1:105], [@60,167:167='a',<459>,1:167]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={negative=[[@26,66:73='negative',<459>,1:66]], negated=[[@42,114:120='negated',<459>,1:114]], zeroed=[[@12,32:37='zeroed',<459>,1:32]], calculated=[[@65,176:185='calculated',<459>,1:176]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={negative=[[@26,66:73='negative',<459>,1:66]], negated=[[@42,114:120='negated',<459>,1:114]], zeroed=[[@12,32:37='zeroed',<459>,1:32]], calculated=[[@65,176:185='calculated',<459>,1:176]]}, table_dictionary={adjustments={delta=[[@53,153:153='b',<459>,1:153]], id=[[@77,230:230='b',<459>,1:230]]}, events={offset_days=[[@33,90:90='a',<459>,1:90], [@49,137:137='a',<459>,1:137]], id=[[@73,223:223='a',<459>,1:223]], ts=[[@7,23:23='a',<459>,1:23], [@21,57:57='a',<459>,1:57], [@37,105:105='a',<459>,1:105], [@60,167:167='a',<459>,1:167]]}}, filters=[{name=id, table_ref=a}, {name=id, table_ref=b}], interface={negative=[{name=ts, table_ref=a}], negated=[{name=offset_days, table_ref=a}, {name=ts, table_ref=a}], zeroed=[{name=ts, table_ref=a}], calculated=[{name=offset_days, table_ref=a}, {name=delta, table_ref=b}, {name=ts, table_ref=a}]}, table_alias={a=events, b=adjustments}}}", extractor.getSymbolTable().toString());
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
		Assert.assertEquals("Table Dictionary is wrong", "{adjustments={hours=[[@28,103:103='b',<459>,1:103], [@32,113:113='b',<459>,1:113], [@40,129:129='b',<459>,1:129]], id=[[@57,197:197='b',<459>,1:197]], ts=[[@36,122:122='b',<459>,1:122]]}, events={offset_days=[[@5,20:20='a',<459>,1:20], [@9,36:36='a',<459>,1:36], [@17,58:58='a',<459>,1:58]], id=[[@53,190:190='a',<459>,1:190]], ts=[[@13,51:51='a',<459>,1:51]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={first_shift=[[@22,76:86='first_shift',<459>,1:76]], second_shift=[[@45,141:152='second_shift',<459>,1:141]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={first_shift=[[@22,76:86='first_shift',<459>,1:76]], second_shift=[[@45,141:152='second_shift',<459>,1:141]]}, table_dictionary={adjustments={hours=[[@28,103:103='b',<459>,1:103], [@32,113:113='b',<459>,1:113], [@40,129:129='b',<459>,1:129]], id=[[@57,197:197='b',<459>,1:197]], ts=[[@36,122:122='b',<459>,1:122]]}, events={offset_days=[[@5,20:20='a',<459>,1:20], [@9,36:36='a',<459>,1:36], [@17,58:58='a',<459>,1:58]], id=[[@53,190:190='a',<459>,1:190]], ts=[[@13,51:51='a',<459>,1:51]]}}, filters=[{name=id, table_ref=a}, {name=id, table_ref=b}], interface={first_shift=[{name=offset_days, table_ref=a}, {name=ts, table_ref=a}], second_shift=[{name=hours, table_ref=b}, {name=ts, table_ref=b}]}, table_alias={a=events, b=adjustments}}}", extractor.getSymbolTable().toString());
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
		Assert.assertEquals("Table Dictionary is wrong", "{adjustments={hours=[[@40,116:116='b',<459>,1:116]], id=[[@62,185:185='b',<459>,1:185]], ts=[[@44,125:125='b',<459>,1:125]]}, events={offset_days=[[@27,80:80='a',<459>,1:80]], id=[[@58,178:178='a',<459>,1:178]], ts=[[@3,16:16='a',<459>,1:16], [@13,38:38='a',<459>,1:38], [@31,95:95='a',<459>,1:95]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={fallback=[[@19,48:55='fallback',<459>,1:48]], latest=[[@50,135:140='latest',<459>,1:135]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={fallback=[[@19,48:55='fallback',<459>,1:48]], latest=[[@50,135:140='latest',<459>,1:135]]}, table_dictionary={adjustments={hours=[[@40,116:116='b',<459>,1:116]], id=[[@62,185:185='b',<459>,1:185]], ts=[[@44,125:125='b',<459>,1:125]]}, events={offset_days=[[@27,80:80='a',<459>,1:80]], id=[[@58,178:178='a',<459>,1:178]], ts=[[@3,16:16='a',<459>,1:16], [@13,38:38='a',<459>,1:38], [@31,95:95='a',<459>,1:95]]}}, filters=[{name=id, table_ref=a}, {name=id, table_ref=b}], interface={fallback=[{name=ts, table_ref=a}], latest=[{name=offset_days, table_ref=a}, {name=ts, table_ref=a}, {name=hours, table_ref=b}, {name=ts, table_ref=b}]}, table_alias={a=events, b=adjustments}}}", extractor.getSymbolTable().toString());
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
		Assert.assertEquals("Table Dictionary is wrong", "{events={<fallback2>=[[@60,176:176='a',<459>,1:176]], days=[[@7,24:24='a',<459>,1:24], [@47,143:143='a',<459>,1:143]], <fallback>=[[@33,96:96='a',<459>,1:96]], ts=[[@14,41:41='a',<459>,1:41], [@29,90:90='a',<459>,1:90], [@56,170:170='a',<459>,1:170]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={numeric_inner=[[@19,50:62='numeric_inner',<459>,1:50]], both_inner=[[@66,195:204='both_inner',<459>,1:195]], date_inner=[[@39,114:123='date_inner',<459>,1:114]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={date_inner=[[@39,114:123='date_inner',<459>,1:114]], numeric_inner=[[@19,50:62='numeric_inner',<459>,1:50]], both_inner=[[@66,195:204='both_inner',<459>,1:195]]}, table_dictionary={events={<fallback2>=[[@60,176:176='a',<459>,1:176]], days=[[@7,24:24='a',<459>,1:24], [@47,143:143='a',<459>,1:143]], <fallback>=[[@33,96:96='a',<459>,1:96]], ts=[[@14,41:41='a',<459>,1:41], [@29,90:90='a',<459>,1:90], [@56,170:170='a',<459>,1:170]]}}, interface={date_inner=[{name=ts, table_ref=a}, {substitution={name=<fallback>, type=column}, table_ref=a}], numeric_inner=[{name=days, table_ref=a}, {name=<inc>, type=predicand}, {name=ts, table_ref=a}], both_inner=[{name=days, table_ref=a}, {name=<inc2>, type=predicand}, {name=ts, table_ref=a}, {substitution={name=<fallback2>, type=column}, table_ref=a}]}, table_alias={a=events}}}", extractor.getSymbolTable().toString());
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
		Assert.assertEquals("Table Dictionary is wrong", "{events={outer_days=[[@25,82:82='a',<459>,1:82]], created_at=[[@13,41:41='a',<459>,1:41], [@41,143:143='a',<459>,1:143]], inner_days=[[@35,119:119='a',<459>,1:119]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={nested=[[@47,161:166='nested',<459>,1:161]], simple=[[@19,59:64='simple',<459>,1:59]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={simple=[[@19,59:64='simple',<459>,1:59]], nested=[[@47,161:166='nested',<459>,1:161]]}, table_dictionary={events={outer_days=[[@25,82:82='a',<459>,1:82]], created_at=[[@13,41:41='a',<459>,1:41], [@41,143:143='a',<459>,1:143]], inner_days=[[@35,119:119='a',<459>,1:119]]}}, interface={simple=[{name=created_at, table_ref=a}], nested=[{name=outer_days, table_ref=a}, {name=<outer>, type=predicand}, {name=inner_days, table_ref=a}, {name=<inner>, type=predicand}, {name=created_at, table_ref=a}]}, table_alias={a=events}}}", extractor.getSymbolTable().toString());
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
		Assert.assertEquals("Table Dictionary is wrong", "{events={flag=[[@3,17:17='a',<459>,1:17], [@42,135:135='a',<459>,1:135]], backup_ts=[[@28,82:82='a',<459>,1:82], [@67,208:208='a',<459>,1:208]], active=[[@57,180:180='a',<459>,1:180]], days=[[@13,46:46='a',<459>,1:46], [@48,151:151='a',<459>,1:151]], ts=[[@17,54:54='a',<459>,1:54], [@63,198:198='a',<459>,1:198]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={branched=[[@34,102:109='branched',<459>,1:102]], arguments=[[@73,228:236='arguments',<459>,1:228]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={arguments=[[@73,228:236='arguments',<459>,1:228]], branched=[[@34,102:109='branched',<459>,1:102]]}, table_dictionary={events={flag=[[@3,17:17='a',<459>,1:17], [@42,135:135='a',<459>,1:135]], backup_ts=[[@28,82:82='a',<459>,1:82], [@67,208:208='a',<459>,1:208]], active=[[@57,180:180='a',<459>,1:180]], days=[[@13,46:46='a',<459>,1:46], [@48,151:151='a',<459>,1:151]], ts=[[@17,54:54='a',<459>,1:54], [@63,198:198='a',<459>,1:198]]}}, interface={arguments=[{name=days, table_ref=a}, {name=flag, table_ref=a}, {name=ts, table_ref=a}, {name=active, table_ref=a}, {name=backup_ts, table_ref=a}], branched=[{name=days, table_ref=a}, {name=ts, table_ref=a}, {name=flag, table_ref=a}, {name=backup_ts, table_ref=a}]}, table_alias={a=events}}}", extractor.getSymbolTable().toString());
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
		Assert.assertEquals("Table Dictionary is wrong", "{events={created_at=[[@15,57:57='a',<459>,1:57]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={shifted=[[@20,74:80='shifted',<459>,1:74], [@1,7:7='d',<459>,1:7]]}, query1={result=[[@5,20:25='result',<459>,1:20]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query1={query_dictionary={result=[[@5,20:25='result',<459>,1:20]]}, def_query0={query_dictionary={shifted=[[@20,74:80='shifted',<459>,1:74], [@1,7:7='d',<459>,1:7]]}, table_dictionary={events={created_at=[[@15,57:57='a',<459>,1:57]]}}, interface={shifted=[{name=created_at, table_ref=a}]}, table_alias={a=events}}, interface={result=[{name=shifted, table_ref=d}]}, table_alias={d=query0}}}", extractor.getSymbolTable().toString());

		final String expectedQualifiedDefQuery1DefQuery0QueryDictionary_0 = "{shifted=[[@20,74:80='shifted',<459>,1:74], [@1,7:7='d',<459>,1:7]]}";
		assertSymbolTreePathEquals(extractor.getSymbolTable(), "def_query1.def_query0.query_dictionary", expectedQualifiedDefQuery1DefQuery0QueryDictionary_0);
		final String expectedQualifiedDefQuery1DefQuery0TableDictionary_1 = "{events={created_at=[[@15,57:57='a',<459>,1:57]]}}";
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
		Assert.assertEquals("Table Dictionary is wrong", "{events={created_at=[[@15,57:66='created_at',<459>,1:57]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={shifted=[[@18,72:78='shifted',<459>,1:72], [@1,7:7='d',<459>,1:7]]}, query1={result=[[@5,20:25='result',<459>,1:20]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query1={query_dictionary={result=[[@5,20:25='result',<459>,1:20]]}, def_query0={query_dictionary={shifted=[[@18,72:78='shifted',<459>,1:72], [@1,7:7='d',<459>,1:7]]}, table_dictionary={events={created_at=[[@15,57:66='created_at',<459>,1:57]]}}, interface={shifted=[{name=created_at, table_ref=events}]}, table_alias={a=events}}, interface={result=[{name=shifted, table_ref=d}]}, table_alias={d=query0}}}", extractor.getSymbolTable().toString());

		final String expectedUnqualifiedDefQuery1DefQuery0QueryDictionary_0 = "{shifted=[[@18,72:78='shifted',<459>,1:72], [@1,7:7='d',<459>,1:7]]}";
		assertSymbolTreePathEquals(extractor.getSymbolTable(), "def_query1.def_query0.query_dictionary", expectedUnqualifiedDefQuery1DefQuery0QueryDictionary_0);
		final String expectedUnqualifiedDefQuery1DefQuery0TableDictionary_1 = "{events={created_at=[[@15,57:66='created_at',<459>,1:57]]}}";
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
		Assert.assertEquals("Table Dictionary is wrong", "{accounts={extra_days=[[@17,69:69='b',<459>,1:69]], id=[[@39,165:165='b',<459>,1:165]]}, events={account_id=[[@35,150:150='a',<459>,1:150]], offset_days=[[@13,53:53='a',<459>,1:53]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={shifted=[[@27,109:115='shifted',<459>,1:109], [@1,7:7='d',<459>,1:7]]}, query1={result=[[@5,20:25='result',<459>,1:20]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query1={query_dictionary={result=[[@5,20:25='result',<459>,1:20]]}, def_query0={query_dictionary={shifted=[[@27,109:115='shifted',<459>,1:109], [@1,7:7='d',<459>,1:7]]}, table_dictionary={accounts={extra_days=[[@17,69:69='b',<459>,1:69]], id=[[@39,165:165='b',<459>,1:165]]}, events={account_id=[[@35,150:150='a',<459>,1:150]], offset_days=[[@13,53:53='a',<459>,1:53]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}], interface={shifted=[{name=offset_days, table_ref=a}, {name=extra_days, table_ref=b}]}, table_alias={a=events, b=accounts}}, interface={result=[{name=shifted, table_ref=d}]}, table_alias={d=query0}}}", extractor.getSymbolTable().toString());

		final String expectedJoinedDefQuery1DefQuery0QueryDictionary_0 = "{shifted=[[@27,109:115='shifted',<459>,1:109], [@1,7:7='d',<459>,1:7]]}";
		assertSymbolTreePathEquals(extractor.getSymbolTable(), "def_query1.def_query0.query_dictionary", expectedJoinedDefQuery1DefQuery0QueryDictionary_0);
		final String expectedJoinedDefQuery1DefQuery0TableDictionary_1 = "{accounts={extra_days=[[@17,69:69='b',<459>,1:69]], id=[[@39,165:165='b',<459>,1:165]]}, events={account_id=[[@35,150:150='a',<459>,1:150]], offset_days=[[@13,53:53='a',<459>,1:53]]}}";
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
	public void dateaddConsumesDerivedTableArguments() {
		final String query = "select dateadd(day, d.offset_days, d.created_at) as shifted from (select a.offset_days, a.created_at from events a) d";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=shifted, datetime_function={function_name=dateadd, level={canonical=DAY, text=day, form=KEYWORD}, increment={column={name=offset_days, table_ref=d}}, value={column={name=created_at, table_ref=d}}}}}, from={table={alias=d, query={select={1={column={name=offset_days, table_ref=a}}, 2={column={name=created_at, table_ref=a}}}, from={table={alias=a, table=events}}}}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[shifted]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{events={created_at=[[@22,88:88='a',<459>,1:88]], offset_days=[[@18,73:73='a',<459>,1:73]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={created_at=[[@24,90:99='created_at',<459>,1:90], [@9,35:35='d',<459>,1:35]], offset_days=[[@20,75:85='offset_days',<459>,1:75], [@5,20:20='d',<459>,1:20]]}, query1={shifted=[[@14,52:58='shifted',<459>,1:52]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query1={query_dictionary={shifted=[[@14,52:58='shifted',<459>,1:52]]}, def_query0={query_dictionary={created_at=[[@24,90:99='created_at',<459>,1:90], [@9,35:35='d',<459>,1:35]], offset_days=[[@20,75:85='offset_days',<459>,1:75], [@5,20:20='d',<459>,1:20]]}, table_dictionary={events={created_at=[[@22,88:88='a',<459>,1:88]], offset_days=[[@18,73:73='a',<459>,1:73]]}}, interface={created_at=[{name=created_at, table_ref=a}], offset_days=[{name=offset_days, table_ref=a}]}, table_alias={a=events}}, interface={shifted=[{name=offset_days, table_ref=d}, {name=created_at, table_ref=d}]}, table_alias={d=query0}}}", extractor.getSymbolTable().toString());

		final String expectedSingleDefQuery1DefQuery0Interface_2 = "{created_at=[{name=created_at, table_ref=a}], offset_days=[{name=offset_days, table_ref=a}]}";
		assertSymbolTreePathEquals(extractor.getSymbolTable(), "def_query1.def_query0.interface", expectedSingleDefQuery1DefQuery0Interface_2);
		final String expectedSingleDefQuery1Interface_4 = "{shifted=[{name=offset_days, table_ref=d}, {name=created_at, table_ref=d}]}";
		assertSymbolTreePathEquals(extractor.getSymbolTable(), "def_query1.interface", expectedSingleDefQuery1Interface_4);
		final String expectedSingleDefQuery1TableAlias_5 = "{d=query0}";
		assertSymbolTreePathEquals(extractor.getSymbolTable(), "def_query1.table_alias", expectedSingleDefQuery1TableAlias_5);
	}

	@Test
	public void dateaddConsumesRenamedArgumentsAcrossDerivedLayers() {
		final String query = "select dateadd(day, d.adjustment, d.event_time) as shifted from (select s.offset_days as adjustment, s.created_at as event_time from (select a.raw_days as offset_days, a.raw_ts as created_at from events a) s) d";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=shifted, datetime_function={function_name=dateadd, level={canonical=DAY, text=day, form=KEYWORD}, increment={column={name=adjustment, table_ref=d}}, value={column={name=event_time, table_ref=d}}}}}, from={table={alias=d, query={select={1={column={name=offset_days, table_ref=s}, alias=adjustment}, 2={column={name=created_at, table_ref=s}, alias=event_time}}, from={table={alias=s, query={select={1={column={name=raw_days, table_ref=a}, alias=offset_days}, 2={column={name=raw_ts, table_ref=a}, alias=created_at}}, from={table={alias=a, table=events}}}}}}}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[shifted]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{events={raw_ts=[[@38,168:168='a',<459>,1:168]], raw_days=[[@32,141:141='a',<459>,1:141]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={created_at=[[@42,180:189='created_at',<459>,1:180], [@24,101:101='s',<459>,1:101]], offset_days=[[@36,155:165='offset_days',<459>,1:155], [@18,72:72='s',<459>,1:72]]}, query1={adjustment=[[@22,89:98='adjustment',<459>,1:89], [@5,20:20='d',<459>,1:20]], event_time=[[@28,117:126='event_time',<459>,1:117], [@9,34:34='d',<459>,1:34]]}, query2={shifted=[[@14,51:57='shifted',<459>,1:51]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query2={query_dictionary={shifted=[[@14,51:57='shifted',<459>,1:51]]}, def_query1={query_dictionary={adjustment=[[@22,89:98='adjustment',<459>,1:89], [@5,20:20='d',<459>,1:20]], event_time=[[@28,117:126='event_time',<459>,1:117], [@9,34:34='d',<459>,1:34]]}, def_query0={query_dictionary={created_at=[[@42,180:189='created_at',<459>,1:180], [@24,101:101='s',<459>,1:101]], offset_days=[[@36,155:165='offset_days',<459>,1:155], [@18,72:72='s',<459>,1:72]]}, table_dictionary={events={raw_ts=[[@38,168:168='a',<459>,1:168]], raw_days=[[@32,141:141='a',<459>,1:141]]}}, interface={created_at=[{name=raw_ts, table_ref=a}], offset_days=[{name=raw_days, table_ref=a}]}, table_alias={a=events}}, interface={adjustment=[{name=offset_days, table_ref=s}], event_time=[{name=created_at, table_ref=s}]}, table_alias={s=query0}}, interface={shifted=[{name=adjustment, table_ref=d}, {name=event_time, table_ref=d}]}, table_alias={d=query1}}}", extractor.getSymbolTable().toString());

		final String expectedNestedDefQuery2DefQuery1DefQuery0QueryDictionary_1 = "{created_at=[[@42,180:189='created_at',<459>,1:180], [@24,101:101='s',<459>,1:101]], offset_days=[[@36,155:165='offset_days',<459>,1:155], [@18,72:72='s',<459>,1:72]]}";
		assertSymbolTreePathEquals(extractor.getSymbolTable(), "def_query2.def_query1.def_query0.query_dictionary", expectedNestedDefQuery2DefQuery1DefQuery0QueryDictionary_1);
		final String expectedNestedDefQuery2DefQuery1QueryDictionary_0 = "{adjustment=[[@22,89:98='adjustment',<459>,1:89], [@5,20:20='d',<459>,1:20]], event_time=[[@28,117:126='event_time',<459>,1:117], [@9,34:34='d',<459>,1:34]]}";
		assertSymbolTreePathEquals(extractor.getSymbolTable(), "def_query2.def_query1.query_dictionary", expectedNestedDefQuery2DefQuery1QueryDictionary_0);
		final String expectedNestedDefQuery2DefQuery1DefQuery0Interface_3 = "{created_at=[{name=raw_ts, table_ref=a}], offset_days=[{name=raw_days, table_ref=a}]}";
		assertSymbolTreePathEquals(extractor.getSymbolTable(), "def_query2.def_query1.def_query0.interface", expectedNestedDefQuery2DefQuery1DefQuery0Interface_3);
		final String expectedNestedDefQuery2DefQuery1DefQuery0TableAlias_4 = "{a=events}";
		assertSymbolTreePathEquals(extractor.getSymbolTable(), "def_query2.def_query1.def_query0.table_alias", expectedNestedDefQuery2DefQuery1DefQuery0TableAlias_4);
		final String expectedNestedDefQuery2DefQuery1Interface_5 = "{adjustment=[{name=offset_days, table_ref=s}], event_time=[{name=created_at, table_ref=s}]}";
		assertSymbolTreePathEquals(extractor.getSymbolTable(), "def_query2.def_query1.interface", expectedNestedDefQuery2DefQuery1Interface_5);
		final String expectedNestedDefQuery2DefQuery1TableAlias_6 = "{s=query0}";
		assertSymbolTreePathEquals(extractor.getSymbolTable(), "def_query2.def_query1.table_alias", expectedNestedDefQuery2DefQuery1TableAlias_6);
		final String expectedNestedDefQuery2Interface_7 = "{shifted=[{name=adjustment, table_ref=d}, {name=event_time, table_ref=d}]}";
		assertSymbolTreePathEquals(extractor.getSymbolTable(), "def_query2.interface", expectedNestedDefQuery2Interface_7);
		final String expectedNestedDefQuery2TableAlias_8 = "{d=query1}";
		assertSymbolTreePathEquals(extractor.getSymbolTable(), "def_query2.table_alias", expectedNestedDefQuery2TableAlias_8);
	}

	@Test
	public void dateaddRejectsHiddenLeafIncrementAcrossDerivedLayers() {
		String query = "select dateadd(day, d.raw_days, d.event_time) as shifted from (select s.offset_days as adjustment, s.created_at as event_time from (select a.raw_days as offset_days, a.raw_ts as created_at from events a) s) d";
		SqlParserAccess parser = new SqlParserAccess(false, false, false);
		parser.executeTheParse(query, "SQL");
		Assert.assertEquals(0, parser.getParser().getNumberOfSyntaxErrors());
		Assert.assertEquals(parser.getAllDiagnostics().toString(), 2, parser.getAllDiagnostics().size());
		assertSnowflakeGrammarWarningAt(parser, "snowflake_datetime_add_expression",
				invocationOffset(query, "dateadd"));
		assertFatalDiagnosticAtPositionWithFullMessage(parser.getSnippet(), "QUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIAS",
				"Qualified column 'raw_days' at (l:1 c:20) was not found in output interface of query alias 'd'.",
				"d.raw_days", 1, 20);
		Assert.assertEquals("d.raw_days", findFatalDiagnosticByCodeAndFragment(parser.getSnippet(),
				"QUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIAS", "d.raw_days").tokenText());
		Assert.assertEquals("{def_query2={query_dictionary={shifted=[[@14,49:55='shifted',<459>,1:49]]}, def_query1={query_dictionary={adjustment=[[@22,87:96='adjustment',<459>,1:87]], event_time=[[@28,115:124='event_time',<459>,1:115], [@9,32:32='d',<459>,1:32]]}, def_query0={query_dictionary={created_at=[[@42,178:187='created_at',<459>,1:178], [@24,99:99='s',<459>,1:99]], offset_days=[[@36,153:163='offset_days',<459>,1:153], [@18,70:70='s',<459>,1:70]]}, table_dictionary={events={raw_ts=[[@38,166:166='a',<459>,1:166]], raw_days=[[@32,139:139='a',<459>,1:139]]}}, interface={created_at=[{name=raw_ts, table_ref=a}], offset_days=[{name=raw_days, table_ref=a}]}, table_alias={a=events}}, interface={adjustment=[{name=offset_days, table_ref=s}], event_time=[{name=created_at, table_ref=s}]}, table_alias={s=query0}}, interface={shifted=[{name=raw_days, table_ref=d}, {name=event_time, table_ref=d}]}, table_alias={d=query1}}}", parser.getSnippet().getSymbolTable().toString());
	}

	@Test
	public void dateaddRejectsHiddenLeafValueAcrossDerivedLayers() {
		String query = "select dateadd(day, d.adjustment, d.raw_ts) as shifted from (select s.offset_days as adjustment, s.created_at as event_time from (select a.raw_days as offset_days, a.raw_ts as created_at from events a) s) d";
		SqlParserAccess parser = new SqlParserAccess(false, false, false);
		parser.executeTheParse(query, "SQL");
		Assert.assertEquals(0, parser.getParser().getNumberOfSyntaxErrors());
		Assert.assertEquals(parser.getAllDiagnostics().toString(), 2, parser.getAllDiagnostics().size());
		assertSnowflakeGrammarWarningAt(parser, "snowflake_datetime_add_expression",
				invocationOffset(query, "dateadd"));
		assertFatalDiagnosticAtPositionWithFullMessage(parser.getSnippet(), "QUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIAS",
				"Qualified column 'raw_ts' at (l:1 c:34) was not found in output interface of query alias 'd'.",
				"d.raw_ts", 1, 34);
		Assert.assertEquals("d.raw_ts", findFatalDiagnosticByCodeAndFragment(parser.getSnippet(),
				"QUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIAS", "d.raw_ts").tokenText());
		Assert.assertEquals("{def_query2={query_dictionary={shifted=[[@14,47:53='shifted',<459>,1:47]]}, def_query1={query_dictionary={adjustment=[[@22,85:94='adjustment',<459>,1:85], [@5,20:20='d',<459>,1:20]], event_time=[[@28,113:122='event_time',<459>,1:113]]}, def_query0={query_dictionary={created_at=[[@42,176:185='created_at',<459>,1:176], [@24,97:97='s',<459>,1:97]], offset_days=[[@36,151:161='offset_days',<459>,1:151], [@18,68:68='s',<459>,1:68]]}, table_dictionary={events={raw_ts=[[@38,164:164='a',<459>,1:164]], raw_days=[[@32,137:137='a',<459>,1:137]]}}, interface={created_at=[{name=raw_ts, table_ref=a}], offset_days=[{name=raw_days, table_ref=a}]}, table_alias={a=events}}, interface={adjustment=[{name=offset_days, table_ref=s}], event_time=[{name=created_at, table_ref=s}]}, table_alias={s=query0}}, interface={shifted=[{name=adjustment, table_ref=d}, {name=raw_ts, table_ref=d}]}, table_alias={d=query1}}}", parser.getSnippet().getSymbolTable().toString());
	}

	@Test
	public void dateaddAnchorsInJoinedSelectSeparateOutputAndFilterDependencies() {
		final String query = "select dateadd(year, 1, a.created_at) as qualified_shift, dateadd(year, 1, created_at) as unqualified_shift, dateadd(day, a.offset_days + b.extra_days * 2, DATE '2026-01-01') as formula_shift from (select e.created_at, e.offset_days, e.account_id from events e) a left join (select x.extra_days, x.id from accounts x) b on a.account_id = b.id";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=qualified_shift, datetime_function={function_name=dateadd, level={canonical=YEAR, text=year, form=KEYWORD}, increment={literal=1}, value={column={name=created_at, table_ref=a}}}}, 2={alias=unqualified_shift, datetime_function={function_name=dateadd, level={canonical=YEAR, text=year, form=KEYWORD}, increment={literal=1}, value={column={name=created_at, table_ref=null}}}}, 3={alias=formula_shift, datetime_function={function_name=dateadd, level={canonical=DAY, text=day, form=KEYWORD}, increment={calc={left={column={name=offset_days, table_ref=a}}, right={calc={left={column={name=extra_days, table_ref=b}}, right={literal=2}, operator=*}}, operator=+}}, value={source_type=DATE, literal=2026-01-01}}}}, from={join={1={table={alias=a, query={select={1={column={name=created_at, table_ref=e}}, 2={column={name=offset_days, table_ref=e}}, 3={column={name=account_id, table_ref=e}}}, from={table={alias=e, table=events}}}}}, 2={join=left, on={condition={left={column={name=account_id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, query={select={1={column={name=extra_days, table_ref=x}}, 2={column={name=id, table_ref=x}}}, from={table={alias=x, table=accounts}}}}}}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[unqualified_shift, formula_shift, qualified_shift]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{accounts={extra_days=[[@67,282:282='x',<459>,1:282]], id=[[@71,296:296='x',<459>,1:296]]}, events={account_id=[[@55,234:234='e',<459>,1:234]], created_at=[[@47,205:205='e',<459>,1:205]], offset_days=[[@51,219:219='e',<459>,1:219]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={created_at=[[@49,207:216='created_at',<459>,1:207], [@7,24:24='a',<459>,1:24], [@20,75:84='created_at',<459>,1:75]], offset_days=[[@53,221:231='offset_days',<459>,1:221], [@29,122:122='a',<459>,1:122]], account_id=[[@57,236:245='account_id',<459>,1:236], [@80,323:323='a',<459>,1:323]]}, query1={extra_days=[[@69,284:293='extra_days',<459>,1:284], [@33,138:138='b',<459>,1:138]], id=[[@73,298:299='id',<459>,1:298], [@84,338:338='b',<459>,1:338]]}, query2={qualified_shift=[[@12,41:55='qualified_shift',<459>,1:41]], unqualified_shift=[[@23,90:106='unqualified_shift',<459>,1:90]], formula_shift=[[@43,178:190='formula_shift',<459>,1:178]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query2={query_dictionary={unqualified_shift=[[@23,90:106='unqualified_shift',<459>,1:90]], formula_shift=[[@43,178:190='formula_shift',<459>,1:178]], qualified_shift=[[@12,41:55='qualified_shift',<459>,1:41]]}, def_query1={query_dictionary={extra_days=[[@69,284:293='extra_days',<459>,1:284], [@33,138:138='b',<459>,1:138]], id=[[@73,298:299='id',<459>,1:298], [@84,338:338='b',<459>,1:338]]}, table_dictionary={accounts={extra_days=[[@67,282:282='x',<459>,1:282]], id=[[@71,296:296='x',<459>,1:296]]}}, interface={extra_days=[{name=extra_days, table_ref=x}], id=[{name=id, table_ref=x}]}, table_alias={x=accounts}}, def_query0={query_dictionary={account_id=[[@57,236:245='account_id',<459>,1:236], [@80,323:323='a',<459>,1:323]], created_at=[[@49,207:216='created_at',<459>,1:207], [@7,24:24='a',<459>,1:24], [@20,75:84='created_at',<459>,1:75]], offset_days=[[@53,221:231='offset_days',<459>,1:221], [@29,122:122='a',<459>,1:122]]}, table_dictionary={events={account_id=[[@55,234:234='e',<459>,1:234]], created_at=[[@47,205:205='e',<459>,1:205]], offset_days=[[@51,219:219='e',<459>,1:219]]}}, interface={account_id=[{name=account_id, table_ref=e}], created_at=[{name=created_at, table_ref=e}], offset_days=[{name=offset_days, table_ref=e}]}, table_alias={e=events}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}], interface={unqualified_shift=[{name=created_at, table_ref=query0}], formula_shift=[{name=offset_days, table_ref=a}, {name=extra_days, table_ref=b}], qualified_shift=[{name=created_at, table_ref=a}]}, table_alias={a=query0, b=query1}}}", extractor.getSymbolTable().toString());

		final String expectedSelectDefQuery2Filters_2 = "[{name=account_id, table_ref=a}, {name=id, table_ref=b}]";
		assertSymbolTreePathEquals(extractor.getSymbolTable(), "def_query2.filters", expectedSelectDefQuery2Filters_2);
		final String expectedSelectDefQuery2Interface_3 = "{unqualified_shift=[{name=created_at, table_ref=query0}], formula_shift=[{name=offset_days, table_ref=a}, {name=extra_days, table_ref=b}], qualified_shift=[{name=created_at, table_ref=a}]}";
		assertSymbolTreePathEquals(extractor.getSymbolTable(), "def_query2.interface", expectedSelectDefQuery2Interface_3);
	}

	@Test
	public void dateaddAnchorsInsideBothInnerJoinedDerivedTables() {
		final String query = "select l.qshift, l.ushift, r.fshift from (select dateadd(year, 1, e.created_at) as qshift, dateadd(year, 1, created_at) as ushift, e.account_id from events e) l join (select dateadd(day, a.offset_days + b.extra_days * 2, DATE '2026-01-01') as fshift, b.id from logs a join adjustments b on a.account_id = b.id) r on l.account_id = r.id";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=qshift, table_ref=l}}, 2={column={name=ushift, table_ref=l}}, 3={column={name=fshift, table_ref=r}}}, from={join={1={table={alias=l, query={select={1={alias=qshift, datetime_function={function_name=dateadd, level={canonical=YEAR, text=year, form=KEYWORD}, increment={literal=1}, value={column={name=created_at, table_ref=e}}}}, 2={alias=ushift, datetime_function={function_name=dateadd, level={canonical=YEAR, text=year, form=KEYWORD}, increment={literal=1}, value={column={name=created_at, table_ref=null}}}}, 3={column={name=account_id, table_ref=e}}}, from={table={alias=e, table=events}}}}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=l}}, right={column={name=id, table_ref=r}}, operator==}}}, 3={table={alias=r, query={select={1={alias=fshift, datetime_function={function_name=dateadd, level={canonical=DAY, text=day, form=KEYWORD}, increment={calc={left={column={name=offset_days, table_ref=a}}, right={calc={left={column={name=extra_days, table_ref=b}}, right={literal=2}, operator=*}}, operator=+}}, value={source_type=DATE, literal=2026-01-01}}}, 2={column={name=id, table_ref=b}}}, from={join={1={table={alias=a, table=logs}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=adjustments}}}}}}}}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[ushift, qshift, fshift]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{adjustments={extra_days=[[@58,203:203='b',<459>,1:203]], id=[[@70,251:251='b',<459>,1:251], [@84,305:305='b',<459>,1:305]]}, logs={account_id=[[@80,290:290='a',<459>,1:290]], offset_days=[[@54,187:187='a',<459>,1:187]]}, events={account_id=[[@39,131:131='e',<459>,1:131]], created_at=[[@21,66:66='e',<459>,1:66], [@34,108:117='created_at',<459>,1:108]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={qshift=[[@26,83:88='qshift',<459>,1:83], [@1,7:7='l',<459>,1:7]], ushift=[[@37,123:128='ushift',<459>,1:123], [@5,17:17='l',<459>,1:17]], account_id=[[@41,133:142='account_id',<459>,1:133], [@90,316:316='l',<459>,1:316]]}, query1={id=[[@72,253:254='id',<459>,1:253], [@94,331:331='r',<459>,1:331]], fshift=[[@68,243:248='fshift',<459>,1:243], [@9,27:27='r',<459>,1:27]]}, query2={qshift=[[@3,9:14='qshift',<459>,1:9]], ushift=[[@7,19:24='ushift',<459>,1:19]], fshift=[[@11,29:34='fshift',<459>,1:29]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query2={query_dictionary={ushift=[[@7,19:24='ushift',<459>,1:19]], qshift=[[@3,9:14='qshift',<459>,1:9]], fshift=[[@11,29:34='fshift',<459>,1:29]]}, def_query1={query_dictionary={id=[[@72,253:254='id',<459>,1:253], [@94,331:331='r',<459>,1:331]], fshift=[[@68,243:248='fshift',<459>,1:243], [@9,27:27='r',<459>,1:27]]}, table_dictionary={adjustments={extra_days=[[@58,203:203='b',<459>,1:203]], id=[[@70,251:251='b',<459>,1:251], [@84,305:305='b',<459>,1:305]]}, logs={account_id=[[@80,290:290='a',<459>,1:290]], offset_days=[[@54,187:187='a',<459>,1:187]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}], interface={id=[{name=id, table_ref=b}], fshift=[{name=offset_days, table_ref=a}, {name=extra_days, table_ref=b}]}, table_alias={a=logs, b=adjustments}}, def_query0={query_dictionary={ushift=[[@37,123:128='ushift',<459>,1:123], [@5,17:17='l',<459>,1:17]], account_id=[[@41,133:142='account_id',<459>,1:133], [@90,316:316='l',<459>,1:316]], qshift=[[@26,83:88='qshift',<459>,1:83], [@1,7:7='l',<459>,1:7]]}, table_dictionary={events={account_id=[[@39,131:131='e',<459>,1:131]], created_at=[[@21,66:66='e',<459>,1:66], [@34,108:117='created_at',<459>,1:108]]}}, interface={ushift=[{name=created_at, table_ref=events}], account_id=[{name=account_id, table_ref=e}], qshift=[{name=created_at, table_ref=e}]}, table_alias={e=events}}, filters=[{name=account_id, table_ref=l}, {name=id, table_ref=r}], interface={ushift=[{name=ushift, table_ref=l}], qshift=[{name=qshift, table_ref=l}], fshift=[{name=fshift, table_ref=r}]}, table_alias={l=query0, r=query1}}}", extractor.getSymbolTable().toString());

		final String expectedLeftDefQuery2DefQuery1Filters_1 = "[{name=account_id, table_ref=a}, {name=id, table_ref=b}]";
		assertSymbolTreePathEquals(extractor.getSymbolTable(), "def_query2.def_query1.filters", expectedLeftDefQuery2DefQuery1Filters_1);
		final String expectedLeftDefQuery2DefQuery1Interface_2 = "{id=[{name=id, table_ref=b}], fshift=[{name=offset_days, table_ref=a}, {name=extra_days, table_ref=b}]}";
		assertSymbolTreePathEquals(extractor.getSymbolTable(), "def_query2.def_query1.interface", expectedLeftDefQuery2DefQuery1Interface_2);
		final String expectedLeftDefQuery2DefQuery0Interface_4 = "{ushift=[{name=created_at, table_ref=events}], account_id=[{name=account_id, table_ref=e}], qshift=[{name=created_at, table_ref=e}]}";
		assertSymbolTreePathEquals(extractor.getSymbolTable(), "def_query2.def_query0.interface", expectedLeftDefQuery2DefQuery0Interface_4);
		final String expectedLeftDefQuery2Filters_5 = "[{name=account_id, table_ref=l}, {name=id, table_ref=r}]";
		assertSymbolTreePathEquals(extractor.getSymbolTable(), "def_query2.filters", expectedLeftDefQuery2Filters_5);
	}

	@Test
	public void dateaddAnchorsInsideBothLeftJoinedDerivedTables() {
		final String query = "select l.fshift, r.qshift, r.ushift from (select dateadd(day, a.offset_days + b.extra_days * 2, DATE '2026-01-01') as fshift, b.id from logs a join adjustments b on a.account_id = b.id) l left join (select dateadd(year, 1, e.created_at) as qshift, dateadd(year, 1, created_at) as ushift, e.account_id from events e) r on l.id = r.account_id";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=fshift, table_ref=l}}, 2={column={name=qshift, table_ref=r}}, 3={column={name=ushift, table_ref=r}}}, from={join={1={table={alias=l, query={select={1={alias=fshift, datetime_function={function_name=dateadd, level={canonical=DAY, text=day, form=KEYWORD}, increment={calc={left={column={name=offset_days, table_ref=a}}, right={calc={left={column={name=extra_days, table_ref=b}}, right={literal=2}, operator=*}}, operator=+}}, value={source_type=DATE, literal=2026-01-01}}}, 2={column={name=id, table_ref=b}}}, from={join={1={table={alias=a, table=logs}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=adjustments}}}}}}}, 2={join=left, on={condition={left={column={name=id, table_ref=l}}, right={column={name=account_id, table_ref=r}}, operator==}}}, 3={table={alias=r, query={select={1={alias=qshift, datetime_function={function_name=dateadd, level={canonical=YEAR, text=year, form=KEYWORD}, increment={literal=1}, value={column={name=created_at, table_ref=e}}}}, 2={alias=ushift, datetime_function={function_name=dateadd, level={canonical=YEAR, text=year, form=KEYWORD}, increment={literal=1}, value={column={name=created_at, table_ref=null}}}}, 3={column={name=account_id, table_ref=e}}}, from={table={alias=e, table=events}}}}}}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[ushift, qshift, fshift]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{adjustments={extra_days=[[@23,78:78='b',<459>,1:78]], id=[[@35,126:126='b',<459>,1:126], [@49,180:180='b',<459>,1:180]]}, logs={account_id=[[@45,165:165='a',<459>,1:165]], offset_days=[[@19,62:62='a',<459>,1:62]]}, events={account_id=[[@82,288:288='e',<459>,1:288]], created_at=[[@64,223:223='e',<459>,1:223], [@77,265:274='created_at',<459>,1:265]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={id=[[@37,128:129='id',<459>,1:128], [@91,321:321='l',<459>,1:321]], fshift=[[@33,118:123='fshift',<459>,1:118], [@1,7:7='l',<459>,1:7]]}, query1={qshift=[[@69,240:245='qshift',<459>,1:240], [@5,17:17='r',<459>,1:17]], ushift=[[@80,280:285='ushift',<459>,1:280], [@9,27:27='r',<459>,1:27]], account_id=[[@84,290:299='account_id',<459>,1:290], [@95,328:328='r',<459>,1:328]]}, query2={qshift=[[@7,19:24='qshift',<459>,1:19]], ushift=[[@11,29:34='ushift',<459>,1:29]], fshift=[[@3,9:14='fshift',<459>,1:9]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query2={query_dictionary={ushift=[[@11,29:34='ushift',<459>,1:29]], qshift=[[@7,19:24='qshift',<459>,1:19]], fshift=[[@3,9:14='fshift',<459>,1:9]]}, def_query1={query_dictionary={ushift=[[@80,280:285='ushift',<459>,1:280], [@9,27:27='r',<459>,1:27]], account_id=[[@84,290:299='account_id',<459>,1:290], [@95,328:328='r',<459>,1:328]], qshift=[[@69,240:245='qshift',<459>,1:240], [@5,17:17='r',<459>,1:17]]}, table_dictionary={events={account_id=[[@82,288:288='e',<459>,1:288]], created_at=[[@64,223:223='e',<459>,1:223], [@77,265:274='created_at',<459>,1:265]]}}, interface={ushift=[{name=created_at, table_ref=events}], account_id=[{name=account_id, table_ref=e}], qshift=[{name=created_at, table_ref=e}]}, table_alias={e=events}}, def_query0={query_dictionary={id=[[@37,128:129='id',<459>,1:128], [@91,321:321='l',<459>,1:321]], fshift=[[@33,118:123='fshift',<459>,1:118], [@1,7:7='l',<459>,1:7]]}, table_dictionary={adjustments={extra_days=[[@23,78:78='b',<459>,1:78]], id=[[@35,126:126='b',<459>,1:126], [@49,180:180='b',<459>,1:180]]}, logs={account_id=[[@45,165:165='a',<459>,1:165]], offset_days=[[@19,62:62='a',<459>,1:62]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}], interface={id=[{name=id, table_ref=b}], fshift=[{name=offset_days, table_ref=a}, {name=extra_days, table_ref=b}]}, table_alias={a=logs, b=adjustments}}, filters=[{name=id, table_ref=l}, {name=account_id, table_ref=r}], interface={ushift=[{name=ushift, table_ref=r}], qshift=[{name=qshift, table_ref=r}], fshift=[{name=fshift, table_ref=l}]}, table_alias={l=query0, r=query1}}}", extractor.getSymbolTable().toString());

		final String expectedRightDefQuery2DefQuery1Interface_1 = "{ushift=[{name=created_at, table_ref=events}], account_id=[{name=account_id, table_ref=e}], qshift=[{name=created_at, table_ref=e}]}";
		assertSymbolTreePathEquals(extractor.getSymbolTable(), "def_query2.def_query1.interface", expectedRightDefQuery2DefQuery1Interface_1);
		final String expectedRightDefQuery2DefQuery0Filters_3 = "[{name=account_id, table_ref=a}, {name=id, table_ref=b}]";
		assertSymbolTreePathEquals(extractor.getSymbolTable(), "def_query2.def_query0.filters", expectedRightDefQuery2DefQuery0Filters_3);
		final String expectedRightDefQuery2DefQuery0Interface_4 = "{id=[{name=id, table_ref=b}], fshift=[{name=offset_days, table_ref=a}, {name=extra_days, table_ref=b}]}";
		assertSymbolTreePathEquals(extractor.getSymbolTable(), "def_query2.def_query0.interface", expectedRightDefQuery2DefQuery0Interface_4);
		final String expectedRightDefQuery2Filters_5 = "[{name=id, table_ref=l}, {name=account_id, table_ref=r}]";
		assertSymbolTreePathEquals(extractor.getSymbolTable(), "def_query2.filters", expectedRightDefQuery2Filters_5);
	}

	@Test
	public void dateaddAnchorsInJoinOnContributeFiltersNotOutputLineage() {
		final String query = "select a.account_id from (select e.created_at, e.offset_days, e.account_id from events e) a join (select x.extra_days, x.cutoff from accounts x) b on dateadd(year, 1, a.created_at) > b.cutoff and dateadd(year, 1, created_at) > b.cutoff and dateadd(day, a.offset_days + b.extra_days * 2, DATE '2026-01-01') > b.cutoff";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=account_id, table_ref=a}}}, from={join={1={table={alias=a, query={select={1={column={name=created_at, table_ref=e}}, 2={column={name=offset_days, table_ref=e}}, 3={column={name=account_id, table_ref=e}}}, from={table={alias=e, table=events}}}}}, 2={join=join, on={and={1={condition={left={datetime_function={function_name=dateadd, level={canonical=YEAR, text=year, form=KEYWORD}, increment={literal=1}, value={column={name=created_at, table_ref=a}}}}, right={column={name=cutoff, table_ref=b}}, operator=>}}, 2={condition={left={datetime_function={function_name=dateadd, level={canonical=YEAR, text=year, form=KEYWORD}, increment={literal=1}, value={column={name=created_at, table_ref=null}}}}, right={column={name=cutoff, table_ref=b}}, operator=>}}, 3={condition={left={datetime_function={function_name=dateadd, level={canonical=DAY, text=day, form=KEYWORD}, increment={calc={left={column={name=offset_days, table_ref=a}}, right={calc={left={column={name=extra_days, table_ref=b}}, right={literal=2}, operator=*}}, operator=+}}, value={source_type=DATE, literal=2026-01-01}}}, right={column={name=cutoff, table_ref=b}}, operator=>}}}}}, 3={table={alias=b, query={select={1={column={name=extra_days, table_ref=x}}, 2={column={name=cutoff, table_ref=x}}}, from={table={alias=x, table=accounts}}}}}}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[account_id]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{accounts={extra_days=[[@26,105:105='x',<459>,1:105]], cutoff=[[@30,119:119='x',<459>,1:119]]}, events={account_id=[[@15,62:62='e',<459>,1:62]], created_at=[[@7,33:33='e',<459>,1:33]], offset_days=[[@11,47:47='e',<459>,1:47]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={created_at=[[@9,35:44='created_at',<459>,1:35], [@45,167:167='a',<459>,1:167], [@60,213:222='created_at',<459>,1:213]], offset_days=[[@13,49:59='offset_days',<459>,1:49], [@71,253:253='a',<459>,1:253]], account_id=[[@17,64:73='account_id',<459>,1:64], [@1,7:7='a',<459>,1:7]]}, query1={extra_days=[[@28,107:116='extra_days',<459>,1:107], [@75,269:269='b',<459>,1:269]], cutoff=[[@32,121:126='cutoff',<459>,1:121], [@50,183:183='b',<459>,1:183], [@63,227:227='b',<459>,1:227], [@85,308:308='b',<459>,1:308]]}, query2={account_id=[[@3,9:18='account_id',<459>,1:9]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query2={query_dictionary={account_id=[[@3,9:18='account_id',<459>,1:9]]}, def_query1={query_dictionary={extra_days=[[@28,107:116='extra_days',<459>,1:107], [@75,269:269='b',<459>,1:269]], cutoff=[[@32,121:126='cutoff',<459>,1:121], [@50,183:183='b',<459>,1:183], [@63,227:227='b',<459>,1:227], [@85,308:308='b',<459>,1:308]]}, table_dictionary={accounts={extra_days=[[@26,105:105='x',<459>,1:105]], cutoff=[[@30,119:119='x',<459>,1:119]]}}, interface={extra_days=[{name=extra_days, table_ref=x}], cutoff=[{name=cutoff, table_ref=x}]}, table_alias={x=accounts}}, def_query0={query_dictionary={account_id=[[@17,64:73='account_id',<459>,1:64], [@1,7:7='a',<459>,1:7]], created_at=[[@9,35:44='created_at',<459>,1:35], [@45,167:167='a',<459>,1:167], [@60,213:222='created_at',<459>,1:213]], offset_days=[[@13,49:59='offset_days',<459>,1:49], [@71,253:253='a',<459>,1:253]]}, table_dictionary={events={account_id=[[@15,62:62='e',<459>,1:62]], created_at=[[@7,33:33='e',<459>,1:33]], offset_days=[[@11,47:47='e',<459>,1:47]]}}, interface={account_id=[{name=account_id, table_ref=e}], created_at=[{name=created_at, table_ref=e}], offset_days=[{name=offset_days, table_ref=e}]}, table_alias={e=events}}, filters=[{name=created_at, table_ref=a}, {name=cutoff, table_ref=b}, {name=created_at, table_ref=query0}, {name=offset_days, table_ref=a}, {name=extra_days, table_ref=b}], interface={account_id=[{name=account_id, table_ref=a}]}, table_alias={a=query0, b=query1}}}", extractor.getSymbolTable().toString());

		final String expectedOnDefQuery2Filters_2 = "[{name=created_at, table_ref=a}, {name=cutoff, table_ref=b}, {name=created_at, table_ref=query0}, {name=offset_days, table_ref=a}, {name=extra_days, table_ref=b}]";
		assertSymbolTreePathEquals(extractor.getSymbolTable(), "def_query2.filters", expectedOnDefQuery2Filters_2);
		final String expectedOnDefQuery2Interface_3 = "{account_id=[{name=account_id, table_ref=a}]}";
		assertSymbolTreePathEquals(extractor.getSymbolTable(), "def_query2.interface", expectedOnDefQuery2Interface_3);
	}

	@Test
	public void dateaddInsideScalarSelectRetainsDependentQueryLineage() {
		final String query = "select (select dateadd(year, 1, max(i.created_at)) as shifted_value from events i) as shifted";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={lookup={from={table={alias=i, table=events}}, select={1={alias=shifted_value, datetime_function={function_name=dateadd, level={canonical=YEAR, text=year, form=KEYWORD}, increment={literal=1}, value={function={function_name=max, qualifier=null, parameters={column={name=created_at, table_ref=i}}}}}}}}, alias=shifted}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[shifted]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{events={created_at=[[@11,36:36='i',<459>,1:36]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={shifted_value=[[@17,54:66='shifted_value',<459>,1:54]]}, query2={shifted=[[@23,86:92='shifted',<459>,1:86]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query2={query_dictionary={shifted=[[@23,86:92='shifted',<459>,1:86]]}, dependent_queries={predicand1={query=query0, type=interface}}, def_query0={query_dictionary={shifted_value=[[@17,54:66='shifted_value',<459>,1:54]]}, table_dictionary={events={created_at=[[@11,36:36='i',<459>,1:36]]}}, interface={shifted_value=[{name=created_at, table_ref=i}]}, table_alias={i=events}}, interface={shifted=[{name=created_at, table_ref=i}]}}}", extractor.getSymbolTable().toString());

		final String expectedInnerDefQuery2DependentQueries_0 = "{predicand1={query=query0, type=interface}}";
		assertSymbolTreePathEquals(extractor.getSymbolTable(), "def_query2.dependent_queries", expectedInnerDefQuery2DependentQueries_0);
		final String expectedInnerDefQuery2DefQuery0TableDictionary_1 = "{events={created_at=[[@11,36:36='i',<459>,1:36]]}}";
		assertSymbolTreePathEquals(extractor.getSymbolTable(), "def_query2.def_query0.table_dictionary", expectedInnerDefQuery2DefQuery0TableDictionary_1);
		final String expectedInnerDefQuery2DefQuery0Interface_2 = "{shifted_value=[{name=created_at, table_ref=i}]}";
		assertSymbolTreePathEquals(extractor.getSymbolTable(), "def_query2.def_query0.interface", expectedInnerDefQuery2DefQuery0Interface_2);
		final String expectedInnerDefQuery2DefQuery0TableAlias_3 = "{i=events}";
		assertSymbolTreePathEquals(extractor.getSymbolTable(), "def_query2.def_query0.table_alias", expectedInnerDefQuery2DefQuery0TableAlias_3);
		final String expectedInnerDefQuery2Interface_4 = "{shifted=[{name=created_at, table_ref=i}]}";
		assertSymbolTreePathEquals(extractor.getSymbolTable(), "def_query2.interface", expectedInnerDefQuery2Interface_4);
	}

	@Test
	public void dateaddIncrementFromScalarQueryRetainsScopedInputs() {
		final String query = "select dateadd(day, (select max(i.offset_days) from adjustments i), a.created_at) as shifted from events a";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=shifted, datetime_function={function_name=dateadd, level={canonical=DAY, text=day, form=KEYWORD}, increment={lookup={from={table={alias=i, table=adjustments}}, select={1={function={function_name=max, qualifier=null, parameters={column={name=offset_days, table_ref=i}}}}}}}, value={column={name=created_at, table_ref=a}}}}}, from={table={alias=a, table=events}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[shifted]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{adjustments={offset_days=[[@9,32:32='i',<459>,1:32]]}, events={created_at=[[@18,68:68='a',<459>,1:68]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={unnamed_0=[[@12,45:45=')',<288>,1:45]]}, query2={shifted=[[@23,85:91='shifted',<459>,1:85]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query2={query_dictionary={shifted=[[@23,85:91='shifted',<459>,1:85]]}, table_dictionary={events={created_at=[[@18,68:68='a',<459>,1:68]]}}, dependent_queries={predicand1={query=query0, type=interface}}, def_query0={query_dictionary={unnamed_0=[[@12,45:45=')',<288>,1:45]]}, table_dictionary={adjustments={offset_days=[[@9,32:32='i',<459>,1:32]]}}, interface={unnamed_0=[{name=offset_days, table_ref=i}]}, table_alias={i=adjustments}}, interface={shifted=[{name=created_at, table_ref=a}]}, table_alias={a=events}}}", extractor.getSymbolTable().toString());

		final String expectedIncrementDefQuery2TableDictionary_0 = "{events={created_at=[[@18,68:68='a',<459>,1:68]]}}";
		assertSymbolTreePathEquals(extractor.getSymbolTable(), "def_query2.table_dictionary", expectedIncrementDefQuery2TableDictionary_0);
		final String expectedIncrementDefQuery2DependentQueries_1 = "{predicand1={query=query0, type=interface}}";
		assertSymbolTreePathEquals(extractor.getSymbolTable(), "def_query2.dependent_queries", expectedIncrementDefQuery2DependentQueries_1);
		final String expectedIncrementDefQuery2DefQuery0TableDictionary_2 = "{adjustments={offset_days=[[@9,32:32='i',<459>,1:32]]}}";
		assertSymbolTreePathEquals(extractor.getSymbolTable(), "def_query2.def_query0.table_dictionary", expectedIncrementDefQuery2DefQuery0TableDictionary_2);
		final String expectedIncrementDefQuery2DefQuery0Interface_3 = "{unnamed_0=[{name=offset_days, table_ref=i}]}";
		assertSymbolTreePathEquals(extractor.getSymbolTable(), "def_query2.def_query0.interface", expectedIncrementDefQuery2DefQuery0Interface_3);
		final String expectedIncrementDefQuery2DefQuery0TableAlias_4 = "{i=adjustments}";
		assertSymbolTreePathEquals(extractor.getSymbolTable(), "def_query2.def_query0.table_alias", expectedIncrementDefQuery2DefQuery0TableAlias_4);
		final String expectedIncrementDefQuery2Interface_5 = "{shifted=[{name=created_at, table_ref=a}]}";
		assertSymbolTreePathEquals(extractor.getSymbolTable(), "def_query2.interface", expectedIncrementDefQuery2Interface_5);
	}

	@Test
	public void dateaddValueFromScalarQueryRetainsScopedInputs() {
		final String query = "select dateadd(day, a.offset_days, (select max(i.created_at) from history i)) as shifted from events a";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=shifted, datetime_function={function_name=dateadd, level={canonical=DAY, text=day, form=KEYWORD}, increment={column={name=offset_days, table_ref=a}}, value={lookup={from={table={alias=i, table=history}}, select={1={function={function_name=max, qualifier=null, parameters={column={name=created_at, table_ref=i}}}}}}}}}}, from={table={alias=a, table=events}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[shifted]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{history={created_at=[[@13,47:47='i',<459>,1:47]]}, events={offset_days=[[@5,20:20='a',<459>,1:20]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={unnamed_0=[[@16,59:59=')',<288>,1:59]]}, query2={shifted=[[@23,81:87='shifted',<459>,1:81]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query2={query_dictionary={shifted=[[@23,81:87='shifted',<459>,1:81]]}, table_dictionary={events={offset_days=[[@5,20:20='a',<459>,1:20]]}}, dependent_queries={predicand1={query=query0, type=interface}}, def_query0={query_dictionary={unnamed_0=[[@16,59:59=')',<288>,1:59]]}, table_dictionary={history={created_at=[[@13,47:47='i',<459>,1:47]]}}, interface={unnamed_0=[{name=created_at, table_ref=i}]}, table_alias={i=history}}, interface={shifted=[{name=offset_days, table_ref=a}]}, table_alias={a=events}}}", extractor.getSymbolTable().toString());

		final String expectedValueDefQuery2TableDictionary_0 = "{events={offset_days=[[@5,20:20='a',<459>,1:20]]}}";
		assertSymbolTreePathEquals(extractor.getSymbolTable(), "def_query2.table_dictionary", expectedValueDefQuery2TableDictionary_0);
		final String expectedValueDefQuery2DependentQueries_1 = "{predicand1={query=query0, type=interface}}";
		assertSymbolTreePathEquals(extractor.getSymbolTable(), "def_query2.dependent_queries", expectedValueDefQuery2DependentQueries_1);
		final String expectedValueDefQuery2DefQuery0TableDictionary_2 = "{history={created_at=[[@13,47:47='i',<459>,1:47]]}}";
		assertSymbolTreePathEquals(extractor.getSymbolTable(), "def_query2.def_query0.table_dictionary", expectedValueDefQuery2DefQuery0TableDictionary_2);
		final String expectedValueDefQuery2DefQuery0Interface_3 = "{unnamed_0=[{name=created_at, table_ref=i}]}";
		assertSymbolTreePathEquals(extractor.getSymbolTable(), "def_query2.def_query0.interface", expectedValueDefQuery2DefQuery0Interface_3);
		final String expectedValueDefQuery2DefQuery0TableAlias_4 = "{i=history}";
		assertSymbolTreePathEquals(extractor.getSymbolTable(), "def_query2.def_query0.table_alias", expectedValueDefQuery2DefQuery0TableAlias_4);
		final String expectedValueDefQuery2Interface_5 = "{shifted=[{name=offset_days, table_ref=a}]}";
		assertSymbolTreePathEquals(extractor.getSymbolTable(), "def_query2.interface", expectedValueDefQuery2Interface_5);
	}

	@Test
	public void dateaddIncrementFromCorrelatedScalarQueryRetainsScopedInputs() {
		final String query = "select dateadd(day, (select max(i.offset_days + a.increment_bias) from adjustments i where i.event_id = a.id), a.created_at) as shifted from events a";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=shifted, datetime_function={function_name=dateadd, level={canonical=DAY, text=day, form=KEYWORD}, increment={lookup={from={table={alias=i, table=adjustments}}, where={condition={left={column={name=event_id, table_ref=i}}, right={column={name=id, table_ref=a}}, operator==}}, select={1={function={function_name=max, qualifier=null, parameters={calc={left={column={name=offset_days, table_ref=i}}, right={column={name=increment_bias, table_ref=a}}, operator=+}}}}}}}, value={column={name=created_at, table_ref=a}}}}}, from={table={alias=a, table=events}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[shifted]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{adjustments={event_id=[[@21,91:91='i',<459>,1:91]], offset_days=[[@9,32:32='i',<459>,1:32]]}, events={increment_bias=[[@13,48:48='a',<459>,1:48]], created_at=[[@30,111:111='a',<459>,1:111]], id=[[@25,104:104='a',<459>,1:104]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={unnamed_0=[[@16,64:64=')',<288>,1:64]]}, query2={shifted=[[@35,128:134='shifted',<459>,1:128]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query2={query_dictionary={shifted=[[@35,128:134='shifted',<459>,1:128]]}, table_dictionary={events={increment_bias=[[@13,48:48='a',<459>,1:48]], created_at=[[@30,111:111='a',<459>,1:111]], id=[[@25,104:104='a',<459>,1:104]]}}, dependent_queries={predicand1={query=query0, type=interface}}, def_query0={query_dictionary={unnamed_0=[[@16,64:64=')',<288>,1:64]]}, table_dictionary={adjustments={event_id=[[@21,91:91='i',<459>,1:91]], offset_days=[[@9,32:32='i',<459>,1:32]]}}, filters=[{name=event_id, table_ref=i}, {name=id, table_ref=a}], interface={unnamed_0=[{name=offset_days, table_ref=i}, {name=increment_bias, table_ref=a}]}, table_alias={i=adjustments}}, interface={shifted=[{name=created_at, table_ref=a}]}, table_alias={a=events}}}", extractor.getSymbolTable().toString());

		final String expectedCorrelatedIncrementDefQuery2TableDictionary_0 = "{events={increment_bias=[[@13,48:48='a',<459>,1:48]], created_at=[[@30,111:111='a',<459>,1:111]], id=[[@25,104:104='a',<459>,1:104]]}}";
		assertSymbolTreePathEquals(extractor.getSymbolTable(), "def_query2.table_dictionary", expectedCorrelatedIncrementDefQuery2TableDictionary_0);
		final String expectedCorrelatedIncrementDefQuery2DependentQueries_1 = "{predicand1={query=query0, type=interface}}";
		assertSymbolTreePathEquals(extractor.getSymbolTable(), "def_query2.dependent_queries", expectedCorrelatedIncrementDefQuery2DependentQueries_1);
		final String expectedCorrelatedIncrementDefQuery2DefQuery0TableDictionary_2 = "{adjustments={event_id=[[@21,91:91='i',<459>,1:91]], offset_days=[[@9,32:32='i',<459>,1:32]]}}";
		assertSymbolTreePathEquals(extractor.getSymbolTable(), "def_query2.def_query0.table_dictionary", expectedCorrelatedIncrementDefQuery2DefQuery0TableDictionary_2);
		final String expectedCorrelatedIncrementDefQuery2DefQuery0Filters_3 = "[{name=event_id, table_ref=i}, {name=id, table_ref=a}]";
		assertSymbolTreePathEquals(extractor.getSymbolTable(), "def_query2.def_query0.filters", expectedCorrelatedIncrementDefQuery2DefQuery0Filters_3);
		final String expectedCorrelatedIncrementDefQuery2DefQuery0Interface_4 = "{unnamed_0=[{name=offset_days, table_ref=i}, {name=increment_bias, table_ref=a}]}";
		assertSymbolTreePathEquals(extractor.getSymbolTable(), "def_query2.def_query0.interface", expectedCorrelatedIncrementDefQuery2DefQuery0Interface_4);
		final String expectedCorrelatedIncrementDefQuery2Interface_6 = "{shifted=[{name=created_at, table_ref=a}]}";
		assertSymbolTreePathEquals(extractor.getSymbolTable(), "def_query2.interface", expectedCorrelatedIncrementDefQuery2Interface_6);
	}

	@Test
	public void dateaddValueFromCorrelatedScalarQueryRetainsScopedInputs() {
		final String query = "select dateadd(day, a.offset_days, (select max(h.created_at + a.value_bias) from history h where h.event_id = a.id)) as shifted from events a";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=shifted, datetime_function={function_name=dateadd, level={canonical=DAY, text=day, form=KEYWORD}, increment={column={name=offset_days, table_ref=a}}, value={lookup={from={table={alias=h, table=history}}, where={condition={left={column={name=event_id, table_ref=h}}, right={column={name=id, table_ref=a}}, operator==}}, select={1={function={function_name=max, qualifier=null, parameters={calc={left={column={name=created_at, table_ref=h}}, right={column={name=value_bias, table_ref=a}}, operator=+}}}}}}}}}}, from={table={alias=a, table=events}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[shifted]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{history={event_id=[[@25,97:97='h',<459>,1:97]], created_at=[[@13,47:47='h',<459>,1:47]]}, events={value_bias=[[@17,62:62='a',<459>,1:62]], offset_days=[[@5,20:20='a',<459>,1:20]], id=[[@29,110:110='a',<459>,1:110]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={unnamed_0=[[@20,74:74=')',<288>,1:74]]}, query2={shifted=[[@35,120:126='shifted',<459>,1:120]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query2={query_dictionary={shifted=[[@35,120:126='shifted',<459>,1:120]]}, table_dictionary={events={value_bias=[[@17,62:62='a',<459>,1:62]], offset_days=[[@5,20:20='a',<459>,1:20]], id=[[@29,110:110='a',<459>,1:110]]}}, dependent_queries={predicand1={query=query0, type=interface}}, def_query0={query_dictionary={unnamed_0=[[@20,74:74=')',<288>,1:74]]}, table_dictionary={history={event_id=[[@25,97:97='h',<459>,1:97]], created_at=[[@13,47:47='h',<459>,1:47]]}}, filters=[{name=event_id, table_ref=h}, {name=id, table_ref=a}], interface={unnamed_0=[{name=created_at, table_ref=h}, {name=value_bias, table_ref=a}]}, table_alias={h=history}}, interface={shifted=[{name=offset_days, table_ref=a}]}, table_alias={a=events}}}", extractor.getSymbolTable().toString());

		final String expectedCorrelatedValueDefQuery2TableDictionary_0 = "{events={value_bias=[[@17,62:62='a',<459>,1:62]], offset_days=[[@5,20:20='a',<459>,1:20]], id=[[@29,110:110='a',<459>,1:110]]}}";
		assertSymbolTreePathEquals(extractor.getSymbolTable(), "def_query2.table_dictionary", expectedCorrelatedValueDefQuery2TableDictionary_0);
		final String expectedCorrelatedValueDefQuery2DependentQueries_1 = "{predicand1={query=query0, type=interface}}";
		assertSymbolTreePathEquals(extractor.getSymbolTable(), "def_query2.dependent_queries", expectedCorrelatedValueDefQuery2DependentQueries_1);
		final String expectedCorrelatedValueDefQuery2DefQuery0TableDictionary_2 = "{history={event_id=[[@25,97:97='h',<459>,1:97]], created_at=[[@13,47:47='h',<459>,1:47]]}}";
		assertSymbolTreePathEquals(extractor.getSymbolTable(), "def_query2.def_query0.table_dictionary", expectedCorrelatedValueDefQuery2DefQuery0TableDictionary_2);
		final String expectedCorrelatedValueDefQuery2DefQuery0Filters_3 = "[{name=event_id, table_ref=h}, {name=id, table_ref=a}]";
		assertSymbolTreePathEquals(extractor.getSymbolTable(), "def_query2.def_query0.filters", expectedCorrelatedValueDefQuery2DefQuery0Filters_3);
		final String expectedCorrelatedValueDefQuery2DefQuery0Interface_4 = "{unnamed_0=[{name=created_at, table_ref=h}, {name=value_bias, table_ref=a}]}";
		assertSymbolTreePathEquals(extractor.getSymbolTable(), "def_query2.def_query0.interface", expectedCorrelatedValueDefQuery2DefQuery0Interface_4);
		final String expectedCorrelatedValueDefQuery2Interface_6 = "{shifted=[{name=offset_days, table_ref=a}]}";
		assertSymbolTreePathEquals(extractor.getSymbolTable(), "def_query2.interface", expectedCorrelatedValueDefQuery2Interface_6);
	}

	@Test
	public void dateaddBothArgumentsFromCorrelatedScalarQueriesRemainSeparate() {
		final String query = "select dateadd(day, (select max(i.offset_days + a.increment_bias) from adjustments i where i.event_id = a.id), (select max(h.created_at + a.value_bias) from history h where h.event_id = a.id)) as shifted from events a";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=shifted, datetime_function={function_name=dateadd, level={canonical=DAY, text=day, form=KEYWORD}, increment={lookup={from={table={alias=i, table=adjustments}}, where={condition={left={column={name=event_id, table_ref=i}}, right={column={name=id, table_ref=a}}, operator==}}, select={1={function={function_name=max, qualifier=null, parameters={calc={left={column={name=offset_days, table_ref=i}}, right={column={name=increment_bias, table_ref=a}}, operator=+}}}}}}}, value={lookup={from={table={alias=h, table=history}}, where={condition={left={column={name=event_id, table_ref=h}}, right={column={name=id, table_ref=a}}, operator==}}, select={1={function={function_name=max, qualifier=null, parameters={calc={left={column={name=created_at, table_ref=h}}, right={column={name=value_bias, table_ref=a}}, operator=+}}}}}}}}}}, from={table={alias=a, table=events}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[shifted]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{adjustments={event_id=[[@21,91:91='i',<459>,1:91]], offset_days=[[@9,32:32='i',<459>,1:32]]}, history={event_id=[[@46,173:173='h',<459>,1:173]], created_at=[[@34,123:123='h',<459>,1:123]]}, events={increment_bias=[[@13,48:48='a',<459>,1:48]], value_bias=[[@38,138:138='a',<459>,1:138]], id=[[@25,104:104='a',<459>,1:104], [@50,186:186='a',<459>,1:186]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query4={shifted=[[@56,196:202='shifted',<459>,1:196]]}, query0={unnamed_0=[[@16,64:64=')',<288>,1:64]]}, query2={unnamed_1=[[@41,150:150=')',<288>,1:150]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query4={query_dictionary={shifted=[[@56,196:202='shifted',<459>,1:196]]}, table_dictionary={events={increment_bias=[[@13,48:48='a',<459>,1:48]], value_bias=[[@38,138:138='a',<459>,1:138]], id=[[@25,104:104='a',<459>,1:104], [@50,186:186='a',<459>,1:186]]}}, dependent_queries={predicand3={query=query2, type=interface}, predicand1={query=query0, type=interface}}, def_query0={query_dictionary={unnamed_0=[[@16,64:64=')',<288>,1:64]]}, table_dictionary={adjustments={event_id=[[@21,91:91='i',<459>,1:91]], offset_days=[[@9,32:32='i',<459>,1:32]]}}, filters=[{name=event_id, table_ref=i}, {name=id, table_ref=a}], interface={unnamed_0=[{name=offset_days, table_ref=i}, {name=increment_bias, table_ref=a}]}, table_alias={i=adjustments}}, interface={shifted=[]}, table_alias={a=events}, def_query2={query_dictionary={unnamed_1=[[@41,150:150=')',<288>,1:150]]}, table_dictionary={history={event_id=[[@46,173:173='h',<459>,1:173]], created_at=[[@34,123:123='h',<459>,1:123]]}}, filters=[{name=event_id, table_ref=h}, {name=id, table_ref=a}], interface={unnamed_1=[{name=created_at, table_ref=h}, {name=value_bias, table_ref=a}]}, table_alias={h=history}}}}", extractor.getSymbolTable().toString());

		final String expectedCorrelatedBothDefQuery4TableDictionary_0 = "{events={increment_bias=[[@13,48:48='a',<459>,1:48]], value_bias=[[@38,138:138='a',<459>,1:138]], id=[[@25,104:104='a',<459>,1:104], [@50,186:186='a',<459>,1:186]]}}";
		assertSymbolTreePathEquals(extractor.getSymbolTable(), "def_query4.table_dictionary", expectedCorrelatedBothDefQuery4TableDictionary_0);
		final String expectedCorrelatedBothDefQuery4DependentQueries_1 = "{predicand3={query=query2, type=interface}, predicand1={query=query0, type=interface}}";
		assertSymbolTreePathEquals(extractor.getSymbolTable(), "def_query4.dependent_queries", expectedCorrelatedBothDefQuery4DependentQueries_1);
		final String expectedCorrelatedBothDefQuery4DefQuery0TableDictionary_2 = "{adjustments={event_id=[[@21,91:91='i',<459>,1:91]], offset_days=[[@9,32:32='i',<459>,1:32]]}}";
		assertSymbolTreePathEquals(extractor.getSymbolTable(), "def_query4.def_query0.table_dictionary", expectedCorrelatedBothDefQuery4DefQuery0TableDictionary_2);
		final String expectedCorrelatedBothDefQuery4DefQuery0Filters_3 = "[{name=event_id, table_ref=i}, {name=id, table_ref=a}]";
		assertSymbolTreePathEquals(extractor.getSymbolTable(), "def_query4.def_query0.filters", expectedCorrelatedBothDefQuery4DefQuery0Filters_3);
		final String expectedCorrelatedBothDefQuery4DefQuery0Interface_4 = "{unnamed_0=[{name=offset_days, table_ref=i}, {name=increment_bias, table_ref=a}]}";
		assertSymbolTreePathEquals(extractor.getSymbolTable(), "def_query4.def_query0.interface", expectedCorrelatedBothDefQuery4DefQuery0Interface_4);
		final String expectedCorrelatedBothDefQuery4Interface_6 = "{shifted=[]}";
		assertSymbolTreePathEquals(extractor.getSymbolTable(), "def_query4.interface", expectedCorrelatedBothDefQuery4Interface_6);
		final String expectedCorrelatedBothDefQuery4DefQuery2TableDictionary_7 = "{history={event_id=[[@46,173:173='h',<459>,1:173]], created_at=[[@34,123:123='h',<459>,1:123]]}}";
		assertSymbolTreePathEquals(extractor.getSymbolTable(), "def_query4.def_query2.table_dictionary", expectedCorrelatedBothDefQuery4DefQuery2TableDictionary_7);
		final String expectedCorrelatedBothDefQuery4DefQuery2Filters_8 = "[{name=event_id, table_ref=h}, {name=id, table_ref=a}]";
		assertSymbolTreePathEquals(extractor.getSymbolTable(), "def_query4.def_query2.filters", expectedCorrelatedBothDefQuery4DefQuery2Filters_8);
		final String expectedCorrelatedBothDefQuery4DefQuery2Interface_9 = "{unnamed_1=[{name=created_at, table_ref=h}, {name=value_bias, table_ref=a}]}";
		assertSymbolTreePathEquals(extractor.getSymbolTable(), "def_query4.def_query2.interface", expectedCorrelatedBothDefQuery4DefQuery2Interface_9);
	}

	@Test
	public void dateaddCteProducerRetainsQualifiedValueLineage() {
		final String query = "with shifted_cte as (select dateadd(year, 1, a.created_at) as shifted from events a) select s.shifted as result from shifted_cte s";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={with={1={cte={select={1={alias=shifted, datetime_function={function_name=dateadd, level={canonical=YEAR, text=year, form=KEYWORD}, increment={literal=1}, value={column={name=created_at, table_ref=a}}}}}, from={table={alias=a, table=events}}}, alias=shifted_cte}}, query={select={1={column={name=shifted, table_ref=s}, alias=result}}, from={table={alias=s, table=shifted_cte}}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[result]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{events={created_at=[[@11,45:45='a',<459>,1:45]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={shifted=[[@16,62:68='shifted',<459>,1:62], [@22,92:92='s',<459>,1:92]]}, query1={result=[[@26,105:110='result',<459>,1:105]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query1={context_list={shifted_cte=query0, s=query0}, query_dictionary={result=[[@26,105:110='result',<459>,1:105]]}, def_query0={query_dictionary={shifted=[[@16,62:68='shifted',<459>,1:62], [@22,92:92='s',<459>,1:92]]}, table_dictionary={events={created_at=[[@11,45:45='a',<459>,1:45]]}}, interface={shifted=[{name=created_at, table_ref=a}]}, table_alias={a=events}}, interface={result=[{name=shifted, table_ref=s}]}, table_alias={shifted_cte=query0, s=query0}}}", extractor.getSymbolTable().toString());

		final String expectedCteQualifiedDefQuery1ContextList_0 = "{shifted_cte=query0, s=query0}";
		assertSymbolTreePathEquals(extractor.getSymbolTable(), "def_query1.context_list", expectedCteQualifiedDefQuery1ContextList_0);
		final String expectedCteQualifiedDefQuery1DefQuery0Interface_3 = "{shifted=[{name=created_at, table_ref=a}]}";
		assertSymbolTreePathEquals(extractor.getSymbolTable(), "def_query1.def_query0.interface", expectedCteQualifiedDefQuery1DefQuery0Interface_3);
		final String expectedCteQualifiedDefQuery1Interface_4 = "{result=[{name=shifted, table_ref=s}]}";
		assertSymbolTreePathEquals(extractor.getSymbolTable(), "def_query1.interface", expectedCteQualifiedDefQuery1Interface_4);
	}

	@Test
	public void dateaddCteProducerResolvesUnqualifiedValueLineage() {
		final String query = "with shifted_cte as (select dateadd(year, 1, created_at) as shifted from events a) select s.shifted as result from shifted_cte s";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={with={1={cte={select={1={alias=shifted, datetime_function={function_name=dateadd, level={canonical=YEAR, text=year, form=KEYWORD}, increment={literal=1}, value={column={name=created_at, table_ref=null}}}}}, from={table={alias=a, table=events}}}, alias=shifted_cte}}, query={select={1={column={name=shifted, table_ref=s}, alias=result}}, from={table={alias=s, table=shifted_cte}}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[result]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{events={created_at=[[@11,45:54='created_at',<459>,1:45]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={shifted=[[@14,60:66='shifted',<459>,1:60], [@20,90:90='s',<459>,1:90]]}, query1={result=[[@24,103:108='result',<459>,1:103]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query1={context_list={shifted_cte=query0, s=query0}, query_dictionary={result=[[@24,103:108='result',<459>,1:103]]}, def_query0={query_dictionary={shifted=[[@14,60:66='shifted',<459>,1:60], [@20,90:90='s',<459>,1:90]]}, table_dictionary={events={created_at=[[@11,45:54='created_at',<459>,1:45]]}}, interface={shifted=[{name=created_at, table_ref=events}]}, table_alias={a=events}}, interface={result=[{name=shifted, table_ref=s}]}, table_alias={shifted_cte=query0, s=query0}}}", extractor.getSymbolTable().toString());

		final String expectedCteUnqualifiedDefQuery1ContextList_0 = "{shifted_cte=query0, s=query0}";
		assertSymbolTreePathEquals(extractor.getSymbolTable(), "def_query1.context_list", expectedCteUnqualifiedDefQuery1ContextList_0);
		final String expectedCteUnqualifiedDefQuery1DefQuery0Interface_3 = "{shifted=[{name=created_at, table_ref=events}]}";
		assertSymbolTreePathEquals(extractor.getSymbolTable(), "def_query1.def_query0.interface", expectedCteUnqualifiedDefQuery1DefQuery0Interface_3);
		final String expectedCteUnqualifiedDefQuery1Interface_4 = "{result=[{name=shifted, table_ref=s}]}";
		assertSymbolTreePathEquals(extractor.getSymbolTable(), "def_query1.interface", expectedCteUnqualifiedDefQuery1Interface_4);
	}

	@Test
	public void dateaddCteProducerRetainsJoinedFormulaAndFilters() {
		final String query = "with shifted_cte as (select dateadd(day, a.offset_days + b.extra_days * 2, DATE '2026-01-01') as shifted from events a join accounts b on a.account_id = b.id) select s.shifted as result from shifted_cte s";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={with={1={cte={select={1={alias=shifted, datetime_function={function_name=dateadd, level={canonical=DAY, text=day, form=KEYWORD}, increment={calc={left={column={name=offset_days, table_ref=a}}, right={calc={left={column={name=extra_days, table_ref=b}}, right={literal=2}, operator=*}}, operator=+}}, value={source_type=DATE, literal=2026-01-01}}}}, from={join={1={table={alias=a, table=events}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}}, alias=shifted_cte}}, query={select={1={column={name=shifted, table_ref=s}, alias=result}}, from={table={alias=s, table=shifted_cte}}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[result]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{accounts={extra_days=[[@13,57:57='b',<459>,1:57]], id=[[@35,153:153='b',<459>,1:153]]}, events={account_id=[[@31,138:138='a',<459>,1:138]], offset_days=[[@9,41:41='a',<459>,1:41]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={shifted=[[@23,97:103='shifted',<459>,1:97], [@40,166:166='s',<459>,1:166]]}, query1={result=[[@44,179:184='result',<459>,1:179]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query1={context_list={shifted_cte=query0, s=query0}, query_dictionary={result=[[@44,179:184='result',<459>,1:179]]}, def_query0={query_dictionary={shifted=[[@23,97:103='shifted',<459>,1:97], [@40,166:166='s',<459>,1:166]]}, table_dictionary={accounts={extra_days=[[@13,57:57='b',<459>,1:57]], id=[[@35,153:153='b',<459>,1:153]]}, events={account_id=[[@31,138:138='a',<459>,1:138]], offset_days=[[@9,41:41='a',<459>,1:41]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}], interface={shifted=[{name=offset_days, table_ref=a}, {name=extra_days, table_ref=b}]}, table_alias={a=events, b=accounts}}, interface={result=[{name=shifted, table_ref=s}]}, table_alias={shifted_cte=query0, s=query0}}}", extractor.getSymbolTable().toString());

		final String expectedCteFormulaDefQuery1DefQuery0Filters_2 = "[{name=account_id, table_ref=a}, {name=id, table_ref=b}]";
		assertSymbolTreePathEquals(extractor.getSymbolTable(), "def_query1.def_query0.filters", expectedCteFormulaDefQuery1DefQuery0Filters_2);
		final String expectedCteFormulaDefQuery1DefQuery0Interface_3 = "{shifted=[{name=offset_days, table_ref=a}, {name=extra_days, table_ref=b}]}";
		assertSymbolTreePathEquals(extractor.getSymbolTable(), "def_query1.def_query0.interface", expectedCteFormulaDefQuery1DefQuery0Interface_3);
		final String expectedCteFormulaDefQuery1Interface_4 = "{result=[{name=shifted, table_ref=s}]}";
		assertSymbolTreePathEquals(extractor.getSymbolTable(), "def_query1.interface", expectedCteFormulaDefQuery1Interface_4);
	}

	@Test
	public void dateaddConsumesCteExportsThroughRenamedCteChain() {
		final String query = "with base_cte as (select a.offset_days, a.created_at from events a), shifted_cte as (select dateadd(day, b.offset_days, b.created_at) as shifted from base_cte b), renamed_cte as (select s.shifted as adjusted from shifted_cte s) select r.adjusted as result from renamed_cte r";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={with={1={cte={select={1={column={name=offset_days, table_ref=a}}, 2={column={name=created_at, table_ref=a}}}, from={table={alias=a, table=events}}}, alias=base_cte}, 2={cte={select={1={alias=shifted, datetime_function={function_name=dateadd, level={canonical=DAY, text=day, form=KEYWORD}, increment={column={name=offset_days, table_ref=b}}, value={column={name=created_at, table_ref=b}}}}}, from={table={alias=b, table=base_cte}}}, alias=shifted_cte}, 3={cte={select={1={column={name=shifted, table_ref=s}, alias=adjusted}}, from={table={alias=s, table=shifted_cte}}}, alias=renamed_cte}}, query={select={1={column={name=adjusted, table_ref=r}, alias=result}}, from={table={alias=r, table=renamed_cte}}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[result]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{events={created_at=[[@9,40:40='a',<459>,1:40]], offset_days=[[@5,25:25='a',<459>,1:25]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={created_at=[[@11,42:51='created_at',<459>,1:42], [@29,120:120='b',<459>,1:120]], offset_days=[[@7,27:37='offset_days',<459>,1:27], [@25,105:105='b',<459>,1:105]]}, query1={shifted=[[@34,137:143='shifted',<459>,1:137], [@44,186:186='s',<459>,1:186]]}, query2={adjusted=[[@48,199:206='adjusted',<459>,1:199], [@54,235:235='r',<459>,1:235]]}, query3={result=[[@58,249:254='result',<459>,1:249]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query3={context_list={base_cte=query0, shifted_cte=query1, renamed_cte=query2, r=query2}, query_dictionary={result=[[@58,249:254='result',<459>,1:249]]}, def_query1={context_list={base_cte=query0, b=query0}, query_dictionary={shifted=[[@34,137:143='shifted',<459>,1:137], [@44,186:186='s',<459>,1:186]]}, interface={shifted=[{name=offset_days, table_ref=b}, {name=created_at, table_ref=b}]}, table_alias={b=query0, base_cte=query0}}, def_query0={query_dictionary={created_at=[[@11,42:51='created_at',<459>,1:42], [@29,120:120='b',<459>,1:120]], offset_days=[[@7,27:37='offset_days',<459>,1:27], [@25,105:105='b',<459>,1:105]]}, table_dictionary={events={created_at=[[@9,40:40='a',<459>,1:40]], offset_days=[[@5,25:25='a',<459>,1:25]]}}, interface={created_at=[{name=created_at, table_ref=a}], offset_days=[{name=offset_days, table_ref=a}]}, table_alias={a=events}}, interface={result=[{name=adjusted, table_ref=r}]}, table_alias={renamed_cte=query2, r=query2, shifted_cte=query1, base_cte=query0}, def_query2={context_list={base_cte=query0, shifted_cte=query1, s=query1}, query_dictionary={adjusted=[[@48,199:206='adjusted',<459>,1:199], [@54,235:235='r',<459>,1:235]]}, interface={adjusted=[{name=shifted, table_ref=s}]}, table_alias={shifted_cte=query1, s=query1, base_cte=query0}}}}", extractor.getSymbolTable().toString());

		final String expectedCteChainDefQuery3ContextList_0 = "{base_cte=query0, shifted_cte=query1, renamed_cte=query2, r=query2}";
		assertSymbolTreePathEquals(extractor.getSymbolTable(), "def_query3.context_list", expectedCteChainDefQuery3ContextList_0);
		final String expectedCteChainDefQuery3DefQuery1ContextList_1 = "{base_cte=query0, b=query0}";
		assertSymbolTreePathEquals(extractor.getSymbolTable(), "def_query3.def_query1.context_list", expectedCteChainDefQuery3DefQuery1ContextList_1);
		final String expectedCteChainDefQuery3DefQuery1QueryDictionary_2 = "{shifted=[[@34,137:143='shifted',<459>,1:137], [@44,186:186='s',<459>,1:186]]}";
		assertSymbolTreePathEquals(extractor.getSymbolTable(), "def_query3.def_query1.query_dictionary", expectedCteChainDefQuery3DefQuery1QueryDictionary_2);
		final String expectedCteChainDefQuery3DefQuery1Interface_3 = "{shifted=[{name=offset_days, table_ref=b}, {name=created_at, table_ref=b}]}";
		assertSymbolTreePathEquals(extractor.getSymbolTable(), "def_query3.def_query1.interface", expectedCteChainDefQuery3DefQuery1Interface_3);
		final String expectedCteChainDefQuery3DefQuery1TableAlias_4 = "{b=query0, base_cte=query0}";
		assertSymbolTreePathEquals(extractor.getSymbolTable(), "def_query3.def_query1.table_alias", expectedCteChainDefQuery3DefQuery1TableAlias_4);
		final String expectedCteChainDefQuery3DefQuery0QueryDictionary_5 = "{created_at=[[@11,42:51='created_at',<459>,1:42], [@29,120:120='b',<459>,1:120]], offset_days=[[@7,27:37='offset_days',<459>,1:27], [@25,105:105='b',<459>,1:105]]}";
		assertSymbolTreePathEquals(extractor.getSymbolTable(), "def_query3.def_query0.query_dictionary", expectedCteChainDefQuery3DefQuery0QueryDictionary_5);
		final String expectedCteChainDefQuery3DefQuery0Interface_7 = "{created_at=[{name=created_at, table_ref=a}], offset_days=[{name=offset_days, table_ref=a}]}";
		assertSymbolTreePathEquals(extractor.getSymbolTable(), "def_query3.def_query0.interface", expectedCteChainDefQuery3DefQuery0Interface_7);
		final String expectedCteChainDefQuery3Interface_9 = "{result=[{name=adjusted, table_ref=r}]}";
		assertSymbolTreePathEquals(extractor.getSymbolTable(), "def_query3.interface", expectedCteChainDefQuery3Interface_9);
		final String expectedCteChainDefQuery3TableAlias_10 = "{renamed_cte=query2, r=query2, shifted_cte=query1, base_cte=query0}";
		assertSymbolTreePathEquals(extractor.getSymbolTable(), "def_query3.table_alias", expectedCteChainDefQuery3TableAlias_10);
		final String expectedCteChainDefQuery3DefQuery2ContextList_11 = "{base_cte=query0, shifted_cte=query1, s=query1}";
		assertSymbolTreePathEquals(extractor.getSymbolTable(), "def_query3.def_query2.context_list", expectedCteChainDefQuery3DefQuery2ContextList_11);
		final String expectedCteChainDefQuery3DefQuery2QueryDictionary_12 = "{adjusted=[[@48,199:206='adjusted',<459>,1:199], [@54,235:235='r',<459>,1:235]]}";
		assertSymbolTreePathEquals(extractor.getSymbolTable(), "def_query3.def_query2.query_dictionary", expectedCteChainDefQuery3DefQuery2QueryDictionary_12);
		final String expectedCteChainDefQuery3DefQuery2Interface_13 = "{adjusted=[{name=shifted, table_ref=s}]}";
		assertSymbolTreePathEquals(extractor.getSymbolTable(), "def_query3.def_query2.interface", expectedCteChainDefQuery3DefQuery2Interface_13);
		final String expectedCteChainDefQuery3DefQuery2TableAlias_14 = "{shifted_cte=query1, s=query1, base_cte=query0}";
		assertSymbolTreePathEquals(extractor.getSymbolTable(), "def_query3.def_query2.table_alias", expectedCteChainDefQuery3DefQuery2TableAlias_14);
	}

	@Test
	public void dateaddReportsInvalidLevelsAtTheirSourceWithoutCollectingColumns() {
		String[] invalidParts = {"bogus", "'bogus'", "''", "'engine''part'", "epoch", "yaer"};
		for (String part : invalidParts) {
			String query = "select dateadd(" + part + ", 1, a.ts) as shifted from events a";
			SqlParserAccess parser = new SqlParserAccess(false, false, false);
			parser.executeTheParse(query, "SQL");
			Assert.assertEquals(query, 0, parser.getParser().getNumberOfSyntaxErrors());
			Assert.assertEquals(query + ": " + parser.getAllDiagnostics(), 2, parser.getAllDiagnostics().size());
			assertSnowflakeGrammarWarningAt(parser, "snowflake_datetime_add_expression",
					invocationOffset(query, "dateadd"));
			assertFatalDiagnosticAtPositionWithFullMessage(parser.getSnippet(), "INVALID_DATETIME_PART",
					"Invalid date/time part " + part + " for DATEADD at (l:1 c:15).", part, 1, 15);
			Assert.assertEquals(part, findFatalDiagnosticByCodeAndFragment(
					parser.getSnippet(), "INVALID_DATETIME_PART", part).tokenText());
			Assert.assertFalse(parser.getSnippet().getSymbolTable().toString().contains(part));
		}
	}

	@Test
	public void dateaddCorrelatedScalarSubqueryRespectsNearestVisibleScopeWithShadowing() {
		final String query = "select dateadd(day, (select max(o.delta_days) from offsets o where o.event_id = outer_e.id), outer_e.created_at) as correlated_shift, dateadd(day, (select max(e.delta_days) from offsets e where e.event_id = outer_e.id), outer_e.created_at) as shadowed_shift from events outer_e";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=correlated_shift, datetime_function={function_name=dateadd, level={canonical=DAY, text=day, form=KEYWORD}, increment={lookup={from={table={alias=o, table=offsets}}, where={condition={left={column={name=event_id, table_ref=o}}, right={column={name=id, table_ref=outer_e}}, operator==}}, select={1={function={function_name=max, qualifier=null, parameters={column={name=delta_days, table_ref=o}}}}}}}, value={column={name=created_at, table_ref=outer_e}}}}, 2={alias=shadowed_shift, datetime_function={function_name=dateadd, level={canonical=DAY, text=day, form=KEYWORD}, increment={lookup={from={table={alias=e, table=offsets}}, where={condition={left={column={name=event_id, table_ref=e}}, right={column={name=id, table_ref=outer_e}}, operator==}}, select={1={function={function_name=max, qualifier=null, parameters={column={name=delta_days, table_ref=e}}}}}}}, value={column={name=created_at, table_ref=outer_e}}}}}, from={table={alias=outer_e, table=events}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[correlated_shift, shadowed_shift]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{offsets={event_id=[[@17,67:67='o',<459>,1:67], [@49,194:194='e',<459>,1:194]], delta_days=[[@9,32:32='o',<459>,1:32], [@41,159:159='e',<459>,1:159]]}, events={created_at=[[@26,93:99='outer_e',<459>,1:93], [@58,220:226='outer_e',<459>,1:220]], id=[[@21,80:86='outer_e',<459>,1:80], [@53,207:213='outer_e',<459>,1:207]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query4={correlated_shift=[[@31,116:131='correlated_shift',<459>,1:116]], shadowed_shift=[[@63,243:256='shadowed_shift',<459>,1:243]]}, query0={unnamed_0=[[@12,44:44=')',<288>,1:44]]}, query2={unnamed_1=[[@44,171:171=')',<288>,1:171]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query4={query_dictionary={correlated_shift=[[@31,116:131='correlated_shift',<459>,1:116]], shadowed_shift=[[@63,243:256='shadowed_shift',<459>,1:243]]}, table_dictionary={events={created_at=[[@26,93:99='outer_e',<459>,1:93], [@58,220:226='outer_e',<459>,1:220]], id=[[@21,80:86='outer_e',<459>,1:80], [@53,207:213='outer_e',<459>,1:207]]}}, dependent_queries={predicand3={query=query2, type=interface}, predicand1={query=query0, type=interface}}, def_query0={query_dictionary={unnamed_0=[[@12,44:44=')',<288>,1:44]]}, table_dictionary={offsets={event_id=[[@17,67:67='o',<459>,1:67], [@49,194:194='e',<459>,1:194]], delta_days=[[@9,32:32='o',<459>,1:32], [@41,159:159='e',<459>,1:159]]}}, filters=[{name=event_id, table_ref=o}, {name=id, table_ref=outer_e}], interface={unnamed_0=[{name=delta_days, table_ref=o}]}, table_alias={o=offsets}}, interface={correlated_shift=[{name=created_at, table_ref=outer_e}], shadowed_shift=[{name=created_at, table_ref=outer_e}]}, table_alias={outer_e=events}, def_query2={query_dictionary={unnamed_1=[[@44,171:171=')',<288>,1:171]]}, table_dictionary={offsets={event_id=[[@49,194:194='e',<459>,1:194]], delta_days=[[@41,159:159='e',<459>,1:159]]}}, filters=[{name=event_id, table_ref=e}, {name=id, table_ref=outer_e}], interface={unnamed_1=[{name=delta_days, table_ref=e}]}, table_alias={e=offsets}}}}", extractor.getSymbolTable().toString());

		final String expectedCorrelatedDefQuery4DependentQueries_0 = "{predicand3={query=query2, type=interface}, predicand1={query=query0, type=interface}}";
		assertSymbolTreePathEquals(extractor.getSymbolTable(), "def_query4.dependent_queries", expectedCorrelatedDefQuery4DependentQueries_0);
		final String expectedCorrelatedDefQuery4DefQuery0Filters_1 = "[{name=event_id, table_ref=o}, {name=id, table_ref=outer_e}]";
		assertSymbolTreePathEquals(extractor.getSymbolTable(), "def_query4.def_query0.filters", expectedCorrelatedDefQuery4DefQuery0Filters_1);
		final String expectedCorrelatedDefQuery4DefQuery2Filters_2 = "[{name=event_id, table_ref=e}, {name=id, table_ref=outer_e}]";
		assertSymbolTreePathEquals(extractor.getSymbolTable(), "def_query4.def_query2.filters", expectedCorrelatedDefQuery4DefQuery2Filters_2);
	}

	@Test
	public void dateaddInWherePredicatesAndExistsSubqueryCollectsFilterAndDependentQueryLineage() {
		final String query = "select e.id from events e where dateadd(day, e.offset_days, e.created_at) > DATE '2026-01-01' and exists (select 1 from adjustments a where a.event_id = e.id and a.updated_at > dateadd(day, a.delta_days, e.created_at))";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=id, table_ref=e}}}, from={table={alias=e, table=events}}, where={and={1={condition={left={datetime_function={function_name=dateadd, level={canonical=DAY, text=day, form=KEYWORD}, increment={column={name=offset_days, table_ref=e}}, value={column={name=created_at, table_ref=e}}}}, right={source_type=DATE, literal=2026-01-01}, operator=>}}, 2={exists={select={1={literal=1}}, from={table={alias=a, table=adjustments}}, where={and={1={condition={left={column={name=event_id, table_ref=a}}, right={column={name=id, table_ref=e}}, operator==}}, 2={condition={left={column={name=updated_at, table_ref=a}}, right={datetime_function={function_name=dateadd, level={canonical=DAY, text=day, form=KEYWORD}, increment={column={name=delta_days, table_ref=a}}, value={column={name=created_at, table_ref=e}}}}, operator=>}}}}, operator=exists}}}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[id]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{adjustments={event_id=[[@32,140:140='a',<459>,1:140]], updated_at=[[@40,162:162='a',<459>,1:162]], delta_days=[[@48,190:190='a',<459>,1:190]]}, events={created_at=[[@16,60:60='e',<459>,1:60], [@52,204:204='e',<459>,1:204]], offset_days=[[@12,45:45='e',<459>,1:45]], id=[[@1,7:7='e',<459>,1:7], [@36,153:153='e',<459>,1:153]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={unnamed_0=[[@27,113:113='1',<300>,1:113]]}, query2={id=[[@3,9:10='id',<459>,1:9]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query2={query_dictionary={id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={created_at=[[@16,60:60='e',<459>,1:60], [@52,204:204='e',<459>,1:204]], offset_days=[[@12,45:45='e',<459>,1:45]], id=[[@1,7:7='e',<459>,1:7], [@36,153:153='e',<459>,1:153]]}}, dependent_queries={exists1={query=query0, type=filters}}, def_query0={query_dictionary={unnamed_0=[[@27,113:113='1',<300>,1:113]]}, table_dictionary={adjustments={event_id=[[@32,140:140='a',<459>,1:140]], updated_at=[[@40,162:162='a',<459>,1:162]], delta_days=[[@48,190:190='a',<459>,1:190]]}}, filters=[{name=event_id, table_ref=a}, {name=id, table_ref=e}, {name=updated_at, table_ref=a}, {name=delta_days, table_ref=a}, {name=created_at, table_ref=e}], interface={unnamed_0=[]}, table_alias={a=adjustments}}, filters=[{name=offset_days, table_ref=e}, {name=created_at, table_ref=e}], interface={id=[{name=id, table_ref=e}]}, table_alias={e=events}}}", extractor.getSymbolTable().toString());

		final String expectedWhereDefQuery2DependentQueries_0 = "{exists1={query=query0, type=filters}}";
		assertSymbolTreePathEquals(extractor.getSymbolTable(), "def_query2.dependent_queries", expectedWhereDefQuery2DependentQueries_0);
		final String expectedWhereDefQuery2Filters_1 = "[{name=offset_days, table_ref=e}, {name=created_at, table_ref=e}]";
		assertSymbolTreePathEquals(extractor.getSymbolTable(), "def_query2.filters", expectedWhereDefQuery2Filters_1);
		final String expectedWhereDefQuery2DefQuery0Filters_2 = "[{name=event_id, table_ref=a}, {name=id, table_ref=e}, {name=updated_at, table_ref=a}, {name=delta_days, table_ref=a}, {name=created_at, table_ref=e}]";
		assertSymbolTreePathEquals(extractor.getSymbolTable(), "def_query2.def_query0.filters", expectedWhereDefQuery2DefQuery0Filters_2);
	}

	@Test
	public void dateaddInGroupByAndHavingSeparatesGroupedAndFilterDependencies() {
		final String query = "select dateadd(day, e.offset_days, e.created_at) as shifted, count(*) as total from events e group by dateadd(day, e.offset_days, e.created_at) having max(dateadd(day, e.offset_days, e.created_at)) > DATE '2026-01-01'";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=shifted, datetime_function={function_name=dateadd, level={canonical=DAY, text=day, form=KEYWORD}, increment={column={name=offset_days, table_ref=e}}, value={column={name=created_at, table_ref=e}}}}, 2={function={function_name=COUNT, qualifier=null, parameters=*}, alias=total}}, having={condition={left={function={function_name=max, qualifier=null, parameters={datetime_function={function_name=dateadd, level={canonical=DAY, text=day, form=KEYWORD}, increment={column={name=offset_days, table_ref=e}}, value={column={name=created_at, table_ref=e}}}}}}, right={source_type=DATE, literal=2026-01-01}, operator=>}}, from={table={alias=e, table=events}}, groupby={1={datetime_function={function_name=dateadd, level={canonical=DAY, text=day, form=KEYWORD}, increment={column={name=offset_days, table_ref=e}}, value={column={name=created_at, table_ref=e}}}}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[total, shifted]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{events={created_at=[[@9,35:35='e',<459>,1:35], [@35,130:130='e',<459>,1:130], [@50,183:183='e',<459>,1:183]], offset_days=[[@5,20:20='e',<459>,1:20], [@31,115:115='e',<459>,1:115], [@46,168:168='e',<459>,1:168]], *=[[@18,67:67='*',<291>,1:67]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={shifted=[[@14,52:58='shifted',<459>,1:52]], total=[[@21,73:77='total',<459>,1:73]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={total=[[@21,73:77='total',<459>,1:73]], shifted=[[@14,52:58='shifted',<459>,1:52]]}, table_dictionary={events={created_at=[[@9,35:35='e',<459>,1:35], [@35,130:130='e',<459>,1:130], [@50,183:183='e',<459>,1:183]], *=[[@18,67:67='*',<291>,1:67]], offset_days=[[@5,20:20='e',<459>,1:20], [@31,115:115='e',<459>,1:115], [@46,168:168='e',<459>,1:168]]}}, grouped_by=[{name=offset_days, table_ref=e}, {name=created_at, table_ref=e}], filters=[{name=offset_days, table_ref=e}, {name=created_at, table_ref=e}], interface={total=[], shifted=[{name=offset_days, table_ref=e}, {name=created_at, table_ref=e}]}, table_alias={e=events}}}", extractor.getSymbolTable().toString());

		final String expectedGroupByDefQuery0GroupedBy_0 = "[{name=offset_days, table_ref=e}, {name=created_at, table_ref=e}]";
		assertSymbolTreePathEquals(extractor.getSymbolTable(), "def_query0.grouped_by", expectedGroupByDefQuery0GroupedBy_0);
		final String expectedGroupByDefQuery0Filters_1 = "[{name=offset_days, table_ref=e}, {name=created_at, table_ref=e}]";
		assertSymbolTreePathEquals(extractor.getSymbolTable(), "def_query0.filters", expectedGroupByDefQuery0Filters_1);
	}

	@Test
	public void dateaddInOrderByExpressionAndAliasTracksOrderedByDependencies() {
		final String query = "select dateadd(day, e.offset_days, e.created_at) as shifted, e.id from events e order by dateadd(day, e.offset_days, e.created_at), shifted";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=shifted, datetime_function={function_name=dateadd, level={canonical=DAY, text=day, form=KEYWORD}, increment={column={name=offset_days, table_ref=e}}, value={column={name=created_at, table_ref=e}}}}, 2={column={name=id, table_ref=e}}}, orderby={1={null_order=null, predicand={datetime_function={function_name=dateadd, level={canonical=DAY, text=day, form=KEYWORD}, increment={column={name=offset_days, table_ref=e}}, value={column={name=created_at, table_ref=e}}}}, sort_order=ASC}, 2={null_order=null, predicand={column={name=shifted, table_ref=null}}, sort_order=ASC}}, from={table={alias=e, table=events}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[id, shifted]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{events={created_at=[[@9,35:35='e',<459>,1:35], [@32,117:117='e',<459>,1:117]], offset_days=[[@5,20:20='e',<459>,1:20], [@28,102:102='e',<459>,1:102]], id=[[@16,61:61='e',<459>,1:61]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={shifted=[[@14,52:58='shifted',<459>,1:52], [@37,132:138='shifted',<459>,1:132]], id=[[@18,63:64='id',<459>,1:63]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={id=[[@18,63:64='id',<459>,1:63]], shifted=[[@14,52:58='shifted',<459>,1:52], [@37,132:138='shifted',<459>,1:132]]}, table_dictionary={events={created_at=[[@9,35:35='e',<459>,1:35], [@32,117:117='e',<459>,1:117]], offset_days=[[@5,20:20='e',<459>,1:20], [@28,102:102='e',<459>,1:102]], id=[[@16,61:61='e',<459>,1:61]]}}, ordered_by=[{name=offset_days, table_ref=e}, {name=created_at, table_ref=e}, {name=shifted, table_ref=query0}], interface={id=[{name=id, table_ref=e}], shifted=[{name=offset_days, table_ref=e}, {name=created_at, table_ref=e}]}, table_alias={e=events}}}", extractor.getSymbolTable().toString());

		final String expectedOrderByDefQuery0OrderedBy_0 = "[{name=offset_days, table_ref=e}, {name=created_at, table_ref=e}, {name=shifted, table_ref=query0}]";
		assertSymbolTreePathEquals(extractor.getSymbolTable(), "def_query0.ordered_by", expectedOrderByDefQuery0OrderedBy_0);
	}

	@Test
	public void dateaddInWindowPartitionOrderAndQualifyCollectsWindowDependencies() {
		final String query = "select e.id from events e qualify row_number() over (partition by dateadd(day, e.offset_days, e.created_at) order by dateadd(hour, e.offset_hours, e.created_at)) = 1";
		SqlParserAccess parserAccess = new SqlParserAccess(false, false, false);
		parserAccess.executeTheParse(query, "SQL");
		Assert.assertEquals(query, 0, parserAccess.getParser().getNumberOfSyntaxErrors());
		Assert.assertEquals(parserAccess.getAllDiagnostics().toString(), 1, parserAccess.getAllDiagnostics().size());
		assertSnowflakeGrammarWarningAt(parserAccess, "snowflake_datetime_add_expression",
				invocationOffset(query, "dateadd"));

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=id, table_ref=e}}}, from={table={alias=e, table=events}}, qualify={condition={left={window_function={over={partition_by={1={datetime_function={function_name=dateadd, level={canonical=DAY, text=day, form=KEYWORD}, increment={column={name=offset_days, table_ref=e}}, value={column={name=created_at, table_ref=e}}}}}, orderby={1={null_order=null, predicand={datetime_function={function_name=dateadd, level={canonical=HOUR, text=hour, form=KEYWORD}, increment={column={name=offset_hours, table_ref=e}}, value={column={name=created_at, table_ref=e}}}}, sort_order=ASC}}}, function={function_name=row_number, parameters=null}}}, right={literal=1}, operator==}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[id]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{events={created_at=[[@23,94:94='e',<459>,1:94], [@37,147:147='e',<459>,1:147]], offset_hours=[[@33,131:131='e',<459>,1:131]], offset_days=[[@19,79:79='e',<459>,1:79]], id=[[@1,7:7='e',<459>,1:7]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={id=[[@3,9:10='id',<459>,1:9]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={window_ordered_by=[{name=offset_hours, table_ref=e}, {name=created_at, table_ref=e}], query_dictionary={id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={created_at=[[@23,94:94='e',<459>,1:94], [@37,147:147='e',<459>,1:147]], offset_hours=[[@33,131:131='e',<459>,1:131]], offset_days=[[@19,79:79='e',<459>,1:79]], id=[[@1,7:7='e',<459>,1:7]]}}, window_partition_by=[{name=offset_days, table_ref=e}, {name=created_at, table_ref=e}], filters=[{name=offset_days, table_ref=e}, {name=created_at, table_ref=e}, {name=offset_hours, table_ref=e}], interface={id=[{name=id, table_ref=e}]}, table_alias={e=events}}}", extractor.getSymbolTable().toString());
		Assert.assertEquals("Array Output Collectors are wrong", "{SQL={queryInterface=[id]}}", extractor.getArrayOutputCollectorsMap().toString());

		final String expectedWindowDefQuery0WindowPartitionBy_0 = "[{name=offset_days, table_ref=e}, {name=created_at, table_ref=e}]";
		assertSymbolTreePathEquals(extractor.getSymbolTable(), "def_query0.window_partition_by", expectedWindowDefQuery0WindowPartitionBy_0);
		final String expectedWindowDefQuery0WindowOrderedBy_1 = "[{name=offset_hours, table_ref=e}, {name=created_at, table_ref=e}]";
		assertSymbolTreePathEquals(extractor.getSymbolTable(), "def_query0.window_ordered_by", expectedWindowDefQuery0WindowOrderedBy_1);
	}

	@Test
	public void dateaddInInsertSelectSourceCollectsTargetAndSourceDependencies() {
		final String query = "insert into archive_events (shifted_at, event_id) select dateadd(day, e.offset_days, e.created_at) as shifted_at, e.id from events e";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={insert={preamble=insert_into, from={from={table={alias=e, table=events}}, select={1={alias=shifted_at, datetime_function={function_name=dateadd, level={canonical=DAY, text=day, form=KEYWORD}, increment={column={name=offset_days, table_ref=e}}, value={column={name=created_at, table_ref=e}}}}, 2={column={name=id, table_ref=e}}}}, target_table={table={alias=null, table=archive_events}}, columns={1={column={name=shifted_at, table_ref=null}}, 2={column={name=event_id, table_ref=null}}}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[shifted_at, event_id]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{archive_events={shifted_at=[[@4,28:37='shifted_at',<459>,1:28]], event_id=[[@6,40:47='event_id',<459>,1:40]]}, events={created_at=[[@17,85:85='e',<459>,1:85]], offset_days=[[@13,70:70='e',<459>,1:70]], id=[[@24,114:114='e',<459>,1:114]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={shifted_at=[[@22,102:111='shifted_at',<459>,1:102]], id=[[@26,116:117='id',<459>,1:116]]}, insert1={shifted_at=[[@4,28:37='shifted_at',<459>,1:28]], event_id=[[@6,40:47='event_id',<459>,1:40]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_insert1={query_dictionary={shifted_at=[[@4,28:37='shifted_at',<459>,1:28]], event_id=[[@6,40:47='event_id',<459>,1:40]]}, table_dictionary={archive_events={shifted_at=[[@4,28:37='shifted_at',<459>,1:28]], event_id=[[@6,40:47='event_id',<459>,1:40]]}}, def_query0={query_dictionary={shifted_at=[[@22,102:111='shifted_at',<459>,1:102]], id=[[@26,116:117='id',<459>,1:116]]}, table_dictionary={events={created_at=[[@17,85:85='e',<459>,1:85]], offset_days=[[@13,70:70='e',<459>,1:70]], id=[[@24,114:114='e',<459>,1:114]]}}, interface={shifted_at=[{name=offset_days, table_ref=e}, {name=created_at, table_ref=e}], id=[{name=id, table_ref=e}]}, table_alias={e=events}}, interface={shifted_at=[{name=shifted_at, table_ref=query0}], event_id=[{name=id, table_ref=query0}]}}}", extractor.getSymbolTable().toString());

		final String expectedInsertDefInsert1Interface_0 = "{shifted_at=[{name=shifted_at, table_ref=query0}], event_id=[{name=id, table_ref=query0}]}";
		assertSymbolTreePathEquals(extractor.getSymbolTable(), "def_insert1.interface", expectedInsertDefInsert1Interface_0);
		final String expectedInsertDefInsert1DefQuery0Interface_1 = "{shifted_at=[{name=offset_days, table_ref=e}, {name=created_at, table_ref=e}], id=[{name=id, table_ref=e}]}";
		assertSymbolTreePathEquals(extractor.getSymbolTable(), "def_insert1.def_query0.interface", expectedInsertDefInsert1DefQuery0Interface_1);
	}

	@Test
	public void dateaddInUpdateSetAndWhereSeparatesAssignmentAndFilterDependencies() {
		final String query = "update events e set shifted_at = dateadd(day, e.offset_days, e.created_at) where dateadd(day, e.offset_days, e.created_at) > DATE '2026-01-01'";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={update={table={alias=e, table=events}, where={condition={left={datetime_function={function_name=dateadd, level={canonical=DAY, text=day, form=KEYWORD}, increment={column={name=offset_days, table_ref=e}}, value={column={name=created_at, table_ref=e}}}}, right={source_type=DATE, literal=2026-01-01}, operator=>}}, assignments={1={set={column={name=shifted_at, table_ref=null}}, to={datetime_function={function_name=dateadd, level={canonical=DAY, text=day, form=KEYWORD}, increment={column={name=offset_days, table_ref=e}}, value={column={name=created_at, table_ref=e}}}}}}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[shifted_at]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{events={created_at=[[@14,61:61='e',<459>,1:61], [@27,109:109='e',<459>,1:109]], offset_days=[[@10,46:46='e',<459>,1:46], [@23,94:94='e',<459>,1:94]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{update0={shifted_at=[[@4,20:29='shifted_at',<459>,1:20]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_update0={assignments={shifted_at=[{name=offset_days, table_ref=e}, {name=created_at, table_ref=e}]}, table_dictionary={events={created_at=[[@14,61:61='e',<459>,1:61], [@27,109:109='e',<459>,1:109]], offset_days=[[@10,46:46='e',<459>,1:46], [@23,94:94='e',<459>,1:94]]}}, update_dictionary={shifted_at=[[@4,20:29='shifted_at',<459>,1:20]]}, target_table={events={shifted_at=[[@4,20:29='shifted_at',<459>,1:20]]}}, filters=[{name=offset_days, table_ref=e}, {name=created_at, table_ref=e}], table_alias={e=events}, lhs_unresolved_columns={shifted_at={column={name=shifted_at, table_ref=null}, locations=[[@4,20:29='shifted_at',<459>,1:20]]}}}}", extractor.getSymbolTable().toString());

		final String expectedUpdateDefUpdate0Assignments_0 = "{shifted_at=[{name=offset_days, table_ref=e}, {name=created_at, table_ref=e}]}";
		assertSymbolTreePathEquals(extractor.getSymbolTable(), "def_update0.assignments", expectedUpdateDefUpdate0Assignments_0);
		final String expectedUpdateDefUpdate0Filters_1 = "[{name=offset_days, table_ref=e}, {name=created_at, table_ref=e}]";
		assertSymbolTreePathEquals(extractor.getSymbolTable(), "def_update0.filters", expectedUpdateDefUpdate0Filters_1);
	}

	@Test
	public void dateaddInDeleteWhereAndExistsSubqueryCollectsDeleteFilterDependencies() {
		final String query = "delete from events e where dateadd(day, e.offset_days, e.created_at) > DATE '2026-01-01' and exists (select 1 from adjustments a where dateadd(day, a.delta_days, a.updated_at) > DATE '2026-01-01')";
		SqlParserAccess parserAccess = new SqlParserAccess(false, false, false);
		parserAccess.executeTheParse(query, "SQL");
		Assert.assertEquals(query, 0, parserAccess.getParser().getNumberOfSyntaxErrors());
		Assert.assertEquals(parserAccess.getAllDiagnostics().toString(), 1, parserAccess.getAllDiagnostics().size());
		assertSnowflakeGrammarWarningAt(parserAccess, "snowflake_datetime_add_expression",
				invocationOffset(query, "dateadd"));

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={delete={table={alias=e, table=events}, where={and={1={condition={left={datetime_function={function_name=dateadd, level={canonical=DAY, text=day, form=KEYWORD}, increment={column={name=offset_days, table_ref=e}}, value={column={name=created_at, table_ref=e}}}}, right={source_type=DATE, literal=2026-01-01}, operator=>}}, 2={exists={select={1={literal=1}}, from={table={alias=a, table=adjustments}}, where={condition={left={datetime_function={function_name=dateadd, level={canonical=DAY, text=day, form=KEYWORD}, increment={column={name=delta_days, table_ref=a}}, value={column={name=updated_at, table_ref=a}}}}, right={source_type=DATE, literal=2026-01-01}, operator=>}}, operator=exists}}}}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{adjustments={updated_at=[[@37,162:162='a',<459>,1:162]], delta_days=[[@33,148:148='a',<459>,1:148]]}, events={created_at=[[@13,55:55='e',<459>,1:55]], offset_days=[[@9,40:40='e',<459>,1:40]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={unnamed_0=[[@24,108:108='1',<300>,1:108]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_delete2={query_dictionary={}, table_dictionary={events={created_at=[[@13,55:55='e',<459>,1:55]], offset_days=[[@9,40:40='e',<459>,1:40]]}}, dependent_queries={exists1={query=query0, type=filters}}, def_query0={query_dictionary={unnamed_0=[[@24,108:108='1',<300>,1:108]]}, table_dictionary={adjustments={updated_at=[[@37,162:162='a',<459>,1:162]], delta_days=[[@33,148:148='a',<459>,1:148]]}}, filters=[{name=delta_days, table_ref=a}, {name=updated_at, table_ref=a}], interface={unnamed_0=[]}, table_alias={a=adjustments}}, filters=[{name=offset_days, table_ref=e}, {name=created_at, table_ref=e}], interface=null, table_alias={e=events}}}", extractor.getSymbolTable().toString());
		Assert.assertEquals("Array Output Collectors are wrong", "{SQL={queryInterface=[]}}", extractor.getArrayOutputCollectorsMap().toString());

		final String expectedDeleteDefDelete2DependentQueries_0 = "{exists1={query=query0, type=filters}}";
		assertSymbolTreePathEquals(extractor.getSymbolTable(), "def_delete2.dependent_queries", expectedDeleteDefDelete2DependentQueries_0);
		final String expectedDeleteDefDelete2Filters_1 = "[{name=offset_days, table_ref=e}, {name=created_at, table_ref=e}]";
		assertSymbolTreePathEquals(extractor.getSymbolTable(), "def_delete2.filters", expectedDeleteDefDelete2Filters_1);
		final String expectedDeleteDefDelete2DefQuery0Filters_2 = "[{name=delta_days, table_ref=a}, {name=updated_at, table_ref=a}]";
		assertSymbolTreePathEquals(extractor.getSymbolTable(), "def_delete2.def_query0.filters", expectedDeleteDefDelete2DefQuery0Filters_2);
	}

	@Test
	public void dateaddAcrossUnionAllBranchesRetainsBranchLocalDependencies() {
		final String query = "select dateadd(day, a.offset_days, a.created_at) as shifted, a.id as event_id from events a union all select dateadd(day, b.offset_days, b.created_at) as shifted, b.id as event_id from events_archive b union all select c.created_at as shifted, c.id as event_id from events c";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={union={1={select={1={alias=shifted, datetime_function={function_name=dateadd, level={canonical=DAY, text=day, form=KEYWORD}, increment={column={name=offset_days, table_ref=a}}, value={column={name=created_at, table_ref=a}}}}, 2={column={name=id, table_ref=a}, alias=event_id}}, from={table={alias=a, table=events}}}, 2={union={qualifier=all, operator=union}}, 3={select={1={alias=shifted, datetime_function={function_name=dateadd, level={canonical=DAY, text=day, form=KEYWORD}, increment={column={name=offset_days, table_ref=b}}, value={column={name=created_at, table_ref=b}}}}, 2={column={name=id, table_ref=b}, alias=event_id}}, from={table={alias=b, table=events_archive}}}, 4={union={qualifier=all, operator=union}}, 5={select={1={column={name=created_at, table_ref=c}, alias=shifted}, 2={column={name=id, table_ref=c}, alias=event_id}}, from={table={alias=c, table=events}}}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[event_id, shifted]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{events_archive={created_at=[[@35,137:137='b',<459>,1:137]], offset_days=[[@31,122:122='b',<459>,1:122]], id=[[@42,163:163='b',<459>,1:163]]}, events={created_at=[[@9,35:35='a',<459>,1:35], [@53,219:219='c',<459>,1:219]], offset_days=[[@5,20:20='a',<459>,1:20]], id=[[@16,61:61='a',<459>,1:61], [@59,244:244='c',<459>,1:244]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={shifted=[[@14,52:58='shifted',<459>,1:52]], event_id=[[@20,69:76='event_id',<459>,1:69]]}, query1={shifted=[[@40,154:160='shifted',<459>,1:154]], event_id=[[@46,171:178='event_id',<459>,1:171]]}, query2={shifted=[[@57,235:241='shifted',<459>,1:235]], event_id=[[@63,252:259='event_id',<459>,1:252]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_union3={def_query1={query_dictionary={event_id=[[@46,171:178='event_id',<459>,1:171]], shifted=[[@40,154:160='shifted',<459>,1:154]]}, table_dictionary={events_archive={created_at=[[@35,137:137='b',<459>,1:137]], offset_days=[[@31,122:122='b',<459>,1:122]], id=[[@42,163:163='b',<459>,1:163]]}}, setop=UNION, interface={event_id=[{name=id, table_ref=b}], shifted=[{name=offset_days, table_ref=b}, {name=created_at, table_ref=b}]}, table_alias={b=events_archive}}, def_query0={query_dictionary={event_id=[[@20,69:76='event_id',<459>,1:69]], shifted=[[@14,52:58='shifted',<459>,1:52]]}, table_dictionary={events={created_at=[[@9,35:35='a',<459>,1:35], [@53,219:219='c',<459>,1:219]], offset_days=[[@5,20:20='a',<459>,1:20]], id=[[@16,61:61='a',<459>,1:61], [@59,244:244='c',<459>,1:244]]}}, interface={event_id=[{name=id, table_ref=a}], shifted=[{name=offset_days, table_ref=a}, {name=created_at, table_ref=a}]}, table_alias={a=events}}, interface={event_id=query_column, shifted=query_column}, def_query2={query_dictionary={event_id=[[@63,252:259='event_id',<459>,1:252]], shifted=[[@57,235:241='shifted',<459>,1:235]]}, table_dictionary={events={created_at=[[@53,219:219='c',<459>,1:219]], id=[[@59,244:244='c',<459>,1:244]]}}, setop=UNION, interface={event_id=[{name=id, table_ref=c}], shifted=[{name=created_at, table_ref=c}]}, table_alias={c=events}}}}", extractor.getSymbolTable().toString());

		final String expectedUnionDefUnion3DefQuery0Interface_0 = "{event_id=[{name=id, table_ref=a}], shifted=[{name=offset_days, table_ref=a}, {name=created_at, table_ref=a}]}";
		assertSymbolTreePathEquals(extractor.getSymbolTable(), "def_union3.def_query0.interface", expectedUnionDefUnion3DefQuery0Interface_0);
		final String expectedUnionDefUnion3DefQuery1Interface_1 = "{event_id=[{name=id, table_ref=b}], shifted=[{name=offset_days, table_ref=b}, {name=created_at, table_ref=b}]}";
		assertSymbolTreePathEquals(extractor.getSymbolTable(), "def_union3.def_query1.interface", expectedUnionDefUnion3DefQuery1Interface_1);
		final String expectedUnionDefUnion3DefQuery2Interface_2 = "{event_id=[{name=id, table_ref=c}], shifted=[{name=created_at, table_ref=c}]}";
		assertSymbolTreePathEquals(extractor.getSymbolTable(), "def_union3.def_query2.interface", expectedUnionDefUnion3DefQuery2Interface_2);
	}

	@Test
	public void dateaddAcrossExceptBranchesKeepsOperandIsolation() {
		final String query = "select dateadd(day, a.offset_days, a.created_at) as shifted, a.id as event_id from events a except select dateadd(day, b.offset_days, b.created_at) as shifted, b.id as event_id from events_archive b";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={union={1={select={1={alias=shifted, datetime_function={function_name=dateadd, level={canonical=DAY, text=day, form=KEYWORD}, increment={column={name=offset_days, table_ref=a}}, value={column={name=created_at, table_ref=a}}}}, 2={column={name=id, table_ref=a}, alias=event_id}}, from={table={alias=a, table=events}}}, 2={union={qualifier=null, operator=except}}, 3={select={1={alias=shifted, datetime_function={function_name=dateadd, level={canonical=DAY, text=day, form=KEYWORD}, increment={column={name=offset_days, table_ref=b}}, value={column={name=created_at, table_ref=b}}}}, 2={column={name=id, table_ref=b}, alias=event_id}}, from={table={alias=b, table=events_archive}}}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[event_id, shifted]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{events_archive={created_at=[[@34,134:134='b',<459>,1:134]], offset_days=[[@30,119:119='b',<459>,1:119]], id=[[@41,160:160='b',<459>,1:160]]}, events={created_at=[[@9,35:35='a',<459>,1:35]], offset_days=[[@5,20:20='a',<459>,1:20]], id=[[@16,61:61='a',<459>,1:61]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={shifted=[[@14,52:58='shifted',<459>,1:52]], event_id=[[@20,69:76='event_id',<459>,1:69]]}, query1={shifted=[[@39,151:157='shifted',<459>,1:151]], event_id=[[@45,168:175='event_id',<459>,1:168]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_union2={def_query1={query_dictionary={event_id=[[@45,168:175='event_id',<459>,1:168]], shifted=[[@39,151:157='shifted',<459>,1:151]]}, table_dictionary={events_archive={created_at=[[@34,134:134='b',<459>,1:134]], offset_days=[[@30,119:119='b',<459>,1:119]], id=[[@41,160:160='b',<459>,1:160]]}}, setop=EXCEPT, interface={event_id=[{name=id, table_ref=b}], shifted=[{name=offset_days, table_ref=b}, {name=created_at, table_ref=b}]}, table_alias={b=events_archive}}, def_query0={query_dictionary={event_id=[[@20,69:76='event_id',<459>,1:69]], shifted=[[@14,52:58='shifted',<459>,1:52]]}, table_dictionary={events={created_at=[[@9,35:35='a',<459>,1:35]], offset_days=[[@5,20:20='a',<459>,1:20]], id=[[@16,61:61='a',<459>,1:61]]}}, interface={event_id=[{name=id, table_ref=a}], shifted=[{name=offset_days, table_ref=a}, {name=created_at, table_ref=a}]}, table_alias={a=events}}, interface={event_id=query_column, shifted=query_column}}}", extractor.getSymbolTable().toString());

		final String expectedExceptDefUnion2DefQuery0Interface_0 = "{event_id=[{name=id, table_ref=a}], shifted=[{name=offset_days, table_ref=a}, {name=created_at, table_ref=a}]}";
		assertSymbolTreePathEquals(extractor.getSymbolTable(), "def_union2.def_query0.interface", expectedExceptDefUnion2DefQuery0Interface_0);
		final String expectedExceptDefUnion2DefQuery1Interface_1 = "{event_id=[{name=id, table_ref=b}], shifted=[{name=offset_days, table_ref=b}, {name=created_at, table_ref=b}]}";
		assertSymbolTreePathEquals(extractor.getSymbolTable(), "def_union2.def_query1.interface", expectedExceptDefUnion2DefQuery1Interface_1);
	}

	@Test
	public void dateaddAcrossIntersectBranchesKeepsOperandIsolation() {
		final String query = "select dateadd(day, a.offset_days, a.created_at) as shifted, a.id as event_id from events a intersect select dateadd(day, b.offset_days, b.created_at) as shifted, b.id as event_id from events_archive b";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={intersect={1={select={1={alias=shifted, datetime_function={function_name=dateadd, level={canonical=DAY, text=day, form=KEYWORD}, increment={column={name=offset_days, table_ref=a}}, value={column={name=created_at, table_ref=a}}}}, 2={column={name=id, table_ref=a}, alias=event_id}}, from={table={alias=a, table=events}}}, 2={intersect={qualifier=null, operator=intersect}}, 3={select={1={alias=shifted, datetime_function={function_name=dateadd, level={canonical=DAY, text=day, form=KEYWORD}, increment={column={name=offset_days, table_ref=b}}, value={column={name=created_at, table_ref=b}}}}, 2={column={name=id, table_ref=b}, alias=event_id}}, from={table={alias=b, table=events_archive}}}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[event_id, shifted]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{events_archive={created_at=[[@34,137:137='b',<459>,1:137]], offset_days=[[@30,122:122='b',<459>,1:122]], id=[[@41,163:163='b',<459>,1:163]]}, events={created_at=[[@9,35:35='a',<459>,1:35]], offset_days=[[@5,20:20='a',<459>,1:20]], id=[[@16,61:61='a',<459>,1:61]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={shifted=[[@14,52:58='shifted',<459>,1:52]], event_id=[[@20,69:76='event_id',<459>,1:69]]}, query1={shifted=[[@39,154:160='shifted',<459>,1:154]], event_id=[[@45,171:178='event_id',<459>,1:171]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_intersect2={def_query1={query_dictionary={event_id=[[@45,171:178='event_id',<459>,1:171]], shifted=[[@39,154:160='shifted',<459>,1:154]]}, table_dictionary={events_archive={created_at=[[@34,137:137='b',<459>,1:137]], offset_days=[[@30,122:122='b',<459>,1:122]], id=[[@41,163:163='b',<459>,1:163]]}}, setop=INTERSECTION, interface={event_id=[{name=id, table_ref=b}], shifted=[{name=offset_days, table_ref=b}, {name=created_at, table_ref=b}]}, table_alias={b=events_archive}}, def_query0={query_dictionary={event_id=[[@20,69:76='event_id',<459>,1:69]], shifted=[[@14,52:58='shifted',<459>,1:52]]}, table_dictionary={events={created_at=[[@9,35:35='a',<459>,1:35]], offset_days=[[@5,20:20='a',<459>,1:20]], id=[[@16,61:61='a',<459>,1:61]]}}, interface={event_id=[{name=id, table_ref=a}], shifted=[{name=offset_days, table_ref=a}, {name=created_at, table_ref=a}]}, table_alias={a=events}}, interface={event_id=query_column, shifted=query_column}}}", extractor.getSymbolTable().toString());

		final String expectedIntersectDefIntersect2DefQuery0Interface_0 = "{event_id=[{name=id, table_ref=a}], shifted=[{name=offset_days, table_ref=a}, {name=created_at, table_ref=a}]}";
		assertSymbolTreePathEquals(extractor.getSymbolTable(), "def_intersect2.def_query0.interface", expectedIntersectDefIntersect2DefQuery0Interface_0);
		final String expectedIntersectDefIntersect2DefQuery1Interface_1 = "{event_id=[{name=id, table_ref=b}], shifted=[{name=offset_days, table_ref=b}, {name=created_at, table_ref=b}]}";
		assertSymbolTreePathEquals(extractor.getSymbolTable(), "def_intersect2.def_query1.interface", expectedIntersectDefIntersect2DefQuery1Interface_1);
	}

	@Test
	public void dateaddAcrossNestedMixedSetOperationsPublishesIntermediateInterfaces() {
		final String query = "(select dateadd(day, a.offset_days, a.created_at) as shifted, a.id as event_id from events a union select dateadd(day, b.offset_days, b.created_at) as shifted, b.id as event_id from events_archive b) except select dateadd(day, c.offset_days, c.created_at) as shifted, c.id as event_id from events c";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={union={1={union={1={select={1={alias=shifted, datetime_function={function_name=dateadd, level={canonical=DAY, text=day, form=KEYWORD}, increment={column={name=offset_days, table_ref=a}}, value={column={name=created_at, table_ref=a}}}}, 2={column={name=id, table_ref=a}, alias=event_id}}, from={table={alias=a, table=events}}}, 2={union={qualifier=null, operator=union}}, 3={select={1={alias=shifted, datetime_function={function_name=dateadd, level={canonical=DAY, text=day, form=KEYWORD}, increment={column={name=offset_days, table_ref=b}}, value={column={name=created_at, table_ref=b}}}}, 2={column={name=id, table_ref=b}, alias=event_id}}, from={table={alias=b, table=events_archive}}}}}, 2={union={qualifier=null, operator=except}}, 3={select={1={alias=shifted, datetime_function={function_name=dateadd, level={canonical=DAY, text=day, form=KEYWORD}, increment={column={name=offset_days, table_ref=c}}, value={column={name=created_at, table_ref=c}}}}, 2={column={name=id, table_ref=c}, alias=event_id}}, from={table={alias=c, table=events}}}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[event_id, shifted]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{events_archive={created_at=[[@35,134:134='b',<459>,1:134]], offset_days=[[@31,119:119='b',<459>,1:119]], id=[[@42,160:160='b',<459>,1:160]]}, events={created_at=[[@10,36:36='a',<459>,1:36], [@61,242:242='c',<459>,1:242]], offset_days=[[@6,21:21='a',<459>,1:21], [@57,227:227='c',<459>,1:227]], id=[[@17,62:62='a',<459>,1:62], [@68,268:268='c',<459>,1:268]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={shifted=[[@15,53:59='shifted',<459>,1:53]], event_id=[[@21,70:77='event_id',<459>,1:70]]}, query1={shifted=[[@40,151:157='shifted',<459>,1:151]], event_id=[[@46,168:175='event_id',<459>,1:168]]}, query3={shifted=[[@66,259:265='shifted',<459>,1:259]], event_id=[[@72,276:283='event_id',<459>,1:276]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_union4={def_union2={def_query1={query_dictionary={event_id=[[@46,168:175='event_id',<459>,1:168]], shifted=[[@40,151:157='shifted',<459>,1:151]]}, table_dictionary={events_archive={created_at=[[@35,134:134='b',<459>,1:134]], offset_days=[[@31,119:119='b',<459>,1:119]], id=[[@42,160:160='b',<459>,1:160]]}}, setop=UNION, interface={event_id=[{name=id, table_ref=b}], shifted=[{name=offset_days, table_ref=b}, {name=created_at, table_ref=b}]}, table_alias={b=events_archive}}, def_query0={query_dictionary={event_id=[[@21,70:77='event_id',<459>,1:70]], shifted=[[@15,53:59='shifted',<459>,1:53]]}, table_dictionary={events={created_at=[[@10,36:36='a',<459>,1:36], [@61,242:242='c',<459>,1:242]], offset_days=[[@6,21:21='a',<459>,1:21], [@57,227:227='c',<459>,1:227]], id=[[@17,62:62='a',<459>,1:62], [@68,268:268='c',<459>,1:268]]}}, interface={event_id=[{name=id, table_ref=a}], shifted=[{name=offset_days, table_ref=a}, {name=created_at, table_ref=a}]}, table_alias={a=events}}, interface={event_id=query_column, shifted=query_column}}, interface={event_id=query_column, shifted=query_column}, def_query3={query_dictionary={event_id=[[@72,276:283='event_id',<459>,1:276]], shifted=[[@66,259:265='shifted',<459>,1:259]]}, table_dictionary={events={created_at=[[@61,242:242='c',<459>,1:242]], offset_days=[[@57,227:227='c',<459>,1:227]], id=[[@68,268:268='c',<459>,1:268]]}}, setop=EXCEPT, interface={event_id=[{name=id, table_ref=c}], shifted=[{name=offset_days, table_ref=c}, {name=created_at, table_ref=c}]}, table_alias={c=events}}}}", extractor.getSymbolTable().toString());

		final String expectedNestedDefUnion4DefUnion2Interface_0 = "{event_id=query_column, shifted=query_column}";
		assertSymbolTreePathEquals(extractor.getSymbolTable(), "def_union4.def_union2.interface", expectedNestedDefUnion4DefUnion2Interface_0);
		final String expectedNestedDefUnion4DefQuery3Interface_1 = "{event_id=[{name=id, table_ref=c}], shifted=[{name=offset_days, table_ref=c}, {name=created_at, table_ref=c}]}";
		assertSymbolTreePathEquals(extractor.getSymbolTable(), "def_union4.def_query3.interface", expectedNestedDefUnion4DefQuery3Interface_1);
	}

	@Test
	public void dateaddConsumesSetOpResultInInsertSourcePreservingIntermediateExports() {
		final String query = "insert into archive_events (shifted_at, event_id) select dateadd(day, u.delta_days, u.created_at) as shifted_at, u.id as event_id from (select a.offset_days as delta_days, a.created_at, a.id from events a union all select b.offset_days as delta_days, b.created_at, b.id from events_archive b) u";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={insert={preamble=insert_into, from={from={table={alias=u, query={union={1={select={1={column={name=offset_days, table_ref=a}, alias=delta_days}, 2={column={name=created_at, table_ref=a}}, 3={column={name=id, table_ref=a}}}, from={table={alias=a, table=events}}}, 2={union={qualifier=all, operator=union}}, 3={select={1={column={name=offset_days, table_ref=b}, alias=delta_days}, 2={column={name=created_at, table_ref=b}}, 3={column={name=id, table_ref=b}}}, from={table={alias=b, table=events_archive}}}}}}}, select={1={alias=shifted_at, datetime_function={function_name=dateadd, level={canonical=DAY, text=day, form=KEYWORD}, increment={column={name=delta_days, table_ref=u}}, value={column={name=created_at, table_ref=u}}}}, 2={column={name=id, table_ref=u}, alias=event_id}}}, target_table={table={alias=null, table=archive_events}}, columns={1={column={name=shifted_at, table_ref=null}}, 2={column={name=event_id, table_ref=null}}}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[shifted_at, event_id]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{archive_events={shifted_at=[[@4,28:37='shifted_at',<459>,1:28]], event_id=[[@6,40:47='event_id',<459>,1:40]]}, events_archive={created_at=[[@57,251:251='b',<459>,1:251]], offset_days=[[@51,222:222='b',<459>,1:222]], id=[[@61,265:265='b',<459>,1:265]]}, events={created_at=[[@38,172:172='a',<459>,1:172]], offset_days=[[@32,143:143='a',<459>,1:143]], id=[[@42,186:186='a',<459>,1:186]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{union2={created_at=[[@17,84:84='u',<459>,1:84]], id=[[@24,113:113='u',<459>,1:113]], delta_days=[[@13,70:70='u',<459>,1:70]]}, query0={created_at=[[@40,174:183='created_at',<459>,1:174]], id=[[@44,188:189='id',<459>,1:188]], delta_days=[[@36,160:169='delta_days',<459>,1:160]]}, insert4={shifted_at=[[@4,28:37='shifted_at',<459>,1:28]], event_id=[[@6,40:47='event_id',<459>,1:40]]}, query1={created_at=[[@59,253:262='created_at',<459>,1:253]], id=[[@63,267:268='id',<459>,1:267]], delta_days=[[@55,239:248='delta_days',<459>,1:239]]}, query3={shifted_at=[[@22,101:110='shifted_at',<459>,1:101]], event_id=[[@28,121:128='event_id',<459>,1:121]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_insert4={query_dictionary={shifted_at=[[@4,28:37='shifted_at',<459>,1:28]], event_id=[[@6,40:47='event_id',<459>,1:40]]}, table_dictionary={archive_events={shifted_at=[[@4,28:37='shifted_at',<459>,1:28]], event_id=[[@6,40:47='event_id',<459>,1:40]]}}, interface={shifted_at=[{name=shifted_at, table_ref=query3}], event_id=[{name=event_id, table_ref=query3}]}, def_query3={def_union2={query_dictionary={created_at=[[@17,84:84='u',<459>,1:84]], id=[[@24,113:113='u',<459>,1:113]], delta_days=[[@13,70:70='u',<459>,1:70]]}, def_query1={query_dictionary={created_at=[[@59,253:262='created_at',<459>,1:253]], id=[[@63,267:268='id',<459>,1:267]], delta_days=[[@55,239:248='delta_days',<459>,1:239]]}, table_dictionary={events_archive={created_at=[[@57,251:251='b',<459>,1:251]], offset_days=[[@51,222:222='b',<459>,1:222]], id=[[@61,265:265='b',<459>,1:265]]}}, setop=UNION, interface={created_at=[{name=created_at, table_ref=b}], id=[{name=id, table_ref=b}], delta_days=[{name=offset_days, table_ref=b}]}, table_alias={b=events_archive}}, def_query0={query_dictionary={created_at=[[@40,174:183='created_at',<459>,1:174]], id=[[@44,188:189='id',<459>,1:188]], delta_days=[[@36,160:169='delta_days',<459>,1:160]]}, table_dictionary={events={created_at=[[@38,172:172='a',<459>,1:172]], offset_days=[[@32,143:143='a',<459>,1:143]], id=[[@42,186:186='a',<459>,1:186]]}}, interface={created_at=[{name=created_at, table_ref=a}], id=[{name=id, table_ref=a}], delta_days=[{name=offset_days, table_ref=a}]}, table_alias={a=events}}, interface={created_at=query_column, id=query_column, delta_days=query_column}}, query_dictionary={shifted_at=[[@22,101:110='shifted_at',<459>,1:101]], event_id=[[@28,121:128='event_id',<459>,1:121]]}, interface={shifted_at=[{name=delta_days, table_ref=u}, {name=created_at, table_ref=u}], event_id=[{name=id, table_ref=u}]}, table_alias={u=union2}}}}", extractor.getSymbolTable().toString());

		final String expectedInsertDefInsert4DefQuery3DefUnion2Interface_0 = "{created_at=query_column, id=query_column, delta_days=query_column}";
		assertSymbolTreePathEquals(extractor.getSymbolTable(), "def_insert4.def_query3.def_union2.interface", expectedInsertDefInsert4DefQuery3DefUnion2Interface_0);
		final String expectedInsertDefInsert4DefQuery3Interface_1 = "{shifted_at=[{name=delta_days, table_ref=u}, {name=created_at, table_ref=u}], event_id=[{name=id, table_ref=u}]}";
		assertSymbolTreePathEquals(extractor.getSymbolTable(), "def_insert4.def_query3.interface", expectedInsertDefInsert4DefQuery3Interface_1);
	}

	@Test
	public void qualifiedSameNameDateaddUdfRemainsGenericAlongsideBuiltinDateadd() {
		final String query = "select dateadd(day, 1, a.created_at) as built_in_shift, custom.dateadd(a.part, 1, a.created_at) as udf_shift from events a";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=built_in_shift, datetime_function={function_name=dateadd, level={canonical=DAY, text=day, form=KEYWORD}, increment={literal=1}, value={column={name=created_at, table_ref=a}}}}, 2={function={schema=custom, parameters={1={column={name=part, table_ref=a}}, 2={literal=1}, 3={column={name=created_at, table_ref=a}}}, function_name=dateadd}, alias=udf_shift}}, from={table={alias=a, table=events}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[built_in_shift, udf_shift]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{events={part=[[@18,71:71='a',<459>,1:71]], created_at=[[@7,23:23='a',<459>,1:23], [@24,82:82='a',<459>,1:82]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={built_in_shift=[[@12,40:53='built_in_shift',<459>,1:40]], udf_shift=[[@29,99:107='udf_shift',<459>,1:99]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={built_in_shift=[[@12,40:53='built_in_shift',<459>,1:40]], udf_shift=[[@29,99:107='udf_shift',<459>,1:99]]}, table_dictionary={events={part=[[@18,71:71='a',<459>,1:71]], created_at=[[@7,23:23='a',<459>,1:23], [@24,82:82='a',<459>,1:82]]}}, interface={built_in_shift=[{name=created_at, table_ref=a}], udf_shift=[{name=part, table_ref=a}, {name=created_at, table_ref=a}]}, table_alias={a=events}}}", extractor.getSymbolTable().toString());

		final String expectedQualifiedNameDefQuery0Interface_0 = "{built_in_shift=[{name=created_at, table_ref=a}], udf_shift=[{name=part, table_ref=a}, {name=created_at, table_ref=a}]}";
		assertSymbolTreePathEquals(extractor.getSymbolTable(), "def_query0.interface", expectedQualifiedNameDefQuery0Interface_0);
	}

	@Test
	public void dateaddScriptStatementsKeepDiagnosticsScopedPerStatement() {
		final String script = "select dateadd(day, 1, a.created_at) as shifted from events a; select e.id from events e qualify row_number() over (partition by dateadd(day, e.offset_days, e.created_at) order by dateadd(hour, e.offset_hours, e.created_at)) = 1";
		SqlParserAccess parser = new SqlParserAccess(false, false, false);
		parser.executeTheParse(script, "SCRIPT");
		Assert.assertEquals(script, 0, parser.getParser().getNumberOfSyntaxErrors());
		Assert.assertEquals(parser.getAllDiagnostics().toString(), 2, parser.getAllDiagnostics().size());
		assertSnowflakeGrammarWarningAt(parser, "snowflake_datetime_add_expression", 7);
		assertSnowflakeGrammarWarningAt(parser, "snowflake_datetime_add_expression", 129);
		Assert.assertEquals("AST is wrong", "{SCRIPT={1={select={1={alias=shifted, datetime_function={function_name=dateadd, level={canonical=DAY, text=day, form=KEYWORD}, increment={literal=1}, value={column={name=created_at, table_ref=a}}}}}, from={table={alias=a, table=events}}}, 2={select={1={column={name=id, table_ref=e}}}, from={table={alias=e, table=events}}, qualify={condition={left={window_function={over={partition_by={1={datetime_function={function_name=dateadd, level={canonical=DAY, text=day, form=KEYWORD}, increment={column={name=offset_days, table_ref=e}}, value={column={name=created_at, table_ref=e}}}}}, orderby={1={null_order=null, predicand={datetime_function={function_name=dateadd, level={canonical=HOUR, text=hour, form=KEYWORD}, increment={column={name=offset_hours, table_ref=e}}, value={column={name=created_at, table_ref=e}}}}, sort_order=ASC}}}, function={function_name=row_number, parameters=null}}}, right={literal=1}, operator==}}}}}", parser.getSnippet().getSqlAbstractTree().toString());
		Assert.assertEquals("Symbol Table is wrong", "{SCRIPT={1={def_query0={query_dictionary={shifted=[[@12,40:46='shifted',<459>,1:40]]}, table_dictionary={events={created_at=[[@7,23:23='a',<459>,1:23]]}}, interface={shifted=[{name=created_at, table_ref=a}]}, table_alias={a=events}}}, 2={def_query0={window_ordered_by=[{name=offset_hours, table_ref=e}, {name=created_at, table_ref=e}], query_dictionary={id=[[@20,72:73='id',<459>,1:72]]}, table_dictionary={events={created_at=[[@40,157:157='e',<459>,1:157], [@54,210:210='e',<459>,1:210]], offset_hours=[[@50,194:194='e',<459>,1:194]], offset_days=[[@36,142:142='e',<459>,1:142]], id=[[@18,70:70='e',<459>,1:70]]}}, window_partition_by=[{name=offset_days, table_ref=e}, {name=created_at, table_ref=e}], filters=[{name=offset_days, table_ref=e}, {name=created_at, table_ref=e}, {name=offset_hours, table_ref=e}], interface={id=[{name=id, table_ref=e}]}, table_alias={e=events}}}}}", parser.getSnippet().getSymbolTable().toString());
		Assert.assertEquals("Array Output Collectors are wrong", "{SCRIPT={1={queryInterface=[shifted], Walker Warnings=[snowflake_datetime_add_expression at (l:1 c:7) is recognized as Snowflake-specific grammar., snowflake_datetime_add_expression at (l:1 c:129) is recognized as Snowflake-specific grammar.]}, 2={queryInterface=[id], Walker Warnings=[snowflake_datetime_add_expression at (l:1 c:7) is recognized as Snowflake-specific grammar., snowflake_datetime_add_expression at (l:1 c:129) is recognized as Snowflake-specific grammar.]}}}", parser.getSnippet().getArrayOutputCollectorsMap().toString());

		final String expectedScriptStatement2DefQuery0WindowOrderedBy_0 = "[{name=offset_hours, table_ref=e}, {name=created_at, table_ref=e}]";
		assertSymbolTreePathEquals(parser.getSnippet().getSymbolTable(), "SCRIPT.2.def_query0.window_ordered_by", expectedScriptStatement2DefQuery0WindowOrderedBy_0);
		final String expectedScriptStatement2DefQuery0WindowPartitionBy_1 = "[{name=offset_days, table_ref=e}, {name=created_at, table_ref=e}]";
		assertSymbolTreePathEquals(parser.getSnippet().getSymbolTable(), "SCRIPT.2.def_query0.window_partition_by", expectedScriptStatement2DefQuery0WindowPartitionBy_1);
	}

	@Test
	public void timeaddRepresentativeSubsetCoversJoinedOnAndNestedFunctionContexts() {
		final String query = "select timeadd(hour, a.offset_hours, a.start_time) as shifted_time from events a join accounts b on timeadd(minute, b.offset_minutes, b.base_time) > a.start_time where timeadd(hour, a.offset_hours, a.start_time) > coalesce(a.start_time, b.base_time)";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=shifted_time, datetime_function={function_name=timeadd, level={canonical=HOUR, text=hour, form=KEYWORD}, increment={column={name=offset_hours, table_ref=a}}, value={column={name=start_time, table_ref=a}}}}}, from={join={1={table={alias=a, table=events}}, 2={join=join, on={condition={left={datetime_function={function_name=timeadd, level={canonical=MINUTE, text=minute, form=KEYWORD}, increment={column={name=offset_minutes, table_ref=b}}, value={column={name=base_time, table_ref=b}}}}, right={column={name=start_time, table_ref=a}}, operator=>}}}, 3={table={alias=b, table=accounts}}}}, where={condition={left={datetime_function={function_name=timeadd, level={canonical=HOUR, text=hour, form=KEYWORD}, increment={column={name=offset_hours, table_ref=a}}, value={column={name=start_time, table_ref=a}}}}, right={function={parameters={1={column={name=start_time, table_ref=a}}, 2={column={name=base_time, table_ref=b}}}, function_name=coalesce}}, operator=>}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[shifted_time]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{accounts={base_time=[[@30,134:134='b',<459>,1:134], [@58,237:237='b',<459>,1:237]], offset_minutes=[[@26,116:116='b',<459>,1:116]]}, events={start_time=[[@9,37:37='a',<459>,1:37], [@35,149:149='a',<459>,1:149], [@47,198:198='a',<459>,1:198], [@54,223:223='a',<459>,1:223]], offset_hours=[[@5,21:21='a',<459>,1:21], [@43,182:182='a',<459>,1:182]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={shifted_time=[[@14,54:65='shifted_time',<459>,1:54]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={shifted_time=[[@14,54:65='shifted_time',<459>,1:54]]}, table_dictionary={accounts={base_time=[[@30,134:134='b',<459>,1:134], [@58,237:237='b',<459>,1:237]], offset_minutes=[[@26,116:116='b',<459>,1:116]]}, events={start_time=[[@9,37:37='a',<459>,1:37], [@35,149:149='a',<459>,1:149], [@47,198:198='a',<459>,1:198], [@54,223:223='a',<459>,1:223]], offset_hours=[[@5,21:21='a',<459>,1:21], [@43,182:182='a',<459>,1:182]]}}, filters=[{name=offset_minutes, table_ref=b}, {name=base_time, table_ref=b}, {name=start_time, table_ref=a}, {name=offset_hours, table_ref=a}], interface={shifted_time=[{name=offset_hours, table_ref=a}, {name=start_time, table_ref=a}]}, table_alias={a=events, b=accounts}}}", extractor.getSymbolTable().toString());

		final String expectedTimeaddDefQuery0Filters_0 = "[{name=offset_minutes, table_ref=b}, {name=base_time, table_ref=b}, {name=start_time, table_ref=a}, {name=offset_hours, table_ref=a}]";
		assertSymbolTreePathEquals(extractor.getSymbolTable(), "def_query0.filters", expectedTimeaddDefQuery0Filters_0);
	}

	@Test
	public void timestampaddRepresentativeSubsetCoversCteAndSetOperationContexts() {
		final String query = "with base as (select timestampadd(day, e.offset_days, e.created_at) as shifted_ts, e.id from events e), recent as (select b.shifted_ts, b.id from base b union all select timestampadd(day, a.offset_days, a.created_at) as shifted_ts, a.id from events_archive a) select timestampadd(hour, 1, r.shifted_ts) as final_shift, r.id from recent r";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={with={1={cte={select={1={alias=shifted_ts, datetime_function={function_name=timestampadd, level={canonical=DAY, text=day, form=KEYWORD}, increment={column={name=offset_days, table_ref=e}}, value={column={name=created_at, table_ref=e}}}}, 2={column={name=id, table_ref=e}}}, from={table={alias=e, table=events}}}, alias=base}, 2={cte={union={1={select={1={column={name=shifted_ts, table_ref=b}}, 2={column={name=id, table_ref=b}}}, from={table={alias=b, table=base}}}, 2={union={qualifier=all, operator=union}}, 3={select={1={alias=shifted_ts, datetime_function={function_name=timestampadd, level={canonical=DAY, text=day, form=KEYWORD}, increment={column={name=offset_days, table_ref=a}}, value={column={name=created_at, table_ref=a}}}}, 2={column={name=id, table_ref=a}}}, from={table={alias=a, table=events_archive}}}}}, alias=recent}}, query={select={1={alias=final_shift, datetime_function={function_name=timestampadd, level={canonical=HOUR, text=hour, form=KEYWORD}, increment={literal=1}, value={column={name=shifted_ts, table_ref=r}}}}, 2={column={name=id, table_ref=r}}}, from={table={alias=r, table=recent}}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[final_shift, id]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{events_archive={created_at=[[@53,203:203='a',<459>,1:203]], offset_days=[[@49,188:188='a',<459>,1:188]], id=[[@60,232:232='a',<459>,1:232]]}, events={created_at=[[@13,54:54='e',<459>,1:54]], offset_days=[[@9,39:39='e',<459>,1:39]], id=[[@20,83:83='e',<459>,1:83]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{union3={id=[[@81,319:319='r',<459>,1:319]], shifted_ts=[[@74,289:289='r',<459>,1:289]]}, query4={final_shift=[[@79,306:316='final_shift',<459>,1:306]], id=[[@83,321:322='id',<459>,1:321]]}, query0={id=[[@22,85:86='id',<459>,1:85], [@36,136:136='b',<459>,1:136]], shifted_ts=[[@18,71:80='shifted_ts',<459>,1:71], [@32,122:122='b',<459>,1:122]]}, query1={id=[[@38,138:139='id',<459>,1:138]], shifted_ts=[[@34,124:133='shifted_ts',<459>,1:124]]}, query2={id=[[@62,234:235='id',<459>,1:234]], shifted_ts=[[@58,220:229='shifted_ts',<459>,1:220]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query4={def_union3={context_list={base=query0}, query_dictionary={id=[[@81,319:319='r',<459>,1:319]], shifted_ts=[[@74,289:289='r',<459>,1:289]]}, def_query1={context_list={base=query0, b=query0}, query_dictionary={id=[[@38,138:139='id',<459>,1:138]], shifted_ts=[[@34,124:133='shifted_ts',<459>,1:124]]}, interface={id=[{name=id, table_ref=b}], shifted_ts=[{name=shifted_ts, table_ref=b}]}, table_alias={b=query0, base=query0}}, interface={id=query_column, shifted_ts=query_column}, table_alias={base=query0}, def_query2={context_list={base=query0}, query_dictionary={id=[[@62,234:235='id',<459>,1:234]], shifted_ts=[[@58,220:229='shifted_ts',<459>,1:220]]}, table_dictionary={events_archive={created_at=[[@53,203:203='a',<459>,1:203]], offset_days=[[@49,188:188='a',<459>,1:188]], id=[[@60,232:232='a',<459>,1:232]]}}, setop=UNION, interface={id=[{name=id, table_ref=a}], shifted_ts=[{name=offset_days, table_ref=a}, {name=created_at, table_ref=a}]}, table_alias={a=events_archive, base=query0}}}, context_list={base=query0, recent=union3, r=union3}, query_dictionary={final_shift=[[@79,306:316='final_shift',<459>,1:306]], id=[[@83,321:322='id',<459>,1:321]]}, def_query0={query_dictionary={id=[[@22,85:86='id',<459>,1:85], [@36,136:136='b',<459>,1:136]], shifted_ts=[[@18,71:80='shifted_ts',<459>,1:71], [@32,122:122='b',<459>,1:122]]}, table_dictionary={events={created_at=[[@13,54:54='e',<459>,1:54]], offset_days=[[@9,39:39='e',<459>,1:39]], id=[[@20,83:83='e',<459>,1:83]]}}, interface={id=[{name=id, table_ref=e}], shifted_ts=[{name=offset_days, table_ref=e}, {name=created_at, table_ref=e}]}, table_alias={e=events}}, interface={final_shift=[{name=shifted_ts, table_ref=r}], id=[{name=id, table_ref=r}]}, table_alias={r=union3, recent=union3, base=query0}}}", extractor.getSymbolTable().toString());

		final String expectedTimestampAddDefQuery4DefUnion3Interface_0 = "{id=query_column, shifted_ts=query_column}";
		assertSymbolTreePathEquals(extractor.getSymbolTable(), "def_query4.def_union3.interface", expectedTimestampAddDefQuery4DefUnion3Interface_0);
		final String expectedTimestampAddDefQuery4Interface_1 = "{final_shift=[{name=shifted_ts, table_ref=r}], id=[{name=id, table_ref=r}]}";
		assertSymbolTreePathEquals(extractor.getSymbolTable(), "def_query4.interface", expectedTimestampAddDefQuery4Interface_1);
	}

	@Test
	public void dateaddRejectsEpochSecondWhileExtractAcceptsIt() {
		SqlParserAccess dateadd = new SqlParserAccess(false, false, false);
		dateadd.executeTheParse("select dateadd(epoch_second, 1, a.ts) as shifted from events a", "SQL");
		Assert.assertEquals(0, dateadd.getParser().getNumberOfSyntaxErrors());
		Assert.assertEquals(dateadd.getAllDiagnostics().toString(), 2, dateadd.getAllDiagnostics().size());
		assertSnowflakeGrammarWarningAt(dateadd, "snowflake_datetime_add_expression", 7);
		assertFatalDiagnosticAtPositionWithFullMessage(dateadd.getSnippet(), "INVALID_DATETIME_PART",
				"Invalid date/time part epoch_second for DATEADD at (l:1 c:15).", "epoch_second", 1, 15);
		Assert.assertEquals("{def_query0={query_dictionary={shifted=[[@12,41:47='shifted',<459>,1:41]]}, table_dictionary={events={ts=[[@7,32:32='a',<459>,1:32]]}}, interface={shifted=[{name=ts, table_ref=a}]}, table_alias={a=events}}}",
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
		Assert.assertEquals("{SQL={select={1={alias=seconds, datetime_function={function_name=extract, part_form=KEYWORD, part=epoch_second, source={column={name=ts, table_ref=a}}}}}, from={table={alias=a, table=events}}}}",
				extract.getSnippet().getSqlAbstractTree().toString());
		Assert.assertEquals("{def_query0={query_dictionary={seconds=[[@10,42:48='seconds',<459>,1:42]]}, table_dictionary={events={ts=[[@5,33:33='a',<459>,1:33]]}}, interface={seconds=[{name=ts, table_ref=a}]}, table_alias={a=events}}}",
				extract.getSnippet().getSymbolTable().toString());
	}

	@Test
	public void datePartEpochSecondEmitsSnowflakeDialectWarning() {
		SqlParserAccess parser = new SqlParserAccess(false, false, false);
		parser.executeTheParse("select date_part(epoch_second from a.ts) as seconds from events a", "SQL");
		Assert.assertEquals(0, parser.getParser().getNumberOfSyntaxErrors());
		Assert.assertEquals(parser.getAllDiagnostics().toString(), 1, parser.getAllDiagnostics().size());
		String warning = "EXTRACT/DATE_PART option 'epoch_second' at (l:1 c:7) is recognized as Snowflake-specific grammar.";
		assertDiagnosticAtPosition(parser.getSnippet(), "STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
				ParseDiagnostic.Severity.WARNING, warning, null, 1, 7);
		Assert.assertEquals(warning, findDiagnosticByCodeAndFragmentAndSeverity(parser.getSnippet(),
				"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR", ParseDiagnostic.Severity.WARNING, "epoch_second").message());
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
			Assert.assertEquals(parser.getAllDiagnostics().toString(), 2, parser.getAllDiagnostics().size());
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
		Assert.assertEquals(parser.getAllDiagnostics().toString(), 3, parser.getAllDiagnostics().size());
		assertFatalDiagnosticAtPositionWithFullMessage(parser.getSnippet(), "INVALID_DATETIME_PART",
				"Invalid date/time part bogus for DATEADD at (l:1 c:15).", "bogus", 1, 15);
		assertFatalDiagnosticAtPositionWithFullMessage(parser.getSnippet(), "INVALID_DATETIME_PART",
				"Invalid date/time part wrong for DATEADD at (l:1 c:33).", "wrong", 1, 33);
		Assert.assertEquals("{def_query0={query_dictionary={shifted=[[@19,53:59='shifted',<459>,1:53]]}, table_dictionary={events={ts=[[@13,43:43='a',<459>,1:43]]}}, interface={shifted=[{name=ts, table_ref=a}]}, table_alias={a=events}}}",
				parser.getSnippet().getSymbolTable().toString());
	}

	@Test
	public void dateaddReportsInvalidNestedLevelAtTheInnerInvocation() {
		String query = "select dateadd(day, 1, dateadd(bogus, 2, a.ts)) as shifted from events a";
		SqlParserAccess parser = new SqlParserAccess(false, false, false);
		parser.executeTheParse(query, "SQL");
		Assert.assertEquals(query, 0, parser.getParser().getNumberOfSyntaxErrors());
		Assert.assertEquals(parser.getAllDiagnostics().toString(), 2, parser.getAllDiagnostics().size());
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
		Assert.assertEquals("Table Dictionary is wrong", "{events={year=[[@7,30:30='a',<459>,1:30]], ts=[[@19,58:58='a',<459>,1:58], [@25,66:66='a',<459>,1:66]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={shifted=[[@30,75:81='shifted',<459>,1:75]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={shifted=[[@30,75:81='shifted',<459>,1:75]]}, table_dictionary={events={year=[[@7,30:30='a',<459>,1:30]], ts=[[@19,58:58='a',<459>,1:58], [@25,66:66='a',<459>,1:66]]}}, interface={shifted=[{name=year, table_ref=a}, {name=ts, table_ref=a}]}, table_alias={a=events}}}", extractor.getSymbolTable().toString());
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
		Assert.assertEquals("Table Dictionary is wrong", "{events={created_at=[[@7,33:33='a',<459>,1:33]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={shifted=[[@12,50:56='shifted',<459>,1:50]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={shifted=[[@12,50:56='shifted',<459>,1:50]]}, table_dictionary={events={created_at=[[@7,33:33='a',<459>,1:33]]}}, interface={shifted=[{name=<increment>, type=predicand}, {name=created_at, table_ref=a}]}, table_alias={a=events}}}", extractor.getSymbolTable().toString());
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
		Assert.assertEquals("Table Dictionary is wrong", "{events={offset_days=[[@5,20:20='a',<459>,1:20]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={shifted=[[@12,52:58='shifted',<459>,1:52]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={shifted=[[@12,52:58='shifted',<459>,1:52]]}, table_dictionary={events={offset_days=[[@5,20:20='a',<459>,1:20]]}}, interface={shifted=[{name=offset_days, table_ref=a}, {name=<date value>, type=predicand}]}, table_alias={a=events}}}", extractor.getSymbolTable().toString());
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
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={shifted=[[@10,51:57='shifted',<459>,1:51]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={shifted=[[@10,51:57='shifted',<459>,1:51]]}, interface={shifted=[{name=<increment>, type=predicand}, {name=<date value>, type=predicand}]}}}", extractor.getSymbolTable().toString());
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
		Assert.assertEquals("Table Dictionary is wrong", "{accounts={created_at=[[@9,42:42='b',<459>,1:42]], id=[[@26,115:115='b',<459>,1:115]]}, events={<increment column>=[[@5,20:20='a',<459>,1:20]], account_id=[[@22,100:100='a',<459>,1:100]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={shifted=[[@14,59:65='shifted',<459>,1:59]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={shifted=[[@14,59:65='shifted',<459>,1:59]]}, table_dictionary={accounts={created_at=[[@9,42:42='b',<459>,1:42]], id=[[@26,115:115='b',<459>,1:115]]}, events={<increment column>=[[@5,20:20='a',<459>,1:20]], account_id=[[@22,100:100='a',<459>,1:100]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}], interface={shifted=[{substitution={name=<increment column>, type=column}, table_ref=a}, {name=created_at, table_ref=b}]}, table_alias={a=events, b=accounts}}}", extractor.getSymbolTable().toString());
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
		Assert.assertEquals("Table Dictionary is wrong", "{accounts={<date column>=[[@9,35:35='b',<459>,1:35]], id=[[@26,111:111='b',<459>,1:111]]}, events={account_id=[[@22,96:96='a',<459>,1:96]], offset_days=[[@5,20:20='a',<459>,1:20]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={shifted=[[@14,55:61='shifted',<459>,1:55]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={shifted=[[@14,55:61='shifted',<459>,1:55]]}, table_dictionary={accounts={<date column>=[[@9,35:35='b',<459>,1:35]], id=[[@26,111:111='b',<459>,1:111]]}, events={account_id=[[@22,96:96='a',<459>,1:96]], offset_days=[[@5,20:20='a',<459>,1:20]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}], interface={shifted=[{name=offset_days, table_ref=a}, {substitution={name=<date column>, type=column}, table_ref=b}]}, table_alias={a=events, b=accounts}}}", extractor.getSymbolTable().toString());
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
		Assert.assertEquals("Table Dictionary is wrong", "{accounts={<date column>=[[@9,42:42='b',<459>,1:42]], id=[[@26,118:118='b',<459>,1:118]]}, events={<increment column>=[[@5,20:20='a',<459>,1:20]], account_id=[[@22,103:103='a',<459>,1:103]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={shifted=[[@14,62:68='shifted',<459>,1:62]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={shifted=[[@14,62:68='shifted',<459>,1:62]]}, table_dictionary={accounts={<date column>=[[@9,42:42='b',<459>,1:42]], id=[[@26,118:118='b',<459>,1:118]]}, events={<increment column>=[[@5,20:20='a',<459>,1:20]], account_id=[[@22,103:103='a',<459>,1:103]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}], interface={shifted=[{substitution={name=<increment column>, type=column}, table_ref=a}, {substitution={name=<date column>, type=column}, table_ref=b}]}, table_alias={a=events, b=accounts}}}", extractor.getSymbolTable().toString());
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
		Assert.assertEquals("Table Dictionary is wrong", "{events={<increment column>=[[@5,20:20='a',<459>,1:20]], <date column>=[[@9,42:42='a',<459>,1:42]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={shifted=[[@14,62:68='shifted',<459>,1:62]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={shifted=[[@14,62:68='shifted',<459>,1:62]]}, table_dictionary={events={<increment column>=[[@5,20:20='a',<459>,1:20]], <date column>=[[@9,42:42='a',<459>,1:42]]}}, interface={shifted=[{substitution={name=<increment column>, type=column}, table_ref=a}, {substitution={name=<date column>, type=column}, table_ref=a}]}, table_alias={a=events}}}", extractor.getSymbolTable().toString());
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
		Assert.assertEquals("Table Dictionary is wrong", "{events={<date column>=[[@7,33:33='a',<459>,1:33]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={shifted=[[@12,53:59='shifted',<459>,1:53]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={shifted=[[@12,53:59='shifted',<459>,1:53]]}, table_dictionary={events={<date column>=[[@7,33:33='a',<459>,1:33]]}}, interface={shifted=[{name=<increment>, type=predicand}, {substitution={name=<date column>, type=column}, table_ref=a}]}, table_alias={a=events}}}", extractor.getSymbolTable().toString());
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
		Assert.assertEquals("Table Dictionary is wrong", "{events={<increment column>=[[@5,20:20='a',<459>,1:20]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={shifted=[[@12,59:65='shifted',<459>,1:59]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={shifted=[[@12,59:65='shifted',<459>,1:59]]}, table_dictionary={events={<increment column>=[[@5,20:20='a',<459>,1:20]]}}, interface={shifted=[{substitution={name=<increment column>, type=column}, table_ref=a}, {name=<date value>, type=predicand}]}, table_alias={a=events}}}", extractor.getSymbolTable().toString());
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
		Assert.assertEquals("Table Dictionary is wrong", "{events={created_at=[[@13,55:55='a',<459>,1:55]], offset_days=[[@5,20:20='a',<459>,1:20]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={shifted=[[@18,72:78='shifted',<459>,1:72]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={shifted=[[@18,72:78='shifted',<459>,1:72]]}, table_dictionary={events={created_at=[[@13,55:55='a',<459>,1:55]], offset_days=[[@5,20:20='a',<459>,1:20]]}}, interface={shifted=[{name=offset_days, table_ref=a}, {name=<delta>, type=predicand}, {name=<extra>, type=predicand}, {name=created_at, table_ref=a}]}, table_alias={a=events}}}", extractor.getSymbolTable().toString());
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
		Assert.assertEquals("Table Dictionary is wrong", "{events={<delta column>=[[@9,36:36='a',<459>,1:36]], created_at=[[@17,73:73='a',<459>,1:73]], <extra column>=[[@13,55:55='a',<459>,1:55]], offset_days=[[@5,20:20='a',<459>,1:20]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={shifted=[[@22,90:96='shifted',<459>,1:90]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={shifted=[[@22,90:96='shifted',<459>,1:90]]}, table_dictionary={events={<delta column>=[[@9,36:36='a',<459>,1:36]], created_at=[[@17,73:73='a',<459>,1:73]], <extra column>=[[@13,55:55='a',<459>,1:55]], offset_days=[[@5,20:20='a',<459>,1:20]]}}, interface={shifted=[{name=offset_days, table_ref=a}, {substitution={name=<delta column>, type=column}, table_ref=a}, {substitution={name=<extra column>, type=column}, table_ref=a}, {name=created_at, table_ref=a}]}, table_alias={a=events}}}", extractor.getSymbolTable().toString());
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
		Assert.assertEquals("Table Dictionary is wrong", "{events={created_at=[[@7,23:23='a',<459>,1:23]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={shifted=[[@18,66:72='shifted',<459>,1:66]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={shifted=[[@18,66:72='shifted',<459>,1:66]]}, table_dictionary={events={created_at=[[@7,23:23='a',<459>,1:23]]}}, interface={shifted=[{name=created_at, table_ref=a}, {name=<days>, type=predicand}, {name=<extra days>, type=predicand}]}, table_alias={a=events}}}", extractor.getSymbolTable().toString());
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
		Assert.assertEquals("Table Dictionary is wrong", "{events={created_at=[[@7,23:23='a',<459>,1:23]], <days column>=[[@12,39:39='a',<459>,1:39]], <extra days column>=[[@16,57:57='a',<459>,1:57]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={shifted=[[@22,84:90='shifted',<459>,1:84]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={shifted=[[@22,84:90='shifted',<459>,1:84]]}, table_dictionary={events={created_at=[[@7,23:23='a',<459>,1:23]], <days column>=[[@12,39:39='a',<459>,1:39]], <extra days column>=[[@16,57:57='a',<459>,1:57]]}}, interface={shifted=[{name=created_at, table_ref=a}, {substitution={name=<days column>, type=column}, table_ref=a}, {substitution={name=<extra days column>, type=column}, table_ref=a}]}, table_alias={a=events}}}", extractor.getSymbolTable().toString());
	}

	@Test
	public void dateaddRetainsMixedSubstitutionsAndColumnInsideIncrementFormula() {
		final String query = "select dateadd(day, a.offset_days + <extra> + a.<adjustment>, a.created_at) as shifted from events a";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=shifted, datetime_function={function_name=dateadd, level={canonical=DAY, text=day, form=KEYWORD}, increment={calc={left={calc={left={column={name=offset_days, table_ref=a}}, right={substitution={name=<extra>, type=predicand}}, operator=+}}, right={column={substitution={name=<adjustment>, type=column}, table_ref=a}}, operator=+}}, value={column={name=created_at, table_ref=a}}}}}, from={table={alias=a, table=events}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[shifted]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<extra>=predicand, <adjustment>=column}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{events={created_at=[[@15,62:62='a',<459>,1:62]], offset_days=[[@5,20:20='a',<459>,1:20]], <adjustment>=[[@11,46:46='a',<459>,1:46]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={shifted=[[@20,79:85='shifted',<459>,1:79]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={shifted=[[@20,79:85='shifted',<459>,1:79]]}, table_dictionary={events={created_at=[[@15,62:62='a',<459>,1:62]], offset_days=[[@5,20:20='a',<459>,1:20]], <adjustment>=[[@11,46:46='a',<459>,1:46]]}}, interface={shifted=[{name=offset_days, table_ref=a}, {name=<extra>, type=predicand}, {substitution={name=<adjustment>, type=column}, table_ref=a}, {name=created_at, table_ref=a}]}, table_alias={a=events}}}", extractor.getSymbolTable().toString());
	}

	@Test
	public void dateaddRetainsMixedSubstitutionsAndColumnInsideValueFormula() {
		final String query = "select dateadd(day, 1, a.created_at + (<days> + a.<adjustment>)) as shifted from events a";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=shifted, datetime_function={function_name=dateadd, level={canonical=DAY, text=day, form=KEYWORD}, increment={literal=1}, value={calc={left={column={name=created_at, table_ref=a}}, right={parentheses={calc={left={substitution={name=<days>, type=predicand}}, right={column={substitution={name=<adjustment>, type=column}, table_ref=a}}, operator=+}}}, operator=+}}}}}, from={table={alias=a, table=events}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[shifted]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<days>=predicand, <adjustment>=column}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{events={created_at=[[@7,23:23='a',<459>,1:23]], <adjustment>=[[@14,48:48='a',<459>,1:48]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={shifted=[[@20,68:74='shifted',<459>,1:68]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={shifted=[[@20,68:74='shifted',<459>,1:68]]}, table_dictionary={events={created_at=[[@7,23:23='a',<459>,1:23]], <adjustment>=[[@14,48:48='a',<459>,1:48]]}}, interface={shifted=[{name=created_at, table_ref=a}, {name=<days>, type=predicand}, {substitution={name=<adjustment>, type=column}, table_ref=a}]}, table_alias={a=events}}}", extractor.getSymbolTable().toString());
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
		Assert.assertEquals("Table Dictionary is wrong", "{events={<delta column>=[[@11,46:46='a',<459>,1:46]], created_at=[[@15,64:64='a',<459>,1:64]], offset_days=[[@5,20:20='a',<459>,1:20]], <days column>=[[@22,89:89='a',<459>,1:89]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={shifted=[[@28,110:116='shifted',<459>,1:110]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={shifted=[[@28,110:116='shifted',<459>,1:110]]}, table_dictionary={events={<delta column>=[[@11,46:46='a',<459>,1:46]], created_at=[[@15,64:64='a',<459>,1:64]], offset_days=[[@5,20:20='a',<459>,1:20]], <days column>=[[@22,89:89='a',<459>,1:89]]}}, interface={shifted=[{name=offset_days, table_ref=a}, {name=<delta>, type=predicand}, {substitution={name=<delta column>, type=column}, table_ref=a}, {name=created_at, table_ref=a}, {name=<days>, type=predicand}, {substitution={name=<days column>, type=column}, table_ref=a}]}, table_alias={a=events}}}", extractor.getSymbolTable().toString());
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
		Assert.assertEquals("Table Dictionary is wrong", "{events={<increment column>=[[@5,20:20='a',<459>,1:20]], ts=[[@11,52:52='a',<459>,1:52]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={shifted=[[@16,61:67='shifted',<459>,1:61]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={shifted=[[@16,61:67='shifted',<459>,1:61]]}, table_dictionary={events={<increment column>=[[@5,20:20='a',<459>,1:20]], ts=[[@11,52:52='a',<459>,1:52]]}}, interface={shifted=[{substitution={name=<increment column>, type=column}, table_ref=a}, {name=<extra>, type=predicand}, {name=ts, table_ref=a}]}, table_alias={a=events}}}", extractor.getSymbolTable().toString());
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
		Assert.assertEquals("Table Dictionary is wrong", "{events={part=[[@5,22:22='a',<459>,1:22]], ts=[[@11,33:33='a',<459>,1:33]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={shifted=[[@16,42:48='shifted',<459>,1:42]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={shifted=[[@16,42:48='shifted',<459>,1:42]]}, table_dictionary={events={part=[[@5,22:22='a',<459>,1:22]], ts=[[@11,33:33='a',<459>,1:33]]}}, interface={shifted=[{name=part, table_ref=a}, {name=ts, table_ref=a}]}, table_alias={a=events}}}", extractor.getSymbolTable().toString());
	}

	@Test
	public void diffFamilySetGWherePlacementV0Test() {
		final String query = "select a.id from events a where datediff(day, a.created_at, a.resolved_at) > 0";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=id, table_ref=a}}}, from={table={alias=a, table=events}}, where={condition={left={datetime_function={function_name=datediff, level={canonical=DAY, text=day, form=KEYWORD}, left={column={name=created_at, table_ref=a}}, right={column={name=resolved_at, table_ref=a}}}}, right={literal=0}, operator=>}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[id]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{events={resolved_at=[[@16,60:60='a',<459>,1:60]], created_at=[[@12,46:46='a',<459>,1:46]], id=[[@1,7:7='a',<459>,1:7]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={id=[[@3,9:10='id',<459>,1:9]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={resolved_at=[[@16,60:60='a',<459>,1:60]], created_at=[[@12,46:46='a',<459>,1:46]], id=[[@1,7:7='a',<459>,1:7]]}}, filters=[{name=created_at, table_ref=a}, {name=resolved_at, table_ref=a}], interface={id=[{name=id, table_ref=a}]}, table_alias={a=events}}}", extractor.getSymbolTable().toString());
	}

	@Test
	public void diffFamilySetGHavingPlacementV0Test() {
		final String query = "select a.account_id, count(*) as cnt from events a group by a.account_id having datediff(day, min(a.created_at), max(a.resolved_at)) > 0";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=account_id, table_ref=a}}, 2={function={function_name=COUNT, qualifier=null, parameters=*}, alias=cnt}}, having={condition={left={datetime_function={function_name=datediff, level={canonical=DAY, text=day, form=KEYWORD}, left={function={function_name=min, qualifier=null, parameters={column={name=created_at, table_ref=a}}}}, right={function={function_name=max, qualifier=null, parameters={column={name=resolved_at, table_ref=a}}}}}}, right={literal=0}, operator=>}}, from={table={alias=a, table=events}}, groupby={1={column={name=account_id, table_ref=a}}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[account_id, cnt]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{events={account_id=[[@1,7:7='a',<459>,1:7], [@16,60:60='a',<459>,1:60]], resolved_at=[[@33,117:117='a',<459>,1:117]], created_at=[[@26,98:98='a',<459>,1:98]], *=[[@7,27:27='*',<291>,1:27]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={cnt=[[@10,33:35='cnt',<459>,1:33]], account_id=[[@3,9:18='account_id',<459>,1:9]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={account_id=[[@3,9:18='account_id',<459>,1:9]], cnt=[[@10,33:35='cnt',<459>,1:33]]}, table_dictionary={events={account_id=[[@1,7:7='a',<459>,1:7], [@16,60:60='a',<459>,1:60]], resolved_at=[[@33,117:117='a',<459>,1:117]], created_at=[[@26,98:98='a',<459>,1:98]], *=[[@7,27:27='*',<291>,1:27]]}}, grouped_by=[{name=account_id, table_ref=a}], filters=[{name=created_at, table_ref=a}, {name=resolved_at, table_ref=a}], interface={account_id=[{name=account_id, table_ref=a}], cnt=[]}, table_alias={a=events}}}", extractor.getSymbolTable().toString());
	}

	@Test
	public void diffFamilySetGOrderByExpressionAndAliasPlacementV0Test() {
		final String query = "select datediff(day, a.created_at, a.resolved_at) as delta_days, a.id from events a order by datediff(day, a.started_at, a.ended_at) desc, delta_days asc";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=delta_days, datetime_function={function_name=datediff, level={canonical=DAY, text=day, form=KEYWORD}, left={column={name=created_at, table_ref=a}}, right={column={name=resolved_at, table_ref=a}}}}, 2={column={name=id, table_ref=a}}}, orderby={1={null_order=null, predicand={datetime_function={function_name=datediff, level={canonical=DAY, text=day, form=KEYWORD}, left={column={name=started_at, table_ref=a}}, right={column={name=ended_at, table_ref=a}}}}, sort_order=desc}, 2={null_order=null, predicand={column={name=delta_days, table_ref=null}}, sort_order=asc}}, from={table={alias=a, table=events}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[id, delta_days]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{events={resolved_at=[[@9,35:35='a',<459>,1:35]], created_at=[[@5,21:21='a',<459>,1:21]], started_at=[[@28,107:107='a',<459>,1:107]], id=[[@16,65:65='a',<459>,1:65]], ended_at=[[@32,121:121='a',<459>,1:121]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={id=[[@18,67:68='id',<459>,1:67]], delta_days=[[@14,53:62='delta_days',<459>,1:53], [@38,139:148='delta_days',<459>,1:139]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={id=[[@18,67:68='id',<459>,1:67]], delta_days=[[@14,53:62='delta_days',<459>,1:53], [@38,139:148='delta_days',<459>,1:139]]}, table_dictionary={events={resolved_at=[[@9,35:35='a',<459>,1:35]], created_at=[[@5,21:21='a',<459>,1:21]], started_at=[[@28,107:107='a',<459>,1:107]], id=[[@16,65:65='a',<459>,1:65]], ended_at=[[@32,121:121='a',<459>,1:121]]}}, ordered_by=[{name=started_at, table_ref=a}, {name=ended_at, table_ref=a}, {name=delta_days, table_ref=query0}], interface={id=[{name=id, table_ref=a}], delta_days=[{name=created_at, table_ref=a}, {name=resolved_at, table_ref=a}]}, table_alias={a=events}}}", extractor.getSymbolTable().toString());
	}

	@Test
	public void diffFamilySetGWindowPartitionOrderPlacementV0Test() {
		final String query = "select row_number() over (partition by datediff(day, a.created_at, a.resolved_at) order by datediff(hour, a.started_at, a.ended_at)) as rn from events a";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=rn, window_function={over={partition_by={1={datetime_function={function_name=datediff, level={canonical=DAY, text=day, form=KEYWORD}, left={column={name=created_at, table_ref=a}}, right={column={name=resolved_at, table_ref=a}}}}}, orderby={1={null_order=null, predicand={datetime_function={function_name=datediff, level={canonical=HOUR, text=hour, form=KEYWORD}, left={column={name=started_at, table_ref=a}}, right={column={name=ended_at, table_ref=a}}}}, sort_order=ASC}}}, function={function_name=row_number, parameters=null}}}}, from={table={alias=a, table=events}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[rn]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{events={resolved_at=[[@16,67:67='a',<459>,1:67]], created_at=[[@12,53:53='a',<459>,1:53]], started_at=[[@26,106:106='a',<459>,1:106]], ended_at=[[@30,120:120='a',<459>,1:120]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={rn=[[@36,136:137='rn',<459>,1:136]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={window_ordered_by=[{name=started_at, table_ref=a}, {name=ended_at, table_ref=a}], query_dictionary={rn=[[@36,136:137='rn',<459>,1:136]]}, table_dictionary={events={resolved_at=[[@16,67:67='a',<459>,1:67]], created_at=[[@12,53:53='a',<459>,1:53]], started_at=[[@26,106:106='a',<459>,1:106]], ended_at=[[@30,120:120='a',<459>,1:120]]}}, window_partition_by=[{name=created_at, table_ref=a}, {name=resolved_at, table_ref=a}], interface={rn=[{name=created_at, table_ref=a}, {name=resolved_at, table_ref=a}, {name=started_at, table_ref=a}, {name=ended_at, table_ref=a}]}, table_alias={a=events}}}", extractor.getSymbolTable().toString());
	}

	@Test
	public void diffFamilySetGInsertSelectPlacementV0Test() {
		final String query = "insert into archive_events (delta_days, event_id) select datediff(day, a.created_at, a.resolved_at) as delta_days, a.id as event_id from events a";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={insert={preamble=insert_into, from={from={table={alias=a, table=events}}, select={1={alias=delta_days, datetime_function={function_name=datediff, level={canonical=DAY, text=day, form=KEYWORD}, left={column={name=created_at, table_ref=a}}, right={column={name=resolved_at, table_ref=a}}}}, 2={column={name=id, table_ref=a}, alias=event_id}}}, target_table={table={alias=null, table=archive_events}}, columns={1={column={name=delta_days, table_ref=null}}, 2={column={name=event_id, table_ref=null}}}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[event_id, delta_days]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{archive_events={event_id=[[@6,40:47='event_id',<459>,1:40]], delta_days=[[@4,28:37='delta_days',<459>,1:28]]}, events={resolved_at=[[@17,85:85='a',<459>,1:85]], created_at=[[@13,71:71='a',<459>,1:71]], id=[[@24,115:115='a',<459>,1:115]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={event_id=[[@28,123:130='event_id',<459>,1:123]], delta_days=[[@22,103:112='delta_days',<459>,1:103]]}, insert1={event_id=[[@6,40:47='event_id',<459>,1:40]], delta_days=[[@4,28:37='delta_days',<459>,1:28]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_insert1={query_dictionary={event_id=[[@6,40:47='event_id',<459>,1:40]], delta_days=[[@4,28:37='delta_days',<459>,1:28]]}, table_dictionary={archive_events={event_id=[[@6,40:47='event_id',<459>,1:40]], delta_days=[[@4,28:37='delta_days',<459>,1:28]]}}, def_query0={query_dictionary={event_id=[[@28,123:130='event_id',<459>,1:123]], delta_days=[[@22,103:112='delta_days',<459>,1:103]]}, table_dictionary={events={resolved_at=[[@17,85:85='a',<459>,1:85]], created_at=[[@13,71:71='a',<459>,1:71]], id=[[@24,115:115='a',<459>,1:115]]}}, interface={event_id=[{name=id, table_ref=a}], delta_days=[{name=created_at, table_ref=a}, {name=resolved_at, table_ref=a}]}, table_alias={a=events}}, interface={delta_days=[{name=delta_days, table_ref=query0}], event_id=[{name=event_id, table_ref=query0}]}}}", extractor.getSymbolTable().toString());
	}

	@Test
	public void diffFamilySetGUpdateSetAndWherePlacementV0Test() {
		final String query = "update events e set offset_days = datediff(day, e.created_at, e.resolved_at) where datediff(day, e.started_at, e.ended_at) > 0";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={update={table={alias=e, table=events}, where={condition={left={datetime_function={function_name=datediff, level={canonical=DAY, text=day, form=KEYWORD}, left={column={name=started_at, table_ref=e}}, right={column={name=ended_at, table_ref=e}}}}, right={literal=0}, operator=>}}, assignments={1={set={column={name=offset_days, table_ref=null}}, to={datetime_function={function_name=datediff, level={canonical=DAY, text=day, form=KEYWORD}, left={column={name=created_at, table_ref=e}}, right={column={name=resolved_at, table_ref=e}}}}}}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[offset_days]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{events={started_at=[[@23,97:97='e',<459>,1:97]], ended_at=[[@27,111:111='e',<459>,1:111]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{update0={offset_days=[[@4,20:30='offset_days',<459>,1:20]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_update0={assignments={offset_days=[{name=created_at, table_ref=e}, {name=resolved_at, table_ref=e}]}, table_dictionary={events={started_at=[[@23,97:97='e',<459>,1:97]], ended_at=[[@27,111:111='e',<459>,1:111]]}}, update_dictionary={offset_days=[[@4,20:30='offset_days',<459>,1:20]]}, target_table={events={resolved_at=[[@14,62:62='e',<459>,1:62]], created_at=[[@10,48:48='e',<459>,1:48]], offset_days=[[@4,20:30='offset_days',<459>,1:20]]}}, filters=[{name=started_at, table_ref=e}, {name=ended_at, table_ref=e}], table_alias={e=events}, lhs_unresolved_columns={offset_days={column={name=offset_days, table_ref=null}, locations=[[@4,20:30='offset_days',<459>,1:20]]}}}}", extractor.getSymbolTable().toString());
	}

	@Test
	public void diffFamilySetGDeleteWhereAndExistsPlacementV0Test() {
		final String query = "delete from events e where datediff(day, e.created_at, e.resolved_at) > 0 and exists (select 1 from adjustments a where datediff(day, a.created_at, a.updated_at) > 0)";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={delete={table={alias=e, table=events}, where={and={1={condition={left={datetime_function={function_name=datediff, level={canonical=DAY, text=day, form=KEYWORD}, left={column={name=created_at, table_ref=e}}, right={column={name=resolved_at, table_ref=e}}}}, right={literal=0}, operator=>}}, 2={exists={select={1={literal=1}}, from={table={alias=a, table=adjustments}}, where={condition={left={datetime_function={function_name=datediff, level={canonical=DAY, text=day, form=KEYWORD}, left={column={name=created_at, table_ref=a}}, right={column={name=updated_at, table_ref=a}}}}, right={literal=0}, operator=>}}, operator=exists}}}}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{adjustments={updated_at=[[@36,148:148='a',<459>,1:148]], created_at=[[@32,134:134='a',<459>,1:134]]}, events={resolved_at=[[@13,55:55='e',<459>,1:55]], created_at=[[@9,41:41='e',<459>,1:41]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={unnamed_0=[[@23,93:93='1',<300>,1:93]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_delete2={query_dictionary={}, table_dictionary={events={resolved_at=[[@13,55:55='e',<459>,1:55]], created_at=[[@9,41:41='e',<459>,1:41]]}}, dependent_queries={exists1={query=query0, type=filters}}, def_query0={query_dictionary={unnamed_0=[[@23,93:93='1',<300>,1:93]]}, table_dictionary={adjustments={updated_at=[[@36,148:148='a',<459>,1:148]], created_at=[[@32,134:134='a',<459>,1:134]]}}, filters=[{name=created_at, table_ref=a}, {name=updated_at, table_ref=a}], interface={unnamed_0=[]}, table_alias={a=adjustments}}, filters=[{name=created_at, table_ref=e}, {name=resolved_at, table_ref=e}], interface=null, table_alias={e=events}}}", extractor.getSymbolTable().toString());
	}

	@Test
	public void diffFamilySetGSetOpBranchIsolationV0Test() {
		final String query = "select datediff(day, a.created_at, a.resolved_at) as delta_days, a.id from events a union all select datediff(day, b.created_at, b.resolved_at) as delta_days, b.id from events_archive b";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={union={1={select={1={alias=delta_days, datetime_function={function_name=datediff, level={canonical=DAY, text=day, form=KEYWORD}, left={column={name=created_at, table_ref=a}}, right={column={name=resolved_at, table_ref=a}}}}, 2={column={name=id, table_ref=a}}}, from={table={alias=a, table=events}}}, 2={union={qualifier=all, operator=union}}, 3={select={1={alias=delta_days, datetime_function={function_name=datediff, level={canonical=DAY, text=day, form=KEYWORD}, left={column={name=created_at, table_ref=b}}, right={column={name=resolved_at, table_ref=b}}}}, 2={column={name=id, table_ref=b}}}, from={table={alias=b, table=events_archive}}}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[id, delta_days]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{events_archive={resolved_at=[[@33,129:129='b',<459>,1:129]], created_at=[[@29,115:115='b',<459>,1:115]], id=[[@40,159:159='b',<459>,1:159]]}, events={resolved_at=[[@9,35:35='a',<459>,1:35]], created_at=[[@5,21:21='a',<459>,1:21]], id=[[@16,65:65='a',<459>,1:65]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={id=[[@18,67:68='id',<459>,1:67]], delta_days=[[@14,53:62='delta_days',<459>,1:53]]}, query1={id=[[@42,161:162='id',<459>,1:161]], delta_days=[[@38,147:156='delta_days',<459>,1:147]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_union2={def_query1={query_dictionary={id=[[@42,161:162='id',<459>,1:161]], delta_days=[[@38,147:156='delta_days',<459>,1:147]]}, table_dictionary={events_archive={resolved_at=[[@33,129:129='b',<459>,1:129]], created_at=[[@29,115:115='b',<459>,1:115]], id=[[@40,159:159='b',<459>,1:159]]}}, setop=UNION, interface={id=[{name=id, table_ref=b}], delta_days=[{name=created_at, table_ref=b}, {name=resolved_at, table_ref=b}]}, table_alias={b=events_archive}}, def_query0={query_dictionary={id=[[@18,67:68='id',<459>,1:67]], delta_days=[[@14,53:62='delta_days',<459>,1:53]]}, table_dictionary={events={resolved_at=[[@9,35:35='a',<459>,1:35]], created_at=[[@5,21:21='a',<459>,1:21]], id=[[@16,65:65='a',<459>,1:65]]}}, interface={id=[{name=id, table_ref=a}], delta_days=[{name=created_at, table_ref=a}, {name=resolved_at, table_ref=a}]}, table_alias={a=events}}, interface={id=query_column, delta_days=query_column}}}", extractor.getSymbolTable().toString());
	}

	@Test
	public void diffFamilySetHQualifiedSameNameUdfRemainsGenericV0Test() {
		final String query = "select datediff(day, a.created_at, a.resolved_at) as built_in_delta, custom.datediff(a.part, a.created_at, a.resolved_at) as udf_delta from events a";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=built_in_delta, datetime_function={function_name=datediff, level={canonical=DAY, text=day, form=KEYWORD}, left={column={name=created_at, table_ref=a}}, right={column={name=resolved_at, table_ref=a}}}}, 2={function={schema=custom, parameters={1={column={name=part, table_ref=a}}, 2={column={name=created_at, table_ref=a}}, 3={column={name=resolved_at, table_ref=a}}}, function_name=datediff}, alias=udf_delta}}, from={table={alias=a, table=events}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[udf_delta, built_in_delta]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{events={resolved_at=[[@9,35:35='a',<459>,1:35], [@28,107:107='a',<459>,1:107]], part=[[@20,85:85='a',<459>,1:85]], created_at=[[@5,21:21='a',<459>,1:21], [@24,93:93='a',<459>,1:93]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={udf_delta=[[@33,125:133='udf_delta',<459>,1:125]], built_in_delta=[[@14,53:66='built_in_delta',<459>,1:53]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={udf_delta=[[@33,125:133='udf_delta',<459>,1:125]], built_in_delta=[[@14,53:66='built_in_delta',<459>,1:53]]}, table_dictionary={events={resolved_at=[[@9,35:35='a',<459>,1:35], [@28,107:107='a',<459>,1:107]], part=[[@20,85:85='a',<459>,1:85]], created_at=[[@5,21:21='a',<459>,1:21], [@24,93:93='a',<459>,1:93]]}}, interface={udf_delta=[{name=part, table_ref=a}, {name=created_at, table_ref=a}, {name=resolved_at, table_ref=a}], built_in_delta=[{name=created_at, table_ref=a}, {name=resolved_at, table_ref=a}]}, table_alias={a=events}}}", extractor.getSymbolTable().toString());
	}

	@Test
	public void diffFamilySetHScriptResetsSharedAliasAffinityPerStatementV0Test() {
		final String script = "select datediff(day, a.created_at, a.resolved_at) as delta_days from events a; select extract(dow from b.created_at) as dow_part from events b";
		final SQLSelectParserParser parser = parse(script);
		SqlParseEventWalker extractor = runScriptParsertest(script, parser);
		Assert.assertEquals(script, 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals(0, extractor.getSnippet().getFatalErrorCount());
		Assert.assertEquals(0, extractor.getSnowflakeDialectGrammarCount());
		Assert.assertEquals(0, extractor.getPostgresDialectGrammarCount());
	}
}
