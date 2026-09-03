package sql.latency;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.junit.Ignore;
import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import access.SqlParserAccess;

import static mumble.SQLParserEndPoints.SQLPARSER_SQL_TREE_KEY;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Safety gate for two-stage SLL→LL prediction in {@link SqlParserAccess}.
 */
public class SqlParserAccessSllPredictionSafetyTest {

    private static final Path MANIFEST = Path.of("target/panto-timeout-batch-manifest.json");

    @Test
    public void clusterB210PendingRows_parseWithoutFatalsOnAccessPath() throws Exception {
        for (int csvRow : PantoOutstandingSqlFixtures.clusterB210PendingRows()) {
            assertAccessOk(PantoOutstandingSqlFixtures.sqlForCsvRow(csvRow));
        }
    }

    @Test
    public void representativeQueriesParseWithoutFatals() throws Exception {
        assertAccessOk("select 1 as x");
        assertAccessOk(PantoOutstandingSqlFixtures.sqlForCsvRow(4176));
        assertAccessOk(PantoOutstandingSqlFixtures.sqlForCsvRow(4177));
        assertAccessOk("select listagg(val, '|') within group (order by val) as blnd from t group by id");
    }

    @Test
    @Ignore("Manual — full timeout_513 corpus via SqlParserAccess; run after SLL policy changes")
    public void timeout513CorpusAccessPathSafety() throws IOException {
        Path manifest = Files.isRegularFile(MANIFEST)
                ? MANIFEST
                : Path.of("parse").resolve(MANIFEST);
        ManifestPayload payload = new Gson().fromJson(
                Files.newBufferedReader(manifest, StandardCharsets.UTF_8), ManifestPayload.class);
        assertNotNull(payload);
        assertNotNull(payload.rows);

        int fatals = 0;
        long slow = 0;
        int skipped = 0;
        Set<Integer> skipRows = PantoCorpusSkipList.subMapWalkerCsvRows();
        for (ManifestRow row : payload.rows) {
            if (skipRows.contains(row.csvRow)) {
                skipped++;
                continue;
            }
            long t0 = System.nanoTime();
            SqlParserAccess access = new SqlParserAccess(false, false, false);
            access.executeTheParse(row.sql, SQLPARSER_SQL_TREE_KEY);
            long ms = (System.nanoTime() - t0) / 1_000_000L;
            int fatalCount = access.getSnippet().getFatalErrorCount();
            if (fatalCount > 0) {
                fatals++;
                System.out.printf(Locale.ROOT, "FATAL csv_row=%d fatals=%d%n", row.csvRow, fatalCount);
            }
            if (ms >= 90_000L) {
                slow++;
                System.out.printf(Locale.ROOT, "SLOW csv_row=%d ms=%d%n", row.csvRow, ms);
            }
        }
        System.out.printf(Locale.ROOT,
                "SLL_CORPUS_SUMMARY rows=%d run=%d skipped=%d fatals=%d slow=%d%n",
                payload.rows.size(), payload.rows.size() - skipped, skipped, fatals, slow);
        assertEquals("no row should exceed 90s after SLL+LL policy", 0, slow);
    }

    private static void assertAccessOk(String sql) {
        SqlParserAccess access = new SqlParserAccess(false, false, false);
        access.executeTheParse(sql, SQLPARSER_SQL_TREE_KEY);
        assertNotNull(access.getSnippet());
        assertEquals(0, access.getSnippet().getFatalErrorCount());
        assertTrue(access.getSnippet().getTableDictionary() != null);
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
