package access;

import errorhandling.ParseDiagnostic;
import errorhandling.ParseSyntaxErrorContext;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import mumble.PUML3Constants;
import mumble.SQLParserEndPoints;
/**
 * Unit test for simple App.
 */
public class ParserAccessClassTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public ParserAccessClassTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( ParserAccessClassTest.class );
    }

    /**
     * Basic Parser Access Object Tests
     */

    // Test the PUML3ParserAccess class for condition and equation parsing.

    public void testConditionEndPointPUML3Parser()
    {
        assertTrue( true );
        PUML3ParserAccess parserAccess = new PUML3ParserAccess(true, true, true);
        String pumlStmt = "SYSDATE > SYSDATE";
        parserAccess.executeTheParse(pumlStmt, PUML3Constants.PUML3_CONDITION_TREE_KEY);
        Snippet snippet = parserAccess.getSnippet();
        System.out.println(snippet.toString());
    }

    public void testEquationEndPointPUML3Parser()
    {
        assertTrue( true );
        PUML3ParserAccess parserAccess = new PUML3ParserAccess(true, true, true);
        String pumlStmt = "SYSDATE";
        parserAccess.executeTheParse(pumlStmt, PUML3Constants.PUML3_EQUATION_TREE_KEY);
        Snippet snippet = parserAccess.getSnippet();
        System.out.println(snippet.toString());
    }

    // Test the SqlParserAccess class for various SQL parsing scenarios.

    public void testSQLParser()
    {
        assertTrue( true );
        SqlParserAccess parserAccess = new SqlParserAccess(true, true, true);
        String stmt = "from (SYSDATE + SYSDATE)";
        parserAccess.executeTheParse(stmt, SQLParserEndPoints.SQLPARSER_PREDICAND_TREE_KEY);
        Snippet snippet = parserAccess.getSnippet();
        System.out.println(snippet.toString());
    }

    public void testBadSQLCollectsFatalErrors()
    {
        SqlParserAccess parserAccess = new SqlParserAccess(true, true, true);
        String badSql = "SELECT FROM";

        parserAccess.executeTheParse(badSql, SQLParserEndPoints.SQLPARSER_SQL_TREE_KEY);

        assertTrue(parserAccess.hasFatalErrors());
        assertNotNull(parserAccess.getFatalErrorList());
        assertFalse(parserAccess.getFatalErrorList().isEmpty());

        Snippet snippet = parserAccess.getSnippet();
        System.out.println(snippet.toString());

        assertNotNull(snippet);
        assertNotNull(snippet.getFatalErrorStringList());
        assertFalse(snippet.getFatalErrorStringList().isEmpty());
        assertNotNull(snippet.getParserDiagnosticList());
        assertTrue(snippet.getParserDiagnosticList().stream()
            .anyMatch(d -> d.severity() == ParseDiagnostic.Severity.FATAL));
        assertEquals(snippet.getFatalErrorCount(), snippet.getFatalErrorStringList().size());

        ParseDiagnostic reportError = snippet.getParserDiagnosticList().stream()
                .filter(d -> d != null && "REPORT_ERROR".equals(d.code()))
                .findFirst()
                .orElse(null);
        assertNotNull(reportError);
        assertEquals(Integer.valueOf(1), reportError.line());
        assertEquals(Integer.valueOf(7), reportError.charPositionInLine());
        assertEquals("FROM", reportError.tokenText());
        assertEquals("select_item", reportError.ruleName());
        assertEquals(
                "Line 1:7 - unexpected input: 'FROM' in rule select_item: SELECT FROM",
                reportError.message());
        assertNotNull(reportError.details());
        assertEquals("SELECT FROM", reportError.details().get(ParseSyntaxErrorContext.DETAIL_CONTEXT_SNIPPET));
        assertEquals(ParseSyntaxErrorContext.SYNTAX_CLASS_GRAMMAR_GAP,
                reportError.details().get(ParseSyntaxErrorContext.DETAIL_SYNTAX_CLASS));
        assertEquals("select_item,select_list,query_specification",
                reportError.details().get(ParseSyntaxErrorContext.DETAIL_PARSER_RULES));
    }

}
