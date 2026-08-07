package sql.walker;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import static mumble.MumbleConstants.MUMBLE_ALTER_KEY;
import static mumble.MumbleConstants.MUMBLE_ASSIGNMENTS_KEY;
import static mumble.MumbleConstants.MUMBLE_CREATE_KEY;
import static mumble.MumbleConstants.MUMBLE_DELETE_KEY;
import static mumble.MumbleConstants.MUMBLE_DROP_KEY;
import static mumble.MumbleConstants.MUMBLE_FROM_KEY;
import static mumble.MumbleConstants.MUMBLE_INSERT_KEY;
import static mumble.MumbleConstants.MUMBLE_INSERT_PREAMBLE_KEY;
import static mumble.MumbleConstants.MUMBLE_QUERY_KEY;
import static mumble.MumbleConstants.MUMBLE_SELECT_KEY;
import static mumble.MumbleConstants.MUMBLE_TABLE_KEY;
import static mumble.MumbleConstants.MUMBLE_TARGET_TABLE_KEY;
import static mumble.MumbleConstants.MUMBLE_TRUNCATE_KEY;
import static mumble.MumbleConstants.MUMBLE_TYPE_KEY;
import static mumble.MumbleConstants.MUMBLE_UPDATE_KEY;
import static mumble.MumbleConstants.MUMBLE_VALUES_KEY;
import static mumble.MumbleConstants.MUMBLE_WITH_KEY;
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
		Assert.assertFalse("SCRIPT AST should not leak rule Type keys", ast.contains("Type="));

		Map<String, Object> scriptMap = (Map<String, Object>) extractor.getAsTree().get("SCRIPT");
		Assert.assertNotNull("SCRIPT map should be present", scriptMap);

		Map<String, Object> firstStatement = (Map<String, Object>) scriptMap.get("1");
		Map<String, Object> secondStatement = (Map<String, Object>) scriptMap.get("2");
		Assert.assertNotNull("SCRIPT statement 1 should be present", firstStatement);
		Assert.assertNotNull("SCRIPT statement 2 should be present", secondStatement);
		Assert.assertEquals("SCRIPT statement 1 tree is wrong",
				"{select={1={column={name=col1, table_ref=p}}, 2={column={name=*, table_ref=*}}}, from={table={alias=f, table_function={function_name=flatten, parameters={input={function={parameters={1={literal='[1,2]'}}, function_name=parse_json}}}}}}}",
				firstStatement.toString());
		Assert.assertEquals("SCRIPT statement 2 tree is wrong",
				"{select={1={column={name=*, table_ref=*}}}, from={table={alias=f2, table_function={function_name=flatten, parameters={input={function={parameters={1={literal='[3,4]'}}, function_name=parse_json}}}}}}}",
				secondStatement.toString());

		Map<String, Object> scriptSymbolTable = (Map<String, Object>) extractor.getSymbolTable().get("SCRIPT");
		Assert.assertNotNull("SCRIPT symbol table should be present", scriptSymbolTable);
		Map<String, Object> firstStatementSymbols = (Map<String, Object>) scriptSymbolTable.get("1");
		Map<String, Object> secondStatementSymbols = (Map<String, Object>) scriptSymbolTable.get("2");
		Assert.assertEquals("Statement 1 symbol subtree is wrong",
				"{def_query0={query_dictionary={*=[[@5,15:15='*',<291>,1:15]], col1=[[@3,9:12='col1',<391>,1:9]]}, table_dictionary={flatten0={*=[[@5,15:15='*',<291>,1:15]]}}, interface={*=[{name=*, table_ref=*}], col1=[{name=col1, table_ref=p}]}, table_alias={f=flatten0}}}",
				firstStatementSymbols.toString());
		Assert.assertEquals("Statement 2 symbol subtree is wrong",
				"{def_query0={query_dictionary={*=[[@22,77:77='*',<291>,2:8]]}, table_dictionary={flatten1={*=[[@22,77:77='*',<291>,2:8]]}}, interface={*=[{name=*, table_ref=*}]}, table_alias={f2=flatten1}}}",
				secondStatementSymbols.toString());

		Map<String, Object> scriptTableDictionary = (Map<String, Object>) extractor.getTableColumnDictionaryMap().get("SCRIPT");
		Assert.assertNotNull("SCRIPT table dictionary collector should be present", scriptTableDictionary);
		Assert.assertEquals("Statement 1 table dictionary snapshot is wrong",
				"{flatten0={*=[[@5,15:15='*',<291>,1:15]]}}",
				scriptTableDictionary.get("1").toString());
		Assert.assertEquals("Statement 2 table dictionary snapshot is wrong",
				"{flatten1={*=[[@22,77:77='*',<291>,2:8]]}}",
				scriptTableDictionary.get("2").toString());

		Map<String, Object> scriptQueryDictionary = (Map<String, Object>) extractor.getQueryColumnDictionaryMap().get("SCRIPT");
		Assert.assertNotNull("SCRIPT query dictionary collector should be present", scriptQueryDictionary);
		Map<String, Object> firstStatementQueryDictionary = (Map<String, Object>) scriptQueryDictionary.get("1");
		Map<String, Object> secondStatementQueryDictionary = (Map<String, Object>) scriptQueryDictionary.get("2");
		Assert.assertEquals("Statement 1 query dictionary snapshot is wrong",
				"{query0={*=[[@5,15:15='*',<291>,1:15]], col1=[[@3,9:12='col1',<391>,1:9]]}}",
				firstStatementQueryDictionary.toString());
		Assert.assertEquals("Statement 2 query dictionary snapshot is wrong",
				"{query0={*=[[@22,77:77='*',<291>,2:8]]}}",
				secondStatementQueryDictionary.toString());

		Map<String, Object> scriptSubstitutions = (Map<String, Object>) extractor.getSubstitutionsMap().get("SCRIPT");
		Assert.assertNotNull("SCRIPT substitutions collector should be present", scriptSubstitutions);
		Assert.assertEquals("Statement 1 substitutions snapshot is wrong", "{}",
				scriptSubstitutions.get("1").toString());
		Assert.assertEquals("Statement 2 substitutions snapshot is wrong", "{}",
				scriptSubstitutions.get("2").toString());

		Map<String, Object> scriptArrayCollectors = (Map<String, Object>) extractor.getArrayOutputCollectorsMap().get("SCRIPT");
		Assert.assertNotNull("SCRIPT array-output collector should be present", scriptArrayCollectors);
		Map<String, Object> firstStatementArrays = (Map<String, Object>) scriptArrayCollectors.get("1");
		Map<String, Object> secondStatementArrays = (Map<String, Object>) scriptArrayCollectors.get("2");
		Assert.assertEquals("Statement 1 array-output snapshot is wrong",
				"{queryInterface=[*, col1]}",
				firstStatementArrays.toString());
		Assert.assertEquals("Statement 2 array-output snapshot is wrong",
				"{queryInterface=[*]}",
				secondStatementArrays.toString());

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

	@Test
	@SuppressWarnings("unchecked")
	public void mixedScriptStatementTypesTest() {
		final String query = ""
				+ "CREATE TABLE demo.t (id INT);\n"
				+ "TRUNCATE TABLE demo.t;\n"
				+ "DELETE FROM demo.t WHERE id = 1;\n"
				+ "INSERT INTO demo.t (id) SELECT 1;\n"
				+ "UPDATE demo.t SET id = 2 WHERE id = 1;\n"
				+ "SELECT id FROM demo.t;";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runScriptParsertest(query, parser);

		Map<String, Object> scriptMap = (Map<String, Object>) extractor.getAsTree().get("SCRIPT");
		Assert.assertNotNull("SCRIPT map should be present", scriptMap);
		Assert.assertEquals("SCRIPT should contain six isolated statements", 6, scriptMap.size());
		Assert.assertEquals(Set.of("1", "2", "3", "4", "5", "6"), scriptMap.keySet());

		Map<String, Object> createStmt = (Map<String, Object>) scriptMap.get("1");
		Map<String, Object> truncateStmt = (Map<String, Object>) scriptMap.get("2");
		Map<String, Object> deleteStmt = (Map<String, Object>) scriptMap.get("3");
		Map<String, Object> insertStmt = (Map<String, Object>) scriptMap.get("4");
		Map<String, Object> updateStmt = (Map<String, Object>) scriptMap.get("5");
		Map<String, Object> selectStmt = (Map<String, Object>) scriptMap.get("6");

		Assert.assertTrue("Statement 1 should be CREATE TABLE",
				createStmt.containsKey(MUMBLE_CREATE_KEY));
		Assert.assertEquals("TABLE",
				((Map<String, Object>) createStmt.get(MUMBLE_CREATE_KEY)).get(MUMBLE_TYPE_KEY));

		Assert.assertTrue("Statement 2 should be TRUNCATE",
				truncateStmt.containsKey(MUMBLE_TRUNCATE_KEY));
		Assert.assertEquals("TABLE",
				((Map<String, Object>) truncateStmt.get(MUMBLE_TRUNCATE_KEY)).get(MUMBLE_TYPE_KEY));

		Assert.assertTrue("Statement 3 should be DELETE",
				deleteStmt.containsKey(MUMBLE_DELETE_KEY));

		Assert.assertTrue("Statement 4 should be INSERT",
				insertStmt.containsKey(MUMBLE_INSERT_KEY));
		@SuppressWarnings("unchecked")
		Map<String, Object> insertBody = (Map<String, Object>) insertStmt.get(MUMBLE_INSERT_KEY);
		Assert.assertTrue("Statement 4 INSERT should include preamble + target + source",
				insertBody.containsKey(MUMBLE_INSERT_PREAMBLE_KEY)
						&& insertBody.containsKey(MUMBLE_TARGET_TABLE_KEY)
						&& insertBody.containsKey(MUMBLE_FROM_KEY));
		Assert.assertEquals("insert_into", insertBody.get(MUMBLE_INSERT_PREAMBLE_KEY));
		Map<String, Object> insertTarget = (Map<String, Object>) ((Map<String, Object>) insertBody.get(MUMBLE_TARGET_TABLE_KEY))
				.get(MUMBLE_TABLE_KEY);
		Assert.assertEquals("demo", insertTarget.get("schema"));
		Assert.assertEquals("t", insertTarget.get("table"));

		Assert.assertTrue("Statement 5 should be UPDATE",
				updateStmt.containsKey(MUMBLE_UPDATE_KEY));
		Assert.assertTrue("Statement 5 UPDATE should include SET assignments",
				((Map<String, Object>) updateStmt.get(MUMBLE_UPDATE_KEY)).containsKey(MUMBLE_ASSIGNMENTS_KEY));

		Assert.assertTrue("Statement 6 should be SELECT",
				selectStmt.containsKey(MUMBLE_SELECT_KEY));

		Map<String, Object> scriptTableDictionary = (Map<String, Object>) extractor.getTableColumnDictionaryMap().get("SCRIPT");
		Map<String, Object> scriptQueryDictionary = (Map<String, Object>) extractor.getQueryColumnDictionaryMap().get("SCRIPT");
		Map<String, Object> scriptSubstitutions = (Map<String, Object>) extractor.getSubstitutionsMap().get("SCRIPT");
		Map<String, Object> scriptArrayCollectors = (Map<String, Object>) extractor.getArrayOutputCollectorsMap().get("SCRIPT");
		Map<String, Object> scriptSymbolTable = (Map<String, Object>) extractor.getSymbolTable().get("SCRIPT");

		Assert.assertEquals("Each SCRIPT statement should have its own table-dictionary snapshot", 6,
				scriptTableDictionary.size());
		Assert.assertEquals("Each SCRIPT statement should have its own query-dictionary snapshot", 6,
				scriptQueryDictionary.size());
		Assert.assertEquals("Each SCRIPT statement should have its own substitutions snapshot", 6,
				scriptSubstitutions.size());
		Assert.assertEquals("Each SCRIPT statement should have its own array-output snapshot", 6,
				scriptArrayCollectors.size());
		Assert.assertEquals("Each SCRIPT statement should have its own symbol-table snapshot", 6,
				scriptSymbolTable.size());

		Assert.assertNotEquals("SELECT query dictionary should not leak into CREATE snapshot",
				scriptQueryDictionary.get("6"), scriptQueryDictionary.get("1"));
		Assert.assertNotEquals("INSERT symbol subtree should not be shared with TRUNCATE",
				scriptSymbolTable.get("4"), scriptSymbolTable.get("2"));
	}

	/**
	 * Covers every {@code sql_statement} alternative from {@code SQLSelectParser.g4}:
	 * {@code ddl_primary} (create, alter, drop, truncate), {@code dml_primary}
	 * (insert, update, delete, values), {@code with_query}, and {@code query_expression}.
	 */
	@Test
	@SuppressWarnings("unchecked")
	public void fullScriptPrimaryCoverageTest() {
		final String query = ""
				+ "CREATE TABLE demo.stage (id INT);\n"
				+ "ALTER TABLE demo.stage RENAME TO demo.stg;\n"
				+ "DROP TABLE demo.old IF EXISTS;\n"
				+ "TRUNCATE TABLE demo.stg;\n"
				+ "INSERT INTO demo.stg (id) VALUES (1);\n"
				+ "UPDATE demo.stg SET id = 2 WHERE id = 1;\n"
				+ "DELETE FROM demo.stg WHERE id = 2;\n"
				+ "(VALUES (10), (20));\n"
				+ "WITH picked AS (SELECT id FROM demo.stg) SELECT id FROM picked;\n"
				+ "SELECT id FROM demo.stg;";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runScriptParsertest(query, parser);

		Map<String, Object> scriptMap = (Map<String, Object>) extractor.getAsTree().get("SCRIPT");
		Assert.assertNotNull("SCRIPT map should be present", scriptMap);
		Assert.assertEquals("SCRIPT should contain ten isolated statements", 10, scriptMap.size());
		Assert.assertEquals(Set.of("1", "2", "3", "4", "5", "6", "7", "8", "9", "10"), scriptMap.keySet());

		Map<String, Object> createStmt = (Map<String, Object>) scriptMap.get("1");
		Map<String, Object> alterStmt = (Map<String, Object>) scriptMap.get("2");
		Map<String, Object> dropStmt = (Map<String, Object>) scriptMap.get("3");
		Map<String, Object> truncateStmt = (Map<String, Object>) scriptMap.get("4");
		Map<String, Object> insertStmt = (Map<String, Object>) scriptMap.get("5");
		Map<String, Object> updateStmt = (Map<String, Object>) scriptMap.get("6");
		Map<String, Object> deleteStmt = (Map<String, Object>) scriptMap.get("7");
		Map<String, Object> valuesStmt = (Map<String, Object>) scriptMap.get("8");
		Map<String, Object> withStmt = (Map<String, Object>) scriptMap.get("9");
		Map<String, Object> selectStmt = (Map<String, Object>) scriptMap.get("10");

		Assert.assertTrue("Statement 1 should be CREATE", createStmt.containsKey(MUMBLE_CREATE_KEY));
		Assert.assertEquals("TABLE",
				((Map<String, Object>) createStmt.get(MUMBLE_CREATE_KEY)).get(MUMBLE_TYPE_KEY));
		Assert.assertEquals("stage",
				((Map<String, Object>) ((Map<String, Object>) createStmt.get(MUMBLE_CREATE_KEY)).get(MUMBLE_TABLE_KEY))
						.get("table"));

		Assert.assertTrue("Statement 2 should be ALTER", alterStmt.containsKey(MUMBLE_ALTER_KEY));
		Assert.assertEquals("TABLE",
				((Map<String, Object>) alterStmt.get(MUMBLE_ALTER_KEY)).get(MUMBLE_TYPE_KEY));

		Assert.assertTrue("Statement 3 should be DROP", dropStmt.containsKey(MUMBLE_DROP_KEY));
		Assert.assertEquals("TABLE",
				((Map<String, Object>) dropStmt.get(MUMBLE_DROP_KEY)).get(MUMBLE_TYPE_KEY));
		Assert.assertTrue("Statement 3 DROP should retain IF EXISTS option text",
				dropStmt.toString().toLowerCase().contains("if exists"));

		Assert.assertTrue("Statement 4 should be TRUNCATE", truncateStmt.containsKey(MUMBLE_TRUNCATE_KEY));

		Assert.assertTrue("Statement 5 should be INSERT",
				insertStmt.containsKey(MUMBLE_INSERT_KEY));
		@SuppressWarnings("unchecked")
		Map<String, Object> insertBody = (Map<String, Object>) insertStmt.get(MUMBLE_INSERT_KEY);
		Assert.assertTrue("Statement 5 INSERT should include preamble + source",
				insertBody.containsKey(MUMBLE_INSERT_PREAMBLE_KEY) && insertBody.containsKey(MUMBLE_FROM_KEY));

		Assert.assertTrue("Statement 6 should be UPDATE", updateStmt.containsKey(MUMBLE_UPDATE_KEY));

		Assert.assertTrue("Statement 7 should be DELETE", deleteStmt.containsKey(MUMBLE_DELETE_KEY));

		Assert.assertTrue("Statement 8 should be VALUES", valuesStmt.containsKey(MUMBLE_VALUES_KEY));

		Assert.assertTrue("Statement 9 should be WITH ... SELECT",
				withStmt.containsKey(MUMBLE_WITH_KEY) && withStmt.containsKey(MUMBLE_QUERY_KEY));
		Assert.assertTrue("Statement 9 WITH clause should name CTE picked",
				withStmt.toString().contains("picked"));
		Assert.assertTrue("Statement 9 main query should be SELECT",
				((Map<String, Object>) withStmt.get(MUMBLE_QUERY_KEY)).containsKey(MUMBLE_SELECT_KEY));

		Assert.assertTrue("Statement 10 should be plain SELECT", selectStmt.containsKey(MUMBLE_SELECT_KEY));
		Assert.assertFalse("Statement 10 should not be a WITH query", selectStmt.containsKey(MUMBLE_WITH_KEY));

		Map<String, Object> scriptTableDictionary = (Map<String, Object>) extractor.getTableColumnDictionaryMap().get("SCRIPT");
		Map<String, Object> scriptQueryDictionary = (Map<String, Object>) extractor.getQueryColumnDictionaryMap().get("SCRIPT");
		Map<String, Object> scriptSymbolTable = (Map<String, Object>) extractor.getSymbolTable().get("SCRIPT");

		Assert.assertEquals("Each SCRIPT statement should have its own table-dictionary snapshot", 10,
				scriptTableDictionary.size());
		Assert.assertEquals("Each SCRIPT statement should have its own query-dictionary snapshot", 10,
				scriptQueryDictionary.size());
		Assert.assertEquals("Each SCRIPT statement should have its own symbol-table snapshot", 10,
				scriptSymbolTable.size());

		Assert.assertNotEquals("WITH-query dictionary should not match plain SELECT dictionary",
				scriptQueryDictionary.get("9"), scriptQueryDictionary.get("10"));
		Assert.assertNotEquals("VALUES symbol subtree should not match DELETE symbol subtree",
				scriptSymbolTable.get("8"), scriptSymbolTable.get("7"));
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
				"{def_create1={def_query0={query_dictionary={*=[[@5,28:28='*',<291>,1:28]]}, table_dictionary={flatten0={*=[[@5,28:28='*',<291>,1:28]]}}, interface={*=[{name=*, table_ref=*}]}, table_alias={f=flatten0}}}}",
				extractor.getSymbolTable().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{flatten0={*=[[@5,28:28='*',<291>,1:28]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{query0={*=[[@5,28:28='*',<291>,1:28]]}}",
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
				"{DDL={create={type=table, table={schema=myschema, dbname=mydb, table=tab2}, query={select={1={column={name=col1, table_ref=src}}, 2={column={name=col2, table_ref=src}}, 3={column={name=col3, table_ref=src}}}, from={table={alias=src, schema=myschema, dbname=mydb, table=source_tab}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[]",
				extractor.getInterface().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_create1={def_query0={query_dictionary={col2=[[@15,56:59='col2',<391>,1:56]], col3=[[@19,66:69='col3',<391>,1:66]], col1=[[@11,46:49='col1',<391>,1:46]]}, table_dictionary={mydb.myschema.source_tab={col2=[[@13,52:54='src',<391>,1:52]], col3=[[@17,62:64='src',<391>,1:62]], col1=[[@9,42:44='src',<391>,1:42]]}}, interface={col2=[{name=col2, table_ref=src}], col3=[{name=col3, table_ref=src}], col1=[{name=col1, table_ref=src}]}, table_alias={src=mydb.myschema.source_tab}}}}",
				extractor.getSymbolTable().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{mydb.myschema.source_tab={col2=[[@13,52:54='src',<391>,1:52]], col3=[[@17,62:64='src',<391>,1:62]], col1=[[@9,42:44='src',<391>,1:42]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{query0={col2=[[@15,56:59='col2',<391>,1:56]], col3=[[@19,66:69='col3',<391>,1:66]], col1=[[@11,46:49='col1',<391>,1:46]]}}",
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
				"{DDL={create={type=index, name={schema=myschema, dbname=mydb, table=idx1}, table={schema=myschema, dbname=mydb, table=source_tab}, columns={1={column={name=col1, table_ref=null}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[]",
				extractor.getInterface().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_create0={unresolved_column={col1={column={name=col1, table_ref=null}, locations=[[@14,61:64='col1',<391>,1:61]]}}}}",
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

		Assert.assertEquals("AST is wrong",
				"{DDL={create={type=view, name={schema=myschema, dbname=mydb, table=vw1}, query={select={1={column={name=col1, table_ref=src}}, 2={column={name=col2, table_ref=src}}, 3={column={name=col3, table_ref=src}}}, from={table={alias=src, schema=myschema, dbname=mydb, table=source_tab}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[]",
				extractor.getInterface().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_create1={def_query0={query_dictionary={col2=[[@15,54:57='col2',<391>,1:54]], col3=[[@19,64:67='col3',<391>,1:64]], col1=[[@11,44:47='col1',<391>,1:44]]}, table_dictionary={mydb.myschema.source_tab={col2=[[@13,50:52='src',<391>,1:50]], col3=[[@17,60:62='src',<391>,1:60]], col1=[[@9,40:42='src',<391>,1:40]]}}, interface={col2=[{name=col2, table_ref=src}], col3=[{name=col3, table_ref=src}], col1=[{name=col1, table_ref=src}]}, table_alias={src=mydb.myschema.source_tab}}}}",
				extractor.getSymbolTable().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{mydb.myschema.source_tab={col2=[[@13,50:52='src',<391>,1:50]], col3=[[@17,60:62='src',<391>,1:60]], col1=[[@9,40:42='src',<391>,1:40]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{query0={col2=[[@15,54:57='col2',<391>,1:54]], col3=[[@19,64:67='col3',<391>,1:64]], col1=[[@11,44:47='col1',<391>,1:44]]}}",
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

		Assert.assertEquals("AST is wrong",
				"{DDL={create={type=materialized view, name={schema=myschema, dbname=mydb, table=mv1}, query={select={1={column={name=col1, table_ref=src}}, 2={column={name=col2, table_ref=src}}, 3={column={name=col3, table_ref=src}}}, from={table={alias=src, schema=myschema, dbname=mydb, table=source_tab}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[]",
				extractor.getInterface().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_create1={def_query0={query_dictionary={col2=[[@16,67:70='col2',<391>,1:67]], col3=[[@20,77:80='col3',<391>,1:77]], col1=[[@12,57:60='col1',<391>,1:57]]}, table_dictionary={mydb.myschema.source_tab={col2=[[@14,63:65='src',<391>,1:63]], col3=[[@18,73:75='src',<391>,1:73]], col1=[[@10,53:55='src',<391>,1:53]]}}, interface={col2=[{name=col2, table_ref=src}], col3=[{name=col3, table_ref=src}], col1=[{name=col1, table_ref=src}]}, table_alias={src=mydb.myschema.source_tab}}}}",
				extractor.getSymbolTable().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{mydb.myschema.source_tab={col2=[[@14,63:65='src',<391>,1:63]], col3=[[@18,73:75='src',<391>,1:73]], col1=[[@10,53:55='src',<391>,1:53]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{query0={col2=[[@16,67:70='col2',<391>,1:67]], col3=[[@20,77:80='col3',<391>,1:77]], col1=[[@12,57:60='col1',<391>,1:57]]}}",
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
				"{DDL={create={type=function, name={schema=myschema, table=fn1}, parameters=arg1 int, data_type={type=INT}, clauses=language sql}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[]",
				extractor.getInterface().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_create0={}}",
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
				"{DDL={create={type=procedure, name={schema=myschema, dbname=mydb, table=pr1}, parameters=arg1 int, clauses=language sql}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[]",
				extractor.getInterface().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_create0={}}",
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
				"{DDL={create={type=macro, name={schema=myschema, dbname=mydb, table=mac1}, parameters=arg1 int, query={select={1={column={name=col1, table_ref=src}}, 2={column={name=col2, table_ref=src}}, 3={column={name=col3, table_ref=src}}}, from={table={alias=src, schema=myschema, dbname=mydb, table=source_tab}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[]",
				extractor.getInterface().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_create1={def_query0={query_dictionary={col2=[[@19,66:69='col2',<391>,1:66]], col3=[[@23,76:79='col3',<391>,1:76]], col1=[[@15,56:59='col1',<391>,1:56]]}, table_dictionary={mydb.myschema.source_tab={col2=[[@17,62:64='src',<391>,1:62]], col3=[[@21,72:74='src',<391>,1:72]], col1=[[@13,52:54='src',<391>,1:52]]}}, interface={col2=[{name=col2, table_ref=src}], col3=[{name=col3, table_ref=src}], col1=[{name=col1, table_ref=src}]}, table_alias={src=mydb.myschema.source_tab}}}}",
				extractor.getSymbolTable().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{mydb.myschema.source_tab={col2=[[@17,62:64='src',<391>,1:62]], col3=[[@21,72:74='src',<391>,1:72]], col1=[[@13,52:54='src',<391>,1:52]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{query0={col2=[[@19,66:69='col2',<391>,1:66]], col3=[[@23,76:79='col3',<391>,1:76]], col1=[[@15,56:59='col1',<391>,1:56]]}}",
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
				"{DDL={create={type=sequence, name={schema=myschema, dbname=mydb, table=seq1}, clauses=start 1}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[]",
				extractor.getInterface().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_create0={}}",
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
				"{DDL={create={type=schema, name={schema=mydb, table=myschema}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[]",
				extractor.getInterface().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_create0={}}",
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
				"{DDL={create={type=database, name={table=mydb}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[]",
				extractor.getInterface().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_create0={}}",
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
				"{DDL={create={type=role, name={table=myrole}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[]",
				extractor.getInterface().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_create0={}}",
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
				"{DDL={create={type=user, name={table=myuser}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[]",
				extractor.getInterface().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_create0={}}",
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
				"{DDL={create={type=stage, name={schema=myschema, dbname=mydb, table=stg1}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[]",
				extractor.getInterface().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_create0={}}",
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
				"{DDL={create={type=file format, name={schema=myschema, dbname=mydb, table=ff1}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[]",
				extractor.getInterface().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_create0={}}",
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
	public void simpleDdlDropTableExpressionV1Test() {
		final String query = "drop table mydb.myschema.tab1 if exists";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runDdlParsertest(query, parser);

		Assert.assertEquals("AST is wrong",
				"{DDL={drop={type=table, name={schema=myschema, dbname=mydb, table=tab1}, options=if exists}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[]",
				extractor.getInterface().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_drop0={}}",
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
	public void simpleDdlAlterTableExpressionV1Test() {
		final String query = "alter table mydb.myschema.tab1 rename to tab2";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runDdlParsertest(query, parser);

		Assert.assertEquals("AST is wrong",
				"{DDL={alter={type=table, name={schema=myschema, dbname=mydb, table=tab1}, options=rename to tab2}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[]",
				extractor.getInterface().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_alter0={}}",
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