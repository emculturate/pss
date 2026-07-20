package sql.walker;

import org.junit.Assert;
import org.junit.Test;

import sql.SQLSelectParserParser;

public class SqlEventWalkerPivotUnpivotTests extends AbstractSqlParseEventWalkerTest {

	// UNPIVOT RELATIONAL OPERATOR TESTS

	@Test
	public void unpivotV1Test() {
		final String query = "SELECT id, metric_name, jan_sales, feb_sales, mar_sales, metric_value\n" 
			+ " FROM my_table \n "
			+ " UNPIVOT (\n" 
			+ " metric_value FOR metric_name IN (jan_sales, feb_sales, mar_sales));";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=id, table_ref=null}}, 2={column={name=metric_name, table_ref=null}}, 3={column={name=jan_sales, table_ref=null}}, 4={column={name=feb_sales, table_ref=null}}, 5={column={name=mar_sales, table_ref=null}}, 6={column={name=metric_value, table_ref=null}}}, from={unpivot={value=metric_value, for=metric_name, in={1={name=jan_sales, table_ref=null}, 2={name=feb_sales, table_ref=null}, 3={name=mar_sales, table_ref=null}}}, table={alias=null, table=my_table}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[jan_sales, mar_sales, metric_name, metric_value, id, feb_sales]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{my_table={jan_sales=[[@21,132:140='jan_sales',<381>,4:34], [@5,24:32='jan_sales',<381>,1:24]], mar_sales=[[@25,154:162='mar_sales',<381>,4:56], [@9,46:54='mar_sales',<381>,1:46]], id=[[@1,7:8='id',<381>,1:7]], feb_sales=[[@23,143:151='feb_sales',<381>,4:45], [@7,35:43='feb_sales',<381>,1:35]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={jan_sales=[[@5,24:32='jan_sales',<381>,1:24]], mar_sales=[[@9,46:54='mar_sales',<381>,1:46]], metric_name=[[@3,11:21='metric_name',<381>,1:11]], metric_value=[[@11,57:68='metric_value',<381>,1:57]], id=[[@1,7:8='id',<381>,1:7]], feb_sales=[[@7,35:43='feb_sales',<381>,1:35]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query0={query_dictionary={jan_sales=[[@5,24:32='jan_sales',<381>,1:24]], mar_sales=[[@9,46:54='mar_sales',<381>,1:46]], metric_name=[[@3,11:21='metric_name',<381>,1:11]], metric_value=[[@11,57:68='metric_value',<381>,1:57]], id=[[@1,7:8='id',<381>,1:7]], feb_sales=[[@7,35:43='feb_sales',<381>,1:35]]}, table_dictionary={my_table={jan_sales=[[@21,132:140='jan_sales',<381>,4:34], [@5,24:32='jan_sales',<381>,1:24]], mar_sales=[[@25,154:162='mar_sales',<381>,4:56], [@9,46:54='mar_sales',<381>,1:46]], id=[[@1,7:8='id',<381>,1:7]], feb_sales=[[@23,143:151='feb_sales',<381>,4:45], [@7,35:43='feb_sales',<381>,1:35]]}}, interface={jan_sales=[{name=jan_sales, table_ref=my_table}], mar_sales=[{name=mar_sales, table_ref=my_table}], metric_name=[{name=metric_name, table_ref=my_table}], metric_value=[{name=jan_sales, table_ref=my_table}, {name=feb_sales, table_ref=my_table}, {name=mar_sales, table_ref=my_table}], id=[{name=id, table_ref=my_table}], feb_sales=[{name=feb_sales, table_ref=my_table}]}, derived_columns={metric_name=[{name=jan_sales, table_ref=my_table}, {name=feb_sales, table_ref=my_table}, {name=mar_sales, table_ref=my_table}], metric_value=[{name=jan_sales, table_ref=my_table}, {name=feb_sales, table_ref=my_table}, {name=mar_sales, table_ref=my_table}]}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void unpivotPostModifierAliasV1Test() {
		final String query =
				"SELECT sales_amount, outer_up.feb_sales\n" +
				"FROM monthly_sales\n" +
				"UNPIVOT (sales_amount FOR month_name IN (jan_sales, feb_sales)) outer_up;";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=sales_amount, table_ref=null}}, 2={column={name=feb_sales, table_ref=outer_up}}}, from={unpivot={value=sales_amount, for=month_name, in={1={name=jan_sales, table_ref=null}, 2={name=feb_sales, table_ref=null}}}, alias=outer_up, table={alias=null, table=monthly_sales}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[sales_amount, feb_sales]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{monthly_sales={jan_sales=[[@15,100:108='jan_sales',<381>,3:41]], feb_sales=[[@3,21:28='outer_up',<381>,1:21], [@17,111:119='feb_sales',<381>,3:52], [@5,30:38='feb_sales',<381>,1:30]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{query0={feb_sales=[[@5,30:38='feb_sales',<381>,1:30]], sales_amount=[[@1,7:18='sales_amount',<381>,1:7]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query0={query_dictionary={sales_amount=[[@1,7:18='sales_amount',<381>,1:7]], feb_sales=[[@5,30:38='feb_sales',<381>,1:30]]}, table_dictionary={monthly_sales={jan_sales=[[@15,100:108='jan_sales',<381>,3:41]], feb_sales=[[@17,111:119='feb_sales',<381>,3:52], [@3,21:28='outer_up',<381>,1:21], [@5,30:38='feb_sales',<381>,1:30]]}}, interface={sales_amount=[{name=jan_sales, table_ref=outer_up}, {name=feb_sales, table_ref=outer_up}], feb_sales=[{name=feb_sales, table_ref=outer_up}]}, derived_columns={month_name=[{name=jan_sales, table_ref=outer_up}, {name=feb_sales, table_ref=outer_up}], sales_amount=[{name=jan_sales, table_ref=outer_up}, {name=feb_sales, table_ref=outer_up}]}, table_alias={outer_up=monthly_sales}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void unpivotTableWithInAliasesJanFebMarV2Test() {
		final String query =
			"SELECT empid, month_name, sales_amount, units, sales_amount / units \n" +
			"FROM monthly_sales\n" +
			"UNPIVOT (sales_amount FOR month_name IN (jan_sales AS 'JAN', feb_sales AS 'FEB', mar_sales AS 'MAR'))\n" +
			"where sales_amount/units > 1.00;";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=empid, table_ref=null}}, 2={column={name=month_name, table_ref=null}}, 3={column={name=sales_amount, table_ref=null}}, 4={column={name=units, table_ref=null}}, 5={calc={left={column={name=sales_amount, table_ref=null}}, right={column={name=units, table_ref=null}}, operator=/}}}, from={unpivot={value=sales_amount, for=month_name, in={1={name=jan_sales, label='JAN', table_ref=null}, 2={name=feb_sales, label='FEB', table_ref=null}, 3={name=mar_sales, label='MAR', table_ref=null}}}, table={alias=null, table=monthly_sales}}, where={condition={left={calc={left={column={name=sales_amount, table_ref=null}}, right={column={name=units, table_ref=null}}, operator=/}}, right={literal=1.00}, operator=>}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[empid, month_name, sales_amount, units, unnamed_0]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{monthly_sales={jan_sales=[[@21,129:137='jan_sales',<381>,3:41]], mar_sales=[[@29,169:177='mar_sales',<381>,3:81]], empid=[[@1,7:11='empid',<381>,1:7]], units=[[@7,40:44='units',<381>,1:40], [@11,62:66='units',<381>,1:62], [@37,209:213='units',<381>,4:19]], feb_sales=[[@25,149:157='feb_sales',<381>,3:61]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={empid=[[@1,7:11='empid',<381>,1:7]], month_name=[[@3,14:23='month_name',<381>,1:14]], sales_amount=[[@5,26:37='sales_amount',<381>,1:26]], units=[[@7,40:44='units',<381>,1:40]], unnamed_0=[[@11,62:66='units',<381>,1:62]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query0={query_dictionary={empid=[[@1,7:11='empid',<381>,1:7]], month_name=[[@3,14:23='month_name',<381>,1:14]], sales_amount=[[@5,26:37='sales_amount',<381>,1:26]], units=[[@7,40:44='units',<381>,1:40]], unnamed_0=[[@11,62:66='units',<381>,1:62]]}, table_dictionary={monthly_sales={jan_sales=[[@21,129:137='jan_sales',<381>,3:41]], mar_sales=[[@29,169:177='mar_sales',<381>,3:81]], empid=[[@1,7:11='empid',<381>,1:7]], units=[[@7,40:44='units',<381>,1:40], [@11,62:66='units',<381>,1:62], [@37,209:213='units',<381>,4:19]], feb_sales=[[@25,149:157='feb_sales',<381>,3:61]]}}, filters=[{name=sales_amount, table_ref=null}, {name=units, table_ref=monthly_sales}], interface={empid=[{name=empid, table_ref=monthly_sales}], month_name=[{name=month_name, table_ref=monthly_sales}], sales_amount=[{name=jan_sales, table_ref=monthly_sales}, {name=feb_sales, table_ref=monthly_sales}, {name=mar_sales, table_ref=monthly_sales}], units=[{name=units, table_ref=monthly_sales}], unnamed_0=[{name=jan_sales, table_ref=monthly_sales}, {name=feb_sales, table_ref=monthly_sales}, {name=mar_sales, table_ref=monthly_sales}, {name=units, table_ref=monthly_sales}]}, derived_columns={month_name=[{name=jan_sales, table_ref=monthly_sales}, {name=feb_sales, table_ref=monthly_sales}, {name=mar_sales, table_ref=monthly_sales}], sales_amount=[{name=jan_sales, table_ref=monthly_sales}, {name=feb_sales, table_ref=monthly_sales}, {name=mar_sales, table_ref=monthly_sales}]}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void unpivotTableWithInAliasesJanFebMarV2WithTabAliasTest() {
		final String query =
			"SELECT empid, month_name, sales_amount, units, sales_amount / units \n" +
			"FROM monthly_sales\n" +
			"UNPIVOT (sales_amount FOR month_name IN (jan_sales AS 'JAN', feb_sales AS 'FEB', mar_sales AS 'MAR')) unpvt\n" +
			"where sales_amount/units > 1.00;";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=empid, table_ref=null}}, 2={column={name=month_name, table_ref=null}}, 3={column={name=sales_amount, table_ref=null}}, 4={column={name=units, table_ref=null}}, 5={calc={left={column={name=sales_amount, table_ref=null}}, right={column={name=units, table_ref=null}}, operator=/}}}, from={unpivot={value=sales_amount, for=month_name, in={1={name=jan_sales, label='JAN', table_ref=null}, 2={name=feb_sales, label='FEB', table_ref=null}, 3={name=mar_sales, label='MAR', table_ref=null}}}, alias=unpvt, table={alias=null, table=monthly_sales}}, where={condition={left={calc={left={column={name=sales_amount, table_ref=null}}, right={column={name=units, table_ref=null}}, operator=/}}, right={literal=1.00}, operator=>}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[empid, month_name, sales_amount, units, unnamed_0]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{monthly_sales={jan_sales=[[@21,129:137='jan_sales',<381>,3:41]], mar_sales=[[@29,169:177='mar_sales',<381>,3:81]], empid=[[@1,7:11='empid',<381>,1:7]], units=[[@7,40:44='units',<381>,1:40], [@11,62:66='units',<381>,1:62], [@38,215:219='units',<381>,4:19]], feb_sales=[[@25,149:157='feb_sales',<381>,3:61]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={empid=[[@1,7:11='empid',<381>,1:7]], month_name=[[@3,14:23='month_name',<381>,1:14]], sales_amount=[[@5,26:37='sales_amount',<381>,1:26]], units=[[@7,40:44='units',<381>,1:40]], unnamed_0=[[@11,62:66='units',<381>,1:62]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query0={query_dictionary={empid=[[@1,7:11='empid',<381>,1:7]], month_name=[[@3,14:23='month_name',<381>,1:14]], sales_amount=[[@5,26:37='sales_amount',<381>,1:26]], units=[[@7,40:44='units',<381>,1:40]], unnamed_0=[[@11,62:66='units',<381>,1:62]]}, table_dictionary={monthly_sales={jan_sales=[[@21,129:137='jan_sales',<381>,3:41]], mar_sales=[[@29,169:177='mar_sales',<381>,3:81]], empid=[[@1,7:11='empid',<381>,1:7]], units=[[@7,40:44='units',<381>,1:40], [@11,62:66='units',<381>,1:62], [@38,215:219='units',<381>,4:19]], feb_sales=[[@25,149:157='feb_sales',<381>,3:61]]}}, filters=[{name=sales_amount, table_ref=null}, {name=units, table_ref=monthly_sales}], interface={empid=[{name=empid, table_ref=monthly_sales}], month_name=[{name=month_name, table_ref=monthly_sales}], sales_amount=[{name=jan_sales, table_ref=unpvt}, {name=feb_sales, table_ref=unpvt}, {name=mar_sales, table_ref=unpvt}], units=[{name=units, table_ref=monthly_sales}], unnamed_0=[{name=jan_sales, table_ref=unpvt}, {name=feb_sales, table_ref=unpvt}, {name=mar_sales, table_ref=unpvt}, {name=units, table_ref=monthly_sales}]}, derived_columns={month_name=[{name=jan_sales, table_ref=unpvt}, {name=feb_sales, table_ref=unpvt}, {name=mar_sales, table_ref=unpvt}], sales_amount=[{name=jan_sales, table_ref=unpvt}, {name=feb_sales, table_ref=unpvt}, {name=mar_sales, table_ref=unpvt}]}, table_alias={unpvt=monthly_sales}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void unpivotTableWithGroupByAndOrderBySalesAmountV2GroupOrderTest() {
		final String query =
			"SELECT month_name, sales_amount, units \n" +
			"FROM monthly_sales\n" +
			"UNPIVOT (sales_amount FOR month_name IN (jan_sales AS 'JAN', feb_sales AS 'FEB', mar_sales AS 'MAR'))\n" +
			"WHERE sales_amount / units > 1.00\n" +
			"GROUP BY month_name, sales_amount, units\n" +
			"ORDER BY sales_amount;";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=month_name, table_ref=null}}, 2={column={name=sales_amount, table_ref=null}}, 3={column={name=units, table_ref=null}}}, orderby={1={null_order=null, predicand={column={name=sales_amount, table_ref=null}}, sort_order=ASC}}, from={unpivot={value=sales_amount, for=month_name, in={1={name=jan_sales, label='JAN', table_ref=null}, 2={name=feb_sales, label='FEB', table_ref=null}, 3={name=mar_sales, label='MAR', table_ref=null}}}, table={alias=null, table=monthly_sales}}, where={condition={left={calc={left={column={name=sales_amount, table_ref=null}}, right={column={name=units, table_ref=null}}, operator=/}}, right={literal=1.00}, operator=>}}, groupby={1={column={name=month_name, table_ref=null}}, 2={column={name=sales_amount, table_ref=null}}, 3={column={name=units, table_ref=null}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[month_name, sales_amount, units]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{monthly_sales={jan_sales=[[@15,100:108='jan_sales',<381>,3:41]], mar_sales=[[@23,140:148='mar_sales',<381>,3:81]], units=[[@5,33:37='units',<381>,1:33], [@31,182:186='units',<381>,4:21], [@42,230:234='units',<381>,5:35]], feb_sales=[[@19,120:128='feb_sales',<381>,3:61]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={month_name=[[@1,7:16='month_name',<381>,1:7]], sales_amount=[[@3,19:30='sales_amount',<381>,1:19]], units=[[@5,33:37='units',<381>,1:33]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query0={query_dictionary={month_name=[[@1,7:16='month_name',<381>,1:7]], sales_amount=[[@3,19:30='sales_amount',<381>,1:19]], units=[[@5,33:37='units',<381>,1:33]]}, table_dictionary={monthly_sales={jan_sales=[[@15,100:108='jan_sales',<381>,3:41]], mar_sales=[[@23,140:148='mar_sales',<381>,3:81]], units=[[@5,33:37='units',<381>,1:33], [@31,182:186='units',<381>,4:21], [@42,230:234='units',<381>,5:35]], feb_sales=[[@19,120:128='feb_sales',<381>,3:61]]}}, grouped_by=[{name=month_name, table_ref=null}, {name=sales_amount, table_ref=null}, {name=units, table_ref=monthly_sales}], ordered_by=[{name=sales_amount, table_ref=null}], filters=[{name=sales_amount, table_ref=null}, {name=units, table_ref=monthly_sales}], interface={month_name=[{name=month_name, table_ref=monthly_sales}], sales_amount=[{name=jan_sales, table_ref=monthly_sales}, {name=feb_sales, table_ref=monthly_sales}, {name=mar_sales, table_ref=monthly_sales}], units=[{name=units, table_ref=monthly_sales}]}, derived_columns={month_name=[{name=jan_sales, table_ref=monthly_sales}, {name=feb_sales, table_ref=monthly_sales}, {name=mar_sales, table_ref=monthly_sales}], sales_amount=[{name=jan_sales, table_ref=monthly_sales}, {name=feb_sales, table_ref=monthly_sales}, {name=mar_sales, table_ref=monthly_sales}]}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void unpivotTableWithGroupByAndOrderBySalesAmountV2GroupOrderWithTabAliasTest() {
		final String query =
			"SELECT month_name, sales_amount, units \n" +
			"FROM monthly_sales\n" +
			"UNPIVOT (sales_amount FOR month_name IN (jan_sales AS 'JAN', feb_sales AS 'FEB', mar_sales AS 'MAR')) unpvt\n" +
			"WHERE sales_amount / units > 1.00\n" +
			"GROUP BY month_name, sales_amount, units\n" +
			"ORDER BY sales_amount;";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=month_name, table_ref=null}}, 2={column={name=sales_amount, table_ref=null}}, 3={column={name=units, table_ref=null}}}, orderby={1={null_order=null, predicand={column={name=sales_amount, table_ref=null}}, sort_order=ASC}}, from={unpivot={value=sales_amount, for=month_name, in={1={name=jan_sales, label='JAN', table_ref=null}, 2={name=feb_sales, label='FEB', table_ref=null}, 3={name=mar_sales, label='MAR', table_ref=null}}}, alias=unpvt, table={alias=null, table=monthly_sales}}, where={condition={left={calc={left={column={name=sales_amount, table_ref=null}}, right={column={name=units, table_ref=null}}, operator=/}}, right={literal=1.00}, operator=>}}, groupby={1={column={name=month_name, table_ref=null}}, 2={column={name=sales_amount, table_ref=null}}, 3={column={name=units, table_ref=null}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[month_name, sales_amount, units]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{monthly_sales={jan_sales=[[@15,100:108='jan_sales',<381>,3:41]], mar_sales=[[@23,140:148='mar_sales',<381>,3:81]], units=[[@5,33:37='units',<381>,1:33], [@32,188:192='units',<381>,4:21], [@43,236:240='units',<381>,5:35]], feb_sales=[[@19,120:128='feb_sales',<381>,3:61]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={month_name=[[@1,7:16='month_name',<381>,1:7]], sales_amount=[[@3,19:30='sales_amount',<381>,1:19]], units=[[@5,33:37='units',<381>,1:33]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query0={query_dictionary={month_name=[[@1,7:16='month_name',<381>,1:7]], sales_amount=[[@3,19:30='sales_amount',<381>,1:19]], units=[[@5,33:37='units',<381>,1:33]]}, table_dictionary={monthly_sales={jan_sales=[[@15,100:108='jan_sales',<381>,3:41]], mar_sales=[[@23,140:148='mar_sales',<381>,3:81]], units=[[@5,33:37='units',<381>,1:33], [@32,188:192='units',<381>,4:21], [@43,236:240='units',<381>,5:35]], feb_sales=[[@19,120:128='feb_sales',<381>,3:61]]}}, grouped_by=[{name=month_name, table_ref=null}, {name=sales_amount, table_ref=null}, {name=units, table_ref=monthly_sales}], ordered_by=[{name=sales_amount, table_ref=null}], filters=[{name=sales_amount, table_ref=null}, {name=units, table_ref=monthly_sales}], interface={month_name=[{name=month_name, table_ref=monthly_sales}], sales_amount=[{name=jan_sales, table_ref=unpvt}, {name=feb_sales, table_ref=unpvt}, {name=mar_sales, table_ref=unpvt}], units=[{name=units, table_ref=monthly_sales}]}, derived_columns={month_name=[{name=jan_sales, table_ref=unpvt}, {name=feb_sales, table_ref=unpvt}, {name=mar_sales, table_ref=unpvt}], sales_amount=[{name=jan_sales, table_ref=unpvt}, {name=feb_sales, table_ref=unpvt}, {name=mar_sales, table_ref=unpvt}]}, table_alias={unpvt=monthly_sales}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void unpivotTableWithHavingAndOrderBySalesAmountV2HavingOrderTest() {
		final String query =
			"SELECT month_name, sales_amount, units \n" +
			"FROM monthly_sales\n" +
			"UNPIVOT (sales_amount FOR month_name IN (jan_sales AS 'JAN', feb_sales AS 'FEB', mar_sales AS 'MAR'))\n" +
			"GROUP BY month_name, sales_amount, units\n" +
			"HAVING sales_amount > 100\n" +
			"ORDER BY sales_amount;";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=month_name, table_ref=null}}, 2={column={name=sales_amount, table_ref=null}}, 3={column={name=units, table_ref=null}}}, having={condition={left={column={name=sales_amount, table_ref=null}}, right={literal=100}, operator=>}}, orderby={1={null_order=null, predicand={column={name=sales_amount, table_ref=null}}, sort_order=ASC}}, from={unpivot={value=sales_amount, for=month_name, in={1={name=jan_sales, label='JAN', table_ref=null}, 2={name=feb_sales, label='FEB', table_ref=null}, 3={name=mar_sales, label='MAR', table_ref=null}}}, table={alias=null, table=monthly_sales}}, groupby={1={column={name=month_name, table_ref=null}}, 2={column={name=sales_amount, table_ref=null}}, 3={column={name=units, table_ref=null}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[month_name, sales_amount, units]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{monthly_sales={jan_sales=[[@15,100:108='jan_sales',<381>,3:41]], mar_sales=[[@23,140:148='mar_sales',<381>,3:81]], units=[[@5,33:37='units',<381>,1:33], [@34,196:200='units',<381>,4:35]], feb_sales=[[@19,120:128='feb_sales',<381>,3:61]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={month_name=[[@1,7:16='month_name',<381>,1:7]], sales_amount=[[@3,19:30='sales_amount',<381>,1:19]], units=[[@5,33:37='units',<381>,1:33]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query0={query_dictionary={month_name=[[@1,7:16='month_name',<381>,1:7]], sales_amount=[[@3,19:30='sales_amount',<381>,1:19]], units=[[@5,33:37='units',<381>,1:33]]}, table_dictionary={monthly_sales={jan_sales=[[@15,100:108='jan_sales',<381>,3:41]], mar_sales=[[@23,140:148='mar_sales',<381>,3:81]], units=[[@5,33:37='units',<381>,1:33], [@34,196:200='units',<381>,4:35]], feb_sales=[[@19,120:128='feb_sales',<381>,3:61]]}}, grouped_by=[{name=month_name, table_ref=null}, {name=sales_amount, table_ref=null}, {name=units, table_ref=monthly_sales}], ordered_by=[{name=sales_amount, table_ref=null}], filters=[{name=sales_amount, table_ref=null}], interface={month_name=[{name=month_name, table_ref=monthly_sales}], sales_amount=[{name=jan_sales, table_ref=monthly_sales}, {name=feb_sales, table_ref=monthly_sales}, {name=mar_sales, table_ref=monthly_sales}], units=[{name=units, table_ref=monthly_sales}]}, derived_columns={month_name=[{name=jan_sales, table_ref=monthly_sales}, {name=feb_sales, table_ref=monthly_sales}, {name=mar_sales, table_ref=monthly_sales}], sales_amount=[{name=jan_sales, table_ref=monthly_sales}, {name=feb_sales, table_ref=monthly_sales}, {name=mar_sales, table_ref=monthly_sales}]}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void unpivotTableWithHavingAndOrderBySalesAmountV2HavingOrderWithTabAliasTest() {
		final String query =
			"SELECT month_name, sales_amount, units \n" +
			"FROM monthly_sales\n" +
			"UNPIVOT (sales_amount FOR month_name IN (jan_sales AS 'JAN', feb_sales AS 'FEB', mar_sales AS 'MAR')) unpvt\n" +
			"GROUP BY month_name, sales_amount, units\n" +
			"HAVING sales_amount > 100\n" +
			"ORDER BY sales_amount;";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=month_name, table_ref=null}}, 2={column={name=sales_amount, table_ref=null}}, 3={column={name=units, table_ref=null}}}, having={condition={left={column={name=sales_amount, table_ref=null}}, right={literal=100}, operator=>}}, orderby={1={null_order=null, predicand={column={name=sales_amount, table_ref=null}}, sort_order=ASC}}, from={unpivot={value=sales_amount, for=month_name, in={1={name=jan_sales, label='JAN', table_ref=null}, 2={name=feb_sales, label='FEB', table_ref=null}, 3={name=mar_sales, label='MAR', table_ref=null}}}, alias=unpvt, table={alias=null, table=monthly_sales}}, groupby={1={column={name=month_name, table_ref=null}}, 2={column={name=sales_amount, table_ref=null}}, 3={column={name=units, table_ref=null}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[month_name, sales_amount, units]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{monthly_sales={jan_sales=[[@15,100:108='jan_sales',<381>,3:41]], mar_sales=[[@23,140:148='mar_sales',<381>,3:81]], units=[[@5,33:37='units',<381>,1:33], [@35,202:206='units',<381>,4:35]], feb_sales=[[@19,120:128='feb_sales',<381>,3:61]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={month_name=[[@1,7:16='month_name',<381>,1:7]], sales_amount=[[@3,19:30='sales_amount',<381>,1:19]], units=[[@5,33:37='units',<381>,1:33]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query0={query_dictionary={month_name=[[@1,7:16='month_name',<381>,1:7]], sales_amount=[[@3,19:30='sales_amount',<381>,1:19]], units=[[@5,33:37='units',<381>,1:33]]}, table_dictionary={monthly_sales={jan_sales=[[@15,100:108='jan_sales',<381>,3:41]], mar_sales=[[@23,140:148='mar_sales',<381>,3:81]], units=[[@5,33:37='units',<381>,1:33], [@35,202:206='units',<381>,4:35]], feb_sales=[[@19,120:128='feb_sales',<381>,3:61]]}}, grouped_by=[{name=month_name, table_ref=null}, {name=sales_amount, table_ref=null}, {name=units, table_ref=monthly_sales}], ordered_by=[{name=sales_amount, table_ref=null}], filters=[{name=sales_amount, table_ref=null}], interface={month_name=[{name=month_name, table_ref=monthly_sales}], sales_amount=[{name=jan_sales, table_ref=unpvt}, {name=feb_sales, table_ref=unpvt}, {name=mar_sales, table_ref=unpvt}], units=[{name=units, table_ref=monthly_sales}]}, derived_columns={month_name=[{name=jan_sales, table_ref=unpvt}, {name=feb_sales, table_ref=unpvt}, {name=mar_sales, table_ref=unpvt}], sales_amount=[{name=jan_sales, table_ref=unpvt}, {name=feb_sales, table_ref=unpvt}, {name=mar_sales, table_ref=unpvt}]}, table_alias={unpvt=monthly_sales}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void unpivotTableJoinOnWithUnqualifiedSalesAmountProbeTest() {
		final String query =
			"SELECT month_name, sales_amount, t.target_amount \n" +
			"FROM monthly_sales\n" +
			"UNPIVOT (sales_amount FOR month_name IN (jan_sales AS 'JAN', feb_sales AS 'FEB', mar_sales AS 'MAR'))\n" +
			"JOIN targets t ON sales_amount >= t.target_amount;";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=month_name, table_ref=null}}, 2={column={name=sales_amount, table_ref=null}}, 3={column={name=target_amount, table_ref=t}}}, from={join={1={unpivot={value=sales_amount, for=month_name, in={1={name=jan_sales, label='JAN', table_ref=null}, 2={name=feb_sales, label='FEB', table_ref=null}, 3={name=mar_sales, label='MAR', table_ref=null}}}, table={alias=null, table=monthly_sales}}, 2={join=JOIN, on={condition={left={column={name=sales_amount, table_ref=null}}, right={column={name=target_amount, table_ref=t}}, operator=>=}}}, 3={table={alias=t, table=targets}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[month_name, target_amount, sales_amount]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{monthly_sales={jan_sales=[[@17,110:118='jan_sales',<381>,3:41]], mar_sales=[[@25,150:158='mar_sales',<381>,3:81]], feb_sales=[[@21,130:138='feb_sales',<381>,3:61]]}, targets={target_amount=[[@5,33:33='t',<381>,1:33], [@36,205:205='t',<381>,4:34]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
		 "{query0={month_name=[[@1,7:16='month_name',<381>,1:7]], sales_amount=[[@3,19:30='sales_amount',<381>,1:19]], target_amount=[[@7,35:47='target_amount',<381>,1:35]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query0={query_dictionary={month_name=[[@1,7:16='month_name',<381>,1:7]], target_amount=[[@7,35:47='target_amount',<381>,1:35]], sales_amount=[[@3,19:30='sales_amount',<381>,1:19]]}, table_dictionary={monthly_sales={jan_sales=[[@17,110:118='jan_sales',<381>,3:41]], mar_sales=[[@25,150:158='mar_sales',<381>,3:81]], feb_sales=[[@21,130:138='feb_sales',<381>,3:61]]}, targets={target_amount=[[@5,33:33='t',<381>,1:33], [@36,205:205='t',<381>,4:34]]}}, filters=[{name=sales_amount, table_ref=null}, {name=target_amount, table_ref=t}], interface={month_name=[{name=month_name, table_ref=monthly_sales}], target_amount=[{name=target_amount, table_ref=t}], sales_amount=[{name=jan_sales, table_ref=monthly_sales}, {name=feb_sales, table_ref=monthly_sales}, {name=mar_sales, table_ref=monthly_sales}]}, derived_columns={month_name=[{name=jan_sales, table_ref=monthly_sales}, {name=feb_sales, table_ref=monthly_sales}, {name=mar_sales, table_ref=monthly_sales}], sales_amount=[{name=jan_sales, table_ref=monthly_sales}, {name=feb_sales, table_ref=monthly_sales}, {name=mar_sales, table_ref=monthly_sales}]}, table_alias={t=targets}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void unpivotTableJoinOnWithUnqualifiedSalesAmountProbeWithTabAliasTest() {
		final String query =
			"SELECT month_name, sales_amount, t.target_amount \n" +
			"FROM monthly_sales\n" +
			"UNPIVOT (sales_amount FOR month_name IN (jan_sales AS 'JAN', feb_sales AS 'FEB', mar_sales AS 'MAR')) unpvt\n" +
			"JOIN targets t ON sales_amount >= t.target_amount;";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=month_name, table_ref=null}}, 2={column={name=sales_amount, table_ref=null}}, 3={column={name=target_amount, table_ref=t}}}, from={join={1={unpivot={value=sales_amount, for=month_name, in={1={name=jan_sales, label='JAN', table_ref=null}, 2={name=feb_sales, label='FEB', table_ref=null}, 3={name=mar_sales, label='MAR', table_ref=null}}}, alias=unpvt, table={alias=null, table=monthly_sales}}, 2={join=JOIN, on={condition={left={column={name=sales_amount, table_ref=null}}, right={column={name=target_amount, table_ref=t}}, operator=>=}}}, 3={table={alias=t, table=targets}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[month_name, target_amount, sales_amount]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{monthly_sales={jan_sales=[[@17,110:118='jan_sales',<381>,3:41]], mar_sales=[[@25,150:158='mar_sales',<381>,3:81]], feb_sales=[[@21,130:138='feb_sales',<381>,3:61]]}, targets={target_amount=[[@5,33:33='t',<381>,1:33], [@37,211:211='t',<381>,4:34]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
		 "{query0={month_name=[[@1,7:16='month_name',<381>,1:7]], sales_amount=[[@3,19:30='sales_amount',<381>,1:19]], target_amount=[[@7,35:47='target_amount',<381>,1:35]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query0={query_dictionary={month_name=[[@1,7:16='month_name',<381>,1:7]], target_amount=[[@7,35:47='target_amount',<381>,1:35]], sales_amount=[[@3,19:30='sales_amount',<381>,1:19]]}, table_dictionary={monthly_sales={jan_sales=[[@17,110:118='jan_sales',<381>,3:41]], mar_sales=[[@25,150:158='mar_sales',<381>,3:81]], feb_sales=[[@21,130:138='feb_sales',<381>,3:61]]}, targets={target_amount=[[@5,33:33='t',<381>,1:33], [@37,211:211='t',<381>,4:34]]}}, filters=[{name=sales_amount, table_ref=null}, {name=target_amount, table_ref=t}], interface={month_name=[{name=month_name, table_ref=unpvt}], target_amount=[{name=target_amount, table_ref=t}], sales_amount=[{name=jan_sales, table_ref=unpvt}, {name=feb_sales, table_ref=unpvt}, {name=mar_sales, table_ref=unpvt}]}, derived_columns={month_name=[{name=jan_sales, table_ref=unpvt}, {name=feb_sales, table_ref=unpvt}, {name=mar_sales, table_ref=unpvt}], sales_amount=[{name=jan_sales, table_ref=unpvt}, {name=feb_sales, table_ref=unpvt}, {name=mar_sales, table_ref=unpvt}]}, table_alias={unpvt=monthly_sales, t=targets}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void unpivotTableWithQualifySalesAmountProbeTest() {
		final String query =
			"SELECT month_name, sales_amount, units \n" +
			"FROM monthly_sales\n" +
			"UNPIVOT (sales_amount FOR month_name IN (jan_sales AS 'JAN', feb_sales AS 'FEB', mar_sales AS 'MAR'))\n" +
			"QUALIFY sales_amount > 100;";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=month_name, table_ref=null}}, 2={column={name=sales_amount, table_ref=null}}, 3={column={name=units, table_ref=null}}}, from={unpivot={value=sales_amount, for=month_name, in={1={name=jan_sales, label='JAN', table_ref=null}, 2={name=feb_sales, label='FEB', table_ref=null}, 3={name=mar_sales, label='MAR', table_ref=null}}}, table={alias=null, table=monthly_sales}}, qualify={condition={left={column={name=sales_amount, table_ref=null}}, right={literal=100}, operator=>}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[month_name, sales_amount, units]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{monthly_sales={jan_sales=[[@15,100:108='jan_sales',<381>,3:41]], mar_sales=[[@23,140:148='mar_sales',<381>,3:81]], units=[[@5,33:37='units',<381>,1:33]], feb_sales=[[@19,120:128='feb_sales',<381>,3:61]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={month_name=[[@1,7:16='month_name',<381>,1:7]], sales_amount=[[@3,19:30='sales_amount',<381>,1:19]], units=[[@5,33:37='units',<381>,1:33]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query0={query_dictionary={month_name=[[@1,7:16='month_name',<381>,1:7]], sales_amount=[[@3,19:30='sales_amount',<381>,1:19]], units=[[@5,33:37='units',<381>,1:33]]}, table_dictionary={monthly_sales={jan_sales=[[@15,100:108='jan_sales',<381>,3:41]], mar_sales=[[@23,140:148='mar_sales',<381>,3:81]], units=[[@5,33:37='units',<381>,1:33]], feb_sales=[[@19,120:128='feb_sales',<381>,3:61]]}}, filters=[{name=sales_amount, table_ref=null}], interface={month_name=[{name=month_name, table_ref=monthly_sales}], sales_amount=[{name=jan_sales, table_ref=monthly_sales}, {name=feb_sales, table_ref=monthly_sales}, {name=mar_sales, table_ref=monthly_sales}], units=[{name=units, table_ref=monthly_sales}]}, derived_columns={month_name=[{name=jan_sales, table_ref=monthly_sales}, {name=feb_sales, table_ref=monthly_sales}, {name=mar_sales, table_ref=monthly_sales}], sales_amount=[{name=jan_sales, table_ref=monthly_sales}, {name=feb_sales, table_ref=monthly_sales}, {name=mar_sales, table_ref=monthly_sales}]}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void unpivotTableWithQualifySalesAmountProbeWithTabAliasTest() {
		final String query =
			"SELECT month_name, sales_amount, units \n" +
			"FROM monthly_sales\n" +
			"UNPIVOT (sales_amount FOR month_name IN (jan_sales AS 'JAN', feb_sales AS 'FEB', mar_sales AS 'MAR')) unpvt\n" +
			"QUALIFY sales_amount > 100;";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=month_name, table_ref=null}}, 2={column={name=sales_amount, table_ref=null}}, 3={column={name=units, table_ref=null}}}, from={unpivot={value=sales_amount, for=month_name, in={1={name=jan_sales, label='JAN', table_ref=null}, 2={name=feb_sales, label='FEB', table_ref=null}, 3={name=mar_sales, label='MAR', table_ref=null}}}, alias=unpvt, table={alias=null, table=monthly_sales}}, qualify={condition={left={column={name=sales_amount, table_ref=null}}, right={literal=100}, operator=>}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[month_name, sales_amount, units]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{monthly_sales={jan_sales=[[@15,100:108='jan_sales',<381>,3:41]], mar_sales=[[@23,140:148='mar_sales',<381>,3:81]], units=[[@5,33:37='units',<381>,1:33]], feb_sales=[[@19,120:128='feb_sales',<381>,3:61]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={month_name=[[@1,7:16='month_name',<381>,1:7]], sales_amount=[[@3,19:30='sales_amount',<381>,1:19]], units=[[@5,33:37='units',<381>,1:33]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query0={query_dictionary={month_name=[[@1,7:16='month_name',<381>,1:7]], sales_amount=[[@3,19:30='sales_amount',<381>,1:19]], units=[[@5,33:37='units',<381>,1:33]]}, table_dictionary={monthly_sales={jan_sales=[[@15,100:108='jan_sales',<381>,3:41]], mar_sales=[[@23,140:148='mar_sales',<381>,3:81]], units=[[@5,33:37='units',<381>,1:33]], feb_sales=[[@19,120:128='feb_sales',<381>,3:61]]}}, filters=[{name=sales_amount, table_ref=null}], interface={month_name=[{name=month_name, table_ref=monthly_sales}], sales_amount=[{name=jan_sales, table_ref=unpvt}, {name=feb_sales, table_ref=unpvt}, {name=mar_sales, table_ref=unpvt}], units=[{name=units, table_ref=monthly_sales}]}, derived_columns={month_name=[{name=jan_sales, table_ref=unpvt}, {name=feb_sales, table_ref=unpvt}, {name=mar_sales, table_ref=unpvt}], sales_amount=[{name=jan_sales, table_ref=unpvt}, {name=feb_sales, table_ref=unpvt}, {name=mar_sales, table_ref=unpvt}]}, table_alias={unpvt=monthly_sales}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void unpivotTableWithOrderByExpressionSalesAmountProbeTest() {
		final String query =
			"SELECT month_name, sales_amount, units \n" +
			"FROM monthly_sales\n" +
			"UNPIVOT (sales_amount FOR month_name IN (jan_sales AS 'JAN', feb_sales AS 'FEB', mar_sales AS 'MAR'))\n" +
			"ORDER BY sales_amount / units;";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=month_name, table_ref=null}}, 2={column={name=sales_amount, table_ref=null}}, 3={column={name=units, table_ref=null}}}, orderby={1={null_order=null, predicand={calc={left={column={name=sales_amount, table_ref=null}}, right={column={name=units, table_ref=null}}, operator=/}}, sort_order=ASC}}, from={unpivot={value=sales_amount, for=month_name, in={1={name=jan_sales, label='JAN', table_ref=null}, 2={name=feb_sales, label='FEB', table_ref=null}, 3={name=mar_sales, label='MAR', table_ref=null}}}, table={alias=null, table=monthly_sales}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[month_name, sales_amount, units]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{monthly_sales={jan_sales=[[@15,100:108='jan_sales',<381>,3:41]], mar_sales=[[@23,140:148='mar_sales',<381>,3:81]], units=[[@5,33:37='units',<381>,1:33], [@32,185:189='units',<381>,4:24]], feb_sales=[[@19,120:128='feb_sales',<381>,3:61]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={month_name=[[@1,7:16='month_name',<381>,1:7]], sales_amount=[[@3,19:30='sales_amount',<381>,1:19]], units=[[@5,33:37='units',<381>,1:33]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query0={query_dictionary={month_name=[[@1,7:16='month_name',<381>,1:7]], sales_amount=[[@3,19:30='sales_amount',<381>,1:19]], units=[[@5,33:37='units',<381>,1:33]]}, table_dictionary={monthly_sales={jan_sales=[[@15,100:108='jan_sales',<381>,3:41]], mar_sales=[[@23,140:148='mar_sales',<381>,3:81]], units=[[@5,33:37='units',<381>,1:33], [@32,185:189='units',<381>,4:24]], feb_sales=[[@19,120:128='feb_sales',<381>,3:61]]}}, ordered_by=[{name=sales_amount, table_ref=null}, {name=units, table_ref=monthly_sales}], interface={month_name=[{name=month_name, table_ref=monthly_sales}], sales_amount=[{name=jan_sales, table_ref=monthly_sales}, {name=feb_sales, table_ref=monthly_sales}, {name=mar_sales, table_ref=monthly_sales}], units=[{name=units, table_ref=monthly_sales}]}, derived_columns={month_name=[{name=jan_sales, table_ref=monthly_sales}, {name=feb_sales, table_ref=monthly_sales}, {name=mar_sales, table_ref=monthly_sales}], sales_amount=[{name=jan_sales, table_ref=monthly_sales}, {name=feb_sales, table_ref=monthly_sales}, {name=mar_sales, table_ref=monthly_sales}]}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void unpivotTableWithOrderByExpressionSalesAmountProbeWithTabAliasTest() {
		final String query =
			"SELECT month_name, sales_amount, units \n" +
			"FROM monthly_sales\n" +
			"UNPIVOT (sales_amount FOR month_name IN (jan_sales AS 'JAN', feb_sales AS 'FEB', mar_sales AS 'MAR')) unpvt\n" +
			"ORDER BY sales_amount / units;";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=month_name, table_ref=null}}, 2={column={name=sales_amount, table_ref=null}}, 3={column={name=units, table_ref=null}}}, orderby={1={null_order=null, predicand={calc={left={column={name=sales_amount, table_ref=null}}, right={column={name=units, table_ref=null}}, operator=/}}, sort_order=ASC}}, from={unpivot={value=sales_amount, for=month_name, in={1={name=jan_sales, label='JAN', table_ref=null}, 2={name=feb_sales, label='FEB', table_ref=null}, 3={name=mar_sales, label='MAR', table_ref=null}}}, alias=unpvt, table={alias=null, table=monthly_sales}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[month_name, sales_amount, units]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{monthly_sales={jan_sales=[[@15,100:108='jan_sales',<381>,3:41]], mar_sales=[[@23,140:148='mar_sales',<381>,3:81]], units=[[@5,33:37='units',<381>,1:33], [@33,191:195='units',<381>,4:24]], feb_sales=[[@19,120:128='feb_sales',<381>,3:61]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={month_name=[[@1,7:16='month_name',<381>,1:7]], sales_amount=[[@3,19:30='sales_amount',<381>,1:19]], units=[[@5,33:37='units',<381>,1:33]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query0={query_dictionary={month_name=[[@1,7:16='month_name',<381>,1:7]], sales_amount=[[@3,19:30='sales_amount',<381>,1:19]], units=[[@5,33:37='units',<381>,1:33]]}, table_dictionary={monthly_sales={jan_sales=[[@15,100:108='jan_sales',<381>,3:41]], mar_sales=[[@23,140:148='mar_sales',<381>,3:81]], units=[[@5,33:37='units',<381>,1:33], [@33,191:195='units',<381>,4:24]], feb_sales=[[@19,120:128='feb_sales',<381>,3:61]]}}, ordered_by=[{name=sales_amount, table_ref=null}, {name=units, table_ref=monthly_sales}], interface={month_name=[{name=month_name, table_ref=monthly_sales}], sales_amount=[{name=jan_sales, table_ref=unpvt}, {name=feb_sales, table_ref=unpvt}, {name=mar_sales, table_ref=unpvt}], units=[{name=units, table_ref=monthly_sales}]}, derived_columns={month_name=[{name=jan_sales, table_ref=unpvt}, {name=feb_sales, table_ref=unpvt}, {name=mar_sales, table_ref=unpvt}], sales_amount=[{name=jan_sales, table_ref=unpvt}, {name=feb_sales, table_ref=unpvt}, {name=mar_sales, table_ref=unpvt}]}, table_alias={unpvt=monthly_sales}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void unpivotFromDerivedAdjustedColumnsV3Test() {
		final String query =
			"SELECT empid, month_name, sales_amount\n" +
			"FROM (SELECT empid, jan_sales * 1.10 AS jan_adjusted, feb_sales * 1.10 AS feb_adjusted FROM monthly_sales)\n" +
			"UNPIVOT (sales_amount FOR month_name IN (jan_adjusted AS 'JAN', feb_adjusted AS 'FEB'));";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=empid, table_ref=null}}, 2={column={name=month_name, table_ref=null}}, 3={column={name=sales_amount, table_ref=null}}}, from={unpivot={value=sales_amount, for=month_name, in={1={name=jan_adjusted, label='JAN', table_ref=null}, 2={name=feb_adjusted, label='FEB', table_ref=null}}}, select={1={column={name=empid, table_ref=null}}, 2={alias=jan_adjusted, calc={left={column={name=jan_sales, table_ref=null}}, right={literal=1.10}, operator=*}}, 3={alias=feb_adjusted, calc={left={column={name=feb_sales, table_ref=null}}, right={literal=1.10}, operator=*}}}, from={table={alias=null, table=monthly_sales}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[empid, month_name, sales_amount]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{monthly_sales={jan_sales=[[@11,59:67='jan_sales',<381>,2:20]], empid=[[@9,52:56='empid',<381>,2:13]], feb_sales=[[@19,93:101='feb_sales',<381>,2:54]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={empid=[[@9,52:56='empid',<381>,2:13], [@1,7:11='empid',<381>,1:7]], feb_adjusted=[[@25,113:124='feb_adjusted',<381>,2:74], [@40,210:221='feb_adjusted',<381>,3:64]], jan_adjusted=[[@17,79:90='jan_adjusted',<381>,2:40], [@36,187:198='jan_adjusted',<381>,3:41]]}, query1={empid=[[@1,7:11='empid',<381>,1:7]], month_name=[[@3,14:23='month_name',<381>,1:14]], sales_amount=[[@5,26:37='sales_amount',<381>,1:26]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query1={query_dictionary={empid=[[@1,7:11='empid',<381>,1:7]], month_name=[[@3,14:23='month_name',<381>,1:14]], sales_amount=[[@5,26:37='sales_amount',<381>,1:26]]}, def_query0={query_dictionary={empid=[[@9,52:56='empid',<381>,2:13], [@1,7:11='empid',<381>,1:7]], feb_adjusted=[[@25,113:124='feb_adjusted',<381>,2:74], [@40,210:221='feb_adjusted',<381>,3:64]], jan_adjusted=[[@17,79:90='jan_adjusted',<381>,2:40], [@36,187:198='jan_adjusted',<381>,3:41]]}, table_dictionary={monthly_sales={jan_sales=[[@11,59:67='jan_sales',<381>,2:20]], empid=[[@9,52:56='empid',<381>,2:13]], feb_sales=[[@19,93:101='feb_sales',<381>,2:54]]}}, interface={empid=[{name=empid, table_ref=monthly_sales}], feb_adjusted=[{name=feb_sales, table_ref=monthly_sales}], jan_adjusted=[{name=jan_sales, table_ref=monthly_sales}]}}, interface={empid=[{name=empid, table_ref=query0}], month_name=[{name=month_name, table_ref=null}], sales_amount=[{name=sales_amount, table_ref=null}]}, derived_columns={month_name=[], sales_amount=[]}, table_alias={query0=query0}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void unpivotWithTaxAndWhereV4Test() {
		final String query =
			"SELECT empid, month_name, sales_amount, sales_amount * 0.07 AS tax\n" +
			"FROM monthly_sales\n" +
			"UNPIVOT (sales_amount FOR month_name IN (jan_sales, feb_sales, mar_sales)) WHERE sales_amount > 100;";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=empid, table_ref=null}}, 2={column={name=month_name, table_ref=null}}, 3={column={name=sales_amount, table_ref=null}}, 4={alias=tax, calc={left={column={name=sales_amount, table_ref=null}}, right={literal=0.07}, operator=*}}}, from={unpivot={value=sales_amount, for=month_name, in={1={name=jan_sales, table_ref=null}, 2={name=feb_sales, table_ref=null}, 3={name=mar_sales, table_ref=null}}}, table={alias=null, table=monthly_sales}}, where={condition={left={column={name=sales_amount, table_ref=null}}, right={literal=100}, operator=>}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[empid, month_name, sales_amount, tax]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{monthly_sales={jan_sales=[[@23,127:135='jan_sales',<381>,3:41]], mar_sales=[[@27,149:157='mar_sales',<381>,3:63]], empid=[[@1,7:11='empid',<381>,1:7]], feb_sales=[[@25,138:146='feb_sales',<381>,3:52]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={empid=[[@1,7:11='empid',<381>,1:7]], month_name=[[@3,14:23='month_name',<381>,1:14]], sales_amount=[[@5,26:37='sales_amount',<381>,1:26]], tax=[[@13,63:65='tax',<381>,1:63]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query0={query_dictionary={empid=[[@1,7:11='empid',<381>,1:7]], month_name=[[@3,14:23='month_name',<381>,1:14]], sales_amount=[[@5,26:37='sales_amount',<381>,1:26]], tax=[[@13,63:65='tax',<381>,1:63]]}, table_dictionary={monthly_sales={jan_sales=[[@23,127:135='jan_sales',<381>,3:41]], mar_sales=[[@27,149:157='mar_sales',<381>,3:63]], empid=[[@1,7:11='empid',<381>,1:7]], feb_sales=[[@25,138:146='feb_sales',<381>,3:52]]}}, filters=[{name=sales_amount, table_ref=null}], interface={empid=[{name=empid, table_ref=monthly_sales}], month_name=[{name=month_name, table_ref=monthly_sales}], sales_amount=[{name=jan_sales, table_ref=monthly_sales}, {name=feb_sales, table_ref=monthly_sales}, {name=mar_sales, table_ref=monthly_sales}], tax=[{name=jan_sales, table_ref=monthly_sales}, {name=feb_sales, table_ref=monthly_sales}, {name=mar_sales, table_ref=monthly_sales}]}, derived_columns={month_name=[{name=jan_sales, table_ref=monthly_sales}, {name=feb_sales, table_ref=monthly_sales}, {name=mar_sales, table_ref=monthly_sales}], sales_amount=[{name=jan_sales, table_ref=monthly_sales}, {name=feb_sales, table_ref=monthly_sales}, {name=mar_sales, table_ref=monthly_sales}]}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void unpivotJoinTargetsWithFilterV5Test() {
		final String query =
			"SELECT empid, month_name, sales_amount, sales_amount * 0.07 AS tax\n" +
			"FROM monthly_sales UNPIVOT (sales_amount FOR month_name IN (jan_sales, feb_sales)) u\n" +
			"JOIN targets t ON u.month_name = t.month_name AND u.sales_amount >= t.target_amount WHERE sales_amount > 100;";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoFatalErrors(extractor);
		Assert.assertEquals("Walker Error diagnostics are wrong",
				"[Unresolved unqualified column reference(s): [empid [(l:1 c:7)]]]",
				extractor.getSnippet().getErrorStringList(errorhandling.ParseDiagnostic.Severity.ERROR).toString());
		assertDiagnosticAtPosition(
				extractor.getSnippet(),
				"AMBIGUOUS_COLUMN_REFERENCE",
				errorhandling.ParseDiagnostic.Severity.SEVERE_WARNING,
				"Ambiguous column reference 'empid'",
				"empid",
				1,
				7);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=empid, table_ref=null}}, 2={column={name=month_name, table_ref=null}}, 3={column={name=sales_amount, table_ref=null}}, 4={alias=tax, calc={left={column={name=sales_amount, table_ref=null}}, right={literal=0.07}, operator=*}}}, from={join={1={unpivot={value=sales_amount, for=month_name, in={1={name=jan_sales, table_ref=null}, 2={name=feb_sales, table_ref=null}}}, alias=u, table={alias=null, table=monthly_sales}}, 2={join=JOIN, on={and={1={condition={left={column={name=month_name, table_ref=u}}, right={column={name=month_name, table_ref=t}}, operator==}}, 2={condition={left={column={name=sales_amount, table_ref=u}}, right={column={name=target_amount, table_ref=t}}, operator=>=}}}}}, 3={table={alias=t, table=targets}}}}, where={condition={left={column={name=sales_amount, table_ref=null}}, right={literal=100}, operator=>}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[empid, month_name, sales_amount, tax]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{monthly_sales={jan_sales=[[@23,127:135='jan_sales',<381>,2:60]], feb_sales=[[@25,138:146='feb_sales',<381>,2:71]]}, targets={month_name=[[@37,185:185='t',<381>,3:33]], target_amount=[[@45,220:220='t',<381>,3:68]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={empid=[[@1,7:11='empid',<381>,1:7]], month_name=[[@3,14:23='month_name',<381>,1:14]], sales_amount=[[@5,26:37='sales_amount',<381>,1:26]], tax=[[@13,63:65='tax',<381>,1:63]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query0={query_dictionary={empid=[[@1,7:11='empid',<381>,1:7]], month_name=[[@3,14:23='month_name',<381>,1:14]], sales_amount=[[@5,26:37='sales_amount',<381>,1:26]], tax=[[@13,63:65='tax',<381>,1:63]]}, table_dictionary={monthly_sales={jan_sales=[[@23,127:135='jan_sales',<381>,2:60]], feb_sales=[[@25,138:146='feb_sales',<381>,2:71]]}, targets={month_name=[[@37,185:185='t',<381>,3:33]], target_amount=[[@45,220:220='t',<381>,3:68]]}}, filters=[{name=month_name, table_ref=u}, {name=month_name, table_ref=t}, {name=sales_amount, table_ref=u}, {name=target_amount, table_ref=t}, {name=sales_amount, table_ref=null}], interface={empid=[{name=empid, table_ref=null}], month_name=[{name=month_name, table_ref=u}], sales_amount=[{name=jan_sales, table_ref=u}, {name=feb_sales, table_ref=u}], tax=[{name=jan_sales, table_ref=u}, {name=feb_sales, table_ref=u}]}, derived_columns={month_name=[{name=jan_sales, table_ref=u}, {name=feb_sales, table_ref=u}], sales_amount=[{name=jan_sales, table_ref=u}, {name=feb_sales, table_ref=u}]}, table_alias={u=monthly_sales, t=targets}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void unpivotKeepingOriginalMonthColumnsV6Test() {
		final String query =
			"SELECT empid, jan_sales, feb_sales, month_name, sales_amount\n" +
			"FROM monthly_sales\n" +
			"UNPIVOT (sales_amount FOR month_name IN (jan_sales, feb_sales, mar_sales));";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=empid, table_ref=null}}, 2={column={name=jan_sales, table_ref=null}}, 3={column={name=feb_sales, table_ref=null}}, 4={column={name=month_name, table_ref=null}}, 5={column={name=sales_amount, table_ref=null}}}, from={unpivot={value=sales_amount, for=month_name, in={1={name=jan_sales, table_ref=null}, 2={name=feb_sales, table_ref=null}, 3={name=mar_sales, table_ref=null}}}, table={alias=null, table=monthly_sales}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[jan_sales, empid, month_name, sales_amount, feb_sales]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{monthly_sales={jan_sales=[[@19,121:129='jan_sales',<381>,3:41], [@3,14:22='jan_sales',<381>,1:14]], mar_sales=[[@23,143:151='mar_sales',<381>,3:63]], empid=[[@1,7:11='empid',<381>,1:7]], feb_sales=[[@21,132:140='feb_sales',<381>,3:52], [@5,25:33='feb_sales',<381>,1:25]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={jan_sales=[[@3,14:22='jan_sales',<381>,1:14]], empid=[[@1,7:11='empid',<381>,1:7]], month_name=[[@7,36:45='month_name',<381>,1:36]], sales_amount=[[@9,48:59='sales_amount',<381>,1:48]], feb_sales=[[@5,25:33='feb_sales',<381>,1:25]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query0={query_dictionary={jan_sales=[[@3,14:22='jan_sales',<381>,1:14]], empid=[[@1,7:11='empid',<381>,1:7]], month_name=[[@7,36:45='month_name',<381>,1:36]], sales_amount=[[@9,48:59='sales_amount',<381>,1:48]], feb_sales=[[@5,25:33='feb_sales',<381>,1:25]]}, table_dictionary={monthly_sales={jan_sales=[[@19,121:129='jan_sales',<381>,3:41], [@3,14:22='jan_sales',<381>,1:14]], mar_sales=[[@23,143:151='mar_sales',<381>,3:63]], empid=[[@1,7:11='empid',<381>,1:7]], feb_sales=[[@21,132:140='feb_sales',<381>,3:52], [@5,25:33='feb_sales',<381>,1:25]]}}, interface={jan_sales=[{name=jan_sales, table_ref=monthly_sales}], empid=[{name=empid, table_ref=monthly_sales}], month_name=[{name=month_name, table_ref=monthly_sales}], sales_amount=[{name=jan_sales, table_ref=monthly_sales}, {name=feb_sales, table_ref=monthly_sales}, {name=mar_sales, table_ref=monthly_sales}], feb_sales=[{name=feb_sales, table_ref=monthly_sales}]}, derived_columns={month_name=[{name=jan_sales, table_ref=monthly_sales}, {name=feb_sales, table_ref=monthly_sales}, {name=mar_sales, table_ref=monthly_sales}], sales_amount=[{name=jan_sales, table_ref=monthly_sales}, {name=feb_sales, table_ref=monthly_sales}, {name=mar_sales, table_ref=monthly_sales}]}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void unpivotBasicMonthSalesV7Test() {
		final String query =
			"SELECT empid, month_name, sales_amount\n" +
			"FROM monthly_sales\n" +
			"UNPIVOT (sales_amount FOR month_name IN (jan_sales, feb_sales, mar_sales));";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=empid, table_ref=null}}, 2={column={name=month_name, table_ref=null}}, 3={column={name=sales_amount, table_ref=null}}}, from={unpivot={value=sales_amount, for=month_name, in={1={name=jan_sales, table_ref=null}, 2={name=feb_sales, table_ref=null}, 3={name=mar_sales, table_ref=null}}}, table={alias=null, table=monthly_sales}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[empid, month_name, sales_amount]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{monthly_sales={jan_sales=[[@15,99:107='jan_sales',<381>,3:41]], mar_sales=[[@19,121:129='mar_sales',<381>,3:63]], empid=[[@1,7:11='empid',<381>,1:7]], feb_sales=[[@17,110:118='feb_sales',<381>,3:52]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={empid=[[@1,7:11='empid',<381>,1:7]], month_name=[[@3,14:23='month_name',<381>,1:14]], sales_amount=[[@5,26:37='sales_amount',<381>,1:26]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query0={query_dictionary={empid=[[@1,7:11='empid',<381>,1:7]], month_name=[[@3,14:23='month_name',<381>,1:14]], sales_amount=[[@5,26:37='sales_amount',<381>,1:26]]}, table_dictionary={monthly_sales={jan_sales=[[@15,99:107='jan_sales',<381>,3:41]], mar_sales=[[@19,121:129='mar_sales',<381>,3:63]], empid=[[@1,7:11='empid',<381>,1:7]], feb_sales=[[@17,110:118='feb_sales',<381>,3:52]]}}, interface={empid=[{name=empid, table_ref=monthly_sales}], month_name=[{name=month_name, table_ref=monthly_sales}], sales_amount=[{name=jan_sales, table_ref=monthly_sales}, {name=feb_sales, table_ref=monthly_sales}, {name=mar_sales, table_ref=monthly_sales}]}, derived_columns={month_name=[{name=jan_sales, table_ref=monthly_sales}, {name=feb_sales, table_ref=monthly_sales}, {name=mar_sales, table_ref=monthly_sales}], sales_amount=[{name=jan_sales, table_ref=monthly_sales}, {name=feb_sales, table_ref=monthly_sales}, {name=mar_sales, table_ref=monthly_sales}]}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void unpivotBasicMonthSalesV8Test() {
		final String query =
			"SELECT empid, month_name, sales_amount, t2.a1, t2.a2\n" +
			"FROM monthly_sales\n" +
			"UNPIVOT (sales_amount FOR month_name IN (jan_sales, feb_sales, mar_sales))\n" +
			"JOIN metrics_table t2 ON month_name = t2.metric_label;";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoFatalErrors(extractor);
		Assert.assertEquals("Walker Error diagnostics are wrong",
				"[Unresolved unqualified column reference(s): [empid [(l:1 c:7)]]]",
				extractor.getSnippet().getErrorStringList(errorhandling.ParseDiagnostic.Severity.ERROR).toString());
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=empid, table_ref=null}}, 2={column={name=month_name, table_ref=null}}, 3={column={name=sales_amount, table_ref=null}}, 4={column={name=a1, table_ref=t2}}, 5={column={name=a2, table_ref=t2}}}, from={join={1={unpivot={value=sales_amount, for=month_name, in={1={name=jan_sales, table_ref=null}, 2={name=feb_sales, table_ref=null}, 3={name=mar_sales, table_ref=null}}}, table={alias=null, table=monthly_sales}}, 2={join=JOIN, on={condition={left={column={name=month_name, table_ref=null}}, right={column={name=metric_label, table_ref=t2}}, operator==}}}, 3={table={alias=t2, table=metrics_table}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[a1, empid, month_name, a2, sales_amount]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{monthly_sales={jan_sales=[[@23,113:121='jan_sales',<381>,3:41]], mar_sales=[[@27,135:143='mar_sales',<381>,3:63]], feb_sales=[[@25,124:132='feb_sales',<381>,3:52]]}, metrics_table={a1=[[@7,40:41='t2',<381>,1:40]], a2=[[@11,47:48='t2',<381>,1:47]], metric_label=[[@36,185:186='t2',<381>,4:38]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={a1=[[@9,43:44='a1',<381>,1:43]], empid=[[@1,7:11='empid',<381>,1:7]], month_name=[[@3,14:23='month_name',<381>,1:14]], a2=[[@13,50:51='a2',<381>,1:50]], sales_amount=[[@5,26:37='sales_amount',<381>,1:26]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query0={query_dictionary={a1=[[@9,43:44='a1',<381>,1:43]], empid=[[@1,7:11='empid',<381>,1:7]], month_name=[[@3,14:23='month_name',<381>,1:14]], a2=[[@13,50:51='a2',<381>,1:50]], sales_amount=[[@5,26:37='sales_amount',<381>,1:26]]}, table_dictionary={monthly_sales={jan_sales=[[@23,113:121='jan_sales',<381>,3:41]], mar_sales=[[@27,135:143='mar_sales',<381>,3:63]], feb_sales=[[@25,124:132='feb_sales',<381>,3:52]]}, metrics_table={a1=[[@7,40:41='t2',<381>,1:40]], a2=[[@11,47:48='t2',<381>,1:47]], metric_label=[[@36,185:186='t2',<381>,4:38]]}}, filters=[{name=month_name, table_ref=null}, {name=metric_label, table_ref=t2}], interface={a1=[{name=a1, table_ref=t2}], empid=[{name=empid, table_ref=null}], month_name=[{name=month_name, table_ref=monthly_sales}], a2=[{name=a2, table_ref=t2}], sales_amount=[{name=jan_sales, table_ref=monthly_sales}, {name=feb_sales, table_ref=monthly_sales}, {name=mar_sales, table_ref=monthly_sales}]}, derived_columns={month_name=[{name=jan_sales, table_ref=monthly_sales}, {name=feb_sales, table_ref=monthly_sales}, {name=mar_sales, table_ref=monthly_sales}], sales_amount=[{name=jan_sales, table_ref=monthly_sales}, {name=feb_sales, table_ref=monthly_sales}, {name=mar_sales, table_ref=monthly_sales}]}, table_alias={t2=metrics_table}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void unpivotBasicMonthSalesV9Test() {
		final String query =
			"SELECT empid, month_name, sales_amount, t2.a1, t2.a2\n" +
			"FROM monthly_sales\n" +
			"UNPIVOT (sales_amount FOR month_name IN (jan_sales, feb_sales, mar_sales)) up\n" +
			"JOIN metrics_table t2 ON up.month_name = t2.metric_label;";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoFatalErrors(extractor);
		Assert.assertEquals("Walker Error diagnostics are wrong",
				"[Unresolved unqualified column reference(s): [empid [(l:1 c:7)]]]",
				extractor.getSnippet().getErrorStringList(errorhandling.ParseDiagnostic.Severity.ERROR).toString());
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=empid, table_ref=null}}, 2={column={name=month_name, table_ref=null}}, 3={column={name=sales_amount, table_ref=null}}, 4={column={name=a1, table_ref=t2}}, 5={column={name=a2, table_ref=t2}}}, from={join={1={unpivot={value=sales_amount, for=month_name, in={1={name=jan_sales, table_ref=null}, 2={name=feb_sales, table_ref=null}, 3={name=mar_sales, table_ref=null}}}, alias=up, table={alias=null, table=monthly_sales}}, 2={join=JOIN, on={condition={left={column={name=month_name, table_ref=up}}, right={column={name=metric_label, table_ref=t2}}, operator==}}}, 3={table={alias=t2, table=metrics_table}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[a1, empid, month_name, a2, sales_amount]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{monthly_sales={jan_sales=[[@23,113:121='jan_sales',<381>,3:41]], mar_sales=[[@27,135:143='mar_sales',<381>,3:63]], feb_sales=[[@25,124:132='feb_sales',<381>,3:52]]}, metrics_table={a1=[[@7,40:41='t2',<381>,1:40]], a2=[[@11,47:48='t2',<381>,1:47]], metric_label=[[@39,191:192='t2',<381>,4:41]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={a1=[[@9,43:44='a1',<381>,1:43]], empid=[[@1,7:11='empid',<381>,1:7]], month_name=[[@3,14:23='month_name',<381>,1:14]], a2=[[@13,50:51='a2',<381>,1:50]], sales_amount=[[@5,26:37='sales_amount',<381>,1:26]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query0={query_dictionary={a1=[[@9,43:44='a1',<381>,1:43]], empid=[[@1,7:11='empid',<381>,1:7]], month_name=[[@3,14:23='month_name',<381>,1:14]], a2=[[@13,50:51='a2',<381>,1:50]], sales_amount=[[@5,26:37='sales_amount',<381>,1:26]]}, table_dictionary={monthly_sales={jan_sales=[[@23,113:121='jan_sales',<381>,3:41]], mar_sales=[[@27,135:143='mar_sales',<381>,3:63]], feb_sales=[[@25,124:132='feb_sales',<381>,3:52]]}, metrics_table={a1=[[@7,40:41='t2',<381>,1:40]], a2=[[@11,47:48='t2',<381>,1:47]], metric_label=[[@39,191:192='t2',<381>,4:41]]}}, filters=[{name=month_name, table_ref=up}, {name=metric_label, table_ref=t2}], interface={a1=[{name=a1, table_ref=t2}], empid=[{name=empid, table_ref=null}], month_name=[{name=month_name, table_ref=up}], a2=[{name=a2, table_ref=t2}], sales_amount=[{name=jan_sales, table_ref=up}, {name=feb_sales, table_ref=up}, {name=mar_sales, table_ref=up}]}, derived_columns={month_name=[{name=jan_sales, table_ref=up}, {name=feb_sales, table_ref=up}, {name=mar_sales, table_ref=up}], sales_amount=[{name=jan_sales, table_ref=up}, {name=feb_sales, table_ref=up}, {name=mar_sales, table_ref=up}]}, table_alias={up=monthly_sales, t2=metrics_table}}}",
				extractor.getSymbolTable().toString());
	}

	// PIVOT RELATIONAL OPERATOR TESTS

	@Test
	public void pivotInIdentifierDirectTableFatalV1Test() {
		final String query = "select * from tab1 pivot (sum(col1) for col2 in (A))";

		final SQLSelectParserParser parser = parse(query);
		ParserRunResult runResult = runSQLParsertestAllowErrors(query, parser);
		SqlParseEventWalker extractor = runResult.getExtractor();

		Assert.assertNotNull(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=*, table_ref=*}}}, from={pivot={value={function={function_name=sum, parameters={column={name=col1, table_ref=null}}}}, for={column={name=col2, table_ref=null}}, in={1={pivot_literal=A}}}, table={alias=null, table=tab1}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{tab1={*=[[@1,7:7='*',<291>,1:7]], col2=[[@11,40:43='col2',<381>,1:40]], col1=[[@8,30:33='col1',<381>,1:30]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{query1={*=[[@1,7:7='*',<291>,1:7]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query1={query_dictionary={*=[[@1,7:7='*',<291>,1:7]]}, table_dictionary={tab1={*=[[@1,7:7='*',<291>,1:7]], col2=[[@11,40:43='col2',<381>,1:40]], col1=[[@8,30:33='col1',<381>,1:30]]}}, interface={*=[{name=*, table_ref=*}]}, derived_columns={A_sum=[{name=col1, table_ref=tab1}]}}}",
				extractor.getSymbolTable().toString());
		assertFatalDiagnosticCount(
				extractor.getSnippet(),
				"PIVOT_IN_IDENTIFIER_UNRESOLVED",
				"cannot be resolved against the PIVOT source",
				"A",
				1);
		Assert.assertEquals(
				"Identifier-form PIVOT IN value against a table should not get the reference warning",
				0,
				countDiagnosticsBySeverity(
						extractor.getSnippet(),
						"PIVOT_IN_IDENTIFIER_REFERENCE",
						errorhandling.ParseDiagnostic.Severity.SEVERE_WARNING,
						null,
						"A"));
	}

	@Test
	public void pivotInIdentifierMissingFromSubqueryFatalV1Test() {
		final String query = "select * from (select col1, col2 from tab1) q pivot (sum(col1) for col2 in (A))";

		final SQLSelectParserParser parser = parse(query);
		ParserRunResult runResult = runSQLParsertestAllowErrors(query, parser);
		SqlParseEventWalker extractor = runResult.getExtractor();

		Assert.assertNotNull(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=*, table_ref=*}}}, from={pivot={value={function={function_name=sum, parameters={column={name=col1, table_ref=null}}}}, for={column={name=col2, table_ref=null}}, in={1={pivot_literal=A}}}, table={alias=q, query={select={1={column={name=col1, table_ref=null}}, 2={column={name=col2, table_ref=null}}}, from={table={alias=null, table=tab1}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{tab1={col2=[[@7,28:31='col2',<381>,1:28]], col1=[[@5,22:25='col1',<381>,1:22]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{query0={col2=[[@7,28:31='col2',<381>,1:28], [@19,67:70='col2',<381>,1:67]], *=[[@1,7:7='*',<291>,1:7]], col1=[[@5,22:25='col1',<381>,1:22], [@16,57:60='col1',<381>,1:57]]}, query2={*=[[@1,7:7='*',<291>,1:7]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query2={query_dictionary={*=[[@1,7:7='*',<291>,1:7]]}, def_query0={query_dictionary={*=[[@1,7:7='*',<291>,1:7]], col2=[[@7,28:31='col2',<381>,1:28], [@19,67:70='col2',<381>,1:67]], col1=[[@5,22:25='col1',<381>,1:22], [@16,57:60='col1',<381>,1:57]]}, table_dictionary={tab1={col2=[[@7,28:31='col2',<381>,1:28]], col1=[[@5,22:25='col1',<381>,1:22]]}}, interface={col2=[{name=col2, table_ref=tab1}], col1=[{name=col1, table_ref=tab1}]}}, interface={*=[{name=*, table_ref=*}]}, derived_columns={A_sum=[{name=col1, table_ref=q}]}, table_alias={q=query0}}}",
				extractor.getSymbolTable().toString());
		assertFatalDiagnosticCount(
				extractor.getSnippet(),
				"PIVOT_IN_IDENTIFIER_UNRESOLVED",
				"cannot be resolved against the PIVOT source",
				"A",
				1);
		Assert.assertEquals(
				"Unresolved subquery PIVOT IN identifier should not get the reference warning",
				0,
				countDiagnosticsBySeverity(
						extractor.getSnippet(),
						"PIVOT_IN_IDENTIFIER_REFERENCE",
						errorhandling.ParseDiagnostic.Severity.SEVERE_WARNING,
						null,
						"A"));
	}

	@Test
	public void pivotInIdentifierResolvedFromSubqueryWarningV1Test() {
		final String query = "select * from (select col1, col2, 1 as A from tab1) q pivot (sum(col1) for col2 in (A))";

		final SQLSelectParserParser parser = parse(query);
		ParserRunResult runResult = runSQLParsertestAllowErrors(query, parser);
		SqlParseEventWalker extractor = runResult.getExtractor();

		assertNoFatalErrors(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=*, table_ref=*}}}, from={pivot={value={function={function_name=sum, parameters={column={name=col1, table_ref=null}}}}, for={column={name=col2, table_ref=null}}, in={1={pivot_literal=A}}}, table={alias=q, query={select={1={column={name=col1, table_ref=null}}, 2={column={name=col2, table_ref=null}}, 3={alias=A, literal=1}}, from={table={alias=null, table=tab1}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{tab1={col2=[[@7,28:31='col2',<381>,1:28]], col1=[[@5,22:25='col1',<381>,1:22]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{query0={A=[[@11,39:39='A',<381>,1:39]], *=[[@1,7:7='*',<291>,1:7]], col2=[[@7,28:31='col2',<381>,1:28], [@23,75:78='col2',<381>,1:75]], col1=[[@5,22:25='col1',<381>,1:22], [@20,65:68='col1',<381>,1:65]]}, query2={*=[[@1,7:7='*',<291>,1:7]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query2={query_dictionary={*=[[@1,7:7='*',<291>,1:7]]}, def_query0={query_dictionary={A=[[@11,39:39='A',<381>,1:39]], *=[[@1,7:7='*',<291>,1:7]], col2=[[@7,28:31='col2',<381>,1:28], [@23,75:78='col2',<381>,1:75]], col1=[[@5,22:25='col1',<381>,1:22], [@20,65:68='col1',<381>,1:65]]}, table_dictionary={tab1={col2=[[@7,28:31='col2',<381>,1:28]], col1=[[@5,22:25='col1',<381>,1:22]]}}, interface={A=[], col2=[{name=col2, table_ref=tab1}], col1=[{name=col1, table_ref=tab1}]}}, interface={*=[{name=*, table_ref=*}]}, derived_columns={A_sum=[{name=col1, table_ref=q}]}, table_alias={q=query0}}}",
				extractor.getSymbolTable().toString());
		Assert.assertEquals(
				"Resolved subquery PIVOT IN identifier should get exactly one warning",
				1,
				countDiagnosticsBySeverity(
						extractor.getSnippet(),
						"PIVOT_IN_IDENTIFIER_REFERENCE",
						errorhandling.ParseDiagnostic.Severity.SEVERE_WARNING,
						null,
						"A"));
		Assert.assertEquals(
				"Resolved subquery PIVOT IN identifier should not get the fatal diagnostic",
				0,
				countFatalDiagnostics(
						extractor.getSnippet(),
						"PIVOT_IN_IDENTIFIER_UNRESOLVED",
						null,
						"A"));
	}

	@Test
	public void pivotV1Tab1Test() {
		final String query = "select *, A_sum from tab1 "
		+"\n pivot (sum(col1), avg(col2), count(col3), max(col4), min(col5) "
		+"\n for col2 in (A, B))";

		final SQLSelectParserParser parser = parse(query);
		ParserRunResult runResult = runSQLParsertestAllowErrors(query, parser);
		SqlParseEventWalker extractor = runResult.getExtractor();
		Assert.assertNotNull(extractor);

		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=*, table_ref=*}}, 2={column={name=A_sum, table_ref=null}}}, from={pivot={value={1={function={function_name=sum, parameters={column={name=col1, table_ref=null}}}}, 2={function={function_name=avg, parameters={column={name=col2, table_ref=null}}}}, 3={function={function_name=count, parameters={column={name=col3, table_ref=null}}}}, 4={function={function_name=max, parameters={column={name=col4, table_ref=null}}}}, 5={function={function_name=min, parameters={column={name=col5, table_ref=null}}}}}, for={column={name=col2, table_ref=null}}, in={1={pivot_literal=A}, 2={pivot_literal=B}}}, table={alias=null, table=tab1}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[*, A_sum]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{tab1={*=[[@1,7:7='*',<291>,1:7]], col4=[[@25,74:77='col4',<381>,2:47]], col5=[[@30,85:88='col5',<381>,2:58]], col2=[[@15,50:53='col2',<381>,2:23], [@33,97:100='col2',<381>,3:5]], col3=[[@20,63:66='col3',<381>,2:36]], col1=[[@10,39:42='col1',<381>,2:12]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{query1={A_sum=[[@3,10:14='A_sum',<381>,1:10]], *=[[@1,7:7='*',<291>,1:7]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query1={query_dictionary={*=[[@1,7:7='*',<291>,1:7]], A_sum=[[@3,10:14='A_sum',<381>,1:10]]}, table_dictionary={tab1={*=[[@1,7:7='*',<291>,1:7]], col4=[[@25,74:77='col4',<381>,2:47]], col5=[[@30,85:88='col5',<381>,2:58]], col2=[[@15,50:53='col2',<381>,2:23], [@33,97:100='col2',<381>,3:5]], col3=[[@20,63:66='col3',<381>,2:36]], col1=[[@10,39:42='col1',<381>,2:12]]}}, interface={*=[{name=*, table_ref=*}], A_sum=[{name=A_sum, table_ref=null}]}, derived_columns={B_sum=[{name=col1, table_ref=tab1}], A_avg=[{name=col2, table_ref=tab1}], A_min=[{name=col5, table_ref=tab1}], A_max=[{name=col4, table_ref=tab1}], A_count=[{name=col3, table_ref=tab1}], B_max=[{name=col4, table_ref=tab1}], B_avg=[{name=col2, table_ref=tab1}], B_min=[{name=col5, table_ref=tab1}], B_count=[{name=col3, table_ref=tab1}], A_sum=[{name=col1, table_ref=tab1}]}}}",
				extractor.getSymbolTable().toString());
		assertFatalDiagnosticAtPosition(
				extractor.getSnippet(),
				"PIVOT_IN_IDENTIFIER_UNRESOLVED",
				"cannot be resolved against the PIVOT source",
				"A",
				3,
				14);
		assertFatalDiagnosticAtPosition(
				extractor.getSnippet(),
				"PIVOT_IN_IDENTIFIER_UNRESOLVED",
				"cannot be resolved against the PIVOT source",
				"B",
				3,
				17);
	}

	@Test
	public void pivotV1Tab1QuotedSelectorsSuccessTest() {
		final String query = "select *, A_sum from tab1 "
		+"\n pivot (sum(col1), avg(col2), count(col3), max(col4), min(col5) "
		+"\n for col2 in ('A', 'B'))";

		final SQLSelectParserParser parser = parse(query);
		ParserRunResult runResult = runSQLParsertestAllowErrors(query, parser);
		SqlParseEventWalker extractor = runResult.getExtractor();

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=*, table_ref=*}}, 2={column={name=A_sum, table_ref=null}}}, from={pivot={value={1={function={function_name=sum, parameters={column={name=col1, table_ref=null}}}}, 2={function={function_name=avg, parameters={column={name=col2, table_ref=null}}}}, 3={function={function_name=count, parameters={column={name=col3, table_ref=null}}}}, 4={function={function_name=max, parameters={column={name=col4, table_ref=null}}}}, 5={function={function_name=min, parameters={column={name=col5, table_ref=null}}}}}, for={column={name=col2, table_ref=null}}, in={1={pivot_literal='A'}, 2={pivot_literal='B'}}}, table={alias=null, table=tab1}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[*, A_sum]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{tab1={*=[[@1,7:7='*',<291>,1:7]], col4=[[@25,74:77='col4',<381>,2:47]], col5=[[@30,85:88='col5',<381>,2:58]], col2=[[@15,50:53='col2',<381>,2:23], [@33,97:100='col2',<381>,3:5]], col3=[[@20,63:66='col3',<381>,2:36]], col1=[[@10,39:42='col1',<381>,2:12]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{query1={A_sum=[[@3,10:14='A_sum',<381>,1:10]], *=[[@1,7:7='*',<291>,1:7]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query1={query_dictionary={*=[[@1,7:7='*',<291>,1:7]], A_sum=[[@3,10:14='A_sum',<381>,1:10]]}, table_dictionary={tab1={*=[[@1,7:7='*',<291>,1:7]], col4=[[@25,74:77='col4',<381>,2:47]], col5=[[@30,85:88='col5',<381>,2:58]], col2=[[@15,50:53='col2',<381>,2:23], [@33,97:100='col2',<381>,3:5]], col3=[[@20,63:66='col3',<381>,2:36]], col1=[[@10,39:42='col1',<381>,2:12]]}}, interface={*=[{name=*, table_ref=*}], A_sum=[{name=A_sum, table_ref=null}]}, derived_columns={B_sum=[{name=col1, table_ref=tab1}], A_avg=[{name=col2, table_ref=tab1}], A_min=[{name=col5, table_ref=tab1}], A_max=[{name=col4, table_ref=tab1}], A_count=[{name=col3, table_ref=tab1}], B_max=[{name=col4, table_ref=tab1}], B_avg=[{name=col2, table_ref=tab1}], B_min=[{name=col5, table_ref=tab1}], B_count=[{name=col3, table_ref=tab1}], A_sum=[{name=col1, table_ref=tab1}]}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void pivotV1Tab1WithAliasQuotedSelectorsSuccessTest() {
		final String query = "select *, pvt.A_sum asum1, A_sum asum2, tab1.A_sum asum3 from tab1 "
		+"\n pivot (sum(col1), avg(col2), count(col3), max(col4), min(col5) "
		+"\n for col2 in ('A', 'B')) as pvt";

		final SQLSelectParserParser parser = parse(query);
		ParserRunResult runResult = runSQLParsertestAllowErrors(query, parser);
		SqlParseEventWalker extractor = runResult.getExtractor();

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=*, table_ref=*}}, 2={column={name=A_sum, table_ref=pvt}, alias=asum1}, 3={column={name=A_sum, table_ref=null}, alias=asum2}, 4={column={name=A_sum, table_ref=tab1}, alias=asum3}}, from={pivot={value={1={function={function_name=sum, parameters={column={name=col1, table_ref=null}}}}, 2={function={function_name=avg, parameters={column={name=col2, table_ref=null}}}}, 3={function={function_name=count, parameters={column={name=col3, table_ref=null}}}}, 4={function={function_name=max, parameters={column={name=col4, table_ref=null}}}}, 5={function={function_name=min, parameters={column={name=col5, table_ref=null}}}}}, for={column={name=col2, table_ref=null}}, in={1={pivot_literal='A'}, 2={pivot_literal='B'}}}, alias=pvt, table={alias=null, table=tab1}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[asum2, asum3, *, asum1]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{tab1={*=[[@1,7:7='*',<291>,1:7]], col4=[[@36,115:118='col4',<381>,2:47]], col5=[[@41,126:129='col5',<381>,2:58]], col2=[[@26,91:94='col2',<381>,2:23], [@44,138:141='col2',<381>,3:5]], A_sum=[[@11,40:43='tab1',<381>,1:40]], col3=[[@31,104:107='col3',<381>,2:36]], col1=[[@21,80:83='col1',<381>,2:12]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{query1={*=[[@1,7:7='*',<291>,1:7]], asum1=[[@6,20:24='asum1',<381>,1:20]], asum2=[[@9,33:37='asum2',<381>,1:33]], asum3=[[@14,51:55='asum3',<381>,1:51]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query1={query_dictionary={asum2=[[@9,33:37='asum2',<381>,1:33]], asum3=[[@14,51:55='asum3',<381>,1:51]], *=[[@1,7:7='*',<291>,1:7]], asum1=[[@6,20:24='asum1',<381>,1:20]]}, table_dictionary={tab1={*=[[@1,7:7='*',<291>,1:7]], col4=[[@36,115:118='col4',<381>,2:47]], col5=[[@41,126:129='col5',<381>,2:58]], col2=[[@26,91:94='col2',<381>,2:23], [@44,138:141='col2',<381>,3:5]], col3=[[@31,104:107='col3',<381>,2:36]], A_sum=[[@11,40:43='tab1',<381>,1:40]], col1=[[@21,80:83='col1',<381>,2:12]]}}, interface={asum2=[{name=A_sum, table_ref=null}], asum3=[{name=A_sum, table_ref=tab1}], *=[{name=*, table_ref=*}], asum1=[{name=A_sum, table_ref=pvt}]}, derived_columns={B_sum=[{name=col1, table_ref=pvt}], A_avg=[{name=col2, table_ref=pvt}], A_min=[{name=col5, table_ref=pvt}], A_max=[{name=col4, table_ref=pvt}], A_count=[{name=col3, table_ref=pvt}], B_max=[{name=col4, table_ref=pvt}], B_avg=[{name=col2, table_ref=pvt}], B_min=[{name=col5, table_ref=pvt}], B_count=[{name=col3, table_ref=pvt}], A_sum=[{name=col1, table_ref=pvt}]}, table_alias={pvt=tab1}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void pivotV1QueryTest() {
		final String query = "select A_sum, A_avg, A_count, A_max, A_min, "
		+ "\n B_sum, B_avg, B_count, B_max, B_min "
		+ "\nfrom (select col1, col2, col3, col4, col5 from tab1) q "
		+"\n pivot (sum(col1), avg(col2), count(col3), max(col4), min(col5) "
		+"\n for col2 in (A, B)) u";

		final SQLSelectParserParser parser = parse(query);
		ParserRunResult runResult = runSQLParsertestAllowErrors(query, parser);
		SqlParseEventWalker extractor = runResult.getExtractor();
		Assert.assertNotNull(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=A_sum, table_ref=null}}, 2={column={name=A_avg, table_ref=null}}, 3={column={name=A_count, table_ref=null}}, 4={column={name=A_max, table_ref=null}}, 5={column={name=A_min, table_ref=null}}, 6={column={name=B_sum, table_ref=null}}, 7={column={name=B_avg, table_ref=null}}, 8={column={name=B_count, table_ref=null}}, 9={column={name=B_max, table_ref=null}}, 10={column={name=B_min, table_ref=null}}}, from={pivot={value={1={function={function_name=sum, parameters={column={name=col1, table_ref=null}}}}, 2={function={function_name=avg, parameters={column={name=col2, table_ref=null}}}}, 3={function={function_name=count, parameters={column={name=col3, table_ref=null}}}}, 4={function={function_name=max, parameters={column={name=col4, table_ref=null}}}}, 5={function={function_name=min, parameters={column={name=col5, table_ref=null}}}}}, for={column={name=col2, table_ref=null}}, in={1={pivot_literal=A}, 2={pivot_literal=B}}}, alias=u, table={alias=q, query={select={1={column={name=col1, table_ref=null}}, 2={column={name=col2, table_ref=null}}, 3={column={name=col3, table_ref=null}}, 4={column={name=col4, table_ref=null}}, 5={column={name=col5, table_ref=null}}}, from={table={alias=null, table=tab1}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[B_sum, A_avg, A_min, A_max, A_count, B_max, B_avg, B_min, B_count, A_sum]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={col4=[[@29,114:117='col4',<381>,3:31]], col5=[[@31,120:123='col5',<381>,3:37]], col2=[[@25,102:105='col2',<381>,3:19]], col3=[[@27,108:111='col3',<381>,3:25]], col1=[[@23,96:99='col1',<381>,3:13]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
		 "{query0={col4=[[@29,114:117='col4',<381>,3:31], [@55,186:189='col4',<381>,4:47]], col5=[[@31,120:123='col5',<381>,3:37], [@60,197:200='col5',<381>,4:58]], col2=[[@25,102:105='col2',<381>,3:19], [@45,162:165='col2',<381>,4:23], [@63,209:212='col2',<381>,5:5]], col3=[[@27,108:111='col3',<381>,3:25], [@50,175:178='col3',<381>,4:36]], col1=[[@23,96:99='col1',<381>,3:13], [@40,151:154='col1',<381>,4:12]]}, query2={B_sum=[[@11,46:50='B_sum',<381>,2:1]], A_avg=[[@3,14:18='A_avg',<381>,1:14]], A_min=[[@9,37:41='A_min',<381>,1:37]], A_max=[[@7,30:34='A_max',<381>,1:30]], A_count=[[@5,21:27='A_count',<381>,1:21]], B_max=[[@17,69:73='B_max',<381>,2:24]], B_avg=[[@13,53:57='B_avg',<381>,2:8]], B_min=[[@19,76:80='B_min',<381>,2:31]], B_count=[[@15,60:66='B_count',<381>,2:15]], A_sum=[[@1,7:11='A_sum',<381>,1:7]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query2={query_dictionary={B_sum=[[@11,46:50='B_sum',<381>,2:1]], A_avg=[[@3,14:18='A_avg',<381>,1:14]], A_min=[[@9,37:41='A_min',<381>,1:37]], A_max=[[@7,30:34='A_max',<381>,1:30]], A_count=[[@5,21:27='A_count',<381>,1:21]], B_max=[[@17,69:73='B_max',<381>,2:24]], B_avg=[[@13,53:57='B_avg',<381>,2:8]], B_min=[[@19,76:80='B_min',<381>,2:31]], B_count=[[@15,60:66='B_count',<381>,2:15]], A_sum=[[@1,7:11='A_sum',<381>,1:7]]}, def_query0={query_dictionary={col4=[[@29,114:117='col4',<381>,3:31], [@55,186:189='col4',<381>,4:47]], col5=[[@31,120:123='col5',<381>,3:37], [@60,197:200='col5',<381>,4:58]], col2=[[@25,102:105='col2',<381>,3:19], [@45,162:165='col2',<381>,4:23], [@63,209:212='col2',<381>,5:5]], col3=[[@27,108:111='col3',<381>,3:25], [@50,175:178='col3',<381>,4:36]], col1=[[@23,96:99='col1',<381>,3:13], [@40,151:154='col1',<381>,4:12]]}, table_dictionary={tab1={col4=[[@29,114:117='col4',<381>,3:31]], col5=[[@31,120:123='col5',<381>,3:37]], col2=[[@25,102:105='col2',<381>,3:19]], col3=[[@27,108:111='col3',<381>,3:25]], col1=[[@23,96:99='col1',<381>,3:13]]}}, interface={col4=[{name=col4, table_ref=tab1}], col5=[{name=col5, table_ref=tab1}], col2=[{name=col2, table_ref=tab1}], col3=[{name=col3, table_ref=tab1}], col1=[{name=col1, table_ref=tab1}]}}, interface={B_sum=[{name=B_sum, table_ref=null}], A_avg=[{name=A_avg, table_ref=null}], A_min=[{name=A_min, table_ref=null}], A_max=[{name=A_max, table_ref=null}], A_count=[{name=A_count, table_ref=null}], B_max=[{name=B_max, table_ref=null}], B_avg=[{name=B_avg, table_ref=null}], B_min=[{name=B_min, table_ref=null}], B_count=[{name=B_count, table_ref=null}], A_sum=[{name=A_sum, table_ref=null}]}, derived_columns={B_sum=[{name=col1, table_ref=u}], A_avg=[{name=col2, table_ref=u}], A_min=[{name=col5, table_ref=u}], A_max=[{name=col4, table_ref=u}], A_count=[{name=col3, table_ref=u}], B_max=[{name=col4, table_ref=u}], B_avg=[{name=col2, table_ref=u}], B_min=[{name=col5, table_ref=u}], B_count=[{name=col3, table_ref=u}], A_sum=[{name=col1, table_ref=u}]}, table_alias={q=query0, u=q}}}",
				extractor.getSymbolTable().toString());
		assertFatalDiagnosticAtPosition(
				extractor.getSnippet(),
				"PIVOT_IN_IDENTIFIER_UNRESOLVED",
				"cannot be resolved against the PIVOT source",
				"A",
				5,
				14);
		assertFatalDiagnosticAtPosition(
				extractor.getSnippet(),
				"PIVOT_IN_IDENTIFIER_UNRESOLVED",
				"cannot be resolved against the PIVOT source",
				"B",
				5,
				17);
	}

	@Test
	public void pivotV1QueryQuotedSelectorsSuccessTest() {
		final String query = "select A_sum, A_avg, A_count, A_max, A_min, "
		+ "\n B_sum, B_avg, B_count, B_max, B_min "
		+ "\nfrom (select col1, col2, col3, col4, col5 from tab1) q "
		+"\n pivot (sum(col1), avg(col2), count(col3), max(col4), min(col5) "
		+"\n for col2 in ('A', 'B')) u";

		final SQLSelectParserParser parser = parse(query);
		ParserRunResult runResult = runSQLParsertestAllowErrors(query, parser);
		SqlParseEventWalker extractor = runResult.getExtractor();

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=A_sum, table_ref=null}}, 2={column={name=A_avg, table_ref=null}}, 3={column={name=A_count, table_ref=null}}, 4={column={name=A_max, table_ref=null}}, 5={column={name=A_min, table_ref=null}}, 6={column={name=B_sum, table_ref=null}}, 7={column={name=B_avg, table_ref=null}}, 8={column={name=B_count, table_ref=null}}, 9={column={name=B_max, table_ref=null}}, 10={column={name=B_min, table_ref=null}}}, from={pivot={value={1={function={function_name=sum, parameters={column={name=col1, table_ref=null}}}}, 2={function={function_name=avg, parameters={column={name=col2, table_ref=null}}}}, 3={function={function_name=count, parameters={column={name=col3, table_ref=null}}}}, 4={function={function_name=max, parameters={column={name=col4, table_ref=null}}}}, 5={function={function_name=min, parameters={column={name=col5, table_ref=null}}}}}, for={column={name=col2, table_ref=null}}, in={1={pivot_literal='A'}, 2={pivot_literal='B'}}}, alias=u, table={alias=q, query={select={1={column={name=col1, table_ref=null}}, 2={column={name=col2, table_ref=null}}, 3={column={name=col3, table_ref=null}}, 4={column={name=col4, table_ref=null}}, 5={column={name=col5, table_ref=null}}}, from={table={alias=null, table=tab1}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[B_sum, A_avg, A_min, A_max, A_count, B_max, B_avg, B_min, B_count, A_sum]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={col4=[[@29,114:117='col4',<381>,3:31]], col5=[[@31,120:123='col5',<381>,3:37]], col2=[[@25,102:105='col2',<381>,3:19]], col3=[[@27,108:111='col3',<381>,3:25]], col1=[[@23,96:99='col1',<381>,3:13]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={col4=[[@29,114:117='col4',<381>,3:31], [@55,186:189='col4',<381>,4:47]], col5=[[@31,120:123='col5',<381>,3:37], [@60,197:200='col5',<381>,4:58]], col2=[[@25,102:105='col2',<381>,3:19], [@45,162:165='col2',<381>,4:23], [@63,209:212='col2',<381>,5:5]], col3=[[@27,108:111='col3',<381>,3:25], [@50,175:178='col3',<381>,4:36]], col1=[[@23,96:99='col1',<381>,3:13], [@40,151:154='col1',<381>,4:12]]}, query2={B_sum=[[@11,46:50='B_sum',<381>,2:1]], A_avg=[[@3,14:18='A_avg',<381>,1:14]], A_min=[[@9,37:41='A_min',<381>,1:37]], A_max=[[@7,30:34='A_max',<381>,1:30]], A_count=[[@5,21:27='A_count',<381>,1:21]], B_max=[[@17,69:73='B_max',<381>,2:24]], B_avg=[[@13,53:57='B_avg',<381>,2:8]], B_min=[[@19,76:80='B_min',<381>,2:31]], B_count=[[@15,60:66='B_count',<381>,2:15]], A_sum=[[@1,7:11='A_sum',<381>,1:7]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query2={query_dictionary={B_sum=[[@11,46:50='B_sum',<381>,2:1]], A_avg=[[@3,14:18='A_avg',<381>,1:14]], A_min=[[@9,37:41='A_min',<381>,1:37]], A_max=[[@7,30:34='A_max',<381>,1:30]], A_count=[[@5,21:27='A_count',<381>,1:21]], B_max=[[@17,69:73='B_max',<381>,2:24]], B_avg=[[@13,53:57='B_avg',<381>,2:8]], B_min=[[@19,76:80='B_min',<381>,2:31]], B_count=[[@15,60:66='B_count',<381>,2:15]], A_sum=[[@1,7:11='A_sum',<381>,1:7]]}, def_query0={query_dictionary={col4=[[@29,114:117='col4',<381>,3:31], [@55,186:189='col4',<381>,4:47]], col5=[[@31,120:123='col5',<381>,3:37], [@60,197:200='col5',<381>,4:58]], col2=[[@25,102:105='col2',<381>,3:19], [@45,162:165='col2',<381>,4:23], [@63,209:212='col2',<381>,5:5]], col3=[[@27,108:111='col3',<381>,3:25], [@50,175:178='col3',<381>,4:36]], col1=[[@23,96:99='col1',<381>,3:13], [@40,151:154='col1',<381>,4:12]]}, table_dictionary={tab1={col4=[[@29,114:117='col4',<381>,3:31]], col5=[[@31,120:123='col5',<381>,3:37]], col2=[[@25,102:105='col2',<381>,3:19]], col3=[[@27,108:111='col3',<381>,3:25]], col1=[[@23,96:99='col1',<381>,3:13]]}}, interface={col4=[{name=col4, table_ref=tab1}], col5=[{name=col5, table_ref=tab1}], col2=[{name=col2, table_ref=tab1}], col3=[{name=col3, table_ref=tab1}], col1=[{name=col1, table_ref=tab1}]}}, interface={B_sum=[{name=B_sum, table_ref=null}], A_avg=[{name=A_avg, table_ref=null}], A_min=[{name=A_min, table_ref=null}], A_max=[{name=A_max, table_ref=null}], A_count=[{name=A_count, table_ref=null}], B_max=[{name=B_max, table_ref=null}], B_avg=[{name=B_avg, table_ref=null}], B_min=[{name=B_min, table_ref=null}], B_count=[{name=B_count, table_ref=null}], A_sum=[{name=A_sum, table_ref=null}]}, derived_columns={B_sum=[{name=col1, table_ref=u}], A_avg=[{name=col2, table_ref=u}], A_min=[{name=col5, table_ref=u}], A_max=[{name=col4, table_ref=u}], A_count=[{name=col3, table_ref=u}], B_max=[{name=col4, table_ref=u}], B_avg=[{name=col2, table_ref=u}], B_min=[{name=col5, table_ref=u}], B_count=[{name=col3, table_ref=u}], A_sum=[{name=col1, table_ref=u}]}, table_alias={q=query0, u=q}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void pivotV1QueryInvalidAggregateFormulaReportsParserErrorTest() {
		final String query = "select A_sum, A_avg, A_count, A_max, A_min, "
		+ "\n B_sum, B_avg, B_count, B_max, B_min "
		+ "\nfrom (select col1, col2, col3, col4, col5 from tab1) q "
		+"\n pivot (sum(col1), avg(col2), count(col3 - col1), max(col4), min(col5) "
		+"\n for col2 in (A, B))";

		final SQLSelectParserParser parser = parse(query);
		ParserRunResult runResult = runSQLParsertestAllowErrors(query, parser);
		SqlParseEventWalker extractor = runResult.getExtractor();

		Assert.assertNull(
				"Extractor should be null when parser rejects an invalid PIVOT aggregate parameter expression.",
				extractor);
		Assert.assertNotNull("Expected parser failure for invalid PIVOT aggregate parameter expression.",
				runResult.getFailure());
		Assert.assertTrue(
				"Expected parser diagnostics or parser errors for invalid PIVOT aggregate syntax.",
				runResult.getParserErrorCount() > 0
						|| !runResult.getParserErrors().isEmpty()
						|| !runResult.getListenerDiagnostics().isEmpty());

		Assert.assertTrue(
				"Expected parser to record at least one parser error entry.",
				runResult.getParserErrorCount() > 0 || !runResult.getParserErrors().isEmpty());
	}

	@Test
	public void pivotV2Tab1Test() {
		final String query = "select * from tab1 "
		+"\n pivot (sum(col1) sum, avg(col2) ave, count(col3) cnt,"
		+"\n max(col4) max, min(col5) min for col2 in (sales as A, units as B))";

		final SQLSelectParserParser parser = parse(query);
		ParserRunResult runResult = runSQLParsertestAllowErrors(query, parser);
		SqlParseEventWalker extractor = runResult.getExtractor();

		Assert.assertNotNull(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=*, table_ref=*}}}, from={pivot={value={1={function={function_name=sum, parameters={column={name=col1, table_ref=null}}}, alias=sum}, 2={function={function_name=avg, parameters={column={name=col2, table_ref=null}}}, alias=ave}, 3={function={function_name=count, parameters={column={name=col3, table_ref=null}}}, alias=cnt}, 4={function={function_name=max, parameters={column={name=col4, table_ref=null}}}, alias=max}, 5={function={function_name=min, parameters={column={name=col5, table_ref=null}}}, alias=min}}, for={column={name=col2, table_ref=null}}, in={1={pivot_literal=sales, pivot_prefix=A}, 2={pivot_literal=units, pivot_prefix=B}}}, table={alias=null, table=tab1}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={*=[[@1,7:7='*',<291>,1:7]], col4=[[@26,80:83='col4',<381>,3:5]], col5=[[@32,95:98='col5',<381>,3:20]], col2=[[@14,47:50='col2',<381>,2:27], [@36,109:112='col2',<381>,3:34]], col3=[[@20,64:67='col3',<381>,2:44]], col1=[[@8,32:35='col1',<381>,2:12]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query1={*=[[@1,7:7='*',<291>,1:7]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query1={query_dictionary={*=[[@1,7:7='*',<291>,1:7]]}, table_dictionary={tab1={*=[[@1,7:7='*',<291>,1:7]], col4=[[@26,80:83='col4',<381>,3:5]], col5=[[@32,95:98='col5',<381>,3:20]], col2=[[@14,47:50='col2',<381>,2:27], [@36,109:112='col2',<381>,3:34]], col3=[[@20,64:67='col3',<381>,2:44]], col1=[[@8,32:35='col1',<381>,2:12]]}}, interface={*=[{name=*, table_ref=*}]}, derived_columns={B_sum=[{name=col1, table_ref=tab1}], A_ave=[{name=col2, table_ref=tab1}], B_cnt=[{name=col3, table_ref=tab1}], A_min=[{name=col5, table_ref=tab1}], A_max=[{name=col4, table_ref=tab1}], B_max=[{name=col4, table_ref=tab1}], A_cnt=[{name=col3, table_ref=tab1}], B_min=[{name=col5, table_ref=tab1}], A_sum=[{name=col1, table_ref=tab1}], B_ave=[{name=col2, table_ref=tab1}]}}}",
				extractor.getSymbolTable().toString());
		assertFatalDiagnosticAtPosition(
				extractor.getSnippet(),
				"PIVOT_IN_IDENTIFIER_UNRESOLVED",
				"cannot be resolved against the PIVOT source",
				"sales",
				3,
				43);
		assertFatalDiagnosticAtPosition(
				extractor.getSnippet(),
				"PIVOT_IN_IDENTIFIER_UNRESOLVED",
				"cannot be resolved against the PIVOT source",
				"units",
				3,
				55);

	}

	@Test
	public void pivotV2Tab1QuotedSelectorsSuccessTest() {
		final String query = "select * from tab1 "
		+"\n pivot (sum(col1) sum, avg(col2) ave, count(col3) cnt,"
		+"\n max(col4) max, min(col5) min for col2 in ('sales' as A, 'units' as B))";

		final SQLSelectParserParser parser = parse(query);
		ParserRunResult runResult = runSQLParsertestAllowErrors(query, parser);
		SqlParseEventWalker extractor = runResult.getExtractor();

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=*, table_ref=*}}}, from={pivot={value={1={function={function_name=sum, parameters={column={name=col1, table_ref=null}}}, alias=sum}, 2={function={function_name=avg, parameters={column={name=col2, table_ref=null}}}, alias=ave}, 3={function={function_name=count, parameters={column={name=col3, table_ref=null}}}, alias=cnt}, 4={function={function_name=max, parameters={column={name=col4, table_ref=null}}}, alias=max}, 5={function={function_name=min, parameters={column={name=col5, table_ref=null}}}, alias=min}}, for={column={name=col2, table_ref=null}}, in={1={pivot_literal='sales', pivot_prefix=A}, 2={pivot_literal='units', pivot_prefix=B}}}, table={alias=null, table=tab1}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={*=[[@1,7:7='*',<291>,1:7]], col4=[[@26,80:83='col4',<381>,3:5]], col5=[[@32,95:98='col5',<381>,3:20]], col2=[[@14,47:50='col2',<381>,2:27], [@36,109:112='col2',<381>,3:34]], col3=[[@20,64:67='col3',<381>,2:44]], col1=[[@8,32:35='col1',<381>,2:12]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query1={*=[[@1,7:7='*',<291>,1:7]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query1={query_dictionary={*=[[@1,7:7='*',<291>,1:7]]}, table_dictionary={tab1={*=[[@1,7:7='*',<291>,1:7]], col4=[[@26,80:83='col4',<381>,3:5]], col5=[[@32,95:98='col5',<381>,3:20]], col2=[[@14,47:50='col2',<381>,2:27], [@36,109:112='col2',<381>,3:34]], col3=[[@20,64:67='col3',<381>,2:44]], col1=[[@8,32:35='col1',<381>,2:12]]}}, interface={*=[{name=*, table_ref=*}]}, derived_columns={B_sum=[{name=col1, table_ref=tab1}], A_ave=[{name=col2, table_ref=tab1}], B_cnt=[{name=col3, table_ref=tab1}], A_min=[{name=col5, table_ref=tab1}], A_max=[{name=col4, table_ref=tab1}], B_max=[{name=col4, table_ref=tab1}], A_cnt=[{name=col3, table_ref=tab1}], B_min=[{name=col5, table_ref=tab1}], A_sum=[{name=col1, table_ref=tab1}], B_ave=[{name=col2, table_ref=tab1}]}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void pivotV2QueryTest() {
		final String query = "select A_sum, A_ave, A_cnt, A_max, A_min, "
		+ "\n B_sum, B_ave, B_cnt, B_max, B_min "
		+ "\nfrom (select col1, col2, col3, col4, col5 from tab1) q "
		+"\n pivot (sum(col1) sum, avg(col2) ave, count(col3) cnt,"
		+"\n max(col4) max, min(col5) min for col2 in (sales as A, units as B))";

		final SQLSelectParserParser parser = parse(query);
		ParserRunResult runResult = runSQLParsertestAllowErrors(query, parser);
		SqlParseEventWalker extractor = runResult.getExtractor();

		Assert.assertNotNull(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=A_sum, table_ref=null}}, 2={column={name=A_ave, table_ref=null}}, 3={column={name=A_cnt, table_ref=null}}, 4={column={name=A_max, table_ref=null}}, 5={column={name=A_min, table_ref=null}}, 6={column={name=B_sum, table_ref=null}}, 7={column={name=B_ave, table_ref=null}}, 8={column={name=B_cnt, table_ref=null}}, 9={column={name=B_max, table_ref=null}}, 10={column={name=B_min, table_ref=null}}}, from={pivot={value={1={function={function_name=sum, parameters={column={name=col1, table_ref=null}}}, alias=sum}, 2={function={function_name=avg, parameters={column={name=col2, table_ref=null}}}, alias=ave}, 3={function={function_name=count, parameters={column={name=col3, table_ref=null}}}, alias=cnt}, 4={function={function_name=max, parameters={column={name=col4, table_ref=null}}}, alias=max}, 5={function={function_name=min, parameters={column={name=col5, table_ref=null}}}, alias=min}}, for={column={name=col2, table_ref=null}}, in={1={pivot_literal=sales, pivot_prefix=A}, 2={pivot_literal=units, pivot_prefix=B}}}, table={alias=q, query={select={1={column={name=col1, table_ref=null}}, 2={column={name=col2, table_ref=null}}, 3={column={name=col3, table_ref=null}}, 4={column={name=col4, table_ref=null}}, 5={column={name=col5, table_ref=null}}}, from={table={alias=null, table=tab1}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[B_sum, A_ave, B_cnt, A_min, A_max, B_max, A_cnt, B_min, A_sum, B_ave]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={col4=[[@29,110:113='col4',<381>,3:31]], col5=[[@31,116:119='col5',<381>,3:37]], col2=[[@25,98:101='col2',<381>,3:19]], col3=[[@27,104:107='col3',<381>,3:25]], col1=[[@23,92:95='col1',<381>,3:13]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
		 "{query0={col4=[[@29,110:113='col4',<381>,3:31], [@58,195:198='col4',<381>,5:5]], col5=[[@31,116:119='col5',<381>,3:37], [@64,210:213='col5',<381>,5:20]], col2=[[@25,98:101='col2',<381>,3:19], [@46,162:165='col2',<381>,4:27], [@68,224:227='col2',<381>,5:34]], col3=[[@27,104:107='col3',<381>,3:25], [@52,179:182='col3',<381>,4:44]], col1=[[@23,92:95='col1',<381>,3:13], [@40,147:150='col1',<381>,4:12]]}, query2={B_sum=[[@11,44:48='B_sum',<381>,2:1]], A_ave=[[@3,14:18='A_ave',<381>,1:14]], B_cnt=[[@15,58:62='B_cnt',<381>,2:15]], A_min=[[@9,35:39='A_min',<381>,1:35]], A_max=[[@7,28:32='A_max',<381>,1:28]], B_max=[[@17,65:69='B_max',<381>,2:22]], A_cnt=[[@5,21:25='A_cnt',<381>,1:21]], B_min=[[@19,72:76='B_min',<381>,2:29]], A_sum=[[@1,7:11='A_sum',<381>,1:7]], B_ave=[[@13,51:55='B_ave',<381>,2:8]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query2={query_dictionary={B_sum=[[@11,44:48='B_sum',<381>,2:1]], A_ave=[[@3,14:18='A_ave',<381>,1:14]], B_cnt=[[@15,58:62='B_cnt',<381>,2:15]], A_min=[[@9,35:39='A_min',<381>,1:35]], A_max=[[@7,28:32='A_max',<381>,1:28]], B_max=[[@17,65:69='B_max',<381>,2:22]], A_cnt=[[@5,21:25='A_cnt',<381>,1:21]], B_min=[[@19,72:76='B_min',<381>,2:29]], A_sum=[[@1,7:11='A_sum',<381>,1:7]], B_ave=[[@13,51:55='B_ave',<381>,2:8]]}, def_query0={query_dictionary={col4=[[@29,110:113='col4',<381>,3:31], [@58,195:198='col4',<381>,5:5]], col5=[[@31,116:119='col5',<381>,3:37], [@64,210:213='col5',<381>,5:20]], col2=[[@25,98:101='col2',<381>,3:19], [@46,162:165='col2',<381>,4:27], [@68,224:227='col2',<381>,5:34]], col3=[[@27,104:107='col3',<381>,3:25], [@52,179:182='col3',<381>,4:44]], col1=[[@23,92:95='col1',<381>,3:13], [@40,147:150='col1',<381>,4:12]]}, table_dictionary={tab1={col4=[[@29,110:113='col4',<381>,3:31]], col5=[[@31,116:119='col5',<381>,3:37]], col2=[[@25,98:101='col2',<381>,3:19]], col3=[[@27,104:107='col3',<381>,3:25]], col1=[[@23,92:95='col1',<381>,3:13]]}}, interface={col4=[{name=col4, table_ref=tab1}], col5=[{name=col5, table_ref=tab1}], col2=[{name=col2, table_ref=tab1}], col3=[{name=col3, table_ref=tab1}], col1=[{name=col1, table_ref=tab1}]}}, interface={B_sum=[{name=B_sum, table_ref=null}], A_ave=[{name=A_ave, table_ref=null}], B_cnt=[{name=B_cnt, table_ref=null}], A_min=[{name=A_min, table_ref=null}], A_max=[{name=A_max, table_ref=null}], B_max=[{name=B_max, table_ref=null}], A_cnt=[{name=A_cnt, table_ref=null}], B_min=[{name=B_min, table_ref=null}], A_sum=[{name=A_sum, table_ref=null}], B_ave=[{name=B_ave, table_ref=null}]}, derived_columns={B_sum=[{name=col1, table_ref=q}], A_ave=[{name=col2, table_ref=q}], B_cnt=[{name=col3, table_ref=q}], A_min=[{name=col5, table_ref=q}], A_max=[{name=col4, table_ref=q}], B_max=[{name=col4, table_ref=q}], A_cnt=[{name=col3, table_ref=q}], B_min=[{name=col5, table_ref=q}], A_sum=[{name=col1, table_ref=q}], B_ave=[{name=col2, table_ref=q}]}, table_alias={q=query0}}}",
				extractor.getSymbolTable().toString());
		assertFatalDiagnosticAtPosition(
				extractor.getSnippet(),
				"PIVOT_IN_IDENTIFIER_UNRESOLVED",
				"cannot be resolved against the PIVOT source",
				"sales",
				5,
				43);
		assertFatalDiagnosticAtPosition(
				extractor.getSnippet(),
				"PIVOT_IN_IDENTIFIER_UNRESOLVED",
				"cannot be resolved against the PIVOT source",
				"units",
				5,
				55);

	}

	@Test
	public void pivotV2QueryQuotedSelectorsSuccessTest() {
		final String query = "select A_sum, A_ave, A_cnt, A_max, A_min, "
		+ "\n B_sum, B_ave, B_cnt, B_max, B_min "
		+ "\nfrom (select col1, col2, col3, col4, col5 from tab1) q "
		+"\n pivot (sum(col1) sum, avg(col2) ave, count(col3) cnt,"
		+"\n max(col4) max, min(col5) min for col2 in ('sales' as A, 'units' as B))";

		final SQLSelectParserParser parser = parse(query);
		ParserRunResult runResult = runSQLParsertestAllowErrors(query, parser);
		SqlParseEventWalker extractor = runResult.getExtractor();

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=A_sum, table_ref=null}}, 2={column={name=A_ave, table_ref=null}}, 3={column={name=A_cnt, table_ref=null}}, 4={column={name=A_max, table_ref=null}}, 5={column={name=A_min, table_ref=null}}, 6={column={name=B_sum, table_ref=null}}, 7={column={name=B_ave, table_ref=null}}, 8={column={name=B_cnt, table_ref=null}}, 9={column={name=B_max, table_ref=null}}, 10={column={name=B_min, table_ref=null}}}, from={pivot={value={1={function={function_name=sum, parameters={column={name=col1, table_ref=null}}}, alias=sum}, 2={function={function_name=avg, parameters={column={name=col2, table_ref=null}}}, alias=ave}, 3={function={function_name=count, parameters={column={name=col3, table_ref=null}}}, alias=cnt}, 4={function={function_name=max, parameters={column={name=col4, table_ref=null}}}, alias=max}, 5={function={function_name=min, parameters={column={name=col5, table_ref=null}}}, alias=min}}, for={column={name=col2, table_ref=null}}, in={1={pivot_literal='sales', pivot_prefix=A}, 2={pivot_literal='units', pivot_prefix=B}}}, table={alias=q, query={select={1={column={name=col1, table_ref=null}}, 2={column={name=col2, table_ref=null}}, 3={column={name=col3, table_ref=null}}, 4={column={name=col4, table_ref=null}}, 5={column={name=col5, table_ref=null}}}, from={table={alias=null, table=tab1}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[B_sum, A_ave, B_cnt, A_min, A_max, B_max, A_cnt, B_min, A_sum, B_ave]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={col4=[[@29,110:113='col4',<381>,3:31]], col5=[[@31,116:119='col5',<381>,3:37]], col2=[[@25,98:101='col2',<381>,3:19]], col3=[[@27,104:107='col3',<381>,3:25]], col1=[[@23,92:95='col1',<381>,3:13]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
		 "{query0={col4=[[@29,110:113='col4',<381>,3:31], [@58,195:198='col4',<381>,5:5]], col5=[[@31,116:119='col5',<381>,3:37], [@64,210:213='col5',<381>,5:20]], col2=[[@25,98:101='col2',<381>,3:19], [@46,162:165='col2',<381>,4:27], [@68,224:227='col2',<381>,5:34]], col3=[[@27,104:107='col3',<381>,3:25], [@52,179:182='col3',<381>,4:44]], col1=[[@23,92:95='col1',<381>,3:13], [@40,147:150='col1',<381>,4:12]]}, query2={B_sum=[[@11,44:48='B_sum',<381>,2:1]], A_ave=[[@3,14:18='A_ave',<381>,1:14]], B_cnt=[[@15,58:62='B_cnt',<381>,2:15]], A_min=[[@9,35:39='A_min',<381>,1:35]], A_max=[[@7,28:32='A_max',<381>,1:28]], B_max=[[@17,65:69='B_max',<381>,2:22]], A_cnt=[[@5,21:25='A_cnt',<381>,1:21]], B_min=[[@19,72:76='B_min',<381>,2:29]], A_sum=[[@1,7:11='A_sum',<381>,1:7]], B_ave=[[@13,51:55='B_ave',<381>,2:8]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query2={query_dictionary={B_sum=[[@11,44:48='B_sum',<381>,2:1]], A_ave=[[@3,14:18='A_ave',<381>,1:14]], B_cnt=[[@15,58:62='B_cnt',<381>,2:15]], A_min=[[@9,35:39='A_min',<381>,1:35]], A_max=[[@7,28:32='A_max',<381>,1:28]], B_max=[[@17,65:69='B_max',<381>,2:22]], A_cnt=[[@5,21:25='A_cnt',<381>,1:21]], B_min=[[@19,72:76='B_min',<381>,2:29]], A_sum=[[@1,7:11='A_sum',<381>,1:7]], B_ave=[[@13,51:55='B_ave',<381>,2:8]]}, def_query0={query_dictionary={col4=[[@29,110:113='col4',<381>,3:31], [@58,195:198='col4',<381>,5:5]], col5=[[@31,116:119='col5',<381>,3:37], [@64,210:213='col5',<381>,5:20]], col2=[[@25,98:101='col2',<381>,3:19], [@46,162:165='col2',<381>,4:27], [@68,224:227='col2',<381>,5:34]], col3=[[@27,104:107='col3',<381>,3:25], [@52,179:182='col3',<381>,4:44]], col1=[[@23,92:95='col1',<381>,3:13], [@40,147:150='col1',<381>,4:12]]}, table_dictionary={tab1={col4=[[@29,110:113='col4',<381>,3:31]], col5=[[@31,116:119='col5',<381>,3:37]], col2=[[@25,98:101='col2',<381>,3:19]], col3=[[@27,104:107='col3',<381>,3:25]], col1=[[@23,92:95='col1',<381>,3:13]]}}, interface={col4=[{name=col4, table_ref=tab1}], col5=[{name=col5, table_ref=tab1}], col2=[{name=col2, table_ref=tab1}], col3=[{name=col3, table_ref=tab1}], col1=[{name=col1, table_ref=tab1}]}}, interface={B_sum=[{name=B_sum, table_ref=null}], A_ave=[{name=A_ave, table_ref=null}], B_cnt=[{name=B_cnt, table_ref=null}], A_min=[{name=A_min, table_ref=null}], A_max=[{name=A_max, table_ref=null}], B_max=[{name=B_max, table_ref=null}], A_cnt=[{name=A_cnt, table_ref=null}], B_min=[{name=B_min, table_ref=null}], A_sum=[{name=A_sum, table_ref=null}], B_ave=[{name=B_ave, table_ref=null}]}, derived_columns={B_sum=[{name=col1, table_ref=q}], A_ave=[{name=col2, table_ref=q}], B_cnt=[{name=col3, table_ref=q}], A_min=[{name=col5, table_ref=q}], A_max=[{name=col4, table_ref=q}], B_max=[{name=col4, table_ref=q}], A_cnt=[{name=col3, table_ref=q}], B_min=[{name=col5, table_ref=q}], A_sum=[{name=col1, table_ref=q}], B_ave=[{name=col2, table_ref=q}]}, table_alias={q=query0}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void pivotV3Tab1Test() {
		final String query = "select * from tab1 "
		+"\n pivot (sum(col1) sums, avg(col2) ave, count(col3) cnts,"
		+"\n max(col4) max, min(col5) min for col2 in ('sales' as A, 'units' as B))";

		final SQLSelectParserParser parser = parse(query);
		ParserRunResult runResult = runSQLParsertestAllowErrors(query, parser);
		SqlParseEventWalker extractor = runResult.getExtractor();

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=*, table_ref=*}}}, from={pivot={value={1={function={function_name=sum, parameters={column={name=col1, table_ref=null}}}, alias=sums}, 2={function={function_name=avg, parameters={column={name=col2, table_ref=null}}}, alias=ave}, 3={function={function_name=count, parameters={column={name=col3, table_ref=null}}}, alias=cnts}, 4={function={function_name=max, parameters={column={name=col4, table_ref=null}}}, alias=max}, 5={function={function_name=min, parameters={column={name=col5, table_ref=null}}}, alias=min}}, for={column={name=col2, table_ref=null}}, in={1={pivot_literal='sales', pivot_prefix=A}, 2={pivot_literal='units', pivot_prefix=B}}}, table={alias=null, table=tab1}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{tab1={*=[[@1,7:7='*',<291>,1:7]], col4=[[@26,82:85='col4',<381>,3:5]], col5=[[@32,97:100='col5',<381>,3:20]], col2=[[@14,48:51='col2',<381>,2:28], [@36,111:114='col2',<381>,3:34]], col3=[[@20,65:68='col3',<381>,2:45]], col1=[[@8,32:35='col1',<381>,2:12]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{query1={*=[[@1,7:7='*',<291>,1:7]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query1={query_dictionary={*=[[@1,7:7='*',<291>,1:7]]}, table_dictionary={tab1={*=[[@1,7:7='*',<291>,1:7]], col4=[[@26,82:85='col4',<381>,3:5]], col5=[[@32,97:100='col5',<381>,3:20]], col2=[[@14,48:51='col2',<381>,2:28], [@36,111:114='col2',<381>,3:34]], col3=[[@20,65:68='col3',<381>,2:45]], col1=[[@8,32:35='col1',<381>,2:12]]}}, interface={*=[{name=*, table_ref=*}]}, derived_columns={A_ave=[{name=col2, table_ref=tab1}], A_min=[{name=col5, table_ref=tab1}], B_sums=[{name=col1, table_ref=tab1}], A_max=[{name=col4, table_ref=tab1}], B_max=[{name=col4, table_ref=tab1}], B_min=[{name=col5, table_ref=tab1}], A_cnts=[{name=col3, table_ref=tab1}], A_sums=[{name=col1, table_ref=tab1}], B_ave=[{name=col2, table_ref=tab1}], B_cnts=[{name=col3, table_ref=tab1}]}}}",
				extractor.getSymbolTable().toString());
		Assert.assertTrue("Expected PIVOT IN item prefixes to be preserved",
				extractor.getAsTree().toString().contains("pivot_prefix=A")
					&& extractor.getAsTree().toString().contains("pivot_prefix=B"));
	}

	@Test
	public void pivotV3QueryTest() {
		final String query = "select A_sums, A_ave, A_cnts, A_max, A_min, B_sums, B_ave, B_cnts, B_max, B_min from (select col1, col2, col3, col4, col5 from tab1) q "
		+"\n pivot (sum(col1) sums, avg(col2) ave, count(col3) cnts,"
		+"\n max(col4) max, min(col5) min for col2 in ('sales' as A, 'units' as B))";

		final SQLSelectParserParser parser = parse(query);
		ParserRunResult runResult = runSQLParsertestAllowErrors(query, parser);
		SqlParseEventWalker extractor = runResult.getExtractor();

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=A_sums, table_ref=null}}, 2={column={name=A_ave, table_ref=null}}, 3={column={name=A_cnts, table_ref=null}}, 4={column={name=A_max, table_ref=null}}, 5={column={name=A_min, table_ref=null}}, 6={column={name=B_sums, table_ref=null}}, 7={column={name=B_ave, table_ref=null}}, 8={column={name=B_cnts, table_ref=null}}, 9={column={name=B_max, table_ref=null}}, 10={column={name=B_min, table_ref=null}}}, from={pivot={value={1={function={function_name=sum, parameters={column={name=col1, table_ref=null}}}, alias=sums}, 2={function={function_name=avg, parameters={column={name=col2, table_ref=null}}}, alias=ave}, 3={function={function_name=count, parameters={column={name=col3, table_ref=null}}}, alias=cnts}, 4={function={function_name=max, parameters={column={name=col4, table_ref=null}}}, alias=max}, 5={function={function_name=min, parameters={column={name=col5, table_ref=null}}}, alias=min}}, for={column={name=col2, table_ref=null}}, in={1={pivot_literal='sales', pivot_prefix=A}, 2={pivot_literal='units', pivot_prefix=B}}}, table={alias=q, query={select={1={column={name=col1, table_ref=null}}, 2={column={name=col2, table_ref=null}}, 3={column={name=col3, table_ref=null}}, 4={column={name=col4, table_ref=null}}, 5={column={name=col5, table_ref=null}}}, from={table={alias=null, table=tab1}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[A_ave, A_min, B_sums, A_max, B_max, B_min, A_cnts, A_sums, B_ave, B_cnts]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{tab1={col4=[[@29,111:114='col4',<381>,1:111]], col5=[[@31,117:120='col5',<381>,1:117]], col2=[[@25,99:102='col2',<381>,1:99]], col3=[[@27,105:108='col3',<381>,1:105]], col1=[[@23,93:96='col1',<381>,1:93]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{query0={col4=[[@29,111:114='col4',<381>,1:111], [@58,198:201='col4',<381>,3:5]], col5=[[@31,117:120='col5',<381>,1:117], [@64,213:216='col5',<381>,3:20]], col2=[[@25,99:102='col2',<381>,1:99], [@46,164:167='col2',<381>,2:28], [@68,227:230='col2',<381>,3:34]], col3=[[@27,105:108='col3',<381>,1:105], [@52,181:184='col3',<381>,2:45]], col1=[[@23,93:96='col1',<381>,1:93], [@40,148:151='col1',<381>,2:12]]}, query2={A_ave=[[@3,15:19='A_ave',<381>,1:15]], A_min=[[@9,37:41='A_min',<381>,1:37]], B_sums=[[@11,44:49='B_sums',<381>,1:44]], A_max=[[@7,30:34='A_max',<381>,1:30]], B_max=[[@17,67:71='B_max',<381>,1:67]], B_min=[[@19,74:78='B_min',<381>,1:74]], A_cnts=[[@5,22:27='A_cnts',<381>,1:22]], A_sums=[[@1,7:12='A_sums',<381>,1:7]], B_ave=[[@13,52:56='B_ave',<381>,1:52]], B_cnts=[[@15,59:64='B_cnts',<381>,1:59]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query2={query_dictionary={A_ave=[[@3,15:19='A_ave',<381>,1:15]], A_min=[[@9,37:41='A_min',<381>,1:37]], B_sums=[[@11,44:49='B_sums',<381>,1:44]], A_max=[[@7,30:34='A_max',<381>,1:30]], B_max=[[@17,67:71='B_max',<381>,1:67]], B_min=[[@19,74:78='B_min',<381>,1:74]], A_cnts=[[@5,22:27='A_cnts',<381>,1:22]], A_sums=[[@1,7:12='A_sums',<381>,1:7]], B_ave=[[@13,52:56='B_ave',<381>,1:52]], B_cnts=[[@15,59:64='B_cnts',<381>,1:59]]}, def_query0={query_dictionary={col4=[[@29,111:114='col4',<381>,1:111], [@58,198:201='col4',<381>,3:5]], col5=[[@31,117:120='col5',<381>,1:117], [@64,213:216='col5',<381>,3:20]], col2=[[@25,99:102='col2',<381>,1:99], [@46,164:167='col2',<381>,2:28], [@68,227:230='col2',<381>,3:34]], col3=[[@27,105:108='col3',<381>,1:105], [@52,181:184='col3',<381>,2:45]], col1=[[@23,93:96='col1',<381>,1:93], [@40,148:151='col1',<381>,2:12]]}, table_dictionary={tab1={col4=[[@29,111:114='col4',<381>,1:111]], col5=[[@31,117:120='col5',<381>,1:117]], col2=[[@25,99:102='col2',<381>,1:99]], col3=[[@27,105:108='col3',<381>,1:105]], col1=[[@23,93:96='col1',<381>,1:93]]}}, interface={col4=[{name=col4, table_ref=tab1}], col5=[{name=col5, table_ref=tab1}], col2=[{name=col2, table_ref=tab1}], col3=[{name=col3, table_ref=tab1}], col1=[{name=col1, table_ref=tab1}]}}, interface={A_ave=[{name=A_ave, table_ref=null}], A_min=[{name=A_min, table_ref=null}], B_sums=[{name=B_sums, table_ref=null}], A_max=[{name=A_max, table_ref=null}], B_max=[{name=B_max, table_ref=null}], B_min=[{name=B_min, table_ref=null}], A_cnts=[{name=A_cnts, table_ref=null}], A_sums=[{name=A_sums, table_ref=null}], B_ave=[{name=B_ave, table_ref=null}], B_cnts=[{name=B_cnts, table_ref=null}]}, derived_columns={A_ave=[{name=col2, table_ref=q}], A_min=[{name=col5, table_ref=q}], B_sums=[{name=col1, table_ref=q}], A_max=[{name=col4, table_ref=q}], B_max=[{name=col4, table_ref=q}], B_min=[{name=col5, table_ref=q}], A_cnts=[{name=col3, table_ref=q}], A_sums=[{name=col1, table_ref=q}], B_ave=[{name=col2, table_ref=q}], B_cnts=[{name=col3, table_ref=q}]}, table_alias={q=query0}}}",
				extractor.getSymbolTable().toString());
		Assert.assertTrue("Expected PIVOT IN item prefixes to be preserved",
				extractor.getAsTree().toString().contains("pivot_prefix=A")
					&& extractor.getAsTree().toString().contains("pivot_prefix=B"));
	}


	@Test
	public void pivotV4Tab1Test() {
		final String query = "select * from tab1 "
		+"\n pivot (sum(col1) sum, avg(col2) ave, count(col3) cnt,"
		+"\n max(col4) max, min(col5) min for col2 in ('sales', 'units'))";

		final SQLSelectParserParser parser = parse(query);
		ParserRunResult runResult = runSQLParsertestAllowErrors(query, parser);
		SqlParseEventWalker extractor = runResult.getExtractor();

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=*, table_ref=*}}}, from={pivot={value={1={function={function_name=sum, parameters={column={name=col1, table_ref=null}}}, alias=sum}, 2={function={function_name=avg, parameters={column={name=col2, table_ref=null}}}, alias=ave}, 3={function={function_name=count, parameters={column={name=col3, table_ref=null}}}, alias=cnt}, 4={function={function_name=max, parameters={column={name=col4, table_ref=null}}}, alias=max}, 5={function={function_name=min, parameters={column={name=col5, table_ref=null}}}, alias=min}}, for={column={name=col2, table_ref=null}}, in={1={pivot_literal='sales'}, 2={pivot_literal='units'}}}, table={alias=null, table=tab1}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{tab1={*=[[@1,7:7='*',<291>,1:7]], col4=[[@26,80:83='col4',<381>,3:5]], col5=[[@32,95:98='col5',<381>,3:20]], col2=[[@14,47:50='col2',<381>,2:27], [@36,109:112='col2',<381>,3:34]], col3=[[@20,64:67='col3',<381>,2:44]], col1=[[@8,32:35='col1',<381>,2:12]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{query1={*=[[@1,7:7='*',<291>,1:7]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query1={query_dictionary={*=[[@1,7:7='*',<291>,1:7]]}, table_dictionary={tab1={*=[[@1,7:7='*',<291>,1:7]], col4=[[@26,80:83='col4',<381>,3:5]], col5=[[@32,95:98='col5',<381>,3:20]], col2=[[@14,47:50='col2',<381>,2:27], [@36,109:112='col2',<381>,3:34]], col3=[[@20,64:67='col3',<381>,2:44]], col1=[[@8,32:35='col1',<381>,2:12]]}}, interface={*=[{name=*, table_ref=*}]}, derived_columns={units_cnt=[{name=col3, table_ref=tab1}], units_sum=[{name=col1, table_ref=tab1}], sales_sum=[{name=col1, table_ref=tab1}], sales_min=[{name=col5, table_ref=tab1}], sales_ave=[{name=col2, table_ref=tab1}], units_ave=[{name=col2, table_ref=tab1}], sales_max=[{name=col4, table_ref=tab1}], units_min=[{name=col5, table_ref=tab1}], sales_cnt=[{name=col3, table_ref=tab1}], units_max=[{name=col4, table_ref=tab1}]}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void pivotV4QueryTest() {
		final String query = "select sales_sum, sales_ave, sales_cnt, sales_max, sales_min, units_sum, units_ave, units_cnt, units_max, units_min from (select col1, col2, col3, col4, col5 from tab1) q "
		+"\n pivot (sum(col1) sum, avg(col2) ave, count(col3) cnt,"
		+"\n max(col4) max, min(col5) min for col2 in ('sales', 'units'))";

		final SQLSelectParserParser parser = parse(query);
		ParserRunResult runResult = runSQLParsertestAllowErrors(query, parser);
		SqlParseEventWalker extractor = runResult.getExtractor();

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=sales_sum, table_ref=null}}, 2={column={name=sales_ave, table_ref=null}}, 3={column={name=sales_cnt, table_ref=null}}, 4={column={name=sales_max, table_ref=null}}, 5={column={name=sales_min, table_ref=null}}, 6={column={name=units_sum, table_ref=null}}, 7={column={name=units_ave, table_ref=null}}, 8={column={name=units_cnt, table_ref=null}}, 9={column={name=units_max, table_ref=null}}, 10={column={name=units_min, table_ref=null}}}, from={pivot={value={1={function={function_name=sum, parameters={column={name=col1, table_ref=null}}}, alias=sum}, 2={function={function_name=avg, parameters={column={name=col2, table_ref=null}}}, alias=ave}, 3={function={function_name=count, parameters={column={name=col3, table_ref=null}}}, alias=cnt}, 4={function={function_name=max, parameters={column={name=col4, table_ref=null}}}, alias=max}, 5={function={function_name=min, parameters={column={name=col5, table_ref=null}}}, alias=min}}, for={column={name=col2, table_ref=null}}, in={1={pivot_literal='sales'}, 2={pivot_literal='units'}}}, table={alias=q, query={select={1={column={name=col1, table_ref=null}}, 2={column={name=col2, table_ref=null}}, 3={column={name=col3, table_ref=null}}, 4={column={name=col4, table_ref=null}}, 5={column={name=col5, table_ref=null}}}, from={table={alias=null, table=tab1}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[units_cnt, units_sum, sales_sum, sales_min, sales_ave, units_ave, sales_max, units_min, sales_cnt, units_max]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{tab1={col4=[[@29,147:150='col4',<381>,1:147]], col5=[[@31,153:156='col5',<381>,1:153]], col2=[[@25,135:138='col2',<381>,1:135]], col3=[[@27,141:144='col3',<381>,1:141]], col1=[[@23,129:132='col1',<381>,1:129]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{query0={col4=[[@29,147:150='col4',<381>,1:147], [@58,232:235='col4',<381>,3:5]], col5=[[@31,153:156='col5',<381>,1:153], [@64,247:250='col5',<381>,3:20]], col2=[[@25,135:138='col2',<381>,1:135], [@46,199:202='col2',<381>,2:27], [@68,261:264='col2',<381>,3:34]], col3=[[@27,141:144='col3',<381>,1:141], [@52,216:219='col3',<381>,2:44]], col1=[[@23,129:132='col1',<381>,1:129], [@40,184:187='col1',<381>,2:12]]}, query2={units_cnt=[[@15,84:92='units_cnt',<381>,1:84]], units_sum=[[@11,62:70='units_sum',<381>,1:62]], sales_sum=[[@1,7:15='sales_sum',<381>,1:7]], sales_min=[[@9,51:59='sales_min',<381>,1:51]], sales_ave=[[@3,18:26='sales_ave',<381>,1:18]], units_ave=[[@13,73:81='units_ave',<381>,1:73]], sales_max=[[@7,40:48='sales_max',<381>,1:40]], units_min=[[@19,106:114='units_min',<381>,1:106]], sales_cnt=[[@5,29:37='sales_cnt',<381>,1:29]], units_max=[[@17,95:103='units_max',<381>,1:95]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query2={query_dictionary={units_cnt=[[@15,84:92='units_cnt',<381>,1:84]], units_sum=[[@11,62:70='units_sum',<381>,1:62]], sales_sum=[[@1,7:15='sales_sum',<381>,1:7]], sales_min=[[@9,51:59='sales_min',<381>,1:51]], sales_ave=[[@3,18:26='sales_ave',<381>,1:18]], units_ave=[[@13,73:81='units_ave',<381>,1:73]], sales_max=[[@7,40:48='sales_max',<381>,1:40]], units_min=[[@19,106:114='units_min',<381>,1:106]], sales_cnt=[[@5,29:37='sales_cnt',<381>,1:29]], units_max=[[@17,95:103='units_max',<381>,1:95]]}, def_query0={query_dictionary={col4=[[@29,147:150='col4',<381>,1:147], [@58,232:235='col4',<381>,3:5]], col5=[[@31,153:156='col5',<381>,1:153], [@64,247:250='col5',<381>,3:20]], col2=[[@25,135:138='col2',<381>,1:135], [@46,199:202='col2',<381>,2:27], [@68,261:264='col2',<381>,3:34]], col3=[[@27,141:144='col3',<381>,1:141], [@52,216:219='col3',<381>,2:44]], col1=[[@23,129:132='col1',<381>,1:129], [@40,184:187='col1',<381>,2:12]]}, table_dictionary={tab1={col4=[[@29,147:150='col4',<381>,1:147]], col5=[[@31,153:156='col5',<381>,1:153]], col2=[[@25,135:138='col2',<381>,1:135]], col3=[[@27,141:144='col3',<381>,1:141]], col1=[[@23,129:132='col1',<381>,1:129]]}}, interface={col4=[{name=col4, table_ref=tab1}], col5=[{name=col5, table_ref=tab1}], col2=[{name=col2, table_ref=tab1}], col3=[{name=col3, table_ref=tab1}], col1=[{name=col1, table_ref=tab1}]}}, interface={units_cnt=[{name=units_cnt, table_ref=null}], units_sum=[{name=units_sum, table_ref=null}], sales_sum=[{name=sales_sum, table_ref=null}], sales_min=[{name=sales_min, table_ref=null}], sales_ave=[{name=sales_ave, table_ref=null}], units_ave=[{name=units_ave, table_ref=null}], sales_max=[{name=sales_max, table_ref=null}], units_min=[{name=units_min, table_ref=null}], sales_cnt=[{name=sales_cnt, table_ref=null}], units_max=[{name=units_max, table_ref=null}]}, derived_columns={units_cnt=[{name=col3, table_ref=q}], units_sum=[{name=col1, table_ref=q}], sales_sum=[{name=col1, table_ref=q}], sales_min=[{name=col5, table_ref=q}], sales_ave=[{name=col2, table_ref=q}], units_ave=[{name=col2, table_ref=q}], sales_max=[{name=col4, table_ref=q}], units_min=[{name=col5, table_ref=q}], sales_cnt=[{name=col3, table_ref=q}], units_max=[{name=col4, table_ref=q}]}, table_alias={q=query0}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void pivotBasicMetricColumnsV1Test() {
		final String query =
			"SELECT id, jan_sales, feb_sales, mar_sales\n" +
			"FROM my_table\n" +
			"PIVOT (SUM(metric_value) FOR metric_name IN ('jan_sales', 'feb_sales', 'mar_sales'));";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoFatalErrors(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=id, table_ref=null}}, 2={column={name=jan_sales, table_ref=null}}, 3={column={name=feb_sales, table_ref=null}}, 4={column={name=mar_sales, table_ref=null}}}, from={pivot={value={function={function_name=SUM, parameters={column={name=metric_value, table_ref=null}}}}, for={column={name=metric_name, table_ref=null}}, in={1={pivot_literal='jan_sales'}, 2={pivot_literal='feb_sales'}, 3={pivot_literal='mar_sales'}}}, table={alias=null, table=my_table}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[jan_sales, mar_sales, id, feb_sales]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{my_table={jan_sales=[[@3,11:19='jan_sales',<381>,1:11]], mar_sales=[[@7,33:41='mar_sales',<381>,1:33]], metric_name=[[@17,86:96='metric_name',<381>,3:29]], metric_value=[[@14,68:79='metric_value',<381>,3:11]], id=[[@1,7:8='id',<381>,1:7]], feb_sales=[[@5,22:30='feb_sales',<381>,1:22]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query1={jan_sales=[[@3,11:19='jan_sales',<381>,1:11]], mar_sales=[[@7,33:41='mar_sales',<381>,1:33]], id=[[@1,7:8='id',<381>,1:7]], feb_sales=[[@5,22:30='feb_sales',<381>,1:22]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query1={query_dictionary={jan_sales=[[@3,11:19='jan_sales',<381>,1:11]], mar_sales=[[@7,33:41='mar_sales',<381>,1:33]], id=[[@1,7:8='id',<381>,1:7]], feb_sales=[[@5,22:30='feb_sales',<381>,1:22]]}, table_dictionary={my_table={jan_sales=[[@3,11:19='jan_sales',<381>,1:11]], mar_sales=[[@7,33:41='mar_sales',<381>,1:33]], metric_name=[[@17,86:96='metric_name',<381>,3:29]], metric_value=[[@14,68:79='metric_value',<381>,3:11]], id=[[@1,7:8='id',<381>,1:7]], feb_sales=[[@5,22:30='feb_sales',<381>,1:22]]}}, interface={jan_sales=[{name=jan_sales, table_ref=my_table}], mar_sales=[{name=mar_sales, table_ref=my_table}], id=[{name=id, table_ref=my_table}], feb_sales=[{name=feb_sales, table_ref=my_table}]}, derived_columns={jan_sales_SUM=[{name=metric_value, table_ref=my_table}], mar_sales_SUM=[{name=metric_value, table_ref=my_table}], feb_sales_SUM=[{name=metric_value, table_ref=my_table}]}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void pivotTableWithInAliasesJanFebMarV2Test() {
		final String query =
			"SELECT empid, units, jan_sales, feb_sales, mar_sales\n" +
			"FROM monthly_sales_long\n" +
			"PIVOT (SUM(sales_amount) FOR month_name IN ('jan_sales', 'feb_sales', 'mar_sales'))\n" +
			"WHERE units > 1.00;";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoFatalErrors(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=empid, table_ref=null}}, 2={column={name=units, table_ref=null}}, 3={column={name=jan_sales, table_ref=null}}, 4={column={name=feb_sales, table_ref=null}}, 5={column={name=mar_sales, table_ref=null}}}, from={pivot={value={function={function_name=SUM, parameters={column={name=sales_amount, table_ref=null}}}}, for={column={name=month_name, table_ref=null}}, in={1={pivot_literal='jan_sales'}, 2={pivot_literal='feb_sales'}, 3={pivot_literal='mar_sales'}}}, table={alias=null, table=monthly_sales_long}}, where={condition={left={column={name=units, table_ref=null}}, right={literal=1.00}, operator=>}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[jan_sales, empid, mar_sales, units, feb_sales]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{monthly_sales_long={jan_sales=[[@5,21:29='jan_sales',<381>,1:21]], month_name=[[@19,106:115='month_name',<381>,3:29]], empid=[[@1,7:11='empid',<381>,1:7]], mar_sales=[[@9,43:51='mar_sales',<381>,1:43]], sales_amount=[[@16,88:99='sales_amount',<381>,3:11]], units=[[@3,14:18='units',<381>,1:14], [@30,167:171='units',<381>,4:6]], feb_sales=[[@7,32:40='feb_sales',<381>,1:32]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query1={jan_sales=[[@5,21:29='jan_sales',<381>,1:21]], empid=[[@1,7:11='empid',<381>,1:7]], mar_sales=[[@9,43:51='mar_sales',<381>,1:43]], units=[[@3,14:18='units',<381>,1:14]], feb_sales=[[@7,32:40='feb_sales',<381>,1:32]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query1={query_dictionary={jan_sales=[[@5,21:29='jan_sales',<381>,1:21]], empid=[[@1,7:11='empid',<381>,1:7]], mar_sales=[[@9,43:51='mar_sales',<381>,1:43]], units=[[@3,14:18='units',<381>,1:14]], feb_sales=[[@7,32:40='feb_sales',<381>,1:32]]}, table_dictionary={monthly_sales_long={jan_sales=[[@5,21:29='jan_sales',<381>,1:21]], month_name=[[@19,106:115='month_name',<381>,3:29]], empid=[[@1,7:11='empid',<381>,1:7]], mar_sales=[[@9,43:51='mar_sales',<381>,1:43]], sales_amount=[[@16,88:99='sales_amount',<381>,3:11]], units=[[@3,14:18='units',<381>,1:14], [@30,167:171='units',<381>,4:6]], feb_sales=[[@7,32:40='feb_sales',<381>,1:32]]}}, filters=[{name=units, table_ref=monthly_sales_long}], interface={jan_sales=[{name=jan_sales, table_ref=monthly_sales_long}], empid=[{name=empid, table_ref=monthly_sales_long}], mar_sales=[{name=mar_sales, table_ref=monthly_sales_long}], units=[{name=units, table_ref=monthly_sales_long}], feb_sales=[{name=feb_sales, table_ref=monthly_sales_long}]}, derived_columns={jan_sales_SUM=[{name=sales_amount, table_ref=monthly_sales_long}], mar_sales_SUM=[{name=sales_amount, table_ref=monthly_sales_long}], feb_sales_SUM=[{name=sales_amount, table_ref=monthly_sales_long}]}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void pivotTableWithGroupByAndOrderByV2GroupOrderTest() {
		final String query =
			"SELECT empid, jan_sales, feb_sales, mar_sales\n" +
			"FROM monthly_sales_long\n" +
			"PIVOT (SUM(sales_amount) FOR month_name IN ('jan_sales', 'feb_sales', 'mar_sales'))\n" +
			"GROUP BY empid, jan_sales, feb_sales, mar_sales\n" +
			"ORDER BY jan_sales;";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoFatalErrors(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=empid, table_ref=null}}, 2={column={name=jan_sales, table_ref=null}}, 3={column={name=feb_sales, table_ref=null}}, 4={column={name=mar_sales, table_ref=null}}}, orderby={1={null_order=null, predicand={column={name=jan_sales, table_ref=null}}, sort_order=ASC}}, from={pivot={value={function={function_name=SUM, parameters={column={name=sales_amount, table_ref=null}}}}, for={column={name=month_name, table_ref=null}}, in={1={pivot_literal='jan_sales'}, 2={pivot_literal='feb_sales'}, 3={pivot_literal='mar_sales'}}}, table={alias=null, table=monthly_sales_long}}, groupby={1={column={name=empid, table_ref=null}}, 2={column={name=jan_sales, table_ref=null}}, 3={column={name=feb_sales, table_ref=null}}, 4={column={name=mar_sales, table_ref=null}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[jan_sales, empid, mar_sales, feb_sales]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{monthly_sales_long={jan_sales=[[@3,14:22='jan_sales',<381>,1:14], [@31,170:178='jan_sales',<381>,4:16], [@38,211:219='jan_sales',<381>,5:9]], month_name=[[@17,99:108='month_name',<381>,3:29]], empid=[[@1,7:11='empid',<381>,1:7], [@29,163:167='empid',<381>,4:9]], mar_sales=[[@7,36:44='mar_sales',<381>,1:36], [@35,192:200='mar_sales',<381>,4:38]], sales_amount=[[@14,81:92='sales_amount',<381>,3:11]], feb_sales=[[@5,25:33='feb_sales',<381>,1:25], [@33,181:189='feb_sales',<381>,4:27]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query1={jan_sales=[[@3,14:22='jan_sales',<381>,1:14]], empid=[[@1,7:11='empid',<381>,1:7]], mar_sales=[[@7,36:44='mar_sales',<381>,1:36]], feb_sales=[[@5,25:33='feb_sales',<381>,1:25]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query1={query_dictionary={jan_sales=[[@3,14:22='jan_sales',<381>,1:14]], empid=[[@1,7:11='empid',<381>,1:7]], mar_sales=[[@7,36:44='mar_sales',<381>,1:36]], feb_sales=[[@5,25:33='feb_sales',<381>,1:25]]}, table_dictionary={monthly_sales_long={jan_sales=[[@3,14:22='jan_sales',<381>,1:14], [@31,170:178='jan_sales',<381>,4:16], [@38,211:219='jan_sales',<381>,5:9]], month_name=[[@17,99:108='month_name',<381>,3:29]], empid=[[@1,7:11='empid',<381>,1:7], [@29,163:167='empid',<381>,4:9]], mar_sales=[[@7,36:44='mar_sales',<381>,1:36], [@35,192:200='mar_sales',<381>,4:38]], sales_amount=[[@14,81:92='sales_amount',<381>,3:11]], feb_sales=[[@5,25:33='feb_sales',<381>,1:25], [@33,181:189='feb_sales',<381>,4:27]]}}, grouped_by=[{name=empid, table_ref=monthly_sales_long}, {name=jan_sales, table_ref=monthly_sales_long}, {name=feb_sales, table_ref=monthly_sales_long}, {name=mar_sales, table_ref=monthly_sales_long}], ordered_by=[{name=jan_sales, table_ref=monthly_sales_long}], interface={jan_sales=[{name=jan_sales, table_ref=monthly_sales_long}], empid=[{name=empid, table_ref=monthly_sales_long}], mar_sales=[{name=mar_sales, table_ref=monthly_sales_long}], feb_sales=[{name=feb_sales, table_ref=monthly_sales_long}]}, derived_columns={jan_sales_SUM=[{name=sales_amount, table_ref=monthly_sales_long}], mar_sales_SUM=[{name=sales_amount, table_ref=monthly_sales_long}], feb_sales_SUM=[{name=sales_amount, table_ref=monthly_sales_long}]}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void pivotTableWithHavingAndOrderByV2HavingOrderTest() {
		final String query =
			"SELECT empid, jan_sales, feb_sales, mar_sales\n" +
			"FROM monthly_sales_long\n" +
			"PIVOT (SUM(sales_amount) FOR month_name IN ('jan_sales', 'feb_sales', 'mar_sales'))\n" +
			"GROUP BY empid, jan_sales, feb_sales, mar_sales\n" +
			"HAVING jan_sales > 100\n" +
			"ORDER BY jan_sales;";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoFatalErrors(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=empid, table_ref=null}}, 2={column={name=jan_sales, table_ref=null}}, 3={column={name=feb_sales, table_ref=null}}, 4={column={name=mar_sales, table_ref=null}}}, having={condition={left={column={name=jan_sales, table_ref=null}}, right={literal=100}, operator=>}}, orderby={1={null_order=null, predicand={column={name=jan_sales, table_ref=null}}, sort_order=ASC}}, from={pivot={value={function={function_name=SUM, parameters={column={name=sales_amount, table_ref=null}}}}, for={column={name=month_name, table_ref=null}}, in={1={pivot_literal='jan_sales'}, 2={pivot_literal='feb_sales'}, 3={pivot_literal='mar_sales'}}}, table={alias=null, table=monthly_sales_long}}, groupby={1={column={name=empid, table_ref=null}}, 2={column={name=jan_sales, table_ref=null}}, 3={column={name=feb_sales, table_ref=null}}, 4={column={name=mar_sales, table_ref=null}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[jan_sales, empid, mar_sales, feb_sales]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{monthly_sales_long={jan_sales=[[@3,14:22='jan_sales',<381>,1:14], [@31,170:178='jan_sales',<381>,4:16], [@37,209:217='jan_sales',<381>,5:7], [@42,234:242='jan_sales',<381>,6:9]], month_name=[[@17,99:108='month_name',<381>,3:29]], empid=[[@1,7:11='empid',<381>,1:7], [@29,163:167='empid',<381>,4:9]], mar_sales=[[@7,36:44='mar_sales',<381>,1:36], [@35,192:200='mar_sales',<381>,4:38]], sales_amount=[[@14,81:92='sales_amount',<381>,3:11]], feb_sales=[[@5,25:33='feb_sales',<381>,1:25], [@33,181:189='feb_sales',<381>,4:27]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query1={jan_sales=[[@3,14:22='jan_sales',<381>,1:14]], empid=[[@1,7:11='empid',<381>,1:7]], mar_sales=[[@7,36:44='mar_sales',<381>,1:36]], feb_sales=[[@5,25:33='feb_sales',<381>,1:25]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query1={query_dictionary={jan_sales=[[@3,14:22='jan_sales',<381>,1:14]], empid=[[@1,7:11='empid',<381>,1:7]], mar_sales=[[@7,36:44='mar_sales',<381>,1:36]], feb_sales=[[@5,25:33='feb_sales',<381>,1:25]]}, table_dictionary={monthly_sales_long={jan_sales=[[@3,14:22='jan_sales',<381>,1:14], [@31,170:178='jan_sales',<381>,4:16], [@37,209:217='jan_sales',<381>,5:7], [@42,234:242='jan_sales',<381>,6:9]], month_name=[[@17,99:108='month_name',<381>,3:29]], empid=[[@1,7:11='empid',<381>,1:7], [@29,163:167='empid',<381>,4:9]], mar_sales=[[@7,36:44='mar_sales',<381>,1:36], [@35,192:200='mar_sales',<381>,4:38]], sales_amount=[[@14,81:92='sales_amount',<381>,3:11]], feb_sales=[[@5,25:33='feb_sales',<381>,1:25], [@33,181:189='feb_sales',<381>,4:27]]}}, grouped_by=[{name=empid, table_ref=monthly_sales_long}, {name=jan_sales, table_ref=monthly_sales_long}, {name=feb_sales, table_ref=monthly_sales_long}, {name=mar_sales, table_ref=monthly_sales_long}], ordered_by=[{name=jan_sales, table_ref=monthly_sales_long}], filters=[{name=jan_sales, table_ref=monthly_sales_long}], interface={jan_sales=[{name=jan_sales, table_ref=monthly_sales_long}], empid=[{name=empid, table_ref=monthly_sales_long}], mar_sales=[{name=mar_sales, table_ref=monthly_sales_long}], feb_sales=[{name=feb_sales, table_ref=monthly_sales_long}]}, derived_columns={jan_sales_SUM=[{name=sales_amount, table_ref=monthly_sales_long}], mar_sales_SUM=[{name=sales_amount, table_ref=monthly_sales_long}], feb_sales_SUM=[{name=sales_amount, table_ref=monthly_sales_long}]}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void pivotTableJoinOnWithUnqualifiedJanSalesProbeTest() {
		final String query =
			"SELECT empid, jan_sales, p.target_amount\n" +
			"FROM monthly_sales_long\n" +
			"PIVOT (SUM(sales_amount) FOR month_name IN ('jan_sales', 'feb_sales', 'mar_sales'))\n" +
			"JOIN targets p ON jan_sales >= p.target_amount;";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoFatalErrors(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=empid, table_ref=null}}, 2={column={name=jan_sales, table_ref=null}}, 3={column={name=target_amount, table_ref=p}}}, from={join={1={pivot={value={function={function_name=SUM, parameters={column={name=sales_amount, table_ref=null}}}}, for={column={name=month_name, table_ref=null}}, in={1={pivot_literal='jan_sales'}, 2={pivot_literal='feb_sales'}, 3={pivot_literal='mar_sales'}}}, table={alias=null, table=monthly_sales_long}}, 2={join=JOIN, on={condition={left={column={name=jan_sales, table_ref=null}}, right={column={name=target_amount, table_ref=p}}, operator=>=}}}, 3={table={alias=p, table=targets}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[jan_sales, empid, target_amount]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{targets={month_name=[[@17,94:103='month_name',<381>,3:29]], target_amount=[[@5,25:25='p',<381>,1:25], [@33,180:180='p',<381>,4:31]], sales_amount=[[@14,76:87='sales_amount',<381>,3:11]]}, monthly_sales_long={}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query1={jan_sales=[[@3,14:22='jan_sales',<381>,1:14]], empid=[[@1,7:11='empid',<381>,1:7]], target_amount=[[@7,27:39='target_amount',<381>,1:27]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query1={query_dictionary={jan_sales=[[@3,14:22='jan_sales',<381>,1:14]], empid=[[@1,7:11='empid',<381>,1:7]], target_amount=[[@7,27:39='target_amount',<381>,1:27]]}, table_dictionary={targets={month_name=[[@17,94:103='month_name',<381>,3:29]], target_amount=[[@5,25:25='p',<381>,1:25], [@33,180:180='p',<381>,4:31]], sales_amount=[[@14,76:87='sales_amount',<381>,3:11]]}, monthly_sales_long={}}, filters=[{name=jan_sales, table_ref=null}, {name=target_amount, table_ref=p}], interface={jan_sales=[{name=jan_sales, table_ref=null}], empid=[{name=empid, table_ref=null}], target_amount=[{name=target_amount, table_ref=p}]}, derived_columns={jan_sales_SUM=[{name=sales_amount, table_ref=monthly_sales_long}], mar_sales_SUM=[{name=sales_amount, table_ref=monthly_sales_long}], feb_sales_SUM=[{name=sales_amount, table_ref=monthly_sales_long}]}, table_alias={p=targets}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void pivotTableWithQualifyJanSalesProbeTest() {
		final String query =
			"SELECT empid, jan_sales, feb_sales, mar_sales\n" +
			"FROM monthly_sales_long\n" +
			"PIVOT (SUM(sales_amount) FOR month_name IN ('jan_sales', 'feb_sales', 'mar_sales'))\n" +
			"QUALIFY jan_sales > 100;";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoFatalErrors(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=empid, table_ref=null}}, 2={column={name=jan_sales, table_ref=null}}, 3={column={name=feb_sales, table_ref=null}}, 4={column={name=mar_sales, table_ref=null}}}, from={pivot={value={function={function_name=SUM, parameters={column={name=sales_amount, table_ref=null}}}}, for={column={name=month_name, table_ref=null}}, in={1={pivot_literal='jan_sales'}, 2={pivot_literal='feb_sales'}, 3={pivot_literal='mar_sales'}}}, table={alias=null, table=monthly_sales_long}}, qualify={condition={left={column={name=jan_sales, table_ref=null}}, right={literal=100}, operator=>}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[jan_sales, empid, mar_sales, feb_sales]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{monthly_sales_long={jan_sales=[[@3,14:22='jan_sales',<381>,1:14], [@28,162:170='jan_sales',<381>,4:8]], month_name=[[@17,99:108='month_name',<381>,3:29]], empid=[[@1,7:11='empid',<381>,1:7]], mar_sales=[[@7,36:44='mar_sales',<381>,1:36]], sales_amount=[[@14,81:92='sales_amount',<381>,3:11]], feb_sales=[[@5,25:33='feb_sales',<381>,1:25]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query1={jan_sales=[[@3,14:22='jan_sales',<381>,1:14]], empid=[[@1,7:11='empid',<381>,1:7]], mar_sales=[[@7,36:44='mar_sales',<381>,1:36]], feb_sales=[[@5,25:33='feb_sales',<381>,1:25]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query1={query_dictionary={jan_sales=[[@3,14:22='jan_sales',<381>,1:14]], empid=[[@1,7:11='empid',<381>,1:7]], mar_sales=[[@7,36:44='mar_sales',<381>,1:36]], feb_sales=[[@5,25:33='feb_sales',<381>,1:25]]}, table_dictionary={monthly_sales_long={jan_sales=[[@3,14:22='jan_sales',<381>,1:14], [@28,162:170='jan_sales',<381>,4:8]], month_name=[[@17,99:108='month_name',<381>,3:29]], empid=[[@1,7:11='empid',<381>,1:7]], mar_sales=[[@7,36:44='mar_sales',<381>,1:36]], sales_amount=[[@14,81:92='sales_amount',<381>,3:11]], feb_sales=[[@5,25:33='feb_sales',<381>,1:25]]}}, filters=[{name=jan_sales, table_ref=monthly_sales_long}], interface={jan_sales=[{name=jan_sales, table_ref=monthly_sales_long}], empid=[{name=empid, table_ref=monthly_sales_long}], mar_sales=[{name=mar_sales, table_ref=monthly_sales_long}], feb_sales=[{name=feb_sales, table_ref=monthly_sales_long}]}, derived_columns={jan_sales_SUM=[{name=sales_amount, table_ref=monthly_sales_long}], mar_sales_SUM=[{name=sales_amount, table_ref=monthly_sales_long}], feb_sales_SUM=[{name=sales_amount, table_ref=monthly_sales_long}]}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void pivotTableWithOrderByExpressionJanFebProbeTest() {
		final String query =
			"SELECT empid, jan_sales, feb_sales, mar_sales\n" +
			"FROM monthly_sales_long\n" +
			"PIVOT (SUM(sales_amount) FOR month_name IN ('jan_sales', 'feb_sales', 'mar_sales'))\n" +
			"ORDER BY jan_sales / feb_sales;";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoFatalErrors(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=empid, table_ref=null}}, 2={column={name=jan_sales, table_ref=null}}, 3={column={name=feb_sales, table_ref=null}}, 4={column={name=mar_sales, table_ref=null}}}, orderby={1={null_order=null, predicand={calc={left={column={name=jan_sales, table_ref=null}}, right={column={name=feb_sales, table_ref=null}}, operator=/}}, sort_order=ASC}}, from={pivot={value={function={function_name=SUM, parameters={column={name=sales_amount, table_ref=null}}}}, for={column={name=month_name, table_ref=null}}, in={1={pivot_literal='jan_sales'}, 2={pivot_literal='feb_sales'}, 3={pivot_literal='mar_sales'}}}, table={alias=null, table=monthly_sales_long}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[jan_sales, empid, mar_sales, feb_sales]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{monthly_sales_long={jan_sales=[[@3,14:22='jan_sales',<381>,1:14], [@29,163:171='jan_sales',<381>,4:9]], month_name=[[@17,99:108='month_name',<381>,3:29]], empid=[[@1,7:11='empid',<381>,1:7]], mar_sales=[[@7,36:44='mar_sales',<381>,1:36]], sales_amount=[[@14,81:92='sales_amount',<381>,3:11]], feb_sales=[[@5,25:33='feb_sales',<381>,1:25], [@31,175:183='feb_sales',<381>,4:21]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query1={jan_sales=[[@3,14:22='jan_sales',<381>,1:14]], empid=[[@1,7:11='empid',<381>,1:7]], mar_sales=[[@7,36:44='mar_sales',<381>,1:36]], feb_sales=[[@5,25:33='feb_sales',<381>,1:25]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query1={query_dictionary={jan_sales=[[@3,14:22='jan_sales',<381>,1:14]], empid=[[@1,7:11='empid',<381>,1:7]], mar_sales=[[@7,36:44='mar_sales',<381>,1:36]], feb_sales=[[@5,25:33='feb_sales',<381>,1:25]]}, table_dictionary={monthly_sales_long={jan_sales=[[@3,14:22='jan_sales',<381>,1:14], [@29,163:171='jan_sales',<381>,4:9]], month_name=[[@17,99:108='month_name',<381>,3:29]], empid=[[@1,7:11='empid',<381>,1:7]], mar_sales=[[@7,36:44='mar_sales',<381>,1:36]], sales_amount=[[@14,81:92='sales_amount',<381>,3:11]], feb_sales=[[@5,25:33='feb_sales',<381>,1:25], [@31,175:183='feb_sales',<381>,4:21]]}}, ordered_by=[{name=jan_sales, table_ref=monthly_sales_long}, {name=feb_sales, table_ref=monthly_sales_long}], interface={jan_sales=[{name=jan_sales, table_ref=monthly_sales_long}], empid=[{name=empid, table_ref=monthly_sales_long}], mar_sales=[{name=mar_sales, table_ref=monthly_sales_long}], feb_sales=[{name=feb_sales, table_ref=monthly_sales_long}]}, derived_columns={jan_sales_SUM=[{name=sales_amount, table_ref=monthly_sales_long}], mar_sales_SUM=[{name=sales_amount, table_ref=monthly_sales_long}], feb_sales_SUM=[{name=sales_amount, table_ref=monthly_sales_long}]}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void pivotFromDerivedAdjustedColumnsV3Test() {
		final String query =
			"SELECT empid, month_name,  jan_adjusted_SUM, feb_adjusted_SUM\n" +
			"FROM (SELECT empid, month_name, sales_amount * 1.10 AS adjusted_sales FROM monthly_sales_long)\n" +
			"PIVOT (SUM(adjusted_sales) FOR month_name IN ('jan_adjusted', 'feb_adjusted'));";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoFatalErrors(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=empid, table_ref=null}}, 2={column={name=month_name, table_ref=null}}, 3={column={name=jan_adjusted_SUM, table_ref=null}}, 4={column={name=feb_adjusted_SUM, table_ref=null}}}, from={select={1={column={name=empid, table_ref=null}}, 2={column={name=month_name, table_ref=null}}, 3={alias=adjusted_sales, calc={left={column={name=sales_amount, table_ref=null}}, right={literal=1.10}, operator=*}}}, pivot={value={function={function_name=SUM, parameters={column={name=adjusted_sales, table_ref=null}}}}, for={column={name=month_name, table_ref=null}}, in={1={pivot_literal='jan_adjusted'}, 2={pivot_literal='feb_adjusted'}}}, from={table={alias=null, table=monthly_sales_long}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[empid, month_name, jan_adjusted_SUM, feb_adjusted_SUM]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
		 "{monthly_sales_long={empid=[[@11,75:79='empid',<381>,2:13]], month_name=[[@13,82:91='month_name',<381>,2:20]], sales_amount=[[@15,94:105='sales_amount',<381>,2:32]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
		 "{query0={empid=[[@11,75:79='empid',<381>,2:13], [@1,7:11='empid',<381>,1:7]], month_name=[[@13,82:91='month_name',<381>,2:20], [@32,188:197='month_name',<381>,3:31]], adjusted_sales=[[@21,117:130='adjusted_sales',<381>,2:55], [@29,168:181='adjusted_sales',<381>,3:11]]}, query2={empid=[[@1,7:11='empid',<381>,1:7]], month_name=[[@3,14:23='month_name',<381>,1:14]], jan_adjusted_SUM=[[@5,27:42='jan_adjusted_SUM',<381>,1:27]], feb_adjusted_SUM=[[@7,45:60='feb_adjusted_SUM',<381>,1:45]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query2={query_dictionary={empid=[[@1,7:11='empid',<381>,1:7]], month_name=[[@3,14:23='month_name',<381>,1:14]], jan_adjusted_SUM=[[@5,27:42='jan_adjusted_SUM',<381>,1:27]], feb_adjusted_SUM=[[@7,45:60='feb_adjusted_SUM',<381>,1:45]]}, def_query0={query_dictionary={empid=[[@11,75:79='empid',<381>,2:13], [@1,7:11='empid',<381>,1:7]], month_name=[[@13,82:91='month_name',<381>,2:20], [@32,188:197='month_name',<381>,3:31]], adjusted_sales=[[@21,117:130='adjusted_sales',<381>,2:55], [@29,168:181='adjusted_sales',<381>,3:11]]}, table_dictionary={monthly_sales_long={empid=[[@11,75:79='empid',<381>,2:13]], month_name=[[@13,82:91='month_name',<381>,2:20]], sales_amount=[[@15,94:105='sales_amount',<381>,2:32]]}}, interface={empid=[{name=empid, table_ref=monthly_sales_long}], month_name=[{name=month_name, table_ref=monthly_sales_long}], adjusted_sales=[{name=sales_amount, table_ref=monthly_sales_long}]}}, interface={empid=[{name=empid, table_ref=query0}], month_name=[{name=month_name, table_ref=query0}], jan_adjusted_SUM=[{name=jan_adjusted_SUM, table_ref=null}], feb_adjusted_SUM=[{name=feb_adjusted_SUM, table_ref=null}]}, derived_columns={jan_adjusted_SUM=[], feb_adjusted_SUM=[]}, table_alias={query0=query0}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void pivotWithTaxAndWhereV4Test() {
		final String query =
			"SELECT empid, jan_sales, feb_sales, mar_sales, jan_sales * 0.07 AS tax\n" +
			"FROM monthly_sales_long\n" +
			"PIVOT (SUM(sales_amount) FOR month_name IN ('jan_sales', 'feb_sales', 'mar_sales')) WHERE empid > 100;";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoFatalErrors(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=empid, table_ref=null}}, 2={column={name=jan_sales, table_ref=null}}, 3={column={name=feb_sales, table_ref=null}}, 4={column={name=mar_sales, table_ref=null}}, 5={alias=tax, calc={left={column={name=jan_sales, table_ref=null}}, right={literal=0.07}, operator=*}}}, from={pivot={value={function={function_name=SUM, parameters={column={name=sales_amount, table_ref=null}}}}, for={column={name=month_name, table_ref=null}}, in={1={pivot_literal='jan_sales'}, 2={pivot_literal='feb_sales'}, 3={pivot_literal='mar_sales'}}}, table={alias=null, table=monthly_sales_long}}, where={condition={left={column={name=empid, table_ref=null}}, right={literal=100}, operator=>}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[jan_sales, empid, mar_sales, tax, feb_sales]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{monthly_sales_long={jan_sales=[[@3,14:22='jan_sales',<381>,1:14], [@9,47:55='jan_sales',<381>,1:47]], month_name=[[@25,124:133='month_name',<381>,3:29]], empid=[[@1,7:11='empid',<381>,1:7], [@36,185:189='empid',<381>,3:90]], mar_sales=[[@7,36:44='mar_sales',<381>,1:36]], sales_amount=[[@22,106:117='sales_amount',<381>,3:11]], feb_sales=[[@5,25:33='feb_sales',<381>,1:25]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query1={jan_sales=[[@3,14:22='jan_sales',<381>,1:14]], empid=[[@1,7:11='empid',<381>,1:7]], mar_sales=[[@7,36:44='mar_sales',<381>,1:36]], tax=[[@15,67:69='tax',<381>,1:67]], feb_sales=[[@5,25:33='feb_sales',<381>,1:25]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query1={query_dictionary={jan_sales=[[@3,14:22='jan_sales',<381>,1:14]], empid=[[@1,7:11='empid',<381>,1:7]], mar_sales=[[@7,36:44='mar_sales',<381>,1:36]], tax=[[@15,67:69='tax',<381>,1:67]], feb_sales=[[@5,25:33='feb_sales',<381>,1:25]]}, table_dictionary={monthly_sales_long={jan_sales=[[@3,14:22='jan_sales',<381>,1:14], [@9,47:55='jan_sales',<381>,1:47]], month_name=[[@25,124:133='month_name',<381>,3:29]], empid=[[@1,7:11='empid',<381>,1:7], [@36,185:189='empid',<381>,3:90]], mar_sales=[[@7,36:44='mar_sales',<381>,1:36]], sales_amount=[[@22,106:117='sales_amount',<381>,3:11]], feb_sales=[[@5,25:33='feb_sales',<381>,1:25]]}}, filters=[{name=empid, table_ref=monthly_sales_long}], interface={jan_sales=[{name=jan_sales, table_ref=monthly_sales_long}], empid=[{name=empid, table_ref=monthly_sales_long}], mar_sales=[{name=mar_sales, table_ref=monthly_sales_long}], tax=[{name=jan_sales, table_ref=monthly_sales_long}], feb_sales=[{name=feb_sales, table_ref=monthly_sales_long}]}, derived_columns={jan_sales_SUM=[{name=sales_amount, table_ref=monthly_sales_long}], mar_sales_SUM=[{name=sales_amount, table_ref=monthly_sales_long}], feb_sales_SUM=[{name=sales_amount, table_ref=monthly_sales_long}]}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void pivotJoinTargetsWithFilterV5Test() {
		final String query =
			"SELECT empid, jan_sales, feb_sales, jan_sales * 0.07 AS tax\n" +
			"FROM monthly_sales_long PIVOT (SUM(sales_amount) FOR month_name IN ('jan_sales', 'feb_sales')) u\n" +
			"JOIN targets t ON u.empid = t.empid AND u.jan_sales >= t.target_amount WHERE jan_sales > 100;";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoFatalErrors(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=empid, table_ref=null}}, 2={column={name=jan_sales, table_ref=null}}, 3={column={name=feb_sales, table_ref=null}}, 4={alias=tax, calc={left={column={name=jan_sales, table_ref=null}}, right={literal=0.07}, operator=*}}}, from={join={1={pivot={value={function={function_name=SUM, parameters={column={name=sales_amount, table_ref=null}}}}, for={column={name=month_name, table_ref=null}}, in={1={pivot_literal='jan_sales'}, 2={pivot_literal='feb_sales'}}}, alias=u, table={alias=null, table=monthly_sales_long}}, 2={join=JOIN, on={and={1={condition={left={column={name=empid, table_ref=u}}, right={column={name=empid, table_ref=t}}, operator==}}, 2={condition={left={column={name=jan_sales, table_ref=u}}, right={column={name=target_amount, table_ref=t}}, operator=>=}}}}}, 3={table={alias=t, table=targets}}}}, where={condition={left={column={name=jan_sales, table_ref=null}}, right={literal=100}, operator=>}}}}",
			extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[jan_sales, empid, tax, feb_sales]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{targets={empid=[[@40,185:185='t',<381>,3:28]], month_name=[[@23,113:122='month_name',<381>,2:53]], target_amount=[[@48,212:212='t',<381>,3:55]], sales_amount=[[@20,95:106='sales_amount',<381>,2:35]]}, monthly_sales_long={jan_sales=[[@44,197:197='u',<381>,3:40]], empid=[[@36,175:175='u',<381>,3:18]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query1={jan_sales=[[@3,14:22='jan_sales',<381>,1:14]], empid=[[@1,7:11='empid',<381>,1:7]], tax=[[@13,56:58='tax',<381>,1:56]], feb_sales=[[@5,25:33='feb_sales',<381>,1:25]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query1={query_dictionary={jan_sales=[[@3,14:22='jan_sales',<381>,1:14]], empid=[[@1,7:11='empid',<381>,1:7]], tax=[[@13,56:58='tax',<381>,1:56]], feb_sales=[[@5,25:33='feb_sales',<381>,1:25]]}, table_dictionary={targets={month_name=[[@23,113:122='month_name',<381>,2:53]], empid=[[@40,185:185='t',<381>,3:28]], target_amount=[[@48,212:212='t',<381>,3:55]], sales_amount=[[@20,95:106='sales_amount',<381>,2:35]]}, monthly_sales_long={jan_sales=[[@44,197:197='u',<381>,3:40]], empid=[[@36,175:175='u',<381>,3:18]]}}, filters=[{name=empid, table_ref=u}, {name=empid, table_ref=t}, {name=jan_sales, table_ref=u}, {name=target_amount, table_ref=t}, {name=jan_sales, table_ref=null}], interface={jan_sales=[{name=jan_sales, table_ref=null}], empid=[{name=empid, table_ref=null}], tax=[{name=jan_sales, table_ref=null}], feb_sales=[{name=feb_sales, table_ref=null}]}, derived_columns={jan_sales_SUM=[{name=sales_amount, table_ref=u}], feb_sales_SUM=[{name=sales_amount, table_ref=u}]}, table_alias={u=monthly_sales_long, t=targets}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void pivotKeepingForColumnV6Test() {
		final String query =
			"SELECT empid, month_name, jan_sales, feb_sales, mar_sales\n" +
			"FROM monthly_sales_long\n" +
			"PIVOT (SUM(sales_amount) FOR month_name IN ('jan_sales', 'feb_sales', 'mar_sales'));";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoFatalErrors(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=empid, table_ref=null}}, 2={column={name=month_name, table_ref=null}}, 3={column={name=jan_sales, table_ref=null}}, 4={column={name=feb_sales, table_ref=null}}, 5={column={name=mar_sales, table_ref=null}}}, from={pivot={value={function={function_name=SUM, parameters={column={name=sales_amount, table_ref=null}}}}, for={column={name=month_name, table_ref=null}}, in={1={pivot_literal='jan_sales'}, 2={pivot_literal='feb_sales'}, 3={pivot_literal='mar_sales'}}}, table={alias=null, table=monthly_sales_long}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[jan_sales, empid, month_name, mar_sales, feb_sales]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{monthly_sales_long={jan_sales=[[@5,26:34='jan_sales',<381>,1:26]], month_name=[[@19,111:120='month_name',<381>,3:29]], empid=[[@1,7:11='empid',<381>,1:7]], mar_sales=[[@9,48:56='mar_sales',<381>,1:48]], sales_amount=[[@16,93:104='sales_amount',<381>,3:11]], feb_sales=[[@7,37:45='feb_sales',<381>,1:37]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query1={jan_sales=[[@5,26:34='jan_sales',<381>,1:26]], empid=[[@1,7:11='empid',<381>,1:7]], month_name=[[@3,14:23='month_name',<381>,1:14]], mar_sales=[[@9,48:56='mar_sales',<381>,1:48]], feb_sales=[[@7,37:45='feb_sales',<381>,1:37]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query1={query_dictionary={jan_sales=[[@5,26:34='jan_sales',<381>,1:26]], empid=[[@1,7:11='empid',<381>,1:7]], month_name=[[@3,14:23='month_name',<381>,1:14]], mar_sales=[[@9,48:56='mar_sales',<381>,1:48]], feb_sales=[[@7,37:45='feb_sales',<381>,1:37]]}, table_dictionary={monthly_sales_long={jan_sales=[[@5,26:34='jan_sales',<381>,1:26]], month_name=[[@19,111:120='month_name',<381>,3:29]], empid=[[@1,7:11='empid',<381>,1:7]], mar_sales=[[@9,48:56='mar_sales',<381>,1:48]], sales_amount=[[@16,93:104='sales_amount',<381>,3:11]], feb_sales=[[@7,37:45='feb_sales',<381>,1:37]]}}, interface={jan_sales=[{name=jan_sales, table_ref=monthly_sales_long}], empid=[{name=empid, table_ref=monthly_sales_long}], month_name=[{name=month_name, table_ref=monthly_sales_long}], mar_sales=[{name=mar_sales, table_ref=monthly_sales_long}], feb_sales=[{name=feb_sales, table_ref=monthly_sales_long}]}, derived_columns={jan_sales_SUM=[{name=sales_amount, table_ref=monthly_sales_long}], mar_sales_SUM=[{name=sales_amount, table_ref=monthly_sales_long}], feb_sales_SUM=[{name=sales_amount, table_ref=monthly_sales_long}]}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void pivotBasicMonthSalesV7Test() {
		final String query =
			"SELECT empid, jan_sales_SUM, feb_sales_SUM, mar_sales_SUM\n" +
			"FROM monthly_sales_long\n" +
			"PIVOT (SUM(sales_amount) FOR month_name IN ('jan_sales', 'feb_sales', 'mar_sales'));";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoFatalErrors(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=empid, table_ref=null}}, 2={column={name=jan_sales_SUM, table_ref=null}}, 3={column={name=feb_sales_SUM, table_ref=null}}, 4={column={name=mar_sales_SUM, table_ref=null}}}, from={pivot={value={function={function_name=SUM, parameters={column={name=sales_amount, table_ref=null}}}}, for={column={name=month_name, table_ref=null}}, in={1={pivot_literal='jan_sales'}, 2={pivot_literal='feb_sales'}, 3={pivot_literal='mar_sales'}}}, table={alias=null, table=monthly_sales_long}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[empid, jan_sales_SUM, mar_sales_SUM, feb_sales_SUM]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{monthly_sales_long={month_name=[[@17,111:120='month_name',<381>,3:29]], empid=[[@1,7:11='empid',<381>,1:7]], sales_amount=[[@14,93:104='sales_amount',<381>,3:11]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query1={empid=[[@1,7:11='empid',<381>,1:7]], jan_sales_SUM=[[@3,14:26='jan_sales_SUM',<381>,1:14]], mar_sales_SUM=[[@7,44:56='mar_sales_SUM',<381>,1:44]], feb_sales_SUM=[[@5,29:41='feb_sales_SUM',<381>,1:29]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query1={query_dictionary={empid=[[@1,7:11='empid',<381>,1:7]], jan_sales_SUM=[[@3,14:26='jan_sales_SUM',<381>,1:14]], mar_sales_SUM=[[@7,44:56='mar_sales_SUM',<381>,1:44]], feb_sales_SUM=[[@5,29:41='feb_sales_SUM',<381>,1:29]]}, table_dictionary={monthly_sales_long={month_name=[[@17,111:120='month_name',<381>,3:29]], empid=[[@1,7:11='empid',<381>,1:7]], sales_amount=[[@14,93:104='sales_amount',<381>,3:11]]}}, interface={empid=[{name=empid, table_ref=monthly_sales_long}], jan_sales_SUM=[{name=jan_sales_SUM, table_ref=null}], mar_sales_SUM=[{name=mar_sales_SUM, table_ref=null}], feb_sales_SUM=[{name=feb_sales_SUM, table_ref=null}]}, derived_columns={jan_sales_SUM=[{name=sales_amount, table_ref=monthly_sales_long}], mar_sales_SUM=[{name=sales_amount, table_ref=monthly_sales_long}], feb_sales_SUM=[{name=sales_amount, table_ref=monthly_sales_long}]}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void pivotBasicMonthSalesJoinV8Test() {
		final String query =
			"SELECT empid, jan_sales, feb_sales, mar_sales, t2.a1, t2.a2\n" +
			"FROM monthly_sales_long\n" +
			"PIVOT (SUM(sales_amount) FOR month_name IN ('jan_sales', 'feb_sales', 'mar_sales'))\n" +
			"JOIN metrics_table t2 ON empid = t2.metric_label;";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoFatalErrors(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=empid, table_ref=null}}, 2={column={name=jan_sales, table_ref=null}}, 3={column={name=feb_sales, table_ref=null}}, 4={column={name=mar_sales, table_ref=null}}, 5={column={name=a1, table_ref=t2}}, 6={column={name=a2, table_ref=t2}}}, from={join={1={pivot={value={function={function_name=SUM, parameters={column={name=sales_amount, table_ref=null}}}}, for={column={name=month_name, table_ref=null}}, in={1={pivot_literal='jan_sales'}, 2={pivot_literal='feb_sales'}, 3={pivot_literal='mar_sales'}}}, table={alias=null, table=monthly_sales_long}}, 2={join=JOIN, on={condition={left={column={name=empid, table_ref=null}}, right={column={name=metric_label, table_ref=t2}}, operator==}}}, 3={table={alias=t2, table=metrics_table}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[jan_sales, a1, empid, mar_sales, a2, feb_sales]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{metrics_table={a1=[[@9,47:48='t2',<381>,1:47]], a2=[[@13,54:55='t2',<381>,1:54]], month_name=[[@25,113:122='month_name',<381>,3:29]], sales_amount=[[@22,95:106='sales_amount',<381>,3:11]], metric_label=[[@41,201:202='t2',<381>,4:33]]}, monthly_sales_long={}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query1={jan_sales=[[@3,14:22='jan_sales',<381>,1:14]], a1=[[@11,50:51='a1',<381>,1:50]], empid=[[@1,7:11='empid',<381>,1:7]], mar_sales=[[@7,36:44='mar_sales',<381>,1:36]], a2=[[@15,57:58='a2',<381>,1:57]], feb_sales=[[@5,25:33='feb_sales',<381>,1:25]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query1={query_dictionary={jan_sales=[[@3,14:22='jan_sales',<381>,1:14]], a1=[[@11,50:51='a1',<381>,1:50]], empid=[[@1,7:11='empid',<381>,1:7]], mar_sales=[[@7,36:44='mar_sales',<381>,1:36]], a2=[[@15,57:58='a2',<381>,1:57]], feb_sales=[[@5,25:33='feb_sales',<381>,1:25]]}, table_dictionary={metrics_table={a1=[[@9,47:48='t2',<381>,1:47]], month_name=[[@25,113:122='month_name',<381>,3:29]], a2=[[@13,54:55='t2',<381>,1:54]], sales_amount=[[@22,95:106='sales_amount',<381>,3:11]], metric_label=[[@41,201:202='t2',<381>,4:33]]}, monthly_sales_long={}}, filters=[{name=empid, table_ref=null}, {name=metric_label, table_ref=t2}], interface={jan_sales=[{name=jan_sales, table_ref=null}], a1=[{name=a1, table_ref=t2}], empid=[{name=empid, table_ref=null}], mar_sales=[{name=mar_sales, table_ref=null}], a2=[{name=a2, table_ref=t2}], feb_sales=[{name=feb_sales, table_ref=null}]}, derived_columns={jan_sales_SUM=[{name=sales_amount, table_ref=monthly_sales_long}], mar_sales_SUM=[{name=sales_amount, table_ref=monthly_sales_long}], feb_sales_SUM=[{name=sales_amount, table_ref=monthly_sales_long}]}, table_alias={t2=metrics_table}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void pivotSameQuerySelectDerivedColumnFromTableTest() {
		final String query =
			"SELECT src, A_sum\n" +
			"FROM tab1\n" +
			"PIVOT (SUM(col1) sum FOR col2 IN ('A'));";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=src, table_ref=null}}, 2={column={name=A_sum, table_ref=null}}}, from={pivot={value={function={function_name=SUM, parameters={column={name=col1, table_ref=null}}}, alias=sum}, for={column={name=col2, table_ref=null}}, in={1={pivot_literal='A'}}}, table={alias=null, table=tab1}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[src, A_sum]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={src=[[@1,7:9='src',<381>,1:7]], col2=[[@14,53:56='col2',<381>,3:25]], col1=[[@10,39:42='col1',<381>,3:11]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query1={src=[[@1,7:9='src',<381>,1:7]], A_sum=[[@3,12:16='A_sum',<381>,1:12]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query1={query_dictionary={src=[[@1,7:9='src',<381>,1:7]], A_sum=[[@3,12:16='A_sum',<381>,1:12]]}, table_dictionary={tab1={src=[[@1,7:9='src',<381>,1:7]], col2=[[@14,53:56='col2',<381>,3:25]], col1=[[@10,39:42='col1',<381>,3:11]]}}, interface={src=[{name=src, table_ref=tab1}], A_sum=[{name=A_sum, table_ref=null}]}, derived_columns={A_sum=[{name=col1, table_ref=tab1}]}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void pivotSameQueryWhereDerivedColumnFromTableTest() {
		final String query =
			"SELECT src\n" +
			"FROM tab1\n" +
			"PIVOT (SUM(col1) sum FOR col2 IN ('A'))\n" +
			"WHERE A_sum > 0;";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=src, table_ref=null}}}, from={pivot={value={function={function_name=SUM, parameters={column={name=col1, table_ref=null}}}, alias=sum}, for={column={name=col2, table_ref=null}}, in={1={pivot_literal='A'}}}, table={alias=null, table=tab1}}, where={condition={left={column={name=A_sum, table_ref=null}}, right={literal=0}, operator=>}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[src]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={src=[[@1,7:9='src',<381>,1:7]], col2=[[@12,46:49='col2',<381>,3:25]], col1=[[@8,32:35='col1',<381>,3:11]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query1={src=[[@1,7:9='src',<381>,1:7]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query1={query_dictionary={src=[[@1,7:9='src',<381>,1:7]]}, table_dictionary={tab1={src=[[@1,7:9='src',<381>,1:7]], col2=[[@12,46:49='col2',<381>,3:25]], col1=[[@8,32:35='col1',<381>,3:11]]}}, filters=[{name=A_sum, table_ref=null}], interface={src=[{name=src, table_ref=tab1}]}, derived_columns={A_sum=[{name=col1, table_ref=tab1}]}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void pivotSameQueryGroupByDerivedColumnFromTableTest() {
		final String query =
			"SELECT src\n" +
			"FROM tab1\n" +
			"PIVOT (SUM(col1) sum FOR col2 IN ('A'))\n" +
			"GROUP BY src, A_sum;";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=src, table_ref=null}}}, from={pivot={value={function={function_name=SUM, parameters={column={name=col1, table_ref=null}}}, alias=sum}, for={column={name=col2, table_ref=null}}, in={1={pivot_literal='A'}}}, table={alias=null, table=tab1}}, groupby={1={column={name=src, table_ref=null}}, 2={column={name=A_sum, table_ref=null}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[src]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={src=[[@1,7:9='src',<381>,1:7], [@20,70:72='src',<381>,4:9]], col2=[[@12,46:49='col2',<381>,3:25]], col1=[[@8,32:35='col1',<381>,3:11]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query1={src=[[@1,7:9='src',<381>,1:7]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query1={query_dictionary={src=[[@1,7:9='src',<381>,1:7]]}, table_dictionary={tab1={src=[[@1,7:9='src',<381>,1:7], [@20,70:72='src',<381>,4:9]], col2=[[@12,46:49='col2',<381>,3:25]], col1=[[@8,32:35='col1',<381>,3:11]]}}, grouped_by=[{name=src, table_ref=tab1}, {name=A_sum, table_ref=null}], interface={src=[{name=src, table_ref=tab1}]}, derived_columns={A_sum=[{name=col1, table_ref=tab1}]}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void pivotSameQueryHavingDerivedColumnFromTableTest() {
		final String query =
			"SELECT src\n" +
			"FROM tab1\n" +
			"PIVOT (SUM(col1) sum FOR col2 IN ('A'))\n" +
			"GROUP BY src, A_sum\n" +
			"HAVING A_sum > 0;";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=src, table_ref=null}}}, having={condition={left={column={name=A_sum, table_ref=null}}, right={literal=0}, operator=>}}, from={pivot={value={function={function_name=SUM, parameters={column={name=col1, table_ref=null}}}, alias=sum}, for={column={name=col2, table_ref=null}}, in={1={pivot_literal='A'}}}, table={alias=null, table=tab1}}, groupby={1={column={name=src, table_ref=null}}, 2={column={name=A_sum, table_ref=null}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[src]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={src=[[@1,7:9='src',<381>,1:7], [@20,70:72='src',<381>,4:9]], col2=[[@12,46:49='col2',<381>,3:25]], col1=[[@8,32:35='col1',<381>,3:11]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query1={src=[[@1,7:9='src',<381>,1:7]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query1={query_dictionary={src=[[@1,7:9='src',<381>,1:7]]}, table_dictionary={tab1={src=[[@1,7:9='src',<381>,1:7], [@20,70:72='src',<381>,4:9]], col2=[[@12,46:49='col2',<381>,3:25]], col1=[[@8,32:35='col1',<381>,3:11]]}}, grouped_by=[{name=src, table_ref=tab1}, {name=A_sum, table_ref=null}], filters=[{name=A_sum, table_ref=null}], interface={src=[{name=src, table_ref=tab1}]}, derived_columns={A_sum=[{name=col1, table_ref=tab1}]}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void pivotSameQueryQualifyDerivedColumnFromTableTest() {
		final String query =
			"SELECT src\n" +
			"FROM tab1\n" +
			"PIVOT (SUM(col1) sum FOR col2 IN ('A'))\n" +
			"QUALIFY A_sum > 0;";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=src, table_ref=null}}}, from={pivot={value={function={function_name=SUM, parameters={column={name=col1, table_ref=null}}}, alias=sum}, for={column={name=col2, table_ref=null}}, in={1={pivot_literal='A'}}}, table={alias=null, table=tab1}}, qualify={condition={left={column={name=A_sum, table_ref=null}}, right={literal=0}, operator=>}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[src]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={src=[[@1,7:9='src',<381>,1:7]], col2=[[@12,46:49='col2',<381>,3:25]], col1=[[@8,32:35='col1',<381>,3:11]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query1={src=[[@1,7:9='src',<381>,1:7]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query1={query_dictionary={src=[[@1,7:9='src',<381>,1:7]]}, table_dictionary={tab1={src=[[@1,7:9='src',<381>,1:7]], col2=[[@12,46:49='col2',<381>,3:25]], col1=[[@8,32:35='col1',<381>,3:11]]}}, filters=[{name=A_sum, table_ref=null}], interface={src=[{name=src, table_ref=tab1}]}, derived_columns={A_sum=[{name=col1, table_ref=tab1}]}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void pivotSameQueryOrderByDerivedColumnFromTableTest() {
		final String query =
			"SELECT src\n" +
			"FROM tab1\n" +
			"PIVOT (SUM(col1) sum FOR col2 IN ('A'))\n" +
			"ORDER BY A_sum;";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=src, table_ref=null}}}, orderby={1={null_order=null, predicand={column={name=A_sum, table_ref=null}}, sort_order=ASC}}, from={pivot={value={function={function_name=SUM, parameters={column={name=col1, table_ref=null}}}, alias=sum}, for={column={name=col2, table_ref=null}}, in={1={pivot_literal='A'}}}, table={alias=null, table=tab1}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[src]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={src=[[@1,7:9='src',<381>,1:7]], col2=[[@12,46:49='col2',<381>,3:25]], col1=[[@8,32:35='col1',<381>,3:11]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query1={src=[[@1,7:9='src',<381>,1:7]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query1={query_dictionary={src=[[@1,7:9='src',<381>,1:7]]}, table_dictionary={tab1={src=[[@1,7:9='src',<381>,1:7]], col2=[[@12,46:49='col2',<381>,3:25]], col1=[[@8,32:35='col1',<381>,3:11]]}}, ordered_by=[{name=A_sum, table_ref=null}], interface={src=[{name=src, table_ref=tab1}]}, derived_columns={A_sum=[{name=col1, table_ref=tab1}]}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void pivotSameQueryJoinDerivedColumnFromTableTest() {
		final String query =
			"SELECT A_sum, t.target_amount\n" +
			"FROM (SELECT col1, col2 FROM tab1) q\n" +
			"PIVOT (SUM(col1) sum FOR col2 IN ('A'))\n" +
			"JOIN targets t ON A_sum >= t.target_amount;";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=A_sum, table_ref=null}}, 2={column={name=target_amount, table_ref=t}}}, from={join={1={pivot={value={function={function_name=SUM, parameters={column={name=col1, table_ref=null}}}, alias=sum}, for={column={name=col2, table_ref=null}}, in={1={pivot_literal='A'}}}, table={alias=q, query={select={1={column={name=col1, table_ref=null}}, 2={column={name=col2, table_ref=null}}}, from={table={alias=null, table=tab1}}}}}, 2={join=JOIN, on={condition={left={column={name=A_sum, table_ref=null}}, right={column={name=target_amount, table_ref=t}}, operator=>=}}}, 3={table={alias=t, table=targets}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[target_amount, A_sum]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={col2=[[@11,49:52='col2',<381>,2:19]], col1=[[@9,43:46='col1',<381>,2:13]]}, targets={target_amount=[[@3,14:14='t',<381>,1:14], [@36,134:134='t',<381>,4:27]], col2=[[@24,92:95='col2',<381>,3:25]], col1=[[@20,78:81='col1',<381>,3:11]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={col2=[[@11,49:52='col2',<381>,2:19]], col1=[[@9,43:46='col1',<381>,2:13]]}, query2={A_sum=[[@1,7:11='A_sum',<381>,1:7]], target_amount=[[@5,16:28='target_amount',<381>,1:16]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query2={query_dictionary={target_amount=[[@5,16:28='target_amount',<381>,1:16]], A_sum=[[@1,7:11='A_sum',<381>,1:7]]}, table_dictionary={targets={target_amount=[[@3,14:14='t',<381>,1:14], [@36,134:134='t',<381>,4:27]], col2=[[@24,92:95='col2',<381>,3:25]], col1=[[@20,78:81='col1',<381>,3:11]]}}, def_query0={query_dictionary={col2=[[@11,49:52='col2',<381>,2:19]], col1=[[@9,43:46='col1',<381>,2:13]]}, table_dictionary={tab1={col2=[[@11,49:52='col2',<381>,2:19]], col1=[[@9,43:46='col1',<381>,2:13]]}}, interface={col2=[{name=col2, table_ref=tab1}], col1=[{name=col1, table_ref=tab1}]}}, filters=[{name=A_sum, table_ref=null}, {name=target_amount, table_ref=t}], interface={target_amount=[{name=target_amount, table_ref=t}], A_sum=[{name=A_sum, table_ref=null}]}, derived_columns={A_sum=[{name=col1, table_ref=q}]}, table_alias={q=query0, t=targets}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void pivotSameQueryDerivedColumnsFromSubqueryAcrossClausesTest() {
		final String query =
			"SELECT q.src\n" +
			"FROM (SELECT src, col1, col2 FROM tab1) q\n" +
			"PIVOT (SUM(col1) sum FOR col2 IN ('A'))\n" +
			"JOIN targets t ON A_sum >= t.target_amount\n" +
			"WHERE A_sum > 0\n" +
			"GROUP BY q.src, A_sum\n" +
			"HAVING A_sum > 0\n" +
			"QUALIFY A_sum > 0\n" +
			"ORDER BY A_sum;";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=src, table_ref=q}}}, having={condition={left={column={name=A_sum, table_ref=null}}, right={literal=0}, operator=>}}, orderby={1={null_order=null, predicand={column={name=A_sum, table_ref=null}}, sort_order=ASC}}, from={join={1={pivot={value={function={function_name=SUM, parameters={column={name=col1, table_ref=null}}}, alias=sum}, for={column={name=col2, table_ref=null}}, in={1={pivot_literal='A'}}}, table={alias=q, query={select={1={column={name=src, table_ref=null}}, 2={column={name=col1, table_ref=null}}, 3={column={name=col2, table_ref=null}}}, from={table={alias=null, table=tab1}}}}}, 2={join=JOIN, on={condition={left={column={name=A_sum, table_ref=null}}, right={column={name=target_amount, table_ref=t}}, operator=>=}}}, 3={table={alias=t, table=targets}}}}, where={condition={left={column={name=A_sum, table_ref=null}}, right={literal=0}, operator=>}}, groupby={1={column={name=src, table_ref=q}}, 2={column={name=A_sum, table_ref=null}}}, qualify={condition={left={column={name=A_sum, table_ref=null}}, right={literal=0}, operator=>}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[src]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={src=[[@7,26:28='src',<381>,2:13]], col2=[[@11,37:40='col2',<381>,2:24]], col1=[[@9,31:34='col1',<381>,2:18]]}, targets={target_amount=[[@36,122:122='t',<381>,4:27]], col2=[[@24,80:83='col2',<381>,3:25]], col1=[[@20,66:69='col1',<381>,3:11]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
		 "{query0={col2=[[@11,37:40='col2',<381>,2:24]], src=[[@7,26:28='src',<381>,2:13], [@1,7:7='q',<381>,1:7], [@45,163:163='q',<381>,6:9]], col1=[[@9,31:34='col1',<381>,2:18]]}, query2={src=[[@3,9:11='src',<381>,1:9]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query2={query_dictionary={src=[[@3,9:11='src',<381>,1:9]]}, table_dictionary={targets={target_amount=[[@36,122:122='t',<381>,4:27]], col2=[[@24,80:83='col2',<381>,3:25]], col1=[[@20,66:69='col1',<381>,3:11]]}}, grouped_by=[{name=src, table_ref=q}, {name=A_sum, table_ref=null}], def_query0={query_dictionary={src=[[@7,26:28='src',<381>,2:13], [@1,7:7='q',<381>,1:7], [@45,163:163='q',<381>,6:9]], col2=[[@11,37:40='col2',<381>,2:24]], col1=[[@9,31:34='col1',<381>,2:18]]}, table_dictionary={tab1={src=[[@7,26:28='src',<381>,2:13]], col2=[[@11,37:40='col2',<381>,2:24]], col1=[[@9,31:34='col1',<381>,2:18]]}}, interface={src=[{name=src, table_ref=tab1}], col2=[{name=col2, table_ref=tab1}], col1=[{name=col1, table_ref=tab1}]}}, ordered_by=[{name=A_sum, table_ref=null}], filters=[{name=A_sum, table_ref=null}, {name=target_amount, table_ref=t}], interface={src=[{name=src, table_ref=q}]}, derived_columns={A_sum=[{name=col1, table_ref=q}]}, table_alias={q=query0, t=targets}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void pivotNestedTableDerivedColumnsResolveInOuterClausesV1Test() {
		final String query =
			"SELECT p.empid, p.jan_sum, t.target_amount\n" +
			"FROM (\n" +
			"  SELECT *\n" +
			"  FROM monthly_sales_long\n" +
			"  PIVOT (SUM(sales_amount) sum FOR month_name IN ('jan', 'feb'))\n" +
			") p\n" +
			"JOIN targets t ON p.jan_sum >= t.target_amount\n" +
			"WHERE p.feb_sum > 0\n" +
			"GROUP BY p.empid, p.jan_sum, p.feb_sum, t.target_amount\n" +
			"HAVING p.jan_sum > 10\n" +
			"QUALIFY p.feb_sum > 0\n" +
			"ORDER BY p.jan_sum;";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=empid, table_ref=p}}, 2={column={name=jan_sum, table_ref=p}}, 3={column={name=target_amount, table_ref=t}}}, having={condition={left={column={name=jan_sum, table_ref=p}}, right={literal=10}, operator=>}}, orderby={1={null_order=null, predicand={column={name=jan_sum, table_ref=p}}, sort_order=ASC}}, from={join={1={table={alias=p, query={select={1={column={name=*, table_ref=*}}}, from={pivot={value={function={function_name=SUM, parameters={column={name=sales_amount, table_ref=null}}}, alias=sum}, for={column={name=month_name, table_ref=null}}, in={1={pivot_literal='jan'}, 2={pivot_literal='feb'}}}, table={alias=null, table=monthly_sales_long}}}}}, 2={join=JOIN, on={condition={left={column={name=jan_sum, table_ref=p}}, right={column={name=target_amount, table_ref=t}}, operator=>=}}}, 3={table={alias=t, table=targets}}}}, where={condition={left={column={name=feb_sum, table_ref=p}}, right={literal=0}, operator=>}}, groupby={1={column={name=empid, table_ref=p}}, 2={column={name=jan_sum, table_ref=p}}, 3={column={name=feb_sum, table_ref=p}}, 4={column={name=target_amount, table_ref=t}}}, qualify={condition={left={column={name=feb_sum, table_ref=p}}, right={literal=0}, operator=>}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[empid, target_amount, jan_sum]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{targets={target_amount=[[@9,27:27='t',<381>,1:27], [@44,187:187='t',<381>,7:31], [@67,263:263='t',<381>,9:40]]}, monthly_sales_long={month_name=[[@26,122:131='month_name',<381>,5:35]], *=[[@15,59:59='*',<291>,3:9]], sales_amount=[[@22,100:111='sales_amount',<381>,5:13]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query1={feb_sum=[[@48,209:209='p',<381>,8:6], [@63,252:252='p',<381>,9:29], [@77,309:309='p',<381>,11:8]], empid=[[@1,7:7='p',<381>,1:7], [@55,232:232='p',<381>,9:9]], *=[[@15,59:59='*',<291>,3:9]], jan_sum=[[@5,16:16='p',<381>,1:16], [@40,174:174='p',<381>,7:18], [@59,241:241='p',<381>,9:18], [@71,286:286='p',<381>,10:7], [@84,332:332='p',<381>,12:9]]}, query2={empid=[[@3,9:13='empid',<381>,1:9]], target_amount=[[@11,29:41='target_amount',<381>,1:29]], jan_sum=[[@7,18:24='jan_sum',<381>,1:18]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query2={query_dictionary={empid=[[@3,9:13='empid',<381>,1:9]], target_amount=[[@11,29:41='target_amount',<381>,1:29]], jan_sum=[[@7,18:24='jan_sum',<381>,1:18]]}, table_dictionary={targets={target_amount=[[@9,27:27='t',<381>,1:27], [@44,187:187='t',<381>,7:31], [@67,263:263='t',<381>,9:40]]}}, grouped_by=[{name=empid, table_ref=p}, {name=jan_sum, table_ref=p}, {name=feb_sum, table_ref=p}, {name=target_amount, table_ref=t}], def_query1={query_dictionary={empid=[[@1,7:7='p',<381>,1:7], [@55,232:232='p',<381>,9:9]], jan_sum=[[@5,16:16='p',<381>,1:16], [@40,174:174='p',<381>,7:18], [@59,241:241='p',<381>,9:18], [@71,286:286='p',<381>,10:7], [@84,332:332='p',<381>,12:9]], feb_sum=[[@48,209:209='p',<381>,8:6], [@63,252:252='p',<381>,9:29], [@77,309:309='p',<381>,11:8]], *=[[@15,59:59='*',<291>,3:9]]}, table_dictionary={monthly_sales_long={month_name=[[@26,122:131='month_name',<381>,5:35]], *=[[@15,59:59='*',<291>,3:9]], sales_amount=[[@22,100:111='sales_amount',<381>,5:13]]}}, interface={*=[{name=*, table_ref=*}]}, derived_columns={jan_sum=[{name=sales_amount, table_ref=monthly_sales_long}], feb_sum=[{name=sales_amount, table_ref=monthly_sales_long}]}}, ordered_by=[{name=jan_sum, table_ref=p}], filters=[{name=jan_sum, table_ref=p}, {name=target_amount, table_ref=t}, {name=feb_sum, table_ref=p}], interface={empid=[{name=empid, table_ref=p}], target_amount=[{name=target_amount, table_ref=t}], jan_sum=[{name=jan_sum, table_ref=p}]}, table_alias={p=query1, t=targets}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void pivotNestedSubqueryDerivedColumnsResolveInOuterClausesV1Test() {
		final String query =
			"SELECT p.empid, p.jan_sum, t.target_amount\n" +
			"FROM (\n" +
			"  SELECT *\n" +
			"  FROM (SELECT empid, month_name, sales_amount FROM monthly_sales_long) src\n" +
			"  PIVOT (SUM(sales_amount) sum FOR month_name IN ('jan', 'feb'))\n" +
			") p\n" +
			"JOIN targets t ON p.jan_sum >= t.target_amount\n" +
			"WHERE p.feb_sum > 0\n" +
			"GROUP BY p.empid, p.jan_sum, p.feb_sum, t.target_amount\n" +
			"HAVING p.jan_sum > 10\n" +
			"QUALIFY p.feb_sum > 0\n" +
			"ORDER BY p.jan_sum;";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=empid, table_ref=p}}, 2={column={name=jan_sum, table_ref=p}}, 3={column={name=target_amount, table_ref=t}}}, having={condition={left={column={name=jan_sum, table_ref=p}}, right={literal=10}, operator=>}}, orderby={1={null_order=null, predicand={column={name=jan_sum, table_ref=p}}, sort_order=ASC}}, from={join={1={table={alias=p, query={select={1={column={name=*, table_ref=*}}}, from={pivot={value={function={function_name=SUM, parameters={column={name=sales_amount, table_ref=null}}}, alias=sum}, for={column={name=month_name, table_ref=null}}, in={1={pivot_literal='jan'}, 2={pivot_literal='feb'}}}, table={alias=src, query={select={1={column={name=empid, table_ref=null}}, 2={column={name=month_name, table_ref=null}}, 3={column={name=sales_amount, table_ref=null}}}, from={table={alias=null, table=monthly_sales_long}}}}}}}}, 2={join=JOIN, on={condition={left={column={name=jan_sum, table_ref=p}}, right={column={name=target_amount, table_ref=t}}, operator=>=}}}, 3={table={alias=t, table=targets}}}}, where={condition={left={column={name=feb_sum, table_ref=p}}, right={literal=0}, operator=>}}, groupby={1={column={name=empid, table_ref=p}}, 2={column={name=jan_sum, table_ref=p}}, 3={column={name=feb_sum, table_ref=p}}, 4={column={name=target_amount, table_ref=t}}}, qualify={condition={left={column={name=feb_sum, table_ref=p}}, right={literal=0}, operator=>}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[empid, target_amount, jan_sum]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{targets={target_amount=[[@9,27:27='t',<381>,1:27], [@54,237:237='t',<381>,7:31], [@77,313:313='t',<381>,9:40]]}, monthly_sales_long={empid=[[@19,76:80='empid',<381>,4:15]], month_name=[[@21,83:92='month_name',<381>,4:22]], sales_amount=[[@23,95:106='sales_amount',<381>,4:34]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={empid=[[@19,76:80='empid',<381>,4:15]], month_name=[[@21,83:92='month_name',<381>,4:22], [@36,172:181='month_name',<381>,5:35]], sales_amount=[[@23,95:106='sales_amount',<381>,4:34], [@32,150:161='sales_amount',<381>,5:13]], *=[[@15,59:59='*',<291>,3:9]]}, query2={feb_sum=[[@58,259:259='p',<381>,8:6], [@73,302:302='p',<381>,9:29], [@87,359:359='p',<381>,11:8]], empid=[[@1,7:7='p',<381>,1:7], [@65,282:282='p',<381>,9:9]], *=[[@15,59:59='*',<291>,3:9]], jan_sum=[[@5,16:16='p',<381>,1:16], [@50,224:224='p',<381>,7:18], [@69,291:291='p',<381>,9:18], [@81,336:336='p',<381>,10:7], [@94,382:382='p',<381>,12:9]]}, query3={empid=[[@3,9:13='empid',<381>,1:9]], target_amount=[[@11,29:41='target_amount',<381>,1:29]], jan_sum=[[@7,18:24='jan_sum',<381>,1:18]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query3={query_dictionary={empid=[[@3,9:13='empid',<381>,1:9]], target_amount=[[@11,29:41='target_amount',<381>,1:29]], jan_sum=[[@7,18:24='jan_sum',<381>,1:18]]}, table_dictionary={targets={target_amount=[[@9,27:27='t',<381>,1:27], [@54,237:237='t',<381>,7:31], [@77,313:313='t',<381>,9:40]]}}, grouped_by=[{name=empid, table_ref=p}, {name=jan_sum, table_ref=p}, {name=feb_sum, table_ref=p}, {name=target_amount, table_ref=t}], ordered_by=[{name=jan_sum, table_ref=p}], filters=[{name=jan_sum, table_ref=p}, {name=target_amount, table_ref=t}, {name=feb_sum, table_ref=p}], interface={empid=[{name=empid, table_ref=p}], target_amount=[{name=target_amount, table_ref=t}], jan_sum=[{name=jan_sum, table_ref=p}]}, table_alias={p=query2, t=targets}, def_query2={query_dictionary={empid=[[@1,7:7='p',<381>,1:7], [@65,282:282='p',<381>,9:9]], jan_sum=[[@5,16:16='p',<381>,1:16], [@50,224:224='p',<381>,7:18], [@69,291:291='p',<381>,9:18], [@81,336:336='p',<381>,10:7], [@94,382:382='p',<381>,12:9]], feb_sum=[[@58,259:259='p',<381>,8:6], [@73,302:302='p',<381>,9:29], [@87,359:359='p',<381>,11:8]], *=[[@15,59:59='*',<291>,3:9]]}, def_query0={query_dictionary={empid=[[@19,76:80='empid',<381>,4:15]], month_name=[[@21,83:92='month_name',<381>,4:22], [@36,172:181='month_name',<381>,5:35]], sales_amount=[[@23,95:106='sales_amount',<381>,4:34], [@32,150:161='sales_amount',<381>,5:13]], *=[[@15,59:59='*',<291>,3:9]]}, table_dictionary={monthly_sales_long={empid=[[@19,76:80='empid',<381>,4:15]], month_name=[[@21,83:92='month_name',<381>,4:22]], sales_amount=[[@23,95:106='sales_amount',<381>,4:34]]}}, interface={empid=[{name=empid, table_ref=monthly_sales_long}], month_name=[{name=month_name, table_ref=monthly_sales_long}], sales_amount=[{name=sales_amount, table_ref=monthly_sales_long}]}}, interface={*=[{name=*, table_ref=*}]}, derived_columns={jan_sum=[{name=sales_amount, table_ref=src}], feb_sum=[{name=sales_amount, table_ref=src}]}, table_alias={src=query0}}}}",
				extractor.getSymbolTable().toString());
	}

	/*
		TUPLE TESTS WITH PIVOT AND UNPIVOT
	*/
	@Test
	public void generatorDirectFromListTupleEndpointNakedSyntaxBuildsSameAstShapeTest() {
		final String query = "tab1 pivot (sum(col1) for col2 in ('A', 'B'))";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runTupleParsertest(query, parser);

		assertNoFatalErrors(extractor);
		Assert.assertEquals("AST is wrong", "{TUPLE={pivot={value={function={function_name=sum, parameters={column={name=col1, table_ref=null}}}}, for={column={name=col2, table_ref=null}}, in={1={pivot_literal='A'}, 2={pivot_literal='B'}}}, table={table=tab1}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{table_dictionary={tab1={}}, unresolved_column={col2={column={name=col2, table_ref=null}, locations=[[@8,26:29='col2',<381>,1:26]]}, col1={column={name=col1, table_ref=null}, locations=[[@5,16:19='col1',<381>,1:16]]}}, derived_columns=[{source_columns=[col2, col1], for=col2, pivot_in_columns=[A, B], source_ref=tab1, pivot_aggregate_dependency_columns={sum=[col1]}, table_ref=tab1, derived_columns=[A_sum, B_sum], operator=pivot, pivot_aggregate_columns=[sum]}]}", extractor.getSymbolTable().toString());
	}

}
