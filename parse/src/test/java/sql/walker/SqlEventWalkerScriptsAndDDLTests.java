package sql.walker;

import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import sql.SQLSelectParserParser;

public class SqlEventWalkerScriptsAndDDLTests extends AbstractSqlParseEventWalkerTest {

	// SCRIPTS TESTS

	@Test
	@SuppressWarnings("unchecked")
	public void simpleScriptTest() {
		final String query = "select p.col1, * from table(flatten(input=>parse_json('[1,2]'))) f; \n"
		+ " select * from table(flatten(input=>parse_json('[3,4]'))) f2;";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runScriptParsertest(query, parser);

		String ast = extractor.getAsTree().toString();
		Assert.assertTrue("AST should contain table_function", ast.contains("table_function="));
		Assert.assertFalse("SCRIPT AST should not leak rule Type keys", ast.contains("Type="));

		Map<String, Object> scriptMap = (Map<String, Object>) extractor.getAsTree().get("SCRIPT");
		Assert.assertNotNull("SCRIPT map should be present", scriptMap);

		Map<String, Object> firstStatement = (Map<String, Object>) scriptMap.get("1");
		Map<String, Object> secondStatement = (Map<String, Object>) scriptMap.get("2");
		Assert.assertNotNull("SCRIPT statement 1 should be present", firstStatement);
		Assert.assertNotNull("SCRIPT statement 2 should be present", secondStatement);
		Assert.assertTrue("SCRIPT statement 1 should be promoted to select AST", firstStatement.containsKey("select"));
		Assert.assertTrue("SCRIPT statement 2 should be promoted to select AST", secondStatement.containsKey("select"));
		Assert.assertFalse("SCRIPT statement 1 should not have wrapper key 1", firstStatement.containsKey("1"));
		Assert.assertFalse("SCRIPT statement 2 should not have wrapper key 1", secondStatement.containsKey("1"));

		Map<String, Object> scriptSymbolTable = (Map<String, Object>) extractor.getSymbolTable().get("SCRIPT");
		Assert.assertNotNull("SCRIPT symbol table should be present", scriptSymbolTable);
		Map<String, Object> firstStatementSymbols = (Map<String, Object>) scriptSymbolTable.get("1");
		Map<String, Object> secondStatementSymbols = (Map<String, Object>) scriptSymbolTable.get("2");
		Assert.assertNotNull("Statement 1 symbol subtree should be present", firstStatementSymbols);
		Assert.assertNotNull("Statement 2 symbol subtree should be present", secondStatementSymbols);
		Assert.assertTrue("Statement 1 should contain query0 scope", firstStatementSymbols.containsKey("query0"));
		Assert.assertTrue("Statement 2 should contain query0 scope", secondStatementSymbols.containsKey("query0"));
		Assert.assertFalse("Statement 2 should not leak query1 from previous statement", secondStatementSymbols.containsKey("query1"));

		Map<String, Object> scriptTableDictionary = (Map<String, Object>) extractor.getTableColumnDictionaryMap().get("SCRIPT");
		Assert.assertNotNull("SCRIPT table dictionary collector should be present", scriptTableDictionary);
		Assert.assertNotNull("Statement 1 table dictionary snapshot should be present", scriptTableDictionary.get("1"));
		Assert.assertNotNull("Statement 2 table dictionary snapshot should be present", scriptTableDictionary.get("2"));

		Map<String, Object> scriptQueryDictionary = (Map<String, Object>) extractor.getQueryColumnDictionaryMap().get("SCRIPT");
		Assert.assertNotNull("SCRIPT query dictionary collector should be present", scriptQueryDictionary);
		Map<String, Object> firstStatementQueryDictionary = (Map<String, Object>) scriptQueryDictionary.get("1");
		Map<String, Object> secondStatementQueryDictionary = (Map<String, Object>) scriptQueryDictionary.get("2");
		Assert.assertNotNull("Statement 1 query dictionary snapshot should be present", firstStatementQueryDictionary);
		Assert.assertNotNull("Statement 2 query dictionary snapshot should be present", secondStatementQueryDictionary);
		Assert.assertTrue("Statement 1 query dictionary should contain query0", firstStatementQueryDictionary.containsKey("query0"));
		Assert.assertTrue("Statement 2 query dictionary should contain query0", secondStatementQueryDictionary.containsKey("query0"));
		Assert.assertFalse("Statement 2 query dictionary should not contain query1", secondStatementQueryDictionary.containsKey("query1"));

		Map<String, Object> scriptSubstitutions = (Map<String, Object>) extractor.getSubstitutionsMap().get("SCRIPT");
		Assert.assertNotNull("SCRIPT substitutions collector should be present", scriptSubstitutions);
		Assert.assertNotNull("Statement 1 substitutions snapshot should be present", scriptSubstitutions.get("1"));
		Assert.assertNotNull("Statement 2 substitutions snapshot should be present", scriptSubstitutions.get("2"));

		Map<String, Object> scriptArrayCollectors = (Map<String, Object>) extractor.getArrayOutputCollectorsMap().get("SCRIPT");
		Assert.assertNotNull("SCRIPT array-output collector should be present", scriptArrayCollectors);
		Map<String, Object> firstStatementArrays = (Map<String, Object>) scriptArrayCollectors.get("1");
		Map<String, Object> secondStatementArrays = (Map<String, Object>) scriptArrayCollectors.get("2");
		Assert.assertNotNull("Statement 1 array-output snapshot should be present", firstStatementArrays);
		Assert.assertNotNull("Statement 2 array-output snapshot should be present", secondStatementArrays);
		Assert.assertTrue("Array-output snapshots should include queryInterface key", firstStatementArrays.containsKey("queryInterface"));
		Assert.assertTrue("Array-output snapshots should include queryInterface key", secondStatementArrays.containsKey("queryInterface"));

		Map<String, Object> snippetArrayCollectors = extractor.getSnippet().getArrayOutputCollectorsMap();
		Assert.assertNotNull("Snippet should carry optional array-output collector for scripts", snippetArrayCollectors);
		Assert.assertTrue("Snippet array-output collector should include SCRIPT key", snippetArrayCollectors.containsKey("SCRIPT"));

		List<String> globalWalkerFatals = extractor.getSnippet().getFatalErrorStringList();
		Assert.assertTrue("Expected at least one global walker fatal diagnostic for the unresolved column script",
				globalWalkerFatals != null && !globalWalkerFatals.isEmpty());
		Assert.assertTrue("Global walker fatal diagnostics should include statement and line prefix",
				globalWalkerFatals.stream().anyMatch(message -> message != null
						&& message.startsWith("Statement 1 (l:")
						&& message.contains(": ")));
		
	}

	// DDL TESTS

	@Test
	public void simpleDdlCreateTableV1Test() {
		final String query = "create table tab1 as select * from table(flatten(input=>parse_json('[1,2]'))) f";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runDdlParsertest(query, parser);

		Assert.assertEquals("AST is wrong",
				"{DDL={create={type=table, table={table=tab1}, query={select={1={column={name=*, table_ref=*}}}, from={table={alias=f, table_function={function_name=flatten, parameters={input={function={parameters={1={literal='[1,2]'}}, function_name=parse_json}}}}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[]",
				extractor.getInterface().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{create1={query0={query_dictionary={*=[[@5,28:28='*',<290>,1:28]]}, table_dictionary={flatten0={*=[[@5,28:28='*',<290>,1:28]]}}, interface={*=[{name=*, table_ref=*}]}, table_alias={f=flatten0}}}}",
				extractor.getSymbolTable().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{flatten0={*=[[@5,28:28='*',<290>,1:28]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{query0={*=[[@5,28:28='*',<290>,1:28]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertNull("Snippet should omit optional array-output collector for non-script parses",
				extractor.getSnippet().getArrayOutputCollectorsMap());
	}

	@Test
	public void simpleDdlCreateTableExpressionV1Test() {
		final String query = "create table mydb.myschema.tab2 as select src.col1, src.col2, src.col3 from mydb.myschema.source_tab src";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runDdlParsertest(query, parser);

		Assert.assertEquals("AST is wrong",
				"{DDL={create={type=table, table={dbname=mydb, schema=myschema, table=tab2}, query={select={1={column={name=col1, table_ref=src}}, 2={column={name=col2, table_ref=src}}, 3={column={name=col3, table_ref=src}}}, from={table={schema=myschema, alias=src, dbname=mydb, table=source_tab}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[]",
				extractor.getInterface().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{create1={query0={query_dictionary={col2=[[@15,56:59='col2',<373>,1:56]], col3=[[@19,66:69='col3',<373>,1:66]], col1=[[@11,46:49='col1',<373>,1:46]]}, table_dictionary={mydb.myschema.source_tab={col2=[[@13,52:54='src',<373>,1:52]], col3=[[@17,62:64='src',<373>,1:62]], col1=[[@9,42:44='src',<373>,1:42]]}}, interface={col2=[{name=col2, table_ref=src}], col3=[{name=col3, table_ref=src}], col1=[{name=col1, table_ref=src}]}, table_alias={src=mydb.myschema.source_tab}}}}",
				extractor.getSymbolTable().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{mydb.myschema.source_tab={col2=[[@13,52:54='src',<373>,1:52]], col3=[[@17,62:64='src',<373>,1:62]], col1=[[@9,42:44='src',<373>,1:42]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{query0={col2=[[@15,56:59='col2',<373>,1:56]], col3=[[@19,66:69='col3',<373>,1:66]], col1=[[@11,46:49='col1',<373>,1:46]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertNull("Snippet should omit optional array-output collector for non-script parses",
				extractor.getSnippet().getArrayOutputCollectorsMap());
	}

	@Test
	public void simpleDdlCreateIndexExpressionV1Test() {
		final String query = "create index mydb.myschema.idx1 on mydb.myschema.source_tab (col1)";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runDdlParsertest(query, parser);

		Assert.assertEquals("AST is wrong",
				"{DDL={create={type=index, name={1=mydb, Type=32, 2=myschema, 3=idx1}, table={schema=myschema, dbname=mydb, table=source_tab}, columns={1={column={name=col1, table_ref=null}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[]",
				extractor.getInterface().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{create0={unresolved_column={col1={column={name=col1, table_ref=null}, locations=[[@14,61:64='col1',<373>,1:61]]}}}}",
				extractor.getSymbolTable().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertNull("Snippet should omit optional array-output collector for non-script parses",
				extractor.getSnippet().getArrayOutputCollectorsMap());
	}

	@Test
	public void simpleDdlCreateViewExpressionV1Test() {
		final String query = "create view mydb.myschema.vw1 as select src.col1, src.col2, src.col3 from mydb.myschema.source_tab src";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runDdlParsertest(query, parser);

		Assert.assertEquals("AST is wrong", "{DDL={create={type=view}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[]",
				extractor.getInterface().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{create1={query0={query_dictionary={col2=[[@15,54:57='col2',<373>,1:54]], col3=[[@19,64:67='col3',<373>,1:64]], col1=[[@11,44:47='col1',<373>,1:44]]}, table_dictionary={mydb.myschema.source_tab={col2=[[@13,50:52='src',<373>,1:50]], col3=[[@17,60:62='src',<373>,1:60]], col1=[[@9,40:42='src',<373>,1:40]]}}, interface={col2=[{name=col2, table_ref=src}], col3=[{name=col3, table_ref=src}], col1=[{name=col1, table_ref=src}]}, table_alias={src=mydb.myschema.source_tab}}}}",
				extractor.getSymbolTable().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{mydb.myschema.source_tab={col2=[[@13,50:52='src',<373>,1:50]], col3=[[@17,60:62='src',<373>,1:60]], col1=[[@9,40:42='src',<373>,1:40]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{query0={col2=[[@15,54:57='col2',<373>,1:54]], col3=[[@19,64:67='col3',<373>,1:64]], col1=[[@11,44:47='col1',<373>,1:44]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertNull("Snippet should omit optional array-output collector for non-script parses",
				extractor.getSnippet().getArrayOutputCollectorsMap());
	}

	@Test
	public void simpleDdlCreateMaterializedViewExpressionV1Test() {
		final String query = "create materialized view mydb.myschema.mv1 as select src.col1, src.col2, src.col3 from mydb.myschema.source_tab src";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runDdlParsertest(query, parser);

		Assert.assertEquals("AST is wrong", "{DDL={create={type=materialized view}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[]",
				extractor.getInterface().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{create1={query0={query_dictionary={col2=[[@16,67:70='col2',<373>,1:67]], col3=[[@20,77:80='col3',<373>,1:77]], col1=[[@12,57:60='col1',<373>,1:57]]}, table_dictionary={mydb.myschema.source_tab={col2=[[@14,63:65='src',<373>,1:63]], col3=[[@18,73:75='src',<373>,1:73]], col1=[[@10,53:55='src',<373>,1:53]]}}, interface={col2=[{name=col2, table_ref=src}], col3=[{name=col3, table_ref=src}], col1=[{name=col1, table_ref=src}]}, table_alias={src=mydb.myschema.source_tab}}}}",
				extractor.getSymbolTable().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{mydb.myschema.source_tab={col2=[[@14,63:65='src',<373>,1:63]], col3=[[@18,73:75='src',<373>,1:73]], col1=[[@10,53:55='src',<373>,1:53]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{query0={col2=[[@16,67:70='col2',<373>,1:67]], col3=[[@20,77:80='col3',<373>,1:77]], col1=[[@12,57:60='col1',<373>,1:57]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertNull("Snippet should omit optional array-output collector for non-script parses",
				extractor.getSnippet().getArrayOutputCollectorsMap());
	}

	@Test
	public void simpleDdlCreateFunctionExpressionV1Test() {
		final String query = "create function myschema.fn1(arg1 int) returns int language sql";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runDdlParsertest(query, parser);

		Assert.assertEquals("AST is wrong",
				"{DDL={create={type=function, name=fn1, parameters={1={}, Type=45}, data_type={type=INT}, clauses={1={}, Type=56}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[]",
				extractor.getInterface().toString());
		Assert.assertEquals("Symbol Table is wrong", "{create0={}}",
				extractor.getSymbolTable().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertNull("Snippet should omit optional array-output collector for non-script parses",
				extractor.getSnippet().getArrayOutputCollectorsMap());
	}

	@Test
	public void simpleDdlCreateProcedureExpressionV1Test() {
		final String query = "create procedure mydb.myschema.pr1(arg1 int) language sql";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runDdlParsertest(query, parser);

		Assert.assertEquals("AST is wrong",
				"{DDL={create={type=procedure, name={1=mydb, Type=32, 2=myschema, 3=pr1}, parameters={1={}, Type=46}, clauses={1={}, Type=57}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[]",
				extractor.getInterface().toString());
		Assert.assertEquals("Symbol Table is wrong", "{create0={}}",
				extractor.getSymbolTable().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertNull("Snippet should omit optional array-output collector for non-script parses",
				extractor.getSnippet().getArrayOutputCollectorsMap());
	}

	@Test
	public void simpleDdlCreateMacroExpressionV1Test() {
		final String query = "create macro mydb.myschema.mac1(arg1 int) as select src.col1, src.col2, src.col3 from mydb.myschema.source_tab src";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runDdlParsertest(query, parser);

		Assert.assertEquals("AST is wrong",
				"{DDL={create={type=macro, query={1={}, Type=47}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[]",
				extractor.getInterface().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{create1={query0={query_dictionary={col2=[[@19,66:69='col2',<373>,1:66]], col3=[[@23,76:79='col3',<373>,1:76]], col1=[[@15,56:59='col1',<373>,1:56]]}, table_dictionary={mydb.myschema.source_tab={col2=[[@17,62:64='src',<373>,1:62]], col3=[[@21,72:74='src',<373>,1:72]], col1=[[@13,52:54='src',<373>,1:52]]}}, interface={col2=[{name=col2, table_ref=src}], col3=[{name=col3, table_ref=src}], col1=[{name=col1, table_ref=src}]}, table_alias={src=mydb.myschema.source_tab}}}}",
				extractor.getSymbolTable().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{mydb.myschema.source_tab={col2=[[@17,62:64='src',<373>,1:62]], col3=[[@21,72:74='src',<373>,1:72]], col1=[[@13,52:54='src',<373>,1:52]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{query0={col2=[[@19,66:69='col2',<373>,1:66]], col3=[[@23,76:79='col3',<373>,1:76]], col1=[[@15,56:59='col1',<373>,1:56]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertNull("Snippet should omit optional array-output collector for non-script parses",
				extractor.getSnippet().getArrayOutputCollectorsMap());
	}

	@Test
	public void simpleDdlCreateSequenceExpressionV1Test() {
		final String query = "create sequence mydb.myschema.seq1 start 1";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runDdlParsertest(query, parser);

		Assert.assertEquals("AST is wrong",
				"{DDL={create={type=sequence, name={1=mydb, Type=32, 2=myschema, 3=seq1}, clauses={1={}, Type=49}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[]",
				extractor.getInterface().toString());
		Assert.assertEquals("Symbol Table is wrong", "{create0={}}",
				extractor.getSymbolTable().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertNull("Snippet should omit optional array-output collector for non-script parses",
				extractor.getSnippet().getArrayOutputCollectorsMap());
	}

	@Test
	public void simpleDdlCreateSchemaExpressionV1Test() {
		final String query = "create schema mydb.myschema";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runDdlParsertest(query, parser);

		Assert.assertEquals("AST is wrong",
				"{DDL={create={type=schema, name={1=mydb, Type=32, 2=myschema}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[]",
				extractor.getInterface().toString());
		Assert.assertEquals("Symbol Table is wrong", "{create0={}}",
				extractor.getSymbolTable().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertNull("Snippet should omit optional array-output collector for non-script parses",
				extractor.getSnippet().getArrayOutputCollectorsMap());
	}

	@Test
	public void simpleDdlCreateDatabaseExpressionV1Test() {
		final String query = "create database mydb";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runDdlParsertest(query, parser);

		Assert.assertEquals("AST is wrong",
				"{DDL={create={type=database, name={1=mydb, Type=32}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[]",
				extractor.getInterface().toString());
		Assert.assertEquals("Symbol Table is wrong", "{create0={}}",
				extractor.getSymbolTable().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertNull("Snippet should omit optional array-output collector for non-script parses",
				extractor.getSnippet().getArrayOutputCollectorsMap());
	}

	@Test
	public void simpleDdlCreateRoleExpressionV1Test() {
		final String query = "create role myrole";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runDdlParsertest(query, parser);

		Assert.assertEquals("AST is wrong",
				"{DDL={create={type=role, name={1=myrole, Type=32}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[]",
				extractor.getInterface().toString());
		Assert.assertEquals("Symbol Table is wrong", "{create0={}}",
				extractor.getSymbolTable().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertNull("Snippet should omit optional array-output collector for non-script parses",
				extractor.getSnippet().getArrayOutputCollectorsMap());
	}

	@Test
	public void simpleDdlCreateUserExpressionV1Test() {
		final String query = "create user myuser";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runDdlParsertest(query, parser);

		Assert.assertEquals("AST is wrong",
				"{DDL={create={type=user, name={1=myuser, Type=32}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[]",
				extractor.getInterface().toString());
		Assert.assertEquals("Symbol Table is wrong", "{create0={}}",
				extractor.getSymbolTable().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertNull("Snippet should omit optional array-output collector for non-script parses",
				extractor.getSnippet().getArrayOutputCollectorsMap());
	}

	@Test
	public void simpleDdlCreateStageExpressionV1Test() {
		final String query = "create stage mydb.myschema.stg1";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runDdlParsertest(query, parser);

		Assert.assertEquals("AST is wrong",
				"{DDL={create={type=stage, name={1=mydb, Type=32, 2=myschema, 3=stg1}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[]",
				extractor.getInterface().toString());
		Assert.assertEquals("Symbol Table is wrong", "{create0={}}",
				extractor.getSymbolTable().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertNull("Snippet should omit optional array-output collector for non-script parses",
				extractor.getSnippet().getArrayOutputCollectorsMap());
	}

	@Test
	public void simpleDdlCreateFileFormatExpressionV1Test() {
		final String query = "create file format mydb.myschema.ff1";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runDdlParsertest(query, parser);

		Assert.assertEquals("AST is wrong",
				"{DDL={create={type=file format, name={1=mydb, Type=32, 2=myschema, 3=ff1}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[]",
				extractor.getInterface().toString());
		Assert.assertEquals("Symbol Table is wrong", "{create0={}}",
				extractor.getSymbolTable().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertNull("Snippet should omit optional array-output collector for non-script parses",
				extractor.getSnippet().getArrayOutputCollectorsMap());
	}

	
}