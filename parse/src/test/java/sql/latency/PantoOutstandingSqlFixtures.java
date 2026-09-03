package sql.latency;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

/**
 * Loads full SQL text for selected Panto outstanding-issue CSV rows from the RMCP handoff pack.
 */
final class PantoOutstandingSqlFixtures {

    private static final Path[] SQL_ROOT_CANDIDATES = {
            Path.of("docs/rmcp-handoff/5.1.3-panto-outstanding/sql"),
            Path.of("parse/docs/rmcp-handoff/5.1.3-panto-outstanding/sql"),
    };

    private static final Path[] CSV_CANDIDATES = {
            Path.of("docs/rmcp-handoff/5.1.3-panto-outstanding/panto_513_outstanding_issues.csv"),
            Path.of("parse/docs/rmcp-handoff/5.1.3-panto-outstanding/panto_513_outstanding_issues.csv"),
    };

    private PantoOutstandingSqlFixtures() {
    }

    static String sqlForCsvRow(int csvRow) throws IOException {
        Path file = resolveSqlRoot().resolve("csv-row-" + csvRow + ".sql");
        if (Files.isRegularFile(file)) {
            return Files.readString(file, StandardCharsets.UTF_8);
        }
        String fromManifest = loadFromManifest(csvRow);
        if (fromManifest != null) {
            return fromManifest;
        }
        return loadFromCsv(csvRow);
    }

    private static String loadFromManifest(int csvRow) throws IOException {
        Path[] manifestCandidates = {
                Path.of("parse/target/panto-timeout-batch-manifest.json"),
                Path.of("target/panto-timeout-batch-manifest.json"),
        };
        for (Path manifestPath : manifestCandidates) {
            if (!Files.isRegularFile(manifestPath)) {
                continue;
            }
            try (Reader reader = Files.newBufferedReader(manifestPath, StandardCharsets.UTF_8)) {
                ManifestPayload payload = new Gson().fromJson(reader, ManifestPayload.class);
                if (payload == null || payload.rows == null) {
                    continue;
                }
                for (ManifestRow row : payload.rows) {
                    if (row.csvRow == csvRow) {
                        return row.sql;
                    }
                }
            }
        }
        return null;
    }

    private static String loadFromCsv(int csvRow) throws IOException {
        Map<Integer, String> index = loadCsvIndex();
        String sql = index.get(csvRow);
        if (sql == null) {
            throw new IllegalStateException("No CSV row " + csvRow + " in outstanding issues pack");
        }
        return sql;
    }

    private static Map<Integer, String> loadCsvIndex() throws IOException {
        Path csv = resolveCsv();
        Map<Integer, String> byRow = new HashMap<>();
        try (BufferedReader reader = Files.newBufferedReader(csv, StandardCharsets.UTF_8)) {
            String header = reader.readLine();
            if (header == null || !header.startsWith("csv_row,")) {
                throw new IllegalStateException("Unexpected CSV header in " + csv);
            }
            String line;
            while ((line = reader.readLine()) != null) {
                int comma = line.indexOf(',');
                if (comma <= 0) {
                    continue;
                }
                int row = Integer.parseInt(line.substring(0, comma));
                byRow.put(row, extractQuerySqlField(line.substring(comma + 1)));
            }
        }
        return byRow;
    }

    private static String extractQuerySqlField(String afterCsvRow) {
        int fieldIndex = 0;
        int i = 0;
        while (i < afterCsvRow.length()) {
            if (afterCsvRow.charAt(i) == '"') {
                if (fieldIndex == 8) {
                    return readQuotedSql(afterCsvRow, i + 1);
                }
                i = endOfQuotedField(afterCsvRow, i);
                fieldIndex++;
                if (i < afterCsvRow.length() && afterCsvRow.charAt(i) == ',') {
                    i++;
                }
                continue;
            }
            int nextComma = afterCsvRow.indexOf(',', i);
            if (nextComma < 0) {
                if (fieldIndex == 8) {
                    return afterCsvRow.substring(i);
                }
                throw new IllegalStateException("query_sql field missing");
            }
            if (fieldIndex == 8) {
                return afterCsvRow.substring(i, nextComma);
            }
            i = nextComma + 1;
            fieldIndex++;
        }
        throw new IllegalStateException("query_sql field missing");
    }

    private static String readQuotedSql(String line, int start) {
        StringBuilder sql = new StringBuilder();
        int end = start;
        while (end < line.length()) {
            char c = line.charAt(end);
            if (c == '"') {
                if (end + 1 < line.length() && line.charAt(end + 1) == '"') {
                    sql.append('"');
                    end += 2;
                    continue;
                }
                break;
            }
            sql.append(c);
            end++;
        }
        return sql.toString();
    }

    private static int endOfQuotedField(String line, int openQuote) {
        int i = openQuote + 1;
        while (i < line.length()) {
            if (line.charAt(i) == '"') {
                if (i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    i += 2;
                    continue;
                }
                return i + 1;
            }
            i++;
        }
        return line.length();
    }

    private static Path resolveSqlRoot() {
        for (Path candidate : SQL_ROOT_CANDIDATES) {
            if (Files.isDirectory(candidate)) {
                return candidate;
            }
        }
        throw new IllegalStateException("Panto outstanding sql/ directory not found");
    }

    private static Path resolveCsv() {
        for (Path candidate : CSV_CANDIDATES) {
            if (Files.isRegularFile(candidate)) {
                return candidate;
            }
        }
        throw new IllegalStateException("Panto outstanding CSV not found");
    }

    static final class ManifestPayload {
        @SerializedName("rows")
        List<ManifestRow> rows;
    }

    static final class ManifestRow {
        @SerializedName("csv_row")
        int csvRow;

        @SerializedName("query_sql")
        String sql;
    }
}
