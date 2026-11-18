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
}
