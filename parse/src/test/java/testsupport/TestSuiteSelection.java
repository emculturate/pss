package testsupport;

import static org.junit.Assume.assumeTrue;

/** Opt-in suites shared by Maven and the IDE's JUnit 4 runner. */
public final class TestSuiteSelection {

    private TestSuiteSelection() {
    }

    public static void assumeSlowTestsEnabled() {
        assumeTrue(
                "Slow release regression: select pss-release in VS Code or use -Prelease-tests in Maven",
                Boolean.getBoolean("pss.test.slow") || Boolean.getBoolean("pss.test.diagnostics"));
    }

    public static void assumeDiagnosticsEnabled() {
        assumeTrue(
                "Manual diagnostic: select pss-diagnostics in VS Code or use -Pdiagnostic-tests in Maven",
                Boolean.getBoolean("pss.test.diagnostics"));
    }
}
