package cli;

import access.Snippet;
import access.SqlParserAccess;
import mumble.SQLParserEndPoints;

public class SqlParse {
    /* Parse method wraps the command line handling but also separates the 
       results from the JVM closing operation in the main method. This allows the main logic to be tested within the project.
        */
    public int parse(String[] args) {
        if (args.length != 2) {
            System.err.println("Usage: java cli.SqlParse <parser_end_point> \"<sql_text>\"");
            System.err.println("Example: java cli.SqlParse SQL \"SELECT * FROM mytable\"");
            return 1;
        }

        String endPointStr = args[0];
        String sqlText = args[1];

        String endPoint = endPointStr.toUpperCase();
        if (SQLParserEndPoints.getNameForValue(endPoint) == null || SQLParserEndPoints.getNameForValue(endPoint).isEmpty()) {
            System.err.println("Invalid parser end point: " + endPointStr);
            System.err.print("Valid values are: ");
            SQLParserEndPoints.getValueToNameMap().keySet().forEach(key -> System.err.print(key + " "));
            System.err.println();
            return 1;
        }

        SqlParserAccess access = new SqlParserAccess(false, false, false);
        access.executeTheParse(sqlText, endPoint);
        Snippet snippet = access.getSnippet(); // Retrieve the Snippet object from the access object

        try {
            System.out.println(snippet.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return 1;
        }

        return 0;
    }

    public static void main(String[] args) {
        SqlParse sqlParse = new SqlParse();
        int exitCode = sqlParse.parse(args);
        System.exit(exitCode);
    }
}
