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
				"{tab1={tags=[[@3,22:22='a',<452>,1:22]]}}",
				"{query0={distinct_tags=[[@8,33:45='distinct_tags',<452>,1:33]]}}",
				"{def_query0={query_dictionary={distinct_tags=[[@8,33:45='distinct_tags',<452>,1:33]]}, table_dictionary={tab1={tags=[[@3,22:22='a',<452>,1:22]]}}, interface={distinct_tags=[{name=tags, table_ref=a}]}, table_alias={a=tab1}}}");
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
				"{query0={distinct_tags=[[@8,33:45='distinct_tags',<452>,1:33]]}}",
				"{def_query0={query_dictionary={distinct_tags=[[@8,33:45='distinct_tags',<452>,1:33]]}, table_dictionary={tab1={}}, interface={distinct_tags=[{name=tags, table_ref=z}]}, table_alias={a=tab1}}}");
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
				"{tab1={tags=[[@3,21:21='a',<452>,1:21]]}}",
				"{query0={compact_tags=[[@8,32:43='compact_tags',<452>,1:32]]}}",
				"{def_query0={query_dictionary={compact_tags=[[@8,32:43='compact_tags',<452>,1:32]]}, table_dictionary={tab1={tags=[[@3,21:21='a',<452>,1:21]]}}, interface={compact_tags=[{name=tags, table_ref=a}]}, table_alias={a=tab1}}}");
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
				"{query0={compact_tags=[[@8,32:43='compact_tags',<452>,1:32]]}}",
				"{def_query0={query_dictionary={compact_tags=[[@8,32:43='compact_tags',<452>,1:32]]}, table_dictionary={tab1={}}, interface={compact_tags=[{name=tags, table_ref=z}]}, table_alias={a=tab1}}}");
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
				"{tab1={tags=[[@3,18:18='a',<452>,1:18]]}}",
				"{query0={tags_size=[[@8,29:37='tags_size',<452>,1:29]]}}",
				"{def_query0={query_dictionary={tags_size=[[@8,29:37='tags_size',<452>,1:29]]}, table_dictionary={tab1={tags=[[@3,18:18='a',<452>,1:18]]}}, interface={tags_size=[{name=tags, table_ref=a}]}, table_alias={a=tab1}}}");
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
				"{query0={tags_size=[[@8,29:37='tags_size',<452>,1:29]]}}",
				"{def_query0={query_dictionary={tags_size=[[@8,29:37='tags_size',<452>,1:29]]}, table_dictionary={tab1={}}, interface={tags_size=[{name=tags, table_ref=z}]}, table_alias={a=tab1}}}");
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
				"{tab1={id=[[@1,7:7='a',<452>,1:7]], tags=[[@10,44:44='a',<452>,1:44]]}}",
				"{query0={id=[[@3,9:10='id',<452>,1:9]]}}",
				"{def_query0={query_dictionary={id=[[@3,9:10='id',<452>,1:9]]}, table_dictionary={tab1={id=[[@1,7:7='a',<452>,1:7]], tags=[[@10,44:44='a',<452>,1:44]]}}, filters=[{name=tags, table_ref=a}], interface={id=[{name=id, table_ref=a}]}, table_alias={a=tab1}}}");
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
				"{tab1={id=[[@1,7:7='a',<452>,1:7]], tags=[[@11,47:47='a',<452>,1:47]]}}",
				"{query0={id=[[@3,9:10='id',<452>,1:9]]}}",
				"{def_query0={query_dictionary={id=[[@3,9:10='id',<452>,1:9]]}, table_dictionary={tab1={id=[[@1,7:7='a',<452>,1:7]], tags=[[@11,47:47='a',<452>,1:47]]}}, ordered_by=[{name=tags, table_ref=a}], interface={id=[{name=id, table_ref=a}]}, table_alias={a=tab1}}}");
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
				"{tab1={tags=[[@9,56:56='a',<452>,1:56]]}, tab_out={compact_tags=[[@4,21:32='compact_tags',<452>,1:21]]}}",
				"{query0={unnamed_0=[[@12,62:62=')',<288>,1:62]]}, insert1={compact_tags=[[@4,21:32='compact_tags',<452>,1:21]]}}",
				"{def_insert1={query_dictionary={compact_tags=[[@4,21:32='compact_tags',<452>,1:21]]}, table_dictionary={tab_out={compact_tags=[[@4,21:32='compact_tags',<452>,1:21]]}}, def_query0={query_dictionary={unnamed_0=[[@12,62:62=')',<288>,1:62]]}, table_dictionary={tab1={tags=[[@9,56:56='a',<452>,1:56]]}}, interface={unnamed_0=[{name=tags, table_ref=a}]}, table_alias={a=tab1}}, interface={compact_tags=[{name=unnamed_0, table_ref=query0}]}}}");
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
				"{tab1={tags=[[@7,45:48='tags',<452>,1:45], [@8,49:49=')',<288>,1:49]]}}",
				"{update0={compact_tags=[[@3,16:27='compact_tags',<452>,1:16]]}}",
				"{def_update0={assignments={compact_tags=[{name=tags, table_ref=tab1}]}, table_dictionary={tab1={tags=[[@7,45:48='tags',<452>,1:45], [@8,49:49=')',<288>,1:49]]}}, update_dictionary={compact_tags=[[@3,16:27='compact_tags',<452>,1:16]]}, target_table={tab1={compact_tags=[[@3,16:27='compact_tags',<452>,1:16]]}}, lhs_unresolved_columns={compact_tags={column={name=compact_tags, table_ref=null}, locations=[[@3,16:27='compact_tags',<452>,1:16]]}}}}");
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
				"{tab1={tags=[[@6,37:40='tags',<452>,1:37]]}}",
				"{}",
				"{def_delete0={query_dictionary={}, table_dictionary={tab1={tags=[[@6,37:40='tags',<452>,1:37]]}}, filters=[{name=tags, table_ref=tab1}], interface=null}}");
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
				"{tab1={id=[[@1,7:7='a',<452>,1:7]], tags=[[@10,41:41='a',<452>,1:41]]}}",
				"{query0={id=[[@3,9:10='id',<452>,1:9]]}}",
				"{def_query0={query_dictionary={id=[[@3,9:10='id',<452>,1:9]]}, table_dictionary={tab1={id=[[@1,7:7='a',<452>,1:7]], tags=[[@10,41:41='a',<452>,1:41]]}}, filters=[{name=tags, table_ref=a}], interface={id=[{name=id, table_ref=a}]}, table_alias={a=tab1}}}");
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
				"{tab1={id=[[@1,7:7='a',<452>,1:7]], tags=[[@11,44:44='a',<452>,1:44]]}}",
				"{query0={id=[[@3,9:10='id',<452>,1:9]]}}",
				"{def_query0={query_dictionary={id=[[@3,9:10='id',<452>,1:9]]}, table_dictionary={tab1={id=[[@1,7:7='a',<452>,1:7]], tags=[[@11,44:44='a',<452>,1:44]]}}, ordered_by=[{name=tags, table_ref=a}], interface={id=[{name=id, table_ref=a}]}, table_alias={a=tab1}}}");
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
				"{tab1={tags=[[@9,50:50='a',<452>,1:50]]}, tab_out={tags_size=[[@4,21:29='tags_size',<452>,1:21]]}}",
				"{query0={unnamed_0=[[@12,56:56=')',<288>,1:56]]}, insert1={tags_size=[[@4,21:29='tags_size',<452>,1:21]]}}",
				"{def_insert1={query_dictionary={tags_size=[[@4,21:29='tags_size',<452>,1:21]]}, table_dictionary={tab_out={tags_size=[[@4,21:29='tags_size',<452>,1:21]]}}, def_query0={query_dictionary={unnamed_0=[[@12,56:56=')',<288>,1:56]]}, table_dictionary={tab1={tags=[[@9,50:50='a',<452>,1:50]]}}, interface={unnamed_0=[{name=tags, table_ref=a}]}, table_alias={a=tab1}}, interface={tags_size=[{name=unnamed_0, table_ref=query0}]}}}");
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
				"{tab1={tags=[[@7,39:42='tags',<452>,1:39], [@8,43:43=')',<288>,1:43]]}}",
				"{update0={tags_size=[[@3,16:24='tags_size',<452>,1:16]]}}",
				"{def_update0={assignments={tags_size=[{name=tags, table_ref=tab1}]}, table_dictionary={tab1={tags=[[@7,39:42='tags',<452>,1:39], [@8,43:43=')',<288>,1:43]]}}, update_dictionary={tags_size=[[@3,16:24='tags_size',<452>,1:16]]}, target_table={tab1={tags_size=[[@3,16:24='tags_size',<452>,1:16]]}}, lhs_unresolved_columns={tags_size={column={name=tags_size, table_ref=null}, locations=[[@3,16:24='tags_size',<452>,1:16]]}}}}");
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
				"{tab1={tags=[[@6,34:37='tags',<452>,1:34]]}}",
				"{}",
				"{def_delete0={query_dictionary={}, table_dictionary={tab1={tags=[[@6,34:37='tags',<452>,1:34]]}}, filters=[{name=tags, table_ref=tab1}], interface=null}}");
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
				"{tab1={payload=[[@3,22:22='a',<452>,1:22]], sep=[[@7,33:33='a',<452>,1:33]]}}",
				"{query0={split_vals=[[@12,43:52='split_vals',<452>,1:43]]}}",
				"{def_query0={query_dictionary={split_vals=[[@12,43:52='split_vals',<452>,1:43]]}, table_dictionary={tab1={payload=[[@3,22:22='a',<452>,1:22]], sep=[[@7,33:33='a',<452>,1:33]]}}, interface={split_vals=[{name=payload, table_ref=a}, {name=sep, table_ref=a}]}, table_alias={a=tab1}}}");
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
				"{tab1={sep=[[@7,33:33='a',<452>,1:33]]}}",
				"{query0={split_vals=[[@12,43:52='split_vals',<452>,1:43]]}}",
				"{def_query0={query_dictionary={split_vals=[[@12,43:52='split_vals',<452>,1:43]]}, table_dictionary={tab1={sep=[[@7,33:33='a',<452>,1:33]]}}, interface={split_vals=[{name=payload, table_ref=z}, {name=sep, table_ref=a}]}, table_alias={a=tab1}}}");
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
				"{query0={gen_vals=[[@8,37:44='gen_vals',<452>,1:37]]}}",
				"{def_query0={query_dictionary={gen_vals=[[@8,37:44='gen_vals',<452>,1:37]]}, table_dictionary={tab1={}}, interface={gen_vals=[]}, table_alias={a=tab1}}}");
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
				"{tab1={needle=[[@3,22:22='a',<452>,1:22]], tags=[[@7,32:32='a',<452>,1:32]]}}",
				"{query0={pos_idx=[[@12,43:49='pos_idx',<452>,1:43]]}}",
				"{def_query0={query_dictionary={pos_idx=[[@12,43:49='pos_idx',<452>,1:43]]}, table_dictionary={tab1={needle=[[@3,22:22='a',<452>,1:22]], tags=[[@7,32:32='a',<452>,1:32]]}}, interface={pos_idx=[{name=needle, table_ref=a}, {name=tags, table_ref=a}]}, table_alias={a=tab1}}}");
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
				"{tab1={tags=[[@7,32:32='a',<452>,1:32]]}}",
				"{query0={pos_idx=[[@12,43:49='pos_idx',<452>,1:43]]}}",
				"{def_query0={query_dictionary={pos_idx=[[@12,43:49='pos_idx',<452>,1:43]]}, table_dictionary={tab1={tags=[[@7,32:32='a',<452>,1:32]]}}, interface={pos_idx=[{name=needle, table_ref=z}, {name=tags, table_ref=a}]}, table_alias={a=tab1}}}");
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
				"{tab1={id=[[@1,7:7='a',<452>,1:7]], needle=[[@10,45:45='a',<452>,1:45]], tags=[[@14,55:55='a',<452>,1:55]]}}",
				"{query0={id=[[@3,9:10='id',<452>,1:9]]}}",
				"{def_query0={query_dictionary={id=[[@3,9:10='id',<452>,1:9]]}, table_dictionary={tab1={id=[[@1,7:7='a',<452>,1:7]], needle=[[@10,45:45='a',<452>,1:45]], tags=[[@14,55:55='a',<452>,1:55]]}}, filters=[{name=needle, table_ref=a}, {name=tags, table_ref=a}], interface={id=[{name=id, table_ref=a}]}, table_alias={a=tab1}}}");
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
				"{tab1={id=[[@1,7:7='a',<452>,1:7]], needle=[[@11,48:48='a',<452>,1:48]], tags=[[@15,58:58='a',<452>,1:58]]}}",
				"{query0={id=[[@3,9:10='id',<452>,1:9]]}}",
				"{def_query0={query_dictionary={id=[[@3,9:10='id',<452>,1:9]]}, table_dictionary={tab1={id=[[@1,7:7='a',<452>,1:7]], needle=[[@11,48:48='a',<452>,1:48]], tags=[[@15,58:58='a',<452>,1:58]]}}, ordered_by=[{name=needle, table_ref=a}, {name=tags, table_ref=a}], interface={id=[{name=id, table_ref=a}]}, table_alias={a=tab1}}}");
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
				"{tab1={needle=[[@9,51:51='a',<452>,1:51]], tags=[[@13,61:61='a',<452>,1:61]]}, tab_out={pos_idx=[[@4,20:26='pos_idx',<452>,1:20]]}}",
				"{query0={unnamed_0=[[@16,67:67=')',<288>,1:67]]}, insert1={pos_idx=[[@4,20:26='pos_idx',<452>,1:20]]}}",
				"{def_insert1={query_dictionary={pos_idx=[[@4,20:26='pos_idx',<452>,1:20]]}, table_dictionary={tab_out={pos_idx=[[@4,20:26='pos_idx',<452>,1:20]]}}, def_query0={query_dictionary={unnamed_0=[[@16,67:67=')',<288>,1:67]]}, table_dictionary={tab1={needle=[[@9,51:51='a',<452>,1:51]], tags=[[@13,61:61='a',<452>,1:61]]}}, interface={unnamed_0=[{name=needle, table_ref=a}, {name=tags, table_ref=a}]}, table_alias={a=tab1}}, interface={pos_idx=[{name=unnamed_0, table_ref=query0}]}}}");
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
				"{tab1={needle=[[@7,41:46='needle',<452>,1:41], [@10,53:53=')',<288>,1:53]], tags=[[@9,49:52='tags',<452>,1:49], [@10,53:53=')',<288>,1:53]]}}",
				"{update0={pos_idx=[[@3,16:22='pos_idx',<452>,1:16]]}}",
				"{def_update0={assignments={pos_idx=[{name=needle, table_ref=tab1}, {name=tags, table_ref=tab1}]}, table_dictionary={tab1={needle=[[@7,41:46='needle',<452>,1:41], [@10,53:53=')',<288>,1:53]], tags=[[@9,49:52='tags',<452>,1:49], [@10,53:53=')',<288>,1:53]]}}, update_dictionary={pos_idx=[[@3,16:22='pos_idx',<452>,1:16]]}, target_table={tab1={pos_idx=[[@3,16:22='pos_idx',<452>,1:16]]}}, lhs_unresolved_columns={pos_idx={column={name=pos_idx, table_ref=null}, locations=[[@3,16:22='pos_idx',<452>,1:16]]}}}}");
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
				"{tab1={needle=[[@6,38:43='needle',<452>,1:38]], tags=[[@8,46:49='tags',<452>,1:46]]}}",
				"{}",
				"{def_delete0={query_dictionary={}, table_dictionary={tab1={needle=[[@6,38:43='needle',<452>,1:38]], tags=[[@8,46:49='tags',<452>,1:46]]}}, filters=[{name=needle, table_ref=tab1}, {name=tags, table_ref=tab1}], interface=null}}");
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
				"{tab1={vals=[[@7,26:26='a',<452>,1:26]], tags=[[@3,18:18='a',<452>,1:18]]}}",
				"{query0={zipped_vals=[[@12,37:47='zipped_vals',<452>,1:37]]}}",
				"{def_query0={query_dictionary={zipped_vals=[[@12,37:47='zipped_vals',<452>,1:37]]}, table_dictionary={tab1={vals=[[@7,26:26='a',<452>,1:26]], tags=[[@3,18:18='a',<452>,1:18]]}}, interface={zipped_vals=[{name=tags, table_ref=a}, {name=vals, table_ref=a}]}, table_alias={a=tab1}}}");
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
				"{tab1={vals=[[@7,26:26='a',<452>,1:26]]}}",
				"{query0={zipped_vals=[[@12,37:47='zipped_vals',<452>,1:37]]}}",
				"{def_query0={query_dictionary={zipped_vals=[[@12,37:47='zipped_vals',<452>,1:37]]}, table_dictionary={tab1={vals=[[@7,26:26='a',<452>,1:26]]}}, interface={zipped_vals=[{name=tags, table_ref=z}, {name=vals, table_ref=a}]}, table_alias={a=tab1}}}");
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
				"{tab1={id=[[@1,7:7='a',<452>,1:7]], tags=[[@10,45:45='a',<452>,1:45]], sep=[[@14,53:53='a',<452>,1:53]]}}",
				"{query0={id=[[@3,9:10='id',<452>,1:9]]}}",
				"{def_query0={query_dictionary={id=[[@3,9:10='id',<452>,1:9]]}, table_dictionary={tab1={id=[[@1,7:7='a',<452>,1:7]], tags=[[@10,45:45='a',<452>,1:45]], sep=[[@14,53:53='a',<452>,1:53]]}}, filters=[{name=tags, table_ref=a}, {name=sep, table_ref=a}], interface={id=[{name=id, table_ref=a}]}, table_alias={a=tab1}}}");
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
				"{tab1={id=[[@1,7:7='a',<452>,1:7]], tags=[[@11,48:48='a',<452>,1:48]], sep=[[@15,56:56='a',<452>,1:56]]}}",
				"{query0={id=[[@3,9:10='id',<452>,1:9]]}}",
				"{def_query0={query_dictionary={id=[[@3,9:10='id',<452>,1:9]]}, table_dictionary={tab1={id=[[@1,7:7='a',<452>,1:7]], tags=[[@11,48:48='a',<452>,1:48]], sep=[[@15,56:56='a',<452>,1:56]]}}, ordered_by=[{name=tags, table_ref=a}, {name=sep, table_ref=a}], interface={id=[{name=id, table_ref=a}]}, table_alias={a=tab1}}}");
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
				"{tab1={tags=[[@9,54:54='a',<452>,1:54]], sep=[[@13,62:62='a',<452>,1:62]]}, tab_out={split_vals=[[@4,20:29='split_vals',<452>,1:20]]}}",
				"{query0={unnamed_0=[[@16,67:67=')',<288>,1:67]]}, insert1={split_vals=[[@4,20:29='split_vals',<452>,1:20]]}}",
				"{def_insert1={query_dictionary={split_vals=[[@4,20:29='split_vals',<452>,1:20]]}, table_dictionary={tab_out={split_vals=[[@4,20:29='split_vals',<452>,1:20]]}}, def_query0={query_dictionary={unnamed_0=[[@16,67:67=')',<288>,1:67]]}, table_dictionary={tab1={tags=[[@9,54:54='a',<452>,1:54]], sep=[[@13,62:62='a',<452>,1:62]]}}, interface={unnamed_0=[{name=tags, table_ref=a}, {name=sep, table_ref=a}]}, table_alias={a=tab1}}, interface={split_vals=[{name=unnamed_0, table_ref=query0}]}}}");
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
				"{tab1={tags=[[@7,44:47='tags',<452>,1:44], [@10,53:53=')',<288>,1:53]], sep=[[@9,50:52='sep',<452>,1:50], [@10,53:53=')',<288>,1:53]]}}",
				"{update0={split_vals=[[@3,16:25='split_vals',<452>,1:16]]}}",
				"{def_update0={assignments={split_vals=[{name=tags, table_ref=tab1}, {name=sep, table_ref=tab1}]}, table_dictionary={tab1={tags=[[@7,44:47='tags',<452>,1:44], [@10,53:53=')',<288>,1:53]], sep=[[@9,50:52='sep',<452>,1:50], [@10,53:53=')',<288>,1:53]]}}, update_dictionary={split_vals=[[@3,16:25='split_vals',<452>,1:16]]}, target_table={tab1={split_vals=[[@3,16:25='split_vals',<452>,1:16]]}}, lhs_unresolved_columns={split_vals={column={name=split_vals, table_ref=null}, locations=[[@3,16:25='split_vals',<452>,1:16]]}}}}");
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
				"{tab1={tags=[[@6,38:41='tags',<452>,1:38]], sep=[[@8,44:46='sep',<452>,1:44]]}}",
				"{}",
				"{def_delete0={query_dictionary={}, table_dictionary={tab1={tags=[[@6,38:41='tags',<452>,1:38]], sep=[[@8,44:46='sep',<452>,1:44]]}}, filters=[{name=tags, table_ref=tab1}, {name=sep, table_ref=tab1}], interface=null}}");
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
				"{tab1={id=[[@1,7:7='a',<452>,1:7]]}}",
				"{query0={id=[[@3,9:10='id',<452>,1:9]]}}",
				"{def_query0={query_dictionary={id=[[@3,9:10='id',<452>,1:9]]}, table_dictionary={tab1={id=[[@1,7:7='a',<452>,1:7]]}}, filters=[], interface={id=[{name=id, table_ref=a}]}, table_alias={a=tab1}}}");
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
				"{tab1={id=[[@1,7:7='a',<452>,1:7]]}}",
				"{query0={id=[[@3,9:10='id',<452>,1:9]]}}",
				"{def_query0={query_dictionary={id=[[@3,9:10='id',<452>,1:9]]}, table_dictionary={tab1={id=[[@1,7:7='a',<452>,1:7]]}}, ordered_by=[], interface={id=[{name=id, table_ref=a}]}, table_alias={a=tab1}}}");
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
				"{tab1={}, tab_out={gen_vals=[[@4,20:27='gen_vals',<452>,1:20]]}}",
				"{query0={unnamed_0=[[@12,62:62=')',<288>,1:62]]}, insert1={gen_vals=[[@4,20:27='gen_vals',<452>,1:20]]}}",
				"{def_insert1={query_dictionary={gen_vals=[[@4,20:27='gen_vals',<452>,1:20]]}, table_dictionary={tab_out={gen_vals=[[@4,20:27='gen_vals',<452>,1:20]]}}, def_query0={query_dictionary={unnamed_0=[[@12,62:62=')',<288>,1:62]]}, table_dictionary={tab1={}}, interface={unnamed_0=[]}, table_alias={a=tab1}}, interface={gen_vals=[{name=unnamed_0, table_ref=query0}]}}}");
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
				"{update0={gen_vals=[[@3,16:23='gen_vals',<452>,1:16]]}}",
				"{def_update0={assignments={gen_vals=[]}, table_dictionary={tab1={}}, update_dictionary={gen_vals=[[@3,16:23='gen_vals',<452>,1:16]]}, target_table={tab1={gen_vals=[[@3,16:23='gen_vals',<452>,1:16]]}}, lhs_unresolved_columns={gen_vals={column={name=gen_vals, table_ref=null}, locations=[[@3,16:23='gen_vals',<452>,1:16]]}}}}");
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
				"{tab1={vals=[[@14,49:49='a',<452>,1:49]], id=[[@1,7:7='a',<452>,1:7]], tags=[[@10,41:41='a',<452>,1:41]]}}",
				"{query0={id=[[@3,9:10='id',<452>,1:9]]}}",
				"{def_query0={query_dictionary={id=[[@3,9:10='id',<452>,1:9]]}, table_dictionary={tab1={vals=[[@14,49:49='a',<452>,1:49]], id=[[@1,7:7='a',<452>,1:7]], tags=[[@10,41:41='a',<452>,1:41]]}}, filters=[{name=tags, table_ref=a}, {name=vals, table_ref=a}], interface={id=[{name=id, table_ref=a}]}, table_alias={a=tab1}}}");
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
				"{tab1={vals=[[@15,52:52='a',<452>,1:52]], id=[[@1,7:7='a',<452>,1:7]], tags=[[@11,44:44='a',<452>,1:44]]}}",
				"{query0={id=[[@3,9:10='id',<452>,1:9]]}}",
				"{def_query0={query_dictionary={id=[[@3,9:10='id',<452>,1:9]]}, table_dictionary={tab1={vals=[[@15,52:52='a',<452>,1:52]], id=[[@1,7:7='a',<452>,1:7]], tags=[[@11,44:44='a',<452>,1:44]]}}, ordered_by=[{name=tags, table_ref=a}, {name=vals, table_ref=a}], interface={id=[{name=id, table_ref=a}]}, table_alias={a=tab1}}}");
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
				"{tab1={vals=[[@13,59:59='a',<452>,1:59]], tags=[[@9,51:51='a',<452>,1:51]]}, tab_out={zipped_vals=[[@4,20:30='zipped_vals',<452>,1:20]]}}",
				"{query0={unnamed_0=[[@16,65:65=')',<288>,1:65]]}, insert1={zipped_vals=[[@4,20:30='zipped_vals',<452>,1:20]]}}",
				"{def_insert1={query_dictionary={zipped_vals=[[@4,20:30='zipped_vals',<452>,1:20]]}, table_dictionary={tab_out={zipped_vals=[[@4,20:30='zipped_vals',<452>,1:20]]}}, def_query0={query_dictionary={unnamed_0=[[@16,65:65=')',<288>,1:65]]}, table_dictionary={tab1={vals=[[@13,59:59='a',<452>,1:59]], tags=[[@9,51:51='a',<452>,1:51]]}}, interface={unnamed_0=[{name=tags, table_ref=a}, {name=vals, table_ref=a}]}, table_alias={a=tab1}}, interface={zipped_vals=[{name=unnamed_0, table_ref=query0}]}}}");
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
				"{tab1={vals=[[@9,47:50='vals',<452>,1:47], [@10,51:51=')',<288>,1:51]], tags=[[@7,41:44='tags',<452>,1:41], [@10,51:51=')',<288>,1:51]]}}",
				"{update0={zipped_vals=[[@3,16:26='zipped_vals',<452>,1:16]]}}",
				"{def_update0={assignments={zipped_vals=[{name=tags, table_ref=tab1}, {name=vals, table_ref=tab1}]}, table_dictionary={tab1={vals=[[@9,47:50='vals',<452>,1:47], [@10,51:51=')',<288>,1:51]], tags=[[@7,41:44='tags',<452>,1:41], [@10,51:51=')',<288>,1:51]]}}, update_dictionary={zipped_vals=[[@3,16:26='zipped_vals',<452>,1:16]]}, target_table={tab1={zipped_vals=[[@3,16:26='zipped_vals',<452>,1:16]]}}, lhs_unresolved_columns={zipped_vals={column={name=zipped_vals, table_ref=null}, locations=[[@3,16:26='zipped_vals',<452>,1:16]]}}}}");
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
				"{tab1={vals=[[@8,40:43='vals',<452>,1:40]], tags=[[@6,34:37='tags',<452>,1:34]]}}",
				"{}",
				"{def_delete0={query_dictionary={}, table_dictionary={tab1={vals=[[@8,40:43='vals',<452>,1:40]], tags=[[@6,34:37='tags',<452>,1:34]]}}, filters=[{name=tags, table_ref=tab1}, {name=vals, table_ref=tab1}], interface=null}}");
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
				"{tab1={payload=[[@3,23:23='a',<452>,1:23]], sep=[[@7,34:34='a',<452>,1:34]]}}",
				"{query0={strtok_vals=[[@12,44:54='strtok_vals',<452>,1:44]]}}",
				"{def_query0={query_dictionary={strtok_vals=[[@12,44:54='strtok_vals',<452>,1:44]]}, table_dictionary={tab1={payload=[[@3,23:23='a',<452>,1:23]], sep=[[@7,34:34='a',<452>,1:34]]}}, interface={strtok_vals=[{name=payload, table_ref=a}, {name=sep, table_ref=a}]}, table_alias={a=tab1}}}");
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
				"{tab1={sep=[[@7,34:34='a',<452>,1:34]]}}",
				"{query0={strtok_vals=[[@12,44:54='strtok_vals',<452>,1:44]]}}",
				"{def_query0={query_dictionary={strtok_vals=[[@12,44:54='strtok_vals',<452>,1:44]]}, table_dictionary={tab1={sep=[[@7,34:34='a',<452>,1:34]]}}, interface={strtok_vals=[{name=payload, table_ref=z}, {name=sep, table_ref=a}]}, table_alias={a=tab1}}}");
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
				"{tab1={id=[[@1,7:7='a',<452>,1:7]], tags=[[@10,46:46='a',<452>,1:46]], sep=[[@14,54:54='a',<452>,1:54]]}}",
				"{query0={id=[[@3,9:10='id',<452>,1:9]]}}",
				"{def_query0={query_dictionary={id=[[@3,9:10='id',<452>,1:9]]}, table_dictionary={tab1={id=[[@1,7:7='a',<452>,1:7]], tags=[[@10,46:46='a',<452>,1:46]], sep=[[@14,54:54='a',<452>,1:54]]}}, filters=[{name=tags, table_ref=a}, {name=sep, table_ref=a}], interface={id=[{name=id, table_ref=a}]}, table_alias={a=tab1}}}");
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
				"{tab1={id=[[@1,7:7='a',<452>,1:7]], tags=[[@11,49:49='a',<452>,1:49]], sep=[[@15,57:57='a',<452>,1:57]]}}",
				"{query0={id=[[@3,9:10='id',<452>,1:9]]}}",
				"{def_query0={query_dictionary={id=[[@3,9:10='id',<452>,1:9]]}, table_dictionary={tab1={id=[[@1,7:7='a',<452>,1:7]], tags=[[@11,49:49='a',<452>,1:49]], sep=[[@15,57:57='a',<452>,1:57]]}}, ordered_by=[{name=tags, table_ref=a}, {name=sep, table_ref=a}], interface={id=[{name=id, table_ref=a}]}, table_alias={a=tab1}}}");
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
				"{tab1={tags=[[@9,53:53='a',<452>,1:53]], sep=[[@13,61:61='a',<452>,1:61]]}, tab_out={tok_vals=[[@4,20:27='tok_vals',<452>,1:20]]}}",
				"{query0={unnamed_0=[[@16,66:66=')',<288>,1:66]]}, insert1={tok_vals=[[@4,20:27='tok_vals',<452>,1:20]]}}",
				"{def_insert1={query_dictionary={tok_vals=[[@4,20:27='tok_vals',<452>,1:20]]}, table_dictionary={tab_out={tok_vals=[[@4,20:27='tok_vals',<452>,1:20]]}}, def_query0={query_dictionary={unnamed_0=[[@16,66:66=')',<288>,1:66]]}, table_dictionary={tab1={tags=[[@9,53:53='a',<452>,1:53]], sep=[[@13,61:61='a',<452>,1:61]]}}, interface={unnamed_0=[{name=tags, table_ref=a}, {name=sep, table_ref=a}]}, table_alias={a=tab1}}, interface={tok_vals=[{name=unnamed_0, table_ref=query0}]}}}");
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
				"{tab1={tags=[[@7,43:46='tags',<452>,1:43], [@10,52:52=')',<288>,1:52]], sep=[[@9,49:51='sep',<452>,1:49], [@10,52:52=')',<288>,1:52]]}}",
				"{update0={tok_vals=[[@3,16:23='tok_vals',<452>,1:16]]}}",
				"{def_update0={assignments={tok_vals=[{name=tags, table_ref=tab1}, {name=sep, table_ref=tab1}]}, table_dictionary={tab1={tags=[[@7,43:46='tags',<452>,1:43], [@10,52:52=')',<288>,1:52]], sep=[[@9,49:51='sep',<452>,1:49], [@10,52:52=')',<288>,1:52]]}}, update_dictionary={tok_vals=[[@3,16:23='tok_vals',<452>,1:16]]}, target_table={tab1={tok_vals=[[@3,16:23='tok_vals',<452>,1:16]]}}, lhs_unresolved_columns={tok_vals={column={name=tok_vals, table_ref=null}, locations=[[@3,16:23='tok_vals',<452>,1:16]]}}}}");
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
				"{tab1={tags=[[@6,39:42='tags',<452>,1:39]], sep=[[@8,45:47='sep',<452>,1:45]]}}",
				"{}",
				"{def_delete0={query_dictionary={}, table_dictionary={tab1={tags=[[@6,39:42='tags',<452>,1:39]], sep=[[@8,45:47='sep',<452>,1:45]]}}, filters=[{name=tags, table_ref=tab1}, {name=sep, table_ref=tab1}], interface=null}}");
	}
}
