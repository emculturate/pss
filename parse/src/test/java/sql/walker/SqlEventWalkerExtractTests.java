package sql.walker;

import org.junit.Assert;
import org.junit.Test;

import sql.SQLSelectParserParser;

/**
 * Exemplar coverage for {@code EXTRACT(field FROM source)} — Snowflake + Postgres surface forms.
 * See {@code parse/documents/extract-dialect-capture-workplan.md}.
 */
public class SqlEventWalkerExtractTests extends AbstractSqlParseEventWalkerTest {

	@Test
	public void extractKeywordYearFromColumn() {
		final String query = "SELECT EXTRACT(YEAR FROM order_date) FROM orders";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={extract={part_form=KEYWORD, part=YEAR, source={column={name=order_date, table_ref=null}}}}}, from={table={alias=null, table=orders}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query0={query_dictionary={unnamed_0=[[@6,35:35=')',<288>,1:35]]}, table_dictionary={orders={order_date=[[@5,25:34='order_date',<390>,1:25]]}}, interface={unnamed_0=[{name=order_date, table_ref=orders}]}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void extractStringMonthFromColumn() {
		final String query = "SELECT EXTRACT('month' FROM order_date) FROM orders";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={extract={part_form=STRING, part=MONTH, source={column={name=order_date, table_ref=null}}}}}, from={table={alias=null, table=orders}}}}",
				extractor.getAsTree().toString());
	}

	@Test
	public void extractPostgresDowFromColumn() {
		final String query = "SELECT EXTRACT(DOW FROM order_date) FROM orders";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={extract={part_form=KEYWORD, part=DOW, source={column={name=order_date, table_ref=null}}}}}, from={table={alias=null, table=orders}}}}",
				extractor.getAsTree().toString());
	}

	@Test
	public void extractSnowflakeDayOfWeekFromColumn() {
		final String query = "SELECT EXTRACT(DAYOFWEEK FROM order_date) FROM orders";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={extract={part_form=KEYWORD, part=DAYOFWEEK, source={column={name=order_date, table_ref=null}}}}}, from={table={alias=null, table=orders}}}}",
				extractor.getAsTree().toString());
	}

	@Test
	public void extractSnowflakeEpochSecondFromColumn() {
		final String query = "SELECT EXTRACT(EPOCH_SECOND FROM ts) FROM t";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={extract={part_form=KEYWORD, part=EPOCH_SECOND, source={column={name=ts, table_ref=null}}}}}, from={table={alias=null, table=t}}}}",
				extractor.getAsTree().toString());
	}

	@Test
	public void extractYearFromDateLiteral() {
		final String query = "SELECT EXTRACT(YEAR FROM DATE '2020-01-01') FROM orders";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={extract={part_form=KEYWORD, part=YEAR, source={literal={Type=314}}}}}, from={table={alias=null, table=orders}}}}",
				extractor.getAsTree().toString());
	}

	@Test
	public void extractYearFromParenthesizedColumn() {
		final String query = "SELECT EXTRACT(YEAR FROM (order_date)) FROM orders";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={extract={part_form=KEYWORD, part=YEAR, source={parentheses={column={name=order_date, table_ref=null}}}}}}, from={table={alias=null, table=orders}}}}",
				extractor.getAsTree().toString());
	}

	@Test
	public void extractHourFromCastTimestamp() {
		final String query = "SELECT EXTRACT(HOUR FROM CAST(order_date AS TIMESTAMP)) FROM orders";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={extract={part_form=KEYWORD, part=HOUR, source={function={function_name=CAST, data_type={type=TIMESTAMP}, type=CAST, value={column={name=order_date, table_ref=null}}}}}}}, from={table={alias=null, table=orders}}}}",
				extractor.getAsTree().toString());
	}

}
