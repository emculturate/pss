package access;

import org.junit.Assert;
import org.junit.Test;

import astwalkers.AbstractASTWalkerHelper;
import errorhandling.ParseDiagnostic;

/**
 * Regression for {@link SqlParserAccess#generateAST()} walk catch path (W3).
 */
public class SqlParserAccessWalkCatchRegressionTest {

    @Test
    public void handleWalkException_mapsSubMapNpeWithoutRawJavaFatal() {
        SqlParserAccess access = new SqlParserAccess(false, false, false);
        NullPointerException npe = new NullPointerException(
                "Cannot invoke \"java.util.Map.remove(Object)\" because \"subMap\" is null");

        Assert.assertTrue(access.handleWalkException(npe));

        long misalignCount = access.getAllDiagnostics().stream()
                .filter(d -> d != null
                        && AbstractASTWalkerHelper.DIAG_AST_WALKER_STACK_MISALIGN.equals(d.code()))
                .count();
        Assert.assertEquals(1L, misalignCount);

        for (String fatal : access.getFatalErrorList()) {
            Assert.assertFalse("raw subMap fatal should not appear: " + fatal, fatal.contains("subMap"));
        }
        Assert.assertTrue(access.getFatalErrorList().stream()
                .anyMatch(msg -> msg.contains("walker stack mis-aligned")));
    }

    @Test
    public void handleWalkException_dedupesSecondCatchWhenMisalignAlreadyRecorded() {
        SqlParserAccess access = new SqlParserAccess(false, false, false);
        NullPointerException npe = new NullPointerException(
                "Cannot invoke \"java.util.Map.remove(Object)\" because \"subMap\" is null");

        Assert.assertTrue(access.handleWalkException(npe));
        Assert.assertTrue(access.handleWalkException(npe));

        long misalignCount = access.getAllDiagnostics().stream()
                .filter(d -> d != null
                        && AbstractASTWalkerHelper.DIAG_AST_WALKER_STACK_MISALIGN.equals(d.code()))
                .count();
        Assert.assertEquals(1L, misalignCount);
    }

    @Test
    public void handleWalkException_leavesUnrelatedExceptionsUnrecognized() {
        SqlParserAccess access = new SqlParserAccess(false, false, false);
        Assert.assertFalse(access.handleWalkException(new IllegalStateException("unrelated")));
        Assert.assertTrue(access.getAllDiagnostics().isEmpty());
    }
}
