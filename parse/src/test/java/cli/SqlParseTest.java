package cli;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.After;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

public class SqlParseTest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;

    @Before
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }

    @After
    public void restoreStreams() {
        System.setOut(originalOut);
        System.setErr(originalErr);
    }

    @Test
    public void testMainWithValidArguments() {
        String[] args = {"sql", "SELECT * FROM mytable"};
        SqlParse sqlParse = new SqlParse();
        int exitCode = sqlParse.parse(args);
        assertEquals(0, exitCode);
        String output = outContent.toString();
        // A simple check to see if we get some JSON back.
        // A more robust test would parse the JSON and check its structure.
        assertEquals("Snippet [sqlAbstractTree={SQL={select={1={column={name=*, table_ref=*}}}, from={table={alias=null, table=mytable}}}}, tableDictionary={mytable={*=[@1,7:7='*',<289>,1:7]}}, symbolTable={query0={mytable={*=[@1,7:7='*',<289>,1:7]}, interface={*={column={name=*, table_ref=*}}}}}, substitutionsMap={}, queryInterface=[*], parserMessageList=[], parserMessageStringList=[], fatalErrorCount=0, fatalErrorStringList=[]]",
                output.trim()); 
        assertTrue(output.contains("sqlAbstractTree"));
        assertTrue(output.contains("tableDictionary"));
        assertTrue(output.contains("symbolTable"));
        assertTrue(output.contains("substitutionsMap"));
        assertTrue(output.contains("queryInterface"));
        assertTrue(output.contains("parserMessageList"));
        assertTrue(output.contains("fatalErrorStringList"));
        assertTrue(output.contains("fatalErrorCount"));
        System.out.println(output);
	
    }

    @Test
    public void testMainWithInvalidEndPoint() {
        String[] args = {"invalid_endpoint", "SELECT * FROM mytable"};
        SqlParse sqlParse = new SqlParse();
        int exitCode = sqlParse.parse(args);
        assertEquals(1, exitCode);
        String errorOutput = errContent.toString();
        assertTrue(errorOutput.contains("Invalid parser end point: invalid_endpoint"));
        assertTrue(errorOutput.contains("Valid values are:"));
    }

    @Test
    public void testMainWithNoArguments() {
        String[] args = {};
        SqlParse sqlParse = new SqlParse();
        int exitCode = sqlParse.parse(args);
        assertEquals(1, exitCode);
        String errorOutput = errContent.toString();
        assertTrue(errorOutput.contains("Usage: java cli.SqlParse <parser_end_point> \"<sql_text>\""));
    }
}
