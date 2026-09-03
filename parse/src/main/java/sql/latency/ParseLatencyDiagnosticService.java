package sql.latency;

import java.util.BitSet;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.DiagnosticErrorListener;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.atn.ATNConfigSet;
import org.antlr.v4.runtime.atn.PredictionMode;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import errorhandling.ParseErrorCollector;
import static mumble.SQLParserEndPoints.SQLPARSER_COLUMN_TREE_KEY;
import static mumble.SQLParserEndPoints.SQLPARSER_CONDITION_TREE_KEY;
import static mumble.SQLParserEndPoints.SQLPARSER_DDL_TREE_KEY;
import static mumble.SQLParserEndPoints.SQLPARSER_DELETE_TREE_KEY;
import static mumble.SQLParserEndPoints.SQLPARSER_INSERT_TREE_KEY;
import static mumble.SQLParserEndPoints.SQLPARSER_IN_LIST_TREE_KEY;
import static mumble.SQLParserEndPoints.SQLPARSER_JOIN_EXTENSION_TREE_KEY;
import static mumble.SQLParserEndPoints.SQLPARSER_LITERAL_TREE_KEY;
import static mumble.SQLParserEndPoints.SQLPARSER_PREDICAND_TREE_KEY;
import static mumble.SQLParserEndPoints.SQLPARSER_QUERY_TREE_KEY;
import static mumble.SQLParserEndPoints.SQLPARSER_SCRIPT_TREE_KEY;
import static mumble.SQLParserEndPoints.SQLPARSER_SQL_TREE_KEY;
import static mumble.SQLParserEndPoints.SQLPARSER_TRUNCATE_TREE_KEY;
import static mumble.SQLParserEndPoints.SQLPARSER_TUPLE_TREE_KEY;
import static mumble.SQLParserEndPoints.SQLPARSER_UPDATE_TREE_KEY;
import static mumble.SQLParserEndPoints.SQLPARSER_VALUES_TREE_KEY;
import sql.SQLSelectParserLexer;
import sql.SQLSelectParserParser;
import sql.walker.SqlParseEventWalker;

/**
 * Splits a full parse run into four independently timed phases and counts
 * ANTLR4 prediction events so Phase 2.8 can determine whether the 5.1.3
 * timeout is in grammar prediction or in the event walker.
 *
 * <h2>How to use</h2>
 * <pre>{@code
 * ParseLatencyReport r = ParseLatencyDiagnosticService.diagnose(queryText, "SQL");
 * System.out.println(r.summary());
 * // inspect r.sllFallbackCount, r.parseMs, r.walkMs, …
 * }</pre>
 *
 * <h2>Interpreting results (Phase 2.8 guide)</h2>
 * <ul>
 *   <li><b>{@code sllFallbackCount} > 0 AND {@code parseMs} is large</b> — the hang
 *       is in grammar prediction.  Focus on set-op member rules, LISTAGG suffix,
 *       or the new {@code script} top-level alternatives.</li>
 *   <li><b>{@code parseMs} is small but {@code walkMs} is large</b> — the hang is
 *       in the event walker.  Profile {@link SqlParseEventWalker} with JFR or
 *       async-profiler; look for O(n²) list scans triggered by many
 *       LISTAGG/window expressions.</li>
 *   <li><b>Both phases complete quickly in isolation but total is slow</b> — check
 *       {@code finalizeMs}; the symbol-tree finalizer may be the culprit.</li>
 * </ul>
 */
public final class ParseLatencyDiagnosticService {

    private ParseLatencyDiagnosticService() {}

    /**
     * Runs a full parse of {@code query} through the given {@code endpoint} and
     * returns a {@link ParseLatencyReport} with per-phase timings and ANTLR4
     * prediction-event counts.
     *
     * @param query    raw SQL text (may contain PSS substitution variables)
     * @param endpoint one of the {@code SQLPARSER_*_TREE_KEY} constants, e.g. {@code "SQL"}
     */
    public static ParseLatencyReport diagnose(String query, String endpoint) {

        // ── Phase 1: Lex ─────────────────────────────────────────────────────
        long t0 = System.nanoTime();
        CharStream charStream = CharStreams.fromString(query);
        SQLSelectParserLexer lexer = new SQLSelectParserLexer(charStream);
        lexer.removeErrorListeners();           // silence default console output
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        tokens.fill();                          // materialise all tokens now
        long lexMs = msElapsed(t0);

        // ── Phase 2: Parse ───────────────────────────────────────────────────
        SQLSelectParserParser parser = new SQLSelectParserParser(tokens);
        parser.removeErrorListeners();          // remove default console listener

        // Counting listener: wraps DiagnosticErrorListener and tallies events.
        CountingDiagnosticListener diagListener = new CountingDiagnosticListener();
        parser.addErrorListener(diagListener);

        // Standard error strategy (allows parse to continue on errors).
        ParseErrorCollector syntaxCollector = new ParseErrorCollector();
        parser.setErrorHandler(syntaxCollector);

        // Force SLL first; SqlParserAccess uses the same policy with LL retry on cancellation.
        parser.getInterpreter().setPredictionMode(PredictionMode.SLL);

        long t1 = System.nanoTime();
        ParseTree parseTree = runEndpoint(parser, endpoint);
        long parseMs = msElapsed(t1);

        // ── Phase 3: Walk ────────────────────────────────────────────────────
        SqlParseEventWalker walker = new SqlParseEventWalker();
        long t2 = System.nanoTime();
        try {
            ParseTreeWalker.DEFAULT.walk(walker, parseTree);
        } catch (Exception e) {
            // record but don't rethrow — we still want the timing data
            System.err.println("[ParseLatencyDiagnosticService] walker threw: " + e.getMessage());
        }
        long walkMs = msElapsed(t2);

        // ── Phase 4: Finalize ────────────────────────────────────────────────
        long t3 = System.nanoTime();
        try {
            walker.finalizeHandoffSymbolTable();
        } catch (Exception e) {
            System.err.println("[ParseLatencyDiagnosticService] finalize threw: " + e.getMessage());
        }
        long finalizeMs = msElapsed(t3);

        // ── Collect counts ───────────────────────────────────────────────────
        int parseErrorCount = parser.getNumberOfSyntaxErrors();
        int walkerFatalCount = countWalkerFatals(walker);

        return new ParseLatencyReport(
                endpoint,
                query.length(),
                lexMs,
                parseMs,
                walkMs,
                finalizeMs,
                diagListener.sllFallbackCount,
                diagListener.ambiguityCount,
                diagListener.contextSensitivityCount,
                parseErrorCount,
                walkerFatalCount);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private static long msElapsed(long nanoStart) {
        return (System.nanoTime() - nanoStart) / 1_000_000L;
    }

    /** Dispatches to the correct grammar rule based on the endpoint key. */
    private static ParseTree runEndpoint(SQLSelectParserParser parser, String endpoint) {
        switch (endpoint) {
            case SQLPARSER_SQL_TREE_KEY:          return parser.sql();
            case SQLPARSER_SCRIPT_TREE_KEY:       return parser.script();
            case SQLPARSER_QUERY_TREE_KEY:        return parser.query_value();
            case SQLPARSER_COLUMN_TREE_KEY:       return parser.column_value();
            case SQLPARSER_PREDICAND_TREE_KEY:    return parser.predicand_value();
            case SQLPARSER_IN_LIST_TREE_KEY:      return parser.in_list_predicate_value();
            case SQLPARSER_CONDITION_TREE_KEY:    return parser.condition_value();
            case SQLPARSER_TUPLE_TREE_KEY:        return parser.tuple_value();
            case SQLPARSER_VALUES_TREE_KEY:       return parser.values_statement_end();
            case SQLPARSER_DDL_TREE_KEY:          return parser.ddl();
            case SQLPARSER_JOIN_EXTENSION_TREE_KEY: return parser.join_extension_value();
            case SQLPARSER_LITERAL_TREE_KEY:      return parser.literal_value();
            case SQLPARSER_INSERT_TREE_KEY:       return parser.insert_end_point();
            case SQLPARSER_UPDATE_TREE_KEY:       return parser.update_end_point();
            case SQLPARSER_DELETE_TREE_KEY:       return parser.delete_end_point();
            case SQLPARSER_TRUNCATE_TREE_KEY:     return parser.truncate_end_point();
            default:
                throw new IllegalArgumentException("Unknown endpoint: " + endpoint);
        }
    }

    private static int countWalkerFatals(SqlParseEventWalker walker) {
        try {
            Object snippet = walker.getClass().getMethod("getSnippet").invoke(walker);
            if (snippet == null) return 0;
            Object diagnostics = snippet.getClass().getMethod("getParserDiagnosticList").invoke(snippet);
            if (!(diagnostics instanceof Iterable<?>)) return 0;
            int n = 0;
            for (Object diagnostic : (Iterable<?>) diagnostics) {
                if (diagnostic == null) {
                    continue;
                }
                Object severity = diagnostic.getClass().getMethod("severity").invoke(diagnostic);
                if (severity != null && "FATAL".equals(String.valueOf(severity))) {
                    n++;
                }
            }
            return n;
        } catch (Exception e) {
            return -1;
        }
    }

    // ── Inner listener ────────────────────────────────────────────────────────

    /**
     * Counts ANTLR4 prediction events without printing them to the console.
     * Extends {@link DiagnosticErrorListener} to inherit its detection logic
     * but overrides each callback to increment counters instead of printing.
     */
    static final class CountingDiagnosticListener extends DiagnosticErrorListener {

        int sllFallbackCount       = 0;
        int ambiguityCount         = 0;
        int contextSensitivityCount = 0;

        CountingDiagnosticListener() {
            super(true); // exactOnly=true: only report genuine ambiguities
        }

        @Override
        public void reportAttemptingFullContext(
                Parser recognizer,
                DFA dfa,
                int startIndex,
                int stopIndex,
                BitSet conflictingAlts,
                ATNConfigSet configs) {
            sllFallbackCount++;
            // do NOT call super — we don't want console output
        }

        @Override
        public void reportAmbiguity(
                Parser recognizer,
                DFA dfa,
                int startIndex,
                int stopIndex,
                boolean exact,
                BitSet ambigAlts,
                ATNConfigSet configs) {
            ambiguityCount++;
        }

        @Override
        public void reportContextSensitivity(
                Parser recognizer,
                DFA dfa,
                int startIndex,
                int stopIndex,
                int prediction,
                ATNConfigSet configs) {
            contextSensitivityCount++;
        }

        @Override
        public void syntaxError(
                Recognizer<?, ?> recognizer,
                Object offendingSymbol,
                int line,
                int charPositionInLine,
                String msg,
                RecognitionException e) {
            // syntax errors are already handled by ParseErrorListener — ignore here
        }
    }
}
