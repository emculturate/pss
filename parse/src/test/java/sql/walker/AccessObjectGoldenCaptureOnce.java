package sql.walker;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

import access.Snippet;
import access.SqlParserAccess;

/**
 * Dev utility: prints {@code GOLDEN|testMethod|field|value} lines for AccessObject walker tests
 * ({@code snippet.get*().toString()} goldens).
 *
 * <p>Usage: {@code java ... AccessObjectGoldenCaptureOnce /path/to/capture.properties}
 *
 * <p>Properties keys:
 * <ul>
 * <li>{@code methodName}=SQL query</li>
 * <li>{@code methodName.endpoint}=grammar endpoint value (e.g. {@code SQL}, {@code INSERT})</li>
 * <li>{@code methodName.fatalErrors}=expected fatal count for failed-syntax tests (omit for success)</li>
 * </ul>
 */
public class AccessObjectGoldenCaptureOnce {

	public static void main(String[] args) throws Exception {
		if (args.length == 0) {
			System.err.println("Usage: AccessObjectGoldenCaptureOnce /path/to/capture.properties");
			return;
		}
		Properties props = loadPropertiesFromFile(Path.of(args[0]));
		for (String methodName : props.stringPropertyNames()) {
			if (methodName.endsWith(".endpoint") || methodName.endsWith(".fatalErrors")) {
				continue;
			}
			String query = props.getProperty(methodName);
			if (query == null || query.isBlank()) {
				continue;
			}
			String endpoint = props.getProperty(methodName + ".endpoint", "SQL");
			String fatalErrorsKey = methodName + ".fatalErrors";
			boolean expectFatal = props.containsKey(fatalErrorsKey);
			int expectedFatalCount = expectFatal ? Integer.parseInt(props.getProperty(fatalErrorsKey)) : 0;
			try {
				captureOne(methodName, query, endpoint, expectFatal, expectedFatalCount);
			} catch (Throwable t) {
				System.err.println("SKIP|" + methodName + "|" + t.getMessage());
			}
		}
	}

	private static void captureOne(String methodName, String query, String endpoint, boolean expectFatal,
			int expectedFatalCount) {
		SqlParserAccess accessObject = new SqlParserAccess(true, true, true);
		accessObject.executeTheParse(query, endpoint);
		Snippet snippet = accessObject.getSnippet();
		if (snippet == null) {
			System.err.println("SKIP|" + methodName + "|null snippet");
			return;
		}
		int numErrors = snippet.getFatalErrorCount();
		if (expectFatal) {
			if (numErrors != expectedFatalCount) {
				throw new IllegalStateException("fatal count " + numErrors + " != expected " + expectedFatalCount
						+ " errors=" + snippet.getFatalErrorStringList());
			}
		} else if (numErrors != 0) {
			throw new IllegalStateException("expected no fatal errors but got " + numErrors + ": "
					+ snippet.getFatalErrorStringList());
		}
		emit(methodName, "AST", snippet.getSqlAbstractTree().toString());
		emit(methodName, "Interface", snippet.getQueryInterface().toString());
		emit(methodName, "Substitution", snippet.getSubstitutionsMap().toString());
		emit(methodName, "TableDictionary", snippet.getTableDictionary().toString());
		emit(methodName, "QueryDictionary", snippet.getQueryColumnDictionaryMap().toString());
		emit(methodName, "SymbolTable", snippet.getSymbolTable().toString());
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
