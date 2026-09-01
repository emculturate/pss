package sql.walker;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Phase 2.7 subquery variant — joined row source is
 * {@code (SELECT … FROM orders_tbl) AS orders_tbl} instead of physical {@code orders_tbl}.
 * <p>
 * Compare with {@link SqlEventWalkerWithConditionlessJoinFinalizerTests}.
 */
public class SqlEventWalkerWithConditionlessJoinFinalizerSubqueryTests extends AbstractSqlParseEventWalkerTest {

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
	public void withFinalQueryCteFirstCrossJoinSubqueryOrdersTest() {
		final String query = "WITH amount_cte AS (SELECT MAX(bt.amount_val) AS max_amount FROM base_table AS bt) "
				+ "SELECT orders_tbl.partner_id, orders_tbl.contact_id FROM amount_cte CROSS JOIN "
				+ "(SELECT partner_id, contact_id, order_dt FROM orders_tbl) AS orders_tbl "
				+ "WHERE orders_tbl.order_dt > amount_cte.max_amount";
		SqlParseEventWalker extractor = runParsertest(query, parse(query));
		assertWithFinalQueryJoinOutputs(extractor,
				"{SQL={with={1={cte={select={1={function={function_name=MAX, qualifier=null, parameters={column={name=amount_val, table_ref=bt}}}, alias=max_amount}}, from={table={alias=bt, table=base_table}}}, alias=amount_cte}}, query={select={1={column={name=partner_id, table_ref=orders_tbl}}, 2={column={name=contact_id, table_ref=orders_tbl}}}, from={join={1={table={alias=null, table=amount_cte}}, 2={join=CROSSJOIN}, 3={table={alias=orders_tbl, query={select={1={column={name=partner_id, table_ref=null}}, 2={column={name=contact_id, table_ref=null}}, 3={column={name=order_dt, table_ref=null}}}, from={table={alias=null, table=orders_tbl}}}}}}}, where={condition={left={column={name=order_dt, table_ref=orders_tbl}}, right={column={name=max_amount, table_ref=amount_cte}}, operator=>}}}}}",
				"[partner_id, contact_id]", "{}",
				"{orders_tbl={order_dt=[[@36,194:201='order_dt',<393>,1:194]], partner_id=[[@32,170:179='partner_id',<393>,1:170]], contact_id=[[@34,182:191='contact_id',<393>,1:182]]}, base_table={amount_val=[[@7,31:32='bt',<393>,1:31]]}}",
				"{query0={max_amount=[[@12,49:58='max_amount',<393>,1:49], [@47,262:271='amount_cte',<393>,1:262]]}, query1={order_dt=[[@36,194:201='order_dt',<393>,1:194], [@43,240:249='orders_tbl',<393>,1:240]], partner_id=[[@32,170:179='partner_id',<393>,1:170], [@19,90:99='orders_tbl',<393>,1:90]], contact_id=[[@34,182:191='contact_id',<393>,1:182], [@23,113:122='orders_tbl',<393>,1:113]]}, query2={contact_id=[[@25,124:133='contact_id',<393>,1:124]], partner_id=[[@21,101:110='partner_id',<393>,1:101]]}}",
				"{def_query2={context_list={amount_cte=query0}, query_dictionary={partner_id=[[@21,101:110='partner_id',<393>,1:101]], contact_id=[[@25,124:133='contact_id',<393>,1:124]]}, def_query1={context_list={amount_cte=query0}, query_dictionary={order_dt=[[@36,194:201='order_dt',<393>,1:194], [@43,240:249='orders_tbl',<393>,1:240]], partner_id=[[@32,170:179='partner_id',<393>,1:170], [@19,90:99='orders_tbl',<393>,1:90]], contact_id=[[@34,182:191='contact_id',<393>,1:182], [@23,113:122='orders_tbl',<393>,1:113]]}, table_dictionary={orders_tbl={order_dt=[[@36,194:201='order_dt',<393>,1:194]], partner_id=[[@32,170:179='partner_id',<393>,1:170]], contact_id=[[@34,182:191='contact_id',<393>,1:182]]}}, interface={order_dt=[{name=order_dt, table_ref=orders_tbl}], partner_id=[{name=partner_id, table_ref=orders_tbl}], contact_id=[{name=contact_id, table_ref=orders_tbl}]}, table_alias={amount_cte=query0}}, def_query0={query_dictionary={max_amount=[[@12,49:58='max_amount',<393>,1:49], [@47,262:271='amount_cte',<393>,1:262]]}, table_dictionary={base_table={amount_val=[[@7,31:32='bt',<393>,1:31]]}}, interface={max_amount=[{name=amount_val, table_ref=bt}]}, table_alias={bt=base_table}}, filters=[{name=order_dt, table_ref=orders_tbl}, {name=max_amount, table_ref=amount_cte}], interface={partner_id=[{name=partner_id, table_ref=orders_tbl}], contact_id=[{name=contact_id, table_ref=orders_tbl}]}, table_alias={orders_tbl=query1, amount_cte=query0}}}");
	}

	@Test
	public void withFinalQueryOuterFirstCrossJoinCteSubqueryOrdersTest() {
		final String query = "WITH amount_cte AS (SELECT MAX(bt.amount_val) AS max_amount FROM base_table AS bt) "
				+ "SELECT orders_tbl.partner_id, orders_tbl.contact_id FROM "
				+ "(SELECT partner_id, contact_id, order_dt FROM orders_tbl) AS orders_tbl CROSS JOIN amount_cte "
				+ "WHERE orders_tbl.order_dt > amount_cte.max_amount";
		SqlParseEventWalker extractor = runParsertest(query, parse(query));
		assertWithFinalQueryJoinOutputs(extractor,
				"{SQL={with={1={cte={select={1={function={function_name=MAX, qualifier=null, parameters={column={name=amount_val, table_ref=bt}}}, alias=max_amount}}, from={table={alias=bt, table=base_table}}}, alias=amount_cte}}, query={select={1={column={name=partner_id, table_ref=orders_tbl}}, 2={column={name=contact_id, table_ref=orders_tbl}}}, from={join={1={table={alias=orders_tbl, query={select={1={column={name=partner_id, table_ref=null}}, 2={column={name=contact_id, table_ref=null}}, 3={column={name=order_dt, table_ref=null}}}, from={table={alias=null, table=orders_tbl}}}}}, 2={join=CROSSJOIN}, 3={table={alias=null, table=amount_cte}}}}, where={condition={left={column={name=order_dt, table_ref=orders_tbl}}, right={column={name=max_amount, table_ref=amount_cte}}, operator=>}}}}}",
				"[partner_id, contact_id]", "{}",
				"{orders_tbl={order_dt=[[@33,172:179='order_dt',<393>,1:172]], partner_id=[[@29,148:157='partner_id',<393>,1:148]], contact_id=[[@31,160:169='contact_id',<393>,1:160]]}, base_table={amount_val=[[@7,31:32='bt',<393>,1:31]]}}",
				"{query0={max_amount=[[@12,49:58='max_amount',<393>,1:49], [@47,262:271='amount_cte',<393>,1:262]]}, query1={order_dt=[[@33,172:179='order_dt',<393>,1:172], [@43,240:249='orders_tbl',<393>,1:240]], partner_id=[[@29,148:157='partner_id',<393>,1:148], [@19,90:99='orders_tbl',<393>,1:90]], contact_id=[[@31,160:169='contact_id',<393>,1:160], [@23,113:122='orders_tbl',<393>,1:113]]}, query2={contact_id=[[@25,124:133='contact_id',<393>,1:124]], partner_id=[[@21,101:110='partner_id',<393>,1:101]]}}",
				"{def_query2={context_list={amount_cte=query0}, query_dictionary={partner_id=[[@21,101:110='partner_id',<393>,1:101]], contact_id=[[@25,124:133='contact_id',<393>,1:124]]}, def_query1={context_list={amount_cte=query0}, query_dictionary={order_dt=[[@33,172:179='order_dt',<393>,1:172], [@43,240:249='orders_tbl',<393>,1:240]], partner_id=[[@29,148:157='partner_id',<393>,1:148], [@19,90:99='orders_tbl',<393>,1:90]], contact_id=[[@31,160:169='contact_id',<393>,1:160], [@23,113:122='orders_tbl',<393>,1:113]]}, table_dictionary={orders_tbl={order_dt=[[@33,172:179='order_dt',<393>,1:172]], partner_id=[[@29,148:157='partner_id',<393>,1:148]], contact_id=[[@31,160:169='contact_id',<393>,1:160]]}}, interface={order_dt=[{name=order_dt, table_ref=orders_tbl}], partner_id=[{name=partner_id, table_ref=orders_tbl}], contact_id=[{name=contact_id, table_ref=orders_tbl}]}, table_alias={amount_cte=query0}}, def_query0={query_dictionary={max_amount=[[@12,49:58='max_amount',<393>,1:49], [@47,262:271='amount_cte',<393>,1:262]]}, table_dictionary={base_table={amount_val=[[@7,31:32='bt',<393>,1:31]]}}, interface={max_amount=[{name=amount_val, table_ref=bt}]}, table_alias={bt=base_table}}, filters=[{name=order_dt, table_ref=orders_tbl}, {name=max_amount, table_ref=amount_cte}], interface={partner_id=[{name=partner_id, table_ref=orders_tbl}], contact_id=[{name=contact_id, table_ref=orders_tbl}]}, table_alias={orders_tbl=query1, amount_cte=query0}}}");
	}

	@Test
	public void withFinalQueryCteFirstNaturalJoinSubqueryOrdersTest() {
		final String query = "WITH amount_cte AS (SELECT MAX(bt.amount_val) AS max_amount FROM base_table AS bt) "
				+ "SELECT orders_tbl.partner_id, orders_tbl.contact_id FROM amount_cte NATURAL JOIN "
				+ "(SELECT partner_id, contact_id, order_dt FROM orders_tbl) AS orders_tbl "
				+ "WHERE orders_tbl.order_dt > amount_cte.max_amount";
		SqlParseEventWalker extractor = runParsertest(query, parse(query));
		assertWithFinalQueryJoinOutputs(extractor,
				"{SQL={with={1={cte={select={1={function={function_name=MAX, qualifier=null, parameters={column={name=amount_val, table_ref=bt}}}, alias=max_amount}}, from={table={alias=bt, table=base_table}}}, alias=amount_cte}}, query={select={1={column={name=partner_id, table_ref=orders_tbl}}, 2={column={name=contact_id, table_ref=orders_tbl}}}, from={join={1={table={alias=null, table=amount_cte}}, 2={join=NATURALJOIN}, 3={table={alias=orders_tbl, query={select={1={column={name=partner_id, table_ref=null}}, 2={column={name=contact_id, table_ref=null}}, 3={column={name=order_dt, table_ref=null}}}, from={table={alias=null, table=orders_tbl}}}}}}}, where={condition={left={column={name=order_dt, table_ref=orders_tbl}}, right={column={name=max_amount, table_ref=amount_cte}}, operator=>}}}}}",
				"[partner_id, contact_id]", "{}",
				"{orders_tbl={order_dt=[[@36,196:203='order_dt',<393>,1:196]], partner_id=[[@32,172:181='partner_id',<393>,1:172]], contact_id=[[@34,184:193='contact_id',<393>,1:184]]}, base_table={amount_val=[[@7,31:32='bt',<393>,1:31]]}}",
				"{query0={max_amount=[[@12,49:58='max_amount',<393>,1:49], [@47,264:273='amount_cte',<393>,1:264]]}, query1={order_dt=[[@36,196:203='order_dt',<393>,1:196], [@43,242:251='orders_tbl',<393>,1:242]], partner_id=[[@32,172:181='partner_id',<393>,1:172], [@19,90:99='orders_tbl',<393>,1:90]], contact_id=[[@34,184:193='contact_id',<393>,1:184], [@23,113:122='orders_tbl',<393>,1:113]]}, query2={contact_id=[[@25,124:133='contact_id',<393>,1:124]], partner_id=[[@21,101:110='partner_id',<393>,1:101]]}}",
				"{def_query2={context_list={amount_cte=query0}, query_dictionary={partner_id=[[@21,101:110='partner_id',<393>,1:101]], contact_id=[[@25,124:133='contact_id',<393>,1:124]]}, def_query1={context_list={amount_cte=query0}, query_dictionary={order_dt=[[@36,196:203='order_dt',<393>,1:196], [@43,242:251='orders_tbl',<393>,1:242]], partner_id=[[@32,172:181='partner_id',<393>,1:172], [@19,90:99='orders_tbl',<393>,1:90]], contact_id=[[@34,184:193='contact_id',<393>,1:184], [@23,113:122='orders_tbl',<393>,1:113]]}, table_dictionary={orders_tbl={order_dt=[[@36,196:203='order_dt',<393>,1:196]], partner_id=[[@32,172:181='partner_id',<393>,1:172]], contact_id=[[@34,184:193='contact_id',<393>,1:184]]}}, interface={order_dt=[{name=order_dt, table_ref=orders_tbl}], partner_id=[{name=partner_id, table_ref=orders_tbl}], contact_id=[{name=contact_id, table_ref=orders_tbl}]}, table_alias={amount_cte=query0}}, def_query0={query_dictionary={max_amount=[[@12,49:58='max_amount',<393>,1:49], [@47,264:273='amount_cte',<393>,1:264]]}, table_dictionary={base_table={amount_val=[[@7,31:32='bt',<393>,1:31]]}}, interface={max_amount=[{name=amount_val, table_ref=bt}]}, table_alias={bt=base_table}}, filters=[{name=order_dt, table_ref=orders_tbl}, {name=max_amount, table_ref=amount_cte}], interface={partner_id=[{name=partner_id, table_ref=orders_tbl}], contact_id=[{name=contact_id, table_ref=orders_tbl}]}, table_alias={orders_tbl=query1, amount_cte=query0}}}");
	}

	@Test
	public void withFinalQueryCteFirstNaturalLeftJoinSubqueryOrdersTest() {
		final String query = "WITH amount_cte AS (SELECT MAX(bt.amount_val) AS max_amount FROM base_table AS bt) "
				+ "SELECT orders_tbl.partner_id, orders_tbl.contact_id FROM amount_cte NATURAL LEFT JOIN "
				+ "(SELECT partner_id, contact_id, order_dt FROM orders_tbl) AS orders_tbl "
				+ "WHERE orders_tbl.order_dt > amount_cte.max_amount";
		SqlParseEventWalker extractor = runParsertest(query, parse(query));
		assertWithFinalQueryJoinOutputs(extractor,
				"{SQL={with={1={cte={select={1={function={function_name=MAX, qualifier=null, parameters={column={name=amount_val, table_ref=bt}}}, alias=max_amount}}, from={table={alias=bt, table=base_table}}}, alias=amount_cte}}, query={select={1={column={name=partner_id, table_ref=orders_tbl}}, 2={column={name=contact_id, table_ref=orders_tbl}}}, from={join={1={table={alias=null, table=amount_cte}}, 2={join=NATURALJOIN}, 3={table={alias=orders_tbl, query={select={1={column={name=partner_id, table_ref=null}}, 2={column={name=contact_id, table_ref=null}}, 3={column={name=order_dt, table_ref=null}}}, from={table={alias=null, table=orders_tbl}}}}}}}, where={condition={left={column={name=order_dt, table_ref=orders_tbl}}, right={column={name=max_amount, table_ref=amount_cte}}, operator=>}}}}}",
				"[partner_id, contact_id]", "{}",
				"{orders_tbl={order_dt=[[@37,201:208='order_dt',<393>,1:201]], partner_id=[[@33,177:186='partner_id',<393>,1:177]], contact_id=[[@35,189:198='contact_id',<393>,1:189]]}, base_table={amount_val=[[@7,31:32='bt',<393>,1:31]]}}",
				"{query0={max_amount=[[@12,49:58='max_amount',<393>,1:49], [@48,269:278='amount_cte',<393>,1:269]]}, query1={order_dt=[[@37,201:208='order_dt',<393>,1:201], [@44,247:256='orders_tbl',<393>,1:247]], partner_id=[[@33,177:186='partner_id',<393>,1:177], [@19,90:99='orders_tbl',<393>,1:90]], contact_id=[[@35,189:198='contact_id',<393>,1:189], [@23,113:122='orders_tbl',<393>,1:113]]}, query2={contact_id=[[@25,124:133='contact_id',<393>,1:124]], partner_id=[[@21,101:110='partner_id',<393>,1:101]]}}",
				"{def_query2={context_list={amount_cte=query0}, query_dictionary={partner_id=[[@21,101:110='partner_id',<393>,1:101]], contact_id=[[@25,124:133='contact_id',<393>,1:124]]}, def_query1={context_list={amount_cte=query0}, query_dictionary={order_dt=[[@37,201:208='order_dt',<393>,1:201], [@44,247:256='orders_tbl',<393>,1:247]], partner_id=[[@33,177:186='partner_id',<393>,1:177], [@19,90:99='orders_tbl',<393>,1:90]], contact_id=[[@35,189:198='contact_id',<393>,1:189], [@23,113:122='orders_tbl',<393>,1:113]]}, table_dictionary={orders_tbl={order_dt=[[@37,201:208='order_dt',<393>,1:201]], partner_id=[[@33,177:186='partner_id',<393>,1:177]], contact_id=[[@35,189:198='contact_id',<393>,1:189]]}}, interface={order_dt=[{name=order_dt, table_ref=orders_tbl}], partner_id=[{name=partner_id, table_ref=orders_tbl}], contact_id=[{name=contact_id, table_ref=orders_tbl}]}, table_alias={amount_cte=query0}}, def_query0={query_dictionary={max_amount=[[@12,49:58='max_amount',<393>,1:49], [@48,269:278='amount_cte',<393>,1:269]]}, table_dictionary={base_table={amount_val=[[@7,31:32='bt',<393>,1:31]]}}, interface={max_amount=[{name=amount_val, table_ref=bt}]}, table_alias={bt=base_table}}, filters=[{name=order_dt, table_ref=orders_tbl}, {name=max_amount, table_ref=amount_cte}], interface={partner_id=[{name=partner_id, table_ref=orders_tbl}], contact_id=[{name=contact_id, table_ref=orders_tbl}]}, table_alias={orders_tbl=query1, amount_cte=query0}}}");
	}

	@Test
	public void withFinalQueryCteFirstNaturalRightJoinSubqueryOrdersTest() {
		final String query = "WITH amount_cte AS (SELECT MAX(bt.amount_val) AS max_amount FROM base_table AS bt) "
				+ "SELECT orders_tbl.partner_id, orders_tbl.contact_id FROM amount_cte NATURAL RIGHT JOIN "
				+ "(SELECT partner_id, contact_id, order_dt FROM orders_tbl) AS orders_tbl "
				+ "WHERE orders_tbl.order_dt > amount_cte.max_amount";
		SqlParseEventWalker extractor = runParsertest(query, parse(query));
		assertWithFinalQueryJoinOutputs(extractor,
				"{SQL={with={1={cte={select={1={function={function_name=MAX, qualifier=null, parameters={column={name=amount_val, table_ref=bt}}}, alias=max_amount}}, from={table={alias=bt, table=base_table}}}, alias=amount_cte}}, query={select={1={column={name=partner_id, table_ref=orders_tbl}}, 2={column={name=contact_id, table_ref=orders_tbl}}}, from={join={1={table={alias=null, table=amount_cte}}, 2={join=NATURALJOIN}, 3={table={alias=orders_tbl, query={select={1={column={name=partner_id, table_ref=null}}, 2={column={name=contact_id, table_ref=null}}, 3={column={name=order_dt, table_ref=null}}}, from={table={alias=null, table=orders_tbl}}}}}}}, where={condition={left={column={name=order_dt, table_ref=orders_tbl}}, right={column={name=max_amount, table_ref=amount_cte}}, operator=>}}}}}",
				"[partner_id, contact_id]", "{}",
				"{orders_tbl={order_dt=[[@37,202:209='order_dt',<393>,1:202]], partner_id=[[@33,178:187='partner_id',<393>,1:178]], contact_id=[[@35,190:199='contact_id',<393>,1:190]]}, base_table={amount_val=[[@7,31:32='bt',<393>,1:31]]}}",
				"{query0={max_amount=[[@12,49:58='max_amount',<393>,1:49], [@48,270:279='amount_cte',<393>,1:270]]}, query1={order_dt=[[@37,202:209='order_dt',<393>,1:202], [@44,248:257='orders_tbl',<393>,1:248]], partner_id=[[@33,178:187='partner_id',<393>,1:178], [@19,90:99='orders_tbl',<393>,1:90]], contact_id=[[@35,190:199='contact_id',<393>,1:190], [@23,113:122='orders_tbl',<393>,1:113]]}, query2={contact_id=[[@25,124:133='contact_id',<393>,1:124]], partner_id=[[@21,101:110='partner_id',<393>,1:101]]}}",
				"{def_query2={context_list={amount_cte=query0}, query_dictionary={partner_id=[[@21,101:110='partner_id',<393>,1:101]], contact_id=[[@25,124:133='contact_id',<393>,1:124]]}, def_query1={context_list={amount_cte=query0}, query_dictionary={order_dt=[[@37,202:209='order_dt',<393>,1:202], [@44,248:257='orders_tbl',<393>,1:248]], partner_id=[[@33,178:187='partner_id',<393>,1:178], [@19,90:99='orders_tbl',<393>,1:90]], contact_id=[[@35,190:199='contact_id',<393>,1:190], [@23,113:122='orders_tbl',<393>,1:113]]}, table_dictionary={orders_tbl={order_dt=[[@37,202:209='order_dt',<393>,1:202]], partner_id=[[@33,178:187='partner_id',<393>,1:178]], contact_id=[[@35,190:199='contact_id',<393>,1:190]]}}, interface={order_dt=[{name=order_dt, table_ref=orders_tbl}], partner_id=[{name=partner_id, table_ref=orders_tbl}], contact_id=[{name=contact_id, table_ref=orders_tbl}]}, table_alias={amount_cte=query0}}, def_query0={query_dictionary={max_amount=[[@12,49:58='max_amount',<393>,1:49], [@48,270:279='amount_cte',<393>,1:270]]}, table_dictionary={base_table={amount_val=[[@7,31:32='bt',<393>,1:31]]}}, interface={max_amount=[{name=amount_val, table_ref=bt}]}, table_alias={bt=base_table}}, filters=[{name=order_dt, table_ref=orders_tbl}, {name=max_amount, table_ref=amount_cte}], interface={partner_id=[{name=partner_id, table_ref=orders_tbl}], contact_id=[{name=contact_id, table_ref=orders_tbl}]}, table_alias={orders_tbl=query1, amount_cte=query0}}}");
	}

	@Test
	public void withFinalQueryCteFirstBareJoinSubqueryOrdersTest() {
		final String query = "WITH amount_cte AS (SELECT MAX(bt.amount_val) AS max_amount FROM base_table AS bt) "
				+ "SELECT orders_tbl.partner_id, orders_tbl.contact_id FROM amount_cte JOIN "
				+ "(SELECT partner_id, contact_id, order_dt FROM orders_tbl) AS orders_tbl "
				+ "WHERE orders_tbl.order_dt > amount_cte.max_amount";
		SqlParseEventWalker extractor = runParsertest(query, parse(query));
		assertWithFinalQueryJoinOutputs(extractor,
				"{SQL={with={1={cte={select={1={function={function_name=MAX, qualifier=null, parameters={column={name=amount_val, table_ref=bt}}}, alias=max_amount}}, from={table={alias=bt, table=base_table}}}, alias=amount_cte}}, query={select={1={column={name=partner_id, table_ref=orders_tbl}}, 2={column={name=contact_id, table_ref=orders_tbl}}}, from={join={1={table={alias=null, table=amount_cte}}, 2={join=JOIN}, 3={table={alias=orders_tbl, query={select={1={column={name=partner_id, table_ref=null}}, 2={column={name=contact_id, table_ref=null}}, 3={column={name=order_dt, table_ref=null}}}, from={table={alias=null, table=orders_tbl}}}}}}}, where={condition={left={column={name=order_dt, table_ref=orders_tbl}}, right={column={name=max_amount, table_ref=amount_cte}}, operator=>}}}}}",
				"[partner_id, contact_id]", "{}",
				"{orders_tbl={order_dt=[[@35,188:195='order_dt',<393>,1:188]], partner_id=[[@31,164:173='partner_id',<393>,1:164]], contact_id=[[@33,176:185='contact_id',<393>,1:176]]}, base_table={amount_val=[[@7,31:32='bt',<393>,1:31]]}}",
				"{query0={max_amount=[[@12,49:58='max_amount',<393>,1:49], [@46,256:265='amount_cte',<393>,1:256]]}, query1={order_dt=[[@35,188:195='order_dt',<393>,1:188], [@42,234:243='orders_tbl',<393>,1:234]], partner_id=[[@31,164:173='partner_id',<393>,1:164], [@19,90:99='orders_tbl',<393>,1:90]], contact_id=[[@33,176:185='contact_id',<393>,1:176], [@23,113:122='orders_tbl',<393>,1:113]]}, query2={contact_id=[[@25,124:133='contact_id',<393>,1:124]], partner_id=[[@21,101:110='partner_id',<393>,1:101]]}}",
				"{def_query2={context_list={amount_cte=query0}, query_dictionary={partner_id=[[@21,101:110='partner_id',<393>,1:101]], contact_id=[[@25,124:133='contact_id',<393>,1:124]]}, def_query1={context_list={amount_cte=query0}, query_dictionary={order_dt=[[@35,188:195='order_dt',<393>,1:188], [@42,234:243='orders_tbl',<393>,1:234]], partner_id=[[@31,164:173='partner_id',<393>,1:164], [@19,90:99='orders_tbl',<393>,1:90]], contact_id=[[@33,176:185='contact_id',<393>,1:176], [@23,113:122='orders_tbl',<393>,1:113]]}, table_dictionary={orders_tbl={order_dt=[[@35,188:195='order_dt',<393>,1:188]], partner_id=[[@31,164:173='partner_id',<393>,1:164]], contact_id=[[@33,176:185='contact_id',<393>,1:176]]}}, interface={order_dt=[{name=order_dt, table_ref=orders_tbl}], partner_id=[{name=partner_id, table_ref=orders_tbl}], contact_id=[{name=contact_id, table_ref=orders_tbl}]}, table_alias={amount_cte=query0}}, def_query0={query_dictionary={max_amount=[[@12,49:58='max_amount',<393>,1:49], [@46,256:265='amount_cte',<393>,1:256]]}, table_dictionary={base_table={amount_val=[[@7,31:32='bt',<393>,1:31]]}}, interface={max_amount=[{name=amount_val, table_ref=bt}]}, table_alias={bt=base_table}}, filters=[{name=order_dt, table_ref=orders_tbl}, {name=max_amount, table_ref=amount_cte}], interface={partner_id=[{name=partner_id, table_ref=orders_tbl}], contact_id=[{name=contact_id, table_ref=orders_tbl}]}, table_alias={orders_tbl=query1, amount_cte=query0}}}");
	}

	@Test
	public void withFinalQueryCteFirstCommaJoinSubqueryOrdersTest() {
		final String query = "WITH amount_cte AS (SELECT MAX(bt.amount_val) AS max_amount FROM base_table AS bt) "
				+ "SELECT orders_tbl.partner_id, orders_tbl.contact_id FROM amount_cte, "
				+ "(SELECT partner_id, contact_id, order_dt FROM orders_tbl) AS orders_tbl "
				+ "WHERE orders_tbl.order_dt > amount_cte.max_amount";
		SqlParseEventWalker extractor = runParsertest(query, parse(query));
		assertWithFinalQueryJoinOutputs(extractor,
				"{SQL={with={1={cte={select={1={function={function_name=MAX, qualifier=null, parameters={column={name=amount_val, table_ref=bt}}}, alias=max_amount}}, from={table={alias=bt, table=base_table}}}, alias=amount_cte}}, query={select={1={column={name=partner_id, table_ref=orders_tbl}}, 2={column={name=contact_id, table_ref=orders_tbl}}}, from={join={1={table={alias=null, table=amount_cte}}, 2={table={alias=orders_tbl, query={select={1={column={name=partner_id, table_ref=null}}, 2={column={name=contact_id, table_ref=null}}, 3={column={name=order_dt, table_ref=null}}}, from={table={alias=null, table=orders_tbl}}}}}}}, where={condition={left={column={name=order_dt, table_ref=orders_tbl}}, right={column={name=max_amount, table_ref=amount_cte}}, operator=>}}}}}",
				"[partner_id, contact_id]", "{}",
				"{orders_tbl={order_dt=[[@35,184:191='order_dt',<393>,1:184]], partner_id=[[@31,160:169='partner_id',<393>,1:160]], contact_id=[[@33,172:181='contact_id',<393>,1:172]]}, base_table={amount_val=[[@7,31:32='bt',<393>,1:31]]}}",
				"{query0={max_amount=[[@12,49:58='max_amount',<393>,1:49], [@46,252:261='amount_cte',<393>,1:252]]}, query1={order_dt=[[@35,184:191='order_dt',<393>,1:184], [@42,230:239='orders_tbl',<393>,1:230]], partner_id=[[@31,160:169='partner_id',<393>,1:160], [@19,90:99='orders_tbl',<393>,1:90]], contact_id=[[@33,172:181='contact_id',<393>,1:172], [@23,113:122='orders_tbl',<393>,1:113]]}, query2={contact_id=[[@25,124:133='contact_id',<393>,1:124]], partner_id=[[@21,101:110='partner_id',<393>,1:101]]}}",
				"{def_query2={context_list={amount_cte=query0}, query_dictionary={partner_id=[[@21,101:110='partner_id',<393>,1:101]], contact_id=[[@25,124:133='contact_id',<393>,1:124]]}, def_query1={context_list={amount_cte=query0}, query_dictionary={order_dt=[[@35,184:191='order_dt',<393>,1:184], [@42,230:239='orders_tbl',<393>,1:230]], partner_id=[[@31,160:169='partner_id',<393>,1:160], [@19,90:99='orders_tbl',<393>,1:90]], contact_id=[[@33,172:181='contact_id',<393>,1:172], [@23,113:122='orders_tbl',<393>,1:113]]}, table_dictionary={orders_tbl={order_dt=[[@35,184:191='order_dt',<393>,1:184]], partner_id=[[@31,160:169='partner_id',<393>,1:160]], contact_id=[[@33,172:181='contact_id',<393>,1:172]]}}, interface={order_dt=[{name=order_dt, table_ref=orders_tbl}], partner_id=[{name=partner_id, table_ref=orders_tbl}], contact_id=[{name=contact_id, table_ref=orders_tbl}]}, table_alias={amount_cte=query0}}, def_query0={query_dictionary={max_amount=[[@12,49:58='max_amount',<393>,1:49], [@46,252:261='amount_cte',<393>,1:252]]}, table_dictionary={base_table={amount_val=[[@7,31:32='bt',<393>,1:31]]}}, interface={max_amount=[{name=amount_val, table_ref=bt}]}, table_alias={bt=base_table}}, filters=[{name=order_dt, table_ref=orders_tbl}, {name=max_amount, table_ref=amount_cte}], interface={partner_id=[{name=partner_id, table_ref=orders_tbl}], contact_id=[{name=contact_id, table_ref=orders_tbl}]}, table_alias={orders_tbl=query1, amount_cte=query0}}}");
	}

	@Test
	public void withFinalQueryOuterFirstInnerJoinOnSubqueryOrdersControlTest() {
		final String query = "WITH amount_cte AS (SELECT MAX(bt.amount_val) AS max_amount FROM base_table AS bt) "
				+ "SELECT orders_tbl.partner_id, orders_tbl.contact_id FROM "
				+ "(SELECT partner_id, contact_id, order_dt FROM orders_tbl) AS orders_tbl INNER JOIN amount_cte ON 1 = 1 "
				+ "WHERE orders_tbl.order_dt > amount_cte.max_amount";
		SqlParseEventWalker extractor = runParsertest(query, parse(query));
		assertWithFinalQueryJoinOutputs(extractor,
				"{SQL={with={1={cte={select={1={function={function_name=MAX, qualifier=null, parameters={column={name=amount_val, table_ref=bt}}}, alias=max_amount}}, from={table={alias=bt, table=base_table}}}, alias=amount_cte}}, query={select={1={column={name=partner_id, table_ref=orders_tbl}}, 2={column={name=contact_id, table_ref=orders_tbl}}}, from={join={1={table={alias=orders_tbl, query={select={1={column={name=partner_id, table_ref=null}}, 2={column={name=contact_id, table_ref=null}}, 3={column={name=order_dt, table_ref=null}}}, from={table={alias=null, table=orders_tbl}}}}}, 2={join=INNER, on={condition={left={literal=1}, right={literal=1}, operator==}}}, 3={table={alias=null, table=amount_cte}}}}, where={condition={left={column={name=order_dt, table_ref=orders_tbl}}, right={column={name=max_amount, table_ref=amount_cte}}, operator=>}}}}}",
				"[partner_id, contact_id]", "{}",
				"{orders_tbl={order_dt=[[@33,172:179='order_dt',<393>,1:172]], partner_id=[[@29,148:157='partner_id',<393>,1:148]], contact_id=[[@31,160:169='contact_id',<393>,1:160]]}, base_table={amount_val=[[@7,31:32='bt',<393>,1:31]]}}",
				"{query0={max_amount=[[@12,49:58='max_amount',<393>,1:49], [@51,271:280='amount_cte',<393>,1:271]]}, query1={order_dt=[[@33,172:179='order_dt',<393>,1:172], [@47,249:258='orders_tbl',<393>,1:249]], partner_id=[[@29,148:157='partner_id',<393>,1:148], [@19,90:99='orders_tbl',<393>,1:90]], contact_id=[[@31,160:169='contact_id',<393>,1:160], [@23,113:122='orders_tbl',<393>,1:113]]}, query2={contact_id=[[@25,124:133='contact_id',<393>,1:124]], partner_id=[[@21,101:110='partner_id',<393>,1:101]]}}",
				"{def_query2={context_list={amount_cte=query0}, query_dictionary={partner_id=[[@21,101:110='partner_id',<393>,1:101]], contact_id=[[@25,124:133='contact_id',<393>,1:124]]}, def_query1={context_list={amount_cte=query0}, query_dictionary={order_dt=[[@33,172:179='order_dt',<393>,1:172], [@47,249:258='orders_tbl',<393>,1:249]], partner_id=[[@29,148:157='partner_id',<393>,1:148], [@19,90:99='orders_tbl',<393>,1:90]], contact_id=[[@31,160:169='contact_id',<393>,1:160], [@23,113:122='orders_tbl',<393>,1:113]]}, table_dictionary={orders_tbl={order_dt=[[@33,172:179='order_dt',<393>,1:172]], partner_id=[[@29,148:157='partner_id',<393>,1:148]], contact_id=[[@31,160:169='contact_id',<393>,1:160]]}}, interface={order_dt=[{name=order_dt, table_ref=orders_tbl}], partner_id=[{name=partner_id, table_ref=orders_tbl}], contact_id=[{name=contact_id, table_ref=orders_tbl}]}, table_alias={amount_cte=query0}}, def_query0={query_dictionary={max_amount=[[@12,49:58='max_amount',<393>,1:49], [@51,271:280='amount_cte',<393>,1:271]]}, table_dictionary={base_table={amount_val=[[@7,31:32='bt',<393>,1:31]]}}, interface={max_amount=[{name=amount_val, table_ref=bt}]}, table_alias={bt=base_table}}, filters=[{name=order_dt, table_ref=orders_tbl}, {name=max_amount, table_ref=amount_cte}], interface={partner_id=[{name=partner_id, table_ref=orders_tbl}], contact_id=[{name=contact_id, table_ref=orders_tbl}]}, table_alias={orders_tbl=query1, amount_cte=query0}}}");
	}

	@Ignore("Walker ClassCastException on NATURAL FULL OUTER JOIN WITH CTE + subquery — enable after walker fix")
	@Test
	public void withFinalQueryCteFirstNaturalFullOuterJoinSubqueryOrdersTest() {
		final String query = "WITH amount_cte AS (SELECT MAX(bt.amount_val) AS max_amount FROM base_table AS bt) "
				+ "SELECT orders_tbl.partner_id, orders_tbl.contact_id FROM amount_cte NATURAL FULL OUTER JOIN "
				+ "(SELECT partner_id, contact_id, order_dt FROM orders_tbl) AS orders_tbl "
				+ "WHERE orders_tbl.order_dt > amount_cte.max_amount";
		SqlParseEventWalker extractor = runParsertest(query, parse(query));
		assertWithFinalQueryJoinOutputs(extractor, "", "", "{}", "", "", "");
	}
}
