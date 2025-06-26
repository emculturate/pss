package puml.walker;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.TerminalNodeImpl;

import mumble.PUML3Constants.*;


import puml3.PUML3BaseListener;
import puml3.PUML3Parser;
import sql.SQLSelectParserParser;


public class PUML3EventWalker extends PUML3BaseListener {
	
	final static Boolean showParse = false;
	final static Boolean showSymbols = false;
	final static Boolean showOther = false;
	final static Boolean showResults = true;

	final static Integer parseTrace = 1;
	final static Integer symbolTrace = 2;
	final static Integer otherTrace = 3;
	final static Integer resultTrace = 4;

	private Boolean useAsLeaf = false;

	public PUML3EventWalker() {
		super();
	}

		/**
	 * Depth of token stack
	 * HashMap keeps track of the nesting depth for each grammar rule during parsing. 
	 * It's a crucial component of the parser's context management system that enables 
	 * processing of recursive SQL structures.
	 * Specifically, stackTree maps:
	 * 		Keys: Integer rule indices (from the ANTLR parser)
	 * 		Values: Integer nesting levels for each rule
	 * This allows the parser to:
	 *  1. Track how deeply nested each grammar rule is within the parse tree
	 *  2. Generate unique keys for AST nodes using makeMapIndex(ruleIndex, stackLevel)
	 *  3. Associate child elements with the correct parent nodes
	 *  4. Handle recursive rule patterns that appear at multiple levels in SQL queries
	 * For example, when processing nested subqueries, stackTree ensures that references
	 * to columns, tables, and expressions are properly associated with their respective 
	 * scopes, even when the same rule (like query_expression) appears at different levels of nesting.
	 */
	private HashMap<Integer, Integer> stackTree = new HashMap<Integer, Integer>();


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


	public HashMap<String, Object> getSqlTree() {
		return sqlTree;
	}

	public HashMap<String, Object> getTableColumnMap() {
		return tableDictionaryMap;
	}

	public HashMap<String, Object> getSymbolTable() {
		return symbolTable;
	}


	public HashMap<String, Object> getSubstitutionsMap() {
		return substitutionsMap;
	}

	public HashSet<String> getInterface() {
		// TODO: When a query has a with, the interface can appear in anyone of
		// the symbol table queries, because it will be a list.
		HashSet<String> interfac = new HashSet<String>();
	
		return interfac;
	}

		/**
	 * Method checks with the level of trace indicated by the calling method
	 * and if the trace is enabled, it will print the trace to the console.
	 * This is an in-class built logging capability used for debugging only.
	 * 
	 * @param trace
	 */
	private void showTrace(Integer traceType, Object trace) {
		if (traceType.equals(parseTrace) && showParse)
			System.out.println(trace);
		if (traceType.equals(symbolTrace) && showSymbols)
			System.out.println(trace);
		if (traceType.equals(resultTrace) && showResults)
			System.out.println(trace);
		if (traceType.equals(otherTrace) && showOther)
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

	/**
	 * Add level map to SQLTree AST by ruleIndex and stackLevel
	 **/
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

	/*****************************************************************************************************
	 *
	 * Common Parser Rule Entry and Exit Methods - Generically Useful for Any Grammar Walker 
	 * 
	 * This method is called automatically by ANTLR when entering any rule in the grammar. It 
	 * 1. Pushes the current rule onto the stack to track nesting level 
	 * 2. Creates an initial AST node for the rule
	 * 3. Logs tracing information if enabled
	 * 
	 * The Parser stream is traversed in a depth-first manner, and each rule is entered 
	 * before its custom methods are processed. Most rules only need an Exit method to
	 * finish.
	 * 
	 * When the specific Exit method for a particular rule is called, the information
	 * collected has been "collected" into the RULE MAP in a raw form. The Exit method
	 * will then process the raw information and add it to the SQL AST Tree, editing and 
	 * adjusting the raw format data from the Parser stream into the final structure 
	 * defined for the SQL AST Tree.
	 */
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
				+ PUML3Parser.ruleNames[ruleIndex] + ": " + sqlTree);
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

	/**
	 * This method is called automatically by ANTLR when exiting any rule in the grammar. It
	 * 1. Pops the current rule from the stack to track nesting level
	 * 2. Removes the current rule's AST node from the SQL AST Tree
	 * 3. Processes the collected information from the rule's context
	 * 4. Adds the processed information to the parent rule's AST node
	 * 5. Logs tracing information if enabled
	 */

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

	}

	/*
	===============================================================================
	  SQL Tree Start Symbol
	===============================================================================
	*/

	@Override
	public void exitEquation(@NotNull PUML3Parser.EquationContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		Integer stackLevel = currentStackLevel(ruleIndex);
		Map<String, Object> subMap = removeNodeMap(ruleIndex, stackLevel);
		Object type = subMap.remove("Type");
		sqlTree.put("EQUATION", subMap.remove("1"));
		// showTrace(resultTrace, collector);
		showTrace(symbolTrace, symbolTable);
		showTrace(symbolTrace, tableDictionaryMap);
	}


}
