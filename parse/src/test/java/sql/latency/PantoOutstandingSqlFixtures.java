package sql.latency;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

/**
 * Loads frozen SQL fixtures for Panto outstanding-issue CSV rows from checked-in
 * {@code sql/csv-row-<n>.sql} files. Tests do not read {@code panto_513_outstanding_issues.csv}
 * at runtime — update the sql file when intentionally refreshing a fixture.
 */
final class PantoOutstandingSqlFixtures {

    private static final Path[] HANDOFF_ROOT_CANDIDATES = {
            Path.of("docs/rmcp-handoff/5.1.3-panto-outstanding"),
            Path.of("parse/docs/rmcp-handoff/5.1.3-panto-outstanding"),
    };

    private PantoOutstandingSqlFixtures() {
    }

    static String sqlForCsvRow(int csvRow) throws IOException {
        Path file = resolveSqlFile(csvRow);
        if (!Files.isRegularFile(file)) {
            throw new IllegalStateException(
                    "Missing frozen SQL fixture csv-row-" + csvRow + ".sql under handoff sql/");
        }
        return Files.readString(file, StandardCharsets.UTF_8);
    }

    /** Sorted {@code csv_row} values for the 74-row {@code timeout_513} E3 corpus. */
    static List<Integer> timeout513CorpusRows() throws IOException {
        Path index = resolveHandoffRoot().resolve("timeout-513-corpus-rows.json");
        if (!Files.isRegularFile(index)) {
            throw new IllegalStateException("Missing timeout-513-corpus-rows.json under handoff pack");
        }
        try (Reader reader = Files.newBufferedReader(index, StandardCharsets.UTF_8)) {
            List<Integer> rows = new Gson().fromJson(
                    reader, TypeToken.getParameterized(List.class, Integer.class).getType());
            if (rows == null || rows.isEmpty()) {
                throw new IllegalStateException("Empty timeout-513-corpus-rows.json");
            }
            List<Integer> copy = new ArrayList<>(rows);
            Collections.sort(copy);
            return copy;
        }
    }

    /** Cluster B rows (E3 fast-FATAL) adjudicated as SLL-only false positives → **2.10**. */
    static List<Integer> clusterB210PendingRows() throws IOException {
        return readCsvRowIndex("pending_2_10_csv_rows");
    }

    /** Cluster B bound-query fast-FATAL rows (excludes utility workbook row 4197). */
    static List<Integer> clusterBE3FastFatalRows() throws IOException {
        return readCsvRowIndex("csv_rows");
    }

    /** Rows excluded from bound-query correctness gates (utility workbooks, etc.). */
    static List<Integer> corpusExclusionRows() {
        return List.copyOf(PantoCorpusExclusionList.excludedCsvRows());
    }

    private static List<Integer> readCsvRowIndex(String field) throws IOException {
        Path index = resolveHandoffRoot().resolve("cluster-b-sll-regression-rows.json");
        if (!Files.isRegularFile(index)) {
            throw new IllegalStateException("Missing cluster-b-sll-regression-rows.json under handoff pack");
        }
        try (Reader reader = Files.newBufferedReader(index, StandardCharsets.UTF_8)) {
            ClusterBIndexPayload payload = new Gson().fromJson(reader, ClusterBIndexPayload.class);
            List<Integer> rows = field.equals("pending_2_10_csv_rows")
                    ? payload.pending210CsvRows
                    : payload.csvRows;
            if (rows == null || rows.isEmpty()) {
                throw new IllegalStateException("Empty cluster-b index field: " + field);
            }
            return List.copyOf(rows);
        }
    }

    private static Path resolveSqlFile(int csvRow) {
        return resolveHandoffRoot().resolve("sql").resolve("csv-row-" + csvRow + ".sql");
    }

    private static Path resolveHandoffRoot() {
        for (Path candidate : HANDOFF_ROOT_CANDIDATES) {
            if (Files.isDirectory(candidate.resolve("sql"))) {
                return candidate;
            }
        }
        throw new IllegalStateException("Panto outstanding handoff directory not found");
    }

    /** Legacy manifest shape — used only by optional manual benchmark tests. */
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

    static final class ClusterBIndexPayload {
        @SerializedName("csv_rows")
        List<Integer> csvRows;

        @SerializedName("pending_2_10_csv_rows")
        List<Integer> pending210CsvRows;
    }
}
