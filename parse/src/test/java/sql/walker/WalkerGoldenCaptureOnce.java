package sql.walker;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

import sql.SQLSelectParserParser;

/**
 * Dev utility: prints {@code GOLDEN|testMethod|field|value} lines for pasting into walker tests.
 *
 * <ul>
 * <li>Edit {@link #INLINE_CASES} and run this class's {@code main} (IDE or {@code mvn exec:java}).</li>
 * <li>{@code main --pivot-unpivot} — all queries from classpath {@code /pivot_unpivot_queries.properties}
 *     (used by {@code parse/tools/refresh_pivot_unpivot_goldens.py}).</li>
 * <li>{@code main /path/to/capture.properties} — keys are test method names, values are SQL
 *     ({@code method.allowErrors=true} optional).</li>
 * </ul>
 */
public class WalkerGoldenCaptureOnce extends AbstractSqlParseEventWalkerTest {

	/**
	 * Ad-hoc capture: {@code { testMethodName, sql } } pairs. Leave empty when not in use.
	 */
	private static final String[][] INLINE_CASES = {};

	public static void main(String[] args) throws Exception {
		if (args.length > 0 && "--pivot-unpivot".equals(args[0])) {
			Properties props = loadPropertiesFromClasspath("/pivot_unpivot_queries.properties");
			runProperties(props, true);
			return;
		}
		if (args.length > 0) {
			Properties props = loadPropertiesFromFile(Path.of(args[0]));
			runProperties(props, true);
			return;
		}
		runInlineCases();
	}

	private static void runInlineCases() throws Exception {
		if (INLINE_CASES.length == 0) {
			System.err.println("INLINE_CASES is empty; pass a .properties path or use --pivot-unpivot");
			return;
		}
		WalkerGoldenCaptureOnce runner = new WalkerGoldenCaptureOnce();
		for (String[] c : INLINE_CASES) {
			captureOne(runner, c[0], c[1], false);
		}
	}

	private static void runProperties(Properties props, boolean allowErrorsKeys) throws Exception {
		WalkerGoldenCaptureOnce runner = new WalkerGoldenCaptureOnce();
		for (String methodName : props.stringPropertyNames()) {
			if (methodName.endsWith(".allowErrors")) {
				continue;
			}
			String query = props.getProperty(methodName);
			if (query == null || query.isBlank()) {
				continue;
			}
			boolean allowErrors = allowErrorsKeys
					&& "true".equalsIgnoreCase(props.getProperty(methodName + ".allowErrors", "false"));
			try {
				captureOne(runner, methodName, query, allowErrors);
			} catch (Throwable t) {
				System.err.println("SKIP|" + methodName + "|" + t.getMessage());
			}
		}
	}

	private static void captureOne(WalkerGoldenCaptureOnce runner, String methodName, String query,
			boolean allowErrors) throws Exception {
		SQLSelectParserParser parser = runner.parse(query);
		SqlParseEventWalker extractor;
		if (allowErrors) {
			ParserRunResult runResult = runner.runSQLParsertestAllowErrors(query, parser);
			extractor = runResult.getExtractor();
			if (extractor == null) {
				System.err.println("SKIP|" + methodName + "|null extractor");
				return;
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
	}

	private static Properties loadPropertiesFromClasspath(String resourcePath) throws Exception {
		Properties props = new Properties();
		try (InputStream in = WalkerGoldenCaptureOnce.class.getResourceAsStream(resourcePath)) {
			if (in == null) {
				throw new IllegalStateException("Missing classpath resource: " + resourcePath);
			}
			props.load(in);
		}
		return props;
	}

	private static Properties loadPropertiesFromFile(Path path) throws Exception {
		Properties props = new Properties();
		try (InputStream in = Files.newInputStream(path)) {
			props.load(in);
		}
		return props;
	}

	private static void emit(String method, String field, String value) {
		System.out.println("GOLDEN|" + method + "|" + field + "|" + value.replace("\n", "\\n"));
	}
}
