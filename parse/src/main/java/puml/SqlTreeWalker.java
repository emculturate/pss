package puml;

import java.util.HashMap;
import java.util.Map;

public class SqlTreeWalker {

	/**
	 * SQL Tree holds a nested Map data structure representing an entire SQL
	 * statement. This class will walk through this Map in a depth first
	 * approach and call a standard interface of the handler object at each
	 * level. Depending on the handler inserted, different actions can be
	 * applied to the tree
	 */
	private HashMap<String, Object> sqlTree;

	/**
	 * the abstract tree handler uses an interface based on different levels of
	 * the sql statement and returns a map with partial results from each level
	 */
	private AbstractSqlTreeHandler handler;

	// Getters and Setters

	public HashMap<String, Object> getSqlTree() {
		return sqlTree;
	}

	public void setSqlTree(HashMap<String, Object> sqlTree) {
		this.sqlTree = sqlTree;
	}

	public AbstractSqlTreeHandler getHandler() {
		return handler;
	}

	public void setHandler(AbstractSqlTreeHandler handler) {
		this.handler = handler;
	}

	// Main Operations

	public void startWalking() {
		walk(sqlTree);
	}

	public HashMap<String, Object> walk(Map<String, Object> nodeList) {
		String[] keys = new String[1];
		keys = nodeList.keySet().toArray(keys);
		HashMap<String, Object> resTree = new HashMap<String, Object>();

		for (String key : keys) {
			Object item = nodeList.get(key);
			if (item instanceof Map<?, ?>) {
				Map<String, Object> map = (Map<String, Object>) item;
				Map<String, Object> partial = walk(map);
				resTree.put(key, handler.handle(key, map, partial));
			} else // if (item )
				resTree.put(key, item);

		}
		return resTree;
	}
}
