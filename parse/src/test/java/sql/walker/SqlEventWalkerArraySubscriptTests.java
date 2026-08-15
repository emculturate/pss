package sql.walker;

import org.junit.Assert;
import org.junit.Test;

import sql.SQLSelectParserParser;

/**
 * Phase 2.6 — postfix array subscript {@code expr[index]} (shared with Phase 5.4).
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
				"{query0={x=[[@11,31:31='x',<392>,1:31]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query0={query_dictionary={x=[[@11,31:31='x',<392>,1:31]]}, interface={x=[]}}}",
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
				"{query0={x=[[@11,33:33='x',<392>,1:33]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query0={query_dictionary={x=[[@11,33:33='x',<392>,1:33]]}, interface={x=[]}}}",
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
				"{tab1={col=[[@3,13:15='col',<392>,1:13]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{query0={x=[[@11,29:29='x',<392>,1:29]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query0={query_dictionary={x=[[@11,29:29='x',<392>,1:29]]}, table_dictionary={tab1={col=[[@3,13:15='col',<392>,1:13]]}}, interface={x=[{name=col, table_ref=tab1}]}}}",
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
				"{tab1={col=[[@3,13:15='col',<392>,1:13]], idx=[[@8,23:25='idx',<392>,1:23]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{query0={x=[[@11,31:31='x',<392>,1:31]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query0={query_dictionary={x=[[@11,31:31='x',<392>,1:31]]}, table_dictionary={tab1={col=[[@3,13:15='col',<392>,1:13]], idx=[[@8,23:25='idx',<392>,1:23]]}}, interface={x=[{name=col, table_ref=tab1}, {name=idx, table_ref=tab1}]}}}",
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
				"{tab1={a=[[@3,13:13='a',<392>,1:13]], b=[[@8,21:24='tab1',<392>,1:21]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{query0={x=[[@13,32:32='x',<392>,1:32]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query0={query_dictionary={x=[[@13,32:32='x',<392>,1:32]]}, table_dictionary={tab1={a=[[@3,13:13='a',<392>,1:13]], b=[[@8,21:24='tab1',<392>,1:21]]}}, interface={x=[{name=a, table_ref=tab1}, {name=b, table_ref=tab1}]}}}",
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
				"{tab1={col=[[@3,13:15='col',<392>,1:13]], idx=[[@9,24:26='idx',<392>,1:24]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{query0={x=[[@13,33:33='x',<392>,1:33]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query0={query_dictionary={x=[[@13,33:33='x',<392>,1:33]]}, table_dictionary={tab1={col=[[@3,13:15='col',<392>,1:13]], idx=[[@9,24:26='idx',<392>,1:24]]}}, interface={x=[{name=col, table_ref=tab1}, {name=idx, table_ref=tab1}]}}}",
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
				"{acs__categories={title=[[@7,31:33='cat',<392>,1:31], [@20,74:76='cat',<392>,1:74]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{query0={product=[[@36,116:122='product',<392>,1:116]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query0={query_dictionary={product=[[@36,116:122='product',<392>,1:116]]}, table_dictionary={acs__categories={title=[[@7,31:33='cat',<392>,1:31], [@20,74:76='cat',<392>,1:74]]}}, interface={product=[{name=title, table_ref=cat}]}, table_alias={cat=acs__categories}}}",
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
				"{acs__categories={title=[[@7,33:35='cat',<392>,1:33], [@26,101:103='cat',<392>,1:101]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{query0={product=[[@58,173:179='product',<392>,1:173]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query0={query_dictionary={product=[[@58,173:179='product',<392>,1:173]]}, table_dictionary={acs__categories={title=[[@7,33:35='cat',<392>,1:33], [@26,101:103='cat',<392>,1:101]]}}, interface={product=[{name=title, table_ref=cat}]}, table_alias={cat=acs__categories}}}",
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
				"{acs__categories={title=[[@7,33:35='cat',<392>,1:33], [@32,135:137='cat',<392>,1:135], [@66,210:212='cat',<392>,1:210], [@79,253:255='cat',<392>,1:253], [@118,450:452='cat',<392>,1:450]]}, ref__standard_value_mapping_alr={eab_std_value=[[@20,74:82='prod_abbr',<392>,1:74]], client_value=[[@106,389:397='prod_abbr',<392>,1:389]], field_name=[[@143,503:511='prod_abbr',<392>,1:503]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{query0={product=[[@95,295:301='product',<392>,1:295]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query0={query_dictionary={product=[[@95,295:301='product',<392>,1:295]]}, table_dictionary={acs__categories={title=[[@7,33:35='cat',<392>,1:33], [@32,135:137='cat',<392>,1:135], [@66,210:212='cat',<392>,1:210], [@79,253:255='cat',<392>,1:253], [@118,450:452='cat',<392>,1:450]]}, ref__standard_value_mapping_alr={eab_std_value=[[@20,74:82='prod_abbr',<392>,1:74]], client_value=[[@106,389:397='prod_abbr',<392>,1:389]], field_name=[[@143,503:511='prod_abbr',<392>,1:503]]}}, filters=[{name=client_value, table_ref=prod_abbr}, {name=title, table_ref=cat}, {name=field_name, table_ref=prod_abbr}], interface={product=[{name=eab_std_value, table_ref=prod_abbr}, {name=title, table_ref=cat}]}, table_alias={cat=acs__categories, prod_abbr=ref__standard_value_mapping_alr}}}",
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
				"{acs__categories={title=[[@7,33:35='ctg',<392>,1:33], [@32,135:137='ctg',<392>,1:135], [@66,210:212='ctg',<392>,1:210], [@79,253:255='ctg',<392>,1:253], [@118,450:452='ctg',<392>,1:450]]}, ref__standard_value_mapping_alr={eab_std_value=[[@20,74:82='prod_abbr',<392>,1:74]], client_value=[[@106,389:397='prod_abbr',<392>,1:389]], field_name=[[@143,503:511='prod_abbr',<392>,1:503]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{query0={product=[[@95,295:301='product',<392>,1:295]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query0={query_dictionary={product=[[@95,295:301='product',<392>,1:295]]}, table_dictionary={acs__categories={title=[[@7,33:35='ctg',<392>,1:33], [@32,135:137='ctg',<392>,1:135], [@66,210:212='ctg',<392>,1:210], [@79,253:255='ctg',<392>,1:253], [@118,450:452='ctg',<392>,1:450]]}, ref__standard_value_mapping_alr={eab_std_value=[[@20,74:82='prod_abbr',<392>,1:74]], client_value=[[@106,389:397='prod_abbr',<392>,1:389]], field_name=[[@143,503:511='prod_abbr',<392>,1:503]]}}, filters=[{name=client_value, table_ref=prod_abbr}, {name=title, table_ref=ctg}, {name=field_name, table_ref=prod_abbr}], interface={product=[{name=eab_std_value, table_ref=prod_abbr}, {name=title, table_ref=ctg}]}, table_alias={ctg=acs__categories, prod_abbr=ref__standard_value_mapping_alr}}}",
				extractor.getSymbolTable().toString());
	}

}
