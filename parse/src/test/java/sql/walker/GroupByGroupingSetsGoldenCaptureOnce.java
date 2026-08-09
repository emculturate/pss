package sql.walker;

import sql.SQLSelectParserParser;

/** Capture goldens for {@link SqlEventWalkerGroupByGroupingSetsTests}. */
public class GroupByGroupingSetsGoldenCaptureOnce extends AbstractSqlParseEventWalkerTest {

	public static void main(String[] args) throws Exception {
		String[][] cases = SqlEventWalkerGroupByGroupingSetsTests.allCases();
		GroupByGroupingSetsGoldenCaptureOnce runner = new GroupByGroupingSetsGoldenCaptureOnce();
		for (String[] c : cases) {
			String name = c[0];
			String query = c[1];
			try {
				SQLSelectParserParser parser = runner.parse(query);
				SqlParseEventWalker extractor = runner.runParsertest(query, parser);
				emit(name, "AST", extractor.getAsTree().toString());
			} catch (Throwable ex) {
				System.err.println("SKIP|" + name + "|" + ex.getMessage());
			}
		}
	}

	private static void emit(String method, String field, String value) {
		System.out.println("GOLDEN|" + method + "|" + field + "|" + value);
	}
}
