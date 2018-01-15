/**
 * Package contains classes used to normalize, analyze, generate and perform standard operations with SQL Templates
 * 
 * SQL Templates are snippets of SQL that contain substitution variables of various types and which represent re-usable, macro-like SQL phrases.
 * 
 * SQL Templates are composable, so long as variables are filled with compatible substitution SQL by type.
 */
package mumble.sql.template;

import java.util.HashMap;
import java.util.Set;

import static mumble.sql.MumbleConstants.*;

import mumble.sql.Snippet;

/**
 * The SQLNormalizer takes a SQL AST object and applies several transformations
 * to it to make it easier to compare to other SQL AST trees. It should be used
 * before the template discoveror attempts to generate a common template.
 * 
 * @author Geoff Howe January 2018
 *
 */
public class SQLNormalizer {

	private Snippet snip;

	/**
	 * @param snip
	 */
	public SQLNormalizer(Snippet snip) {
		super();
		this.snip = snip;
	}

	public Snippet getSnip() {
		return snip;
	}

	public void setSnip(Snippet snip) {
		this.snip = snip;
	}

	@Override
	public String toString() {
		return "SQLNormalizer [snip=" + snip.getSqlAbstractTree() + "]";
	}

	// Ok now to business

	public boolean normalize() {
		boolean status = true;
		try {
			// Determine Type of Snippet
			HashMap<String, Object> hold = snip.getSqlAbstractTree();
			HashMap<String, Object> norm = new HashMap<String, Object>();

			if (hold.containsKey("SQL")) {
				// Query AST
				System.out.println("Starting walking SQL Tree");
				traverseSqlTree(hold);
			} else if (hold.containsKey("PREDICAND")) {
				// Predicand AST

			} else if (hold.containsKey("CONDITION")) {
				// Condition AST

			} else {
				// Unrecognized AST type
				System.err.println("Unrecognized SQL AST: " + snip.toString());
			}

		} catch (Exception e) {
			System.err.println("Normalizing exception: " + snip.toString());
			System.err.println("Normalizing Exception: " + e.getMessage());
			return false;
		} finally {

		}
		return status;
	}

	public void traverseSqlTree(HashMap<String, Object> tree) {

		Set<String> keys = tree.keySet();
		for (String key : keys) {

			switch (key) {

			case PSS_ALIAS_KEY:
				System.out.println("alias");
				System.out.println(tree.get(key));
				break;
			case PSS_AND_KEY:
				System.out.println("and");
				traverseSqlTree((HashMap<String, Object>) tree.get(key));
				break;
			case PSS_ASSIGNMENTS_KEY:
				System.out.println("assignments");
				break;
			case PSS_BETWEEN_KEY:
				System.out.println("between");
				traverseSqlTree((HashMap<String, Object>) tree.get(key));
				break;
			case PSS_CALCULATION_KEY:
				System.out.println("calc");
				traverseSqlTree((HashMap<String, Object>) tree.get(key));
				break;
			case PSS_CASE_KEY:
				System.out.println("case");
				traverseSqlTree((HashMap<String, Object>) tree.get(key));
				break;
			case PSS_CLAUSES_KEY:
				System.out.println("clauses");
				break;
			case PSS_COLUMN_KEY:
				System.out.println("column");
				traverseSqlTree((HashMap<String, Object>) tree.get(key));
				break;
			case PSS_CONCATENATE_KEY:
				System.out.println("concatenate");
				traverseSqlTree((HashMap<String, Object>) tree.get(key));
				break;
			case PSS_CONDITION_KEY:
				System.out.println("condition");
				traverseSqlTree((HashMap<String, Object>) tree.get(key));
				break;
			case PSS_CONDITION_TREE_KEY:
				System.out.println("CONDITION");
				traverseSqlTree((HashMap<String, Object>) tree.get(key));
				break;
			case PSS_DATABASE_NAME_KEY:
				System.out.println("dbname");
				break;
			case PSS_ELSE_KEY:
				System.out.println("else");
				break;
			case PSS_FROM_KEY:
				System.out.println("from");
				traverseSqlTree((HashMap<String, Object>) tree.get(key));
				break;
			case PSS_FUNCTION_KEY:
				System.out.println("function");
				traverseSqlTree((HashMap<String, Object>) tree.get(key));
				break;
			case PSS_FUNCTION_NAME_KEY:
				System.out.println("function_name");
				break;
			case PSS_GROUPBY_KEY:
				System.out.println("groupby");
				traverseSqlTree((HashMap<String, Object>) tree.get(key));
				break;
			case PSS_HAVING_KEY:
				System.out.println("having");
				traverseSqlTree((HashMap<String, Object>) tree.get(key));
				break;
			case PSS_IN_KEY:
				System.out.println("in");
				traverseSqlTree((HashMap<String, Object>) tree.get(key));
				break;
			case PSS_IN_LIST_KEY:
				System.out.println("in_list");
				break;
			case PSS_INSERT_KEY:
				System.out.println("insert");
				traverseSqlTree((HashMap<String, Object>) tree.get(key));
				break;
			case PSS_INTERSECT_KEY:
				System.out.println("intersect");
				traverseSqlTree((HashMap<String, Object>) tree.get(key));
				break;
			case PSS_ITEM_KEY:
				System.out.println("item");
				break;
			case PSS_JOIN_EXTENSION_KEY:
				System.out.println("extension");
				break;
			case PSS_JOIN_KEY:
				System.out.println("join");
				traverseSqlTree((HashMap<String, Object>) tree.get(key));
				break;
			case PSS_JOIN_ON_KEY:
				System.out.println("on");
				traverseSqlTree((HashMap<String, Object>) tree.get(key));
				break;
			case PSS_JOIN_TYPE_KEY:
				System.out.println("join_type");
				traverseSqlTree((HashMap<String, Object>) tree.get(key));
				break;
			case PSS_LEFT_KEY:
				System.out.println("left");
				traverseSqlTree((HashMap<String, Object>) tree.get(key));
				break;
			case PSS_LIMIT_KEY:
				System.out.println("limit");
				break;
			case PSS_LIST_KEY:
				System.out.println("list");
				traverseSqlTree((HashMap<String, Object>) tree.get(key));
				break;
			case PSS_LITERAL_KEY:
				System.out.println("literal");
				break;
			case PSS_LOOKUP_KEY:
				System.out.println("lookup");
				traverseSqlTree((HashMap<String, Object>) tree.get(key));
				break;
			case PSS_NAME_KEY:
				System.out.println("name");
				break;
			case PSS_NOT_IN_LIST_KEY:
				System.out.println("not_in_list");
				traverseSqlTree((HashMap<String, Object>) tree.get(key));
				break;
			case PSS_NULL_LITERAL_KEY:
				System.out.println("null_literal");
				break;
			case PSS_NULL_ORDER_KEY:
				System.out.println("null_order");
				break;
			case PSS_OPERATOR_KEY:
				System.out.println("operator");
				break;
			case PSS_OR_KEY:
				System.out.println("or");
				traverseSqlTree((HashMap<String, Object>) tree.get(key));
				break;
			case PSS_ORDERBY_KEY:
				System.out.println("orderby");
				traverseSqlTree((HashMap<String, Object>) tree.get(key));
				break;
			case PSS_OVER_KEY:
				System.out.println("over");
				traverseSqlTree((HashMap<String, Object>) tree.get(key));
				break;
			case PSS_PARAMETERS_KEY:
				System.out.println("parameters");
				traverseSqlTree((HashMap<String, Object>) tree.get(key));
				break;
			case PSS_PARENTHESES_KEY:
				System.out.println("parentheses");
				traverseSqlTree((HashMap<String, Object>) tree.get(key));
				break;
			case PSS_PARTITION_BY_KEY:
				System.out.println("partition_by");
				traverseSqlTree((HashMap<String, Object>) tree.get(key));
				break;
			case PSS_PREDICAND_KEY:
				System.out.println("predicand");
				traverseSqlTree((HashMap<String, Object>) tree.get(key));
				break;
			case PSS_PREDICAND_TREE_KEY:
				System.out.println("PREDICAND");
				traverseSqlTree((HashMap<String, Object>) tree.get(key));
				break;
			case PSS_QUALIFIER_KEY:
				System.out.println("qualifier");
				break;
			case PSS_QUERY_KEY:
				System.out.println("query");
				traverseSqlTree((HashMap<String, Object>) tree.get(key));
				break;
			case PSS_RANGE_BEGIN_KEY:
				System.out.println("begin");
				break;
			case PSS_RANGE_END_KEY:
				System.out.println("end");
				break;
			case PSS_RETURNING_KEY:
				System.out.println("returning");
				break;
			case PSS_RIGHT_FACTOR_KEY:
				System.out.println("right");
				traverseSqlTree((HashMap<String, Object>) tree.get(key));
				break;
			case PSS_SCHEMA_KEY:
				System.out.println("schema");
				break;
			case PSS_SELECT_KEY:
				System.out.println("select");
				traverseSqlTree((HashMap<String, Object>) tree.get(key));
				break;
			case PSS_SET_KEY:
				System.out.println("set");
				break;
			case PSS_SORT_ORDER_KEY:
				System.out.println("sort_order");
				traverseSqlTree((HashMap<String, Object>) tree.get(key));
				break;
			case PSS_SQL_TREE_KEY:
				System.out.println("SQL");
				traverseSqlTree((HashMap<String, Object>) tree.get(key));
				break;
			case PSS_SUBSTITUTION_KEY:
				System.out.println("substitution");
				traverseSqlTree((HashMap<String, Object>) tree.get(key));
				break;
			case PSS_SYMMETRY_KEY:
				System.out.println("symmetry");
				break;
			case PSS_TABLE_KEY:
				System.out.println("table");
				traverseSqlTree((HashMap<String, Object>) tree.get(key));
				break;
			case PSS_TABLE_REF_KEY:
				System.out.println("table_ref");
				break;
			case PSS_THEN_KEY:
				System.out.println("then");
				traverseSqlTree((HashMap<String, Object>) tree.get(key));
				break;
			case PSS_TO_KEY:
				System.out.println("to");
				break;
			case PSS_TRIM_CHARACTER_KEY:
				System.out.println("trim_character");
				break;
			case PSS_TYPE_KEY:
				System.out.println("type");
				traverseSqlTree((HashMap<String, Object>) tree.get(key));
				break;
			case PSS_UNION_KEY:
				System.out.println("union");
				traverseSqlTree((HashMap<String, Object>) tree.get(key));
				break;
			case PSS_UPDATE_KEY:
				System.out.println("update");
				traverseSqlTree((HashMap<String, Object>) tree.get(key));
				break;
			case PSS_VALUE_KEY:
				System.out.println("value");
				break;
			case PSS_WHEN_KEY:
				System.out.println("when");
				traverseSqlTree((HashMap<String, Object>) tree.get(key));
				break;
			case PSS_WHERE_KEY:
				System.out.println("where");
				traverseSqlTree((HashMap<String, Object>) tree.get(key));
				break;
			case PSS_WINDOW_FUNCTION_KEY:
				System.out.println("window_function");
				traverseSqlTree((HashMap<String, Object>) tree.get(key));
				break;
			case PSS_WITH_KEY:
				System.out.println("with");
				traverseSqlTree((HashMap<String, Object>) tree.get(key));
				break;
			default:
				System.out.println("DEFAULT:" + key + " - " + tree.get(key));
				traverseSqlTree((HashMap<String, Object>) tree.get(key));
				break;

			}
		}
	}
}
