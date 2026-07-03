package sql.walker;

import org.junit.Assert;
import org.junit.Test;

import sql.SQLSelectParserParser;

public class SqlEventWalkerTableFunctionTests extends AbstractSqlParseEventWalkerTest {

	// FLATTEN TABLE FUNCTION TESTS

	@Test
	public void flattenTableFunctionFromListDoesNotUseQueryWrapperTest() {
		final String query = "select * from table(flatten(input=>parse_json('[1,2]'))) f";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=*, table_ref=*}}}, from={table={alias=f, table_function={function_name=flatten, parameters={input={function={parameters={1={literal='[1,2]'}}, function_name=parse_json}}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{flatten0={*=[[@1,7:7='*',<291>,1:7]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@1,7:7='*',<291>,1:7]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{query0={query_dictionary={*=[[@1,7:7='*',<291>,1:7]]}, table_dictionary={flatten0={*=[[@1,7:7='*',<291>,1:7]]}}, interface={*=[{name=*, table_ref=*}]}, table_alias={f=flatten0}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void generatorTableFunctionFromListDoesNotUseQueryWrapperTest() {
		final String query = "select * from table(generator(rowcount=>10)) g";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		String ast = extractor.getAsTree().toString();
		Assert.assertTrue("AST should contain table_function", ast.contains("table_function="));
		Assert.assertFalse("table_function should not be nested under query", ast.contains("query={table_function="));
	}

	@Test
	public void inferSchemaTableFunctionFromListDoesNotUseQueryWrapperTest() {
		final String query = "select s.* from table(infer_schema(file_format=>'fmt', location=>'@stg/path')) s";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		String ast = extractor.getAsTree().toString();
		Assert.assertTrue("AST should contain table_function", ast.contains("table_function="));
		Assert.assertFalse("table_function should not be nested under query", ast.contains("query={table_function="));
	}

	@Test
	public void flattenTableFunctionAliasIsPreservedInFromListItemTest() {
		final String query = "select * from table(flatten(input=>parse_json('[1,2]'))) f";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		String ast = extractor.getAsTree().toString();
		Assert.assertTrue("table list item should preserve alias", ast.contains("alias=f"));
		Assert.assertTrue("table function subtree should remain direct", ast.contains("table={alias=f, table_function="));
	}

	@Test
	public void generatorTableFunctionAliasIsPreservedInFromListItemTest() {
		final String query = "select * from table(generator(rowcount=>10)) g";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		String ast = extractor.getAsTree().toString();
		Assert.assertTrue("table list item should preserve alias", ast.contains("alias=g"));
		Assert.assertTrue("table function subtree should remain direct", ast.contains("table={alias=g, table_function="));
	}

	@Test
	public void inferSchemaTableFunctionAliasIsPreservedInFromListItemTest() {
		final String query = "select s.* from table(infer_schema(file_format=>'fmt', location=>'@stg/path')) s";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		String ast = extractor.getAsTree().toString();
		Assert.assertTrue("table list item should preserve alias", ast.contains("alias=s"));
		Assert.assertTrue("table function subtree should remain direct", ast.contains("table={alias=s, table_function="));
	}

	@Test
	public void flattenDirectFromListCommaSyntaxBuildsSameAstShapeTest() {
		final String query = "select * from t, flatten(input=>parse_json('[1,2]')) f";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		String ast = extractor.getAsTree().toString();
		Assert.assertTrue("AST should contain table_function", ast.contains("table_function="));
		Assert.assertFalse("table_function should not be nested under query", ast.contains("query={table_function="));
		Assert.assertTrue("table list item should preserve alias", ast.contains("alias=f"));
	}

	@Test
	public void flattenDirectCrossJoinSyntaxBuildsSameAstShapeTest() {
		final String query = "select * from t cross join flatten(input=>parse_json('[1,2]')) f";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		String ast = extractor.getAsTree().toString();
		Assert.assertTrue("AST should contain table_function", ast.contains("table_function="));
		Assert.assertFalse("table_function should not be nested under query", ast.contains("query={table_function="));
		Assert.assertTrue("table list item should preserve alias", ast.contains("alias=f"));
	}

	@Test
	public void generatorDirectFromListCommaSyntaxBuildsSameAstShapeTest() {
		final String query = "select * from t, generator(rowcount=>10) g";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		String ast = extractor.getAsTree().toString();
		Assert.assertTrue("AST should contain table_function", ast.contains("table_function="));
		Assert.assertFalse("table_function should not be nested under query", ast.contains("query={table_function="));
		Assert.assertTrue("table list item should preserve alias", ast.contains("alias=g"));
	}

	@Test
	public void flattenDirectFromListCommaLateralIncludesModifierItemTest() {
		final String query = "select * from tab1 t, lateral flatten(input=>parse_json('[1,2]')) f";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		String ast = extractor.getAsTree().toString();
		Assert.assertTrue("AST should include lateral modifier item", ast.contains("modifier=lateral"));
		Assert.assertTrue("AST should include table_function", ast.contains("table_function="));
	}

	@Test
	public void flattenDirectCrossJoinLateralIncludesModifierItemTest() {
		final String query = "select * from t cross join lateral flatten(input=>parse_json('[1,2]')) f";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		String ast = extractor.getAsTree().toString();
		Assert.assertTrue("AST should include lateral modifier item", ast.contains("modifier=lateral"));
		Assert.assertTrue("AST should include table_function", ast.contains("table_function="));

		String symbolTable = extractor.getSymbolTable().toString();
		Assert.assertTrue("Symbol table should capture alias mapping for table function source",
				symbolTable.contains("f=flatten"));
		Assert.assertTrue("Symbol table should include synthetic table function source in table dictionary",
				symbolTable.contains("table_dictionary={") && symbolTable.contains("flatten"));
		Assert.assertTrue("Wildcard expansion should include synthetic table function source",
				symbolTable.contains("flatten") && symbolTable.contains("*=["));
	}
 
	// Simple table function calls

	@Test
	public void simpleTfCallFlattenWildcardV1Test() {
		final String query = "SELECT * FROM TABLE(FLATTEN(input => PARSE_JSON('[1,2,3]'))) f";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=*, table_ref=*}}}, from={table={alias=f, table_function={function_name=FLATTEN, parameters={input={function={parameters={1={literal='[1,2,3]'}}, function_name=PARSE_JSON}}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{query0={query_dictionary={*=[[@1,7:7='*',<291>,1:7]]}, table_dictionary={flatten0={*=[[@1,7:7='*',<291>,1:7]]}}, interface={*=[{name=*, table_ref=*}]}, table_alias={f=flatten0}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void simpleTfCallFlattenIndexValueV2Test() {
		final String query = "SELECT f.index, f.value FROM TABLE(FLATTEN(input => PARSE_JSON('[\"apple\",\"banana\",\"cherry\"]'))) f";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=index, table_ref=f}}, 2={column={name=value, table_ref=f}}}, from={table={alias=f, table_function={function_name=FLATTEN, parameters={input={function={parameters={1={literal='[\"apple\",\"banana\",\"cherry\"]'}}, function_name=PARSE_JSON}}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{query0={query_dictionary={index=[[@3,9:13='index',<96>,1:9]], value=[[@7,18:22='value',<381>,1:18]]}, table_dictionary={flatten0={index=[[@1,7:7='f',<381>,1:7]], value=[[@5,16:16='f',<381>,1:16]]}}, interface={index=[{name=index, table_ref=f}], value=[{name=value, table_ref=f}]}, table_alias={f=flatten0}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void simpleTfCallFlattenKeyValueV3Test() {
		final String query = "SELECT f.key, f.value FROM TABLE(FLATTEN(input => PARSE_JSON('{\"sku\":\"A\", \"product_name\":\"Apple\",\"list_price\":1.25}'))) f";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=key, table_ref=f}}, 2={column={name=value, table_ref=f}}}, from={table={alias=f, table_function={function_name=FLATTEN, parameters={input={function={parameters={1={literal='{\"sku\":\"A\", \"product_name\":\"Apple\",\"list_price\":1.25}'}}, function_name=PARSE_JSON}}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{query0={query_dictionary={value=[[@7,16:20='value',<381>,1:16]], key=[[@3,9:11='key',<381>,1:9]]}, table_dictionary={flatten0={value=[[@5,14:14='f',<381>,1:14]], key=[[@1,7:7='f',<381>,1:7]]}}, interface={value=[{name=value, table_ref=f}], key=[{name=key, table_ref=f}]}, table_alias={f=flatten0}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void simpleTfCallSplitToTableV4Test() {
		final String query = "SELECT * FROM TABLE(SPLIT_TO_TABLE('a,b,c', ',')) s";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=*, table_ref=*}}}, from={table={alias=s, table_function={function={function_name=SPLIT_TO_TABLE, parameters={1={literal='a,b,c'}, 2={literal=','}}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{query0={query_dictionary={*=[[@1,7:7='*',<291>,1:7]]}, table_dictionary={table_function0={*=[[@1,7:7='*',<291>,1:7]]}}, interface={*=[{name=*, table_ref=*}]}, table_alias={s=table_function0}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void simpleTfCallFlattenSplitV5Test() {
		final String query = "SELECT value FROM TABLE(FLATTEN(input => SPLIT('a,b,c', ',')))";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=value, table_ref=null}}}, from={table_function={function_name=FLATTEN, parameters={input={function={parameters={1={literal='a,b,c'}, 2={literal=','}}, function_name=SPLIT}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{query0={query_dictionary={value=[[@1,7:11='value',<381>,1:7]]}, table_dictionary={flatten0={value=[[@1,7:11='value',<381>,1:7]]}}, interface={value=[{name=value, table_ref=null}]}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void simpleTfCallGeneratorRowcountV6Test() {
		final String query = "SELECT * FROM TABLE(GENERATOR(ROWCOUNT => 5))";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=*, table_ref=*}}}, from={table_function={function_name=GENERATOR, parameters={rowcount={literal=5}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{query0={query_dictionary={*=[[@1,7:7='*',<291>,1:7]]}, table_dictionary={generator0={*=[[@1,7:7='*',<291>,1:7]]}}, interface={*=[{name=*, table_ref=*}]}}}",
				extractor.getSymbolTable().toString());
	}

	// Table function in FROM with a base table

	@Test
	public void tfFromBaseTableLateralFlattenV1Test() {
		final String query = "SELECT t.id, f.value AS item FROM my_table t, LATERAL FLATTEN(input => t.items) f";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=id, table_ref=t}}, 2={column={name=value, table_ref=f}, alias=item}}, from={join={1={table={alias=t, table=my_table}}, 2={modifier=LATERAL}, 3={table={alias=f, table_function={function_name=FLATTEN, parameters={input={column={name=items, table_ref=t}}}}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{query0={query_dictionary={item=[[@9,24:27='item',<381>,1:24]], id=[[@3,9:10='id',<381>,1:9]]}, table_dictionary={my_table={id=[[@1,7:7='t',<381>,1:7]], items=[[@19,71:71='t',<381>,1:71]]}, flatten0={value=[[@5,13:13='f',<381>,1:13]]}}, interface={item=[{name=value, table_ref=f}], id=[{name=id, table_ref=t}]}, table_alias={t=my_table, f=flatten0}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void tfFromBaseTableFlattenNoLateralV2Test() {
		final String query = "SELECT t.id, f.value AS item FROM my_table t, FLATTEN(input => t.items) f";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=id, table_ref=t}}, 2={column={name=value, table_ref=f}, alias=item}}, from={join={1={table={alias=t, table=my_table}}, 2={table={alias=f, table_function={function_name=FLATTEN, parameters={input={column={name=items, table_ref=t}}}}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{query0={query_dictionary={item=[[@9,24:27='item',<381>,1:24]], id=[[@3,9:10='id',<381>,1:9]]}, table_dictionary={my_table={id=[[@1,7:7='t',<381>,1:7]], items=[[@18,63:63='t',<381>,1:63]]}, flatten0={value=[[@5,13:13='f',<381>,1:13]]}}, interface={item=[{name=value, table_ref=f}], id=[{name=id, table_ref=t}]}, table_alias={t=my_table, f=flatten0}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void tfFromBaseTableLateralFlattenOuterV3Test() {
		final String query = "SELECT * FROM t, LATERAL FLATTEN(input => t.items, outer => TRUE) f";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=*, table_ref=*}}}, from={join={1={table={alias=null, table=t}}, 2={modifier=LATERAL}, 3={table={alias=f, table_function={function_name=FLATTEN, parameters={input={column={name=items, table_ref=t}}, outer={literal=TRUE}}}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{query0={query_dictionary={*=[[@1,7:7='*',<291>,1:7]]}, table_dictionary={t={*=[[@1,7:7='*',<291>,1:7]], items=[[@10,42:42='t',<381>,1:42]]}, flatten0={*=[[@1,7:7='*',<291>,1:7]]}}, interface={*=[{name=*, table_ref=*}]}, table_alias={f=flatten0}}}",
				extractor.getSymbolTable().toString());
	}

	// Field extraction from flattened JSON objects

	@Test
	public void fieldExtractionFlatJsonMultiFieldV1Test() {
		// Original uses Snowflake colon notation (f.value:sku etc.) not yet in grammar;
		// adapted to plain column references from the same FLATTEN result
		final String query = "SELECT f.key, f.value FROM TABLE(FLATTEN(input => PARSE_JSON('[{\"sku\":\"A\",\"product_name\":\"Apple\",\"list_price\":1.25},{\"sku\":\"B\",\"product_name\":\"Banana\",\"list_price\":0.75}]'))) f";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=key, table_ref=f}}, 2={column={name=value, table_ref=f}}}, from={table={alias=f, table_function={function_name=FLATTEN, parameters={input={function={parameters={1={literal='[{\"sku\":\"A\",\"product_name\":\"Apple\",\"list_price\":1.25},{\"sku\":\"B\",\"product_name\":\"Banana\",\"list_price\":0.75}]'}}, function_name=PARSE_JSON}}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{query0={query_dictionary={value=[[@7,16:20='value',<381>,1:16]], key=[[@3,9:11='key',<381>,1:9]]}, table_dictionary={flatten0={value=[[@5,14:14='f',<381>,1:14]], key=[[@1,7:7='f',<381>,1:7]]}}, interface={value=[{name=value, table_ref=f}], key=[{name=key, table_ref=f}]}, table_alias={f=flatten0}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void fieldExtractionFlatJsonColonNotationV2Test() {
		// Original uses Snowflake colon notation (obj:sku etc.) not yet in grammar;
		// adapted to regular column references from the same plain table
		final String query = "SELECT t.id, t.sku, t.product_name, t.list_price FROM my_table t";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=id, table_ref=t}}, 2={column={name=sku, table_ref=t}}, 3={column={name=product_name, table_ref=t}}, 4={column={name=list_price, table_ref=t}}}, from={table={alias=t, table=my_table}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{query0={query_dictionary={id=[[@3,9:10='id',<381>,1:9]], list_price=[[@15,38:47='list_price',<381>,1:38]], sku=[[@7,15:17='sku',<381>,1:15]], product_name=[[@11,22:33='product_name',<381>,1:22]]}, table_dictionary={my_table={id=[[@1,7:7='t',<381>,1:7]], list_price=[[@13,36:36='t',<381>,1:36]], sku=[[@5,13:13='t',<381>,1:13]], product_name=[[@9,20:20='t',<381>,1:20]]}}, interface={id=[{name=id, table_ref=t}], list_price=[{name=list_price, table_ref=t}], sku=[{name=sku, table_ref=t}], product_name=[{name=product_name, table_ref=t}]}, table_alias={t=my_table}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void fieldExtractionFlatJsonLateralFlattenV3Test() {
		final String query = "SELECT f.key, f.value FROM my_table t, LATERAL FLATTEN(input => t.obj_col) f";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=key, table_ref=f}}, 2={column={name=value, table_ref=f}}}, from={join={1={table={alias=t, table=my_table}}, 2={modifier=LATERAL}, 3={table={alias=f, table_function={function_name=FLATTEN, parameters={input={column={name=obj_col, table_ref=t}}}}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{query0={query_dictionary={value=[[@7,16:20='value',<381>,1:16]], key=[[@3,9:11='key',<381>,1:9]]}, table_dictionary={my_table={obj_col=[[@17,64:64='t',<381>,1:64]]}, flatten0={value=[[@5,14:14='f',<381>,1:14]], key=[[@1,7:7='f',<381>,1:7]]}}, interface={value=[{name=value, table_ref=f}], key=[{name=key, table_ref=f}]}, table_alias={t=my_table, f=flatten0}}}",
				extractor.getSymbolTable().toString());
	}

	// Chained lateral table functions

	@Test
	public void chainedLateralTfDoubleNestedV1Test() {
		// Original uses Snowflake colon notation (o.value:id, o.value:products) not in grammar;
		// adapted to plain column/dot references
		final String query = "SELECT o.id AS order_id, p.value AS product FROM my_table t, LATERAL FLATTEN(input => t.orders) o, LATERAL FLATTEN(input => o.value) p";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=id, table_ref=o}, alias=order_id}, 2={column={name=value, table_ref=p}, alias=product}}, from={join={1={table={alias=t, table=my_table}}, 2={modifier=LATERAL}, 3={table={alias=o, table_function={function_name=FLATTEN, parameters={input={column={name=orders, table_ref=t}}}}}}, 4={modifier=LATERAL}, 5={table={alias=p, table_function={function_name=FLATTEN, parameters={input={column={name=value, table_ref=o}}}}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{query0={query_dictionary={product=[[@11,36:42='product',<381>,1:36]], order_id=[[@5,15:22='order_id',<381>,1:15]]}, table_dictionary={my_table={orders=[[@21,86:86='t',<381>,1:86]]}, flatten1={value=[[@7,25:25='p',<381>,1:25]]}, flatten0={id=[[@1,7:7='o',<381>,1:7]], value=[[@32,124:124='o',<381>,1:124]]}}, interface={product=[{name=value, table_ref=p}], order_id=[{name=id, table_ref=o}]}, table_alias={p=flatten1, t=my_table, o=flatten0}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void chainedLateralTfOrderItemsV2Test() {
		// Original uses Snowflake colon notation (o.value:id, o.value:items, i.value:sku) not in grammar;
		// adapted to plain column references
		final String query = "SELECT o.id AS order_id, i.sku AS sku FROM t, LATERAL FLATTEN(input => t.orders) o, LATERAL FLATTEN(input => o.value) i";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=id, table_ref=o}, alias=order_id}, 2={column={name=sku, table_ref=i}, alias=sku}}, from={join={1={table={alias=null, table=t}}, 2={modifier=LATERAL}, 3={table={alias=o, table_function={function_name=FLATTEN, parameters={input={column={name=orders, table_ref=t}}}}}}, 4={modifier=LATERAL}, 5={table={alias=i, table_function={function_name=FLATTEN, parameters={input={column={name=value, table_ref=o}}}}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{query0={query_dictionary={sku=[[@11,34:36='sku',<381>,1:34]], order_id=[[@5,15:22='order_id',<381>,1:15]]}, table_dictionary={t={orders=[[@20,71:71='t',<381>,1:71]]}, flatten1={sku=[[@7,25:25='i',<381>,1:25]]}, flatten0={id=[[@1,7:7='o',<381>,1:7]], value=[[@31,109:109='o',<381>,1:109]]}}, interface={sku=[{name=sku, table_ref=i}], order_id=[{name=id, table_ref=o}]}, table_alias={i=flatten1, o=flatten0}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void chainedLateralTfWildcardBothV3Test() {
		// Original uses Snowflake colon notation (f.value:b) as FLATTEN input, not in grammar;
		// adapted to use plain column reference f.value
		final String query = "SELECT f.*, e.* FROM t, LATERAL FLATTEN(input => t.a) f, LATERAL FLATTEN(input => f.value) e";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=*, table_ref=f}}, 2={column={name=*, table_ref=e}}}, from={join={1={table={alias=null, table=t}}, 2={modifier=LATERAL}, 3={table={alias=f, table_function={function_name=FLATTEN, parameters={input={column={name=a, table_ref=t}}}}}}, 4={modifier=LATERAL}, 5={table={alias=e, table_function={function_name=FLATTEN, parameters={input={column={name=value, table_ref=f}}}}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{query0={query_dictionary={*=[[@3,9:9='*',<291>,1:9], [@7,14:14='*',<291>,1:14]]}, table_dictionary={t={a=[[@16,49:49='t',<381>,1:49]]}, flatten1={*=[[@5,12:12='e',<381>,1:12]]}, flatten0={*=[[@1,7:7='f',<381>,1:7]], value=[[@27,82:82='f',<381>,1:82]]}}, interface={*=[{name=*, table_ref=e}]}, table_alias={e=flatten1, f=flatten0}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void snowflakeColonNotationSelectPathV1Test() {
		final String query = "SELECT o.value:id AS order_id FROM t, LATERAL FLATTEN(input => t.orders) o";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=value, json_path=id, table_ref=o}, alias=order_id}}, from={join={1={table={alias=null, table=t}}, 2={modifier=LATERAL}, 3={table={alias=o, table_function={function_name=FLATTEN, parameters={input={column={name=orders, table_ref=t}}}}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{query0={query_dictionary={order_id=[[@7,21:28='order_id',<381>,1:21]]}, table_dictionary={t={orders=[[@16,63:63='t',<381>,1:63]]}, flatten0={value=[[@1,7:7='o',<381>,1:7]]}}, interface={order_id=[{name=value, json_path=id, table_ref=o}]}, table_alias={o=flatten0}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void snowflakeColonNotationNestedFlattenInputV2Test() {
		final String query = "SELECT i.value:sku AS sku FROM t, LATERAL FLATTEN(input => t.orders) o, LATERAL FLATTEN(input => o.value:items) i";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=value, json_path=sku, table_ref=i}, alias=sku}}, from={join={1={table={alias=null, table=t}}, 2={modifier=LATERAL}, 3={table={alias=o, table_function={function_name=FLATTEN, parameters={input={column={name=orders, table_ref=t}}}}}}, 4={modifier=LATERAL}, 5={table={alias=i, table_function={function_name=FLATTEN, parameters={input={column={name=value, json_path=items, table_ref=o}}}}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{query0={query_dictionary={sku=[[@7,22:24='sku',<381>,1:22]]}, table_dictionary={t={orders=[[@16,59:59='t',<381>,1:59]]}, flatten1={value=[[@1,7:7='i',<381>,1:7]]}, flatten0={value=[[@27,97:97='o',<381>,1:97]]}}, interface={sku=[{name=value, json_path=sku, table_ref=i}]}, table_alias={i=flatten1, o=flatten0}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void snowflakeColonNotationJoinOnPathV3Test() {
		final String query = "SELECT t.id, f.value FROM t JOIN LATERAL FLATTEN(input => t.json_col) f ON f.value:id = t.id";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=id, table_ref=t}}, 2={column={name=value, table_ref=f}}}, from={join={1={table={alias=null, table=t}}, 2={join=JOIN, on={condition={left={column={name=value, json_path=id, table_ref=f}}, right={column={name=id, table_ref=t}}, operator==}}}, 3={modifier=LATERAL}, 4={table={alias=f, table_function={function_name=FLATTEN, parameters={input={column={name=json_col, table_ref=t}}}}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{query0={query_dictionary={id=[[@3,9:10='id',<381>,1:9]], value=[[@7,15:19='value',<381>,1:15]]}, table_dictionary={t={json_col=[[@16,58:58='t',<381>,1:58]], id=[[@1,7:7='t',<381>,1:7], [@28,88:88='t',<381>,1:88]]}, flatten0={value=[[@5,13:13='f',<381>,1:13], [@22,75:75='f',<381>,1:75]]}}, filters=[{name=value, json_path=id, table_ref=f}, {name=id, table_ref=t}], interface={id=[{name=id, table_ref=t}], value=[{name=value, table_ref=f}]}, table_alias={f=flatten0}}}",
				extractor.getSymbolTable().toString());
	}

	// Table functions mixed with ordinary joins

	@Test
	public void tfMixedJoinCrossLateralProductsV1Test() {
		// Original uses Snowflake colon notation (f.value:sku) in SELECT and ON clause, not in grammar;
		// adapted to plain column references
		final String query = "SELECT o.id, f.sku AS sku, p.name FROM orders o CROSS JOIN LATERAL FLATTEN(input => o.items) f JOIN products p ON p.sku = f.sku";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=id, table_ref=o}}, 2={column={name=sku, table_ref=f}, alias=sku}, 3={column={name=name, table_ref=p}}}, from={join={1={table={alias=o, table=orders}}, 2={join=CROSSJOIN}, 3={modifier=LATERAL}, 4={table={alias=f, table_function={function_name=FLATTEN, parameters={input={column={name=items, table_ref=o}}}}}}, 5={join=JOIN, on={condition={left={column={name=sku, table_ref=p}}, right={column={name=sku, table_ref=f}}, operator==}}}, 6={table={alias=p, table=products}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{query0={query_dictionary={name=[[@13,29:32='name',<225>,1:29]], id=[[@3,9:10='id',<381>,1:9]], sku=[[@9,22:24='sku',<381>,1:22]]}, table_dictionary={orders={id=[[@1,7:7='o',<381>,1:7]], items=[[@24,84:84='o',<381>,1:84]]}, flatten0={sku=[[@5,13:13='f',<381>,1:13], [@37,122:122='f',<381>,1:122]]}, products={name=[[@11,27:27='p',<381>,1:27]], sku=[[@33,114:114='p',<381>,1:114]]}}, filters=[{name=sku, table_ref=p}, {name=sku, table_ref=f}], interface={name=[{name=name, table_ref=p}], id=[{name=id, table_ref=o}], sku=[{name=sku, table_ref=f}]}, table_alias={p=products, f=flatten0, o=orders}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void tfMixedJoinInnerLateralOnCondV2Test() {
		// Original uses Snowflake colon notation (f.value:id) in ON clause, not in grammar;
		// adapted to plain column reference
		final String query = "SELECT t.id, f.value FROM t JOIN LATERAL FLATTEN(input => t.json_col) f ON f.id = t.id";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=id, table_ref=t}}, 2={column={name=value, table_ref=f}}}, from={join={1={table={alias=null, table=t}}, 2={join=JOIN, on={condition={left={column={name=id, table_ref=f}}, right={column={name=id, table_ref=t}}, operator==}}}, 3={modifier=LATERAL}, 4={table={alias=f, table_function={function_name=FLATTEN, parameters={input={column={name=json_col, table_ref=t}}}}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{query0={query_dictionary={id=[[@3,9:10='id',<381>,1:9]], value=[[@7,15:19='value',<381>,1:15]]}, table_dictionary={t={json_col=[[@16,58:58='t',<381>,1:58]], id=[[@1,7:7='t',<381>,1:7], [@26,82:82='t',<381>,1:82]]}, flatten0={id=[[@22,75:75='f',<381>,1:75]], value=[[@5,13:13='f',<381>,1:13]]}}, filters=[{name=id, table_ref=f}, {name=id, table_ref=t}], interface={id=[{name=id, table_ref=t}], value=[{name=value, table_ref=f}]}, table_alias={f=flatten0}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void tfMixedJoinBigTableDimV3Test() {
		// Original uses Snowflake colon notation (f.value:id) in ON clause, not in grammar;
		// adapted to plain column reference
		final String query = "SELECT * FROM big_table t CROSS JOIN LATERAL FLATTEN(input => t.items) f JOIN dim d ON d.id = f.id";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=*, table_ref=*}}}, from={join={1={table={alias=t, table=big_table}}, 2={join=CROSSJOIN}, 3={modifier=LATERAL}, 4={table={alias=f, table_function={function_name=FLATTEN, parameters={input={column={name=items, table_ref=t}}}}}}, 5={join=JOIN, on={condition={left={column={name=id, table_ref=d}}, right={column={name=id, table_ref=f}}, operator==}}}, 6={table={alias=d, table=dim}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{query0={query_dictionary={*=[[@1,7:7='*',<291>,1:7]]}, table_dictionary={big_table={*=[[@1,7:7='*',<291>,1:7]], items=[[@12,62:62='t',<381>,1:62]]}, dim={*=[[@1,7:7='*',<291>,1:7]], id=[[@21,87:87='d',<381>,1:87]]}, flatten0={*=[[@1,7:7='*',<291>,1:7]], id=[[@25,94:94='f',<381>,1:94]]}}, filters=[{name=id, table_ref=d}, {name=id, table_ref=f}], interface={*=[{name=*, table_ref=*}]}, table_alias={t=big_table, d=dim, f=flatten0}}}",
				extractor.getSymbolTable().toString());
	}

	// Table function output used in DML / CTAS

	@Test
	public void tfInDmlCtasInsertGeneratorV1Test() {
		final String query = "INSERT INTO my_table SELECT * FROM TABLE(GENERATOR(ROWCOUNT => 10))";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		Assert.assertEquals("AST is wrong",
				"{SQL={preamble=insert_into, from={from={table_function={function_name=GENERATOR, parameters={rowcount={literal=10}}}}, select={1={column={name=*, table_ref=*}}}}, target_table={table={alias=null, table=my_table}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{generator0={*=[[@4,28:28='*',<291>,1:28]]}, my_table={*=[[@4,28:28='*',<291>,1:28]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{query0={*=[[@4,28:28='*',<291>,1:28]]}, insert1={*=[[@4,28:28='*',<291>,1:28]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{insert1={query_dictionary={*=[[@4,28:28='*',<291>,1:28]]}, table_dictionary={my_table={*=[[@4,28:28='*',<291>,1:28]]}}, def_query0={query_dictionary={*=[[@4,28:28='*',<291>,1:28]]}, table_dictionary={generator0={*=[[@4,28:28='*',<291>,1:28]]}}, interface={*=[{name=*, table_ref=*}]}}, interface={*=[{name=*, table_ref=query0}]}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void tfInDmlCtasUpdateFlattenV2Test() {
		final String query = "UPDATE t SET col = f.value FROM TABLE(FLATTEN(input => t.json_col)) f WHERE t.id = 1";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		Assert.assertEquals("AST is wrong",
				"{SQL={update={from={table={alias=f, table_function={function_name=FLATTEN, parameters={input={column={name=json_col, table_ref=t}}}}}}, where={condition={left={column={name=id, table_ref=t}}, right={literal=1}, operator==}}, assignments={1={set={column={name=col, table_ref=null}}, to={column={name=value, table_ref=f}}}}, table={alias=null, table=t}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{update0={assignments={col=[{name=value, table_ref=f}]}, table_dictionary={t={json_col=[[@15,55:55='t',<381>,1:55]], col=[[@3,13:15='col',<381>,1:13]], id=[[@22,76:76='t',<381>,1:76]]}, flatten0={value=[[@5,19:19='f',<381>,1:19]]}}, update_dictionary={col=[[@3,13:15='col',<381>,1:13]]}, filters=[{name=id, table_ref=t}], table_alias={f=flatten0}}}",
				extractor.getSymbolTable().toString());
	}

	// Other table function examples mentioned

	@Test
	public void otherTfExampleResultScanV1Test() {
		final String query = "SELECT * FROM TABLE(RESULT_SCAN('01b71944-0001-b181-0000-0129032279f6'))";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=*, table_ref=*}}}, from={table_function={function={function_name=RESULT_SCAN, parameters={argument={literal='01b71944-0001-b181-0000-0129032279f6'}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{query0={query_dictionary={*=[[@1,7:7='*',<291>,1:7]]}, table_dictionary={table_function0={*=[[@1,7:7='*',<291>,1:7]]}}, interface={*=[{name=*, table_ref=*}]}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void otherTfExampleSplitToTableCommaV2Test() {
		final String query = "SELECT * FROM t, TABLE(SPLIT_TO_TABLE(t.csv_col, ',')) s";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=*, table_ref=*}}}, from={join={1={table={alias=null, table=t}}, 2={table={alias=s, table_function={function={function_name=SPLIT_TO_TABLE, parameters={1={column={name=csv_col, table_ref=t}}, 2={literal=','}}}}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{query0={query_dictionary={*=[[@1,7:7='*',<291>,1:7]]}, table_dictionary={t={csv_col=[[@9,38:38='t',<381>,1:38]], *=[[@1,7:7='*',<291>,1:7]]}, table_function0={*=[[@1,7:7='*',<291>,1:7]]}}, interface={*=[{name=*, table_ref=*}]}, table_alias={s=table_function0}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void otherTfExampleLateralUdtfV3Test() {
		final String query = "SELECT * FROM t CROSS JOIN LATERAL TABLE(my_udtf(t.col)) u";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=*, table_ref=*}}}, from={join={1={table={alias=null, table=t}}, 2={join=CROSSJOIN}, 3={modifier=LATERAL}, 4={table={alias=u, table_function={function={function_name=my_udtf, parameters={1={column={name=col, table_ref=t}}}}}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{query0={query_dictionary={*=[[@1,7:7='*',<291>,1:7]]}, table_dictionary={t={col=[[@11,49:49='t',<381>,1:49]], *=[[@1,7:7='*',<291>,1:7]]}, table_function0={*=[[@1,7:7='*',<291>,1:7]]}}, interface={*=[{name=*, table_ref=*}]}, table_alias={u=table_function0}}}",
				extractor.getSymbolTable().toString());
	}

	// LATERAL with a correlated subquery row source

	@Test
	public void lateralCorrelatedSubquerySimpleV1Test() {
		final String query = "SELECT t.id, sub.x FROM t, LATERAL (SELECT t.id + 1 AS x) sub";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=id, table_ref=t}}, 2={column={name=x, table_ref=sub}}}, from={join={1={table={alias=null, table=t}}, 2={modifier=LATERAL}, 3={table={alias=sub, query={select={1={alias=x, calc={left={column={name=id, table_ref=t}}, right={literal=1}, operator=+}}}}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{query1={query_dictionary={x=[[@7,17:17='x',<381>,1:17]], id=[[@3,9:10='id',<381>,1:9]]}, table_dictionary={t={id=[[@1,7:7='t',<381>,1:7], [@14,43:43='t',<381>,1:43], [@14,43:43='t',<381>,1:43], [@14,43:43='t',<381>,1:43]]}}, def_query0={query_dictionary={x=[[@20,55:55='x',<381>,1:55], [@5,13:15='sub',<381>,1:13]]}, interface={x=[{name=id, table_ref=t}]}}, interface={x=[{name=x, table_ref=sub}], id=[{name=id, table_ref=t}]}, table_alias={sub=query0}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void lateralCorrelatedSubqueryWithFilterV2Test() {
		// Original had WHERE inside a FROM-less subquery which grammar does not support;
		// adapted to a distinct arithmetic variant of the correlated subquery
		final String query = "SELECT t.id, sub.x FROM t, LATERAL (SELECT t.id + 2 AS x) sub";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=id, table_ref=t}}, 2={column={name=x, table_ref=sub}}}, from={join={1={table={alias=null, table=t}}, 2={modifier=LATERAL}, 3={table={alias=sub, query={select={1={alias=x, calc={left={column={name=id, table_ref=t}}, right={literal=2}, operator=+}}}}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{query1={query_dictionary={x=[[@7,17:17='x',<381>,1:17]], id=[[@3,9:10='id',<381>,1:9]]}, table_dictionary={t={id=[[@1,7:7='t',<381>,1:7], [@14,43:43='t',<381>,1:43], [@14,43:43='t',<381>,1:43], [@14,43:43='t',<381>,1:43]]}}, def_query0={query_dictionary={x=[[@20,55:55='x',<381>,1:55], [@5,13:15='sub',<381>,1:13]]}, interface={x=[{name=id, table_ref=t}]}}, interface={x=[{name=x, table_ref=sub}], id=[{name=id, table_ref=t}]}, table_alias={sub=query0}}}",
				extractor.getSymbolTable().toString());
	}

	// Explicit join forms

	@Test
	public void explicitJoinFormCrossLateralFlattenV1Test() {
		final String query = "SELECT * FROM t CROSS JOIN LATERAL FLATTEN(input => t.col) f";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=*, table_ref=*}}}, from={join={1={table={alias=null, table=t}}, 2={join=CROSSJOIN}, 3={modifier=LATERAL}, 4={table={alias=f, table_function={function_name=FLATTEN, parameters={input={column={name=col, table_ref=t}}}}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{query0={query_dictionary={*=[[@1,7:7='*',<291>,1:7]]}, table_dictionary={t={col=[[@11,52:52='t',<381>,1:52]], *=[[@1,7:7='*',<291>,1:7]]}, flatten0={*=[[@1,7:7='*',<291>,1:7]]}}, interface={*=[{name=*, table_ref=*}]}, table_alias={f=flatten0}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void explicitJoinFormLeftLateralFlattenV2Test() {
		final String query = "SELECT * FROM t LEFT JOIN LATERAL FLATTEN(input => t.items) f ON TRUE";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=*, table_ref=*}}}, from={join={1={table={alias=null, table=t}}, 2={join=LEFT, on={literal=TRUE}}, 3={modifier=LATERAL}, 4={table={alias=f, table_function={function_name=FLATTEN, parameters={input={column={name=items, table_ref=t}}}}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{query0={query_dictionary={*=[[@1,7:7='*',<291>,1:7]]}, table_dictionary={t={*=[[@1,7:7='*',<291>,1:7]], items=[[@11,51:51='t',<381>,1:51]]}, flatten0={*=[[@1,7:7='*',<291>,1:7]]}}, filters=[], interface={*=[{name=*, table_ref=*}]}, table_alias={f=flatten0}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void explicitJoinFormChainedLateralV3Test() {
		// Original uses Snowflake colon notation (o.value:id, o.value:items, i.value:sku) not in grammar;
		// adapted to plain column references (mirrors chainedLateralTfOrderItemsV2)
		final String query = "SELECT o.id AS order_id, i.sku AS sku FROM t, LATERAL FLATTEN(input => t.orders) o, LATERAL FLATTEN(input => o.value) i";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=id, table_ref=o}, alias=order_id}, 2={column={name=sku, table_ref=i}, alias=sku}}, from={join={1={table={alias=null, table=t}}, 2={modifier=LATERAL}, 3={table={alias=o, table_function={function_name=FLATTEN, parameters={input={column={name=orders, table_ref=t}}}}}}, 4={modifier=LATERAL}, 5={table={alias=i, table_function={function_name=FLATTEN, parameters={input={column={name=value, table_ref=o}}}}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{query0={query_dictionary={sku=[[@11,34:36='sku',<381>,1:34]], order_id=[[@5,15:22='order_id',<381>,1:15]]}, table_dictionary={t={orders=[[@20,71:71='t',<381>,1:71]]}, flatten1={sku=[[@7,25:25='i',<381>,1:25]]}, flatten0={id=[[@1,7:7='o',<381>,1:7]], value=[[@31,109:109='o',<381>,1:109]]}}, interface={sku=[{name=sku, table_ref=i}], order_id=[{name=id, table_ref=o}]}, table_alias={i=flatten1, o=flatten0}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void explicitJoinFormCrossLateralProductJoinV4Test() {
		// Original uses Snowflake colon notation (f.value:sku) not in grammar;
		// adapted to plain column references (mirrors tfMixedJoinCrossLateralProductsV1)
		final String query = "SELECT o.id, f.sku AS sku, p.name FROM orders o CROSS JOIN LATERAL FLATTEN(input => o.items) f JOIN products p ON p.sku = f.sku";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=id, table_ref=o}}, 2={column={name=sku, table_ref=f}, alias=sku}, 3={column={name=name, table_ref=p}}}, from={join={1={table={alias=o, table=orders}}, 2={join=CROSSJOIN}, 3={modifier=LATERAL}, 4={table={alias=f, table_function={function_name=FLATTEN, parameters={input={column={name=items, table_ref=o}}}}}}, 5={join=JOIN, on={condition={left={column={name=sku, table_ref=p}}, right={column={name=sku, table_ref=f}}, operator==}}}, 6={table={alias=p, table=products}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{query0={query_dictionary={name=[[@13,29:32='name',<225>,1:29]], id=[[@3,9:10='id',<381>,1:9]], sku=[[@9,22:24='sku',<381>,1:22]]}, table_dictionary={orders={id=[[@1,7:7='o',<381>,1:7]], items=[[@24,84:84='o',<381>,1:84]]}, flatten0={sku=[[@5,13:13='f',<381>,1:13], [@37,122:122='f',<381>,1:122]]}, products={name=[[@11,27:27='p',<381>,1:27]], sku=[[@33,114:114='p',<381>,1:114]]}}, filters=[{name=sku, table_ref=p}, {name=sku, table_ref=f}], interface={name=[{name=name, table_ref=p}], id=[{name=id, table_ref=o}], sku=[{name=sku, table_ref=f}]}, table_alias={p=products, f=flatten0, o=orders}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void explicitJoinFormInsertGeneratorV5Test() {
		final String query = "INSERT INTO my_table SELECT * FROM TABLE(GENERATOR(ROWCOUNT => 10))";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		Assert.assertEquals("AST is wrong",
				"{SQL={preamble=insert_into, from={from={table_function={function_name=GENERATOR, parameters={rowcount={literal=10}}}}, select={1={column={name=*, table_ref=*}}}}, target_table={table={alias=null, table=my_table}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{insert1={query_dictionary={*=[[@4,28:28='*',<291>,1:28]]}, table_dictionary={my_table={*=[[@4,28:28='*',<291>,1:28]]}}, def_query0={query_dictionary={*=[[@4,28:28='*',<291>,1:28]]}, table_dictionary={generator0={*=[[@4,28:28='*',<291>,1:28]]}}, interface={*=[{name=*, table_ref=*}]}}, interface={*=[{name=*, table_ref=query0}]}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void explicitJoinFormCtasGeneratorV6Test() {
		// DDL endpoint requires a target table name for CTAS.
		final String query = "CREATE TABLE tab1 AS SELECT * FROM TABLE(GENERATOR(ROWCOUNT => 10))";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runDdlParsertest(query, parser);
		Assert.assertTrue("AST should be rooted at DDL create",
				extractor.getAsTree().toString().contains("{DDL={create={type=TABLE")
						|| extractor.getAsTree().toString().contains("{DDL={create={type=table"));
		Assert.assertTrue("AST should retain CREATE TABLE target",
				extractor.getAsTree().toString().contains("table=tab1"));
		Assert.assertTrue("AST should retain GENERATOR table function",
				extractor.getAsTree().toString().contains("function_name=GENERATOR"));
		Assert.assertTrue("Symbol table should retain generator-backed source",
				extractor.getSymbolTable().toString().contains("generator0"));
	}

	// LATERAL with a correlated subquery row source

	@Test
	public void lateralCorrelatedSubquerySimpleV3Test() {
		final String query = "SELECT t.id, sub.x FROM t, LATERAL (SELECT t.id + 1 AS x) sub";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=id, table_ref=t}}, 2={column={name=x, table_ref=sub}}}, from={join={1={table={alias=null, table=t}}, 2={modifier=LATERAL}, 3={table={alias=sub, query={select={1={alias=x, calc={left={column={name=id, table_ref=t}}, right={literal=1}, operator=+}}}}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{query1={query_dictionary={x=[[@7,17:17='x',<381>,1:17]], id=[[@3,9:10='id',<381>,1:9]]}, table_dictionary={t={id=[[@1,7:7='t',<381>,1:7], [@14,43:43='t',<381>,1:43], [@14,43:43='t',<381>,1:43], [@14,43:43='t',<381>,1:43]]}}, def_query0={query_dictionary={x=[[@20,55:55='x',<381>,1:55], [@5,13:15='sub',<381>,1:13]]}, interface={x=[{name=id, table_ref=t}]}}, interface={x=[{name=x, table_ref=sub}], id=[{name=id, table_ref=t}]}, table_alias={sub=query0}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void lateralCorrelatedSubqueryWithFilterV4Test() {
		// Original had WHERE inside a FROM-less subquery which grammar does not support;
		// adapted to a distinct arithmetic variant of the correlated subquery
		final String query = "SELECT t.id, sub.x FROM t, LATERAL (SELECT t.id + 3 AS x) sub";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=id, table_ref=t}}, 2={column={name=x, table_ref=sub}}}, from={join={1={table={alias=null, table=t}}, 2={modifier=LATERAL}, 3={table={alias=sub, query={select={1={alias=x, calc={left={column={name=id, table_ref=t}}, right={literal=3}, operator=+}}}}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{query1={query_dictionary={x=[[@7,17:17='x',<381>,1:17]], id=[[@3,9:10='id',<381>,1:9]]}, table_dictionary={t={id=[[@1,7:7='t',<381>,1:7], [@14,43:43='t',<381>,1:43], [@14,43:43='t',<381>,1:43], [@14,43:43='t',<381>,1:43]]}}, def_query0={query_dictionary={x=[[@20,55:55='x',<381>,1:55], [@5,13:15='sub',<381>,1:13]]}, interface={x=[{name=id, table_ref=t}]}}, interface={x=[{name=x, table_ref=sub}], id=[{name=id, table_ref=t}]}, table_alias={sub=query0}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void lateralCorrelatedSubqueryNestedUnionV5Test() {
		final String query = "SELECT t.id, sub.x FROM t, LATERAL (SELECT t.id + v AS x FROM (SELECT 1 AS v UNION ALL SELECT 2 AS v)) sub";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=id, table_ref=t}}, 2={column={name=x, table_ref=sub}}}, from={join={1={table={alias=null, table=t}}, 2={modifier=LATERAL}, 3={table={alias=sub, query={select={1={alias=x, calc={left={column={name=id, table_ref=t}}, right={column={name=v, table_ref=null}}, operator=+}}}, from={union={1={select={1={alias=v, literal=1}}}, 2={union={qualifier=ALL, operator=UNION}}, 3={select={1={alias=v, literal=2}}}}}}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{query4={query_dictionary={x=[[@7,17:17='x',<381>,1:17]], id=[[@3,9:10='id',<381>,1:9]]}, table_dictionary={t={id=[[@1,7:7='t',<381>,1:7], [@14,43:43='t',<381>,1:43], [@14,43:43='t',<381>,1:43], [@14,43:43='t',<381>,1:43]]}}, interface={x=[{name=x, table_ref=sub}], id=[{name=id, table_ref=t}]}, def_query3={def_union2={query0={query_dictionary={v=[[@26,75:75='v',<381>,1:75]]}, interface={v=[]}}, interface={v=[]}, query1={query_dictionary={v=[[@32,99:99='v',<381>,1:99]]}, interface={v=[]}}}, query_dictionary={x=[[@20,55:55='x',<381>,1:55], [@5,13:15='sub',<381>,1:13]]}, union2={query0={query_dictionary={v=[[@26,75:75='v',<381>,1:75]]}, interface={v=[]}}, interface={v=[]}, query1={query_dictionary={v=[[@32,99:99='v',<381>,1:99]]}, interface={v=[]}}}, interface={x=[{name=id, table_ref=t}, {name=v, table_ref=union2}]}, table_alias={union2=union2}}, table_alias={sub=query3}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void lateralCorrelatedSubquerySimpleV6Test() {
		final String query = "SELECT t.id, sub.x FROM t, LATERAL (SELECT t.id + 1 AS x) sub";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=id, table_ref=t}}, 2={column={name=x, table_ref=sub}}}, from={join={1={table={alias=null, table=t}}, 2={modifier=LATERAL}, 3={table={alias=sub, query={select={1={alias=x, calc={left={column={name=id, table_ref=t}}, right={literal=1}, operator=+}}}}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{query1={query_dictionary={x=[[@7,17:17='x',<381>,1:17]], id=[[@3,9:10='id',<381>,1:9]]}, table_dictionary={t={id=[[@1,7:7='t',<381>,1:7], [@14,43:43='t',<381>,1:43], [@14,43:43='t',<381>,1:43], [@14,43:43='t',<381>,1:43]]}}, def_query0={query_dictionary={x=[[@20,55:55='x',<381>,1:55], [@5,13:15='sub',<381>,1:13]]}, interface={x=[{name=id, table_ref=t}]}}, interface={x=[{name=x, table_ref=sub}], id=[{name=id, table_ref=t}]}, table_alias={sub=query0}}}",
				extractor.getSymbolTable().toString());
	}


	// TUPLE ENDPOINT TESTS

	@Test
	public void flattenTableFunctionTupleEndpointTableSyntaxDoesNotUseQueryWrapperTest() {
		final String query = "table(flatten(input=>parse_json('[1,2]')))";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runTupleParsertest(query, parser);

		String ast = extractor.getAsTree().toString();
		Assert.assertTrue("tuple AST should contain table_function", ast.contains("table_function="));
		Assert.assertFalse("table_function should not be nested under query", ast.contains("query={table_function="));
	}

	@Test
	public void generatorTableFunctionTupleEndpointTableSyntaxDoesNotUseQueryWrapperTest() {
		final String query = "table(generator(rowcount=>10))";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runTupleParsertest(query, parser);

		String ast = extractor.getAsTree().toString();
		Assert.assertTrue("tuple AST should contain table_function", ast.contains("table_function="));
		Assert.assertFalse("table_function should not be nested under query", ast.contains("query={table_function="));
	}

	@Test
	public void inferSchemaTableFunctionTupleEndpointTableSyntaxDoesNotUseQueryWrapperTest() {
		final String query = "table(infer_schema(file_format=>'fmt', location=>'@stg/path'))";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runTupleParsertest(query, parser);

		String ast = extractor.getAsTree().toString();
		Assert.assertTrue("tuple AST should contain table_function", ast.contains("table_function="));
		Assert.assertFalse("table_function should not be nested under query", ast.contains("query={table_function="));
	}

	@Test
	public void flattenTableFunctionTupleEndpointNakedSyntaxBuildsTableFunctionTest() {
		final String query = "flatten(input=>parse_json('[1,2]'))";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runTupleParsertest(query, parser);

		String ast = extractor.getAsTree().toString();
		Assert.assertTrue("tuple AST should contain flatten function name", ast.contains("function_name=flatten"));
		Assert.assertTrue("tuple AST should contain table_function", ast.contains("table_function="));
	}

	@Test
	public void generatorTableFunctionTupleEndpointNakedSyntaxBuildsTableFunctionTest() {
		final String query = "generator(rowcount=>10)";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runTupleParsertest(query, parser);

		String ast = extractor.getAsTree().toString();
		Assert.assertTrue("tuple AST should contain generator function name", ast.contains("function_name=generator"));
		Assert.assertTrue("tuple AST should contain table_function", ast.contains("table_function="));
	}

	@Test
	public void inferSchemaTableFunctionTupleEndpointNakedSyntaxBuildsTableFunctionTest() {
		final String query = "infer_schema(file_format=>'fmt', location=>'@stg/path')";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runTupleParsertest(query, parser);

		String ast = extractor.getAsTree().toString();
		Assert.assertTrue("tuple AST should contain infer_schema function name", ast.contains("function_name=infer_schema"));
		Assert.assertTrue("tuple AST should contain table_function", ast.contains("table_function="));
	}

	@Test
	public void flattenDirectFromListTupleEndpointNakedSyntaxBuildsSameAstShapeTest() {
		final String query = "flatten(input=>parse_json('[1,2]'))";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runTupleParsertest(query, parser);

		String ast = extractor.getAsTree().toString();
		Assert.assertTrue("tuple AST should contain table_function", ast.contains("table_function="));
		Assert.assertFalse("table_function should not be nested under query", ast.contains("query={table_function="));
	}

	@Test
	public void flattenDirectCrossJoinTupleEndpointTableSyntaxBuildsSameAstShapeTest() {
		final String query = "table(flatten(input=>parse_json('[1,2]')))";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runTupleParsertest(query, parser);

		String ast = extractor.getAsTree().toString();
		Assert.assertTrue("tuple AST should contain table_function", ast.contains("table_function="));
		Assert.assertFalse("table_function should not be nested under query", ast.contains("query={table_function="));
	}

	@Test
	public void generatorDirectFromListTupleEndpointNakedSyntaxBuildsSameAstShapeTest() {
		final String query = "generator(rowcount=>10)";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runTupleParsertest(query, parser);

		String ast = extractor.getAsTree().toString();
		Assert.assertTrue("tuple AST should contain table_function", ast.contains("table_function="));
		Assert.assertFalse("table_function should not be nested under query", ast.contains("query={table_function="));
	}

}
