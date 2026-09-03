package sql.latency;

import access.SqlParserAccess;

import static mumble.SQLParserEndPoints.SQLPARSER_SQL_TREE_KEY;

/** CLI: {@code mvn -pl parse exec:java -Dexec.mainClass=sql.latency.PantoSubMapRowProbe -Dexec.args=314} */
public final class PantoSubMapRowProbe {
    public static void main(String[] args) throws Exception {
        int row = Integer.parseInt(args[0]);
        String sql = PantoOutstandingSqlFixtures.sqlForCsvRow(row);
        SqlParserAccess access = new SqlParserAccess(false, false, false);
        access.executeTheParse(sql, SQLPARSER_SQL_TREE_KEY);
        int syntax = access.getParser() == null ? -1 : access.getParser().getNumberOfSyntaxErrors();
        int fatals = access.getSnippet() == null ? -1 : access.getSnippet().getFatalErrorCount();
        System.out.printf("row=%d syntaxErrors=%d fatals=%d tableDictKeys=%d%n",
                row,
                syntax,
                fatals,
                access.getSnippet() == null || access.getSnippet().getTableDictionary() == null
                        ? -1 : access.getSnippet().getTableDictionary().size());
        if (access.getFatalErrorList() != null) {
            access.getFatalErrorList().stream().limit(5).forEach(msg -> System.out.println("  fatal: " + msg));
        }
        access.getAllErrorStrings().stream().limit(5).forEach(msg -> System.out.println("  diag: " + msg));
    }
}
