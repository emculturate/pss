package mumble.sql.template;

import static mumble.sql.MumbleConstants.*;

import java.util.HashMap;
import java.util.Set;

import mumble.sql.Snippet;

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

	public void traverseSqlTree(HashMap<String, Object> tree, Object inprog) {
	
			Set<String> keys = tree.keySet();
			for (String key : keys) {
	
				HashMap<String, Object> subtree;
				String value;
				
				switch (key) {
	
				case PSS_ALIAS_KEY:
					System.out.println("alias");
					value = (String) tree.get(key);
					handleValue(value, inprog);
					break;
				case PSS_AND_KEY:
					System.out.println("and");
					subtree = (HashMap<String, Object>) tree.get(key);
					handleListOfSubTree(subtree, inprog);
					break;
				case PSS_ASSIGNMENTS_KEY:
					System.out.println("assignments");
					subtree = (HashMap<String, Object>) tree.get(key);
					handleListOfSubTree(subtree, inprog);
					break;
				case PSS_BETWEEN_KEY:
					System.out.println("between");
					subtree = (HashMap<String, Object>) tree.get(key);
					handleSubTree(subtree, inprog);
					break;
				case PSS_CALCULATION_KEY:
					System.out.println("calc");
					subtree = (HashMap<String, Object>) tree.get(key);
					handleSubTree(subtree, inprog);
					break;
				case PSS_CASE_KEY:
					System.out.println("case");
					subtree = (HashMap<String, Object>) tree.get(key);
					handleSubTree(subtree, inprog);
					break;
				case PSS_CLAUSES_KEY:
					System.out.println("clauses");
					subtree = (HashMap<String, Object>) tree.get(key);
					handleListOfSubTree(subtree, inprog);
					break;
				case PSS_COLUMN_KEY:
					System.out.println("column");
					if (tree.get(key) instanceof String) {
						value = (String) tree.get(key);
						handleValue(value, inprog);
					} else{
						subtree = (HashMap<String, Object>) tree.get(key);
						handleSubTree(subtree, inprog);
					}
					break;
				case PSS_CONCATENATE_KEY:
					System.out.println("concatenate");
					subtree = (HashMap<String, Object>) tree.get(key);
					handleListOfSubTree(subtree, inprog);
					break;
				case PSS_CONDITION_KEY:
					System.out.println("condition");
					subtree = (HashMap<String, Object>) tree.get(key);
					handleSubTree(subtree, inprog);
					break;
				case PSS_CONDITION_TREE_KEY:
					System.out.println("CONDITION");
					subtree = (HashMap<String, Object>) tree.get(key);
					handleSubTree(subtree, inprog);
					break;
				case PSS_DATABASE_NAME_KEY:
					System.out.println("dbname");
					value = (String) tree.get(key);
					handleValue(value, inprog);
					break;
				case PSS_ELSE_KEY:
					System.out.println("else");
					break;
				case PSS_FROM_KEY:
					System.out.println("from");
					subtree = (HashMap<String, Object>) tree.get(key);
					handleSubTree(subtree, inprog);
					break;
				case PSS_FUNCTION_KEY:
					System.out.println("function");
					subtree = (HashMap<String, Object>) tree.get(key);
					handleSubTree(subtree, inprog);
					break;
				case PSS_FUNCTION_NAME_KEY:
					System.out.println("function_name");
					value = (String) tree.get(key);
					handleValue(value, inprog);
					break;
				case PSS_GROUPBY_KEY:
					System.out.println("groupby");
					subtree = (HashMap<String, Object>) tree.get(key);
					handleListOfSubTree(subtree, inprog);
					break;
				case PSS_HAVING_KEY:
					System.out.println("having");
					subtree = (HashMap<String, Object>) tree.get(key);
					handleSubTree(subtree, inprog);
					break;
				case PSS_IN_KEY:
					System.out.println("in");
					subtree = (HashMap<String, Object>) tree.get(key);
					handleSubTree(subtree, inprog);
					break;
				case PSS_IN_LIST_KEY:
					System.out.println("in_list");
					break;
				case PSS_INSERT_KEY:
					System.out.println("insert");
					subtree = (HashMap<String, Object>) tree.get(key);
					handleSubTree(subtree, inprog);
					break;
				case PSS_INTERSECT_KEY:
					System.out.println("intersect");
					subtree = (HashMap<String, Object>) tree.get(key);
					handleListOfSubTree(subtree, inprog);
					break;
				case PSS_ITEM_KEY:
					System.out.println("item");
					break;
				case PSS_JOIN_EXTENSION_KEY:
					System.out.println("extension");
					break;
				case PSS_JOIN_KEY:
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
				case PSS_JOIN_ON_KEY:
					System.out.println("on");
					subtree = (HashMap<String, Object>) tree.get(key);
					handleSubTree(subtree, inprog);
					break;
				case PSS_LEFT_FACTOR_KEY:
					System.out.println("left");
					subtree = (HashMap<String, Object>) tree.get(key);
					handleSubTree(subtree, inprog);
					break;
				case PSS_LIMIT_KEY:
					System.out.println("limit");
					break;
				case PSS_LIST_KEY:
					System.out.println("list");
					subtree = (HashMap<String, Object>) tree.get(key);
					handleListOfSubTree(subtree, inprog);
					break;
				case PSS_LITERAL_KEY:
					System.out.println("literal");
					value = (String) tree.get(key);
					handleValue(value, inprog);
					break;
				case PSS_LOOKUP_KEY:
					System.out.println("lookup");
					subtree = (HashMap<String, Object>) tree.get(key);
					handleSubTree(subtree, inprog);
					break;
				case PSS_NAME_KEY:
					System.out.println("name");
					value = (String) tree.get(key);
					handleValue(value, inprog);
					break;
				case PSS_NOT_IN_LIST_KEY:
					System.out.println("not_in_list");
					subtree = (HashMap<String, Object>) tree.get(key);
					handleSubTree(subtree, inprog);
					break;
				case PSS_NOT_KEY:
					System.out.println("not");
					subtree = (HashMap<String, Object>) tree.get(key);
					handleSubTree(subtree, inprog);
					break;
				case PSS_NULL_LITERAL_KEY:
					System.out.println("null_literal");
					value = (String) tree.get(key);
					handleValue(value, inprog);
					break;
				case PSS_NULL_ORDER_KEY:
					System.out.println("null_order");
					value = (String) tree.get(key);
					handleValue(value, inprog);
					break;
				case PSS_OPERATOR_KEY:
					System.out.println("operator");
					value = (String) tree.get(key);
					handleValue(value, inprog);
					break;
				case PSS_OR_KEY:
					System.out.println("or");
					subtree = (HashMap<String, Object>) tree.get(key);
					handleListOfSubTree(subtree, inprog);
					break;
				case PSS_ORDERBY_KEY:
					System.out.println("orderby");
					subtree = (HashMap<String, Object>) tree.get(key);
					handleListOfSubTree(subtree, inprog);
					break;
				case PSS_OVER_KEY:
					System.out.println("over");
					subtree = (HashMap<String, Object>) tree.get(key);
					handleSubTree(subtree, inprog);
					break;
				case PSS_PARAMETERS_KEY:
					System.out.println("parameters");
					subtree = (HashMap<String, Object>) tree.get(key);
					handleListOfSubTree(subtree, inprog);
					break;
				case PSS_PARENTHESES_KEY:
					System.out.println("parentheses");
					subtree = (HashMap<String, Object>) tree.get(key);
					handleSubTree(subtree, inprog);
					break;
				case PSS_PARTITION_BY_KEY:
					System.out.println("partition_by");
					subtree = (HashMap<String, Object>) tree.get(key);
					handleListOfSubTree(subtree, inprog);
					break;
				case PSS_PREDICAND_KEY:
					System.out.println("predicand");
					subtree = (HashMap<String, Object>) tree.get(key);
					handleSubTree(subtree, inprog);
					break;
				case PSS_PREDICAND_TREE_KEY:
					System.out.println("PREDICAND");
					subtree = (HashMap<String, Object>) tree.get(key);
					handleSubTree(subtree, inprog);
					break;
				case PSS_QUALIFIER_KEY:
					System.out.println("qualifier");
					value = (String) tree.get(key);
					handleValue(value, inprog);
					break;
				case PSS_QUERY_KEY:
					System.out.println("query");
					subtree = (HashMap<String, Object>) tree.get(key);
					handleSubTree(subtree, inprog);
					break;
				case PSS_RANGE_BEGIN_KEY:
					System.out.println("begin");
					value = (String) tree.get(key);
					handleValue(value, inprog);
					break;
				case PSS_RANGE_END_KEY:
					System.out.println("end");
					value = (String) tree.get(key);
					handleValue(value, inprog);
					break;
				case PSS_RETURNING_KEY:
					System.out.println("returning");
					value = (String) tree.get(key);
					handleValue(value, inprog);
					break;
				case PSS_RIGHT_FACTOR_KEY:
					System.out.println("right");
					subtree = (HashMap<String, Object>) tree.get(key);
					handleSubTree(subtree, inprog);
					break;
				case PSS_SCHEMA_KEY:
					System.out.println("schema");
					value = (String) tree.get(key);
					handleValue(value, inprog);
					break;
				case PSS_SELECT_KEY:
					System.out.println("select");
					subtree = (HashMap<String, Object>) tree.get(key);
					handleListOfSubTree(subtree, inprog);
					break;
				case PSS_SET_KEY:
					System.out.println("set");
					break;
				case PSS_SORT_ORDER_KEY:
					System.out.println("sort_order");
					value = (String) tree.get(key);
					handleValue(value, inprog);
					break;
				case PSS_SQL_TREE_KEY:
					System.out.println("SQL");
					subtree = (HashMap<String, Object>) tree.get(key);
					handleSubTree(subtree, inprog);
					break;
				case PSS_SUBSTITUTION_KEY:
					System.out.println("substitution");
					subtree = (HashMap<String, Object>) tree.get(key);
					handleSubTree(subtree, inprog);
					break;
				case PSS_SYMMETRY_KEY:
					System.out.println("symmetry");
					value = (String) tree.get(key);
					handleValue(value, inprog);
					break;
				case PSS_TABLE_KEY:
					System.out.println("table");
					if (tree.get(key) instanceof String) {
						value = (String) tree.get(key);
						handleValue(value, inprog);
					} else{
						subtree = (HashMap<String, Object>) tree.get(key);
						handleSubTree(subtree, inprog);
					}
					break;
				case PSS_TABLE_REF_KEY:
					System.out.println("table_ref");
					value = (String) tree.get(key);
					handleValue(value, inprog);
					break;
				case PSS_THEN_KEY:
					System.out.println("then");
					subtree = (HashMap<String, Object>) tree.get(key);
					handleSubTree(subtree, inprog);
					break;
				case PSS_TO_KEY:
					System.out.println("to");
					break;
				case PSS_TRIM_CHARACTER_KEY:
					System.out.println("trim_character");
					value = (String) tree.get(key);
					handleValue(value, inprog);
					break;
				case PSS_TYPE_KEY:
					System.out.println("type");
					value = (String) tree.get(key);
					handleValue(value, inprog);
					break;
				case PSS_UNION_KEY:
					System.out.println("union");
					subtree = (HashMap<String, Object>) tree.get(key);
					handleListOfSubTree(subtree, inprog);
					break;
				case PSS_UPDATE_KEY:
					System.out.println("update");
					subtree = (HashMap<String, Object>) tree.get(key);
					handleSubTree(subtree, inprog);
					break;
				case PSS_VALUE_KEY:
					System.out.println("value");
					value = (String) tree.get(key);
					handleValue(value, inprog);
					break;
				case PSS_WHEN_KEY:
					System.out.println("when");
					subtree = (HashMap<String, Object>) tree.get(key);
					handleSubTree(subtree, inprog);
					break;
				case PSS_WHERE_KEY:
					System.out.println("where");
					subtree = (HashMap<String, Object>) tree.get(key);
					handleSubTree(subtree, inprog);
					break;
				case PSS_WINDOW_FUNCTION_KEY:
					System.out.println("window_function");
					subtree = (HashMap<String, Object>) tree.get(key);
					handleSubTree(subtree, inprog);
					break;
				case PSS_WITH_KEY:
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