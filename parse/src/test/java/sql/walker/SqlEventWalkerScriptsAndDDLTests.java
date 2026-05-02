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
	public void simpleDdlTest() {
		final String query = "create table tab1 as select * from table(flatten(input=>parse_json('[1,2]'))) f";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runDdlParsertest(query, parser);

		String ast = extractor.getAsTree().toString();
		Assert.assertTrue("AST should contain table_function", ast.contains("table_function="));
		Assert.assertNull("Snippet should omit optional array-output collector for non-script parses",
				extractor.getSnippet().getArrayOutputCollectorsMap());
	}

}