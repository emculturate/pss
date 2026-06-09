package sql.walker;

import org.junit.Assert;
import org.junit.Test;

import sql.SQLSelectParserParser;

public class SqlEventWalkerPivotUnpivotTests extends AbstractSqlParseEventWalkerTest {

	// UNPIVOT RELATIONAL OPERATOR TESTS

	@Test
	public void unpivotV1Test() {
		final String query = "SELECT id, metric_name, metric_value\n" + 
						"FROM my_table \n UNPIVOT (\n" +
						"  metric_value FOR metric_name IN (jan_sales, feb_sales, mar_sales));";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=id, table_ref=null}}, 2={column={name=metric_name, table_ref=null}}, 3={column={name=metric_value, table_ref=null}}}, from={unpivot={value=metric_value, for=metric_name, in={1={name=jan_sales, table_ref=null}, 2={name=feb_sales, table_ref=null}, 3={name=mar_sales, table_ref=null}}}, table={alias=null, table=my_table}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[metric_name, metric_value, id]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{my_table={jan_sales=[[@15,98:106='jan_sales',<381>,4:35]], mar_sales=[[@19,120:128='mar_sales',<381>,4:57]], metric_name=[[@3,11:21='metric_name',<381>,1:11]], metric_value=[[@5,24:35='metric_value',<381>,1:24]], id=[[@1,7:8='id',<381>,1:7]], feb_sales=[[@17,109:117='feb_sales',<381>,4:46]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={metric_name=[[@3,11:21='metric_name',<381>,1:11]], metric_value=[[@5,24:35='metric_value',<381>,1:24]], id=[[@1,7:8='id',<381>,1:7]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{query0={query_dictionary={metric_name=[[@3,11:21='metric_name',<381>,1:11]], metric_value=[[@5,24:35='metric_value',<381>,1:24]], id=[[@1,7:8='id',<381>,1:7]]}, table_dictionary={my_table={jan_sales=[[@15,98:106='jan_sales',<381>,4:35]], mar_sales=[[@19,120:128='mar_sales',<381>,4:57]], metric_name=[[@3,11:21='metric_name',<381>,1:11]], metric_value=[[@5,24:35='metric_value',<381>,1:24]], id=[[@1,7:8='id',<381>,1:7]], feb_sales=[[@17,109:117='feb_sales',<381>,4:46]]}}, interface={metric_name=[{name=metric_name, table_ref=my_table}], metric_value=[{name=jan_sales, table_ref=my_table}, {name=feb_sales, table_ref=my_table}, {name=mar_sales, table_ref=my_table}], id=[{name=id, table_ref=my_table}]}}}",
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
		Assert.assertEquals("Table Dictionary is wrong", "{monthly_sales={jan_sales=[[@21,129:137='jan_sales',<381>,3:41]], empid=[[@1,7:11='empid',<381>,1:7]], month_name=[[@3,14:23='month_name',<381>,1:14]], mar_sales=[[@29,169:177='mar_sales',<381>,3:81]], sales_amount=[[@5,26:37='sales_amount',<381>,1:26], [@9,47:58='sales_amount',<381>,1:47], [@35,196:207='sales_amount',<381>,4:6]], units=[[@7,40:44='units',<381>,1:40], [@11,62:66='units',<381>,1:62], [@37,209:213='units',<381>,4:19]], feb_sales=[[@25,149:157='feb_sales',<381>,3:61]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={empid=[[@1,7:11='empid',<381>,1:7]], month_name=[[@3,14:23='month_name',<381>,1:14]], sales_amount=[[@5,26:37='sales_amount',<381>,1:26]], units=[[@7,40:44='units',<381>,1:40]], unnamed_0=[[@11,62:66='units',<381>,1:62]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{query0={query_dictionary={empid=[[@1,7:11='empid',<381>,1:7]], month_name=[[@3,14:23='month_name',<381>,1:14]], sales_amount=[[@5,26:37='sales_amount',<381>,1:26]], units=[[@7,40:44='units',<381>,1:40]], unnamed_0=[[@11,62:66='units',<381>,1:62]]}, table_dictionary={monthly_sales={jan_sales=[[@21,129:137='jan_sales',<381>,3:41]], empid=[[@1,7:11='empid',<381>,1:7]], month_name=[[@3,14:23='month_name',<381>,1:14]], mar_sales=[[@29,169:177='mar_sales',<381>,3:81]], sales_amount=[[@5,26:37='sales_amount',<381>,1:26], [@9,47:58='sales_amount',<381>,1:47], [@35,196:207='sales_amount',<381>,4:6]], units=[[@7,40:44='units',<381>,1:40], [@11,62:66='units',<381>,1:62], [@37,209:213='units',<381>,4:19]], feb_sales=[[@25,149:157='feb_sales',<381>,3:61]]}}, filters=[{name=jan_sales, table_ref=monthly_sales}, {name=feb_sales, table_ref=monthly_sales}, {name=mar_sales, table_ref=monthly_sales}, {name=units, table_ref=monthly_sales}], interface={empid=[{name=empid, table_ref=monthly_sales}], month_name=[{name=month_name, table_ref=monthly_sales}], sales_amount=[{name=jan_sales, table_ref=monthly_sales}, {name=feb_sales, table_ref=monthly_sales}, {name=mar_sales, table_ref=monthly_sales}], units=[{name=units, table_ref=monthly_sales}], unnamed_0=[{name=jan_sales, table_ref=monthly_sales}, {name=feb_sales, table_ref=monthly_sales}, {name=mar_sales, table_ref=monthly_sales}, {name=units, table_ref=monthly_sales}]}}}",
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

		String symbolTable = extractor.getSymbolTable().toString();
		Assert.assertTrue("Expected grouped_by clause in Symbol Table", symbolTable.contains("grouped_by="));
		Assert.assertTrue("Expected ordered_by clause in Symbol Table", symbolTable.contains("ordered_by="));
		Assert.assertTrue("Expected sales_amount interface derivation to Jan/Feb/Mar in Symbol Table",
				symbolTable.contains("sales_amount=[{name=jan_sales, table_ref=monthly_sales}, {name=feb_sales, table_ref=monthly_sales}, {name=mar_sales, table_ref=monthly_sales}]"));
		Assert.assertTrue("Expected grouped_by sales_amount to be expanded to Jan/Feb/Mar references",
				symbolTable.contains("grouped_by=[{name=month_name, table_ref=monthly_sales}, {name=jan_sales, table_ref=monthly_sales}, {name=feb_sales, table_ref=monthly_sales}, {name=mar_sales, table_ref=monthly_sales}, {name=units, table_ref=monthly_sales}]"));
		Assert.assertTrue("Expected ordered_by sales_amount to be expanded to Jan/Feb/Mar references",
				symbolTable.contains("ordered_by=[{name=jan_sales, table_ref=monthly_sales}, {name=feb_sales, table_ref=monthly_sales}, {name=mar_sales, table_ref=monthly_sales}]"));
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

		String symbolTable = extractor.getSymbolTable().toString();
		Assert.assertTrue("Expected filters clause in Symbol Table", symbolTable.contains("filters="));
		Assert.assertTrue("Expected grouped_by clause in Symbol Table", symbolTable.contains("grouped_by="));
		Assert.assertTrue("Expected ordered_by clause in Symbol Table", symbolTable.contains("ordered_by="));
		Assert.assertTrue("Expected HAVING sales_amount filter to be expanded to Jan/Feb/Mar references",
				symbolTable.contains("filters=[{name=jan_sales, table_ref=monthly_sales}, {name=feb_sales, table_ref=monthly_sales}, {name=mar_sales, table_ref=monthly_sales}]"));
		Assert.assertTrue("Expected ordered_by sales_amount to be expanded to Jan/Feb/Mar references",
				symbolTable.contains("ordered_by=[{name=jan_sales, table_ref=monthly_sales}, {name=feb_sales, table_ref=monthly_sales}, {name=mar_sales, table_ref=monthly_sales}]"));
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

		String symbolTable = extractor.getSymbolTable().toString();
		Assert.assertTrue("Expected filters clause in Symbol Table", symbolTable.contains("filters="));
		Assert.assertTrue("Expected JOIN ON unqualified sales_amount to expand to Jan/Feb/Mar references",
				symbolTable.contains("{name=jan_sales, table_ref=monthly_sales}")
					&& symbolTable.contains("{name=feb_sales, table_ref=monthly_sales}")
					&& symbolTable.contains("{name=mar_sales, table_ref=monthly_sales}"));
		Assert.assertTrue("Expected JOIN ON qualified target reference to remain present",
				symbolTable.contains("{name=target_amount, table_ref=t}"));
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

		String symbolTable = extractor.getSymbolTable().toString();
		Assert.assertTrue("Expected filters clause in Symbol Table", symbolTable.contains("filters="));
		Assert.assertTrue("Expected QUALIFY sales_amount to expand to Jan/Feb/Mar references",
				symbolTable.contains("filters=[{name=jan_sales, table_ref=monthly_sales}, {name=feb_sales, table_ref=monthly_sales}, {name=mar_sales, table_ref=monthly_sales}]"));
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

		String symbolTable = extractor.getSymbolTable().toString();
		Assert.assertTrue("Expected ordered_by clause in Symbol Table", symbolTable.contains("ordered_by="));
		Assert.assertTrue("Expected ORDER BY expression to expand sales_amount to Jan/Feb/Mar references",
				symbolTable.contains("{name=jan_sales, table_ref=monthly_sales}")
					&& symbolTable.contains("{name=feb_sales, table_ref=monthly_sales}")
					&& symbolTable.contains("{name=mar_sales, table_ref=monthly_sales}"));
		Assert.assertTrue("Expected ORDER BY expression to preserve units dependency",
				symbolTable.contains("{name=units, table_ref=monthly_sales}"));
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
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={empid=[[@9,52:56='empid',<381>,2:13]], feb_adjusted=[[@25,113:124='feb_adjusted',<381>,2:74]], jan_adjusted=[[@17,79:90='jan_adjusted',<381>,2:40]]}, query1={empid=[[@1,7:11='empid',<381>,1:7]], month_name=[[@3,14:23='month_name',<381>,1:14]], sales_amount=[[@5,26:37='sales_amount',<381>,1:26]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{query1={query_dictionary={empid=[[@1,7:11='empid',<381>,1:7]], month_name=[[@3,14:23='month_name',<381>,1:14]], sales_amount=[[@5,26:37='sales_amount',<381>,1:26]]}, table_dictionary={}, query0={query_dictionary={empid=[[@9,52:56='empid',<381>,2:13]], feb_adjusted=[[@25,113:124='feb_adjusted',<381>,2:74]], jan_adjusted=[[@17,79:90='jan_adjusted',<381>,2:40]]}, table_dictionary={monthly_sales={jan_sales=[[@11,59:67='jan_sales',<381>,2:20]], empid=[[@9,52:56='empid',<381>,2:13]], feb_sales=[[@19,93:101='feb_sales',<381>,2:54]]}}, interface={empid=[{name=empid, table_ref=monthly_sales}], feb_adjusted=[{name=feb_sales, table_ref=monthly_sales}], jan_adjusted=[{name=jan_sales, table_ref=monthly_sales}]}}, interface={empid=[{name=empid, table_ref=null}], month_name=[{name=month_name, table_ref=null}], sales_amount=[{name=jan_adjusted, table_ref=query0}, {name=feb_adjusted, table_ref=query0}]}}}",
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
		Assert.assertEquals("Table Dictionary is wrong", "{monthly_sales={jan_sales=[[@23,127:135='jan_sales',<381>,3:41]], empid=[[@1,7:11='empid',<381>,1:7]], month_name=[[@3,14:23='month_name',<381>,1:14]], mar_sales=[[@27,149:157='mar_sales',<381>,3:63]], sales_amount=[[@5,26:37='sales_amount',<381>,1:26], [@7,40:51='sales_amount',<381>,1:40], [@31,167:178='sales_amount',<381>,3:81]], feb_sales=[[@25,138:146='feb_sales',<381>,3:52]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={empid=[[@1,7:11='empid',<381>,1:7]], month_name=[[@3,14:23='month_name',<381>,1:14]], sales_amount=[[@5,26:37='sales_amount',<381>,1:26]], tax=[[@13,63:65='tax',<381>,1:63]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{query0={query_dictionary={empid=[[@1,7:11='empid',<381>,1:7]], month_name=[[@3,14:23='month_name',<381>,1:14]], sales_amount=[[@5,26:37='sales_amount',<381>,1:26]], tax=[[@13,63:65='tax',<381>,1:63]]}, table_dictionary={monthly_sales={jan_sales=[[@23,127:135='jan_sales',<381>,3:41]], empid=[[@1,7:11='empid',<381>,1:7]], month_name=[[@3,14:23='month_name',<381>,1:14]], mar_sales=[[@27,149:157='mar_sales',<381>,3:63]], sales_amount=[[@5,26:37='sales_amount',<381>,1:26], [@7,40:51='sales_amount',<381>,1:40], [@31,167:178='sales_amount',<381>,3:81]], feb_sales=[[@25,138:146='feb_sales',<381>,3:52]]}}, filters=[{name=jan_sales, table_ref=monthly_sales}, {name=feb_sales, table_ref=monthly_sales}, {name=mar_sales, table_ref=monthly_sales}], interface={empid=[{name=empid, table_ref=monthly_sales}], month_name=[{name=month_name, table_ref=monthly_sales}], sales_amount=[{name=jan_sales, table_ref=monthly_sales}, {name=feb_sales, table_ref=monthly_sales}, {name=mar_sales, table_ref=monthly_sales}], tax=[{name=jan_sales, table_ref=monthly_sales}, {name=feb_sales, table_ref=monthly_sales}, {name=mar_sales, table_ref=monthly_sales}]}}}",
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
				"[Unresolved unqualified column reference(s): [jan_sales [(l:2 c:60)], empid [(l:1 c:7)], feb_sales [(l:2 c:71)]]]",
				extractor.getSnippet().getErrorStringList(errorhandling.ParseDiagnostic.Severity.ERROR).toString());
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=empid, table_ref=null}}, 2={column={name=month_name, table_ref=null}}, 3={column={name=sales_amount, table_ref=null}}, 4={alias=tax, calc={left={column={name=sales_amount, table_ref=null}}, right={literal=0.07}, operator=*}}}, from={join={1={unpivot={value=sales_amount, for=month_name, in={1={name=jan_sales, table_ref=null}, 2={name=feb_sales, table_ref=null}}, alias=u}, table={alias=null, table=monthly_sales}}, 2={join=JOIN, on={and={1={condition={left={column={name=month_name, table_ref=u}}, right={column={name=month_name, table_ref=t}}, operator==}}, 2={condition={left={column={name=sales_amount, table_ref=u}}, right={column={name=target_amount, table_ref=t}}, operator=>=}}}}}, 3={table={alias=t, table=targets}}}}, where={condition={left={column={name=sales_amount, table_ref=null}}, right={literal=100}, operator=>}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[empid, month_name, sales_amount, tax]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{monthly_sales={}, u={month_name=[[@33,170:170='u',<381>,3:18]], sales_amount=[[@41,202:202='u',<381>,3:50]]}, targets={month_name=[[@37,185:185='t',<381>,3:33]], target_amount=[[@45,220:220='t',<381>,3:68]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={empid=[[@1,7:11='empid',<381>,1:7]], month_name=[[@3,14:23='month_name',<381>,1:14]], sales_amount=[[@5,26:37='sales_amount',<381>,1:26]], tax=[[@13,63:65='tax',<381>,1:63]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{query0={query_dictionary={empid=[[@1,7:11='empid',<381>,1:7]], month_name=[[@3,14:23='month_name',<381>,1:14]], sales_amount=[[@5,26:37='sales_amount',<381>,1:26]], tax=[[@13,63:65='tax',<381>,1:63]]}, table_dictionary={monthly_sales={}, u={month_name=[[@33,170:170='u',<381>,3:18]], sales_amount=[[@41,202:202='u',<381>,3:50]]}, targets={month_name=[[@37,185:185='t',<381>,3:33]], target_amount=[[@45,220:220='t',<381>,3:68]]}}, filters=[{name=month_name, table_ref=u}, {name=month_name, table_ref=t}, {name=jan_sales, table_ref=monthly_sales}, {name=feb_sales, table_ref=monthly_sales}, {name=target_amount, table_ref=t}], interface={empid=[{name=empid, table_ref=null}], month_name=[{name=month_name, table_ref=null}], sales_amount=[{name=jan_sales, table_ref=monthly_sales}, {name=feb_sales, table_ref=monthly_sales}], tax=[{name=jan_sales, table_ref=monthly_sales}, {name=feb_sales, table_ref=monthly_sales}]}, table_alias={t=targets}}}",
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
		Assert.assertEquals("Table Dictionary is wrong", "{monthly_sales={jan_sales=[[@3,14:22='jan_sales',<381>,1:14], [@19,121:129='jan_sales',<381>,3:41]], empid=[[@1,7:11='empid',<381>,1:7]], month_name=[[@7,36:45='month_name',<381>,1:36]], mar_sales=[[@23,143:151='mar_sales',<381>,3:63]], sales_amount=[[@9,48:59='sales_amount',<381>,1:48]], feb_sales=[[@5,25:33='feb_sales',<381>,1:25], [@21,132:140='feb_sales',<381>,3:52]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={jan_sales=[[@3,14:22='jan_sales',<381>,1:14]], empid=[[@1,7:11='empid',<381>,1:7]], month_name=[[@7,36:45='month_name',<381>,1:36]], sales_amount=[[@9,48:59='sales_amount',<381>,1:48]], feb_sales=[[@5,25:33='feb_sales',<381>,1:25]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{query0={query_dictionary={jan_sales=[[@3,14:22='jan_sales',<381>,1:14]], empid=[[@1,7:11='empid',<381>,1:7]], month_name=[[@7,36:45='month_name',<381>,1:36]], sales_amount=[[@9,48:59='sales_amount',<381>,1:48]], feb_sales=[[@5,25:33='feb_sales',<381>,1:25]]}, table_dictionary={monthly_sales={jan_sales=[[@3,14:22='jan_sales',<381>,1:14], [@19,121:129='jan_sales',<381>,3:41]], empid=[[@1,7:11='empid',<381>,1:7]], month_name=[[@7,36:45='month_name',<381>,1:36]], mar_sales=[[@23,143:151='mar_sales',<381>,3:63]], sales_amount=[[@9,48:59='sales_amount',<381>,1:48]], feb_sales=[[@5,25:33='feb_sales',<381>,1:25], [@21,132:140='feb_sales',<381>,3:52]]}}, interface={jan_sales=[{name=jan_sales, table_ref=monthly_sales}], empid=[{name=empid, table_ref=monthly_sales}], month_name=[{name=month_name, table_ref=monthly_sales}], sales_amount=[{name=jan_sales, table_ref=monthly_sales}, {name=feb_sales, table_ref=monthly_sales}, {name=mar_sales, table_ref=monthly_sales}], feb_sales=[{name=feb_sales, table_ref=monthly_sales}]}}}",
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
		Assert.assertEquals("Table Dictionary is wrong", "{monthly_sales={jan_sales=[[@15,99:107='jan_sales',<381>,3:41]], empid=[[@1,7:11='empid',<381>,1:7]], month_name=[[@3,14:23='month_name',<381>,1:14]], mar_sales=[[@19,121:129='mar_sales',<381>,3:63]], sales_amount=[[@5,26:37='sales_amount',<381>,1:26]], feb_sales=[[@17,110:118='feb_sales',<381>,3:52]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={empid=[[@1,7:11='empid',<381>,1:7]], month_name=[[@3,14:23='month_name',<381>,1:14]], sales_amount=[[@5,26:37='sales_amount',<381>,1:26]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{query0={query_dictionary={empid=[[@1,7:11='empid',<381>,1:7]], month_name=[[@3,14:23='month_name',<381>,1:14]], sales_amount=[[@5,26:37='sales_amount',<381>,1:26]]}, table_dictionary={monthly_sales={jan_sales=[[@15,99:107='jan_sales',<381>,3:41]], empid=[[@1,7:11='empid',<381>,1:7]], month_name=[[@3,14:23='month_name',<381>,1:14]], mar_sales=[[@19,121:129='mar_sales',<381>,3:63]], sales_amount=[[@5,26:37='sales_amount',<381>,1:26]], feb_sales=[[@17,110:118='feb_sales',<381>,3:52]]}}, interface={empid=[{name=empid, table_ref=monthly_sales}], month_name=[{name=month_name, table_ref=monthly_sales}], sales_amount=[{name=jan_sales, table_ref=monthly_sales}, {name=feb_sales, table_ref=monthly_sales}, {name=mar_sales, table_ref=monthly_sales}]}}}",
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
				"[Unresolved unqualified column reference(s): [jan_sales [(l:3 c:41)], empid [(l:1 c:7)], mar_sales [(l:3 c:63)], feb_sales [(l:3 c:52)]]]",
				extractor.getSnippet().getErrorStringList(errorhandling.ParseDiagnostic.Severity.ERROR).toString());
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=empid, table_ref=null}}, 2={column={name=month_name, table_ref=null}}, 3={column={name=sales_amount, table_ref=null}}, 4={column={name=a1, table_ref=t2}}, 5={column={name=a2, table_ref=t2}}}, from={join={1={unpivot={value=sales_amount, for=month_name, in={1={name=jan_sales, table_ref=null}, 2={name=feb_sales, table_ref=null}, 3={name=mar_sales, table_ref=null}}}, table={alias=null, table=monthly_sales}}, 2={join=JOIN, on={condition={left={column={name=month_name, table_ref=null}}, right={column={name=metric_label, table_ref=t2}}, operator==}}}, 3={table={alias=t2, table=metrics_table}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[a1, empid, month_name, a2, sales_amount]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{monthly_sales={}, metrics_table={a1=[[@7,40:41='t2',<381>,1:40]], a2=[[@11,47:48='t2',<381>,1:47]], metric_label=[[@36,185:186='t2',<381>,4:38]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={a1=[[@9,43:44='a1',<381>,1:43]], empid=[[@1,7:11='empid',<381>,1:7]], month_name=[[@3,14:23='month_name',<381>,1:14]], a2=[[@13,50:51='a2',<381>,1:50]], sales_amount=[[@5,26:37='sales_amount',<381>,1:26]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{query0={query_dictionary={a1=[[@9,43:44='a1',<381>,1:43]], empid=[[@1,7:11='empid',<381>,1:7]], month_name=[[@3,14:23='month_name',<381>,1:14]], a2=[[@13,50:51='a2',<381>,1:50]], sales_amount=[[@5,26:37='sales_amount',<381>,1:26]]}, table_dictionary={monthly_sales={}, metrics_table={a1=[[@7,40:41='t2',<381>,1:40]], a2=[[@11,47:48='t2',<381>,1:47]], metric_label=[[@36,185:186='t2',<381>,4:38]]}}, filters=[{name=month_name, table_ref=null}, {name=metric_label, table_ref=t2}], interface={a1=[{name=a1, table_ref=t2}], empid=[{name=empid, table_ref=null}], month_name=[{name=month_name, table_ref=null}], a2=[{name=a2, table_ref=t2}], sales_amount=[{name=jan_sales, table_ref=monthly_sales}, {name=feb_sales, table_ref=monthly_sales}, {name=mar_sales, table_ref=monthly_sales}]}, table_alias={t2=metrics_table}}}",
				extractor.getSymbolTable().toString());
	}

	// PIVOT RELATIONAL OPERATOR TESTS

	@Test
	public void pivotV1Test() {
		final String query = "select * from tab1 pivot (sum(col1) for col2 in (A, B))";

		final SQLSelectParserParser parser = parse(query);
		ParserRunResult runResult = runSQLParsertestAllowErrors(query, parser);
		Assert.assertNull("Unexpected parser execution failure",
				runResult.getFailure());
		Assert.assertNotNull("Extractor should be available after parse attempt",
				runResult.getExtractor());
		SqlParseEventWalker extractor = runResult.getExtractor();

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=*, table_ref=*}}}, from={pivot={value={function={function_name=sum, parameters=col1}}, for=col2, in={1={name=A, table_ref=null}, 2={name=B, table_ref=null}}}, table={alias=null, table=tab1}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={A=[[@14,49:49='A',<381>,1:49]], B=[[@16,52:52='B',<381>,1:52]], *=[[@1,7:7='*',<291>,1:7]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@1,7:7='*',<291>,1:7]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{query0={query_dictionary={*=[[@1,7:7='*',<291>,1:7]]}, table_dictionary={tab1={A=[[@14,49:49='A',<381>,1:49]], B=[[@16,52:52='B',<381>,1:52]], *=[[@1,7:7='*',<291>,1:7]]}}, interface={*=[{name=*, table_ref=*}]}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void pivotBasicMetricColumnsV1Test() {
		final String query =
			"SELECT id, jan_sales, feb_sales, mar_sales\n" +
			"FROM my_table\n" +
			"PIVOT (SUM(metric_value) FOR metric_name IN (jan_sales, feb_sales, mar_sales));";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoFatalErrors(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=id, table_ref=null}}, 2={column={name=jan_sales, table_ref=null}}, 3={column={name=feb_sales, table_ref=null}}, 4={column={name=mar_sales, table_ref=null}}}, from={pivot={value={function={function_name=SUM, parameters=metric_value}}, for=metric_name, in={1={name=jan_sales, table_ref=null}, 2={name=feb_sales, table_ref=null}, 3={name=mar_sales, table_ref=null}}}, table={alias=null, table=my_table}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{query0={query_dictionary={jan_sales=[[@3,11:19='jan_sales',<381>,1:11]], mar_sales=[[@7,33:41='mar_sales',<381>,1:33]], id=[[@1,7:8='id',<381>,1:7]], feb_sales=[[@5,22:30='feb_sales',<381>,1:22]]}, table_dictionary={my_table={jan_sales=[[@3,11:19='jan_sales',<381>,1:11], [@20,102:110='jan_sales',<381>,3:45]], mar_sales=[[@7,33:41='mar_sales',<381>,1:33], [@24,124:132='mar_sales',<381>,3:67]], id=[[@1,7:8='id',<381>,1:7]], feb_sales=[[@5,22:30='feb_sales',<381>,1:22], [@22,113:121='feb_sales',<381>,3:56]]}}, interface={jan_sales=[{name=jan_sales, table_ref=my_table}], mar_sales=[{name=mar_sales, table_ref=my_table}], id=[{name=id, table_ref=my_table}], feb_sales=[{name=feb_sales, table_ref=my_table}]}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void pivotTableWithInAliasesJanFebMarV2Test() {
		final String query =
			"SELECT empid, units, jan_sales, feb_sales, mar_sales\n" +
			"FROM monthly_sales_long\n" +
			"PIVOT (SUM(sales_amount) FOR month_name IN (jan_sales AS 'JAN', feb_sales AS 'FEB', mar_sales AS 'MAR'))\n" +
			"WHERE units > 1.00;";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoFatalErrors(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=empid, table_ref=null}}, 2={column={name=units, table_ref=null}}, 3={column={name=jan_sales, table_ref=null}}, 4={column={name=feb_sales, table_ref=null}}, 5={column={name=mar_sales, table_ref=null}}}, from={pivot={value={function={function_name=SUM, parameters=sales_amount}}, for=month_name, in={1={name=jan_sales, label='JAN', table_ref=null}, 2={name=feb_sales, label='FEB', table_ref=null}, 3={name=mar_sales, label='MAR', table_ref=null}}}, table={alias=null, table=monthly_sales_long}}, where={condition={left={column={name=units, table_ref=null}}, right={literal=1.00}, operator=>}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{query0={query_dictionary={jan_sales=[[@5,21:29='jan_sales',<381>,1:21]], empid=[[@1,7:11='empid',<381>,1:7]], mar_sales=[[@9,43:51='mar_sales',<381>,1:43]], units=[[@3,14:18='units',<381>,1:14]], feb_sales=[[@7,32:40='feb_sales',<381>,1:32]]}, table_dictionary={monthly_sales_long={jan_sales=[[@5,21:29='jan_sales',<381>,1:21], [@22,121:129='jan_sales',<381>,3:44]], empid=[[@1,7:11='empid',<381>,1:7], [@35,184:188='empid',<381>,4:9]], mar_sales=[[@9,43:51='mar_sales',<381>,1:43], [@30,161:169='mar_sales',<381>,3:84]], units=[[@3,14:18='units',<381>,1:14], [@36,188:192='units',<381>,4:6]], feb_sales=[[@7,32:40='feb_sales',<381>,1:32], [@26,141:149='feb_sales',<381>,3:64], [@39,202:210='feb_sales',<381>,4:27]]}}, grouped_by=[{name=empid, table_ref=monthly_sales_long}, {name=jan_sales, table_ref=monthly_sales_long}, {name=feb_sales, table_ref=monthly_sales_long}, {name=mar_sales, table_ref=monthly_sales_long}], ordered_by=[{name=jan_sales, table_ref=monthly_sales_long}], interface={jan_sales=[{name=jan_sales, table_ref=monthly_sales_long}], empid=[{name=empid, table_ref=monthly_sales_long}], mar_sales=[{name=mar_sales, table_ref=monthly_sales_long}], units=[{name=units, table_ref=monthly_sales_long}], feb_sales=[{name=feb_sales, table_ref=monthly_sales_long}]}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void pivotTableWithGroupByAndOrderByV2GroupOrderTest() {
		final String query =
			"SELECT empid, jan_sales, feb_sales, mar_sales\n" +
			"FROM monthly_sales_long\n" +
			"PIVOT (SUM(sales_amount) FOR month_name IN (jan_sales AS 'JAN', feb_sales AS 'FEB', mar_sales AS 'MAR'))\n" +
			"GROUP BY empid, jan_sales, feb_sales, mar_sales\n" +
			"ORDER BY jan_sales;";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoFatalErrors(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=empid, table_ref=null}}, 2={column={name=jan_sales, table_ref=null}}, 3={column={name=feb_sales, table_ref=null}}, 4={column={name=mar_sales, table_ref=null}}}, orderby={1={null_order=null, predicand={column={name=jan_sales, table_ref=null}}, sort_order=ASC}}, from={pivot={value={function={function_name=SUM, parameters=sales_amount}}, for=month_name, in={1={name=jan_sales, label='JAN', table_ref=null}, 2={name=feb_sales, label='FEB', table_ref=null}, 3={name=mar_sales, label='MAR', table_ref=null}}}, table={alias=null, table=monthly_sales_long}}, groupby={1={column={name=empid, table_ref=null}}, 2={column={name=jan_sales, table_ref=null}}, 3={column={name=feb_sales, table_ref=null}}, 4={column={name=mar_sales, table_ref=null}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{query0={query_dictionary={jan_sales=[[@3,14:22='jan_sales',<381>,1:14]], empid=[[@1,7:11='empid',<381>,1:7]], mar_sales=[[@7,36:44='mar_sales',<381>,1:36]], feb_sales=[[@5,25:33='feb_sales',<381>,1:25]]}, table_dictionary={monthly_sales_long={jan_sales=[[@3,14:22='jan_sales',<381>,1:14], [@20,114:122='jan_sales',<381>,3:44], [@35,184:192='jan_sales',<381>,4:9]], empid=[[@1,7:11='empid',<381>,1:7]], mar_sales=[[@7,36:44='mar_sales',<381>,1:36], [@28,154:162='mar_sales',<381>,3:84], [@41,213:221='mar_sales',<381>,4:38]], feb_sales=[[@5,25:33='feb_sales',<381>,1:25], [@24,134:142='feb_sales',<381>,3:64], [@37,196:204='feb_sales',<381>,4:21]]}}, grouped_by=[{name=empid, table_ref=monthly_sales_long}, {name=jan_sales, table_ref=monthly_sales_long}, {name=feb_sales, table_ref=monthly_sales_long}, {name=mar_sales, table_ref=monthly_sales_long}], ordered_by=[{name=jan_sales, table_ref=monthly_sales_long}], interface={jan_sales=[{name=jan_sales, table_ref=monthly_sales_long}], empid=[{name=empid, table_ref=monthly_sales_long}], mar_sales=[{name=mar_sales, table_ref=monthly_sales_long}], feb_sales=[{name=feb_sales, table_ref=monthly_sales_long}]}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void pivotTableWithHavingAndOrderByV2HavingOrderTest() {
		final String query =
			"SELECT empid, jan_sales, feb_sales, mar_sales\n" +
			"FROM monthly_sales_long\n" +
			"PIVOT (SUM(sales_amount) FOR month_name IN (jan_sales AS 'JAN', feb_sales AS 'FEB', mar_sales AS 'MAR'))\n" +
			"GROUP BY empid, jan_sales, feb_sales, mar_sales\n" +
			"HAVING jan_sales > 100\n" +
			"ORDER BY jan_sales;";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoFatalErrors(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=empid, table_ref=null}}, 2={column={name=jan_sales, table_ref=null}}, 3={column={name=feb_sales, table_ref=null}}, 4={column={name=mar_sales, table_ref=null}}}, having={condition={left={column={name=jan_sales, table_ref=null}}, right={literal=100}, operator=>}}, orderby={1={null_order=null, predicand={column={name=jan_sales, table_ref=null}}, sort_order=ASC}}, from={pivot={value={function={function_name=SUM, parameters=sales_amount}}, for=month_name, in={1={name=jan_sales, label='JAN', table_ref=null}, 2={name=feb_sales, label='FEB', table_ref=null}, 3={name=mar_sales, label='MAR', table_ref=null}}}, table={alias=null, table=monthly_sales_long}}, groupby={1={column={name=empid, table_ref=null}}, 2={column={name=jan_sales, table_ref=null}}, 3={column={name=feb_sales, table_ref=null}}, 4={column={name=mar_sales, table_ref=null}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{query0={query_dictionary={jan_sales=[[@3,14:22='jan_sales',<381>,1:14]], empid=[[@1,7:11='empid',<381>,1:7]], mar_sales=[[@7,36:44='mar_sales',<381>,1:36]], feb_sales=[[@5,25:33='feb_sales',<381>,1:25]]}, table_dictionary={monthly_sales_long={jan_sales=[[@3,14:22='jan_sales',<381>,1:14], [@20,114:122='jan_sales',<381>,3:44], [@35,184:192='jan_sales',<381>,4:9]], empid=[[@1,7:11='empid',<381>,1:7]], mar_sales=[[@7,36:44='mar_sales',<381>,1:36], [@28,154:162='mar_sales',<381>,3:84], [@41,213:221='mar_sales',<381>,4:38]], feb_sales=[[@5,25:33='feb_sales',<381>,1:25], [@24,134:142='feb_sales',<381>,3:64], [@37,196:204='feb_sales',<381>,4:21]]}}, grouped_by=[{name=empid, table_ref=monthly_sales_long}, {name=jan_sales, table_ref=monthly_sales_long}, {name=feb_sales, table_ref=monthly_sales_long}, {name=mar_sales, table_ref=monthly_sales_long}], ordered_by=[{name=jan_sales, table_ref=monthly_sales_long}], filters=[{name=jan_sales, table_ref=monthly_sales_long}], interface={jan_sales=[{name=jan_sales, table_ref=monthly_sales_long}], empid=[{name=empid, table_ref=monthly_sales_long}], mar_sales=[{name=mar_sales, table_ref=monthly_sales_long}], feb_sales=[{name=feb_sales, table_ref=monthly_sales_long}]}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void pivotTableJoinOnWithUnqualifiedJanSalesProbeTest() {
		final String query =
			"SELECT empid, jan_sales, p.target_amount\n" +
			"FROM monthly_sales_long\n" +
			"PIVOT (SUM(sales_amount) FOR month_name IN (jan_sales AS 'JAN', feb_sales AS 'FEB', mar_sales AS 'MAR'))\n" +
			"JOIN targets p ON jan_sales >= p.target_amount;";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoFatalErrors(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=empid, table_ref=null}}, 2={column={name=jan_sales, table_ref=null}}, 3={column={name=target_amount, table_ref=p}}}, from={join={1={pivot={value={function={function_name=SUM, parameters=sales_amount}}, for=month_name, in={1={name=jan_sales, label='JAN', table_ref=null}, 2={name=feb_sales, label='FEB', table_ref=null}, 3={name=mar_sales, label='MAR', table_ref=null}}}, table={alias=null, table=monthly_sales_long}}, 2={join=JOIN, on={condition={left={column={name=jan_sales, table_ref=null}}, right={column={name=target_amount, table_ref=p}}, operator=>=}}}, 3={table={alias=p, table=targets}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{query0={query_dictionary={jan_sales=[[@3,14:22='jan_sales',<381>,1:14]], empid=[[@1,7:11='empid',<381>,1:7]], target_amount=[[@7,27:39='target_amount',<381>,1:27]]}, table_dictionary={targets={target_amount=[[@5,25:25='p',<381>,1:25], [@39,201:201='p',<381>,4:31]]}, monthly_sales_long={}}, filters=[{name=jan_sales, table_ref=null}, {name=target_amount, table_ref=p}], interface={jan_sales=[{name=jan_sales, table_ref=null}], empid=[{name=empid, table_ref=null}], target_amount=[{name=target_amount, table_ref=p}]}, table_alias={p=targets}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void pivotTableWithQualifyJanSalesProbeTest() {
		final String query =
			"SELECT empid, jan_sales, feb_sales, mar_sales\n" +
			"FROM monthly_sales_long\n" +
			"PIVOT (SUM(sales_amount) FOR month_name IN (jan_sales AS 'JAN', feb_sales AS 'FEB', mar_sales AS 'MAR'))\n" +
			"QUALIFY jan_sales > 100;";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoFatalErrors(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=empid, table_ref=null}}, 2={column={name=jan_sales, table_ref=null}}, 3={column={name=feb_sales, table_ref=null}}, 4={column={name=mar_sales, table_ref=null}}}, from={pivot={value={function={function_name=SUM, parameters=sales_amount}}, for=month_name, in={1={name=jan_sales, label='JAN', table_ref=null}, 2={name=feb_sales, label='FEB', table_ref=null}, 3={name=mar_sales, label='MAR', table_ref=null}}}, table={alias=null, table=monthly_sales_long}}, qualify={condition={left={column={name=jan_sales, table_ref=null}}, right={literal=100}, operator=>}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{query0={query_dictionary={jan_sales=[[@3,14:22='jan_sales',<381>,1:14]], empid=[[@1,7:11='empid',<381>,1:7]], mar_sales=[[@7,36:44='mar_sales',<381>,1:36]], feb_sales=[[@5,25:33='feb_sales',<381>,1:25]]}, table_dictionary={monthly_sales_long={jan_sales=[[@3,14:22='jan_sales',<381>,1:14], [@20,114:122='jan_sales',<381>,3:44], [@35,184:192='jan_sales',<381>,4:9]], empid=[[@1,7:11='empid',<381>,1:7]], mar_sales=[[@7,36:44='mar_sales',<381>,1:36], [@28,154:162='mar_sales',<381>,3:84], [@41,213:221='mar_sales',<381>,4:38]], feb_sales=[[@5,25:33='feb_sales',<381>,1:25], [@24,134:142='feb_sales',<381>,3:64], [@37,196:204='feb_sales',<381>,4:21]]}}, filters=[{name=jan_sales, table_ref=monthly_sales_long}], interface={jan_sales=[{name=jan_sales, table_ref=monthly_sales_long}], empid=[{name=empid, table_ref=monthly_sales_long}], mar_sales=[{name=mar_sales, table_ref=monthly_sales_long}], feb_sales=[{name=feb_sales, table_ref=monthly_sales_long}]}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void pivotTableWithOrderByExpressionJanFebProbeTest() {
		final String query =
			"SELECT empid, jan_sales, feb_sales, mar_sales\n" +
			"FROM monthly_sales_long\n" +
			"PIVOT (SUM(sales_amount) FOR month_name IN (jan_sales AS 'JAN', feb_sales AS 'FEB', mar_sales AS 'MAR'))\n" +
			"ORDER BY jan_sales / feb_sales;";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoFatalErrors(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=empid, table_ref=null}}, 2={column={name=jan_sales, table_ref=null}}, 3={column={name=feb_sales, table_ref=null}}, 4={column={name=mar_sales, table_ref=null}}}, orderby={1={null_order=null, predicand={calc={left={column={name=jan_sales, table_ref=null}}, right={column={name=feb_sales, table_ref=null}}, operator=/}}, sort_order=ASC}}, from={pivot={value={function={function_name=SUM, parameters=sales_amount}}, for=month_name, in={1={name=jan_sales, label='JAN', table_ref=null}, 2={name=feb_sales, label='FEB', table_ref=null}, 3={name=mar_sales, label='MAR', table_ref=null}}}, table={alias=null, table=monthly_sales_long}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{query0={query_dictionary={jan_sales=[[@3,14:22='jan_sales',<381>,1:14]], empid=[[@1,7:11='empid',<381>,1:7]], mar_sales=[[@7,36:44='mar_sales',<381>,1:36]], feb_sales=[[@5,25:33='feb_sales',<381>,1:25]]}, table_dictionary={monthly_sales_long={jan_sales=[[@3,14:22='jan_sales',<381>,1:14], [@20,114:122='jan_sales',<381>,3:44], [@35,184:192='jan_sales',<381>,4:9]], empid=[[@1,7:11='empid',<381>,1:7]], mar_sales=[[@7,36:44='mar_sales',<381>,1:36], [@28,154:162='mar_sales',<381>,3:84], [@41,213:221='mar_sales',<381>,4:38]], feb_sales=[[@5,25:33='feb_sales',<381>,1:25], [@24,134:142='feb_sales',<381>,3:64], [@37,196:204='feb_sales',<381>,4:21]]}}, ordered_by=[{name=jan_sales, table_ref=monthly_sales_long}, {name=feb_sales, table_ref=monthly_sales_long}], interface={jan_sales=[{name=jan_sales, table_ref=monthly_sales_long}], empid=[{name=empid, table_ref=monthly_sales_long}], mar_sales=[{name=mar_sales, table_ref=monthly_sales_long}], feb_sales=[{name=feb_sales, table_ref=monthly_sales_long}]}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void pivotFromDerivedAdjustedColumnsV3Test() {
		final String query =
			"SELECT empid, jan_adjusted, feb_adjusted\n" +
			"FROM (SELECT empid, month_name, sales_amount * 1.10 AS adjusted_sales FROM monthly_sales_long)\n" +
			"PIVOT (SUM(adjusted_sales) FOR month_name IN (jan_adjusted AS 'JAN', feb_adjusted AS 'FEB'));";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoFatalErrors(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=empid, table_ref=null}}, 2={column={name=jan_adjusted, table_ref=null}}, 3={column={name=feb_adjusted, table_ref=null}}}, from={select={1={column={name=empid, table_ref=null}}, 2={column={name=month_name, table_ref=null}}, 3={alias=adjusted_sales, calc={left={column={name=sales_amount, table_ref=null}}, right={literal=1.10}, operator=*}}}, pivot={value={function={function_name=SUM, parameters=adjusted_sales}}, for=month_name, in={1={name=jan_adjusted, label='JAN', table_ref=null}, 2={name=feb_adjusted, label='FEB', table_ref=null}}}, from={table={alias=null, table=monthly_sales_long}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{query1={query_dictionary={empid=[[@1,7:11='empid',<381>,1:7]], feb_adjusted=[[@5,28:39='feb_adjusted',<381>,1:28]], jan_adjusted=[[@3,14:25='jan_adjusted',<381>,1:14]]}, table_dictionary={}, query0={query_dictionary={empid=[[@9,54:58='empid',<381>,2:13]], month_name=[[@11,61:70='month_name',<381>,2:20]], adjusted_sales=[[@19,96:109='adjusted_sales',<381>,2:55]]}, table_dictionary={monthly_sales_long={empid=[[@9,54:58='empid',<381>,2:13]], month_name=[[@11,61:70='month_name',<381>,2:20]], sales_amount=[[@13,73:84='sales_amount',<381>,2:32]]}}, interface={empid=[{name=empid, table_ref=monthly_sales_long}], month_name=[{name=month_name, table_ref=monthly_sales_long}], adjusted_sales=[{name=sales_amount, table_ref=monthly_sales_long}]}}, interface={empid=[{name=empid, table_ref=null}], feb_adjusted=[{name=feb_adjusted, table_ref=null}], jan_adjusted=[{name=jan_adjusted, table_ref=null}]}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void pivotWithTaxAndWhereV4Test() {
		final String query =
			"SELECT empid, jan_sales, feb_sales, mar_sales, jan_sales * 0.07 AS tax\n" +
			"FROM monthly_sales_long\n" +
			"PIVOT (SUM(sales_amount) FOR month_name IN (jan_sales, feb_sales, mar_sales)) WHERE empid > 100;";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoFatalErrors(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=empid, table_ref=null}}, 2={column={name=jan_sales, table_ref=null}}, 3={column={name=feb_sales, table_ref=null}}, 4={column={name=mar_sales, table_ref=null}}, 5={alias=tax, calc={left={column={name=jan_sales, table_ref=null}}, right={literal=0.07}, operator=*}}}, from={pivot={value={function={function_name=SUM, parameters=sales_amount}}, for=month_name, in={1={name=jan_sales, table_ref=null}, 2={name=feb_sales, table_ref=null}, 3={name=mar_sales, table_ref=null}}}, table={alias=null, table=monthly_sales_long}}, where={condition={left={column={name=empid, table_ref=null}}, right={literal=100}, operator=>}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{query0={query_dictionary={jan_sales=[[@3,14:22='jan_sales',<381>,1:14]], empid=[[@1,7:11='empid',<381>,1:7]], mar_sales=[[@7,36:44='mar_sales',<381>,1:36]], tax=[[@15,67:69='tax',<381>,1:67]], feb_sales=[[@5,25:33='feb_sales',<381>,1:25]]}, table_dictionary={monthly_sales_long={jan_sales=[[@3,14:22='jan_sales',<381>,1:14], [@9,47:55='jan_sales',<381>,1:47], [@28,139:147='jan_sales',<381>,3:44]], empid=[[@1,7:11='empid',<381>,1:7], [@36,179:183='empid',<381>,3:84]], mar_sales=[[@7,36:44='mar_sales',<381>,1:36], [@32,161:169='mar_sales',<381>,3:66]], feb_sales=[[@5,25:33='feb_sales',<381>,1:25], [@30,150:158='feb_sales',<381>,3:55]]}}, filters=[{name=empid, table_ref=monthly_sales_long}], interface={jan_sales=[{name=jan_sales, table_ref=monthly_sales_long}], empid=[{name=empid, table_ref=monthly_sales_long}], mar_sales=[{name=mar_sales, table_ref=monthly_sales_long}], tax=[{name=jan_sales, table_ref=monthly_sales_long}], feb_sales=[{name=feb_sales, table_ref=monthly_sales_long}]}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void pivotJoinTargetsWithFilterV5Test() {
		final String query =
			"SELECT empid, jan_sales, feb_sales, jan_sales * 0.07 AS tax\n" +
			"FROM monthly_sales_long PIVOT (SUM(sales_amount) FOR month_name IN (jan_sales, feb_sales)) u\n" +
			"JOIN targets t ON u.empid = t.empid AND u.jan_sales >= t.target_amount WHERE jan_sales > 100;";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoFatalErrors(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=empid, table_ref=null}}, 2={column={name=jan_sales, table_ref=null}}, 3={column={name=feb_sales, table_ref=null}}, 4={alias=tax, calc={left={column={name=jan_sales, table_ref=null}}, right={literal=0.07}, operator=*}}}, from={join={1={pivot={value={function={function_name=SUM, parameters=sales_amount}}, for=month_name, in={1={name=jan_sales, table_ref=null}, 2={name=feb_sales, table_ref=null}, 3={name=mar_sales, table_ref=null}}}, table={alias=null, table=monthly_sales_long}}, 2={join=JOIN, on={and={1={condition={left={column={name=empid, table_ref=u}}, right={column={name=empid, table_ref=t}}, operator==}}, 2={condition={left={column={name=jan_sales, table_ref=u}}, right={column={name=target_amount, table_ref=t}}, operator=>=}}}}}, 3={table={alias=t, table=targets}}}}, where={condition={left={column={name=jan_sales, table_ref=null}}, right={literal=100}, operator=>}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{query0={query_dictionary={jan_sales=[[@3,14:22='jan_sales',<381>,1:14]], empid=[[@1,7:11='empid',<381>,1:7]], tax=[[@13,56:58='tax',<381>,1:56]], feb_sales=[[@5,25:33='feb_sales',<381>,1:25]]}, table_dictionary={targets={empid=[[@40,181:181='t',<381>,3:28]], target_amount=[[@48,208:208='t',<381>,3:55]]}, monthly_sales_long={}}, filters=[{name=empid, table_ref=u}, {name=empid, table_ref=t}, {name=jan_sales, table_ref=u}, {name=target_amount, table_ref=t}, {name=jan_sales, table_ref=null}], interface={jan_sales=[{name=jan_sales, table_ref=null}], empid=[{name=empid, table_ref=null}], tax=[{name=jan_sales, table_ref=null}], feb_sales=[{name=feb_sales, table_ref=null}]}, table_alias={t=targets}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void pivotKeepingForColumnV6Test() {
		final String query =
			"SELECT empid, month_name, jan_sales, feb_sales, mar_sales\n" +
			"FROM monthly_sales_long\n" +
			"PIVOT (SUM(sales_amount) FOR month_name IN (jan_sales, feb_sales, mar_sales));";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoFatalErrors(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=empid, table_ref=null}}, 2={column={name=month_name, table_ref=null}}, 3={column={name=jan_sales, table_ref=null}}, 4={column={name=feb_sales, table_ref=null}}, 5={column={name=mar_sales, table_ref=null}}}, from={pivot={value={function={function_name=SUM, parameters=sales_amount}}, for=month_name, in={1={name=jan_sales, table_ref=null}, 2={name=feb_sales, table_ref=null}, 3={name=mar_sales, table_ref=null}}}, table={alias=null, table=monthly_sales_long}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{query0={query_dictionary={jan_sales=[[@5,26:34='jan_sales',<381>,1:26]], empid=[[@1,7:11='empid',<381>,1:7]], month_name=[[@3,14:23='month_name',<381>,1:14]], mar_sales=[[@9,48:56='mar_sales',<381>,1:48]], feb_sales=[[@7,37:45='feb_sales',<381>,1:37]]}, table_dictionary={monthly_sales_long={jan_sales=[[@5,26:34='jan_sales',<381>,1:26], [@22,126:134='jan_sales',<381>,3:44]], empid=[[@1,7:11='empid',<381>,1:7]], month_name=[[@3,14:23='month_name',<381>,1:14]], mar_sales=[[@9,48:56='mar_sales',<381>,1:48], [@26,148:156='mar_sales',<381>,3:66]], feb_sales=[[@7,37:45='feb_sales',<381>,1:37], [@24,137:145='feb_sales',<381>,3:55]]}}, interface={jan_sales=[{name=jan_sales, table_ref=monthly_sales_long}], empid=[{name=empid, table_ref=monthly_sales_long}], month_name=[{name=month_name, table_ref=monthly_sales_long}], mar_sales=[{name=mar_sales, table_ref=monthly_sales_long}], feb_sales=[{name=feb_sales, table_ref=monthly_sales_long}]}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void pivotBasicMonthSalesV7Test() {
		final String query =
			"SELECT empid, jan_sales, feb_sales, mar_sales\n" +
			"FROM monthly_sales_long\n" +
			"PIVOT (SUM(sales_amount) FOR month_name IN (jan_sales, feb_sales, mar_sales));";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoFatalErrors(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=empid, table_ref=null}}, 2={column={name=jan_sales, table_ref=null}}, 3={column={name=feb_sales, table_ref=null}}, 4={column={name=mar_sales, table_ref=null}}}, from={pivot={value={function={function_name=SUM, parameters=sales_amount}}, for=month_name, in={1={name=jan_sales, table_ref=null}, 2={name=feb_sales, table_ref=null}, 3={name=mar_sales, table_ref=null}}}, table={alias=null, table=monthly_sales_long}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{query0={query_dictionary={jan_sales=[[@3,14:22='jan_sales',<381>,1:14]], empid=[[@1,7:11='empid',<381>,1:7]], mar_sales=[[@7,36:44='mar_sales',<381>,1:36]], feb_sales=[[@5,25:33='feb_sales',<381>,1:25]]}, table_dictionary={monthly_sales_long={jan_sales=[[@3,14:22='jan_sales',<381>,1:14], [@20,114:122='jan_sales',<381>,3:44], [@37,191:199='jan_sales',<381>,4:16], [@43,230:238='jan_sales',<381>,5:7], [@48,255:263='jan_sales',<381>,6:9]], empid=[[@1,7:11='empid',<381>,1:7], [@35,184:188='empid',<381>,4:9]], mar_sales=[[@7,36:44='mar_sales',<381>,1:36], [@28,154:162='mar_sales',<381>,3:84], [@41,213:221='mar_sales',<381>,4:38]], feb_sales=[[@5,25:33='feb_sales',<381>,1:25], [@24,134:142='feb_sales',<381>,3:64], [@39,202:210='feb_sales',<381>,4:27]]}}, interface={jan_sales=[{name=jan_sales, table_ref=monthly_sales_long}], empid=[{name=empid, table_ref=monthly_sales_long}], mar_sales=[{name=mar_sales, table_ref=monthly_sales_long}], feb_sales=[{name=feb_sales, table_ref=monthly_sales_long}]}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void pivotBasicMonthSalesJoinV8Test() {
		final String query =
			"SELECT empid, jan_sales, feb_sales, mar_sales, t2.a1, t2.a2\n" +
			"FROM monthly_sales_long\n" +
			"PIVOT (SUM(sales_amount) FOR month_name IN (jan_sales, feb_sales, mar_sales))\n" +
			"JOIN metrics_table t2 ON empid = t2.metric_label;";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoFatalErrors(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=empid, table_ref=null}}, 2={column={name=jan_sales, table_ref=null}}, 3={column={name=feb_sales, table_ref=null}}, 4={column={name=mar_sales, table_ref=null}}, 5={column={name=a1, table_ref=t2}}, 6={column={name=a2, table_ref=t2}}}, from={join={1={pivot={value={function={function_name=SUM, parameters=sales_amount}}, for=month_name, in={1={name=jan_sales, table_ref=null}, 2={name=feb_sales, table_ref=null}, 3={name=mar_sales, table_ref=null}}}, table={alias=null, table=monthly_sales_long}}, 2={join=JOIN, on={condition={left={column={name=empid, table_ref=null}}, right={column={name=metric_label, table_ref=t2}}, operator==}}}, 3={table={alias=t2, table=metrics_table}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{query0={query_dictionary={jan_sales=[[@3,14:22='jan_sales',<381>,1:14]], a1=[[@11,50:51='a1',<381>,1:50]], empid=[[@1,7:11='empid',<381>,1:7]], mar_sales=[[@7,36:44='mar_sales',<381>,1:36]], a2=[[@15,57:58='a2',<381>,1:57]], feb_sales=[[@5,25:33='feb_sales',<381>,1:25]]}, table_dictionary={metrics_table={a1=[[@9,47:48='t2',<381>,1:47]], a2=[[@13,54:55='t2',<381>,1:54]], metric_label=[[@41,195:196='t2',<381>,4:33]]}, monthly_sales_long={}}, filters=[{name=empid, table_ref=null}, {name=metric_label, table_ref=t2}], interface={jan_sales=[{name=jan_sales, table_ref=null}], a1=[{name=a1, table_ref=t2}], empid=[{name=empid, table_ref=null}], mar_sales=[{name=mar_sales, table_ref=null}], a2=[{name=a2, table_ref=t2}], feb_sales=[{name=feb_sales, table_ref=null}]}, table_alias={t2=metrics_table}}}",
				extractor.getSymbolTable().toString());
	}

	/*
		TUPLE TESTS WITH PIVOT AND UNPIVOT
	*/
	@Test
	public void generatorDirectFromListTupleEndpointNakedSyntaxBuildsSameAstShapeTest() {
		final String query = "tab1 pivot (sum(col1) for col2 in (A, B))";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runTupleParsertest(query, parser);

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong", "{TUPLE={pivot={value={function={function_name=sum, parameters=col1}}, for=col2, in={1={name=A, table_ref=null}, 2={name=B, table_ref=null}}}, table={table=tab1}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{table_dictionary={tab1={}}, unresolved_column={A={column={name=A, table_ref=null}, locations=[[@11,35:35='A',<381>,1:35]]}, B={column={name=B, table_ref=null}, locations=[[@13,38:38='B',<381>,1:38]]}}}", extractor.getSymbolTable().toString());
	}

}
