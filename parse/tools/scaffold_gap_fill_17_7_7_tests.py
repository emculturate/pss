#!/usr/bin/env python3
"""Insert §17.7.7 gap-fill tests with golden placeholders (run refresh_pivot_unpivot_goldens.py next)."""
from pathlib import Path

TEST_JAVA = Path(__file__).resolve().parents[1] / "src/test/java/sql/walker/SqlEventWalkerPivotUnpivotTests.java"

GOLDEN_BLOCK = """
		Assert.assertEquals("AST is wrong", "-",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "-",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "-",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "-",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "-",
				extractor.getSymbolTable().toString());
"""

TESTS = r'''
	// --- §17.7.7-gap-fill (focused matrix cells) ---

	/**
	 * Matrix: subset=E | topo=S3 (P–U–P) | bucket=GROUP_BY,HAVING,ORDER_BY | kind=derived (qualified) |
	 * outcome=happy.
	 */
	@Test
	public void gapFill17_7_7_S3PivotUnpivotPivotGroupByHavingQualifiedDerivedV1Test() {
		final String query =
				"SELECT p.jan_sales_SUM, u.month_name, u.sales_amount\n"
						+ "FROM monthly_sales_long p_src\n"
						+ "PIVOT (SUM(sales_amount) FOR month_name IN ('jan_sales', 'feb_sales')) p\n"
						+ "JOIN monthly_sales u_src\n"
						+ "UNPIVOT (sales_amount FOR month_name IN (jan_sales, feb_sales)) u\n"
						+ "  ON p.jan_sales_SUM = u.sales_amount AND u.month_name = 'jan_sales'\n"
						+ "JOIN monthly_sales_long q_src\n"
						+ "PIVOT (SUM(sales_amount) FOR month_name IN ('feb_sales')) q\n"
						+ "  ON u.sales_amount = q.feb_sales_SUM\n"
						+ "GROUP BY p.jan_sales_SUM, u.month_name, u.sales_amount\n"
						+ "HAVING p.jan_sales_SUM > 0 AND u.sales_amount > 10 AND q.feb_sales_SUM > 0\n"
						+ "ORDER BY p.jan_sales_SUM, u.month_name;";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoFatalErrors(extractor);
		assertDiagnosticAtPosition(
				extractor.getSnippet(),
				"AMBIGUOUS_COLUMN_REFERENCE",
				ParseDiagnostic.Severity.SEVERE_WARNING,
				"Ambiguous column reference 'month_name' at (l:1 c:26). Possible sources: [p, q]",
				"month_name",
				1,
				26);
		assertDiagnosticAtPosition(
				extractor.getSnippet(),
				"AMBIGUOUS_COLUMN_REFERENCE",
				ParseDiagnostic.Severity.SEVERE_WARNING,
				"Ambiguous column reference 'sales_amount' at (l:1 c:40). Possible sources: [p, q]",
				"sales_amount",
				1,
				40);
''' + GOLDEN_BLOCK + r'''
	}

	/**
	 * Matrix: subset=E | topo=S3 (U–P–U) | bucket=GROUP_BY | kind=derived (unqualified) | outcome=unhappy.
	 */
	@Test
	public void gapFill17_7_7_S3UnpivotPivotUnpivotGroupByAmbiguousDerivedFatalV1Test() {
		final String query =
				"SELECT p.jan_sales_SUM\n"
						+ "FROM monthly_sales u1_src\n"
						+ "UNPIVOT (sales_amount FOR month_name IN (jan_sales, feb_sales)) u1\n"
						+ "JOIN monthly_sales_long p_src\n"
						+ "PIVOT (SUM(sales_amount) FOR month_name IN ('jan_sales')) p\n"
						+ "  ON u1.sales_amount = p.jan_sales_SUM\n"
						+ "JOIN monthly_sales u2_src\n"
						+ "UNPIVOT (sales_amount FOR month_name IN (feb_sales, mar_sales)) u2\n"
						+ "  ON p.jan_sales_SUM = u2.sales_amount\n"
						+ "GROUP BY sales_amount;";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertFatalDiagnosticAtPosition(
				extractor.getSnippet(),
				"AMBIGUOUS_DERIVED_COLUMN_REFERENCE",
				"Ambiguous derived column reference 'sales_amount' at (l:10 c:9). Possible sources: [u1, u2]",
				"sales_amount",
				10,
				9);
''' + GOLDEN_BLOCK + r'''
	}

	/**
	 * Matrix: subset=E | topo=S2-PU | bucket=WHERE,GROUP_BY,HAVING,ORDER_BY | kind=derived (qualified) |
	 * outcome=happy.
	 */
	@Test
	public void gapFill17_7_7_S2PuPivotUnpivotJoinClauseEgressDerivedV1Test() {
		final String query =
				"SELECT p.jan_sales_SUM, u.month_name, u.sales_amount\n"
						+ "FROM monthly_sales_long p_src\n"
						+ "PIVOT (SUM(sales_amount) FOR month_name IN ('jan_sales')) p\n"
						+ "JOIN monthly_sales u_src\n"
						+ "UNPIVOT (sales_amount FOR month_name IN (jan_sales, feb_sales)) u\n"
						+ "  ON p.jan_sales_SUM = u.sales_amount AND u.month_name = 'jan_sales'\n"
						+ "WHERE p.jan_sales_SUM > 0\n"
						+ "GROUP BY p.jan_sales_SUM, u.month_name, u.sales_amount\n"
						+ "HAVING u.sales_amount > 10\n"
						+ "ORDER BY p.jan_sales_SUM, u.month_name;";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
''' + GOLDEN_BLOCK + r'''
	}

	/** Matrix: subset=E | topo=S3 (U–P–U) | bucket=ORDER_BY | kind=derived | outcome=unhappy. */
	@Test
	public void gapFill17_7_7_S3UnpivotPivotUnpivotOrderByAmbiguousDerivedMonthNameFatalV1Test() {
		final String query =
				"SELECT p.jan_sales_SUM\n"
						+ "FROM monthly_sales u1_src\n"
						+ "UNPIVOT (sales_amount FOR month_name IN (jan_sales, feb_sales)) u1\n"
						+ "JOIN monthly_sales_long p_src\n"
						+ "PIVOT (SUM(sales_amount) FOR month_name IN ('jan_sales')) p\n"
						+ "  ON u1.sales_amount = p.jan_sales_SUM\n"
						+ "JOIN monthly_sales u2_src\n"
						+ "UNPIVOT (sales_amount FOR month_name IN (feb_sales, mar_sales)) u2\n"
						+ "  ON p.jan_sales_SUM = u2.sales_amount\n"
						+ "ORDER BY month_name;";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertFatalDiagnosticAtPosition(
				extractor.getSnippet(),
				"AMBIGUOUS_DERIVED_COLUMN_REFERENCE",
				"Ambiguous derived column reference 'month_name' at (l:10 c:9). Possible sources: [u1, u2]",
				"month_name",
				10,
				9);
''' + GOLDEN_BLOCK + r'''
	}

	/** Matrix: subset=E | topo=S3 (P–U–P) | bucket=HAVING | kind=derived | outcome=unhappy. */
	@Test
	public void gapFill17_7_7_S3PivotUnpivotPivotHavingAmbiguousDerivedFebSalesSumFatalV1Test() {
		final String query =
				"SELECT p.jan_sales_SUM, u.month_name, u.sales_amount\n"
						+ "FROM monthly_sales_long p_src\n"
						+ "PIVOT (SUM(sales_amount) FOR month_name IN ('jan_sales', 'feb_sales')) p\n"
						+ "JOIN monthly_sales u_src\n"
						+ "UNPIVOT (sales_amount FOR month_name IN (jan_sales, feb_sales)) u\n"
						+ "  ON p.jan_sales_SUM = u.sales_amount AND u.month_name = 'jan_sales'\n"
						+ "JOIN monthly_sales_long q_src\n"
						+ "PIVOT (SUM(sales_amount) FOR month_name IN ('feb_sales')) q\n"
						+ "  ON u.sales_amount = q.feb_sales_SUM\n"
						+ "HAVING feb_sales_SUM > 0;";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertFatalDiagnosticAtPosition(
				extractor.getSnippet(),
				"AMBIGUOUS_DERIVED_COLUMN_REFERENCE",
				"Ambiguous derived column reference 'feb_sales_SUM' at (l:11 c:8). Possible sources: [p, q]",
				"feb_sales_SUM",
				11,
				8);
		assertDiagnosticAtPosition(
				extractor.getSnippet(),
				"AMBIGUOUS_COLUMN_REFERENCE",
				ParseDiagnostic.Severity.SEVERE_WARNING,
				"Ambiguous column reference 'month_name' at (l:1 c:26). Possible sources: [p, q]",
				"month_name",
				1,
				26);
		assertDiagnosticAtPosition(
				extractor.getSnippet(),
				"AMBIGUOUS_COLUMN_REFERENCE",
				ParseDiagnostic.Severity.SEVERE_WARNING,
				"Ambiguous column reference 'sales_amount' at (l:1 c:40). Possible sources: [p, q]",
				"sales_amount",
				1,
				40);
''' + GOLDEN_BLOCK + r'''
	}

	/** Matrix: subset=E | topo=S2-PP | bucket=GROUP_BY | kind=derived | outcome=unhappy. */
	@Test
	public void gapFill17_7_7_S2PpDualPivotGroupByAmbiguousDerivedJanSalesSumFatalV1Test() {
		final String query =
				"SELECT p.jan_sales_SUM\n"
						+ "FROM monthly_sales_long p_src\n"
						+ "PIVOT (SUM(sales_amount) FOR month_name IN ('jan_sales')) p\n"
						+ "JOIN monthly_sales_long q_src\n"
						+ "PIVOT (SUM(sales_amount) FOR month_name IN ('jan_sales')) q\n"
						+ "  ON p.jan_sales_SUM = q.jan_sales_SUM\n"
						+ "GROUP BY jan_sales_SUM;";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertFatalDiagnosticAtPosition(
				extractor.getSnippet(),
				"AMBIGUOUS_DERIVED_COLUMN_REFERENCE",
				"Ambiguous derived column reference 'jan_sales_SUM' at (l:7 c:9). Possible sources: [p, q]",
				"jan_sales_SUM",
				7,
				9);
''' + GOLDEN_BLOCK + r'''
	}

	/** Matrix: subset=E | topo=S2-PU | bucket=QUALIFY | kind=derived (qualified) | outcome=happy. */
	@Test
	public void gapFill17_7_7_S2PuQualifyDerivedQualifiedHappyV1Test() {
		final String query =
				"SELECT p.jan_sales_SUM, u.month_name, u.sales_amount\n"
						+ "FROM monthly_sales_long p_src\n"
						+ "PIVOT (SUM(sales_amount) FOR month_name IN ('jan_sales')) p\n"
						+ "JOIN monthly_sales u_src\n"
						+ "UNPIVOT (sales_amount FOR month_name IN (jan_sales, feb_sales)) u\n"
						+ "  ON p.jan_sales_SUM = u.sales_amount AND u.month_name = 'jan_sales'\n"
						+ "QUALIFY u.sales_amount > 10;";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
''' + GOLDEN_BLOCK + r'''
	}

	/** Matrix: subset=E | topo=S2-PP | bucket=ORDER_BY | kind=source (unqualified) | outcome=unhappy (SEVERE). */
	@Test
	public void gapFill17_7_7_S2PpDualPivotOrderByAmbiguousSourceMonthNameSevereV1Test() {
		final String query =
				"SELECT p.jan_sales_SUM, q.feb_sales_SUM\n"
						+ "FROM monthly_sales_long p_src\n"
						+ "PIVOT (SUM(sales_amount) FOR month_name IN ('jan_sales')) p\n"
						+ "JOIN monthly_sales_long q_src\n"
						+ "PIVOT (SUM(sales_amount) FOR month_name IN ('feb_sales')) q\n"
						+ "  ON p.jan_sales_SUM > 0 AND q.feb_sales_SUM > 0\n"
						+ "ORDER BY month_name;";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoFatalErrors(extractor);
		assertDiagnosticAtPosition(
				extractor.getSnippet(),
				"AMBIGUOUS_COLUMN_REFERENCE",
				ParseDiagnostic.Severity.SEVERE_WARNING,
				"Ambiguous column reference 'month_name' at (l:7 c:9). Possible sources: [p, q]",
				"month_name",
				7,
				9);
''' + GOLDEN_BLOCK + r'''
	}

	/** Matrix: subset=E | topo=S2-PP | bucket=GROUP_BY,HAVING | kind=derived (qualified) | outcome=happy. */
	@Test
	public void gapFill17_7_7_S2PpDualPivotGroupByHavingQualifiedDerivedHappyV1Test() {
		final String query =
				"SELECT p.jan_sales_SUM, q.feb_sales_SUM\n"
						+ "FROM monthly_sales_long p_src\n"
						+ "PIVOT (SUM(sales_amount) FOR month_name IN ('jan_sales')) p\n"
						+ "JOIN monthly_sales_long q_src\n"
						+ "PIVOT (SUM(sales_amount) FOR month_name IN ('feb_sales')) q\n"
						+ "  ON p.jan_sales_SUM > 0 AND q.feb_sales_SUM > 0\n"
						+ "GROUP BY p.jan_sales_SUM, q.feb_sales_SUM\n"
						+ "HAVING p.jan_sales_SUM > 0 AND q.feb_sales_SUM > 0;";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
''' + GOLDEN_BLOCK + r'''
	}

	/** Matrix: subset=E | topo=S3 (P–U–P) | bucket=JOIN ON | kind=derived (qualified) | outcome=happy. */
	@Test
	public void gapFill17_7_7_S3PivotUnpivotPivotJoinOnQualifiedDerivedHappyV1Test() {
		final String query =
				"SELECT p.jan_sales_SUM\n"
						+ "FROM monthly_sales_long p_src\n"
						+ "PIVOT (SUM(sales_amount) FOR month_name IN ('jan_sales', 'feb_sales')) p\n"
						+ "JOIN monthly_sales u_src\n"
						+ "UNPIVOT (sales_amount FOR month_name IN (jan_sales, feb_sales)) u\n"
						+ "  ON p.jan_sales_SUM = u.sales_amount AND u.month_name = 'jan_sales'\n"
						+ "JOIN monthly_sales_long q_src\n"
						+ "PIVOT (SUM(sales_amount) FOR month_name IN ('feb_sales')) q\n"
						+ "  ON u.sales_amount = q.feb_sales_SUM AND p.jan_sales_SUM > 0;";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
''' + GOLDEN_BLOCK + r'''
	}

	/** Matrix: subset=E | topo=S2-PU | bucket=GROUP_BY | kind=source IN-list (unqualified) | outcome=SEVERE. */
	@Test
	public void gapFill17_7_7_S2PuGroupByUnqualifiedPhysicalInListJanSalesSevereV1Test() {
		final String query =
				"SELECT p.jan_sales_SUM, u.month_name, u.sales_amount\n"
						+ "FROM monthly_sales_long p_src\n"
						+ "PIVOT (SUM(sales_amount) FOR month_name IN ('jan_sales')) p\n"
						+ "JOIN monthly_sales u_src\n"
						+ "UNPIVOT (sales_amount FOR month_name IN (jan_sales, feb_sales)) u\n"
						+ "  ON p.jan_sales_SUM = u.sales_amount AND u.month_name = 'jan_sales'\n"
						+ "GROUP BY jan_sales;";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoFatalErrors(extractor);
		assertDiagnosticAtPosition(
				extractor.getSnippet(),
				"AMBIGUOUS_COLUMN_REFERENCE",
				ParseDiagnostic.Severity.SEVERE_WARNING,
				"Ambiguous column reference 'jan_sales' at (l:8 c:9). Possible sources: [p, u]",
				"jan_sales",
				8,
				9);
''' + GOLDEN_BLOCK + r'''
	}

'''

def main() -> None:
    text = TEST_JAVA.read_text(encoding="utf-8")
    start = text.index("\t// --- §17.7.7-gap-fill")
    end = text.index("\t// --- Phase 17.7.8 closeout")
    text = text[:start] + TESTS + "\n" + text[end:]
    TEST_JAVA.write_text(text, encoding="utf-8")
    print("Scaffolded gap-fill tests")


if __name__ == "__main__":
    main()
