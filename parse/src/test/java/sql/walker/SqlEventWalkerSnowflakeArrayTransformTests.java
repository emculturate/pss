package sql.walker;

import java.util.Arrays;

import org.antlr.v4.runtime.tree.Trees;
import org.junit.Assert;
import org.junit.Test;

import access.Snippet;
import errorhandling.ParseDiagnostic;
import sql.SQLSelectParserParser;

public class SqlEventWalkerSnowflakeArrayTransformTests extends AbstractSqlParseEventWalkerTest {

	@Test
	public void arC41_arrayRepeatSelectPlacementV0Test() {
		runDedicatedFunctionQuery(
				"SELECT a.id, array_repeat(a.item, a.repeat_count) AS repeated FROM events a WHERE a.active = 1",
				SQLSelectParserParser.RULE_array_transform_function);
	}

	private void assertExactDiagnostics(SqlParseEventWalker extractor, ParseDiagnostic... expected) {
		Assert.assertEquals("Diagnostics are wrong",
				Arrays.asList(expected), extractor.getSnippet().getParserDiagnosticList());
	}

	private SqlParseEventWalker runQuery(String query) {
		final SQLSelectParserParser parser = parse(query);
		return runParsertest(query, parser);
	}

	private SqlParseEventWalker runUnaryQuery(String query) {
		return runDedicatedFunctionQuery(query, SQLSelectParserParser.RULE_array_unary_transform_function);
	}

	private SqlParseEventWalker runObjectConversionQuery(String query) {
		return runDedicatedFunctionQuery(query, SQLSelectParserParser.RULE_array_conversion_seed_function);
	}

	private SqlParseEventWalker runDedicatedFunctionQuery(String query, int ruleIndex) {
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals(
				"Expected dedicated array function routing",
				1,
				Trees.findAllRuleNodes(tree, ruleIndex).size());
		return runAnyParsertest(query, parser, tree, true);
	}

	private void assertOutputs(
			SqlParseEventWalker extractor,
			String ast,
			String outputInterface,
			String substitutions,
			String tableDictionary,
			String queryDictionary,
			String symbolTable) {
		Assert.assertEquals("AST is wrong", ast, extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", outputInterface, extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", substitutions, extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", tableDictionary, extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", queryDictionary, extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", symbolTable, extractor.getSymbolTable().toString());
	}

	private void assertSingleMissingAliasFatal(SqlParseEventWalker extractor) {
		Assert.assertEquals("Expected one fatal walker message", 1, extractor.getSnippet().getFatalErrorStringList().size());
		Assert.assertTrue(
				"Expected missing alias fatal for z",
				extractor.getSnippet().getFatalErrorStringList().get(0).contains("No alias or table called 'z'"));
	}

	@Test
	public void snowflakeArrayToStringSelectPlacementV0Test() {
		final String query = "SELECT array_to_string(a.tags, a.sep) AS joined_tags FROM tab1 a";
		final SqlParseEventWalker extractor = runQuery(query);
		final Snippet snippet = extractor.getSnippet();
		assertDiagnosticAtPosition(
				snippet,
				"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
				ParseDiagnostic.Severity.WARNING,
				"routine 'array_to_string' at (l:1 c:7) is recognized as Snowflake-specific grammar.",
				null,
				1,
				7);
		assertNoFatalErrors(extractor);
		assertOutputs(
				extractor,
				"{SQL={select={1={function={function_name=array_to_string, parameters={1={column={name=tags, table_ref=a}}, 2={column={name=sep, table_ref=a}}}}, alias=joined_tags}}, from={table={alias=a, table=tab1}}}}",
				"[joined_tags]",
				"{}",
				"{tab1={tags=[[@3,23:23='a',<455>,1:23]], sep=[[@7,31:31='a',<455>,1:31]]}}",
				"{query0={joined_tags=[[@12,41:51='joined_tags',<455>,1:41]]}}",
				"{def_query0={query_dictionary={joined_tags=[[@12,41:51='joined_tags',<455>,1:41]]}, table_dictionary={tab1={tags=[[@3,23:23='a',<455>,1:23]], sep=[[@7,31:31='a',<455>,1:31]]}}, interface={joined_tags=[{name=tags, table_ref=a}, {name=sep, table_ref=a}]}, table_alias={a=tab1}}}");
	}

	@Test
	public void snowflakeArrayToStringSecondOperandMissingAliasFatalV0Test() {
		final String query = "SELECT array_to_string(a.tags, z.sep) AS joined_tags FROM tab1 a";
		final SqlParseEventWalker extractor = runQuery(query);
		final Snippet snippet = extractor.getSnippet();
		assertDiagnosticAtPosition(
				snippet,
				"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
				ParseDiagnostic.Severity.WARNING,
				"routine 'array_to_string' at (l:1 c:7) is recognized as Snowflake-specific grammar.",
				null,
				1,
				7);
		Assert.assertEquals("Expected one fatal walker message", 1, extractor.getSnippet().getFatalErrorStringList().size());
		Assert.assertTrue(
				"Expected missing alias fatal for z",
				extractor.getSnippet().getFatalErrorStringList().get(0).contains("No alias or table called 'z'"));
		assertOutputs(
				extractor,
				"{SQL={select={1={function={function_name=array_to_string, parameters={1={column={name=tags, table_ref=a}}, 2={column={name=sep, table_ref=z}}}}, alias=joined_tags}}, from={table={alias=a, table=tab1}}}}",
				"[joined_tags]",
				"{}",
				"{tab1={tags=[[@3,23:23='a',<455>,1:23]]}}",
				"{query0={joined_tags=[[@12,41:51='joined_tags',<455>,1:41]]}}",
				"{def_query0={query_dictionary={joined_tags=[[@12,41:51='joined_tags',<455>,1:41]]}, table_dictionary={tab1={tags=[[@3,23:23='a',<455>,1:23]]}}, interface={joined_tags=[{name=tags, table_ref=a}, {name=sep, table_ref=z}]}, table_alias={a=tab1}}}");
	}

	@Test
	public void snowflakeArrayToStringQualifiedColumnVariablesV0Test() {
		final String query = "SELECT array_to_string(a.<arr_col>, a.<sep_col>) AS joined_tags FROM tab1 a";
		final SqlParseEventWalker extractor = runQuery(query);
		final Snippet snippet = extractor.getSnippet();
		assertDiagnosticAtPosition(
				snippet,
				"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
				ParseDiagnostic.Severity.WARNING,
				"routine 'array_to_string' at (l:1 c:7) is recognized as Snowflake-specific grammar.",
				null,
				1,
				7);
		assertNoFatalErrors(extractor);
		assertOutputs(
				extractor,
				"{SQL={select={1={function={function_name=array_to_string, parameters={1={column={substitution={name=<arr_col>, type=column}, table_ref=a}}, 2={column={substitution={name=<sep_col>, type=column}, table_ref=a}}}}, alias=joined_tags}}, from={table={alias=a, table=tab1}}}}",
				"[joined_tags]",
				"{<sep_col>=column, <arr_col>=column}",
				"{tab1={<sep_col>=[[@7,36:36='a',<455>,1:36]], <arr_col>=[[@3,23:23='a',<455>,1:23]]}}",
				"{query0={joined_tags=[[@12,52:62='joined_tags',<455>,1:52]]}}",
				"{def_query0={query_dictionary={joined_tags=[[@12,52:62='joined_tags',<455>,1:52]]}, table_dictionary={tab1={<sep_col>=[[@7,36:36='a',<455>,1:36]], <arr_col>=[[@3,23:23='a',<455>,1:23]]}}, interface={joined_tags=[{substitution={name=<arr_col>, type=column}, table_ref=a}, {substitution={name=<sep_col>, type=column}, table_ref=a}]}, table_alias={a=tab1}}}");
	}

	@Test
	public void snowflakeArrayToStringFirstOperandMissingAliasFatalV0Test() {
		final String query = "SELECT array_to_string(z.tags, a.sep) AS joined_tags FROM tab1 a";
		final SqlParseEventWalker extractor = runQuery(query);
		final Snippet snippet = extractor.getSnippet();
		assertDiagnosticAtPosition(
				snippet,
				"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
				ParseDiagnostic.Severity.WARNING,
				"routine 'array_to_string' at (l:1 c:7) is recognized as Snowflake-specific grammar.",
				null,
				1,
				7);
		assertSingleMissingAliasFatal(extractor);
		assertOutputs(
				extractor,
				"{SQL={select={1={function={function_name=array_to_string, parameters={1={column={name=tags, table_ref=z}}, 2={column={name=sep, table_ref=a}}}}, alias=joined_tags}}, from={table={alias=a, table=tab1}}}}",
				"[joined_tags]",
				"{}",
				"{tab1={sep=[[@7,31:31='a',<455>,1:31]]}}",
				"{query0={joined_tags=[[@12,41:51='joined_tags',<455>,1:41]]}}",
				"{def_query0={query_dictionary={joined_tags=[[@12,41:51='joined_tags',<455>,1:41]]}, table_dictionary={tab1={sep=[[@7,31:31='a',<455>,1:31]]}}, interface={joined_tags=[{name=tags, table_ref=z}, {name=sep, table_ref=a}]}, table_alias={a=tab1}}}");
	}

	@Test
	public void snowflakeArrayToStringWherePlacementV0Test() {
		final String query = "SELECT a.id FROM tab1 a WHERE array_to_string(a.tags, a.sep) IS NOT NULL";
		final SqlParseEventWalker extractor = runQuery(query);
		final Snippet snippet = extractor.getSnippet();
		assertDiagnosticAtPosition(
				snippet,
				"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
				ParseDiagnostic.Severity.WARNING,
				"routine 'array_to_string' at (l:1 c:30) is recognized as Snowflake-specific grammar.",
				null,
				1,
				30);
		assertNoFatalErrors(extractor);
		assertOutputs(
				extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}}, from={table={alias=a, table=tab1}}, where={condition={left={function={function_name=array_to_string, parameters={1={column={name=tags, table_ref=a}}, 2={column={name=sep, table_ref=a}}}}}, operator=IS NOT NULL}}}}",
				"[id]",
				"{}",
				"{tab1={id=[[@1,7:7='a',<455>,1:7]], tags=[[@10,46:46='a',<455>,1:46]], sep=[[@14,54:54='a',<455>,1:54]]}}",
				"{query0={id=[[@3,9:10='id',<455>,1:9]]}}",
				"{def_query0={query_dictionary={id=[[@3,9:10='id',<455>,1:9]]}, table_dictionary={tab1={id=[[@1,7:7='a',<455>,1:7]], tags=[[@10,46:46='a',<455>,1:46]], sep=[[@14,54:54='a',<455>,1:54]]}}, filters=[{name=tags, table_ref=a}, {name=sep, table_ref=a}], interface={id=[{name=id, table_ref=a}]}, table_alias={a=tab1}}}");
	}

	@Test
	public void snowflakeArrayToStringAmbiguousUnqualifiedDiagnosticV0Test() {
		final String query =
				"SELECT array_to_string(tags, ',') AS joined_tags "
						+ "FROM tab1 a JOIN tab2 b ON a.id = b.id";
		final SqlParseEventWalker extractor = runQuery(query);
		final Snippet snippet = extractor.getSnippet();
		assertDiagnosticCountBySeverity(
				snippet,
				"AMBIGUOUS_COLUMN_REFERENCE",
				ParseDiagnostic.Severity.SEVERE_WARNING,
				null,
				"tags",
				1);
		assertOutputs(
				extractor,
				"{SQL={select={1={function={function_name=array_to_string, parameters={1={column={name=tags, table_ref=null}}, 2={literal=','}}}, alias=joined_tags}}, from={join={1={table={alias=a, table=tab1}}, 2={join=JOIN, on={condition={left={column={name=id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=tab2}}}}}}",
				"[joined_tags]",
				"{}",
				"{tab1={id=[[@16,76:76='a',<455>,1:76]]}, tab2={id=[[@20,83:83='b',<455>,1:83]]}}",
				"{query0={joined_tags=[[@8,37:47='joined_tags',<455>,1:37]]}}",
				"{def_query0={query_dictionary={joined_tags=[[@8,37:47='joined_tags',<455>,1:37]]}, table_dictionary={tab1={id=[[@16,76:76='a',<455>,1:76]]}, tab2={id=[[@20,83:83='b',<455>,1:83]]}}, filters=[{name=id, table_ref=a}, {name=id, table_ref=b}], interface={joined_tags=[{name=tags, table_ref=null}]}, table_alias={a=tab1, b=tab2}}}");
	}

	@Test
	public void snowflakeArrayToStringWithPredicandSubstitutionV0Test() {
		final String query = "SELECT array_to_string(a.tags, <sep_expr>) AS joined_tags FROM tab1 a";
		final SqlParseEventWalker extractor = runQuery(query);
		final Snippet snippet = extractor.getSnippet();
		assertDiagnosticAtPosition(
				snippet,
				"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
				ParseDiagnostic.Severity.WARNING,
				"routine 'array_to_string' at (l:1 c:7) is recognized as Snowflake-specific grammar.",
				null,
				1,
				7);
		assertNoFatalErrors(extractor);
		assertOutputs(
				extractor,
				"{SQL={select={1={function={function_name=array_to_string, parameters={1={column={name=tags, table_ref=a}}, 2={substitution={name=<sep_expr>, type=predicand}}}}, alias=joined_tags}}, from={table={alias=a, table=tab1}}}}",
				"[joined_tags]",
				"{<sep_expr>=predicand}",
				"{tab1={tags=[[@3,23:23='a',<455>,1:23]]}}",
				"{query0={joined_tags=[[@10,46:56='joined_tags',<455>,1:46]]}}",
				"{def_query0={query_dictionary={joined_tags=[[@10,46:56='joined_tags',<455>,1:46]]}, table_dictionary={tab1={tags=[[@3,23:23='a',<455>,1:23]]}}, interface={joined_tags=[{name=tags, table_ref=a}, {name=<sep_expr>, type=predicand}]}, table_alias={a=tab1}}}");
	}

	@Test
	public void snowflakeArrayToStringScriptStatementScopedWarningV0Test() {
		final String query =
				"SELECT array_to_string(a.tags, a.sep) AS joined_tags FROM tab1 a; "
						+ "SELECT a.id FROM tab1 a;";
		final SQLSelectParserParser parser = parse(query);
		final SqlParseEventWalker extractor = runScriptParsertest(query, parser);
		final Snippet snippet = extractor.getSnippet();
		assertDiagnosticCountBySeverity(
				snippet,
				"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
				ParseDiagnostic.Severity.WARNING,
				null,
				null,
				1);
		assertDiagnosticAtPosition(
				snippet,
				"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
				ParseDiagnostic.Severity.WARNING,
				"routine 'array_to_string' at (l:1 c:7) is recognized as Snowflake-specific grammar.",
				null,
				1,
				7);
		assertNoFatalErrors(extractor);
		assertOutputs(
				extractor,
				"{SCRIPT={1={select={1={function={function_name=array_to_string, parameters={1={column={name=tags, table_ref=a}}, 2={column={name=sep, table_ref=a}}}}, alias=joined_tags}}, from={table={alias=a, table=tab1}}}, 2={select={1={column={name=id, table_ref=a}}}, from={table={alias=a, table=tab1}}}}}",
				"[]",
				"{SCRIPT={1={}, 2={}}}",
				"{SCRIPT={1={tab1={tags=[[@3,23:23='a',<455>,1:23]], sep=[[@7,31:31='a',<455>,1:31]]}}, 2={tab1={id=[[@18,73:73='a',<455>,1:73]]}}}}",
				"{SCRIPT={1={query0={joined_tags=[[@12,41:51='joined_tags',<455>,1:41]]}}, 2={query0={id=[[@20,75:76='id',<455>,1:75]]}}}}",
				"{SCRIPT={1={def_query0={query_dictionary={joined_tags=[[@12,41:51='joined_tags',<455>,1:41]]}, table_dictionary={tab1={tags=[[@3,23:23='a',<455>,1:23]], sep=[[@7,31:31='a',<455>,1:31]]}}, interface={joined_tags=[{name=tags, table_ref=a}, {name=sep, table_ref=a}]}, table_alias={a=tab1}}}, 2={def_query0={query_dictionary={id=[[@20,75:76='id',<455>,1:75]]}, table_dictionary={tab1={id=[[@18,73:73='a',<455>,1:73]]}}, interface={id=[{name=id, table_ref=a}]}, table_alias={a=tab1}}}}}");
	}

	@Test
	public void snowflakeArrayToStringCreateViewDdlPlacementV0Test() {
		final String query = "CREATE VIEW v_arr AS SELECT array_to_string(a.tags, a.sep) AS joined_tags FROM tab1 a";
		final SQLSelectParserParser parser = parse(query);
		final SqlParseEventWalker extractor = runDdlParsertest(query, parser);
		final Snippet snippet = extractor.getSnippet();
		assertDiagnosticAtPosition(
				snippet,
				"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
				ParseDiagnostic.Severity.WARNING,
				"routine 'array_to_string' at (l:1 c:28) is recognized as Snowflake-specific grammar.",
				null,
				1,
				28);
		assertNoFatalErrors(extractor);
		assertOutputs(
				extractor,
				"{DDL={create={type=VIEW, name={table=v_arr}, query={select={1={function={function_name=array_to_string, parameters={1={column={name=tags, table_ref=a}}, 2={column={name=sep, table_ref=a}}}}, alias=joined_tags}}, from={table={alias=a, table=tab1}}}}}}",
				"[]",
				"{}",
				"{tab1={tags=[[@7,44:44='a',<455>,1:44]], sep=[[@11,52:52='a',<455>,1:52]]}}",
				"{query0={joined_tags=[[@16,62:72='joined_tags',<455>,1:62]]}}",
				"{def_create1={def_query0={query_dictionary={joined_tags=[[@16,62:72='joined_tags',<455>,1:62]]}, table_dictionary={tab1={tags=[[@7,44:44='a',<455>,1:44]], sep=[[@11,52:52='a',<455>,1:52]]}}, interface={joined_tags=[{name=tags, table_ref=a}, {name=sep, table_ref=a}]}, table_alias={a=tab1}}}}");
	}

	@Test
	public void snowflakeArraySliceSelectPlacementV0Test() {
		final String query = "SELECT array_slice(a.tags, a.start_idx, a.end_idx) AS sliced_tags FROM tab1 a";
		final SqlParseEventWalker extractor = runQuery(query);
		final Snippet snippet = extractor.getSnippet();
		assertDiagnosticAtPosition(
				snippet,
				"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
				ParseDiagnostic.Severity.WARNING,
				"routine 'array_slice' at (l:1 c:7) is recognized as Snowflake-specific grammar.",
				null,
				1,
				7);
		assertNoFatalErrors(extractor);
		assertOutputs(
				extractor,
				"{SQL={select={1={function={function_name=array_slice, parameters={1={column={name=tags, table_ref=a}}, 2={column={name=start_idx, table_ref=a}}, 3={column={name=end_idx, table_ref=a}}}}, alias=sliced_tags}}, from={table={alias=a, table=tab1}}}}",
				"[sliced_tags]",
				"{}",
				"{tab1={end_idx=[[@11,40:40='a',<455>,1:40]], start_idx=[[@7,27:27='a',<455>,1:27]], tags=[[@3,19:19='a',<455>,1:19]]}}",
				"{query0={sliced_tags=[[@16,54:64='sliced_tags',<455>,1:54]]}}",
				"{def_query0={query_dictionary={sliced_tags=[[@16,54:64='sliced_tags',<455>,1:54]]}, table_dictionary={tab1={end_idx=[[@11,40:40='a',<455>,1:40]], start_idx=[[@7,27:27='a',<455>,1:27]], tags=[[@3,19:19='a',<455>,1:19]]}}, interface={sliced_tags=[{name=tags, table_ref=a}, {name=start_idx, table_ref=a}, {name=end_idx, table_ref=a}]}, table_alias={a=tab1}}}");
	}

	@Test
	public void snowflakeArraySliceThirdOperandMissingAliasFatalV0Test() {
		final String query = "SELECT array_slice(a.tags, a.start_idx, z.end_idx) AS sliced_tags FROM tab1 a";
		final SqlParseEventWalker extractor = runQuery(query);
		final Snippet snippet = extractor.getSnippet();
		assertDiagnosticAtPosition(
				snippet,
				"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
				ParseDiagnostic.Severity.WARNING,
				"routine 'array_slice' at (l:1 c:7) is recognized as Snowflake-specific grammar.",
				null,
				1,
				7);
		assertSingleMissingAliasFatal(extractor);
		assertOutputs(
				extractor,
				"{SQL={select={1={function={function_name=array_slice, parameters={1={column={name=tags, table_ref=a}}, 2={column={name=start_idx, table_ref=a}}, 3={column={name=end_idx, table_ref=z}}}}, alias=sliced_tags}}, from={table={alias=a, table=tab1}}}}",
				"[sliced_tags]",
				"{}",
				"{tab1={start_idx=[[@7,27:27='a',<455>,1:27]], tags=[[@3,19:19='a',<455>,1:19]]}}",
				"{query0={sliced_tags=[[@16,54:64='sliced_tags',<455>,1:54]]}}",
				"{def_query0={query_dictionary={sliced_tags=[[@16,54:64='sliced_tags',<455>,1:54]]}, table_dictionary={tab1={start_idx=[[@7,27:27='a',<455>,1:27]], tags=[[@3,19:19='a',<455>,1:19]]}}, interface={sliced_tags=[{name=tags, table_ref=a}, {name=start_idx, table_ref=a}, {name=end_idx, table_ref=z}]}, table_alias={a=tab1}}}");
	}

	@Test
	public void snowflakeArraySliceQualifiedColumnVariablesV0Test() {
		final String query = "SELECT array_slice(a.<arr_col>, a.<start_col>, a.<end_col>) AS sliced_tags FROM tab1 a";
		final SqlParseEventWalker extractor = runQuery(query);
		final Snippet snippet = extractor.getSnippet();
		assertDiagnosticAtPosition(
				snippet,
				"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
				ParseDiagnostic.Severity.WARNING,
				"routine 'array_slice' at (l:1 c:7) is recognized as Snowflake-specific grammar.",
				null,
				1,
				7);
		assertNoFatalErrors(extractor);
		assertOutputs(
				extractor,
				"{SQL={select={1={function={function_name=array_slice, parameters={1={column={substitution={name=<arr_col>, type=column}, table_ref=a}}, 2={column={substitution={name=<start_col>, type=column}, table_ref=a}}, 3={column={substitution={name=<end_col>, type=column}, table_ref=a}}}}, alias=sliced_tags}}, from={table={alias=a, table=tab1}}}}",
				"[sliced_tags]",
				"{<end_col>=column, <start_col>=column, <arr_col>=column}",
				"{tab1={<end_col>=[[@11,47:47='a',<455>,1:47]], <start_col>=[[@7,32:32='a',<455>,1:32]], <arr_col>=[[@3,19:19='a',<455>,1:19]]}}",
				"{query0={sliced_tags=[[@16,63:73='sliced_tags',<455>,1:63]]}}",
				"{def_query0={query_dictionary={sliced_tags=[[@16,63:73='sliced_tags',<455>,1:63]]}, table_dictionary={tab1={<end_col>=[[@11,47:47='a',<455>,1:47]], <start_col>=[[@7,32:32='a',<455>,1:32]], <arr_col>=[[@3,19:19='a',<455>,1:19]]}}, interface={sliced_tags=[{substitution={name=<arr_col>, type=column}, table_ref=a}, {substitution={name=<start_col>, type=column}, table_ref=a}, {substitution={name=<end_col>, type=column}, table_ref=a}]}, table_alias={a=tab1}}}");
	}

	@Test
	public void snowflakeArraySliceFirstOperandMissingAliasFatalV0Test() {
		final String query = "SELECT array_slice(z.tags, a.start_idx, a.end_idx) AS sliced_tags FROM tab1 a";
		final SqlParseEventWalker extractor = runQuery(query);
		final Snippet snippet = extractor.getSnippet();
		assertDiagnosticAtPosition(
				snippet,
				"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
				ParseDiagnostic.Severity.WARNING,
				"routine 'array_slice' at (l:1 c:7) is recognized as Snowflake-specific grammar.",
				null,
				1,
				7);
		assertSingleMissingAliasFatal(extractor);
		assertOutputs(
				extractor,
				"{SQL={select={1={function={function_name=array_slice, parameters={1={column={name=tags, table_ref=z}}, 2={column={name=start_idx, table_ref=a}}, 3={column={name=end_idx, table_ref=a}}}}, alias=sliced_tags}}, from={table={alias=a, table=tab1}}}}",
				"[sliced_tags]",
				"{}",
				"{tab1={end_idx=[[@11,40:40='a',<455>,1:40]], start_idx=[[@7,27:27='a',<455>,1:27]]}}",
				"{query0={sliced_tags=[[@16,54:64='sliced_tags',<455>,1:54]]}}",
				"{def_query0={query_dictionary={sliced_tags=[[@16,54:64='sliced_tags',<455>,1:54]]}, table_dictionary={tab1={end_idx=[[@11,40:40='a',<455>,1:40]], start_idx=[[@7,27:27='a',<455>,1:27]]}}, interface={sliced_tags=[{name=tags, table_ref=z}, {name=start_idx, table_ref=a}, {name=end_idx, table_ref=a}]}, table_alias={a=tab1}}}");
	}

	@Test
	public void snowflakeArraySliceSecondOperandMissingAliasFatalV0Test() {
		final String query = "SELECT array_slice(a.tags, z.start_idx, a.end_idx) AS sliced_tags FROM tab1 a";
		final SqlParseEventWalker extractor = runQuery(query);
		final Snippet snippet = extractor.getSnippet();
		assertDiagnosticAtPosition(
				snippet,
				"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
				ParseDiagnostic.Severity.WARNING,
				"routine 'array_slice' at (l:1 c:7) is recognized as Snowflake-specific grammar.",
				null,
				1,
				7);
		assertSingleMissingAliasFatal(extractor);
		assertOutputs(
				extractor,
				"{SQL={select={1={function={function_name=array_slice, parameters={1={column={name=tags, table_ref=a}}, 2={column={name=start_idx, table_ref=z}}, 3={column={name=end_idx, table_ref=a}}}}, alias=sliced_tags}}, from={table={alias=a, table=tab1}}}}",
				"[sliced_tags]",
				"{}",
				"{tab1={end_idx=[[@11,40:40='a',<455>,1:40]], tags=[[@3,19:19='a',<455>,1:19]]}}",
				"{query0={sliced_tags=[[@16,54:64='sliced_tags',<455>,1:54]]}}",
				"{def_query0={query_dictionary={sliced_tags=[[@16,54:64='sliced_tags',<455>,1:54]]}, table_dictionary={tab1={end_idx=[[@11,40:40='a',<455>,1:40]], tags=[[@3,19:19='a',<455>,1:19]]}}, interface={sliced_tags=[{name=tags, table_ref=a}, {name=start_idx, table_ref=z}, {name=end_idx, table_ref=a}]}, table_alias={a=tab1}}}");
	}

	@Test
	public void snowflakeArraySliceAmbiguousUnqualifiedDiagnosticV0Test() {
		final String query =
				"SELECT array_slice(tags, tags, tags) AS sliced_tags "
						+ "FROM tab1 a JOIN tab2 b ON a.id = b.id";
		final SqlParseEventWalker extractor = runQuery(query);
		final Snippet snippet = extractor.getSnippet();
		assertDiagnosticCountBySeverity(
				snippet,
				"AMBIGUOUS_COLUMN_REFERENCE",
				ParseDiagnostic.Severity.SEVERE_WARNING,
				null,
				"tags",
				1);
		assertOutputs(
				extractor,
				"{SQL={select={1={function={function_name=array_slice, parameters={1={column={name=tags, table_ref=null}}, 2={column={name=tags, table_ref=null}}, 3={column={name=tags, table_ref=null}}}}, alias=sliced_tags}}, from={join={1={table={alias=a, table=tab1}}, 2={join=JOIN, on={condition={left={column={name=id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=tab2}}}}}}",
				"[sliced_tags]",
				"{}",
				"{tab1={id=[[@18,79:79='a',<455>,1:79]]}, tab2={id=[[@22,86:86='b',<455>,1:86]]}}",
				"{query0={sliced_tags=[[@10,40:50='sliced_tags',<455>,1:40]]}}",
				"{def_query0={query_dictionary={sliced_tags=[[@10,40:50='sliced_tags',<455>,1:40]]}, table_dictionary={tab1={id=[[@18,79:79='a',<455>,1:79]]}, tab2={id=[[@22,86:86='b',<455>,1:86]]}}, filters=[{name=id, table_ref=a}, {name=id, table_ref=b}], interface={sliced_tags=[{name=tags, table_ref=null}]}, table_alias={a=tab1, b=tab2}}}");
	}

	@Test
	public void snowflakeArraySliceHavingPlacementV0Test() {
		final String query =
				"SELECT a.id, count(*) AS c FROM tab1 a GROUP BY a.id "
						+ "HAVING array_slice(a.tags, a.start_idx, a.end_idx) IS NOT NULL";
		final SqlParseEventWalker extractor = runQuery(query);
		final Snippet snippet = extractor.getSnippet();
		assertDiagnosticAtPosition(
				snippet,
				"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
				ParseDiagnostic.Severity.WARNING,
				"routine 'array_slice' at (l:1 c:60) is recognized as Snowflake-specific grammar.",
				null,
				1,
				60);
		assertNoFatalErrors(extractor);
		assertOutputs(
				extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={function_name=COUNT, qualifier=null, parameters=*}, alias=c}}, having={condition={left={function={function_name=array_slice, parameters={1={column={name=tags, table_ref=a}}, 2={column={name=start_idx, table_ref=a}}, 3={column={name=end_idx, table_ref=a}}}}}, operator=IS NOT NULL}}, from={table={alias=a, table=tab1}}, groupby={1={column={name=id, table_ref=a}}}}}",
				"[c, id]",
				"{}",
				"{tab1={end_idx=[[@30,93:93='a',<455>,1:93]], start_idx=[[@26,80:80='a',<455>,1:80]], *=[[@7,19:19='*',<291>,1:19]], id=[[@1,7:7='a',<455>,1:7], [@16,48:48='a',<455>,1:48]], tags=[[@22,72:72='a',<455>,1:72]]}}",
				"{query0={c=[[@10,25:25='c',<455>,1:25]], id=[[@3,9:10='id',<455>,1:9]]}}",
				"{def_query0={query_dictionary={c=[[@10,25:25='c',<455>,1:25]], id=[[@3,9:10='id',<455>,1:9]]}, table_dictionary={tab1={end_idx=[[@30,93:93='a',<455>,1:93]], *=[[@7,19:19='*',<291>,1:19]], start_idx=[[@26,80:80='a',<455>,1:80]], id=[[@1,7:7='a',<455>,1:7], [@16,48:48='a',<455>,1:48]], tags=[[@22,72:72='a',<455>,1:72]]}}, grouped_by=[{name=id, table_ref=a}], filters=[{name=tags, table_ref=a}, {name=start_idx, table_ref=a}, {name=end_idx, table_ref=a}], interface={c=[], id=[{name=id, table_ref=a}]}, table_alias={a=tab1}}}");
	}

	@Test
	public void snowflakeArraySliceOrderByPlacementV0Test() {
		final String query = "SELECT a.id FROM tab1 a ORDER BY array_slice(a.tags, a.start_idx, a.end_idx)";
		final SqlParseEventWalker extractor = runQuery(query);
		final Snippet snippet = extractor.getSnippet();
		assertDiagnosticAtPosition(
				snippet,
				"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
				ParseDiagnostic.Severity.WARNING,
				"routine 'array_slice' at (l:1 c:33) is recognized as Snowflake-specific grammar.",
				null,
				1,
				33);
		assertNoFatalErrors(extractor);
		assertOutputs(
				extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}}, orderby={1={null_order=null, predicand={function={function_name=array_slice, parameters={1={column={name=tags, table_ref=a}}, 2={column={name=start_idx, table_ref=a}}, 3={column={name=end_idx, table_ref=a}}}}}, sort_order=ASC}}, from={table={alias=a, table=tab1}}}}",
				"[id]",
				"{}",
				"{tab1={end_idx=[[@19,66:66='a',<455>,1:66]], start_idx=[[@15,53:53='a',<455>,1:53]], id=[[@1,7:7='a',<455>,1:7]], tags=[[@11,45:45='a',<455>,1:45]]}}",
				"{query0={id=[[@3,9:10='id',<455>,1:9]]}}",
				"{def_query0={query_dictionary={id=[[@3,9:10='id',<455>,1:9]]}, table_dictionary={tab1={end_idx=[[@19,66:66='a',<455>,1:66]], start_idx=[[@15,53:53='a',<455>,1:53]], id=[[@1,7:7='a',<455>,1:7]], tags=[[@11,45:45='a',<455>,1:45]]}}, ordered_by=[{name=tags, table_ref=a}, {name=start_idx, table_ref=a}, {name=end_idx, table_ref=a}], interface={id=[{name=id, table_ref=a}]}, table_alias={a=tab1}}}");
	}

	@Test
	public void snowflakeArrayToStringQualifyPlacementV0Test() {
		final String query =
				"SELECT a.id, row_number() over (partition by a.id) AS rn FROM tab1 a "
						+ "QUALIFY array_to_string(a.tags, a.sep) IS NOT NULL";
		final SqlParseEventWalker extractor = runQuery(query);
		final Snippet snippet = extractor.getSnippet();
		assertDiagnosticAtPosition(
				snippet,
				"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
				ParseDiagnostic.Severity.WARNING,
				"routine 'array_to_string' at (l:1 c:77) is recognized as Snowflake-specific grammar.",
				null,
				1,
				77);
		assertNoFatalErrors(extractor);
		assertOutputs(
				extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={alias=rn, window_function={over={partition_by={1={column={name=id, table_ref=a}}}}, function={function_name=row_number, parameters=null}}}}, from={table={alias=a, table=tab1}}, qualify={condition={left={function={function_name=array_to_string, parameters={1={column={name=tags, table_ref=a}}, 2={column={name=sep, table_ref=a}}}}}, operator=IS NOT NULL}}}}",
				"[id, rn]",
				"{}",
				"{tab1={id=[[@1,7:7='a',<455>,1:7], [@12,45:45='a',<455>,1:45]], tags=[[@24,93:93='a',<455>,1:93]], sep=[[@28,101:101='a',<455>,1:101]]}}",
				"{query0={rn=[[@17,54:55='rn',<455>,1:54]], id=[[@3,9:10='id',<455>,1:9]]}}",
				"{def_query0={query_dictionary={id=[[@3,9:10='id',<455>,1:9]], rn=[[@17,54:55='rn',<455>,1:54]]}, table_dictionary={tab1={id=[[@1,7:7='a',<455>,1:7], [@12,45:45='a',<455>,1:45]], tags=[[@24,93:93='a',<455>,1:93]], sep=[[@28,101:101='a',<455>,1:101]]}}, window_partition_by=[{name=id, table_ref=a}], filters=[{name=tags, table_ref=a}, {name=sep, table_ref=a}], interface={id=[{name=id, table_ref=a}], rn=[{name=id, table_ref=a}]}, table_alias={a=tab1}}}");
	}

	@Test
	public void snowflakeArrayToStringOrderByPlacementV0Test() {
		final String query = "SELECT a.id FROM tab1 a ORDER BY array_to_string(a.tags, a.sep)";
		final SqlParseEventWalker extractor = runQuery(query);
		final Snippet snippet = extractor.getSnippet();
		assertDiagnosticAtPosition(
				snippet,
				"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
				ParseDiagnostic.Severity.WARNING,
				"routine 'array_to_string' at (l:1 c:33) is recognized as Snowflake-specific grammar.",
				null,
				1,
				33);
		assertNoFatalErrors(extractor);
		assertOutputs(
				extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}}, orderby={1={null_order=null, predicand={function={function_name=array_to_string, parameters={1={column={name=tags, table_ref=a}}, 2={column={name=sep, table_ref=a}}}}}, sort_order=ASC}}, from={table={alias=a, table=tab1}}}}",
				"[id]",
				"{}",
				"{tab1={id=[[@1,7:7='a',<455>,1:7]], tags=[[@11,49:49='a',<455>,1:49]], sep=[[@15,57:57='a',<455>,1:57]]}}",
				"{query0={id=[[@3,9:10='id',<455>,1:9]]}}",
				"{def_query0={query_dictionary={id=[[@3,9:10='id',<455>,1:9]]}, table_dictionary={tab1={id=[[@1,7:7='a',<455>,1:7]], tags=[[@11,49:49='a',<455>,1:49]], sep=[[@15,57:57='a',<455>,1:57]]}}, ordered_by=[{name=tags, table_ref=a}, {name=sep, table_ref=a}], interface={id=[{name=id, table_ref=a}]}, table_alias={a=tab1}}}");
	}

	@Test
	public void snowflakeArrayToStringValuesPlacementV0Test() {
		final String query = "VALUES (array_to_string('a', '|'))";
		final SqlParseEventWalker extractor = runQuery(query);
		final Snippet snippet = extractor.getSnippet();
		assertDiagnosticAtPosition(
				snippet,
				"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
				ParseDiagnostic.Severity.WARNING,
				"routine 'array_to_string' at (l:1 c:8) is recognized as Snowflake-specific grammar.",
				null,
				1,
				8);
		assertNoFatalErrors(extractor);
		assertOutputs(
				extractor,
				"{SQL={values={matrix={1={row={1={function={function_name=array_to_string, parameters={1={literal='a'}, 2={literal='|'}}}}}}}}}}",
				"[$1]",
				"{}",
				"{}",
				"{values0={$1=[[@1,7:7='(',<287>,1:7]]}}",
				"{def_values0={query_dictionary={$1=[[@1,7:7='(',<287>,1:7]]}, interface={$1=[]}}}");
	}

	@Test
	public void snowflakeArraySliceValuesPlacementV0Test() {
		final String query = "VALUES (array_slice('arr', 1, 2))";
		final SqlParseEventWalker extractor = runQuery(query);
		final Snippet snippet = extractor.getSnippet();
		assertDiagnosticAtPosition(
				snippet,
				"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
				ParseDiagnostic.Severity.WARNING,
				"routine 'array_slice' at (l:1 c:8) is recognized as Snowflake-specific grammar.",
				null,
				1,
				8);
		assertNoFatalErrors(extractor);
		assertOutputs(
				extractor,
				"{SQL={values={matrix={1={row={1={function={function_name=array_slice, parameters={1={literal='arr'}, 2={literal=1}, 3={literal=2}}}}}}}}}}",
				"[$1]",
				"{}",
				"{}",
				"{values0={$1=[[@1,7:7='(',<287>,1:7]]}}",
				"{def_values0={query_dictionary={$1=[[@1,7:7='(',<287>,1:7]]}, interface={$1=[]}}}");
	}

	@Test
	public void snowflakeArrayToStringInsertPlacementV0Test() {
		final String query = "INSERT INTO tab_out (joined_tags) SELECT array_to_string(a.tags, a.sep) FROM tab1 a";
		final SqlParseEventWalker extractor = runQuery(query);
		final Snippet snippet = extractor.getSnippet();
		assertDiagnosticAtPosition(
				snippet,
				"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
				ParseDiagnostic.Severity.WARNING,
				"routine 'array_to_string' at (l:1 c:41) is recognized as Snowflake-specific grammar.",
				null,
				1,
				41);
		assertNoFatalErrors(extractor);
		assertOutputs(
				extractor,
				"{SQL={insert={preamble=insert_into, from={from={table={alias=a, table=tab1}}, select={1={function={function_name=array_to_string, parameters={1={column={name=tags, table_ref=a}}, 2={column={name=sep, table_ref=a}}}}}}}, target_table={table={alias=null, table=tab_out}}, columns={1={column={name=joined_tags, table_ref=null}}}}}}",
				"[joined_tags]",
				"{}",
				"{tab1={tags=[[@9,57:57='a',<455>,1:57]], sep=[[@13,65:65='a',<455>,1:65]]}, tab_out={joined_tags=[[@4,21:31='joined_tags',<455>,1:21]]}}",
				"{query0={unnamed_0=[[@16,70:70=')',<288>,1:70]]}, insert1={joined_tags=[[@4,21:31='joined_tags',<455>,1:21]]}}",
				"{def_insert1={query_dictionary={joined_tags=[[@4,21:31='joined_tags',<455>,1:21]]}, table_dictionary={tab_out={joined_tags=[[@4,21:31='joined_tags',<455>,1:21]]}}, def_query0={query_dictionary={unnamed_0=[[@16,70:70=')',<288>,1:70]]}, table_dictionary={tab1={tags=[[@9,57:57='a',<455>,1:57]], sep=[[@13,65:65='a',<455>,1:65]]}}, interface={unnamed_0=[{name=tags, table_ref=a}, {name=sep, table_ref=a}]}, table_alias={a=tab1}}, interface={joined_tags=[{name=unnamed_0, table_ref=query0}]}}}");
	}

	@Test
	public void snowflakeArraySliceInsertPlacementV0Test() {
		final String query =
				"INSERT INTO tab_out (sliced_tags) SELECT array_slice(a.tags, a.start_idx, a.end_idx) FROM tab1 a";
		final SqlParseEventWalker extractor = runQuery(query);
		final Snippet snippet = extractor.getSnippet();
		assertDiagnosticAtPosition(
				snippet,
				"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
				ParseDiagnostic.Severity.WARNING,
				"routine 'array_slice' at (l:1 c:41) is recognized as Snowflake-specific grammar.",
				null,
				1,
				41);
		assertNoFatalErrors(extractor);
		assertOutputs(
				extractor,
				"{SQL={insert={preamble=insert_into, from={from={table={alias=a, table=tab1}}, select={1={function={function_name=array_slice, parameters={1={column={name=tags, table_ref=a}}, 2={column={name=start_idx, table_ref=a}}, 3={column={name=end_idx, table_ref=a}}}}}}}, target_table={table={alias=null, table=tab_out}}, columns={1={column={name=sliced_tags, table_ref=null}}}}}}",
				"[sliced_tags]",
				"{}",
				"{tab1={end_idx=[[@17,74:74='a',<455>,1:74]], start_idx=[[@13,61:61='a',<455>,1:61]], tags=[[@9,53:53='a',<455>,1:53]]}, tab_out={sliced_tags=[[@4,21:31='sliced_tags',<455>,1:21]]}}",
				"{query0={unnamed_0=[[@20,83:83=')',<288>,1:83]]}, insert1={sliced_tags=[[@4,21:31='sliced_tags',<455>,1:21]]}}",
				"{def_insert1={query_dictionary={sliced_tags=[[@4,21:31='sliced_tags',<455>,1:21]]}, table_dictionary={tab_out={sliced_tags=[[@4,21:31='sliced_tags',<455>,1:21]]}}, def_query0={query_dictionary={unnamed_0=[[@20,83:83=')',<288>,1:83]]}, table_dictionary={tab1={end_idx=[[@17,74:74='a',<455>,1:74]], start_idx=[[@13,61:61='a',<455>,1:61]], tags=[[@9,53:53='a',<455>,1:53]]}}, interface={unnamed_0=[{name=tags, table_ref=a}, {name=start_idx, table_ref=a}, {name=end_idx, table_ref=a}]}, table_alias={a=tab1}}, interface={sliced_tags=[{name=unnamed_0, table_ref=query0}]}}}");
	}

	@Test
	public void snowflakeArrayToStringUpdatePlacementV0Test() {
		final String query = "UPDATE tab1 SET joined_tags = array_to_string(tags, sep)";
		final SqlParseEventWalker extractor = runQuery(query);
		final Snippet snippet = extractor.getSnippet();
		assertDiagnosticAtPosition(
				snippet,
				"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
				ParseDiagnostic.Severity.WARNING,
				"routine 'array_to_string' at (l:1 c:30) is recognized as Snowflake-specific grammar.",
				null,
				1,
				30);
		assertNoFatalErrors(extractor);
		assertOutputs(
				extractor,
				"{SQL={update={table={alias=null, table=tab1}, assignments={1={set={column={name=joined_tags, table_ref=null}}, to={function={function_name=array_to_string, parameters={1={column={name=tags, table_ref=null}}, 2={column={name=sep, table_ref=null}}}}}}}}}}",
				"[joined_tags]",
				"{}",
				"{tab1={tags=[[@7,46:49='tags',<455>,1:46], [@10,55:55=')',<288>,1:55]], sep=[[@9,52:54='sep',<455>,1:52], [@10,55:55=')',<288>,1:55]]}}",
				"{update0={joined_tags=[[@3,16:26='joined_tags',<455>,1:16]]}}",
				"{def_update0={assignments={joined_tags=[{name=tags, table_ref=tab1}, {name=sep, table_ref=tab1}]}, table_dictionary={tab1={tags=[[@7,46:49='tags',<455>,1:46], [@10,55:55=')',<288>,1:55]], sep=[[@9,52:54='sep',<455>,1:52], [@10,55:55=')',<288>,1:55]]}}, update_dictionary={joined_tags=[[@3,16:26='joined_tags',<455>,1:16]]}, target_table={tab1={joined_tags=[[@3,16:26='joined_tags',<455>,1:16]]}}, lhs_unresolved_columns={joined_tags={column={name=joined_tags, table_ref=null}, locations=[[@3,16:26='joined_tags',<455>,1:16]]}}}}");
	}

	@Test
	public void snowflakeArraySliceUpdatePlacementV0Test() {
		final String query = "UPDATE tab1 SET sliced_tags = array_slice(tags, start_idx, end_idx)";
		final SqlParseEventWalker extractor = runQuery(query);
		final Snippet snippet = extractor.getSnippet();
		assertDiagnosticAtPosition(
				snippet,
				"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
				ParseDiagnostic.Severity.WARNING,
				"routine 'array_slice' at (l:1 c:30) is recognized as Snowflake-specific grammar.",
				null,
				1,
				30);
		assertNoFatalErrors(extractor);
		assertOutputs(
				extractor,
				"{SQL={update={table={alias=null, table=tab1}, assignments={1={set={column={name=sliced_tags, table_ref=null}}, to={function={function_name=array_slice, parameters={1={column={name=tags, table_ref=null}}, 2={column={name=start_idx, table_ref=null}}, 3={column={name=end_idx, table_ref=null}}}}}}}}}}",
				"[sliced_tags]",
				"{}",
				"{tab1={end_idx=[[@11,59:65='end_idx',<455>,1:59], [@12,66:66=')',<288>,1:66]], start_idx=[[@9,48:56='start_idx',<455>,1:48], [@12,66:66=')',<288>,1:66]], tags=[[@7,42:45='tags',<455>,1:42], [@12,66:66=')',<288>,1:66]]}}",
				"{update0={sliced_tags=[[@3,16:26='sliced_tags',<455>,1:16]]}}",
				"{def_update0={assignments={sliced_tags=[{name=tags, table_ref=tab1}, {name=start_idx, table_ref=tab1}, {name=end_idx, table_ref=tab1}]}, table_dictionary={tab1={end_idx=[[@11,59:65='end_idx',<455>,1:59], [@12,66:66=')',<288>,1:66]], start_idx=[[@9,48:56='start_idx',<455>,1:48], [@12,66:66=')',<288>,1:66]], tags=[[@7,42:45='tags',<455>,1:42], [@12,66:66=')',<288>,1:66]]}}, update_dictionary={sliced_tags=[[@3,16:26='sliced_tags',<455>,1:16]]}, target_table={tab1={sliced_tags=[[@3,16:26='sliced_tags',<455>,1:16]]}}, lhs_unresolved_columns={sliced_tags={column={name=sliced_tags, table_ref=null}, locations=[[@3,16:26='sliced_tags',<455>,1:16]]}}}}");
	}

	@Test
	public void snowflakeArrayToStringDeletePlacementV0Test() {
		final String query = "DELETE FROM tab1 WHERE array_to_string(tags, sep) IS NULL";
		final SqlParseEventWalker extractor = runQuery(query);
		final Snippet snippet = extractor.getSnippet();
		assertDiagnosticAtPosition(
				snippet,
				"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
				ParseDiagnostic.Severity.WARNING,
				"routine 'array_to_string' at (l:1 c:23) is recognized as Snowflake-specific grammar.",
				null,
				1,
				23);
		assertNoFatalErrors(extractor);
		assertOutputs(
				extractor,
				"{SQL={delete={table={alias=null, table=tab1}, where={condition={left={function={function_name=array_to_string, parameters={1={column={name=tags, table_ref=null}}, 2={column={name=sep, table_ref=null}}}}}, operator=IS NULL}}}}}",
				"[]",
				"{}",
				"{tab1={tags=[[@6,39:42='tags',<455>,1:39]], sep=[[@8,45:47='sep',<455>,1:45]]}}",
				"{}",
				"{def_delete0={query_dictionary={}, table_dictionary={tab1={tags=[[@6,39:42='tags',<455>,1:39]], sep=[[@8,45:47='sep',<455>,1:45]]}}, filters=[{name=tags, table_ref=tab1}, {name=sep, table_ref=tab1}], interface=null}}");
	}

	@Test
	public void snowflakeArraySliceDeletePlacementV0Test() {
		final String query = "DELETE FROM tab1 WHERE array_slice(tags, start_idx, end_idx) IS NULL";
		final SqlParseEventWalker extractor = runQuery(query);
		final Snippet snippet = extractor.getSnippet();
		assertDiagnosticAtPosition(
				snippet,
				"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
				ParseDiagnostic.Severity.WARNING,
				"routine 'array_slice' at (l:1 c:23) is recognized as Snowflake-specific grammar.",
				null,
				1,
				23);
		assertNoFatalErrors(extractor);
		assertOutputs(
				extractor,
				"{SQL={delete={table={alias=null, table=tab1}, where={condition={left={function={function_name=array_slice, parameters={1={column={name=tags, table_ref=null}}, 2={column={name=start_idx, table_ref=null}}, 3={column={name=end_idx, table_ref=null}}}}}, operator=IS NULL}}}}}",
				"[]",
				"{}",
				"{tab1={end_idx=[[@10,52:58='end_idx',<455>,1:52]], start_idx=[[@8,41:49='start_idx',<455>,1:41]], tags=[[@6,35:38='tags',<455>,1:35]]}}",
				"{}",
				"{def_delete0={query_dictionary={}, table_dictionary={tab1={end_idx=[[@10,52:58='end_idx',<455>,1:52]], start_idx=[[@8,41:49='start_idx',<455>,1:41]], tags=[[@6,35:38='tags',<455>,1:35]]}}, filters=[{name=tags, table_ref=tab1}, {name=start_idx, table_ref=tab1}, {name=end_idx, table_ref=tab1}], interface=null}}");
	}

	@Test
	public void snowflakeArrayDistinctSelectPlacementV0Test() {
		final String query = "SELECT array_distinct(a.tags) AS distinct_tags FROM tab1 a";
		final SqlParseEventWalker extractor = runQuery(query);
		final Snippet snippet = extractor.getSnippet();
		assertDiagnosticAtPosition(
				snippet,
				"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
				ParseDiagnostic.Severity.WARNING,
				"routine 'array_distinct' at (l:1 c:7) is recognized as Snowflake-specific grammar.",
				null,
				1,
				7);
		assertNoFatalErrors(extractor);
		assertOutputs(
				extractor,
				"{SQL={select={1={function={function_name=array_distinct, parameters={1={column={name=tags, table_ref=a}}}}, alias=distinct_tags}}, from={table={alias=a, table=tab1}}}}",
				"[distinct_tags]",
				"{}",
				"{tab1={tags=[[@3,22:22='a',<455>,1:22]]}}",
				"{query0={distinct_tags=[[@8,33:45='distinct_tags',<455>,1:33]]}}",
				"{def_query0={query_dictionary={distinct_tags=[[@8,33:45='distinct_tags',<455>,1:33]]}, table_dictionary={tab1={tags=[[@3,22:22='a',<455>,1:22]]}}, interface={distinct_tags=[{name=tags, table_ref=a}]}, table_alias={a=tab1}}}");
	}

	@Test
	public void snowflakeArrayDistinctMissingAliasFatalV0Test() {
		final String query = "SELECT array_distinct(z.tags) AS distinct_tags FROM tab1 a";
		final SqlParseEventWalker extractor = runQuery(query);
		final Snippet snippet = extractor.getSnippet();
		assertDiagnosticAtPosition(
				snippet,
				"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
				ParseDiagnostic.Severity.WARNING,
				"routine 'array_distinct' at (l:1 c:7) is recognized as Snowflake-specific grammar.",
				null,
				1,
				7);
		assertSingleMissingAliasFatal(extractor);
		assertOutputs(
				extractor,
				"{SQL={select={1={function={function_name=array_distinct, parameters={1={column={name=tags, table_ref=z}}}}, alias=distinct_tags}}, from={table={alias=a, table=tab1}}}}",
				"[distinct_tags]",
				"{}",
				"{tab1={}}",
				"{query0={distinct_tags=[[@8,33:45='distinct_tags',<455>,1:33]]}}",
				"{def_query0={query_dictionary={distinct_tags=[[@8,33:45='distinct_tags',<455>,1:33]]}, table_dictionary={tab1={}}, interface={distinct_tags=[{name=tags, table_ref=z}]}, table_alias={a=tab1}}}");
	}

	@Test
	public void snowflakeArrayCompactSelectPlacementV0Test() {
		final String query = "SELECT array_compact(a.tags) AS compact_tags FROM tab1 a";
		final SqlParseEventWalker extractor = runQuery(query);
		final Snippet snippet = extractor.getSnippet();
		assertDiagnosticAtPosition(
				snippet,
				"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
				ParseDiagnostic.Severity.WARNING,
				"routine 'array_compact' at (l:1 c:7) is recognized as Snowflake-specific grammar.",
				null,
				1,
				7);
		assertNoFatalErrors(extractor);
		assertOutputs(
				extractor,
				"{SQL={select={1={function={function_name=array_compact, parameters={1={column={name=tags, table_ref=a}}}}, alias=compact_tags}}, from={table={alias=a, table=tab1}}}}",
				"[compact_tags]",
				"{}",
				"{tab1={tags=[[@3,21:21='a',<455>,1:21]]}}",
				"{query0={compact_tags=[[@8,32:43='compact_tags',<455>,1:32]]}}",
				"{def_query0={query_dictionary={compact_tags=[[@8,32:43='compact_tags',<455>,1:32]]}, table_dictionary={tab1={tags=[[@3,21:21='a',<455>,1:21]]}}, interface={compact_tags=[{name=tags, table_ref=a}]}, table_alias={a=tab1}}}");
	}

	@Test
	public void snowflakeArrayCompactMissingAliasFatalV0Test() {
		final String query = "SELECT array_compact(z.tags) AS compact_tags FROM tab1 a";
		final SqlParseEventWalker extractor = runQuery(query);
		final Snippet snippet = extractor.getSnippet();
		assertDiagnosticAtPosition(
				snippet,
				"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
				ParseDiagnostic.Severity.WARNING,
				"routine 'array_compact' at (l:1 c:7) is recognized as Snowflake-specific grammar.",
				null,
				1,
				7);
		assertSingleMissingAliasFatal(extractor);
		assertOutputs(
				extractor,
				"{SQL={select={1={function={function_name=array_compact, parameters={1={column={name=tags, table_ref=z}}}}, alias=compact_tags}}, from={table={alias=a, table=tab1}}}}",
				"[compact_tags]",
				"{}",
				"{tab1={}}",
				"{query0={compact_tags=[[@8,32:43='compact_tags',<455>,1:32]]}}",
				"{def_query0={query_dictionary={compact_tags=[[@8,32:43='compact_tags',<455>,1:32]]}, table_dictionary={tab1={}}, interface={compact_tags=[{name=tags, table_ref=z}]}, table_alias={a=tab1}}}");
	}

	@Test
	public void snowflakeArraySizeSelectPlacementV0Test() {
		final String query = "SELECT array_size(a.tags) AS tags_size FROM tab1 a";
		final SqlParseEventWalker extractor = runQuery(query);
		final Snippet snippet = extractor.getSnippet();
		assertDiagnosticAtPosition(
				snippet,
				"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
				ParseDiagnostic.Severity.WARNING,
				"routine 'array_size' at (l:1 c:7) is recognized as Snowflake-specific grammar.",
				null,
				1,
				7);
		assertNoFatalErrors(extractor);
		assertOutputs(
				extractor,
				"{SQL={select={1={function={function_name=array_size, parameters={1={column={name=tags, table_ref=a}}}}, alias=tags_size}}, from={table={alias=a, table=tab1}}}}",
				"[tags_size]",
				"{}",
				"{tab1={tags=[[@3,18:18='a',<455>,1:18]]}}",
				"{query0={tags_size=[[@8,29:37='tags_size',<455>,1:29]]}}",
				"{def_query0={query_dictionary={tags_size=[[@8,29:37='tags_size',<455>,1:29]]}, table_dictionary={tab1={tags=[[@3,18:18='a',<455>,1:18]]}}, interface={tags_size=[{name=tags, table_ref=a}]}, table_alias={a=tab1}}}");
	}

	@Test
	public void snowflakeArraySizeMissingAliasFatalV0Test() {
		final String query = "SELECT array_size(z.tags) AS tags_size FROM tab1 a";
		final SqlParseEventWalker extractor = runQuery(query);
		final Snippet snippet = extractor.getSnippet();
		assertDiagnosticAtPosition(
				snippet,
				"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
				ParseDiagnostic.Severity.WARNING,
				"routine 'array_size' at (l:1 c:7) is recognized as Snowflake-specific grammar.",
				null,
				1,
				7);
		assertSingleMissingAliasFatal(extractor);
		assertOutputs(
				extractor,
				"{SQL={select={1={function={function_name=array_size, parameters={1={column={name=tags, table_ref=z}}}}, alias=tags_size}}, from={table={alias=a, table=tab1}}}}",
				"[tags_size]",
				"{}",
				"{tab1={}}",
				"{query0={tags_size=[[@8,29:37='tags_size',<455>,1:29]]}}",
				"{def_query0={query_dictionary={tags_size=[[@8,29:37='tags_size',<455>,1:29]]}, table_dictionary={tab1={}}, interface={tags_size=[{name=tags, table_ref=z}]}, table_alias={a=tab1}}}");
	}

	@Test
	public void snowflakeArrayCompactWherePlacementV0Test() {
		final String query = "SELECT a.id FROM tab1 a WHERE array_compact(a.tags) IS NOT NULL";
		final SqlParseEventWalker extractor = runQuery(query);
		final Snippet snippet = extractor.getSnippet();
		assertDiagnosticAtPosition(
				snippet,
				"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
				ParseDiagnostic.Severity.WARNING,
				"routine 'array_compact' at (l:1 c:30) is recognized as Snowflake-specific grammar.",
				null,
				1,
				30);
		assertNoFatalErrors(extractor);
		assertOutputs(
				extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}}, from={table={alias=a, table=tab1}}, where={condition={left={function={function_name=array_compact, parameters={1={column={name=tags, table_ref=a}}}}}, operator=IS NOT NULL}}}}",
				"[id]",
				"{}",
				"{tab1={id=[[@1,7:7='a',<455>,1:7]], tags=[[@10,44:44='a',<455>,1:44]]}}",
				"{query0={id=[[@3,9:10='id',<455>,1:9]]}}",
				"{def_query0={query_dictionary={id=[[@3,9:10='id',<455>,1:9]]}, table_dictionary={tab1={id=[[@1,7:7='a',<455>,1:7]], tags=[[@10,44:44='a',<455>,1:44]]}}, filters=[{name=tags, table_ref=a}], interface={id=[{name=id, table_ref=a}]}, table_alias={a=tab1}}}");
	}

	@Test
	public void snowflakeArrayCompactOrderByPlacementV0Test() {
		final String query = "SELECT a.id FROM tab1 a ORDER BY array_compact(a.tags)";
		final SqlParseEventWalker extractor = runQuery(query);
		final Snippet snippet = extractor.getSnippet();
		assertDiagnosticAtPosition(
				snippet,
				"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
				ParseDiagnostic.Severity.WARNING,
				"routine 'array_compact' at (l:1 c:33) is recognized as Snowflake-specific grammar.",
				null,
				1,
				33);
		assertNoFatalErrors(extractor);
		assertOutputs(
				extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}}, orderby={1={null_order=null, predicand={function={function_name=array_compact, parameters={1={column={name=tags, table_ref=a}}}}}, sort_order=ASC}}, from={table={alias=a, table=tab1}}}}",
				"[id]",
				"{}",
				"{tab1={id=[[@1,7:7='a',<455>,1:7]], tags=[[@11,47:47='a',<455>,1:47]]}}",
				"{query0={id=[[@3,9:10='id',<455>,1:9]]}}",
				"{def_query0={query_dictionary={id=[[@3,9:10='id',<455>,1:9]]}, table_dictionary={tab1={id=[[@1,7:7='a',<455>,1:7]], tags=[[@11,47:47='a',<455>,1:47]]}}, ordered_by=[{name=tags, table_ref=a}], interface={id=[{name=id, table_ref=a}]}, table_alias={a=tab1}}}");
	}

	@Test
	public void snowflakeArrayCompactValuesPlacementV0Test() {
		final String query = "VALUES (array_compact('arr'))";
		final SqlParseEventWalker extractor = runQuery(query);
		final Snippet snippet = extractor.getSnippet();
		assertDiagnosticAtPosition(
				snippet,
				"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
				ParseDiagnostic.Severity.WARNING,
				"routine 'array_compact' at (l:1 c:8) is recognized as Snowflake-specific grammar.",
				null,
				1,
				8);
		assertNoFatalErrors(extractor);
		assertOutputs(
				extractor,
				"{SQL={values={matrix={1={row={1={function={function_name=array_compact, parameters={1={literal='arr'}}}}}}}}}}",
				"[$1]",
				"{}",
				"{}",
				"{values0={$1=[[@1,7:7='(',<287>,1:7]]}}",
				"{def_values0={query_dictionary={$1=[[@1,7:7='(',<287>,1:7]]}, interface={$1=[]}}}");
	}

	@Test
	public void snowflakeArrayCompactInsertPlacementV0Test() {
		final String query = "INSERT INTO tab_out (compact_tags) SELECT array_compact(a.tags) FROM tab1 a";
		final SqlParseEventWalker extractor = runQuery(query);
		final Snippet snippet = extractor.getSnippet();
		assertDiagnosticAtPosition(
				snippet,
				"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
				ParseDiagnostic.Severity.WARNING,
				"routine 'array_compact' at (l:1 c:42) is recognized as Snowflake-specific grammar.",
				null,
				1,
				42);
		assertNoFatalErrors(extractor);
		assertOutputs(
				extractor,
				"{SQL={insert={preamble=insert_into, from={from={table={alias=a, table=tab1}}, select={1={function={function_name=array_compact, parameters={1={column={name=tags, table_ref=a}}}}}}}, target_table={table={alias=null, table=tab_out}}, columns={1={column={name=compact_tags, table_ref=null}}}}}}",
				"[compact_tags]",
				"{}",
				"{tab1={tags=[[@9,56:56='a',<455>,1:56]]}, tab_out={compact_tags=[[@4,21:32='compact_tags',<455>,1:21]]}}",
				"{query0={unnamed_0=[[@12,62:62=')',<288>,1:62]]}, insert1={compact_tags=[[@4,21:32='compact_tags',<455>,1:21]]}}",
				"{def_insert1={query_dictionary={compact_tags=[[@4,21:32='compact_tags',<455>,1:21]]}, table_dictionary={tab_out={compact_tags=[[@4,21:32='compact_tags',<455>,1:21]]}}, def_query0={query_dictionary={unnamed_0=[[@12,62:62=')',<288>,1:62]]}, table_dictionary={tab1={tags=[[@9,56:56='a',<455>,1:56]]}}, interface={unnamed_0=[{name=tags, table_ref=a}]}, table_alias={a=tab1}}, interface={compact_tags=[{name=unnamed_0, table_ref=query0}]}}}");
	}

	@Test
	public void snowflakeArrayCompactUpdatePlacementV0Test() {
		final String query = "UPDATE tab1 SET compact_tags = array_compact(tags)";
		final SqlParseEventWalker extractor = runQuery(query);
		final Snippet snippet = extractor.getSnippet();
		assertDiagnosticAtPosition(
				snippet,
				"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
				ParseDiagnostic.Severity.WARNING,
				"routine 'array_compact' at (l:1 c:31) is recognized as Snowflake-specific grammar.",
				null,
				1,
				31);
		assertNoFatalErrors(extractor);
		assertOutputs(
				extractor,
				"{SQL={update={table={alias=null, table=tab1}, assignments={1={set={column={name=compact_tags, table_ref=null}}, to={function={function_name=array_compact, parameters={1={column={name=tags, table_ref=null}}}}}}}}}}",
				"[compact_tags]",
				"{}",
				"{tab1={tags=[[@7,45:48='tags',<455>,1:45], [@8,49:49=')',<288>,1:49]]}}",
				"{update0={compact_tags=[[@3,16:27='compact_tags',<455>,1:16]]}}",
				"{def_update0={assignments={compact_tags=[{name=tags, table_ref=tab1}]}, table_dictionary={tab1={tags=[[@7,45:48='tags',<455>,1:45], [@8,49:49=')',<288>,1:49]]}}, update_dictionary={compact_tags=[[@3,16:27='compact_tags',<455>,1:16]]}, target_table={tab1={compact_tags=[[@3,16:27='compact_tags',<455>,1:16]]}}, lhs_unresolved_columns={compact_tags={column={name=compact_tags, table_ref=null}, locations=[[@3,16:27='compact_tags',<455>,1:16]]}}}}");
	}

	@Test
	public void snowflakeArrayCompactDeletePlacementV0Test() {
		final String query = "DELETE FROM tab1 WHERE array_compact(tags) IS NULL";
		final SqlParseEventWalker extractor = runQuery(query);
		final Snippet snippet = extractor.getSnippet();
		assertDiagnosticAtPosition(
				snippet,
				"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
				ParseDiagnostic.Severity.WARNING,
				"routine 'array_compact' at (l:1 c:23) is recognized as Snowflake-specific grammar.",
				null,
				1,
				23);
		assertNoFatalErrors(extractor);
		assertOutputs(
				extractor,
				"{SQL={delete={table={alias=null, table=tab1}, where={condition={left={function={function_name=array_compact, parameters={1={column={name=tags, table_ref=null}}}}}, operator=IS NULL}}}}}",
				"[]",
				"{}",
				"{tab1={tags=[[@6,37:40='tags',<455>,1:37]]}}",
				"{}",
				"{def_delete0={query_dictionary={}, table_dictionary={tab1={tags=[[@6,37:40='tags',<455>,1:37]]}}, filters=[{name=tags, table_ref=tab1}], interface=null}}");
	}

	@Test
	public void snowflakeArraySizeWherePlacementV0Test() {
		final String query = "SELECT a.id FROM tab1 a WHERE array_size(a.tags) > 0";
		final SqlParseEventWalker extractor = runQuery(query);
		final Snippet snippet = extractor.getSnippet();
		assertDiagnosticAtPosition(
				snippet,
				"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
				ParseDiagnostic.Severity.WARNING,
				"routine 'array_size' at (l:1 c:30) is recognized as Snowflake-specific grammar.",
				null,
				1,
				30);
		assertNoFatalErrors(extractor);
		assertOutputs(
				extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}}, from={table={alias=a, table=tab1}}, where={condition={left={function={function_name=array_size, parameters={1={column={name=tags, table_ref=a}}}}}, right={literal=0}, operator=>}}}}",
				"[id]",
				"{}",
				"{tab1={id=[[@1,7:7='a',<455>,1:7]], tags=[[@10,41:41='a',<455>,1:41]]}}",
				"{query0={id=[[@3,9:10='id',<455>,1:9]]}}",
				"{def_query0={query_dictionary={id=[[@3,9:10='id',<455>,1:9]]}, table_dictionary={tab1={id=[[@1,7:7='a',<455>,1:7]], tags=[[@10,41:41='a',<455>,1:41]]}}, filters=[{name=tags, table_ref=a}], interface={id=[{name=id, table_ref=a}]}, table_alias={a=tab1}}}");
	}

	@Test
	public void snowflakeArraySizeOrderByPlacementV0Test() {
		final String query = "SELECT a.id FROM tab1 a ORDER BY array_size(a.tags)";
		final SqlParseEventWalker extractor = runQuery(query);
		final Snippet snippet = extractor.getSnippet();
		assertDiagnosticAtPosition(
				snippet,
				"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
				ParseDiagnostic.Severity.WARNING,
				"routine 'array_size' at (l:1 c:33) is recognized as Snowflake-specific grammar.",
				null,
				1,
				33);
		assertNoFatalErrors(extractor);
		assertOutputs(
				extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}}, orderby={1={null_order=null, predicand={function={function_name=array_size, parameters={1={column={name=tags, table_ref=a}}}}}, sort_order=ASC}}, from={table={alias=a, table=tab1}}}}",
				"[id]",
				"{}",
				"{tab1={id=[[@1,7:7='a',<455>,1:7]], tags=[[@11,44:44='a',<455>,1:44]]}}",
				"{query0={id=[[@3,9:10='id',<455>,1:9]]}}",
				"{def_query0={query_dictionary={id=[[@3,9:10='id',<455>,1:9]]}, table_dictionary={tab1={id=[[@1,7:7='a',<455>,1:7]], tags=[[@11,44:44='a',<455>,1:44]]}}, ordered_by=[{name=tags, table_ref=a}], interface={id=[{name=id, table_ref=a}]}, table_alias={a=tab1}}}");
	}

	@Test
	public void snowflakeArraySizeValuesPlacementV0Test() {
		final String query = "VALUES (array_size('arr'))";
		final SqlParseEventWalker extractor = runQuery(query);
		final Snippet snippet = extractor.getSnippet();
		assertDiagnosticAtPosition(
				snippet,
				"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
				ParseDiagnostic.Severity.WARNING,
				"routine 'array_size' at (l:1 c:8) is recognized as Snowflake-specific grammar.",
				null,
				1,
				8);
		assertNoFatalErrors(extractor);
		assertOutputs(
				extractor,
				"{SQL={values={matrix={1={row={1={function={function_name=array_size, parameters={1={literal='arr'}}}}}}}}}}",
				"[$1]",
				"{}",
				"{}",
				"{values0={$1=[[@1,7:7='(',<287>,1:7]]}}",
				"{def_values0={query_dictionary={$1=[[@1,7:7='(',<287>,1:7]]}, interface={$1=[]}}}");
	}

	@Test
	public void snowflakeArraySizeInsertPlacementV0Test() {
		final String query = "INSERT INTO tab_out (tags_size) SELECT array_size(a.tags) FROM tab1 a";
		final SqlParseEventWalker extractor = runQuery(query);
		final Snippet snippet = extractor.getSnippet();
		assertDiagnosticAtPosition(
				snippet,
				"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
				ParseDiagnostic.Severity.WARNING,
				"routine 'array_size' at (l:1 c:39) is recognized as Snowflake-specific grammar.",
				null,
				1,
				39);
		assertNoFatalErrors(extractor);
		assertOutputs(
				extractor,
				"{SQL={insert={preamble=insert_into, from={from={table={alias=a, table=tab1}}, select={1={function={function_name=array_size, parameters={1={column={name=tags, table_ref=a}}}}}}}, target_table={table={alias=null, table=tab_out}}, columns={1={column={name=tags_size, table_ref=null}}}}}}",
				"[tags_size]",
				"{}",
				"{tab1={tags=[[@9,50:50='a',<455>,1:50]]}, tab_out={tags_size=[[@4,21:29='tags_size',<455>,1:21]]}}",
				"{query0={unnamed_0=[[@12,56:56=')',<288>,1:56]]}, insert1={tags_size=[[@4,21:29='tags_size',<455>,1:21]]}}",
				"{def_insert1={query_dictionary={tags_size=[[@4,21:29='tags_size',<455>,1:21]]}, table_dictionary={tab_out={tags_size=[[@4,21:29='tags_size',<455>,1:21]]}}, def_query0={query_dictionary={unnamed_0=[[@12,56:56=')',<288>,1:56]]}, table_dictionary={tab1={tags=[[@9,50:50='a',<455>,1:50]]}}, interface={unnamed_0=[{name=tags, table_ref=a}]}, table_alias={a=tab1}}, interface={tags_size=[{name=unnamed_0, table_ref=query0}]}}}");
	}

	@Test
	public void snowflakeArraySizeUpdatePlacementV0Test() {
		final String query = "UPDATE tab1 SET tags_size = array_size(tags)";
		final SqlParseEventWalker extractor = runQuery(query);
		final Snippet snippet = extractor.getSnippet();
		assertDiagnosticAtPosition(
				snippet,
				"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
				ParseDiagnostic.Severity.WARNING,
				"routine 'array_size' at (l:1 c:28) is recognized as Snowflake-specific grammar.",
				null,
				1,
				28);
		assertNoFatalErrors(extractor);
		assertOutputs(
				extractor,
				"{SQL={update={table={alias=null, table=tab1}, assignments={1={set={column={name=tags_size, table_ref=null}}, to={function={function_name=array_size, parameters={1={column={name=tags, table_ref=null}}}}}}}}}}",
				"[tags_size]",
				"{}",
				"{tab1={tags=[[@7,39:42='tags',<455>,1:39], [@8,43:43=')',<288>,1:43]]}}",
				"{update0={tags_size=[[@3,16:24='tags_size',<455>,1:16]]}}",
				"{def_update0={assignments={tags_size=[{name=tags, table_ref=tab1}]}, table_dictionary={tab1={tags=[[@7,39:42='tags',<455>,1:39], [@8,43:43=')',<288>,1:43]]}}, update_dictionary={tags_size=[[@3,16:24='tags_size',<455>,1:16]]}, target_table={tab1={tags_size=[[@3,16:24='tags_size',<455>,1:16]]}}, lhs_unresolved_columns={tags_size={column={name=tags_size, table_ref=null}, locations=[[@3,16:24='tags_size',<455>,1:16]]}}}}");
	}

	@Test
	public void snowflakeArraySizeDeletePlacementV0Test() {
		final String query = "DELETE FROM tab1 WHERE array_size(tags) > 0";
		final SqlParseEventWalker extractor = runQuery(query);
		final Snippet snippet = extractor.getSnippet();
		assertDiagnosticAtPosition(
				snippet,
				"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
				ParseDiagnostic.Severity.WARNING,
				"routine 'array_size' at (l:1 c:23) is recognized as Snowflake-specific grammar.",
				null,
				1,
				23);
		assertNoFatalErrors(extractor);
		assertOutputs(
				extractor,
				"{SQL={delete={table={alias=null, table=tab1}, where={condition={left={function={function_name=array_size, parameters={1={column={name=tags, table_ref=null}}}}}, right={literal=0}, operator=>}}}}}",
				"[]",
				"{}",
				"{tab1={tags=[[@6,34:37='tags',<455>,1:34]]}}",
				"{}",
				"{def_delete0={query_dictionary={}, table_dictionary={tab1={tags=[[@6,34:37='tags',<455>,1:34]]}}, filters=[{name=tags, table_ref=tab1}], interface=null}}");
	}

	@Test
	public void snowflakeSplitToArraySelectSeedV0Test() {
		final String query = "SELECT split_to_array(a.payload, a.sep) AS split_vals FROM tab1 a";
		final SqlParseEventWalker extractor = runQuery(query);
		final Snippet snippet = extractor.getSnippet();
		assertDiagnosticAtPosition(
				snippet,
				"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
				ParseDiagnostic.Severity.WARNING,
				"routine 'split_to_array' at (l:1 c:7) is recognized as Snowflake-specific grammar.",
				null,
				1,
				7);
		assertNoFatalErrors(extractor);
		assertOutputs(
				extractor,
				"{SQL={select={1={function={function_name=split_to_array, parameters={1={column={name=payload, table_ref=a}}, 2={column={name=sep, table_ref=a}}}}, alias=split_vals}}, from={table={alias=a, table=tab1}}}}",
				"[split_vals]",
				"{}",
				"{tab1={payload=[[@3,22:22='a',<455>,1:22]], sep=[[@7,33:33='a',<455>,1:33]]}}",
				"{query0={split_vals=[[@12,43:52='split_vals',<455>,1:43]]}}",
				"{def_query0={query_dictionary={split_vals=[[@12,43:52='split_vals',<455>,1:43]]}, table_dictionary={tab1={payload=[[@3,22:22='a',<455>,1:22]], sep=[[@7,33:33='a',<455>,1:33]]}}, interface={split_vals=[{name=payload, table_ref=a}, {name=sep, table_ref=a}]}, table_alias={a=tab1}}}");
	}

	@Test
	public void snowflakeSplitToArrayMissingAliasFatalV0Test() {
		final String query = "SELECT split_to_array(z.payload, a.sep) AS split_vals FROM tab1 a";
		final SqlParseEventWalker extractor = runQuery(query);
		final Snippet snippet = extractor.getSnippet();
		assertDiagnosticAtPosition(
				snippet,
				"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
				ParseDiagnostic.Severity.WARNING,
				"routine 'split_to_array' at (l:1 c:7) is recognized as Snowflake-specific grammar.",
				null,
				1,
				7);
		assertSingleMissingAliasFatal(extractor);
		assertOutputs(
				extractor,
				"{SQL={select={1={function={function_name=split_to_array, parameters={1={column={name=payload, table_ref=z}}, 2={column={name=sep, table_ref=a}}}}, alias=split_vals}}, from={table={alias=a, table=tab1}}}}",
				"[split_vals]",
				"{}",
				"{tab1={sep=[[@7,33:33='a',<455>,1:33]]}}",
				"{query0={split_vals=[[@12,43:52='split_vals',<455>,1:43]]}}",
				"{def_query0={query_dictionary={split_vals=[[@12,43:52='split_vals',<455>,1:43]]}, table_dictionary={tab1={sep=[[@7,33:33='a',<455>,1:33]]}}, interface={split_vals=[{name=payload, table_ref=z}, {name=sep, table_ref=a}]}, table_alias={a=tab1}}}");
	}

	@Test
	public void snowflakeArrayGenerateRangeSelectSeedV0Test() {
		final String query = "SELECT array_generate_range(1, 5) AS gen_vals FROM tab1 a";
		final SqlParseEventWalker extractor = runQuery(query);
		final Snippet snippet = extractor.getSnippet();
		assertDiagnosticAtPosition(
				snippet,
				"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
				ParseDiagnostic.Severity.WARNING,
				"routine 'array_generate_range' at (l:1 c:7) is recognized as Snowflake-specific grammar.",
				null,
				1,
				7);
		assertNoFatalErrors(extractor);
		assertOutputs(
				extractor,
				"{SQL={select={1={function={function_name=array_generate_range, parameters={1={literal=1}, 2={literal=5}}}, alias=gen_vals}}, from={table={alias=a, table=tab1}}}}",
				"[gen_vals]",
				"{}",
				"{tab1={}}",
				"{query0={gen_vals=[[@8,37:44='gen_vals',<455>,1:37]]}}",
				"{def_query0={query_dictionary={gen_vals=[[@8,37:44='gen_vals',<455>,1:37]]}, table_dictionary={tab1={}}, interface={gen_vals=[]}, table_alias={a=tab1}}}");
	}

	@Test
	public void snowflakeArrayPositionSelectSeedV0Test() {
		final String query = "SELECT array_position(a.needle, a.tags) AS pos_idx FROM tab1 a";
		final SqlParseEventWalker extractor = runQuery(query);
		final Snippet snippet = extractor.getSnippet();
		assertDiagnosticAtPosition(
				snippet,
				"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
				ParseDiagnostic.Severity.WARNING,
				"routine 'array_position' at (l:1 c:7) is recognized as Snowflake-specific grammar.",
				null,
				1,
				7);
		assertNoFatalErrors(extractor);
		assertOutputs(
				extractor,
				"{SQL={select={1={function={function_name=array_position, parameters={1={column={name=needle, table_ref=a}}, 2={column={name=tags, table_ref=a}}}}, alias=pos_idx}}, from={table={alias=a, table=tab1}}}}",
				"[pos_idx]",
				"{}",
				"{tab1={needle=[[@3,22:22='a',<455>,1:22]], tags=[[@7,32:32='a',<455>,1:32]]}}",
				"{query0={pos_idx=[[@12,43:49='pos_idx',<455>,1:43]]}}",
				"{def_query0={query_dictionary={pos_idx=[[@12,43:49='pos_idx',<455>,1:43]]}, table_dictionary={tab1={needle=[[@3,22:22='a',<455>,1:22]], tags=[[@7,32:32='a',<455>,1:32]]}}, interface={pos_idx=[{name=needle, table_ref=a}, {name=tags, table_ref=a}]}, table_alias={a=tab1}}}");
	}

	@Test
	public void snowflakeArrayPositionMissingAliasFatalV0Test() {
		final String query = "SELECT array_position(z.needle, a.tags) AS pos_idx FROM tab1 a";
		final SqlParseEventWalker extractor = runQuery(query);
		final Snippet snippet = extractor.getSnippet();
		assertDiagnosticAtPosition(
				snippet,
				"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
				ParseDiagnostic.Severity.WARNING,
				"routine 'array_position' at (l:1 c:7) is recognized as Snowflake-specific grammar.",
				null,
				1,
				7);
		assertSingleMissingAliasFatal(extractor);
		assertOutputs(
				extractor,
				"{SQL={select={1={function={function_name=array_position, parameters={1={column={name=needle, table_ref=z}}, 2={column={name=tags, table_ref=a}}}}, alias=pos_idx}}, from={table={alias=a, table=tab1}}}}",
				"[pos_idx]",
				"{}",
				"{tab1={tags=[[@7,32:32='a',<455>,1:32]]}}",
				"{query0={pos_idx=[[@12,43:49='pos_idx',<455>,1:43]]}}",
				"{def_query0={query_dictionary={pos_idx=[[@12,43:49='pos_idx',<455>,1:43]]}, table_dictionary={tab1={tags=[[@7,32:32='a',<455>,1:32]]}}, interface={pos_idx=[{name=needle, table_ref=z}, {name=tags, table_ref=a}]}, table_alias={a=tab1}}}");
	}

	@Test
	public void snowflakeArrayPositionWherePlacementV0Test() {
		final String query = "SELECT a.id FROM tab1 a WHERE array_position(a.needle, a.tags) > 0";
		final SqlParseEventWalker extractor = runQuery(query);
		final Snippet snippet = extractor.getSnippet();
		assertDiagnosticAtPosition(
				snippet,
				"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
				ParseDiagnostic.Severity.WARNING,
				"routine 'array_position' at (l:1 c:30) is recognized as Snowflake-specific grammar.",
				null,
				1,
				30);
		assertNoFatalErrors(extractor);
		assertOutputs(
				extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}}, from={table={alias=a, table=tab1}}, where={condition={left={function={function_name=array_position, parameters={1={column={name=needle, table_ref=a}}, 2={column={name=tags, table_ref=a}}}}}, right={literal=0}, operator=>}}}}",
				"[id]",
				"{}",
				"{tab1={id=[[@1,7:7='a',<455>,1:7]], needle=[[@10,45:45='a',<455>,1:45]], tags=[[@14,55:55='a',<455>,1:55]]}}",
				"{query0={id=[[@3,9:10='id',<455>,1:9]]}}",
				"{def_query0={query_dictionary={id=[[@3,9:10='id',<455>,1:9]]}, table_dictionary={tab1={id=[[@1,7:7='a',<455>,1:7]], needle=[[@10,45:45='a',<455>,1:45]], tags=[[@14,55:55='a',<455>,1:55]]}}, filters=[{name=needle, table_ref=a}, {name=tags, table_ref=a}], interface={id=[{name=id, table_ref=a}]}, table_alias={a=tab1}}}");
	}

	@Test
	public void snowflakeArrayPositionOrderByPlacementV0Test() {
		final String query = "SELECT a.id FROM tab1 a ORDER BY array_position(a.needle, a.tags)";
		final SqlParseEventWalker extractor = runQuery(query);
		final Snippet snippet = extractor.getSnippet();
		assertDiagnosticAtPosition(
				snippet,
				"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
				ParseDiagnostic.Severity.WARNING,
				"routine 'array_position' at (l:1 c:33) is recognized as Snowflake-specific grammar.",
				null,
				1,
				33);
		assertNoFatalErrors(extractor);
		assertOutputs(
				extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}}, orderby={1={null_order=null, predicand={function={function_name=array_position, parameters={1={column={name=needle, table_ref=a}}, 2={column={name=tags, table_ref=a}}}}}, sort_order=ASC}}, from={table={alias=a, table=tab1}}}}",
				"[id]",
				"{}",
				"{tab1={id=[[@1,7:7='a',<455>,1:7]], needle=[[@11,48:48='a',<455>,1:48]], tags=[[@15,58:58='a',<455>,1:58]]}}",
				"{query0={id=[[@3,9:10='id',<455>,1:9]]}}",
				"{def_query0={query_dictionary={id=[[@3,9:10='id',<455>,1:9]]}, table_dictionary={tab1={id=[[@1,7:7='a',<455>,1:7]], needle=[[@11,48:48='a',<455>,1:48]], tags=[[@15,58:58='a',<455>,1:58]]}}, ordered_by=[{name=needle, table_ref=a}, {name=tags, table_ref=a}], interface={id=[{name=id, table_ref=a}]}, table_alias={a=tab1}}}");
	}

	@Test
	public void snowflakeArrayPositionValuesPlacementV0Test() {
		final String query = "VALUES (array_position(a.needle, a.tags))";
		final SqlParseEventWalker extractor = runQuery(query);
		final Snippet snippet = extractor.getSnippet();
		assertDiagnosticAtPosition(
				snippet,
				"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
				ParseDiagnostic.Severity.WARNING,
				"routine 'array_position' at (l:1 c:8) is recognized as Snowflake-specific grammar.",
				null,
				1,
				8);
		assertNoFatalErrors(extractor);
		assertOutputs(
				extractor,
				"{SQL={values={matrix={1={row={1={function={function_name=array_position, parameters={1={column={name=needle, table_ref=a}}, 2={column={name=tags, table_ref=a}}}}}}}}}}}",
				"[$1]",
				"{}",
				"{}",
				"{values0={$1=[[@1,7:7='(',<287>,1:7]]}}",
				"{def_values0={query_dictionary={$1=[[@1,7:7='(',<287>,1:7]]}, interface={$1=[]}}}");
	}

	@Test
	public void snowflakeArrayPositionInsertPlacementV0Test() {
		final String query = "INSERT INTO tab_out(pos_idx) SELECT array_position(a.needle, a.tags) FROM tab1 a";
		final SqlParseEventWalker extractor = runQuery(query);
		final Snippet snippet = extractor.getSnippet();
		assertDiagnosticAtPosition(
				snippet,
				"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
				ParseDiagnostic.Severity.WARNING,
				"routine 'array_position' at (l:1 c:36) is recognized as Snowflake-specific grammar.",
				null,
				1,
				36);
		assertNoFatalErrors(extractor);
		assertOutputs(
				extractor,
				"{SQL={insert={preamble=insert_into, from={from={table={alias=a, table=tab1}}, select={1={function={function_name=array_position, parameters={1={column={name=needle, table_ref=a}}, 2={column={name=tags, table_ref=a}}}}}}}, target_table={table={alias=null, table=tab_out}}, columns={1={column={name=pos_idx, table_ref=null}}}}}}",
				"[pos_idx]",
				"{}",
				"{tab1={needle=[[@9,51:51='a',<455>,1:51]], tags=[[@13,61:61='a',<455>,1:61]]}, tab_out={pos_idx=[[@4,20:26='pos_idx',<455>,1:20]]}}",
				"{query0={unnamed_0=[[@16,67:67=')',<288>,1:67]]}, insert1={pos_idx=[[@4,20:26='pos_idx',<455>,1:20]]}}",
				"{def_insert1={query_dictionary={pos_idx=[[@4,20:26='pos_idx',<455>,1:20]]}, table_dictionary={tab_out={pos_idx=[[@4,20:26='pos_idx',<455>,1:20]]}}, def_query0={query_dictionary={unnamed_0=[[@16,67:67=')',<288>,1:67]]}, table_dictionary={tab1={needle=[[@9,51:51='a',<455>,1:51]], tags=[[@13,61:61='a',<455>,1:61]]}}, interface={unnamed_0=[{name=needle, table_ref=a}, {name=tags, table_ref=a}]}, table_alias={a=tab1}}, interface={pos_idx=[{name=unnamed_0, table_ref=query0}]}}}");
	}

	@Test
	public void snowflakeArrayPositionUpdatePlacementV0Test() {
		final String query = "UPDATE tab1 SET pos_idx = array_position(needle, tags)";
		final SqlParseEventWalker extractor = runQuery(query);
		final Snippet snippet = extractor.getSnippet();
		assertDiagnosticAtPosition(
				snippet,
				"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
				ParseDiagnostic.Severity.WARNING,
				"routine 'array_position' at (l:1 c:26) is recognized as Snowflake-specific grammar.",
				null,
				1,
				26);
		assertNoFatalErrors(extractor);
		assertOutputs(
				extractor,
				"{SQL={update={table={alias=null, table=tab1}, assignments={1={set={column={name=pos_idx, table_ref=null}}, to={function={function_name=array_position, parameters={1={column={name=needle, table_ref=null}}, 2={column={name=tags, table_ref=null}}}}}}}}}}",
				"[pos_idx]",
				"{}",
				"{tab1={needle=[[@7,41:46='needle',<455>,1:41], [@10,53:53=')',<288>,1:53]], tags=[[@9,49:52='tags',<455>,1:49], [@10,53:53=')',<288>,1:53]]}}",
				"{update0={pos_idx=[[@3,16:22='pos_idx',<455>,1:16]]}}",
				"{def_update0={assignments={pos_idx=[{name=needle, table_ref=tab1}, {name=tags, table_ref=tab1}]}, table_dictionary={tab1={needle=[[@7,41:46='needle',<455>,1:41], [@10,53:53=')',<288>,1:53]], tags=[[@9,49:52='tags',<455>,1:49], [@10,53:53=')',<288>,1:53]]}}, update_dictionary={pos_idx=[[@3,16:22='pos_idx',<455>,1:16]]}, target_table={tab1={pos_idx=[[@3,16:22='pos_idx',<455>,1:16]]}}, lhs_unresolved_columns={pos_idx={column={name=pos_idx, table_ref=null}, locations=[[@3,16:22='pos_idx',<455>,1:16]]}}}}");
	}

	@Test
	public void snowflakeArrayPositionDeletePlacementV0Test() {
		final String query = "DELETE FROM tab1 WHERE array_position(needle, tags) > 0";
		final SqlParseEventWalker extractor = runQuery(query);
		final Snippet snippet = extractor.getSnippet();
		assertDiagnosticAtPosition(
				snippet,
				"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
				ParseDiagnostic.Severity.WARNING,
				"routine 'array_position' at (l:1 c:23) is recognized as Snowflake-specific grammar.",
				null,
				1,
				23);
		assertNoFatalErrors(extractor);
		assertOutputs(
				extractor,
				"{SQL={delete={table={alias=null, table=tab1}, where={condition={left={function={function_name=array_position, parameters={1={column={name=needle, table_ref=null}}, 2={column={name=tags, table_ref=null}}}}}, right={literal=0}, operator=>}}}}}",
				"[]",
				"{}",
				"{tab1={needle=[[@6,38:43='needle',<455>,1:38]], tags=[[@8,46:49='tags',<455>,1:46]]}}",
				"{}",
				"{def_delete0={query_dictionary={}, table_dictionary={tab1={needle=[[@6,38:43='needle',<455>,1:38]], tags=[[@8,46:49='tags',<455>,1:46]]}}, filters=[{name=needle, table_ref=tab1}, {name=tags, table_ref=tab1}], interface=null}}");
	}

	@Test
	public void snowflakeArraysZipSelectSeedV0Test() {
		final String query = "SELECT arrays_zip(a.tags, a.vals) AS zipped_vals FROM tab1 a";
		final SqlParseEventWalker extractor = runQuery(query);
		final Snippet snippet = extractor.getSnippet();
		assertDiagnosticAtPosition(
				snippet,
				"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
				ParseDiagnostic.Severity.WARNING,
				"routine 'arrays_zip' at (l:1 c:7) is recognized as Snowflake-specific grammar.",
				null,
				1,
				7);
		assertNoFatalErrors(extractor);
		assertOutputs(
				extractor,
				"{SQL={select={1={function={function_name=arrays_zip, parameters={1={column={name=tags, table_ref=a}}, 2={column={name=vals, table_ref=a}}}}, alias=zipped_vals}}, from={table={alias=a, table=tab1}}}}",
				"[zipped_vals]",
				"{}",
				"{tab1={vals=[[@7,26:26='a',<455>,1:26]], tags=[[@3,18:18='a',<455>,1:18]]}}",
				"{query0={zipped_vals=[[@12,37:47='zipped_vals',<455>,1:37]]}}",
				"{def_query0={query_dictionary={zipped_vals=[[@12,37:47='zipped_vals',<455>,1:37]]}, table_dictionary={tab1={vals=[[@7,26:26='a',<455>,1:26]], tags=[[@3,18:18='a',<455>,1:18]]}}, interface={zipped_vals=[{name=tags, table_ref=a}, {name=vals, table_ref=a}]}, table_alias={a=tab1}}}");
	}

	@Test
	public void snowflakeArraysZipMissingAliasFatalV0Test() {
		final String query = "SELECT arrays_zip(z.tags, a.vals) AS zipped_vals FROM tab1 a";
		final SqlParseEventWalker extractor = runQuery(query);
		final Snippet snippet = extractor.getSnippet();
		assertDiagnosticAtPosition(
				snippet,
				"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
				ParseDiagnostic.Severity.WARNING,
				"routine 'arrays_zip' at (l:1 c:7) is recognized as Snowflake-specific grammar.",
				null,
				1,
				7);
		assertSingleMissingAliasFatal(extractor);
		assertOutputs(
				extractor,
				"{SQL={select={1={function={function_name=arrays_zip, parameters={1={column={name=tags, table_ref=z}}, 2={column={name=vals, table_ref=a}}}}, alias=zipped_vals}}, from={table={alias=a, table=tab1}}}}",
				"[zipped_vals]",
				"{}",
				"{tab1={vals=[[@7,26:26='a',<455>,1:26]]}}",
				"{query0={zipped_vals=[[@12,37:47='zipped_vals',<455>,1:37]]}}",
				"{def_query0={query_dictionary={zipped_vals=[[@12,37:47='zipped_vals',<455>,1:37]]}, table_dictionary={tab1={vals=[[@7,26:26='a',<455>,1:26]]}}, interface={zipped_vals=[{name=tags, table_ref=z}, {name=vals, table_ref=a}]}, table_alias={a=tab1}}}");
	}

	@Test
	public void snowflakeSplitToArrayWherePlacementV0Test() {
		final String query = "SELECT a.id FROM tab1 a WHERE split_to_array(a.tags, a.sep) IS NOT NULL";
		final SqlParseEventWalker extractor = runQuery(query);
		final Snippet snippet = extractor.getSnippet();
		assertDiagnosticAtPosition(
				snippet,
				"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
				ParseDiagnostic.Severity.WARNING,
				"routine 'split_to_array' at (l:1 c:30) is recognized as Snowflake-specific grammar.",
				null,
				1,
				30);
		assertNoFatalErrors(extractor);
		assertOutputs(
				extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}}, from={table={alias=a, table=tab1}}, where={condition={left={function={function_name=split_to_array, parameters={1={column={name=tags, table_ref=a}}, 2={column={name=sep, table_ref=a}}}}}, operator=IS NOT NULL}}}}",
				"[id]",
				"{}",
				"{tab1={id=[[@1,7:7='a',<455>,1:7]], tags=[[@10,45:45='a',<455>,1:45]], sep=[[@14,53:53='a',<455>,1:53]]}}",
				"{query0={id=[[@3,9:10='id',<455>,1:9]]}}",
				"{def_query0={query_dictionary={id=[[@3,9:10='id',<455>,1:9]]}, table_dictionary={tab1={id=[[@1,7:7='a',<455>,1:7]], tags=[[@10,45:45='a',<455>,1:45]], sep=[[@14,53:53='a',<455>,1:53]]}}, filters=[{name=tags, table_ref=a}, {name=sep, table_ref=a}], interface={id=[{name=id, table_ref=a}]}, table_alias={a=tab1}}}");
	}

	@Test
	public void snowflakeSplitToArrayOrderByPlacementV0Test() {
		final String query = "SELECT a.id FROM tab1 a ORDER BY split_to_array(a.tags, a.sep)";
		final SqlParseEventWalker extractor = runQuery(query);
		final Snippet snippet = extractor.getSnippet();
		assertDiagnosticAtPosition(
				snippet,
				"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
				ParseDiagnostic.Severity.WARNING,
				"routine 'split_to_array' at (l:1 c:33) is recognized as Snowflake-specific grammar.",
				null,
				1,
				33);
		assertNoFatalErrors(extractor);
		assertOutputs(
				extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}}, orderby={1={null_order=null, predicand={function={function_name=split_to_array, parameters={1={column={name=tags, table_ref=a}}, 2={column={name=sep, table_ref=a}}}}}, sort_order=ASC}}, from={table={alias=a, table=tab1}}}}",
				"[id]",
				"{}",
				"{tab1={id=[[@1,7:7='a',<455>,1:7]], tags=[[@11,48:48='a',<455>,1:48]], sep=[[@15,56:56='a',<455>,1:56]]}}",
				"{query0={id=[[@3,9:10='id',<455>,1:9]]}}",
				"{def_query0={query_dictionary={id=[[@3,9:10='id',<455>,1:9]]}, table_dictionary={tab1={id=[[@1,7:7='a',<455>,1:7]], tags=[[@11,48:48='a',<455>,1:48]], sep=[[@15,56:56='a',<455>,1:56]]}}, ordered_by=[{name=tags, table_ref=a}, {name=sep, table_ref=a}], interface={id=[{name=id, table_ref=a}]}, table_alias={a=tab1}}}");
	}

	@Test
	public void snowflakeSplitToArrayValuesPlacementV0Test() {
		final String query = "VALUES (split_to_array(a.tags, a.sep))";
		final SqlParseEventWalker extractor = runQuery(query);
		final Snippet snippet = extractor.getSnippet();
		assertDiagnosticAtPosition(
				snippet,
				"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
				ParseDiagnostic.Severity.WARNING,
				"routine 'split_to_array' at (l:1 c:8) is recognized as Snowflake-specific grammar.",
				null,
				1,
				8);
		assertNoFatalErrors(extractor);
		assertOutputs(
				extractor,
				"{SQL={values={matrix={1={row={1={function={function_name=split_to_array, parameters={1={column={name=tags, table_ref=a}}, 2={column={name=sep, table_ref=a}}}}}}}}}}}",
				"[$1]",
				"{}",
				"{}",
				"{values0={$1=[[@1,7:7='(',<287>,1:7]]}}",
				"{def_values0={query_dictionary={$1=[[@1,7:7='(',<287>,1:7]]}, interface={$1=[]}}}");
	}

	@Test
	public void snowflakeSplitToArrayInsertPlacementV0Test() {
		final String query = "INSERT INTO tab_out(split_vals) SELECT split_to_array(a.tags, a.sep) FROM tab1 a";
		final SqlParseEventWalker extractor = runQuery(query);
		final Snippet snippet = extractor.getSnippet();
		assertDiagnosticAtPosition(
				snippet,
				"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
				ParseDiagnostic.Severity.WARNING,
				"routine 'split_to_array' at (l:1 c:39) is recognized as Snowflake-specific grammar.",
				null,
				1,
				39);
		assertNoFatalErrors(extractor);
		assertOutputs(
				extractor,
				"{SQL={insert={preamble=insert_into, from={from={table={alias=a, table=tab1}}, select={1={function={function_name=split_to_array, parameters={1={column={name=tags, table_ref=a}}, 2={column={name=sep, table_ref=a}}}}}}}, target_table={table={alias=null, table=tab_out}}, columns={1={column={name=split_vals, table_ref=null}}}}}}",
				"[split_vals]",
				"{}",
				"{tab1={tags=[[@9,54:54='a',<455>,1:54]], sep=[[@13,62:62='a',<455>,1:62]]}, tab_out={split_vals=[[@4,20:29='split_vals',<455>,1:20]]}}",
				"{query0={unnamed_0=[[@16,67:67=')',<288>,1:67]]}, insert1={split_vals=[[@4,20:29='split_vals',<455>,1:20]]}}",
				"{def_insert1={query_dictionary={split_vals=[[@4,20:29='split_vals',<455>,1:20]]}, table_dictionary={tab_out={split_vals=[[@4,20:29='split_vals',<455>,1:20]]}}, def_query0={query_dictionary={unnamed_0=[[@16,67:67=')',<288>,1:67]]}, table_dictionary={tab1={tags=[[@9,54:54='a',<455>,1:54]], sep=[[@13,62:62='a',<455>,1:62]]}}, interface={unnamed_0=[{name=tags, table_ref=a}, {name=sep, table_ref=a}]}, table_alias={a=tab1}}, interface={split_vals=[{name=unnamed_0, table_ref=query0}]}}}");
	}

	@Test
	public void snowflakeSplitToArrayUpdatePlacementV0Test() {
		final String query = "UPDATE tab1 SET split_vals = split_to_array(tags, sep)";
		final SqlParseEventWalker extractor = runQuery(query);
		final Snippet snippet = extractor.getSnippet();
		assertDiagnosticAtPosition(
				snippet,
				"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
				ParseDiagnostic.Severity.WARNING,
				"routine 'split_to_array' at (l:1 c:29) is recognized as Snowflake-specific grammar.",
				null,
				1,
				29);
		assertNoFatalErrors(extractor);
		assertOutputs(
				extractor,
				"{SQL={update={table={alias=null, table=tab1}, assignments={1={set={column={name=split_vals, table_ref=null}}, to={function={function_name=split_to_array, parameters={1={column={name=tags, table_ref=null}}, 2={column={name=sep, table_ref=null}}}}}}}}}}",
				"[split_vals]",
				"{}",
				"{tab1={tags=[[@7,44:47='tags',<455>,1:44], [@10,53:53=')',<288>,1:53]], sep=[[@9,50:52='sep',<455>,1:50], [@10,53:53=')',<288>,1:53]]}}",
				"{update0={split_vals=[[@3,16:25='split_vals',<455>,1:16]]}}",
				"{def_update0={assignments={split_vals=[{name=tags, table_ref=tab1}, {name=sep, table_ref=tab1}]}, table_dictionary={tab1={tags=[[@7,44:47='tags',<455>,1:44], [@10,53:53=')',<288>,1:53]], sep=[[@9,50:52='sep',<455>,1:50], [@10,53:53=')',<288>,1:53]]}}, update_dictionary={split_vals=[[@3,16:25='split_vals',<455>,1:16]]}, target_table={tab1={split_vals=[[@3,16:25='split_vals',<455>,1:16]]}}, lhs_unresolved_columns={split_vals={column={name=split_vals, table_ref=null}, locations=[[@3,16:25='split_vals',<455>,1:16]]}}}}");
	}

	@Test
	public void snowflakeSplitToArrayDeletePlacementV0Test() {
		final String query = "DELETE FROM tab1 WHERE split_to_array(tags, sep) IS NOT NULL";
		final SqlParseEventWalker extractor = runQuery(query);
		final Snippet snippet = extractor.getSnippet();
		assertDiagnosticAtPosition(
				snippet,
				"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
				ParseDiagnostic.Severity.WARNING,
				"routine 'split_to_array' at (l:1 c:23) is recognized as Snowflake-specific grammar.",
				null,
				1,
				23);
		assertNoFatalErrors(extractor);
		assertOutputs(
				extractor,
				"{SQL={delete={table={alias=null, table=tab1}, where={condition={left={function={function_name=split_to_array, parameters={1={column={name=tags, table_ref=null}}, 2={column={name=sep, table_ref=null}}}}}, operator=IS NOT NULL}}}}}",
				"[]",
				"{}",
				"{tab1={tags=[[@6,38:41='tags',<455>,1:38]], sep=[[@8,44:46='sep',<455>,1:44]]}}",
				"{}",
				"{def_delete0={query_dictionary={}, table_dictionary={tab1={tags=[[@6,38:41='tags',<455>,1:38]], sep=[[@8,44:46='sep',<455>,1:44]]}}, filters=[{name=tags, table_ref=tab1}, {name=sep, table_ref=tab1}], interface=null}}");
	}

	@Test
	public void snowflakeArrayGenerateRangeWherePlacementV0Test() {
		final String query = "SELECT a.id FROM tab1 a WHERE array_generate_range(1, 5) IS NOT NULL";
		final SqlParseEventWalker extractor = runQuery(query);
		final Snippet snippet = extractor.getSnippet();
		assertDiagnosticAtPosition(
				snippet,
				"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
				ParseDiagnostic.Severity.WARNING,
				"routine 'array_generate_range' at (l:1 c:30) is recognized as Snowflake-specific grammar.",
				null,
				1,
				30);
		assertNoFatalErrors(extractor);
		assertOutputs(
				extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}}, from={table={alias=a, table=tab1}}, where={condition={left={function={function_name=array_generate_range, parameters={1={literal=1}, 2={literal=5}}}}, operator=IS NOT NULL}}}}",
				"[id]",
				"{}",
				"{tab1={id=[[@1,7:7='a',<455>,1:7]]}}",
				"{query0={id=[[@3,9:10='id',<455>,1:9]]}}",
				"{def_query0={query_dictionary={id=[[@3,9:10='id',<455>,1:9]]}, table_dictionary={tab1={id=[[@1,7:7='a',<455>,1:7]]}}, filters=[], interface={id=[{name=id, table_ref=a}]}, table_alias={a=tab1}}}");
	}

	@Test
	public void snowflakeArrayGenerateRangeOrderByPlacementV0Test() {
		final String query = "SELECT a.id FROM tab1 a ORDER BY array_generate_range(1, 5)";
		final SqlParseEventWalker extractor = runQuery(query);
		final Snippet snippet = extractor.getSnippet();
		assertDiagnosticAtPosition(
				snippet,
				"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
				ParseDiagnostic.Severity.WARNING,
				"routine 'array_generate_range' at (l:1 c:33) is recognized as Snowflake-specific grammar.",
				null,
				1,
				33);
		assertNoFatalErrors(extractor);
		assertOutputs(
				extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}}, orderby={1={null_order=null, predicand={function={function_name=array_generate_range, parameters={1={literal=1}, 2={literal=5}}}}, sort_order=ASC}}, from={table={alias=a, table=tab1}}}}",
				"[id]",
				"{}",
				"{tab1={id=[[@1,7:7='a',<455>,1:7]]}}",
				"{query0={id=[[@3,9:10='id',<455>,1:9]]}}",
				"{def_query0={query_dictionary={id=[[@3,9:10='id',<455>,1:9]]}, table_dictionary={tab1={id=[[@1,7:7='a',<455>,1:7]]}}, ordered_by=[], interface={id=[{name=id, table_ref=a}]}, table_alias={a=tab1}}}");
	}

	@Test
	public void snowflakeArrayGenerateRangeValuesPlacementV0Test() {
		final String query = "VALUES (array_generate_range(1, 5))";
		final SqlParseEventWalker extractor = runQuery(query);
		final Snippet snippet = extractor.getSnippet();
		assertDiagnosticAtPosition(
				snippet,
				"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
				ParseDiagnostic.Severity.WARNING,
				"routine 'array_generate_range' at (l:1 c:8) is recognized as Snowflake-specific grammar.",
				null,
				1,
				8);
		assertNoFatalErrors(extractor);
		assertOutputs(
				extractor,
				"{SQL={values={matrix={1={row={1={function={function_name=array_generate_range, parameters={1={literal=1}, 2={literal=5}}}}}}}}}}",
				"[$1]",
				"{}",
				"{}",
				"{values0={$1=[[@1,7:7='(',<287>,1:7]]}}",
				"{def_values0={query_dictionary={$1=[[@1,7:7='(',<287>,1:7]]}, interface={$1=[]}}}");
	}

	@Test
	public void snowflakeArrayGenerateRangeInsertPlacementV0Test() {
		final String query = "INSERT INTO tab_out(gen_vals) SELECT array_generate_range(1, 5) FROM tab1 a";
		final SqlParseEventWalker extractor = runQuery(query);
		final Snippet snippet = extractor.getSnippet();
		assertDiagnosticAtPosition(
				snippet,
				"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
				ParseDiagnostic.Severity.WARNING,
				"routine 'array_generate_range' at (l:1 c:37) is recognized as Snowflake-specific grammar.",
				null,
				1,
				37);
		assertNoFatalErrors(extractor);
		assertOutputs(
				extractor,
				"{SQL={insert={preamble=insert_into, from={from={table={alias=a, table=tab1}}, select={1={function={function_name=array_generate_range, parameters={1={literal=1}, 2={literal=5}}}}}}, target_table={table={alias=null, table=tab_out}}, columns={1={column={name=gen_vals, table_ref=null}}}}}}",
				"[gen_vals]",
				"{}",
				"{tab1={}, tab_out={gen_vals=[[@4,20:27='gen_vals',<455>,1:20]]}}",
				"{query0={unnamed_0=[[@12,62:62=')',<288>,1:62]]}, insert1={gen_vals=[[@4,20:27='gen_vals',<455>,1:20]]}}",
				"{def_insert1={query_dictionary={gen_vals=[[@4,20:27='gen_vals',<455>,1:20]]}, table_dictionary={tab_out={gen_vals=[[@4,20:27='gen_vals',<455>,1:20]]}}, def_query0={query_dictionary={unnamed_0=[[@12,62:62=')',<288>,1:62]]}, table_dictionary={tab1={}}, interface={unnamed_0=[]}, table_alias={a=tab1}}, interface={gen_vals=[{name=unnamed_0, table_ref=query0}]}}}");
	}

	@Test
	public void snowflakeArrayGenerateRangeUpdatePlacementV0Test() {
		final String query = "UPDATE tab1 SET gen_vals = array_generate_range(1, 5)";
		final SqlParseEventWalker extractor = runQuery(query);
		final Snippet snippet = extractor.getSnippet();
		assertNoFatalErrors(extractor);
		assertOutputs(
				extractor,
				"{SQL={update={table={alias=null, table=tab1}, assignments={1={set={column={name=gen_vals, table_ref=null}}, to={function={function_name=array_generate_range, parameters={1={literal=1}, 2={literal=5}}}}}}}}}",
				"[gen_vals]",
				"{}",
				"{tab1={}}",
				"{update0={gen_vals=[[@3,16:23='gen_vals',<455>,1:16]]}}",
				"{def_update0={assignments={gen_vals=[]}, table_dictionary={tab1={}}, update_dictionary={gen_vals=[[@3,16:23='gen_vals',<455>,1:16]]}, target_table={tab1={gen_vals=[[@3,16:23='gen_vals',<455>,1:16]]}}, lhs_unresolved_columns={gen_vals={column={name=gen_vals, table_ref=null}, locations=[[@3,16:23='gen_vals',<455>,1:16]]}}}}");
	}

	@Test
	public void snowflakeArrayGenerateRangeDeletePlacementV0Test() {
		final String query = "DELETE FROM tab1 WHERE array_generate_range(1, 5) IS NOT NULL";
		final SqlParseEventWalker extractor = runQuery(query);
		final Snippet snippet = extractor.getSnippet();
		assertDiagnosticAtPosition(
				snippet,
				"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
				ParseDiagnostic.Severity.WARNING,
				"routine 'array_generate_range' at (l:1 c:23) is recognized as Snowflake-specific grammar.",
				null,
				1,
				23);
		assertNoFatalErrors(extractor);
		assertOutputs(
				extractor,
				"{SQL={delete={table={alias=null, table=tab1}, where={condition={left={function={function_name=array_generate_range, parameters={1={literal=1}, 2={literal=5}}}}, operator=IS NOT NULL}}}}}",
				"[]",
				"{}",
				"{tab1={}}",
				"{}",
				"{def_delete0={query_dictionary={}, table_dictionary={tab1={}}, filters=[], interface=null}}");
	}

	@Test
	public void snowflakeArraysZipWherePlacementV0Test() {
		final String query = "SELECT a.id FROM tab1 a WHERE arrays_zip(a.tags, a.vals) IS NOT NULL";
		final SqlParseEventWalker extractor = runQuery(query);
		final Snippet snippet = extractor.getSnippet();
		assertDiagnosticAtPosition(
				snippet,
				"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
				ParseDiagnostic.Severity.WARNING,
				"routine 'arrays_zip' at (l:1 c:30) is recognized as Snowflake-specific grammar.",
				null,
				1,
				30);
		assertNoFatalErrors(extractor);
		assertOutputs(
				extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}}, from={table={alias=a, table=tab1}}, where={condition={left={function={function_name=arrays_zip, parameters={1={column={name=tags, table_ref=a}}, 2={column={name=vals, table_ref=a}}}}}, operator=IS NOT NULL}}}}",
				"[id]",
				"{}",
				"{tab1={vals=[[@14,49:49='a',<455>,1:49]], id=[[@1,7:7='a',<455>,1:7]], tags=[[@10,41:41='a',<455>,1:41]]}}",
				"{query0={id=[[@3,9:10='id',<455>,1:9]]}}",
				"{def_query0={query_dictionary={id=[[@3,9:10='id',<455>,1:9]]}, table_dictionary={tab1={vals=[[@14,49:49='a',<455>,1:49]], id=[[@1,7:7='a',<455>,1:7]], tags=[[@10,41:41='a',<455>,1:41]]}}, filters=[{name=tags, table_ref=a}, {name=vals, table_ref=a}], interface={id=[{name=id, table_ref=a}]}, table_alias={a=tab1}}}");
	}

	@Test
	public void snowflakeArraysZipOrderByPlacementV0Test() {
		final String query = "SELECT a.id FROM tab1 a ORDER BY arrays_zip(a.tags, a.vals)";
		final SqlParseEventWalker extractor = runQuery(query);
		final Snippet snippet = extractor.getSnippet();
		assertDiagnosticAtPosition(
				snippet,
				"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
				ParseDiagnostic.Severity.WARNING,
				"routine 'arrays_zip' at (l:1 c:33) is recognized as Snowflake-specific grammar.",
				null,
				1,
				33);
		assertNoFatalErrors(extractor);
		assertOutputs(
				extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}}, orderby={1={null_order=null, predicand={function={function_name=arrays_zip, parameters={1={column={name=tags, table_ref=a}}, 2={column={name=vals, table_ref=a}}}}}, sort_order=ASC}}, from={table={alias=a, table=tab1}}}}",
				"[id]",
				"{}",
				"{tab1={vals=[[@15,52:52='a',<455>,1:52]], id=[[@1,7:7='a',<455>,1:7]], tags=[[@11,44:44='a',<455>,1:44]]}}",
				"{query0={id=[[@3,9:10='id',<455>,1:9]]}}",
				"{def_query0={query_dictionary={id=[[@3,9:10='id',<455>,1:9]]}, table_dictionary={tab1={vals=[[@15,52:52='a',<455>,1:52]], id=[[@1,7:7='a',<455>,1:7]], tags=[[@11,44:44='a',<455>,1:44]]}}, ordered_by=[{name=tags, table_ref=a}, {name=vals, table_ref=a}], interface={id=[{name=id, table_ref=a}]}, table_alias={a=tab1}}}");
	}

	@Test
	public void snowflakeArraysZipValuesPlacementV0Test() {
		final String query = "VALUES (arrays_zip(a.tags, a.vals))";
		final SqlParseEventWalker extractor = runQuery(query);
		final Snippet snippet = extractor.getSnippet();
		assertDiagnosticAtPosition(
				snippet,
				"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
				ParseDiagnostic.Severity.WARNING,
				"routine 'arrays_zip' at (l:1 c:8) is recognized as Snowflake-specific grammar.",
				null,
				1,
				8);
		assertNoFatalErrors(extractor);
		assertOutputs(
				extractor,
				"{SQL={values={matrix={1={row={1={function={function_name=arrays_zip, parameters={1={column={name=tags, table_ref=a}}, 2={column={name=vals, table_ref=a}}}}}}}}}}}",
				"[$1]",
				"{}",
				"{}",
				"{values0={$1=[[@1,7:7='(',<287>,1:7]]}}",
				"{def_values0={query_dictionary={$1=[[@1,7:7='(',<287>,1:7]]}, interface={$1=[]}}}");
	}

	@Test
	public void snowflakeArraysZipInsertPlacementV0Test() {
		final String query = "INSERT INTO tab_out(zipped_vals) SELECT arrays_zip(a.tags, a.vals) FROM tab1 a";
		final SqlParseEventWalker extractor = runQuery(query);
		final Snippet snippet = extractor.getSnippet();
		assertDiagnosticAtPosition(
				snippet,
				"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
				ParseDiagnostic.Severity.WARNING,
				"routine 'arrays_zip' at (l:1 c:40) is recognized as Snowflake-specific grammar.",
				null,
				1,
				40);
		assertNoFatalErrors(extractor);
		assertOutputs(
				extractor,
				"{SQL={insert={preamble=insert_into, from={from={table={alias=a, table=tab1}}, select={1={function={function_name=arrays_zip, parameters={1={column={name=tags, table_ref=a}}, 2={column={name=vals, table_ref=a}}}}}}}, target_table={table={alias=null, table=tab_out}}, columns={1={column={name=zipped_vals, table_ref=null}}}}}}",
				"[zipped_vals]",
				"{}",
				"{tab1={vals=[[@13,59:59='a',<455>,1:59]], tags=[[@9,51:51='a',<455>,1:51]]}, tab_out={zipped_vals=[[@4,20:30='zipped_vals',<455>,1:20]]}}",
				"{query0={unnamed_0=[[@16,65:65=')',<288>,1:65]]}, insert1={zipped_vals=[[@4,20:30='zipped_vals',<455>,1:20]]}}",
				"{def_insert1={query_dictionary={zipped_vals=[[@4,20:30='zipped_vals',<455>,1:20]]}, table_dictionary={tab_out={zipped_vals=[[@4,20:30='zipped_vals',<455>,1:20]]}}, def_query0={query_dictionary={unnamed_0=[[@16,65:65=')',<288>,1:65]]}, table_dictionary={tab1={vals=[[@13,59:59='a',<455>,1:59]], tags=[[@9,51:51='a',<455>,1:51]]}}, interface={unnamed_0=[{name=tags, table_ref=a}, {name=vals, table_ref=a}]}, table_alias={a=tab1}}, interface={zipped_vals=[{name=unnamed_0, table_ref=query0}]}}}");
	}

	@Test
	public void snowflakeArraysZipUpdatePlacementV0Test() {
		final String query = "UPDATE tab1 SET zipped_vals = arrays_zip(tags, vals)";
		final SqlParseEventWalker extractor = runQuery(query);
		final Snippet snippet = extractor.getSnippet();
		assertNoFatalErrors(extractor);
		assertOutputs(
				extractor,
				"{SQL={update={table={alias=null, table=tab1}, assignments={1={set={column={name=zipped_vals, table_ref=null}}, to={function={function_name=arrays_zip, parameters={1={column={name=tags, table_ref=null}}, 2={column={name=vals, table_ref=null}}}}}}}}}}",
				"[zipped_vals]",
				"{}",
				"{tab1={vals=[[@9,47:50='vals',<455>,1:47], [@10,51:51=')',<288>,1:51]], tags=[[@7,41:44='tags',<455>,1:41], [@10,51:51=')',<288>,1:51]]}}",
				"{update0={zipped_vals=[[@3,16:26='zipped_vals',<455>,1:16]]}}",
				"{def_update0={assignments={zipped_vals=[{name=tags, table_ref=tab1}, {name=vals, table_ref=tab1}]}, table_dictionary={tab1={vals=[[@9,47:50='vals',<455>,1:47], [@10,51:51=')',<288>,1:51]], tags=[[@7,41:44='tags',<455>,1:41], [@10,51:51=')',<288>,1:51]]}}, update_dictionary={zipped_vals=[[@3,16:26='zipped_vals',<455>,1:16]]}, target_table={tab1={zipped_vals=[[@3,16:26='zipped_vals',<455>,1:16]]}}, lhs_unresolved_columns={zipped_vals={column={name=zipped_vals, table_ref=null}, locations=[[@3,16:26='zipped_vals',<455>,1:16]]}}}}");
	}

	@Test
	public void snowflakeArraysZipDeletePlacementV0Test() {
		final String query = "DELETE FROM tab1 WHERE arrays_zip(tags, vals) IS NOT NULL";
		final SqlParseEventWalker extractor = runQuery(query);
		final Snippet snippet = extractor.getSnippet();
		assertDiagnosticAtPosition(
				snippet,
				"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
				ParseDiagnostic.Severity.WARNING,
				"routine 'arrays_zip' at (l:1 c:23) is recognized as Snowflake-specific grammar.",
				null,
				1,
				23);
		assertNoFatalErrors(extractor);
		assertOutputs(
				extractor,
				"{SQL={delete={table={alias=null, table=tab1}, where={condition={left={function={function_name=arrays_zip, parameters={1={column={name=tags, table_ref=null}}, 2={column={name=vals, table_ref=null}}}}}, operator=IS NOT NULL}}}}}",
				"[]",
				"{}",
				"{tab1={vals=[[@8,40:43='vals',<455>,1:40]], tags=[[@6,34:37='tags',<455>,1:34]]}}",
				"{}",
				"{def_delete0={query_dictionary={}, table_dictionary={tab1={vals=[[@8,40:43='vals',<455>,1:40]], tags=[[@6,34:37='tags',<455>,1:34]]}}, filters=[{name=tags, table_ref=tab1}, {name=vals, table_ref=tab1}], interface=null}}");
	}

	@Test
	public void snowflakeStrtokToArraySelectSeedV0Test() {
		final String query = "SELECT strtok_to_array(a.payload, a.sep) AS strtok_vals FROM tab1 a";
		final SqlParseEventWalker extractor = runQuery(query);
		final Snippet snippet = extractor.getSnippet();
		assertDiagnosticAtPosition(
				snippet,
				"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
				ParseDiagnostic.Severity.WARNING,
				"routine 'strtok_to_array' at (l:1 c:7) is recognized as Snowflake-specific grammar.",
				null,
				1,
				7);
		assertNoFatalErrors(extractor);
		assertOutputs(
				extractor,
				"{SQL={select={1={function={function_name=strtok_to_array, parameters={1={column={name=payload, table_ref=a}}, 2={column={name=sep, table_ref=a}}}}, alias=strtok_vals}}, from={table={alias=a, table=tab1}}}}",
				"[strtok_vals]",
				"{}",
				"{tab1={payload=[[@3,23:23='a',<455>,1:23]], sep=[[@7,34:34='a',<455>,1:34]]}}",
				"{query0={strtok_vals=[[@12,44:54='strtok_vals',<455>,1:44]]}}",
				"{def_query0={query_dictionary={strtok_vals=[[@12,44:54='strtok_vals',<455>,1:44]]}, table_dictionary={tab1={payload=[[@3,23:23='a',<455>,1:23]], sep=[[@7,34:34='a',<455>,1:34]]}}, interface={strtok_vals=[{name=payload, table_ref=a}, {name=sep, table_ref=a}]}, table_alias={a=tab1}}}");
	}

	@Test
	public void snowflakeStrtokToArrayMissingAliasFatalV0Test() {
		final String query = "SELECT strtok_to_array(z.payload, a.sep) AS strtok_vals FROM tab1 a";
		final SqlParseEventWalker extractor = runQuery(query);
		final Snippet snippet = extractor.getSnippet();
		assertDiagnosticAtPosition(
				snippet,
				"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
				ParseDiagnostic.Severity.WARNING,
				"routine 'strtok_to_array' at (l:1 c:7) is recognized as Snowflake-specific grammar.",
				null,
				1,
				7);
		assertSingleMissingAliasFatal(extractor);
		assertOutputs(
				extractor,
				"{SQL={select={1={function={function_name=strtok_to_array, parameters={1={column={name=payload, table_ref=z}}, 2={column={name=sep, table_ref=a}}}}, alias=strtok_vals}}, from={table={alias=a, table=tab1}}}}",
				"[strtok_vals]",
				"{}",
				"{tab1={sep=[[@7,34:34='a',<455>,1:34]]}}",
				"{query0={strtok_vals=[[@12,44:54='strtok_vals',<455>,1:44]]}}",
				"{def_query0={query_dictionary={strtok_vals=[[@12,44:54='strtok_vals',<455>,1:44]]}, table_dictionary={tab1={sep=[[@7,34:34='a',<455>,1:34]]}}, interface={strtok_vals=[{name=payload, table_ref=z}, {name=sep, table_ref=a}]}, table_alias={a=tab1}}}");
	}

	@Test
	public void snowflakeStrtokToArrayWherePlacementV0Test() {
		final String query = "SELECT a.id FROM tab1 a WHERE strtok_to_array(a.tags, a.sep) IS NOT NULL";
		final SqlParseEventWalker extractor = runQuery(query);
		final Snippet snippet = extractor.getSnippet();
		assertDiagnosticAtPosition(
				snippet,
				"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
				ParseDiagnostic.Severity.WARNING,
				"routine 'strtok_to_array' at (l:1 c:30) is recognized as Snowflake-specific grammar.",
				null,
				1,
				30);
		assertNoFatalErrors(extractor);
		assertOutputs(
				extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}}, from={table={alias=a, table=tab1}}, where={condition={left={function={function_name=strtok_to_array, parameters={1={column={name=tags, table_ref=a}}, 2={column={name=sep, table_ref=a}}}}}, operator=IS NOT NULL}}}}",
				"[id]",
				"{}",
				"{tab1={id=[[@1,7:7='a',<455>,1:7]], tags=[[@10,46:46='a',<455>,1:46]], sep=[[@14,54:54='a',<455>,1:54]]}}",
				"{query0={id=[[@3,9:10='id',<455>,1:9]]}}",
				"{def_query0={query_dictionary={id=[[@3,9:10='id',<455>,1:9]]}, table_dictionary={tab1={id=[[@1,7:7='a',<455>,1:7]], tags=[[@10,46:46='a',<455>,1:46]], sep=[[@14,54:54='a',<455>,1:54]]}}, filters=[{name=tags, table_ref=a}, {name=sep, table_ref=a}], interface={id=[{name=id, table_ref=a}]}, table_alias={a=tab1}}}");
	}

	@Test
	public void snowflakeStrtokToArrayOrderByPlacementV0Test() {
		final String query = "SELECT a.id FROM tab1 a ORDER BY strtok_to_array(a.tags, a.sep)";
		final SqlParseEventWalker extractor = runQuery(query);
		final Snippet snippet = extractor.getSnippet();
		assertDiagnosticAtPosition(
				snippet,
				"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
				ParseDiagnostic.Severity.WARNING,
				"routine 'strtok_to_array' at (l:1 c:33) is recognized as Snowflake-specific grammar.",
				null,
				1,
				33);
		assertNoFatalErrors(extractor);
		assertOutputs(
				extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}}, orderby={1={null_order=null, predicand={function={function_name=strtok_to_array, parameters={1={column={name=tags, table_ref=a}}, 2={column={name=sep, table_ref=a}}}}}, sort_order=ASC}}, from={table={alias=a, table=tab1}}}}",
				"[id]",
				"{}",
				"{tab1={id=[[@1,7:7='a',<455>,1:7]], tags=[[@11,49:49='a',<455>,1:49]], sep=[[@15,57:57='a',<455>,1:57]]}}",
				"{query0={id=[[@3,9:10='id',<455>,1:9]]}}",
				"{def_query0={query_dictionary={id=[[@3,9:10='id',<455>,1:9]]}, table_dictionary={tab1={id=[[@1,7:7='a',<455>,1:7]], tags=[[@11,49:49='a',<455>,1:49]], sep=[[@15,57:57='a',<455>,1:57]]}}, ordered_by=[{name=tags, table_ref=a}, {name=sep, table_ref=a}], interface={id=[{name=id, table_ref=a}]}, table_alias={a=tab1}}}");
	}

	@Test
	public void snowflakeStrtokToArrayValuesPlacementV0Test() {
		final String query = "VALUES (strtok_to_array(a.tags, a.sep))";
		final SqlParseEventWalker extractor = runQuery(query);
		final Snippet snippet = extractor.getSnippet();
		assertDiagnosticAtPosition(
				snippet,
				"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
				ParseDiagnostic.Severity.WARNING,
				"routine 'strtok_to_array' at (l:1 c:8) is recognized as Snowflake-specific grammar.",
				null,
				1,
				8);
		assertNoFatalErrors(extractor);
		assertOutputs(
				extractor,
				"{SQL={values={matrix={1={row={1={function={function_name=strtok_to_array, parameters={1={column={name=tags, table_ref=a}}, 2={column={name=sep, table_ref=a}}}}}}}}}}}",
				"[$1]",
				"{}",
				"{}",
				"{values0={$1=[[@1,7:7='(',<287>,1:7]]}}",
				"{def_values0={query_dictionary={$1=[[@1,7:7='(',<287>,1:7]]}, interface={$1=[]}}}");
	}

	@Test
	public void snowflakeStrtokToArrayInsertPlacementV0Test() {
		final String query = "INSERT INTO tab_out(tok_vals) SELECT strtok_to_array(a.tags, a.sep) FROM tab1 a";
		final SqlParseEventWalker extractor = runQuery(query);
		final Snippet snippet = extractor.getSnippet();
		assertDiagnosticAtPosition(
				snippet,
				"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
				ParseDiagnostic.Severity.WARNING,
				"routine 'strtok_to_array' at (l:1 c:37) is recognized as Snowflake-specific grammar.",
				null,
				1,
				37);
		assertNoFatalErrors(extractor);
		assertOutputs(
				extractor,
				"{SQL={insert={preamble=insert_into, from={from={table={alias=a, table=tab1}}, select={1={function={function_name=strtok_to_array, parameters={1={column={name=tags, table_ref=a}}, 2={column={name=sep, table_ref=a}}}}}}}, target_table={table={alias=null, table=tab_out}}, columns={1={column={name=tok_vals, table_ref=null}}}}}}",
				"[tok_vals]",
				"{}",
				"{tab1={tags=[[@9,53:53='a',<455>,1:53]], sep=[[@13,61:61='a',<455>,1:61]]}, tab_out={tok_vals=[[@4,20:27='tok_vals',<455>,1:20]]}}",
				"{query0={unnamed_0=[[@16,66:66=')',<288>,1:66]]}, insert1={tok_vals=[[@4,20:27='tok_vals',<455>,1:20]]}}",
				"{def_insert1={query_dictionary={tok_vals=[[@4,20:27='tok_vals',<455>,1:20]]}, table_dictionary={tab_out={tok_vals=[[@4,20:27='tok_vals',<455>,1:20]]}}, def_query0={query_dictionary={unnamed_0=[[@16,66:66=')',<288>,1:66]]}, table_dictionary={tab1={tags=[[@9,53:53='a',<455>,1:53]], sep=[[@13,61:61='a',<455>,1:61]]}}, interface={unnamed_0=[{name=tags, table_ref=a}, {name=sep, table_ref=a}]}, table_alias={a=tab1}}, interface={tok_vals=[{name=unnamed_0, table_ref=query0}]}}}");
	}

	@Test
	public void snowflakeStrtokToArrayUpdatePlacementV0Test() {
		final String query = "UPDATE tab1 SET tok_vals = strtok_to_array(tags, sep)";
		final SqlParseEventWalker extractor = runQuery(query);
		final Snippet snippet = extractor.getSnippet();
		assertNoFatalErrors(extractor);
		assertOutputs(
				extractor,
				"{SQL={update={table={alias=null, table=tab1}, assignments={1={set={column={name=tok_vals, table_ref=null}}, to={function={function_name=strtok_to_array, parameters={1={column={name=tags, table_ref=null}}, 2={column={name=sep, table_ref=null}}}}}}}}}}",
				"[tok_vals]",
				"{}",
				"{tab1={tags=[[@7,43:46='tags',<455>,1:43], [@10,52:52=')',<288>,1:52]], sep=[[@9,49:51='sep',<455>,1:49], [@10,52:52=')',<288>,1:52]]}}",
				"{update0={tok_vals=[[@3,16:23='tok_vals',<455>,1:16]]}}",
				"{def_update0={assignments={tok_vals=[{name=tags, table_ref=tab1}, {name=sep, table_ref=tab1}]}, table_dictionary={tab1={tags=[[@7,43:46='tags',<455>,1:43], [@10,52:52=')',<288>,1:52]], sep=[[@9,49:51='sep',<455>,1:49], [@10,52:52=')',<288>,1:52]]}}, update_dictionary={tok_vals=[[@3,16:23='tok_vals',<455>,1:16]]}, target_table={tab1={tok_vals=[[@3,16:23='tok_vals',<455>,1:16]]}}, lhs_unresolved_columns={tok_vals={column={name=tok_vals, table_ref=null}, locations=[[@3,16:23='tok_vals',<455>,1:16]]}}}}");
	}

	@Test
	public void snowflakeStrtokToArrayDeletePlacementV0Test() {
		final String query = "DELETE FROM tab1 WHERE strtok_to_array(tags, sep) IS NOT NULL";
		final SqlParseEventWalker extractor = runQuery(query);
		final Snippet snippet = extractor.getSnippet();
		assertDiagnosticAtPosition(
				snippet,
				"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
				ParseDiagnostic.Severity.WARNING,
				"routine 'strtok_to_array' at (l:1 c:23) is recognized as Snowflake-specific grammar.",
				null,
				1,
				23);
		assertNoFatalErrors(extractor);
		assertOutputs(
				extractor,
				"{SQL={delete={table={alias=null, table=tab1}, where={condition={left={function={function_name=strtok_to_array, parameters={1={column={name=tags, table_ref=null}}, 2={column={name=sep, table_ref=null}}}}}, operator=IS NOT NULL}}}}}",
				"[]",
				"{}",
				"{tab1={tags=[[@6,39:42='tags',<455>,1:39]], sep=[[@8,45:47='sep',<455>,1:45]]}}",
				"{}",
				"{def_delete0={query_dictionary={}, table_dictionary={tab1={tags=[[@6,39:42='tags',<455>,1:39]], sep=[[@8,45:47='sep',<455>,1:45]]}}, filters=[{name=tags, table_ref=tab1}, {name=sep, table_ref=tab1}], interface=null}}");
	}

	@Test
	public void arC38_arraySortSelectPlacementV0Test() {
		final String query = "SELECT a.id, array_sort(a.tags) AS result_tags FROM events a WHERE a.active = 1";
		final SqlParseEventWalker extractor = runUnaryQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_sort' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null,
						true, "ast-walk", null, null));
		assertOutputs(
				extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={function_name=array_sort, parameters={1={column={name=tags, table_ref=a}}}}, alias=result_tags}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_tags, id]",
				"{}",
				"{events={active=[[@17,67:67='a',<455>,1:67]], id=[[@1,7:7='a',<455>,1:7]], tags=[[@7,24:24='a',<455>,1:24]]}}",
				"{query0={result_tags=[[@12,35:45='result_tags',<455>,1:35]], id=[[@3,9:10='id',<455>,1:9]]}}",
				"{def_query0={query_dictionary={result_tags=[[@12,35:45='result_tags',<455>,1:35]], id=[[@3,9:10='id',<455>,1:9]]}, table_dictionary={events={active=[[@17,67:67='a',<455>,1:67]], id=[[@1,7:7='a',<455>,1:7]], tags=[[@7,24:24='a',<455>,1:24]]}}, filters=[{name=active, table_ref=a}], interface={result_tags=[{name=tags, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC38_arraySortWherePlacementV0Test() {
		final String query = "SELECT a.id FROM events a WHERE array_sort(a.tags) IS NOT NULL AND a.active = 1";
		final SqlParseEventWalker extractor = runUnaryQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_sort' at (l:1 c:32) is recognized as Snowflake-specific grammar.",
						1, 32, "SqlASTWalkerHelper", null, null,
						true, "ast-walk", null, null));
		assertOutputs(
				extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}}, from={table={alias=a, table=events}}, where={and={1={condition={left={function={function_name=array_sort, parameters={1={column={name=tags, table_ref=a}}}}}, operator=IS NOT NULL}}, 2={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}}}",
				"[id]",
				"{}",
				"{events={active=[[@18,67:67='a',<455>,1:67]], id=[[@1,7:7='a',<455>,1:7]], tags=[[@10,43:43='a',<455>,1:43]]}}",
				"{query0={id=[[@3,9:10='id',<455>,1:9]]}}",
				"{def_query0={query_dictionary={id=[[@3,9:10='id',<455>,1:9]]}, table_dictionary={events={active=[[@18,67:67='a',<455>,1:67]], id=[[@1,7:7='a',<455>,1:7]], tags=[[@10,43:43='a',<455>,1:43]]}}, filters=[{name=tags, table_ref=a}, {name=active, table_ref=a}], interface={id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC38_arraySortJoinOnPlacementV0Test() {
		final String query = "SELECT a.id, b.id AS other_id FROM events a JOIN archived_events b ON array_sort(a.tags) = b.tags AND a.id = b.id";
		final SqlParseEventWalker extractor = runUnaryQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_sort' at (l:1 c:70) is recognized as Snowflake-specific grammar.",
						1, 70, "SqlASTWalkerHelper", null, null,
						true, "ast-walk", null, null));
		assertOutputs(
				extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={column={name=id, table_ref=b}, alias=other_id}}, from={join={1={table={alias=a, table=events}}, 2={join=JOIN, on={and={1={condition={left={function={function_name=array_sort, parameters={1={column={name=tags, table_ref=a}}}}}, right={column={name=tags, table_ref=b}}, operator==}}, 2={condition={left={column={name=id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}}}, 3={table={alias=b, table=archived_events}}}}}}",
				"[id, other_id]",
				"{}",
				"{archived_events={id=[[@5,13:13='b',<455>,1:13], [@32,109:109='b',<455>,1:109]], tags=[[@24,91:91='b',<455>,1:91]]}, events={id=[[@1,7:7='a',<455>,1:7], [@28,102:102='a',<455>,1:102]], tags=[[@19,81:81='a',<455>,1:81]]}}",
				"{query0={id=[[@3,9:10='id',<455>,1:9]], other_id=[[@9,21:28='other_id',<455>,1:21]]}}",
				"{def_query0={query_dictionary={id=[[@3,9:10='id',<455>,1:9]], other_id=[[@9,21:28='other_id',<455>,1:21]]}, table_dictionary={archived_events={id=[[@5,13:13='b',<455>,1:13], [@32,109:109='b',<455>,1:109]], tags=[[@24,91:91='b',<455>,1:91]]}, events={id=[[@1,7:7='a',<455>,1:7], [@28,102:102='a',<455>,1:102]], tags=[[@19,81:81='a',<455>,1:81]]}}, filters=[{name=tags, table_ref=a}, {name=tags, table_ref=b}, {name=id, table_ref=a}, {name=id, table_ref=b}], interface={id=[{name=id, table_ref=a}], other_id=[{name=id, table_ref=b}]}, table_alias={a=events, b=archived_events}}}");
	}

	@Test
	public void arC38_arraySortHavingPlacementV0Test() {
		final String query = "SELECT a.group_id, count(*) AS n FROM events a GROUP BY a.group_id HAVING array_sort(array_agg(a.tags)) IS NOT NULL AND count(*) > 1";
		final SqlParseEventWalker extractor = runUnaryQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_sort' at (l:1 c:74) is recognized as Snowflake-specific grammar.",
						1, 74, "SqlASTWalkerHelper", null, null,
						true, "ast-walk", null, null));
		assertOutputs(
				extractor,
				"{SQL={select={1={column={name=group_id, table_ref=a}}, 2={function={function_name=COUNT, qualifier=null, parameters=*}, alias=n}}, having={and={1={condition={left={function={function_name=array_sort, parameters={1={function={function_name=array_agg, qualifier=null, parameters={column={name=tags, table_ref=a}}}}}}}, operator=IS NOT NULL}}, 2={condition={left={function={function_name=COUNT, qualifier=null, parameters=*}}, right={literal=1}, operator=>}}}}, from={table={alias=a, table=events}}, groupby={1={column={name=group_id, table_ref=a}}}}}",
				"[group_id, n]",
				"{}",
				"{events={group_id=[[@1,7:7='a',<455>,1:7], [@16,56:56='a',<455>,1:56]], *=[[@7,25:25='*',<291>,1:25], [@35,126:126='*',<291>,1:126]], tags=[[@24,95:95='a',<455>,1:95]]}}",
				"{query0={group_id=[[@3,9:16='group_id',<455>,1:9]], n=[[@10,31:31='n',<455>,1:31]]}}",
				"{def_query0={query_dictionary={group_id=[[@3,9:16='group_id',<455>,1:9]], n=[[@10,31:31='n',<455>,1:31]]}, table_dictionary={events={group_id=[[@1,7:7='a',<455>,1:7], [@16,56:56='a',<455>,1:56]], *=[[@7,25:25='*',<291>,1:25], [@35,126:126='*',<291>,1:126]], tags=[[@24,95:95='a',<455>,1:95]]}}, grouped_by=[{name=group_id, table_ref=a}], filters=[{name=tags, table_ref=a}], interface={group_id=[{name=group_id, table_ref=a}], n=[]}, table_alias={a=events}}}");
	}

	@Test
	public void arC38_arraySortQualifyPlacementV0Test() {
		final String query = "SELECT a.id FROM events a QUALIFY array_sort(a.tags) IS NOT NULL AND a.id > 0";
		final SqlParseEventWalker extractor = runUnaryQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_sort' at (l:1 c:34) is recognized as Snowflake-specific grammar.",
						1, 34, "SqlASTWalkerHelper", null, null,
						true, "ast-walk", null, null));
		assertOutputs(
				extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}}, from={table={alias=a, table=events}}, qualify={and={1={condition={left={function={function_name=array_sort, parameters={1={column={name=tags, table_ref=a}}}}}, operator=IS NOT NULL}}, 2={condition={left={column={name=id, table_ref=a}}, right={literal=0}, operator=>}}}}}}",
				"[id]",
				"{}",
				"{events={id=[[@1,7:7='a',<455>,1:7], [@18,69:69='a',<455>,1:69]], tags=[[@10,45:45='a',<455>,1:45]]}}",
				"{query0={id=[[@3,9:10='id',<455>,1:9]]}}",
				"{def_query0={query_dictionary={id=[[@3,9:10='id',<455>,1:9]]}, table_dictionary={events={id=[[@1,7:7='a',<455>,1:7], [@18,69:69='a',<455>,1:69]], tags=[[@10,45:45='a',<455>,1:45]]}}, filters=[{name=tags, table_ref=a}, {name=id, table_ref=a}], interface={id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC38_arraySortOrderByPlacementV0Test() {
		final String query = "SELECT a.id FROM events a WHERE a.active = 1 ORDER BY array_sort(a.tags), a.id";
		final SqlParseEventWalker extractor = runUnaryQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_sort' at (l:1 c:54) is recognized as Snowflake-specific grammar.",
						1, 54, "SqlASTWalkerHelper", null, null,
						true, "ast-walk", null, null));
		assertOutputs(
				extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}}, orderby={1={null_order=null, predicand={function={function_name=array_sort, parameters={1={column={name=tags, table_ref=a}}}}}, sort_order=ASC}, 2={null_order=null, predicand={column={name=id, table_ref=a}}, sort_order=ASC}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[id]",
				"{}",
				"{events={active=[[@8,32:32='a',<455>,1:32]], id=[[@1,7:7='a',<455>,1:7], [@22,74:74='a',<455>,1:74]], tags=[[@17,65:65='a',<455>,1:65]]}}",
				"{query0={id=[[@3,9:10='id',<455>,1:9]]}}",
				"{def_query0={query_dictionary={id=[[@3,9:10='id',<455>,1:9]]}, table_dictionary={events={active=[[@8,32:32='a',<455>,1:32]], id=[[@1,7:7='a',<455>,1:7], [@22,74:74='a',<455>,1:74]], tags=[[@17,65:65='a',<455>,1:65]]}}, ordered_by=[{name=tags, table_ref=a}, {name=id, table_ref=a}], filters=[{name=active, table_ref=a}], interface={id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC38_arraySortValuesPlacementV0Test() {
		final String query = "VALUES (array_sort(<tags_expr>), 7)";
		final SqlParseEventWalker extractor = runUnaryQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_sort' at (l:1 c:8) is recognized as Snowflake-specific grammar.",
						1, 8, "SqlASTWalkerHelper", null, null,
						true, "ast-walk", null, null));
		assertOutputs(
				extractor,
				"{SQL={values={matrix={1={row={1={function={function_name=array_sort, parameters={1={substitution={name=<tags_expr>, type=predicand}}}}}, 2={literal=7}}}}}}}",
				"[$1, $2]",
				"{<tags_expr>=predicand}",
				"{}",
				"{values0={$1=[[@1,7:7='(',<287>,1:7]], $2=[[@1,7:7='(',<287>,1:7]]}}",
				"{def_values0={query_dictionary={$1=[[@1,7:7='(',<287>,1:7]], $2=[[@1,7:7='(',<287>,1:7]]}, interface={$1=[], $2=[]}}}");
	}

	@Test
	public void arC38_arraySortInsertPlacementV0Test() {
		final String query = "INSERT INTO archived_events (tags, id) SELECT array_sort(a.tags), a.id FROM events a WHERE a.active = 1";
		final SqlParseEventWalker extractor = runUnaryQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_sort' at (l:1 c:46) is recognized as Snowflake-specific grammar.",
						1, 46, "SqlASTWalkerHelper", null, null,
						true, "ast-walk", null, null));
		assertOutputs(
				extractor,
				"{SQL={insert={preamble=insert_into, from={from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}, select={1={function={function_name=array_sort, parameters={1={column={name=tags, table_ref=a}}}}}, 2={column={name=id, table_ref=a}}}}, target_table={table={alias=null, table=archived_events}}, columns={1={column={name=tags, table_ref=null}}, 2={column={name=id, table_ref=null}}}}}}",
				"[id, tags]",
				"{}",
				"{archived_events={id=[[@6,35:36='id',<455>,1:35]], tags=[[@4,29:32='tags',<455>,1:29]]}, events={active=[[@23,91:91='a',<455>,1:91]], id=[[@16,66:66='a',<455>,1:66]], tags=[[@11,57:57='a',<455>,1:57]]}}",
				"{query0={unnamed_0=[[@14,63:63=')',<288>,1:63]], id=[[@18,68:69='id',<455>,1:68]]}, insert1={id=[[@6,35:36='id',<455>,1:35]], tags=[[@4,29:32='tags',<455>,1:29]]}}",
				"{def_insert1={query_dictionary={id=[[@6,35:36='id',<455>,1:35]], tags=[[@4,29:32='tags',<455>,1:29]]}, table_dictionary={archived_events={id=[[@6,35:36='id',<455>,1:35]], tags=[[@4,29:32='tags',<455>,1:29]]}}, def_query0={query_dictionary={unnamed_0=[[@14,63:63=')',<288>,1:63]], id=[[@18,68:69='id',<455>,1:68]]}, table_dictionary={events={active=[[@23,91:91='a',<455>,1:91]], id=[[@16,66:66='a',<455>,1:66]], tags=[[@11,57:57='a',<455>,1:57]]}}, filters=[{name=active, table_ref=a}], interface={unnamed_0=[{name=tags, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}, interface={tags=[{name=unnamed_0, table_ref=query0}], id=[{name=id, table_ref=query0}]}}}");
	}

	@Test
	public void arC38_arraySortUpdatePlacementV0Test() {
		final String query = "UPDATE events SET tags = array_sort(tags), active = 1 WHERE id > 0";
		final SqlParseEventWalker extractor = runUnaryQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_sort' at (l:1 c:25) is recognized as Snowflake-specific grammar.",
						1, 25, "SqlASTWalkerHelper", null, null,
						true, "ast-walk", null, null));
		assertOutputs(
				extractor,
				"{SQL={update={table={alias=null, table=events}, where={condition={left={column={name=id, table_ref=null}}, right={literal=0}, operator=>}}, assignments={1={set={column={name=tags, table_ref=null}}, to={function={function_name=array_sort, parameters={1={column={name=tags, table_ref=null}}}}}}, 2={set={column={name=active, table_ref=null}}, to={literal=1}}}}}}",
				"[active, tags]",
				"{}",
				"{events={id=[[@14,60:61='id',<455>,1:60]], tags=[[@7,36:39='tags',<455>,1:36]]}}",
				"{update0={active=[[@10,43:48='active',<455>,1:43]], tags=[[@3,18:21='tags',<455>,1:18]]}}",
				"{def_update0={assignments={active=[], tags=[{name=tags, table_ref=events}]}, table_dictionary={events={id=[[@14,60:61='id',<455>,1:60]], tags=[[@7,36:39='tags',<455>,1:36]]}}, update_dictionary={active=[[@10,43:48='active',<455>,1:43]], tags=[[@3,18:21='tags',<455>,1:18]]}, target_table={events={active=[[@10,43:48='active',<455>,1:43]], tags=[[@3,18:21='tags',<455>,1:18], [@7,36:39='tags',<455>,1:36], [@8,40:40=')',<288>,1:40]]}}, filters=[{name=id, table_ref=events}], lhs_unresolved_columns={active={column={name=active, table_ref=null}, locations=[[@10,43:48='active',<455>,1:43]]}, tags={column={name=tags, table_ref=null}, locations=[[@3,18:21='tags',<455>,1:18], [@7,36:39='tags',<455>,1:36], [@8,40:40=')',<288>,1:40]]}}}}");
	}

	@Test
	public void arC38_arraySortDeletePlacementV0Test() {
		final String query = "DELETE FROM events WHERE array_sort(tags) IS NULL AND id > 0";
		final SqlParseEventWalker extractor = runUnaryQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_sort' at (l:1 c:25) is recognized as Snowflake-specific grammar.",
						1, 25, "SqlASTWalkerHelper", null, null,
						true, "ast-walk", null, null));
		assertOutputs(
				extractor,
				"{SQL={delete={table={alias=null, table=events}, where={and={1={condition={left={function={function_name=array_sort, parameters={1={column={name=tags, table_ref=null}}}}}, operator=IS NULL}}, 2={condition={left={column={name=id, table_ref=null}}, right={literal=0}, operator=>}}}}}}}",
				"[]",
				"{}",
				"{events={id=[[@11,54:55='id',<455>,1:54]], tags=[[@6,36:39='tags',<455>,1:36]]}}",
				"{}",
				"{def_delete0={query_dictionary={}, table_dictionary={events={id=[[@11,54:55='id',<455>,1:54]], tags=[[@6,36:39='tags',<455>,1:36]]}}, filters=[{name=tags, table_ref=events}, {name=id, table_ref=events}], interface=null}}");
	}

	@Test
	public void arC38_arraySortDdlPlacementV0Test() {
		final String query = "CREATE VIEW v_arr AS SELECT a.id, array_sort(a.tags) AS result_tags FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SqlParseEventWalker extractor = runDdlParsertest(query, parser);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_sort' at (l:1 c:34) is recognized as Snowflake-specific grammar.",
						1, 34, "SqlASTWalkerHelper", null, null,
						true, "ast-walk", null, null));
		assertOutputs(
				extractor,
				"{DDL={create={type=VIEW, name={table=v_arr}, query={select={1={column={name=id, table_ref=a}}, 2={function={function_name=array_sort, parameters={1={column={name=tags, table_ref=a}}}}, alias=result_tags}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}}}",
				"[]",
				"{}",
				"{events={active=[[@21,88:88='a',<455>,1:88]], id=[[@5,28:28='a',<455>,1:28]], tags=[[@11,45:45='a',<455>,1:45]]}}",
				"{query0={result_tags=[[@16,56:66='result_tags',<455>,1:56]], id=[[@7,30:31='id',<455>,1:30]]}}",
				"{def_create1={def_query0={query_dictionary={result_tags=[[@16,56:66='result_tags',<455>,1:56]], id=[[@7,30:31='id',<455>,1:30]]}, table_dictionary={events={active=[[@21,88:88='a',<455>,1:88]], id=[[@5,28:28='a',<455>,1:28]], tags=[[@11,45:45='a',<455>,1:45]]}}, filters=[{name=active, table_ref=a}], interface={result_tags=[{name=tags, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}}");
	}

	@Test
	public void arR20_arraySortScriptStatementResetV0Test() {
		final String query = "SELECT a.id, array_sort(a.tags) AS result_tags FROM events a;\nSELECT b.id FROM archived_events b;\nSELECT c.id, array_sort(c.tags) AS result_tags FROM events c;";
		final SQLSelectParserParser parser = parse(query);
		final SqlParseEventWalker extractor = runScriptParsertest(query, parser);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_sort' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null,
						true, "ast-walk", null, null),
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_sort' at (l:3 c:13) is recognized as Snowflake-specific grammar.",
						3, 13, "SqlASTWalkerHelper", null, null,
						true, "ast-walk", null, null));
		assertOutputs(
				extractor,
				"{SCRIPT={1={select={1={column={name=id, table_ref=a}}, 2={function={function_name=array_sort, parameters={1={column={name=tags, table_ref=a}}}}, alias=result_tags}}, from={table={alias=a, table=events}}}, 2={select={1={column={name=id, table_ref=b}}}, from={table={alias=b, table=archived_events}}}, 3={select={1={column={name=id, table_ref=c}}, 2={function={function_name=array_sort, parameters={1={column={name=tags, table_ref=c}}}}, alias=result_tags}}, from={table={alias=c, table=events}}}}}",
				"[]",
				"{SCRIPT={1={}, 2={}, 3={}}}",
				"{SCRIPT={1={events={id=[[@1,7:7='a',<455>,1:7]], tags=[[@7,24:24='a',<455>,1:24]]}}, 2={archived_events={id=[[@18,69:69='b',<455>,2:7]]}}, 3={events={id=[[@26,105:105='c',<455>,3:7]], tags=[[@32,122:122='c',<455>,3:24]]}}}}",
				"{SCRIPT={1={query0={result_tags=[[@12,35:45='result_tags',<455>,1:35]], id=[[@3,9:10='id',<455>,1:9]]}}, 2={query0={id=[[@20,71:72='id',<455>,2:9]]}}, 3={query0={result_tags=[[@37,133:143='result_tags',<455>,3:35]], id=[[@28,107:108='id',<455>,3:9]]}}}}",
				"{SCRIPT={1={def_query0={query_dictionary={result_tags=[[@12,35:45='result_tags',<455>,1:35]], id=[[@3,9:10='id',<455>,1:9]]}, table_dictionary={events={id=[[@1,7:7='a',<455>,1:7]], tags=[[@7,24:24='a',<455>,1:24]]}}, interface={result_tags=[{name=tags, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}, 2={def_query0={query_dictionary={id=[[@20,71:72='id',<455>,2:9]]}, table_dictionary={archived_events={id=[[@18,69:69='b',<455>,2:7]]}}, interface={id=[{name=id, table_ref=b}]}, table_alias={b=archived_events}}}, 3={def_query0={query_dictionary={result_tags=[[@37,133:143='result_tags',<455>,3:35]], id=[[@28,107:108='id',<455>,3:9]]}, table_dictionary={events={id=[[@26,105:105='c',<455>,3:7]], tags=[[@32,122:122='c',<455>,3:24]]}}, interface={result_tags=[{name=tags, table_ref=c}], id=[{name=id, table_ref=c}]}, table_alias={c=events}}}}}");
	}

	@Test
	public void arR18_arraySortNestedExpressionV0Test() {
		final String query = "SELECT a.id, coalesce(array_sort(a.tags), a.backup_tags) AS result_tags FROM events a WHERE a.active = 1";
		final SqlParseEventWalker extractor = runUnaryQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_sort' at (l:1 c:22) is recognized as Snowflake-specific grammar.",
						1, 22, "SqlASTWalkerHelper", null, null,
						true, "ast-walk", null, null));
		assertOutputs(
				extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={parameters={1={function={function_name=array_sort, parameters={1={column={name=tags, table_ref=a}}}}}, 2={column={name=backup_tags, table_ref=a}}}, function_name=coalesce}, alias=result_tags}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_tags, id]",
				"{}",
				"{events={active=[[@24,92:92='a',<455>,1:92]], id=[[@1,7:7='a',<455>,1:7]], backup_tags=[[@14,42:42='a',<455>,1:42]], tags=[[@9,33:33='a',<455>,1:33]]}}",
				"{query0={result_tags=[[@19,60:70='result_tags',<455>,1:60]], id=[[@3,9:10='id',<455>,1:9]]}}",
				"{def_query0={query_dictionary={result_tags=[[@19,60:70='result_tags',<455>,1:60]], id=[[@3,9:10='id',<455>,1:9]]}, table_dictionary={events={active=[[@24,92:92='a',<455>,1:92]], id=[[@1,7:7='a',<455>,1:7]], backup_tags=[[@14,42:42='a',<455>,1:42]], tags=[[@9,33:33='a',<455>,1:33]]}}, filters=[{name=active, table_ref=a}], interface={result_tags=[{name=tags, table_ref=a}, {name=backup_tags, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arR18_arraySortDerivedOutputBoundaryV0Test() {
		final String query = "SELECT d.id, d.result_tags FROM (SELECT a.id, array_sort(a.tags) AS result_tags FROM events a) d WHERE d.id > 0";
		final SqlParseEventWalker extractor = runUnaryQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_sort' at (l:1 c:46) is recognized as Snowflake-specific grammar.",
						1, 46, "SqlASTWalkerHelper", null, null,
						true, "ast-walk", null, null));
		assertOutputs(
				extractor,
				"{SQL={select={1={column={name=id, table_ref=d}}, 2={column={name=result_tags, table_ref=d}}}, from={table={alias=d, query={select={1={column={name=id, table_ref=a}}, 2={function={function_name=array_sort, parameters={1={column={name=tags, table_ref=a}}}}, alias=result_tags}}, from={table={alias=a, table=events}}}}}, where={condition={left={column={name=id, table_ref=d}}, right={literal=0}, operator=>}}}}",
				"[result_tags, id]",
				"{}",
				"{events={id=[[@11,40:40='a',<455>,1:40]], tags=[[@17,57:57='a',<455>,1:57]]}}",
				"{query0={result_tags=[[@22,68:78='result_tags',<455>,1:68], [@5,13:13='d',<455>,1:13]], id=[[@13,42:43='id',<455>,1:42], [@1,7:7='d',<455>,1:7], [@29,103:103='d',<455>,1:103]]}, query1={result_tags=[[@7,15:25='result_tags',<455>,1:15]], id=[[@3,9:10='id',<455>,1:9]]}}",
				"{def_query1={query_dictionary={result_tags=[[@7,15:25='result_tags',<455>,1:15]], id=[[@3,9:10='id',<455>,1:9]]}, def_query0={query_dictionary={result_tags=[[@22,68:78='result_tags',<455>,1:68], [@5,13:13='d',<455>,1:13]], id=[[@13,42:43='id',<455>,1:42], [@1,7:7='d',<455>,1:7], [@29,103:103='d',<455>,1:103]]}, table_dictionary={events={id=[[@11,40:40='a',<455>,1:40]], tags=[[@17,57:57='a',<455>,1:57]]}}, interface={result_tags=[{name=tags, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}, filters=[{name=id, table_ref=d}], interface={result_tags=[{name=result_tags, table_ref=d}], id=[{name=id, table_ref=d}]}, table_alias={d=query0}}}");
	}

	@Test
	public void arR19_arraySortSetOpSiblingIsolationV0Test() {
		final String query = "SELECT a.id, array_sort(a.tags) AS result_tags FROM events a UNION ALL SELECT b.id, b.tags AS result_tags FROM archived_events b";
		final SqlParseEventWalker extractor = runUnaryQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_sort' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null,
						true, "ast-walk", null, null));
		assertOutputs(
				extractor,
				"{SQL={union={1={select={1={column={name=id, table_ref=a}}, 2={function={function_name=array_sort, parameters={1={column={name=tags, table_ref=a}}}}, alias=result_tags}}, from={table={alias=a, table=events}}}, 2={union={qualifier=ALL, operator=UNION}}, 3={select={1={column={name=id, table_ref=b}}, 2={column={name=tags, table_ref=b}, alias=result_tags}}, from={table={alias=b, table=archived_events}}}}}}",
				"[result_tags, id]",
				"{}",
				"{archived_events={id=[[@19,78:78='b',<455>,1:78]], tags=[[@23,84:84='b',<455>,1:84]]}, events={id=[[@1,7:7='a',<455>,1:7]], tags=[[@7,24:24='a',<455>,1:24]]}}",
				"{query0={result_tags=[[@12,35:45='result_tags',<455>,1:35]], id=[[@3,9:10='id',<455>,1:9]]}, query1={result_tags=[[@27,94:104='result_tags',<455>,1:94]], id=[[@21,80:81='id',<455>,1:80]]}}",
				"{def_union2={def_query1={query_dictionary={result_tags=[[@27,94:104='result_tags',<455>,1:94]], id=[[@21,80:81='id',<455>,1:80]]}, table_dictionary={archived_events={id=[[@19,78:78='b',<455>,1:78]], tags=[[@23,84:84='b',<455>,1:84]]}}, setop=UNION, interface={result_tags=[{name=tags, table_ref=b}], id=[{name=id, table_ref=b}]}, table_alias={b=archived_events}}, def_query0={query_dictionary={result_tags=[[@12,35:45='result_tags',<455>,1:35]], id=[[@3,9:10='id',<455>,1:9]]}, table_dictionary={events={id=[[@1,7:7='a',<455>,1:7]], tags=[[@7,24:24='a',<455>,1:24]]}}, interface={result_tags=[{name=tags, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}, interface={result_tags=query_column, id=query_column}}}");
	}

	@Test
	public void arC38_arraySortUnqualifiedOperandV0Test() {
		final String query = "SELECT id, array_sort(tags) AS result_tags FROM events WHERE active = 1";
		final SqlParseEventWalker extractor = runUnaryQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_sort' at (l:1 c:11) is recognized as Snowflake-specific grammar.",
						1, 11, "SqlASTWalkerHelper", null, null,
						true, "ast-walk", null, null));
		assertOutputs(
				extractor,
				"{SQL={select={1={column={name=id, table_ref=null}}, 2={function={function_name=array_sort, parameters={1={column={name=tags, table_ref=null}}}}, alias=result_tags}}, from={table={alias=null, table=events}}, where={condition={left={column={name=active, table_ref=null}}, right={literal=1}, operator==}}}}",
				"[result_tags, id]",
				"{}",
				"{events={active=[[@12,61:66='active',<455>,1:61]], id=[[@1,7:8='id',<455>,1:7]], tags=[[@5,22:25='tags',<455>,1:22]]}}",
				"{query0={result_tags=[[@8,31:41='result_tags',<455>,1:31]], id=[[@1,7:8='id',<455>,1:7]]}}",
				"{def_query0={query_dictionary={result_tags=[[@8,31:41='result_tags',<455>,1:31]], id=[[@1,7:8='id',<455>,1:7]]}, table_dictionary={events={active=[[@12,61:66='active',<455>,1:61]], id=[[@1,7:8='id',<455>,1:7]], tags=[[@5,22:25='tags',<455>,1:22]]}}, filters=[{name=active, table_ref=events}], interface={result_tags=[{name=tags, table_ref=events}], id=[{name=id, table_ref=events}]}}}");
	}

	@Test
	public void arM20_arraySortUnresolvedOperandV0Test() {
		final String query = "SELECT array_sort(missing_tags) AS result_tags";
		final SqlParseEventWalker extractor = runUnaryQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_sort' at (l:1 c:7) is recognized as Snowflake-specific grammar.",
						1, 7, "SqlASTWalkerHelper", null, null,
						true, "ast-walk", null, null),
				new ParseDiagnostic(ParseDiagnostic.Severity.ERROR,
						"UNRESOLVED_UNQUALIFIED_COLUMNS",
						"Unresolved unqualified column reference(s): [missing_tags [(l:1 c:18)]]",
						1, 18, "SqlASTWalkerHelper", null, "missing_tags",
						false, "ast-walk", null, null));
		assertOutputs(
				extractor,
				"{SQL={select={1={function={function_name=array_sort, parameters={1={column={name=missing_tags, table_ref=null}}}}, alias=result_tags}}}}",
				"[result_tags]",
				"{}",
				"{}",
				"{query0={result_tags=[[@6,35:45='result_tags',<455>,1:35]]}}",
				"{def_query0={query_dictionary={result_tags=[[@6,35:45='result_tags',<455>,1:35]]}, interface={result_tags=[{name=missing_tags, table_ref=null}]}}}");
	}

	@Test
	public void arM21_arraySortAmbiguousOperandV0Test() {
		final String query = "SELECT array_sort(tags) AS result_tags FROM events a JOIN archived_events b ON a.id = b.id";
		final SqlParseEventWalker extractor = runUnaryQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_sort' at (l:1 c:7) is recognized as Snowflake-specific grammar.",
						1, 7, "SqlASTWalkerHelper", null, null,
						true, "ast-walk", null, null),
				new ParseDiagnostic(ParseDiagnostic.Severity.SEVERE_WARNING,
						"AMBIGUOUS_COLUMN_REFERENCE",
						"Ambiguous column reference 'tags' at (l:1 c:18). Possible sources: [archived_events, events]",
						1, 18, "SqlASTWalkerHelper", null, "tags",
						true, "ast-walk", null, null),
				new ParseDiagnostic(ParseDiagnostic.Severity.ERROR,
						"UNRESOLVED_UNQUALIFIED_COLUMNS",
						"Unresolved unqualified column reference(s): [tags [(l:1 c:18)]]",
						1, 18, "SqlASTWalkerHelper", null, "tags",
						false, "ast-walk", null, null));
		assertOutputs(
				extractor,
				"{SQL={select={1={function={function_name=array_sort, parameters={1={column={name=tags, table_ref=null}}}}, alias=result_tags}}, from={join={1={table={alias=a, table=events}}, 2={join=JOIN, on={condition={left={column={name=id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=archived_events}}}}}}",
				"[result_tags]",
				"{}",
				"{archived_events={id=[[@18,86:86='b',<455>,1:86]]}, events={id=[[@14,79:79='a',<455>,1:79]]}}",
				"{query0={result_tags=[[@6,27:37='result_tags',<455>,1:27]]}}",
				"{def_query0={query_dictionary={result_tags=[[@6,27:37='result_tags',<455>,1:27]]}, table_dictionary={archived_events={id=[[@18,86:86='b',<455>,1:86]]}, events={id=[[@14,79:79='a',<455>,1:79]]}}, filters=[{name=id, table_ref=a}, {name=id, table_ref=b}], interface={result_tags=[{name=tags, table_ref=null}]}, table_alias={a=events, b=archived_events}}}");
	}

	@Test
	public void arM22_arraySortMissingAliasOperandV0Test() {
		final String query = "SELECT a.id, array_sort(z.tags) AS result_tags FROM events a WHERE a.active = 1";
		final SqlParseEventWalker extractor = runUnaryQuery(query);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_sort' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null,
						true, "ast-walk", null, null),
				new ParseDiagnostic(ParseDiagnostic.Severity.FATAL,
						"QUALIFIED_COLUMN_NOT_FOUND_IN_TABLE",
						"Source Table not found for Column 'tags' at (l:1 c:35). No alias or table called 'z'.",
						1, 35, "SqlASTWalkerHelper", null, "tags",
						false, "ast-walk", null, null));
		assertOutputs(
				extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={function_name=array_sort, parameters={1={column={name=tags, table_ref=z}}}}, alias=result_tags}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_tags, id]",
				"{}",
				"{events={active=[[@17,67:67='a',<455>,1:67]], id=[[@1,7:7='a',<455>,1:7]]}}",
				"{query0={result_tags=[[@12,35:45='result_tags',<455>,1:35]], id=[[@3,9:10='id',<455>,1:9]]}}",
				"{def_query0={query_dictionary={result_tags=[[@12,35:45='result_tags',<455>,1:35]], id=[[@3,9:10='id',<455>,1:9]]}, table_dictionary={events={active=[[@17,67:67='a',<455>,1:67]], id=[[@1,7:7='a',<455>,1:7]]}}, filters=[{name=active, table_ref=a}], interface={result_tags=[{name=tags, table_ref=z}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arM22_arraySortHiddenAliasOperandV0Test() {
		final String query = "SELECT d.id, array_sort(a.tags) AS result_tags FROM (SELECT a.id, a.tags FROM events a) d WHERE d.id > 0";
		final SqlParseEventWalker extractor = runUnaryQuery(query);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_sort' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null,
						true, "ast-walk", null, null),
				new ParseDiagnostic(ParseDiagnostic.Severity.FATAL,
						"QUALIFIED_COLUMN_NOT_FOUND_IN_TABLE",
						"Source Table not found for Column 'tags' at (l:1 c:35). No alias or table called 'a'.",
						1, 35, "SqlASTWalkerHelper", null, "tags",
						false, "ast-walk", null, null));
		assertOutputs(
				extractor,
				"{SQL={select={1={column={name=id, table_ref=d}}, 2={function={function_name=array_sort, parameters={1={column={name=tags, table_ref=a}}}}, alias=result_tags}}, from={table={alias=d, query={select={1={column={name=id, table_ref=a}}, 2={column={name=tags, table_ref=a}}}, from={table={alias=a, table=events}}}}}, where={condition={left={column={name=id, table_ref=d}}, right={literal=0}, operator=>}}}}",
				"[result_tags, id]",
				"{}",
				"{events={id=[[@16,60:60='a',<455>,1:60]], tags=[[@20,66:66='a',<455>,1:66]]}}",
				"{query0={id=[[@18,62:63='id',<455>,1:62], [@1,7:7='d',<455>,1:7], [@29,96:96='d',<455>,1:96]], tags=[[@22,68:71='tags',<455>,1:68]]}, query1={result_tags=[[@12,35:45='result_tags',<455>,1:35]], id=[[@3,9:10='id',<455>,1:9]]}}",
				"{def_query1={query_dictionary={result_tags=[[@12,35:45='result_tags',<455>,1:35]], id=[[@3,9:10='id',<455>,1:9]]}, table_dictionary={}, def_query0={query_dictionary={id=[[@18,62:63='id',<455>,1:62], [@1,7:7='d',<455>,1:7], [@29,96:96='d',<455>,1:96]], tags=[[@22,68:71='tags',<455>,1:68]]}, table_dictionary={events={id=[[@16,60:60='a',<455>,1:60]], tags=[[@20,66:66='a',<455>,1:66]]}}, interface={id=[{name=id, table_ref=a}], tags=[{name=tags, table_ref=a}]}, table_alias={a=events}}, filters=[{name=id, table_ref=d}], interface={result_tags=[{name=tags, table_ref=a}], id=[{name=id, table_ref=d}]}, table_alias={d=query0}}}");
	}

	@Test
	public void arC38_arraySortPredicandVariableV0Test() {
		final String query = "SELECT a.id, array_sort(<tags_expr>) AS result_tags FROM events a WHERE a.active = 1";
		final SqlParseEventWalker extractor = runUnaryQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_sort' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null,
						true, "ast-walk", null, null));
		assertOutputs(
				extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={function_name=array_sort, parameters={1={substitution={name=<tags_expr>, type=predicand}}}}, alias=result_tags}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_tags, id]",
				"{<tags_expr>=predicand}",
				"{events={active=[[@15,72:72='a',<455>,1:72]], id=[[@1,7:7='a',<455>,1:7]]}}",
				"{query0={result_tags=[[@10,40:50='result_tags',<455>,1:40]], id=[[@3,9:10='id',<455>,1:9]]}}",
				"{def_query0={query_dictionary={result_tags=[[@10,40:50='result_tags',<455>,1:40]], id=[[@3,9:10='id',<455>,1:9]]}, table_dictionary={events={active=[[@15,72:72='a',<455>,1:72]], id=[[@1,7:7='a',<455>,1:7]]}}, filters=[{name=active, table_ref=a}], interface={result_tags=[{name=<tags_expr>, type=predicand}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC38_arraySortQualifiedColumnVariableV0Test() {
		final String query = "SELECT a.id, array_sort(a.<tags_col>) AS result_tags FROM events a WHERE a.active = 1";
		final SqlParseEventWalker extractor = runUnaryQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_sort' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null,
						true, "ast-walk", null, null));
		assertOutputs(
				extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={function_name=array_sort, parameters={1={column={substitution={name=<tags_col>, type=column}, table_ref=a}}}}, alias=result_tags}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_tags, id]",
				"{<tags_col>=column}",
				"{events={active=[[@17,73:73='a',<455>,1:73]], id=[[@1,7:7='a',<455>,1:7]], <tags_col>=[[@7,24:24='a',<455>,1:24]]}}",
				"{query0={result_tags=[[@12,41:51='result_tags',<455>,1:41]], id=[[@3,9:10='id',<455>,1:9]]}}",
				"{def_query0={query_dictionary={result_tags=[[@12,41:51='result_tags',<455>,1:41]], id=[[@3,9:10='id',<455>,1:9]]}, table_dictionary={events={active=[[@17,73:73='a',<455>,1:73]], id=[[@1,7:7='a',<455>,1:7]], <tags_col>=[[@7,24:24='a',<455>,1:24]]}}, filters=[{name=active, table_ref=a}], interface={result_tags=[{substitution={name=<tags_col>, type=column}, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arM23_arraySortSchemaQualifiedRoutineControlV0Test() {
		final String query = "SELECT a.id, custom.array_sort(a.tags) AS result_tags FROM events a WHERE a.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(
				extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={schema=custom, parameters={1={column={name=tags, table_ref=a}}}, function_name=array_sort}, alias=result_tags}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_tags, id]",
				"{}",
				"{events={active=[[@19,74:74='a',<455>,1:74]], id=[[@1,7:7='a',<455>,1:7]], tags=[[@9,31:31='a',<455>,1:31]]}}",
				"{query0={result_tags=[[@14,42:52='result_tags',<455>,1:42]], id=[[@3,9:10='id',<455>,1:9]]}}",
				"{def_query0={query_dictionary={result_tags=[[@14,42:52='result_tags',<455>,1:42]], id=[[@3,9:10='id',<455>,1:9]]}, table_dictionary={events={active=[[@19,74:74='a',<455>,1:74]], id=[[@1,7:7='a',<455>,1:7]], tags=[[@9,31:31='a',<455>,1:31]]}}, filters=[{name=active, table_ref=a}], interface={result_tags=[{name=tags, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arM24_arraySortMixedDialectControlV0Test() {
		final String query = "SELECT array_sort(a.tags) AS result_tags, extract(isodow from a.created_at) AS weekday FROM events a";
		final SqlParseEventWalker extractor = runUnaryQuery(query);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_sort' at (l:1 c:7) is recognized as Snowflake-specific grammar.",
						1, 7, "SqlASTWalkerHelper", null, null,
						true, "ast-walk", null, null),
				new ParseDiagnostic(ParseDiagnostic.Severity.FATAL,
						"STATEMENT_MIXED_DIALECT_GRAMMAR",
						"Statement mixes Snowflake-only and PostgreSQL-only grammar at (l:1 c:42); unlikely to run on either engine.",
						1, 42, "SqlASTWalkerHelper", null, null,
						false, "ast-walk", null, null));
		assertOutputs(
				extractor,
				"{SQL={select={1={function={function_name=array_sort, parameters={1={column={name=tags, table_ref=a}}}}, alias=result_tags}, 2={alias=weekday, datetime_function={function_name=extract, part_form=KEYWORD, part=isodow, source={column={name=created_at, table_ref=a}}}}}, from={table={alias=a, table=events}}}}",
				"[result_tags, weekday]",
				"{}",
				"{events={created_at=[[@14,62:62='a',<455>,1:62]], tags=[[@3,18:18='a',<455>,1:18]]}}",
				"{query0={weekday=[[@19,79:85='weekday',<455>,1:79]], result_tags=[[@8,29:39='result_tags',<455>,1:29]]}}",
				"{def_query0={query_dictionary={result_tags=[[@8,29:39='result_tags',<455>,1:29]], weekday=[[@19,79:85='weekday',<455>,1:79]]}, table_dictionary={events={created_at=[[@14,62:62='a',<455>,1:62]], tags=[[@3,18:18='a',<455>,1:18]]}}, interface={result_tags=[{name=tags, table_ref=a}], weekday=[{name=created_at, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC38_arraySortUppercaseNameV0Test() {
		final String query = "SELECT a.id, ARRAY_SORT(a.tags) AS result_tags FROM events a WHERE a.active = 1";
		final SqlParseEventWalker extractor = runUnaryQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_sort' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null,
						true, "ast-walk", null, null));
		assertOutputs(
				extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={function_name=ARRAY_SORT, parameters={1={column={name=tags, table_ref=a}}}}, alias=result_tags}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_tags, id]",
				"{}",
				"{events={active=[[@17,67:67='a',<455>,1:67]], id=[[@1,7:7='a',<455>,1:7]], tags=[[@7,24:24='a',<455>,1:24]]}}",
				"{query0={result_tags=[[@12,35:45='result_tags',<455>,1:35]], id=[[@3,9:10='id',<455>,1:9]]}}",
				"{def_query0={query_dictionary={result_tags=[[@12,35:45='result_tags',<455>,1:35]], id=[[@3,9:10='id',<455>,1:9]]}, table_dictionary={events={active=[[@17,67:67='a',<455>,1:67]], id=[[@1,7:7='a',<455>,1:7]], tags=[[@7,24:24='a',<455>,1:24]]}}, filters=[{name=active, table_ref=a}], interface={result_tags=[{name=tags, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC38_arraySortDescendingOptionV0Test() {
		final String query = "SELECT a.id, array_sort(a.tags, FALSE) AS result_tags FROM events a WHERE a.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_sort' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null,
						true, "ast-walk", null, null));
		assertOutputs(
				extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={parameters={1={column={name=tags, table_ref=a}}, 2={literal=FALSE}}, function_name=array_sort}, alias=result_tags}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_tags, id]",
				"{}",
				"{events={active=[[@19,74:74='a',<455>,1:74]], id=[[@1,7:7='a',<455>,1:7]], tags=[[@7,24:24='a',<455>,1:24]]}}",
				"{query0={result_tags=[[@14,42:52='result_tags',<455>,1:42]], id=[[@3,9:10='id',<455>,1:9]]}}",
				"{def_query0={query_dictionary={result_tags=[[@14,42:52='result_tags',<455>,1:42]], id=[[@3,9:10='id',<455>,1:9]]}, table_dictionary={events={active=[[@19,74:74='a',<455>,1:74]], id=[[@1,7:7='a',<455>,1:7]], tags=[[@7,24:24='a',<455>,1:24]]}}, filters=[{name=active, table_ref=a}], interface={result_tags=[{name=tags, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC38_arraySortNullOrderingOptionV0Test() {
		final String query = "SELECT a.id, array_sort(a.tags, TRUE, FALSE) AS result_tags FROM events a WHERE a.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_sort' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null,
						true, "ast-walk", null, null));
		assertOutputs(
				extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={parameters={1={column={name=tags, table_ref=a}}, 2={literal=TRUE}, 3={literal=FALSE}}, function_name=array_sort}, alias=result_tags}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_tags, id]",
				"{}",
				"{events={active=[[@21,80:80='a',<455>,1:80]], id=[[@1,7:7='a',<455>,1:7]], tags=[[@7,24:24='a',<455>,1:24]]}}",
				"{query0={result_tags=[[@16,48:58='result_tags',<455>,1:48]], id=[[@3,9:10='id',<455>,1:9]]}}",
				"{def_query0={query_dictionary={result_tags=[[@16,48:58='result_tags',<455>,1:48]], id=[[@3,9:10='id',<455>,1:9]]}, table_dictionary={events={active=[[@21,80:80='a',<455>,1:80]], id=[[@1,7:7='a',<455>,1:7]], tags=[[@7,24:24='a',<455>,1:24]]}}, filters=[{name=active, table_ref=a}], interface={result_tags=[{name=tags, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC38_arraySortOptionalArgumentVariablesV0Test() {
		final String query = "SELECT a.id, array_sort(a.<tags_col>, <ascending>, <nulls_first>) AS result_tags FROM events a WHERE a.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_sort' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null,
						true, "ast-walk", null, null));
		assertOutputs(
				extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={parameters={1={column={substitution={name=<tags_col>, type=column}, table_ref=a}}, 2={substitution={name=<ascending>, type=predicand}}, 3={substitution={name=<nulls_first>, type=predicand}}}, function_name=array_sort}, alias=result_tags}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_tags, id]",
				"{<nulls_first>=predicand, <tags_col>=column, <ascending>=predicand}",
				"{events={active=[[@21,101:101='a',<455>,1:101]], id=[[@1,7:7='a',<455>,1:7]], <tags_col>=[[@7,24:24='a',<455>,1:24]]}}",
				"{query0={result_tags=[[@16,69:79='result_tags',<455>,1:69]], id=[[@3,9:10='id',<455>,1:9]]}}",
				"{def_query0={query_dictionary={result_tags=[[@16,69:79='result_tags',<455>,1:69]], id=[[@3,9:10='id',<455>,1:9]]}, table_dictionary={events={active=[[@21,101:101='a',<455>,1:101]], id=[[@1,7:7='a',<455>,1:7]], <tags_col>=[[@7,24:24='a',<455>,1:24]]}}, filters=[{name=active, table_ref=a}], interface={result_tags=[{substitution={name=<tags_col>, type=column}, table_ref=a}, {name=<ascending>, type=predicand}, {name=<nulls_first>, type=predicand}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC39_arrayMaxSelectPlacementV0Test() {
		final String query = "SELECT a.id, array_max(a.tags) AS result_tags FROM events a WHERE a.active = 1";
		final SqlParseEventWalker extractor = runUnaryQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_max' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null,
						true, "ast-walk", null, null));
		assertOutputs(
				extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={function_name=array_max, parameters={1={column={name=tags, table_ref=a}}}}, alias=result_tags}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_tags, id]",
				"{}",
				"{events={active=[[@17,66:66='a',<455>,1:66]], id=[[@1,7:7='a',<455>,1:7]], tags=[[@7,23:23='a',<455>,1:23]]}}",
				"{query0={result_tags=[[@12,34:44='result_tags',<455>,1:34]], id=[[@3,9:10='id',<455>,1:9]]}}",
				"{def_query0={query_dictionary={result_tags=[[@12,34:44='result_tags',<455>,1:34]], id=[[@3,9:10='id',<455>,1:9]]}, table_dictionary={events={active=[[@17,66:66='a',<455>,1:66]], id=[[@1,7:7='a',<455>,1:7]], tags=[[@7,23:23='a',<455>,1:23]]}}, filters=[{name=active, table_ref=a}], interface={result_tags=[{name=tags, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC39_arrayMaxWherePlacementV0Test() {
		final String query = "SELECT a.id FROM events a WHERE array_max(a.tags) IS NOT NULL AND a.active = 1";
		final SqlParseEventWalker extractor = runUnaryQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_max' at (l:1 c:32) is recognized as Snowflake-specific grammar.",
						1, 32, "SqlASTWalkerHelper", null, null,
						true, "ast-walk", null, null));
		assertOutputs(
				extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}}, from={table={alias=a, table=events}}, where={and={1={condition={left={function={function_name=array_max, parameters={1={column={name=tags, table_ref=a}}}}}, operator=IS NOT NULL}}, 2={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}}}",
				"[id]",
				"{}",
				"{events={active=[[@18,66:66='a',<455>,1:66]], id=[[@1,7:7='a',<455>,1:7]], tags=[[@10,42:42='a',<455>,1:42]]}}",
				"{query0={id=[[@3,9:10='id',<455>,1:9]]}}",
				"{def_query0={query_dictionary={id=[[@3,9:10='id',<455>,1:9]]}, table_dictionary={events={active=[[@18,66:66='a',<455>,1:66]], id=[[@1,7:7='a',<455>,1:7]], tags=[[@10,42:42='a',<455>,1:42]]}}, filters=[{name=tags, table_ref=a}, {name=active, table_ref=a}], interface={id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC39_arrayMaxJoinOnPlacementV0Test() {
		final String query = "SELECT a.id, b.id AS other_id FROM events a JOIN archived_events b ON array_max(a.tags) = b.tags AND a.id = b.id";
		final SqlParseEventWalker extractor = runUnaryQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_max' at (l:1 c:70) is recognized as Snowflake-specific grammar.",
						1, 70, "SqlASTWalkerHelper", null, null,
						true, "ast-walk", null, null));
		assertOutputs(
				extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={column={name=id, table_ref=b}, alias=other_id}}, from={join={1={table={alias=a, table=events}}, 2={join=JOIN, on={and={1={condition={left={function={function_name=array_max, parameters={1={column={name=tags, table_ref=a}}}}}, right={column={name=tags, table_ref=b}}, operator==}}, 2={condition={left={column={name=id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}}}, 3={table={alias=b, table=archived_events}}}}}}",
				"[id, other_id]",
				"{}",
				"{archived_events={id=[[@5,13:13='b',<455>,1:13], [@32,108:108='b',<455>,1:108]], tags=[[@24,90:90='b',<455>,1:90]]}, events={id=[[@1,7:7='a',<455>,1:7], [@28,101:101='a',<455>,1:101]], tags=[[@19,80:80='a',<455>,1:80]]}}",
				"{query0={id=[[@3,9:10='id',<455>,1:9]], other_id=[[@9,21:28='other_id',<455>,1:21]]}}",
				"{def_query0={query_dictionary={id=[[@3,9:10='id',<455>,1:9]], other_id=[[@9,21:28='other_id',<455>,1:21]]}, table_dictionary={archived_events={id=[[@5,13:13='b',<455>,1:13], [@32,108:108='b',<455>,1:108]], tags=[[@24,90:90='b',<455>,1:90]]}, events={id=[[@1,7:7='a',<455>,1:7], [@28,101:101='a',<455>,1:101]], tags=[[@19,80:80='a',<455>,1:80]]}}, filters=[{name=tags, table_ref=a}, {name=tags, table_ref=b}, {name=id, table_ref=a}, {name=id, table_ref=b}], interface={id=[{name=id, table_ref=a}], other_id=[{name=id, table_ref=b}]}, table_alias={a=events, b=archived_events}}}");
	}

	@Test
	public void arC39_arrayMaxHavingPlacementV0Test() {
		final String query = "SELECT a.group_id, count(*) AS n FROM events a GROUP BY a.group_id HAVING array_max(array_agg(a.tags)) IS NOT NULL AND count(*) > 1";
		final SqlParseEventWalker extractor = runUnaryQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_max' at (l:1 c:74) is recognized as Snowflake-specific grammar.",
						1, 74, "SqlASTWalkerHelper", null, null,
						true, "ast-walk", null, null));
		assertOutputs(
				extractor,
				"{SQL={select={1={column={name=group_id, table_ref=a}}, 2={function={function_name=COUNT, qualifier=null, parameters=*}, alias=n}}, having={and={1={condition={left={function={function_name=array_max, parameters={1={function={function_name=array_agg, qualifier=null, parameters={column={name=tags, table_ref=a}}}}}}}, operator=IS NOT NULL}}, 2={condition={left={function={function_name=COUNT, qualifier=null, parameters=*}}, right={literal=1}, operator=>}}}}, from={table={alias=a, table=events}}, groupby={1={column={name=group_id, table_ref=a}}}}}",
				"[group_id, n]",
				"{}",
				"{events={group_id=[[@1,7:7='a',<455>,1:7], [@16,56:56='a',<455>,1:56]], *=[[@7,25:25='*',<291>,1:25], [@35,125:125='*',<291>,1:125]], tags=[[@24,94:94='a',<455>,1:94]]}}",
				"{query0={group_id=[[@3,9:16='group_id',<455>,1:9]], n=[[@10,31:31='n',<455>,1:31]]}}",
				"{def_query0={query_dictionary={group_id=[[@3,9:16='group_id',<455>,1:9]], n=[[@10,31:31='n',<455>,1:31]]}, table_dictionary={events={group_id=[[@1,7:7='a',<455>,1:7], [@16,56:56='a',<455>,1:56]], *=[[@7,25:25='*',<291>,1:25], [@35,125:125='*',<291>,1:125]], tags=[[@24,94:94='a',<455>,1:94]]}}, grouped_by=[{name=group_id, table_ref=a}], filters=[{name=tags, table_ref=a}], interface={group_id=[{name=group_id, table_ref=a}], n=[]}, table_alias={a=events}}}");
	}

	@Test
	public void arC39_arrayMaxQualifyPlacementV0Test() {
		final String query = "SELECT a.id FROM events a QUALIFY array_max(a.tags) IS NOT NULL AND a.id > 0";
		final SqlParseEventWalker extractor = runUnaryQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_max' at (l:1 c:34) is recognized as Snowflake-specific grammar.",
						1, 34, "SqlASTWalkerHelper", null, null,
						true, "ast-walk", null, null));
		assertOutputs(
				extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}}, from={table={alias=a, table=events}}, qualify={and={1={condition={left={function={function_name=array_max, parameters={1={column={name=tags, table_ref=a}}}}}, operator=IS NOT NULL}}, 2={condition={left={column={name=id, table_ref=a}}, right={literal=0}, operator=>}}}}}}",
				"[id]",
				"{}",
				"{events={id=[[@1,7:7='a',<455>,1:7], [@18,68:68='a',<455>,1:68]], tags=[[@10,44:44='a',<455>,1:44]]}}",
				"{query0={id=[[@3,9:10='id',<455>,1:9]]}}",
				"{def_query0={query_dictionary={id=[[@3,9:10='id',<455>,1:9]]}, table_dictionary={events={id=[[@1,7:7='a',<455>,1:7], [@18,68:68='a',<455>,1:68]], tags=[[@10,44:44='a',<455>,1:44]]}}, filters=[{name=tags, table_ref=a}, {name=id, table_ref=a}], interface={id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC39_arrayMaxOrderByPlacementV0Test() {
		final String query = "SELECT a.id FROM events a WHERE a.active = 1 ORDER BY array_max(a.tags), a.id";
		final SqlParseEventWalker extractor = runUnaryQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_max' at (l:1 c:54) is recognized as Snowflake-specific grammar.",
						1, 54, "SqlASTWalkerHelper", null, null,
						true, "ast-walk", null, null));
		assertOutputs(
				extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}}, orderby={1={null_order=null, predicand={function={function_name=array_max, parameters={1={column={name=tags, table_ref=a}}}}}, sort_order=ASC}, 2={null_order=null, predicand={column={name=id, table_ref=a}}, sort_order=ASC}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[id]",
				"{}",
				"{events={active=[[@8,32:32='a',<455>,1:32]], id=[[@1,7:7='a',<455>,1:7], [@22,73:73='a',<455>,1:73]], tags=[[@17,64:64='a',<455>,1:64]]}}",
				"{query0={id=[[@3,9:10='id',<455>,1:9]]}}",
				"{def_query0={query_dictionary={id=[[@3,9:10='id',<455>,1:9]]}, table_dictionary={events={active=[[@8,32:32='a',<455>,1:32]], id=[[@1,7:7='a',<455>,1:7], [@22,73:73='a',<455>,1:73]], tags=[[@17,64:64='a',<455>,1:64]]}}, ordered_by=[{name=tags, table_ref=a}, {name=id, table_ref=a}], filters=[{name=active, table_ref=a}], interface={id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC39_arrayMaxValuesPlacementV0Test() {
		final String query = "VALUES (array_max(<tags_expr>), 7)";
		final SqlParseEventWalker extractor = runUnaryQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_max' at (l:1 c:8) is recognized as Snowflake-specific grammar.",
						1, 8, "SqlASTWalkerHelper", null, null,
						true, "ast-walk", null, null));
		assertOutputs(
				extractor,
				"{SQL={values={matrix={1={row={1={function={function_name=array_max, parameters={1={substitution={name=<tags_expr>, type=predicand}}}}}, 2={literal=7}}}}}}}",
				"[$1, $2]",
				"{<tags_expr>=predicand}",
				"{}",
				"{values0={$1=[[@1,7:7='(',<287>,1:7]], $2=[[@1,7:7='(',<287>,1:7]]}}",
				"{def_values0={query_dictionary={$1=[[@1,7:7='(',<287>,1:7]], $2=[[@1,7:7='(',<287>,1:7]]}, interface={$1=[], $2=[]}}}");
	}

	@Test
	public void arC39_arrayMaxInsertPlacementV0Test() {
		final String query = "INSERT INTO archived_events (tags, id) SELECT array_max(a.tags), a.id FROM events a WHERE a.active = 1";
		final SqlParseEventWalker extractor = runUnaryQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_max' at (l:1 c:46) is recognized as Snowflake-specific grammar.",
						1, 46, "SqlASTWalkerHelper", null, null,
						true, "ast-walk", null, null));
		assertOutputs(
				extractor,
				"{SQL={insert={preamble=insert_into, from={from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}, select={1={function={function_name=array_max, parameters={1={column={name=tags, table_ref=a}}}}}, 2={column={name=id, table_ref=a}}}}, target_table={table={alias=null, table=archived_events}}, columns={1={column={name=tags, table_ref=null}}, 2={column={name=id, table_ref=null}}}}}}",
				"[id, tags]",
				"{}",
				"{archived_events={id=[[@6,35:36='id',<455>,1:35]], tags=[[@4,29:32='tags',<455>,1:29]]}, events={active=[[@23,90:90='a',<455>,1:90]], id=[[@16,65:65='a',<455>,1:65]], tags=[[@11,56:56='a',<455>,1:56]]}}",
				"{query0={unnamed_0=[[@14,62:62=')',<288>,1:62]], id=[[@18,67:68='id',<455>,1:67]]}, insert1={id=[[@6,35:36='id',<455>,1:35]], tags=[[@4,29:32='tags',<455>,1:29]]}}",
				"{def_insert1={query_dictionary={id=[[@6,35:36='id',<455>,1:35]], tags=[[@4,29:32='tags',<455>,1:29]]}, table_dictionary={archived_events={id=[[@6,35:36='id',<455>,1:35]], tags=[[@4,29:32='tags',<455>,1:29]]}}, def_query0={query_dictionary={unnamed_0=[[@14,62:62=')',<288>,1:62]], id=[[@18,67:68='id',<455>,1:67]]}, table_dictionary={events={active=[[@23,90:90='a',<455>,1:90]], id=[[@16,65:65='a',<455>,1:65]], tags=[[@11,56:56='a',<455>,1:56]]}}, filters=[{name=active, table_ref=a}], interface={unnamed_0=[{name=tags, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}, interface={tags=[{name=unnamed_0, table_ref=query0}], id=[{name=id, table_ref=query0}]}}}");
	}

	@Test
	public void arC39_arrayMaxUpdatePlacementV0Test() {
		final String query = "UPDATE events SET tags = array_max(tags), active = 1 WHERE id > 0";
		final SqlParseEventWalker extractor = runUnaryQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_max' at (l:1 c:25) is recognized as Snowflake-specific grammar.",
						1, 25, "SqlASTWalkerHelper", null, null,
						true, "ast-walk", null, null));
		assertOutputs(
				extractor,
				"{SQL={update={table={alias=null, table=events}, where={condition={left={column={name=id, table_ref=null}}, right={literal=0}, operator=>}}, assignments={1={set={column={name=tags, table_ref=null}}, to={function={function_name=array_max, parameters={1={column={name=tags, table_ref=null}}}}}}, 2={set={column={name=active, table_ref=null}}, to={literal=1}}}}}}",
				"[active, tags]",
				"{}",
				"{events={id=[[@14,59:60='id',<455>,1:59]], tags=[[@7,35:38='tags',<455>,1:35]]}}",
				"{update0={active=[[@10,42:47='active',<455>,1:42]], tags=[[@3,18:21='tags',<455>,1:18]]}}",
				"{def_update0={assignments={active=[], tags=[{name=tags, table_ref=events}]}, table_dictionary={events={id=[[@14,59:60='id',<455>,1:59]], tags=[[@7,35:38='tags',<455>,1:35]]}}, update_dictionary={active=[[@10,42:47='active',<455>,1:42]], tags=[[@3,18:21='tags',<455>,1:18]]}, target_table={events={active=[[@10,42:47='active',<455>,1:42]], tags=[[@3,18:21='tags',<455>,1:18], [@7,35:38='tags',<455>,1:35], [@8,39:39=')',<288>,1:39]]}}, filters=[{name=id, table_ref=events}], lhs_unresolved_columns={active={column={name=active, table_ref=null}, locations=[[@10,42:47='active',<455>,1:42]]}, tags={column={name=tags, table_ref=null}, locations=[[@3,18:21='tags',<455>,1:18], [@7,35:38='tags',<455>,1:35], [@8,39:39=')',<288>,1:39]]}}}}");
	}

	@Test
	public void arC39_arrayMaxDeletePlacementV0Test() {
		final String query = "DELETE FROM events WHERE array_max(tags) IS NULL AND id > 0";
		final SqlParseEventWalker extractor = runUnaryQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_max' at (l:1 c:25) is recognized as Snowflake-specific grammar.",
						1, 25, "SqlASTWalkerHelper", null, null,
						true, "ast-walk", null, null));
		assertOutputs(
				extractor,
				"{SQL={delete={table={alias=null, table=events}, where={and={1={condition={left={function={function_name=array_max, parameters={1={column={name=tags, table_ref=null}}}}}, operator=IS NULL}}, 2={condition={left={column={name=id, table_ref=null}}, right={literal=0}, operator=>}}}}}}}",
				"[]",
				"{}",
				"{events={id=[[@11,53:54='id',<455>,1:53]], tags=[[@6,35:38='tags',<455>,1:35]]}}",
				"{}",
				"{def_delete0={query_dictionary={}, table_dictionary={events={id=[[@11,53:54='id',<455>,1:53]], tags=[[@6,35:38='tags',<455>,1:35]]}}, filters=[{name=tags, table_ref=events}, {name=id, table_ref=events}], interface=null}}");
	}

	@Test
	public void arC39_arrayMaxDdlPlacementV0Test() {
		final String query = "CREATE VIEW v_arr AS SELECT a.id, array_max(a.tags) AS result_tags FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SqlParseEventWalker extractor = runDdlParsertest(query, parser);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_max' at (l:1 c:34) is recognized as Snowflake-specific grammar.",
						1, 34, "SqlASTWalkerHelper", null, null,
						true, "ast-walk", null, null));
		assertOutputs(
				extractor,
				"{DDL={create={type=VIEW, name={table=v_arr}, query={select={1={column={name=id, table_ref=a}}, 2={function={function_name=array_max, parameters={1={column={name=tags, table_ref=a}}}}, alias=result_tags}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}}}",
				"[]",
				"{}",
				"{events={active=[[@21,87:87='a',<455>,1:87]], id=[[@5,28:28='a',<455>,1:28]], tags=[[@11,44:44='a',<455>,1:44]]}}",
				"{query0={result_tags=[[@16,55:65='result_tags',<455>,1:55]], id=[[@7,30:31='id',<455>,1:30]]}}",
				"{def_create1={def_query0={query_dictionary={result_tags=[[@16,55:65='result_tags',<455>,1:55]], id=[[@7,30:31='id',<455>,1:30]]}, table_dictionary={events={active=[[@21,87:87='a',<455>,1:87]], id=[[@5,28:28='a',<455>,1:28]], tags=[[@11,44:44='a',<455>,1:44]]}}, filters=[{name=active, table_ref=a}], interface={result_tags=[{name=tags, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}}");
	}

	@Test
	public void arR20_arrayMaxScriptStatementResetV0Test() {
		final String query = "SELECT a.id, array_max(a.tags) AS result_tags FROM events a;\nSELECT b.id FROM archived_events b;\nSELECT c.id, array_max(c.tags) AS result_tags FROM events c;";
		final SQLSelectParserParser parser = parse(query);
		final SqlParseEventWalker extractor = runScriptParsertest(query, parser);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_max' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null,
						true, "ast-walk", null, null),
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_max' at (l:3 c:13) is recognized as Snowflake-specific grammar.",
						3, 13, "SqlASTWalkerHelper", null, null,
						true, "ast-walk", null, null));
		assertOutputs(
				extractor,
				"{SCRIPT={1={select={1={column={name=id, table_ref=a}}, 2={function={function_name=array_max, parameters={1={column={name=tags, table_ref=a}}}}, alias=result_tags}}, from={table={alias=a, table=events}}}, 2={select={1={column={name=id, table_ref=b}}}, from={table={alias=b, table=archived_events}}}, 3={select={1={column={name=id, table_ref=c}}, 2={function={function_name=array_max, parameters={1={column={name=tags, table_ref=c}}}}, alias=result_tags}}, from={table={alias=c, table=events}}}}}",
				"[]",
				"{SCRIPT={1={}, 2={}, 3={}}}",
				"{SCRIPT={1={events={id=[[@1,7:7='a',<455>,1:7]], tags=[[@7,23:23='a',<455>,1:23]]}}, 2={archived_events={id=[[@18,68:68='b',<455>,2:7]]}}, 3={events={id=[[@26,104:104='c',<455>,3:7]], tags=[[@32,120:120='c',<455>,3:23]]}}}}",
				"{SCRIPT={1={query0={result_tags=[[@12,34:44='result_tags',<455>,1:34]], id=[[@3,9:10='id',<455>,1:9]]}}, 2={query0={id=[[@20,70:71='id',<455>,2:9]]}}, 3={query0={result_tags=[[@37,131:141='result_tags',<455>,3:34]], id=[[@28,106:107='id',<455>,3:9]]}}}}",
				"{SCRIPT={1={def_query0={query_dictionary={result_tags=[[@12,34:44='result_tags',<455>,1:34]], id=[[@3,9:10='id',<455>,1:9]]}, table_dictionary={events={id=[[@1,7:7='a',<455>,1:7]], tags=[[@7,23:23='a',<455>,1:23]]}}, interface={result_tags=[{name=tags, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}, 2={def_query0={query_dictionary={id=[[@20,70:71='id',<455>,2:9]]}, table_dictionary={archived_events={id=[[@18,68:68='b',<455>,2:7]]}}, interface={id=[{name=id, table_ref=b}]}, table_alias={b=archived_events}}}, 3={def_query0={query_dictionary={result_tags=[[@37,131:141='result_tags',<455>,3:34]], id=[[@28,106:107='id',<455>,3:9]]}, table_dictionary={events={id=[[@26,104:104='c',<455>,3:7]], tags=[[@32,120:120='c',<455>,3:23]]}}, interface={result_tags=[{name=tags, table_ref=c}], id=[{name=id, table_ref=c}]}, table_alias={c=events}}}}}");
	}

	@Test
	public void arR18_arrayMaxNestedExpressionV0Test() {
		final String query = "SELECT a.id, coalesce(array_max(a.tags), a.backup_tags) AS result_tags FROM events a WHERE a.active = 1";
		final SqlParseEventWalker extractor = runUnaryQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_max' at (l:1 c:22) is recognized as Snowflake-specific grammar.",
						1, 22, "SqlASTWalkerHelper", null, null,
						true, "ast-walk", null, null));
		assertOutputs(
				extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={parameters={1={function={function_name=array_max, parameters={1={column={name=tags, table_ref=a}}}}}, 2={column={name=backup_tags, table_ref=a}}}, function_name=coalesce}, alias=result_tags}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_tags, id]",
				"{}",
				"{events={active=[[@24,91:91='a',<455>,1:91]], id=[[@1,7:7='a',<455>,1:7]], backup_tags=[[@14,41:41='a',<455>,1:41]], tags=[[@9,32:32='a',<455>,1:32]]}}",
				"{query0={result_tags=[[@19,59:69='result_tags',<455>,1:59]], id=[[@3,9:10='id',<455>,1:9]]}}",
				"{def_query0={query_dictionary={result_tags=[[@19,59:69='result_tags',<455>,1:59]], id=[[@3,9:10='id',<455>,1:9]]}, table_dictionary={events={active=[[@24,91:91='a',<455>,1:91]], id=[[@1,7:7='a',<455>,1:7]], backup_tags=[[@14,41:41='a',<455>,1:41]], tags=[[@9,32:32='a',<455>,1:32]]}}, filters=[{name=active, table_ref=a}], interface={result_tags=[{name=tags, table_ref=a}, {name=backup_tags, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arR18_arrayMaxDerivedOutputBoundaryV0Test() {
		final String query = "SELECT d.id, d.result_tags FROM (SELECT a.id, array_max(a.tags) AS result_tags FROM events a) d WHERE d.id > 0";
		final SqlParseEventWalker extractor = runUnaryQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_max' at (l:1 c:46) is recognized as Snowflake-specific grammar.",
						1, 46, "SqlASTWalkerHelper", null, null,
						true, "ast-walk", null, null));
		assertOutputs(
				extractor,
				"{SQL={select={1={column={name=id, table_ref=d}}, 2={column={name=result_tags, table_ref=d}}}, from={table={alias=d, query={select={1={column={name=id, table_ref=a}}, 2={function={function_name=array_max, parameters={1={column={name=tags, table_ref=a}}}}, alias=result_tags}}, from={table={alias=a, table=events}}}}}, where={condition={left={column={name=id, table_ref=d}}, right={literal=0}, operator=>}}}}",
				"[result_tags, id]",
				"{}",
				"{events={id=[[@11,40:40='a',<455>,1:40]], tags=[[@17,56:56='a',<455>,1:56]]}}",
				"{query0={result_tags=[[@22,67:77='result_tags',<455>,1:67], [@5,13:13='d',<455>,1:13]], id=[[@13,42:43='id',<455>,1:42], [@1,7:7='d',<455>,1:7], [@29,102:102='d',<455>,1:102]]}, query1={result_tags=[[@7,15:25='result_tags',<455>,1:15]], id=[[@3,9:10='id',<455>,1:9]]}}",
				"{def_query1={query_dictionary={result_tags=[[@7,15:25='result_tags',<455>,1:15]], id=[[@3,9:10='id',<455>,1:9]]}, def_query0={query_dictionary={result_tags=[[@22,67:77='result_tags',<455>,1:67], [@5,13:13='d',<455>,1:13]], id=[[@13,42:43='id',<455>,1:42], [@1,7:7='d',<455>,1:7], [@29,102:102='d',<455>,1:102]]}, table_dictionary={events={id=[[@11,40:40='a',<455>,1:40]], tags=[[@17,56:56='a',<455>,1:56]]}}, interface={result_tags=[{name=tags, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}, filters=[{name=id, table_ref=d}], interface={result_tags=[{name=result_tags, table_ref=d}], id=[{name=id, table_ref=d}]}, table_alias={d=query0}}}");
	}

	@Test
	public void arR19_arrayMaxSetOpSiblingIsolationV0Test() {
		final String query = "SELECT a.id, array_max(a.tags) AS result_tags FROM events a UNION ALL SELECT b.id, b.tags AS result_tags FROM archived_events b";
		final SqlParseEventWalker extractor = runUnaryQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_max' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null,
						true, "ast-walk", null, null));
		assertOutputs(
				extractor,
				"{SQL={union={1={select={1={column={name=id, table_ref=a}}, 2={function={function_name=array_max, parameters={1={column={name=tags, table_ref=a}}}}, alias=result_tags}}, from={table={alias=a, table=events}}}, 2={union={qualifier=ALL, operator=UNION}}, 3={select={1={column={name=id, table_ref=b}}, 2={column={name=tags, table_ref=b}, alias=result_tags}}, from={table={alias=b, table=archived_events}}}}}}",
				"[result_tags, id]",
				"{}",
				"{archived_events={id=[[@19,77:77='b',<455>,1:77]], tags=[[@23,83:83='b',<455>,1:83]]}, events={id=[[@1,7:7='a',<455>,1:7]], tags=[[@7,23:23='a',<455>,1:23]]}}",
				"{query0={result_tags=[[@12,34:44='result_tags',<455>,1:34]], id=[[@3,9:10='id',<455>,1:9]]}, query1={result_tags=[[@27,93:103='result_tags',<455>,1:93]], id=[[@21,79:80='id',<455>,1:79]]}}",
				"{def_union2={def_query1={query_dictionary={result_tags=[[@27,93:103='result_tags',<455>,1:93]], id=[[@21,79:80='id',<455>,1:79]]}, table_dictionary={archived_events={id=[[@19,77:77='b',<455>,1:77]], tags=[[@23,83:83='b',<455>,1:83]]}}, setop=UNION, interface={result_tags=[{name=tags, table_ref=b}], id=[{name=id, table_ref=b}]}, table_alias={b=archived_events}}, def_query0={query_dictionary={result_tags=[[@12,34:44='result_tags',<455>,1:34]], id=[[@3,9:10='id',<455>,1:9]]}, table_dictionary={events={id=[[@1,7:7='a',<455>,1:7]], tags=[[@7,23:23='a',<455>,1:23]]}}, interface={result_tags=[{name=tags, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}, interface={result_tags=query_column, id=query_column}}}");
	}

	@Test
	public void arC39_arrayMaxUnqualifiedOperandV0Test() {
		final String query = "SELECT id, array_max(tags) AS result_tags FROM events WHERE active = 1";
		final SqlParseEventWalker extractor = runUnaryQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_max' at (l:1 c:11) is recognized as Snowflake-specific grammar.",
						1, 11, "SqlASTWalkerHelper", null, null,
						true, "ast-walk", null, null));
		assertOutputs(
				extractor,
				"{SQL={select={1={column={name=id, table_ref=null}}, 2={function={function_name=array_max, parameters={1={column={name=tags, table_ref=null}}}}, alias=result_tags}}, from={table={alias=null, table=events}}, where={condition={left={column={name=active, table_ref=null}}, right={literal=1}, operator==}}}}",
				"[result_tags, id]",
				"{}",
				"{events={active=[[@12,60:65='active',<455>,1:60]], id=[[@1,7:8='id',<455>,1:7]], tags=[[@5,21:24='tags',<455>,1:21]]}}",
				"{query0={result_tags=[[@8,30:40='result_tags',<455>,1:30]], id=[[@1,7:8='id',<455>,1:7]]}}",
				"{def_query0={query_dictionary={result_tags=[[@8,30:40='result_tags',<455>,1:30]], id=[[@1,7:8='id',<455>,1:7]]}, table_dictionary={events={active=[[@12,60:65='active',<455>,1:60]], id=[[@1,7:8='id',<455>,1:7]], tags=[[@5,21:24='tags',<455>,1:21]]}}, filters=[{name=active, table_ref=events}], interface={result_tags=[{name=tags, table_ref=events}], id=[{name=id, table_ref=events}]}}}");
	}

	@Test
	public void arM20_arrayMaxUnresolvedOperandV0Test() {
		final String query = "SELECT array_max(missing_tags) AS result_tags";
		final SqlParseEventWalker extractor = runUnaryQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_max' at (l:1 c:7) is recognized as Snowflake-specific grammar.",
						1, 7, "SqlASTWalkerHelper", null, null,
						true, "ast-walk", null, null),
				new ParseDiagnostic(ParseDiagnostic.Severity.ERROR,
						"UNRESOLVED_UNQUALIFIED_COLUMNS",
						"Unresolved unqualified column reference(s): [missing_tags [(l:1 c:17)]]",
						1, 17, "SqlASTWalkerHelper", null, "missing_tags",
						false, "ast-walk", null, null));
		assertOutputs(
				extractor,
				"{SQL={select={1={function={function_name=array_max, parameters={1={column={name=missing_tags, table_ref=null}}}}, alias=result_tags}}}}",
				"[result_tags]",
				"{}",
				"{}",
				"{query0={result_tags=[[@6,34:44='result_tags',<455>,1:34]]}}",
				"{def_query0={query_dictionary={result_tags=[[@6,34:44='result_tags',<455>,1:34]]}, interface={result_tags=[{name=missing_tags, table_ref=null}]}}}");
	}

	@Test
	public void arM21_arrayMaxAmbiguousOperandV0Test() {
		final String query = "SELECT array_max(tags) AS result_tags FROM events a JOIN archived_events b ON a.id = b.id";
		final SqlParseEventWalker extractor = runUnaryQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_max' at (l:1 c:7) is recognized as Snowflake-specific grammar.",
						1, 7, "SqlASTWalkerHelper", null, null,
						true, "ast-walk", null, null),
				new ParseDiagnostic(ParseDiagnostic.Severity.SEVERE_WARNING,
						"AMBIGUOUS_COLUMN_REFERENCE",
						"Ambiguous column reference 'tags' at (l:1 c:17). Possible sources: [archived_events, events]",
						1, 17, "SqlASTWalkerHelper", null, "tags",
						true, "ast-walk", null, null),
				new ParseDiagnostic(ParseDiagnostic.Severity.ERROR,
						"UNRESOLVED_UNQUALIFIED_COLUMNS",
						"Unresolved unqualified column reference(s): [tags [(l:1 c:17)]]",
						1, 17, "SqlASTWalkerHelper", null, "tags",
						false, "ast-walk", null, null));
		assertOutputs(
				extractor,
				"{SQL={select={1={function={function_name=array_max, parameters={1={column={name=tags, table_ref=null}}}}, alias=result_tags}}, from={join={1={table={alias=a, table=events}}, 2={join=JOIN, on={condition={left={column={name=id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=archived_events}}}}}}",
				"[result_tags]",
				"{}",
				"{archived_events={id=[[@18,85:85='b',<455>,1:85]]}, events={id=[[@14,78:78='a',<455>,1:78]]}}",
				"{query0={result_tags=[[@6,26:36='result_tags',<455>,1:26]]}}",
				"{def_query0={query_dictionary={result_tags=[[@6,26:36='result_tags',<455>,1:26]]}, table_dictionary={archived_events={id=[[@18,85:85='b',<455>,1:85]]}, events={id=[[@14,78:78='a',<455>,1:78]]}}, filters=[{name=id, table_ref=a}, {name=id, table_ref=b}], interface={result_tags=[{name=tags, table_ref=null}]}, table_alias={a=events, b=archived_events}}}");
	}

	@Test
	public void arM22_arrayMaxMissingAliasOperandV0Test() {
		final String query = "SELECT a.id, array_max(z.tags) AS result_tags FROM events a WHERE a.active = 1";
		final SqlParseEventWalker extractor = runUnaryQuery(query);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_max' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null,
						true, "ast-walk", null, null),
				new ParseDiagnostic(ParseDiagnostic.Severity.FATAL,
						"QUALIFIED_COLUMN_NOT_FOUND_IN_TABLE",
						"Source Table not found for Column 'tags' at (l:1 c:34). No alias or table called 'z'.",
						1, 34, "SqlASTWalkerHelper", null, "tags",
						false, "ast-walk", null, null));
		assertOutputs(
				extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={function_name=array_max, parameters={1={column={name=tags, table_ref=z}}}}, alias=result_tags}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_tags, id]",
				"{}",
				"{events={active=[[@17,66:66='a',<455>,1:66]], id=[[@1,7:7='a',<455>,1:7]]}}",
				"{query0={result_tags=[[@12,34:44='result_tags',<455>,1:34]], id=[[@3,9:10='id',<455>,1:9]]}}",
				"{def_query0={query_dictionary={result_tags=[[@12,34:44='result_tags',<455>,1:34]], id=[[@3,9:10='id',<455>,1:9]]}, table_dictionary={events={active=[[@17,66:66='a',<455>,1:66]], id=[[@1,7:7='a',<455>,1:7]]}}, filters=[{name=active, table_ref=a}], interface={result_tags=[{name=tags, table_ref=z}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arM22_arrayMaxHiddenAliasOperandV0Test() {
		final String query = "SELECT d.id, array_max(a.tags) AS result_tags FROM (SELECT a.id, a.tags FROM events a) d WHERE d.id > 0";
		final SqlParseEventWalker extractor = runUnaryQuery(query);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_max' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null,
						true, "ast-walk", null, null),
				new ParseDiagnostic(ParseDiagnostic.Severity.FATAL,
						"QUALIFIED_COLUMN_NOT_FOUND_IN_TABLE",
						"Source Table not found for Column 'tags' at (l:1 c:34). No alias or table called 'a'.",
						1, 34, "SqlASTWalkerHelper", null, "tags",
						false, "ast-walk", null, null));
		assertOutputs(
				extractor,
				"{SQL={select={1={column={name=id, table_ref=d}}, 2={function={function_name=array_max, parameters={1={column={name=tags, table_ref=a}}}}, alias=result_tags}}, from={table={alias=d, query={select={1={column={name=id, table_ref=a}}, 2={column={name=tags, table_ref=a}}}, from={table={alias=a, table=events}}}}}, where={condition={left={column={name=id, table_ref=d}}, right={literal=0}, operator=>}}}}",
				"[result_tags, id]",
				"{}",
				"{events={id=[[@16,59:59='a',<455>,1:59]], tags=[[@20,65:65='a',<455>,1:65]]}}",
				"{query0={id=[[@18,61:62='id',<455>,1:61], [@1,7:7='d',<455>,1:7], [@29,95:95='d',<455>,1:95]], tags=[[@22,67:70='tags',<455>,1:67]]}, query1={result_tags=[[@12,34:44='result_tags',<455>,1:34]], id=[[@3,9:10='id',<455>,1:9]]}}",
				"{def_query1={query_dictionary={result_tags=[[@12,34:44='result_tags',<455>,1:34]], id=[[@3,9:10='id',<455>,1:9]]}, table_dictionary={}, def_query0={query_dictionary={id=[[@18,61:62='id',<455>,1:61], [@1,7:7='d',<455>,1:7], [@29,95:95='d',<455>,1:95]], tags=[[@22,67:70='tags',<455>,1:67]]}, table_dictionary={events={id=[[@16,59:59='a',<455>,1:59]], tags=[[@20,65:65='a',<455>,1:65]]}}, interface={id=[{name=id, table_ref=a}], tags=[{name=tags, table_ref=a}]}, table_alias={a=events}}, filters=[{name=id, table_ref=d}], interface={result_tags=[{name=tags, table_ref=a}], id=[{name=id, table_ref=d}]}, table_alias={d=query0}}}");
	}

	@Test
	public void arC39_arrayMaxPredicandVariableV0Test() {
		final String query = "SELECT a.id, array_max(<tags_expr>) AS result_tags FROM events a WHERE a.active = 1";
		final SqlParseEventWalker extractor = runUnaryQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_max' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null,
						true, "ast-walk", null, null));
		assertOutputs(
				extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={function_name=array_max, parameters={1={substitution={name=<tags_expr>, type=predicand}}}}, alias=result_tags}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_tags, id]",
				"{<tags_expr>=predicand}",
				"{events={active=[[@15,71:71='a',<455>,1:71]], id=[[@1,7:7='a',<455>,1:7]]}}",
				"{query0={result_tags=[[@10,39:49='result_tags',<455>,1:39]], id=[[@3,9:10='id',<455>,1:9]]}}",
				"{def_query0={query_dictionary={result_tags=[[@10,39:49='result_tags',<455>,1:39]], id=[[@3,9:10='id',<455>,1:9]]}, table_dictionary={events={active=[[@15,71:71='a',<455>,1:71]], id=[[@1,7:7='a',<455>,1:7]]}}, filters=[{name=active, table_ref=a}], interface={result_tags=[{name=<tags_expr>, type=predicand}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC39_arrayMaxQualifiedColumnVariableV0Test() {
		final String query = "SELECT a.id, array_max(a.<tags_col>) AS result_tags FROM events a WHERE a.active = 1";
		final SqlParseEventWalker extractor = runUnaryQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_max' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null,
						true, "ast-walk", null, null));
		assertOutputs(
				extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={function_name=array_max, parameters={1={column={substitution={name=<tags_col>, type=column}, table_ref=a}}}}, alias=result_tags}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_tags, id]",
				"{<tags_col>=column}",
				"{events={active=[[@17,72:72='a',<455>,1:72]], id=[[@1,7:7='a',<455>,1:7]], <tags_col>=[[@7,23:23='a',<455>,1:23]]}}",
				"{query0={result_tags=[[@12,40:50='result_tags',<455>,1:40]], id=[[@3,9:10='id',<455>,1:9]]}}",
				"{def_query0={query_dictionary={result_tags=[[@12,40:50='result_tags',<455>,1:40]], id=[[@3,9:10='id',<455>,1:9]]}, table_dictionary={events={active=[[@17,72:72='a',<455>,1:72]], id=[[@1,7:7='a',<455>,1:7]], <tags_col>=[[@7,23:23='a',<455>,1:23]]}}, filters=[{name=active, table_ref=a}], interface={result_tags=[{substitution={name=<tags_col>, type=column}, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arM23_arrayMaxSchemaQualifiedRoutineControlV0Test() {
		final String query = "SELECT a.id, custom.array_max(a.tags) AS result_tags FROM events a WHERE a.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(
				extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={schema=custom, parameters={1={column={name=tags, table_ref=a}}}, function_name=array_max}, alias=result_tags}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_tags, id]",
				"{}",
				"{events={active=[[@19,73:73='a',<455>,1:73]], id=[[@1,7:7='a',<455>,1:7]], tags=[[@9,30:30='a',<455>,1:30]]}}",
				"{query0={result_tags=[[@14,41:51='result_tags',<455>,1:41]], id=[[@3,9:10='id',<455>,1:9]]}}",
				"{def_query0={query_dictionary={result_tags=[[@14,41:51='result_tags',<455>,1:41]], id=[[@3,9:10='id',<455>,1:9]]}, table_dictionary={events={active=[[@19,73:73='a',<455>,1:73]], id=[[@1,7:7='a',<455>,1:7]], tags=[[@9,30:30='a',<455>,1:30]]}}, filters=[{name=active, table_ref=a}], interface={result_tags=[{name=tags, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arM24_arrayMaxMixedDialectControlV0Test() {
		final String query = "SELECT array_max(a.tags) AS result_tags, extract(isodow from a.created_at) AS weekday FROM events a";
		final SqlParseEventWalker extractor = runUnaryQuery(query);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_max' at (l:1 c:7) is recognized as Snowflake-specific grammar.",
						1, 7, "SqlASTWalkerHelper", null, null,
						true, "ast-walk", null, null),
				new ParseDiagnostic(ParseDiagnostic.Severity.FATAL,
						"STATEMENT_MIXED_DIALECT_GRAMMAR",
						"Statement mixes Snowflake-only and PostgreSQL-only grammar at (l:1 c:41); unlikely to run on either engine.",
						1, 41, "SqlASTWalkerHelper", null, null,
						false, "ast-walk", null, null));
		assertOutputs(
				extractor,
				"{SQL={select={1={function={function_name=array_max, parameters={1={column={name=tags, table_ref=a}}}}, alias=result_tags}, 2={alias=weekday, datetime_function={function_name=extract, part_form=KEYWORD, part=isodow, source={column={name=created_at, table_ref=a}}}}}, from={table={alias=a, table=events}}}}",
				"[result_tags, weekday]",
				"{}",
				"{events={created_at=[[@14,61:61='a',<455>,1:61]], tags=[[@3,17:17='a',<455>,1:17]]}}",
				"{query0={weekday=[[@19,78:84='weekday',<455>,1:78]], result_tags=[[@8,28:38='result_tags',<455>,1:28]]}}",
				"{def_query0={query_dictionary={result_tags=[[@8,28:38='result_tags',<455>,1:28]], weekday=[[@19,78:84='weekday',<455>,1:78]]}, table_dictionary={events={created_at=[[@14,61:61='a',<455>,1:61]], tags=[[@3,17:17='a',<455>,1:17]]}}, interface={result_tags=[{name=tags, table_ref=a}], weekday=[{name=created_at, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC39_arrayMaxUppercaseNameV0Test() {
		final String query = "SELECT a.id, ARRAY_MAX(a.tags) AS result_tags FROM events a WHERE a.active = 1";
		final SqlParseEventWalker extractor = runUnaryQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_max' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null,
						true, "ast-walk", null, null));
		assertOutputs(
				extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={function_name=ARRAY_MAX, parameters={1={column={name=tags, table_ref=a}}}}, alias=result_tags}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_tags, id]",
				"{}",
				"{events={active=[[@17,66:66='a',<455>,1:66]], id=[[@1,7:7='a',<455>,1:7]], tags=[[@7,23:23='a',<455>,1:23]]}}",
				"{query0={result_tags=[[@12,34:44='result_tags',<455>,1:34]], id=[[@3,9:10='id',<455>,1:9]]}}",
				"{def_query0={query_dictionary={result_tags=[[@12,34:44='result_tags',<455>,1:34]], id=[[@3,9:10='id',<455>,1:9]]}, table_dictionary={events={active=[[@17,66:66='a',<455>,1:66]], id=[[@1,7:7='a',<455>,1:7]], tags=[[@7,23:23='a',<455>,1:23]]}}, filters=[{name=active, table_ref=a}], interface={result_tags=[{name=tags, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC40_arrayMinSelectPlacementV0Test() {
		final String query = "SELECT a.id, array_min(a.tags) AS result_tags FROM events a WHERE a.active = 1";
		final SqlParseEventWalker extractor = runUnaryQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_min' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null,
						true, "ast-walk", null, null));
		assertOutputs(
				extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={function_name=array_min, parameters={1={column={name=tags, table_ref=a}}}}, alias=result_tags}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_tags, id]",
				"{}",
				"{events={active=[[@17,66:66='a',<455>,1:66]], id=[[@1,7:7='a',<455>,1:7]], tags=[[@7,23:23='a',<455>,1:23]]}}",
				"{query0={result_tags=[[@12,34:44='result_tags',<455>,1:34]], id=[[@3,9:10='id',<455>,1:9]]}}",
				"{def_query0={query_dictionary={result_tags=[[@12,34:44='result_tags',<455>,1:34]], id=[[@3,9:10='id',<455>,1:9]]}, table_dictionary={events={active=[[@17,66:66='a',<455>,1:66]], id=[[@1,7:7='a',<455>,1:7]], tags=[[@7,23:23='a',<455>,1:23]]}}, filters=[{name=active, table_ref=a}], interface={result_tags=[{name=tags, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC40_arrayMinWherePlacementV0Test() {
		final String query = "SELECT a.id FROM events a WHERE array_min(a.tags) IS NOT NULL AND a.active = 1";
		final SqlParseEventWalker extractor = runUnaryQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_min' at (l:1 c:32) is recognized as Snowflake-specific grammar.",
						1, 32, "SqlASTWalkerHelper", null, null,
						true, "ast-walk", null, null));
		assertOutputs(
				extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}}, from={table={alias=a, table=events}}, where={and={1={condition={left={function={function_name=array_min, parameters={1={column={name=tags, table_ref=a}}}}}, operator=IS NOT NULL}}, 2={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}}}",
				"[id]",
				"{}",
				"{events={active=[[@18,66:66='a',<455>,1:66]], id=[[@1,7:7='a',<455>,1:7]], tags=[[@10,42:42='a',<455>,1:42]]}}",
				"{query0={id=[[@3,9:10='id',<455>,1:9]]}}",
				"{def_query0={query_dictionary={id=[[@3,9:10='id',<455>,1:9]]}, table_dictionary={events={active=[[@18,66:66='a',<455>,1:66]], id=[[@1,7:7='a',<455>,1:7]], tags=[[@10,42:42='a',<455>,1:42]]}}, filters=[{name=tags, table_ref=a}, {name=active, table_ref=a}], interface={id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC40_arrayMinJoinOnPlacementV0Test() {
		final String query = "SELECT a.id, b.id AS other_id FROM events a JOIN archived_events b ON array_min(a.tags) = b.tags AND a.id = b.id";
		final SqlParseEventWalker extractor = runUnaryQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_min' at (l:1 c:70) is recognized as Snowflake-specific grammar.",
						1, 70, "SqlASTWalkerHelper", null, null,
						true, "ast-walk", null, null));
		assertOutputs(
				extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={column={name=id, table_ref=b}, alias=other_id}}, from={join={1={table={alias=a, table=events}}, 2={join=JOIN, on={and={1={condition={left={function={function_name=array_min, parameters={1={column={name=tags, table_ref=a}}}}}, right={column={name=tags, table_ref=b}}, operator==}}, 2={condition={left={column={name=id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}}}, 3={table={alias=b, table=archived_events}}}}}}",
				"[id, other_id]",
				"{}",
				"{archived_events={id=[[@5,13:13='b',<455>,1:13], [@32,108:108='b',<455>,1:108]], tags=[[@24,90:90='b',<455>,1:90]]}, events={id=[[@1,7:7='a',<455>,1:7], [@28,101:101='a',<455>,1:101]], tags=[[@19,80:80='a',<455>,1:80]]}}",
				"{query0={id=[[@3,9:10='id',<455>,1:9]], other_id=[[@9,21:28='other_id',<455>,1:21]]}}",
				"{def_query0={query_dictionary={id=[[@3,9:10='id',<455>,1:9]], other_id=[[@9,21:28='other_id',<455>,1:21]]}, table_dictionary={archived_events={id=[[@5,13:13='b',<455>,1:13], [@32,108:108='b',<455>,1:108]], tags=[[@24,90:90='b',<455>,1:90]]}, events={id=[[@1,7:7='a',<455>,1:7], [@28,101:101='a',<455>,1:101]], tags=[[@19,80:80='a',<455>,1:80]]}}, filters=[{name=tags, table_ref=a}, {name=tags, table_ref=b}, {name=id, table_ref=a}, {name=id, table_ref=b}], interface={id=[{name=id, table_ref=a}], other_id=[{name=id, table_ref=b}]}, table_alias={a=events, b=archived_events}}}");
	}

	@Test
	public void arC40_arrayMinHavingPlacementV0Test() {
		final String query = "SELECT a.group_id, count(*) AS n FROM events a GROUP BY a.group_id HAVING array_min(array_agg(a.tags)) IS NOT NULL AND count(*) > 1";
		final SqlParseEventWalker extractor = runUnaryQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_min' at (l:1 c:74) is recognized as Snowflake-specific grammar.",
						1, 74, "SqlASTWalkerHelper", null, null,
						true, "ast-walk", null, null));
		assertOutputs(
				extractor,
				"{SQL={select={1={column={name=group_id, table_ref=a}}, 2={function={function_name=COUNT, qualifier=null, parameters=*}, alias=n}}, having={and={1={condition={left={function={function_name=array_min, parameters={1={function={function_name=array_agg, qualifier=null, parameters={column={name=tags, table_ref=a}}}}}}}, operator=IS NOT NULL}}, 2={condition={left={function={function_name=COUNT, qualifier=null, parameters=*}}, right={literal=1}, operator=>}}}}, from={table={alias=a, table=events}}, groupby={1={column={name=group_id, table_ref=a}}}}}",
				"[group_id, n]",
				"{}",
				"{events={group_id=[[@1,7:7='a',<455>,1:7], [@16,56:56='a',<455>,1:56]], *=[[@7,25:25='*',<291>,1:25], [@35,125:125='*',<291>,1:125]], tags=[[@24,94:94='a',<455>,1:94]]}}",
				"{query0={group_id=[[@3,9:16='group_id',<455>,1:9]], n=[[@10,31:31='n',<455>,1:31]]}}",
				"{def_query0={query_dictionary={group_id=[[@3,9:16='group_id',<455>,1:9]], n=[[@10,31:31='n',<455>,1:31]]}, table_dictionary={events={group_id=[[@1,7:7='a',<455>,1:7], [@16,56:56='a',<455>,1:56]], *=[[@7,25:25='*',<291>,1:25], [@35,125:125='*',<291>,1:125]], tags=[[@24,94:94='a',<455>,1:94]]}}, grouped_by=[{name=group_id, table_ref=a}], filters=[{name=tags, table_ref=a}], interface={group_id=[{name=group_id, table_ref=a}], n=[]}, table_alias={a=events}}}");
	}

	@Test
	public void arC40_arrayMinQualifyPlacementV0Test() {
		final String query = "SELECT a.id FROM events a QUALIFY array_min(a.tags) IS NOT NULL AND a.id > 0";
		final SqlParseEventWalker extractor = runUnaryQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_min' at (l:1 c:34) is recognized as Snowflake-specific grammar.",
						1, 34, "SqlASTWalkerHelper", null, null,
						true, "ast-walk", null, null));
		assertOutputs(
				extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}}, from={table={alias=a, table=events}}, qualify={and={1={condition={left={function={function_name=array_min, parameters={1={column={name=tags, table_ref=a}}}}}, operator=IS NOT NULL}}, 2={condition={left={column={name=id, table_ref=a}}, right={literal=0}, operator=>}}}}}}",
				"[id]",
				"{}",
				"{events={id=[[@1,7:7='a',<455>,1:7], [@18,68:68='a',<455>,1:68]], tags=[[@10,44:44='a',<455>,1:44]]}}",
				"{query0={id=[[@3,9:10='id',<455>,1:9]]}}",
				"{def_query0={query_dictionary={id=[[@3,9:10='id',<455>,1:9]]}, table_dictionary={events={id=[[@1,7:7='a',<455>,1:7], [@18,68:68='a',<455>,1:68]], tags=[[@10,44:44='a',<455>,1:44]]}}, filters=[{name=tags, table_ref=a}, {name=id, table_ref=a}], interface={id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC40_arrayMinOrderByPlacementV0Test() {
		final String query = "SELECT a.id FROM events a WHERE a.active = 1 ORDER BY array_min(a.tags), a.id";
		final SqlParseEventWalker extractor = runUnaryQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_min' at (l:1 c:54) is recognized as Snowflake-specific grammar.",
						1, 54, "SqlASTWalkerHelper", null, null,
						true, "ast-walk", null, null));
		assertOutputs(
				extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}}, orderby={1={null_order=null, predicand={function={function_name=array_min, parameters={1={column={name=tags, table_ref=a}}}}}, sort_order=ASC}, 2={null_order=null, predicand={column={name=id, table_ref=a}}, sort_order=ASC}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[id]",
				"{}",
				"{events={active=[[@8,32:32='a',<455>,1:32]], id=[[@1,7:7='a',<455>,1:7], [@22,73:73='a',<455>,1:73]], tags=[[@17,64:64='a',<455>,1:64]]}}",
				"{query0={id=[[@3,9:10='id',<455>,1:9]]}}",
				"{def_query0={query_dictionary={id=[[@3,9:10='id',<455>,1:9]]}, table_dictionary={events={active=[[@8,32:32='a',<455>,1:32]], id=[[@1,7:7='a',<455>,1:7], [@22,73:73='a',<455>,1:73]], tags=[[@17,64:64='a',<455>,1:64]]}}, ordered_by=[{name=tags, table_ref=a}, {name=id, table_ref=a}], filters=[{name=active, table_ref=a}], interface={id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC40_arrayMinValuesPlacementV0Test() {
		final String query = "VALUES (array_min(<tags_expr>), 7)";
		final SqlParseEventWalker extractor = runUnaryQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_min' at (l:1 c:8) is recognized as Snowflake-specific grammar.",
						1, 8, "SqlASTWalkerHelper", null, null,
						true, "ast-walk", null, null));
		assertOutputs(
				extractor,
				"{SQL={values={matrix={1={row={1={function={function_name=array_min, parameters={1={substitution={name=<tags_expr>, type=predicand}}}}}, 2={literal=7}}}}}}}",
				"[$1, $2]",
				"{<tags_expr>=predicand}",
				"{}",
				"{values0={$1=[[@1,7:7='(',<287>,1:7]], $2=[[@1,7:7='(',<287>,1:7]]}}",
				"{def_values0={query_dictionary={$1=[[@1,7:7='(',<287>,1:7]], $2=[[@1,7:7='(',<287>,1:7]]}, interface={$1=[], $2=[]}}}");
	}

	@Test
	public void arC40_arrayMinInsertPlacementV0Test() {
		final String query = "INSERT INTO archived_events (tags, id) SELECT array_min(a.tags), a.id FROM events a WHERE a.active = 1";
		final SqlParseEventWalker extractor = runUnaryQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_min' at (l:1 c:46) is recognized as Snowflake-specific grammar.",
						1, 46, "SqlASTWalkerHelper", null, null,
						true, "ast-walk", null, null));
		assertOutputs(
				extractor,
				"{SQL={insert={preamble=insert_into, from={from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}, select={1={function={function_name=array_min, parameters={1={column={name=tags, table_ref=a}}}}}, 2={column={name=id, table_ref=a}}}}, target_table={table={alias=null, table=archived_events}}, columns={1={column={name=tags, table_ref=null}}, 2={column={name=id, table_ref=null}}}}}}",
				"[id, tags]",
				"{}",
				"{archived_events={id=[[@6,35:36='id',<455>,1:35]], tags=[[@4,29:32='tags',<455>,1:29]]}, events={active=[[@23,90:90='a',<455>,1:90]], id=[[@16,65:65='a',<455>,1:65]], tags=[[@11,56:56='a',<455>,1:56]]}}",
				"{query0={unnamed_0=[[@14,62:62=')',<288>,1:62]], id=[[@18,67:68='id',<455>,1:67]]}, insert1={id=[[@6,35:36='id',<455>,1:35]], tags=[[@4,29:32='tags',<455>,1:29]]}}",
				"{def_insert1={query_dictionary={id=[[@6,35:36='id',<455>,1:35]], tags=[[@4,29:32='tags',<455>,1:29]]}, table_dictionary={archived_events={id=[[@6,35:36='id',<455>,1:35]], tags=[[@4,29:32='tags',<455>,1:29]]}}, def_query0={query_dictionary={unnamed_0=[[@14,62:62=')',<288>,1:62]], id=[[@18,67:68='id',<455>,1:67]]}, table_dictionary={events={active=[[@23,90:90='a',<455>,1:90]], id=[[@16,65:65='a',<455>,1:65]], tags=[[@11,56:56='a',<455>,1:56]]}}, filters=[{name=active, table_ref=a}], interface={unnamed_0=[{name=tags, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}, interface={tags=[{name=unnamed_0, table_ref=query0}], id=[{name=id, table_ref=query0}]}}}");
	}

	@Test
	public void arC40_arrayMinUpdatePlacementV0Test() {
		final String query = "UPDATE events SET tags = array_min(tags), active = 1 WHERE id > 0";
		final SqlParseEventWalker extractor = runUnaryQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_min' at (l:1 c:25) is recognized as Snowflake-specific grammar.",
						1, 25, "SqlASTWalkerHelper", null, null,
						true, "ast-walk", null, null));
		assertOutputs(
				extractor,
				"{SQL={update={table={alias=null, table=events}, where={condition={left={column={name=id, table_ref=null}}, right={literal=0}, operator=>}}, assignments={1={set={column={name=tags, table_ref=null}}, to={function={function_name=array_min, parameters={1={column={name=tags, table_ref=null}}}}}}, 2={set={column={name=active, table_ref=null}}, to={literal=1}}}}}}",
				"[active, tags]",
				"{}",
				"{events={id=[[@14,59:60='id',<455>,1:59]], tags=[[@7,35:38='tags',<455>,1:35]]}}",
				"{update0={active=[[@10,42:47='active',<455>,1:42]], tags=[[@3,18:21='tags',<455>,1:18]]}}",
				"{def_update0={assignments={active=[], tags=[{name=tags, table_ref=events}]}, table_dictionary={events={id=[[@14,59:60='id',<455>,1:59]], tags=[[@7,35:38='tags',<455>,1:35]]}}, update_dictionary={active=[[@10,42:47='active',<455>,1:42]], tags=[[@3,18:21='tags',<455>,1:18]]}, target_table={events={active=[[@10,42:47='active',<455>,1:42]], tags=[[@3,18:21='tags',<455>,1:18], [@7,35:38='tags',<455>,1:35], [@8,39:39=')',<288>,1:39]]}}, filters=[{name=id, table_ref=events}], lhs_unresolved_columns={active={column={name=active, table_ref=null}, locations=[[@10,42:47='active',<455>,1:42]]}, tags={column={name=tags, table_ref=null}, locations=[[@3,18:21='tags',<455>,1:18], [@7,35:38='tags',<455>,1:35], [@8,39:39=')',<288>,1:39]]}}}}");
	}

	@Test
	public void arC40_arrayMinDeletePlacementV0Test() {
		final String query = "DELETE FROM events WHERE array_min(tags) IS NULL AND id > 0";
		final SqlParseEventWalker extractor = runUnaryQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_min' at (l:1 c:25) is recognized as Snowflake-specific grammar.",
						1, 25, "SqlASTWalkerHelper", null, null,
						true, "ast-walk", null, null));
		assertOutputs(
				extractor,
				"{SQL={delete={table={alias=null, table=events}, where={and={1={condition={left={function={function_name=array_min, parameters={1={column={name=tags, table_ref=null}}}}}, operator=IS NULL}}, 2={condition={left={column={name=id, table_ref=null}}, right={literal=0}, operator=>}}}}}}}",
				"[]",
				"{}",
				"{events={id=[[@11,53:54='id',<455>,1:53]], tags=[[@6,35:38='tags',<455>,1:35]]}}",
				"{}",
				"{def_delete0={query_dictionary={}, table_dictionary={events={id=[[@11,53:54='id',<455>,1:53]], tags=[[@6,35:38='tags',<455>,1:35]]}}, filters=[{name=tags, table_ref=events}, {name=id, table_ref=events}], interface=null}}");
	}

	@Test
	public void arC40_arrayMinDdlPlacementV0Test() {
		final String query = "CREATE VIEW v_arr AS SELECT a.id, array_min(a.tags) AS result_tags FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SqlParseEventWalker extractor = runDdlParsertest(query, parser);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_min' at (l:1 c:34) is recognized as Snowflake-specific grammar.",
						1, 34, "SqlASTWalkerHelper", null, null,
						true, "ast-walk", null, null));
		assertOutputs(
				extractor,
				"{DDL={create={type=VIEW, name={table=v_arr}, query={select={1={column={name=id, table_ref=a}}, 2={function={function_name=array_min, parameters={1={column={name=tags, table_ref=a}}}}, alias=result_tags}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}}}",
				"[]",
				"{}",
				"{events={active=[[@21,87:87='a',<455>,1:87]], id=[[@5,28:28='a',<455>,1:28]], tags=[[@11,44:44='a',<455>,1:44]]}}",
				"{query0={result_tags=[[@16,55:65='result_tags',<455>,1:55]], id=[[@7,30:31='id',<455>,1:30]]}}",
				"{def_create1={def_query0={query_dictionary={result_tags=[[@16,55:65='result_tags',<455>,1:55]], id=[[@7,30:31='id',<455>,1:30]]}, table_dictionary={events={active=[[@21,87:87='a',<455>,1:87]], id=[[@5,28:28='a',<455>,1:28]], tags=[[@11,44:44='a',<455>,1:44]]}}, filters=[{name=active, table_ref=a}], interface={result_tags=[{name=tags, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}}");
	}

	@Test
	public void arR20_arrayMinScriptStatementResetV0Test() {
		final String query = "SELECT a.id, array_min(a.tags) AS result_tags FROM events a;\nSELECT b.id FROM archived_events b;\nSELECT c.id, array_min(c.tags) AS result_tags FROM events c;";
		final SQLSelectParserParser parser = parse(query);
		final SqlParseEventWalker extractor = runScriptParsertest(query, parser);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_min' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null,
						true, "ast-walk", null, null),
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_min' at (l:3 c:13) is recognized as Snowflake-specific grammar.",
						3, 13, "SqlASTWalkerHelper", null, null,
						true, "ast-walk", null, null));
		assertOutputs(
				extractor,
				"{SCRIPT={1={select={1={column={name=id, table_ref=a}}, 2={function={function_name=array_min, parameters={1={column={name=tags, table_ref=a}}}}, alias=result_tags}}, from={table={alias=a, table=events}}}, 2={select={1={column={name=id, table_ref=b}}}, from={table={alias=b, table=archived_events}}}, 3={select={1={column={name=id, table_ref=c}}, 2={function={function_name=array_min, parameters={1={column={name=tags, table_ref=c}}}}, alias=result_tags}}, from={table={alias=c, table=events}}}}}",
				"[]",
				"{SCRIPT={1={}, 2={}, 3={}}}",
				"{SCRIPT={1={events={id=[[@1,7:7='a',<455>,1:7]], tags=[[@7,23:23='a',<455>,1:23]]}}, 2={archived_events={id=[[@18,68:68='b',<455>,2:7]]}}, 3={events={id=[[@26,104:104='c',<455>,3:7]], tags=[[@32,120:120='c',<455>,3:23]]}}}}",
				"{SCRIPT={1={query0={result_tags=[[@12,34:44='result_tags',<455>,1:34]], id=[[@3,9:10='id',<455>,1:9]]}}, 2={query0={id=[[@20,70:71='id',<455>,2:9]]}}, 3={query0={result_tags=[[@37,131:141='result_tags',<455>,3:34]], id=[[@28,106:107='id',<455>,3:9]]}}}}",
				"{SCRIPT={1={def_query0={query_dictionary={result_tags=[[@12,34:44='result_tags',<455>,1:34]], id=[[@3,9:10='id',<455>,1:9]]}, table_dictionary={events={id=[[@1,7:7='a',<455>,1:7]], tags=[[@7,23:23='a',<455>,1:23]]}}, interface={result_tags=[{name=tags, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}, 2={def_query0={query_dictionary={id=[[@20,70:71='id',<455>,2:9]]}, table_dictionary={archived_events={id=[[@18,68:68='b',<455>,2:7]]}}, interface={id=[{name=id, table_ref=b}]}, table_alias={b=archived_events}}}, 3={def_query0={query_dictionary={result_tags=[[@37,131:141='result_tags',<455>,3:34]], id=[[@28,106:107='id',<455>,3:9]]}, table_dictionary={events={id=[[@26,104:104='c',<455>,3:7]], tags=[[@32,120:120='c',<455>,3:23]]}}, interface={result_tags=[{name=tags, table_ref=c}], id=[{name=id, table_ref=c}]}, table_alias={c=events}}}}}");
	}

	@Test
	public void arR18_arrayMinNestedExpressionV0Test() {
		final String query = "SELECT a.id, coalesce(array_min(a.tags), a.backup_tags) AS result_tags FROM events a WHERE a.active = 1";
		final SqlParseEventWalker extractor = runUnaryQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_min' at (l:1 c:22) is recognized as Snowflake-specific grammar.",
						1, 22, "SqlASTWalkerHelper", null, null,
						true, "ast-walk", null, null));
		assertOutputs(
				extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={parameters={1={function={function_name=array_min, parameters={1={column={name=tags, table_ref=a}}}}}, 2={column={name=backup_tags, table_ref=a}}}, function_name=coalesce}, alias=result_tags}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_tags, id]",
				"{}",
				"{events={active=[[@24,91:91='a',<455>,1:91]], id=[[@1,7:7='a',<455>,1:7]], backup_tags=[[@14,41:41='a',<455>,1:41]], tags=[[@9,32:32='a',<455>,1:32]]}}",
				"{query0={result_tags=[[@19,59:69='result_tags',<455>,1:59]], id=[[@3,9:10='id',<455>,1:9]]}}",
				"{def_query0={query_dictionary={result_tags=[[@19,59:69='result_tags',<455>,1:59]], id=[[@3,9:10='id',<455>,1:9]]}, table_dictionary={events={active=[[@24,91:91='a',<455>,1:91]], id=[[@1,7:7='a',<455>,1:7]], backup_tags=[[@14,41:41='a',<455>,1:41]], tags=[[@9,32:32='a',<455>,1:32]]}}, filters=[{name=active, table_ref=a}], interface={result_tags=[{name=tags, table_ref=a}, {name=backup_tags, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arR18_arrayMinDerivedOutputBoundaryV0Test() {
		final String query = "SELECT d.id, d.result_tags FROM (SELECT a.id, array_min(a.tags) AS result_tags FROM events a) d WHERE d.id > 0";
		final SqlParseEventWalker extractor = runUnaryQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_min' at (l:1 c:46) is recognized as Snowflake-specific grammar.",
						1, 46, "SqlASTWalkerHelper", null, null,
						true, "ast-walk", null, null));
		assertOutputs(
				extractor,
				"{SQL={select={1={column={name=id, table_ref=d}}, 2={column={name=result_tags, table_ref=d}}}, from={table={alias=d, query={select={1={column={name=id, table_ref=a}}, 2={function={function_name=array_min, parameters={1={column={name=tags, table_ref=a}}}}, alias=result_tags}}, from={table={alias=a, table=events}}}}}, where={condition={left={column={name=id, table_ref=d}}, right={literal=0}, operator=>}}}}",
				"[result_tags, id]",
				"{}",
				"{events={id=[[@11,40:40='a',<455>,1:40]], tags=[[@17,56:56='a',<455>,1:56]]}}",
				"{query0={result_tags=[[@22,67:77='result_tags',<455>,1:67], [@5,13:13='d',<455>,1:13]], id=[[@13,42:43='id',<455>,1:42], [@1,7:7='d',<455>,1:7], [@29,102:102='d',<455>,1:102]]}, query1={result_tags=[[@7,15:25='result_tags',<455>,1:15]], id=[[@3,9:10='id',<455>,1:9]]}}",
				"{def_query1={query_dictionary={result_tags=[[@7,15:25='result_tags',<455>,1:15]], id=[[@3,9:10='id',<455>,1:9]]}, def_query0={query_dictionary={result_tags=[[@22,67:77='result_tags',<455>,1:67], [@5,13:13='d',<455>,1:13]], id=[[@13,42:43='id',<455>,1:42], [@1,7:7='d',<455>,1:7], [@29,102:102='d',<455>,1:102]]}, table_dictionary={events={id=[[@11,40:40='a',<455>,1:40]], tags=[[@17,56:56='a',<455>,1:56]]}}, interface={result_tags=[{name=tags, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}, filters=[{name=id, table_ref=d}], interface={result_tags=[{name=result_tags, table_ref=d}], id=[{name=id, table_ref=d}]}, table_alias={d=query0}}}");
	}

	@Test
	public void arR19_arrayMinSetOpSiblingIsolationV0Test() {
		final String query = "SELECT a.id, array_min(a.tags) AS result_tags FROM events a UNION ALL SELECT b.id, b.tags AS result_tags FROM archived_events b";
		final SqlParseEventWalker extractor = runUnaryQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_min' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null,
						true, "ast-walk", null, null));
		assertOutputs(
				extractor,
				"{SQL={union={1={select={1={column={name=id, table_ref=a}}, 2={function={function_name=array_min, parameters={1={column={name=tags, table_ref=a}}}}, alias=result_tags}}, from={table={alias=a, table=events}}}, 2={union={qualifier=ALL, operator=UNION}}, 3={select={1={column={name=id, table_ref=b}}, 2={column={name=tags, table_ref=b}, alias=result_tags}}, from={table={alias=b, table=archived_events}}}}}}",
				"[result_tags, id]",
				"{}",
				"{archived_events={id=[[@19,77:77='b',<455>,1:77]], tags=[[@23,83:83='b',<455>,1:83]]}, events={id=[[@1,7:7='a',<455>,1:7]], tags=[[@7,23:23='a',<455>,1:23]]}}",
				"{query0={result_tags=[[@12,34:44='result_tags',<455>,1:34]], id=[[@3,9:10='id',<455>,1:9]]}, query1={result_tags=[[@27,93:103='result_tags',<455>,1:93]], id=[[@21,79:80='id',<455>,1:79]]}}",
				"{def_union2={def_query1={query_dictionary={result_tags=[[@27,93:103='result_tags',<455>,1:93]], id=[[@21,79:80='id',<455>,1:79]]}, table_dictionary={archived_events={id=[[@19,77:77='b',<455>,1:77]], tags=[[@23,83:83='b',<455>,1:83]]}}, setop=UNION, interface={result_tags=[{name=tags, table_ref=b}], id=[{name=id, table_ref=b}]}, table_alias={b=archived_events}}, def_query0={query_dictionary={result_tags=[[@12,34:44='result_tags',<455>,1:34]], id=[[@3,9:10='id',<455>,1:9]]}, table_dictionary={events={id=[[@1,7:7='a',<455>,1:7]], tags=[[@7,23:23='a',<455>,1:23]]}}, interface={result_tags=[{name=tags, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}, interface={result_tags=query_column, id=query_column}}}");
	}

	@Test
	public void arC40_arrayMinUnqualifiedOperandV0Test() {
		final String query = "SELECT id, array_min(tags) AS result_tags FROM events WHERE active = 1";
		final SqlParseEventWalker extractor = runUnaryQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_min' at (l:1 c:11) is recognized as Snowflake-specific grammar.",
						1, 11, "SqlASTWalkerHelper", null, null,
						true, "ast-walk", null, null));
		assertOutputs(
				extractor,
				"{SQL={select={1={column={name=id, table_ref=null}}, 2={function={function_name=array_min, parameters={1={column={name=tags, table_ref=null}}}}, alias=result_tags}}, from={table={alias=null, table=events}}, where={condition={left={column={name=active, table_ref=null}}, right={literal=1}, operator==}}}}",
				"[result_tags, id]",
				"{}",
				"{events={active=[[@12,60:65='active',<455>,1:60]], id=[[@1,7:8='id',<455>,1:7]], tags=[[@5,21:24='tags',<455>,1:21]]}}",
				"{query0={result_tags=[[@8,30:40='result_tags',<455>,1:30]], id=[[@1,7:8='id',<455>,1:7]]}}",
				"{def_query0={query_dictionary={result_tags=[[@8,30:40='result_tags',<455>,1:30]], id=[[@1,7:8='id',<455>,1:7]]}, table_dictionary={events={active=[[@12,60:65='active',<455>,1:60]], id=[[@1,7:8='id',<455>,1:7]], tags=[[@5,21:24='tags',<455>,1:21]]}}, filters=[{name=active, table_ref=events}], interface={result_tags=[{name=tags, table_ref=events}], id=[{name=id, table_ref=events}]}}}");
	}

	@Test
	public void arM20_arrayMinUnresolvedOperandV0Test() {
		final String query = "SELECT array_min(missing_tags) AS result_tags";
		final SqlParseEventWalker extractor = runUnaryQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_min' at (l:1 c:7) is recognized as Snowflake-specific grammar.",
						1, 7, "SqlASTWalkerHelper", null, null,
						true, "ast-walk", null, null),
				new ParseDiagnostic(ParseDiagnostic.Severity.ERROR,
						"UNRESOLVED_UNQUALIFIED_COLUMNS",
						"Unresolved unqualified column reference(s): [missing_tags [(l:1 c:17)]]",
						1, 17, "SqlASTWalkerHelper", null, "missing_tags",
						false, "ast-walk", null, null));
		assertOutputs(
				extractor,
				"{SQL={select={1={function={function_name=array_min, parameters={1={column={name=missing_tags, table_ref=null}}}}, alias=result_tags}}}}",
				"[result_tags]",
				"{}",
				"{}",
				"{query0={result_tags=[[@6,34:44='result_tags',<455>,1:34]]}}",
				"{def_query0={query_dictionary={result_tags=[[@6,34:44='result_tags',<455>,1:34]]}, interface={result_tags=[{name=missing_tags, table_ref=null}]}}}");
	}

	@Test
	public void arM21_arrayMinAmbiguousOperandV0Test() {
		final String query = "SELECT array_min(tags) AS result_tags FROM events a JOIN archived_events b ON a.id = b.id";
		final SqlParseEventWalker extractor = runUnaryQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_min' at (l:1 c:7) is recognized as Snowflake-specific grammar.",
						1, 7, "SqlASTWalkerHelper", null, null,
						true, "ast-walk", null, null),
				new ParseDiagnostic(ParseDiagnostic.Severity.SEVERE_WARNING,
						"AMBIGUOUS_COLUMN_REFERENCE",
						"Ambiguous column reference 'tags' at (l:1 c:17). Possible sources: [archived_events, events]",
						1, 17, "SqlASTWalkerHelper", null, "tags",
						true, "ast-walk", null, null),
				new ParseDiagnostic(ParseDiagnostic.Severity.ERROR,
						"UNRESOLVED_UNQUALIFIED_COLUMNS",
						"Unresolved unqualified column reference(s): [tags [(l:1 c:17)]]",
						1, 17, "SqlASTWalkerHelper", null, "tags",
						false, "ast-walk", null, null));
		assertOutputs(
				extractor,
				"{SQL={select={1={function={function_name=array_min, parameters={1={column={name=tags, table_ref=null}}}}, alias=result_tags}}, from={join={1={table={alias=a, table=events}}, 2={join=JOIN, on={condition={left={column={name=id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=archived_events}}}}}}",
				"[result_tags]",
				"{}",
				"{archived_events={id=[[@18,85:85='b',<455>,1:85]]}, events={id=[[@14,78:78='a',<455>,1:78]]}}",
				"{query0={result_tags=[[@6,26:36='result_tags',<455>,1:26]]}}",
				"{def_query0={query_dictionary={result_tags=[[@6,26:36='result_tags',<455>,1:26]]}, table_dictionary={archived_events={id=[[@18,85:85='b',<455>,1:85]]}, events={id=[[@14,78:78='a',<455>,1:78]]}}, filters=[{name=id, table_ref=a}, {name=id, table_ref=b}], interface={result_tags=[{name=tags, table_ref=null}]}, table_alias={a=events, b=archived_events}}}");
	}

	@Test
	public void arM22_arrayMinMissingAliasOperandV0Test() {
		final String query = "SELECT a.id, array_min(z.tags) AS result_tags FROM events a WHERE a.active = 1";
		final SqlParseEventWalker extractor = runUnaryQuery(query);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_min' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null,
						true, "ast-walk", null, null),
				new ParseDiagnostic(ParseDiagnostic.Severity.FATAL,
						"QUALIFIED_COLUMN_NOT_FOUND_IN_TABLE",
						"Source Table not found for Column 'tags' at (l:1 c:34). No alias or table called 'z'.",
						1, 34, "SqlASTWalkerHelper", null, "tags",
						false, "ast-walk", null, null));
		assertOutputs(
				extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={function_name=array_min, parameters={1={column={name=tags, table_ref=z}}}}, alias=result_tags}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_tags, id]",
				"{}",
				"{events={active=[[@17,66:66='a',<455>,1:66]], id=[[@1,7:7='a',<455>,1:7]]}}",
				"{query0={result_tags=[[@12,34:44='result_tags',<455>,1:34]], id=[[@3,9:10='id',<455>,1:9]]}}",
				"{def_query0={query_dictionary={result_tags=[[@12,34:44='result_tags',<455>,1:34]], id=[[@3,9:10='id',<455>,1:9]]}, table_dictionary={events={active=[[@17,66:66='a',<455>,1:66]], id=[[@1,7:7='a',<455>,1:7]]}}, filters=[{name=active, table_ref=a}], interface={result_tags=[{name=tags, table_ref=z}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arM22_arrayMinHiddenAliasOperandV0Test() {
		final String query = "SELECT d.id, array_min(a.tags) AS result_tags FROM (SELECT a.id, a.tags FROM events a) d WHERE d.id > 0";
		final SqlParseEventWalker extractor = runUnaryQuery(query);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_min' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null,
						true, "ast-walk", null, null),
				new ParseDiagnostic(ParseDiagnostic.Severity.FATAL,
						"QUALIFIED_COLUMN_NOT_FOUND_IN_TABLE",
						"Source Table not found for Column 'tags' at (l:1 c:34). No alias or table called 'a'.",
						1, 34, "SqlASTWalkerHelper", null, "tags",
						false, "ast-walk", null, null));
		assertOutputs(
				extractor,
				"{SQL={select={1={column={name=id, table_ref=d}}, 2={function={function_name=array_min, parameters={1={column={name=tags, table_ref=a}}}}, alias=result_tags}}, from={table={alias=d, query={select={1={column={name=id, table_ref=a}}, 2={column={name=tags, table_ref=a}}}, from={table={alias=a, table=events}}}}}, where={condition={left={column={name=id, table_ref=d}}, right={literal=0}, operator=>}}}}",
				"[result_tags, id]",
				"{}",
				"{events={id=[[@16,59:59='a',<455>,1:59]], tags=[[@20,65:65='a',<455>,1:65]]}}",
				"{query0={id=[[@18,61:62='id',<455>,1:61], [@1,7:7='d',<455>,1:7], [@29,95:95='d',<455>,1:95]], tags=[[@22,67:70='tags',<455>,1:67]]}, query1={result_tags=[[@12,34:44='result_tags',<455>,1:34]], id=[[@3,9:10='id',<455>,1:9]]}}",
				"{def_query1={query_dictionary={result_tags=[[@12,34:44='result_tags',<455>,1:34]], id=[[@3,9:10='id',<455>,1:9]]}, table_dictionary={}, def_query0={query_dictionary={id=[[@18,61:62='id',<455>,1:61], [@1,7:7='d',<455>,1:7], [@29,95:95='d',<455>,1:95]], tags=[[@22,67:70='tags',<455>,1:67]]}, table_dictionary={events={id=[[@16,59:59='a',<455>,1:59]], tags=[[@20,65:65='a',<455>,1:65]]}}, interface={id=[{name=id, table_ref=a}], tags=[{name=tags, table_ref=a}]}, table_alias={a=events}}, filters=[{name=id, table_ref=d}], interface={result_tags=[{name=tags, table_ref=a}], id=[{name=id, table_ref=d}]}, table_alias={d=query0}}}");
	}

	@Test
	public void arC40_arrayMinPredicandVariableV0Test() {
		final String query = "SELECT a.id, array_min(<tags_expr>) AS result_tags FROM events a WHERE a.active = 1";
		final SqlParseEventWalker extractor = runUnaryQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_min' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null,
						true, "ast-walk", null, null));
		assertOutputs(
				extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={function_name=array_min, parameters={1={substitution={name=<tags_expr>, type=predicand}}}}, alias=result_tags}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_tags, id]",
				"{<tags_expr>=predicand}",
				"{events={active=[[@15,71:71='a',<455>,1:71]], id=[[@1,7:7='a',<455>,1:7]]}}",
				"{query0={result_tags=[[@10,39:49='result_tags',<455>,1:39]], id=[[@3,9:10='id',<455>,1:9]]}}",
				"{def_query0={query_dictionary={result_tags=[[@10,39:49='result_tags',<455>,1:39]], id=[[@3,9:10='id',<455>,1:9]]}, table_dictionary={events={active=[[@15,71:71='a',<455>,1:71]], id=[[@1,7:7='a',<455>,1:7]]}}, filters=[{name=active, table_ref=a}], interface={result_tags=[{name=<tags_expr>, type=predicand}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC40_arrayMinQualifiedColumnVariableV0Test() {
		final String query = "SELECT a.id, array_min(a.<tags_col>) AS result_tags FROM events a WHERE a.active = 1";
		final SqlParseEventWalker extractor = runUnaryQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_min' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null,
						true, "ast-walk", null, null));
		assertOutputs(
				extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={function_name=array_min, parameters={1={column={substitution={name=<tags_col>, type=column}, table_ref=a}}}}, alias=result_tags}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_tags, id]",
				"{<tags_col>=column}",
				"{events={active=[[@17,72:72='a',<455>,1:72]], id=[[@1,7:7='a',<455>,1:7]], <tags_col>=[[@7,23:23='a',<455>,1:23]]}}",
				"{query0={result_tags=[[@12,40:50='result_tags',<455>,1:40]], id=[[@3,9:10='id',<455>,1:9]]}}",
				"{def_query0={query_dictionary={result_tags=[[@12,40:50='result_tags',<455>,1:40]], id=[[@3,9:10='id',<455>,1:9]]}, table_dictionary={events={active=[[@17,72:72='a',<455>,1:72]], id=[[@1,7:7='a',<455>,1:7]], <tags_col>=[[@7,23:23='a',<455>,1:23]]}}, filters=[{name=active, table_ref=a}], interface={result_tags=[{substitution={name=<tags_col>, type=column}, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arM23_arrayMinSchemaQualifiedRoutineControlV0Test() {
		final String query = "SELECT a.id, custom.array_min(a.tags) AS result_tags FROM events a WHERE a.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(
				extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={schema=custom, parameters={1={column={name=tags, table_ref=a}}}, function_name=array_min}, alias=result_tags}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_tags, id]",
				"{}",
				"{events={active=[[@19,73:73='a',<455>,1:73]], id=[[@1,7:7='a',<455>,1:7]], tags=[[@9,30:30='a',<455>,1:30]]}}",
				"{query0={result_tags=[[@14,41:51='result_tags',<455>,1:41]], id=[[@3,9:10='id',<455>,1:9]]}}",
				"{def_query0={query_dictionary={result_tags=[[@14,41:51='result_tags',<455>,1:41]], id=[[@3,9:10='id',<455>,1:9]]}, table_dictionary={events={active=[[@19,73:73='a',<455>,1:73]], id=[[@1,7:7='a',<455>,1:7]], tags=[[@9,30:30='a',<455>,1:30]]}}, filters=[{name=active, table_ref=a}], interface={result_tags=[{name=tags, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arM24_arrayMinMixedDialectControlV0Test() {
		final String query = "SELECT array_min(a.tags) AS result_tags, extract(isodow from a.created_at) AS weekday FROM events a";
		final SqlParseEventWalker extractor = runUnaryQuery(query);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_min' at (l:1 c:7) is recognized as Snowflake-specific grammar.",
						1, 7, "SqlASTWalkerHelper", null, null,
						true, "ast-walk", null, null),
				new ParseDiagnostic(ParseDiagnostic.Severity.FATAL,
						"STATEMENT_MIXED_DIALECT_GRAMMAR",
						"Statement mixes Snowflake-only and PostgreSQL-only grammar at (l:1 c:41); unlikely to run on either engine.",
						1, 41, "SqlASTWalkerHelper", null, null,
						false, "ast-walk", null, null));
		assertOutputs(
				extractor,
				"{SQL={select={1={function={function_name=array_min, parameters={1={column={name=tags, table_ref=a}}}}, alias=result_tags}, 2={alias=weekday, datetime_function={function_name=extract, part_form=KEYWORD, part=isodow, source={column={name=created_at, table_ref=a}}}}}, from={table={alias=a, table=events}}}}",
				"[result_tags, weekday]",
				"{}",
				"{events={created_at=[[@14,61:61='a',<455>,1:61]], tags=[[@3,17:17='a',<455>,1:17]]}}",
				"{query0={weekday=[[@19,78:84='weekday',<455>,1:78]], result_tags=[[@8,28:38='result_tags',<455>,1:28]]}}",
				"{def_query0={query_dictionary={result_tags=[[@8,28:38='result_tags',<455>,1:28]], weekday=[[@19,78:84='weekday',<455>,1:78]]}, table_dictionary={events={created_at=[[@14,61:61='a',<455>,1:61]], tags=[[@3,17:17='a',<455>,1:17]]}}, interface={result_tags=[{name=tags, table_ref=a}], weekday=[{name=created_at, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC40_arrayMinUppercaseNameV0Test() {
		final String query = "SELECT a.id, ARRAY_MIN(a.tags) AS result_tags FROM events a WHERE a.active = 1";
		final SqlParseEventWalker extractor = runUnaryQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_min' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null,
						true, "ast-walk", null, null));
		assertOutputs(
				extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={function_name=ARRAY_MIN, parameters={1={column={name=tags, table_ref=a}}}}, alias=result_tags}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_tags, id]",
				"{}",
				"{events={active=[[@17,66:66='a',<455>,1:66]], id=[[@1,7:7='a',<455>,1:7]], tags=[[@7,23:23='a',<455>,1:23]]}}",
				"{query0={result_tags=[[@12,34:44='result_tags',<455>,1:34]], id=[[@3,9:10='id',<455>,1:9]]}}",
				"{def_query0={query_dictionary={result_tags=[[@12,34:44='result_tags',<455>,1:34]], id=[[@3,9:10='id',<455>,1:9]]}, table_dictionary={events={active=[[@17,66:66='a',<455>,1:66]], id=[[@1,7:7='a',<455>,1:7]], tags=[[@7,23:23='a',<455>,1:23]]}}, filters=[{name=active, table_ref=a}], interface={result_tags=[{name=tags, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC39_arrayReverseSelectPlacementV0Test() {
		final String query = "SELECT a.id, array_reverse(a.tags) AS result_tags FROM events a WHERE a.active = 1";
		final SqlParseEventWalker extractor = runUnaryQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_reverse' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null,
						true, "ast-walk", null, null));
		assertOutputs(
				extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={function_name=array_reverse, parameters={1={column={name=tags, table_ref=a}}}}, alias=result_tags}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_tags, id]",
				"{}",
				"{events={active=[[@17,70:70='a',<455>,1:70]], id=[[@1,7:7='a',<455>,1:7]], tags=[[@7,27:27='a',<455>,1:27]]}}",
				"{query0={result_tags=[[@12,38:48='result_tags',<455>,1:38]], id=[[@3,9:10='id',<455>,1:9]]}}",
				"{def_query0={query_dictionary={result_tags=[[@12,38:48='result_tags',<455>,1:38]], id=[[@3,9:10='id',<455>,1:9]]}, table_dictionary={events={active=[[@17,70:70='a',<455>,1:70]], id=[[@1,7:7='a',<455>,1:7]], tags=[[@7,27:27='a',<455>,1:27]]}}, filters=[{name=active, table_ref=a}], interface={result_tags=[{name=tags, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC39_arrayReverseWherePlacementV0Test() {
		final String query = "SELECT a.id FROM events a WHERE array_reverse(a.tags) IS NOT NULL AND a.active = 1";
		final SqlParseEventWalker extractor = runUnaryQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_reverse' at (l:1 c:32) is recognized as Snowflake-specific grammar.",
						1, 32, "SqlASTWalkerHelper", null, null,
						true, "ast-walk", null, null));
		assertOutputs(
				extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}}, from={table={alias=a, table=events}}, where={and={1={condition={left={function={function_name=array_reverse, parameters={1={column={name=tags, table_ref=a}}}}}, operator=IS NOT NULL}}, 2={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}}}",
				"[id]",
				"{}",
				"{events={active=[[@18,70:70='a',<455>,1:70]], id=[[@1,7:7='a',<455>,1:7]], tags=[[@10,46:46='a',<455>,1:46]]}}",
				"{query0={id=[[@3,9:10='id',<455>,1:9]]}}",
				"{def_query0={query_dictionary={id=[[@3,9:10='id',<455>,1:9]]}, table_dictionary={events={active=[[@18,70:70='a',<455>,1:70]], id=[[@1,7:7='a',<455>,1:7]], tags=[[@10,46:46='a',<455>,1:46]]}}, filters=[{name=tags, table_ref=a}, {name=active, table_ref=a}], interface={id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC39_arrayReverseJoinOnPlacementV0Test() {
		final String query = "SELECT a.id, b.id AS other_id FROM events a JOIN archived_events b ON array_reverse(a.tags) = b.tags AND a.id = b.id";
		final SqlParseEventWalker extractor = runUnaryQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_reverse' at (l:1 c:70) is recognized as Snowflake-specific grammar.",
						1, 70, "SqlASTWalkerHelper", null, null,
						true, "ast-walk", null, null));
		assertOutputs(
				extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={column={name=id, table_ref=b}, alias=other_id}}, from={join={1={table={alias=a, table=events}}, 2={join=JOIN, on={and={1={condition={left={function={function_name=array_reverse, parameters={1={column={name=tags, table_ref=a}}}}}, right={column={name=tags, table_ref=b}}, operator==}}, 2={condition={left={column={name=id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}}}, 3={table={alias=b, table=archived_events}}}}}}",
				"[id, other_id]",
				"{}",
				"{archived_events={id=[[@5,13:13='b',<455>,1:13], [@32,112:112='b',<455>,1:112]], tags=[[@24,94:94='b',<455>,1:94]]}, events={id=[[@1,7:7='a',<455>,1:7], [@28,105:105='a',<455>,1:105]], tags=[[@19,84:84='a',<455>,1:84]]}}",
				"{query0={id=[[@3,9:10='id',<455>,1:9]], other_id=[[@9,21:28='other_id',<455>,1:21]]}}",
				"{def_query0={query_dictionary={id=[[@3,9:10='id',<455>,1:9]], other_id=[[@9,21:28='other_id',<455>,1:21]]}, table_dictionary={archived_events={id=[[@5,13:13='b',<455>,1:13], [@32,112:112='b',<455>,1:112]], tags=[[@24,94:94='b',<455>,1:94]]}, events={id=[[@1,7:7='a',<455>,1:7], [@28,105:105='a',<455>,1:105]], tags=[[@19,84:84='a',<455>,1:84]]}}, filters=[{name=tags, table_ref=a}, {name=tags, table_ref=b}, {name=id, table_ref=a}, {name=id, table_ref=b}], interface={id=[{name=id, table_ref=a}], other_id=[{name=id, table_ref=b}]}, table_alias={a=events, b=archived_events}}}");
	}

	@Test
	public void arC39_arrayReverseHavingPlacementV0Test() {
		final String query = "SELECT a.group_id, count(*) AS n FROM events a GROUP BY a.group_id HAVING array_reverse(array_agg(a.tags)) IS NOT NULL AND count(*) > 1";
		final SqlParseEventWalker extractor = runUnaryQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_reverse' at (l:1 c:74) is recognized as Snowflake-specific grammar.",
						1, 74, "SqlASTWalkerHelper", null, null,
						true, "ast-walk", null, null));
		assertOutputs(
				extractor,
				"{SQL={select={1={column={name=group_id, table_ref=a}}, 2={function={function_name=COUNT, qualifier=null, parameters=*}, alias=n}}, having={and={1={condition={left={function={function_name=array_reverse, parameters={1={function={function_name=array_agg, qualifier=null, parameters={column={name=tags, table_ref=a}}}}}}}, operator=IS NOT NULL}}, 2={condition={left={function={function_name=COUNT, qualifier=null, parameters=*}}, right={literal=1}, operator=>}}}}, from={table={alias=a, table=events}}, groupby={1={column={name=group_id, table_ref=a}}}}}",
				"[group_id, n]",
				"{}",
				"{events={group_id=[[@1,7:7='a',<455>,1:7], [@16,56:56='a',<455>,1:56]], *=[[@7,25:25='*',<291>,1:25], [@35,129:129='*',<291>,1:129]], tags=[[@24,98:98='a',<455>,1:98]]}}",
				"{query0={group_id=[[@3,9:16='group_id',<455>,1:9]], n=[[@10,31:31='n',<455>,1:31]]}}",
				"{def_query0={query_dictionary={group_id=[[@3,9:16='group_id',<455>,1:9]], n=[[@10,31:31='n',<455>,1:31]]}, table_dictionary={events={group_id=[[@1,7:7='a',<455>,1:7], [@16,56:56='a',<455>,1:56]], *=[[@7,25:25='*',<291>,1:25], [@35,129:129='*',<291>,1:129]], tags=[[@24,98:98='a',<455>,1:98]]}}, grouped_by=[{name=group_id, table_ref=a}], filters=[{name=tags, table_ref=a}], interface={group_id=[{name=group_id, table_ref=a}], n=[]}, table_alias={a=events}}}");
	}

	@Test
	public void arC39_arrayReverseQualifyPlacementV0Test() {
		final String query = "SELECT a.id FROM events a QUALIFY array_reverse(a.tags) IS NOT NULL AND a.id > 0";
		final SqlParseEventWalker extractor = runUnaryQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_reverse' at (l:1 c:34) is recognized as Snowflake-specific grammar.",
						1, 34, "SqlASTWalkerHelper", null, null,
						true, "ast-walk", null, null));
		assertOutputs(
				extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}}, from={table={alias=a, table=events}}, qualify={and={1={condition={left={function={function_name=array_reverse, parameters={1={column={name=tags, table_ref=a}}}}}, operator=IS NOT NULL}}, 2={condition={left={column={name=id, table_ref=a}}, right={literal=0}, operator=>}}}}}}",
				"[id]",
				"{}",
				"{events={id=[[@1,7:7='a',<455>,1:7], [@18,72:72='a',<455>,1:72]], tags=[[@10,48:48='a',<455>,1:48]]}}",
				"{query0={id=[[@3,9:10='id',<455>,1:9]]}}",
				"{def_query0={query_dictionary={id=[[@3,9:10='id',<455>,1:9]]}, table_dictionary={events={id=[[@1,7:7='a',<455>,1:7], [@18,72:72='a',<455>,1:72]], tags=[[@10,48:48='a',<455>,1:48]]}}, filters=[{name=tags, table_ref=a}, {name=id, table_ref=a}], interface={id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC39_arrayReverseOrderByPlacementV0Test() {
		final String query = "SELECT a.id FROM events a WHERE a.active = 1 ORDER BY array_reverse(a.tags), a.id";
		final SqlParseEventWalker extractor = runUnaryQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_reverse' at (l:1 c:54) is recognized as Snowflake-specific grammar.",
						1, 54, "SqlASTWalkerHelper", null, null,
						true, "ast-walk", null, null));
		assertOutputs(
				extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}}, orderby={1={null_order=null, predicand={function={function_name=array_reverse, parameters={1={column={name=tags, table_ref=a}}}}}, sort_order=ASC}, 2={null_order=null, predicand={column={name=id, table_ref=a}}, sort_order=ASC}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[id]",
				"{}",
				"{events={active=[[@8,32:32='a',<455>,1:32]], id=[[@1,7:7='a',<455>,1:7], [@22,77:77='a',<455>,1:77]], tags=[[@17,68:68='a',<455>,1:68]]}}",
				"{query0={id=[[@3,9:10='id',<455>,1:9]]}}",
				"{def_query0={query_dictionary={id=[[@3,9:10='id',<455>,1:9]]}, table_dictionary={events={active=[[@8,32:32='a',<455>,1:32]], id=[[@1,7:7='a',<455>,1:7], [@22,77:77='a',<455>,1:77]], tags=[[@17,68:68='a',<455>,1:68]]}}, ordered_by=[{name=tags, table_ref=a}, {name=id, table_ref=a}], filters=[{name=active, table_ref=a}], interface={id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC39_arrayReverseValuesPlacementV0Test() {
		final String query = "VALUES (array_reverse(<tags_expr>), 7)";
		final SqlParseEventWalker extractor = runUnaryQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_reverse' at (l:1 c:8) is recognized as Snowflake-specific grammar.",
						1, 8, "SqlASTWalkerHelper", null, null,
						true, "ast-walk", null, null));
		assertOutputs(
				extractor,
				"{SQL={values={matrix={1={row={1={function={function_name=array_reverse, parameters={1={substitution={name=<tags_expr>, type=predicand}}}}}, 2={literal=7}}}}}}}",
				"[$1, $2]",
				"{<tags_expr>=predicand}",
				"{}",
				"{values0={$1=[[@1,7:7='(',<287>,1:7]], $2=[[@1,7:7='(',<287>,1:7]]}}",
				"{def_values0={query_dictionary={$1=[[@1,7:7='(',<287>,1:7]], $2=[[@1,7:7='(',<287>,1:7]]}, interface={$1=[], $2=[]}}}");
	}

	@Test
	public void arC39_arrayReverseInsertPlacementV0Test() {
		final String query = "INSERT INTO archived_events (tags, id) SELECT array_reverse(a.tags), a.id FROM events a WHERE a.active = 1";
		final SqlParseEventWalker extractor = runUnaryQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_reverse' at (l:1 c:46) is recognized as Snowflake-specific grammar.",
						1, 46, "SqlASTWalkerHelper", null, null,
						true, "ast-walk", null, null));
		assertOutputs(
				extractor,
				"{SQL={insert={preamble=insert_into, from={from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}, select={1={function={function_name=array_reverse, parameters={1={column={name=tags, table_ref=a}}}}}, 2={column={name=id, table_ref=a}}}}, target_table={table={alias=null, table=archived_events}}, columns={1={column={name=tags, table_ref=null}}, 2={column={name=id, table_ref=null}}}}}}",
				"[id, tags]",
				"{}",
				"{archived_events={id=[[@6,35:36='id',<455>,1:35]], tags=[[@4,29:32='tags',<455>,1:29]]}, events={active=[[@23,94:94='a',<455>,1:94]], id=[[@16,69:69='a',<455>,1:69]], tags=[[@11,60:60='a',<455>,1:60]]}}",
				"{query0={unnamed_0=[[@14,66:66=')',<288>,1:66]], id=[[@18,71:72='id',<455>,1:71]]}, insert1={id=[[@6,35:36='id',<455>,1:35]], tags=[[@4,29:32='tags',<455>,1:29]]}}",
				"{def_insert1={query_dictionary={id=[[@6,35:36='id',<455>,1:35]], tags=[[@4,29:32='tags',<455>,1:29]]}, table_dictionary={archived_events={id=[[@6,35:36='id',<455>,1:35]], tags=[[@4,29:32='tags',<455>,1:29]]}}, def_query0={query_dictionary={unnamed_0=[[@14,66:66=')',<288>,1:66]], id=[[@18,71:72='id',<455>,1:71]]}, table_dictionary={events={active=[[@23,94:94='a',<455>,1:94]], id=[[@16,69:69='a',<455>,1:69]], tags=[[@11,60:60='a',<455>,1:60]]}}, filters=[{name=active, table_ref=a}], interface={unnamed_0=[{name=tags, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}, interface={tags=[{name=unnamed_0, table_ref=query0}], id=[{name=id, table_ref=query0}]}}}");
	}

	@Test
	public void arC39_arrayReverseUpdatePlacementV0Test() {
		final String query = "UPDATE events SET tags = array_reverse(tags), active = 1 WHERE id > 0";
		final SqlParseEventWalker extractor = runUnaryQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_reverse' at (l:1 c:25) is recognized as Snowflake-specific grammar.",
						1, 25, "SqlASTWalkerHelper", null, null,
						true, "ast-walk", null, null));
		assertOutputs(
				extractor,
				"{SQL={update={table={alias=null, table=events}, where={condition={left={column={name=id, table_ref=null}}, right={literal=0}, operator=>}}, assignments={1={set={column={name=tags, table_ref=null}}, to={function={function_name=array_reverse, parameters={1={column={name=tags, table_ref=null}}}}}}, 2={set={column={name=active, table_ref=null}}, to={literal=1}}}}}}",
				"[active, tags]",
				"{}",
				"{events={id=[[@14,63:64='id',<455>,1:63]], tags=[[@7,39:42='tags',<455>,1:39]]}}",
				"{update0={active=[[@10,46:51='active',<455>,1:46]], tags=[[@3,18:21='tags',<455>,1:18]]}}",
				"{def_update0={assignments={active=[], tags=[{name=tags, table_ref=events}]}, table_dictionary={events={id=[[@14,63:64='id',<455>,1:63]], tags=[[@7,39:42='tags',<455>,1:39]]}}, update_dictionary={active=[[@10,46:51='active',<455>,1:46]], tags=[[@3,18:21='tags',<455>,1:18]]}, target_table={events={active=[[@10,46:51='active',<455>,1:46]], tags=[[@3,18:21='tags',<455>,1:18], [@7,39:42='tags',<455>,1:39], [@8,43:43=')',<288>,1:43]]}}, filters=[{name=id, table_ref=events}], lhs_unresolved_columns={active={column={name=active, table_ref=null}, locations=[[@10,46:51='active',<455>,1:46]]}, tags={column={name=tags, table_ref=null}, locations=[[@3,18:21='tags',<455>,1:18], [@7,39:42='tags',<455>,1:39], [@8,43:43=')',<288>,1:43]]}}}}");
	}

	@Test
	public void arC39_arrayReverseDeletePlacementV0Test() {
		final String query = "DELETE FROM events WHERE array_reverse(tags) IS NULL AND id > 0";
		final SqlParseEventWalker extractor = runUnaryQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_reverse' at (l:1 c:25) is recognized as Snowflake-specific grammar.",
						1, 25, "SqlASTWalkerHelper", null, null,
						true, "ast-walk", null, null));
		assertOutputs(
				extractor,
				"{SQL={delete={table={alias=null, table=events}, where={and={1={condition={left={function={function_name=array_reverse, parameters={1={column={name=tags, table_ref=null}}}}}, operator=IS NULL}}, 2={condition={left={column={name=id, table_ref=null}}, right={literal=0}, operator=>}}}}}}}",
				"[]",
				"{}",
				"{events={id=[[@11,57:58='id',<455>,1:57]], tags=[[@6,39:42='tags',<455>,1:39]]}}",
				"{}",
				"{def_delete0={query_dictionary={}, table_dictionary={events={id=[[@11,57:58='id',<455>,1:57]], tags=[[@6,39:42='tags',<455>,1:39]]}}, filters=[{name=tags, table_ref=events}, {name=id, table_ref=events}], interface=null}}");
	}

	@Test
	public void arC39_arrayReverseDdlPlacementV0Test() {
		final String query = "CREATE VIEW v_arr AS SELECT a.id, array_reverse(a.tags) AS result_tags FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SqlParseEventWalker extractor = runDdlParsertest(query, parser);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_reverse' at (l:1 c:34) is recognized as Snowflake-specific grammar.",
						1, 34, "SqlASTWalkerHelper", null, null,
						true, "ast-walk", null, null));
		assertOutputs(
				extractor,
				"{DDL={create={type=VIEW, name={table=v_arr}, query={select={1={column={name=id, table_ref=a}}, 2={function={function_name=array_reverse, parameters={1={column={name=tags, table_ref=a}}}}, alias=result_tags}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}}}",
				"[]",
				"{}",
				"{events={active=[[@21,91:91='a',<455>,1:91]], id=[[@5,28:28='a',<455>,1:28]], tags=[[@11,48:48='a',<455>,1:48]]}}",
				"{query0={result_tags=[[@16,59:69='result_tags',<455>,1:59]], id=[[@7,30:31='id',<455>,1:30]]}}",
				"{def_create1={def_query0={query_dictionary={result_tags=[[@16,59:69='result_tags',<455>,1:59]], id=[[@7,30:31='id',<455>,1:30]]}, table_dictionary={events={active=[[@21,91:91='a',<455>,1:91]], id=[[@5,28:28='a',<455>,1:28]], tags=[[@11,48:48='a',<455>,1:48]]}}, filters=[{name=active, table_ref=a}], interface={result_tags=[{name=tags, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}}");
	}

	@Test
	public void arR20_arrayReverseScriptStatementResetV0Test() {
		final String query = "SELECT a.id, array_reverse(a.tags) AS result_tags FROM events a;\nSELECT b.id FROM archived_events b;\nSELECT c.id, array_reverse(c.tags) AS result_tags FROM events c;";
		final SQLSelectParserParser parser = parse(query);
		final SqlParseEventWalker extractor = runScriptParsertest(query, parser);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_reverse' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null,
						true, "ast-walk", null, null),
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_reverse' at (l:3 c:13) is recognized as Snowflake-specific grammar.",
						3, 13, "SqlASTWalkerHelper", null, null,
						true, "ast-walk", null, null));
		assertOutputs(
				extractor,
				"{SCRIPT={1={select={1={column={name=id, table_ref=a}}, 2={function={function_name=array_reverse, parameters={1={column={name=tags, table_ref=a}}}}, alias=result_tags}}, from={table={alias=a, table=events}}}, 2={select={1={column={name=id, table_ref=b}}}, from={table={alias=b, table=archived_events}}}, 3={select={1={column={name=id, table_ref=c}}, 2={function={function_name=array_reverse, parameters={1={column={name=tags, table_ref=c}}}}, alias=result_tags}}, from={table={alias=c, table=events}}}}}",
				"[]",
				"{SCRIPT={1={}, 2={}, 3={}}}",
				"{SCRIPT={1={events={id=[[@1,7:7='a',<455>,1:7]], tags=[[@7,27:27='a',<455>,1:27]]}}, 2={archived_events={id=[[@18,72:72='b',<455>,2:7]]}}, 3={events={id=[[@26,108:108='c',<455>,3:7]], tags=[[@32,128:128='c',<455>,3:27]]}}}}",
				"{SCRIPT={1={query0={result_tags=[[@12,38:48='result_tags',<455>,1:38]], id=[[@3,9:10='id',<455>,1:9]]}}, 2={query0={id=[[@20,74:75='id',<455>,2:9]]}}, 3={query0={result_tags=[[@37,139:149='result_tags',<455>,3:38]], id=[[@28,110:111='id',<455>,3:9]]}}}}",
				"{SCRIPT={1={def_query0={query_dictionary={result_tags=[[@12,38:48='result_tags',<455>,1:38]], id=[[@3,9:10='id',<455>,1:9]]}, table_dictionary={events={id=[[@1,7:7='a',<455>,1:7]], tags=[[@7,27:27='a',<455>,1:27]]}}, interface={result_tags=[{name=tags, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}, 2={def_query0={query_dictionary={id=[[@20,74:75='id',<455>,2:9]]}, table_dictionary={archived_events={id=[[@18,72:72='b',<455>,2:7]]}}, interface={id=[{name=id, table_ref=b}]}, table_alias={b=archived_events}}}, 3={def_query0={query_dictionary={result_tags=[[@37,139:149='result_tags',<455>,3:38]], id=[[@28,110:111='id',<455>,3:9]]}, table_dictionary={events={id=[[@26,108:108='c',<455>,3:7]], tags=[[@32,128:128='c',<455>,3:27]]}}, interface={result_tags=[{name=tags, table_ref=c}], id=[{name=id, table_ref=c}]}, table_alias={c=events}}}}}");
	}

	@Test
	public void arR18_arrayReverseNestedExpressionV0Test() {
		final String query = "SELECT a.id, coalesce(array_reverse(a.tags), a.backup_tags) AS result_tags FROM events a WHERE a.active = 1";
		final SqlParseEventWalker extractor = runUnaryQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_reverse' at (l:1 c:22) is recognized as Snowflake-specific grammar.",
						1, 22, "SqlASTWalkerHelper", null, null,
						true, "ast-walk", null, null));
		assertOutputs(
				extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={parameters={1={function={function_name=array_reverse, parameters={1={column={name=tags, table_ref=a}}}}}, 2={column={name=backup_tags, table_ref=a}}}, function_name=coalesce}, alias=result_tags}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_tags, id]",
				"{}",
				"{events={active=[[@24,95:95='a',<455>,1:95]], id=[[@1,7:7='a',<455>,1:7]], backup_tags=[[@14,45:45='a',<455>,1:45]], tags=[[@9,36:36='a',<455>,1:36]]}}",
				"{query0={result_tags=[[@19,63:73='result_tags',<455>,1:63]], id=[[@3,9:10='id',<455>,1:9]]}}",
				"{def_query0={query_dictionary={result_tags=[[@19,63:73='result_tags',<455>,1:63]], id=[[@3,9:10='id',<455>,1:9]]}, table_dictionary={events={active=[[@24,95:95='a',<455>,1:95]], id=[[@1,7:7='a',<455>,1:7]], backup_tags=[[@14,45:45='a',<455>,1:45]], tags=[[@9,36:36='a',<455>,1:36]]}}, filters=[{name=active, table_ref=a}], interface={result_tags=[{name=tags, table_ref=a}, {name=backup_tags, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arR18_arrayReverseDerivedOutputBoundaryV0Test() {
		final String query = "SELECT d.id, d.result_tags FROM (SELECT a.id, array_reverse(a.tags) AS result_tags FROM events a) d WHERE d.id > 0";
		final SqlParseEventWalker extractor = runUnaryQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_reverse' at (l:1 c:46) is recognized as Snowflake-specific grammar.",
						1, 46, "SqlASTWalkerHelper", null, null,
						true, "ast-walk", null, null));
		assertOutputs(
				extractor,
				"{SQL={select={1={column={name=id, table_ref=d}}, 2={column={name=result_tags, table_ref=d}}}, from={table={alias=d, query={select={1={column={name=id, table_ref=a}}, 2={function={function_name=array_reverse, parameters={1={column={name=tags, table_ref=a}}}}, alias=result_tags}}, from={table={alias=a, table=events}}}}}, where={condition={left={column={name=id, table_ref=d}}, right={literal=0}, operator=>}}}}",
				"[result_tags, id]",
				"{}",
				"{events={id=[[@11,40:40='a',<455>,1:40]], tags=[[@17,60:60='a',<455>,1:60]]}}",
				"{query0={result_tags=[[@22,71:81='result_tags',<455>,1:71], [@5,13:13='d',<455>,1:13]], id=[[@13,42:43='id',<455>,1:42], [@1,7:7='d',<455>,1:7], [@29,106:106='d',<455>,1:106]]}, query1={result_tags=[[@7,15:25='result_tags',<455>,1:15]], id=[[@3,9:10='id',<455>,1:9]]}}",
				"{def_query1={query_dictionary={result_tags=[[@7,15:25='result_tags',<455>,1:15]], id=[[@3,9:10='id',<455>,1:9]]}, def_query0={query_dictionary={result_tags=[[@22,71:81='result_tags',<455>,1:71], [@5,13:13='d',<455>,1:13]], id=[[@13,42:43='id',<455>,1:42], [@1,7:7='d',<455>,1:7], [@29,106:106='d',<455>,1:106]]}, table_dictionary={events={id=[[@11,40:40='a',<455>,1:40]], tags=[[@17,60:60='a',<455>,1:60]]}}, interface={result_tags=[{name=tags, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}, filters=[{name=id, table_ref=d}], interface={result_tags=[{name=result_tags, table_ref=d}], id=[{name=id, table_ref=d}]}, table_alias={d=query0}}}");
	}

	@Test
	public void arR19_arrayReverseSetOpSiblingIsolationV0Test() {
		final String query = "SELECT a.id, array_reverse(a.tags) AS result_tags FROM events a UNION ALL SELECT b.id, b.tags AS result_tags FROM archived_events b";
		final SqlParseEventWalker extractor = runUnaryQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_reverse' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null,
						true, "ast-walk", null, null));
		assertOutputs(
				extractor,
				"{SQL={union={1={select={1={column={name=id, table_ref=a}}, 2={function={function_name=array_reverse, parameters={1={column={name=tags, table_ref=a}}}}, alias=result_tags}}, from={table={alias=a, table=events}}}, 2={union={qualifier=ALL, operator=UNION}}, 3={select={1={column={name=id, table_ref=b}}, 2={column={name=tags, table_ref=b}, alias=result_tags}}, from={table={alias=b, table=archived_events}}}}}}",
				"[result_tags, id]",
				"{}",
				"{archived_events={id=[[@19,81:81='b',<455>,1:81]], tags=[[@23,87:87='b',<455>,1:87]]}, events={id=[[@1,7:7='a',<455>,1:7]], tags=[[@7,27:27='a',<455>,1:27]]}}",
				"{query0={result_tags=[[@12,38:48='result_tags',<455>,1:38]], id=[[@3,9:10='id',<455>,1:9]]}, query1={result_tags=[[@27,97:107='result_tags',<455>,1:97]], id=[[@21,83:84='id',<455>,1:83]]}}",
				"{def_union2={def_query1={query_dictionary={result_tags=[[@27,97:107='result_tags',<455>,1:97]], id=[[@21,83:84='id',<455>,1:83]]}, table_dictionary={archived_events={id=[[@19,81:81='b',<455>,1:81]], tags=[[@23,87:87='b',<455>,1:87]]}}, setop=UNION, interface={result_tags=[{name=tags, table_ref=b}], id=[{name=id, table_ref=b}]}, table_alias={b=archived_events}}, def_query0={query_dictionary={result_tags=[[@12,38:48='result_tags',<455>,1:38]], id=[[@3,9:10='id',<455>,1:9]]}, table_dictionary={events={id=[[@1,7:7='a',<455>,1:7]], tags=[[@7,27:27='a',<455>,1:27]]}}, interface={result_tags=[{name=tags, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}, interface={result_tags=query_column, id=query_column}}}");
	}

	@Test
	public void arC39_arrayReverseUnqualifiedOperandV0Test() {
		final String query = "SELECT id, array_reverse(tags) AS result_tags FROM events WHERE active = 1";
		final SqlParseEventWalker extractor = runUnaryQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_reverse' at (l:1 c:11) is recognized as Snowflake-specific grammar.",
						1, 11, "SqlASTWalkerHelper", null, null,
						true, "ast-walk", null, null));
		assertOutputs(
				extractor,
				"{SQL={select={1={column={name=id, table_ref=null}}, 2={function={function_name=array_reverse, parameters={1={column={name=tags, table_ref=null}}}}, alias=result_tags}}, from={table={alias=null, table=events}}, where={condition={left={column={name=active, table_ref=null}}, right={literal=1}, operator==}}}}",
				"[result_tags, id]",
				"{}",
				"{events={active=[[@12,64:69='active',<455>,1:64]], id=[[@1,7:8='id',<455>,1:7]], tags=[[@5,25:28='tags',<455>,1:25]]}}",
				"{query0={result_tags=[[@8,34:44='result_tags',<455>,1:34]], id=[[@1,7:8='id',<455>,1:7]]}}",
				"{def_query0={query_dictionary={result_tags=[[@8,34:44='result_tags',<455>,1:34]], id=[[@1,7:8='id',<455>,1:7]]}, table_dictionary={events={active=[[@12,64:69='active',<455>,1:64]], id=[[@1,7:8='id',<455>,1:7]], tags=[[@5,25:28='tags',<455>,1:25]]}}, filters=[{name=active, table_ref=events}], interface={result_tags=[{name=tags, table_ref=events}], id=[{name=id, table_ref=events}]}}}");
	}

	@Test
	public void arM20_arrayReverseUnresolvedOperandV0Test() {
		final String query = "SELECT array_reverse(missing_tags) AS result_tags";
		final SqlParseEventWalker extractor = runUnaryQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_reverse' at (l:1 c:7) is recognized as Snowflake-specific grammar.",
						1, 7, "SqlASTWalkerHelper", null, null,
						true, "ast-walk", null, null),
				new ParseDiagnostic(ParseDiagnostic.Severity.ERROR,
						"UNRESOLVED_UNQUALIFIED_COLUMNS",
						"Unresolved unqualified column reference(s): [missing_tags [(l:1 c:21)]]",
						1, 21, "SqlASTWalkerHelper", null, "missing_tags",
						false, "ast-walk", null, null));
		assertOutputs(
				extractor,
				"{SQL={select={1={function={function_name=array_reverse, parameters={1={column={name=missing_tags, table_ref=null}}}}, alias=result_tags}}}}",
				"[result_tags]",
				"{}",
				"{}",
				"{query0={result_tags=[[@6,38:48='result_tags',<455>,1:38]]}}",
				"{def_query0={query_dictionary={result_tags=[[@6,38:48='result_tags',<455>,1:38]]}, interface={result_tags=[{name=missing_tags, table_ref=null}]}}}");
	}

	@Test
	public void arM21_arrayReverseAmbiguousOperandV0Test() {
		final String query = "SELECT array_reverse(tags) AS result_tags FROM events a JOIN archived_events b ON a.id = b.id";
		final SqlParseEventWalker extractor = runUnaryQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_reverse' at (l:1 c:7) is recognized as Snowflake-specific grammar.",
						1, 7, "SqlASTWalkerHelper", null, null,
						true, "ast-walk", null, null),
				new ParseDiagnostic(ParseDiagnostic.Severity.SEVERE_WARNING,
						"AMBIGUOUS_COLUMN_REFERENCE",
						"Ambiguous column reference 'tags' at (l:1 c:21). Possible sources: [archived_events, events]",
						1, 21, "SqlASTWalkerHelper", null, "tags",
						true, "ast-walk", null, null),
				new ParseDiagnostic(ParseDiagnostic.Severity.ERROR,
						"UNRESOLVED_UNQUALIFIED_COLUMNS",
						"Unresolved unqualified column reference(s): [tags [(l:1 c:21)]]",
						1, 21, "SqlASTWalkerHelper", null, "tags",
						false, "ast-walk", null, null));
		assertOutputs(
				extractor,
				"{SQL={select={1={function={function_name=array_reverse, parameters={1={column={name=tags, table_ref=null}}}}, alias=result_tags}}, from={join={1={table={alias=a, table=events}}, 2={join=JOIN, on={condition={left={column={name=id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=archived_events}}}}}}",
				"[result_tags]",
				"{}",
				"{archived_events={id=[[@18,89:89='b',<455>,1:89]]}, events={id=[[@14,82:82='a',<455>,1:82]]}}",
				"{query0={result_tags=[[@6,30:40='result_tags',<455>,1:30]]}}",
				"{def_query0={query_dictionary={result_tags=[[@6,30:40='result_tags',<455>,1:30]]}, table_dictionary={archived_events={id=[[@18,89:89='b',<455>,1:89]]}, events={id=[[@14,82:82='a',<455>,1:82]]}}, filters=[{name=id, table_ref=a}, {name=id, table_ref=b}], interface={result_tags=[{name=tags, table_ref=null}]}, table_alias={a=events, b=archived_events}}}");
	}

	@Test
	public void arM22_arrayReverseMissingAliasOperandV0Test() {
		final String query = "SELECT a.id, array_reverse(z.tags) AS result_tags FROM events a WHERE a.active = 1";
		final SqlParseEventWalker extractor = runUnaryQuery(query);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_reverse' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null,
						true, "ast-walk", null, null),
				new ParseDiagnostic(ParseDiagnostic.Severity.FATAL,
						"QUALIFIED_COLUMN_NOT_FOUND_IN_TABLE",
						"Source Table not found for Column 'tags' at (l:1 c:38). No alias or table called 'z'.",
						1, 38, "SqlASTWalkerHelper", null, "tags",
						false, "ast-walk", null, null));
		assertOutputs(
				extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={function_name=array_reverse, parameters={1={column={name=tags, table_ref=z}}}}, alias=result_tags}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_tags, id]",
				"{}",
				"{events={active=[[@17,70:70='a',<455>,1:70]], id=[[@1,7:7='a',<455>,1:7]]}}",
				"{query0={result_tags=[[@12,38:48='result_tags',<455>,1:38]], id=[[@3,9:10='id',<455>,1:9]]}}",
				"{def_query0={query_dictionary={result_tags=[[@12,38:48='result_tags',<455>,1:38]], id=[[@3,9:10='id',<455>,1:9]]}, table_dictionary={events={active=[[@17,70:70='a',<455>,1:70]], id=[[@1,7:7='a',<455>,1:7]]}}, filters=[{name=active, table_ref=a}], interface={result_tags=[{name=tags, table_ref=z}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arM22_arrayReverseHiddenAliasOperandV0Test() {
		final String query = "SELECT d.id, array_reverse(a.tags) AS result_tags FROM (SELECT a.id, a.tags FROM events a) d WHERE d.id > 0";
		final SqlParseEventWalker extractor = runUnaryQuery(query);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_reverse' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null,
						true, "ast-walk", null, null),
				new ParseDiagnostic(ParseDiagnostic.Severity.FATAL,
						"QUALIFIED_COLUMN_NOT_FOUND_IN_TABLE",
						"Source Table not found for Column 'tags' at (l:1 c:38). No alias or table called 'a'.",
						1, 38, "SqlASTWalkerHelper", null, "tags",
						false, "ast-walk", null, null));
		assertOutputs(
				extractor,
				"{SQL={select={1={column={name=id, table_ref=d}}, 2={function={function_name=array_reverse, parameters={1={column={name=tags, table_ref=a}}}}, alias=result_tags}}, from={table={alias=d, query={select={1={column={name=id, table_ref=a}}, 2={column={name=tags, table_ref=a}}}, from={table={alias=a, table=events}}}}}, where={condition={left={column={name=id, table_ref=d}}, right={literal=0}, operator=>}}}}",
				"[result_tags, id]",
				"{}",
				"{events={id=[[@16,63:63='a',<455>,1:63]], tags=[[@20,69:69='a',<455>,1:69]]}}",
				"{query0={id=[[@18,65:66='id',<455>,1:65], [@1,7:7='d',<455>,1:7], [@29,99:99='d',<455>,1:99]], tags=[[@22,71:74='tags',<455>,1:71]]}, query1={result_tags=[[@12,38:48='result_tags',<455>,1:38]], id=[[@3,9:10='id',<455>,1:9]]}}",
				"{def_query1={query_dictionary={result_tags=[[@12,38:48='result_tags',<455>,1:38]], id=[[@3,9:10='id',<455>,1:9]]}, table_dictionary={}, def_query0={query_dictionary={id=[[@18,65:66='id',<455>,1:65], [@1,7:7='d',<455>,1:7], [@29,99:99='d',<455>,1:99]], tags=[[@22,71:74='tags',<455>,1:71]]}, table_dictionary={events={id=[[@16,63:63='a',<455>,1:63]], tags=[[@20,69:69='a',<455>,1:69]]}}, interface={id=[{name=id, table_ref=a}], tags=[{name=tags, table_ref=a}]}, table_alias={a=events}}, filters=[{name=id, table_ref=d}], interface={result_tags=[{name=tags, table_ref=a}], id=[{name=id, table_ref=d}]}, table_alias={d=query0}}}");
	}

	@Test
	public void arC39_arrayReversePredicandVariableV0Test() {
		final String query = "SELECT a.id, array_reverse(<tags_expr>) AS result_tags FROM events a WHERE a.active = 1";
		final SqlParseEventWalker extractor = runUnaryQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_reverse' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null,
						true, "ast-walk", null, null));
		assertOutputs(
				extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={function_name=array_reverse, parameters={1={substitution={name=<tags_expr>, type=predicand}}}}, alias=result_tags}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_tags, id]",
				"{<tags_expr>=predicand}",
				"{events={active=[[@15,75:75='a',<455>,1:75]], id=[[@1,7:7='a',<455>,1:7]]}}",
				"{query0={result_tags=[[@10,43:53='result_tags',<455>,1:43]], id=[[@3,9:10='id',<455>,1:9]]}}",
				"{def_query0={query_dictionary={result_tags=[[@10,43:53='result_tags',<455>,1:43]], id=[[@3,9:10='id',<455>,1:9]]}, table_dictionary={events={active=[[@15,75:75='a',<455>,1:75]], id=[[@1,7:7='a',<455>,1:7]]}}, filters=[{name=active, table_ref=a}], interface={result_tags=[{name=<tags_expr>, type=predicand}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC39_arrayReverseQualifiedColumnVariableV0Test() {
		final String query = "SELECT a.id, array_reverse(a.<tags_col>) AS result_tags FROM events a WHERE a.active = 1";
		final SqlParseEventWalker extractor = runUnaryQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_reverse' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null,
						true, "ast-walk", null, null));
		assertOutputs(
				extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={function_name=array_reverse, parameters={1={column={substitution={name=<tags_col>, type=column}, table_ref=a}}}}, alias=result_tags}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_tags, id]",
				"{<tags_col>=column}",
				"{events={active=[[@17,76:76='a',<455>,1:76]], id=[[@1,7:7='a',<455>,1:7]], <tags_col>=[[@7,27:27='a',<455>,1:27]]}}",
				"{query0={result_tags=[[@12,44:54='result_tags',<455>,1:44]], id=[[@3,9:10='id',<455>,1:9]]}}",
				"{def_query0={query_dictionary={result_tags=[[@12,44:54='result_tags',<455>,1:44]], id=[[@3,9:10='id',<455>,1:9]]}, table_dictionary={events={active=[[@17,76:76='a',<455>,1:76]], id=[[@1,7:7='a',<455>,1:7]], <tags_col>=[[@7,27:27='a',<455>,1:27]]}}, filters=[{name=active, table_ref=a}], interface={result_tags=[{substitution={name=<tags_col>, type=column}, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arM23_arrayReverseSchemaQualifiedRoutineControlV0Test() {
		final String query = "SELECT a.id, custom.array_reverse(a.tags) AS result_tags FROM events a WHERE a.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(
				extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={schema=custom, parameters={1={column={name=tags, table_ref=a}}}, function_name=array_reverse}, alias=result_tags}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_tags, id]",
				"{}",
				"{events={active=[[@19,77:77='a',<455>,1:77]], id=[[@1,7:7='a',<455>,1:7]], tags=[[@9,34:34='a',<455>,1:34]]}}",
				"{query0={result_tags=[[@14,45:55='result_tags',<455>,1:45]], id=[[@3,9:10='id',<455>,1:9]]}}",
				"{def_query0={query_dictionary={result_tags=[[@14,45:55='result_tags',<455>,1:45]], id=[[@3,9:10='id',<455>,1:9]]}, table_dictionary={events={active=[[@19,77:77='a',<455>,1:77]], id=[[@1,7:7='a',<455>,1:7]], tags=[[@9,34:34='a',<455>,1:34]]}}, filters=[{name=active, table_ref=a}], interface={result_tags=[{name=tags, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arM24_arrayReverseMixedDialectControlV0Test() {
		final String query = "SELECT array_reverse(a.tags) AS result_tags, extract(isodow from a.created_at) AS weekday FROM events a";
		final SqlParseEventWalker extractor = runUnaryQuery(query);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_reverse' at (l:1 c:7) is recognized as Snowflake-specific grammar.",
						1, 7, "SqlASTWalkerHelper", null, null,
						true, "ast-walk", null, null),
				new ParseDiagnostic(ParseDiagnostic.Severity.FATAL,
						"STATEMENT_MIXED_DIALECT_GRAMMAR",
						"Statement mixes Snowflake-only and PostgreSQL-only grammar at (l:1 c:45); unlikely to run on either engine.",
						1, 45, "SqlASTWalkerHelper", null, null,
						false, "ast-walk", null, null));
		assertOutputs(
				extractor,
				"{SQL={select={1={function={function_name=array_reverse, parameters={1={column={name=tags, table_ref=a}}}}, alias=result_tags}, 2={alias=weekday, datetime_function={function_name=extract, part_form=KEYWORD, part=isodow, source={column={name=created_at, table_ref=a}}}}}, from={table={alias=a, table=events}}}}",
				"[result_tags, weekday]",
				"{}",
				"{events={created_at=[[@14,65:65='a',<455>,1:65]], tags=[[@3,21:21='a',<455>,1:21]]}}",
				"{query0={weekday=[[@19,82:88='weekday',<455>,1:82]], result_tags=[[@8,32:42='result_tags',<455>,1:32]]}}",
				"{def_query0={query_dictionary={result_tags=[[@8,32:42='result_tags',<455>,1:32]], weekday=[[@19,82:88='weekday',<455>,1:82]]}, table_dictionary={events={created_at=[[@14,65:65='a',<455>,1:65]], tags=[[@3,21:21='a',<455>,1:21]]}}, interface={result_tags=[{name=tags, table_ref=a}], weekday=[{name=created_at, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC39_arrayReverseUppercaseNameV0Test() {
		final String query = "SELECT a.id, ARRAY_REVERSE(a.tags) AS result_tags FROM events a WHERE a.active = 1";
		final SqlParseEventWalker extractor = runUnaryQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_reverse' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null,
						true, "ast-walk", null, null));
		assertOutputs(
				extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={function_name=ARRAY_REVERSE, parameters={1={column={name=tags, table_ref=a}}}}, alias=result_tags}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_tags, id]",
				"{}",
				"{events={active=[[@17,70:70='a',<455>,1:70]], id=[[@1,7:7='a',<455>,1:7]], tags=[[@7,27:27='a',<455>,1:27]]}}",
				"{query0={result_tags=[[@12,38:48='result_tags',<455>,1:38]], id=[[@3,9:10='id',<455>,1:9]]}}",
				"{def_query0={query_dictionary={result_tags=[[@12,38:48='result_tags',<455>,1:38]], id=[[@3,9:10='id',<455>,1:9]]}, table_dictionary={events={active=[[@17,70:70='a',<455>,1:70]], id=[[@1,7:7='a',<455>,1:7]], tags=[[@7,27:27='a',<455>,1:27]]}}, filters=[{name=active, table_ref=a}], interface={result_tags=[{name=tags, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC39_arrayReverseNullOperandV0Test() {
		final String query = "SELECT a.id, array_reverse(NULL) AS result_tags FROM events a WHERE a.active = 1";
		final SqlParseEventWalker extractor = runUnaryQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_reverse' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null,
						true, "ast-walk", null, null));
		assertOutputs(
				extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={function_name=array_reverse, parameters={1={null_literal=null}}}, alias=result_tags}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_tags, id]",
				"{}",
				"{events={active=[[@15,68:68='a',<455>,1:68]], id=[[@1,7:7='a',<455>,1:7]]}}",
				"{query0={result_tags=[[@10,36:46='result_tags',<455>,1:36]], id=[[@3,9:10='id',<455>,1:9]]}}",
				"{def_query0={query_dictionary={result_tags=[[@10,36:46='result_tags',<455>,1:36]], id=[[@3,9:10='id',<455>,1:9]]}, table_dictionary={events={active=[[@15,68:68='a',<455>,1:68]], id=[[@1,7:7='a',<455>,1:7]]}}, filters=[{name=active, table_ref=a}], interface={result_tags=[], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arM23_arrayReverseNonreservedColumnNameV0Test() {
		final String query = "SELECT a.array_reverse AS result_tags FROM events a WHERE a.id > 0";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(
				extractor,
				"{SQL={select={1={column={name=array_reverse, table_ref=a}, alias=result_tags}}, from={table={alias=a, table=events}}, where={condition={left={column={name=id, table_ref=a}}, right={literal=0}, operator=>}}}}",
				"[result_tags]",
				"{}",
				"{events={id=[[@10,58:58='a',<455>,1:58]], array_reverse=[[@1,7:7='a',<455>,1:7]]}}",
				"{query0={result_tags=[[@5,26:36='result_tags',<455>,1:26]]}}",
				"{def_query0={query_dictionary={result_tags=[[@5,26:36='result_tags',<455>,1:26]]}, table_dictionary={events={id=[[@10,58:58='a',<455>,1:58]], array_reverse=[[@1,7:7='a',<455>,1:7]]}}, filters=[{name=id, table_ref=a}], interface={result_tags=[{name=array_reverse, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC40_arraySumSelectPlacementV0Test() {
		final String query = "SELECT a.id, array_sum(a.tags) AS result_tags FROM events a WHERE a.active = 1";
		final SqlParseEventWalker extractor = runUnaryQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_sum' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null,
						true, "ast-walk", null, null));
		assertOutputs(
				extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={function_name=array_sum, parameters={1={column={name=tags, table_ref=a}}}}, alias=result_tags}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_tags, id]",
				"{}",
				"{events={active=[[@17,66:66='a',<455>,1:66]], id=[[@1,7:7='a',<455>,1:7]], tags=[[@7,23:23='a',<455>,1:23]]}}",
				"{query0={result_tags=[[@12,34:44='result_tags',<455>,1:34]], id=[[@3,9:10='id',<455>,1:9]]}}",
				"{def_query0={query_dictionary={result_tags=[[@12,34:44='result_tags',<455>,1:34]], id=[[@3,9:10='id',<455>,1:9]]}, table_dictionary={events={active=[[@17,66:66='a',<455>,1:66]], id=[[@1,7:7='a',<455>,1:7]], tags=[[@7,23:23='a',<455>,1:23]]}}, filters=[{name=active, table_ref=a}], interface={result_tags=[{name=tags, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC40_arraySumWherePlacementV0Test() {
		final String query = "SELECT a.id FROM events a WHERE array_sum(a.tags) IS NOT NULL AND a.active = 1";
		final SqlParseEventWalker extractor = runUnaryQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_sum' at (l:1 c:32) is recognized as Snowflake-specific grammar.",
						1, 32, "SqlASTWalkerHelper", null, null,
						true, "ast-walk", null, null));
		assertOutputs(
				extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}}, from={table={alias=a, table=events}}, where={and={1={condition={left={function={function_name=array_sum, parameters={1={column={name=tags, table_ref=a}}}}}, operator=IS NOT NULL}}, 2={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}}}",
				"[id]",
				"{}",
				"{events={active=[[@18,66:66='a',<455>,1:66]], id=[[@1,7:7='a',<455>,1:7]], tags=[[@10,42:42='a',<455>,1:42]]}}",
				"{query0={id=[[@3,9:10='id',<455>,1:9]]}}",
				"{def_query0={query_dictionary={id=[[@3,9:10='id',<455>,1:9]]}, table_dictionary={events={active=[[@18,66:66='a',<455>,1:66]], id=[[@1,7:7='a',<455>,1:7]], tags=[[@10,42:42='a',<455>,1:42]]}}, filters=[{name=tags, table_ref=a}, {name=active, table_ref=a}], interface={id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC40_arraySumJoinOnPlacementV0Test() {
		final String query = "SELECT a.id, b.id AS other_id FROM events a JOIN archived_events b ON array_sum(a.tags) = b.tags AND a.id = b.id";
		final SqlParseEventWalker extractor = runUnaryQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_sum' at (l:1 c:70) is recognized as Snowflake-specific grammar.",
						1, 70, "SqlASTWalkerHelper", null, null,
						true, "ast-walk", null, null));
		assertOutputs(
				extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={column={name=id, table_ref=b}, alias=other_id}}, from={join={1={table={alias=a, table=events}}, 2={join=JOIN, on={and={1={condition={left={function={function_name=array_sum, parameters={1={column={name=tags, table_ref=a}}}}}, right={column={name=tags, table_ref=b}}, operator==}}, 2={condition={left={column={name=id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}}}, 3={table={alias=b, table=archived_events}}}}}}",
				"[id, other_id]",
				"{}",
				"{archived_events={id=[[@5,13:13='b',<455>,1:13], [@32,108:108='b',<455>,1:108]], tags=[[@24,90:90='b',<455>,1:90]]}, events={id=[[@1,7:7='a',<455>,1:7], [@28,101:101='a',<455>,1:101]], tags=[[@19,80:80='a',<455>,1:80]]}}",
				"{query0={id=[[@3,9:10='id',<455>,1:9]], other_id=[[@9,21:28='other_id',<455>,1:21]]}}",
				"{def_query0={query_dictionary={id=[[@3,9:10='id',<455>,1:9]], other_id=[[@9,21:28='other_id',<455>,1:21]]}, table_dictionary={archived_events={id=[[@5,13:13='b',<455>,1:13], [@32,108:108='b',<455>,1:108]], tags=[[@24,90:90='b',<455>,1:90]]}, events={id=[[@1,7:7='a',<455>,1:7], [@28,101:101='a',<455>,1:101]], tags=[[@19,80:80='a',<455>,1:80]]}}, filters=[{name=tags, table_ref=a}, {name=tags, table_ref=b}, {name=id, table_ref=a}, {name=id, table_ref=b}], interface={id=[{name=id, table_ref=a}], other_id=[{name=id, table_ref=b}]}, table_alias={a=events, b=archived_events}}}");
	}

	@Test
	public void arC40_arraySumHavingPlacementV0Test() {
		final String query = "SELECT a.group_id, count(*) AS n FROM events a GROUP BY a.group_id HAVING array_sum(array_agg(a.tags)) IS NOT NULL AND count(*) > 1";
		final SqlParseEventWalker extractor = runUnaryQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_sum' at (l:1 c:74) is recognized as Snowflake-specific grammar.",
						1, 74, "SqlASTWalkerHelper", null, null,
						true, "ast-walk", null, null));
		assertOutputs(
				extractor,
				"{SQL={select={1={column={name=group_id, table_ref=a}}, 2={function={function_name=COUNT, qualifier=null, parameters=*}, alias=n}}, having={and={1={condition={left={function={function_name=array_sum, parameters={1={function={function_name=array_agg, qualifier=null, parameters={column={name=tags, table_ref=a}}}}}}}, operator=IS NOT NULL}}, 2={condition={left={function={function_name=COUNT, qualifier=null, parameters=*}}, right={literal=1}, operator=>}}}}, from={table={alias=a, table=events}}, groupby={1={column={name=group_id, table_ref=a}}}}}",
				"[group_id, n]",
				"{}",
				"{events={group_id=[[@1,7:7='a',<455>,1:7], [@16,56:56='a',<455>,1:56]], *=[[@7,25:25='*',<291>,1:25], [@35,125:125='*',<291>,1:125]], tags=[[@24,94:94='a',<455>,1:94]]}}",
				"{query0={group_id=[[@3,9:16='group_id',<455>,1:9]], n=[[@10,31:31='n',<455>,1:31]]}}",
				"{def_query0={query_dictionary={group_id=[[@3,9:16='group_id',<455>,1:9]], n=[[@10,31:31='n',<455>,1:31]]}, table_dictionary={events={group_id=[[@1,7:7='a',<455>,1:7], [@16,56:56='a',<455>,1:56]], *=[[@7,25:25='*',<291>,1:25], [@35,125:125='*',<291>,1:125]], tags=[[@24,94:94='a',<455>,1:94]]}}, grouped_by=[{name=group_id, table_ref=a}], filters=[{name=tags, table_ref=a}], interface={group_id=[{name=group_id, table_ref=a}], n=[]}, table_alias={a=events}}}");
	}

	@Test
	public void arC40_arraySumQualifyPlacementV0Test() {
		final String query = "SELECT a.id FROM events a QUALIFY array_sum(a.tags) IS NOT NULL AND a.id > 0";
		final SqlParseEventWalker extractor = runUnaryQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_sum' at (l:1 c:34) is recognized as Snowflake-specific grammar.",
						1, 34, "SqlASTWalkerHelper", null, null,
						true, "ast-walk", null, null));
		assertOutputs(
				extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}}, from={table={alias=a, table=events}}, qualify={and={1={condition={left={function={function_name=array_sum, parameters={1={column={name=tags, table_ref=a}}}}}, operator=IS NOT NULL}}, 2={condition={left={column={name=id, table_ref=a}}, right={literal=0}, operator=>}}}}}}",
				"[id]",
				"{}",
				"{events={id=[[@1,7:7='a',<455>,1:7], [@18,68:68='a',<455>,1:68]], tags=[[@10,44:44='a',<455>,1:44]]}}",
				"{query0={id=[[@3,9:10='id',<455>,1:9]]}}",
				"{def_query0={query_dictionary={id=[[@3,9:10='id',<455>,1:9]]}, table_dictionary={events={id=[[@1,7:7='a',<455>,1:7], [@18,68:68='a',<455>,1:68]], tags=[[@10,44:44='a',<455>,1:44]]}}, filters=[{name=tags, table_ref=a}, {name=id, table_ref=a}], interface={id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC40_arraySumOrderByPlacementV0Test() {
		final String query = "SELECT a.id FROM events a WHERE a.active = 1 ORDER BY array_sum(a.tags), a.id";
		final SqlParseEventWalker extractor = runUnaryQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_sum' at (l:1 c:54) is recognized as Snowflake-specific grammar.",
						1, 54, "SqlASTWalkerHelper", null, null,
						true, "ast-walk", null, null));
		assertOutputs(
				extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}}, orderby={1={null_order=null, predicand={function={function_name=array_sum, parameters={1={column={name=tags, table_ref=a}}}}}, sort_order=ASC}, 2={null_order=null, predicand={column={name=id, table_ref=a}}, sort_order=ASC}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[id]",
				"{}",
				"{events={active=[[@8,32:32='a',<455>,1:32]], id=[[@1,7:7='a',<455>,1:7], [@22,73:73='a',<455>,1:73]], tags=[[@17,64:64='a',<455>,1:64]]}}",
				"{query0={id=[[@3,9:10='id',<455>,1:9]]}}",
				"{def_query0={query_dictionary={id=[[@3,9:10='id',<455>,1:9]]}, table_dictionary={events={active=[[@8,32:32='a',<455>,1:32]], id=[[@1,7:7='a',<455>,1:7], [@22,73:73='a',<455>,1:73]], tags=[[@17,64:64='a',<455>,1:64]]}}, ordered_by=[{name=tags, table_ref=a}, {name=id, table_ref=a}], filters=[{name=active, table_ref=a}], interface={id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC40_arraySumValuesPlacementV0Test() {
		final String query = "VALUES (array_sum(<tags_expr>), 7)";
		final SqlParseEventWalker extractor = runUnaryQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_sum' at (l:1 c:8) is recognized as Snowflake-specific grammar.",
						1, 8, "SqlASTWalkerHelper", null, null,
						true, "ast-walk", null, null));
		assertOutputs(
				extractor,
				"{SQL={values={matrix={1={row={1={function={function_name=array_sum, parameters={1={substitution={name=<tags_expr>, type=predicand}}}}}, 2={literal=7}}}}}}}",
				"[$1, $2]",
				"{<tags_expr>=predicand}",
				"{}",
				"{values0={$1=[[@1,7:7='(',<287>,1:7]], $2=[[@1,7:7='(',<287>,1:7]]}}",
				"{def_values0={query_dictionary={$1=[[@1,7:7='(',<287>,1:7]], $2=[[@1,7:7='(',<287>,1:7]]}, interface={$1=[], $2=[]}}}");
	}

	@Test
	public void arC40_arraySumInsertPlacementV0Test() {
		final String query = "INSERT INTO archived_events (tags, id) SELECT array_sum(a.tags), a.id FROM events a WHERE a.active = 1";
		final SqlParseEventWalker extractor = runUnaryQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_sum' at (l:1 c:46) is recognized as Snowflake-specific grammar.",
						1, 46, "SqlASTWalkerHelper", null, null,
						true, "ast-walk", null, null));
		assertOutputs(
				extractor,
				"{SQL={insert={preamble=insert_into, from={from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}, select={1={function={function_name=array_sum, parameters={1={column={name=tags, table_ref=a}}}}}, 2={column={name=id, table_ref=a}}}}, target_table={table={alias=null, table=archived_events}}, columns={1={column={name=tags, table_ref=null}}, 2={column={name=id, table_ref=null}}}}}}",
				"[id, tags]",
				"{}",
				"{archived_events={id=[[@6,35:36='id',<455>,1:35]], tags=[[@4,29:32='tags',<455>,1:29]]}, events={active=[[@23,90:90='a',<455>,1:90]], id=[[@16,65:65='a',<455>,1:65]], tags=[[@11,56:56='a',<455>,1:56]]}}",
				"{query0={unnamed_0=[[@14,62:62=')',<288>,1:62]], id=[[@18,67:68='id',<455>,1:67]]}, insert1={id=[[@6,35:36='id',<455>,1:35]], tags=[[@4,29:32='tags',<455>,1:29]]}}",
				"{def_insert1={query_dictionary={id=[[@6,35:36='id',<455>,1:35]], tags=[[@4,29:32='tags',<455>,1:29]]}, table_dictionary={archived_events={id=[[@6,35:36='id',<455>,1:35]], tags=[[@4,29:32='tags',<455>,1:29]]}}, def_query0={query_dictionary={unnamed_0=[[@14,62:62=')',<288>,1:62]], id=[[@18,67:68='id',<455>,1:67]]}, table_dictionary={events={active=[[@23,90:90='a',<455>,1:90]], id=[[@16,65:65='a',<455>,1:65]], tags=[[@11,56:56='a',<455>,1:56]]}}, filters=[{name=active, table_ref=a}], interface={unnamed_0=[{name=tags, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}, interface={tags=[{name=unnamed_0, table_ref=query0}], id=[{name=id, table_ref=query0}]}}}");
	}

	@Test
	public void arC40_arraySumUpdatePlacementV0Test() {
		final String query = "UPDATE events SET tags = array_sum(tags), active = 1 WHERE id > 0";
		final SqlParseEventWalker extractor = runUnaryQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_sum' at (l:1 c:25) is recognized as Snowflake-specific grammar.",
						1, 25, "SqlASTWalkerHelper", null, null,
						true, "ast-walk", null, null));
		assertOutputs(
				extractor,
				"{SQL={update={table={alias=null, table=events}, where={condition={left={column={name=id, table_ref=null}}, right={literal=0}, operator=>}}, assignments={1={set={column={name=tags, table_ref=null}}, to={function={function_name=array_sum, parameters={1={column={name=tags, table_ref=null}}}}}}, 2={set={column={name=active, table_ref=null}}, to={literal=1}}}}}}",
				"[active, tags]",
				"{}",
				"{events={id=[[@14,59:60='id',<455>,1:59]], tags=[[@7,35:38='tags',<455>,1:35]]}}",
				"{update0={active=[[@10,42:47='active',<455>,1:42]], tags=[[@3,18:21='tags',<455>,1:18]]}}",
				"{def_update0={assignments={active=[], tags=[{name=tags, table_ref=events}]}, table_dictionary={events={id=[[@14,59:60='id',<455>,1:59]], tags=[[@7,35:38='tags',<455>,1:35]]}}, update_dictionary={active=[[@10,42:47='active',<455>,1:42]], tags=[[@3,18:21='tags',<455>,1:18]]}, target_table={events={active=[[@10,42:47='active',<455>,1:42]], tags=[[@3,18:21='tags',<455>,1:18], [@7,35:38='tags',<455>,1:35], [@8,39:39=')',<288>,1:39]]}}, filters=[{name=id, table_ref=events}], lhs_unresolved_columns={active={column={name=active, table_ref=null}, locations=[[@10,42:47='active',<455>,1:42]]}, tags={column={name=tags, table_ref=null}, locations=[[@3,18:21='tags',<455>,1:18], [@7,35:38='tags',<455>,1:35], [@8,39:39=')',<288>,1:39]]}}}}");
	}

	@Test
	public void arC40_arraySumDeletePlacementV0Test() {
		final String query = "DELETE FROM events WHERE array_sum(tags) IS NULL AND id > 0";
		final SqlParseEventWalker extractor = runUnaryQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_sum' at (l:1 c:25) is recognized as Snowflake-specific grammar.",
						1, 25, "SqlASTWalkerHelper", null, null,
						true, "ast-walk", null, null));
		assertOutputs(
				extractor,
				"{SQL={delete={table={alias=null, table=events}, where={and={1={condition={left={function={function_name=array_sum, parameters={1={column={name=tags, table_ref=null}}}}}, operator=IS NULL}}, 2={condition={left={column={name=id, table_ref=null}}, right={literal=0}, operator=>}}}}}}}",
				"[]",
				"{}",
				"{events={id=[[@11,53:54='id',<455>,1:53]], tags=[[@6,35:38='tags',<455>,1:35]]}}",
				"{}",
				"{def_delete0={query_dictionary={}, table_dictionary={events={id=[[@11,53:54='id',<455>,1:53]], tags=[[@6,35:38='tags',<455>,1:35]]}}, filters=[{name=tags, table_ref=events}, {name=id, table_ref=events}], interface=null}}");
	}

	@Test
	public void arC40_arraySumDdlPlacementV0Test() {
		final String query = "CREATE VIEW v_arr AS SELECT a.id, array_sum(a.tags) AS result_tags FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SqlParseEventWalker extractor = runDdlParsertest(query, parser);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_sum' at (l:1 c:34) is recognized as Snowflake-specific grammar.",
						1, 34, "SqlASTWalkerHelper", null, null,
						true, "ast-walk", null, null));
		assertOutputs(
				extractor,
				"{DDL={create={type=VIEW, name={table=v_arr}, query={select={1={column={name=id, table_ref=a}}, 2={function={function_name=array_sum, parameters={1={column={name=tags, table_ref=a}}}}, alias=result_tags}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}}}",
				"[]",
				"{}",
				"{events={active=[[@21,87:87='a',<455>,1:87]], id=[[@5,28:28='a',<455>,1:28]], tags=[[@11,44:44='a',<455>,1:44]]}}",
				"{query0={result_tags=[[@16,55:65='result_tags',<455>,1:55]], id=[[@7,30:31='id',<455>,1:30]]}}",
				"{def_create1={def_query0={query_dictionary={result_tags=[[@16,55:65='result_tags',<455>,1:55]], id=[[@7,30:31='id',<455>,1:30]]}, table_dictionary={events={active=[[@21,87:87='a',<455>,1:87]], id=[[@5,28:28='a',<455>,1:28]], tags=[[@11,44:44='a',<455>,1:44]]}}, filters=[{name=active, table_ref=a}], interface={result_tags=[{name=tags, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}}");
	}

	@Test
	public void arR20_arraySumScriptStatementResetV0Test() {
		final String query = "SELECT a.id, array_sum(a.tags) AS result_tags FROM events a;\nSELECT b.id FROM archived_events b;\nSELECT c.id, array_sum(c.tags) AS result_tags FROM events c;";
		final SQLSelectParserParser parser = parse(query);
		final SqlParseEventWalker extractor = runScriptParsertest(query, parser);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_sum' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null,
						true, "ast-walk", null, null),
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_sum' at (l:3 c:13) is recognized as Snowflake-specific grammar.",
						3, 13, "SqlASTWalkerHelper", null, null,
						true, "ast-walk", null, null));
		assertOutputs(
				extractor,
				"{SCRIPT={1={select={1={column={name=id, table_ref=a}}, 2={function={function_name=array_sum, parameters={1={column={name=tags, table_ref=a}}}}, alias=result_tags}}, from={table={alias=a, table=events}}}, 2={select={1={column={name=id, table_ref=b}}}, from={table={alias=b, table=archived_events}}}, 3={select={1={column={name=id, table_ref=c}}, 2={function={function_name=array_sum, parameters={1={column={name=tags, table_ref=c}}}}, alias=result_tags}}, from={table={alias=c, table=events}}}}}",
				"[]",
				"{SCRIPT={1={}, 2={}, 3={}}}",
				"{SCRIPT={1={events={id=[[@1,7:7='a',<455>,1:7]], tags=[[@7,23:23='a',<455>,1:23]]}}, 2={archived_events={id=[[@18,68:68='b',<455>,2:7]]}}, 3={events={id=[[@26,104:104='c',<455>,3:7]], tags=[[@32,120:120='c',<455>,3:23]]}}}}",
				"{SCRIPT={1={query0={result_tags=[[@12,34:44='result_tags',<455>,1:34]], id=[[@3,9:10='id',<455>,1:9]]}}, 2={query0={id=[[@20,70:71='id',<455>,2:9]]}}, 3={query0={result_tags=[[@37,131:141='result_tags',<455>,3:34]], id=[[@28,106:107='id',<455>,3:9]]}}}}",
				"{SCRIPT={1={def_query0={query_dictionary={result_tags=[[@12,34:44='result_tags',<455>,1:34]], id=[[@3,9:10='id',<455>,1:9]]}, table_dictionary={events={id=[[@1,7:7='a',<455>,1:7]], tags=[[@7,23:23='a',<455>,1:23]]}}, interface={result_tags=[{name=tags, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}, 2={def_query0={query_dictionary={id=[[@20,70:71='id',<455>,2:9]]}, table_dictionary={archived_events={id=[[@18,68:68='b',<455>,2:7]]}}, interface={id=[{name=id, table_ref=b}]}, table_alias={b=archived_events}}}, 3={def_query0={query_dictionary={result_tags=[[@37,131:141='result_tags',<455>,3:34]], id=[[@28,106:107='id',<455>,3:9]]}, table_dictionary={events={id=[[@26,104:104='c',<455>,3:7]], tags=[[@32,120:120='c',<455>,3:23]]}}, interface={result_tags=[{name=tags, table_ref=c}], id=[{name=id, table_ref=c}]}, table_alias={c=events}}}}}");
	}

	@Test
	public void arR18_arraySumNestedExpressionV0Test() {
		final String query = "SELECT a.id, coalesce(array_sum(a.tags), a.backup_tags) AS result_tags FROM events a WHERE a.active = 1";
		final SqlParseEventWalker extractor = runUnaryQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_sum' at (l:1 c:22) is recognized as Snowflake-specific grammar.",
						1, 22, "SqlASTWalkerHelper", null, null,
						true, "ast-walk", null, null));
		assertOutputs(
				extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={parameters={1={function={function_name=array_sum, parameters={1={column={name=tags, table_ref=a}}}}}, 2={column={name=backup_tags, table_ref=a}}}, function_name=coalesce}, alias=result_tags}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_tags, id]",
				"{}",
				"{events={active=[[@24,91:91='a',<455>,1:91]], id=[[@1,7:7='a',<455>,1:7]], backup_tags=[[@14,41:41='a',<455>,1:41]], tags=[[@9,32:32='a',<455>,1:32]]}}",
				"{query0={result_tags=[[@19,59:69='result_tags',<455>,1:59]], id=[[@3,9:10='id',<455>,1:9]]}}",
				"{def_query0={query_dictionary={result_tags=[[@19,59:69='result_tags',<455>,1:59]], id=[[@3,9:10='id',<455>,1:9]]}, table_dictionary={events={active=[[@24,91:91='a',<455>,1:91]], id=[[@1,7:7='a',<455>,1:7]], backup_tags=[[@14,41:41='a',<455>,1:41]], tags=[[@9,32:32='a',<455>,1:32]]}}, filters=[{name=active, table_ref=a}], interface={result_tags=[{name=tags, table_ref=a}, {name=backup_tags, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arR18_arraySumDerivedOutputBoundaryV0Test() {
		final String query = "SELECT d.id, d.result_tags FROM (SELECT a.id, array_sum(a.tags) AS result_tags FROM events a) d WHERE d.id > 0";
		final SqlParseEventWalker extractor = runUnaryQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_sum' at (l:1 c:46) is recognized as Snowflake-specific grammar.",
						1, 46, "SqlASTWalkerHelper", null, null,
						true, "ast-walk", null, null));
		assertOutputs(
				extractor,
				"{SQL={select={1={column={name=id, table_ref=d}}, 2={column={name=result_tags, table_ref=d}}}, from={table={alias=d, query={select={1={column={name=id, table_ref=a}}, 2={function={function_name=array_sum, parameters={1={column={name=tags, table_ref=a}}}}, alias=result_tags}}, from={table={alias=a, table=events}}}}}, where={condition={left={column={name=id, table_ref=d}}, right={literal=0}, operator=>}}}}",
				"[result_tags, id]",
				"{}",
				"{events={id=[[@11,40:40='a',<455>,1:40]], tags=[[@17,56:56='a',<455>,1:56]]}}",
				"{query0={result_tags=[[@22,67:77='result_tags',<455>,1:67], [@5,13:13='d',<455>,1:13]], id=[[@13,42:43='id',<455>,1:42], [@1,7:7='d',<455>,1:7], [@29,102:102='d',<455>,1:102]]}, query1={result_tags=[[@7,15:25='result_tags',<455>,1:15]], id=[[@3,9:10='id',<455>,1:9]]}}",
				"{def_query1={query_dictionary={result_tags=[[@7,15:25='result_tags',<455>,1:15]], id=[[@3,9:10='id',<455>,1:9]]}, def_query0={query_dictionary={result_tags=[[@22,67:77='result_tags',<455>,1:67], [@5,13:13='d',<455>,1:13]], id=[[@13,42:43='id',<455>,1:42], [@1,7:7='d',<455>,1:7], [@29,102:102='d',<455>,1:102]]}, table_dictionary={events={id=[[@11,40:40='a',<455>,1:40]], tags=[[@17,56:56='a',<455>,1:56]]}}, interface={result_tags=[{name=tags, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}, filters=[{name=id, table_ref=d}], interface={result_tags=[{name=result_tags, table_ref=d}], id=[{name=id, table_ref=d}]}, table_alias={d=query0}}}");
	}

	@Test
	public void arR19_arraySumSetOpSiblingIsolationV0Test() {
		final String query = "SELECT a.id, array_sum(a.tags) AS result_tags FROM events a UNION ALL SELECT b.id, b.tags AS result_tags FROM archived_events b";
		final SqlParseEventWalker extractor = runUnaryQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_sum' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null,
						true, "ast-walk", null, null));
		assertOutputs(
				extractor,
				"{SQL={union={1={select={1={column={name=id, table_ref=a}}, 2={function={function_name=array_sum, parameters={1={column={name=tags, table_ref=a}}}}, alias=result_tags}}, from={table={alias=a, table=events}}}, 2={union={qualifier=ALL, operator=UNION}}, 3={select={1={column={name=id, table_ref=b}}, 2={column={name=tags, table_ref=b}, alias=result_tags}}, from={table={alias=b, table=archived_events}}}}}}",
				"[result_tags, id]",
				"{}",
				"{archived_events={id=[[@19,77:77='b',<455>,1:77]], tags=[[@23,83:83='b',<455>,1:83]]}, events={id=[[@1,7:7='a',<455>,1:7]], tags=[[@7,23:23='a',<455>,1:23]]}}",
				"{query0={result_tags=[[@12,34:44='result_tags',<455>,1:34]], id=[[@3,9:10='id',<455>,1:9]]}, query1={result_tags=[[@27,93:103='result_tags',<455>,1:93]], id=[[@21,79:80='id',<455>,1:79]]}}",
				"{def_union2={def_query1={query_dictionary={result_tags=[[@27,93:103='result_tags',<455>,1:93]], id=[[@21,79:80='id',<455>,1:79]]}, table_dictionary={archived_events={id=[[@19,77:77='b',<455>,1:77]], tags=[[@23,83:83='b',<455>,1:83]]}}, setop=UNION, interface={result_tags=[{name=tags, table_ref=b}], id=[{name=id, table_ref=b}]}, table_alias={b=archived_events}}, def_query0={query_dictionary={result_tags=[[@12,34:44='result_tags',<455>,1:34]], id=[[@3,9:10='id',<455>,1:9]]}, table_dictionary={events={id=[[@1,7:7='a',<455>,1:7]], tags=[[@7,23:23='a',<455>,1:23]]}}, interface={result_tags=[{name=tags, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}, interface={result_tags=query_column, id=query_column}}}");
	}

	@Test
	public void arC40_arraySumUnqualifiedOperandV0Test() {
		final String query = "SELECT id, array_sum(tags) AS result_tags FROM events WHERE active = 1";
		final SqlParseEventWalker extractor = runUnaryQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_sum' at (l:1 c:11) is recognized as Snowflake-specific grammar.",
						1, 11, "SqlASTWalkerHelper", null, null,
						true, "ast-walk", null, null));
		assertOutputs(
				extractor,
				"{SQL={select={1={column={name=id, table_ref=null}}, 2={function={function_name=array_sum, parameters={1={column={name=tags, table_ref=null}}}}, alias=result_tags}}, from={table={alias=null, table=events}}, where={condition={left={column={name=active, table_ref=null}}, right={literal=1}, operator==}}}}",
				"[result_tags, id]",
				"{}",
				"{events={active=[[@12,60:65='active',<455>,1:60]], id=[[@1,7:8='id',<455>,1:7]], tags=[[@5,21:24='tags',<455>,1:21]]}}",
				"{query0={result_tags=[[@8,30:40='result_tags',<455>,1:30]], id=[[@1,7:8='id',<455>,1:7]]}}",
				"{def_query0={query_dictionary={result_tags=[[@8,30:40='result_tags',<455>,1:30]], id=[[@1,7:8='id',<455>,1:7]]}, table_dictionary={events={active=[[@12,60:65='active',<455>,1:60]], id=[[@1,7:8='id',<455>,1:7]], tags=[[@5,21:24='tags',<455>,1:21]]}}, filters=[{name=active, table_ref=events}], interface={result_tags=[{name=tags, table_ref=events}], id=[{name=id, table_ref=events}]}}}");
	}

	@Test
	public void arM20_arraySumUnresolvedOperandV0Test() {
		final String query = "SELECT array_sum(missing_tags) AS result_tags";
		final SqlParseEventWalker extractor = runUnaryQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_sum' at (l:1 c:7) is recognized as Snowflake-specific grammar.",
						1, 7, "SqlASTWalkerHelper", null, null,
						true, "ast-walk", null, null),
				new ParseDiagnostic(ParseDiagnostic.Severity.ERROR,
						"UNRESOLVED_UNQUALIFIED_COLUMNS",
						"Unresolved unqualified column reference(s): [missing_tags [(l:1 c:17)]]",
						1, 17, "SqlASTWalkerHelper", null, "missing_tags",
						false, "ast-walk", null, null));
		assertOutputs(
				extractor,
				"{SQL={select={1={function={function_name=array_sum, parameters={1={column={name=missing_tags, table_ref=null}}}}, alias=result_tags}}}}",
				"[result_tags]",
				"{}",
				"{}",
				"{query0={result_tags=[[@6,34:44='result_tags',<455>,1:34]]}}",
				"{def_query0={query_dictionary={result_tags=[[@6,34:44='result_tags',<455>,1:34]]}, interface={result_tags=[{name=missing_tags, table_ref=null}]}}}");
	}

	@Test
	public void arM21_arraySumAmbiguousOperandV0Test() {
		final String query = "SELECT array_sum(tags) AS result_tags FROM events a JOIN archived_events b ON a.id = b.id";
		final SqlParseEventWalker extractor = runUnaryQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_sum' at (l:1 c:7) is recognized as Snowflake-specific grammar.",
						1, 7, "SqlASTWalkerHelper", null, null,
						true, "ast-walk", null, null),
				new ParseDiagnostic(ParseDiagnostic.Severity.SEVERE_WARNING,
						"AMBIGUOUS_COLUMN_REFERENCE",
						"Ambiguous column reference 'tags' at (l:1 c:17). Possible sources: [archived_events, events]",
						1, 17, "SqlASTWalkerHelper", null, "tags",
						true, "ast-walk", null, null),
				new ParseDiagnostic(ParseDiagnostic.Severity.ERROR,
						"UNRESOLVED_UNQUALIFIED_COLUMNS",
						"Unresolved unqualified column reference(s): [tags [(l:1 c:17)]]",
						1, 17, "SqlASTWalkerHelper", null, "tags",
						false, "ast-walk", null, null));
		assertOutputs(
				extractor,
				"{SQL={select={1={function={function_name=array_sum, parameters={1={column={name=tags, table_ref=null}}}}, alias=result_tags}}, from={join={1={table={alias=a, table=events}}, 2={join=JOIN, on={condition={left={column={name=id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=archived_events}}}}}}",
				"[result_tags]",
				"{}",
				"{archived_events={id=[[@18,85:85='b',<455>,1:85]]}, events={id=[[@14,78:78='a',<455>,1:78]]}}",
				"{query0={result_tags=[[@6,26:36='result_tags',<455>,1:26]]}}",
				"{def_query0={query_dictionary={result_tags=[[@6,26:36='result_tags',<455>,1:26]]}, table_dictionary={archived_events={id=[[@18,85:85='b',<455>,1:85]]}, events={id=[[@14,78:78='a',<455>,1:78]]}}, filters=[{name=id, table_ref=a}, {name=id, table_ref=b}], interface={result_tags=[{name=tags, table_ref=null}]}, table_alias={a=events, b=archived_events}}}");
	}

	@Test
	public void arM22_arraySumMissingAliasOperandV0Test() {
		final String query = "SELECT a.id, array_sum(z.tags) AS result_tags FROM events a WHERE a.active = 1";
		final SqlParseEventWalker extractor = runUnaryQuery(query);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_sum' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null,
						true, "ast-walk", null, null),
				new ParseDiagnostic(ParseDiagnostic.Severity.FATAL,
						"QUALIFIED_COLUMN_NOT_FOUND_IN_TABLE",
						"Source Table not found for Column 'tags' at (l:1 c:34). No alias or table called 'z'.",
						1, 34, "SqlASTWalkerHelper", null, "tags",
						false, "ast-walk", null, null));
		assertOutputs(
				extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={function_name=array_sum, parameters={1={column={name=tags, table_ref=z}}}}, alias=result_tags}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_tags, id]",
				"{}",
				"{events={active=[[@17,66:66='a',<455>,1:66]], id=[[@1,7:7='a',<455>,1:7]]}}",
				"{query0={result_tags=[[@12,34:44='result_tags',<455>,1:34]], id=[[@3,9:10='id',<455>,1:9]]}}",
				"{def_query0={query_dictionary={result_tags=[[@12,34:44='result_tags',<455>,1:34]], id=[[@3,9:10='id',<455>,1:9]]}, table_dictionary={events={active=[[@17,66:66='a',<455>,1:66]], id=[[@1,7:7='a',<455>,1:7]]}}, filters=[{name=active, table_ref=a}], interface={result_tags=[{name=tags, table_ref=z}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arM22_arraySumHiddenAliasOperandV0Test() {
		final String query = "SELECT d.id, array_sum(a.tags) AS result_tags FROM (SELECT a.id, a.tags FROM events a) d WHERE d.id > 0";
		final SqlParseEventWalker extractor = runUnaryQuery(query);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_sum' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null,
						true, "ast-walk", null, null),
				new ParseDiagnostic(ParseDiagnostic.Severity.FATAL,
						"QUALIFIED_COLUMN_NOT_FOUND_IN_TABLE",
						"Source Table not found for Column 'tags' at (l:1 c:34). No alias or table called 'a'.",
						1, 34, "SqlASTWalkerHelper", null, "tags",
						false, "ast-walk", null, null));
		assertOutputs(
				extractor,
				"{SQL={select={1={column={name=id, table_ref=d}}, 2={function={function_name=array_sum, parameters={1={column={name=tags, table_ref=a}}}}, alias=result_tags}}, from={table={alias=d, query={select={1={column={name=id, table_ref=a}}, 2={column={name=tags, table_ref=a}}}, from={table={alias=a, table=events}}}}}, where={condition={left={column={name=id, table_ref=d}}, right={literal=0}, operator=>}}}}",
				"[result_tags, id]",
				"{}",
				"{events={id=[[@16,59:59='a',<455>,1:59]], tags=[[@20,65:65='a',<455>,1:65]]}}",
				"{query0={id=[[@18,61:62='id',<455>,1:61], [@1,7:7='d',<455>,1:7], [@29,95:95='d',<455>,1:95]], tags=[[@22,67:70='tags',<455>,1:67]]}, query1={result_tags=[[@12,34:44='result_tags',<455>,1:34]], id=[[@3,9:10='id',<455>,1:9]]}}",
				"{def_query1={query_dictionary={result_tags=[[@12,34:44='result_tags',<455>,1:34]], id=[[@3,9:10='id',<455>,1:9]]}, table_dictionary={}, def_query0={query_dictionary={id=[[@18,61:62='id',<455>,1:61], [@1,7:7='d',<455>,1:7], [@29,95:95='d',<455>,1:95]], tags=[[@22,67:70='tags',<455>,1:67]]}, table_dictionary={events={id=[[@16,59:59='a',<455>,1:59]], tags=[[@20,65:65='a',<455>,1:65]]}}, interface={id=[{name=id, table_ref=a}], tags=[{name=tags, table_ref=a}]}, table_alias={a=events}}, filters=[{name=id, table_ref=d}], interface={result_tags=[{name=tags, table_ref=a}], id=[{name=id, table_ref=d}]}, table_alias={d=query0}}}");
	}

	@Test
	public void arC40_arraySumPredicandVariableV0Test() {
		final String query = "SELECT a.id, array_sum(<tags_expr>) AS result_tags FROM events a WHERE a.active = 1";
		final SqlParseEventWalker extractor = runUnaryQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_sum' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null,
						true, "ast-walk", null, null));
		assertOutputs(
				extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={function_name=array_sum, parameters={1={substitution={name=<tags_expr>, type=predicand}}}}, alias=result_tags}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_tags, id]",
				"{<tags_expr>=predicand}",
				"{events={active=[[@15,71:71='a',<455>,1:71]], id=[[@1,7:7='a',<455>,1:7]]}}",
				"{query0={result_tags=[[@10,39:49='result_tags',<455>,1:39]], id=[[@3,9:10='id',<455>,1:9]]}}",
				"{def_query0={query_dictionary={result_tags=[[@10,39:49='result_tags',<455>,1:39]], id=[[@3,9:10='id',<455>,1:9]]}, table_dictionary={events={active=[[@15,71:71='a',<455>,1:71]], id=[[@1,7:7='a',<455>,1:7]]}}, filters=[{name=active, table_ref=a}], interface={result_tags=[{name=<tags_expr>, type=predicand}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC40_arraySumQualifiedColumnVariableV0Test() {
		final String query = "SELECT a.id, array_sum(a.<tags_col>) AS result_tags FROM events a WHERE a.active = 1";
		final SqlParseEventWalker extractor = runUnaryQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_sum' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null,
						true, "ast-walk", null, null));
		assertOutputs(
				extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={function_name=array_sum, parameters={1={column={substitution={name=<tags_col>, type=column}, table_ref=a}}}}, alias=result_tags}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_tags, id]",
				"{<tags_col>=column}",
				"{events={active=[[@17,72:72='a',<455>,1:72]], id=[[@1,7:7='a',<455>,1:7]], <tags_col>=[[@7,23:23='a',<455>,1:23]]}}",
				"{query0={result_tags=[[@12,40:50='result_tags',<455>,1:40]], id=[[@3,9:10='id',<455>,1:9]]}}",
				"{def_query0={query_dictionary={result_tags=[[@12,40:50='result_tags',<455>,1:40]], id=[[@3,9:10='id',<455>,1:9]]}, table_dictionary={events={active=[[@17,72:72='a',<455>,1:72]], id=[[@1,7:7='a',<455>,1:7]], <tags_col>=[[@7,23:23='a',<455>,1:23]]}}, filters=[{name=active, table_ref=a}], interface={result_tags=[{substitution={name=<tags_col>, type=column}, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arM23_arraySumSchemaQualifiedRoutineControlV0Test() {
		final String query = "SELECT a.id, custom.array_sum(a.tags) AS result_tags FROM events a WHERE a.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(
				extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={schema=custom, parameters={1={column={name=tags, table_ref=a}}}, function_name=array_sum}, alias=result_tags}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_tags, id]",
				"{}",
				"{events={active=[[@19,73:73='a',<455>,1:73]], id=[[@1,7:7='a',<455>,1:7]], tags=[[@9,30:30='a',<455>,1:30]]}}",
				"{query0={result_tags=[[@14,41:51='result_tags',<455>,1:41]], id=[[@3,9:10='id',<455>,1:9]]}}",
				"{def_query0={query_dictionary={result_tags=[[@14,41:51='result_tags',<455>,1:41]], id=[[@3,9:10='id',<455>,1:9]]}, table_dictionary={events={active=[[@19,73:73='a',<455>,1:73]], id=[[@1,7:7='a',<455>,1:7]], tags=[[@9,30:30='a',<455>,1:30]]}}, filters=[{name=active, table_ref=a}], interface={result_tags=[{name=tags, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arM24_arraySumMixedDialectControlV0Test() {
		final String query = "SELECT array_sum(a.tags) AS result_tags, extract(isodow from a.created_at) AS weekday FROM events a";
		final SqlParseEventWalker extractor = runUnaryQuery(query);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_sum' at (l:1 c:7) is recognized as Snowflake-specific grammar.",
						1, 7, "SqlASTWalkerHelper", null, null,
						true, "ast-walk", null, null),
				new ParseDiagnostic(ParseDiagnostic.Severity.FATAL,
						"STATEMENT_MIXED_DIALECT_GRAMMAR",
						"Statement mixes Snowflake-only and PostgreSQL-only grammar at (l:1 c:41); unlikely to run on either engine.",
						1, 41, "SqlASTWalkerHelper", null, null,
						false, "ast-walk", null, null));
		assertOutputs(
				extractor,
				"{SQL={select={1={function={function_name=array_sum, parameters={1={column={name=tags, table_ref=a}}}}, alias=result_tags}, 2={alias=weekday, datetime_function={function_name=extract, part_form=KEYWORD, part=isodow, source={column={name=created_at, table_ref=a}}}}}, from={table={alias=a, table=events}}}}",
				"[result_tags, weekday]",
				"{}",
				"{events={created_at=[[@14,61:61='a',<455>,1:61]], tags=[[@3,17:17='a',<455>,1:17]]}}",
				"{query0={weekday=[[@19,78:84='weekday',<455>,1:78]], result_tags=[[@8,28:38='result_tags',<455>,1:28]]}}",
				"{def_query0={query_dictionary={result_tags=[[@8,28:38='result_tags',<455>,1:28]], weekday=[[@19,78:84='weekday',<455>,1:78]]}, table_dictionary={events={created_at=[[@14,61:61='a',<455>,1:61]], tags=[[@3,17:17='a',<455>,1:17]]}}, interface={result_tags=[{name=tags, table_ref=a}], weekday=[{name=created_at, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC40_arraySumUppercaseNameV0Test() {
		final String query = "SELECT a.id, ARRAY_SUM(a.tags) AS result_tags FROM events a WHERE a.active = 1";
		final SqlParseEventWalker extractor = runUnaryQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_sum' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null,
						true, "ast-walk", null, null));
		assertOutputs(
				extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={function_name=ARRAY_SUM, parameters={1={column={name=tags, table_ref=a}}}}, alias=result_tags}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_tags, id]",
				"{}",
				"{events={active=[[@17,66:66='a',<455>,1:66]], id=[[@1,7:7='a',<455>,1:7]], tags=[[@7,23:23='a',<455>,1:23]]}}",
				"{query0={result_tags=[[@12,34:44='result_tags',<455>,1:34]], id=[[@3,9:10='id',<455>,1:9]]}}",
				"{def_query0={query_dictionary={result_tags=[[@12,34:44='result_tags',<455>,1:34]], id=[[@3,9:10='id',<455>,1:9]]}, table_dictionary={events={active=[[@17,66:66='a',<455>,1:66]], id=[[@1,7:7='a',<455>,1:7]], tags=[[@7,23:23='a',<455>,1:23]]}}, filters=[{name=active, table_ref=a}], interface={result_tags=[{name=tags, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC40_arraySumNullOperandV0Test() {
		final String query = "SELECT a.id, array_sum(NULL) AS result_tags FROM events a WHERE a.active = 1";
		final SqlParseEventWalker extractor = runUnaryQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_sum' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null,
						true, "ast-walk", null, null));
		assertOutputs(
				extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={function_name=array_sum, parameters={1={null_literal=null}}}, alias=result_tags}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_tags, id]",
				"{}",
				"{events={active=[[@15,64:64='a',<455>,1:64]], id=[[@1,7:7='a',<455>,1:7]]}}",
				"{query0={result_tags=[[@10,32:42='result_tags',<455>,1:32]], id=[[@3,9:10='id',<455>,1:9]]}}",
				"{def_query0={query_dictionary={result_tags=[[@10,32:42='result_tags',<455>,1:32]], id=[[@3,9:10='id',<455>,1:9]]}, table_dictionary={events={active=[[@15,64:64='a',<455>,1:64]], id=[[@1,7:7='a',<455>,1:7]]}}, filters=[{name=active, table_ref=a}], interface={result_tags=[], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arM23_arraySumNonreservedColumnNameV0Test() {
		final String query = "SELECT a.array_sum AS result_tags FROM events a WHERE a.id > 0";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(
				extractor,
				"{SQL={select={1={column={name=array_sum, table_ref=a}, alias=result_tags}}, from={table={alias=a, table=events}}, where={condition={left={column={name=id, table_ref=a}}, right={literal=0}, operator=>}}}}",
				"[result_tags]",
				"{}",
				"{events={array_sum=[[@1,7:7='a',<455>,1:7]], id=[[@10,54:54='a',<455>,1:54]]}}",
				"{query0={result_tags=[[@5,22:32='result_tags',<455>,1:22]]}}",
				"{def_query0={query_dictionary={result_tags=[[@5,22:32='result_tags',<455>,1:22]]}, table_dictionary={events={array_sum=[[@1,7:7='a',<455>,1:7]], id=[[@10,54:54='a',<455>,1:54]]}}, filters=[{name=id, table_ref=a}], interface={result_tags=[{name=array_sum, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC40_arraysToObjectSelectPlacementV0Test() {
		final String query = "SELECT a.id, arrays_to_object(a.keys_arr, a.values_arr) AS result_object FROM events a WHERE a.active = 1";
		final SqlParseEventWalker extractor = runObjectConversionQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'arrays_to_object' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null,
						true, "ast-walk", null, null));
		assertOutputs(
				extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={function_name=arrays_to_object, parameters={1={column={name=keys_arr, table_ref=a}}, 2={column={name=values_arr, table_ref=a}}}}, alias=result_object}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_object, id]",
				"{}",
				"{events={keys_arr=[[@7,30:30='a',<455>,1:30]], active=[[@21,93:93='a',<455>,1:93]], id=[[@1,7:7='a',<455>,1:7]], values_arr=[[@11,42:42='a',<455>,1:42]]}}",
				"{query0={result_object=[[@16,59:71='result_object',<455>,1:59]], id=[[@3,9:10='id',<455>,1:9]]}}",
				"{def_query0={query_dictionary={result_object=[[@16,59:71='result_object',<455>,1:59]], id=[[@3,9:10='id',<455>,1:9]]}, table_dictionary={events={keys_arr=[[@7,30:30='a',<455>,1:30]], active=[[@21,93:93='a',<455>,1:93]], id=[[@1,7:7='a',<455>,1:7]], values_arr=[[@11,42:42='a',<455>,1:42]]}}, filters=[{name=active, table_ref=a}], interface={result_object=[{name=keys_arr, table_ref=a}, {name=values_arr, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC40_arraysToObjectWherePlacementV0Test() {
		final String query = "SELECT a.id FROM events a WHERE arrays_to_object(a.keys_arr, a.values_arr) IS NOT NULL AND a.active = 1";
		final SqlParseEventWalker extractor = runObjectConversionQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'arrays_to_object' at (l:1 c:32) is recognized as Snowflake-specific grammar.",
						1, 32, "SqlASTWalkerHelper", null, null,
						true, "ast-walk", null, null));
		assertOutputs(
				extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}}, from={table={alias=a, table=events}}, where={and={1={condition={left={function={function_name=arrays_to_object, parameters={1={column={name=keys_arr, table_ref=a}}, 2={column={name=values_arr, table_ref=a}}}}}, operator=IS NOT NULL}}, 2={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}}}",
				"[id]",
				"{}",
				"{events={keys_arr=[[@10,49:49='a',<455>,1:49]], active=[[@22,91:91='a',<455>,1:91]], id=[[@1,7:7='a',<455>,1:7]], values_arr=[[@14,61:61='a',<455>,1:61]]}}",
				"{query0={id=[[@3,9:10='id',<455>,1:9]]}}",
				"{def_query0={query_dictionary={id=[[@3,9:10='id',<455>,1:9]]}, table_dictionary={events={keys_arr=[[@10,49:49='a',<455>,1:49]], active=[[@22,91:91='a',<455>,1:91]], id=[[@1,7:7='a',<455>,1:7]], values_arr=[[@14,61:61='a',<455>,1:61]]}}, filters=[{name=keys_arr, table_ref=a}, {name=values_arr, table_ref=a}, {name=active, table_ref=a}], interface={id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC40_arraysToObjectJoinOnPlacementV0Test() {
		final String query = "SELECT a.id, b.id AS other_id FROM events a JOIN archived_events b ON arrays_to_object(a.keys_arr, a.values_arr) = b.payload AND a.id = b.id";
		final SqlParseEventWalker extractor = runObjectConversionQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'arrays_to_object' at (l:1 c:70) is recognized as Snowflake-specific grammar.",
						1, 70, "SqlASTWalkerHelper", null, null,
						true, "ast-walk", null, null));
		assertOutputs(
				extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={column={name=id, table_ref=b}, alias=other_id}}, from={join={1={table={alias=a, table=events}}, 2={join=JOIN, on={and={1={condition={left={function={function_name=arrays_to_object, parameters={1={column={name=keys_arr, table_ref=a}}, 2={column={name=values_arr, table_ref=a}}}}}, right={column={name=payload, table_ref=b}}, operator==}}, 2={condition={left={column={name=id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}}}, 3={table={alias=b, table=archived_events}}}}}}",
				"[id, other_id]",
				"{}",
				"{archived_events={payload=[[@28,115:115='b',<455>,1:115]], id=[[@5,13:13='b',<455>,1:13], [@36,136:136='b',<455>,1:136]]}, events={keys_arr=[[@19,87:87='a',<455>,1:87]], id=[[@1,7:7='a',<455>,1:7], [@32,129:129='a',<455>,1:129]], values_arr=[[@23,99:99='a',<455>,1:99]]}}",
				"{query0={id=[[@3,9:10='id',<455>,1:9]], other_id=[[@9,21:28='other_id',<455>,1:21]]}}",
				"{def_query0={query_dictionary={id=[[@3,9:10='id',<455>,1:9]], other_id=[[@9,21:28='other_id',<455>,1:21]]}, table_dictionary={archived_events={payload=[[@28,115:115='b',<455>,1:115]], id=[[@5,13:13='b',<455>,1:13], [@36,136:136='b',<455>,1:136]]}, events={keys_arr=[[@19,87:87='a',<455>,1:87]], id=[[@1,7:7='a',<455>,1:7], [@32,129:129='a',<455>,1:129]], values_arr=[[@23,99:99='a',<455>,1:99]]}}, filters=[{name=keys_arr, table_ref=a}, {name=values_arr, table_ref=a}, {name=payload, table_ref=b}, {name=id, table_ref=a}, {name=id, table_ref=b}], interface={id=[{name=id, table_ref=a}], other_id=[{name=id, table_ref=b}]}, table_alias={a=events, b=archived_events}}}");
	}

	@Test
	public void arC40_arraysToObjectHavingPlacementV0Test() {
		final String query = "SELECT a.group_id, count(*) AS n FROM events a GROUP BY a.group_id HAVING arrays_to_object(array_agg(a.key_col), array_agg(a.value_col)) IS NOT NULL AND count(*) > 1";
		final SqlParseEventWalker extractor = runObjectConversionQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'arrays_to_object' at (l:1 c:74) is recognized as Snowflake-specific grammar.",
						1, 74, "SqlASTWalkerHelper", null, null,
						true, "ast-walk", null, null));
		assertOutputs(
				extractor,
				"{SQL={select={1={column={name=group_id, table_ref=a}}, 2={function={function_name=COUNT, qualifier=null, parameters=*}, alias=n}}, having={and={1={condition={left={function={function_name=arrays_to_object, parameters={1={function={function_name=array_agg, qualifier=null, parameters={column={name=key_col, table_ref=a}}}}, 2={function={function_name=array_agg, qualifier=null, parameters={column={name=value_col, table_ref=a}}}}}}}, operator=IS NOT NULL}}, 2={condition={left={function={function_name=COUNT, qualifier=null, parameters=*}}, right={literal=1}, operator=>}}}}, from={table={alias=a, table=events}}, groupby={1={column={name=group_id, table_ref=a}}}}}",
				"[group_id, n]",
				"{}",
				"{events={value_col=[[@31,123:123='a',<455>,1:123]], group_id=[[@1,7:7='a',<455>,1:7], [@16,56:56='a',<455>,1:56]], key_col=[[@24,101:101='a',<455>,1:101]], *=[[@7,25:25='*',<291>,1:25], [@42,159:159='*',<291>,1:159]]}}",
				"{query0={group_id=[[@3,9:16='group_id',<455>,1:9]], n=[[@10,31:31='n',<455>,1:31]]}}",
				"{def_query0={query_dictionary={group_id=[[@3,9:16='group_id',<455>,1:9]], n=[[@10,31:31='n',<455>,1:31]]}, table_dictionary={events={value_col=[[@31,123:123='a',<455>,1:123]], group_id=[[@1,7:7='a',<455>,1:7], [@16,56:56='a',<455>,1:56]], *=[[@7,25:25='*',<291>,1:25], [@42,159:159='*',<291>,1:159]], key_col=[[@24,101:101='a',<455>,1:101]]}}, grouped_by=[{name=group_id, table_ref=a}], filters=[{name=key_col, table_ref=a}, {name=value_col, table_ref=a}], interface={group_id=[{name=group_id, table_ref=a}], n=[]}, table_alias={a=events}}}");
	}

	@Test
	public void arC40_arraysToObjectQualifyPlacementV0Test() {
		final String query = "SELECT a.id FROM events a QUALIFY arrays_to_object(a.keys_arr, a.values_arr) IS NOT NULL AND a.id > 0";
		final SqlParseEventWalker extractor = runObjectConversionQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'arrays_to_object' at (l:1 c:34) is recognized as Snowflake-specific grammar.",
						1, 34, "SqlASTWalkerHelper", null, null,
						true, "ast-walk", null, null));
		assertOutputs(
				extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}}, from={table={alias=a, table=events}}, qualify={and={1={condition={left={function={function_name=arrays_to_object, parameters={1={column={name=keys_arr, table_ref=a}}, 2={column={name=values_arr, table_ref=a}}}}}, operator=IS NOT NULL}}, 2={condition={left={column={name=id, table_ref=a}}, right={literal=0}, operator=>}}}}}}",
				"[id]",
				"{}",
				"{events={keys_arr=[[@10,51:51='a',<455>,1:51]], id=[[@1,7:7='a',<455>,1:7], [@22,93:93='a',<455>,1:93]], values_arr=[[@14,63:63='a',<455>,1:63]]}}",
				"{query0={id=[[@3,9:10='id',<455>,1:9]]}}",
				"{def_query0={query_dictionary={id=[[@3,9:10='id',<455>,1:9]]}, table_dictionary={events={keys_arr=[[@10,51:51='a',<455>,1:51]], id=[[@1,7:7='a',<455>,1:7], [@22,93:93='a',<455>,1:93]], values_arr=[[@14,63:63='a',<455>,1:63]]}}, filters=[{name=keys_arr, table_ref=a}, {name=values_arr, table_ref=a}, {name=id, table_ref=a}], interface={id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC40_arraysToObjectOrderByPlacementV0Test() {
		final String query = "SELECT a.id FROM events a WHERE a.active = 1 ORDER BY arrays_to_object(a.keys_arr, a.values_arr), a.id";
		final SqlParseEventWalker extractor = runObjectConversionQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'arrays_to_object' at (l:1 c:54) is recognized as Snowflake-specific grammar.",
						1, 54, "SqlASTWalkerHelper", null, null,
						true, "ast-walk", null, null));
		assertOutputs(
				extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}}, orderby={1={null_order=null, predicand={function={function_name=arrays_to_object, parameters={1={column={name=keys_arr, table_ref=a}}, 2={column={name=values_arr, table_ref=a}}}}}, sort_order=ASC}, 2={null_order=null, predicand={column={name=id, table_ref=a}}, sort_order=ASC}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[id]",
				"{}",
				"{events={keys_arr=[[@17,71:71='a',<455>,1:71]], active=[[@8,32:32='a',<455>,1:32]], id=[[@1,7:7='a',<455>,1:7], [@26,98:98='a',<455>,1:98]], values_arr=[[@21,83:83='a',<455>,1:83]]}}",
				"{query0={id=[[@3,9:10='id',<455>,1:9]]}}",
				"{def_query0={query_dictionary={id=[[@3,9:10='id',<455>,1:9]]}, table_dictionary={events={keys_arr=[[@17,71:71='a',<455>,1:71]], active=[[@8,32:32='a',<455>,1:32]], id=[[@1,7:7='a',<455>,1:7], [@26,98:98='a',<455>,1:98]], values_arr=[[@21,83:83='a',<455>,1:83]]}}, ordered_by=[{name=keys_arr, table_ref=a}, {name=values_arr, table_ref=a}, {name=id, table_ref=a}], filters=[{name=active, table_ref=a}], interface={id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC40_arraysToObjectValuesPlacementV0Test() {
		final String query = "VALUES (arrays_to_object(<keys_expr>, <values_expr>), 7)";
		final SqlParseEventWalker extractor = runObjectConversionQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'arrays_to_object' at (l:1 c:8) is recognized as Snowflake-specific grammar.",
						1, 8, "SqlASTWalkerHelper", null, null,
						true, "ast-walk", null, null));
		assertOutputs(
				extractor,
				"{SQL={values={matrix={1={row={1={function={function_name=arrays_to_object, parameters={1={substitution={name=<keys_expr>, type=predicand}}, 2={substitution={name=<values_expr>, type=predicand}}}}}, 2={literal=7}}}}}}}",
				"[$1, $2]",
				"{<keys_expr>=predicand, <values_expr>=predicand}",
				"{}",
				"{values0={$1=[[@1,7:7='(',<287>,1:7]], $2=[[@1,7:7='(',<287>,1:7]]}}",
				"{def_values0={query_dictionary={$1=[[@1,7:7='(',<287>,1:7]], $2=[[@1,7:7='(',<287>,1:7]]}, interface={$1=[], $2=[]}}}");
	}

	@Test
	public void arC40_arraysToObjectInsertPlacementV0Test() {
		final String query = "INSERT INTO archived_events (payload, id) SELECT arrays_to_object(a.keys_arr, a.values_arr), a.id FROM events a WHERE a.active = 1";
		final SqlParseEventWalker extractor = runObjectConversionQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'arrays_to_object' at (l:1 c:49) is recognized as Snowflake-specific grammar.",
						1, 49, "SqlASTWalkerHelper", null, null,
						true, "ast-walk", null, null));
		assertOutputs(
				extractor,
				"{SQL={insert={preamble=insert_into, from={from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}, select={1={function={function_name=arrays_to_object, parameters={1={column={name=keys_arr, table_ref=a}}, 2={column={name=values_arr, table_ref=a}}}}}, 2={column={name=id, table_ref=a}}}}, target_table={table={alias=null, table=archived_events}}, columns={1={column={name=payload, table_ref=null}}, 2={column={name=id, table_ref=null}}}}}}",
				"[payload, id]",
				"{}",
				"{archived_events={payload=[[@4,29:35='payload',<455>,1:29]], id=[[@6,38:39='id',<455>,1:38]]}, events={keys_arr=[[@11,66:66='a',<455>,1:66]], active=[[@27,118:118='a',<455>,1:118]], id=[[@20,93:93='a',<455>,1:93]], values_arr=[[@15,78:78='a',<455>,1:78]]}}",
				"{query0={unnamed_0=[[@18,90:90=')',<288>,1:90]], id=[[@22,95:96='id',<455>,1:95]]}, insert1={payload=[[@4,29:35='payload',<455>,1:29]], id=[[@6,38:39='id',<455>,1:38]]}}",
				"{def_insert1={query_dictionary={payload=[[@4,29:35='payload',<455>,1:29]], id=[[@6,38:39='id',<455>,1:38]]}, table_dictionary={archived_events={payload=[[@4,29:35='payload',<455>,1:29]], id=[[@6,38:39='id',<455>,1:38]]}}, def_query0={query_dictionary={unnamed_0=[[@18,90:90=')',<288>,1:90]], id=[[@22,95:96='id',<455>,1:95]]}, table_dictionary={events={keys_arr=[[@11,66:66='a',<455>,1:66]], active=[[@27,118:118='a',<455>,1:118]], id=[[@20,93:93='a',<455>,1:93]], values_arr=[[@15,78:78='a',<455>,1:78]]}}, filters=[{name=active, table_ref=a}], interface={unnamed_0=[{name=keys_arr, table_ref=a}, {name=values_arr, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}, interface={payload=[{name=unnamed_0, table_ref=query0}], id=[{name=id, table_ref=query0}]}}}");
	}

	@Test
	public void arC40_arraysToObjectUpdatePlacementV0Test() {
		final String query = "UPDATE events SET payload = arrays_to_object(keys_arr, values_arr), active = 1 WHERE id > 0";
		final SqlParseEventWalker extractor = runObjectConversionQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'arrays_to_object' at (l:1 c:28) is recognized as Snowflake-specific grammar.",
						1, 28, "SqlASTWalkerHelper", null, null,
						true, "ast-walk", null, null));
		assertOutputs(
				extractor,
				"{SQL={update={table={alias=null, table=events}, where={condition={left={column={name=id, table_ref=null}}, right={literal=0}, operator=>}}, assignments={1={set={column={name=payload, table_ref=null}}, to={function={function_name=arrays_to_object, parameters={1={column={name=keys_arr, table_ref=null}}, 2={column={name=values_arr, table_ref=null}}}}}}, 2={set={column={name=active, table_ref=null}}, to={literal=1}}}}}}",
				"[payload, active]",
				"{}",
				"{events={keys_arr=[[@7,45:52='keys_arr',<455>,1:45], [@10,65:65=')',<288>,1:65]], id=[[@16,85:86='id',<455>,1:85]], values_arr=[[@9,55:64='values_arr',<455>,1:55], [@10,65:65=')',<288>,1:65]]}}",
				"{update0={payload=[[@3,18:24='payload',<455>,1:18]], active=[[@12,68:73='active',<455>,1:68]]}}",
				"{def_update0={assignments={payload=[{name=keys_arr, table_ref=events}, {name=values_arr, table_ref=events}], active=[]}, table_dictionary={events={keys_arr=[[@7,45:52='keys_arr',<455>,1:45], [@10,65:65=')',<288>,1:65]], id=[[@16,85:86='id',<455>,1:85]], values_arr=[[@9,55:64='values_arr',<455>,1:55], [@10,65:65=')',<288>,1:65]]}}, update_dictionary={payload=[[@3,18:24='payload',<455>,1:18]], active=[[@12,68:73='active',<455>,1:68]]}, target_table={events={payload=[[@3,18:24='payload',<455>,1:18]], active=[[@12,68:73='active',<455>,1:68]]}}, filters=[{name=id, table_ref=events}], lhs_unresolved_columns={payload={column={name=payload, table_ref=null}, locations=[[@3,18:24='payload',<455>,1:18]]}, active={column={name=active, table_ref=null}, locations=[[@12,68:73='active',<455>,1:68]]}}}}");
	}

	@Test
	public void arC40_arraysToObjectDeletePlacementV0Test() {
		final String query = "DELETE FROM events WHERE arrays_to_object(keys_arr, values_arr) IS NULL AND id > 0";
		final SqlParseEventWalker extractor = runObjectConversionQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'arrays_to_object' at (l:1 c:25) is recognized as Snowflake-specific grammar.",
						1, 25, "SqlASTWalkerHelper", null, null,
						true, "ast-walk", null, null));
		assertOutputs(
				extractor,
				"{SQL={delete={table={alias=null, table=events}, where={and={1={condition={left={function={function_name=arrays_to_object, parameters={1={column={name=keys_arr, table_ref=null}}, 2={column={name=values_arr, table_ref=null}}}}}, operator=IS NULL}}, 2={condition={left={column={name=id, table_ref=null}}, right={literal=0}, operator=>}}}}}}}",
				"[]",
				"{}",
				"{events={keys_arr=[[@6,42:49='keys_arr',<455>,1:42]], id=[[@13,76:77='id',<455>,1:76]], values_arr=[[@8,52:61='values_arr',<455>,1:52]]}}",
				"{}",
				"{def_delete0={query_dictionary={}, table_dictionary={events={keys_arr=[[@6,42:49='keys_arr',<455>,1:42]], id=[[@13,76:77='id',<455>,1:76]], values_arr=[[@8,52:61='values_arr',<455>,1:52]]}}, filters=[{name=keys_arr, table_ref=events}, {name=values_arr, table_ref=events}, {name=id, table_ref=events}], interface=null}}");
	}

	@Test
	public void arC40_arraysToObjectDdlPlacementV0Test() {
		final String query = "CREATE VIEW v_obj AS SELECT a.id, arrays_to_object(a.keys_arr, a.values_arr) AS result_object FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SqlParseEventWalker extractor = runDdlParsertest(query, parser);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'arrays_to_object' at (l:1 c:34) is recognized as Snowflake-specific grammar.",
						1, 34, "SqlASTWalkerHelper", null, null,
						true, "ast-walk", null, null));
		assertOutputs(
				extractor,
				"{DDL={create={type=VIEW, name={table=v_obj}, query={select={1={column={name=id, table_ref=a}}, 2={function={function_name=arrays_to_object, parameters={1={column={name=keys_arr, table_ref=a}}, 2={column={name=values_arr, table_ref=a}}}}, alias=result_object}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}}}",
				"[]",
				"{}",
				"{events={keys_arr=[[@11,51:51='a',<455>,1:51]], active=[[@25,114:114='a',<455>,1:114]], id=[[@5,28:28='a',<455>,1:28]], values_arr=[[@15,63:63='a',<455>,1:63]]}}",
				"{query0={result_object=[[@20,80:92='result_object',<455>,1:80]], id=[[@7,30:31='id',<455>,1:30]]}}",
				"{def_create1={def_query0={query_dictionary={result_object=[[@20,80:92='result_object',<455>,1:80]], id=[[@7,30:31='id',<455>,1:30]]}, table_dictionary={events={keys_arr=[[@11,51:51='a',<455>,1:51]], active=[[@25,114:114='a',<455>,1:114]], id=[[@5,28:28='a',<455>,1:28]], values_arr=[[@15,63:63='a',<455>,1:63]]}}, filters=[{name=active, table_ref=a}], interface={result_object=[{name=keys_arr, table_ref=a}, {name=values_arr, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}}");
	}

	@Test
	public void arR20_arraysToObjectScriptStatementResetV0Test() {
		final String query = "SELECT a.id, arrays_to_object(a.keys_arr, a.values_arr) AS result_object FROM events a;\nSELECT b.id FROM archived_events b;\nSELECT c.id, arrays_to_object(c.keys_arr, c.values_arr) AS result_object FROM events c;";
		final SQLSelectParserParser parser = parse(query);
		final SqlParseEventWalker extractor = runScriptParsertest(query, parser);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'arrays_to_object' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null,
						true, "ast-walk", null, null),
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'arrays_to_object' at (l:3 c:13) is recognized as Snowflake-specific grammar.",
						3, 13, "SqlASTWalkerHelper", null, null,
						true, "ast-walk", null, null));
		assertOutputs(
				extractor,
				"{SCRIPT={1={select={1={column={name=id, table_ref=a}}, 2={function={function_name=arrays_to_object, parameters={1={column={name=keys_arr, table_ref=a}}, 2={column={name=values_arr, table_ref=a}}}}, alias=result_object}}, from={table={alias=a, table=events}}}, 2={select={1={column={name=id, table_ref=b}}}, from={table={alias=b, table=archived_events}}}, 3={select={1={column={name=id, table_ref=c}}, 2={function={function_name=arrays_to_object, parameters={1={column={name=keys_arr, table_ref=c}}, 2={column={name=values_arr, table_ref=c}}}}, alias=result_object}}, from={table={alias=c, table=events}}}}}",
				"[]",
				"{SCRIPT={1={}, 2={}, 3={}}}",
				"{SCRIPT={1={events={keys_arr=[[@7,30:30='a',<455>,1:30]], id=[[@1,7:7='a',<455>,1:7]], values_arr=[[@11,42:42='a',<455>,1:42]]}}, 2={archived_events={id=[[@22,95:95='b',<455>,2:7]]}}, 3={events={keys_arr=[[@36,154:154='c',<455>,3:30]], id=[[@30,131:131='c',<455>,3:7]], values_arr=[[@40,166:166='c',<455>,3:42]]}}}}",
				"{SCRIPT={1={query0={result_object=[[@16,59:71='result_object',<455>,1:59]], id=[[@3,9:10='id',<455>,1:9]]}}, 2={query0={id=[[@24,97:98='id',<455>,2:9]]}}, 3={query0={result_object=[[@45,183:195='result_object',<455>,3:59]], id=[[@32,133:134='id',<455>,3:9]]}}}}",
				"{SCRIPT={1={def_query0={query_dictionary={result_object=[[@16,59:71='result_object',<455>,1:59]], id=[[@3,9:10='id',<455>,1:9]]}, table_dictionary={events={keys_arr=[[@7,30:30='a',<455>,1:30]], id=[[@1,7:7='a',<455>,1:7]], values_arr=[[@11,42:42='a',<455>,1:42]]}}, interface={result_object=[{name=keys_arr, table_ref=a}, {name=values_arr, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}, 2={def_query0={query_dictionary={id=[[@24,97:98='id',<455>,2:9]]}, table_dictionary={archived_events={id=[[@22,95:95='b',<455>,2:7]]}}, interface={id=[{name=id, table_ref=b}]}, table_alias={b=archived_events}}}, 3={def_query0={query_dictionary={result_object=[[@45,183:195='result_object',<455>,3:59]], id=[[@32,133:134='id',<455>,3:9]]}, table_dictionary={events={keys_arr=[[@36,154:154='c',<455>,3:30]], id=[[@30,131:131='c',<455>,3:7]], values_arr=[[@40,166:166='c',<455>,3:42]]}}, interface={result_object=[{name=keys_arr, table_ref=c}, {name=values_arr, table_ref=c}], id=[{name=id, table_ref=c}]}, table_alias={c=events}}}}}");
	}

	@Test
	public void arR18_arraysToObjectNestedExpressionV0Test() {
		final String query = "SELECT a.id, coalesce(arrays_to_object(a.keys_arr, a.values_arr), a.payload) AS result_object FROM events a WHERE a.active = 1";
		final SqlParseEventWalker extractor = runObjectConversionQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'arrays_to_object' at (l:1 c:22) is recognized as Snowflake-specific grammar.",
						1, 22, "SqlASTWalkerHelper", null, null,
						true, "ast-walk", null, null));
		assertOutputs(
				extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={parameters={1={function={function_name=arrays_to_object, parameters={1={column={name=keys_arr, table_ref=a}}, 2={column={name=values_arr, table_ref=a}}}}}, 2={column={name=payload, table_ref=a}}}, function_name=coalesce}, alias=result_object}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_object, id]",
				"{}",
				"{events={payload=[[@18,66:66='a',<455>,1:66]], keys_arr=[[@9,39:39='a',<455>,1:39]], active=[[@28,114:114='a',<455>,1:114]], id=[[@1,7:7='a',<455>,1:7]], values_arr=[[@13,51:51='a',<455>,1:51]]}}",
				"{query0={result_object=[[@23,80:92='result_object',<455>,1:80]], id=[[@3,9:10='id',<455>,1:9]]}}",
				"{def_query0={query_dictionary={result_object=[[@23,80:92='result_object',<455>,1:80]], id=[[@3,9:10='id',<455>,1:9]]}, table_dictionary={events={payload=[[@18,66:66='a',<455>,1:66]], keys_arr=[[@9,39:39='a',<455>,1:39]], active=[[@28,114:114='a',<455>,1:114]], id=[[@1,7:7='a',<455>,1:7]], values_arr=[[@13,51:51='a',<455>,1:51]]}}, filters=[{name=active, table_ref=a}], interface={result_object=[{name=keys_arr, table_ref=a}, {name=values_arr, table_ref=a}, {name=payload, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arR18_arraysToObjectDerivedOutputBoundaryV0Test() {
		final String query = "SELECT d.id, d.result_object FROM (SELECT a.id, arrays_to_object(a.keys_arr, a.values_arr) AS result_object FROM events a) d WHERE d.id > 0";
		final SqlParseEventWalker extractor = runObjectConversionQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'arrays_to_object' at (l:1 c:48) is recognized as Snowflake-specific grammar.",
						1, 48, "SqlASTWalkerHelper", null, null,
						true, "ast-walk", null, null));
		assertOutputs(
				extractor,
				"{SQL={select={1={column={name=id, table_ref=d}}, 2={column={name=result_object, table_ref=d}}}, from={table={alias=d, query={select={1={column={name=id, table_ref=a}}, 2={function={function_name=arrays_to_object, parameters={1={column={name=keys_arr, table_ref=a}}, 2={column={name=values_arr, table_ref=a}}}}, alias=result_object}}, from={table={alias=a, table=events}}}}}, where={condition={left={column={name=id, table_ref=d}}, right={literal=0}, operator=>}}}}",
				"[result_object, id]",
				"{}",
				"{events={keys_arr=[[@17,65:65='a',<455>,1:65]], id=[[@11,42:42='a',<455>,1:42]], values_arr=[[@21,77:77='a',<455>,1:77]]}}",
				"{query0={result_object=[[@26,94:106='result_object',<455>,1:94], [@5,13:13='d',<455>,1:13]], id=[[@13,44:45='id',<455>,1:44], [@1,7:7='d',<455>,1:7], [@33,131:131='d',<455>,1:131]]}, query1={result_object=[[@7,15:27='result_object',<455>,1:15]], id=[[@3,9:10='id',<455>,1:9]]}}",
				"{def_query1={query_dictionary={result_object=[[@7,15:27='result_object',<455>,1:15]], id=[[@3,9:10='id',<455>,1:9]]}, def_query0={query_dictionary={result_object=[[@26,94:106='result_object',<455>,1:94], [@5,13:13='d',<455>,1:13]], id=[[@13,44:45='id',<455>,1:44], [@1,7:7='d',<455>,1:7], [@33,131:131='d',<455>,1:131]]}, table_dictionary={events={keys_arr=[[@17,65:65='a',<455>,1:65]], id=[[@11,42:42='a',<455>,1:42]], values_arr=[[@21,77:77='a',<455>,1:77]]}}, interface={result_object=[{name=keys_arr, table_ref=a}, {name=values_arr, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}, filters=[{name=id, table_ref=d}], interface={result_object=[{name=result_object, table_ref=d}], id=[{name=id, table_ref=d}]}, table_alias={d=query0}}}");
	}

	@Test
	public void arR19_arraysToObjectSetOpSiblingIsolationV0Test() {
		final String query = "SELECT a.id, arrays_to_object(a.keys_arr, a.values_arr) AS result_object FROM events a UNION ALL SELECT b.id, b.payload AS result_object FROM archived_events b";
		final SqlParseEventWalker extractor = runObjectConversionQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'arrays_to_object' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null,
						true, "ast-walk", null, null));
		assertOutputs(
				extractor,
				"{SQL={union={1={select={1={column={name=id, table_ref=a}}, 2={function={function_name=arrays_to_object, parameters={1={column={name=keys_arr, table_ref=a}}, 2={column={name=values_arr, table_ref=a}}}}, alias=result_object}}, from={table={alias=a, table=events}}}, 2={union={qualifier=ALL, operator=UNION}}, 3={select={1={column={name=id, table_ref=b}}, 2={column={name=payload, table_ref=b}, alias=result_object}}, from={table={alias=b, table=archived_events}}}}}}",
				"[result_object, id]",
				"{}",
				"{archived_events={payload=[[@27,110:110='b',<455>,1:110]], id=[[@23,104:104='b',<455>,1:104]]}, events={keys_arr=[[@7,30:30='a',<455>,1:30]], id=[[@1,7:7='a',<455>,1:7]], values_arr=[[@11,42:42='a',<455>,1:42]]}}",
				"{query0={result_object=[[@16,59:71='result_object',<455>,1:59]], id=[[@3,9:10='id',<455>,1:9]]}, query1={result_object=[[@31,123:135='result_object',<455>,1:123]], id=[[@25,106:107='id',<455>,1:106]]}}",
				"{def_union2={def_query1={query_dictionary={result_object=[[@31,123:135='result_object',<455>,1:123]], id=[[@25,106:107='id',<455>,1:106]]}, table_dictionary={archived_events={payload=[[@27,110:110='b',<455>,1:110]], id=[[@23,104:104='b',<455>,1:104]]}}, setop=UNION, interface={result_object=[{name=payload, table_ref=b}], id=[{name=id, table_ref=b}]}, table_alias={b=archived_events}}, def_query0={query_dictionary={result_object=[[@16,59:71='result_object',<455>,1:59]], id=[[@3,9:10='id',<455>,1:9]]}, table_dictionary={events={keys_arr=[[@7,30:30='a',<455>,1:30]], id=[[@1,7:7='a',<455>,1:7]], values_arr=[[@11,42:42='a',<455>,1:42]]}}, interface={result_object=[{name=keys_arr, table_ref=a}, {name=values_arr, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}, interface={result_object=query_column, id=query_column}}}");
	}

	@Test
	public void arC40_arraysToObjectUnqualifiedOperandsV0Test() {
		final String query = "SELECT id, arrays_to_object(keys_arr, values_arr) AS result_object FROM events WHERE active = 1";
		final SqlParseEventWalker extractor = runObjectConversionQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'arrays_to_object' at (l:1 c:11) is recognized as Snowflake-specific grammar.",
						1, 11, "SqlASTWalkerHelper", null, null,
						true, "ast-walk", null, null));
		assertOutputs(
				extractor,
				"{SQL={select={1={column={name=id, table_ref=null}}, 2={function={function_name=arrays_to_object, parameters={1={column={name=keys_arr, table_ref=null}}, 2={column={name=values_arr, table_ref=null}}}}, alias=result_object}}, from={table={alias=null, table=events}}, where={condition={left={column={name=active, table_ref=null}}, right={literal=1}, operator==}}}}",
				"[result_object, id]",
				"{}",
				"{events={keys_arr=[[@5,28:35='keys_arr',<455>,1:28]], active=[[@14,85:90='active',<455>,1:85]], id=[[@1,7:8='id',<455>,1:7]], values_arr=[[@7,38:47='values_arr',<455>,1:38]]}}",
				"{query0={result_object=[[@10,53:65='result_object',<455>,1:53]], id=[[@1,7:8='id',<455>,1:7]]}}",
				"{def_query0={query_dictionary={result_object=[[@10,53:65='result_object',<455>,1:53]], id=[[@1,7:8='id',<455>,1:7]]}, table_dictionary={events={keys_arr=[[@5,28:35='keys_arr',<455>,1:28]], active=[[@14,85:90='active',<455>,1:85]], id=[[@1,7:8='id',<455>,1:7]], values_arr=[[@7,38:47='values_arr',<455>,1:38]]}}, filters=[{name=active, table_ref=events}], interface={result_object=[{name=keys_arr, table_ref=events}, {name=values_arr, table_ref=events}], id=[{name=id, table_ref=events}]}}}");
	}

	@Test
	public void arM20_arraysToObjectUnresolvedArgument1V0Test() {
		final String query = "SELECT arrays_to_object(missing_arr, NULL) AS result_object";
		final SqlParseEventWalker extractor = runObjectConversionQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'arrays_to_object' at (l:1 c:7) is recognized as Snowflake-specific grammar.",
						1, 7, "SqlASTWalkerHelper", null, null,
						true, "ast-walk", null, null),
				new ParseDiagnostic(ParseDiagnostic.Severity.ERROR,
						"UNRESOLVED_UNQUALIFIED_COLUMNS",
						"Unresolved unqualified column reference(s): [missing_arr [(l:1 c:24)]]",
						1, 24, "SqlASTWalkerHelper", null, "missing_arr",
						false, "ast-walk", null, null));
		assertOutputs(
				extractor,
				"{SQL={select={1={function={function_name=arrays_to_object, parameters={1={column={name=missing_arr, table_ref=null}}, 2={null_literal=null}}}, alias=result_object}}}}",
				"[result_object]",
				"{}",
				"{}",
				"{query0={result_object=[[@8,46:58='result_object',<455>,1:46]]}}",
				"{def_query0={query_dictionary={result_object=[[@8,46:58='result_object',<455>,1:46]]}, interface={result_object=[{name=missing_arr, table_ref=null}]}}}");
	}

	@Test
	public void arM20_arraysToObjectUnresolvedArgument2V0Test() {
		final String query = "SELECT arrays_to_object(NULL, missing_arr) AS result_object";
		final SqlParseEventWalker extractor = runObjectConversionQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'arrays_to_object' at (l:1 c:7) is recognized as Snowflake-specific grammar.",
						1, 7, "SqlASTWalkerHelper", null, null,
						true, "ast-walk", null, null),
				new ParseDiagnostic(ParseDiagnostic.Severity.ERROR,
						"UNRESOLVED_UNQUALIFIED_COLUMNS",
						"Unresolved unqualified column reference(s): [missing_arr [(l:1 c:30)]]",
						1, 30, "SqlASTWalkerHelper", null, "missing_arr",
						false, "ast-walk", null, null));
		assertOutputs(
				extractor,
				"{SQL={select={1={function={function_name=arrays_to_object, parameters={1={null_literal=null}, 2={column={name=missing_arr, table_ref=null}}}}, alias=result_object}}}}",
				"[result_object]",
				"{}",
				"{}",
				"{query0={result_object=[[@8,46:58='result_object',<455>,1:46]]}}",
				"{def_query0={query_dictionary={result_object=[[@8,46:58='result_object',<455>,1:46]]}, interface={result_object=[{name=missing_arr, table_ref=null}]}}}");
	}

	@Test
	public void arM21_arraysToObjectAmbiguousArgument1V0Test() {
		final String query = "SELECT arrays_to_object(shared_arr, a.values_arr) AS result_object FROM events a JOIN archived_events b ON a.id = b.id";
		final SqlParseEventWalker extractor = runObjectConversionQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'arrays_to_object' at (l:1 c:7) is recognized as Snowflake-specific grammar.",
						1, 7, "SqlASTWalkerHelper", null, null,
						true, "ast-walk", null, null),
				new ParseDiagnostic(ParseDiagnostic.Severity.SEVERE_WARNING,
						"AMBIGUOUS_COLUMN_REFERENCE",
						"Ambiguous column reference 'shared_arr' at (l:1 c:24). Possible sources: [archived_events, events]",
						1, 24, "SqlASTWalkerHelper", null, "shared_arr",
						true, "ast-walk", null, null),
				new ParseDiagnostic(ParseDiagnostic.Severity.ERROR,
						"UNRESOLVED_UNQUALIFIED_COLUMNS",
						"Unresolved unqualified column reference(s): [shared_arr [(l:1 c:24)]]",
						1, 24, "SqlASTWalkerHelper", null, "shared_arr",
						false, "ast-walk", null, null));
		assertOutputs(
				extractor,
				"{SQL={select={1={function={function_name=arrays_to_object, parameters={1={column={name=shared_arr, table_ref=null}}, 2={column={name=values_arr, table_ref=a}}}}, alias=result_object}}, from={join={1={table={alias=a, table=events}}, 2={join=JOIN, on={condition={left={column={name=id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=archived_events}}}}}}",
				"[result_object]",
				"{}",
				"{archived_events={id=[[@22,114:114='b',<455>,1:114]]}, events={id=[[@18,107:107='a',<455>,1:107]], values_arr=[[@5,36:36='a',<455>,1:36]]}}",
				"{query0={result_object=[[@10,53:65='result_object',<455>,1:53]]}}",
				"{def_query0={query_dictionary={result_object=[[@10,53:65='result_object',<455>,1:53]]}, table_dictionary={archived_events={id=[[@22,114:114='b',<455>,1:114]]}, events={id=[[@18,107:107='a',<455>,1:107]], values_arr=[[@5,36:36='a',<455>,1:36]]}}, filters=[{name=id, table_ref=a}, {name=id, table_ref=b}], interface={result_object=[{name=shared_arr, table_ref=null}, {name=values_arr, table_ref=a}]}, table_alias={a=events, b=archived_events}}}");
	}

	@Test
	public void arM21_arraysToObjectAmbiguousArgument2V0Test() {
		final String query = "SELECT arrays_to_object(a.keys_arr, shared_arr) AS result_object FROM events a JOIN archived_events b ON a.id = b.id";
		final SqlParseEventWalker extractor = runObjectConversionQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'arrays_to_object' at (l:1 c:7) is recognized as Snowflake-specific grammar.",
						1, 7, "SqlASTWalkerHelper", null, null,
						true, "ast-walk", null, null),
				new ParseDiagnostic(ParseDiagnostic.Severity.SEVERE_WARNING,
						"AMBIGUOUS_COLUMN_REFERENCE",
						"Ambiguous column reference 'shared_arr' at (l:1 c:36). Possible sources: [archived_events, events]",
						1, 36, "SqlASTWalkerHelper", null, "shared_arr",
						true, "ast-walk", null, null),
				new ParseDiagnostic(ParseDiagnostic.Severity.ERROR,
						"UNRESOLVED_UNQUALIFIED_COLUMNS",
						"Unresolved unqualified column reference(s): [shared_arr [(l:1 c:36)]]",
						1, 36, "SqlASTWalkerHelper", null, "shared_arr",
						false, "ast-walk", null, null));
		assertOutputs(
				extractor,
				"{SQL={select={1={function={function_name=arrays_to_object, parameters={1={column={name=keys_arr, table_ref=a}}, 2={column={name=shared_arr, table_ref=null}}}}, alias=result_object}}, from={join={1={table={alias=a, table=events}}, 2={join=JOIN, on={condition={left={column={name=id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=archived_events}}}}}}",
				"[result_object]",
				"{}",
				"{archived_events={id=[[@22,112:112='b',<455>,1:112]]}, events={keys_arr=[[@3,24:24='a',<455>,1:24]], id=[[@18,105:105='a',<455>,1:105]]}}",
				"{query0={result_object=[[@10,51:63='result_object',<455>,1:51]]}}",
				"{def_query0={query_dictionary={result_object=[[@10,51:63='result_object',<455>,1:51]]}, table_dictionary={archived_events={id=[[@22,112:112='b',<455>,1:112]]}, events={keys_arr=[[@3,24:24='a',<455>,1:24]], id=[[@18,105:105='a',<455>,1:105]]}}, filters=[{name=id, table_ref=a}, {name=id, table_ref=b}], interface={result_object=[{name=keys_arr, table_ref=a}, {name=shared_arr, table_ref=null}]}, table_alias={a=events, b=archived_events}}}");
	}

	@Test
	public void arM22_arraysToObjectMissingAliasArgument1V0Test() {
		final String query = "SELECT a.id, arrays_to_object(z.tags, a.values_arr) AS result_object FROM events a WHERE a.active = 1";
		final SqlParseEventWalker extractor = runObjectConversionQuery(query);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'arrays_to_object' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null,
						true, "ast-walk", null, null),
				new ParseDiagnostic(ParseDiagnostic.Severity.FATAL,
						"QUALIFIED_COLUMN_NOT_FOUND_IN_TABLE",
						"Source Table not found for Column 'tags' at (l:1 c:55). No alias or table called 'z'.",
						1, 55, "SqlASTWalkerHelper", null, "tags",
						false, "ast-walk", null, null));
		assertOutputs(
				extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={function_name=arrays_to_object, parameters={1={column={name=tags, table_ref=z}}, 2={column={name=values_arr, table_ref=a}}}}, alias=result_object}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_object, id]",
				"{}",
				"{events={active=[[@21,89:89='a',<455>,1:89]], id=[[@1,7:7='a',<455>,1:7]], values_arr=[[@11,38:38='a',<455>,1:38]]}}",
				"{query0={result_object=[[@16,55:67='result_object',<455>,1:55]], id=[[@3,9:10='id',<455>,1:9]]}}",
				"{def_query0={query_dictionary={result_object=[[@16,55:67='result_object',<455>,1:55]], id=[[@3,9:10='id',<455>,1:9]]}, table_dictionary={events={active=[[@21,89:89='a',<455>,1:89]], id=[[@1,7:7='a',<455>,1:7]], values_arr=[[@11,38:38='a',<455>,1:38]]}}, filters=[{name=active, table_ref=a}], interface={result_object=[{name=tags, table_ref=z}, {name=values_arr, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arM22_arraysToObjectMissingAliasArgument2V0Test() {
		final String query = "SELECT a.id, arrays_to_object(a.keys_arr, z.tags) AS result_object FROM events a WHERE a.active = 1";
		final SqlParseEventWalker extractor = runObjectConversionQuery(query);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'arrays_to_object' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null,
						true, "ast-walk", null, null),
				new ParseDiagnostic(ParseDiagnostic.Severity.FATAL,
						"QUALIFIED_COLUMN_NOT_FOUND_IN_TABLE",
						"Source Table not found for Column 'tags' at (l:1 c:53). No alias or table called 'z'.",
						1, 53, "SqlASTWalkerHelper", null, "tags",
						false, "ast-walk", null, null));
		assertOutputs(
				extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={function_name=arrays_to_object, parameters={1={column={name=keys_arr, table_ref=a}}, 2={column={name=tags, table_ref=z}}}}, alias=result_object}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_object, id]",
				"{}",
				"{events={keys_arr=[[@7,30:30='a',<455>,1:30]], active=[[@21,87:87='a',<455>,1:87]], id=[[@1,7:7='a',<455>,1:7]]}}",
				"{query0={result_object=[[@16,53:65='result_object',<455>,1:53]], id=[[@3,9:10='id',<455>,1:9]]}}",
				"{def_query0={query_dictionary={result_object=[[@16,53:65='result_object',<455>,1:53]], id=[[@3,9:10='id',<455>,1:9]]}, table_dictionary={events={keys_arr=[[@7,30:30='a',<455>,1:30]], active=[[@21,87:87='a',<455>,1:87]], id=[[@1,7:7='a',<455>,1:7]]}}, filters=[{name=active, table_ref=a}], interface={result_object=[{name=keys_arr, table_ref=a}, {name=tags, table_ref=z}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arM22_arraysToObjectHiddenAliasArgument1V0Test() {
		final String query = "SELECT d.id, arrays_to_object(a.tags, d.values_arr) AS result_object FROM (SELECT a.id, a.keys_arr, a.values_arr FROM events a) d WHERE d.id > 0";
		final SqlParseEventWalker extractor = runObjectConversionQuery(query);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'arrays_to_object' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null,
						true, "ast-walk", null, null),
				new ParseDiagnostic(ParseDiagnostic.Severity.FATAL,
						"QUALIFIED_COLUMN_NOT_FOUND_IN_TABLE",
						"Source Table not found for Column 'tags' at (l:1 c:55). No alias or table called 'a'.",
						1, 55, "SqlASTWalkerHelper", null, "tags",
						false, "ast-walk", null, null));
		assertOutputs(
				extractor,
				"{SQL={select={1={column={name=id, table_ref=d}}, 2={function={function_name=arrays_to_object, parameters={1={column={name=tags, table_ref=a}}, 2={column={name=values_arr, table_ref=d}}}}, alias=result_object}}, from={table={alias=d, query={select={1={column={name=id, table_ref=a}}, 2={column={name=keys_arr, table_ref=a}}, 3={column={name=values_arr, table_ref=a}}}, from={table={alias=a, table=events}}}}}, where={condition={left={column={name=id, table_ref=d}}, right={literal=0}, operator=>}}}}",
				"[result_object, id]",
				"{}",
				"{events={keys_arr=[[@24,88:88='a',<455>,1:88]], id=[[@20,82:82='a',<455>,1:82]], values_arr=[[@28,100:100='a',<455>,1:100]]}}",
				"{query0={keys_arr=[[@26,90:97='keys_arr',<455>,1:90]], values_arr=[[@30,102:111='values_arr',<455>,1:102], [@11,38:38='d',<455>,1:38]], id=[[@22,84:85='id',<455>,1:84], [@1,7:7='d',<455>,1:7], [@37,136:136='d',<455>,1:136]]}, query1={result_object=[[@16,55:67='result_object',<455>,1:55]], id=[[@3,9:10='id',<455>,1:9]]}}",
				"{def_query1={query_dictionary={result_object=[[@16,55:67='result_object',<455>,1:55]], id=[[@3,9:10='id',<455>,1:9]]}, table_dictionary={}, def_query0={query_dictionary={keys_arr=[[@26,90:97='keys_arr',<455>,1:90]], id=[[@22,84:85='id',<455>,1:84], [@1,7:7='d',<455>,1:7], [@37,136:136='d',<455>,1:136]], values_arr=[[@30,102:111='values_arr',<455>,1:102], [@11,38:38='d',<455>,1:38]]}, table_dictionary={events={keys_arr=[[@24,88:88='a',<455>,1:88]], id=[[@20,82:82='a',<455>,1:82]], values_arr=[[@28,100:100='a',<455>,1:100]]}}, interface={keys_arr=[{name=keys_arr, table_ref=a}], id=[{name=id, table_ref=a}], values_arr=[{name=values_arr, table_ref=a}]}, table_alias={a=events}}, filters=[{name=id, table_ref=d}], interface={result_object=[{name=tags, table_ref=a}, {name=values_arr, table_ref=d}], id=[{name=id, table_ref=d}]}, table_alias={d=query0}}}");
	}

	@Test
	public void arM22_arraysToObjectHiddenAliasArgument2V0Test() {
		final String query = "SELECT d.id, arrays_to_object(d.keys_arr, a.tags) AS result_object FROM (SELECT a.id, a.keys_arr, a.values_arr FROM events a) d WHERE d.id > 0";
		final SqlParseEventWalker extractor = runObjectConversionQuery(query);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'arrays_to_object' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null,
						true, "ast-walk", null, null),
				new ParseDiagnostic(ParseDiagnostic.Severity.FATAL,
						"QUALIFIED_COLUMN_NOT_FOUND_IN_TABLE",
						"Source Table not found for Column 'tags' at (l:1 c:53). No alias or table called 'a'.",
						1, 53, "SqlASTWalkerHelper", null, "tags",
						false, "ast-walk", null, null));
		assertOutputs(
				extractor,
				"{SQL={select={1={column={name=id, table_ref=d}}, 2={function={function_name=arrays_to_object, parameters={1={column={name=keys_arr, table_ref=d}}, 2={column={name=tags, table_ref=a}}}}, alias=result_object}}, from={table={alias=d, query={select={1={column={name=id, table_ref=a}}, 2={column={name=keys_arr, table_ref=a}}, 3={column={name=values_arr, table_ref=a}}}, from={table={alias=a, table=events}}}}}, where={condition={left={column={name=id, table_ref=d}}, right={literal=0}, operator=>}}}}",
				"[result_object, id]",
				"{}",
				"{events={keys_arr=[[@24,86:86='a',<455>,1:86]], id=[[@20,80:80='a',<455>,1:80]], values_arr=[[@28,98:98='a',<455>,1:98]]}}",
				"{query0={keys_arr=[[@26,88:95='keys_arr',<455>,1:88], [@7,30:30='d',<455>,1:30]], values_arr=[[@30,100:109='values_arr',<455>,1:100]], id=[[@22,82:83='id',<455>,1:82], [@1,7:7='d',<455>,1:7], [@37,134:134='d',<455>,1:134]]}, query1={result_object=[[@16,53:65='result_object',<455>,1:53]], id=[[@3,9:10='id',<455>,1:9]]}}",
				"{def_query1={query_dictionary={result_object=[[@16,53:65='result_object',<455>,1:53]], id=[[@3,9:10='id',<455>,1:9]]}, table_dictionary={}, def_query0={query_dictionary={keys_arr=[[@26,88:95='keys_arr',<455>,1:88], [@7,30:30='d',<455>,1:30]], id=[[@22,82:83='id',<455>,1:82], [@1,7:7='d',<455>,1:7], [@37,134:134='d',<455>,1:134]], values_arr=[[@30,100:109='values_arr',<455>,1:100]]}, table_dictionary={events={keys_arr=[[@24,86:86='a',<455>,1:86]], id=[[@20,80:80='a',<455>,1:80]], values_arr=[[@28,98:98='a',<455>,1:98]]}}, interface={keys_arr=[{name=keys_arr, table_ref=a}], id=[{name=id, table_ref=a}], values_arr=[{name=values_arr, table_ref=a}]}, table_alias={a=events}}, filters=[{name=id, table_ref=d}], interface={result_object=[{name=keys_arr, table_ref=d}, {name=tags, table_ref=a}], id=[{name=id, table_ref=d}]}, table_alias={d=query0}}}");
	}

	@Test
	public void arC40_arraysToObjectPredicandVariableArgument1V0Test() {
		final String query = "SELECT a.id, arrays_to_object(<arg_expr>, a.values_arr) AS result_object FROM events a WHERE a.active = 1";
		final SqlParseEventWalker extractor = runObjectConversionQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'arrays_to_object' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null,
						true, "ast-walk", null, null));
		assertOutputs(
				extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={function_name=arrays_to_object, parameters={1={substitution={name=<arg_expr>, type=predicand}}, 2={column={name=values_arr, table_ref=a}}}}, alias=result_object}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_object, id]",
				"{<arg_expr>=predicand}",
				"{events={active=[[@19,93:93='a',<455>,1:93]], id=[[@1,7:7='a',<455>,1:7]], values_arr=[[@9,42:42='a',<455>,1:42]]}}",
				"{query0={result_object=[[@14,59:71='result_object',<455>,1:59]], id=[[@3,9:10='id',<455>,1:9]]}}",
				"{def_query0={query_dictionary={result_object=[[@14,59:71='result_object',<455>,1:59]], id=[[@3,9:10='id',<455>,1:9]]}, table_dictionary={events={active=[[@19,93:93='a',<455>,1:93]], id=[[@1,7:7='a',<455>,1:7]], values_arr=[[@9,42:42='a',<455>,1:42]]}}, filters=[{name=active, table_ref=a}], interface={result_object=[{name=<arg_expr>, type=predicand}, {name=values_arr, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC40_arraysToObjectPredicandVariableArgument2V0Test() {
		final String query = "SELECT a.id, arrays_to_object(a.keys_arr, <arg_expr>) AS result_object FROM events a WHERE a.active = 1";
		final SqlParseEventWalker extractor = runObjectConversionQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'arrays_to_object' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null,
						true, "ast-walk", null, null));
		assertOutputs(
				extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={function_name=arrays_to_object, parameters={1={column={name=keys_arr, table_ref=a}}, 2={substitution={name=<arg_expr>, type=predicand}}}}, alias=result_object}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_object, id]",
				"{<arg_expr>=predicand}",
				"{events={keys_arr=[[@7,30:30='a',<455>,1:30]], active=[[@19,91:91='a',<455>,1:91]], id=[[@1,7:7='a',<455>,1:7]]}}",
				"{query0={result_object=[[@14,57:69='result_object',<455>,1:57]], id=[[@3,9:10='id',<455>,1:9]]}}",
				"{def_query0={query_dictionary={result_object=[[@14,57:69='result_object',<455>,1:57]], id=[[@3,9:10='id',<455>,1:9]]}, table_dictionary={events={keys_arr=[[@7,30:30='a',<455>,1:30]], active=[[@19,91:91='a',<455>,1:91]], id=[[@1,7:7='a',<455>,1:7]]}}, filters=[{name=active, table_ref=a}], interface={result_object=[{name=keys_arr, table_ref=a}, {name=<arg_expr>, type=predicand}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC40_arraysToObjectQualifiedColumnVariableArgument1V0Test() {
		final String query = "SELECT a.id, arrays_to_object(a.<arg_col>, a.values_arr) AS result_object FROM events a WHERE a.active = 1";
		final SqlParseEventWalker extractor = runObjectConversionQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'arrays_to_object' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null,
						true, "ast-walk", null, null));
		assertOutputs(
				extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={function_name=arrays_to_object, parameters={1={column={substitution={name=<arg_col>, type=column}, table_ref=a}}, 2={column={name=values_arr, table_ref=a}}}}, alias=result_object}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_object, id]",
				"{<arg_col>=column}",
				"{events={active=[[@21,94:94='a',<455>,1:94]], id=[[@1,7:7='a',<455>,1:7]], <arg_col>=[[@7,30:30='a',<455>,1:30]], values_arr=[[@11,43:43='a',<455>,1:43]]}}",
				"{query0={result_object=[[@16,60:72='result_object',<455>,1:60]], id=[[@3,9:10='id',<455>,1:9]]}}",
				"{def_query0={query_dictionary={result_object=[[@16,60:72='result_object',<455>,1:60]], id=[[@3,9:10='id',<455>,1:9]]}, table_dictionary={events={active=[[@21,94:94='a',<455>,1:94]], id=[[@1,7:7='a',<455>,1:7]], <arg_col>=[[@7,30:30='a',<455>,1:30]], values_arr=[[@11,43:43='a',<455>,1:43]]}}, filters=[{name=active, table_ref=a}], interface={result_object=[{substitution={name=<arg_col>, type=column}, table_ref=a}, {name=values_arr, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC40_arraysToObjectQualifiedColumnVariableArgument2V0Test() {
		final String query = "SELECT a.id, arrays_to_object(a.keys_arr, a.<arg_col>) AS result_object FROM events a WHERE a.active = 1";
		final SqlParseEventWalker extractor = runObjectConversionQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'arrays_to_object' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null,
						true, "ast-walk", null, null));
		assertOutputs(
				extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={function_name=arrays_to_object, parameters={1={column={name=keys_arr, table_ref=a}}, 2={column={substitution={name=<arg_col>, type=column}, table_ref=a}}}}, alias=result_object}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_object, id]",
				"{<arg_col>=column}",
				"{events={keys_arr=[[@7,30:30='a',<455>,1:30]], active=[[@21,92:92='a',<455>,1:92]], id=[[@1,7:7='a',<455>,1:7]], <arg_col>=[[@11,42:42='a',<455>,1:42]]}}",
				"{query0={result_object=[[@16,58:70='result_object',<455>,1:58]], id=[[@3,9:10='id',<455>,1:9]]}}",
				"{def_query0={query_dictionary={result_object=[[@16,58:70='result_object',<455>,1:58]], id=[[@3,9:10='id',<455>,1:9]]}, table_dictionary={events={keys_arr=[[@7,30:30='a',<455>,1:30]], active=[[@21,92:92='a',<455>,1:92]], id=[[@1,7:7='a',<455>,1:7]], <arg_col>=[[@11,42:42='a',<455>,1:42]]}}, filters=[{name=active, table_ref=a}], interface={result_object=[{name=keys_arr, table_ref=a}, {substitution={name=<arg_col>, type=column}, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC40_arraysToObjectNullArgument1V0Test() {
		final String query = "SELECT a.id, arrays_to_object(NULL, a.values_arr) AS result_object FROM events a WHERE a.active = 1";
		final SqlParseEventWalker extractor = runObjectConversionQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'arrays_to_object' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null,
						true, "ast-walk", null, null));
		assertOutputs(
				extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={function_name=arrays_to_object, parameters={1={null_literal=null}, 2={column={name=values_arr, table_ref=a}}}}, alias=result_object}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_object, id]",
				"{}",
				"{events={active=[[@19,87:87='a',<455>,1:87]], id=[[@1,7:7='a',<455>,1:7]], values_arr=[[@9,36:36='a',<455>,1:36]]}}",
				"{query0={result_object=[[@14,53:65='result_object',<455>,1:53]], id=[[@3,9:10='id',<455>,1:9]]}}",
				"{def_query0={query_dictionary={result_object=[[@14,53:65='result_object',<455>,1:53]], id=[[@3,9:10='id',<455>,1:9]]}, table_dictionary={events={active=[[@19,87:87='a',<455>,1:87]], id=[[@1,7:7='a',<455>,1:7]], values_arr=[[@9,36:36='a',<455>,1:36]]}}, filters=[{name=active, table_ref=a}], interface={result_object=[{name=values_arr, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC40_arraysToObjectNullArgument2V0Test() {
		final String query = "SELECT a.id, arrays_to_object(a.keys_arr, NULL) AS result_object FROM events a WHERE a.active = 1";
		final SqlParseEventWalker extractor = runObjectConversionQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'arrays_to_object' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null,
						true, "ast-walk", null, null));
		assertOutputs(
				extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={function_name=arrays_to_object, parameters={1={column={name=keys_arr, table_ref=a}}, 2={null_literal=null}}}, alias=result_object}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_object, id]",
				"{}",
				"{events={keys_arr=[[@7,30:30='a',<455>,1:30]], active=[[@19,85:85='a',<455>,1:85]], id=[[@1,7:7='a',<455>,1:7]]}}",
				"{query0={result_object=[[@14,51:63='result_object',<455>,1:51]], id=[[@3,9:10='id',<455>,1:9]]}}",
				"{def_query0={query_dictionary={result_object=[[@14,51:63='result_object',<455>,1:51]], id=[[@3,9:10='id',<455>,1:9]]}, table_dictionary={events={keys_arr=[[@7,30:30='a',<455>,1:30]], active=[[@19,85:85='a',<455>,1:85]], id=[[@1,7:7='a',<455>,1:7]]}}, filters=[{name=active, table_ref=a}], interface={result_object=[{name=keys_arr, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC40_arraysToObjectCombinedColumnVariablesV0Test() {
		final String query = "SELECT a.id, arrays_to_object(a.<keys_col>, a.<values_col>) AS result_object FROM events a WHERE a.active = 1";
		final SqlParseEventWalker extractor = runObjectConversionQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'arrays_to_object' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null,
						true, "ast-walk", null, null));
		assertOutputs(
				extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={function_name=arrays_to_object, parameters={1={column={substitution={name=<keys_col>, type=column}, table_ref=a}}, 2={column={substitution={name=<values_col>, type=column}, table_ref=a}}}}, alias=result_object}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_object, id]",
				"{<values_col>=column, <keys_col>=column}",
				"{events={active=[[@21,97:97='a',<455>,1:97]], <values_col>=[[@11,44:44='a',<455>,1:44]], id=[[@1,7:7='a',<455>,1:7]], <keys_col>=[[@7,30:30='a',<455>,1:30]]}}",
				"{query0={result_object=[[@16,63:75='result_object',<455>,1:63]], id=[[@3,9:10='id',<455>,1:9]]}}",
				"{def_query0={query_dictionary={result_object=[[@16,63:75='result_object',<455>,1:63]], id=[[@3,9:10='id',<455>,1:9]]}, table_dictionary={events={active=[[@21,97:97='a',<455>,1:97]], <values_col>=[[@11,44:44='a',<455>,1:44]], id=[[@1,7:7='a',<455>,1:7]], <keys_col>=[[@7,30:30='a',<455>,1:30]]}}, filters=[{name=active, table_ref=a}], interface={result_object=[{substitution={name=<keys_col>, type=column}, table_ref=a}, {substitution={name=<values_col>, type=column}, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arM23_arraysToObjectSchemaQualifiedRoutineControlV0Test() {
		final String query = "SELECT a.id, custom.arrays_to_object(a.keys_arr, a.values_arr) AS result_object FROM events a WHERE a.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(
				extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={schema=custom, parameters={1={column={name=keys_arr, table_ref=a}}, 2={column={name=values_arr, table_ref=a}}}, function_name=arrays_to_object}, alias=result_object}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_object, id]",
				"{}",
				"{events={keys_arr=[[@9,37:37='a',<455>,1:37]], active=[[@23,100:100='a',<455>,1:100]], id=[[@1,7:7='a',<455>,1:7]], values_arr=[[@13,49:49='a',<455>,1:49]]}}",
				"{query0={result_object=[[@18,66:78='result_object',<455>,1:66]], id=[[@3,9:10='id',<455>,1:9]]}}",
				"{def_query0={query_dictionary={result_object=[[@18,66:78='result_object',<455>,1:66]], id=[[@3,9:10='id',<455>,1:9]]}, table_dictionary={events={keys_arr=[[@9,37:37='a',<455>,1:37]], active=[[@23,100:100='a',<455>,1:100]], id=[[@1,7:7='a',<455>,1:7]], values_arr=[[@13,49:49='a',<455>,1:49]]}}, filters=[{name=active, table_ref=a}], interface={result_object=[{name=keys_arr, table_ref=a}, {name=values_arr, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arM23_arraysToObjectNonreservedColumnNameV0Test() {
		final String query = "SELECT a.arrays_to_object AS result_object FROM events a WHERE a.id > 0";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(
				extractor,
				"{SQL={select={1={column={name=arrays_to_object, table_ref=a}, alias=result_object}}, from={table={alias=a, table=events}}, where={condition={left={column={name=id, table_ref=a}}, right={literal=0}, operator=>}}}}",
				"[result_object]",
				"{}",
				"{events={arrays_to_object=[[@1,7:7='a',<455>,1:7]], id=[[@10,63:63='a',<455>,1:63]]}}",
				"{query0={result_object=[[@5,29:41='result_object',<455>,1:29]]}}",
				"{def_query0={query_dictionary={result_object=[[@5,29:41='result_object',<455>,1:29]]}, table_dictionary={events={arrays_to_object=[[@1,7:7='a',<455>,1:7]], id=[[@10,63:63='a',<455>,1:63]]}}, filters=[{name=id, table_ref=a}], interface={result_object=[{name=arrays_to_object, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arM24_arraysToObjectMixedDialectControlV0Test() {
		final String query = "SELECT arrays_to_object(a.keys_arr, a.values_arr) AS result_object, extract(isodow from a.created_at) AS weekday FROM events a";
		final SqlParseEventWalker extractor = runObjectConversionQuery(query);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'arrays_to_object' at (l:1 c:7) is recognized as Snowflake-specific grammar.",
						1, 7, "SqlASTWalkerHelper", null, null,
						true, "ast-walk", null, null),
				new ParseDiagnostic(ParseDiagnostic.Severity.FATAL,
						"STATEMENT_MIXED_DIALECT_GRAMMAR",
						"Statement mixes Snowflake-only and PostgreSQL-only grammar at (l:1 c:68); unlikely to run on either engine.",
						1, 68, "SqlASTWalkerHelper", null, null,
						false, "ast-walk", null, null));
		assertOutputs(
				extractor,
				"{SQL={select={1={function={function_name=arrays_to_object, parameters={1={column={name=keys_arr, table_ref=a}}, 2={column={name=values_arr, table_ref=a}}}}, alias=result_object}, 2={alias=weekday, datetime_function={function_name=extract, part_form=KEYWORD, part=isodow, source={column={name=created_at, table_ref=a}}}}}, from={table={alias=a, table=events}}}}",
				"[result_object, weekday]",
				"{}",
				"{events={keys_arr=[[@3,24:24='a',<455>,1:24]], created_at=[[@18,88:88='a',<455>,1:88]], values_arr=[[@7,36:36='a',<455>,1:36]]}}",
				"{query0={weekday=[[@23,105:111='weekday',<455>,1:105]], result_object=[[@12,53:65='result_object',<455>,1:53]]}}",
				"{def_query0={query_dictionary={result_object=[[@12,53:65='result_object',<455>,1:53]], weekday=[[@23,105:111='weekday',<455>,1:105]]}, table_dictionary={events={keys_arr=[[@3,24:24='a',<455>,1:24]], created_at=[[@18,88:88='a',<455>,1:88]], values_arr=[[@7,36:36='a',<455>,1:36]]}}, interface={result_object=[{name=keys_arr, table_ref=a}, {name=values_arr, table_ref=a}], weekday=[{name=created_at, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC40_arraysToObjectUppercaseNameV0Test() {
		final String query = "SELECT a.id, ARRAYS_TO_OBJECT(a.keys_arr, a.values_arr) AS result_object FROM events a WHERE a.active = 1";
		final SqlParseEventWalker extractor = runObjectConversionQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'arrays_to_object' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null,
						true, "ast-walk", null, null));
		assertOutputs(
				extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={function_name=ARRAYS_TO_OBJECT, parameters={1={column={name=keys_arr, table_ref=a}}, 2={column={name=values_arr, table_ref=a}}}}, alias=result_object}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_object, id]",
				"{}",
				"{events={keys_arr=[[@7,30:30='a',<455>,1:30]], active=[[@21,93:93='a',<455>,1:93]], id=[[@1,7:7='a',<455>,1:7]], values_arr=[[@11,42:42='a',<455>,1:42]]}}",
				"{query0={result_object=[[@16,59:71='result_object',<455>,1:59]], id=[[@3,9:10='id',<455>,1:9]]}}",
				"{def_query0={query_dictionary={result_object=[[@16,59:71='result_object',<455>,1:59]], id=[[@3,9:10='id',<455>,1:9]]}, table_dictionary={events={keys_arr=[[@7,30:30='a',<455>,1:30]], active=[[@21,93:93='a',<455>,1:93]], id=[[@1,7:7='a',<455>,1:7]], values_arr=[[@11,42:42='a',<455>,1:42]]}}, filters=[{name=active, table_ref=a}], interface={result_object=[{name=keys_arr, table_ref=a}, {name=values_arr, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC40_arraysToObjectLiteralArraysV0Test() {
		final String query = "SELECT a.id, arrays_to_object(['first', 'second'], [a.value_col, 2]) AS result_object FROM events a WHERE a.active = 1";
		final SqlParseEventWalker extractor = runObjectConversionQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"array literal at (l:1 c:30) is recognized as Snowflake-specific grammar.",
						1, 30, "SqlASTWalkerHelper", null, null,
						true, "ast-walk", null, null));
		assertOutputs(
				extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={function_name=arrays_to_object, parameters={1={function={function_name=array_construct, parameters={1={literal='first'}, 2={literal='second'}}}}, 2={function={function_name=array_construct, parameters={1={column={name=value_col, table_ref=a}}, 2={literal=2}}}}}}, alias=result_object}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_object, id]",
				"{}",
				"{events={value_col=[[@14,52:52='a',<455>,1:52]], active=[[@27,106:106='a',<455>,1:106]], id=[[@1,7:7='a',<455>,1:7]]}}",
				"{query0={result_object=[[@22,72:84='result_object',<455>,1:72]], id=[[@3,9:10='id',<455>,1:9]]}}",
				"{def_query0={query_dictionary={result_object=[[@22,72:84='result_object',<455>,1:72]], id=[[@3,9:10='id',<455>,1:9]]}, table_dictionary={events={value_col=[[@14,52:52='a',<455>,1:52]], active=[[@27,106:106='a',<455>,1:106]], id=[[@1,7:7='a',<455>,1:7]]}}, filters=[{name=active, table_ref=a}], interface={result_object=[{name=value_col, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC40_arraysToObjectNullKeyElementV0Test() {
		final String query = "SELECT a.id, arrays_to_object(['first', NULL], [a.value_col, 2]) AS result_object FROM events a WHERE a.active = 1";
		final SqlParseEventWalker extractor = runObjectConversionQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"array literal at (l:1 c:30) is recognized as Snowflake-specific grammar.",
						1, 30, "SqlASTWalkerHelper", null, null,
						true, "ast-walk", null, null));
		assertOutputs(
				extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={function_name=arrays_to_object, parameters={1={function={function_name=array_construct, parameters={1={literal='first'}, 2={null_literal=null}}}}, 2={function={function_name=array_construct, parameters={1={column={name=value_col, table_ref=a}}, 2={literal=2}}}}}}, alias=result_object}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_object, id]",
				"{}",
				"{events={value_col=[[@14,48:48='a',<455>,1:48]], active=[[@27,102:102='a',<455>,1:102]], id=[[@1,7:7='a',<455>,1:7]]}}",
				"{query0={result_object=[[@22,68:80='result_object',<455>,1:68]], id=[[@3,9:10='id',<455>,1:9]]}}",
				"{def_query0={query_dictionary={result_object=[[@22,68:80='result_object',<455>,1:68]], id=[[@3,9:10='id',<455>,1:9]]}, table_dictionary={events={value_col=[[@14,48:48='a',<455>,1:48]], active=[[@27,102:102='a',<455>,1:102]], id=[[@1,7:7='a',<455>,1:7]]}}, filters=[{name=active, table_ref=a}], interface={result_object=[{name=value_col, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC40_arraysToObjectNullValueElementV0Test() {
		final String query = "SELECT a.id, arrays_to_object(['first', 'second'], [NULL, a.value_col]) AS result_object FROM events a WHERE a.active = 1";
		final SqlParseEventWalker extractor = runObjectConversionQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"array literal at (l:1 c:30) is recognized as Snowflake-specific grammar.",
						1, 30, "SqlASTWalkerHelper", null, null,
						true, "ast-walk", null, null));
		assertOutputs(
				extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={function_name=arrays_to_object, parameters={1={function={function_name=array_construct, parameters={1={literal='first'}, 2={literal='second'}}}}, 2={function={function_name=array_construct, parameters={1={null_literal=null}, 2={column={name=value_col, table_ref=a}}}}}}}, alias=result_object}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_object, id]",
				"{}",
				"{events={value_col=[[@16,58:58='a',<455>,1:58]], active=[[@27,109:109='a',<455>,1:109]], id=[[@1,7:7='a',<455>,1:7]]}}",
				"{query0={result_object=[[@22,75:87='result_object',<455>,1:75]], id=[[@3,9:10='id',<455>,1:9]]}}",
				"{def_query0={query_dictionary={result_object=[[@22,75:87='result_object',<455>,1:75]], id=[[@3,9:10='id',<455>,1:9]]}, table_dictionary={events={value_col=[[@16,58:58='a',<455>,1:58]], active=[[@27,109:109='a',<455>,1:109]], id=[[@1,7:7='a',<455>,1:7]]}}, filters=[{name=active, table_ref=a}], interface={result_object=[{name=value_col, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arM24_arraysToObjectTrailingCommaSyntaxV0Test() {
		final String query = "SELECT arrays_to_object(a.keys_arr, a.values_arr,) AS result_object FROM events a";
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
						"Line 1:49 - unexpected input: ')' in rule value_expression: SELECT arrays_to_object(a.keys_arr, a.values_arr,) AS result_object FROM events a",
						1, 49, "ParseErrorCollector", "value_expression", ")",
						false, "parse.strategy", "NoViableAltException", java.util.Map.of("contextSnippet", "SELECT arrays_to_object(a.keys_arr, a.values_arr,) AS result_object FROM events a", "syntaxClass", "GRAMMAR_GAP", "parserRules", "value_expression,select_item,select_list")),
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"RECOVER",
						"Line 1:49 - Syntax error, attempting recovery",
						1, 49, "ParseErrorCollector", null, ")",
						true, "parse.strategy", "NoViableAltException", null),
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"RECOVER_INLINE",
						"Line 1:23 - Invalid syntax near '('",
						1, 23, "ParseErrorCollector", null, "(",
						true, "parse.strategy", null, null)), diagnostics);
	}

	@Test
	public void arM24_arraysToObjectMissingCommaSyntaxV0Test() {
		final String query = "SELECT arrays_to_object(a.keys_arr a.values_arr) AS result_object FROM events a";
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
						"Line 1:35 - unexpected input: 'a' in rule value_expression: SELECT arrays_to_object(a.keys_arr a.values_arr) AS result_object FROM events a",
						1, 35, "ParseErrorCollector", "value_expression", "a",
						false, "parse.strategy", "NoViableAltException", java.util.Map.of("contextSnippet", "SELECT arrays_to_object(a.keys_arr a.values_arr) AS result_object FROM events a", "syntaxClass", "GRAMMAR_GAP", "parserRules", "value_expression,select_item,select_list")),
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"RECOVER",
						"Line 1:35 - Syntax error, attempting recovery",
						1, 35, "ParseErrorCollector", null, "a",
						true, "parse.strategy", "NoViableAltException", null),
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"RECOVER_INLINE",
						"Line 1:23 - Invalid syntax near '('",
						1, 23, "ParseErrorCollector", null, "(",
						true, "parse.strategy", null, null)), diagnostics);
	}
}
