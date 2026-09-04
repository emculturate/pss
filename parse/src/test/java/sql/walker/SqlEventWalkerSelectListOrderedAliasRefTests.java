package sql.walker;

import org.junit.Assert;
import org.junit.Test;

import access.Snippet;
import errorhandling.ParseDiagnostic;
import sql.SQLSelectParserParser;

/**
 * Ordered intra–SELECT-list output-alias references: a prior select-item alias may
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

	private static final String WRAPPED_FROM_AB = " FROM (select a, b from tab1)";
	private static final String WRAPPED_FROM_T1 = " FROM (select 1 as k from t1)";
	// --- origin item types (first select-list entry) ---

	@Test
	public void orderedAliasFromPlainColumnInArithmeticConsumerTest() {
		SqlParseEventWalker extractor = parseHappyPath(
				"SELECT a AS " + PRIOR_ALIAS + ", " + PRIOR_ALIAS + " + 1 AS nxt FROM tab1");
		assertPriorAliasBoundToQueryScope(extractor);
	}

	@Test
	public void orderedAliasFromArithmeticExpressionInFunctionConsumerTest() {
		SqlParseEventWalker extractor = parseHappyPath(
				"SELECT a + b AS " + PRIOR_ALIAS + ", TRIM(" + PRIOR_ALIAS + ") AS nxt FROM tab1");
		assertPriorAliasBoundToQueryScope(extractor);
	}

	@Test
	public void orderedAliasFromFunctionExpressionInCalcConsumerTest() {
		SqlParseEventWalker extractor = parseHappyPath(
				"SELECT LOWER(a) AS " + PRIOR_ALIAS + ", " + PRIOR_ALIAS + " || 'x' AS nxt FROM tab1");
		assertPriorAliasBoundToQueryScope(extractor);
	}

	@Test
	public void orderedAliasFromCaseExpressionInCalcConsumerTest() {
		SqlParseEventWalker extractor = parseHappyPath(
				"SELECT CASE WHEN a > 0 THEN a ELSE b END AS " + PRIOR_ALIAS
						+ ", " + PRIOR_ALIAS + " + 1 AS nxt FROM tab1");
		assertPriorAliasBoundToQueryScope(extractor);
	}

	@Test
	public void orderedAliasFromCastExpressionInFunctionConsumerTest() {
		SqlParseEventWalker extractor = parseHappyPath(
				"SELECT CAST(a AS VARCHAR) AS " + PRIOR_ALIAS
						+ ", TRIM(" + PRIOR_ALIAS + ") AS nxt FROM tab1");
		assertPriorAliasBoundToQueryScope(extractor);
	}

	@Test
	public void orderedAliasFromPredicandSubstitutionInCalcConsumerTest() {
		SqlParseEventWalker extractor = parseHappyPath(
				"SELECT <partner_name> AS " + PRIOR_ALIAS
						+ ", " + PRIOR_ALIAS + " || 'z' AS nxt FROM tab1");
		assertPriorAliasBoundToQueryScope(extractor);
	}

	@Test
	public void orderedAliasFromComparisonPredicandInCalcConsumerTest() {
		SqlParseEventWalker extractor = parseHappyPath(
				"SELECT <a> >= <b> AS " + PRIOR_ALIAS
						+ ", " + PRIOR_ALIAS + " + 0 AS nxt FROM tab1");
		assertPriorAliasBoundToQueryScope(extractor);
	}

	@Test
	public void orderedAliasFromBareValueInCalcConsumerTest() {
		SqlParseEventWalker extractor = parseHappyPath(
				"SELECT CURRENT_DATE AS " + PRIOR_ALIAS
						+ ", " + PRIOR_ALIAS + " AS nxt FROM tab1");
		// bare-value origin is grounded; consumer re-reference stays on query scope
		assertPriorAliasBoundToQueryScope(extractor);
	}

	@Test
	public void orderedAliasFromColumnSubstitutionInWindowPartitionByTest() {
		SqlParseEventWalker extractor = parseHappyPath(
				"SELECT <email_col> AS " + PRIOR_ALIAS
						+ ", ROW_NUMBER() OVER (PARTITION BY " + PRIOR_ALIAS + ") AS rn FROM tab1");
		assertWindowPartitionByPriorAlias(extractor);
		assertPriorAliasBoundToQueryScope(extractor);
	}

	@Test
	public void orderedAliasFromBooleanConditionSubstitutionInFunctionConsumerTest() {
		SqlParseEventWalker extractor = parseHappyPath(
				"SELECT <a> AND <b> AS " + PRIOR_ALIAS
						+ ", TRIM(" + PRIOR_ALIAS + ") AS nxt FROM tab1");
		assertPriorAliasBoundToQueryScope(extractor);
	}

	@Test
	public void orderedAliasFromScalarSubqueryInArithmeticConsumerTest() {
		SqlParseEventWalker extractor = parseHappyPath(
				"SELECT (SELECT max(x) FROM t2) AS " + PRIOR_ALIAS
						+ ", " + PRIOR_ALIAS + " + 1 AS nxt FROM t1");
		assertPriorAliasBoundToQueryScope(extractor);
	}

	@Test
	public void orderedAliasFromScalarSubqueryInWindowPartitionByTest() {
		SqlParseEventWalker extractor = parseHappyPath(
				"SELECT (SELECT max(x) FROM t2) AS " + PRIOR_ALIAS
						+ ", ROW_NUMBER() OVER (PARTITION BY " + PRIOR_ALIAS + ") AS rn FROM t1");
		assertWindowPartitionByPriorAlias(extractor);
		assertPriorAliasBoundToQueryScope(extractor);
	}

	// --- column-ref sites inside later select-list items ---

	@Test
	public void orderedAliasReferencedInWindowPartitionByTest() {
		SqlParseEventWalker extractor = parseHappyPath(
				"SELECT a AS " + PRIOR_ALIAS
						+ ", ROW_NUMBER() OVER (PARTITION BY " + PRIOR_ALIAS + ") AS rn FROM tab1");
		assertWindowPartitionByPriorAlias(extractor);
		assertPriorAliasBoundToQueryScope(extractor);
	}

	@Test
	public void orderedAliasReferencedInWindowOrderByTest() {
		SqlParseEventWalker extractor = parseHappyPath(
				"SELECT a AS " + PRIOR_ALIAS
						+ ", ROW_NUMBER() OVER (ORDER BY " + PRIOR_ALIAS + ") AS rn FROM tab1");
		assertWindowOrderedByPriorAlias(extractor);
		assertPriorAliasBoundToQueryScope(extractor);
	}

	@Test
	public void orderedAliasReferencedInWindowPartitionByAndOrderByTest() {
		SqlParseEventWalker extractor = parseHappyPath(
				"SELECT a AS " + PRIOR_ALIAS
						+ ", ROW_NUMBER() OVER (PARTITION BY a ORDER BY " + PRIOR_ALIAS + ") AS rn FROM tab1");
		assertWindowOrderedByPriorAlias(extractor);
		assertPriorAliasBoundToQueryScope(extractor);
	}

	@Test
	public void orderedPredicandAliasReferencedInWindowPartitionByTest() {
		SqlParseEventWalker extractor = parseHappyPath(
				"SELECT <partner_name> AS " + PRIOR_ALIAS
						+ ", ROW_NUMBER() OVER (PARTITION BY " + PRIOR_ALIAS + ") AS rn FROM tab1");
		assertWindowPartitionByPriorAlias(extractor);
		assertPriorAliasBoundToQueryScope(extractor);
	}

	@Test
	public void orderedPredicandAliasReferencedInWindowOrderByTest() {
		SqlParseEventWalker extractor = parseHappyPath(
				"SELECT <partner_name> AS " + PRIOR_ALIAS
						+ ", ROW_NUMBER() OVER (ORDER BY " + PRIOR_ALIAS + ") AS rn FROM tab1");
		assertWindowOrderedByPriorAlias(extractor);
		assertPriorAliasBoundToQueryScope(extractor);
	}

	@Test
	public void orderedAliasReferencedInAggregateWindowFunctionParameterTest() {
		SqlParseEventWalker extractor = parseHappyPath(
				"SELECT a AS " + PRIOR_ALIAS
						+ ", SUM(b) OVER (PARTITION BY " + PRIOR_ALIAS + " ORDER BY " + PRIOR_ALIAS + ") AS s FROM tab1");
		assertWindowPartitionByPriorAlias(extractor);
		assertWindowOrderedByPriorAlias(extractor);
		assertPriorAliasBoundToQueryScope(extractor);
	}

	// --- forward references (consumer before defining select-list item) ---

	@Test
	public void orderedAliasForwardRefFromPlainColumnUnresolvedTest() {
		assertPriorAliasForwardReferenceUnresolved(
				"SELECT " + PRIOR_ALIAS + " + 1 AS nxt, a AS " + PRIOR_ALIAS + WRAPPED_FROM_AB);
	}

	@Test
	public void orderedAliasForwardRefFromArithmeticExpressionUnresolvedTest() {
		assertPriorAliasForwardReferenceUnresolved(
				"SELECT TRIM(" + PRIOR_ALIAS + ") AS nxt, a + b AS " + PRIOR_ALIAS + WRAPPED_FROM_AB);
	}

	@Test
	public void orderedAliasForwardRefFromFunctionExpressionUnresolvedTest() {
		assertPriorAliasForwardReferenceUnresolved(
				"SELECT " + PRIOR_ALIAS + " || 'x' AS nxt, LOWER(a) AS " + PRIOR_ALIAS + WRAPPED_FROM_AB);
	}

	@Test
	public void orderedAliasForwardRefFromCaseExpressionUnresolvedTest() {
		assertPriorAliasForwardReferenceUnresolved(
				"SELECT " + PRIOR_ALIAS + " + 1 AS nxt, CASE WHEN a > 0 THEN a ELSE b END AS "
						+ PRIOR_ALIAS + WRAPPED_FROM_AB);
	}

	@Test
	public void orderedAliasForwardRefFromCastExpressionUnresolvedTest() {
		assertPriorAliasForwardReferenceUnresolved(
				"SELECT TRIM(" + PRIOR_ALIAS + ") AS nxt, CAST(a AS VARCHAR) AS " + PRIOR_ALIAS + WRAPPED_FROM_AB);
	}

	@Test
	public void orderedAliasForwardRefFromPredicandSubstitutionUnresolvedTest() {
		assertPriorAliasForwardReferenceUnresolved(
				"SELECT " + PRIOR_ALIAS + " || 'z' AS nxt, <partner_name> AS " + PRIOR_ALIAS + WRAPPED_FROM_AB);
	}

	@Test
	public void orderedAliasForwardRefFromComparisonPredicandUnresolvedTest() {
		assertPriorAliasForwardReferenceUnresolved(
				"SELECT " + PRIOR_ALIAS + " + 0 AS nxt, <a> >= <b> AS " + PRIOR_ALIAS + WRAPPED_FROM_AB);
	}

	@Test
	public void orderedAliasForwardRefFromBareValueUnresolvedTest() {
		assertPriorAliasForwardReferenceUnresolved(
				"SELECT " + PRIOR_ALIAS + " AS nxt, CURRENT_DATE AS " + PRIOR_ALIAS + WRAPPED_FROM_AB);
	}

	@Test
	public void orderedAliasForwardRefFromColumnSubstitutionInWindowPartitionByUnresolvedTest() {
		assertPriorAliasForwardReferenceUnresolved(
				"SELECT ROW_NUMBER() OVER (PARTITION BY " + PRIOR_ALIAS + ") AS rn, <email_col> AS "
						+ PRIOR_ALIAS + WRAPPED_FROM_AB);
	}

	@Test
	public void orderedAliasForwardRefFromBooleanConditionSubstitutionUnresolvedTest() {
		assertPriorAliasForwardReferenceUnresolved(
				"SELECT TRIM(" + PRIOR_ALIAS + ") AS nxt, <a> AND <b> AS " + PRIOR_ALIAS + WRAPPED_FROM_AB);
	}

	@Test
	public void orderedAliasForwardRefFromScalarSubqueryUnresolvedTest() {
		assertPriorAliasForwardReferenceUnresolved(
				"SELECT " + PRIOR_ALIAS + " + 1 AS nxt, (SELECT max(x) FROM t2) AS " + PRIOR_ALIAS + WRAPPED_FROM_T1);
	}
}
