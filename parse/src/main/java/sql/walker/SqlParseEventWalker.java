package sql.walker;

import java.util.HashMap;

import java.util.HashSet;
import java.util.Map;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.antlr.v4.runtime.tree.TerminalNodeImpl;

import static mumble.sql.MumbleConstants.*;

import mumble.sql.Snippet;

import sql.SQLSelectParserParser;
import sql.SQLSelectParserParser.Cast_function_expressionContext;
import sql.SQLSelectParserParser.Static_data_typeContext;
import sql.SQLSelectParserBaseListener;
/**
 * Primary Listener Class; The class accepts events from the parse project's 
 * Base Parser Listener and creates a nested Hashmap Abstract Tree of the SQL
 * 
 * @author geoffreyhowe
 *
 */
public class SqlParseEventWalker extends SQLSelectParserBaseListener {

	final static Boolean showParse = false;
	final static Boolean showSymbols = false;
	final static Boolean showOther = false;
	final static Boolean showResults = true;

	final static Integer parseTrace = 1;
	final static Integer symbolTrace = 2;
	final static Integer otherTrace = 3;
	final static Integer resultTrace = 4;

	/**
	 * SQL Abstract Syntax Tree: This collects and constructs a nested Map data
	 * structure representing the entire SQL statement
	 */
	private HashMap<String, Object> sqlTree = new HashMap<String, Object>();

	/**
	 * Collect Root Table Column Dictionary
	 */
	private HashMap<String, Object> tableDictionaryMap = new HashMap<String, Object>();

	/**
	 * Collect Nested Symbol Table for the query
	 */
	private HashMap<String, Object> symbolTable = new HashMap<String, Object>();

	/**
	 * Collect Substitution Variable List
	 */
	private HashMap<String, Object> substitutionsMap = new HashMap<String, Object>();

	/**
	 * Depth of token stack
	 */
	private HashMap<Integer, Integer> stackTree = new HashMap<Integer, Integer>();

	/**
	 * Depth of token stack; this keeps track of recursive depth in the
	 * multi-stack dta structure, allowing correct indexing of clauses during
	 * the walking operation
	 */
	private HashMap<String, Integer> stackSymbols = new HashMap<String, Integer>();

	/**
	 * Number of query and subqueries encountered
	 */
	private Integer queryCount = 0;

	/**
	 * Number of predicands without aliases encountered
	 */
	private Integer predicandCount = 0;

	/**
	 * These variables keep track of syntax that can repeat in series so that
	 * the list can be managed as a whole
	 */
	private Boolean unionClauseFound = false;
	private Boolean firstUnionClause = false;
	private Boolean intersectClauseFound = false;
	private Boolean firstIntersectClause = false;
	private Boolean useAsLeaf = false;

	// Extra-Grammar Identifiers

	/**
	 * Symbol Swap Maps
	 */
	private HashMap<String, String> entityTableNameMap;
	private HashMap<String, Map<String, String>> attributeColumnMap;

	public void setEntityTableNameMap(HashMap<String, String> entityTableNameMap) {
		this.entityTableNameMap = entityTableNameMap;
	}

	public void setAttributeColumnMap(HashMap<String, Map<String, String>> attributeColumnMap) {
		this.attributeColumnMap = attributeColumnMap;
	}

	private String getTableName(String entityName) {
		return getLookupValue(entityTableNameMap, entityName);
	}

	public HashMap<String, Object> getSubstitutionsMap() {
		return substitutionsMap;
	}

	/**
	 * @param lkp
	 * @param lkpName
	 * @return
	 */
	private String getLookupValue(HashMap<String, String> lkp, String lkpName) {
		if (lkp == null)
			return lkpName;
		String hold = lkp.get(lkpName);
		if (hold == null)
			return lkpName;
		return hold;
	}

	// Constructors
	public SqlParseEventWalker() {
		super();
	}

	// Getters and Setters
	public static Boolean getShowparse() {
		return showParse;
	}

	public static Boolean getShowsymbols() {
		return showSymbols;
	}

	public static Boolean getShowother() {
		return showOther;
	}

	public static Boolean getShowresults() {
		return showResults;
	}

	public HashMap<String, Object> getSqlTree() {
		return sqlTree;
	}

	public HashMap<String, Object> getTableColumnMap() {
		return tableDictionaryMap;
	}

	public HashMap<String, Object> getSymbolTable() {
		return symbolTable;
	}

	@SuppressWarnings("unchecked")
	public HashSet<String> getInterface() {
		// TODO: When a query has a with, the interface can appear in anyone of
		// the symbol table queries, because it will be a list.
		HashSet<String> interfac = new HashSet<String>();
		HashMap<String, Object> hold = null;
		if (symbolTable != null) {
			for (String key : symbolTable.keySet()) {
				if (key.equals(PSS_WITH_KEY)) {
				} else {
					hold = (HashMap<String, Object>) symbolTable.get(key);
					break;
				}
			}
			if (hold != null) {
				hold = (HashMap<String, Object>) hold.get("interface");
			}
		}
		if (hold != null)
			for (String key : hold.keySet()) {
				interfac.add(key);
			}
		return interfac;
	}

	/**
	 * Emit a Snippet object with all of the parts of the SQL Parse Event Walker results related to the query
	*
	 * @return
	 */
	public Snippet getSnippet() {
		return new Snippet(sqlTree, tableDictionaryMap, symbolTable, substitutionsMap, getInterface());
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

	/**
	 * Multi-stack management operations for recursive clauses
	 */

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
		showTrace(otherTrace, "PUSH - " + symbolKey + ": " + symbols);
		return newLevel;
	}

	private Object popStack(String key) {
		Integer level = stackSymbols.get(key);
		String symbolKey = key + "_" + level;
		if (level == 1)
			stackSymbols.remove(key);
		else
			stackSymbols.put(key, level - 1);
		showTrace(otherTrace, "POP - " + symbolKey + ": " + stackSymbols);
		return sqlTree.remove(symbolKey);
	}

	private void pushSymbolTable() {
		Object symbols = symbolTable;
		if (symbols != null) {
			pushStack("symbolTable", symbols);
		}
		symbolTable = new HashMap<String, Object>();
	}

	/**
	 * @param key
	 * @param symbols
	 */
	@SuppressWarnings("unchecked")
	private void popSymbolTable(String key, HashMap<String, Object> symbols) {
		symbolTable = (HashMap<String, Object>) popStack("symbolTable");
		symbolTable.put(key, symbols);
	}

	@SuppressWarnings("unchecked")
	private void popSymbolTablePutAll(HashMap<String, Object> symbols) {
		symbolTable = (HashMap<String, Object>) popStack("symbolTable");
		symbolTable.putAll(symbols);
	}

	private Integer currentStackLevel(String key) {
		return stackSymbols.get(key);
	}

	/**
	 * Add level map to SQLTree AST by ruleIndex and stackLevel
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
		sqlTree.put(index, item);
	}

	/**
	 * SQLTree operations when re-writing the AST during the walk
	 */

	private Object getNode(int ruleIndex, Integer stackLevel) {
		String mapIdx = makeMapIndex(ruleIndex, stackLevel);
		return sqlTree.get(mapIdx);
	}

	private Object removeNode(int ruleIndex, Integer stackLevel) {
		String mapIdx = makeMapIndex(ruleIndex, stackLevel);
		return sqlTree.remove(mapIdx);
	}

	@SuppressWarnings("unchecked")
	private Map<String, Object> getNodeMap(int ruleIndex, Integer stackLevel) {
		String mapIdx = makeMapIndex(ruleIndex, stackLevel);
		Map<String, Object> idMap = (Map<String, Object>) sqlTree.get(mapIdx);
		return idMap;
	}

	@SuppressWarnings("unchecked")
	private Map<String, Object> removeNodeMap(int ruleIndex, Integer stackLevel) {
		String mapIdx = makeMapIndex(ruleIndex, stackLevel);
		return (Map<String, Object>) sqlTree.remove(mapIdx);
	}

	private String makeMapIndex(int ruleIndex, Integer stackIndex) {
		return ruleIndex + "_" + stackIndex;
	}


	// SUBSTITUTION VARIABLE HELPER METHODS
	
	/**
	 * If the sub tree is a substitution, add it to the substitution list and
	 * assign it the given substitution variable type
	 * 
	 * @param subMap
	 * @param type
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private Map<String, Object> checkForSubstitutionVariable(Map<String, Object> subMap, String type) {
		if (subMap.containsKey(PSS_SUBSTITUTION_KEY)) {
			Map<String, Object> hold = (Map<String, Object>) subMap.get(PSS_SUBSTITUTION_KEY);
			if (!hold.containsKey(PSS_TYPE_KEY)) {
				hold.put(PSS_TYPE_KEY, type);
				substitutionsMap.put((String) hold.get("name"), type);
			}
		}
		return subMap;
	}

	// Standard Actions: Construct Symbol Tables

	/**
	 * Put table aliases into Symbol Tree and move item list to the table
	 * reference
	 * 
	 * @param tableReference
	 * @param tableReference
	 */
	@SuppressWarnings("unchecked")
	private void collectSymbolTable(String alias, Object tableReference) {
		if (tableReference instanceof String) {
			Object aliasSet = symbolTable.get((String) alias);
			HashMap<String, Object> ref = (HashMap<String, Object>) symbolTable.get((String) tableReference);
			if (aliasSet == null) {
				if (!alias.equals((String) tableReference))
					symbolTable.put(alias, (String) tableReference);
				if (ref == null)
					symbolTable.put((String) tableReference, new HashMap<String, Object>());
			} else {
				if (!alias.equals((String) tableReference))
					symbolTable.put(alias, (String) tableReference);
				if (ref == null)
					symbolTable.put((String) tableReference, aliasSet);
				else
					ref.putAll((Map<String, Object>) aliasSet);
			}
		} else if (tableReference instanceof HashMap<?, ?>) {
			showTrace(symbolTrace, "Error collecting table: " + tableReference);
		}
	}

	/**
	 * Add the item subtree to the Symbol Table
	 * 
	 * @param tableReference
	 * @param token
	 */
	@SuppressWarnings("unchecked")
	private void collectSymbolTableItem(Object tableReference, Object item, Token token) {
		// TODO
		if (tableReference instanceof String) {
			Object localSymbolTable = symbolTable.get((String) tableReference);
			if (localSymbolTable == null) {
				// tableReference has not been added to Symbol Table before
				localSymbolTable = new HashMap<String, Object>();
				symbolTable.put((String) tableReference, localSymbolTable);
			}
			if (localSymbolTable instanceof String) {
				// tableReference is an ALIAS to a different table
				localSymbolTable = symbolTable.get((String) localSymbolTable);
				addItemToSymbolTable(localSymbolTable, item, token);
			} else if (localSymbolTable instanceof HashMap<?, ?>) {
				// tableReference is new entry for existing table in the symbol
				// table
				addItemToSymbolTable(localSymbolTable, item, token);
			}
		} else if (tableReference instanceof HashMap<?, ?>) {
			showTrace(symbolTrace, "Error collecting table: " + tableReference);
		}
	}

	/**
	 * Determines the type of the item subtree and adds it to the Symbol Table
	 * in the correct location
	 * 
	 * @param localSymbolTable
	 * @param item
	 * @param token
	 */
	@SuppressWarnings("unchecked")
	private void addItemToSymbolTable(Object localSymbolTable, Object item, Token token) {
		if (item instanceof String)
			// Item is a column reference
			((HashMap<String, Object>) localSymbolTable).put((String) item, token.toString());
		else {
			HashMap<String, Object> node = (HashMap<String, Object>) item;
			if (node.containsKey(PSS_SUBSTITUTION_KEY)) {

				node = (HashMap<String, Object>) node.get(PSS_SUBSTITUTION_KEY);
				if (node.get(PSS_TYPE_KEY).equals(PSS_COLUMN_KEY))
					// Item is a Column Substitution Variable
					((HashMap<String, Object>) localSymbolTable).put((String) node.get("name"),
							(HashMap<String, Object>) item);
				else
					// Item is a Predicate Substitution Variable
					((HashMap<String, Object>) localSymbolTable).putAll((HashMap<String, Object>) item);
			} else if (node.containsKey(PSS_COLUMN_KEY)) {
				// Item is a Column and should already be in the Symbol Table
			} else
				// Item is a subquery with its own Symbol Table
				((HashMap<String, Object>) localSymbolTable).put("subquery", item);
		}
	}

	/**
	 * Put the query interface into the Symbol Table
	 */
	private void captureQueryInterface() {
		String prefx = "query";
		HashMap<String, Object> interfac = getInterfaceFromQuery(prefx);
		if (interfac == null) {
			prefx = "insert";
			interfac = getInterfaceFromQuery(prefx);
		}
		if (interfac == null) {
			prefx = "update";
			interfac = getInterfaceFromQuery(prefx);
		}
		if (interfac == null) {
			prefx = PSS_UNION_KEY;
			interfac = getInterfaceFromQuery(prefx);
		}
		if (interfac == null) {
			prefx = PSS_INTERSECT_KEY;
			interfac = getInterfaceFromQuery(prefx);
		}
		if (interfac != null) {
			// need to get the interface from inside the query
			HashMap<String, Object> newif = new HashMap<String, Object>();
			for (String key : interfac.keySet()) {
				newif.put(key, prefx + "_column");
			}
			symbolTable.put("interface", newif);
		}
	}

	/**
	 * @param hdr
	 * @return
	 */
	private HashMap<String, Object> getInterfaceFromQuery(String hdr) {
		String queryName = hdr + (queryCount - 1);
		HashMap<String, Object> query = (HashMap<String, Object>) symbolTable.get(queryName);
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

	// Standard Actions: SQL

	/**
	 * Pops node that is a single child entry up one level of SQL Tree and
	 * removes stack references
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
	 * If the current node is a list, pull it up to the parent node in the SQL
	 * Tree as its value
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
		sqlTree.put("SKIP", "TRUE");
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
			sqlTree.put("SKIP", "TRUE");

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
		sqlTree.put("SKIP", "TRUE");
	}

	/*****************************************************************************************************
	 * Grammar Clauses Start Here
	 * 
	 * The following methods act as overrides on the default Walker Exit and Entry logic for each clause.
	 * 
	 */
	/*
	===============================================================================
	  Start Statements: SQL, Condition, Predicand and Literal
	===============================================================================
	*/

	// Parser End Points: These are independently callable and produce complete set of objects for each call. 

	@Override
	public void exitSql(@NotNull SQLSelectParserParser.SqlContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		Integer stackLevel = currentStackLevel(ruleIndex);
		Map<String, Object> subMap = removeNodeMap(ruleIndex, stackLevel);
		Object type = subMap.remove("Type");
		sqlTree.put(PSS_SQL_TREE_KEY, subMap.remove("1"));
		// showTrace(resultTrace, collector);
		showTrace(symbolTrace, symbolTable);
		showTrace(symbolTrace, tableDictionaryMap);
	}

	/*
	===============================================================================
	  Condition Start Symbol
	===============================================================================
	*/
	@Override
	public void exitCondition_value(SQLSelectParserParser.Condition_valueContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		Integer stackLevel = currentStackLevel(ruleIndex);
		Map<String, Object> subMap = removeNodeMap(ruleIndex, stackLevel);
		Object type = subMap.remove("Type");
		sqlTree.put(PSS_CONDITION_TREE_KEY, subMap.remove("1"));
		// showTrace(resultTrace, collector);
		showTrace(symbolTrace, symbolTable);

		// Add TABLE references to Table Dictionary
		HashMap<String, Object> hold = symbolTable;
		if (hold.size() > 0) {
			for (String tab_ref : hold.keySet()) {
				if ((tab_ref.startsWith("query")) || (tab_ref.startsWith(PSS_UNION_KEY))
						|| (tab_ref.startsWith(PSS_INTERSECT_KEY))) {
				} else {
					String reference;
					if (tab_ref.startsWith("<"))
						// Tuple Substitution Variable, do NOT alter case
						reference = tab_ref;
					else
						reference = tab_ref.toLowerCase();
					HashMap<String, Object> currItem = (HashMap<String, Object>) tableDictionaryMap.get(reference);
					if (currItem != null)
						currItem.putAll((Map<? extends String, ? extends Object>) hold.get(tab_ref));
					else {
						HashMap<String, Object> newItem = new HashMap<String, Object>();
						newItem.putAll((Map<? extends String, ? extends Object>) hold.get(tab_ref));
						tableDictionaryMap.put(reference, newItem);
					}
				}
			}
		}

		showTrace(symbolTrace, tableDictionaryMap);
	}

	  
	/*
	===============================================================================
	  Predicand Start Symbol
	===============================================================================
	*/
	@Override
	public void exitPredicand_value(@NotNull SQLSelectParserParser.Predicand_valueContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		Integer stackLevel = currentStackLevel(ruleIndex);
		Map<String, Object> subMap = removeNodeMap(ruleIndex, stackLevel);
		Object type = subMap.remove("Type");
		sqlTree.put(PSS_PREDICAND_TREE_KEY, subMap.remove("1"));
		// showTrace(resultTrace, collector);
		showTrace(symbolTrace, symbolTable);

		// Add TABLE references to Table Dictionary
		HashMap<String, Object> hold = symbolTable;
		if (hold.size() > 0) {
			for (String tab_ref : hold.keySet()) {
				if ((tab_ref.startsWith("query")) || (tab_ref.startsWith(PSS_UNION_KEY))
						|| (tab_ref.startsWith(PSS_INTERSECT_KEY))) {
				} else {
					String reference;
					if (tab_ref.startsWith("<"))
						// Tuple Substitution Variable, do NOT alter case
						reference = tab_ref;
					else
						reference = tab_ref.toLowerCase();
					HashMap<String, Object> currItem = (HashMap<String, Object>) tableDictionaryMap.get(reference);
					if (currItem != null)
						currItem.putAll((Map<? extends String, ? extends Object>) hold.get(tab_ref));
					else {
						HashMap<String, Object> newItem = new HashMap<String, Object>();
						newItem.putAll((Map<? extends String, ? extends Object>) hold.get(tab_ref));
						tableDictionaryMap.put(reference, newItem);
					}
				}
			}
		}

		showTrace(symbolTrace, tableDictionaryMap);
	}
	
	/*
	===============================================================================
	  Literal Value Start Symbol
	===============================================================================
	*/

	// TODO: Add to AST
//	@Override
//	public void exitLiteral_value(@NotNull SQLSelectParserParser.Literal_valueContext ctx) {
//	}
	
	// End of Grammar End Points
	 
	/*
	===============================================================================
	  Dependent Grammar Rules
	===============================================================================
	*/
	/*
	===============================================================================
	  WITH Statement <with query>
	===============================================================================
	*/
	@SuppressWarnings("unchecked")
	@Override
	public void exitWith_query(@NotNull SQLSelectParserParser.With_queryContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		int parentRuleIndex = ctx.getParent().getRuleIndex();

		Integer stackLevel = currentStackLevel(ruleIndex);
		Integer parentStackLevel = currentStackLevel(parentRuleIndex);

		Map<String, Object> subMap = removeNodeMap(ruleIndex, stackLevel);
		Object type = subMap.remove("Type");
		Map<String, Object> item;

		if (subMap.size() == 1) {
			// just a query by itself
			subMap = (Map<String, Object>) subMap.remove("1");

		} else if (subMap.size() == 2) {
			// A With Query
			Map<String, Object> withList = (Map<String, Object>) subMap.remove("1");
			Map<String, Object> query = (Map<String, Object>) subMap.remove("2");

			subMap.put(PSS_WITH_KEY, withList);
			subMap.put(PSS_QUERY_KEY, query);
		} else {
			showTrace(parseTrace, "Wrong number of entries: " + ctx.getText());
		}
		addToParent(parentRuleIndex, parentStackLevel, subMap);
		showTrace(parseTrace, "WITH QUERY: " + subMap);
	}

	@Override
	public void exitWith_clause(@NotNull SQLSelectParserParser.With_clauseContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		Integer stackLevel = currentStackLevel(ruleIndex);

		Map<String, Object> subMap = removeNodeMap(ruleIndex, stackLevel);
		Object type = subMap.remove("Type");

		Map<String, Object> newMap = collectNewRuleMap(ruleIndex, stackLevel);
		type = newMap.remove("Type");

		String[] keys = new String[1];
		keys = subMap.keySet().toArray(keys);

		for (String key : keys) {
			newMap.putAll((Map<String, Object>) subMap.remove(key));
		}

		showTrace(parseTrace, "WITH CLAUSE: " + newMap);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void exitWith_list_item(@NotNull SQLSelectParserParser.With_list_itemContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		int parentRuleIndex = ctx.getParent().getRuleIndex();

		Integer stackLevel = currentStackLevel(ruleIndex);
		Integer parentStackLevel = currentStackLevel(parentRuleIndex);

		Map<String, Object> subMap = removeNodeMap(ruleIndex, stackLevel);
		Object type = subMap.remove("Type");
		Map<String, Object> item;
		String alias = null;

		if (subMap.size() == 2) {

			alias = (String) subMap.remove("1");

			Map<String, Object> aliasMap = (Map<String, Object>) subMap.remove("2");

			subMap.put(alias, aliasMap);
			// Add to symbol tree WITH subclause
			if (symbolTable.containsKey(PSS_WITH_KEY)) {
				Map<String, Object> with = (Map<String, Object>) symbolTable.remove(PSS_WITH_KEY);
				with.put(alias, symbolTable);
				symbolTable = new HashMap<String, Object>();
				symbolTable.put(PSS_WITH_KEY, with);
			} else {
				Map<String, Object> with = new HashMap<String, Object>();
				with.put(alias, symbolTable);
				symbolTable = new HashMap<String, Object>();
				symbolTable.put(PSS_WITH_KEY, with);
			}
		} else {
			showTrace(parseTrace, "Wrong number of entries: " + ctx.getText());
		}
		addToParent(parentRuleIndex, parentStackLevel, subMap);
		showTrace(parseTrace, "WITH QUERY: " + subMap);
	}

	@Override
	public void exitQuery_alias(@NotNull SQLSelectParserParser.Query_aliasContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		handleOneChild(ruleIndex);
	}

	@Override
	public void exitQuery(@NotNull SQLSelectParserParser.QueryContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		handleOneChild(ruleIndex);
	}


/*
===============================================================================
  INSERT Statement <insert expression>
===============================================================================
*/

	@Override
	public void exitInsert_expression(@NotNull SQLSelectParserParser.Insert_expressionContext ctx) {
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
				if (childKey == null) {
					if (value.containsKey(PSS_TABLE_KEY)) {
						subMap.put("insert", value);
					} else {
						String nk = "query" + queryCount;
						subMap.put(nk, value);
						queryCount++;
					}
				} else {
					Object segment = value.remove(childKey.toString());
					if (childKey == (Integer) SQLSelectParserParser.RULE_column_reference_list) {
						subMap.put("into", value);
					} else if (childKey == (Integer) SQLSelectParserParser.RULE_returning) {
						subMap.put(PSS_RETURNING_KEY, segment);
					} else {
						showTrace(parseTrace, "Too Many Entries" + segment);
					}
				}
			}
		}
		showTrace(parseTrace, subMap);

		// Handle symbol tables
		HashMap<String, Object> symbols = symbolTable;

		// Special handling of queries with only one source: Move "unknown"
		// references to that table
		HashMap<String, Object> unks = (HashMap<String, Object>) symbols.remove("unknown");

		Integer count = 0;
		Integer tableCount = 0;
		String onlyTableName = null;
		HashMap<String, Object> hold = new HashMap<String, Object>();
		String holdTabRef = null;

		for (String tab_ref : symbols.keySet()) {
			if ((tab_ref.equals("interface")) || (tab_ref.startsWith("def_query")) || (tab_ref.startsWith("def_insert"))
					|| (tab_ref.startsWith("def_update")) || (tab_ref.startsWith("def_union"))
					|| (tab_ref.startsWith("def_intersect"))) {
			} else {
				Object item = symbols.get(tab_ref);
				if (item instanceof HashMap<?, ?>) {
					hold.put(tab_ref, item);
					holdTabRef = tab_ref;
					count++;
					if ((tab_ref.startsWith("query")) || (tab_ref.startsWith("insert"))
							|| (tab_ref.startsWith("update")) || (tab_ref.startsWith(PSS_UNION_KEY))
							|| (tab_ref.startsWith(PSS_INTERSECT_KEY))) {
					} else {
						tableCount++;
						onlyTableName = tab_ref;
					}
				}
			}
		}
		if (unks != null) {

			if (count == 1) {
				// just one table referenced, put all unknowns into it
				((HashMap<String, Object>) hold.get(holdTabRef)).putAll(unks);
			} else {
				// Allocate Unknowns
				for (String tab_ref : hold.keySet()) {
					HashMap<String, Object> currItem = (HashMap<String, Object>) hold.get(tab_ref);
					for (String key : currItem.keySet()) {
						unks.remove(key);
					}
				}
				// put whatever is left back into the unknowns
				if (unks.size() > 0) {
					if (tableCount == 1)
						// just one table remains referenced, put all unknowns
						// into it
						((HashMap<String, Object>) hold.get(onlyTableName)).putAll(unks);
					else
						symbols.put("unknown", unks);
				}
			}
		}
		// TODO: Add TABLE references to Table Dictionary
		if (hold.size() > 0) {
			for (String tab_ref : hold.keySet()) {
				if ((tab_ref.startsWith("query")) || (tab_ref.startsWith("insert")) || (tab_ref.startsWith("update"))
						|| (tab_ref.startsWith(PSS_UNION_KEY)) || (tab_ref.startsWith(PSS_INTERSECT_KEY))) {
				} else {
					String reference;
					if (tab_ref.startsWith("<"))
						// Tuple Substitution Variable, do NOT alter case
						reference = tab_ref;
					else
						reference = tab_ref.toLowerCase();
					HashMap<String, Object> currItem = (HashMap<String, Object>) tableDictionaryMap.get(reference);
					if (currItem != null)
						currItem.putAll((Map<? extends String, ? extends Object>) hold.get(tab_ref));
					else {
						HashMap<String, Object> newItem = new HashMap<String, Object>();
						newItem.putAll((Map<? extends String, ? extends Object>) hold.get(tab_ref));
						tableDictionaryMap.put(reference, newItem);
					}
				}
			}
		}
	}
	  
	/*
	===============================================================================
	  UPDATE Statement <update expression>
	===============================================================================
	*/

	@Override
	public void exitUpdate_expression(@NotNull SQLSelectParserParser.Update_expressionContext ctx) {
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
				if (childKey == null) {
					String k2 = "update" + queryCount;
					queryCount++;
					subMap.put(k2, value);
				} else {
					Object segment = value.remove(childKey.toString());
					if (childKey == (Integer) SQLSelectParserParser.RULE_assignment_expression_list) {
						subMap.put(PSS_ASSIGNMENTS_KEY, segment);
					} else if (childKey == (Integer) SQLSelectParserParser.RULE_from_clause) {
						if (((HashMap<String, Object>) segment).size() == 1) {
							subMap.put(PSS_FROM_KEY, ((HashMap<String, Object>) segment).remove("1"));
						} else
							subMap.put(PSS_FROM_KEY, segment);
					} else if (childKey == (Integer) SQLSelectParserParser.RULE_where_clause) {
						HashMap<String, Object> item = (HashMap<String, Object>) segment;
						item = (HashMap<String, Object>) item.remove("1");
						subMap.put(PSS_WHERE_KEY, item);
					} else if (childKey == (Integer) SQLSelectParserParser.RULE_returning) {
						subMap.put(PSS_RETURNING_KEY, segment);
					} else {
						showTrace(parseTrace, "Too Many Entries" + segment);
					}
				}
			}
		}
		showTrace(parseTrace, subMap);

		// Handle symbol tables
		HashMap<String, Object> symbols = symbolTable;

		// Special handling of queries with only one source: Move "unknown"
		// references to that table
		HashMap<String, Object> unks = (HashMap<String, Object>) symbols.remove("unknown");

		Integer count = 0;
		Integer tableCount = 0;
		String onlyTableName = null;
		HashMap<String, Object> hold = new HashMap<String, Object>();
		String holdTabRef = null;

		for (String tab_ref : symbols.keySet()) {
			if ((tab_ref.equals("interface")) || (tab_ref.startsWith("def_query")) || (tab_ref.startsWith("def_union"))
					|| (tab_ref.startsWith("def_intersect"))) {
			} else {
				Object item = symbols.get(tab_ref);
				if (item instanceof HashMap<?, ?>) {
					hold.put(tab_ref, item);
					holdTabRef = tab_ref;
					count++;
					if ((tab_ref.startsWith("query")) || (tab_ref.startsWith(PSS_UNION_KEY))
							|| (tab_ref.startsWith(PSS_INTERSECT_KEY))) {
					} else {
						tableCount++;
						onlyTableName = tab_ref;
					}
				}
			}
		}
		if (unks != null) {

			if (count == 1) {
				// just one table referenced, put all unknowns into it
				((HashMap<String, Object>) hold.get(holdTabRef)).putAll(unks);
			} else {
				// Allocate Unknowns
				for (String tab_ref : hold.keySet()) {
					HashMap<String, Object> currItem = (HashMap<String, Object>) hold.get(tab_ref);
					for (String key : currItem.keySet()) {
						unks.remove(key);
					}
				}
				// put whatever is left back into the unknowns
				if (unks.size() > 0) {
					if (tableCount == 1)
						// just one table remains referenced, put all unknowns
						// into it
						((HashMap<String, Object>) hold.get(onlyTableName)).putAll(unks);
					else
						symbols.put("unknown", unks);
				}
			}
		}
		// TODO: Add TABLE references to Table Dictionary
		if (hold.size() > 0) {
			for (String tab_ref : hold.keySet()) {
				if ((tab_ref.startsWith("query")) || (tab_ref.startsWith(PSS_UNION_KEY))
						|| (tab_ref.startsWith(PSS_INTERSECT_KEY))) {
				} else {
					String reference;
					if (tab_ref.startsWith("<"))
						// Tuple Substitution Variable, do NOT alter case
						reference = tab_ref;
					else
						reference = tab_ref.toLowerCase();
					HashMap<String, Object> currItem = (HashMap<String, Object>) tableDictionaryMap.get(reference);
					if (currItem != null)
						currItem.putAll((Map<? extends String, ? extends Object>) hold.get(tab_ref));
					else {
						HashMap<String, Object> newItem = new HashMap<String, Object>();
						newItem.putAll((Map<? extends String, ? extends Object>) hold.get(tab_ref));
						tableDictionaryMap.put(reference, newItem);
					}
				}
			}
		}
	}

	// TODO: Add to AST
//	@Override
//	public void exitReturning(@NotNull SQLSelectParserParser.ReturningContext ctx) {
//	}
	
	@Override
	public void exitAssignment_expression_list(@NotNull SQLSelectParserParser.Assignment_expression_listContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		int parentRuleIndex = ctx.getParent().getRuleIndex();
		if (parentRuleIndex == (Integer) SQLSelectParserParser.RULE_partition_by_clause) {
			handleListList(ruleIndex, parentRuleIndex);
		} else {
			// then parent is normal query
			handlePushDown(ruleIndex);
		}
	}

	@Override
	public void exitAssignment_expression(@NotNull SQLSelectParserParser.Assignment_expressionContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		Integer stackLevel = currentStackLevel(ruleIndex);
		int parentRuleIndex = ctx.getParent().getRuleIndex();
		Integer parentStackLevel = currentStackLevel(parentRuleIndex);

		Map<String, Object> subMap = removeNodeMap(ruleIndex, stackLevel);
		Object type = subMap.remove("Type");

		if (subMap.size() == 2) {
			showTrace(parseTrace, "Comparison: " + subMap);
			Map<String, Object> left = (Map<String, Object>) subMap.remove("1");
			subMap.put(PSS_SET_KEY, left);

			Map<String, Object> right = (Map<String, Object>) subMap.remove("2");
			subMap.put(PSS_TO_KEY, right);

			showTrace(parseTrace, "Assignment: " + subMap);

			// Put target column symbol into update table's set and interface
			Map<String, Object> unk = (HashMap<String, Object>) symbolTable.get("unknown");
			String column = ((HashMap<String, String>) ((HashMap<String, Object>) left).get(PSS_COLUMN_KEY)).get("name");

			String[] keys = new String[1];
			keys = symbolTable.keySet().toArray(keys);

			for (String key : keys) {
				if (key.equals("unknown")) { // do nothing

				} else if (key.equals(PSS_WITH_KEY)) { // do nothing

				} else {
					// must be the table
					showTrace(symbolTrace, "Key for not 'UNKNOWN': " + key + " Entry: " + symbolTable.get(key));
					Object item = symbolTable.get(key);
					if (item instanceof Map<?, ?>) {
						HashMap<String, Object> map = (HashMap<String, Object>) item;
						map.put(column, unk.get(column));
					}
				}
			}
		} else {
			showTrace(parseTrace, "Wrong number of entries: " + subMap);
		}
		addToParent(parentRuleIndex, parentStackLevel, subMap);

	}

	/*
	===============================================================================
	  CREATE TABLE
	===============================================================================
	*/

	// TODO: Complete Logic
	// @Override
	// public void exitCreate_table_as_expression_list(@NotNull
	// SQLSelectParserParser.Create_table_as_expression_listContext ctx) {
	// int ruleIndex = ctx.getRuleIndex();
	// handleOneChild(ruleIndex);
	// }

	/*
	===============================================================================
	  QUERY EXPRESSION
	===============================================================================
	*/
	// Nested, structured query construction that preserves precedence order:  Intersect then Union

	@Override
	public void exitQuery_expression(SQLSelectParserParser.Query_expressionContext ctx) {
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

		handleOperandList(ruleIndex, PSS_INTERSECT_KEY);

		// Handle symbol tables
		HashMap<String, Object> symbols = symbolTable;

		if (intersectClauseFound) {
			// Retrieve outer symbol table, insert this symbol table into it
			String key = PSS_INTERSECT_KEY + queryCount;
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
			item.put(PSS_OPERATOR_KEY, subMap.remove("1"));
			item.put(PSS_QUALIFIER_KEY, null);
			subMap.put(PSS_INTERSECT_KEY, item);
		} else if (ctx.getChildCount() == 2) {
			Map<String, Object> item = new HashMap<String, Object>();
			item.put(PSS_OPERATOR_KEY, subMap.remove("1"));
			item.put(PSS_QUALIFIER_KEY, subMap.remove("2"));
			subMap.put(PSS_INTERSECT_KEY, item);
		} else {
			showTrace(parseTrace, "Wrong number of entries: " + ctx.getText());
		}
		showTrace(parseTrace, "Intersect Operator: " + subMap);

		// Get first interface to represent intersection output
		if (firstIntersectClause) {
			showTrace(symbolTrace, "Intersect So Far: " + symbolTable);
			captureQueryInterface();
			showTrace(symbolTrace, "Intersect So Far: " + symbolTable);

		}
	}

	// Intersect_operator does not need its own logic
	
	@Override
	public void enterUnionized_query(@NotNull SQLSelectParserParser.Unionized_queryContext ctx) {
		pushSymbolTable();
	}

	@Override
	public void exitUnionized_query(@NotNull SQLSelectParserParser.Unionized_queryContext ctx) {
		int ruleIndex = ctx.getRuleIndex();

		handleOperandList(ruleIndex, PSS_UNION_KEY);

		// Handle symbol tables
		HashMap<String, Object> symbols = symbolTable;

		if (unionClauseFound) {
			// Retrieve outer symbol table, insert this symbol table into it
			String key = PSS_UNION_KEY + queryCount;
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
			item.put(PSS_OPERATOR_KEY, subMap.remove("1"));
			item.put(PSS_QUALIFIER_KEY, null);
			subMap.put(PSS_UNION_KEY, item);
		} else if (ctx.getChildCount() == 2) {
			Map<String, Object> item = new HashMap<String, Object>();
			item.put(PSS_OPERATOR_KEY, subMap.remove("1"));
			item.put(PSS_QUALIFIER_KEY, subMap.remove("2"));
			subMap.put(PSS_UNION_KEY, item);
		} else {
			showTrace(parseTrace, "Wrong number of entries: " + ctx.getText());
		}
		showTrace(parseTrace, "Union Operator: " + subMap);

		// Get first interface to represent union output
		if (firstUnionClause) {
			showTrace(symbolTrace, "Union So Far: " + symbolTable);
			captureQueryInterface();
			showTrace(symbolTrace, "Union So Far: " + symbolTable);
		}

	}
	
	// Union_operator does NOT need its own method
	

/*
===============================================================================
  SELECT Statement <query primary>
===============================================================================
*/


	@Override
	public void exitQuery_primary(@NotNull SQLSelectParserParser.Query_primaryContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		Integer stackLevel = currentStackLevel(ruleIndex);
		int parentRuleIndex = ctx.getParent().getRuleIndex();

		Map<String, Object> subMap = getNodeMap(ruleIndex, stackLevel);
		checkForSubstitutionVariable((Map<String, Object>) subMap.get("1"), "query");

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

			item.put(PSS_LOOKUP_KEY, subMap.remove("1"));

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
					subMap.put(PSS_SELECT_KEY, segment);
				} else if (childKey == (Integer) SQLSelectParserParser.RULE_set_qualifier) {
					subMap.put(PSS_QUALIFIER_KEY, segment);
				} else if (childKey == (Integer) SQLSelectParserParser.RULE_from_clause) {
					if (((HashMap<String, Object>) segment).size() == 1) {
						subMap.put(PSS_FROM_KEY, ((HashMap<String, Object>) segment).remove("1"));
					} else
						subMap.put(PSS_FROM_KEY, segment);
				} else if (childKey == (Integer) SQLSelectParserParser.RULE_where_clause) {
					HashMap<String, Object> item = (HashMap<String, Object>) segment;
					item = (HashMap<String, Object>) item.remove("1");
					subMap.put(PSS_WHERE_KEY, item);
				} else if (childKey == (Integer) SQLSelectParserParser.RULE_groupby_clause) {
					subMap.put(PSS_GROUPBY_KEY, segment);
				} else if (childKey == (Integer) SQLSelectParserParser.RULE_having_clause) {
					HashMap<String, Object> item = (HashMap<String, Object>) segment;
					item = (HashMap<String, Object>) item.remove("1");
					subMap.put(PSS_HAVING_KEY, item);
				} else if (childKey == (Integer) SQLSelectParserParser.RULE_orderby_clause) {
					subMap.put(PSS_ORDERBY_KEY, segment);
				} else if (childKey == (Integer) SQLSelectParserParser.RULE_limit_clause) {
					HashMap<String, Object>  hold = (HashMap<String, Object>) ((HashMap<String, Object>) segment).remove("1");
					subMap.put(PSS_LIMIT_KEY, hold);
				} else {
					showTrace(parseTrace, "Too Many Entries" + segment);
				}
			}
		}
		showTrace(parseTrace, subMap);

		// Handle symbol tables
		HashMap<String, Object> symbols = symbolTable;

		// Special handling of queries with only one source: Move "unknown"
		// references to that table
		HashMap<String, Object> unks = (HashMap<String, Object>) symbols.remove("unknown");

		Integer count = 0;
		Integer tableCount = 0;
		String onlyTableName = null;
		HashMap<String, Object> hold = new HashMap<String, Object>();
		String holdTabRef = null;

		for (String tab_ref : symbols.keySet()) {
			if ((tab_ref.equals("interface")) || (tab_ref.startsWith("def_query")) || (tab_ref.startsWith("def_insert"))
					|| (tab_ref.startsWith("def_update")) || (tab_ref.startsWith("def_union"))
					|| (tab_ref.startsWith("def_intersect"))) {
			} else {
				Object item = symbols.get(tab_ref);
				if (item instanceof HashMap<?, ?>) {
					hold.put(tab_ref, item);
					holdTabRef = tab_ref;
					count++;
					if ((tab_ref.startsWith("query")) || (tab_ref.startsWith("insert"))
							|| (tab_ref.startsWith("update")) || (tab_ref.startsWith(PSS_UNION_KEY))
							|| (tab_ref.startsWith(PSS_INTERSECT_KEY))) {
					} else {
						tableCount++;
						onlyTableName = tab_ref;
					}
				}
			}
		}
		if (unks != null) {

			if (count == 1) {
				// just one table referenced, put all unknowns into it
				((HashMap<String, Object>) hold.get(holdTabRef)).putAll(unks);
			} else {
				// Allocate Unknowns
				for (String tab_ref : hold.keySet()) {
					HashMap<String, Object> currItem = (HashMap<String, Object>) hold.get(tab_ref);
					for (String key : currItem.keySet()) {
						unks.remove(key);
					}
				}
				// put whatever is left back into the unknowns
				if (unks.size() > 0) {
					if (tableCount == 1)
						// just one table remains referenced, put all unknowns
						// into it
						((HashMap<String, Object>) hold.get(onlyTableName)).putAll(unks);
					else
						symbols.put("unknown", unks);
				}
			}
		}
		// TODO: Add TABLE references to Table Dictionary
		if (hold.size() > 0) {
			for (String tab_ref : hold.keySet()) {
				if ((tab_ref.startsWith("query")) || (tab_ref.startsWith(PSS_UNION_KEY))
						|| (tab_ref.startsWith(PSS_INTERSECT_KEY))) {
				} else {
					String reference;
					if (tab_ref.startsWith("<"))
						// Tuple Substitution Variable, do NOT alter case
						reference = tab_ref;
					else
						reference = tab_ref.toLowerCase();
					HashMap<String, Object> currItem = (HashMap<String, Object>) tableDictionaryMap.get(reference);
					if (currItem != null)
						currItem.putAll((Map<? extends String, ? extends Object>) hold.get(tab_ref));
					else {
						HashMap<String, Object> newItem = new HashMap<String, Object>();
						newItem.putAll((Map<? extends String, ? extends Object>) hold.get(tab_ref));
						tableDictionaryMap.put(reference, newItem);
					}
				}
			}
		}
		// Retrieve outer symbol table, insert this symbol table into it
		String key = "query" + queryCount;
		popSymbolTable(key, symbols);
		queryCount++;
	}

/*
===============================================================================
  SELECT Details
===============================================================================
*/
 // TODO: Select Into Table syntax has not been implemented
	
// set_qualifier does NOT need its own method
	

	@Override
	public void exitSelect_list(@NotNull SQLSelectParserParser.Select_listContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		int parentRuleIndex = ctx.getParent().getRuleIndex();
		// then parent is normal query
		handlePushDown(ruleIndex);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void exitSelect_item(@NotNull SQLSelectParserParser.Select_itemContext ctx) {
		// TODO
		int ruleIndex = ctx.getRuleIndex();
		int parentRuleIndex = ctx.getParent().getRuleIndex();

		Integer stackLevel = currentStackLevel(ruleIndex);
		Integer parentStackLevel = currentStackLevel(parentRuleIndex);

		Map<String, Object> subMap = removeNodeMap(ruleIndex, stackLevel);
		subMap.remove("Type");
		Map<String, Object> item;

		// variables for constructing the Symbol Table Interface
		String interfaceAlias = null;
		HashMap<String, Object> interfaceReference = new HashMap<String, Object>();

		// Get first item, record if it is a Substitution Variable by adding the
		// Substitution List
		item = checkForSubstitutionVariable((Map<String, Object>) subMap.remove("1"), "predicand");

		interfaceReference.putAll(item);

		if (subMap.size() == 0) {
			// Select Item did not have an Alias, construct one from options
			showTrace(parseTrace, "Just One Item: " + item);
			HashMap<String, Object> node = (HashMap<String, Object>) item.get(PSS_COLUMN_KEY);
			if (node == null)
				node = (HashMap<String, Object>) item.get(PSS_SUBSTITUTION_KEY);
			if (node != null)
				if (node.containsKey(PSS_NAME_KEY))
					// Select Item is a column or substitution, use its name
					interfaceAlias = (String) node.get(PSS_NAME_KEY);
				else if (node.containsKey(PSS_SUBSTITUTION_KEY))
					// then Select Item is a COLUMN Substitution Variable, get
					// the variable's name
					interfaceAlias = (String) ((HashMap<String, Object>) node.get(PSS_SUBSTITUTION_KEY)).get("name");
			if (interfaceAlias == null) {
				// Select Item is a PREDICAND without a name, generate the next
				// placeholder for the interface
				interfaceAlias = "unnamed_" + predicandCount;
				predicandCount++;
			}

		} else {
			// Select Item has an alias
			showTrace(parseTrace, "Item and Alias: " + item);

			Map<String, Object> aliasMap = (Map<String, Object>) subMap.remove("2");
			interfaceAlias = (String) aliasMap.get(PSS_ALIAS_KEY);
			((Map<String, Object>) item).putAll(aliasMap);
		}
		addToParent(parentRuleIndex, parentStackLevel, item);
		showTrace(parseTrace, "SELECT ITEM: " + item);

		// Add item to symbol table
		HashMap<String, Object> selectInterface = (HashMap<String, Object>) symbolTable.get("interface");
		if (selectInterface == null) {
			selectInterface = new HashMap<String, Object>();
			symbolTable.put("interface", selectInterface);
		}
		selectInterface.put(interfaceAlias, interfaceReference);
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
			subMap.put(PSS_ALIAS_KEY, alias);
			showTrace(parseTrace, "Alias: " + alias + " Map: " + subMap);
		} else {
			showTrace(parseTrace, "Too many entries: " + subMap);
		}

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
			item.put(PSS_TABLE_REF_KEY, "*");
			item.put(PSS_NAME_KEY, "*");

			collectSymbolTableItem("unknown", "*", ctx.getStart());

			subMap.put(PSS_COLUMN_KEY, item);
		} else if (ctx.getChildCount() == 3) {
			showTrace(parseTrace, "Three entries: " + ctx.getText());
			item.put(PSS_TABLE_REF_KEY, ctx.getChild(0).getText());

			collectSymbolTableItem(item.get(PSS_TABLE_REF_KEY), "*", ctx.getStart());

			item.put(PSS_NAME_KEY, "*");
			subMap.put(PSS_COLUMN_KEY, item);
		}
		// Add item to parent map
		addToParent(parentRuleIndex, parentStackLevel, subMap);
		showTrace(parseTrace, "Table Alias . Column Name: " + subMap);
	}

/*
===============================================================================
  FROM Statement <from clause>
===============================================================================
*/

	@Override
	public void exitFrom_clause(@NotNull SQLSelectParserParser.From_clauseContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		int stackLevel = currentStackLevel(ruleIndex);
		int parentRuleIndex = ctx.getParent().getRuleIndex();
		Map<String, Object> subMap = getNodeMap(ruleIndex, stackLevel);
		if (subMap.size() == 3) {
			// handle from clause with extension
			subMap.putAll((Map<String, Object>) subMap.remove("1"));
			if (subMap.containsKey("2"))
				subMap.put(PSS_JOIN_EXTENSION_KEY, subMap.remove("2"));
		}
		handlePushDown(ruleIndex);
	}
	
	// RULE_join_extension

	@Override
	public void exitJoin_extension(@NotNull SQLSelectParserParser.Join_extensionContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		int stackLevel = currentStackLevel(ruleIndex);
		int parentRuleIndex = ctx.getParent().getRuleIndex();
		Map<String, Object> subMap = getNodeMap(ruleIndex, stackLevel);
		Map<String, Object> reference = checkForSubstitutionVariable((Map<String, Object>) subMap.get("1"),
				"join_extension");

		handleOneChild(ruleIndex);
	}

	@Override
	public void exitTable_reference_list(@NotNull SQLSelectParserParser.Table_reference_listContext ctx) {
		int ruleIndex = ctx.getRuleIndex();

		handleOperandList(ruleIndex, PSS_JOIN_KEY);
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
			item.put(PSS_ALIAS_KEY, null);

			Object table = item.get(PSS_TABLE_KEY);
			if (table != null) {
				alias = table.toString();
				collectSymbolTable(alias, table);

				subMap.put(PSS_TABLE_KEY, item);
			} else {
				alias = "unnamed";
				Map<String, Object> aliasMap = new HashMap<String, Object>();
				aliasMap.put(alias, alias);
				Boolean done = collectQuerySymbolTable(PSS_QUERY_KEY, aliasMap, alias, item);
				if (!done)
					done = collectQuerySymbolTable("insert", aliasMap, alias, item);
				if (!done)
					done = collectQuerySymbolTable("update", aliasMap, alias, item);
				if (!done)
					done = collectQuerySymbolTable(PSS_UNION_KEY, aliasMap, alias, item);
				if (!done)
					done = collectQuerySymbolTable(PSS_INTERSECT_KEY, aliasMap, alias, item);

			}

		} else if (ctx.getChildCount() == 2) {
			item = new HashMap<String, Object>();
			Map<String, Object> reference = checkForSubstitutionVariable((Map<String, Object>) subMap.remove("1"),
					"tuple");

			Map<String, Object> aliasMap = (Map<String, Object>) subMap.remove("2");
			alias = (String) aliasMap.get(PSS_ALIAS_KEY);
			item.putAll(aliasMap);

			// Try various alternatives
			if (reference.containsKey(PSS_TABLE_KEY)) {
				Object table = reference.get(PSS_TABLE_KEY);
				item.putAll(reference);
				collectSymbolTable(alias, table);
			} else if (reference.containsKey(PSS_SUBSTITUTION_KEY)) {
				// Check for Substitution Variable
				item.putAll(reference);
				// Collect Symbol Table Reference
				Map<String, Object> substitution = (Map<String, Object>) reference.get(PSS_SUBSTITUTION_KEY);
				collectSymbolTable(alias, substitution.get("name"));

			} else {
				Boolean done = collectQuerySymbolTable(PSS_QUERY_KEY, item, alias, reference);
				if (!done)
					done = collectQuerySymbolTable(PSS_INSERT_KEY, item, alias, reference);
				if (!done)
					done = collectQuerySymbolTable(PSS_UPDATE_KEY, item, alias, reference);
				if (!done)
					done = collectQuerySymbolTable(PSS_UNION_KEY, item, alias, reference);
				if (!done)
					done = collectQuerySymbolTable(PSS_INTERSECT_KEY, item, alias, reference);
			}

			subMap.put(PSS_TABLE_KEY, item);
		} else {
			showTrace(parseTrace, "Wrong number of entries: " + ctx.getText());
		}
		addToParent(parentRuleIndex, parentStackLevel, subMap);
		showTrace(parseTrace, "TABLE PRIMARY: " + subMap);
	}
	

	private Boolean collectQuerySymbolTable(String hdr, Map<String, Object> item, String alias,
			Map<String, Object> reference) {
		String queryName = hdr + (queryCount - 1);
		Map<String, Object> query = (Map<String, Object>) symbolTable.remove(queryName);
		if (query != null) {
			item.put(hdr, reference);

			// add alias to query
			collectSymbolTable(alias, queryName);

			// propagate interface to outer layer of query
			Map<String, Object> hold = (Map<String, Object>) symbolTable.get(queryName);
			// Move unknowns to query
			Map<String, Object> unk = (Map<String, Object>) symbolTable.remove("unknown");

			if (unk != null) {
				// move any other interface elements to query and empty unknowns
				Map<String, Object> interfac = (Map<String, Object>) query.get("interface");
				if (interfac != null)
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
					symbolTable.put("unknown", unk);
			}
			// Add query definition back into symbol table
			symbolTable.put("def_" + queryName, query);
			return true;
		} else
			return false;
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

			// try swapping names here
			table = getTableName(table);

			collectSymbolTable(table, table);

			subMap.put(PSS_TABLE_KEY, table);
			showTrace(parseTrace, "table: " + table + " Map: " + subMap);
		} else if (subMap.size() == 2) {
			showTrace(parseTrace, "Two entries: " + subMap);
			String schema = (String) subMap.remove("1");
			subMap.put(PSS_SCHEMA_KEY, schema);
			String table = (String) subMap.remove("2");

			// try swapping names here
			table = getTableName(table);

			collectSymbolTable(table, table);

			subMap.put(PSS_TABLE_KEY, table);
			showTrace(parseTrace, "Schema: " + schema + " Table: " + table + " Map: " + subMap);
		} else if (subMap.size() == 3) {
			showTrace(parseTrace, "Three entries: " + subMap);
			String dbname = (String) subMap.remove("1");
			subMap.put(PSS_DATABASE_NAME_KEY, dbname);
			String schema = (String) subMap.remove("2");
			subMap.put(PSS_SCHEMA_KEY, schema);
			String table = (String) subMap.remove("3");

			// try swapping names here
			table = getTableName(table);

			collectSymbolTable(table, table);

			subMap.put(PSS_TABLE_KEY, table);
			showTrace(parseTrace, "Database: " + dbname + "Schema: " + schema + " Table: " + table + " Map: " + subMap);
		} else {
			showTrace(parseTrace, "Too many entries: " + subMap);
		}
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
			subMap.put(PSS_JOIN_KEY, ctx.getText());
		else if (ctx.getChildCount() == 3) {
			String type = (String) subMap.remove("1");
			subMap.put(PSS_JOIN_KEY, ctx.getChild(0).getText() + ctx.getChild(2).getText());
//			subMap.put(PSS_JOIN_TYPE_KEY, type);
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
			subMap.put(PSS_JOIN_KEY, ctx.getText());
		else if (ctx.getChildCount() == 2) {
			// String type = (String) subMap.remove("1");
			subMap.put(PSS_JOIN_KEY, ctx.getChild(0).getText());
			// subMap.put("join_type", type);
		} else if (ctx.getChildCount() == 3) {
			// String type = (String) subMap.remove("1");
			subMap.put(PSS_JOIN_KEY, ctx.getChild(0).getText() + ctx.getChild(1).getText());
			// subMap.put("join_type", type);
		}
		// Add item to parent map
		addToParent(parentRuleIndex, parentStackLevel, subMap);
		showTrace(parseTrace, "QUALIFIED JOIN: " + subMap);
	}

	// join_type does NOT need its own methods
	
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
			join.put(PSS_JOIN_ON_KEY, item);
			showTrace(parseTrace, "join On Clause: " + join);
		} else {
			showTrace(parseTrace, "Wrong number of entries: " + ctx.getText());
		}
		sqlTree.put("SKIP", "TRUE");
	}

	@Override
	public void exitJoin_condition(@NotNull SQLSelectParserParser.Join_conditionContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		int parentRuleIndex = ctx.getParent().getRuleIndex();

		Integer stackLevel = currentStackLevel(ruleIndex);
		Integer parentStackLevel = currentStackLevel(parentRuleIndex);

		Map<String, Object> subMap = getNodeMap(ruleIndex, stackLevel);
		subMap = (Map<String, Object>) subMap.get("1");
		if (subMap.containsKey(PSS_PARENTHESES_KEY)) {
			// Remove extraneous parentheses from the outermost layer of the On
			// Condition
			Map<String, Object> contents = (Map<String, Object>) subMap.remove(PSS_PARENTHESES_KEY);
			subMap.putAll(contents);
		}

		// Now handle child as usual
		handleOneChild(ruleIndex);
	}

	@Override
	public void exitNamed_columns_join(@NotNull SQLSelectParserParser.Named_columns_joinContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		handleOneChild(ruleIndex);
	}
	
	// using_term does NOT need its own methods

	  
	/*
	===============================================================================
	  Column List clauses
	===============================================================================
	*/
	

	@Override
	public void exitColumn_reference_list(@NotNull SQLSelectParserParser.Column_reference_listContext ctx) {
	}

	@Override
	public void exitColumn_reference(@NotNull SQLSelectParserParser.Column_referenceContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		Integer stackLevel = currentStackLevel(ruleIndex);
		Map<String, Object> subMap = getNodeMap(ruleIndex, stackLevel);
		subMap.remove("Type");

		Map<String, Object> columnSubTree = new HashMap<String, Object>();
		Object columnRef = null;
		String tableRef = null;
		String tableRefKey = "unknown";
		Boolean doNotSkip = true;

		if (subMap.size() == 1) {
			showTrace(parseTrace, "Just One Identifier: " + subMap);
			columnRef = subMap.remove("1");
		} else if (subMap.size() == 2) {
			showTrace(parseTrace, "Two entries: " + subMap);
			// tableRefKey = "table_ref";
			tableRef = (String) subMap.remove("1");
			tableRefKey = tableRef;
			columnRef = subMap.remove("2");

		} else {
			showTrace(parseTrace, "Too many entries: " + subMap);
			doNotSkip = false;
		}
		if (doNotSkip) {
			// Add column to SQL AST Tree
			columnSubTree.put(PSS_TABLE_REF_KEY, tableRef);
			if (columnRef instanceof HashMap<?, ?>) {
				// should be a substitution
				HashMap<String, Object> columnMap = (HashMap<String, Object>) columnRef;
				HashMap<String, Object> substitutionMap = (HashMap<String, Object>) columnMap.get(PSS_SUBSTITUTION_KEY);
				substitutionMap.put(PSS_TYPE_KEY, PSS_COLUMN_KEY);

				// Add reference to Substitution Variables list
				substitutionsMap.put((String) substitutionMap.get("name"), PSS_COLUMN_KEY);

				columnSubTree.putAll((HashMap<String, Object>) columnRef);
			} else {
				columnSubTree.put(PSS_NAME_KEY, columnRef);
			}
			subMap.put(PSS_COLUMN_KEY, columnSubTree);

			// Capture SymbolTable entry
			collectSymbolTableItem(tableRefKey, columnRef, ctx.getStart());
		}
		showTrace(parseTrace, "Column Reference: " + subMap);
	}

/*
===============================================================================
  Predicands <value expression primary>
===============================================================================
*/


	@Override
	public void exitValue_expression_primary(@NotNull SQLSelectParserParser.Value_expression_primaryContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		handleOneChild(ruleIndex);
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
			subMap.put(PSS_PARENTHESES_KEY, item);
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

	/*
	===============================================================================
	  Aggregate Over Sets Functions
	===============================================================================
	*/
	// Part of the <aggregate_function> rule
	@Override
	public void exitCount_all_aggregate(@NotNull SQLSelectParserParser.Count_all_aggregateContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		Integer stackLevel = currentStackLevel(ruleIndex);
		Map<String, Object> subMap = getNodeMap(ruleIndex, stackLevel);
		Object type = subMap.remove("Type");

		Map<String, Object> item = new HashMap<String, Object>();

		if (subMap.size() == 0) {
			item.put(PSS_FUNCTION_NAME_KEY, "COUNT");
			item.put(PSS_QUALIFIER_KEY, null);
			item.put(PSS_PARAMETERS_KEY, "*");
			subMap.put(PSS_FUNCTION_KEY, item);
		} else {
			showTrace(parseTrace, "Wrong number of entries: " + subMap);
		}
		showTrace(parseTrace, "Aggregate Function: " + subMap);
	}

	// Part of the <aggregate_function> rule
	@Override
	public void exitGeneral_set_function(@NotNull SQLSelectParserParser.General_set_functionContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		Integer stackLevel = currentStackLevel(ruleIndex);
		Map<String, Object> subMap = getNodeMap(ruleIndex, stackLevel);
		Object type = subMap.remove("Type");

		Map<String, Object> item = new HashMap<String, Object>();

		if (subMap.size() == 2) {
			item.put(PSS_FUNCTION_NAME_KEY, subMap.remove("1"));
			item.put(PSS_QUALIFIER_KEY, null);
			item.put(PSS_PARAMETERS_KEY, subMap.remove("2"));
			subMap.put(PSS_FUNCTION_KEY, item);
		} else if (subMap.size() == 3) {
			item.put(PSS_FUNCTION_NAME_KEY, subMap.remove("1"));
			item.put(PSS_QUALIFIER_KEY, subMap.remove("2"));
			item.put(PSS_PARAMETERS_KEY, subMap.remove("3"));
			subMap.put(PSS_FUNCTION_KEY, item);
		} else {
			showTrace(parseTrace, "Wrong number of entries: " + subMap);
		}
		showTrace(parseTrace, "Aggregate Function: " + subMap);
	}

	// set_function_type does NOT need its own exit method
	// set_qualifier_type does NOT need its own exit method
	
	/*
	===============================================================================
	 CASE Clause <case expression>
	===============================================================================
	*/


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
				// Variation 2: When contains CONDITIONS
				subMap.putAll((Map<String, Object>) subMap.remove("1"));
				subMap.putAll((Map<String, Object>) subMap.remove("2"));
			} else if (subMap.size() == 3) {
				// Variation 1: Case ITEM in implied equals formula with When
				// Predicand
				subMap.put(PSS_ITEM_KEY, subMap.remove("1"));
				subMap.putAll((Map<String, Object>) subMap.remove("2"));
				subMap.putAll((Map<String, Object>) subMap.remove("3"));
			} else {
				showTrace(parseTrace, "Wrong number of entries: " + ctx.getText());
			}

			Map<String, Object> item = new HashMap<String, Object>();
			item.put(PSS_CASE_KEY, subMap);
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
			if (subMap.size() >= 1) {
				item.put(PSS_CLAUSES_KEY, subMap);
			} else {
				showTrace(parseTrace, "Wrong number of entries: " + ctx.getText());
			}
			addToParent(parentRuleIndex, parentStackLevel, item);
			showTrace(parseTrace, "Case Variation 2, Without Item: When Clause List: " + item);
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
				item.put(PSS_WHEN_KEY, subMap.remove("1"));
				item.put(PSS_THEN_KEY, subMap.remove("2"));
			} else {
				showTrace(parseTrace, "Wrong number of entries: " + ctx.getText());
			}
			addToParent(parentRuleIndex, parentStackLevel, item);
			showTrace(parseTrace, "Case Variation 2, Without Item: Case When Clause: " + item);

		}

		@Override
		public void exitWhen_value_list(@NotNull SQLSelectParserParser.When_value_listContext ctx) {
			int ruleIndex = ctx.getRuleIndex();
			int parentRuleIndex = ctx.getParent().getRuleIndex();

			Integer stackLevel = currentStackLevel(ruleIndex);
			Integer parentStackLevel = currentStackLevel(parentRuleIndex);

			Map<String, Object> subMap = removeNodeMap(ruleIndex, stackLevel);
			Object type = subMap.remove("Type");
			Map<String, Object> item = new HashMap<String, Object>();
			if (subMap.size() >= 1) {
				item.put(PSS_CLAUSES_KEY, subMap);
			} else {
				showTrace(parseTrace, "Wrong number of entries: " + ctx.getText());
			}
			addToParent(parentRuleIndex, parentStackLevel, item);
			showTrace(parseTrace, "Case Variation 1, With Item: When Value List: " + item);
		}

		@Override
		public void exitWhen_value_clause(@NotNull SQLSelectParserParser.When_value_clauseContext ctx) {
			int ruleIndex = ctx.getRuleIndex();
			int parentRuleIndex = ctx.getParent().getRuleIndex();

			Integer stackLevel = currentStackLevel(ruleIndex);
			Integer parentStackLevel = currentStackLevel(parentRuleIndex);

			Map<String, Object> subMap = removeNodeMap(ruleIndex, stackLevel);
			Object type = subMap.remove("Type");
			Map<String, Object> item = new HashMap<String, Object>();
			if (subMap.size() == 2) {
				item.put(PSS_WHEN_KEY, subMap.remove("1"));
				item.put(PSS_THEN_KEY, subMap.remove("2"));
			} else {
				showTrace(parseTrace, "Wrong number of entries: " + ctx.getText());
			}
			addToParent(parentRuleIndex, parentStackLevel, item);
			showTrace(parseTrace, "Case Variation 1, With Item: When Value Clause: " + item);

		}

		@Override
		public void exitElse_clause(@NotNull SQLSelectParserParser.Else_clauseContext ctx) {
			int ruleIndex = ctx.getRuleIndex();
			int parentRuleIndex = ctx.getParent().getRuleIndex();

			Integer stackLevel = currentStackLevel(ruleIndex);
			Integer parentStackLevel = currentStackLevel(parentRuleIndex);

			Map<String, Object> subMap = removeNodeMap(ruleIndex, stackLevel);
			Object type = subMap.remove("Type");
			Map<String, Object> item = new HashMap<String, Object>();
			if (subMap.size() == 1) {
				item.put(PSS_ELSE_KEY, subMap.remove("1"));
			} else {
				showTrace(parseTrace, "Wrong number of entries: " + ctx.getText());
			}
			addToParent(parentRuleIndex, parentStackLevel, item);
			showTrace(parseTrace, "Else Clause: " + item);

		}

		@Override
		public void exitCase_result(@NotNull SQLSelectParserParser.Case_resultContext ctx) {
			int ruleIndex = ctx.getRuleIndex();
			handleOneChild(ruleIndex);
		}

		// null_literal does NOT need its own exit method
		
		/*
		===============================================================================
		  CAST Function
		===============================================================================
		*/

//		cast_function_expression
//		  : (CAST | TRYCAST) LEFT_PAREN value_expression AS data_type RIGHT_PAREN
//		  ;

		@Override
		public void exitCast_function_expression(@NotNull SQLSelectParserParser.Cast_function_expressionContext ctx) {
			int ruleIndex = ctx.getRuleIndex();
			Integer stackLevel = currentStackLevel(ruleIndex);
			Map<String, Object> subMap = getNodeMap(ruleIndex, stackLevel);
			Object type = subMap.remove("Type");

			Map<String, Object> item = new HashMap<String, Object>();

			if (subMap.size() == 3) {
				String function = (String) subMap.remove("1");
				item.put(PSS_FUNCTION_NAME_KEY, function);
				item.put(PSS_TYPE_KEY, function.toUpperCase());
				item.put(PSS_VALUE_KEY, subMap.remove("2"));
				item.put(PSS_DATATYPE_KEY, subMap.remove("3"));
				subMap.put(PSS_FUNCTION_KEY, item);
			} else {
				showTrace(parseTrace, "Wrong number of entries: " + subMap);
			}
			showTrace(parseTrace, "CAST Function: " + subMap);
		}

		@Override
		public void exitData_type(@NotNull SQLSelectParserParser.Data_typeContext ctx) {
			int ruleIndex = ctx.getRuleIndex();
			handleOneChild(ruleIndex);
		}


		@Override
		public void exitVariable_size_data_type(@NotNull SQLSelectParserParser.Variable_size_data_typeContext ctx) {
			int ruleIndex = ctx.getRuleIndex();
			Integer stackLevel = currentStackLevel(ruleIndex);
			Map<String, Object> subMap = getNodeMap(ruleIndex, stackLevel);
			Object type = subMap.remove("Type");

			if (subMap.size() >=1) {
				Object item = subMap.remove("1");
				if (item instanceof String)
					subMap.put(PSS_TYPE_KEY, item);
				else
					subMap.put(PSS_TYPE_KEY, ((HashMap<String, String>) item).get("1"));
			}
			
			if (subMap.size() == 2) {
				subMap.putAll((Map<String, Object>)  subMap.remove("2"));
			} else {
				showTrace(parseTrace, "Wrong number of entries: " + ctx.getText());
			}
			showTrace(parseTrace, "Variable Data Type: " + subMap);
		}


		@Override
		public void exitVariable_data_type_name(@NotNull SQLSelectParserParser.Variable_data_type_nameContext ctx) {
			int ruleIndex = ctx.getRuleIndex();
			int parentRuleIndex = ctx.getParent().getRuleIndex();

			Integer stackLevel = currentStackLevel(ruleIndex);
			Integer parentStackLevel = currentStackLevel(parentRuleIndex);

			Map<String, Object> subMap = removeNodeMap(ruleIndex, stackLevel);

			subMap = makeRuleMap(ruleIndex);
			subMap.remove("Type");
			
			if (ctx.getChildCount() == 1) {
				showTrace(parseTrace, "one word data type: " + ctx.getText());
				String part = ctx.getChild(0).getText().toUpperCase();
				subMap.put("1", part);
			} else if (ctx.getChildCount() == 2) {
				showTrace(parseTrace, "two word data type: " + ctx.getText());
				String part = ctx.getChild(0).getText().toUpperCase();
				part = part + " " + ctx.getChild(1).getText().toUpperCase();
				subMap.put("1", part);
			} else if (ctx.getChildCount() == 3) {
				showTrace(parseTrace, "three word data type: " + ctx.getText());
				String part = ctx.getChild(0).getText().toUpperCase();
				part = part + " " + ctx.getChild(1).getText().toUpperCase();
				part = part + " " + ctx.getChild(2).getText().toUpperCase();
				subMap.put("1", part);
			}
			// Add item to parent map
			addToParent(parentRuleIndex, parentStackLevel, subMap);

			showTrace(parseTrace, "Variable Data Type Name: " + subMap);
		}

		@Override
		public void exitType_length(@NotNull SQLSelectParserParser.Type_lengthContext ctx) {
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
			if (ctx.getChildCount() == 3) {
				showTrace(parseTrace, "Three entries: " + ctx.getText());
				subMap.put(PSS_LENGTH_KEY, ctx.getChild(1).getText());
			}
			// Add item to parent map
			addToParent(parentRuleIndex, parentStackLevel, subMap);
			showTrace(parseTrace, "Data Type Length: " + subMap);
		}


		@Override
		public void exitPrecision_scale_data_type(@NotNull SQLSelectParserParser.Precision_scale_data_typeContext ctx) {
			int ruleIndex = ctx.getRuleIndex();
			Integer stackLevel = currentStackLevel(ruleIndex);
			Map<String, Object> subMap = getNodeMap(ruleIndex, stackLevel);
			Object type = subMap.remove("Type");

			if (subMap.size() >=1) {
				Object item = subMap.remove("1");
				if (item instanceof String)
					subMap.put(PSS_TYPE_KEY, item);
				else
					subMap.put(PSS_TYPE_KEY, ((HashMap<String, String>) item).get("1"));
			}
			
			if (subMap.size() == 2) {
				subMap.putAll((Map<String, Object>)  subMap.remove("2"));
			} else {
				showTrace(parseTrace, "Wrong number of entries: " + ctx.getText());
			}
			showTrace(parseTrace, "Precision Data Type: " + subMap);
		}


		@Override
		public void exitPrecision_data_type_name(@NotNull SQLSelectParserParser.Precision_data_type_nameContext ctx) {
			int ruleIndex = ctx.getRuleIndex();
			int parentRuleIndex = ctx.getParent().getRuleIndex();

			Integer stackLevel = currentStackLevel(ruleIndex);
			Integer parentStackLevel = currentStackLevel(parentRuleIndex);

			Map<String, Object> subMap = removeNodeMap(ruleIndex, stackLevel);

			subMap = makeRuleMap(ruleIndex);
			subMap.remove("Type");
			
			if (ctx.getChildCount() == 1) {
				showTrace(parseTrace, "one word data type: " + ctx.getText());
				String part = ctx.getChild(0).getText().toUpperCase();
				subMap.put("1", part);
			} else if (ctx.getChildCount() == 2) {
				showTrace(parseTrace, "two word data type: " + ctx.getText());
				String part = ctx.getChild(0).getText().toUpperCase();
				part = part + " " + ctx.getChild(1).getText().toUpperCase();
				subMap.put("1", part);
			}
			// Add item to parent map
			addToParent(parentRuleIndex, parentStackLevel, subMap);

			showTrace(parseTrace, "Precision Data Type Name: " + subMap);
		}

		@Override
		public void exitPrecision_param(@NotNull SQLSelectParserParser.Precision_paramContext ctx) {
			int ruleIndex = ctx.getRuleIndex();
			int parentRuleIndex = ctx.getParent().getRuleIndex();

			Integer stackLevel = currentStackLevel(ruleIndex);
			Integer parentStackLevel = currentStackLevel(parentRuleIndex);

			Map<String, Object> subMap = removeNodeMap(ruleIndex, stackLevel);
			subMap = makeRuleMap(ruleIndex);
			subMap.remove("Type");
			
			if (ctx.getChildCount() == 3) {
				showTrace(parseTrace, "Three entries: " + ctx.getText());
				subMap.put(PSS_PRECISION_KEY, ctx.getChild(1).getText());
			} else if (ctx.getChildCount() == 5) {
				showTrace(parseTrace, "Three entries: " + ctx.getText());
				subMap.put(PSS_PRECISION_KEY, ctx.getChild(1).getText());
				subMap.put(PSS_SCALE_KEY, ctx.getChild(3).getText());
			} 
			// Add item to parent map
			addToParent(parentRuleIndex, parentStackLevel, subMap);
			showTrace(parseTrace, "Precision Param: " + subMap);
		}


		@Override
		public void exitStatic_data_type(@NotNull SQLSelectParserParser.Static_data_typeContext ctx) {
			int ruleIndex = ctx.getRuleIndex();
			Integer stackLevel = currentStackLevel(ruleIndex);
			Map<String, Object> subMap = getNodeMap(ruleIndex, stackLevel);
			Object type = subMap.remove("Type");

			if (subMap.size() ==1) {
				Object item = subMap.remove("1");
				if (item instanceof String)
					subMap.put(PSS_TYPE_KEY, item);
				else
					subMap.put(PSS_TYPE_KEY, ((HashMap<String, String>) item).get("1"));
			} else {
				showTrace(parseTrace, "Wrong number of entries: " + ctx.getText());
			}
			showTrace(parseTrace, "Static Data Type: " + subMap);
		}

		@Override
		public void exitStatic_data_type_name(@NotNull SQLSelectParserParser.Static_data_type_nameContext ctx) {
			int ruleIndex = ctx.getRuleIndex();
			int parentRuleIndex = ctx.getParent().getRuleIndex();

			Integer stackLevel = currentStackLevel(ruleIndex);
			Integer parentStackLevel = currentStackLevel(parentRuleIndex);

			Map<String, Object> subMap = removeNodeMap(ruleIndex, stackLevel);

			subMap = makeRuleMap(ruleIndex);
			subMap.remove("Type");
			
			if (ctx.getChildCount() == 1) {
				showTrace(parseTrace, "one word data type: " + ctx.getText());
				String part = ctx.getChild(0).getText().toUpperCase();
				subMap.put("1", part);
			} else if (ctx.getChildCount() == 2) {
				showTrace(parseTrace, "two word data type: " + ctx.getText());
				String part = ctx.getChild(0).getText().toUpperCase();
				part = part + " " + ctx.getChild(1).getText().toUpperCase();
				subMap.put("1", part);
			} else if (ctx.getChildCount() == 3) {
				showTrace(parseTrace, "three word data type: " + ctx.getText());
				String part = ctx.getChild(0).getText().toUpperCase();
				part = part + " " + ctx.getChild(1).getText().toUpperCase();
				part = part + " " + ctx.getChild(2).getText().toUpperCase();
				subMap.put("1", part);
			} else if (ctx.getChildCount() == 4) {
				showTrace(parseTrace, "four word data type: " + ctx.getText());
				String part = ctx.getChild(0).getText().toUpperCase();
				part = part + " " + ctx.getChild(1).getText().toUpperCase();
				part = part + " " + ctx.getChild(2).getText().toUpperCase();
				part = part + " " + ctx.getChild(3).getText().toUpperCase();
				subMap.put("1", part);
			}
			// Add item to parent map
			addToParent(parentRuleIndex, parentStackLevel, subMap);
			showTrace(parseTrace, "Static Data Type: " + subMap);
		}

		/*
		===============================================================================
		  WINDOW Functions
		===============================================================================
		*/

		  /*
		   * Functions over partitions
		   * rank() OVER (partition by k_stfd order by OBSERVATION_TM desc, row_num desc)
		   * last_value(column) over (partition by other_column rows between 2 preceding and unbounded following)
		   */

		@Override
		public void exitWindow_over_partition_expression(
				@NotNull SQLSelectParserParser.Window_over_partition_expressionContext ctx) {
			int ruleIndex = ctx.getRuleIndex();
			int parentRuleIndex = ctx.getParent().getRuleIndex();

			Integer stackLevel = currentStackLevel(ruleIndex);
			Integer parentStackLevel = currentStackLevel(parentRuleIndex);

			Map<String, Object> subMap = getNodeMap(ruleIndex, stackLevel);
			Object type = subMap.remove("Type");

			if (subMap.size() == 2) {
				showTrace(parseTrace, "Window Over Partition: " + subMap);
				Map<String, Object> item = new HashMap<String, Object>();
				item.putAll((Map<String, Object>) subMap.remove("1"));
				item.putAll((Map<String, Object>) subMap.remove("2"));
				subMap.put(PSS_WINDOW_FUNCTION_KEY, item);
			} else {
				showTrace(parseTrace, "Incorrect number of entries: " + subMap);
			}
		}

		@SuppressWarnings("unchecked")
		@Override
		public void exitWindow_function(@NotNull SQLSelectParserParser.Window_functionContext ctx) {
			int ruleIndex = ctx.getRuleIndex();
			int parentRuleIndex = ctx.getParent().getRuleIndex();

			Integer stackLevel = currentStackLevel(ruleIndex);
			Integer parentStackLevel = currentStackLevel(parentRuleIndex);

			Map<String, Object> subMap = removeNodeMap(ruleIndex, stackLevel);
			Object type = subMap.remove("Type");
			String functType = (String) subMap.remove("1");

			Map<String, Object> item = new HashMap<String, Object>();
			Map<String, Object> hold = new HashMap<String, Object>();

			if (subMap.size() == 0) {
				item.put(PSS_PARAMETERS_KEY, null);
			} else if (subMap.size() >= 1) {
				hold = (Map<String, Object>) subMap.remove("2");
				type = hold.remove("Type");
				item.put(PSS_PARAMETERS_KEY, hold.remove(type.toString()));
			} else {
				showTrace(parseTrace, "Wrong number of entries: " + ctx.getText());
			}
			
			item.put(PSS_FUNCTION_NAME_KEY, functType);

			if (subMap.containsKey("3")) {
				item.putAll((Map<String, Object>) subMap.remove("3"));
			}
			if (subMap.containsKey("4")) {
				item.putAll((Map<String, Object>) subMap.remove("4"));
			}
			
			subMap.put(PSS_FUNCTION_KEY, item);

			addToParent(parentRuleIndex, parentStackLevel, subMap);
			showTrace(parseTrace, "WINDOW FUNCTION: " + subMap);
		}

		@SuppressWarnings("unchecked")
		@Override
		public void exitOver_clause(@NotNull SQLSelectParserParser.Over_clauseContext ctx) {
			int ruleIndex = ctx.getRuleIndex();
			int parentRuleIndex = ctx.getParent().getRuleIndex();

			Integer stackLevel = currentStackLevel(ruleIndex);
			Integer parentStackLevel = currentStackLevel(parentRuleIndex);

			Map<String, Object> subMap = getNodeMap(ruleIndex, stackLevel);
			Object type = subMap.remove("Type");

			HashMap<String, Object> item = new HashMap<String, Object>();
			if (subMap.size() == 0) {
				subMap.put(PSS_OVER_KEY, null);
			} else if (subMap.size() == 1) {
				item.putAll((Map<String, Object>) subMap.remove("1"));
				subMap.put(PSS_OVER_KEY, item);
			} else if (subMap.size() == 2) {
				item.putAll((Map<String, Object>) subMap.remove("1"));
				item.putAll((Map<String, Object>) subMap.remove("2"));
				subMap.put(PSS_OVER_KEY, item);
			} else if (subMap.size() == 3) {
				item.putAll((Map<String, Object>) subMap.remove("1"));
				item.putAll((Map<String, Object>) subMap.remove("2"));
				item.putAll((Map<String, Object>) subMap.remove("3"));
				subMap.put(PSS_OVER_KEY, item);
			} else {
				showTrace(parseTrace, "Wrong number of entries: " + subMap);
			}
		}

		@SuppressWarnings("unchecked")
		@Override
		public void exitPartition_by_clause(@NotNull SQLSelectParserParser.Partition_by_clauseContext ctx) {
			int ruleIndex = ctx.getRuleIndex();
			int parentRuleIndex = ctx.getParent().getRuleIndex();

			Integer stackLevel = currentStackLevel(ruleIndex);
			Integer parentStackLevel = currentStackLevel(parentRuleIndex);

			Map<String, Object> subMap = removeNodeMap(ruleIndex, stackLevel);
			Object type = subMap.remove("Type");

			if (subMap.size() >= 1) {
				HashMap<String, Object> item = (HashMap<String, Object>) subMap.remove("1");
				type = item.remove("Type");

				item.put(PSS_PARTITION_BY_KEY, item.remove(type.toString()));
				addToParent(parentRuleIndex, parentStackLevel, item);
			} else {
				showTrace(parseTrace, "Not enough entries: " + subMap);
			}

		}


		@Override
		public void exitBracket_frame_clause(
				@NotNull SQLSelectParserParser.Bracket_frame_clauseContext ctx) {
			int ruleIndex = ctx.getRuleIndex();
			int parentRuleIndex = ctx.getParent().getRuleIndex();

			Integer stackLevel = currentStackLevel(ruleIndex);
			Integer parentStackLevel = currentStackLevel(parentRuleIndex);

			Map<String, Object> subMap = getNodeMap(ruleIndex, stackLevel);
			Object type = subMap.remove("Type");

			if (subMap.size() == 2) {
				showTrace(parseTrace, "Window Over Partition: " + subMap);
				Map<String, Object> item = new HashMap<String, Object>();
				item.put(PSS_TYPE_KEY, (String) subMap.remove("1"));
				item.putAll((Map<String, Object>) subMap.remove("2"));
				subMap.put(PSS_BRACKET_FRAME_KEY, item);
			} else {
				showTrace(parseTrace, "Incorrect number of entries: " + subMap);
			}
		}

		// rows_or_range clauses do not need their own method
		

		@Override
		public void exitBracket_frame_definition(@NotNull SQLSelectParserParser.Bracket_frame_definitionContext ctx) {
			int ruleIndex = ctx.getRuleIndex();
			handleOneChild(ruleIndex);
		}

		@Override
		public void exitBetween_frame_definition(@NotNull SQLSelectParserParser.Between_frame_definitionContext ctx) {
			int ruleIndex = ctx.getRuleIndex();
			int parentRuleIndex = ctx.getParent().getRuleIndex();

			Integer stackLevel = currentStackLevel(ruleIndex);
			Integer parentStackLevel = currentStackLevel(parentRuleIndex);

			Map<String, Object> subMap = getNodeMap(ruleIndex, stackLevel);
			Object type = subMap.remove("Type");

			if (subMap.size() == 2) {
				showTrace(parseTrace, "Window Over Partition: " + subMap);
				Map<String, Object> item = new HashMap<String, Object>();
				item.put(PSS_RANGE_BEGIN_KEY,  subMap.remove("1"));
				item.put(PSS_RANGE_END_KEY,  subMap.remove("2"));
				subMap.put(PSS_BETWEEN_KEY, item);
			} else {
				showTrace(parseTrace, "Incorrect number of entries: " + subMap);
			}
		}


		@Override
		public void exitFrame_edge(@NotNull SQLSelectParserParser.Frame_edgeContext ctx) {
			int ruleIndex = ctx.getRuleIndex();
			handleOneChild(ruleIndex);
		}

		@Override
		public void exitPreceding_frame_edge(
				@NotNull SQLSelectParserParser.Preceding_frame_edgeContext ctx) {
			int ruleIndex = ctx.getRuleIndex();
			Integer stackLevel = currentStackLevel(ruleIndex);
			Map<String, Object> subMap = getNodeMap(ruleIndex, stackLevel);
			Object type = subMap.remove("Type");

			if (subMap.size() == 1) {
				subMap.put(PSS_VALUE_KEY, (String) subMap.remove("1"));
				subMap.put(PSS_BRACKET_DIRECTION_KEY, PSS_PRECEDING_KEY);
				showTrace(parseTrace, "Preceding Edge Clause: " + subMap);

			} else {
				showTrace(parseTrace, "Wrong number of entries: " + subMap);
			}
		}

		@Override
		public void exitFollowing_frame_edge(
				@NotNull SQLSelectParserParser.Following_frame_edgeContext ctx) {
			int ruleIndex = ctx.getRuleIndex();
			Integer stackLevel = currentStackLevel(ruleIndex);
			Map<String, Object> subMap = getNodeMap(ruleIndex, stackLevel);
			Object type = subMap.remove("Type");

			if (subMap.size() == 1) {
				subMap.put(PSS_VALUE_KEY, (String) subMap.remove("1"));
				subMap.put(PSS_BRACKET_DIRECTION_KEY, PSS_FOLLOWING_KEY);
				showTrace(parseTrace, "Preceding Edge Clause: " + subMap);

			} else {
				showTrace(parseTrace, "Wrong number of entries: " + subMap);
			}
		}


		@Override
		public void exitCurrent_row_edge(@NotNull SQLSelectParserParser.Current_row_edgeContext ctx) {
			int ruleIndex = ctx.getRuleIndex();
			int parentRuleIndex = ctx.getParent().getRuleIndex();

			Integer stackLevel = currentStackLevel(ruleIndex);
			Integer parentStackLevel = currentStackLevel(parentRuleIndex);

			Map<String, Object> subMap = removeNodeMap(ruleIndex, stackLevel);

			subMap = makeRuleMap(ruleIndex);
			subMap.remove("Type");
			
			if (ctx.getChildCount() == 2) {
				showTrace(parseTrace, "two word frame edge: " + ctx.getText());
				String part = ctx.getChild(0).getText().toUpperCase();
				part = part + " " + ctx.getChild(1).getText().toUpperCase();
				subMap.put(PSS_VALUE_KEY, part);
			} else  {
				showTrace(parseTrace, "incorrect phrase");
			}
			// Add item to parent map
			addToParent(parentRuleIndex, parentStackLevel, subMap);
			showTrace(parseTrace, "Static Data Type: " + subMap);
		}

		// item_select_function does NOT need its own exit method

		@Override
		public void exitSelect_direction(@NotNull SQLSelectParserParser.Select_directionContext ctx) {
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
				subMap.put(PSS_SELECT_DIRECTION_KEY, ctx.getChild(1).getText());
			}
			// Add item to parent map
			addToParent(parentRuleIndex, parentStackLevel, subMap);
		}

		@Override
		public void exitNull_handling(@NotNull SQLSelectParserParser.Null_handlingContext ctx) {
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
				subMap.put(PSS_NULL_HANDLING_KEY, ctx.getChild(0).getText());
			}
			// Add item to parent map
			addToParent(parentRuleIndex, parentStackLevel, subMap);
		}


/*
===============================================================================
  Reorganize the following: PROGRESS MADE TO THIS POINT
===============================================================================
*/

	@Override
	public void exitSimple_variable_identifier(@NotNull SQLSelectParserParser.Simple_variable_identifierContext ctx) {
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
			item.put(PSS_NAME_KEY, ctx.getChild(0).getText());
			subMap.put(PSS_SUBSTITUTION_KEY, item);
		}
		// Add item to parent map
		addToParent(parentRuleIndex, parentStackLevel, subMap);
		showTrace(parseTrace, "Substitution Variable: " + subMap);
	}

	@Override
	public void exitExtended_variable_identifier(@NotNull SQLSelectParserParser.Extended_variable_identifierContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		int parentRuleIndex = ctx.getParent().getRuleIndex();

		Integer stackLevel = currentStackLevel(ruleIndex);
		Integer parentStackLevel = currentStackLevel(parentRuleIndex);

		Map<String, Object> subMap = removeNodeMap(ruleIndex, stackLevel);

		Map<String, Object> item = new HashMap<String, Object>();
		Map<String, Object> subItem = new HashMap<String, Object>();

		if (subMap == null) {
			// unqualified select all has no map
			subMap = makeRuleMap(ruleIndex);
		}
		subMap.remove("Type");
		if (ctx.getChildCount() == 1) {
			showTrace(parseTrace, "Just One Identifier: " + ctx.getText());
			String variable_name = ctx.getChild(0).getText();
			item.put(PSS_NAME_KEY, variable_name);
			item.put(PSS_PARTS_KEY, subItem);
			String[] trim = variable_name.split("\\.",0);
			if (trim.length == 3) {
				subItem.put("1", trim[0].substring(1));
				subItem.put("2", trim[1]);
				subItem.put("3", trim[2].substring(0, trim[2].length()-1));
			} else if (trim.length == 2) {
				subItem.put("1", trim[0].substring(1));
				subItem.put("2", trim[1].substring(0, trim[1].length()-1));
			} else {
				subItem.put("1", variable_name.substring(1, variable_name.length()-1));				
			}
			subMap.put(PSS_SUBSTITUTION_KEY, item);
		}
		// Add item to parent map
		addToParent(parentRuleIndex, parentStackLevel, subMap);
		showTrace(parseTrace, "Substitution Variable: " + subMap);
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
		int stackLevel = currentStackLevel(ruleIndex);

		Map<String, Object> subMap = getNodeMap(ruleIndex, stackLevel);
		handlePushDown(ruleIndex);
//		handleOneChild(ruleIndex);
	}

	@Override
	public void exitOrderby_clause(@NotNull SQLSelectParserParser.Orderby_clauseContext ctx) {
		// TODO: ITEM 36 - Add Substitution Variables to Order By: Subs Variable
		// List, Table Dictionary, Symbol Table, AST Tree
		int ruleIndex = ctx.getRuleIndex();
		int parentRuleIndex = ctx.getParent().getRuleIndex();
		if (parentRuleIndex == (Integer) SQLSelectParserParser.RULE_over_clause) {

			Integer stackLevel = currentStackLevel(ruleIndex);
			Integer parentStackLevel = currentStackLevel(parentRuleIndex);

			Map<String, Object> subMap = removeNodeMap(ruleIndex, stackLevel);
			// Part of a window function
			subMap.remove("Type");
			Map<String, Object> item = new HashMap<String, Object>();
			item.put(PSS_ORDERBY_KEY, subMap);

			addToParent(parentRuleIndex, parentStackLevel, item);
		} else
			// Normal order by clause
			handlePushDown(ruleIndex);
	}


	@Override
	public void exitLimit_clause(@NotNull SQLSelectParserParser.Limit_clauseContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		int parentRuleIndex = ctx.getParent().getRuleIndex();

		Integer stackLevel = currentStackLevel(ruleIndex);
		Integer parentStackLevel = currentStackLevel(parentRuleIndex);

		Map<String, Object> subMap = getNodeMap(ruleIndex, stackLevel);

		handlePushDown(ruleIndex);
	}


	@Override
	public void exitSearch_condition(@NotNull SQLSelectParserParser.Search_conditionContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		int parentRuleIndex = ctx.getParent().getRuleIndex();

		Integer stackLevel = currentStackLevel(ruleIndex);
		Integer parentStackLevel = currentStackLevel(parentRuleIndex);

		Map<String, Object> subMap = getNodeMap(ruleIndex, stackLevel);

		handleOneChild(ruleIndex);
	}

	@Override
	public void exitOr_predicate(@NotNull SQLSelectParserParser.Or_predicateContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		String key = PSS_OR_KEY;

		handleOperandList(ruleIndex, key);
	}

	@Override
	public void exitAnd_predicate(@NotNull SQLSelectParserParser.And_predicateContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		String key = PSS_AND_KEY;

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
	public void exitBasic_predicate_clause(@NotNull SQLSelectParserParser.Basic_predicate_clauseContext ctx) {
		// {condition={left={substitution={name=<subject code>,
		// type=predicand}}, operator=is true}}
		int ruleIndex = ctx.getRuleIndex();
		Integer stackLevel = currentStackLevel(ruleIndex);
		Map<String, Object> subMap = getNodeMap(ruleIndex, stackLevel);
		Object type = subMap.remove("Type");

		if (subMap.size() == 1) {
			Map<String, Object> left = (Map<String, Object>) subMap.remove("1");
			subMap.putAll(left);
			showTrace(parseTrace, "Clause: " + subMap);
		} else if (subMap.size() == 2) {
			// TODO: Grammar peculiarity results in Substitution Variable
			// mislabelled as a condition when it should be a predicand.
			// Fixing it here
			Map<String, Object> item = (Map<String, Object>) subMap.remove("1");
			if (item.containsKey(PSS_SUBSTITUTION_KEY)) {
				HashMap<String, Object> hold = (HashMap<String, Object>) item.get(PSS_SUBSTITUTION_KEY);
				hold.put(PSS_TYPE_KEY, "predicand");
				substitutionsMap.put((String) hold.get("name"), "predicand");
			}
			HashMap<String, Object> hold = new HashMap<String, Object>();
			hold.put(PSS_LEFT_FACTOR_KEY, item);
			subMap.put(PSS_CONDITION_KEY, hold);

			item = (Map<String, Object>) subMap.remove("2");
			subMap.putAll(item);
			showTrace(parseTrace, "Clause: " + subMap);

		} else {
			showTrace(parseTrace, "Wrong number of entries: " + subMap);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void exitSubstitution_predicate(@NotNull SQLSelectParserParser.Substitution_predicateContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		int parentRuleIndex = ctx.getParent().getRuleIndex();

		Integer stackLevel = currentStackLevel(ruleIndex);
		Map<String, Object> subMap = getNodeMap(ruleIndex, stackLevel);
		Object type = subMap.remove("Type");

		if (subMap.size() == 1) {
			Map<String, Object> left = (Map<String, Object>) subMap.remove("1");
			subMap.putAll(left);
			// If the clause remaining is an embedded Condition Substitution
			// Variable, this captures and labels it
			subMap = checkForSubstitutionVariable(subMap, PSS_CONDITION_KEY);
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
			Map<String, Object> condition = new HashMap<String, Object>();
			Map<String, Object> left = (Map<String, Object>) subMap.remove("1");
			condition.put(PSS_LEFT_FACTOR_KEY, left);

			condition.putAll((Map<String, Object>) subMap.remove("2"));

			subMap.put(PSS_CONDITION_KEY, condition);
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
			subMap.put(PSS_OPERATOR_KEY, ctx.getChild(0).getText() + " " + ctx.getChild(1).getText());
		} else if (ctx.getChildCount() == 3) {
			subMap.put(PSS_OPERATOR_KEY,
					ctx.getChild(0).getText() + " " + ctx.getChild(1).getText() + " " + ctx.getChild(2).getText());
		}
		// Add item to parent map
		addToParent(parentRuleIndex, parentStackLevel, subMap);
	}

	@Override
	public void exitIs_clause(@NotNull SQLSelectParserParser.Is_clauseContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		int parentRuleIndex = ctx.getParent().getRuleIndex();

		Integer stackLevel = currentStackLevel(ruleIndex);
		Integer parentStackLevel = currentStackLevel(parentRuleIndex);

		Map<String, Object> subMap = removeNodeMap(ruleIndex, stackLevel);

		// Make a new submap and fill it by reading the tokens directly
		subMap = new HashMap<String, Object>();

		if (ctx.getChildCount() == 2) {
			subMap.put(PSS_OPERATOR_KEY, ctx.getChild(0).getText() + " " + ctx.getChild(1).getText());
		} else if (ctx.getChildCount() == 3) {
			subMap.put(PSS_OPERATOR_KEY,
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
			subMap.put(PSS_PARENTHESES_KEY, item);
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
			Map<String, Object> condition = new HashMap<String, Object>();

			Object operator = subMap.remove("2");
			if (operator instanceof String)
				condition.put(PSS_OPERATOR_KEY, operator);
			else
				condition.put(PSS_OPERATOR_KEY, ((HashMap<String, String>) operator).get("1"));

			Map<String, Object> left = checkForSubstitutionVariable((Map<String, Object>) subMap.remove("1"),
					"predicand");
			condition.put(PSS_LEFT_FACTOR_KEY, left);

			Map<String, Object> right = checkForSubstitutionVariable((Map<String, Object>) subMap.remove("3"),
					"predicand");
			condition.put(PSS_RIGHT_FACTOR_KEY, right);

			subMap.put(PSS_CONDITION_KEY, condition);
			showTrace(parseTrace, "Comparison: " + subMap);

		} else {
			showTrace(parseTrace, "Wrong number of entries: " + subMap);
		}
	}

	@Override
	public void exitComparison_operator(@NotNull SQLSelectParserParser.Comparison_operatorContext ctx) {
		int ruleIndex = ctx.getRuleIndex();

		Integer stackLevel = currentStackLevel(ruleIndex);
		Map<String, Object> subMap = getNodeMap(ruleIndex, stackLevel);
		subMap.remove("Type");

		if (subMap.size() == 1) {
			showTrace(parseTrace, "Comparison Operator: " + subMap);
		} else if (subMap.size() == 2) {
			showTrace(parseTrace, "Comparison Operator: " + subMap);
			String notvar = (String) subMap.remove("1");
			String operator = (String) subMap.remove("2");
			subMap.put("1", notvar + '_' + operator);
		} else {
			showTrace(parseTrace, "Wrong number of entries: " + subMap);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void exitBetween_predicate(@NotNull SQLSelectParserParser.Between_predicateContext ctx) {
		// RULE_between_predicate
		int ruleIndex = ctx.getRuleIndex();
		Integer stackLevel = currentStackLevel(ruleIndex);
		Map<String, Object> subMap = getNodeMap(ruleIndex, stackLevel);
		Object type = subMap.remove("Type");

		if (subMap.size() >= 3) {
			showTrace(parseTrace, "Bewteen: " + subMap);
			Map<String, Object> condition = new HashMap<String, Object>();
			condition.put(PSS_ITEM_KEY, subMap.remove("1"));
			String itemKey = PSS_RANGE_BEGIN_KEY;

			Object operator = subMap.remove("2");
			if (operator instanceof String)
				if (operator.equals("not"))
					condition.put(PSS_OPERATOR_KEY, "not between");
				else {
					condition.put(PSS_SYMMETRY_KEY, operator);
					condition.put(PSS_OPERATOR_KEY, PSS_BETWEEN_KEY);
				}
			else {
				operator = checkForSubstitutionVariable((Map<String, Object>) operator, "predicand");
				condition.put(itemKey, operator);
				itemKey = PSS_RANGE_END_KEY;
				condition.put(PSS_OPERATOR_KEY, PSS_BETWEEN_KEY);
				condition.put(PSS_SYMMETRY_KEY, null);
			}

			operator = subMap.remove("3");
			if (operator instanceof String)
				condition.put(PSS_SYMMETRY_KEY, operator);
			else {
				if (!condition.containsKey(PSS_SYMMETRY_KEY))
					condition.put(PSS_SYMMETRY_KEY, null);
				operator = checkForSubstitutionVariable((Map<String, Object>) operator, "predicand");
				condition.put(itemKey, operator);
				if (itemKey.equals(PSS_RANGE_BEGIN_KEY))
					itemKey = PSS_RANGE_END_KEY;
				else
					itemKey = "stop";
			}

			if (itemKey.equals(PSS_RANGE_BEGIN_KEY)) {
				operator = subMap.remove("4");
				condition.put(itemKey, operator);
				operator = subMap.remove("5");
				condition.put(PSS_RANGE_END_KEY, operator);
			} else if (itemKey.equals("end")) {
				operator = subMap.remove("4");
				condition.put(itemKey, operator);
			}

			subMap.put(PSS_BETWEEN_KEY, condition);
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
			subMap.put(PSS_ITEM_KEY, subMap.remove("1"));
			subMap.put(PSS_IN_LIST_KEY, subMap.remove("2"));
		} else if (subMap.size() == 3) {
			showTrace(parseTrace, "In predicate: " + subMap);
			subMap.put(PSS_ITEM_KEY, subMap.remove("1"));
			subMap.remove("2");
			subMap.put(PSS_NOT_IN_LIST_KEY, subMap.remove("3"));
		} else {
			showTrace(parseTrace, "Wrong number of entries: " + subMap);
		}

		Map<String, Object> item = new HashMap<String, Object>();
		item.put(PSS_IN_KEY, subMap);

		int parentRuleIndex = ctx.getParent().getRuleIndex();
		Integer parentStackLevel = currentStackLevel(parentRuleIndex);
		addToParent(parentRuleIndex, parentStackLevel, item);
	}

	@Override
	public void exitIn_predicate_value(@NotNull SQLSelectParserParser.In_predicate_valueContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		Integer stackLevel = currentStackLevel(ruleIndex);
		int parentRuleIndex = ctx.getParent().getRuleIndex();
		Map<String, Object> subMap = getNodeMap(ruleIndex, stackLevel);
		Map<String, Object> reference = checkForSubstitutionVariable((Map<String, Object>) subMap.get("1"), "in_list");

		handleOneChild(ruleIndex);
	}

	@Override
	public void exitIn_value_list(@NotNull SQLSelectParserParser.In_value_listContext ctx) {
		int ruleIndex = ctx.getRuleIndex();

		Integer stackLevel = currentStackLevel(ruleIndex);
		Map<String, Object> subMap = removeNodeMap(ruleIndex, stackLevel);
		subMap.remove("Type");
		Map<String, Object> item = new HashMap<String, Object>();
		item.put(PSS_LIST_KEY, subMap);

		int parentRuleIndex = ctx.getParent().getRuleIndex();
		Integer parentStackLevel = currentStackLevel(parentRuleIndex);
		addToParent(parentRuleIndex, parentStackLevel, item);
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
				left.put(PSS_LITERAL_KEY, "-1");
				Map<String, Object> item = new HashMap<String, Object>();
				item.put(PSS_LEFT_FACTOR_KEY, left);
				item.put(PSS_OPERATOR_KEY, "*");
				item.put(PSS_RIGHT_FACTOR_KEY, subMap.remove("2"));
				Map<String, Object> calc = new HashMap<String, Object>();
				calc.put(PSS_CALCULATION_KEY, item);
				subMap.put("1", calc);
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
	public void exitAdditive_expression(@NotNull SQLSelectParserParser.Additive_expressionContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		Integer stackLevel = currentStackLevel(ruleIndex);
		Map<String, Object> subMap = removeNodeMap(ruleIndex, stackLevel);
		subMap.remove("Type");

		if (subMap.size() == 1) {
			subMap.putAll((Map<String, Object>) subMap.remove("1"));
		} else if (subMap.size() >= 2) {
			Map<String, Object> item = new HashMap<String, Object>();
			int indx = 1;
			for (int x = 1; subMap.size() > 0; x = x + 2) {
				Map<String, Object> calc = new HashMap<String, Object>();
				if (x == 1)
					calc.put(PSS_LEFT_FACTOR_KEY, subMap.remove("" + indx++));
				else {
					calc.put(PSS_LEFT_FACTOR_KEY, item);
					item = new HashMap<String, Object>();
				}
				calc.put(PSS_RIGHT_FACTOR_KEY, subMap.remove("" + indx++));
				calc.put(PSS_OPERATOR_KEY, ctx.getChild(x).getText());
				item.put(PSS_CALCULATION_KEY, calc);
			}

			subMap = item;
		} else {
			showTrace(parseTrace, "Too many entries: " + subMap);
		}
		collect(ruleIndex, stackLevel, subMap);
	}

	@Override
	public void exitMultiplicative_expression(@NotNull SQLSelectParserParser.Multiplicative_expressionContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		Integer stackLevel = currentStackLevel(ruleIndex);
		Map<String, Object> subMap = removeNodeMap(ruleIndex, stackLevel);
		subMap.remove("Type");

		if (subMap.size() == 1) {
			subMap.putAll((Map<String, Object>) subMap.remove("1"));
		} else if (subMap.size() >= 2) {
			Map<String, Object> item = new HashMap<String, Object>();
			int indx = 1;
			for (int x = 1; subMap.size() > 0; x = x + 2) {
				Map<String, Object> calc = new HashMap<String, Object>();
				if (x == 1)
					calc.put(PSS_LEFT_FACTOR_KEY, subMap.remove("" + indx++));
				else {
					calc.put(PSS_LEFT_FACTOR_KEY, item);
					item = new HashMap<String, Object>();
				}
				calc.put(PSS_RIGHT_FACTOR_KEY, subMap.remove("" + indx++));
				calc.put(PSS_OPERATOR_KEY, ctx.getChild(x).getText());
				item.put(PSS_CALCULATION_KEY, calc);
			}

			subMap = item;
		} else {
			showTrace(parseTrace, "Too many entries: " + subMap);
		}
		collect(ruleIndex, stackLevel, subMap);
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
	public void exitString_value_expression(@NotNull SQLSelectParserParser.String_value_expressionContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		handleOperandList(ruleIndex, PSS_CONCATENATE_KEY);
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
			item.put(PSS_FUNCTION_NAME_KEY, subMap.remove("1"));
			item.put(PSS_PARAMETERS_KEY, subMap.remove("2"));
			subMap.put(PSS_FUNCTION_KEY, item);
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
			item.put(PSS_TRIM_CHARACTER_KEY, subMap.remove("1"));
			item.put(PSS_VALUE_KEY, subMap.remove("2"));

		} else if (subMap.size() == 3) {
			item.put(PSS_QUALIFIER_KEY, subMap.remove("1"));
			item.put(PSS_TRIM_CHARACTER_KEY, subMap.remove("2"));
			item.put(PSS_VALUE_KEY, subMap.remove("3"));
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
			item.put(PSS_TRIM_CHARACTER_KEY, subMap.remove("2"));
			item.put(PSS_VALUE_KEY, subMap.remove("1"));
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
			item.putAll((Map<? extends String, ? extends Object>) subMap.remove("1"));
			subMap = (Map<String, Object>) subMap.remove("2");
			type = subMap.remove("Type");
			item.put(PSS_PARAMETERS_KEY, subMap.remove(type.toString()));
			subMap.put(PSS_FUNCTION_KEY, item);
		} else {
			showTrace(parseTrace, "Wrong number of entries: " + subMap);
		}
		showTrace(parseTrace, "Function: " + subMap);
		addToParent(parentRuleIndex, parentStackLevel, subMap);
	}

	@Override
	public void exitFunction_name(@NotNull SQLSelectParserParser.Function_nameContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		Integer stackLevel = currentStackLevel(ruleIndex);
		Map<String, Object> subMap = getNodeMap(ruleIndex, stackLevel);
		Object type = subMap.remove("Type");

		if (subMap.size() == 1) {
			showTrace(parseTrace, "Just One Identifier: " + subMap);
			String functName = (String) subMap.remove("1");
			subMap.put(PSS_FUNCTION_NAME_KEY, functName);
			showTrace(parseTrace, "function_name: " + functName + " Map: " + subMap);
		} else if (subMap.size() == 2) {
			showTrace(parseTrace, "Two entries: " + subMap);
			String schema = (String) subMap.remove("1");
			subMap.put(PSS_SCHEMA_KEY, schema);
			String functName = (String) subMap.remove("2");
			subMap.put(PSS_FUNCTION_NAME_KEY, functName);
			showTrace(parseTrace, "Schema: " + schema + " function_name: " + functName + " Map: " + subMap);
		} else {
			showTrace(parseTrace, "Too many entries: " + subMap);
		}
	}

	@Override
	public void exitSql_argument_list(@NotNull SQLSelectParserParser.Sql_argument_listContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		int stackLevel = currentStackLevel(ruleIndex);
		Map<String, Object> subMap = getNodeMap(ruleIndex, stackLevel);
		handlePushDown(ruleIndex);
	}

	@Override
	public void exitValue_expression(@NotNull SQLSelectParserParser.Value_expressionContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		int parentRuleIndex = ctx.getParent().getRuleIndex();
		if (parentRuleIndex == (Integer) SQLSelectParserParser.RULE_sql_argument_list) {
			Integer stackLevel = currentStackLevel(ruleIndex);
			Map<String, Object> subMap = getNodeMap(ruleIndex, stackLevel);
			subMap.remove("Type");

			Map<String, Object> valueExpression = null;
			String tableRef = null;
			String name = null;
			Boolean doNotSkip = true;

			if (subMap.size() == 1) {
				showTrace(parseTrace, "Just One Identifier: " + subMap);
				// Get first item, record if it is a Substitution Variable by
				// adding the Substitution List
				valueExpression = checkForSubstitutionVariable((Map<String, Object>) subMap.remove("1"), "predicand");

				// Get Value Expression entry
				HashMap<String, Object> node = (HashMap<String, Object>) valueExpression.get(PSS_COLUMN_KEY);
				if (node == null)
					node = (HashMap<String, Object>) valueExpression.get(PSS_SUBSTITUTION_KEY);
				if (node != null) {
					if (node.containsKey(PSS_TABLE_REF_KEY))
						// Value is associated with a table
						tableRef = (String) node.get(PSS_TABLE_REF_KEY);
					if (node.containsKey(PSS_NAME_KEY))
						// Value Expression is a column or substitution, use its
						// name
						name = (String) node.get(PSS_NAME_KEY);
					else if (node.containsKey(PSS_SUBSTITUTION_KEY))
						// then Value Expression is a COLUMN Substitution
						// Variable, get the variable's name
						name = (String) ((HashMap<String, Object>) node.get(PSS_SUBSTITUTION_KEY)).get("name");
				}

			} else {
				showTrace(parseTrace, "Too many entries: " + subMap);
				doNotSkip = false;
			}
			if (doNotSkip) {
				// Capture SymbolTable entry
				collectSymbolTableItem(tableRef, valueExpression, ctx.getStart());
				// Add column to SQL AST Tree
				subMap.putAll(valueExpression);
			}
			showTrace(parseTrace, "Column Reference: " + subMap);
		} else if ((parentRuleIndex == (Integer) SQLSelectParserParser.RULE_search_condition)
				|| (parentRuleIndex == (Integer) SQLSelectParserParser.RULE_parenthesized_value_expression)
				|| (parentRuleIndex == (Integer) SQLSelectParserParser.RULE_condition_value)) {
			Integer stackLevel = currentStackLevel(ruleIndex);
			Map<String, Object> subMap = getNodeMap(ruleIndex, stackLevel);
			subMap = (Map<String, Object>) subMap.get("1");
			// Get first item, record if it is a Substitution Variable by
			// adding the Substitution List - This captures when the entire
			// condition is a Substitution Variable alone
			subMap = checkForSubstitutionVariable((Map<String, Object>) subMap, PSS_CONDITION_KEY);

			// NOW handle the child
			handleOneChild(ruleIndex);
		} else if ((parentRuleIndex == (Integer) SQLSelectParserParser.RULE_case_expression)
				|| (parentRuleIndex == (Integer) SQLSelectParserParser.RULE_when_value_clause)
				|| (parentRuleIndex == (Integer) SQLSelectParserParser.RULE_case_result)) {
			Integer stackLevel = currentStackLevel(ruleIndex);
			Map<String, Object> subMap = getNodeMap(ruleIndex, stackLevel);
			subMap = (Map<String, Object>) subMap.get("1");
			// Get first item, record if it is a Substitution Variable by
			// adding the Substitution List - This captures when the entire
			// condition is a Substitution Variable alone
			subMap = checkForSubstitutionVariable((Map<String, Object>) subMap, "predicand");

			// NOW handle the child
			handleOneChild(ruleIndex);
		} else if ((parentRuleIndex == (Integer) SQLSelectParserParser.RULE_aggregate_function)
				|| (parentRuleIndex == (Integer) SQLSelectParserParser.RULE_trim_operands)) {
			// Trim and Aggregate Function Parameter
			Integer stackLevel = currentStackLevel(ruleIndex);
			Map<String, Object> subMap = getNodeMap(ruleIndex, stackLevel);
			subMap = (Map<String, Object>) subMap.get("1");
			// Get first item, record if it is a Substitution Variable by
			// adding the Substitution List - This captures when the entire
			// condition is a Substitution Variable alone
			subMap = checkForSubstitutionVariable((Map<String, Object>) subMap, "predicand");

			// NOW handle the child
			handleOneChild(ruleIndex);
			//
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
			item.put(PSS_PREDICAND_KEY, subMap.remove("1"));
			item.put(PSS_SORT_ORDER_KEY, "ASC");
			item.put(PSS_NULL_ORDER_KEY, null);
			showTrace(parseTrace, "One Entry: " + item);

		} else if (subMap.size() == 2) {
			item.put(PSS_PREDICAND_KEY, subMap.remove("1"));
			item.put(PSS_SORT_ORDER_KEY, subMap.remove("2"));
			item.put(PSS_NULL_ORDER_KEY, null);
			showTrace(parseTrace, "Two entries: " + item);

		} else if (subMap.size() == 3) {
			item.put(PSS_PREDICAND_KEY, subMap.remove("1"));
			item.put(PSS_SORT_ORDER_KEY, subMap.remove("2"));
			Map<String, Object> hold = (Map<String, Object>) subMap.remove("3");
			type = hold.remove("Type").toString();
			item.put(PSS_NULL_ORDER_KEY, ((HashMap<String, Object>) hold.get(type)).get("1"));
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
	public void exitNull_ordering(@NotNull SQLSelectParserParser.Null_orderingContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		handlePushDown(ruleIndex);
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
		int parentRuleIndex = ctx.getParent().getRuleIndex();
		Integer stackLevel = currentStackLevel(ruleIndex);
		Map<String, Object> subMap = getNodeMap(ruleIndex, stackLevel);
		// Get first item, record if it is a Substitution Variable by
		// adding the Substitution List
		Map<String, Object> substitutionPredicand = checkForSubstitutionVariable((Map<String, Object>) subMap.get("1"),
				"predicand");

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
			subMap.put(PSS_LITERAL_KEY, item);
		} else {
			showTrace(parseTrace, "Too many entries: " + subMap);
		}
		showTrace(parseTrace, "Unsigned Literal: " + subMap);
	}

	@Override
	public void exitReal_number(@NotNull SQLSelectParserParser.Real_numberContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		handleOneChild(ruleIndex);
	}

	@Override
	public void exitReal_number_def(@NotNull SQLSelectParserParser.Real_number_defContext ctx) {
		// Tell master exit that the full text is the value
		useAsLeaf = true;
	}

	@Override
	public void exitExponent(@NotNull SQLSelectParserParser.ExponentContext ctx) {
		// Tell master exit that the full text is the value
		useAsLeaf = true;
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
	public void exitVariable_identifier(@NotNull SQLSelectParserParser.Variable_identifierContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		handleOneChild(ruleIndex);
	}

	@Override
	public void exitNull_literal(@NotNull SQLSelectParserParser.Null_literalContext ctx) {
		HashMap<String, Object> item = new HashMap<String, Object>();
		item.put(PSS_NULL_LITERAL_KEY, "null");

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
				+ SQLSelectParserParser.ruleNames[ruleIndex] + ": " + sqlTree);
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

		Object skip = sqlTree.remove("SKIP");
		if (skip == null) {
			if (useAsLeaf) {
				item = ctx.getText();
				removeNode(ruleIndex, stackLevel);
				useAsLeaf = false;
			} else if (ctx.getChildCount() == 1)
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
				showTrace(parseTrace, sqlTree);
			}
		}

		popStack(ruleIndex);
		showTrace(parseTrace, "EXIT " + makeMapIndex(ruleIndex, stackLevel) + ": "
				+ SQLSelectParserParser.ruleNames[ruleIndex] + ": " + sqlTree);
		showTrace(parseTrace, "");
	}

	@Override
	public void visitTerminal(@NotNull TerminalNode node) {
	}

	@Override
	public void visitErrorNode(@NotNull ErrorNode node) {
	}
}
