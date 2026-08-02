package sql.walker;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

import sql.SQLSelectParserParser;

/**
 * Emits walker goldens for {@code SqlEventWalkerPivotUnpivotTests} (one line per field per test).
 * Run via {@code parse/tools/refresh_pivot_unpivot_goldens.py}.
 */
public class PivotUnpivotGoldenCaptureOnce extends AbstractSqlParseEventWalkerTest {

	public static void main(String[] args) throws Exception {
		Properties props = new Properties();
		try (InputStream in = PivotUnpivotGoldenCaptureOnce.class
				.getResourceAsStream("/pivot_unpivot_queries.properties")) {
			if (in == null) {
				throw new IllegalStateException("Missing pivot_unpivot_queries.properties on classpath");
			}
			props.load(in);
		}
		for (String methodName : props.stringPropertyNames()) {
			String query = props.getProperty(methodName);
			if (query == null || query.isBlank()) {
				continue;
			}
			try {
				PivotUnpivotGoldenCaptureOnce runner = new PivotUnpivotGoldenCaptureOnce();
				SQLSelectParserParser parser = runner.parse(query);
				SqlParseEventWalker extractor;
				if (props.getProperty(methodName + ".allowErrors", "false").equals("true")) {
					ParserRunResult runResult = runner.runSQLParsertestAllowErrors(query, parser);
					extractor = runResult.getExtractor();
					if (extractor == null) {
						System.err.println("SKIP|" + methodName + "|null extractor");
						continue;
					}
				} else {
					extractor = runner.runParsertest(query, parser);
				}
				emit(methodName, "AST", extractor.getAsTree().toString());
				emit(methodName, "Interface", extractor.getInterface().toString());
				emit(methodName, "Substitution", extractor.getSubstitutionsMap().toString());
				emit(methodName, "TableDictionary", extractor.getTableColumnDictionaryMap().toString());
				emit(methodName, "QueryDictionary", extractor.getQueryColumnDictionaryMap().toString());
				emit(methodName, "SymbolTable", extractor.getSymbolTable().toString());
			} catch (Throwable t) {
				System.err.println("SKIP|" + methodName + "|" + t.getMessage());
			}
		}
	}

	private static void emit(String method, String field, String value) {
		System.out.println("GOLDEN|" + method + "|" + field + "|" + value.replace("\n", "\\n"));
	}
}
