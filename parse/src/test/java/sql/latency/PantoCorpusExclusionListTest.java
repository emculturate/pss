package sql.latency;

import java.io.IOException;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/** Bound-query corpus exclusions (utility workbooks, not parser defects). */
public class PantoCorpusExclusionListTest {

    @Test
    public void exclusionList_includesRow4197UtilityWorkbook() {
        assertTrue(PantoCorpusExclusionList.isExcluded(4197));
        assertEquals(1, PantoCorpusExclusionList.excludedCsvRows().size());
        PantoCorpusExclusionList.ExclusionEntry entry = PantoCorpusExclusionList.entries().get(0);
        assertEquals(4197, entry.csvRow());
        assertEquals("utility_workbook", entry.category());
    }

    @Test
    public void clusterB_doesNotIncludeExcludedRows() throws IOException {
        for (int csvRow : PantoOutstandingSqlFixtures.clusterBRegressionRows()) {
            assertFalse("csv_row=" + csvRow, PantoCorpusExclusionList.isExcluded(csvRow));
        }
    }
}
