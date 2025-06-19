package mumble.template;

import java.util.HashMap;
import java.util.Set;

import static mumble.MumbleConstants.MUMBLE_ALIAS_KEY;
import static mumble.MumbleConstants.MUMBLE_AND_KEY;
import static mumble.MumbleConstants.MUMBLE_ASSIGNMENTS_KEY;
import static mumble.MumbleConstants.MUMBLE_BETWEEN_KEY;
import static mumble.MumbleConstants.MUMBLE_CALCULATION_KEY;
import static mumble.MumbleConstants.MUMBLE_CASE_KEY;
import static mumble.MumbleConstants.MUMBLE_CLAUSES_KEY;
import static mumble.MumbleConstants.MUMBLE_COLUMN_KEY;
import static mumble.MumbleConstants.MUMBLE_CONCATENATE_KEY;
import static mumble.MumbleConstants.MUMBLE_CONDITION_KEY;
import static mumble.MumbleConstants.MUMBLE_CONDITION_TREE_KEY;
import static mumble.MumbleConstants.MUMBLE_DATABASE_NAME_KEY;
import static mumble.MumbleConstants.MUMBLE_ELSE_KEY;
import static mumble.MumbleConstants.MUMBLE_FROM_KEY;
import static mumble.MumbleConstants.MUMBLE_FUNCTION_KEY;
import static mumble.MumbleConstants.MUMBLE_FUNCTION_NAME_KEY;
import static mumble.MumbleConstants.MUMBLE_GROUPBY_KEY;
import static mumble.MumbleConstants.MUMBLE_HAVING_KEY;
import static mumble.MumbleConstants.MUMBLE_INSERT_KEY;
import static mumble.MumbleConstants.MUMBLE_INTERSECT_KEY;
import static mumble.MumbleConstants.MUMBLE_IN_KEY;
import static mumble.MumbleConstants.MUMBLE_IN_LIST_KEY;
import static mumble.MumbleConstants.MUMBLE_ITEM_KEY;
import static mumble.MumbleConstants.MUMBLE_JOIN_EXTENSION_KEY;
import static mumble.MumbleConstants.MUMBLE_JOIN_KEY;
import static mumble.MumbleConstants.MUMBLE_JOIN_ON_KEY;
import static mumble.MumbleConstants.MUMBLE_LEFT_FACTOR_KEY;
import static mumble.MumbleConstants.MUMBLE_LIMIT_KEY;
import static mumble.MumbleConstants.MUMBLE_LIST_KEY;
import static mumble.MumbleConstants.MUMBLE_LITERAL_KEY;
import static mumble.MumbleConstants.MUMBLE_LOOKUP_KEY;
import static mumble.MumbleConstants.MUMBLE_NAME_KEY;
import static mumble.MumbleConstants.MUMBLE_NOT_IN_LIST_KEY;
import static mumble.MumbleConstants.MUMBLE_NOT_KEY;
import static mumble.MumbleConstants.MUMBLE_NULL_LITERAL_KEY;
import static mumble.MumbleConstants.MUMBLE_NULL_ORDER_KEY;
import static mumble.MumbleConstants.MUMBLE_OPERATOR_KEY;
import static mumble.MumbleConstants.MUMBLE_ORDERBY_KEY;
import static mumble.MumbleConstants.MUMBLE_OR_KEY;
import static mumble.MumbleConstants.MUMBLE_OVER_KEY;
import static mumble.MumbleConstants.MUMBLE_PARAMETERS_KEY;
import static mumble.MumbleConstants.MUMBLE_PARENTHESES_KEY;
import static mumble.MumbleConstants.MUMBLE_PARTITION_BY_KEY;
import static mumble.MumbleConstants.MUMBLE_PREDICAND_KEY;
import static mumble.MumbleConstants.MUMBLE_PREDICAND_TREE_KEY;
import static mumble.MumbleConstants.MUMBLE_QUALIFIER_KEY;
import static mumble.MumbleConstants.MUMBLE_QUERY_KEY;
import static mumble.MumbleConstants.MUMBLE_RANGE_BEGIN_KEY;
import static mumble.MumbleConstants.MUMBLE_RANGE_END_KEY;
import static mumble.MumbleConstants.MUMBLE_RETURNING_KEY;
import static mumble.MumbleConstants.MUMBLE_RIGHT_FACTOR_KEY;
import static mumble.MumbleConstants.MUMBLE_SCHEMA_KEY;
import static mumble.MumbleConstants.MUMBLE_SELECT_KEY;
import static mumble.MumbleConstants.MUMBLE_SET_KEY;
import static mumble.MumbleConstants.MUMBLE_SORT_ORDER_KEY;
import static mumble.MumbleConstants.MUMBLE_SQL_TREE_KEY;
import static mumble.MumbleConstants.MUMBLE_SUBSTITUTION_KEY;
import static mumble.MumbleConstants.MUMBLE_SYMMETRY_KEY;
import static mumble.MumbleConstants.MUMBLE_TABLE_KEY;
import static mumble.MumbleConstants.MUMBLE_TABLE_REF_KEY;
import static mumble.MumbleConstants.MUMBLE_THEN_KEY;
import static mumble.MumbleConstants.MUMBLE_TO_KEY;
import static mumble.MumbleConstants.MUMBLE_TRIM_CHARACTER_KEY;
import static mumble.MumbleConstants.MUMBLE_TYPE_KEY;
import static mumble.MumbleConstants.MUMBLE_UNION_KEY;
import static mumble.MumbleConstants.MUMBLE_UPDATE_KEY;
import static mumble.MumbleConstants.MUMBLE_VALUE_KEY;
import static mumble.MumbleConstants.MUMBLE_WHEN_KEY;
import static mumble.MumbleConstants.MUMBLE_WHERE_KEY;
import static mumble.MumbleConstants.MUMBLE_WINDOW_FUNCTION_KEY;
import static mumble.MumbleConstants.MUMBLE_WITH_KEY;
import mumble.Snippet;

public class AbstractASTWalker {

	protected Snippet snip;

	public AbstractASTWalker() {
		super();
	}

	public Snippet getSnip() {
		return snip;
	}

	public void setSnip(Snippet snip) {
		this.snip = snip;
	}

	@SuppressWarnings("unchecked")
	public void traverseSqlTree(HashMap<String, Object> tree, Object inprog) {
	
			Set<String> keys = tree.keySet();
			for (String key : keys) {
	
				HashMap<String, Object> subtree;
				String value;
				
				switch (key) {
	
				case MUMBLE_ALIAS_KEY:
					System.out.println("alias");
					value = (String) tree.get(key);
					handleValue(value, inprog);
					break;
				case MUMBLE_AND_KEY:
					System.out.println("and");
					subtree = (HashMap<String, Object>) tree.get(key);
					handleListOfSubTree(subtree, inprog);
					break;
				case MUMBLE_ASSIGNMENTS_KEY:
					System.out.println("assignments");
					subtree = (HashMap<String, Object>) tree.get(key);
					handleListOfSubTree(subtree, inprog);
					break;
				case MUMBLE_BETWEEN_KEY:
					System.out.println("between");
					subtree = (HashMap<String, Object>) tree.get(key);
					handleSubTree(subtree, inprog);
					break;
				case MUMBLE_CALCULATION_KEY:
					System.out.println("calc");
					subtree = (HashMap<String, Object>) tree.get(key);
					handleSubTree(subtree, inprog);
					break;
				case MUMBLE_CASE_KEY:
					System.out.println("case");
					subtree = (HashMap<String, Object>) tree.get(key);
					handleSubTree(subtree, inprog);
					break;
				case MUMBLE_CLAUSES_KEY:
					System.out.println("clauses");
					subtree = (HashMap<String, Object>) tree.get(key);
					handleListOfSubTree(subtree, inprog);
					break;
				case MUMBLE_COLUMN_KEY:
					System.out.println("column");
					if (tree.get(key) instanceof String) {
						value = (String) tree.get(key);
						handleValue(value, inprog);
					} else{
						subtree = (HashMap<String, Object>) tree.get(key);
						handleSubTree(subtree, inprog);
					}
					break;
				case MUMBLE_CONCATENATE_KEY:
					System.out.println("concatenate");
					subtree = (HashMap<String, Object>) tree.get(key);
					handleListOfSubTree(subtree, inprog);
					break;
				case MUMBLE_CONDITION_KEY:
					System.out.println("condition");
					subtree = (HashMap<String, Object>) tree.get(key);
					handleSubTree(subtree, inprog);
					break;
				case MUMBLE_CONDITION_TREE_KEY:
					System.out.println("CONDITION");
					subtree = (HashMap<String, Object>) tree.get(key);
					handleSubTree(subtree, inprog);
					break;
				case MUMBLE_DATABASE_NAME_KEY:
					System.out.println("dbname");
					value = (String) tree.get(key);
					handleValue(value, inprog);
					break;
				case MUMBLE_ELSE_KEY:
					System.out.println("else");
					break;
				case MUMBLE_FROM_KEY:
					System.out.println("from");
					subtree = (HashMap<String, Object>) tree.get(key);
					handleSubTree(subtree, inprog);
					break;
				case MUMBLE_FUNCTION_KEY:
					System.out.println("function");
					subtree = (HashMap<String, Object>) tree.get(key);
					handleSubTree(subtree, inprog);
					break;
				case MUMBLE_FUNCTION_NAME_KEY:
					System.out.println("function_name");
					value = (String) tree.get(key);
					handleValue(value, inprog);
					break;
				case MUMBLE_GROUPBY_KEY:
					System.out.println("groupby");
					subtree = (HashMap<String, Object>) tree.get(key);
					handleListOfSubTree(subtree, inprog);
					break;
				case MUMBLE_HAVING_KEY:
					System.out.println("having");
					subtree = (HashMap<String, Object>) tree.get(key);
					handleSubTree(subtree, inprog);
					break;
				case MUMBLE_IN_KEY:
					System.out.println("in");
					subtree = (HashMap<String, Object>) tree.get(key);
					handleSubTree(subtree, inprog);
					break;
				case MUMBLE_IN_LIST_KEY:
					System.out.println("in_list");
					break;
				case MUMBLE_INSERT_KEY:
					System.out.println("insert");
					subtree = (HashMap<String, Object>) tree.get(key);
					handleSubTree(subtree, inprog);
					break;
				case MUMBLE_INTERSECT_KEY:
					System.out.println("intersect");
					subtree = (HashMap<String, Object>) tree.get(key);
					handleListOfSubTree(subtree, inprog);
					break;
				case MUMBLE_ITEM_KEY:
					System.out.println("item");
					break;
				case MUMBLE_JOIN_EXTENSION_KEY:
					System.out.println("extension");
					break;
				case MUMBLE_JOIN_KEY:
					System.out.println("join");
					if (tree.get(key) instanceof String) {
						value = (String) tree.get(key);
						handleValue(value, inprog);
					} else{
						subtree = (HashMap<String, Object>) tree.get(key);
						handleListOfSubTree(subtree, inprog);
	//					handleSubTree(subtree, inprog);
					}
					break;
				case MUMBLE_JOIN_ON_KEY:
					System.out.println("on");
					subtree = (HashMap<String, Object>) tree.get(key);
					handleSubTree(subtree, inprog);
					break;
				case MUMBLE_LEFT_FACTOR_KEY:
					System.out.println("left");
					subtree = (HashMap<String, Object>) tree.get(key);
					handleSubTree(subtree, inprog);
					break;
				case MUMBLE_LIMIT_KEY:
					System.out.println("limit");
					break;
				case MUMBLE_LIST_KEY:
					System.out.println("list");
					subtree = (HashMap<String, Object>) tree.get(key);
					handleListOfSubTree(subtree, inprog);
					break;
				case MUMBLE_LITERAL_KEY:
					System.out.println("literal");
					value = (String) tree.get(key);
					handleValue(value, inprog);
					break;
				case MUMBLE_LOOKUP_KEY:
					System.out.println("lookup");
					subtree = (HashMap<String, Object>) tree.get(key);
					handleSubTree(subtree, inprog);
					break;
				case MUMBLE_NAME_KEY:
					System.out.println("name");
					value = (String) tree.get(key);
					handleValue(value, inprog);
					break;
				case MUMBLE_NOT_IN_LIST_KEY:
					System.out.println("not_in_list");
					subtree = (HashMap<String, Object>) tree.get(key);
					handleSubTree(subtree, inprog);
					break;
				case MUMBLE_NOT_KEY:
					System.out.println("not");
					subtree = (HashMap<String, Object>) tree.get(key);
					handleSubTree(subtree, inprog);
					break;
				case MUMBLE_NULL_LITERAL_KEY:
					System.out.println("null_literal");
					value = (String) tree.get(key);
					handleValue(value, inprog);
					break;
				case MUMBLE_NULL_ORDER_KEY:
					System.out.println("null_order");
					value = (String) tree.get(key);
					handleValue(value, inprog);
					break;
				case MUMBLE_OPERATOR_KEY:
					System.out.println("operator");
					value = (String) tree.get(key);
					handleValue(value, inprog);
					break;
				case MUMBLE_OR_KEY:
					System.out.println("or");
					subtree = (HashMap<String, Object>) tree.get(key);
					handleListOfSubTree(subtree, inprog);
					break;
				case MUMBLE_ORDERBY_KEY:
					System.out.println("orderby");
					subtree = (HashMap<String, Object>) tree.get(key);
					handleListOfSubTree(subtree, inprog);
					break;
				case MUMBLE_OVER_KEY:
					System.out.println("over");
					subtree = (HashMap<String, Object>) tree.get(key);
					handleSubTree(subtree, inprog);
					break;
				case MUMBLE_PARAMETERS_KEY:
					System.out.println("parameters");
					subtree = (HashMap<String, Object>) tree.get(key);
					handleListOfSubTree(subtree, inprog);
					break;
				case MUMBLE_PARENTHESES_KEY:
					System.out.println("parentheses");
					subtree = (HashMap<String, Object>) tree.get(key);
					handleSubTree(subtree, inprog);
					break;
				case MUMBLE_PARTITION_BY_KEY:
					System.out.println("partition_by");
					subtree = (HashMap<String, Object>) tree.get(key);
					handleListOfSubTree(subtree, inprog);
					break;
				case MUMBLE_PREDICAND_KEY:
					System.out.println("predicand");
					subtree = (HashMap<String, Object>) tree.get(key);
					handleSubTree(subtree, inprog);
					break;
				case MUMBLE_PREDICAND_TREE_KEY:
					System.out.println("PREDICAND");
					subtree = (HashMap<String, Object>) tree.get(key);
					handleSubTree(subtree, inprog);
					break;
				case MUMBLE_QUALIFIER_KEY:
					System.out.println("qualifier");
					value = (String) tree.get(key);
					handleValue(value, inprog);
					break;
				case MUMBLE_QUERY_KEY:
					System.out.println("query");
					subtree = (HashMap<String, Object>) tree.get(key);
					handleSubTree(subtree, inprog);
					break;
				case MUMBLE_RANGE_BEGIN_KEY:
					System.out.println("begin");
					value = (String) tree.get(key);
					handleValue(value, inprog);
					break;
				case MUMBLE_RANGE_END_KEY:
					System.out.println("end");
					value = (String) tree.get(key);
					handleValue(value, inprog);
					break;
				case MUMBLE_RETURNING_KEY:
					System.out.println("returning");
					value = (String) tree.get(key);
					handleValue(value, inprog);
					break;
				case MUMBLE_RIGHT_FACTOR_KEY:
					System.out.println("right");
					subtree = (HashMap<String, Object>) tree.get(key);
					handleSubTree(subtree, inprog);
					break;
				case MUMBLE_SCHEMA_KEY:
					System.out.println("schema");
					value = (String) tree.get(key);
					handleValue(value, inprog);
					break;
				case MUMBLE_SELECT_KEY:
					System.out.println("select");
					subtree = (HashMap<String, Object>) tree.get(key);
					handleListOfSubTree(subtree, inprog);
					break;
				case MUMBLE_SET_KEY:
					System.out.println("set");
					break;
				case MUMBLE_SORT_ORDER_KEY:
					System.out.println("sort_order");
					value = (String) tree.get(key);
					handleValue(value, inprog);
					break;
				case MUMBLE_SQL_TREE_KEY:
					System.out.println("SQL");
					subtree = (HashMap<String, Object>) tree.get(key);
					handleSubTree(subtree, inprog);
					break;
				case MUMBLE_SUBSTITUTION_KEY:
					System.out.println("substitution");
					subtree = (HashMap<String, Object>) tree.get(key);
					handleSubTree(subtree, inprog);
					break;
				case MUMBLE_SYMMETRY_KEY:
					System.out.println("symmetry");
					value = (String) tree.get(key);
					handleValue(value, inprog);
					break;
				case MUMBLE_TABLE_KEY:
					System.out.println("table");
					if (tree.get(key) instanceof String) {
						value = (String) tree.get(key);
						handleValue(value, inprog);
					} else{
						subtree = (HashMap<String, Object>) tree.get(key);
						handleSubTree(subtree, inprog);
					}
					break;
				case MUMBLE_TABLE_REF_KEY:
					System.out.println("table_ref");
					value = (String) tree.get(key);
					handleValue(value, inprog);
					break;
				case MUMBLE_THEN_KEY:
					System.out.println("then");
					subtree = (HashMap<String, Object>) tree.get(key);
					handleSubTree(subtree, inprog);
					break;
				case MUMBLE_TO_KEY:
					System.out.println("to");
					break;
				case MUMBLE_TRIM_CHARACTER_KEY:
					System.out.println("trim_character");
					value = (String) tree.get(key);
					handleValue(value, inprog);
					break;
				case MUMBLE_TYPE_KEY:
					System.out.println("type");
					value = (String) tree.get(key);
					handleValue(value, inprog);
					break;
				case MUMBLE_UNION_KEY:
					System.out.println("union");
					subtree = (HashMap<String, Object>) tree.get(key);
					handleListOfSubTree(subtree, inprog);
					break;
				case MUMBLE_UPDATE_KEY:
					System.out.println("update");
					subtree = (HashMap<String, Object>) tree.get(key);
					handleSubTree(subtree, inprog);
					break;
				case MUMBLE_VALUE_KEY:
					System.out.println("value");
					value = (String) tree.get(key);
					handleValue(value, inprog);
					break;
				case MUMBLE_WHEN_KEY:
					System.out.println("when");
					subtree = (HashMap<String, Object>) tree.get(key);
					handleSubTree(subtree, inprog);
					break;
				case MUMBLE_WHERE_KEY:
					System.out.println("where");
					subtree = (HashMap<String, Object>) tree.get(key);
					handleSubTree(subtree, inprog);
					break;
				case MUMBLE_WINDOW_FUNCTION_KEY:
					System.out.println("window_function");
					subtree = (HashMap<String, Object>) tree.get(key);
					handleSubTree(subtree, inprog);
					break;
				case MUMBLE_WITH_KEY:
					System.out.println("with");
					subtree = (HashMap<String, Object>) tree.get(key);
					handleSubTree(subtree, inprog);
					break;
				default:
					System.out.println("DEFAULT:" + key + " - " + tree.get(key));
					if (tree.get(key) instanceof String) {
						value = (String) tree.get(key);
						handleValue(value, inprog);
					} else{
						subtree = (HashMap<String, Object>) tree.get(key);
						handleSubTree(subtree, inprog);
					}
					break;
	
				}
			}
		}

	/**
	 * @param value
	 */
	private void handleValue(String value, Object inprog) {
		System.out.println(value);
	}

	private void handleSubTree(HashMap<String, Object> subtree, Object inprog) {
		traverseSqlTree(subtree, inprog);
		
	}

	private void handleListOfSubTree(HashMap<String, Object> list, Object inprog) {
		Set<String> keys = list.keySet();
		int count = keys.size();
		
		for (Integer i = 1; i < count+1; i++) {
			String key = i.toString();
			if (list.get(key) instanceof String) {
				String value = (String) list.get(key);
				handleValue(value, inprog);
			} else{
				HashMap<String, Object> subtree = (HashMap<String, Object>) list.get(key);
				handleSubTree(subtree, inprog);
			}
		}
		
	}

}