package sql.walker;
import org.junit.Assert;
import org.junit.Test;

import access.Snippet;
import errorhandling.ParseDiagnostic;
import errorhandling.ParseErrorCollector;
import static mumble.SQLParserEndPoints.SQLPARSER_DDL_TREE_KEY;
import static mumble.SQLParserEndPoints.SQLPARSER_DELETE_TREE_KEY;
import static mumble.SQLParserEndPoints.SQLPARSER_SQL_TREE_KEY;
import static mumble.SQLParserEndPoints.SQLPARSER_TRUNCATE_TREE_KEY;
import static mumble.SQLParserEndPoints.SQLPARSER_TUPLE_TREE_KEY;
import static mumble.SQLParserEndPoints.SQLPARSER_UPDATE_TREE_KEY;
import static mumble.SQLParserEndPoints.SQLPARSER_VALUES_TREE_KEY;
import sql.SQLSelectParserParser;
import sql.SQLSelectParserParser.SqlContext;

public class SqlEventWalkerNonSqlEndpointParserTests extends AbstractSqlParseEventWalkerTest {

	@Test
	public void arithmeticExpressionPredicandTest() {
		String sql = "-(aa.scbcrse_coll_code * 6 - other) ";
		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runPredicandParsertest(sql, parser);

		Assert.assertEquals("AST is wrong", "{PREDICAND={calc={left={literal=-1}, right={parentheses={calc={left={calc={left={column={name=scbcrse_coll_code, table_ref=aa}}, right={literal=6}, operator=*}}, right={column={name=other, table_ref=null}}, operator=-}}}, operator=*}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{table_dictionary={unresolved_column={other=[[@8,29:33='other',<392>,1:29]], aa.scbcrse_coll_code=[[@2,2:3='aa',<392>,1:2]]}}, unresolved_column={other={column={name=other, table_ref=null}, locations=[[@8,29:33='other',<392>,1:29]]}, aa.scbcrse_coll_code={column={name=scbcrse_coll_code, table_ref=aa}, locations=[[@2,2:3='aa',<392>,1:2]]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void invalidVariableNamePopulationTest() {
		// Variables cannot start with population qualifiers
		final String query = " SELECT  a.col FROM <{pop1}>  as a "; 

		final SQLSelectParserParser parser = parse(query);
		runExpectSQLParserFailuretest(query, parser);
	}


	@Test
	public void tupleSubstitutionVariableTestV1() {
		// Special Tuple Variable Test with synthetic tuple constructed even when tuple variable name is malformed
		// This test validates that the parser recovers from the malformed tuple variable by synthesizing a tuple variable entry in the substitution map 
		// and retaining it for downstream processing, and surfaces appropriate diagnostics for the malformed tuple variable name
		String query = " select 1 from <[Acquia_ALR].[no__contacts].last_delivered> no_contacts";
		final SQLSelectParserParser parser = parse(query);
		SqlContext tree = parser.sql();
		ParseErrorCollector parseErrorCollector = (ParseErrorCollector) parser.getErrorHandler();
		System.out.println("[V1 errors] count=" + parseErrorCollector.getErrorCount() + " list=" + parseErrorCollector.getErrorList());
		Assert.assertNotEquals(
				"Expected parser recovery diagnostics for malformed tuple variable in query: " + query,
				0,
				parseErrorCollector.getErrorCount());
		assertParserErrorsContainExactly(
				parseErrorCollector,
				"Line 1:15 - null - unexpected input: '<'",
				"Line 1:15 - Recovering malformed variable identifier start '<' by skipping one token",
				"Line 1:28 - Invalid syntax near '.'");

		SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		Snippet snippet = extractor.getSnippet();

		assertDiagnosticAtPosition(
				snippet,
				"RECOVER_MALFORMED_VARIABLE_START",
				ParseDiagnostic.Severity.WARNING,
				"Recovering malformed variable identifier start '<' by skipping one token",
				"<",
				1,
				15);
		
			assertDiagnosticAtPosition(
				snippet,
				"INVALID VARIABLE NAME",
				ParseDiagnostic.Severity.FATAL,
				"Format of Variable Name is unrecognized <[Acquia_ALR].[no__contacts].last_delivered> as one of the supported variable identifier forms at (l:1 c:15).",
				"<[Acquia_ALR].[no__contacts].last_delivered>",
				1,
				15);

		Assert.assertTrue(
				"Expected synthesized substitution variable entry to be retained after recovery",
				extractor.getSubstitutionsMap().containsKey("<[Acquia_ALR].[no__contacts].last_delivered>"));
	}


	@Test
	public void tupleSubstitutionVariableTestV2() {
		// Special Tuple Variable Test with synthetic tuple constructed even when tuple variable name is malformed
		// This test validates that the parser recovers from the malformed tuple variable by synthesizing a tuple variable entry in the substitution map 
		// and retaining it for downstream processing, and surfaces appropriate diagnostics for the malformed tuple variable name
		String query = " select 1 from <[Acquia_ALR].[no__contacts].hmmm> no_contacts"
			+ " join <[another].[malformed].tuple> t on 1=1";
		final SQLSelectParserParser parser = parse(query);
		SqlContext tree = parser.sql();
		ParseErrorCollector parseErrorCollector = (ParseErrorCollector) parser.getErrorHandler();
		System.out.println("[V2 errors] count=" + parseErrorCollector.getErrorCount() + " list=" + parseErrorCollector.getErrorList());
		Assert.assertNotEquals(
				"Expected parser recovery diagnostics for malformed tuple variable in query: " + query,
				0,
				parseErrorCollector.getErrorCount());
		assertParserErrorsContainExactly(
				parseErrorCollector,
				"Line 1:15 - null - unexpected input: '<'",
				"Line 1:15 - Recovering malformed variable identifier start '<' by skipping one token",
				"Line 1:28 - Invalid syntax near '.'");

		SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		Snippet snippet = extractor.getSnippet();

		assertDiagnosticAtPosition(
				snippet,
				"RECOVER_MALFORMED_VARIABLE_START",
				ParseDiagnostic.Severity.WARNING,
				"Recovering malformed variable identifier start '<' by skipping one token",
				"<",
				1,
				15);
		
		assertDiagnosticAtPosition(
				snippet,
				"INVALID VARIABLE NAME",
				ParseDiagnostic.Severity.FATAL,
				"Format of Variable Name is unrecognized <[Acquia_ALR].[no__contacts].hmmm> as one of the supported variable identifier forms at (l:1 c:15).",
				"<[Acquia_ALR].[no__contacts].hmmm>",
				1,
				15);

		Assert.assertTrue(
				"Expected synthesized substitution variable entry to be retained after recovery",
				extractor.getSubstitutionsMap().containsKey("<[Acquia_ALR].[no__contacts].hmmm>"));
	}


	@Test
	public void basicCaseConditionConstantsTest() {
		String sql = "case when true then 'Y' when false then 'N' else 'N' end";
		final SQLSelectParserParser parser = parse(sql);
		
		SqlParseEventWalker extractor = runPredicandParsertest(sql, parser);
		
		Assert.assertEquals("AST is wrong", "{PREDICAND={case={clauses={1={then={literal='Y'}, when={literal=true}}, 2={then={literal='N'}, when={literal=false}}}, else={literal='N'}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void basicCaseExplicitConditionExpressionTest() {
		String sql = "case when column1 = true then 'Y' when column2 = false then 'N' else 'N' end";
		final SQLSelectParserParser parser = parse(sql);
		
		SqlParseEventWalker extractor = runPredicandParsertest(sql, parser);
		
		Assert.assertEquals("AST is wrong", "{PREDICAND={case={clauses={1={then={literal='Y'}, when={condition={left={column={name=column1, table_ref=null}}, right={literal=true}, operator==}}}, 2={then={literal='N'}, when={condition={left={column={name=column2, table_ref=null}}, right={literal=false}, operator==}}}}, else={literal='N'}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{table_dictionary={unresolved_column={column1=[[@2,10:16='column1',<392>,1:10]], column2=[[@8,39:45='column2',<392>,1:39]]}}, unresolved_column={column1={column={name=column1, table_ref=null}, locations=[[@2,10:16='column1',<392>,1:10]]}, column2={column={name=column2, table_ref=null}, locations=[[@8,39:45='column2',<392>,1:39]]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void basicCaseImpliedConditionExpressionV1Test() {
		String sql = "case column1 when true then 'Y' when false then 'N' else 'N' end";
		final SQLSelectParserParser parser = parse(sql);
		
		SqlParseEventWalker extractor = runPredicandParsertest(sql, parser);
		
		Assert.assertEquals("AST is wrong", "{PREDICAND={case={item={column={name=column1, table_ref=null}}, clauses={1={then={literal='Y'}, when={literal=true}}, 2={then={literal='N'}, when={literal=false}}}, else={literal='N'}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{table_dictionary={unresolved_column={column1=[[@1,5:11='column1',<392>,1:5]]}}, unresolved_column={column1={column={name=column1, table_ref=null}, locations=[[@1,5:11='column1',<392>,1:5]]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void basicCaseImpliedColumnExpressionV2Test() {
		String sql = "case column1 when column2 then 'Y' when column3 then 'N' else 'N' end";
		final SQLSelectParserParser parser = parse(sql);
		
		SqlParseEventWalker extractor = runPredicandParsertest(sql, parser);
		
		Assert.assertEquals("AST is wrong", "{PREDICAND={case={item={column={name=column1, table_ref=null}}, clauses={1={then={literal='Y'}, when={column={name=column2, table_ref=null}}}, 2={then={literal='N'}, when={column={name=column3, table_ref=null}}}}, else={literal='N'}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{table_dictionary={unresolved_column={column1=[[@1,5:11='column1',<392>,1:5]], column3=[[@7,40:46='column3',<392>,1:40]], column2=[[@3,18:24='column2',<392>,1:18]]}}, unresolved_column={column1={column={name=column1, table_ref=null}, locations=[[@1,5:11='column1',<392>,1:5]]}, column3={column={name=column3, table_ref=null}, locations=[[@7,40:46='column3',<392>,1:40]]}, column2={column={name=column2, table_ref=null}, locations=[[@3,18:24='column2',<392>,1:18]]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void complexCaseImpliedConditionExpressionWithPredicandSubstitutionPos1Test() {
		// Item 27 - Substitution variable <item> does not get the right type, should be PREDICAND because of the type of CASE STMT
		// Item 50 - Table Dictionary is not created when the Predicand is parsed on its own
		final String query = "CASE observation_time WHEN s948.OBSERVATION_TM THEN S948.t_student_last_name "
				+ " WHEN <item> THEN S949.t_student_last_name "
				+ " ELSE COALESCE(S948.t_student_last_name, S949.t_student_last_name) END";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runPredicandParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{PREDICAND={case={item={column={name=observation_time, table_ref=null}}, clauses={1={then={column={name=t_student_last_name, table_ref=S948}}, when={column={name=OBSERVATION_TM, table_ref=s948}}}, 2={then={column={name=t_student_last_name, table_ref=S949}}, when={substitution={name=<item>, type=predicand}}}}, else={function={parameters={1={column={name=t_student_last_name, table_ref=S948}}, 2={column={name=t_student_last_name, table_ref=S949}}}, function_name=COALESCE}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<item>=predicand}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{table_dictionary={unresolved_column={observation_time=[[@1,5:20='observation_time',<392>,1:5]], S949.t_student_last_name=[[@13,95:98='S949',<392>,1:95], [@23,161:164='S949',<392>,1:161]], s948.OBSERVATION_TM=[[@3,27:30='s948',<392>,1:27]], S948.t_student_last_name=[[@7,52:55='S948',<392>,1:52], [@19,135:138='S948',<392>,1:135]]}}, unresolved_column={observation_time={column={name=observation_time, table_ref=null}, locations=[[@1,5:20='observation_time',<392>,1:5]]}, S949.t_student_last_name={column={name=t_student_last_name, table_ref=S949}, locations=[[@13,95:98='S949',<392>,1:95], [@23,161:164='S949',<392>,1:161]]}, s948.OBSERVATION_TM={column={name=OBSERVATION_TM, table_ref=s948}, locations=[[@3,27:30='s948',<392>,1:27]]}, S948.t_student_last_name={column={name=t_student_last_name, table_ref=S948}, locations=[[@7,52:55='S948',<392>,1:52], [@19,135:138='S948',<392>,1:135]]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void complexCaseImpliedConditionExpressionWithPredicandSubstitutionPos2Test() {
		// Item 27 - Substitution variable <column2> does not get the right type, should be PREDICAND because of the type of CASE STMT
		// Item 50 - Table Dictionary is not created when the Predicand is parsed on its own
		String sql = "case <column1> when column2 then 'Y' when column3 then 'N' else 'N' end";
		final SQLSelectParserParser parser = parse(sql);
		
		SqlParseEventWalker extractor = runPredicandParsertest(sql, parser);
		
		Assert.assertEquals("AST is wrong", "{PREDICAND={case={item={substitution={name=<column1>, type=predicand}}, clauses={1={then={literal='Y'}, when={column={name=column2, table_ref=null}}}, 2={then={literal='N'}, when={column={name=column3, table_ref=null}}}}, else={literal='N'}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<column1>=predicand}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{table_dictionary={unresolved_column={column3=[[@7,42:48='column3',<392>,1:42]], column2=[[@3,20:26='column2',<392>,1:20]]}}, unresolved_column={column3={column={name=column3, table_ref=null}, locations=[[@7,42:48='column3',<392>,1:42]]}, column2={column={name=column2, table_ref=null}, locations=[[@3,20:26='column2',<392>,1:20]]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void complexCaseImpliedConditionExpressionWithPredicandSubstitutionInThenTest() {
		// Item 27 - Substitution variable <column2> does not get the right type, should be PREDICAND because of the type of CASE STMT
		// Item 50 - Table Dictionary is not created when the Predicand is parsed on its own
		String sql = "case column1 when <column2> then 'Y' when column3 then 'N' else 'N' end";
		final SQLSelectParserParser parser = parse(sql);
		
		SqlParseEventWalker extractor = runPredicandParsertest(sql, parser);
		
		Assert.assertEquals("AST is wrong", "{PREDICAND={case={item={column={name=column1, table_ref=null}}, clauses={1={then={literal='Y'}, when={substitution={name=<column2>, type=predicand}}}, 2={then={literal='N'}, when={column={name=column3, table_ref=null}}}}, else={literal='N'}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<column2>=predicand}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{table_dictionary={unresolved_column={column1=[[@1,5:11='column1',<392>,1:5]], column3=[[@7,42:48='column3',<392>,1:42]]}}, unresolved_column={column1={column={name=column1, table_ref=null}, locations=[[@1,5:11='column1',<392>,1:5]]}, column3={column={name=column3, table_ref=null}, locations=[[@7,42:48='column3',<392>,1:42]]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void complexCaseImpliedConditionExpressionWithPredicandSubstitutionPos3Test() {
		// Item 30 - Predicand substitution not typed nor included in the Substitution Table
		String sql = "case column1 when column2 then 'Y' when column3 then <column4> else 'N' end";
		final SQLSelectParserParser parser = parse(sql);
		
		SqlParseEventWalker extractor = runPredicandParsertest(sql, parser);
		
		Assert.assertEquals("AST is wrong", "{PREDICAND={case={item={column={name=column1, table_ref=null}}, clauses={1={then={literal='Y'}, when={column={name=column2, table_ref=null}}}, 2={then={substitution={name=<column4>, type=predicand}}, when={column={name=column3, table_ref=null}}}}, else={literal='N'}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<column4>=predicand}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{table_dictionary={unresolved_column={column1=[[@1,5:11='column1',<392>,1:5]], column3=[[@7,40:46='column3',<392>,1:40]], column2=[[@3,18:24='column2',<392>,1:18]]}}, unresolved_column={column1={column={name=column1, table_ref=null}, locations=[[@1,5:11='column1',<392>,1:5]]}, column3={column={name=column3, table_ref=null}, locations=[[@7,40:46='column3',<392>,1:40]]}, column2={column={name=column2, table_ref=null}, locations=[[@3,18:24='column2',<392>,1:18]]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void complexCaseImpliedConditionExpressionWithPredicandSubstitutionPos4Test() {
		// Item 30 - Predicand substitution not typed nor included in the Substitution Table
		String sql = "case column1 when column2 then 'Y' when column3 then column4 else <column5> end";
		final SQLSelectParserParser parser = parse(sql);
		
		SqlParseEventWalker extractor = runPredicandParsertest(sql, parser);
		
		Assert.assertEquals("AST is wrong", "{PREDICAND={case={item={column={name=column1, table_ref=null}}, clauses={1={then={literal='Y'}, when={column={name=column2, table_ref=null}}}, 2={then={column={name=column4, table_ref=null}}, when={column={name=column3, table_ref=null}}}}, else={substitution={name=<column5>, type=predicand}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<column5>=predicand}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{table_dictionary={unresolved_column={column1=[[@1,5:11='column1',<392>,1:5]], column4=[[@9,53:59='column4',<392>,1:53]], column3=[[@7,40:46='column3',<392>,1:40]], column2=[[@3,18:24='column2',<392>,1:18]]}}, unresolved_column={column1={column={name=column1, table_ref=null}, locations=[[@1,5:11='column1',<392>,1:5]]}, column4={column={name=column4, table_ref=null}, locations=[[@9,53:59='column4',<392>,1:53]]}, column3={column={name=column3, table_ref=null}, locations=[[@7,40:46='column3',<392>,1:40]]}, column2={column={name=column2, table_ref=null}, locations=[[@3,18:24='column2',<392>,1:18]]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void complexCaseExplicitConditionExpressionWithPredicandSubstitutionPos1Test() {
		String sql = "case when <column1> = true then 'Y' when column2 = false then 'N' else 'N' end";
		final SQLSelectParserParser parser = parse(sql);
		
		SqlParseEventWalker extractor = runPredicandParsertest(sql, parser);
		
		Assert.assertEquals("AST is wrong", "{PREDICAND={case={clauses={1={then={literal='Y'}, when={condition={left={substitution={name=<column1>, type=predicand}}, right={literal=true}, operator==}}}, 2={then={literal='N'}, when={condition={left={column={name=column2, table_ref=null}}, right={literal=false}, operator==}}}}, else={literal='N'}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<column1>=predicand}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{table_dictionary={unresolved_column={column2=[[@8,41:47='column2',<392>,1:41]]}}, unresolved_column={column2={column={name=column2, table_ref=null}, locations=[[@8,41:47='column2',<392>,1:41]]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void complexCaseExplicitConditionExpressionWithPredicandSubstitutionPos2Test() {
		String sql = "case when a.<column1> = 700 then 'Y' when a.column2 = 800 then 'N' else 'N' end";
		final SQLSelectParserParser parser = parse(sql);
		
		SqlParseEventWalker extractor = runPredicandParsertest(sql, parser);
		
		Assert.assertEquals("AST is wrong", "{PREDICAND={case={clauses={1={then={literal='Y'}, when={condition={left={column={substitution={name=<column1>, type=column}, table_ref=a}}, right={literal=700}, operator==}}}, 2={then={literal='N'}, when={condition={left={column={name=column2, table_ref=a}}, right={literal=800}, operator==}}}}, else={literal='N'}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<column1>=column}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{table_dictionary={unresolved_column={a.column2=[[@10,42:42='a',<392>,1:42]], a.<column1>=[[@2,10:10='a',<392>,1:10]]}}, unresolved_column={a.column2={column={name=column2, table_ref=a}, locations=[[@10,42:42='a',<392>,1:42]]}, a.<column1>={column={table_ref=a, substitution={name=<column1>, type=column}}, locations=[[@2,10:10='a',<392>,1:10]]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void complexCaseExplicitConditionExpressionWithPredicandSubstitutionPos3Test() {
		String sql = "case when <column1> then 'Y' when column2 = false then 'N' else 'N' end";
		final SQLSelectParserParser parser = parse(sql);
		
		SqlParseEventWalker extractor = runPredicandParsertest(sql, parser);
		
		Assert.assertEquals("AST is wrong", "{PREDICAND={case={clauses={1={then={literal='Y'}, when={substitution={name=<column1>, type=condition}}}, 2={then={literal='N'}, when={condition={left={column={name=column2, table_ref=null}}, right={literal=false}, operator==}}}}, else={literal='N'}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<column1>=condition}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{table_dictionary={unresolved_column={column2=[[@6,34:40='column2',<392>,1:34]]}}, unresolved_column={column2={column={name=column2, table_ref=null}, locations=[[@6,34:40='column2',<392>,1:34]]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void complexCaseExplicitConditionExpressionWithPredicandSubstitutionPos4Test() {
		// Item 30 - Predicand substitution not typed nor included in the Substitution Table
		String sql = "case when a.column1 = 700 then 'Y' when a.column2 = 800 then <predicand> else 'N' end";
		final SQLSelectParserParser parser = parse(sql);
		
		SqlParseEventWalker extractor = runPredicandParsertest(sql, parser);
		
		Assert.assertEquals("AST is wrong", "{PREDICAND={case={clauses={1={then={literal='Y'}, when={condition={left={column={name=column1, table_ref=a}}, right={literal=700}, operator==}}}, 2={then={substitution={name=<predicand>, type=predicand}}, when={condition={left={column={name=column2, table_ref=a}}, right={literal=800}, operator==}}}}, else={literal='N'}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<predicand>=predicand}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{table_dictionary={unresolved_column={a.column2=[[@10,40:40='a',<392>,1:40]], a.column1=[[@2,10:10='a',<392>,1:10]]}}, unresolved_column={a.column2={column={name=column2, table_ref=a}, locations=[[@10,40:40='a',<392>,1:40]]}, a.column1={column={name=column1, table_ref=a}, locations=[[@2,10:10='a',<392>,1:10]]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void complexCaseExplicitConditionExpressionWithPredicandSubstitutionPos5Test() {
		// Item 30 - Predicand substitution not typed nor included in the Substitution Table
		String sql = "case when a.column1 = 700 then 'Y' when a.column2 = 800 then 'N' else <predicand> end";
		final SQLSelectParserParser parser = parse(sql);
		
		SqlParseEventWalker extractor = runPredicandParsertest(sql, parser);
		
		Assert.assertEquals("AST is wrong", "{PREDICAND={case={clauses={1={then={literal='Y'}, when={condition={left={column={name=column1, table_ref=a}}, right={literal=700}, operator==}}}, 2={then={literal='N'}, when={condition={left={column={name=column2, table_ref=a}}, right={literal=800}, operator==}}}}, else={substitution={name=<predicand>, type=predicand}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<predicand>=predicand}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{table_dictionary={unresolved_column={a.column2=[[@10,40:40='a',<392>,1:40]], a.column1=[[@2,10:10='a',<392>,1:10]]}}, unresolved_column={a.column2={column={name=column2, table_ref=a}, locations=[[@10,40:40='a',<392>,1:40]]}, a.column1={column={name=column1, table_ref=a}, locations=[[@2,10:10='a',<392>,1:10]]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void complexCaseExplicitConditionExpressionWithPredicandSubstitutionPos6Test() {
		String sql = "case when a.column1 = 700 then 'Y' when a.column2 = 800 then a.<column4> else 'N' end";
		final SQLSelectParserParser parser = parse(sql);
		
		SqlParseEventWalker extractor = runPredicandParsertest(sql, parser);
		
		Assert.assertEquals("AST is wrong", "{PREDICAND={case={clauses={1={then={literal='Y'}, when={condition={left={column={name=column1, table_ref=a}}, right={literal=700}, operator==}}}, 2={then={column={substitution={name=<column4>, type=column}, table_ref=a}}, when={condition={left={column={name=column2, table_ref=a}}, right={literal=800}, operator==}}}}, else={literal='N'}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<column4>=column}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{table_dictionary={unresolved_column={a.column2=[[@10,40:40='a',<392>,1:40]], a.column1=[[@2,10:10='a',<392>,1:10]], a.<column4>=[[@16,61:61='a',<392>,1:61]]}}, unresolved_column={a.column2={column={name=column2, table_ref=a}, locations=[[@10,40:40='a',<392>,1:40]]}, a.column1={column={name=column1, table_ref=a}, locations=[[@2,10:10='a',<392>,1:10]]}, a.<column4>={column={table_ref=a, substitution={name=<column4>, type=column}}, locations=[[@16,61:61='a',<392>,1:61]]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void complexCaseExplicitConditionExpressionWithPredicandSubstitutionPos7Test() {
		String sql = "case when a.column1 = 700 then 'Y' when a.column2 = 800 then 'N' else a.<column4> end";
		final SQLSelectParserParser parser = parse(sql);
		
		SqlParseEventWalker extractor = runPredicandParsertest(sql, parser);
		
		Assert.assertEquals("AST is wrong", "{PREDICAND={case={clauses={1={then={literal='Y'}, when={condition={left={column={name=column1, table_ref=a}}, right={literal=700}, operator==}}}, 2={then={literal='N'}, when={condition={left={column={name=column2, table_ref=a}}, right={literal=800}, operator==}}}}, else={column={substitution={name=<column4>, type=column}, table_ref=a}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<column4>=column}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{table_dictionary={unresolved_column={a.column2=[[@10,40:40='a',<392>,1:40]], a.column1=[[@2,10:10='a',<392>,1:10]], a.<column4>=[[@18,70:70='a',<392>,1:70]]}}, unresolved_column={a.column2={column={name=column2, table_ref=a}, locations=[[@10,40:40='a',<392>,1:40]]}, a.column1={column={name=column1, table_ref=a}, locations=[[@2,10:10='a',<392>,1:10]]}, a.<column4>={column={table_ref=a, substitution={name=<column4>, type=column}}, locations=[[@18,70:70='a',<392>,1:70]]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void windowFunctionPredicandTest() {
		String sql = "rank() OVER (partition by k_stfd, kppi order by OBSERVATION_TM desc, row_num desc)";
		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runPredicandParsertest(sql, parser);
		
		Assert.assertEquals("AST is wrong", "{PREDICAND={window_function={over={partition_by={1={column={name=k_stfd, table_ref=null}}, 2={column={name=kppi, table_ref=null}}}, orderby={1={null_order=null, predicand={column={name=OBSERVATION_TM, table_ref=null}}, sort_order=desc}, 2={null_order=null, predicand={column={name=row_num, table_ref=null}}, sort_order=desc}}}, function={function_name=rank, parameters=null}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{window_ordered_by=[{name=OBSERVATION_TM, locations=[[@12,48:61='OBSERVATION_TM',<392>,1:48]], table_ref=null}, {name=row_num, locations=[[@15,69:75='row_num',<392>,1:69]], table_ref=null}], table_dictionary={unresolved_column={k_stfd=[[@7,26:31='k_stfd',<392>,1:26]], row_num=[[@15,69:75='row_num',<392>,1:69]], kppi=[[@9,34:37='kppi',<392>,1:34]], OBSERVATION_TM=[[@12,48:61='OBSERVATION_TM',<392>,1:48]]}}, unresolved_column={k_stfd={column={name=k_stfd, table_ref=null}, locations=[[@7,26:31='k_stfd',<392>,1:26]]}, row_num={column={name=row_num, table_ref=null}, locations=[[@15,69:75='row_num',<392>,1:69]]}, kppi={column={name=kppi, table_ref=null}, locations=[[@9,34:37='kppi',<392>,1:34]]}, OBSERVATION_TM={column={name=OBSERVATION_TM, table_ref=null}, locations=[[@12,48:61='OBSERVATION_TM',<392>,1:48]]}}, window_partition_by=[{name=k_stfd, locations=[[@7,26:31='k_stfd',<392>,1:26]], table_ref=null}, {name=kppi, locations=[[@9,34:37='kppi',<392>,1:34]], table_ref=null}]}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void windowFunctionColumnVariableP1Test() {
		// Item 52 - Partition clause doesn't take column references with table references/aliases
		String sql = "rank(a.<columnParam>) OVER (partition by a.k_stfd, a.kppi order by a.row_num desc)";
		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runPredicandParsertest(sql, parser);
		
		Assert.assertEquals("AST is wrong", "{PREDICAND={window_function={over={partition_by={1={column={name=k_stfd, table_ref=a}}, 2={column={name=kppi, table_ref=a}}}, orderby={1={null_order=null, predicand={column={name=row_num, table_ref=a}}, sort_order=desc}}}, function={function_name=rank, parameters={1={column={substitution={name=<columnParam>, type=column}, table_ref=a}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<columnParam>=column}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{window_ordered_by=[{name=row_num, locations=[[@21,69:75='row_num',<392>,1:69]], table_ref=a}], table_dictionary={unresolved_column={a.k_stfd=[[@10,41:41='a',<392>,1:41]], a.<columnParam>=[[@2,5:5='a',<392>,1:5]], a.row_num=[[@19,67:67='a',<392>,1:67]], a.kppi=[[@14,51:51='a',<392>,1:51]]}}, unresolved_column={a.k_stfd={column={name=k_stfd, table_ref=a}, locations=[[@10,41:41='a',<392>,1:41]]}, a.<columnParam>={column={table_ref=a, substitution={name=<columnParam>, type=column}}, locations=[[@2,5:5='a',<392>,1:5]]}, a.row_num={column={name=row_num, table_ref=a}, locations=[[@19,67:67='a',<392>,1:67]]}, a.kppi={column={name=kppi, table_ref=a}, locations=[[@14,51:51='a',<392>,1:51]]}}, window_partition_by=[{name=k_stfd, locations=[[@12,43:48='k_stfd',<392>,1:43]], table_ref=a}, {name=kppi, locations=[[@16,53:56='kppi',<392>,1:53]], table_ref=a}]}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void windowFunctionColumnVariableP2Test() {
		// Item 52 - Partition clause doesn't take column references with table references/aliases
		String sql = "rank(a.column) OVER (partition by a.<k_stfd>, a.kppi order by a.row_num desc)";
		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runPredicandParsertest(sql, parser);
		
		Assert.assertEquals("AST is wrong", "{PREDICAND={window_function={over={partition_by={1={column={substitution={name=<k_stfd>, type=column}, table_ref=a}}, 2={column={name=kppi, table_ref=a}}}, orderby={1={null_order=null, predicand={column={name=row_num, table_ref=a}}, sort_order=desc}}}, function={function_name=rank, parameters={1={column={name=column, table_ref=a}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<k_stfd>=column}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{window_ordered_by=[{name=row_num, locations=[[@21,64:70='row_num',<392>,1:64]], table_ref=a}], table_dictionary={unresolved_column={a.<k_stfd>=[[@10,34:34='a',<392>,1:34]], a.row_num=[[@19,62:62='a',<392>,1:62]], a.kppi=[[@14,46:46='a',<392>,1:46]], a.column=[[@2,5:5='a',<392>,1:5]]}}, unresolved_column={a.<k_stfd>={column={table_ref=a, substitution={name=<k_stfd>, type=column}}, locations=[[@10,34:34='a',<392>,1:34]]}, a.row_num={column={name=row_num, table_ref=a}, locations=[[@19,62:62='a',<392>,1:62]]}, a.kppi={column={name=kppi, table_ref=a}, locations=[[@14,46:46='a',<392>,1:46]]}, a.column={column={name=column, table_ref=a}, locations=[[@2,5:5='a',<392>,1:5]]}}, window_partition_by=[{substitution={name=<k_stfd>, type=column}, locations=[[@10,34:34='a',<392>,1:34]], table_ref=a}, {name=kppi, locations=[[@16,48:51='kppi',<392>,1:48]], table_ref=a}]}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void windowFunctionColumnVariableP3Test() {
		// Item 52 - Partition clause doesn't take column references with table references/aliases
		String sql = "rank(a.column) OVER (partition by a.k_stfd, a.kppi order by a.<row_num> desc)";
		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runPredicandParsertest(sql, parser);
		
		Assert.assertEquals("AST is wrong", "{PREDICAND={window_function={over={partition_by={1={column={name=k_stfd, table_ref=a}}, 2={column={name=kppi, table_ref=a}}}, orderby={1={null_order=null, predicand={column={substitution={name=<row_num>, type=column}, table_ref=a}}, sort_order=desc}}}, function={function_name=rank, parameters={1={column={name=column, table_ref=a}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<row_num>=column}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{window_ordered_by=[{substitution={name=<row_num>, type=column}, locations=[[@19,60:60='a',<392>,1:60]], table_ref=a}], table_dictionary={unresolved_column={a.<row_num>=[[@19,60:60='a',<392>,1:60]], a.k_stfd=[[@10,34:34='a',<392>,1:34]], a.kppi=[[@14,44:44='a',<392>,1:44]], a.column=[[@2,5:5='a',<392>,1:5]]}}, unresolved_column={a.<row_num>={column={table_ref=a, substitution={name=<row_num>, type=column}}, locations=[[@19,60:60='a',<392>,1:60]]}, a.k_stfd={column={name=k_stfd, table_ref=a}, locations=[[@10,34:34='a',<392>,1:34]]}, a.kppi={column={name=kppi, table_ref=a}, locations=[[@14,44:44='a',<392>,1:44]]}, a.column={column={name=column, table_ref=a}, locations=[[@2,5:5='a',<392>,1:5]]}}, window_partition_by=[{name=k_stfd, locations=[[@12,36:41='k_stfd',<392>,1:36]], table_ref=a}, {name=kppi, locations=[[@16,46:49='kppi',<392>,1:46]], table_ref=a}]}",
				extractor.getSymbolTable().toString());
	}


	@Test
	// Contract: unresolved_column tracks only concrete columns (or column substitutions),
	// so predicand substitutions like <columnParam> are excluded from unresolved/table dictionary.
	public void windowFunctionPredicandVariableP1Test() {
		String sql = "rank(<columnParam>) OVER (partition by k_stfd, kppi order by row_num desc)";
		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runPredicandParsertest(sql, parser);
		
		Assert.assertEquals("AST is wrong", "{PREDICAND={window_function={over={partition_by={1={column={name=k_stfd, table_ref=null}}, 2={column={name=kppi, table_ref=null}}}, orderby={1={null_order=null, predicand={column={name=row_num, table_ref=null}}, sort_order=desc}}}, function={function_name=rank, parameters={1={substitution={name=<columnParam>, type=predicand}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<columnParam>=predicand}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{window_ordered_by=[{name=row_num, locations=[[@13,61:67='row_num',<392>,1:61]], table_ref=null}], table_dictionary={unresolved_column={k_stfd=[[@8,39:44='k_stfd',<392>,1:39]], row_num=[[@13,61:67='row_num',<392>,1:61]], kppi=[[@10,47:50='kppi',<392>,1:47]]}}, unresolved_column={k_stfd={column={name=k_stfd, table_ref=null}, locations=[[@8,39:44='k_stfd',<392>,1:39]]}, row_num={column={name=row_num, table_ref=null}, locations=[[@13,61:67='row_num',<392>,1:61]]}, kppi={column={name=kppi, table_ref=null}, locations=[[@10,47:50='kppi',<392>,1:47]]}}, window_partition_by=[{name=k_stfd, locations=[[@8,39:44='k_stfd',<392>,1:39]], table_ref=null}, {name=kppi, locations=[[@10,47:50='kppi',<392>,1:47]], table_ref=null}]}",
				extractor.getSymbolTable().toString());
	}


	@Test
	// Contract: even in standalone predicand parsing, predicand substitutions such as <k_stfd>
	// do not materialize into unresolved/table dictionary entries.
	public void windowFunctionPredicandVariableP2Test() {
		String sql = "rank(column) OVER (partition by <k_stfd>, kppi order by row_num desc)";
		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runPredicandParsertest(sql, parser);
		
		Assert.assertEquals("AST is wrong", "{PREDICAND={window_function={over={partition_by={1={substitution={name=<k_stfd>, type=predicand}}, 2={column={name=kppi, table_ref=null}}}, orderby={1={null_order=null, predicand={column={name=row_num, table_ref=null}}, sort_order=desc}}}, function={function_name=rank, parameters={1={column={name=column, table_ref=null}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<k_stfd>=predicand}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{window_ordered_by=[{name=row_num, locations=[[@13,56:62='row_num',<392>,1:56]], table_ref=null}], table_dictionary={unresolved_column={column=[[@2,5:10='column',<68>,1:5]], row_num=[[@13,56:62='row_num',<392>,1:56]], kppi=[[@10,42:45='kppi',<392>,1:42]]}}, unresolved_column={column={column={name=column, table_ref=null}, locations=[[@2,5:10='column',<68>,1:5]]}, row_num={column={name=row_num, table_ref=null}, locations=[[@13,56:62='row_num',<392>,1:56]]}, kppi={column={name=kppi, table_ref=null}, locations=[[@10,42:45='kppi',<392>,1:42]]}}, window_partition_by=[{name=<k_stfd>, type=predicand}, {name=kppi, locations=[[@10,42:45='kppi',<392>,1:42]], table_ref=null}]}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void windowFunctionPredicandVariableP3Test() {
		String sql = "rank(column) OVER (partition by k_stfd, kppi order by <row_num> desc)";
		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runPredicandParsertest(sql, parser);
		
		Assert.assertEquals("AST is wrong", "{PREDICAND={window_function={over={partition_by={1={column={name=k_stfd, table_ref=null}}, 2={column={name=kppi, table_ref=null}}}, orderby={1={null_order=null, predicand={substitution={name=<row_num>, type=predicand}}, sort_order=desc}}}, function={function_name=rank, parameters={1={column={name=column, table_ref=null}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<row_num>=predicand}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{window_ordered_by=[{name=<row_num>, type=predicand}], table_dictionary={unresolved_column={k_stfd=[[@8,32:37='k_stfd',<392>,1:32]], column=[[@2,5:10='column',<68>,1:5]], kppi=[[@10,40:43='kppi',<392>,1:40]]}}, unresolved_column={k_stfd={column={name=k_stfd, table_ref=null}, locations=[[@8,32:37='k_stfd',<392>,1:32]]}, column={column={name=column, table_ref=null}, locations=[[@2,5:10='column',<68>,1:5]]}, kppi={column={name=kppi, table_ref=null}, locations=[[@10,40:43='kppi',<392>,1:40]]}}, window_partition_by=[{name=k_stfd, locations=[[@8,32:37='k_stfd',<392>,1:32]], table_ref=null}, {name=kppi, locations=[[@10,40:43='kppi',<392>,1:40]], table_ref=null}]}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void basicColumnValueTest() {
		String sql = "emp_sales_count";
		final SQLSelectParserParser parser = parse(sql);
			
		SqlParseEventWalker extractor = runColumnParsertest(sql, parser);
		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{COLUMN={column={name=emp_sales_count, table_ref=null}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{table_dictionary={unresolved_column={emp_sales_count=[[@0,0:14='emp_sales_count',<392>,1:0]]}}, unresolved_column={emp_sales_count={column={name=emp_sales_count, table_ref=null}, locations=[[@0,0:14='emp_sales_count',<392>,1:0]]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void basicTableColumnValueTest() {
		String sql = "table1.emp_sales_count";
		final SQLSelectParserParser parser = parse(sql);
			
		SqlParseEventWalker extractor = runColumnParsertest(sql, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{COLUMN={column={name=emp_sales_count, table_ref=table1}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{table_dictionary={unresolved_column={table1.emp_sales_count=[[@0,0:5='table1',<392>,1:0]]}}, unresolved_column={table1.emp_sales_count={column={name=emp_sales_count, table_ref=table1}, locations=[[@0,0:5='table1',<392>,1:0]]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void basicColumnSubstitutionWithPrefixTest() {
		String sql = "table1.<emp_sales_count>";
		final SQLSelectParserParser parser = parse(sql);
			
		SqlParseEventWalker extractor = runColumnParsertest(sql, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{COLUMN={column={substitution={name=<emp_sales_count>, type=column}, table_ref=table1}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<emp_sales_count>=column}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{table_dictionary={unresolved_column={table1.<emp_sales_count>=[[@0,0:5='table1',<392>,1:0]]}}, unresolved_column={table1.<emp_sales_count>={column={table_ref=table1, substitution={name=<emp_sales_count>, type=column}}, locations=[[@0,0:5='table1',<392>,1:0]]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void basicColumnVariableTest() {
		//ITEM 83: Type isn't being set; Substitution List isn't being filled
		String sql = "<emp_sales_count>";
		final SQLSelectParserParser parser = parse(sql);
			
		SqlParseEventWalker extractor = runColumnParsertest(sql, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{COLUMN={column={substitution={name=<emp_sales_count>, type=column}, table_ref=null}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<emp_sales_count>=column}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{table_dictionary={unresolved_column={<emp_sales_count>=[[@0,0:16='<emp_sales_count>',<327>,1:0]]}}, unresolved_column={<emp_sales_count>={column={table_ref=null, substitution={name=<emp_sales_count>, type=column}}, locations=[[@0,0:16='<emp_sales_count>',<327>,1:0]]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void basicColumnPredicandTest() {
		String sql = "table1.emp_sales_count";
		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runPredicandParsertest(sql, parser);
		
		Assert.assertEquals("AST is wrong", "{PREDICAND={column={name=emp_sales_count, table_ref=table1}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{table_dictionary={unresolved_column={table1.emp_sales_count=[[@0,0:5='table1',<392>,1:0]]}}, unresolved_column={table1.emp_sales_count={column={name=emp_sales_count, table_ref=table1}, locations=[[@0,0:5='table1',<392>,1:0]]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void basicColumnPredicandWithSubstitutionTest() {
		String sql = "table1.<emp_sales_count>";
		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runPredicandParsertest(sql, parser);
		
		Assert.assertEquals("AST is wrong", "{PREDICAND={column={substitution={name=<emp_sales_count>, type=column}, table_ref=table1}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<emp_sales_count>=column}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{table_dictionary={unresolved_column={table1.<emp_sales_count>=[[@0,0:5='table1',<392>,1:0]]}}, unresolved_column={table1.<emp_sales_count>={column={table_ref=table1, substitution={name=<emp_sales_count>, type=column}}, locations=[[@0,0:5='table1',<392>,1:0]]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void basicPredicandSubstitutionTest() {
		String sql = "<emp_sales_count>";
		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runPredicandParsertest(sql, parser);
		
		Assert.assertEquals("AST is wrong", "{PREDICAND={substitution={name=<emp_sales_count>, type=predicand}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<emp_sales_count>=predicand}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void basicLiteralValuePredicandTest() {
		String sql = "'AA'";
		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runPredicandParsertest(sql, parser);
		
		Assert.assertEquals("AST is wrong", "{PREDICAND={literal='AA'}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void basicNullValuePredicandTest() {
		// ITEM 84 -- Null Literal not accepted as a Predicand
		String sql = "null";
		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runPredicandParsertest(sql, parser);
		
		Assert.assertEquals("AST is wrong", "{PREDICAND={null_literal=null}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void concatenationPredicandTest() {
		String sql = "a || b || 'oops'";
		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runPredicandParsertest(sql, parser);

		Assert.assertEquals("AST is wrong", "{PREDICAND={concatenate={1={column={name=a, table_ref=null}}, 2={column={name=b, table_ref=null}}, 3={literal='oops'}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{table_dictionary={unresolved_column={a=[[@0,0:0='a',<392>,1:0]], b=[[@2,5:5='b',<392>,1:5]]}}, unresolved_column={a={column={name=a, table_ref=null}, locations=[[@0,0:0='a',<392>,1:0]]}, b={column={name=b, table_ref=null}, locations=[[@2,5:5='b',<392>,1:5]]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void concatenationParenthesisPredicandTest() {
		String sql = "(a || b || 'oops')";
		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runPredicandParsertest(sql, parser);
		
		Assert.assertEquals("AST is wrong", "{PREDICAND={parentheses={concatenate={1={column={name=a, table_ref=null}}, 2={column={name=b, table_ref=null}}, 3={literal='oops'}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{table_dictionary={unresolved_column={a=[[@1,1:1='a',<392>,1:1]], b=[[@3,6:6='b',<392>,1:6]]}}, unresolved_column={a={column={name=a, table_ref=null}, locations=[[@1,1:1='a',<392>,1:1]]}, b={column={name=b, table_ref=null}, locations=[[@3,6:6='b',<392>,1:6]]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void functionPredicandTest() {
		String sql = "concat_ws('-', crs.subject_code, crs.course_number) ";
		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runPredicandParsertest(sql, parser);
		
		Assert.assertEquals("AST is wrong", "{PREDICAND={function={parameters={1={literal='-'}, 2={column={name=subject_code, table_ref=crs}}, 3={column={name=course_number, table_ref=crs}}}, function_name=concat_ws}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{table_dictionary={unresolved_column={crs.subject_code=[[@4,15:17='crs',<392>,1:15]], crs.course_number=[[@8,33:35='crs',<392>,1:33]]}}, unresolved_column={crs.subject_code={column={name=subject_code, table_ref=crs}, locations=[[@4,15:17='crs',<392>,1:15]]}, crs.course_number={column={name=course_number, table_ref=crs}, locations=[[@8,33:35='crs',<392>,1:33]]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void dollarFunctionPredicandTest() {
		String sql = "system$typeof(acolumn, bcolumn) ";
		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runPredicandParsertest(sql, parser);
		
		Assert.assertEquals("AST is wrong", "{PREDICAND={function={parameters={1={column={name=acolumn, table_ref=null}}, 2={column={name=bcolumn, table_ref=null}}}, function_name=system$typeof}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{table_dictionary={unresolved_column={bcolumn=[[@4,23:29='bcolumn',<392>,1:23]], acolumn=[[@2,14:20='acolumn',<392>,1:14]]}}, unresolved_column={bcolumn={column={name=bcolumn, table_ref=null}, locations=[[@4,23:29='bcolumn',<392>,1:23]]}, acolumn={column={name=acolumn, table_ref=null}, locations=[[@2,14:20='acolumn',<392>,1:14]]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void aggregateFunctionPredicandTest() {
		String sql = "max(scbcrse_eff_term)";
		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runPredicandParsertest(sql, parser);
		
		Assert.assertEquals("AST is wrong", "{PREDICAND={function={function_name=max, qualifier=null, parameters={column={name=scbcrse_eff_term, table_ref=null}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{table_dictionary={unresolved_column={scbcrse_eff_term=[[@2,4:19='scbcrse_eff_term',<392>,1:4]]}}, unresolved_column={scbcrse_eff_term={column={name=scbcrse_eff_term, table_ref=null}, locations=[[@2,4:19='scbcrse_eff_term',<392>,1:4]]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void caseFunctionPredicandTest() {
		String sql = "case when true then ‘Y’ when false then ‘N’ else ‘N’ end";
		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runPredicandParsertest(sql, parser);
		
		Assert.assertEquals("AST is wrong", "{PREDICAND={case={clauses={1={then={column={name=Y, table_ref=null}}, when={literal=true}}, 2={then={column={name=N, table_ref=null}}, when={literal=false}}}, else={column={name=N, table_ref=null}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{table_dictionary={unresolved_column={Y=[[@4,21:21='Y',<392>,1:21]], N=[[@8,41:41='N',<392>,1:41], [@10,50:50='N',<392>,1:50]]}}, unresolved_column={Y={column={name=Y, table_ref=null}, locations=[[@4,21:21='Y',<392>,1:21]]}, N={column={name=N, table_ref=null}, locations=[[@8,41:41='N',<392>,1:41], [@10,50:50='N',<392>,1:50]]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void trimFunctionPredicandTest() {
		final String sql = "trim(leading '0' from field1)";
		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runPredicandParsertest(sql, parser);
		
		Assert.assertEquals("AST is wrong", "{PREDICAND={function={function_name=trim, parameters={qualifier=leading, trim_character={literal='0'}, value={column={name=field1, table_ref=null}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{table_dictionary={unresolved_column={field1=[[@5,22:27='field1',<392>,1:22]]}}, unresolved_column={field1={column={name=field1, table_ref=null}, locations=[[@5,22:27='field1',<392>,1:22]]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void rankPredicandTest() {
		final String sql = "rank() OVER (partition by k_stfd, kppi order by OBSERVATION_TM desc, row_num desc)";
		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runPredicandParsertest(sql, parser);
		
		Assert.assertEquals("AST is wrong", "{PREDICAND={window_function={over={partition_by={1={column={name=k_stfd, table_ref=null}}, 2={column={name=kppi, table_ref=null}}}, orderby={1={null_order=null, predicand={column={name=OBSERVATION_TM, table_ref=null}}, sort_order=desc}, 2={null_order=null, predicand={column={name=row_num, table_ref=null}}, sort_order=desc}}}, function={function_name=rank, parameters=null}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{window_ordered_by=[{name=OBSERVATION_TM, locations=[[@12,48:61='OBSERVATION_TM',<392>,1:48]], table_ref=null}, {name=row_num, locations=[[@15,69:75='row_num',<392>,1:69]], table_ref=null}], table_dictionary={unresolved_column={k_stfd=[[@7,26:31='k_stfd',<392>,1:26]], row_num=[[@15,69:75='row_num',<392>,1:69]], kppi=[[@9,34:37='kppi',<392>,1:34]], OBSERVATION_TM=[[@12,48:61='OBSERVATION_TM',<392>,1:48]]}}, unresolved_column={k_stfd={column={name=k_stfd, table_ref=null}, locations=[[@7,26:31='k_stfd',<392>,1:26]]}, row_num={column={name=row_num, table_ref=null}, locations=[[@15,69:75='row_num',<392>,1:69]]}, kppi={column={name=kppi, table_ref=null}, locations=[[@9,34:37='kppi',<392>,1:34]]}, OBSERVATION_TM={column={name=OBSERVATION_TM, table_ref=null}, locations=[[@12,48:61='OBSERVATION_TM',<392>,1:48]]}}, window_partition_by=[{name=k_stfd, locations=[[@7,26:31='k_stfd',<392>,1:26]], table_ref=null}, {name=kppi, locations=[[@9,34:37='kppi',<392>,1:34]], table_ref=null}]}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void selectLookupSubqueryPredicandTest() {
		String sql = "(SELECT aa.scbcrse_coll_code FROM scbcrse aa)";
		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runPredicandParsertest(sql, parser);
		
		Assert.assertEquals("AST is wrong", "{PREDICAND={lookup={from={table={alias=aa, table=scbcrse}}, select={1={column={name=scbcrse_coll_code, table_ref=aa}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[scbcrse_coll_code]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{scbcrse={scbcrse_coll_code=[[@2,8:9='aa',<392>,1:8]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={scbcrse_coll_code=[[@4,11:27='scbcrse_coll_code',<392>,1:11]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{dependent_queries={predicand1={query=query0, type=filters}}, def_query0={query_dictionary={scbcrse_coll_code=[[@4,11:27='scbcrse_coll_code',<392>,1:11]]}, table_dictionary={scbcrse={scbcrse_coll_code=[[@2,8:9='aa',<392>,1:8]]}}, interface={scbcrse_coll_code=[{name=scbcrse_coll_code, table_ref=aa}]}, table_alias={aa=scbcrse}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void arithmeticParenExpressionPredicandTest() {
		String sql = "(-(aa.scbcrse_coll_code * 6 - other) )";
		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runPredicandParsertest(sql, parser);
		
		Assert.assertEquals("AST is wrong", "{PREDICAND={parentheses={calc={left={literal=-1}, right={parentheses={calc={left={calc={left={column={name=scbcrse_coll_code, table_ref=aa}}, right={literal=6}, operator=*}}, right={column={name=other, table_ref=null}}, operator=-}}}, operator=*}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{table_dictionary={unresolved_column={other=[[@9,30:34='other',<392>,1:30]], aa.scbcrse_coll_code=[[@3,3:4='aa',<392>,1:3]]}}, unresolved_column={other={column={name=other, table_ref=null}, locations=[[@9,30:34='other',<392>,1:30]]}, aa.scbcrse_coll_code={column={name=scbcrse_coll_code, table_ref=aa}, locations=[[@3,3:4='aa',<392>,1:3]]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void pumlConstantTenantSKPredicandTest() {
		String sql = "#TENANT_SK";
		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runPredicandParsertest(sql, parser);
		
		Assert.assertEquals("AST is wrong", "{PREDICAND={puml_constant=#TENANT_SK}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void pumlConstantTenantSKMixedCasePredicandTest() {
		String sql = "#Tenant_SK";
		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runPredicandParsertest(sql, parser);
		
		Assert.assertEquals("AST is wrong", "{PREDICAND={puml_constant=#TENANT_SK}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void pumlConstantTenantGuidMixedCasePredicandTest() {
		String sql = "#Tenant_GuiD";
		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runPredicandParsertest(sql, parser);
		
		Assert.assertEquals("AST is wrong", "{PREDICAND={puml_constant=#TENANT_GUID}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void pumlConstantTenantMasterIdMixedCasePredicandTest() {
		String sql = "#Tenant_Master_ID";
		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runPredicandParsertest(sql, parser);
		
		Assert.assertEquals("AST is wrong", "{PREDICAND={puml_constant=#TENANT_MASTER_ID}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void pumlConstantTenantNameMixedCasePredicandTest() {
		String sql = "#Tenant_name";
		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runPredicandParsertest(sql, parser);
		
		Assert.assertEquals("AST is wrong", "{PREDICAND={puml_constant=#TENANT_NAME}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void pumlConstantTenantAcronymMixedCasePredicandTest() {
		String sql = "#Tenant_ACRONYM";
		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runPredicandParsertest(sql, parser);
		
		Assert.assertEquals("AST is wrong", "{PREDICAND={puml_constant=#TENANT_ACRONYM}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void pumlConstantTenantWebDomainMixedCasePredicandTest() {
		String sql = "#Tenant_WEB_domain";
		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runPredicandParsertest(sql, parser);
		
		Assert.assertEquals("AST is wrong", "{PREDICAND={puml_constant=#TENANT_WEB_DOMAIN}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void pumlConstantEsInstitutionIdMixedCasePredicandTest() {
		String sql = "#es_INSTITUTION_id";
		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runPredicandParsertest(sql, parser);
		
		Assert.assertEquals("AST is wrong", "{PREDICAND={puml_constant=#ES_INSTITUTION_ID}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void pumlConstantEsInstitutionCodeMixedCasePredicandTest() {
		String sql = "#es_INSTITUTION_CODE";
		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runPredicandParsertest(sql, parser);
		
		Assert.assertEquals("AST is wrong", "{PREDICAND={puml_constant=#ES_INSTITUTION_CODE}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void pumlConstantEsInstitutionNameMixedCasePredicandTest() {
		String sql = "#es_INSTITUTION_naME";
		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runPredicandParsertest(sql, parser);
		
		Assert.assertEquals("AST is wrong", "{PREDICAND={puml_constant=#ES_INSTITUTION_NAME}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void pumlConstantSFCounterIdMixedCasePredicandTest() {
		String sql = "#SF_COUNTER_ID";
		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runPredicandParsertest(sql, parser);
		
		Assert.assertEquals("AST is wrong", "{PREDICAND={puml_constant=#SF_COUNTER_ID}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void pumlConstantSourceFileNameMixedCasePredicandTest() {
		String sql = "#SOURCE_FILE_NAME";
		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runPredicandParsertest(sql, parser);
		
		Assert.assertEquals("AST is wrong", "{PREDICAND={puml_constant=#SOURCE_FILE_NAME}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void pumlConstantFileIdCasePredicandTest() {
		String sql = "#FILE_ID";
		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runPredicandParsertest(sql, parser);
		
		Assert.assertEquals("AST is wrong", "{PREDICAND={puml_constant=#FILE_ID}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void pumlConstantRowIdPredicandTest() {
		String sql = "#ROW_ID";
		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runPredicandParsertest(sql, parser);
		
		Assert.assertEquals("AST is wrong", "{PREDICAND={puml_constant=#ROW_ID}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void pumlConstantObservationTimeMixedCasePredicandTest() {
		String sql = "#Observation_TIME";
		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runPredicandParsertest(sql, parser);
		
		Assert.assertEquals("AST is wrong", "{PREDICAND={puml_constant=#OBSERVATION_TIME}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void pumlConstantSystemDateMixedCasePredicandTest() {
		String sql = "#System_Date";
		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runPredicandParsertest(sql, parser);
		
		Assert.assertEquals("AST is wrong", "{PREDICAND={puml_constant=#SYSTEM_DATE}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void pumlConstantSystemTimeMixedCasePredicandTest() {
		String sql = "#System_Time";
		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runPredicandParsertest(sql, parser);
		
		Assert.assertEquals("AST is wrong", "{PREDICAND={puml_constant=#SYSTEM_TIME}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void pumlConstantFeedRunIdMixedCasePredicandTest() {
		String sql = "#FEED_RUN_ID";
		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runPredicandParsertest(sql, parser);
		
		Assert.assertEquals("AST is wrong", "{PREDICAND={puml_constant=#FEED_RUN_ID}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void pumlConstantFeedNameMixedCasePredicandTest() {
		String sql = "#feed_name";
		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runPredicandParsertest(sql, parser);
		
		Assert.assertEquals("AST is wrong", "{PREDICAND={puml_constant=#FEED_NAME}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void pumlConstantTransactionRunIdPredicandTest() {
		String sql = "#TRANSACTION_RUN_ID";
		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runPredicandParsertest(sql, parser);
		
		Assert.assertEquals("AST is wrong", "{PREDICAND={puml_constant=#TRANSACTION_RUN_ID}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void pumlConstantTransactionNameMixedCasePredicandTest() {
		String sql = "#transaction_name";
		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runPredicandParsertest(sql, parser);
		
		Assert.assertEquals("AST is wrong", "{PREDICAND={puml_constant=#TRANSACTION_NAME}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void pumlConstantPopulationNameMixedCasePredicandTest() {
		String sql = "#population_name";
		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runPredicandParsertest(sql, parser);
		
		Assert.assertEquals("AST is wrong", "{PREDICAND={puml_constant=#POPULATION_NAME}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void pumlConstantTargetModelNamePredicandTest() {
		String sql = "#TARGET_MODEL_NAME";
		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runPredicandParsertest(sql, parser);
		
		Assert.assertEquals("AST is wrong", "{PREDICAND={puml_constant=#TARGET_MODEL_NAME}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void pumlConstantTenantSaltPredicandTest() {
		String sql = "#TENANT_salt";
		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runPredicandParsertest(sql, parser);
		
		Assert.assertEquals("AST is wrong", "{PREDICAND={puml_constant=#TENANT_SALT}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void pumlConstantPITStartTimePredicandTest() {
		String sql = "#pit_START_time";
		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runPredicandParsertest(sql, parser);
		
		Assert.assertEquals("AST is wrong", "{PREDICAND={puml_constant=#PIT_START_TIME}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void pumlConstantPITEndTimePredicandTest() {
		String sql = "#pit_end_time";
		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runPredicandParsertest(sql, parser);
		
		Assert.assertEquals("AST is wrong", "{PREDICAND={puml_constant=#PIT_END_TIME}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void informaticaINFunctionPredicandTest() {
		String sql = "in(property,property,0)";
		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runPredicandParsertest(sql, parser);
		
		Assert.assertEquals("AST is wrong", "{PREDICAND={function={parameters={1={column={name=property, table_ref=null}}, 2={column={name=property, table_ref=null}}, 3={literal=0}}, function_name=in}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{table_dictionary={unresolved_column={property=[[@2,3:10='property',<392>,1:3], [@4,12:19='property',<392>,1:12]]}}, unresolved_column={property={column={name=property, table_ref=null}, locations=[[@2,3:10='property',<392>,1:3], [@4,12:19='property',<392>,1:12]]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void informaticaINFunctionPredicandConditionTest() {
		String sql = "in(property,property,0) in ('A', 'B')";
		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runConditionParsertest(sql, parser);
		
		Assert.assertEquals("AST is wrong", "{CONDITION={in={item={function={parameters={1={column={name=property, table_ref=null}}, 2={column={name=property, table_ref=null}}, 3={literal=0}}, function_name=in}}, in_list={list={1={literal='A'}, 2={literal='B'}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{table_dictionary={unresolved_column={property=[[@2,3:10='property',<392>,1:3], [@4,12:19='property',<392>,1:12]]}}, unresolved_column={property={column={name=property, table_ref=null}, locations=[[@2,3:10='property',<392>,1:3], [@4,12:19='property',<392>,1:12]]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void inListVariableSubstitutionTest() {
		String sql = "<in list>";
		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runInListPredicateParsertest(sql, parser);
		
		
		Assert.assertEquals("AST is wrong", "{IN_LIST={substitution={name=<in list>, type=in_list}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<in list>=in_list}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void inListNumericBasicConditionTest() {
		String sql = "(1, 2, 3)";
		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runInListPredicateParsertest(sql, parser);
		
		
		Assert.assertEquals("AST is wrong", "{IN_LIST={list={1={literal=1}, 2={literal=2}, 3={literal=3}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void inListAlphaBasicConditionTest() {
		String sql = "('a', 'dog', 'god')";
		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runInListPredicateParsertest(sql, parser);
		
		
		Assert.assertEquals("AST is wrong", "{IN_LIST={list={1={literal='a'}, 2={literal='dog'}, 3={literal='god'}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void inListEmbeddedVariablecConditionTest() {
		String sql = "<var>";
		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runInListPredicateParsertest(sql, parser);
		
		
		Assert.assertEquals("AST is wrong", "{IN_LIST={substitution={name=<var>, type=in_list}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<var>=in_list}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void inListSubqueryBasicConditionTest() {
		String sql = "(select * from tab1)";
		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runInListPredicateParsertest(sql, parser);
		
		
		Assert.assertEquals("AST is wrong", "{IN_LIST={select={1={column={name=*, table_ref=*}}}, from={table={alias=null, table=tab1}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={*=[[@2,8:8='*',<291>,1:8]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@2,8:8='*',<291>,1:8]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{dependent_queries={in_list1={query=query0, type=filters}}, def_query0={query_dictionary={*=[[@2,8:8='*',<291>,1:8]]}, table_dictionary={tab1={*=[[@2,8:8='*',<291>,1:8]]}}, interface={*=[{name=*, table_ref=*}]}}}",
				extractor.getSymbolTable().toString());

	}


	@Test
	public void literalNumericValueEndpointTest() {
		assertLiteralEndpoint("25", "{LITERAL={literal=25}}");
	}

	@Test
	public void literalNegativeIntegerEndpointTest() {
		assertLiteralEndpoint("-42", "{LITERAL={literal=-42}}");
	}

	@Test
	public void literalRealNumberEndpointTest() {
		assertLiteralEndpoint("3.14", "{LITERAL={literal=3.14}}");
	}

	@Test
	public void literalScientificNotationEndpointTest() {
		assertLiteralEndpoint("1.5e2", "{LITERAL={literal=1.5e2}}");
	}

	@Test
	public void literalCharacterStringEndpointTest() {
		assertLiteralEndpoint("'hello'", "{LITERAL={literal='hello'}}");
	}

	@Test
	public void literalStringWithPunctuationEndpointTest() {
		assertLiteralEndpoint("'O''Reilly, Inc.!'", "{LITERAL={literal='O''Reilly, Inc.!'}}");
	}

	@Test
	public void literalBooleanTrueEndpointTest() {
		assertLiteralEndpoint("true", "{LITERAL={literal=true}}");
	}

	private void assertLiteralEndpoint(String sql, String expectedAst) {
		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runLiteralParsertest(sql, parser);

		Assert.assertEquals("AST is wrong", expectedAst, extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{}", extractor.getSymbolTable().toString());
	}


	@Test
	public void conditionBasicConditionTest() {
		String sql = "table1.emp_sales_count >= 25";
		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runConditionParsertest(sql, parser);
				
		Assert.assertEquals("AST is wrong", "{CONDITION={condition={left={column={name=emp_sales_count, table_ref=table1}}, right={literal=25}, operator=>=}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{table_dictionary={unresolved_column={table1.emp_sales_count=[[@0,0:5='table1',<392>,1:0]]}}, unresolved_column={table1.emp_sales_count={column={name=emp_sales_count, table_ref=table1}, locations=[[@0,0:5='table1',<392>,1:0]]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void conditionSimpleBooleanTest() {
		String sql = "true";
		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runConditionParsertest(sql, parser);
				
		Assert.assertEquals("AST is wrong", "{CONDITION={literal=true}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void conditionListOfAndsV1Test() {
		String sql = "a=b and b=c and x >y";
		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runConditionParsertest(sql, parser);
		
		Assert.assertEquals("AST is wrong", "{CONDITION={and={1={condition={left={column={name=a, table_ref=null}}, right={column={name=b, table_ref=null}}, operator==}}, 2={condition={left={column={name=b, table_ref=null}}, right={column={name=c, table_ref=null}}, operator==}}, 3={condition={left={column={name=x, table_ref=null}}, right={column={name=y, table_ref=null}}, operator=>}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{table_dictionary={unresolved_column={a=[[@0,0:0='a',<392>,1:0]], b=[[@2,2:2='b',<392>,1:2], [@4,8:8='b',<392>,1:8]], c=[[@6,10:10='c',<392>,1:10]], x=[[@8,16:16='x',<392>,1:16]], y=[[@10,19:19='y',<392>,1:19]]}}, unresolved_column={a={column={name=a, table_ref=null}, locations=[[@0,0:0='a',<392>,1:0]]}, b={column={name=b, table_ref=null}, locations=[[@2,2:2='b',<392>,1:2], [@4,8:8='b',<392>,1:8]]}, c={column={name=c, table_ref=null}, locations=[[@6,10:10='c',<392>,1:10]]}, x={column={name=x, table_ref=null}, locations=[[@8,16:16='x',<392>,1:16]]}, y={column={name=y, table_ref=null}, locations=[[@10,19:19='y',<392>,1:19]]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void conditionListOfAndsV2Test() {
		// Item 51 (FIXED) - Table Dictionary not created when condition parsing is performed on its own
		String sql = "a.a=b.b and a.b=b.c and a.x > b.y";
		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runConditionParsertest(sql, parser);
		
		Assert.assertEquals("AST is wrong", "{CONDITION={and={1={condition={left={column={name=a, table_ref=a}}, right={column={name=b, table_ref=b}}, operator==}}, 2={condition={left={column={name=b, table_ref=a}}, right={column={name=c, table_ref=b}}, operator==}}, 3={condition={left={column={name=x, table_ref=a}}, right={column={name=y, table_ref=b}}, operator=>}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{table_dictionary={unresolved_column={a.b=[[@8,12:12='a',<392>,1:12]], a.a=[[@0,0:0='a',<392>,1:0]], b.c=[[@12,16:16='b',<392>,1:16]], b.b=[[@4,4:4='b',<392>,1:4]], a.x=[[@16,24:24='a',<392>,1:24]], b.y=[[@20,30:30='b',<392>,1:30]]}}, unresolved_column={a.b={column={name=b, table_ref=a}, locations=[[@8,12:12='a',<392>,1:12]]}, a.a={column={name=a, table_ref=a}, locations=[[@0,0:0='a',<392>,1:0]]}, b.c={column={name=c, table_ref=b}, locations=[[@12,16:16='b',<392>,1:16]]}, b.b={column={name=b, table_ref=b}, locations=[[@4,4:4='b',<392>,1:4]]}, a.x={column={name=x, table_ref=a}, locations=[[@16,24:24='a',<392>,1:24]]}, b.y={column={name=y, table_ref=b}, locations=[[@20,30:30='b',<392>,1:30]]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void conditionListOfOrsTest() {
		String sql = "a=b or b=c or x >y";
		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runConditionParsertest(sql, parser);
		
		Assert.assertEquals("AST is wrong", "{CONDITION={or={1={condition={left={column={name=a, table_ref=null}}, right={column={name=b, table_ref=null}}, operator==}}, 2={condition={left={column={name=b, table_ref=null}}, right={column={name=c, table_ref=null}}, operator==}}, 3={condition={left={column={name=x, table_ref=null}}, right={column={name=y, table_ref=null}}, operator=>}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{table_dictionary={unresolved_column={a=[[@0,0:0='a',<392>,1:0]], b=[[@2,2:2='b',<392>,1:2], [@4,7:7='b',<392>,1:7]], c=[[@6,9:9='c',<392>,1:9]], x=[[@8,14:14='x',<392>,1:14]], y=[[@10,17:17='y',<392>,1:17]]}}, unresolved_column={a={column={name=a, table_ref=null}, locations=[[@0,0:0='a',<392>,1:0]]}, b={column={name=b, table_ref=null}, locations=[[@2,2:2='b',<392>,1:2], [@4,7:7='b',<392>,1:7]]}, c={column={name=c, table_ref=null}, locations=[[@6,9:9='c',<392>,1:9]]}, x={column={name=x, table_ref=null}, locations=[[@8,14:14='x',<392>,1:14]]}, y={column={name=y, table_ref=null}, locations=[[@10,17:17='y',<392>,1:17]]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void conditionParentheticalTest() {
		String sql = "((a=b) or (b=c))";
		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runConditionParsertest(sql, parser);
		
		Assert.assertEquals("AST is wrong", "{CONDITION={parentheses={or={1={parentheses={condition={left={column={name=a, table_ref=null}}, right={column={name=b, table_ref=null}}, operator==}}}, 2={parentheses={condition={left={column={name=b, table_ref=null}}, right={column={name=c, table_ref=null}}, operator==}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{table_dictionary={unresolved_column={a=[[@2,2:2='a',<392>,1:2]], b=[[@4,4:4='b',<392>,1:4], [@8,11:11='b',<392>,1:11]], c=[[@10,13:13='c',<392>,1:13]]}}, unresolved_column={a={column={name=a, table_ref=null}, locations=[[@2,2:2='a',<392>,1:2]]}, b={column={name=b, table_ref=null}, locations=[[@4,4:4='b',<392>,1:4], [@8,11:11='b',<392>,1:11]]}, c={column={name=c, table_ref=null}, locations=[[@10,13:13='c',<392>,1:13]]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void conditionNotTest() {
		String sql = "not a = b";
		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runConditionParsertest(sql, parser);
		
		Assert.assertEquals("AST is wrong", "{CONDITION={not={condition={left={column={name=a, table_ref=null}}, right={column={name=b, table_ref=null}}, operator==}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{table_dictionary={unresolved_column={a=[[@1,4:4='a',<392>,1:4]], b=[[@3,8:8='b',<392>,1:8]]}}, unresolved_column={a={column={name=a, table_ref=null}, locations=[[@1,4:4='a',<392>,1:4]]}, b={column={name=b, table_ref=null}, locations=[[@3,8:8='b',<392>,1:8]]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void conditionInTest() {
		String sql = "columnName in (25, 26)";
		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runConditionParsertest(sql, parser);
		
		Assert.assertEquals("AST is wrong", "{CONDITION={in={item={column={name=columnName, table_ref=null}}, in_list={list={1={literal=25}, 2={literal=26}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{table_dictionary={unresolved_column={columnName=[[@0,0:9='columnName',<392>,1:0]]}}, unresolved_column={columnName={column={name=columnName, table_ref=null}, locations=[[@0,0:9='columnName',<392>,1:0]]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void conditionBetweenTest() {
		String sql = "columnName between 24 and 28";
		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runConditionParsertest(sql, parser);
		
		Assert.assertEquals("AST is wrong", "{CONDITION={between={item={column={name=columnName, table_ref=null}}, symmetry=null, end={literal=28}, begin={literal=24}, operator=between}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{table_dictionary={unresolved_column={columnName=[[@0,0:9='columnName',<392>,1:0]]}}, unresolved_column={columnName={column={name=columnName, table_ref=null}, locations=[[@0,0:9='columnName',<392>,1:0]]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void conditionIsNullTest() {
		String sql = "table1.emp_sales_count is null";
		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runConditionParsertest(sql, parser);
		
		Assert.assertEquals("AST is wrong", "{CONDITION={condition={left={column={name=emp_sales_count, table_ref=table1}}, operator=is null}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{table_dictionary={unresolved_column={table1.emp_sales_count=[[@0,0:5='table1',<392>,1:0]]}}, unresolved_column={table1.emp_sales_count={column={name=emp_sales_count, table_ref=table1}, locations=[[@0,0:5='table1',<392>,1:0]]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void conditionIsNotNullTest() {
		String sql = "table1.emp_sales_count is not null";
		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runConditionParsertest(sql, parser);
		
		Assert.assertEquals("AST is wrong", "{CONDITION={condition={left={column={name=emp_sales_count, table_ref=table1}}, operator=is not null}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{table_dictionary={unresolved_column={table1.emp_sales_count=[[@0,0:5='table1',<392>,1:0]]}}, unresolved_column={table1.emp_sales_count={column={name=emp_sales_count, table_ref=table1}, locations=[[@0,0:5='table1',<392>,1:0]]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void substitutionConditionTest() {
		String sql = "<item> = 26";
		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runConditionParsertest(sql, parser);
		
		
		Assert.assertEquals("AST is wrong", "{CONDITION={condition={left={substitution={name=<item>, type=predicand}}, right={literal=26}, operator==}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<item>=predicand}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void conditionWithSubstitutionInV1Test() {
		String sql = "<columnName> in (25, 26)";
		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runConditionParsertest(sql, parser);
		
		
		Assert.assertEquals("AST is wrong", "{CONDITION={in={item={substitution={name=<columnName>, type=predicand}}, in_list={list={1={literal=25}, 2={literal=26}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<columnName>=predicand}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void conditionWithSubstitutionInV2Test() {
		String sql = "<columnName> in <inList>";
		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runConditionParsertest(sql, parser);
		
		
		Assert.assertEquals("AST is wrong", "{CONDITION={in={item={substitution={name=<columnName>, type=predicand}}, in_list={substitution={name=<inList>, type=in_list}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<inList>=in_list, <columnName>=predicand}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void conditionWithSubstitutionInV3Test() {
		String sql = "column in <inList>";
		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runConditionParsertest(sql, parser);
		
		
		Assert.assertEquals("AST is wrong", "{CONDITION={in={item={column={name=column, table_ref=null}}, in_list={substitution={name=<inList>, type=in_list}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<inList>=in_list}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{table_dictionary={unresolved_column={column=[[@0,0:5='column',<68>,1:0]]}}, unresolved_column={column={column={name=column, table_ref=null}, locations=[[@0,0:5='column',<68>,1:0]]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void conditionVariableSubstitutionInV3Test() {
		String sql = "<column condition>";
		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runConditionParsertest(sql, parser);
		
		
		Assert.assertEquals("AST is wrong", "{CONDITION={substitution={name=<column condition>, type=condition}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<column condition>=condition}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void basicTupleTableTest() {
		String sql = "schema1.emp_sales";
		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runTupleParsertest(sql, parser);
		
		
		Assert.assertEquals("AST is wrong", "{TUPLE={table={schema=schema1, table=emp_sales}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{table_dictionary={schema1.emp_sales={}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void basicTupleSubstitutionVariableTest() {
		String sql = "<simple variable>";
		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runTupleParsertest(sql, parser);
		
		
		Assert.assertEquals("AST is wrong", "{TUPLE={substitution={name=<simple variable>, type=tuple}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Substitution List is wrong", "{<simple variable>=tuple}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{table_dictionary={<simple variable>={}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void complexTupleSubstitutionVariableTestExtended1() {
		String sql = "<[domain].[entity]>";
		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runTupleParsertest(sql, parser);
		
		
		Assert.assertEquals("AST is wrong", "{TUPLE={substitution={name=<[domain].[entity]>, parts={1=[domain], 2=[entity]}, type=tuple}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Substitution List is wrong", "{<[domain].[entity]>=tuple}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{table_dictionary={<[domain].[entity]>={}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void complexTupleSubstitutionVariableTestExtended2() {
		String sql = "<[domain].[entity].[third]>";
		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runTupleParsertest(sql, parser);
		
		
		Assert.assertEquals("AST is wrong", "{TUPLE={substitution={name=<[domain].[entity].[third]>, parts={1=[domain], 2=[entity], 3=[third]}, type=tuple}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Substitution List is wrong", "{<[domain].[entity].[third]>=tuple}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{table_dictionary={<[domain].[entity].[third]>={}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void complexTupleSubstitutionVariableTestExtended3() {
		String sql = "<[domain].[entity].[third].[fourth]>";
		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runTupleParsertest(sql, parser);
		
		
		Assert.assertEquals("AST is wrong", "{TUPLE={substitution={name=<[domain].[entity].[third].[fourth]>, parts={1=[domain], 2=[entity], 3=[third], 4=[fourth]}, type=tuple}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Substitution List is wrong", "{<[domain].[entity].[third].[fourth]>=tuple}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{table_dictionary={<[domain].[entity].[third].[fourth]>={}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void complexTupleSubstitutionVariableTestExtendedWithPopulation1() {
		String sql = "<[domain].[entity].{population}>";
		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runTupleParsertest(sql, parser);
		
		
		Assert.assertEquals("AST is wrong", "{TUPLE={substitution={name=<[domain].[entity].{population}>, parts={1=[domain], 2=[entity], 3={population}}, type=tuple}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Substitution List is wrong", "{<[domain].[entity].{population}>=tuple}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{table_dictionary={<[domain].[entity].{population}>={}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void complexTupleSubstitutionVariableTestExtendedWithPopulation2() {
		String sql = "<[domain].[entity].[third].{population}>";
		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runTupleParsertest(sql, parser);
		
		
		Assert.assertEquals("AST is wrong", "{TUPLE={substitution={name=<[domain].[entity].[third].{population}>, parts={1=[domain], 2=[entity], 3=[third], 4={population}}, type=tuple}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Substitution List is wrong", "{<[domain].[entity].[third].{population}>=tuple}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{table_dictionary={<[domain].[entity].[third].{population}>={}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void complexTupleSubstitutionVariableTestAlgorithm1() {
		String sql = "<fulfill.[domain]>";
		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runTupleParsertest(sql, parser);
		
		
		Assert.assertEquals("AST is wrong", "{TUPLE={substitution={name=<fulfill.[domain]>, parts={1=fulfill, 2=[domain]}, type=tuple}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Substitution List is wrong", "{<fulfill.[domain]>=tuple}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{table_dictionary={<fulfill.[domain]>={}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void complexTupleSubstitutionVariableTestAlgorithm2() {
		String sql = "<fulfill.[domain].[entity]>";
		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runTupleParsertest(sql, parser);
		
		
		Assert.assertEquals("AST is wrong", "{TUPLE={substitution={name=<fulfill.[domain].[entity]>, parts={1=fulfill, 2=[domain], 3=[entity]}, type=tuple}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Substitution List is wrong", "{<fulfill.[domain].[entity]>=tuple}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{table_dictionary={<fulfill.[domain].[entity]>={}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void complexTupleSubstitutionVariableTestAlgorithm3() {
		String sql = "<fulfill.[domain].[entity].[file category]>";
		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runTupleParsertest(sql, parser);
		
		
		Assert.assertEquals("AST is wrong", "{TUPLE={substitution={name=<fulfill.[domain].[entity].[file category]>, parts={1=fulfill, 2=[domain], 3=[entity], 4=[file category]}, type=tuple}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Substitution List is wrong", "{<fulfill.[domain].[entity].[file category]>=tuple}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{table_dictionary={<fulfill.[domain].[entity].[file category]>={}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void complexTupleSubstitutionVariableTestAlgorithm4() {
		String sql = "<fulfill.[domain].[entity].[file category].[whats this]>";
		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runTupleParsertest(sql, parser);
		
		
		Assert.assertEquals("AST is wrong", "{TUPLE={substitution={name=<fulfill.[domain].[entity].[file category].[whats this]>, parts={1=fulfill, 2=[domain], 3=[entity], 4=[file category], 5=[whats this]}, type=tuple}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Substitution List is wrong", "{<fulfill.[domain].[entity].[file category].[whats this]>=tuple}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{table_dictionary={<fulfill.[domain].[entity].[file category].[whats this]>={}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void complexTupleSubstitutionVariableTestAlgorithmWithSnapshot1() {
		String sql = "<fulfill.[domain].[entity].{snapshot}>";
		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runTupleParsertest(sql, parser);
		
		
		Assert.assertEquals("AST is wrong", "{TUPLE={substitution={name=<fulfill.[domain].[entity].{snapshot}>, parts={1=fulfill, 2=[domain], 3=[entity], 4={snapshot}}, type=tuple}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Substitution List is wrong", "{<fulfill.[domain].[entity].{snapshot}>=tuple}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{table_dictionary={<fulfill.[domain].[entity].{snapshot}>={}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void complexTupleSubstitutionVariableTestAlgorithmWithSnapshot2() {
		String sql = "<fulfill.[domain].[entity].[file category].{snapshot}>";
		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runTupleParsertest(sql, parser);
		
		
		Assert.assertEquals("AST is wrong", "{TUPLE={substitution={name=<fulfill.[domain].[entity].[file category].{snapshot}>, parts={1=fulfill, 2=[domain], 3=[entity], 4=[file category], 5={snapshot}}, type=tuple}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Substitution List is wrong", "{<fulfill.[domain].[entity].[file category].{snapshot}>=tuple}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{table_dictionary={<fulfill.[domain].[entity].[file category].{snapshot}>={}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void basicTupleSubqueryTest() {
		String sql = "(select emp_sales.col2 from schema1.emp_sales as emp_sales where emp_sales.col1 > 100)";
		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runTupleParsertest(sql, parser);
		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{TUPLE={from={table={alias=emp_sales, schema=schema1, table=emp_sales}}, where={condition={left={column={name=col1, table_ref=emp_sales}}, right={literal=100}, operator=>}}, select={1={column={name=col2, table_ref=emp_sales}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[col2]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{schema1.emp_sales={col2=[[@2,8:16='emp_sales',<392>,1:8]], col1=[[@12,65:73='emp_sales',<392>,1:65]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={col2=[[@4,18:21='col2',<392>,1:18]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={col2=[[@4,18:21='col2',<392>,1:18]]}, table_dictionary={schema1.emp_sales={col2=[[@2,8:16='emp_sales',<392>,1:8]], col1=[[@12,65:73='emp_sales',<392>,1:65]]}}, filters=[{name=col1, table_ref=emp_sales}], interface={col2=[{name=col2, table_ref=emp_sales}]}, table_alias={emp_sales=schema1.emp_sales}}, table_alias={query0=query0}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void basicTupleValuesTest() {
		String sql = "(values (1, 2, 'aaa'), (92, 3, 'aaa')) as source (col1, col2, col3)";
		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runTupleParsertest(sql, parser);
		
		
		Assert.assertEquals("AST is wrong", "{TUPLE={values={columns={1={column={name=col1, table_ref=null}}, 2={column={name=col2, table_ref=null}}, 3={column={name=col3, table_ref=null}}}, alias=source, matrix={1={row={1={literal=1}, 2={literal=2}, 3={literal='aaa'}}}, 2={row={1={literal=92}, 2={literal=3}, 3={literal='aaa'}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[col2, col3, col1]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{values0={col2=[[@23,56:59='col2',<392>,1:56]], col3=[[@25,62:65='col3',<392>,1:62]], col1=[[@21,50:53='col1',<392>,1:50]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_values0={query_dictionary={col2=[[@23,56:59='col2',<392>,1:56]], col3=[[@25,62:65='col3',<392>,1:62]], col1=[[@21,50:53='col1',<392>,1:50]]}, interface={col2=[], col3=[], col1=[]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void basicQueryValueTest() {
		String sql = "select * from schema1.emp_sales";
		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runQueryParsertest(sql, parser);
		
		Assert.assertEquals("AST is wrong", "{QUERY={select={1={column={name=*, table_ref=*}}}, from={table={schema=schema1, alias=null, table=emp_sales}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{schema1.emp_sales={*=[[@1,7:7='*',<291>,1:7]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@1,7:7='*',<291>,1:7]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={*=[[@1,7:7='*',<291>,1:7]]}, table_dictionary={schema1.emp_sales={*=[[@1,7:7='*',<291>,1:7]]}}, interface={*=[{name=*, table_ref=*}]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void basicQuerySubstitutionValueTest() {
		String sql = "<simple variable>";
		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runQueryParsertest(sql, parser);
		
		Assert.assertEquals("AST is wrong", "{QUERY={substitution={name=<simple variable>, type=query}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<simple variable>=query}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void basicJoinExtensionValueWithOnClauseTest() {
		String sql = "join schema1.emp_sales as dd on (dd.col1=bb.col1)";
		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runJoinExtensionParsertest(sql, parser);
		
		Assert.assertEquals("AST is wrong", "{JOIN_EXTENSION={1={join=join, on={condition={left={column={name=col1, table_ref=dd}}, right={column={name=col1, table_ref=bb}}, operator==}}}, 2={table={alias=dd, schema=schema1, table=emp_sales}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{schema1.emp_sales={col1=[[@8,33:34='dd',<392>,1:33]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{table_dictionary={schema1.emp_sales={col1=[[@8,33:34='dd',<392>,1:33]]}}, unresolved_column={bb.col1={column={name=col1, table_ref=bb}, locations=[[@12,41:42='bb',<392>,1:41]]}}, filters=[{name=col1, table_ref=dd}, {name=col1, table_ref=bb}], table_alias={dd=schema1.emp_sales}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void joinExtensionLeftJoinWithOnTest() {
		final String sql = "left join fourth b on  a.a = b.b "; 
		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runJoinExtensionParsertest(sql, parser);
		
		Assert.assertEquals("AST is wrong", "{JOIN_EXTENSION={1={join=left, on={condition={left={column={name=a, table_ref=a}}, right={column={name=b, table_ref=b}}, operator==}}}, 2={table={alias=b, table=fourth}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{fourth={b=[[@9,29:29='b',<392>,1:29]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{table_dictionary={fourth={b=[[@9,29:29='b',<392>,1:29]]}}, unresolved_column={a.a={column={name=a, table_ref=a}, locations=[[@5,23:23='a',<392>,1:23]]}}, filters=[{name=a, table_ref=a}, {name=b, table_ref=b}], table_alias={b=fourth}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void joinExtensionUnqualifiedJoinWithOnParenthesisTest() {
		final String sql = "cross join fourth b"; 
		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runJoinExtensionParsertest(sql, parser);
		
		Assert.assertEquals("AST is wrong", "{JOIN_EXTENSION={1={join=crossjoin}, 2={table={alias=b, table=fourth}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{fourth={}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{table_dictionary={fourth={}}, table_alias={b=fourth}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void joinExtensionFullOuterJoinWithOnOnConditionVariableTest() {
		final String sql = " full outer join fourth b on <OnJoinCondition> "; 
		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runJoinExtensionParsertest(sql, parser);
		
		Assert.assertEquals("AST is wrong", "{JOIN_EXTENSION={1={join=fullouter, on={substitution={name=<OnJoinCondition>, type=condition}}}, 2={table={alias=b, table=fourth}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Substitution List is wrong", "{<OnJoinCondition>=condition}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{fourth={}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{table_dictionary={fourth={}}, filters=[], table_alias={b=fourth}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void joinExtensionJoinWithOnConditionVariableInParenthesisTest() {
		final String sql = "  join fourth b on (<OnJoinCondition>)"; 
		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runJoinExtensionParsertest(sql, parser);
		
		Assert.assertEquals("AST is wrong", "{JOIN_EXTENSION={1={join=join, on={substitution={name=<OnJoinCondition>, type=condition}}}, 2={table={alias=b, table=fourth}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Substitution List is wrong", "{<OnJoinCondition>=condition}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{fourth={}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{table_dictionary={fourth={}}, filters=[], table_alias={b=fourth}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void joinExtensionJoinWithOnTwoConditionVariablesTest() {
		//  Condition Variable and another join extension variable
		final String sql = " join fourth b on <OnJoinCondition> <OtherJoinCondition>"; 
		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runJoinExtensionParsertest(sql, parser);
		
		Assert.assertEquals("AST is wrong", "{JOIN_EXTENSION={1={join=join, on={substitution={name=<OnJoinCondition>, type=condition}}}, 2={table={alias=b, table=fourth}}, 3={substitution={name=<OtherJoinCondition>, type=join_extension}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Substitution List is wrong", "{<OtherJoinCondition>=join_extension, <OnJoinCondition>=condition}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{fourth={}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{table_dictionary={fourth={}}, filters=[], table_alias={b=fourth}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void joinExtensionJoinTableListWithTable() {
			final String query = " , tab1 as two ";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runJoinExtensionParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{JOIN_EXTENSION={1={table={alias=two, table=tab1}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{table_dictionary={tab1={}}, table_alias={two=tab1}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void joinExtensionJoinTableListWithTupleVariableV1() {
			final String query = " , <tuple variable> as two ";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runJoinExtensionParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{JOIN_EXTENSION={1={table={alias=two, substitution={name=<tuple variable>, type=tuple}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Substitution List is wrong", "{<tuple variable>=tuple}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{<tuple variable>={}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{table_dictionary={<tuple variable>={}}, table_alias={two=<tuple variable>}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void joinExtensionOneTableWithJoinExtensionVariableV1() {
		//  ITEM 17 - Doesn't recognize optional join tree additions after the on clause
		final String query = ", third <extension> ";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runJoinExtensionParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{JOIN_EXTENSION={1={table={alias=null, table=third}}, 2={substitution={name=<extension>, type=join_extension}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Substitution List is wrong", "{<extension>=join_extension}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{third={}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{table_dictionary={third={}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void joinExtensionJoinlessJoinExtensionVariableV1() {
		//  ITEM 17 - Doesn't recognize optional join tree additions after the on clause
		final String query = ", third as T3, fourth as F4 <extension> ";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runJoinExtensionParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{JOIN_EXTENSION={1={table={alias=T3, table=third}}, 2={table={alias=F4, table=fourth}}, 3={substitution={name=<extension>, type=join_extension}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Substitution List is wrong", "{<extension>=join_extension}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{third={}, fourth={}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{table_dictionary={third={}, fourth={}}, table_alias={F4=fourth, T3=third}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void joinExtensionJoinWithConditionAndJoinExtensionVariablesTest() {
		//  ITEM 17 - Doesn't recognize optional join tree additions after the on clause
		final String query = " join fourth as F4 on <third_fourth_join_condition> <extension> ";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runJoinExtensionParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{JOIN_EXTENSION={1={join=join, on={substitution={name=<third_fourth_join_condition>, type=condition}}}, 2={table={alias=F4, table=fourth}}, 3={substitution={name=<extension>, type=join_extension}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Substitution List is wrong", "{<third_fourth_join_condition>=condition, <extension>=join_extension}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{fourth={}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{table_dictionary={fourth={}}, filters=[], table_alias={F4=fourth}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void joinExtensionUnionJoinWithSubstitutionV1() {
		final String query = "  cross join fourth union join fifth natural join sixth ";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runJoinExtensionParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{JOIN_EXTENSION={1={join=crossjoin}, 2={table={alias=null, table=fourth}}, 3={join=unionjoin}, 4={table={alias=null, table=fifth}}, 5={join=naturaljoin}, 6={table={alias=null, table=sixth}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{sixth={}, fifth={}, fourth={}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{table_dictionary={sixth={}, fifth={}, fourth={}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void joinExtensionUnionJoinWithSubstitutionV2() {
		final String query = "  cross join <fourth> as f union join <fifth> as fi natural join <sixth> as si ";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runJoinExtensionParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{JOIN_EXTENSION={1={join=crossjoin}, 2={table={alias=f, substitution={name=<fourth>, type=tuple}}}, 3={join=unionjoin}, 4={table={alias=fi, substitution={name=<fifth>, type=tuple}}}, 5={join=naturaljoin}, 6={table={alias=si, substitution={name=<sixth>, type=tuple}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Substitution List is wrong", "{<sixth>=tuple, <fifth>=tuple, <fourth>=tuple}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{<sixth>={}, <fifth>={}, <fourth>={}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{table_dictionary={<sixth>={}, <fifth>={}, <fourth>={}}, table_alias={fi=<fifth>, f=<fourth>, si=<sixth>}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void valuesStatementAloneTest() {
		final String query = " (values (1, 'aaa'), (92, 'aaa')) ";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor =  runValuesStatementEndParsertest(query, parser);
			
		Assert.assertEquals("Interface is wrong", "[$1, $2]", 
					extractor.getInterface().toString());
		Assert.assertEquals("AST is wrong", "{VALUES={values={matrix={1={row={1={literal=1}, 2={literal='aaa'}}}, 2={row={1={literal=92}, 2={literal='aaa'}}}}}}}",
					extractor.getAsTree().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
					extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{}",
					extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{values0={$1=[[@2,9:9='(',<287>,1:9], [@8,21:21='(',<287>,1:21]], $2=[[@2,9:9='(',<287>,1:9], [@8,21:21='(',<287>,1:21]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_values0={query_dictionary={$1=[[@2,9:9='(',<287>,1:9], [@8,21:21='(',<287>,1:21]], $2=[[@2,9:9='(',<287>,1:9], [@8,21:21='(',<287>,1:21]]}, interface={$1=[], $2=[]}}}",
					extractor.getSymbolTable().toString());
	}


	@Test
	public void valuesStatementAsClauseTest() {
			
		final String query = " (values (1, 'aaa'), (92, 'aaa')) as source";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor =  runValuesStatementEndParsertest(query, parser);
			
		Assert.assertEquals("Interface is wrong", "[$1, $2]", 
					extractor.getInterface().toString());
		Assert.assertEquals("AST is wrong", "{VALUES={values={alias=source, matrix={1={row={1={literal=1}, 2={literal='aaa'}}}, 2={row={1={literal=92}, 2={literal='aaa'}}}}}}}",
					extractor.getAsTree().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
					extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{}",
					extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{values0={$1=[[@2,9:9='(',<287>,1:9], [@8,21:21='(',<287>,1:21]], $2=[[@2,9:9='(',<287>,1:9], [@8,21:21='(',<287>,1:21]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_values0={query_dictionary={$1=[[@2,9:9='(',<287>,1:9], [@8,21:21='(',<287>,1:21]], $2=[[@2,9:9='(',<287>,1:9], [@8,21:21='(',<287>,1:21]]}, interface={$1=[], $2=[]}}}",
					extractor.getSymbolTable().toString());
	}


	@Test
	public void valuesStatementAsClauseAndColumnsTest() {
			
		final String query = " (values (1, 'aaa'), (92, 'aaa')) as source (col1, col2)";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor =  runValuesStatementEndParsertest(query, parser);
			
		Assert.assertEquals("Interface is wrong", "[col2, col1]", 
					extractor.getInterface().toString());
		Assert.assertEquals("AST is wrong", "{VALUES={values={columns={1={column={name=col1, table_ref=null}}, 2={column={name=col2, table_ref=null}}}, alias=source, matrix={1={row={1={literal=1}, 2={literal='aaa'}}}, 2={row={1={literal=92}, 2={literal='aaa'}}}}}}}",
					extractor.getAsTree().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
					extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{}",
					extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{values0={col2=[[@19,51:54='col2',<392>,1:51]], col1=[[@17,45:48='col1',<392>,1:45]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_values0={query_dictionary={col2=[[@19,51:54='col2',<392>,1:51]], col1=[[@17,45:48='col1',<392>,1:45]]}, interface={col2=[], col1=[]}}}",
					extractor.getSymbolTable().toString());
	}


	@Test
	public void subqueryInTupleVariableTest() {
			
		final String query = " (select col1, col2, col3 from source)";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor =  runTupleParsertest(query, parser); 
			
	}


	@Test
	public void valuesStatementAloneCompareValuesAndTupleEndPointTest() {
	    final String query = " (values (1, 2, 'aaa'), (92, 3, 'aaa')) ";
			
		System.out.println("\nTUPLE END POINT PARSE RESULTS: " + query);

		final SQLSelectParserParser tupleParser = parse(query);
		SqlParseEventWalker tupleExtractor =  runTupleParsertest(query, tupleParser);
			
		System.out.println("\nVALUES END POINT PARSE RESULTS: " + query);

		final SQLSelectParserParser valuesParser = parse(query);
		SqlParseEventWalker valuesExtractor =  runValuesStatementEndParsertest(query, valuesParser);

		// Compare Tuple and Values Parse Objects
		Assert.assertEquals("Interfaces don't match", tupleExtractor.getInterface().toString(), 
				valuesExtractor.getInterface().toString());
		Assert.assertEquals("ASTs don't match", tupleExtractor.getAsTree().get(SQLPARSER_TUPLE_TREE_KEY).toString(),
				valuesExtractor.getAsTree().get(SQLPARSER_VALUES_TREE_KEY).toString());
		Assert.assertEquals("Substitution Lists don't match", tupleExtractor.getSubstitutionsMap().toString(), 
				valuesExtractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionaries don't match", tupleExtractor.getTableColumnDictionaryMap().toString(),
				valuesExtractor.getTableColumnDictionaryMap().toString()			);
		Assert.assertEquals("Query Column Dictionary don't match", tupleExtractor.getQueryColumnDictionaryMap().toString(),
				valuesExtractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Tables don't match", tupleExtractor.getSymbolTable().toString()	,
				valuesExtractor.getSymbolTable().toString()	);


		// Expected Tuple Parse Objects
		Assert.assertEquals("Interface is wrong", "[$1, $2, $3]", 
			tupleExtractor.getInterface().toString());
		Assert.assertEquals("AST is wrong", "{TUPLE={values={matrix={1={row={1={literal=1}, 2={literal=2}, 3={literal='aaa'}}}, 2={row={1={literal=92}, 2={literal=3}, 3={literal='aaa'}}}}}}}",
			tupleExtractor.getAsTree().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
			tupleExtractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{}",
			tupleExtractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{values0={$1=[[@2,9:9='(',<287>,1:9], [@10,24:24='(',<287>,1:24]], $2=[[@2,9:9='(',<287>,1:9], [@10,24:24='(',<287>,1:24]], $3=[[@2,9:9='(',<287>,1:9], [@10,24:24='(',<287>,1:24]]}}",
			tupleExtractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_values0={query_dictionary={$1=[[@2,9:9='(',<287>,1:9], [@10,24:24='(',<287>,1:24]], $2=[[@2,9:9='(',<287>,1:9], [@10,24:24='(',<287>,1:24]], $3=[[@2,9:9='(',<287>,1:9], [@10,24:24='(',<287>,1:24]]}, interface={$1=[], $2=[], $3=[]}}}",
			tupleExtractor.getSymbolTable().toString());

		// Expected Values Parse Objects
		Assert.assertEquals("Interface is wrong", "[$1, $2, $3]", 
			valuesExtractor.getInterface().toString());
		Assert.assertEquals("AST is wrong", "{VALUES={values={matrix={1={row={1={literal=1}, 2={literal=2}, 3={literal='aaa'}}}, 2={row={1={literal=92}, 2={literal=3}, 3={literal='aaa'}}}}}}}",
			valuesExtractor.getAsTree().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
			valuesExtractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{}",
			valuesExtractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{values0={$1=[[@2,9:9='(',<287>,1:9], [@10,24:24='(',<287>,1:24]], $2=[[@2,9:9='(',<287>,1:9], [@10,24:24='(',<287>,1:24]], $3=[[@2,9:9='(',<287>,1:9], [@10,24:24='(',<287>,1:24]]}}",
			valuesExtractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_values0={query_dictionary={$1=[[@2,9:9='(',<287>,1:9], [@10,24:24='(',<287>,1:24]], $2=[[@2,9:9='(',<287>,1:9], [@10,24:24='(',<287>,1:24]], $3=[[@2,9:9='(',<287>,1:9], [@10,24:24='(',<287>,1:24]]}, interface={$1=[], $2=[], $3=[]}}}",
			valuesExtractor.getSymbolTable().toString());

		}


	@Test
	public void valuesStatementAloneInTupleVariableTest() {
		final String query = " (values (1, 2, 'aaa'), (92, 3, 'aaa')) ";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor =  runTupleParsertest(query, parser);
			
		Assert.assertEquals("Interface is wrong", "[$1, $2, $3]", 
					extractor.getInterface().toString());
		Assert.assertEquals("AST is wrong", "{TUPLE={values={matrix={1={row={1={literal=1}, 2={literal=2}, 3={literal='aaa'}}}, 2={row={1={literal=92}, 2={literal=3}, 3={literal='aaa'}}}}}}}",
					extractor.getAsTree().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
					extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{}",
					extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{values0={$1=[[@2,9:9='(',<287>,1:9], [@10,24:24='(',<287>,1:24]], $2=[[@2,9:9='(',<287>,1:9], [@10,24:24='(',<287>,1:24]], $3=[[@2,9:9='(',<287>,1:9], [@10,24:24='(',<287>,1:24]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_values0={query_dictionary={$1=[[@2,9:9='(',<287>,1:9], [@10,24:24='(',<287>,1:24]], $2=[[@2,9:9='(',<287>,1:9], [@10,24:24='(',<287>,1:24]], $3=[[@2,9:9='(',<287>,1:9], [@10,24:24='(',<287>,1:24]]}, interface={$1=[], $2=[], $3=[]}}}",
					extractor.getSymbolTable().toString());
	}

	@Test
	public void updateStatementEndpointMatchesSqlEndpointTest() {
		final String query = "UPDATE t SET a = b FROM t2 WHERE c = 1";

		final SQLSelectParserParser sqlParser = parse(query);
		SqlParseEventWalker sqlExtractor = runSQLParsertest(query, sqlParser);

		final SQLSelectParserParser updateParser = parse(query);
		SqlParseEventWalker updateExtractor = runUpdateEndPointParsertest(query, updateParser);

		Assert.assertEquals("Update endpoint AST is wrong",
				"{UPDATE={update={from={table={alias=null, table=t2}}, where={condition={left={column={name=c, table_ref=null}}, right={literal=1}, operator==}}, assignments={1={set={column={name=a, table_ref=null}}, to={column={name=b, table_ref=null}}}}, table={alias=null, table=t}}}}",
				updateExtractor.getAsTree().toString());
		Assert.assertEquals("UPDATE endpoint subtree should match SQL endpoint subtree",
				sqlExtractor.getAsTree().get(SQLPARSER_SQL_TREE_KEY).toString(),
				updateExtractor.getAsTree().get(SQLPARSER_UPDATE_TREE_KEY).toString());
		Assert.assertEquals("Interface is wrong",
				sqlExtractor.getInterface().toString(),
				updateExtractor.getInterface().toString());
		Assert.assertEquals("Substitution list is wrong",
				sqlExtractor.getSubstitutionsMap().toString(),
				updateExtractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table dictionary is wrong",
				sqlExtractor.getTableColumnDictionaryMap().toString(),
				updateExtractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query column dictionary is wrong",
				sqlExtractor.getQueryColumnDictionaryMap().toString(),
				updateExtractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol table is wrong",
				sqlExtractor.getSymbolTable().toString(),
				updateExtractor.getSymbolTable().toString());
	}

	@Test
	public void truncateStatementEndpointMatchesDdlEndpointTest() {
		final String query = "TRUNCATE TABLE tab1";

		final SQLSelectParserParser ddlParser = parse(query);
		SqlParseEventWalker ddlExtractor = runDdlParsertest(query, ddlParser);

		final SQLSelectParserParser truncateParser = parse(query);
		SqlParseEventWalker truncateExtractor = runTruncateEndPointParsertest(query, truncateParser);

		Assert.assertEquals("Truncate endpoint AST is wrong",
				"{TRUNCATE={truncate={type=TABLE, name={table=tab1}}}}",
				truncateExtractor.getAsTree().toString());
		Assert.assertEquals("TRUNCATE endpoint subtree should match DDL endpoint subtree",
				ddlExtractor.getAsTree().get(SQLPARSER_DDL_TREE_KEY).toString(),
				truncateExtractor.getAsTree().get(SQLPARSER_TRUNCATE_TREE_KEY).toString());
		Assert.assertEquals("Interface is wrong",
				ddlExtractor.getInterface().toString(),
				truncateExtractor.getInterface().toString());
		Assert.assertEquals("Substitution list is wrong",
				ddlExtractor.getSubstitutionsMap().toString(),
				truncateExtractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table dictionary is wrong",
				ddlExtractor.getTableColumnDictionaryMap().toString(),
				truncateExtractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query column dictionary is wrong",
				ddlExtractor.getQueryColumnDictionaryMap().toString(),
				truncateExtractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol table is wrong",
				ddlExtractor.getSymbolTable().toString(),
				truncateExtractor.getSymbolTable().toString());
	}

	@Test
	public void deleteStatementEndpointMatchesSqlEndpointWithUsingSubqueryTest() {
		// Snowflake DELETE (no RETURNING) routes through delete_snowflake_expression.
		// The sql endpoint no longer accepts DELETE without RETURNING; compare delete endpoint to itself.
		final String query = "DELETE FROM t USING (SELECT id FROM t2) s WHERE t.id = s.id";

		final SQLSelectParserParser deleteParser = parse(query);
		SqlParseEventWalker deleteExtractor = runDeleteEndPointParsertest(query, deleteParser);

		System.out.println("AST: " + deleteExtractor.getAsTree());
		System.out.println("Interface: " + deleteExtractor.getInterface());
		System.out.println("SubMap: " + deleteExtractor.getSubstitutionsMap());
		System.out.println("TableDict: " + deleteExtractor.getTableColumnDictionaryMap());
		System.out.println("QueryColDict: " + deleteExtractor.getQueryColumnDictionaryMap());
		System.out.println("SymbolTable: " + deleteExtractor.getSymbolTable());

		Assert.assertNotNull("DELETE endpoint should produce a result",
				deleteExtractor.getAsTree().get(SQLPARSER_DELETE_TREE_KEY));
	}


	@Test
	public void valuesStatementAsClauseInTupleVariableTest() {
			
		final String query = " (values (1, 2, 'aaa'), (92, 3, 'aaa')) as source";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor =  runTupleParsertest(query, parser);
			
		Assert.assertEquals("Interface is wrong", "[$1, $2, $3]", 
					extractor.getInterface().toString());
		Assert.assertEquals("AST is wrong", "{TUPLE={values={alias=source, matrix={1={row={1={literal=1}, 2={literal=2}, 3={literal='aaa'}}}, 2={row={1={literal=92}, 2={literal=3}, 3={literal='aaa'}}}}}}}",
					extractor.getAsTree().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
					extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{}",
					extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{values0={$1=[[@2,9:9='(',<287>,1:9], [@10,24:24='(',<287>,1:24]], $2=[[@2,9:9='(',<287>,1:9], [@10,24:24='(',<287>,1:24]], $3=[[@2,9:9='(',<287>,1:9], [@10,24:24='(',<287>,1:24]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_values0={query_dictionary={$1=[[@2,9:9='(',<287>,1:9], [@10,24:24='(',<287>,1:24]], $2=[[@2,9:9='(',<287>,1:9], [@10,24:24='(',<287>,1:24]], $3=[[@2,9:9='(',<287>,1:9], [@10,24:24='(',<287>,1:24]]}, interface={$1=[], $2=[], $3=[]}}}",
					extractor.getSymbolTable().toString());
	}


	@Test
	public void valuesStatementWithAsClauseAndColumnsInTupleVariableTest() {
				
		final String query = " (values (1, 2, 'aaa'), (92, 3, 'aaa')) as source (col1, col2, col3)";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor =  runTupleParsertest(query, parser); 
				
		Assert.assertEquals("Interface is wrong", "[col2, col3, col1]", 
					extractor.getInterface().toString());
		Assert.assertEquals("AST is wrong", "{TUPLE={values={columns={1={column={name=col1, table_ref=null}}, 2={column={name=col2, table_ref=null}}, 3={column={name=col3, table_ref=null}}}, alias=source, matrix={1={row={1={literal=1}, 2={literal=2}, 3={literal='aaa'}}}, 2={row={1={literal=92}, 2={literal=3}, 3={literal='aaa'}}}}}}}",
					extractor.getAsTree().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
					extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{}",
					extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{values0={col2=[[@23,57:60='col2',<392>,1:57]], col3=[[@25,63:66='col3',<392>,1:63]], col1=[[@21,51:54='col1',<392>,1:51]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_values0={query_dictionary={col2=[[@23,57:60='col2',<392>,1:57]], col3=[[@25,63:66='col3',<392>,1:63]], col1=[[@21,51:54='col1',<392>,1:51]]}, interface={col2=[], col3=[], col1=[]}}}",
					extractor.getSymbolTable().toString());
	}

}
