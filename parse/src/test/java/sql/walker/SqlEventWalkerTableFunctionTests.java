package sql.walker;

import java.util.Arrays;

import org.antlr.v4.runtime.tree.Trees;
import org.junit.Assert;
import org.junit.Test;

import access.Snippet;
import errorhandling.ParseDiagnostic;
import sql.SQLSelectParserParser;

public class SqlEventWalkerTableFunctionTests extends AbstractSqlParseEventWalkerTest {

	private void assertExactDiagnostics(SqlParseEventWalker extractor, ParseDiagnostic... expected) {
		Assert.assertEquals("Diagnostics are wrong",
				Arrays.asList(expected), extractor.getSnippet().getParserDiagnosticList());
	}

	private void assertOutputs(SqlParseEventWalker extractor, String ast, String outputInterface, String substitutions,
			String tableDictionary, String queryDictionary, String symbolTable) {
		Assert.assertEquals("AST is wrong", ast, extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", outputInterface, extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", substitutions, extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", tableDictionary, extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", queryDictionary, extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", symbolTable, extractor.getSymbolTable().toString());
	}

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
		Assert.assertEquals("Table Dictionary is wrong",
		 "{flatten0={*=[[@1,7:7='*',<291>,1:7]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
		 "{query0={*=[[@1,7:7='*',<291>,1:7]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
		"{def_query0={query_dictionary={*=[[@1,7:7='*',<291>,1:7]]}, table_dictionary={flatten0={*=[[@1,7:7='*',<291>,1:7]]}}, interface={*=[{name=*, table_ref=*}]}, table_alias={f=flatten0}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void generatorTableFunctionFromListDoesNotUseQueryWrapperTest() {
		final String query = "select * from table(generator(rowcount=>10)) g";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=*, table_ref=*}}}, from={table={alias=g, table_function={function_name=generator, parameters={rowcount={literal=10}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{generator0={*=[[@1,7:7='*',<291>,1:7]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@1,7:7='*',<291>,1:7]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query0={query_dictionary={*=[[@1,7:7='*',<291>,1:7]]}, table_dictionary={generator0={*=[[@1,7:7='*',<291>,1:7]]}}, interface={*=[{name=*, table_ref=*}]}, table_alias={g=generator0}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void inferSchemaTableFunctionFromListDoesNotUseQueryWrapperTest() {
		final String query = "select s.* from table(infer_schema(file_format=>'fmt', location=>'@stg/path')) s";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=*, table_ref=s}}}, from={table={alias=s, table_function={function_name=infer_schema, parameters={location='@stg/path', file_format='fmt'}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{infer_schema0={*=[[@1,7:7='s',<459>,1:7]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@3,9:9='*',<291>,1:9]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query0={query_dictionary={*=[[@3,9:9='*',<291>,1:9]]}, table_dictionary={infer_schema0={*=[[@1,7:7='s',<459>,1:7]]}}, interface={*=[{name=*, table_ref=s}]}, table_alias={s=infer_schema0}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void flattenTableFunctionAliasIsPreservedInFromListItemTest() {
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
				"{def_query0={query_dictionary={*=[[@1,7:7='*',<291>,1:7]]}, table_dictionary={flatten0={*=[[@1,7:7='*',<291>,1:7]]}}, interface={*=[{name=*, table_ref=*}]}, table_alias={f=flatten0}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void generatorTableFunctionAliasIsPreservedInFromListItemTest() {
		final String query = "select * from table(generator(rowcount=>10)) g";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=*, table_ref=*}}}, from={table={alias=g, table_function={function_name=generator, parameters={rowcount={literal=10}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{generator0={*=[[@1,7:7='*',<291>,1:7]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@1,7:7='*',<291>,1:7]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query0={query_dictionary={*=[[@1,7:7='*',<291>,1:7]]}, table_dictionary={generator0={*=[[@1,7:7='*',<291>,1:7]]}}, interface={*=[{name=*, table_ref=*}]}, table_alias={g=generator0}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void inferSchemaTableFunctionAliasIsPreservedInFromListItemTest() {
		final String query = "select s.* from table(infer_schema(file_format=>'fmt', location=>'@stg/path')) s";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=*, table_ref=s}}}, from={table={alias=s, table_function={function_name=infer_schema, parameters={location='@stg/path', file_format='fmt'}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{infer_schema0={*=[[@1,7:7='s',<459>,1:7]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@3,9:9='*',<291>,1:9]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query0={query_dictionary={*=[[@3,9:9='*',<291>,1:9]]}, table_dictionary={infer_schema0={*=[[@1,7:7='s',<459>,1:7]]}}, interface={*=[{name=*, table_ref=s}]}, table_alias={s=infer_schema0}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void flattenDirectFromListCommaSyntaxBuildsSameAstShapeTest() {
		final String query = "select * from t, flatten(input=>parse_json('[1,2]')) f";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=*, table_ref=*}}}, from={join={1={table={alias=null, table=t}}, 2={table={alias=f, table_function={function_name=flatten, parameters={input={function={parameters={1={literal='[1,2]'}}, function_name=parse_json}}}}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{t={*=[[@1,7:7='*',<291>,1:7]]}, flatten0={*=[[@1,7:7='*',<291>,1:7]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@1,7:7='*',<291>,1:7]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query0={query_dictionary={*=[[@1,7:7='*',<291>,1:7]]}, table_dictionary={t={*=[[@1,7:7='*',<291>,1:7]]}, flatten0={*=[[@1,7:7='*',<291>,1:7]]}}, interface={*=[{name=*, table_ref=*}]}, table_alias={f=flatten0}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void flattenDirectCrossJoinSyntaxBuildsSameAstShapeTest() {
		final String query = "select * from t cross join flatten(input=>parse_json('[1,2]')) f";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=*, table_ref=*}}}, from={join={1={table={alias=null, table=t}}, 2={join=crossjoin}, 3={table={alias=f, table_function={function_name=flatten, parameters={input={function={parameters={1={literal='[1,2]'}}, function_name=parse_json}}}}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{t={*=[[@1,7:7='*',<291>,1:7]]}, flatten0={*=[[@1,7:7='*',<291>,1:7]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@1,7:7='*',<291>,1:7]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query0={query_dictionary={*=[[@1,7:7='*',<291>,1:7]]}, table_dictionary={t={*=[[@1,7:7='*',<291>,1:7]]}, flatten0={*=[[@1,7:7='*',<291>,1:7]]}}, interface={*=[{name=*, table_ref=*}]}, table_alias={f=flatten0}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void generatorDirectFromListCommaSyntaxBuildsSameAstShapeTest() {
		final String query = "select * from t, generator(rowcount=>10) g";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=*, table_ref=*}}}, from={join={1={table={alias=null, table=t}}, 2={table={alias=g, table_function={function_name=generator, parameters={rowcount={literal=10}}}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{t={*=[[@1,7:7='*',<291>,1:7]]}, generator0={*=[[@1,7:7='*',<291>,1:7]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@1,7:7='*',<291>,1:7]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query0={query_dictionary={*=[[@1,7:7='*',<291>,1:7]]}, table_dictionary={t={*=[[@1,7:7='*',<291>,1:7]]}, generator0={*=[[@1,7:7='*',<291>,1:7]]}}, interface={*=[{name=*, table_ref=*}]}, table_alias={g=generator0}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void flattenDirectFromListCommaLateralIncludesModifierItemTest() {
		final String query = "select * from tab1 t, lateral flatten(input=>parse_json('[1,2]')) f";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=*, table_ref=*}}}, from={join={1={table={alias=t, table=tab1}}, 2={modifier=lateral}, 3={table={alias=f, table_function={function_name=flatten, parameters={input={function={parameters={1={literal='[1,2]'}}, function_name=parse_json}}}}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={*=[[@1,7:7='*',<291>,1:7]]}, flatten0={*=[[@1,7:7='*',<291>,1:7]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@1,7:7='*',<291>,1:7]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query0={query_dictionary={*=[[@1,7:7='*',<291>,1:7]]}, table_dictionary={tab1={*=[[@1,7:7='*',<291>,1:7]]}, flatten0={*=[[@1,7:7='*',<291>,1:7]]}}, interface={*=[{name=*, table_ref=*}]}, table_alias={t=tab1, f=flatten0}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void flattenDirectCrossJoinLateralIncludesModifierItemTest() {
		final String query = "select * from t cross join lateral flatten(input=>parse_json('[1,2]')) f";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=*, table_ref=*}}}, from={join={1={table={alias=null, table=t}}, 2={join=crossjoin}, 3={modifier=lateral}, 4={table={alias=f, table_function={function_name=flatten, parameters={input={function={parameters={1={literal='[1,2]'}}, function_name=parse_json}}}}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{t={*=[[@1,7:7='*',<291>,1:7]]}, flatten0={*=[[@1,7:7='*',<291>,1:7]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@1,7:7='*',<291>,1:7]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query0={query_dictionary={*=[[@1,7:7='*',<291>,1:7]]}, table_dictionary={t={*=[[@1,7:7='*',<291>,1:7]]}, flatten0={*=[[@1,7:7='*',<291>,1:7]]}}, interface={*=[{name=*, table_ref=*}]}, table_alias={f=flatten0}}}",
				extractor.getSymbolTable().toString());
	}
 
	// Simple table function calls

	@Test
	public void simpleTfCallFlattenWildcardV1Test() {
		final String query = "SELECT * FROM TABLE(FLATTEN(input => PARSE_JSON('[1,2,3]'))) f";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=*, table_ref=*}}}, from={table={alias=f, table_function={function_name=FLATTEN, parameters={input={function={parameters={1={literal='[1,2,3]'}}, function_name=PARSE_JSON}}}}}}}}",
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
				"{def_query0={query_dictionary={*=[[@1,7:7='*',<291>,1:7]]}, table_dictionary={flatten0={*=[[@1,7:7='*',<291>,1:7]]}}, interface={*=[{name=*, table_ref=*}]}, table_alias={f=flatten0}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void simpleTfCallFlattenIndexValueV2Test() {
		final String query = "SELECT f.index, f.value FROM TABLE(FLATTEN(input => PARSE_JSON('[\"apple\",\"banana\",\"cherry\"]'))) f";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=index, table_ref=f}}, 2={column={name=value, table_ref=f}}}, from={table={alias=f, table_function={function_name=FLATTEN, parameters={input={function={parameters={1={literal='[\"apple\",\"banana\",\"cherry\"]'}}, function_name=PARSE_JSON}}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[index, value]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{flatten0={index=[[@1,7:7='f',<459>,1:7]], value=[[@5,16:16='f',<459>,1:16]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={index=[[@3,9:13='index',<96>,1:9]], value=[[@7,18:22='value',<459>,1:18]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query0={query_dictionary={index=[[@3,9:13='index',<96>,1:9]], value=[[@7,18:22='value',<459>,1:18]]}, table_dictionary={flatten0={index=[[@1,7:7='f',<459>,1:7]], value=[[@5,16:16='f',<459>,1:16]]}}, interface={index=[{name=index, table_ref=f}], value=[{name=value, table_ref=f}]}, table_alias={f=flatten0}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void simpleTfCallFlattenKeyValueV3Test() {
		final String query = "SELECT f.key, f.value FROM TABLE(FLATTEN(input => PARSE_JSON('{\"sku\":\"A\", \"product_name\":\"Apple\",\"list_price\":1.25}'))) f";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=key, table_ref=f}}, 2={column={name=value, table_ref=f}}}, from={table={alias=f, table_function={function_name=FLATTEN, parameters={input={function={parameters={1={literal='{\"sku\":\"A\", \"product_name\":\"Apple\",\"list_price\":1.25}'}}, function_name=PARSE_JSON}}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[value, key]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{flatten0={value=[[@5,14:14='f',<459>,1:14]], key=[[@1,7:7='f',<459>,1:7]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={value=[[@7,16:20='value',<459>,1:16]], key=[[@3,9:11='key',<459>,1:9]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query0={query_dictionary={value=[[@7,16:20='value',<459>,1:16]], key=[[@3,9:11='key',<459>,1:9]]}, table_dictionary={flatten0={value=[[@5,14:14='f',<459>,1:14]], key=[[@1,7:7='f',<459>,1:7]]}}, interface={value=[{name=value, table_ref=f}], key=[{name=key, table_ref=f}]}, table_alias={f=flatten0}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void simpleTfCallSplitToTableV4Test() {
		final String query = "SELECT * FROM TABLE(SPLIT_TO_TABLE('a,b,c', ',')) s";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=*, table_ref=*}}}, from={table={alias=s, table_function={function={function_name=SPLIT_TO_TABLE, parameters={1={literal='a,b,c'}, 2={literal=','}}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{table_function0={*=[[@1,7:7='*',<291>,1:7]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@1,7:7='*',<291>,1:7]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query0={query_dictionary={*=[[@1,7:7='*',<291>,1:7]]}, table_dictionary={table_function0={*=[[@1,7:7='*',<291>,1:7]]}}, interface={*=[{name=*, table_ref=*}]}, table_alias={s=table_function0}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void simpleTfCallSplitToTableV4WithWhereReferenceTest() {
		final String query = "SELECT tf.seq, tf.value FROM TABLE(SPLIT_TO_TABLE('11,12', ',')) tf where tf.index > 11";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=seq, table_ref=tf}}, 2={column={name=value, table_ref=tf}}}, from={table={alias=tf, table_function={function={function_name=SPLIT_TO_TABLE, parameters={1={literal='11,12'}, 2={literal=','}}}}}}, where={condition={left={column={name=index, table_ref=tf}}, right={literal=11}, operator=>}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[value, seq]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{table_function0={index=[[@20,74:75='tf',<459>,1:74]], value=[[@5,15:16='tf',<459>,1:15]], seq=[[@1,7:8='tf',<459>,1:7]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={value=[[@7,18:22='value',<459>,1:18]], seq=[[@3,10:12='seq',<459>,1:10]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query0={query_dictionary={value=[[@7,18:22='value',<459>,1:18]], seq=[[@3,10:12='seq',<459>,1:10]]}, table_dictionary={table_function0={index=[[@20,74:75='tf',<459>,1:74]], value=[[@5,15:16='tf',<459>,1:15]], seq=[[@1,7:8='tf',<459>,1:7]]}}, filters=[{name=index, table_ref=tf}], interface={value=[{name=value, table_ref=tf}], seq=[{name=seq, table_ref=tf}]}, table_alias={tf=table_function0}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void simpleTfCallFlattenSplitV5Test() {
		// Unaliased FLATTEN in FROM does not expose value for unqualified SELECT resolution;
		// writers should add a table-function alias and qualify the column (e.g. f.value).
		final String query = "SELECT value FROM TABLE(FLATTEN(input => SPLIT('a,b,c', ',')))";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoFatalErrors(extractor);
		Snippet snippet = extractor.getSnippet();
		assertDiagnosticAtPosition(
				snippet,
				"UNRESOLVED_UNQUALIFIED_COLUMNS",
				ParseDiagnostic.Severity.ERROR,
				"Unresolved unqualified column reference(s): [value [(l:1 c:7)]]",
				"value",
				1,
				7);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=value, table_ref=null}}}, from={table_function={function_name=FLATTEN, parameters={input={function={parameters={1={literal='a,b,c'}, 2={literal=','}}, function_name=SPLIT}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[value]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{flatten0={value=[[@1,7:11='value',<459>,1:7]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={value=[[@1,7:11='value',<459>,1:7]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query0={query_dictionary={value=[[@1,7:11='value',<459>,1:7]]}, table_dictionary={flatten0={value=[[@1,7:11='value',<459>,1:7]]}}, interface={value=[{name=value, table_ref=null}]}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void simpleTfCallGeneratorRowcountV6Test() {
		final String query = "SELECT * FROM TABLE(GENERATOR(ROWCOUNT => 5))";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=*, table_ref=*}}}, from={table_function={function_name=GENERATOR, parameters={rowcount={literal=5}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{generator0={*=[[@1,7:7='*',<291>,1:7]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@1,7:7='*',<291>,1:7]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query0={query_dictionary={*=[[@1,7:7='*',<291>,1:7]]}, table_dictionary={generator0={*=[[@1,7:7='*',<291>,1:7]]}}, interface={*=[{name=*, table_ref=*}]}}}",
				extractor.getSymbolTable().toString());
	}

	// Table function in FROM with a base table

	@Test
	public void tfFromBaseTableLateralFlattenV1Test() {
		final String query = "SELECT t.id, f.value AS item FROM my_table t, LATERAL FLATTEN(input => t.items) f";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=id, table_ref=t}}, 2={column={name=value, table_ref=f}, alias=item}}, from={join={1={table={alias=t, table=my_table}}, 2={modifier=LATERAL}, 3={table={alias=f, table_function={function_name=FLATTEN, parameters={input={column={name=items, table_ref=t}}}}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[item, id]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{my_table={id=[[@1,7:7='t',<459>,1:7]], items=[[@19,71:71='t',<459>,1:71]]}, flatten0={value=[[@5,13:13='f',<459>,1:13]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={item=[[@9,24:27='item',<459>,1:24]], id=[[@3,9:10='id',<459>,1:9]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query0={query_dictionary={item=[[@9,24:27='item',<459>,1:24]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={my_table={id=[[@1,7:7='t',<459>,1:7]], items=[[@19,71:71='t',<459>,1:71]]}, flatten0={value=[[@5,13:13='f',<459>,1:13]]}}, interface={item=[{name=value, table_ref=f}], id=[{name=id, table_ref=t}]}, table_alias={t=my_table, f=flatten0}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void tfFromBaseTableFlattenNoLateralV2Test() {
		final String query = "SELECT t.id, f.value AS item FROM my_table t, FLATTEN(input => t.items) f";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=id, table_ref=t}}, 2={column={name=value, table_ref=f}, alias=item}}, from={join={1={table={alias=t, table=my_table}}, 2={table={alias=f, table_function={function_name=FLATTEN, parameters={input={column={name=items, table_ref=t}}}}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[item, id]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{my_table={id=[[@1,7:7='t',<459>,1:7]], items=[[@18,63:63='t',<459>,1:63]]}, flatten0={value=[[@5,13:13='f',<459>,1:13]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={item=[[@9,24:27='item',<459>,1:24]], id=[[@3,9:10='id',<459>,1:9]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query0={query_dictionary={item=[[@9,24:27='item',<459>,1:24]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={my_table={id=[[@1,7:7='t',<459>,1:7]], items=[[@18,63:63='t',<459>,1:63]]}, flatten0={value=[[@5,13:13='f',<459>,1:13]]}}, interface={item=[{name=value, table_ref=f}], id=[{name=id, table_ref=t}]}, table_alias={t=my_table, f=flatten0}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void tfFromBaseTableLateralFlattenOuterV3Test() {
		final String query = "SELECT * FROM t, LATERAL FLATTEN(input => t.items, outer => TRUE) f";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=*, table_ref=*}}}, from={join={1={table={alias=null, table=t}}, 2={modifier=LATERAL}, 3={table={alias=f, table_function={function_name=FLATTEN, parameters={input={column={name=items, table_ref=t}}, outer=TRUE}}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{t={*=[[@1,7:7='*',<291>,1:7]], items=[[@10,42:42='t',<459>,1:42]]}, flatten0={*=[[@1,7:7='*',<291>,1:7]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@1,7:7='*',<291>,1:7]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query0={query_dictionary={*=[[@1,7:7='*',<291>,1:7]]}, table_dictionary={t={*=[[@1,7:7='*',<291>,1:7]], items=[[@10,42:42='t',<459>,1:42]]}, flatten0={*=[[@1,7:7='*',<291>,1:7]]}}, interface={*=[{name=*, table_ref=*}]}, table_alias={f=flatten0}}}",
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

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=key, table_ref=f}}, 2={column={name=value, table_ref=f}}}, from={table={alias=f, table_function={function_name=FLATTEN, parameters={input={function={parameters={1={literal='[{\"sku\":\"A\",\"product_name\":\"Apple\",\"list_price\":1.25},{\"sku\":\"B\",\"product_name\":\"Banana\",\"list_price\":0.75}]'}}, function_name=PARSE_JSON}}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[value, key]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{flatten0={value=[[@5,14:14='f',<459>,1:14]], key=[[@1,7:7='f',<459>,1:7]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={value=[[@7,16:20='value',<459>,1:16]], key=[[@3,9:11='key',<459>,1:9]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query0={query_dictionary={value=[[@7,16:20='value',<459>,1:16]], key=[[@3,9:11='key',<459>,1:9]]}, table_dictionary={flatten0={value=[[@5,14:14='f',<459>,1:14]], key=[[@1,7:7='f',<459>,1:7]]}}, interface={value=[{name=value, table_ref=f}], key=[{name=key, table_ref=f}]}, table_alias={f=flatten0}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void fieldExtractionFlatJsonLateralFlattenV3Test() {
		final String query = "SELECT f.key, f.value FROM my_table t, LATERAL FLATTEN(input => t.obj_col) f";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=key, table_ref=f}}, 2={column={name=value, table_ref=f}}}, from={join={1={table={alias=t, table=my_table}}, 2={modifier=LATERAL}, 3={table={alias=f, table_function={function_name=FLATTEN, parameters={input={column={name=obj_col, table_ref=t}}}}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[value, key]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{my_table={obj_col=[[@17,64:64='t',<459>,1:64]]}, flatten0={value=[[@5,14:14='f',<459>,1:14]], key=[[@1,7:7='f',<459>,1:7]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={value=[[@7,16:20='value',<459>,1:16]], key=[[@3,9:11='key',<459>,1:9]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query0={query_dictionary={value=[[@7,16:20='value',<459>,1:16]], key=[[@3,9:11='key',<459>,1:9]]}, table_dictionary={my_table={obj_col=[[@17,64:64='t',<459>,1:64]]}, flatten0={value=[[@5,14:14='f',<459>,1:14]], key=[[@1,7:7='f',<459>,1:7]]}}, interface={value=[{name=value, table_ref=f}], key=[{name=key, table_ref=f}]}, table_alias={t=my_table, f=flatten0}}}",
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

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=id, table_ref=o}, alias=order_id}, 2={column={name=value, table_ref=p}, alias=product}}, from={join={1={table={alias=t, table=my_table}}, 2={modifier=LATERAL}, 3={table={alias=o, table_function={function_name=FLATTEN, parameters={input={column={name=orders, table_ref=t}}}}}}, 4={modifier=LATERAL}, 5={table={alias=p, table_function={function_name=FLATTEN, parameters={input={column={name=value, table_ref=o}}}}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[product, order_id]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{my_table={orders=[[@21,86:86='t',<459>,1:86]]}, flatten1={value=[[@7,25:25='p',<459>,1:25]]}, flatten0={id=[[@1,7:7='o',<459>,1:7]], value=[[@32,124:124='o',<459>,1:124]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={order_id=[[@5,15:22='order_id',<459>,1:15]], product=[[@11,36:42='product',<459>,1:36]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query0={query_dictionary={product=[[@11,36:42='product',<459>,1:36]], order_id=[[@5,15:22='order_id',<459>,1:15]]}, table_dictionary={my_table={orders=[[@21,86:86='t',<459>,1:86]]}, flatten1={value=[[@7,25:25='p',<459>,1:25]]}, flatten0={id=[[@1,7:7='o',<459>,1:7]], value=[[@32,124:124='o',<459>,1:124]]}}, interface={product=[{name=value, table_ref=p}], order_id=[{name=id, table_ref=o}]}, table_alias={p=flatten1, t=my_table, o=flatten0}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void chainedLateralTfOrderItemsV2Test() {
		// Original uses Snowflake colon notation (o.value:id, o.value:items, i.value:sku) not in grammar;
		// adapted to plain column references
		final String query = "SELECT o.id AS order_id, i.sku AS sku FROM t, LATERAL FLATTEN(input => t.orders) o, LATERAL FLATTEN(input => o.value) i";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=id, table_ref=o}, alias=order_id}, 2={column={name=sku, table_ref=i}, alias=sku}}, from={join={1={table={alias=null, table=t}}, 2={modifier=LATERAL}, 3={table={alias=o, table_function={function_name=FLATTEN, parameters={input={column={name=orders, table_ref=t}}}}}}, 4={modifier=LATERAL}, 5={table={alias=i, table_function={function_name=FLATTEN, parameters={input={column={name=value, table_ref=o}}}}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[sku, order_id]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{t={orders=[[@20,71:71='t',<459>,1:71]]}, flatten1={sku=[[@7,25:25='i',<459>,1:25]]}, flatten0={id=[[@1,7:7='o',<459>,1:7]], value=[[@31,109:109='o',<459>,1:109]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={sku=[[@11,34:36='sku',<459>,1:34]], order_id=[[@5,15:22='order_id',<459>,1:15]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query0={query_dictionary={sku=[[@11,34:36='sku',<459>,1:34]], order_id=[[@5,15:22='order_id',<459>,1:15]]}, table_dictionary={t={orders=[[@20,71:71='t',<459>,1:71]]}, flatten1={sku=[[@7,25:25='i',<459>,1:25]]}, flatten0={id=[[@1,7:7='o',<459>,1:7]], value=[[@31,109:109='o',<459>,1:109]]}}, interface={sku=[{name=sku, table_ref=i}], order_id=[{name=id, table_ref=o}]}, table_alias={i=flatten1, o=flatten0}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void chainedLateralTfWildcardBothV3Test() {
		// Original uses Snowflake colon notation (f.value:b) as FLATTEN input, not in grammar;
		// adapted to use plain column reference f.value
		final String query = "SELECT f.*, e.* FROM t, LATERAL FLATTEN(input => t.a) f, LATERAL FLATTEN(input => f.value) e";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=*, table_ref=f}}, 2={column={name=*, table_ref=e}}}, from={join={1={table={alias=null, table=t}}, 2={modifier=LATERAL}, 3={table={alias=f, table_function={function_name=FLATTEN, parameters={input={column={name=a, table_ref=t}}}}}}, 4={modifier=LATERAL}, 5={table={alias=e, table_function={function_name=FLATTEN, parameters={input={column={name=value, table_ref=f}}}}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{t={a=[[@16,49:49='t',<459>,1:49]]}, flatten1={*=[[@5,12:12='e',<459>,1:12]]}, flatten0={*=[[@1,7:7='f',<459>,1:7]], value=[[@27,82:82='f',<459>,1:82]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@3,9:9='*',<291>,1:9], [@7,14:14='*',<291>,1:14]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query0={query_dictionary={*=[[@3,9:9='*',<291>,1:9], [@7,14:14='*',<291>,1:14]]}, table_dictionary={t={a=[[@16,49:49='t',<459>,1:49]]}, flatten1={*=[[@5,12:12='e',<459>,1:12]]}, flatten0={*=[[@1,7:7='f',<459>,1:7]], value=[[@27,82:82='f',<459>,1:82]]}}, interface={*=[{name=*, table_ref=e}]}, table_alias={e=flatten1, f=flatten0}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void snowflakeColonNotationSelectPathV1Test() {
		final String query = "SELECT o.value:id AS order_id FROM t, LATERAL FLATTEN(input => t.orders) o";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=value, json_path=id, table_ref=o}, alias=order_id}}, from={join={1={table={alias=null, table=t}}, 2={modifier=LATERAL}, 3={table={alias=o, table_function={function_name=FLATTEN, parameters={input={column={name=orders, table_ref=t}}}}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[order_id]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{t={orders=[[@16,63:63='t',<459>,1:63]]}, flatten0={value=[[@1,7:7='o',<459>,1:7]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={order_id=[[@7,21:28='order_id',<459>,1:21]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query0={query_dictionary={order_id=[[@7,21:28='order_id',<459>,1:21]]}, table_dictionary={t={orders=[[@16,63:63='t',<459>,1:63]]}, flatten0={value=[[@1,7:7='o',<459>,1:7]]}}, interface={order_id=[{name=value, json_path=id, table_ref=o}]}, table_alias={o=flatten0}}}",
				extractor.getSymbolTable().toString());
	}

	/**
	 * Entry 3 Stage B target: post-call Snowflake field access on PARSE_URL should parse and
	 * remain compatible with downstream cast syntax.
	 */
	@Test
	public void parseUrlPostCallFieldAccessResolvedStageBTest() {
		final String query = "SELECT PARSE_URL(t.url, 1):scheme::varchar AS scheme_txt FROM t";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={function={function_name=cast, data_type={type=VARCHAR}, type=CAST, value={function={parameters={1={column={name=url, table_ref=t}}, 2={literal=1}}, json_path=scheme, function_name=PARSE_URL}}}, alias=scheme_txt}}, from={table={alias=null, table=t}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[scheme_txt]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{t={url=[[@3,17:17='t',<459>,1:17]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={scheme_txt=[[@14,46:55='scheme_txt',<459>,1:46]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query0={query_dictionary={scheme_txt=[[@14,46:55='scheme_txt',<459>,1:46]]}, table_dictionary={t={url=[[@3,17:17='t',<459>,1:17]]}}, interface={scheme_txt=[{name=url, table_ref=t}]}}}",
				extractor.getSymbolTable().toString());
	}

	/**
	 * Entry 3 Stage A guard: regular cast syntax on PARSE_URL invocation remains healthy.
	 */
	@Test
	public void parseUrlCastWithoutPostCallFieldAccessGuardStageATest() {
		final String query = "SELECT PARSE_URL(t.url, 1)::varchar AS parsed_url_txt FROM t";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={function={function_name=cast, data_type={type=VARCHAR}, type=CAST, value={function={parameters={1={column={name=url, table_ref=t}}, 2={literal=1}}, function_name=PARSE_URL}}}, alias=parsed_url_txt}}, from={table={alias=null, table=t}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[parsed_url_txt]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{t={url=[[@3,17:17='t',<459>,1:17]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={parsed_url_txt=[[@12,39:52='parsed_url_txt',<459>,1:39]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query0={query_dictionary={parsed_url_txt=[[@12,39:52='parsed_url_txt',<459>,1:39]]}, table_dictionary={t={url=[[@3,17:17='t',<459>,1:17]]}}, interface={parsed_url_txt=[{name=url, table_ref=t}]}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void parseJsonPostCallFieldAccessSelectStageDTest() {
		final String query = "SELECT PARSE_JSON('{\"a\":1}'):a::integer AS a_num FROM t";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={function={function_name=cast, data_type={type=INTEGER}, type=CAST, value={function={parameters={1={literal='{\"a\":1}'}}, json_path=a, function_name=PARSE_JSON}}}, alias=a_num}}, from={table={alias=null, table=t}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[a_num]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{t={}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={a_num=[[@10,43:47='a_num',<459>,1:43]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query0={query_dictionary={a_num=[[@10,43:47='a_num',<459>,1:43]]}, table_dictionary={t={}}, interface={a_num=[]}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void parseXmlPostCallFieldAccessSelectStageDTest() {
		final String query = "SELECT PARSE_XML('<r><a>1</a></r>'):a::varchar AS a_txt FROM t";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={function={function_name=cast, data_type={type=VARCHAR}, type=CAST, value={function={parameters={1={literal='<r><a>1</a></r>'}}, json_path=a, function_name=PARSE_XML}}}, alias=a_txt}}, from={table={alias=null, table=t}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[a_txt]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{t={}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={a_txt=[[@10,50:54='a_txt',<459>,1:50]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query0={query_dictionary={a_txt=[[@10,50:54='a_txt',<459>,1:50]]}, table_dictionary={t={}}, interface={a_txt=[]}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void parseIpPostCallFieldAccessWhereStageDTest() {
		final String query = "SELECT 1 FROM t WHERE PARSE_IP('10.0.0.1'):host::varchar IS NOT NULL";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={literal=1}}, from={table={alias=null, table=t}}, where={condition={left={function={function_name=cast, data_type={type=VARCHAR}, type=CAST, value={function={parameters={1={literal='10.0.0.1'}}, json_path=host, function_name=PARSE_IP}}}}, operator=IS NOT NULL}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[unnamed_0]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{t={}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={unnamed_0=[[@1,7:7='1',<300>,1:7]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query0={query_dictionary={unnamed_0=[[@1,7:7='1',<300>,1:7]]}, table_dictionary={t={}}, filters=[], interface={unnamed_0=[]}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void tryParseJsonPostCallFieldAccessJoinOnStageDTest() {
		final String query =
				"SELECT t.id FROM t JOIN u ON TRY_PARSE_JSON(u.payload):a::integer = t.id";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=id, table_ref=t}}}, from={join={1={table={alias=null, table=t}}, 2={join=JOIN, on={condition={left={function={function_name=cast, data_type={type=INTEGER}, type=CAST, value={function={parameters={1={column={name=payload, table_ref=u}}}, json_path=a, function_name=TRY_PARSE_JSON}}}}, right={column={name=id, table_ref=t}}, operator==}}}, 3={table={alias=null, table=u}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[id]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{t={id=[[@1,7:7='t',<459>,1:7], [@20,68:68='t',<459>,1:68]]}, u={payload=[[@11,44:44='u',<459>,1:44]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={id=[[@3,9:10='id',<459>,1:9]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query0={query_dictionary={id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={t={id=[[@1,7:7='t',<459>,1:7], [@20,68:68='t',<459>,1:68]]}, u={payload=[[@11,44:44='u',<459>,1:44]]}}, filters=[{name=payload, table_ref=u}, {name=id, table_ref=t}], interface={id=[{name=id, table_ref=t}]}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void parseUrlPostCallFieldAccessAmbiguousArgumentColumnDiagnosticByPositionTest() {
		final String query =
				"SELECT PARSE_URL(url, 1):scheme::varchar AS scheme_txt FROM t JOIN u ON t.id = u.id";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoFatalErrors(extractor);
		assertDiagnosticAtPosition(
				extractor.getSnippet(),
				"AMBIGUOUS_COLUMN_REFERENCE",
				ParseDiagnostic.Severity.SEVERE_WARNING,
				"Ambiguous column reference 'url'",
				"url",
				1,
				17);
		assertDiagnosticAtPosition(
				extractor.getSnippet(),
				"UNRESOLVED_UNQUALIFIED_COLUMNS",
				ParseDiagnostic.Severity.ERROR,
				"Unresolved unqualified column reference(s): [url [(l:1 c:17)]]",
				"url",
				1,
				17);
	}

	@Test
	public void parseUrlPostCallFieldAccessHasNoSnowflakeDialectGrammarWarningTest() {
		final String query = "SELECT PARSE_URL(t.url, 1):scheme::varchar AS scheme_txt FROM t";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoFatalErrors(extractor);
		assertDiagnosticCountBySeverity(
				extractor.getSnippet(),
				"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
				ParseDiagnostic.Severity.WARNING,
				null,
				null,
				0);
	}

	@Test
	public void parseUrlPostCallFieldAccessOrderByPlacementParityExpansionTest() {
		final String query =
				"SELECT PARSE_URL(t.url, 1):scheme::varchar AS scheme_txt FROM t ORDER BY PARSE_URL(t.url, 1):scheme::varchar";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={function={function_name=cast, data_type={type=VARCHAR}, type=CAST, value={function={parameters={1={column={name=url, table_ref=t}}, 2={literal=1}}, json_path=scheme, function_name=PARSE_URL}}}, alias=scheme_txt}}, orderby={1={null_order=null, predicand={function={function_name=cast, data_type={type=VARCHAR}, type=CAST, value={function={parameters={1={column={name=url, table_ref=t}}, 2={literal=1}}, json_path=scheme, function_name=PARSE_URL}}}}, sort_order=ASC}}, from={table={alias=null, table=t}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[scheme_txt]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{t={url=[[@3,17:17='t',<459>,1:17], [@21,83:83='t',<459>,1:83]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={scheme_txt=[[@14,46:55='scheme_txt',<459>,1:46]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query0={query_dictionary={scheme_txt=[[@14,46:55='scheme_txt',<459>,1:46]]}, table_dictionary={t={url=[[@3,17:17='t',<459>,1:17], [@21,83:83='t',<459>,1:83]]}}, ordered_by=[{name=url, table_ref=t}], interface={scheme_txt=[{name=url, table_ref=t}]}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void parseUrlPostCallFieldAccessGroupByPlacementParityExpansionTest() {
		final String query =
				"SELECT PARSE_URL(t.url, 1):scheme::varchar AS scheme_txt FROM t GROUP BY PARSE_URL(t.url, 1):scheme::varchar";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={function={function_name=cast, data_type={type=VARCHAR}, type=CAST, value={function={parameters={1={column={name=url, table_ref=t}}, 2={literal=1}}, json_path=scheme, function_name=PARSE_URL}}}, alias=scheme_txt}}, from={table={alias=null, table=t}}, groupby={1={function={function_name=cast, data_type={type=VARCHAR}, type=CAST, value={function={parameters={1={column={name=url, table_ref=t}}, 2={literal=1}}, json_path=scheme, function_name=PARSE_URL}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[scheme_txt]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{t={url=[[@3,17:17='t',<459>,1:17], [@21,83:83='t',<459>,1:83]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={scheme_txt=[[@14,46:55='scheme_txt',<459>,1:46]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query0={query_dictionary={scheme_txt=[[@14,46:55='scheme_txt',<459>,1:46]]}, table_dictionary={t={url=[[@3,17:17='t',<459>,1:17], [@21,83:83='t',<459>,1:83]]}}, grouped_by=[{name=url, table_ref=t}], interface={scheme_txt=[{name=url, table_ref=t}]}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void parseUrlPostCallFieldAccessHavingPlacementParityExpansionTest() {
		final String query =
				"SELECT t.id, MAX(PARSE_URL(t.url, 1):scheme::varchar) AS scheme_txt FROM t GROUP BY t.id HAVING MAX(PARSE_URL(t.url, 1):scheme::varchar) IS NOT NULL";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=id, table_ref=t}}, 2={function={function_name=MAX, qualifier=null, parameters={function={function_name=cast, data_type={type=VARCHAR}, type=CAST, value={function={parameters={1={column={name=url, table_ref=t}}, 2={literal=1}}, json_path=scheme, function_name=PARSE_URL}}}}}, alias=scheme_txt}}, having={condition={left={function={function_name=MAX, qualifier=null, parameters={function={function_name=cast, data_type={type=VARCHAR}, type=CAST, value={function={parameters={1={column={name=url, table_ref=t}}, 2={literal=1}}, json_path=scheme, function_name=PARSE_URL}}}}}}, operator=IS NOT NULL}}, from={table={alias=null, table=t}}, groupby={1={column={name=id, table_ref=t}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[scheme_txt, id]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{t={id=[[@1,7:7='t',<459>,1:7], [@26,84:84='t',<459>,1:84]], url=[[@9,27:27='t',<459>,1:27], [@34,110:110='t',<459>,1:110]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={scheme_txt=[[@21,57:66='scheme_txt',<459>,1:57]], id=[[@3,9:10='id',<459>,1:9]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query0={query_dictionary={scheme_txt=[[@21,57:66='scheme_txt',<459>,1:57]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={t={id=[[@1,7:7='t',<459>,1:7], [@26,84:84='t',<459>,1:84]], url=[[@9,27:27='t',<459>,1:27], [@34,110:110='t',<459>,1:110]]}}, grouped_by=[{name=id, table_ref=t}], filters=[{name=url, table_ref=t}], interface={scheme_txt=[{name=url, table_ref=t}], id=[{name=id, table_ref=t}]}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void parseUrlPostCallFieldAccessHavingSubstitutionParityExpansionTest() {
		final String query =
				"SELECT t.id, MAX(PARSE_URL(t.url, 1):scheme::varchar) AS scheme_txt FROM t GROUP BY t.id HAVING MAX(PARSE_URL(t.url, 1):scheme::varchar) = <scheme filter>";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=id, table_ref=t}}, 2={function={function_name=MAX, qualifier=null, parameters={function={function_name=cast, data_type={type=VARCHAR}, type=CAST, value={function={parameters={1={column={name=url, table_ref=t}}, 2={literal=1}}, json_path=scheme, function_name=PARSE_URL}}}}}, alias=scheme_txt}}, having={condition={left={function={function_name=MAX, qualifier=null, parameters={function={function_name=cast, data_type={type=VARCHAR}, type=CAST, value={function={parameters={1={column={name=url, table_ref=t}}, 2={literal=1}}, json_path=scheme, function_name=PARSE_URL}}}}}}, right={substitution={name=<scheme filter>, type=predicand}}, operator==}}, from={table={alias=null, table=t}}, groupby={1={column={name=id, table_ref=t}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[scheme_txt, id]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<scheme filter>=predicand}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{t={id=[[@1,7:7='t',<459>,1:7], [@26,84:84='t',<459>,1:84]], url=[[@9,27:27='t',<459>,1:27], [@34,110:110='t',<459>,1:110]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={scheme_txt=[[@21,57:66='scheme_txt',<459>,1:57]], id=[[@3,9:10='id',<459>,1:9]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query0={query_dictionary={scheme_txt=[[@21,57:66='scheme_txt',<459>,1:57]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={t={id=[[@1,7:7='t',<459>,1:7], [@26,84:84='t',<459>,1:84]], url=[[@9,27:27='t',<459>,1:27], [@34,110:110='t',<459>,1:110]]}}, grouped_by=[{name=id, table_ref=t}], filters=[{name=url, table_ref=t}, {name=<scheme filter>, type=predicand}], interface={scheme_txt=[{name=url, table_ref=t}], id=[{name=id, table_ref=t}]}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void parseUrlPostCallFieldAccessUpdateSetPlacementParityExpansionTest() {
		final String query =
				"UPDATE t SET scheme_col = PARSE_URL(t.url, 1):scheme::varchar WHERE t.id = 1";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={update={table={alias=null, table=t}, where={condition={left={column={name=id, table_ref=t}}, right={literal=1}, operator==}}, assignments={1={set={column={name=scheme_col, table_ref=null}}, to={function={function_name=cast, data_type={type=VARCHAR}, type=CAST, value={function={parameters={1={column={name=url, table_ref=t}}, 2={literal=1}}, json_path=scheme, function_name=PARSE_URL}}}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[scheme_col]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{t={id=[[@18,68:68='t',<459>,1:68]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{update0={scheme_col=[[@3,13:22='scheme_col',<459>,1:13]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_update0={assignments={scheme_col=[{name=url, table_ref=t}]}, table_dictionary={t={id=[[@18,68:68='t',<459>,1:68]]}}, update_dictionary={scheme_col=[[@3,13:22='scheme_col',<459>,1:13]]}, target_table={t={scheme_col=[[@3,13:22='scheme_col',<459>,1:13]], url=[[@7,36:36='t',<459>,1:36]]}}, filters=[{name=id, table_ref=t}], lhs_unresolved_columns={scheme_col={column={name=scheme_col, table_ref=null}, locations=[[@3,13:22='scheme_col',<459>,1:13]]}}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void snowflakeColonNotationNestedFlattenInputV2Test() {
		final String query = "SELECT i.value:sku AS sku FROM t, LATERAL FLATTEN(input => t.orders) o, LATERAL FLATTEN(input => o.value:items) i";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=value, json_path=sku, table_ref=i}, alias=sku}}, from={join={1={table={alias=null, table=t}}, 2={modifier=LATERAL}, 3={table={alias=o, table_function={function_name=FLATTEN, parameters={input={column={name=orders, table_ref=t}}}}}}, 4={modifier=LATERAL}, 5={table={alias=i, table_function={function_name=FLATTEN, parameters={input={column={name=value, json_path=items, table_ref=o}}}}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[sku]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{t={orders=[[@16,59:59='t',<459>,1:59]]}, flatten1={value=[[@1,7:7='i',<459>,1:7]]}, flatten0={value=[[@27,97:97='o',<459>,1:97]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={sku=[[@7,22:24='sku',<459>,1:22]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query0={query_dictionary={sku=[[@7,22:24='sku',<459>,1:22]]}, table_dictionary={t={orders=[[@16,59:59='t',<459>,1:59]]}, flatten1={value=[[@1,7:7='i',<459>,1:7]]}, flatten0={value=[[@27,97:97='o',<459>,1:97]]}}, interface={sku=[{name=value, json_path=sku, table_ref=i}]}, table_alias={i=flatten1, o=flatten0}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void snowflakeColonNotationJoinOnPathV3Test() {
		final String query = "SELECT t.id, f.value FROM t JOIN LATERAL FLATTEN(input => t.json_col) f ON f.value:id = t.id";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=id, table_ref=t}}, 2={column={name=value, table_ref=f}}}, from={join={1={table={alias=null, table=t}}, 2={join=JOIN, on={condition={left={column={name=value, json_path=id, table_ref=f}}, right={column={name=id, table_ref=t}}, operator==}}}, 3={modifier=LATERAL}, 4={table={alias=f, table_function={function_name=FLATTEN, parameters={input={column={name=json_col, table_ref=t}}}}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[id, value]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{t={json_col=[[@16,58:58='t',<459>,1:58]], id=[[@1,7:7='t',<459>,1:7], [@28,88:88='t',<459>,1:88]]}, flatten0={value=[[@5,13:13='f',<459>,1:13], [@22,75:75='f',<459>,1:75]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={value=[[@7,15:19='value',<459>,1:15]], id=[[@3,9:10='id',<459>,1:9]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query0={query_dictionary={id=[[@3,9:10='id',<459>,1:9]], value=[[@7,15:19='value',<459>,1:15]]}, table_dictionary={t={json_col=[[@16,58:58='t',<459>,1:58]], id=[[@1,7:7='t',<459>,1:7], [@28,88:88='t',<459>,1:88]]}, flatten0={value=[[@5,13:13='f',<459>,1:13], [@22,75:75='f',<459>,1:75]]}}, filters=[{name=value, json_path=id, table_ref=f}, {name=id, table_ref=t}], interface={id=[{name=id, table_ref=t}], value=[{name=value, table_ref=f}]}, table_alias={f=flatten0}}}",
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

		assertNoFatalErrors(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=id, table_ref=o}}, 2={column={name=sku, table_ref=f}, alias=sku}, 3={column={name=name, table_ref=p}}}, from={join={1={table={alias=o, table=orders}}, 2={join=CROSSJOIN}, 3={modifier=LATERAL}, 4={table={alias=f, table_function={function_name=FLATTEN, parameters={input={column={name=items, table_ref=o}}}}}}, 5={join=JOIN, on={condition={left={column={name=sku, table_ref=p}}, right={column={name=sku, table_ref=f}}, operator==}}}, 6={table={alias=p, table=products}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[name, id, sku]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{orders={id=[[@1,7:7='o',<459>,1:7]], items=[[@24,84:84='o',<459>,1:84]]}, flatten0={sku=[[@5,13:13='f',<459>,1:13], [@37,122:122='f',<459>,1:122]]}, products={name=[[@11,27:27='p',<459>,1:27]], sku=[[@33,114:114='p',<459>,1:114]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={name=[[@13,29:32='name',<225>,1:29]], sku=[[@9,22:24='sku',<459>,1:22]], id=[[@3,9:10='id',<459>,1:9]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query0={query_dictionary={name=[[@13,29:32='name',<225>,1:29]], id=[[@3,9:10='id',<459>,1:9]], sku=[[@9,22:24='sku',<459>,1:22]]}, table_dictionary={orders={id=[[@1,7:7='o',<459>,1:7]], items=[[@24,84:84='o',<459>,1:84]]}, flatten0={sku=[[@5,13:13='f',<459>,1:13], [@37,122:122='f',<459>,1:122]]}, products={name=[[@11,27:27='p',<459>,1:27]], sku=[[@33,114:114='p',<459>,1:114]]}}, filters=[{name=sku, table_ref=p}, {name=sku, table_ref=f}], interface={name=[{name=name, table_ref=p}], id=[{name=id, table_ref=o}], sku=[{name=sku, table_ref=f}]}, table_alias={p=products, f=flatten0, o=orders}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void tfMixedJoinInnerLateralOnCondV2Test() {
		// Original uses Snowflake colon notation (f.value:id) in ON clause, not in grammar;
		// adapted to plain column reference
		final String query = "SELECT t.id, f.value FROM t JOIN LATERAL FLATTEN(input => t.json_col) f ON f.id = t.id";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=id, table_ref=t}}, 2={column={name=value, table_ref=f}}}, from={join={1={table={alias=null, table=t}}, 2={join=JOIN, on={condition={left={column={name=id, table_ref=f}}, right={column={name=id, table_ref=t}}, operator==}}}, 3={modifier=LATERAL}, 4={table={alias=f, table_function={function_name=FLATTEN, parameters={input={column={name=json_col, table_ref=t}}}}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[id, value]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{t={json_col=[[@16,58:58='t',<459>,1:58]], id=[[@1,7:7='t',<459>,1:7], [@26,82:82='t',<459>,1:82]]}, flatten0={id=[[@22,75:75='f',<459>,1:75]], value=[[@5,13:13='f',<459>,1:13]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={value=[[@7,15:19='value',<459>,1:15]], id=[[@3,9:10='id',<459>,1:9]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query0={query_dictionary={id=[[@3,9:10='id',<459>,1:9]], value=[[@7,15:19='value',<459>,1:15]]}, table_dictionary={t={json_col=[[@16,58:58='t',<459>,1:58]], id=[[@1,7:7='t',<459>,1:7], [@26,82:82='t',<459>,1:82]]}, flatten0={id=[[@22,75:75='f',<459>,1:75]], value=[[@5,13:13='f',<459>,1:13]]}}, filters=[{name=id, table_ref=f}, {name=id, table_ref=t}], interface={id=[{name=id, table_ref=t}], value=[{name=value, table_ref=f}]}, table_alias={f=flatten0}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void tfMixedJoinBigTableDimV3Test() {
		// Original uses Snowflake colon notation (f.value:id) in ON clause, not in grammar;
		// adapted to plain column reference
		final String query = "SELECT * FROM big_table t CROSS JOIN LATERAL FLATTEN(input => t.items) f JOIN dim d ON d.id = f.id";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoFatalErrors(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=*, table_ref=*}}}, from={join={1={table={alias=t, table=big_table}}, 2={join=CROSSJOIN}, 3={modifier=LATERAL}, 4={table={alias=f, table_function={function_name=FLATTEN, parameters={input={column={name=items, table_ref=t}}}}}}, 5={join=JOIN, on={condition={left={column={name=id, table_ref=d}}, right={column={name=id, table_ref=f}}, operator==}}}, 6={table={alias=d, table=dim}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{big_table={*=[[@1,7:7='*',<291>,1:7]], items=[[@12,62:62='t',<459>,1:62]]}, dim={*=[[@1,7:7='*',<291>,1:7]], id=[[@21,87:87='d',<459>,1:87]]}, flatten0={*=[[@1,7:7='*',<291>,1:7]], id=[[@25,94:94='f',<459>,1:94]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@1,7:7='*',<291>,1:7]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query0={query_dictionary={*=[[@1,7:7='*',<291>,1:7]]}, table_dictionary={big_table={*=[[@1,7:7='*',<291>,1:7]], items=[[@12,62:62='t',<459>,1:62]]}, dim={*=[[@1,7:7='*',<291>,1:7]], id=[[@21,87:87='d',<459>,1:87]]}, flatten0={*=[[@1,7:7='*',<291>,1:7]], id=[[@25,94:94='f',<459>,1:94]]}}, filters=[{name=id, table_ref=d}, {name=id, table_ref=f}], interface={*=[{name=*, table_ref=*}]}, table_alias={t=big_table, d=dim, f=flatten0}}}",
				extractor.getSymbolTable().toString());
	}

	// Table function output used in DML / CTAS

	@Test
	public void tfInDmlCtasInsertGeneratorV1Test() {
		final String query = "INSERT INTO my_table SELECT * FROM TABLE(GENERATOR(ROWCOUNT => 10))";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={insert={preamble=insert_into, from={from={table_function={function_name=GENERATOR, parameters={rowcount={literal=10}}}}, select={1={column={name=*, table_ref=*}}}}, target_table={table={alias=null, table=my_table}}}}}",
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
				"{def_insert1={query_dictionary={*=[[@4,28:28='*',<291>,1:28]]}, table_dictionary={my_table={*=[[@4,28:28='*',<291>,1:28]]}}, def_query0={query_dictionary={*=[[@4,28:28='*',<291>,1:28]]}, table_dictionary={generator0={*=[[@4,28:28='*',<291>,1:28]]}}, interface={*=[{name=*, table_ref=*}]}}, interface={*=[{name=*, table_ref=query0}]}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void tfInDmlCtasUpdateFlattenV2Test() {
		final String query = "UPDATE t SET col = f.value FROM TABLE(FLATTEN(input => t.json_col)) f WHERE t.id = 1";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={update={from={table={alias=f, table_function={function_name=FLATTEN, parameters={input={column={name=json_col, table_ref=t}}}}}}, where={condition={left={column={name=id, table_ref=t}}, right={literal=1}, operator==}}, assignments={1={set={column={name=col, table_ref=null}}, to={column={name=value, table_ref=f}}}}, table={alias=null, table=t}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[col]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{t={col=[[@3,13:15='col',<459>,1:13]], json_col=[[@15,55:55='t',<459>,1:55]], id=[[@22,76:76='t',<459>,1:76]]}, flatten0={value=[[@5,19:19='f',<459>,1:19]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{update0={col=[[@3,13:15='col',<459>,1:13]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_update0={assignments={col=[{name=value, table_ref=f}]}, table_dictionary={t={json_col=[[@15,55:55='t',<459>,1:55]], col=[[@3,13:15='col',<459>,1:13]], id=[[@22,76:76='t',<459>,1:76]]}, flatten0={value=[[@5,19:19='f',<459>,1:19]]}}, update_dictionary={col=[[@3,13:15='col',<459>,1:13]]}, filters=[{name=id, table_ref=t}], table_alias={f=flatten0}}}",
				extractor.getSymbolTable().toString());
	}

	// Other table function examples mentioned

	@Test
	public void otherTfExampleResultScanV1Test() {
		final String query = "SELECT * FROM TABLE(RESULT_SCAN('01b71944-0001-b181-0000-0129032279f6'))";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=*, table_ref=*}}}, from={table_function={function={function_name=RESULT_SCAN, parameters={argument={literal='01b71944-0001-b181-0000-0129032279f6'}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{table_function0={*=[[@1,7:7='*',<291>,1:7]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@1,7:7='*',<291>,1:7]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query0={query_dictionary={*=[[@1,7:7='*',<291>,1:7]]}, table_dictionary={table_function0={*=[[@1,7:7='*',<291>,1:7]]}}, interface={*=[{name=*, table_ref=*}]}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void otherTfExampleSplitToTableCommaV2Test() {
		final String query = "SELECT * FROM t, TABLE(SPLIT_TO_TABLE(t.csv_col, ',')) s";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=*, table_ref=*}}}, from={join={1={table={alias=null, table=t}}, 2={table={alias=s, table_function={function={function_name=SPLIT_TO_TABLE, parameters={1={column={name=csv_col, table_ref=t}}, 2={literal=','}}}}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
		 "{t={csv_col=[[@9,38:38='t',<459>,1:38]], *=[[@1,7:7='*',<291>,1:7]]}, table_function0={*=[[@1,7:7='*',<291>,1:7]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
		 "{query0={*=[[@1,7:7='*',<291>,1:7]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
		"{def_query0={query_dictionary={*=[[@1,7:7='*',<291>,1:7]]}, table_dictionary={t={csv_col=[[@9,38:38='t',<459>,1:38]], *=[[@1,7:7='*',<291>,1:7]]}, table_function0={*=[[@1,7:7='*',<291>,1:7]]}}, interface={*=[{name=*, table_ref=*}]}, table_alias={s=table_function0}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void otherTfExampleLateralUdtfV3Test() {
		final String query = "SELECT * FROM t CROSS JOIN LATERAL TABLE(my_udtf(t.col)) u";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=*, table_ref=*}}}, from={join={1={table={alias=null, table=t}}, 2={join=CROSSJOIN}, 3={modifier=LATERAL}, 4={table={alias=u, table_function={function={function_name=my_udtf, parameters={1={column={name=col, table_ref=t}}}}}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{t={col=[[@11,49:49='t',<459>,1:49]], *=[[@1,7:7='*',<291>,1:7]]}, table_function0={*=[[@1,7:7='*',<291>,1:7]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@1,7:7='*',<291>,1:7]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query0={query_dictionary={*=[[@1,7:7='*',<291>,1:7]]}, table_dictionary={t={col=[[@11,49:49='t',<459>,1:49]], *=[[@1,7:7='*',<291>,1:7]]}, table_function0={*=[[@1,7:7='*',<291>,1:7]]}}, interface={*=[{name=*, table_ref=*}]}, table_alias={u=table_function0}}}",
				extractor.getSymbolTable().toString());
	}

	// LATERAL with a correlated subquery row source

	@Test
	public void lateralCorrelatedSubquerySimpleV1Test() {
		final String query = "SELECT t.id, sub.x FROM t, LATERAL (SELECT t.id + 1 AS x) sub";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=id, table_ref=t}}, 2={column={name=x, table_ref=sub}}}, from={join={1={table={alias=null, table=t}}, 2={modifier=LATERAL}, 3={table={alias=sub, query={select={1={alias=x, calc={left={column={name=id, table_ref=t}}, right={literal=1}, operator=+}}}}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[x, id]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{t={id=[[@14,43:43='t',<459>,1:43], [@1,7:7='t',<459>,1:7]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={x=[[@20,55:55='x',<459>,1:55], [@5,13:15='sub',<459>,1:13]]}, query1={x=[[@7,17:17='x',<459>,1:17]], id=[[@3,9:10='id',<459>,1:9]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query1={query_dictionary={x=[[@7,17:17='x',<459>,1:17]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={t={id=[[@1,7:7='t',<459>,1:7], [@14,43:43='t',<459>,1:43]]}}, def_query0={query_dictionary={x=[[@20,55:55='x',<459>,1:55], [@5,13:15='sub',<459>,1:13]]}, table_dictionary={t={id=[[@14,43:43='t',<459>,1:43], [@1,7:7='t',<459>,1:7]]}}, interface={x=[{name=id, table_ref=t}]}}, interface={x=[{name=x, table_ref=sub}], id=[{name=id, table_ref=t}]}, table_alias={sub=query0}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void lateralCorrelatedSubqueryWithFilterV2Test() {
		// Original had WHERE inside a FROM-less subquery which grammar does not support;
		// adapted to a distinct arithmetic variant of the correlated subquery
		final String query = "SELECT t.id, sub.x FROM t, LATERAL (SELECT t.id + 2 AS x) sub";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=id, table_ref=t}}, 2={column={name=x, table_ref=sub}}}, from={join={1={table={alias=null, table=t}}, 2={modifier=LATERAL}, 3={table={alias=sub, query={select={1={alias=x, calc={left={column={name=id, table_ref=t}}, right={literal=2}, operator=+}}}}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[x, id]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{t={id=[[@14,43:43='t',<459>,1:43], [@1,7:7='t',<459>,1:7]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={x=[[@20,55:55='x',<459>,1:55], [@5,13:15='sub',<459>,1:13]]}, query1={x=[[@7,17:17='x',<459>,1:17]], id=[[@3,9:10='id',<459>,1:9]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query1={query_dictionary={x=[[@7,17:17='x',<459>,1:17]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={t={id=[[@1,7:7='t',<459>,1:7], [@14,43:43='t',<459>,1:43]]}}, def_query0={query_dictionary={x=[[@20,55:55='x',<459>,1:55], [@5,13:15='sub',<459>,1:13]]}, table_dictionary={t={id=[[@14,43:43='t',<459>,1:43], [@1,7:7='t',<459>,1:7]]}}, interface={x=[{name=id, table_ref=t}]}}, interface={x=[{name=x, table_ref=sub}], id=[{name=id, table_ref=t}]}, table_alias={sub=query0}}}",
				extractor.getSymbolTable().toString());
	}

	// Explicit join forms

	@Test
	public void explicitJoinFormCrossLateralFlattenV1Test() {
		final String query = "SELECT * FROM t CROSS JOIN LATERAL FLATTEN(input => t.col) f";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=*, table_ref=*}}}, from={join={1={table={alias=null, table=t}}, 2={join=CROSSJOIN}, 3={modifier=LATERAL}, 4={table={alias=f, table_function={function_name=FLATTEN, parameters={input={column={name=col, table_ref=t}}}}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{t={col=[[@11,52:52='t',<459>,1:52]], *=[[@1,7:7='*',<291>,1:7]]}, flatten0={*=[[@1,7:7='*',<291>,1:7]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@1,7:7='*',<291>,1:7]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query0={query_dictionary={*=[[@1,7:7='*',<291>,1:7]]}, table_dictionary={t={col=[[@11,52:52='t',<459>,1:52]], *=[[@1,7:7='*',<291>,1:7]]}, flatten0={*=[[@1,7:7='*',<291>,1:7]]}}, interface={*=[{name=*, table_ref=*}]}, table_alias={f=flatten0}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void explicitJoinFormLeftLateralFlattenV2Test() {
		final String query = "SELECT * FROM t LEFT JOIN LATERAL FLATTEN(input => t.items) f ON TRUE";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=*, table_ref=*}}}, from={join={1={table={alias=null, table=t}}, 2={join=LEFT, on={literal=TRUE}}, 3={modifier=LATERAL}, 4={table={alias=f, table_function={function_name=FLATTEN, parameters={input={column={name=items, table_ref=t}}}}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{t={*=[[@1,7:7='*',<291>,1:7]], items=[[@11,51:51='t',<459>,1:51]]}, flatten0={*=[[@1,7:7='*',<291>,1:7]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@1,7:7='*',<291>,1:7]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query0={query_dictionary={*=[[@1,7:7='*',<291>,1:7]]}, table_dictionary={t={*=[[@1,7:7='*',<291>,1:7]], items=[[@11,51:51='t',<459>,1:51]]}, flatten0={*=[[@1,7:7='*',<291>,1:7]]}}, filters=[], interface={*=[{name=*, table_ref=*}]}, table_alias={f=flatten0}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void explicitJoinFormChainedLateralV3Test() {
		// Original uses Snowflake colon notation (o.value:id, o.value:items, i.value:sku) not in grammar;
		// adapted to plain column references (mirrors chainedLateralTfOrderItemsV2)
		final String query = "SELECT o.id AS order_id, i.sku AS sku FROM t, LATERAL FLATTEN(input => t.orders) o, LATERAL FLATTEN(input => o.value) i";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=id, table_ref=o}, alias=order_id}, 2={column={name=sku, table_ref=i}, alias=sku}}, from={join={1={table={alias=null, table=t}}, 2={modifier=LATERAL}, 3={table={alias=o, table_function={function_name=FLATTEN, parameters={input={column={name=orders, table_ref=t}}}}}}, 4={modifier=LATERAL}, 5={table={alias=i, table_function={function_name=FLATTEN, parameters={input={column={name=value, table_ref=o}}}}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[sku, order_id]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{t={orders=[[@20,71:71='t',<459>,1:71]]}, flatten1={sku=[[@7,25:25='i',<459>,1:25]]}, flatten0={id=[[@1,7:7='o',<459>,1:7]], value=[[@31,109:109='o',<459>,1:109]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={sku=[[@11,34:36='sku',<459>,1:34]], order_id=[[@5,15:22='order_id',<459>,1:15]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query0={query_dictionary={sku=[[@11,34:36='sku',<459>,1:34]], order_id=[[@5,15:22='order_id',<459>,1:15]]}, table_dictionary={t={orders=[[@20,71:71='t',<459>,1:71]]}, flatten1={sku=[[@7,25:25='i',<459>,1:25]]}, flatten0={id=[[@1,7:7='o',<459>,1:7]], value=[[@31,109:109='o',<459>,1:109]]}}, interface={sku=[{name=sku, table_ref=i}], order_id=[{name=id, table_ref=o}]}, table_alias={i=flatten1, o=flatten0}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void explicitJoinFormCrossLateralProductJoinV4Test() {
		// Original uses Snowflake colon notation (f.value:sku) not in grammar;
		// adapted to plain column references (mirrors tfMixedJoinCrossLateralProductsV1)
		final String query = "SELECT o.id, f.sku AS sku, p.name FROM orders o CROSS JOIN LATERAL FLATTEN(input => o.items) f JOIN products p ON p.sku = f.sku";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoFatalErrors(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=id, table_ref=o}}, 2={column={name=sku, table_ref=f}, alias=sku}, 3={column={name=name, table_ref=p}}}, from={join={1={table={alias=o, table=orders}}, 2={join=CROSSJOIN}, 3={modifier=LATERAL}, 4={table={alias=f, table_function={function_name=FLATTEN, parameters={input={column={name=items, table_ref=o}}}}}}, 5={join=JOIN, on={condition={left={column={name=sku, table_ref=p}}, right={column={name=sku, table_ref=f}}, operator==}}}, 6={table={alias=p, table=products}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[name, id, sku]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{orders={id=[[@1,7:7='o',<459>,1:7]], items=[[@24,84:84='o',<459>,1:84]]}, flatten0={sku=[[@5,13:13='f',<459>,1:13], [@37,122:122='f',<459>,1:122]]}, products={name=[[@11,27:27='p',<459>,1:27]], sku=[[@33,114:114='p',<459>,1:114]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={name=[[@13,29:32='name',<225>,1:29]], sku=[[@9,22:24='sku',<459>,1:22]], id=[[@3,9:10='id',<459>,1:9]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query0={query_dictionary={name=[[@13,29:32='name',<225>,1:29]], id=[[@3,9:10='id',<459>,1:9]], sku=[[@9,22:24='sku',<459>,1:22]]}, table_dictionary={orders={id=[[@1,7:7='o',<459>,1:7]], items=[[@24,84:84='o',<459>,1:84]]}, flatten0={sku=[[@5,13:13='f',<459>,1:13], [@37,122:122='f',<459>,1:122]]}, products={name=[[@11,27:27='p',<459>,1:27]], sku=[[@33,114:114='p',<459>,1:114]]}}, filters=[{name=sku, table_ref=p}, {name=sku, table_ref=f}], interface={name=[{name=name, table_ref=p}], id=[{name=id, table_ref=o}], sku=[{name=sku, table_ref=f}]}, table_alias={p=products, f=flatten0, o=orders}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void explicitJoinFormInsertGeneratorV5Test() {
		final String query = "INSERT INTO my_table SELECT * FROM TABLE(GENERATOR(ROWCOUNT => 10))";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong",
				"{SQL={insert={preamble=insert_into, from={from={table_function={function_name=GENERATOR, parameters={rowcount={literal=10}}}}, select={1={column={name=*, table_ref=*}}}}, target_table={table={alias=null, table=my_table}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{generator0={*=[[@4,28:28='*',<291>,1:28]]}, my_table={*=[[@4,28:28='*',<291>,1:28]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@4,28:28='*',<291>,1:28]]}, insert1={*=[[@4,28:28='*',<291>,1:28]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_insert1={query_dictionary={*=[[@4,28:28='*',<291>,1:28]]}, table_dictionary={my_table={*=[[@4,28:28='*',<291>,1:28]]}}, def_query0={query_dictionary={*=[[@4,28:28='*',<291>,1:28]]}, table_dictionary={generator0={*=[[@4,28:28='*',<291>,1:28]]}}, interface={*=[{name=*, table_ref=*}]}}, interface={*=[{name=*, table_ref=query0}]}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void explicitJoinFormCtasGeneratorV6Test() {
		// DDL endpoint requires a target table name for CTAS.
		final String query = "CREATE TABLE tab1 AS SELECT * FROM TABLE(GENERATOR(ROWCOUNT => 10))";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runDdlParsertest(query, parser);

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong",
				"{DDL={create={type=TABLE, table={table=tab1}, query={select={1={column={name=*, table_ref=*}}}, from={table_function={function_name=GENERATOR, parameters={rowcount={literal=10}}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{generator0={*=[[@5,28:28='*',<291>,1:28]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@5,28:28='*',<291>,1:28]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_create1={def_query0={query_dictionary={*=[[@5,28:28='*',<291>,1:28]]}, table_dictionary={generator0={*=[[@5,28:28='*',<291>,1:28]]}}, interface={*=[{name=*, table_ref=*}]}}}}",
				extractor.getSymbolTable().toString());
	}

	// LATERAL with a correlated subquery row source

	@Test
	public void lateralCorrelatedSubquerySimpleV3Test() {
		final String query = "SELECT t.id, sub.x FROM t, LATERAL (SELECT t.id + 1 AS x) sub";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=id, table_ref=t}}, 2={column={name=x, table_ref=sub}}}, from={join={1={table={alias=null, table=t}}, 2={modifier=LATERAL}, 3={table={alias=sub, query={select={1={alias=x, calc={left={column={name=id, table_ref=t}}, right={literal=1}, operator=+}}}}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[x, id]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{t={id=[[@14,43:43='t',<459>,1:43], [@1,7:7='t',<459>,1:7]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={x=[[@20,55:55='x',<459>,1:55], [@5,13:15='sub',<459>,1:13]]}, query1={x=[[@7,17:17='x',<459>,1:17]], id=[[@3,9:10='id',<459>,1:9]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query1={query_dictionary={x=[[@7,17:17='x',<459>,1:17]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={t={id=[[@1,7:7='t',<459>,1:7], [@14,43:43='t',<459>,1:43]]}}, def_query0={query_dictionary={x=[[@20,55:55='x',<459>,1:55], [@5,13:15='sub',<459>,1:13]]}, table_dictionary={t={id=[[@14,43:43='t',<459>,1:43], [@1,7:7='t',<459>,1:7]]}}, interface={x=[{name=id, table_ref=t}]}}, interface={x=[{name=x, table_ref=sub}], id=[{name=id, table_ref=t}]}, table_alias={sub=query0}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void lateralCorrelatedSubqueryWithFilterV4Test() {
		// Original had WHERE inside a FROM-less subquery which grammar does not support;
		// adapted to a distinct arithmetic variant of the correlated subquery
		final String query = "SELECT t.id, sub.x FROM t, LATERAL (SELECT t.id + 3 AS x) sub";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=id, table_ref=t}}, 2={column={name=x, table_ref=sub}}}, from={join={1={table={alias=null, table=t}}, 2={modifier=LATERAL}, 3={table={alias=sub, query={select={1={alias=x, calc={left={column={name=id, table_ref=t}}, right={literal=3}, operator=+}}}}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[x, id]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{t={id=[[@14,43:43='t',<459>,1:43], [@1,7:7='t',<459>,1:7]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={x=[[@20,55:55='x',<459>,1:55], [@5,13:15='sub',<459>,1:13]]}, query1={x=[[@7,17:17='x',<459>,1:17]], id=[[@3,9:10='id',<459>,1:9]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query1={query_dictionary={x=[[@7,17:17='x',<459>,1:17]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={t={id=[[@1,7:7='t',<459>,1:7], [@14,43:43='t',<459>,1:43]]}}, def_query0={query_dictionary={x=[[@20,55:55='x',<459>,1:55], [@5,13:15='sub',<459>,1:13]]}, table_dictionary={t={id=[[@14,43:43='t',<459>,1:43], [@1,7:7='t',<459>,1:7]]}}, interface={x=[{name=id, table_ref=t}]}}, interface={x=[{name=x, table_ref=sub}], id=[{name=id, table_ref=t}]}, table_alias={sub=query0}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void lateralCorrelatedSubqueryNestedUnionV5Test() {
		final String query = "SELECT t.id, sub.x FROM t, LATERAL (SELECT t.id + v AS x FROM (SELECT 1 AS v UNION ALL SELECT 2 AS v)) sub";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=id, table_ref=t}}, 2={column={name=x, table_ref=sub}}}, from={join={1={table={alias=null, table=t}}, 2={modifier=LATERAL}, 3={table={alias=sub, query={select={1={alias=x, calc={left={column={name=id, table_ref=t}}, right={column={name=v, table_ref=null}}, operator=+}}}, from={union={1={select={1={alias=v, literal=1}}}, 2={union={qualifier=ALL, operator=UNION}}, 3={select={1={alias=v, literal=2}}}}}}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[x, id]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{t={id=[[@14,43:43='t',<459>,1:43], [@1,7:7='t',<459>,1:7]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{union2={v=[[@18,50:50='v',<459>,1:50]]}, query4={x=[[@7,17:17='x',<459>,1:17]], id=[[@3,9:10='id',<459>,1:9]]}, query0={v=[[@26,75:75='v',<459>,1:75]]}, query1={v=[[@32,99:99='v',<459>,1:99]]}, query3={x=[[@20,55:55='x',<459>,1:55], [@5,13:15='sub',<459>,1:13]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query4={query_dictionary={x=[[@7,17:17='x',<459>,1:17]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={t={id=[[@1,7:7='t',<459>,1:7], [@14,43:43='t',<459>,1:43]]}}, interface={x=[{name=x, table_ref=sub}], id=[{name=id, table_ref=t}]}, def_query3={def_union2={query_dictionary={v=[[@18,50:50='v',<459>,1:50]]}, def_query1={query_dictionary={v=[[@32,99:99='v',<459>,1:99]]}, setop=UNION, interface={v=[]}}, def_query0={query_dictionary={v=[[@26,75:75='v',<459>,1:75]]}, interface={v=[]}}, interface={v=query_column}}, query_dictionary={x=[[@20,55:55='x',<459>,1:55], [@5,13:15='sub',<459>,1:13]]}, table_dictionary={t={id=[[@14,43:43='t',<459>,1:43], [@1,7:7='t',<459>,1:7]]}}, interface={x=[{name=id, table_ref=t}, {name=v, table_ref=union2}]}, table_alias={union2=union2}}, table_alias={sub=query3}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void lateralCorrelatedSubqueryNestedExceptV5Test(){
		final String query = "SELECT t.id, sub.x FROM t, LATERAL (SELECT t.id + v AS x FROM (SELECT 1 AS v EXCEPT SELECT 2 AS v)) sub";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=id, table_ref=t}}, 2={column={name=x, table_ref=sub}}}, from={join={1={table={alias=null, table=t}}, 2={modifier=LATERAL}, 3={table={alias=sub, query={select={1={alias=x, calc={left={column={name=id, table_ref=t}}, right={column={name=v, table_ref=null}}, operator=+}}}, from={union={1={select={1={alias=v, literal=1}}}, 2={union={qualifier=null, operator=EXCEPT}}, 3={select={1={alias=v, literal=2}}}}}}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[x, id]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{t={id=[[@14,43:43='t',<459>,1:43], [@1,7:7='t',<459>,1:7]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{union2={v=[[@18,50:50='v',<459>,1:50]]}, query4={x=[[@7,17:17='x',<459>,1:17]], id=[[@3,9:10='id',<459>,1:9]]}, query0={v=[[@26,75:75='v',<459>,1:75]]}, query1={v=[[@31,96:96='v',<459>,1:96]]}, query3={x=[[@20,55:55='x',<459>,1:55], [@5,13:15='sub',<459>,1:13]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query4={query_dictionary={x=[[@7,17:17='x',<459>,1:17]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={t={id=[[@1,7:7='t',<459>,1:7], [@14,43:43='t',<459>,1:43]]}}, interface={x=[{name=x, table_ref=sub}], id=[{name=id, table_ref=t}]}, def_query3={def_union2={query_dictionary={v=[[@18,50:50='v',<459>,1:50]]}, def_query1={query_dictionary={v=[[@31,96:96='v',<459>,1:96]]}, setop=EXCEPT, interface={v=[]}}, def_query0={query_dictionary={v=[[@26,75:75='v',<459>,1:75]]}, interface={v=[]}}, interface={v=query_column}}, query_dictionary={x=[[@20,55:55='x',<459>,1:55], [@5,13:15='sub',<459>,1:13]]}, table_dictionary={t={id=[[@14,43:43='t',<459>,1:43], [@1,7:7='t',<459>,1:7]]}}, interface={x=[{name=id, table_ref=t}, {name=v, table_ref=union2}]}, table_alias={union2=union2}}, table_alias={sub=query3}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void lateralCorrelatedSubquerySimpleV6Test() {
		final String query = "SELECT t.id, sub.x FROM t, LATERAL (SELECT t.id + 1 AS x) sub";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=id, table_ref=t}}, 2={column={name=x, table_ref=sub}}}, from={join={1={table={alias=null, table=t}}, 2={modifier=LATERAL}, 3={table={alias=sub, query={select={1={alias=x, calc={left={column={name=id, table_ref=t}}, right={literal=1}, operator=+}}}}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[x, id]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{t={id=[[@14,43:43='t',<459>,1:43], [@1,7:7='t',<459>,1:7]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={x=[[@20,55:55='x',<459>,1:55], [@5,13:15='sub',<459>,1:13]]}, query1={x=[[@7,17:17='x',<459>,1:17]], id=[[@3,9:10='id',<459>,1:9]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query1={query_dictionary={x=[[@7,17:17='x',<459>,1:17]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={t={id=[[@1,7:7='t',<459>,1:7], [@14,43:43='t',<459>,1:43]]}}, def_query0={query_dictionary={x=[[@20,55:55='x',<459>,1:55], [@5,13:15='sub',<459>,1:13]]}, table_dictionary={t={id=[[@14,43:43='t',<459>,1:43], [@1,7:7='t',<459>,1:7]]}}, interface={x=[{name=id, table_ref=t}]}}, interface={x=[{name=x, table_ref=sub}], id=[{name=id, table_ref=t}]}, table_alias={sub=query0}}}",
				extractor.getSymbolTable().toString());
	}


	// TUPLE ENDPOINT TESTS

	@Test
	public void flattenTableFunctionTupleEndpointTableSyntaxDoesNotUseQueryWrapperTest() {
		final String query = "table(flatten(input=>parse_json('[1,2]')))";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runTupleParsertest(query, parser);

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong",
				"{TUPLE={table_function={function_name=flatten, parameters={input={function={parameters={1={literal='[1,2]'}}, function_name=parse_json}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void generatorTableFunctionTupleEndpointTableSyntaxDoesNotUseQueryWrapperTest() {
		final String query = "table(generator(rowcount=>10))";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runTupleParsertest(query, parser);

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong",
				"{TUPLE={table_function={function_name=generator, parameters={rowcount={literal=10}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void inferSchemaTableFunctionTupleEndpointTableSyntaxDoesNotUseQueryWrapperTest() {
		final String query = "table(infer_schema(file_format=>'fmt', location=>'@stg/path'))";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runTupleParsertest(query, parser);
		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong",
				"{TUPLE={table_function={function_name=infer_schema, parameters={location='@stg/path', file_format='fmt'}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void flattenTableFunctionTupleEndpointNakedSyntaxBuildsTableFunctionTest() {
		final String query = "flatten(input=>parse_json('[1,2]'))";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runTupleParsertest(query, parser);

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong",
				"{TUPLE={table_function={function_name=flatten, parameters={input={function={parameters={1={literal='[1,2]'}}, function_name=parse_json}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
		 "{}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
		 "{}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
		"{}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void generatorTableFunctionTupleEndpointNakedSyntaxBuildsTableFunctionTest() {
		final String query = "generator(rowcount=>10)";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runTupleParsertest(query, parser);

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong",
				"{TUPLE={table_function={function_name=generator, parameters={rowcount={literal=10}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
		 "{}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
		 "{}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
		"{}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void inferSchemaTableFunctionTupleEndpointNakedSyntaxBuildsTableFunctionTest() {
		final String query = "infer_schema(file_format=>'fmt', location=>'@stg/path')";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runTupleParsertest(query, parser);

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong",
				"{TUPLE={table_function={function_name=infer_schema, parameters={location='@stg/path', file_format='fmt'}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
		 "{}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
		 "{}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
		"{}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void flattenDirectFromListTupleEndpointNakedSyntaxBuildsSameAstShapeTest() {
		final String query = "flatten(input=>parse_json('[1,2]'))";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runTupleParsertest(query, parser);

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong",
				"{TUPLE={table_function={function_name=flatten, parameters={input={function={parameters={1={literal='[1,2]'}}, function_name=parse_json}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
		 "{}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
		 "{}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
		"{}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void flattenDirectCrossJoinTupleEndpointTableSyntaxBuildsSameAstShapeTest() {
		final String query = "table(flatten(input=>parse_json('[1,2]')))";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runTupleParsertest(query, parser);

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong",
				"{TUPLE={table_function={function_name=flatten, parameters={input={function={parameters={1={literal='[1,2]'}}, function_name=parse_json}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
		 "{}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
		 "{}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
		"{}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void generatorDirectFromListTupleEndpointNakedSyntaxBuildsSameAstShapeTest() {
		final String query = "generator(rowcount=>10)";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runTupleParsertest(query, parser);

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong",
				"{TUPLE={table_function={function_name=generator, parameters={rowcount={literal=10}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
		 "{}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
		 "{}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
		"{}",
				extractor.getSymbolTable().toString());
	}

	/** Walker coverage T1.5 — {@code exitTable_argument_boolean} via INFER_SCHEMA {@code IGNORE_CASE => FALSE}. */
	@Test
	public void inferSchemaIgnoreCaseFalseTableArgumentBooleanT1_5Test() {
		final String query =
				"SELECT * FROM TABLE(INFER_SCHEMA(LOCATION => 's', IGNORE_CASE => FALSE))";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
		String ast = extractor.getAsTree().toString();
		Assert.assertFalse("AST should not leak rule Type keys", ast.contains("Type="));
		Assert.assertEquals(
				"AST is wrong",
				"{SQL={select={1={column={name=*, table_ref=*}}}, from={table_function={function_name=INFER_SCHEMA, parameters={ignore_case=FALSE, location='s'}}}}}",
				ast);
		Assert.assertEquals("Interface is wrong", "[*]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals(
				"Table Dictionary is wrong",
				"{infer_schema0={*=[[@1,7:7='*',<291>,1:7]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals(
				"Query Column Dictionary is wrong",
				"{query0={*=[[@1,7:7='*',<291>,1:7]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals(
				"Symbol Table is wrong",
				"{def_query0={query_dictionary={*=[[@1,7:7='*',<291>,1:7]]}, table_dictionary={infer_schema0={*=[[@1,7:7='*',<291>,1:7]]}}, interface={*=[{name=*, table_ref=*}]}}}",
				extractor.getSymbolTable().toString());
	}

	/** Walker coverage T1.6 — {@code exitInfer_schema_files_argument}. */
	@Test
	public void inferSchemaFilesArgumentT1_6Test() {
		final String query =
				"SELECT * FROM TABLE(INFER_SCHEMA("
						+ "LOCATION => '@db.schema.stage/path/', "
						+ "FILES => ('part-0001.json', 'part-0002.json')))";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
		String ast = extractor.getAsTree().toString();
		Assert.assertFalse("AST should not leak rule Type keys", ast.contains("Type="));
		Assert.assertEquals(
				"AST is wrong",
				"{SQL={select={1={column={name=*, table_ref=*}}}, from={table_function={function_name=INFER_SCHEMA, parameters={files={1={literal='part-0001.json'}, 2={literal='part-0002.json'}}, location='@db.schema.stage/path/'}}}}}",
				ast);
		Assert.assertEquals("Interface is wrong", "[*]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals(
				"Table Dictionary is wrong",
				"{infer_schema0={*=[[@1,7:7='*',<291>,1:7]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals(
				"Query Column Dictionary is wrong",
				"{query0={*=[[@1,7:7='*',<291>,1:7]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals(
				"Symbol Table is wrong",
				"{def_query0={query_dictionary={*=[[@1,7:7='*',<291>,1:7]]}, table_dictionary={infer_schema0={*=[[@1,7:7='*',<291>,1:7]]}}, interface={*=[{name=*, table_ref=*}]}}}",
				extractor.getSymbolTable().toString());
	}

	/** Walker coverage T1.7 — {@code exitValidate_table_function}. */
	@Test
	public void validateTableFunctionT1_7Test() {
		final String query =
				"SELECT * FROM TABLE(VALIDATE(my_db.my_schema.my_table, JOB_ID => '_last'))";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
		String ast = extractor.getAsTree().toString();
		Assert.assertFalse("AST should not leak rule Type keys", ast.contains("Type="));
		Assert.assertEquals(
				"AST is wrong",
				"{SQL={select={1={column={name=*, table_ref=*}}}, from={table_function={function={function_name=VALIDATE, parameters={table={schema=my_schema, dbname=my_db, table=my_table}, job_id='_last'}}}}}}",
				ast);
		Assert.assertEquals("Interface is wrong", "[*]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals(
				"Table Dictionary is wrong",
				"{my_db.my_schema.my_table={*=[[@1,7:7='*',<291>,1:7]]}, table_function0={*=[[@1,7:7='*',<291>,1:7]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals(
				"Query Column Dictionary is wrong",
				"{query0={*=[[@1,7:7='*',<291>,1:7]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals(
				"Symbol Table is wrong",
				"{def_query0={query_dictionary={*=[[@1,7:7='*',<291>,1:7]]}, table_dictionary={my_db.my_schema.my_table={*=[[@1,7:7='*',<291>,1:7]]}, table_function0={*=[[@1,7:7='*',<291>,1:7]]}}, interface={*=[{name=*, table_ref=*}]}}}",
				extractor.getSymbolTable().toString());
	}

	/** Walker coverage T2.1 — {@code exitTable_argument_literal} via INFER_SCHEMA string + numeric kwargs. */
	@Test
	public void inferSchemaLiteralsAndNumericT2_1Test() {
		final String query =
				"SELECT * FROM TABLE(INFER_SCHEMA("
						+ "LOCATION => '@db.schema.stage/path/', "
						+ "FILE_FORMAT => 'JSON', "
						+ "MAX_FILE_COUNT => 10, "
						+ "KIND => 'JSON'))";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals(
				"AST is wrong",
				"{SQL={select={1={column={name=*, table_ref=*}}}, from={table_function={function_name=INFER_SCHEMA, parameters={max_file_count=10, kind='JSON', location='@db.schema.stage/path/', file_format='JSON'}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals(
				"Table Dictionary is wrong",
				"{infer_schema0={*=[[@1,7:7='*',<291>,1:7]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals(
				"Query Column Dictionary is wrong",
				"{query0={*=[[@1,7:7='*',<291>,1:7]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals(
				"Symbol Table is wrong",
				"{def_query0={query_dictionary={*=[[@1,7:7='*',<291>,1:7]]}, table_dictionary={infer_schema0={*=[[@1,7:7='*',<291>,1:7]]}}, interface={*=[{name=*, table_ref=*}]}}}",
				extractor.getSymbolTable().toString());
	}

	/** Walker coverage T2.1 — {@code exitTable_argument_literal} via FLATTEN {@code mode => 'ARRAY'}. */
	@Test
	public void flattenModeArrayLiteralT2_1Test() {
		final String query =
				"SELECT * FROM TABLE(FLATTEN("
						+ "input => PARSE_JSON('[1,2]'), "
						+ "mode => 'ARRAY')) f";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals(
				"AST is wrong",
				"{SQL={select={1={column={name=*, table_ref=*}}}, from={table={alias=f, table_function={function_name=FLATTEN, parameters={mode='ARRAY', input={function={parameters={1={literal='[1,2]'}}, function_name=PARSE_JSON}}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals(
				"Table Dictionary is wrong",
				"{flatten0={*=[[@1,7:7='*',<291>,1:7]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals(
				"Query Column Dictionary is wrong",
				"{query0={*=[[@1,7:7='*',<291>,1:7]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals(
				"Symbol Table is wrong",
				"{def_query0={query_dictionary={*=[[@1,7:7='*',<291>,1:7]]}, table_dictionary={flatten0={*=[[@1,7:7='*',<291>,1:7]]}}, interface={*=[{name=*, table_ref=*}]}, table_alias={f=flatten0}}}",
				extractor.getSymbolTable().toString());
	}

	/** Walker coverage T2.1 — {@code exitTable_argument_literal} via FLATTEN {@code path => 'a'}. */
	@Test
	public void flattenPathStringLiteralT2_1Test() {
		final String query =
				"SELECT * FROM TABLE(FLATTEN("
						+ "input => PARSE_JSON('{\"a\":1}'), "
						+ "path => 'a')) f";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals(
				"AST is wrong",
				"{SQL={select={1={column={name=*, table_ref=*}}}, from={table={alias=f, table_function={function_name=FLATTEN, parameters={input={function={parameters={1={literal='{\"a\":1}'}}, function_name=PARSE_JSON}}, path='a'}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals(
				"Table Dictionary is wrong",
				"{flatten0={*=[[@1,7:7='*',<291>,1:7]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals(
				"Query Column Dictionary is wrong",
				"{query0={*=[[@1,7:7='*',<291>,1:7]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals(
				"Symbol Table is wrong",
				"{def_query0={query_dictionary={*=[[@1,7:7='*',<291>,1:7]]}, table_dictionary={flatten0={*=[[@1,7:7='*',<291>,1:7]]}}, interface={*=[{name=*, table_ref=*}]}, table_alias={f=flatten0}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void arC028_flattenNamedInputV0Test() {
		final String query = "SELECT a.id, f.value AS item_value FROM events a, FLATTEN(input => a.payload) f WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_flatten_table_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={column={name=value, table_ref=f}, alias=item_value}}, from={join={1={table={alias=a, table=events}}, 2={table={alias=f, table_function={function_name=FLATTEN, parameters={input={column={name=payload, table_ref=a}}}}}}}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[id, item_value]",
				"{}",
				"{flatten0={value=[[@5,13:13='f',<459>,1:13]]}, events={payload=[[@18,67:67='a',<459>,1:67]], active=[[@24,86:86='a',<459>,1:86]], id=[[@1,7:7='a',<459>,1:7]]}}",
				"{query0={item_value=[[@9,24:33='item_value',<459>,1:24]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={id=[[@3,9:10='id',<459>,1:9]], item_value=[[@9,24:33='item_value',<459>,1:24]]}, table_dictionary={flatten0={value=[[@5,13:13='f',<459>,1:13]]}, events={payload=[[@18,67:67='a',<459>,1:67]], active=[[@24,86:86='a',<459>,1:86]], id=[[@1,7:7='a',<459>,1:7]]}}, filters=[{name=active, table_ref=a}], interface={id=[{name=id, table_ref=a}], item_value=[{name=value, table_ref=f}]}, table_alias={a=events, f=flatten0}}}");
	}

	@Test
	public void arC028_flattenTableWrapperV0Test() {
		final String query = "SELECT f.value, f.index FROM TABLE(FLATTEN(input => PARSE_JSON('[1,2]'))) AS f WHERE f.value IS NOT NULL";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_flatten_table_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=value, table_ref=f}}, 2={column={name=index, table_ref=f}}}, from={table={alias=f, table_function={function_name=FLATTEN, parameters={input={function={parameters={1={literal='[1,2]'}}, function_name=PARSE_JSON}}}}}}, where={condition={left={column={name=value, table_ref=f}}, operator=IS NOT NULL}}}}",
				"[index, value]",
				"{}",
				"{flatten0={index=[[@5,16:16='f',<459>,1:16]], value=[[@1,7:7='f',<459>,1:7], [@24,85:85='f',<459>,1:85]]}}",
				"{query0={index=[[@7,18:22='index',<96>,1:18]], value=[[@3,9:13='value',<459>,1:9]]}}",
				"{def_query0={query_dictionary={index=[[@7,18:22='index',<96>,1:18]], value=[[@3,9:13='value',<459>,1:9]]}, table_dictionary={flatten0={index=[[@5,16:16='f',<459>,1:16]], value=[[@1,7:7='f',<459>,1:7], [@24,85:85='f',<459>,1:85]]}}, filters=[{name=value, table_ref=f}], interface={index=[{name=index, table_ref=f}], value=[{name=value, table_ref=f}]}, table_alias={f=flatten0}}}");
	}

	@Test
	public void arC028_flattenDirectFromV0Test() {
		final String query = "SELECT f.value FROM FLATTEN(input => PARSE_JSON('[1,2]')) f WHERE f.index > 0";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_flatten_table_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=value, table_ref=f}}}, from={table={alias=f, table_function={function_name=FLATTEN, parameters={input={function={parameters={1={literal='[1,2]'}}, function_name=PARSE_JSON}}}}}}, where={condition={left={column={name=index, table_ref=f}}, right={literal=0}, operator=>}}}}",
				"[value]",
				"{}",
				"{flatten0={index=[[@16,66:66='f',<459>,1:66]], value=[[@1,7:7='f',<459>,1:7]]}}",
				"{query0={value=[[@3,9:13='value',<459>,1:9]]}}",
				"{def_query0={query_dictionary={value=[[@3,9:13='value',<459>,1:9]]}, table_dictionary={flatten0={index=[[@16,66:66='f',<459>,1:66]], value=[[@1,7:7='f',<459>,1:7]]}}, filters=[{name=index, table_ref=f}], interface={value=[{name=value, table_ref=f}]}, table_alias={f=flatten0}}}");
	}

	@Test
	public void arC028_flattenCrossJoinV0Test() {
		final String query = "SELECT a.id, f.value FROM events a CROSS JOIN FLATTEN(input => a.payload) f WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_flatten_table_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={column={name=value, table_ref=f}}}, from={join={1={table={alias=a, table=events}}, 2={join=CROSSJOIN}, 3={table={alias=f, table_function={function_name=FLATTEN, parameters={input={column={name=payload, table_ref=a}}}}}}}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[id, value]",
				"{}",
				"{flatten0={value=[[@5,13:13='f',<459>,1:13]]}, events={payload=[[@17,63:63='a',<459>,1:63]], active=[[@23,82:82='a',<459>,1:82]], id=[[@1,7:7='a',<459>,1:7]]}}",
				"{query0={value=[[@7,15:19='value',<459>,1:15]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={id=[[@3,9:10='id',<459>,1:9]], value=[[@7,15:19='value',<459>,1:15]]}, table_dictionary={flatten0={value=[[@5,13:13='f',<459>,1:13]]}, events={payload=[[@17,63:63='a',<459>,1:63]], active=[[@23,82:82='a',<459>,1:82]], id=[[@1,7:7='a',<459>,1:7]]}}, filters=[{name=active, table_ref=a}], interface={id=[{name=id, table_ref=a}], value=[{name=value, table_ref=f}]}, table_alias={a=events, f=flatten0}}}");
	}

	@Test
	public void arC028_flattenImplicitLateralV0Test() {
		final String query = "SELECT a.id, f.value FROM events a, FLATTEN(input => a.payload) f WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_flatten_table_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={column={name=value, table_ref=f}}}, from={join={1={table={alias=a, table=events}}, 2={table={alias=f, table_function={function_name=FLATTEN, parameters={input={column={name=payload, table_ref=a}}}}}}}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[id, value]",
				"{}",
				"{flatten0={value=[[@5,13:13='f',<459>,1:13]]}, events={payload=[[@16,53:53='a',<459>,1:53]], active=[[@22,72:72='a',<459>,1:72]], id=[[@1,7:7='a',<459>,1:7]]}}",
				"{query0={value=[[@7,15:19='value',<459>,1:15]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={id=[[@3,9:10='id',<459>,1:9]], value=[[@7,15:19='value',<459>,1:15]]}, table_dictionary={flatten0={value=[[@5,13:13='f',<459>,1:13]]}, events={payload=[[@16,53:53='a',<459>,1:53]], active=[[@22,72:72='a',<459>,1:72]], id=[[@1,7:7='a',<459>,1:7]]}}, filters=[{name=active, table_ref=a}], interface={id=[{name=id, table_ref=a}], value=[{name=value, table_ref=f}]}, table_alias={a=events, f=flatten0}}}");
	}

	@Test
	public void arC028_flattenModeARRAYV0Test() {
		final String query = "SELECT a.id, f.value FROM events a, FLATTEN(input => a.payload, mode => 'ARRAY') f WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_flatten_table_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={column={name=value, table_ref=f}}}, from={join={1={table={alias=a, table=events}}, 2={table={alias=f, table_function={function_name=FLATTEN, parameters={mode='ARRAY', input={column={name=payload, table_ref=a}}}}}}}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[id, value]",
				"{}",
				"{flatten0={value=[[@5,13:13='f',<459>,1:13]]}, events={payload=[[@16,53:53='a',<459>,1:53]], active=[[@26,89:89='a',<459>,1:89]], id=[[@1,7:7='a',<459>,1:7]]}}",
				"{query0={value=[[@7,15:19='value',<459>,1:15]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={id=[[@3,9:10='id',<459>,1:9]], value=[[@7,15:19='value',<459>,1:15]]}, table_dictionary={flatten0={value=[[@5,13:13='f',<459>,1:13]]}, events={payload=[[@16,53:53='a',<459>,1:53]], active=[[@26,89:89='a',<459>,1:89]], id=[[@1,7:7='a',<459>,1:7]]}}, filters=[{name=active, table_ref=a}], interface={id=[{name=id, table_ref=a}], value=[{name=value, table_ref=f}]}, table_alias={a=events, f=flatten0}}}");
	}

	@Test
	public void arC028_flattenModeOBJECTV0Test() {
		final String query = "SELECT a.id, f.value FROM events a, FLATTEN(input => a.payload, mode => 'OBJECT') f WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_flatten_table_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={column={name=value, table_ref=f}}}, from={join={1={table={alias=a, table=events}}, 2={table={alias=f, table_function={function_name=FLATTEN, parameters={mode='OBJECT', input={column={name=payload, table_ref=a}}}}}}}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[id, value]",
				"{}",
				"{flatten0={value=[[@5,13:13='f',<459>,1:13]]}, events={payload=[[@16,53:53='a',<459>,1:53]], active=[[@26,90:90='a',<459>,1:90]], id=[[@1,7:7='a',<459>,1:7]]}}",
				"{query0={value=[[@7,15:19='value',<459>,1:15]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={id=[[@3,9:10='id',<459>,1:9]], value=[[@7,15:19='value',<459>,1:15]]}, table_dictionary={flatten0={value=[[@5,13:13='f',<459>,1:13]]}, events={payload=[[@16,53:53='a',<459>,1:53]], active=[[@26,90:90='a',<459>,1:90]], id=[[@1,7:7='a',<459>,1:7]]}}, filters=[{name=active, table_ref=a}], interface={id=[{name=id, table_ref=a}], value=[{name=value, table_ref=f}]}, table_alias={a=events, f=flatten0}}}");
	}

	@Test
	public void arC028_flattenModeBOTHV0Test() {
		final String query = "SELECT a.id, f.value FROM events a, FLATTEN(input => a.payload, mode => 'BOTH') f WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_flatten_table_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={column={name=value, table_ref=f}}}, from={join={1={table={alias=a, table=events}}, 2={table={alias=f, table_function={function_name=FLATTEN, parameters={mode='BOTH', input={column={name=payload, table_ref=a}}}}}}}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[id, value]",
				"{}",
				"{flatten0={value=[[@5,13:13='f',<459>,1:13]]}, events={payload=[[@16,53:53='a',<459>,1:53]], active=[[@26,88:88='a',<459>,1:88]], id=[[@1,7:7='a',<459>,1:7]]}}",
				"{query0={value=[[@7,15:19='value',<459>,1:15]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={id=[[@3,9:10='id',<459>,1:9]], value=[[@7,15:19='value',<459>,1:15]]}, table_dictionary={flatten0={value=[[@5,13:13='f',<459>,1:13]]}, events={payload=[[@16,53:53='a',<459>,1:53]], active=[[@26,88:88='a',<459>,1:88]], id=[[@1,7:7='a',<459>,1:7]]}}, filters=[{name=active, table_ref=a}], interface={id=[{name=id, table_ref=a}], value=[{name=value, table_ref=f}]}, table_alias={a=events, f=flatten0}}}");
	}

	@Test
	public void arC028_flattenAllOptionsV0Test() {
		final String query = "SELECT a.id, f.value AS item_value FROM events a, FLATTEN(path => 'items', outer => TRUE, recursive => FALSE, mode => 'BOTH', input => a.payload) f WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_flatten_table_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={column={name=value, table_ref=f}, alias=item_value}}, from={join={1={table={alias=a, table=events}}, 2={table={alias=f, table_function={function_name=FLATTEN, parameters={mode='BOTH', path='items', input={column={name=payload, table_ref=a}}, outer=TRUE, recursive=FALSE}}}}}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[id, item_value]",
				"{}",
				"{flatten0={value=[[@5,13:13='f',<459>,1:13]]}, events={payload=[[@34,135:135='a',<459>,1:135]], active=[[@40,154:154='a',<459>,1:154]], id=[[@1,7:7='a',<459>,1:7]]}}",
				"{query0={item_value=[[@9,24:33='item_value',<459>,1:24]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={id=[[@3,9:10='id',<459>,1:9]], item_value=[[@9,24:33='item_value',<459>,1:24]]}, table_dictionary={flatten0={value=[[@5,13:13='f',<459>,1:13]]}, events={payload=[[@34,135:135='a',<459>,1:135]], active=[[@40,154:154='a',<459>,1:154]], id=[[@1,7:7='a',<459>,1:7]]}}, filters=[{name=active, table_ref=a}], interface={id=[{name=id, table_ref=a}], item_value=[{name=value, table_ref=f}]}, table_alias={a=events, f=flatten0}}}");
	}

	@Test
	public void arC028_flattenPathEmptyV0Test() {
		final String query = "SELECT a.id, f.value AS item_value FROM events a, FLATTEN(path => '', input => a.payload) f WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_flatten_table_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={column={name=value, table_ref=f}, alias=item_value}}, from={join={1={table={alias=a, table=events}}, 2={table={alias=f, table_function={function_name=FLATTEN, parameters={path='', input={column={name=payload, table_ref=a}}}}}}}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[id, item_value]",
				"{}",
				"{flatten0={value=[[@5,13:13='f',<459>,1:13]]}, events={payload=[[@22,79:79='a',<459>,1:79]], active=[[@28,98:98='a',<459>,1:98]], id=[[@1,7:7='a',<459>,1:7]]}}",
				"{query0={item_value=[[@9,24:33='item_value',<459>,1:24]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={id=[[@3,9:10='id',<459>,1:9]], item_value=[[@9,24:33='item_value',<459>,1:24]]}, table_dictionary={flatten0={value=[[@5,13:13='f',<459>,1:13]]}, events={payload=[[@22,79:79='a',<459>,1:79]], active=[[@28,98:98='a',<459>,1:98]], id=[[@1,7:7='a',<459>,1:7]]}}, filters=[{name=active, table_ref=a}], interface={id=[{name=id, table_ref=a}], item_value=[{name=value, table_ref=f}]}, table_alias={a=events, f=flatten0}}}");
	}

	@Test
	public void arC028_flattenPathNestedV0Test() {
		final String query = "SELECT a.id, f.value AS item_value FROM events a, FLATTEN(path => 'items[0].children', input => a.payload) f WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_flatten_table_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={column={name=value, table_ref=f}, alias=item_value}}, from={join={1={table={alias=a, table=events}}, 2={table={alias=f, table_function={function_name=FLATTEN, parameters={path='items[0].children', input={column={name=payload, table_ref=a}}}}}}}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[id, item_value]",
				"{}",
				"{flatten0={value=[[@5,13:13='f',<459>,1:13]]}, events={payload=[[@22,96:96='a',<459>,1:96]], active=[[@28,115:115='a',<459>,1:115]], id=[[@1,7:7='a',<459>,1:7]]}}",
				"{query0={item_value=[[@9,24:33='item_value',<459>,1:24]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={id=[[@3,9:10='id',<459>,1:9]], item_value=[[@9,24:33='item_value',<459>,1:24]]}, table_dictionary={flatten0={value=[[@5,13:13='f',<459>,1:13]]}, events={payload=[[@22,96:96='a',<459>,1:96]], active=[[@28,115:115='a',<459>,1:115]], id=[[@1,7:7='a',<459>,1:7]]}}, filters=[{name=active, table_ref=a}], interface={id=[{name=id, table_ref=a}], item_value=[{name=value, table_ref=f}]}, table_alias={a=events, f=flatten0}}}");
	}

	@Test
	public void arC028_flattenOuterFalseV0Test() {
		final String query = "SELECT a.id, f.value AS item_value FROM events a, FLATTEN(outer => FALSE, input => a.payload) f WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_flatten_table_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={column={name=value, table_ref=f}, alias=item_value}}, from={join={1={table={alias=a, table=events}}, 2={table={alias=f, table_function={function_name=FLATTEN, parameters={input={column={name=payload, table_ref=a}}, outer=FALSE}}}}}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[id, item_value]",
				"{}",
				"{flatten0={value=[[@5,13:13='f',<459>,1:13]]}, events={payload=[[@22,83:83='a',<459>,1:83]], active=[[@28,102:102='a',<459>,1:102]], id=[[@1,7:7='a',<459>,1:7]]}}",
				"{query0={item_value=[[@9,24:33='item_value',<459>,1:24]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={id=[[@3,9:10='id',<459>,1:9]], item_value=[[@9,24:33='item_value',<459>,1:24]]}, table_dictionary={flatten0={value=[[@5,13:13='f',<459>,1:13]]}, events={payload=[[@22,83:83='a',<459>,1:83]], active=[[@28,102:102='a',<459>,1:102]], id=[[@1,7:7='a',<459>,1:7]]}}, filters=[{name=active, table_ref=a}], interface={id=[{name=id, table_ref=a}], item_value=[{name=value, table_ref=f}]}, table_alias={a=events, f=flatten0}}}");
	}

	@Test
	public void arC028_flattenRecursiveTrueV0Test() {
		final String query = "SELECT a.id, f.value AS item_value FROM events a, FLATTEN(recursive => TRUE, input => a.payload) f WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_flatten_table_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={column={name=value, table_ref=f}, alias=item_value}}, from={join={1={table={alias=a, table=events}}, 2={table={alias=f, table_function={function_name=FLATTEN, parameters={input={column={name=payload, table_ref=a}}, recursive=TRUE}}}}}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[id, item_value]",
				"{}",
				"{flatten0={value=[[@5,13:13='f',<459>,1:13]]}, events={payload=[[@22,86:86='a',<459>,1:86]], active=[[@28,105:105='a',<459>,1:105]], id=[[@1,7:7='a',<459>,1:7]]}}",
				"{query0={item_value=[[@9,24:33='item_value',<459>,1:24]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={id=[[@3,9:10='id',<459>,1:9]], item_value=[[@9,24:33='item_value',<459>,1:24]]}, table_dictionary={flatten0={value=[[@5,13:13='f',<459>,1:13]]}, events={payload=[[@22,86:86='a',<459>,1:86]], active=[[@28,105:105='a',<459>,1:105]], id=[[@1,7:7='a',<459>,1:7]]}}, filters=[{name=active, table_ref=a}], interface={id=[{name=id, table_ref=a}], item_value=[{name=value, table_ref=f}]}, table_alias={a=events, f=flatten0}}}");
	}

	@Test
	public void arC028_flattenOptionsReorderedV0Test() {
		final String query = "SELECT a.id, f.value AS item_value FROM events a, FLATTEN(mode => 'array', recursive => TRUE, outer => FALSE, path => 'items', input => a.payload) f WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_flatten_table_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={column={name=value, table_ref=f}, alias=item_value}}, from={join={1={table={alias=a, table=events}}, 2={table={alias=f, table_function={function_name=FLATTEN, parameters={mode='array', path='items', input={column={name=payload, table_ref=a}}, outer=FALSE, recursive=TRUE}}}}}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[id, item_value]",
				"{}",
				"{flatten0={value=[[@5,13:13='f',<459>,1:13]]}, events={payload=[[@34,136:136='a',<459>,1:136]], active=[[@40,155:155='a',<459>,1:155]], id=[[@1,7:7='a',<459>,1:7]]}}",
				"{query0={item_value=[[@9,24:33='item_value',<459>,1:24]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={id=[[@3,9:10='id',<459>,1:9]], item_value=[[@9,24:33='item_value',<459>,1:24]]}, table_dictionary={flatten0={value=[[@5,13:13='f',<459>,1:13]]}, events={payload=[[@34,136:136='a',<459>,1:136]], active=[[@40,155:155='a',<459>,1:155]], id=[[@1,7:7='a',<459>,1:7]]}}, filters=[{name=active, table_ref=a}], interface={id=[{name=id, table_ref=a}], item_value=[{name=value, table_ref=f}]}, table_alias={a=events, f=flatten0}}}");
	}

	@Test
	public void arC028_flattenUnqualifiedV0Test() {
		final String query = "SELECT a.id, f.value AS item_value FROM events a, FLATTEN(input => payload) f WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_flatten_table_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={column={name=value, table_ref=f}, alias=item_value}}, from={join={1={table={alias=a, table=events}}, 2={table={alias=f, table_function={function_name=FLATTEN, parameters={input={column={name=payload, table_ref=null}}}}}}}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[id, item_value]",
				"{}",
				"{flatten0={value=[[@5,13:13='f',<459>,1:13]]}, events={payload=[[@18,67:73='payload',<459>,1:67]], active=[[@22,84:84='a',<459>,1:84]], id=[[@1,7:7='a',<459>,1:7]]}}",
				"{query0={item_value=[[@9,24:33='item_value',<459>,1:24]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={id=[[@3,9:10='id',<459>,1:9]], item_value=[[@9,24:33='item_value',<459>,1:24]]}, table_dictionary={flatten0={value=[[@5,13:13='f',<459>,1:13]]}, events={payload=[[@18,67:73='payload',<459>,1:67]], active=[[@22,84:84='a',<459>,1:84]], id=[[@1,7:7='a',<459>,1:7]]}}, filters=[{name=active, table_ref=a}], interface={id=[{name=id, table_ref=a}], item_value=[{name=value, table_ref=f}]}, table_alias={a=events, f=flatten0}}}");
	}

	@Test
	public void arC028_flattenMissingAliasV0Test() {
		final String query = "SELECT a.id, f.value AS item_value FROM events a, FLATTEN(input => z.payload) f WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_flatten_table_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.FATAL,
						"QUALIFIED_COLUMN_NOT_FOUND_IN_TABLE",
						"Source Table not found for Column 'payload' at (l:1 c:67). No alias or table called 'z'.",
						1, 67, "SqlASTWalkerHelper", null, "payload", false, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={column={name=value, table_ref=f}, alias=item_value}}, from={join={1={table={alias=a, table=events}}, 2={table={alias=f, table_function={function_name=FLATTEN, parameters={input={column={name=payload, table_ref=z}}}}}}}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[id, item_value]",
				"{}",
				"{flatten0={value=[[@5,13:13='f',<459>,1:13]]}, events={active=[[@24,86:86='a',<459>,1:86]], id=[[@1,7:7='a',<459>,1:7]]}}",
				"{query0={item_value=[[@9,24:33='item_value',<459>,1:24]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={id=[[@3,9:10='id',<459>,1:9]], item_value=[[@9,24:33='item_value',<459>,1:24]]}, table_dictionary={flatten0={value=[[@5,13:13='f',<459>,1:13]]}, events={active=[[@24,86:86='a',<459>,1:86]], id=[[@1,7:7='a',<459>,1:7]]}}, filters=[{name=active, table_ref=a}], interface={id=[{name=id, table_ref=a}], item_value=[{name=value, table_ref=f}]}, table_alias={a=events, f=flatten0}}}");
	}

	@Test
	public void arC028_flattenExpressionV0Test() {
		final String query = "SELECT a.id, f.value AS item_value FROM events a, FLATTEN(input => coalesce(a.payload, a.fallback_payload)) f WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_flatten_table_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={column={name=value, table_ref=f}, alias=item_value}}, from={join={1={table={alias=a, table=events}}, 2={table={alias=f, table_function={function_name=FLATTEN, parameters={input={function={parameters={1={column={name=payload, table_ref=a}}, 2={column={name=fallback_payload, table_ref=a}}}, function_name=coalesce}}}}}}}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[id, item_value]",
				"{}",
				"{flatten0={value=[[@5,13:13='f',<459>,1:13]]}, events={payload=[[@20,76:76='a',<459>,1:76]], active=[[@31,116:116='a',<459>,1:116]], id=[[@1,7:7='a',<459>,1:7]], fallback_payload=[[@24,87:87='a',<459>,1:87]]}}",
				"{query0={item_value=[[@9,24:33='item_value',<459>,1:24]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={id=[[@3,9:10='id',<459>,1:9]], item_value=[[@9,24:33='item_value',<459>,1:24]]}, table_dictionary={flatten0={value=[[@5,13:13='f',<459>,1:13]]}, events={payload=[[@20,76:76='a',<459>,1:76]], active=[[@31,116:116='a',<459>,1:116]], id=[[@1,7:7='a',<459>,1:7]], fallback_payload=[[@24,87:87='a',<459>,1:87]]}}, filters=[{name=active, table_ref=a}], interface={id=[{name=id, table_ref=a}], item_value=[{name=value, table_ref=f}]}, table_alias={a=events, f=flatten0}}}");
	}

	@Test
	public void arC028_flattenPredicandVariableV0Test() {
		final String query = "SELECT a.id, f.value AS item_value FROM events a, FLATTEN(input => <payload_expr>) f WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_flatten_table_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={column={name=value, table_ref=f}, alias=item_value}}, from={join={1={table={alias=a, table=events}}, 2={table={alias=f, table_function={function_name=FLATTEN, parameters={input={substitution={name=<payload_expr>, type=predicand}}}}}}}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[id, item_value]",
				"{<payload_expr>=predicand}",
				"{flatten0={value=[[@5,13:13='f',<459>,1:13]]}, events={active=[[@22,91:91='a',<459>,1:91]], id=[[@1,7:7='a',<459>,1:7]]}}",
				"{query0={item_value=[[@9,24:33='item_value',<459>,1:24]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={id=[[@3,9:10='id',<459>,1:9]], item_value=[[@9,24:33='item_value',<459>,1:24]]}, table_dictionary={flatten0={value=[[@5,13:13='f',<459>,1:13]]}, events={active=[[@22,91:91='a',<459>,1:91]], id=[[@1,7:7='a',<459>,1:7]]}}, filters=[{name=active, table_ref=a}], interface={id=[{name=id, table_ref=a}], item_value=[{name=value, table_ref=f}]}, table_alias={a=events, f=flatten0}}}");
	}

	@Test
	public void arC028_flattenColumnVariableV0Test() {
		final String query = "SELECT a.id, f.value AS item_value FROM events a, FLATTEN(input => a.<payload_col>) f WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_flatten_table_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={column={name=value, table_ref=f}, alias=item_value}}, from={join={1={table={alias=a, table=events}}, 2={table={alias=f, table_function={function_name=FLATTEN, parameters={input={column={substitution={name=<payload_col>, type=column}, table_ref=a}}}}}}}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[id, item_value]",
				"{<payload_col>=column}",
				"{flatten0={value=[[@5,13:13='f',<459>,1:13]]}, events={active=[[@24,92:92='a',<459>,1:92]], id=[[@1,7:7='a',<459>,1:7]], <payload_col>=[[@18,67:67='a',<459>,1:67]]}}",
				"{query0={item_value=[[@9,24:33='item_value',<459>,1:24]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={id=[[@3,9:10='id',<459>,1:9]], item_value=[[@9,24:33='item_value',<459>,1:24]]}, table_dictionary={flatten0={value=[[@5,13:13='f',<459>,1:13]]}, events={active=[[@24,92:92='a',<459>,1:92]], id=[[@1,7:7='a',<459>,1:7]], <payload_col>=[[@18,67:67='a',<459>,1:67]]}}, filters=[{name=active, table_ref=a}], interface={id=[{name=id, table_ref=a}], item_value=[{name=value, table_ref=f}]}, table_alias={a=events, f=flatten0}}}");
	}

	@Test
	public void arC028_flattenNullV0Test() {
		final String query = "SELECT a.id, f.value AS item_value FROM events a, FLATTEN(input => NULL) f WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_flatten_table_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={column={name=value, table_ref=f}, alias=item_value}}, from={join={1={table={alias=a, table=events}}, 2={table={alias=f, table_function={function_name=FLATTEN, parameters={input={null_literal=null}}}}}}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[id, item_value]",
				"{}",
				"{flatten0={value=[[@5,13:13='f',<459>,1:13]]}, events={active=[[@22,81:81='a',<459>,1:81]], id=[[@1,7:7='a',<459>,1:7]]}}",
				"{query0={item_value=[[@9,24:33='item_value',<459>,1:24]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={id=[[@3,9:10='id',<459>,1:9]], item_value=[[@9,24:33='item_value',<459>,1:24]]}, table_dictionary={flatten0={value=[[@5,13:13='f',<459>,1:13]]}, events={active=[[@22,81:81='a',<459>,1:81]], id=[[@1,7:7='a',<459>,1:7]]}}, filters=[{name=active, table_ref=a}], interface={id=[{name=id, table_ref=a}], item_value=[{name=value, table_ref=f}]}, table_alias={a=events, f=flatten0}}}");
	}

	@Test
	public void arC028_flattenNestedArraysV0Test() {
		final String query = "SELECT a.id, f.value AS item_value FROM events a, FLATTEN(input => [[1,2], [3, NULL]]) f WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_flatten_table_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"array literal at (l:1 c:68) is recognized as Snowflake-specific grammar.",
						1, 68, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={column={name=value, table_ref=f}, alias=item_value}}, from={join={1={table={alias=a, table=events}}, 2={table={alias=f, table_function={function_name=FLATTEN, parameters={input={function={function_name=array_construct, parameters={1={function={function_name=array_construct, parameters={1={literal=1}, 2={literal=2}}}}, 2={function={function_name=array_construct, parameters={1={literal=3}, 2={null_literal=null}}}}}}}}}}}}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[id, item_value]",
				"{}",
				"{flatten0={value=[[@5,13:13='f',<459>,1:13]]}, events={active=[[@34,95:95='a',<459>,1:95]], id=[[@1,7:7='a',<459>,1:7]]}}",
				"{query0={item_value=[[@9,24:33='item_value',<459>,1:24]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={id=[[@3,9:10='id',<459>,1:9]], item_value=[[@9,24:33='item_value',<459>,1:24]]}, table_dictionary={flatten0={value=[[@5,13:13='f',<459>,1:13]]}, events={active=[[@34,95:95='a',<459>,1:95]], id=[[@1,7:7='a',<459>,1:7]]}}, filters=[{name=active, table_ref=a}], interface={id=[{name=id, table_ref=a}], item_value=[{name=value, table_ref=f}]}, table_alias={a=events, f=flatten0}}}");
	}

	@Test
	public void arC028_flattenUnresolvedInputV0Test() {
		final String query = "SELECT f.value FROM FLATTEN(input => missing_payload) f";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_flatten_table_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.ERROR,
						"UNRESOLVED_UNQUALIFIED_COLUMNS",
						"Unresolved unqualified column reference(s): [missing_payload [(l:1 c:37)]]",
						1, 37, "SqlASTWalkerHelper", null, "missing_payload", false, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=value, table_ref=f}}}, from={table={alias=f, table_function={function_name=FLATTEN, parameters={input={column={name=missing_payload, table_ref=null}}}}}}}}",
				"[value]",
				"{}",
				"{flatten0={value=[[@1,7:7='f',<459>,1:7]]}}",
				"{query0={value=[[@3,9:13='value',<459>,1:9]]}}",
				"{def_query0={query_dictionary={value=[[@3,9:13='value',<459>,1:9]]}, table_dictionary={flatten0={value=[[@1,7:7='f',<459>,1:7]]}}, interface={value=[{name=value, table_ref=f}]}, table_alias={f=flatten0}}}");
	}

	@Test
	public void arC028_flattenAmbiguousInputV0Test() {
		final String query = "SELECT a.id, f.value FROM events a JOIN archive_events b ON a.id = b.id, FLATTEN(input => payload) f";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_flatten_table_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.SEVERE_WARNING,
						"AMBIGUOUS_COLUMN_REFERENCE",
						"Ambiguous column reference 'payload' at (l:1 c:90). Possible sources: [archive_events, events]",
						1, 90, "SqlASTWalkerHelper", null, "payload", true, "ast-walk", null, null),
				new ParseDiagnostic(ParseDiagnostic.Severity.ERROR,
						"UNRESOLVED_UNQUALIFIED_COLUMNS",
						"Unresolved unqualified column reference(s): [payload [(l:1 c:90)]]",
						1, 90, "SqlASTWalkerHelper", null, "payload", false, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={column={name=value, table_ref=f}}}, from={join={1={table={alias=a, table=events}}, 2={join=JOIN, on={condition={left={column={name=id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=archive_events}}, 4={table={alias=f, table_function={function_name=FLATTEN, parameters={input={column={name=payload, table_ref=null}}}}}}}}}}",
				"[id, value]",
				"{}",
				"{archive_events={id=[[@19,67:67='b',<459>,1:67]]}, flatten0={value=[[@5,13:13='f',<459>,1:13]]}, events={id=[[@1,7:7='a',<459>,1:7], [@15,60:60='a',<459>,1:60]]}}",
				"{query0={value=[[@7,15:19='value',<459>,1:15]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={id=[[@3,9:10='id',<459>,1:9]], value=[[@7,15:19='value',<459>,1:15]]}, table_dictionary={archive_events={id=[[@19,67:67='b',<459>,1:67]]}, flatten0={value=[[@5,13:13='f',<459>,1:13]]}, events={id=[[@1,7:7='a',<459>,1:7], [@15,60:60='a',<459>,1:60]]}}, filters=[{name=id, table_ref=a}, {name=id, table_ref=b}], interface={id=[{name=id, table_ref=a}], value=[{name=value, table_ref=f}]}, table_alias={a=events, b=archive_events, f=flatten0}}}");
	}

	@Test
	public void arC028_flattenHiddenInputAliasV0Test() {
		final String query = "SELECT q.id, f.value FROM (SELECT a.id, a.payload FROM events a) q, FLATTEN(input => a.payload) f";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_flatten_table_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.FATAL,
						"QUALIFIED_COLUMN_NOT_FOUND_IN_TABLE",
						"Source Table not found for Column 'payload' at (l:1 c:85). No alias or table called 'a'.",
						1, 85, "SqlASTWalkerHelper", null, "payload", false, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=q}}, 2={column={name=value, table_ref=f}}}, from={join={1={table={alias=q, query={select={1={column={name=id, table_ref=a}}, 2={column={name=payload, table_ref=a}}}, from={table={alias=a, table=events}}}}}, 2={table={alias=f, table_function={function_name=FLATTEN, parameters={input={column={name=payload, table_ref=a}}}}}}}}}}",
				"[id, value]",
				"{}",
				"{flatten0={value=[[@5,13:13='f',<459>,1:13]]}, events={payload=[[@15,40:40='a',<459>,1:40]], id=[[@11,34:34='a',<459>,1:34]]}}",
				"{query0={payload=[[@17,42:48='payload',<459>,1:42]], id=[[@13,36:37='id',<459>,1:36], [@1,7:7='q',<459>,1:7]]}, query1={value=[[@7,15:19='value',<459>,1:15]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query1={query_dictionary={id=[[@3,9:10='id',<459>,1:9]], value=[[@7,15:19='value',<459>,1:15]]}, table_dictionary={flatten0={value=[[@5,13:13='f',<459>,1:13]]}}, def_query0={query_dictionary={payload=[[@17,42:48='payload',<459>,1:42]], id=[[@13,36:37='id',<459>,1:36], [@1,7:7='q',<459>,1:7]]}, table_dictionary={events={payload=[[@15,40:40='a',<459>,1:40]], id=[[@11,34:34='a',<459>,1:34]]}}, interface={payload=[{name=payload, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}, interface={id=[{name=id, table_ref=q}], value=[{name=value, table_ref=f}]}, table_alias={q=query0, f=flatten0}}}");
	}

	@Test
	public void arC028_flattenOwnOutputInvisibleV0Test() {
		final String query = "SELECT f.value FROM FLATTEN(input => f.value) f";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_flatten_table_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.FATAL,
						"QUALIFIED_COLUMN_NOT_FOUND_IN_TABLE",
						"Source Table not found for Column 'value' at (l:1 c:7). No alias or table called 'f'.",
						1, 7, "SqlASTWalkerHelper", null, "value", false, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=value, table_ref=f}}}, from={table={alias=f, table_function={function_name=FLATTEN, parameters={input={column={name=value, table_ref=f}}}}}}}}",
				"[value]",
				"{}",
				"{flatten0={value=[[@1,7:7='f',<459>,1:7]]}}",
				"{query0={value=[[@3,9:13='value',<459>,1:9]]}}",
				"{def_query0={query_dictionary={value=[[@3,9:13='value',<459>,1:9]]}, table_dictionary={flatten0={value=[[@1,7:7='f',<459>,1:7]]}}, interface={value=[{name=value, table_ref=f}]}, table_alias={f=flatten0}}}");
	}

	@Test
	public void arC028_flattenLaterSourceInvisibleV0Test() {
		final String query = "SELECT f.value FROM FLATTEN(input => a.payload) f, events a";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_flatten_table_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.FATAL,
						"QUALIFIED_COLUMN_NOT_FOUND_IN_TABLE",
						"Source Table not found for Column 'payload' at (l:1 c:37). No alias or table called 'a'.",
						1, 37, "SqlASTWalkerHelper", null, "payload", false, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=value, table_ref=f}}}, from={join={1={table={alias=f, table_function={function_name=FLATTEN, parameters={input={column={name=payload, table_ref=a}}}}}}, 2={table={alias=a, table=events}}}}}}",
				"[value]",
				"{}",
				"{flatten0={value=[[@1,7:7='f',<459>,1:7]]}, events={}}",
				"{query0={value=[[@3,9:13='value',<459>,1:9]]}}",
				"{def_query0={query_dictionary={value=[[@3,9:13='value',<459>,1:9]]}, table_dictionary={flatten0={value=[[@1,7:7='f',<459>,1:7]]}, events={}}, interface={value=[{name=value, table_ref=f}]}, table_alias={a=events, f=flatten0}}}");
	}

	@Test
	public void arC028_flattenSameInputAndProjectionV0Test() {
		final String query = "SELECT payload AS exported_payload, f.value FROM events a, FLATTEN(input => payload) f";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_flatten_table_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.SEVERE_WARNING,
						"AMBIGUOUS_COLUMN_REFERENCE",
						"Ambiguous column reference 'payload' at (l:1 c:7). Possible sources: [events, flatten0]",
						1, 7, "SqlASTWalkerHelper", null, "payload", true, "ast-walk", null, null),
				new ParseDiagnostic(ParseDiagnostic.Severity.ERROR,
						"UNRESOLVED_UNQUALIFIED_COLUMNS",
						"Unresolved unqualified column reference(s): [payload [(l:1 c:7)]]",
						1, 7, "SqlASTWalkerHelper", null, "payload", false, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=payload, table_ref=null}, alias=exported_payload}, 2={column={name=value, table_ref=f}}}, from={join={1={table={alias=a, table=events}}, 2={table={alias=f, table_function={function_name=FLATTEN, parameters={input={column={name=payload, table_ref=null}}}}}}}}}}",
				"[exported_payload, value]",
				"{}",
				"{flatten0={value=[[@5,36:36='f',<459>,1:36]]}, events={payload=[[@16,76:82='payload',<459>,1:76]]}}",
				"{query0={exported_payload=[[@3,18:33='exported_payload',<459>,1:18]], value=[[@7,38:42='value',<459>,1:38]]}}",
				"{def_query0={query_dictionary={exported_payload=[[@3,18:33='exported_payload',<459>,1:18]], value=[[@7,38:42='value',<459>,1:38]]}, table_dictionary={flatten0={value=[[@5,36:36='f',<459>,1:36]]}, events={payload=[[@16,76:82='payload',<459>,1:76]]}}, interface={exported_payload=[{name=payload, table_ref=null}], value=[{name=value, table_ref=f}]}, table_alias={a=events, f=flatten0}}}");
	}

	@Test
	public void arC028_flattenCteMissingInputV0Test() {
		final String query = "WITH seed AS (SELECT a.id FROM events a) SELECT s.id, f.value FROM seed s, FLATTEN(input => s.payload) f";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_flatten_table_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.FATAL,
						"QUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIAS",
						"Qualified column 'payload' at (l:1 c:92) was not found in output interface of query alias 's'.",
						1, 92, "SqlASTWalkerHelper", null, "s.payload", false, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={with={1={cte={select={1={column={name=id, table_ref=a}}}, from={table={alias=a, table=events}}}, alias=seed}}, query={select={1={column={name=id, table_ref=s}}, 2={column={name=value, table_ref=f}}}, from={join={1={table={alias=s, table=seed}}, 2={table={alias=f, table_function={function_name=FLATTEN, parameters={input={column={name=payload, table_ref=s}}}}}}}}}}}",
				"[id, value]",
				"{}",
				"{flatten0={value=[[@17,54:54='f',<459>,1:54]]}, events={id=[[@5,21:21='a',<459>,1:21]]}}",
				"{query0={id=[[@7,23:24='id',<459>,1:23], [@13,48:48='s',<459>,1:48]]}, query1={value=[[@19,56:60='value',<459>,1:56]], id=[[@15,50:51='id',<459>,1:50]]}}",
				"{def_query1={context_list={seed=query0, s=query0}, query_dictionary={id=[[@15,50:51='id',<459>,1:50]], value=[[@19,56:60='value',<459>,1:56]]}, table_dictionary={flatten0={value=[[@17,54:54='f',<459>,1:54]]}}, def_query0={query_dictionary={id=[[@7,23:24='id',<459>,1:23], [@13,48:48='s',<459>,1:48]]}, table_dictionary={events={id=[[@5,21:21='a',<459>,1:21]]}}, interface={id=[{name=id, table_ref=a}]}, table_alias={a=events}}, interface={id=[{name=id, table_ref=s}], value=[{name=value, table_ref=f}]}, table_alias={s=query0, seed=query0, f=flatten0}}}");
	}

	@Test
	public void arC028_flattenOutputColumnsV0Test() {
		final String query = "SELECT f.seq, f.key, f.path, f.index, f.value, f.this FROM events a, FLATTEN(input => a.payload) f WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_flatten_table_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=seq, table_ref=f}}, 2={column={name=key, table_ref=f}}, 3={column={name=path, table_ref=f}}, 4={column={name=index, table_ref=f}}, 5={column={name=value, table_ref=f}}, 6={column={name=this, table_ref=f}}}, from={join={1={table={alias=a, table=events}}, 2={table={alias=f, table_function={function_name=FLATTEN, parameters={input={column={name=payload, table_ref=a}}}}}}}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[path, this, index, value, seq, key]",
				"{}",
				"{flatten0={path=[[@9,21:21='f',<459>,1:21]], this=[[@21,47:47='f',<459>,1:47]], index=[[@13,29:29='f',<459>,1:29]], value=[[@17,38:38='f',<459>,1:38]], seq=[[@1,7:7='f',<459>,1:7]], key=[[@5,14:14='f',<459>,1:14]]}, events={payload=[[@32,86:86='a',<459>,1:86]], active=[[@38,105:105='a',<459>,1:105]]}}",
				"{query0={this=[[@23,49:52='this',<459>,1:49]], path=[[@11,23:26='path',<382>,1:23]], index=[[@15,31:35='index',<96>,1:31]], value=[[@19,40:44='value',<459>,1:40]], seq=[[@3,9:11='seq',<459>,1:9]], key=[[@7,16:18='key',<459>,1:16]]}}",
				"{def_query0={query_dictionary={path=[[@11,23:26='path',<382>,1:23]], this=[[@23,49:52='this',<459>,1:49]], index=[[@15,31:35='index',<96>,1:31]], value=[[@19,40:44='value',<459>,1:40]], seq=[[@3,9:11='seq',<459>,1:9]], key=[[@7,16:18='key',<459>,1:16]]}, table_dictionary={flatten0={path=[[@9,21:21='f',<459>,1:21]], this=[[@21,47:47='f',<459>,1:47]], index=[[@13,29:29='f',<459>,1:29]], value=[[@17,38:38='f',<459>,1:38]], seq=[[@1,7:7='f',<459>,1:7]], key=[[@5,14:14='f',<459>,1:14]]}, events={payload=[[@32,86:86='a',<459>,1:86]], active=[[@38,105:105='a',<459>,1:105]]}}, filters=[{name=active, table_ref=a}], interface={path=[{name=path, table_ref=f}], this=[{name=this, table_ref=f}], index=[{name=index, table_ref=f}], value=[{name=value, table_ref=f}], seq=[{name=seq, table_ref=f}], key=[{name=key, table_ref=f}]}, table_alias={a=events, f=flatten0}}}");
	}

	@Test
	public void arC028_flattenOutputRenameV0Test() {
		final String query = "SELECT a.id AS event_id, f.value AS item_value, f.index AS item_position FROM events a, FLATTEN(input => a.payload) f WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_flatten_table_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}, alias=event_id}, 2={column={name=value, table_ref=f}, alias=item_value}, 3={column={name=index, table_ref=f}, alias=item_position}}, from={join={1={table={alias=a, table=events}}, 2={table={alias=f, table_function={function_name=FLATTEN, parameters={input={column={name=payload, table_ref=a}}}}}}}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[item_position, event_id, item_value]",
				"{}",
				"{flatten0={index=[[@13,48:48='f',<459>,1:48]], value=[[@7,25:25='f',<459>,1:25]]}, events={payload=[[@26,105:105='a',<459>,1:105]], active=[[@32,124:124='a',<459>,1:124]], id=[[@1,7:7='a',<459>,1:7]]}}",
				"{query0={item_position=[[@17,59:71='item_position',<459>,1:59]], item_value=[[@11,36:45='item_value',<459>,1:36]], event_id=[[@5,15:22='event_id',<459>,1:15]]}}",
				"{def_query0={query_dictionary={item_position=[[@17,59:71='item_position',<459>,1:59]], event_id=[[@5,15:22='event_id',<459>,1:15]], item_value=[[@11,36:45='item_value',<459>,1:36]]}, table_dictionary={flatten0={index=[[@13,48:48='f',<459>,1:48]], value=[[@7,25:25='f',<459>,1:25]]}, events={payload=[[@26,105:105='a',<459>,1:105]], active=[[@32,124:124='a',<459>,1:124]], id=[[@1,7:7='a',<459>,1:7]]}}, filters=[{name=active, table_ref=a}], interface={item_position=[{name=index, table_ref=f}], event_id=[{name=id, table_ref=a}], item_value=[{name=value, table_ref=f}]}, table_alias={a=events, f=flatten0}}}");
	}

	@Test
	public void arC028_flattenOutputStarV0Test() {
		final String query = "SELECT a.id, f.* FROM events a, FLATTEN(input => a.payload) f WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_flatten_table_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={column={name=*, table_ref=f}}}, from={join={1={table={alias=a, table=events}}, 2={table={alias=f, table_function={function_name=FLATTEN, parameters={input={column={name=payload, table_ref=a}}}}}}}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[*, id]",
				"{}",
				"{flatten0={*=[[@5,13:13='f',<459>,1:13]]}, events={payload=[[@16,49:49='a',<459>,1:49]], active=[[@22,68:68='a',<459>,1:68]], id=[[@1,7:7='a',<459>,1:7]]}}",
				"{query0={*=[[@7,15:15='*',<291>,1:15]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={*=[[@7,15:15='*',<291>,1:15]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={flatten0={*=[[@5,13:13='f',<459>,1:13]]}, events={payload=[[@16,49:49='a',<459>,1:49]], active=[[@22,68:68='a',<459>,1:68]], id=[[@1,7:7='a',<459>,1:7]]}}, filters=[{name=active, table_ref=a}], interface={*=[{name=*, table_ref=f}], id=[{name=id, table_ref=a}]}, table_alias={a=events, f=flatten0}}}");
	}

	@Test
	public void arC028_flattenChainedV0Test() {
		final String query = "SELECT a.id, f.value AS parent_value, g.value AS child_value FROM events a, FLATTEN(input => a.payload) f, FLATTEN(input => f.value, path => 'children') g WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 2,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_flatten_table_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={column={name=value, table_ref=f}, alias=parent_value}, 3={column={name=value, table_ref=g}, alias=child_value}}, from={join={1={table={alias=a, table=events}}, 2={table={alias=f, table_function={function_name=FLATTEN, parameters={input={column={name=payload, table_ref=a}}}}}}, 3={table={alias=g, table_function={function_name=FLATTEN, parameters={input={column={name=value, table_ref=f}}, path='children'}}}}}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[child_value, id, parent_value]",
				"{}",
				"{flatten1={value=[[@11,38:38='g',<459>,1:38]]}, flatten0={value=[[@34,124:124='f',<459>,1:124], [@5,13:13='f',<459>,1:13]]}, events={payload=[[@24,93:93='a',<459>,1:93]], active=[[@44,161:161='a',<459>,1:161]], id=[[@1,7:7='a',<459>,1:7]]}}",
				"{query0={child_value=[[@15,49:59='child_value',<459>,1:49]], parent_value=[[@9,24:35='parent_value',<459>,1:24]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={child_value=[[@15,49:59='child_value',<459>,1:49]], id=[[@3,9:10='id',<459>,1:9]], parent_value=[[@9,24:35='parent_value',<459>,1:24]]}, table_dictionary={flatten1={value=[[@11,38:38='g',<459>,1:38]]}, flatten0={value=[[@34,124:124='f',<459>,1:124], [@5,13:13='f',<459>,1:13]]}, events={payload=[[@24,93:93='a',<459>,1:93]], active=[[@44,161:161='a',<459>,1:161]], id=[[@1,7:7='a',<459>,1:7]]}}, filters=[{name=active, table_ref=a}], interface={child_value=[{name=value, table_ref=g}], id=[{name=id, table_ref=a}], parent_value=[{name=value, table_ref=f}]}, table_alias={a=events, f=flatten0, g=flatten1}}}");
	}

	@Test
	public void arC028_flattenCorrelatedExistsV0Test() {
		final String query = "SELECT a.id FROM events a WHERE EXISTS (SELECT f.value FROM TABLE(FLATTEN(input => a.payload)) f WHERE f.value IS NOT NULL) AND a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_flatten_table_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}}, from={table={alias=a, table=events}}, where={and={1={exists={select={1={column={name=value, table_ref=f}}}, from={table={alias=f, table_function={function_name=FLATTEN, parameters={input={column={name=payload, table_ref=a}}}}}}, where={condition={left={column={name=value, table_ref=f}}, operator=IS NOT NULL}}, operator=EXISTS}}, 2={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}}}",
				"[id]",
				"{}",
				"{flatten0={value=[[@11,47:47='f',<459>,1:47], [@28,103:103='f',<459>,1:103]]}, events={payload=[[@21,83:83='a',<459>,1:83]], active=[[@36,128:128='a',<459>,1:128]], id=[[@1,7:7='a',<459>,1:7]]}}",
				"{query0={value=[[@13,49:53='value',<459>,1:49]]}, query2={id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query2={query_dictionary={id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={active=[[@36,128:128='a',<459>,1:128]], id=[[@1,7:7='a',<459>,1:7]]}}, dependent_queries={exists1={query=query0, type=filters}}, def_query0={query_dictionary={value=[[@13,49:53='value',<459>,1:49]]}, table_dictionary={flatten0={value=[[@11,47:47='f',<459>,1:47], [@28,103:103='f',<459>,1:103]]}, events={payload=[[@21,83:83='a',<459>,1:83]]}}, filters=[{name=value, table_ref=f}], interface={value=[{name=value, table_ref=f}]}, table_alias={f=flatten0}}, filters=[{name=active, table_ref=a}], interface={id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC028_flattenDerivedConsumerV0Test() {
		final String query = "SELECT q.id, q.item_value FROM (SELECT a.id, f.value AS item_value FROM events a, FLATTEN(input => a.payload) f WHERE a.active = 1) q WHERE q.item_value IS NOT NULL ORDER BY q.id";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_flatten_table_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=q}}, 2={column={name=item_value, table_ref=q}}}, orderby={1={null_order=null, predicand={column={name=id, table_ref=q}}, sort_order=ASC}}, from={table={alias=q, query={select={1={column={name=id, table_ref=a}}, 2={column={name=value, table_ref=f}, alias=item_value}}, from={join={1={table={alias=a, table=events}}, 2={table={alias=f, table_function={function_name=FLATTEN, parameters={input={column={name=payload, table_ref=a}}}}}}}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}}, where={condition={left={column={name=item_value, table_ref=q}}, operator=IS NOT NULL}}}}",
				"[id, item_value]",
				"{}",
				"{flatten0={value=[[@15,45:45='f',<459>,1:45]]}, events={payload=[[@28,99:99='a',<459>,1:99]], active=[[@34,118:118='a',<459>,1:118]], id=[[@11,39:39='a',<459>,1:39]]}}",
				"{query0={item_value=[[@19,56:65='item_value',<459>,1:56], [@5,13:13='q',<459>,1:13], [@42,140:140='q',<459>,1:140]], id=[[@13,41:42='id',<459>,1:41], [@1,7:7='q',<459>,1:7], [@50,174:174='q',<459>,1:174]]}, query1={item_value=[[@7,15:24='item_value',<459>,1:15]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query1={query_dictionary={id=[[@3,9:10='id',<459>,1:9]], item_value=[[@7,15:24='item_value',<459>,1:15]]}, def_query0={query_dictionary={id=[[@13,41:42='id',<459>,1:41], [@1,7:7='q',<459>,1:7], [@50,174:174='q',<459>,1:174]], item_value=[[@19,56:65='item_value',<459>,1:56], [@5,13:13='q',<459>,1:13], [@42,140:140='q',<459>,1:140]]}, table_dictionary={flatten0={value=[[@15,45:45='f',<459>,1:45]]}, events={payload=[[@28,99:99='a',<459>,1:99]], active=[[@34,118:118='a',<459>,1:118]], id=[[@11,39:39='a',<459>,1:39]]}}, filters=[{name=active, table_ref=a}], interface={id=[{name=id, table_ref=a}], item_value=[{name=value, table_ref=f}]}, table_alias={a=events, f=flatten0}}, ordered_by=[{name=id, table_ref=q}], filters=[{name=item_value, table_ref=q}], interface={id=[{name=id, table_ref=q}], item_value=[{name=item_value, table_ref=q}]}, table_alias={q=query0}}}");
	}

	@Test
	public void arC028_flattenCteConsumerV0Test() {
		final String query = "WITH exploded AS (SELECT a.id, f.value AS item_value FROM events a, FLATTEN(input => a.payload) f WHERE a.active = 1) SELECT e.id, e.item_value FROM exploded e WHERE e.item_value IS NOT NULL";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_flatten_table_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={with={1={cte={select={1={column={name=id, table_ref=a}}, 2={column={name=value, table_ref=f}, alias=item_value}}, from={join={1={table={alias=a, table=events}}, 2={table={alias=f, table_function={function_name=FLATTEN, parameters={input={column={name=payload, table_ref=a}}}}}}}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}, alias=exploded}}, query={select={1={column={name=id, table_ref=e}}, 2={column={name=item_value, table_ref=e}}}, from={table={alias=e, table=exploded}}, where={condition={left={column={name=item_value, table_ref=e}}, operator=IS NOT NULL}}}}}",
				"[id, item_value]",
				"{}",
				"{flatten0={value=[[@9,31:31='f',<459>,1:31]]}, events={payload=[[@22,85:85='a',<459>,1:85]], active=[[@28,104:104='a',<459>,1:104]], id=[[@5,25:25='a',<459>,1:25]]}}",
				"{query0={item_value=[[@13,42:51='item_value',<459>,1:42], [@39,131:131='e',<459>,1:131], [@46,166:166='e',<459>,1:166]], id=[[@7,27:28='id',<459>,1:27], [@35,125:125='e',<459>,1:125]]}, query1={item_value=[[@41,133:142='item_value',<459>,1:133]], id=[[@37,127:128='id',<459>,1:127]]}}",
				"{def_query1={context_list={exploded=query0, e=query0}, query_dictionary={id=[[@37,127:128='id',<459>,1:127]], item_value=[[@41,133:142='item_value',<459>,1:133]]}, def_query0={query_dictionary={id=[[@7,27:28='id',<459>,1:27], [@35,125:125='e',<459>,1:125]], item_value=[[@13,42:51='item_value',<459>,1:42], [@39,131:131='e',<459>,1:131], [@46,166:166='e',<459>,1:166]]}, table_dictionary={flatten0={value=[[@9,31:31='f',<459>,1:31]]}, events={payload=[[@22,85:85='a',<459>,1:85]], active=[[@28,104:104='a',<459>,1:104]], id=[[@5,25:25='a',<459>,1:25]]}}, filters=[{name=active, table_ref=a}], interface={id=[{name=id, table_ref=a}], item_value=[{name=value, table_ref=f}]}, table_alias={a=events, f=flatten0}}, filters=[{name=item_value, table_ref=e}], interface={id=[{name=id, table_ref=e}], item_value=[{name=item_value, table_ref=e}]}, table_alias={exploded=query0, e=query0}}}");
	}

	@Test
	public void arC028_flattenCteRenameChainV0Test() {
		final String query = "WITH exploded AS (SELECT a.id, f.value AS item_value FROM events a, FLATTEN(input => a.payload) f WHERE a.active = 1), renamed AS (SELECT id, item_value AS exported_value FROM exploded) SELECT r.id, r.exported_value FROM renamed r WHERE r.exported_value IS NOT NULL";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_flatten_table_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={with={1={cte={select={1={column={name=id, table_ref=a}}, 2={column={name=value, table_ref=f}, alias=item_value}}, from={join={1={table={alias=a, table=events}}, 2={table={alias=f, table_function={function_name=FLATTEN, parameters={input={column={name=payload, table_ref=a}}}}}}}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}, alias=exploded}, 2={cte={select={1={column={name=id, table_ref=null}}, 2={column={name=item_value, table_ref=null}, alias=exported_value}}, from={table={alias=null, table=exploded}}}, alias=renamed}}, query={select={1={column={name=id, table_ref=r}}, 2={column={name=exported_value, table_ref=r}}}, from={table={alias=r, table=renamed}}, where={condition={left={column={name=exported_value, table_ref=r}}, operator=IS NOT NULL}}}}}",
				"[exported_value, id]",
				"{}",
				"{flatten0={value=[[@9,31:31='f',<459>,1:31]]}, events={payload=[[@22,85:85='a',<459>,1:85]], active=[[@28,104:104='a',<459>,1:104]], id=[[@5,25:25='a',<459>,1:25]]}}",
				"{query0={item_value=[[@13,42:51='item_value',<459>,1:42], [@41,142:151='item_value',<459>,1:142]], id=[[@7,27:28='id',<459>,1:27], [@39,138:139='id',<459>,1:138]]}, query1={exported_value=[[@43,156:169='exported_value',<459>,1:156], [@52,199:199='r',<459>,1:199], [@59,237:237='r',<459>,1:237]], id=[[@39,138:139='id',<459>,1:138], [@48,193:193='r',<459>,1:193]]}, query2={exported_value=[[@54,201:214='exported_value',<459>,1:201]], id=[[@50,195:196='id',<459>,1:195]]}}",
				"{def_query2={context_list={exploded=query0, renamed=query1, r=query1}, query_dictionary={exported_value=[[@54,201:214='exported_value',<459>,1:201]], id=[[@50,195:196='id',<459>,1:195]]}, def_query1={context_list={exploded=query0}, query_dictionary={exported_value=[[@43,156:169='exported_value',<459>,1:156], [@52,199:199='r',<459>,1:199], [@59,237:237='r',<459>,1:237]], id=[[@39,138:139='id',<459>,1:138], [@48,193:193='r',<459>,1:193]]}, interface={exported_value=[{name=item_value, table_ref=query0}], id=[{name=id, table_ref=query0}]}, table_alias={exploded=query0}}, def_query0={query_dictionary={id=[[@7,27:28='id',<459>,1:27], [@39,138:139='id',<459>,1:138]], item_value=[[@13,42:51='item_value',<459>,1:42], [@41,142:151='item_value',<459>,1:142]]}, table_dictionary={flatten0={value=[[@9,31:31='f',<459>,1:31]]}, events={payload=[[@22,85:85='a',<459>,1:85]], active=[[@28,104:104='a',<459>,1:104]], id=[[@5,25:25='a',<459>,1:25]]}}, filters=[{name=active, table_ref=a}], interface={id=[{name=id, table_ref=a}], item_value=[{name=value, table_ref=f}]}, table_alias={a=events, f=flatten0}}, filters=[{name=exported_value, table_ref=r}], interface={exported_value=[{name=exported_value, table_ref=r}], id=[{name=id, table_ref=r}]}, table_alias={r=query1, exploded=query0, renamed=query1}}}");
	}

	@Test
	public void arC028_flattenSetOpConsumerV0Test() {
		final String query = "SELECT a.id, f.value AS item_value FROM events a, FLATTEN(input => a.payload) f WHERE a.active = 1 UNION ALL SELECT b.id AS other_id, b.label AS other_value FROM archive_events b WHERE b.active = 0";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_flatten_table_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={union={1={select={1={column={name=id, table_ref=a}}, 2={column={name=value, table_ref=f}, alias=item_value}}, from={join={1={table={alias=a, table=events}}, 2={table={alias=f, table_function={function_name=FLATTEN, parameters={input={column={name=payload, table_ref=a}}}}}}}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}, 2={union={qualifier=ALL, operator=UNION}}, 3={select={1={column={name=id, table_ref=b}, alias=other_id}, 2={column={name=label, table_ref=b}, alias=other_value}}, from={table={alias=b, table=archive_events}}, where={condition={left={column={name=active, table_ref=b}}, right={literal=0}, operator==}}}}}}",
				"[id, item_value]",
				"{}",
				"{archive_events={active=[[@47,185:185='b',<459>,1:185]], label=[[@38,134:134='b',<459>,1:134]], id=[[@32,116:116='b',<459>,1:116]]}, flatten0={value=[[@5,13:13='f',<459>,1:13]]}, events={payload=[[@18,67:67='a',<459>,1:67]], active=[[@24,86:86='a',<459>,1:86]], id=[[@1,7:7='a',<459>,1:7]]}}",
				"{query0={item_value=[[@9,24:33='item_value',<459>,1:24]], id=[[@3,9:10='id',<459>,1:9]]}, query1={other_value=[[@42,145:155='other_value',<459>,1:145]], other_id=[[@36,124:131='other_id',<459>,1:124]]}}",
				"{def_union2={def_query1={query_dictionary={other_value=[[@42,145:155='other_value',<459>,1:145]], other_id=[[@36,124:131='other_id',<459>,1:124]]}, table_dictionary={archive_events={active=[[@47,185:185='b',<459>,1:185]], label=[[@38,134:134='b',<459>,1:134]], id=[[@32,116:116='b',<459>,1:116]]}}, setop=UNION, filters=[{name=active, table_ref=b}], interface={other_value=[{name=label, table_ref=b}], other_id=[{name=id, table_ref=b}]}, table_alias={b=archive_events}}, def_query0={query_dictionary={id=[[@3,9:10='id',<459>,1:9]], item_value=[[@9,24:33='item_value',<459>,1:24]]}, table_dictionary={flatten0={value=[[@5,13:13='f',<459>,1:13]]}, events={payload=[[@18,67:67='a',<459>,1:67]], active=[[@24,86:86='a',<459>,1:86]], id=[[@1,7:7='a',<459>,1:7]]}}, filters=[{name=active, table_ref=a}], interface={id=[{name=id, table_ref=a}], item_value=[{name=value, table_ref=f}]}, table_alias={a=events, f=flatten0}}, interface={id=query_column, item_value=query_column}}}");
	}

	@Test
	public void arC028_flattenJoinConsumerV0Test() {
		final String query = "SELECT q.id, q.item_value, b.label FROM (SELECT a.id, f.value AS item_value FROM events a, FLATTEN(input => a.payload) f WHERE a.active = 1) q JOIN labels b ON q.id = b.event_id WHERE b.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_flatten_table_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=q}}, 2={column={name=item_value, table_ref=q}}, 3={column={name=label, table_ref=b}}}, from={join={1={table={alias=q, query={select={1={column={name=id, table_ref=a}}, 2={column={name=value, table_ref=f}, alias=item_value}}, from={join={1={table={alias=a, table=events}}, 2={table={alias=f, table_function={function_name=FLATTEN, parameters={input={column={name=payload, table_ref=a}}}}}}}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}}, 2={join=JOIN, on={condition={left={column={name=id, table_ref=q}}, right={column={name=event_id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=labels}}}}, where={condition={left={column={name=active, table_ref=b}}, right={literal=1}, operator==}}}}",
				"[id, label, item_value]",
				"{}",
				"{flatten0={value=[[@19,54:54='f',<459>,1:54]]}, events={payload=[[@32,108:108='a',<459>,1:108]], active=[[@38,127:127='a',<459>,1:127]], id=[[@15,48:48='a',<459>,1:48]]}, labels={event_id=[[@53,167:167='b',<459>,1:167]], active=[[@57,184:184='b',<459>,1:184]], label=[[@9,27:27='b',<459>,1:27]]}}",
				"{query0={item_value=[[@23,65:74='item_value',<459>,1:65], [@5,13:13='q',<459>,1:13]], id=[[@17,50:51='id',<459>,1:50], [@1,7:7='q',<459>,1:7], [@49,160:160='q',<459>,1:160]]}, query1={item_value=[[@7,15:24='item_value',<459>,1:15]], id=[[@3,9:10='id',<459>,1:9]], label=[[@11,29:33='label',<459>,1:29]]}}",
				"{def_query1={query_dictionary={id=[[@3,9:10='id',<459>,1:9]], label=[[@11,29:33='label',<459>,1:29]], item_value=[[@7,15:24='item_value',<459>,1:15]]}, table_dictionary={labels={event_id=[[@53,167:167='b',<459>,1:167]], active=[[@57,184:184='b',<459>,1:184]], label=[[@9,27:27='b',<459>,1:27]]}}, def_query0={query_dictionary={id=[[@17,50:51='id',<459>,1:50], [@1,7:7='q',<459>,1:7], [@49,160:160='q',<459>,1:160]], item_value=[[@23,65:74='item_value',<459>,1:65], [@5,13:13='q',<459>,1:13]]}, table_dictionary={flatten0={value=[[@19,54:54='f',<459>,1:54]]}, events={payload=[[@32,108:108='a',<459>,1:108]], active=[[@38,127:127='a',<459>,1:127]], id=[[@15,48:48='a',<459>,1:48]]}}, filters=[{name=active, table_ref=a}], interface={id=[{name=id, table_ref=a}], item_value=[{name=value, table_ref=f}]}, table_alias={a=events, f=flatten0}}, filters=[{name=id, table_ref=q}, {name=event_id, table_ref=b}, {name=active, table_ref=b}], interface={id=[{name=id, table_ref=q}], label=[{name=label, table_ref=b}], item_value=[{name=item_value, table_ref=q}]}, table_alias={q=query0, b=labels}}}");
	}

	@Test
	public void arC028_flattenHiddenChildPrivateV0Test() {
		final String query = "SELECT q.id, a.payload FROM (SELECT a.id, f.value AS item_value FROM events a, FLATTEN(input => a.payload) f WHERE a.active = 1) q WHERE q.item_value IS NOT NULL";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_flatten_table_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.FATAL,
						"QUALIFIED_COLUMN_NOT_FOUND_IN_TABLE",
						"Source Table not found for Column 'payload' at (l:1 c:15). No alias or table called 'a'.",
						1, 15, "SqlASTWalkerHelper", null, "payload", false, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=q}}, 2={column={name=payload, table_ref=a}}}, from={table={alias=q, query={select={1={column={name=id, table_ref=a}}, 2={column={name=value, table_ref=f}, alias=item_value}}, from={join={1={table={alias=a, table=events}}, 2={table={alias=f, table_function={function_name=FLATTEN, parameters={input={column={name=payload, table_ref=a}}}}}}}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}}, where={condition={left={column={name=item_value, table_ref=q}}, operator=IS NOT NULL}}}}",
				"[payload, id]",
				"{}",
				"{flatten0={value=[[@15,42:42='f',<459>,1:42]]}, events={payload=[[@28,96:96='a',<459>,1:96]], active=[[@34,115:115='a',<459>,1:115]], id=[[@11,36:36='a',<459>,1:36]]}}",
				"{query0={item_value=[[@19,53:62='item_value',<459>,1:53], [@42,137:137='q',<459>,1:137]], id=[[@13,38:39='id',<459>,1:38], [@1,7:7='q',<459>,1:7]]}, query1={payload=[[@7,15:21='payload',<459>,1:15]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query1={query_dictionary={payload=[[@7,15:21='payload',<459>,1:15]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={}, def_query0={query_dictionary={id=[[@13,38:39='id',<459>,1:38], [@1,7:7='q',<459>,1:7]], item_value=[[@19,53:62='item_value',<459>,1:53], [@42,137:137='q',<459>,1:137]]}, table_dictionary={flatten0={value=[[@15,42:42='f',<459>,1:42]]}, events={payload=[[@28,96:96='a',<459>,1:96]], active=[[@34,115:115='a',<459>,1:115]], id=[[@11,36:36='a',<459>,1:36]]}}, filters=[{name=active, table_ref=a}], interface={id=[{name=id, table_ref=a}], item_value=[{name=value, table_ref=f}]}, table_alias={a=events, f=flatten0}}, filters=[{name=item_value, table_ref=q}], interface={payload=[{name=payload, table_ref=a}], id=[{name=id, table_ref=q}]}, table_alias={q=query0}}}");
	}

	@Test
	public void arC028_flattenGroupedConsumerV0Test() {
		final String query = "SELECT a.id, count(f.value) AS item_count FROM events a, FLATTEN(input => a.payload) f WHERE a.active = 1 GROUP BY a.id HAVING count(f.value) > 1 ORDER BY a.id";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_flatten_table_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={function_name=count, qualifier=null, parameters={column={name=value, table_ref=f}}}, alias=item_count}}, having={condition={left={function={function_name=count, qualifier=null, parameters={column={name=value, table_ref=f}}}}, right={literal=1}, operator=>}}, orderby={1={null_order=null, predicand={column={name=id, table_ref=a}}, sort_order=ASC}}, from={join={1={table={alias=a, table=events}}, 2={table={alias=f, table_function={function_name=FLATTEN, parameters={input={column={name=payload, table_ref=a}}}}}}}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}, groupby={1={column={name=id, table_ref=a}}}}}",
				"[item_count, id]",
				"{}",
				"{flatten0={value=[[@7,19:19='f',<459>,1:19], [@40,133:133='f',<459>,1:133]]}, events={payload=[[@21,74:74='a',<459>,1:74]], active=[[@27,93:93='a',<459>,1:93]], id=[[@1,7:7='a',<459>,1:7], [@34,115:115='a',<459>,1:115], [@48,155:155='a',<459>,1:155]]}}",
				"{query0={item_count=[[@12,31:40='item_count',<459>,1:31]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={item_count=[[@12,31:40='item_count',<459>,1:31]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={flatten0={value=[[@7,19:19='f',<459>,1:19], [@40,133:133='f',<459>,1:133]]}, events={payload=[[@21,74:74='a',<459>,1:74]], active=[[@27,93:93='a',<459>,1:93]], id=[[@1,7:7='a',<459>,1:7], [@34,115:115='a',<459>,1:115], [@48,155:155='a',<459>,1:155]]}}, grouped_by=[{name=id, table_ref=a}], ordered_by=[{name=id, table_ref=a}], filters=[{name=active, table_ref=a}, {name=value, table_ref=f}], interface={item_count=[{name=value, table_ref=f}], id=[{name=id, table_ref=a}]}, table_alias={a=events, f=flatten0}}}");
	}

	@Test
	public void arC028_flattenInsertConsumerV0Test() {
		final String query = "INSERT INTO results (id, item_value) SELECT a.id, f.value AS item_value FROM events a, FLATTEN(input => a.payload) f WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_flatten_table_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={insert={preamble=insert_into, from={from={join={1={table={alias=a, table=events}}, 2={table={alias=f, table_function={function_name=FLATTEN, parameters={input={column={name=payload, table_ref=a}}}}}}}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}, select={1={column={name=id, table_ref=a}}, 2={column={name=value, table_ref=f}, alias=item_value}}}, target_table={table={alias=null, table=results}}, columns={1={column={name=id, table_ref=null}}, 2={column={name=item_value, table_ref=null}}}}}}",
				"[id, item_value]",
				"{}",
				"{flatten0={value=[[@13,50:50='f',<459>,1:50]]}, results={id=[[@4,21:22='id',<459>,1:21]], item_value=[[@6,25:34='item_value',<459>,1:25]]}, events={payload=[[@26,104:104='a',<459>,1:104]], active=[[@32,123:123='a',<459>,1:123]], id=[[@9,44:44='a',<459>,1:44]]}}",
				"{query0={item_value=[[@17,61:70='item_value',<459>,1:61]], id=[[@11,46:47='id',<459>,1:46]]}, insert1={item_value=[[@6,25:34='item_value',<459>,1:25]], id=[[@4,21:22='id',<459>,1:21]]}}",
				"{def_insert1={query_dictionary={item_value=[[@6,25:34='item_value',<459>,1:25]], id=[[@4,21:22='id',<459>,1:21]]}, table_dictionary={results={id=[[@4,21:22='id',<459>,1:21]], item_value=[[@6,25:34='item_value',<459>,1:25]]}}, def_query0={query_dictionary={id=[[@11,46:47='id',<459>,1:46]], item_value=[[@17,61:70='item_value',<459>,1:61]]}, table_dictionary={flatten0={value=[[@13,50:50='f',<459>,1:50]]}, events={payload=[[@26,104:104='a',<459>,1:104]], active=[[@32,123:123='a',<459>,1:123]], id=[[@9,44:44='a',<459>,1:44]]}}, filters=[{name=active, table_ref=a}], interface={id=[{name=id, table_ref=a}], item_value=[{name=value, table_ref=f}]}, table_alias={a=events, f=flatten0}}, interface={id=[{name=id, table_ref=query0}], item_value=[{name=item_value, table_ref=query0}]}}}");
	}

	@Test
	public void arC028_flattenDdlConsumerV0Test() {
		final String query = "CREATE VIEW v_flatten AS SELECT a.id, f.value AS item_value FROM events a, FLATTEN(input => a.payload) f WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.DdlContext tree = parser.ddl();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_flatten_table_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{DDL={create={type=VIEW, name={table=v_flatten}, query={select={1={column={name=id, table_ref=a}}, 2={column={name=value, table_ref=f}, alias=item_value}}, from={join={1={table={alias=a, table=events}}, 2={table={alias=f, table_function={function_name=FLATTEN, parameters={input={column={name=payload, table_ref=a}}}}}}}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}}}",
				"[]",
				"{}",
				"{flatten0={value=[[@9,38:38='f',<459>,1:38]]}, events={payload=[[@22,92:92='a',<459>,1:92]], active=[[@28,111:111='a',<459>,1:111]], id=[[@5,32:32='a',<459>,1:32]]}}",
				"{query0={item_value=[[@13,49:58='item_value',<459>,1:49]], id=[[@7,34:35='id',<459>,1:34]]}}",
				"{def_create1={def_query0={query_dictionary={id=[[@7,34:35='id',<459>,1:34]], item_value=[[@13,49:58='item_value',<459>,1:49]]}, table_dictionary={flatten0={value=[[@9,38:38='f',<459>,1:38]]}, events={payload=[[@22,92:92='a',<459>,1:92]], active=[[@28,111:111='a',<459>,1:111]], id=[[@5,32:32='a',<459>,1:32]]}}, filters=[{name=active, table_ref=a}], interface={id=[{name=id, table_ref=a}], item_value=[{name=value, table_ref=f}]}, table_alias={a=events, f=flatten0}}}}");
	}

	@Test
	public void arC028_flattenScriptResetV0Test() {
		final String query = "SELECT a.id, f.value AS item_value FROM events a, FLATTEN(input => a.payload) f WHERE a.active = 1; SELECT b.id FROM orders b; SELECT a.id, f.value AS item_value FROM events a, FLATTEN(input => a.payload) f WHERE a.active = 1;";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.ScriptContext tree = parser.script();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 2,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_flatten_table_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SCRIPT={1={select={1={column={name=id, table_ref=a}}, 2={column={name=value, table_ref=f}, alias=item_value}}, from={join={1={table={alias=a, table=events}}, 2={table={alias=f, table_function={function_name=FLATTEN, parameters={input={column={name=payload, table_ref=a}}}}}}}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}, 2={select={1={column={name=id, table_ref=b}}}, from={table={alias=b, table=orders}}}, 3={select={1={column={name=id, table_ref=a}}, 2={column={name=value, table_ref=f}, alias=item_value}}, from={join={1={table={alias=a, table=events}}, 2={table={alias=f, table_function={function_name=FLATTEN, parameters={input={column={name=payload, table_ref=a}}}}}}}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}}",
				"[]",
				"{SCRIPT={1={}, 2={}, 3={}}}",
				"{SCRIPT={1={flatten0={value=[[@5,13:13='f',<459>,1:13]]}, events={payload=[[@18,67:67='a',<459>,1:67]], active=[[@24,86:86='a',<459>,1:86]], id=[[@1,7:7='a',<459>,1:7]]}}, 2={orders={id=[[@31,107:107='b',<459>,1:107]]}}, 3={flatten1={value=[[@43,140:140='f',<459>,1:140]]}, events={payload=[[@56,194:194='a',<459>,1:194]], active=[[@62,213:213='a',<459>,1:213]], id=[[@39,134:134='a',<459>,1:134]]}}}}",
				"{SCRIPT={1={query0={item_value=[[@9,24:33='item_value',<459>,1:24]], id=[[@3,9:10='id',<459>,1:9]]}}, 2={query0={id=[[@33,109:110='id',<459>,1:109]]}}, 3={query0={item_value=[[@47,151:160='item_value',<459>,1:151]], id=[[@41,136:137='id',<459>,1:136]]}}}}",
				"{SCRIPT={1={def_query0={query_dictionary={id=[[@3,9:10='id',<459>,1:9]], item_value=[[@9,24:33='item_value',<459>,1:24]]}, table_dictionary={flatten0={value=[[@5,13:13='f',<459>,1:13]]}, events={payload=[[@18,67:67='a',<459>,1:67]], active=[[@24,86:86='a',<459>,1:86]], id=[[@1,7:7='a',<459>,1:7]]}}, filters=[{name=active, table_ref=a}], interface={id=[{name=id, table_ref=a}], item_value=[{name=value, table_ref=f}]}, table_alias={a=events, f=flatten0}}}, 2={def_query0={query_dictionary={id=[[@33,109:110='id',<459>,1:109]]}, table_dictionary={orders={id=[[@31,107:107='b',<459>,1:107]]}}, interface={id=[{name=id, table_ref=b}]}, table_alias={b=orders}}}, 3={def_query0={query_dictionary={id=[[@41,136:137='id',<459>,1:136]], item_value=[[@47,151:160='item_value',<459>,1:151]]}, table_dictionary={flatten1={value=[[@43,140:140='f',<459>,1:140]]}, events={payload=[[@56,194:194='a',<459>,1:194]], active=[[@62,213:213='a',<459>,1:213]], id=[[@39,134:134='a',<459>,1:134]]}}, filters=[{name=active, table_ref=a}], interface={id=[{name=id, table_ref=a}], item_value=[{name=value, table_ref=f}]}, table_alias={a=events, f=flatten1}}}}}");
	}

	@Test
	public void arC028_flattenLowercaseV0Test() {
		final String query = "select a.id, f.value as item_value from events a, flatten(input => a.payload) f where a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_flatten_table_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={column={name=value, table_ref=f}, alias=item_value}}, from={join={1={table={alias=a, table=events}}, 2={table={alias=f, table_function={function_name=flatten, parameters={input={column={name=payload, table_ref=a}}}}}}}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[id, item_value]",
				"{}",
				"{flatten0={value=[[@5,13:13='f',<459>,1:13]]}, events={payload=[[@18,67:67='a',<459>,1:67]], active=[[@24,86:86='a',<459>,1:86]], id=[[@1,7:7='a',<459>,1:7]]}}",
				"{query0={item_value=[[@9,24:33='item_value',<459>,1:24]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={id=[[@3,9:10='id',<459>,1:9]], item_value=[[@9,24:33='item_value',<459>,1:24]]}, table_dictionary={flatten0={value=[[@5,13:13='f',<459>,1:13]]}, events={payload=[[@18,67:67='a',<459>,1:67]], active=[[@24,86:86='a',<459>,1:86]], id=[[@1,7:7='a',<459>,1:7]]}}, filters=[{name=active, table_ref=a}], interface={id=[{name=id, table_ref=a}], item_value=[{name=value, table_ref=f}]}, table_alias={a=events, f=flatten0}}}");
	}

	@Test
	public void arC028_flattenSameNamedColumnV0Test() {
		final String query = "SELECT a.flatten, a.id FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 0,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_flatten_table_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=flatten, table_ref=a}}, 2={column={name=id, table_ref=a}}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[flatten, id]",
				"{}",
				"{events={flatten=[[@1,7:7='a',<459>,1:7]], active=[[@12,43:43='a',<459>,1:43]], id=[[@5,18:18='a',<459>,1:18]]}}",
				"{query0={flatten=[[@3,9:15='flatten',<339>,1:9]], id=[[@7,20:21='id',<459>,1:20]]}}",
				"{def_query0={query_dictionary={flatten=[[@3,9:15='flatten',<339>,1:9]], id=[[@7,20:21='id',<459>,1:20]]}, table_dictionary={events={flatten=[[@1,7:7='a',<459>,1:7]], active=[[@12,43:43='a',<459>,1:43]], id=[[@5,18:18='a',<459>,1:18]]}}, filters=[{name=active, table_ref=a}], interface={flatten=[{name=flatten, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC028_flattenMissingInputSyntaxV0Test() {
		final String query = "SELECT f.value FROM TABLE(FLATTEN(path => 'items')) f";
		final SQLSelectParserParser parser = parse(query);
		parser.sql();
		final errorhandling.ParseErrorCollector collector =
				(errorhandling.ParseErrorCollector) parser.getErrorHandler();
		Assert.assertEquals("Parser error count is wrong", 3, collector.getErrorCount());
		final java.util.List<ParseDiagnostic> diagnostics = new java.util.ArrayList<>(collector.getDiagnostics());
		diagnostics.addAll(collectParserListenerDiagnostics(parser));
		Assert.assertEquals("Parser diagnostics are wrong", Arrays.asList(
				new ParseDiagnostic(ParseDiagnostic.Severity.FATAL,
						"REPORT_ERROR",
						"Line 1:49 - unexpected input: ')' in rule flatten_argument_list: SELECT f.value FROM TABLE(FLATTEN(path => 'items')) f",
						1, 49, "ParseErrorCollector", "flatten_argument_list", ")", false, "parse.strategy", "NoViableAltException", java.util.Map.of("contextSnippet", "SELECT f.value FROM TABLE(FLATTEN(path => 'items')) f", "parserRules", "flatten_argument_list,flatten_table_function,table_function", "syntaxClass", "GRAMMAR_GAP")),
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"RECOVER",
						"Line 1:49 - Syntax error, attempting recovery",
						1, 49, "ParseErrorCollector", null, ")", true, "parse.strategy", "NoViableAltException", null),
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"RECOVER_INLINE",
						"Line 1:52 - Invalid syntax near 'f'",
						1, 52, "ParseErrorCollector", null, "f", true, "parse.strategy", null, null)), diagnostics);
	}

	@Test
	public void arC028_flattenInvalidModeSyntaxV0Test() {
		final String query = "SELECT f.value FROM TABLE(FLATTEN(input => NULL, mode => 'INVALID')) f";
		final SQLSelectParserParser parser = parse(query);
		parser.sql();
		final errorhandling.ParseErrorCollector collector =
				(errorhandling.ParseErrorCollector) parser.getErrorHandler();
		Assert.assertEquals("Parser error count is wrong", 3, collector.getErrorCount());
		final java.util.List<ParseDiagnostic> diagnostics = new java.util.ArrayList<>(collector.getDiagnostics());
		diagnostics.addAll(collectParserListenerDiagnostics(parser));
		Assert.assertEquals("Parser diagnostics are wrong", Arrays.asList(
				new ParseDiagnostic(ParseDiagnostic.Severity.FATAL,
						"REPORT_ERROR",
						"Line 1:66 - unexpected input: ')' in rule flatten_argument: SELECT f.value FROM TABLE(FLATTEN(input => NULL, mode => 'INVALID')) f",
						1, 66, "ParseErrorCollector", "flatten_argument", ")", false, "parse.strategy", "FailedPredicateException", java.util.Map.of("contextSnippet", "SELECT f.value FROM TABLE(FLATTEN(input => NULL, mode => 'INVALID')) f", "parserRules", "flatten_argument,flatten_argument_list,flatten_table_function", "syntaxClass", "GRAMMAR_GAP")),
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"RECOVER",
						"Line 1:66 - Syntax error, attempting recovery",
						1, 66, "ParseErrorCollector", null, ")", true, "parse.strategy", "FailedPredicateException", null),
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"RECOVER_INLINE",
						"Line 1:69 - Invalid syntax near 'f'",
						1, 69, "ParseErrorCollector", null, "f", true, "parse.strategy", null, null)), diagnostics);
	}

	@Test
	public void arC028_flattenTrailingCommaSyntaxV0Test() {
		final String query = "SELECT f.value FROM TABLE(FLATTEN(input => NULL,)) f";
		final SQLSelectParserParser parser = parse(query);
		parser.sql();
		final errorhandling.ParseErrorCollector collector =
				(errorhandling.ParseErrorCollector) parser.getErrorHandler();
		Assert.assertEquals("Parser error count is wrong", 3, collector.getErrorCount());
		final java.util.List<ParseDiagnostic> diagnostics = new java.util.ArrayList<>(collector.getDiagnostics());
		diagnostics.addAll(collectParserListenerDiagnostics(parser));
		Assert.assertEquals("Parser diagnostics are wrong", Arrays.asList(
				new ParseDiagnostic(ParseDiagnostic.Severity.FATAL,
						"REPORT_ERROR",
						"Line 1:48 - unexpected input: ')' in rule flatten_argument: SELECT f.value FROM TABLE(FLATTEN(input => NULL,)) f",
						1, 48, "ParseErrorCollector", "flatten_argument", ")", false, "parse.strategy", "NoViableAltException", java.util.Map.of("contextSnippet", "SELECT f.value FROM TABLE(FLATTEN(input => NULL,)) f", "parserRules", "flatten_argument,flatten_argument_list,flatten_table_function", "syntaxClass", "GRAMMAR_GAP")),
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"RECOVER",
						"Line 1:48 - Syntax error, attempting recovery",
						1, 48, "ParseErrorCollector", null, ")", true, "parse.strategy", "NoViableAltException", null),
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"RECOVER_INLINE",
						"Line 1:51 - Invalid syntax near 'f'",
						1, 51, "ParseErrorCollector", null, "f", true, "parse.strategy", null, null)), diagnostics);
	}

	@Test
	public void arC028_flattenMissingArrowSyntaxV0Test() {
		final String query = "SELECT f.value FROM TABLE(FLATTEN(input NULL)) f";
		final SQLSelectParserParser parser = parse(query);
		parser.sql();
		final errorhandling.ParseErrorCollector collector =
				(errorhandling.ParseErrorCollector) parser.getErrorHandler();
		Assert.assertEquals("Parser error count is wrong", 4, collector.getErrorCount());
		final java.util.List<ParseDiagnostic> diagnostics = new java.util.ArrayList<>(collector.getDiagnostics());
		diagnostics.addAll(collectParserListenerDiagnostics(parser));
		Assert.assertEquals("Parser diagnostics are wrong", Arrays.asList(
				new ParseDiagnostic(ParseDiagnostic.Severity.FATAL,
						"REPORT_ERROR",
						"Line 1:40 - unexpected input: 'NULL' in rule table_function: SELECT f.value FROM TABLE(FLATTEN(input NULL)) f",
						1, 40, "ParseErrorCollector", "table_function", "NULL", false, "parse.strategy", "NoViableAltException", java.util.Map.of("contextSnippet", "SELECT f.value FROM TABLE(FLATTEN(input NULL)) f", "parserRules", "table_function,table_function_primary,table_source_primary", "syntaxClass", "GRAMMAR_GAP")),
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"RECOVER",
						"Line 1:40 - Syntax error, attempting recovery",
						1, 40, "ParseErrorCollector", null, "NULL", true, "parse.strategy", "NoViableAltException", null),
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"RECOVER_INLINE",
						"Line 1:33 - Invalid syntax near '('",
						1, 33, "ParseErrorCollector", null, "(", true, "parse.strategy", null, null),
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"RECOVER_INLINE",
						"Line 1:33 - Invalid syntax near '('",
						1, 33, "ParseErrorCollector", null, "(", true, "parse.strategy", null, null)), diagnostics);
	}

	@Test
	public void arC028_flattenMissingCloseSyntaxV0Test() {
		final String query = "SELECT f.value FROM TABLE(FLATTEN(input => NULL) f";
		final SQLSelectParserParser parser = parse(query);
		parser.sql();
		final errorhandling.ParseErrorCollector collector =
				(errorhandling.ParseErrorCollector) parser.getErrorHandler();
		Assert.assertEquals("Parser error count is wrong", 1, collector.getErrorCount());
		final java.util.List<ParseDiagnostic> diagnostics = new java.util.ArrayList<>(collector.getDiagnostics());
		diagnostics.addAll(collectParserListenerDiagnostics(parser));
		Assert.assertEquals("Parser diagnostics are wrong", Arrays.asList(
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"RECOVER_INLINE",
						"Line 1:49 - Invalid syntax near 'f'",
						1, 49, "ParseErrorCollector", null, "f", true, "parse.strategy", null, null)), diagnostics);
	}

	@Test
	public void arC028_flattenQUERYSnippetV0Test() {
		final String query = "SELECT a.id, f.value AS item_value FROM events a, FLATTEN(input => a.payload) f WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final org.antlr.v4.runtime.ParserRuleContext tree = parser.query_value();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_flatten_table_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{QUERY={select={1={column={name=id, table_ref=a}}, 2={column={name=value, table_ref=f}, alias=item_value}}, from={join={1={table={alias=a, table=events}}, 2={table={alias=f, table_function={function_name=FLATTEN, parameters={input={column={name=payload, table_ref=a}}}}}}}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[id, item_value]",
				"{}",
				"{flatten0={value=[[@5,13:13='f',<459>,1:13]]}, events={payload=[[@18,67:67='a',<459>,1:67]], active=[[@24,86:86='a',<459>,1:86]], id=[[@1,7:7='a',<459>,1:7]]}}",
				"{query0={item_value=[[@9,24:33='item_value',<459>,1:24]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={id=[[@3,9:10='id',<459>,1:9]], item_value=[[@9,24:33='item_value',<459>,1:24]]}, table_dictionary={flatten0={value=[[@5,13:13='f',<459>,1:13]]}, events={payload=[[@18,67:67='a',<459>,1:67]], active=[[@24,86:86='a',<459>,1:86]], id=[[@1,7:7='a',<459>,1:7]]}}, filters=[{name=active, table_ref=a}], interface={id=[{name=id, table_ref=a}], item_value=[{name=value, table_ref=f}]}, table_alias={a=events, f=flatten0}}}");
		final access.SqlParserAccess accessParser = new access.SqlParserAccess(false, false, false);
		accessParser.executeTheParse(query, "QUERY");
		Assert.assertEquals("Access syntax errors", 0, accessParser.getParser().getNumberOfSyntaxErrors());
		Assert.assertEquals("Access diagnostics are wrong", Arrays.<ParseDiagnostic>asList(), accessParser.getAllDiagnostics());
		Assert.assertEquals("Access AST is wrong", "{QUERY={select={1={column={name=id, table_ref=a}}, 2={column={name=value, table_ref=f}, alias=item_value}}, from={join={1={table={alias=a, table=events}}, 2={table={alias=f, table_function={function_name=FLATTEN, parameters={input={column={name=payload, table_ref=a}}}}}}}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}", accessParser.getSnippet().getSqlAbstractTree().toString());
		Assert.assertEquals("Access Interface is wrong", "[id, item_value]", accessParser.getSnippet().getQueryInterface().toString());
		Assert.assertEquals("Access Substitution List is wrong", "{}", accessParser.getSnippet().getSubstitutionsMap().toString());
		Assert.assertEquals("Access Table Dictionary is wrong", "{flatten0={value=[[@5,13:13='f',<459>,1:13]]}, events={payload=[[@18,67:67='a',<459>,1:67]], active=[[@24,86:86='a',<459>,1:86]], id=[[@1,7:7='a',<459>,1:7]]}}", accessParser.getSnippet().getTableDictionary().toString());
		Assert.assertEquals("Access Query Column Dictionary is wrong", "{query0={item_value=[[@9,24:33='item_value',<459>,1:24]], id=[[@3,9:10='id',<459>,1:9]]}}", accessParser.getSnippet().getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Access Symbol Table is wrong", "{def_query0={query_dictionary={id=[[@3,9:10='id',<459>,1:9]], item_value=[[@9,24:33='item_value',<459>,1:24]]}, table_dictionary={flatten0={value=[[@5,13:13='f',<459>,1:13]]}, events={payload=[[@18,67:67='a',<459>,1:67]], active=[[@24,86:86='a',<459>,1:86]], id=[[@1,7:7='a',<459>,1:7]]}}, filters=[{name=active, table_ref=a}], interface={id=[{name=id, table_ref=a}], item_value=[{name=value, table_ref=f}]}, table_alias={a=events, f=flatten0}}}", accessParser.getSnippet().getSymbolTable().toString());
	}

	@Test
	public void arC028_flattenTUPLESnippetV0Test() {
		final String query = "TABLE(FLATTEN(input => PARSE_JSON('[1,2]')))";
		final SQLSelectParserParser parser = parse(query);
		final org.antlr.v4.runtime.ParserRuleContext tree = parser.tuple_value();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_flatten_table_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{TUPLE={table_function={function_name=FLATTEN, parameters={input={function={parameters={1={literal='[1,2]'}}, function_name=PARSE_JSON}}}}}}",
				"[]",
				"{}",
				"{}",
				"{}",
				"{}");
		final access.SqlParserAccess accessParser = new access.SqlParserAccess(false, false, false);
		accessParser.executeTheParse(query, "TUPLE");
		Assert.assertEquals("Access syntax errors", 0, accessParser.getParser().getNumberOfSyntaxErrors());
		Assert.assertEquals("Access diagnostics are wrong", Arrays.<ParseDiagnostic>asList(), accessParser.getAllDiagnostics());
		Assert.assertEquals("Access AST is wrong", "{TUPLE={table_function={function_name=FLATTEN, parameters={input={function={parameters={1={literal='[1,2]'}}, function_name=PARSE_JSON}}}}}}", accessParser.getSnippet().getSqlAbstractTree().toString());
		Assert.assertEquals("Access Interface is wrong", "[]", accessParser.getSnippet().getQueryInterface().toString());
		Assert.assertEquals("Access Substitution List is wrong", "{}", accessParser.getSnippet().getSubstitutionsMap().toString());
		Assert.assertEquals("Access Table Dictionary is wrong", "{}", accessParser.getSnippet().getTableDictionary().toString());
		Assert.assertEquals("Access Query Column Dictionary is wrong", "{}", accessParser.getSnippet().getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Access Symbol Table is wrong", "{}", accessParser.getSnippet().getSymbolTable().toString());
	}

	@Test
	public void arC028_flattenJOIN_EXTENSIONSnippetV0Test() {
		final String query = "CROSS JOIN FLATTEN(input => PARSE_JSON('[1,2]')) f";
		final SQLSelectParserParser parser = parse(query);
		final org.antlr.v4.runtime.ParserRuleContext tree = parser.join_extension_value();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_flatten_table_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{JOIN_EXTENSION={1={join=CROSSJOIN}, 2={table={alias=f, table_function={function_name=FLATTEN, parameters={input={function={parameters={1={literal='[1,2]'}}, function_name=PARSE_JSON}}}}}}}}",
				"[]",
				"{}",
				"{flatten0={}}",
				"{}",
				"{table_dictionary={flatten0={}}, table_alias={f=flatten0}}");
		final access.SqlParserAccess accessParser = new access.SqlParserAccess(false, false, false);
		accessParser.executeTheParse(query, "JOIN_EXTENSION");
		Assert.assertEquals("Access syntax errors", 0, accessParser.getParser().getNumberOfSyntaxErrors());
		Assert.assertEquals("Access diagnostics are wrong", Arrays.<ParseDiagnostic>asList(), accessParser.getAllDiagnostics());
		Assert.assertEquals("Access AST is wrong", "{JOIN_EXTENSION={1={join=CROSSJOIN}, 2={table={alias=f, table_function={function_name=FLATTEN, parameters={input={function={parameters={1={literal='[1,2]'}}, function_name=PARSE_JSON}}}}}}}}", accessParser.getSnippet().getSqlAbstractTree().toString());
		Assert.assertEquals("Access Interface is wrong", "[]", accessParser.getSnippet().getQueryInterface().toString());
		Assert.assertEquals("Access Substitution List is wrong", "{}", accessParser.getSnippet().getSubstitutionsMap().toString());
		Assert.assertEquals("Access Table Dictionary is wrong", "{flatten0={}}", accessParser.getSnippet().getTableDictionary().toString());
		Assert.assertEquals("Access Query Column Dictionary is wrong", "{}", accessParser.getSnippet().getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Access Symbol Table is wrong", "{table_dictionary={flatten0={}}, table_alias={f=flatten0}}", accessParser.getSnippet().getSymbolTable().toString());
	}

	@Test
	public void arC028_flattenINSERTSnippetV0Test() {
		final String query = "INSERT INTO results (id, item_value) SELECT a.id, f.value AS item_value FROM events a, FLATTEN(input => a.payload) f WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final org.antlr.v4.runtime.ParserRuleContext tree = parser.insert_end_point();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_flatten_table_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{INSERT={insert={preamble=insert_into, from={from={join={1={table={alias=a, table=events}}, 2={table={alias=f, table_function={function_name=FLATTEN, parameters={input={column={name=payload, table_ref=a}}}}}}}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}, select={1={column={name=id, table_ref=a}}, 2={column={name=value, table_ref=f}, alias=item_value}}}, target_table={table={alias=null, table=results}}, columns={1={column={name=id, table_ref=null}}, 2={column={name=item_value, table_ref=null}}}}}}",
				"[id, item_value]",
				"{}",
				"{flatten0={value=[[@13,50:50='f',<459>,1:50]]}, results={id=[[@4,21:22='id',<459>,1:21]], item_value=[[@6,25:34='item_value',<459>,1:25]]}, events={payload=[[@26,104:104='a',<459>,1:104]], active=[[@32,123:123='a',<459>,1:123]], id=[[@9,44:44='a',<459>,1:44]]}}",
				"{query0={item_value=[[@17,61:70='item_value',<459>,1:61]], id=[[@11,46:47='id',<459>,1:46]]}, insert1={item_value=[[@6,25:34='item_value',<459>,1:25]], id=[[@4,21:22='id',<459>,1:21]]}}",
				"{def_insert1={query_dictionary={item_value=[[@6,25:34='item_value',<459>,1:25]], id=[[@4,21:22='id',<459>,1:21]]}, table_dictionary={results={id=[[@4,21:22='id',<459>,1:21]], item_value=[[@6,25:34='item_value',<459>,1:25]]}}, def_query0={query_dictionary={id=[[@11,46:47='id',<459>,1:46]], item_value=[[@17,61:70='item_value',<459>,1:61]]}, table_dictionary={flatten0={value=[[@13,50:50='f',<459>,1:50]]}, events={payload=[[@26,104:104='a',<459>,1:104]], active=[[@32,123:123='a',<459>,1:123]], id=[[@9,44:44='a',<459>,1:44]]}}, filters=[{name=active, table_ref=a}], interface={id=[{name=id, table_ref=a}], item_value=[{name=value, table_ref=f}]}, table_alias={a=events, f=flatten0}}, interface={id=[{name=id, table_ref=query0}], item_value=[{name=item_value, table_ref=query0}]}}}");
		final access.SqlParserAccess accessParser = new access.SqlParserAccess(false, false, false);
		accessParser.executeTheParse(query, "INSERT");
		Assert.assertEquals("Access syntax errors", 0, accessParser.getParser().getNumberOfSyntaxErrors());
		Assert.assertEquals("Access diagnostics are wrong", Arrays.<ParseDiagnostic>asList(), accessParser.getAllDiagnostics());
		Assert.assertEquals("Access AST is wrong", "{INSERT={insert={preamble=insert_into, from={from={join={1={table={alias=a, table=events}}, 2={table={alias=f, table_function={function_name=FLATTEN, parameters={input={column={name=payload, table_ref=a}}}}}}}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}, select={1={column={name=id, table_ref=a}}, 2={column={name=value, table_ref=f}, alias=item_value}}}, target_table={table={alias=null, table=results}}, columns={1={column={name=id, table_ref=null}}, 2={column={name=item_value, table_ref=null}}}}}}", accessParser.getSnippet().getSqlAbstractTree().toString());
		Assert.assertEquals("Access Interface is wrong", "[id, item_value]", accessParser.getSnippet().getQueryInterface().toString());
		Assert.assertEquals("Access Substitution List is wrong", "{}", accessParser.getSnippet().getSubstitutionsMap().toString());
		Assert.assertEquals("Access Table Dictionary is wrong", "{flatten0={value=[[@13,50:50='f',<459>,1:50]]}, results={id=[[@4,21:22='id',<459>,1:21]], item_value=[[@6,25:34='item_value',<459>,1:25]]}, events={payload=[[@26,104:104='a',<459>,1:104]], active=[[@32,123:123='a',<459>,1:123]], id=[[@9,44:44='a',<459>,1:44]]}}", accessParser.getSnippet().getTableDictionary().toString());
		Assert.assertEquals("Access Query Column Dictionary is wrong", "{query0={item_value=[[@17,61:70='item_value',<459>,1:61]], id=[[@11,46:47='id',<459>,1:46]]}, insert1={item_value=[[@6,25:34='item_value',<459>,1:25]], id=[[@4,21:22='id',<459>,1:21]]}}", accessParser.getSnippet().getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Access Symbol Table is wrong", "{def_insert1={query_dictionary={item_value=[[@6,25:34='item_value',<459>,1:25]], id=[[@4,21:22='id',<459>,1:21]]}, table_dictionary={results={id=[[@4,21:22='id',<459>,1:21]], item_value=[[@6,25:34='item_value',<459>,1:25]]}}, def_query0={query_dictionary={id=[[@11,46:47='id',<459>,1:46]], item_value=[[@17,61:70='item_value',<459>,1:61]]}, table_dictionary={flatten0={value=[[@13,50:50='f',<459>,1:50]]}, events={payload=[[@26,104:104='a',<459>,1:104]], active=[[@32,123:123='a',<459>,1:123]], id=[[@9,44:44='a',<459>,1:44]]}}, filters=[{name=active, table_ref=a}], interface={id=[{name=id, table_ref=a}], item_value=[{name=value, table_ref=f}]}, table_alias={a=events, f=flatten0}}, interface={id=[{name=id, table_ref=query0}], item_value=[{name=item_value, table_ref=query0}]}}}", accessParser.getSnippet().getSymbolTable().toString());
	}

	@Test
	public void arC028_flattenDDLSnippetV0Test() {
		final String query = "CREATE VIEW v_flatten AS SELECT a.id, f.value AS item_value FROM events a, FLATTEN(input => a.payload) f WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final org.antlr.v4.runtime.ParserRuleContext tree = parser.ddl();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_flatten_table_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{DDL={create={type=VIEW, name={table=v_flatten}, query={select={1={column={name=id, table_ref=a}}, 2={column={name=value, table_ref=f}, alias=item_value}}, from={join={1={table={alias=a, table=events}}, 2={table={alias=f, table_function={function_name=FLATTEN, parameters={input={column={name=payload, table_ref=a}}}}}}}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}}}",
				"[]",
				"{}",
				"{flatten0={value=[[@9,38:38='f',<459>,1:38]]}, events={payload=[[@22,92:92='a',<459>,1:92]], active=[[@28,111:111='a',<459>,1:111]], id=[[@5,32:32='a',<459>,1:32]]}}",
				"{query0={item_value=[[@13,49:58='item_value',<459>,1:49]], id=[[@7,34:35='id',<459>,1:34]]}}",
				"{def_create1={def_query0={query_dictionary={id=[[@7,34:35='id',<459>,1:34]], item_value=[[@13,49:58='item_value',<459>,1:49]]}, table_dictionary={flatten0={value=[[@9,38:38='f',<459>,1:38]]}, events={payload=[[@22,92:92='a',<459>,1:92]], active=[[@28,111:111='a',<459>,1:111]], id=[[@5,32:32='a',<459>,1:32]]}}, filters=[{name=active, table_ref=a}], interface={id=[{name=id, table_ref=a}], item_value=[{name=value, table_ref=f}]}, table_alias={a=events, f=flatten0}}}}");
		final access.SqlParserAccess accessParser = new access.SqlParserAccess(false, false, false);
		accessParser.executeTheParse(query, "DDL");
		Assert.assertEquals("Access syntax errors", 0, accessParser.getParser().getNumberOfSyntaxErrors());
		Assert.assertEquals("Access diagnostics are wrong", Arrays.<ParseDiagnostic>asList(), accessParser.getAllDiagnostics());
		Assert.assertEquals("Access AST is wrong", "{DDL={create={type=VIEW, name={table=v_flatten}, query={select={1={column={name=id, table_ref=a}}, 2={column={name=value, table_ref=f}, alias=item_value}}, from={join={1={table={alias=a, table=events}}, 2={table={alias=f, table_function={function_name=FLATTEN, parameters={input={column={name=payload, table_ref=a}}}}}}}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}}}", accessParser.getSnippet().getSqlAbstractTree().toString());
		Assert.assertEquals("Access Interface is wrong", "[]", accessParser.getSnippet().getQueryInterface().toString());
		Assert.assertEquals("Access Substitution List is wrong", "{}", accessParser.getSnippet().getSubstitutionsMap().toString());
		Assert.assertEquals("Access Table Dictionary is wrong", "{flatten0={value=[[@9,38:38='f',<459>,1:38]]}, events={payload=[[@22,92:92='a',<459>,1:92]], active=[[@28,111:111='a',<459>,1:111]], id=[[@5,32:32='a',<459>,1:32]]}}", accessParser.getSnippet().getTableDictionary().toString());
		Assert.assertEquals("Access Query Column Dictionary is wrong", "{query0={item_value=[[@13,49:58='item_value',<459>,1:49]], id=[[@7,34:35='id',<459>,1:34]]}}", accessParser.getSnippet().getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Access Symbol Table is wrong", "{def_create1={def_query0={query_dictionary={id=[[@7,34:35='id',<459>,1:34]], item_value=[[@13,49:58='item_value',<459>,1:49]]}, table_dictionary={flatten0={value=[[@9,38:38='f',<459>,1:38]]}, events={payload=[[@22,92:92='a',<459>,1:92]], active=[[@28,111:111='a',<459>,1:111]], id=[[@5,32:32='a',<459>,1:32]]}}, filters=[{name=active, table_ref=a}], interface={id=[{name=id, table_ref=a}], item_value=[{name=value, table_ref=f}]}, table_alias={a=events, f=flatten0}}}}", accessParser.getSnippet().getSymbolTable().toString());
	}

	@Test
	public void arC028_flattenSCRIPTSnippetV0Test() {
		final String query = "SELECT a.id, f.value AS item_value FROM events a, FLATTEN(input => a.payload) f WHERE a.active = 1; SELECT b.id FROM orders b; SELECT a.id, f.value AS item_value FROM events a, FLATTEN(input => a.payload) f WHERE a.active = 1;";
		final SQLSelectParserParser parser = parse(query);
		final org.antlr.v4.runtime.ParserRuleContext tree = parser.script();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 2,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_flatten_table_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SCRIPT={1={select={1={column={name=id, table_ref=a}}, 2={column={name=value, table_ref=f}, alias=item_value}}, from={join={1={table={alias=a, table=events}}, 2={table={alias=f, table_function={function_name=FLATTEN, parameters={input={column={name=payload, table_ref=a}}}}}}}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}, 2={select={1={column={name=id, table_ref=b}}}, from={table={alias=b, table=orders}}}, 3={select={1={column={name=id, table_ref=a}}, 2={column={name=value, table_ref=f}, alias=item_value}}, from={join={1={table={alias=a, table=events}}, 2={table={alias=f, table_function={function_name=FLATTEN, parameters={input={column={name=payload, table_ref=a}}}}}}}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}}",
				"[]",
				"{SCRIPT={1={}, 2={}, 3={}}}",
				"{SCRIPT={1={flatten0={value=[[@5,13:13='f',<459>,1:13]]}, events={payload=[[@18,67:67='a',<459>,1:67]], active=[[@24,86:86='a',<459>,1:86]], id=[[@1,7:7='a',<459>,1:7]]}}, 2={orders={id=[[@31,107:107='b',<459>,1:107]]}}, 3={flatten1={value=[[@43,140:140='f',<459>,1:140]]}, events={payload=[[@56,194:194='a',<459>,1:194]], active=[[@62,213:213='a',<459>,1:213]], id=[[@39,134:134='a',<459>,1:134]]}}}}",
				"{SCRIPT={1={query0={item_value=[[@9,24:33='item_value',<459>,1:24]], id=[[@3,9:10='id',<459>,1:9]]}}, 2={query0={id=[[@33,109:110='id',<459>,1:109]]}}, 3={query0={item_value=[[@47,151:160='item_value',<459>,1:151]], id=[[@41,136:137='id',<459>,1:136]]}}}}",
				"{SCRIPT={1={def_query0={query_dictionary={id=[[@3,9:10='id',<459>,1:9]], item_value=[[@9,24:33='item_value',<459>,1:24]]}, table_dictionary={flatten0={value=[[@5,13:13='f',<459>,1:13]]}, events={payload=[[@18,67:67='a',<459>,1:67]], active=[[@24,86:86='a',<459>,1:86]], id=[[@1,7:7='a',<459>,1:7]]}}, filters=[{name=active, table_ref=a}], interface={id=[{name=id, table_ref=a}], item_value=[{name=value, table_ref=f}]}, table_alias={a=events, f=flatten0}}}, 2={def_query0={query_dictionary={id=[[@33,109:110='id',<459>,1:109]]}, table_dictionary={orders={id=[[@31,107:107='b',<459>,1:107]]}}, interface={id=[{name=id, table_ref=b}]}, table_alias={b=orders}}}, 3={def_query0={query_dictionary={id=[[@41,136:137='id',<459>,1:136]], item_value=[[@47,151:160='item_value',<459>,1:151]]}, table_dictionary={flatten1={value=[[@43,140:140='f',<459>,1:140]]}, events={payload=[[@56,194:194='a',<459>,1:194]], active=[[@62,213:213='a',<459>,1:213]], id=[[@39,134:134='a',<459>,1:134]]}}, filters=[{name=active, table_ref=a}], interface={id=[{name=id, table_ref=a}], item_value=[{name=value, table_ref=f}]}, table_alias={a=events, f=flatten1}}}}}");
		final access.SqlParserAccess accessParser = new access.SqlParserAccess(false, false, false);
		accessParser.executeTheParse(query, "SCRIPT");
		Assert.assertEquals("Access syntax errors", 0, accessParser.getParser().getNumberOfSyntaxErrors());
		Assert.assertEquals("Access diagnostics are wrong", Arrays.<ParseDiagnostic>asList(), accessParser.getAllDiagnostics());
		Assert.assertEquals("Access AST is wrong", "{SCRIPT={1={select={1={column={name=id, table_ref=a}}, 2={column={name=value, table_ref=f}, alias=item_value}}, from={join={1={table={alias=a, table=events}}, 2={table={alias=f, table_function={function_name=FLATTEN, parameters={input={column={name=payload, table_ref=a}}}}}}}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}, 2={select={1={column={name=id, table_ref=b}}}, from={table={alias=b, table=orders}}}, 3={select={1={column={name=id, table_ref=a}}, 2={column={name=value, table_ref=f}, alias=item_value}}, from={join={1={table={alias=a, table=events}}, 2={table={alias=f, table_function={function_name=FLATTEN, parameters={input={column={name=payload, table_ref=a}}}}}}}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}}", accessParser.getSnippet().getSqlAbstractTree().toString());
		Assert.assertEquals("Access Interface is wrong", "[]", accessParser.getSnippet().getQueryInterface().toString());
		Assert.assertEquals("Access Substitution List is wrong", "{SCRIPT={1={}, 2={}, 3={}}}", accessParser.getSnippet().getSubstitutionsMap().toString());
		Assert.assertEquals("Access Table Dictionary is wrong", "{SCRIPT={1={flatten0={value=[[@5,13:13='f',<459>,1:13]]}, events={payload=[[@18,67:67='a',<459>,1:67]], active=[[@24,86:86='a',<459>,1:86]], id=[[@1,7:7='a',<459>,1:7]]}}, 2={orders={id=[[@31,107:107='b',<459>,1:107]]}}, 3={flatten1={value=[[@43,140:140='f',<459>,1:140]]}, events={payload=[[@56,194:194='a',<459>,1:194]], active=[[@62,213:213='a',<459>,1:213]], id=[[@39,134:134='a',<459>,1:134]]}}}}", accessParser.getSnippet().getTableDictionary().toString());
		Assert.assertEquals("Access Query Column Dictionary is wrong", "{SCRIPT={1={query0={item_value=[[@9,24:33='item_value',<459>,1:24]], id=[[@3,9:10='id',<459>,1:9]]}}, 2={query0={id=[[@33,109:110='id',<459>,1:109]]}}, 3={query0={item_value=[[@47,151:160='item_value',<459>,1:151]], id=[[@41,136:137='id',<459>,1:136]]}}}}", accessParser.getSnippet().getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Access Symbol Table is wrong", "{SCRIPT={1={def_query0={query_dictionary={id=[[@3,9:10='id',<459>,1:9]], item_value=[[@9,24:33='item_value',<459>,1:24]]}, table_dictionary={flatten0={value=[[@5,13:13='f',<459>,1:13]]}, events={payload=[[@18,67:67='a',<459>,1:67]], active=[[@24,86:86='a',<459>,1:86]], id=[[@1,7:7='a',<459>,1:7]]}}, filters=[{name=active, table_ref=a}], interface={id=[{name=id, table_ref=a}], item_value=[{name=value, table_ref=f}]}, table_alias={a=events, f=flatten0}}}, 2={def_query0={query_dictionary={id=[[@33,109:110='id',<459>,1:109]]}, table_dictionary={orders={id=[[@31,107:107='b',<459>,1:107]]}}, interface={id=[{name=id, table_ref=b}]}, table_alias={b=orders}}}, 3={def_query0={query_dictionary={id=[[@41,136:137='id',<459>,1:136]], item_value=[[@47,151:160='item_value',<459>,1:151]]}, table_dictionary={flatten1={value=[[@43,140:140='f',<459>,1:140]]}, events={payload=[[@56,194:194='a',<459>,1:194]], active=[[@62,213:213='a',<459>,1:213]], id=[[@39,134:134='a',<459>,1:134]]}}, filters=[{name=active, table_ref=a}], interface={id=[{name=id, table_ref=a}], item_value=[{name=value, table_ref=f}]}, table_alias={a=events, f=flatten1}}}}}", accessParser.getSnippet().getSymbolTable().toString());
	}
}
