package sql.walker;

import org.junit.Assert;
import org.junit.Test;

import access.Snippet;
import errorhandling.ParseDiagnostic;
import sql.SQLSelectParserParser;

public class SqlEventWalkerSnowflakeArrayMutatorTests extends AbstractSqlParseEventWalkerTest {

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
	public void snowflakeArrayAppendSelectPlacementV0Test() {
		final String query =
				"SELECT array_append(a.tags, b.tags) AS appended_tags "
						+ "FROM tab1 a JOIN tab2 b ON a.id = b.id";
		final SqlParseEventWalker extractor = runQuery(query);
		final Snippet snippet = extractor.getSnippet();
		assertDiagnosticAtPosition(
				snippet,
				"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
				ParseDiagnostic.Severity.WARNING,
				"routine 'array_append' at (l:1 c:7) is recognized as Snowflake-specific grammar.",
				null,
				1,
				7);
		assertNoFatalErrors(extractor);
		assertOutputs(
				extractor,
				"{SQL={select={1={function={function_name=array_append, parameters={1={column={name=tags, table_ref=a}}, 2={column={name=tags, table_ref=b}}}}, alias=appended_tags}}, from={join={1={table={alias=a, table=tab1}}, 2={join=JOIN, on={condition={left={column={name=id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=tab2}}}}}}",
				"[appended_tags]",
				"{}",
				"{tab1={id=[[@20,80:80='a',<459>,1:80]], tags=[[@3,20:20='a',<459>,1:20]]}, tab2={id=[[@24,87:87='b',<459>,1:87]], tags=[[@7,28:28='b',<459>,1:28]]}}",
				"{query0={appended_tags=[[@12,39:51='appended_tags',<459>,1:39]]}}",
				"{def_query0={query_dictionary={appended_tags=[[@12,39:51='appended_tags',<459>,1:39]]}, table_dictionary={tab1={id=[[@20,80:80='a',<459>,1:80]], tags=[[@3,20:20='a',<459>,1:20]]}, tab2={id=[[@24,87:87='b',<459>,1:87]], tags=[[@7,28:28='b',<459>,1:28]]}}, filters=[{name=id, table_ref=a}, {name=id, table_ref=b}], interface={appended_tags=[{name=tags, table_ref=a}, {name=tags, table_ref=b}]}, table_alias={a=tab1, b=tab2}}}");
	}

	@Test
	public void snowflakeArrayInsertSelectPlacementV0Test() {
		final String query =
				"SELECT array_insert(a.tags, 0, b.tags) AS inserted_tags "
						+ "FROM tab1 a JOIN tab2 b ON a.id = b.id";
		final SqlParseEventWalker extractor = runQuery(query);
		final Snippet snippet = extractor.getSnippet();
		assertDiagnosticAtPosition(
				snippet,
				"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
				ParseDiagnostic.Severity.WARNING,
				"routine 'array_insert' at (l:1 c:7) is recognized as Snowflake-specific grammar.",
				null,
				1,
				7);
		assertNoFatalErrors(extractor);
		assertOutputs(
				extractor,
				"{SQL={select={1={function={function_name=array_insert, parameters={1={column={name=tags, table_ref=a}}, 2={literal=0}, 3={column={name=tags, table_ref=b}}}}, alias=inserted_tags}}, from={join={1={table={alias=a, table=tab1}}, 2={join=JOIN, on={condition={left={column={name=id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=tab2}}}}}}",
				"[inserted_tags]",
				"{}",
				"{tab1={id=[[@22,83:83='a',<459>,1:83]], tags=[[@3,20:20='a',<459>,1:20]]}, tab2={id=[[@26,90:90='b',<459>,1:90]], tags=[[@9,31:31='b',<459>,1:31]]}}",
				"{query0={inserted_tags=[[@14,42:54='inserted_tags',<459>,1:42]]}}",
				"{def_query0={query_dictionary={inserted_tags=[[@14,42:54='inserted_tags',<459>,1:42]]}, table_dictionary={tab1={id=[[@22,83:83='a',<459>,1:83]], tags=[[@3,20:20='a',<459>,1:20]]}, tab2={id=[[@26,90:90='b',<459>,1:90]], tags=[[@9,31:31='b',<459>,1:31]]}}, filters=[{name=id, table_ref=a}, {name=id, table_ref=b}], interface={inserted_tags=[{name=tags, table_ref=a}, {name=tags, table_ref=b}]}, table_alias={a=tab1, b=tab2}}}");
	}

	@Test
	public void snowflakeArrayRemoveAtWherePlacementV0Test() {
		final String query = "SELECT a.id FROM tab1 a WHERE array_remove_at(a.tags, 0) IS NOT NULL";
		final SqlParseEventWalker extractor = runQuery(query);
		final Snippet snippet = extractor.getSnippet();
		assertDiagnosticAtPosition(
				snippet,
				"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
				ParseDiagnostic.Severity.WARNING,
				"routine 'array_remove_at' at (l:1 c:30) is recognized as Snowflake-specific grammar.",
				null,
				1,
				30);
		assertNoFatalErrors(extractor);
		assertOutputs(
				extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}}, from={table={alias=a, table=tab1}}, where={condition={left={function={function_name=array_remove_at, parameters={1={column={name=tags, table_ref=a}}, 2={literal=0}}}}, operator=IS NOT NULL}}}}",
				"[id]",
				"{}",
				"{tab1={id=[[@1,7:7='a',<459>,1:7]], tags=[[@10,46:46='a',<459>,1:46]]}}",
				"{query0={id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={tab1={id=[[@1,7:7='a',<459>,1:7]], tags=[[@10,46:46='a',<459>,1:46]]}}, filters=[{name=tags, table_ref=a}], interface={id=[{name=id, table_ref=a}]}, table_alias={a=tab1}}}");
	}

	@Test
	public void snowflakeArrayPrependWithSubstitutionArgumentV0Test() {
		final String query = "SELECT array_prepend(<lhs_arr>, a.tags) AS prepended_tags FROM tab1 a";
		final SqlParseEventWalker extractor = runQuery(query);
		final Snippet snippet = extractor.getSnippet();
		assertDiagnosticAtPosition(
				snippet,
				"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
				ParseDiagnostic.Severity.WARNING,
				"routine 'array_prepend' at (l:1 c:7) is recognized as Snowflake-specific grammar.",
				null,
				1,
				7);
		assertNoFatalErrors(extractor);
		assertOutputs(
				extractor,
				"{SQL={select={1={function={function_name=array_prepend, parameters={1={substitution={name=<lhs_arr>, type=predicand}}, 2={column={name=tags, table_ref=a}}}}, alias=prepended_tags}}, from={table={alias=a, table=tab1}}}}",
				"[prepended_tags]",
				"{<lhs_arr>=predicand}",
				"{tab1={tags=[[@5,32:32='a',<459>,1:32]]}}",
				"{query0={prepended_tags=[[@10,43:56='prepended_tags',<459>,1:43]]}}",
				"{def_query0={query_dictionary={prepended_tags=[[@10,43:56='prepended_tags',<459>,1:43]]}, table_dictionary={tab1={tags=[[@5,32:32='a',<459>,1:32]]}}, interface={prepended_tags=[{name=<lhs_arr>, type=predicand}, {name=tags, table_ref=a}]}, table_alias={a=tab1}}}");
	}

	@Test
	public void snowflakeArrayRemoveScriptStatementScopedWarningV0Test() {
		final String query =
				"SELECT array_remove(a.tags, a.tags) AS removed_tags FROM tab1 a; "
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
				"routine 'array_remove' at (l:1 c:7) is recognized as Snowflake-specific grammar.",
				null,
				1,
				7);
		assertOutputs(
				extractor,
				"{SCRIPT={1={select={1={function={function_name=array_remove, parameters={1={column={name=tags, table_ref=a}}, 2={column={name=tags, table_ref=a}}}}, alias=removed_tags}}, from={table={alias=a, table=tab1}}}, 2={select={1={column={name=id, table_ref=a}}}, from={table={alias=a, table=tab1}}}}}",
				"[]",
				"{SCRIPT={1={}, 2={}}}",
				"{SCRIPT={1={tab1={tags=[[@3,20:20='a',<459>,1:20], [@7,28:28='a',<459>,1:28]]}}, 2={tab1={id=[[@18,72:72='a',<459>,1:72]]}}}}",
				"{SCRIPT={1={query0={removed_tags=[[@12,39:50='removed_tags',<459>,1:39]]}}, 2={query0={id=[[@20,74:75='id',<459>,1:74]]}}}}",
				"{SCRIPT={1={def_query0={query_dictionary={removed_tags=[[@12,39:50='removed_tags',<459>,1:39]]}, table_dictionary={tab1={tags=[[@3,20:20='a',<459>,1:20], [@7,28:28='a',<459>,1:28]]}}, interface={removed_tags=[{name=tags, table_ref=a}]}, table_alias={a=tab1}}}, 2={def_query0={query_dictionary={id=[[@20,74:75='id',<459>,1:74]]}, table_dictionary={tab1={id=[[@18,72:72='a',<459>,1:72]]}}, interface={id=[{name=id, table_ref=a}]}, table_alias={a=tab1}}}}}");
	}

	@Test
	public void snowflakeArrayAppendQualifiedMissingAliasFatalV0Test() {
		final String query = "SELECT array_append(z.tags, a.tags) AS appended_tags FROM tab1 a";
		final SqlParseEventWalker extractor = runQuery(query);
		assertSingleMissingAliasFatal(extractor);
	}

	@Test
	public void snowflakeArrayAppendSecondOperandMissingAliasFatalV0Test() {
		final String query = "SELECT array_append(a.tags, z.tags) AS appended_tags FROM tab1 a";
		final SqlParseEventWalker extractor = runQuery(query);
		assertSingleMissingAliasFatal(extractor);
	}

	@Test
	public void snowflakeArrayPrependSecondOperandMissingAliasFatalV0Test() {
		final String query = "SELECT array_prepend(a.tags, z.tags) AS prepended_tags FROM tab1 a";
		final SqlParseEventWalker extractor = runQuery(query);
		assertSingleMissingAliasFatal(extractor);
	}

	@Test
	public void snowflakeArrayInsertFirstOperandMissingAliasFatalV0Test() {
		final String query = "SELECT array_insert(z.tags, 0, a.tags) AS inserted_tags FROM tab1 a";
		final SqlParseEventWalker extractor = runQuery(query);
		assertSingleMissingAliasFatal(extractor);
	}

	@Test
	public void snowflakeArrayInsertSecondOperandMissingAliasFatalV0Test() {
		final String query = "SELECT array_insert(a.tags, z.idx, a.tags) AS inserted_tags FROM tab1 a";
		final SqlParseEventWalker extractor = runQuery(query);
		assertSingleMissingAliasFatal(extractor);
	}

	@Test
	public void snowflakeArrayInsertThirdOperandMissingAliasFatalV0Test() {
		final String query = "SELECT array_insert(a.tags, 0, z.tags) AS inserted_tags FROM tab1 a";
		final SqlParseEventWalker extractor = runQuery(query);
		assertSingleMissingAliasFatal(extractor);
	}

	@Test
	public void snowflakeArrayRemoveFirstOperandMissingAliasFatalV0Test() {
		final String query = "SELECT array_remove(z.tags, a.tags) AS removed_tags FROM tab1 a";
		final SqlParseEventWalker extractor = runQuery(query);
		assertSingleMissingAliasFatal(extractor);
	}

	@Test
	public void snowflakeArrayRemoveSecondOperandMissingAliasFatalV0Test() {
		final String query = "SELECT array_remove(a.tags, z.tags) AS removed_tags FROM tab1 a";
		final SqlParseEventWalker extractor = runQuery(query);
		assertSingleMissingAliasFatal(extractor);
	}

	@Test
	public void snowflakeArrayRemoveAtFirstOperandMissingAliasFatalV0Test() {
		final String query = "SELECT array_remove_at(z.tags, 0) AS removed_tags FROM tab1 a";
		final SqlParseEventWalker extractor = runQuery(query);
		assertSingleMissingAliasFatal(extractor);
	}

	@Test
	public void snowflakeArrayRemoveAtSecondOperandMissingAliasFatalV0Test() {
		final String query = "SELECT array_remove_at(a.tags, z.idx) AS removed_tags FROM tab1 a";
		final SqlParseEventWalker extractor = runQuery(query);
		assertSingleMissingAliasFatal(extractor);
	}

	@Test
	public void snowflakeArrayAppendAmbiguousUnqualifiedDiagnosticV0Test() {
		final String query = "SELECT array_append(tags, tags) AS appended_tags FROM tab1 a JOIN tab2 b ON a.id = b.id";
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
				"{SQL={select={1={function={function_name=array_append, parameters={1={column={name=tags, table_ref=null}}, 2={column={name=tags, table_ref=null}}}}, alias=appended_tags}}, from={join={1={table={alias=a, table=tab1}}, 2={join=JOIN, on={condition={left={column={name=id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=tab2}}}}}}",
				"[appended_tags]",
				"{}",
				"{tab1={id=[[@16,76:76='a',<459>,1:76]]}, tab2={id=[[@20,83:83='b',<459>,1:83]]}}",
				"{query0={appended_tags=[[@8,35:47='appended_tags',<459>,1:35]]}}",
				"{def_query0={query_dictionary={appended_tags=[[@8,35:47='appended_tags',<459>,1:35]]}, table_dictionary={tab1={id=[[@16,76:76='a',<459>,1:76]]}, tab2={id=[[@20,83:83='b',<459>,1:83]]}}, filters=[{name=id, table_ref=a}, {name=id, table_ref=b}], interface={appended_tags=[{name=tags, table_ref=null}]}, table_alias={a=tab1, b=tab2}}}");
	}

	@Test
	public void snowflakeArrayInsertCreateViewDdlPlacementV0Test() {
		final String query = "CREATE VIEW v_arr AS SELECT array_insert(a.tags, 0, a.tags) AS inserted_tags FROM tab1 a";
		final SQLSelectParserParser parser = parse(query);
		final SqlParseEventWalker extractor = runDdlParsertest(query, parser);
		final Snippet snippet = extractor.getSnippet();
		assertDiagnosticAtPosition(
				snippet,
				"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
				ParseDiagnostic.Severity.WARNING,
				"routine 'array_insert' at (l:1 c:28) is recognized as Snowflake-specific grammar.",
				null,
				1,
				28);
		assertNoFatalErrors(extractor);
		assertOutputs(
				extractor,
				"{DDL={create={type=VIEW, name={table=v_arr}, query={select={1={function={function_name=array_insert, parameters={1={column={name=tags, table_ref=a}}, 2={literal=0}, 3={column={name=tags, table_ref=a}}}}, alias=inserted_tags}}, from={table={alias=a, table=tab1}}}}}}",
				"[]",
				"{}",
				"{tab1={tags=[[@7,41:41='a',<459>,1:41], [@13,52:52='a',<459>,1:52]]}}",
				"{query0={inserted_tags=[[@18,63:75='inserted_tags',<459>,1:63]]}}",
				"{def_create1={def_query0={query_dictionary={inserted_tags=[[@18,63:75='inserted_tags',<459>,1:63]]}, table_dictionary={tab1={tags=[[@7,41:41='a',<459>,1:41], [@13,52:52='a',<459>,1:52]]}}, interface={inserted_tags=[{name=tags, table_ref=a}]}, table_alias={a=tab1}}}}");
	}

	@Test
	public void snowflakeArrayAppendWithSubstitutionArgumentsV0Test() {
		final String query = "SELECT array_append(<lhs_arr>, <rhs_arr>) AS appended_tags FROM tab1 a";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		Assert.assertTrue(
				"Expected lhs substitution to be tracked",
				extractor.getSubstitutionsMap().toString().contains("<lhs_arr>=predicand"));
		Assert.assertTrue(
				"Expected rhs substitution to be tracked",
				extractor.getSubstitutionsMap().toString().contains("<rhs_arr>=predicand"));
		Assert.assertTrue(
				"Expected interface lineage to retain both substitutions",
				extractor.getSymbolTable().toString().contains("{name=<lhs_arr>, type=predicand}")
						&& extractor.getSymbolTable().toString().contains("{name=<rhs_arr>, type=predicand}"));
	}

	@Test
	public void snowflakeArrayInsertWithSubstitutionArgumentsV0Test() {
		final String query = "SELECT array_insert(<arr_val>, <insert_pos>, <insert_val>) AS inserted_tags FROM tab1 a";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		Assert.assertTrue(
				"Expected array substitution to be tracked",
				extractor.getSubstitutionsMap().toString().contains("<arr_val>=predicand"));
		Assert.assertTrue(
				"Expected position substitution to be tracked",
				extractor.getSubstitutionsMap().toString().contains("<insert_pos>=predicand"));
		Assert.assertTrue(
				"Expected insert-value substitution to be tracked",
				extractor.getSubstitutionsMap().toString().contains("<insert_val>=predicand"));
		Assert.assertTrue(
				"Expected interface lineage to retain all insert substitutions",
				extractor.getSymbolTable().toString().contains("{name=<arr_val>, type=predicand}")
						&& extractor.getSymbolTable().toString().contains("{name=<insert_pos>, type=predicand}")
						&& extractor.getSymbolTable().toString().contains("{name=<insert_val>, type=predicand}"));
	}

	@Test
	public void snowflakeArrayInsertWithQualifiedColumnVariablesV0Test() {
		final String query =
				"SELECT array_insert(a.<arr_col>, a.<idx_col>, <insert_val>) AS inserted_tags FROM tab1 a";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		Assert.assertTrue(
				"Expected array variable with table prefix to be typed as column",
				extractor.getSubstitutionsMap().toString().contains("<arr_col>=column"));
		Assert.assertTrue(
				"Expected index variable with table prefix to be typed as column",
				extractor.getSubstitutionsMap().toString().contains("<idx_col>=column"));
		Assert.assertTrue(
				"Expected unqualified insert variable to remain predicand",
				extractor.getSubstitutionsMap().toString().contains("<insert_val>=predicand"));
		Assert.assertTrue(
				"Expected lineage to retain column typing for qualified variables",
				extractor.getSymbolTable().toString().contains("{substitution={name=<arr_col>, type=column}, table_ref=a}")
						&& extractor.getSymbolTable().toString().contains("{substitution={name=<idx_col>, type=column}, table_ref=a}")
						&& extractor.getSymbolTable().toString().contains("{name=<insert_val>, type=predicand}"));
	}

	@Test
	public void snowflakeArrayRemoveAtWithSubstitutionArgumentsV0Test() {
		final String query = "SELECT array_remove_at(<arr_val>, <remove_pos>) AS removed_tags FROM tab1 a";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		Assert.assertTrue(
				"Expected array substitution to be tracked",
				extractor.getSubstitutionsMap().toString().contains("<arr_val>=predicand"));
		Assert.assertTrue(
				"Expected remove-position substitution to be tracked",
				extractor.getSubstitutionsMap().toString().contains("<remove_pos>=predicand"));
		Assert.assertTrue(
				"Expected interface lineage to retain remove_at substitutions",
				extractor.getSymbolTable().toString().contains("{name=<arr_val>, type=predicand}")
						&& extractor.getSymbolTable().toString().contains("{name=<remove_pos>, type=predicand}"));
	}

	@Test
	public void snowflakeArrayMutatorsWarningEmittedOncePerStatementV0Test() {
		final String query =
				"SELECT array_append(a.tags, b.tags) AS appended_tags, "
						+ "array_remove(a.tags, b.tags) AS removed_tags "
						+ "FROM tab1 a JOIN tab2 b ON a.id = b.id";
		final SqlParseEventWalker extractor = runQuery(query);
		assertDiagnosticCountBySeverity(
				extractor.getSnippet(),
				"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
				ParseDiagnostic.Severity.WARNING,
				null,
				null,
				1);
		assertOutputs(
				extractor,
				"{SQL={select={1={function={function_name=array_append, parameters={1={column={name=tags, table_ref=a}}, 2={column={name=tags, table_ref=b}}}}, alias=appended_tags}, 2={function={function_name=array_remove, parameters={1={column={name=tags, table_ref=a}}, 2={column={name=tags, table_ref=b}}}}, alias=removed_tags}}, from={join={1={table={alias=a, table=tab1}}, 2={join=JOIN, on={condition={left={column={name=id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=tab2}}}}}}",
				"[appended_tags, removed_tags]",
				"{}",
				"{tab1={id=[[@33,126:126='a',<459>,1:126]], tags=[[@3,20:20='a',<459>,1:20], [@16,67:67='a',<459>,1:67]]}, tab2={id=[[@37,133:133='b',<459>,1:133]], tags=[[@7,28:28='b',<459>,1:28], [@20,75:75='b',<459>,1:75]]}}",
				"{query0={removed_tags=[[@25,86:97='removed_tags',<459>,1:86]], appended_tags=[[@12,39:51='appended_tags',<459>,1:39]]}}",
				"{def_query0={query_dictionary={appended_tags=[[@12,39:51='appended_tags',<459>,1:39]], removed_tags=[[@25,86:97='removed_tags',<459>,1:86]]}, table_dictionary={tab1={id=[[@33,126:126='a',<459>,1:126]], tags=[[@3,20:20='a',<459>,1:20], [@16,67:67='a',<459>,1:67]]}, tab2={id=[[@37,133:133='b',<459>,1:133]], tags=[[@7,28:28='b',<459>,1:28], [@20,75:75='b',<459>,1:75]]}}, filters=[{name=id, table_ref=a}, {name=id, table_ref=b}], interface={appended_tags=[{name=tags, table_ref=a}, {name=tags, table_ref=b}], removed_tags=[{name=tags, table_ref=a}, {name=tags, table_ref=b}]}, table_alias={a=tab1, b=tab2}}}");
	}
}
