package access;

import java.util.ArrayList;
import java.util.List;

import astwalkers.AbstractASTWalkerHelper;
import errorhandling.ParseDiagnostic;

/**
 * Maps walker {@code subMap} NPEs thrown during AST walk to structured
 * {@link AbstractASTWalkerHelper#DIAG_AST_WALKER_STACK_MISALIGN} diagnostics.
 * Shared by {@link SqlParserAccess} and {@link sql.latency.ParseLatencyDiagnosticService}.
 */
public final class WalkerWalkExceptionGate {

    private WalkerWalkExceptionGate() {
    }

    public static boolean isWalkerStackMisalignNpe(Throwable throwable) {
        for (Throwable current = throwable; current != null; current = current.getCause()) {
            if (!(current instanceof NullPointerException)) {
                continue;
            }
            String message = current.getMessage();
            if (message != null && message.contains("\"subMap\" is null")) {
                return true;
            }
            if (isWalkerStackFrame(current.getStackTrace())) {
                return true;
            }
        }
        return false;
    }

    private static boolean isWalkerStackFrame(StackTraceElement[] stackTrace) {
        if (stackTrace == null) {
            return false;
        }
        for (StackTraceElement frame : stackTrace) {
            if (frame == null) {
                continue;
            }
            if ("astwalkers.AbstractASTWalkerHelper".equals(frame.getClassName())
                    && ("removeNodeMap".equals(frame.getMethodName())
                            || "requireNodeMap".equals(frame.getMethodName()))) {
                return true;
            }
        }
        return false;
    }

    public static boolean containsStackMisalignDiagnostic(Iterable<ParseDiagnostic> diagnostics) {
        if (diagnostics == null) {
            return false;
        }
        for (ParseDiagnostic diagnostic : diagnostics) {
            if (diagnostic != null
                    && AbstractASTWalkerHelper.DIAG_AST_WALKER_STACK_MISALIGN.equals(diagnostic.code())) {
                return true;
            }
        }
        return false;
    }

    /**
     * When {@code throwable} is a walker stack mis-align NPE, records a structured fatal on
     * {@code sink} unless one is already present in {@code existingForDedupe} or {@code sink}.
     *
     * @return true when the throwable was recognized as a walker stack mis-align NPE
     */
    public static boolean recognizeWalkException(
            Exception throwable,
            String source,
            List<ParseDiagnostic> sink,
            Iterable<ParseDiagnostic> existingForDedupe) {
        if (!isWalkerStackMisalignNpe(throwable)) {
            return false;
        }
        if (containsStackMisalignDiagnostic(existingForDedupe)
                || containsStackMisalignDiagnostic(sink)) {
            return true;
        }
        if (sink != null) {
            sink.add(stackMisalignDiagnostic(source, throwable));
        }
        return true;
    }

    public static ParseDiagnostic stackMisalignDiagnostic(String source, Throwable throwable) {
        String exceptionType = throwable == null ? null : throwable.getClass().getSimpleName();
        return new ParseDiagnostic(
                ParseDiagnostic.Severity.FATAL,
                AbstractASTWalkerHelper.DIAG_AST_WALKER_STACK_MISALIGN,
                "Semantic analysis aborted: walker stack mis-aligned during AST walk.",
                null,
                null,
                source,
                null,
                null,
                false,
                "ast-walk",
                exceptionType,
                null);
    }

    /** Collects diagnostics already present on the access layer and optional walker snippet. */
    public static List<ParseDiagnostic> existingMisalignCandidates(
            Iterable<ParseDiagnostic> accessAndParserDiagnostics,
            Iterable<ParseDiagnostic> walkerDiagnostics) {
        List<ParseDiagnostic> existing = new ArrayList<>();
        if (accessAndParserDiagnostics != null) {
            for (ParseDiagnostic diagnostic : accessAndParserDiagnostics) {
                if (diagnostic != null) {
                    existing.add(diagnostic);
                }
            }
        }
        if (walkerDiagnostics != null) {
            for (ParseDiagnostic diagnostic : walkerDiagnostics) {
                if (diagnostic != null) {
                    existing.add(diagnostic);
                }
            }
        }
        return existing;
    }
}
