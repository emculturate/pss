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

import java.util.ArrayList;

import static mumble.MumbleConstants.*;
import static mumble.ASTWalkerHelperConstants.*;
import static mumble.SQLParserEndPoints.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;

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

	public HashMap<String, Object> getTableColumnDictionaryMap() {
		return walker.getWalkerTableDictionary();
	}

	public HashMap<String, Object> getQueryColumnDictionaryMap() {
		return walker.queryColumnDictionaryMap;
	}

	public HashMap<String, Object> getSymbolTable() {
		return walker.symbolTable;
	}

	@SuppressWarnings("unchecked")
	public HashSet<String> getInterface() {
		HashSet<String> interfac = new HashSet<String>();
		if (walker.symbolTable == null || walker.symbolTable.isEmpty()) {
			return interfac;
		}

		Map<String, Object> queryMap = null;
		int topQueryIndex = -1;
		for (String key : walker.symbolTable.keySet()) {
			if (!key.startsWith("query")) {
				continue;
			}
			Object queryObject = walker.symbolTable.get(key);
			if (!(queryObject instanceof HashMap<?, ?>)) {
				continue;
			}
			String suffix = key.substring("query".length());
			int queryIndex;
			try {
				queryIndex = Integer.parseInt(suffix);
			} catch (NumberFormatException ex) {
				continue;
			}
			if (queryIndex > topQueryIndex) {
				topQueryIndex = queryIndex;
				queryMap = (Map<String, Object>) queryObject;
			}
		}

		if (queryMap == null) {
			return interfac;
		}

		Object interfaceObject = queryMap.get(MUMBLE_INTERFACE_KEY);
		if (!(interfaceObject instanceof Map<?, ?> interfaceMap)) {
			return interfac;
		}

		for (Object keyObj : interfaceMap.keySet()) {
			if (keyObj instanceof String key) {
				interfac.add(key);
			}
		}
		return interfac;
	}

	/**
	 * Emit a Snippet object with all of the parts of the SQL Parse Event Walker results related to the query
	*
	 * @return
	 */
	public Snippet getSnippet() {
		Snippet snippet = new Snippet(walker.asTree, walker.getWalkerTableDictionary(), walker.queryColumnDictionaryMap,
				walker.symbolTable, walker.substitutionsMap, getInterface());
		// Handoff: copy walker-generated (non-parser) diagnostics into the snippet.
		snippet.setParserDiagnosticList(new ArrayList<>(walker.getWalkerDiagnostics()));
		return snippet;
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
				+ SQLSelectParserParser.ruleNames[ruleIndex] + ": " +  walker.asTree);
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

		Object skip =  walker.asTree.remove("SKIP");
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
				walker.showTrace(walker.parseTrace,  walker.asTree);
			}
		}

		walker.popStack(ruleIndex);

		walker.showTrace(walker.parseTrace, "EXIT " + walker.makeMapIndex(ruleIndex, stackLevel) + ": "
				+ SQLSelectParserParser.ruleNames[ruleIndex] + ": " +  walker.asTree);
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
		Object type = subMap.remove(ASTWALKER_RULE_TYPE_KEY);
		 walker.asTree.put(SQLPARSER_SQL_TREE_KEY, subMap.remove("1"));
		finalizeTopLevelUnresolvedColumns();
		// walker.showTrace(resultTrace, collector);
		walker.showTrace(walker.symbolTrace,  walker.symbolTable);
		walker.showTrace(walker.symbolTrace,  walker.peekCurrentTableDictionary());
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
		Object type = subMap.remove(ASTWALKER_RULE_TYPE_KEY);
		 walker.asTree.put(SQLPARSER_COLUMN_TREE_KEY, subMap.remove("1"));
		// walker.showTrace(resultTrace, collector);
		walker.showTrace(walker.symbolTrace,  walker.symbolTable);

		walker.addQueryInputColumnsToTableDictionary();

		walker.showTrace(walker.symbolTrace,  walker.peekCurrentTableDictionary());
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
		Object type = subMap.remove(ASTWALKER_RULE_TYPE_KEY);
		 walker.asTree.put(SQLPARSER_PREDICAND_TREE_KEY, subMap.remove("1"));
		// walker.showTrace(resultTrace, collector);
		walker.showTrace(walker.symbolTrace,  walker.symbolTable);

		walker.addQueryInputColumnsToTableDictionary();

		walker.showTrace(walker.symbolTrace,  walker.peekCurrentTableDictionary());
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
		Object type = subMap.remove(ASTWALKER_RULE_TYPE_KEY);
		 walker.asTree.put(SQLPARSER_IN_LIST_TREE_KEY, subMap.remove("1"));
		// walker.showTrace(resultTrace, collector);
		walker.showTrace(walker.symbolTrace,  walker.symbolTable);

		walker.addQueryInputColumnsToTableDictionary();

		walker.showTrace(walker.symbolTrace,  walker.peekCurrentTableDictionary());
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
		Object type = subMap.remove(ASTWALKER_RULE_TYPE_KEY);
		 walker.asTree.put(SQLPARSER_CONDITION_TREE_KEY, subMap.remove("1"));
		// walker.showTrace(resultTrace, collector);
		walker.showTrace(walker.symbolTrace,  walker.symbolTable);

		walker.addQueryInputColumnsToTableDictionary();

		walker.showTrace(walker.symbolTrace,  walker.peekCurrentTableDictionary());
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
		Object type = subMap.remove(ASTWALKER_RULE_TYPE_KEY);
		HashMap<String, Object> item = new HashMap<String, Object> ();
		if (subMap.size() == 1) {
			// normal TUPLE value
			item.putAll((HashMap<String, Object>) subMap.remove("1"));
			
		} else {	
			walker.showTrace(walker.parseTrace, "Wrong number of entries: " + subMap);
		}

		 walker.asTree.put(SQLPARSER_TUPLE_TREE_KEY, item);
		
		walker.showTrace(walker.symbolTrace,  walker.symbolTable);
		walker.showTrace(walker.symbolTrace,  walker.peekCurrentTableDictionary());
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
		Object type = subMap.remove(ASTWALKER_RULE_TYPE_KEY);
		 walker.asTree.put(SQLPARSER_QUERY_TREE_KEY, subMap.remove("1"));
		finalizeTopLevelUnresolvedColumns();
		// walker.showTrace(resultTrace, collector);
		walker.showTrace(walker.symbolTrace,  walker.symbolTable);

		// Add TABLE references to Table Dictionary
		walker.addQueryInputColumnsToTableDictionary();

		walker.showTrace(walker.symbolTrace,  walker.peekCurrentTableDictionary());
	}

	@SuppressWarnings("unchecked")
	private void finalizeTopLevelUnresolvedColumns() {
		Object unresolvedObject = walker.symbolTable.remove(MUMBLE_UNRESOLVED_COLUMN_KEY);
		if (!(unresolvedObject instanceof HashMap<?, ?> unresolvedMapObject)) {
			return;
		}

		HashMap<String, Object> unresolvedMap = unresolvedObject instanceof HashMap<?, ?> 
											  ? (HashMap<String, Object>) unresolvedMapObject : null;
		if (unresolvedMap == null || unresolvedMap.isEmpty()) {
			return;
		}

		Integer[] firstTokenLocation = walker.getFirstEntryLineAndCharacter(unresolvedMap);
		String unknownColumnsWithLocations = walker.formatColumnEntriesWithLocations(unresolvedMap);
		String unknownColumnsCsv = walker.formatEntryKeysAsCsv(unresolvedMap);
		String diagCode = walker.getDiagnosticCode(SqlASTWalkerHelper.DIAG_SQL_UNRESOLVED_COLUMNS);
		String diagTemplate = walker.getDiagnosticMessage(SqlASTWalkerHelper.DIAG_SQL_UNRESOLVED_COLUMNS);
		String diagMessage =  String.format(diagTemplate, unknownColumnsWithLocations);

		walker.addWalkerFatal(
				diagCode,
				diagMessage,
				firstTokenLocation[0],
				firstTokenLocation[1],
				unknownColumnsCsv);
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
		Object type = subMap.remove(ASTWALKER_RULE_TYPE_KEY);
		 walker.asTree.put(SQLPARSER_JOIN_EXTENSION_TREE_KEY, subMap.remove("1"));

		 // Cleanup symbol table and table dictionary for join extension before returning to Snippet requester
		walker.symbolTable.remove(MUMBLE_QUERY_DICTIONARY_KEY);
		walker.symbolTable.remove(MUMBLE_SCALAR_SUBQUERY_ALIASES_KEY);
		walker.symbolTable.remove(MUMBLE_INTERFACE_KEY);
		
		walker.showTrace(walker.symbolTrace,  walker.symbolTable);
		walker.showTrace(walker.symbolTrace,  walker.peekCurrentTableDictionary());
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
		Object type = subMap.remove(ASTWALKER_RULE_TYPE_KEY);
		 walker.asTree.put(SQLPARSER_VALUES_TREE_KEY, subMap.remove("1"));
		// walker.showTrace(resultTrace, collector);
		walker.showTrace(walker.symbolTrace,  walker.symbolTable);
		walker.showTrace(walker.symbolTrace,  walker.peekCurrentTableDictionary());
	}
	/*
	===============================================================================
	  Literal Value Start Symbol
	===============================================================================
	*/

		/*
	===============================================================================
	  Insert Start Symbol
	===============================================================================
	*/
	@Override
	public void exitInsert_end_point(SQLSelectParserParser.Insert_end_pointContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Map<String, Object> subMap = walker.removeNodeMap(ruleIndex, stackLevel);
		Object type = subMap.remove(ASTWALKER_RULE_TYPE_KEY);
		 walker.asTree.put(SQLPARSER_INSERT_TREE_KEY, subMap.remove("1"));
		// walker.showTrace(resultTrace, collector);
		walker.showTrace(walker.symbolTrace,  walker.symbolTable);
		walker.showTrace(walker.symbolTrace,  walker.peekCurrentTableDictionary());
	}
	/*

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
		Object type = subMap.remove(ASTWALKER_RULE_TYPE_KEY);
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
		Object type = subMap.remove(ASTWALKER_RULE_TYPE_KEY);

		Map<String, Object> newMap = walker.collectNewRuleMap(ruleIndex, stackLevel);
		type = newMap.remove(ASTWALKER_RULE_TYPE_KEY);

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
		Object type = subMap.remove(ASTWALKER_RULE_TYPE_KEY);
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
		int parentRuleIndex = ctx.getParent().getRuleIndex();

		Map<String, Object> subMap = walker.getNodeMap(ruleIndex, stackLevel);
		walker.checkForSubstitutionVariable((Map<String, Object>) subMap.get("1"), "query");

		walker.handleOneChild(ruleIndex);
		walker.showTrace(walker.parseTrace, "INSERT EXPRESSION: " + subMap);
	}

	@Override
	public void exitSnowflake_insert(@NotNull SQLSelectParserParser.Snowflake_insertContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Map<String, Object> subMap = walker.removeNodeMap(ruleIndex, stackLevel);
		subMap.remove(ASTWALKER_RULE_TYPE_KEY);
		String type = new String();

		if (subMap.size() == 3) {
			// Matches the minimum mandatory entries of the rule:
			subMap.put(MUMBLE_INSERT_PREAMBLE_KEY, (String) subMap.remove("1"));	// this is the insert preamble
			subMap.put(MUMBLE_TABLE_KEY,(Map<String, Object>)subMap.remove("2"));  // This is the table primary
            Map<String, Object> item = (Map<String, Object>) subMap.remove("3");
			subMap.putAll(item); // This is the insert_source
			walker.showTrace(walker.parseTrace, "Snowflake insert: " + subMap);
		} else if (subMap.size() == 4) {
			// Matches the minimum mandatory entries of the rule:
			subMap.put(MUMBLE_INSERT_PREAMBLE_KEY, (String) subMap.remove("1"));	// this is the insert preamble
			subMap.put(MUMBLE_TABLE_KEY,(Map<String, Object>)subMap.remove("2"));  // This is the table primary
			subMap.put(MUMBLE_COLUMNS_KEY,(Map<String, Object>)subMap.remove("3")); // This is the column reference list
            Map<String, Object> item = (Map<String, Object>) subMap.remove("4");
			subMap.putAll(item); // This is the insert_source
			walker.showTrace(walker.parseTrace, "Snowflake insert: " + subMap);
		} else {
			walker.showTrace(walker.parseTrace, "Wrong number of entries: " + subMap);
		}
		// Add the type to the subMap

		int parentRuleIndex = ctx.getParent().getRuleIndex();
		Integer parentStackLevel = walker.currentStackLevel(parentRuleIndex);
		walker.addToParent(parentRuleIndex, parentStackLevel, subMap);
		
	}
	  
	
	@Override
	public void exitInsert_preamble(@NotNull SQLSelectParserParser.Insert_preambleContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		int parentRuleIndex = ctx.getParent().getRuleIndex();
		Map<String, Object> subMap = walker.getNodeMap(ruleIndex, stackLevel);

		if (ctx.getChildCount() <= 2) {
			// just the insert into statement
			subMap.remove(ASTWALKER_RULE_TYPE_KEY);
			subMap.put(MUMBLE_INSERT_PREAMBLE_KEY, MUMBLE_INSERT_INTO_KEY);
		} else if (ctx.getChildCount() == 3) {
			// Insert with override
			subMap.remove(ASTWALKER_RULE_TYPE_KEY);
			subMap.put(MUMBLE_INSERT_PREAMBLE_KEY, MUMBLE_INSERT_INTO_OVERWRITE_KEY);
			walker.showTrace(walker.parseTrace, "Insert Preamble: " + subMap);
		} else {
			walker.showTrace(walker.parseTrace, "Wrong number of entries: " + ctx.getText());
		}

		walker.handleOneChild(ruleIndex);
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
		Object type = subMap.remove(ASTWALKER_RULE_TYPE_KEY);

		keys = subMap.keySet().toArray(keys);

		for (String key : keys) {
			Object obj = subMap.remove(key);
			if (obj instanceof String) {

			} else {
				HashMap<String, Object> value = (HashMap<String, Object>) obj;
				Integer childKey = (Integer) (value).remove(ASTWALKER_RULE_TYPE_KEY);
				if (childKey == null) {
					String k2 = "update" + walker.queryCount;
					walker.queryCount++;
					subMap.put(k2, value);
				} else {
					Object segment = value.remove(childKey.toString());
					if (childKey.equals((Integer) SQLSelectParserParser.RULE_assignment_expression_list)) {
						subMap.put(MUMBLE_ASSIGNMENTS_KEY, segment);
					} else if (childKey.equals((Integer) SQLSelectParserParser.RULE_from_clause)) {
						if (((HashMap<String, Object>) segment).size() == 1) {
							subMap.put(MUMBLE_FROM_KEY, ((HashMap<String, Object>) segment).remove("1"));
						} else
							subMap.put(MUMBLE_FROM_KEY, segment);
					} else if (childKey.equals((Integer) SQLSelectParserParser.RULE_where_clause)) {
						HashMap<String, Object> item = (HashMap<String, Object>) segment;
						item = (HashMap<String, Object>) item.remove("1");
						subMap.put(MUMBLE_WHERE_KEY, item);
					} else if (childKey.equals((Integer) SQLSelectParserParser.RULE_returning)) {
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
		HashMap<String, Object> unks = (HashMap<String, Object>) symbols.remove(MUMBLE_UNRESOLVED_COLUMN_KEY);

		Integer count = 0;
		Integer tableCount = 0;
		String onlyTableName = null;
		HashMap<String, Object> hold = new HashMap<String, Object>();
		String holdTabRef = null;

		for (String tab_ref : symbols.keySet()) {
			if ((tab_ref.equals(MUMBLE_INTERFACE_KEY)) || (tab_ref.startsWith("def_query")) || (tab_ref.startsWith("def_union"))
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
				// just one source referenced
				// If the single source is a real table, assign unknowns to it.
				// If it is a subquery/union/intersect/values (e.g., "query0"), do NOT force unknowns into it;
				// leave them in the unknown bucket for post-parse validation.
				if (holdTabRef != null && (
						holdTabRef.startsWith("query")
						|| holdTabRef.startsWith(MUMBLE_UNION_KEY)
						|| holdTabRef.startsWith(MUMBLE_INTERSECT_KEY)
						|| holdTabRef.startsWith(MUMBLE_VALUES_KEY))) {
					symbols.put(MUMBLE_UNRESOLVED_COLUMN_KEY, unks);
				} else {
					((HashMap<String, Object>) hold.get(holdTabRef)).putAll(unks);
				}
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
						symbols.put(MUMBLE_UNRESOLVED_COLUMN_KEY, unks);
				}
			}
		}
		// TODO: Add TABLE references to Table Dictionary
		HashMap<String, Object> currentTableDictionary = walker.getCurrentTableDictionary();
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
					HashMap<String, Object> currItem = (HashMap<String, Object>)  currentTableDictionary.get(reference);
					if (currItem != null)
						currItem.putAll((Map<? extends String, ? extends Object>) hold.get(tab_ref));
					else {
						HashMap<String, Object> newItem = new HashMap<String, Object>();
						newItem.putAll((Map<? extends String, ? extends Object>) hold.get(tab_ref));
						 currentTableDictionary.put(reference, newItem);
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
		Integer parentRuleIndex = (Integer) ctx.getParent().getRuleIndex();
		if (parentRuleIndex.equals((Integer) SQLSelectParserParser.RULE_partition_by_clause)) {
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
		Object type = subMap.remove(ASTWALKER_RULE_TYPE_KEY);

		if (subMap.size() == 2) {
			walker.showTrace(walker.parseTrace, "Comparison: " + subMap);
			Map<String, Object> left = (Map<String, Object>) subMap.remove("1");
			subMap.put(MUMBLE_SET_KEY, left);

			Map<String, Object> right = (Map<String, Object>) subMap.remove("2");
			subMap.put(MUMBLE_TO_KEY, right);

			walker.showTrace(walker.parseTrace, "Assignment: " + subMap);

			// Put target column symbol into update table's set and interface
			Map<String, Object> unk = (HashMap<String, Object>)  walker.symbolTable.get(MUMBLE_UNRESOLVED_COLUMN_KEY);
			String column = ((HashMap<String, String>) ((HashMap<String, Object>) left).get(MUMBLE_COLUMN_KEY)).get("name");

			String[] keys = new String[1];
			keys =  walker.symbolTable.keySet().toArray(keys);

			for (String key : keys) {
				if (key.equals(MUMBLE_UNRESOLVED_COLUMN_KEY)) { // do nothing

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
			Object type = subMap.remove(ASTWALKER_RULE_TYPE_KEY);
			Map<String, Object> item = new HashMap<String, Object>();
			if (subMap.size() == 1) {
				item.putAll((Map<String, Object>) subMap.remove("1"));
			} else {
				walker.showTrace(walker.parseTrace, "Wrong number of entries: " + ctx.getText());
			}
			walker.addToParent(parentRuleIndex, parentStackLevel, item);
			walker.showTrace(walker.parseTrace, "Case: " + item);
			
			// Finish Symbol Table Construction
			HashMap<String, Object> symbols =  walker.symbolTable;
			String key = MUMBLE_VALUES_KEY + walker.queryCount;

			// Capture Query Column Dictionary for this level
			HashMap<String, Object> localCurrentQueryDictionary = (HashMap<String, Object>) walker.symbolTable.remove(MUMBLE_QUERY_DICTIONARY_KEY);
			if (localCurrentQueryDictionary == null)
				localCurrentQueryDictionary = new HashMap<String, Object>();
			walker.queryColumnDictionaryMap.put(key, localCurrentQueryDictionary);
			symbols.put(MUMBLE_QUERY_DICTIONARY_KEY, localCurrentQueryDictionary);
			walker.symbolTable.remove(MUMBLE_SCALAR_SUBQUERY_ALIASES_KEY);

			// Pop the symbol table for this level and add it to the parent level with a unique key
			walker.popSymbolTable(key, symbols);

			walker.queryCount++;
	
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
			Object type = subMap.remove(ASTWALKER_RULE_TYPE_KEY);
			if (subMap.size() == 3) {
				// Variation 3: Contains an alias and a list of column name assignments
				
				Map<String, Object> item = (Map<String, Object>) subMap.remove("1");
				Map<String, Object> vals = (Map<String, Object>) item.get(MUMBLE_VALUES_KEY);
				Map<String, Object> aliasMap = (Map<String, Object>) subMap.remove("2");
				vals.putAll(aliasMap);
				vals.put(MUMBLE_COLUMNS_KEY,  subMap.remove("3"));

				subMap.putAll(item);

				// Replace the interface entry in the symbol table from the values statement
				Map<String, Object> hold = (Map<String, Object>) walker.symbolTable.get(MUMBLE_VALUES_KEY);
				walker.symbolTable.put(MUMBLE_INTERFACE_KEY, hold);
				
				// Resolve Symbol Table, add alias from Values statement to the Symbol Table.
				walker.symbolTable.put((String) aliasMap.get(MUMBLE_ALIAS_KEY), MUMBLE_VALUES_KEY);
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
			Object type = subMap.remove(ASTWALKER_RULE_TYPE_KEY);
			if (subMap.size() == 2) {
				// Variation 2: Contains an alias
				Map<String, Object> item = (Map<String, Object>) subMap.remove("1");
				Map<String, Object> vals = (Map<String, Object>) item.get(MUMBLE_VALUES_KEY);
				Map<String, Object> aliasMap = (Map<String, Object>) subMap.remove("2");
				vals.putAll(aliasMap);
				
				subMap.putAll(item);

				// Resolve Symbol Table, add alias from Values statement to the Symbol Table.
				walker.symbolTable.put((String) aliasMap.get(MUMBLE_ALIAS_KEY), MUMBLE_VALUES_KEY);
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
			Object type = subMap.remove(ASTWALKER_RULE_TYPE_KEY);
			if (subMap.size() == 1) {
				// Variation 1: Just a matrix of rows
				subMap.putAll((Map<String, Object>) subMap.remove("1"));
			} else {
				walker.showTrace(walker.parseTrace, "Wrong number of entries: " + ctx.getText());
			}
			// Duplicate the Values entry in the symbol table to construct the interface entry
			Map<String, Object> hold = new HashMap<String, Object>();
			hold.putAll((Map<String, Object>) walker.symbolTable.get(MUMBLE_VALUES_KEY));
			walker.symbolTable.put(MUMBLE_INTERFACE_KEY, hold);

			// Add the matrix to the SQL Tree
			Map<String, Object> item = new HashMap<String, Object>();
			item.put(MUMBLE_VALUES_KEY, subMap);
			walker.addToParent(parentRuleIndex, parentStackLevel, item);
			walker.showTrace(walker.parseTrace, "Case: " + item);

		}
		
		@Override
		public void exitValues_matrix(@NotNull SQLSelectParserParser.Values_matrixContext ctx) {
			int ruleIndex = ctx.getRuleIndex();

			Integer stackLevel = walker.currentStackLevel(ruleIndex);
			Map<String, Object> subMap = walker.removeNodeMap(ruleIndex, stackLevel);
			subMap.remove(ASTWALKER_RULE_TYPE_KEY);
			Map<String, Object> item = new HashMap<String, Object>();
			item.put(MUMBLE_MATRIX_KEY, subMap);
						
			int parentRuleIndex = ctx.getParent().getRuleIndex();
			Integer parentStackLevel = walker.currentStackLevel(parentRuleIndex);
			walker.addToParent(parentRuleIndex, parentStackLevel, item);
		}


		@Override
		public void exitValues_row(@NotNull SQLSelectParserParser.Values_rowContext ctx) {
			int ruleIndex = ctx.getRuleIndex();

			Integer stackLevel = walker.currentStackLevel(ruleIndex);
			Map<String, Object> subMap = walker.removeNodeMap(ruleIndex, stackLevel);
			subMap.remove(ASTWALKER_RULE_TYPE_KEY);
			Map<String, Object> item = (Map<String, Object>) subMap.remove("1");
			Map<String, Object> row = (Map<String, Object>) item.remove("list");
			item.put(MUMBLE_ROW_KEY, row);

			// Construct virtual column references from the first row in the values matrix 
			// Put these in the symbol table dictionary for the default "values" table
			for (int i = 1; i <= row.size(); i++) {
				String ref = "$" + i;
				walker.collectUnresolvedColumnReference(MUMBLE_VALUES_KEY, ref, ctx.getStart());
			}

			int parentRuleIndex = ctx.getParent().getRuleIndex();
			Integer parentStackLevel = walker.currentStackLevel(parentRuleIndex);
			walker.addToParent(parentRuleIndex, parentStackLevel, item);
		}

		@Override
		public void exitValues_aliases(@NotNull SQLSelectParserParser.Values_aliasesContext ctx) {
			int ruleIndex = ctx.getRuleIndex();

			Integer stackLevel = walker.currentStackLevel(ruleIndex);
			Map<String, Object> subMap = walker.removeNodeMap(ruleIndex, stackLevel);
			subMap.remove(ASTWALKER_RULE_TYPE_KEY);

			// Update the Values Symbol Table column names
			Map<String, Object> symbols = (Map<String, Object>) walker.symbolTable.get("values");
			for (int i = 1; i <= subMap.size(); i++) {
				// Get the ith alias name from the SQL Tree alias list 
				String name = (String) subMap.remove("" + i);
				// Get the symbol table entry for $i, remove it and reinsert it with the new name
				String ref = "$" + i;
				Object token =  symbols.remove(ref);
				symbols.put(name, token);
				// Construct a column entry for the SQL Tree

				HashMap<String, Object> item = new HashMap<String, Object>();
				item.put(MUMBLE_NAME_KEY, name);
				item.put(MUMBLE_TABLE_REF_KEY, null);
				HashMap<String, Object> hold = new HashMap<String, Object>();
				hold.put(MUMBLE_COLUMN_KEY, item);
				subMap.put("" + i, hold);
				
			}
			// Put the alias list in the SQL Query Tree
			int parentRuleIndex = ctx.getParent().getRuleIndex();
			Integer parentStackLevel = walker.currentStackLevel(parentRuleIndex);
			walker.addToParent(parentRuleIndex, parentStackLevel, subMap);
		}
	
		
		@Override
		public void exitValues_aliases_list(@NotNull SQLSelectParserParser.Values_aliases_listContext ctx) {
			int ruleIndex = ctx.getRuleIndex();
			int parentRuleIndex = ctx.getParent().getRuleIndex();
			walker.handleListList(ruleIndex, parentRuleIndex);

			// Loop through the ctx children and add the token strings to the Query Column Dictionary for the default "values" table
			for (int i = 0; i < ctx.getChildCount(); i++) {
				if (i % 2 == 0) {
					Object child = ctx.getChild(i);
					String name = ctx.getChild(i).getText();
					Token childToken = null;
					if (child instanceof TerminalNode) {
						childToken = ((TerminalNode) child).getSymbol();
					} else if (child instanceof ParserRuleContext) {
						childToken = ((ParserRuleContext) child).getStart();
					}
					String tokenString = childToken != null ? childToken.toString() : ctx.getChild(i).toString();
				
					// Add item alias into the Current Query Column Dictionary
					addAliasTokensObject(name, tokenString);
				}
			}
		}
						
		@SuppressWarnings("unchecked")
		@Override
		public void exitInsert_values_statement(@NotNull SQLSelectParserParser.Insert_values_statementContext ctx) {
			int ruleIndex = ctx.getRuleIndex();
			int parentRuleIndex = ctx.getParent().getRuleIndex();

			Integer stackLevel = walker.currentStackLevel(ruleIndex);
			Integer parentStackLevel = walker.currentStackLevel(parentRuleIndex);

			Map<String, Object> subMap = walker.removeNodeMap(ruleIndex, stackLevel);
			Object type = subMap.remove(ASTWALKER_RULE_TYPE_KEY);
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
		Object type = subMap.remove(ASTWALKER_RULE_TYPE_KEY);

		if (ctx.getChildCount() == 1) {
			Map<String, Object> item = new HashMap<String, Object>();
			item.put(MUMBLE_OPERATOR_KEY, subMap.remove("1"));
			item.put(MUMBLE_QUALIFIER_KEY, null);
			subMap.put(MUMBLE_INTERSECT_KEY, item);
		} else if (ctx.getChildCount() == 2) {
			Map<String, Object> item = new HashMap<String, Object>();
			item.put(MUMBLE_OPERATOR_KEY, subMap.remove("1"));

			Map<String, Object> hold = (Map<String, Object>) subMap.remove("2");
			if (hold.containsKey(ASTWALKER_RULE_TYPE_KEY)) {
				Integer childKey = (Integer) hold.remove(ASTWALKER_RULE_TYPE_KEY);
				if (childKey.equals((Integer) SQLSelectParserParser.RULE_set_qualifier))
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
		Object type = subMap.remove(ASTWALKER_RULE_TYPE_KEY);

		if (ctx.getChildCount() == 1) {
			Map<String, Object> item = new HashMap<String, Object>();
			item.put(MUMBLE_OPERATOR_KEY, subMap.remove("1"));
			item.put(MUMBLE_QUALIFIER_KEY, null);
			subMap.put(MUMBLE_UNION_KEY, item);
		} else if (ctx.getChildCount() == 2) {
			Map<String, Object> item = new HashMap<String, Object>();
			item.put(MUMBLE_OPERATOR_KEY, subMap.remove("1"));

			Map<String, Object> hold = (Map<String, Object>) subMap.remove("2");
			if (hold.containsKey(ASTWALKER_RULE_TYPE_KEY)) {
				Integer childKey = (Integer) hold.remove(ASTWALKER_RULE_TYPE_KEY);
				if (childKey.equals((Integer) SQLSelectParserParser.RULE_set_qualifier))
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

		Integer parentRuleIndex = (Integer) ctx.getParent().getRuleIndex();
		if (parentRuleIndex.equals((Integer) SQLSelectParserParser.RULE_nonparenthesized_value_expression_primary)) {
			// Subquery is acting as a lookup function
			Integer stackLevel = walker.currentStackLevel(ruleIndex);
			Map<String, Object> subMap = walker.getNodeMap(ruleIndex, stackLevel);
			Object type = subMap.remove(ASTWALKER_RULE_TYPE_KEY);
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
		Object type = subMap.remove(ASTWALKER_RULE_TYPE_KEY);

		keys = subMap.keySet().toArray(keys);

		// Since there are too many combinations of clauses, we will
		// have to cycle through them one at a time and figure out what kind
		// of clause they are.  The keys will be the rule type, so we
		// can use that to determine what kind of clause it is.
		// Then we construct the final AST map from the availble parts of the query.
		for (String key : keys) {
			Object obj = subMap.remove(key);
			if (obj instanceof String) {

			} else {
				HashMap<String, Object> value = (HashMap<String, Object>) obj;
				Integer childKey = (Integer) (value).remove(ASTWALKER_RULE_TYPE_KEY);
				Object segment = value.remove(childKey.toString());
				if (childKey.equals((Integer) SQLSelectParserParser.RULE_select_list)) {
					subMap.put(MUMBLE_SELECT_KEY, segment);
				} else if (childKey.equals((Integer) SQLSelectParserParser.RULE_set_qualifier)) {
					subMap.putAll(value);
				} else if (childKey.equals((Integer) SQLSelectParserParser.RULE_from_clause)) {
					if (((HashMap<String, Object>) segment).size() == 1) {
						subMap.put(MUMBLE_FROM_KEY, ((HashMap<String, Object>) segment).remove("1"));
					} else
						subMap.put(MUMBLE_FROM_KEY, segment);
				} else if (childKey.equals((Integer) SQLSelectParserParser.RULE_where_clause)) {
					HashMap<String, Object> item = (HashMap<String, Object>) segment;
					item = (HashMap<String, Object>) item.remove("1");
					subMap.put(MUMBLE_WHERE_KEY, item);
				} else if (childKey.equals((Integer) SQLSelectParserParser.RULE_groupby_clause)) {
					subMap.put(MUMBLE_GROUPBY_KEY, segment);
				} else if (childKey.equals((Integer) SQLSelectParserParser.RULE_having_clause)) {
					HashMap<String, Object> item = (HashMap<String, Object>) segment;
					item = (HashMap<String, Object>) item.remove("1");
					subMap.put(MUMBLE_HAVING_KEY, item);
				} else if (childKey.equals((Integer) SQLSelectParserParser.RULE_orderby_clause)) {
					subMap.put(MUMBLE_ORDERBY_KEY, segment);
				} else if (childKey.equals((Integer) SQLSelectParserParser.RULE_limit_clause)) {
					subMap.put(MUMBLE_LIMIT_KEY, segment);
				} else {
					walker.showTrace(walker.parseTrace, "Too Many Entries" + segment);
				}
			}
		}
		walker.showTrace(walker.parseTrace, subMap);

		Integer symbolScopeLevel = walker.stackSymbols.get("symbolTable");
		boolean hasParentQueryScope = symbolScopeLevel != null && symbolScopeLevel > 2;
		boolean emitFinalUnresolvedUnknownFatal = !hasParentQueryScope;
		// Handle symbol tables		
		HashMap<String, Object> symbols = convertSymbolTableToTableDictionary(emitFinalUnresolvedUnknownFatal);

		// Retrieve outer symbol table, insert this symbol table into it
		String key = "query" + walker.queryCount;

		// Capture Query Column Dictionary for this level
		HashMap<String, Object> localCurrentQueryDictionary = (HashMap<String, Object>) walker.symbolTable.remove(MUMBLE_QUERY_DICTIONARY_KEY);
		if (localCurrentQueryDictionary == null)
			localCurrentQueryDictionary = new HashMap<String, Object>();
		walker.queryColumnDictionaryMap.put(key, localCurrentQueryDictionary);
		symbols.put(MUMBLE_QUERY_DICTIONARY_KEY, localCurrentQueryDictionary);
		walker.symbolTable.remove(MUMBLE_SCALAR_SUBQUERY_ALIASES_KEY);

		// Get the remaining unresolved column references from this query and push them up one level with the query key as a prefix
		HashMap<String, Object> unresolvedMap = (HashMap<String, Object>) walker.symbolTable.remove(MUMBLE_UNRESOLVED_COLUMN_KEY);
		if (unresolvedMap != null && !unresolvedMap.isEmpty()) {
			symbols.put(MUMBLE_UNRESOLVED_COLUMN_KEY, unresolvedMap);
		}
	
		walker.popSymbolTable(key, symbols);
		walker.queryCount++;
		if (!hasParentQueryScope) {
			walker.symbolTable.remove(MUMBLE_UNRESOLVED_COLUMN_KEY);
		}

		if (hasParentQueryScope && unresolvedMap != null && !unresolvedMap.isEmpty()) {
			Object parentUnresolvedObject = walker.symbolTable.get(MUMBLE_UNRESOLVED_COLUMN_KEY);
			if (parentUnresolvedObject instanceof HashMap<?, ?>) {
				walker.mergeUnknownEntries((HashMap<String, Object>) parentUnresolvedObject, unresolvedMap);
			} else {
				walker.symbolTable.put(MUMBLE_UNRESOLVED_COLUMN_KEY, unresolvedMap);
			}
		}

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
		subMap.put(ASTWALKER_RULE_TYPE_KEY, SQLSelectParserParser.RULE_set_qualifier);
		
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
		int ruleIndex = ctx.getRuleIndex();
		int parentRuleIndex = ctx.getParent().getRuleIndex();

		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Integer parentStackLevel = walker.currentStackLevel(parentRuleIndex);

		Map<String, Object> subMap = walker.removeNodeMap(ruleIndex, stackLevel);
		subMap.remove(ASTWALKER_RULE_TYPE_KEY);
		Map<String, Object> item;

		// variables for constructing the Symbol Table Interface
		String interfaceAlias = null;
		HashMap<String, Object> interfaceReference = new HashMap<String, Object>();
		String aliasToken = null;

		// Get first item, record if it is a Substitution Variable by adding the
		// Substitution List
		item = walker.checkForSubstitutionVariable((Map<String, Object>) subMap.remove("1"), "predicand");

		// make a copy of the item AST subtree without the alias for use in the symbol table interface
		interfaceReference.putAll(item);

		// derive an alias for the item if it does not have one, and add the alias to the item if it does not have one
		if (subMap.size() == 0) {
			// Select Item did not have an Alias, construct one from options
			aliasToken = ctx.getStop().toString();
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
			// Select Item has an alias, extract it and add it back into the item map for use in the SQL Tree and Symbol Table construction
			walker.showTrace(walker.parseTrace, "Item and Alias: " + item);
			aliasToken = ctx.getStop().toString();	

			Map<String, Object> aliasMap = (Map<String, Object>) subMap.remove("2");
			interfaceAlias = (String) aliasMap.get(MUMBLE_ALIAS_KEY);

			if (item.containsKey(MUMBLE_SELECT_KEY)) {
			// then this item is a subquery, so we need to push it down under a LOOKUP subtree in the AST
				Map<String, Object> lookup = new HashMap<String, Object>();
				lookup.putAll(item);
				item = new HashMap<String, Object>();
				item.put(MUMBLE_LOOKUP_KEY, lookup);
			 	walker.showTrace(walker.parseTrace, "Select Item is a Lookup Subquery: " + item);
			}
			// Add the alias back into the item map for use in the SQL Tree and Symbol Table construction
			((Map<String, Object>) item).putAll(aliasMap);
		}
		walker.addToParent(parentRuleIndex, parentStackLevel, item);
		walker.showTrace(walker.parseTrace, "SELECT ITEM: " + item);

		// Add item to symbol table
		HashMap<String, Object> selectInterface = (HashMap<String, Object>)  walker.symbolTable.get(MUMBLE_INTERFACE_KEY);
		if (selectInterface == null) {
			selectInterface = new HashMap<String, Object>();
			 walker.symbolTable.put(MUMBLE_INTERFACE_KEY, selectInterface);
		}

		// Simplify interface reference map by standardizing it into a flat map of column references and not the entire AST subtree
		ArrayList<Object> columnList = new ArrayList<Object>();
		flattenSubTreeForInterfaceColumns(interfaceReference, columnList);

		selectInterface.put(interfaceAlias, columnList);
		if (interfaceReference.containsKey(MUMBLE_SELECT_KEY) || interfaceReference.containsKey(MUMBLE_LOOKUP_KEY)) {
			addCurrentQueryScalarSubqueryAlias(interfaceAlias);
		}

		// Add item alias into the Current Query Column Dictionary
		addAliasTokensObject(interfaceAlias, aliasToken);
		
	}

	private void addAliasTokensObject(String interfaceAlias, String aliasToken) {
		String queryDictionaryKey = MUMBLE_QUERY_DICTIONARY_KEY;
		HashMap<String, Object> queryColumnDictionary = (HashMap<String, Object>) walker.symbolTable.get(queryDictionaryKey);
		if (queryColumnDictionary == null) {
			queryColumnDictionary = new HashMap<String, Object>();
			walker.symbolTable.put(queryDictionaryKey, queryColumnDictionary);
		}

		Object aliasTokensObject = queryColumnDictionary.get(interfaceAlias);
		if (aliasTokensObject == null) {
			aliasTokensObject = new ArrayList<String>();
			queryColumnDictionary.put(interfaceAlias, aliasTokensObject);
		} 
		
		((ArrayList<String>) aliasTokensObject).add(aliasToken);
	}

	private void addCurrentQueryScalarSubqueryAlias(String interfaceAlias) {
		if (interfaceAlias == null) {
			return;
		}

		Object scalarAliasObject = walker.symbolTable.get(MUMBLE_SCALAR_SUBQUERY_ALIASES_KEY);
		HashSet<String> scalarAliases;
		if (scalarAliasObject instanceof HashSet<?>) {
			scalarAliases = (HashSet<String>) scalarAliasObject;
		} else {
			scalarAliases = new HashSet<String>();
			walker.symbolTable.put(MUMBLE_SCALAR_SUBQUERY_ALIASES_KEY, scalarAliases);
		}

		scalarAliases.add(interfaceAlias);
	}

	// Standardize the interface reference map into a flat map of column references and not the entire AST subtree
	// This is a recursive function that traverses the item subtree until it finds column references or substitution variables, 
	// which it adds to the column list with the alias as the key

	private void flattenSubTreeForInterfaceColumns(HashMap<String, Object> subTree, ArrayList<Object> columnList) {
		if (subTree.containsKey(MUMBLE_COLUMN_KEY)) {
			Object col = subTree.get(MUMBLE_COLUMN_KEY);
			if (!columnList.contains(col)) {
				columnList.add(col);
			}
		} else if (subTree.containsKey(MUMBLE_SUBSTITUTION_KEY)) {
			Object subst = subTree.get(MUMBLE_SUBSTITUTION_KEY);
			if (subst instanceof HashMap) {
				HashMap<String, Object> substMap = (HashMap<String, Object>) subst;
				Object type = substMap.get("type");
				if (type != null && (MUMBLE_COLUMN_KEY.equals(type) || MUMBLE_PREDICAND_KEY.equals(type))) {
					if (!columnList.contains(subst)) {
						columnList.add(subst);
					}
				}
			}
		} else {
			for (Object key : subTree.keySet()) {
				Object value = subTree.get(key);
				if (value instanceof HashMap) {
					flattenSubTreeForInterfaceColumns((HashMap<String, Object>) value, columnList);
				}
			}
		}
	}

	@Override
	public void exitAs_clause(@NotNull SQLSelectParserParser.As_clauseContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Map<String, Object> subMap = walker.getNodeMap(ruleIndex, stackLevel);
		Object type = subMap.remove(ASTWALKER_RULE_TYPE_KEY);

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
		subMap.remove(ASTWALKER_RULE_TYPE_KEY);
		if (ctx.getChildCount() == 1) {
			walker.showTrace(walker.parseTrace, "Just One Identifier: " + ctx.getText());
			item.put(MUMBLE_TABLE_REF_KEY, "*");
			item.put(MUMBLE_NAME_KEY, "*");

			walker.collectUnresolvedColumnReference(MUMBLE_UNKNOWN_KEY, "*", ctx.getStart());

			subMap.put(MUMBLE_COLUMN_KEY, item);
		} else if (ctx.getChildCount() == 3) {
			walker.showTrace(walker.parseTrace, "Three entries: " + ctx.getText());
			item.put(MUMBLE_TABLE_REF_KEY, ctx.getChild(0).getText());

			walker.collectUnresolvedColumnReference(item.get(MUMBLE_TABLE_REF_KEY), "*", ctx.getStart());

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
		Object type = subMap.remove(ASTWALKER_RULE_TYPE_KEY);

		walker.addToParent(parentRuleIndex, parentStackLevel, subMap);
		walker.showTrace(walker.parseTrace, "Join Extension: " + subMap);

		convertSymbolTableToTableDictionary(false);
	}

	/**
	 * Create Dictionary from Symbol Table
	 * Validate and assign all columns to a specific source table or query
	 * Perform quality diagnostics for any unresolved columns, and if emitFinalUnresolvedUnknownFatal is true,
	 * then add fatal diagnostics to parser resultfor any remaining unresolved columns after this process
	 * 
	 * @return
	 */
	private HashMap<String, Object> convertSymbolTableToTableDictionary(boolean emitFinalUnresolvedUnknownFatal) {
	
		// deconstruct current symbol table into components for analysis
 		HashMap<String, Object> localInterface = (HashMap<String, Object>) walker.symbolTable.remove(MUMBLE_INTERFACE_KEY);
		HashMap<String, Object> localUnresolvedColumnMap = (HashMap<String, Object>) walker.symbolTable.remove(MUMBLE_UNRESOLVED_COLUMN_KEY);
		if (localUnresolvedColumnMap == null)
			localUnresolvedColumnMap = new HashMap<String, Object>();
		HashMap<String, Object> localTableAliasMap = (HashMap<String, Object>) walker.symbolTable.remove(MUMBLE_TABLE_ALIAS_KEY);
		if (localTableAliasMap == null)
			localTableAliasMap = new HashMap<String, Object>();
		HashMap<String, Object> localTableCollection = (HashMap<String, Object>) walker.symbolTable.remove(MUMBLE_TABLE_DICTIONARY_KEY);
		if (localTableCollection == null)
			localTableCollection = new HashMap<String, Object>();
		HashMap<String, Object> localCurrentQueryDictionary = (HashMap<String, Object>) walker.symbolTable.remove(MUMBLE_QUERY_DICTIONARY_KEY);
		if (localCurrentQueryDictionary == null)
			localCurrentQueryDictionary = new HashMap<String, Object>();

		// Leave these null if they don't exist
		HashSet<String> localScalarSubqueryAliases = (HashSet<String>) walker.symbolTable.remove(MUMBLE_SCALAR_SUBQUERY_ALIASES_KEY);
        Object  filtersList = walker.symbolTable.remove(MUMBLE_FILTERS_KEY);


		// Add query/derived-source aliases into the alias collection for downstream source resolution.
		walker.mergeNonTableAliasMappingsIntoAliasCollection(localCurrentQueryDictionary, localTableAliasMap);

		// Resolve alias-backed table refs so tableCollection keys align with canonical table references.
		walker.reconcileAliasBackedTableReferences(localTableCollection, localTableAliasMap);

		// Expand wildcard unknown entries (for example *, alias.*) into concrete source-scoped unknowns.
		walker.processWildcardUnknownEntries(
				localUnresolvedColumnMap,
				localInterface,
				localTableAliasMap,
				localTableCollection,
				localCurrentQueryDictionary);

		walker.moveUnknownEntriesToSingleWildcardBackedNonTableSource(
				localUnresolvedColumnMap,
				localCurrentQueryDictionary,
				localTableAliasMap);

		HashMap<String, Object> currentTableDictionary = walker.getCurrentTableDictionary();

		if (!localUnresolvedColumnMap.isEmpty()) {
			// Check for explicitly qualified columns whose table qualifiers do not exist
			HashMap<String, Object> explicitQualifiedUnknownEntries = extractExplicitQualifiedUnknownEntries(
					localUnresolvedColumnMap,
					localInterface,
					filtersList);
			emitExplicitQualifiedUnknownDiagnostics(
					explicitQualifiedUnknownEntries,
					localInterface,
					filtersList,
					localTableAliasMap,
					localCurrentQueryDictionary,
					localCurrentQueryDictionary);

			// Attempt to resolve any remaining unqualified unknowns that only have one possible source table, and move them to the resolved table dictionary for downstream resolution.  This allows us to resolve queries with unqualified columns that only have one possible source.
			boolean movedToSingleTableTarget = 
				walker.moveEntriesToSingleTableIfSingleTarget(localUnresolvedColumnMap, localTableCollection);
			if (!movedToSingleTableTarget && !localUnresolvedColumnMap.isEmpty()) {
				walker.moveEntriesToSingleTableIfSingleTarget(localUnresolvedColumnMap, currentTableDictionary);
			}
		}

		// Merge table collection into the table dictionary map, which is used for symbol resolution in the rest of the query processing.
		//   This allows all table references to be resolved against the same dictionary regardless of where they are defined in the query.
		if (localTableCollection != null && localTableCollection.size() > 0) {
			for (String tab_ref : localTableCollection.keySet()) {
				String reference;
				if (tab_ref.startsWith("<"))
					// Tuple Substitution Variable, do NOT alter case
					reference = tab_ref;
				else
					reference = tab_ref.toLowerCase();
				HashMap<String, Object> currDict = (HashMap<String, Object>)  currentTableDictionary.get(reference);
				if (currDict != null)
					currDict.putAll((Map<? extends String, ? extends Object>) localTableCollection.get(tab_ref));
				else {
					HashMap<String, Object> newDict = new HashMap<String, Object>();
					newDict.putAll((Map<? extends String, ? extends Object>) localTableCollection.get(tab_ref));
					 currentTableDictionary.put(reference, newDict);
				}
			}
		}
	
		// Validation that follows checks that the query's output interface can be constructed from the available columns
		// in the query context, which includes the FROM clause tables and any query-level filters.  If the interface 
		// cannot be constructed, then we have an invalid query and any unexplained logic will add fatal errors to the diagnostics 
		// list that will help the user understand why their query is invalid.
		if (localInterface != null) {
		for (String outputCol: localInterface.keySet()) {
			if (localScalarSubqueryAliases != null && localScalarSubqueryAliases.contains(outputCol)) {
				continue;
			}
			boolean hasSpecificResolutionFatalForOutputColumn = false;
			Object refsObj = localInterface.get(outputCol);
			if (refsObj instanceof ArrayList<?> refs) {
				for (Object refObj : refs) {
					String columnName = walker.extractReferenceNameFromInterfaceEntry(refObj);
					String tableRef = walker.extractReferenceTableRefFromInterfaceEntry(refObj);
					String substitutionType = walker.extractSubstitutionTypeFromInterfaceEntry(refObj);

					if (substitutionType != null
							&& (MUMBLE_COLUMN_KEY.equals(substitutionType) || MUMBLE_PREDICAND_KEY.equals(substitutionType))) {
						continue;
					}

					if (columnName == null || "*".equals(columnName)) {
						continue;
					}

					if (tableRef != null) {
						String resolvedTableRef = walker.resolveAliasToTableName(tableRef, localTableAliasMap);
						String resolvedNonTableSourceRef = walker.resolveAliasToNonTableSourceQueryKey(tableRef, localCurrentQueryDictionary);
						boolean explicitQueryReference = resolvedNonTableSourceRef != null
								|| (resolvedTableRef != null && resolvedTableRef.startsWith("query"));
						boolean resolvedInSource = false;

						if (explicitQueryReference) {
							String queryDictionaryKey = (resolvedNonTableSourceRef != null)
									? resolvedNonTableSourceRef
									: resolvedTableRef;
							Object queryDictionaryObj = walker.queryColumnDictionaryMap.get(queryDictionaryKey);
							if (queryDictionaryObj instanceof Map<?, ?> queryDictionary) {
								resolvedInSource = queryDictionary.containsKey(columnName)
										|| queryDictionary.containsKey("*");
							}
							if (!resolvedInSource && queryDictionaryKey != null
									&& (queryDictionaryKey.startsWith(MUMBLE_QUERY_KEY)
											|| queryDictionaryKey.startsWith(MUMBLE_UNION_KEY)
											|| queryDictionaryKey.startsWith(MUMBLE_INTERSECT_KEY)
											|| queryDictionaryKey.startsWith(MUMBLE_VALUES_KEY)
											|| MUMBLE_VALUES_KEY.equals(queryDictionaryKey))) {
								// Query-backed source dictionary may be finalized after this validation step.
								resolvedInSource = true;
							}
						} else {
							HashMap<String, Object> indicatedTableDictionary = walker.getTableDictionaryForReference(
									resolvedTableRef,
									localTableCollection);
							resolvedInSource = indicatedTableDictionary != null
									&& indicatedTableDictionary.containsKey(columnName);
						}

						if (!resolvedInSource) {
							Integer[] refLocation = (localCurrentQueryDictionary == null)
									? new Integer[] { null, null }
									: walker.getLineAndCharacterFromEntry(localCurrentQueryDictionary.get(outputCol));
							if (refLocation[0] == null || refLocation[1] == null) {
								refLocation = walker.getFirstEntryLineAndCharacter(localCurrentQueryDictionary);
							}

							String diagCode = walker.getDiagnosticCode(SqlASTWalkerHelper.DIAG_SQL_QUALIFIED_COLUMN_NOT_FOUND_IN_TABLE);
							String diagTemplate = walker.getDiagnosticMessage(SqlASTWalkerHelper.DIAG_SQL_QUALIFIED_COLUMN_NOT_FOUND_IN_TABLE);
							String indicatedSourceRef = (resolvedNonTableSourceRef != null)
									? resolvedNonTableSourceRef
									: resolvedTableRef;
							String diagMessage =  String.format(diagTemplate,
											columnName,
											refLocation[0],
											refLocation[1],
											indicatedSourceRef);

							walker.addWalkerFatal(
									diagCode,
									diagMessage,
									refLocation[0],
									refLocation[1],
									columnName);
							hasSpecificResolutionFatalForOutputColumn = true;
						}
					} else {
						Integer[] refLocation = (localCurrentQueryDictionary == null)
								? new Integer[] { null, null }
								: walker.getLineAndCharacterFromEntry(localCurrentQueryDictionary.get(outputCol));
						if (refLocation[0] == null || refLocation[1] == null) {
							refLocation = walker.getFirstEntryLineAndCharacter(localCurrentQueryDictionary);
						}

						ArrayList<String> sourceRefs = walker.collectSourceReferencesForColumn(
								columnName,
								localTableCollection,
								localCurrentQueryDictionary);

						if (sourceRefs.isEmpty()) {
							// Scenario: implicit reference has no candidate source.
							continue;
							// String diagCode = walker.getDiagnosticCode(
							// 		SqlASTWalkerHelper.DIAG_SQL_UNKNOWN_IMPLICIT_COLUMN_REFERENCE);
							// String diagTemplate = walker.getDiagnosticMessage(
							// 		SqlASTWalkerHelper.DIAG_SQL_UNKNOWN_IMPLICIT_COLUMN_REFERENCE);
							// String diagMessage = (diagTemplate == null)
							// 		? String.format(
							// 				"Unknown column reference '%s' at (l:%s c:%s). No matching column found in any table/query dictionary in scope.",
							// 				columnName,
							// 				refLocation[0],
							// 				refLocation[1])
							// 		: String.format(diagTemplate,
							// 				columnName,
							// 				refLocation[0],
							// 				refLocation[1]);

							// walker.addWalkerFatal(
							// 		diagCode,
							// 		diagMessage,
							// 		refLocation[0],
							// 		refLocation[1],
							// 		columnName);
							// hasSpecificResolutionFatalForOutputColumn = true;
						} else if (sourceRefs.size() > 1) {
							// Scenario: implicit reference matches multiple candidate sources.
							String possibleSources = sourceRefs.toString();
							String diagCode = walker.getDiagnosticCode(
									SqlASTWalkerHelper.DIAG_SQL_AMBIGUOUS_COLUMN_REFERENCE);
							String diagTemplate = walker.getDiagnosticMessage(
									SqlASTWalkerHelper.DIAG_SQL_AMBIGUOUS_COLUMN_REFERENCE);
							String diagMessage = (diagTemplate == null)
									? String.format(
											"Ambiguous column reference '%s' at (l:%s c:%s). Possible sources: %s",
											columnName,
											refLocation[0],
											refLocation[1],
											possibleSources)
									: String.format(diagTemplate,
											columnName,
											refLocation[0],
											refLocation[1],
											possibleSources);

							walker.addWalkerFatal(
									diagCode,
									diagMessage,
									refLocation[0],
									refLocation[1],
									columnName);
							hasSpecificResolutionFatalForOutputColumn = true;
						} else {
							// resolved by exactly one source
						}
					}
				}
			}
			// if (!hasSpecificResolutionFatalForOutputColumn
			// 		&& !walker.validateInterfaceColumn(outputCol, localInterface, localCurrentQueryDictionary,
			// 	localTableAliasMap, localTableCollection)) {
			// 	Integer[] outputColLocation = walker.getLineAndCharacterFromEntry(localCurrentQueryDictionary.get(outputCol));
			// 	if (outputColLocation[0] == null || outputColLocation[1] == null) {
			// 		outputColLocation = walker.getFirstEntryLineAndCharacter(localCurrentQueryDictionary);
			// 	}
			// 	String unresolvedSourceRefs = walker.formatInterfaceColumnReferences(localInterface.get(outputCol));
			// 	// Detection point: output column in query interface has unresolved references that cannot be resolved to any 
			// 	// available column in the table or query dictionaries.
			// 	String diagCode = walker.getDiagnosticCode(SqlASTWalkerHelper.DIAG_SQL_INTERFACE_COLUMN_UNRESOLVED);
			// 	String diagTemplate = walker.getDiagnosticMessage(SqlASTWalkerHelper.DIAG_SQL_INTERFACE_COLUMN_UNRESOLVED);
			// 	String diagMessage = (diagTemplate == null)
			// 			? String.format("Output column '%s' at (l:%s c:%s) has unresolved source reference(s): %s",
			// 					outputCol,
			// 					outputColLocation[0],
			// 					outputColLocation[1],
			// 					unresolvedSourceRefs)
			// 			: String.format(diagTemplate,
			// 					outputCol,
			// 					outputColLocation[0],
			// 					outputColLocation[1],
			// 					unresolvedSourceRefs);
			// 	// Collection point: record fatal diagnostic in walker helper diagnostics list.
			// 	walker.addWalkerFatal(
			// 			diagCode,
			// 			diagMessage,
			// 			outputColLocation[0], // line number
			// 			outputColLocation[1], // character position
			// 			outputCol);
			// }
		}
		}

		 walker.validateQueryInterface(localInterface, localCurrentQueryDictionary, localTableAliasMap, localTableCollection);
		 walker.validateFilterReferences(filtersList, localCurrentQueryDictionary, localTableAliasMap, localTableCollection);

		// Merge everything back together into the final symbol table for this level of the query, with table aliases first, 
		// then symbol table entries, then query entries, then table entries, then the interface and current query dictionary 
		// for this level, and finally any filters list for this level if it exists.
		if (localTableAliasMap != null && !localTableAliasMap.isEmpty())
			walker.symbolTable.put(MUMBLE_TABLE_ALIAS_KEY, localTableAliasMap);
		walker.symbolTable.put(MUMBLE_TABLE_DICTIONARY_KEY, localTableCollection);
		walker.symbolTable.put(MUMBLE_QUERY_DICTIONARY_KEY, localCurrentQueryDictionary);
		//walker.symbolTable.putAll(localCurrentQueryDictionary);
		//walker.symbolTable.putAll(localTableCollection);
		walker.symbolTable.put(MUMBLE_INTERFACE_KEY, localInterface);
		if (!localUnresolvedColumnMap.isEmpty()) {
			walker.symbolTable.put(MUMBLE_UNRESOLVED_COLUMN_KEY, localUnresolvedColumnMap);
		}
		// Call a method here that will merge the local Table Dictionary into the walker's TableDictionary Map
		walker.mergeTableDictionaryIntoWalkerTableDictionary(currentTableDictionary);
		
		if (localScalarSubqueryAliases != null)
			walker.symbolTable.put(MUMBLE_SCALAR_SUBQUERY_ALIASES_KEY, localScalarSubqueryAliases);
		if (filtersList != null)
			walker.symbolTable.put(MUMBLE_FILTERS_KEY, filtersList);

		 walker.showTrace(walker.symbolTrace, "Symbol Table: " + walker.symbolTable);
		return walker.symbolTable;
	}

	@SuppressWarnings("unchecked")
	private HashMap<String, Object> extractExplicitQualifiedUnknownEntries(
			HashMap<String, Object> unresolvedColumnMap,
			HashMap<String, Object> localInterface,
			Object filtersList) {
		HashMap<String, Object> explicitQualified = new HashMap<String, Object>();
		if (unresolvedColumnMap == null || unresolvedColumnMap.isEmpty()) {
			return explicitQualified;
		}

		HashSet<String> explicitQualifiedNames = new HashSet<String>();

		if (localInterface != null) {
			for (Object refsObj : localInterface.values()) {
				if (!(refsObj instanceof ArrayList<?> refs)) {
					continue;
				}
				for (Object refObj : refs) {
					String refName = walker.extractReferenceNameFromInterfaceEntry(refObj);
					String refTable = walker.extractReferenceTableRefFromInterfaceEntry(refObj);
					if (refName != null && refTable != null && !"*".equals(refTable) && unresolvedColumnMap.containsKey(refName)) {
						explicitQualifiedNames.add(refName);
					}
				}
			}
		}

		if (filtersList instanceof ArrayList<?> filters) {
			for (Object filterObj : filters) {
				if (!(filterObj instanceof Map<?, ?> filterMap)) {
					continue;
				}
				Object filterNameObj = filterMap.get(MUMBLE_NAME_KEY);
				Object filterTableRefObj = filterMap.get(MUMBLE_TABLE_REF_KEY);
				if (filterNameObj instanceof String filterName
						&& filterTableRefObj instanceof String filterTableRef
						&& !"*".equals(filterTableRef)
						&& unresolvedColumnMap.containsKey(filterName)) {
					explicitQualifiedNames.add(filterName);
				}
			}
		}

		for (String qualifiedName : explicitQualifiedNames) {
			Object removed = unresolvedColumnMap.remove(qualifiedName);
			if (removed != null) {
				explicitQualified.put(qualifiedName, removed);
			}
		}

		return explicitQualified;
	}

	@SuppressWarnings("unchecked")
	private void emitExplicitQualifiedUnknownDiagnostics(
			HashMap<String, Object> explicitQualifiedUnknownEntries,
			HashMap<String, Object> localInterface,
			Object filtersList,
			HashMap<String, Object> tableAliasCollection,
			HashMap<String, Object> scopedQueryCollection,
			HashMap<String, Object> localCurrentQueryDictionary) {
		if (explicitQualifiedUnknownEntries == null || explicitQualifiedUnknownEntries.isEmpty()) {
			return;
		}

		HashMap<String, String> explicitTableRefByColumn = new HashMap<String, String>();
		if (localInterface != null) {
			for (Object refsObj : localInterface.values()) {
				if (!(refsObj instanceof ArrayList<?> refs)) {
					continue;
				}
				for (Object refObj : refs) {
					String refName = walker.extractReferenceNameFromInterfaceEntry(refObj);
					String refTable = walker.extractReferenceTableRefFromInterfaceEntry(refObj);
					if (refName != null && refTable != null && !"*".equals(refTable)) {
						explicitTableRefByColumn.putIfAbsent(refName, refTable);
					}
				}
			}
		}

		if (filtersList instanceof ArrayList<?> filters) {
			for (Object filterObj : filters) {
				if (!(filterObj instanceof Map<?, ?> filterMap)) {
					continue;
				}
				Object filterNameObj = filterMap.get(MUMBLE_NAME_KEY);
				Object filterTableRefObj = filterMap.get(MUMBLE_TABLE_REF_KEY);
				if (filterNameObj instanceof String filterName
						&& filterTableRefObj instanceof String filterTableRef
						&& !"*".equals(filterTableRef)) {
					explicitTableRefByColumn.putIfAbsent(filterName, filterTableRef);
				}
			}
		}

		for (Map.Entry<String, Object> unknownEntry : explicitQualifiedUnknownEntries.entrySet()) {
			String columnName = unknownEntry.getKey();
			if (localCurrentQueryDictionary != null && localCurrentQueryDictionary.containsKey(columnName)) {
				// This column is already represented at the current query level; defer to interface validation
				// so diagnostics use the query-level token location and are emitted once.
				continue;
			}
			String tableRef = explicitTableRefByColumn.get(columnName);
			if (tableRef == null) {
				continue;
			}

			String resolvedTableRef = walker.resolveAliasToTableName(tableRef, tableAliasCollection);
			String resolvedNonTableSourceRef = walker.resolveAliasToNonTableSourceQueryKey(tableRef, scopedQueryCollection);
			boolean explicitQueryReference = resolvedNonTableSourceRef != null
					|| (resolvedTableRef != null && resolvedTableRef.startsWith("query"));
			if (explicitQueryReference) {
				continue;
			}

			String indicatedSourceRef = (resolvedNonTableSourceRef != null)
					? resolvedNonTableSourceRef
					: (resolvedTableRef != null ? resolvedTableRef : tableRef);

			Integer[] refLocation = walker.getLineAndCharacterFromEntry(unknownEntry.getValue());
			if (refLocation[0] == null || refLocation[1] == null) {
				refLocation = walker.getFirstEntryLineAndCharacter(explicitQualifiedUnknownEntries);
			}

			String diagCode = walker.getDiagnosticCode(SqlASTWalkerHelper.DIAG_SQL_QUALIFIED_COLUMN_NOT_FOUND_IN_TABLE);
			String diagTemplate = walker.getDiagnosticMessage(SqlASTWalkerHelper.DIAG_SQL_QUALIFIED_COLUMN_NOT_FOUND_IN_TABLE);
			String diagMessage =  String.format(diagTemplate,
							columnName,
							refLocation[0],
							refLocation[1],
							indicatedSourceRef);

			walker.addWalkerFatal(
					diagCode,
					diagMessage,
					refLocation[0],
					refLocation[1],
					columnName);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void exitTable_primary(@NotNull SQLSelectParserParser.Table_primaryContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		int parentRuleIndex = ctx.getParent().getRuleIndex();

		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Integer parentStackLevel = walker.currentStackLevel(parentRuleIndex);

		Map<String, Object> subMap = walker.removeNodeMap(ruleIndex, stackLevel);
		Object type = subMap.remove(ASTWALKER_RULE_TYPE_KEY);
		Map<String, Object> item;
		String alias = null;

		if (ctx.getChildCount() == 1) {
			item = (Map<String, Object>) subMap.remove("1");
			if (item.keySet().contains(MUMBLE_TABLE_KEY)) {
				item.put(MUMBLE_ALIAS_KEY, null);

				Object table = item.get(MUMBLE_TABLE_KEY);
				if (table != null) {
					// Table doesn't have an Alias, so it doesn't need to be collected in the Table ALias map
					// walker.collectTableAlias(alias, table);

					// However, we still need to collect the table reference in the AST 
					subMap.put(MUMBLE_TABLE_KEY, item);

					// And add the table header to the local Table_Dictionary
					walker.ensureTableDictionaryEntry(table.toString());
				} else {
					// TODO: not sure this can get called. 
					alias = "unnamed";
					
					Boolean done = collectQuerySymbolTable(MUMBLE_QUERY_KEY, alias);
					if (!done)
						done = collectQuerySymbolTable(MUMBLE_INSERT_KEY, alias);
					if (!done)
						done = collectQuerySymbolTable(MUMBLE_UPDATE_KEY, alias);
					if (!done)
						done = collectQuerySymbolTable(MUMBLE_UNION_KEY, alias);
					if (!done)
						done = collectQuerySymbolTable(MUMBLE_INTERSECT_KEY, alias);
				}
			} else { // VALUES STATEMENT can only happen in this instance
				subMap.putAll(item);
				
				// And add the table header to the local Table_Dictionary
				String table = (String) item.get(MUMBLE_VALUES_KEY);
				walker.ensureTableDictionaryEntry(table);

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
				walker.collectTableAlias(alias, table);
				walker.ensureTableDictionaryEntry(table.toString());
				
			} else if (reference.containsKey(MUMBLE_SUBSTITUTION_KEY)) {
				// Check for Substitution Variable
				item.putAll(reference);
				// Collect Symbol Table Reference
				Map<String, Object> substitution = (Map<String, Object>) reference.get(MUMBLE_SUBSTITUTION_KEY);
				String tableName = (String) substitution.get("name");
				walker.collectTableAlias(alias, tableName);
				walker.ensureTableDictionaryEntry(tableName);

			} else {// then it's a query, add it to the tree no matter what kind of query it is
				item.put(MUMBLE_QUERY_KEY, reference);
				// Add the query to the symbol table tree 
				Boolean done = collectQuerySymbolTable(MUMBLE_QUERY_KEY, alias);
				if (!done)
					done = collectQuerySymbolTable(MUMBLE_INSERT_KEY, alias);
				if (!done)
					done = collectQuerySymbolTable(MUMBLE_UPDATE_KEY, alias);
				if (!done)
					done = collectQuerySymbolTable(MUMBLE_UNION_KEY, alias);
				if (!done)
					done = collectQuerySymbolTable(MUMBLE_INTERSECT_KEY, alias);
			}

			subMap.put(MUMBLE_TABLE_KEY, item);
		} else {
			walker.showTrace(walker.parseTrace, "Wrong number of entries: " + ctx.getText());
		}
		walker.addToParent(parentRuleIndex, parentStackLevel, subMap);
		walker.showTrace(walker.parseTrace, "TABLE PRIMARY: " + subMap);
	}
	
	@Override
	public void exitInsert_source_primary(@NotNull SQLSelectParserParser.Insert_source_primaryContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		int parentRuleIndex = ctx.getParent().getRuleIndex();

		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Integer parentStackLevel = walker.currentStackLevel(parentRuleIndex);

		Map<String, Object> subMap = walker.removeNodeMap(ruleIndex, stackLevel);
		Object type = subMap.remove(ASTWALKER_RULE_TYPE_KEY);
		Map<String, Object> item;
		String alias = null;

		item = new HashMap<String, Object>();
		Map<String, Object> reference = walker.checkForSubstitutionVariable((Map<String, Object>) subMap.remove("1"),
					"tuple");

		if (reference.containsKey(MUMBLE_VALUES_KEY)) {
			
			subMap.putAll(reference);
			
		} else if (reference.containsKey(MUMBLE_SUBSTITUTION_KEY)) {
			// Check for Substitution Variable
			Map<String, Object> substitution = (Map<String, Object>) reference.get(MUMBLE_SUBSTITUTION_KEY);
			// Collect Symbol Table Reference
			String name = (String) substitution.get("name");
			walker.ensureTableDictionaryEntry(name);
			subMap.putAll(reference);

		} else { // then it's a query, add it to the tree no matter what kind of query it is
			item.put(MUMBLE_QUERY_KEY, reference);
			// Add the query to the symbol table tree 
			Boolean done = collectQuerySymbolTable(MUMBLE_QUERY_KEY, alias);
			if (!done)
					done = collectQuerySymbolTable(MUMBLE_INSERT_KEY, alias);
			if (!done)
					done = collectQuerySymbolTable(MUMBLE_UPDATE_KEY, alias);
			if (!done)
					done = collectQuerySymbolTable(MUMBLE_UNION_KEY, alias);
			if (!done)
					done = collectQuerySymbolTable(MUMBLE_INTERSECT_KEY, alias);
			subMap.putAll(reference);
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
		Object type = subMap.remove(ASTWALKER_RULE_TYPE_KEY);
		// Get the referenced AST segment from the stack and collect any Substitution Variables in the process
		Map<String, Object> item = walker.checkForSubstitutionVariable((Map<String, Object>) subMap.remove("1"),
					"tuple");
		
		// allocate work variables if needed later

		// Figure out what kind of Tuple entry you've got, and construct the next level's subtree
		if (item.containsKey(MUMBLE_TABLE_KEY)) {
			// Table Reference
			Object table = item.get(MUMBLE_TABLE_KEY);
			walker.ensureTableDictionaryEntry((String) table);
			// Table reference needs an AST Key added to it
			subMap.put(MUMBLE_TABLE_KEY, item);

		} else if (item.containsKey(MUMBLE_SUBSTITUTION_KEY)) {
			// Substitution Variable
			Map<String, Object> substitution = (Map<String, Object>) item.get(MUMBLE_SUBSTITUTION_KEY);
			// Collect Symbol Table Reference
			String name = (String) substitution.get("name");
			walker.ensureTableDictionaryEntry(name);
			// Substitution Variable is ready for use
			subMap.putAll(item);

		} else if (item.containsKey(MUMBLE_VALUES_KEY)) {
			//	Values Statement is simply ready for use
			subMap.putAll(item);
			
		} else { 
			// Only other option is a QUERY Object of some kind
			// Add the query to the symbol table tree and collect any interface elements
			String alias = null;
		
			Boolean done = collectQuerySymbolTable(MUMBLE_QUERY_KEY, alias);
			if (!done)
					done = collectQuerySymbolTable(MUMBLE_INSERT_KEY, alias);
			if (!done)
					done = collectQuerySymbolTable(MUMBLE_UPDATE_KEY, alias);
			if (!done)
					done = collectQuerySymbolTable(MUMBLE_UNION_KEY, alias);
			if (!done)
					done = collectQuerySymbolTable(MUMBLE_INTERSECT_KEY, alias);
			// Add the query AST to the tree, it is ready for use As Is
			subMap.putAll(item);
		}

		// Put nearly-completed AST back into parent rule and stack level
		walker.addToParent(parentRuleIndex, parentStackLevel, subMap);
		walker.showTrace(walker.parseTrace, "TUPLE PRIMARY: " + subMap);
	}
	

	private Boolean collectQuerySymbolTable(String hdr, String alias) {
		String queryName = hdr + (walker.queryCount - 1);
		Map<String, Object> query = (Map<String, Object>)  walker.symbolTable.remove(queryName);
		if (query != null) {
			// add alias to query
			if (alias != null) {
				walker.collectTableAlias(alias, queryName);
			} else
				 walker.symbolTable.put(queryName, new HashMap<String, Object>());

			// propagate interface to outer layer of query
			Map<String, Object> hold = (Map<String, Object>)  walker.symbolTable.get(queryName);
			// Move unknowns to query
			Map<String, Object> unk = (Map<String, Object>)  walker.symbolTable.remove(MUMBLE_UNRESOLVED_COLUMN_KEY);

			if (unk != null) {
				if (hold != null) {
					// move any other interface elements to query and empty unknowns
					Map<String, Object> interfac = (Map<String, Object>) query.get(MUMBLE_INTERFACE_KEY);
					if (interfac != null)
						for (String key : interfac.keySet()) {
							Object unkItem = unk.remove(key);
							if (unkItem != null)
								hold.put(key, unkItem);
						}
				}

				// if any unknowns left, put them back into table
				if (unk.size() > 0)
					 walker.symbolTable.put(MUMBLE_UNRESOLVED_COLUMN_KEY, unk);
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
		Object type = subMap.remove(ASTWALKER_RULE_TYPE_KEY);

		if (subMap.size() == 1) {
			walker.showTrace(walker.parseTrace, "Just One Identifier: " + subMap);
			String table = (String) subMap.remove("1");

			// try swapping names here
			table = getTableName(table);

			subMap.put(MUMBLE_TABLE_KEY, table);
			walker.showTrace(walker.parseTrace, "table: " + table + " Map: " + subMap);
		} else if (subMap.size() == 2) {
			walker.showTrace(walker.parseTrace, "Two entries: " + subMap);
			String schema = (String) subMap.remove("1");
			subMap.put(MUMBLE_SCHEMA_KEY, schema);
			String table = (String) subMap.remove("2");

			// try swapping names here
			table = getTableName(table);

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
		subMap.remove(ASTWALKER_RULE_TYPE_KEY);
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
		subMap.remove(ASTWALKER_RULE_TYPE_KEY);
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
		Object type = subMap.remove(ASTWALKER_RULE_TYPE_KEY);
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
		 walker.asTree.put("SKIP", "TRUE");
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
		subMap.remove(ASTWALKER_RULE_TYPE_KEY);
	}

	@Override
	public void exitColumn_reference(@NotNull SQLSelectParserParser.Column_referenceContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Map<String, Object> subMap = walker.getNodeMap(ruleIndex, stackLevel);
		subMap.remove(ASTWALKER_RULE_TYPE_KEY);

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
			walker.collectUnresolvedColumnReference(tableRefKey, columnRef, ctx.getStart());
		}
		walker.showTrace(walker.parseTrace, "Column Reference: " + subMap);
	}


	@Override
	public void exitColumn_primary(@NotNull SQLSelectParserParser.Column_primaryContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Map<String, Object> subMap = walker.getNodeMap(ruleIndex, stackLevel);
		subMap.remove(ASTWALKER_RULE_TYPE_KEY);

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
			walker.collectUnresolvedColumnReference(tableRefKey, columnRef, ctx.getStart());
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
		Map<String, Object> item = walker.checkForSubstitutionVariable((Map<String, Object>) subMap.get("1"), "predicand");


		if (item.containsKey(MUMBLE_SELECT_KEY)) {
			// then this item is a subquery, so we need to push it down under a LOOKUP subtree in the AST
				Map<String, Object> lookup = new HashMap<String, Object>();
				lookup.putAll(item);
				item = new HashMap<String, Object>();
				item.put(MUMBLE_LOOKUP_KEY, lookup);
			 	walker.showTrace(walker.parseTrace, "Select Item is a Lookup Subquery: " + item);
				// replace the first entry in the AST Tree with the modified item subtree for scalar SQL trees
				subMap.put("1", item);
			}

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
		Object type = subMap.remove(ASTWALKER_RULE_TYPE_KEY);

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
		Object type = subMap.remove(ASTWALKER_RULE_TYPE_KEY);

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
		Object type = subMap.remove(ASTWALKER_RULE_TYPE_KEY);

		Map<String, Object> item = new HashMap<String, Object>();

		if (subMap.size() == 2) {
			item.put(MUMBLE_FUNCTION_NAME_KEY, subMap.remove("1"));
			item.put(MUMBLE_QUALIFIER_KEY, null);
			item.put(MUMBLE_PARAMETERS_KEY, subMap.remove("2"));
			subMap.put(MUMBLE_FUNCTION_KEY, item);
		} else if (subMap.size() == 3) {
			item.put(MUMBLE_FUNCTION_NAME_KEY, subMap.remove("1"));
			Map<String, Object> hold = (Map<String, Object>) subMap.remove("2");
			if (hold.containsKey(ASTWALKER_RULE_TYPE_KEY)) {
				Integer childKey = (Integer) hold.remove(ASTWALKER_RULE_TYPE_KEY);
				if (childKey.equals((Integer) SQLSelectParserParser.RULE_set_qualifier))
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
			Object type = subMap.remove(ASTWALKER_RULE_TYPE_KEY);
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
			Object type = subMap.remove(ASTWALKER_RULE_TYPE_KEY);
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
			Object type = subMap.remove(ASTWALKER_RULE_TYPE_KEY);
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
			Object type = subMap.remove(ASTWALKER_RULE_TYPE_KEY);
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
			Object type = subMap.remove(ASTWALKER_RULE_TYPE_KEY);
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
			Object type = subMap.remove(ASTWALKER_RULE_TYPE_KEY);
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
			Object type = subMap.remove(ASTWALKER_RULE_TYPE_KEY);

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
			Object type = subMap.remove(ASTWALKER_RULE_TYPE_KEY);

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
			subMap.remove(ASTWALKER_RULE_TYPE_KEY);
			
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
			subMap.remove(ASTWALKER_RULE_TYPE_KEY);
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
			Object type = subMap.remove(ASTWALKER_RULE_TYPE_KEY);

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
			subMap.remove(ASTWALKER_RULE_TYPE_KEY);
			
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
			subMap.remove(ASTWALKER_RULE_TYPE_KEY);
			
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
			Object type = subMap.remove(ASTWALKER_RULE_TYPE_KEY);

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
			subMap.remove(ASTWALKER_RULE_TYPE_KEY);
			
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
			Object type = subMap.remove(ASTWALKER_RULE_TYPE_KEY);

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
			Object type = subMap.remove(ASTWALKER_RULE_TYPE_KEY);
			String functType = (String) subMap.remove("1");

			Map<String, Object> item = new HashMap<String, Object>();
			Map<String, Object> hold = new HashMap<String, Object>();

			if (subMap.size() == 0) {
				item.put(MUMBLE_PARAMETERS_KEY, null);
			} else if (subMap.size() >= 1) {
				hold = (Map<String, Object>) subMap.remove("2");
				type = hold.remove(ASTWALKER_RULE_TYPE_KEY);
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
			Object type = subMap.remove(ASTWALKER_RULE_TYPE_KEY);

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
			Object type = subMap.remove(ASTWALKER_RULE_TYPE_KEY);

			if (subMap.size() >= 1) {
				HashMap<String, Object> item = (HashMap<String, Object>) subMap.remove("1");
				type = item.remove(ASTWALKER_RULE_TYPE_KEY);

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
			Object type = subMap.remove(ASTWALKER_RULE_TYPE_KEY);

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
			Object type = subMap.remove(ASTWALKER_RULE_TYPE_KEY);

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
			Object type = subMap.remove(ASTWALKER_RULE_TYPE_KEY);

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
			Object type = subMap.remove(ASTWALKER_RULE_TYPE_KEY);

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
			subMap.remove(ASTWALKER_RULE_TYPE_KEY);
			
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
			subMap.remove(ASTWALKER_RULE_TYPE_KEY);
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
			subMap.remove(ASTWALKER_RULE_TYPE_KEY);
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
		subMap.remove(ASTWALKER_RULE_TYPE_KEY);
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
		subMap.remove(ASTWALKER_RULE_TYPE_KEY);
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
		Integer parentRuleIndex = (Integer) ctx.getParent().getRuleIndex();
		if (parentRuleIndex.equals((Integer) SQLSelectParserParser.RULE_over_clause)) {

			Integer stackLevel = walker.currentStackLevel(ruleIndex);
			Integer parentStackLevel = walker.currentStackLevel(parentRuleIndex);

			Map<String, Object> subMap = walker.removeNodeMap(ruleIndex, stackLevel);
			// Part of a window function
			subMap.remove(ASTWALKER_RULE_TYPE_KEY);
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
//		Object type = subMap.remove(ASTWALKER_RULE_TYPE_KEY);

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

		// FIRST Capture the Filter Dependencies
		// Remove the FILTERS entry from the symbol table. Create a variable to hold it.
		// Create a variable to hold the column list value from the filters entry if it exists. 
		// If there is no filters entry, create an empty map to hold the filters and an empty 
		// list to hold the column list value
		Object filters = walker.symbolTable.remove(MUMBLE_FILTERS_KEY);
		ArrayList<Object> flatList;
		if (filters != null) {
			flatList = (ArrayList<Object>) filters;
		} else {
			flatList = new ArrayList<Object>();
		}

		// Create a flatten list to contain the column and predicand references, then call the 
		// existing flatten function used to find column and predicands in a subtree for interface, to populate it
		flattenSubTreeForFilterColumns((HashMap<String, Object>) subMap, flatList);

		// Add the flatList back into the SymbolTree as the Object part of the filter entry. Use the MUMBLE_FILTERS_KEY as the key
		walker.symbolTable.put(MUMBLE_FILTERS_KEY, flatList);

		// NOW handle the push down of the search condition as normal
		walker.handleOneChild(ruleIndex);
	}

	// Standardize the filters reference map into a flat map of column references and not the entire AST subtree
	// This is a recursive function that traverses the item subtree until it finds column references or substitution variables, 
	// which it adds to the column list with the alias as the key

	private void flattenSubTreeForFilterColumns(HashMap<String, Object> subTree, ArrayList<Object> columnList) {
		if (subTree.containsKey(MUMBLE_COLUMN_KEY)) {
			Object col = subTree.get(MUMBLE_COLUMN_KEY);
			if (!columnList.contains(col)) {
				columnList.add(col);
			}
		} else if (subTree.containsKey(MUMBLE_SUBSTITUTION_KEY)) {
			Object subst = subTree.get(MUMBLE_SUBSTITUTION_KEY);
			if (subst instanceof HashMap) {
				HashMap<String, Object> substMap = (HashMap<String, Object>) subst;
				Object type = substMap.get("type");
				if (type != null && (MUMBLE_COLUMN_KEY.equals(type) || MUMBLE_PREDICAND_KEY.equals(type))) {
					if (!columnList.contains(subst)) {
						columnList.add(subst);
					}
				}
			}
		} else if (subTree.containsKey(MUMBLE_SELECT_KEY)) {
		} else {
			for (Object key : subTree.keySet()) {
				Object value = subTree.get(key);
				if (value instanceof HashMap) {
					flattenSubTreeForFilterColumns((HashMap<String, Object>) value, columnList);
				}
			}
		}
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
		Object type = subMap.remove(ASTWALKER_RULE_TYPE_KEY);

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
		Object type = subMap.remove(ASTWALKER_RULE_TYPE_KEY);

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
		Object type = subMap.remove(ASTWALKER_RULE_TYPE_KEY);

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
		Object type = subMap.remove(ASTWALKER_RULE_TYPE_KEY);

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
		subMap.remove(ASTWALKER_RULE_TYPE_KEY);
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
		Object type = subMap.remove(ASTWALKER_RULE_TYPE_KEY);

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
		Object type = subMap.remove(ASTWALKER_RULE_TYPE_KEY);

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
		subMap.remove(ASTWALKER_RULE_TYPE_KEY);

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
		Object type = subMap.remove(ASTWALKER_RULE_TYPE_KEY);

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
		subMap.remove(ASTWALKER_RULE_TYPE_KEY);

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
		subMap.remove(ASTWALKER_RULE_TYPE_KEY);
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
		subMap.remove(ASTWALKER_RULE_TYPE_KEY);
		subMap.put("Operator", text);
		
		walker.handleOneChild(ruleIndex);
	}

	@Override
	public void enterIn_predicate_value(@NotNull SQLSelectParserParser.In_predicate_valueContext ctx) {
		walker.pushSymbolTable();
	}

	@Override
	public void exitIn_predicate_value(@NotNull SQLSelectParserParser.In_predicate_valueContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		int parentRuleIndex = ctx.getParent().getRuleIndex();
		Map<String, Object> subMap = walker.getNodeMap(ruleIndex, stackLevel);
		Map<String, Object> reference = walker.checkForSubstitutionVariable((Map<String, Object>) subMap.get("1"), "in_list");

		walker.handleOneChild(ruleIndex);

		// Conditionally restore or inject based on whether the IN value is a subquery.
		HashMap<String, Object> symbols = walker.symbolTable;

		// Decide based on the subtree content in reference: true when it contains SELECT key
		boolean isSubquery = (reference != null) && reference.containsKey(MUMBLE_SELECT_KEY);
		
		if (isSubquery) {
			// Subquery case: inject the subquery's local symbol table under IN_LIST<N>
			String key = MUMBLE_IN_LIST_KEY + walker.queryCount;
			// Extract the singular query reference key from the local symbol table (e.g., "query0")
			String queryRefKey = null;
			if (symbols != null && !symbols.isEmpty()) {
				queryRefKey = symbols.keySet().iterator().next();
			}
			// Store only the query reference key under the IN_LIST<N> entry
			symbols.put(key, queryRefKey);

			// Capture Query Column Dictionary for this level
// ***			walker.queryColumnDictionaryMap.put(key, walker.symbolTable.remove(CURRENT_QUERY_COLUMN_DICTIONARY));

			// Modify the symbol table by removing the query and adding it back with the def prefix to avoid conflicts
			symbols.put("def_" + queryRefKey, symbols.remove(queryRefKey));

			// Merge local symbol table back into parent
			walker.popSymbolTablePutAll(symbols);

			// Advance query counter after recording the injection
			walker.queryCount++;

		} else {
			// Default: not a subquery, just restore the parent's symbol table
			walker.popSymbolTablePutAll(symbols);
		}
			
	}

	/*
==============================================================================================
  8.9 <exists predicate>

  Specify a test for a non_empty set.
==============================================================================================
*/

	@Override
	public void exitExists_predicate(@NotNull SQLSelectParserParser.Exists_predicateContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		int parentRuleIndex = ctx.getParent().getRuleIndex();

		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Integer parentStackLevel = walker.currentStackLevel(parentRuleIndex);

		Map<String, Object> subMap = walker.removeNodeMap(ruleIndex, stackLevel);
		Object type = subMap.remove(ASTWALKER_RULE_TYPE_KEY);
		if (subMap.size() == 2) {
			// Variation 2: When contains CONDITIONS
			subMap.putAll((Map<String, Object>) subMap.remove("1"));
			subMap.putAll((Map<String, Object>) subMap.remove("2"));
		} else {
			walker.showTrace(walker.parseTrace, "Wrong number of entries: " + ctx.getText());
		}

		Map<String, Object> item = new HashMap<String, Object>();
		item.put(MUMBLE_EXISTS_KEY, subMap);
		walker.addToParent(parentRuleIndex, parentStackLevel, item);
		walker.showTrace(walker.parseTrace, "Case: " + item);
	}

	@Override
	public void exitExists_operator(@NotNull SQLSelectParserParser.Exists_operatorContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		int parentRuleIndex = ctx.getParent().getRuleIndex();

		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Integer parentStackLevel = walker.currentStackLevel(parentRuleIndex);

		Map<String, Object> subMap = walker.removeNodeMap(ruleIndex, stackLevel);

		if (subMap == null) {
			// unqualified select all has no map
			subMap = walker.makeRuleMap(ruleIndex);
		}
		subMap.remove(ASTWALKER_RULE_TYPE_KEY);
		if (ctx.getChildCount() == 1) {
			subMap.put(MUMBLE_OPERATOR_KEY, ctx.getChild(0).getText());
		} else if (ctx.getChildCount() == 2) {
			subMap.put(MUMBLE_OPERATOR_KEY, ctx.getChild(0).getText() + " " + ctx.getChild(1).getText());
		}
		// Add item to parent map
		walker.addToParent(parentRuleIndex, parentStackLevel, subMap);
	}

	@Override
	public void enterExists_predicate_value(@NotNull SQLSelectParserParser.Exists_predicate_valueContext ctx) {
		walker.pushSymbolTable();
	}

	@Override
	public void exitExists_predicate_value(@NotNull SQLSelectParserParser.Exists_predicate_valueContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		int parentRuleIndex = ctx.getParent().getRuleIndex();
		Map<String, Object> subMap = walker.getNodeMap(ruleIndex, stackLevel);
		Map<String, Object> reference = walker.checkForSubstitutionVariable((Map<String, Object>) subMap.get("1"), "tuple");

		walker.handleOneChild(ruleIndex);

		// Conditionally restore or inject based on whether the IN value is a subquery.
		HashMap<String, Object> symbols = walker.symbolTable;

		// Decide based on the subtree content in reference: true when it contains SELECT key
		boolean isSubquery = (reference != null) && reference.containsKey(MUMBLE_SELECT_KEY);
		
		if (isSubquery) {
			// Subquery case: inject the subquery's local symbol table under EXISTS<N>
			String key = MUMBLE_EXISTS_KEY + walker.queryCount;
			// Extract the singular query reference key from the local symbol table (e.g., "query0")
			String queryRefKey = null;
			if (symbols != null && !symbols.isEmpty()) {
				queryRefKey = symbols.keySet().iterator().next();
			}
			// Store only the query reference key under the EXISTS<N> entry
			symbols.put(key, queryRefKey);
			// Capture Query Column Dictionary for this level
// ***			walker.queryColumnDictionaryMap.put(key, walker.symbolTable.remove(CURRENT_QUERY_COLUMN_DICTIONARY));
			// Modify the symbol table by removing the query and adding it back with the def prefix to avoid conflicts
			symbols.put("def_" + queryRefKey, symbols.remove(queryRefKey));
			// Merge local symbol table back into parent
			walker.popSymbolTablePutAll(symbols);
			// Advance query counter after recording the injection
			walker.queryCount++;

		} else {
			// Default: not a subquery, just restore the parent's symbol table
			walker.popSymbolTablePutAll(symbols);
		}
			
	}

	@Override
	public void enterPredicand_subquery(@NotNull SQLSelectParserParser.Predicand_subqueryContext ctx) {
		walker.pushSymbolTable();
	}

	@Override
	public void exitPredicand_subquery(@NotNull SQLSelectParserParser.Predicand_subqueryContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		int parentRuleIndex = ctx.getParent().getRuleIndex();
		Map<String, Object> subMap = walker.getNodeMap(ruleIndex, stackLevel);
		Map<String, Object> reference = walker.checkForSubstitutionVariable((Map<String, Object>) subMap.get("1"), "tuple");

		walker.handleOneChild(ruleIndex);

		// Conditionally restore or inject based on whether the IN value is a subquery.
		HashMap<String, Object> symbols = walker.symbolTable;

		// Decide based on the subtree content in reference: true when it contains SELECT key
		boolean isSubquery = (reference != null) && reference.containsKey(MUMBLE_SELECT_KEY);
		
		if (isSubquery) {
			// Subquery case: inject the subquery's local symbol table under predicand<N>
			String key = MUMBLE_PREDICAND_KEY + walker.queryCount;

			// Capture Query Column Dictionary for this level
// ***			walker.queryColumnDictionaryMap.put(key, walker.symbolTable.remove(CURRENT_QUERY_COLUMN_DICTIONARY));

			// Extract the singular query reference key from the local symbol table (e.g., "query0")
			String queryRefKey = null;
			if (symbols != null && !symbols.isEmpty()) {
				queryRefKey = symbols.keySet().iterator().next();
			}
			// Store only the query reference key under the PREDICAND<N> entry
			symbols.put(key, queryRefKey);
			// Modify the symbol table by removing the query and adding it back with the def prefix to avoid conflicts
			symbols.put("def_" + queryRefKey, symbols.remove(queryRefKey));
			// Merge local symbol table back into parent
			walker.popSymbolTablePutAll(symbols);
			// Advance query counter after recording the injection
			walker.queryCount++;

		} else {
			// Default: not a subquery, just restore the parent's symbol table
			walker.popSymbolTablePutAll(symbols);
		}
			
	}

	@Override
	public void exitIn_value_list(@NotNull SQLSelectParserParser.In_value_listContext ctx) {
		int ruleIndex = ctx.getRuleIndex();

		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Map<String, Object> subMap = walker.removeNodeMap(ruleIndex, stackLevel);
		subMap.remove(ASTWALKER_RULE_TYPE_KEY);
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
		subMap.remove(ASTWALKER_RULE_TYPE_KEY);
		
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
		subMap.remove(ASTWALKER_RULE_TYPE_KEY);

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
		subMap.remove(ASTWALKER_RULE_TYPE_KEY);

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
		subMap.remove(ASTWALKER_RULE_TYPE_KEY);

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
		Object type = subMap.remove(ASTWALKER_RULE_TYPE_KEY);

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
		Object type = subMap.remove(ASTWALKER_RULE_TYPE_KEY);

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
		Object type = subMap.remove(ASTWALKER_RULE_TYPE_KEY);

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
		Object type = subMap.remove(ASTWALKER_RULE_TYPE_KEY);

		Map<String, Object> item = new HashMap<String, Object>();

		if (subMap.size() == 2) {
			item.putAll((Map<? extends String, ? extends Object>) subMap.remove("1"));
			subMap = (Map<String, Object>) subMap.remove("2");
			type = subMap.remove(ASTWALKER_RULE_TYPE_KEY);
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
		Object type = subMap.remove(ASTWALKER_RULE_TYPE_KEY);

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
		Integer parentRuleIndex = (Integer) ctx.getParent().getRuleIndex();
		if (parentRuleIndex.equals((Integer) SQLSelectParserParser.RULE_sql_argument_list)) {
			Integer stackLevel = walker.currentStackLevel(ruleIndex);
			Map<String, Object> subMap = walker.getNodeMap(ruleIndex, stackLevel);
			subMap.remove(ASTWALKER_RULE_TYPE_KEY);

			Map<String, Object> valueExpression = null;
			String tableRef = MUMBLE_UNKNOWN_KEY;
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
				if (tableRef == null)
					tableRef = MUMBLE_UNKNOWN_KEY;
				walker.collectUnresolvedColumnReference(tableRef, valueExpression, ctx.getStart());
				// Add column to SQL AST Tree
				subMap.putAll(valueExpression);
			}
			walker.showTrace(walker.parseTrace, "Column Reference: " + subMap);
		} else if ((parentRuleIndex.equals((Integer) SQLSelectParserParser.RULE_search_condition))
				|| (parentRuleIndex.equals((Integer) SQLSelectParserParser.RULE_parenthesized_value_expression))
				|| (parentRuleIndex.equals((Integer) SQLSelectParserParser.RULE_searched_when_clause))
				|| (parentRuleIndex.equals((Integer) SQLSelectParserParser.RULE_condition_value))) {
			Integer stackLevel = walker.currentStackLevel(ruleIndex);
			Map<String, Object> subMap = walker.getNodeMap(ruleIndex, stackLevel);
			subMap = (Map<String, Object>) subMap.get("1");
			// Get first item, record if it is a Substitution Variable by
			// adding the Substitution List - This captures when the entire
			// condition is a Substitution Variable alone
			subMap = walker.checkForSubstitutionVariable((Map<String, Object>) subMap, MUMBLE_CONDITION_KEY);

			// NOW handle the child
			walker.handleOneChild(ruleIndex);
		} else if ((parentRuleIndex.equals((Integer) SQLSelectParserParser.RULE_case_expression))
				|| (parentRuleIndex.equals((Integer) SQLSelectParserParser.RULE_when_value_clause))
				|| (parentRuleIndex.equals((Integer) SQLSelectParserParser.RULE_case_result))) {
			Integer stackLevel = walker.currentStackLevel(ruleIndex);
			Map<String, Object> subMap = walker.getNodeMap(ruleIndex, stackLevel);
			subMap = (Map<String, Object>) subMap.get("1");
			// Get first item, record if it is a Substitution Variable by
			// adding the Substitution List - This captures when the entire
			// condition is a Substitution Variable alone
			subMap = walker.checkForSubstitutionVariable((Map<String, Object>) subMap, "predicand");

			// NOW handle the child
			walker.handleOneChild(ruleIndex);
		} else if ((parentRuleIndex.equals((Integer) SQLSelectParserParser.RULE_aggregate_function))
				|| (parentRuleIndex.equals((Integer) SQLSelectParserParser.RULE_trim_operands))) {
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
		Integer parentRuleIndex = (Integer) ctx.getParent().getRuleIndex();
		if (parentRuleIndex.equals((Integer) SQLSelectParserParser.RULE_in_value_list)) {
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
		Object type = subMap.remove(ASTWALKER_RULE_TYPE_KEY);
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
				type = hold.remove(ASTWALKER_RULE_TYPE_KEY).toString();
				item.put(MUMBLE_NULL_ORDER_KEY, ((HashMap<String, Object>) hold.get(type)).get("1"));
				walker.showTrace(walker.parseTrace, "Three entries: " + item);

			} else { // Item 2 is not null and Item 3 is null :- Item 2 could be ASC/DESC or Nulls command
				if (item2 instanceof Map<?,?>) {
					// item2 is the null order value
					item.put(MUMBLE_SORT_ORDER_KEY, "ASC");
					Map<String, Object> hold = (Map<String, Object>) item2;
					type = hold.remove(ASTWALKER_RULE_TYPE_KEY).toString();
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
		Integer parentRuleIndex = (Integer) ctx.getParent().getRuleIndex();
		if (parentRuleIndex.equals((Integer) SQLSelectParserParser.RULE_grouping_element))
			walker.handleOneChild(ruleIndex);
		else if (parentRuleIndex.equals((Integer) SQLSelectParserParser.RULE_ordinary_grouping_set_list))
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
		subMap.remove(ASTWALKER_RULE_TYPE_KEY);
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
		subMap.remove(ASTWALKER_RULE_TYPE_KEY);
		
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
