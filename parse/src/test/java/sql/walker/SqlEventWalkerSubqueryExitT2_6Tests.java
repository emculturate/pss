package sql.walker;

import org.junit.Assert;
import org.junit.Test;

/**
 * Walker coverage T2.6 — {@code exitSubquery} across parenthesized {@code query_expression} sites.
 */
public class SqlEventWalkerSubqueryExitT2_6Tests extends AbstractSqlParseEventWalkerTest {

	private void assertSubqueryExitOutputs(SqlParseEventWalker extractor, String expectedAst,
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
	public void subqueryScalarInSelectListT2_6Test() {
		final String query = "SELECT (SELECT max(x) FROM t2) AS m FROM t1";
		SqlParseEventWalker extractor = runParsertest(query, parse(query));
		assertSubqueryExitOutputs(extractor,
				"{SQL={select={1={lookup={from={table={alias=null, table=t2}}, select={1={function={function_name=max, qualifier=null, parameters={column={name=x, table_ref=null}}}}}}, alias=m}}, from={table={alias=null, table=t1}}}}",
				"[m]", "{}",
				"{t1={}, t2={x=[[@5,19:19='x',<393>,1:19]]}}",
				"{query0={unnamed_0=[[@6,20:20=')',<288>,1:20]]}, query2={m=[[@11,34:34='m',<393>,1:34]]}}",
				"{def_query2={query_dictionary={m=[[@11,34:34='m',<393>,1:34]]}, table_dictionary={t1={}}, dependent_queries={predicand1={query=query0, type=interface}}, def_query0={query_dictionary={unnamed_0=[[@6,20:20=')',<288>,1:20]]}, table_dictionary={t2={x=[[@5,19:19='x',<393>,1:19]]}}, interface={unnamed_0=[{name=x, table_ref=t2}]}}, interface={m=[{name=x, table_ref=null}]}}}");
	}

	@Test
	public void subqueryScalarInWhereT2_6Test() {
		final String query = "SELECT a FROM t1 WHERE a > (SELECT max(b) FROM t2)";
		SqlParseEventWalker extractor = runParsertest(query, parse(query));
		assertSubqueryExitOutputs(extractor,
				"{SQL={select={1={column={name=a, table_ref=null}}}, from={table={alias=null, table=t1}}, where={condition={left={column={name=a, table_ref=null}}, right={select={1={function={function_name=max, qualifier=null, parameters={column={name=b, table_ref=null}}}}}, from={table={alias=null, table=t2}}}, operator=>}}}}",
				"[a]", "{}",
				"{t1={a=[[@1,7:7='a',<393>,1:7], [@5,23:23='a',<393>,1:23]]}, t2={b=[[@11,39:39='b',<393>,1:39]]}}",
				"{query0={unnamed_0=[[@12,40:40=')',<288>,1:40]]}, query2={a=[[@1,7:7='a',<393>,1:7]]}}",
				"{def_query2={query_dictionary={a=[[@1,7:7='a',<393>,1:7]]}, table_dictionary={t1={a=[[@1,7:7='a',<393>,1:7], [@5,23:23='a',<393>,1:23]]}}, dependent_queries={predicand1={query=query0, type=filters}}, def_query0={query_dictionary={unnamed_0=[[@12,40:40=')',<288>,1:40]]}, table_dictionary={t2={b=[[@11,39:39='b',<393>,1:39]]}}, interface={unnamed_0=[{name=b, table_ref=t2}]}}, filters=[{name=a, table_ref=t1}], interface={a=[{name=a, table_ref=t1}]}}}");
	}

	@Test
	public void subqueryExistsPredicateT2_6Test() {
		final String query = "SELECT a FROM t1 WHERE EXISTS (SELECT 1 FROM t2 WHERE t2.id = t1.id)";
		SqlParseEventWalker extractor = runParsertest(query, parse(query));
		assertSubqueryExitOutputs(extractor,
				"{SQL={select={1={column={name=a, table_ref=null}}}, from={table={alias=null, table=t1}}, where={exists={select={1={literal=1}}, from={table={alias=null, table=t2}}, where={condition={left={column={name=id, table_ref=t2}}, right={column={name=id, table_ref=t1}}, operator==}}, operator=EXISTS}}}}",
				"[a]", "{}",
				"{t1={a=[[@1,7:7='a',<393>,1:7]], id=[[@16,62:63='t1',<393>,1:62]]}, t2={id=[[@12,54:55='t2',<393>,1:54]]}}",
				"{query0={unnamed_0=[[@8,38:38='1',<300>,1:38]]}, query2={a=[[@1,7:7='a',<393>,1:7]]}}",
				"{def_query2={query_dictionary={a=[[@1,7:7='a',<393>,1:7]]}, table_dictionary={t1={a=[[@1,7:7='a',<393>,1:7]], id=[[@16,62:63='t1',<393>,1:62]]}}, dependent_queries={exists1={query=query0, type=filters}}, def_query0={query_dictionary={unnamed_0=[[@8,38:38='1',<300>,1:38]]}, table_dictionary={t2={id=[[@12,54:55='t2',<393>,1:54]]}}, filters=[{name=id, table_ref=t2}, {name=id, table_ref=t1}], interface={unnamed_0=[]}}, filters=[], interface={a=[{name=a, table_ref=t1}]}}}");
	}

	@Test
	public void subqueryInPredicateT2_6Test() {
		final String query = "SELECT a FROM t1 WHERE a IN (SELECT b FROM t2)";
		SqlParseEventWalker extractor = runParsertest(query, parse(query));
		assertSubqueryExitOutputs(extractor,
				"{SQL={select={1={column={name=a, table_ref=null}}}, from={table={alias=null, table=t1}}, where={in={item={column={name=a, table_ref=null}}, in_list={select={1={column={name=b, table_ref=null}}}, from={table={alias=null, table=t2}}}}}}}",
				"[a]", "{}",
				"{t1={a=[[@1,7:7='a',<393>,1:7], [@5,23:23='a',<393>,1:23]]}, t2={b=[[@9,36:36='b',<393>,1:36]]}}",
				"{query0={b=[[@9,36:36='b',<393>,1:36]]}, query2={a=[[@1,7:7='a',<393>,1:7]]}}",
				"{def_query2={query_dictionary={a=[[@1,7:7='a',<393>,1:7]]}, table_dictionary={t1={a=[[@1,7:7='a',<393>,1:7], [@5,23:23='a',<393>,1:23]]}}, dependent_queries={in_list1={query=query0, type=filters}}, def_query0={query_dictionary={b=[[@9,36:36='b',<393>,1:36]]}, table_dictionary={t2={b=[[@9,36:36='b',<393>,1:36]]}}, interface={b=[{name=b, table_ref=t2}]}}, filters=[{name=a, table_ref=t1}], interface={a=[{name=a, table_ref=t1}]}}}");
	}

	@Test
	public void subqueryFromDerivedTableT2_6Test() {
		final String query = "SELECT s.x FROM (SELECT a AS x FROM t2) s";
		SqlParseEventWalker extractor = runParsertest(query, parse(query));
		assertSubqueryExitOutputs(extractor,
				"{SQL={select={1={column={name=x, table_ref=s}}}, from={table={alias=s, query={select={1={column={name=a, table_ref=null}, alias=x}}, from={table={alias=null, table=t2}}}}}}}",
				"[x]", "{}",
				"{t2={a=[[@7,24:24='a',<393>,1:24]]}}",
				"{query0={x=[[@9,29:29='x',<393>,1:29], [@1,7:7='s',<393>,1:7]]}, query1={x=[[@3,9:9='x',<393>,1:9]]}}",
				"{def_query1={query_dictionary={x=[[@3,9:9='x',<393>,1:9]]}, def_query0={query_dictionary={x=[[@9,29:29='x',<393>,1:29], [@1,7:7='s',<393>,1:7]]}, table_dictionary={t2={a=[[@7,24:24='a',<393>,1:24]]}}, interface={x=[{name=a, table_ref=t2}]}}, interface={x=[{name=x, table_ref=s}]}, table_alias={s=query0}}}");
	}

	@Test
	public void subqueryInsertSelectSourceT2_6Test() {
		final String query = "INSERT INTO dst SELECT * FROM (SELECT a, b FROM src) q";
		SqlParseEventWalker extractor = runParsertest(query, parse(query));
		assertSubqueryExitOutputs(extractor,
				"{SQL={insert={preamble=insert_into, from={from={table={alias=q, query={select={1={column={name=a, table_ref=null}}, 2={column={name=b, table_ref=null}}}, from={table={alias=null, table=src}}}}}, select={1={column={name=*, table_ref=*}}}}, target_table={table={alias=null, table=dst}}}}}",
				"[*]", "{}",
				"{dst={*=[[@4,23:23='*',<291>,1:23]]}, src={a=[[@8,38:38='a',<393>,1:38]], b=[[@10,41:41='b',<393>,1:41]]}}",
				"{query0={a=[[@8,38:38='a',<393>,1:38]], b=[[@10,41:41='b',<393>,1:41]], *=[[@4,23:23='*',<291>,1:23]]}, query1={*=[[@4,23:23='*',<291>,1:23]]}, insert2={*=[[@4,23:23='*',<291>,1:23]]}}",
				"{def_insert2={query_dictionary={*=[[@4,23:23='*',<291>,1:23]]}, table_dictionary={dst={*=[[@4,23:23='*',<291>,1:23]]}}, def_query1={query_dictionary={*=[[@4,23:23='*',<291>,1:23]]}, def_query0={query_dictionary={a=[[@8,38:38='a',<393>,1:38]], b=[[@10,41:41='b',<393>,1:41]], *=[[@4,23:23='*',<291>,1:23]]}, table_dictionary={src={a=[[@8,38:38='a',<393>,1:38]], b=[[@10,41:41='b',<393>,1:41]]}}, interface={a=[{name=a, table_ref=src}], b=[{name=b, table_ref=src}]}}, interface={*=[{name=*, table_ref=*}]}, table_alias={q=query0}}, interface={*=[{name=*, table_ref=query1}]}}}");
	}

	@Test
	public void subquerySetOpMemberT2_6Test() {
		final String query = "(SELECT a FROM t1) UNION (SELECT a FROM t2)";
		SqlParseEventWalker extractor = runParsertest(query, parse(query));
		assertSubqueryExitOutputs(extractor,
				"{SQL={union={1={select={1={column={name=a, table_ref=null}}}, from={table={alias=null, table=t1}}}, 2={union={qualifier=null, operator=UNION}}, 3={select={1={column={name=a, table_ref=null}}}, from={table={alias=null, table=t2}}}}}}",
				"[a]", "{}",
				"{t1={a=[[@2,8:8='a',<393>,1:8]]}, t2={a=[[@9,33:33='a',<393>,1:33]]}}",
				"{query0={a=[[@2,8:8='a',<393>,1:8]]}, query1={a=[[@9,33:33='a',<393>,1:33]]}}",
				"{def_union2={def_query1={query_dictionary={a=[[@9,33:33='a',<393>,1:33]]}, table_dictionary={t2={a=[[@9,33:33='a',<393>,1:33]]}}, setop=UNION, interface={a=[{name=a, table_ref=t2}]}}, def_query0={query_dictionary={a=[[@2,8:8='a',<393>,1:8]]}, table_dictionary={t1={a=[[@2,8:8='a',<393>,1:8]]}}, interface={a=[{name=a, table_ref=t1}]}}, interface={a=query_column}}}");
	}

	@Test
	public void subqueryQuantifiedCompareT2_6Test() {
		final String query = "SELECT a FROM t1 WHERE a > ALL (SELECT b FROM t2)";
		SqlParseEventWalker extractor = runParsertest(query, parse(query));
		assertSubqueryExitOutputs(extractor,
				"{SQL={select={1={column={name=a, table_ref=null}}}, from={table={alias=null, table=t1}}, where={condition={left={column={name=a, table_ref=null}}, right={select={1={column={name=b, table_ref=null}}}, from={table={alias=null, table=t2}}}, quantifier=ALL, operator=>}}}}",
				"[a]", "{}",
				"{t1={a=[[@1,7:7='a',<393>,1:7], [@5,23:23='a',<393>,1:23]]}, t2={b=[[@10,39:39='b',<393>,1:39]]}}",
				"{query0={b=[[@10,39:39='b',<393>,1:39]]}, query2={a=[[@1,7:7='a',<393>,1:7]]}}",
				"{def_query2={query_dictionary={a=[[@1,7:7='a',<393>,1:7]]}, table_dictionary={t1={a=[[@1,7:7='a',<393>,1:7], [@5,23:23='a',<393>,1:23]]}}, dependent_queries={quantified1={query=query0, type=filters}}, def_query0={query_dictionary={b=[[@10,39:39='b',<393>,1:39]]}, table_dictionary={t2={b=[[@10,39:39='b',<393>,1:39]]}}, interface={b=[{name=b, table_ref=t2}]}}, filters=[{name=a, table_ref=t1}], interface={a=[{name=a, table_ref=t1}]}}}");
	}
}
