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
}
