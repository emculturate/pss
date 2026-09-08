package sql.walker;

import org.junit.Assert;
import org.junit.Test;

import access.Snippet;
import errorhandling.ParseDiagnostic;
import sql.SQLSelectParserParser;

/**
 * Ordered intra-SELECT-list output-alias references: a prior select-item alias may
 * appear anywhere a column reference is valid in a later select-list item, provided
 * the alias is defined earlier in source order.
 */
public class SqlEventWalkerSelectListOrderedAliasRefTests extends AbstractSqlParseEventWalkerTest {

	private static final String PRIOR_ALIAS = "prior_alias";
	private static final String NOT_IN_QUERY_ALIASES = "UNQUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIASES";

	private SqlParseEventWalker parseHappyPath(String query) {
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		assertNoFatalErrors(extractor);
		return extractor;
	}

	private void assertWalkerGoldenOutputs(SqlParseEventWalker extractor, String expectedAst,
			String expectedInterface, String expectedSubstitutions, String expectedTableDictionary,
			String expectedQueryColumnDictionary, String expectedSymbolTable) {
		Assert.assertEquals("AST is wrong", expectedAst, extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", expectedInterface, extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", expectedSubstitutions,
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", expectedTableDictionary,
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", expectedQueryColumnDictionary,
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", expectedSymbolTable,
				extractor.getSymbolTable().toString());
	}

	private void assertPriorAliasBoundToQueryScope(SqlParseEventWalker extractor) {
		String symbolTable = extractor.getSymbolTable().toString();
		Assert.assertTrue(
				"expected prior_alias lineage via queryN in symbol table: " + symbolTable,
				symbolTable.matches("(?s).*\\{name=" + PRIOR_ALIAS + ", table_ref=query\\d+\\}.*"));
	}

	private void assertWindowPartitionByPriorAlias(SqlParseEventWalker extractor) {
		String symbolTable = extractor.getSymbolTable().toString();
		Assert.assertTrue(
				"expected window_partition_by prior_alias@queryN: " + symbolTable,
				symbolTable.contains("window_partition_by=[")
						&& symbolTable.matches("(?s).*window_partition_by=\\[.*\\{name="
								+ PRIOR_ALIAS + ", table_ref=query\\d+\\}.*"));
	}

	private void assertWindowOrderedByPriorAlias(SqlParseEventWalker extractor) {
		String symbolTable = extractor.getSymbolTable().toString();
		Assert.assertTrue(
				"expected window_ordered_by prior_alias@queryN: " + symbolTable,
				symbolTable.contains("window_ordered_by=[")
						&& symbolTable.matches("(?s).*window_ordered_by=\\[.*\\{name="
								+ PRIOR_ALIAS + ", table_ref=query\\d+\\}.*"));
	}

	private void assertPriorAliasForwardReferenceUnresolved(String query) {
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		String symbolTable = extractor.getSymbolTable().toString();
		Assert.assertTrue(
				"expected forward hop to keep table_ref=null: " + symbolTable,
				symbolTable.contains("{name=" + PRIOR_ALIAS + ", table_ref=null}"));
		Assert.assertFalse(
				"forward window hop must not stamp queryN: " + symbolTable,
				symbolTable.matches("(?s).*window_partition_by=\\[.*\\{name="
						+ PRIOR_ALIAS + ", table_ref=query\\d+\\}.*")
						|| symbolTable.matches("(?s).*window_ordered_by=\\[.*\\{name="
								+ PRIOR_ALIAS + ", table_ref=query\\d+\\}.*"));
		Snippet snippet = extractor.getSnippet();
		ParseDiagnostic fatal = findFatalDiagnosticByCodeAndFragment(
				snippet, NOT_IN_QUERY_ALIASES, PRIOR_ALIAS);
		Assert.assertNotNull("expected fatal for forward prior_alias ref", fatal);
	}

	// --- origin item types (first select-list entry) ---

	@Test
	public void orderedAliasFromPlainColumnInArithmeticConsumerTest() {
		SqlParseEventWalker extractor = parseHappyPath(
				"SELECT a AS prior_alias, prior_alias + 1 AS nxt FROM tab1");
		assertPriorAliasBoundToQueryScope(extractor);
	}

	@Test
	public void orderedAliasFromArithmeticExpressionInFunctionConsumerTest() {
		SqlParseEventWalker extractor = parseHappyPath(
				"SELECT a + b AS prior_alias, TRIM(prior_alias) AS nxt FROM tab1");
		assertPriorAliasBoundToQueryScope(extractor);
	}

	@Test
	public void orderedAliasFromFunctionExpressionInCalcConsumerTest() {
		SqlParseEventWalker extractor = parseHappyPath(
				"SELECT LOWER(a) AS prior_alias, prior_alias || 'x' AS nxt FROM tab1");
		assertPriorAliasBoundToQueryScope(extractor);
	}

	@Test
	public void orderedAliasFromCaseExpressionInCalcConsumerTest() {
		SqlParseEventWalker extractor = parseHappyPath(
				"SELECT CASE WHEN a > 0 THEN a ELSE b END AS prior_alias, prior_alias + 1 AS nxt FROM tab1");
		assertPriorAliasBoundToQueryScope(extractor);
	}

	@Test
	public void orderedAliasFromCastExpressionInFunctionConsumerTest() {
		SqlParseEventWalker extractor = parseHappyPath(
				"SELECT CAST(a AS VARCHAR) AS prior_alias, TRIM(prior_alias) AS nxt FROM tab1");
		assertPriorAliasBoundToQueryScope(extractor);
	}

	@Test
	public void orderedAliasFromPredicandSubstitutionInCalcConsumerTest() {
		SqlParseEventWalker extractor = parseHappyPath(
				"SELECT <partner_name> AS prior_alias, prior_alias || 'z' AS nxt FROM tab1");
		assertPriorAliasBoundToQueryScope(extractor);
	}

	@Test
	public void orderedAliasFromComparisonPredicandInCalcConsumerTest() {
		SqlParseEventWalker extractor = parseHappyPath(
				"SELECT <a> >= <b> AS prior_alias, prior_alias + 0 AS nxt FROM tab1");
		assertPriorAliasBoundToQueryScope(extractor);
	}

	@Test
	public void orderedAliasFromBareValueInCalcConsumerTest() {
		SqlParseEventWalker extractor = parseHappyPath(
				"SELECT CURRENT_DATE AS prior_alias, prior_alias AS nxt FROM tab1");
		// bare-value origin is grounded; consumer re-reference stays on query scope
		assertPriorAliasBoundToQueryScope(extractor);
	}

	@Test
	public void orderedAliasFromColumnSubstitutionInWindowPartitionByTest() {
		SqlParseEventWalker extractor = parseHappyPath(
				"SELECT <email_col> AS prior_alias, ROW_NUMBER() OVER (PARTITION BY prior_alias) AS rn FROM tab1");
		assertWindowPartitionByPriorAlias(extractor);
		assertPriorAliasBoundToQueryScope(extractor);
	}

	@Test
	public void orderedAliasFromBooleanConditionSubstitutionInFunctionConsumerTest() {
		SqlParseEventWalker extractor = parseHappyPath(
				"SELECT <a> AND <b> AS prior_alias, TRIM(prior_alias) AS nxt FROM tab1");
		assertPriorAliasBoundToQueryScope(extractor);
	}

	@Test
	public void orderedAliasFromScalarSubqueryInArithmeticConsumerTest() {
		SqlParseEventWalker extractor = parseHappyPath(
				"SELECT (SELECT max(x) FROM t2) AS prior_alias, prior_alias + 1 AS nxt FROM t1");
		assertWalkerGoldenOutputs(extractor,
				"{SQL={select={1={lookup={from={table={alias=null, table=t2}}, select={1={function={function_name=max, qualifier=null, parameters={column={name=x, table_ref=null}}}}}}, alias=prior_alias}, 2={alias=nxt, calc={left={column={name=prior_alias, table_ref=null}}, right={literal=1}, operator=+}}}, from={table={alias=null, table=t1}}}}",
				"[prior_alias, nxt]",
				"{}",
				"{t1={}, t2={x=[[@5,19:19='x',<393>,1:19]]}}",
				"{query0={unnamed_0=[[@6,20:20=')',<288>,1:20]]}, query2={prior_alias=[[@11,34:44='prior_alias',<393>,1:34], [@13,47:57='prior_alias',<393>,1:47]], nxt=[[@17,66:68='nxt',<393>,1:66]]}}",
				"{def_query2={query_dictionary={prior_alias=[[@11,34:44='prior_alias',<393>,1:34], [@13,47:57='prior_alias',<393>,1:47]], nxt=[[@17,66:68='nxt',<393>,1:66]]}, table_dictionary={t1={}}, dependent_queries={predicand1={query=query0, type=interface}}, def_query0={query_dictionary={unnamed_0=[[@6,20:20=')',<288>,1:20]]}, table_dictionary={t2={x=[[@5,19:19='x',<393>,1:19]]}}, interface={unnamed_0=[{name=x, table_ref=t2}]}}, interface={prior_alias=[{name=x, table_ref=null}], nxt=[{name=prior_alias, table_ref=query2}]}}}");
	}

	@Test
	public void orderedAliasFromScalarSubqueryInWindowPartitionByTest() {
		SqlParseEventWalker extractor = parseHappyPath(
				"SELECT (SELECT max(x) FROM t2) AS prior_alias, ROW_NUMBER() OVER (PARTITION BY prior_alias) AS rn FROM t1");
		assertWalkerGoldenOutputs(extractor,
				"{SQL={select={1={lookup={from={table={alias=null, table=t2}}, select={1={function={function_name=max, qualifier=null, parameters={column={name=x, table_ref=null}}}}}}, alias=prior_alias}, 2={alias=rn, window_function={over={partition_by={1={column={name=prior_alias, table_ref=null}}}}, function={function_name=ROW_NUMBER, parameters=null}}}}, from={table={alias=null, table=t1}}}}",
				"[prior_alias, rn]",
				"{}",
				"{t1={}, t2={x=[[@5,19:19='x',<393>,1:19]]}}",
				"{query0={unnamed_0=[[@6,20:20=')',<288>,1:20]]}, query2={rn=[[@23,95:96='rn',<393>,1:95]], prior_alias=[[@11,34:44='prior_alias',<393>,1:34], [@20,79:89='prior_alias',<393>,1:79]]}}",
				"{def_query2={query_dictionary={prior_alias=[[@11,34:44='prior_alias',<393>,1:34], [@20,79:89='prior_alias',<393>,1:79]], rn=[[@23,95:96='rn',<393>,1:95]]}, table_dictionary={t1={}}, window_partition_by=[{name=prior_alias, table_ref=query2}], dependent_queries={predicand1={query=query0, type=interface}}, def_query0={query_dictionary={unnamed_0=[[@6,20:20=')',<288>,1:20]]}, table_dictionary={t2={x=[[@5,19:19='x',<393>,1:19]]}}, interface={unnamed_0=[{name=x, table_ref=t2}]}}, interface={prior_alias=[{name=x, table_ref=null}], rn=[{name=prior_alias, table_ref=query2}]}}}");
	}

	// --- column-ref sites inside later select-list items ---

	@Test
	public void orderedAliasReferencedInWindowPartitionByTest() {
		SqlParseEventWalker extractor = parseHappyPath(
				"SELECT a AS prior_alias, ROW_NUMBER() OVER (PARTITION BY prior_alias) AS rn FROM tab1");
		assertWindowPartitionByPriorAlias(extractor);
		assertPriorAliasBoundToQueryScope(extractor);
	}

	@Test
	public void orderedAliasReferencedInWindowOrderByTest() {
		SqlParseEventWalker extractor = parseHappyPath(
				"SELECT a AS prior_alias, ROW_NUMBER() OVER (ORDER BY prior_alias) AS rn FROM tab1");
		assertWindowOrderedByPriorAlias(extractor);
		assertPriorAliasBoundToQueryScope(extractor);
	}

	@Test
	public void orderedAliasReferencedInWindowPartitionByAndOrderByTest() {
		SqlParseEventWalker extractor = parseHappyPath(
				"SELECT a AS prior_alias, ROW_NUMBER() OVER (PARTITION BY a ORDER BY prior_alias) AS rn FROM tab1");
		assertWindowOrderedByPriorAlias(extractor);
		assertPriorAliasBoundToQueryScope(extractor);
	}

	@Test
	public void orderedPredicandAliasReferencedInWindowPartitionByTest() {
		SqlParseEventWalker extractor = parseHappyPath(
				"SELECT <partner_name> AS prior_alias, ROW_NUMBER() OVER (PARTITION BY prior_alias) AS rn FROM tab1");
		assertWindowPartitionByPriorAlias(extractor);
		assertPriorAliasBoundToQueryScope(extractor);
	}

	@Test
	public void orderedPredicandAliasReferencedInWindowOrderByTest() {
		SqlParseEventWalker extractor = parseHappyPath(
				"SELECT <partner_name> AS prior_alias, ROW_NUMBER() OVER (ORDER BY prior_alias) AS rn FROM tab1");
		assertWindowOrderedByPriorAlias(extractor);
		assertPriorAliasBoundToQueryScope(extractor);
	}

	@Test
	public void orderedAliasReferencedInAggregateWindowFunctionParameterTest() {
		SqlParseEventWalker extractor = parseHappyPath(
				"SELECT a AS prior_alias, SUM(b) OVER (PARTITION BY prior_alias ORDER BY prior_alias) AS s FROM tab1");
		assertWindowPartitionByPriorAlias(extractor);
		assertWindowOrderedByPriorAlias(extractor);
		assertPriorAliasBoundToQueryScope(extractor);
	}

	// --- forward references (consumer before defining select-list item) ---

	@Test
	public void orderedAliasForwardRefFromPlainColumnUnresolvedTest() {
		assertPriorAliasForwardReferenceUnresolved(
				"SELECT prior_alias + 1 AS nxt, a AS prior_alias FROM (select a, b from tab1)");
	}

	@Test
	public void orderedAliasForwardRefFromArithmeticExpressionUnresolvedTest() {
		assertPriorAliasForwardReferenceUnresolved(
				"SELECT TRIM(prior_alias) AS nxt, a + b AS prior_alias FROM (select a, b from tab1)");
	}

	@Test
	public void orderedAliasForwardRefFromFunctionExpressionUnresolvedTest() {
		assertPriorAliasForwardReferenceUnresolved(
				"SELECT prior_alias || 'x' AS nxt, LOWER(a) AS prior_alias FROM (select a, b from tab1)");
	}

	@Test
	public void orderedAliasForwardRefFromCaseExpressionUnresolvedTest() {
		assertPriorAliasForwardReferenceUnresolved(
				"SELECT prior_alias + 1 AS nxt, CASE WHEN a > 0 THEN a ELSE b END AS prior_alias FROM (select a, b from tab1)");
	}

	@Test
	public void orderedAliasForwardRefFromCastExpressionUnresolvedTest() {
		assertPriorAliasForwardReferenceUnresolved(
				"SELECT TRIM(prior_alias) AS nxt, CAST(a AS VARCHAR) AS prior_alias FROM (select a, b from tab1)");
	}

	@Test
	public void orderedAliasForwardRefFromPredicandSubstitutionUnresolvedTest() {
		assertPriorAliasForwardReferenceUnresolved(
				"SELECT prior_alias || 'z' AS nxt, <partner_name> AS prior_alias FROM (select a, b from tab1)");
	}

	@Test
	public void orderedAliasForwardRefFromComparisonPredicandUnresolvedTest() {
		assertPriorAliasForwardReferenceUnresolved(
				"SELECT prior_alias + 0 AS nxt, <a> >= <b> AS prior_alias FROM (select a, b from tab1)");
	}

	@Test
	public void orderedAliasForwardRefFromBareValueUnresolvedTest() {
		assertPriorAliasForwardReferenceUnresolved(
				"SELECT prior_alias AS nxt, CURRENT_DATE AS prior_alias FROM (select a, b from tab1)");
	}

	@Test
	public void orderedAliasForwardRefFromColumnSubstitutionInWindowPartitionByUnresolvedTest() {
		assertPriorAliasForwardReferenceUnresolved(
				"SELECT ROW_NUMBER() OVER (PARTITION BY prior_alias) AS rn, <email_col> AS prior_alias FROM (select a, b from tab1)");
	}

	@Test
	public void orderedAliasForwardRefFromBooleanConditionSubstitutionUnresolvedTest() {
		assertPriorAliasForwardReferenceUnresolved(
				"SELECT TRIM(prior_alias) AS nxt, <a> AND <b> AS prior_alias FROM (select a, b from tab1)");
	}

	@Test
	public void orderedAliasForwardRefFromScalarSubqueryUnresolvedTest() {
		assertPriorAliasForwardReferenceUnresolved(
				"SELECT prior_alias + 1 AS nxt, (SELECT max(x) FROM t2) AS prior_alias FROM (select 1 as k from t1)");
	}
}
