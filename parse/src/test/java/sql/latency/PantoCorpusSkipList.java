package sql.latency;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

/**
 * CSV rows excluded from automated full-parse corpus runs (batch benchmark, manifest
 * generation, SqlParserAccess safety sweeps) because they throw walker fatals from
 * {@code subMap} null after parse-error recovery.
 *
 * <p>Canonical list: {@code parse/docs/rmcp-handoff/5.1.3-panto-outstanding/panto-submap-walker-skip-list.json}
 */
final class PantoCorpusSkipList {

    private static final Path DEFAULT_SKIP_LIST = Path.of(
            "docs/rmcp-handoff/5.1.3-panto-outstanding/panto-submap-walker-skip-list.json");

    private PantoCorpusSkipList() {
    }

    static Set<Integer> subMapWalkerCsvRows() {
        return loadSkipList().csvRows;
    }

    static boolean isSubMapWalkerSkip(int csvRow) {
        return subMapWalkerCsvRows().contains(csvRow);
    }

    static List<SkipEntry> entries() {
        return loadSkipList().entries;
    }

    private static SkipListPayload loadSkipList() {
        return SkipListHolder.INSTANCE;
    }

    private static final class SkipListHolder {
        private static final SkipListPayload INSTANCE = readSkipList();
    }

    private static SkipListPayload readSkipList() {
        Path path = resolveSkipListPath();
        try (Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            SkipListFile file = new Gson().fromJson(reader, SkipListFile.class);
            if (file == null || file.rows == null) {
                return new SkipListPayload(Collections.emptySet(), List.of());
            }
            if (file.rows.isEmpty()) {
                return new SkipListPayload(Collections.emptySet(), List.of());
            }
            Set<Integer> csvRows = new LinkedHashSet<>();
            List<SkipEntry> entries = file.rows.stream()
                    .map(row -> {
                        csvRows.add(row.csvRow);
                        return new SkipEntry(
                                row.csvRow,
                                row.queryKey,
                                row.domainName,
                                row.queryName,
                                row.batchException,
                                row.category,
                                row.note);
                    })
                    .collect(Collectors.toList());
            return new SkipListPayload(Collections.unmodifiableSet(csvRows), List.copyOf(entries));
        } catch (IOException e) {
            throw new IllegalStateException("Cannot read skip list at " + path, e);
        }
    }

    private static Path resolveSkipListPath() {
        String override = System.getProperty("panto.skip.list");
        if (override != null && !override.isBlank()) {
            return Path.of(override);
        }
        Path fromModule = Path.of("parse").resolve(DEFAULT_SKIP_LIST);
        if (Files.isRegularFile(fromModule)) {
            return fromModule;
        }
        if (Files.isRegularFile(DEFAULT_SKIP_LIST)) {
            return DEFAULT_SKIP_LIST;
        }
        throw new IllegalStateException("Cannot find skip list at " + DEFAULT_SKIP_LIST);
    }

    private record SkipListPayload(Set<Integer> csvRows, List<SkipEntry> entries) {
    }

    record SkipEntry(
            int csvRow,
            String queryKey,
            String domainName,
            String queryName,
            String batchException,
            String category,
            String note) {
    }

    static final class SkipListFile {
        @SerializedName("rows")
        List<SkipListRow> rows;
    }

    static final class SkipListRow {
        @SerializedName("csv_row")
        int csvRow;

        @SerializedName("query_key")
        String queryKey;

        @SerializedName("domain_name")
        String domainName;

        @SerializedName("query_name")
        String queryName;

        @SerializedName("batch_exception")
        String batchException;

        @SerializedName("category")
        String category;

        @SerializedName("note")
        String note;
    }
}
