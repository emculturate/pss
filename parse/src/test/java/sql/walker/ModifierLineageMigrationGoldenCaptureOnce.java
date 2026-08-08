package sql.walker;

import sql.SQLSelectParserParser;

/** One-off goldens for modifier formula lineage migration tests. */
public class ModifierLineageMigrationGoldenCaptureOnce extends AbstractSqlParseEventWalkerTest {

	public static void main(String[] args) throws Exception {
		String[][] cases = {
				{ "pivotSelectAndPostClausesFormulaLineageMigrationTest",
						"SELECT empid * 2 AS emp_doubled, jan_sales * 0.07 AS tax_on_jan,\n"
								+ "  sales_amount / jan_sales_SUM AS ratio_to_pivot, mar_sales_SUM\n"
								+ "FROM monthly_sales_long\n"
								+ "PIVOT (SUM(sales_amount) FOR month_name IN ('jan_sales', 'feb_sales', 'mar_sales'))\n"
								+ "WHERE empid > 100 AND sales_amount + units > 0\n"
								+ "GROUP BY sales_amount / jan_sales_SUM\n"
								+ "ORDER BY feb_sales_SUM + mar_sales_SUM;" },
				{ "pivotInAnySelectExpressionOperandLineageMigrationTest",
						"SELECT empid * 2 AS emp_doubled, sales_amount + units AS amt_plus_units\n"
								+ "FROM monthly_sales_long\n"
								+ "PIVOT (SUM(sales_amount) FOR month_name IN (ANY ORDER BY empid, units))\n"
								+ "WHERE empid + units > 1\n"
								+ "GROUP BY sales_amount + units\n"
								+ "ORDER BY empid + units;" },
				{ "unpivotSelectAndPostClausesFormulaLineageMigrationTest",
						"SELECT empid, month_name, sales_amount * 0.07 AS tax_on_value,\n"
								+ "  sales_amount / units AS amt_per_unit, jan_sales + feb_sales AS q1_sum\n"
								+ "FROM monthly_sales\n"
								+ "UNPIVOT (sales_amount FOR month_name IN (jan_sales AS JAN, feb_sales AS FEB, mar_sales AS MAR))\n"
								+ "WHERE sales_amount * 0.07 > 1 AND month_name = 'JAN'\n"
								+ "GROUP BY month_name, sales_amount / units\n"
								+ "ORDER BY jan_sales + feb_sales;" },
		};
		ModifierLineageMigrationGoldenCaptureOnce runner = new ModifierLineageMigrationGoldenCaptureOnce();
		for (String[] c : cases) {
			String name = c[0];
			String query = c[1];
			SQLSelectParserParser parser = runner.parse(query);
			SqlParseEventWalker extractor = runner.runParsertest(query, parser);
			emit(name, "AST", extractor.getAsTree().toString());
			emit(name, "Interface", extractor.getInterface().toString());
			emit(name, "TableDictionary", extractor.getTableColumnDictionaryMap().toString());
			emit(name, "QueryDictionary", extractor.getQueryColumnDictionaryMap().toString());
			emit(name, "SymbolTable", extractor.getSymbolTable().toString());
		}
	}

	private static void emit(String method, String field, String value) {
		System.out.println("GOLDEN|" + method + "|" + field + "|" + value);
	}
}
