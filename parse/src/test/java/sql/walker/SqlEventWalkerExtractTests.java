package sql.walker;

import java.util.regex.Pattern;

import org.junit.Assert;
import org.junit.Test;

import sql.SQLSelectParserParser;

/**
 * EXTRACT AST coverage: field forms (keyword, string, Snowflake/Postgres parts), source shapes
 * (column, typed literals, parentheses, calc, cast, nested extract). Every test asserts the
 * walker tree contains no grammar {@code Type=NNN} rule stubs.
 *
 * @see parse/documents/extract-dialect-capture-workplan.md
 */
public class SqlEventWalkerExtractTests extends AbstractSqlParseEventWalkerTest {

	private static final Pattern GRAMMAR_RULE_TYPE_IN_AST = Pattern.compile("Type=\\d+");

	private SqlParseEventWalker assertExtractAst(String query, String expectedAst) {
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		String ast = extractor.getAsTree().toString();
		Assert.assertFalse(
				"AST must not contain grammar rule Type= entries (use semantic keys only): " + ast,
				GRAMMAR_RULE_TYPE_IN_AST.matcher(ast).find());
		Assert.assertEquals("AST is wrong", expectedAst, ast);
		return extractor;
	}

	// --- field: standard keyword + column source ---

	@Test
	public void extractKeywordYearFromColumn() {
		SqlParseEventWalker extractor = assertExtractAst(
				"SELECT EXTRACT(YEAR FROM order_date) FROM orders",
				"{SQL={select={1={extract={part_form=KEYWORD, part=YEAR, source={column={name=order_date, table_ref=null}}}}}, from={table={alias=null, table=orders}}}}");
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query0={query_dictionary={unnamed_0=[[@6,35:35=')',<288>,1:35]]}, table_dictionary={orders={order_date=[[@5,25:34='order_date',<390>,1:25]]}}, interface={unnamed_0=[{name=order_date, table_ref=orders}]}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void extractStringMonthFromColumn() {
		assertExtractAst(
				"SELECT EXTRACT('month' FROM order_date) FROM orders",
				"{SQL={select={1={extract={part_form=STRING, part=MONTH, source={column={name=order_date, table_ref=null}}}}}, from={table={alias=null, table=orders}}}}");
	}

	@Test
	public void extractStringDowFromColumn() {
		assertExtractAst(
				"SELECT EXTRACT('dow' FROM order_date) FROM orders",
				"{SQL={select={1={extract={part_form=STRING, part=DOW, source={column={name=order_date, table_ref=null}}}}}, from={table={alias=null, table=orders}}}}");
	}

	// --- field: Postgres extended ---

	@Test
	public void extractPostgresDowFromColumn() {
		assertExtractAst(
				"SELECT EXTRACT(DOW FROM order_date) FROM orders",
				"{SQL={select={1={extract={part_form=KEYWORD, part=DOW, source={column={name=order_date, table_ref=null}}}}}, from={table={alias=null, table=orders}}}}");
	}

	@Test
	public void extractPostgresCenturyFromColumn() {
		assertExtractAst(
				"SELECT EXTRACT(CENTURY FROM order_date) FROM orders",
				"{SQL={select={1={extract={part_form=KEYWORD, part=CENTURY, source={column={name=order_date, table_ref=null}}}}}, from={table={alias=null, table=orders}}}}");
	}

	@Test
	public void extractPostgresMicrosecondsFromColumn() {
		assertExtractAst(
				"SELECT EXTRACT(MICROSECONDS FROM ts) FROM t",
				"{SQL={select={1={extract={part_form=KEYWORD, part=MICROSECONDS, source={column={name=ts, table_ref=null}}}}}, from={table={alias=null, table=t}}}}");
	}

	// --- field: Snowflake-specific keywords ---

	@Test
	public void extractSnowflakeDayOfWeekFromColumn() {
		assertExtractAst(
				"SELECT EXTRACT(DAYOFWEEK FROM order_date) FROM orders",
				"{SQL={select={1={extract={part_form=KEYWORD, part=DAYOFWEEK, source={column={name=order_date, table_ref=null}}}}}, from={table={alias=null, table=orders}}}}");
	}

	@Test
	public void extractSnowflakeWeekIsoFromColumn() {
		assertExtractAst(
				"SELECT EXTRACT(WEEKISO FROM order_date) FROM orders",
				"{SQL={select={1={extract={part_form=KEYWORD, part=WEEKISO, source={column={name=order_date, table_ref=null}}}}}, from={table={alias=null, table=orders}}}}");
	}

	@Test
	public void extractSnowflakeEpochSecondFromColumn() {
		assertExtractAst(
				"SELECT EXTRACT(EPOCH_SECOND FROM ts) FROM t",
				"{SQL={select={1={extract={part_form=KEYWORD, part=EPOCH_SECOND, source={column={name=ts, table_ref=null}}}}}, from={table={alias=null, table=t}}}}");
	}

	@Test
	public void extractSnowflakeEpochMicrosecondFromColumn() {
		assertExtractAst(
				"SELECT EXTRACT(EPOCH_MICROSECOND FROM ts) FROM t",
				"{SQL={select={1={extract={part_form=KEYWORD, part=EPOCH_MICROSECOND, source={column={name=ts, table_ref=null}}}}}, from={table={alias=null, table=t}}}}");
	}

	// --- field: timezone parts ---

	@Test
	public void extractTimezoneFromColumn() {
		assertExtractAst(
				"SELECT EXTRACT(TIMEZONE FROM ts) FROM t",
				"{SQL={select={1={extract={part_form=KEYWORD, part=TIMEZONE, source={column={name=ts, table_ref=null}}}}}, from={table={alias=null, table=t}}}}");
	}

	@Test
	public void extractTimezoneHourFromColumn() {
		assertExtractAst(
				"SELECT EXTRACT(TIMEZONE_HOUR FROM ts) FROM t",
				"{SQL={select={1={extract={part_form=KEYWORD, part=TIMEZONE_HOUR, source={column={name=ts, table_ref=null}}}}}, from={table={alias=null, table=t}}}}");
	}

	// --- source: typed SQL literals (source_type on extract) ---

	@Test
	public void extractYearFromDateLiteral() {
		assertExtractAst(
				"SELECT EXTRACT(YEAR FROM DATE '2020-01-01') FROM orders",
				"{SQL={select={1={extract={part_form=KEYWORD, part=YEAR, source_type=DATE, source={literal=2020-01-01}}}}, from={table={alias=null, table=orders}}}}");
	}

	@Test
	public void extractHourFromTimestampLiteral() {
		assertExtractAst(
				"SELECT EXTRACT(HOUR FROM TIMESTAMP '2020-01-01 12:00:00') FROM orders",
				"{SQL={select={1={extract={part_form=KEYWORD, part=HOUR, source_type=TIMESTAMP, source={literal=2020-01-01 12:00:00}}}}, from={table={alias=null, table=orders}}}}");
	}

	@Test
	public void extractMinuteFromTimeLiteral() {
		assertExtractAst(
				"SELECT EXTRACT(MINUTE FROM TIME '12:34:56') FROM orders",
				"{SQL={select={1={extract={part_form=KEYWORD, part=MINUTE, source_type=TIME, source={literal=12:34:56}}}}, from={table={alias=null, table=orders}}}}");
	}

	// --- source: column qualification, parentheses, arithmetic ---

	@Test
	public void extractDayFromQualifiedColumn() {
		assertExtractAst(
				"SELECT EXTRACT(DAY FROM o.order_date) FROM orders o",
				"{SQL={select={1={extract={part_form=KEYWORD, part=DAY, source={column={name=order_date, table_ref=o}}}}}, from={table={alias=o, table=orders}}}}");
	}

	@Test
	public void extractYearFromParenthesizedColumn() {
		assertExtractAst(
				"SELECT EXTRACT(YEAR FROM (order_date)) FROM orders",
				"{SQL={select={1={extract={part_form=KEYWORD, part=YEAR, source={parentheses={column={name=order_date, table_ref=null}}}}}}, from={table={alias=null, table=orders}}}}");
	}

	@Test
	public void extractYearFromAdditiveSource() {
		assertExtractAst(
				"SELECT EXTRACT(YEAR FROM order_date + 1) FROM orders",
				"{SQL={select={1={extract={part_form=KEYWORD, part=YEAR, source={calc={left={column={name=order_date, table_ref=null}}, right={literal=1}, operator=+}}}}}, from={table={alias=null, table=orders}}}}");
	}

	// --- source: functions and nested extract ---

	@Test
	public void extractHourFromCastTimestamp() {
		assertExtractAst(
				"SELECT EXTRACT(HOUR FROM CAST(order_date AS TIMESTAMP)) FROM orders",
				"{SQL={select={1={extract={part_form=KEYWORD, part=HOUR, source={function={function_name=CAST, data_type={type=TIMESTAMP}, type=CAST, value={column={name=order_date, table_ref=null}}}}}}}, from={table={alias=null, table=orders}}}}");
	}

	@Test
	public void extractSecondFromPostgresCastTimestamp() {
		assertExtractAst(
				"SELECT EXTRACT(SECOND FROM '2020-01-01'::timestamp) FROM orders",
				"{SQL={select={1={extract={part_form=KEYWORD, part=SECOND, source={function={function_name=cast, data_type={type=TIMESTAMP}, type=CAST, value={literal='2020-01-01'}}}}}}, from={table={alias=null, table=orders}}}}");
	}

	@Test
	public void extractMonthFromNestedExtractSource() {
		assertExtractAst(
				"SELECT EXTRACT(MONTH FROM EXTRACT(DAY FROM order_date)) FROM orders",
				"{SQL={select={1={extract={part_form=KEYWORD, part=MONTH, source={extract={part_form=KEYWORD, part=DAY, source={column={name=order_date, table_ref=null}}}}}}}, from={table={alias=null, table=orders}}}}");
	}
}
