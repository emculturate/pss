package sql.walker;

import sql.SQLSelectParserParser;

/** One-off goldens for pivot walker tier coverage — {@code mvn -q -Dtest=PivotWalkerTierGoldenCaptureOnce test}. */
public class PivotWalkerTierGoldenCaptureOnce extends AbstractSqlParseEventWalkerTest {

	public static void main(String[] args) throws Exception {
		String[][] cases = {
				{ "pivotInSubqueryAstShapeTest",
						"SELECT empid\n"
								+ "FROM monthly_sales_long\n"
								+ "PIVOT (SUM(sales_amount) FOR month_name IN (\n"
								+ "  SELECT DISTINCT month_name FROM monthly_sales_long))\n"
								+ "WHERE empid > 0;" },
				{ "pivotInLiteralStringWithAsPrefixAstShapeTest",
						"SELECT jan_sales_SUM\n"
								+ "FROM monthly_sales_long\n"
								+ "PIVOT (SUM(sales_amount) FOR month_name IN ('jan_sales' AS jan_pivot, 'feb_sales'));" },
				{ "unpivotClauseInListItemAliasesAstShapeTest",
						"SELECT empid, month_name, sales_amount\n"
								+ "FROM monthly_sales\n"
								+ "UNPIVOT (sales_amount FOR month_name IN (\n"
								+ "  jan_sales AS JAN, feb_sales AS FEB, mar_sales));" },
		};
		PivotWalkerTierGoldenCaptureOnce runner = new PivotWalkerTierGoldenCaptureOnce();
		for (String[] c : cases) {
			String name = c[0];
			String query = c[1];
			SQLSelectParserParser parser = runner.parse(query);
			SqlParseEventWalker extractor = runner.runParsertest(query, parser);
			emit(name, "AST", extractor.getAsTree().toString());
			emit(name, "Interface", extractor.getInterface().toString());
			emit(name, "Substitution", extractor.getSubstitutionsMap().toString());
			emit(name, "TableDictionary", extractor.getTableColumnDictionaryMap().toString());
			emit(name, "QueryDictionary", extractor.getQueryColumnDictionaryMap().toString());
			emit(name, "SymbolTable", extractor.getSymbolTable().toString());
		}
	}

	private static void emit(String method, String field, String value) {
		System.out.println("GOLDEN|" + method + "|" + field + "|" + value);
	}
}
