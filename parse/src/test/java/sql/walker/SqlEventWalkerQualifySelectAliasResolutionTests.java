package sql.walker;

import org.junit.Assert;
import org.junit.Test;

import access.Snippet;
import errorhandling.ParseDiagnostic;
import sql.SQLSelectParserParser;

/**
 * QUALIFY may reference a SELECT-list output alias (e.g. {@code ROW_NUMBER() … AS rn} then
 * {@code QUALIFY rn = 1}). Resolution must bind against the <em>current</em> query's output
 * interface / {@code query_dictionary}, not only {@code FROM} source aliases — especially when
 * {@code FROM} is CTE- or subquery-backed.
 */
public class SqlEventWalkerQualifySelectAliasResolutionTests extends AbstractSqlParseEventWalkerTest {

	private static final String NOT_IN_QUERY_ALIAS_FATAL = "UNQUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIASES";
	private static final String UNRESOLVED_UNQUALIFIED_ERROR = "UNRESOLVED_UNQUALIFIED_COLUMNS";

	private SqlParseEventWalker walk(String query) {
		SQLSelectParserParser parser = parse(query);
		return runParsertest(query, parser);
	}

	private void assertNoQualifyAliasResolutionDiagnostics(SqlParseEventWalker extractor) {
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
	}

	private void assertQualifyAliasInQueryDictionary(SqlParseEventWalker extractor, String aliasName) {
		String snapshot = extractor.getQueryColumnDictionaryMap().toString();
		Assert.assertTrue(
				"Expected query_dictionary entry for QUALIFY alias '" + aliasName + "' in " + snapshot,
				snapshot.contains(aliasName + "="));
	}

	@Test
	public void qualifySelectAliasRowNumberBaseTableV0Test() {
		final String query = "SELECT t.col1, ROW_NUMBER() OVER (PARTITION BY t.col1 ORDER BY t.col1) AS rn"
				+ " FROM tab1 t QUALIFY rn = 1";
		SqlParseEventWalker extractor = walk(query);
		assertNoQualifyAliasResolutionDiagnostics(extractor);
		assertQualifyAliasInQueryDictionary(extractor, "rn");
	}

	@Test
	public void qualifySelectAliasLagBaseTableV0Test() {
		final String query = "SELECT t.col1, LAG(t.col1) OVER (ORDER BY t.col1) AS prev_val"
				+ " FROM tab1 t QUALIFY prev_val IS NOT NULL";
		SqlParseEventWalker extractor = walk(query);
		assertNoQualifyAliasResolutionDiagnostics(extractor);
		assertQualifyAliasInQueryDictionary(extractor, "prev_val");
	}

	@Test
	public void qualifySelectAliasSumBaseTableV0Test() {
		final String query = "SELECT t.col1, SUM(t.col2) OVER (PARTITION BY t.col1) AS running_total"
				+ " FROM tab1 t QUALIFY running_total > 0";
		SqlParseEventWalker extractor = walk(query);
		assertNoQualifyAliasResolutionDiagnostics(extractor);
		assertQualifyAliasInQueryDictionary(extractor, "running_total");
	}

	@Test
	public void qualifySelectAliasCustomNameBaseTableV0Test() {
		final String query = "SELECT t.col1, ROW_NUMBER() OVER (ORDER BY t.col1) AS rank_col"
				+ " FROM tab1 t QUALIFY rank_col = 1";
		SqlParseEventWalker extractor = walk(query);
		assertNoQualifyAliasResolutionDiagnostics(extractor);
		assertQualifyAliasInQueryDictionary(extractor, "rank_col");
	}

	@Test
	public void qualifySelectAliasRowNumberCteSourceV0Test() {
		final String query = "WITH src AS ( SELECT a.col1 FROM tab1 a )"
				+ " SELECT s.col1, ROW_NUMBER() OVER (ORDER BY s.col1) AS rn FROM src s QUALIFY rn = 1";
		SqlParseEventWalker extractor = walk(query);
		assertNoQualifyAliasResolutionDiagnostics(extractor);
		assertQualifyAliasInQueryDictionary(extractor, "rn");
	}

	@Test
	public void qualifySelectAliasRowNumberSubqueryFromV0Test() {
		final String query = "SELECT sub.col1, ROW_NUMBER() OVER (ORDER BY sub.col1) AS rn"
				+ " FROM ( SELECT col1 FROM tab1 ) sub QUALIFY rn = 1";
		SqlParseEventWalker extractor = walk(query);
		assertNoQualifyAliasResolutionDiagnostics(extractor);
		assertQualifyAliasInQueryDictionary(extractor, "rn");
	}

	@Test
	public void qualifySelectAliasLagCteSourceV0Test() {
		final String query = "WITH src AS ( SELECT a.col1 FROM tab1 a )"
				+ " SELECT s.col1, LAG(s.col1) OVER (ORDER BY s.col1) AS prev_val FROM src s"
				+ " QUALIFY prev_val IS NOT NULL";
		SqlParseEventWalker extractor = walk(query);
		assertNoQualifyAliasResolutionDiagnostics(extractor);
		assertQualifyAliasInQueryDictionary(extractor, "prev_val");
	}

	@Test
	public void qualifySelectAliasSumCteSourceV0Test() {
		final String query = "WITH src AS ( SELECT a.col1, a.col2 FROM tab1 a )"
				+ " SELECT s.col1, SUM(s.col2) OVER (PARTITION BY s.col1) AS running_total FROM src s"
				+ " QUALIFY running_total > 0";
		SqlParseEventWalker extractor = walk(query);
		assertNoQualifyAliasResolutionDiagnostics(extractor);
		assertQualifyAliasInQueryDictionary(extractor, "running_total");
	}

	@Test
	public void qualifySelectAliasCustomNameCteSourceV0Test() {
		final String query = "WITH src AS ( SELECT a.col1 FROM tab1 a )"
				+ " SELECT s.col1, ROW_NUMBER() OVER (ORDER BY s.col1) AS rank_col FROM src s"
				+ " QUALIFY rank_col = 1";
		SqlParseEventWalker extractor = walk(query);
		assertNoQualifyAliasResolutionDiagnostics(extractor);
		assertQualifyAliasInQueryDictionary(extractor, "rank_col");
	}

	/** Control: inline window in QUALIFY (no SELECT-list alias) already works with CTE FROM. */
	@Test
	public void qualifyInlineRowNumberCteSourceV0Test() {
		final String query = "WITH src AS ( SELECT a.col1 FROM tab1 a )"
				+ " SELECT s.col1 FROM src s QUALIFY ROW_NUMBER() OVER (ORDER BY s.col1) = 1";
		SqlParseEventWalker extractor = walk(query);
		assertNoQualifyAliasResolutionDiagnostics(extractor);
	}

	@Test
	public void qualifySelectAliasRowNumberCteWithPhysicalJoinV0Test() {
		final String query = "WITH src AS ( SELECT a.col1 FROM tab1 a )"
				+ " SELECT s.col1, b.col2, ROW_NUMBER() OVER (ORDER BY s.col1) AS rn"
				+ " FROM src s JOIN tab2 b ON s.col1 = b.col1 QUALIFY rn = 1";
		SqlParseEventWalker extractor = walk(query);
		assertNoQualifyAliasResolutionDiagnostics(extractor);
		assertQualifyAliasInQueryDictionary(extractor, "rn");
	}

	@Test
	public void qualifySelectAliasRowNumberCteGroupByV0Test() {
		final String query = "WITH src AS ( SELECT a.col1 FROM tab1 a )"
				+ " SELECT s.col1, ROW_NUMBER() OVER (ORDER BY s.col1) AS rn FROM src s"
				+ " GROUP BY s.col1 QUALIFY rn = 1";
		SqlParseEventWalker extractor = walk(query);
		assertNoQualifyAliasResolutionDiagnostics(extractor);
		assertQualifyAliasInQueryDictionary(extractor, "rn");
	}
}
