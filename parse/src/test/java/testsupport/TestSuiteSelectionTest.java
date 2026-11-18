package testsupport;

import org.junit.After;
import org.junit.AssumptionViolatedException;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

public class TestSuiteSelectionTest {

    private String previousSlow;
    private String previousDiagnostics;

    @Before
    public void saveSuiteSelection() {
        previousSlow = System.getProperty("pss.test.slow");
        previousDiagnostics = System.getProperty("pss.test.diagnostics");
        System.clearProperty("pss.test.slow");
        System.clearProperty("pss.test.diagnostics");
    }

    @After
    public void restoreSuiteSelection() {
        restoreProperty("pss.test.slow", previousSlow);
        restoreProperty("pss.test.diagnostics", previousDiagnostics);
    }

    @Test
    public void everydaySuite_skipsSlowAndDiagnosticWork() {
        assertTrue(assertThrows(AssumptionViolatedException.class, TestSuiteSelection::assumeSlowTestsEnabled)
                .getMessage().contains("pss-release"));
        assertTrue(assertThrows(AssumptionViolatedException.class, TestSuiteSelection::assumeDiagnosticsEnabled)
                .getMessage().contains("pss-diagnostics"));
    }

    @Test
    public void releaseSuite_enablesSlowButNotDiagnosticWork() {
        System.setProperty("pss.test.slow", "true");

        TestSuiteSelection.assumeSlowTestsEnabled();
        assertTrue(assertThrows(AssumptionViolatedException.class, TestSuiteSelection::assumeDiagnosticsEnabled)
                .getMessage().contains("pss-diagnostics"));
    }

    @Test
    public void diagnosticSuite_enablesSlowAndDiagnosticWork() {
        System.setProperty("pss.test.diagnostics", "true");

        TestSuiteSelection.assumeSlowTestsEnabled();
        TestSuiteSelection.assumeDiagnosticsEnabled();
    }

    private static void restoreProperty(String name, String value) {
        if (value == null) {
            System.clearProperty(name);
        } else {
            System.setProperty(name, value);
        }
    }
}
