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
		//TODO: ITEM 24 - the concatenated elements work when in parentheses, otherwise grammar is indeterminate
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
	
	// Simple Select with Predicands (Casting and Not Casting)

	@Test
	public void basicSelectList1Test() {
		final String query = " SELECT * FROM tab1"; 

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=*, table_ref=*}}}, from={table={alias=null, table=tab1}}}}",
				extractor.getSqlTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={*=[@1,8:8='*',<285>,1:8]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={tab1={*=[@1,8:8='*',<285>,1:8]}, interface={*={column={name=*, table_ref=*}}}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void basicSelectList2Test() {
		final String query = " SELECT a,b,c FROM tab1"; 

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=a, table_ref=null}}, 2={column={name=b, table_ref=null}}, 3={column={name=c, table_ref=null}}}, from={table={alias=null, table=tab1}}}}",
				extractor.getSqlTree().toString());
		Assert.assertEquals("Interface is wrong", "[a, b, c]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={a=[@1,8:8='a',<299>,1:8], b=[@3,10:10='b',<299>,1:10], c=[@5,12:12='c',<299>,1:12]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={tab1={a=[@1,8:8='a',<299>,1:8], b=[@3,10:10='b',<299>,1:10], c=[@5,12:12='c',<299>,1:12]}, interface={a={column={name=a, table_ref=null}}, b={column={name=b, table_ref=null}}, c={column={name=c, table_ref=null}}}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void basicSelectList3Test() {
		final String query = " SELECT 1 + 2 as a,(1+2) b, (d) as c FROM tab1"; 

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=a, calc={left={literal=1}, right={literal=2}, operator=+}}, 2={parentheses={calc={left={literal=1}, right={literal=2}, operator=+}}, alias=b}, 3={parentheses={column={name=d, table_ref=null}}, alias=c}}, from={table={alias=null, table=tab1}}}}",
				extractor.getSqlTree().toString());
		Assert.assertEquals("Interface is wrong", "[a, b, c]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={d=[@15,29:29='d',<299>,1:29]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={tab1={d=[@15,29:29='d',<299>,1:29]}, interface={a={calc={left={literal=1}, right={literal=2}, operator=+}}, b={parentheses={calc={left={literal=1}, right={literal=2}, operator=+}}}, c={parentheses={column={name=d, table_ref=null}}}}}}",
				extractor.getSymbolTable().toString());
	}

	/* ===========================================================
	 * Basic Variable Name Tests
	 * Default names with spaces and mixed case
	 * ITEM 62: Substitution Names accept domain, entity and attribute notations like “<[DOMAIN].[ENTITY].[ATTRIBUTE]>”
	   ===========================================================*/
	@Test
	public void simpleVariableName1Test() {
		final String query = " SELECT a.<simple>, a.<with blanks in name> FROM tab1 as a"; 

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={substitution={name=<simple>, type=column}, table_ref=a}}, 2={column={substitution={name=<with blanks in name>, type=column}, table_ref=a}}}, from={table={alias=a, table=tab1}}}}",
				extractor.getSqlTree().toString());
		Assert.assertEquals("Interface is wrong", "[<with blanks in name>, <simple>]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<with blanks in name>=column, <simple>=column}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={<with blanks in name>={substitution={name=<with blanks in name>, type=column}}, <simple>={substitution={name=<simple>, type=column}}}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={a=tab1, tab1={<with blanks in name>={substitution={name=<with blanks in name>, type=column}}, <simple>={substitution={name=<simple>, type=column}}}, interface={<with blanks in name>={column={substitution={name=<with blanks in name>, type=column}, table_ref=a}}, <simple>={column={substitution={name=<simple>, type=column}, table_ref=a}}}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void extendedVariableName1Test() {
		final String query = " SELECT a.<[simple]>, a.<[DOMAIN].[ENTITY].[ATTRIBUTE]>, a.<[another].[item]> FROM <[DOMAIN].[ENTITY]>  as a "; 

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={substitution={name=<[simple]>, parts={1=[simple]}, type=column}, table_ref=a}}, 2={column={substitution={name=<[DOMAIN].[ENTITY].[ATTRIBUTE]>, parts={1=[DOMAIN], 2=[ENTITY], 3=[ATTRIBUTE]}, type=column}, table_ref=a}}, 3={column={substitution={name=<[another].[item]>, parts={1=[another], 2=[item]}, type=column}, table_ref=a}}}, from={table={alias=a, substitution={name=<[DOMAIN].[ENTITY]>, parts={1=[DOMAIN], 2=[ENTITY]}, type=tuple}}}}}",
				extractor.getSqlTree().toString());
		Assert.assertEquals("Interface is wrong", "[<[another].[item]>, <[DOMAIN].[ENTITY].[ATTRIBUTE]>, <[simple]>]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<[another].[item]>=column, <[DOMAIN].[ENTITY].[ATTRIBUTE]>=column, <[DOMAIN].[ENTITY]>=tuple, <[simple]>=column}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{<[DOMAIN].[ENTITY]>={<[another].[item]>={substitution={name=<[another].[item]>, parts={1=[another], 2=[item]}, type=column}}, <[DOMAIN].[ENTITY].[ATTRIBUTE]>={substitution={name=<[DOMAIN].[ENTITY].[ATTRIBUTE]>, parts={1=[DOMAIN], 2=[ENTITY], 3=[ATTRIBUTE]}, type=column}}, <[simple]>={substitution={name=<[simple]>, parts={1=[simple]}, type=column}}}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={a=<[DOMAIN].[ENTITY]>, <[DOMAIN].[ENTITY]>={<[another].[item]>={substitution={name=<[another].[item]>, parts={1=[another], 2=[item]}, type=column}}, <[DOMAIN].[ENTITY].[ATTRIBUTE]>={substitution={name=<[DOMAIN].[ENTITY].[ATTRIBUTE]>, parts={1=[DOMAIN], 2=[ENTITY], 3=[ATTRIBUTE]}, type=column}}, <[simple]>={substitution={name=<[simple]>, parts={1=[simple]}, type=column}}}, interface={<[another].[item]>={column={substitution={name=<[another].[item]>, parts={1=[another], 2=[item]}, type=column}, table_ref=a}}, <[DOMAIN].[ENTITY].[ATTRIBUTE]>={column={substitution={name=<[DOMAIN].[ENTITY].[ATTRIBUTE]>, parts={1=[DOMAIN], 2=[ENTITY], 3=[ATTRIBUTE]}, type=column}, table_ref=a}}, <[simple]>={column={substitution={name=<[simple]>, parts={1=[simple]}, type=column}, table_ref=a}}}}}",
				extractor.getSymbolTable().toString());
	}

	/* ===========================================================
	 * CAST Function Tests
	 * ITEM 58: Add support for CAST AS function
	 * ITEM 65: Snowflake, Hive and many Postgres Data Types
	 * ITEM 66: Snowflake's TRY_CAST function
	   ===========================================================*/
	@Test
	public void basicSelectListCasting1Test() {
		// TODO: ITEM 59 - Casting syntax style 1, in-line casting has not been implemented
		final String query = " SELECT 1 + 2 as a,(1+2)::varchar b, (d)::integer as c FROM tab1"; 

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "",
				extractor.getSqlTree().toString());
		Assert.assertEquals("Interface is wrong", "[a, b, c]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void basicSelectListCasting2Test() {
		final String query = " SELECT cast(col1 as boolean) a,cast(col2 as varchar(2)) b, cast(col3 as numeric(9,3)) as c FROM tab1"; 

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={function={function_name=cast, data_type={type=BOOLEAN}, type=CAST, value={column={name=col1, table_ref=null}}}, alias=a}, 2={function={function_name=cast, data_type={length=2, type=VARCHAR}, type=CAST, value={column={name=col2, table_ref=null}}}, alias=b}, 3={function={function_name=cast, data_type={precision=9, scale=3, type=NUMERIC}, type=CAST, value={column={name=col3, table_ref=null}}}, alias=c}}, from={table={alias=null, table=tab1}}}}",
				extractor.getSqlTree().toString());
		Assert.assertEquals("Interface is wrong", "[a, b, c]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={col2=[@11,37:40='col2',<299>,1:37], col3=[@22,65:68='col3',<299>,1:65], col1=[@3,13:16='col1',<299>,1:13]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={tab1={col2=[@11,37:40='col2',<299>,1:37], col3=[@22,65:68='col3',<299>,1:65], col1=[@3,13:16='col1',<299>,1:13]}, interface={a={function={function_name=cast, data_type={type=BOOLEAN}, type=CAST, value={column={name=col1, table_ref=null}}}}, b={function={function_name=cast, data_type={length=2, type=VARCHAR}, type=CAST, value={column={name=col2, table_ref=null}}}}, c={function={function_name=cast, data_type={precision=9, scale=3, type=NUMERIC}, type=CAST, value={column={name=col3, table_ref=null}}}}}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void basicSelectListTryCasting2Test() {
		// ITEM 66: Snowflake's TRY_CAST function
		final String query = " SELECT TRY_cast(col1 as boolean) a,cast(col2 as varchar(2)) b, cast(col3 as numeric(9,3)) as c FROM tab1"; 

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={function={function_name=TRY_cast, data_type={type=BOOLEAN}, type=TRY_CAST, value={column={name=col1, table_ref=null}}}, alias=a}, 2={function={function_name=cast, data_type={length=2, type=VARCHAR}, type=CAST, value={column={name=col2, table_ref=null}}}, alias=b}, 3={function={function_name=cast, data_type={precision=9, scale=3, type=NUMERIC}, type=CAST, value={column={name=col3, table_ref=null}}}, alias=c}}, from={table={alias=null, table=tab1}}}}",
				extractor.getSqlTree().toString());
		Assert.assertEquals("Interface is wrong", "[a, b, c]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={col2=[@11,41:44='col2',<299>,1:41], col3=[@22,69:72='col3',<299>,1:69], col1=[@3,17:20='col1',<299>,1:17]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={tab1={col2=[@11,41:44='col2',<299>,1:41], col3=[@22,69:72='col3',<299>,1:69], col1=[@3,17:20='col1',<299>,1:17]}, interface={a={function={function_name=TRY_cast, data_type={type=BOOLEAN}, type=TRY_CAST, value={column={name=col1, table_ref=null}}}}, b={function={function_name=cast, data_type={length=2, type=VARCHAR}, type=CAST, value={column={name=col2, table_ref=null}}}}, c={function={function_name=cast, data_type={precision=9, scale=3, type=NUMERIC}, type=CAST, value={column={name=col3, table_ref=null}}}}}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void castInDifferentContextsWhereConditionTest() {
		final String query = " SELECT colu FROM tab1 where cast(cola as boolean) is true"; 

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=colu, table_ref=null}}}, from={table={alias=null, table=tab1}}, where={condition={left={function={function_name=cast, data_type={type=BOOLEAN}, type=CAST, value={column={name=cola, table_ref=null}}}}}, operator=is true}}}",
				extractor.getSqlTree().toString());
		Assert.assertEquals("Interface is wrong", "[colu]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={colu=[@1,8:11='colu',<299>,1:8], cola=[@7,34:37='cola',<299>,1:34]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={tab1={colu=[@1,8:11='colu',<299>,1:8], cola=[@7,34:37='cola',<299>,1:34]}, interface={colu={column={name=colu, table_ref=null}}}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void castInDifferentContextsCalculationTest() {
		final String query = " SELECT colu + cast(cola as numeric (9)) FROM tab1"; 

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={calc={left={column={name=colu, table_ref=null}}, right={function={function_name=cast, data_type={precision=9, type=NUMERIC}, type=CAST, value={column={name=cola, table_ref=null}}}}, operator=+}}}, from={table={alias=null, table=tab1}}}}",
				extractor.getSqlTree().toString());
		Assert.assertEquals("Interface is wrong", "[unnamed_0]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={colu=[@1,8:11='colu',<299>,1:8], cola=[@5,20:23='cola',<299>,1:20]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={tab1={colu=[@1,8:11='colu',<299>,1:8], cola=[@5,20:23='cola',<299>,1:20]}, interface={unnamed_0={calc={left={column={name=colu, table_ref=null}}, right={function={function_name=cast, data_type={precision=9, type=NUMERIC}, type=CAST, value={column={name=cola, table_ref=null}}}}, operator=+}}}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void castInDifferentContextsJoinConditionTest() {
		final String query = " SELECT tab1.colu FROM tab1 join tab2 on tab1.cola = cast(tab2.cola as CHARACTER VARYING)"; 

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=colu, table_ref=tab1}}}, from={join={1={table={alias=null, table=tab1}}, 2={join=join, on={condition={left={column={name=cola, table_ref=tab1}}, right={function={function_name=cast, data_type={type=CHARACTER VARYING}, type=CAST, value={column={name=cola, table_ref=tab2}}}}, operator==}}}, 3={table={alias=null, table=tab2}}}}}}",
				extractor.getSqlTree().toString());
		Assert.assertEquals("Interface is wrong", "[colu]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={colu=[@1,8:11='tab1',<299>,1:8], cola=[@9,41:44='tab1',<299>,1:41]}, tab2={cola=[@15,58:61='tab2',<299>,1:58]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={tab1={colu=[@1,8:11='tab1',<299>,1:8], cola=[@9,41:44='tab1',<299>,1:41]}, tab2={cola=[@15,58:61='tab2',<299>,1:58]}, interface={colu={column={name=colu, table_ref=tab1}}}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void castInDifferentContextsGroupByTest() {
		final String query = " SELECT cast(cola as boolean) a, max(cast(cola as boolean)) b FROM tab1 group by cast(cola as boolean)"; 

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={function={function_name=cast, data_type={type=BOOLEAN}, type=CAST, value={column={name=cola, table_ref=null}}}, alias=a}, 2={function={function_name=max, qualifier=null, parameters={function={function_name=cast, data_type={type=BOOLEAN}, type=CAST, value={column={name=cola, table_ref=null}}}}}, alias=b}}, from={table={alias=null, table=tab1}}, groupby={1={function={function_name=cast, data_type={type=BOOLEAN}, type=CAST, value={column={name=cola, table_ref=null}}}}}}}",
				extractor.getSqlTree().toString());
		Assert.assertEquals("Interface is wrong", "[a, b]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={cola=[@25,86:89='cola',<299>,1:86]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={tab1={cola=[@25,86:89='cola',<299>,1:86]}, interface={a={function={function_name=cast, data_type={type=BOOLEAN}, type=CAST, value={column={name=cola, table_ref=null}}}}, b={function={function_name=max, qualifier=null, parameters={function={function_name=cast, data_type={type=BOOLEAN}, type=CAST, value={column={name=cola, table_ref=null}}}}}}}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void castInDifferentContextsOrderByTest() {
		final String query = " SELECT a, b FROM tab1 order by cast(cola as boolean)"; 

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=a, table_ref=null}}, 2={column={name=b, table_ref=null}}}, orderby={1={null_order=null, predicand={function={function_name=cast, data_type={type=BOOLEAN}, type=CAST, value={column={name=cola, table_ref=null}}}}, sort_order=ASC}}, from={table={alias=null, table=tab1}}}}",
				extractor.getSqlTree().toString());
		Assert.assertEquals("Interface is wrong", "[a, b]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={a=[@1,8:8='a',<299>,1:8], b=[@3,11:11='b',<299>,1:11], cola=[@10,37:40='cola',<299>,1:37]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={tab1={a=[@1,8:8='a',<299>,1:8], b=[@3,11:11='b',<299>,1:11], cola=[@10,37:40='cola',<299>,1:37]}, interface={a={column={name=a, table_ref=null}}, b={column={name=b, table_ref=null}}}}}",
				extractor.getSymbolTable().toString());
	}

	// ITEM 65: All Hive and Snowflake data types, and most Postgres data types
	@Test
	public void basicCastingVariableTypesWithLengthsTest() {
		final String query = " SELECT cast('a' as character varying (10)) a,"
				+ " cast('a' as national character) b,"
				+ " cast('a' as national character (256)) c,"
				+ " cast('a' as national character varying) as d,"
				+ " cast('a' as national character varying (1000)) as e"
				+ " FROM tab1"; 

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={function={function_name=cast, data_type={length=10, type=CHARACTER VARYING}, type=CAST, value={literal='a'}}, alias=a}, 2={function={function_name=cast, data_type={type=NATIONAL CHARACTER}, type=CAST, value={literal='a'}}, alias=b}, 3={function={function_name=cast, data_type={length=256, type=NATIONAL CHARACTER}, type=CAST, value={literal='a'}}, alias=c}, 4={function={function_name=cast, data_type={type=NATIONAL CHARACTER VARYING}, type=CAST, value={literal='a'}}, alias=d}, 5={function={function_name=cast, data_type={length=1000, type=NATIONAL CHARACTER VARYING}, type=CAST, value={literal='a'}}, alias=e}}, from={table={alias=null, table=tab1}}}}",
				extractor.getSqlTree().toString());
		Assert.assertEquals("Interface is wrong", "[a, b, c, d, e]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={tab1={}, interface={a={function={function_name=cast, data_type={length=10, type=CHARACTER VARYING}, type=CAST, value={literal='a'}}}, b={function={function_name=cast, data_type={type=NATIONAL CHARACTER}, type=CAST, value={literal='a'}}}, c={function={function_name=cast, data_type={length=256, type=NATIONAL CHARACTER}, type=CAST, value={literal='a'}}}, d={function={function_name=cast, data_type={type=NATIONAL CHARACTER VARYING}, type=CAST, value={literal='a'}}}, e={function={function_name=cast, data_type={length=1000, type=NATIONAL CHARACTER VARYING}, type=CAST, value={literal='a'}}}}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void basicCastingPrecisionTypesTest() {
		final String query = " SELECT cast('a' as numeric) a,"
				+ " cast('a' as double precision) b,"
				+ " cast('a' as decimal (9, 7)) c,"
				+ " cast('a' as double   precision (98,7)) as d,"
				+ " cast('a' as FloaT(2)) as e"
				+ " FROM tab1"; 

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={function={function_name=cast, data_type={type=NUMERIC}, type=CAST, value={literal='a'}}, alias=a}, 2={function={function_name=cast, data_type={type=DOUBLE PRECISION}, type=CAST, value={literal='a'}}, alias=b}, 3={function={function_name=cast, data_type={precision=9, scale=7, type=DECIMAL}, type=CAST, value={literal='a'}}, alias=c}, 4={function={function_name=cast, data_type={precision=98, scale=7, type=DOUBLE PRECISION}, type=CAST, value={literal='a'}}, alias=d}, 5={function={function_name=cast, data_type={precision=2, type=FLOAT}, type=CAST, value={literal='a'}}, alias=e}}, from={table={alias=null, table=tab1}}}}",
				extractor.getSqlTree().toString());
		Assert.assertEquals("Interface is wrong", "[a, b, c, d, e]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={tab1={}, interface={a={function={function_name=cast, data_type={type=NUMERIC}, type=CAST, value={literal='a'}}}, b={function={function_name=cast, data_type={type=DOUBLE PRECISION}, type=CAST, value={literal='a'}}}, c={function={function_name=cast, data_type={precision=9, scale=7, type=DECIMAL}, type=CAST, value={literal='a'}}}, d={function={function_name=cast, data_type={precision=98, scale=7, type=DOUBLE PRECISION}, type=CAST, value={literal='a'}}}, e={function={function_name=cast, data_type={precision=2, type=FLOAT}, type=CAST, value={literal='a'}}}}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void basicCastingCompundStaticTypesTest() {
		final String query = " SELECT cast('a' as text) a,"
				+ " cast('a' as float4) b,"
				+ " cast('a' as time with  time zone) c,"
				+ " cast('a' as timestamp  with time  zone) as d,"
				+ " cast('a' as inet4) as e"
				+ " FROM tab1"; 

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={function={function_name=cast, data_type={type=TEXT}, type=CAST, value={literal='a'}}, alias=a}, 2={function={function_name=cast, data_type={type=FLOAT4}, type=CAST, value={literal='a'}}, alias=b}, 3={function={function_name=cast, data_type={type=TIME WITH TIME ZONE}, type=CAST, value={literal='a'}}, alias=c}, 4={function={function_name=cast, data_type={type=TIMESTAMP WITH TIME ZONE}, type=CAST, value={literal='a'}}, alias=d}, 5={function={function_name=cast, data_type={type=INET4}, type=CAST, value={literal='a'}}, alias=e}}, from={table={alias=null, table=tab1}}}}",
				extractor.getSqlTree().toString());
		Assert.assertEquals("Interface is wrong", "[a, b, c, d, e]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={tab1={}, interface={a={function={function_name=cast, data_type={type=TEXT}, type=CAST, value={literal='a'}}}, b={function={function_name=cast, data_type={type=FLOAT4}, type=CAST, value={literal='a'}}}, c={function={function_name=cast, data_type={type=TIME WITH TIME ZONE}, type=CAST, value={literal='a'}}}, d={function={function_name=cast, data_type={type=TIMESTAMP WITH TIME ZONE}, type=CAST, value={literal='a'}}}, e={function={function_name=cast, data_type={type=INET4}, type=CAST, value={literal='a'}}}}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void realisticCastingTest() {
		final String query = "select distinct" + 
				"        cast(s.school_id as varchar(100)) as school_id" + 
				"      , cast(s.school_key as varchar(50))  as school_ceeb_code" + 
				"      , cast(case when schl_type.value is null or schl_type.value = ''" + 
				"          then d.dataset_name else schl_type.value end as varchar(100)) as school_type" + 
				"      , cast(case when schl_cat.value is null or schl_cat.value = ''" + 
				"          then d.dataset_name else schl_cat.value end as varchar(100)) as school_category" + 
				"      , cast(case when s.school_name is null or s.school_name = '' " + 
				"          then sl.lookup_school_name else s.school_name end as varchar(100)) as school_name" + 
				"      , cast(case when s.school_country is null or s.school_country = ''" + 
				"          then a.address_country else s.school_country end as varchar(100)) as school_country" + 
				"      , cast(case when s.school_region is null or s.school_region = ''" + 
				"          then a.address_region else s.school_region end as varchar(50)) as school_state" + 
				"      , cast(case when s.school_city is null or s.school_city = ''" + 
				"          then a.address_city else s.school_city end as varchar(255)) school_city" + 
				"      , cast(a.address_county as varchar(100)) as school_county" + 
				"      , cast(a.address_zip as varchar(100)) as school_zip" + 
				"      , cast(a.address_street as varchar(1000)) as school_address" + 
				"      , cast(dr.dataset_row_created as timestamp) as crm_created_at" + 
				"      , cast(dr.dataset_row_updated as timestamp) as crm_updated_at" + 
				"  from school s" + 
				"    left join <slate_lookup_school> sl" + 
				"      on sl.lookup_school_id = s.school_key" + 
				"    left join <slate_dataset_row> dr" + 
				"      on dr.dataset_row_key = s.school_key" + 
				"    left join <slate_dataset> d" + 
				"      on d.dataset_id = dr.dataset_row_dataset" + 
				"      and d.dataset_name = 'Organizations'" + 
				"    left join <slate_address> a" + 
				"      on a.address_record = dr.dataset_row_id" + 
				"      and a.address_rank_overall = 1" + 
				"    left join schl_type" + 
				"      on schl_type.field_record = dr.dataset_row_id" + 
				"    left join schl_cat" + 
				"        on schl_cat.field_record = dr.dataset_row_id"; 

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={11={function={function_name=cast, data_type={length=1000, type=VARCHAR}, type=CAST, value={column={name=address_street, table_ref=a}}}, alias=school_address}, 12={function={function_name=cast, data_type={type=TIMESTAMP}, type=CAST, value={column={name=dataset_row_created, table_ref=dr}}}, alias=crm_created_at}, 13={function={function_name=cast, data_type={type=TIMESTAMP}, type=CAST, value={column={name=dataset_row_updated, table_ref=dr}}}, alias=crm_updated_at}, 1={function={function_name=cast, data_type={length=100, type=VARCHAR}, type=CAST, value={column={name=school_id, table_ref=s}}}, alias=school_id}, 2={function={function_name=cast, data_type={length=50, type=VARCHAR}, type=CAST, value={column={name=school_key, table_ref=s}}}, alias=school_ceeb_code}, 3={function={function_name=cast, data_type={length=100, type=VARCHAR}, type=CAST, value={case={clauses={1={then={column={name=dataset_name, table_ref=d}}, when={or={1={condition={left={column={name=value, table_ref=schl_type}}, operator=is null}}, 2={condition={left={column={name=value, table_ref=schl_type}}, right={literal=''}, operator==}}}}}}, else={column={name=value, table_ref=schl_type}}}}}, alias=school_type}, 4={function={function_name=cast, data_type={length=100, type=VARCHAR}, type=CAST, value={case={clauses={1={then={column={name=dataset_name, table_ref=d}}, when={or={1={condition={left={column={name=value, table_ref=schl_cat}}, operator=is null}}, 2={condition={left={column={name=value, table_ref=schl_cat}}, right={literal=''}, operator==}}}}}}, else={column={name=value, table_ref=schl_cat}}}}}, alias=school_category}, 5={function={function_name=cast, data_type={length=100, type=VARCHAR}, type=CAST, value={case={clauses={1={then={column={name=lookup_school_name, table_ref=sl}}, when={or={1={condition={left={column={name=school_name, table_ref=s}}, operator=is null}}, 2={condition={left={column={name=school_name, table_ref=s}}, right={literal=''}, operator==}}}}}}, else={column={name=school_name, table_ref=s}}}}}, alias=school_name}, 6={function={function_name=cast, data_type={length=100, type=VARCHAR}, type=CAST, value={case={clauses={1={then={column={name=address_country, table_ref=a}}, when={or={1={condition={left={column={name=school_country, table_ref=s}}, operator=is null}}, 2={condition={left={column={name=school_country, table_ref=s}}, right={literal=''}, operator==}}}}}}, else={column={name=school_country, table_ref=s}}}}}, alias=school_country}, 7={function={function_name=cast, data_type={length=50, type=VARCHAR}, type=CAST, value={case={clauses={1={then={column={name=address_region, table_ref=a}}, when={or={1={condition={left={column={name=school_region, table_ref=s}}, operator=is null}}, 2={condition={left={column={name=school_region, table_ref=s}}, right={literal=''}, operator==}}}}}}, else={column={name=school_region, table_ref=s}}}}}, alias=school_state}, 8={function={function_name=cast, data_type={length=255, type=VARCHAR}, type=CAST, value={case={clauses={1={then={column={name=address_city, table_ref=a}}, when={or={1={condition={left={column={name=school_city, table_ref=s}}, operator=is null}}, 2={condition={left={column={name=school_city, table_ref=s}}, right={literal=''}, operator==}}}}}}, else={column={name=school_city, table_ref=s}}}}}, alias=school_city}, 9={function={function_name=cast, data_type={length=100, type=VARCHAR}, type=CAST, value={column={name=address_county, table_ref=a}}}, alias=school_county}, 10={function={function_name=cast, data_type={length=100, type=VARCHAR}, type=CAST, value={column={name=address_zip, table_ref=a}}}, alias=school_zip}}, from={join={11={table={alias=null, table=schl_type}}, 12={join=left, on={condition={left={column={name=field_record, table_ref=schl_cat}}, right={column={name=dataset_row_id, table_ref=dr}}, operator==}}}, 13={table={alias=null, table=schl_cat}}, 1={table={alias=s, table=school}}, 2={join=left, on={condition={left={column={name=lookup_school_id, table_ref=sl}}, right={column={name=school_key, table_ref=s}}, operator==}}}, 3={table={alias=sl, substitution={name=<slate_lookup_school>, type=tuple}}}, 4={join=left, on={condition={left={column={name=dataset_row_key, table_ref=dr}}, right={column={name=school_key, table_ref=s}}, operator==}}}, 5={table={alias=dr, substitution={name=<slate_dataset_row>, type=tuple}}}, 6={join=left, on={and={1={condition={left={column={name=dataset_id, table_ref=d}}, right={column={name=dataset_row_dataset, table_ref=dr}}, operator==}}, 2={condition={left={column={name=dataset_name, table_ref=d}}, right={literal='Organizations'}, operator==}}}}}, 7={table={alias=d, substitution={name=<slate_dataset>, type=tuple}}}, 8={join=left, on={and={1={condition={left={column={name=address_record, table_ref=a}}, right={column={name=dataset_row_id, table_ref=dr}}, operator==}}, 2={condition={left={column={name=address_rank_overall, table_ref=a}}, right={literal=1}, operator==}}}}}, 9={table={alias=a, substitution={name=<slate_address>, type=tuple}}}, 10={join=left, on={condition={left={column={name=field_record, table_ref=schl_type}}, right={column={name=dataset_row_id, table_ref=dr}}, operator==}}}}}}}",
				extractor.getSqlTree().toString());
		Assert.assertEquals("Interface is wrong", "[school_type, crm_created_at, school_name, school_city, school_category, school_state, school_id, school_country, school_ceeb_code, school_address, school_county, crm_updated_at, school_zip]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<slate_lookup_school>=tuple, <slate_address>=tuple, <slate_dataset>=tuple, <slate_dataset_row>=tuple}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{schl_type={value=[@50,236:244='schl_type',<299>,1:236], field_record=[@357,1828:1836='schl_type',<299>,1:1828]}, school={school_id=[@4,28:28='s',<299>,1:28], school_country=[@149,712:712='s',<299>,1:712], school_name=[@116,553:553='s',<299>,1:553], school_city=[@215,1026:1026='s',<299>,1:1026], school_region=[@182,874:874='s',<299>,1:874], school_key=[@314,1553:1553='s',<299>,1:1553]}, <slate_lookup_school>={lookup_school_id=[@298,1453:1454='sl',<299>,1:1453], lookup_school_name=[@112,526:527='sl',<299>,1:526]}, <slate_address>={address_county=[@229,1085:1085='a',<299>,1:1085], address_region=[@178,852:852='a',<299>,1:852], address_record=[@340,1724:1724='a',<299>,1:1724], address_country=[@145,689:689='a',<299>,1:689], address_street=[@257,1205:1205='a',<299>,1:1205], address_rank_overall=[@348,1770:1770='a',<299>,1:1770], address_zip=[@243,1148:1148='a',<299>,1:1148], address_city=[@211,1006:1006='a',<299>,1:1006]}, schl_cat={value=[@83,390:397='schl_cat',<299>,1:390], field_record=[@368,1903:1910='schl_cat',<299>,1:1903]}, <slate_dataset>={dataset_name=[@330,1652:1652='d',<299>,1:1652], dataset_id=[@322,1605:1605='d',<299>,1:1605]}, <slate_dataset_row>={dataset_row_created=[@271,1270:1271='dr',<299>,1:1270], dataset_row_updated=[@282,1337:1338='dr',<299>,1:1337], dataset_row_key=[@310,1532:1533='dr',<299>,1:1532], dataset_row_id=[@372,1927:1928='dr',<299>,1:1927], dataset_row_dataset=[@326,1620:1621='dr',<299>,1:1620]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={a=<slate_address>, d=<slate_dataset>, interface={school_type={function={function_name=cast, data_type={length=100, type=VARCHAR}, type=CAST, value={case={clauses={1={then={column={name=dataset_name, table_ref=d}}, when={or={1={condition={left={column={name=value, table_ref=schl_type}}, operator=is null}}, 2={condition={left={column={name=value, table_ref=schl_type}}, right={literal=''}, operator==}}}}}}, else={column={name=value, table_ref=schl_type}}}}}}, crm_created_at={function={function_name=cast, data_type={type=TIMESTAMP}, type=CAST, value={column={name=dataset_row_created, table_ref=dr}}}}, school_name={function={function_name=cast, data_type={length=100, type=VARCHAR}, type=CAST, value={case={clauses={1={then={column={name=lookup_school_name, table_ref=sl}}, when={or={1={condition={left={column={name=school_name, table_ref=s}}, operator=is null}}, 2={condition={left={column={name=school_name, table_ref=s}}, right={literal=''}, operator==}}}}}}, else={column={name=school_name, table_ref=s}}}}}}, school_city={function={function_name=cast, data_type={length=255, type=VARCHAR}, type=CAST, value={case={clauses={1={then={column={name=address_city, table_ref=a}}, when={or={1={condition={left={column={name=school_city, table_ref=s}}, operator=is null}}, 2={condition={left={column={name=school_city, table_ref=s}}, right={literal=''}, operator==}}}}}}, else={column={name=school_city, table_ref=s}}}}}}, school_category={function={function_name=cast, data_type={length=100, type=VARCHAR}, type=CAST, value={case={clauses={1={then={column={name=dataset_name, table_ref=d}}, when={or={1={condition={left={column={name=value, table_ref=schl_cat}}, operator=is null}}, 2={condition={left={column={name=value, table_ref=schl_cat}}, right={literal=''}, operator==}}}}}}, else={column={name=value, table_ref=schl_cat}}}}}}, school_state={function={function_name=cast, data_type={length=50, type=VARCHAR}, type=CAST, value={case={clauses={1={then={column={name=address_region, table_ref=a}}, when={or={1={condition={left={column={name=school_region, table_ref=s}}, operator=is null}}, 2={condition={left={column={name=school_region, table_ref=s}}, right={literal=''}, operator==}}}}}}, else={column={name=school_region, table_ref=s}}}}}}, school_id={function={function_name=cast, data_type={length=100, type=VARCHAR}, type=CAST, value={column={name=school_id, table_ref=s}}}}, school_country={function={function_name=cast, data_type={length=100, type=VARCHAR}, type=CAST, value={case={clauses={1={then={column={name=address_country, table_ref=a}}, when={or={1={condition={left={column={name=school_country, table_ref=s}}, operator=is null}}, 2={condition={left={column={name=school_country, table_ref=s}}, right={literal=''}, operator==}}}}}}, else={column={name=school_country, table_ref=s}}}}}}, school_ceeb_code={function={function_name=cast, data_type={length=50, type=VARCHAR}, type=CAST, value={column={name=school_key, table_ref=s}}}}, school_address={function={function_name=cast, data_type={length=1000, type=VARCHAR}, type=CAST, value={column={name=address_street, table_ref=a}}}}, school_county={function={function_name=cast, data_type={length=100, type=VARCHAR}, type=CAST, value={column={name=address_county, table_ref=a}}}}, crm_updated_at={function={function_name=cast, data_type={type=TIMESTAMP}, type=CAST, value={column={name=dataset_row_updated, table_ref=dr}}}}, school_zip={function={function_name=cast, data_type={length=100, type=VARCHAR}, type=CAST, value={column={name=address_zip, table_ref=a}}}}}, <slate_address>={address_county=[@229,1085:1085='a',<299>,1:1085], address_region=[@178,852:852='a',<299>,1:852], address_record=[@340,1724:1724='a',<299>,1:1724], address_country=[@145,689:689='a',<299>,1:689], address_street=[@257,1205:1205='a',<299>,1:1205], address_rank_overall=[@348,1770:1770='a',<299>,1:1770], address_zip=[@243,1148:1148='a',<299>,1:1148], address_city=[@211,1006:1006='a',<299>,1:1006]}, schl_cat={value=[@83,390:397='schl_cat',<299>,1:390], field_record=[@368,1903:1910='schl_cat',<299>,1:1903]}, dr=<slate_dataset_row>, s=school, schl_type={value=[@50,236:244='schl_type',<299>,1:236], field_record=[@357,1828:1836='schl_type',<299>,1:1828]}, school={school_id=[@4,28:28='s',<299>,1:28], school_country=[@149,712:712='s',<299>,1:712], school_name=[@116,553:553='s',<299>,1:553], school_city=[@215,1026:1026='s',<299>,1:1026], school_region=[@182,874:874='s',<299>,1:874], school_key=[@314,1553:1553='s',<299>,1:1553]}, <slate_lookup_school>={lookup_school_name=[@112,526:527='sl',<299>,1:526], lookup_school_id=[@298,1453:1454='sl',<299>,1:1453]}, sl=<slate_lookup_school>, <slate_dataset>={dataset_id=[@322,1605:1605='d',<299>,1:1605], dataset_name=[@330,1652:1652='d',<299>,1:1652]}, <slate_dataset_row>={dataset_row_created=[@271,1270:1271='dr',<299>,1:1270], dataset_row_id=[@372,1927:1928='dr',<299>,1:1927], dataset_row_updated=[@282,1337:1338='dr',<299>,1:1337], dataset_row_key=[@310,1532:1533='dr',<299>,1:1532], dataset_row_dataset=[@326,1620:1621='dr',<299>,1:1620]}}}",
				extractor.getSymbolTable().toString());
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
		Assert.assertEquals("Table Dictionary is wrong", "{third={a=[@11,43:43='a',<299>,1:43], *=[@1,8:8='a',<299>,1:8]}, fourth={b=[@15,49:49='b',<299>,1:49]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={a=third, b=fourth, third={a=[@11,43:43='a',<299>,1:43], *=[@1,8:8='a',<299>,1:8]}, fourth={b=[@15,49:49='b',<299>,1:49]}, interface={*={column={name=*, table_ref=a}}}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void basicLeftJoinWithOnTest() {
		final String query = " SELECT a.* FROM third a left join fourth b on  a.a = b.b "; 

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=*, table_ref=a}}}, from={join={1={table={alias=a, table=third}}, 2={join=left, on={condition={left={column={name=a, table_ref=a}}, right={column={name=b, table_ref=b}}, operator==}}}, 3={table={alias=b, table=fourth}}}}}}",
				extractor.getSqlTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{third={a=[@12,48:48='a',<299>,1:48], *=[@1,8:8='a',<299>,1:8]}, fourth={b=[@16,54:54='b',<299>,1:54]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={a=third, b=fourth, third={a=[@12,48:48='a',<299>,1:48], *=[@1,8:8='a',<299>,1:8]}, fourth={b=[@16,54:54='b',<299>,1:54]}, interface={*={column={name=*, table_ref=a}}}}}",
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
		Assert.assertEquals("Table Dictionary is wrong", "{third={a=[@12,43:43='a',<299>,1:43], *=[@1,8:8='a',<299>,1:8]}, fourth={b=[@16,49:49='b',<299>,1:49]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={a=third, b=fourth, third={a=[@12,43:43='a',<299>,1:43], *=[@1,8:8='a',<299>,1:8]}, fourth={b=[@16,49:49='b',<299>,1:49]}, interface={*={column={name=*, table_ref=a}}}}}",
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
		Assert.assertEquals("Table Dictionary is wrong", "{third={*=[@1,8:8='a',<299>,1:8]}, fourth={}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={a=third, b=fourth, third={*=[@1,8:8='a',<299>,1:8]}, fourth={}, interface={*={column={name=*, table_ref=a}}}}}",
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
		Assert.assertEquals("Table Dictionary is wrong", "{third={*=[@1,8:8='a',<299>,1:8]}, fourth={}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={a=third, b=fourth, third={*=[@1,8:8='a',<299>,1:8]}, fourth={}, interface={*={column={name=*, table_ref=a}}}}}",
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
		Assert.assertEquals("Table Dictionary is wrong", "{third={*=[@1,8:8='a',<299>,1:8]}, fourth={}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={a=third, b=fourth, third={*=[@1,8:8='a',<299>,1:8]}, fourth={}, interface={*={column={name=*, table_ref=a}}}}}",
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
		Assert.assertEquals("Table Dictionary is wrong", "{third={*=[@1,8:8='*',<285>,1:8]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={third={*=[@1,8:8='*',<285>,1:8]}, interface={*={column={name=*, table_ref=*}}}}}",
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
		Assert.assertEquals("Symbol Table is wrong", "{query0={<tuple variable>={}, third={}, interface={*={column={name=*, table_ref=*}}}, two=<tuple variable>, unknown={*=[@1,8:8='*',<285>,1:8]}}}",
				extractor.getSymbolTable().toString());
	}

	// THIS IS FINE: IT SHOULDN"T ALLOW TUPLE VARIABLES WITHOUT ALIASES BCAUSE WE CAN"T FIGURE OUT WHAT KIND OF VARIABLE IT IS
//	@Test
//	public void tableListWithTupleVariableV2() {
//		// TODO: ITEM 28 - Doesn't parse tuple variable in a from list without an alias
//		final String query = " SELECT * FROM third, <tuple variable> ";
//
//		final SQLSelectParserParser parser = parse(query);
//		SqlParseEventWalker extractor = runParsertest(query, parser);
//		
//		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=*, table_ref=*}}}, from={extension={substitution={name=<extension>, type=join_extension}}, table={alias=null, table=third}}}}",
//				extractor.getSqlTree().toString());
//		Assert.assertEquals("Interface is wrong", "[*]", 
//				extractor.getInterface().toString());
//		Assert.assertEquals("Substitution List is wrong", "{<extension>=join_extension}", 
//				extractor.getSubstitutionsMap().toString());
//		Assert.assertEquals("Table Dictionary is wrong", "{third={*=[@1,8:8='*',<285>,1:8]}}",
//				extractor.getTableColumnMap().toString());
//		Assert.assertEquals("Symbol Table is wrong", "{query0={third={*=[@1,8:8='*',<285>,1:8]}, interface={*={column={name=*, table_ref=*}}}}}",
//				extractor.getSymbolTable().toString());
//	}

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
		Assert.assertEquals("Table Dictionary is wrong", "{third={*=[@1,8:8='*',<285>,1:8]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={third={*=[@1,8:8='*',<285>,1:8]}, interface={*={column={name=*, table_ref=*}}}}}",
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
		Assert.assertEquals("Symbol Table is wrong", "{query0={third={}, fourth={}, interface={*={column={name=*, table_ref=*}}}, F4=fourth, T3=third, unknown={*=[@1,8:8='*',<285>,1:8]}}}",
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
		Assert.assertEquals("Symbol Table is wrong", "{query0={third={}, fourth={}, interface={*={column={name=*, table_ref=*}}}, F4=fourth, T3=third, unknown={*=[@1,8:8='*',<285>,1:8]}}}",
				extractor.getSymbolTable().toString());
	}

	// End of Join Extensions

	// RESERVED WORD CONVERSION: ASC AND DESC TO BECOME NON-RESERVED WORDS
	// ITEM 64: Support ASC and DESC as column names
	// ITEM 69: Support RANK as a column name
	
	@Test
	public void descReservedWordTest() {
		final String query = "SELECT apple from tab1 order by apple desc";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=apple, table_ref=null}}}, orderby={1={null_order=null, predicand={column={name=apple, table_ref=null}}, sort_order=desc}}, from={table={alias=null, table=tab1}}}}",
				extractor.getSqlTree().toString());
		Assert.assertEquals("Interface is wrong", "[apple]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={apple=[@6,32:36='apple',<299>,1:32]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={tab1={apple=[@6,32:36='apple',<299>,1:32]}, interface={apple={column={name=apple, table_ref=null}}}}}",
				extractor.getSymbolTable().toString());
	}
	
	@Test
	public void descAsColumnTest() {
		final String query = "SELECT desc from tab1";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=desc, table_ref=null}}}, from={table={alias=null, table=tab1}}}}",
				extractor.getSqlTree().toString());
		Assert.assertEquals("Interface is wrong", "[desc]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={desc=[@1,7:10='desc',<74>,1:7]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={tab1={desc=[@1,7:10='desc',<74>,1:7]}, interface={desc={column={name=desc, table_ref=null}}}}}",
				extractor.getSymbolTable().toString());
	}
	
	@Test
	public void queryHasBothDescAsColumnAndReservedWordTest() {
		final String query = "SELECT desc from tab1 order by desc desc";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=desc, table_ref=null}}}, orderby={1={null_order=null, predicand={column={name=desc, table_ref=null}}, sort_order=desc}}, from={table={alias=null, table=tab1}}}}",
				extractor.getSqlTree().toString());
		Assert.assertEquals("Interface is wrong", "[desc]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={desc=[@6,31:34='desc',<74>,1:31]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={tab1={desc=[@6,31:34='desc',<74>,1:31]}, interface={desc={column={name=desc, table_ref=null}}}}}",
				extractor.getSymbolTable().toString());
	}
	
	@Test
	public void ascReservedWordTest() {
		final String query = "SELECT apple from tab1 order by apple asc";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=apple, table_ref=null}}}, orderby={1={null_order=null, predicand={column={name=apple, table_ref=null}}, sort_order=asc}}, from={table={alias=null, table=tab1}}}}",
				extractor.getSqlTree().toString());
		Assert.assertEquals("Interface is wrong", "[apple]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={apple=[@6,32:36='apple',<299>,1:32]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={tab1={apple=[@6,32:36='apple',<299>,1:32]}, interface={apple={column={name=apple, table_ref=null}}}}}",
				extractor.getSymbolTable().toString());
	}
	
	@Test
	public void ascAsColumnTest() {
		final String query = "SELECT asc from tab1";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=asc, table_ref=null}}}, from={table={alias=null, table=tab1}}}}",
				extractor.getSqlTree().toString());
		Assert.assertEquals("Interface is wrong", "[asc]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={asc=[@1,7:9='asc',<59>,1:7]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={tab1={asc=[@1,7:9='asc',<59>,1:7]}, interface={asc={column={name=asc, table_ref=null}}}}}",
				extractor.getSymbolTable().toString());
	}
	
	@Test
	public void queryHasBothAscAsColumnAndReservedWordTest() {
		final String query = "SELECT asc from tab1 order by asc asc";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=asc, table_ref=null}}}, orderby={1={null_order=null, predicand={column={name=asc, table_ref=null}}, sort_order=asc}}, from={table={alias=null, table=tab1}}}}",
				extractor.getSqlTree().toString());
		Assert.assertEquals("Interface is wrong", "[asc]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={asc=[@6,30:32='asc',<59>,1:30]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={tab1={asc=[@6,30:32='asc',<59>,1:30]}, interface={asc={column={name=asc, table_ref=null}}}}}",
				extractor.getSymbolTable().toString());
	}
	
	@Test
	public void rankReservedWordTest() {
		final String query = "SELECT rank() OVER (partition by k_stfd, kppi order by OBSERVATION_TM desc, row_num desc) AS key_rank from tab1";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=key_rank, window_function={over={partition_by={1={column={name=k_stfd, table_ref=null}}, 2={column={name=kppi, table_ref=null}}}, orderby={1={null_order=null, predicand={column={name=OBSERVATION_TM, table_ref=null}}, sort_order=desc}, 2={null_order=null, predicand={column={name=row_num, table_ref=null}}, sort_order=desc}}}, function={function_name=rank, parameters=null}}}}, from={table={alias=null, table=tab1}}}}",
				extractor.getSqlTree().toString());
		Assert.assertEquals("Interface is wrong", "[key_rank]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={row_num=[@16,76:82='row_num',<299>,1:76], k_stfd=[@8,33:38='k_stfd',<299>,1:33], kppi=[@10,41:44='kppi',<299>,1:41], OBSERVATION_TM=[@13,55:68='OBSERVATION_TM',<299>,1:55]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={tab1={row_num=[@16,76:82='row_num',<299>,1:76], k_stfd=[@8,33:38='k_stfd',<299>,1:33], kppi=[@10,41:44='kppi',<299>,1:41], OBSERVATION_TM=[@13,55:68='OBSERVATION_TM',<299>,1:55]}, interface={key_rank={window_function={over={partition_by={1={column={name=k_stfd, table_ref=null}}, 2={column={name=kppi, table_ref=null}}}, orderby={1={null_order=null, predicand={column={name=OBSERVATION_TM, table_ref=null}}, sort_order=desc}, 2={null_order=null, predicand={column={name=row_num, table_ref=null}}, sort_order=desc}}}, function={function_name=rank, parameters=null}}}}}}",
				extractor.getSymbolTable().toString());
	}
	
	@Test
	public void rankAsColumnTest() {
		final String query = "SELECT rank from tab1";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=rank, table_ref=null}}}, from={table={alias=null, table=tab1}}}}",
				extractor.getSqlTree().toString());
		Assert.assertEquals("Interface is wrong", "[rank]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={rank=[@1,7:10='rank',<124>,1:7]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={tab1={rank=[@1,7:10='rank',<124>,1:7]}, interface={rank={column={name=rank, table_ref=null}}}}}",
				extractor.getSymbolTable().toString());
	}
	
	@Test
	public void queryHasRankAsBothColumnAndReservedWordTest() {
		final String query = "SELECT rank() OVER (partition by k_stfd, kppi order by OBSERVATION_TM desc, row_num desc) AS rank from tab1";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=rank, window_function={over={partition_by={1={column={name=k_stfd, table_ref=null}}, 2={column={name=kppi, table_ref=null}}}, orderby={1={null_order=null, predicand={column={name=OBSERVATION_TM, table_ref=null}}, sort_order=desc}, 2={null_order=null, predicand={column={name=row_num, table_ref=null}}, sort_order=desc}}}, function={function_name=rank, parameters=null}}}}, from={table={alias=null, table=tab1}}}}",
				extractor.getSqlTree().toString());
		Assert.assertEquals("Interface is wrong", "[rank]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={row_num=[@16,76:82='row_num',<299>,1:76], k_stfd=[@8,33:38='k_stfd',<299>,1:33], kppi=[@10,41:44='kppi',<299>,1:41], OBSERVATION_TM=[@13,55:68='OBSERVATION_TM',<299>,1:55]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={tab1={row_num=[@16,76:82='row_num',<299>,1:76], k_stfd=[@8,33:38='k_stfd',<299>,1:33], kppi=[@10,41:44='kppi',<299>,1:41], OBSERVATION_TM=[@13,55:68='OBSERVATION_TM',<299>,1:55]}, interface={rank={window_function={over={partition_by={1={column={name=k_stfd, table_ref=null}}, 2={column={name=kppi, table_ref=null}}}, orderby={1={null_order=null, predicand={column={name=OBSERVATION_TM, table_ref=null}}, sort_order=desc}, 2={null_order=null, predicand={column={name=row_num, table_ref=null}}, sort_order=desc}}}, function={function_name=rank, parameters=null}}}}}}",
				extractor.getSymbolTable().toString());
	}
	
	@Test
	public void realisticRankAndDescColumnNameTest() {
		final String query = "SELECT 'Guide' AS app_name,  category, is_active, nk, rank, desc, student " + 
				"FROM  <Guide> AS Guide_Student_Conditions " + 
				" UNION ALL " + 
				"SELECT 'Nav' AS app_name, category, is_active, nk, rank, desc, student " + 
				"FROM <NAV> AS  Nav_Student_Conditions";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={union={1={select={1={alias=app_name, literal='Guide'}, 2={column={name=category, table_ref=null}}, 3={column={name=is_active, table_ref=null}}, 4={column={name=nk, table_ref=null}}, 5={column={name=rank, table_ref=null}}, 6={column={name=desc, table_ref=null}}, 7={column={name=student, table_ref=null}}}, from={table={alias=Guide_Student_Conditions, substitution={name=<Guide>, type=tuple}}}}, 2={union={qualifier=ALL, operator=UNION}}, 3={select={1={alias=app_name, literal='Nav'}, 2={column={name=category, table_ref=null}}, 3={column={name=is_active, table_ref=null}}, 4={column={name=nk, table_ref=null}}, 5={column={name=rank, table_ref=null}}, 6={column={name=desc, table_ref=null}}, 7={column={name=student, table_ref=null}}}, from={table={alias=Nav_Student_Conditions, substitution={name=<NAV>, type=tuple}}}}}}}",
				extractor.getSqlTree().toString());
		Assert.assertEquals("Interface is wrong", "[app_name, is_active, student, rank, category, nk, desc]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<Guide>=tuple, <NAV>=tuple}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{<Guide>={is_active=[@7,39:47='is_active',<299>,1:39], student=[@15,66:72='student',<299>,1:66], rank=[@11,54:57='rank',<124>,1:54], category=[@5,29:36='category',<299>,1:29], nk=[@9,50:51='nk',<299>,1:50], desc=[@13,60:63='desc',<74>,1:60]}, <NAV>={is_active=[@29,163:171='is_active',<299>,1:163], student=[@37,190:196='student',<299>,1:190], rank=[@33,178:181='rank',<124>,1:178], category=[@27,153:160='category',<299>,1:153], nk=[@31,174:175='nk',<299>,1:174], desc=[@35,184:187='desc',<74>,1:184]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{union2={query0={<Guide>={is_active=[@7,39:47='is_active',<299>,1:39], student=[@15,66:72='student',<299>,1:66], rank=[@11,54:57='rank',<124>,1:54], category=[@5,29:36='category',<299>,1:29], nk=[@9,50:51='nk',<299>,1:50], desc=[@13,60:63='desc',<74>,1:60]}, Guide_Student_Conditions=<Guide>, interface={app_name={literal='Guide'}, is_active={column={name=is_active, table_ref=null}}, student={column={name=student, table_ref=null}}, rank={column={name=rank, table_ref=null}}, category={column={name=category, table_ref=null}}, nk={column={name=nk, table_ref=null}}, desc={column={name=desc, table_ref=null}}}}, interface={app_name=query_column, is_active=query_column, student=query_column, rank=query_column, category=query_column, nk=query_column, desc=query_column}, query1={Nav_Student_Conditions=<NAV>, <NAV>={is_active=[@29,163:171='is_active',<299>,1:163], student=[@37,190:196='student',<299>,1:190], rank=[@33,178:181='rank',<124>,1:178], category=[@27,153:160='category',<299>,1:153], nk=[@31,174:175='nk',<299>,1:174], desc=[@35,184:187='desc',<74>,1:184]}, interface={app_name={literal='Nav'}, is_active={column={name=is_active, table_ref=null}}, student={column={name=student, table_ref=null}}, rank={column={name=rank, table_ref=null}}, category={column={name=category, table_ref=null}}, nk={column={name=nk, table_ref=null}}, desc={column={name=desc, table_ref=null}}}}}}",
				extractor.getSymbolTable().toString());
	}
	

	// END OF RESERVED WORD CONVERSION
	
	// WHERE CONDITION VARIATIONS
	
	@Test
	public void whereConditionWithNegationTest() {
		final String query = "SELECT apple from tab1 where not true";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=apple, table_ref=null}}}, from={table={alias=null, table=tab1}}, where={not={literal=true}}}}",
				extractor.getSqlTree().toString());
		Assert.assertEquals("Interface is wrong", "[apple]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={apple=[@1,7:11='apple',<299>,1:7]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={tab1={apple=[@1,7:11='apple',<299>,1:7]}, interface={apple={column={name=apple, table_ref=null}}}}}",
				extractor.getSymbolTable().toString());
	}
	
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
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={apple=[@1,7:11='apple',<299>,1:7]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={tab1={apple=[@1,7:11='apple',<299>,1:7]}, interface={apple={column={name=apple, table_ref=null}}}}}",
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
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={apple=[@1,7:11='apple',<299>,1:7], <subject code>={substitution={name=<subject code>, type=column}}}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={tab1={apple=[@1,7:11='apple',<299>,1:7], <subject code>={substitution={name=<subject code>, type=column}}}, interface={apple={column={name=apple, table_ref=null}}}}}",
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
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={apple=[@1,7:11='apple',<299>,1:7]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={tab1={apple=[@1,7:11='apple',<299>,1:7]}, interface={apple={column={name=apple, table_ref=null}}}}}",
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
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={apple=[@1,7:11='apple',<299>,1:7]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={tab1={apple=[@1,7:11='apple',<299>,1:7]}, interface={apple={column={name=apple, table_ref=null}}}}}",
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
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={apple=[@1,7:11='apple',<299>,1:7]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={tab1={apple=[@1,7:11='apple',<299>,1:7]}, interface={apple={column={name=apple, table_ref=null}}}}}",
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
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={apple=[@1,7:11='apple',<299>,1:7]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={tab1={apple=[@1,7:11='apple',<299>,1:7]}, interface={apple={column={name=apple, table_ref=null}}}}}",
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
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={subj=[@5,29:32='subj',<299>,1:29], apple=[@1,7:11='apple',<299>,1:7]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={tab1={subj=[@5,29:32='subj',<299>,1:29], apple=[@1,7:11='apple',<299>,1:7]}, interface={apple={column={name=apple, table_ref=null}}}}}",
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
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={subj=[@5,29:32='subj',<299>,1:29], apple=[@1,7:11='apple',<299>,1:7]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={tab1={subj=[@5,29:32='subj',<299>,1:29], apple=[@1,7:11='apple',<299>,1:7]}, interface={apple={column={name=apple, table_ref=null}}}}}",
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
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={apple=[@1,7:11='apple',<299>,1:7]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={tab1={apple=[@1,7:11='apple',<299>,1:7]}, interface={apple={column={name=apple, table_ref=null}}}}}",
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
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={apple=[@1,7:11='apple',<299>,1:7]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={tab1={apple=[@1,7:11='apple',<299>,1:7]}, interface={apple={column={name=apple, table_ref=null}}}}}",
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
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={apple=[@1,7:11='apple',<299>,1:7]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={tab1={apple=[@1,7:11='apple',<299>,1:7]}, interface={apple={column={name=apple, table_ref=null}}}}}",
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
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={apple=[@1,7:11='apple',<299>,1:7]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={tab1={apple=[@1,7:11='apple',<299>,1:7]}, interface={apple={column={name=apple, table_ref=null}}}}}",
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
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={apple=[@1,7:11='apple',<299>,1:7]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={tab1={apple=[@1,7:11='apple',<299>,1:7]}, interface={apple={column={name=apple, table_ref=null}}}}}",
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
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={apple=[@1,7:11='apple',<299>,1:7]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={tab1={apple=[@1,7:11='apple',<299>,1:7]}, interface={apple={column={name=apple, table_ref=null}}}}}",
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
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={banana=[@9,85:90='banana',<299>,1:85], apple=[@1,7:11='apple',<299>,1:7]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={tab1={banana=[@9,85:90='banana',<299>,1:85], apple=[@1,7:11='apple',<299>,1:7]}, interface={apple={column={name=apple, table_ref=null}}}}}",
				extractor.getSymbolTable().toString());
	}
	
	// ORDER BY TESTS
	
	@Test
	public void simpleOrderByTest() {
		final String query = "SELECT * from tab1 order by col1 ";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=*, table_ref=*}}}, orderby={1={null_order=null, predicand={column={name=col1, table_ref=null}}, sort_order=ASC}}, from={table={alias=null, table=tab1}}}}",
				extractor.getSqlTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={*=[@1,7:7='*',<285>,1:7], col1=[@6,28:31='col1',<299>,1:28]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={tab1={*=[@1,7:7='*',<285>,1:7], col1=[@6,28:31='col1',<299>,1:28]}, interface={*={column={name=*, table_ref=*}}}}}",
				extractor.getSymbolTable().toString());
	}
	
	@Test
	public void directionAscOrderByTest() {
		final String query = "SELECT * from tab1 order by col1 ASC";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=*, table_ref=*}}}, orderby={1={null_order=null, predicand={column={name=col1, table_ref=null}}, sort_order=ASC}}, from={table={alias=null, table=tab1}}}}",
				extractor.getSqlTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={*=[@1,7:7='*',<285>,1:7], col1=[@6,28:31='col1',<299>,1:28]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={tab1={*=[@1,7:7='*',<285>,1:7], col1=[@6,28:31='col1',<299>,1:28]}, interface={*={column={name=*, table_ref=*}}}}}",
				extractor.getSymbolTable().toString());
	}
	
	@Test
	public void directionDescOrderByTest() {
		final String query = "SELECT * from tab1 order by col1 DESC";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=*, table_ref=*}}}, orderby={1={null_order=null, predicand={column={name=col1, table_ref=null}}, sort_order=DESC}}, from={table={alias=null, table=tab1}}}}",
				extractor.getSqlTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={*=[@1,7:7='*',<285>,1:7], col1=[@6,28:31='col1',<299>,1:28]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={tab1={*=[@1,7:7='*',<285>,1:7], col1=[@6,28:31='col1',<299>,1:28]}, interface={*={column={name=*, table_ref=*}}}}}",
				extractor.getSymbolTable().toString());
	}
	
	@Test
	public void directionAscWithNullsDecoratorOrderByTest() {
		final String query = "SELECT * from tab1 order by col1 ASC nulls last";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=*, table_ref=*}}}, orderby={1={null_order=last, predicand={column={name=col1, table_ref=null}}, sort_order=ASC}}, from={table={alias=null, table=tab1}}}}",
				extractor.getSqlTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={*=[@1,7:7='*',<285>,1:7], col1=[@6,28:31='col1',<299>,1:28]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={tab1={*=[@1,7:7='*',<285>,1:7], col1=[@6,28:31='col1',<299>,1:28]}, interface={*={column={name=*, table_ref=*}}}}}",
				extractor.getSymbolTable().toString());
	}
	
	@Test
	public void multipleColumnsWithNullsDecoratorsOrderByTest() {
		final String query = "SELECT * from tab1 order by col1 ASC nulls last, col2 desc nulls first";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=*, table_ref=*}}}, orderby={1={null_order=last, predicand={column={name=col1, table_ref=null}}, sort_order=ASC}, 2={null_order=first, predicand={column={name=col2, table_ref=null}}, sort_order=desc}}, from={table={alias=null, table=tab1}}}}",
				extractor.getSqlTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={*=[@1,7:7='*',<285>,1:7], col2=[@11,49:52='col2',<299>,1:49], col1=[@6,28:31='col1',<299>,1:28]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={tab1={*=[@1,7:7='*',<285>,1:7], col2=[@11,49:52='col2',<299>,1:49], col1=[@6,28:31='col1',<299>,1:28]}, interface={*={column={name=*, table_ref=*}}}}}",
				extractor.getSymbolTable().toString());
	}

	// LIMIT Statements
	
	@Test
	public void simpleLimitTest() {
		final String query = "SELECT * from tab1 limit 100";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=*, table_ref=*}}}, limit={literal=100}, from={table={alias=null, table=tab1}}}}",
				extractor.getSqlTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={*=[@1,7:7='*',<285>,1:7]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={tab1={*=[@1,7:7='*',<285>,1:7]}, interface={*={column={name=*, table_ref=*}}}}}",
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
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={apple=[@1,7:11='apple',<299>,1:7], a=[@5,29:29='a',<299>,1:29], c=[@7,39:39='c',<299>,1:39], d=[@9,45:45='d',<299>,1:45]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={tab1={apple=[@1,7:11='apple',<299>,1:7], a=[@5,29:29='a',<299>,1:29], c=[@7,39:39='c',<299>,1:39], d=[@9,45:45='d',<299>,1:45]}, interface={apple={column={name=apple, table_ref=null}}}}}",
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
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={apple=[@1,7:11='apple',<299>,1:7], a=[@5,29:29='a',<299>,1:29], c=[@8,49:49='c',<299>,1:49], d=[@10,55:55='d',<299>,1:55]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={tab1={apple=[@1,7:11='apple',<299>,1:7], a=[@5,29:29='a',<299>,1:29], c=[@8,49:49='c',<299>,1:49], d=[@10,55:55='d',<299>,1:55]}, interface={apple={column={name=apple, table_ref=null}}}}}",
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
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={apple=[@1,7:11='apple',<299>,1:7], a=[@5,29:29='a',<299>,1:29], c=[@8,43:43='c',<299>,1:43], d=[@10,49:49='d',<299>,1:49]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={tab1={apple=[@1,7:11='apple',<299>,1:7], a=[@5,29:29='a',<299>,1:29], c=[@8,43:43='c',<299>,1:43], d=[@10,49:49='d',<299>,1:49]}, interface={apple={column={name=apple, table_ref=null}}}}}",
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
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={apple=[@1,7:11='apple',<299>,1:7], a=[@5,29:29='a',<299>,1:29], c=[@9,53:53='c',<299>,1:53], d=[@11,59:59='d',<299>,1:59]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={tab1={apple=[@1,7:11='apple',<299>,1:7], a=[@5,29:29='a',<299>,1:29], c=[@9,53:53='c',<299>,1:53], d=[@11,59:59='d',<299>,1:59]}, interface={apple={column={name=apple, table_ref=null}}}}}",
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
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={apple=[@1,7:11='apple',<299>,1:7], d=[@13,68:68='d',<299>,1:68], <c>={substitution={name=<c>, type=column}}}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={tab1={apple=[@1,7:11='apple',<299>,1:7], d=[@13,68:68='d',<299>,1:68], <c>={substitution={name=<c>, type=column}}}, interface={apple={column={name=apple, table_ref=null}}}}}",
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
		Assert.assertEquals("Table Dictionary is wrong", "{scbcrse={a=[@9,38:38='a',<299>,1:38], field1=[@6,29:34='field1',<299>,1:29], b=[@11,43:43='b',<299>,1:43], field2=[@17,59:64='field2',<299>,1:59], subj_code=[@25,95:103='subj_code',<299>,1:95]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={aa=scbcrse, scbcrse={a=[@9,38:38='a',<299>,1:38], field1=[@6,29:34='field1',<299>,1:29], b=[@11,43:43='b',<299>,1:43], field2=[@17,59:64='field2',<299>,1:59], subj_code=[@25,95:103='subj_code',<299>,1:95]}, interface={unnamed_1={concatenate={1={column={name=a, table_ref=null}}, 2={column={name=b, table_ref=null}}}}, unnamed_2={function={parameters={1={concatenate={1={literal='0'}, 2={column={name=field2, table_ref=null}}}}, 2={literal='0'}}, function_name=trim}}, unnamed_0={function={function_name=trim, parameters={qualifier=leading, trim_character={literal='0'}, value={column={name=field1, table_ref=null}}}}}}}}",
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
		Assert.assertEquals("Table Dictionary is wrong", "{other={*=[@18,79:79='*',<285>,1:79]}, scbcrse={item=[@14,63:66='item',<299>,1:63], *=[@1,7:7='*',<285>,1:7], subj_code=[@6,32:40='subj_code',<299>,1:32]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query1={aa=scbcrse, scbcrse={item=[@14,63:66='item',<299>,1:63], *=[@1,7:7='*',<285>,1:7], subj_code=[@6,32:40='subj_code',<299>,1:32]}, interface={*={column={name=*, table_ref=*}}}, query0={other={*=[@18,79:79='*',<285>,1:79]}, interface={*={column={name=*, table_ref=*}}}}}}",
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
		Assert.assertEquals("Table Dictionary is wrong", "{scbcrse={item=[@14,62:65='item',<299>,1:62], *=[@1,7:7='*',<285>,1:7], subj_code=[@6,31:39='subj_code',<299>,1:31]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={aa=scbcrse, scbcrse={item=[@14,62:65='item',<299>,1:62], *=[@1,7:7='*',<285>,1:7], subj_code=[@6,31:39='subj_code',<299>,1:31]}, interface={*={column={name=*, table_ref=*}}}}}",
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
		Assert.assertEquals("Table Dictionary is wrong", "{scbcrse={<subj_code>={substitution={name=<subj_code>, type=column}}, *=[@1,7:7='*',<285>,1:7]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={aa=scbcrse, scbcrse={<subj_code>={substitution={name=<subj_code>, type=column}}, *=[@1,7:7='*',<285>,1:7]}, interface={*={column={name=*, table_ref=*}}}}}",
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
		Assert.assertEquals("Table Dictionary is wrong", "{scbcrse={*=[@1,7:7='*',<285>,1:7]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={aa=scbcrse, scbcrse={*=[@1,7:7='*',<285>,1:7]}, interface={*={column={name=*, table_ref=*}}}}}",
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
		Assert.assertEquals("Table Dictionary is wrong", "{scbcrse={item=[@6,33:36='item',<299>,1:33], *=[@1,7:7='*',<285>,1:7]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={aa=scbcrse, scbcrse={item=[@6,33:36='item',<299>,1:33], *=[@1,7:7='*',<285>,1:7]}, interface={*={column={name=*, table_ref=*}}}}}",
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
		Assert.assertEquals("Table Dictionary is wrong", "{other={*=[@20,87:87='*',<285>,1:87]}, scbcrse={item=[@15,67:70='item',<299>,1:67], *=[@1,7:7='*',<285>,1:7], subj_code=[@6,32:40='subj_code',<299>,1:32]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query1={aa=scbcrse, scbcrse={item=[@15,67:70='item',<299>,1:67], *=[@1,7:7='*',<285>,1:7], subj_code=[@6,32:40='subj_code',<299>,1:32]}, interface={*={column={name=*, table_ref=*}}}, query0={other={*=[@20,87:87='*',<285>,1:87]}, interface={*={column={name=*, table_ref=*}}}}}}",
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
		Assert.assertEquals("Table Dictionary is wrong", "{scbcrse={item=[@15,66:69='item',<299>,1:66], *=[@1,7:7='*',<285>,1:7], subj_code=[@6,31:39='subj_code',<299>,1:31]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={aa=scbcrse, scbcrse={item=[@15,66:69='item',<299>,1:66], *=[@1,7:7='*',<285>,1:7], subj_code=[@6,31:39='subj_code',<299>,1:31]}, interface={*={column={name=*, table_ref=*}}}}}",
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
		Assert.assertEquals("Table Dictionary is wrong", "{scbcrse={<subj_code>={substitution={name=<subj_code>, type=column}}, *=[@1,7:7='*',<285>,1:7]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={aa=scbcrse, scbcrse={<subj_code>={substitution={name=<subj_code>, type=column}}, *=[@1,7:7='*',<285>,1:7]}, interface={*={column={name=*, table_ref=*}}}}}",
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
		Assert.assertEquals("Table Dictionary is wrong", "{scbcrse={*=[@1,7:7='*',<285>,1:7]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={aa=scbcrse, scbcrse={*=[@1,7:7='*',<285>,1:7]}, interface={*={column={name=*, table_ref=*}}}}}",
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
		Assert.assertEquals("Table Dictionary is wrong", "{scbcrse={item=[@6,33:36='item',<299>,1:33], *=[@1,7:7='*',<285>,1:7]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={aa=scbcrse, scbcrse={item=[@6,33:36='item',<299>,1:33], *=[@1,7:7='*',<285>,1:7]}, interface={*={column={name=*, table_ref=*}}}}}",
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
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={subj_cd=[@5,29:35='subj_cd',<299>,1:29], apple=[@1,7:11='apple',<299>,1:7]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={tab1={subj_cd=[@5,29:35='subj_cd',<299>,1:29], apple=[@1,7:11='apple',<299>,1:7]}, interface={apple={column={name=apple, table_ref=null}}}}}",
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
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={subj_cd=[@7,42:48='subj_cd',<299>,1:42], apple=[@1,7:11='apple',<299>,1:7]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={tab1={subj_cd=[@7,42:48='subj_cd',<299>,1:42], apple=[@1,7:11='apple',<299>,1:7]}, interface={apple={column={name=apple, table_ref=null}}}}}",
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
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={subj_cd=[@8,47:53='subj_cd',<299>,1:47], apple=[@1,7:11='apple',<299>,1:7]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={tab1={subj_cd=[@8,47:53='subj_cd',<299>,1:47], apple=[@1,7:11='apple',<299>,1:7]}, interface={apple={column={name=apple, table_ref=null}}}}}",
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
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={subj_cd=[@5,29:35='subj_cd',<299>,1:29], apple=[@1,7:11='apple',<299>,1:7]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={tab1={subj_cd=[@5,29:35='subj_cd',<299>,1:29], apple=[@1,7:11='apple',<299>,1:7]}, interface={apple={column={name=apple, table_ref=null}}}}}",
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
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={apple=[@1,7:11='apple',<299>,1:7]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={tab1={apple=[@1,7:11='apple',<299>,1:7]}, interface={apple={column={name=apple, table_ref=null}}}}}",
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
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={subj_cd=[@5,29:35='subj_cd',<299>,1:29], apple=[@1,7:11='apple',<299>,1:7]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={tab1={subj_cd=[@5,29:35='subj_cd',<299>,1:29], apple=[@1,7:11='apple',<299>,1:7]}, interface={apple={column={name=apple, table_ref=null}}}}}",
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
		Assert.assertEquals("Table Dictionary is wrong", "{unknown={column1=[@2,10:16='column1',<299>,1:10], column2=[@8,39:45='column2',<299>,1:39]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{unknown={column1=[@2,10:16='column1',<299>,1:10], column2=[@8,39:45='column2',<299>,1:39]}}",
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
		Assert.assertEquals("Table Dictionary is wrong", "{unknown={column1=[@1,5:11='column1',<299>,1:5]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{unknown={column1=[@1,5:11='column1',<299>,1:5]}}",
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
		Assert.assertEquals("Table Dictionary is wrong", "{unknown={column1=[@1,5:11='column1',<299>,1:5], column3=[@7,40:46='column3',<299>,1:40], column2=[@3,18:24='column2',<299>,1:18]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{unknown={column1=[@1,5:11='column1',<299>,1:5], column3=[@7,40:46='column3',<299>,1:40], column2=[@3,18:24='column2',<299>,1:18]}}",
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
		Assert.assertEquals("Table Dictionary is wrong", "{s949={t_student_last_name=[@23,161:164='S949',<299>,1:161]}, s948={OBSERVATION_TM=[@3,27:30='s948',<299>,1:27], t_student_last_name=[@19,135:138='S948',<299>,1:135]}, unknown={observation_time=[@1,5:20='observation_time',<299>,1:5]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{S948={t_student_last_name=[@19,135:138='S948',<299>,1:135]}, S949={t_student_last_name=[@23,161:164='S949',<299>,1:161]}, unknown={observation_time=[@1,5:20='observation_time',<299>,1:5]}, s948={OBSERVATION_TM=[@3,27:30='s948',<299>,1:27]}}",
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
		Assert.assertEquals("Table Dictionary is wrong", "{unknown={column3=[@7,42:48='column3',<299>,1:42], column2=[@3,20:26='column2',<299>,1:20]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{unknown={column3=[@7,42:48='column3',<299>,1:42], column2=[@3,20:26='column2',<299>,1:20]}}",
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
		Assert.assertEquals("Table Dictionary is wrong", "{unknown={column1=[@1,5:11='column1',<299>,1:5], column3=[@7,40:46='column3',<299>,1:40], column2=[@3,18:24='column2',<299>,1:18]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{unknown={column1=[@1,5:11='column1',<299>,1:5], column3=[@7,40:46='column3',<299>,1:40], column2=[@3,18:24='column2',<299>,1:18]}}",
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
		Assert.assertEquals("Table Dictionary is wrong", "{unknown={column1=[@1,5:11='column1',<299>,1:5], column4=[@9,53:59='column4',<299>,1:53], column3=[@7,40:46='column3',<299>,1:40], column2=[@3,18:24='column2',<299>,1:18]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{unknown={column1=[@1,5:11='column1',<299>,1:5], column4=[@9,53:59='column4',<299>,1:53], column3=[@7,40:46='column3',<299>,1:40], column2=[@3,18:24='column2',<299>,1:18]}}",
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
		Assert.assertEquals("Table Dictionary is wrong", "{unknown={column2=[@8,41:47='column2',<299>,1:41]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{unknown={column2=[@8,41:47='column2',<299>,1:41]}}",
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
		Assert.assertEquals("Table Dictionary is wrong", "{a={<column1>={substitution={name=<column1>, type=column}}, column2=[@10,42:42='a',<299>,1:42]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{a={column2=[@10,42:42='a',<299>,1:42], <column1>={substitution={name=<column1>, type=column}}}}",
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
		Assert.assertEquals("Table Dictionary is wrong", "{unknown={column2=[@6,34:40='column2',<299>,1:34]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{unknown={column2=[@6,34:40='column2',<299>,1:34]}}",
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
		Assert.assertEquals("Table Dictionary is wrong", "{a={column1=[@2,10:10='a',<299>,1:10], column2=[@10,40:40='a',<299>,1:40]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{a={column1=[@2,10:10='a',<299>,1:10], column2=[@10,40:40='a',<299>,1:40]}}",
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
		Assert.assertEquals("Table Dictionary is wrong", "{a={column1=[@2,10:10='a',<299>,1:10], column2=[@10,40:40='a',<299>,1:40]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{a={column1=[@2,10:10='a',<299>,1:10], column2=[@10,40:40='a',<299>,1:40]}}",
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
		Assert.assertEquals("Table Dictionary is wrong", "{a={column1=[@2,10:10='a',<299>,1:10], <column4>={substitution={name=<column4>, type=column}}, column2=[@10,40:40='a',<299>,1:40]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{a={column1=[@2,10:10='a',<299>,1:10], column2=[@10,40:40='a',<299>,1:40], <column4>={substitution={name=<column4>, type=column}}}}",
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
		Assert.assertEquals("Table Dictionary is wrong", "{a={column1=[@2,10:10='a',<299>,1:10], <column4>={substitution={name=<column4>, type=column}}, column2=[@10,40:40='a',<299>,1:40]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{a={column1=[@2,10:10='a',<299>,1:10], column2=[@10,40:40='a',<299>,1:40], <column4>={substitution={name=<column4>, type=column}}}}",
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
		Assert.assertEquals("Table Dictionary is wrong", "{sgbstdn={a=[@9,38:38='a',<299>,1:38], b=[@11,42:42='b',<299>,1:42]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={sgbstdn={a=[@9,38:38='a',<299>,1:38], b=[@11,42:42='b',<299>,1:42]}, interface={case_one={case={clauses={1={then={literal='Y'}, when={condition={left={column={name=a, table_ref=null}}, right={column={name=b, table_ref=null}}, operator=<}}}, 2={then={literal='N'}, when={condition={left={column={name=a, table_ref=null}}, right={column={name=b, table_ref=null}}, operator==}}}}, else={literal='N'}}}}}}",
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
		Assert.assertEquals("Table Dictionary is wrong", "{scbcrse={field2=[@13,50:55='field2',<299>,1:50], field1=[@6,29:34='field1',<299>,1:29]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={scbcrse={field2=[@13,50:55='field2',<299>,1:50], field1=[@6,29:34='field1',<299>,1:29]}, interface={unnamed_1={function={parameters={1={concatenate={1={literal='0'}, 2={column={name=field2, table_ref=null}}}}, 2={literal='0'}}, function_name=trim}}, unnamed_0={function={function_name=trim, parameters={qualifier=leading, trim_character={literal='0'}, value={column={name=field1, table_ref=null}}}}}}}}",
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
	// HAVING Clauses
	
	@Test
	public void basicHavingTest() {

		final String query = " select spriden_id,  TERM_CODE_ADMIT FROM tab1 "
				+ " HAVING max(TERM_CODE_ADMIT) >= 201310 ";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=spriden_id, table_ref=null}}, 2={column={name=TERM_CODE_ADMIT, table_ref=null}}}, having={condition={left={function={function_name=max, qualifier=null, parameters={column={name=TERM_CODE_ADMIT, table_ref=null}}}}, right={literal=201310}, operator=>=}}, from={table={alias=null, table=tab1}}}}",
				extractor.getSqlTree().toString());
		Assert.assertEquals("Interface is wrong", "[TERM_CODE_ADMIT, spriden_id]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={TERM_CODE_ADMIT=[@9,59:73='TERM_CODE_ADMIT',<299>,1:59], spriden_id=[@1,8:17='spriden_id',<299>,1:8]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={tab1={TERM_CODE_ADMIT=[@9,59:73='TERM_CODE_ADMIT',<299>,1:59], spriden_id=[@1,8:17='spriden_id',<299>,1:8]}, interface={TERM_CODE_ADMIT={column={name=TERM_CODE_ADMIT, table_ref=null}}, spriden_id={column={name=spriden_id, table_ref=null}}}}}",
				extractor.getSymbolTable().toString());
	}
	
	@Test
	public void conditionVariableHavingTest() {

		final String query = " select spriden_id,  TERM_CODE_ADMIT FROM tab1 "
				+ " HAVING <condition> or <condition2>";

		final SQLSelectParserParser parser = parse(query);
		runParsertest(query, parser);
	}
	
	@Test
	public void predicandVariableHavingTest() {

		final String query = " select spriden_id,  TERM_CODE_ADMIT FROM tab1 "
				+ " HAVING <condition> > '20130101' ";

		final SQLSelectParserParser parser = parse(query);
		runParsertest(query, parser);
	}
	
	@Test
	public void columnVariableHavingTest() {

		final String query = " select spriden_id,  TERM_CODE_ADMIT FROM tab1 "
				+ " HAVING tab1.<condition> > '20130101' ";

		final SQLSelectParserParser parser = parse(query);
		runParsertest(query, parser);
	}

	// end of having clause
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
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={apple=[@8,42:46='apple',<299>,1:42], fruit_cd=[@12,52:59='fruit_cd',<299>,1:52]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={tab1={apple=[@8,42:46='apple',<299>,1:42], fruit_cd=[@12,52:59='fruit_cd',<299>,1:52]}, interface={apple={column={name=apple, table_ref=null}}, fruit_cd={column={name=fruit_cd, table_ref=null}}}}}",
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
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={apple=[@1,7:11='apple',<299>,1:7], fruit_cd=[@11,69:76='fruit_cd',<299>,1:69]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={tab1={apple=[@1,7:11='apple',<299>,1:7], fruit_cd=[@11,69:76='fruit_cd',<299>,1:69]}, interface={apple={column={name=apple, table_ref=null}}, fruit_cd={column={name=fruit_cd, table_ref=null}}}}}",
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
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={apple=[@1,7:11='apple',<299>,1:7], <column variable>={substitution={name=<column variable>, type=column}}, fruit_cd=[@13,71:78='fruit_cd',<299>,1:71]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={tab1={apple=[@1,7:11='apple',<299>,1:7], <column variable>={substitution={name=<column variable>, type=column}}, fruit_cd=[@13,71:78='fruit_cd',<299>,1:71]}, interface={apple={column={name=apple, table_ref=null}}, fruit_cd={column={name=fruit_cd, table_ref=null}}}}}",
				extractor.getSymbolTable().toString());
	}
	
	// END OF ORDER BY CLAUSES
	// AGGREGATE FUNCTIONS
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
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={apple=[@11,42:46='apple',<299>,1:42]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={tab1={apple=[@11,42:46='apple',<299>,1:42]}, interface={apple={column={name=apple, table_ref=null}}, unnamed_0={function={function_name=COUNT, qualifier=null, parameters=*}}}}}",
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
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={apple=[@1,7:11='apple',<299>,1:7], <other>={substitution={name=<other>, type=column}}}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={tab1={apple=[@1,7:11='apple',<299>,1:7], <other>={substitution={name=<other>, type=column}}}, interface={apple={column={name=apple, table_ref=null}}, unnamed_0={function={function_name=COUNT, qualifier=null, parameters=*}}}}}",
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
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={apple=[@1,7:11='apple',<299>,1:7], a=[@18,70:70='a',<299>,1:70], <other>={substitution={name=<other>, type=column}}, b=[@20,72:72='b',<299>,1:72], c=[@22,74:74='c',<299>,1:74]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={tab1={apple=[@1,7:11='apple',<299>,1:7], a=[@18,70:70='a',<299>,1:70], b=[@20,72:72='b',<299>,1:72], c=[@22,74:74='c',<299>,1:74], <other>={substitution={name=<other>, type=column}}}, interface={apple={column={name=apple, table_ref=null}}, unnamed_0={function={function_name=COUNT, qualifier=null, parameters=*}}}}}",
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
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={apple=[@1,7:11='apple',<299>,1:7]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={tab1={apple=[@1,7:11='apple',<299>,1:7]}, interface={apple={column={name=apple, table_ref=null}}, unnamed_0={function={function_name=COUNT, qualifier=null, parameters=*}}}}}",
				extractor.getSymbolTable().toString());
	}
	
	@Test
	public void basicAggregateQueryWithCountOverCalcTest() {
		// TODO: ITEM 67 - Count function over calculation
		final String query = "SELECT apple, count(subj + object) from tab1 group by apple";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=apple, table_ref=null}}, 2={function={function_name=count, qualifier=null, parameters={calc={left={column={name=subj, table_ref=null}}, right={column={name=object, table_ref=null}}, operator=+}}}}}, from={table={alias=null, table=tab1}}, groupby={1={column={name=apple, table_ref=null}}}}}",
				extractor.getSqlTree().toString());
		Assert.assertEquals("Interface is wrong", "[apple, unnamed_0]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={apple=[@13,54:58='apple',<299>,1:54], subj=[@5,20:23='subj',<299>,1:20], object=[@7,27:32='object',<263>,1:27]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={tab1={apple=[@13,54:58='apple',<299>,1:54], subj=[@5,20:23='subj',<299>,1:20], object=[@7,27:32='object',<263>,1:27]}, interface={apple={column={name=apple, table_ref=null}}, unnamed_0={function={function_name=count, qualifier=null, parameters={calc={left={column={name=subj, table_ref=null}}, right={column={name=object, table_ref=null}}, operator=+}}}}}}}",
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
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={apple=[@13,53:57='apple',<299>,1:53], <other>={substitution={name=<other>, type=column}}}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={tab1={apple=[@13,53:57='apple',<299>,1:53], <other>={substitution={name=<other>, type=column}}}, interface={apple={column={name=apple, table_ref=null}}, unnamed_0={function={function_name=count, qualifier=null, parameters={column={substitution={name=<other>, type=column}, table_ref=tab1}}}}}}}",
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
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={apple=[@11,48:52='apple',<299>,1:48]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={tab1={apple=[@11,48:52='apple',<299>,1:48]}, interface={apple={column={name=apple, table_ref=null}}, unnamed_0={function={function_name=count, qualifier=null, parameters={substitution={name=<other>, type=predicand}}}}}}}",
				extractor.getSymbolTable().toString());
	}
	
	// END OF AGGREGATE QUERIES
	// SNOWFLAKE AGGREGATE FUNCTIONS QUERIES
	
  // Snowflake Aggregate Function ANY_VALUE
	
	@Test
	public void snowflakeAggregate_ANY_VALUE_QueryTest() {
		final String query = "SELECT ANY_VALUE(col1) from tab1";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={function={function_name=ANY_VALUE, qualifier=null, parameters={column={name=col1, table_ref=null}}}}}, from={table={alias=null, table=tab1}}}}",
				extractor.getSqlTree().toString());
	}
	
  // Snowflake Aggregate Function CORR
	
	@Test
	public void snowflakeAggregate_CORR_QueryTest() {
		final String query = "SELECT CORR(col1) from tab1";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={function={function_name=CORR, qualifier=null, parameters={column={name=col1, table_ref=null}}}}}, from={table={alias=null, table=tab1}}}}",
				extractor.getSqlTree().toString());
	}
	
  // Snowflake Aggregate Function COVAR_POP
	
	@Test
	public void snowflakeAggregate_COVAR_POP_QueryTest() {
		final String query = "SELECT COVAR_POP(col1) from tab1";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={function={function_name=COVAR_POP, qualifier=null, parameters={column={name=col1, table_ref=null}}}}}, from={table={alias=null, table=tab1}}}}",
				extractor.getSqlTree().toString());
	}
	
  // Snowflake Aggregate Function COVAR_SAMP
	
	@Test
	public void snowflakeAggregate_COVAR_SAMP_QueryTest() {
		final String query = "SELECT COVAR_SAMP(col1) from tab1";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={function={function_name=COVAR_SAMP, qualifier=null, parameters={column={name=col1, table_ref=null}}}}}, from={table={alias=null, table=tab1}}}}",
				extractor.getSqlTree().toString());
	}
	
  // Snowflake Aggregate Function LISTAGG
	
	@Test
	public void snowflakeAggregate_LISTAGG_QueryTest() {
		final String query = "SELECT LISTAGG(col1) from tab1";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={function={function_name=LISTAGG, qualifier=null, parameters={column={name=col1, table_ref=null}}}}}, from={table={alias=null, table=tab1}}}}",
				extractor.getSqlTree().toString());
	}
	
  // Snowflake Aggregate Function MEDIAN
	
	@Test
	public void snowflakeAggregate_MEDIAN_QueryTest() {
		final String query = "SELECT MEDIAN(col1) from tab1";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={function={function_name=MEDIAN, qualifier=null, parameters={column={name=col1, table_ref=null}}}}}, from={table={alias=null, table=tab1}}}}",
				extractor.getSqlTree().toString());
	}
	
  // Snowflake Aggregate Function PERCENTILE_CONT
	
	@Test
	public void snowflakeAggregate_PERCENTILE_CONT_QueryTest() {
		final String query = "SELECT PERCENTILE_CONT(col1) from tab1";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={function={function_name=PERCENTILE_CONT, qualifier=null, parameters={column={name=col1, table_ref=null}}}}}, from={table={alias=null, table=tab1}}}}",
				extractor.getSqlTree().toString());
	}
	
  // Snowflake Aggregate Function PERCENTILE_DISC
	
	@Test
	public void snowflakeAggregate_PERCENTILE_DISC_QueryTest() {
		final String query = "SELECT PERCENTILE_DISC(col1) from tab1";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={function={function_name=PERCENTILE_DISC, qualifier=null, parameters={column={name=col1, table_ref=null}}}}}, from={table={alias=null, table=tab1}}}}",
				extractor.getSqlTree().toString());
	}
	
  // Snowflake Aggregate Function STDDEV
	
	@Test
	public void snowflakeAggregate_STDDEV_QueryTest() {
		final String query = "SELECT STDDEV(col1) from tab1";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={function={function_name=STDDEV, qualifier=null, parameters={column={name=col1, table_ref=null}}}}}, from={table={alias=null, table=tab1}}}}",
				extractor.getSqlTree().toString());
	}
	
  // Snowflake Aggregate Function VARIANCE_POP
	
	@Test
	public void snowflakeAggregate_VARIANCE_POP_QueryTest() {
		final String query = "SELECT VARIANCE_POP(col1) from tab1";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={function={function_name=VARIANCE_POP, qualifier=null, parameters={column={name=col1, table_ref=null}}}}}, from={table={alias=null, table=tab1}}}}",
				extractor.getSqlTree().toString());
	}
	
  // Snowflake Aggregate Function VARIANCE
	
	@Test
	public void snowflakeAggregate_VARIANCE_QueryTest() {
		final String query = "SELECT VARIANCE(col1) from tab1";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={function={function_name=VARIANCE, qualifier=null, parameters={column={name=col1, table_ref=null}}}}}, from={table={alias=null, table=tab1}}}}",
				extractor.getSqlTree().toString());
	}
	
  // Snowflake Aggregate Function VARIANCE_SAMP
	
	@Test
	public void snowflakeAggregate_VARIANCE_SAMP_QueryTest() {
		final String query = "SELECT VARIANCE_SAMP(col1) from tab1";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={function={function_name=VARIANCE_SAMP, qualifier=null, parameters={column={name=col1, table_ref=null}}}}}, from={table={alias=null, table=tab1}}}}",
				extractor.getSqlTree().toString());
	}
	
  // Snowflake Aggregate Function CUME_DIST
	
	@Test
	public void snowflakeAggregate_CUME_DIST_QueryTest() {
		final String query = "SELECT CUME_DIST(col1) from tab1";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={function={function_name=CUME_DIST, qualifier=null, parameters={column={name=col1, table_ref=null}}}}}, from={table={alias=null, table=tab1}}}}",
				extractor.getSqlTree().toString());
	}
	
  // Snowflake Aggregate Function DENSE_RANK
	
	@Test
	public void snowflakeAggregate_DENSE_RANK_QueryTest() {
		final String query = "SELECT DENSE_RANK(col1) from tab1";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={function={function_name=DENSE_RANK, qualifier=null, parameters={column={name=col1, table_ref=null}}}}}, from={table={alias=null, table=tab1}}}}",
				extractor.getSqlTree().toString());
	}
	
  // Snowflake Aggregate Function NTILE
	
	@Test
	public void snowflakeAggregate_NTILE_QueryTest() {
		final String query = "SELECT NTILE(col1) from tab1";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={function={function_name=NTILE, qualifier=null, parameters={column={name=col1, table_ref=null}}}}}, from={table={alias=null, table=tab1}}}}",
				extractor.getSqlTree().toString());
	}
	
  // Snowflake Aggregate Function PERCENT_RANK
	
	@Test
	public void snowflakeAggregate_PERCENT_RANK_QueryTest() {
		final String query = "SELECT PERCENT_RANK(col1) from tab1";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={function={function_name=PERCENT_RANK, qualifier=null, parameters={column={name=col1, table_ref=null}}}}}, from={table={alias=null, table=tab1}}}}",
				extractor.getSqlTree().toString());
	}
	
  // Snowflake Aggregate Function WIDTH_BUCKET
	
	@Test
	public void snowflakeAggregate_WIDTH_BUCKET_QueryTest() {
		final String query = "SELECT WIDTH_BUCKET(col1) from tab1";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={function={function_name=WIDTH_BUCKET, qualifier=null, parameters={column={name=col1, table_ref=null}}}}}, from={table={alias=null, table=tab1}}}}",
				extractor.getSqlTree().toString());
	}
	
  // Snowflake Aggregate Function BITAND_AGG
	
	@Test
	public void snowflakeAggregate_BITAND_AGG_QueryTest() {
		final String query = "SELECT BITAND_AGG(col1) from tab1";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={function={function_name=BITAND_AGG, qualifier=null, parameters={column={name=col1, table_ref=null}}}}}, from={table={alias=null, table=tab1}}}}",
				extractor.getSqlTree().toString());
	}
	
  // Snowflake Aggregate Function BITOR_AGG
	
	@Test
	public void snowflakeAggregate_BITOR_AGG_QueryTest() {
		final String query = "SELECT BITOR_AGG(col1) from tab1";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={function={function_name=BITOR_AGG, qualifier=null, parameters={column={name=col1, table_ref=null}}}}}, from={table={alias=null, table=tab1}}}}",
				extractor.getSqlTree().toString());
	}
	
  // Snowflake Aggregate Function BITXOR_AGG
	
	@Test
	public void snowflakeAggregate_BITXOR_AGG_QueryTest() {
		final String query = "SELECT BITXOR_AGG(col1) from tab1";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={function={function_name=BITXOR_AGG, qualifier=null, parameters={column={name=col1, table_ref=null}}}}}, from={table={alias=null, table=tab1}}}}",
				extractor.getSqlTree().toString());
	}
	
  // Snowflake Aggregate Function HASH_AGG
	
	@Test
	public void snowflakeAggregate_HASH_AGG_QueryTest() {
		final String query = "SELECT HASH_AGG(col1) from tab1";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={function={function_name=HASH_AGG, qualifier=null, parameters={column={name=col1, table_ref=null}}}}}, from={table={alias=null, table=tab1}}}}",
				extractor.getSqlTree().toString());
	}
	
  // Snowflake Aggregate Function ARRAY_AGG
	
	@Test
	public void snowflakeAggregate_ARRAY_AGG_QueryTest() {
		final String query = "SELECT ARRAY_AGG(col1) from tab1";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={function={function_name=ARRAY_AGG, qualifier=null, parameters={column={name=col1, table_ref=null}}}}}, from={table={alias=null, table=tab1}}}}",
				extractor.getSqlTree().toString());
	}
	
  // Snowflake Aggregate Function OBJECT_AGG
	
	@Test
	public void snowflakeAggregate_OBJECT_AGG_QueryTest() {
		final String query = "SELECT OBJECT_AGG(col1) from tab1";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={function={function_name=OBJECT_AGG, qualifier=null, parameters={column={name=col1, table_ref=null}}}}}, from={table={alias=null, table=tab1}}}}",
				extractor.getSqlTree().toString());
	}
	
  // Snowflake Aggregate Function REGR_AVGX
	
	@Test
	public void snowflakeAggregate_REGR_AVGX_QueryTest() {
		final String query = "SELECT REGR_AVGX(col1) from tab1";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={function={function_name=REGR_AVGX, qualifier=null, parameters={column={name=col1, table_ref=null}}}}}, from={table={alias=null, table=tab1}}}}",
				extractor.getSqlTree().toString());
	}
	
  // Snowflake Aggregate Function REGR_AVGY
	
	@Test
	public void snowflakeAggregate_REGR_AVGY_QueryTest() {
		final String query = "SELECT REGR_AVGY(col1) from tab1";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={function={function_name=REGR_AVGY, qualifier=null, parameters={column={name=col1, table_ref=null}}}}}, from={table={alias=null, table=tab1}}}}",
				extractor.getSqlTree().toString());
	}
	
  // Snowflake Aggregate Function REGR_COUNT
	
	@Test
	public void snowflakeAggregate_REGR_COUNT_QueryTest() {
		final String query = "SELECT REGR_COUNT(col1) from tab1";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={function={function_name=REGR_COUNT, qualifier=null, parameters={column={name=col1, table_ref=null}}}}}, from={table={alias=null, table=tab1}}}}",
				extractor.getSqlTree().toString());
	}
	
  // Snowflake Aggregate Function REGR_INTERCEPT
	
	@Test
	public void snowflakeAggregate_REGR_INTERCEPT_QueryTest() {
		final String query = "SELECT REGR_INTERCEPT(col1) from tab1";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={function={function_name=REGR_INTERCEPT, qualifier=null, parameters={column={name=col1, table_ref=null}}}}}, from={table={alias=null, table=tab1}}}}",
				extractor.getSqlTree().toString());
	}
	
  // Snowflake Aggregate Function REGR_R2
	
	@Test
	public void snowflakeAggregate_REGR_R2_QueryTest() {
		final String query = "SELECT REGR_R2(col1) from tab1";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={function={function_name=REGR_R2, qualifier=null, parameters={column={name=col1, table_ref=null}}}}}, from={table={alias=null, table=tab1}}}}",
				extractor.getSqlTree().toString());
	}
	
  // Snowflake Aggregate Function REGR_SLOPE
	
	@Test
	public void snowflakeAggregate_REGR_SLOPE_QueryTest() {
		final String query = "SELECT REGR_SLOPE(col1) from tab1";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={function={function_name=REGR_SLOPE, qualifier=null, parameters={column={name=col1, table_ref=null}}}}}, from={table={alias=null, table=tab1}}}}",
				extractor.getSqlTree().toString());
	}
	
  // Snowflake Aggregate Function REGR_SXX
	
	@Test
	public void snowflakeAggregate_REGR_SXX_QueryTest() {
		final String query = "SELECT REGR_SXX(col1) from tab1";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={function={function_name=REGR_SXX, qualifier=null, parameters={column={name=col1, table_ref=null}}}}}, from={table={alias=null, table=tab1}}}}",
				extractor.getSqlTree().toString());
	}
	
  // Snowflake Aggregate Function REGR_SXY
	
	@Test
	public void snowflakeAggregate_REGR_SXY_QueryTest() {
		final String query = "SELECT REGR_SXY(col1) from tab1";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={function={function_name=REGR_SXY, qualifier=null, parameters={column={name=col1, table_ref=null}}}}}, from={table={alias=null, table=tab1}}}}",
				extractor.getSqlTree().toString());
	}
	
  // Snowflake Aggregate Function REGR_SYY
	
	@Test
	public void snowflakeAggregate_REGR_SYY_QueryTest() {
		final String query = "SELECT REGR_SYY(col1) from tab1";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={function={function_name=REGR_SYY, qualifier=null, parameters={column={name=col1, table_ref=null}}}}}, from={table={alias=null, table=tab1}}}}",
				extractor.getSqlTree().toString());
	}
	
  // Snowflake Aggregate Function APPROX_COUNT_DISTINCT
	
	@Test
	public void snowflakeAggregate_APPROX_COUNT_DISTINCT_QueryTest() {
		final String query = "SELECT APPROX_COUNT_DISTINCT(col1) from tab1";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={function={function_name=APPROX_COUNT_DISTINCT, qualifier=null, parameters={column={name=col1, table_ref=null}}}}}, from={table={alias=null, table=tab1}}}}",
				extractor.getSqlTree().toString());
	}
	
  // Snowflake Aggregate Function HLL
	
	@Test
	public void snowflakeAggregate_HLL_QueryTest() {
		final String query = "SELECT HLL(col1) from tab1";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={function={function_name=HLL, qualifier=null, parameters={column={name=col1, table_ref=null}}}}}, from={table={alias=null, table=tab1}}}}",
				extractor.getSqlTree().toString());
	}
	
  // Snowflake Aggregate Function HLL_ACCUMULATE
	
	@Test
	public void snowflakeAggregate_HLL_ACCUMULATE_QueryTest() {
		final String query = "SELECT HLL_ACCUMULATE(col1) from tab1";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={function={function_name=HLL_ACCUMULATE, qualifier=null, parameters={column={name=col1, table_ref=null}}}}}, from={table={alias=null, table=tab1}}}}",
				extractor.getSqlTree().toString());
	}
	
  // Snowflake Aggregate Function HLL_COMBINE
	
	@Test
	public void snowflakeAggregate_HLL_COMBINE_QueryTest() {
		final String query = "SELECT HLL_COMBINE(col1) from tab1";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={function={function_name=HLL_COMBINE, qualifier=null, parameters={column={name=col1, table_ref=null}}}}}, from={table={alias=null, table=tab1}}}}",
				extractor.getSqlTree().toString());
	}
	
  // Snowflake Aggregate Function HLL_EXPORT
	
	@Test
	public void snowflakeAggregate_HLL_EXPORT_QueryTest() {
		final String query = "SELECT HLL_EXPORT(col1) from tab1";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={function={function_name=HLL_EXPORT, qualifier=null, parameters={column={name=col1, table_ref=null}}}}}, from={table={alias=null, table=tab1}}}}",
				extractor.getSqlTree().toString());
	}
	
  // Snowflake Aggregate Function HLL_IMPORT
	
	@Test
	public void snowflakeAggregate_HLL_IMPORT_QueryTest() {
		final String query = "SELECT HLL_IMPORT(col1) from tab1";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={function={function_name=HLL_IMPORT, qualifier=null, parameters={column={name=col1, table_ref=null}}}}}, from={table={alias=null, table=tab1}}}}",
				extractor.getSqlTree().toString());
	}
	
  // Snowflake Aggregate Function APPROXIMATE_JACCARD_INDEX
	
	@Test
	public void snowflakeAggregate_APPROXIMATE_JACCARD_INDEX_QueryTest() {
		final String query = "SELECT APPROXIMATE_JACCARD_INDEX(col1) from tab1";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={function={function_name=APPROXIMATE_JACCARD_INDEX, qualifier=null, parameters={column={name=col1, table_ref=null}}}}}, from={table={alias=null, table=tab1}}}}",
				extractor.getSqlTree().toString());
	}
	
  // Snowflake Aggregate Function APPROXIMATE_SIMILARITY
	
	@Test
	public void snowflakeAggregate_APPROXIMATE_SIMILARITY_QueryTest() {
		final String query = "SELECT APPROXIMATE_SIMILARITY(col1) from tab1";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={function={function_name=APPROXIMATE_SIMILARITY, qualifier=null, parameters={column={name=col1, table_ref=null}}}}}, from={table={alias=null, table=tab1}}}}",
				extractor.getSqlTree().toString());
	}
	
  // Snowflake Aggregate Function MINHASH
	
	@Test
	public void snowflakeAggregate_MINHASH_QueryTest() {
		final String query = "SELECT MINHASH(col1) from tab1";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={function={function_name=MINHASH, qualifier=null, parameters={column={name=col1, table_ref=null}}}}}, from={table={alias=null, table=tab1}}}}",
				extractor.getSqlTree().toString());
	}
	
  // Snowflake Aggregate Function MINHASH_COMBINE
	
	@Test
	public void snowflakeAggregate_MINHASH_COMBINE_QueryTest() {
		final String query = "SELECT MINHASH_COMBINE(col1) from tab1";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={function={function_name=MINHASH_COMBINE, qualifier=null, parameters={column={name=col1, table_ref=null}}}}}, from={table={alias=null, table=tab1}}}}",
				extractor.getSqlTree().toString());
	}
	
  // Snowflake Aggregate Function APPROX_TOP_K
	
	@Test
	public void snowflakeAggregate_APPROX_TOP_K_QueryTest() {
		final String query = "SELECT APPROX_TOP_K(col1) from tab1";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={function={function_name=APPROX_TOP_K, qualifier=null, parameters={column={name=col1, table_ref=null}}}}}, from={table={alias=null, table=tab1}}}}",
				extractor.getSqlTree().toString());
	}
	
  // Snowflake Aggregate Function APPROX_TOP_K_ACCUMULATE
	
	@Test
	public void snowflakeAggregate_APPROX_TOP_K_ACCUMULATE_QueryTest() {
		final String query = "SELECT APPROX_TOP_K_ACCUMULATE(col1) from tab1";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={function={function_name=APPROX_TOP_K_ACCUMULATE, qualifier=null, parameters={column={name=col1, table_ref=null}}}}}, from={table={alias=null, table=tab1}}}}",
				extractor.getSqlTree().toString());
	}
	
  // Snowflake Aggregate Function APPROX_TOP_K_COMBINE
	
	@Test
	public void snowflakeAggregate_APPROX_TOP_K_COMBINE_QueryTest() {
		final String query = "SELECT APPROX_TOP_K_COMBINE(col1) from tab1";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={function={function_name=APPROX_TOP_K_COMBINE, qualifier=null, parameters={column={name=col1, table_ref=null}}}}}, from={table={alias=null, table=tab1}}}}",
				extractor.getSqlTree().toString());
	}
	
  // Snowflake Aggregate Function APPROX_PERCENTILE
	
	@Test
	public void snowflakeAggregate_APPROX_PERCENTILE_QueryTest() {
		final String query = "SELECT APPROX_PERCENTILE(col1) from tab1";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={function={function_name=APPROX_PERCENTILE, qualifier=null, parameters={column={name=col1, table_ref=null}}}}}, from={table={alias=null, table=tab1}}}}",
				extractor.getSqlTree().toString());
	}
	
  // Snowflake Aggregate Function APPROX_PERCENTILE_ACCUMULATE
	
	@Test
	public void snowflakeAggregate_APPROX_PERCENTILE_ACCUMULATE_QueryTest() {
		final String query = "SELECT APPROX_PERCENTILE_ACCUMULATE(col1) from tab1";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={function={function_name=APPROX_PERCENTILE_ACCUMULATE, qualifier=null, parameters={column={name=col1, table_ref=null}}}}}, from={table={alias=null, table=tab1}}}}",
				extractor.getSqlTree().toString());
	}
	
  // Snowflake Aggregate Function APPROX_PERCENTILE_COMBINE
	
	@Test
	public void snowflakeAggregate_APPROX_PERCENTILE_COMBINE_QueryTest() {
		final String query = "SELECT APPROX_PERCENTILE_COMBINE(col1) from tab1";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={function={function_name=APPROX_PERCENTILE_COMBINE, qualifier=null, parameters={column={name=col1, table_ref=null}}}}}, from={table={alias=null, table=tab1}}}}",
				extractor.getSqlTree().toString());
	}
	
	
	  // Snowflake Aggregate Function GROUPING
	
	@Test
	public void snowflakeAggregate_GROUPING_SingleParameterQueryTest() {
		final String query = "SELECT GROUPING(col1) from tab1";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={function={parameters={1={column={name=col1, table_ref=null}}}, function_name=GROUPING}}}, from={table={alias=null, table=tab1}}}}",
				extractor.getSqlTree().toString());
	}
	
	@Test
	public void snowflakeAggregate_GROUPING_MultipleParameterQueryTest() {
		final String query = "SELECT col, GROUPING(col1, col2) from tab1 group by col";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=col, table_ref=null}}, 2={function={parameters={1={column={name=col1, table_ref=null}}, 2={column={name=col2, table_ref=null}}}, function_name=GROUPING}}}, from={table={alias=null, table=tab1}}, groupby={1={column={name=col, table_ref=null}}}}}",
				extractor.getSqlTree().toString());
	}
	
  	// END OF SNOWFLAKE AGGREGATES
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
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={item=[@3,12:15='item',<299>,1:12], code=[@19,71:74='code',<299>,1:71], spriden_id=[@16,51:60='spriden_id',<299>,1:51]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={tab1={item=[@3,12:15='item',<299>,1:12], code=[@19,71:74='code',<299>,1:71], spriden_id=[@16,51:60='spriden_id',<299>,1:51]}, interface={unnamed_1={window_function={over={partition_by={1={column={name=spriden_id, table_ref=null}}}, orderby={1={null_order=null, predicand={column={name=code, table_ref=null}}, sort_order=ASC}}}, function={function_name=lead, parameters={1={column={name=code, table_ref=null}}, 2={literal=1}}}}}, unnamed_0={function={parameters={1={column={name=item, table_ref=null}}}, function_name=func}}}}}",
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
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={row_num=[@16,78:84='row_num',<299>,1:78], k_stfd=[@8,35:40='k_stfd',<299>,1:35], kppi=[@10,43:46='kppi',<299>,1:43], OBSERVATION_TM=[@13,57:70='OBSERVATION_TM',<299>,1:57]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={a=tab1, tab1={row_num=[@16,78:84='row_num',<299>,1:78], k_stfd=[@8,35:40='k_stfd',<299>,1:35], kppi=[@10,43:46='kppi',<299>,1:43], OBSERVATION_TM=[@13,57:70='OBSERVATION_TM',<299>,1:57]}, interface={key_rank={window_function={over={partition_by={1={column={name=k_stfd, table_ref=null}}, 2={column={name=kppi, table_ref=null}}}, orderby={1={null_order=null, predicand={column={name=OBSERVATION_TM, table_ref=null}}, sort_order=desc}, 2={null_order=null, predicand={column={name=row_num, table_ref=null}}, sort_order=desc}}}, function={function_name=rank, parameters=null}}}}}}",
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
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={parm=[@3,14:17='parm',<299>,1:14], row_num=[@17,82:88='row_num',<299>,1:82], k_stfd=[@9,39:44='k_stfd',<299>,1:39], kppi=[@11,47:50='kppi',<299>,1:47], OBSERVATION_TM=[@14,61:74='OBSERVATION_TM',<299>,1:61]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={a=tab1, tab1={parm=[@3,14:17='parm',<299>,1:14], row_num=[@17,82:88='row_num',<299>,1:82], k_stfd=[@9,39:44='k_stfd',<299>,1:39], kppi=[@11,47:50='kppi',<299>,1:47], OBSERVATION_TM=[@14,61:74='OBSERVATION_TM',<299>,1:61]}, interface={key_rank={window_function={over={partition_by={1={column={name=k_stfd, table_ref=null}}, 2={column={name=kppi, table_ref=null}}}, orderby={1={null_order=null, predicand={column={name=OBSERVATION_TM, table_ref=null}}, sort_order=desc}, 2={null_order=null, predicand={column={name=row_num, table_ref=null}}, sort_order=desc}}}, function={function_name=rank, parameters={1={column={name=parm, table_ref=null}}}}}}}}}",
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

	// SNOWFLAKE SELECT FROM WINDOW TESTS
	
	@Test
	public void lagWindowIgnoreNullsTest() {
		String query = " SELECT  "
				+ "   lag(major_cd) ignore nulls over (partition by student_id, value_partition order by term_row) as major_cd_fill "
				+ " FROM student_term_major where major_cd is null";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=major_cd_fill, window_function={over={partition_by={1={column={name=student_id, table_ref=null}}, 2={column={name=value_partition, table_ref=null}}}, orderby={1={null_order=null, predicand={column={name=term_row, table_ref=null}}, sort_order=ASC}}}, function={null_handle=ignore, function_name=lag, parameters={1={column={name=major_cd, table_ref=null}}}}}}}, from={table={alias=null, table=student_term_major}}, where={condition={left={column={name=major_cd, table_ref=null}}, operator=is null}}}}",
				extractor.getSqlTree().toString());
		Assert.assertEquals("Interface is wrong", "[major_cd_fill]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{student_term_major={major_cd=[@23,153:160='major_cd',<299>,1:153], student_id=[@11,58:67='student_id',<299>,1:58], value_partition=[@13,70:84='value_partition',<299>,1:70], term_row=[@16,95:102='term_row',<299>,1:95]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={student_term_major={major_cd=[@23,153:160='major_cd',<299>,1:153], student_id=[@11,58:67='student_id',<299>,1:58], value_partition=[@13,70:84='value_partition',<299>,1:70], term_row=[@16,95:102='term_row',<299>,1:95]}, interface={major_cd_fill={window_function={over={partition_by={1={column={name=student_id, table_ref=null}}, 2={column={name=value_partition, table_ref=null}}}, orderby={1={null_order=null, predicand={column={name=term_row, table_ref=null}}, sort_order=ASC}}}, function={null_handle=ignore, function_name=lag, parameters={1={column={name=major_cd, table_ref=null}}}}}}}}}",
				extractor.getSymbolTable().toString());
	}
	
	@Test
	public void leadWindowIgnoreNullsTest() {
		String query = " SELECT  "
				+ "   lead(major_cd) ignore nulls over (partition by student_id, value_partition order by term_row) as major_cd_fill "
				+ " FROM student_term_major where major_cd is null";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=major_cd_fill, window_function={over={partition_by={1={column={name=student_id, table_ref=null}}, 2={column={name=value_partition, table_ref=null}}}, orderby={1={null_order=null, predicand={column={name=term_row, table_ref=null}}, sort_order=ASC}}}, function={null_handle=ignore, function_name=lead, parameters={1={column={name=major_cd, table_ref=null}}}}}}}, from={table={alias=null, table=student_term_major}}, where={condition={left={column={name=major_cd, table_ref=null}}, operator=is null}}}}",
				extractor.getSqlTree().toString());
		Assert.assertEquals("Interface is wrong", "[major_cd_fill]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{student_term_major={major_cd=[@23,154:161='major_cd',<299>,1:154], student_id=[@11,59:68='student_id',<299>,1:59], value_partition=[@13,71:85='value_partition',<299>,1:71], term_row=[@16,96:103='term_row',<299>,1:96]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={student_term_major={major_cd=[@23,154:161='major_cd',<299>,1:154], student_id=[@11,59:68='student_id',<299>,1:59], value_partition=[@13,71:85='value_partition',<299>,1:71], term_row=[@16,96:103='term_row',<299>,1:96]}, interface={major_cd_fill={window_function={over={partition_by={1={column={name=student_id, table_ref=null}}, 2={column={name=value_partition, table_ref=null}}}, orderby={1={null_order=null, predicand={column={name=term_row, table_ref=null}}, sort_order=ASC}}}, function={null_handle=ignore, function_name=lead, parameters={1={column={name=major_cd, table_ref=null}}}}}}}}}",
				extractor.getSymbolTable().toString());
	}
	
	@Test
	public void lastValueWindowIgnoreNullsTest() {
		String query = " SELECT  "
				+ "   last_value(major_cd) ignore nulls over (partition by student_id, value_partition order by term_row) as major_cd_fill "
				+ " FROM student_term_major where major_cd is null";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=major_cd_fill, window_function={over={partition_by={1={column={name=student_id, table_ref=null}}, 2={column={name=value_partition, table_ref=null}}}, orderby={1={null_order=null, predicand={column={name=term_row, table_ref=null}}, sort_order=ASC}}}, function={null_handle=ignore, function_name=last_value, parameters={1={column={name=major_cd, table_ref=null}}}}}}}, from={table={alias=null, table=student_term_major}}, where={condition={left={column={name=major_cd, table_ref=null}}, operator=is null}}}}",
				extractor.getSqlTree().toString());
		Assert.assertEquals("Interface is wrong", "[major_cd_fill]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{student_term_major={major_cd=[@23,160:167='major_cd',<299>,1:160], student_id=[@11,65:74='student_id',<299>,1:65], value_partition=[@13,77:91='value_partition',<299>,1:77], term_row=[@16,102:109='term_row',<299>,1:102]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={student_term_major={major_cd=[@23,160:167='major_cd',<299>,1:160], student_id=[@11,65:74='student_id',<299>,1:65], value_partition=[@13,77:91='value_partition',<299>,1:77], term_row=[@16,102:109='term_row',<299>,1:102]}, interface={major_cd_fill={window_function={over={partition_by={1={column={name=student_id, table_ref=null}}, 2={column={name=value_partition, table_ref=null}}}, orderby={1={null_order=null, predicand={column={name=term_row, table_ref=null}}, sort_order=ASC}}}, function={null_handle=ignore, function_name=last_value, parameters={1={column={name=major_cd, table_ref=null}}}}}}}}}",
				extractor.getSymbolTable().toString());
	}
	
	@Test
	public void lastValueWindowRespectNullsTest() {
		String query = " SELECT  "
				+ "   last_value(major_cd) respect nulls over (partition by student_id, value_partition order by term_row) as major_cd_fill "
				+ " FROM student_term_major where major_cd is null";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=major_cd_fill, window_function={over={partition_by={1={column={name=student_id, table_ref=null}}, 2={column={name=value_partition, table_ref=null}}}, orderby={1={null_order=null, predicand={column={name=term_row, table_ref=null}}, sort_order=ASC}}}, function={null_handle=respect, function_name=last_value, parameters={1={column={name=major_cd, table_ref=null}}}}}}}, from={table={alias=null, table=student_term_major}}, where={condition={left={column={name=major_cd, table_ref=null}}, operator=is null}}}}",
				extractor.getSqlTree().toString());
		Assert.assertEquals("Interface is wrong", "[major_cd_fill]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{student_term_major={major_cd=[@23,161:168='major_cd',<299>,1:161], student_id=[@11,66:75='student_id',<299>,1:66], value_partition=[@13,78:92='value_partition',<299>,1:78], term_row=[@16,103:110='term_row',<299>,1:103]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={student_term_major={major_cd=[@23,161:168='major_cd',<299>,1:161], student_id=[@11,66:75='student_id',<299>,1:66], value_partition=[@13,78:92='value_partition',<299>,1:78], term_row=[@16,103:110='term_row',<299>,1:103]}, interface={major_cd_fill={window_function={over={partition_by={1={column={name=student_id, table_ref=null}}, 2={column={name=value_partition, table_ref=null}}}, orderby={1={null_order=null, predicand={column={name=term_row, table_ref=null}}, sort_order=ASC}}}, function={null_handle=respect, function_name=last_value, parameters={1={column={name=major_cd, table_ref=null}}}}}}}}}",
				extractor.getSymbolTable().toString());
	}
	
	@Test
	public void firstValueWindowIgnoreNullsTest() {
		String query = " SELECT  "
				+ "   first_value(major_cd) ignore nulls over (partition by student_id, value_partition order by term_row) as major_cd_fill "
				+ " FROM student_term_major where major_cd is null";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=major_cd_fill, window_function={over={partition_by={1={column={name=student_id, table_ref=null}}, 2={column={name=value_partition, table_ref=null}}}, orderby={1={null_order=null, predicand={column={name=term_row, table_ref=null}}, sort_order=ASC}}}, function={null_handle=ignore, function_name=first_value, parameters={1={column={name=major_cd, table_ref=null}}}}}}}, from={table={alias=null, table=student_term_major}}, where={condition={left={column={name=major_cd, table_ref=null}}, operator=is null}}}}",
				extractor.getSqlTree().toString());
		Assert.assertEquals("Interface is wrong", "[major_cd_fill]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{student_term_major={major_cd=[@23,161:168='major_cd',<299>,1:161], student_id=[@11,66:75='student_id',<299>,1:66], value_partition=[@13,78:92='value_partition',<299>,1:78], term_row=[@16,103:110='term_row',<299>,1:103]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={student_term_major={major_cd=[@23,161:168='major_cd',<299>,1:161], student_id=[@11,66:75='student_id',<299>,1:66], value_partition=[@13,78:92='value_partition',<299>,1:78], term_row=[@16,103:110='term_row',<299>,1:103]}, interface={major_cd_fill={window_function={over={partition_by={1={column={name=student_id, table_ref=null}}, 2={column={name=value_partition, table_ref=null}}}, orderby={1={null_order=null, predicand={column={name=term_row, table_ref=null}}, sort_order=ASC}}}, function={null_handle=ignore, function_name=first_value, parameters={1={column={name=major_cd, table_ref=null}}}}}}}}}",
				extractor.getSymbolTable().toString());
	}
	
	@Test
	public void firstValueWindowRespectNullsTest() {
		String query = " SELECT  "
				+ "   first_value(major_cd) respect nulls over (partition by student_id, value_partition order by term_row) as major_cd_fill "
				+ " FROM student_term_major where major_cd is null";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=major_cd_fill, window_function={over={partition_by={1={column={name=student_id, table_ref=null}}, 2={column={name=value_partition, table_ref=null}}}, orderby={1={null_order=null, predicand={column={name=term_row, table_ref=null}}, sort_order=ASC}}}, function={null_handle=respect, function_name=first_value, parameters={1={column={name=major_cd, table_ref=null}}}}}}}, from={table={alias=null, table=student_term_major}}, where={condition={left={column={name=major_cd, table_ref=null}}, operator=is null}}}}",
				extractor.getSqlTree().toString());
		Assert.assertEquals("Interface is wrong", "[major_cd_fill]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{student_term_major={major_cd=[@23,162:169='major_cd',<299>,1:162], student_id=[@11,67:76='student_id',<299>,1:67], value_partition=[@13,79:93='value_partition',<299>,1:79], term_row=[@16,104:111='term_row',<299>,1:104]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={student_term_major={major_cd=[@23,162:169='major_cd',<299>,1:162], student_id=[@11,67:76='student_id',<299>,1:67], value_partition=[@13,79:93='value_partition',<299>,1:79], term_row=[@16,104:111='term_row',<299>,1:104]}, interface={major_cd_fill={window_function={over={partition_by={1={column={name=student_id, table_ref=null}}, 2={column={name=value_partition, table_ref=null}}}, orderby={1={null_order=null, predicand={column={name=term_row, table_ref=null}}, sort_order=ASC}}}, function={null_handle=respect, function_name=first_value, parameters={1={column={name=major_cd, table_ref=null}}}}}}}}}",
				extractor.getSymbolTable().toString());
	}

	
	@Test
	public void nthValueWindowIgnoreNullsTest() {
		String query = " SELECT  "
				+ "   nth_value(major_cd, 2) ignore nulls over (partition by student_id, value_partition order by term_row) as major_cd_fill "
				+ " FROM student_term_major where major_cd is null";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=major_cd_fill, window_function={over={partition_by={1={column={name=student_id, table_ref=null}}, 2={column={name=value_partition, table_ref=null}}}, orderby={1={null_order=null, predicand={column={name=term_row, table_ref=null}}, sort_order=ASC}}}, function={null_handle=ignore, function_name=nth_value, parameters={1={column={name=major_cd, table_ref=null}}, 2={literal=2}}}}}}, from={table={alias=null, table=student_term_major}}, where={condition={left={column={name=major_cd, table_ref=null}}, operator=is null}}}}",
				extractor.getSqlTree().toString());
		Assert.assertEquals("Interface is wrong", "[major_cd_fill]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{student_term_major={major_cd=[@25,162:169='major_cd',<299>,1:162], student_id=[@13,67:76='student_id',<299>,1:67], value_partition=[@15,79:93='value_partition',<299>,1:79], term_row=[@18,104:111='term_row',<299>,1:104]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={student_term_major={major_cd=[@25,162:169='major_cd',<299>,1:162], student_id=[@13,67:76='student_id',<299>,1:67], value_partition=[@15,79:93='value_partition',<299>,1:79], term_row=[@18,104:111='term_row',<299>,1:104]}, interface={major_cd_fill={window_function={over={partition_by={1={column={name=student_id, table_ref=null}}, 2={column={name=value_partition, table_ref=null}}}, orderby={1={null_order=null, predicand={column={name=term_row, table_ref=null}}, sort_order=ASC}}}, function={null_handle=ignore, function_name=nth_value, parameters={1={column={name=major_cd, table_ref=null}}, 2={literal=2}}}}}}}}",
				extractor.getSymbolTable().toString());
	}
	
	@Test
	public void nthValueWindowRespectNullsTest() {
		String query = " SELECT  "
				+ "   nth_value(major_cd, 2) respect nulls over (partition by student_id, value_partition order by term_row) as major_cd_fill "
				+ " FROM student_term_major where major_cd is null";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=major_cd_fill, window_function={over={partition_by={1={column={name=student_id, table_ref=null}}, 2={column={name=value_partition, table_ref=null}}}, orderby={1={null_order=null, predicand={column={name=term_row, table_ref=null}}, sort_order=ASC}}}, function={null_handle=respect, function_name=nth_value, parameters={1={column={name=major_cd, table_ref=null}}, 2={literal=2}}}}}}, from={table={alias=null, table=student_term_major}}, where={condition={left={column={name=major_cd, table_ref=null}}, operator=is null}}}}",
				extractor.getSqlTree().toString());
		Assert.assertEquals("Interface is wrong", "[major_cd_fill]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{student_term_major={major_cd=[@25,163:170='major_cd',<299>,1:163], student_id=[@13,68:77='student_id',<299>,1:68], value_partition=[@15,80:94='value_partition',<299>,1:80], term_row=[@18,105:112='term_row',<299>,1:105]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={student_term_major={major_cd=[@25,163:170='major_cd',<299>,1:163], student_id=[@13,68:77='student_id',<299>,1:68], value_partition=[@15,80:94='value_partition',<299>,1:80], term_row=[@18,105:112='term_row',<299>,1:105]}, interface={major_cd_fill={window_function={over={partition_by={1={column={name=student_id, table_ref=null}}, 2={column={name=value_partition, table_ref=null}}}, orderby={1={null_order=null, predicand={column={name=term_row, table_ref=null}}, sort_order=ASC}}}, function={null_handle=respect, function_name=nth_value, parameters={1={column={name=major_cd, table_ref=null}}, 2={literal=2}}}}}}}}",
				extractor.getSymbolTable().toString());
	}
	
	@Test
	public void nthValueWindowIgnoreNullsFromFirstTest() {
		String query = " SELECT  "
				+ "   nth_value(major_cd, 2) from first ignore nulls over (partition by student_id, value_partition order by term_row) as major_cd_fill "
				+ " FROM student_term_major where major_cd is null";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=major_cd_fill, window_function={over={partition_by={1={column={name=student_id, table_ref=null}}, 2={column={name=value_partition, table_ref=null}}}, orderby={1={null_order=null, predicand={column={name=term_row, table_ref=null}}, sort_order=ASC}}}, function={null_handle=ignore, function_name=nth_value, select_from=first, parameters={1={column={name=major_cd, table_ref=null}}, 2={literal=2}}}}}}, from={table={alias=null, table=student_term_major}}, where={condition={left={column={name=major_cd, table_ref=null}}, operator=is null}}}}",
				extractor.getSqlTree().toString());
		Assert.assertEquals("Interface is wrong", "[major_cd_fill]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{student_term_major={major_cd=[@27,173:180='major_cd',<299>,1:173], student_id=[@15,78:87='student_id',<299>,1:78], value_partition=[@17,90:104='value_partition',<299>,1:90], term_row=[@20,115:122='term_row',<299>,1:115]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={student_term_major={major_cd=[@27,173:180='major_cd',<299>,1:173], student_id=[@15,78:87='student_id',<299>,1:78], value_partition=[@17,90:104='value_partition',<299>,1:90], term_row=[@20,115:122='term_row',<299>,1:115]}, interface={major_cd_fill={window_function={over={partition_by={1={column={name=student_id, table_ref=null}}, 2={column={name=value_partition, table_ref=null}}}, orderby={1={null_order=null, predicand={column={name=term_row, table_ref=null}}, sort_order=ASC}}}, function={null_handle=ignore, function_name=nth_value, select_from=first, parameters={1={column={name=major_cd, table_ref=null}}, 2={literal=2}}}}}}}}",
				extractor.getSymbolTable().toString());
	}
	
	@Test
	public void nthValueWindowIgnoreNullsFromLastTest() {
		String query = " SELECT  "
				+ "   nth_value(major_cd, 2) from last ignore nulls over (partition by student_id, value_partition order by term_row) as major_cd_fill "
				+ " FROM student_term_major where major_cd is null";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=major_cd_fill, window_function={over={partition_by={1={column={name=student_id, table_ref=null}}, 2={column={name=value_partition, table_ref=null}}}, orderby={1={null_order=null, predicand={column={name=term_row, table_ref=null}}, sort_order=ASC}}}, function={null_handle=ignore, function_name=nth_value, select_from=last, parameters={1={column={name=major_cd, table_ref=null}}, 2={literal=2}}}}}}, from={table={alias=null, table=student_term_major}}, where={condition={left={column={name=major_cd, table_ref=null}}, operator=is null}}}}",
				extractor.getSqlTree().toString());
		Assert.assertEquals("Interface is wrong", "[major_cd_fill]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{student_term_major={major_cd=[@27,172:179='major_cd',<299>,1:172], student_id=[@15,77:86='student_id',<299>,1:77], value_partition=[@17,89:103='value_partition',<299>,1:89], term_row=[@20,114:121='term_row',<299>,1:114]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={student_term_major={major_cd=[@27,172:179='major_cd',<299>,1:172], student_id=[@15,77:86='student_id',<299>,1:77], value_partition=[@17,89:103='value_partition',<299>,1:89], term_row=[@20,114:121='term_row',<299>,1:114]}, interface={major_cd_fill={window_function={over={partition_by={1={column={name=student_id, table_ref=null}}, 2={column={name=value_partition, table_ref=null}}}, orderby={1={null_order=null, predicand={column={name=term_row, table_ref=null}}, sort_order=ASC}}}, function={null_handle=ignore, function_name=nth_value, select_from=last, parameters={1={column={name=major_cd, table_ref=null}}, 2={literal=2}}}}}}}}",
				extractor.getSymbolTable().toString());
	}
	
	// END SNOWFLAKE SELECT FROM WINDOW TESTS
	
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
		Assert.assertEquals("Table Dictionary is wrong", "{unknown={row_num=[@15,69:75='row_num',<299>,1:69], k_stfd=[@7,26:31='k_stfd',<299>,1:26], kppi=[@9,34:37='kppi',<299>,1:34], OBSERVATION_TM=[@12,48:61='OBSERVATION_TM',<299>,1:48]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{unknown={k_stfd=[@7,26:31='k_stfd',<299>,1:26], row_num=[@15,69:75='row_num',<299>,1:69], kppi=[@9,34:37='kppi',<299>,1:34], OBSERVATION_TM=[@12,48:61='OBSERVATION_TM',<299>,1:48]}}",
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
		Assert.assertEquals("Table Dictionary is wrong", "{a={<columnParam>={substitution={name=<columnParam>, type=column}}, row_num=[@19,67:67='a',<299>,1:67], k_stfd=[@10,41:41='a',<299>,1:41], kppi=[@14,51:51='a',<299>,1:51]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{a={<columnParam>={substitution={name=<columnParam>, type=column}}, k_stfd=[@10,41:41='a',<299>,1:41], row_num=[@19,67:67='a',<299>,1:67], kppi=[@14,51:51='a',<299>,1:51]}}",
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
		Assert.assertEquals("Table Dictionary is wrong", "{a={column=[@2,5:5='a',<299>,1:5], row_num=[@19,62:62='a',<299>,1:62], kppi=[@14,46:46='a',<299>,1:46], <k_stfd>={substitution={name=<k_stfd>, type=column}}}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{a={<k_stfd>={substitution={name=<k_stfd>, type=column}}, column=[@2,5:5='a',<299>,1:5], row_num=[@19,62:62='a',<299>,1:62], kppi=[@14,46:46='a',<299>,1:46]}}",
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
		Assert.assertEquals("Table Dictionary is wrong", "{a={column=[@2,5:5='a',<299>,1:5], <row_num>={substitution={name=<row_num>, type=column}}, k_stfd=[@10,34:34='a',<299>,1:34], kppi=[@14,44:44='a',<299>,1:44]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{a={k_stfd=[@10,34:34='a',<299>,1:34], column=[@2,5:5='a',<299>,1:5], <row_num>={substitution={name=<row_num>, type=column}}, kppi=[@14,44:44='a',<299>,1:44]}}",
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
		Assert.assertEquals("Table Dictionary is wrong", "{unknown={row_num=[@13,61:67='row_num',<299>,1:61], k_stfd=[@8,39:44='k_stfd',<299>,1:39], kppi=[@10,47:50='kppi',<299>,1:47]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{unknown={k_stfd=[@8,39:44='k_stfd',<299>,1:39], row_num=[@13,61:67='row_num',<299>,1:61], kppi=[@10,47:50='kppi',<299>,1:47]}}",
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
		Assert.assertEquals("Table Dictionary is wrong", "{unknown={column=[@2,5:10='column',<67>,1:5], row_num=[@13,56:62='row_num',<299>,1:56], kppi=[@10,42:45='kppi',<299>,1:42]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{unknown={column=[@2,5:10='column',<67>,1:5], row_num=[@13,56:62='row_num',<299>,1:56], kppi=[@10,42:45='kppi',<299>,1:42]}}",
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
		Assert.assertEquals("Table Dictionary is wrong", "{unknown={column=[@2,5:10='column',<67>,1:5], k_stfd=[@8,32:37='k_stfd',<299>,1:32], kppi=[@10,40:43='kppi',<299>,1:40]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{unknown={k_stfd=[@8,32:37='k_stfd',<299>,1:32], column=[@2,5:10='column',<67>,1:5], kppi=[@10,40:43='kppi',<299>,1:40]}}",
				extractor.getSymbolTable().toString());
	}

	// ITEM 61: Window Functions with window frame syntax in the order by clause 
	@Test
	public void windowWithUnboundedBoundingFrameTest() {
		final String query = " SELECT "
				+ " rank(parm) OVER (partition by k_stfd order by OBSERVATION_TM desc "
				+ " rows between unbounded preceding and unbounded following) AS key_rank "
				+ " FROM tab1 as a";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=key_rank, window_function={over={bracket={type=rows, between={end={value=unbounded, direction=FOLLOWING}, begin={value=unbounded, direction=PRECEDING}}}, partition_by={1={column={name=k_stfd, table_ref=null}}}, orderby={1={null_order=null, predicand={column={name=OBSERVATION_TM, table_ref=null}}, sort_order=desc}}}, function={function_name=rank, parameters={1={column={name=parm, table_ref=null}}}}}}}, from={table={alias=a, table=tab1}}}}",
				extractor.getSqlTree().toString());
	}
	
	@Test
	public void windowWithReversedUnboundedBoundingFrameTest() {
		final String query = " SELECT "
				+ " rank(parm) OVER (partition by k_stfd order by OBSERVATION_TM desc "
				+ " rows between unbounded following and unbounded preceding) AS key_rank "
				+ " FROM tab1 as a";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=key_rank, window_function={over={bracket={type=rows, between={end={value=unbounded, direction=PRECEDING}, begin={value=unbounded, direction=FOLLOWING}}}, partition_by={1={column={name=k_stfd, table_ref=null}}}, orderby={1={null_order=null, predicand={column={name=OBSERVATION_TM, table_ref=null}}, sort_order=desc}}}, function={function_name=rank, parameters={1={column={name=parm, table_ref=null}}}}}}}, from={table={alias=a, table=tab1}}}}",
				extractor.getSqlTree().toString());
	}
	
	@Test
	public void windowWithLeftBoundRightUnboundedFrameTest() {
		final String query = " SELECT "
				+ " rank(parm) OVER (partition by k_stfd order by OBSERVATION_TM desc "
				+ " rows between 100 preceding and unbounded following) AS key_rank "
				+ " FROM tab1 as a";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=key_rank, window_function={over={bracket={type=rows, between={end={value=unbounded, direction=FOLLOWING}, begin={value=100, direction=PRECEDING}}}, partition_by={1={column={name=k_stfd, table_ref=null}}}, orderby={1={null_order=null, predicand={column={name=OBSERVATION_TM, table_ref=null}}, sort_order=desc}}}, function={function_name=rank, parameters={1={column={name=parm, table_ref=null}}}}}}}, from={table={alias=a, table=tab1}}}}",
				extractor.getSqlTree().toString());
	}
	
	@Test
	public void windowWithLeftUnboundRightBoundFrameTest() {
		final String query = " SELECT "
				+ " rank(parm) OVER (partition by k_stfd order by OBSERVATION_TM desc "
				+ " rows between unbounded preceding and 25 following) AS key_rank "
				+ " FROM tab1 as a";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=key_rank, window_function={over={bracket={type=rows, between={end={value=25, direction=FOLLOWING}, begin={value=unbounded, direction=PRECEDING}}}, partition_by={1={column={name=k_stfd, table_ref=null}}}, orderby={1={null_order=null, predicand={column={name=OBSERVATION_TM, table_ref=null}}, sort_order=desc}}}, function={function_name=rank, parameters={1={column={name=parm, table_ref=null}}}}}}}, from={table={alias=a, table=tab1}}}}",
				extractor.getSqlTree().toString());
	}
	
	@Test
	public void windowWithLeftBoundRightBoundFrameTest() {
		final String query = " SELECT "
				+ " rank(parm) OVER (partition by k_stfd order by OBSERVATION_TM desc "
				+ " rows between 10 preceding and 25 following) AS key_rank "
				+ " FROM tab1 as a";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=key_rank, window_function={over={bracket={type=rows, between={end={value=25, direction=FOLLOWING}, begin={value=10, direction=PRECEDING}}}, partition_by={1={column={name=k_stfd, table_ref=null}}}, orderby={1={null_order=null, predicand={column={name=OBSERVATION_TM, table_ref=null}}, sort_order=desc}}}, function={function_name=rank, parameters={1={column={name=parm, table_ref=null}}}}}}}, from={table={alias=a, table=tab1}}}}",
				extractor.getSqlTree().toString());
	}
	
	@Test
	public void windowWithLeftCurrentRowRightBoundFrameTest() {
		final String query = " SELECT "
				+ " rank(parm) OVER (partition by k_stfd order by OBSERVATION_TM desc "
				+ " rows between current row and 25 following) AS key_rank "
				+ " FROM tab1 as a";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=key_rank, window_function={over={bracket={type=rows, between={end={value=25, direction=FOLLOWING}, begin={value=CURRENT ROW}}}, partition_by={1={column={name=k_stfd, table_ref=null}}}, orderby={1={null_order=null, predicand={column={name=OBSERVATION_TM, table_ref=null}}, sort_order=desc}}}, function={function_name=rank, parameters={1={column={name=parm, table_ref=null}}}}}}}, from={table={alias=a, table=tab1}}}}",
				extractor.getSqlTree().toString());
	}
	
	@Test
	public void windowWithLeftUnboundRightCurrentRowFrameTest() {
		final String query = " SELECT "
				+ " rank(parm) OVER (partition by k_stfd order by OBSERVATION_TM desc "
				+ " rows between unbounded preceding and current row) AS key_rank "
				+ " FROM tab1 as a";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=key_rank, window_function={over={bracket={type=rows, between={end={value=CURRENT ROW}, begin={value=unbounded, direction=PRECEDING}}}, partition_by={1={column={name=k_stfd, table_ref=null}}}, orderby={1={null_order=null, predicand={column={name=OBSERVATION_TM, table_ref=null}}, sort_order=desc}}}, function={function_name=rank, parameters={1={column={name=parm, table_ref=null}}}}}}}, from={table={alias=a, table=tab1}}}}",
				extractor.getSqlTree().toString());
	}
	
	@Test
	public void windowWithPrecedingUnboundFrameTest() {
		final String query = " SELECT "
				+ " rank(parm) OVER (partition by k_stfd order by OBSERVATION_TM desc "
				+ " rows unbounded preceding) AS key_rank "
				+ " FROM tab1 as a";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=key_rank, window_function={over={bracket={type=rows, value=unbounded, direction=PRECEDING}, partition_by={1={column={name=k_stfd, table_ref=null}}}, orderby={1={null_order=null, predicand={column={name=OBSERVATION_TM, table_ref=null}}, sort_order=desc}}}, function={function_name=rank, parameters={1={column={name=parm, table_ref=null}}}}}}}, from={table={alias=a, table=tab1}}}}",
				extractor.getSqlTree().toString());
	}
	
	@Test
	public void windowWithPrecedingBoundFrameTest() {
		final String query = " SELECT "
				+ " rank(parm) OVER (partition by k_stfd order by OBSERVATION_TM desc "
				+ " rows 30 preceding) AS key_rank "
				+ " FROM tab1 as a";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=key_rank, window_function={over={bracket={type=rows, value=30, direction=PRECEDING}, partition_by={1={column={name=k_stfd, table_ref=null}}}, orderby={1={null_order=null, predicand={column={name=OBSERVATION_TM, table_ref=null}}, sort_order=desc}}}, function={function_name=rank, parameters={1={column={name=parm, table_ref=null}}}}}}}, from={table={alias=a, table=tab1}}}}",
				extractor.getSqlTree().toString());
	}
	
	@Test
	public void windowWithCurrentRowFrameTest() {
		final String query = " SELECT "
				+ " rank(parm) OVER (partition by k_stfd order by OBSERVATION_TM desc "
				+ " rows current row) AS key_rank "
				+ " FROM tab1 as a";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=key_rank, window_function={over={bracket={type=rows, value=CURRENT ROW}, partition_by={1={column={name=k_stfd, table_ref=null}}}, orderby={1={null_order=null, predicand={column={name=OBSERVATION_TM, table_ref=null}}, sort_order=desc}}}, function={function_name=rank, parameters={1={column={name=parm, table_ref=null}}}}}}}, from={table={alias=a, table=tab1}}}}",
				extractor.getSqlTree().toString());
	}
	
	@Test
	public void windowWithPrecedingUnboundRangeFrameTest() {
		final String query = " SELECT "
				+ " rank(parm) OVER (partition by k_stfd order by OBSERVATION_TM desc "
				+ " range unboundED preceding) AS key_rank "
				+ " FROM tab1 as a";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=key_rank, window_function={over={bracket={type=range, value=unboundED, direction=PRECEDING}, partition_by={1={column={name=k_stfd, table_ref=null}}}, orderby={1={null_order=null, predicand={column={name=OBSERVATION_TM, table_ref=null}}, sort_order=desc}}}, function={function_name=rank, parameters={1={column={name=parm, table_ref=null}}}}}}}, from={table={alias=a, table=tab1}}}}",
				extractor.getSqlTree().toString());
	}
	
	@Test
	public void windowWithPrecedingBoundRangeFrameTest() {
		final String query = " SELECT "
				+ " rank(parm) OVER (partition by k_stfd order by OBSERVATION_TM desc "
				+ " range 30 preceding) AS key_rank "
				+ " FROM tab1 as a";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=key_rank, window_function={over={bracket={type=range, value=30, direction=PRECEDING}, partition_by={1={column={name=k_stfd, table_ref=null}}}, orderby={1={null_order=null, predicand={column={name=OBSERVATION_TM, table_ref=null}}, sort_order=desc}}}, function={function_name=rank, parameters={1={column={name=parm, table_ref=null}}}}}}}, from={table={alias=a, table=tab1}}}}",
				extractor.getSqlTree().toString());
	}
	
	@Test
	public void windowWithCurrentRowRangeFrameTest() {
		final String query = " SELECT "
				+ " rank(parm) OVER (partition by k_stfd order by OBSERVATION_TM desc "
				+ " range current row) AS key_rank "
				+ " FROM tab1 as a";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=key_rank, window_function={over={bracket={type=range, value=CURRENT ROW}, partition_by={1={column={name=k_stfd, table_ref=null}}}, orderby={1={null_order=null, predicand={column={name=OBSERVATION_TM, table_ref=null}}, sort_order=desc}}}, function={function_name=rank, parameters={1={column={name=parm, table_ref=null}}}}}}}, from={table={alias=a, table=tab1}}}}",
				extractor.getSqlTree().toString());
	}

	@Test
	public void windowWithLeftBoundRightBoundRangeFrameTest() {
		final String query = " SELECT "
				+ " rank(parm) OVER (partition by k_stfd order by OBSERVATION_TM desc "
				+ " range between 10 preceding and 10 following) AS key_rank "
				+ " FROM tab1 as a";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=key_rank, window_function={over={bracket={type=range, between={end={value=10, direction=FOLLOWING}, begin={value=10, direction=PRECEDING}}}, partition_by={1={column={name=k_stfd, table_ref=null}}}, orderby={1={null_order=null, predicand={column={name=OBSERVATION_TM, table_ref=null}}, sort_order=desc}}}, function={function_name=rank, parameters={1={column={name=parm, table_ref=null}}}}}}}, from={table={alias=a, table=tab1}}}}",
				extractor.getSqlTree().toString());
	}

	@Test
	public void realisticRangeFrameTest() {
		final String query = "SELECT " + 
				"  st.student_id, st.term_code, st.level_code " + 
				"  , major_code_1 " + 
				"  , COALESCE(first_value(st.major_code_1) OVER (PARTITION BY st.student_id, bfdf.major1_partition_downfill ORDER BY st.student_id, st.term_code), last_value(st.major_code_1) OVER (PARTITION BY st.student_id, bfdf.major1_partition_backfill ORDER BY st.student_id, st.term_code rows between unbounded preceding and unbounded following), 'NA') AS major_code_1_new " + 
				"  , degree_code_1 " + 
				"  , COALESCE(first_value(st.degree_code_1) OVER (PARTITION BY st.student_id, bfdf.degree1_partition_downfill ORDER BY st.student_id, st.term_code), last_value(st.degree_code_1) OVER (PARTITION BY st.student_id, bfdf.degree1_partition_backfill ORDER BY st.student_id, st.term_code rows between unbounded preceding and unbounded following), 'NA') AS degree_code_1_new " + 
				"  , concentration_code_1 " + 
				"  , first_value(st.concentration_code_1) OVER (PARTITION BY st.student_id, bfdf.conc1_partition_downfill ORDER BY st.student_id, st.term_code), last_value(st.concentration_code_1) OVER (PARTITION BY st.student_id, bfdf.conc1_partition_backfill ORDER BY st.student_id, st.term_code rows between unbounded preceding and unbounded following) AS concentration_code_1_new " + 
				"  , campus_code " + 
				"  , COALESCE(first_value(st.campus_code) OVER (PARTITION BY st.student_id, bfdf.campus_partition_downfill ORDER BY st.student_id, st.term_code), last_value(st.campus_code) OVER (PARTITION BY st.student_id, bfdf.campus_partition_backfill ORDER BY st.student_id, st.term_code rows between unbounded preceding and unbounded following), 'NA') AS campus_code_new " + 
				"  , college_code " + 
				"  , COALESCE(first_value(st.college_code) OVER (PARTITION BY st.student_id, bfdf.coll1_partition_downfill ORDER BY st.student_id, st.term_code), last_value(st.college_code) OVER (PARTITION BY st.student_id, bfdf.coll1_partition_backfill ORDER BY st.student_id, st.term_code rows between unbounded preceding and unbounded following), 'NA') AS college_code_new " + 
				"  , department_code " + 
				"  , COALESCE(first_value(st.department_code) OVER (PARTITION BY st.student_id, bfdf.dept_partition_downfill ORDER BY st.student_id, st.term_code), last_value(st.department_code) OVER (PARTITION BY st.student_id, bfdf.dept_partition_backfill ORDER BY st.student_id, st.term_code rows between unbounded preceding and unbounded following), 'NA') AS department_code_new " + 
				"  , major_code_2 " + 
				"  , COALESCE(first_value(st.major_code_2) OVER (PARTITION BY st.student_id, bfdf.major2_partition_downfill ORDER BY st.student_id, st.term_code), last_value(st.major_code_2) OVER (PARTITION BY st.student_id, bfdf.major2_partition_backfill ORDER BY st.student_id, st.term_code rows between unbounded preceding and unbounded following)) AS major_code_2_new " + 
				"  , concentration_code_2 " + 
				"  , COALESCE(first_value(st.concentration_code_2) OVER (PARTITION BY st.student_id, bfdf.conc2_partition_downfill ORDER BY st.student_id, st.term_code), last_value(st.concentration_code_2) OVER (PARTITION BY st.student_id, bfdf.conc2_partition_backfill ORDER BY st.student_id, st.term_code rows between unbounded preceding and unbounded following)) AS concentration_code_2_new " + 
				"  , degree_code_2 " + 
				"  , COALESCE(first_value(st.degree_code_2) OVER (PARTITION BY st.student_id, bfdf.degree2_partition_downfill ORDER BY st.student_id, st.term_code), last_value(st.degree_code_2) OVER (PARTITION BY st.student_id, bfdf.degree2_partition_backfill ORDER BY st.student_id, st.term_code rows between unbounded preceding and unbounded following)) AS degree_code_2_new " + 
				"  , college_code_2 " + 
				"  , COALESCE(first_value(st.college_code_2) OVER (PARTITION BY st.student_id, bfdf.coll2_partition_downfill ORDER BY st.student_id, st.term_code), last_value(st.college_code_2) OVER (PARTITION BY st.student_id, bfdf.coll2_partition_backfill ORDER BY st.student_id, st.term_code rows between unbounded preceding and unbounded following)) AS college_code_2_new " + 
				"  , major_code_3 " + 
				"  , COALESCE(first_value(st.major_code_3) OVER (PARTITION BY st.student_id, bfdf.major_code_3_downfill ORDER BY st.student_id, st.term_code), last_value(st.major_code_3) OVER (PARTITION BY st.student_id, bfdf.major_code_3_backfill ORDER BY st.student_id, st.term_code rows between unbounded preceding and unbounded following)) AS major_code_3_new " + 
				"  , concentration_code_3 " + 
				"  , COALESCE(first_value(st.concentration_code_3) OVER (PARTITION BY st.student_id, bfdf.concentration_code_3_downfill ORDER BY st.student_id, st.term_code), last_value(st.concentration_code_3) OVER (PARTITION BY st.student_id, bfdf.concentration_code_3_backfill ORDER BY st.student_id, st.term_code rows between unbounded preceding and unbounded following)) AS concentration_code_3_new " + 
				"  , degree_code_3 " + 
				"  , COALESCE(first_value(st.degree_code_3) OVER (PARTITION BY st.student_id, bfdf.degree_code_3_downfill ORDER BY st.student_id, st.term_code), last_value(st.degree_code_3) OVER (PARTITION BY st.student_id, bfdf.degree_code_3_backfill ORDER BY st.student_id, st.term_code rows between unbounded preceding and unbounded following)) AS degree_code_3_new " + 
				"  , college_code_3 " + 
				"  , COALESCE(first_value(st.college_code_3) OVER (PARTITION BY st.student_id, bfdf.college_code_3_downfill ORDER BY st.student_id, st.term_code), last_value(st.college_code_3) OVER (PARTITION BY st.student_id, bfdf.college_code_3_backfill ORDER BY st.student_id, st.term_code rows between unbounded preceding and unbounded following)) AS college_code_3_new " + 
				"  , major_code_4 " + 
				"  , COALESCE(first_value(st.major_code_4) OVER (PARTITION BY st.student_id, bfdf.major_code_4_downfill ORDER BY st.student_id, st.term_code), last_value(st.major_code_4) OVER (PARTITION BY st.student_id, bfdf.major_code_4_backfill ORDER BY st.student_id, st.term_code rows between unbounded preceding and unbounded following)) AS major_code_4_new " + 
				"  , concentration_code_4 " + 
				"  , COALESCE(first_value(st.concentration_code_4) OVER (PARTITION BY st.student_id, bfdf.concentration_code_4_downfill ORDER BY st.student_id, st.term_code), last_value(st.concentration_code_4) OVER (PARTITION BY st.student_id, bfdf.concentration_code_4_backfill ORDER BY st.student_id, st.term_code rows between unbounded preceding and unbounded following)) AS concentration_code_4_new " + 
				"  , degree_code_4 " + 
				"  , COALESCE(first_value(st.degree_code_4) OVER (PARTITION BY st.student_id, bfdf.degree_code_4_downfill ORDER BY st.student_id, st.term_code), last_value(st.degree_code_4) OVER (PARTITION BY st.student_id, bfdf.degree_code_4_backfill ORDER BY st.student_id, st.term_code rows between unbounded preceding and unbounded following)) AS degree_code_4_new " + 
				"  , college_code_4 " + 
				"  , COALESCE(first_value(st.college_code_4) OVER (PARTITION BY st.student_id, bfdf.college_code_4_downfill ORDER BY st.student_id, st.term_code), last_value(st.college_code_4) OVER (PARTITION BY st.student_id, bfdf.college_code_4_backfill ORDER BY st.student_id, st.term_code rows between unbounded preceding and unbounded following)) AS college_code_4_new " + 
				"  , department_code_2 " + 
				"  , COALESCE(first_value(st.department_code_2) OVER (PARTITION BY st.student_id, bfdf.department_code_2_downfill ORDER BY st.student_id, st.term_code), last_value(st.department_code_2) OVER (PARTITION BY st.student_id, bfdf.department_code_2_backfill ORDER BY st.student_id, st.term_code rows between unbounded preceding and unbounded following)) AS department_code_2_new " + 
				"  , department_code_3 " + 
				"  , COALESCE(first_value(st.department_code_3) OVER (PARTITION BY st.student_id, bfdf.department_code_3_downfill ORDER BY st.student_id, st.term_code), last_value(st.department_code_3) OVER (PARTITION BY st.student_id, bfdf.department_code_3_backfill ORDER BY st.student_id, st.term_code rows between unbounded preceding and unbounded following)) AS department_code_3_new " + 
				"  , department_code_4 " + 
				"  , COALESCE(first_value(st.department_code_4) OVER (PARTITION BY st.student_id, bfdf.department_code_4_downfill ORDER BY st.student_id, st.term_code), last_value(st.department_code_4) OVER (PARTITION BY st.student_id, bfdf.department_code_4_backfill ORDER BY st.student_id, st.term_code rows between unbounded preceding and unbounded following)) AS department_code_4_new  , first_value(st.academic_standing_code) OVER (PARTITION BY st.student_id, bfdf.as_partition_downfill ORDER BY st.student_id, st.term_code) AS academic_standing_code_new " + 
				"  , academic_standing_code " + 
				"FROM sar_student_term st " + 
				"JOIN bf_df_values bfdf ON st.term_code = bfdf.term_code AND st.student_id = bfdf.student_id AND st.level_code = bfdf.level_code " + 
				"WHERE 1=1;";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
			
		Assert.assertEquals("AST is wrong", "{SQL={select={44={function={parameters={1={window_function={over={partition_by={1={column={name=student_id, table_ref=st}}, 2={column={name=department_code_3_downfill, table_ref=bfdf}}}, orderby={1={null_order=null, predicand={column={name=student_id, table_ref=st}}, sort_order=ASC}, 2={null_order=null, predicand={column={name=term_code, table_ref=st}}, sort_order=ASC}}}, function={function_name=first_value, parameters={1={column={name=department_code_3, table_ref=st}}}}}}, 2={window_function={over={bracket={type=rows, between={end={value=unbounded, direction=FOLLOWING}, begin={value=unbounded, direction=PRECEDING}}}, partition_by={1={column={name=student_id, table_ref=st}}, 2={column={name=department_code_3_backfill, table_ref=bfdf}}}, orderby={1={null_order=null, predicand={column={name=student_id, table_ref=st}}, sort_order=ASC}, 2={null_order=null, predicand={column={name=term_code, table_ref=st}}, sort_order=ASC}}}, function={function_name=last_value, parameters={1={column={name=department_code_3, table_ref=st}}}}}}}, function_name=COALESCE}, alias=department_code_3_new}, 45={column={name=department_code_4, table_ref=null}}, 46={function={parameters={1={window_function={over={partition_by={1={column={name=student_id, table_ref=st}}, 2={column={name=department_code_4_downfill, table_ref=bfdf}}}, orderby={1={null_order=null, predicand={column={name=student_id, table_ref=st}}, sort_order=ASC}, 2={null_order=null, predicand={column={name=term_code, table_ref=st}}, sort_order=ASC}}}, function={function_name=first_value, parameters={1={column={name=department_code_4, table_ref=st}}}}}}, 2={window_function={over={bracket={type=rows, between={end={value=unbounded, direction=FOLLOWING}, begin={value=unbounded, direction=PRECEDING}}}, partition_by={1={column={name=student_id, table_ref=st}}, 2={column={name=department_code_4_backfill, table_ref=bfdf}}}, orderby={1={null_order=null, predicand={column={name=student_id, table_ref=st}}, sort_order=ASC}, 2={null_order=null, predicand={column={name=term_code, table_ref=st}}, sort_order=ASC}}}, function={function_name=last_value, parameters={1={column={name=department_code_4, table_ref=st}}}}}}}, function_name=COALESCE}, alias=department_code_4_new}, 47={alias=academic_standing_code_new, window_function={over={partition_by={1={column={name=student_id, table_ref=st}}, 2={column={name=as_partition_downfill, table_ref=bfdf}}}, orderby={1={null_order=null, predicand={column={name=student_id, table_ref=st}}, sort_order=ASC}, 2={null_order=null, predicand={column={name=term_code, table_ref=st}}, sort_order=ASC}}}, function={function_name=first_value, parameters={1={column={name=academic_standing_code, table_ref=st}}}}}}, 48={column={name=academic_standing_code, table_ref=null}}, 10={alias=concentration_code_1_new, window_function={over={bracket={type=rows, between={end={value=unbounded, direction=FOLLOWING}, begin={value=unbounded, direction=PRECEDING}}}, partition_by={1={column={name=student_id, table_ref=st}}, 2={column={name=conc1_partition_backfill, table_ref=bfdf}}}, orderby={1={null_order=null, predicand={column={name=student_id, table_ref=st}}, sort_order=ASC}, 2={null_order=null, predicand={column={name=term_code, table_ref=st}}, sort_order=ASC}}}, function={function_name=last_value, parameters={1={column={name=concentration_code_1, table_ref=st}}}}}}, 11={column={name=campus_code, table_ref=null}}, 12={function={parameters={1={window_function={over={partition_by={1={column={name=student_id, table_ref=st}}, 2={column={name=campus_partition_downfill, table_ref=bfdf}}}, orderby={1={null_order=null, predicand={column={name=student_id, table_ref=st}}, sort_order=ASC}, 2={null_order=null, predicand={column={name=term_code, table_ref=st}}, sort_order=ASC}}}, function={function_name=first_value, parameters={1={column={name=campus_code, table_ref=st}}}}}}, 2={window_function={over={bracket={type=rows, between={end={value=unbounded, direction=FOLLOWING}, begin={value=unbounded, direction=PRECEDING}}}, partition_by={1={column={name=student_id, table_ref=st}}, 2={column={name=campus_partition_backfill, table_ref=bfdf}}}, orderby={1={null_order=null, predicand={column={name=student_id, table_ref=st}}, sort_order=ASC}, 2={null_order=null, predicand={column={name=term_code, table_ref=st}}, sort_order=ASC}}}, function={function_name=last_value, parameters={1={column={name=campus_code, table_ref=st}}}}}}, 3={literal='NA'}}, function_name=COALESCE}, alias=campus_code_new}, 13={column={name=college_code, table_ref=null}}, 14={function={parameters={1={window_function={over={partition_by={1={column={name=student_id, table_ref=st}}, 2={column={name=coll1_partition_downfill, table_ref=bfdf}}}, orderby={1={null_order=null, predicand={column={name=student_id, table_ref=st}}, sort_order=ASC}, 2={null_order=null, predicand={column={name=term_code, table_ref=st}}, sort_order=ASC}}}, function={function_name=first_value, parameters={1={column={name=college_code, table_ref=st}}}}}}, 2={window_function={over={bracket={type=rows, between={end={value=unbounded, direction=FOLLOWING}, begin={value=unbounded, direction=PRECEDING}}}, partition_by={1={column={name=student_id, table_ref=st}}, 2={column={name=coll1_partition_backfill, table_ref=bfdf}}}, orderby={1={null_order=null, predicand={column={name=student_id, table_ref=st}}, sort_order=ASC}, 2={null_order=null, predicand={column={name=term_code, table_ref=st}}, sort_order=ASC}}}, function={function_name=last_value, parameters={1={column={name=college_code, table_ref=st}}}}}}, 3={literal='NA'}}, function_name=COALESCE}, alias=college_code_new}, 15={column={name=department_code, table_ref=null}}, 16={function={parameters={1={window_function={over={partition_by={1={column={name=student_id, table_ref=st}}, 2={column={name=dept_partition_downfill, table_ref=bfdf}}}, orderby={1={null_order=null, predicand={column={name=student_id, table_ref=st}}, sort_order=ASC}, 2={null_order=null, predicand={column={name=term_code, table_ref=st}}, sort_order=ASC}}}, function={function_name=first_value, parameters={1={column={name=department_code, table_ref=st}}}}}}, 2={window_function={over={bracket={type=rows, between={end={value=unbounded, direction=FOLLOWING}, begin={value=unbounded, direction=PRECEDING}}}, partition_by={1={column={name=student_id, table_ref=st}}, 2={column={name=dept_partition_backfill, table_ref=bfdf}}}, orderby={1={null_order=null, predicand={column={name=student_id, table_ref=st}}, sort_order=ASC}, 2={null_order=null, predicand={column={name=term_code, table_ref=st}}, sort_order=ASC}}}, function={function_name=last_value, parameters={1={column={name=department_code, table_ref=st}}}}}}, 3={literal='NA'}}, function_name=COALESCE}, alias=department_code_new}, 17={column={name=major_code_2, table_ref=null}}, 18={function={parameters={1={window_function={over={partition_by={1={column={name=student_id, table_ref=st}}, 2={column={name=major2_partition_downfill, table_ref=bfdf}}}, orderby={1={null_order=null, predicand={column={name=student_id, table_ref=st}}, sort_order=ASC}, 2={null_order=null, predicand={column={name=term_code, table_ref=st}}, sort_order=ASC}}}, function={function_name=first_value, parameters={1={column={name=major_code_2, table_ref=st}}}}}}, 2={window_function={over={bracket={type=rows, between={end={value=unbounded, direction=FOLLOWING}, begin={value=unbounded, direction=PRECEDING}}}, partition_by={1={column={name=student_id, table_ref=st}}, 2={column={name=major2_partition_backfill, table_ref=bfdf}}}, orderby={1={null_order=null, predicand={column={name=student_id, table_ref=st}}, sort_order=ASC}, 2={null_order=null, predicand={column={name=term_code, table_ref=st}}, sort_order=ASC}}}, function={function_name=last_value, parameters={1={column={name=major_code_2, table_ref=st}}}}}}}, function_name=COALESCE}, alias=major_code_2_new}, 19={column={name=concentration_code_2, table_ref=null}}, 1={column={name=student_id, table_ref=st}}, 2={column={name=term_code, table_ref=st}}, 3={column={name=level_code, table_ref=st}}, 4={column={name=major_code_1, table_ref=null}}, 5={function={parameters={1={window_function={over={partition_by={1={column={name=student_id, table_ref=st}}, 2={column={name=major1_partition_downfill, table_ref=bfdf}}}, orderby={1={null_order=null, predicand={column={name=student_id, table_ref=st}}, sort_order=ASC}, 2={null_order=null, predicand={column={name=term_code, table_ref=st}}, sort_order=ASC}}}, function={function_name=first_value, parameters={1={column={name=major_code_1, table_ref=st}}}}}}, 2={window_function={over={bracket={type=rows, between={end={value=unbounded, direction=FOLLOWING}, begin={value=unbounded, direction=PRECEDING}}}, partition_by={1={column={name=student_id, table_ref=st}}, 2={column={name=major1_partition_backfill, table_ref=bfdf}}}, orderby={1={null_order=null, predicand={column={name=student_id, table_ref=st}}, sort_order=ASC}, 2={null_order=null, predicand={column={name=term_code, table_ref=st}}, sort_order=ASC}}}, function={function_name=last_value, parameters={1={column={name=major_code_1, table_ref=st}}}}}}, 3={literal='NA'}}, function_name=COALESCE}, alias=major_code_1_new}, 6={column={name=degree_code_1, table_ref=null}}, 7={function={parameters={1={window_function={over={partition_by={1={column={name=student_id, table_ref=st}}, 2={column={name=degree1_partition_downfill, table_ref=bfdf}}}, orderby={1={null_order=null, predicand={column={name=student_id, table_ref=st}}, sort_order=ASC}, 2={null_order=null, predicand={column={name=term_code, table_ref=st}}, sort_order=ASC}}}, function={function_name=first_value, parameters={1={column={name=degree_code_1, table_ref=st}}}}}}, 2={window_function={over={bracket={type=rows, between={end={value=unbounded, direction=FOLLOWING}, begin={value=unbounded, direction=PRECEDING}}}, partition_by={1={column={name=student_id, table_ref=st}}, 2={column={name=degree1_partition_backfill, table_ref=bfdf}}}, orderby={1={null_order=null, predicand={column={name=student_id, table_ref=st}}, sort_order=ASC}, 2={null_order=null, predicand={column={name=term_code, table_ref=st}}, sort_order=ASC}}}, function={function_name=last_value, parameters={1={column={name=degree_code_1, table_ref=st}}}}}}, 3={literal='NA'}}, function_name=COALESCE}, alias=degree_code_1_new}, 8={column={name=concentration_code_1, table_ref=null}}, 9={window_function={over={partition_by={1={column={name=student_id, table_ref=st}}, 2={column={name=conc1_partition_downfill, table_ref=bfdf}}}, orderby={1={null_order=null, predicand={column={name=student_id, table_ref=st}}, sort_order=ASC}, 2={null_order=null, predicand={column={name=term_code, table_ref=st}}, sort_order=ASC}}}, function={function_name=first_value, parameters={1={column={name=concentration_code_1, table_ref=st}}}}}}, 20={function={parameters={1={window_function={over={partition_by={1={column={name=student_id, table_ref=st}}, 2={column={name=conc2_partition_downfill, table_ref=bfdf}}}, orderby={1={null_order=null, predicand={column={name=student_id, table_ref=st}}, sort_order=ASC}, 2={null_order=null, predicand={column={name=term_code, table_ref=st}}, sort_order=ASC}}}, function={function_name=first_value, parameters={1={column={name=concentration_code_2, table_ref=st}}}}}}, 2={window_function={over={bracket={type=rows, between={end={value=unbounded, direction=FOLLOWING}, begin={value=unbounded, direction=PRECEDING}}}, partition_by={1={column={name=student_id, table_ref=st}}, 2={column={name=conc2_partition_backfill, table_ref=bfdf}}}, orderby={1={null_order=null, predicand={column={name=student_id, table_ref=st}}, sort_order=ASC}, 2={null_order=null, predicand={column={name=term_code, table_ref=st}}, sort_order=ASC}}}, function={function_name=last_value, parameters={1={column={name=concentration_code_2, table_ref=st}}}}}}}, function_name=COALESCE}, alias=concentration_code_2_new}, 21={column={name=degree_code_2, table_ref=null}}, 22={function={parameters={1={window_function={over={partition_by={1={column={name=student_id, table_ref=st}}, 2={column={name=degree2_partition_downfill, table_ref=bfdf}}}, orderby={1={null_order=null, predicand={column={name=student_id, table_ref=st}}, sort_order=ASC}, 2={null_order=null, predicand={column={name=term_code, table_ref=st}}, sort_order=ASC}}}, function={function_name=first_value, parameters={1={column={name=degree_code_2, table_ref=st}}}}}}, 2={window_function={over={bracket={type=rows, between={end={value=unbounded, direction=FOLLOWING}, begin={value=unbounded, direction=PRECEDING}}}, partition_by={1={column={name=student_id, table_ref=st}}, 2={column={name=degree2_partition_backfill, table_ref=bfdf}}}, orderby={1={null_order=null, predicand={column={name=student_id, table_ref=st}}, sort_order=ASC}, 2={null_order=null, predicand={column={name=term_code, table_ref=st}}, sort_order=ASC}}}, function={function_name=last_value, parameters={1={column={name=degree_code_2, table_ref=st}}}}}}}, function_name=COALESCE}, alias=degree_code_2_new}, 23={column={name=college_code_2, table_ref=null}}, 24={function={parameters={1={window_function={over={partition_by={1={column={name=student_id, table_ref=st}}, 2={column={name=coll2_partition_downfill, table_ref=bfdf}}}, orderby={1={null_order=null, predicand={column={name=student_id, table_ref=st}}, sort_order=ASC}, 2={null_order=null, predicand={column={name=term_code, table_ref=st}}, sort_order=ASC}}}, function={function_name=first_value, parameters={1={column={name=college_code_2, table_ref=st}}}}}}, 2={window_function={over={bracket={type=rows, between={end={value=unbounded, direction=FOLLOWING}, begin={value=unbounded, direction=PRECEDING}}}, partition_by={1={column={name=student_id, table_ref=st}}, 2={column={name=coll2_partition_backfill, table_ref=bfdf}}}, orderby={1={null_order=null, predicand={column={name=student_id, table_ref=st}}, sort_order=ASC}, 2={null_order=null, predicand={column={name=term_code, table_ref=st}}, sort_order=ASC}}}, function={function_name=last_value, parameters={1={column={name=college_code_2, table_ref=st}}}}}}}, function_name=COALESCE}, alias=college_code_2_new}, 25={column={name=major_code_3, table_ref=null}}, 26={function={parameters={1={window_function={over={partition_by={1={column={name=student_id, table_ref=st}}, 2={column={name=major_code_3_downfill, table_ref=bfdf}}}, orderby={1={null_order=null, predicand={column={name=student_id, table_ref=st}}, sort_order=ASC}, 2={null_order=null, predicand={column={name=term_code, table_ref=st}}, sort_order=ASC}}}, function={function_name=first_value, parameters={1={column={name=major_code_3, table_ref=st}}}}}}, 2={window_function={over={bracket={type=rows, between={end={value=unbounded, direction=FOLLOWING}, begin={value=unbounded, direction=PRECEDING}}}, partition_by={1={column={name=student_id, table_ref=st}}, 2={column={name=major_code_3_backfill, table_ref=bfdf}}}, orderby={1={null_order=null, predicand={column={name=student_id, table_ref=st}}, sort_order=ASC}, 2={null_order=null, predicand={column={name=term_code, table_ref=st}}, sort_order=ASC}}}, function={function_name=last_value, parameters={1={column={name=major_code_3, table_ref=st}}}}}}}, function_name=COALESCE}, alias=major_code_3_new}, 27={column={name=concentration_code_3, table_ref=null}}, 28={function={parameters={1={window_function={over={partition_by={1={column={name=student_id, table_ref=st}}, 2={column={name=concentration_code_3_downfill, table_ref=bfdf}}}, orderby={1={null_order=null, predicand={column={name=student_id, table_ref=st}}, sort_order=ASC}, 2={null_order=null, predicand={column={name=term_code, table_ref=st}}, sort_order=ASC}}}, function={function_name=first_value, parameters={1={column={name=concentration_code_3, table_ref=st}}}}}}, 2={window_function={over={bracket={type=rows, between={end={value=unbounded, direction=FOLLOWING}, begin={value=unbounded, direction=PRECEDING}}}, partition_by={1={column={name=student_id, table_ref=st}}, 2={column={name=concentration_code_3_backfill, table_ref=bfdf}}}, orderby={1={null_order=null, predicand={column={name=student_id, table_ref=st}}, sort_order=ASC}, 2={null_order=null, predicand={column={name=term_code, table_ref=st}}, sort_order=ASC}}}, function={function_name=last_value, parameters={1={column={name=concentration_code_3, table_ref=st}}}}}}}, function_name=COALESCE}, alias=concentration_code_3_new}, 29={column={name=degree_code_3, table_ref=null}}, 30={function={parameters={1={window_function={over={partition_by={1={column={name=student_id, table_ref=st}}, 2={column={name=degree_code_3_downfill, table_ref=bfdf}}}, orderby={1={null_order=null, predicand={column={name=student_id, table_ref=st}}, sort_order=ASC}, 2={null_order=null, predicand={column={name=term_code, table_ref=st}}, sort_order=ASC}}}, function={function_name=first_value, parameters={1={column={name=degree_code_3, table_ref=st}}}}}}, 2={window_function={over={bracket={type=rows, between={end={value=unbounded, direction=FOLLOWING}, begin={value=unbounded, direction=PRECEDING}}}, partition_by={1={column={name=student_id, table_ref=st}}, 2={column={name=degree_code_3_backfill, table_ref=bfdf}}}, orderby={1={null_order=null, predicand={column={name=student_id, table_ref=st}}, sort_order=ASC}, 2={null_order=null, predicand={column={name=term_code, table_ref=st}}, sort_order=ASC}}}, function={function_name=last_value, parameters={1={column={name=degree_code_3, table_ref=st}}}}}}}, function_name=COALESCE}, alias=degree_code_3_new}, 31={column={name=college_code_3, table_ref=null}}, 32={function={parameters={1={window_function={over={partition_by={1={column={name=student_id, table_ref=st}}, 2={column={name=college_code_3_downfill, table_ref=bfdf}}}, orderby={1={null_order=null, predicand={column={name=student_id, table_ref=st}}, sort_order=ASC}, 2={null_order=null, predicand={column={name=term_code, table_ref=st}}, sort_order=ASC}}}, function={function_name=first_value, parameters={1={column={name=college_code_3, table_ref=st}}}}}}, 2={window_function={over={bracket={type=rows, between={end={value=unbounded, direction=FOLLOWING}, begin={value=unbounded, direction=PRECEDING}}}, partition_by={1={column={name=student_id, table_ref=st}}, 2={column={name=college_code_3_backfill, table_ref=bfdf}}}, orderby={1={null_order=null, predicand={column={name=student_id, table_ref=st}}, sort_order=ASC}, 2={null_order=null, predicand={column={name=term_code, table_ref=st}}, sort_order=ASC}}}, function={function_name=last_value, parameters={1={column={name=college_code_3, table_ref=st}}}}}}}, function_name=COALESCE}, alias=college_code_3_new}, 33={column={name=major_code_4, table_ref=null}}, 34={function={parameters={1={window_function={over={partition_by={1={column={name=student_id, table_ref=st}}, 2={column={name=major_code_4_downfill, table_ref=bfdf}}}, orderby={1={null_order=null, predicand={column={name=student_id, table_ref=st}}, sort_order=ASC}, 2={null_order=null, predicand={column={name=term_code, table_ref=st}}, sort_order=ASC}}}, function={function_name=first_value, parameters={1={column={name=major_code_4, table_ref=st}}}}}}, 2={window_function={over={bracket={type=rows, between={end={value=unbounded, direction=FOLLOWING}, begin={value=unbounded, direction=PRECEDING}}}, partition_by={1={column={name=student_id, table_ref=st}}, 2={column={name=major_code_4_backfill, table_ref=bfdf}}}, orderby={1={null_order=null, predicand={column={name=student_id, table_ref=st}}, sort_order=ASC}, 2={null_order=null, predicand={column={name=term_code, table_ref=st}}, sort_order=ASC}}}, function={function_name=last_value, parameters={1={column={name=major_code_4, table_ref=st}}}}}}}, function_name=COALESCE}, alias=major_code_4_new}, 35={column={name=concentration_code_4, table_ref=null}}, 36={function={parameters={1={window_function={over={partition_by={1={column={name=student_id, table_ref=st}}, 2={column={name=concentration_code_4_downfill, table_ref=bfdf}}}, orderby={1={null_order=null, predicand={column={name=student_id, table_ref=st}}, sort_order=ASC}, 2={null_order=null, predicand={column={name=term_code, table_ref=st}}, sort_order=ASC}}}, function={function_name=first_value, parameters={1={column={name=concentration_code_4, table_ref=st}}}}}}, 2={window_function={over={bracket={type=rows, between={end={value=unbounded, direction=FOLLOWING}, begin={value=unbounded, direction=PRECEDING}}}, partition_by={1={column={name=student_id, table_ref=st}}, 2={column={name=concentration_code_4_backfill, table_ref=bfdf}}}, orderby={1={null_order=null, predicand={column={name=student_id, table_ref=st}}, sort_order=ASC}, 2={null_order=null, predicand={column={name=term_code, table_ref=st}}, sort_order=ASC}}}, function={function_name=last_value, parameters={1={column={name=concentration_code_4, table_ref=st}}}}}}}, function_name=COALESCE}, alias=concentration_code_4_new}, 37={column={name=degree_code_4, table_ref=null}}, 38={function={parameters={1={window_function={over={partition_by={1={column={name=student_id, table_ref=st}}, 2={column={name=degree_code_4_downfill, table_ref=bfdf}}}, orderby={1={null_order=null, predicand={column={name=student_id, table_ref=st}}, sort_order=ASC}, 2={null_order=null, predicand={column={name=term_code, table_ref=st}}, sort_order=ASC}}}, function={function_name=first_value, parameters={1={column={name=degree_code_4, table_ref=st}}}}}}, 2={window_function={over={bracket={type=rows, between={end={value=unbounded, direction=FOLLOWING}, begin={value=unbounded, direction=PRECEDING}}}, partition_by={1={column={name=student_id, table_ref=st}}, 2={column={name=degree_code_4_backfill, table_ref=bfdf}}}, orderby={1={null_order=null, predicand={column={name=student_id, table_ref=st}}, sort_order=ASC}, 2={null_order=null, predicand={column={name=term_code, table_ref=st}}, sort_order=ASC}}}, function={function_name=last_value, parameters={1={column={name=degree_code_4, table_ref=st}}}}}}}, function_name=COALESCE}, alias=degree_code_4_new}, 39={column={name=college_code_4, table_ref=null}}, 40={function={parameters={1={window_function={over={partition_by={1={column={name=student_id, table_ref=st}}, 2={column={name=college_code_4_downfill, table_ref=bfdf}}}, orderby={1={null_order=null, predicand={column={name=student_id, table_ref=st}}, sort_order=ASC}, 2={null_order=null, predicand={column={name=term_code, table_ref=st}}, sort_order=ASC}}}, function={function_name=first_value, parameters={1={column={name=college_code_4, table_ref=st}}}}}}, 2={window_function={over={bracket={type=rows, between={end={value=unbounded, direction=FOLLOWING}, begin={value=unbounded, direction=PRECEDING}}}, partition_by={1={column={name=student_id, table_ref=st}}, 2={column={name=college_code_4_backfill, table_ref=bfdf}}}, orderby={1={null_order=null, predicand={column={name=student_id, table_ref=st}}, sort_order=ASC}, 2={null_order=null, predicand={column={name=term_code, table_ref=st}}, sort_order=ASC}}}, function={function_name=last_value, parameters={1={column={name=college_code_4, table_ref=st}}}}}}}, function_name=COALESCE}, alias=college_code_4_new}, 41={column={name=department_code_2, table_ref=null}}, 42={function={parameters={1={window_function={over={partition_by={1={column={name=student_id, table_ref=st}}, 2={column={name=department_code_2_downfill, table_ref=bfdf}}}, orderby={1={null_order=null, predicand={column={name=student_id, table_ref=st}}, sort_order=ASC}, 2={null_order=null, predicand={column={name=term_code, table_ref=st}}, sort_order=ASC}}}, function={function_name=first_value, parameters={1={column={name=department_code_2, table_ref=st}}}}}}, 2={window_function={over={bracket={type=rows, between={end={value=unbounded, direction=FOLLOWING}, begin={value=unbounded, direction=PRECEDING}}}, partition_by={1={column={name=student_id, table_ref=st}}, 2={column={name=department_code_2_backfill, table_ref=bfdf}}}, orderby={1={null_order=null, predicand={column={name=student_id, table_ref=st}}, sort_order=ASC}, 2={null_order=null, predicand={column={name=term_code, table_ref=st}}, sort_order=ASC}}}, function={function_name=last_value, parameters={1={column={name=department_code_2, table_ref=st}}}}}}}, function_name=COALESCE}, alias=department_code_2_new}, 43={column={name=department_code_3, table_ref=null}}}, from={join={1={table={alias=st, table=sar_student_term}}, 2={join=JOIN, on={and={1={condition={left={column={name=term_code, table_ref=st}}, right={column={name=term_code, table_ref=bfdf}}, operator==}}, 2={condition={left={column={name=student_id, table_ref=st}}, right={column={name=student_id, table_ref=bfdf}}, operator==}}, 3={condition={left={column={name=level_code, table_ref=st}}, right={column={name=level_code, table_ref=bfdf}}, operator==}}}}}, 3={table={alias=bfdf, table=bf_df_values}}}}, where={condition={left={literal=1}, right={literal=1}, operator==}}}}",
				extractor.getSqlTree().toString());
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
//
//	@Test
//	public void substitutionsOfColumnsWithTableNoAliasTest() {
//		// TODO: ITEM 28 - Does not Parse without Alias, Tuple Substitution Variable does not appear in Symbol Tree or Table Dictionary
//	This is okay. It should require some reference so that we can build a proper dictionary.

//		final String query = " Select col1 as redvalue, col2 as greenvalue "
//				+ " from <table>;";
//
//		final SQLSelectParserParser parser = parse(query);
//		runParsertest(query, parser);
//	}

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
		Assert.assertEquals("Table Dictionary is wrong", "{<StudentTable>={<ColumnVariableNoAlias>={substitution={name=<ColumnVariableNoAlias>, type=column}}, <ColumnVariableWithAlias>={substitution={name=<ColumnVariableWithAlias>, type=column}}, normcol=[@3,17:23='normcol',<299>,1:17], noalias=[@1,8:14='noalias',<299>,1:8]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={<StudentTable>={<ColumnVariableNoAlias>={substitution={name=<ColumnVariableNoAlias>, type=column}}, <ColumnVariableWithAlias>={substitution={name=<ColumnVariableWithAlias>, type=column}}, normcol=[@3,17:23='normcol',<299>,1:17], noalias=[@1,8:14='noalias',<299>,1:8]}, interface={<ColumnVariableNoAlias>={column={substitution={name=<ColumnVariableNoAlias>, type=column}, table_ref=studentTable}}, <PredicandVariableNoAlias>={substitution={name=<PredicandVariableNoAlias>, type=predicand}}, predicandAlias={substitution={name=<PredicandVariable>, type=predicand}}, normalias={column={name=normcol, table_ref=null}}, columnAlias={column={substitution={name=<ColumnVariableWithAlias>, type=column}, table_ref=studentTable}}, noalias={column={name=noalias, table_ref=null}}}, studentTable=<StudentTable>}}",
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
		Assert.assertEquals("Table Dictionary is wrong", "{old_table={otherColumn=[@7,33:43='otherColumn',<299>,1:33], newColumn=[@3,12:20='old_table',<299>,1:12], <today>={substitution={name=<today>, type=column}}}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={old_table={newColumn=[@3,12:20='old_table',<299>,1:12], otherColumn=[@7,33:43='otherColumn',<299>,1:33], <today>={substitution={name=<today>, type=column}}}, interface={ex={function={parameters={1={column={name=newColumn, table_ref=old_table}}, 2={column={name=otherColumn, table_ref=null}}, 3={substitution={name=<substitute_me>, type=predicand}}, 4={column={substitution={name=<today>, type=column}, table_ref=old_table}}, 5={literal=128.9}, 6={literal='A'}}, function_name=func}}}}}",
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
		Assert.assertEquals("Table Dictionary is wrong", "{student={normalColumn=[@1,8:19='normalColumn',<299>,1:8]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={student={normalColumn=[@1,8:19='normalColumn',<299>,1:8]}, interface={normalColumn={column={name=normalColumn, table_ref=null}}, notMissing={substitution={name=<notmissing>, type=predicand}}, <missing>={substitution={name=<missing>, type=predicand}}}}}",
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
		Assert.assertEquals("Table Dictionary is wrong", "{student={normalColumn=[@1,8:19='normalColumn',<299>,1:8]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={student={normalColumn=[@1,8:19='normalColumn',<299>,1:8]}, interface={normalColumn={column={name=normalColumn, table_ref=null}}, notMissing={substitution={name=<notmissing>, type=predicand}}, <missing>={substitution={name=<missing>, type=predicand}}}}}",
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
		Assert.assertEquals("Table Dictionary is wrong", "{scbcrse={scbcrse_subj_code=[@23,127:143=\'scbcrse_subj_code\',<299>,1:127], scbcrse_eff_term=[@12,54:69=\'scbcrse_eff_term\',<299>,1:54]}}",
				extractor.getTableColumnMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={scbcrse={scbcrse_subj_code=[@23,127:143='scbcrse_subj_code',<299>,1:127], scbcrse_eff_term=[@12,54:69='scbcrse_eff_term',<299>,1:54]}, interface={subj_code={column={name=scbcrse_subj_code, table_ref=null}}, unnamed_1={function={function_name=MAX, qualifier=null, parameters={column={name=scbcrse_eff_term, table_ref=null}}}}, unnamed_0={function={function_name=COUNT, qualifier=null, parameters=*}}}}}",
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
		// TODO: Hive Lateral Function
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
		// TODO: Null Literal not accepted as a Predicand
		String sql = "null";
		final SQLSelectParserParser parser = parse(sql);
		runPredicandParsertest(sql, parser);
	}

	@Test
	public void concatenationPredicandTest() {
		// TODO: ITEM 24 - Concatenation outside of parentheses not recognized as a predicand
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
		//TODO: Predicand formula beginning with a negative sign does not pass as a predicand
		String sql = "-(aa.scbcrse_coll_code * 6 - other) ";
		final SQLSelectParserParser parser = parse(sql);
		runPredicandParsertest(sql, parser);
	}

	/**
	 * CONDITION TESTS
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
