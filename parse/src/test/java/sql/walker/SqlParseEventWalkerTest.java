package sql.walker;

import java.util.HashMap;
import java.util.Map;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.junit.Assert;
import org.junit.Test;

import sql.SQLSelectParserLexer;
import sql.SQLSelectParserParser;
import sql.SQLSelectParserParser.Condition_valueContext;
import sql.SQLSelectParserParser.Predicand_valueContext;
import sql.SQLSelectParserParser.SqlContext;

public class SqlParseEventWalkerTest {
	// *********************************
	// Clauses that need to be built out
	
	@Test
	public void concatenationForumlaTest() {
		//TODO: the concatenated elements work when in parentheses, otherwise grammar is indeterminate
		final String query = "SELECT substr(strm, 1, 2) || substr(strm, 3, 1) + 1 || substr(strm, 4,1)"
				+ " from tab1";

		final SQLSelectParserParser parser = parse(query);
		runParsertest(query, parser);
	}
	
	@Test
	public void concatenationInTest() {
		//TODO: the concatenated elements work when in parentheses, otherwise grammar is indeterminate
		final String query = "SELECT apple"
				+ " from tab1 where subj_cd || crs_nm in (select fld from orange)";

		final SQLSelectParserParser parser = parse(query);
		runParsertest(query, parser);
	}
	
	@Test
	public void topXv1Test() {
		//TODO: TOP 100 does not parse
		final String query = "SELECT top 100 apple"
				+ " from tab1";

		final SQLSelectParserParser parser = parse(query);
		runParsertest(query, parser);
	}
	
	@Test
	public void topXv2Test() {
		// TODO: Query doesn't parse correctly. top(100) interpreted as a general function with "apple" as its alias
		// leaving the "as orange" 
		final String query = "SELECT top(100) apple as orange"
				+ " from tab1";

		final SQLSelectParserParser parser = parse(query);
		runParsertest(query, parser);
	}
	
	// JOIN CONDITION VARIATIONS

	@Test
	public void basicJoinWithOnTest() {
		final String query = " SELECT a.* FROM third a join fourth b on  a.a = b.b "; 

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=*, table_ref=a}}}, from={join={1={table={alias=a, table=third}}, 2={join=join, on={condition={left={column={name=a, table_ref=a}}, right={column={name=b, table_ref=b}}, operator==}}}, 3={table={alias=b, table=fourth}}}}}}",
				extractor.getSqlTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{third={a=[@11,43:43='a',<210>,1:43], *=[@1,8:8='a',<210>,1:8]}, fourth={b=[@15,49:49='b',<210>,1:49]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={a=third, b=fourth, third={a=[@11,43:43='a',<210>,1:43], *=[@1,8:8='a',<210>,1:8]}, fourth={b=[@15,49:49='b',<210>,1:49]}, interface={*={column={name=*, table_ref=a}}}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void basicJoinWithOnParenthesisTest() {
		// Item 4 - Normal join ON Condition in parentheses should drop the parenthetical
		final String query = " SELECT a.* FROM third a join fourth b on (a.a = b.b)"; 

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=*, table_ref=a}}}, from={join={1={table={alias=a, table=third}}, 2={join=join, on={condition={left={column={name=a, table_ref=a}}, right={column={name=b, table_ref=b}}, operator==}}}, 3={table={alias=b, table=fourth}}}}}}",
				extractor.getSqlTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{third={a=[@12,43:43='a',<210>,1:43], *=[@1,8:8='a',<210>,1:8]}, fourth={b=[@16,49:49='b',<210>,1:49]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={a=third, b=fourth, third={a=[@12,43:43='a',<210>,1:43], *=[@1,8:8='a',<210>,1:8]}, fourth={b=[@16,49:49='b',<210>,1:49]}, interface={*={column={name=*, table_ref=a}}}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void basicJoinWithOnOnConditionVariableTest() {
		// Item 46 - Condition Variable not typed or captured
		final String query = " SELECT a.* FROM third a join fourth b on <OnJoinCondition> "; 

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=*, table_ref=a}}}, from={join={1={table={alias=a, table=third}}, 2={join=join, on={substitution={name=<OnJoinCondition>, type=condition}}}, 3={table={alias=b, table=fourth}}}}}}",
				extractor.getSqlTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<OnJoinCondition>=condition}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{third={*=[@1,8:8='a',<210>,1:8]}, fourth={}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={a=third, b=fourth, third={*=[@1,8:8='a',<210>,1:8]}, fourth={}, interface={*={column={name=*, table_ref=a}}}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void basicJoinWithOnConditionVariableInParenthesisTest() {
		//  Item 47 - Condition Variable in parenthetical ON statement not typed or captured
		final String query = " SELECT a.* FROM third a join fourth b on (<OnJoinCondition>)"; 

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=*, table_ref=a}}}, from={join={1={table={alias=a, table=third}}, 2={join=join, on={substitution={name=<OnJoinCondition>, type=condition}}}, 3={table={alias=b, table=fourth}}}}}}",
				extractor.getSqlTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<OnJoinCondition>=condition}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{third={*=[@1,8:8='a',<210>,1:8]}, fourth={}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={a=third, b=fourth, third={*=[@1,8:8='a',<210>,1:8]}, fourth={}, interface={*={column={name=*, table_ref=a}}}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void basicJoinWithOnTwoConditionVariablesTest() {
		//  Condition Variables in an AND clause are labeled and captured correctly
		final String query = " SELECT a.* FROM third a join fourth b on <OnJoinCondition> and <OtherJoinCondition>"; 

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=*, table_ref=a}}}, from={join={1={table={alias=a, table=third}}, 2={join=join, on={and={1={substitution={name=<OnJoinCondition>, type=condition}}, 2={substitution={name=<OtherJoinCondition>, type=condition}}}}}, 3={table={alias=b, table=fourth}}}}}}",
				extractor.getSqlTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<OtherJoinCondition>=condition, <OnJoinCondition>=condition}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{third={*=[@1,8:8='a',<210>,1:8]}, fourth={}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={a=third, b=fourth, third={*=[@1,8:8='a',<210>,1:8]}, fourth={}, interface={*={column={name=*, table_ref=a}}}}}",
				extractor.getSymbolTable().toString());
	}

	// Special Join Extension Variables

	@Test
	public void fromListTest() {
		final String query = " SELECT * FROM third ";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=*, table_ref=*}}}, from={table={alias=null, table=third}}}}",
				extractor.getSqlTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{third={*=[@1,8:8='*',<198>,1:8]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={third={*=[@1,8:8='*',<198>,1:8]}, interface={*={column={name=*, table_ref=*}}}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void tableListWithTupleVariableV1() {
		final String query = " SELECT * FROM third, <tuple variable> as two ";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=*, table_ref=*}}}, from={join={1={table={alias=null, table=third}}, 2={table={alias=two, substitution={name=<tuple variable>, type=tuple}}}}}}}",
				extractor.getSqlTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<tuple variable>=tuple}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{<tuple variable>={}, third={}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={<tuple variable>={}, third={}, interface={*={column={name=*, table_ref=*}}}, two=<tuple variable>, unknown={*=[@1,8:8='*',<198>,1:8]}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void tableListWithTupleVariableV2() {
		// TODO: ITEM 28 - Doesn't parse tuple variable in a from list without an alias
		final String query = " SELECT * FROM third, <tuple variable> ";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=*, table_ref=*}}}, from={extension={substitution={name=<extension>, type=join_extension}}, table={alias=null, table=third}}}}",
				extractor.getSqlTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<extension>=join_extension}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{third={*=[@1,8:8='*',<198>,1:8]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={third={*=[@1,8:8='*',<198>,1:8]}, interface={*={column={name=*, table_ref=*}}}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void oneTableWithJoinExtensionVariableV1() {
		//  ITEM 17 - Doesn't recognize optional join tree additions after the on clause
		final String query = " SELECT * FROM third <extension> ";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=*, table_ref=*}}}, from={extension={substitution={name=<extension>, type=join_extension}}, table={alias=null, table=third}}}}",
				extractor.getSqlTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<extension>=join_extension}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{third={*=[@1,8:8='*',<198>,1:8]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={third={*=[@1,8:8='*',<198>,1:8]}, interface={*={column={name=*, table_ref=*}}}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void joinlessJoinExtensionVariableV1() {
		//  ITEM 17 - Doesn't recognize optional join tree additions after the on clause
		final String query = " SELECT * FROM third as T3, fourth as F4 <extension> ";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=*, table_ref=*}}}, from={extension={substitution={name=<extension>, type=join_extension}}, join={1={table={alias=T3, table=third}}, 2={table={alias=F4, table=fourth}}}}}}",
				extractor.getSqlTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<extension>=join_extension}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{third={}, fourth={}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={third={}, fourth={}, interface={*={column={name=*, table_ref=*}}}, F4=fourth, T3=third, unknown={*=[@1,8:8='*',<198>,1:8]}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void joinExtensionVariableV1() {
		//  ITEM 17 - Doesn't recognize optional join tree additions after the on clause
		final String query = " SELECT * FROM third as T3 join fourth as F4 on <third_fourth_join_condition> <extension> ";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=*, table_ref=*}}}, from={extension={substitution={name=<extension>, type=join_extension}}, join={1={table={alias=T3, table=third}}, 2={join=join, on={substitution={name=<third_fourth_join_condition>, type=condition}}}, 3={table={alias=F4, table=fourth}}}}}}",
				extractor.getSqlTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<third_fourth_join_condition>=condition, <extension>=join_extension}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{third={}, fourth={}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={third={}, fourth={}, interface={*={column={name=*, table_ref=*}}}, F4=fourth, T3=third, unknown={*=[@1,8:8='*',<198>,1:8]}}}",
				extractor.getSymbolTable().toString());
	}

	// End of Join Extensions

	// WHERE CONDITION VARIATIONS
	
	@Test
	public void whereConditionWithSingleConditionVariableTest() {
		// Item 43 - Where with single predicand variable does not recognize it as a variable or set its type
		final String query = "SELECT apple from tab1 where <subject code>";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=apple, table_ref=null}}}, from={table={alias=null, table=tab1}}, where={substitution={name=<subject code>, type=condition}}}}",
				extractor.getSqlTree().toString());
		Assert.assertEquals("Interface is wrong", "[apple]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<subject code>=condition}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={apple=[@1,7:11='apple',<210>,1:7]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={tab1={apple=[@1,7:11='apple',<210>,1:7]}, interface={apple={column={name=apple, table_ref=null}}}}}",
				extractor.getSymbolTable().toString());
	}
	
	@Test
	public void whereConditionWithSingleColumnVariableTest() {
		final String query = "SELECT apple from tab1 where tab1.<subject code>";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=apple, table_ref=null}}}, from={table={alias=null, table=tab1}}, where={column={substitution={name=<subject code>, type=column}, table_ref=tab1}}}}",
				extractor.getSqlTree().toString());
		Assert.assertEquals("Interface is wrong", "[apple]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<subject code>=column}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={apple=[@1,7:11='apple',<210>,1:7], <subject code>={substitution={name=<subject code>, type=column}}}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={tab1={apple=[@1,7:11='apple',<210>,1:7], <subject code>={substitution={name=<subject code>, type=column}}}, interface={apple={column={name=apple, table_ref=null}}}}}",
				extractor.getSymbolTable().toString());
	}
	
	@Test
	public void whereConditionCOmparingPredicandVariablesTest() {
		final String query = "SELECT apple from tab1 where <subject code> = <other subject code>";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=apple, table_ref=null}}}, from={table={alias=null, table=tab1}}, where={condition={left={substitution={name=<subject code>, type=predicand}}, right={substitution={name=<other subject code>, type=predicand}}, operator==}}}}",
				extractor.getSqlTree().toString());
		Assert.assertEquals("Interface is wrong", "[apple]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<other subject code>=predicand, <subject code>=predicand}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={apple=[@1,7:11='apple',<210>,1:7]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={tab1={apple=[@1,7:11='apple',<210>,1:7]}, interface={apple={column={name=apple, table_ref=null}}}}}",
				extractor.getSymbolTable().toString());
	}
	
	@Test
	public void whereConditionComparingPredicandVariableToNullTest() {
		// Item 48 - Predicand Variable in an IS NULL condition is not recognized
		final String query = "SELECT apple from tab1 where <subject code> is null";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=apple, table_ref=null}}}, from={table={alias=null, table=tab1}}, where={condition={left={substitution={name=<subject code>, type=predicand}}, operator=is null}}}}",
				extractor.getSqlTree().toString());
		Assert.assertEquals("Interface is wrong", "[apple]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<subject code>=predicand}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={apple=[@1,7:11='apple',<210>,1:7]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={tab1={apple=[@1,7:11='apple',<210>,1:7]}, interface={apple={column={name=apple, table_ref=null}}}}}",
				extractor.getSymbolTable().toString());
	}
	
	@Test
	public void whereConditionComparingPredicandVariableToNotNullTest() {
		// Item 48 - Predicand Variable in an IS NULL condition is not recognized
		final String query = "SELECT apple from tab1 where <subject code> is not null";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=apple, table_ref=null}}}, from={table={alias=null, table=tab1}}, where={condition={left={substitution={name=<subject code>, type=predicand}}, operator=is not null}}}}",
				extractor.getSqlTree().toString());
		Assert.assertEquals("Interface is wrong", "[apple]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<subject code>=predicand}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={apple=[@1,7:11='apple',<210>,1:7]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={tab1={apple=[@1,7:11='apple',<210>,1:7]}, interface={apple={column={name=apple, table_ref=null}}}}}",
				extractor.getSymbolTable().toString());
	}
	
	@Test
	public void whereMultipleConditionComparingPredicandVariableToNullTest() {
		// A condition and a predicand Variable connected by and in an IS NULL condition is not recognized
		final String query = "SELECT apple from tab1 where <first condition> and <subject code> is null";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=apple, table_ref=null}}}, from={table={alias=null, table=tab1}}, where={and={1={substitution={name=<first condition>, type=condition}}, 2={condition={left={substitution={name=<subject code>, type=predicand}}, operator=is null}}}}}}",
				extractor.getSqlTree().toString());
		Assert.assertEquals("Interface is wrong", "[apple]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<subject code>=predicand, <first condition>=condition}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={apple=[@1,7:11='apple',<210>,1:7]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={tab1={apple=[@1,7:11='apple',<210>,1:7]}, interface={apple={column={name=apple, table_ref=null}}}}}",
				extractor.getSymbolTable().toString());
	}
	
	@Test
	public void whereIsTrueTest() {
		final String query = "SELECT apple from tab1 where subj is true";
		//2={condition={left={substitution={name=<subject code>, type=predicand}}, operator=is true}}

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=apple, table_ref=null}}}, from={table={alias=null, table=tab1}}, where={condition={left={column={name=subj, table_ref=null}}}, operator=is true}}}",
				extractor.getSqlTree().toString());
		Assert.assertEquals("Interface is wrong", "[apple]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={subj=[@5,29:32='subj',<210>,1:29], apple=[@1,7:11='apple',<210>,1:7]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={tab1={subj=[@5,29:32='subj',<210>,1:29], apple=[@1,7:11='apple',<210>,1:7]}, interface={apple={column={name=apple, table_ref=null}}}}}",
				extractor.getSymbolTable().toString());
	}
	
	@Test
	public void whereIsNotTrueTest() {
		final String query = "SELECT apple from tab1 where subj is not true";
		//2={condition={left={substitution={name=<subject code>, type=predicand}}, operator=is not true}}

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=apple, table_ref=null}}}, from={table={alias=null, table=tab1}}, where={condition={left={column={name=subj, table_ref=null}}}, operator=is not true}}}",
				extractor.getSqlTree().toString());
		Assert.assertEquals("Interface is wrong", "[apple]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={subj=[@5,29:32='subj',<210>,1:29], apple=[@1,7:11='apple',<210>,1:7]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={tab1={subj=[@5,29:32='subj',<210>,1:29], apple=[@1,7:11='apple',<210>,1:7]}, interface={apple={column={name=apple, table_ref=null}}}}}",
				extractor.getSymbolTable().toString());
	}
	
	@Test
	public void whereConditionComparingPredicandVariableToIsTrueTest() {
		// Item 49 - Predicand Variable in an IS TRUE condition is not recognized
		final String query = "SELECT apple from tab1 where <subject code> is true";
		//{condition={left={substitution={name=<subject code>, type=predicand}}, operator=is true}}

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=apple, table_ref=null}}}, from={table={alias=null, table=tab1}}, where={condition={left={substitution={name=<subject code>, type=predicand}}}, operator=is true}}}",
				extractor.getSqlTree().toString());
		Assert.assertEquals("Interface is wrong", "[apple]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<subject code>=predicand}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={apple=[@1,7:11='apple',<210>,1:7]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={tab1={apple=[@1,7:11='apple',<210>,1:7]}, interface={apple={column={name=apple, table_ref=null}}}}}",
				extractor.getSymbolTable().toString());
	}
	
	@Test
	public void whereConditionWithAndPredicandTest() {
		final String query = "SELECT apple from tab1 where <subject code> and true";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=apple, table_ref=null}}}, from={table={alias=null, table=tab1}}, where={and={1={substitution={name=<subject code>, type=condition}}, 2={literal=true}}}}}",
				extractor.getSqlTree().toString());
		Assert.assertEquals("Interface is wrong", "[apple]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<subject code>=condition}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={apple=[@1,7:11='apple',<210>,1:7]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={tab1={apple=[@1,7:11='apple',<210>,1:7]}, interface={apple={column={name=apple, table_ref=null}}}}}",
				extractor.getSymbolTable().toString());
	}
	
	@Test
	public void whereConditionWithOrPredicandTest() {
		final String query = "SELECT apple from tab1 where <subject code> or true";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=apple, table_ref=null}}}, from={table={alias=null, table=tab1}}, where={or={1={substitution={name=<subject code>, type=condition}}, 2={literal=true}}}}}",
				extractor.getSqlTree().toString());
		Assert.assertEquals("Interface is wrong", "[apple]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<subject code>=condition}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={apple=[@1,7:11='apple',<210>,1:7]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={tab1={apple=[@1,7:11='apple',<210>,1:7]}, interface={apple={column={name=apple, table_ref=null}}}}}",
				extractor.getSymbolTable().toString());
	}
	
	@Test
	public void whereConditionWithOrPredicandVariablesTest() {
		final String query = "SELECT apple from tab1 where <subject code> or (<other>)";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=apple, table_ref=null}}}, from={table={alias=null, table=tab1}}, where={or={1={substitution={name=<subject code>, type=condition}}, 2={parentheses={substitution={name=<other>, type=condition}}}}}}}",
				extractor.getSqlTree().toString());
		Assert.assertEquals("Interface is wrong", "[apple]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<subject code>=condition, <other>=condition}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={apple=[@1,7:11='apple',<210>,1:7]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={tab1={apple=[@1,7:11='apple',<210>,1:7]}, interface={apple={column={name=apple, table_ref=null}}}}}",
				extractor.getSymbolTable().toString());
	}
	
	@Test
	public void whereConditionWithParentheticalConditionVariableTest() {
		// Item 44 - does not recognize condition variable
		final String query = "SELECT apple from tab1 where (<subject code>)";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=apple, table_ref=null}}}, from={table={alias=null, table=tab1}}, where={parentheses={substitution={name=<subject code>, type=condition}}}}}",
				extractor.getSqlTree().toString());
		Assert.assertEquals("Interface is wrong", "[apple]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<subject code>=condition}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={apple=[@1,7:11='apple',<210>,1:7]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={tab1={apple=[@1,7:11='apple',<210>,1:7]}, interface={apple={column={name=apple, table_ref=null}}}}}",
				extractor.getSymbolTable().toString());
	}
	
	@Test
	public void whereConditionWithParentheticalConditionVariableInOrTest() {
		// Item 45 - Thinks the condition variable is a query variable
		final String query = "SELECT apple from tab1 where (<subject code>) or true";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=apple, table_ref=null}}}, from={table={alias=null, table=tab1}}, where={or={1={parentheses={substitution={name=<subject code>, type=condition}}}, 2={literal=true}}}}}",
				extractor.getSqlTree().toString());
		Assert.assertEquals("Interface is wrong", "[apple]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<subject code>=condition}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={apple=[@1,7:11='apple',<210>,1:7]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={tab1={apple=[@1,7:11='apple',<210>,1:7]}, interface={apple={column={name=apple, table_ref=null}}}}}",
				extractor.getSymbolTable().toString());
	}
	
	@Test
	public void whereConditionWithMixedConditionAndPredicandVariablesTest() {
		// Where with both condition variable and predicand variable in a comparison
		final String query = "SELECT apple from tab1 where <subject code_condition> and <subject_code_predicand> = banana";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=apple, table_ref=null}}}, from={table={alias=null, table=tab1}}, where={and={1={substitution={name=<subject code_condition>, type=condition}}, 2={condition={left={substitution={name=<subject_code_predicand>, type=predicand}}, right={column={name=banana, table_ref=null}}, operator==}}}}}}",
				extractor.getSqlTree().toString());
		Assert.assertEquals("Interface is wrong", "[apple]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<subject code_condition>=condition, <subject_code_predicand>=predicand}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={banana=[@9,85:90='banana',<210>,1:85], apple=[@1,7:11='apple',<210>,1:7]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={tab1={banana=[@9,85:90='banana',<210>,1:85], apple=[@1,7:11='apple',<210>,1:7]}, interface={apple={column={name=apple, table_ref=null}}}}}",
				extractor.getSymbolTable().toString());
	}

	// BETWEEN Statements
	
	@Test
	public void basicBetweenTest() {
		// Item 19 - finish between statement
		final String query = "SELECT apple from tab1 where a between c and d";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=apple, table_ref=null}}}, from={table={alias=null, table=tab1}}, where={between={item={column={name=a, table_ref=null}}, symmetry=null, end={column={name=d, table_ref=null}}, begin={column={name=c, table_ref=null}}, operator=between}}}}",
				extractor.getSqlTree().toString());
		Assert.assertEquals("Interface is wrong", "[apple]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={apple=[@1,7:11='apple',<210>,1:7], a=[@5,29:29='a',<210>,1:29], c=[@7,39:39='c',<210>,1:39], d=[@9,45:45='d',<210>,1:45]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={tab1={apple=[@1,7:11='apple',<210>,1:7], a=[@5,29:29='a',<210>,1:29], c=[@7,39:39='c',<210>,1:39], d=[@9,45:45='d',<210>,1:45]}, interface={apple={column={name=apple, table_ref=null}}}}}",
				extractor.getSymbolTable().toString());
	}
	
	@Test
	public void basicBetweenTestWithSymmetry() {
		// Item 19 - finish between statement
		final String query = "SELECT apple from tab1 where a between symmetric c and d";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=apple, table_ref=null}}}, from={table={alias=null, table=tab1}}, where={between={item={column={name=a, table_ref=null}}, symmetry=symmetric, end={column={name=d, table_ref=null}}, begin={column={name=c, table_ref=null}}, operator=between}}}}",
				extractor.getSqlTree().toString());
		Assert.assertEquals("Interface is wrong", "[apple]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={apple=[@1,7:11='apple',<210>,1:7], a=[@5,29:29='a',<210>,1:29], c=[@8,49:49='c',<210>,1:49], d=[@10,55:55='d',<210>,1:55]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={tab1={apple=[@1,7:11='apple',<210>,1:7], a=[@5,29:29='a',<210>,1:29], c=[@8,49:49='c',<210>,1:49], d=[@10,55:55='d',<210>,1:55]}, interface={apple={column={name=apple, table_ref=null}}}}}",
				extractor.getSymbolTable().toString());
	}
	
	@Test
	public void basicNotBetweenTest() {
		// Item 19 - finish between statement
		final String query = "SELECT apple from tab1 where a not between c and d";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=apple, table_ref=null}}}, from={table={alias=null, table=tab1}}, where={between={item={column={name=a, table_ref=null}}, symmetry=null, end={column={name=d, table_ref=null}}, begin={column={name=c, table_ref=null}}, operator=not between}}}}",
				extractor.getSqlTree().toString());
		Assert.assertEquals("Interface is wrong", "[apple]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={apple=[@1,7:11='apple',<210>,1:7], a=[@5,29:29='a',<210>,1:29], c=[@8,43:43='c',<210>,1:43], d=[@10,49:49='d',<210>,1:49]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={tab1={apple=[@1,7:11='apple',<210>,1:7], a=[@5,29:29='a',<210>,1:29], c=[@8,43:43='c',<210>,1:43], d=[@10,49:49='d',<210>,1:49]}, interface={apple={column={name=apple, table_ref=null}}}}}",
				extractor.getSymbolTable().toString());
	}
	
	@Test
	public void basicNotBetweenTestWithSymmetry() {
		// Item 19 - finish between statement
		final String query = "SELECT apple from tab1 where a not between symmetric c and d";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=apple, table_ref=null}}}, from={table={alias=null, table=tab1}}, where={between={item={column={name=a, table_ref=null}}, symmetry=symmetric, end={column={name=d, table_ref=null}}, begin={column={name=c, table_ref=null}}, operator=not between}}}}",
				extractor.getSqlTree().toString());
		Assert.assertEquals("Interface is wrong", "[apple]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={apple=[@1,7:11='apple',<210>,1:7], a=[@5,29:29='a',<210>,1:29], c=[@9,53:53='c',<210>,1:53], d=[@11,59:59='d',<210>,1:59]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={tab1={apple=[@1,7:11='apple',<210>,1:7], a=[@5,29:29='a',<210>,1:29], c=[@9,53:53='c',<210>,1:53], d=[@11,59:59='d',<210>,1:59]}, interface={apple={column={name=apple, table_ref=null}}}}}",
				extractor.getSymbolTable().toString());
	}
	
	@Test
	public void predicandAndColumnVariableNotBetweenTestWithSymmetry() {
		// Item 19 - finish between statement
		final String query = "SELECT apple from tab1 where <a> not between symmetric tab1.<c> and d";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=apple, table_ref=null}}}, from={table={alias=null, table=tab1}}, where={between={item={substitution={name=<a>, type=predicand}}, symmetry=symmetric, end={column={name=d, table_ref=null}}, begin={column={substitution={name=<c>, type=column}, table_ref=tab1}}, operator=not between}}}}",
				extractor.getSqlTree().toString());
		Assert.assertEquals("Interface is wrong", "[apple]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<c>=column, <a>=predicand}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={apple=[@1,7:11='apple',<210>,1:7], d=[@13,68:68='d',<210>,1:68], <c>={substitution={name=<c>, type=column}}}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={tab1={apple=[@1,7:11='apple',<210>,1:7], d=[@13,68:68='d',<210>,1:68], <c>={substitution={name=<c>, type=column}}}, interface={apple={column={name=apple, table_ref=null}}}}}",
				extractor.getSymbolTable().toString());
	}
	
	// End Between Statements
	// IN Statements

	@Test
	public void stringFunctionWithInStatementParseTest() {
		final String query = "SELECT trim(leading '0' from field1), a || b, " + " trim('0' || field2,'0') "
				+ " FROM scbcrse aa " + " WHERE subj_code in ('AA', 'BB') ";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={function={function_name=trim, parameters={qualifier=leading, trim_character={literal='0'}, value={column={name=field1, table_ref=null}}}}}, 2={concatenate={1={column={name=a, table_ref=null}}, 2={column={name=b, table_ref=null}}}}, 3={function={parameters={1={concatenate={1={literal='0'}, 2={column={name=field2, table_ref=null}}}}, 2={literal='0'}}, function_name=trim}}}, from={table={alias=aa, table=scbcrse}}, where={in={item={column={name=subj_code, table_ref=null}}, in_list={list={1={literal='AA'}, 2={literal='BB'}}}}}}}",
				extractor.getSqlTree().toString());
		Assert.assertEquals("Interface is wrong", "[unnamed_1, unnamed_2, unnamed_0]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{scbcrse={a=[@9,38:38='a',<210>,1:38], field1=[@6,29:34='field1',<210>,1:29], b=[@11,43:43='b',<210>,1:43], field2=[@17,59:64='field2',<210>,1:59], subj_code=[@25,95:103='subj_code',<210>,1:95]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={aa=scbcrse, scbcrse={a=[@9,38:38='a',<210>,1:38], field1=[@6,29:34='field1',<210>,1:29], b=[@11,43:43='b',<210>,1:43], field2=[@17,59:64='field2',<210>,1:59], subj_code=[@25,95:103='subj_code',<210>,1:95]}, interface={unnamed_1={concatenate={1={column={name=a, table_ref=null}}, 2={column={name=b, table_ref=null}}}}, unnamed_2={function={parameters={1={concatenate={1={literal='0'}, 2={column={name=field2, table_ref=null}}}}, 2={literal='0'}}, function_name=trim}}, unnamed_0={function={function_name=trim, parameters={qualifier=leading, trim_character={literal='0'}, value={column={name=field1, table_ref=null}}}}}}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void inPredicateSubqueryTest() {
		final String query = "SELECT * FROM scbcrse aa  WHERE subj_code in ('AA', 'BB') "
				+ " and item in (select * from other)";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=*, table_ref=*}}}, from={table={alias=aa, table=scbcrse}}, where={and={1={in={item={column={name=subj_code, table_ref=null}}, in_list={list={1={literal='AA'}, 2={literal='BB'}}}}}, 2={in={item={column={name=item, table_ref=null}}, in_list={select={1={column={name=*, table_ref=*}}}, from={table={alias=null, table=other}}}}}}}}}",
				extractor.getSqlTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{other={*=[@18,79:79='*',<198>,1:79]}, scbcrse={item=[@14,63:66='item',<210>,1:63], *=[@1,7:7='*',<198>,1:7], subj_code=[@6,32:40='subj_code',<210>,1:32]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query1={aa=scbcrse, scbcrse={item=[@14,63:66='item',<210>,1:63], *=[@1,7:7='*',<198>,1:7], subj_code=[@6,32:40='subj_code',<210>,1:32]}, interface={*={column={name=*, table_ref=*}}}, query0={other={*=[@18,79:79='*',<198>,1:79]}, interface={*={column={name=*, table_ref=*}}}}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void inPredicateInListSubqueryVariableTest() {
		final String query = "SELECT * FROM scbcrse aa WHERE subj_code in ('AA', 'BB') "
				+ " and item in (<inlist subquery>)";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=*, table_ref=*}}}, from={table={alias=aa, table=scbcrse}}, where={and={1={in={item={column={name=subj_code, table_ref=null}}, in_list={list={1={literal='AA'}, 2={literal='BB'}}}}}, 2={in={item={column={name=item, table_ref=null}}, in_list={substitution={name=<inlist subquery>, type=query}}}}}}}}",
				extractor.getSqlTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<inlist subquery>=query}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{scbcrse={item=[@14,62:65='item',<210>,1:62], *=[@1,7:7='*',<198>,1:7], subj_code=[@6,31:39='subj_code',<210>,1:31]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={aa=scbcrse, scbcrse={item=[@14,62:65='item',<210>,1:62], *=[@1,7:7='*',<198>,1:7], subj_code=[@6,31:39='subj_code',<210>,1:31]}, interface={*={column={name=*, table_ref=*}}}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void inPredicateColumnVariableInTest() {
		final String query = "SELECT *  FROM scbcrse aa  WHERE aa.<subj_code> in ('AA', 'BB') ";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=*, table_ref=*}}}, from={table={alias=aa, table=scbcrse}}, where={in={item={column={substitution={name=<subj_code>, type=column}, table_ref=aa}}, in_list={list={1={literal='AA'}, 2={literal='BB'}}}}}}}",
				extractor.getSqlTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<subj_code>=column}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{scbcrse={<subj_code>={substitution={name=<subj_code>, type=column}}, *=[@1,7:7='*',<198>,1:7]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={aa=scbcrse, scbcrse={<subj_code>={substitution={name=<subj_code>, type=column}}, *=[@1,7:7='*',<198>,1:7]}, interface={*={column={name=*, table_ref=*}}}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void inPredicatePredicandVariableInTest() {
		// Item 8 - Parse and handle in clauses with Predicand on the right
		final String query = "SELECT *  FROM scbcrse aa  WHERE <subj_code> in ('AA', 'BB') ";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=*, table_ref=*}}}, from={table={alias=aa, table=scbcrse}}, where={in={item={substitution={name=<subj_code>, type=predicand}}, in_list={list={1={literal='AA'}, 2={literal='BB'}}}}}}}",
				extractor.getSqlTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<subj_code>=predicand}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{scbcrse={*=[@1,7:7='*',<198>,1:7]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={aa=scbcrse, scbcrse={*=[@1,7:7='*',<198>,1:7]}, interface={*={column={name=*, table_ref=*}}}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void inPredicateInListVariableTest() {
		// Item 10 - Parse and handle New Substitution Variable for the In List (not a subquery)
		final String query = "SELECT *  FROM scbcrse aa  WHERE item in <inlist substitution>";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=*, table_ref=*}}}, from={table={alias=aa, table=scbcrse}}, where={in={item={column={name=item, table_ref=null}}, in_list={substitution={name=<inlist substitution>, type=in_list}}}}}}",
				extractor.getSqlTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<inlist substitution>=in_list}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{scbcrse={item=[@6,33:36='item',<210>,1:33], *=[@1,7:7='*',<198>,1:7]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={aa=scbcrse, scbcrse={item=[@6,33:36='item',<210>,1:33], *=[@1,7:7='*',<198>,1:7]}, interface={*={column={name=*, table_ref=*}}}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void notInPredicateSubqueryTest() {
		final String query = "SELECT * FROM scbcrse aa  WHERE subj_code not in ('AA', 'BB') "
				+ " and item not in (select * from other)";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=*, table_ref=*}}}, from={table={alias=aa, table=scbcrse}}, where={and={1={in={item={column={name=subj_code, table_ref=null}}, not_in_list={list={1={literal='AA'}, 2={literal='BB'}}}}}, 2={in={item={column={name=item, table_ref=null}}, not_in_list={select={1={column={name=*, table_ref=*}}}, from={table={alias=null, table=other}}}}}}}}}",
				extractor.getSqlTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{other={*=[@20,87:87='*',<198>,1:87]}, scbcrse={item=[@15,67:70='item',<210>,1:67], *=[@1,7:7='*',<198>,1:7], subj_code=[@6,32:40='subj_code',<210>,1:32]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query1={aa=scbcrse, scbcrse={item=[@15,67:70='item',<210>,1:67], *=[@1,7:7='*',<198>,1:7], subj_code=[@6,32:40='subj_code',<210>,1:32]}, interface={*={column={name=*, table_ref=*}}}, query0={other={*=[@20,87:87='*',<198>,1:87]}, interface={*={column={name=*, table_ref=*}}}}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void notInPredicateInListSubqueryVariableTest() {
		final String query = "SELECT * FROM scbcrse aa WHERE subj_code not in ('AA', 'BB') "
				+ " and item not in (<inlist subquery>)";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=*, table_ref=*}}}, from={table={alias=aa, table=scbcrse}}, where={and={1={in={item={column={name=subj_code, table_ref=null}}, not_in_list={list={1={literal='AA'}, 2={literal='BB'}}}}}, 2={in={item={column={name=item, table_ref=null}}, not_in_list={substitution={name=<inlist subquery>, type=query}}}}}}}}",
				extractor.getSqlTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<inlist subquery>=query}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{scbcrse={item=[@15,66:69='item',<210>,1:66], *=[@1,7:7='*',<198>,1:7], subj_code=[@6,31:39='subj_code',<210>,1:31]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={aa=scbcrse, scbcrse={item=[@15,66:69='item',<210>,1:66], *=[@1,7:7='*',<198>,1:7], subj_code=[@6,31:39='subj_code',<210>,1:31]}, interface={*={column={name=*, table_ref=*}}}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void notInPredicateColumnVariableInTest() {
		final String query = "SELECT *  FROM scbcrse aa  WHERE aa.<subj_code> not in ('AA', 'BB') ";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=*, table_ref=*}}}, from={table={alias=aa, table=scbcrse}}, where={in={item={column={substitution={name=<subj_code>, type=column}, table_ref=aa}}, not_in_list={list={1={literal='AA'}, 2={literal='BB'}}}}}}}",
				extractor.getSqlTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<subj_code>=column}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{scbcrse={<subj_code>={substitution={name=<subj_code>, type=column}}, *=[@1,7:7='*',<198>,1:7]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={aa=scbcrse, scbcrse={<subj_code>={substitution={name=<subj_code>, type=column}}, *=[@1,7:7='*',<198>,1:7]}, interface={*={column={name=*, table_ref=*}}}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void notInPredicatePredicandVariableInTest() {
		// Item 8 - Parse and handle in clauses with Predicand on the right
		final String query = "SELECT *  FROM scbcrse aa  WHERE <subj_code> not in ('AA', 'BB') ";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=*, table_ref=*}}}, from={table={alias=aa, table=scbcrse}}, where={in={item={substitution={name=<subj_code>, type=predicand}}, not_in_list={list={1={literal='AA'}, 2={literal='BB'}}}}}}}",
				extractor.getSqlTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<subj_code>=predicand}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{scbcrse={*=[@1,7:7='*',<198>,1:7]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={aa=scbcrse, scbcrse={*=[@1,7:7='*',<198>,1:7]}, interface={*={column={name=*, table_ref=*}}}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void notInPredicateInListVariableTest() {
		// Item 10 - Parse and handle New Substitution Variable for the In List (not a subquery)
		final String query = "SELECT *  FROM scbcrse aa  WHERE item not in <inlist substitution>";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=*, table_ref=*}}}, from={table={alias=aa, table=scbcrse}}, where={in={item={column={name=item, table_ref=null}}, not_in_list={substitution={name=<inlist substitution>, type=in_list}}}}}}",
				extractor.getSqlTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<inlist substitution>=in_list}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{scbcrse={item=[@6,33:36='item',<210>,1:33], *=[@1,7:7='*',<198>,1:7]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={aa=scbcrse, scbcrse={item=[@6,33:36='item',<210>,1:33], *=[@1,7:7='*',<198>,1:7]}, interface={*={column={name=*, table_ref=*}}}}}",
				extractor.getSymbolTable().toString());
	}
	
	// End of In statements
	// LIKE Statements
	
	@Test
	public void likeCondition1V1Test() {
		//Item 20 - Like Not implemented completely
		final String query = "SELECT apple"
				+ " from tab1 where subj_cd like '%STUFF%'";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=apple, table_ref=null}}}, from={table={alias=null, table=tab1}}, where={condition={left={column={name=subj_cd, table_ref=null}}, right={literal='%STUFF%'}, operator=like}}}}",
				extractor.getSqlTree().toString());
		Assert.assertEquals("Interface is wrong", "[apple]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={subj_cd=[@5,29:35='subj_cd',<210>,1:29], apple=[@1,7:11='apple',<210>,1:7]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={tab1={subj_cd=[@5,29:35='subj_cd',<210>,1:29], apple=[@1,7:11='apple',<210>,1:7]}, interface={apple={column={name=apple, table_ref=null}}}}}",
				extractor.getSymbolTable().toString());
	}
	
	@Test
	public void likeCondition1WithColumnTest() {
		//Item 21 - Not parsing any predicand after the LIKE, only string literals
		final String query = "SELECT apple"
				+ " from tab1 where subj_cd like subj_cd";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=apple, table_ref=null}}}, from={table={alias=null, table=tab1}}, where={condition={left={column={name=subj_cd, table_ref=null}}, right={column={name=subj_cd, table_ref=null}}, operator=like}}}}",
				extractor.getSqlTree().toString());
		Assert.assertEquals("Interface is wrong", "[apple]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={subj_cd=[@7,42:48='subj_cd',<210>,1:42], apple=[@1,7:11='apple',<210>,1:7]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={tab1={subj_cd=[@7,42:48='subj_cd',<210>,1:42], apple=[@1,7:11='apple',<210>,1:7]}, interface={apple={column={name=apple, table_ref=null}}}}}",
				extractor.getSymbolTable().toString());
	}
	
	@Test
	public void notLikeCondition1WithColumnTest() {
		//Item 53 - not like AND SIMILAR FAILS TO BUILD TREE
		final String query = "SELECT apple"
				+ " from tab1 where subj_cd not  like subj_cd";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=apple, table_ref=null}}}, from={table={alias=null, table=tab1}}, where={condition={left={column={name=subj_cd, table_ref=null}}, right={column={name=subj_cd, table_ref=null}}, operator=not_like}}}}",
				extractor.getSqlTree().toString());
		Assert.assertEquals("Interface is wrong", "[apple]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={subj_cd=[@8,47:53='subj_cd',<210>,1:47], apple=[@1,7:11='apple',<210>,1:7]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={tab1={subj_cd=[@8,47:53='subj_cd',<210>,1:47], apple=[@1,7:11='apple',<210>,1:7]}, interface={apple={column={name=apple, table_ref=null}}}}}",
				extractor.getSymbolTable().toString());
	}
	
	@Test
	public void likeCondition2Test() {
		// Item 21 - Not parsing any predicand after the LIKE, only string literals
		final String query = "SELECT apple"
				+ " from tab1 where subj_cd like lower('%STUFF%')";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=apple, table_ref=null}}}, from={table={alias=null, table=tab1}}, where={condition={left={column={name=subj_cd, table_ref=null}}, right={function={parameters={1={literal='%STUFF%'}}, function_name=lower}}, operator=like}}}}",
				extractor.getSqlTree().toString());
		Assert.assertEquals("Interface is wrong", "[apple]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={subj_cd=[@5,29:35='subj_cd',<210>,1:29], apple=[@1,7:11='apple',<210>,1:7]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={tab1={subj_cd=[@5,29:35='subj_cd',<210>,1:29], apple=[@1,7:11='apple',<210>,1:7]}, interface={apple={column={name=apple, table_ref=null}}}}}",
				extractor.getSymbolTable().toString());
	}
	
	@Test
	public void likeConditionWithSubstitutionV1Test() {
		//  Item 42 - predicand before Like not properly recognized
		final String query = "SELECT apple"
				+ " from tab1 where <subj_cd> like '%STUFF%'";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=apple, table_ref=null}}}, from={table={alias=null, table=tab1}}, where={condition={left={substitution={name=<subj_cd>, type=predicand}}, right={literal='%STUFF%'}, operator=like}}}}",
				extractor.getSqlTree().toString());
		Assert.assertEquals("Interface is wrong", "[apple]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<subj_cd>=predicand}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={apple=[@1,7:11='apple',<210>,1:7]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={tab1={apple=[@1,7:11='apple',<210>,1:7]}, interface={apple={column={name=apple, table_ref=null}}}}}",
				extractor.getSymbolTable().toString());
	}
	
	@Test
	public void likeConditionWithSubstitutionV2Test() {
		// Item 41 - Not parsing any predicand after the LIKE, only string literals
		final String query = "SELECT apple"
				+ " from tab1 where subj_cd like <predicand>";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=apple, table_ref=null}}}, from={table={alias=null, table=tab1}}, where={condition={left={column={name=subj_cd, table_ref=null}}, right={substitution={name=<predicand>, type=predicand}}, operator=like}}}}",
				extractor.getSqlTree().toString());
		Assert.assertEquals("Interface is wrong", "[apple]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<predicand>=predicand}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={subj_cd=[@5,29:35='subj_cd',<210>,1:29], apple=[@1,7:11='apple',<210>,1:7]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={tab1={subj_cd=[@5,29:35='subj_cd',<210>,1:29], apple=[@1,7:11='apple',<210>,1:7]}, interface={apple={column={name=apple, table_ref=null}}}}}",
				extractor.getSymbolTable().toString());
	}
	
	// END OF WHERE CLAUSE CONDITIONS
	// CASE STATEMENTS

	@Test
	public void basicCaseConditionConstantsTest() {
		String sql = "case when true then 'Y' when false then 'N' else 'N' end";
		final SQLSelectParserParser parser = parse(sql);
		
		SqlParseEventWalker extractor = runPredicandParsertest(sql, parser);
		
		Assert.assertEquals("AST is wrong", "{PREDICAND={case={clauses={1={then={literal='Y'}, when={literal=true}}, 2={then={literal='N'}, when={literal=false}}}, else={literal='N'}}}}",
				extractor.getSqlTree().toString());
		Assert.assertEquals("Interface is wrong", "[]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void basicCaseExplicitConditionExpressionTest() {
		String sql = "case when column1 = true then 'Y' when column2 = false then 'N' else 'N' end";
		final SQLSelectParserParser parser = parse(sql);
		
		SqlParseEventWalker extractor = runPredicandParsertest(sql, parser);
		
		Assert.assertEquals("AST is wrong", "{PREDICAND={case={clauses={1={then={literal='Y'}, when={condition={left={column={name=column1, table_ref=null}}, right={literal=true}, operator==}}}, 2={then={literal='N'}, when={condition={left={column={name=column2, table_ref=null}}, right={literal=false}, operator==}}}}, else={literal='N'}}}}",
				extractor.getSqlTree().toString());
		Assert.assertEquals("Interface is wrong", "[]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{unknown={column1=[@2,10:16='column1',<210>,1:10], column2=[@8,39:45='column2',<210>,1:39]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{unknown={column1=[@2,10:16='column1',<210>,1:10], column2=[@8,39:45='column2',<210>,1:39]}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void basicCaseImpliedConditionExpressionV1Test() {
		String sql = "case column1 when true then 'Y' when false then 'N' else 'N' end";
		final SQLSelectParserParser parser = parse(sql);
		
		SqlParseEventWalker extractor = runPredicandParsertest(sql, parser);
		
		Assert.assertEquals("AST is wrong", "{PREDICAND={case={item={column={name=column1, table_ref=null}}, clauses={1={then={literal='Y'}, when={literal=true}}, 2={then={literal='N'}, when={literal=false}}}, else={literal='N'}}}}",
				extractor.getSqlTree().toString());
		Assert.assertEquals("Interface is wrong", "[]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{unknown={column1=[@1,5:11='column1',<210>,1:5]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{unknown={column1=[@1,5:11='column1',<210>,1:5]}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void basicCaseImpliedColumnExpressionV2Test() {
		String sql = "case column1 when column2 then 'Y' when column3 then 'N' else 'N' end";
		final SQLSelectParserParser parser = parse(sql);
		
		SqlParseEventWalker extractor = runPredicandParsertest(sql, parser);
		
		Assert.assertEquals("AST is wrong", "{PREDICAND={case={item={column={name=column1, table_ref=null}}, clauses={1={then={literal='Y'}, when={column={name=column2, table_ref=null}}}, 2={then={literal='N'}, when={column={name=column3, table_ref=null}}}}, else={literal='N'}}}}",
				extractor.getSqlTree().toString());
		Assert.assertEquals("Interface is wrong", "[]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{unknown={column1=[@1,5:11='column1',<210>,1:5], column3=[@7,40:46='column3',<210>,1:40], column2=[@3,18:24='column2',<210>,1:18]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{unknown={column1=[@1,5:11='column1',<210>,1:5], column3=[@7,40:46='column3',<210>,1:40], column2=[@3,18:24='column2',<210>,1:18]}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void complexCaseImpliedConditionExpressionWithPredicandSubstitutionPos1Test() {
		// TODO: Item 27 - Substitution variable <item> does not get the right type, should be PREDICAND because of the type of CASE STMT
		// Item 50 - Table Dictionary is not created when the Predicand is parsed on its own
		final String query = "CASE observation_time WHEN s948.OBSERVATION_TM THEN S948.t_student_last_name "
				+ " WHEN <item> THEN S949.t_student_last_name "
				+ " ELSE COALESCE(S948.t_student_last_name, S949.t_student_last_name) END";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runPredicandParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{PREDICAND={case={item={column={name=observation_time, table_ref=null}}, clauses={1={then={column={name=t_student_last_name, table_ref=S948}}, when={column={name=OBSERVATION_TM, table_ref=s948}}}, 2={then={column={name=t_student_last_name, table_ref=S949}}, when={substitution={name=<item>, type=predicand}}}}, else={function={parameters={1={column={name=t_student_last_name, table_ref=S948}}, 2={column={name=t_student_last_name, table_ref=S949}}}, function_name=COALESCE}}}}}",
				extractor.getSqlTree().toString());
		Assert.assertEquals("Interface is wrong", "[]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<item>=predicand}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{s949={t_student_last_name=[@23,161:164='S949',<210>,1:161]}, s948={OBSERVATION_TM=[@3,27:30='s948',<210>,1:27], t_student_last_name=[@19,135:138='S948',<210>,1:135]}, unknown={observation_time=[@1,5:20='observation_time',<210>,1:5]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{S948={t_student_last_name=[@19,135:138='S948',<210>,1:135]}, S949={t_student_last_name=[@23,161:164='S949',<210>,1:161]}, unknown={observation_time=[@1,5:20='observation_time',<210>,1:5]}, s948={OBSERVATION_TM=[@3,27:30='s948',<210>,1:27]}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void complexCaseImpliedConditionExpressionWithPredicandSubstitutionPos2Test() {
		// TODO:  Item 27 - Substitution variable <column2> does not get the right type, should be PREDICAND because of the type of CASE STMT
		// Item 50 - Table Dictionary is not created when the Predicand is parsed on its own
		String sql = "case <column1> when column2 then 'Y' when column3 then 'N' else 'N' end";
		final SQLSelectParserParser parser = parse(sql);
		
		SqlParseEventWalker extractor = runPredicandParsertest(sql, parser);
		
		Assert.assertEquals("AST is wrong", "{PREDICAND={case={item={substitution={name=<column1>, type=predicand}}, clauses={1={then={literal='Y'}, when={column={name=column2, table_ref=null}}}, 2={then={literal='N'}, when={column={name=column3, table_ref=null}}}}, else={literal='N'}}}}",
				extractor.getSqlTree().toString());
		Assert.assertEquals("Interface is wrong", "[]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<column1>=predicand}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{unknown={column3=[@7,42:48='column3',<210>,1:42], column2=[@3,20:26='column2',<210>,1:20]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{unknown={column3=[@7,42:48='column3',<210>,1:42], column2=[@3,20:26='column2',<210>,1:20]}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void complexCaseImpliedConditionExpressionWithPredicandSubstitutionPos3Test() {
		// TODO:  Item 30 - Predicand substitution not typed nor included in the Substitution Table
		String sql = "case column1 when column2 then 'Y' when column3 then <column4> else 'N' end";
		final SQLSelectParserParser parser = parse(sql);
		
		SqlParseEventWalker extractor = runPredicandParsertest(sql, parser);
		
		Assert.assertEquals("AST is wrong", "{PREDICAND={case={item={column={name=column1, table_ref=null}}, clauses={1={then={literal='Y'}, when={column={name=column2, table_ref=null}}}, 2={then={substitution={name=<column4>, type=predicand}}, when={column={name=column3, table_ref=null}}}}, else={literal='N'}}}}",
				extractor.getSqlTree().toString());
		Assert.assertEquals("Interface is wrong", "[]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<column4>=predicand}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{unknown={column1=[@1,5:11='column1',<210>,1:5], column3=[@7,40:46='column3',<210>,1:40], column2=[@3,18:24='column2',<210>,1:18]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{unknown={column1=[@1,5:11='column1',<210>,1:5], column3=[@7,40:46='column3',<210>,1:40], column2=[@3,18:24='column2',<210>,1:18]}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void complexCaseImpliedConditionExpressionWithPredicandSubstitutionPos4Test() {
		// TODO:  Item 30 - Predicand substitution not typed nor included in the Substitution Table
		String sql = "case column1 when column2 then 'Y' when column3 then column4 else <column5> end";
		final SQLSelectParserParser parser = parse(sql);
		
		SqlParseEventWalker extractor = runPredicandParsertest(sql, parser);
		
		Assert.assertEquals("AST is wrong", "{PREDICAND={case={item={column={name=column1, table_ref=null}}, clauses={1={then={literal='Y'}, when={column={name=column2, table_ref=null}}}, 2={then={column={name=column4, table_ref=null}}, when={column={name=column3, table_ref=null}}}}, else={substitution={name=<column5>, type=predicand}}}}}",
				extractor.getSqlTree().toString());
		Assert.assertEquals("Interface is wrong", "[]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<column5>=predicand}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{unknown={column1=[@1,5:11='column1',<210>,1:5], column4=[@9,53:59='column4',<210>,1:53], column3=[@7,40:46='column3',<210>,1:40], column2=[@3,18:24='column2',<210>,1:18]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{unknown={column1=[@1,5:11='column1',<210>,1:5], column4=[@9,53:59='column4',<210>,1:53], column3=[@7,40:46='column3',<210>,1:40], column2=[@3,18:24='column2',<210>,1:18]}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void complexCaseExplicitConditionExpressionWithPredicandSubstitutionPos1Test() {
		String sql = "case when <column1> = true then 'Y' when column2 = false then 'N' else 'N' end";
		final SQLSelectParserParser parser = parse(sql);
		
		SqlParseEventWalker extractor = runPredicandParsertest(sql, parser);
		
		Assert.assertEquals("AST is wrong", "{PREDICAND={case={clauses={1={then={literal='Y'}, when={condition={left={substitution={name=<column1>, type=predicand}}, right={literal=true}, operator==}}}, 2={then={literal='N'}, when={condition={left={column={name=column2, table_ref=null}}, right={literal=false}, operator==}}}}, else={literal='N'}}}}",
				extractor.getSqlTree().toString());
		Assert.assertEquals("Interface is wrong", "[]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<column1>=predicand}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{unknown={column2=[@8,41:47='column2',<210>,1:41]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{unknown={column2=[@8,41:47='column2',<210>,1:41]}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void complexCaseExplicitConditionExpressionWithPredicandSubstitutionPos2Test() {
		String sql = "case when a.<column1> = 700 then 'Y' when a.column2 = 800 then 'N' else 'N' end";
		final SQLSelectParserParser parser = parse(sql);
		
		SqlParseEventWalker extractor = runPredicandParsertest(sql, parser);
		
		Assert.assertEquals("AST is wrong", "{PREDICAND={case={clauses={1={then={literal='Y'}, when={condition={left={column={substitution={name=<column1>, type=column}, table_ref=a}}, right={literal=700}, operator==}}}, 2={then={literal='N'}, when={condition={left={column={name=column2, table_ref=a}}, right={literal=800}, operator==}}}}, else={literal='N'}}}}",
				extractor.getSqlTree().toString());
		Assert.assertEquals("Interface is wrong", "[]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<column1>=column}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{a={<column1>={substitution={name=<column1>, type=column}}, column2=[@10,42:42='a',<210>,1:42]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{a={column2=[@10,42:42='a',<210>,1:42], <column1>={substitution={name=<column1>, type=column}}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void complexCaseExplicitConditionExpressionWithPredicandSubstitutionPos3Test() {
		String sql = "case when <column1> then 'Y' when column2 = false then 'N' else 'N' end";
		final SQLSelectParserParser parser = parse(sql);
		
		SqlParseEventWalker extractor = runPredicandParsertest(sql, parser);
		
		Assert.assertEquals("AST is wrong", "{PREDICAND={case={clauses={1={then={literal='Y'}, when={substitution={name=<column1>, type=condition}}}, 2={then={literal='N'}, when={condition={left={column={name=column2, table_ref=null}}, right={literal=false}, operator==}}}}, else={literal='N'}}}}",
				extractor.getSqlTree().toString());
		Assert.assertEquals("Interface is wrong", "[]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<column1>=condition}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{unknown={column2=[@6,34:40='column2',<210>,1:34]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{unknown={column2=[@6,34:40='column2',<210>,1:34]}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void complexCaseExplicitConditionExpressionWithPredicandSubstitutionPos4Test() {
		// Item 30 - Predicand substitution not typed nor included in the Substitution Table
		String sql = "case when a.column1 = 700 then 'Y' when a.column2 = 800 then <predicand> else 'N' end";
		final SQLSelectParserParser parser = parse(sql);
		
		SqlParseEventWalker extractor = runPredicandParsertest(sql, parser);
		
		Assert.assertEquals("AST is wrong", "{PREDICAND={case={clauses={1={then={literal='Y'}, when={condition={left={column={name=column1, table_ref=a}}, right={literal=700}, operator==}}}, 2={then={substitution={name=<predicand>, type=predicand}}, when={condition={left={column={name=column2, table_ref=a}}, right={literal=800}, operator==}}}}, else={literal='N'}}}}",
				extractor.getSqlTree().toString());
		Assert.assertEquals("Interface is wrong", "[]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<predicand>=predicand}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{a={column1=[@2,10:10='a',<210>,1:10], column2=[@10,40:40='a',<210>,1:40]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{a={column1=[@2,10:10='a',<210>,1:10], column2=[@10,40:40='a',<210>,1:40]}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void complexCaseExplicitConditionExpressionWithPredicandSubstitutionPos5Test() {
		// Item 30 - Predicand substitution not typed nor included in the Substitution Table
		String sql = "case when a.column1 = 700 then 'Y' when a.column2 = 800 then 'N' else <predicand> end";
		final SQLSelectParserParser parser = parse(sql);
		
		SqlParseEventWalker extractor = runPredicandParsertest(sql, parser);
		
		Assert.assertEquals("AST is wrong", "{PREDICAND={case={clauses={1={then={literal='Y'}, when={condition={left={column={name=column1, table_ref=a}}, right={literal=700}, operator==}}}, 2={then={literal='N'}, when={condition={left={column={name=column2, table_ref=a}}, right={literal=800}, operator==}}}}, else={substitution={name=<predicand>, type=predicand}}}}}",
				extractor.getSqlTree().toString());
		Assert.assertEquals("Interface is wrong", "[]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<predicand>=predicand}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{a={column1=[@2,10:10='a',<210>,1:10], column2=[@10,40:40='a',<210>,1:40]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{a={column1=[@2,10:10='a',<210>,1:10], column2=[@10,40:40='a',<210>,1:40]}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void complexCaseExplicitConditionExpressionWithPredicandSubstitutionPos6Test() {
		String sql = "case when a.column1 = 700 then 'Y' when a.column2 = 800 then a.<column4> else 'N' end";
		final SQLSelectParserParser parser = parse(sql);
		
		SqlParseEventWalker extractor = runPredicandParsertest(sql, parser);
		
		Assert.assertEquals("AST is wrong", "{PREDICAND={case={clauses={1={then={literal='Y'}, when={condition={left={column={name=column1, table_ref=a}}, right={literal=700}, operator==}}}, 2={then={column={substitution={name=<column4>, type=column}, table_ref=a}}, when={condition={left={column={name=column2, table_ref=a}}, right={literal=800}, operator==}}}}, else={literal='N'}}}}",
				extractor.getSqlTree().toString());
		Assert.assertEquals("Interface is wrong", "[]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<column4>=column}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{a={column1=[@2,10:10='a',<210>,1:10], <column4>={substitution={name=<column4>, type=column}}, column2=[@10,40:40='a',<210>,1:40]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{a={column1=[@2,10:10='a',<210>,1:10], column2=[@10,40:40='a',<210>,1:40], <column4>={substitution={name=<column4>, type=column}}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void complexCaseExplicitConditionExpressionWithPredicandSubstitutionPos7Test() {
		String sql = "case when a.column1 = 700 then 'Y' when a.column2 = 800 then 'N' else a.<column4> end";
		final SQLSelectParserParser parser = parse(sql);
		
		SqlParseEventWalker extractor = runPredicandParsertest(sql, parser);
		
		Assert.assertEquals("AST is wrong", "{PREDICAND={case={clauses={1={then={literal='Y'}, when={condition={left={column={name=column1, table_ref=a}}, right={literal=700}, operator==}}}, 2={then={literal='N'}, when={condition={left={column={name=column2, table_ref=a}}, right={literal=800}, operator==}}}}, else={column={substitution={name=<column4>, type=column}, table_ref=a}}}}}",
				extractor.getSqlTree().toString());
		Assert.assertEquals("Interface is wrong", "[]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<column4>=column}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{a={column1=[@2,10:10='a',<210>,1:10], <column4>={substitution={name=<column4>, type=column}}, column2=[@10,40:40='a',<210>,1:40]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{a={column1=[@2,10:10='a',<210>,1:10], column2=[@10,40:40='a',<210>,1:40], <column4>={substitution={name=<column4>, type=column}}}}",
				extractor.getSymbolTable().toString());
	}

	// Various other case examples
	@Test
	public void caseExpressionStatementParseTest() {
		final String query = " SELECT CASE WHEN a < b THEN 'Y' WHEN a = b THEN 'N' "
				+ " ELSE 'N' END as case_one " 
				+ " FROM sgbstdn ";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=case_one, case={clauses={1={then={literal='Y'}, when={condition={left={column={name=a, table_ref=null}}, right={column={name=b, table_ref=null}}, operator=<}}}, 2={then={literal='N'}, when={condition={left={column={name=a, table_ref=null}}, right={column={name=b, table_ref=null}}, operator==}}}}, else={literal='N'}}}}, from={table={alias=null, table=sgbstdn}}}}",
				extractor.getSqlTree().toString());
		Assert.assertEquals("Interface is wrong", "[case_one]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{sgbstdn={a=[@9,38:38='a',<210>,1:38], b=[@11,42:42='b',<210>,1:42]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={sgbstdn={a=[@9,38:38='a',<210>,1:38], b=[@11,42:42='b',<210>,1:42]}, interface={case_one={case={clauses={1={then={literal='Y'}, when={condition={left={column={name=a, table_ref=null}}, right={column={name=b, table_ref=null}}, operator=<}}}, 2={then={literal='N'}, when={condition={left={column={name=a, table_ref=null}}, right={column={name=b, table_ref=null}}, operator==}}}}, else={literal='N'}}}}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void complexCaseFunctionTest() {

		final String query = " SELECT " + " CASE   " + " WHEN s948.OBSERVATION_TM THEN S948.t_student_last_name   "
				+ " WHEN COALESCE( S949.OBSERVATION_TM>=S948.OBSERVATION_TM , FALSE) THEN S949.t_student_last_name   "
				+ " ELSE COALESCE(S948.t_student_last_name, S949.t_student_last_name) END AS t_student_last_name "
				+ " FROM my.234 as s948, my.other5 as s949";

		final SQLSelectParserParser parser = parse(query);
		runParsertest(query, parser);
	}

	@Test
	public void caseStatementParseTest() {

		final String query = " SELECT CASE WHEN true THEN 'Y' " + "  WHEN false THEN 'N' "
				+ " ELSE 'N' END as case_one, " + " CASE  col WHEN 'a' THEN 'b'	 " + " ELSE null END as case_two "
				+ " FROM sgbstdn ";

		final SQLSelectParserParser parser = parse(query);
		runParsertest(query, parser);
	}

	@Test
	public void getMajorSqlTest() {
		/*
		 * Major COLUMNS: RECORD_TYPE, ACTION, EXTERNAL_ID, NAME
		 */
		String query = "select record_type as RECORD_TYPE,action as ACTION,"
				+ "trim(external_id) as EXTERNAL_ID,case when name is null or length(trim(name)) = 0 then 'Major name not available' else trim(name) end as NAME from "
				+ " majorTbl where external_id is not null and length(trim(external_id)) > 0";
		final SQLSelectParserParser parser = parse(query);
		runParsertest(query, parser);
	}

	// END OF CASE STATEMENTS
	// TRIM Function Statements

	@Test
	public void trimFunctionVariationsTest() {
		final String query = "SELECT trim(leading '0' from field1), trim('0' || field2,'0') "
				+ " FROM scbcrse";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={function={function_name=trim, parameters={qualifier=leading, trim_character={literal='0'}, value={column={name=field1, table_ref=null}}}}}, 2={function={parameters={1={concatenate={1={literal='0'}, 2={column={name=field2, table_ref=null}}}}, 2={literal='0'}}, function_name=trim}}}, from={table={alias=null, table=scbcrse}}}}",
				extractor.getSqlTree().toString());
		Assert.assertEquals("Interface is wrong", "[unnamed_1, unnamed_0]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{scbcrse={field2=[@13,50:55='field2',<210>,1:50], field1=[@6,29:34='field1',<210>,1:29]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={scbcrse={field2=[@13,50:55='field2',<210>,1:50], field1=[@6,29:34='field1',<210>,1:29]}, interface={unnamed_1={function={parameters={1={concatenate={1={literal='0'}, 2={column={name=field2, table_ref=null}}}}, 2={literal='0'}}, function_name=trim}}, unnamed_0={function={function_name=trim, parameters={qualifier=leading, trim_character={literal='0'}, value={column={name=field1, table_ref=null}}}}}}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void trimFunctionColumnSubstitutionsTest() {
		// Item 38 - Trim Functions recognize column and predicand variables
		final String query = "SELECT trim(leading '0' from a.<field1>), trim('0' || a.<field2>,'0') "
				+ " FROM scbcrse as a";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={function={function_name=trim, parameters={qualifier=leading, trim_character={literal='0'}, value={column={substitution={name=<field1>, type=column}, table_ref=a}}}}}, 2={function={parameters={1={concatenate={1={literal='0'}, 2={column={substitution={name=<field2>, type=column}, table_ref=a}}}}, 2={literal='0'}}, function_name=trim}}}, from={table={alias=a, table=scbcrse}}}}",
				extractor.getSqlTree().toString());
		Assert.assertEquals("Interface is wrong", "[unnamed_1, unnamed_0]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<field2>=column, <field1>=column}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{scbcrse={<field2>={substitution={name=<field2>, type=column}}, <field1>={substitution={name=<field1>, type=column}}}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={a=scbcrse, scbcrse={<field2>={substitution={name=<field2>, type=column}}, <field1>={substitution={name=<field1>, type=column}}}, interface={unnamed_1={function={parameters={1={concatenate={1={literal='0'}, 2={column={substitution={name=<field2>, type=column}, table_ref=a}}}}, 2={literal='0'}}, function_name=trim}}, unnamed_0={function={function_name=trim, parameters={qualifier=leading, trim_character={literal='0'}, value={column={substitution={name=<field1>, type=column}, table_ref=a}}}}}}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void trimFunctionPredicandSubstitutionsTest() {
		// Item 38 - Trim Functions recognize column and predicand variables
		final String query = "SELECT trim(leading '0' from <field1>), trim(<field2>,'0') "
				+ " FROM scbcrse as a";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={function={function_name=trim, parameters={qualifier=leading, trim_character={literal='0'}, value={substitution={name=<field1>, type=predicand}}}}}, 2={function={parameters={1={substitution={name=<field2>, type=predicand}}, 2={literal='0'}}, function_name=trim}}}, from={table={alias=a, table=scbcrse}}}}",
				extractor.getSqlTree().toString());
		Assert.assertEquals("Interface is wrong", "[unnamed_1, unnamed_0]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<field2>=predicand, <field1>=predicand}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{scbcrse={}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={a=scbcrse, scbcrse={}, interface={unnamed_1={function={parameters={1={substitution={name=<field2>, type=predicand}}, 2={literal='0'}}, function_name=trim}}, unnamed_0={function={function_name=trim, parameters={qualifier=leading, trim_character={literal='0'}, value={substitution={name=<field1>, type=predicand}}}}}}}}",
				extractor.getSymbolTable().toString());
	}

	// end of trim functions
	// ORDER BY Clauses
	
	@Test
	public void basicOrderByTest() {
		final String query = "SELECT apple, fruit_cd from tab1 order by apple, 2, fruit_cd + 1";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=apple, table_ref=null}}, 2={column={name=fruit_cd, table_ref=null}}}, orderby={1={null_order=null, predicand={column={name=apple, table_ref=null}}, sort_order=ASC}, 2={null_order=null, predicand={literal=2}, sort_order=ASC}, 3={null_order=null, predicand={calc={left={column={name=fruit_cd, table_ref=null}}, right={literal=1}, operator=+}}, sort_order=ASC}}, from={table={alias=null, table=tab1}}}}",
				extractor.getSqlTree().toString());
		Assert.assertEquals("Interface is wrong", "[apple, fruit_cd]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={apple=[@8,42:46='apple',<210>,1:42], fruit_cd=[@12,52:59='fruit_cd',<210>,1:52]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={tab1={apple=[@8,42:46='apple',<210>,1:42], fruit_cd=[@12,52:59='fruit_cd',<210>,1:52]}, interface={apple={column={name=apple, table_ref=null}}, fruit_cd={column={name=fruit_cd, table_ref=null}}}}}",
				extractor.getSymbolTable().toString());
	}
	
	@Test
	public void basicOrderByWithPredicandVariableTest() {
		// Item 36 - Predicand Variable in Order By
		final String query = "SELECT apple, fruit_cd from tab1 order by <predicand variable> desc, fruit_cd";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=apple, table_ref=null}}, 2={column={name=fruit_cd, table_ref=null}}}, orderby={1={null_order=null, predicand={substitution={name=<predicand variable>, type=predicand}}, sort_order=desc}, 2={null_order=null, predicand={column={name=fruit_cd, table_ref=null}}, sort_order=ASC}}, from={table={alias=null, table=tab1}}}}",
				extractor.getSqlTree().toString());
		Assert.assertEquals("Interface is wrong", "[apple, fruit_cd]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<predicand variable>=predicand}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={apple=[@1,7:11='apple',<210>,1:7], fruit_cd=[@11,69:76='fruit_cd',<210>,1:69]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={tab1={apple=[@1,7:11='apple',<210>,1:7], fruit_cd=[@11,69:76='fruit_cd',<210>,1:69]}, interface={apple={column={name=apple, table_ref=null}}, fruit_cd={column={name=fruit_cd, table_ref=null}}}}}",
				extractor.getSymbolTable().toString());
	}
	
	@Test
	public void basicOrderByWithColumnVariableTest() {
		// Item 36 - Column Variable in Order By
		final String query = "SELECT apple, fruit_cd from tab1 order by tab1.<column variable> desc, fruit_cd";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=apple, table_ref=null}}, 2={column={name=fruit_cd, table_ref=null}}}, orderby={1={null_order=null, predicand={column={substitution={name=<column variable>, type=column}, table_ref=tab1}}, sort_order=desc}, 2={null_order=null, predicand={column={name=fruit_cd, table_ref=null}}, sort_order=ASC}}, from={table={alias=null, table=tab1}}}}",
				extractor.getSqlTree().toString());
		Assert.assertEquals("Interface is wrong", "[apple, fruit_cd]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<column variable>=column}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={apple=[@1,7:11='apple',<210>,1:7], <column variable>={substitution={name=<column variable>, type=column}}, fruit_cd=[@13,71:78='fruit_cd',<210>,1:71]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={tab1={apple=[@1,7:11='apple',<210>,1:7], <column variable>={substitution={name=<column variable>, type=column}}, fruit_cd=[@13,71:78='fruit_cd',<210>,1:71]}, interface={apple={column={name=apple, table_ref=null}}, fruit_cd={column={name=fruit_cd, table_ref=null}}}}}",
				extractor.getSymbolTable().toString());
	}
	
	// END OF ORDER BY CLAUSES
	// AGGREGATE QUERIES
	
	@Test
	public void basicAggregateQueryTest() {
		final String query = "SELECT apple, count(*) from tab1 group by apple";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=apple, table_ref=null}}, 2={function={function_name=COUNT, qualifier=null, parameters=*}}}, from={table={alias=null, table=tab1}}, groupby={1={column={name=apple, table_ref=null}}}}}",
				extractor.getSqlTree().toString());
		Assert.assertEquals("Interface is wrong", "[apple, unnamed_0]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={apple=[@11,42:46='apple',<210>,1:42]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={tab1={apple=[@11,42:46='apple',<210>,1:42]}, interface={apple={column={name=apple, table_ref=null}}, unnamed_0={function={function_name=COUNT, qualifier=null, parameters=*}}}}}",
				extractor.getSymbolTable().toString());
	}
	
	@Test
	public void basicAggregateQueryWithColumnVariableTest() {
		// Item 37 - Group by recognizes Column variables
		// Note, this query is semantically incorrect because it does not include the same unaggregated columns in the select and group by
		final String query = "SELECT apple, count(*) from tab1 group by tab1.<other>";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=apple, table_ref=null}}, 2={function={function_name=COUNT, qualifier=null, parameters=*}}}, from={table={alias=null, table=tab1}}, groupby={1={column={substitution={name=<other>, type=column}, table_ref=tab1}}}}}",
				extractor.getSqlTree().toString());
		Assert.assertEquals("Interface is wrong", "[apple, unnamed_0]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<other>=column}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={apple=[@1,7:11='apple',<210>,1:7], <other>={substitution={name=<other>, type=column}}}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={tab1={apple=[@1,7:11='apple',<210>,1:7], <other>={substitution={name=<other>, type=column}}}, interface={apple={column={name=apple, table_ref=null}}, unnamed_0={function={function_name=COUNT, qualifier=null, parameters=*}}}}}",
				extractor.getSymbolTable().toString());
	}
	
	@Test
	public void basicAggregateQueryWithMultiplePredicandsTest() {
		// Note, this query is semantically incorrect because it does not include the same unaggregated columns in the select and group by
		final String query = "SELECT apple, count(*) from tab1 group by tab1.<other>, <predicand>, (a+b*c)";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=apple, table_ref=null}}, 2={function={function_name=COUNT, qualifier=null, parameters=*}}}, from={table={alias=null, table=tab1}}, groupby={1={column={substitution={name=<other>, type=column}, table_ref=tab1}}, 2={substitution={name=<predicand>, type=predicand}}, 3={parentheses={calc={left={column={name=a, table_ref=null}}, right={calc={left={column={name=b, table_ref=null}}, right={column={name=c, table_ref=null}}, operator=*}}, operator=+}}}}}}",
				extractor.getSqlTree().toString());
		Assert.assertEquals("Interface is wrong", "[apple, unnamed_0]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<predicand>=predicand, <other>=column}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={apple=[@1,7:11='apple',<210>,1:7], a=[@18,70:70='a',<210>,1:70], <other>={substitution={name=<other>, type=column}}, b=[@20,72:72='b',<210>,1:72], c=[@22,74:74='c',<210>,1:74]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={tab1={apple=[@1,7:11='apple',<210>,1:7], a=[@18,70:70='a',<210>,1:70], b=[@20,72:72='b',<210>,1:72], c=[@22,74:74='c',<210>,1:74], <other>={substitution={name=<other>, type=column}}}, interface={apple={column={name=apple, table_ref=null}}, unnamed_0={function={function_name=COUNT, qualifier=null, parameters=*}}}}}",
				extractor.getSymbolTable().toString());
	}
	
	@Test
	public void basicAggregateQueryWithPredicandVariableTest() {
		// Item 37 - Group by recognizes Predicand variables
		// Note, this query is semantically incorrect because it does not include the same unaggregated columns in the select and group by
		final String query = "SELECT apple, count(*) from tab1 group by <other>";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=apple, table_ref=null}}, 2={function={function_name=COUNT, qualifier=null, parameters=*}}}, from={table={alias=null, table=tab1}}, groupby={1={substitution={name=<other>, type=predicand}}}}}",
				extractor.getSqlTree().toString());
		Assert.assertEquals("Interface is wrong", "[apple, unnamed_0]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<other>=predicand}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={apple=[@1,7:11='apple',<210>,1:7]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={tab1={apple=[@1,7:11='apple',<210>,1:7]}, interface={apple={column={name=apple, table_ref=null}}, unnamed_0={function={function_name=COUNT, qualifier=null, parameters=*}}}}}",
				extractor.getSymbolTable().toString());
	}
	
	@Test
	public void basicAggregateQueryWithCountOverCalcTest() {
		// Count function over calculation
		final String query = "SELECT apple, count(subj + object) from tab1 group by apple";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=apple, table_ref=null}}, 2={function={function_name=count, qualifier=null, parameters={calc={left={column={name=subj, table_ref=null}}, right={column={name=object, table_ref=null}}, operator=+}}}}}, from={table={alias=null, table=tab1}}, groupby={1={column={name=apple, table_ref=null}}}}}",
				extractor.getSqlTree().toString());
		Assert.assertEquals("Interface is wrong", "[apple, unnamed_0]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={apple=[@13,54:58='apple',<210>,1:54], subj=[@5,20:23='subj',<210>,1:20], object=[@7,27:32='object',<210>,1:27]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={tab1={apple=[@13,54:58='apple',<210>,1:54], subj=[@5,20:23='subj',<210>,1:20], object=[@7,27:32='object',<210>,1:27]}, interface={apple={column={name=apple, table_ref=null}}, unnamed_0={function={function_name=count, qualifier=null, parameters={calc={left={column={name=subj, table_ref=null}}, right={column={name=object, table_ref=null}}, operator=+}}}}}}}",
				extractor.getSymbolTable().toString());
	}
	
	@Test
	public void basicAggregateQueryWithCountOverColumnVariableTest() {
		// Item 39 - Count function over Column variables
		final String query = "SELECT apple, count(tab1.<other>) from tab1 group by apple";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=apple, table_ref=null}}, 2={function={function_name=count, qualifier=null, parameters={column={substitution={name=<other>, type=column}, table_ref=tab1}}}}}, from={table={alias=null, table=tab1}}, groupby={1={column={name=apple, table_ref=null}}}}}",
				extractor.getSqlTree().toString());
		Assert.assertEquals("Interface is wrong", "[apple, unnamed_0]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<other>=column}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={apple=[@13,53:57='apple',<210>,1:53], <other>={substitution={name=<other>, type=column}}}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={tab1={apple=[@13,53:57='apple',<210>,1:53], <other>={substitution={name=<other>, type=column}}}, interface={apple={column={name=apple, table_ref=null}}, unnamed_0={function={function_name=count, qualifier=null, parameters={column={substitution={name=<other>, type=column}, table_ref=tab1}}}}}}}",
				extractor.getSymbolTable().toString());
	}
	
	@Test
	public void basicAggregateQueryWithCountOverPredicandVariableTest() {
		// Item 39 - Count function over Predicand variables
		final String query = "SELECT apple, count(<other>) from tab1 group by apple";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=apple, table_ref=null}}, 2={function={function_name=count, qualifier=null, parameters={substitution={name=<other>, type=predicand}}}}}, from={table={alias=null, table=tab1}}, groupby={1={column={name=apple, table_ref=null}}}}}",
				extractor.getSqlTree().toString());
		Assert.assertEquals("Interface is wrong", "[apple, unnamed_0]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<other>=predicand}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={apple=[@11,48:52='apple',<210>,1:48]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={tab1={apple=[@11,48:52='apple',<210>,1:48]}, interface={apple={column={name=apple, table_ref=null}}, unnamed_0={function={function_name=count, qualifier=null, parameters={substitution={name=<other>, type=predicand}}}}}}}",
				extractor.getSymbolTable().toString());
	}
	
	// END OF AGGREGATE QUERIES
	// WINDOW FUNCTIONS
	
	@Test
	public void leadOverPartitionTest() {
		// Item 26 - Window function property "spriden_id" appearing in Interface improperly;
		final String query = "SELECT func(item), lead(code,1) over (partition by spriden_id order by code)"
				+ " from tab1 ";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={function={parameters={1={column={name=item, table_ref=null}}}, function_name=func}}, 2={window_function={over={partition_by={1={column={name=spriden_id, table_ref=null}}}, orderby={1={null_order=null, predicand={column={name=code, table_ref=null}}, sort_order=ASC}}}, function={function_name=lead, parameters={1={column={name=code, table_ref=null}}, 2={literal=1}}}}}}, from={table={alias=null, table=tab1}}}}",
				extractor.getSqlTree().toString());
		Assert.assertEquals("Interface is wrong", "[unnamed_1, unnamed_0]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={item=[@3,12:15='item',<210>,1:12], code=[@19,71:74='code',<210>,1:71], spriden_id=[@16,51:60='spriden_id',<210>,1:51]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={tab1={item=[@3,12:15='item',<210>,1:12], code=[@19,71:74='code',<210>,1:71], spriden_id=[@16,51:60='spriden_id',<210>,1:51]}, interface={unnamed_1={window_function={over={partition_by={1={column={name=spriden_id, table_ref=null}}}, orderby={1={null_order=null, predicand={column={name=code, table_ref=null}}, sort_order=ASC}}}, function={function_name=lead, parameters={1={column={name=code, table_ref=null}}, 2={literal=1}}}}}, unnamed_0={function={parameters={1={column={name=item, table_ref=null}}}, function_name=func}}}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void rankPartitionSyntaxTest() {
		final String query = " SELECT "
				+ " rank() OVER (partition by k_stfd, kppi order by OBSERVATION_TM desc, row_num desc) AS key_rank "
				+ " FROM tab1 as a";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=key_rank, window_function={over={partition_by={1={column={name=k_stfd, table_ref=null}}, 2={column={name=kppi, table_ref=null}}}, orderby={1={null_order=null, predicand={column={name=OBSERVATION_TM, table_ref=null}}, sort_order=desc}, 2={null_order=null, predicand={column={name=row_num, table_ref=null}}, sort_order=desc}}}, function={function_name=rank, parameters=null}}}}, from={table={alias=a, table=tab1}}}}",
				extractor.getSqlTree().toString());
		Assert.assertEquals("Interface is wrong", "[key_rank]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={row_num=[@16,78:84='row_num',<210>,1:78], k_stfd=[@8,35:40='k_stfd',<210>,1:35], kppi=[@10,43:46='kppi',<210>,1:43], OBSERVATION_TM=[@13,57:70='OBSERVATION_TM',<210>,1:57]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={a=tab1, tab1={row_num=[@16,78:84='row_num',<210>,1:78], k_stfd=[@8,35:40='k_stfd',<210>,1:35], kppi=[@10,43:46='kppi',<210>,1:43], OBSERVATION_TM=[@13,57:70='OBSERVATION_TM',<210>,1:57]}, interface={key_rank={window_function={over={partition_by={1={column={name=k_stfd, table_ref=null}}, 2={column={name=kppi, table_ref=null}}}, orderby={1={null_order=null, predicand={column={name=OBSERVATION_TM, table_ref=null}}, sort_order=desc}, 2={null_order=null, predicand={column={name=row_num, table_ref=null}}, sort_order=desc}}}, function={function_name=rank, parameters=null}}}}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void rankWithParameterPartitionSyntaxTest() {
		final String query = " SELECT "
				+ " rank(parm) OVER (partition by k_stfd, kppi order by OBSERVATION_TM desc, row_num desc) AS key_rank "
				+ " FROM tab1 as a";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=key_rank, window_function={over={partition_by={1={column={name=k_stfd, table_ref=null}}, 2={column={name=kppi, table_ref=null}}}, orderby={1={null_order=null, predicand={column={name=OBSERVATION_TM, table_ref=null}}, sort_order=desc}, 2={null_order=null, predicand={column={name=row_num, table_ref=null}}, sort_order=desc}}}, function={function_name=rank, parameters={1={column={name=parm, table_ref=null}}}}}}}, from={table={alias=a, table=tab1}}}}",
				extractor.getSqlTree().toString());
		Assert.assertEquals("Interface is wrong", "[key_rank]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={parm=[@3,14:17='parm',<210>,1:14], row_num=[@17,82:88='row_num',<210>,1:82], k_stfd=[@9,39:44='k_stfd',<210>,1:39], kppi=[@11,47:50='kppi',<210>,1:47], OBSERVATION_TM=[@14,61:74='OBSERVATION_TM',<210>,1:61]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={a=tab1, tab1={parm=[@3,14:17='parm',<210>,1:14], row_num=[@17,82:88='row_num',<210>,1:82], k_stfd=[@9,39:44='k_stfd',<210>,1:39], kppi=[@11,47:50='kppi',<210>,1:47], OBSERVATION_TM=[@14,61:74='OBSERVATION_TM',<210>,1:61]}, interface={key_rank={window_function={over={partition_by={1={column={name=k_stfd, table_ref=null}}, 2={column={name=kppi, table_ref=null}}}, orderby={1={null_order=null, predicand={column={name=OBSERVATION_TM, table_ref=null}}, sort_order=desc}, 2={null_order=null, predicand={column={name=row_num, table_ref=null}}, sort_order=desc}}}, function={function_name=rank, parameters={1={column={name=parm, table_ref=null}}}}}}}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void selectPartitionDownfillTest() {
		String query = " SELECT  "
				+ "   first_value(major_cd) over (partition by student_id, value_partition order by term_row) as major_cd_fill "
				+ " , first_value(college_cd) over (partition by student_id, value_partition order by term_row) as college_cd_fill "
				+ " , first_value(degree_cd) over (partition by student_id, value_partition order by term_row) as degree_cd_fill "
				+ " , first_value(concentration_cd) over (partition by student_id, value_partition order by term_row) as concentration_cd_fill "
				+ " , first_value(major_cd_2) over (partition by student_id, value_partition order by term_row) as major_cd_2_fill "
				+ " , first_value(college_cd_2) over (partition by student_id, value_partition order by term_row) as college_cd_2_fill "
				+ " , first_value(degree_cd_2) over (partition by student_id, value_partition order by term_row) as degree_cd_2_fill "
				+ " , first_value(concentration_cd_2) over (partition by student_id, value_partition order by term_row) as concentration_cd_2_fill "
				+ " FROM student_term_major where major_cd is null";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("Interface is wrong", "[degree_cd_2_fill, concentration_cd_fill, college_cd_fill, major_cd_2_fill, college_cd_2_fill, degree_cd_fill, concentration_cd_2_fill, major_cd_fill]", 
				extractor.getInterface().toString());
	}

	@Test
	public void windowFunctionPredicandTest() {
		String sql = "rank() OVER (partition by k_stfd, kppi order by OBSERVATION_TM desc, row_num desc)";
		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runPredicandParsertest(sql, parser);
		
		Assert.assertEquals("AST is wrong", "{PREDICAND={window_function={over={partition_by={1={column={name=k_stfd, table_ref=null}}, 2={column={name=kppi, table_ref=null}}}, orderby={1={null_order=null, predicand={column={name=OBSERVATION_TM, table_ref=null}}, sort_order=desc}, 2={null_order=null, predicand={column={name=row_num, table_ref=null}}, sort_order=desc}}}, function={function_name=rank, parameters=null}}}}",
				extractor.getSqlTree().toString());
		Assert.assertEquals("Interface is wrong", "[]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{unknown={row_num=[@15,69:75='row_num',<210>,1:69], k_stfd=[@7,26:31='k_stfd',<210>,1:26], kppi=[@9,34:37='kppi',<210>,1:34], OBSERVATION_TM=[@12,48:61='OBSERVATION_TM',<210>,1:48]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{unknown={k_stfd=[@7,26:31='k_stfd',<210>,1:26], row_num=[@15,69:75='row_num',<210>,1:69], kppi=[@9,34:37='kppi',<210>,1:34], OBSERVATION_TM=[@12,48:61='OBSERVATION_TM',<210>,1:48]}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void windowFunctionColumnVariableP1Test() {
		// Item 52 - Partition clause doesn't take column references with table references/aliases
		String sql = "rank(a.<columnParam>) OVER (partition by a.k_stfd, a.kppi order by a.row_num desc)";
		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runPredicandParsertest(sql, parser);
		
		Assert.assertEquals("AST is wrong", "{PREDICAND={window_function={over={partition_by={1={column={name=k_stfd, table_ref=a}}, 2={column={name=kppi, table_ref=a}}}, orderby={1={null_order=null, predicand={column={name=row_num, table_ref=a}}, sort_order=desc}}}, function={function_name=rank, parameters={1={column={substitution={name=<columnParam>, type=column}, table_ref=a}}}}}}}",
				extractor.getSqlTree().toString());
		Assert.assertEquals("Interface is wrong", "[]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<columnParam>=column}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{a={<columnParam>={substitution={name=<columnParam>, type=column}}, row_num=[@19,67:67='a',<210>,1:67], k_stfd=[@10,41:41='a',<210>,1:41], kppi=[@14,51:51='a',<210>,1:51]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{a={<columnParam>={substitution={name=<columnParam>, type=column}}, k_stfd=[@10,41:41='a',<210>,1:41], row_num=[@19,67:67='a',<210>,1:67], kppi=[@14,51:51='a',<210>,1:51]}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void windowFunctionColumnVariableP2Test() {
		// Item 52 - Partition clause doesn't take column references with table references/aliases
		String sql = "rank(a.column) OVER (partition by a.<k_stfd>, a.kppi order by a.row_num desc)";
		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runPredicandParsertest(sql, parser);
		
		Assert.assertEquals("AST is wrong", "{PREDICAND={window_function={over={partition_by={1={column={substitution={name=<k_stfd>, type=column}, table_ref=a}}, 2={column={name=kppi, table_ref=a}}}, orderby={1={null_order=null, predicand={column={name=row_num, table_ref=a}}, sort_order=desc}}}, function={function_name=rank, parameters={1={column={name=column, table_ref=a}}}}}}}",
				extractor.getSqlTree().toString());
		Assert.assertEquals("Interface is wrong", "[]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<k_stfd>=column}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{a={column=[@2,5:5='a',<210>,1:5], row_num=[@19,62:62='a',<210>,1:62], kppi=[@14,46:46='a',<210>,1:46], <k_stfd>={substitution={name=<k_stfd>, type=column}}}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{a={<k_stfd>={substitution={name=<k_stfd>, type=column}}, column=[@2,5:5='a',<210>,1:5], row_num=[@19,62:62='a',<210>,1:62], kppi=[@14,46:46='a',<210>,1:46]}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void windowFunctionColumnVariableP3Test() {
		// Item 52 - Partition clause doesn't take column references with table references/aliases
		String sql = "rank(a.column) OVER (partition by a.k_stfd, a.kppi order by a.<row_num> desc)";
		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runPredicandParsertest(sql, parser);
		
		Assert.assertEquals("AST is wrong", "{PREDICAND={window_function={over={partition_by={1={column={name=k_stfd, table_ref=a}}, 2={column={name=kppi, table_ref=a}}}, orderby={1={null_order=null, predicand={column={substitution={name=<row_num>, type=column}, table_ref=a}}, sort_order=desc}}}, function={function_name=rank, parameters={1={column={name=column, table_ref=a}}}}}}}",
				extractor.getSqlTree().toString());
		Assert.assertEquals("Interface is wrong", "[]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<row_num>=column}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{a={column=[@2,5:5='a',<210>,1:5], <row_num>={substitution={name=<row_num>, type=column}}, k_stfd=[@10,34:34='a',<210>,1:34], kppi=[@14,44:44='a',<210>,1:44]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{a={k_stfd=[@10,34:34='a',<210>,1:34], column=[@2,5:5='a',<210>,1:5], <row_num>={substitution={name=<row_num>, type=column}}, kppi=[@14,44:44='a',<210>,1:44]}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void windowFunctionPredicandVariableP1Test() {
		String sql = "rank(<columnParam>) OVER (partition by k_stfd, kppi order by row_num desc)";
		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runPredicandParsertest(sql, parser);
		
		Assert.assertEquals("AST is wrong", "{PREDICAND={window_function={over={partition_by={1={column={name=k_stfd, table_ref=null}}, 2={column={name=kppi, table_ref=null}}}, orderby={1={null_order=null, predicand={column={name=row_num, table_ref=null}}, sort_order=desc}}}, function={function_name=rank, parameters={1={substitution={name=<columnParam>, type=predicand}}}}}}}",
				extractor.getSqlTree().toString());
		Assert.assertEquals("Interface is wrong", "[]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<columnParam>=predicand}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{unknown={row_num=[@13,61:67='row_num',<210>,1:61], k_stfd=[@8,39:44='k_stfd',<210>,1:39], kppi=[@10,47:50='kppi',<210>,1:47]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{unknown={k_stfd=[@8,39:44='k_stfd',<210>,1:39], row_num=[@13,61:67='row_num',<210>,1:61], kppi=[@10,47:50='kppi',<210>,1:47]}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void windowFunctionPredicandVariableP2Test() {
		String sql = "rank(column) OVER (partition by <k_stfd>, kppi order by row_num desc)";
		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runPredicandParsertest(sql, parser);
		
		Assert.assertEquals("AST is wrong", "{PREDICAND={window_function={over={partition_by={1={substitution={name=<k_stfd>, type=predicand}}, 2={column={name=kppi, table_ref=null}}}, orderby={1={null_order=null, predicand={column={name=row_num, table_ref=null}}, sort_order=desc}}}, function={function_name=rank, parameters={1={column={name=column, table_ref=null}}}}}}}",
				extractor.getSqlTree().toString());
		Assert.assertEquals("Interface is wrong", "[]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<k_stfd>=predicand}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{unknown={column=[@2,5:10='column',<63>,1:5], row_num=[@13,56:62='row_num',<210>,1:56], kppi=[@10,42:45='kppi',<210>,1:42]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{unknown={column=[@2,5:10='column',<63>,1:5], row_num=[@13,56:62='row_num',<210>,1:56], kppi=[@10,42:45='kppi',<210>,1:42]}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void windowFunctionPredicandVariableP3Test() {
		String sql = "rank(column) OVER (partition by k_stfd, kppi order by <row_num> desc)";
		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runPredicandParsertest(sql, parser);
		
		Assert.assertEquals("AST is wrong", "{PREDICAND={window_function={over={partition_by={1={column={name=k_stfd, table_ref=null}}, 2={column={name=kppi, table_ref=null}}}, orderby={1={null_order=null, predicand={substitution={name=<row_num>, type=predicand}}, sort_order=desc}}}, function={function_name=rank, parameters={1={column={name=column, table_ref=null}}}}}}}",
				extractor.getSqlTree().toString());
		Assert.assertEquals("Interface is wrong", "[]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<row_num>=predicand}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{unknown={column=[@2,5:10='column',<63>,1:5], k_stfd=[@8,32:37='k_stfd',<210>,1:32], kppi=[@10,40:43='kppi',<210>,1:40]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{unknown={k_stfd=[@8,32:37='k_stfd',<210>,1:32], column=[@2,5:10='column',<63>,1:5], kppi=[@10,40:43='kppi',<210>,1:40]}}",
				extractor.getSymbolTable().toString());
	}
	
	// end of Window Functions
	// Miscellaneous
	@Test
	public void doubleQuotedEscapeSequenceV1Test() {
		final String query = "SELECT 'try embedd\\'d quote' as a"
				+ " from tab1 ";

		final SQLSelectParserParser parser = parse(query);
		runParsertest(query, parser);
	}
	
	@Test
	public void doubleQuotedEscapeSequenceV2Test() {
		//TODO: Repeated single quote within quoted constant not recognized
		final String query = "SELECT 'try embedd''d quote' as b"
				+ " from tab1 ";

		final SQLSelectParserParser parser = parse(query);
		runParsertest(query, parser);
	}

	
	// *********************************
	// Macro Variables for Substitutions

	@Test
	public void querySubstitutionVariableForPredicandV1() {
		final String query = "SELECT col1 as ex, <basic_predicand> from old_table "
				+ " WHERE  <second_predicand> = <third_predicand> ";

		final SQLSelectParserParser parser = parse(query);
		runParsertest(query, parser);
	}

	@Test
	public void querySubstitutionVariableForPredicandV2() {
		final String query = "SELECT col1 as ex, <basic_predicand> as predicand from old_table "
				+ " WHERE  <second_predicand> = <third_predicand> ";

		final SQLSelectParserParser parser = parse(query);
		runParsertest(query, parser);
	}

	@Test
	public void querySubstitutionVariableForFullCondition() {
		final String query = "SELECT col1 as ex, <basic_predicand> as predicand from old_table "
				+ " WHERE  <condition_substitute> ";

		final SQLSelectParserParser parser = parse(query);
		runParsertest(query, parser);
	}

	@Test
	public void querySubstitutionVariableForMultipleConditions() {
		final String query = "SELECT col1 as ex, <basic_predicand> as predicand from old_table "
				+ " WHERE  <first_condition> or <second_condition> ";

		final SQLSelectParserParser parser = parse(query);
		runParsertest(query, parser);
	}

	@Test
	public void querySubstitutionVariableForTableOrSubQueryFromSubstitution() {
		final String query = "SELECT col1 as ex, <basic_predicand> as predicand from <old_table> as new_table "
				+ " WHERE  <second_predicand> or <third_predicand> ";

		final SQLSelectParserParser parser = parse(query);
		runParsertest(query, parser);
	}

	@Test
	public void querySubstitutionVariableForTableOrSubQueryJoinSubstitution() {
		final String query = "SELECT new_table.col1 as ex, <basic_predicand> as predicand from <gu> as old_table "
				+ " join <nt> as new_table on <gu_nt_join_condition>"
				+ " WHERE  <second_predicand> or <third_predicand> ";

		final SQLSelectParserParser parser = parse(query);
		runParsertest(query, parser);
	}

	@Test
	public void substitutionsWithWhereClausePredicandsTest() {
		// TODO: Item 28 - will not parse unaliased tuple variable; getting stuck at where clause
		final String query = " Select <column1> as redvalue, <column2> as greenvalue "
				+ " from <table> where <column1> > <column2>;";

		final SQLSelectParserParser parser = parse(query);
		runParsertest(query, parser);
	}

	@Test
	public void substitutionsOfColumnsWithTableNoAliasTest() {
		// TODO: ITEM 28 - Does not Parse without Alias, Tuple Substitution Variable does not appear in Symbol Tree or Table Dictionary
		final String query = " Select col1 as redvalue, col2 as greenvalue "
				+ " from <table>;";

		final SQLSelectParserParser parser = parse(query);
		runParsertest(query, parser);
	}

	@Test
	public void substitutionsOfColumnsWithTableAliasTest() {
		// ITEM 29 - Tuple Substitution Variable does not appear in Symbol Tree or Table Dictionary
		final String query = " Select tt.<column1> as redvalue, tt.<column2> as greenvalue "
				+ " from <table> as tt where tt.<column1> > tt.<column2>";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={substitution={name=<column1>, type=column}, table_ref=tt}, alias=redvalue}, 2={column={substitution={name=<column2>, type=column}, table_ref=tt}, alias=greenvalue}}, from={table={alias=tt, substitution={name=<table>, type=tuple}}}, where={condition={left={column={substitution={name=<column1>, type=column}, table_ref=tt}}, right={column={substitution={name=<column2>, type=column}, table_ref=tt}}, operator=>}}}}",
				extractor.getSqlTree().toString());
		Assert.assertEquals("Interface is wrong", "[redvalue, greenvalue]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<table>=tuple, <column1>=column, <column2>=column}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{<table>={<column1>={substitution={name=<column1>, type=column}}, <column2>={substitution={name=<column2>, type=column}}}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={tt=<table>, <table>={<column1>={substitution={name=<column1>, type=column}}, <column2>={substitution={name=<column2>, type=column}}}, interface={redvalue={column={substitution={name=<column1>, type=column}, table_ref=tt}}, greenvalue={column={substitution={name=<column2>, type=column}, table_ref=tt}}}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void selectListWithSubstitutions() {
		// ITEM 1 - Build out the AST, interface and Substitution list so that all placeholders are recorded
		final String query = " select noalias, normcol as normalias, <PredicandVariableNoAlias>, <PredicandVariable> predicandAlias,"
				+ " studentTable.<ColumnVariableNoAlias>, studentTable.<ColumnVariableWithAlias> columnAlias"
				+ " from <StudentTable> as studentTable ";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=noalias, table_ref=null}}, 2={column={name=normcol, table_ref=null}, alias=normalias}, 3={substitution={name=<PredicandVariableNoAlias>, type=predicand}}, 4={substitution={name=<PredicandVariable>, type=predicand}, alias=predicandAlias}, 5={column={substitution={name=<ColumnVariableNoAlias>, type=column}, table_ref=studentTable}}, 6={column={substitution={name=<ColumnVariableWithAlias>, type=column}, table_ref=studentTable}, alias=columnAlias}}, from={table={alias=studentTable, substitution={name=<StudentTable>, type=tuple}}}}}",
				extractor.getSqlTree().toString());
		Assert.assertEquals("Interface is wrong", "[<ColumnVariableNoAlias>, <PredicandVariableNoAlias>, predicandAlias, normalias, columnAlias, noalias]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<ColumnVariableNoAlias>=column, <ColumnVariableWithAlias>=column, <StudentTable>=tuple, <PredicandVariableNoAlias>=predicand, <PredicandVariable>=predicand}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{<StudentTable>={<ColumnVariableNoAlias>={substitution={name=<ColumnVariableNoAlias>, type=column}}, <ColumnVariableWithAlias>={substitution={name=<ColumnVariableWithAlias>, type=column}}, normcol=[@3,17:23='normcol',<210>,1:17], noalias=[@1,8:14='noalias',<210>,1:8]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={<StudentTable>={<ColumnVariableNoAlias>={substitution={name=<ColumnVariableNoAlias>, type=column}}, <ColumnVariableWithAlias>={substitution={name=<ColumnVariableWithAlias>, type=column}}, normcol=[@3,17:23='normcol',<210>,1:17], noalias=[@1,8:14='noalias',<210>,1:8]}, interface={<ColumnVariableNoAlias>={column={substitution={name=<ColumnVariableNoAlias>, type=column}, table_ref=studentTable}}, <PredicandVariableNoAlias>={substitution={name=<PredicandVariableNoAlias>, type=predicand}}, predicandAlias={substitution={name=<PredicandVariable>, type=predicand}}, normalias={column={name=normcol, table_ref=null}}, columnAlias={column={substitution={name=<ColumnVariableWithAlias>, type=column}, table_ref=studentTable}}, noalias={column={name=noalias, table_ref=null}}}, studentTable=<StudentTable>}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void formulaWithSubstitution() {
		// Item 33 - substitution variables inside of functions now appear in all places correctly
		// open question is whether predicand entries embedded inside functions should appear in the Symbol TAble for the query... Not sure how to decide
		final String query = "SELECT func(old_table.newColumn, otherColumn, <substitute_me>, old_table.<today>, 128.9, 'A') as ex "
				+ " from old_table ";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={function={parameters={1={column={name=newColumn, table_ref=old_table}}, 2={column={name=otherColumn, table_ref=null}}, 3={substitution={name=<substitute_me>, type=predicand}}, 4={column={substitution={name=<today>, type=column}, table_ref=old_table}}, 5={literal=128.9}, 6={literal='A'}}, function_name=func}, alias=ex}}, from={table={alias=null, table=old_table}}}}",
				extractor.getSqlTree().toString());
		Assert.assertEquals("Interface is wrong", "[ex]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<today>=column, <substitute_me>=predicand}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{old_table={otherColumn=[@7,33:43='otherColumn',<210>,1:33], newColumn=[@3,12:20='old_table',<210>,1:12], <today>={substitution={name=<today>, type=column}}}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={old_table={newColumn=[@3,12:20='old_table',<210>,1:12], otherColumn=[@7,33:43='otherColumn',<210>,1:33], <today>={substitution={name=<today>, type=column}}}, interface={ex={function={parameters={1={column={name=newColumn, table_ref=old_table}}, 2={column={name=otherColumn, table_ref=null}}, 3={substitution={name=<substitute_me>, type=predicand}}, 4={column={substitution={name=<today>, type=column}, table_ref=old_table}}, 5={literal=128.9}, 6={literal='A'}}, function_name=func}}}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void navigateV2StudentSubstitution() {
		// TODO: Build out the AST, interface and Substitution list so that all placeholders are recorded
		final String query = "with getLastXTerms as ( <GetLastXTerms> ), "
				+ " studentPopulation as ( <studentPopulation> ), "
				+ " student as ( "
				+ " select distinct <StudentIdentifier> as nk, "
				+ " studentTable.<StudentId> as username, "
				+ " <StudentEmailAddress> as email, "
				+ " studentTable.<StudentFirstName> as first_name, "
				+ " <StudentLastName> as last_name, "
				+ " <Birthdate> as birthdate, "
				+ " <ActiveStudent> as is_active "
				+ " from <StudentTable> as studentTable join studentPopulation ON  "
				+ " <studentPopulationJoinCondition>  "
				+ " Left join <PersonTable> as personTable on ( "
				+ " <personTableJoinCondition> ) "
				+ " where <whereClause> )"
				+ " select *, <missing>, <notmissing> as notMissing from student ";

		final SQLSelectParserParser parser = parse(query);
		runParsertest(query, parser);
	}

	@Test
	public void interfaceHandlingOfSubstitution() {
		// Item 32 - Interface should use the substitution variables name if not aliased
		final String query =  " select normalColumn, <missing>, <notmissing> as notMissing from student ";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=normalColumn, table_ref=null}}, 2={substitution={name=<missing>, type=predicand}}, 3={substitution={name=<notmissing>, type=predicand}, alias=notMissing}}, from={table={alias=null, table=student}}}}",
				extractor.getSqlTree().toString());
		Assert.assertEquals("Interface is wrong", "[normalColumn, notMissing, <missing>]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<notmissing>=predicand, <missing>=predicand}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{student={normalColumn=[@1,8:19='normalColumn',<210>,1:8]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={student={normalColumn=[@1,8:19='normalColumn',<210>,1:8]}, interface={normalColumn={column={name=normalColumn, table_ref=null}}, notMissing={substitution={name=<notmissing>, type=predicand}}, <missing>={substitution={name=<missing>, type=predicand}}}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void unionJoinWithSubstitutionV1() {
		// TODO: Item 34 - Parse error - These special joins do not like substitutions
		final String query = " SELECT * FROM third cross join <fourth> union join <fifth> natural join sixth ";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=normalColumn, table_ref=null}}, 2={substitution={name=<missing>, type=predicand}}, 3={substitution={name=<notmissing>, type=predicand}, alias=notMissing}}, from={table={alias=null, table=student}}}}",
				extractor.getSqlTree().toString());
		Assert.assertEquals("Interface is wrong", "[normalColumn, notMissing, <missing>]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<notmissing>=predicand, <missing>=predicand}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{student={normalColumn=[@1,8:19='normalColumn',<210>,1:8]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={student={normalColumn=[@1,8:19='normalColumn',<210>,1:8]}, interface={normalColumn={column={name=normalColumn, table_ref=null}}, notMissing={substitution={name=<notmissing>, type=predicand}}, <missing>={substitution={name=<missing>, type=predicand}}}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void unionSubstitutionV1() {
		final String query = " SELECT * FROM third union <fourth> intersect <sixth> union <fifth> ";

		final SQLSelectParserParser parser = parse(query);
		runParsertest(query, parser);
	}

	@Test
	public void unionSubstitutionV2() {
		final String query = " SELECT * FROM student union <optionalAllStudent> ";

		final SQLSelectParserParser parser = parse(query);
		runParsertest(query, parser);
	}
	

	@Test
	public void numericLiteralParseTest() {
		// NUMBERS MISTAKEN FOR COLUMN NAMES; SHOULD notice context. Table names
		// can start with numbers, not column names
		// TODO: ITEM 15 - Fix this, it doesn't actually parse the scientific notation
		// properly
		final String query = " SELECT 123 as intgr, 56.98 as decml, 34.0 e+8 as expon from h.5463_77 ";

		final SQLSelectParserParser parser = parse(query);
		runParsertest(query, parser);
	}

	// *********************************
	// Correctly Parsed, Completely developed

	@Test
	public void queryOverEntityTest() {
		final String query = "SELECT aa.scbcrse_coll_code as [College Code], aa.*, aa.[Attribute Name] FROM [Student Coursework] as aa, [Institutional Course] as courses "
				+ " WHERE not aa.scbcrse_subj_code = courses.subj_code "
				+ " AND aa.scbcrse_crse_numb = courses.crse_numb ";

		final SQLSelectParserParser parser = parse(query);
		runParsertest(query, parser);
	}

	@Test
	public void queryOverEntitySwapTest() {
		final String query = "SELECT aa.[College Name] as [College Code], aa.*, aa.[Attribute Name] FROM [Student Coursework] as aa, [Institutional Course] as courses "
				+ " WHERE not aa.[Subject Code] = courses.[Subject Code] "
				+ " AND aa.[Course Number] = courses.[Course Number] ";

		final SQLSelectParserParser parser = parse(query);

		HashMap<String, String> entityMap = new HashMap<String, String>();
		// load with physical table names
		entityMap.put("[Institutional Course]", "panto.1234_908");
		entityMap.put("[Student Coursework]", "panto.5637_453");

		HashMap<String, Map<String, String>> attributeMap = new HashMap<String, Map<String, String>>();
		// [institutional course]
		HashMap<String, String> tableMap = new HashMap<String, String>();
		attributeMap.put("[Institutional Course]", tableMap);
		tableMap.put("[Subject Code]", "scbcrse_subj_code");
		tableMap.put("[Course Number]", "crs_no");

		// [student coursework]
		tableMap = new HashMap<String, String>();
		attributeMap.put("[Student Coursework]", tableMap);
		tableMap.put("[Attribute Name]", "oth_name");
		tableMap.put("[College Name]", "clg_name");
		tableMap.put("[Subject Code]", "scbcrse_subj_code");
		tableMap.put("[Course Number]", "crs_no");

		runSQLParsertest(query, parser, entityMap, attributeMap);
	}

	@Test
	public void simpleParseTest() {
		final String query = "SELECT aa.scbcrse_coll_code, aa.* FROM scbcrse as aa, mycrse as courses "
				+ " WHERE not aa.scbcrse_subj_code = courses.subj_code "
				+ " AND (aa.scbcrse_crse_numb = courses.crse_numb " + " or aa.scbcrse_crse_numb = courses.crse_numb) ";

		final SQLSelectParserParser parser = parse(query);
		runParsertest(query, parser);
	}

	@Test
	public void aggregateParseTest() {

		final String query = " SELECT scbcrse_subj_code as subj_code, count(*), MAX(scbcrse_eff_term) "
				+ " FROM scbcrse " + " group by scbcrse_subj_code " + " order by 2, scbcrse_subj_code, 1 ";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=scbcrse_subj_code, table_ref=null}, alias=subj_code}, 2={function={function_name=COUNT, qualifier=null, parameters=*}}, 3={function={function_name=MAX, qualifier=null, parameters={column={name=scbcrse_eff_term, table_ref=null}}}}}, orderby={1={null_order=null, predicand={literal=2}, sort_order=ASC}, 2={null_order=null, predicand={column={name=scbcrse_subj_code, table_ref=null}}, sort_order=ASC}, 3={null_order=null, predicand={literal=1}, sort_order=ASC}}, from={table={alias=null, table=scbcrse}}, groupby={1={column={name=scbcrse_subj_code, table_ref=null}}}}}",
				extractor.getSqlTree().toString());
		Assert.assertEquals("Interface is wrong", "[subj_code, unnamed_1, unnamed_0]", extractor.getInterface().toString());
		Assert.assertTrue("Substitution List is wrong", extractor.getSubstitutionsMap().isEmpty());
		Assert.assertEquals("Table Dictionary is wrong", "{scbcrse={scbcrse_subj_code=[@23,127:143=\'scbcrse_subj_code\',<210>,1:127], scbcrse_eff_term=[@12,54:69=\'scbcrse_eff_term\',<210>,1:54]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={scbcrse={scbcrse_subj_code=[@23,127:143='scbcrse_subj_code',<210>,1:127], scbcrse_eff_term=[@12,54:69='scbcrse_eff_term',<210>,1:54]}, interface={subj_code={column={name=scbcrse_subj_code, table_ref=null}}, unnamed_1={function={function_name=MAX, qualifier=null, parameters={column={name=scbcrse_eff_term, table_ref=null}}}}, unnamed_0={function={function_name=COUNT, qualifier=null, parameters=*}}}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void aggregateWithAliasParseTest() {

		final String query = " SELECT scbcrse_subj_code as subj_code, count(*) as total, MAX(scbcrse_eff_term) as maximum"
				+ " FROM scbcrse " + " group by scbcrse_subj_code " + " order by 2, scbcrse_subj_code, 1 ";

		final SQLSelectParserParser parser = parse(query);
		runParsertest(query, parser);
	}

	@Test
	public void simpleAndOrParseTest() {
		// gyg
		final String query = " SELECT scbcrse_subj_code FROM scbcrse " + " where a = b AND c=d  OR e=f and g=h ";

		final SQLSelectParserParser parser = parse(query);
		runParsertest(query, parser);
	}

	@Test
	public void simpleFromStatementTest() {

		final String query = " SELECT * FROM tab1 ";

		final SQLSelectParserParser parser = parse(query);
		runParsertest(query, parser);
	}

	@Test
	public void simpleFromListType1ParseTest() {

		final String query = " SELECT * FROM third, fourth, fifth, sixth ";

		final SQLSelectParserParser parser = parse(query);
		runParsertest(query, parser);
	}

	@Test
	public void simpleFromListType2ParseTest() {

		final String query = " SELECT * FROM third cross join fourth union join fifth natural join sixth ";

		final SQLSelectParserParser parser = parse(query);
		runParsertest(query, parser);
	}

	@Test
	public void simpleFromListType3ParseTest() {

		final String query = " SELECT * FROM third cross join fourth "
				+ " union join fifth natural join sixth natural inner join seventh";

		final SQLSelectParserParser parser = parse(query);
		runParsertest(query, parser);
	}

	@Test
	public void simpleFromListType4ParseTest() {

		final String query = " SELECT * FROM third join fourth on a = b " 
		+ " left outer join fifth on b = d ";

		final SQLSelectParserParser parser = parse(query);
		runParsertest(query, parser);
	}

	@Test
	public void simpleMultipleUnionParseTest() {

		final String query = " SELECT first FROM third " + " union select third from fifth "
				+ " union select fourth from sixth " + " union select seventh from eighth ";

		final SQLSelectParserParser parser = parse(query);
		runParsertest(query, parser);
	}

	@Test
	public void simpleMultipleIntersectParseTest() {

		final String query = " SELECT first FROM third " + " intersect select third from fifth "
				+ " intersect select fourth from sixth " + " intersect select seventh from eighth ";

		final SQLSelectParserParser parser = parse(query);
		runParsertest(query, parser);
	}

	@Test
	public void simpleMultipleUnion1ParseTest() {

		final String query = " SELECT first FROM third " + " union select second from fifth "
				+ " union select fourth from sixth " + " union select seventh from eighth ";

		final SQLSelectParserParser parser = parse(query);
		runParsertest(query, parser);
	}

	@Test
	public void simpleMultipleIntersect1ParseTest() {

		final String query = " SELECT first FROM third " + " intersect select second from fifth "
				+ " intersect select fourth from sixth " + " intersect select seventh from eighth ";

		final SQLSelectParserParser parser = parse(query);
		runParsertest(query, parser);
	}

	@Test
	public void simpleUnionIntersectParseTest() {

		final String query = " SELECT first FROM third " + " union select third from fifth "
				+ " intersect select fourth from sixth " + " union select seventh from eighth ";

		final SQLSelectParserParser parser = parse(query);
		runParsertest(query, parser);
	}

	@Test
	public void nestedUnionIntersectAAParseTest() {

		final String query = " SELECT first FROM ( " + "  select third from fifth "
				+ " intersect select fourth from sixth ) aa " + " union select seventh from eighth ";

		final SQLSelectParserParser parser = parse(query);
		runParsertest(query, parser);
	}

	@Test
	public void nestedUnionIntersectParseTest() {

		final String query = " SELECT first FROM ( " + "  select third from fifth "
				+ " intersect select fourth from sixth ) " + " union select seventh from eighth ";

		final SQLSelectParserParser parser = parse(query);
		runParsertest(query, parser);
	}

	@Test
	public void subqueryParseTest() {
		// probably could handle unknowns from inside query better. Also, should
		// trap/notice there's no COURSES table in the from statement
		final String query = "SELECT aa.scbcrse_coll_code FROM scbcrse aa "
				+ " WHERE aa.scbcrse_subj_code = courses.subj_code " + " AND aa.scbcrse_crse_numb = courses.crse_numb "
				+ " AND aa.scbcrse_eff_term = ( " + " SELECT MAX(scbcrse_eff_term) " + " FROM scbcrse "
				+ " WHERE scbcrse_subj_code = courses.subj_code " + " AND scbcrse_crse_numb = courses.crse_numb "
				+ " AND scbcrse_eff_term <= courses.term) ";

		final SQLSelectParserParser parser = parse(query);
		runParsertest(query, parser);
	}

	@Test
	public void arithmeticSimpleParseTest() {
		final String query = "SELECT (6 * 9 - 100 + a)  FROM scbcrse aa " + " WHERE aa.scbcrse_subj_code is not null ";

		final SQLSelectParserParser parser = parse(query);
		runParsertest(query, parser);
	}

	@Test
	public void arithmeticParseTest() {

		final String query = "SELECT -(aa.scbcrse_coll_code * 6 - other) FROM scbcrse aa "
				+ " WHERE aa.scbcrse_subj_code is not null ";

		final SQLSelectParserParser parser = parse(query);
		runParsertest(query, parser);
	}

	@Test
	public void arithmeticRunningAdditionTest() {

		final String query = "SELECT 5+8+9-2+9 FROM scbcrse aa " + " WHERE aa.scbcrse_subj_code is not null ";

		final SQLSelectParserParser parser = parse(query);
		runParsertest(query, parser);
	}

	@Test
	public void arithmeticRunningMultiplicationTest() {

		final String query = "SELECT 5*8*9/2*9 FROM scbcrse aa " + " WHERE aa.scbcrse_subj_code is not null ";

		final SQLSelectParserParser parser = parse(query);
		runParsertest(query, parser);
	}

	@Test
	public void arithmeticRunningMultiplicationWithParenTest() {

		final String query = "SELECT 5*(8*9)/(2*9) FROM scbcrse aa " + " WHERE aa.scbcrse_subj_code is not null ";

		final SQLSelectParserParser parser = parse(query);
		runParsertest(query, parser);
	}

	@Test
	public void arithmeticWithAliasParseTest() {

		final String query = "SELECT -(aa.scbcrse_coll_code * 6 - other) as item FROM scbcrse aa "
				+ " WHERE aa.scbcrse_subj_code is not null ";

		final SQLSelectParserParser parser = parse(query);
		runParsertest(query, parser);
	}
	@Test
	public void selectItemSubqueryStatementParseTest() {
		final String query = " SELECT first_item,( " + " SELECT item " + " FROM sgbstdn "
				+ " WHERE sgbstdn_levl_code = 'US' " + " ) AS INTERNATIONAL_IND " + " FROM sgbstdn ";

		final SQLSelectParserParser parser = parse(query);
		runParsertest(query, parser);
	}

	@Test
	public void nestedSymbolTableConstructionTest() {
		final String query = " SELECT b.att1, b.att2 " + " from (SELECT a.col1 as att1, a.col2 as att2 "
				+ " FROM tab1 as a" + " WHERE a.col1 <> a.col3 " + " ) AS b ";

		final SQLSelectParserParser parser = parse(query);
		runParsertest(query, parser);
	}

	@Test
	public void biggerQueryParseTest() {

		final String query = " select spriden_id, spriden_pidm, terms.max_term, spriden_first_name, spriden_last_name, "
				+ "spriden_mi, TERM_CODE_ADMIT FROM ( "
				+ " SELECT spriden_id, spriden_pidm, spriden_first_name, spriden_last_name, spriden_mi FROM spriden "
				+ " WHERE spriden_change_ind is null) spriden " + " JOIN (  SELECT pidm, max(term) AS max_term FROM ( "
				+ " SELECT shrtgpa_pidm AS pidm, shrtgpa_term_code AS term  "
				+ " FROM shrtgpa WHERE shrtgpa_levl_code = 'UG' "
				+ " UNION ALL SELECT shrtrce_pidm AS pidm, shrtrce_term_code_eff AS term FROM shrtrce WHERE shrtrce_levl_code = 'UG' "
				+ " UNION ALL SELECT sfrstca_pidm AS pidm, sfrstca_term_code AS term   FROM sfrstca "
				+ " JOIN stvterm ON stvterm_code = sfrstca_term_code " + " AND stvterm_end_date > SYSDATE - 365  "
				+ " UNION ALL SELECT sgbstdn_pidm AS pidm, sgbstdn_term_code_eff AS term "
				+ " FROM sgbstdn WHERE sgbstdn_levl_code = 'UG'  ) x GROUP BY pidm "
				+ " ) terms ON spriden.spriden_pidm = terms.pidm "
				+ " JOIN STVTERM termDates ON termDates.STVTERM_CODE = terms.max_term "
				+ " JOIN (SELECT sgbstdn_pidm, MIN(SGBSTDN_TERM_CODE_ADMIT) AS TERM_CODE_ADMIT from sgbstdn "
				+ " WHERE sgbstdn_levl_code = 'UG' GROUP BY sgbstdn_pidm "
				+ " ) undergradOnly ON undergradOnly.sgbstdn_pidm = spriden.spriden_pidm "
				+ " GROUP BY spriden_id, spriden_pidm, terms.max_term, spriden_first_name, spriden_last_name, spriden_mi, TERM_CODE_ADMIT "
				+ " HAVING max(max_term) >= 201310 ";

		final SQLSelectParserParser parser = parse(query);
		runParsertest(query, parser);
	}

	@Test
	public void largeStudentgeneralQueryParseTest() {

		final String query = "SELECT " + " population.spriden_id AS STUDENT_ID "
				+ " , population.spriden_first_name AS FIRST_NAME " + " , population.spriden_mi AS MIDDLE_INITIAL "
				+ " , population.spriden_last_name AS LAST_NAME "
				+ " , TO_CHAR(demographic.spbpers_birth_date, 'yyyymmdd') AS DATE_OF_BIRTH "
				+ " , NVL(demographic.spbpers_sex,'') AS GENDER " + " , NVL(f.goremal_email_address,'') AS EMAIL_ID "
				+ " , NVL(demographic.spbpers_ethn_code,'') AS ETHNICITY_CD "
				+ " , NVL(demographic.spbpers_dead_ind,'') AS DECEASED_IND " + " , '' AS Field10 " + " , ( "
				+ " SELECT CASE WHEN sgbstdn.sgbstdn_resd_code in ('G') THEN 'Y'	 " + " ELSE 'N' END "
				+ " FROM sgbstdn " + " JOIN ( " + " SELECT sgbstdn_pidm, max(sgbstdn_term_code_eff) AS max_term "
				+ " FROM sgbstdn " + " WHERE sgbstdn_levl_code = 'US' " + " GROUP BY sgbstdn_pidm "
				+ " ) m on sgbstdn.sgbstdn_pidm = m.sgbstdn_pidm and sgbstdn.sgbstdn_term_code_eff = m.max_term "
				+ " WHERE sgbstdn.sgbstdn_pidm = population.spriden_pidm " + " 		) AS INTERNATIONAL_IND "
				+ " , NVL(GOBINTL.GOBINTL_NATN_CODE_LEGAL,'') AS COUNTRY_CD " + " , '' AS INST_FIRST_TERM_ID "
				+ " , hs.stvsbgi_desc AS HS_NAME " + " , sobsbgi.sobsbgi_city AS HS_CITY "
				+ " , sobsbgi.sobsbgi_stat_code AS HS_STATE " + " , hs.sorhsch_class_size AS HS_SIZE "
				+ " , hs.sorhsch_percentile AS HS_PERCENTILE " + " , hs.sorhsch_class_rank AS HS_RANK "
				+ " , hs.sorhsch_gpa AS HS_GPA " + " , '' AS Field21 "
				+ " , hp.sprtele_phone_area || hp.sprtele_phone_number AS HOME_PHONE " + " , '' AS Field23 "
				+ " , cp.sprtele_phone_area || cp.sprtele_phone_number AS MOBILE_PHONE "
				+ " , address.spraddr_street_line1 AS MAIL_ADDRESS1 "
				+ " , address.spraddr_street_line2 AS MAIL_ADDRESS2 " + " , address.spraddr_city AS MAIL_CITY "
				+ " , address.spraddr_stat_code AS MAIL_STATE " + " , address.spraddr_zip AS MAIL_ZIP_CODE "
				+ " , '' AS Field30 " + " , '' AS Field31 " + " , demographic.SPBPERS_LGCY_CODE AS STUDENT_LEGACY_CD "
				+ " , ( " + " SELECT SGBSTDN_ADMT_CODE " + " FROM sgbstdn "
				+ " WHERE sgbstdn_pidm = population.spriden_pidm " + " AND sgbstdn_levl_code = 'US'			 "
				// --MODIFY PER MEMBER
				+ " AND sgbstdn_term_code_eff = ( " + " SELECT max(sgbstdn_term_code_eff) " + " FROM sgbstdn "
				+ " WHERE sgbstdn_pidm = population.spriden_pidm " + " AND sgbstdn_levl_code = 'US')		 "
				// --MODIFY PER MEMBER
				+ " 		) AS STUDENT_ADMIT_CD "
				+ " , CASE WHEN shrtrit_primary.shrtrit_sbgi_code is not null THEN 'Y' ELSE 'N' END AS TRANSFER_STUDENT_IND "
				+ " , shrtrit_primary.shrtrit_sbgi_code AS TRANSFER_INST_CD "
				+ " , NVL(demographic.SPBPERS_VERA_IND,'N') as VETERAN_IND "
				+ " , CASE WHEN readmit.saradap_pidm IS NULL THEN 'N' ELSE 'Y' END as READMIT_IND "
				+ " , CASE WHEN RCRAPP3_1.pidm IS NOT NULL THEN 'Y' ELSE 'N' END AS FIRST_GEN_IND  "
				+ " , sobsbgi.SOBSBGI_ZIP AS HS_ZIP_CODE " + " , '' AS ADMISSION_ZIP_CODE " + " , '' AS REGION_CD "
				+ " , ( " + " SELECT CASE WHEN SGBSTDN_STST_CODE = 'AS' THEN 'Y' ELSE 'N' END "
				// --MODIFY PER MEMBER
				+ " FROM sgbstdn " + " WHERE sgbstdn_pidm = population.spriden_pidm "
				+ " AND sgbstdn_levl_code = 'US'			 "
				// --MODIFY PER MEMBER
				+ " AND sgbstdn_term_code_eff = ( " + " SELECT max(sgbstdn_term_code_eff) " + " FROM sgbstdn "
				+ " WHERE sgbstdn_pidm = population.spriden_pidm " + " 	AND sgbstdn_levl_code = 'US')		 "
				// --MODIFY PER MEMBER
				+ " ) AS ACTIVE_IND "

				+ " FROM  "
				// --STUDENT POPULATION
				+ " ( " + " SELECT "
				+ " spriden_id, spriden_pidm, terms.max_term, spriden_first_name, spriden_last_name, spriden_mi "
				+ " FROM ( "
				+ " SELECT spriden_id, spriden_pidm, spriden_first_name, spriden_last_name, spriden_mi FROM spriden WHERE spriden_change_ind is null "
				+ " ) spriden " + " JOIN ( " + " SELECT pidm, max(term) AS max_term " + " FROM ( "
				+ " SELECT shrtgpa_pidm AS pidm, shrtgpa_term_code AS term "
				+ "	FROM shrtgpa WHERE shrtgpa_levl_code = 'US'	 "
				// -- MODIFY PER MEMBER
				+ " UNION ALL SELECT shrtrce_pidm AS pidm, shrtrce_term_code_eff AS term "
				+ " FROM shrtrce WHERE shrtrce_levl_code = 'US'	 "
				// -- MODIFY PER MEMBER
				+ " UNION ALL SELECT sfrstcr_pidm AS pidm, sfrstcr_term_code AS term " + " FROM sfrstcr "
				+ " JOIN stvterm ON stvterm_code = sfrstcr_term_code " + " WHERE sfrstcr_levl_code = 'US'		 "
				// -- MODIFY PER MEMBER
				+ " AND stvterm_end_date > SYSDATE - 365		 "
				// --INSTATED TO REDUCE RUN TIME
				+ " UNION ALL SELECT sgbstdn_pidm AS pidm, sgbstdn_term_code_eff AS term 	FROM sgbstdn WHERE sgbstdn_levl_code = 'US'	 "
				// -- MODIFY PER MEMBER
				+ " ) x " + " GROUP BY pidm " + " ) terms ON spriden.spriden_pidm = terms.pidm "
				/*
				 * JOIN ( REMOVED 2015 01 20 CHECK SF TICKET FOR FULL DETAILS --
				 * NEW LEVL CODE LOGIC IN ST SHOULD FILTER THESE STUDENTS SELECT
				 * sgbstdn_pidm FROM sgbstdn a WHERE sgbstdn_levl_code = 'US'
				 * AND sgbstdn_term_code_eff = ( SELECT
				 * MAX(sgbstdn_term_code_eff) FROM sgbstdn WHERE sgbstdn_pidm =
				 * a.sgbstdn_pidm ) ) UGRD ON spriden.spriden_pidm =
				 * ugrd.sgbstdn_pidm
				 */
				+ " JOIN STVTERM termDates ON termDates.STVTERM_CODE = terms.max_term "
				+ " GROUP BY spriden_id, spriden_pidm, terms.max_term, spriden_first_name, spriden_last_name, spriden_mi "
				// --DEVELOPMENT FILTER
				// --HAVING max(max_term) >= 199808 --ADJUST THIS LINE BY
				// LOOKING UP THE
				// CURRENT TERM
				// --HISTORICAL FILTER
				// --HAVING max(termDates.STVTERM_START_DATE) > SYSDATE - 3650
				// --ADJUST IF
				// WANT DIFFERENT THAN 10 YEARS
				// --DAILY FILTER
				+ " HAVING max(termDates.STVTERM_START_DATE) > SYSDATE - 730 "
				// --ADJUST IF WANT DIFFERENT THAN 2 YEARS
				+ " ) population "
				// --DEMOGRAPHIC INFORMATION
				+ " LEFT OUTER JOIN spbpers demographic " + " ON population.spriden_pidm = demographic.spbpers_pidm "
				// --ADDRESS
				+ " LEFT OUTER JOIN ( "
				+ " SELECT spraddr.spraddr_pidm, spraddr.spraddr_street_line1, spraddr.spraddr_street_line2, spraddr_city, spraddr_stat_code, spraddr_zip "
				+ " FROM spraddr " + " JOIN ( " + " SELECT spraddr_pidm, max(spraddr_seqno) AS max_seqno "
				+ " FROM spraddr " + " WHERE spraddr_atyp_code = 'MA'							 "
				// --MODIFY PER MEMBER
				+ " AND spraddr_status_ind is null " + " GROUP BY spraddr_pidm "
				+ " ) addr_max ON spraddr.spraddr_pidm = addr_max.spraddr_pidm AND spraddr.spraddr_seqno = addr_max.max_seqno "
				+ " WHERE spraddr.spraddr_atyp_code = 'MA' "
				// --MODIFY PER MEMBER
				+ " AND spraddr.spraddr_status_ind is null " + " AND spraddr_FROM_date <= sysdate "
				+ " AND (spraddr_to_date >= sysdate or spraddr_to_date is null) "
				+ " ) address ON population.spriden_pidm = address.spraddr_pidm "
				// --HOME PHONE
				+ " LEFT OUTER JOIN ( "
				+ " SELECT sprtele.sprtele_pidm, sprtele.sprtele_phone_area, sprtele.sprtele_phone_number "
				+ " FROM sprtele " + " JOIN ( " + " SELECT sprtele_pidm, max(sprtele_seqno) AS max_seqno "
				+ " FROM sprtele  " + " WHERE sprtele_tele_code = 'MA' 	 "
				// --MODIFY PER MEMBER
				+ " AND sprtele_status_ind is null " + " GROUP BY sprtele_pidm "
				+ " ) tele_max ON sprtele.sprtele_pidm = tele_max.sprtele_pidm AND sprtele.sprtele_seqno = tele_max.max_seqno "
				+ " WHERE sprtele.sprtele_tele_code = 'MA'  "
				// --MODIFY PER MEMBER
				+ " AND sprtele.sprtele_status_ind is null " + " ) hp ON population.spriden_pidm = hp.sprtele_pidm "

				// --MOBILE PHONE
				+ " LEFT OUTER JOIN ( "
				+ " SELECT sprtele.sprtele_pidm, sprtele.sprtele_phone_area, sprtele.sprtele_phone_number "
				+ " FROM sprtele " + " JOIN ( " + " SELECT sprtele_pidm, max(sprtele_seqno) as max_seqno "
				+ " FROM sprtele  " + " WHERE sprtele_tele_code = 'CP'  "
				// --MODIFY PER MEMBER
				+ " AND sprtele_status_ind is null " + " GROUP BY sprtele_pidm "
				+ " ) tele_max ON sprtele.sprtele_pidm = tele_max.sprtele_pidm AND sprtele.sprtele_seqno = tele_max.max_seqno "
				+ " WHERE sprtele.sprtele_tele_code = 'CP'  "
				// --MODIFY PER MEMBER
				+ " AND sprtele.sprtele_status_ind is null " + " ) cp ON population.spriden_pidm = cp.sprtele_pidm "
				// --EMAIL
				+ " LEFT OUTER JOIN goremal f  " + " ON population.spriden_pidm = f.goremal_pidm "
				+ " 	AND f.goremal_emal_code = 'GSU' AND f.goremal_status_ind = 'A' AND f.goremal_preferred_ind = 'Y' "
				// --MODIFY PER MEMBER
				/*
				 * LEFT OUTER JOIN ( SELECT SGBSTDN.SGBSTDN_pidm,
				 * SGBSTDN.SGBSTDN_ADMT_CODE, SGBSTDN.SGBSTDN_STST_CODE FROM
				 * SGBSTDN WHERE sgbstdn_levl_code in ('US') --MODIFY PER MEMBER
				 * ) m on population.spriden_pidm = m.sgbstdn_pidm
				 */
				// --LEGACY
				+ " LEFT OUTER JOIN stvlgcy leg " + " ON demographic.spbpers_lgcy_code = leg.stvlgcy_code "
				// --HIGH SCHOOL
				+ " LEFT OUTER JOIN ( "
				+ " SELECT sorhsch.sorhsch_pidm, sorhsch_gpa, sorhsch_class_rank, sorhsch_percentile, sorhsch_class_size, stvsbgi.stvsbgi_desc, sorhsch_sbgi_code "
				+ " FROM sorhsch " + " JOIN ( " + " SELECT sorhsch_pidm, max(sorhsch_activity_date) AS max_date "
				+ " FROM sorhsch " + " GROUP BY sorhsch_pidm "
				+ " ) crit ON sorhsch.sorhsch_pidm = crit.sorhsch_pidm AND sorhsch.sorhsch_activity_date = crit.max_date "
				+ " JOIN stvsbgi on sorhsch.sorhsch_sbgi_code = stvsbgi.stvsbgi_code "
				+ " ) hs ON population.spriden_pidm = hs.sorhsch_pidm "
				// --HIGH SCHOOL PT2
				+ " LEFT OUTER JOIN sobsbgi  " + " ON hs.sorhsch_sbgi_code = sobsbgi.sobsbgi_sbgi_code "
				// --COUNTRY CODE
				+ " LEFT OUTER JOIN gobintl  " + " 	ON gobintl.gobintl_pidm = population.spriden_pidm "
				// --TRANSFER INSTITUTION
				+ " LEFT OUTER JOIN ( " + " SELECT * " + " FROM shrtrit " + " JOIN ( "
				+ " SELECT a.shrtrit_pidm AS pidm, max(a.shrtrit_seq_no) AS max_date " + " FROM shrtrit a "
				+ " GROUP BY a.shrtrit_pidm "
				+ " ) shrtrit_max ON shrtrit.shrtrit_pidm = shrtrit_max.pidm AND shrtrit.shrtrit_seq_no = shrtrit_max.max_date "
				+ " ) shrtrit_primary ON shrtrit_primary.pidm = population.spriden_pidm "
				// --FIRST GEN INDICATOR
				+ " LEFT JOIN ( " + " SELECT DISTINCT rcrapp3.rcrapp3_pidm as pidm " + " FROM rcrapp3 " + " JOIN ( "
				+ " SELECT a.rcrapp3_pidm, max(a.rcrapp3_aidy_code) as max_aidy " + " FROM rcrapp3 a "
				+ " GROUP BY a.rcrapp3_pidm, a.rcrapp3_seq_no "
				+ " ) rcrapp3_max on rcrapp3.rcrapp3_pidm = rcrapp3_max.rcrapp3_pidm AND rcrapp3.rcrapp3_aidy_code = rcrapp3_max.max_aidy "
				+ " WHERE rcrapp3.rcrapp3_seq_no = '1' AND (rcrapp3.RCRAPP3_FATHER_HI_GRADE IN ('1','2') OR rcrapp3.RCRAPP3_MOTHER_HI_GRADE IN ('1','2')) "
				+ " ) RCRAPP3_1 ON population.spriden_pidm = RCRAPP3_1.pidm "
				// READMIT INDICATOR
				+ " LEFT OUTER JOIN ( " + " SELECT DISTINCT SARADAP_PIDM " + " FROM SARADAP  "
				+ " WHERE SARADAP_ADMT_CODE = 'RE' AND SARADAP_LEVL_CODE = 'US'  "
				// --MODIFY PER MEMBER
				+ " ) readmit ON readmit.SARADAP_PIDM = population.spriden_pidm " + " WHERE 1=1 " + " ORDER BY 1";

		final SQLSelectParserParser parser = parse(query);
		runParsertest(query, parser);
	}

	@Test
	public void complexHiveQueryJoinTest() {

		final String query = " SELECT " + " CASE   "
				+ " WHEN COALESCE( S948.OBSERVATION_TM>=S949.OBSERVATION_TM , FALSE) THEN S948.t_student_last_name   "
				+ " WHEN COALESCE( S949.OBSERVATION_TM>=S948.OBSERVATION_TM , FALSE) THEN S949.t_student_last_name   "
				+ " ELSE COALESCE(S948.t_student_last_name, S949.t_student_last_name) END AS t_student_last_name, "
				+ " CASE   "
				+ " WHEN COALESCE( S948.OBSERVATION_TM>=S949.OBSERVATION_TM , FALSE) THEN S948.t_sur_name   "
				+ " WHEN COALESCE( S949.OBSERVATION_TM>=S948.OBSERVATION_TM , FALSE) THEN S949.t_sur_name "
				+ " ELSE COALESCE(S948.t_sur_name, S949.t_sur_name) END AS t_sur_name, " + " CASE   "
				+ " WHEN COALESCE( S948.OBSERVATION_TM>=S949.OBSERVATION_TM , FALSE) THEN S948.t_student_first_name   "
				+ " WHEN COALESCE( S949.OBSERVATION_TM>=S948.OBSERVATION_TM , FALSE) THEN S949.t_student_first_name   "
				+ " ELSE COALESCE(S948.t_student_first_name, S949.t_student_first_name) END AS t_student_first_name "
				+ " FROM ( " + " SELECT  " + " t_student_first_name, " + " t_sur_name, " + " t_student_last_name, "
				+ " k_stfd, " + " OBSERVATION_TM " + " from ( " + " SELECT  " + " t_student_first_name, "
				+ " t_sur_name, " + " t_student_last_name, " + " k_stfd, " + " OBSERVATION_TM,  "
				+ " rank() OVER (partition by k_stfd order by OBSERVATION_TM desc, row_num desc) AS key_rank "
				+ " from ( " + " SELECT  " + " DOB AS t_student_first_name, " + " NAME AS t_sur_name, "
				+ " LOCATION AS t_student_last_name, " + " NAME AS k_stfd, " + " OBSERVATION_TM,  "
				+ " pantodev.row_num() as row_num " + " FROM pantodev.23810_949 " + " WHERE  "
				+ " OBSERVATION_DT <= 20160321  "
				+ " AND unix_timestamp(OBSERVATION_TM) <= unix_timestamp('2016-03-21 10:43:15.0') " + " ) a "
				+ " ) b where key_rank =1 " + " ) S949  " + " FULL OUTER JOIN ( " + "  SELECT  "
				+ "  t_student_first_name, " + " t_sur_name, " + " t_student_last_name, " + " k_stfd, "
				+ " OBSERVATION_TM " + " from ( " + " SELECT  " + " t_student_first_name, " + " t_sur_name, "
				+ " t_student_last_name, " + " k_stfd, " + " OBSERVATION_TM,  "
				+ " rank() OVER (partition by k_stfd order by OBSERVATION_TM desc, row_num desc) AS key_rank "
				+ " from ( " + " SELECT  " + " DOB AS t_student_first_name, " + " NAME AS t_sur_name, "
				+ " LOCATION AS t_student_last_name, " + " NAME AS k_stfd, " + " OBSERVATION_TM,  "
				+ " pantodev.row_num() as row_num " + " FROM pantodev.23810_948 " + " WHERE  "
				+ " OBSERVATION_DT <= 20160309  "
				+ " AND unix_timestamp(OBSERVATION_TM) <= unix_timestamp('2016-03-09 12:54:18.0') " + " ) a "
				+ " ) b where key_rank =1 " + " ) S948  " + " ON (S949.k_stfd=S948.k_stfd) " + " where  "
				+ " (((unix_timestamp(S949.observation_tm) > unix_timestamp('1900-01-01 00:00:00.0'))  "
				+ "  AND (unix_timestamp(S949.observation_tm) <= unix_timestamp('2016-03-30 11:04:40.484'))) "
				+ " OR ((unix_timestamp(S948.observation_tm) > unix_timestamp('1900-01-01 00:00:00.0')) "
				+ " AND (unix_timestamp(S948.observation_tm) <= unix_timestamp('2016-03-30 11:04:40.484')))) ";

		final SQLSelectParserParser parser = parse(query);
		runParsertest(query, parser);
	}

	@Test
	public void simpleInsertFromQueryTest() {

		final String query = " insert into sch.subj.tbl (newcol1, newcol2) values (SELECT b.att1, b.att2 "
				+ " from (SELECT a.col1 as att1, a.col2 as att2 " + " FROM sch.subj.tab1 as a"
				+ " WHERE a.col1 <> a.col3 " + " ) AS b )";

		final SQLSelectParserParser parser = parse(query);
		runParsertest(query, parser);
	}

	/***********************************
	 * Section covering GF Encoder Style Queries
	 */

	@Test
	public void authorizationQueryTest() {

		String sql = "select RECORD_TYPE as RECORD_TYPE, ACTION as ACTION, USER_ID as PRIMARY_USER_ID, ";
		// check authorizationTbl

		sql += "authn.is_active as IS_ACTIVE, authn.can_login as CAN_LOGIN, authn.send_activation as SEND_ACTIVATION, ";
		sql += "IS_ACTIVE, CAN_LOGIN, SEND_ACTIVATION, ";

		sql += "FIRST_NAME as FIRST_NAME, LAST_NAME as LAST_NAME, ";
		sql += "authn.alt_user_id as ALT_USER_ID, ";
		sql += "ROLE_ID as ROLE_ID"
				+ ", EMAIL as EMAIL, ALT_EMAIL as ALT_EMAIL, ADDRESS_1 as ADDRESS_1, ADDRESS_2 as ADDRESS_2, CITY as CITY, STATE as STATE, POSTAL_CODE as POSTAL_CODE, HOME_PHONE as HOME_PHONE,"
				+ " CELL_PHONE as CELL_PHONE, WORK_PHONE as WORK_PHONE, GENDER as GENDER, ETHNICITY as ETHNICITY, DATE_OF_BIRTH as DATE_OF_BIRTH, TOTAL_CREDIT_HOURS as TOTAL_CREDIT_HOURS, CREDIT_HOURS_ATTEMPTED as CREDIT_HOURS_ATTEMPTED, "
				+ " MAJOR_ID as MAJOR_ID, STUDENT_ENROLLMENT_STATUS as STUDENT_ENROLLMENT_STATUS, STUDENT_ENROLLMENT_GOAL as STUDENT_ENROLLMENT_GOAL, ";
		sql += "authn.pin as PIN, authn.sso_id as SSO_ID, ";
		sql += "'' as ACT_TOTAL, '' as ACT_ENGLISH, "
				+ " '' as ACT_READING, '' as ACT_MATH, '' as ACT_SCIENCE, '' as SAT_TOTAL, '' as SAT_VERBAL, '' as SAT_MATH, '' as HIGH_SCHOOL_GPA, "
				+ " '' as FIRST_GENERATION_IND, '' as FATHER_EDUCATION, '' as MOTHER_EDUCATION, '' as HIGH_SCHOOL_ZIP_CODE, '' as HOUSEHOLD_INCOME, "
				+ " '' as SINGLE_PARENT_FAMILY_IND, '' as TRANSFER_GPA, '' as HOME_COLLEGE, '' as RECEIVE_TXT_MESSAGE_IND "
				+ " from "
				+ "(select a.record_type as RECORD_TYPE, a.action as ACTION, a.primary_user_id as USER_ID, first_name as FIRST_NAME, last_name as LAST_NAME, a.is_active as IS_ACTIVE, a.login_ind AS CAN_LOGIN, a.activate_email_ind as SEND_ACTIVATION, role_id as ROLE_ID, '' as MAJOR_ID,total_credit_hours as TOTAL_CREDIT_HOURS, attempted_credit_hours as CREDIT_HOURS_ATTEMPTED, "
				+ " email as EMAIL, '' as ALT_EMAIL, address_1 as ADDRESS_1, address_2 as ADDRESS_2, city as CITY, state as STATE, postal_code as POSTAL_CODE, home_phone as HOME_PHONE, cell_phone as CELL_PHONE, '' as WORK_PHONE, gender as GENDER, ethnicity as ETHNICITY, "
				+ " date_of_birth as DATE_OF_BIRTH, receive_txt_message_ind as RECEIVE_TXT_MESSAGE_IND, student_enrollment_status as STUDENT_ENROLLMENT_STATUS, student_enrollment_goal as STUDENT_ENROLLMENT_GOAL from "
				+ " studentTbl a left join "
				// start of student major
				+ "(select primary_user_id, total_credit_hours,attempted_credit_hours from (select primary_user_id, total_credit_hours,attempted_credit_hours,"
				+ "rank() over (partition by primary_user_id order by b.begin_date desc ,b.end_date desc) term_rank from "
				+ " studentMajorTbl  a, academicPeriodTbl "
				+ " b where a.term_id=b.external_id and a.total_credit_hours is not null and a.attempted_credit_hours is not null and length(trim(a.total_credit_hours)) > 0 and length(trim(a.attempted_credit_hours)) > 0) tbl where term_rank =1)"
				// end of student major
				+ " b on (a.primary_user_id = b.primary_user_id) ";
		sql += " union all "
				+ " select record_type as RECORD_TYPE, action as ACTION, primary_user_id as USER_ID, first_name as FIRST_NAME, last_name as LAST_NAME, is_active as IS_ACTIVE, login_ind AS CAN_LOGIN, activate_email_ind as SEND_ACTIVATION, role_id as ROLE_ID, '' as MAJOR_ID,'' as TOTAL_CREDIT_HOURS, '' as CREDIT_HOURS_ATTEMPTED, "
				+ " email as EMAIL, alt_email as ALT_EMAIL, '' as ADDRESS_1, '' as ADDRESS_2, '' as CITY, '' as STATE, '' as POSTAL_CODE, home_phone as HOME_PHONE, cell_phone as CELL_PHONE, work_phone as WORK_PHONE, '' as GENDER, '' as ETHNICITY, '' as DATE_OF_BIRTH, '' as RECEIVE_TXT_MESSAGE_IND, '' as STUDENT_ENROLLMENT_STATUS, '' as STUDENT_ENROLLMENT_GOAL from "
				+ " advisorTbl  staff";
		sql += " union all "
				+ " select record_type as RECORD_TYPE, action as ACTION, primary_user_id as USER_ID, first_name as FIRST_NAME, last_name as LAST_NAME,is_active as IS_ACTIVE, login_ind AS CAN_LOGIN, activate_email_ind as SEND_ACTIVATION,  role_id as ROLE_ID, '' as MAJOR_ID,'' as TOTAL_CREDIT_HOURS, '' as CREDIT_HOURS_ATTEMPTED, "
				+ " email as EMAIL, alt_email as ALT_EMAIL, '' as ADDRESS_1, '' as ADDRESS_2, '' as CITY, '' as STATE, '' as POSTAL_CODE, home_phone as HOME_PHONE, cell_phone as CELL_PHONE, work_phone as WORK_PHONE, '' as GENDER, '' as ETHNICITY, '' as DATE_OF_BIRTH, '' as RECEIVE_TXT_MESSAGE_IND, '' as STUDENT_ENROLLMENT_STATUS, '' as STUDENT_ENROLLMENT_GOAL from "
				+ " instructorTbl inst";
		sql += ") user";

		sql += " left join authorizationTbl " + " authn on user.USER_ID = authn.primary_user_id";
		sql += " where user.USER_ID is not null and length(trim(user.USER_ID)) > 0";

		final SQLSelectParserParser parser = parse(sql);
		runParsertest(sql, parser);
	}

	@Test
	public void getRegistrationSqlTest() {
		/*
		 * Registration COLUMNS: RECORD_TYPE, ACTION, TERM_ID, PRIMARY_USER_ID,
		 * GROUP_ID, CLASSIFICATION, OVERALL_GPA, TERM_GPA
		 */

		String query = " SELECT reg.record_type as RECORD_TYPE, reg.action as ACTION, reg.term_id as TERM_ID, "
				+ " reg.primary_user_id as PRIMARY_USER_ID, reg.group_id as GROUP_ID,reg.classification as CLASSIFICATION, "
				+ " gpatbl.cum_gpa as OVERALL_GPA, gpatbl.term_gpa as TERM_GPA " + " from "
				+ " studentAcademicTbl reg  left outer join "
				+ " (select coalesce(cgpa.primary_user_id,tgpa.primary_user_id) as primary_user_id, coalesce(cgpa.term_id,tgpa.term_id) as term_id, "
				+ " coalesce(cgpa.key_value,'') as cum_gpa, coalesce(tgpa.key_value,'') as term_gpa from (select * from "
				+ " studentTermDataTbl where key_column='cumGPAKey') cgpa "
				+ " full outer join (select * from  studentTermDataTbl  where key_column='termGPAKey' ) tgpa "
				+ " on (cgpa.primary_user_id=tgpa.primary_user_id and "
				+ " cgpa.term_id=tgpa.term_id ) ) gpatbl on (reg.primary_user_id=gpatbl.primary_user_id and reg.term_id=gpatbl.term_id) "
				+ " inner join termFilterTbl  tf on reg.term_id = tf.term_id";
		final SQLSelectParserParser parser = parse(query);
		runParsertest(query, parser);
	}

	@Test
	public void getTermSqlTest() {
		/*
		 * Term COLUMNS: RECORD_TYPE, ACTION, EXTERNAL_ID, NAME, BEGIN_DATE,
		 * END_DATE
		 */
		String query = "select term.record_type as RECORD_TYPE, term.action as ACTION,  term.external_id as EXTERNAL_ID, term.name as NAME, datestr(term.begin_date, "
				+ " 'TERM_SOURCE_DATE_FORMAT', 'SSCPLUS_DEFAULT_DATE_FORMAT') as BEGIN_DATE, datestr(term.end_date, 'TERM_SOURCE_DATE_FORMAT', "
				+ "'SSCPLUS_DEFAULT_DATE_FORMAT') as END_DATE from academicPeriodTbl " + " term";
		final SQLSelectParserParser parser = parse(query);
		runParsertest(query, parser);
	}

	@Test
	public void getCourseSqlTest() {
		/*
		 * Course COLUMNS: RECORD_TYPE, ACTION, EXTERNAL_ID, COURSE_ID, TITLE,
		 * CREDIT_HOURS
		 */
		String query = "select crs.record_type as RECORD_TYPE,crs.action as ACTION,concat_ws('-',crs.subject_code,crs.course_number) as EXTERNAL_ID, concat_ws('-',crs.subject_code,crs.course_number) as COURSE_ID, "
				+ " crs.course_title as TITLE, COALESCE(crs.credit_hour_low,crs.credit_hour_high,0) as CREDIT_HOURS from "
				+ " courseTbl crs";
		final SQLSelectParserParser parser = parse(query);
		runParsertest(query, parser);
	}

	@Test
	public void getSectionSqlTest() {
		// ********* ERROR: Reference in Hive ARRAY object: "sched_arr[0].col1
		// as BEGIN_DATE"
		/*
		 * Section COLUMNS: RECORD_TYPE, ACTION, TERM_ID, COURSE_EXTERNAL_ID,
		 * SECTION_NAME, SECTION_TAGS,BEGIN_DATE, END_DATE, START_TIME,
		 * END_TIME, MEETING_DAYS, LOCATION, ADDITIONAL MEETINGS (repeat meeting
		 * info)
		 */
		String query = "select record_type as RECORD_TYPE, action as ACTION,term_id as TERM_ID,course_external_id as COURSE_EXTERNAL_ID ,section_name as SECTION_NAME,"
				+ "'' as section_tags,  sched_arr[0].col1 as BEGIN_DATE, sched_arr[0].col2 as END_DATE, sched_arr[0].col3 as BEGIN_TIME, sched_arr[0].col4 as END_TIME, sched_arr[0].col5 as MEETING_DAYS,sched_arr[0].col6 as LOCATION from ( "
				+ "select record_type,action,term_id,course_external_id,section_name, collectarray(sched) as sched_arr from ("
				+ "select record_type,action,term_id,course_external_id,section_name,struct(begin_date,end_date,start_time,end_time,meeting_days,location) as sched from ("
				+ "select s.record_type as RECORD_TYPE,s.action as ACTION, s.term_code as TERM_ID, concat_ws('-',s.subject_code,s.course_number) as COURSE_EXTERNAL_ID,"
				+ "case when s.section_name is null or length(trim(s.section_name))=0 then '' else s.section_name end as SECTION_NAME, case when sec.meet_start_date is not null then datestr(sec.meet_start_date, '"
				+ " SECTION_SOURCE_DATE_FORMAT', 'SSCPLUS_DEFAULT_DATE_FORMAT')  else '' end as BEGIN_DATE, case when sec.meet_end_date is not null then datestr(sec.meet_end_date, '"
				+ "SECTION_SOURCE_DATE_FORMAT', 'SSCPLUS_DEFAULT_DATE_FORMAT') else ''  end as END_DATE, "
				+ "case when sec.meet_start_time is null or length(trim(sec.meet_start_time))=0 or length(trim(sec.meet_start_time)) < 4 then '' else concat_ws(':',substr(sec.meet_start_time,1,2),substr(sec.meet_start_time,3,2)) end as START_TIME, "
				+ "case when sec.meet_end_time is null or length(trim(sec.meet_end_time))=0 or length(trim(sec.meet_end_time)) < 4 then '' else concat_ws(':',substr(sec.meet_end_time,1,2),substr(sec.meet_end_time,3,2)) end as END_TIME, "
				+ "coalesce(concat(sec.meet_sunday,meet_monday,meet_tuesday,meet_wednesday,meet_thursday,meet_friday,meet_saturday),'') as MEETING_DAYS,"
				+ "case when (meet_building_code is null or length(trim(meet_building_code))=0) and (meet_room_code is null or length(trim(meet_room_code))=0) then '' "
				+ "when (meet_building_code is null or length(trim(meet_building_code))=0) or (meet_room_code is null or length(trim(meet_room_code))=0) then concat(trim(meet_building_code),trim(meet_room_code)) "
				+ " else concat_ws('-',meet_building_code,meet_room_code) end as LOCATION from "
				+ " sectionTbl s inner join termFilterTbl tf on " + "s.term_code = tf.term_id "
				+ " left join sectionMeetTbl "
				+ " sec on (sec.course_ref_no=s.course_ref_no and sec.term_code=s.term_code) "
				+ ") sub_sec) mid_sec group by record_type,action,term_id,course_external_id,section_name) main_sec";
		final SQLSelectParserParser parser = parse(query);
		// TODO: change parser to recognize this example
		// runParsertest(query, parser);
	}

	@Test
	public void getSectionSqlV6Test() {
		/*
		 * Section COLUMNS: RECORD_TYPE, ACTION, TERM_ID, COURSE_EXTERNAL_ID,
		 * SECTION_NAME, SECTION_TAGS
		 */
		final String query = "select s.record_type as RECORD_TYPE, " + "s.action as ACTION, "
				+ "s.term_code as TERM_ID, " + "concat_ws('-',s.subject_code,s.course_number) as COURSE_EXTERNAL_ID, "
				+ "case  " + "when s.section_name is null or length(trim(s.section_name))=0  " + "then ''  "
				+ "else s.section_name  " + "end as SECTION_NAME, " + "s.section_tag as SECTION_TAGS "
				+ "from sectionTbl s  " + "inner join termFilterTbl tf  " + "on s.term_code = tf.term_id ";
		final SQLSelectParserParser parser = parse(query);
		runParsertest(query, parser);
	}

	@Test
	public void getEnrollmentSqlTest() {
		/*
		 * Enrollment COLUMNS: RECORD_TYPE, ACTION, PRIMARY_USER_ID, TERM_ID,
		 * COURSE_EXTERNAL_ID, SECTION_NAME, MIDTERM_GRADE, FINAL_GRADE
		 */
		String query = "select cw.record_type as RECORD_TYPE, cw.action as ACTION, cw.student_id as PRIMARY_USER_ID, cw.term_code as TERM_ID, concat_ws('-',s.subject_code,s.course_number) "
				+ " as COURSE_EXTERNAL_ID, s.section_name as SECTION_NAME,cw.midterm_grade as MIDTERM_GRADE,cw.final_grade as "
				+ " FINAL_GRADE"
				+ " from courseWorkTbl  cw,  sectionTbl  s inner join termFilterTbl tf on cw.term_code = tf.term_id "
				+ "where cw.course_ref_no=s.course_ref_no and cw.term_code=s.term_code and cw.registration_status_cd in ('regCodes')";
		final SQLSelectParserParser parser = parse(query);
		runParsertest(query, parser);
	}

	@Test
	public void getInstructionSqlTest() {
		/*
		 * Instructor Assignments COLUMNS: RECORD_TYPE, ACTION, TERM_ID,
		 * COURSE_EXTERNAL_ID, SECTION_NAME, PRIMARY_USER_ID
		 */
		String query = "select ia.record_type as RECORD_TYPE, ia.action as ACTION, ia.term_code as TERM_ID, concat_ws('-',ia.subject_code,ia.course_number) "
				+ "as COURSE_EXTERNAL_ID,ia.section_name as SECTION_NAME ,ia.instructor_id as PRIMARY_USER_ID "
				+ "from instructorAssgnmtTbl  ia inner join termFilterTbl  tf on " + "ia.term_code = tf.term_id";
		final SQLSelectParserParser parser = parse(query);
		runParsertest(query, parser);
	}

	@Test
	public void getGroupingSqlTest() {
		/*
		 * Grouping COLUMNS: RECORD_TYPE, ACTION, GROUP_ID, PRIMARY_USER_ID
		 */
		String sql = " select user.secondary_record_type as RECORD_TYPE,user.action as ACTION,user.group_id as GROUP_ID,user.primary_user_id as PRIMARY_USER_ID from "
				+ " (";
		String[] groupingTbls = new String[10];
		groupingTbls[0] = "firstTable";
		groupingTbls[1] = "secondTable";
		groupingTbls[2] = "thirdTable";
		groupingTbls[3] = "fourthTable";

		String unionStatement = " select secondary_record_type,action,group_id,primary_user_id from zeroTable ";
		for (int i = 0; i < 4; i++) {
			unionStatement += " union all ";
			unionStatement += " select secondary_record_type,action,group_id,primary_user_id from " + groupingTbls[i]
					+ " ";
		}

		sql += unionStatement
				+ ") user where user.primary_user_id is not null and length(trim(user.primary_user_id)) > 0";
		final SQLSelectParserParser parser = parse(sql);
		runParsertest(sql, parser);
	}

	@Test
	public void getDeclarationSqlTest() {
		// *************** ERROR: Hive syntax not handled: "lateral view
		// explode"
		/*
		 * Declaration COLUMNS: RECORD_TYPE, ACTION, PRIMARY_USER_ID, MAJOR_ID
		 */
		String sql = "select record_type as record_type,action as action,primary_user_id as primary_user_id, "
				+ "maj_items.col1 as major_id from (select record_type,action,primary_user_id, "
				+ "array(struct(major_cd_1), struct(major_cd_2), struct(major_cd_3), struct(major_cd_4)) as major_arr from "
				+ "(select record_type,action, primary_user_id, major_cd_1,major_cd_2,major_cd_3, major_cd_4 from "
				+ "(select a.record_type,a.action,primary_user_id, major_cd_1,major_cd_2,major_cd_3,major_cd_4,";
		sql += "rank() over (partition by primary_user_id order by b.begin_date desc ,b.end_date desc) term_rank from "
				+ " studentMajorTbl a,  academicPeriodTbl "
				+ " b where a.term_id=b.external_id and a.major_cd_1 is not null and length(trim(a.major_cd_1))>0 ) tbl where term_rank=1) res) fin_res ";
		// sql += "lateral view explode(major_arr) exploded_table as maj_items
		// where length(trim(maj_items.col1)) > 0";
		final SQLSelectParserParser parser = parse(sql);
		runParsertest(sql, parser);
	}

	@Test
	public void getTagSqlTest() {
		/*
		 * Tag COLUMNS: RECORD_TYPE, ACTION, TAG, GROUP ID, PRIMARY_USER_ID
		 */
		String sql = " select rec_type as RECORD_TYPE, action_cd as ACTION, "
				+ "tag_name as TAG, grp_id as GROUP_ID, user_id as PRIMARY_USER_ID from "
				+ " tagTbl where tag_name is not null and length(trim(tag_name)) > 0 "
				+ "and grp_id is not null and length(trim(grp_id)) > 0 "
				+ "and user_id is not null and length(trim(user_id)) > 0 ";
		final SQLSelectParserParser parser = parse(sql);
		runParsertest(sql, parser);
	}

	@Test
	public void getAuthorizeSqlTest() {
		/*
		 * Authorize COLUMNS: RECORD_TYPE, ACTION, ROLE_ID, PRIMARY_USER_ID
		 */
		String sql = " select record_type as RECORD_TYPE, action as ACTION, "
				+ "role_id as ROLE_ID, primary_user_id as PRIMARY_USER_ID from  authorizeTbl "
				+ " where role_id is not null and length(trim(role_id)) > 0 "
				+ "and primary_user_id is not null and length(trim(primary_user_id)) > 0 ";
		final SQLSelectParserParser parser = parse(sql);
		runParsertest(sql, parser);
	}

	@Test
	public void getCategorySqlTest() {
		/*
		 * Category COLUMNS: RECORD_TYPE, ACTION, EXTERNAL_ID, NAME, GROUP_ID
		 */
		String sql = " select record_type as RECORD_TYPE, action as ACTION, "
				+ "external_id as EXTERNAL_ID, name as NAME, group_id as GROUP_ID from "
				+ " categoryTbl where external_id is not null and length(trim(external_id)) > 0 "
				+ "and name is not null and length(trim(name)) > 0 "
				+ "and group_id is not null and length(trim(group_id)) > 0 ";
		final SQLSelectParserParser parser = parse(sql);
		runParsertest(sql, parser);
	}

	@Test
	public void getCategorizeSqlTest() {
		/*
		 * Categorize COLUMNS: RECORD_TYPE, ACTION, CATEGORY_ID, PRIMARY_ID
		 */
		String sql = " select record_type as RECORD_TYPE, action as ACTION, "
				+ "category_id as CATEGORy_id, primary_id as PRIMARY_ID from  categorizeTbl "
				+ " where (category_id is not null and length(trim(category_id)) > 0 "
				+ "and primary_id is not null and length(trim(primary_id)) > 0) ";
		final SQLSelectParserParser parser = parse(sql);
		runParsertest(sql, parser);
	}

	@Test
	public void getRelationshipSqlTest() {
		/*
		 * Relationship COLUMNS: RECORD_TYPE, ACTION, NAME,
		 * PARENT_PRIMARY_USER_ID, CHILD_PRIMARY_USER_ID, GROUP_ID
		 */
		String sql = "select record_type as RECORD_TYPE, action as ACTION, "
				+ "name as NAME, parent_primary_user_id as PARENT_PRIMARY_USER_ID,"
				+ "child_primary_user_id as CHILD_PRIMARY_USER_ID, group_id as GROUP_ID from relationshipTbl "
				+ " where lower(trim(name)) in ('advisor','coach','professor','tutor') "
				+ " and parent_primary_user_id is not null and length(trim(parent_primary_user_id)) > 0"
				+ " and child_primary_user_id is not null and length(trim(child_primary_user_id)) > 0"
				+ " and group_id is not null and length(trim(group_id)) > 0";
		final SQLSelectParserParser parser = parse(sql);
		runParsertest(sql, parser);
	}

	@Test
	public void getSectionMeetingSqlTest() {

		String sql = "select sec.record_type as RECORD_TYPE,sec.action as ACTION, sec.term_code as TERM_ID,concat_ws('-',s.subject_code,s.course_number) as COURSE_EXTERNAL_ID, "
				+ "case when s.section_name is null or length(trim(s.section_name))=0 then '' else s.section_name end as SECTION_NAME, "
				+ "case when sec.meet_start_date is not null then datestr(sec.meet_start_date, 'SECTION_SOURCE_DATE_FORMAT', "
				+ "'SSCPLUS_DEFAULT_DATE_FORMAT')  else '' end as BEGIN_DATE, "
				+ "case when sec.meet_end_date is not null then datestr(sec.meet_end_date, 'SECTION_SOURCE_DATE_FORMAT',"
				+ " 'SSCPLUS_DEFAULT_DATE_FORMAT') else ''  end as END_DATE, "
				+ "case when sec.meet_start_time is null or length(trim(sec.meet_start_time))=0 or length(trim(sec.meet_start_time)) < 4 then '' else concat_ws(':',substr(sec.meet_start_time,1,2),substr(sec.meet_start_time,3,2)) end as START_TIME, "
				+ "case when sec.meet_end_time is null or length(trim(sec.meet_end_time))=0 or length(trim(sec.meet_end_time)) < 4 then '' else concat_ws(':',substr(sec.meet_end_time,1,2),substr(sec.meet_end_time,3,2)) end as END_TIME, "
				+ "coalesce(concat(sec.meet_sunday,meet_monday,meet_tuesday,meet_wednesday,meet_thursday,meet_friday,meet_saturday),'') as MEETING_DAYS, "
				+ "case when (meet_building_code is null or length(trim(meet_building_code))=0) and (meet_room_code is null or length(trim(meet_room_code))=0) then '' "
				+ "when (meet_building_code is null or length(trim(meet_building_code))=0) or (meet_room_code is null or length(trim(meet_room_code))=0) then concat(trim(meet_building_code),trim(meet_room_code)) "
				+ " else concat_ws('-',meet_building_code,meet_room_code) end as LOCATION from sectionMeetTbl sec inner join termFilterTbl tf on "
				+ "sec.term_code = tf.term_id " + " inner join sectionTbl "
				+ " s on (sec.course_ref_no=s.course_ref_no and sec.term_code=s.term_code)";
		final SQLSelectParserParser parser = parse(sql);
		runParsertest(sql, parser);
	}

	@Test
	public void createTermFilterTableTest() {
		String sql = "select external_id as term_id from " + "termStgTableName  a, (select min(term_rank) as term_rank "
				+ "from (" + "select curr_term_rank as term_rank from currentTermTableName union all "
				+ "select max(t1.term_rank) term_rank from termStgTableName  t1, "
				+ " currentTermTableName t2 where t1.term_rank < t2.curr_term_rank" + ") tbl" + ") b "
				+ "where a.term_rank >= b.term_rank";
		final SQLSelectParserParser parser = parse(sql);
		runParsertest(sql, parser);
	}

	@Test
	public void createCurrentTermTableTest() {
		String sql = "select min(term_rank) curr_term_rank from ( " + "select term_rank from "
				+ " termStgTableName  where unix_timestamp() between term_start and term_end " + "union all "
				+ "select max(term_rank) term_rank from termStgTableName " + " where unix_timestamp() >= term_start "
				+ ") tbl";
		final SQLSelectParserParser parser = parse(sql);
		runParsertest(sql, parser);
	}

	@Test
	public void createTermStgTableTest() {
		String sql = "select external_id, " + "rank() over (order by begin_date, end_date) term_rank, "
				+ "unix_timestamp(begin_date,'yyyyMMdd') term_start, "
				+ "unix_timestamp(end_date,'yyyyMMdd') term_end from  hiveTableName ";
		final SQLSelectParserParser parser = parse(sql);
		runParsertest(sql, parser);
	}

	/*****************************
	 * End Of GF Encoder Tests
	 */

	/*****************************
	 * Start Of Pagoda Style Tests
	 */

	@Test
	public void selectWithBasicTest() {
		String sql = "with first as (select a from mulch), " + "second as (select b from lawn) "
				+ " select * from first, second";
		final SQLSelectParserParser parser = parse(sql);
		runParsertest(sql, parser);
	}

	@Test
	public void selectWithPostgresUpsert() {
		String sql = "WITH upsert AS  " + " (UPDATE cat_concentration " + " SET concentration_desc = stvmajr_desc "
				+ " FROM bnr_stvmajr " + " RETURNING * ) " + " INSERT INTO cat_concentration "
				+ " (        concentration_code, " + " concentration_desc, " + " active_ind " + " ) values ("
				+ " SELECT stvmajr_code AS concentration_code " + " , stvmajr_desc AS concentration_desc "
				+ " , 'T' AS active_ind " + " FROM bnr_stvmajr " + " WHERE NOT EXISTS ( " + " SELECT *  "
				+ " FROM upsert " + " ) " + " AND stvmajr_valid_concentratn_ind = 'Y')";
		final SQLSelectParserParser parser = parse(sql);
		runParsertest(sql, parser);
	}

	@Test
	public void selectWithEmbeddedSelect() {
		String sql = "WITH upsert AS  " + " (Select " + " concentration_desc, stvmajr_desc " + " FROM bnr_stvmajr "
				+ "  ) " + " Select cat_concentration, " + " concentration_code, " + " concentration_desc, "
				+ " active_ind " + " FROM upsert ";
		final SQLSelectParserParser parser = parse(sql);
		runParsertest(sql, parser);
	}

	@Test
	public void selectWorkbooksDownfillTest() {
		String sql = "with downfill as ( " + " select  " + " student_id " + " , term_id " + " , major_cd_fill "
				+ " , college_cd_fill " + " , degree_cd_fill " + " , concentration_cd_fill " + " , major_cd_2_fill "
				+ " , college_cd_2_fill " + " , degree_cd_2_fill " + " , concentration_cd_2_fill " + " from ( "
				+ " SELECT " + " student_id " + " , major_cd " + " , term_id " + " , value_partition "
				+ " , first_value(major_cd) over (partition by student_id, value_partition order by term_row) as major_cd_fill "
				+ " , first_value(college_cd) over (partition by student_id, value_partition order by term_row) as college_cd_fill "
				+ " , first_value(degree_cd) over (partition by student_id, value_partition order by term_row) as degree_cd_fill "
				+ " , first_value(concentration_cd) over (partition by student_id, value_partition order by term_row) as concentration_cd_fill "
				+ " , first_value(major_cd_2) over (partition by student_id, value_partition order by term_row) as major_cd_2_fill "
				+ " , first_value(college_cd_2) over (partition by student_id, value_partition order by term_row) as college_cd_2_fill "
				+ " , first_value(degree_cd_2) over (partition by student_id, value_partition order by term_row) as degree_cd_2_fill "
				+ " , first_value(concentration_cd_2) over (partition by student_id, value_partition order by term_row) as concentration_cd_2_fill "
				+ " FROM ( " + " SELECT " + " student_id " + " , major_cd " + " , smt.term_id " + " , college_cd "
				+ " , degree_cd " + " , concentration_cd " + " , major_cd_2 " + " , college_cd_2 " + " , degree_cd_2 "
				+ " , concentration_cd_2 " + " , sum(case when major_cd is null then 0 else 1 end) "
				+ " over (partition by student_id order by term_row) as value_partition " + " , term_row "
				+ " 	  FROM student_major_term smt "
				+ " left join (select row_number() over(order by start_date asc) as term_row, term_id from standard_term) terms "
				+ " on terms.term_id = smt.term_id " + " ORDER BY 1,12 DESC " + " 	  ) sub1 " + " order by 1,3 desc "
				+ " ) sub2 " + " where sub2.major_cd is null " + " ) " + " update student_major_term smt set "
				+ " major_cd = downfill.major_cd_fill " + " , college_cd = downfill.college_cd_fill "
				+ " , degree_cd = downfill.degree_cd_fill " + " , concentration_cd = downfill.concentration_cd_fill "
				+ " , major_cd_2 = downfill.major_cd_2_fill " + " , college_cd_2 = downfill.college_cd_2_fill "
				+ " , degree_cd_2 = downfill.degree_cd_2_fill "
				+ " 	, concentration_cd_2 = downfill.concentration_cd_2_fill " + " from downfill "
				+ " where smt.student_id = downfill.student_id " + " and smt.term_id = downfill.term_id ";

		final SQLSelectParserParser parser = parse(sql);
		runParsertest(sql, parser);
	}

	@Test
	public void selectBasicUpdateTest() {
		String sql = "UPDATE employees SET emp_sales_count = acct_sales_count + 1, redder = greener  FROM accounts";
		final SQLSelectParserParser parser = parse(sql);
		runParsertest(sql, parser);
	}

	@Test
	public void selectBasic2UpdateTest() {
		// TODO: ITEM 11 - generate Interface list and proper Table Dictionary
		// from Update queries; Assign unknown symbols from source table to source table in Symbol tree
		String sql = "update this_table set outputA = column1, outputB = column2, outputC = column3 "
				+ " from that_table where this_table.key=that_table.key";
		final SQLSelectParserParser parser = parse(sql);
		runParsertest(sql, parser);
	}

	@Test
	public void selectBasicInsertTest() {
		// TODO: Item 13 - Generate proper Interface list from Insert statements
		String sql = "insert into employees  (emp_sales_count, redder)  values (select acct_sales_count + 1, greener  FROM accounts)";
		final SQLSelectParserParser parser = parse(sql);
		runParsertest(sql, parser);
	}

	/*
	 * UPDATE weather SET (temp_lo, temp_hi, prcp) = (temp_lo+1, temp_lo+15,
	 * DEFAULT) WHERE city = 'San Francisco' AND date = '2003-07-03'
	 * 
	 * UPDATE accounts SET (contact_first_name, contact_last_name) = (SELECT
	 * first_name, last_name FROM salesmen WHERE salesmen.id =
	 * accounts.sales_id);
	 * 
	 * UPDATE accounts SET contact_first_name = first_name, contact_last_name =
	 * last_name FROM salesmen WHERE salesmen.id = accounts.sales_id;
	 * 
	 * UPDATE summary s SET (sum_x, sum_y, avg_x, avg_y) = (SELECT sum(x),
	 * sum(y), avg(x), avg(y) FROM data d WHERE d.group_id = s.group_id);
	 * 
	 * UPDATE employees SET sales_count = sales_count + 1 WHERE id = (SELECT
	 * sales_person FROM accounts WHERE name = 'Acme Corporation');
	 * 
	 * UPDATE weather SET temp_lo = temp_lo+1, temp_hi = temp_lo+15, prcp =
	 * DEFAULT WHERE city = 'San Francisco' AND date = '2003-07-03' RETURNING
	 * temp_lo, temp_hi, prcp;
	 * 
	 * // WITH EXAMPLES
	 * 
	 * WITH moved_rows AS ( DELETE FROM products WHERE "date" >= '2010-10-01'
	 * AND "date" < '2010-11-01' RETURNING * ) INSERT INTO products_log SELECT *
	 * FROM moved_rows;
	 * 
	 * 
	 * WITH regional_sales AS ( SELECT region, SUM(amount) AS total_sales FROM
	 * orders GROUP BY region ), top_regions AS ( SELECT region FROM
	 * regional_sales WHERE total_sales > (SELECT SUM(total_sales)/10 FROM
	 * regional_sales) ) SELECT region, product, SUM(quantity) AS product_units,
	 * SUM(amount) AS product_sales FROM orders WHERE region IN (SELECT region
	 * FROM top_regions) GROUP BY region, product;
	 * 
	 * WITH t AS ( UPDATE products SET price = price * 1.05 RETURNING * ) SELECT
	 * * FROM products;
	 * 
	 * WITH t AS ( UPDATE accounts SET (contact_first_name, contact_last_name) =
	 * (SELECT first_name, last_name FROM salesmen WHERE salesmen.id =
	 * accounts.sales_id); RETURNING * ) insert into accounts values ((SELECT
	 * first_name, last_name FROM salesmen WHERE !exists t.id =
	 * accounts.sales_id))
	 * 
	 * INSERT INTO table [ ( column [, ...] ) ] { DEFAULT VALUES | VALUES ( {
	 * expression | DEFAULT } [, ...] ) [, ...] | query } [ RETURNING * |
	 * output_expression [ AS output_name ] [, ...] ]
	 */

	/**
	 * PREDICANDS TESTS
	 * 
	 * @param query
	 * @param parser
	 */

	@Test
	public void basicColumnPredicandTest() {
		String sql = "table1.emp_sales_count";
		final SQLSelectParserParser parser = parse(sql);
		runPredicandParsertest(sql, parser);
	}

	@Test
	public void basicColumnPredicandWithSubstitutionTest() {
		//TODO: Does not parse; qualified substitution should produce COLUMN substitution
		String sql = "table1.<emp_sales_count>";
		final SQLSelectParserParser parser = parse(sql);
		runPredicandParsertest(sql, parser);
	}

	@Test
	public void basicColumnSubstitutionTest() {
		//TODO: Type isn't being set; Substitution List isn't being filled
		String sql = "<emp_sales_count>";
		final SQLSelectParserParser parser = parse(sql);
		runPredicandParsertest(sql, parser);
	}

	@Test
	public void basicLiteralValuePredicandTest() {
		String sql = "'AA'";
		final SQLSelectParserParser parser = parse(sql);
		runPredicandParsertest(sql, parser);
	}

	@Test
	public void basicNullValuePredicandTest() {
		String sql = "null";
		final SQLSelectParserParser parser = parse(sql);
		runPredicandParsertest(sql, parser);
	}

	@Test
	public void concatenationPredicandTest() {
		String sql = "a || b || 'oops'";
		final SQLSelectParserParser parser = parse(sql);
		runPredicandParsertest(sql, parser);
	}

	@Test
	public void functionPredicandTest() {
		String sql = "concat_ws('-', crs.subject_code, crs.course_number) ";
		final SQLSelectParserParser parser = parse(sql);
		runPredicandParsertest(sql, parser);
	}

	@Test
	public void aggregateFunctionPredicandTest() {
		String sql = "max(scbcrse_eff_term)";
		final SQLSelectParserParser parser = parse(sql);
		runPredicandParsertest(sql, parser);
	}

	@Test
	public void caseFunctionPredicandTest() {
		String sql = "case when true then ‘Y’ when false then ‘N’ else ‘N’ end";
		final SQLSelectParserParser parser = parse(sql);
		runPredicandParsertest(sql, parser);
	}

	@Test
	public void selectLookupSubqueryPredicandTest() {
		String sql = "(SELECT aa.scbcrse_coll_code FROM scbcrse aa)";
		final SQLSelectParserParser parser = parse(sql);
		runPredicandParsertest(sql, parser);
	}

	@Test
	public void arithmeticExpressionPredicandTest() {
		String sql = "-(aa.scbcrse_coll_code * 6 - other) ";
		final SQLSelectParserParser parser = parse(sql);
		runPredicandParsertest(sql, parser);
	}

	/**
	 * CONDITION TESTS
	 * 
	 * @param query
	 * @param parser
	 */

	@Test
	public void conditionBasicConditionTest() {
		String sql = "table1.emp_sales_count >= 25";
		final SQLSelectParserParser parser = parse(sql);
		runConditionParsertest(sql, parser);
	}

	@Test
	public void conditionSimpleBooleanTest() {
		String sql = "true";
		final SQLSelectParserParser parser = parse(sql);
		runConditionParsertest(sql, parser);
	}

	@Test
	public void conditionListOfAndsV1Test() {
		String sql = "a=b and b=c and x >y";
		final SQLSelectParserParser parser = parse(sql);
		runConditionParsertest(sql, parser);
	}

	@Test
	public void conditionListOfAndsV2Test() {
		// Item 51 - Table Dictionary not created when condition parsing is performed on its own
		String sql = "a.a=b.b and a.b=b.c and a.x > b.y";
		final SQLSelectParserParser parser = parse(sql);
		runConditionParsertest(sql, parser);
	}

	@Test
	public void conditionListOfOrsTest() {
		String sql = "a=b or b=c or x >y";
		final SQLSelectParserParser parser = parse(sql);
		runConditionParsertest(sql, parser);
	}

	@Test
	public void conditionParentheticalTest() {
		String sql = "((a=b) or (b=c))";
		final SQLSelectParserParser parser = parse(sql);
		runConditionParsertest(sql, parser);
	}

	@Test
	public void conditionNotTest() {
		String sql = "not a = b";
		final SQLSelectParserParser parser = parse(sql);
		runConditionParsertest(sql, parser);
	}

	@Test
	public void conditionInTest() {
		String sql = "columnName in (25, 26)";
		final SQLSelectParserParser parser = parse(sql);
		runConditionParsertest(sql, parser);
	}

	@Test
	public void conditionIsNullTest() {
		String sql = "table1.emp_sales_count is null";
		final SQLSelectParserParser parser = parse(sql);
		runConditionParsertest(sql, parser);
	}

	@Test
	public void conditionIsNotNullTest() {
		String sql = "table1.emp_sales_count is not null";
		final SQLSelectParserParser parser = parse(sql);
		runConditionParsertest(sql, parser);
	}

	@Test
	public void substitutionConditionTest() {
		String sql = "<item> = 26";
		final SQLSelectParserParser parser = parse(sql);
		runConditionParsertest(sql, parser);
	}

	@Test
	public void conditionWithSubstitutionInV1Test() {
		// TODO: Does not parse; fails on "in" after the variable
		String sql = "<columnName> in (25, 26)";
		final SQLSelectParserParser parser = parse(sql);
		runConditionParsertest(sql, parser);
	}

	@Test
	public void conditionWithSubstitutionInV2Test() {
		// TODO: Does not parse; fails on "in" after the first variable
		String sql = "<columnName> in <inList>";
		final SQLSelectParserParser parser = parse(sql);
		runConditionParsertest(sql, parser);
	}

	@Test
	public void conditionWithSubstitutionInV3Test() {
		// TODO: Does not parse; can't handle in list type variable
		String sql = "column in <inList>";
		final SQLSelectParserParser parser = parse(sql);
		runConditionParsertest(sql, parser);
	}

	// *****************************
	// COMMON TEST METHODS

	private SqlParseEventWalker runParsertest(final String query, final SQLSelectParserParser parser) {
		return runSQLParsertest(query, parser, null, null);
	}

	private SqlParseEventWalker runSQLParsertest(final String query, final SQLSelectParserParser parser,
			HashMap<String, String> entityMap, HashMap<String, Map<String, String>> attributeMap) {
		try {
			System.out.println();
			// There should be zero errors
			SqlContext tree = parser.sql();
			final int numErrors = parser.getNumberOfSyntaxErrors();
			Assert.assertEquals("Expected no failures with " + query, 0, numErrors);

			SqlParseEventWalker extractor = new SqlParseEventWalker();
			if (entityMap != null)
				extractor.setEntityTableNameMap(entityMap);
			if (attributeMap != null)
				extractor.setAttributeColumnMap(attributeMap);
			ParseTreeWalker.DEFAULT.walk(extractor, tree);
			System.out.println("Result: " + extractor.getSqlTree());
			System.out.println("Interface: " + extractor.getInterface());
			System.out.println("Symbol Tree: " + extractor.getSymbolTable());
			System.out.println("Table Dictionary: " + extractor.getTableColumnMap());
			System.out.println("Substitution Variables: " + extractor.getSubstitutionsMap());
			return extractor;
		} catch (RecognitionException e) {
			System.err.println("Exception parsing eqn: " + query);
			System.err.println("Recognition Exception: " + e.getMessage());
			return null;
		}
	}

	private SqlParseEventWalker runPredicandParsertest(final String query, final SQLSelectParserParser parser) {
		try {
			System.out.println();
			// There should be zero errors
			Predicand_valueContext tree = parser.predicand_value();
			final int numErrors = parser.getNumberOfSyntaxErrors();
			Assert.assertEquals("Expected no failures with " + query, 0, numErrors);

			SqlParseEventWalker extractor = new SqlParseEventWalker();
			ParseTreeWalker.DEFAULT.walk(extractor, tree);
			System.out.println("Result: " + extractor.getSqlTree());
			System.out.println("Interface: " + extractor.getInterface());
			System.out.println("Symbol Tree: " + extractor.getSymbolTable());
			System.out.println("Table Dictionary: " + extractor.getTableColumnMap());
			System.out.println("Substitution Variables: " + extractor.getSubstitutionsMap());

			return extractor;
		} catch (RecognitionException e) {
			System.err.println("Exception parsing eqn: " + query);
			return null;
		} 
	}

	private void runConditionParsertest(final String query, final SQLSelectParserParser parser) {
		try {
			System.out.println();
			// There should be zero errors
			Condition_valueContext tree = parser.condition_value();
			final int numErrors = parser.getNumberOfSyntaxErrors();
			Assert.assertEquals("Expected no failures with " + query, 0, numErrors);

			SqlParseEventWalker extractor = new SqlParseEventWalker();
			ParseTreeWalker.DEFAULT.walk(extractor, tree);
			System.out.println("Result: " + extractor.getSqlTree());
			System.out.println("Interface: " + extractor.getInterface());
			System.out.println("Symbol Tree: " + extractor.getSymbolTable());
			System.out.println("Table Dictionary: " + extractor.getTableColumnMap());
			System.out.println("Substitution Variables: " + extractor.getSubstitutionsMap());

		} catch (RecognitionException e) {
			System.err.println("Exception parsing eqn: " + query);
		}
	}

	private static final SQLSelectParserParser parse(final String query) {
		CharStream input = new ANTLRInputStream(query);
		SQLSelectParserLexer lexer = new SQLSelectParserLexer(input);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		SQLSelectParserParser parser = new SQLSelectParserParser(tokens);

		return parser;
	}

}
