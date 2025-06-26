package puml;

import access.PUML3ParserAccess;
import access.Snippet;
import access.SqlParserAccess;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


/**
 * Unit test for simple App.
 */
public class AppTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testPUML3Parser()
    {
        assertTrue( true );
        PUML3ParserAccess parserAccess = new PUML3ParserAccess(false, false, false);
        String pumlStmt = "SYSDATE > SYSDATE";
        parserAccess.executeTheParse(pumlStmt, "condition");
        Snippet snippet = parserAccess.getSnippet();
        System.out.println(snippet.toString());
    }

    public void testSQLParser()
    {
        assertTrue( true );
        SqlParserAccess parserAccess = new SqlParserAccess(true, true, true);
        String stmt = "(SYSDATE + SYSDATE)";
        parserAccess.executeTheParse(stmt, "predicand_value");
        Snippet snippet = parserAccess.getSnippet();
        System.out.println(snippet.toString());
    }

}
