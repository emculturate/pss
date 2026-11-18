package sql.walker;

import org.junit.Assert;
import org.junit.Test;

import access.Snippet;
import errorhandling.ParseDiagnostic;
import sql.SQLSelectParserParser;

public class SqlEventWalkerSnowflakeArrayTransformTests extends AbstractSqlParseEventWalkerTest {

	private SqlParseEventWalker runQuery(String query) {
		final SQLSelectParserParser parser = parse(query);
		return runParsertest(query, parser);
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
				"{tab1={tags=[[@3,23:23='a',<452>,1:23]], sep=[[@7,31:31='a',<452>,1:31]]}}",
				"{query0={joined_tags=[[@12,41:51='joined_tags',<452>,1:41]]}}",
				"{def_query0={query_dictionary={joined_tags=[[@12,41:51='joined_tags',<452>,1:41]]}, table_dictionary={tab1={tags=[[@3,23:23='a',<452>,1:23]], sep=[[@7,31:31='a',<452>,1:31]]}}, interface={joined_tags=[{name=tags, table_ref=a}, {name=sep, table_ref=a}]}, table_alias={a=tab1}}}");
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
				"{tab1={tags=[[@3,23:23='a',<452>,1:23]]}}",
				"{query0={joined_tags=[[@12,41:51='joined_tags',<452>,1:41]]}}",
				"{def_query0={query_dictionary={joined_tags=[[@12,41:51='joined_tags',<452>,1:41]]}, table_dictionary={tab1={tags=[[@3,23:23='a',<452>,1:23]]}}, interface={joined_tags=[{name=tags, table_ref=a}, {name=sep, table_ref=z}]}, table_alias={a=tab1}}}");
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
				"{tab1={<sep_col>=[[@7,36:36='a',<452>,1:36]], <arr_col>=[[@3,23:23='a',<452>,1:23]]}}",
				"{query0={joined_tags=[[@12,52:62='joined_tags',<452>,1:52]]}}",
				"{def_query0={query_dictionary={joined_tags=[[@12,52:62='joined_tags',<452>,1:52]]}, table_dictionary={tab1={<sep_col>=[[@7,36:36='a',<452>,1:36]], <arr_col>=[[@3,23:23='a',<452>,1:23]]}}, interface={joined_tags=[{substitution={name=<arr_col>, type=column}, table_ref=a}, {substitution={name=<sep_col>, type=column}, table_ref=a}]}, table_alias={a=tab1}}}");
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
				"{tab1={sep=[[@7,31:31='a',<452>,1:31]]}}",
				"{query0={joined_tags=[[@12,41:51='joined_tags',<452>,1:41]]}}",
				"{def_query0={query_dictionary={joined_tags=[[@12,41:51='joined_tags',<452>,1:41]]}, table_dictionary={tab1={sep=[[@7,31:31='a',<452>,1:31]]}}, interface={joined_tags=[{name=tags, table_ref=z}, {name=sep, table_ref=a}]}, table_alias={a=tab1}}}");
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
				"{tab1={id=[[@1,7:7='a',<452>,1:7]], tags=[[@10,46:46='a',<452>,1:46]], sep=[[@14,54:54='a',<452>,1:54]]}}",
				"{query0={id=[[@3,9:10='id',<452>,1:9]]}}",
				"{def_query0={query_dictionary={id=[[@3,9:10='id',<452>,1:9]]}, table_dictionary={tab1={id=[[@1,7:7='a',<452>,1:7]], tags=[[@10,46:46='a',<452>,1:46]], sep=[[@14,54:54='a',<452>,1:54]]}}, filters=[{name=tags, table_ref=a}, {name=sep, table_ref=a}], interface={id=[{name=id, table_ref=a}]}, table_alias={a=tab1}}}");
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
				"{tab1={id=[[@16,76:76='a',<452>,1:76]]}, tab2={id=[[@20,83:83='b',<452>,1:83]]}}",
				"{query0={joined_tags=[[@8,37:47='joined_tags',<452>,1:37]]}}",
				"{def_query0={query_dictionary={joined_tags=[[@8,37:47='joined_tags',<452>,1:37]]}, table_dictionary={tab1={id=[[@16,76:76='a',<452>,1:76]]}, tab2={id=[[@20,83:83='b',<452>,1:83]]}}, filters=[{name=id, table_ref=a}, {name=id, table_ref=b}], interface={joined_tags=[{name=tags, table_ref=null}]}, table_alias={a=tab1, b=tab2}}}");
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
				"{tab1={tags=[[@3,23:23='a',<452>,1:23]]}}",
				"{query0={joined_tags=[[@10,46:56='joined_tags',<452>,1:46]]}}",
				"{def_query0={query_dictionary={joined_tags=[[@10,46:56='joined_tags',<452>,1:46]]}, table_dictionary={tab1={tags=[[@3,23:23='a',<452>,1:23]]}}, interface={joined_tags=[{name=tags, table_ref=a}, {name=<sep_expr>, type=predicand}]}, table_alias={a=tab1}}}");
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
				"{SCRIPT={1={tab1={tags=[[@3,23:23='a',<452>,1:23]], sep=[[@7,31:31='a',<452>,1:31]]}}, 2={tab1={id=[[@18,73:73='a',<452>,1:73]]}}}}",
				"{SCRIPT={1={query0={joined_tags=[[@12,41:51='joined_tags',<452>,1:41]]}}, 2={query0={id=[[@20,75:76='id',<452>,1:75]]}}}}",
				"{SCRIPT={1={def_query0={query_dictionary={joined_tags=[[@12,41:51='joined_tags',<452>,1:41]]}, table_dictionary={tab1={tags=[[@3,23:23='a',<452>,1:23]], sep=[[@7,31:31='a',<452>,1:31]]}}, interface={joined_tags=[{name=tags, table_ref=a}, {name=sep, table_ref=a}]}, table_alias={a=tab1}}}, 2={def_query0={query_dictionary={id=[[@20,75:76='id',<452>,1:75]]}, table_dictionary={tab1={id=[[@18,73:73='a',<452>,1:73]]}}, interface={id=[{name=id, table_ref=a}]}, table_alias={a=tab1}}}}}");
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
				"{tab1={tags=[[@7,44:44='a',<452>,1:44]], sep=[[@11,52:52='a',<452>,1:52]]}}",
				"{query0={joined_tags=[[@16,62:72='joined_tags',<452>,1:62]]}}",
				"{def_create1={def_query0={query_dictionary={joined_tags=[[@16,62:72='joined_tags',<452>,1:62]]}, table_dictionary={tab1={tags=[[@7,44:44='a',<452>,1:44]], sep=[[@11,52:52='a',<452>,1:52]]}}, interface={joined_tags=[{name=tags, table_ref=a}, {name=sep, table_ref=a}]}, table_alias={a=tab1}}}}");
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
				"{tab1={end_idx=[[@11,40:40='a',<452>,1:40]], start_idx=[[@7,27:27='a',<452>,1:27]], tags=[[@3,19:19='a',<452>,1:19]]}}",
				"{query0={sliced_tags=[[@16,54:64='sliced_tags',<452>,1:54]]}}",
				"{def_query0={query_dictionary={sliced_tags=[[@16,54:64='sliced_tags',<452>,1:54]]}, table_dictionary={tab1={end_idx=[[@11,40:40='a',<452>,1:40]], start_idx=[[@7,27:27='a',<452>,1:27]], tags=[[@3,19:19='a',<452>,1:19]]}}, interface={sliced_tags=[{name=tags, table_ref=a}, {name=start_idx, table_ref=a}, {name=end_idx, table_ref=a}]}, table_alias={a=tab1}}}");
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
				"{tab1={start_idx=[[@7,27:27='a',<452>,1:27]], tags=[[@3,19:19='a',<452>,1:19]]}}",
				"{query0={sliced_tags=[[@16,54:64='sliced_tags',<452>,1:54]]}}",
				"{def_query0={query_dictionary={sliced_tags=[[@16,54:64='sliced_tags',<452>,1:54]]}, table_dictionary={tab1={start_idx=[[@7,27:27='a',<452>,1:27]], tags=[[@3,19:19='a',<452>,1:19]]}}, interface={sliced_tags=[{name=tags, table_ref=a}, {name=start_idx, table_ref=a}, {name=end_idx, table_ref=z}]}, table_alias={a=tab1}}}");
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
				"{tab1={<end_col>=[[@11,47:47='a',<452>,1:47]], <start_col>=[[@7,32:32='a',<452>,1:32]], <arr_col>=[[@3,19:19='a',<452>,1:19]]}}",
				"{query0={sliced_tags=[[@16,63:73='sliced_tags',<452>,1:63]]}}",
				"{def_query0={query_dictionary={sliced_tags=[[@16,63:73='sliced_tags',<452>,1:63]]}, table_dictionary={tab1={<end_col>=[[@11,47:47='a',<452>,1:47]], <start_col>=[[@7,32:32='a',<452>,1:32]], <arr_col>=[[@3,19:19='a',<452>,1:19]]}}, interface={sliced_tags=[{substitution={name=<arr_col>, type=column}, table_ref=a}, {substitution={name=<start_col>, type=column}, table_ref=a}, {substitution={name=<end_col>, type=column}, table_ref=a}]}, table_alias={a=tab1}}}");
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
				"{tab1={end_idx=[[@11,40:40='a',<452>,1:40]], start_idx=[[@7,27:27='a',<452>,1:27]]}}",
				"{query0={sliced_tags=[[@16,54:64='sliced_tags',<452>,1:54]]}}",
				"{def_query0={query_dictionary={sliced_tags=[[@16,54:64='sliced_tags',<452>,1:54]]}, table_dictionary={tab1={end_idx=[[@11,40:40='a',<452>,1:40]], start_idx=[[@7,27:27='a',<452>,1:27]]}}, interface={sliced_tags=[{name=tags, table_ref=z}, {name=start_idx, table_ref=a}, {name=end_idx, table_ref=a}]}, table_alias={a=tab1}}}");
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
				"{tab1={end_idx=[[@11,40:40='a',<452>,1:40]], tags=[[@3,19:19='a',<452>,1:19]]}}",
				"{query0={sliced_tags=[[@16,54:64='sliced_tags',<452>,1:54]]}}",
				"{def_query0={query_dictionary={sliced_tags=[[@16,54:64='sliced_tags',<452>,1:54]]}, table_dictionary={tab1={end_idx=[[@11,40:40='a',<452>,1:40]], tags=[[@3,19:19='a',<452>,1:19]]}}, interface={sliced_tags=[{name=tags, table_ref=a}, {name=start_idx, table_ref=z}, {name=end_idx, table_ref=a}]}, table_alias={a=tab1}}}");
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
				"{tab1={id=[[@18,79:79='a',<452>,1:79]]}, tab2={id=[[@22,86:86='b',<452>,1:86]]}}",
				"{query0={sliced_tags=[[@10,40:50='sliced_tags',<452>,1:40]]}}",
				"{def_query0={query_dictionary={sliced_tags=[[@10,40:50='sliced_tags',<452>,1:40]]}, table_dictionary={tab1={id=[[@18,79:79='a',<452>,1:79]]}, tab2={id=[[@22,86:86='b',<452>,1:86]]}}, filters=[{name=id, table_ref=a}, {name=id, table_ref=b}], interface={sliced_tags=[{name=tags, table_ref=null}]}, table_alias={a=tab1, b=tab2}}}");
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
				"{tab1={end_idx=[[@30,93:93='a',<452>,1:93]], start_idx=[[@26,80:80='a',<452>,1:80]], *=[[@7,19:19='*',<291>,1:19]], id=[[@1,7:7='a',<452>,1:7], [@16,48:48='a',<452>,1:48]], tags=[[@22,72:72='a',<452>,1:72]]}}",
				"{query0={c=[[@10,25:25='c',<452>,1:25]], id=[[@3,9:10='id',<452>,1:9]]}}",
				"{def_query0={query_dictionary={c=[[@10,25:25='c',<452>,1:25]], id=[[@3,9:10='id',<452>,1:9]]}, table_dictionary={tab1={end_idx=[[@30,93:93='a',<452>,1:93]], *=[[@7,19:19='*',<291>,1:19]], start_idx=[[@26,80:80='a',<452>,1:80]], id=[[@1,7:7='a',<452>,1:7], [@16,48:48='a',<452>,1:48]], tags=[[@22,72:72='a',<452>,1:72]]}}, grouped_by=[{name=id, table_ref=a}], filters=[{name=tags, table_ref=a}, {name=start_idx, table_ref=a}, {name=end_idx, table_ref=a}], interface={c=[], id=[{name=id, table_ref=a}]}, table_alias={a=tab1}}}");
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
				"{tab1={end_idx=[[@19,66:66='a',<452>,1:66]], start_idx=[[@15,53:53='a',<452>,1:53]], id=[[@1,7:7='a',<452>,1:7]], tags=[[@11,45:45='a',<452>,1:45]]}}",
				"{query0={id=[[@3,9:10='id',<452>,1:9]]}}",
				"{def_query0={query_dictionary={id=[[@3,9:10='id',<452>,1:9]]}, table_dictionary={tab1={end_idx=[[@19,66:66='a',<452>,1:66]], start_idx=[[@15,53:53='a',<452>,1:53]], id=[[@1,7:7='a',<452>,1:7]], tags=[[@11,45:45='a',<452>,1:45]]}}, ordered_by=[{name=tags, table_ref=a}, {name=start_idx, table_ref=a}, {name=end_idx, table_ref=a}], interface={id=[{name=id, table_ref=a}]}, table_alias={a=tab1}}}");
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
				"{tab1={id=[[@1,7:7='a',<452>,1:7], [@12,45:45='a',<452>,1:45]], tags=[[@24,93:93='a',<452>,1:93]], sep=[[@28,101:101='a',<452>,1:101]]}}",
				"{query0={rn=[[@17,54:55='rn',<452>,1:54]], id=[[@3,9:10='id',<452>,1:9]]}}",
				"{def_query0={query_dictionary={id=[[@3,9:10='id',<452>,1:9]], rn=[[@17,54:55='rn',<452>,1:54]]}, table_dictionary={tab1={id=[[@1,7:7='a',<452>,1:7], [@12,45:45='a',<452>,1:45]], tags=[[@24,93:93='a',<452>,1:93]], sep=[[@28,101:101='a',<452>,1:101]]}}, window_partition_by=[{name=id, table_ref=a}], filters=[{name=tags, table_ref=a}, {name=sep, table_ref=a}], interface={id=[{name=id, table_ref=a}], rn=[{name=id, table_ref=a}]}, table_alias={a=tab1}}}");
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
				"{tab1={id=[[@1,7:7='a',<452>,1:7]], tags=[[@11,49:49='a',<452>,1:49]], sep=[[@15,57:57='a',<452>,1:57]]}}",
				"{query0={id=[[@3,9:10='id',<452>,1:9]]}}",
				"{def_query0={query_dictionary={id=[[@3,9:10='id',<452>,1:9]]}, table_dictionary={tab1={id=[[@1,7:7='a',<452>,1:7]], tags=[[@11,49:49='a',<452>,1:49]], sep=[[@15,57:57='a',<452>,1:57]]}}, ordered_by=[{name=tags, table_ref=a}, {name=sep, table_ref=a}], interface={id=[{name=id, table_ref=a}]}, table_alias={a=tab1}}}");
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
				"{tab1={tags=[[@9,57:57='a',<452>,1:57]], sep=[[@13,65:65='a',<452>,1:65]]}, tab_out={joined_tags=[[@4,21:31='joined_tags',<452>,1:21]]}}",
				"{query0={unnamed_0=[[@16,70:70=')',<288>,1:70]]}, insert1={joined_tags=[[@4,21:31='joined_tags',<452>,1:21]]}}",
				"{def_insert1={query_dictionary={joined_tags=[[@4,21:31='joined_tags',<452>,1:21]]}, table_dictionary={tab_out={joined_tags=[[@4,21:31='joined_tags',<452>,1:21]]}}, def_query0={query_dictionary={unnamed_0=[[@16,70:70=')',<288>,1:70]]}, table_dictionary={tab1={tags=[[@9,57:57='a',<452>,1:57]], sep=[[@13,65:65='a',<452>,1:65]]}}, interface={unnamed_0=[{name=tags, table_ref=a}, {name=sep, table_ref=a}]}, table_alias={a=tab1}}, interface={joined_tags=[{name=unnamed_0, table_ref=query0}]}}}");
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
				"{tab1={end_idx=[[@17,74:74='a',<452>,1:74]], start_idx=[[@13,61:61='a',<452>,1:61]], tags=[[@9,53:53='a',<452>,1:53]]}, tab_out={sliced_tags=[[@4,21:31='sliced_tags',<452>,1:21]]}}",
				"{query0={unnamed_0=[[@20,83:83=')',<288>,1:83]]}, insert1={sliced_tags=[[@4,21:31='sliced_tags',<452>,1:21]]}}",
				"{def_insert1={query_dictionary={sliced_tags=[[@4,21:31='sliced_tags',<452>,1:21]]}, table_dictionary={tab_out={sliced_tags=[[@4,21:31='sliced_tags',<452>,1:21]]}}, def_query0={query_dictionary={unnamed_0=[[@20,83:83=')',<288>,1:83]]}, table_dictionary={tab1={end_idx=[[@17,74:74='a',<452>,1:74]], start_idx=[[@13,61:61='a',<452>,1:61]], tags=[[@9,53:53='a',<452>,1:53]]}}, interface={unnamed_0=[{name=tags, table_ref=a}, {name=start_idx, table_ref=a}, {name=end_idx, table_ref=a}]}, table_alias={a=tab1}}, interface={sliced_tags=[{name=unnamed_0, table_ref=query0}]}}}");
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
				"{tab1={tags=[[@7,46:49='tags',<452>,1:46], [@10,55:55=')',<288>,1:55]], sep=[[@9,52:54='sep',<452>,1:52], [@10,55:55=')',<288>,1:55]]}}",
				"{update0={joined_tags=[[@3,16:26='joined_tags',<452>,1:16]]}}",
				"{def_update0={assignments={joined_tags=[{name=tags, table_ref=tab1}, {name=sep, table_ref=tab1}]}, table_dictionary={tab1={tags=[[@7,46:49='tags',<452>,1:46], [@10,55:55=')',<288>,1:55]], sep=[[@9,52:54='sep',<452>,1:52], [@10,55:55=')',<288>,1:55]]}}, update_dictionary={joined_tags=[[@3,16:26='joined_tags',<452>,1:16]]}, target_table={tab1={joined_tags=[[@3,16:26='joined_tags',<452>,1:16]]}}, lhs_unresolved_columns={joined_tags={column={name=joined_tags, table_ref=null}, locations=[[@3,16:26='joined_tags',<452>,1:16]]}}}}");
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
				"{tab1={end_idx=[[@11,59:65='end_idx',<452>,1:59], [@12,66:66=')',<288>,1:66]], start_idx=[[@9,48:56='start_idx',<452>,1:48], [@12,66:66=')',<288>,1:66]], tags=[[@7,42:45='tags',<452>,1:42], [@12,66:66=')',<288>,1:66]]}}",
				"{update0={sliced_tags=[[@3,16:26='sliced_tags',<452>,1:16]]}}",
				"{def_update0={assignments={sliced_tags=[{name=tags, table_ref=tab1}, {name=start_idx, table_ref=tab1}, {name=end_idx, table_ref=tab1}]}, table_dictionary={tab1={end_idx=[[@11,59:65='end_idx',<452>,1:59], [@12,66:66=')',<288>,1:66]], start_idx=[[@9,48:56='start_idx',<452>,1:48], [@12,66:66=')',<288>,1:66]], tags=[[@7,42:45='tags',<452>,1:42], [@12,66:66=')',<288>,1:66]]}}, update_dictionary={sliced_tags=[[@3,16:26='sliced_tags',<452>,1:16]]}, target_table={tab1={sliced_tags=[[@3,16:26='sliced_tags',<452>,1:16]]}}, lhs_unresolved_columns={sliced_tags={column={name=sliced_tags, table_ref=null}, locations=[[@3,16:26='sliced_tags',<452>,1:16]]}}}}");
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
				"{tab1={tags=[[@6,39:42='tags',<452>,1:39]], sep=[[@8,45:47='sep',<452>,1:45]]}}",
				"{}",
				"{def_delete0={query_dictionary={}, table_dictionary={tab1={tags=[[@6,39:42='tags',<452>,1:39]], sep=[[@8,45:47='sep',<452>,1:45]]}}, filters=[{name=tags, table_ref=tab1}, {name=sep, table_ref=tab1}], interface=null}}");
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
				"{tab1={end_idx=[[@10,52:58='end_idx',<452>,1:52]], start_idx=[[@8,41:49='start_idx',<452>,1:41]], tags=[[@6,35:38='tags',<452>,1:35]]}}",
				"{}",
				"{def_delete0={query_dictionary={}, table_dictionary={tab1={end_idx=[[@10,52:58='end_idx',<452>,1:52]], start_idx=[[@8,41:49='start_idx',<452>,1:41]], tags=[[@6,35:38='tags',<452>,1:35]]}}, filters=[{name=tags, table_ref=tab1}, {name=start_idx, table_ref=tab1}, {name=end_idx, table_ref=tab1}], interface=null}}");
	}
}
