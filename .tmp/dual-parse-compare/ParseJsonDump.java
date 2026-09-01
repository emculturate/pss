import access.Snippet;
import access.SqlParserAccess;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static mumble.SQLParserEndPoints.SQLPARSER_SQL_TREE_KEY;

/** Minimal JSON dump of parse outputs for dual-version comparison. */
public final class ParseJsonDump {
    private ParseJsonDump() {
    }

    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.err.println("Usage: ParseJsonDump <sql-file>");
            System.exit(1);
        }

        String sql = Files.readString(Path.of(args[0]), StandardCharsets.UTF_8);
        SqlParserAccess access = new SqlParserAccess(false, false, false);
        access.executeTheParse(sql, SQLPARSER_SQL_TREE_KEY);
        Snippet snippet = access.getSnippet();
        if (snippet == null) {
            System.err.println("No snippet produced");
            System.exit(2);
        }

        Map<String, Object> out = new LinkedHashMap<>();
        out.put("tableDictionary", snippet.getTableDictionary());
        out.put("symbolTable", snippet.getSymbolTable());
        out.put("substitutionsMap", snippet.getSubstitutionsMap());
        out.put("queryInterface", snippet.getQueryInterface());
        out.put("fatalErrorCount", snippet.getFatalErrorCount());
        out.put("fatalErrorStringList", snippet.getFatalErrorStringList());

        try {
            var method = snippet.getClass().getMethod("getQueryColumnDictionaryMap");
            out.put("queryDictionary", method.invoke(snippet));
        } catch (NoSuchMethodException ignored) {
            out.put("queryDictionary", null);
        }

        try {
            var method = snippet.getClass().getMethod("getParserDiagnosticList");
            out.put("diagnostics", method.invoke(snippet));
        } catch (NoSuchMethodException ignored) {
            out.put("parserMessageList", snippet.getParserMessageList());
            out.put("parserMessageStringList", snippet.getParserMessageStringList());
        }

        Gson gson = new GsonBuilder().serializeNulls().create();
        System.out.println(gson.toJson(out));
    }
}
