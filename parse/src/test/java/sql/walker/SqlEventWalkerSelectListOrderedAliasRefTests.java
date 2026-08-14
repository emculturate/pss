package sql.walker;

import org.junit.Assert;
import org.junit.Test;

import sql.SQLSelectParserParser;

/**
 * Ordered intra–SELECT-list output-alias references: a prior select-item alias may
 * appear anywhere a column reference is valid in a later select-list item, provided
 * the alias is defined earlier in source order.
 */
public class SqlEventWalkerSelectListOrderedAliasRefTests extends AbstractSqlParseEventWalkerTest {

	private static final String PRIOR_ALIAS = "prior_alias";

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
				"expected prior_alias lineage via query0 in symbol table: " + symbolTable,
				symbolTable.contains("{name=" + PRIOR_ALIAS + ", table_ref=query0}"));
	}

	private void assertWindowPartitionByPriorAlias(SqlParseEventWalker extractor) {
		String symbolTable = extractor.getSymbolTable().toString();
		Assert.assertTrue(
				"expected window_partition_by prior_alias@query0: " + symbolTable,
				symbolTable.contains("window_partition_by=[")
						&& symbolTable.contains("{name=" + PRIOR_ALIAS + ", table_ref=query0}"));
	}

	private void assertWindowOrderedByPriorAlias(SqlParseEventWalker extractor) {
		String symbolTable = extractor.getSymbolTable().toString();
		Assert.assertTrue(
				"expected window_ordered_by prior_alias@query0: " + symbolTable,
				symbolTable.contains("window_ordered_by=[")
						&& symbolTable.contains("{name=" + PRIOR_ALIAS + ", table_ref=query0}"));
	}

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
}
