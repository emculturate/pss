package sql.latency;

import java.util.Map;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.atn.PredictionMode;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import sql.SQLSelectParserLexer;
import sql.SQLSelectParserParser;
import sql.walker.SqlParseEventWalker;

/**
 * Probe SLL vs default prediction fatals for Cluster B adjudication.
 * {@code mvn -pl parse exec:java -Dexec.classpathScope=test -Dexec.mainClass=sql.latency.SllVsDefaultClusterBProbe -Dexec.args=605}
 */
@SuppressWarnings("unchecked")
public final class SllVsDefaultClusterBProbe {

    private SllVsDefaultClusterBProbe() {
    }

    public static void main(String[] args) throws Exception {
        int row = Integer.parseInt(args[0]);
        String sql = PantoOutstandingSqlFixtures.sqlForCsvRow(row);
        printMode("DEFAULT", sql, false);
        printMode("SLL", sql, true);
    }

    private static void printMode(String label, String sql, boolean sll) {
        try {
            SqlParseEventWalker walker = walk(sql, sll);
            Map<String, Object> st = walker.getSnippet().getSymbolTable();
            System.out.println(label + "_FATALS=" + walker.getSnippet().getFatalErrorCount());
            System.out.println(label + "_TOP=" + st.keySet());
            System.out.println(label + "_ALIAS=" + topAlias(st));
            System.out.println(label + "_ERROR=");
        } catch (Exception e) {
            System.out.println(label + "_FATALS=-1");
            System.out.println(label + "_TOP=[]");
            System.out.println(label + "_ALIAS=null");
            System.out.println(label + "_ERROR=" + e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }

    private static SqlParseEventWalker walk(String sql, boolean sll) {
        SQLSelectParserParser parser = new SQLSelectParserParser(
                new CommonTokenStream(new SQLSelectParserLexer(CharStreams.fromString(sql))));
        parser.removeErrorListeners();
        if (sll) {
            parser.getInterpreter().setPredictionMode(PredictionMode.SLL);
        }
        SqlParseEventWalker walker = new SqlParseEventWalker();
        ParseTreeWalker.DEFAULT.walk(walker, parser.sql());
        walker.finalizeHandoffSymbolTable();
        return walker;
    }

    private static Object topAlias(Map<String, Object> st) {
        if (st.isEmpty()) {
            return null;
        }
        String top = st.keySet().iterator().next();
        Object scope = st.get(top);
        if (scope instanceof Map<?, ?> map) {
            return map.get("table_alias");
        }
        return null;
    }
}
