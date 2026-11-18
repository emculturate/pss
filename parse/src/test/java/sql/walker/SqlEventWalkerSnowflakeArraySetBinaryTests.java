package sql.walker;

import org.junit.Assert;
import org.junit.Test;

import access.Snippet;
import errorhandling.ParseDiagnostic;
import sql.SQLSelectParserParser;

public class SqlEventWalkerSnowflakeArraySetBinaryTests extends AbstractSqlParseEventWalkerTest {

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
	public void snowflakeArrayCatSelectPlacementV0Test() {
		final String query =
				"SELECT array_cat(a.tags, b.tags) AS merged_tags "
						+ "FROM tab1 a JOIN tab2 b ON a.id = b.id";
		final SqlParseEventWalker extractor = runQuery(query);
		final Snippet snippet = extractor.getSnippet();
		assertDiagnosticAtPosition(
				snippet,
				"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
				ParseDiagnostic.Severity.WARNING,
				"routine 'array_cat' at (l:1 c:7) is recognized as Snowflake-specific grammar.",
				null,
				1,
				7);
		assertNoFatalErrors(extractor);
		assertOutputs(
				extractor,
				"{SQL={select={1={function={function_name=array_cat, parameters={1={column={name=tags, table_ref=a}}, 2={column={name=tags, table_ref=b}}}}, alias=merged_tags}}, from={join={1={table={alias=a, table=tab1}}, 2={join=JOIN, on={condition={left={column={name=id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=tab2}}}}}}",
				"[merged_tags]",
				"{}",
				"{tab1={id=[[@20,75:75='a',<455>,1:75]], tags=[[@3,17:17='a',<455>,1:17]]}, tab2={id=[[@24,82:82='b',<455>,1:82]], tags=[[@7,25:25='b',<455>,1:25]]}}",
				"{query0={merged_tags=[[@12,36:46='merged_tags',<455>,1:36]]}}",
				"{def_query0={query_dictionary={merged_tags=[[@12,36:46='merged_tags',<455>,1:36]]}, table_dictionary={tab1={id=[[@20,75:75='a',<455>,1:75]], tags=[[@3,17:17='a',<455>,1:17]]}, tab2={id=[[@24,82:82='b',<455>,1:82]], tags=[[@7,25:25='b',<455>,1:25]]}}, filters=[{name=id, table_ref=a}, {name=id, table_ref=b}], interface={merged_tags=[{name=tags, table_ref=a}, {name=tags, table_ref=b}]}, table_alias={a=tab1, b=tab2}}}");
	}

	@Test
	public void snowflakeArrayExceptQualifiedMissingAliasFatalV0Test() {
		final String query = "SELECT array_except(z.tags, a.tags) AS diff_tags FROM tab1 a";
		final SqlParseEventWalker extractor = runQuery(query);
		final Snippet snippet = extractor.getSnippet();
		assertDiagnosticAtPosition(
				snippet,
				"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
				ParseDiagnostic.Severity.WARNING,
				"routine 'array_except' at (l:1 c:7) is recognized as Snowflake-specific grammar.",
				null,
				1,
				7);
		Assert.assertEquals("Expected one fatal walker message", 1, extractor.getSnippet().getFatalErrorStringList().size());
		Assert.assertTrue(
				"Expected missing alias fatal for z",
				extractor.getSnippet().getFatalErrorStringList().get(0).contains("No alias or table called 'z'"));
		assertOutputs(
				extractor,
				"{SQL={select={1={function={function_name=array_except, parameters={1={column={name=tags, table_ref=z}}, 2={column={name=tags, table_ref=a}}}}, alias=diff_tags}}, from={table={alias=a, table=tab1}}}}",
				"[diff_tags]",
				"{}",
				"{tab1={tags=[[@7,28:28='a',<455>,1:28]]}}",
				"{query0={diff_tags=[[@12,39:47='diff_tags',<455>,1:39]]}}",
				"{def_query0={query_dictionary={diff_tags=[[@12,39:47='diff_tags',<455>,1:39]]}, table_dictionary={tab1={tags=[[@7,28:28='a',<455>,1:28]]}}, interface={diff_tags=[{name=tags, table_ref=z}, {name=tags, table_ref=a}]}, table_alias={a=tab1}}}");
	}

	@Test
	public void snowflakeArrayIntersectionJoinArgumentsUseNormalResolutionV0Test() {
		final String query =
				"SELECT array_intersection(a.tags, b.tags) AS overlap_tags "
						+ "FROM tab1 a JOIN tab2 b ON a.id = b.id";
		final SqlParseEventWalker extractor = runQuery(query);
		final Snippet snippet = extractor.getSnippet();
		assertDiagnosticAtPosition(
				snippet,
				"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
				ParseDiagnostic.Severity.WARNING,
				"routine 'array_intersection' at (l:1 c:7) is recognized as Snowflake-specific grammar.",
				null,
				1,
				7);
		assertNoFatalErrors(extractor);
		assertOutputs(
				extractor,
				"{SQL={select={1={function={function_name=array_intersection, parameters={1={column={name=tags, table_ref=a}}, 2={column={name=tags, table_ref=b}}}}, alias=overlap_tags}}, from={join={1={table={alias=a, table=tab1}}, 2={join=JOIN, on={condition={left={column={name=id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=tab2}}}}}}",
				"[overlap_tags]",
				"{}",
				"{tab1={id=[[@20,85:85='a',<455>,1:85]], tags=[[@3,26:26='a',<455>,1:26]]}, tab2={id=[[@24,92:92='b',<455>,1:92]], tags=[[@7,34:34='b',<455>,1:34]]}}",
				"{query0={overlap_tags=[[@12,45:56='overlap_tags',<455>,1:45]]}}",
				"{def_query0={query_dictionary={overlap_tags=[[@12,45:56='overlap_tags',<455>,1:45]]}, table_dictionary={tab1={id=[[@20,85:85='a',<455>,1:85]], tags=[[@3,26:26='a',<455>,1:26]]}, tab2={id=[[@24,92:92='b',<455>,1:92]], tags=[[@7,34:34='b',<455>,1:34]]}}, filters=[{name=id, table_ref=a}, {name=id, table_ref=b}], interface={overlap_tags=[{name=tags, table_ref=a}, {name=tags, table_ref=b}]}, table_alias={a=tab1, b=tab2}}}");
	}

	@Test
	public void snowflakeArraysOverlapWherePredicateV0Test() {
		final String query =
				"SELECT a.id FROM tab1 a JOIN tab2 b ON a.id = b.id "
						+ "WHERE arrays_overlap(a.tags, b.tags)";
		final SqlParseEventWalker extractor = runQuery(query);
		final Snippet snippet = extractor.getSnippet();
		assertDiagnosticAtPosition(
				snippet,
				"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
				ParseDiagnostic.Severity.WARNING,
				"routine 'arrays_overlap' at (l:1 c:57) is recognized as Snowflake-specific grammar.",
				null,
				1,
				57);
		assertNoFatalErrors(extractor);
		assertOutputs(
				extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}}, from={join={1={table={alias=a, table=tab1}}, 2={join=JOIN, on={condition={left={column={name=id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=tab2}}}}, where={function={function_name=arrays_overlap, parameters={1={column={name=tags, table_ref=a}}, 2={column={name=tags, table_ref=b}}}}}}}",
				"[id]",
				"{}",
				"{tab1={id=[[@1,7:7='a',<455>,1:7], [@11,39:39='a',<455>,1:39]], tags=[[@21,72:72='a',<455>,1:72]]}, tab2={id=[[@15,46:46='b',<455>,1:46]], tags=[[@25,80:80='b',<455>,1:80]]}}",
				"{query0={id=[[@3,9:10='id',<455>,1:9]]}}",
				"{def_query0={query_dictionary={id=[[@3,9:10='id',<455>,1:9]]}, table_dictionary={tab1={id=[[@1,7:7='a',<455>,1:7], [@11,39:39='a',<455>,1:39]], tags=[[@21,72:72='a',<455>,1:72]]}, tab2={id=[[@15,46:46='b',<455>,1:46]], tags=[[@25,80:80='b',<455>,1:80]]}}, filters=[{name=id, table_ref=a}, {name=id, table_ref=b}, {name=tags, table_ref=a}, {name=tags, table_ref=b}], interface={id=[{name=id, table_ref=a}]}, table_alias={a=tab1, b=tab2}}}");
	}

	@Test
	public void snowflakeArraysOverlapHavingPlacementV0Test() {
		final String query = "SELECT a.id, count(*) AS c FROM tab1 a GROUP BY a.id HAVING arrays_overlap(a.tags, a.tags)";
		final SqlParseEventWalker extractor = runQuery(query);
		final Snippet snippet = extractor.getSnippet();
		Assert.assertTrue(
				"Expected positional warning in warning list",
				snippet.getErrorStringList(ParseDiagnostic.Severity.WARNING).toString()
						.contains("routine 'arrays_overlap' at (l:1 c:60)"));
		assertNoFatalErrors(extractor);
		assertOutputs(
				extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={function_name=COUNT, qualifier=null, parameters=*}, alias=c}}, having={function={function_name=arrays_overlap, parameters={1={column={name=tags, table_ref=a}}, 2={column={name=tags, table_ref=a}}}}}, from={table={alias=a, table=tab1}}, groupby={1={column={name=id, table_ref=a}}}}}",
				"[c, id]",
				"{}",
				"{tab1={*=[[@7,19:19='*',<291>,1:19]], id=[[@1,7:7='a',<455>,1:7], [@16,48:48='a',<455>,1:48]], tags=[[@22,75:75='a',<455>,1:75], [@26,83:83='a',<455>,1:83]]}}",
				"{query0={c=[[@10,25:25='c',<455>,1:25]], id=[[@3,9:10='id',<455>,1:9]]}}",
				"{def_query0={query_dictionary={c=[[@10,25:25='c',<455>,1:25]], id=[[@3,9:10='id',<455>,1:9]]}, table_dictionary={tab1={*=[[@7,19:19='*',<291>,1:19]], id=[[@1,7:7='a',<455>,1:7], [@16,48:48='a',<455>,1:48]], tags=[[@22,75:75='a',<455>,1:75], [@26,83:83='a',<455>,1:83]]}}, grouped_by=[{name=id, table_ref=a}], filters=[{name=tags, table_ref=a}], interface={c=[], id=[{name=id, table_ref=a}]}, table_alias={a=tab1}}}");
	}

	@Test
	public void snowflakeArrayIntersectionOrderByPlacementV0Test() {
		final String query = "SELECT a.id FROM tab1 a ORDER BY array_intersection(a.tags, a.tags)";
		final SqlParseEventWalker extractor = runQuery(query);
		final Snippet snippet = extractor.getSnippet();
		Assert.assertTrue(
				"Expected positional warning in warning list",
				snippet.getErrorStringList(ParseDiagnostic.Severity.WARNING).toString()
						.contains("routine 'array_intersection' at (l:1 c:33)"));
		assertNoFatalErrors(extractor);
		assertOutputs(
				extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}}, orderby={1={null_order=null, predicand={function={function_name=array_intersection, parameters={1={column={name=tags, table_ref=a}}, 2={column={name=tags, table_ref=a}}}}}, sort_order=ASC}}, from={table={alias=a, table=tab1}}}}",
				"[id]",
				"{}",
				"{tab1={id=[[@1,7:7='a',<455>,1:7]], tags=[[@11,52:52='a',<455>,1:52], [@15,60:60='a',<455>,1:60]]}}",
				"{query0={id=[[@3,9:10='id',<455>,1:9]]}}",
				"{def_query0={query_dictionary={id=[[@3,9:10='id',<455>,1:9]]}, table_dictionary={tab1={id=[[@1,7:7='a',<455>,1:7]], tags=[[@11,52:52='a',<455>,1:52], [@15,60:60='a',<455>,1:60]]}}, ordered_by=[{name=tags, table_ref=a}], interface={id=[{name=id, table_ref=a}]}, table_alias={a=tab1}}}");
	}

	@Test
	public void snowflakeArrayCatQualifyPlacementV0Test() {
		final String query =
				"SELECT a.id, row_number() over (partition by a.id) AS rn FROM tab1 a "
						+ "QUALIFY array_cat(a.tags, a.tags) IS NOT NULL";
		final SqlParseEventWalker extractor = runQuery(query);
		final Snippet snippet = extractor.getSnippet();
		Assert.assertTrue(
				"Expected positional warning in warning list",
				snippet.getErrorStringList(ParseDiagnostic.Severity.WARNING).toString()
						.contains("routine 'array_cat' at (l:1 c:77)"));
		assertNoFatalErrors(extractor);
		assertOutputs(
				extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={alias=rn, window_function={over={partition_by={1={column={name=id, table_ref=a}}}}, function={function_name=row_number, parameters=null}}}}, from={table={alias=a, table=tab1}}, qualify={condition={left={function={function_name=array_cat, parameters={1={column={name=tags, table_ref=a}}, 2={column={name=tags, table_ref=a}}}}}, operator=IS NOT NULL}}}}",
				"[id, rn]",
				"{}",
				"{tab1={id=[[@1,7:7='a',<455>,1:7], [@12,45:45='a',<455>,1:45]], tags=[[@24,87:87='a',<455>,1:87], [@28,95:95='a',<455>,1:95]]}}",
				"{query0={rn=[[@17,54:55='rn',<455>,1:54]], id=[[@3,9:10='id',<455>,1:9]]}}",
				"{def_query0={query_dictionary={id=[[@3,9:10='id',<455>,1:9]], rn=[[@17,54:55='rn',<455>,1:54]]}, table_dictionary={tab1={id=[[@1,7:7='a',<455>,1:7], [@12,45:45='a',<455>,1:45]], tags=[[@24,87:87='a',<455>,1:87], [@28,95:95='a',<455>,1:95]]}}, window_partition_by=[{name=id, table_ref=a}], filters=[{name=tags, table_ref=a}], interface={id=[{name=id, table_ref=a}], rn=[{name=id, table_ref=a}]}, table_alias={a=tab1}}}");
	}

	@Test
	public void snowflakeArrayExceptScriptStatementScopedWarningV0Test() {
		final String query =
				"SELECT array_except(a.tags, a.tags) AS diff_tags FROM tab1 a; "
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
				"routine 'array_except' at (l:1 c:7) is recognized as Snowflake-specific grammar.",
				null,
				1,
				7);
		assertOutputs(
				extractor,
				"{SCRIPT={1={select={1={function={function_name=array_except, parameters={1={column={name=tags, table_ref=a}}, 2={column={name=tags, table_ref=a}}}}, alias=diff_tags}}, from={table={alias=a, table=tab1}}}, 2={select={1={column={name=id, table_ref=a}}}, from={table={alias=a, table=tab1}}}}}",
				"[]",
				"{SCRIPT={1={}, 2={}}}",
				"{SCRIPT={1={tab1={tags=[[@3,20:20='a',<455>,1:20], [@7,28:28='a',<455>,1:28]]}}, 2={tab1={id=[[@18,69:69='a',<455>,1:69]]}}}}",
				"{SCRIPT={1={query0={diff_tags=[[@12,39:47='diff_tags',<455>,1:39]]}}, 2={query0={id=[[@20,71:72='id',<455>,1:71]]}}}}",
				"{SCRIPT={1={def_query0={query_dictionary={diff_tags=[[@12,39:47='diff_tags',<455>,1:39]]}, table_dictionary={tab1={tags=[[@3,20:20='a',<455>,1:20], [@7,28:28='a',<455>,1:28]]}}, interface={diff_tags=[{name=tags, table_ref=a}]}, table_alias={a=tab1}}}, 2={def_query0={query_dictionary={id=[[@20,71:72='id',<455>,1:71]]}, table_dictionary={tab1={id=[[@18,69:69='a',<455>,1:69]]}}, interface={id=[{name=id, table_ref=a}]}, table_alias={a=tab1}}}}}");
	}

	@Test
	public void snowflakeArrayCatWithSubstitutionArgumentV0Test() {
		final String query = "SELECT array_cat(a.tags, <rhs_arr>) AS merged_tags FROM tab1 a";
		final SqlParseEventWalker extractor = runQuery(query);
		final Snippet snippet = extractor.getSnippet();
		assertDiagnosticAtPosition(
				snippet,
				"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
				ParseDiagnostic.Severity.WARNING,
				"routine 'array_cat' at (l:1 c:7) is recognized as Snowflake-specific grammar.",
				null,
				1,
				7);
		assertNoFatalErrors(extractor);
		assertOutputs(
				extractor,
				"{SQL={select={1={function={function_name=array_cat, parameters={1={column={name=tags, table_ref=a}}, 2={substitution={name=<rhs_arr>, type=predicand}}}}, alias=merged_tags}}, from={table={alias=a, table=tab1}}}}",
				"[merged_tags]",
				"{<rhs_arr>=predicand}",
				"{tab1={tags=[[@3,17:17='a',<455>,1:17]]}}",
				"{query0={merged_tags=[[@10,39:49='merged_tags',<455>,1:39]]}}",
				"{def_query0={query_dictionary={merged_tags=[[@10,39:49='merged_tags',<455>,1:39]]}, table_dictionary={tab1={tags=[[@3,17:17='a',<455>,1:17]]}}, interface={merged_tags=[{name=tags, table_ref=a}, {name=<rhs_arr>, type=predicand}]}, table_alias={a=tab1}}}");
	}

	@Test
	public void snowflakeArrayCatWithLeftSubstitutionArgumentV0Test() {
		final String query = "SELECT array_cat(<lhs_arr>, a.tags) AS merged_tags FROM tab1 a";
		final SqlParseEventWalker extractor = runQuery(query);
		final Snippet snippet = extractor.getSnippet();
		assertDiagnosticAtPosition(
				snippet,
				"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
				ParseDiagnostic.Severity.WARNING,
				"routine 'array_cat' at (l:1 c:7) is recognized as Snowflake-specific grammar.",
				null,
				1,
				7);
		assertNoFatalErrors(extractor);
		Assert.assertEquals(
				"Substitution List is wrong",
				"{<lhs_arr>=predicand}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertTrue(
				"Interface should retain left substitution lineage",
				extractor.getSymbolTable().toString().contains("{name=<lhs_arr>, type=predicand}"));
	}

	@Test
	public void snowflakeArrayCatWithQualifiedColumnVariableArgumentsV0Test() {
		final String query = "SELECT array_cat(a.<lhs_col>, a.<rhs_col>) AS merged_tags FROM tab1 a";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		Assert.assertTrue(
				"Expected lhs qualified variable to be typed as column",
				extractor.getSubstitutionsMap().toString().contains("<lhs_col>=column"));
		Assert.assertTrue(
				"Expected rhs qualified variable to be typed as column",
				extractor.getSubstitutionsMap().toString().contains("<rhs_col>=column"));
		Assert.assertTrue(
				"Expected lineage to retain qualified column-variable typing",
				extractor.getSymbolTable().toString().contains("{substitution={name=<lhs_col>, type=column}, table_ref=a}")
						&& extractor.getSymbolTable().toString().contains("{substitution={name=<rhs_col>, type=column}, table_ref=a}"));
	}

	@Test
	public void snowflakeArrayCatSecondOperandMissingAliasFatalV0Test() {
		final String query = "SELECT array_cat(a.tags, z.tags) AS merged_tags FROM tab1 a";
		final SqlParseEventWalker extractor = runQuery(query);
		Assert.assertEquals("Expected one fatal walker message", 1, extractor.getSnippet().getFatalErrorStringList().size());
		Assert.assertTrue(
				"Expected missing alias fatal for z",
				extractor.getSnippet().getFatalErrorStringList().get(0).contains("No alias or table called 'z'"));
	}

	@Test
	public void snowflakeArraysOverlapAmbiguousUnqualifiedDiagnosticV0Test() {
		final String query = "SELECT arrays_overlap(tags, tags) AS overlap_flag FROM tab1 a JOIN tab2 b ON a.id = b.id";
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
				"{SQL={select={1={function={function_name=arrays_overlap, parameters={1={column={name=tags, table_ref=null}}, 2={column={name=tags, table_ref=null}}}}, alias=overlap_flag}}, from={join={1={table={alias=a, table=tab1}}, 2={join=JOIN, on={condition={left={column={name=id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=tab2}}}}}}",
				"[overlap_flag]",
				"{}",
				"{tab1={id=[[@16,77:77='a',<455>,1:77]]}, tab2={id=[[@20,84:84='b',<455>,1:84]]}}",
				"{query0={overlap_flag=[[@8,37:48='overlap_flag',<455>,1:37]]}}",
				"{def_query0={query_dictionary={overlap_flag=[[@8,37:48='overlap_flag',<455>,1:37]]}, table_dictionary={tab1={id=[[@16,77:77='a',<455>,1:77]]}, tab2={id=[[@20,84:84='b',<455>,1:84]]}}, filters=[{name=id, table_ref=a}, {name=id, table_ref=b}], interface={overlap_flag=[{name=tags, table_ref=null}]}, table_alias={a=tab1, b=tab2}}}");
	}

	@Test
	public void snowflakeArrayIntersectionCreateViewDdlPlacementV0Test() {
		final String query = "CREATE VIEW v_arr AS SELECT array_intersection(a.tags, a.tags) AS overlap_tags FROM tab1 a";
		final SQLSelectParserParser parser = parse(query);
		final SqlParseEventWalker extractor = runDdlParsertest(query, parser);
		final Snippet snippet = extractor.getSnippet();
		Assert.assertTrue(
				"Expected positional warning in warning list",
				snippet.getErrorStringList(ParseDiagnostic.Severity.WARNING).toString()
						.contains("routine 'array_intersection'"));
		assertNoFatalErrors(extractor);
	}
}
