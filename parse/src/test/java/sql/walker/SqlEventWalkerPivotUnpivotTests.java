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
		Assert.assertEquals("Table Dictionary is wrong", "{my_table={jan_sales=[[@15,98:106='jan_sales',<380>,4:35]], mar_sales=[[@19,120:128='mar_sales',<380>,4:57]], metric_name=[[@3,11:21='metric_name',<380>,1:11]], metric_value=[[@5,24:35='metric_value',<380>,1:24]], id=[[@1,7:8='id',<380>,1:7]], feb_sales=[[@17,109:117='feb_sales',<380>,4:46]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={metric_name=[[@3,11:21='metric_name',<380>,1:11]], metric_value=[[@5,24:35='metric_value',<380>,1:24]], id=[[@1,7:8='id',<380>,1:7]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{query0={query_dictionary={metric_name=[[@3,11:21='metric_name',<380>,1:11]], metric_value=[[@5,24:35='metric_value',<380>,1:24]], id=[[@1,7:8='id',<380>,1:7]]}, table_dictionary={my_table={jan_sales=[[@15,98:106='jan_sales',<380>,4:35]], mar_sales=[[@19,120:128='mar_sales',<380>,4:57]], metric_name=[[@3,11:21='metric_name',<380>,1:11]], metric_value=[[@5,24:35='metric_value',<380>,1:24]], id=[[@1,7:8='id',<380>,1:7]], feb_sales=[[@17,109:117='feb_sales',<380>,4:46]]}}, interface={metric_name=[{name=metric_name, table_ref=my_table}], metric_value=[{name=metric_value, table_ref=my_table}], id=[{name=id, table_ref=my_table}]}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void unpivotWithInAliasesJanFebMarV2Test() {
		final String query =
			"SELECT empid, month_name, sales_amount\n" +
			"FROM monthly_sales\n" +
			"UNPIVOT (sales_amount FOR month_name IN (jan_sales AS 'JAN', feb_sales AS 'FEB', mar_sales AS 'MAR'));";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong",
				"{106_1={}, SQL={select={1={column={name=empid, table_ref=null}}, 2={column={name=month_name, table_ref=null}}, 3={column={name=sales_amount, table_ref=null}}}, from={unpivot={value=sales_amount, for=month_name, in={1={name=jan_sales, label='JAN', table_ref=null}, 2={name=feb_sales, label='FEB', table_ref=null}, 3={name=mar_sales, label='MAR', table_ref=null}}}, table={alias=null, table=monthly_sales}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[empid, month_name, sales_amount]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{monthly_sales={jan_sales=[[@15,99:107='jan_sales',<380>,3:41]], empid=[[@1,7:11='empid',<380>,1:7]], month_name=[[@3,14:23='month_name',<380>,1:14]], mar_sales=[[@23,139:147='mar_sales',<380>,3:81]], sales_amount=[[@5,26:37='sales_amount',<380>,1:26]], feb_sales=[[@19,119:127='feb_sales',<380>,3:61]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={empid=[[@1,7:11='empid',<380>,1:7]], month_name=[[@3,14:23='month_name',<380>,1:14]], sales_amount=[[@5,26:37='sales_amount',<380>,1:26]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{query0={query_dictionary={empid=[[@1,7:11='empid',<380>,1:7]], month_name=[[@3,14:23='month_name',<380>,1:14]], sales_amount=[[@5,26:37='sales_amount',<380>,1:26]]}, table_dictionary={monthly_sales={jan_sales=[[@15,99:107='jan_sales',<380>,3:41]], empid=[[@1,7:11='empid',<380>,1:7]], month_name=[[@3,14:23='month_name',<380>,1:14]], mar_sales=[[@23,139:147='mar_sales',<380>,3:81]], sales_amount=[[@5,26:37='sales_amount',<380>,1:26]], feb_sales=[[@19,119:127='feb_sales',<380>,3:61]]}}, interface={empid=[{name=empid, table_ref=monthly_sales}], month_name=[{name=month_name, table_ref=monthly_sales}], sales_amount=[{name=sales_amount, table_ref=monthly_sales}]}}}",
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
		Assert.assertEquals("Walker Error diagnostics are wrong",
				"[Unresolved unqualified column reference(s): [empid [(l:1 c:7)], month_name [(l:1 c:14)], feb_adjusted [(l:3 c:64)], jan_adjusted [(l:3 c:41)], sales_amount [(l:1 c:26)]]]",
				extractor.getSnippet().getErrorStringList(errorhandling.ParseDiagnostic.Severity.ERROR).toString());
		Assert.assertEquals("AST is wrong",
				"{106_1={}, SQL={select={1={column={name=empid, table_ref=null}}, 2={column={name=month_name, table_ref=null}}, 3={column={name=sales_amount, table_ref=null}}}, from={unpivot={value=sales_amount, for=month_name, in={1={name=jan_adjusted, label='JAN', table_ref=null}, 2={name=feb_adjusted, label='FEB', table_ref=null}}}, select={1={column={name=empid, table_ref=null}}, 2={alias=jan_adjusted, calc={left={column={name=jan_sales, table_ref=null}}, right={literal=1.10}, operator=*}}, 3={alias=feb_adjusted, calc={left={column={name=feb_sales, table_ref=null}}, right={literal=1.10}, operator=*}}}, from={table={alias=null, table=monthly_sales}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[empid, month_name, sales_amount]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{monthly_sales={jan_sales=[[@11,59:67='jan_sales',<380>,2:20]], empid=[[@9,52:56='empid',<380>,2:13]], feb_sales=[[@19,93:101='feb_sales',<380>,2:54]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={empid=[[@9,52:56='empid',<380>,2:13]], feb_adjusted=[[@25,113:124='feb_adjusted',<380>,2:74]], jan_adjusted=[[@17,79:90='jan_adjusted',<380>,2:40]]}, query1={empid=[[@1,7:11='empid',<380>,1:7]], month_name=[[@3,14:23='month_name',<380>,1:14]], sales_amount=[[@5,26:37='sales_amount',<380>,1:26]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{query1={query_dictionary={empid=[[@1,7:11='empid',<380>,1:7]], month_name=[[@3,14:23='month_name',<380>,1:14]], sales_amount=[[@5,26:37='sales_amount',<380>,1:26]]}, table_dictionary={}, query0={query_dictionary={empid=[[@9,52:56='empid',<380>,2:13]], feb_adjusted=[[@25,113:124='feb_adjusted',<380>,2:74]], jan_adjusted=[[@17,79:90='jan_adjusted',<380>,2:40]]}, table_dictionary={monthly_sales={jan_sales=[[@11,59:67='jan_sales',<380>,2:20]], empid=[[@9,52:56='empid',<380>,2:13]], feb_sales=[[@19,93:101='feb_sales',<380>,2:54]]}}, interface={empid=[{name=empid, table_ref=monthly_sales}], feb_adjusted=[{name=feb_sales, table_ref=monthly_sales}], jan_adjusted=[{name=jan_sales, table_ref=monthly_sales}]}}, interface={empid=[{name=empid, table_ref=null}], month_name=[{name=month_name, table_ref=null}], sales_amount=[{name=sales_amount, table_ref=null}]}}}",
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
		Assert.assertEquals("Table Dictionary is wrong", "{monthly_sales={jan_sales=[[@23,127:135='jan_sales',<380>,3:41]], empid=[[@1,7:11='empid',<380>,1:7]], month_name=[[@3,14:23='month_name',<380>,1:14]], mar_sales=[[@27,149:157='mar_sales',<380>,3:63]], sales_amount=[[@5,26:37='sales_amount',<380>,1:26], [@7,40:51='sales_amount',<380>,1:40], [@31,167:178='sales_amount',<380>,3:81]], feb_sales=[[@25,138:146='feb_sales',<380>,3:52]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={empid=[[@1,7:11='empid',<380>,1:7]], month_name=[[@3,14:23='month_name',<380>,1:14]], sales_amount=[[@5,26:37='sales_amount',<380>,1:26]], tax=[[@13,63:65='tax',<380>,1:63]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{query0={query_dictionary={empid=[[@1,7:11='empid',<380>,1:7]], month_name=[[@3,14:23='month_name',<380>,1:14]], sales_amount=[[@5,26:37='sales_amount',<380>,1:26]], tax=[[@13,63:65='tax',<380>,1:63]]}, table_dictionary={monthly_sales={jan_sales=[[@23,127:135='jan_sales',<380>,3:41]], empid=[[@1,7:11='empid',<380>,1:7]], month_name=[[@3,14:23='month_name',<380>,1:14]], mar_sales=[[@27,149:157='mar_sales',<380>,3:63]], sales_amount=[[@5,26:37='sales_amount',<380>,1:26], [@7,40:51='sales_amount',<380>,1:40], [@31,167:178='sales_amount',<380>,3:81]], feb_sales=[[@25,138:146='feb_sales',<380>,3:52]]}}, filters=[{name=sales_amount, table_ref=monthly_sales}], interface={empid=[{name=empid, table_ref=monthly_sales}], month_name=[{name=month_name, table_ref=monthly_sales}], sales_amount=[{name=sales_amount, table_ref=monthly_sales}], tax=[{name=sales_amount, table_ref=monthly_sales}]}}}",
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
				"[Unresolved unqualified column reference(s): [jan_sales [(l:2 c:60)], empid [(l:1 c:7)], month_name [(l:1 c:14)], sales_amount [(l:1 c:26), (l:1 c:40), (l:3 c:90)], feb_sales [(l:2 c:71)]]]",
				extractor.getSnippet().getErrorStringList(errorhandling.ParseDiagnostic.Severity.ERROR).toString());
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=empid, table_ref=null}}, 2={column={name=month_name, table_ref=null}}, 3={column={name=sales_amount, table_ref=null}}, 4={alias=tax, calc={left={column={name=sales_amount, table_ref=null}}, right={literal=0.07}, operator=*}}}, from={join={1={unpivot={value=sales_amount, for=month_name, in={1={name=jan_sales, table_ref=null}, 2={name=feb_sales, table_ref=null}}, alias=u}, table={alias=null, table=monthly_sales}}, 2={join=JOIN, on={and={1={condition={left={column={name=month_name, table_ref=u}}, right={column={name=month_name, table_ref=t}}, operator==}}, 2={condition={left={column={name=sales_amount, table_ref=u}}, right={column={name=target_amount, table_ref=t}}, operator=>=}}}}}, 3={table={alias=t, table=targets}}}}, where={condition={left={column={name=sales_amount, table_ref=null}}, right={literal=100}, operator=>}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[empid, month_name, sales_amount, tax]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{monthly_sales={}, targets={month_name=[[@37,185:185='t',<380>,3:33]], target_amount=[[@45,220:220='t',<380>,3:68]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={empid=[[@1,7:11='empid',<380>,1:7]], month_name=[[@3,14:23='month_name',<380>,1:14]], sales_amount=[[@5,26:37='sales_amount',<380>,1:26]], tax=[[@13,63:65='tax',<380>,1:63]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{query0={query_dictionary={empid=[[@1,7:11='empid',<380>,1:7]], month_name=[[@3,14:23='month_name',<380>,1:14]], sales_amount=[[@5,26:37='sales_amount',<380>,1:26]], tax=[[@13,63:65='tax',<380>,1:63]]}, table_dictionary={monthly_sales={}, targets={month_name=[[@37,185:185='t',<380>,3:33]], target_amount=[[@45,220:220='t',<380>,3:68]]}}, filters=[{name=month_name, table_ref=u}, {name=month_name, table_ref=t}, {name=sales_amount, table_ref=u}, {name=target_amount, table_ref=t}, {name=sales_amount, table_ref=null}], interface={empid=[{name=empid, table_ref=null}], month_name=[{name=month_name, table_ref=null}], sales_amount=[{name=sales_amount, table_ref=null}], tax=[{name=sales_amount, table_ref=null}]}, table_alias={t=targets}}}",
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
		Assert.assertEquals("Table Dictionary is wrong", "{monthly_sales={jan_sales=[[@3,14:22='jan_sales',<380>,1:14], [@19,121:129='jan_sales',<380>,3:41]], empid=[[@1,7:11='empid',<380>,1:7]], month_name=[[@7,36:45='month_name',<380>,1:36]], mar_sales=[[@23,143:151='mar_sales',<380>,3:63]], sales_amount=[[@9,48:59='sales_amount',<380>,1:48]], feb_sales=[[@5,25:33='feb_sales',<380>,1:25], [@21,132:140='feb_sales',<380>,3:52]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={jan_sales=[[@3,14:22='jan_sales',<380>,1:14]], empid=[[@1,7:11='empid',<380>,1:7]], month_name=[[@7,36:45='month_name',<380>,1:36]], sales_amount=[[@9,48:59='sales_amount',<380>,1:48]], feb_sales=[[@5,25:33='feb_sales',<380>,1:25]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{query0={query_dictionary={jan_sales=[[@3,14:22='jan_sales',<380>,1:14]], empid=[[@1,7:11='empid',<380>,1:7]], month_name=[[@7,36:45='month_name',<380>,1:36]], sales_amount=[[@9,48:59='sales_amount',<380>,1:48]], feb_sales=[[@5,25:33='feb_sales',<380>,1:25]]}, table_dictionary={monthly_sales={jan_sales=[[@3,14:22='jan_sales',<380>,1:14], [@19,121:129='jan_sales',<380>,3:41]], empid=[[@1,7:11='empid',<380>,1:7]], month_name=[[@7,36:45='month_name',<380>,1:36]], mar_sales=[[@23,143:151='mar_sales',<380>,3:63]], sales_amount=[[@9,48:59='sales_amount',<380>,1:48]], feb_sales=[[@5,25:33='feb_sales',<380>,1:25], [@21,132:140='feb_sales',<380>,3:52]]}}, interface={jan_sales=[{name=jan_sales, table_ref=monthly_sales}], empid=[{name=empid, table_ref=monthly_sales}], month_name=[{name=month_name, table_ref=monthly_sales}], sales_amount=[{name=sales_amount, table_ref=monthly_sales}], feb_sales=[{name=feb_sales, table_ref=monthly_sales}]}}}",
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
		Assert.assertEquals("Table Dictionary is wrong", "{monthly_sales={jan_sales=[[@15,99:107='jan_sales',<380>,3:41]], empid=[[@1,7:11='empid',<380>,1:7]], month_name=[[@3,14:23='month_name',<380>,1:14]], mar_sales=[[@19,121:129='mar_sales',<380>,3:63]], sales_amount=[[@5,26:37='sales_amount',<380>,1:26]], feb_sales=[[@17,110:118='feb_sales',<380>,3:52]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={empid=[[@1,7:11='empid',<380>,1:7]], month_name=[[@3,14:23='month_name',<380>,1:14]], sales_amount=[[@5,26:37='sales_amount',<380>,1:26]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{query0={query_dictionary={empid=[[@1,7:11='empid',<380>,1:7]], month_name=[[@3,14:23='month_name',<380>,1:14]], sales_amount=[[@5,26:37='sales_amount',<380>,1:26]]}, table_dictionary={monthly_sales={jan_sales=[[@15,99:107='jan_sales',<380>,3:41]], empid=[[@1,7:11='empid',<380>,1:7]], month_name=[[@3,14:23='month_name',<380>,1:14]], mar_sales=[[@19,121:129='mar_sales',<380>,3:63]], sales_amount=[[@5,26:37='sales_amount',<380>,1:26]], feb_sales=[[@17,110:118='feb_sales',<380>,3:52]]}}, interface={empid=[{name=empid, table_ref=monthly_sales}], month_name=[{name=month_name, table_ref=monthly_sales}], sales_amount=[{name=sales_amount, table_ref=monthly_sales}]}}}",
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
				"[Unresolved unqualified column reference(s): [jan_sales [(l:3 c:41)], empid [(l:1 c:7)], month_name [(l:1 c:14), (l:4 c:25)], mar_sales [(l:3 c:63)], sales_amount [(l:1 c:26)], feb_sales [(l:3 c:52)]]]",
				extractor.getSnippet().getErrorStringList(errorhandling.ParseDiagnostic.Severity.ERROR).toString());
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=empid, table_ref=null}}, 2={column={name=month_name, table_ref=null}}, 3={column={name=sales_amount, table_ref=null}}, 4={column={name=a1, table_ref=t2}}, 5={column={name=a2, table_ref=t2}}}, from={join={1={unpivot={value=sales_amount, for=month_name, in={1={name=jan_sales, table_ref=null}, 2={name=feb_sales, table_ref=null}, 3={name=mar_sales, table_ref=null}}}, table={alias=null, table=monthly_sales}}, 2={join=JOIN, on={condition={left={column={name=month_name, table_ref=null}}, right={column={name=metric_label, table_ref=t2}}, operator==}}}, 3={table={alias=t2, table=metrics_table}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[a1, empid, month_name, a2, sales_amount]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{monthly_sales={}, metrics_table={a1=[[@7,40:41='t2',<380>,1:40]], a2=[[@11,47:48='t2',<380>,1:47]], metric_label=[[@36,185:186='t2',<380>,4:38]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={a1=[[@9,43:44='a1',<380>,1:43]], empid=[[@1,7:11='empid',<380>,1:7]], month_name=[[@3,14:23='month_name',<380>,1:14]], a2=[[@13,50:51='a2',<380>,1:50]], sales_amount=[[@5,26:37='sales_amount',<380>,1:26]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{query0={query_dictionary={a1=[[@9,43:44='a1',<380>,1:43]], empid=[[@1,7:11='empid',<380>,1:7]], month_name=[[@3,14:23='month_name',<380>,1:14]], a2=[[@13,50:51='a2',<380>,1:50]], sales_amount=[[@5,26:37='sales_amount',<380>,1:26]]}, table_dictionary={monthly_sales={}, metrics_table={a1=[[@7,40:41='t2',<380>,1:40]], a2=[[@11,47:48='t2',<380>,1:47]], metric_label=[[@36,185:186='t2',<380>,4:38]]}}, filters=[{name=month_name, table_ref=null}, {name=metric_label, table_ref=t2}], interface={a1=[{name=a1, table_ref=t2}], empid=[{name=empid, table_ref=null}], month_name=[{name=month_name, table_ref=null}], a2=[{name=a2, table_ref=t2}], sales_amount=[{name=sales_amount, table_ref=null}]}, table_alias={t2=metrics_table}}}",
				extractor.getSymbolTable().toString());
	}

	// PIVOT RELATIONAL OPERATOR TESTS

	@Test
	public void pivotV1Test() {
		final String query = "select * from tab1 pivot (sum(col1) for col2 in (A, B))";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=*, table_ref=*}}}, from={pivot={value={function={function_name=sum, parameters=col1}}, for=col2, in={1={name=A, table_ref=null}, 2={name=B, table_ref=null}}}, table={alias=null, table=tab1}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={A=[[@14,49:49='A',<380>,1:49]], B=[[@16,52:52='B',<380>,1:52]], *=[[@1,7:7='*',<290>,1:7]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@1,7:7='*',<290>,1:7]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{query0={query_dictionary={*=[[@1,7:7='*',<290>,1:7]]}, table_dictionary={tab1={A=[[@14,49:49='A',<380>,1:49]], B=[[@16,52:52='B',<380>,1:52]], *=[[@1,7:7='*',<290>,1:7]]}}, interface={*=[{name=*, table_ref=*}]}}}",
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
		Assert.assertEquals("Symbol Table is wrong", "{table_dictionary={tab1={}}, unresolved_column={A={column={name=A, table_ref=null}, locations=[[@11,35:35='A',<380>,1:35]]}, B={column={name=B, table_ref=null}, locations=[[@13,38:38='B',<380>,1:38]]}}}", extractor.getSymbolTable().toString());
	}

}
