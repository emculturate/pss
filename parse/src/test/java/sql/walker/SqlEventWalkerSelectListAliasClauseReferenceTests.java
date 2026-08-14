package sql.walker;

import org.junit.Assert;
import org.junit.Test;

import access.Snippet;
import errorhandling.ParseDiagnostic;
import sql.SQLSelectParserParser;

/**
 * SELECT-list output aliases (arithmetic, functions, aggregates, window) referenced from later
 * clauses must resolve against the <em>current</em> query interface — not only {@code FROM} sources.
 * Covers base-table and single CTE/subquery {@code FROM} shapes across clause buckets.
 */
public class SqlEventWalkerSelectListAliasClauseReferenceTests extends AbstractSqlParseEventWalkerTest {

	private static final String NOT_IN_QUERY_ALIAS_FATAL = "UNQUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIASES";
	private static final String UNRESOLVED_UNQUALIFIED_ERROR = "UNRESOLVED_UNQUALIFIED_COLUMNS";

	private SqlParseEventWalker walk(String query) {
		SQLSelectParserParser parser = parse(query);
		return runParsertest(query, parser);
	}

	private void assertNoSelectListAliasClauseDiagnostics(SqlParseEventWalker extractor, String label) {
		Snippet snippet = extractor.getSnippet();
		assertDiagnosticCountBySeverity(
				snippet,
				NOT_IN_QUERY_ALIAS_FATAL,
				ParseDiagnostic.Severity.FATAL,
				null,
				null,
				0);
		assertDiagnosticCountBySeverity(
				snippet,
				UNRESOLVED_UNQUALIFIED_ERROR,
				ParseDiagnostic.Severity.ERROR,
				null,
				null,
				0);
		Assert.assertTrue(
				label + " should retain alias in query_dictionary: " + extractor.getQueryColumnDictionaryMap(),
				extractor.getQueryColumnDictionaryMap().toString().contains("formula_"));
	}

	// --- arithmetic alias (col + literal) ---

	@Test
	public void arithmeticAliasWhereBaseTableV0Test() {
		SqlParseEventWalker e = walk(
				"SELECT t.col1, t.col1 + 1 AS formula_a FROM tab1 t WHERE formula_a > 0");
		assertNoSelectListAliasClauseDiagnostics(e, "arithmetic WHERE base");
	}

	@Test
	public void arithmeticAliasWhereCteSourceV0Test() {
		SqlParseEventWalker e = walk(
				"WITH src AS ( SELECT a.col1 FROM tab1 a )"
						+ " SELECT s.col1, s.col1 + 1 AS formula_a FROM src s WHERE formula_a > 0");
		assertNoSelectListAliasClauseDiagnostics(e, "arithmetic WHERE cte");
	}

	@Test
	public void arithmeticAliasWhereSubqueryFromV0Test() {
		SqlParseEventWalker e = walk(
				"SELECT sub.col1, sub.col1 + 1 AS formula_a"
						+ " FROM ( SELECT col1 FROM tab1 ) sub WHERE formula_a > 0");
		assertNoSelectListAliasClauseDiagnostics(e, "arithmetic WHERE subquery");
	}

	@Test
	public void arithmeticAliasOrderByCteSourceV0Test() {
		SqlParseEventWalker e = walk(
				"WITH src AS ( SELECT a.col1 FROM tab1 a )"
						+ " SELECT s.col1, s.col1 + 1 AS formula_a FROM src s ORDER BY formula_a");
		assertNoSelectListAliasClauseDiagnostics(e, "arithmetic ORDER BY cte");
	}

	@Test
	public void arithmeticAliasJoinOnCteSourceV0Test() {
		SqlParseEventWalker e = walk(
				"WITH src AS ( SELECT a.col1 FROM tab1 a )"
						+ " SELECT s.col1, s.col1 + 1 AS formula_a, b.col2"
						+ " FROM src s JOIN tab2 b ON formula_a = b.col1");
		assertNoSelectListAliasClauseDiagnostics(e, "arithmetic JOIN ON cte");
	}

	@Test
	public void arithmeticAliasGroupByCteSourceV0Test() {
		SqlParseEventWalker e = walk(
				"WITH src AS ( SELECT a.col1 FROM tab1 a )"
						+ " SELECT s.col1, s.col1 + 1 AS formula_a FROM src s GROUP BY formula_a");
		assertNoSelectListAliasClauseDiagnostics(e, "arithmetic GROUP BY cte");
	}

	@Test
	public void arithmeticAliasHavingCteSourceV0Test() {
		SqlParseEventWalker e = walk(
				"WITH src AS ( SELECT a.col1, a.col2 FROM tab1 a )"
						+ " SELECT s.col1, SUM(s.col2) AS formula_sum FROM src s"
						+ " GROUP BY s.col1 HAVING formula_sum > 0");
		assertNoSelectListAliasClauseDiagnostics(e, "aggregate HAVING cte");
	}

	@Test
	public void arithmeticAliasQualifyCteSourceV0Test() {
		SqlParseEventWalker e = walk(
				"WITH src AS ( SELECT a.col1 FROM tab1 a )"
						+ " SELECT s.col1, s.col1 + 1 AS formula_a FROM src s QUALIFY formula_a > 0");
		assertNoSelectListAliasClauseDiagnostics(e, "arithmetic QUALIFY cte");
	}

	// --- scalar function alias ---

	@Test
	public void functionAliasWhereCteSourceV0Test() {
		SqlParseEventWalker e = walk(
				"WITH src AS ( SELECT a.col1 FROM tab1 a )"
						+ " SELECT s.col1, UPPER(s.col1) AS formula_u FROM src s WHERE formula_u IS NOT NULL");
		assertNoSelectListAliasClauseDiagnostics(e, "function WHERE cte");
	}

	@Test
	public void functionAliasOrderByCteSourceV0Test() {
		SqlParseEventWalker e = walk(
				"WITH src AS ( SELECT a.col1 FROM tab1 a )"
						+ " SELECT s.col1, UPPER(s.col1) AS formula_u FROM src s ORDER BY formula_u");
		assertNoSelectListAliasClauseDiagnostics(e, "function ORDER BY cte");
	}

	// --- aggregate alias (non-window) ---

	@Test
	public void aggregateAliasWhereCteSourceV0Test() {
		SqlParseEventWalker e = walk(
				"WITH src AS ( SELECT a.col2 FROM tab1 a )"
						+ " SELECT SUM(s.col2) AS formula_sum FROM src s WHERE formula_sum > 0");
		assertNoSelectListAliasClauseDiagnostics(e, "aggregate WHERE cte");
	}

	// --- window alias (non-rn name) in non-QUALIFY clauses ---

	@Test
	public void windowAliasWhereCteSourceV0Test() {
		SqlParseEventWalker e = walk(
				"WITH src AS ( SELECT a.col1 FROM tab1 a )"
						+ " SELECT s.col1, ROW_NUMBER() OVER (ORDER BY s.col1) AS formula_rn"
						+ " FROM src s WHERE formula_rn = 1");
		assertNoSelectListAliasClauseDiagnostics(e, "window WHERE cte");
	}

	@Test
	public void windowAliasOrderByCteSourceV0Test() {
		SqlParseEventWalker e = walk(
				"WITH src AS ( SELECT a.col1 FROM tab1 a )"
						+ " SELECT s.col1, ROW_NUMBER() OVER (ORDER BY s.col1) AS formula_rn"
						+ " FROM src s ORDER BY formula_rn");
		assertNoSelectListAliasClauseDiagnostics(e, "window ORDER BY cte");
	}

	// --- JOIN USING uses physical column names only; formula alias must not appear ---

	@Test
	public void arithmeticAliasJoinUsingBaseTableV0Test() {
		SqlParseEventWalker e = walk(
				"SELECT a.col1, a.col1 + 1 AS formula_a, b.col2"
						+ " FROM tab1 a JOIN tab2 b USING (col1) WHERE formula_a > 0");
		assertNoSelectListAliasClauseDiagnostics(e, "arithmetic WHERE with USING join");
	}
}
