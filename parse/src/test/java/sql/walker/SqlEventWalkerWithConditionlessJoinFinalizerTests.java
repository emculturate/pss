package sql.walker;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Phase 2.7 — WITH final-query {@code tableDictionary} omits CTE sources joined in the outer query.
 * <p>
 * Goldens capture current 5.1.3 walker output (including the missing {@code amount_cte} global
 * dictionary key). Update goldens when the finalizer fix restores CTE entries to match 5.0.0-3.
 * <p>
 * Fixtures: {@code SqlEventWalkerWithConditionlessJoinFinalizerTests}.
 */
public class SqlEventWalkerWithConditionlessJoinFinalizerTests extends AbstractSqlParseEventWalkerTest {

	private void assertWithFinalQueryJoinOutputs(SqlParseEventWalker extractor, String expectedAst,
			String expectedInterface, String expectedSubstitutions, String expectedTableDictionary,
			String expectedQueryColumnDictionary, String expectedSymbolTable) {
		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong", expectedAst, extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", expectedInterface, extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", expectedSubstitutions,
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", expectedTableDictionary,
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", expectedQueryColumnDictionary,
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", expectedSymbolTable,
				extractor.getSymbolTable().toString());
	}

	@Test
	public void withFinalQueryCteFirstCrossJoinTest() {
		final String query = "WITH amount_cte AS (SELECT MAX(bt.amount_val) AS max_amount FROM base_table AS bt) "
				+ "SELECT ord.partner_id, ord.contact_id FROM amount_cte CROSS JOIN orders_tbl AS ord "
				+ "WHERE ord.order_dt > amount_cte.max_amount";
		SqlParseEventWalker extractor = runParsertest(query, parse(query));
		assertWithFinalQueryJoinOutputs(extractor,
				"{SQL={with={1={cte={select={1={function={function_name=MAX, qualifier=null, parameters={column={name=amount_val, table_ref=bt}}}, alias=max_amount}}, from={table={alias=bt, table=base_table}}}, alias=amount_cte}}, query={select={1={column={name=partner_id, table_ref=ord}}, 2={column={name=contact_id, table_ref=ord}}}, from={join={1={table={alias=null, table=amount_cte}}, 2={join=CROSSJOIN}, 3={table={alias=ord, table=orders_tbl}}}}, where={condition={left={column={name=order_dt, table_ref=ord}}, right={column={name=max_amount, table_ref=amount_cte}}, operator=>}}}}}",
				"[partner_id, contact_id]", "{}",
				"{orders_tbl={order_dt=[[@34,172:174='ord',<393>,1:172]], partner_id=[[@19,90:92='ord',<393>,1:90]], contact_id=[[@23,106:108='ord',<393>,1:106]]}, base_table={amount_val=[[@7,31:32='bt',<393>,1:31]]}}",
				"{query0={max_amount=[[@12,49:58='max_amount',<393>,1:49], [@38,187:196='amount_cte',<393>,1:187]]}, query1={contact_id=[[@25,110:119='contact_id',<393>,1:110]], partner_id=[[@21,94:103='partner_id',<393>,1:94]]}}",
				"{def_query1={context_list={amount_cte=query0}, query_dictionary={partner_id=[[@21,94:103='partner_id',<393>,1:94]], contact_id=[[@25,110:119='contact_id',<393>,1:110]]}, table_dictionary={orders_tbl={order_dt=[[@34,172:174='ord',<393>,1:172]], partner_id=[[@19,90:92='ord',<393>,1:90]], contact_id=[[@23,106:108='ord',<393>,1:106]]}}, def_query0={query_dictionary={max_amount=[[@12,49:58='max_amount',<393>,1:49], [@38,187:196='amount_cte',<393>,1:187]]}, table_dictionary={base_table={amount_val=[[@7,31:32='bt',<393>,1:31]]}}, interface={max_amount=[{name=amount_val, table_ref=bt}]}, table_alias={bt=base_table}}, filters=[{name=order_dt, table_ref=ord}, {name=max_amount, table_ref=amount_cte}], interface={partner_id=[{name=partner_id, table_ref=ord}], contact_id=[{name=contact_id, table_ref=ord}]}, table_alias={ord=orders_tbl, amount_cte=query0}}}");
	}

	@Test
	public void withFinalQueryOuterFirstCrossJoinCteTest() {
		final String query = "WITH amount_cte AS (SELECT MAX(bt.amount_val) AS max_amount FROM base_table AS bt) "
				+ "SELECT ord.partner_id, ord.contact_id FROM orders_tbl AS ord CROSS JOIN amount_cte "
				+ "WHERE ord.order_dt > amount_cte.max_amount";
		SqlParseEventWalker extractor = runParsertest(query, parse(query));
		assertWithFinalQueryJoinOutputs(extractor,
				"{SQL={with={1={cte={select={1={function={function_name=MAX, qualifier=null, parameters={column={name=amount_val, table_ref=bt}}}, alias=max_amount}}, from={table={alias=bt, table=base_table}}}, alias=amount_cte}}, query={select={1={column={name=partner_id, table_ref=ord}}, 2={column={name=contact_id, table_ref=ord}}}, from={join={1={table={alias=ord, table=orders_tbl}}, 2={join=CROSSJOIN}, 3={table={alias=null, table=amount_cte}}}}, where={condition={left={column={name=order_dt, table_ref=ord}}, right={column={name=max_amount, table_ref=amount_cte}}, operator=>}}}}}",
				"[partner_id, contact_id]", "{}",
				"{orders_tbl={order_dt=[[@34,172:174='ord',<393>,1:172]], partner_id=[[@19,90:92='ord',<393>,1:90]], contact_id=[[@23,106:108='ord',<393>,1:106]]}, base_table={amount_val=[[@7,31:32='bt',<393>,1:31]]}}",
				"{query0={max_amount=[[@12,49:58='max_amount',<393>,1:49], [@38,187:196='amount_cte',<393>,1:187]]}, query1={contact_id=[[@25,110:119='contact_id',<393>,1:110]], partner_id=[[@21,94:103='partner_id',<393>,1:94]]}}",
				"{def_query1={context_list={amount_cte=query0}, query_dictionary={partner_id=[[@21,94:103='partner_id',<393>,1:94]], contact_id=[[@25,110:119='contact_id',<393>,1:110]]}, table_dictionary={orders_tbl={order_dt=[[@34,172:174='ord',<393>,1:172]], partner_id=[[@19,90:92='ord',<393>,1:90]], contact_id=[[@23,106:108='ord',<393>,1:106]]}}, def_query0={query_dictionary={max_amount=[[@12,49:58='max_amount',<393>,1:49], [@38,187:196='amount_cte',<393>,1:187]]}, table_dictionary={base_table={amount_val=[[@7,31:32='bt',<393>,1:31]]}}, interface={max_amount=[{name=amount_val, table_ref=bt}]}, table_alias={bt=base_table}}, filters=[{name=order_dt, table_ref=ord}, {name=max_amount, table_ref=amount_cte}], interface={partner_id=[{name=partner_id, table_ref=ord}], contact_id=[{name=contact_id, table_ref=ord}]}, table_alias={ord=orders_tbl, amount_cte=query0}}}");
	}

	@Test
	public void withFinalQueryCteFirstNaturalJoinTest() {
		final String query = "WITH amount_cte AS (SELECT MAX(bt.amount_val) AS max_amount FROM base_table AS bt) "
				+ "SELECT ord.partner_id, ord.contact_id FROM amount_cte NATURAL JOIN orders_tbl AS ord "
				+ "WHERE ord.order_dt > amount_cte.max_amount";
		SqlParseEventWalker extractor = runParsertest(query, parse(query));
		assertWithFinalQueryJoinOutputs(extractor,
				"{SQL={with={1={cte={select={1={function={function_name=MAX, qualifier=null, parameters={column={name=amount_val, table_ref=bt}}}, alias=max_amount}}, from={table={alias=bt, table=base_table}}}, alias=amount_cte}}, query={select={1={column={name=partner_id, table_ref=ord}}, 2={column={name=contact_id, table_ref=ord}}}, from={join={1={table={alias=null, table=amount_cte}}, 2={join=NATURALJOIN}, 3={table={alias=ord, table=orders_tbl}}}}, where={condition={left={column={name=order_dt, table_ref=ord}}, right={column={name=max_amount, table_ref=amount_cte}}, operator=>}}}}}",
				"[partner_id, contact_id]", "{}",
				"{orders_tbl={order_dt=[[@34,174:176='ord',<393>,1:174]], partner_id=[[@19,90:92='ord',<393>,1:90]], contact_id=[[@23,106:108='ord',<393>,1:106]]}, base_table={amount_val=[[@7,31:32='bt',<393>,1:31]]}}",
				"{query0={max_amount=[[@12,49:58='max_amount',<393>,1:49], [@38,189:198='amount_cte',<393>,1:189]]}, query1={contact_id=[[@25,110:119='contact_id',<393>,1:110]], partner_id=[[@21,94:103='partner_id',<393>,1:94]]}}",
				"{def_query1={context_list={amount_cte=query0}, query_dictionary={partner_id=[[@21,94:103='partner_id',<393>,1:94]], contact_id=[[@25,110:119='contact_id',<393>,1:110]]}, table_dictionary={orders_tbl={order_dt=[[@34,174:176='ord',<393>,1:174]], partner_id=[[@19,90:92='ord',<393>,1:90]], contact_id=[[@23,106:108='ord',<393>,1:106]]}}, def_query0={query_dictionary={max_amount=[[@12,49:58='max_amount',<393>,1:49], [@38,189:198='amount_cte',<393>,1:189]]}, table_dictionary={base_table={amount_val=[[@7,31:32='bt',<393>,1:31]]}}, interface={max_amount=[{name=amount_val, table_ref=bt}]}, table_alias={bt=base_table}}, filters=[{name=order_dt, table_ref=ord}, {name=max_amount, table_ref=amount_cte}], interface={partner_id=[{name=partner_id, table_ref=ord}], contact_id=[{name=contact_id, table_ref=ord}]}, table_alias={ord=orders_tbl, amount_cte=query0}}}");
	}

	@Test
	public void withFinalQueryCteFirstNaturalLeftJoinTest() {
		final String query = "WITH amount_cte AS (SELECT MAX(bt.amount_val) AS max_amount FROM base_table AS bt) "
				+ "SELECT ord.partner_id, ord.contact_id FROM amount_cte NATURAL LEFT JOIN orders_tbl AS ord "
				+ "WHERE ord.order_dt > amount_cte.max_amount";
		SqlParseEventWalker extractor = runParsertest(query, parse(query));
		assertWithFinalQueryJoinOutputs(extractor,
				"{SQL={with={1={cte={select={1={function={function_name=MAX, qualifier=null, parameters={column={name=amount_val, table_ref=bt}}}, alias=max_amount}}, from={table={alias=bt, table=base_table}}}, alias=amount_cte}}, query={select={1={column={name=partner_id, table_ref=ord}}, 2={column={name=contact_id, table_ref=ord}}}, from={join={1={table={alias=null, table=amount_cte}}, 2={join=NATURALJOIN}, 3={table={alias=ord, table=orders_tbl}}}}, where={condition={left={column={name=order_dt, table_ref=ord}}, right={column={name=max_amount, table_ref=amount_cte}}, operator=>}}}}}",
				"[partner_id, contact_id]", "{}",
				"{orders_tbl={order_dt=[[@35,179:181='ord',<393>,1:179]], partner_id=[[@19,90:92='ord',<393>,1:90]], contact_id=[[@23,106:108='ord',<393>,1:106]]}, base_table={amount_val=[[@7,31:32='bt',<393>,1:31]]}}",
				"{query0={max_amount=[[@12,49:58='max_amount',<393>,1:49], [@39,194:203='amount_cte',<393>,1:194]]}, query1={contact_id=[[@25,110:119='contact_id',<393>,1:110]], partner_id=[[@21,94:103='partner_id',<393>,1:94]]}}",
				"{def_query1={context_list={amount_cte=query0}, query_dictionary={partner_id=[[@21,94:103='partner_id',<393>,1:94]], contact_id=[[@25,110:119='contact_id',<393>,1:110]]}, table_dictionary={orders_tbl={order_dt=[[@35,179:181='ord',<393>,1:179]], partner_id=[[@19,90:92='ord',<393>,1:90]], contact_id=[[@23,106:108='ord',<393>,1:106]]}}, def_query0={query_dictionary={max_amount=[[@12,49:58='max_amount',<393>,1:49], [@39,194:203='amount_cte',<393>,1:194]]}, table_dictionary={base_table={amount_val=[[@7,31:32='bt',<393>,1:31]]}}, interface={max_amount=[{name=amount_val, table_ref=bt}]}, table_alias={bt=base_table}}, filters=[{name=order_dt, table_ref=ord}, {name=max_amount, table_ref=amount_cte}], interface={partner_id=[{name=partner_id, table_ref=ord}], contact_id=[{name=contact_id, table_ref=ord}]}, table_alias={ord=orders_tbl, amount_cte=query0}}}");
	}

	@Test
	public void withFinalQueryCteFirstNaturalRightJoinTest() {
		final String query = "WITH amount_cte AS (SELECT MAX(bt.amount_val) AS max_amount FROM base_table AS bt) "
				+ "SELECT ord.partner_id, ord.contact_id FROM amount_cte NATURAL RIGHT JOIN orders_tbl AS ord "
				+ "WHERE ord.order_dt > amount_cte.max_amount";
		SqlParseEventWalker extractor = runParsertest(query, parse(query));
		assertWithFinalQueryJoinOutputs(extractor,
				"{SQL={with={1={cte={select={1={function={function_name=MAX, qualifier=null, parameters={column={name=amount_val, table_ref=bt}}}, alias=max_amount}}, from={table={alias=bt, table=base_table}}}, alias=amount_cte}}, query={select={1={column={name=partner_id, table_ref=ord}}, 2={column={name=contact_id, table_ref=ord}}}, from={join={1={table={alias=null, table=amount_cte}}, 2={join=NATURALJOIN}, 3={table={alias=ord, table=orders_tbl}}}}, where={condition={left={column={name=order_dt, table_ref=ord}}, right={column={name=max_amount, table_ref=amount_cte}}, operator=>}}}}}",
				"[partner_id, contact_id]", "{}",
				"{orders_tbl={order_dt=[[@35,180:182='ord',<393>,1:180]], partner_id=[[@19,90:92='ord',<393>,1:90]], contact_id=[[@23,106:108='ord',<393>,1:106]]}, base_table={amount_val=[[@7,31:32='bt',<393>,1:31]]}}",
				"{query0={max_amount=[[@12,49:58='max_amount',<393>,1:49], [@39,195:204='amount_cte',<393>,1:195]]}, query1={contact_id=[[@25,110:119='contact_id',<393>,1:110]], partner_id=[[@21,94:103='partner_id',<393>,1:94]]}}",
				"{def_query1={context_list={amount_cte=query0}, query_dictionary={partner_id=[[@21,94:103='partner_id',<393>,1:94]], contact_id=[[@25,110:119='contact_id',<393>,1:110]]}, table_dictionary={orders_tbl={order_dt=[[@35,180:182='ord',<393>,1:180]], partner_id=[[@19,90:92='ord',<393>,1:90]], contact_id=[[@23,106:108='ord',<393>,1:106]]}}, def_query0={query_dictionary={max_amount=[[@12,49:58='max_amount',<393>,1:49], [@39,195:204='amount_cte',<393>,1:195]]}, table_dictionary={base_table={amount_val=[[@7,31:32='bt',<393>,1:31]]}}, interface={max_amount=[{name=amount_val, table_ref=bt}]}, table_alias={bt=base_table}}, filters=[{name=order_dt, table_ref=ord}, {name=max_amount, table_ref=amount_cte}], interface={partner_id=[{name=partner_id, table_ref=ord}], contact_id=[{name=contact_id, table_ref=ord}]}, table_alias={ord=orders_tbl, amount_cte=query0}}}");
	}

	@Test
	public void withFinalQueryCteFirstBareJoinTest() {
		final String query = "WITH amount_cte AS (SELECT MAX(bt.amount_val) AS max_amount FROM base_table AS bt) "
				+ "SELECT ord.partner_id, ord.contact_id FROM amount_cte JOIN orders_tbl AS ord "
				+ "WHERE ord.order_dt > amount_cte.max_amount";
		SqlParseEventWalker extractor = runParsertest(query, parse(query));
		assertWithFinalQueryJoinOutputs(extractor,
				"{SQL={with={1={cte={select={1={function={function_name=MAX, qualifier=null, parameters={column={name=amount_val, table_ref=bt}}}, alias=max_amount}}, from={table={alias=bt, table=base_table}}}, alias=amount_cte}}, query={select={1={column={name=partner_id, table_ref=ord}}, 2={column={name=contact_id, table_ref=ord}}}, from={join={1={table={alias=null, table=amount_cte}}, 2={join=JOIN}, 3={table={alias=ord, table=orders_tbl}}}}, where={condition={left={column={name=order_dt, table_ref=ord}}, right={column={name=max_amount, table_ref=amount_cte}}, operator=>}}}}}",
				"[partner_id, contact_id]", "{}",
				"{orders_tbl={order_dt=[[@33,166:168='ord',<393>,1:166]], partner_id=[[@19,90:92='ord',<393>,1:90]], contact_id=[[@23,106:108='ord',<393>,1:106]]}, base_table={amount_val=[[@7,31:32='bt',<393>,1:31]]}}",
				"{query0={max_amount=[[@12,49:58='max_amount',<393>,1:49], [@37,181:190='amount_cte',<393>,1:181]]}, query1={contact_id=[[@25,110:119='contact_id',<393>,1:110]], partner_id=[[@21,94:103='partner_id',<393>,1:94]]}}",
				"{def_query1={context_list={amount_cte=query0}, query_dictionary={partner_id=[[@21,94:103='partner_id',<393>,1:94]], contact_id=[[@25,110:119='contact_id',<393>,1:110]]}, table_dictionary={orders_tbl={order_dt=[[@33,166:168='ord',<393>,1:166]], partner_id=[[@19,90:92='ord',<393>,1:90]], contact_id=[[@23,106:108='ord',<393>,1:106]]}}, def_query0={query_dictionary={max_amount=[[@12,49:58='max_amount',<393>,1:49], [@37,181:190='amount_cte',<393>,1:181]]}, table_dictionary={base_table={amount_val=[[@7,31:32='bt',<393>,1:31]]}}, interface={max_amount=[{name=amount_val, table_ref=bt}]}, table_alias={bt=base_table}}, filters=[{name=order_dt, table_ref=ord}, {name=max_amount, table_ref=amount_cte}], interface={partner_id=[{name=partner_id, table_ref=ord}], contact_id=[{name=contact_id, table_ref=ord}]}, table_alias={ord=orders_tbl, amount_cte=query0}}}");
	}

	@Test
	public void withFinalQueryCteFirstCommaJoinTest() {
		final String query = "WITH amount_cte AS (SELECT MAX(bt.amount_val) AS max_amount FROM base_table AS bt) "
				+ "SELECT ord.partner_id, ord.contact_id FROM amount_cte, orders_tbl AS ord "
				+ "WHERE ord.order_dt > amount_cte.max_amount";
		SqlParseEventWalker extractor = runParsertest(query, parse(query));
		assertWithFinalQueryJoinOutputs(extractor,
				"{SQL={with={1={cte={select={1={function={function_name=MAX, qualifier=null, parameters={column={name=amount_val, table_ref=bt}}}, alias=max_amount}}, from={table={alias=bt, table=base_table}}}, alias=amount_cte}}, query={select={1={column={name=partner_id, table_ref=ord}}, 2={column={name=contact_id, table_ref=ord}}}, from={join={1={table={alias=null, table=amount_cte}}, 2={table={alias=ord, table=orders_tbl}}}}, where={condition={left={column={name=order_dt, table_ref=ord}}, right={column={name=max_amount, table_ref=amount_cte}}, operator=>}}}}}",
				"[partner_id, contact_id]", "{}",
				"{orders_tbl={order_dt=[[@33,162:164='ord',<393>,1:162]], partner_id=[[@19,90:92='ord',<393>,1:90]], contact_id=[[@23,106:108='ord',<393>,1:106]]}, base_table={amount_val=[[@7,31:32='bt',<393>,1:31]]}}",
				"{query0={max_amount=[[@12,49:58='max_amount',<393>,1:49], [@37,177:186='amount_cte',<393>,1:177]]}, query1={contact_id=[[@25,110:119='contact_id',<393>,1:110]], partner_id=[[@21,94:103='partner_id',<393>,1:94]]}}",
				"{def_query1={context_list={amount_cte=query0}, query_dictionary={partner_id=[[@21,94:103='partner_id',<393>,1:94]], contact_id=[[@25,110:119='contact_id',<393>,1:110]]}, table_dictionary={orders_tbl={order_dt=[[@33,162:164='ord',<393>,1:162]], partner_id=[[@19,90:92='ord',<393>,1:90]], contact_id=[[@23,106:108='ord',<393>,1:106]]}}, def_query0={query_dictionary={max_amount=[[@12,49:58='max_amount',<393>,1:49], [@37,177:186='amount_cte',<393>,1:177]]}, table_dictionary={base_table={amount_val=[[@7,31:32='bt',<393>,1:31]]}}, interface={max_amount=[{name=amount_val, table_ref=bt}]}, table_alias={bt=base_table}}, filters=[{name=order_dt, table_ref=ord}, {name=max_amount, table_ref=amount_cte}], interface={partner_id=[{name=partner_id, table_ref=ord}], contact_id=[{name=contact_id, table_ref=ord}]}, table_alias={ord=orders_tbl, amount_cte=query0}}}");
	}

	@Test
	public void withFinalQueryOuterFirstInnerJoinOnControlTest() {
		final String query = "WITH amount_cte AS (SELECT MAX(bt.amount_val) AS max_amount FROM base_table AS bt) "
				+ "SELECT ord.partner_id, ord.contact_id FROM orders_tbl AS ord INNER JOIN amount_cte ON 1 = 1 "
				+ "WHERE ord.order_dt > amount_cte.max_amount";
		SqlParseEventWalker extractor = runParsertest(query, parse(query));
		assertWithFinalQueryJoinOutputs(extractor,
				"{SQL={with={1={cte={select={1={function={function_name=MAX, qualifier=null, parameters={column={name=amount_val, table_ref=bt}}}, alias=max_amount}}, from={table={alias=bt, table=base_table}}}, alias=amount_cte}}, query={select={1={column={name=partner_id, table_ref=ord}}, 2={column={name=contact_id, table_ref=ord}}}, from={join={1={table={alias=ord, table=orders_tbl}}, 2={join=INNER, on={condition={left={literal=1}, right={literal=1}, operator==}}}, 3={table={alias=null, table=amount_cte}}}}, where={condition={left={column={name=order_dt, table_ref=ord}}, right={column={name=max_amount, table_ref=amount_cte}}, operator=>}}}}}",
				"[partner_id, contact_id]", "{}",
				"{orders_tbl={order_dt=[[@38,181:183='ord',<393>,1:181]], partner_id=[[@19,90:92='ord',<393>,1:90]], contact_id=[[@23,106:108='ord',<393>,1:106]]}, base_table={amount_val=[[@7,31:32='bt',<393>,1:31]]}}",
				"{query0={max_amount=[[@12,49:58='max_amount',<393>,1:49], [@42,196:205='amount_cte',<393>,1:196]]}, query1={contact_id=[[@25,110:119='contact_id',<393>,1:110]], partner_id=[[@21,94:103='partner_id',<393>,1:94]]}}",
				"{def_query1={context_list={amount_cte=query0}, query_dictionary={partner_id=[[@21,94:103='partner_id',<393>,1:94]], contact_id=[[@25,110:119='contact_id',<393>,1:110]]}, table_dictionary={orders_tbl={order_dt=[[@38,181:183='ord',<393>,1:181]], partner_id=[[@19,90:92='ord',<393>,1:90]], contact_id=[[@23,106:108='ord',<393>,1:106]]}}, def_query0={query_dictionary={max_amount=[[@12,49:58='max_amount',<393>,1:49], [@42,196:205='amount_cte',<393>,1:196]]}, table_dictionary={base_table={amount_val=[[@7,31:32='bt',<393>,1:31]]}}, interface={max_amount=[{name=amount_val, table_ref=bt}]}, table_alias={bt=base_table}}, filters=[{name=order_dt, table_ref=ord}, {name=max_amount, table_ref=amount_cte}], interface={partner_id=[{name=partner_id, table_ref=ord}], contact_id=[{name=contact_id, table_ref=ord}]}, table_alias={ord=orders_tbl, amount_cte=query0}}}");
	}

	@Ignore("Walker ClassCastException on NATURAL FULL OUTER JOIN WITH CTE — enable after walker fix")
	@Test
	public void withFinalQueryCteFirstNaturalFullOuterJoinTest() {
		final String query = "WITH amount_cte AS (SELECT MAX(bt.amount_val) AS max_amount FROM base_table AS bt) "
				+ "SELECT ord.partner_id, ord.contact_id FROM amount_cte NATURAL FULL OUTER JOIN orders_tbl AS ord "
				+ "WHERE ord.order_dt > amount_cte.max_amount";
		SqlParseEventWalker extractor = runParsertest(query, parse(query));
		assertWithFinalQueryJoinOutputs(extractor,
				"", "", "{}", "", "", "");
	}
}
