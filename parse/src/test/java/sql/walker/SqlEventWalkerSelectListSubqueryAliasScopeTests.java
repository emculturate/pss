package sql.walker;

import static mumble.SQLParserEndPoints.SQLPARSER_QUERY_TREE_KEY;
import static mumble.SQLParserEndPoints.SQLPARSER_SQL_TREE_KEY;

import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import access.Snippet;
import access.SqlParserAccess;

/**
 * Workplan 5.1.4 Entry 13: subqueries nested inside SELECT-list expressions keep their own alias
 * scope. Inner-alias refs must not be validated against the outer query block, and operand scalar
 * subqueries carry the same {@code lookup} subtree as 5.0.0-3.
 * Brief: parse/documents/select-list-subquery-alias-scope-defect.md
 */
public class SqlEventWalkerSelectListSubqueryAliasScopeTests {

	private static final String[] SELECT_LIST_EXPRESSION_SUBQUERIES = {
		"select (select sum(pp.x) as a from t as pp) / (select sum(pq.x) as b from t as pq) as r from t as pr",
		"select ((select sum(pp.x) as a from t as pp) / (select sum(pq.x) as b from t as pq)) as r from t as pr",
		"select case when (select sum(pp.x) as a from t as pp) > 0 then 1 end as r from t as pr",
		"select (select max(pp.x) from t as pp) + 0 as r from t as pr",
		"select coalesce((select sum(pp.x) as a from t as pp), 0) as r from t as pr",
		"select d.id in (select x from t as g where g.y = 1) as r from t as d",
		"select case when d.id in (select x from t as g where g.y = 1) then 1 else 0 end as r from t as d",
		"select case when d.id in (select g.x from t as g) then 1 else 0 end as r from t as d",
		"select case when d.id not in (select x from t as g where g.y = 1) then 1 else 0 end as r from t as d",
		"select case when d.id in (select x from t as g1 where g1.y = 1) and d.id not in (select z from u as g2 where g2.y = 2) then 1 else 0 end as r from t as d",
		"select case when exists (select 1 from t as g where g.y = d.id) then 1 else 0 end as r from t as d",
		"select case when d.id in (select g.x from t as g where g.y = d.k) then 1 else 0 end as r from t as d",
		"select case when 1 in (select x from t as g where g.y = 1) then 1 else 0 end as r",
		"with c as (select case when d.id in (select x from t as g where g.y = 1) then 1 else 0 end as r from t as d) select c.r from c",
		"select s.r from (select case when d.id in (select x from t as g where g.y = 1) then 1 else 0 end as r from t as d) as s",
	};

	private static final String[] ALREADY_ACCEPTED_SUBQUERIES = {
		"select (select sum(pp.x) as a from t as pp) as r from t as pr",
		"select (select sum(x) as a from t as pp) / (select sum(x) as b from t as pq) as r from t as pr",
		"select case when d.id in (select x from t as g) then 1 else 0 end as r from t as d",
		"select pr.id from t as pr where (select sum(pp.x) as a from t as pp) > 0",
		"select d.id from t as d where d.id in (select g.x from t as g where g.y = 1)",
		"select d.id from t as d where case when d.id in (select x from t as g where g.y = 1) then 1 else 0 end = 1",
		"select d.id from t as d join u as e on e.id = d.id and d.id in (select x from t as g where g.y = 1)",
		"select d.k from t as d group by d.k having count(*) > (select count(*) from t as g where g.y = 1)",
		"select d.id from t as d order by case when d.id in (select x from t as g where g.y = 1) then 1 else 0 end",
	};

	private static final String SNIPPET_9530 = String.join("\n",
		"select d.src_donor_id,",
		"case",
		"	when d.contact_optout_indicator = 'Yes' then 1",
		"	when d.DONOR_CATEGORY_DESCRIPTION = 'Friend' and d.src_donor_id in (",
		"	  select src_donor_id",
		"	  from <[AMS].[gifts].{fulfillment}> as g",
		"	  where 1=1",
		"	  and g.calculated_field_8 in ('Athletics','School of Law')",
		"	  )",
		"	  and",
		"	  d.src_donor_id not in (",
		"		select soft_credit_id",
		"	    from <[AMS].[gifts_allocation].{fulfillment}> as g",
		"	    where 1=1",
		"	    and g.calculated_field_2 not in ('Athletics','School of Law')",
		"	    ) then 1",
		"	else 0",
		"	end as contact_optout_ind",
		"FROM <[AMS].[donor].{fulfillment}> as d");

	private static final String SNIPPET_8520 = String.join("\n",
		"(",
		"select  distinct ((",
		"   select ifnull(sum(pp.record_count),0) as unknown_total",
		"		from <Base Tbl> as pp where field_name = 'address_type' and pp.eab_standard_value in ('Unmapped Value')",
		"	   ) /",
		"		(",
		"		  select sum(pq.record_count)+1 as total",
		"			from <Base Tbl> as pq where pq.field_name = 'address_type'",
		"		)) as missing_pct",
		"from <Base Tbl> as pr",
		"where ifnull(pr.partner_value,'') <>''",
		")");

	private static Snippet parse(String sql, String endpoint) {
		SqlParserAccess access = new SqlParserAccess(false, false, false);
		access.executeTheParse(sql, endpoint);
		Snippet snippet = access.getSnippet();
		Assert.assertNotNull("Snippet should not be null for: " + sql, snippet);
		return snippet;
	}

	private static void assertNoFatals(String sql, String endpoint) {
		Snippet snippet = parse(sql, endpoint);
		Assert.assertEquals("Unexpected FATALs for: " + sql + " -> " + snippet.getFatalErrorStringList(),
				0, snippet.getFatalErrorCount());
	}

	private static void assertSingleFatalNaming(String sql, String fragment) {
		Snippet snippet = parse(sql, SQLPARSER_SQL_TREE_KEY);
		Assert.assertEquals("Expected one FATAL for: " + sql + " -> " + snippet.getFatalErrorStringList(),
				1, snippet.getFatalErrorCount());
		Assert.assertTrue("FATAL should name " + fragment + ": " + snippet.getFatalErrorStringList(),
				snippet.getFatalErrorStringList().get(0).contains(fragment));
	}

	@SuppressWarnings("unchecked")
	private static Object findInterfaceEntry(Object node, String outputColumn) {
		if (!(node instanceof Map<?, ?> map)) {
			return null;
		}
		Object iface = map.get("interface");
		if (iface instanceof Map<?, ?> ifaceMap && ifaceMap.containsKey(outputColumn)) {
			return ifaceMap.get(outputColumn);
		}
		for (Object value : ((Map<String, Object>) map).values()) {
			Object found = findInterfaceEntry(value, outputColumn);
			if (found != null) {
				return found;
			}
		}
		return null;
	}

	@Test
	public void selectListExpressionSubqueriesKeepInnerAliasScopeTest() {
		for (String sql : SELECT_LIST_EXPRESSION_SUBQUERIES) {
			assertNoFatals(sql, SQLPARSER_SQL_TREE_KEY);
		}
	}

	@Test
	public void subqueriesOutsideSelectListExpressionsStillAcceptedTest() {
		for (String sql : ALREADY_ACCEPTED_SUBQUERIES) {
			assertNoFatals(sql, SQLPARSER_SQL_TREE_KEY);
		}
	}

	@Test
	public void liveSnippetsWithSelectListSubqueriesParseCleanTest() {
		assertNoFatals(SNIPPET_9530, SQLPARSER_QUERY_TREE_KEY);
		assertNoFatals(SNIPPET_8520, SQLPARSER_QUERY_TREE_KEY);
	}

	@Test
	public void genuineScopeErrorsInsideSelectListSubqueriesStillFatalTest() {
		assertSingleFatalNaming(
				"select case when d.id in (select x from t as g where zz.y = 1) then 1 else 0 end as r from t as d",
				"'zz'");
		assertSingleFatalNaming(
				"select case when exists (select 1 from t as g where g.y = qq.id) then 1 else 0 end as r from t as d",
				"'qq'");
		assertSingleFatalNaming(
				"select (select max(pp.x) from t as pp where pp.k = qq.k) + 0 as r from t as pr",
				"'qq'");
		assertSingleFatalNaming(
				"select g.y + (select max(g.x) from t as g) as r from t as d",
				"'g'");
	}

	@Test
	public void outerInterfaceOmitsSubqueryLocalReferencesTest() {
		Snippet snippet = parse(
				"select case when d.id in (select g.x from t as g where g.y = d.k) then 1 else 0 end as r from t as d",
				SQLPARSER_SQL_TREE_KEY);
		Assert.assertEquals("[{name=id, table_ref=d}]",
				String.valueOf(findInterfaceEntry(snippet.getSymbolTable(), "r")));
	}

	@Test
	public void operandScalarSubqueriesUseLookupSubtreeTest() {
		Snippet arithmetic = parse(
				"select (select sum(pp.x) as a from t as pp) / (select sum(pq.x) as b from t as pq) as r from t as pr",
				SQLPARSER_SQL_TREE_KEY);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={alias=r, calc={left={lookup={from={table={alias=pp, table=t}}, select={1={function={function_name=sum, qualifier=null, parameters={column={name=x, table_ref=pp}}}, alias=a}}}}, right={lookup={from={table={alias=pq, table=t}}, select={1={function={function_name=sum, qualifier=null, parameters={column={name=x, table_ref=pq}}}, alias=b}}}}, operator=/}}}, from={table={alias=pr, table=t}}}}",
				arithmetic.getSqlAbstractTree().toString());

		Snippet whereComparison = parse(
				"select pr.id from t as pr where (select sum(pp.x) as a from t as pp) > 0",
				SQLPARSER_SQL_TREE_KEY);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=id, table_ref=pr}}}, from={table={alias=pr, table=t}}, where={condition={left={lookup={from={table={alias=pp, table=t}}, select={1={function={function_name=sum, qualifier=null, parameters={column={name=x, table_ref=pp}}}, alias=a}}}}, right={literal=0}, operator=>}}}}",
				whereComparison.getSqlAbstractTree().toString());

		Snippet bareItem = parse(
				"select (select sum(pp.x) as a from t as pp) as r from t as pr",
				SQLPARSER_SQL_TREE_KEY);
		String bareTree = bareItem.getSqlAbstractTree().toString();
		Assert.assertEquals("Bare scalar subquery must keep a single lookup wrapper",
				1, bareTree.split("lookup=", -1).length - 1);
		Assert.assertFalse("Bare scalar subquery must not be double-wrapped",
				bareTree.contains("lookup={lookup="));
	}
}
