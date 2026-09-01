package sql.latency;

/**
 * Timing and prediction-event summary produced by {@link ParseLatencyDiagnosticService}.
 * <p>
 * All {@code *Ms} fields are wall-clock milliseconds measured with {@link System#nanoTime()}.
 * The prediction-event counts come from ANTLR4's built-in
 * {@code DiagnosticErrorListener} and are the primary signal for Phase 2.8:
 * <ul>
 *   <li>{@link #sllFallbackCount} — fires whenever ANTLR gives up on SLL and
 *       retries with full LL; even a few dozen of these on a long query can
 *       cause catastrophic slowdown.</li>
 *   <li>{@link #ambiguityCount} — grammar is structurally ambiguous at that
 *       position; each one forces the ALL(*) resolver.</li>
 *   <li>{@link #contextSensitivityCount} — prediction required full parse
 *       context; usually follows SLL→LL fallback.</li>
 * </ul>
 */
public final class ParseLatencyReport {

    public final String endpoint;
    public final int    querySizeChars;

    /** Token stream construction time. */
    public final long lexMs;
    /** {@code parser.<rule>()} call — pure grammar/prediction time. */
    public final long parseMs;
    /** {@code ParseTreeWalker.DEFAULT.walk(extractor, tree)} — event-walker time. */
    public final long walkMs;
    /** {@code extractor.finalizeHandoffSymbolTable()} time. */
    public final long finalizeMs;
    public final long totalMs;

    // ── ANTLR4 DiagnosticErrorListener counters ──────────────────────────────
    /** SLL→LL fallbacks: the primary indicator of expensive prediction. */
    public final int sllFallbackCount;
    /** Grammar ambiguities detected. */
    public final int ambiguityCount;
    /** Context-sensitive prediction events. */
    public final int contextSensitivityCount;

    // ── Error counts ─────────────────────────────────────────────────────────
    /** Syntax errors reported by ParseErrorListener. */
    public final int parseErrorCount;
    /** FATAL diagnostics emitted by the event walker. */
    public final int walkerFatalCount;

    ParseLatencyReport(
            String endpoint,
            int querySizeChars,
            long lexMs,
            long parseMs,
            long walkMs,
            long finalizeMs,
            int sllFallbackCount,
            int ambiguityCount,
            int contextSensitivityCount,
            int parseErrorCount,
            int walkerFatalCount) {
        this.endpoint               = endpoint;
        this.querySizeChars         = querySizeChars;
        this.lexMs                  = lexMs;
        this.parseMs                = parseMs;
        this.walkMs                 = walkMs;
        this.finalizeMs             = finalizeMs;
        this.totalMs                = lexMs + parseMs + walkMs + finalizeMs;
        this.sllFallbackCount       = sllFallbackCount;
        this.ambiguityCount         = ambiguityCount;
        this.contextSensitivityCount = contextSensitivityCount;
        this.parseErrorCount        = parseErrorCount;
        this.walkerFatalCount       = walkerFatalCount;
    }

    /**
     * Single-line summary suitable for a JUnit sysout or a table row in the
     * workplan progress notes.
     */
    public String summary() {
        return String.format(
            "endpoint=%-12s  chars=%6d  lex=%4dms  parse=%6dms  walk=%6dms  fin=%4dms  " +
            "total=%6dms  sllFallback=%d  ambig=%d  ctxSens=%d  parseErr=%d  walkerFatal=%d",
            endpoint, querySizeChars,
            lexMs, parseMs, walkMs, finalizeMs, totalMs,
            sllFallbackCount, ambiguityCount, contextSensitivityCount,
            parseErrorCount, walkerFatalCount);
    }

    @Override
    public String toString() {
        return summary();
    }
}
