package sql.latency;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.junit.Ignore;
import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import access.SqlParserAccess;

import static mumble.SQLParserEndPoints.SQLPARSER_SQL_TREE_KEY;

/** One-off classifier for subMap walker failures on timeout_513 corpus. */
public class PantoSubMapFailureAnalysisTest {

    @Test
    @Ignore("Manual — classify subMap walker failures; requires panto-timeout-batch-manifest.json")
    public void classifyTimeout513SubMapFailures() throws IOException {
        Path manifest = Path.of("parse/target/panto-timeout-batch-manifest.json");
        if (!Files.isRegularFile(manifest)) {
            manifest = Path.of("target/panto-timeout-batch-manifest.json");
        }
        ManifestPayload payload = new Gson().fromJson(
                Files.newBufferedReader(manifest, StandardCharsets.UTF_8), ManifestPayload.class);

        System.out.println("csv_row,query_key,chars,walkerException,syntaxErrors,fatals,shapeFlags");
        int walkerExceptions = 0;
        for (ManifestRow row : payload.rows) {
            RowReport report = analyzeRow(row);
            if (report.walkerException != null) {
                walkerExceptions++;
            }
            System.out.println(report);
        }
        System.out.printf(Locale.ROOT, "SUMMARY walkerExceptions=%d rows=%d%n",
                walkerExceptions, payload.rows.size());
    }

    private static RowReport analyzeRow(ManifestRow row) {
        String shape = classifySqlShape(row.sql);
        String walkerEx = null;
        int syntaxErrors = -1;
        int fatals = -1;
        try {
            SqlParserAccess access = new SqlParserAccess(false, false, false);
            access.executeTheParse(row.sql, SQLPARSER_SQL_TREE_KEY);
            if (access.getParser() != null) {
                syntaxErrors = access.getParser().getNumberOfSyntaxErrors();
            }
            if (access.getSnippet() != null) {
                fatals = access.getSnippet().getFatalErrorCount();
            }
            List<String> fatalsList = access.getFatalErrorList();
            for (String msg : fatalsList) {
                if (msg != null && msg.contains("subMap")) {
                    walkerEx = msg;
                    break;
                }
            }
        } catch (Exception ex) {
            walkerEx = ex.getClass().getSimpleName() + ": " + ex.getMessage();
        }
        return new RowReport(row.csvRow, row.queryKey, row.sql.length(), walkerEx, syntaxErrors, fatals, shape);
    }

    private static String classifySqlShape(String sql) {
        List<String> flags = new ArrayList<>();
        String trimmed = sql.stripLeading();
        String upper = sql.toUpperCase(Locale.ROOT);

        if (trimmed.startsWith("--") || trimmed.startsWith("/*")) {
            flags.add("LEADING_COMMENT");
        }
        if (countTopLevelSemicolons(sql) > 0) {
            flags.add("MULTI_STATEMENT");
        }
        if (countLineStartsWith(sql, "select") > 1) {
            flags.add("MULTIPLE_SELECT_LINES");
        }
        if (upper.contains("UNION") && countLineStartsWith(sql, "select") >= 2
                && !upper.contains("UNION ALL") && !upper.matches("(?s).*\\bUNION\\b.*")) {
            flags.add("FRAGMENT_CANDIDATE");
        }
        if (sql.lines().filter(l -> l.strip().startsWith("--")).count() > 5) {
            flags.add("HEAVY_COMMENT");
        }
        if (!trimmed.regionMatches(true, 0, "SELECT", 0, 6)
                && !trimmed.regionMatches(true, 0, "WITH", 0, 4)
                && !trimmed.regionMatches(true, 0, "INSERT", 0, 6)
                && !trimmed.regionMatches(true, 0, "UPDATE", 0, 6)
                && !trimmed.regionMatches(true, 0, "DELETE", 0, 6)
                && !trimmed.regionMatches(true, 0, "CREATE", 0, 6)) {
            flags.add("NON_STANDARD_START");
        }
        if (upper.contains("PRACTICE") || upper.contains("SANDBOX") || upper.contains("EXPERIMENT")) {
            flags.add("PRACTICE_NAME");
        }
        if (flags.isEmpty()) {
            return "CLEAN_SHAPE";
        }
        return String.join("|", flags);
    }

    private static int countTopLevelSemicolons(String sql) {
        int count = 0;
        boolean inSingle = false;
        boolean inDouble = false;
        for (int i = 0; i < sql.length(); i++) {
            char c = sql.charAt(i);
            if (c == '\'' && !inDouble) {
                inSingle = !inSingle;
            } else if (c == '"' && !inSingle) {
                inDouble = !inDouble;
            } else if (c == ';' && !inSingle && !inDouble) {
                count++;
            }
        }
        return count;
    }

    private static int countLineStartsWith(String sql, String prefix) {
        int n = 0;
        for (String line : sql.split("\\R")) {
            String t = line.stripLeading();
            if (t.regionMatches(true, 0, prefix, 0, prefix.length())) {
                n++;
            }
        }
        return n;
    }

    private record RowReport(
            int csvRow,
            String queryKey,
            int chars,
            String walkerException,
            int syntaxErrors,
            int fatals,
            String shapeFlags) {
        @Override
        public String toString() {
            return String.format(Locale.ROOT, "%d,%s,%d,%s,%d,%d,%s",
                    csvRow,
                    csvQuote(queryKey),
                    chars,
                    walkerException == null ? "" : csvQuote(walkerException),
                    syntaxErrors,
                    fatals,
                    shapeFlags);
        }

        private static String csvQuote(String value) {
            if (value == null) {
                return "";
            }
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
    }

    static final class ManifestPayload {
        @SerializedName("rows")
        List<ManifestRow> rows;
    }

    static final class ManifestRow {
        @SerializedName("csv_row")
        int csvRow;

        @SerializedName("query_key")
        String queryKey;

        @SerializedName("query_sql")
        String sql;
    }
}
