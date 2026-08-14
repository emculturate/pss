package sql.walker;

import org.junit.Assert;
import org.junit.Test;

import access.Snippet;
import errorhandling.ParseDiagnostic;
import sql.SQLSelectParserParser;

/**
 * SELECT-list formula aliases must not bind to a physical {@code table_dictionary} entry or trigger
 * {@code AMBIGUOUS_COLUMN_REFERENCE} when {@code FROM} has multiple tables — the alias is a
 * current-query output name, not an unqualified source column.
 */
public class SqlEventWalkerSelectListAliasTwoTableClauseReferenceTests extends AbstractSqlParseEventWalkerTest {

	private static final String AMBIGUOUS = "AMBIGUOUS_COLUMN_REFERENCE";
	private static final String NOT_IN_QUERY_ALIAS_FATAL = "UNQUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIASES";

	private SqlParseEventWalker walk(String query) {
		SQLSelectParserParser parser = parse(query);
		return runParsertest(query, parser);
	}

	private void assertFormulaAliasIsQueryOutputOnly(SqlParseEventWalker extractor, String label) {
		Snippet snippet = extractor.getSnippet();
		assertDiagnosticCountBySeverity(
				snippet, AMBIGUOUS, ParseDiagnostic.Severity.SEVERE_WARNING, null, null, 0);
		assertDiagnosticCountBySeverity(
				snippet, NOT_IN_QUERY_ALIAS_FATAL, ParseDiagnostic.Severity.FATAL, null, null, 0);

		String tableDict = extractor.getTableColumnDictionaryMap().toString();
		Assert.assertFalse(
				label + " must not materialize formula_a onto tab1: " + tableDict,
				tableDict.matches("(?s).*tab1=\\{[^}]*formula_a.*"));
		Assert.assertFalse(
				label + " must not materialize formula_a onto tab2: " + tableDict,
				tableDict.matches("(?s).*tab2=\\{[^}]*formula_a.*"));

		String symbol = extractor.getSymbolTable().toString();
		Assert.assertTrue(
				label + " should keep formula_a on query_dictionary: " + symbol,
				symbol.contains("formula_a="));
	}

	private static final String SELECT_TWO_TABLE =
			"SELECT a.col1, b.col2, a.col1 + b.col2 AS formula_a FROM tab1 a JOIN tab2 b ON a.col1 = b.col1";

	@Test
	public void formulaAliasWhereTwoTablesV0Test() {
		SqlParseEventWalker e = walk(SELECT_TWO_TABLE + " WHERE formula_a > 0");
		assertFormulaAliasIsQueryOutputOnly(e, "WHERE two tables");
	}

	@Test
	public void formulaAliasHavingTwoTablesV0Test() {
		SqlParseEventWalker e = walk(
				SELECT_TWO_TABLE + " GROUP BY a.col1, b.col2, formula_a HAVING formula_a > 0");
		assertFormulaAliasIsQueryOutputOnly(e, "HAVING two tables");
	}

	@Test
	public void formulaAliasGroupByTwoTablesV0Test() {
		SqlParseEventWalker e = walk(SELECT_TWO_TABLE + " GROUP BY formula_a");
		assertFormulaAliasIsQueryOutputOnly(e, "GROUP BY two tables");
	}

	@Test
	public void formulaAliasOrderByTwoTablesV0Test() {
		SqlParseEventWalker e = walk(SELECT_TWO_TABLE + " ORDER BY formula_a");
		assertFormulaAliasIsQueryOutputOnly(e, "ORDER BY two tables");
	}

	@Test
	public void formulaAliasJoinOnTwoTablesV0Test() {
		SqlParseEventWalker e = walk(
				SELECT_TWO_TABLE + " JOIN tab3 c ON formula_a = c.col1");
		assertFormulaAliasIsQueryOutputOnly(e, "JOIN ON two tables");
	}

	@Test
	public void formulaAliasQualifyTwoTablesV0Test() {
		SqlParseEventWalker e = walk(
				"SELECT a.col1, b.col2, ROW_NUMBER() OVER (ORDER BY a.col1) AS formula_a"
						+ " FROM tab1 a JOIN tab2 b ON a.col1 = b.col1 QUALIFY formula_a = 1");
		assertFormulaAliasIsQueryOutputOnly(e, "QUALIFY two tables");
	}

	@Test
	public void formulaAliasWhereWithUsingJoinTwoTablesV0Test() {
		SqlParseEventWalker e = walk(
				"SELECT a.col1, a.col1 + 1 AS formula_a, b.col2"
						+ " FROM tab1 a JOIN tab2 b USING (col1) WHERE formula_a > 0");
		assertFormulaAliasIsQueryOutputOnly(e, "WHERE with USING join");
	}
}
