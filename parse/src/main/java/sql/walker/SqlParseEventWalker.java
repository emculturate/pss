package sql.walker;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.antlr.v4.runtime.tree.TerminalNodeImpl;

import sql.SQLSelectParserParser;
import sql.SQLSelectParserBaseListener;

public class SqlParseEventWalker extends SQLSelectParserBaseListener {

	final static Boolean showParse = false;
	final static Boolean showSymbols = true;
	final static Boolean showOther = false;
	final static Boolean showResults = true;

	final static Integer parseTrace = 1;
	final static Integer symbolTrace = 2;
	final static Integer otherTrace = 3;
	final static Integer resultTrace = 4;

	/**
	 * Collect components of parse tree
	 */
	HashMap<String, Object> collector = new HashMap<String, Object>();

	/**
	 * Collect Alias to Table/Query Map
	 */
	HashMap<String, Object> aliasTableMap = new HashMap<String, Object>();

	/**
	 * Collect Table Names
	 */
	HashMap<String, Object> tableColumnList = new HashMap<String, Object>();

	/**
	 * Depth of token stack
	 */
	HashMap<Integer, Integer> stackTree = new HashMap<Integer, Integer>();

	/**
	 * Depth of token stack
	 */
	HashMap<String, Integer> stackSymbols = new HashMap<String, Integer>();

	/**
	 * Number of query and subqueries encountered
	 */
	Integer queryCount = 0;
	Boolean unionClauseFound = false;
	Boolean firstUnionClause = false;
	Boolean intersectClauseFound = false;
	Boolean firstIntersectClause = false;

	// Extra-Grammar Identifiers

	// Constructors
	public SqlParseEventWalker() {
		super();
	}

	// Getters and Setters

	public HashMap<String, Object> getCollector() {
		return collector;
	}

	public void setCollector(HashMap<String, Object> collector) {
		this.collector = collector;
	}

	// Other Methods

	/**
	 * @param trace
	 */
	private void showTrace(Integer traceType, Object trace) {
		if (traceType == parseTrace && showParse)
			System.out.println(trace);
		if (traceType == symbolTrace && showSymbols)
			System.out.println(trace);
		if (traceType == resultTrace && showResults)
			System.out.println(trace);
		if (traceType == otherTrace && showOther)
			System.out.println(trace);
	}

	private Integer pushStack(Integer ruleIndex) {
		Integer context = stackTree.get(ruleIndex);
		Integer newLevel;
		if (context == null) {
			newLevel = 1;
		} else {
			newLevel = context + 1;
		}
		stackTree.put(ruleIndex, newLevel);
		showTrace(otherTrace, "PUSH - " + makeMapIndex(ruleIndex, newLevel) + ": " + stackTree);
		return newLevel;
	}

	private Integer popStack(Integer ruleIndex) {
		Integer level = stackTree.get(ruleIndex) - 1;
		if (level == 0) {
			stackTree.remove(ruleIndex);
		}
		stackTree.put(ruleIndex, level);
		showTrace(otherTrace, "POP - " + makeMapIndex(ruleIndex, level) + ": " + stackTree);
		return level;
	}

	private Integer currentStackLevel(int ruleIndex) {
		return stackTree.get(ruleIndex);
	}

	private Integer pushStack(String key, Object symbols) {
		Integer level = stackSymbols.get(key);
		Integer newLevel;
		if (level == null) {
			newLevel = 1;
		} else {
			newLevel = level + 1;
		}
		stackSymbols.put(key, newLevel);
		String symbolKey = key + "_" + newLevel;
		collect(symbolKey, symbols);
		showTrace(symbolTrace, "PUSH - " + symbolKey + ": " + symbols);
		return newLevel;
	}

	private Object popStack(String key) {
		Integer level = stackSymbols.get(key);
		String symbolKey = key + "_" + level;
		if (level == 1)
			stackSymbols.remove(key);
		else
			stackSymbols.put(key, level - 1);
		showTrace(symbolTrace, "POP - " + symbolKey + ": " + stackSymbols);
		return collector.remove(symbolKey);
	}

	/**
	 * 
	 */
	private void pushSymbolTable() {
		Object symbols = tableColumnList;
		if (symbols != null) {
			pushStack("tableColumnList", symbols);
		}
		tableColumnList = new HashMap<String, Object>();
	}

	/**
	 * @param key
	 * @param symbols
	 */
	@SuppressWarnings("unchecked")
	private void popSymbolTable(String key, HashMap<String, Object> symbols) {
		tableColumnList = (HashMap<String, Object>) popStack("tableColumnList");
		tableColumnList.put(key, symbols);
	}

	private void popSymbolTablePutAll(HashMap<String, Object> symbols) {
		tableColumnList = (HashMap<String, Object>) popStack("tableColumnList");
		tableColumnList.putAll(symbols);
	}

	private Integer currentStackLevel(String key) {
		return stackSymbols.get(key);
	}

	/**
	 * Add level map to collection by ruleIndex and stackLevel
	 * 
	 * @param ruleIndex
	 * @param stackLevel
	 * @param hashMap
	 * @return
	 */
	private Object collect(int ruleIndex, Integer stackLevel, Object item) {
		String index = makeMapIndex(ruleIndex, stackLevel);
		collect(index, item);
		if (item instanceof Map<?, ?>)
			return getNodeMap(ruleIndex, stackLevel);
		else
			return getNode(ruleIndex, stackLevel);
	}

	/**
	 * @param index
	 * @param item
	 */
	private void collect(String index, Object item) {
		collector.put(index, item);
	}

	private Object getNode(int ruleIndex, Integer stackLevel) {
		String mapIdx = makeMapIndex(ruleIndex, stackLevel);
		return collector.get(mapIdx);
	}

	private Object removeNode(int ruleIndex, Integer stackLevel) {
		String mapIdx = makeMapIndex(ruleIndex, stackLevel);
		return collector.remove(mapIdx);
	}

	@SuppressWarnings("unchecked")
	private Map<String, Object> getNodeMap(int ruleIndex, Integer stackLevel) {
		String mapIdx = makeMapIndex(ruleIndex, stackLevel);
		Map<String, Object> idMap = (Map<String, Object>) collector.get(mapIdx);
		return idMap;
	}

	@SuppressWarnings("unchecked")
	private Map<String, Object> removeNodeMap(int ruleIndex, Integer stackLevel) {
		String mapIdx = makeMapIndex(ruleIndex, stackLevel);
		return (Map<String, Object>) collector.remove(mapIdx);
	}

	private String makeMapIndex(int ruleIndex, Integer stackIndex) {
		return ruleIndex + "_" + stackIndex;
	}

	// Standard Actions: Symbol Tables

	/**
	 * @param tableReference
	 * @param tableReference
	 */
	@SuppressWarnings("unchecked")
	private void collectTable(String alias, Object tableReference) {
		if (tableReference instanceof String) {
			Object aliasSet = tableColumnList.get((String) alias);
			HashMap<String, Object> ref = (HashMap<String, Object>) tableColumnList.get((String) tableReference);
			if (aliasSet == null) {
				if (!alias.equals((String) tableReference))
					tableColumnList.put(alias, (String) tableReference);
				if (ref == null)
					tableColumnList.put((String) tableReference, new HashMap<String, Object>());
			} else {
				if (!alias.equals((String) tableReference))
					tableColumnList.put(alias, (String) tableReference);
				if (ref == null)
					tableColumnList.put((String) tableReference, aliasSet);
				else
					ref.putAll((Map<String, Object>) aliasSet);
			}
		} else if (tableReference instanceof HashMap<?, ?>) {
			showTrace(symbolTrace, "Error collecting table: " + tableReference);
		}
	}

	/**
	 * @param tableReference
	 */
	@SuppressWarnings("unchecked")
	private void collectTableItem(Object tableReference, Object item) {
		if (tableReference instanceof String) {
			Object symbols = tableColumnList.get((String) tableReference);
			if (symbols == null) {
				symbols = new HashMap<String, Object>();
				if (item instanceof String)
					((HashMap<String, Object>) symbols).put((String) item, item);
				else
					((HashMap<String, Object>) symbols).put("subquery", item);
				tableColumnList.put((String) tableReference, symbols);
			} else if (symbols instanceof String) {
				// Refernce is an ALIAS to a different table
				symbols = tableColumnList.get((String) symbols);
				if (item instanceof String)
					((HashMap<String, Object>) symbols).put((String) item, item);
				else
					((HashMap<String, Object>) symbols).put("subquery", item);
			} else if (symbols instanceof HashMap<?, ?>) {
				if (item instanceof String)
					((HashMap<String, Object>) symbols).put((String) item, item);
				else
					((HashMap<String, Object>) symbols).put("subquery", item);
				// tableColumnList.put((String) tableReference, symbols);
			}
		} else if (tableReference instanceof HashMap<?, ?>) {
			showTrace(symbolTrace, "Error collecting table: " + tableReference);
		}
	}

	// Standard Actions: SQL

	/**
	 * Pops single child entry up one level
	 * 
	 * @param ruleIndex
	 */
	private void handleOneChild(int ruleIndex) {
		Integer stackLevel = currentStackLevel(ruleIndex);
		Map<String, Object> subMap = removeNodeMap(ruleIndex, stackLevel);
		subMap.remove("Type");
		String[] keys = new String[1];
		keys = subMap.keySet().toArray(keys);

		if (keys.length == 1) {
			showTrace(parseTrace, "Just One Entry: " + subMap);
			Object item = subMap.remove(keys[0]);
			collect(ruleIndex, stackLevel, item);
		} else {
			showTrace(parseTrace, "Too many entries: " + subMap);
		}
	}

	/**
	 * If the current node is a list, pull it up to its parent
	 * 
	 * @param ruleIndex
	 */
	private void handleListList(int ruleIndex, int parentRuleIndex) {
		Integer stackLevel = currentStackLevel(ruleIndex);
		Map<String, Object> item = removeNodeMap(ruleIndex, stackLevel);
		item.remove("Type");

		Integer parentStackLevel = currentStackLevel(parentRuleIndex);

		Map<String, Object> subMap = getNodeMap(parentRuleIndex, parentStackLevel);
		subMap.putAll(item);
		collector.put("SKIP", "TRUE");
	}

	/**
	 * If the parent of the current node is a list, use this to put the child
	 * into the list
	 * 
	 * @param ruleIndex
	 * @param ctx
	 */
	private void handleListItem(int ruleIndex, int parentRuleIndex) {
		Integer stackLevel = currentStackLevel(ruleIndex);
		Map<String, Object> subMap = removeNodeMap(ruleIndex, stackLevel);
		subMap.remove("Type");
		String[] keys = new String[1];
		keys = subMap.keySet().toArray(keys);

		if (keys.length == 1) {
			showTrace(parseTrace, "Just One Entry: " + subMap);
			Object item = subMap.remove(keys[0]);

			Integer parentStackLevel = currentStackLevel(parentRuleIndex);

			subMap = getNodeMap(parentRuleIndex, parentStackLevel);
			Integer indx = subMap.size();
			subMap.put(indx.toString(), item);
			collector.put("SKIP", "TRUE");

		} else {
			showTrace(parseTrace, "Too many entries: " + subMap);
		}
		showTrace(parseTrace, "handleListItem: " + subMap);
	}

	/**
	 * Construct a list that has a repeating operator
	 * 
	 * @param ruleIndex
	 * @param operand
	 */
	private void handleOperandList(int ruleIndex, String operand) {
		Integer stackLevel = currentStackLevel(ruleIndex);
		Map<String, Object> subMap = removeNodeMap(ruleIndex, stackLevel);
		Object type = subMap.remove("Type");

		if (subMap.size() == 1) {
			Map<String, Object> item = (Map<String, Object>) subMap.remove("1");
			collect(ruleIndex, stackLevel, item);
			showTrace(parseTrace, operand + "-less " + operand + " predicate: " + item);

		} else if (subMap.size() >= 2) {
			HashMap<String, Object> item = new HashMap<String, Object>();
			item.put(operand, subMap);

			collect(ruleIndex, stackLevel, item);
			showTrace(parseTrace, operand + "-ed predicate: " + item);
		} else {
			showTrace(parseTrace, "Wrong number of entries: " + subMap);
		}
	}

	/**
	 * If the Node has one level of children, use this to push the children down
	 * one level of a tree with the current node as parent
	 * 
	 * @param ruleIndex
	 */
	private void handlePushDown(int ruleIndex) {
		Integer stackLevel = currentStackLevel(ruleIndex);
		Map<String, Object> subMap = removeNodeMap(ruleIndex, stackLevel);
		Object type = subMap.remove("Type");

		Map<String, Object> newMap = collectNewRuleMap(ruleIndex, stackLevel);
		newMap.put(type.toString(), subMap);
		showTrace(parseTrace, "handlePushDown: " + subMap);
	}

	/**
	 * Find Parent and put this one item in it
	 * 
	 * @param parentRuleIndex
	 * @param parentStackLevel
	 * @param item
	 */
	private void addToParent(int parentRuleIndex, Integer parentStackLevel, Object item) {
		Map<String, Object> pMap = getNodeMap(parentRuleIndex, parentStackLevel);
		Integer indx = pMap.size();
		pMap.put(indx.toString(), item);
		collector.put("SKIP", "TRUE");
	}

	// Listener overrides

	@Override
	public void exitSql(@NotNull SQLSelectParserParser.SqlContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		Integer stackLevel = currentStackLevel(ruleIndex);
		Map<String, Object> subMap = removeNodeMap(ruleIndex, stackLevel);
		Object type = subMap.remove("Type");
		collector.put("SQL", subMap.remove("1"));
		// showTrace(resultTrace, collector);
		showTrace(symbolTrace, tableColumnList);
	}

	@Override
	public void exitQuery_expression(@NotNull SQLSelectParserParser.Query_expressionContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		int parentRuleIndex = ctx.getParent().getRuleIndex();
		handleListList(ruleIndex, parentRuleIndex);

		// clear union clause count
		intersectClauseFound = false;
	}

	@Override
	public void enterIntersected_query(@NotNull SQLSelectParserParser.Intersected_queryContext ctx) {
		pushSymbolTable();
	}

	@Override
	public void exitIntersected_query(@NotNull SQLSelectParserParser.Intersected_queryContext ctx) {
		int ruleIndex = ctx.getRuleIndex();

		handleOperandList(ruleIndex, "intersect");

		// Handle symbol tables
		HashMap<String, Object> symbols = tableColumnList;

		if (intersectClauseFound) {
			// Retrieve outer symbol table, insert this symbol table into it
			String key = "intersect" + queryCount;
			popSymbolTable(key, symbols);
			queryCount++;
		} else {
			popSymbolTablePutAll(symbols);
		}

		// clear union clause count
		unionClauseFound = false;
	}

	@Override
	public void enterIntersect_clause(@NotNull SQLSelectParserParser.Intersect_clauseContext ctx) {
		if (!intersectClauseFound) {
			intersectClauseFound = true;
			firstIntersectClause = true;
		} else
			firstIntersectClause = false;
	}

	@Override
	public void exitIntersect_clause(@NotNull SQLSelectParserParser.Intersect_clauseContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		Integer stackLevel = currentStackLevel(ruleIndex);
		Map<String, Object> subMap = getNodeMap(ruleIndex, stackLevel);
		Object type = subMap.remove("Type");

		if (ctx.getChildCount() == 1) {
			Map<String, Object> item = new HashMap<String, Object>();
			item.put("operator", subMap.remove("1"));
			item.put("qualifier", null);
			subMap.put("intersect", item);
		} else if (ctx.getChildCount() == 2) {
			Map<String, Object> item = new HashMap<String, Object>();
			item.put("operator", subMap.remove("1"));
			item.put("qualifier", subMap.remove("2"));
			subMap.put("intersect", item);
		} else {
			showTrace(parseTrace, "Wrong number of entries: " + ctx.getText());
		}
		showTrace(parseTrace, "Intersect Operator: " + subMap);

		// Get first interface to represent intersection output
		if (firstIntersectClause) {
			showTrace(symbolTrace, "Intersect So Far: " + tableColumnList);
			captureQueryInterface();
			showTrace(symbolTrace, "Intersect So Far: " + tableColumnList);

		}
	}

	@Override
	public void enterUnionized_query(@NotNull SQLSelectParserParser.Unionized_queryContext ctx) {
		pushSymbolTable();
	}

	@Override
	public void exitUnionized_query(@NotNull SQLSelectParserParser.Unionized_queryContext ctx) {
		int ruleIndex = ctx.getRuleIndex();

		handleOperandList(ruleIndex, "union");

		// Handle symbol tables
		HashMap<String, Object> symbols = tableColumnList;

		if (unionClauseFound) {
			// Retrieve outer symbol table, insert this symbol table into it
			String key = "union" + queryCount;
			popSymbolTable(key, symbols);
			queryCount++;
		} else {
			popSymbolTablePutAll(symbols);
		}

	}

	@Override
	public void enterUnion_clause(@NotNull SQLSelectParserParser.Union_clauseContext ctx) {
		if (!unionClauseFound) {
			unionClauseFound = true;
			firstUnionClause = true;
		} else
			firstUnionClause = false;
	}

	@Override
	public void exitUnion_clause(@NotNull SQLSelectParserParser.Union_clauseContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		Integer stackLevel = currentStackLevel(ruleIndex);
		Map<String, Object> subMap = getNodeMap(ruleIndex, stackLevel);
		Object type = subMap.remove("Type");

		if (ctx.getChildCount() == 1) {
			Map<String, Object> item = new HashMap<String, Object>();
			item.put("operator", subMap.remove("1"));
			item.put("qualifier", null);
			subMap.put("union", item);
		} else if (ctx.getChildCount() == 2) {
			Map<String, Object> item = new HashMap<String, Object>();
			item.put("operator", subMap.remove("1"));
			item.put("qualifier", subMap.remove("2"));
			subMap.put("union", item);
		} else {
			showTrace(parseTrace, "Wrong number of entries: " + ctx.getText());
		}
		showTrace(parseTrace, "Union Operator: " + subMap);

		// Get first interface to represent union output
		if (firstUnionClause) {
			showTrace(symbolTrace, "Union So Far: " + tableColumnList);
			captureQueryInterface();
			showTrace(symbolTrace, "Union So Far: " + tableColumnList);
		}

	}

	/**
	 * 
	 */
	private void captureQueryInterface() {
		HashMap<String, Object> interfac = getInterfaceFromQuery("query");
		if (interfac == null) {
			interfac = getInterfaceFromQuery("union");
		}
		if (interfac == null) {
			interfac = getInterfaceFromQuery("intersect");
		}
		if (interfac != null) {
			// need to get the interface from inside the query
			HashMap<String, Object> newif = new HashMap<String, Object>();
			for (String key : interfac.keySet()) {
				newif.put(key, "union_column");
			}
			tableColumnList.put("interface", newif);
		}
	}

	/**
	 * @param hdr
	 * @return
	 */
	private HashMap<String, Object> getInterfaceFromQuery(String hdr) {
		String queryName = hdr + (queryCount - 1);
		HashMap<String, Object> query = (HashMap<String, Object>) tableColumnList.get(queryName);
		HashMap<String, Object> interfac = getInterface(query);
		return interfac;
	}

	/**
	 * @param query
	 * @return
	 */
	private HashMap<String, Object> getInterface(HashMap<String, Object> query) {
		HashMap<String, Object> interfac = null;
		if (query != null) {
			interfac = (HashMap<String, Object>) query.get("interface");
		} else
			interfac = null;
		HashMap<String, Object> interfac1 = interfac;
		return interfac1;
	}

	@Override
	public void exitQuery_primary(@NotNull SQLSelectParserParser.Query_primaryContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		handleOneChild(ruleIndex);
	}

	@Override
	public void exitSubquery(@NotNull SQLSelectParserParser.SubqueryContext ctx) {
		int ruleIndex = ctx.getRuleIndex();

		int parentRuleIndex = ctx.getParent().getRuleIndex();
		if (parentRuleIndex == (Integer) SQLSelectParserParser.RULE_nonparenthesized_value_expression_primary) {
			// Subquery is acting as a lookup function
			Integer stackLevel = currentStackLevel(ruleIndex);
			Map<String, Object> subMap = getNodeMap(ruleIndex, stackLevel);
			Object type = subMap.remove("Type");
			HashMap<String, Object> item = new HashMap<String, Object>();

			item.put("lookup", subMap.remove("1"));

			subMap.put("1", item);
			handleListItem(ruleIndex, parentRuleIndex);
		} else {
			// then parent is any non-list parent
			handleOneChild(ruleIndex);
		}
	}

	@Override
	public void enterQuery_specification(@NotNull SQLSelectParserParser.Query_specificationContext ctx) {
		pushSymbolTable();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void exitQuery_specification(@NotNull SQLSelectParserParser.Query_specificationContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		Integer stackLevel = currentStackLevel(ruleIndex);
		Map<String, Object> subMap = getNodeMap(ruleIndex, stackLevel);
		String[] keys = new String[1];
		Object type = subMap.remove("Type");

		keys = subMap.keySet().toArray(keys);

		for (String key : keys) {
			Object obj = subMap.remove(key);
			if (obj instanceof String) {

			} else {
				HashMap<String, Object> value = (HashMap<String, Object>) obj;
				Integer childKey = (Integer) (value).remove("Type");
				Object segment = value.remove(childKey.toString());
				if (childKey == (Integer) SQLSelectParserParser.RULE_select_list) {
					subMap.put("select", segment);
				} else if (childKey == (Integer) SQLSelectParserParser.RULE_set_qualifier) {
					subMap.put("qualifier", segment);
				} else if (childKey == (Integer) SQLSelectParserParser.RULE_from_clause) {
					if (((HashMap<String, Object>) segment).size() == 1) {
						subMap.put("from", ((HashMap<String, Object>) segment).remove("1"));
					} else
						subMap.put("from", segment);
				} else if (childKey == (Integer) SQLSelectParserParser.RULE_where_clause) {
					HashMap<String, Object> item = (HashMap<String, Object>) segment;
					item = (HashMap<String, Object>) item.remove("1");
					subMap.put("where", item);
				} else if (childKey == (Integer) SQLSelectParserParser.RULE_groupby_clause) {
					subMap.put("groupby", segment);
				} else if (childKey == (Integer) SQLSelectParserParser.RULE_having_clause) {
					subMap.put("having", segment);
				} else if (childKey == (Integer) SQLSelectParserParser.RULE_orderby_clause) {
					subMap.put("orderby", segment);
				} else if (childKey == (Integer) SQLSelectParserParser.RULE_limit_clause) {
					subMap.put("limit", segment);
				} else {
					showTrace(parseTrace, "Too Many Entries" + segment);
				}
			}
		}
		showTrace(parseTrace, subMap);

		// Handle symbol tables
		HashMap<String, Object> symbols = tableColumnList;

		// Special handling of queries with only one source: Move "unknown"
		// references to that table
		HashMap<String, Object> unks = (HashMap<String, Object>) symbols.remove("unknown");

		Integer count = 0;
		HashMap<String, Object> hold = null;

		if (unks != null) {
			for (String tab_ref : symbols.keySet()) {
				if ((tab_ref.equals("interface")) || (tab_ref.startsWith("def_query"))) {
				} else {
					Object item = symbols.get(tab_ref);
					if (item instanceof HashMap<?, ?>) {
						hold = (HashMap<String, Object>) item;
						count++;
					}
				}
				if (count > 1)
					break;
			}
		}

		if (count == 1) {
			// just one table referenced, put all unknowns into it
			hold.putAll(unks);
		} else {
			// otherwise put things back
			if (unks != null)
				symbols.put("unknown", unks);
		}

		// Retrieve outer symbol table, insert this symbol table into it
		String key = "query" + queryCount;
		popSymbolTable(key, symbols);
		queryCount++;
	}

	@Override
	public void exitFrom_clause(@NotNull SQLSelectParserParser.From_clauseContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		handlePushDown(ruleIndex);
	}

	@Override
	public void exitTable_reference_list(@NotNull SQLSelectParserParser.Table_reference_listContext ctx) {
		int ruleIndex = ctx.getRuleIndex();

		handleOperandList(ruleIndex, "join");
	}

	@SuppressWarnings("unchecked")
	@Override
	public void exitTable_primary(@NotNull SQLSelectParserParser.Table_primaryContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		int parentRuleIndex = ctx.getParent().getRuleIndex();

		Integer stackLevel = currentStackLevel(ruleIndex);
		Integer parentStackLevel = currentStackLevel(parentRuleIndex);

		Map<String, Object> subMap = removeNodeMap(ruleIndex, stackLevel);
		Object type = subMap.remove("Type");
		Map<String, Object> item;
		String alias = null;

		if (ctx.getChildCount() == 1) {
			item = (Map<String, Object>) subMap.remove("1");
			item.put("alias", null);

			Object table = item.get("table");
			if (table != null) {
				alias = table.toString();
				collectTable(alias, table);

				subMap.put("table", item);
			} else {
				alias = "unnamed";
				Map<String, Object> aliasMap = new HashMap<String, Object>();
				aliasMap.put(alias, alias);
				Boolean done = handleQuery("query", aliasMap, alias, item);
				if (!done)
					done = handleQuery("union", aliasMap, alias, item);
				if (!done)
					done = handleQuery("intersect", aliasMap, alias, item);

			}

		} else if (ctx.getChildCount() == 2) {
			item = new HashMap<String, Object>();
			Map<String, Object> reference = (Map<String, Object>) subMap.remove("1");

			Map<String, Object> aliasMap = (Map<String, Object>) subMap.remove("2");
			alias = (String) aliasMap.get("alias");
			item.putAll(aliasMap);

			// Try various alternatives
			Object table = reference.get("table");
			if (table != null) {
				item.putAll(reference);
				collectTable(alias, table);
			} else {
				Boolean done = handleQuery("query", item, alias, reference);
				if (!done)
					done = handleQuery("union", item, alias, reference);
				if (!done)
					done = handleQuery("intersect", item, alias, reference);
			}

			subMap.put("table", item);
		} else {
			showTrace(parseTrace, "Wrong number of entries: " + ctx.getText());
		}
		addToParent(parentRuleIndex, parentStackLevel, subMap);
		showTrace(parseTrace, "TABLE PRIMARY: " + subMap);
	}

	/**
	 * @param hdr
	 * @param item
	 * @param alias
	 * @param reference
	 * @return
	 */
	private Boolean handleQuery(String hdr, Map<String, Object> item, String alias, Map<String, Object> reference) {
		String queryName = hdr + (queryCount - 1);
		Map<String, Object> query = (Map<String, Object>) tableColumnList.remove(queryName);
		if (query != null) {
			item.put(hdr, reference);

			// add alias to query
			collectTable(alias, queryName);

			// propagate interface to outer layer of query
			Map<String, Object> hold = (Map<String, Object>) tableColumnList.get(queryName);
			// Move unknowns to query
			Map<String, Object> unk = (Map<String, Object>) tableColumnList.remove("unknown");
			// hold.putAll(unk);

			// move any other interface elements to query and empty unknowns
			Map<String, Object> interfac = (Map<String, Object>) query.get("interface");
			for (String key : interfac.keySet()) {
				Object unkItem = unk.remove(key);
				if (unkItem != null)
					hold.put(key, unkItem);
				else
					hold.put(key, key);
				;
			}

			// if any unknowns left, put them back into table
			if (unk.size() > 0)
				tableColumnList.put("unknown", unk);
			
			// Add query definition back into symbol table
			tableColumnList.put("def_" + queryName, query);
			return true;
		} else
			return false;
	}

	@Override
	public void exitUnqualified_join(@NotNull SQLSelectParserParser.Unqualified_joinContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		int parentRuleIndex = ctx.getParent().getRuleIndex();

		Integer stackLevel = currentStackLevel(ruleIndex);
		Integer parentStackLevel = currentStackLevel(parentRuleIndex);

		Map<String, Object> subMap = removeNodeMap(ruleIndex, stackLevel);
		showTrace(parseTrace, subMap);
		subMap.remove("Type");
		if (ctx.getChildCount() == 2)
			subMap.put("join", ctx.getText());
		else if (ctx.getChildCount() == 3) {
			String type = (String) subMap.remove("1");
			subMap.put("join", ctx.getChild(0).getText() + ctx.getChild(2).getText());
			subMap.put("join_type", type);
		}
		// Add item to parent map
		addToParent(parentRuleIndex, parentStackLevel, subMap);
		showTrace(parseTrace, "UNQUALIFIED JOIN: " + subMap);
	}

	@Override
	public void exitQualified_join(@NotNull SQLSelectParserParser.Qualified_joinContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		int parentRuleIndex = ctx.getParent().getRuleIndex();

		Integer stackLevel = currentStackLevel(ruleIndex);
		Integer parentStackLevel = currentStackLevel(parentRuleIndex);

		Map<String, Object> subMap = removeNodeMap(ruleIndex, stackLevel);

		if (subMap == null) {
			// Qualified join has no map
			subMap = makeRuleMap(ruleIndex);
		} else
			subMap.remove("1");
		subMap.remove("Type");
		if (ctx.getChildCount() == 1)
			subMap.put("join", ctx.getText());
		else if (ctx.getChildCount() == 2) {
			// String type = (String) subMap.remove("1");
			subMap.put("join", ctx.getChild(0).getText());
			// subMap.put("join_type", type);
		} else if (ctx.getChildCount() == 3) {
			// String type = (String) subMap.remove("1");
			subMap.put("join", ctx.getChild(0).getText() + ctx.getChild(1).getText());
			// subMap.put("join_type", type);
		}
		// Add item to parent map
		addToParent(parentRuleIndex, parentStackLevel, subMap);
		showTrace(parseTrace, "QUALIFIED JOIN: " + subMap);
	}

	// @Override
	// public void exitJoin_type(@NotNull SQLSelectParserParser.Join_typeContext
	// ctx) {
	// int ruleIndex = ctx.getRuleIndex();
	// Integer stackLevel = currentStackLevel(ruleIndex);
	// Map<String, Object> subMap = getNodeMap(ruleIndex, stackLevel);
	// }

	@SuppressWarnings("unchecked")
	@Override
	public void exitJoin_specification(@NotNull SQLSelectParserParser.Join_specificationContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		int parentRuleIndex = ctx.getParent().getRuleIndex();

		Integer stackLevel = currentStackLevel(ruleIndex);
		Integer parentStackLevel = currentStackLevel(parentRuleIndex);

		Map<String, Object> subMap = removeNodeMap(ruleIndex, stackLevel);
		Object type = subMap.remove("Type");
		Map<String, Object> item;
		if (subMap.size() == 1) {
			// get the most recent JOIN condition
			Map<String, Object> pMap = getNodeMap(parentRuleIndex, parentStackLevel);
			Integer indx = pMap.size() - 2;
			Map<String, Object> join = (Map<String, Object>) pMap.get(indx.toString());
			// Add On clause to previous Join statement
			item = (Map<String, Object>) subMap.remove("1");
			join.put("on", item);
			showTrace(parseTrace, "join On Clause: " + join);
		} else {
			showTrace(parseTrace, "Wrong number of entries: " + ctx.getText());
		}
		collector.put("SKIP", "TRUE");
	}

	@Override
	public void exitJoin_condition(@NotNull SQLSelectParserParser.Join_conditionContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		handleOneChild(ruleIndex);
	}

	@Override
	public void exitNamed_columns_join(@NotNull SQLSelectParserParser.Named_columns_joinContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		handleOneChild(ruleIndex);
	}

	@Override
	public void exitWhere_clause(@NotNull SQLSelectParserParser.Where_clauseContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		handlePushDown(ruleIndex);
	}

	@Override
	public void exitGroupby_clause(@NotNull SQLSelectParserParser.Groupby_clauseContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		handlePushDown(ruleIndex);
	}

	@Override
	public void exitHaving_clause(@NotNull SQLSelectParserParser.Having_clauseContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		handlePushDown(ruleIndex);
	}

	@Override
	public void exitOrderby_clause(@NotNull SQLSelectParserParser.Orderby_clauseContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		handlePushDown(ruleIndex);
	}

	@Override
	public void exitLimit_clause(@NotNull SQLSelectParserParser.Limit_clauseContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		handlePushDown(ruleIndex);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void exitCase_expression(@NotNull SQLSelectParserParser.Case_expressionContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		int parentRuleIndex = ctx.getParent().getRuleIndex();

		Integer stackLevel = currentStackLevel(ruleIndex);
		Integer parentStackLevel = currentStackLevel(parentRuleIndex);

		Map<String, Object> subMap = removeNodeMap(ruleIndex, stackLevel);
		Object type = subMap.remove("Type");
		if (subMap.size() == 2) {
			subMap.putAll((Map<String, Object>) subMap.remove("1"));
			subMap.putAll((Map<String, Object>) subMap.remove("2"));
		} else if (subMap.size() == 3) {
			subMap.put("item", subMap.remove("1"));
			subMap.putAll((Map<String, Object>) subMap.remove("2"));
			subMap.putAll((Map<String, Object>) subMap.remove("3"));
		} else {
			showTrace(parseTrace, "Wrong number of entries: " + ctx.getText());
		}

		Map<String, Object> item = new HashMap<String, Object>();
		item.put("case", subMap);
		addToParent(parentRuleIndex, parentStackLevel, item);
		showTrace(parseTrace, "Case: " + item);

	}

	@Override
	public void exitWhen_clause_list(@NotNull SQLSelectParserParser.When_clause_listContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		int parentRuleIndex = ctx.getParent().getRuleIndex();

		Integer stackLevel = currentStackLevel(ruleIndex);
		Integer parentStackLevel = currentStackLevel(parentRuleIndex);

		Map<String, Object> subMap = removeNodeMap(ruleIndex, stackLevel);
		Object type = subMap.remove("Type");
		Map<String, Object> item = new HashMap<String, Object>();
		if (subMap.size() == 1) {
			item.put("clauses", subMap);
		} else {
			showTrace(parseTrace, "Wrong number of entries: " + ctx.getText());
		}
		addToParent(parentRuleIndex, parentStackLevel, item);
		showTrace(parseTrace, "When Clause List: " + item);
	}

	@Override
	public void exitSearched_when_clause(@NotNull SQLSelectParserParser.Searched_when_clauseContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		int parentRuleIndex = ctx.getParent().getRuleIndex();

		Integer stackLevel = currentStackLevel(ruleIndex);
		Integer parentStackLevel = currentStackLevel(parentRuleIndex);

		Map<String, Object> subMap = removeNodeMap(ruleIndex, stackLevel);
		Object type = subMap.remove("Type");
		Map<String, Object> item = new HashMap<String, Object>();
		if (subMap.size() == 2) {
			item.put("when", subMap.remove("1"));
			item.put("then", subMap.remove("2"));
		} else {
			showTrace(parseTrace, "Wrong number of entries: " + ctx.getText());
		}
		addToParent(parentRuleIndex, parentStackLevel, item);
		showTrace(parseTrace, "Case When Clause: " + item);

	}

	@Override
	public void exitElse_clause(@NotNull SQLSelectParserParser.Else_clauseContext ctx) {
		// int ruleIndex = ctx.getRuleIndex();
		// handleOneChild(ruleIndex);

		int ruleIndex = ctx.getRuleIndex();
		int parentRuleIndex = ctx.getParent().getRuleIndex();

		Integer stackLevel = currentStackLevel(ruleIndex);
		Integer parentStackLevel = currentStackLevel(parentRuleIndex);

		Map<String, Object> subMap = removeNodeMap(ruleIndex, stackLevel);
		Object type = subMap.remove("Type");
		Map<String, Object> item = new HashMap<String, Object>();
		if (subMap.size() == 1) {
			item.put("else", subMap.remove("1"));
		} else {
			showTrace(parseTrace, "Wrong number of entries: " + ctx.getText());
		}
		addToParent(parentRuleIndex, parentStackLevel, item);
		showTrace(parseTrace, "Else Clause: " + item);

	}

	@Override
	public void exitResult(@NotNull SQLSelectParserParser.ResultContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		handleOneChild(ruleIndex);
	}

	@Override
	public void exitSearch_condition(@NotNull SQLSelectParserParser.Search_conditionContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		handleOneChild(ruleIndex);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void exitSelect_item(@NotNull SQLSelectParserParser.Select_itemContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		int parentRuleIndex = ctx.getParent().getRuleIndex();

		Integer stackLevel = currentStackLevel(ruleIndex);
		Integer parentStackLevel = currentStackLevel(parentRuleIndex);

		Map<String, Object> subMap = removeNodeMap(ruleIndex, stackLevel);
		subMap.remove("Type");
		Object item;
		HashMap<String, Object> reference = new HashMap<String, Object>();
		String alias = null;

		if (subMap.size() == 1) {
			showTrace(parseTrace, "Just One Item: " + subMap);
			item = subMap.remove("1");
			reference.putAll((Map<String, Object>) item);
			HashMap<String, Object> column = (HashMap<String, Object>) reference.get("column");
			if (column == null)
				alias = "unnamed";
			else
				alias = (String) column.get("name");
		} else {
			showTrace(parseTrace, "Item and Alias: " + subMap);
			item = subMap.remove("1");
			reference.putAll((Map<String, Object>) item);
			Map<String, Object> aliasMap = (Map<String, Object>) subMap.remove("2");
			alias = (String) aliasMap.get("alias");
			((Map<String, Object>) item).putAll(aliasMap);
		}
		addToParent(parentRuleIndex, parentStackLevel, item);
		showTrace(parseTrace, "SELECT ITEM: " + item);

		// Add item to symbol table
		HashMap<String, Object> selectInterface = (HashMap<String, Object>) tableColumnList.get("interface");
		if (selectInterface == null) {
			selectInterface = new HashMap<String, Object>();
			tableColumnList.put("interface", selectInterface);
		}
		selectInterface.put(alias, reference);
	}

	@Override
	public void exitColumn_name_list(@NotNull SQLSelectParserParser.Column_name_listContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		handlePushDown(ruleIndex);
	}

	@Override
	public void exitTable_or_query_name(@NotNull SQLSelectParserParser.Table_or_query_nameContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		Integer stackLevel = currentStackLevel(ruleIndex);
		Map<String, Object> subMap = getNodeMap(ruleIndex, stackLevel);
		Object type = subMap.remove("Type");

		if (subMap.size() == 1) {
			showTrace(parseTrace, "Just One Identifier: " + subMap);
			String table = (String) subMap.remove("1");

			collectTable(table, table);

			subMap.put("table", table);
			showTrace(parseTrace, "table: " + table + " Map: " + subMap);
		} else if (subMap.size() == 2) {
			showTrace(parseTrace, "Two entries: " + subMap);
			String schema = (String) subMap.remove("1");
			subMap.put("schema", schema);
			String table = (String) subMap.remove("2");

			collectTable(table, table);

			subMap.put("table", table);
			showTrace(parseTrace, "Schema: " + schema + " Table: " + table + " Map: " + subMap);
		} else if (subMap.size() == 3) {
			showTrace(parseTrace, "Three entries: " + subMap);
			String dbname = (String) subMap.remove("1");
			subMap.put("dbname", dbname);
			String schema = (String) subMap.remove("2");
			subMap.put("schema", schema);
			String table = (String) subMap.remove("3");

			collectTable(table, table);

			subMap.put("table", table);
			showTrace(parseTrace, "Database: " + dbname + "Schema: " + schema + " Table: " + table + " Map: " + subMap);
		} else {
			showTrace(parseTrace, "Too many entries: " + subMap);
		}
	}

	@Override
	public void exitAs_clause(@NotNull SQLSelectParserParser.As_clauseContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		Integer stackLevel = currentStackLevel(ruleIndex);
		Map<String, Object> subMap = getNodeMap(ruleIndex, stackLevel);
		Object type = subMap.remove("Type");

		if (subMap.size() == 1) {
			showTrace(parseTrace, "Just One Identifier: " + subMap);
			String alias = (String) subMap.remove("1");
			subMap.put("alias", alias);
			showTrace(parseTrace, "Alias: " + alias + " Map: " + subMap);
		} else {
			showTrace(parseTrace, "Too many entries: " + subMap);
		}

	}

	@Override
	public void exitOr_predicate(@NotNull SQLSelectParserParser.Or_predicateContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		String key = "OR";

		handleOperandList(ruleIndex, key);
	}

	@Override
	public void exitAnd_predicate(@NotNull SQLSelectParserParser.And_predicateContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		String key = "AND";

		handleOperandList(ruleIndex, key);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void exitNegative_predicate(@NotNull SQLSelectParserParser.Negative_predicateContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		Integer stackLevel = currentStackLevel(ruleIndex);
		Map<String, Object> subMap = getNodeMap(ruleIndex, stackLevel);
		Object type = subMap.remove("Type");

		if (subMap.size() == 1) {
			showTrace(parseTrace, "Not negated predicate: " + subMap);

			// String negation = (String) subMap.remove("1");

			Map<String, Object> left = (Map<String, Object>) subMap.remove("1");
			subMap.putAll(left);

			showTrace(parseTrace, "Negated predicate: " + subMap);

		} else if (subMap.size() == 2) {
			showTrace(parseTrace, "Negated predicate: " + subMap);

			String negation = (String) subMap.remove("1");

			Map<String, Object> left = (Map<String, Object>) subMap.remove("2");
			subMap.put(negation, left);

			showTrace(parseTrace, "Negated predicate: " + subMap);

		} else {
			showTrace(parseTrace, "Wrong number of entries: " + subMap);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void exitBasic_clause(@NotNull SQLSelectParserParser.Basic_clauseContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		Integer stackLevel = currentStackLevel(ruleIndex);
		Map<String, Object> subMap = getNodeMap(ruleIndex, stackLevel);
		Object type = subMap.remove("Type");

		if (subMap.size() == 1) {
			Map<String, Object> left = (Map<String, Object>) subMap.remove("1");
			subMap.putAll(left);
			showTrace(parseTrace, "Clause: " + subMap);
		} else if (subMap.size() == 2) {
			Map<String, Object> item = (Map<String, Object>) subMap.remove("1");
			subMap.put("Clause", item.get("1"));

			item = (Map<String, Object>) subMap.remove("2");
			subMap.put("Is Clause", item.get("1"));
			showTrace(parseTrace, "Clause: " + subMap);

		} else {
			showTrace(parseTrace, "Wrong number of entries: " + subMap);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void exitNull_predicate(@NotNull SQLSelectParserParser.Null_predicateContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		Integer stackLevel = currentStackLevel(ruleIndex);
		Map<String, Object> subMap = getNodeMap(ruleIndex, stackLevel);
		Object type = subMap.remove("Type");

		if (subMap.size() == 2) {
			showTrace(parseTrace, "Comparison: " + subMap);
			Map<String, Object> left = (Map<String, Object>) subMap.remove("1");
			subMap.put("left", left);

			subMap.putAll((Map<String, Object>) subMap.remove("2"));

			showTrace(parseTrace, "IS NULL Clause: " + subMap);

		} else {
			showTrace(parseTrace, "Wrong number of entries: " + subMap);
		}
	}

	@Override
	public void exitIs_null_clause(@NotNull SQLSelectParserParser.Is_null_clauseContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		int parentRuleIndex = ctx.getParent().getRuleIndex();

		Integer stackLevel = currentStackLevel(ruleIndex);
		Integer parentStackLevel = currentStackLevel(parentRuleIndex);

		Map<String, Object> subMap = removeNodeMap(ruleIndex, stackLevel);

		if (subMap == null) {
			// unqualified select all has no map
			subMap = makeRuleMap(ruleIndex);
		}
		subMap.remove("Type");
		if (ctx.getChildCount() == 2) {
			subMap.put("operator", ctx.getChild(0).getText() + " " + ctx.getChild(1).getText());
		} else if (ctx.getChildCount() == 3) {
			subMap.put("operator",
					ctx.getChild(0).getText() + " " + ctx.getChild(1).getText() + " " + ctx.getChild(2).getText());
		}
		// Add item to parent map
		addToParent(parentRuleIndex, parentStackLevel, subMap);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void exitParen_clause(@NotNull SQLSelectParserParser.Paren_clauseContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		Integer stackLevel = currentStackLevel(ruleIndex);
		Map<String, Object> subMap = getNodeMap(ruleIndex, stackLevel);
		Object type = subMap.remove("Type");

		if (subMap.size() == 1) {
			Map<String, Object> item = (Map<String, Object>) subMap.remove("1");
			subMap.put("Parentheses", item);
			showTrace(parseTrace, "Parenthesed Clause: " + subMap);

		} else {
			showTrace(parseTrace, "Wrong number of entries: " + subMap);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void exitComparison_predicate(@NotNull SQLSelectParserParser.Comparison_predicateContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		Integer stackLevel = currentStackLevel(ruleIndex);
		Map<String, Object> subMap = getNodeMap(ruleIndex, stackLevel);
		Object type = subMap.remove("Type");

		if (subMap.size() == 3) {
			showTrace(parseTrace, "Comparison: " + subMap);
			Map<String, Object> left = (Map<String, Object>) subMap.remove("1");
			subMap.put("left", left);

			String operator = (String) subMap.remove("2");
			subMap.put("operator", operator);

			Map<String, Object> right = (Map<String, Object>) subMap.remove("3");
			subMap.put("right", right);

			showTrace(parseTrace, "Comparison: " + subMap);

		} else {
			showTrace(parseTrace, "Wrong number of entries: " + subMap);
		}
	}

	@Override
	public void exitIn_predicate(@NotNull SQLSelectParserParser.In_predicateContext ctx) {
		int ruleIndex = ctx.getRuleIndex();

		Integer stackLevel = currentStackLevel(ruleIndex);
		Map<String, Object> subMap = removeNodeMap(ruleIndex, stackLevel);
		subMap.remove("Type");

		if (subMap.size() == 2) {
			showTrace(parseTrace, "In predicate: " + subMap);
			subMap.put("item", subMap.remove("1"));
			subMap.put("in_list", subMap.remove("2"));
		} else if (subMap.size() == 3) {
			showTrace(parseTrace, "In predicate: " + subMap);
			subMap.put("item", subMap.remove("1"));
			subMap.put("not_in_list", subMap.remove("3"));
		} else {
			showTrace(parseTrace, "Wrong number of entries: " + subMap);
		}

		Map<String, Object> item = new HashMap<String, Object>();
		item.put("in", subMap);

		int parentRuleIndex = ctx.getParent().getRuleIndex();
		Integer parentStackLevel = currentStackLevel(parentRuleIndex);
		addToParent(parentRuleIndex, parentStackLevel, item);
	}

	@Override
	public void exitIn_predicate_value(@NotNull SQLSelectParserParser.In_predicate_valueContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		handleOneChild(ruleIndex);
	}

	@Override
	public void exitIn_value_list(@NotNull SQLSelectParserParser.In_value_listContext ctx) {
		int ruleIndex = ctx.getRuleIndex();

		Integer stackLevel = currentStackLevel(ruleIndex);
		Map<String, Object> subMap = removeNodeMap(ruleIndex, stackLevel);
		subMap.remove("Type");
		Map<String, Object> item = new HashMap<String, Object>();
		item.put("list", subMap);

		int parentRuleIndex = ctx.getParent().getRuleIndex();
		Integer parentStackLevel = currentStackLevel(parentRuleIndex);
		addToParent(parentRuleIndex, parentStackLevel, item);
	}

	@Override
	public void exitParenthesized_value_expression(
			@NotNull SQLSelectParserParser.Parenthesized_value_expressionContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		Integer stackLevel = currentStackLevel(ruleIndex);
		Map<String, Object> subMap = getNodeMap(ruleIndex, stackLevel);
		Object type = subMap.remove("Type");

		if (subMap.size() == 1) {
			Map<String, Object> item = (Map<String, Object>) subMap.remove("1");
			subMap.put("Parentheses", item);
			showTrace(parseTrace, "Parenthesed Clause: " + subMap);

		} else {
			showTrace(parseTrace, "Wrong number of entries: " + subMap);
		}
	}

	@Override
	public void exitNonparenthesized_value_expression_primary(
			@NotNull SQLSelectParserParser.Nonparenthesized_value_expression_primaryContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		handleOneChild(ruleIndex);
	}

	@Override
	public void exitSelect_list(@NotNull SQLSelectParserParser.Select_listContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		handlePushDown(ruleIndex);
	}

	@Override
	public void exitGeneral_set_function(@NotNull SQLSelectParserParser.General_set_functionContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		Integer stackLevel = currentStackLevel(ruleIndex);
		Map<String, Object> subMap = getNodeMap(ruleIndex, stackLevel);
		Object type = subMap.remove("Type");

		Map<String, Object> item = new HashMap<String, Object>();

		if (subMap.size() == 2) {
			item.put("function", subMap.remove("1"));
			item.put("qualifier", null);
			item.put("parameters", subMap.remove("2"));
			subMap.put("function", item);
		} else if (subMap.size() == 3) {
			item.put("function", subMap.remove("1"));
			item.put("qualifier", subMap.remove("2"));
			item.put("parameters", subMap.remove("3"));
			subMap.put("function", item);
		} else {
			showTrace(parseTrace, "Wrong number of entries: " + subMap);
		}
		showTrace(parseTrace, "Aggregate Function: " + subMap);
	}

	@Override
	public void exitCount_all_aggregate(@NotNull SQLSelectParserParser.Count_all_aggregateContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		Integer stackLevel = currentStackLevel(ruleIndex);
		Map<String, Object> subMap = getNodeMap(ruleIndex, stackLevel);
		Object type = subMap.remove("Type");

		Map<String, Object> item = new HashMap<String, Object>();

		if (subMap.size() == 0) {
			item.put("function", "COUNT");
			item.put("qualifier", null);
			item.put("parameters", "*");
			subMap.put("function", item);
		} else {
			showTrace(parseTrace, "Wrong number of entries: " + subMap);
		}
		showTrace(parseTrace, "Aggregate Function: " + subMap);
	}

	@Override
	public void exitFactor(@NotNull SQLSelectParserParser.FactorContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		Integer stackLevel = currentStackLevel(ruleIndex);
		Map<String, Object> subMap = getNodeMap(ruleIndex, stackLevel);

		if (subMap.size() == 3) {
			String sign = (String) subMap.remove("1");
			if (sign.equals("-")) {
				// multiply by -1
				// 1={left={literal=-1}, right={...}, operand=*}
				Map<String, Object> left = new HashMap<String, Object>();
				left.put("literal", "-1");
				Map<String, Object> item = new HashMap<String, Object>();
				item.put("left", left);
				item.put("operand", "*");
				item.put("right", subMap.remove("2"));
				subMap.put("1", item);
			} else {
				subMap.put("1", subMap.remove("2"));
			}
		}
		showTrace(parseTrace, "Factor: " + subMap);
		handleOneChild(ruleIndex);
	}

	@Override
	public void exitRow_value_predicand_list(@NotNull SQLSelectParserParser.Row_value_predicand_listContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		handleOneChild(ruleIndex);
	}

	@Override
	public void exitNumeric_primary(@NotNull SQLSelectParserParser.Numeric_primaryContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		handleOneChild(ruleIndex);
	}

	@Override
	public void exitCommon_value_expression(@NotNull SQLSelectParserParser.Common_value_expressionContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		handleOneChild(ruleIndex);
	}

	@Override
	public void exitColumn_reference(@NotNull SQLSelectParserParser.Column_referenceContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		Integer stackLevel = currentStackLevel(ruleIndex);
		Map<String, Object> subMap = getNodeMap(ruleIndex, stackLevel);
		subMap.remove("Type");

		Map<String, Object> item = new HashMap<String, Object>();

		if (subMap.size() == 1) {
			showTrace(parseTrace, "Just One Identifier: " + subMap);
			item.put("table_ref", null);

			Object name = subMap.remove("1");
			collectTableItem("unknown", name);

			item.put("name", name);
			subMap.put("column", item);
		} else if (subMap.size() == 2) {
			showTrace(parseTrace, "Two entries: " + subMap);
			item.put("table_ref", subMap.remove("1"));

			Object name = subMap.remove("2");
			collectTableItem(item.get("table_ref"), name);

			item.put("name", name);
			subMap.put("column", item);
		} else {
			showTrace(parseTrace, "Too many entries: " + subMap);
		}
		showTrace(parseTrace, "Column Reference: " + subMap);

	}

	@Override
	public void exitSelect_all_columns(@NotNull SQLSelectParserParser.Select_all_columnsContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		int parentRuleIndex = ctx.getParent().getRuleIndex();

		Integer stackLevel = currentStackLevel(ruleIndex);
		Integer parentStackLevel = currentStackLevel(parentRuleIndex);

		Map<String, Object> subMap = removeNodeMap(ruleIndex, stackLevel);

		Map<String, Object> item = new HashMap<String, Object>();

		if (subMap == null) {
			// unqualified select all has no map
			subMap = makeRuleMap(ruleIndex);
		}
		subMap.remove("Type");
		if (ctx.getChildCount() == 1) {
			showTrace(parseTrace, "Just One Identifier: " + ctx.getText());
			item.put("table_ref", "*");
			item.put("name", "*");

			collectTableItem("unknown", "*");

			subMap.put("column", item);
		} else if (ctx.getChildCount() == 3) {
			showTrace(parseTrace, "Three entries: " + ctx.getText());
			item.put("table_ref", ctx.getChild(0).getText());

			collectTableItem(item.get("table_ref"), "*");

			item.put("name", "*");
			subMap.put("column", item);
		}
		// Add item to parent map
		addToParent(parentRuleIndex, parentStackLevel, subMap);
		showTrace(parseTrace, "Table Alias . Column Name: " + subMap);
	}

	@Override
	public void exitAdditive_expression(@NotNull SQLSelectParserParser.Additive_expressionContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		Integer stackLevel = currentStackLevel(ruleIndex);
		Map<String, Object> subMap = getNodeMap(ruleIndex, stackLevel);
		subMap.remove("Type");

		if (subMap.size() == 1) {
			handleOneChild(ruleIndex);
		} else if (subMap.size() == 2) {
			subMap.put("left", subMap.remove("1"));
			subMap.put("right", subMap.remove("2"));
			subMap.put("operand", ctx.getChild(1).getText());
			showTrace(parseTrace, "Operation: " + subMap);
		} else {
			showTrace(parseTrace, "Too many entries: " + subMap);
		}
	}

	@Override
	public void exitMultiplicative_expression(@NotNull SQLSelectParserParser.Multiplicative_expressionContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		Integer stackLevel = currentStackLevel(ruleIndex);
		Map<String, Object> subMap = getNodeMap(ruleIndex, stackLevel);
		subMap.remove("Type");

		if (subMap.size() == 1) {
			handleOneChild(ruleIndex);
		} else if (subMap.size() == 2) {
			subMap.put("left", subMap.remove("1"));
			subMap.put("right", subMap.remove("2"));
			subMap.put("operand", ctx.getChild(1).getText());
			showTrace(parseTrace, "Operation: " + subMap);
		} else {
			showTrace(parseTrace, "Too many entries: " + subMap);
		}

	}

	@Override
	public void exitBoolean_value_expression(@NotNull SQLSelectParserParser.Boolean_value_expressionContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		handleOneChild(ruleIndex);
	}

	@Override
	public void exitBoolean_primary(@NotNull SQLSelectParserParser.Boolean_primaryContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		handleOneChild(ruleIndex);
	}

	@Override
	public void exitPredicate(@NotNull SQLSelectParserParser.PredicateContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		handleOneChild(ruleIndex);
	}

	@Override
	public void exitValue_expression_primary(@NotNull SQLSelectParserParser.Value_expression_primaryContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		handleOneChild(ruleIndex);
	}

	@Override
	public void exitString_value_expression(@NotNull SQLSelectParserParser.String_value_expressionContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		handleOperandList(ruleIndex, "concatenate");
	}

	@Override
	public void exitCharacter_primary(@NotNull SQLSelectParserParser.Character_primaryContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		int parentRuleIndex = ctx.getParent().getRuleIndex();

		Integer stackLevel = currentStackLevel(ruleIndex);
		Integer parentStackLevel = currentStackLevel(parentRuleIndex);

		Map<String, Object> subMap = removeNodeMap(ruleIndex, stackLevel);
		subMap.remove("Type");

		if (subMap.size() == 1) {
			showTrace(parseTrace, "Just One Item: " + subMap);
			addToParent(parentRuleIndex, parentStackLevel, subMap.remove("1"));
		}
	}

	@Override
	public void exitTrim_function(@NotNull SQLSelectParserParser.Trim_functionContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		Integer stackLevel = currentStackLevel(ruleIndex);
		Map<String, Object> subMap = getNodeMap(ruleIndex, stackLevel);
		Object type = subMap.remove("Type");

		Map<String, Object> item = new HashMap<String, Object>();

		if (subMap.size() == 2) {
			item.put("function", subMap.remove("1"));
			item.put("parameters", subMap.remove("2"));
			subMap.put("function", item);
		} else {
			showTrace(parseTrace, "Wrong number of entries: " + subMap);
		}
		showTrace(parseTrace, "TRIM Function: " + subMap);
	}

	@Override
	public void exitMysql_trim_operands(@NotNull SQLSelectParserParser.Mysql_trim_operandsContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		int parentRuleIndex = ctx.getParent().getRuleIndex();

		Integer stackLevel = currentStackLevel(ruleIndex);
		Integer parentStackLevel = currentStackLevel(parentRuleIndex);

		Map<String, Object> subMap = removeNodeMap(ruleIndex, stackLevel);
		Object type = subMap.remove("Type");

		Map<String, Object> item = new HashMap<String, Object>();

		if (subMap.size() == 2) {
			item.put("trim_character", subMap.remove("1"));
			item.put("value", subMap.remove("2"));

		} else if (subMap.size() == 3) {
			item.put("qualifier", subMap.remove("1"));
			item.put("trim_character", subMap.remove("2"));
			item.put("value", subMap.remove("3"));
		}

		// Add item to parent map
		showTrace(parseTrace, "Trim Operands: " + item);
		addToParent(parentRuleIndex, parentStackLevel, item);
	}

	@Override
	public void exitOther_trim_operands(@NotNull SQLSelectParserParser.Other_trim_operandsContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		int parentRuleIndex = ctx.getParent().getRuleIndex();

		Integer stackLevel = currentStackLevel(ruleIndex);
		Integer parentStackLevel = currentStackLevel(parentRuleIndex);

		Map<String, Object> subMap = removeNodeMap(ruleIndex, stackLevel);
		Object type = subMap.remove("Type");

		Map<String, Object> item = new HashMap<String, Object>();

		if (subMap.size() == 2) {
			item.put("trim_character", subMap.remove("2"));
			item.put("value", subMap.remove("1"));
		}

		// Add item to parent map
		showTrace(parseTrace, "Trim Operands: " + item);
		addToParent(parentRuleIndex, parentStackLevel, item);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void exitRoutine_invocation(@NotNull SQLSelectParserParser.Routine_invocationContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		int parentRuleIndex = ctx.getParent().getRuleIndex();

		Integer stackLevel = currentStackLevel(ruleIndex);
		Integer parentStackLevel = currentStackLevel(parentRuleIndex);

		Map<String, Object> subMap = removeNodeMap(ruleIndex, stackLevel);
		Object type = subMap.remove("Type");

		Map<String, Object> item = new HashMap<String, Object>();

		if (subMap.size() == 2) {
			item.put("function", subMap.remove("1"));
			subMap = (Map<String, Object>) subMap.remove("2");
			type = subMap.remove("Type");
			item.put("parameters", subMap.remove(type.toString()));
			subMap.put("function", item);
		} else {
			showTrace(parseTrace, "Wrong number of entries: " + subMap);
		}
		showTrace(parseTrace, "Function: " + subMap);
		addToParent(parentRuleIndex, parentStackLevel, subMap);
	}

	@Override
	public void exitFunction_name(@NotNull SQLSelectParserParser.Function_nameContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		handleOneChild(ruleIndex);
	}

	@Override
	public void exitSql_argument_list(@NotNull SQLSelectParserParser.Sql_argument_listContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		// int parentRuleIndex = ctx.getParent().getRuleIndex();
		// handleListList(ruleIndex, parentRuleIndex);
		handlePushDown(ruleIndex);
	}

	@Override
	public void exitValue_expression(@NotNull SQLSelectParserParser.Value_expressionContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		int parentRuleIndex = ctx.getParent().getRuleIndex();
		if (parentRuleIndex == (Integer) SQLSelectParserParser.RULE_sql_argument_list) {
			handleListItem(ruleIndex, parentRuleIndex);
		} else {
			// then parent is any non-list parent
			handleOneChild(ruleIndex);
		}
	}

	@Override
	public void exitRow_value_expression(@NotNull SQLSelectParserParser.Row_value_expressionContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		int parentRuleIndex = ctx.getParent().getRuleIndex();
		if (parentRuleIndex == (Integer) SQLSelectParserParser.RULE_in_value_list) {
			handleListItem(ruleIndex, parentRuleIndex);
		} else {
			// then parent is probably Rule_value_expression and this should
			// just be one child
			handleOneChild(ruleIndex);
		}
	}

	@Override
	public void exitSort_specifier_list(@NotNull SQLSelectParserParser.Sort_specifier_listContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		int parentRuleIndex = ctx.getParent().getRuleIndex();
		handleListList(ruleIndex, parentRuleIndex);
	}

	@Override
	public void exitSort_specifier(@NotNull SQLSelectParserParser.Sort_specifierContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		Integer stackLevel = currentStackLevel(ruleIndex);
		Map<String, Object> subMap = getNodeMap(ruleIndex, stackLevel);
		Object type = subMap.remove("Type");
		HashMap<String, Object> item = new HashMap<String, Object>();

		if (subMap.size() == 1) {
			item.put("column", subMap.remove("1"));
			item.put("sortOrder", "ASC");
			item.put("nullOrder", null);
			showTrace(parseTrace, "One Entry: " + item);

		} else if (subMap.size() == 2) {
			item.put("column", subMap.remove("1"));
			item.put("sortOrder", subMap.remove("2"));
			item.put("nullOrder", null);
			showTrace(parseTrace, "Two entries: " + item);

		} else if (subMap.size() == 3) {
			item.put("column", subMap.remove("1"));
			item.put("sortOrder", subMap.remove("2"));
			item.put("nullOrder", subMap.remove("3"));
			showTrace(parseTrace, "Three entries: " + item);

		} else {
			showTrace(parseTrace, "Too many entries: " + subMap);
		}

		subMap.put("1", item);
		showTrace(parseTrace, "Sort Item: " + subMap);

		int parentRuleIndex = ctx.getParent().getRuleIndex();
		handleListItem(ruleIndex, parentRuleIndex);

	}

	@Override
	public void exitGrouping_element_list(@NotNull SQLSelectParserParser.Grouping_element_listContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		int parentRuleIndex = ctx.getParent().getRuleIndex();
		handleListList(ruleIndex, parentRuleIndex);
	}

	@Override
	public void exitGrouping_element(@NotNull SQLSelectParserParser.Grouping_elementContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		int parentRuleIndex = ctx.getParent().getRuleIndex();
		handleListItem(ruleIndex, parentRuleIndex);
	}

	@Override
	public void exitOrdinary_grouping_set_list(@NotNull SQLSelectParserParser.Ordinary_grouping_set_listContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		handleOneChild(ruleIndex);
	}

	@Override
	public void exitOrdinary_grouping_set(@NotNull SQLSelectParserParser.Ordinary_grouping_setContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		int parentRuleIndex = ctx.getParent().getRuleIndex();
		if (parentRuleIndex == SQLSelectParserParser.RULE_grouping_element)
			handleOneChild(ruleIndex);
		else if (parentRuleIndex == SQLSelectParserParser.RULE_ordinary_grouping_set_list)
			handleListItem(ruleIndex, parentRuleIndex);
	}

	@Override
	public void exitRow_value_predicand(@NotNull SQLSelectParserParser.Row_value_predicandContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		handleOneChild(ruleIndex);
	}

	@Override
	public void exitGeneral_literal(@NotNull SQLSelectParserParser.General_literalContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		handleOneChild(ruleIndex);
	}

	@Override
	public void exitUnsigned_literal(@NotNull SQLSelectParserParser.Unsigned_literalContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		Integer stackLevel = currentStackLevel(ruleIndex);
		Map<String, Object> subMap = getNodeMap(ruleIndex, stackLevel);
		subMap.remove("Type");
		String[] keys = new String[1];
		keys = subMap.keySet().toArray(keys);

		if (keys.length == 1) {
			showTrace(parseTrace, "Just One Entry: " + subMap);
			Object item = subMap.remove(keys[0]);
			subMap.put("literal", item);
		} else {
			showTrace(parseTrace, "Too many entries: " + subMap);
		}
		showTrace(parseTrace, "Unsigned Literal: " + subMap);
	}

	@Override
	public void exitDatetime_literal(@NotNull SQLSelectParserParser.Datetime_literalContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		handleOneChild(ruleIndex);
	}

	@Override
	public void exitIdentifier(@NotNull SQLSelectParserParser.IdentifierContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		handleOneChild(ruleIndex);
	}

	@Override
	public void exitNull_literal(@NotNull SQLSelectParserParser.Null_literalContext ctx) {
		HashMap<String, Object> item = new HashMap<String, Object>();
		item.put("null_literal", "null");

		int parentRuleIndex = ctx.getParent().getRuleIndex();
		Integer parentStackLevel = currentStackLevel(parentRuleIndex);

		addToParent(parentRuleIndex, parentStackLevel, item);
	}

	@Override
	public void enterEveryRule(@NotNull ParserRuleContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		Integer stackLvl = pushStack(ruleIndex);

		if (ctx.getChildCount() == 1)
			if (ctx.getChild(0) instanceof TerminalNodeImpl) {
				// I'm a leaf
			} else {
				collectNewRuleMap(ruleIndex, stackLvl);
			}
		else {
			collectNewRuleMap(ruleIndex, stackLvl);
		}

		showTrace(parseTrace, "Enter " + makeMapIndex(ruleIndex, stackLvl) + ": "
				+ SQLSelectParserParser.ruleNames[ruleIndex] + ": " + collector);
		showTrace(parseTrace, "");
	}

	/**
	 * Create an empty ruleMap with ruleIndex and stackLvl key containing Type
	 * code
	 * 
	 * @param ruleIndex
	 * @param stackLvl
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private Map<String, Object> collectNewRuleMap(int ruleIndex, Integer stackLvl) {
		HashMap<String, Object> item = makeRuleMap(ruleIndex);
		return (Map<String, Object>) collect(ruleIndex, stackLvl, item);

	}

	/**
	 * Create new ruleMap with a ruleIndex Type
	 * 
	 * @param ruleIndex
	 * @return
	 */
	private HashMap<String, Object> makeRuleMap(int ruleIndex) {
		HashMap<String, Object> item = new HashMap<String, Object>();
		item.put("Type", ruleIndex);
		return item;
	}

	@Override
	public void exitEveryRule(@NotNull ParserRuleContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		Integer stackLevel = currentStackLevel(ruleIndex);
		Object item = null;

		Object skip = collector.remove("SKIP");
		if (skip == null) {
			if (ctx.getChildCount() == 1)
				if (ctx.getChild(0) instanceof TerminalNodeImpl) {
					// I'm a leaf
					item = ctx.getText();
				} else
					item = removeNode(ruleIndex, stackLevel);
			else
				item = removeNode(ruleIndex, stackLevel);

			// Add item to parent map
			if (ctx.getParent() != null) {
				int parentNodeIndex = ctx.getParent().getRuleIndex();
				Integer parentStackIndex = currentStackLevel(parentNodeIndex);
				if (ruleIndex == parentNodeIndex && stackLevel == parentStackIndex) {
					// oddity - in case it appears my parent is myself
					collect(ruleIndex, stackLevel, item);
				} else {
					Map<String, Object> idMap = getNodeMap(parentNodeIndex, parentStackIndex);
					if (idMap == null) {
						showTrace(parseTrace, "EXIT " + makeMapIndex(ruleIndex, stackLevel) + ": "
								+ SQLSelectParserParser.ruleNames[ruleIndex] + ": Missing pMap");
						showTrace(parseTrace, "");
					} else
						idMap.put(((Integer) (idMap.size())).toString(), item);
				}
			} else {
				showTrace(parseTrace, collector);
			}
		}

		popStack(ruleIndex);
		showTrace(parseTrace, "EXIT " + makeMapIndex(ruleIndex, stackLevel) + ": "
				+ SQLSelectParserParser.ruleNames[ruleIndex] + ": " + collector);
		showTrace(parseTrace, "");
	}

	@Override
	public void visitTerminal(@NotNull TerminalNode node) {
	}

	@Override
	public void visitErrorNode(@NotNull ErrorNode node) {
	}

}
