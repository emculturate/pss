package sql.walker;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import sql.SQLSelectParserParser;

/**
 * Lineage contracts for relational-modifier consolidation (PIVOT / UNPIVOT). Remove {@link Ignore} per
 * migration phase in {@code parse/documents/coverage/relational-modifier-lineage-consolidation-migration.md}.
 */
public class ModifierLineageConsolidationContractTests extends AbstractSqlParseEventWalkerTest {

	/** M3: pivot-operand interface egress must bind SELECT expression sites, not only post-pivot clauses. */
	@Test
	public void pivotInAnySelectExpressionOperandSelectSitesContractTest() {
		final String query =
				"SELECT empid * 2 AS emp_doubled, sales_amount + units AS amt_plus_units\n"
						+ "FROM monthly_sales_long\n"
						+ "PIVOT (SUM(sales_amount) FOR month_name IN (ANY ORDER BY empid, units))\n"
						+ "WHERE empid + units > 1\n"
						+ "GROUP BY sales_amount + units\n"
						+ "ORDER BY empid + units;";

		SqlParseEventWalker extractor = runParsertest(query, parse(query));
		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);

		String tableDict = extractor.getTableColumnDictionaryMap().toString();
		SqlEventWalkerPivotUnpivotTests.assertTableDictionaryContainsAntlrSite(
				tableDict, "monthly_sales_long", "empid", 1, 7);
		SqlEventWalkerPivotUnpivotTests.assertTableDictionaryContainsAntlrSite(
				tableDict, "monthly_sales_long", "sales_amount", 1, 33);
		SqlEventWalkerPivotUnpivotTests.assertTableDictionaryContainsAntlrSite(
				tableDict, "monthly_sales_long", "units", 1, 48);
	}

	/** M3: static IN pivot — ratio expression uses aggregate operand; SELECT sales_amount site must remain. */
	@Test
	public void pivotSelectExpressionAggregateOperandSelectSitesContractTest() {
		final String query =
				"SELECT sales_amount / jan_sales_SUM AS ratio_to_pivot\n"
						+ "FROM monthly_sales_long\n"
						+ "PIVOT (SUM(sales_amount) FOR month_name IN ('jan_sales'));";

		SqlParseEventWalker extractor = runParsertest(query, parse(query));
		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);

		String tableDict = extractor.getTableColumnDictionaryMap().toString();
		SqlEventWalkerPivotUnpivotTests.assertTableDictionaryContainsAntlrSite(
				tableDict, "monthly_sales_long", "sales_amount", 1, 7);
	}

	/**
	 * M4 (scoped): UNPIVOT VALUE in a SELECT expression — published lineage does not require the
	 * SELECT operand token on {@code derived_columns.sales_amount}; we require UNPIVOT derivation
	 * (VALUE + IN sources) and output interface deps that trace to those physical sources after
	 * convert egress expands VALUE refs to IN-list columns.
	 */
	@Test
	public void unpivotValueOperandSelectExpressionSitesContractTest() {
		final String query =
				"SELECT sales_amount * 0.07 AS tax_on_value\n"
						+ "FROM monthly_sales\n"
						+ "UNPIVOT (sales_amount FOR month_name IN (jan_sales, feb_sales));";

		SqlParseEventWalker extractor = runParsertest(query, parse(query));
		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("[tax_on_value]", extractor.getInterface().toString());

		String symbolFlat = extractor.getSymbolTable().toString();
		String lower = symbolFlat.toLowerCase(java.util.Locale.ROOT);
		Assert.assertTrue(
				"Expected UNPIVOT VALUE column sales_amount on structured derived_columns",
				lower.contains("derived_columns")
						&& lower.contains("tuple_0")
						&& lower.contains("sales_amount"));
		Assert.assertTrue(
				"Expected UNPIVOT IN sources on derivation.source_columns",
				lower.contains("source_columns")
						&& lower.contains("jan_sales")
						&& lower.contains("feb_sales")
						&& lower.contains("monthly_sales"));
		Assert.assertTrue(
				"Expected tax_on_value to retain UNPIVOT VALUE hop and IN-list physical deps",
				symbolFlat.contains("tax_on_value=[{name=sales_amount, table_ref=tuple_0}")
						&& symbolFlat.contains("jan_sales")
						&& symbolFlat.contains("feb_sales")
						&& symbolFlat.contains("monthly_sales"));
	}
}
