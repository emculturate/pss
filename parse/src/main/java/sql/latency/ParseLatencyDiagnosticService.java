package sql.latency;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

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

import errorhandling.ParseDiagnostic;
import errorhandling.ParseErrorCollector;
import errorhandling.ParseErrorListener;
import access.ParsePhaseErrorGate;
import access.Snippet;
import access.WalkerWalkExceptionGate;
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
 * <b>Opt-in timing instrumentation only.</b> Not used by production {@code SqlParserAccess}.
 *
 * <p>Splits a parse run into independently timed phases (lex / parse / walk / finalize) and
 * optionally counts ANTLR4 prediction events. Intended for future performance investigations
 * (Phase 2.8-style profiling) and CI timing gates such as {@code PantoTimeoutCorpusE3GateTest}.
 *
 * <p><b>Normal entry point:</b> {@link #diagnose(String, String)} uses the same LL prediction
 * mode as production and always attempts the AST walk (walker exceptions are mapped via
 * {@link WalkerWalkExceptionGate}, matching {@code SqlParserAccess}).
 *
 * <p><b>Performance probe only:</b> {@link #diagnoseWithSllProbe(String, String)} additionally
 * runs an SLL parse pass before LL. Its results must never drive accept/reject or skip-walk
 * policy in production code.
 *
 * <h2>How to use</h2>
 * <pre>{@code
 * ParseLatencyReport r = ParseLatencyDiagnosticService.diagnose(queryText, "SQL");
 * System.out.println(r.summary());
 * }</pre>
 */
public final class ParseLatencyDiagnosticService {

    private ParseLatencyDiagnosticService() {}

    /**
     * Production-parity timing split: LL parse + walk + finalize (no SLL probe).
     */
    public static ParseLatencyReport diagnose(String query, String endpoint) {
        return runDiagnose(query, endpoint, false);
    }

    /**
     * Opt-in performance probe: SLL parse timing pass, then LL parse + walk + finalize.
     * {@link ParseLatencyReport#llReparseAfterSllFailure} is {@code 1} when the SLL-only
     * attempt reported parse-phase FATAL/ERROR. That signal is for profiling only.
     */
    public static ParseLatencyReport diagnoseWithSllProbe(String query, String endpoint) {
        return runDiagnose(query, endpoint, true);
    }

    private static ParseLatencyReport runDiagnose(String query, String endpoint, boolean includeSllProbe) {

        // ── Phase 1: Lex ─────────────────────────────────────────────────────
        long t0 = System.nanoTime();
        CharStream charStream = CharStreams.fromString(query);
        SQLSelectParserLexer lexer = new SQLSelectParserLexer(charStream);
        lexer.removeErrorListeners();
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        tokens.fill();
        long lexMs = msElapsed(t0);

        // ── Phase 2: Parse ───────────────────────────────────────────────────
        CountingDiagnosticListener diagListener = new CountingDiagnosticListener();
        long parseMs = 0;
        boolean sllParsePhaseFailed = false;

        if (includeSllProbe) {
            ParsePhaseResult sllPhase = runParsePhase(tokens, endpoint, PredictionMode.SLL, diagListener);
            parseMs += sllPhase.parseMs;
            sllParsePhaseFailed = ParsePhaseErrorGate.hasParsePhaseErrors(
                    sllPhase.parser, sllPhase.syntaxCollector, sllPhase.syntaxListener);
            tokens.seek(0);
        }

        ParsePhaseResult llPhase = runParsePhase(tokens, endpoint, PredictionMode.LL, diagListener);
        parseMs += llPhase.parseMs;

        SQLSelectParserParser parser = llPhase.parser;
        ParseErrorCollector syntaxCollector = llPhase.syntaxCollector;
        ParseErrorListener syntaxListener = llPhase.syntaxListener;
        ParseTree parseTree = llPhase.parseTree;

        // ── Phase 3: Walk (always — same as SqlParserAccess) ─────────────────
        SqlParseEventWalker walker = new SqlParseEventWalker();
        List<ParseDiagnostic> walkExceptionDiagnostics = new ArrayList<>();
        long t2 = System.nanoTime();
        try {
            ParseTreeWalker.DEFAULT.walk(walker, parseTree);
        } catch (Exception e) {
            List<ParseDiagnostic> walkerDiagnostics = null;
            Snippet walkerSnippet = walker.getSnippet();
            if (walkerSnippet != null) {
                walkerDiagnostics = walkerSnippet.getParserDiagnosticList();
            }
            if (!WalkerWalkExceptionGate.recognizeWalkException(
                    e,
                    "ParseLatencyDiagnosticService",
                    walkExceptionDiagnostics,
                    WalkerWalkExceptionGate.existingMisalignCandidates(
                            ParsePhaseErrorGate.collectDiagnostics(syntaxCollector, syntaxListener),
                            walkerDiagnostics))) {
                System.err.println("[ParseLatencyDiagnosticService] walker threw: " + e.getMessage());
            }
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

        int parseErrorCount = parser.getNumberOfSyntaxErrors();
        int walkerFatalCount = countWalkerFatals(walker, walkExceptionDiagnostics);
        List<ParseDiagnostic> diagnostics = collectReportDiagnostics(
                syntaxCollector, syntaxListener, walker, walkExceptionDiagnostics);

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
                walkerFatalCount,
                includeSllProbe && sllParsePhaseFailed ? 1 : 0,
                diagnostics);
    }

    private static ParsePhaseResult runParsePhase(
            CommonTokenStream tokens,
            String endpoint,
            PredictionMode predictionMode,
            CountingDiagnosticListener diagListener) {
        SQLSelectParserParser parser = new SQLSelectParserParser(tokens);
        parser.removeErrorListeners();
        parser.addErrorListener(diagListener);

        ParseErrorListener syntaxListener = new ParseErrorListener();
        ParseErrorCollector syntaxCollector = new ParseErrorCollector();
        parser.setErrorHandler(syntaxCollector);
        parser.addErrorListener(syntaxListener);
        parser.getInterpreter().setPredictionMode(predictionMode);

        long t0 = System.nanoTime();
        ParseTree parseTree = runEndpoint(parser, endpoint);
        long parseMs = msElapsed(t0);

        return new ParsePhaseResult(parser, syntaxCollector, syntaxListener, parseTree, parseMs);
    }

    private record ParsePhaseResult(
            SQLSelectParserParser parser,
            ParseErrorCollector syntaxCollector,
            ParseErrorListener syntaxListener,
            ParseTree parseTree,
            long parseMs) {
    }

    private static long msElapsed(long nanoStart) {
        return (System.nanoTime() - nanoStart) / 1_000_000L;
    }

    private static ParseTree runEndpoint(SQLSelectParserParser parser, String endpoint) {
        switch (endpoint) {
            case SQLPARSER_SQL_TREE_KEY:          return parser.sql();
            case SQLPARSER_SCRIPT_TREE_KEY:       return parser.script();
            case SQLPARSER_QUERY_TREE_KEY:        return parser.query_value();
            case SQLPARSER_COLUMN_TREE_KEY:       return parser.column_value();
            case SQLPARSER_PREDICAND_TREE_KEY:    return parser.predicand_value();
            case SQLPARSER_IN_LIST_TREE_KEY:      return parser.in_list_predicate_value();
            case SQLPARSER_CONDITION_TREE_KEY:    return parser.condition_value();
            case SQLPARSER_TUPLE_TREE_KEY:       return parser.tuple_value();
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

    private static int countWalkerFatals(
            SqlParseEventWalker walker,
            List<ParseDiagnostic> walkExceptionDiagnostics) {
        try {
            int n = 0;
            Snippet snippet = walker.getSnippet();
            if (snippet != null && snippet.getParserDiagnosticList() != null) {
                for (ParseDiagnostic diagnostic : snippet.getParserDiagnosticList()) {
                    if (diagnostic != null && diagnostic.severity() == ParseDiagnostic.Severity.FATAL) {
                        n++;
                    }
                }
            }
            if (walkExceptionDiagnostics != null) {
                for (ParseDiagnostic diagnostic : walkExceptionDiagnostics) {
                    if (diagnostic != null && diagnostic.severity() == ParseDiagnostic.Severity.FATAL) {
                        n++;
                    }
                }
            }
            return n;
        } catch (Exception e) {
            return -1;
        }
    }

    private static List<ParseDiagnostic> collectReportDiagnostics(
            ParseErrorCollector syntaxCollector,
            ParseErrorListener syntaxListener,
            SqlParseEventWalker walker,
            List<ParseDiagnostic> walkExceptionDiagnostics) {
        List<ParseDiagnostic> diagnostics = new ArrayList<>(
                ParsePhaseErrorGate.collectDiagnostics(syntaxCollector, syntaxListener));
        Snippet snippet = walker.getSnippet();
        if (snippet != null && snippet.getParserDiagnosticList() != null) {
            diagnostics.addAll(snippet.getParserDiagnosticList());
        }
        if (walkExceptionDiagnostics != null && !walkExceptionDiagnostics.isEmpty()) {
            diagnostics.addAll(walkExceptionDiagnostics);
        }
        return diagnostics;
    }

    static final class CountingDiagnosticListener extends DiagnosticErrorListener {

        int sllFallbackCount       = 0;
        int ambiguityCount         = 0;
        int contextSensitivityCount = 0;

        CountingDiagnosticListener() {
            super(true);
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
