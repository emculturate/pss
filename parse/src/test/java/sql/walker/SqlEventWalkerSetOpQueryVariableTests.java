package sql.walker;

import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import access.Snippet;
import sql.SQLSelectParserParser;

/**
 * Entry 16 — query substitution variables are syntactic set-operation members without
 * manufacturing {@code def_queryN} scopes.
 */
public class SqlEventWalkerSetOpQueryVariableTests extends AbstractSqlParseEventWalkerTest {

	private SqlParseEventWalker parseSuccessfully(String query) {
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
		return extractor;
	}

	@Test
	public void everySetOperatorPublishesConcreteInterfaceWithTrailingQueryVariableTest() {
		String[] operators = {
				"UNION", "UNION ALL", "UNION DISTINCT",
				"EXCEPT", "EXCEPT ALL", "EXCEPT DISTINCT",
				"INTERSECT", "INTERSECT ALL", "INTERSECT DISTINCT"
		};

		for (String operator : operators) {
			SqlParseEventWalker extractor = parseSuccessfully(
					"SELECT agg.a, agg.b FROM ("
							+ "SELECT t.a, t.b FROM tbl t "
							+ operator + " <q.X>) agg");
			Map<String, Object> setScope = findPublishedSetOperationScope(extractor.getSymbolTable());
			Assert.assertNotNull("Expected published set-operation scope for " + operator, setScope);
			Assert.assertEquals("Concrete interface was not promoted for " + operator,
					"{a=query_column, b=query_column}",
					String.valueOf(setScope.get("interface")));
			Assert.assertFalse("Variable operator leaked onto composite scope for " + operator,
					setScope.containsKey("setop"));
			Assert.assertEquals("Unexpected substitution typing for " + operator,
					"{<q.X>=query}", extractor.getSubstitutionsMap().toString());
		}
	}

	@Test
	public void variablePlacementAndMultipleMembersKeepConcreteInterfaceTest() {
		String[] queries = {
				"SELECT agg.a FROM (<q.X> UNION ALL SELECT t.a FROM tbl t) agg",
				"SELECT agg.a FROM (SELECT t.a FROM tbl t UNION ALL <q.X> UNION ALL <q.Y>) agg",
				"SELECT agg.a FROM (SELECT t.a FROM tbl t UNION ALL <q.X> "
						+ "UNION ALL SELECT u.a FROM other u UNION ALL <q.Y>) agg",
				"SELECT agg.a FROM ((SELECT t.a FROM tbl t UNION <q.X>) "
						+ "INTERSECT (SELECT u.a FROM other u EXCEPT <q.Y>)) agg"
		};

		for (String query : queries) {
			SqlParseEventWalker extractor = parseSuccessfully(query);
			Map<String, Object> setScope = findPublishedSetOperationScope(extractor.getSymbolTable());
			Assert.assertNotNull("Expected published set-operation scope: " + query, setScope);
			Assert.assertTrue("Concrete output a was not published: " + setScope,
					setScope.get("interface") instanceof Map<?, ?> interfaceMap
							&& interfaceMap.containsKey("a"));
		}
	}

	@Test
	public void cteAndDerivedTableConsumersSeeConcreteInterfaceTest() {
		parseSuccessfully(
				"WITH agg AS (SELECT t.a, t.b FROM tbl t UNION ALL <q.X>) "
						+ "SELECT agg.a, agg.b FROM agg");
		parseSuccessfully(
				"SELECT outer_q.a FROM (SELECT t.a FROM tbl t EXCEPT <q.X>) outer_q");
	}

	@Test
	public void scalarInListAndInsertContextsKeepConcreteInterfaceTest() {
		SqlParseEventWalker scalar = parseSuccessfully(
				"SELECT (SELECT t.a FROM tbl t UNION ALL <q.X>) AS a FROM outer_t");
		Assert.assertEquals("Set-operation member must remain a query variable in scalar context",
				"{<q.X>=query}", scalar.getSubstitutionsMap().toString());
		parseSuccessfully(
				"SELECT d.a FROM d WHERE d.a IN "
						+ "(SELECT t.a FROM tbl t INTERSECT <q.X>)");
		parseSuccessfully(
				"INSERT INTO target (a) SELECT t.a FROM tbl t UNION ALL <q.X>");
	}

	@Test
	public void variableOnlySetOperationPublishesOpenInterfaceTest() {
		SqlParseEventWalker extractor = parseSuccessfully(
				"SELECT agg.any_column FROM (<q.X> UNION ALL <q.Y>) agg");
		Map<String, Object> setScope = findPublishedSetOperationScope(extractor.getSymbolTable());
		Assert.assertNotNull("Expected variable-only set-operation scope", setScope);
		Assert.assertEquals("Variable-only set operation should publish an open interface",
				"{*=wildcard}", String.valueOf(setScope.get("interface")));
	}

	@Test
	public void missingConcreteOutputStillFatalsTest() {
		final String query =
				"SELECT agg.not_there FROM (SELECT t.a FROM tbl t UNION ALL <q.X>) agg";
		SqlParseEventWalker extractor = runParsertest(query, parse(query));
		Snippet snippet = extractor.getSnippet();
		Assert.assertNotNull(
				"Expected missing concrete output fatal",
				findFatalDiagnosticByCodeAndFragment(
						snippet,
						"QUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIAS",
						"not_there"));
	}

	@Test
	public void concreteColumnCountMismatchStillFatalsWithVariableMemberTest() {
		final String query =
				"SELECT t.a FROM tbl t UNION ALL <q.X> UNION ALL SELECT u.a, u.b FROM other u";
		SqlParseEventWalker extractor = runParsertest(query, parse(query));
		Snippet snippet = extractor.getSnippet();
		Assert.assertNotNull(
				"Expected concrete branch width mismatch fatal",
				findFatalDiagnosticByCodeAndFragment(
						snippet,
						"SET_OPERATION_INTERFACE_COLUMN_COUNT_MISMATCH",
						null));
	}

	@SuppressWarnings("unchecked")
	private Map<String, Object> findPublishedSetOperationScope(Map<String, Object> scope) {
		if (scope == null) {
			return null;
		}
		for (Map.Entry<String, Object> entry : scope.entrySet()) {
			if ((entry.getKey().startsWith("def_union")
					|| entry.getKey().startsWith("def_intersect"))
					&& entry.getValue() instanceof Map<?, ?> setScope) {
				return (Map<String, Object>) setScope;
			}
		}
		for (Object value : scope.values()) {
			if (value instanceof Map<?, ?> childMap) {
				Map<String, Object> result =
						findPublishedSetOperationScope((Map<String, Object>) childMap);
				if (result != null) {
					return result;
				}
			}
		}
		return null;
	}
}
