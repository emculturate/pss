package sql.latency;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
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
        return sqlFromHandoffCsvOnly(csvRow);
    }

    /** Loads row SQL from {@code panto_513_outstanding_issues.csv} only (no manifest). */
    static String sqlFromHandoffCsvOnly(int csvRow) throws IOException {
        String sql = loadFromCsv(csvRow);
        if (sql == null) {
            throw new IllegalStateException("No CSV row " + csvRow + " in outstanding issues pack");
        }
        return sql;
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
        return loadCsvIndex().get(csvRow);
    }

    private static Map<Integer, String> loadCsvIndex() throws IOException {
        Path csv = resolveCsv();
        String content = Files.readString(csv, StandardCharsets.UTF_8);
        List<String[]> records = parseCsvRecords(content);
        if (records.isEmpty()) {
            throw new IllegalStateException("Empty CSV: " + csv);
        }
        String[] header = records.get(0);
        int csvRowIndex = -1;
        int querySqlIndex = -1;
        for (int i = 0; i < header.length; i++) {
            if ("csv_row".equals(header[i])) {
                csvRowIndex = i;
            } else if ("query_sql".equals(header[i])) {
                querySqlIndex = i;
            }
        }
        if (csvRowIndex < 0 || querySqlIndex < 0) {
            throw new IllegalStateException("Unexpected CSV header in " + csv);
        }
        Map<Integer, String> byRow = new HashMap<>();
        for (int r = 1; r < records.size(); r++) {
            String[] fields = records.get(r);
            if (fields.length <= Math.max(csvRowIndex, querySqlIndex)) {
                continue;
            }
            byRow.put(Integer.parseInt(fields[csvRowIndex]), fields[querySqlIndex]);
        }
        return byRow;
    }

    /**
     * RFC 4180-style CSV parse (quoted fields may contain newlines and doubled quotes).
     */
    private static List<String[]> parseCsvRecords(String content) {
        List<String[]> records = new ArrayList<>();
        List<String> fields = new ArrayList<>();
        StringBuilder field = new StringBuilder();
        boolean inQuotes = false;
        for (int i = 0; i < content.length(); i++) {
            char c = content.charAt(i);
            if (inQuotes) {
                if (c == '"') {
                    if (i + 1 < content.length() && content.charAt(i + 1) == '"') {
                        field.append('"');
                        i++;
                    } else {
                        inQuotes = false;
                    }
                } else {
                    field.append(c);
                }
                continue;
            }
            if (c == '"') {
                inQuotes = true;
            } else if (c == ',') {
                fields.add(field.toString());
                field.setLength(0);
            } else if (c == '\r') {
                // ignore
            } else if (c == '\n') {
                fields.add(field.toString());
                field.setLength(0);
                records.add(fields.toArray(String[]::new));
                fields = new ArrayList<>();
            } else {
                field.append(c);
            }
        }
        if (!field.isEmpty() || !fields.isEmpty() || inQuotes) {
            fields.add(field.toString());
            records.add(fields.toArray(String[]::new));
        }
        return records;
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
