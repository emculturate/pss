package sql.walker;

import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import sql.SQLSelectParserParser;

import static mumble.MumbleConstants.MUMBLE_INSERT_KEY;
import static mumble.MumbleConstants.MUMBLE_INSERT_PREAMBLE_KEY;
import static mumble.MumbleConstants.MUMBLE_ON_CONFLICT_KEY;
import static mumble.MumbleConstants.MUMBLE_RETURNING_KEY;
import static mumble.MumbleConstants.MUMBLE_WITH_KEY;
import static mumble.SQLParserEndPoints.SQLPARSER_SQL_TREE_KEY;

/**
 * Fast gate for INSERT AST {@code insert={...}} wrapping.
 * Run via {@code mvn -Pinsert-ast-gate test}.
 */
public class InsertAstWrapGateTests extends AbstractSqlParseEventWalkerTest {

	@SuppressWarnings("unchecked")
	private static Map<String, Object> requireInsertBody(SqlParseEventWalker extractor) {
		Map<String, Object> sqlTree = (Map<String, Object>) extractor.getAsTree().get(SQLPARSER_SQL_TREE_KEY);
		Assert.assertNotNull("SQL tree should be present", sqlTree);
		Assert.assertTrue("Top-level INSERT should be wrapped in insert={}", sqlTree.containsKey(MUMBLE_INSERT_KEY));
		return (Map<String, Object>) sqlTree.get(MUMBLE_INSERT_KEY);
	}

	@Test
	public void snowflakeValuesInsertWrappedTest() {
		final String query = "INSERT INTO employees (score, rank_bucket) VALUES (100, 1)";
		SqlParseEventWalker extractor = runParsertest(query, parse(query));
		assertNoWalkerDiagnostics(extractor);

		Map<String, Object> insert = requireInsertBody(extractor);
		Assert.assertEquals("insert_into", insert.get(MUMBLE_INSERT_PREAMBLE_KEY));
		Assert.assertTrue(insert.containsKey("from"));
		Assert.assertTrue(insert.containsKey("target_table"));
	}

	@Test
	public void snowflakeInsertSelectWrappedTest() {
		final String query = "INSERT INTO employees (score) SELECT score FROM perf_feed";
		SqlParseEventWalker extractor = runParsertest(query, parse(query));
		assertNoWalkerDiagnostics(extractor);

		Map<String, Object> insert = requireInsertBody(extractor);
		Assert.assertEquals("insert_into", insert.get(MUMBLE_INSERT_PREAMBLE_KEY));
		Assert.assertTrue(((Map<?, ?>) insert.get("from")).containsKey("select"));
	}

	@Test
	public void postgresOnConflictDoUpdateWrappedTest() {
		final String query = "INSERT INTO employees (emp_id, score) VALUES (1, 100)"
				+ " ON CONFLICT (emp_id) DO UPDATE SET score = EXCLUDED.score";
		SqlParseEventWalker extractor = runParsertest(query, parse(query));
		assertNoWalkerDiagnostics(extractor);

		Map<String, Object> insert = requireInsertBody(extractor);
		Assert.assertTrue(insert.containsKey(MUMBLE_ON_CONFLICT_KEY));
	}

	@Test
	public void postgresDefaultValuesWrappedTest() {
		final String query = "INSERT INTO employees DEFAULT VALUES";
		SqlParseEventWalker extractor = runParsertest(query, parse(query));
		assertNoWalkerDiagnostics(extractor);

		Map<String, Object> insert = requireInsertBody(extractor);
		Assert.assertTrue(((Map<?, ?>) insert.get("from")).containsKey("default_values"));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void withInsertCteBodyWrappedTest() {
		final String query = "WITH ins AS (INSERT INTO employees (score, rank_bucket) VALUES (100, 1) RETURNING score, emp_id)"
				+ " SELECT score FROM ins";
		SqlParseEventWalker extractor = runParsertest(query, parse(query));
		assertNoWalkerDiagnostics(extractor);

		Map<String, Object> sqlTree = (Map<String, Object>) extractor.getAsTree().get(SQLPARSER_SQL_TREE_KEY);
		Assert.assertTrue(sqlTree.containsKey(MUMBLE_WITH_KEY));
		Assert.assertFalse("Outer SQL should not be a bare INSERT", sqlTree.containsKey(MUMBLE_INSERT_KEY));

		Map<String, Object> withClause = (Map<String, Object>) sqlTree.get(MUMBLE_WITH_KEY);
		Map<String, Object> cteItem = (Map<String, Object>) withClause.get("1");
		Map<String, Object> cteBody = (Map<String, Object>) cteItem.get("cte");
		Assert.assertTrue("CTE INSERT body should be wrapped in insert={}", cteBody.containsKey(MUMBLE_INSERT_KEY));
		Map<String, Object> insert = (Map<String, Object>) cteBody.get(MUMBLE_INSERT_KEY);
		Assert.assertTrue(insert.containsKey(MUMBLE_RETURNING_KEY));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void withInsertOuterStatementWrappedTest() {
		final String query = "WITH staged AS (SELECT emp_id, score FROM perf_feed)"
				+ " INSERT INTO employees (score, rank_bucket) SELECT score, emp_id FROM staged";
		SqlParseEventWalker extractor = runParsertest(query, parse(query));
		assertNoWalkerDiagnostics(extractor);

		Map<String, Object> sqlTree = (Map<String, Object>) extractor.getAsTree().get(SQLPARSER_SQL_TREE_KEY);
		Assert.assertTrue(sqlTree.containsKey(MUMBLE_WITH_KEY));
		Assert.assertFalse("Outer SQL should not be a bare INSERT", sqlTree.containsKey(MUMBLE_INSERT_KEY));

		Map<String, Object> queryNode = (Map<String, Object>) sqlTree.get("query");
		Assert.assertTrue("WITH main statement INSERT should be under query.insert", queryNode.containsKey(MUMBLE_INSERT_KEY));
		Map<String, Object> insert = (Map<String, Object>) queryNode.get(MUMBLE_INSERT_KEY);
		Assert.assertEquals("insert_into", insert.get(MUMBLE_INSERT_PREAMBLE_KEY));
	}
}
