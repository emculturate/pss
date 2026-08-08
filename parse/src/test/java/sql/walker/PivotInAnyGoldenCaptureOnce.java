package sql.walker;

import sql.SQLSelectParserParser;

/** One-off goldens for pivot IN (ANY) tests — run via {@code mvn -q -Dtest=PivotInAnyGoldenCaptureOnce test}. */
public class PivotInAnyGoldenCaptureOnce extends AbstractSqlParseEventWalkerTest {

	public static void main(String[] args) throws Exception {
		String[][] cases = {
				{ "pivotInAnyAstShapeTest",
						"SELECT empid\nFROM monthly_sales_long\n"
								+ "PIVOT (SUM(sales_amount) FOR month_name IN (ANY))\n"
								+ "WHERE sales_amount > 0;" },
				{ "pivotInAnyOrderByMonthNameAstShapeTest",
						"SELECT empid\nFROM monthly_sales_long\n"
								+ "PIVOT (SUM(sales_amount) FOR month_name IN (ANY ORDER BY month_name))\n"
								+ "WHERE month_name = 'jan_sales';" },
				{ "pivotInAnyOrderByMonthNameAndEmpidWithWhereTest",
						"SELECT empid\nFROM monthly_sales_long\n"
								+ "PIVOT (SUM(sales_amount) FOR month_name IN (ANY ORDER BY month_name, empid))\n"
								+ "WHERE month_name = 'jan_sales' AND empid > 0;" },
				{ "pivotInAnyOrderByNonForColumnsWithWhereTest",
						"SELECT empid\nFROM monthly_sales_long\n"
								+ "PIVOT (SUM(sales_amount) FOR month_name IN (ANY ORDER BY empid, units))\n"
								+ "WHERE empid > 0 AND units > 1;" },
				{ "pivotInAnyWhereResolvesForAndAggregateSourceTest",
						"SELECT empid\nFROM monthly_sales_long\n"
								+ "PIVOT (SUM(sales_amount) FOR month_name IN (ANY))\n"
								+ "WHERE month_name = 'jan_sales' AND sales_amount > 0;" },
		};
		PivotInAnyGoldenCaptureOnce runner = new PivotInAnyGoldenCaptureOnce();
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
