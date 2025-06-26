package sql.walker;
/*
 * SqlParseEventWalker Class Purpose
 * ---------------------------------
 * This class is a listener-based SQL parser implementation that builds an abstract syntax tree (AST)
 * representation of SQL queries. It extends SQLSelectParserBaseListener and processes events triggered
 * during the parsing of SQL text. The parser converts the SQL structure into a nested HashMap-based 
 * tree representation called a "Mumble AST" that can be easily manipulated programmatically.
 * 
 * Key Features:
 * 1. Constructs a hierarchical map representation of SQL statements
 * 2. Maintains symbol tables for identifiers, columns, and tables
 * 3. Tracks substitution variables for parameterized queries
 * 4. Supports nested queries, joins, and complex expressions
 * 5. Creates data dictionaries mapping tables to their columns
 * 6. Provides methods to retrieve and manipulate the resulting SQL AST
 */

import static mumble.MumbleConstants.*;


import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.antlr.v4.runtime.ParserRuleContext;

import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.antlr.v4.runtime.tree.TerminalNodeImpl;

import access.Snippet;
import astwalkers.SqlASTWalkerHelper;

import sql.SQLSelectParserBaseListener;
import sql.SQLSelectParserParser;
/**
 * Primary Listener Class; The class accepts events from the parse project's 
 * Base Parser Listener and creates a nested Hashmap Abstract Tree of the SQL.
 * 
 * As the Parser is called to parse a string, it will send events to this class for each recognized
 * statement in the grammar. This class contains individual methods for each type of event, pre- and -post- 
 * encounter by the Parser and it uses the local information from the parse as well as possible children
 * collected into the growing Mumble Abstract Syntax Tree (AST) to build a nested Map structure.
 * 
 * @author geoffreyhowe
 *
 */
@SuppressWarnings("Convert2Diamond")
public class SqlParseEventWalker extends SQLSelectParserBaseListener {


	/**
	 * AST Walker Helper for this instance of the SQL Parse Event Walker
	 */
	private final SqlASTWalkerHelper walker;


	// Constructors
	public SqlParseEventWalker() {
		super();

		// Initialize the walker with the SqlASTWalkerHelper
		this.walker = new SqlASTWalkerHelper();


	}

	// Getters and Setters

	public HashMap<String, Object> getAsTree() {
		return walker.asTree;
	}

	public HashMap<String, Object> getTableColumnMap() {
		return walker.tableDictionaryMap;
	}

	public HashMap<String, Object> getSymbolTable() {
		return walker.symbolTable;
	}

	@SuppressWarnings("unchecked")
	public HashSet<String> getInterface() {
		// TODO: When a query has a with, the interface can appear in anyone of
		// the symbol table queries, because it will be a list.
		HashSet<String> interfac = new HashSet<String>();
		HashMap<String, Object> hold = null;
		if ( walker.symbolTable != null) {
			for (String key :  walker.symbolTable.keySet()) {
				if (key.equals(MUMBLE_WITH_KEY)) {
				} else {
					hold = (HashMap<String, Object>)  walker.symbolTable.get(key);
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
		//TODO: What the hell
		return new Snippet(walker.getAsTree(), walker.tableDictionaryMap,  walker.symbolTable, walker.substitutionsMap, null);
	}
	


	/* ==================================================================================
	 *
	 * SQL Event Walker Helper Methods
	 *
	 * These methods are used to manage the SQL AST and Symbol Table and apply specialized functions
	 * related to the SQL Grammar itself. These are not GENERIC methods so they are placed after
	 * the Standard Parser Event Walker methods. 
	 */
	// Extra-Grammar Identifiers

	/**
	 * Symbol Swap Maps: Table and Column Name Replacement
	 * This is an optional operating mode for the event walker that allows it to
	 * replace table and column names in the SQL AST with alternative names. The SQL AST might
	 * contain logical names for tables and columns that the calling process
	 * wants to replace with physical table and column names.	
	 * This is useful in scenarios where the SQL AST is used to generate
	 * SQL code that needs to reference specific database objects,
	 * such as when generating SQL for a specific database schema or when
	 * the SQL AST is used to generate code for a specific database
	 * implementation.
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
		return walker.substitutionsMap;
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

	
	/*****************************************************************************************************
	 * Grammar Clauses Start Here
	 * 
	 * The following methods act as overrides on the default Walker Exit and Entry logic for each clause.
	 * 
	 */
	
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
		Integer stackLvl = walker.pushStack(ruleIndex);

		if (ctx.getChildCount() == 1)
			if (ctx.getChild(0) instanceof TerminalNodeImpl) {
				// I'm a leaf
			} else {
				walker.collectNewRuleMap(ruleIndex, stackLvl);
			}
		else {
			walker.collectNewRuleMap(ruleIndex, stackLvl);
		}

		walker.showTrace(walker.parseTrace, "Enter " + walker.makeMapIndex(ruleIndex, stackLvl) + ": "
				+ SQLSelectParserParser.ruleNames[ruleIndex] + ": " +  walker.getAsTree());
		walker.showTrace(walker.parseTrace, "");
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
		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Object item = null;

		Object skip =  walker.getAsTree().remove("SKIP");
		if (skip == null) {
			if (walker.useAsLeaf) {
				item = ctx.getText();
				walker.removeNode(ruleIndex, stackLevel);
				walker.useAsLeaf = false;
			} else if (ctx.getChildCount() == 1)
				if (ctx.getChild(0) instanceof TerminalNodeImpl) {
					// I'm a leaf
					item = ctx.getText();
				} else
					item = walker.removeNode(ruleIndex, stackLevel);
			else
				item = walker.removeNode(ruleIndex, stackLevel);

			// Add item to parent map
			if (ctx.getParent() != null) {
				int parentNodeIndex = ctx.getParent().getRuleIndex();
				Integer parentStackIndex = walker.currentStackLevel(parentNodeIndex);
				if (ruleIndex == parentNodeIndex && stackLevel == parentStackIndex) {
					// oddity - in case it appears my parent is myself
					walker.collect(ruleIndex, stackLevel, item);
				} else {
					Map<String, Object> idMap = walker.getNodeMap(parentNodeIndex, parentStackIndex);
					if (idMap == null) {
						walker.showTrace(walker.parseTrace, "EXIT " + walker.makeMapIndex(ruleIndex, stackLevel) + ": "
								+ SQLSelectParserParser.ruleNames[ruleIndex] + ": Missing pMap");
								walker.showTrace(walker.parseTrace, "");
					} else
						idMap.put(((Integer) (idMap.size())).toString(), item);
				}
			} else {
				walker.showTrace(walker.parseTrace,  walker.getAsTree());
			}
		}

		walker.popStack(ruleIndex);

		walker.showTrace(walker.parseTrace, "EXIT " + walker.makeMapIndex(ruleIndex, stackLevel) + ": "
				+ SQLSelectParserParser.ruleNames[ruleIndex] + ": " +  walker.getAsTree());
		walker.showTrace(walker.parseTrace, "");
	}

	/*
	 * These are placeholder methods that will eventually be implemented to handle
	 * Could handle syntax error reporting but is currently not implemented
	 */
	@Override
	public void visitTerminal(@NotNull TerminalNode node) {
	}

	@Override
	public void visitErrorNode(@NotNull ErrorNode node) {
	}

	/******************************************************************************
	 * 
	 * RULE EXIT METHODS
	 * 
	 * Below are methods specific to the SQLSelectParser Grammar.
	 * They are called by the ANTLR Parser when the Walker is traversing the
	 * parse tree.
	 */
	/*
	===============================================================================
	  Start Statements: SQL, Condition, Predicand and Literal
	  Parser End Points: These are independently callable and produce complete set 
	  of objects for each call. 
	===============================================================================
	*/

	/*
	===============================================================================
	  SQL Tree Start Symbol
	===============================================================================
	*/

	@Override
	public void exitSql(@NotNull SQLSelectParserParser.SqlContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Map<String, Object> subMap = walker.removeNodeMap(ruleIndex, stackLevel);
		Object type = subMap.remove("Type");
		 walker.getAsTree().put(MUMBLE_SQL_TREE_KEY, subMap.remove("1"));
		// walker.showTrace(resultTrace, collector);
		walker.showTrace(walker.symbolTrace,  walker.symbolTable);
		walker.showTrace(walker.symbolTrace,  walker.tableDictionaryMap);
	}

	/*
	===============================================================================
	  Column Start Symbol
	===============================================================================
	*/
	@Override
	public void exitColumn_value(SQLSelectParserParser.Column_valueContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Map<String, Object> subMap = walker.removeNodeMap(ruleIndex, stackLevel);
		Object type = subMap.remove("Type");
		 walker.getAsTree().put(MUMBLE_COLUMN_TREE_KEY, subMap.remove("1"));
		// walker.showTrace(resultTrace, collector);
		walker.showTrace(walker.symbolTrace,  walker.symbolTable);

		walker.addTableReferencesToTableDictionary();

		walker.showTrace(walker.symbolTrace,  walker.tableDictionaryMap);
	}

	/*
	===============================================================================
	  Predicand Start Symbol
	===============================================================================
	*/
	@Override
	public void exitPredicand_value(@NotNull SQLSelectParserParser.Predicand_valueContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Map<String, Object> subMap = walker.removeNodeMap(ruleIndex, stackLevel);
		Object type = subMap.remove("Type");
		 walker.getAsTree().put(MUMBLE_PREDICAND_TREE_KEY, subMap.remove("1"));
		// walker.showTrace(resultTrace, collector);
		walker.showTrace(walker.symbolTrace,  walker.symbolTable);

		walker.addTableReferencesToTableDictionary();

		walker.showTrace(walker.symbolTrace,  walker.tableDictionaryMap);
	}
	

	/*
	===============================================================================
	  In List Start Symbol
	===============================================================================
	*/
	@Override
	public void exitIn_list_predicate_value(SQLSelectParserParser.In_list_predicate_valueContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Map<String, Object> subMap = walker.removeNodeMap(ruleIndex, stackLevel);
		Object type = subMap.remove("Type");
		 walker.getAsTree().put(MUMBLE_IN_LIST_TREE_KEY, subMap.remove("1"));
		// walker.showTrace(resultTrace, collector);
		walker.showTrace(walker.symbolTrace,  walker.symbolTable);

		walker.addTableReferencesToTableDictionary();

		walker.showTrace(walker.symbolTrace,  walker.tableDictionaryMap);
	}


	/*
	===============================================================================
	  Condition Start Symbol
	===============================================================================
	*/
	@Override
	public void exitCondition_value(SQLSelectParserParser.Condition_valueContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Map<String, Object> subMap = walker.removeNodeMap(ruleIndex, stackLevel);
		Object type = subMap.remove("Type");
		 walker.getAsTree().put(MUMBLE_CONDITION_TREE_KEY, subMap.remove("1"));
		// walker.showTrace(resultTrace, collector);
		walker.showTrace(walker.symbolTrace,  walker.symbolTable);

		walker.addTableReferencesToTableDictionary();

		walker.showTrace(walker.symbolTrace,  walker.tableDictionaryMap);
	}

	/*
	===============================================================================
	  Tuple Start Symbol
	===============================================================================
	*/
	@Override
	public void exitTuple_value(SQLSelectParserParser.Tuple_valueContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Map<String, Object> subMap = walker.removeNodeMap(ruleIndex, stackLevel);
		Object type = subMap.remove("Type");
		HashMap<String, Object> item = new HashMap<String, Object> ();
		if (subMap.size() == 1) {
			// normal TUPLE value
			item.putAll((HashMap<String, Object>) subMap.remove("1"));
			
		} else if (subMap.size() == 2) {
			// Only VALUES statement with values and alias
			item.putAll((HashMap<String, Object>) subMap.remove("1"));
			HashMap<String, Object> hold = (HashMap<String, Object>) item.get(MUMBLE_VALUES_KEY);
			hold.putAll((HashMap<String, Object>)  subMap.remove("2"));
		
		} else if (subMap.size() == 3) {
			// Only VALUES statement with values alias and column list
			item.putAll((HashMap<String, Object>) subMap.remove("1"));
			HashMap<String, Object> hold = (HashMap<String, Object>) item.get(MUMBLE_VALUES_KEY);
			hold.putAll((HashMap<String, Object>)  subMap.remove("2"));
			hold.put(MUMBLE_COLUMNS_KEY, (HashMap<String, Object>)  subMap.remove("3"));
		} else {	
			walker.showTrace(walker.parseTrace, "Wrong number of entries: " + subMap);
		}

		 walker.getAsTree().put(MUMBLE_TUPLE_TREE_KEY, item);
		
		// walker.showTrace(resultTrace, collector);
		walker.showTrace(walker.symbolTrace,  walker.symbolTable);
		walker.showTrace(walker.symbolTrace,  walker.tableDictionaryMap);
	}

	/*
	===============================================================================
	  Query Value Start Symbol
	===============================================================================
	*/
	@Override
	public void exitQuery_value(SQLSelectParserParser.Query_valueContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Map<String, Object> subMap = walker.removeNodeMap(ruleIndex, stackLevel);
		Object type = subMap.remove("Type");
		 walker.getAsTree().put(MUMBLE_QUERY_TREE_KEY, subMap.remove("1"));
		// walker.showTrace(resultTrace, collector);
		walker.showTrace(walker.symbolTrace,  walker.symbolTable);

		// Add TABLE references to Table Dictionary
		walker.addTableReferencesToTableDictionary();

		walker.showTrace(walker.symbolTrace,  walker.tableDictionaryMap);
	}

	/*
	===============================================================================
	  Join Extension Value Start Symbol
	===============================================================================
	*/
	@Override
	public void exitJoin_extension_value(SQLSelectParserParser.Join_extension_valueContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Map<String, Object> subMap = walker.removeNodeMap(ruleIndex, stackLevel);
		Object type = subMap.remove("Type");
		 walker.getAsTree().put(MUMBLE_JOIN_EXTENSION_TREE_KEY, subMap.remove("1"));
		// walker.showTrace(resultTrace, collector);
		walker.showTrace(walker.symbolTrace,  walker.symbolTable);
		walker.showTrace(walker.symbolTrace,  walker.tableDictionaryMap);
	}
	

	/*
	===============================================================================
	  Values Start Symbol
	===============================================================================
	*/
	@Override
	public void exitValues_statement_end(SQLSelectParserParser.Values_statement_endContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Map<String, Object> subMap = walker.removeNodeMap(ruleIndex, stackLevel);
		Object type = subMap.remove("Type");
		 walker.getAsTree().put(MUMBLE_VALUES_TREE_KEY, subMap.remove("1"));
		// walker.showTrace(resultTrace, collector);
		walker.showTrace(walker.symbolTrace,  walker.symbolTable);
		walker.showTrace(walker.symbolTrace,  walker.tableDictionaryMap);
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

	  The remaining methods represent internal Grammar rules and are used to
	  build the SQL AST Tree. They are called by the ANTLR Parser when the Walker
	  is traversing the parse tree. They are not independently callable and
	  produce no complete set of objects for each call. They are used to
	  transform the raw parse tree into a structured AST representation.
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

		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Integer parentStackLevel = walker.currentStackLevel(parentRuleIndex);

		Map<String, Object> subMap = walker.removeNodeMap(ruleIndex, stackLevel);
		Object type = subMap.remove("Type");
		Map<String, Object> item;

		if (subMap.size() == 1) {
			// just a query by itself
			subMap = (Map<String, Object>) subMap.remove("1");

		} else if (subMap.size() == 2) {
			// A With Query
			Map<String, Object> withList = (Map<String, Object>) subMap.remove("1");
			Map<String, Object> query = (Map<String, Object>) subMap.remove("2");

			subMap.put(MUMBLE_WITH_KEY, withList);
			subMap.put(MUMBLE_QUERY_KEY, query);
		} else {
			walker.showTrace(walker.parseTrace, "Wrong number of entries: " + ctx.getText());
		}
		walker.addToParent(parentRuleIndex, parentStackLevel, subMap);
		walker.showTrace(walker.parseTrace, "WITH QUERY: " + subMap);
	}

	@Override
	public void exitWith_clause(@NotNull SQLSelectParserParser.With_clauseContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		Integer stackLevel = walker.currentStackLevel(ruleIndex);

		Map<String, Object> subMap = walker.removeNodeMap(ruleIndex, stackLevel);
		Object type = subMap.remove("Type");

		Map<String, Object> newMap = walker.collectNewRuleMap(ruleIndex, stackLevel);
		type = newMap.remove("Type");

		String[] keys = new String[1];
		keys = subMap.keySet().toArray(keys);

		for (String key : keys) {
			newMap.putAll((Map<String, Object>) subMap.remove(key));
		}

		walker.showTrace(walker.parseTrace, "WITH CLAUSE: " + newMap);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void exitWith_list_item(@NotNull SQLSelectParserParser.With_list_itemContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		int parentRuleIndex = ctx.getParent().getRuleIndex();

		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Integer parentStackLevel = walker.currentStackLevel(parentRuleIndex);

		Map<String, Object> subMap = walker.removeNodeMap(ruleIndex, stackLevel);
		Object type = subMap.remove("Type");
		Map<String, Object> item;
		String alias = null;

		if (subMap.size() == 2) {

			alias = (String) subMap.remove("1");

			Map<String, Object> aliasMap = (Map<String, Object>) subMap.remove("2");

			subMap.put(alias, aliasMap);
			// Add to symbol tree WITH subclause
			if ( walker.symbolTable.containsKey(MUMBLE_WITH_KEY)) {
				Map<String, Object> with = (Map<String, Object>)  walker.symbolTable.remove(MUMBLE_WITH_KEY);
				with.put(alias,  walker.symbolTable);
				 walker.symbolTable = new HashMap<String, Object>();
				 walker.symbolTable.put(MUMBLE_WITH_KEY, with);
			} else {
				Map<String, Object> with = new HashMap<String, Object>();
				with.put(alias,  walker.symbolTable);
				 walker.symbolTable = new HashMap<String, Object>();
				 walker.symbolTable.put(MUMBLE_WITH_KEY, with);
			}
		} else {
			walker.showTrace(walker.parseTrace, "Wrong number of entries: " + ctx.getText());
		}
		walker.addToParent(parentRuleIndex, parentStackLevel, subMap);
		walker.showTrace(walker.parseTrace, "WITH QUERY: " + subMap);
	}

	@Override
	public void exitQuery_alias(@NotNull SQLSelectParserParser.Query_aliasContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		walker.handleOneChild(ruleIndex);
	}

	@Override
	public void exitQuery(@NotNull SQLSelectParserParser.QueryContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		walker.handleOneChild(ruleIndex);
	}


/*
===============================================================================
  INSERT Statement <insert expression>
===============================================================================
*/

	@Override
	public void exitInsert_expression(@NotNull SQLSelectParserParser.Insert_expressionContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Map<String, Object> subMap = walker.getNodeMap(ruleIndex, stackLevel);
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
					if (value.containsKey(MUMBLE_TABLE_KEY)) {
						subMap.put("insert", value);
					} else {
						String nk = "query" + walker.queryCount;
						subMap.put(nk, value);
						walker.queryCount++;
					}
				} else {
					Object segment = value.remove(childKey.toString());
					if (childKey == (Integer) SQLSelectParserParser.RULE_column_reference_list) {
						subMap.put("into", value);
					} else if (childKey == (Integer) SQLSelectParserParser.RULE_returning) {
						subMap.put(MUMBLE_RETURNING_KEY, segment);
					} else {
						walker.showTrace(walker.parseTrace, "Too Many Entries" + segment);
					}
				}
			}
		}
		walker.showTrace(walker.parseTrace, subMap);

		// Handle symbol tables
		HashMap<String, Object> symbols =  walker.symbolTable;

		// Special handling of queries with only one source: Move "unknown"
		// references to that table
		HashMap<String, Object> unks = (HashMap<String, Object>) symbols.remove(MUMBLE_UNKNOWN_KEY);

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
							|| (tab_ref.startsWith("update")) || (tab_ref.startsWith(MUMBLE_UNION_KEY))
							|| (tab_ref.startsWith(MUMBLE_INTERSECT_KEY))) {
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
						symbols.put(MUMBLE_UNKNOWN_KEY, unks);
				}
			}
		}
		// TODO: Add TABLE references to Table Dictionary
		if (hold.size() > 0) {
			for (String tab_ref : hold.keySet()) {
				if ((tab_ref.startsWith("query")) || (tab_ref.startsWith("insert")) || (tab_ref.startsWith("update"))
						|| (tab_ref.startsWith(MUMBLE_UNION_KEY)) || (tab_ref.startsWith(MUMBLE_INTERSECT_KEY))) {
				} else {
					String reference;
					if (tab_ref.startsWith("<"))
						// Tuple Substitution Variable, do NOT alter case
						reference = tab_ref;
					else
						reference = tab_ref.toLowerCase();
					HashMap<String, Object> currItem = (HashMap<String, Object>)  walker.tableDictionaryMap.get(reference);
					if (currItem != null)
						currItem.putAll((Map<? extends String, ? extends Object>) hold.get(tab_ref));
					else {
						HashMap<String, Object> newItem = new HashMap<String, Object>();
						newItem.putAll((Map<? extends String, ? extends Object>) hold.get(tab_ref));
						 walker.tableDictionaryMap.put(reference, newItem);
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
		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Map<String, Object> subMap = walker.getNodeMap(ruleIndex, stackLevel);
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
					String k2 = "update" + walker.queryCount;
					walker.queryCount++;
					subMap.put(k2, value);
				} else {
					Object segment = value.remove(childKey.toString());
					if (childKey == (Integer) SQLSelectParserParser.RULE_assignment_expression_list) {
						subMap.put(MUMBLE_ASSIGNMENTS_KEY, segment);
					} else if (childKey == (Integer) SQLSelectParserParser.RULE_from_clause) {
						if (((HashMap<String, Object>) segment).size() == 1) {
							subMap.put(MUMBLE_FROM_KEY, ((HashMap<String, Object>) segment).remove("1"));
						} else
							subMap.put(MUMBLE_FROM_KEY, segment);
					} else if (childKey == (Integer) SQLSelectParserParser.RULE_where_clause) {
						HashMap<String, Object> item = (HashMap<String, Object>) segment;
						item = (HashMap<String, Object>) item.remove("1");
						subMap.put(MUMBLE_WHERE_KEY, item);
					} else if (childKey == (Integer) SQLSelectParserParser.RULE_returning) {
						subMap.put(MUMBLE_RETURNING_KEY, segment);
					} else {
						walker.showTrace(walker.parseTrace, "Too Many Entries" + segment);
					}
				}
			}
		}
		walker.showTrace(walker.parseTrace, subMap);

		// Handle symbol tables
		HashMap<String, Object> symbols =  walker.symbolTable;

		// Special handling of queries with only one source: Move "unknown"
		// references to that table
		HashMap<String, Object> unks = (HashMap<String, Object>) symbols.remove(MUMBLE_UNKNOWN_KEY);

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
					if ((tab_ref.startsWith("query")) || (tab_ref.startsWith(MUMBLE_UNION_KEY))
							|| (tab_ref.startsWith(MUMBLE_INTERSECT_KEY))) {
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
						symbols.put(MUMBLE_UNKNOWN_KEY, unks);
				}
			}
		}
		// TODO: Add TABLE references to Table Dictionary
		if (hold.size() > 0) {
			for (String tab_ref : hold.keySet()) {
				if ((tab_ref.startsWith("query")) || (tab_ref.startsWith(MUMBLE_UNION_KEY))
						|| (tab_ref.startsWith(MUMBLE_INTERSECT_KEY))) {
				} else {
					String reference;
					if (tab_ref.startsWith("<"))
						// Tuple Substitution Variable, do NOT alter case
						reference = tab_ref;
					else
						reference = tab_ref.toLowerCase();
					HashMap<String, Object> currItem = (HashMap<String, Object>)  walker.tableDictionaryMap.get(reference);
					if (currItem != null)
						currItem.putAll((Map<? extends String, ? extends Object>) hold.get(tab_ref));
					else {
						HashMap<String, Object> newItem = new HashMap<String, Object>();
						newItem.putAll((Map<? extends String, ? extends Object>) hold.get(tab_ref));
						 walker.tableDictionaryMap.put(reference, newItem);
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
			walker.handleListList(ruleIndex, parentRuleIndex);
		} else {
			// then parent is normal query
			walker.handlePushDown(ruleIndex);
		}
	}

	@Override
	public void exitAssignment_expression(@NotNull SQLSelectParserParser.Assignment_expressionContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		int parentRuleIndex = ctx.getParent().getRuleIndex();
		Integer parentStackLevel = walker.currentStackLevel(parentRuleIndex);

		Map<String, Object> subMap = walker.removeNodeMap(ruleIndex, stackLevel);
		Object type = subMap.remove("Type");

		if (subMap.size() == 2) {
			walker.showTrace(walker.parseTrace, "Comparison: " + subMap);
			Map<String, Object> left = (Map<String, Object>) subMap.remove("1");
			subMap.put(MUMBLE_SET_KEY, left);

			Map<String, Object> right = (Map<String, Object>) subMap.remove("2");
			subMap.put(MUMBLE_TO_KEY, right);

			walker.showTrace(walker.parseTrace, "Assignment: " + subMap);

			// Put target column symbol into update table's set and interface
			Map<String, Object> unk = (HashMap<String, Object>)  walker.symbolTable.get(MUMBLE_UNKNOWN_KEY);
			String column = ((HashMap<String, String>) ((HashMap<String, Object>) left).get(MUMBLE_COLUMN_KEY)).get("name");

			String[] keys = new String[1];
			keys =  walker.symbolTable.keySet().toArray(keys);

			for (String key : keys) {
				if (key.equals(MUMBLE_UNKNOWN_KEY)) { // do nothing

				} else if (key.equals(MUMBLE_WITH_KEY)) { // do nothing

				} else {
					// must be the table
					walker.showTrace(walker.symbolTrace, "Key for not 'UNKNOWN': " + key + " Entry: " +  walker.symbolTable.get(key));
					Object item =  walker.symbolTable.get(key);
					if (item instanceof Map<?, ?>) {
						HashMap<String, Object> map = (HashMap<String, Object>) item;
						map.put(column, unk.get(column));
					}
				}
			}
		} else {
			walker.showTrace(walker.parseTrace, "Wrong number of entries: " + subMap);
		}
		walker.addToParent(parentRuleIndex, parentStackLevel, subMap);

	}

	/*
	===============================================================================
	  VALUES Statement as Tuple
	===============================================================================
	*/

	// 
		

		@Override
		public void enterValues_statement_primary(@NotNull SQLSelectParserParser.Values_statement_primaryContext ctx) {
			walker.pushSymbolTable();
		}

		@Override
		public void exitValues_statement_primary(@NotNull SQLSelectParserParser.Values_statement_primaryContext ctx) {
			int ruleIndex = ctx.getRuleIndex();
			int parentRuleIndex = ctx.getParent().getRuleIndex();

			Integer stackLevel = walker.currentStackLevel(ruleIndex);
			Integer parentStackLevel = walker.currentStackLevel(parentRuleIndex);

			Map<String, Object> subMap = walker.removeNodeMap(ruleIndex, stackLevel);
			Object type = subMap.remove("Type");
			Map<String, Object> item = new HashMap<String, Object>();
			if (subMap.size() == 1) {
				item.putAll((Map<String, Object>) subMap.remove("1"));
			} else {
				walker.showTrace(walker.parseTrace, "Wrong number of entries: " + ctx.getText());
			}
			walker.addToParent(parentRuleIndex, parentStackLevel, item);
			walker.showTrace(walker.parseTrace, "Case: " + item);
			
			// Add item to symbol table
			Map<String, Object> values = (Map<String, Object>) item.get(MUMBLE_VALUES_KEY);
			HashMap<String, Object> selectInterface = null;
			String interfaceAlias = (String) values.get(MUMBLE_ALIAS_KEY);

			if (interfaceAlias == null) {
				interfaceAlias = "unnamed";
				selectInterface = (HashMap<String, Object>)  walker.symbolTable.get(MUMBLE_VALUES_KEY);

			} else {
				selectInterface = (HashMap<String, Object>)  walker.symbolTable.get("interface");
			
				if (selectInterface == null) {
					selectInterface = new HashMap<String, Object>();
					HashMap<String, Object> gfg = (HashMap<String, Object>)  walker.symbolTable.get(interfaceAlias);
					for (Map.Entry<String, Object> entry : gfg.entrySet()) {
						System.out.println("Key = " + entry.getKey() +
		                             ", Value = " + entry.getValue());
						selectInterface.put(entry.getKey(), entry.getValue());
					}
				}
			}
			 walker.symbolTable.put("interface", selectInterface);

			// Symbol Table Construction
			HashMap<String, Object> symbols =  walker.symbolTable;

			String key = MUMBLE_VALUES_KEY + walker.queryCount;
			walker.popSymbolTable("def_" + key, symbols);
			walker.queryCount++;

			 walker.symbolTable.put(interfaceAlias, key);
			 walker.symbolTable.put(key,selectInterface);
			
			// Construct Table Dictionary

		}
		
		@SuppressWarnings("unchecked")
		@Override
		public void exitFully_defined_values_statement(@NotNull SQLSelectParserParser.Fully_defined_values_statementContext ctx) {
			int ruleIndex = ctx.getRuleIndex();
			int parentRuleIndex = ctx.getParent().getRuleIndex();

			Integer stackLevel = walker.currentStackLevel(ruleIndex);
			Integer parentStackLevel = walker.currentStackLevel(parentRuleIndex);

			Map<String, Object> subMap = walker.removeNodeMap(ruleIndex, stackLevel);
			Object type = subMap.remove("Type");
			if (subMap.size() == 3) {
				// Variation 3: Contains an alias and a list of column name assignments
				
				Map<String, Object> item = (Map<String, Object>) subMap.remove("1");
				Map<String, Object> vals = (Map<String, Object>) item.get(MUMBLE_VALUES_KEY);
				Map<String, Object> aliasMap = (Map<String, Object>) subMap.remove("2");
				vals.putAll(aliasMap);
				vals.put(MUMBLE_COLUMNS_KEY,  subMap.remove("3"));

				subMap.putAll(item);

				// Resolve Symbol Table, eliminate virtual references because this statement has an actual columns list.
				walker.consolidateValuesStatementSymbolTable((String) aliasMap.get(MUMBLE_ALIAS_KEY));
			} else {
				walker.showTrace(walker.parseTrace, "Wrong number of entries: " + ctx.getText());
			}

			walker.addToParent(parentRuleIndex, parentStackLevel, subMap);
			walker.showTrace(walker.parseTrace, "Case: " + subMap);
			
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public void exitAliased_values_statement(@NotNull SQLSelectParserParser.Aliased_values_statementContext ctx) {
			int ruleIndex = ctx.getRuleIndex();
			int parentRuleIndex = ctx.getParent().getRuleIndex();

			Integer stackLevel = walker.currentStackLevel(ruleIndex);
			Integer parentStackLevel = walker.currentStackLevel(parentRuleIndex);

			Map<String, Object> subMap = walker.removeNodeMap(ruleIndex, stackLevel);
			Object type = subMap.remove("Type");
			if (subMap.size() == 2) {
				// Variation 2: Contains an alias
				Map<String, Object> item = (Map<String, Object>) subMap.remove("1");
				Map<String, Object> vals = (Map<String, Object>) item.get(MUMBLE_VALUES_KEY);
				Map<String, Object> aliasMap = (Map<String, Object>) subMap.remove("2");
				vals.putAll(aliasMap);
				
				subMap.putAll(item);

				// Resolve Symbol Table, eliminate virtual references because this statement has an actual columns list.
				walker.consolidateValuesStatementSymbolTable((String) aliasMap.get(MUMBLE_ALIAS_KEY));
			} else {
				walker.showTrace(walker.parseTrace, "Wrong number of entries: " + ctx.getText());
			}

			walker.addToParent(parentRuleIndex, parentStackLevel, subMap);
			walker.showTrace(walker.parseTrace, "Case: " + subMap);
			}
		
		
		@SuppressWarnings("unchecked")
		@Override
		public void exitValues_statement(@NotNull SQLSelectParserParser.Values_statementContext ctx) {
			int ruleIndex = ctx.getRuleIndex();
			int parentRuleIndex = ctx.getParent().getRuleIndex();

			Integer stackLevel = walker.currentStackLevel(ruleIndex);
			Integer parentStackLevel = walker.currentStackLevel(parentRuleIndex);

			Map<String, Object> subMap = walker.removeNodeMap(ruleIndex, stackLevel);
			Object type = subMap.remove("Type");
			if (subMap.size() == 1) {
				// Variation 1: Just a matrix of rows
				subMap.putAll((Map<String, Object>) subMap.remove("1"));
			} else {
				walker.showTrace(walker.parseTrace, "Wrong number of entries: " + ctx.getText());
			}

			Map<String, Object> item = new HashMap<String, Object>();
			item.put(MUMBLE_VALUES_KEY, subMap);
			walker.addToParent(parentRuleIndex, parentStackLevel, item);
			walker.showTrace(walker.parseTrace, "Case: " + item);

		}
	// TODO: add symbol tree collection
		
		@Override
		public void exitValues_matrix(@NotNull SQLSelectParserParser.Values_matrixContext ctx) {
			int ruleIndex = ctx.getRuleIndex();

			Integer stackLevel = walker.currentStackLevel(ruleIndex);
			Map<String, Object> subMap = walker.removeNodeMap(ruleIndex, stackLevel);
			subMap.remove("Type");
			Map<String, Object> item = new HashMap<String, Object>();
			item.put(MUMBLE_MATRIX_KEY, subMap);
			
			// Construct virtual column references from the first row in the values matrix 
			Map<String, Object> row = (Map<String, Object>) subMap.get("1");
			row = (Map<String, Object>) row.get(MUMBLE_ROW_KEY);
			for (int i = 1; i <= row.size(); i++) {
				String ref = "$" + i;
				walker.collectSymbolTableItem(MUMBLE_VALUES_KEY, ref, ctx.getStart());
			}
			
			int parentRuleIndex = ctx.getParent().getRuleIndex();
			Integer parentStackLevel = walker.currentStackLevel(parentRuleIndex);
			walker.addToParent(parentRuleIndex, parentStackLevel, item);
		}


		@Override
		public void exitValues_row(@NotNull SQLSelectParserParser.Values_rowContext ctx) {
			int ruleIndex = ctx.getRuleIndex();

			Integer stackLevel = walker.currentStackLevel(ruleIndex);
			Map<String, Object> subMap = walker.removeNodeMap(ruleIndex, stackLevel);
			subMap.remove("Type");
			Map<String, Object> item = (Map<String, Object>) subMap.remove("1");
			item.put(MUMBLE_ROW_KEY, item.remove("list"));

			int parentRuleIndex = ctx.getParent().getRuleIndex();
			Integer parentStackLevel = walker.currentStackLevel(parentRuleIndex);
			walker.addToParent(parentRuleIndex, parentStackLevel, item);
		}

		@Override
		public void exitValues_columns(@NotNull SQLSelectParserParser.Values_columnsContext ctx) {
			int ruleIndex = ctx.getRuleIndex();

			Integer stackLevel = walker.currentStackLevel(ruleIndex);
			Map<String, Object> subMap = walker.removeNodeMap(ruleIndex, stackLevel);
			subMap.remove("Type");
			Map<String, Object> item = (Map<String, Object>) subMap.remove("1");
			item.put(MUMBLE_COLUMNS_KEY, item.remove("list"));

			int parentRuleIndex = ctx.getParent().getRuleIndex();
			Integer parentStackLevel = walker.currentStackLevel(parentRuleIndex);
			walker.addToParent(parentRuleIndex, parentStackLevel, item);
		}
		
	// End Values Statement	

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
	// walker.handleOneChild(ruleIndex);
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
		walker.handleListList(ruleIndex, parentRuleIndex);

		// clear intersect clause count
		walker.intersectClauseFound = false;
	}

	@Override
	public void enterIntersected_query(@NotNull SQLSelectParserParser.Intersected_queryContext ctx) {
		walker.pushSymbolTable();
	}

	@Override
	public void exitIntersected_query(@NotNull SQLSelectParserParser.Intersected_queryContext ctx) {
		int ruleIndex = ctx.getRuleIndex();

		walker.handleOperandList(ruleIndex, MUMBLE_INTERSECT_KEY);

		// Handle symbol tables
		HashMap<String, Object> symbols =  walker.symbolTable;

		if (walker.intersectClauseFound) {
			// Retrieve outer symbol table, insert this symbol table into it
			String key = MUMBLE_INTERSECT_KEY + walker.queryCount;
			walker.popSymbolTable(key, symbols);
			walker.queryCount++;
		} else {
			walker.popSymbolTablePutAll(symbols);
		}

		// clear union clause count
		walker.unionClauseFound = false;
	}

	@Override
	public void enterIntersect_clause(@NotNull SQLSelectParserParser.Intersect_clauseContext ctx) {
		if (!walker.intersectClauseFound) {
			walker.intersectClauseFound = true;
			walker.firstIntersectClause = true;
		} else
			walker.firstIntersectClause = false;
	}

	@Override
	public void exitIntersect_clause(@NotNull SQLSelectParserParser.Intersect_clauseContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Map<String, Object> subMap = walker.getNodeMap(ruleIndex, stackLevel);
		Object type = subMap.remove("Type");

		if (ctx.getChildCount() == 1) {
			Map<String, Object> item = new HashMap<String, Object>();
			item.put(MUMBLE_OPERATOR_KEY, subMap.remove("1"));
			item.put(MUMBLE_QUALIFIER_KEY, null);
			subMap.put(MUMBLE_INTERSECT_KEY, item);
		} else if (ctx.getChildCount() == 2) {
			Map<String, Object> item = new HashMap<String, Object>();
			item.put(MUMBLE_OPERATOR_KEY, subMap.remove("1"));

			Map<String, Object> hold = (Map<String, Object>) subMap.remove("2");
			if (hold.containsKey("Type")) {
				Integer childKey = (Integer) hold.remove("Type");
				if (childKey == (Integer) SQLSelectParserParser.RULE_set_qualifier)
					item.putAll(hold);
			} else {
				item.put(MUMBLE_QUALIFIER_KEY, hold);
			}
			
			subMap.put(MUMBLE_INTERSECT_KEY, item);
		} else {
			walker.showTrace(walker.parseTrace, "Wrong number of entries: " + ctx.getText());
		}
		walker.showTrace(walker.parseTrace, "Intersect Operator: " + subMap);

		// Get first interface to represent intersection output
		if (walker.firstIntersectClause) {
			walker.showTrace(walker.symbolTrace, "Intersect So Far: " +  walker.symbolTable);
			walker.captureQueryInterface();
			walker.showTrace(walker.symbolTrace, "Intersect So Far: " +  walker.symbolTable);

		}
	}

	// Intersect_operator does not need its own logic
	
	@Override
	public void enterUnionized_query(@NotNull SQLSelectParserParser.Unionized_queryContext ctx) {
		walker.pushSymbolTable();
	}

	@Override
	public void exitUnionized_query(@NotNull SQLSelectParserParser.Unionized_queryContext ctx) {
		int ruleIndex = ctx.getRuleIndex();

		walker.handleOperandList(ruleIndex, MUMBLE_UNION_KEY);

		// Handle symbol tables
		HashMap<String, Object> symbols =  walker.symbolTable;

		if (walker.unionClauseFound) {
			// Retrieve outer symbol table, insert this symbol table into it
			String key = MUMBLE_UNION_KEY + walker.queryCount;
			walker.popSymbolTable(key, symbols);
			walker.queryCount++;
		} else {
			walker.popSymbolTablePutAll(symbols);
		}

	}

	@Override
	public void enterUnion_clause(@NotNull SQLSelectParserParser.Union_clauseContext ctx) {
		if (!walker.unionClauseFound) {
			walker.unionClauseFound = true;
			walker.firstUnionClause = true;
		} else
			walker.firstUnionClause = false;
	}

	@Override
	public void exitUnion_clause(@NotNull SQLSelectParserParser.Union_clauseContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Map<String, Object> subMap = walker.getNodeMap(ruleIndex, stackLevel);
		Object type = subMap.remove("Type");

		if (ctx.getChildCount() == 1) {
			Map<String, Object> item = new HashMap<String, Object>();
			item.put(MUMBLE_OPERATOR_KEY, subMap.remove("1"));
			item.put(MUMBLE_QUALIFIER_KEY, null);
			subMap.put(MUMBLE_UNION_KEY, item);
		} else if (ctx.getChildCount() == 2) {
			Map<String, Object> item = new HashMap<String, Object>();
			item.put(MUMBLE_OPERATOR_KEY, subMap.remove("1"));

			Map<String, Object> hold = (Map<String, Object>) subMap.remove("2");
			if (hold.containsKey("Type")) {
				Integer childKey = (Integer) hold.remove("Type");
				if (childKey == (Integer) SQLSelectParserParser.RULE_set_qualifier)
					item.putAll(hold);
			} else {
				item.put(MUMBLE_QUALIFIER_KEY, hold);
			}
			
			subMap.put(MUMBLE_UNION_KEY, item);
		} else {
			walker.showTrace(walker.parseTrace, "Wrong number of entries: " + ctx.getText());
		}
		walker.showTrace(walker.parseTrace, "Union Operator: " + subMap);

		// Get first interface to represent union output
		if (walker.firstUnionClause) {
			walker.showTrace(walker.symbolTrace, "Union So Far: " +  walker.symbolTable);
			walker.captureQueryInterface();
			walker.showTrace(walker.symbolTrace, "Union So Far: " +  walker.symbolTable);
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
		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		int parentRuleIndex = ctx.getParent().getRuleIndex();

		Map<String, Object> subMap = walker.getNodeMap(ruleIndex, stackLevel);
		walker.checkForSubstitutionVariable((Map<String, Object>) subMap.get("1"), "query");

		walker.handleOneChild(ruleIndex);
	}

	@Override
	public void exitSubquery(@NotNull SQLSelectParserParser.SubqueryContext ctx) {
		int ruleIndex = ctx.getRuleIndex();

		int parentRuleIndex = ctx.getParent().getRuleIndex();
		if (parentRuleIndex == (Integer) SQLSelectParserParser.RULE_nonparenthesized_value_expression_primary) {
			// Subquery is acting as a lookup function
			Integer stackLevel = walker.currentStackLevel(ruleIndex);
			Map<String, Object> subMap = walker.getNodeMap(ruleIndex, stackLevel);
			Object type = subMap.remove("Type");
			HashMap<String, Object> item = new HashMap<String, Object>();

			item.put(MUMBLE_LOOKUP_KEY, subMap.remove("1"));

			subMap.put("1", item);
			walker.handleListItem(ruleIndex, parentRuleIndex);
		} else {
			// then parent is any non-list parent
			walker.handleOneChild(ruleIndex);
		}
	}

	@Override
	public void enterQuery_specification(@NotNull SQLSelectParserParser.Query_specificationContext ctx) {
		walker.pushSymbolTable();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void exitQuery_specification(@NotNull SQLSelectParserParser.Query_specificationContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Map<String, Object> subMap = walker.getNodeMap(ruleIndex, stackLevel);
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
					subMap.put(MUMBLE_SELECT_KEY, segment);
				} else if (childKey == (Integer) SQLSelectParserParser.RULE_set_qualifier) {
					subMap.putAll(value);
				} else if (childKey == (Integer) SQLSelectParserParser.RULE_from_clause) {
					if (((HashMap<String, Object>) segment).size() == 1) {
						subMap.put(MUMBLE_FROM_KEY, ((HashMap<String, Object>) segment).remove("1"));
					} else
						subMap.put(MUMBLE_FROM_KEY, segment);
				} else if (childKey == (Integer) SQLSelectParserParser.RULE_where_clause) {
					HashMap<String, Object> item = (HashMap<String, Object>) segment;
					item = (HashMap<String, Object>) item.remove("1");
					subMap.put(MUMBLE_WHERE_KEY, item);
				} else if (childKey == (Integer) SQLSelectParserParser.RULE_groupby_clause) {
					subMap.put(MUMBLE_GROUPBY_KEY, segment);
				} else if (childKey == SQLSelectParserParser.RULE_having_clause) {
					HashMap<String, Object> item = (HashMap<String, Object>) segment;
					item = (HashMap<String, Object>) item.remove("1");
					subMap.put(MUMBLE_HAVING_KEY, item);
				} else if (childKey == (Integer) SQLSelectParserParser.RULE_orderby_clause) {
					subMap.put(MUMBLE_ORDERBY_KEY, segment);
				} else if (childKey == (Integer) SQLSelectParserParser.RULE_limit_clause) {
					subMap.put(MUMBLE_LIMIT_KEY, segment);
				} else {
					walker.showTrace(walker.parseTrace, "Too Many Entries" + segment);
				}
			}
		}
		walker.showTrace(walker.parseTrace, subMap);

		// Handle symbol tables		
		HashMap<String, Object> symbols = convertSymbolTableToTableDictionary();

		// Retrieve outer symbol table, insert this symbol table into it
		String key = "query" + walker.queryCount;
		walker.popSymbolTable(key, symbols);
		walker.queryCount++;
	}

/*
===============================================================================
  SELECT Details
===============================================================================
*/
 // TODO: Select Into Table syntax has not been implemented
	
// set_qualifier needs to insert into parent object
	

	@Override
	public void exitSet_qualifier(@NotNull SQLSelectParserParser.Set_qualifierContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		int parentRuleIndex = ctx.getParent().getRuleIndex();

		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Integer parentStackLevel = walker.currentStackLevel(parentRuleIndex);
		
		String item = ctx.getChild(0).getText();
		
		Map<String, Object> subMap = new HashMap<String, Object>();
		subMap.put(MUMBLE_QUALIFIER_KEY, item);
		subMap.put("Type", SQLSelectParserParser.RULE_set_qualifier);
		
		walker.showTrace(walker.parseTrace, "Qualifier: " + subMap);
	
		walker.addToParent(parentRuleIndex, parentStackLevel, subMap);
	}

	@Override
	public void exitSelect_list(@NotNull SQLSelectParserParser.Select_listContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		int parentRuleIndex = ctx.getParent().getRuleIndex();
		// then parent is normal query
		walker.handlePushDown(ruleIndex);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void exitSelect_item(@NotNull SQLSelectParserParser.Select_itemContext ctx) {
		// TODO
		int ruleIndex = ctx.getRuleIndex();
		int parentRuleIndex = ctx.getParent().getRuleIndex();

		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Integer parentStackLevel = walker.currentStackLevel(parentRuleIndex);

		Map<String, Object> subMap = walker.removeNodeMap(ruleIndex, stackLevel);
		subMap.remove("Type");
		Map<String, Object> item;

		// variables for constructing the Symbol Table Interface
		String interfaceAlias = null;
		HashMap<String, Object> interfaceReference = new HashMap<String, Object>();

		// Get first item, record if it is a Substitution Variable by adding the
		// Substitution List
		item = walker.checkForSubstitutionVariable((Map<String, Object>) subMap.remove("1"), "predicand");

		interfaceReference.putAll(item);

		if (subMap.size() == 0) {
			// Select Item did not have an Alias, construct one from options
			walker.showTrace(walker.parseTrace, "Just One Item: " + item);
			HashMap<String, Object> node = (HashMap<String, Object>) item.get(MUMBLE_COLUMN_KEY);
			if (node == null)
				node = (HashMap<String, Object>) item.get(MUMBLE_SUBSTITUTION_KEY);
			if (node != null)
				if (node.containsKey(MUMBLE_NAME_KEY))
					// Select Item is a column or substitution, use its name
					interfaceAlias = (String) node.get(MUMBLE_NAME_KEY);
				else if (node.containsKey(MUMBLE_SUBSTITUTION_KEY))
					// then Select Item is a COLUMN Substitution Variable, get
					// the variable's name
					interfaceAlias = (String) ((HashMap<String, Object>) node.get(MUMBLE_SUBSTITUTION_KEY)).get("name");
			if (interfaceAlias == null) {
				// Select Item is a PREDICAND without a name, generate the next
				// placeholder for the interface
				interfaceAlias = "unnamed_" + walker.predicandCount;
				walker.predicandCount++;
			}

		} else {
			// Select Item has an alias
			walker.showTrace(walker.parseTrace, "Item and Alias: " + item);

			Map<String, Object> aliasMap = (Map<String, Object>) subMap.remove("2");
			interfaceAlias = (String) aliasMap.get(MUMBLE_ALIAS_KEY);
			((Map<String, Object>) item).putAll(aliasMap);
		}
		walker.addToParent(parentRuleIndex, parentStackLevel, item);
		walker.showTrace(walker.parseTrace, "SELECT ITEM: " + item);

		// Add item to symbol table
		HashMap<String, Object> selectInterface = (HashMap<String, Object>)  walker.symbolTable.get("interface");
		if (selectInterface == null) {
			selectInterface = new HashMap<String, Object>();
			 walker.symbolTable.put("interface", selectInterface);
		}
		selectInterface.put(interfaceAlias, interfaceReference);
	}


	@Override
	public void exitAs_clause(@NotNull SQLSelectParserParser.As_clauseContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Map<String, Object> subMap = walker.getNodeMap(ruleIndex, stackLevel);
		Object type = subMap.remove("Type");

		if (subMap.size() == 1) {
			walker.showTrace(walker.parseTrace, "Just One Identifier: " + subMap);
			String alias = (String) subMap.remove("1");
			subMap.put(MUMBLE_ALIAS_KEY, alias);
			walker.showTrace(walker.parseTrace, "Alias: " + alias + " Map: " + subMap);
		} else {
			walker.showTrace(walker.parseTrace, "Too many entries: " + subMap);
		}

	}

	@Override
	public void exitSelect_all_columns(@NotNull SQLSelectParserParser.Select_all_columnsContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		int parentRuleIndex = ctx.getParent().getRuleIndex();

		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Integer parentStackLevel = walker.currentStackLevel(parentRuleIndex);

		Map<String, Object> subMap = walker.removeNodeMap(ruleIndex, stackLevel);

		Map<String, Object> item = new HashMap<String, Object>();

		if (subMap == null) {
			// unqualified select all has no map
			subMap = walker.makeRuleMap(ruleIndex);
		}
		subMap.remove("Type");
		if (ctx.getChildCount() == 1) {
			walker.showTrace(walker.parseTrace, "Just One Identifier: " + ctx.getText());
			item.put(MUMBLE_TABLE_REF_KEY, "*");
			item.put(MUMBLE_NAME_KEY, "*");

			walker.collectSymbolTableItem(MUMBLE_UNKNOWN_KEY, "*", ctx.getStart());

			subMap.put(MUMBLE_COLUMN_KEY, item);
		} else if (ctx.getChildCount() == 3) {
			walker.showTrace(walker.parseTrace, "Three entries: " + ctx.getText());
			item.put(MUMBLE_TABLE_REF_KEY, ctx.getChild(0).getText());

			walker.collectSymbolTableItem(item.get(MUMBLE_TABLE_REF_KEY), "*", ctx.getStart());

			item.put(MUMBLE_NAME_KEY, "*");
			subMap.put(MUMBLE_COLUMN_KEY, item);
		}
		// Add item to parent map
		walker.addToParent(parentRuleIndex, parentStackLevel, subMap);
		walker.showTrace(walker.parseTrace, "Table Alias . Column Name: " + subMap);
	}

/*
===============================================================================
  FROM Statement <from clause>
===============================================================================
*/

	@Override
	public void exitFrom_clause(@NotNull SQLSelectParserParser.From_clauseContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		int stackLevel = walker.currentStackLevel(ruleIndex);
		int parentRuleIndex = ctx.getParent().getRuleIndex();
		Map<String, Object> subMap = walker.getNodeMap(ruleIndex, stackLevel);
		if (subMap.size() == 3) {
			// handle from clause with extension
			subMap.putAll((Map<String, Object>) subMap.remove("1"));
			if (subMap.containsKey("2"))
				subMap.put(MUMBLE_JOIN_EXTENSION_KEY, subMap.remove("2"));
		}
		walker.handlePushDown(ruleIndex);
	}
	
	// RULE_join_extension

	@Override
	public void exitJoin_extension(@NotNull SQLSelectParserParser.Join_extensionContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		int stackLevel = walker.currentStackLevel(ruleIndex);
		int parentRuleIndex = ctx.getParent().getRuleIndex();
		Map<String, Object> subMap = walker.getNodeMap(ruleIndex, stackLevel);
		Map<String, Object> reference = walker.checkForSubstitutionVariable((Map<String, Object>) subMap.get("1"),
				"join_extension");

		walker.handleOneChild(ruleIndex);
	}

	@Override
	public void exitTable_reference_list(@NotNull SQLSelectParserParser.Table_reference_listContext ctx) {
		int ruleIndex = ctx.getRuleIndex();

		walker.handleOperandList(ruleIndex, MUMBLE_JOIN_KEY);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void exitJoin_extension_primary(@NotNull SQLSelectParserParser.Join_extension_primaryContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		int parentRuleIndex = ctx.getParent().getRuleIndex();

		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Integer parentStackLevel = walker.currentStackLevel(parentRuleIndex);

		Map<String, Object> subMap = walker.removeNodeMap(ruleIndex, stackLevel);
		Object type = subMap.remove("Type");

		walker.addToParent(parentRuleIndex, parentStackLevel, subMap);
		walker.showTrace(walker.parseTrace, "Join Extension: " + subMap);

		convertSymbolTableToTableDictionary();
	}

	/**
	 * Create Dictionary from Symbol Table
	 * 
	 * @return
	 */
	private HashMap<String, Object> convertSymbolTableToTableDictionary() {
		// Handle symbol tables
		HashMap<String, Object> symbols =  walker.symbolTable;

		// Special handling of queries with only one source: Move "unknown"
		// references to that table
		HashMap<String, Object> unks = (HashMap<String, Object>) symbols.remove(MUMBLE_UNKNOWN_KEY);

		Integer count = 0;
		Integer tableCount = 0;
		String onlyTableName = null;
		HashMap<String, Object> hold = new HashMap<String, Object>();
		String holdTabRef = null;

		for (String tab_ref : symbols.keySet()) {
			if ((tab_ref.equals("interface")) || (tab_ref.startsWith("def_query")) || (tab_ref.startsWith("def_insert"))
					|| (tab_ref.startsWith("def_update")) || (tab_ref.startsWith("def_union"))
					|| (tab_ref.startsWith("def_intersect")) || (tab_ref.startsWith("def_values"))) {
			} else {
				Object item = symbols.get(tab_ref);
				if (item instanceof HashMap<?, ?>) {
					hold.put(tab_ref, item);
					holdTabRef = tab_ref;
					count++;
					if ((tab_ref.startsWith("query")) || (tab_ref.startsWith("insert"))
							|| (tab_ref.startsWith("update")) || (tab_ref.startsWith(MUMBLE_UNION_KEY))
							|| (tab_ref.startsWith(MUMBLE_INTERSECT_KEY)) || (tab_ref.startsWith(MUMBLE_VALUES_KEY))) {
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
						symbols.put(MUMBLE_UNKNOWN_KEY, unks);
				}
			}
		}
		// TODO: Add TABLE references to Table Dictionary
		if (hold.size() > 0) {
			for (String tab_ref : hold.keySet()) {
				if ((tab_ref.startsWith("query")) 
						|| (tab_ref.startsWith(MUMBLE_UNION_KEY))
						|| (tab_ref.startsWith(MUMBLE_INTERSECT_KEY))
						|| (tab_ref.startsWith(MUMBLE_VALUES_KEY))) {
//				}
//				else if (tab_ref.startsWith(MUMBLE_VALUES_KEY)) {
//					walker.showTrace(walker.parseTrace, "Symbol Tree: " + symbols);
				} else {
					String reference;
					if (tab_ref.startsWith("<"))
						// Tuple Substitution Variable, do NOT alter case
						reference = tab_ref;
					else
						reference = tab_ref.toLowerCase();
					HashMap<String, Object> currItem = (HashMap<String, Object>)  walker.tableDictionaryMap.get(reference);
					if (currItem != null)
						currItem.putAll((Map<? extends String, ? extends Object>) hold.get(tab_ref));
					else {
						HashMap<String, Object> newItem = new HashMap<String, Object>();
						newItem.putAll((Map<? extends String, ? extends Object>) hold.get(tab_ref));
						 walker.tableDictionaryMap.put(reference, newItem);
					}
				}
			}
		}
		return symbols;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void exitTable_primary(@NotNull SQLSelectParserParser.Table_primaryContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		int parentRuleIndex = ctx.getParent().getRuleIndex();

		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Integer parentStackLevel = walker.currentStackLevel(parentRuleIndex);

		Map<String, Object> subMap = walker.removeNodeMap(ruleIndex, stackLevel);
		Object type = subMap.remove("Type");
		Map<String, Object> item;
		String alias = null;

		if (ctx.getChildCount() == 1) {
			item = (Map<String, Object>) subMap.remove("1");
			if (item.keySet().contains(MUMBLE_TABLE_KEY)) {
				item.put(MUMBLE_ALIAS_KEY, null);

				Object table = item.get(MUMBLE_TABLE_KEY);
				if (table != null) {
					alias = table.toString();
					walker.collectSymbolTable(alias, table);

					subMap.put(MUMBLE_TABLE_KEY, item);
				} else {
					alias = "unnamed";
					Map<String, Object> aliasMap = new HashMap<String, Object>();
					aliasMap.put(alias, alias);
					Boolean done = collectQuerySymbolTable(MUMBLE_QUERY_KEY, aliasMap, alias, item);
					if (!done)
						done = collectQuerySymbolTable(MUMBLE_INSERT_KEY, aliasMap, alias, item);
					if (!done)
						done = collectQuerySymbolTable(MUMBLE_UPDATE_KEY, aliasMap, alias, item);
					if (!done)
						done = collectQuerySymbolTable(MUMBLE_UNION_KEY, aliasMap, alias, item);
					if (!done)
						done = collectQuerySymbolTable(MUMBLE_INTERSECT_KEY, aliasMap, alias, item);
				}
			} else { // VALUES STATEMENT can only happen in this instance
				subMap.putAll(item);
				
//				TODO:??? convertSymbolTableToTableDictionary();

			}

		} else if (ctx.getChildCount() == 2) {
			item = new HashMap<String, Object>();
			Map<String, Object> reference = walker.checkForSubstitutionVariable((Map<String, Object>) subMap.remove("1"),
					"tuple");

			Map<String, Object> aliasMap = (Map<String, Object>) subMap.remove("2");
			alias = (String) aliasMap.get(MUMBLE_ALIAS_KEY);
			item.putAll(aliasMap);

			// Try various alternatives
			if (reference.containsKey(MUMBLE_TABLE_KEY)) {
				Object table = reference.get(MUMBLE_TABLE_KEY);
				item.putAll(reference);
				walker.collectSymbolTable(alias, table);
				
			} else if (reference.containsKey(MUMBLE_SUBSTITUTION_KEY)) {
				// Check for Substitution Variable
				item.putAll(reference);
				// Collect Symbol Table Reference
				Map<String, Object> substitution = (Map<String, Object>) reference.get(MUMBLE_SUBSTITUTION_KEY);
				walker.collectSymbolTable(alias, substitution.get("name"));

			} else {// then it's a query, add it to the tree no matter what kind of query it is
				item.put(MUMBLE_QUERY_KEY, reference);
				// Add the query to the symbol table tree 
				Boolean done = collectQuerySymbolTable(MUMBLE_QUERY_KEY, item, alias, reference);
				if (!done)
					done = collectQuerySymbolTable(MUMBLE_INSERT_KEY, item, alias, reference);
				if (!done)
					done = collectQuerySymbolTable(MUMBLE_UPDATE_KEY, item, alias, reference);
				if (!done)
					done = collectQuerySymbolTable(MUMBLE_UNION_KEY, item, alias, reference);
				if (!done)
					done = collectQuerySymbolTable(MUMBLE_INTERSECT_KEY, item, alias, reference);
			}

			subMap.put(MUMBLE_TABLE_KEY, item);
		} else {
			walker.showTrace(walker.parseTrace, "Wrong number of entries: " + ctx.getText());
		}
		walker.addToParent(parentRuleIndex, parentStackLevel, subMap);
		walker.showTrace(walker.parseTrace, "TABLE PRIMARY: " + subMap);
	}
	

	@SuppressWarnings("unchecked")
	@Override
	public void exitTuple_primary(@NotNull SQLSelectParserParser.Tuple_primaryContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		int parentRuleIndex = ctx.getParent().getRuleIndex();

		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Integer parentStackLevel = walker.currentStackLevel(parentRuleIndex);

		Map<String, Object> subMap = walker.removeNodeMap(ruleIndex, stackLevel);
		Object type = subMap.remove("Type");
		Map<String, Object> item;
		String alias = null;

		item = new HashMap<String, Object>();
		Map<String, Object> reference = walker.checkForSubstitutionVariable((Map<String, Object>) subMap.remove("1"),
					"tuple");

		// Try various alternatives
		if (reference.containsKey(MUMBLE_TABLE_KEY)) {
			Object table = reference.get(MUMBLE_TABLE_KEY);
			 walker.symbolTable.put((String) table, new HashMap<String, Object>());
			 walker.tableDictionaryMap.put((String) table, new HashMap<String, Object>());
			subMap.put(MUMBLE_TABLE_KEY, reference);

		} else if (reference.containsKey(MUMBLE_VALUES_KEY)) {
			
			subMap.putAll(reference);
			
			// Symbol Table Construction
			HashMap<String, Object> symbols =  walker.symbolTable;
			// empty out symbol table
			 walker.symbolTable = (HashMap<String, Object>) item;
			// grab reference contents
			item = (Map<String, Object>) reference.get(MUMBLE_VALUES_KEY);
			// get alias from values
			alias=(String) item.get(MUMBLE_ALIAS_KEY);
			// set default alias if clause doesn't have one
			if (alias == null)
				alias = "unnamed";
			// Construct new Symbol Table
			String key = MUMBLE_VALUES_KEY + walker.queryCount;
			 walker.symbolTable.put(alias, key);
			
			HashMap<String, Object> queryBody =  new HashMap<String, Object>();
			queryBody.putAll(symbols);
			queryBody.put("interface",symbols.get(alias));
			
			 walker.symbolTable.put(key,symbols.get(alias));
			 walker.symbolTable.put("def_" + key, queryBody);
			
			
			// Construct Table Dictionary

//			TODO:??? convertSymbolTableToTableDictionary();
			
		} else if (reference.containsKey(MUMBLE_SUBSTITUTION_KEY)) {
			// Check for Substitution Variable
			Map<String, Object> substitution = (Map<String, Object>) reference.get(MUMBLE_SUBSTITUTION_KEY);
			// Collect Symbol Table Reference
			String name = (String) substitution.get("name");
			 walker.symbolTable.put(name, new HashMap<String, Object>());
			 walker.tableDictionaryMap.put(name, new HashMap<String, Object>());
			subMap.putAll(reference);

		} else { // then it's a query, add it to the tree no matter what kind of query it is
			item.put(MUMBLE_QUERY_KEY, reference);
			// Add the query to the symbol table tree 
			Boolean done = collectQuerySymbolTable(MUMBLE_QUERY_KEY, item, alias, reference);
			if (!done)
					done = collectQuerySymbolTable(MUMBLE_INSERT_KEY, item, alias, reference);
			if (!done)
					done = collectQuerySymbolTable(MUMBLE_UPDATE_KEY, item, alias, reference);
			if (!done)
					done = collectQuerySymbolTable(MUMBLE_UNION_KEY, item, alias, reference);
			if (!done)
					done = collectQuerySymbolTable(MUMBLE_INTERSECT_KEY, item, alias, reference);
			subMap.putAll(reference);
		}

		walker.addToParent(parentRuleIndex, parentStackLevel, subMap);
		walker.showTrace(walker.parseTrace, "TABLE PRIMARY: " + subMap);
	}
	

	private Boolean collectQuerySymbolTable(String hdr, Map<String, Object> item, String alias,
			Map<String, Object> reference) {
		String queryName = hdr + (walker.queryCount - 1);
		Map<String, Object> query = (Map<String, Object>)  walker.symbolTable.remove(queryName);
		if (query != null) {
//			item.put(hdr, reference);

			// add alias to query
			if (alias != null) 
				walker.collectSymbolTable(alias, queryName);
			else
				 walker.symbolTable.put(queryName, new HashMap<String, Object>());

			// propagate interface to outer layer of query
			Map<String, Object> hold = (Map<String, Object>)  walker.symbolTable.get(queryName);
			// Move unknowns to query
			Map<String, Object> unk = (Map<String, Object>)  walker.symbolTable.remove(MUMBLE_UNKNOWN_KEY);

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
					 walker.symbolTable.put(MUMBLE_UNKNOWN_KEY, unk);
			}
			// Add query definition back into symbol table
			 walker.symbolTable.put("def_" + queryName, query);
			return true;
		} else
			return false;
	}
	
	@Override
	public void exitTable_or_query_name(@NotNull SQLSelectParserParser.Table_or_query_nameContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Map<String, Object> subMap = walker.getNodeMap(ruleIndex, stackLevel);
		Object type = subMap.remove("Type");

		if (subMap.size() == 1) {
			walker.showTrace(walker.parseTrace, "Just One Identifier: " + subMap);
			String table = (String) subMap.remove("1");

			// try swapping names here
			table = getTableName(table);

			walker.collectSymbolTable(table, table);

			subMap.put(MUMBLE_TABLE_KEY, table);
			walker.showTrace(walker.parseTrace, "table: " + table + " Map: " + subMap);
		} else if (subMap.size() == 2) {
			walker.showTrace(walker.parseTrace, "Two entries: " + subMap);
			String schema = (String) subMap.remove("1");
			subMap.put(MUMBLE_SCHEMA_KEY, schema);
			String table = (String) subMap.remove("2");

			// try swapping names here
			table = getTableName(table);

			walker.collectSymbolTable(table, table);

			subMap.put(MUMBLE_TABLE_KEY, table);
			walker.showTrace(walker.parseTrace, "Schema: " + schema + " Table: " + table + " Map: " + subMap);
		} else if (subMap.size() == 3) {
			walker.showTrace(walker.parseTrace, "Three entries: " + subMap);
			String dbname = (String) subMap.remove("1");
			subMap.put(MUMBLE_DATABASE_NAME_KEY, dbname);
			String schema = (String) subMap.remove("2");
			subMap.put(MUMBLE_SCHEMA_KEY, schema);
			String table = (String) subMap.remove("3");

			// try swapping names here
			table = getTableName(table);

			walker.collectSymbolTable(table, table);

			subMap.put(MUMBLE_TABLE_KEY, table);
			walker.showTrace(walker.parseTrace, "Database: " + dbname + "Schema: " + schema + " Table: " + table + " Map: " + subMap);
		} else {
			walker.showTrace(walker.parseTrace, "Too many entries: " + subMap);
		}
	}

	@Override
	public void exitUnqualified_join(@NotNull SQLSelectParserParser.Unqualified_joinContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		int parentRuleIndex = ctx.getParent().getRuleIndex();

		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Integer parentStackLevel = walker.currentStackLevel(parentRuleIndex);

		Map<String, Object> subMap = walker.removeNodeMap(ruleIndex, stackLevel);
		walker.showTrace(walker.parseTrace, subMap);
		subMap.remove("Type");
		if (ctx.getChildCount() == 2)
			subMap.put(MUMBLE_JOIN_KEY, ctx.getText());
		else if (ctx.getChildCount() == 3) {
			String type = (String) subMap.remove("1");
			subMap.put(MUMBLE_JOIN_KEY, ctx.getChild(0).getText() + ctx.getChild(2).getText());
//			subMap.put(MUMBLE_JOIN_TYPE_KEY, type);
		}
		// Add item to parent map
		walker.addToParent(parentRuleIndex, parentStackLevel, subMap);
		walker.showTrace(walker.parseTrace, "UNQUALIFIED JOIN: " + subMap);
	}

	@Override
	public void exitQualified_join(@NotNull SQLSelectParserParser.Qualified_joinContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		int parentRuleIndex = ctx.getParent().getRuleIndex();

		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Integer parentStackLevel = walker.currentStackLevel(parentRuleIndex);

		Map<String, Object> subMap = walker.removeNodeMap(ruleIndex, stackLevel);

		if (subMap == null) {
			// Qualified join has no map
			subMap = walker.makeRuleMap(ruleIndex);
		} else
			subMap.remove("1");
		subMap.remove("Type");
			switch (ctx.getChildCount()) {
				case 1 -> subMap.put(MUMBLE_JOIN_KEY, ctx.getText());
				case 2 -> subMap.put(MUMBLE_JOIN_KEY, ctx.getChild(0).getText());
				case 3 -> subMap.put(MUMBLE_JOIN_KEY, ctx.getChild(0).getText() + ctx.getChild(1).getText());
				default -> {}
			}
		// Add item to parent map
		walker.addToParent(parentRuleIndex, parentStackLevel, subMap);
		walker.showTrace(walker.parseTrace, "QUALIFIED JOIN: " + subMap);
	}

	// join_type does NOT need its own methods
	
	@SuppressWarnings("unchecked")
	@Override
	public void exitJoin_specification(@NotNull SQLSelectParserParser.Join_specificationContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		int parentRuleIndex = ctx.getParent().getRuleIndex();

		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Integer parentStackLevel = walker.currentStackLevel(parentRuleIndex);

		Map<String, Object> subMap = walker.removeNodeMap(ruleIndex, stackLevel);
		Object type = subMap.remove("Type");
		Map<String, Object> item;
		if (subMap.size() == 1) {
			// get the most recent JOIN condition
			Map<String, Object> pMap = walker.getNodeMap(parentRuleIndex, parentStackLevel);
			Integer indx = pMap.size() - 2;
			Map<String, Object> join = (Map<String, Object>) pMap.get(indx.toString());
			// Add On clause to previous Join statement
			item = (Map<String, Object>) subMap.remove("1");
			join.put(MUMBLE_JOIN_ON_KEY, item);
			walker.showTrace(walker.parseTrace, "join On Clause: " + join);
		} else {
			walker.showTrace(walker.parseTrace, "Wrong number of entries: " + ctx.getText());
		}
		 walker.getAsTree().put("SKIP", "TRUE");
	}

	@Override
	public void exitJoin_condition(@NotNull SQLSelectParserParser.Join_conditionContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		int parentRuleIndex = ctx.getParent().getRuleIndex();

		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Integer parentStackLevel = walker.currentStackLevel(parentRuleIndex);

		Map<String, Object> subMap = walker.getNodeMap(ruleIndex, stackLevel);
		subMap = (Map<String, Object>) subMap.get("1");
		if (subMap.containsKey(MUMBLE_PARENTHESES_KEY)) {
			// Remove extraneous parentheses from the outermost layer of the On
			// Condition
			Map<String, Object> contents = (Map<String, Object>) subMap.remove(MUMBLE_PARENTHESES_KEY);
			subMap.putAll(contents);
		}

		// Now handle child as usual
		walker.handleOneChild(ruleIndex);
	}

	@Override
	public void exitNamed_columns_join(@NotNull SQLSelectParserParser.Named_columns_joinContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		walker.handleOneChild(ruleIndex);
	}
	
	// using_term does NOT need its own methods

	  
	/*
	===============================================================================
	  Column List clauses
	===============================================================================
	*/
	

	@Override
	public void exitColumn_reference_list(@NotNull SQLSelectParserParser.Column_reference_listContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Map<String, Object> subMap = walker.getNodeMap(ruleIndex, stackLevel);
		subMap.remove("Type");
	}

	@Override
	public void exitColumn_reference(@NotNull SQLSelectParserParser.Column_referenceContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Map<String, Object> subMap = walker.getNodeMap(ruleIndex, stackLevel);
		subMap.remove("Type");

		Map<String, Object> columnSubTree = new HashMap<String, Object>();
		Object columnRef = null;
		String tableRef = null;
		String tableRefKey = MUMBLE_UNKNOWN_KEY;
		Boolean doNotSkip = true;

		if (subMap.size() == 1) {
			walker.showTrace(walker.parseTrace, "Just One Identifier: " + subMap);
			columnRef = subMap.remove("1");
		} else if (subMap.size() == 2) {
			walker.showTrace(walker.parseTrace, "Two entries: " + subMap);
			// tableRefKey = "table_ref";
			tableRef = (String) subMap.remove("1");
			tableRefKey = tableRef;
			columnRef = subMap.remove("2");

		} else {
			walker.showTrace(walker.parseTrace, "Too many entries: " + subMap);
			doNotSkip = false;
		}
		if (doNotSkip) {
			// Add column to SQL AST Tree
			columnSubTree.put(MUMBLE_TABLE_REF_KEY, tableRef);
			if (columnRef instanceof HashMap<?, ?>) {
				// should be a substitution
				HashMap<String, Object> columnMap = (HashMap<String, Object>) columnRef;
				HashMap<String, Object> substitutionMap = (HashMap<String, Object>) columnMap.get(MUMBLE_SUBSTITUTION_KEY);
				substitutionMap.put(MUMBLE_TYPE_KEY, MUMBLE_COLUMN_KEY);

				// Add reference to Substitution Variables list
				walker.substitutionsMap.put((String) substitutionMap.get("name"), MUMBLE_COLUMN_KEY);

				columnSubTree.putAll((HashMap<String, Object>) columnRef);
			} else {
				columnSubTree.put(MUMBLE_NAME_KEY, columnRef);
			}
			subMap.put(MUMBLE_COLUMN_KEY, columnSubTree);

			// Capture  walker.symbolTable entry
			walker.collectSymbolTableItem(tableRefKey, columnRef, ctx.getStart());
		}
		walker.showTrace(walker.parseTrace, "Column Reference: " + subMap);
	}


	@Override
	public void exitColumn_primary(@NotNull SQLSelectParserParser.Column_primaryContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Map<String, Object> subMap = walker.getNodeMap(ruleIndex, stackLevel);
		subMap.remove("Type");

		Map<String, Object> columnSubTree = new HashMap<String, Object>();
		Object columnRef = null;
		String tableRef = null;
		String tableRefKey = MUMBLE_UNKNOWN_KEY;
		Boolean doNotSkip = true;

		if (subMap.size() == 1) {
			walker.showTrace(walker.parseTrace, "Just One Identifier: " + subMap);
			columnRef = subMap.remove("1");
		} else if (subMap.size() == 2) {
			walker.showTrace(walker.parseTrace, "Two entries: " + subMap);
			// tableRefKey = "table_ref";
			tableRef = (String) subMap.remove("1");
			tableRefKey = tableRef;
			columnRef = subMap.remove("2");

		} else {
			walker.showTrace(walker.parseTrace, "Too many entries: " + subMap);
			doNotSkip = false;
		}
		if (doNotSkip) {
			// Add column to SQL AST Tree
			columnSubTree.put(MUMBLE_TABLE_REF_KEY, tableRef);
			if (columnRef instanceof HashMap<?, ?>) {
				// should be a substitution
				HashMap<String, Object> columnMap = (HashMap<String, Object>) columnRef;
				HashMap<String, Object> substitutionMap = (HashMap<String, Object>) columnMap.get(MUMBLE_SUBSTITUTION_KEY);
				substitutionMap.put(MUMBLE_TYPE_KEY, MUMBLE_COLUMN_KEY);

				// Add reference to Substitution Variables list
				walker.substitutionsMap.put((String) substitutionMap.get("name"), MUMBLE_COLUMN_KEY);

				columnSubTree.putAll((HashMap<String, Object>) columnRef);
			} else {
				columnSubTree.put(MUMBLE_NAME_KEY, columnRef);
			}
			subMap.put(MUMBLE_COLUMN_KEY, columnSubTree);

			// Capture  walker.symbolTable entry
			walker.collectSymbolTableItem(tableRefKey, columnRef, ctx.getStart());
		}
		walker.showTrace(walker.parseTrace, "Column Reference: " + subMap);
	}

/*
===============================================================================
  Predicands <value expression primary>
===============================================================================
*/


	@Override
	public void exitPredicand_primary(@NotNull SQLSelectParserParser.Predicand_primaryContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		int parentRuleIndex = ctx.getParent().getRuleIndex();

		Map<String, Object> subMap = walker.getNodeMap(ruleIndex, stackLevel);
		walker.checkForSubstitutionVariable((Map<String, Object>) subMap.get("1"), "predicand");

		walker.handleOneChild(ruleIndex);
	}

	@Override
	public void exitValue_expression_primary(@NotNull SQLSelectParserParser.Value_expression_primaryContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		walker.handleOneChild(ruleIndex);
	}

	@Override
	public void exitParenthesized_value_expression(
			@NotNull SQLSelectParserParser.Parenthesized_value_expressionContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Map<String, Object> subMap = walker.getNodeMap(ruleIndex, stackLevel);
		Object type = subMap.remove("Type");

		if (subMap.size() == 1) {
			Map<String, Object> item = (Map<String, Object>) subMap.remove("1");
			subMap.put(MUMBLE_PARENTHESES_KEY, item);
			walker.showTrace(walker.parseTrace, "Parenthesed Clause: " + subMap);

		} else {
			walker.showTrace(walker.parseTrace, "Wrong number of entries: " + subMap);
		}
	}

	@Override
	public void exitNonparenthesized_value_expression_primary(
			@NotNull SQLSelectParserParser.Nonparenthesized_value_expression_primaryContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		walker.handleOneChild(ruleIndex);
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
		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Map<String, Object> subMap = walker.getNodeMap(ruleIndex, stackLevel);
		Object type = subMap.remove("Type");

		Map<String, Object> item = new HashMap<String, Object>();

		if (subMap.size() == 0) {
			item.put(MUMBLE_FUNCTION_NAME_KEY, "COUNT");
			item.put(MUMBLE_QUALIFIER_KEY, null);
			item.put(MUMBLE_PARAMETERS_KEY, "*");
			subMap.put(MUMBLE_FUNCTION_KEY, item);
		} else {
			walker.showTrace(walker.parseTrace, "Wrong number of entries: " + subMap);
		}
		walker.showTrace(walker.parseTrace, "Aggregate Function: " + subMap);
	}

	// Part of the <aggregate_function> rule
	@Override
	public void exitGeneral_set_function(@NotNull SQLSelectParserParser.General_set_functionContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Map<String, Object> subMap = walker.getNodeMap(ruleIndex, stackLevel);
		Object type = subMap.remove("Type");

		Map<String, Object> item = new HashMap<String, Object>();

		if (subMap.size() == 2) {
			item.put(MUMBLE_FUNCTION_NAME_KEY, subMap.remove("1"));
			item.put(MUMBLE_QUALIFIER_KEY, null);
			item.put(MUMBLE_PARAMETERS_KEY, subMap.remove("2"));
			subMap.put(MUMBLE_FUNCTION_KEY, item);
		} else if (subMap.size() == 3) {
			item.put(MUMBLE_FUNCTION_NAME_KEY, subMap.remove("1"));
			Map<String, Object> hold = (Map<String, Object>) subMap.remove("2");
			if (hold.containsKey("Type")) {
				Integer childKey = (Integer) hold.remove("Type");
				if (childKey == (Integer) SQLSelectParserParser.RULE_set_qualifier)
					item.putAll(hold);
			} else {
				item.put(MUMBLE_QUALIFIER_KEY, hold);
			}
			item.put(MUMBLE_PARAMETERS_KEY, subMap.remove("3"));
			subMap.put(MUMBLE_FUNCTION_KEY, item);
		} else {
			walker.showTrace(walker.parseTrace, "Wrong number of entries: " + subMap);
		}
		walker.showTrace(walker.parseTrace, "Aggregate Function: " + subMap);
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

			Integer stackLevel = walker.currentStackLevel(ruleIndex);
			Integer parentStackLevel = walker.currentStackLevel(parentRuleIndex);

			Map<String, Object> subMap = walker.removeNodeMap(ruleIndex, stackLevel);
			Object type = subMap.remove("Type");
			if (subMap.size() == 2) {
				// Variation 2: When contains CONDITIONS
				subMap.putAll((Map<String, Object>) subMap.remove("1"));
				subMap.putAll((Map<String, Object>) subMap.remove("2"));
			} else if (subMap.size() == 3) {
				// Variation 1: Case ITEM in implied equals formula with When
				// Predicand
				subMap.put(MUMBLE_ITEM_KEY, subMap.remove("1"));
				subMap.putAll((Map<String, Object>) subMap.remove("2"));
				subMap.putAll((Map<String, Object>) subMap.remove("3"));
			} else {
				walker.showTrace(walker.parseTrace, "Wrong number of entries: " + ctx.getText());
			}

			Map<String, Object> item = new HashMap<String, Object>();
			item.put(MUMBLE_CASE_KEY, subMap);
			walker.addToParent(parentRuleIndex, parentStackLevel, item);
			walker.showTrace(walker.parseTrace, "Case: " + item);

		}

		@Override
		public void exitWhen_clause_list(@NotNull SQLSelectParserParser.When_clause_listContext ctx) {
			int ruleIndex = ctx.getRuleIndex();
			int parentRuleIndex = ctx.getParent().getRuleIndex();

			Integer stackLevel = walker.currentStackLevel(ruleIndex);
			Integer parentStackLevel = walker.currentStackLevel(parentRuleIndex);

			Map<String, Object> subMap = walker.removeNodeMap(ruleIndex, stackLevel);
			Object type = subMap.remove("Type");
			Map<String, Object> item = new HashMap<String, Object>();
			if (subMap.size() >= 1) {
				item.put(MUMBLE_CLAUSES_KEY, subMap);
			} else {
				walker.showTrace(walker.parseTrace, "Wrong number of entries: " + ctx.getText());
			}
			walker.addToParent(parentRuleIndex, parentStackLevel, item);
			walker.showTrace(walker.parseTrace, "Case Variation 2, Without Item: When Clause List: " + item);
		}

		@Override
		public void exitSearched_when_clause(@NotNull SQLSelectParserParser.Searched_when_clauseContext ctx) {
			int ruleIndex = ctx.getRuleIndex();
			int parentRuleIndex = ctx.getParent().getRuleIndex();

			Integer stackLevel = walker.currentStackLevel(ruleIndex);
			Integer parentStackLevel = walker.currentStackLevel(parentRuleIndex);

			Map<String, Object> subMap = walker.removeNodeMap(ruleIndex, stackLevel);
			Object type = subMap.remove("Type");
			Map<String, Object> item = new HashMap<String, Object>();
			if (subMap.size() == 2) {
				item.put(MUMBLE_WHEN_KEY, subMap.remove("1"));
				item.put(MUMBLE_THEN_KEY, subMap.remove("2"));
			} else {
				walker.showTrace(walker.parseTrace, "Wrong number of entries: " + ctx.getText());
			}
			walker.addToParent(parentRuleIndex, parentStackLevel, item);
			walker.showTrace(walker.parseTrace, "Case Variation 2, Without Item: Case When Clause: " + item);

		}

		@Override
		public void exitWhen_value_list(@NotNull SQLSelectParserParser.When_value_listContext ctx) {
			int ruleIndex = ctx.getRuleIndex();
			int parentRuleIndex = ctx.getParent().getRuleIndex();

			Integer stackLevel = walker.currentStackLevel(ruleIndex);
			Integer parentStackLevel = walker.currentStackLevel(parentRuleIndex);

			Map<String, Object> subMap = walker.removeNodeMap(ruleIndex, stackLevel);
			Object type = subMap.remove("Type");
			Map<String, Object> item = new HashMap<String, Object>();
			if (subMap.size() >= 1) {
				item.put(MUMBLE_CLAUSES_KEY, subMap);
			} else {
				walker.showTrace(walker.parseTrace, "Wrong number of entries: " + ctx.getText());
			}
			walker.addToParent(parentRuleIndex, parentStackLevel, item);
			walker.showTrace(walker.parseTrace, "Case Variation 1, With Item: When Value List: " + item);
		}

		@Override
		public void exitWhen_value_clause(@NotNull SQLSelectParserParser.When_value_clauseContext ctx) {
			int ruleIndex = ctx.getRuleIndex();
			int parentRuleIndex = ctx.getParent().getRuleIndex();

			Integer stackLevel = walker.currentStackLevel(ruleIndex);
			Integer parentStackLevel = walker.currentStackLevel(parentRuleIndex);

			Map<String, Object> subMap = walker.removeNodeMap(ruleIndex, stackLevel);
			Object type = subMap.remove("Type");
			Map<String, Object> item = new HashMap<String, Object>();
			if (subMap.size() == 2) {
				item.put(MUMBLE_WHEN_KEY, subMap.remove("1"));
				item.put(MUMBLE_THEN_KEY, subMap.remove("2"));
			} else {
				walker.showTrace(walker.parseTrace, "Wrong number of entries: " + ctx.getText());
			}
			walker.addToParent(parentRuleIndex, parentStackLevel, item);
			walker.showTrace(walker.parseTrace, "Case Variation 1, With Item: When Value Clause: " + item);

		}

		@Override
		public void exitElse_clause(@NotNull SQLSelectParserParser.Else_clauseContext ctx) {
			int ruleIndex = ctx.getRuleIndex();
			int parentRuleIndex = ctx.getParent().getRuleIndex();

			Integer stackLevel = walker.currentStackLevel(ruleIndex);
			Integer parentStackLevel = walker.currentStackLevel(parentRuleIndex);

			Map<String, Object> subMap = walker.removeNodeMap(ruleIndex, stackLevel);
			Object type = subMap.remove("Type");
			Map<String, Object> item = new HashMap<String, Object>();
			if (subMap.size() == 1) {
				item.put(MUMBLE_ELSE_KEY, subMap.remove("1"));
			} else {
				walker.showTrace(walker.parseTrace, "Wrong number of entries: " + ctx.getText());
			}
			walker.addToParent(parentRuleIndex, parentStackLevel, item);
			walker.showTrace(walker.parseTrace, "Else Clause: " + item);

		}

		@Override
		public void exitCase_result(@NotNull SQLSelectParserParser.Case_resultContext ctx) {
			int ruleIndex = ctx.getRuleIndex();
			walker.handleOneChild(ruleIndex);
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
			Integer stackLevel = walker.currentStackLevel(ruleIndex);
			Map<String, Object> subMap = walker.getNodeMap(ruleIndex, stackLevel);
			Object type = subMap.remove("Type");

			Map<String, Object> item = new HashMap<String, Object>();

			if (subMap.size() == 3) {
				String function = (String) subMap.remove("1");
				item.put(MUMBLE_FUNCTION_NAME_KEY, function);
				item.put(MUMBLE_TYPE_KEY, function.toUpperCase());
				// ITEM 104: Set the substitution type if the cast statement has a variable
				item.put(MUMBLE_VALUE_KEY, walker.checkForSubstitutionVariable((Map<String, Object>) subMap.remove("2"), "predicand"));
				item.put(MUMBLE_DATATYPE_KEY, subMap.remove("3"));
				subMap.put(MUMBLE_FUNCTION_KEY, item);
			} else {
				walker.showTrace(walker.parseTrace, "Wrong number of entries: " + subMap);
			}
			walker.showTrace(walker.parseTrace, "CAST Function: " + subMap);
		}

		@Override
		public void exitData_type(@NotNull SQLSelectParserParser.Data_typeContext ctx) {
			int ruleIndex = ctx.getRuleIndex();
			walker.handleOneChild(ruleIndex);
		}


		@Override
		public void exitVariable_size_data_type(@NotNull SQLSelectParserParser.Variable_size_data_typeContext ctx) {
			int ruleIndex = ctx.getRuleIndex();
			Integer stackLevel = walker.currentStackLevel(ruleIndex);
			Map<String, Object> subMap = walker.getNodeMap(ruleIndex, stackLevel);
			Object type = subMap.remove("Type");

			if (subMap.size() >=1) {
				Object item = subMap.remove("1");
				if (item instanceof String)
					subMap.put(MUMBLE_TYPE_KEY, item);
				else
					subMap.put(MUMBLE_TYPE_KEY, ((HashMap<String, String>) item).get("1"));
			}
			
			if (subMap.size() == 2) {
				subMap.putAll((Map<String, Object>)  subMap.remove("2"));
			} else {
				walker.showTrace(walker.parseTrace, "Wrong number of entries: " + ctx.getText());
			}
			walker.showTrace(walker.parseTrace, "Variable Data Type: " + subMap);
		}


		@Override
		public void exitVariable_data_type_name(@NotNull SQLSelectParserParser.Variable_data_type_nameContext ctx) {
			int ruleIndex = ctx.getRuleIndex();
			int parentRuleIndex = ctx.getParent().getRuleIndex();

			Integer stackLevel = walker.currentStackLevel(ruleIndex);
			Integer parentStackLevel = walker.currentStackLevel(parentRuleIndex);

			Map<String, Object> subMap = walker.removeNodeMap(ruleIndex, stackLevel);

			subMap = walker.makeRuleMap(ruleIndex);
			subMap.remove("Type");
			
			if (ctx.getChildCount() == 1) {
				walker.showTrace(walker.parseTrace, "one word data type: " + ctx.getText());
				String part = ctx.getChild(0).getText().toUpperCase();
				subMap.put("1", part);
			} else if (ctx.getChildCount() == 2) {
				walker.showTrace(walker.parseTrace, "two word data type: " + ctx.getText());
				String part = ctx.getChild(0).getText().toUpperCase();
				part = part + " " + ctx.getChild(1).getText().toUpperCase();
				subMap.put("1", part);
			} else if (ctx.getChildCount() == 3) {
				walker.showTrace(walker.parseTrace, "three word data type: " + ctx.getText());
				String part = ctx.getChild(0).getText().toUpperCase();
				part = part + " " + ctx.getChild(1).getText().toUpperCase();
				part = part + " " + ctx.getChild(2).getText().toUpperCase();
				subMap.put("1", part);
			}
			// Add item to parent map
			walker.addToParent(parentRuleIndex, parentStackLevel, subMap);

			walker.showTrace(walker.parseTrace, "Variable Data Type Name: " + subMap);
		}

		@Override
		public void exitType_length(@NotNull SQLSelectParserParser.Type_lengthContext ctx) {
			int ruleIndex = ctx.getRuleIndex();
			int parentRuleIndex = ctx.getParent().getRuleIndex();

			Integer stackLevel = walker.currentStackLevel(ruleIndex);
			Integer parentStackLevel = walker.currentStackLevel(parentRuleIndex);

			Map<String, Object> subMap = walker.removeNodeMap(ruleIndex, stackLevel);

			if (subMap == null) {
				// unqualified select all has no map
				subMap = walker.makeRuleMap(ruleIndex);
			}
			subMap.remove("Type");
			if (ctx.getChildCount() == 3) {
				walker.showTrace(walker.parseTrace, "Three entries: " + ctx.getText());
				subMap.put(MUMBLE_LENGTH_KEY, ctx.getChild(1).getText());
			}
			// Add item to parent map
			walker.addToParent(parentRuleIndex, parentStackLevel, subMap);
			walker.showTrace(walker.parseTrace, "Data Type Length: " + subMap);
		}


		@Override
		public void exitPrecision_scale_data_type(@NotNull SQLSelectParserParser.Precision_scale_data_typeContext ctx) {
			int ruleIndex = ctx.getRuleIndex();
			Integer stackLevel = walker.currentStackLevel(ruleIndex);
			Map<String, Object> subMap = walker.getNodeMap(ruleIndex, stackLevel);
			Object type = subMap.remove("Type");

			if (subMap.size() >=1) {
				Object item = subMap.remove("1");
				if (item instanceof String)
					subMap.put(MUMBLE_TYPE_KEY, item);
				else
					subMap.put(MUMBLE_TYPE_KEY, ((HashMap<String, String>) item).get("1"));
			}
			
			if (subMap.size() == 2) {
				subMap.putAll((Map<String, Object>)  subMap.remove("2"));
			} else {
				walker.showTrace(walker.parseTrace, "Wrong number of entries: " + ctx.getText());
			}
			walker.showTrace(walker.parseTrace, "Precision Data Type: " + subMap);
		}


		@Override
		public void exitPrecision_data_type_name(@NotNull SQLSelectParserParser.Precision_data_type_nameContext ctx) {
			int ruleIndex = ctx.getRuleIndex();
			int parentRuleIndex = ctx.getParent().getRuleIndex();

			Integer stackLevel = walker.currentStackLevel(ruleIndex);
			Integer parentStackLevel = walker.currentStackLevel(parentRuleIndex);

			Map<String, Object> subMap = walker.removeNodeMap(ruleIndex, stackLevel);

			subMap = walker.makeRuleMap(ruleIndex);
			subMap.remove("Type");
			
			if (ctx.getChildCount() == 1) {
				walker.showTrace(walker.parseTrace, "one word data type: " + ctx.getText());
				String part = ctx.getChild(0).getText().toUpperCase();
				subMap.put("1", part);
			} else if (ctx.getChildCount() == 2) {
				walker.showTrace(walker.parseTrace, "two word data type: " + ctx.getText());
				String part = ctx.getChild(0).getText().toUpperCase();
				part = part + " " + ctx.getChild(1).getText().toUpperCase();
				subMap.put("1", part);
			}
			// Add item to parent map
			walker.addToParent(parentRuleIndex, parentStackLevel, subMap);

			walker.showTrace(walker.parseTrace, "Precision Data Type Name: " + subMap);
		}

		@Override
		public void exitPrecision_param(@NotNull SQLSelectParserParser.Precision_paramContext ctx) {
			int ruleIndex = ctx.getRuleIndex();
			int parentRuleIndex = ctx.getParent().getRuleIndex();

			Integer stackLevel = walker.currentStackLevel(ruleIndex);
			Integer parentStackLevel = walker.currentStackLevel(parentRuleIndex);

			Map<String, Object> subMap = walker.removeNodeMap(ruleIndex, stackLevel);
			subMap = walker.makeRuleMap(ruleIndex);
			subMap.remove("Type");
			
			if (ctx.getChildCount() == 3) {
				walker.showTrace(walker.parseTrace, "Three entries: " + ctx.getText());
				subMap.put(MUMBLE_PRECISION_KEY, ctx.getChild(1).getText());
			} else if (ctx.getChildCount() == 5) {
				walker.showTrace(walker.parseTrace, "Three entries: " + ctx.getText());
				subMap.put(MUMBLE_PRECISION_KEY, ctx.getChild(1).getText());
				subMap.put(MUMBLE_SCALE_KEY, ctx.getChild(3).getText());
			} 
			// Add item to parent map
			walker.addToParent(parentRuleIndex, parentStackLevel, subMap);
			walker.showTrace(walker.parseTrace, "Precision Param: " + subMap);
		}


		@Override
		public void exitStatic_data_type(@NotNull SQLSelectParserParser.Static_data_typeContext ctx) {
			int ruleIndex = ctx.getRuleIndex();
			Integer stackLevel = walker.currentStackLevel(ruleIndex);
			Map<String, Object> subMap = walker.getNodeMap(ruleIndex, stackLevel);
			Object type = subMap.remove("Type");

			if (subMap.size() ==1) {
				Object item = subMap.remove("1");
				if (item instanceof String)
					subMap.put(MUMBLE_TYPE_KEY, item);
				else
					subMap.put(MUMBLE_TYPE_KEY, ((HashMap<String, String>) item).get("1"));
			} else {
				walker.showTrace(walker.parseTrace, "Wrong number of entries: " + ctx.getText());
			}
			walker.showTrace(walker.parseTrace, "Static Data Type: " + subMap);
		}

		@Override
		public void exitStatic_data_type_name(@NotNull SQLSelectParserParser.Static_data_type_nameContext ctx) {
			int ruleIndex = ctx.getRuleIndex();
			int parentRuleIndex = ctx.getParent().getRuleIndex();

			Integer stackLevel = walker.currentStackLevel(ruleIndex);
			Integer parentStackLevel = walker.currentStackLevel(parentRuleIndex);

			Map<String, Object> subMap = walker.removeNodeMap(ruleIndex, stackLevel);

			subMap = walker.makeRuleMap(ruleIndex);
			subMap.remove("Type");
			
			if (ctx.getChildCount() == 1) {
				walker.showTrace(walker.parseTrace, "one word data type: " + ctx.getText());
				String part = ctx.getChild(0).getText().toUpperCase();
				subMap.put("1", part);
			} else if (ctx.getChildCount() == 2) {
				walker.showTrace(walker.parseTrace, "two word data type: " + ctx.getText());
				String part = ctx.getChild(0).getText().toUpperCase();
				part = part + " " + ctx.getChild(1).getText().toUpperCase();
				subMap.put("1", part);
			} else if (ctx.getChildCount() == 3) {
				walker.showTrace(walker.parseTrace, "three word data type: " + ctx.getText());
				String part = ctx.getChild(0).getText().toUpperCase();
				part = part + " " + ctx.getChild(1).getText().toUpperCase();
				part = part + " " + ctx.getChild(2).getText().toUpperCase();
				subMap.put("1", part);
			} else if (ctx.getChildCount() == 4) {
				walker.showTrace(walker.parseTrace, "four word data type: " + ctx.getText());
				String part = ctx.getChild(0).getText().toUpperCase();
				part = part + " " + ctx.getChild(1).getText().toUpperCase();
				part = part + " " + ctx.getChild(2).getText().toUpperCase();
				part = part + " " + ctx.getChild(3).getText().toUpperCase();
				subMap.put("1", part);
			}
			// Add item to parent map
			walker.addToParent(parentRuleIndex, parentStackLevel, subMap);
			walker.showTrace(walker.parseTrace, "Static Data Type: " + subMap);
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

			Integer stackLevel = walker.currentStackLevel(ruleIndex);
			Integer parentStackLevel = walker.currentStackLevel(parentRuleIndex);

			Map<String, Object> subMap = walker.getNodeMap(ruleIndex, stackLevel);
			Object type = subMap.remove("Type");

			if (subMap.size() == 2) {
				walker.showTrace(walker.parseTrace, "Window Over Partition: " + subMap);
				Map<String, Object> item = new HashMap<String, Object>();
				item.putAll((Map<String, Object>) subMap.remove("1"));
				item.putAll((Map<String, Object>) subMap.remove("2"));
				subMap.put(MUMBLE_WINDOW_FUNCTION_KEY, item);
			} else {
				walker.showTrace(walker.parseTrace, "Incorrect number of entries: " + subMap);
			}
		}

		@SuppressWarnings("unchecked")
		@Override
		public void exitWindow_function(@NotNull SQLSelectParserParser.Window_functionContext ctx) {
			int ruleIndex = ctx.getRuleIndex();
			int parentRuleIndex = ctx.getParent().getRuleIndex();

			Integer stackLevel = walker.currentStackLevel(ruleIndex);
			Integer parentStackLevel = walker.currentStackLevel(parentRuleIndex);

			Map<String, Object> subMap = walker.removeNodeMap(ruleIndex, stackLevel);
			Object type = subMap.remove("Type");
			String functType = (String) subMap.remove("1");

			Map<String, Object> item = new HashMap<String, Object>();
			Map<String, Object> hold = new HashMap<String, Object>();

			if (subMap.size() == 0) {
				item.put(MUMBLE_PARAMETERS_KEY, null);
			} else if (subMap.size() >= 1) {
				hold = (Map<String, Object>) subMap.remove("2");
				type = hold.remove("Type");
				item.put(MUMBLE_PARAMETERS_KEY, hold.remove(type.toString()));
			} else {
				walker.showTrace(walker.parseTrace, "Wrong number of entries: " + ctx.getText());
			}
			
			item.put(MUMBLE_FUNCTION_NAME_KEY, functType);

			if (subMap.containsKey("3")) {
				item.putAll((Map<String, Object>) subMap.remove("3"));
			}
			if (subMap.containsKey("4")) {
				item.putAll((Map<String, Object>) subMap.remove("4"));
			}
			
			subMap.put(MUMBLE_FUNCTION_KEY, item);

			walker.addToParent(parentRuleIndex, parentStackLevel, subMap);
			walker.showTrace(walker.parseTrace, "WINDOW FUNCTION: " + subMap);
		}

		@SuppressWarnings("unchecked")
		@Override
		public void exitOver_clause(@NotNull SQLSelectParserParser.Over_clauseContext ctx) {
			int ruleIndex = ctx.getRuleIndex();
			int parentRuleIndex = ctx.getParent().getRuleIndex();

			Integer stackLevel = walker.currentStackLevel(ruleIndex);
			Integer parentStackLevel = walker.currentStackLevel(parentRuleIndex);

			Map<String, Object> subMap = walker.getNodeMap(ruleIndex, stackLevel);
			Object type = subMap.remove("Type");

			HashMap<String, Object> item = new HashMap<String, Object>();
			if (subMap.size() == 0) {
				subMap.put(MUMBLE_OVER_KEY, null);
			} else if (subMap.size() == 1) {
				item.putAll((Map<String, Object>) subMap.remove("1"));
				subMap.put(MUMBLE_OVER_KEY, item);
			} else if (subMap.size() == 2) {
				item.putAll((Map<String, Object>) subMap.remove("1"));
				item.putAll((Map<String, Object>) subMap.remove("2"));
				subMap.put(MUMBLE_OVER_KEY, item);
			} else if (subMap.size() == 3) {
				item.putAll((Map<String, Object>) subMap.remove("1"));
				item.putAll((Map<String, Object>) subMap.remove("2"));
				item.putAll((Map<String, Object>) subMap.remove("3"));
				subMap.put(MUMBLE_OVER_KEY, item);
			} else {
				walker.showTrace(walker.parseTrace, "Wrong number of entries: " + subMap);
			}
		}

		@SuppressWarnings("unchecked")
		@Override
		public void exitPartition_by_clause(@NotNull SQLSelectParserParser.Partition_by_clauseContext ctx) {
			int ruleIndex = ctx.getRuleIndex();
			int parentRuleIndex = ctx.getParent().getRuleIndex();

			Integer stackLevel = walker.currentStackLevel(ruleIndex);
			Integer parentStackLevel = walker.currentStackLevel(parentRuleIndex);

			Map<String, Object> subMap = walker.removeNodeMap(ruleIndex, stackLevel);
			Object type = subMap.remove("Type");

			if (subMap.size() >= 1) {
				HashMap<String, Object> item = (HashMap<String, Object>) subMap.remove("1");
				type = item.remove("Type");

				item.put(MUMBLE_PARTITION_BY_KEY, item.remove(type.toString()));
				walker.addToParent(parentRuleIndex, parentStackLevel, item);
			} else {
				walker.showTrace(walker.parseTrace, "Not enough entries: " + subMap);
			}

		}


		@Override
		public void exitBracket_frame_clause(
				@NotNull SQLSelectParserParser.Bracket_frame_clauseContext ctx) {
			int ruleIndex = ctx.getRuleIndex();
			int parentRuleIndex = ctx.getParent().getRuleIndex();

			Integer stackLevel = walker.currentStackLevel(ruleIndex);
			Integer parentStackLevel = walker.currentStackLevel(parentRuleIndex);

			Map<String, Object> subMap = walker.getNodeMap(ruleIndex, stackLevel);
			Object type = subMap.remove("Type");

			if (subMap.size() == 2) {
				walker.showTrace(walker.parseTrace, "Window Over Partition: " + subMap);
				Map<String, Object> item = new HashMap<String, Object>();
				item.put(MUMBLE_TYPE_KEY, (String) subMap.remove("1"));
				item.putAll((Map<String, Object>) subMap.remove("2"));
				subMap.put(MUMBLE_BRACKET_FRAME_KEY, item);
			} else {
				walker.showTrace(walker.parseTrace, "Incorrect number of entries: " + subMap);
			}
		}

		// rows_or_range clauses do not need their own method
		

		@Override
		public void exitBracket_frame_definition(@NotNull SQLSelectParserParser.Bracket_frame_definitionContext ctx) {
			int ruleIndex = ctx.getRuleIndex();
			walker.handleOneChild(ruleIndex);
		}

		@Override
		public void exitBetween_frame_definition(@NotNull SQLSelectParserParser.Between_frame_definitionContext ctx) {
			int ruleIndex = ctx.getRuleIndex();
			int parentRuleIndex = ctx.getParent().getRuleIndex();

			Integer stackLevel = walker.currentStackLevel(ruleIndex);
			Integer parentStackLevel = walker.currentStackLevel(parentRuleIndex);

			Map<String, Object> subMap = walker.getNodeMap(ruleIndex, stackLevel);
			Object type = subMap.remove("Type");

			if (subMap.size() == 2) {
				walker.showTrace(walker.parseTrace, "Window Over Partition: " + subMap);
				Map<String, Object> item = new HashMap<String, Object>();
				item.put(MUMBLE_RANGE_BEGIN_KEY,  subMap.remove("1"));
				item.put(MUMBLE_RANGE_END_KEY,  subMap.remove("2"));
				subMap.put(MUMBLE_BETWEEN_KEY, item);
			} else {
				walker.showTrace(walker.parseTrace, "Incorrect number of entries: " + subMap);
			}
		}


		@Override
		public void exitFrame_edge(@NotNull SQLSelectParserParser.Frame_edgeContext ctx) {
			int ruleIndex = ctx.getRuleIndex();
			walker.handleOneChild(ruleIndex);
		}

		@Override
		public void exitPreceding_frame_edge(
				@NotNull SQLSelectParserParser.Preceding_frame_edgeContext ctx) {
			int ruleIndex = ctx.getRuleIndex();
			Integer stackLevel = walker.currentStackLevel(ruleIndex);
			Map<String, Object> subMap = walker.getNodeMap(ruleIndex, stackLevel);
			Object type = subMap.remove("Type");

			if (subMap.size() == 1) {
				subMap.put(MUMBLE_VALUE_KEY, (String) subMap.remove("1"));
				subMap.put(MUMBLE_BRACKET_DIRECTION_KEY, MUMBLE_PRECEDING_KEY);
				walker.showTrace(walker.parseTrace, "Preceding Edge Clause: " + subMap);

			} else {
				walker.showTrace(walker.parseTrace, "Wrong number of entries: " + subMap);
			}
		}

		@Override
		public void exitFollowing_frame_edge(
				@NotNull SQLSelectParserParser.Following_frame_edgeContext ctx) {
			int ruleIndex = ctx.getRuleIndex();
			Integer stackLevel = walker.currentStackLevel(ruleIndex);
			Map<String, Object> subMap = walker.getNodeMap(ruleIndex, stackLevel);
			Object type = subMap.remove("Type");

			if (subMap.size() == 1) {
				subMap.put(MUMBLE_VALUE_KEY, (String) subMap.remove("1"));
				subMap.put(MUMBLE_BRACKET_DIRECTION_KEY, MUMBLE_FOLLOWING_KEY);
				walker.showTrace(walker.parseTrace, "Preceding Edge Clause: " + subMap);

			} else {
				walker.showTrace(walker.parseTrace, "Wrong number of entries: " + subMap);
			}
		}


		@Override
		public void exitCurrent_row_edge(@NotNull SQLSelectParserParser.Current_row_edgeContext ctx) {
			int ruleIndex = ctx.getRuleIndex();
			int parentRuleIndex = ctx.getParent().getRuleIndex();

			Integer stackLevel = walker.currentStackLevel(ruleIndex);
			Integer parentStackLevel = walker.currentStackLevel(parentRuleIndex);

			Map<String, Object> subMap = walker.removeNodeMap(ruleIndex, stackLevel);

			subMap = walker.makeRuleMap(ruleIndex);
			subMap.remove("Type");
			
			if (ctx.getChildCount() == 2) {
				walker.showTrace(walker.parseTrace, "two word frame edge: " + ctx.getText());
				String part = ctx.getChild(0).getText().toUpperCase();
				part = part + " " + ctx.getChild(1).getText().toUpperCase();
				subMap.put(MUMBLE_VALUE_KEY, part);
			} else  {
				walker.showTrace(walker.parseTrace, "incorrect phrase");
			}
			// Add item to parent map
			walker.addToParent(parentRuleIndex, parentStackLevel, subMap);
			walker.showTrace(walker.parseTrace, "Static Data Type: " + subMap);
		}

		// item_select_function does NOT need its own exit method

		@Override
		public void exitSelect_direction(@NotNull SQLSelectParserParser.Select_directionContext ctx) {
			int ruleIndex = ctx.getRuleIndex();
			int parentRuleIndex = ctx.getParent().getRuleIndex();

			Integer stackLevel = walker.currentStackLevel(ruleIndex);
			Integer parentStackLevel = walker.currentStackLevel(parentRuleIndex);

			Map<String, Object> subMap = walker.removeNodeMap(ruleIndex, stackLevel);

			if (subMap == null) {
				// unqualified select all has no map
				subMap = walker.makeRuleMap(ruleIndex);
			}
			subMap.remove("Type");
			if (ctx.getChildCount() == 2) {
				subMap.put(MUMBLE_SELECT_DIRECTION_KEY, ctx.getChild(1).getText());
			}
			// Add item to parent map
			walker.addToParent(parentRuleIndex, parentStackLevel, subMap);
		}

		@Override
		public void exitNull_handling(@NotNull SQLSelectParserParser.Null_handlingContext ctx) {
			int ruleIndex = ctx.getRuleIndex();
			int parentRuleIndex = ctx.getParent().getRuleIndex();

			Integer stackLevel = walker.currentStackLevel(ruleIndex);
			Integer parentStackLevel = walker.currentStackLevel(parentRuleIndex);

			Map<String, Object> subMap = walker.removeNodeMap(ruleIndex, stackLevel);

			if (subMap == null) {
				// unqualified select all has no map
				subMap = walker.makeRuleMap(ruleIndex);
			}
			subMap.remove("Type");
			if (ctx.getChildCount() == 2) {
				subMap.put(MUMBLE_NULL_HANDLING_KEY, ctx.getChild(0).getText());
			}
			// Add item to parent map
			walker.addToParent(parentRuleIndex, parentStackLevel, subMap);
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

		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Integer parentStackLevel = walker.currentStackLevel(parentRuleIndex);

		Map<String, Object> subMap = walker.removeNodeMap(ruleIndex, stackLevel);

		Map<String, Object> item = new HashMap<String, Object>();

		if (subMap == null) {
			// unqualified select all has no map
			subMap = walker.makeRuleMap(ruleIndex);
		}
		subMap.remove("Type");
		if (ctx.getChildCount() == 1) {
			walker.showTrace(walker.parseTrace, "Just One Identifier: " + ctx.getText());
			item.put(MUMBLE_NAME_KEY, ctx.getChild(0).getText());
			subMap.put(MUMBLE_SUBSTITUTION_KEY, item);
		}
		// Add item to parent map
		walker.addToParent(parentRuleIndex, parentStackLevel, subMap);
		walker.showTrace(walker.parseTrace, "Substitution Variable: " + subMap);
	}

	@Override
	public void exitExtended_variable_identifier(@NotNull SQLSelectParserParser.Extended_variable_identifierContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		int parentRuleIndex = ctx.getParent().getRuleIndex();

		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Integer parentStackLevel = walker.currentStackLevel(parentRuleIndex);

		Map<String, Object> subMap = walker.removeNodeMap(ruleIndex, stackLevel);

		Map<String, Object> item = new HashMap<String, Object>();
		Map<String, Object> subItem = new HashMap<String, Object>();

		if (subMap == null) {
			// unqualified select all has no map
			subMap = walker.makeRuleMap(ruleIndex);
		}
		subMap.remove("Type");
		if (ctx.getChildCount() == 1) {
			walker.showTrace(walker.parseTrace, "Just One Identifier: " + ctx.getText());
			String variable_name = ctx.getChild(0).getText();
			item.put(MUMBLE_NAME_KEY, variable_name);
			item.put(MUMBLE_PARTS_KEY, subItem);
			String[] trim = variable_name.split("\\.",0);
			// Added 9/16/2021 GAH: Allow 5 part variable names
			if (trim.length == 5) {
				subItem.put("1", trim[0].substring(1));
				subItem.put("2", trim[1]);
				subItem.put("3", trim[2]); 
				subItem.put("4", trim[3]); 
				subItem.put("5", trim[4].substring(0, trim[4].length()-1)); 
			} else if (trim.length == 4) {
				subItem.put("1", trim[0].substring(1));
				subItem.put("2", trim[1]);
				subItem.put("3", trim[2]); 
				subItem.put("4", trim[3].substring(0, trim[3].length()-1)); 
			} else if (trim.length == 3) {
				subItem.put("1", trim[0].substring(1));
				subItem.put("2", trim[1]);
				subItem.put("3", trim[2].substring(0, trim[2].length()-1));
			} else if (trim.length == 2) {
				subItem.put("1", trim[0].substring(1));
				subItem.put("2", trim[1].substring(0, trim[1].length()-1));
			} else {
				subItem.put("1", variable_name.substring(1, variable_name.length()-1));				
			}
			subMap.put(MUMBLE_SUBSTITUTION_KEY, item);
		}
		// Add item to parent map
		walker.addToParent(parentRuleIndex, parentStackLevel, subMap);
		walker.showTrace(walker.parseTrace, "Substitution Variable: " + subMap);
	}

	@Override
	public void exitWhere_clause(@NotNull SQLSelectParserParser.Where_clauseContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		walker.handlePushDown(ruleIndex);
	}

	@Override
	public void exitGroupby_clause(@NotNull SQLSelectParserParser.Groupby_clauseContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		walker.handlePushDown(ruleIndex);
	}

	@Override
	public void exitHaving_clause(@NotNull SQLSelectParserParser.Having_clauseContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		int stackLevel = walker.currentStackLevel(ruleIndex);

		Map<String, Object> subMap = walker.getNodeMap(ruleIndex, stackLevel);
		walker.handlePushDown(ruleIndex);
//		walker.handleOneChild(ruleIndex);
	}

	@Override
	public void exitOrderby_clause(@NotNull SQLSelectParserParser.Orderby_clauseContext ctx) {
		// TODO: ITEM 36 - Add Substitution Variables to Order By: Subs Variable
		// List, Table Dictionary, Symbol Table, AST Tree
		int ruleIndex = ctx.getRuleIndex();
		int parentRuleIndex = ctx.getParent().getRuleIndex();
		if (parentRuleIndex == (Integer) SQLSelectParserParser.RULE_over_clause) {

			Integer stackLevel = walker.currentStackLevel(ruleIndex);
			Integer parentStackLevel = walker.currentStackLevel(parentRuleIndex);

			Map<String, Object> subMap = walker.removeNodeMap(ruleIndex, stackLevel);
			// Part of a window function
			subMap.remove("Type");
			Map<String, Object> item = new HashMap<String, Object>();
			item.put(MUMBLE_ORDERBY_KEY, subMap);

			walker.addToParent(parentRuleIndex, parentStackLevel, item);
		} else
			// Normal order by clause
			walker.handlePushDown(ruleIndex);
	}


	@Override
	public void exitLimit_clause(@NotNull SQLSelectParserParser.Limit_clauseContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		int parentRuleIndex = ctx.getParent().getRuleIndex();

		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Integer parentStackLevel = walker.currentStackLevel(parentRuleIndex);

		Map<String, Object> subMap = walker.getNodeMap(ruleIndex, stackLevel);
//		Object type = subMap.remove("Type");

		if (subMap.size() == 2) {
			walker.showTrace(walker.parseTrace, "Limit only: " + subMap);

			Map<String, Object> left = (Map<String, Object>) subMap.remove("1");
			subMap.putAll(left);

			walker.showTrace(walker.parseTrace, "Limit only: " + subMap);
			walker.handlePushDown(ruleIndex);
			
		} else if (subMap.size() == 3) {
			walker.showTrace(walker.parseTrace, "Limit AND OFFSET: " + subMap);

			Map<String, Object> limit = (Map<String, Object>) subMap.remove("1");

			Map<String, Object>  offsetObj = (Map<String, Object> )  subMap.remove("2");
			Object offset = (Object) offsetObj.remove(MUMBLE_LITERAL_KEY);
			subMap.put(MUMBLE_OFFSET_KEY, offset);
			subMap.putAll(limit);

			walker.showTrace(walker.parseTrace, "LIMIT AND OFFSET: " + subMap);
			walker.handlePushDown(ruleIndex);

		} else {
			walker.showTrace(walker.parseTrace, "Wrong number of entries: " + subMap);
		}
	}


	@Override
	public void exitSearch_condition(@NotNull SQLSelectParserParser.Search_conditionContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		int parentRuleIndex = ctx.getParent().getRuleIndex();

		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Integer parentStackLevel = walker.currentStackLevel(parentRuleIndex);

		Map<String, Object> subMap = walker.getNodeMap(ruleIndex, stackLevel);

		walker.handleOneChild(ruleIndex);
	}

	@Override
	public void exitOr_predicate(@NotNull SQLSelectParserParser.Or_predicateContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		String key = MUMBLE_OR_KEY;

		walker.handleOperandList(ruleIndex, key);
	}

	@Override
	public void exitAnd_predicate(@NotNull SQLSelectParserParser.And_predicateContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		String key = MUMBLE_AND_KEY;

		walker.handleOperandList(ruleIndex, key);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void exitNegative_predicate(@NotNull SQLSelectParserParser.Negative_predicateContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Map<String, Object> subMap = walker.getNodeMap(ruleIndex, stackLevel);
		Object type = subMap.remove("Type");

		if (subMap.size() == 1) {
			walker.showTrace(walker.parseTrace, "Not negated predicate: " + subMap);

			Map<String, Object> left = (Map<String, Object>) subMap.remove("1");
			subMap.putAll(left);

			walker.showTrace(walker.parseTrace, "Negated predicate: " + subMap);

		} else if (subMap.size() == 2) {
			walker.showTrace(walker.parseTrace, "Negated predicate: " + subMap);

			String negation = (String) subMap.remove("1");

			Map<String, Object> left = (Map<String, Object>) subMap.remove("2");
			subMap.put(negation, left);

			walker.showTrace(walker.parseTrace, "Negated predicate: " + subMap);

		} else {
			walker.showTrace(walker.parseTrace, "Wrong number of entries: " + subMap);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void exitBasic_predicate_clause(@NotNull SQLSelectParserParser.Basic_predicate_clauseContext ctx) {
		// {condition={left={substitution={name=<subject code>,
		// type=predicand}}, operator=is true}}
		int ruleIndex = ctx.getRuleIndex();
		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Map<String, Object> subMap = walker.getNodeMap(ruleIndex, stackLevel);
		Object type = subMap.remove("Type");

		if (subMap.size() == 1) {
			Map<String, Object> left = (Map<String, Object>) subMap.remove("1");
			subMap.putAll(left);
			walker.showTrace(walker.parseTrace, "Clause: " + subMap);
		} else if (subMap.size() == 2) {
			// Grammar peculiarity results in Substitution Variable
			// mislabelled as a condition when it should be a predicand.
			// Fixing it here
			Map<String, Object> item = (Map<String, Object>) subMap.remove("1");
			if (item.containsKey(MUMBLE_SUBSTITUTION_KEY)) {
				HashMap<String, Object> hold = (HashMap<String, Object>) item.get(MUMBLE_SUBSTITUTION_KEY);
				hold.put(MUMBLE_TYPE_KEY, "predicand");
				walker.substitutionsMap.put((String) hold.get("name"), "predicand");
			}
			HashMap<String, Object> hold = new HashMap<String, Object>();
			hold.put(MUMBLE_LEFT_FACTOR_KEY, item);
			subMap.put(MUMBLE_CONDITION_KEY, hold);

			item = (Map<String, Object>) subMap.remove("2");
			subMap.putAll(item);
			walker.showTrace(walker.parseTrace, "Clause: " + subMap);

		} else {
			walker.showTrace(walker.parseTrace, "Wrong number of entries: " + subMap);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void exitSubstitution_predicate(@NotNull SQLSelectParserParser.Substitution_predicateContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		int parentRuleIndex = ctx.getParent().getRuleIndex();

		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Map<String, Object> subMap = walker.getNodeMap(ruleIndex, stackLevel);
		Object type = subMap.remove("Type");

		if (subMap.size() == 1) {
			Map<String, Object> left = (Map<String, Object>) subMap.remove("1");
			subMap.putAll(left);
			// If the clause remaining is an embedded Condition Substitution
			// Variable, this captures and labels it
			subMap = walker.checkForSubstitutionVariable(subMap, MUMBLE_CONDITION_KEY);
			walker.showTrace(walker.parseTrace, "Clause: " + subMap);
		} else {
			walker.showTrace(walker.parseTrace, "Wrong number of entries: " + subMap);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void exitNull_predicate(@NotNull SQLSelectParserParser.Null_predicateContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Map<String, Object> subMap = walker.getNodeMap(ruleIndex, stackLevel);
		Object type = subMap.remove("Type");

		if (subMap.size() == 2) {
			walker.showTrace(walker.parseTrace, "Comparison: " + subMap);
			Map<String, Object> condition = new HashMap<String, Object>();
			Map<String, Object> left = (Map<String, Object>) subMap.remove("1");
			condition.put(MUMBLE_LEFT_FACTOR_KEY, left);

			condition.putAll((Map<String, Object>) subMap.remove("2"));

			subMap.put(MUMBLE_CONDITION_KEY, condition);
			walker.showTrace(walker.parseTrace, "IS NULL Clause: " + subMap);

		} else {
			walker.showTrace(walker.parseTrace, "Wrong number of entries: " + subMap);
		}
	}

	@Override
	public void exitIs_null_clause(@NotNull SQLSelectParserParser.Is_null_clauseContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		int parentRuleIndex = ctx.getParent().getRuleIndex();

		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Integer parentStackLevel = walker.currentStackLevel(parentRuleIndex);

		Map<String, Object> subMap = walker.removeNodeMap(ruleIndex, stackLevel);

		if (subMap == null) {
			// unqualified select all has no map
			subMap = walker.makeRuleMap(ruleIndex);
		}
		subMap.remove("Type");
		if (ctx.getChildCount() == 2) {
			subMap.put(MUMBLE_OPERATOR_KEY, ctx.getChild(0).getText() + " " + ctx.getChild(1).getText());
		} else if (ctx.getChildCount() == 3) {
			subMap.put(MUMBLE_OPERATOR_KEY,
					ctx.getChild(0).getText() + " " + ctx.getChild(1).getText() + " " + ctx.getChild(2).getText());
		}
		// Add item to parent map
		walker.addToParent(parentRuleIndex, parentStackLevel, subMap);
	}

	@Override
	public void exitIs_clause(@NotNull SQLSelectParserParser.Is_clauseContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		int parentRuleIndex = ctx.getParent().getRuleIndex();

		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Integer parentStackLevel = walker.currentStackLevel(parentRuleIndex);

		Map<String, Object> subMap = walker.removeNodeMap(ruleIndex, stackLevel);

		// Make a new submap and fill it by reading the tokens directly
		subMap = new HashMap<String, Object>();

		if (ctx.getChildCount() == 2) {
			subMap.put(MUMBLE_OPERATOR_KEY, ctx.getChild(0).getText() + " " + ctx.getChild(1).getText());
		} else if (ctx.getChildCount() == 3) {
			subMap.put(MUMBLE_OPERATOR_KEY,
					ctx.getChild(0).getText() + " " + ctx.getChild(1).getText() + " " + ctx.getChild(2).getText());
		}
		// Add item to parent map
		walker.addToParent(parentRuleIndex, parentStackLevel, subMap);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void exitParen_clause(@NotNull SQLSelectParserParser.Paren_clauseContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Map<String, Object> subMap = walker.getNodeMap(ruleIndex, stackLevel);
		Object type = subMap.remove("Type");

		if (subMap.size() == 1) {
			Map<String, Object> item = (Map<String, Object>) subMap.remove("1");
			subMap.put(MUMBLE_PARENTHESES_KEY, item);
			walker.showTrace(walker.parseTrace, "Parenthesed Clause: " + subMap);

		} else {
			walker.showTrace(walker.parseTrace, "Wrong number of entries: " + subMap);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void exitComparison_predicate(@NotNull SQLSelectParserParser.Comparison_predicateContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Map<String, Object> subMap = walker.getNodeMap(ruleIndex, stackLevel);
		Object type = subMap.remove("Type");

		if (subMap.size() == 3) {
			walker.showTrace(walker.parseTrace, "Comparison: " + subMap);
			Map<String, Object> condition = new HashMap<String, Object>();

			Object operator = subMap.remove("2");
			if (operator instanceof String)
				condition.put(MUMBLE_OPERATOR_KEY, operator);
			else
				condition.put(MUMBLE_OPERATOR_KEY, ((HashMap<String, String>) operator).get("1"));

			Map<String, Object> left = walker.checkForSubstitutionVariable((Map<String, Object>) subMap.remove("1"),
					"predicand");
			condition.put(MUMBLE_LEFT_FACTOR_KEY, left);

			Map<String, Object> right = walker.checkForSubstitutionVariable((Map<String, Object>) subMap.remove("3"),
					"predicand");
			condition.put(MUMBLE_RIGHT_FACTOR_KEY, right);

			subMap.put(MUMBLE_CONDITION_KEY, condition);
			walker.showTrace(walker.parseTrace, "Comparison: " + subMap);

		} else {
			walker.showTrace(walker.parseTrace, "Wrong number of entries: " + subMap);
		}
	}

	@Override
	public void exitComparison_operator(@NotNull SQLSelectParserParser.Comparison_operatorContext ctx) {
		int ruleIndex = ctx.getRuleIndex();

		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Map<String, Object> subMap = walker.getNodeMap(ruleIndex, stackLevel);
		subMap.remove("Type");

		if (subMap.size() == 1) {
			walker.showTrace(walker.parseTrace, "Comparison Operator: " + subMap);
		} else if (subMap.size() == 2) {
			walker.showTrace(walker.parseTrace, "Comparison Operator: " + subMap);
			String notvar = (String) subMap.remove("1");
			String operator = (String) subMap.remove("2");
			subMap.put("1", notvar + '_' + operator);
		} else {
			walker.showTrace(walker.parseTrace, "Wrong number of entries: " + subMap);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void exitBetween_predicate(@NotNull SQLSelectParserParser.Between_predicateContext ctx) {
		// RULE_between_predicate
		int ruleIndex = ctx.getRuleIndex();
		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Map<String, Object> subMap = walker.getNodeMap(ruleIndex, stackLevel);
		Object type = subMap.remove("Type");

		if (subMap.size() >= 3) {
			walker.showTrace(walker.parseTrace, "Bewteen: " + subMap);
			Map<String, Object> condition = new HashMap<String, Object>();
			condition.put(MUMBLE_ITEM_KEY, subMap.remove("1"));
			String itemKey = MUMBLE_RANGE_BEGIN_KEY;

			Object operator = subMap.remove("2");
			if (operator instanceof String)
				if (operator.equals("not"))
					condition.put(MUMBLE_OPERATOR_KEY, "not between");
				else {
					condition.put(MUMBLE_SYMMETRY_KEY, operator);
					condition.put(MUMBLE_OPERATOR_KEY, MUMBLE_BETWEEN_KEY);
				}
			else {
				operator = walker.checkForSubstitutionVariable((Map<String, Object>) operator, "predicand");
				condition.put(itemKey, operator);
				itemKey = MUMBLE_RANGE_END_KEY;
				condition.put(MUMBLE_OPERATOR_KEY, MUMBLE_BETWEEN_KEY);
				condition.put(MUMBLE_SYMMETRY_KEY, null);
			}

			operator = subMap.remove("3");
			if (operator instanceof String)
				condition.put(MUMBLE_SYMMETRY_KEY, operator);
			else {
				if (!condition.containsKey(MUMBLE_SYMMETRY_KEY))
					condition.put(MUMBLE_SYMMETRY_KEY, null);
				operator = walker.checkForSubstitutionVariable((Map<String, Object>) operator, "predicand");
				condition.put(itemKey, operator);
				if (itemKey.equals(MUMBLE_RANGE_BEGIN_KEY))
					itemKey = MUMBLE_RANGE_END_KEY;
				else
					itemKey = "stop";
			}

			if (itemKey.equals(MUMBLE_RANGE_BEGIN_KEY)) {
				operator = subMap.remove("4");
				condition.put(itemKey, operator);
				operator = subMap.remove("5");
				condition.put(MUMBLE_RANGE_END_KEY, operator);
			} else if (itemKey.equals("end")) {
				operator = subMap.remove("4");
				condition.put(itemKey, operator);
			}

			subMap.put(MUMBLE_BETWEEN_KEY, condition);
			walker.showTrace(walker.parseTrace, "Comparison: " + subMap);

		} else {
			walker.showTrace(walker.parseTrace, "Wrong number of entries: " + subMap);
		}
	}

	@Override
	public void exitIn_predicate(@NotNull SQLSelectParserParser.In_predicateContext ctx) {
		int ruleIndex = ctx.getRuleIndex();

		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Map<String, Object> subMap = walker.removeNodeMap(ruleIndex, stackLevel);
		subMap.remove("Type");

		if (subMap.size() == 2) {
			walker.showTrace(walker.parseTrace, "In predicate: " + subMap);
			subMap.put(MUMBLE_ITEM_KEY, subMap.remove("1"));
			subMap.put(MUMBLE_IN_LIST_KEY, subMap.remove("2"));
		} else if (subMap.size() == 3) {
			walker.showTrace(walker.parseTrace, "In predicate: " + subMap);
			subMap.put(MUMBLE_ITEM_KEY, subMap.remove("1"));
			subMap.remove("2");
			subMap.put(MUMBLE_NOT_IN_LIST_KEY, subMap.remove("3"));
		} else {
			walker.showTrace(walker.parseTrace, "Wrong number of entries: " + subMap);
		}

		Map<String, Object> item = new HashMap<String, Object>();
		item.put(MUMBLE_IN_KEY, subMap);

		int parentRuleIndex = ctx.getParent().getRuleIndex();
		Integer parentStackLevel = walker.currentStackLevel(parentRuleIndex);
		walker.addToParent(parentRuleIndex, parentStackLevel, item);
	}

	@Override
	public void exitLike_any_predicate(@NotNull SQLSelectParserParser.Like_any_predicateContext ctx) {
		// Item 95 - add support for PostgresSQL escape character syntax in Like Any clauses
		int ruleIndex = ctx.getRuleIndex();

		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Map<String, Object> subMap = walker.removeNodeMap(ruleIndex, stackLevel);
		subMap.remove("Type");
		String type = new String();

		if (subMap.size() == 3) {
			// Matches the minimum mandatory entries of the rule:
			// row_value_predicand like_any_operator in_predicate_value
			walker.showTrace(walker.parseTrace, "In predicate: " + subMap);
			subMap.put(MUMBLE_ITEM_KEY, subMap.remove("1"));// This is the row_value_predicand
			type = (String) subMap.remove("2");// This is the like_any_operator
			subMap.put(MUMBLE_LIKE_ANY_LIST_KEY, subMap.remove("3")); // This is the in_predicate_value for the LIKE ANY
		} else if (subMap.size() == 4) {
			// Matches the minimum mandatory entries plus one of the optional items:
			// row_value_predicand not? like_any_operator in_predicate_value
			// row_value_predicand like_any_operator in_predicate_value escape_character_clause
			walker.showTrace(walker.parseTrace, "In predicate: " + subMap);

			// Has to determine which of the two possible constructions is presented and then build the AST entry
			Object clause = subMap.get("2"); // If this is the "not" operator, construct the NOT LIKE ANY, Else Construct the Escape Clause
			if (clause instanceof String && ((String) clause).equals("not")) {
				// This is the NOT LIKE ANY construction without escape clause
				subMap.put(MUMBLE_ITEM_KEY, subMap.remove("1"));// This is the row_value_predicand
				subMap.remove("2"); // This is the "not" operator, We don't need it in the AST since it's implied by the
				// MUMBLE_NOT_LIKE_ANY_LIST_KEY used to hold the list of values
				type = (String) subMap.remove("3");// The type is the like_any_operator
				subMap.put(MUMBLE_NOT_LIKE_ANY_LIST_KEY, subMap.remove("4"));// This is the in_predicate_value for the NOT LIKE ANY
			} else {
				// This is the LIKE ANY construction with an escape clause
				subMap.put(MUMBLE_ITEM_KEY, subMap.remove("1"));// This is the row_value_predicand
				type = (String) subMap.remove("2");// The type is the like_any_operator
				subMap.put(MUMBLE_NOT_LIKE_ANY_LIST_KEY, subMap.remove("3"));// This is the in_predicate_value for the NOT LIKE ANY
				subMap.putAll((Map<String, Object>) subMap.remove("4")); // This is the escape_character_clause, pulled up
			}
		} else if (subMap.size() == 5) {
			// Matches the maximum number of entries:
			// row_value_predicand not? like_any_operator in_predicate_value escape_character_clause
			walker.showTrace(walker.parseTrace, "In predicate: " + subMap);
			subMap.put(MUMBLE_ITEM_KEY, subMap.remove("1"));// This is the row_value_predicand
			subMap.remove("2"); // This is the "not" operator, We don't need it in the AST since it's implied by the
			// MUMBLE_NOT_LIKE_ANY_LIST_KEY used to hold the list of values
			type = (String) subMap.remove("3");// The type is the like_any_operator
			subMap.put(MUMBLE_NOT_LIKE_ANY_LIST_KEY, subMap.remove("4"));// This is the in_predicate_value for the NOT LIKE ANY
			subMap.putAll((Map<String, Object>) subMap.remove("5")); // This is the escape_character_clause, pulled up
		} else {
			walker.showTrace(walker.parseTrace, "Wrong number of entries: " + subMap);
		}

		Map<String, Object> item = new HashMap<String, Object>();
		if ("ilike".equals(type.toLowerCase()))
			item.put(MUMBLE_ILIKE_ANY_KEY, subMap);
		else
			item.put(MUMBLE_LIKE_ANY_KEY, subMap);

		int parentRuleIndex = ctx.getParent().getRuleIndex();
		Integer parentStackLevel = walker.currentStackLevel(parentRuleIndex);
		walker.addToParent(parentRuleIndex, parentStackLevel, item);
	}

	@Override
	public void exitLike_any_operator(@NotNull SQLSelectParserParser.Like_any_operatorContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		int parentRuleIndex = ctx.getParent().getRuleIndex();
		Map<String, Object> subMap = walker.getNodeMap(ruleIndex, stackLevel);

		String text = ctx.getChild(0).getText();
		subMap.remove("Type");
		subMap.put("Operator", text);
		
		walker.handleOneChild(ruleIndex);
	}

	@Override
	public void exitIn_predicate_value(@NotNull SQLSelectParserParser.In_predicate_valueContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		int parentRuleIndex = ctx.getParent().getRuleIndex();
		Map<String, Object> subMap = walker.getNodeMap(ruleIndex, stackLevel);
		Map<String, Object> reference = walker.checkForSubstitutionVariable((Map<String, Object>) subMap.get("1"), "in_list");

		walker.handleOneChild(ruleIndex);
	}

	@Override
	public void exitIn_value_list(@NotNull SQLSelectParserParser.In_value_listContext ctx) {
		int ruleIndex = ctx.getRuleIndex();

		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Map<String, Object> subMap = walker.removeNodeMap(ruleIndex, stackLevel);
		subMap.remove("Type");
		Map<String, Object> item = new HashMap<String, Object>();
		item.put(MUMBLE_LIST_KEY, subMap);

		int parentRuleIndex = ctx.getParent().getRuleIndex();
		Integer parentStackLevel = walker.currentStackLevel(parentRuleIndex);
		walker.addToParent(parentRuleIndex, parentStackLevel, item);
	}
	

	@Override
	public void exitEscape_character_clause(@NotNull SQLSelectParserParser.Escape_character_clauseContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		int parentRuleIndex = ctx.getParent().getRuleIndex();

		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Integer parentStackLevel = walker.currentStackLevel(parentRuleIndex);

		Map<String, Object> subMap = walker.removeNodeMap(ruleIndex, stackLevel);

		subMap = walker.makeRuleMap(ruleIndex);
		subMap.remove("Type");
		
		if (ctx.getChildCount() == 2) {
			walker.showTrace(walker.parseTrace, "TWO WORD lexer objects: " + ctx.getText());
			String escape_key_word = ctx.getChild(0).getText().toUpperCase();
			String escape_string = ctx.getChild(1).getText();
			subMap.put(MUMBLE_ESCAPE_KEY, escape_string);
		} else  {
			walker.showTrace(walker.parseTrace, "incorrect phrase");
		}
		// Add item to parent map
		walker.addToParent(parentRuleIndex, parentStackLevel, subMap);
		walker.showTrace(walker.parseTrace, "Static Data Type: " + subMap);
	}

	@Override
	public void exitFactor(@NotNull SQLSelectParserParser.FactorContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Map<String, Object> subMap = walker.getNodeMap(ruleIndex, stackLevel);

		if (subMap.size() == 3) {
			String sign = (String) subMap.remove("1");
			if (sign.equals("-")) {
				// multiply by -1
				// 1={left={literal=-1}, right={...}, operand=*}
				Map<String, Object> left = new HashMap<String, Object>();
				left.put(MUMBLE_LITERAL_KEY, "-1");
				Map<String, Object> item = new HashMap<String, Object>();
				item.put(MUMBLE_LEFT_FACTOR_KEY, left);
				item.put(MUMBLE_OPERATOR_KEY, "*");
				item.put(MUMBLE_RIGHT_FACTOR_KEY, subMap.remove("2"));
				Map<String, Object> calc = new HashMap<String, Object>();
				calc.put(MUMBLE_CALCULATION_KEY, item);
				subMap.put("1", calc);
			} else {
				subMap.put("1", subMap.remove("2"));
			}
		}
		walker.showTrace(walker.parseTrace, "Factor: " + subMap);
		walker.handleOneChild(ruleIndex);
	}
	
	@Override
	public void exitRow_value_predicand_list(@NotNull SQLSelectParserParser.Row_value_predicand_listContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		walker.handleOneChild(ruleIndex);
	}

	@Override
	public void exitNumeric_primary(@NotNull SQLSelectParserParser.Numeric_primaryContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		walker.handleOneChild(ruleIndex);
	}

	@Override
	public void exitCommon_value_expression(@NotNull SQLSelectParserParser.Common_value_expressionContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		walker.handleOneChild(ruleIndex);
	}

	@Override
	public void exitAdditive_expression(@NotNull SQLSelectParserParser.Additive_expressionContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Map<String, Object> subMap = walker.removeNodeMap(ruleIndex, stackLevel);
		subMap.remove("Type");

		if (subMap.size() == 1) {
			subMap.putAll((Map<String, Object>) subMap.remove("1"));
		} else if (subMap.size() >= 2) {
			Map<String, Object> item = new HashMap<String, Object>();
			int indx = 1;
			for (int x = 1; subMap.size() > 0; x = x + 2) {
				Map<String, Object> calc = new HashMap<String, Object>();
				if (x == 1)
					calc.put(MUMBLE_LEFT_FACTOR_KEY, subMap.remove("" + indx++));
				else {
					calc.put(MUMBLE_LEFT_FACTOR_KEY, item);
					item = new HashMap<String, Object>();
				}
				calc.put(MUMBLE_RIGHT_FACTOR_KEY, subMap.remove("" + indx++));
				calc.put(MUMBLE_OPERATOR_KEY, ctx.getChild(x).getText());
				item.put(MUMBLE_CALCULATION_KEY, calc);
			}

			subMap = item;
		} else {
			walker.showTrace(walker.parseTrace, "Too many entries: " + subMap);
		}
		walker.collect(ruleIndex, stackLevel, subMap);
	}

	@Override
	public void exitMultiplicative_expression(@NotNull SQLSelectParserParser.Multiplicative_expressionContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Map<String, Object> subMap = walker.removeNodeMap(ruleIndex, stackLevel);
		subMap.remove("Type");

		if (subMap.size() == 1) {
			subMap.putAll((Map<String, Object>) subMap.remove("1"));
		} else if (subMap.size() >= 2) {
			Map<String, Object> item = new HashMap<String, Object>();
			int indx = 1;
			for (int x = 1; subMap.size() > 0; x = x + 2) {
				Map<String, Object> calc = new HashMap<String, Object>();
				if (x == 1)
					calc.put(MUMBLE_LEFT_FACTOR_KEY, subMap.remove("" + indx++));
				else {
					calc.put(MUMBLE_LEFT_FACTOR_KEY, item);
					item = new HashMap<String, Object>();
				}
				calc.put(MUMBLE_RIGHT_FACTOR_KEY, subMap.remove("" + indx++));
				calc.put(MUMBLE_OPERATOR_KEY, ctx.getChild(x).getText());
				item.put(MUMBLE_CALCULATION_KEY, calc);
			}

			subMap = item;
		} else {
			walker.showTrace(walker.parseTrace, "Too many entries: " + subMap);
		}
		walker.collect(ruleIndex, stackLevel, subMap);
	}

	@Override
	public void exitBoolean_value_expression(@NotNull SQLSelectParserParser.Boolean_value_expressionContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		walker.handleOneChild(ruleIndex);
	}

	@Override
	public void exitBoolean_primary(@NotNull SQLSelectParserParser.Boolean_primaryContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		walker.handleOneChild(ruleIndex);
	}

	@Override
	public void exitPredicate(@NotNull SQLSelectParserParser.PredicateContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		walker.handleOneChild(ruleIndex);
	}

	@Override
	public void exitString_value_expression(@NotNull SQLSelectParserParser.String_value_expressionContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		walker.handleOperandList(ruleIndex, MUMBLE_CONCATENATE_KEY);
	}

	@Override
	public void exitCharacter_primary(@NotNull SQLSelectParserParser.Character_primaryContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		int parentRuleIndex = ctx.getParent().getRuleIndex();

		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Integer parentStackLevel = walker.currentStackLevel(parentRuleIndex);

		Map<String, Object> subMap = walker.removeNodeMap(ruleIndex, stackLevel);
		subMap.remove("Type");

		if (subMap.size() == 1) {
			walker.showTrace(walker.parseTrace, "Just One Item: " + subMap);
			walker.addToParent(parentRuleIndex, parentStackLevel, subMap.remove("1"));
		}
	}

	@Override
	public void exitTrim_function(@NotNull SQLSelectParserParser.Trim_functionContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Map<String, Object> subMap = walker.getNodeMap(ruleIndex, stackLevel);
		Object type = subMap.remove("Type");

		Map<String, Object> item = new HashMap<String, Object>();

		if (subMap.size() == 2) {
			item.put(MUMBLE_FUNCTION_NAME_KEY, subMap.remove("1"));
			item.put(MUMBLE_PARAMETERS_KEY, subMap.remove("2"));
			subMap.put(MUMBLE_FUNCTION_KEY, item);
		} else {
			walker.showTrace(walker.parseTrace, "Wrong number of entries: " + subMap);
		}
		walker.showTrace(walker.parseTrace, "TRIM Function: " + subMap);
	}

	@Override
	public void exitMysql_trim_operands(@NotNull SQLSelectParserParser.Mysql_trim_operandsContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		int parentRuleIndex = ctx.getParent().getRuleIndex();

		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Integer parentStackLevel = walker.currentStackLevel(parentRuleIndex);

		Map<String, Object> subMap = walker.removeNodeMap(ruleIndex, stackLevel);
		Object type = subMap.remove("Type");

		Map<String, Object> item = new HashMap<String, Object>();

		if (subMap.size() == 2) {
			item.put(MUMBLE_TRIM_CHARACTER_KEY, subMap.remove("1"));
			item.put(MUMBLE_VALUE_KEY, subMap.remove("2"));

		} else if (subMap.size() == 3) {
			item.put(MUMBLE_QUALIFIER_KEY, subMap.remove("1"));
			item.put(MUMBLE_TRIM_CHARACTER_KEY, subMap.remove("2"));
			item.put(MUMBLE_VALUE_KEY, subMap.remove("3"));
		}

		// Add item to parent map
		walker.showTrace(walker.parseTrace, "Trim Operands: " + item);
		walker.addToParent(parentRuleIndex, parentStackLevel, item);
	}

	@Override
	public void exitOther_trim_operands(@NotNull SQLSelectParserParser.Other_trim_operandsContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		int parentRuleIndex = ctx.getParent().getRuleIndex();

		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Integer parentStackLevel = walker.currentStackLevel(parentRuleIndex);

		Map<String, Object> subMap = walker.removeNodeMap(ruleIndex, stackLevel);
		Object type = subMap.remove("Type");

		Map<String, Object> item = new HashMap<String, Object>();

		if (subMap.size() == 2) {
			item.put(MUMBLE_TRIM_CHARACTER_KEY, subMap.remove("2"));
			item.put(MUMBLE_VALUE_KEY, subMap.remove("1"));
		}

		// Add item to parent map
		walker.showTrace(walker.parseTrace, "Trim Operands: " + item);
		walker.addToParent(parentRuleIndex, parentStackLevel, item);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void exitRoutine_invocation(@NotNull SQLSelectParserParser.Routine_invocationContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		int parentRuleIndex = ctx.getParent().getRuleIndex();

		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Integer parentStackLevel = walker.currentStackLevel(parentRuleIndex);

		Map<String, Object> subMap = walker.removeNodeMap(ruleIndex, stackLevel);
		Object type = subMap.remove("Type");

		Map<String, Object> item = new HashMap<String, Object>();

		if (subMap.size() == 2) {
			item.putAll((Map<? extends String, ? extends Object>) subMap.remove("1"));
			subMap = (Map<String, Object>) subMap.remove("2");
			type = subMap.remove("Type");
			item.put(MUMBLE_PARAMETERS_KEY, subMap.remove(type.toString()));
			subMap.put(MUMBLE_FUNCTION_KEY, item);
		} else {
			walker.showTrace(walker.parseTrace, "Wrong number of entries: " + subMap);
		}
		walker.showTrace(walker.parseTrace, "Function: " + subMap);
		walker.addToParent(parentRuleIndex, parentStackLevel, subMap);
	}

	@Override
	public void exitFunction_name(@NotNull SQLSelectParserParser.Function_nameContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Map<String, Object> subMap = walker.getNodeMap(ruleIndex, stackLevel);
		Object type = subMap.remove("Type");

		if (subMap.size() == 1) {
			walker.showTrace(walker.parseTrace, "Just One Identifier: " + subMap);
			String functName = (String) subMap.remove("1");
			subMap.put(MUMBLE_FUNCTION_NAME_KEY, functName);
			walker.showTrace(walker.parseTrace, "function_name: " + functName + " Map: " + subMap);
		} else if (subMap.size() == 2) {
			walker.showTrace(walker.parseTrace, "Two entries: " + subMap);
			String schema = (String) subMap.remove("1");
			subMap.put(MUMBLE_SCHEMA_KEY, schema);
			String functName = (String) subMap.remove("2");
			subMap.put(MUMBLE_FUNCTION_NAME_KEY, functName);
			walker.showTrace(walker.parseTrace, "Schema: " + schema + " function_name: " + functName + " Map: " + subMap);
		} else {
			walker.showTrace(walker.parseTrace, "Too many entries: " + subMap);
		}
	}

	@Override
	public void exitSql_argument_list(@NotNull SQLSelectParserParser.Sql_argument_listContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		int stackLevel = walker.currentStackLevel(ruleIndex);
		Map<String, Object> subMap = walker.getNodeMap(ruleIndex, stackLevel);
		walker.handlePushDown(ruleIndex);
	}

	@Override
	public void exitValue_expression(@NotNull SQLSelectParserParser.Value_expressionContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		int parentRuleIndex = ctx.getParent().getRuleIndex();
		if (parentRuleIndex == (Integer) SQLSelectParserParser.RULE_sql_argument_list) {
			Integer stackLevel = walker.currentStackLevel(ruleIndex);
			Map<String, Object> subMap = walker.getNodeMap(ruleIndex, stackLevel);
			subMap.remove("Type");

			Map<String, Object> valueExpression = null;
			String tableRef = null;
			String name = null;
			Boolean doNotSkip = true;

			if (subMap.size() == 1) {
				walker.showTrace(walker.parseTrace, "Just One Identifier: " + subMap);
				// Get first item, record if it is a Substitution Variable by
				// adding the Substitution List
				valueExpression = walker.checkForSubstitutionVariable((Map<String, Object>) subMap.remove("1"), "predicand");

				// Get Value Expression entry
				HashMap<String, Object> node = (HashMap<String, Object>) valueExpression.get(MUMBLE_COLUMN_KEY);
				if (node == null)
					node = (HashMap<String, Object>) valueExpression.get(MUMBLE_SUBSTITUTION_KEY);
				if (node != null) {
					if (node.containsKey(MUMBLE_TABLE_REF_KEY))
						// Value is associated with a table
						tableRef = (String) node.get(MUMBLE_TABLE_REF_KEY);
					if (node.containsKey(MUMBLE_NAME_KEY))
						// Value Expression is a column or substitution, use its
						// name
						name = (String) node.get(MUMBLE_NAME_KEY);
					else if (node.containsKey(MUMBLE_SUBSTITUTION_KEY))
						// then Value Expression is a COLUMN Substitution
						// Variable, get the variable's name
						name = (String) ((HashMap<String, Object>) node.get(MUMBLE_SUBSTITUTION_KEY)).get("name");
				}

			} else {
				walker.showTrace(walker.parseTrace, "Too many entries: " + subMap);
				doNotSkip = false;
			}
			if (doNotSkip) {
				// Capture  walker.symbolTable entry
				walker.collectSymbolTableItem(tableRef, valueExpression, ctx.getStart());
				// Add column to SQL AST Tree
				subMap.putAll(valueExpression);
			}
			walker.showTrace(walker.parseTrace, "Column Reference: " + subMap);
		} else if ((parentRuleIndex == (Integer) SQLSelectParserParser.RULE_search_condition)
				|| (parentRuleIndex == (Integer) SQLSelectParserParser.RULE_parenthesized_value_expression)
				|| (parentRuleIndex == (Integer) SQLSelectParserParser.RULE_condition_value)) {
			Integer stackLevel = walker.currentStackLevel(ruleIndex);
			Map<String, Object> subMap = walker.getNodeMap(ruleIndex, stackLevel);
			subMap = (Map<String, Object>) subMap.get("1");
			// Get first item, record if it is a Substitution Variable by
			// adding the Substitution List - This captures when the entire
			// condition is a Substitution Variable alone
			subMap = walker.checkForSubstitutionVariable((Map<String, Object>) subMap, MUMBLE_CONDITION_KEY);

			// NOW handle the child
			walker.handleOneChild(ruleIndex);
		} else if ((parentRuleIndex == (Integer) SQLSelectParserParser.RULE_case_expression)
				|| (parentRuleIndex == (Integer) SQLSelectParserParser.RULE_when_value_clause)
				|| (parentRuleIndex == (Integer) SQLSelectParserParser.RULE_case_result)) {
			Integer stackLevel = walker.currentStackLevel(ruleIndex);
			Map<String, Object> subMap = walker.getNodeMap(ruleIndex, stackLevel);
			subMap = (Map<String, Object>) subMap.get("1");
			// Get first item, record if it is a Substitution Variable by
			// adding the Substitution List - This captures when the entire
			// condition is a Substitution Variable alone
			subMap = walker.checkForSubstitutionVariable((Map<String, Object>) subMap, "predicand");

			// NOW handle the child
			walker.handleOneChild(ruleIndex);
		} else if ((parentRuleIndex == (Integer) SQLSelectParserParser.RULE_aggregate_function)
				|| (parentRuleIndex == (Integer) SQLSelectParserParser.RULE_trim_operands)) {
			// Trim and Aggregate Function Parameter
			Integer stackLevel = walker.currentStackLevel(ruleIndex);
			Map<String, Object> subMap = walker.getNodeMap(ruleIndex, stackLevel);
			subMap = (Map<String, Object>) subMap.get("1");
			// Get first item, record if it is a Substitution Variable by
			// adding the Substitution List - This captures when the entire
			// condition is a Substitution Variable alone
			subMap = walker.checkForSubstitutionVariable((Map<String, Object>) subMap, "predicand");

			// NOW handle the child
			walker.handleOneChild(ruleIndex);
			//
		} else {
			// then parent is any non-list parent
			walker.handleOneChild(ruleIndex);
		}

	}

	@Override
	public void exitRow_value_expression(@NotNull SQLSelectParserParser.Row_value_expressionContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		int parentRuleIndex = ctx.getParent().getRuleIndex();
		if (parentRuleIndex == (Integer) SQLSelectParserParser.RULE_in_value_list) {
			walker.handleListItem(ruleIndex, parentRuleIndex);
		} else {
			// then parent is probably Rule_value_expression and this should
			// just be one child
			walker.handleOneChild(ruleIndex);
		}
	}

	@Override
	public void exitSort_specifier_list(@NotNull SQLSelectParserParser.Sort_specifier_listContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		int parentRuleIndex = ctx.getParent().getRuleIndex();
		walker.handleListList(ruleIndex, parentRuleIndex);
	}

	@Override
	public void exitSort_specifier(@NotNull SQLSelectParserParser.Sort_specifierContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Map<String, Object> subMap = walker.getNodeMap(ruleIndex, stackLevel);
		Object type = subMap.remove("Type");
		HashMap<String, Object> item = new HashMap<String, Object>();

		if (subMap.size() >= 1) {
			item.put(MUMBLE_PREDICAND_KEY, subMap.remove("1"));
			Object item2 = null;
			Object item3 = null;
					
			if (subMap.size() >= 1) {
				item2 = subMap.remove("2");
			}
			if (subMap.size() >= 1) {
				item3 = subMap.remove("3");
			}
			
			if (item2 == null) {
				item.put(MUMBLE_SORT_ORDER_KEY, "ASC");
				item.put(MUMBLE_NULL_ORDER_KEY, null);
				walker.showTrace(walker.parseTrace, "One Entry: " + item);
			} else if (item3 != null) {
				item.put(MUMBLE_SORT_ORDER_KEY, item2);
				Map<String, Object> hold = (Map<String, Object>) item3;
				type = hold.remove("Type").toString();
				item.put(MUMBLE_NULL_ORDER_KEY, ((HashMap<String, Object>) hold.get(type)).get("1"));
				walker.showTrace(walker.parseTrace, "Three entries: " + item);

			} else { // Item 2 is not null and Item 3 is null :- Item 2 could be ASC/DESC or Nulls command
				if (item2 instanceof Map<?,?>) {
					// item2 is the null order value
					item.put(MUMBLE_SORT_ORDER_KEY, "ASC");
					Map<String, Object> hold = (Map<String, Object>) item2;
					type = hold.remove("Type").toString();
					item.put(MUMBLE_NULL_ORDER_KEY, ((HashMap<String, Object>) hold.get(type)).get("1"));
					walker.showTrace(walker.parseTrace, "Two entries: " + item);
				} else {
					//item2 is the ASC/DESC value
					item.put(MUMBLE_SORT_ORDER_KEY, item2);
					item.put(MUMBLE_NULL_ORDER_KEY, null);
					walker.showTrace(walker.parseTrace, "Two entries: " + item);
				}
			}
		}
		else {
			walker.showTrace(walker.parseTrace, "Too many entries: " + subMap);
		}

		subMap.put("1", item);
		walker.showTrace(walker.parseTrace, "Sort Item: " + subMap);

		int parentRuleIndex = ctx.getParent().getRuleIndex();
		walker.handleListItem(ruleIndex, parentRuleIndex);

	}

	@Override
	public void exitNull_ordering(@NotNull SQLSelectParserParser.Null_orderingContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		walker.handlePushDown(ruleIndex);
	}

	@Override
	public void exitGrouping_element_list(@NotNull SQLSelectParserParser.Grouping_element_listContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		int parentRuleIndex = ctx.getParent().getRuleIndex();
		walker.handleListList(ruleIndex, parentRuleIndex);
	}

	@Override
	public void exitGrouping_element(@NotNull SQLSelectParserParser.Grouping_elementContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		int parentRuleIndex = ctx.getParent().getRuleIndex();
		walker.handleListItem(ruleIndex, parentRuleIndex);
	}

	@Override
	public void exitOrdinary_grouping_set_list(@NotNull SQLSelectParserParser.Ordinary_grouping_set_listContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		walker.handleOneChild(ruleIndex);
	}

	@Override
	public void exitOrdinary_grouping_set(@NotNull SQLSelectParserParser.Ordinary_grouping_setContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		int parentRuleIndex = ctx.getParent().getRuleIndex();
		if (parentRuleIndex == SQLSelectParserParser.RULE_grouping_element)
			walker.handleOneChild(ruleIndex);
		else if (parentRuleIndex == SQLSelectParserParser.RULE_ordinary_grouping_set_list)
			walker.handleListItem(ruleIndex, parentRuleIndex);
	}

	@Override
	public void exitRow_value_predicand(@NotNull SQLSelectParserParser.Row_value_predicandContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		int parentRuleIndex = ctx.getParent().getRuleIndex();
		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Map<String, Object> subMap = walker.getNodeMap(ruleIndex, stackLevel);
		// Get first item, record if it is a Substitution Variable by
		// adding the Substitution List
		Map<String, Object> substitutionPredicand = walker.checkForSubstitutionVariable((Map<String, Object>) subMap.get("1"),
				"predicand");

		walker.handleOneChild(ruleIndex);
	}

	@Override
	public void exitGeneral_literal(@NotNull SQLSelectParserParser.General_literalContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		walker.handleOneChild(ruleIndex);
	}

	@Override
	public void exitUnsigned_literal(@NotNull SQLSelectParserParser.Unsigned_literalContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Map<String, Object> subMap = walker.getNodeMap(ruleIndex, stackLevel);
		subMap.remove("Type");
		String[] keys = new String[1];
		keys = subMap.keySet().toArray(keys);

		if (keys.length == 1) {
			walker.showTrace(walker.parseTrace, "Just One Entry: " + subMap);
			Object item = subMap.remove(keys[0]);
			subMap.put(MUMBLE_LITERAL_KEY, item);
		} else {
			walker.showTrace(walker.parseTrace, "Too many entries: " + subMap);
		}
		walker.showTrace(walker.parseTrace, "Unsigned Literal: " + subMap);
	}

	@Override
	public void exitReal_number(@NotNull SQLSelectParserParser.Real_numberContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		walker.handleOneChild(ruleIndex);
	}

	@Override
	public void exitReal_number_def(@NotNull SQLSelectParserParser.Real_number_defContext ctx) {
		// Tell master exit that the full text is the value
		walker.useAsLeaf = true;
	}

	@Override
	public void exitExponent(@NotNull SQLSelectParserParser.ExponentContext ctx) {
		// Tell master exit that the full text is the value
		walker.useAsLeaf = true;
	}

	@Override
	public void exitDatetime_literal(@NotNull SQLSelectParserParser.Datetime_literalContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		walker.handleOneChild(ruleIndex);
	}

	@Override
	public void exitIdentifier(@NotNull SQLSelectParserParser.IdentifierContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		walker.handleOneChild(ruleIndex);
	}

	@Override
	public void exitAlias_identifier(@NotNull SQLSelectParserParser.Alias_identifierContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		walker.handleOneChild(ruleIndex);
	}

	@Override
	public void exitVariable_identifier(@NotNull SQLSelectParserParser.Variable_identifierContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		walker.handleOneChild(ruleIndex);
	}

	@Override
	public void exitNull_literal(@NotNull SQLSelectParserParser.Null_literalContext ctx) {
		HashMap<String, Object> item = new HashMap<String, Object>();
		item.put(MUMBLE_NULL_LITERAL_KEY, "null");

		int parentRuleIndex = ctx.getParent().getRuleIndex();
		Integer parentStackLevel = walker.currentStackLevel(parentRuleIndex);

		walker.addToParent(parentRuleIndex, parentStackLevel, item);
	}

	@Override
	public void exitPuml_constant_identifier(@NotNull SQLSelectParserParser.Puml_constant_identifierContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		int parentRuleIndex = ctx.getParent().getRuleIndex();

		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Integer parentStackLevel = walker.currentStackLevel(parentRuleIndex);

		Map<String, Object> subMap = walker.removeNodeMap(ruleIndex, stackLevel);

		subMap = walker.makeRuleMap(ruleIndex);
		subMap.remove("Type");
		
		if (ctx.getChildCount() == 1) {
			walker.showTrace(walker.parseTrace, "one word PUML Constant: " + ctx.getText());
			String part = ctx.getChild(0).getText().toUpperCase();
			subMap.put(MUMBLE_PUML_CONSTANT_KEY, part);
		}
		// Add item to parent map
		walker.addToParent(parentRuleIndex, parentStackLevel, subMap);

		walker.showTrace(walker.parseTrace, "PUML CONSTANT IDENTIFIER: " + subMap);
	}

}
