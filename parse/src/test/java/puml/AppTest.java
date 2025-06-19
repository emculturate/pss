package puml;


import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import puml3.PUML3Parser;
import puml3.PUML3Parser.EquationContext;
import puml3.PUML3Lexer;
import puml.special.PUML3Listener;

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
    public void testApp()
    {
        assertTrue( true );
        CharStream input = CharStreams.fromString("if(true, [a], 99)");
        PUML3Lexer lexer = new PUML3Lexer(input);
        CommonTokenStream tokens = new CommonTokenStream((org.antlr.v4.runtime.TokenSource) lexer);
        PUML3Parser parser = new PUML3Parser(tokens);
        EquationContext tree = parser.equation(); // parse an equation

        PUML3Listener extractor = new PUML3Listener(parser);
        ParseTreeWalker.DEFAULT.walk(extractor, tree); 
        System.out.println(tree);
    }
}
