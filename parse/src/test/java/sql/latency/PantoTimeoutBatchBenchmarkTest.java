package sql.latency;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.junit.Ignore;
import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import static mumble.SQLParserEndPoints.SQLPARSER_SQL_TREE_KEY;

/**
 * Batch re-run of Panto outstanding {@code timeout_513} rows against the current
 * tree ({@link ParseLatencyDiagnosticService}). Feed a JSON manifest produced by
 * {@code tools/benchmark_panto_timeout_rows.py}.
 *
 * <pre>
 *   python3 tools/benchmark_panto_timeout_rows.py --issue timeout_513
 *   mvn -pl parse -Dtest=PantoTimeoutBatchBenchmarkTest#runTimeout513RowsFromManifest test
 * </pre>
 *
 * <p>Rows in {@link PantoCorpusSkipList} (subMap walker fatals) and
 * {@link PantoCorpusExclusionList} (utility workbooks) are skipped unless
 * {@code -Dpanto.skip.list.include=true}.
 */
public class PantoTimeoutBatchBenchmarkTest {

    private static final Path DEFAULT_MANIFEST = Path.of("target/panto-timeout-batch-manifest.json");
    private static final long TIMEOUT_MS = 90_000L;
    private static final long WARN_MS = 5_000L;

    @Test
    @Ignore("Manual — re-benchmark timeout_513 rows; run with -Dtest=…#runTimeout513RowsFromManifest")
    public void runTimeout513RowsFromManifest() throws IOException {
        Path manifest = resolveManifestPath();
        List<ManifestRow> rows = loadManifest(manifest);
        boolean includeSkipList = Boolean.getBoolean("panto.skip.list.include");
        Set<Integer> skipRows = includeSkipList
                ? Set.of()
                : unionSkipAndExclusionRows();
        int skipped = 0;
        System.out.printf(Locale.ROOT,
                "PANTO_BATCH manifest=%s rows=%d skip=%d timeoutMs=%d%n",
                manifest.toAbsolutePath(), rows.size(), skipRows.size(), TIMEOUT_MS);
        System.out.println(
                "csv_row,query_key,chars,lex_ms,parse_ms,walk_ms,fin_ms,total_ms,"
                        + "sll_fallback,ambig,ctx_sens,parse_err,walker_fatal,status,prev_5003_ms,prev_513_ms");

        int stillTimeout = 0;
        int slowWalk = 0;
        int slowParse = 0;
        int ok = 0;

        for (ManifestRow row : rows) {
            if (skipRows.contains(row.csvRow)) {
                skipped++;
                String skipReason = PantoCorpusExclusionList.isExcluded(row.csvRow)
                        ? "corpus_exclusion"
                        : "subMap_walker_skip";
                System.out.printf(Locale.ROOT,
                        "%d,%s,SKIP,%s,,,,,,,,,,SKIP,%d,%d%n",
                        row.csvRow,
                        csvEscape(row.queryKey),
                        skipReason,
                        row.prev5003Ms,
                        row.prev513Ms);
                continue;
            }
            BatchResult result = diagnoseRow(row);
            System.out.println(result.csvLine());

            switch (result.status) {
                case "TIMEOUT" -> stillTimeout++;
                case "SLOW_WALK" -> slowWalk++;
                case "SLOW_PARSE" -> slowParse++;
                default -> ok++;
            }
        }

        System.out.printf(Locale.ROOT,
                "PANTO_BATCH_SUMMARY rows=%d run=%d skipped=%d ok=%d slow_parse=%d slow_walk=%d still_timeout=%d%n",
                rows.size(), rows.size() - skipped, skipped, ok, slowParse, slowWalk, stillTimeout);
    }

    private static Path resolveManifestPath() {
        String override = System.getProperty("panto.timeout.manifest");
        if (override != null && !override.isBlank()) {
            return Path.of(override);
        }
        Path fromModule = Path.of("parse").resolve(DEFAULT_MANIFEST);
        if (Files.isRegularFile(fromModule)) {
            return fromModule;
        }
        if (Files.isRegularFile(DEFAULT_MANIFEST)) {
            return DEFAULT_MANIFEST;
        }
        throw new IllegalStateException(
                "Cannot find manifest at " + DEFAULT_MANIFEST + " — run tools/benchmark_panto_timeout_rows.py first");
    }

    private static List<ManifestRow> loadManifest(Path manifest) throws IOException {
        try (Reader reader = Files.newBufferedReader(manifest, StandardCharsets.UTF_8)) {
            ManifestPayload payload = new Gson().fromJson(reader, ManifestPayload.class);
            if (payload == null || payload.rows == null) {
                throw new IllegalStateException("Manifest is empty: " + manifest);
            }
            List<ManifestRow> rows = new ArrayList<>(payload.rows);
            rows.sort(Comparator.comparingInt(r -> r.csvRow));
            return rows;
        }
    }

    private static BatchResult diagnoseRow(ManifestRow row) {
        long start = System.nanoTime();
        ParseLatencyReport report = ParseLatencyDiagnosticService.diagnose(row.sql, SQLPARSER_SQL_TREE_KEY);
        long elapsedMs = (System.nanoTime() - start) / 1_000_000L;

        String status = "OK";
        if (elapsedMs >= TIMEOUT_MS) {
            status = "TIMEOUT";
        } else if (report.walkMs >= WARN_MS) {
            status = "SLOW_WALK";
        } else if (report.parseMs >= WARN_MS) {
            status = "SLOW_PARSE";
        }

        return new BatchResult(row, report, elapsedMs, status);
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

        @SerializedName("parse_ms_5_0_0_3")
        long prev5003Ms;

        @SerializedName("parse_ms_5_1_3")
        long prev513Ms;
    }

    private record BatchResult(ManifestRow row, ParseLatencyReport report, long elapsedMs, String status) {
        String csvLine() {
            return String.format(Locale.ROOT,
                    "%d,%s,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d,%s,%d,%d",
                    row.csvRow,
                    csvEscape(row.queryKey),
                    report.querySizeChars,
                    report.lexMs,
                    report.parseMs,
                    report.walkMs,
                    report.finalizeMs,
                    report.totalMs,
                    report.sllFallbackCount,
                    report.ambiguityCount,
                    report.contextSensitivityCount,
                    report.parseErrorCount,
                    report.walkerFatalCount,
                    status,
                    row.prev5003Ms,
                    row.prev513Ms);
        }

    }

    private static String csvEscape(String value) {
        if (value == null) {
            return "";
        }
        if (value.contains(",") || value.contains("\"")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }

    private static Set<Integer> unionSkipAndExclusionRows() {
        Set<Integer> combined = new java.util.LinkedHashSet<>(PantoCorpusSkipList.subMapWalkerCsvRows());
        combined.addAll(PantoCorpusExclusionList.excludedCsvRows());
        return combined;
    }
}
