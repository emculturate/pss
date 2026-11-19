package sql.walker;

import org.junit.Assert;
import org.junit.Test;

import access.Snippet;
import errorhandling.ParseDiagnostic;
import sql.SQLSelectParserParser;

public class SqlEventWalkerSnowflakeArraySetAggregateTests extends AbstractSqlParseEventWalkerTest {

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
	public void snowflakeArrayUnionAggSelectPlacementV0Test() {
		final String query = "SELECT array_union_agg(tags) AS merged_tags FROM events e";
		final SqlParseEventWalker extractor = runQuery(query);
		final Snippet snippet = extractor.getSnippet();
		assertDiagnosticAtPosition(
				snippet,
				"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
				ParseDiagnostic.Severity.WARNING,
				"routine 'array_union_agg' at (l:1 c:7) is recognized as Snowflake-specific grammar.",
				null,
				1,
				7);
		assertNoFatalErrors(extractor);
		assertOutputs(
				extractor,
				"{SQL={select={1={function={function_name=array_union_agg, qualifier=null, parameters={column={name=tags, table_ref=null}}}, alias=merged_tags}}, from={table={alias=e, table=events}}}}",
				"[merged_tags]",
				"{}",
				"{events={tags=[[@3,23:26='tags',<459>,1:23]]}}",
				"{query0={merged_tags=[[@6,32:42='merged_tags',<459>,1:32]]}}",
				"{def_query0={query_dictionary={merged_tags=[[@6,32:42='merged_tags',<459>,1:32]]}, table_dictionary={events={tags=[[@3,23:26='tags',<459>,1:23]]}}, interface={merged_tags=[{name=tags, table_ref=events}]}, table_alias={e=events}}}");
	}

	@Test
	public void snowflakeArrayUniqueAggSelectPlacementV0Test() {
		final String query = "SELECT array_unique_agg(tags) AS unique_tags FROM events e";
		final SqlParseEventWalker extractor = runQuery(query);
		final Snippet snippet = extractor.getSnippet();
		assertDiagnosticAtPosition(
				snippet,
				"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
				ParseDiagnostic.Severity.WARNING,
				"routine 'array_unique_agg' at (l:1 c:7) is recognized as Snowflake-specific grammar.",
				null,
				1,
				7);
		assertNoFatalErrors(extractor);
		assertOutputs(
				extractor,
				"{SQL={select={1={function={function_name=array_unique_agg, qualifier=null, parameters={column={name=tags, table_ref=null}}}, alias=unique_tags}}, from={table={alias=e, table=events}}}}",
				"[unique_tags]",
				"{}",
				"{events={tags=[[@3,24:27='tags',<459>,1:24]]}}",
				"{query0={unique_tags=[[@6,33:43='unique_tags',<459>,1:33]]}}",
				"{def_query0={query_dictionary={unique_tags=[[@6,33:43='unique_tags',<459>,1:33]]}, table_dictionary={events={tags=[[@3,24:27='tags',<459>,1:24]]}}, interface={unique_tags=[{name=tags, table_ref=events}]}, table_alias={e=events}}}");
	}

	@Test
	public void snowflakeArraySetAggregatesWarningEmittedOncePerStatementV0Test() {
		final String query =
				"SELECT array_union_agg(tags) AS merged_tags, array_unique_agg(tags) AS unique_tags FROM events e";
		final SqlParseEventWalker extractor = runQuery(query);
		assertDiagnosticCountBySeverity(
				extractor.getSnippet(),
				"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
				ParseDiagnostic.Severity.WARNING,
				null,
				null,
				1);
	}

	@Test
	public void snowflakeArrayUnionAggQualifiedMissingAliasFatalV0Test() {
		final String query = "SELECT array_union_agg(z.tags) AS merged_tags FROM events e";
		final SqlParseEventWalker extractor = runQuery(query);
		Assert.assertEquals("Expected one fatal walker message", 1, extractor.getSnippet().getFatalErrorStringList().size());
		Assert.assertTrue(
				"Expected missing alias fatal for z",
				extractor.getSnippet().getFatalErrorStringList().get(0).contains("No alias or table called 'z'"));
	}

	@Test
	public void snowflakeArrayUniqueAggAmbiguousColumnV0Test() {
		final String query = "SELECT array_unique_agg(id) AS unique_ids FROM tab1 a JOIN tab2 b ON a.id = b.id";
		final SqlParseEventWalker extractor = runQuery(query);
		final Snippet snippet = extractor.getSnippet();
		assertDiagnosticCountBySeverity(
				snippet,
				"AMBIGUOUS_COLUMN_REFERENCE",
				ParseDiagnostic.Severity.SEVERE_WARNING,
				null,
				"id",
				1);
	}

	@Test
	public void snowflakeArrayUnionAggSetOpInterfacePropagationV0Test() {
		final String query =
				"SELECT array_union_agg(t.tags) AS arr FROM tab1 t "
						+ "UNION ALL "
						+ "SELECT array_union_agg(u.tags) AS arr FROM tab2 u";
		final SqlParseEventWalker extractor = runQuery(query);
		final Snippet snippet = extractor.getSnippet();
		assertDiagnosticCountBySeverity(
				snippet,
				"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
				ParseDiagnostic.Severity.WARNING,
				null,
				null,
				1);
		Assert.assertEquals("Interface is wrong", "[arr]", extractor.getInterface().toString());
		Assert.assertTrue(
				"AST should include both UNION ALL branches with array_union_agg",
				extractor.getAsTree().toString().contains("operator=UNION")
						&& extractor.getAsTree().toString().contains("qualifier=ALL")
						&& extractor.getAsTree().toString().contains("function_name=array_union_agg"));
		Assert.assertTrue(
				"Symbol table should retain set-op branch query dictionaries",
				extractor.getSymbolTable().toString().contains("def_union2")
						&& extractor.getSymbolTable().toString().contains("def_query1")
						&& extractor.getSymbolTable().toString().contains("def_query0"));
	}

	@Test
	public void snowflakeArrayUniqueAggScriptStatementScopedWarningV0Test() {
		final String query =
				"SELECT array_unique_agg(tags) AS unique_tags FROM events e; SELECT event_id FROM events e;";
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
				"routine 'array_unique_agg' at (l:1 c:7) is recognized as Snowflake-specific grammar.",
				null,
				1,
				7);
	}
}
