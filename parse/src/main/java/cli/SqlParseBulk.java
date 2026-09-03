package cli;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

import com.google.gson.JsonObject;

/**
 * Bulk stdin transport: reads raw UTF-8 SQL from stdin and writes the same parse envelope MCP clients
 * expect, without embedding SQL in JSON.
 *
 * <pre>
 * java -cp pss-parse-fat.jar cli.SqlParseBulk SQL &lt; query.sql
 * java -cp pss-parse-fat.jar cli.SqlParseMCP --bulk SQL &lt; query.sql
 * </pre>
 */
public final class SqlParseBulk {

    private SqlParseBulk() {
    }

    public static void main(String[] args) {
        int endpointIndex = 0;
        if (args.length > 0 && "--bulk".equals(args[0])) {
            endpointIndex = 1;
        }
        String endPoint = args.length > endpointIndex ? args[endpointIndex] : "SQL";
        run(endPoint, System.in);
    }

    static void run(String endPoint, java.io.InputStream in) {
        try {
            String sql = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))
                    .lines()
                    .collect(Collectors.joining("\n"));
            sql = SqlParseMcpSupport.normalizeSqlTextForMcp(sql);
            JsonObject result = SqlParseMcpSupport.parseAndFormatWithTransport(
                    endPoint, sql, SqlParseMcpSupport.TRANSPORT_BULK);
            JsonObject meta = new JsonObject();
            meta.addProperty("versionTag", "5.1.3");
            result.add("rmcpParserMeta", meta);
            System.out.println(SqlParseMcpSupport.gson().toJson(result));
        } catch (RuntimeException e) {
            JsonObject error = SqlParseMcpSupport.buildMcpInternalErrorResult(e);
            error.addProperty("transport", SqlParseMcpSupport.TRANSPORT_BULK);
            System.out.println(SqlParseMcpSupport.gson().toJson(error));
        }
    }
}
