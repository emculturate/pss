package sql.walker;

import org.junit.Assert;
import org.junit.Test;

import sql.SQLSelectParserParser;

/**
 * Phase 2.6 — postfix array subscript {@code expr[index]} (shared with Phase 5.4).
 * Phase 2.4 nested searched {@code CASE} exemplars (blocked on subscript until 2.6).
 */
public class SqlEventWalkerArraySubscriptTests extends AbstractSqlParseEventWalkerTest {

	@Test
	public void splitResultLiteralSubscriptV0Test() {
		final String query = "SELECT SPLIT('a|b', '|')[0] AS x";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		assertNoFatalErrors(extractor);

		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={subscript={array={function={parameters={1={literal='a|b'}, 2={literal='|'}}, function_name=SPLIT}}, index={literal=0}}, alias=x}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[x]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{query0={x=[[@11,31:31='x',<393>,1:31]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query0={query_dictionary={x=[[@11,31:31='x',<393>,1:31]]}, interface={x=[]}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void splitResultLiteralSubscriptIndexOneV1Test() {
		final String query = "SELECT SPLIT('a|b|c', '|')[1] AS x";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		assertNoFatalErrors(extractor);

		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={subscript={array={function={parameters={1={literal='a|b|c'}, 2={literal='|'}}, function_name=SPLIT}}, index={literal=1}}, alias=x}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[x]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{query0={x=[[@11,33:33='x',<393>,1:33]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query0={query_dictionary={x=[[@11,33:33='x',<393>,1:33]]}, interface={x=[]}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void splitColumnSubscriptWithColumnArgumentV2Test() {
		final String query = "SELECT SPLIT(col, '|')[1] AS x FROM tab1";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		assertNoFatalErrors(extractor);

		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={subscript={array={function={parameters={1={column={name=col, table_ref=null}}, 2={literal='|'}}, function_name=SPLIT}}, index={literal=1}}, alias=x}}, from={table={alias=null, table=tab1}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[x]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{tab1={col=[[@3,13:15='col',<393>,1:13]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{query0={x=[[@11,29:29='x',<393>,1:29]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query0={query_dictionary={x=[[@11,29:29='x',<393>,1:29]]}, table_dictionary={tab1={col=[[@3,13:15='col',<393>,1:13]]}}, interface={x=[{name=col, table_ref=tab1}]}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void splitColumnSubscriptBareIndexColumnRefV2cTest() {
		final String query = "SELECT SPLIT(col, '|')[idx] AS x FROM tab1";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		assertNoFatalErrors(extractor);

		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={subscript={array={function={parameters={1={column={name=col, table_ref=null}}, 2={literal='|'}}, function_name=SPLIT}}, index={column={name=idx, table_ref=null}}}, alias=x}}, from={table={alias=null, table=tab1}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[x]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{tab1={col=[[@3,13:15='col',<393>,1:13]], idx=[[@8,23:25='idx',<393>,1:23]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{query0={x=[[@11,31:31='x',<393>,1:31]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query0={query_dictionary={x=[[@11,31:31='x',<393>,1:31]]}, table_dictionary={tab1={col=[[@3,13:15='col',<393>,1:13]], idx=[[@8,23:25='idx',<393>,1:23]]}}, interface={x=[{name=col, table_ref=tab1}, {name=idx, table_ref=tab1}]}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void splitColumnSubscriptQualifiedIndexColumnRefV2dTest() {
		final String query = "SELECT SPLIT(a, '|')[tab1.b] AS x FROM tab1";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		assertNoFatalErrors(extractor);

		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={subscript={array={function={parameters={1={column={name=a, table_ref=null}}, 2={literal='|'}}, function_name=SPLIT}}, index={column={name=b, table_ref=tab1}}}, alias=x}}, from={table={alias=null, table=tab1}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[x]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{tab1={a=[[@3,13:13='a',<393>,1:13]], b=[[@8,21:24='tab1',<393>,1:21]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{query0={x=[[@13,32:32='x',<393>,1:32]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query0={query_dictionary={x=[[@13,32:32='x',<393>,1:32]]}, table_dictionary={tab1={a=[[@3,13:13='a',<393>,1:13]], b=[[@8,21:24='tab1',<393>,1:21]]}}, interface={x=[{name=a, table_ref=tab1}, {name=b, table_ref=tab1}]}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void splitColumnSubscriptParenthesizedIndexColumnRefV2bTest() {
		// Parenthesized [(idx)] still valid; bare [idx] covered by splitColumnSubscriptBareIndexColumnRefV2cTest.
		final String query = "SELECT SPLIT(col, '|')[(idx)] AS x FROM tab1";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		assertNoFatalErrors(extractor);

		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={subscript={array={function={parameters={1={column={name=col, table_ref=null}}, 2={literal='|'}}, function_name=SPLIT}}, index={parentheses={column={name=idx, table_ref=null}}}}, alias=x}}, from={table={alias=null, table=tab1}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[x]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{tab1={col=[[@3,13:15='col',<393>,1:13]], idx=[[@9,24:26='idx',<393>,1:24]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{query0={x=[[@13,33:33='x',<393>,1:33]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query0={query_dictionary={x=[[@13,33:33='x',<393>,1:33]]}, table_dictionary={tab1={col=[[@3,13:15='col',<393>,1:13]], idx=[[@9,24:26='idx',<393>,1:24]]}}, interface={x=[{name=col, table_ref=tab1}, {name=idx, table_ref=tab1}]}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void productCaseSecondBranchSemicolonSplitPartV3Test() {
		final String query =
				"SELECT CASE WHEN POSITION(';', cat.title) > 0"
				+ " THEN NULLIF(TRIM(SPLIT_PART(cat.title, ';', 1)), '')"
				+ " ELSE NULL END AS product"
				+ " FROM acs__categories AS cat";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		assertNoFatalErrors(extractor);

		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={alias=product, case={clauses={1={then={function={parameters={1={function={parameters={1={function={parameters={1={column={name=title, table_ref=cat}}, 2={literal=';'}, 3={literal=1}}, function_name=SPLIT_PART}}}, function_name=TRIM}}, 2={literal=''}}, function_name=NULLIF}}, when={condition={left={function={function_name=POSITION, parameters={1={literal=';'}, 2={column={name=title, table_ref=cat}}}, operator=IN}}, right={literal=0}, operator=>}}}}, else={null_literal=null}}}}, from={table={alias=cat, table=acs__categories}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[product]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{acs__categories={title=[[@7,31:33='cat',<393>,1:31], [@20,74:76='cat',<393>,1:74]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{query0={product=[[@36,116:122='product',<393>,1:116]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query0={query_dictionary={product=[[@36,116:122='product',<393>,1:116]]}, table_dictionary={acs__categories={title=[[@7,31:33='cat',<393>,1:31], [@20,74:76='cat',<393>,1:74]]}}, interface={product=[{name=title, table_ref=cat}]}, table_alias={cat=acs__categories}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void productCaseFirstBranchNestedSplitSubscriptV4Test() {
		final String query =
				"SELECT CASE WHEN POSITION('p={', cat.title) > 0"
				+ " THEN NULLIF(TRIM(REPLACE(SPLIT(SPLIT_PART(SPLIT_PART(cat.title, 'p={', 2), '}', 1), '|')[0], '\"', '')), '')"
				+ " ELSE NULL END AS product"
				+ " FROM acs__categories AS cat";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		assertNoFatalErrors(extractor);

		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={alias=product, case={clauses={1={then={function={parameters={1={function={parameters={1={function={parameters={1={subscript={array={function={parameters={1={function={parameters={1={function={parameters={1={column={name=title, table_ref=cat}}, 2={literal='p={'}, 3={literal=2}}, function_name=SPLIT_PART}}, 2={literal='}'}, 3={literal=1}}, function_name=SPLIT_PART}}, 2={literal='|'}}, function_name=SPLIT}}, index={literal=0}}}, 2={literal='\"'}, 3={literal=''}}, function_name=REPLACE}}}, function_name=TRIM}}, 2={literal=''}}, function_name=NULLIF}}, when={condition={left={function={function_name=POSITION, parameters={1={literal='p={'}, 2={column={name=title, table_ref=cat}}}, operator=IN}}, right={literal=0}, operator=>}}}}, else={null_literal=null}}}}, from={table={alias=cat, table=acs__categories}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[product]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{acs__categories={title=[[@7,33:35='cat',<393>,1:33], [@26,101:103='cat',<393>,1:101]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{query0={product=[[@58,173:179='product',<393>,1:173]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query0={query_dictionary={product=[[@58,173:179='product',<393>,1:173]]}, table_dictionary={acs__categories={title=[[@7,33:35='cat',<393>,1:33], [@26,101:103='cat',<393>,1:101]]}}, interface={product=[{name=title, table_ref=cat}]}, table_alias={cat=acs__categories}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void productCaseFullFixtureCatTitleWithJoinV5Test() {
		final String query =
				"SELECT CASE WHEN POSITION('p={', cat.title) > 0"
				+ " THEN NULLIF(TRIM(COALESCE(prod_abbr.eab_std_value,"
				+ " REPLACE(SPLIT(SPLIT_PART(SPLIT_PART(cat.title, 'p={', 2), '}', 1), '|')[0], '\"', ''))), '')"
				+ " WHEN POSITION(';', cat.title) > 0"
				+ " THEN NULLIF(TRIM(SPLIT_PART(cat.title, ';', 1)), '')"
				+ " ELSE NULL END AS product"
				+ " FROM acs__categories AS cat"
				+ " LEFT JOIN ref__standard_value_mapping_alr AS prod_abbr"
				+ " ON prod_abbr.client_value = REPLACE(SPLIT(SPLIT_PART(SPLIT_PART(cat.title, 'p={', 2), '}', 1), '|')[0], '\"', '')"
				+ " AND prod_abbr.field_name = 'product'";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		assertNoFatalErrors(extractor);

		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={alias=product, case={clauses={1={then={function={parameters={1={function={parameters={1={function={parameters={1={column={name=eab_std_value, table_ref=prod_abbr}}, 2={function={parameters={1={subscript={array={function={parameters={1={function={parameters={1={function={parameters={1={column={name=title, table_ref=cat}}, 2={literal='p={'}, 3={literal=2}}, function_name=SPLIT_PART}}, 2={literal='}'}, 3={literal=1}}, function_name=SPLIT_PART}}, 2={literal='|'}}, function_name=SPLIT}}, index={literal=0}}}, 2={literal='\"'}, 3={literal=''}}, function_name=REPLACE}}}, function_name=COALESCE}}}, function_name=TRIM}}, 2={literal=''}}, function_name=NULLIF}}, when={condition={left={function={function_name=POSITION, parameters={1={literal='p={'}, 2={column={name=title, table_ref=cat}}}, operator=IN}}, right={literal=0}, operator=>}}}, 2={then={function={parameters={1={function={parameters={1={function={parameters={1={column={name=title, table_ref=cat}}, 2={literal=';'}, 3={literal=1}}, function_name=SPLIT_PART}}}, function_name=TRIM}}, 2={literal=''}}, function_name=NULLIF}}, when={condition={left={function={function_name=POSITION, parameters={1={literal=';'}, 2={column={name=title, table_ref=cat}}}, operator=IN}}, right={literal=0}, operator=>}}}}, else={null_literal=null}}}}, from={join={1={table={alias=cat, table=acs__categories}}, 2={join=LEFT, on={and={1={condition={left={column={name=client_value, table_ref=prod_abbr}}, right={function={parameters={1={subscript={array={function={parameters={1={function={parameters={1={function={parameters={1={column={name=title, table_ref=cat}}, 2={literal='p={'}, 3={literal=2}}, function_name=SPLIT_PART}}, 2={literal='}'}, 3={literal=1}}, function_name=SPLIT_PART}}, 2={literal='|'}}, function_name=SPLIT}}, index={literal=0}}}, 2={literal='\"'}, 3={literal=''}}, function_name=REPLACE}}, operator==}}, 2={condition={left={column={name=field_name, table_ref=prod_abbr}}, right={literal='product'}, operator==}}}}}, 3={table={alias=prod_abbr, table=ref__standard_value_mapping_alr}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[product]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{acs__categories={title=[[@7,33:35='cat',<393>,1:33], [@32,135:137='cat',<393>,1:135], [@66,210:212='cat',<393>,1:210], [@79,253:255='cat',<393>,1:253], [@118,450:452='cat',<393>,1:450]]}, ref__standard_value_mapping_alr={eab_std_value=[[@20,74:82='prod_abbr',<393>,1:74]], client_value=[[@106,389:397='prod_abbr',<393>,1:389]], field_name=[[@143,503:511='prod_abbr',<393>,1:503]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{query0={product=[[@95,295:301='product',<393>,1:295]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query0={query_dictionary={product=[[@95,295:301='product',<393>,1:295]]}, table_dictionary={acs__categories={title=[[@7,33:35='cat',<393>,1:33], [@32,135:137='cat',<393>,1:135], [@66,210:212='cat',<393>,1:210], [@79,253:255='cat',<393>,1:253], [@118,450:452='cat',<393>,1:450]]}, ref__standard_value_mapping_alr={eab_std_value=[[@20,74:82='prod_abbr',<393>,1:74]], client_value=[[@106,389:397='prod_abbr',<393>,1:389]], field_name=[[@143,503:511='prod_abbr',<393>,1:503]]}}, filters=[{name=client_value, table_ref=prod_abbr}, {name=title, table_ref=cat}, {name=field_name, table_ref=prod_abbr}], interface={product=[{name=eab_std_value, table_ref=prod_abbr}, {name=title, table_ref=cat}]}, table_alias={cat=acs__categories, prod_abbr=ref__standard_value_mapping_alr}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void productCaseFullFixtureCtgTitleLineBrokenCoalesceV6Test() {
		final String query =
				"SELECT CASE WHEN POSITION('p={', ctg.title) > 0"
				+ " THEN NULLIF(TRIM(COALESCE(prod_abbr.eab_std_value,"
				+ " REPLACE(SPLIT(SPLIT_PART(SPLIT_PART(ctg.title, 'p={', 2), '}', 1), '|')[0], '\"', ''))), '')"
				+ " WHEN POSITION(';', ctg.title) > 0"
				+ " THEN NULLIF(TRIM(SPLIT_PART(ctg.title, ';', 1)), '')"
				+ " ELSE NULL END AS product"
				+ " FROM acs__categories AS ctg"
				+ " LEFT JOIN ref__standard_value_mapping_alr AS prod_abbr"
				+ " ON prod_abbr.client_value = REPLACE(SPLIT(SPLIT_PART(SPLIT_PART(ctg.title, 'p={', 2), '}', 1), '|')[0], '\"', '')"
				+ " AND prod_abbr.field_name = 'product'";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		assertNoFatalErrors(extractor);

		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={alias=product, case={clauses={1={then={function={parameters={1={function={parameters={1={function={parameters={1={column={name=eab_std_value, table_ref=prod_abbr}}, 2={function={parameters={1={subscript={array={function={parameters={1={function={parameters={1={function={parameters={1={column={name=title, table_ref=ctg}}, 2={literal='p={'}, 3={literal=2}}, function_name=SPLIT_PART}}, 2={literal='}'}, 3={literal=1}}, function_name=SPLIT_PART}}, 2={literal='|'}}, function_name=SPLIT}}, index={literal=0}}}, 2={literal='\"'}, 3={literal=''}}, function_name=REPLACE}}}, function_name=COALESCE}}}, function_name=TRIM}}, 2={literal=''}}, function_name=NULLIF}}, when={condition={left={function={function_name=POSITION, parameters={1={literal='p={'}, 2={column={name=title, table_ref=ctg}}}, operator=IN}}, right={literal=0}, operator=>}}}, 2={then={function={parameters={1={function={parameters={1={function={parameters={1={column={name=title, table_ref=ctg}}, 2={literal=';'}, 3={literal=1}}, function_name=SPLIT_PART}}}, function_name=TRIM}}, 2={literal=''}}, function_name=NULLIF}}, when={condition={left={function={function_name=POSITION, parameters={1={literal=';'}, 2={column={name=title, table_ref=ctg}}}, operator=IN}}, right={literal=0}, operator=>}}}}, else={null_literal=null}}}}, from={join={1={table={alias=ctg, table=acs__categories}}, 2={join=LEFT, on={and={1={condition={left={column={name=client_value, table_ref=prod_abbr}}, right={function={parameters={1={subscript={array={function={parameters={1={function={parameters={1={function={parameters={1={column={name=title, table_ref=ctg}}, 2={literal='p={'}, 3={literal=2}}, function_name=SPLIT_PART}}, 2={literal='}'}, 3={literal=1}}, function_name=SPLIT_PART}}, 2={literal='|'}}, function_name=SPLIT}}, index={literal=0}}}, 2={literal='\"'}, 3={literal=''}}, function_name=REPLACE}}, operator==}}, 2={condition={left={column={name=field_name, table_ref=prod_abbr}}, right={literal='product'}, operator==}}}}}, 3={table={alias=prod_abbr, table=ref__standard_value_mapping_alr}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[product]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{acs__categories={title=[[@7,33:35='ctg',<393>,1:33], [@32,135:137='ctg',<393>,1:135], [@66,210:212='ctg',<393>,1:210], [@79,253:255='ctg',<393>,1:253], [@118,450:452='ctg',<393>,1:450]]}, ref__standard_value_mapping_alr={eab_std_value=[[@20,74:82='prod_abbr',<393>,1:74]], client_value=[[@106,389:397='prod_abbr',<393>,1:389]], field_name=[[@143,503:511='prod_abbr',<393>,1:503]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{query0={product=[[@95,295:301='product',<393>,1:295]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query0={query_dictionary={product=[[@95,295:301='product',<393>,1:295]]}, table_dictionary={acs__categories={title=[[@7,33:35='ctg',<393>,1:33], [@32,135:137='ctg',<393>,1:135], [@66,210:212='ctg',<393>,1:210], [@79,253:255='ctg',<393>,1:253], [@118,450:452='ctg',<393>,1:450]]}, ref__standard_value_mapping_alr={eab_std_value=[[@20,74:82='prod_abbr',<393>,1:74]], client_value=[[@106,389:397='prod_abbr',<393>,1:389]], field_name=[[@143,503:511='prod_abbr',<393>,1:503]]}}, filters=[{name=client_value, table_ref=prod_abbr}, {name=title, table_ref=ctg}, {name=field_name, table_ref=prod_abbr}], interface={product=[{name=eab_std_value, table_ref=prod_abbr}, {name=title, table_ref=ctg}]}, table_alias={ctg=acs__categories, prod_abbr=ref__standard_value_mapping_alr}}}",
				extractor.getSymbolTable().toString());
	}

	private static String compactNestedCase() {
		return "CASE WHEN 1 = 1 THEN CASE WHEN col = 'a' THEN 'y' ELSE 'n' END ELSE 'z' END";
	}

	private static String compactNestedCaseForJoinOn() {
		return "CASE WHEN 1 = 1 THEN CASE WHEN t.col = 'a' THEN 'y' ELSE 'n' END ELSE 'z' END";
	}

	private static String compactProductCase(String alias) {
		return "CASE WHEN POSITION(';', " + alias + ".title) > 0"
				+ " THEN NULLIF(TRIM(SPLIT_PART(" + alias + ".title, ';', 1)), '')"
				+ " ELSE NULL END";
	}

	private static String nestedSourceTypeCaseExpression() {
		final String innerPipeToken =
				"LOWER(REPLACE(SPLIT(SPLIT_PART(SPLIT_PART(ACA.title, 'f={',2), '}', 1), '|')[0], '\"', ''))";
		return "CASE"
				+ " WHEN POSITION('p={', ACA.title) > 0 THEN"
				+ " CASE"
				+ " WHEN " + innerPipeToken + " = LOWER('Email') THEN 'EAB Web form'"
				+ " WHEN " + innerPipeToken + " = LOWER('Facebook') THEN 'Facebook'"
				+ " WHEN " + innerPipeToken + " = LOWER('LinkedIn') THEN 'LinkedIn'"
				+ " WHEN " + innerPipeToken + " = LOWER('Paid Search') THEN 'Paid Search'"
				+ " WHEN " + innerPipeToken + " = LOWER('.EDU Web Form') THEN '.EDU Web form'"
				+ " ELSE 'EAB Web form'"
				+ " END"
				+ " ELSE"
				+ " CASE"
				+ " WHEN LOWER(TRIM(SPLIT_PART(ACA.title, ';', -1))) = LOWER('Email') THEN 'EAB Web form'"
				+ " WHEN LOWER(TRIM(SPLIT_PART(ACA.title, ';', -1))) = LOWER('Facebook') THEN 'Facebook'"
				+ " WHEN LOWER(TRIM(SPLIT_PART(ACA.title, ';', -1))) = LOWER('LinkedIn') THEN 'LinkedIn'"
				+ " WHEN LOWER(TRIM(SPLIT_PART(ACA.title, ';', -1))) = LOWER('Paid Search') THEN 'Paid Search'"
				+ " WHEN LOWER(TRIM(SPLIT_PART(ACA.title, ';', -1))) = LOWER('.EDU Web Form') THEN '.EDU Web form'"
				+ " ELSE 'EAB Web form'"
				+ " END"
				+ " END";
	}

	@Test
	public void nestedSearchedCaseWorkplanStarterV0Test() {
		final String query = "SELECT " + nestedSourceTypeCaseExpression() + " AS source_type"
				+ " FROM acs__categories ACA";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		assertNoFatalErrors(extractor);

		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={alias=source_type, case={clauses={1={then={case={clauses={1={then={literal='EAB Web form'}, when={condition={left={function={parameters={1={function={parameters={1={subscript={array={function={parameters={1={function={parameters={1={function={parameters={1={column={name=title, table_ref=ACA}}, 2={literal='f={'}, 3={literal=2}}, function_name=SPLIT_PART}}, 2={literal='}'}, 3={literal=1}}, function_name=SPLIT_PART}}, 2={literal='|'}}, function_name=SPLIT}}, index={literal=0}}}, 2={literal='\"'}, 3={literal=''}}, function_name=REPLACE}}}, function_name=LOWER}}, right={function={parameters={1={literal='Email'}}, function_name=LOWER}}, operator==}}}, 2={then={literal='Facebook'}, when={condition={left={function={parameters={1={function={parameters={1={subscript={array={function={parameters={1={function={parameters={1={function={parameters={1={column={name=title, table_ref=ACA}}, 2={literal='f={'}, 3={literal=2}}, function_name=SPLIT_PART}}, 2={literal='}'}, 3={literal=1}}, function_name=SPLIT_PART}}, 2={literal='|'}}, function_name=SPLIT}}, index={literal=0}}}, 2={literal='\"'}, 3={literal=''}}, function_name=REPLACE}}}, function_name=LOWER}}, right={function={parameters={1={literal='Facebook'}}, function_name=LOWER}}, operator==}}}, 3={then={literal='LinkedIn'}, when={condition={left={function={parameters={1={function={parameters={1={subscript={array={function={parameters={1={function={parameters={1={function={parameters={1={column={name=title, table_ref=ACA}}, 2={literal='f={'}, 3={literal=2}}, function_name=SPLIT_PART}}, 2={literal='}'}, 3={literal=1}}, function_name=SPLIT_PART}}, 2={literal='|'}}, function_name=SPLIT}}, index={literal=0}}}, 2={literal='\"'}, 3={literal=''}}, function_name=REPLACE}}}, function_name=LOWER}}, right={function={parameters={1={literal='LinkedIn'}}, function_name=LOWER}}, operator==}}}, 4={then={literal='Paid Search'}, when={condition={left={function={parameters={1={function={parameters={1={subscript={array={function={parameters={1={function={parameters={1={function={parameters={1={column={name=title, table_ref=ACA}}, 2={literal='f={'}, 3={literal=2}}, function_name=SPLIT_PART}}, 2={literal='}'}, 3={literal=1}}, function_name=SPLIT_PART}}, 2={literal='|'}}, function_name=SPLIT}}, index={literal=0}}}, 2={literal='\"'}, 3={literal=''}}, function_name=REPLACE}}}, function_name=LOWER}}, right={function={parameters={1={literal='Paid Search'}}, function_name=LOWER}}, operator==}}}, 5={then={literal='.EDU Web form'}, when={condition={left={function={parameters={1={function={parameters={1={subscript={array={function={parameters={1={function={parameters={1={function={parameters={1={column={name=title, table_ref=ACA}}, 2={literal='f={'}, 3={literal=2}}, function_name=SPLIT_PART}}, 2={literal='}'}, 3={literal=1}}, function_name=SPLIT_PART}}, 2={literal='|'}}, function_name=SPLIT}}, index={literal=0}}}, 2={literal='\"'}, 3={literal=''}}, function_name=REPLACE}}}, function_name=LOWER}}, right={function={parameters={1={literal='.EDU Web Form'}}, function_name=LOWER}}, operator==}}}}, else={literal='EAB Web form'}}}, when={condition={left={function={function_name=POSITION, parameters={1={literal='p={'}, 2={column={name=title, table_ref=ACA}}}, operator=IN}}, right={literal=0}, operator=>}}}}, else={case={clauses={1={then={literal='EAB Web form'}, when={condition={left={function={parameters={1={function={parameters={1={function={parameters={1={column={name=title, table_ref=ACA}}, 2={literal=';'}, 3={calc={left={literal=-1}, right={literal=1}, operator=*}}}, function_name=SPLIT_PART}}}, function_name=TRIM}}}, function_name=LOWER}}, right={function={parameters={1={literal='Email'}}, function_name=LOWER}}, operator==}}}, 2={then={literal='Facebook'}, when={condition={left={function={parameters={1={function={parameters={1={function={parameters={1={column={name=title, table_ref=ACA}}, 2={literal=';'}, 3={calc={left={literal=-1}, right={literal=1}, operator=*}}}, function_name=SPLIT_PART}}}, function_name=TRIM}}}, function_name=LOWER}}, right={function={parameters={1={literal='Facebook'}}, function_name=LOWER}}, operator==}}}, 3={then={literal='LinkedIn'}, when={condition={left={function={parameters={1={function={parameters={1={function={parameters={1={column={name=title, table_ref=ACA}}, 2={literal=';'}, 3={calc={left={literal=-1}, right={literal=1}, operator=*}}}, function_name=SPLIT_PART}}}, function_name=TRIM}}}, function_name=LOWER}}, right={function={parameters={1={literal='LinkedIn'}}, function_name=LOWER}}, operator==}}}, 4={then={literal='Paid Search'}, when={condition={left={function={parameters={1={function={parameters={1={function={parameters={1={column={name=title, table_ref=ACA}}, 2={literal=';'}, 3={calc={left={literal=-1}, right={literal=1}, operator=*}}}, function_name=SPLIT_PART}}}, function_name=TRIM}}}, function_name=LOWER}}, right={function={parameters={1={literal='Paid Search'}}, function_name=LOWER}}, operator==}}}, 5={then={literal='.EDU Web form'}, when={condition={left={function={parameters={1={function={parameters={1={function={parameters={1={column={name=title, table_ref=ACA}}, 2={literal=';'}, 3={calc={left={literal=-1}, right={literal=1}, operator=*}}}, function_name=SPLIT_PART}}}, function_name=TRIM}}}, function_name=LOWER}}, right={function={parameters={1={literal='.EDU Web Form'}}, function_name=LOWER}}, operator==}}}}, else={literal='EAB Web form'}}}}}}, from={table={alias=ACA, table=acs__categories}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[source_type]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{acs__categories={title=[[@7,33:35='ACA',<393>,1:33], [@26,105:107='ACA',<393>,1:105], [@69,238:240='ACA',<393>,1:238], [@112,370:372='ACA',<393>,1:370], [@155,502:504='ACA',<393>,1:502], [@198,640:642='ACA',<393>,1:640], [@242,796:798='ACA',<393>,1:796], [@267,882:884='ACA',<393>,1:882], [@292,967:969='ACA',<393>,1:967], [@317,1052:1054='ACA',<393>,1:1052], [@342,1143:1145='ACA',<393>,1:1143]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{query0={source_type=[[@365,1242:1252='source_type',<393>,1:1242]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query0={query_dictionary={source_type=[[@365,1242:1252='source_type',<393>,1:1242]]}, table_dictionary={acs__categories={title=[[@7,33:35='ACA',<393>,1:33], [@26,105:107='ACA',<393>,1:105], [@69,238:240='ACA',<393>,1:238], [@112,370:372='ACA',<393>,1:370], [@155,502:504='ACA',<393>,1:502], [@198,640:642='ACA',<393>,1:640], [@242,796:798='ACA',<393>,1:796], [@267,882:884='ACA',<393>,1:882], [@292,967:969='ACA',<393>,1:967], [@317,1052:1054='ACA',<393>,1:1052], [@342,1143:1145='ACA',<393>,1:1143]]}}, interface={source_type=[{name=title, table_ref=ACA}]}, table_alias={ACA=acs__categories}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void nestedSearchedCaseIsolationV1Test() {
		final String query = "SELECT CASE WHEN 1 = 1 THEN CASE WHEN 1 = 1 THEN 'a' ELSE 'b' END ELSE 'c' END AS nested_flag"
				+ " FROM tab1";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		assertNoFatalErrors(extractor);

		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={alias=nested_flag, case={clauses={1={then={case={clauses={1={then={literal='a'}, when={condition={left={literal=1}, right={literal=1}, operator==}}}}, else={literal='b'}}}, when={condition={left={literal=1}, right={literal=1}, operator==}}}}, else={literal='c'}}}}, from={table={alias=null, table=tab1}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[nested_flag]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{tab1={}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{query0={nested_flag=[[@21,82:92='nested_flag',<393>,1:82]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query0={query_dictionary={nested_flag=[[@21,82:92='nested_flag',<393>,1:82]]}, table_dictionary={tab1={}}, interface={nested_flag=[]}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void splitPartNegativeIndexInNestedCaseV2Test() {
		final String query = "SELECT CASE WHEN LOWER(TRIM(SPLIT_PART(ACA.title, ';', -1))) = LOWER('Email')"
				+ " THEN 'EAB Web form' ELSE 'Other' END AS source_type"
				+ " FROM acs__categories ACA";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		assertNoFatalErrors(extractor);

		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={alias=source_type, case={clauses={1={then={literal='EAB Web form'}, when={condition={left={function={parameters={1={function={parameters={1={function={parameters={1={column={name=title, table_ref=ACA}}, 2={literal=';'}, 3={calc={left={literal=-1}, right={literal=1}, operator=*}}}, function_name=SPLIT_PART}}}, function_name=TRIM}}}, function_name=LOWER}}, right={function={parameters={1={literal='Email'}}, function_name=LOWER}}, operator==}}}}, else={literal='Other'}}}}, from={table={alias=ACA, table=acs__categories}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[source_type]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{acs__categories={title=[[@9,39:41='ACA',<393>,1:39]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{query0={source_type=[[@31,118:128='source_type',<393>,1:118]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query0={query_dictionary={source_type=[[@31,118:128='source_type',<393>,1:118]]}, table_dictionary={acs__categories={title=[[@9,39:41='ACA',<393>,1:39]]}}, interface={source_type=[{name=title, table_ref=ACA}]}, table_alias={ACA=acs__categories}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void nestedSearchedCaseInWherePlacementTest() {
		final String query = "SELECT col FROM tab1 WHERE " + compactNestedCase() + " = 'y'";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		assertNoFatalErrors(extractor);

		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=col, table_ref=null}}}, from={table={alias=null, table=tab1}}, where={condition={left={case={clauses={1={then={case={clauses={1={then={literal='y'}, when={condition={left={column={name=col, table_ref=null}}, right={literal='a'}, operator==}}}}, else={literal='n'}}}, when={condition={left={literal=1}, right={literal=1}, operator==}}}}, else={literal='z'}}}, right={literal='y'}, operator==}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[col]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{tab1={col=[[@1,7:9='col',<393>,1:7], [@13,58:60='col',<393>,1:58]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{query0={col=[[@1,7:9='col',<393>,1:7]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query0={query_dictionary={col=[[@1,7:9='col',<393>,1:7]]}, table_dictionary={tab1={col=[[@1,7:9='col',<393>,1:7], [@13,58:60='col',<393>,1:58]]}}, filters=[{name=col, table_ref=tab1}], interface={col=[{name=col, table_ref=tab1}]}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void nestedSearchedCaseInJoinOnPlacementTest() {
		final String query = "SELECT t.col FROM tab1 t JOIN tab2 u ON " + compactNestedCaseForJoinOn() + " = 'y'";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		assertNoFatalErrors(extractor);

		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=col, table_ref=t}}}, from={join={1={table={alias=t, table=tab1}}, 2={join=JOIN, on={condition={left={case={clauses={1={then={case={clauses={1={then={literal='y'}, when={condition={left={column={name=col, table_ref=t}}, right={literal='a'}, operator==}}}}, else={literal='n'}}}, when={condition={left={literal=1}, right={literal=1}, operator==}}}}, else={literal='z'}}}, right={literal='y'}, operator==}}}, 3={table={alias=u, table=tab2}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[col]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{tab1={col=[[@1,7:7='t',<393>,1:7], [@19,71:71='t',<393>,1:71]]}, tab2={}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{query0={col=[[@3,9:11='col',<393>,1:9]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query0={query_dictionary={col=[[@3,9:11='col',<393>,1:9]]}, table_dictionary={tab1={col=[[@1,7:7='t',<393>,1:7], [@19,71:71='t',<393>,1:71]]}, tab2={}}, filters=[{name=col, table_ref=t}], interface={col=[{name=col, table_ref=t}]}, table_alias={t=tab1, u=tab2}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void nestedSearchedCaseInHavingPlacementTest() {
		final String query = "SELECT col FROM tab1 GROUP BY col HAVING " + compactNestedCase() + " = 'y'";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		assertNoFatalErrors(extractor);

		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=col, table_ref=null}}}, having={condition={left={case={clauses={1={then={case={clauses={1={then={literal='y'}, when={condition={left={column={name=col, table_ref=null}}, right={literal='a'}, operator==}}}}, else={literal='n'}}}, when={condition={left={literal=1}, right={literal=1}, operator==}}}}, else={literal='z'}}}, right={literal='y'}, operator==}}, from={table={alias=null, table=tab1}}, groupby={1={column={name=col, table_ref=null}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[col]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{tab1={col=[[@1,7:9='col',<393>,1:7], [@6,30:32='col',<393>,1:30], [@16,72:74='col',<393>,1:72]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{query0={col=[[@1,7:9='col',<393>,1:7]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query0={query_dictionary={col=[[@1,7:9='col',<393>,1:7]]}, table_dictionary={tab1={col=[[@1,7:9='col',<393>,1:7], [@6,30:32='col',<393>,1:30], [@16,72:74='col',<393>,1:72]]}}, grouped_by=[{name=col, table_ref=tab1}], filters=[{name=col, table_ref=tab1}], interface={col=[{name=col, table_ref=tab1}]}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void nestedSearchedCaseInUpdateSetPlacementTest() {
		final String query = "UPDATE tab1 t SET flag = " + compactNestedCase() + " WHERE t.col IS NOT NULL";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		assertNoFatalErrors(extractor);

		Assert.assertEquals("AST is wrong",
				"{SQL={update={table={alias=t, table=tab1}, where={condition={left={column={name=col, table_ref=t}}, operator=IS NOT NULL}}, assignments={1={set={column={name=flag, table_ref=null}}, to={case={clauses={1={then={case={clauses={1={then={literal='y'}, when={condition={left={column={name=col, table_ref=null}}, right={literal='a'}, operator==}}}}, else={literal='n'}}}, when={condition={left={literal=1}, right={literal=1}, operator==}}}}, else={literal='z'}}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[flag]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{tab1={col=[[@26,107:107='t',<393>,1:107], [@14,56:58='col',<393>,1:56], [@24,97:99='END',<12>,1:97]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{update0={flag=[[@4,18:21='flag',<393>,1:18]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_update0={assignments={flag=[{name=col, table_ref=tab1}]}, table_dictionary={tab1={col=[[@26,107:107='t',<393>,1:107], [@14,56:58='col',<393>,1:56], [@24,97:99='END',<12>,1:97]]}}, update_dictionary={flag=[[@4,18:21='flag',<393>,1:18]]}, target_table={tab1={flag=[[@4,18:21='flag',<393>,1:18]]}}, filters=[{name=col, table_ref=t}], table_alias={t=tab1}, lhs_unresolved_columns={flag={column={name=flag, table_ref=null}, locations=[[@4,18:21='flag',<393>,1:18]]}}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void productCaseInWherePlacementTest() {
		final String query = "SELECT col FROM acs__categories cat WHERE " + compactProductCase("cat") + " = 'x'";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		assertNoFatalErrors(extractor);

		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=col, table_ref=null}}}, from={table={alias=cat, table=acs__categories}}, where={condition={left={case={clauses={1={then={function={parameters={1={function={parameters={1={function={parameters={1={column={name=title, table_ref=cat}}, 2={literal=';'}, 3={literal=1}}, function_name=SPLIT_PART}}}, function_name=TRIM}}, 2={literal=''}}, function_name=NULLIF}}, when={condition={left={function={function_name=POSITION, parameters={1={literal=';'}, 2={column={name=title, table_ref=cat}}}, operator=IN}}, right={literal=0}, operator=>}}}}, else={null_literal=null}}}, right={literal='x'}, operator==}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[col]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{acs__categories={col=[[@1,7:9='col',<393>,1:7]], title=[[@12,66:68='cat',<393>,1:66], [@25,109:111='cat',<393>,1:109]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{query0={col=[[@1,7:9='col',<393>,1:7]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query0={query_dictionary={col=[[@1,7:9='col',<393>,1:7]]}, table_dictionary={acs__categories={col=[[@1,7:9='col',<393>,1:7]], title=[[@12,66:68='cat',<393>,1:66], [@25,109:111='cat',<393>,1:109]]}}, filters=[{name=title, table_ref=cat}], interface={col=[{name=col, table_ref=acs__categories}]}, table_alias={cat=acs__categories}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void productCaseInHavingPlacementTest() {
		final String query = "SELECT cat.title FROM acs__categories cat GROUP BY cat.title HAVING "
				+ compactProductCase("cat") + " IS NOT NULL";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		assertNoFatalErrors(extractor);

		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=title, table_ref=cat}}}, having={condition={left={case={clauses={1={then={function={parameters={1={function={parameters={1={function={parameters={1={column={name=title, table_ref=cat}}, 2={literal=';'}, 3={literal=1}}, function_name=SPLIT_PART}}}, function_name=TRIM}}, 2={literal=''}}, function_name=NULLIF}}, when={condition={left={function={function_name=POSITION, parameters={1={literal=';'}, 2={column={name=title, table_ref=cat}}}, operator=IN}}, right={literal=0}, operator=>}}}}, else={null_literal=null}}}, operator=IS NOT NULL}}, from={table={alias=cat, table=acs__categories}}, groupby={1={column={name=title, table_ref=cat}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[title]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{acs__categories={title=[[@1,7:9='cat',<393>,1:7], [@9,51:53='cat',<393>,1:51], [@19,92:94='cat',<393>,1:92], [@32,135:137='cat',<393>,1:135]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{query0={title=[[@3,11:15='title',<393>,1:11]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query0={query_dictionary={title=[[@3,11:15='title',<393>,1:11]]}, table_dictionary={acs__categories={title=[[@1,7:9='cat',<393>,1:7], [@9,51:53='cat',<393>,1:51], [@19,92:94='cat',<393>,1:92], [@32,135:137='cat',<393>,1:135]]}}, grouped_by=[{name=title, table_ref=cat}], filters=[{name=title, table_ref=cat}], interface={title=[{name=title, table_ref=cat}]}, table_alias={cat=acs__categories}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void productCaseInUpdateSetPlacementTest() {
		final String query = "UPDATE acs__categories cat SET product = " + compactProductCase("cat")
				+ " WHERE cat.title IS NOT NULL";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		assertNoFatalErrors(extractor);

		Assert.assertEquals("AST is wrong",
				"{SQL={update={table={alias=cat, table=acs__categories}, where={condition={left={column={name=title, table_ref=cat}}, operator=IS NOT NULL}}, assignments={1={set={column={name=product, table_ref=null}}, to={case={clauses={1={then={function={parameters={1={function={parameters={1={function={parameters={1={column={name=title, table_ref=cat}}, 2={literal=';'}, 3={literal=1}}, function_name=SPLIT_PART}}}, function_name=TRIM}}, 2={literal=''}}, function_name=NULLIF}}, when={condition={left={function={function_name=POSITION, parameters={1={literal=';'}, 2={column={name=title, table_ref=cat}}}, operator=IN}}, right={literal=0}, operator=>}}}}, else={null_literal=null}}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[product]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{acs__categories={title=[[@12,65:67='cat',<393>,1:65], [@25,108:110='cat',<393>,1:108], [@41,153:155='cat',<393>,1:153]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{update0={product=[[@4,31:37='product',<393>,1:31]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_update0={assignments={product=[{name=title, table_ref=cat}]}, table_dictionary={acs__categories={title=[[@12,65:67='cat',<393>,1:65], [@25,108:110='cat',<393>,1:108], [@41,153:155='cat',<393>,1:153]]}}, update_dictionary={product=[[@4,31:37='product',<393>,1:31]]}, target_table={acs__categories={product=[[@4,31:37='product',<393>,1:31]]}}, filters=[{name=title, table_ref=cat}], table_alias={cat=acs__categories}, lhs_unresolved_columns={product={column={name=product, table_ref=null}, locations=[[@4,31:37='product',<393>,1:31]]}}}}",
				extractor.getSymbolTable().toString());
	}

}
