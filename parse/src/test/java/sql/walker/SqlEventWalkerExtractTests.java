package sql.walker;

import java.util.regex.Pattern;

import org.junit.Assert;
import org.junit.Test;

import sql.SQLSelectParserParser;

/**
 * EXTRACT and DATE_PART AST coverage: field forms (keyword, string, Snowflake/Postgres parts), source shapes
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
		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
		String ast = extractor.getAsTree().toString();
		Assert.assertFalse(
				"AST must not contain grammar rule Type= entries (use semantic keys only): " + ast,
				GRAMMAR_RULE_TYPE_IN_AST.matcher(ast).find());
		Assert.assertEquals("AST is wrong", expectedAst, ast);
		return extractor;
	}

	private SqlParseEventWalker assertExtractPredicandAst(String predicandSql, String expectedAst) {
		final SQLSelectParserParser parser = parse(predicandSql);
		SqlParseEventWalker extractor = runPredicandParsertest(predicandSql, parser);
		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
		String ast = extractor.getAsTree().toString();
		Assert.assertFalse(
				"AST must not contain grammar rule Type= entries (use semantic keys only): " + ast,
				GRAMMAR_RULE_TYPE_IN_AST.matcher(ast).find());
		Assert.assertEquals("AST is wrong", expectedAst, ast);
		return extractor;
	}

	private void assertExtractSubstitutionSelect(String query, String expectedAst, String expectedSubstitutions,
			String expectedSymbolTable) {
		SqlParseEventWalker extractor = assertExtractAst(query, expectedAst);
		Assert.assertEquals("Substitution List is wrong", expectedSubstitutions,
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Symbol Table is wrong", expectedSymbolTable, extractor.getSymbolTable().toString());
	}

	private void assertExtractSubstitutionPredicand(String predicandSql, String expectedAst,
			String expectedSubstitutions, String expectedSymbolTable) {
		SqlParseEventWalker extractor = assertExtractPredicandAst(predicandSql, expectedAst);
		Assert.assertEquals("Substitution List is wrong", expectedSubstitutions,
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Symbol Table is wrong", expectedSymbolTable, extractor.getSymbolTable().toString());
	}

	// --- field: standard keyword + column source ---

	@Test
	public void extractKeywordYearFromColumn() {
		SqlParseEventWalker extractor = assertExtractAst(
				"SELECT EXTRACT(YEAR FROM order_date) FROM orders",
				"{SQL={select={1={extract={part_form=KEYWORD, part=YEAR, source={column={name=order_date, table_ref=null}}}}}, from={table={alias=null, table=orders}}}}");
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query0={query_dictionary={unnamed_0=[[@6,35:35=')',<288>,1:35]]}, table_dictionary={orders={order_date=[[@5,25:34='order_date',<391>,1:25]]}}, interface={unnamed_0=[{name=order_date, table_ref=orders}]}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void extractStringMonthFromColumn() {
		assertExtractAst(
				"SELECT EXTRACT('month' FROM order_date) FROM orders",
				"{SQL={select={1={extract={part_form=STRING, part=month, source={column={name=order_date, table_ref=null}}}}}, from={table={alias=null, table=orders}}}}");
	}

	@Test
	public void extractStringDowFromColumn() {
		assertExtractAst(
				"SELECT EXTRACT('dow' FROM order_date) FROM orders",
				"{SQL={select={1={extract={part_form=STRING, part=dow, source={column={name=order_date, table_ref=null}}}}}, from={table={alias=null, table=orders}}}}");
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
	public void extractHourFromMixedCaseCastKeyword() {
		assertExtractAst(
				"SELECT EXTRACT(HOUR FROM CasT(order_date AS TIMESTAMP)) FROM orders",
				"{SQL={select={1={extract={part_form=KEYWORD, part=HOUR, source={function={function_name=CasT, data_type={type=TIMESTAMP}, type=CAST, value={column={name=order_date, table_ref=null}}}}}}}, from={table={alias=null, table=orders}}}}");
	}

	@Test
	public void extractSecondFromPostgresCastTimestamp() {
		assertExtractAst(
				"SELECT EXTRACT(SECOND FROM '2020-01-01'::timestamp) FROM orders",
				"{SQL={select={1={extract={part_form=KEYWORD, part=SECOND, source={function={function_name=cast, data_type={type=TIMESTAMP}, type=CAST, value={literal=2020-01-01}}}}}}, from={table={alias=null, table=orders}}}}");
	}

	@Test
	public void extractSnowflakeLowercaseEpochSecondFromColumn() {
		assertExtractAst(
				"SELECT EXTRACT(epoch_second FROM ts) FROM t",
				"{SQL={select={1={extract={part_form=KEYWORD, part=epoch_second, source={column={name=ts, table_ref=null}}}}}, from={table={alias=null, table=t}}}}");
	}

	@Test
	public void extractYearFromColumnPlusInterval() {
		assertExtractAst(
				"SELECT EXTRACT(YEAR FROM order_date + INTERVAL '1 day') FROM orders",
				"{SQL={select={1={extract={part_form=KEYWORD, part=YEAR, source={calc={left={column={name=order_date, table_ref=null}}, right={source_type=INTERVAL, literal=1 day}, operator=+}}}}}, from={table={alias=null, table=orders}}}}");
	}

	@Test
	public void extractDayFromIntervalLiteralSource() {
		assertExtractAst(
				"SELECT EXTRACT(DAY FROM INTERVAL '7 days') FROM orders",
				"{SQL={select={1={extract={part_form=KEYWORD, part=DAY, source_type=INTERVAL, source={literal=7 days}}}}, from={table={alias=null, table=orders}}}}");
	}

	@Test
	public void extractMonthFromNestedExtractSource() {
		assertExtractAst(
				"SELECT EXTRACT(MONTH FROM EXTRACT(DAY FROM order_date)) FROM orders",
				"{SQL={select={1={extract={part_form=KEYWORD, part=MONTH, source={extract={part_form=KEYWORD, part=DAY, source={column={name=order_date, table_ref=null}}}}}}}, from={table={alias=null, table=orders}}}}");
	}

	// =============================================================================
	// Predicand endpoint (predicand_value start symbol) — one case per SQL test above
	// =============================================================================

	@Test
	public void extractKeywordYearFromColumnPredicand() {
		assertExtractPredicandAst(
				"EXTRACT(YEAR FROM order_date)",
				"{PREDICAND={extract={part_form=KEYWORD, part=YEAR, source={column={name=order_date, table_ref=null}}}}}");
	}

	@Test
	public void extractStringMonthFromColumnPredicand() {
		assertExtractPredicandAst(
				"EXTRACT('month' FROM order_date)",
				"{PREDICAND={extract={part_form=STRING, part=month, source={column={name=order_date, table_ref=null}}}}}");
	}

	@Test
	public void extractStringDowFromColumnPredicand() {
		assertExtractPredicandAst(
				"EXTRACT('dow' FROM order_date)",
				"{PREDICAND={extract={part_form=STRING, part=dow, source={column={name=order_date, table_ref=null}}}}}");
	}

	@Test
	public void extractPostgresDowFromColumnPredicand() {
		assertExtractPredicandAst(
				"EXTRACT(DOW FROM order_date)",
				"{PREDICAND={extract={part_form=KEYWORD, part=DOW, source={column={name=order_date, table_ref=null}}}}}");
	}

	@Test
	public void extractPostgresCenturyFromColumnPredicand() {
		assertExtractPredicandAst(
				"EXTRACT(CENTURY FROM order_date)",
				"{PREDICAND={extract={part_form=KEYWORD, part=CENTURY, source={column={name=order_date, table_ref=null}}}}}");
	}

	@Test
	public void extractPostgresMicrosecondsFromColumnPredicand() {
		assertExtractPredicandAst(
				"EXTRACT(MICROSECONDS FROM ts)",
				"{PREDICAND={extract={part_form=KEYWORD, part=MICROSECONDS, source={column={name=ts, table_ref=null}}}}}");
	}

	@Test
	public void extractSnowflakeDayOfWeekFromColumnPredicand() {
		assertExtractPredicandAst(
				"EXTRACT(DAYOFWEEK FROM order_date)",
				"{PREDICAND={extract={part_form=KEYWORD, part=DAYOFWEEK, source={column={name=order_date, table_ref=null}}}}}");
	}

	@Test
	public void extractSnowflakeWeekIsoFromColumnPredicand() {
		assertExtractPredicandAst(
				"EXTRACT(WEEKISO FROM order_date)",
				"{PREDICAND={extract={part_form=KEYWORD, part=WEEKISO, source={column={name=order_date, table_ref=null}}}}}");
	}

	@Test
	public void extractSnowflakeEpochSecondFromColumnPredicand() {
		assertExtractPredicandAst(
				"EXTRACT(EPOCH_SECOND FROM ts)",
				"{PREDICAND={extract={part_form=KEYWORD, part=EPOCH_SECOND, source={column={name=ts, table_ref=null}}}}}");
	}

	@Test
	public void extractSnowflakeEpochMicrosecondFromColumnPredicand() {
		assertExtractPredicandAst(
				"EXTRACT(EPOCH_MICROSECOND FROM ts)",
				"{PREDICAND={extract={part_form=KEYWORD, part=EPOCH_MICROSECOND, source={column={name=ts, table_ref=null}}}}}");
	}

	@Test
	public void extractTimezoneFromColumnPredicand() {
		assertExtractPredicandAst(
				"EXTRACT(TIMEZONE FROM ts)",
				"{PREDICAND={extract={part_form=KEYWORD, part=TIMEZONE, source={column={name=ts, table_ref=null}}}}}");
	}

	@Test
	public void extractTimezoneHourFromColumnPredicand() {
		assertExtractPredicandAst(
				"EXTRACT(TIMEZONE_HOUR FROM ts)",
				"{PREDICAND={extract={part_form=KEYWORD, part=TIMEZONE_HOUR, source={column={name=ts, table_ref=null}}}}}");
	}

	@Test
	public void extractYearFromDateLiteralPredicand() {
		assertExtractPredicandAst(
				"EXTRACT(YEAR FROM DATE '2020-01-01')",
				"{PREDICAND={extract={part_form=KEYWORD, part=YEAR, source_type=DATE, source={literal=2020-01-01}}}}");
	}

	@Test
	public void extractHourFromTimestampLiteralPredicand() {
		assertExtractPredicandAst(
				"EXTRACT(HOUR FROM TIMESTAMP '2020-01-01 12:00:00')",
				"{PREDICAND={extract={part_form=KEYWORD, part=HOUR, source_type=TIMESTAMP, source={literal=2020-01-01 12:00:00}}}}");
	}

	@Test
	public void extractMinuteFromTimeLiteralPredicand() {
		assertExtractPredicandAst(
				"EXTRACT(MINUTE FROM TIME '12:34:56')",
				"{PREDICAND={extract={part_form=KEYWORD, part=MINUTE, source_type=TIME, source={literal=12:34:56}}}}");
	}

	@Test
	public void extractDayFromQualifiedColumnPredicand() {
		assertExtractPredicandAst(
				"EXTRACT(DAY FROM o.order_date)",
				"{PREDICAND={extract={part_form=KEYWORD, part=DAY, source={column={name=order_date, table_ref=o}}}}}");
	}

	@Test
	public void extractYearFromParenthesizedColumnPredicand() {
		assertExtractPredicandAst(
				"EXTRACT(YEAR FROM (order_date))",
				"{PREDICAND={extract={part_form=KEYWORD, part=YEAR, source={parentheses={column={name=order_date, table_ref=null}}}}}}");
	}

	@Test
	public void extractYearFromAdditiveSourcePredicand() {
		assertExtractPredicandAst(
				"EXTRACT(YEAR FROM order_date + 1)",
				"{PREDICAND={extract={part_form=KEYWORD, part=YEAR, source={calc={left={column={name=order_date, table_ref=null}}, right={literal=1}, operator=+}}}}}");
	}

	@Test
	public void extractHourFromCastTimestampPredicand() {
		assertExtractPredicandAst(
				"EXTRACT(HOUR FROM CAST(order_date AS TIMESTAMP))",
				"{PREDICAND={extract={part_form=KEYWORD, part=HOUR, source={function={function_name=CAST, data_type={type=TIMESTAMP}, type=CAST, value={column={name=order_date, table_ref=null}}}}}}}");
	}

	@Test
	public void extractHourFromMixedCaseCastKeywordPredicand() {
		assertExtractPredicandAst(
				"EXTRACT(HOUR FROM CasT(order_date AS TIMESTAMP))",
				"{PREDICAND={extract={part_form=KEYWORD, part=HOUR, source={function={function_name=CasT, data_type={type=TIMESTAMP}, type=CAST, value={column={name=order_date, table_ref=null}}}}}}}");
	}

	@Test
	public void extractSecondFromPostgresCastTimestampPredicand() {
		assertExtractPredicandAst(
				"EXTRACT(SECOND FROM '2020-01-01'::timestamp)",
				"{PREDICAND={extract={part_form=KEYWORD, part=SECOND, source={function={function_name=cast, data_type={type=TIMESTAMP}, type=CAST, value={literal=2020-01-01}}}}}}");
	}

	@Test
	public void extractSnowflakeLowercaseEpochSecondFromColumnPredicand() {
		assertExtractPredicandAst(
				"EXTRACT(epoch_second FROM ts)",
				"{PREDICAND={extract={part_form=KEYWORD, part=epoch_second, source={column={name=ts, table_ref=null}}}}}");
	}

	@Test
	public void extractYearFromColumnPlusIntervalPredicand() {
		assertExtractPredicandAst(
				"EXTRACT(YEAR FROM order_date + INTERVAL '1 day')",
				"{PREDICAND={extract={part_form=KEYWORD, part=YEAR, source={calc={left={column={name=order_date, table_ref=null}}, right={source_type=INTERVAL, literal=1 day}, operator=+}}}}}");
	}

	@Test
	public void extractDayFromIntervalLiteralSourcePredicand() {
		assertExtractPredicandAst(
				"EXTRACT(DAY FROM INTERVAL '7 days')",
				"{PREDICAND={extract={part_form=KEYWORD, part=DAY, source_type=INTERVAL, source={literal=7 days}}}}");
	}

	@Test
	public void extractMonthFromNestedExtractSourcePredicand() {
		assertExtractPredicandAst(
				"EXTRACT(MONTH FROM EXTRACT(DAY FROM order_date))",
				"{PREDICAND={extract={part_form=KEYWORD, part=MONTH, source={extract={part_form=KEYWORD, part=DAY, source={column={name=order_date, table_ref=null}}}}}}}");
	}

	// =============================================================================
	// Predicand substitution variables (predicand typing on EXTRACT sources)
	// =============================================================================

	@Test
	public void extractKeywordYearFromPredicandSubstitution() {
		assertExtractSubstitutionSelect(
				"SELECT EXTRACT(YEAR FROM <order_date>) FROM orders",
				"{SQL={select={1={extract={part_form=KEYWORD, part=YEAR, source={substitution={name=<order_date>, type=predicand}}}}}, from={table={alias=null, table=orders}}}}",
				"{<order_date>=predicand}",
				"{def_query0={query_dictionary={unnamed_0=[[@6,37:37=')',<288>,1:37]]}, table_dictionary={orders={}}, interface={unnamed_0=[{name=<order_date>, type=predicand}]}}}");
	}

	@Test
	public void extractYearFromAdditivePredicandSubstitution() {
		assertExtractSubstitutionSelect(
				"SELECT EXTRACT(YEAR FROM <order_date> + 1) FROM orders",
				"{SQL={select={1={extract={part_form=KEYWORD, part=YEAR, source={calc={left={substitution={name=<order_date>, type=predicand}}, right={literal=1}, operator=+}}}}}, from={table={alias=null, table=orders}}}}",
				"{<order_date>=predicand}",
				"{def_query0={query_dictionary={unnamed_0=[[@8,41:41=')',<288>,1:41]]}, table_dictionary={orders={}}, interface={unnamed_0=[{name=<order_date>, type=predicand}]}}}");
	}

	@Test
	public void extractHourFromCastPredicandSubstitution() {
		assertExtractSubstitutionSelect(
				"SELECT EXTRACT(HOUR FROM CAST(<order_date> AS TIMESTAMP)) FROM orders",
				"{SQL={select={1={extract={part_form=KEYWORD, part=HOUR, source={function={function_name=CAST, data_type={type=TIMESTAMP}, type=CAST, value={substitution={name=<order_date>, type=predicand}}}}}}}, from={table={alias=null, table=orders}}}}",
				"{<order_date>=predicand}",
				"{def_query0={query_dictionary={unnamed_0=[[@11,56:56=')',<288>,1:56]]}, table_dictionary={orders={}}, interface={unnamed_0=[{name=<order_date>, type=predicand}]}}}");
	}

	@Test
	public void extractMonthFromNestedExtractPredicandSubstitution() {
		assertExtractSubstitutionSelect(
				"SELECT EXTRACT(MONTH FROM EXTRACT(DAY FROM <order_date>)) FROM orders",
				"{SQL={select={1={extract={part_form=KEYWORD, part=MONTH, source={extract={part_form=KEYWORD, part=DAY, source={substitution={name=<order_date>, type=predicand}}}}}}}, from={table={alias=null, table=orders}}}}",
				"{<order_date>=predicand}",
				"{def_query0={query_dictionary={unnamed_0=[[@11,56:56=')',<288>,1:56]]}, table_dictionary={orders={}}, interface={unnamed_0=[{name=<order_date>, type=predicand}]}}}");
	}

	@Test
	public void extractYearFromParenthesizedPredicandSubstitution() {
		assertExtractSubstitutionSelect(
				"SELECT EXTRACT(YEAR FROM (<order_date>)) FROM orders",
				"{SQL={select={1={extract={part_form=KEYWORD, part=YEAR, source={parentheses={substitution={name=<order_date>, type=predicand}}}}}}, from={table={alias=null, table=orders}}}}",
				"{<order_date>=predicand}",
				"{def_query0={query_dictionary={unnamed_0=[[@8,39:39=')',<288>,1:39]]}, table_dictionary={orders={}}, interface={unnamed_0=[{name=<order_date>, type=predicand}]}}}");
	}

	@Test
	public void extractKeywordYearPredicandEndpointSubstitution() {
		assertExtractSubstitutionPredicand(
				"EXTRACT(YEAR FROM <order_date>)",
				"{PREDICAND={extract={part_form=KEYWORD, part=YEAR, source={substitution={name=<order_date>, type=predicand}}}}}",
				"{<order_date>=predicand}",
				"{}");
	}

	// --- DATE_PART(... FROM ...) → extract AST; comma forms → routine_invocation ---

	@Test
	public void extractCommaFormIsRoutineInvocation() {
		assertExtractAst(
				"SELECT EXTRACT('year', order_date) FROM orders",
				"{SQL={select={1={function={parameters={1={literal='year'}, 2={column={name=order_date, table_ref=null}}}, function_name=EXTRACT}}}, from={table={alias=null, table=orders}}}}");
	}

	@Test
	public void extractCommaLowercaseNameIsRoutineInvocation() {
		assertExtractAst(
				"SELECT extract('year', order_date) FROM orders",
				"{SQL={select={1={function={parameters={1={literal='year'}, 2={column={name=order_date, table_ref=null}}}, function_name=extract}}}, from={table={alias=null, table=orders}}}}");
	}

	@Test
	public void extractFromLowercaseNameYearFromColumn() {
		assertExtractAst(
				"SELECT extract(YEAR FROM order_date) FROM orders",
				"{SQL={select={1={extract={part_form=KEYWORD, part=YEAR, source={column={name=order_date, table_ref=null}}}}}, from={table={alias=null, table=orders}}}}");
	}

	@Test
	public void datePartCommaStringYearFromColumnIsRoutineInvocation() {
		assertExtractAst(
				"SELECT DATE_PART('year', order_date) FROM orders",
				"{SQL={select={1={function={parameters={1={literal='year'}, 2={column={name=order_date, table_ref=null}}}, function_name=DATE_PART}}}, from={table={alias=null, table=orders}}}}");
	}

	@Test
	public void datePartCommaLowercaseNameIsRoutineInvocation() {
		assertExtractAst(
				"SELECT date_part('year', order_date) FROM orders",
				"{SQL={select={1={function={parameters={1={literal='year'}, 2={column={name=order_date, table_ref=null}}}, function_name=date_part}}}, from={table={alias=null, table=orders}}}}");
	}

	@Test
	public void datePartFromKeywordYearFromColumn() {
		assertExtractAst(
				"SELECT DATE_PART(YEAR FROM order_date) FROM orders",
				"{SQL={select={1={extract={invocation=DATE_PART, part_form=KEYWORD, part=YEAR, source={column={name=order_date, table_ref=null}}}}}, from={table={alias=null, table=orders}}}}");
	}

	@Test
	public void datePartFromLowercaseNameYearFromColumn() {
		assertExtractAst(
				"SELECT date_part(MONTH FROM order_date) FROM orders",
				"{SQL={select={1={extract={invocation=DATE_PART, part_form=KEYWORD, part=MONTH, source={column={name=order_date, table_ref=null}}}}}, from={table={alias=null, table=orders}}}}");
	}

	@Test
	public void datePartFromNestedExtractSource() {
		assertExtractAst(
				"SELECT DATE_PART(MONTH FROM EXTRACT(DAY FROM order_date)) FROM orders",
				"{SQL={select={1={extract={invocation=DATE_PART, part_form=KEYWORD, part=MONTH, source={extract={part_form=KEYWORD, part=DAY, source={column={name=order_date, table_ref=null}}}}}}}, from={table={alias=null, table=orders}}}}");
	}

	@Test
	public void datePartFromPredicandEndpointSubstitution() {
		assertExtractSubstitutionPredicand(
				"date_part(HOUR FROM <order_date>)",
				"{PREDICAND={extract={invocation=DATE_PART, part_form=KEYWORD, part=HOUR, source={substitution={name=<order_date>, type=predicand}}}}}",
				"{<order_date>=predicand}",
				"{}");
	}
}
