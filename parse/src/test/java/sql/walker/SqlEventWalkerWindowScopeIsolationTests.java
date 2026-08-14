package sql.walker;

import org.junit.Assert;
import org.junit.Test;

import sql.SQLSelectParserParser;

/**
 * Post-{@code SELECT}-list clauses (QUALIFY, HAVING, ORDER BY, …) that contain
 * {@code OVER (PARTITION BY … ORDER BY …)} must not leave window-interface latch
 * state that bleeds into a later query scope (outer WITH body, join partner, etc.).
 */
public class SqlEventWalkerWindowScopeIsolationTests extends AbstractSqlParseEventWalkerTest {

	private static final String RANKED_JOIN_FROM =
			"SELECT rsc.id, cbsc.contact_key "
					+ "FROM rsc_tab rsc "
					+ "INNER JOIN cbsc_tab cbsc ON rsc.source_id = cbsc.source_id ";

	private static final String WINDOW_OVER =
			"OVER (PARTITION BY contact_key ORDER BY cbsc.priority ASC)";

	private static final String RANKED_ROW_NUMBER_CTE = RANKED_JOIN_FROM
			+ "QUALIFY ROW_NUMBER() " + WINDOW_OVER + " = 1";

	private static final String SIMPLE_FROM = "SELECT id, col1, col2 FROM tab1 ";

	/** CTE body alone vs WITH-wrapped outer query must produce the same fatal count. */
	private void assertCteScopeDoesNotLeakFatals(String cteBody, String outerQuerySuffix) {
		SqlParseEventWalker cteOnly = runParsertest(cteBody, parse(cteBody));
		String fullQuery = "WITH cte AS (" + cteBody + ") " + outerQuerySuffix;
		SqlParseEventWalker full = runParsertest(fullQuery, parse(fullQuery));
		assertNoFatalErrors(cteOnly);
		assertNoFatalErrors(full);
		Assert.assertEquals(
				"Outer query must not add fatals beyond the isolated CTE body",
				fatalCount(cteOnly),
				fatalCount(full));
	}

	private static int fatalCount(SqlParseEventWalker extractor) {
		if (extractor.getSnippet() == null || extractor.getSnippet().getFatalErrorStringList() == null) {
			return 0;
		}
		return extractor.getSnippet().getFatalErrorStringList().size();
	}

	// --- QUALIFY + assorted window functions (join body with partition/order refs) ---

	@Test
	public void qualifyRowNumberPartitionOrderCteScopeV0Test() {
		assertCteScopeDoesNotLeakFatals(RANKED_ROW_NUMBER_CTE, "SELECT contact_key FROM cte");
	}

	@Test
	public void qualifyRankPartitionOrderCteScopeV0Test() {
		assertCteScopeDoesNotLeakFatals(
				RANKED_JOIN_FROM + "QUALIFY RANK() " + WINDOW_OVER + " = 1",
				"SELECT contact_key FROM cte");
	}

	@Test
	public void qualifyDenseRankPartitionOrderCteScopeV0Test() {
		assertCteScopeDoesNotLeakFatals(
				RANKED_JOIN_FROM + "QUALIFY DENSE_RANK() " + WINDOW_OVER + " = 1",
				"SELECT contact_key FROM cte");
	}

	@Test
	public void qualifyPercentRankPartitionOrderCteScopeV0Test() {
		assertCteScopeDoesNotLeakFatals(
				RANKED_JOIN_FROM + "QUALIFY PERCENT_RANK() " + WINDOW_OVER + " = 1",
				"SELECT contact_key FROM cte");
	}

	@Test
	public void qualifySumOverPartitionOrderCteScopeV0Test() {
		assertCteScopeDoesNotLeakFatals(
				RANKED_JOIN_FROM + "QUALIFY SUM(rsc.id) " + WINDOW_OVER + " > 0",
				"SELECT contact_key FROM cte");
	}

	@Test
	public void qualifyRowNumberJoinPartnerScopeV0Test() {
		String full =
				"SELECT a.contact_key FROM (" + RANKED_ROW_NUMBER_CTE + ") a "
						+ "JOIN plain_tab p ON a.id = p.id";
		SqlParseEventWalker cteOnly = runParsertest(RANKED_ROW_NUMBER_CTE, parse(RANKED_ROW_NUMBER_CTE));
		SqlParseEventWalker fullWalker = runParsertest(full, parse(full));
		assertNoFatalErrors(cteOnly);
		assertNoFatalErrors(fullWalker);
	}

	@Test
	public void qualifyRowNumberTwoCteFinalJoinScopeV0Test() {
		String full =
				"WITH ranked AS (" + RANKED_ROW_NUMBER_CTE + "), "
						+ "plain AS (SELECT id, label FROM plain_tab) "
						+ "SELECT r.contact_key, p.label FROM ranked r JOIN plain p ON r.id = p.id";
		assertNoFatalErrors(runParsertest(full, parse(full)));
	}

	// --- Simple tab1 QUALIFY variants (single-table window refs) ---

	@Test
	public void qualifyRankSimpleTableCteScopeV0Test() {
		assertCteScopeDoesNotLeakFatals(
				SIMPLE_FROM + "QUALIFY RANK() OVER (PARTITION BY col1 ORDER BY col2) = 1",
				"SELECT col1 FROM cte");
	}

	@Test
	public void qualifyLagSimpleTableCteScopeV0Test() {
		assertCteScopeDoesNotLeakFatals(
				SIMPLE_FROM + "QUALIFY LAG(col2, 1) OVER (PARTITION BY col1 ORDER BY col2) IS NOT NULL",
				"SELECT col1 FROM cte");
	}

	// --- CTE bodies ending with each trailing clause type (non-window control cases) ---

	@Test
	public void cteEndingWhereScopeV0Test() {
		assertCteScopeDoesNotLeakFatals(
				SIMPLE_FROM + "WHERE col1 > 0",
				"SELECT col1 FROM cte");
	}

	@Test
	public void cteEndingGroupByScopeV0Test() {
		assertCteScopeDoesNotLeakFatals(
				"SELECT col1, SUM(col2) AS total FROM tab1 GROUP BY col1",
				"SELECT col1 FROM cte");
	}

	@Test
	public void cteEndingHavingScopeV0Test() {
		assertCteScopeDoesNotLeakFatals(
				"SELECT col1, SUM(col2) AS total FROM tab1 GROUP BY col1 HAVING SUM(col2) > 0",
				"SELECT col1 FROM cte");
	}

	@Test
	public void cteEndingOrderByScopeV0Test() {
		assertCteScopeDoesNotLeakFatals(
				SIMPLE_FROM + "ORDER BY col1, col2",
				"SELECT col1 FROM cte");
	}

	@Test
	public void cteEndingLimitScopeV0Test() {
		assertCteScopeDoesNotLeakFatals(
				SIMPLE_FROM + "LIMIT 10",
				"SELECT id FROM cte");
	}

	// --- Post-SELECT clauses that embed OVER (same latch risk as QUALIFY) ---

	@Test
	public void cteEndingHavingWithWindowScopeV0Test() {
		assertCteScopeDoesNotLeakFatals(
				"SELECT col1, SUM(col2) AS total FROM tab1 "
						+ "GROUP BY col1 "
						+ "HAVING ROW_NUMBER() OVER (PARTITION BY col1 ORDER BY col2) = 1",
				"SELECT col1 FROM cte");
	}

	@Test
	public void cteEndingOrderByWithWindowScopeV0Test() {
		assertCteScopeDoesNotLeakFatals(
				SIMPLE_FROM + "ORDER BY ROW_NUMBER() OVER (PARTITION BY col1 ORDER BY col2)",
				"SELECT col1 FROM cte");
	}

	@Test
	public void cteEndingQualifyRankScopeV0Test() {
		assertCteScopeDoesNotLeakFatals(
				SIMPLE_FROM + "QUALIFY RANK() OVER (PARTITION BY col1 ORDER BY col2) <= 1",
				"SELECT col1 FROM cte");
	}
}
