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

	/** M4: UNPIVOT VALUE operand in SELECT expression — VALUE sites on derivation + SELECT expression lineage. */
	@Test
	@Ignore("M4 partial — unpivot VALUE SELECT site contract pending scoped capture")
	public void unpivotValueOperandSelectExpressionSitesContractTest() {
		final String query =
				"SELECT sales_amount * 0.07 AS tax_on_value\n"
						+ "FROM monthly_sales\n"
						+ "UNPIVOT (sales_amount FOR month_name IN (jan_sales, feb_sales));";

		SqlParseEventWalker extractor = runParsertest(query, parse(query));
		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);

		String symbolFlat = extractor.getSymbolTable().toString();
		Assert.assertTrue(
				"Expected SELECT sales_amount site (1:7) on UNPIVOT VALUE derived_columns lineage",
				symbolFlat.contains("1:7")
						&& symbolFlat.toLowerCase(java.util.Locale.ROOT).contains("sales_amount"));
	}
}
