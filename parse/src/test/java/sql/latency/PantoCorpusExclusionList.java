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
 * CSV rows excluded from bound-query parser correctness gates (utility workbooks,
 * multi-statement scripts, etc.). Distinct from {@link PantoCorpusSkipList} (walker
 * subMap fatals).
 *
 * <p>Canonical list: {@code parse/docs/rmcp-handoff/5.1.3-panto-outstanding/panto-corpus-exclusion-list.json}
 */
final class PantoCorpusExclusionList {

    private static final Path DEFAULT_EXCLUSION_LIST = Path.of(
            "docs/rmcp-handoff/5.1.3-panto-outstanding/panto-corpus-exclusion-list.json");

    private PantoCorpusExclusionList() {
    }

    static Set<Integer> excludedCsvRows() {
        return loadExclusionList().csvRows;
    }

    static boolean isExcluded(int csvRow) {
        return excludedCsvRows().contains(csvRow);
    }

    static List<ExclusionEntry> entries() {
        return loadExclusionList().entries;
    }

    private static ExclusionListPayload loadExclusionList() {
        return ExclusionListHolder.INSTANCE;
    }

    private static final class ExclusionListHolder {
        private static final ExclusionListPayload INSTANCE = readExclusionList();
    }

    private static ExclusionListPayload readExclusionList() {
        Path path = resolveExclusionListPath();
        try (Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            ExclusionListFile file = new Gson().fromJson(reader, ExclusionListFile.class);
            if (file == null || file.rows == null || file.rows.isEmpty()) {
                Set<Integer> csvRows = file != null && file.csvRows != null
                        ? Set.copyOf(file.csvRows)
                        : Collections.emptySet();
                return new ExclusionListPayload(csvRows, List.of());
            }
            Set<Integer> csvRows = new LinkedHashSet<>();
            List<ExclusionEntry> entries = file.rows.stream()
                    .map(row -> {
                        csvRows.add(row.csvRow);
                        return new ExclusionEntry(
                                row.csvRow,
                                row.queryKey,
                                row.domainName,
                                row.queryName,
                                row.category,
                                row.reason);
                    })
                    .collect(Collectors.toList());
            return new ExclusionListPayload(Collections.unmodifiableSet(csvRows), List.copyOf(entries));
        } catch (IOException e) {
            throw new IllegalStateException("Cannot read exclusion list at " + path, e);
        }
    }

    private static Path resolveExclusionListPath() {
        String override = System.getProperty("panto.exclusion.list");
        if (override != null && !override.isBlank()) {
            return Path.of(override);
        }
        Path fromModule = Path.of("parse").resolve(DEFAULT_EXCLUSION_LIST);
        if (Files.isRegularFile(fromModule)) {
            return fromModule;
        }
        if (Files.isRegularFile(DEFAULT_EXCLUSION_LIST)) {
            return DEFAULT_EXCLUSION_LIST;
        }
        throw new IllegalStateException("Cannot find exclusion list at " + DEFAULT_EXCLUSION_LIST);
    }

    private record ExclusionListPayload(Set<Integer> csvRows, List<ExclusionEntry> entries) {
    }

    record ExclusionEntry(
            int csvRow,
            String queryKey,
            String domainName,
            String queryName,
            String category,
            String reason) {
    }

    static final class ExclusionListFile {
        @SerializedName("csv_rows")
        List<Integer> csvRows;

        @SerializedName("rows")
        List<ExclusionListRow> rows;
    }

    static final class ExclusionListRow {
        @SerializedName("csv_row")
        int csvRow;

        @SerializedName("query_key")
        String queryKey;

        @SerializedName("domain_name")
        String domainName;

        @SerializedName("query_name")
        String queryName;

        @SerializedName("category")
        String category;

        @SerializedName("reason")
        String reason;
    }
}
