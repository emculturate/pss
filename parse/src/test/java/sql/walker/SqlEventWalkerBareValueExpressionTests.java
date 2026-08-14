package sql.walker;

import org.junit.Assert;
import org.junit.Test;

import access.Snippet;
import errorhandling.ParseDiagnostic;
import sql.SQLSelectParserParser;

/**
 * Bare ANSI/dialect value expressions ({@code CURRENT_TIMESTAMP}, {@code SESSION_USER}, …)
 * must not participate in unresolved-column recognition or bind as physical columns.
 * Covers SELECT clauses (including join ON/USING, QUALIFY, window OVER), DML (UPDATE/INSERT/DELETE),
 * RETURNING, and the outer query of WITH (CTE bodies use only ordinary columns).
 */
public class SqlEventWalkerBareValueExpressionTests extends AbstractSqlParseEventWalkerTest {

	private void assertBareValueHappyPathGoldens(SqlParseEventWalker extractor) {
		assertNoWalkerDiagnostics(extractor);
	}

	@Test
	public void bareValueExpressionSingleTableAllClausesV0Test() {
		final String query = "SELECT t.col1,"
				+ " ROW_NUMBER() OVER (PARTITION BY CURRENT_DATE ORDER BY CURRENT_TIME) AS rn,"
				+ " CURRENT_TIMESTAMP AS ts_ref, t.col2"
				+ " FROM tab1 t"
				+ " WHERE t.col1 = CURRENT_USER AND CURRENT_TIMESTAMP > LOCALTIME"
				+ " GROUP BY t.col1, LOCALTIMESTAMP, CURRENT_USER"
				+ " HAVING COUNT(*) > 0 AND CURRENT_DATE IS NOT NULL"
				+ " ORDER BY CURRENT_TIMESTAMP, t.col2";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertBareValueHappyPathGoldens(extractor);

		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=col1, table_ref=t}}, 2={alias=rn, window_function={over={partition_by={1={column={name=CURRENT_DATE, table_ref=null}}}, orderby={1={null_order=null, predicand={column={name=CURRENT_TIME, table_ref=null}}, sort_order=ASC}}}, function={function_name=ROW_NUMBER, parameters=null}}}, 3={column={name=CURRENT_TIMESTAMP, table_ref=null}, alias=ts_ref}, 4={column={name=col2, table_ref=t}}}, having={and={1={condition={left={function={function_name=COUNT, qualifier=null, parameters=*}}, right={literal=0}, operator=>}}, 2={condition={left={column={name=CURRENT_DATE, table_ref=null}}, operator=IS NOT NULL}}}}, orderby={1={null_order=null, predicand={column={name=CURRENT_TIMESTAMP, table_ref=null}}, sort_order=ASC}, 2={null_order=null, predicand={column={name=col2, table_ref=t}}, sort_order=ASC}}, from={table={alias=t, table=tab1}}, where={and={1={condition={left={column={name=col1, table_ref=t}}, right={column={name=CURRENT_USER, table_ref=null}}, operator==}}, 2={condition={left={column={name=CURRENT_TIMESTAMP, table_ref=null}}, right={column={name=LOCALTIME, table_ref=null}}, operator=>}}}}, groupby={1={column={name=col1, table_ref=t}}, 2={column={name=LOCALTIMESTAMP, table_ref=null}}, 3={column={name=CURRENT_USER, table_ref=null}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[ts_ref, rn, col2, col1]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{tab1={*=[[@52,259:259='*',<291>,1:259]], col2=[[@24,119:119='t',<392>,1:119], [@65,323:323='t',<392>,1:323]], col1=[[@1,7:7='t',<392>,1:7], [@31,144:144='t',<392>,1:144], [@42,209:209='t',<392>,1:209]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{query0={ts_ref=[[@22,111:116='ts_ref',<392>,1:111]], rn=[[@18,86:87='rn',<392>,1:86]], col2=[[@26,121:124='col2',<392>,1:121]], col1=[[@3,9:12='col1',<392>,1:9]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query0={window_ordered_by=[{name=CURRENT_TIME, table_ref=null}], query_dictionary={ts_ref=[[@22,111:116='ts_ref',<392>,1:111]], rn=[[@18,86:87='rn',<392>,1:86]], col2=[[@26,121:124='col2',<392>,1:121]], col1=[[@3,9:12='col1',<392>,1:9]]}, table_dictionary={tab1={*=[[@52,259:259='*',<291>,1:259]], col2=[[@24,119:119='t',<392>,1:119], [@65,323:323='t',<392>,1:323]], col1=[[@1,7:7='t',<392>,1:7], [@31,144:144='t',<392>,1:144], [@42,209:209='t',<392>,1:209]]}}, grouped_by=[{name=col1, table_ref=t}, {name=LOCALTIMESTAMP, table_ref=null}, {name=CURRENT_USER, table_ref=null}], window_partition_by=[{name=CURRENT_DATE, table_ref=null}], ordered_by=[{name=CURRENT_TIMESTAMP, table_ref=null}, {name=col2, table_ref=t}], filters=[{name=col1, table_ref=t}, {name=CURRENT_USER, table_ref=null}, {name=CURRENT_TIMESTAMP, table_ref=null}, {name=LOCALTIME, table_ref=null}, {name=CURRENT_DATE, table_ref=null}], interface={ts_ref=[{name=CURRENT_TIMESTAMP, table_ref=null}], rn=[{name=CURRENT_DATE, table_ref=null}, {name=CURRENT_TIME, table_ref=null}], col2=[{name=col2, table_ref=t}], col1=[{name=col1, table_ref=t}]}, table_alias={t=tab1}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void bareValueExpressionSubqueryFromAllClausesV0Test() {
		final String query = "SELECT sub.outer_col, CURRENT_DATE, sub.rn"
				+ " FROM ( SELECT t.col1 AS outer_col,"
				+ " ROW_NUMBER() OVER (PARTITION BY CURRENT_DATE ORDER BY CURRENT_TIME) AS rn,"
				+ " CURRENT_TIMESTAMP"
				+ " FROM tab1 t"
				+ " WHERE t.col1 = CURRENT_USER AND LOCALTIME IS NOT NULL"
				+ " GROUP BY t.col1, LOCALTIMESTAMP"
				+ " HAVING COUNT(*) > 0 AND CURRENT_DATE IS NOT NULL"
				+ " ORDER BY CURRENT_TIMESTAMP ) sub"
				+ " WHERE sub.outer_col IS NOT NULL AND CURRENT_USER = CURRENT_TIMESTAMP"
				+ " ORDER BY CURRENT_DATE";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertBareValueHappyPathGoldens(extractor);

		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=outer_col, table_ref=sub}}, 2={column={name=CURRENT_DATE, table_ref=null}}, 3={column={name=rn, table_ref=sub}}}, orderby={1={null_order=null, predicand={column={name=CURRENT_DATE, table_ref=null}}, sort_order=ASC}}, from={table={alias=sub, query={select={1={column={name=col1, table_ref=t}, alias=outer_col}, 2={alias=rn, window_function={over={partition_by={1={column={name=CURRENT_DATE, table_ref=null}}}, orderby={1={null_order=null, predicand={column={name=CURRENT_TIME, table_ref=null}}, sort_order=ASC}}}, function={function_name=ROW_NUMBER, parameters=null}}}, 3={column={name=CURRENT_TIMESTAMP, table_ref=null}}}, having={and={1={condition={left={function={function_name=COUNT, qualifier=null, parameters=*}}, right={literal=0}, operator=>}}, 2={condition={left={column={name=CURRENT_DATE, table_ref=null}}, operator=IS NOT NULL}}}}, orderby={1={null_order=null, predicand={column={name=CURRENT_TIMESTAMP, table_ref=null}}, sort_order=ASC}}, from={table={alias=t, table=tab1}}, where={and={1={condition={left={column={name=col1, table_ref=t}}, right={column={name=CURRENT_USER, table_ref=null}}, operator==}}, 2={condition={left={column={name=LOCALTIME, table_ref=null}}, operator=IS NOT NULL}}}}, groupby={1={column={name=col1, table_ref=t}}, 2={column={name=LOCALTIMESTAMP, table_ref=null}}}}}}, where={and={1={condition={left={column={name=outer_col, table_ref=sub}}, operator=IS NOT NULL}}, 2={condition={left={column={name=CURRENT_USER, table_ref=null}}, right={column={name=CURRENT_TIMESTAMP, table_ref=null}}, operator==}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[outer_col, CURRENT_DATE, rn]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{tab1={*=[[@59,282:282='*',<291>,1:282]], col1=[[@13,57:57='t',<392>,1:57], [@39,189:189='t',<392>,1:189], [@51,246:246='t',<392>,1:246]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{query0={outer_col=[[@17,67:75='outer_col',<392>,1:67], [@1,7:9='sub',<392>,1:7], [@74,357:359='sub',<392>,1:357]], CURRENT_TIMESTAMP=[[@34,153:169='CURRENT_TIMESTAMP',<392>,1:153]], rn=[[@32,149:150='rn',<392>,1:149], [@7,36:38='sub',<392>,1:36]]}, query1={outer_col=[[@3,11:19='outer_col',<392>,1:11]], CURRENT_DATE=[[@5,22:33='CURRENT_DATE',<392>,1:22]], rn=[[@9,40:41='rn',<392>,1:40]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query1={query_dictionary={outer_col=[[@3,11:19='outer_col',<392>,1:11]], CURRENT_DATE=[[@5,22:33='CURRENT_DATE',<392>,1:22]], rn=[[@9,40:41='rn',<392>,1:40]]}, def_query0={window_ordered_by=[{name=CURRENT_TIME, table_ref=null}], query_dictionary={CURRENT_TIMESTAMP=[[@34,153:169='CURRENT_TIMESTAMP',<392>,1:153]], outer_col=[[@17,67:75='outer_col',<392>,1:67], [@1,7:9='sub',<392>,1:7], [@74,357:359='sub',<392>,1:357]], rn=[[@32,149:150='rn',<392>,1:149], [@7,36:38='sub',<392>,1:36]]}, table_dictionary={tab1={*=[[@59,282:282='*',<291>,1:282]], col1=[[@13,57:57='t',<392>,1:57], [@39,189:189='t',<392>,1:189], [@51,246:246='t',<392>,1:246]]}}, grouped_by=[{name=col1, table_ref=t}, {name=LOCALTIMESTAMP, table_ref=null}], window_partition_by=[{name=CURRENT_DATE, table_ref=null}], ordered_by=[{name=CURRENT_TIMESTAMP, table_ref=null}], filters=[{name=col1, table_ref=t}, {name=CURRENT_USER, table_ref=null}, {name=LOCALTIME, table_ref=null}, {name=CURRENT_DATE, table_ref=null}], interface={CURRENT_TIMESTAMP=[{name=CURRENT_TIMESTAMP, table_ref=null}], outer_col=[{name=col1, table_ref=t}], rn=[{name=CURRENT_DATE, table_ref=null}, {name=CURRENT_TIME, table_ref=null}]}, table_alias={t=tab1}}, ordered_by=[{name=CURRENT_DATE, table_ref=null}], filters=[{name=outer_col, table_ref=sub}, {name=CURRENT_USER, table_ref=null}, {name=CURRENT_TIMESTAMP, table_ref=null}], interface={outer_col=[{name=outer_col, table_ref=sub}], CURRENT_DATE=[{name=CURRENT_DATE, table_ref=null}], rn=[{name=rn, table_ref=sub}]}, table_alias={sub=query0}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void bareValueExpressionWithCteInnerAndOuterV0Test() {
		final String query = "WITH inner_cte AS ("
				+ " SELECT col1, CURRENT_TIMESTAMP FROM tab1 WHERE CURRENT_DATE = col1"
				+ " ) SELECT c.col1, CURRENT_USER FROM inner_cte c"
				+ " WHERE CURRENT_TIMESTAMP IS NOT NULL ORDER BY LOCALTIME";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertBareValueHappyPathGoldens(extractor);

		Assert.assertEquals("AST is wrong",
				"{SQL={with={1={cte={select={1={column={name=col1, table_ref=null}}, 2={column={name=CURRENT_TIMESTAMP, table_ref=null}}}, from={table={alias=null, table=tab1}}, where={condition={left={column={name=CURRENT_DATE, table_ref=null}}, right={column={name=col1, table_ref=null}}, operator==}}}, alias=inner_cte}}, query={select={1={column={name=col1, table_ref=c}}, 2={column={name=CURRENT_USER, table_ref=null}}}, orderby={1={null_order=null, predicand={column={name=LOCALTIME, table_ref=null}}, sort_order=ASC}}, from={table={alias=c, table=inner_cte}}, where={condition={left={column={name=CURRENT_TIMESTAMP, table_ref=null}}, operator=IS NOT NULL}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[CURRENT_USER, col1]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{tab1={col1=[[@5,27:30='col1',<392>,1:27], [@13,82:85='col1',<392>,1:82]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{query0={CURRENT_TIMESTAMP=[[@7,33:49='CURRENT_TIMESTAMP',<392>,1:33]], col1=[[@5,27:30='col1',<392>,1:27], [@16,96:96='c',<392>,1:96]]}, query1={CURRENT_USER=[[@20,104:115='CURRENT_USER',<392>,1:104]], col1=[[@18,98:101='col1',<392>,1:98]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query1={context_list={inner_cte=query0, c=query0}, query_dictionary={CURRENT_USER=[[@20,104:115='CURRENT_USER',<392>,1:104]], col1=[[@18,98:101='col1',<392>,1:98]]}, def_query0={query_dictionary={CURRENT_TIMESTAMP=[[@7,33:49='CURRENT_TIMESTAMP',<392>,1:33]], col1=[[@5,27:30='col1',<392>,1:27], [@16,96:96='c',<392>,1:96]]}, table_dictionary={tab1={col1=[[@5,27:30='col1',<392>,1:27], [@13,82:85='col1',<392>,1:82]]}}, filters=[{name=CURRENT_DATE, table_ref=null}, {name=col1, table_ref=tab1}], interface={CURRENT_TIMESTAMP=[{name=CURRENT_TIMESTAMP, table_ref=null}], col1=[{name=col1, table_ref=tab1}]}}, ordered_by=[{name=LOCALTIME, table_ref=null}], filters=[{name=CURRENT_TIMESTAMP, table_ref=null}], interface={CURRENT_USER=[{name=CURRENT_USER, table_ref=null}], col1=[{name=col1, table_ref=c}]}, table_alias={c=query0, inner_cte=query0}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void bareValueExpressionWithCteWindowAndOuterV1Test() {
		final String query = "WITH ranked AS ("
				+ " SELECT col1, ROW_NUMBER() OVER (PARTITION BY CURRENT_DATE ORDER BY CURRENT_TIME) AS rn"
				+ " FROM tab1 WHERE CURRENT_USER IS NOT NULL"
				+ " ) SELECT r.col1, r.rn, CURRENT_TIMESTAMP FROM ranked r"
				+ " WHERE LOCALTIMESTAMP IS NOT NULL ORDER BY CURRENT_DATE";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertBareValueHappyPathGoldens(extractor);

		Assert.assertEquals("AST is wrong",
				"{SQL={with={1={cte={select={1={column={name=col1, table_ref=null}}, 2={alias=rn, window_function={over={partition_by={1={column={name=CURRENT_DATE, table_ref=null}}}, orderby={1={null_order=null, predicand={column={name=CURRENT_TIME, table_ref=null}}, sort_order=ASC}}}, function={function_name=ROW_NUMBER, parameters=null}}}}, from={table={alias=null, table=tab1}}, where={condition={left={column={name=CURRENT_USER, table_ref=null}}, operator=IS NOT NULL}}}, alias=ranked}}, query={select={1={column={name=col1, table_ref=r}}, 2={column={name=rn, table_ref=r}}, 3={column={name=CURRENT_TIMESTAMP, table_ref=null}}}, orderby={1={null_order=null, predicand={column={name=CURRENT_DATE, table_ref=null}}, sort_order=ASC}}, from={table={alias=r, table=ranked}}, where={condition={left={column={name=LOCALTIMESTAMP, table_ref=null}}, operator=IS NOT NULL}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[CURRENT_TIMESTAMP, rn, col1]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{tab1={col1=[[@5,24:27='col1',<392>,1:24]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{query0={rn=[[@20,101:102='rn',<392>,1:101], [@34,162:162='r',<392>,1:162]], col1=[[@5,24:27='col1',<392>,1:24], [@30,154:154='r',<392>,1:154]]}, query1={CURRENT_TIMESTAMP=[[@38,168:184='CURRENT_TIMESTAMP',<392>,1:168]], rn=[[@36,164:165='rn',<392>,1:164]], col1=[[@32,156:159='col1',<392>,1:156]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query1={context_list={ranked=query0, r=query0}, query_dictionary={CURRENT_TIMESTAMP=[[@38,168:184='CURRENT_TIMESTAMP',<392>,1:168]], rn=[[@36,164:165='rn',<392>,1:164]], col1=[[@32,156:159='col1',<392>,1:156]]}, def_query0={window_ordered_by=[{name=CURRENT_TIME, table_ref=null}], query_dictionary={rn=[[@20,101:102='rn',<392>,1:101], [@34,162:162='r',<392>,1:162]], col1=[[@5,24:27='col1',<392>,1:24], [@30,154:154='r',<392>,1:154]]}, table_dictionary={tab1={col1=[[@5,24:27='col1',<392>,1:24]]}}, window_partition_by=[{name=CURRENT_DATE, table_ref=null}], filters=[{name=CURRENT_USER, table_ref=null}], interface={rn=[{name=CURRENT_DATE, table_ref=null}, {name=CURRENT_TIME, table_ref=null}], col1=[{name=col1, table_ref=tab1}]}}, ordered_by=[{name=CURRENT_DATE, table_ref=null}], filters=[{name=LOCALTIMESTAMP, table_ref=null}], interface={CURRENT_TIMESTAMP=[{name=CURRENT_TIMESTAMP, table_ref=null}], rn=[{name=rn, table_ref=r}], col1=[{name=col1, table_ref=r}]}, table_alias={r=query0, ranked=query0}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void bareValueExpressionMixedSnowflakePostgresDialectV0Test() {
		final String query = "SELECT col1 FROM tab1"
				+ " WHERE CURRENT_ORGANIZATION_USER IS NOT NULL AND SESSION_USER = col1";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		Snippet snippet = extractor.getSnippet();

		Assert.assertEquals("Expected one fatal (mixed dialect)", 1,
				snippet.getFatalErrorStringList().size());
		assertDiagnosticAtPosition(
				snippet,
				"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
				ParseDiagnostic.Severity.WARNING,
				"Snowflake-specific grammar",
				null,
				1,
				28);
		assertFatalDiagnosticAtPosition(
				snippet,
				"STATEMENT_MIXED_DIALECT_GRAMMAR",
				"unlikely to run on either engine",
				null,
				1,
				70);

		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=col1, table_ref=null}}}, from={table={alias=null, table=tab1}}, where={and={1={condition={left={column={name=CURRENT_ORGANIZATION_USER, table_ref=null}}, operator=IS NOT NULL}}, 2={condition={left={column={name=SESSION_USER, table_ref=null}}, right={column={name=col1, table_ref=null}}, operator==}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[col1]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{tab1={col1=[[@1,7:10='col1',<392>,1:7], [@12,85:88='col1',<392>,1:85]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{query0={col1=[[@1,7:10='col1',<392>,1:7]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query0={query_dictionary={col1=[[@1,7:10='col1',<392>,1:7]]}, table_dictionary={tab1={col1=[[@1,7:10='col1',<392>,1:7], [@12,85:88='col1',<392>,1:85]]}}, filters=[{name=CURRENT_ORGANIZATION_USER, table_ref=null}, {name=SESSION_USER, table_ref=null}, {name=col1, table_ref=tab1}], interface={col1=[{name=col1, table_ref=tab1}]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void bareValueExpressionJoinOnClauseV0Test() {
		final String query = "SELECT a.col1, b.col2 FROM tab1 a JOIN tab2 b ON a.col1 = b.col1 AND CURRENT_TIMESTAMP >"
				+ " LOCALTIME AND CURRENT_USER = a.col2 WHERE CURRENT_DATE IS NOT NULL";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertBareValueHappyPathGoldens(extractor);

		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=col1, table_ref=a}}, 2={column={name=col2, table_ref=b}}}, from={join={1={table={alias=a, table=tab1}}, 2={join=JOIN, on={and={1={condition={left={column={name=col1, table_ref=a}}, right={column={name=col1, table_ref=b}}, operator==}}, 2={condition={left={column={name=CURRENT_TIMESTAMP, table_ref=null}}, right={column={name=LOCALTIME, table_ref=null}}, operator=>}}, 3={condition={left={column={name=CURRENT_USER, table_ref=null}}, right={column={name=col2, table_ref=a}}, operator==}}}}}, 3={table={alias=b, table=tab2}}}}, where={condition={left={column={name=CURRENT_DATE, table_ref=null}}, operator=IS NOT NULL}}}}",
				extractor.getAsTree().toString());

		Assert.assertEquals("Interface is wrong",
				"[col2, col1]",
				extractor.getInterface().toString());

		Assert.assertEquals("Substitution List is wrong",
				"{}",
				extractor.getSubstitutionsMap().toString());

		Assert.assertEquals("Table Dictionary is wrong",
				"{tab1={col2=[[@29,118:118='a',<392>,1:118]], col1=[[@1,7:7='a',<392>,1:7], [@15,49:49='a',<392>,1:49]]}, tab2={col2=[[@5,15:15='b',<392>,1:15]], col1=[[@19,58:58='b',<392>,1:58]]}}",
				extractor.getTableColumnDictionaryMap().toString());

		Assert.assertEquals("Query Column Dictionary is wrong",
				"{query0={col2=[[@7,17:20='col2',<392>,1:17]], col1=[[@3,9:12='col1',<392>,1:9]]}}",
				extractor.getQueryColumnDictionaryMap().toString());

		Assert.assertEquals("Symbol Table is wrong",
				"{def_query0={query_dictionary={col2=[[@7,17:20='col2',<392>,1:17]], col1=[[@3,9:12='col1',<392>,1:9]]}, table_dictionary={tab1={col2=[[@29,118:118='a',<392>,1:118]], col1=[[@1,7:7='a',<392>,1:7], [@15,49:49='a',<392>,1:49]]}, tab2={col2=[[@5,15:15='b',<392>,1:15]], col1=[[@19,58:58='b',<392>,1:58]]}}, filters=[{name=col1, table_ref=a}, {name=col1, table_ref=b}, {name=CURRENT_TIMESTAMP, table_ref=null}, {name=LOCALTIME, table_ref=null}, {name=CURRENT_USER, table_ref=null}, {name=col2, table_ref=a}, {name=CURRENT_DATE, table_ref=null}], interface={col2=[{name=col2, table_ref=b}], col1=[{name=col1, table_ref=a}]}, table_alias={a=tab1, b=tab2}}}",
				extractor.getSymbolTable().toString());

	}

	@Test
	public void bareValueExpressionJoinUsingClauseV0Test() {
		final String query = "SELECT a.col1, b.col2 FROM tab1 a JOIN tab2 b USING (col1) WHERE CURRENT_TIMESTAMP >"
				+ " LOCALTIME AND CURRENT_USER IS NOT NULL AND LOCALTIMESTAMP < CURRENT_DATE";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertBareValueHappyPathGoldens(extractor);

		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=col1, table_ref=a}}, 2={column={name=col2, table_ref=b}}}, from={join={1={table={alias=a, table=tab1}}, 2={using={1={column={name=col1, table_ref=null}}}, join=JOIN}, 3={table={alias=b, table=tab2}}}}, where={and={1={condition={left={column={name=CURRENT_TIMESTAMP, table_ref=null}}, right={column={name=LOCALTIME, table_ref=null}}, operator=>}}, 2={condition={left={column={name=CURRENT_USER, table_ref=null}}, operator=IS NOT NULL}}, 3={condition={left={column={name=LOCALTIMESTAMP, table_ref=null}}, right={column={name=CURRENT_DATE, table_ref=null}}, operator=<}}}}}}",
				extractor.getAsTree().toString());

		Assert.assertEquals("Interface is wrong",
				"[col2, col1]",
				extractor.getInterface().toString());

		Assert.assertEquals("Substitution List is wrong",
				"{}",
				extractor.getSubstitutionsMap().toString());

		Assert.assertEquals("Table Dictionary is wrong",
				"{tab1={col1=[[@1,7:7='a',<392>,1:7], [@16,53:56='col1',<392>,1:53]]}, tab2={col2=[[@5,15:15='b',<392>,1:15]], col1=[[@16,53:56='col1',<392>,1:53]]}}",
				extractor.getTableColumnDictionaryMap().toString());

		Assert.assertEquals("Query Column Dictionary is wrong",
				"{query0={col2=[[@7,17:20='col2',<392>,1:17]], col1=[[@3,9:12='col1',<392>,1:9]]}}",
				extractor.getQueryColumnDictionaryMap().toString());

		Assert.assertEquals("Symbol Table is wrong",
				"{def_query0={query_dictionary={col2=[[@7,17:20='col2',<392>,1:17]], col1=[[@3,9:12='col1',<392>,1:9]]}, table_dictionary={tab1={col1=[[@1,7:7='a',<392>,1:7], [@16,53:56='col1',<392>,1:53]]}, tab2={col2=[[@5,15:15='b',<392>,1:15]], col1=[[@16,53:56='col1',<392>,1:53]]}}, filters=[{name=col1, table_ref=a}, {name=col1, table_ref=b}, {name=CURRENT_TIMESTAMP, table_ref=null}, {name=LOCALTIME, table_ref=null}, {name=CURRENT_USER, table_ref=null}, {name=LOCALTIMESTAMP, table_ref=null}, {name=CURRENT_DATE, table_ref=null}], interface={col2=[{name=col2, table_ref=b}], col1=[{name=col1, table_ref=a}]}, table_alias={a=tab1, b=tab2}}}",
				extractor.getSymbolTable().toString());

	}

	private static final String BARE_VALUE_WINDOW_ALIAS_SELECT =
			"SELECT t.col1, ROW_NUMBER() OVER (PARTITION BY CURRENT_DATE ORDER BY CURRENT_TIME) AS rn"
					+ " FROM tab1 t";

	private void assertBareValueWindowAliasLaterClauseRouting(
			SqlParseEventWalker extractor,
			String clauseLabel,
			String expectedBucketFragment) {
		assertBareValueOutputAliasLaterClauseRouting(
				extractor,
				clauseLabel,
				"rn",
				expectedBucketFragment);
	}

	private void assertBareValueOutputAliasLaterClauseRouting(
			SqlParseEventWalker extractor,
			String clauseLabel,
			String outputAliasName,
			String expectedBucketFragment) {
		final String tableDictionary = extractor.getTableColumnDictionaryMap().toString();
		final String queryDictionary = extractor.getQueryColumnDictionaryMap().toString();
		final String symbolTable = extractor.getSymbolTable().toString();

		Assert.assertFalse(
				clauseLabel + " must not leak " + outputAliasName + " into table_dictionary.tab1: "
						+ tableDictionary,
				tableDictionary.matches("(?s).*tab1=\\{[^}]*" + outputAliasName + "=.*"));
		Assert.assertTrue(
				clauseLabel + " must retain " + outputAliasName + " in query_dictionary: " + queryDictionary,
				queryDictionary.contains(outputAliasName + "="));
		Assert.assertTrue(
				clauseLabel + " must stamp " + outputAliasName + " with query0 in " + expectedBucketFragment
						+ ": " + symbolTable,
				symbolTable.contains(expectedBucketFragment));
	}

	@Test
	public void bareValueExpressionWindowAliasWhereClauseV0Test() {
		final String query = BARE_VALUE_WINDOW_ALIAS_SELECT + " WHERE rn = 1 AND LOCALTIME IS NOT NULL";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertBareValueHappyPathGoldens(extractor);
		assertBareValueWindowAliasLaterClauseRouting(
				extractor,
				"WHERE",
				"filters=[{name=rn, table_ref=query0}");
	}

	@Test
	public void bareValueExpressionWindowAliasHavingClauseV0Test() {
		final String query = BARE_VALUE_WINDOW_ALIAS_SELECT
				+ " GROUP BY t.col1 HAVING rn = 1 AND CURRENT_TIMESTAMP > LOCALTIME";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertBareValueHappyPathGoldens(extractor);
		assertBareValueWindowAliasLaterClauseRouting(
				extractor,
				"HAVING",
				"filters=[{name=rn, table_ref=query0}");
	}

	@Test
	public void bareValueExpressionWindowAliasGroupByClauseV0Test() {
		final String query = BARE_VALUE_WINDOW_ALIAS_SELECT + " GROUP BY rn, t.col1";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertBareValueHappyPathGoldens(extractor);
		assertBareValueWindowAliasLaterClauseRouting(
				extractor,
				"GROUP BY",
				"grouped_by=[{name=rn, table_ref=query0}");
	}

	@Test
	public void bareValueExpressionWindowAliasOrderByClauseV0Test() {
		final String query = BARE_VALUE_WINDOW_ALIAS_SELECT + " ORDER BY rn, t.col1";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertBareValueHappyPathGoldens(extractor);
		assertBareValueWindowAliasLaterClauseRouting(
				extractor,
				"ORDER BY",
				"ordered_by=[{name=rn, table_ref=query0}");
	}

	@Test
	public void bareValueExpressionScalarAliasWhereClauseV0Test() {
		final String query = "SELECT CURRENT_TIMESTAMP AS ts_ref, t.col1 FROM tab1 t WHERE ts_ref IS NOT NULL";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertBareValueHappyPathGoldens(extractor);
		assertBareValueOutputAliasLaterClauseRouting(
				extractor,
				"scalar bare-value alias WHERE",
				"ts_ref",
				"filters=[{name=ts_ref, table_ref=query0}");
	}

	@Test
	public void bareValueExpressionChainedBareValueWindowPartitionByV0Test() {
		final String query = "SELECT CURRENT_DATE AS d,"
				+ " ROW_NUMBER() OVER (PARTITION BY d ORDER BY CURRENT_TIME) AS rn"
				+ " FROM tab1 t WHERE rn = 1";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertBareValueHappyPathGoldens(extractor);
		assertBareValueOutputAliasLaterClauseRouting(
				extractor,
				"chained bare-value PARTITION BY",
				"d",
				"window_partition_by=[{name=d, table_ref=query0}");
		assertBareValueWindowAliasLaterClauseRouting(
				extractor,
				"chained bare-value WHERE rn",
				"filters=[{name=rn, table_ref=query0}");
		Assert.assertTrue(
				"interface must route rn's PARTITION BY dep d through query0",
				extractor.getSymbolTable().toString().contains(
						"interface={d=[{name=CURRENT_DATE, table_ref=null}], rn=[{name=d, table_ref=query0}"));
	}

	@Test
	public void bareValueExpressionQualifyClauseV0Test() {
		final String query = "SELECT t.col1, ROW_NUMBER() OVER (PARTITION BY CURRENT_DATE ORDER BY CURRENT_TIME) AS rn"
				+ " FROM tab1 t WHERE LOCALTIME IS NOT NULL QUALIFY rn = 1 AND CURRENT_TIMESTAMP > LOCALTIME";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertBareValueHappyPathGoldens(extractor);

		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=col1, table_ref=t}}, 2={alias=rn, window_function={over={partition_by={1={column={name=CURRENT_DATE, table_ref=null}}}, orderby={1={null_order=null, predicand={column={name=CURRENT_TIME, table_ref=null}}, sort_order=ASC}}}, function={function_name=ROW_NUMBER, parameters=null}}}}, from={table={alias=t, table=tab1}}, where={condition={left={column={name=LOCALTIME, table_ref=null}}, operator=IS NOT NULL}}, qualify={and={1={condition={left={column={name=rn, table_ref=null}}, right={literal=1}, operator==}}, 2={condition={left={column={name=CURRENT_TIMESTAMP, table_ref=null}}, right={column={name=LOCALTIME, table_ref=null}}, operator=>}}}}}}",
				extractor.getAsTree().toString());

		Assert.assertEquals("Interface is wrong",
				"[rn, col1]",
				extractor.getInterface().toString());

		Assert.assertEquals("Substitution List is wrong",
				"{}",
				extractor.getSubstitutionsMap().toString());

		Assert.assertEquals("Table Dictionary is wrong",
				"{tab1={col1=[[@1,7:7='t',<392>,1:7]]}}",
				extractor.getTableColumnDictionaryMap().toString());

		Assert.assertEquals("Query Column Dictionary is wrong",
				"{query0={rn=[[@18,86:87='rn',<392>,1:86], [@28,137:138='rn',<392>,1:137]], col1=[[@3,9:12='col1',<392>,1:9]]}}",
				extractor.getQueryColumnDictionaryMap().toString());

		Assert.assertEquals("Symbol Table is wrong",
				"{def_query0={window_ordered_by=[{name=CURRENT_TIME, table_ref=null}], query_dictionary={rn=[[@18,86:87='rn',<392>,1:86], [@28,137:138='rn',<392>,1:137]], col1=[[@3,9:12='col1',<392>,1:9]]}, table_dictionary={tab1={col1=[[@1,7:7='t',<392>,1:7]]}}, window_partition_by=[{name=CURRENT_DATE, table_ref=null}], filters=[{name=LOCALTIME, table_ref=null}, {name=rn, table_ref=query0}, {name=CURRENT_TIMESTAMP, table_ref=null}], interface={rn=[{name=CURRENT_DATE, table_ref=null}, {name=CURRENT_TIME, table_ref=null}], col1=[{name=col1, table_ref=t}]}, table_alias={t=tab1}}}",
				extractor.getSymbolTable().toString());

	}

	@Test
	public void bareValueExpressionUpdateStatementV0Test() {
		final String query = "UPDATE employees e SET score = CURRENT_DATE FROM tab1 src WHERE e.emp_id = src.col1 AND"
				+ " CURRENT_TIMESTAMP > LOCALTIME AND CURRENT_USER = src.col2";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertBareValueHappyPathGoldens(extractor);

		Assert.assertEquals("AST is wrong",
				"{SQL={update={from={table={alias=src, table=tab1}}, where={and={1={condition={left={column={name=emp_id, table_ref=e}}, right={column={name=col1, table_ref=src}}, operator==}}, 2={condition={left={column={name=CURRENT_TIMESTAMP, table_ref=null}}, right={column={name=LOCALTIME, table_ref=null}}, operator=>}}, 3={condition={left={column={name=CURRENT_USER, table_ref=null}}, right={column={name=col2, table_ref=src}}, operator==}}}}, assignments={1={set={column={name=score, table_ref=null}}, to={column={name=CURRENT_DATE, table_ref=null}}}}, table={alias=e, table=employees}}}}",
				extractor.getAsTree().toString());

		Assert.assertEquals("Interface is wrong",
				"[score]",
				extractor.getInterface().toString());

		Assert.assertEquals("Substitution List is wrong",
				"{}",
				extractor.getSubstitutionsMap().toString());

		Assert.assertEquals("Table Dictionary is wrong",
				"{tab1={col2=[[@25,137:139='src',<392>,1:137]], CURRENT_DATE=[[@6,31:42='CURRENT_DATE',<392>,1:31]], col1=[[@15,75:77='src',<392>,1:75]]}, employees={score=[[@4,23:27='score',<392>,1:23]], emp_id=[[@11,64:64='e',<392>,1:64]]}}",
				extractor.getTableColumnDictionaryMap().toString());

		Assert.assertEquals("Query Column Dictionary is wrong",
				"{update0={score=[[@4,23:27='score',<392>,1:23]]}}",
				extractor.getQueryColumnDictionaryMap().toString());

		Assert.assertEquals("Symbol Table is wrong",
				"{def_update0={assignments={score=[{name=CURRENT_DATE, table_ref=tab1}]}, table_dictionary={tab1={CURRENT_DATE=[[@6,31:42='CURRENT_DATE',<392>,1:31]], col2=[[@25,137:139='src',<392>,1:137]], col1=[[@15,75:77='src',<392>,1:75]]}, employees={score=[[@4,23:27='score',<392>,1:23]], emp_id=[[@11,64:64='e',<392>,1:64]]}}, update_dictionary={score=[[@4,23:27='score',<392>,1:23]]}, filters=[{name=emp_id, table_ref=e}, {name=col1, table_ref=src}, {name=CURRENT_TIMESTAMP, table_ref=null}, {name=LOCALTIME, table_ref=null}, {name=CURRENT_USER, table_ref=null}, {name=col2, table_ref=src}], table_alias={e=employees, src=tab1}}}",
				extractor.getSymbolTable().toString());

	}

	@Test
	public void bareValueExpressionInsertSelectStatementV0Test() {
		final String query = "INSERT INTO employees (col1, score) SELECT t.col1, CURRENT_TIMESTAMP FROM tab1 t WHERE"
				+ " CURRENT_USER IS NOT NULL AND LOCALTIME > CURRENT_TIME AND CURRENT_DATE = t.col1";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertBareValueHappyPathGoldens(extractor);

		Assert.assertEquals("AST is wrong",
				"{SQL={insert={preamble=insert_into, from={from={table={alias=t, table=tab1}}, where={and={1={condition={left={column={name=CURRENT_USER, table_ref=null}}, operator=IS NOT NULL}}, 2={condition={left={column={name=LOCALTIME, table_ref=null}}, right={column={name=CURRENT_TIME, table_ref=null}}, operator=>}}, 3={condition={left={column={name=CURRENT_DATE, table_ref=null}}, right={column={name=col1, table_ref=t}}, operator==}}}}, select={1={column={name=col1, table_ref=t}}, 2={column={name=CURRENT_TIMESTAMP, table_ref=null}}}}, target_table={table={alias=null, table=employees}}, columns={1={column={name=col1, table_ref=null}}, 2={column={name=score, table_ref=null}}}}}}",
				extractor.getAsTree().toString());

		Assert.assertEquals("Interface is wrong",
				"[score, col1]",
				extractor.getInterface().toString());

		Assert.assertEquals("Substitution List is wrong",
				"{}",
				extractor.getSubstitutionsMap().toString());

		Assert.assertEquals("Table Dictionary is wrong",
				"{tab1={col1=[[@9,43:43='t',<392>,1:43], [@29,160:160='t',<392>,1:160]]}, employees={score=[[@6,29:33='score',<392>,1:29]], col1=[[@4,23:26='col1',<392>,1:23]]}}",
				extractor.getTableColumnDictionaryMap().toString());

		Assert.assertEquals("Query Column Dictionary is wrong",
				"{query0={CURRENT_TIMESTAMP=[[@13,51:67='CURRENT_TIMESTAMP',<392>,1:51]], col1=[[@11,45:48='col1',<392>,1:45]]}, insert1={score=[[@6,29:33='score',<392>,1:29]], col1=[[@4,23:26='col1',<392>,1:23]]}}",
				extractor.getQueryColumnDictionaryMap().toString());

		Assert.assertEquals("Symbol Table is wrong",
				"{def_insert1={query_dictionary={score=[[@6,29:33='score',<392>,1:29]], col1=[[@4,23:26='col1',<392>,1:23]]}, table_dictionary={employees={score=[[@6,29:33='score',<392>,1:29]], col1=[[@4,23:26='col1',<392>,1:23]]}}, def_query0={query_dictionary={CURRENT_TIMESTAMP=[[@13,51:67='CURRENT_TIMESTAMP',<392>,1:51]], col1=[[@11,45:48='col1',<392>,1:45]]}, table_dictionary={tab1={col1=[[@9,43:43='t',<392>,1:43], [@29,160:160='t',<392>,1:160]]}}, filters=[{name=CURRENT_USER, table_ref=null}, {name=LOCALTIME, table_ref=null}, {name=CURRENT_TIME, table_ref=null}, {name=CURRENT_DATE, table_ref=null}, {name=col1, table_ref=t}], interface={CURRENT_TIMESTAMP=[{name=CURRENT_TIMESTAMP, table_ref=null}], col1=[{name=col1, table_ref=t}]}, table_alias={t=tab1}}, interface={col1=[{name=col1, table_ref=query0}], score=[{name=CURRENT_TIMESTAMP, table_ref=query0}]}}}",
				extractor.getSymbolTable().toString());

	}

	@Test
	public void bareValueExpressionDeleteStatementV0Test() {
		final String query = "DELETE FROM employees e WHERE e.emp_id = 1 AND CURRENT_TIMESTAMP < LOCALTIMESTAMP AND"
				+ " CURRENT_USER IS NOT NULL AND CURRENT_DATE = e.score";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertBareValueHappyPathGoldens(extractor);

		Assert.assertEquals("AST is wrong",
				"{SQL={delete={table={alias=e, table=employees}, where={and={1={condition={left={column={name=emp_id, table_ref=e}}, right={literal=1}, operator==}}, 2={condition={left={column={name=CURRENT_TIMESTAMP, table_ref=null}}, right={column={name=LOCALTIMESTAMP, table_ref=null}}, operator=<}}, 3={condition={left={column={name=CURRENT_USER, table_ref=null}}, operator=IS NOT NULL}}, 4={condition={left={column={name=CURRENT_DATE, table_ref=null}}, right={column={name=score, table_ref=e}}, operator==}}}}}}}",
				extractor.getAsTree().toString());

		Assert.assertEquals("Interface is wrong",
				"[]",
				extractor.getInterface().toString());

		Assert.assertEquals("Substitution List is wrong",
				"{}",
				extractor.getSubstitutionsMap().toString());

		Assert.assertEquals("Table Dictionary is wrong",
				"{employees={score=[[@22,130:130='e',<392>,1:130]], emp_id=[[@5,30:30='e',<392>,1:30]]}}",
				extractor.getTableColumnDictionaryMap().toString());

		Assert.assertEquals("Query Column Dictionary is wrong",
				"{}",
				extractor.getQueryColumnDictionaryMap().toString());

		Assert.assertEquals("Symbol Table is wrong",
				"{def_delete0={query_dictionary={}, table_dictionary={employees={score=[[@22,130:130='e',<392>,1:130]], emp_id=[[@5,30:30='e',<392>,1:30]]}}, filters=[{name=emp_id, table_ref=e}, {name=CURRENT_TIMESTAMP, table_ref=null}, {name=LOCALTIMESTAMP, table_ref=null}, {name=CURRENT_USER, table_ref=null}, {name=CURRENT_DATE, table_ref=null}, {name=score, table_ref=e}], interface=null, table_alias={e=employees}}}",
				extractor.getSymbolTable().toString());

	}

	@Test
	public void bareValueExpressionUpdateReturningV0Test() {
		final String query = "UPDATE employees e SET score = 1 WHERE CURRENT_TIMESTAMP IS NOT NULL AND LOCALTIME <"
				+ " CURRENT_DATE RETURNING e.emp_id, CURRENT_USER, CURRENT_TIMESTAMP";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertBareValueHappyPathGoldens(extractor);

		Assert.assertEquals("AST is wrong",
				"{SQL={update={where={and={1={condition={left={column={name=CURRENT_TIMESTAMP, table_ref=null}}, operator=IS NOT NULL}}, 2={condition={left={column={name=LOCALTIME, table_ref=null}}, right={column={name=CURRENT_DATE, table_ref=null}}, operator=<}}}}, assignments={1={set={column={name=score, table_ref=null}}, to={literal=1}}}, table={alias=e, table=employees}, returning={1={column={name=emp_id, table_ref=e}}, 2={column={name=CURRENT_USER, table_ref=null}}, 3={column={name=CURRENT_TIMESTAMP, table_ref=null}}}}}}",
				extractor.getAsTree().toString());

		Assert.assertEquals("Interface is wrong",
				"[score, CURRENT_USER, CURRENT_TIMESTAMP, emp_id]",
				extractor.getInterface().toString());

		Assert.assertEquals("Substitution List is wrong",
				"{}",
				extractor.getSubstitutionsMap().toString());

		Assert.assertEquals("Table Dictionary is wrong",
				"{employees={emp_id=[[@17,108:108='e',<392>,1:108]]}}",
				extractor.getTableColumnDictionaryMap().toString());

		Assert.assertEquals("Query Column Dictionary is wrong",
				"{update0={CURRENT_USER=[[@21,118:129='CURRENT_USER',<392>,1:118]], score=[[@4,23:27='score',<392>,1:23]], CURRENT_TIMESTAMP=[[@23,132:148='CURRENT_TIMESTAMP',<392>,1:132]], emp_id=[[@19,110:115='emp_id',<392>,1:110]]}}",
				extractor.getQueryColumnDictionaryMap().toString());

		Assert.assertEquals("Symbol Table is wrong",
				"{def_update0={assignments={score=[]}, query_dictionary={CURRENT_USER=[[@21,118:129='CURRENT_USER',<392>,1:118]], CURRENT_TIMESTAMP=[[@23,132:148='CURRENT_TIMESTAMP',<392>,1:132]], emp_id=[[@19,110:115='emp_id',<392>,1:110]]}, table_dictionary={employees={emp_id=[[@17,108:108='e',<392>,1:108]]}}, update_dictionary={score=[[@4,23:27='score',<392>,1:23]]}, target_table={employees={score=[[@4,23:27='score',<392>,1:23]]}}, filters=[{name=CURRENT_TIMESTAMP, table_ref=null}, {name=LOCALTIME, table_ref=null}, {name=CURRENT_DATE, table_ref=null}], interface={score=[], CURRENT_USER=[{name=CURRENT_USER, table_ref=null}], CURRENT_TIMESTAMP=[{name=CURRENT_TIMESTAMP, table_ref=null}], emp_id=[{name=emp_id, table_ref=e}]}, table_alias={e=employees}, lhs_unresolved_columns={score={column={name=score, table_ref=null}, locations=[[@4,23:27='score',<392>,1:23]]}}}}",
				extractor.getSymbolTable().toString());

	}

	@Test
	public void bareValueExpressionInsertReturningV0Test() {
		final String query = "INSERT INTO employees (score) VALUES (1) RETURNING emp_id, CURRENT_TIMESTAMP, LOCALTIME,"
				+ " CURRENT_USER";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertBareValueHappyPathGoldens(extractor);

		Assert.assertEquals("AST is wrong",
				"{SQL={insert={preamble=insert_into, from={values={matrix={1={row={1={literal=1}}}}}}, target_table={table={alias=null, table=employees}}, columns={1={column={name=score, table_ref=null}}}, returning={1={column={name=emp_id, table_ref=employees}}, 2={column={name=CURRENT_TIMESTAMP, table_ref=employees}}, 3={column={name=LOCALTIME, table_ref=employees}}, 4={column={name=CURRENT_USER, table_ref=employees}}}}}}",
				extractor.getAsTree().toString());

		Assert.assertEquals("Interface is wrong",
				"[score, CURRENT_USER, CURRENT_TIMESTAMP, LOCALTIME, emp_id]",
				extractor.getInterface().toString());

		Assert.assertEquals("Substitution List is wrong",
				"{}",
				extractor.getSubstitutionsMap().toString());

		Assert.assertEquals("Table Dictionary is wrong",
				"{employees={score=[[@4,23:27='score',<392>,1:23]], CURRENT_USER=[[@17,89:100='CURRENT_USER',<392>,1:89], [@17,89:100='CURRENT_USER',<392>,1:89]], CURRENT_TIMESTAMP=[[@13,59:75='CURRENT_TIMESTAMP',<392>,1:59], [@13,59:75='CURRENT_TIMESTAMP',<392>,1:59]], LOCALTIME=[[@15,78:86='LOCALTIME',<392>,1:78], [@15,78:86='LOCALTIME',<392>,1:78]], emp_id=[[@11,51:56='emp_id',<392>,1:51], [@11,51:56='emp_id',<392>,1:51]]}}",
				extractor.getTableColumnDictionaryMap().toString());

		Assert.assertEquals("Query Column Dictionary is wrong",
				"{values0={$1=[[@7,37:37='(',<287>,1:37]]}, insert1={LOCALTIME=[[@15,78:86='LOCALTIME',<392>,1:78], [@15,78:86='LOCALTIME',<392>,1:78]], score=[[@4,23:27='score',<392>,1:23]], CURRENT_USER=[[@17,89:100='CURRENT_USER',<392>,1:89], [@17,89:100='CURRENT_USER',<392>,1:89]], CURRENT_TIMESTAMP=[[@13,59:75='CURRENT_TIMESTAMP',<392>,1:59], [@13,59:75='CURRENT_TIMESTAMP',<392>,1:59]], emp_id=[[@11,51:56='emp_id',<392>,1:51], [@11,51:56='emp_id',<392>,1:51]]}}",
				extractor.getQueryColumnDictionaryMap().toString());

		Assert.assertEquals("Symbol Table is wrong",
				"{def_insert1={query_dictionary={LOCALTIME=[[@15,78:86='LOCALTIME',<392>,1:78], [@15,78:86='LOCALTIME',<392>,1:78]], score=[[@4,23:27='score',<392>,1:23]], CURRENT_USER=[[@17,89:100='CURRENT_USER',<392>,1:89], [@17,89:100='CURRENT_USER',<392>,1:89]], CURRENT_TIMESTAMP=[[@13,59:75='CURRENT_TIMESTAMP',<392>,1:59], [@13,59:75='CURRENT_TIMESTAMP',<392>,1:59]], emp_id=[[@11,51:56='emp_id',<392>,1:51], [@11,51:56='emp_id',<392>,1:51]]}, table_dictionary={employees={score=[[@4,23:27='score',<392>,1:23]], CURRENT_USER=[[@17,89:100='CURRENT_USER',<392>,1:89], [@17,89:100='CURRENT_USER',<392>,1:89]], CURRENT_TIMESTAMP=[[@13,59:75='CURRENT_TIMESTAMP',<392>,1:59], [@13,59:75='CURRENT_TIMESTAMP',<392>,1:59]], LOCALTIME=[[@15,78:86='LOCALTIME',<392>,1:78], [@15,78:86='LOCALTIME',<392>,1:78]], emp_id=[[@11,51:56='emp_id',<392>,1:51], [@11,51:56='emp_id',<392>,1:51]]}}, def_values0={query_dictionary={$1=[[@7,37:37='(',<287>,1:37]]}, interface={$1=[]}}, _tmp_insert_source_select_sequence=[emp_id, CURRENT_TIMESTAMP, LOCALTIME, CURRENT_USER], interface={score=[{name=$1, table_ref=values0}], CURRENT_USER=[{name=CURRENT_USER, table_ref=employees}], CURRENT_TIMESTAMP=[{name=CURRENT_TIMESTAMP, table_ref=employees}], LOCALTIME=[{name=LOCALTIME, table_ref=employees}], emp_id=[{name=emp_id, table_ref=employees}]}}}",
				extractor.getSymbolTable().toString());

	}

	@Test
	public void bareValueExpressionDeleteReturningV0Test() {
		final String query = "DELETE FROM employees e WHERE CURRENT_DATE = e.col1 AND CURRENT_USER IS NOT NULL RETURNING"
				+ " e.emp_id, LOCALTIMESTAMP, CURRENT_TIMESTAMP";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertBareValueHappyPathGoldens(extractor);

		Assert.assertEquals("AST is wrong",
				"{SQL={delete={table={alias=e, table=employees}, where={and={1={condition={left={column={name=CURRENT_DATE, table_ref=null}}, right={column={name=col1, table_ref=e}}, operator==}}, 2={condition={left={column={name=CURRENT_USER, table_ref=null}}, operator=IS NOT NULL}}}}, returning={1={column={name=emp_id, table_ref=e}}, 2={column={name=LOCALTIMESTAMP, table_ref=null}}, 3={column={name=CURRENT_TIMESTAMP, table_ref=null}}}}}}",
				extractor.getAsTree().toString());

		Assert.assertEquals("Interface is wrong",
				"[CURRENT_TIMESTAMP, LOCALTIMESTAMP, emp_id]",
				extractor.getInterface().toString());

		Assert.assertEquals("Substitution List is wrong",
				"{}",
				extractor.getSubstitutionsMap().toString());

		Assert.assertEquals("Table Dictionary is wrong",
				"{employees={emp_id=[[@16,91:91='e',<392>,1:91]], col1=[[@7,45:45='e',<392>,1:45]]}}",
				extractor.getTableColumnDictionaryMap().toString());

		Assert.assertEquals("Query Column Dictionary is wrong",
				"{delete0={CURRENT_TIMESTAMP=[[@22,117:133='CURRENT_TIMESTAMP',<392>,1:117]], LOCALTIMESTAMP=[[@20,101:114='LOCALTIMESTAMP',<392>,1:101]], emp_id=[[@18,93:98='emp_id',<392>,1:93]]}}",
				extractor.getQueryColumnDictionaryMap().toString());

		Assert.assertEquals("Symbol Table is wrong",
				"{def_delete0={query_dictionary={CURRENT_TIMESTAMP=[[@22,117:133='CURRENT_TIMESTAMP',<392>,1:117]], LOCALTIMESTAMP=[[@20,101:114='LOCALTIMESTAMP',<392>,1:101]], emp_id=[[@18,93:98='emp_id',<392>,1:93]]}, table_dictionary={employees={emp_id=[[@16,91:91='e',<392>,1:91]], col1=[[@7,45:45='e',<392>,1:45]]}}, filters=[{name=CURRENT_DATE, table_ref=null}, {name=col1, table_ref=e}, {name=CURRENT_USER, table_ref=null}], interface={CURRENT_TIMESTAMP=[{name=CURRENT_TIMESTAMP, table_ref=null}], LOCALTIMESTAMP=[{name=LOCALTIMESTAMP, table_ref=null}], emp_id=[{name=emp_id, table_ref=e}]}, table_alias={e=employees}}}",
				extractor.getSymbolTable().toString());

	}

	@Test
	public void bareValueExpressionWithFinalQueryAllContextsV0Test() {
		final String query = "WITH src AS ( SELECT a.col1, a.col2 FROM tab1 a ) SELECT s.col1, b.col2, ROW_NUMBER() OVER"
				+ " (PARTITION BY CURRENT_DATE ORDER BY CURRENT_TIME) AS rn FROM src s JOIN tab2 b ON s.col1 ="
				+ " b.col1 AND CURRENT_TIMESTAMP > LOCALTIME JOIN tab3 c USING (col1) WHERE CURRENT_USER IS NOT"
				+ " NULL AND LOCALTIME < CURRENT_DATE GROUP BY s.col1, b.col2, LOCALTIMESTAMP HAVING"
				+ " CURRENT_TIMESTAMP IS NOT NULL QUALIFY CURRENT_TIMESTAMP > LOCALTIME ORDER BY LOCALTIME";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertBareValueHappyPathGoldens(extractor);

		Assert.assertEquals("AST is wrong",
				"{SQL={with={1={cte={select={1={column={name=col1, table_ref=a}}, 2={column={name=col2, table_ref=a}}}, from={table={alias=a, table=tab1}}}, alias=src}}, query={select={1={column={name=col1, table_ref=s}}, 2={column={name=col2, table_ref=b}}, 3={alias=rn, window_function={over={partition_by={1={column={name=CURRENT_DATE, table_ref=null}}}, orderby={1={null_order=null, predicand={column={name=CURRENT_TIME, table_ref=null}}, sort_order=ASC}}}, function={function_name=ROW_NUMBER, parameters=null}}}}, having={condition={left={column={name=CURRENT_TIMESTAMP, table_ref=null}}, operator=IS NOT NULL}}, orderby={1={null_order=null, predicand={column={name=LOCALTIME, table_ref=null}}, sort_order=ASC}}, from={join={1={table={alias=s, table=src}}, 2={join=JOIN, on={and={1={condition={left={column={name=col1, table_ref=s}}, right={column={name=col1, table_ref=b}}, operator==}}, 2={condition={left={column={name=CURRENT_TIMESTAMP, table_ref=null}}, right={column={name=LOCALTIME, table_ref=null}}, operator=>}}}}}, 3={table={alias=b, table=tab2}}, 4={using={1={column={name=col1, table_ref=null}}}, join=JOIN}, 5={table={alias=c, table=tab3}}}}, where={and={1={condition={left={column={name=CURRENT_USER, table_ref=null}}, operator=IS NOT NULL}}, 2={condition={left={column={name=LOCALTIME, table_ref=null}}, right={column={name=CURRENT_DATE, table_ref=null}}, operator=<}}}}, groupby={1={column={name=col1, table_ref=s}}, 2={column={name=col2, table_ref=b}}, 3={column={name=LOCALTIMESTAMP, table_ref=null}}}, qualify={condition={left={column={name=CURRENT_TIMESTAMP, table_ref=null}}, right={column={name=LOCALTIME, table_ref=null}}, operator=>}}}}}",
				extractor.getAsTree().toString());

		Assert.assertEquals("Interface is wrong",
				"[col2, rn, col1]",
				extractor.getInterface().toString());

		Assert.assertEquals("Substitution List is wrong",
				"{}",
				extractor.getSubstitutionsMap().toString());

		Assert.assertEquals("Table Dictionary is wrong",
				"{tab3={col1=[[@62,242:245='col1',<392>,1:242]]}, tab1={col2=[[@9,29:29='a',<392>,1:29]], col1=[[@5,21:21='a',<392>,1:21]]}, tab2={col2=[[@21,65:65='b',<392>,1:65], [@79,325:325='b',<392>,1:325]], col1=[[@50,182:182='b',<392>,1:182], [@62,242:245='col1',<392>,1:242]]}}",
				extractor.getTableColumnDictionaryMap().toString());

		Assert.assertEquals("Query Column Dictionary is wrong",
				"{query0={col2=[[@11,31:34='col2',<392>,1:31]], col1=[[@7,23:26='col1',<392>,1:23], [@17,57:57='s',<392>,1:57], [@46,173:173='s',<392>,1:173], [@75,317:317='s',<392>,1:317]]}, query1={col2=[[@23,67:70='col2',<392>,1:67]], rn=[[@38,144:145='rn',<392>,1:144]], col1=[[@19,59:62='col1',<392>,1:59]]}}",
				extractor.getQueryColumnDictionaryMap().toString());

		Assert.assertEquals("Symbol Table is wrong",
				"{def_query1={context_list={src=query0, s=query0}, window_ordered_by=[{name=CURRENT_TIME, table_ref=null}], table_dictionary={tab3={col1=[[@62,242:245='col1',<392>,1:242]]}, tab2={col2=[[@21,65:65='b',<392>,1:65], [@79,325:325='b',<392>,1:325]], col1=[[@50,182:182='b',<392>,1:182], [@62,242:245='col1',<392>,1:242]]}}, grouped_by=[{name=col1, table_ref=s}, {name=col2, table_ref=b}, {name=LOCALTIMESTAMP, table_ref=null}], window_partition_by=[{name=CURRENT_DATE, table_ref=null}], def_query0={query_dictionary={col2=[[@11,31:34='col2',<392>,1:31]], col1=[[@7,23:26='col1',<392>,1:23], [@17,57:57='s',<392>,1:57], [@46,173:173='s',<392>,1:173], [@75,317:317='s',<392>,1:317]]}, table_dictionary={tab1={col2=[[@9,29:29='a',<392>,1:29]], col1=[[@5,21:21='a',<392>,1:21]]}}, interface={col2=[{name=col2, table_ref=a}], col1=[{name=col1, table_ref=a}]}, table_alias={a=tab1}}, ordered_by=[{name=LOCALTIME, table_ref=null}], filters=[{name=col1, table_ref=s}, {name=col1, table_ref=b}, {name=CURRENT_TIMESTAMP, table_ref=null}, {name=LOCALTIME, table_ref=null}, {name=col1, table_ref=c}, {name=CURRENT_USER, table_ref=null}, {name=CURRENT_DATE, table_ref=null}], interface={col2=[{name=col2, table_ref=b}], rn=[{name=CURRENT_DATE, table_ref=null}, {name=CURRENT_TIME, table_ref=null}], col1=[{name=col1, table_ref=s}]}, query_dictionary={col2=[[@23,67:70='col2',<392>,1:67]], rn=[[@38,144:145='rn',<392>,1:144]], col1=[[@19,59:62='col1',<392>,1:59]]}, table_alias={b=tab2, s=query0, c=tab3, src=query0}}}",
				extractor.getSymbolTable().toString());

	}

}
