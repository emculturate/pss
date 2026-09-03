package access;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import astwalkers.AbstractASTWalkerHelper;
import errorhandling.ParseDiagnostic;

public class WalkerWalkExceptionGateTest {

    private static NullPointerException subMapNullNpe() {
        return new NullPointerException(
                "Cannot invoke \"java.util.Map.remove(Object)\" because \"subMap\" is null");
    }

    @Test
    public void recognizeWalkException_mapsSubMapNpeToStructuredFatal() {
        List<ParseDiagnostic> captured = new ArrayList<>();
        boolean recognized = WalkerWalkExceptionGate.recognizeWalkException(
                subMapNullNpe(), "SqlParserAccess", captured, List.of());

        Assert.assertTrue(recognized);
        Assert.assertEquals(1, captured.size());
        ParseDiagnostic fatal = captured.get(0);
        Assert.assertEquals(ParseDiagnostic.Severity.FATAL, fatal.severity());
        Assert.assertEquals(AbstractASTWalkerHelper.DIAG_AST_WALKER_STACK_MISALIGN, fatal.code());
        Assert.assertEquals("SqlParserAccess", fatal.source());
        Assert.assertEquals("ast-walk", fatal.phase());
        Assert.assertEquals("NullPointerException", fatal.exceptionType());
    }

    @Test
    public void recognizeWalkException_dedupesWhenWalkerAlreadyRecordedMisalign() {
        ParseDiagnostic existing = WalkerWalkExceptionGate.stackMisalignDiagnostic(
                "SqlParseEventWalker", subMapNullNpe());
        List<ParseDiagnostic> captured = new ArrayList<>();

        boolean recognized = WalkerWalkExceptionGate.recognizeWalkException(
                subMapNullNpe(), "SqlParserAccess", captured, List.of(existing));

        Assert.assertTrue(recognized);
        Assert.assertTrue(captured.isEmpty());
    }

    @Test
    public void isWalkerStackMisalignNpe_followsCauseChain() {
        Exception wrapped = new RuntimeException("outer", subMapNullNpe());
        Assert.assertTrue(WalkerWalkExceptionGate.isWalkerStackMisalignNpe(wrapped));
    }
}
