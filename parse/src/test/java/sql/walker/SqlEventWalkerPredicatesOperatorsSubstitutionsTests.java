package sql.walker;
import org.junit.Assert;
import org.junit.Test;

import sql.SQLSelectParserParser;

public class SqlEventWalkerPredicatesOperatorsSubstitutionsTests extends AbstractSqlParseEventWalkerTest {

	@Test
	public void concatenationInTest() {
		// the concatenated elements as a predicand in an IN statement
		final String query = "SELECT apple"
				+ " from tab1 where subj_cd || crs_nm in (select fld from orange)";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=apple, table_ref=null}}}, from={table={alias=null, table=tab1}}, where={in={item={concatenate={1={column={name=subj_cd, table_ref=null}}, 2={column={name=crs_nm, table_ref=null}}}}, in_list={select={1={column={name=fld, table_ref=null}}}, from={table={alias=null, table=orange}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[apple]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{orange={fld=[[@11,58:60='fld',<381>,1:58]]}, tab1={apple=[[@1,7:11='apple',<381>,1:7]], crs_nm=[[@7,40:45='crs_nm',<381>,1:40]], subj_cd=[[@5,29:35='subj_cd',<381>,1:29]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={fld=[[@11,58:60='fld',<381>,1:58]]}, query2={apple=[[@1,7:11='apple',<381>,1:7]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query2={query_dictionary={apple=[[@1,7:11='apple',<381>,1:7]]}, table_dictionary={tab1={apple=[[@1,7:11='apple',<381>,1:7]], crs_nm=[[@7,40:45='crs_nm',<381>,1:40]], subj_cd=[[@5,29:35='subj_cd',<381>,1:29]]}}, dependent_queries={in_list1={query=query0, type=filters}}, def_query0={query_dictionary={fld=[[@11,58:60='fld',<381>,1:58]]}, table_dictionary={orange={fld=[[@11,58:60='fld',<381>,1:58]]}}, interface={fld=[{name=fld, table_ref=orange}]}}, filters=[{name=subj_cd, table_ref=tab1}, {name=crs_nm, table_ref=tab1}], interface={apple=[{name=apple, table_ref=tab1}]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void descReservedWordTest() {
		final String query = "SELECT apple from tab1 order by apple desc";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=apple, table_ref=null}}}, orderby={1={null_order=null, predicand={column={name=apple, table_ref=null}}, sort_order=desc}}, from={table={alias=null, table=tab1}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[apple]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={apple=[[@1,7:11='apple',<381>,1:7], [@6,32:36='apple',<381>,1:32]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={apple=[[@1,7:11='apple',<381>,1:7]]}}",
						extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={apple=[[@1,7:11='apple',<381>,1:7]]}, table_dictionary={tab1={apple=[[@1,7:11='apple',<381>,1:7], [@6,32:36='apple',<381>,1:32]]}}, ordered_by=[{name=apple, table_ref=tab1}], interface={apple=[{name=apple, table_ref=tab1}]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void queryHasBothDescAsColumnAndReservedWordTest() {
		final String query = "SELECT desc from tab1 order by desc desc";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=desc, table_ref=null}}}, orderby={1={null_order=null, predicand={column={name=desc, table_ref=null}}, sort_order=desc}}, from={table={alias=null, table=tab1}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[desc]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={desc=[[@1,7:10='desc',<77>,1:7], [@6,31:34='desc',<77>,1:31]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={desc=[[@1,7:10='desc',<77>,1:7]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={desc=[[@1,7:10='desc',<77>,1:7]]}, table_dictionary={tab1={desc=[[@1,7:10='desc',<77>,1:7], [@6,31:34='desc',<77>,1:31]]}}, ordered_by=[{name=desc, table_ref=tab1}], interface={desc=[{name=desc, table_ref=tab1}]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void ascReservedWordTest() {
		final String query = "SELECT apple from tab1 order by apple asc";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=apple, table_ref=null}}}, orderby={1={null_order=null, predicand={column={name=apple, table_ref=null}}, sort_order=asc}}, from={table={alias=null, table=tab1}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[apple]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={apple=[[@1,7:11='apple',<381>,1:7], [@6,32:36='apple',<381>,1:32]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={apple=[[@1,7:11='apple',<381>,1:7]]}}",
						extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={apple=[[@1,7:11='apple',<381>,1:7]]}, table_dictionary={tab1={apple=[[@1,7:11='apple',<381>,1:7], [@6,32:36='apple',<381>,1:32]]}}, ordered_by=[{name=apple, table_ref=tab1}], interface={apple=[{name=apple, table_ref=tab1}]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void queryHasBothAscAsColumnAndReservedWordTest() {
		final String query = "SELECT asc from tab1 order by asc asc";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=asc, table_ref=null}}}, orderby={1={null_order=null, predicand={column={name=asc, table_ref=null}}, sort_order=asc}}, from={table={alias=null, table=tab1}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[asc]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={asc=[[@1,7:9='asc',<60>,1:7], [@6,30:32='asc',<60>,1:30]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={asc=[[@1,7:9='asc',<60>,1:7]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={asc=[[@1,7:9='asc',<60>,1:7]]}, table_dictionary={tab1={asc=[[@1,7:9='asc',<60>,1:7], [@6,30:32='asc',<60>,1:30]]}}, ordered_by=[{name=asc, table_ref=tab1}], interface={asc=[{name=asc, table_ref=tab1}]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void whereConditionWithNegationTest() {
		final String query = "SELECT apple from tab1 where not true";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=apple, table_ref=null}}}, from={table={alias=null, table=tab1}}, where={not={literal=true}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[apple]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={apple=[[@1,7:11='apple',<381>,1:7]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={apple=[[@1,7:11='apple',<381>,1:7]]}}",
						extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={apple=[[@1,7:11='apple',<381>,1:7]]}, table_dictionary={tab1={apple=[[@1,7:11='apple',<381>,1:7]]}}, filters=[], interface={apple=[{name=apple, table_ref=tab1}]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void whereConditionWithSingleConditionVariableTest() {
		// Item 43 - Where with single predicand variable does not recognize it as a variable or set its type
		final String query = "SELECT apple from tab1 where <subject code>";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=apple, table_ref=null}}}, from={table={alias=null, table=tab1}}, where={substitution={name=<subject code>, type=condition}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[apple]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<subject code>=condition}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={apple=[[@1,7:11='apple',<381>,1:7]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={apple=[[@1,7:11='apple',<381>,1:7]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={apple=[[@1,7:11='apple',<381>,1:7]]}, table_dictionary={tab1={apple=[[@1,7:11='apple',<381>,1:7]]}}, filters=[], interface={apple=[{name=apple, table_ref=tab1}]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void whereConditionWithSingleColumnVariableTest() {
		final String query = "SELECT apple from tab1 where tab1.<subject code>";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=apple, table_ref=null}}}, from={table={alias=null, table=tab1}}, where={column={substitution={name=<subject code>, type=column}, table_ref=tab1}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[apple]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<subject code>=column}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={apple=[[@1,7:11='apple',<381>,1:7]], <subject code>=[[@5,29:32='tab1',<381>,1:29]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={apple=[[@1,7:11='apple',<381>,1:7]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={apple=[[@1,7:11='apple',<381>,1:7]]}, table_dictionary={tab1={apple=[[@1,7:11='apple',<381>,1:7]], <subject code>=[[@5,29:32='tab1',<381>,1:29]]}}, filters=[{substitution={name=<subject code>, type=column}, table_ref=tab1}], interface={apple=[{name=apple, table_ref=tab1}]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void whereConditionComparingPredicandVariablesTest() {
		final String query = "SELECT apple from tab1 where <subject code> = <other subject code>";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=apple, table_ref=null}}}, from={table={alias=null, table=tab1}}, where={condition={left={substitution={name=<subject code>, type=predicand}}, right={substitution={name=<other subject code>, type=predicand}}, operator==}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[apple]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<other subject code>=predicand, <subject code>=predicand}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={apple=[[@1,7:11='apple',<381>,1:7]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={apple=[[@1,7:11='apple',<381>,1:7]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={apple=[[@1,7:11='apple',<381>,1:7]]}, table_dictionary={tab1={apple=[[@1,7:11='apple',<381>,1:7]]}}, filters=[{name=<subject code>, type=predicand}, {name=<other subject code>, type=predicand}], interface={apple=[{name=apple, table_ref=tab1}]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void whereConditionComparingPredicandVariableToNullTest() {
		// Item 48 - Predicand Variable in an IS NULL condition is not recognized
		final String query = "SELECT apple from tab1 where <subject code> is null";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=apple, table_ref=null}}}, from={table={alias=null, table=tab1}}, where={condition={left={substitution={name=<subject code>, type=predicand}}, operator=is null}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[apple]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<subject code>=predicand}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={apple=[[@1,7:11='apple',<381>,1:7]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={apple=[[@1,7:11='apple',<381>,1:7]]}}",
						extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={apple=[[@1,7:11='apple',<381>,1:7]]}, table_dictionary={tab1={apple=[[@1,7:11='apple',<381>,1:7]]}}, filters=[{name=<subject code>, type=predicand}], interface={apple=[{name=apple, table_ref=tab1}]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void whereConditionComparingPredicandVariableToNotNullTest() {
		// Item 48 - Predicand Variable in an IS NULL condition is not recognized
		final String query = "SELECT apple from tab1 where <subject code> is not null";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=apple, table_ref=null}}}, from={table={alias=null, table=tab1}}, where={condition={left={substitution={name=<subject code>, type=predicand}}, operator=is not null}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[apple]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<subject code>=predicand}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={apple=[[@1,7:11='apple',<381>,1:7]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={apple=[[@1,7:11='apple',<381>,1:7]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={apple=[[@1,7:11='apple',<381>,1:7]]}, table_dictionary={tab1={apple=[[@1,7:11='apple',<381>,1:7]]}}, filters=[{name=<subject code>, type=predicand}], interface={apple=[{name=apple, table_ref=tab1}]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void whereMultipleConditionComparingPredicandVariableToNullTest() {
		// A condition and a predicand Variable connected by and in an IS NULL condition is not recognized
		final String query = "SELECT apple from tab1 where <first condition> and <subject code> is null";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=apple, table_ref=null}}}, from={table={alias=null, table=tab1}}, where={and={1={substitution={name=<first condition>, type=condition}}, 2={condition={left={substitution={name=<subject code>, type=predicand}}, operator=is null}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[apple]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<subject code>=predicand, <first condition>=condition}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={apple=[[@1,7:11='apple',<381>,1:7]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={apple=[[@1,7:11='apple',<381>,1:7]]}}",
						extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={apple=[[@1,7:11='apple',<381>,1:7]]}, table_dictionary={tab1={apple=[[@1,7:11='apple',<381>,1:7]]}}, filters=[{name=<subject code>, type=predicand}], interface={apple=[{name=apple, table_ref=tab1}]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void whereIsTrueTest() {
		final String query = "SELECT apple from tab1 where subj is true";
		//2={condition={left={substitution={name=<subject code>, type=predicand}}, operator=is true}}

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=apple, table_ref=null}}}, from={table={alias=null, table=tab1}}, where={condition={left={column={name=subj, table_ref=null}}}, operator=is true}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[apple]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={apple=[[@1,7:11='apple',<381>,1:7]], subj=[[@5,29:32='subj',<381>,1:29]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={apple=[[@1,7:11='apple',<381>,1:7]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={apple=[[@1,7:11='apple',<381>,1:7]]}, table_dictionary={tab1={apple=[[@1,7:11='apple',<381>,1:7]], subj=[[@5,29:32='subj',<381>,1:29]]}}, filters=[{name=subj, table_ref=tab1}], interface={apple=[{name=apple, table_ref=tab1}]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void whereIsNotTrueTest() {
		final String query = "SELECT apple from tab1 where subj is not true";
		//2={condition={left={substitution={name=<subject code>, type=predicand}}, operator=is not true}}

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=apple, table_ref=null}}}, from={table={alias=null, table=tab1}}, where={condition={left={column={name=subj, table_ref=null}}}, operator=is not true}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[apple]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={apple=[[@1,7:11='apple',<381>,1:7]], subj=[[@5,29:32='subj',<381>,1:29]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={apple=[[@1,7:11='apple',<381>,1:7]]}}",
						extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={apple=[[@1,7:11='apple',<381>,1:7]]}, table_dictionary={tab1={apple=[[@1,7:11='apple',<381>,1:7]], subj=[[@5,29:32='subj',<381>,1:29]]}}, filters=[{name=subj, table_ref=tab1}], interface={apple=[{name=apple, table_ref=tab1}]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void whereConditionComparingPredicandVariableToIsTrueTest() {
		// Item 49 - Predicand Variable in an IS TRUE condition is not recognized
		final String query = "SELECT apple from tab1 where <subject code> is true";
		//{condition={left={substitution={name=<subject code>, type=predicand}}, operator=is true}}

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=apple, table_ref=null}}}, from={table={alias=null, table=tab1}}, where={condition={left={substitution={name=<subject code>, type=predicand}}}, operator=is true}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[apple]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<subject code>=predicand}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={apple=[[@1,7:11='apple',<381>,1:7]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={apple=[[@1,7:11='apple',<381>,1:7]]}}",
						extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={apple=[[@1,7:11='apple',<381>,1:7]]}, table_dictionary={tab1={apple=[[@1,7:11='apple',<381>,1:7]]}}, filters=[{name=<subject code>, type=predicand}], interface={apple=[{name=apple, table_ref=tab1}]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void whereConditionWithAndPredicandTest() {
		final String query = "SELECT apple from tab1 where <subject code> and true";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=apple, table_ref=null}}}, from={table={alias=null, table=tab1}}, where={and={1={substitution={name=<subject code>, type=condition}}, 2={literal=true}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[apple]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<subject code>=condition}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={apple=[[@1,7:11='apple',<381>,1:7]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={apple=[[@1,7:11='apple',<381>,1:7]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={apple=[[@1,7:11='apple',<381>,1:7]]}, table_dictionary={tab1={apple=[[@1,7:11='apple',<381>,1:7]]}}, filters=[], interface={apple=[{name=apple, table_ref=tab1}]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void whereConditionWithOrPredicandTest() {
		final String query = "SELECT apple from tab1 where <subject code> or true";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=apple, table_ref=null}}}, from={table={alias=null, table=tab1}}, where={or={1={substitution={name=<subject code>, type=condition}}, 2={literal=true}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[apple]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<subject code>=condition}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={apple=[[@1,7:11='apple',<381>,1:7]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={apple=[[@1,7:11='apple',<381>,1:7]]}}",
						extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={apple=[[@1,7:11='apple',<381>,1:7]]}, table_dictionary={tab1={apple=[[@1,7:11='apple',<381>,1:7]]}}, filters=[], interface={apple=[{name=apple, table_ref=tab1}]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void whereConditionWithOrPredicandVariablesTest() {
		final String query = "SELECT apple from tab1 where <subject code> or (<other>)";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=apple, table_ref=null}}}, from={table={alias=null, table=tab1}}, where={or={1={substitution={name=<subject code>, type=condition}}, 2={parentheses={substitution={name=<other>, type=condition}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[apple]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<subject code>=condition, <other>=condition}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={apple=[[@1,7:11='apple',<381>,1:7]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={apple=[[@1,7:11='apple',<381>,1:7]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={apple=[[@1,7:11='apple',<381>,1:7]]}, table_dictionary={tab1={apple=[[@1,7:11='apple',<381>,1:7]]}}, filters=[], interface={apple=[{name=apple, table_ref=tab1}]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void whereConditionWithParentheticalConditionVariableTest() {
		// Item 44 - does not recognize condition variable
		final String query = "SELECT apple from tab1 where (<subject code>)";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=apple, table_ref=null}}}, from={table={alias=null, table=tab1}}, where={parentheses={substitution={name=<subject code>, type=condition}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[apple]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<subject code>=condition}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={apple=[[@1,7:11='apple',<381>,1:7]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={apple=[[@1,7:11='apple',<381>,1:7]]}}",
						extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={apple=[[@1,7:11='apple',<381>,1:7]]}, table_dictionary={tab1={apple=[[@1,7:11='apple',<381>,1:7]]}}, filters=[], interface={apple=[{name=apple, table_ref=tab1}]}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void whereComparisonPredicandSameAsSelectTest() {
		final String query = "SELECT apple FROM tab1 WHERE <a> >= <b>";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=apple, table_ref=null}}}, from={table={alias=null, table=tab1}}, where={condition={left={substitution={name=<a>, type=predicand}}, right={substitution={name=<b>, type=predicand}}, operator=>=}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Substitution List is wrong", "{<b>=predicand, <a>=predicand}",
				extractor.getSubstitutionsMap().toString());
	}

	@Test
	public void filterArithmeticSubtractionComparisonPredicandTest() {
		final String query = "SELECT apple FROM tab1 WHERE <a> - 20 >= 50";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=apple, table_ref=null}}}, from={table={alias=null, table=tab1}}, where={condition={left={calc={left={substitution={name=<a>, type=predicand}}, right={literal=20}, operator=-}}, right={literal=50}, operator=>=}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Substitution List is wrong", "{<a>=predicand}",
				extractor.getSubstitutionsMap().toString());
	}

	@Test
	public void filterArithmeticDivisionComparisonPredicandTest() {
		final String query = "SELECT apple FROM tab1 WHERE ((<a>) / (<b>)) >= 1";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=apple, table_ref=null}}}, from={table={alias=null, table=tab1}}, where={condition={left={parentheses={calc={left={parentheses={substitution={name=<a>, type=predicand}}}, right={parentheses={substitution={name=<b>, type=predicand}}}, operator=/}}}, right={literal=1}, operator=>=}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Substitution List is wrong", "{<b>=predicand, <a>=predicand}",
				extractor.getSubstitutionsMap().toString());
	}

	@Test
	public void groupByArithmeticPredicandSubstitutionTest() {
		final String query = "SELECT apple FROM tab1 GROUP BY <a> - <b>";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=apple, table_ref=null}}}, from={table={alias=null, table=tab1}}, groupby={1={calc={left={substitution={name=<a>, type=predicand}}, right={substitution={name=<b>, type=predicand}}, operator=-}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Substitution List is wrong", "{<b>=predicand, <a>=predicand}",
				extractor.getSubstitutionsMap().toString());
	}

	@Test
	public void orderByArithmeticPredicandSubstitutionTest() {
		final String query = "SELECT apple FROM tab1 ORDER BY (<a>) + (<b>)";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=apple, table_ref=null}}}, orderby={1={null_order=null, predicand={calc={left={parentheses={substitution={name=<a>, type=predicand}}}, right={parentheses={substitution={name=<b>, type=predicand}}}, operator=+}}, sort_order=ASC}}, from={table={alias=null, table=tab1}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Substitution List is wrong", "{<b>=predicand, <a>=predicand}",
				extractor.getSubstitutionsMap().toString());
	}

	@Test
	public void orderByParenthesizedPredicandSubstitutionTest() {
		// Regression: ORDER BY (<a>) parsed via predicand_subquery was mis-typed as query (not predicand).
		final String query = "SELECT apple FROM tab1 ORDER BY (<a>)";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=apple, table_ref=null}}}, orderby={1={null_order=null, predicand={substitution={name=<a>, type=predicand}}, sort_order=ASC}}, from={table={alias=null, table=tab1}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Substitution List is wrong", "{<a>=predicand}",
				extractor.getSubstitutionsMap().toString());
	}

	@Test
	public void havingArithmeticSubtractionComparisonPredicandTest() {
		final String query = "SELECT apple FROM tab1 GROUP BY apple HAVING <a> - 20 >= 50";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=apple, table_ref=null}}}, having={condition={left={calc={left={substitution={name=<a>, type=predicand}}, right={literal=20}, operator=-}}, right={literal=50}, operator=>=}}, from={table={alias=null, table=tab1}}, groupby={1={column={name=apple, table_ref=null}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Substitution List is wrong", "{<a>=predicand}",
				extractor.getSubstitutionsMap().toString());
	}

	@Test
	public void qualifyArithmeticSubtractionComparisonPredicandTest() {
		final String query = "SELECT apple FROM tab1 QUALIFY ((<a>) - 20) >= 50";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=apple, table_ref=null}}}, from={table={alias=null, table=tab1}}, qualify={condition={left={parentheses={calc={left={parentheses={substitution={name=<a>, type=predicand}}}, right={literal=20}, operator=-}}}, right={literal=50}, operator=>=}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Substitution List is wrong", "{<a>=predicand}",
				extractor.getSubstitutionsMap().toString());
	}

	@Test
	public void havingParenthesizedConditionSubstitutionTest() {
		final String query = "SELECT apple FROM tab1 GROUP BY apple HAVING (<subject code>)";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=apple, table_ref=null}}}, having={parentheses={substitution={name=<subject code>, type=condition}}}, from={table={alias=null, table=tab1}}, groupby={1={column={name=apple, table_ref=null}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Substitution List is wrong", "{<subject code>=condition}",
				extractor.getSubstitutionsMap().toString());
	}

	@Test
	public void qualifyParenthesizedConditionSubstitutionTest() {
		final String query = "SELECT apple FROM tab1 QUALIFY (<subject code>)";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=apple, table_ref=null}}}, from={table={alias=null, table=tab1}}, qualify={parentheses={substitution={name=<subject code>, type=condition}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Substitution List is wrong", "{<subject code>=condition}",
				extractor.getSubstitutionsMap().toString());
	}

	@Test
	public void havingBareConditionSubstitutionTest() {
		final String query = "SELECT apple FROM tab1 GROUP BY apple HAVING <subject code>";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=apple, table_ref=null}}}, having={substitution={name=<subject code>, type=condition}}, from={table={alias=null, table=tab1}}, groupby={1={column={name=apple, table_ref=null}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Substitution List is wrong", "{<subject code>=condition}",
				extractor.getSubstitutionsMap().toString());
	}

	@Test
	public void qualifyBareConditionSubstitutionTest() {
		final String query = "SELECT apple FROM tab1 QUALIFY <subject code>";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=apple, table_ref=null}}}, from={table={alias=null, table=tab1}}, qualify={substitution={name=<subject code>, type=condition}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Substitution List is wrong", "{<subject code>=condition}",
				extractor.getSubstitutionsMap().toString());
	}

	@Test
	public void groupByParenthesizedPredicandSubstitutionTest() {
		// Regression: GROUP BY (<a>) parsed via predicand_subquery was mis-typed as query (not predicand).
		final String query = "SELECT apple FROM tab1 GROUP BY (<a>)";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=apple, table_ref=null}}}, from={table={alias=null, table=tab1}}, groupby={1={substitution={name=<a>, type=predicand}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Substitution List is wrong", "{<a>=predicand}",
				extractor.getSubstitutionsMap().toString());
	}


	@Test
	public void whereConditionWithParentheticalConditionVariableInOrTest() {
		// Item 45 - Thinks the condition variable is a query variable
		final String query = "SELECT apple from tab1 where (<subject code>) or true";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=apple, table_ref=null}}}, from={table={alias=null, table=tab1}}, where={or={1={parentheses={substitution={name=<subject code>, type=condition}}}, 2={literal=true}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[apple]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<subject code>=condition}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={apple=[[@1,7:11='apple',<381>,1:7]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={apple=[[@1,7:11='apple',<381>,1:7]]}}",
						extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={apple=[[@1,7:11='apple',<381>,1:7]]}, table_dictionary={tab1={apple=[[@1,7:11='apple',<381>,1:7]]}}, filters=[], interface={apple=[{name=apple, table_ref=tab1}]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void whereConditionWithMixedConditionAndPredicandVariablesTest() {
		// Where with both condition variable and predicand variable in a comparison
		final String query = "SELECT apple from tab1 where <subject code_condition> and <subject_code_predicand> = banana";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=apple, table_ref=null}}}, from={table={alias=null, table=tab1}}, where={and={1={substitution={name=<subject code_condition>, type=condition}}, 2={condition={left={substitution={name=<subject_code_predicand>, type=predicand}}, right={column={name=banana, table_ref=null}}, operator==}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[apple]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<subject code_condition>=condition, <subject_code_predicand>=predicand}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={banana=[[@9,85:90='banana',<381>,1:85]], apple=[[@1,7:11='apple',<381>,1:7]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={apple=[[@1,7:11='apple',<381>,1:7]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={apple=[[@1,7:11='apple',<381>,1:7]]}, table_dictionary={tab1={banana=[[@9,85:90='banana',<381>,1:85]], apple=[[@1,7:11='apple',<381>,1:7]]}}, filters=[{name=<subject_code_predicand>, type=predicand}, {name=banana, table_ref=tab1}], interface={apple=[{name=apple, table_ref=tab1}]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void simpleOrderByTest() {
		final String query = "SELECT * from tab1 order by col1 ";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=*, table_ref=*}}}, orderby={1={null_order=null, predicand={column={name=col1, table_ref=null}}, sort_order=ASC}}, from={table={alias=null, table=tab1}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={*=[[@1,7:7='*',<291>,1:7]], col1=[[@6,28:31='col1',<381>,1:28]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@1,7:7='*',<291>,1:7]]}}",
						extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={*=[[@1,7:7='*',<291>,1:7]]}, table_dictionary={tab1={*=[[@1,7:7='*',<291>,1:7]], col1=[[@6,28:31='col1',<381>,1:28]]}}, ordered_by=[{name=col1, table_ref=null}], interface={*=[{name=*, table_ref=*}]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void directionAscOrderByTest() {
		final String query = "SELECT * from tab1 order by col1 ASC";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=*, table_ref=*}}}, orderby={1={null_order=null, predicand={column={name=col1, table_ref=null}}, sort_order=ASC}}, from={table={alias=null, table=tab1}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={*=[[@1,7:7='*',<291>,1:7]], col1=[[@6,28:31='col1',<381>,1:28]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@1,7:7='*',<291>,1:7]]}}",
						extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={*=[[@1,7:7='*',<291>,1:7]]}, table_dictionary={tab1={*=[[@1,7:7='*',<291>,1:7]], col1=[[@6,28:31='col1',<381>,1:28]]}}, ordered_by=[{name=col1, table_ref=null}], interface={*=[{name=*, table_ref=*}]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void directionDescOrderByTest() {
		final String query = "SELECT * from tab1 order by col1 DESC";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=*, table_ref=*}}}, orderby={1={null_order=null, predicand={column={name=col1, table_ref=null}}, sort_order=DESC}}, from={table={alias=null, table=tab1}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={*=[[@1,7:7='*',<291>,1:7]], col1=[[@6,28:31='col1',<381>,1:28]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@1,7:7='*',<291>,1:7]]}}",
						extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={*=[[@1,7:7='*',<291>,1:7]]}, table_dictionary={tab1={*=[[@1,7:7='*',<291>,1:7]], col1=[[@6,28:31='col1',<381>,1:28]]}}, ordered_by=[{name=col1, table_ref=null}], interface={*=[{name=*, table_ref=*}]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void directionAscWithNullsDecoratorOrderByTest() {
		final String query = "SELECT * from tab1 order by col1 ASC nulls last";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=*, table_ref=*}}}, orderby={1={null_order=last, predicand={column={name=col1, table_ref=null}}, sort_order=ASC}}, from={table={alias=null, table=tab1}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={*=[[@1,7:7='*',<291>,1:7]], col1=[[@6,28:31='col1',<381>,1:28]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@1,7:7='*',<291>,1:7]]}}",
						extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={*=[[@1,7:7='*',<291>,1:7]]}, table_dictionary={tab1={*=[[@1,7:7='*',<291>,1:7]], col1=[[@6,28:31='col1',<381>,1:28]]}}, ordered_by=[{name=col1, table_ref=null}], interface={*=[{name=*, table_ref=*}]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void multipleColumnsWithNullsDecoratorsOrderByTest() {
		final String query = "SELECT * from tab1 order by col1 ASC nulls last, col2 desc nulls first";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=*, table_ref=*}}}, orderby={1={null_order=last, predicand={column={name=col1, table_ref=null}}, sort_order=ASC}, 2={null_order=first, predicand={column={name=col2, table_ref=null}}, sort_order=desc}}, from={table={alias=null, table=tab1}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={*=[[@1,7:7='*',<291>,1:7]], col2=[[@11,49:52='col2',<381>,1:49]], col1=[[@6,28:31='col1',<381>,1:28]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@1,7:7='*',<291>,1:7]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={*=[[@1,7:7='*',<291>,1:7]]}, table_dictionary={tab1={*=[[@1,7:7='*',<291>,1:7]], col2=[[@11,49:52='col2',<381>,1:49]], col1=[[@6,28:31='col1',<381>,1:28]]}}, ordered_by=[{name=col1, table_ref=null}, {name=col2, table_ref=null}], interface={*=[{name=*, table_ref=*}]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void selectOrderByNullsLastStatementTest() {
	// Item 100 - Order by accepts null operations
		final String query = " Select * from dual"
			+ " order by 1 nulls last";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
	
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=*, table_ref=*}}}, orderby={1={null_order=last, predicand={literal=1}, sort_order=ASC}}, from={table={alias=null, table=dual}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
			extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{dual={*=[[@1,8:8='*',<291>,1:8]]}}",
			extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@1,8:8='*',<291>,1:8]]}}",
			extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={*=[[@1,8:8='*',<291>,1:8]]}, table_dictionary={dual={*=[[@1,8:8='*',<291>,1:8]]}}, ordered_by=[], interface={*=[{name=*, table_ref=*}]}}}",
			extractor.getSymbolTable().toString());
	}


	@Test
	public void selectOrderByVariableNullsLastStatementTest() {
	// Item 100 - Order by accepts null operations
		final String query = " Select * from dual"
			+ " order by <var1> nulls last";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
	
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=*, table_ref=*}}}, orderby={1={null_order=last, predicand={substitution={name=<var1>, type=predicand}}, sort_order=ASC}}, from={table={alias=null, table=dual}}}}",
			extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]", 
			extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<var1>=predicand}", 
			extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{dual={*=[[@1,8:8='*',<291>,1:8]]}}",
			extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@1,8:8='*',<291>,1:8]]}}",
			extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={*=[[@1,8:8='*',<291>,1:8]]}, table_dictionary={dual={*=[[@1,8:8='*',<291>,1:8]]}}, ordered_by=[{name=<var1>, type=predicand}], interface={*=[{name=*, table_ref=*}]}}}",
			extractor.getSymbolTable().toString());	
	}


	@Test
	public void simpleLimitTest() {
		final String query = "SELECT * from tab1 limit 100";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=*, table_ref=*}}}, limit={literal=100}, from={table={alias=null, table=tab1}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={*=[[@1,7:7='*',<291>,1:7]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@1,7:7='*',<291>,1:7]]}}",
			extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={*=[[@1,7:7='*',<291>,1:7]]}, table_dictionary={tab1={*=[[@1,7:7='*',<291>,1:7]]}}, interface={*=[{name=*, table_ref=*}]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void simpleLimitAndOffsetTest() {
		final String query = "SELECT * from tab1 limit 100 offset 300";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=*, table_ref=*}}}, limit={offset=300, literal=100}, from={table={alias=null, table=tab1}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={*=[[@1,7:7='*',<291>,1:7]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@1,7:7='*',<291>,1:7]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={*=[[@1,7:7='*',<291>,1:7]]}, table_dictionary={tab1={*=[[@1,7:7='*',<291>,1:7]]}}, interface={*=[{name=*, table_ref=*}]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void basicBetweenTest() {
		// Item 19 - finish between statement
		final String query = "SELECT apple from tab1 where a between c and d";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=apple, table_ref=null}}}, from={table={alias=null, table=tab1}}, where={between={item={column={name=a, table_ref=null}}, symmetry=null, end={column={name=d, table_ref=null}}, begin={column={name=c, table_ref=null}}, operator=between}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[apple]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={apple=[[@1,7:11='apple',<381>,1:7]], a=[[@5,29:29='a',<381>,1:29]], c=[[@7,39:39='c',<381>,1:39]], d=[[@9,45:45='d',<381>,1:45]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={apple=[[@1,7:11='apple',<381>,1:7]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={apple=[[@1,7:11='apple',<381>,1:7]]}, table_dictionary={tab1={apple=[[@1,7:11='apple',<381>,1:7]], a=[[@5,29:29='a',<381>,1:29]], c=[[@7,39:39='c',<381>,1:39]], d=[[@9,45:45='d',<381>,1:45]]}}, filters=[{name=a, table_ref=tab1}, {name=d, table_ref=tab1}, {name=c, table_ref=tab1}], interface={apple=[{name=apple, table_ref=tab1}]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void basicBetweenTestWithSymmetry() {
		// Item 19 - finish between statement
		final String query = "SELECT apple from tab1 where a between symmetric c and d";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=apple, table_ref=null}}}, from={table={alias=null, table=tab1}}, where={between={item={column={name=a, table_ref=null}}, symmetry=symmetric, end={column={name=d, table_ref=null}}, begin={column={name=c, table_ref=null}}, operator=between}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[apple]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={apple=[[@1,7:11='apple',<381>,1:7]], a=[[@5,29:29='a',<381>,1:29]], c=[[@8,49:49='c',<381>,1:49]], d=[[@10,55:55='d',<381>,1:55]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={apple=[[@1,7:11='apple',<381>,1:7]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={apple=[[@1,7:11='apple',<381>,1:7]]}, table_dictionary={tab1={apple=[[@1,7:11='apple',<381>,1:7]], a=[[@5,29:29='a',<381>,1:29]], c=[[@8,49:49='c',<381>,1:49]], d=[[@10,55:55='d',<381>,1:55]]}}, filters=[{name=a, table_ref=tab1}, {name=d, table_ref=tab1}, {name=c, table_ref=tab1}], interface={apple=[{name=apple, table_ref=tab1}]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void basicNotBetweenTest() {
		// Item 19 - finish between statement
		final String query = "SELECT apple from tab1 where a not between c and d";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=apple, table_ref=null}}}, from={table={alias=null, table=tab1}}, where={between={item={column={name=a, table_ref=null}}, symmetry=null, end={column={name=d, table_ref=null}}, begin={column={name=c, table_ref=null}}, operator=not between}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[apple]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={apple=[[@1,7:11='apple',<381>,1:7]], a=[[@5,29:29='a',<381>,1:29]], c=[[@8,43:43='c',<381>,1:43]], d=[[@10,49:49='d',<381>,1:49]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={apple=[[@1,7:11='apple',<381>,1:7]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={apple=[[@1,7:11='apple',<381>,1:7]]}, table_dictionary={tab1={apple=[[@1,7:11='apple',<381>,1:7]], a=[[@5,29:29='a',<381>,1:29]], c=[[@8,43:43='c',<381>,1:43]], d=[[@10,49:49='d',<381>,1:49]]}}, filters=[{name=a, table_ref=tab1}, {name=d, table_ref=tab1}, {name=c, table_ref=tab1}], interface={apple=[{name=apple, table_ref=tab1}]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void basicNotBetweenTestWithSymmetry() {
		// Item 19 - finish between statement
		final String query = "SELECT apple from tab1 where a not between symmetric c and d";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=apple, table_ref=null}}}, from={table={alias=null, table=tab1}}, where={between={item={column={name=a, table_ref=null}}, symmetry=symmetric, end={column={name=d, table_ref=null}}, begin={column={name=c, table_ref=null}}, operator=not between}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[apple]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={apple=[[@1,7:11='apple',<381>,1:7]], a=[[@5,29:29='a',<381>,1:29]], c=[[@9,53:53='c',<381>,1:53]], d=[[@11,59:59='d',<381>,1:59]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={apple=[[@1,7:11='apple',<381>,1:7]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={apple=[[@1,7:11='apple',<381>,1:7]]}, table_dictionary={tab1={apple=[[@1,7:11='apple',<381>,1:7]], a=[[@5,29:29='a',<381>,1:29]], c=[[@9,53:53='c',<381>,1:53]], d=[[@11,59:59='d',<381>,1:59]]}}, filters=[{name=a, table_ref=tab1}, {name=d, table_ref=tab1}, {name=c, table_ref=tab1}], interface={apple=[{name=apple, table_ref=tab1}]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void predicandAndColumnVariableNotBetweenTestWithSymmetry() {
		// Item 19 - finish between statement
		final String query = "SELECT apple from tab1 where <a> not between symmetric tab1.<c> and d";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=apple, table_ref=null}}}, from={table={alias=null, table=tab1}}, where={between={item={substitution={name=<a>, type=predicand}}, symmetry=symmetric, end={column={name=d, table_ref=null}}, begin={column={substitution={name=<c>, type=column}, table_ref=tab1}}, operator=not between}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[apple]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<c>=column, <a>=predicand}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={apple=[[@1,7:11='apple',<381>,1:7]], d=[[@13,68:68='d',<381>,1:68]], <c>=[[@9,55:58='tab1',<381>,1:55]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={apple=[[@1,7:11='apple',<381>,1:7]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={apple=[[@1,7:11='apple',<381>,1:7]]}, table_dictionary={tab1={apple=[[@1,7:11='apple',<381>,1:7]], d=[[@13,68:68='d',<381>,1:68]], <c>=[[@9,55:58='tab1',<381>,1:55]]}}, filters=[{name=<a>, type=predicand}, {name=d, table_ref=tab1}, {substitution={name=<c>, type=column}, table_ref=tab1}], interface={apple=[{name=apple, table_ref=tab1}]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void stringFunctionWithInStatementParseTest() {
		final String query = "SELECT trim(leading '0' from field1), a || b, " + " trim('0' || field2,'0') "
				+ " FROM scbcrse aa " + " WHERE subj_code in ('AA', 'BB') ";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={function={function_name=trim, parameters={qualifier=leading, trim_character={literal='0'}, value={column={name=field1, table_ref=null}}}}}, 2={concatenate={1={column={name=a, table_ref=null}}, 2={column={name=b, table_ref=null}}}}, 3={function={parameters={1={concatenate={1={literal='0'}, 2={column={name=field2, table_ref=null}}}}, 2={literal='0'}}, function_name=trim}}}, from={table={alias=aa, table=scbcrse}}, where={in={item={column={name=subj_code, table_ref=null}}, in_list={list={1={literal='AA'}, 2={literal='BB'}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[unnamed_1, unnamed_2, unnamed_0]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{scbcrse={a=[[@9,38:38='a',<381>,1:38]], b=[[@11,43:43='b',<381>,1:43]], field1=[[@6,29:34='field1',<381>,1:29]], subj_code=[[@25,95:103='subj_code',<381>,1:95]], field2=[[@17,59:64='field2',<381>,1:59]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={unnamed_1=[[@11,43:43='b',<381>,1:43]], unnamed_2=[[@20,69:69=')',<288>,1:69]], unnamed_0=[[@7,35:35=')',<288>,1:35]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={unnamed_1=[[@11,43:43='b',<381>,1:43]], unnamed_2=[[@20,69:69=')',<288>,1:69]], unnamed_0=[[@7,35:35=')',<288>,1:35]]}, table_dictionary={scbcrse={a=[[@9,38:38='a',<381>,1:38]], b=[[@11,43:43='b',<381>,1:43]], field1=[[@6,29:34='field1',<381>,1:29]], subj_code=[[@25,95:103='subj_code',<381>,1:95]], field2=[[@17,59:64='field2',<381>,1:59]]}}, filters=[{name=subj_code, table_ref=scbcrse}], interface={unnamed_1=[{name=a, table_ref=scbcrse}, {name=b, table_ref=scbcrse}], unnamed_2=[{name=field2, table_ref=scbcrse}], unnamed_0=[{name=field1, table_ref=scbcrse}]}, table_alias={aa=scbcrse}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void inPredicateSubqueryTest() {
		final String query = "SELECT * FROM scbcrse aa  WHERE subj_code in ('AA', 'BB') "
				+ " and item in (select * from other)";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=*, table_ref=*}}}, from={table={alias=aa, table=scbcrse}}, where={and={1={in={item={column={name=subj_code, table_ref=null}}, in_list={list={1={literal='AA'}, 2={literal='BB'}}}}}, 2={in={item={column={name=item, table_ref=null}}, in_list={select={1={column={name=*, table_ref=*}}}, from={table={alias=null, table=other}}}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{other={*=[[@18,79:79='*',<291>,1:79]]}, scbcrse={item=[[@14,63:66='item',<381>,1:63]], subj_code=[[@6,32:40='subj_code',<381>,1:32]], *=[[@1,7:7='*',<291>,1:7]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@18,79:79='*',<291>,1:79]]}, query2={*=[[@1,7:7='*',<291>,1:7]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query2={query_dictionary={*=[[@1,7:7='*',<291>,1:7]]}, table_dictionary={scbcrse={item=[[@14,63:66='item',<381>,1:63]], subj_code=[[@6,32:40='subj_code',<381>,1:32]], *=[[@1,7:7='*',<291>,1:7]]}}, dependent_queries={in_list1={query=query0, type=filters}}, def_query0={query_dictionary={*=[[@18,79:79='*',<291>,1:79]]}, table_dictionary={other={*=[[@18,79:79='*',<291>,1:79]]}}, interface={*=[{name=*, table_ref=*}]}}, filters=[{name=subj_code, table_ref=scbcrse}, {name=item, table_ref=scbcrse}], interface={*=[{name=*, table_ref=*}]}, table_alias={aa=scbcrse}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void inPredicateInListSubqueryVariableTest() {
		final String query = "SELECT * FROM scbcrse aa WHERE subj_code in ('AA', 'BB') "
				+ " and item in (<inlist subquery>)";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=*, table_ref=*}}}, from={table={alias=aa, table=scbcrse}}, where={and={1={in={item={column={name=subj_code, table_ref=null}}, in_list={list={1={literal='AA'}, 2={literal='BB'}}}}}, 2={in={item={column={name=item, table_ref=null}}, in_list={substitution={name=<inlist subquery>, type=query}}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<inlist subquery>=query}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{scbcrse={item=[[@14,62:65='item',<381>,1:62]], subj_code=[[@6,31:39='subj_code',<381>,1:31]], *=[[@1,7:7='*',<291>,1:7]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@1,7:7='*',<291>,1:7]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={*=[[@1,7:7='*',<291>,1:7]]}, table_dictionary={scbcrse={item=[[@14,62:65='item',<381>,1:62]], subj_code=[[@6,31:39='subj_code',<381>,1:31]], *=[[@1,7:7='*',<291>,1:7]]}}, filters=[{name=subj_code, table_ref=scbcrse}, {name=item, table_ref=scbcrse}], interface={*=[{name=*, table_ref=*}]}, table_alias={aa=scbcrse}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void inPredicateColumnVariableInTest() {
		final String query = "SELECT *  FROM scbcrse aa  WHERE aa.<subj_code> in ('AA', 'BB') ";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=*, table_ref=*}}}, from={table={alias=aa, table=scbcrse}}, where={in={item={column={substitution={name=<subj_code>, type=column}, table_ref=aa}}, in_list={list={1={literal='AA'}, 2={literal='BB'}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<subj_code>=column}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{scbcrse={<subj_code>=[[@6,33:34='aa',<381>,1:33]], *=[[@1,7:7='*',<291>,1:7]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@1,7:7='*',<291>,1:7]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={*=[[@1,7:7='*',<291>,1:7]]}, table_dictionary={scbcrse={<subj_code>=[[@6,33:34='aa',<381>,1:33]], *=[[@1,7:7='*',<291>,1:7]]}}, filters=[{substitution={name=<subj_code>, type=column}, table_ref=aa}], interface={*=[{name=*, table_ref=*}]}, table_alias={aa=scbcrse}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void inPredicatePredicandVariableInTest() {
		// Item 8 - Parse and handle in clauses with Predicand on the right
		final String query = "SELECT *  FROM scbcrse aa  WHERE <subj_code> in ('AA', 'BB') ";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=*, table_ref=*}}}, from={table={alias=aa, table=scbcrse}}, where={in={item={substitution={name=<subj_code>, type=predicand}}, in_list={list={1={literal='AA'}, 2={literal='BB'}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<subj_code>=predicand}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{scbcrse={*=[[@1,7:7='*',<291>,1:7]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@1,7:7='*',<291>,1:7]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={*=[[@1,7:7='*',<291>,1:7]]}, table_dictionary={scbcrse={*=[[@1,7:7='*',<291>,1:7]]}}, filters=[{name=<subj_code>, type=predicand}], interface={*=[{name=*, table_ref=*}]}, table_alias={aa=scbcrse}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void inPredicateInListVariableTest() {
		// Item 10 - Parse and handle New Substitution Variable for the In List (not a subquery)
		final String query = "SELECT *  FROM scbcrse aa  WHERE item in <inlist substitution>";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=*, table_ref=*}}}, from={table={alias=aa, table=scbcrse}}, where={in={item={column={name=item, table_ref=null}}, in_list={substitution={name=<inlist substitution>, type=in_list}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<inlist substitution>=in_list}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{scbcrse={item=[[@6,33:36='item',<381>,1:33]], *=[[@1,7:7='*',<291>,1:7]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@1,7:7='*',<291>,1:7]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={*=[[@1,7:7='*',<291>,1:7]]}, table_dictionary={scbcrse={item=[[@6,33:36='item',<381>,1:33]], *=[[@1,7:7='*',<291>,1:7]]}}, filters=[{name=item, table_ref=scbcrse}], interface={*=[{name=*, table_ref=*}]}, table_alias={aa=scbcrse}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void notInPredicateSubqueryTest() {
		final String query = "SELECT * FROM scbcrse aa  WHERE subj_code not in ('AA', 'BB') "
				+ " and item not in (select * from other)";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=*, table_ref=*}}}, from={table={alias=aa, table=scbcrse}}, where={and={1={in={item={column={name=subj_code, table_ref=null}}, not_in_list={list={1={literal='AA'}, 2={literal='BB'}}}}}, 2={in={item={column={name=item, table_ref=null}}, not_in_list={select={1={column={name=*, table_ref=*}}}, from={table={alias=null, table=other}}}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{other={*=[[@20,87:87='*',<291>,1:87]]}, scbcrse={item=[[@15,67:70='item',<381>,1:67]], subj_code=[[@6,32:40='subj_code',<381>,1:32]], *=[[@1,7:7='*',<291>,1:7]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@20,87:87='*',<291>,1:87]]}, query2={*=[[@1,7:7='*',<291>,1:7]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query2={query_dictionary={*=[[@1,7:7='*',<291>,1:7]]}, table_dictionary={scbcrse={item=[[@15,67:70='item',<381>,1:67]], subj_code=[[@6,32:40='subj_code',<381>,1:32]], *=[[@1,7:7='*',<291>,1:7]]}}, dependent_queries={in_list1={query=query0, type=filters}}, def_query0={query_dictionary={*=[[@20,87:87='*',<291>,1:87]]}, table_dictionary={other={*=[[@20,87:87='*',<291>,1:87]]}}, interface={*=[{name=*, table_ref=*}]}}, filters=[{name=subj_code, table_ref=scbcrse}, {name=item, table_ref=scbcrse}], interface={*=[{name=*, table_ref=*}]}, table_alias={aa=scbcrse}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void notInPredicateInListSubqueryVariableTest() {
		final String query = "SELECT * FROM scbcrse aa WHERE subj_code not in ('AA', 'BB') "
				+ " and item not in (<inlist subquery>)";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=*, table_ref=*}}}, from={table={alias=aa, table=scbcrse}}, where={and={1={in={item={column={name=subj_code, table_ref=null}}, not_in_list={list={1={literal='AA'}, 2={literal='BB'}}}}}, 2={in={item={column={name=item, table_ref=null}}, not_in_list={substitution={name=<inlist subquery>, type=query}}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<inlist subquery>=query}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{scbcrse={item=[[@15,66:69='item',<381>,1:66]], subj_code=[[@6,31:39='subj_code',<381>,1:31]], *=[[@1,7:7='*',<291>,1:7]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@1,7:7='*',<291>,1:7]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={*=[[@1,7:7='*',<291>,1:7]]}, table_dictionary={scbcrse={item=[[@15,66:69='item',<381>,1:66]], subj_code=[[@6,31:39='subj_code',<381>,1:31]], *=[[@1,7:7='*',<291>,1:7]]}}, filters=[{name=subj_code, table_ref=scbcrse}, {name=item, table_ref=scbcrse}], interface={*=[{name=*, table_ref=*}]}, table_alias={aa=scbcrse}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void notInPredicateColumnVariableInTest() {
		final String query = "SELECT *  FROM scbcrse aa  WHERE aa.<subj_code> not in ('AA', 'BB') ";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=*, table_ref=*}}}, from={table={alias=aa, table=scbcrse}}, where={in={item={column={substitution={name=<subj_code>, type=column}, table_ref=aa}}, not_in_list={list={1={literal='AA'}, 2={literal='BB'}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<subj_code>=column}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{scbcrse={<subj_code>=[[@6,33:34='aa',<381>,1:33]], *=[[@1,7:7='*',<291>,1:7]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@1,7:7='*',<291>,1:7]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={*=[[@1,7:7='*',<291>,1:7]]}, table_dictionary={scbcrse={<subj_code>=[[@6,33:34='aa',<381>,1:33]], *=[[@1,7:7='*',<291>,1:7]]}}, filters=[{substitution={name=<subj_code>, type=column}, table_ref=aa}], interface={*=[{name=*, table_ref=*}]}, table_alias={aa=scbcrse}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void notInPredicatePredicandVariableInTest() {
		// Item 8 - Parse and handle in clauses with Predicand on the right
		final String query = "SELECT *  FROM scbcrse aa  WHERE <subj_code> not in ('AA', 'BB') ";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=*, table_ref=*}}}, from={table={alias=aa, table=scbcrse}}, where={in={item={substitution={name=<subj_code>, type=predicand}}, not_in_list={list={1={literal='AA'}, 2={literal='BB'}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<subj_code>=predicand}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{scbcrse={*=[[@1,7:7='*',<291>,1:7]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@1,7:7='*',<291>,1:7]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={*=[[@1,7:7='*',<291>,1:7]]}, table_dictionary={scbcrse={*=[[@1,7:7='*',<291>,1:7]]}}, filters=[{name=<subj_code>, type=predicand}], interface={*=[{name=*, table_ref=*}]}, table_alias={aa=scbcrse}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void notInPredicateInListVariableTest() {
		// Item 10 - Parse and handle New Substitution Variable for the In List (not a subquery)
		final String query = "SELECT *  FROM scbcrse aa  WHERE item not in <inlist substitution>";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=*, table_ref=*}}}, from={table={alias=aa, table=scbcrse}}, where={in={item={column={name=item, table_ref=null}}, not_in_list={substitution={name=<inlist substitution>, type=in_list}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<inlist substitution>=in_list}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{scbcrse={item=[[@6,33:36='item',<381>,1:33]], *=[[@1,7:7='*',<291>,1:7]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@1,7:7='*',<291>,1:7]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={*=[[@1,7:7='*',<291>,1:7]]}, table_dictionary={scbcrse={item=[[@6,33:36='item',<381>,1:33]], *=[[@1,7:7='*',<291>,1:7]]}}, filters=[{name=item, table_ref=scbcrse}], interface={*=[{name=*, table_ref=*}]}, table_alias={aa=scbcrse}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void likeAnyPredicateSubqueryTest() {
		final String query = "SELECT * FROM scbcrse aa  WHERE subj_code like any ('AA%', 'BB%') ";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=*, table_ref=*}}}, from={table={alias=aa, table=scbcrse}}, where={like_any={item={column={name=subj_code, table_ref=null}}, like_any_list={list={1={literal='AA%'}, 2={literal='BB%'}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{scbcrse={subj_code=[[@6,32:40='subj_code',<381>,1:32]], *=[[@1,7:7='*',<291>,1:7]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@1,7:7='*',<291>,1:7]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={*=[[@1,7:7='*',<291>,1:7]]}, table_dictionary={scbcrse={subj_code=[[@6,32:40='subj_code',<381>,1:32]], *=[[@1,7:7='*',<291>,1:7]]}}, filters=[{name=subj_code, table_ref=scbcrse}], interface={*=[{name=*, table_ref=*}]}, table_alias={aa=scbcrse}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void notLikeAnyPredicateSubqueryTest() {
		final String query = "SELECT * FROM scbcrse aa  WHERE subj_code not  LIKE aNy ('AA%', 'BB%') ";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=*, table_ref=*}}}, from={table={alias=aa, table=scbcrse}}, where={like_any={item={column={name=subj_code, table_ref=null}}, not_like_any_list={list={1={literal='AA%'}, 2={literal='BB%'}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{scbcrse={subj_code=[[@6,32:40='subj_code',<381>,1:32]], *=[[@1,7:7='*',<291>,1:7]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@1,7:7='*',<291>,1:7]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={*=[[@1,7:7='*',<291>,1:7]]}, table_dictionary={scbcrse={subj_code=[[@6,32:40='subj_code',<381>,1:32]], *=[[@1,7:7='*',<291>,1:7]]}}, filters=[{name=subj_code, table_ref=scbcrse}], interface={*=[{name=*, table_ref=*}]}, table_alias={aa=scbcrse}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void notIlikeAnyPredicateSubqueryTest() {
		final String query = "SELECT * FROM scbcrse aa  WHERE subj_code not ILIKE aNy ('AA%', 'BB%') ";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=*, table_ref=*}}}, from={table={alias=aa, table=scbcrse}}, where={ilike_any={item={column={name=subj_code, table_ref=null}}, not_like_any_list={list={1={literal='AA%'}, 2={literal='BB%'}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{scbcrse={subj_code=[[@6,32:40='subj_code',<381>,1:32]], *=[[@1,7:7='*',<291>,1:7]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@1,7:7='*',<291>,1:7]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={*=[[@1,7:7='*',<291>,1:7]]}, table_dictionary={scbcrse={subj_code=[[@6,32:40='subj_code',<381>,1:32]], *=[[@1,7:7='*',<291>,1:7]]}}, filters=[{name=subj_code, table_ref=scbcrse}], interface={*=[{name=*, table_ref=*}]}, table_alias={aa=scbcrse}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void likeAnyInListVariableSubqueryTest() {
		final String query = "SELECT * FROM scbcrse aa  WHERE subj_code like any <variable> ";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=*, table_ref=*}}}, from={table={alias=aa, table=scbcrse}}, where={like_any={item={column={name=subj_code, table_ref=null}}, like_any_list={substitution={name=<variable>, type=in_list}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<variable>=in_list}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{scbcrse={subj_code=[[@6,32:40='subj_code',<381>,1:32]], *=[[@1,7:7='*',<291>,1:7]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@1,7:7='*',<291>,1:7]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={*=[[@1,7:7='*',<291>,1:7]]}, table_dictionary={scbcrse={subj_code=[[@6,32:40='subj_code',<381>,1:32]], *=[[@1,7:7='*',<291>,1:7]]}}, filters=[{name=subj_code, table_ref=scbcrse}], interface={*=[{name=*, table_ref=*}]}, table_alias={aa=scbcrse}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void likeAnyWithEscapePredicateSubqueryTest() {
		// Item 95 - add support for PostgresSQL escape character syntax in Like Any clauses
		final String query = "SELECT * FROM scbcrse aa  WHERE subj_code like any ('AA%', 'BB%') escape '_'";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=*, table_ref=*}}}, from={table={alias=aa, table=scbcrse}}, where={like_any={item={column={name=subj_code, table_ref=null}}, not_like_any_list={list={1={literal='AA%'}, 2={literal='BB%'}}}, escape='_'}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{scbcrse={subj_code=[[@6,32:40='subj_code',<381>,1:32]], *=[[@1,7:7='*',<291>,1:7]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@1,7:7='*',<291>,1:7]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={*=[[@1,7:7='*',<291>,1:7]]}, table_dictionary={scbcrse={subj_code=[[@6,32:40='subj_code',<381>,1:32]], *=[[@1,7:7='*',<291>,1:7]]}}, filters=[{name=subj_code, table_ref=scbcrse}], interface={*=[{name=*, table_ref=*}]}, table_alias={aa=scbcrse}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void notLikeAnyWithEscapePredicateSubqueryTest() {
		// Item 95 - add support for PostgresSQL escape character syntax in Like Any clauses
		final String query = "SELECT * FROM scbcrse aa  WHERE subj_code not like any ('AA%', 'BB%') escape '_'";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=*, table_ref=*}}}, from={table={alias=aa, table=scbcrse}}, where={like_any={item={column={name=subj_code, table_ref=null}}, not_like_any_list={list={1={literal='AA%'}, 2={literal='BB%'}}}, escape='_'}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{scbcrse={subj_code=[[@6,32:40='subj_code',<381>,1:32]], *=[[@1,7:7='*',<291>,1:7]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@1,7:7='*',<291>,1:7]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={*=[[@1,7:7='*',<291>,1:7]]}, table_dictionary={scbcrse={subj_code=[[@6,32:40='subj_code',<381>,1:32]], *=[[@1,7:7='*',<291>,1:7]]}}, filters=[{name=subj_code, table_ref=scbcrse}], interface={*=[{name=*, table_ref=*}]}, table_alias={aa=scbcrse}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void notIlikeAnyWithEscapePredicateSubqueryTest() {
		// Item 95 - add support for PostgresSQL escape character syntax in iLike Any clauses
		final String query = "SELECT * FROM scbcrse aa  WHERE subj_code not ilike any ('AA%', 'BB%') escape '_'";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=*, table_ref=*}}}, from={table={alias=aa, table=scbcrse}}, where={ilike_any={item={column={name=subj_code, table_ref=null}}, not_like_any_list={list={1={literal='AA%'}, 2={literal='BB%'}}}, escape='_'}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{scbcrse={subj_code=[[@6,32:40='subj_code',<381>,1:32]], *=[[@1,7:7='*',<291>,1:7]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@1,7:7='*',<291>,1:7]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={*=[[@1,7:7='*',<291>,1:7]]}, table_dictionary={scbcrse={subj_code=[[@6,32:40='subj_code',<381>,1:32]], *=[[@1,7:7='*',<291>,1:7]]}}, filters=[{name=subj_code, table_ref=scbcrse}], interface={*=[{name=*, table_ref=*}]}, table_alias={aa=scbcrse}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void likeCondition1V1Test() {
		//Item 20 - Like Not implemented completely
		final String query = "SELECT apple"
				+ " from tab1 where subj_cd like '%STUFF%'";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=apple, table_ref=null}}}, from={table={alias=null, table=tab1}}, where={condition={left={column={name=subj_cd, table_ref=null}}, right={literal='%STUFF%'}, operator=like}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[apple]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={apple=[[@1,7:11='apple',<381>,1:7]], subj_cd=[[@5,29:35='subj_cd',<381>,1:29]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={apple=[[@1,7:11='apple',<381>,1:7]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={apple=[[@1,7:11='apple',<381>,1:7]]}, table_dictionary={tab1={apple=[[@1,7:11='apple',<381>,1:7]], subj_cd=[[@5,29:35='subj_cd',<381>,1:29]]}}, filters=[{name=subj_cd, table_ref=tab1}], interface={apple=[{name=apple, table_ref=tab1}]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void likeCondition1WithColumnTest() {
		//Item 21 - Not parsing any predicand after the LIKE, only string literals
		final String query = "SELECT apple"
				+ " from tab1 where subj_cd like subj_cd";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=apple, table_ref=null}}}, from={table={alias=null, table=tab1}}, where={condition={left={column={name=subj_cd, table_ref=null}}, right={column={name=subj_cd, table_ref=null}}, operator=like}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[apple]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={apple=[[@1,7:11='apple',<381>,1:7]], subj_cd=[[@5,29:35='subj_cd',<381>,1:29], [@7,42:48='subj_cd',<381>,1:42]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={apple=[[@1,7:11='apple',<381>,1:7]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={apple=[[@1,7:11='apple',<381>,1:7]]}, table_dictionary={tab1={apple=[[@1,7:11='apple',<381>,1:7]], subj_cd=[[@5,29:35='subj_cd',<381>,1:29], [@7,42:48='subj_cd',<381>,1:42]]}}, filters=[{name=subj_cd, table_ref=tab1}], interface={apple=[{name=apple, table_ref=tab1}]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void notLikeCondition1WithColumnTest() {
		//Item 53 - not like AND SIMILAR FAILS TO BUILD TREE
		final String query = "SELECT apple"
				+ " from tab1 where subj_cd not  like subj_cd";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=apple, table_ref=null}}}, from={table={alias=null, table=tab1}}, where={condition={left={column={name=subj_cd, table_ref=null}}, right={column={name=subj_cd, table_ref=null}}, operator=not_like}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[apple]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={apple=[[@1,7:11='apple',<381>,1:7]], subj_cd=[[@5,29:35='subj_cd',<381>,1:29], [@8,47:53='subj_cd',<381>,1:47]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={apple=[[@1,7:11='apple',<381>,1:7]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={apple=[[@1,7:11='apple',<381>,1:7]]}, table_dictionary={tab1={apple=[[@1,7:11='apple',<381>,1:7]], subj_cd=[[@5,29:35='subj_cd',<381>,1:29], [@8,47:53='subj_cd',<381>,1:47]]}}, filters=[{name=subj_cd, table_ref=tab1}], interface={apple=[{name=apple, table_ref=tab1}]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void likeCondition2Test() {
		// Item 21 - Not parsing any predicand after the LIKE, only string literals
		final String query = "SELECT apple"
				+ " from tab1 where subj_cd like lower('%STUFF%')";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=apple, table_ref=null}}}, from={table={alias=null, table=tab1}}, where={condition={left={column={name=subj_cd, table_ref=null}}, right={function={parameters={1={literal='%STUFF%'}}, function_name=lower}}, operator=like}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[apple]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={apple=[[@1,7:11='apple',<381>,1:7]], subj_cd=[[@5,29:35='subj_cd',<381>,1:29]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={apple=[[@1,7:11='apple',<381>,1:7]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={apple=[[@1,7:11='apple',<381>,1:7]]}, table_dictionary={tab1={apple=[[@1,7:11='apple',<381>,1:7]], subj_cd=[[@5,29:35='subj_cd',<381>,1:29]]}}, filters=[{name=subj_cd, table_ref=tab1}], interface={apple=[{name=apple, table_ref=tab1}]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void likeConditionWithSubstitutionV1Test() {
		//  Item 42 - predicand before Like not properly recognized
		final String query = "SELECT apple"
				+ " from tab1 where <subj_cd> like '%STUFF%'";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=apple, table_ref=null}}}, from={table={alias=null, table=tab1}}, where={condition={left={substitution={name=<subj_cd>, type=predicand}}, right={literal='%STUFF%'}, operator=like}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[apple]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<subj_cd>=predicand}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={apple=[[@1,7:11='apple',<381>,1:7]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={apple=[[@1,7:11='apple',<381>,1:7]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={apple=[[@1,7:11='apple',<381>,1:7]]}, table_dictionary={tab1={apple=[[@1,7:11='apple',<381>,1:7]]}}, filters=[{name=<subj_cd>, type=predicand}], interface={apple=[{name=apple, table_ref=tab1}]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void likeConditionWithSubstitutionV2Test() {
		// Item 41 - Not parsing any predicand after the LIKE, only string literals
		final String query = "SELECT apple"
				+ " from tab1 where subj_cd like <predicand>";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=apple, table_ref=null}}}, from={table={alias=null, table=tab1}}, where={condition={left={column={name=subj_cd, table_ref=null}}, right={substitution={name=<predicand>, type=predicand}}, operator=like}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[apple]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<predicand>=predicand}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={apple=[[@1,7:11='apple',<381>,1:7]], subj_cd=[[@5,29:35='subj_cd',<381>,1:29]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={apple=[[@1,7:11='apple',<381>,1:7]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={apple=[[@1,7:11='apple',<381>,1:7]]}, table_dictionary={tab1={apple=[[@1,7:11='apple',<381>,1:7]], subj_cd=[[@5,29:35='subj_cd',<381>,1:29]]}}, filters=[{name=subj_cd, table_ref=tab1}, {name=<predicand>, type=predicand}], interface={apple=[{name=apple, table_ref=tab1}]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void basicHavingTest() {

		final String query = " select spriden_id,  TERM_CODE_ADMIT FROM tab1 "
				+ " HAVING max(TERM_CODE_ADMIT) >= 201310 ";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=spriden_id, table_ref=null}}, 2={column={name=TERM_CODE_ADMIT, table_ref=null}}}, having={condition={left={function={function_name=max, qualifier=null, parameters={column={name=TERM_CODE_ADMIT, table_ref=null}}}}, right={literal=201310}, operator=>=}}, from={table={alias=null, table=tab1}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[TERM_CODE_ADMIT, spriden_id]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={TERM_CODE_ADMIT=[[@3,21:35='TERM_CODE_ADMIT',<381>,1:21], [@9,59:73='TERM_CODE_ADMIT',<381>,1:59]], spriden_id=[[@1,8:17='spriden_id',<381>,1:8]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={TERM_CODE_ADMIT=[[@3,21:35='TERM_CODE_ADMIT',<381>,1:21]], spriden_id=[[@1,8:17='spriden_id',<381>,1:8]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={TERM_CODE_ADMIT=[[@3,21:35='TERM_CODE_ADMIT',<381>,1:21]], spriden_id=[[@1,8:17='spriden_id',<381>,1:8]]}, table_dictionary={tab1={TERM_CODE_ADMIT=[[@3,21:35='TERM_CODE_ADMIT',<381>,1:21], [@9,59:73='TERM_CODE_ADMIT',<381>,1:59]], spriden_id=[[@1,8:17='spriden_id',<381>,1:8]]}}, filters=[{name=TERM_CODE_ADMIT, table_ref=tab1}], interface={TERM_CODE_ADMIT=[{name=TERM_CODE_ADMIT, table_ref=tab1}], spriden_id=[{name=spriden_id, table_ref=tab1}]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void conditionVariableHavingTest() {

		final String query = " select spriden_id,  TERM_CODE_ADMIT FROM tab1 "
				+ " HAVING <condition> or <condition2>";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=spriden_id, table_ref=null}}, 2={column={name=TERM_CODE_ADMIT, table_ref=null}}}, having={or={1={substitution={name=<condition>, type=condition}}, 2={substitution={name=<condition2>, type=condition}}}}, from={table={alias=null, table=tab1}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[TERM_CODE_ADMIT, spriden_id]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<condition>=condition, <condition2>=condition}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={TERM_CODE_ADMIT=[[@3,21:35='TERM_CODE_ADMIT',<381>,1:21]], spriden_id=[[@1,8:17='spriden_id',<381>,1:8]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={TERM_CODE_ADMIT=[[@3,21:35='TERM_CODE_ADMIT',<381>,1:21]], spriden_id=[[@1,8:17='spriden_id',<381>,1:8]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={TERM_CODE_ADMIT=[[@3,21:35='TERM_CODE_ADMIT',<381>,1:21]], spriden_id=[[@1,8:17='spriden_id',<381>,1:8]]}, table_dictionary={tab1={TERM_CODE_ADMIT=[[@3,21:35='TERM_CODE_ADMIT',<381>,1:21]], spriden_id=[[@1,8:17='spriden_id',<381>,1:8]]}}, filters=[], interface={TERM_CODE_ADMIT=[{name=TERM_CODE_ADMIT, table_ref=tab1}], spriden_id=[{name=spriden_id, table_ref=tab1}]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void predicandVariableHavingTest() {

		final String query = " select spriden_id,  TERM_CODE_ADMIT FROM tab1 "
				+ " HAVING <condition> > '20130101' ";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=spriden_id, table_ref=null}}, 2={column={name=TERM_CODE_ADMIT, table_ref=null}}}, having={condition={left={substitution={name=<condition>, type=predicand}}, right={literal='20130101'}, operator=>}}, from={table={alias=null, table=tab1}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[TERM_CODE_ADMIT, spriden_id]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<condition>=predicand}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={TERM_CODE_ADMIT=[[@3,21:35='TERM_CODE_ADMIT',<381>,1:21]], spriden_id=[[@1,8:17='spriden_id',<381>,1:8]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={TERM_CODE_ADMIT=[[@3,21:35='TERM_CODE_ADMIT',<381>,1:21]], spriden_id=[[@1,8:17='spriden_id',<381>,1:8]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={TERM_CODE_ADMIT=[[@3,21:35='TERM_CODE_ADMIT',<381>,1:21]], spriden_id=[[@1,8:17='spriden_id',<381>,1:8]]}, table_dictionary={tab1={TERM_CODE_ADMIT=[[@3,21:35='TERM_CODE_ADMIT',<381>,1:21]], spriden_id=[[@1,8:17='spriden_id',<381>,1:8]]}}, filters=[{name=<condition>, type=predicand}], interface={TERM_CODE_ADMIT=[{name=TERM_CODE_ADMIT, table_ref=tab1}], spriden_id=[{name=spriden_id, table_ref=tab1}]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void columnVariableHavingTest() {

		final String query = " select spriden_id,  TERM_CODE_ADMIT FROM tab1 "
				+ " HAVING tab1.<condition> > '20130101' ";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=spriden_id, table_ref=null}}, 2={column={name=TERM_CODE_ADMIT, table_ref=null}}}, having={condition={left={column={substitution={name=<condition>, type=column}, table_ref=tab1}}, right={literal='20130101'}, operator=>}}, from={table={alias=null, table=tab1}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[TERM_CODE_ADMIT, spriden_id]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<condition>=column}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={<condition>=[[@7,55:58='tab1',<381>,1:55]], TERM_CODE_ADMIT=[[@3,21:35='TERM_CODE_ADMIT',<381>,1:21]], spriden_id=[[@1,8:17='spriden_id',<381>,1:8]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={TERM_CODE_ADMIT=[[@3,21:35='TERM_CODE_ADMIT',<381>,1:21]], spriden_id=[[@1,8:17='spriden_id',<381>,1:8]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={TERM_CODE_ADMIT=[[@3,21:35='TERM_CODE_ADMIT',<381>,1:21]], spriden_id=[[@1,8:17='spriden_id',<381>,1:8]]}, table_dictionary={tab1={<condition>=[[@7,55:58='tab1',<381>,1:55]], TERM_CODE_ADMIT=[[@3,21:35='TERM_CODE_ADMIT',<381>,1:21]], spriden_id=[[@1,8:17='spriden_id',<381>,1:8]]}}, filters=[{substitution={name=<condition>, type=column}, table_ref=tab1}], interface={TERM_CODE_ADMIT=[{name=TERM_CODE_ADMIT, table_ref=tab1}], spriden_id=[{name=spriden_id, table_ref=tab1}]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void basicOrderByTest() {
		final String query = "SELECT apple, fruit_cd from tab1 order by apple, 2, fruit_cd + 1";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=apple, table_ref=null}}, 2={column={name=fruit_cd, table_ref=null}}}, orderby={1={null_order=null, predicand={column={name=apple, table_ref=null}}, sort_order=ASC}, 2={null_order=null, predicand={literal=2}, sort_order=ASC}, 3={null_order=null, predicand={calc={left={column={name=fruit_cd, table_ref=null}}, right={literal=1}, operator=+}}, sort_order=ASC}}, from={table={alias=null, table=tab1}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[apple, fruit_cd]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={apple=[[@1,7:11='apple',<381>,1:7], [@8,42:46='apple',<381>,1:42]], fruit_cd=[[@3,14:21='fruit_cd',<381>,1:14], [@12,52:59='fruit_cd',<381>,1:52]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={apple=[[@1,7:11='apple',<381>,1:7]], fruit_cd=[[@3,14:21='fruit_cd',<381>,1:14]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={apple=[[@1,7:11='apple',<381>,1:7]], fruit_cd=[[@3,14:21='fruit_cd',<381>,1:14]]}, table_dictionary={tab1={apple=[[@1,7:11='apple',<381>,1:7], [@8,42:46='apple',<381>,1:42]], fruit_cd=[[@3,14:21='fruit_cd',<381>,1:14], [@12,52:59='fruit_cd',<381>,1:52]]}}, ordered_by=[{name=apple, table_ref=tab1}, {name=fruit_cd, table_ref=tab1}], interface={apple=[{name=apple, table_ref=tab1}], fruit_cd=[{name=fruit_cd, table_ref=tab1}]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void basicOrderByWithPredicandVariableTest() {
		// Item 36 - Predicand Variable in Order By
		final String query = "SELECT apple, fruit_cd from tab1 order by <predicand variable> desc, fruit_cd";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=apple, table_ref=null}}, 2={column={name=fruit_cd, table_ref=null}}}, orderby={1={null_order=null, predicand={substitution={name=<predicand variable>, type=predicand}}, sort_order=desc}, 2={null_order=null, predicand={column={name=fruit_cd, table_ref=null}}, sort_order=ASC}}, from={table={alias=null, table=tab1}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[apple, fruit_cd]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<predicand variable>=predicand}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={apple=[[@1,7:11='apple',<381>,1:7]], fruit_cd=[[@3,14:21='fruit_cd',<381>,1:14], [@11,69:76='fruit_cd',<381>,1:69]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={apple=[[@1,7:11='apple',<381>,1:7]], fruit_cd=[[@3,14:21='fruit_cd',<381>,1:14]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={apple=[[@1,7:11='apple',<381>,1:7]], fruit_cd=[[@3,14:21='fruit_cd',<381>,1:14]]}, table_dictionary={tab1={apple=[[@1,7:11='apple',<381>,1:7]], fruit_cd=[[@3,14:21='fruit_cd',<381>,1:14], [@11,69:76='fruit_cd',<381>,1:69]]}}, ordered_by=[{name=<predicand variable>, type=predicand}, {name=fruit_cd, table_ref=tab1}], interface={apple=[{name=apple, table_ref=tab1}], fruit_cd=[{name=fruit_cd, table_ref=tab1}]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void basicOrderByWithColumnVariableTest() {
		// Item 36 - Column Variable in Order By
		final String query = "SELECT apple, fruit_cd from tab1 order by tab1.<column variable> desc, fruit_cd";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=apple, table_ref=null}}, 2={column={name=fruit_cd, table_ref=null}}}, orderby={1={null_order=null, predicand={column={substitution={name=<column variable>, type=column}, table_ref=tab1}}, sort_order=desc}, 2={null_order=null, predicand={column={name=fruit_cd, table_ref=null}}, sort_order=ASC}}, from={table={alias=null, table=tab1}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[apple, fruit_cd]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<column variable>=column}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={apple=[[@1,7:11='apple',<381>,1:7]], <column variable>=[[@8,42:45='tab1',<381>,1:42]], fruit_cd=[[@3,14:21='fruit_cd',<381>,1:14], [@13,71:78='fruit_cd',<381>,1:71]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={apple=[[@1,7:11='apple',<381>,1:7]], fruit_cd=[[@3,14:21='fruit_cd',<381>,1:14]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={apple=[[@1,7:11='apple',<381>,1:7]], fruit_cd=[[@3,14:21='fruit_cd',<381>,1:14]]}, table_dictionary={tab1={apple=[[@1,7:11='apple',<381>,1:7]], <column variable>=[[@8,42:45='tab1',<381>,1:42]], fruit_cd=[[@3,14:21='fruit_cd',<381>,1:14], [@13,71:78='fruit_cd',<381>,1:71]]}}, ordered_by=[{substitution={name=<column variable>, type=column}, table_ref=tab1}, {name=fruit_cd, table_ref=tab1}], interface={apple=[{name=apple, table_ref=tab1}], fruit_cd=[{name=fruit_cd, table_ref=tab1}]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void doubleQuotedEscapeSequenceV1Test() {
		final String query = "SELECT 'try embedd\\'d quote' as a"
				+ " from tab1 ";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=a, literal='try embedd\\'d quote'}}, from={table={alias=null, table=tab1}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[a]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={a=[[@3,32:32='a',<381>,1:32]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={a=[[@3,32:32='a',<381>,1:32]]}, table_dictionary={tab1={}}, interface={a=[]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void doubleQuotedEscapeSequenceV2Test() {
		final String query = "SELECT 'try embedd''d quote' as b"
				+ " from tab1 ";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=b, literal='try embedd''d quote'}}, from={table={alias=null, table=tab1}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[b]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={b=[[@3,32:32='b',<381>,1:32]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={b=[[@3,32:32='b',<381>,1:32]]}, table_dictionary={tab1={}}, interface={b=[]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void querySubstitutionVariableForPredicandV1() {
		final String query = "SELECT col1 as ex, <basic_predicand> from old_table "
				+ " WHERE  <second_predicand> = <third_predicand> ";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=col1, table_ref=null}, alias=ex}, 2={substitution={name=<basic_predicand>, type=predicand}}}, from={table={alias=null, table=old_table}}, where={condition={left={substitution={name=<second_predicand>, type=predicand}}, right={substitution={name=<third_predicand>, type=predicand}}, operator==}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[ex, <basic_predicand>]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<basic_predicand>=predicand, <third_predicand>=predicand, <second_predicand>=predicand}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{old_table={col1=[[@1,7:10='col1',<381>,1:7]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={<basic_predicand>=[[@5,19:35='<basic_predicand>',<327>,1:19]], ex=[[@3,15:16='ex',<381>,1:15]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={ex=[[@3,15:16='ex',<381>,1:15]], <basic_predicand>=[[@5,19:35='<basic_predicand>',<327>,1:19]]}, table_dictionary={old_table={col1=[[@1,7:10='col1',<381>,1:7]]}}, filters=[{name=<second_predicand>, type=predicand}, {name=<third_predicand>, type=predicand}], interface={ex=[{name=col1, table_ref=old_table}], <basic_predicand>=[{name=<basic_predicand>, type=predicand}]}}}",
				extractor.getSymbolTable().toString());

	}


	@Test
	public void querySubstitutionVariableForPredicandV2() {
		final String query = "SELECT col1 as ex, <basic_predicand> as predicand from old_table "
				+ " WHERE  <second_predicand> = <third_predicand> ";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=col1, table_ref=null}, alias=ex}, 2={substitution={name=<basic_predicand>, type=predicand}, alias=predicand}}, from={table={alias=null, table=old_table}}, where={condition={left={substitution={name=<second_predicand>, type=predicand}}, right={substitution={name=<third_predicand>, type=predicand}}, operator==}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[ex, predicand]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<basic_predicand>=predicand, <third_predicand>=predicand, <second_predicand>=predicand}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{old_table={col1=[[@1,7:10='col1',<381>,1:7]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={predicand=[[@7,40:48='predicand',<381>,1:40]], ex=[[@3,15:16='ex',<381>,1:15]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={ex=[[@3,15:16='ex',<381>,1:15]], predicand=[[@7,40:48='predicand',<381>,1:40]]}, table_dictionary={old_table={col1=[[@1,7:10='col1',<381>,1:7]]}}, filters=[{name=<second_predicand>, type=predicand}, {name=<third_predicand>, type=predicand}], interface={ex=[{name=col1, table_ref=old_table}], predicand=[{name=<basic_predicand>, type=predicand}]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void querySubstitutionVariableForFullCondition() {
		final String query = "SELECT col1 as ex, <basic_predicand> as predicand from old_table "
				+ " WHERE  <condition_substitute> ";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=col1, table_ref=null}, alias=ex}, 2={substitution={name=<basic_predicand>, type=predicand}, alias=predicand}}, from={table={alias=null, table=old_table}}, where={substitution={name=<condition_substitute>, type=condition}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[ex, predicand]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<basic_predicand>=predicand, <condition_substitute>=condition}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{old_table={col1=[[@1,7:10='col1',<381>,1:7]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={predicand=[[@7,40:48='predicand',<381>,1:40]], ex=[[@3,15:16='ex',<381>,1:15]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={ex=[[@3,15:16='ex',<381>,1:15]], predicand=[[@7,40:48='predicand',<381>,1:40]]}, table_dictionary={old_table={col1=[[@1,7:10='col1',<381>,1:7]]}}, filters=[], interface={ex=[{name=col1, table_ref=old_table}], predicand=[{name=<basic_predicand>, type=predicand}]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void querySubstitutionVariableForMultipleConditions() {
		final String query = "SELECT col1 as ex, <basic_predicand> as predicand from old_table "
				+ " WHERE  <first_condition> or <second_condition> ";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=col1, table_ref=null}, alias=ex}, 2={substitution={name=<basic_predicand>, type=predicand}, alias=predicand}}, from={table={alias=null, table=old_table}}, where={or={1={substitution={name=<first_condition>, type=condition}}, 2={substitution={name=<second_condition>, type=condition}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[ex, predicand]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<first_condition>=condition, <basic_predicand>=predicand, <second_condition>=condition}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{old_table={col1=[[@1,7:10='col1',<381>,1:7]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={predicand=[[@7,40:48='predicand',<381>,1:40]], ex=[[@3,15:16='ex',<381>,1:15]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={ex=[[@3,15:16='ex',<381>,1:15]], predicand=[[@7,40:48='predicand',<381>,1:40]]}, table_dictionary={old_table={col1=[[@1,7:10='col1',<381>,1:7]]}}, filters=[], interface={ex=[{name=col1, table_ref=old_table}], predicand=[{name=<basic_predicand>, type=predicand}]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void querySubstitutionVariableForTableOrSubQueryFromSubstitution() {
		final String query = "SELECT col1 as ex, <basic_predicand> as predicand from <old_table> as new_table "
				+ " WHERE  <second_predicand> or <third_predicand> ";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=col1, table_ref=null}, alias=ex}, 2={substitution={name=<basic_predicand>, type=predicand}, alias=predicand}}, from={table={alias=new_table, substitution={name=<old_table>, type=tuple}}}, where={or={1={substitution={name=<second_predicand>, type=condition}}, 2={substitution={name=<third_predicand>, type=condition}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[ex, predicand]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<basic_predicand>=predicand, <old_table>=tuple, <third_predicand>=condition, <second_predicand>=condition}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{<old_table>={col1=[[@1,7:10='col1',<381>,1:7]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={predicand=[[@7,40:48='predicand',<381>,1:40]], ex=[[@3,15:16='ex',<381>,1:15]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={ex=[[@3,15:16='ex',<381>,1:15]], predicand=[[@7,40:48='predicand',<381>,1:40]]}, table_dictionary={<old_table>={col1=[[@1,7:10='col1',<381>,1:7]]}}, filters=[], interface={ex=[{name=col1, table_ref=<old_table>}], predicand=[{name=<basic_predicand>, type=predicand}]}, table_alias={new_table=<old_table>}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void querySubstitutionVariableWithQualifiedColumnsMixedConditionVariablesSubstitution() {
		final String query = "SELECT new_table.<col1> as ex, <basic_predicand> as predicand from <old_table> as new_table "
				+ " WHERE  <second_condition> or <third_condition> or (<second_predicand> > <third_predicand>) ";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={substitution={name=<col1>, type=column}, table_ref=new_table}, alias=ex}, 2={substitution={name=<basic_predicand>, type=predicand}, alias=predicand}}, from={table={alias=new_table, substitution={name=<old_table>, type=tuple}}}, where={or={1={substitution={name=<second_condition>, type=condition}}, 2={substitution={name=<third_condition>, type=condition}}, 3={parentheses={condition={left={substitution={name=<second_predicand>, type=predicand}}, right={substitution={name=<third_predicand>, type=predicand}}, operator=>}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[ex, predicand]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<third_condition>=condition, <basic_predicand>=predicand, <second_condition>=condition, <col1>=column, <old_table>=tuple, <third_predicand>=predicand, <second_predicand>=predicand}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{<old_table>={<col1>=[[@1,7:15='new_table',<381>,1:7]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={predicand=[[@9,52:60='predicand',<381>,1:52]], ex=[[@5,27:28='ex',<381>,1:27]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={ex=[[@5,27:28='ex',<381>,1:27]], predicand=[[@9,52:60='predicand',<381>,1:52]]}, table_dictionary={<old_table>={<col1>=[[@1,7:15='new_table',<381>,1:7]]}}, filters=[{name=<second_predicand>, type=predicand}, {name=<third_predicand>, type=predicand}], interface={ex=[{substitution={name=<col1>, type=column}, table_ref=new_table}], predicand=[{name=<basic_predicand>, type=predicand}]}, table_alias={new_table=<old_table>}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void querySubstitutionVariableNotInJoinSubstitution() {
		final String query = "SELECT new_table.col1 as ex, <basic_predicand> as predicand from <gu> as old_table "
				+ " join <nt> as new_table on new_table.a1=old_table.b1"
				+ " WHERE  <second_condition> or <third_condition> ";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=col1, table_ref=new_table}, alias=ex}, 2={substitution={name=<basic_predicand>, type=predicand}, alias=predicand}}, from={join={1={table={alias=old_table, substitution={name=<gu>, type=tuple}}}, 2={join=join, on={condition={left={column={name=a1, table_ref=new_table}}, right={column={name=b1, table_ref=old_table}}, operator==}}}, 3={table={alias=new_table, substitution={name=<nt>, type=tuple}}}}}, where={or={1={substitution={name=<second_condition>, type=condition}}, 2={substitution={name=<third_condition>, type=condition}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[ex, predicand]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<nt>=tuple, <third_condition>=condition, <basic_predicand>=predicand, <second_condition>=condition, <gu>=tuple}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{<nt>={a1=[[@19,110:118='new_table',<381>,1:110]], col1=[[@1,7:15='new_table',<381>,1:7]]}, <gu>={b1=[[@23,123:131='old_table',<381>,1:123]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={predicand=[[@9,50:58='predicand',<381>,1:50]], ex=[[@5,25:26='ex',<381>,1:25]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={ex=[[@5,25:26='ex',<381>,1:25]], predicand=[[@9,50:58='predicand',<381>,1:50]]}, table_dictionary={<nt>={a1=[[@19,110:118='new_table',<381>,1:110]], col1=[[@1,7:15='new_table',<381>,1:7]]}, <gu>={b1=[[@23,123:131='old_table',<381>,1:123]]}}, filters=[{name=a1, table_ref=new_table}, {name=b1, table_ref=old_table}], interface={ex=[{name=col1, table_ref=new_table}], predicand=[{name=<basic_predicand>, type=predicand}]}, table_alias={old_table=<gu>, new_table=<nt>}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void querySubstitutionVariableInJoinSubstitution() {
		// TODO: Any variable in the ON clause is added to the table dictionary for some reason. Should NOT be added from the ON clause since its not a table.
		final String query = "SELECT new_table.col1 as ex, <basic_predicand> as predicand from <gu> as old_table "
				+ " join <nt> as new_table on <gu_nt_join_condition>"
				+ " WHERE  <second_condition> or <third_condition> ";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=col1, table_ref=new_table}, alias=ex}, 2={substitution={name=<basic_predicand>, type=predicand}, alias=predicand}}, from={join={1={table={alias=old_table, substitution={name=<gu>, type=tuple}}}, 2={join=join, on={substitution={name=<gu_nt_join_condition>, type=condition}}}, 3={table={alias=new_table, substitution={name=<nt>, type=tuple}}}}}, where={or={1={substitution={name=<second_condition>, type=condition}}, 2={substitution={name=<third_condition>, type=condition}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[ex, predicand]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<nt>=tuple, <gu_nt_join_condition>=condition, <third_condition>=condition, <basic_predicand>=predicand, <second_condition>=condition, <gu>=tuple}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{<nt>={col1=[[@1,7:15='new_table',<381>,1:7]]}, <gu>={}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={predicand=[[@9,50:58='predicand',<381>,1:50]], ex=[[@5,25:26='ex',<381>,1:25]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={ex=[[@5,25:26='ex',<381>,1:25]], predicand=[[@9,50:58='predicand',<381>,1:50]]}, table_dictionary={<nt>={col1=[[@1,7:15='new_table',<381>,1:7]]}, <gu>={}}, filters=[], interface={ex=[{name=col1, table_ref=new_table}], predicand=[{name=<basic_predicand>, type=predicand}]}, table_alias={old_table=<gu>, new_table=<nt>}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void querySubstitutionVariableColumnVarInJoinSubstitution() {
		// TODO: Any variable in the ON clause is added to the table dictionary for some reason. Should NOT be added from the ON clause since its not a table.
		final String query = "SELECT new_table.col1 as ex, <basic_predicand> as predicand from <gu> as old_table "
				+ " join <nt> as new_table on (new_table.<a1>=old_table.<b1>)"
				+ " WHERE  <second_condition> or <third_condition> ";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=col1, table_ref=new_table}, alias=ex}, 2={substitution={name=<basic_predicand>, type=predicand}, alias=predicand}}, from={join={1={table={alias=old_table, substitution={name=<gu>, type=tuple}}}, 2={join=join, on={condition={left={column={substitution={name=<a1>, type=column}, table_ref=new_table}}, right={column={substitution={name=<b1>, type=column}, table_ref=old_table}}, operator==}}}, 3={table={alias=new_table, substitution={name=<nt>, type=tuple}}}}}, where={or={1={substitution={name=<second_condition>, type=condition}}, 2={substitution={name=<third_condition>, type=condition}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[ex, predicand]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<nt>=tuple, <third_condition>=condition, <basic_predicand>=predicand, <second_condition>=condition, <gu>=tuple, <a1>=column, <b1>=column}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{<nt>={<a1>=[[@20,111:119='new_table',<381>,1:111]], col1=[[@1,7:15='new_table',<381>,1:7]]}, <gu>={<b1>=[[@24,126:134='old_table',<381>,1:126]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={predicand=[[@9,50:58='predicand',<381>,1:50]], ex=[[@5,25:26='ex',<381>,1:25]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={ex=[[@5,25:26='ex',<381>,1:25]], predicand=[[@9,50:58='predicand',<381>,1:50]]}, table_dictionary={<nt>={<a1>=[[@20,111:119='new_table',<381>,1:111]], col1=[[@1,7:15='new_table',<381>,1:7]]}, <gu>={<b1>=[[@24,126:134='old_table',<381>,1:126]]}}, filters=[{substitution={name=<a1>, type=column}, table_ref=new_table}, {substitution={name=<b1>, type=column}, table_ref=old_table}], interface={ex=[{name=col1, table_ref=new_table}], predicand=[{name=<basic_predicand>, type=predicand}]}, table_alias={old_table=<gu>, new_table=<nt>}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void querySubstitutionVariablePredicandAndColumnVarsInJoinSubstitution() {
		// TODO: Any variable in the ON clause is added to the table dictionary for some reason. Should NOT be added from the ON clause since its not a table.
		final String query = "SELECT new_table.col1 as ex, <basic_predicand> as predicand from <gu> as old_table "
				+ " join <nt> as new_table on (<a1>=old_table.<b1>)"
				+ " WHERE  <second_condition> or <third_condition> ";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=col1, table_ref=new_table}, alias=ex}, 2={substitution={name=<basic_predicand>, type=predicand}, alias=predicand}}, from={join={1={table={alias=old_table, substitution={name=<gu>, type=tuple}}}, 2={join=join, on={condition={left={substitution={name=<a1>, type=predicand}}, right={column={substitution={name=<b1>, type=column}, table_ref=old_table}}, operator==}}}, 3={table={alias=new_table, substitution={name=<nt>, type=tuple}}}}}, where={or={1={substitution={name=<second_condition>, type=condition}}, 2={substitution={name=<third_condition>, type=condition}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[ex, predicand]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<nt>=tuple, <third_condition>=condition, <basic_predicand>=predicand, <second_condition>=condition, <gu>=tuple, <a1>=predicand, <b1>=column}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{<nt>={col1=[[@1,7:15='new_table',<381>,1:7]]}, <gu>={<b1>=[[@22,116:124='old_table',<381>,1:116]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={predicand=[[@9,50:58='predicand',<381>,1:50]], ex=[[@5,25:26='ex',<381>,1:25]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={ex=[[@5,25:26='ex',<381>,1:25]], predicand=[[@9,50:58='predicand',<381>,1:50]]}, table_dictionary={<nt>={col1=[[@1,7:15='new_table',<381>,1:7]]}, <gu>={<b1>=[[@22,116:124='old_table',<381>,1:116]]}}, filters=[{name=<a1>, type=predicand}, {substitution={name=<b1>, type=column}, table_ref=old_table}], interface={ex=[{name=col1, table_ref=new_table}], predicand=[{name=<basic_predicand>, type=predicand}]}, table_alias={old_table=<gu>, new_table=<nt>}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void substitutionsWithWhereClausePredicandsTest() {
		final String query = " Select <column1> as redvalue, <column2> as greenvalue "
				+ " from <table> as tab where <column1> > <column2>;";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={substitution={name=<column1>, type=predicand}, alias=redvalue}, 2={substitution={name=<column2>, type=predicand}, alias=greenvalue}}, from={table={alias=tab, substitution={name=<table>, type=tuple}}}, where={condition={left={substitution={name=<column1>, type=predicand}}, right={substitution={name=<column2>, type=predicand}}, operator=>}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[redvalue, greenvalue]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<table>=tuple, <column1>=predicand, <column2>=predicand}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{<table>={}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={redvalue=[[@3,21:28='redvalue',<381>,1:21]], greenvalue=[[@7,44:53='greenvalue',<381>,1:44]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={redvalue=[[@3,21:28='redvalue',<381>,1:21]], greenvalue=[[@7,44:53='greenvalue',<381>,1:44]]}, table_dictionary={<table>={}}, filters=[{name=<column1>, type=predicand}, {name=<column2>, type=predicand}], interface={redvalue=[{name=<column1>, type=predicand}], greenvalue=[{name=<column2>, type=predicand}]}, table_alias={tab=<table>}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void substitutionsOfColumnsWithTableAliasTest() {
		// ITEM 29 - Tuple Substitution Variable does not appear in Symbol Tree or Table Dictionary
		final String query = " Select tt.<column1> as redvalue, tt.<column2> as greenvalue "
				+ " from <table> as tt where tt.<column1> > tt.<column2>";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={substitution={name=<column1>, type=column}, table_ref=tt}, alias=redvalue}, 2={column={substitution={name=<column2>, type=column}, table_ref=tt}, alias=greenvalue}}, from={table={alias=tt, substitution={name=<table>, type=tuple}}}, where={condition={left={column={substitution={name=<column1>, type=column}, table_ref=tt}}, right={column={substitution={name=<column2>, type=column}, table_ref=tt}}, operator=>}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[redvalue, greenvalue]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<table>=tuple, <column1>=column, <column2>=column}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{<table>={<column1>=[[@1,8:9='tt',<381>,1:8], [@17,87:88='tt',<381>,1:87]], <column2>=[[@7,34:35='tt',<381>,1:34], [@21,102:103='tt',<381>,1:102]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={redvalue=[[@5,24:31='redvalue',<381>,1:24]], greenvalue=[[@11,50:59='greenvalue',<381>,1:50]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={redvalue=[[@5,24:31='redvalue',<381>,1:24]], greenvalue=[[@11,50:59='greenvalue',<381>,1:50]]}, table_dictionary={<table>={<column1>=[[@1,8:9='tt',<381>,1:8], [@17,87:88='tt',<381>,1:87]], <column2>=[[@7,34:35='tt',<381>,1:34], [@21,102:103='tt',<381>,1:102]]}}, filters=[{substitution={name=<column1>, type=column}, table_ref=tt}, {substitution={name=<column2>, type=column}, table_ref=tt}], interface={redvalue=[{substitution={name=<column1>, type=column}, table_ref=tt}], greenvalue=[{substitution={name=<column2>, type=column}, table_ref=tt}]}, table_alias={tt=<table>}}}",
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
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=noalias, table_ref=null}}, 2={column={name=normcol, table_ref=null}, alias=normalias}, 3={substitution={name=<PredicandVariableNoAlias>, type=predicand}}, 4={substitution={name=<PredicandVariable>, type=predicand}, alias=predicandAlias}, 5={column={substitution={name=<ColumnVariableNoAlias>, type=column}, table_ref=studentTable}}, 6={column={substitution={name=<ColumnVariableWithAlias>, type=column}, table_ref=studentTable}, alias=columnAlias}}, from={table={alias=studentTable, substitution={name=<StudentTable>, type=tuple}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[<ColumnVariableNoAlias>, <PredicandVariableNoAlias>, predicandAlias, normalias, columnAlias, noalias]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<ColumnVariableNoAlias>=column, <ColumnVariableWithAlias>=column, <StudentTable>=tuple, <PredicandVariableNoAlias>=predicand, <PredicandVariable>=predicand}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{<StudentTable>={<ColumnVariableWithAlias>=[[@16,141:152='studentTable',<381>,1:141]], <ColumnVariableNoAlias>=[[@12,103:114='studentTable',<381>,1:103]], normcol=[[@3,17:23='normcol',<381>,1:17]], noalias=[[@1,8:14='noalias',<381>,1:8]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={<ColumnVariableNoAlias>=[[@14,116:138='<ColumnVariableNoAlias>',<327>,1:116]], <PredicandVariableNoAlias>=[[@7,39:64='<PredicandVariableNoAlias>',<327>,1:39]], predicandAlias=[[@10,87:100='predicandAlias',<381>,1:87]], normalias=[[@5,28:36='normalias',<381>,1:28]], columnAlias=[[@19,180:190='columnAlias',<381>,1:180]], noalias=[[@1,8:14='noalias',<381>,1:8]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={<ColumnVariableNoAlias>=[[@14,116:138='<ColumnVariableNoAlias>',<327>,1:116]], <PredicandVariableNoAlias>=[[@7,39:64='<PredicandVariableNoAlias>',<327>,1:39]], predicandAlias=[[@10,87:100='predicandAlias',<381>,1:87]], normalias=[[@5,28:36='normalias',<381>,1:28]], columnAlias=[[@19,180:190='columnAlias',<381>,1:180]], noalias=[[@1,8:14='noalias',<381>,1:8]]}, table_dictionary={<StudentTable>={<ColumnVariableWithAlias>=[[@16,141:152='studentTable',<381>,1:141]], <ColumnVariableNoAlias>=[[@12,103:114='studentTable',<381>,1:103]], normcol=[[@3,17:23='normcol',<381>,1:17]], noalias=[[@1,8:14='noalias',<381>,1:8]]}}, interface={<ColumnVariableNoAlias>=[{substitution={name=<ColumnVariableNoAlias>, type=column}, table_ref=studentTable}], <PredicandVariableNoAlias>=[{name=<PredicandVariableNoAlias>, type=predicand}], predicandAlias=[{name=<PredicandVariable>, type=predicand}], normalias=[{name=normcol, table_ref=<StudentTable>}], columnAlias=[{substitution={name=<ColumnVariableWithAlias>, type=column}, table_ref=studentTable}], noalias=[{name=noalias, table_ref=<StudentTable>}]}, table_alias={studentTable=<StudentTable>}}}",
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
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={function={parameters={1={column={name=newColumn, table_ref=old_table}}, 2={column={name=otherColumn, table_ref=null}}, 3={substitution={name=<substitute_me>, type=predicand}}, 4={column={substitution={name=<today>, type=column}, table_ref=old_table}}, 5={literal=128.9}, 6={literal='A'}}, function_name=func}, alias=ex}}, from={table={alias=null, table=old_table}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[ex]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<today>=column, <substitute_me>=predicand}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{old_table={newColumn=[[@3,12:20='old_table',<381>,1:12]], otherColumn=[[@7,33:43='otherColumn',<381>,1:33]], <today>=[[@11,63:71='old_table',<381>,1:63]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={ex=[[@22,97:98='ex',<381>,1:97]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={ex=[[@22,97:98='ex',<381>,1:97]]}, table_dictionary={old_table={newColumn=[[@3,12:20='old_table',<381>,1:12]], otherColumn=[[@7,33:43='otherColumn',<381>,1:33]], <today>=[[@11,63:71='old_table',<381>,1:63]]}}, interface={ex=[{name=newColumn, table_ref=old_table}, {name=otherColumn, table_ref=old_table}, {name=<substitute_me>, type=predicand}, {substitution={name=<today>, type=column}, table_ref=old_table}]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void withQueryFromNavigateV2StudentSubstitution() {
		final String query = "with getLastXTerms as ( <GetLastXTerms> ), "
				+ "\n studentPopulation as ( <studentPopulation> ), "
				+ "\n student as ( "
				+ "\n select distinct <StudentIdentifier> as nk, "
				+ "\n studentTable.<StudentId> as username, "
				+ "\n <StudentEmailAddress> as email, "
				+ "\n studentTable.<StudentFirstName> as first_name, "
				+ "\n <StudentLastName> as last_name, "
				+ "\n <Birthdate> as birthdate, "
				+ "\n <ActiveStudent> as is_active "
				+ "\n from <StudentTable> as studentTable join studentPopulation ON  "
				+ "\n <studentPopulationJoinCondition>  "
				+ "\n Left join <PersonTable> as personTable on ( "
				+ "\n <personTableJoinCondition> ) "
				+ "\n where <whereClause> )"
				+ "\n select *, <missing>, <notmissing> as notMissing from student ";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={with={1={cte={substitution={name=<GetLastXTerms>, type=query}}, alias=getLastXTerms}, 2={cte={substitution={name=<studentPopulation>, type=query}}, alias=studentPopulation}, 3={cte={select={1={substitution={name=<StudentIdentifier>, type=predicand}, alias=nk}, 2={column={substitution={name=<StudentId>, type=column}, table_ref=studentTable}, alias=username}, 3={substitution={name=<StudentEmailAddress>, type=predicand}, alias=email}, 4={column={substitution={name=<StudentFirstName>, type=column}, table_ref=studentTable}, alias=first_name}, 5={substitution={name=<StudentLastName>, type=predicand}, alias=last_name}, 6={substitution={name=<Birthdate>, type=predicand}, alias=birthdate}, 7={substitution={name=<ActiveStudent>, type=predicand}, alias=is_active}}, qualifier=distinct, from={join={1={table={alias=studentTable, substitution={name=<StudentTable>, type=tuple}}}, 2={join=join, on={substitution={name=<studentPopulationJoinCondition>, type=condition}}}, 3={table={alias=null, table=studentPopulation}}, 4={join=Left, on={substitution={name=<personTableJoinCondition>, type=condition}}}, 5={table={alias=personTable, substitution={name=<PersonTable>, type=tuple}}}}}, where={substitution={name=<whereClause>, type=condition}}}, alias=student}}, query={select={1={column={name=*, table_ref=*}}, 2={substitution={name=<missing>, type=predicand}}, 3={substitution={name=<notmissing>, type=predicand}, alias=notMissing}}, from={table={alias=null, table=student}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[notMissing, *, <missing>]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<GetLastXTerms>=query, <StudentIdentifier>=predicand, <personTableJoinCondition>=condition, <StudentId>=column, <studentPopulationJoinCondition>=condition, <missing>=predicand, <StudentFirstName>=column, <StudentLastName>=predicand, <whereClause>=condition, <studentPopulation>=query, <PersonTable>=tuple, <StudentTable>=tuple, <StudentEmailAddress>=predicand, <Birthdate>=predicand, <notmissing>=predicand, <ActiveStudent>=predicand}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{<PersonTable>={}, <StudentTable>={<StudentFirstName>=[[@32,227:238='studentTable',<381>,7:1]], <StudentId>=[[@22,153:164='studentTable',<381>,5:1]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={birthdate=[[@44,325:333='birthdate',<381>,9:16]], is_active=[[@48,357:365='is_active',<381>,10:20]], last_name=[[@40,297:305='last_name',<381>,8:22]], *=[[@70,577:577='*',<291>,16:8]], nk=[[@20,147:148='nk',<381>,4:40]], first_name=[[@36,262:271='first_name',<381>,7:36]], email=[[@30,218:222='email',<381>,6:26]], username=[[@26,181:188='username',<381>,5:29]]}, query1={notMissing=[[@76,607:616='notMissing',<381>,16:38]], *=[[@70,577:577='*',<291>,16:8]], <missing>=[[@72,580:588='<missing>',<327>,16:11]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query1={context_list={student=query0}, query_dictionary={notMissing=[[@76,607:616='notMissing',<381>,16:38]], *=[[@70,577:577='*',<291>,16:8]], <missing>=[[@72,580:588='<missing>',<327>,16:11]]}, def_query0={query_dictionary={birthdate=[[@44,325:333='birthdate',<381>,9:16]], is_active=[[@48,357:365='is_active',<381>,10:20]], last_name=[[@40,297:305='last_name',<381>,8:22]], *=[[@70,577:577='*',<291>,16:8]], nk=[[@20,147:148='nk',<381>,4:40]], first_name=[[@36,262:271='first_name',<381>,7:36]], email=[[@30,218:222='email',<381>,6:26]], username=[[@26,181:188='username',<381>,5:29]]}, table_dictionary={<PersonTable>={}, <StudentTable>={<StudentFirstName>=[[@32,227:238='studentTable',<381>,7:1]], <StudentId>=[[@22,153:164='studentTable',<381>,5:1]]}}, filters=[], interface={birthdate=[{name=<Birthdate>, type=predicand}], is_active=[{name=<ActiveStudent>, type=predicand}], last_name=[{name=<StudentLastName>, type=predicand}], nk=[{name=<StudentIdentifier>, type=predicand}], first_name=[{substitution={name=<StudentFirstName>, type=column}, table_ref=studentTable}], email=[{name=<StudentEmailAddress>, type=predicand}], username=[{substitution={name=<StudentId>, type=column}, table_ref=studentTable}]}, table_alias={studentPopulation=query1, personTable=<PersonTable>, studentTable=<StudentTable>}}, interface={notMissing=[{name=<notmissing>, type=predicand}], *=[{name=*, table_ref=*}], <missing>=[{name=<missing>, type=predicand}]}, table_alias={student=query0}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void interfaceHandlingOfSubstitution() {
		// Item 32 - Interface should use the substitution variables name if not aliased
		final String query =  " select normalColumn, <missing>, <notmissing> as notMissing from student ";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=normalColumn, table_ref=null}}, 2={substitution={name=<missing>, type=predicand}}, 3={substitution={name=<notmissing>, type=predicand}, alias=notMissing}}, from={table={alias=null, table=student}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[normalColumn, notMissing, <missing>]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<notmissing>=predicand, <missing>=predicand}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{student={normalColumn=[[@1,8:19='normalColumn',<381>,1:8]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={normalColumn=[[@1,8:19='normalColumn',<381>,1:8]], notMissing=[[@7,49:58='notMissing',<381>,1:49]], <missing>=[[@3,22:30='<missing>',<327>,1:22]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={normalColumn=[[@1,8:19='normalColumn',<381>,1:8]], notMissing=[[@7,49:58='notMissing',<381>,1:49]], <missing>=[[@3,22:30='<missing>',<327>,1:22]]}, table_dictionary={student={normalColumn=[[@1,8:19='normalColumn',<381>,1:8]]}}, interface={normalColumn=[{name=normalColumn, table_ref=student}], notMissing=[{name=<notmissing>, type=predicand}], <missing>=[{name=<missing>, type=predicand}]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void unionJoinWithSubstitutionV1() {
		// Item 34 - With bare FROM variables, "union join" binds as UNION JOIN (not alias=union + JOIN).
		// "natural join" still needs care: implicit aliases can steal join-type words when allowed.
		final String query = " SELECT * FROM third cross join <fourth> union join <fifth> fth natural join sixth ";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=*, table_ref=*}}}, from={join={1={table={alias=null, table=third}}, 2={join=crossjoin}, 3={table={substitution={name=<fourth>, type=tuple}, alias=null}}, 4={join=unionjoin}, 5={table={alias=fth, substitution={name=<fifth>, type=tuple}}}, 6={join=naturaljoin}, 7={table={alias=null, table=sixth}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<fifth>=tuple, <fourth>=tuple}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{sixth={*=[[@1,8:8='*',<291>,1:8]]}, third={*=[[@1,8:8='*',<291>,1:8]]}, <fifth>={*=[[@1,8:8='*',<291>,1:8]]}, <fourth>={*=[[@1,8:8='*',<291>,1:8]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@1,8:8='*',<291>,1:8]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={*=[[@1,8:8='*',<291>,1:8]]}, table_dictionary={sixth={*=[[@1,8:8='*',<291>,1:8]]}, third={*=[[@1,8:8='*',<291>,1:8]]}, <fifth>={*=[[@1,8:8='*',<291>,1:8]]}, <fourth>={*=[[@1,8:8='*',<291>,1:8]]}}, interface={*=[{name=*, table_ref=*}]}, table_alias={fth=<fifth>}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void unionSubstitutionV1() {
		final String query = " SELECT * FROM third union <fourth>  intersect <sixth>  union <fifth>";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={intersect={1={union={1={select={1={column={name=*, table_ref=*}}}, from={table={alias=null, table=third}}}, 2={union={qualifier=null, operator=union}}, 3={substitution={name=<fourth>, type=query}}}}, 2={intersect={qualifier=null, operator=intersect}}, 3={union={1={substitution={name=<sixth>, type=query}}, 2={union={qualifier=null, operator=union}}, 3={substitution={name=<fifth>, type=query}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<sixth>=query, <fifth>=query, <fourth>=query}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{third={*=[[@1,8:8='*',<291>,1:8]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@1,8:8='*',<291>,1:8]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_intersect3={def_union1={setop=UNION, def_query0={query_dictionary={*=[[@1,8:8='*',<291>,1:8]]}, table_dictionary={third={*=[[@1,8:8='*',<291>,1:8]]}}, interface={*=[{name=*, table_ref=*}]}}}, def_union2={setop=INTERSECTION}, setop=UNION}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void unionSubstitutionV1IntersectAsExcept() {
		final String query = " SELECT * FROM third union <fourth>  except <sixth>  union <fifth>";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={union={1={select={1={column={name=*, table_ref=*}}}, from={table={alias=null, table=third}}}, 2={union={qualifier=null, operator=union}}, 3={substitution={name=<fourth>, type=query}}, 4={union={qualifier=null, operator=except}}, 5={substitution={name=<sixth>, type=query}}, 6={union={qualifier=null, operator=union}}, 7={substitution={name=<fifth>, type=query}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<sixth>=query, <fifth>=query, <fourth>=query}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{third={*=[[@1,8:8='*',<291>,1:8]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@1,8:8='*',<291>,1:8]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_union1={setop=UNION, def_query0={query_dictionary={*=[[@1,8:8='*',<291>,1:8]]}, table_dictionary={third={*=[[@1,8:8='*',<291>,1:8]]}}, interface={*=[{name=*, table_ref=*}]}}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void exceptSubstitutionV1(){
		final String query = " SELECT * FROM third except <fourth>  intersect <sixth>  except <fifth>";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={intersect={1={union={1={select={1={column={name=*, table_ref=*}}}, from={table={alias=null, table=third}}}, 2={union={qualifier=null, operator=except}}, 3={substitution={name=<fourth>, type=query}}}}, 2={intersect={qualifier=null, operator=intersect}}, 3={union={1={substitution={name=<sixth>, type=query}}, 2={union={qualifier=null, operator=except}}, 3={substitution={name=<fifth>, type=query}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<sixth>=query, <fifth>=query, <fourth>=query}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{third={*=[[@1,8:8='*',<291>,1:8]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@1,8:8='*',<291>,1:8]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_intersect3={def_union1={setop=EXCEPT, def_query0={query_dictionary={*=[[@1,8:8='*',<291>,1:8]]}, table_dictionary={third={*=[[@1,8:8='*',<291>,1:8]]}}, interface={*=[{name=*, table_ref=*}]}}}, def_union2={setop=INTERSECTION}, setop=EXCEPT}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void unionSubstitutionParserShapeFocusedTest() {
		final String query = " SELECT * FROM third union <fourth>";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong", "{SQL={union={1={select={1={column={name=*, table_ref=*}}}, from={table={alias=null, table=third}}}, 2={union={qualifier=null, operator=union}}, 3={substitution={name=<fourth>, type=query}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<fourth>=query}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{third={*=[[@1,8:8='*',<291>,1:8]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@1,8:8='*',<291>,1:8]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_union1={setop=UNION, def_query0={query_dictionary={*=[[@1,8:8='*',<291>,1:8]]}, table_dictionary={third={*=[[@1,8:8='*',<291>,1:8]]}}, interface={*=[{name=*, table_ref=*}]}}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void exceptSubstitutionParserShapeFocusedTest(){
		final String query = " SELECT * FROM third except <fourth>";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong", "{SQL={union={1={select={1={column={name=*, table_ref=*}}}, from={table={alias=null, table=third}}}, 2={union={qualifier=null, operator=except}}, 3={substitution={name=<fourth>, type=query}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<fourth>=query}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{third={*=[[@1,8:8='*',<291>,1:8]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@1,8:8='*',<291>,1:8]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_union1={setop=EXCEPT, def_query0={query_dictionary={*=[[@1,8:8='*',<291>,1:8]]}, table_dictionary={third={*=[[@1,8:8='*',<291>,1:8]]}}, interface={*=[{name=*, table_ref=*}]}}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void unionSubstitutionV2() {
		final String query = " SELECT * FROM student union <optionalAllStudent> ";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={union={1={select={1={column={name=*, table_ref=*}}}, from={table={alias=null, table=student}}}, 2={union={qualifier=null, operator=union}}, 3={substitution={name=<optionalAllStudent>, type=query}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<optionalAllStudent>=query}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{student={*=[[@1,8:8='*',<291>,1:8]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@1,8:8='*',<291>,1:8]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_union1={setop=UNION, def_query0={query_dictionary={*=[[@1,8:8='*',<291>,1:8]]}, table_dictionary={student={*=[[@1,8:8='*',<291>,1:8]]}}, interface={*=[{name=*, table_ref=*}]}}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void exceptSubstitutionV2(){
		final String query = " SELECT * FROM student except <optionalAllStudent> ";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={union={1={select={1={column={name=*, table_ref=*}}}, from={table={alias=null, table=student}}}, 2={union={qualifier=null, operator=except}}, 3={substitution={name=<optionalAllStudent>, type=query}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<optionalAllStudent>=query}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{student={*=[[@1,8:8='*',<291>,1:8]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@1,8:8='*',<291>,1:8]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_union1={setop=EXCEPT, def_query0={query_dictionary={*=[[@1,8:8='*',<291>,1:8]]}, table_dictionary={student={*=[[@1,8:8='*',<291>,1:8]]}}, interface={*=[{name=*, table_ref=*}]}}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void numericLiteralParseTest() {
		// NUMBERS MISTAKEN FOR COLUMN NAMES; SHOULD notice context. Table names
		// can start with numbers, not column names
		// Scientific notation: space-separated (34.0 e+8) works via EXPONEN token;
		// non-spaced (34.0e+8) now works via Scientific_Numeric_Literal lexer rule
		final String query = " SELECT 123 as intgr, 56.98 as decml, 34.0 e+8 as expon from h.5463_77 ";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=intgr, literal=123}, 2={alias=decml, literal=56.98}, 3={alias=expon, literal=34.0e+8}}, from={table={schema=h, alias=null, table=5463_77}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[intgr, decml, expon]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{h.5463_77={}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={intgr=[[@3,15:19='intgr',<381>,1:15]], decml=[[@9,31:35='decml',<381>,1:31]], expon=[[@17,50:54='expon',<381>,1:50]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={intgr=[[@3,15:19='intgr',<381>,1:15]], decml=[[@9,31:35='decml',<381>,1:31]], expon=[[@17,50:54='expon',<381>,1:50]]}, table_dictionary={h.5463_77={}}, interface={intgr=[], decml=[], expon=[]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void numericLiteralNonSpacedScientificNotationParseTest() {
		// Standard scientific notation without spaces (34.0e+8) parsed via Scientific_Numeric_Literal lexer rule
		final String query = " SELECT 123 as intgr, 56.98 as decml, 34.0e+8 as expon from h.5463_77 ";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=intgr, literal=123}, 2={alias=decml, literal=56.98}, 3={alias=expon, literal=34.0e+8}}, from={table={schema=h, alias=null, table=5463_77}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[intgr, decml, expon]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{h.5463_77={}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={intgr=[[@3,15:19='intgr',<381>,1:15]], decml=[[@9,31:35='decml',<381>,1:31]], expon=[[@13,49:53='expon',<381>,1:49]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={intgr=[[@3,15:19='intgr',<381>,1:15]], decml=[[@9,31:35='decml',<381>,1:31]], expon=[[@13,49:53='expon',<381>,1:49]]}, table_dictionary={h.5463_77={}}, interface={intgr=[], decml=[], expon=[]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void simpleAndOrParseTest() {
		// gyg
		final String query = " SELECT scbcrse_subj_code FROM scbcrse " 
		+ "\n where a = b AND c=d  OR e=f and g=h ";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=scbcrse_subj_code, table_ref=null}}}, from={table={alias=null, table=scbcrse}}, where={or={1={and={1={condition={left={column={name=a, table_ref=null}}, right={column={name=b, table_ref=null}}, operator==}}, 2={condition={left={column={name=c, table_ref=null}}, right={column={name=d, table_ref=null}}, operator==}}}}, 2={and={1={condition={left={column={name=e, table_ref=null}}, right={column={name=f, table_ref=null}}, operator==}}, 2={condition={left={column={name=g, table_ref=null}}, right={column={name=h, table_ref=null}}, operator==}}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[scbcrse_subj_code]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{scbcrse={a=[[@5,47:47='a',<381>,2:7]], b=[[@7,51:51='b',<381>,2:11]], c=[[@9,57:57='c',<381>,2:17]], scbcrse_subj_code=[[@1,8:24='scbcrse_subj_code',<381>,1:8]], d=[[@11,59:59='d',<381>,2:19]], e=[[@13,65:65='e',<381>,2:25]], f=[[@15,67:67='f',<381>,2:27]], g=[[@17,73:73='g',<381>,2:33]], h=[[@19,75:75='h',<381>,2:35]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={scbcrse_subj_code=[[@1,8:24='scbcrse_subj_code',<381>,1:8]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={scbcrse_subj_code=[[@1,8:24='scbcrse_subj_code',<381>,1:8]]}, table_dictionary={scbcrse={a=[[@5,47:47='a',<381>,2:7]], b=[[@7,51:51='b',<381>,2:11]], c=[[@9,57:57='c',<381>,2:17]], scbcrse_subj_code=[[@1,8:24='scbcrse_subj_code',<381>,1:8]], d=[[@11,59:59='d',<381>,2:19]], e=[[@13,65:65='e',<381>,2:25]], f=[[@15,67:67='f',<381>,2:27]], g=[[@17,73:73='g',<381>,2:33]], h=[[@19,75:75='h',<381>,2:35]]}}, filters=[{name=a, table_ref=scbcrse}, {name=b, table_ref=scbcrse}, {name=c, table_ref=scbcrse}, {name=d, table_ref=scbcrse}, {name=e, table_ref=scbcrse}, {name=f, table_ref=scbcrse}, {name=g, table_ref=scbcrse}, {name=h, table_ref=scbcrse}], interface={scbcrse_subj_code=[{name=scbcrse_subj_code, table_ref=scbcrse}]}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void whereColumnEqualsAllSubqueryTest() {
		final String query = "SELECT a FROM tab1 WHERE tab1.a = ALL (SELECT b FROM tab2)";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		String ast = extractor.getAsTree().toString();
		Assert.assertTrue("AST should contain quantified ALL comparison", ast.contains("quantifier=ALL"));
		Assert.assertTrue("AST should contain = operator", ast.contains("operator=="));
		Assert.assertTrue("AST should contain inner subquery select", ast.contains("table=tab2"));
		Assert.assertEquals("Interface is wrong", "[a]", extractor.getInterface().toString());
	}

	@Test
	public void whereColumnEqualsAnySubqueryTest() {
		final String query = "SELECT a FROM tab1 WHERE tab1.a = ANY (SELECT b FROM tab2)";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		String ast = extractor.getAsTree().toString();
		Assert.assertTrue("AST should contain quantified ANY comparison", ast.contains("quantifier=ANY"));
		Assert.assertTrue("AST should contain inner subquery select", ast.contains("table=tab2"));
	}

	@Test
	public void whereColumnEqualsSomeSubqueryTest() {
		final String query = "SELECT a FROM tab1 WHERE tab1.a = SOME (SELECT b FROM tab2)";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		String ast = extractor.getAsTree().toString();
		Assert.assertTrue("AST should contain quantified SOME comparison", ast.contains("quantifier=SOME"));
	}

	@Test
	public void whereColumnNotEqualAllSubqueryTest() {
		final String query = "SELECT a FROM tab1 WHERE tab1.a != ALL (SELECT b FROM tab2)";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		String ast = extractor.getAsTree().toString();
		Assert.assertTrue("AST should contain quantified ALL with !=", ast.contains("quantifier=ALL"));
		Assert.assertTrue("AST should contain != operator", ast.contains("operator=!="));
	}

	@Test
	public void whereColumnGreaterThanAnySubqueryTest() {
		final String query = "SELECT a FROM tab1 WHERE tab1.a > ANY (SELECT b FROM tab2)";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		String ast = extractor.getAsTree().toString();
		Assert.assertTrue("AST should contain > ANY quantified comparison", ast.contains("quantifier=ANY"));
		Assert.assertTrue("AST should contain > operator", ast.contains("operator=>"));
	}

}
