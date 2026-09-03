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
import java.util.StringJoiner;

import org.junit.BeforeClass;
import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import static mumble.SQLParserEndPoints.SQLPARSER_SQL_TREE_KEY;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Phase 2.8-E3 corpus gate — all {@code timeout_513} rows (74) plus named bucket canaries
 * must finish under the 90s RMCP kill.
 *
 * <p>Manifest: {@code python3 tools/benchmark_panto_timeout_rows.py --issue timeout_513}
 */
public class PantoTimeoutCorpusE3GateTest {

    static final long E3_TIMEOUT_MS = 90_000L;

    /** 2.8-1 — giant constant-row {@code UNION} lookup (~248 branches). */
    static final int[] E3_BUCKET_2_8_1 = {475, 476, 1827, 1828};

    /** 2.8-2 canary — PCM convert {@code UNION ALL} slices. */
    static final int E3_BUCKET_2_8_2_CANARY = 1837;

    /** Non-UNION guardrails (row 130 formerly on subMap skip list). */
    static final int[] E3_NON_UNION_CANARIES = {5261, 4647, 4197, 130};

    @BeforeClass
    public static void ensureManifest() throws IOException, InterruptedException {
        if (Files.isRegularFile(resolveManifestPath())) {
            return;
        }
        Path repoRoot = Path.of("..").toAbsolutePath().normalize();
        ProcessBuilder builder = new ProcessBuilder(
                "python3",
                repoRoot.resolve("tools/benchmark_panto_timeout_rows.py").toString(),
                "--issue",
                "timeout_513");
        builder.directory(repoRoot.toFile());
        builder.inheritIO();
        int exit = builder.start().waitFor();
        if (exit != 0 || !Files.isRegularFile(resolveManifestPath())) {
            throw new IllegalStateException(
                    "Failed to generate panto-timeout-batch-manifest.json — run tools/benchmark_panto_timeout_rows.py");
        }
    }

    @Test
    public void e3RunnableCorpus_allRowsUnder90s() throws IOException {
        List<ManifestRow> rows = loadManifest(resolveManifestPath());
        long maxWalkMs = 0;
        long maxTotalMs = 0;
        int maxWalkRow = -1;
        int maxTotalRow = -1;
        int withFatals = 0;
        List<String> failures = new ArrayList<>();

        for (ManifestRow row : rows) {
            RowGateResult result = gateRow(row);
            if (result.walkerFatalCount() > 0) {
                withFatals++;
            }
            if (result.walkMs() > maxWalkMs) {
                maxWalkMs = result.walkMs();
                maxWalkRow = row.csvRow;
            }
            if (result.totalMs() > maxTotalMs) {
                maxTotalMs = result.totalMs();
                maxTotalRow = row.csvRow;
            }
            if (!result.passed()) {
                failures.add(result.failureMessage());
            }
        }

        System.out.printf(Locale.ROOT,
                "E3_CORPUS_SUMMARY rows=%d skipList=%d maxWalkMs=%d (csv_row=%d) maxTotalMs=%d (csv_row=%d) rowsWithFatals=%d%n",
                rows.size(),
                PantoCorpusSkipList.subMapWalkerCsvRows().size(),
                maxWalkMs,
                maxWalkRow,
                maxTotalMs,
                maxTotalRow,
                withFatals);

        if (!failures.isEmpty()) {
            StringJoiner joiner = new StringJoiner(System.lineSeparator());
            failures.forEach(joiner::add);
            assertTrue("E3 corpus failures:\n" + joiner, failures.isEmpty());
        }
    }

    @Test
    public void e3NamedCanaries_under90s() throws Exception {
        for (int csvRow : E3_BUCKET_2_8_1) {
            assertNamedCanary("2.8-1", csvRow);
        }
        assertNamedCanary("2.8-2", E3_BUCKET_2_8_2_CANARY);
        for (int csvRow : E3_NON_UNION_CANARIES) {
            assertNamedCanary("non-UNION", csvRow);
        }
    }

    private static void assertNamedCanary(String bucket, int csvRow) throws IOException {
        String sql = PantoOutstandingSqlFixtures.sqlForCsvRow(csvRow);
        RowGateResult result = gateRow(new ManifestRow(csvRow, sql));
        System.out.printf(Locale.ROOT,
                "E3_CANARY bucket=%s csv_row=%d walkMs=%d totalMs=%d walkerFatal=%d%n",
                bucket,
                csvRow,
                result.walkMs(),
                result.totalMs(),
                result.walkerFatalCount());
        assertTrue(result.failureMessage(), result.passed());
    }

    private static RowGateResult gateRow(ManifestRow row) {
        ParseLatencyReport report = ParseLatencyDiagnosticService.diagnose(row.sql, SQLPARSER_SQL_TREE_KEY);
        return new RowGateResult(row.csvRow, report);
    }

    private static Path resolveManifestPath() {
        String override = System.getProperty("panto.timeout.manifest");
        if (override != null && !override.isBlank()) {
            return Path.of(override);
        }
        Path fromModule = Path.of("parse").resolve("target/panto-timeout-batch-manifest.json");
        if (Files.isRegularFile(fromModule)) {
            return fromModule;
        }
        Path local = Path.of("target/panto-timeout-batch-manifest.json");
        if (Files.isRegularFile(local)) {
            return local;
        }
        throw new IllegalStateException(
                "Cannot find panto-timeout-batch-manifest.json — run tools/benchmark_panto_timeout_rows.py first");
    }

    private static List<ManifestRow> loadManifest(Path manifest) throws IOException {
        try (Reader reader = Files.newBufferedReader(manifest, StandardCharsets.UTF_8)) {
            ManifestPayload payload = new Gson().fromJson(reader, ManifestPayload.class);
            if (payload == null || payload.rows == null || payload.rows.isEmpty()) {
                throw new IllegalStateException("Manifest is empty: " + manifest);
            }
            Set<Integer> skipRows = PantoCorpusSkipList.subMapWalkerCsvRows();
            List<ManifestRow> rows = new ArrayList<>();
            for (ManifestRow row : payload.rows) {
                if (skipRows.contains(row.csvRow)) {
                    throw new IllegalStateException(
                            "Manifest includes subMap skip row " + row.csvRow + " — regenerate without skip rows");
                }
                rows.add(row);
            }
            rows.sort(Comparator.comparingInt(r -> r.csvRow));
            assertEquals("expected 74 timeout_513 rows", 74, rows.size());
            return rows;
        }
    }

    private record RowGateResult(int csvRow, ParseLatencyReport report) {
        long walkMs() {
            return report.walkMs;
        }

        long totalMs() {
            return report.totalMs;
        }

        int walkerFatalCount() {
            return report.walkerFatalCount;
        }

        boolean passed() {
            return report.walkMs < E3_TIMEOUT_MS && report.totalMs < E3_TIMEOUT_MS;
        }

        String failureMessage() {
            if (passed()) {
                return "";
            }
            return String.format(Locale.ROOT,
                    "csv_row=%d walkMs=%d totalMs=%d walkerFatal=%d parseErr=%d (E3 gate: under %dms)",
                    csvRow,
                    report.walkMs,
                    report.totalMs,
                    report.walkerFatalCount,
                    report.parseErrorCount,
                    E3_TIMEOUT_MS);
        }
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

        ManifestRow() {
        }

        ManifestRow(int csvRow, String sql) {
            this.csvRow = csvRow;
            this.sql = sql;
        }
    }
}
