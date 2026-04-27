package sql.walker;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import access.Snippet;
import errorhandling.ParseErrorCollector;
import errorhandling.ParseDiagnostic;
import errorhandling.ParseErrorListener;
import static mumble.SQLParserEndPoints.*;
import sql.SQLSelectParserParser;
import sql.SQLSelectParserParser.Column_valueContext;
import sql.SQLSelectParserParser.Condition_valueContext;
import sql.SQLSelectParserParser.In_list_predicate_valueContext;
import sql.SQLSelectParserParser.Join_extension_valueContext;
import sql.SQLSelectParserParser.Predicand_valueContext;
import sql.SQLSelectParserParser.Query_valueContext;
import sql.SQLSelectParserParser.SqlContext;
import sql.SQLSelectParserParser.Tuple_valueContext;
import sql.SQLSelectParserParser.Values_statement_endContext;
import sql.factory.SQLSelectParserFactory;

public class SqlInventoryJoinsAndTableResolutionTests extends AbstractSqlParseEventWalkerTest {

	@Test
	public void basicJoinWithOnTest() {
		final String query = " SELECT a.* FROM third a join fourth b on  a.a = b.b "; 

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=*, table_ref=a}}}, from={join={1={table={alias=a, table=third}}, 2={join=join, on={condition={left={column={name=a, table_ref=a}}, right={column={name=b, table_ref=b}}, operator==}}}, 3={table={alias=b, table=fourth}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{third={a=[[@11,43:43='a',<329>,1:43]], *=[[@1,8:8='a',<329>,1:8]]}, fourth={b=[[@15,49:49='b',<329>,1:49]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@3,10:10='*',<289>,1:10]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={query_dictionary={*=[[@3,10:10='*',<289>,1:10]]}, table_dictionary={third={a=[[@11,43:43='a',<329>,1:43]], *=[[@1,8:8='a',<329>,1:8]]}, fourth={b=[[@15,49:49='b',<329>,1:49]]}}, filters=[{name=a, table_ref=a}, {name=b, table_ref=b}], interface={*=[{name=*, table_ref=a}]}, table_alias={a=third, b=fourth}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void basicLeftJoinWithOnTest() {
		final String query = " SELECT a.* FROM third a left join fourth b on  a.a = b.b "; 

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=*, table_ref=a}}}, from={join={1={table={alias=a, table=third}}, 2={join=left, on={condition={left={column={name=a, table_ref=a}}, right={column={name=b, table_ref=b}}, operator==}}}, 3={table={alias=b, table=fourth}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{third={a=[[@12,48:48='a',<329>,1:48]], *=[[@1,8:8='a',<329>,1:8]]}, fourth={b=[[@16,54:54='b',<329>,1:54]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@3,10:10='*',<289>,1:10]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={query_dictionary={*=[[@3,10:10='*',<289>,1:10]]}, table_dictionary={third={a=[[@12,48:48='a',<329>,1:48]], *=[[@1,8:8='a',<329>,1:8]]}, fourth={b=[[@16,54:54='b',<329>,1:54]]}}, filters=[{name=a, table_ref=a}, {name=b, table_ref=b}], interface={*=[{name=*, table_ref=a}]}, table_alias={a=third, b=fourth}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void basicLeftJoinWithOnAndWhereTest() {
		final String query = " SELECT a.* FROM third a left join fourth b on  a.a = b.b "
				+ "\n where b.c = 1 "; 

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=*, table_ref=a}}}, from={join={1={table={alias=a, table=third}}, 2={join=left, on={condition={left={column={name=a, table_ref=a}}, right={column={name=b, table_ref=b}}, operator==}}}, 3={table={alias=b, table=fourth}}}}, where={condition={left={column={name=c, table_ref=b}}, right={literal=1}, operator==}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{third={a=[[@12,48:48='a',<329>,1:48]], *=[[@1,8:8='a',<329>,1:8]]}, fourth={b=[[@16,54:54='b',<329>,1:54]], c=[[@20,66:66='b',<329>,2:7]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@3,10:10='*',<289>,1:10]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={query_dictionary={*=[[@3,10:10='*',<289>,1:10]]}, table_dictionary={third={a=[[@12,48:48='a',<329>,1:48]], *=[[@1,8:8='a',<329>,1:8]]}, fourth={b=[[@16,54:54='b',<329>,1:54]], c=[[@20,66:66='b',<329>,2:7]]}}, filters=[{name=a, table_ref=a}, {name=b, table_ref=b}, {name=c, table_ref=b}], interface={*=[{name=*, table_ref=a}]}, table_alias={a=third, b=fourth}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void basicJoinWithOnParenthesisTest() {
		// Item 4 - Normal join ON Condition in parentheses should drop the parenthetical
		final String query = " SELECT a.* FROM third a join fourth b on (a.a = b.b)"; 

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=*, table_ref=a}}}, from={join={1={table={alias=a, table=third}}, 2={join=join, on={condition={left={column={name=a, table_ref=a}}, right={column={name=b, table_ref=b}}, operator==}}}, 3={table={alias=b, table=fourth}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{third={a=[[@12,43:43='a',<329>,1:43]], *=[[@1,8:8='a',<329>,1:8]]}, fourth={b=[[@16,49:49='b',<329>,1:49]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@3,10:10='*',<289>,1:10]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={query_dictionary={*=[[@3,10:10='*',<289>,1:10]]}, table_dictionary={third={a=[[@12,43:43='a',<329>,1:43]], *=[[@1,8:8='a',<329>,1:8]]}, fourth={b=[[@16,49:49='b',<329>,1:49]]}}, filters=[{name=a, table_ref=a}, {name=b, table_ref=b}], interface={*=[{name=*, table_ref=a}]}, table_alias={a=third, b=fourth}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void basicJoinWithOnOnConditionVariableTest() {
		// Item 46 - Condition Variable not typed or captured
		final String query = " SELECT a.* FROM third a join fourth b on <OnJoinCondition> "; 

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=*, table_ref=a}}}, from={join={1={table={alias=a, table=third}}, 2={join=join, on={substitution={name=<OnJoinCondition>, type=condition}}}, 3={table={alias=b, table=fourth}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<OnJoinCondition>=condition}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{third={*=[[@1,8:8='a',<329>,1:8]]}, fourth={}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@3,10:10='*',<289>,1:10]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={query_dictionary={*=[[@3,10:10='*',<289>,1:10]]}, table_dictionary={third={*=[[@1,8:8='a',<329>,1:8]]}, fourth={}}, filters=[], interface={*=[{name=*, table_ref=a}]}, table_alias={a=third, b=fourth}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void basicJoinWithOnConditionVariableInParenthesisTest() {
		//  Item 47 - Condition Variable in parenthetical ON statement not typed or captured
		final String query = " SELECT a.* FROM third a join fourth b on (<OnJoinCondition>)"; 

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=*, table_ref=a}}}, from={join={1={table={alias=a, table=third}}, 2={join=join, on={substitution={name=<OnJoinCondition>, type=condition}}}, 3={table={alias=b, table=fourth}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<OnJoinCondition>=condition}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{third={*=[[@1,8:8='a',<329>,1:8]]}, fourth={}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@3,10:10='*',<289>,1:10]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={query_dictionary={*=[[@3,10:10='*',<289>,1:10]]}, table_dictionary={third={*=[[@1,8:8='a',<329>,1:8]]}, fourth={}}, filters=[], interface={*=[{name=*, table_ref=a}]}, table_alias={a=third, b=fourth}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void basicJoinWithOnTwoConditionVariablesTest() {
		//  Condition Variables in an AND clause are labeled and captured correctly
		final String query = " SELECT a.* FROM third a join fourth b on <OnJoinCondition> and <OtherJoinCondition>"; 

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=*, table_ref=a}}}, from={join={1={table={alias=a, table=third}}, 2={join=join, on={and={1={substitution={name=<OnJoinCondition>, type=condition}}, 2={substitution={name=<OtherJoinCondition>, type=condition}}}}}, 3={table={alias=b, table=fourth}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<OtherJoinCondition>=condition, <OnJoinCondition>=condition}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{third={*=[[@1,8:8='a',<329>,1:8]]}, fourth={}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@3,10:10='*',<289>,1:10]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={query_dictionary={*=[[@3,10:10='*',<289>,1:10]]}, table_dictionary={third={*=[[@1,8:8='a',<329>,1:8]]}, fourth={}}, filters=[], interface={*=[{name=*, table_ref=a}]}, table_alias={a=third, b=fourth}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void joinQualifiedWithTupleVariableT1() {
		// ITEM 34 - Qualified Joins (e.g., cross, natural, union) do not parse when tuple substitution variables are included
		final String query = " SELECT * FROM <tuple1> as T3 join fourth as F4";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=*, table_ref=*}}}, from={join={1={table={alias=T3, substitution={name=<tuple1>, type=tuple}}}, 2={join=join}, 3={table={alias=F4, table=fourth}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<tuple1>=tuple}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{<tuple1>={*=[[@1,8:8='*',<289>,1:8]]}, fourth={*=[[@1,8:8='*',<289>,1:8]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@1,8:8='*',<289>,1:8]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={query_dictionary={*=[[@1,8:8='*',<289>,1:8]]}, table_dictionary={<tuple1>={*=[[@1,8:8='*',<289>,1:8]]}, fourth={*=[[@1,8:8='*',<289>,1:8]]}}, interface={*=[{name=*, table_ref=*}]}, table_alias={F4=fourth, T3=<tuple1>}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void joinQualifiedWithTupleVariableT2() {
		// ITEM 34 - Qualified Joins (e.g., left) do not parse when tuple substitution variables are included
		final String query = " SELECT * FROM <tuple1> as T3 left outer join fourth as F4";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=*, table_ref=*}}}, from={join={1={table={alias=T3, substitution={name=<tuple1>, type=tuple}}}, 2={join=leftouter}, 3={table={alias=F4, table=fourth}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<tuple1>=tuple}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{<tuple1>={*=[[@1,8:8='*',<289>,1:8]]}, fourth={*=[[@1,8:8='*',<289>,1:8]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@1,8:8='*',<289>,1:8]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={query_dictionary={*=[[@1,8:8='*',<289>,1:8]]}, table_dictionary={<tuple1>={*=[[@1,8:8='*',<289>,1:8]]}, fourth={*=[[@1,8:8='*',<289>,1:8]]}}, interface={*=[{name=*, table_ref=*}]}, table_alias={F4=fourth, T3=<tuple1>}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void joinUnQualifiedWithTupleVariableT2() {
		// ITEM 34 - Qualified Joins (e.g., cross, natural, union) do not parse when tuple substitution variables are included
		final String query = " SELECT * FROM <tuple1> as T3 cross join fourth as F4";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=*, table_ref=*}}}, from={join={1={table={alias=T3, substitution={name=<tuple1>, type=tuple}}}, 2={join=crossjoin}, 3={table={alias=F4, table=fourth}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<tuple1>=tuple}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{<tuple1>={*=[[@1,8:8='*',<289>,1:8]]}, fourth={*=[[@1,8:8='*',<289>,1:8]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@1,8:8='*',<289>,1:8]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={query_dictionary={*=[[@1,8:8='*',<289>,1:8]]}, table_dictionary={<tuple1>={*=[[@1,8:8='*',<289>,1:8]]}, fourth={*=[[@1,8:8='*',<289>,1:8]]}}, interface={*=[{name=*, table_ref=*}]}, table_alias={F4=fourth, T3=<tuple1>}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void joinUnQualifiedWithTupleVariableT3() {
		// ITEM 34 - Qualified Joins (e.g., cross, natural, union) do not parse when tuple substitution variables are included
		final String query = " SELECT * FROM <tuple1> as T3 natural join fourth as F4";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=*, table_ref=*}}}, from={join={1={table={alias=T3, substitution={name=<tuple1>, type=tuple}}}, 2={join=naturaljoin}, 3={table={alias=F4, table=fourth}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<tuple1>=tuple}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{<tuple1>={*=[[@1,8:8='*',<289>,1:8]]}, fourth={*=[[@1,8:8='*',<289>,1:8]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@1,8:8='*',<289>,1:8]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={query_dictionary={*=[[@1,8:8='*',<289>,1:8]]}, table_dictionary={<tuple1>={*=[[@1,8:8='*',<289>,1:8]]}, fourth={*=[[@1,8:8='*',<289>,1:8]]}}, interface={*=[{name=*, table_ref=*}]}, table_alias={F4=fourth, T3=<tuple1>}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void joinUnQualifiedWithTupleVariableT4() {
		// ITEM 34 - Qualified Joins (e.g., cross, natural, union) do not parse when tuple substitution variables are included
		final String query = " SELECT * FROM <tuple1> as T3 union join fourth as F4";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=*, table_ref=*}}}, from={join={1={table={alias=T3, substitution={name=<tuple1>, type=tuple}}}, 2={join=unionjoin}, 3={table={alias=F4, table=fourth}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<tuple1>=tuple}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{<tuple1>={*=[[@1,8:8='*',<289>,1:8]]}, fourth={*=[[@1,8:8='*',<289>,1:8]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@1,8:8='*',<289>,1:8]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={query_dictionary={*=[[@1,8:8='*',<289>,1:8]]}, table_dictionary={<tuple1>={*=[[@1,8:8='*',<289>,1:8]]}, fourth={*=[[@1,8:8='*',<289>,1:8]]}}, interface={*=[{name=*, table_ref=*}]}, table_alias={F4=fourth, T3=<tuple1>}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void handlingSelectStarWithExplicitTableRef1() {
		final String query = " SELECT t3.* FROM <tuple1> as T3 union join fourth as F4";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=*, table_ref=t3}}}, from={join={1={table={alias=T3, substitution={name=<tuple1>, type=tuple}}}, 2={join=unionjoin}, 3={table={alias=F4, table=fourth}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<tuple1>=tuple}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{<tuple1>={*=[[@1,8:9='t3',<329>,1:8]]}, fourth={}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@3,11:11='*',<289>,1:11]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={query_dictionary={*=[[@3,11:11='*',<289>,1:11]]}, table_dictionary={<tuple1>={*=[[@1,8:9='t3',<329>,1:8]]}, fourth={}}, interface={*=[{name=*, table_ref=t3}]}, table_alias={F4=fourth, T3=<tuple1>}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void handlingRepeatingColumnNamesInTheInterfaceV1() {
		final String query = " SELECT T3.col1, F4.col1 FROM third as T3 join fourth as F4";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=col1, table_ref=T3}}, 2={column={name=col1, table_ref=F4}}}, from={join={1={table={alias=T3, table=third}}, 2={join=join}, 3={table={alias=F4, table=fourth}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[col1]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{third={col1=[[@1,8:9='T3',<329>,1:8]]}, fourth={col1=[[@5,17:18='F4',<329>,1:17]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={col1=[[@3,11:14='col1',<329>,1:11], [@7,20:23='col1',<329>,1:20]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Tree is wrong", "{query0={query_dictionary={col1=[[@3,11:14='col1',<329>,1:11], [@7,20:23='col1',<329>,1:20]]}, table_dictionary={third={}, fourth={col1=[[@5,17:18='F4',<329>,1:17]]}}, interface={col1=[{name=col1, table_ref=F4}]}, table_alias={F4=fourth, T3=third}}}",
				extractor.getSymbolTable().toString());

		Snippet snippet = extractor.getSnippet();
		assertFatalDiagnosticAtPosition(snippet, "DUPLICATE_INTERFACE_COLUMNS",
				"Duplicate interface columns defined: T3.col1 at (l:1 c:11) and F4.col1 at (l:1 c:20).",
				"T3.col1,F4.col1", 1, 11);
		// assertFatalDiagnosticAtPosition(snippet, "QUALIFIED_COLUMN_NOT_FOUND_IN_TABLE",
		// 		"Source Table not found for Column 'col1' at (l:1 c:8). No alias or table called 'T3'.",
		// 		"col1", 1, 8);
	}


	@Test
	public void handlingRepeatingColumnNamesInTheInterfaceV2() {
		final String query = " SELECT col1, T3.col1, F4.col1 FROM third as T3 join fourth as F4";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=col1, table_ref=null}}, 2={column={name=col1, table_ref=T3}}, 3={column={name=col1, table_ref=F4}}}, from={join={1={table={alias=T3, table=third}}, 2={join=join}, 3={table={alias=F4, table=fourth}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[col1]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{third={col1=[[@3,14:15='T3',<329>,1:14]]}, fourth={col1=[[@7,23:24='F4',<329>,1:23]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={col1=[[@1,8:11='col1',<329>,1:8], [@5,17:20='col1',<329>,1:17], [@9,26:29='col1',<329>,1:26]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Tree is wrong", "{query0={query_dictionary={col1=[[@1,8:11='col1',<329>,1:8], [@5,17:20='col1',<329>,1:17], [@9,26:29='col1',<329>,1:26]]}, table_dictionary={third={}, fourth={col1=[[@7,23:24='F4',<329>,1:23]]}}, interface={col1=[{name=col1, table_ref=F4}]}, table_alias={F4=fourth, T3=third}}}",
				extractor.getSymbolTable().toString());

		Snippet snippet = extractor.getSnippet();
		assertFatalDiagnosticAtPosition(snippet, "DUPLICATE_INTERFACE_COLUMNS",
				"Duplicate interface columns defined: col1 at (l:1 c:8) and T3.col1 at (l:1 c:17).",
				"col1,T3.col1", 1, 8);
		assertFatalDiagnosticAtPosition(snippet, "DUPLICATE_INTERFACE_COLUMNS",
				"Duplicate interface columns defined: T3.col1 at (l:1 c:8) and F4.col1 at (l:1 c:26).",
				"T3.col1,F4.col1", 1, 8);
		assertFatalDiagnosticCount(snippet,
				"DUPLICATE_INTERFACE_COLUMNS",
				"Duplicate interface columns defined",
				null,
				2);
		assertDiagnosticAtPosition(snippet, "UNRESOLVED_UNQUALIFIED_COLUMNS", ParseDiagnostic.Severity.ERROR,
				"Unresolved unqualified column reference(s): [col1",
				"col1", 1, 8);
		assertDiagnosticCountBySeverity(snippet,
				"UNRESOLVED_UNQUALIFIED_COLUMNS",
				ParseDiagnostic.Severity.ERROR,
				"Unresolved unqualified column reference(s)",
				"col1",
				1);
	}


	@Test
	public void handlingRepeatingColumnNamesInTheInterfaceV3() {
		final String query = " SELECT T3.col1, (F4.x + T3.y) col1 FROM third as T3 join fourth as F4";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=col1, table_ref=T3}}, 2={parentheses={calc={left={column={name=x, table_ref=F4}}, right={column={name=y, table_ref=T3}}, operator=+}}, alias=col1}}, from={join={1={table={alias=T3, table=third}}, 2={join=join}, 3={table={alias=F4, table=fourth}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[col1]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{third={y=[[@10,25:26='T3',<329>,1:25]], col1=[[@1,8:9='T3',<329>,1:8]]}, fourth={x=[[@6,18:19='F4',<329>,1:18]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={col1=[[@3,11:14='col1',<329>,1:11], [@14,31:34='col1',<329>,1:31]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Tree is wrong", "{query0={query_dictionary={col1=[[@3,11:14='col1',<329>,1:11], [@14,31:34='col1',<329>,1:31]]}, table_dictionary={third={y=[[@10,25:26='T3',<329>,1:25]]}, fourth={x=[[@6,18:19='F4',<329>,1:18]]}}, interface={col1=[{name=x, table_ref=F4}, {name=y, table_ref=T3}]}, table_alias={F4=fourth, T3=third}}}",
				extractor.getSymbolTable().toString());

		Snippet snippet = extractor.getSnippet();
		assertFatalDiagnosticAtPosition(snippet, "DUPLICATE_INTERFACE_COLUMNS",
				"Duplicate interface columns defined: T3.col1 at (l:1 c:11) and F4.x at (l:1 c:31).",
				"T3.col1,F4.x", 1, 11);
		// assertFatalDiagnosticAtPosition(snippet, "QUALIFIED_COLUMN_NOT_FOUND_IN_TABLE",
		// 		"Source Table not found for Column 'col1' at (l:1 c:8). No alias or table called 'T3'.",
		// 		"col1", 1, 8);
	}


	@Test
	public void joinSubqueryTableV1() {
		// ITEM 92 - Join with subquery and table shouldn't need on statement
		final String query = " SELECT * FROM (select * from third) as T3 join fourth as F4";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=*, table_ref=*}}}, from={join={1={table={alias=T3, query={select={1={column={name=*, table_ref=*}}}, from={table={alias=null, table=third}}}}}, 2={join=join}, 3={table={alias=F4, table=fourth}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{third={*=[[@5,23:23='*',<289>,1:23]]}, fourth={*=[[@1,8:8='*',<289>,1:8]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@5,23:23='*',<289>,1:23]]}, query1={*=[[@1,8:8='*',<289>,1:8]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query1={query_dictionary={*=[[@1,8:8='*',<289>,1:8]]}, table_dictionary={fourth={*=[[@1,8:8='*',<289>,1:8]]}}, def_query0={query_dictionary={*=[[@5,23:23='*',<289>,1:23]]}, table_dictionary={third={*=[[@5,23:23='*',<289>,1:23]]}}, interface={*=[{name=*, table_ref=*}]}}, interface={*=[{name=*, table_ref=*}]}, table_alias={F4=fourth, T3=query0}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void joinSubqueryWithColumnsTableV1() {
		// ITEM 92 - Join with subquery and table shouldn't need on statement
		final String query = " SELECT T3.transcol as outercol, F4.tablecol FROM (select innercol as transcol, othercol from third) as T3 join fourth as F4";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=transcol, table_ref=T3}, alias=outercol}, 2={column={name=tablecol, table_ref=F4}}}, from={join={1={table={alias=T3, query={select={1={column={name=innercol, table_ref=null}, alias=transcol}, 2={column={name=othercol, table_ref=null}}}, from={table={alias=null, table=third}}}}}, 2={join=join}, 3={table={alias=F4, table=fourth}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[tablecol, outercol]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{third={othercol=[[@17,80:87='othercol',<329>,1:80]], innercol=[[@13,58:65='innercol',<329>,1:58]]}, fourth={tablecol=[[@7,33:34='F4',<329>,1:33]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={transcol=[[@15,70:77='transcol',<329>,1:70], [@1,8:9='T3',<329>,1:8]], othercol=[[@17,80:87='othercol',<329>,1:80]]}, query1={tablecol=[[@9,36:43='tablecol',<329>,1:36]], outercol=[[@5,23:30='outercol',<329>,1:23]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query1={query_dictionary={tablecol=[[@9,36:43='tablecol',<329>,1:36]], outercol=[[@5,23:30='outercol',<329>,1:23]]}, table_dictionary={fourth={tablecol=[[@7,33:34='F4',<329>,1:33]]}}, def_query0={query_dictionary={transcol=[[@15,70:77='transcol',<329>,1:70], [@1,8:9='T3',<329>,1:8]], othercol=[[@17,80:87='othercol',<329>,1:80]]}, table_dictionary={third={othercol=[[@17,80:87='othercol',<329>,1:80]], innercol=[[@13,58:65='innercol',<329>,1:58]]}}, interface={transcol=[{name=innercol, table_ref=third}], othercol=[{name=othercol, table_ref=third}]}}, interface={tablecol=[{name=tablecol, table_ref=F4}], outercol=[{name=transcol, table_ref=T3}]}, table_alias={F4=fourth, T3=query0}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void joinSubqueryTableV2() {
		// ITEM 92 - Join with subquery and table shouldn't need on statement
		final String query = " SELECT * FROM (select * from third) as T3 cross join fourth as F4";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=*, table_ref=*}}}, from={join={1={table={alias=T3, query={select={1={column={name=*, table_ref=*}}}, from={table={alias=null, table=third}}}}}, 2={join=crossjoin}, 3={table={alias=F4, table=fourth}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{third={*=[[@5,23:23='*',<289>,1:23]]}, fourth={*=[[@1,8:8='*',<289>,1:8]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@5,23:23='*',<289>,1:23]]}, query1={*=[[@1,8:8='*',<289>,1:8]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query1={query_dictionary={*=[[@1,8:8='*',<289>,1:8]]}, table_dictionary={fourth={*=[[@1,8:8='*',<289>,1:8]]}}, def_query0={query_dictionary={*=[[@5,23:23='*',<289>,1:23]]}, table_dictionary={third={*=[[@5,23:23='*',<289>,1:23]]}}, interface={*=[{name=*, table_ref=*}]}}, interface={*=[{name=*, table_ref=*}]}, table_alias={F4=fourth, T3=query0}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void fromListTest() {
		final String query = " SELECT * FROM third ";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=*, table_ref=*}}}, from={table={alias=null, table=third}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{third={*=[[@1,8:8='*',<289>,1:8]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@1,8:8='*',<289>,1:8]]}}",
						extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={query_dictionary={*=[[@1,8:8='*',<289>,1:8]]}, table_dictionary={third={*=[[@1,8:8='*',<289>,1:8]]}}, interface={*=[{name=*, table_ref=*}]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void tableListWithTupleVariableV1() {
		final String query = " SELECT * FROM third, <tuple variable> as two ";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=*, table_ref=*}}}, from={join={1={table={alias=null, table=third}}, 2={table={alias=two, substitution={name=<tuple variable>, type=tuple}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<tuple variable>=tuple}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{<tuple variable>={*=[[@1,8:8='*',<289>,1:8]]}, third={*=[[@1,8:8='*',<289>,1:8]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@1,8:8='*',<289>,1:8]]}}",
						extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={query_dictionary={*=[[@1,8:8='*',<289>,1:8]]}, table_dictionary={<tuple variable>={*=[[@1,8:8='*',<289>,1:8]]}, third={*=[[@1,8:8='*',<289>,1:8]]}}, interface={*=[{name=*, table_ref=*}]}, table_alias={two=<tuple variable>}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void oneTableWithJoinExtensionVariableV1() {
		//  ITEM 17 - Doesn't recognize optional join tree additions after the on clause
		final String query = " SELECT * FROM third <extension> ";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=*, table_ref=*}}}, from={extension={substitution={name=<extension>, type=join_extension}}, table={alias=null, table=third}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<extension>=join_extension}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{third={*=[[@1,8:8='*',<289>,1:8]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@1,8:8='*',<289>,1:8]]}}",
						extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={query_dictionary={*=[[@1,8:8='*',<289>,1:8]]}, table_dictionary={third={*=[[@1,8:8='*',<289>,1:8]]}}, interface={*=[{name=*, table_ref=*}]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void joinlessJoinExtensionVariableV1() {
		//  ITEM 17 - Doesn't recognize optional join tree additions after the on clause
		final String query = " SELECT * FROM third as T3, fourth as F4 <extension> ";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=*, table_ref=*}}}, from={extension={substitution={name=<extension>, type=join_extension}}, join={1={table={alias=T3, table=third}}, 2={table={alias=F4, table=fourth}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<extension>=join_extension}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{third={*=[[@1,8:8='*',<289>,1:8]]}, fourth={*=[[@1,8:8='*',<289>,1:8]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@1,8:8='*',<289>,1:8]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={query_dictionary={*=[[@1,8:8='*',<289>,1:8]]}, table_dictionary={third={*=[[@1,8:8='*',<289>,1:8]]}, fourth={*=[[@1,8:8='*',<289>,1:8]]}}, interface={*=[{name=*, table_ref=*}]}, table_alias={F4=fourth, T3=third}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void joinExtensionVariableV1() {
		//  ITEM 17 - Doesn't recognize optional join tree additions after the on clause
		final String query = " SELECT * FROM third as T3 join fourth as F4 on <third_fourth_join_condition> <extension> ";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=*, table_ref=*}}}, from={extension={substitution={name=<extension>, type=join_extension}}, join={1={table={alias=T3, table=third}}, 2={join=join, on={substitution={name=<third_fourth_join_condition>, type=condition}}}, 3={table={alias=F4, table=fourth}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<third_fourth_join_condition>=condition, <extension>=join_extension}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{third={*=[[@1,8:8='*',<289>,1:8]]}, fourth={*=[[@1,8:8='*',<289>,1:8]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@1,8:8='*',<289>,1:8]]}}",
						extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={query_dictionary={*=[[@1,8:8='*',<289>,1:8]]}, table_dictionary={third={*=[[@1,8:8='*',<289>,1:8]]}, fourth={*=[[@1,8:8='*',<289>,1:8]]}}, filters=[], interface={*=[{name=*, table_ref=*}]}, table_alias={F4=fourth, T3=third}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void joinWithDuplicateColumnNameTest() {
		// Because of the join, all of the columns in the outermost query could be in the <Guide> table,
		// but also appear to be accounted for as individual columns in the subquery 0. Hence
		// Table dictionary doesn't definitively record them in the <Guide> table dictionary
		// and the query issues ambiguous column warnings
		final String query = "SELECT 'Guide' AS app_name,  category, is_active, nk, rank, desc, student " + 
				"FROM  <Guide> AS Guide_Student_Conditions " + 
				"\n join " + 
				"\n (SELECT 'Nav' AS app_name, category, is_active, nk, rank, desc, student " + 
				"FROM <NAV> AS  Nav_Ss) AS Nav_Student_Conditions on 1=1";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=app_name, literal='Guide'}, 2={column={name=category, table_ref=null}}, 3={column={name=is_active, table_ref=null}}, 4={column={name=nk, table_ref=null}}, 5={column={name=rank, table_ref=null}}, 6={column={name=desc, table_ref=null}}, 7={column={name=student, table_ref=null}}}, from={join={1={table={alias=Guide_Student_Conditions, substitution={name=<Guide>, type=tuple}}}, 2={join=join, on={condition={left={literal=1}, right={literal=1}, operator==}}}, 3={table={alias=Nav_Student_Conditions, query={select={1={alias=app_name, literal='Nav'}, 2={column={name=category, table_ref=null}}, 3={column={name=is_active, table_ref=null}}, 4={column={name=nk, table_ref=null}}, 5={column={name=rank, table_ref=null}}, 6={column={name=desc, table_ref=null}}, 7={column={name=student, table_ref=null}}}, from={table={alias=Nav_Ss, substitution={name=<NAV>, type=tuple}}}}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[app_name, is_active, student, rank, category, nk, desc]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<Guide>=tuple, <NAV>=tuple}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{<Guide>={}, <NAV>={is_active=[[@29,162:170='is_active',<329>,3:38]], student=[[@37,189:195='student',<329>,3:65]], rank=[[@33,177:180='rank',<127>,3:53]], category=[[@27,152:159='category',<329>,3:28]], nk=[[@31,173:174='nk',<329>,3:49]], desc=[[@35,183:186='desc',<76>,3:59]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={app_name=[[@25,142:149='app_name',<329>,3:18]], is_active=[[@29,162:170='is_active',<329>,3:38]], student=[[@37,189:195='student',<329>,3:65]], rank=[[@33,177:180='rank',<127>,3:53]], category=[[@27,152:159='category',<329>,3:28]], nk=[[@31,173:174='nk',<329>,3:49]], desc=[[@35,183:186='desc',<76>,3:59]]}, query1={app_name=[[@3,18:25='app_name',<329>,1:18]], is_active=[[@7,39:47='is_active',<329>,1:39]], student=[[@15,66:72='student',<329>,1:66]], rank=[[@11,54:57='rank',<127>,1:54]], category=[[@5,29:36='category',<329>,1:29]], nk=[[@9,50:51='nk',<329>,1:50]], desc=[[@13,60:63='desc',<76>,1:60]]}}",
						extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query1={query_dictionary={app_name=[[@3,18:25='app_name',<329>,1:18]], is_active=[[@7,39:47='is_active',<329>,1:39]], student=[[@15,66:72='student',<329>,1:66]], rank=[[@11,54:57='rank',<127>,1:54]], category=[[@5,29:36='category',<329>,1:29]], nk=[[@9,50:51='nk',<329>,1:50]], desc=[[@13,60:63='desc',<76>,1:60]]}, table_dictionary={<Guide>={}}, def_query0={query_dictionary={app_name=[[@25,142:149='app_name',<329>,3:18]], is_active=[[@29,162:170='is_active',<329>,3:38]], student=[[@37,189:195='student',<329>,3:65]], rank=[[@33,177:180='rank',<127>,3:53]], category=[[@27,152:159='category',<329>,3:28]], nk=[[@31,173:174='nk',<329>,3:49]], desc=[[@35,183:186='desc',<76>,3:59]]}, table_dictionary={<NAV>={is_active=[[@29,162:170='is_active',<329>,3:38]], student=[[@37,189:195='student',<329>,3:65]], rank=[[@33,177:180='rank',<127>,3:53]], category=[[@27,152:159='category',<329>,3:28]], nk=[[@31,173:174='nk',<329>,3:49]], desc=[[@35,183:186='desc',<76>,3:59]]}}, interface={app_name=[], is_active=[{name=is_active, table_ref=<NAV>}], student=[{name=student, table_ref=<NAV>}], rank=[{name=rank, table_ref=<NAV>}], category=[{name=category, table_ref=<NAV>}], nk=[{name=nk, table_ref=<NAV>}], desc=[{name=desc, table_ref=<NAV>}]}, table_alias={Nav_Ss=<NAV>}}, filters=[], interface={app_name=[], is_active=[{name=is_active, table_ref=null}], student=[{name=student, table_ref=null}], rank=[{name=rank, table_ref=null}], category=[{name=category, table_ref=null}], nk=[{name=nk, table_ref=null}], desc=[{name=desc, table_ref=null}]}, table_alias={Guide_Student_Conditions=<Guide>, Nav_Student_Conditions=query0}}}",
				extractor.getSymbolTable().toString());
		Snippet snippet = extractor.getSnippet();
		assertDiagnosticCountBySeverity(
				snippet,
				"UNRESOLVED_UNQUALIFIED_COLUMNS",
				ParseDiagnostic.Severity.ERROR,
				null,
				null,
				1);
		assertDiagnosticCountBySeverity(
				snippet,
				"AMBIGUOUS_COLUMN_REFERENCE",
				ParseDiagnostic.Severity.SEVERE_WARNING,
				null,
				null,
				6);

	}


	@Test
	public void simpleQuotedTableNameTest() {
		final String query = "SELECT * FROM \"Name\"";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=*, table_ref=*}}}, from={table={alias=null, table=\"Name\"}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{\"Name\"={*=[[@1,7:7='*',<289>,1:7]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@1,7:7='*',<289>,1:7]]}}",
						extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={query_dictionary={*=[[@1,7:7='*',<289>,1:7]]}, table_dictionary={\"Name\"={*=[[@1,7:7='*',<289>,1:7]]}}, interface={*=[{name=*, table_ref=*}]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void simpleQuotedSchemaAndTableNameTest() {
		final String query = "SELECT * FROM \"scheme\".\"Name\"";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=*, table_ref=*}}}, from={table={schema=\"scheme\", alias=null, table=\"Name\"}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{\"scheme\".\"Name\"={*=[[@1,7:7='*',<289>,1:7]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@1,7:7='*',<289>,1:7]]}}",
						extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={query_dictionary={*=[[@1,7:7='*',<289>,1:7]]}, table_dictionary={\"scheme\".\"Name\"={*=[[@1,7:7='*',<289>,1:7]]}}, interface={*=[{name=*, table_ref=*}]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void simpleQuotedDatabaseSchemaAndTableNameTest() {
		final String query = "SELECT * FROM \"db\".\"scheme\".\"Name\"";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=*, table_ref=*}}}, from={table={schema=\"scheme\", dbname=\"db\", alias=null, table=\"Name\"}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{\"db\".\"scheme\".\"Name\"={*=[[@1,7:7='*',<289>,1:7]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@1,7:7='*',<289>,1:7]]}}",
						extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={query_dictionary={*=[[@1,7:7='*',<289>,1:7]]}, table_dictionary={\"db\".\"scheme\".\"Name\"={*=[[@1,7:7='*',<289>,1:7]]}}, interface={*=[{name=*, table_ref=*}]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void quotedGuidDatabaseNameUnquotedSchemaAndUnquotedTableNameTest() {
		final String query = "SELECT * FROM \"PROD-3beb02cb-f710-4d2d-a6a1-40c229e4a40e\".panto.\"1234_987654\"";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=*, table_ref=*}}}, from={table={schema=panto, dbname=\"PROD-3beb02cb-f710-4d2d-a6a1-40c229e4a40e\", alias=null, table=\"1234_987654\"}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{\"PROD-3beb02cb-f710-4d2d-a6a1-40c229e4a40e\".panto.\"1234_987654\"={*=[[@1,7:7='*',<289>,1:7]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@1,7:7='*',<289>,1:7]]}}",
						extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={query_dictionary={*=[[@1,7:7='*',<289>,1:7]]}, table_dictionary={\"PROD-3beb02cb-f710-4d2d-a6a1-40c229e4a40e\".panto.\"1234_987654\"={*=[[@1,7:7='*',<289>,1:7]]}}, interface={*=[{name=*, table_ref=*}]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void simpleQuotedColumnNameTest() {
		final String query = "SELECT \"ColUmn_Name\" as cOl1, Col2 FROM \"Name\" where \"cOlumn_nAME\" > 5 and cOL2 < 10";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=\"ColUmn_Name\", table_ref=null}, alias=cOl1}, 2={column={name=Col2, table_ref=null}}}, from={table={alias=null, table=\"Name\"}}, where={and={1={condition={left={column={name=\"cOlumn_nAME\", table_ref=null}}, right={literal=5}, operator=>}}, 2={condition={left={column={name=cOL2, table_ref=null}}, right={literal=10}, operator=<}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[Col2, cOl1]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		// Quoted column names are case-sensitive: "ColUmn_Name" and "cOlumn_nAME" produce two distinct entries.
		// Unquoted column names are case-insensitive: Col2 and cOL2 merge into one entry (col2) with two token refs.
		Assert.assertEquals("Table Dictionary is wrong",
				"{\"Name\"={Col2=[[@5,30:33='Col2',<329>,1:30], [@13,75:78='cOL2',<329>,1:75]], \"ColUmn_Name\"=[[@1,7:19='\"ColUmn_Name\"',<329>,1:7]], \"cOlumn_nAME\"=[[@9,53:65='\"cOlumn_nAME\"',<329>,1:53]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{query0={Col2=[[@5,30:33='Col2',<329>,1:30]], cOl1=[[@3,24:27='cOl1',<329>,1:24]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{query0={query_dictionary={Col2=[[@5,30:33='Col2',<329>,1:30]], cOl1=[[@3,24:27='cOl1',<329>,1:24]]}, table_dictionary={\"Name\"={Col2=[[@5,30:33='Col2',<329>,1:30], [@13,75:78='cOL2',<329>,1:75]], \"ColUmn_Name\"=[[@1,7:19='\"ColUmn_Name\"',<329>,1:7]], \"cOlumn_nAME\"=[[@9,53:65='\"cOlumn_nAME\"',<329>,1:53]]}}, filters=[{name=\"cOlumn_nAME\", table_ref=\"Name\"}, {name=cOL2, table_ref=\"Name\"}], interface={Col2=[{name=Col2, table_ref=\"Name\"}], cOl1=[{name=\"ColUmn_Name\", table_ref=\"Name\"}]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void variation4ColumnVariableTest() {
		// 3 nested subqueries with correlated subquery at each nested query
		// under join with column variable in middle subquery
		final String query = "select apple from "
			+ "\n (SELECT apple from "
			+" \n (SELECT apple, banana from tab1 where tab2.<other> > 20) b "
			+ "\n where tab2.<middle>) a"
			+ "\n join tab2 on a.apple = tab2.pickle";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=apple, table_ref=null}}}, from={join={1={table={alias=a, query={select={1={column={name=apple, table_ref=null}}}, from={table={alias=b, query={select={1={column={name=apple, table_ref=null}}, 2={column={name=banana, table_ref=null}}}, from={table={alias=null, table=tab1}}, where={condition={left={column={substitution={name=<other>, type=column}, table_ref=tab2}}, right={literal=20}, operator=>}}}}}, where={column={substitution={name=<middle>, type=column}, table_ref=tab2}}}}}, 2={join=join, on={condition={left={column={name=apple, table_ref=a}}, right={column={name=pickle, table_ref=tab2}}, operator==}}}, 3={table={alias=null, table=tab2}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[apple]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<middle>=column, <other>=column}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={banana=[[@11,57:62='banana',<329>,3:16]], apple=[[@9,50:54='apple',<329>,3:9]]}, tab2={<middle>=[[@23,109:112='tab2',<329>,4:7]], <other>=[[@15,80:83='tab2',<329>,3:39]], pickle=[[@35,150:153='tab2',<329>,5:24]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={banana=[[@11,57:62='banana',<329>,3:16]], apple=[[@9,50:54='apple',<329>,3:9]]}, query1={apple=[[@5,28:32='apple',<329>,2:9], [@31,140:140='a',<329>,5:14]]}, query2={apple=[[@1,7:11='apple',<329>,1:7]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query2={query_dictionary={apple=[[@1,7:11='apple',<329>,1:7]]}, table_dictionary={tab2={pickle=[[@35,150:153='tab2',<329>,5:24]]}}, def_query1={query_dictionary={apple=[[@5,28:32='apple',<329>,2:9], [@31,140:140='a',<329>,5:14]]}, table_dictionary={}, def_query0={query_dictionary={banana=[[@11,57:62='banana',<329>,3:16]], apple=[[@9,50:54='apple',<329>,3:9]]}, table_dictionary={tab1={banana=[[@11,57:62='banana',<329>,3:16]], apple=[[@9,50:54='apple',<329>,3:9]]}}, filters=[{substitution={name=<other>, type=column}, table_ref=tab2}], interface={banana=[{name=banana, table_ref=tab1}], apple=[{name=apple, table_ref=tab1}]}}, filters=[{substitution={name=<middle>, type=column}, table_ref=tab2}], interface={apple=[{name=apple, table_ref=query0}]}, table_alias={b=query0}}, filters=[{name=apple, table_ref=a}, {name=pickle, table_ref=tab2}], interface={apple=[{name=apple, table_ref=query1}]}, table_alias={a=query1}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void variation4_2ColumnVariableTest() {
		// 3 nested subqueries with correlated subquery at each nested query
		// under join with column variable in middle subquery but repeat of tab2.apple
		// at the top level join.
		final String query = "select apple from "
			+ "\n (SELECT apple from "
			+" \n (SELECT apple, banana from tab1 where tab2.<other> > 20) b "
			+ "\n where tab2.<middle>) a"
			+ "\n join tab2 on a.apple = tab2.apple";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoFatalErrors(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=apple, table_ref=null}}}, from={join={1={table={alias=a, query={select={1={column={name=apple, table_ref=null}}}, from={table={alias=b, query={select={1={column={name=apple, table_ref=null}}, 2={column={name=banana, table_ref=null}}}, from={table={alias=null, table=tab1}}, where={condition={left={column={substitution={name=<other>, type=column}, table_ref=tab2}}, right={literal=20}, operator=>}}}}}, where={column={substitution={name=<middle>, type=column}, table_ref=tab2}}}}}, 2={join=join, on={condition={left={column={name=apple, table_ref=a}}, right={column={name=apple, table_ref=tab2}}, operator==}}}, 3={table={alias=null, table=tab2}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[apple]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<middle>=column, <other>=column}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={banana=[[@11,57:62='banana',<329>,3:16]], apple=[[@9,50:54='apple',<329>,3:9]]}, tab2={apple=[[@35,150:153='tab2',<329>,5:24]], <middle>=[[@23,109:112='tab2',<329>,4:7]], <other>=[[@15,80:83='tab2',<329>,3:39]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={banana=[[@11,57:62='banana',<329>,3:16]], apple=[[@9,50:54='apple',<329>,3:9]]}, query1={apple=[[@5,28:32='apple',<329>,2:9], [@31,140:140='a',<329>,5:14]]}, query2={apple=[[@1,7:11='apple',<329>,1:7]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query2={query_dictionary={apple=[[@1,7:11='apple',<329>,1:7]]}, table_dictionary={tab2={apple=[[@35,150:153='tab2',<329>,5:24]]}}, def_query1={query_dictionary={apple=[[@5,28:32='apple',<329>,2:9], [@31,140:140='a',<329>,5:14]]}, table_dictionary={}, def_query0={query_dictionary={banana=[[@11,57:62='banana',<329>,3:16]], apple=[[@9,50:54='apple',<329>,3:9]]}, table_dictionary={tab1={banana=[[@11,57:62='banana',<329>,3:16]], apple=[[@9,50:54='apple',<329>,3:9]]}}, filters=[{substitution={name=<other>, type=column}, table_ref=tab2}], interface={banana=[{name=banana, table_ref=tab1}], apple=[{name=apple, table_ref=tab1}]}}, filters=[{substitution={name=<middle>, type=column}, table_ref=tab2}], interface={apple=[{name=apple, table_ref=query0}]}, table_alias={b=query0}}, filters=[{name=apple, table_ref=a}, {name=apple, table_ref=tab2}], interface={apple=[{name=apple, table_ref=null}]}, table_alias={a=query1}}}",
				extractor.getSymbolTable().toString());

		Snippet snippet = extractor.getSnippet();
		assertDiagnosticCountBySeverity(snippet, "AMBIGUOUS_COLUMN_REFERENCE", ParseDiagnostic.Severity.SEVERE_WARNING, "Ambiguous column reference 'apple'", "apple", 1);
		assertDiagnosticAtPosition(
				snippet,
				"AMBIGUOUS_COLUMN_REFERENCE",
				ParseDiagnostic.Severity.SEVERE_WARNING,
				"Ambiguous column reference 'apple' at (l:1 c:7). Possible sources: [tab2, query1]",
				"apple",
				1,
				7);
	}


	@Test
	public void simpleColumnAllocationTest() {
		String query = " select a aa,b,c from tab1 dd";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=a, table_ref=null}, alias=aa}, 2={column={name=b, table_ref=null}}, 3={column={name=c, table_ref=null}}}, from={table={alias=dd, table=tab1}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[aa, b, c]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={a=[[@1,8:8='a',<329>,1:8]], b=[[@4,13:13='b',<329>,1:13]], c=[[@6,15:15='c',<329>,1:15]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={aa=[[@2,10:11='aa',<329>,1:10]], b=[[@4,13:13='b',<329>,1:13]], c=[[@6,15:15='c',<329>,1:15]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={query_dictionary={aa=[[@2,10:11='aa',<329>,1:10]], b=[[@4,13:13='b',<329>,1:13]], c=[[@6,15:15='c',<329>,1:15]]}, table_dictionary={tab1={a=[[@1,8:8='a',<329>,1:8]], b=[[@4,13:13='b',<329>,1:13]], c=[[@6,15:15='c',<329>,1:15]]}}, interface={aa=[{name=a, table_ref=tab1}], b=[{name=b, table_ref=tab1}], c=[{name=c, table_ref=tab1}]}, table_alias={dd=tab1}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void explicitButPartialColumnAllocationTest() {
		String query = " select dd.a aa, dd.b, \n c from tab1 dd";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=a, table_ref=dd}, alias=aa}, 2={column={name=b, table_ref=dd}}, 3={column={name=c, table_ref=null}}}, from={table={alias=dd, table=tab1}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[aa, b, c]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={a=[[@1,8:9='dd',<329>,1:8]], b=[[@6,17:18='dd',<329>,1:17]], c=[[@10,25:25='c',<329>,2:1]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={aa=[[@4,13:14='aa',<329>,1:13]], b=[[@8,20:20='b',<329>,1:20]], c=[[@10,25:25='c',<329>,2:1]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={query_dictionary={aa=[[@4,13:14='aa',<329>,1:13]], b=[[@8,20:20='b',<329>,1:20]], c=[[@10,25:25='c',<329>,2:1]]}, table_dictionary={tab1={a=[[@1,8:9='dd',<329>,1:8]], b=[[@6,17:18='dd',<329>,1:17]], c=[[@10,25:25='c',<329>,2:1]]}}, interface={aa=[{name=a, table_ref=dd}], b=[{name=b, table_ref=dd}], c=[{name=c, table_ref=tab1}]}, table_alias={dd=tab1}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void ambiguousColumnAllocationTest() {
		String query = " select dd.a aa, cc.b, c from tab1 dd join tab2 cc";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=a, table_ref=dd}, alias=aa}, 2={column={name=b, table_ref=cc}}, 3={column={name=c, table_ref=null}}}, from={join={1={table={alias=dd, table=tab1}}, 2={join=join}, 3={table={alias=cc, table=tab2}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[aa, b, c]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={a=[[@1,8:9='dd',<329>,1:8]]}, tab2={b=[[@6,17:18='cc',<329>,1:17]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={aa=[[@4,13:14='aa',<329>,1:13]], b=[[@8,20:20='b',<329>,1:20]], c=[[@10,23:23='c',<329>,1:23]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={query_dictionary={aa=[[@4,13:14='aa',<329>,1:13]], b=[[@8,20:20='b',<329>,1:20]], c=[[@10,23:23='c',<329>,1:23]]}, table_dictionary={tab1={a=[[@1,8:9='dd',<329>,1:8]]}, tab2={b=[[@6,17:18='cc',<329>,1:17]]}}, interface={aa=[{name=a, table_ref=dd}], b=[{name=b, table_ref=cc}], c=[{name=c, table_ref=null}]}, table_alias={dd=tab1, cc=tab2}}}",
				extractor.getSymbolTable().toString());

		Snippet snippet = extractor.getSnippet();
		assertDiagnosticAtPosition(snippet, "AMBIGUOUS_COLUMN_REFERENCE", ParseDiagnostic.Severity.SEVERE_WARNING,
				"Ambiguous column reference 'c'",
				"c", 1, 23);
		assertUnresolvedUnknownColumnsDiagnostic(snippet, 1, 23, ParseDiagnostic.Severity.ERROR, "c");
		assertDiagnosticAtPosition(snippet, "UNRESOLVED_UNQUALIFIED_COLUMNS", ParseDiagnostic.Severity.ERROR,
				"Unresolved unqualified column reference(s): [c",
				"c", 1, 23);
		assertDiagnosticCountBySeverity(snippet, "AMBIGUOUS_COLUMN_REFERENCE", ParseDiagnostic.Severity.SEVERE_WARNING, null, "c", 1);
		assertDiagnosticCountBySeverity(snippet, "UNRESOLVED_UNQUALIFIED_COLUMNS", ParseDiagnostic.Severity.ERROR, null, "c", 1);
	}


	@Test
	public void ambiguousColumnAllocationCollectsFatalDiagnosticTest() {
		String query = " select dd.a aa, cc.b, c from tab1 dd join tab2 cc";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		Snippet snippet = extractor.getSnippet();

		assertDiagnosticAtPosition(snippet, "AMBIGUOUS_COLUMN_REFERENCE", ParseDiagnostic.Severity.SEVERE_WARNING,
				"Ambiguous column reference 'c'",
				"c", 1, 23);
		assertUnresolvedUnknownColumnsDiagnostic(snippet, 1, 23, ParseDiagnostic.Severity.ERROR, "c");
		assertDiagnosticAtPosition(snippet, "UNRESOLVED_UNQUALIFIED_COLUMNS", ParseDiagnostic.Severity.ERROR,
				"Unresolved unqualified column reference(s): [c",
				"c", 1, 23);
		assertDiagnosticCountBySeverity(snippet, "AMBIGUOUS_COLUMN_REFERENCE", ParseDiagnostic.Severity.SEVERE_WARNING, null, "c", 1);
		assertDiagnosticCountBySeverity(snippet, "UNRESOLVED_UNQUALIFIED_COLUMNS", ParseDiagnostic.Severity.ERROR, null, "c", 1);
	}


	@Test
	public void unresolvedUnknownSymbolTableWithSimpleSubqueryTest() {
		String query = " select a aa, b, c from (select a, e as b from ee where 1=1) dd";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=a, table_ref=null}, alias=aa}, 2={column={name=b, table_ref=null}}, 3={column={name=c, table_ref=null}}}, from={table={alias=dd, query={select={1={column={name=a, table_ref=null}}, 2={column={name=e, table_ref=null}, alias=b}}, from={table={alias=null, table=ee}}, where={condition={left={literal=1}, right={literal=1}, operator==}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[aa, b, c]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{ee={a=[[@10,32:32='a',<329>,1:32]], e=[[@12,35:35='e',<329>,1:35]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={a=[[@10,32:32='a',<329>,1:32]], b=[[@14,40:40='b',<329>,1:40]]}, query1={aa=[[@2,10:11='aa',<329>,1:10]], b=[[@4,14:14='b',<329>,1:14]], c=[[@6,17:17='c',<329>,1:17]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query1={query_dictionary={aa=[[@2,10:11='aa',<329>,1:10]], b=[[@4,14:14='b',<329>,1:14]], c=[[@6,17:17='c',<329>,1:17]]}, table_dictionary={}, def_query0={query_dictionary={a=[[@10,32:32='a',<329>,1:32]], b=[[@14,40:40='b',<329>,1:40]]}, table_dictionary={ee={a=[[@10,32:32='a',<329>,1:32]], e=[[@12,35:35='e',<329>,1:35]]}}, filters=[], interface={a=[{name=a, table_ref=ee}], b=[{name=e, table_ref=ee}]}}, interface={aa=[{name=a, table_ref=query0}], b=[{name=b, table_ref=query0}], c=[{name=c, table_ref=null}]}, table_alias={dd=query0}}}",
				extractor.getSymbolTable().toString());

		Snippet snippet = extractor.getSnippet();
		assertUnresolvedUnknownColumnsDiagnostic(snippet, 1, 17, ParseDiagnostic.Severity.ERROR, "c");
		assertDiagnosticAtPosition(snippet, "UNRESOLVED_UNQUALIFIED_COLUMNS", ParseDiagnostic.Severity.ERROR,
				"Unresolved unqualified column reference(s)",
				"c", 1, 17);
		assertDiagnosticCountBySeverity(snippet, "UNRESOLVED_UNQUALIFIED_COLUMNS", ParseDiagnostic.Severity.ERROR, null, "c", 1);
	}


	@Test
	public void unresolvedUnknownSymbolTableWithSimpleSubqueryCollectsFatalDiagnosticTest() {
		String query = " select a aa, b, c from (select a, e as b from ee where 1=1) dd";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		Snippet snippet = extractor.getSnippet();

		assertUnresolvedUnknownColumnsDiagnostic(snippet, 1, 17, ParseDiagnostic.Severity.ERROR, "c");
		assertDiagnosticAtPosition(snippet, "UNRESOLVED_UNQUALIFIED_COLUMNS", ParseDiagnostic.Severity.ERROR,
				"Unresolved",
				"c", 1, 17);
		assertDiagnosticCountBySeverity(snippet, "UNRESOLVED_UNQUALIFIED_COLUMNS", ParseDiagnostic.Severity.ERROR, null, "c", 1);
	}


	@Test
	public void ambiguousQueryColumnWithCompetingSubqueryAliasesCollectsFatalDiagnosticV1Test() {
		String query = "SELECT a FROM (SELECT x AS a FROM tab1) dd JOIN (SELECT z AS a FROM tab2) cc ON dd.a = cc.a";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		Snippet snippet = extractor.getSnippet();

		assertDiagnosticAtPosition(snippet, "AMBIGUOUS_COLUMN_REFERENCE", ParseDiagnostic.Severity.SEVERE_WARNING, "Ambiguous column reference 'a'", "a", 1, 7);
		assertDiagnosticAtPosition(snippet, "UNRESOLVED_UNQUALIFIED_COLUMNS", ParseDiagnostic.Severity.ERROR,
				"Unresolved unqualified column reference(s)",
				"a", 1, 7);
		assertDiagnosticCountBySeverity(snippet, "AMBIGUOUS_COLUMN_REFERENCE", ParseDiagnostic.Severity.SEVERE_WARNING, null, "a", 1);
		assertDiagnosticCountBySeverity(snippet, "UNRESOLVED_UNQUALIFIED_COLUMNS", ParseDiagnostic.Severity.ERROR, null, "a", 1);
	}


	@Test
	public void ambiguousQueryColumnWithCompetingSubqueryAliasesCollectsFatalDiagnosticV2Test() {
		String query = "SELECT a FROM (SELECT * FROM tab1) dd JOIN (SELECT z AS a FROM tab2) cc ON dd.a = cc.a";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		Snippet snippet = extractor.getSnippet();

		assertDiagnosticAtPosition(snippet, "AMBIGUOUS_COLUMN_REFERENCE", ParseDiagnostic.Severity.SEVERE_WARNING, "Ambiguous column reference 'a'", "a", 1, 7);
		assertDiagnosticAtPosition(snippet, "UNRESOLVED_UNQUALIFIED_COLUMNS", ParseDiagnostic.Severity.ERROR,
				"Unresolved unqualified column reference(s)",
				"a", 1, 7);
		assertDiagnosticCountBySeverity(snippet, "AMBIGUOUS_COLUMN_REFERENCE", ParseDiagnostic.Severity.SEVERE_WARNING, null, "a", 1);
		assertDiagnosticCountBySeverity(snippet, "UNRESOLVED_UNQUALIFIED_COLUMNS", ParseDiagnostic.Severity.ERROR, null, "a", 1);
	}


	@Test
	public void ambiguousQueryColumnWithCompetingSubqueryAliasesCollectsFatalDiagnosticV3Test() {
		String query = "SELECT a FROM (SELECT * FROM tab1) dd JOIN (SELECT * FROM tab2) cc ON dd.a = cc.a";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		Snippet snippet = extractor.getSnippet();

		assertDiagnosticAtPosition(snippet, "AMBIGUOUS_COLUMN_REFERENCE", ParseDiagnostic.Severity.SEVERE_WARNING, "Ambiguous column reference 'a'", "a", 1, 7);
		assertDiagnosticAtPosition(snippet, "UNRESOLVED_UNQUALIFIED_COLUMNS", ParseDiagnostic.Severity.ERROR,
				"Unresolved unqualified column reference(s)",
				"a", 1, 7);
		assertDiagnosticCountBySeverity(snippet, "AMBIGUOUS_COLUMN_REFERENCE", ParseDiagnostic.Severity.SEVERE_WARNING, null, "a", 1);
		assertDiagnosticCountBySeverity(snippet, "UNRESOLVED_UNQUALIFIED_COLUMNS", ParseDiagnostic.Severity.ERROR, null, "a", 1);
	}


	@Test
	public void ambiguousQueryColumnWithCompetingSubqueryAliasesCollectsFatalDiagnosticV4Test() {
		String query = "SELECT a FROM tab1 dd JOIN tab2 cc ON dd.a = cc.a";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		Snippet snippet = extractor.getSnippet();

		assertDiagnosticAtPosition(snippet, "AMBIGUOUS_COLUMN_REFERENCE", ParseDiagnostic.Severity.SEVERE_WARNING, "Ambiguous column reference 'a'", "a", 1, 7);
		assertDiagnosticAtPosition(snippet, "UNRESOLVED_UNQUALIFIED_COLUMNS", ParseDiagnostic.Severity.ERROR,
				"Unresolved unqualified column reference(s)",
				"a", 1, 7);
		assertDiagnosticCountBySeverity(snippet, "AMBIGUOUS_COLUMN_REFERENCE", ParseDiagnostic.Severity.SEVERE_WARNING, null, "a", 1);
		assertDiagnosticCountBySeverity(snippet, "UNRESOLVED_UNQUALIFIED_COLUMNS", ParseDiagnostic.Severity.ERROR, null, "a", 1);
	}


	@Test
	public void ambiguousQueryColumnWithCompetingSubqueryAliasesCollectsFatalDiagnosticV5Test() {
		String query = "SELECT a FROM tab1 dd JOIN tab2 cc ON dd.a = cc.a"
			+ " JOIN (select * from tab3) ee ON dd.a = ee.a";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		Snippet snippet = extractor.getSnippet();

		assertDiagnosticAtPosition(snippet, "AMBIGUOUS_COLUMN_REFERENCE", ParseDiagnostic.Severity.SEVERE_WARNING, "Ambiguous column reference 'a'", "a", 1, 7);
		assertDiagnosticAtPosition(snippet, "UNRESOLVED_UNQUALIFIED_COLUMNS", ParseDiagnostic.Severity.ERROR,
				"Unresolved unqualified column reference(s)",
				"a", 1, 7);
		assertDiagnosticCountBySeverity(snippet, "AMBIGUOUS_COLUMN_REFERENCE", ParseDiagnostic.Severity.SEVERE_WARNING, null, "a", 1);
		assertDiagnosticCountBySeverity(snippet, "UNRESOLVED_UNQUALIFIED_COLUMNS", ParseDiagnostic.Severity.ERROR, null, "a", 1);
	}


	@Test
	public void ambiguousQueryColumnWithCompetingSubqueryAliasesCollectsFatalDiagnosticV6Test() {
		String query = "SELECT a FROM tab1 dd JOIN tab2 cc ON dd.a = cc.a"
			+ " JOIN (select x as a from tab3) ee ON dd.a = ee.a";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		Snippet snippet = extractor.getSnippet();

		assertDiagnosticAtPosition(snippet, "AMBIGUOUS_COLUMN_REFERENCE", ParseDiagnostic.Severity.SEVERE_WARNING, "Ambiguous column reference 'a'", "a", 1, 7);
		assertDiagnosticAtPosition(snippet, "UNRESOLVED_UNQUALIFIED_COLUMNS", ParseDiagnostic.Severity.ERROR,
				"Unresolved unqualified column reference(s)",
				"a", 1, 7);
		assertDiagnosticCountBySeverity(snippet, "AMBIGUOUS_COLUMN_REFERENCE", ParseDiagnostic.Severity.SEVERE_WARNING, null, "a", 1);
		assertDiagnosticCountBySeverity(snippet, "UNRESOLVED_UNQUALIFIED_COLUMNS", ParseDiagnostic.Severity.ERROR, null, "a", 1);
	}


	@Test
	public void simpleFromListType3ParseTest() {

		final String query = " SELECT * FROM third cross join fourth "
				+ " union join fifth natural join sixth natural inner join seventh";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
	}


	@Test
	public void simpleFromListType4ParseTest() {

		final String query = " SELECT * FROM third join fourth on a = b " 
		+ " left outer join fifth on b = d ";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
				
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=*, table_ref=*}}}, from={join={1={table={alias=null, table=third}}, 2={join=join, on={condition={left={column={name=a, table_ref=null}}, right={column={name=b, table_ref=null}}, operator==}}}, 3={table={alias=null, table=fourth}}, 4={join=leftouter, on={condition={left={column={name=b, table_ref=null}}, right={column={name=d, table_ref=null}}, operator==}}}, 5={table={alias=null, table=fifth}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{third={*=[[@1,8:8='*',<289>,1:8]]}, fifth={*=[[@1,8:8='*',<289>,1:8]]}, fourth={*=[[@1,8:8='*',<289>,1:8]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@1,8:8='*',<289>,1:8]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={query_dictionary={*=[[@1,8:8='*',<289>,1:8]]}, table_dictionary={third={*=[[@1,8:8='*',<289>,1:8]]}, fifth={*=[[@1,8:8='*',<289>,1:8]]}, fourth={*=[[@1,8:8='*',<289>,1:8]]}}, filters=[{name=a, table_ref=null}, {name=b, table_ref=null}, {name=d, table_ref=null}], interface={*=[{name=*, table_ref=*}]}}}",
				extractor.getSymbolTable().toString());

		Snippet snippet = extractor.getSnippet();
		assertDiagnosticAtPosition(snippet, "UNRESOLVED_UNQUALIFIED_COLUMNS", ParseDiagnostic.Severity.ERROR,
				"Unresolved unqualified column reference(s): [a [(l:1 c:36)], b [(l:1 c:40), (l:1 c:68)], d [(l:1 c:72)]]",
				"a", 1, 36);

	}


	@Test
	public void simpleFromListType5ParseTest() {

		final String query = " SELECT * FROM third join (select x from sixth where m.issing > 0) as fourth on a = b " 
		+ " left outer join fifth on b = d ";
		
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
				
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=*, table_ref=*}}}, from={join={1={table={alias=null, table=third}}, 2={join=join, on={condition={left={column={name=a, table_ref=null}}, right={column={name=b, table_ref=null}}, operator==}}}, 3={table={alias=fourth, query={select={1={column={name=x, table_ref=null}}}, from={table={alias=null, table=sixth}}, where={condition={left={column={name=issing, table_ref=m}}, right={literal=0}, operator=>}}}}}, 4={join=leftouter, on={condition={left={column={name=b, table_ref=null}}, right={column={name=d, table_ref=null}}, operator==}}}, 5={table={alias=null, table=fifth}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={x=[[@7,34:34='x',<329>,1:34]]}, query1={*=[[@1,8:8='*',<289>,1:8]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{sixth={x=[[@7,34:34='x',<329>,1:34]]}, third={*=[[@1,8:8='*',<289>,1:8]]}, fifth={*=[[@1,8:8='*',<289>,1:8]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query1={query_dictionary={*=[[@1,8:8='*',<289>,1:8]]}, table_dictionary={third={*=[[@1,8:8='*',<289>,1:8]]}, fifth={*=[[@1,8:8='*',<289>,1:8]]}}, def_query0={query_dictionary={x=[[@7,34:34='x',<329>,1:34]]}, table_dictionary={sixth={x=[[@7,34:34='x',<329>,1:34]]}}, filters=[{name=issing, table_ref=m}], interface={x=[{name=x, table_ref=sixth}]}}, filters=[{name=a, table_ref=null}, {name=b, table_ref=null}, {name=d, table_ref=null}], interface={*=[{name=*, table_ref=*}]}, table_alias={fourth=query0}}}",
				extractor.getSymbolTable().toString());

		Snippet snippet = extractor.getSnippet();
		assertFatalDiagnosticAtPosition(snippet, "QUALIFIED_COLUMN_NOT_FOUND_IN_TABLE",
				"Source Table not found for Column 'issing' at (l:1 c:53). No alias or table called 'm'.",
				"issing", 1, 53);
		assertDiagnosticAtPosition(snippet, "UNRESOLVED_UNQUALIFIED_COLUMNS", ParseDiagnostic.Severity.ERROR,
				"Unresolved unqualified column reference(s): [a [(l:1 c:80)], b [(l:1 c:84), (l:1 c:112)], d [(l:1 c:116)]]",
				"a", 1, 80);
		assertFatalDiagnosticCount(snippet, "QUALIFIED_COLUMN_NOT_FOUND_IN_TABLE",
				"Source Table not found for Column 'issing'",
				"issing",
				1);
		assertDiagnosticCountBySeverity(snippet, "UNRESOLVED_UNQUALIFIED_COLUMNS", ParseDiagnostic.Severity.ERROR,
				"Unresolved unqualified column reference(s)",
				"a",
				1);

	}


	@Test
	public void simpleFromListType6ParseTest() {

		final String query = " select x from sixth where m.issing > y";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
				
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=x, table_ref=null}}}, from={table={alias=null, table=sixth}}, where={condition={left={column={name=issing, table_ref=m}}, right={column={name=y, table_ref=null}}, operator=>}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[x]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={x=[[@1,8:8='x',<329>,1:8]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{sixth={x=[[@1,8:8='x',<329>,1:8]], y=[[@9,38:38='y',<329>,1:38]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={query_dictionary={x=[[@1,8:8='x',<329>,1:8]]}, table_dictionary={sixth={x=[[@1,8:8='x',<329>,1:8]], y=[[@9,38:38='y',<329>,1:38]]}}, filters=[{name=issing, table_ref=m}, {name=y, table_ref=sixth}], interface={x=[{name=x, table_ref=sixth}]}}}",
				extractor.getSymbolTable().toString());

		Snippet snippet = extractor.getSnippet();
		assertFatalDiagnosticAtPosition(snippet, "QUALIFIED_COLUMN_NOT_FOUND_IN_TABLE",
				"Source Table not found for Column 'issing' at (l:1 c:27). No alias or table called 'm'.",
				"issing", 1, 27);
	}


	@Test
	public void sameTableDifferentSchemaQualifiedReferencesV1() {
		String sql =  " Select  aaa.col1, bbb.col1 FROM sch1.aaa aaa join sch2.bbb bbb on aaa.col1 = bbb.col2";
		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runParsertest(sql, parser);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=col1, table_ref=aaa}}, 2={column={name=col1, table_ref=bbb}}}, from={join={1={table={schema=sch1, alias=aaa, table=aaa}}, 2={join=join, on={condition={left={column={name=col1, table_ref=aaa}}, right={column={name=col2, table_ref=bbb}}, operator==}}}, 3={table={schema=sch2, alias=bbb, table=bbb}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[col1]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{sch2.bbb={col2=[[@23,78:80='bbb',<329>,1:78]], col1=[[@5,19:21='bbb',<329>,1:19]]}, sch1.aaa={col1=[[@1,9:11='aaa',<329>,1:9], [@19,67:69='aaa',<329>,1:67]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={col1=[[@3,13:16='col1',<329>,1:13], [@7,23:26='col1',<329>,1:23]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={query_dictionary={col1=[[@3,13:16='col1',<329>,1:13], [@7,23:26='col1',<329>,1:23]]}, table_dictionary={sch2.bbb={col2=[[@23,78:80='bbb',<329>,1:78]], col1=[[@5,19:21='bbb',<329>,1:19]]}, sch1.aaa={col1=[[@1,9:11='aaa',<329>,1:9], [@19,67:69='aaa',<329>,1:67]]}}, filters=[{name=col1, table_ref=aaa}, {name=col2, table_ref=bbb}], interface={col1=[{name=col1, table_ref=bbb}]}, table_alias={aaa=sch1.aaa, bbb=sch2.bbb}}}",
				extractor.getSymbolTable().toString());

		Snippet snippet = extractor.getSnippet();
		Assert.assertEquals("Fatal error count is wrong", 1, snippet.getFatalErrorStringList().size());
		Assert.assertEquals(
				"Fatal error message is wrong",
				"[Duplicate interface columns defined: aaa.col1 at (l:1 c:13) and bbb.col1 at (l:1 c:23).]",
				snippet.getFatalErrorStringList().toString());
		assertDiagnosticCountBySeverity(snippet, "DUPLICATE_INTERFACE_COLUMNS", ParseDiagnostic.Severity.FATAL, "Duplicate interface columns defined: aaa.col1 at (l:1 c:13) and bbb.col1 at (l:1 c:23).", null, 1);
	}


	@Test
	public void sameTableDifferentSchemaQualifiedReferencesV2() {
		String sql =  " Select  aaa.col1, bbb.col2 FROM sch1.aaa aaa join sch2.bbb bbb on aaa.col1 = bbb.col2";
		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runParsertest(sql, parser);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=col1, table_ref=aaa}}, 2={column={name=col2, table_ref=bbb}}}, from={join={1={table={schema=sch1, alias=aaa, table=aaa}}, 2={join=join, on={condition={left={column={name=col1, table_ref=aaa}}, right={column={name=col2, table_ref=bbb}}, operator==}}}, 3={table={schema=sch2, alias=bbb, table=bbb}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[col2, col1]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{sch2.bbb={col2=[[@5,19:21='bbb',<329>,1:19], [@23,78:80='bbb',<329>,1:78]]}, sch1.aaa={col1=[[@1,9:11='aaa',<329>,1:9], [@19,67:69='aaa',<329>,1:67]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={col2=[[@7,23:26='col2',<329>,1:23]], col1=[[@3,13:16='col1',<329>,1:13]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={query_dictionary={col2=[[@7,23:26='col2',<329>,1:23]], col1=[[@3,13:16='col1',<329>,1:13]]}, table_dictionary={sch2.bbb={col2=[[@5,19:21='bbb',<329>,1:19], [@23,78:80='bbb',<329>,1:78]]}, sch1.aaa={col1=[[@1,9:11='aaa',<329>,1:9], [@19,67:69='aaa',<329>,1:67]]}}, filters=[{name=col1, table_ref=aaa}, {name=col2, table_ref=bbb}], interface={col2=[{name=col2, table_ref=bbb}], col1=[{name=col1, table_ref=aaa}]}, table_alias={aaa=sch1.aaa, bbb=sch2.bbb}}}",
				extractor.getSymbolTable().toString());

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
	}


	@Test
	public void sameTableDifferentSchemaQualifiedReferencesV3() {
		String sql =  " Select  aaa.col1 FROM sch1.aaa aaa "
			+	"\n join (select col1 from sch2.bbb) bbb on aaa.col1 = bbb.col1";
		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runParsertest(sql, parser);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=col1, table_ref=aaa}}}, from={join={1={table={schema=sch1, alias=aaa, table=aaa}}, 2={join=join, on={condition={left={column={name=col1, table_ref=aaa}}, right={column={name=col1, table_ref=bbb}}, operator==}}}, 3={table={alias=bbb, query={select={1={column={name=col1, table_ref=null}}}, from={table={schema=sch2, alias=null, table=bbb}}}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[col1]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{sch2.bbb={col1=[[@12,51:54='col1',<329>,2:14]]}, sch1.aaa={col1=[[@1,9:11='aaa',<329>,1:9], [@20,78:80='aaa',<329>,2:41]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={col1=[[@12,51:54='col1',<329>,2:14], [@24,89:91='bbb',<329>,2:52]]}, query1={col1=[[@3,13:16='col1',<329>,1:13]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query1={query_dictionary={col1=[[@3,13:16='col1',<329>,1:13]]}, table_dictionary={sch1.aaa={col1=[[@1,9:11='aaa',<329>,1:9], [@20,78:80='aaa',<329>,2:41]]}}, def_query0={query_dictionary={col1=[[@12,51:54='col1',<329>,2:14], [@24,89:91='bbb',<329>,2:52]]}, table_dictionary={sch2.bbb={col1=[[@12,51:54='col1',<329>,2:14]]}}, interface={col1=[{name=col1, table_ref=sch2.bbb}]}}, filters=[{name=col1, table_ref=aaa}, {name=col1, table_ref=bbb}], interface={col1=[{name=col1, table_ref=aaa}]}, table_alias={aaa=sch1.aaa, bbb=query0}}}",
				extractor.getSymbolTable().toString());

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
	}


	@Test
	public void sameTableDifferentSchemaQualifiedReferencesV4() {
		String sql =  " Select  aaa.col1 FROM sch1.aaa aaa union select bbb.col1 from sch2.bbb bbb";
		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runParsertest(sql, parser);

		Assert.assertEquals("AST is wrong", "{SQL={union={1={select={1={column={name=col1, table_ref=aaa}}}, from={table={schema=sch1, alias=aaa, table=aaa}}}, 2={union={qualifier=null, operator=union}}, 3={select={1={column={name=col1, table_ref=bbb}}}, from={table={schema=sch2, alias=bbb, table=bbb}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[col1]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{sch2.bbb={col1=[[@11,49:51='bbb',<329>,1:49]]}, sch1.aaa={col1=[[@1,9:11='aaa',<329>,1:9]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={col1=[[@3,13:16='col1',<329>,1:13]]}, query1={col1=[[@13,53:56='col1',<329>,1:53]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{union2={query0={query_dictionary={col1=[[@3,13:16='col1',<329>,1:13]]}, table_dictionary={sch1.aaa={col1=[[@1,9:11='aaa',<329>,1:9]]}}, interface={col1=[{name=col1, table_ref=aaa}]}, table_alias={aaa=sch1.aaa}}, interface={col1=query_column}, query1={query_dictionary={col1=[[@13,53:56='col1',<329>,1:53]]}, table_dictionary={sch2.bbb={col1=[[@11,49:51='bbb',<329>,1:49]]}}, interface={col1=[{name=col1, table_ref=bbb}]}, table_alias={bbb=sch2.bbb}}}}",
				extractor.getSymbolTable().toString());

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
	}

}
