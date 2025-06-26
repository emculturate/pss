package access;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import mumble.MumbleConstants;
import mumble.PUML3Constants;
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
        String stmt = "(SYSDATE + SYSDATE)";
        parserAccess.executeTheParse(stmt, MumbleConstants.MUMBLE_PREDICAND_TREE_KEY);
        Snippet snippet = parserAccess.getSnippet();
        System.out.println(snippet.toString());
    }

}
