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
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

import static mumble.MumbleConstants.*;
import static mumble.ASTWalkerHelperConstants.*;
import static mumble.SQLParserEndPoints.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.CharStream;

import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.misc.Interval;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.antlr.v4.runtime.tree.TerminalNodeImpl;

import access.Snippet;
import astwalkers.SqlASTWalkerHelper;
import errorhandling.ParseDiagnostic;

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
	private static final String TEMP_INSERT_SOURCE_SELECT_SEQUENCE_KEY = "_tmp_insert_source_select_sequence";

	private static final class IntoSetPlacementViolation {
		private final String setOperationType;
		private final int memberPosition;
		private final Token token;

		private IntoSetPlacementViolation(String setOperationType, int memberPosition, Token token) {
			this.setOperationType = setOperationType;
			this.memberPosition = memberPosition;
			this.token = token;
		}
	}



	/**
	 * AST Walker Helper for this instance of the SQL Parse Event Walker
	 */
	private final SqlASTWalkerHelper walker;
	private final Set<String> invalidVariableDiagnosticKeys;


	// Constructors
	public SqlParseEventWalker() {
		super();

		// Initialize the walker with the SqlASTWalkerHelper
		this.walker = new SqlASTWalkerHelper();
		this.invalidVariableDiagnosticKeys = new HashSet<String>();


	}

	private void emitInvalidVariableDiagnostic(Token startToken, String tokenText) {
		Integer line = startToken == null ? null : startToken.getLine();
		Integer charPos = startToken == null ? null : startToken.getCharPositionInLine();

		String key = String.valueOf(line)
				+ ":"
				+ String.valueOf(charPos)
				+ ":"
				+ String.valueOf(tokenText);
		if (invalidVariableDiagnosticKeys.contains(key)) {
			return;
		}
		invalidVariableDiagnosticKeys.add(key);

		String variableName = tokenText == null ? "<unknown>" : tokenText;
		String message = String.format(
				"Format of Variable Name is unrecognized %s as one of the supported variable identifier forms at (l:%s c:%s).",
				variableName,
				String.valueOf(line),
				String.valueOf(charPos));

		walker.addWalkerFatal("INVALID VARIABLE NAME", message, line, charPos, tokenText);
	}

	private IntoSetPlacementViolation findIntoSetPlacementViolation(
			SQLSelectParserParser.Query_specificationContext ctx) {
		ParserRuleContext child = ctx;
		ParserRuleContext parent = ctx.getParent();

		while (parent != null) {
			if (parent instanceof SQLSelectParserParser.Unionized_queryContext unionizedCtx
					&& child instanceof SQLSelectParserParser.Query_primaryContext queryPrimaryCtx) {
				int memberPosition = unionizedCtx.query_primary().indexOf(queryPrimaryCtx) + 1;
				if (memberPosition > 1) {
					return new IntoSetPlacementViolation("UNION", memberPosition, ctx.getStart());
				}
			}

			if (parent instanceof SQLSelectParserParser.Intersected_queryContext intersectedCtx
					&& child instanceof SQLSelectParserParser.Unionized_queryContext unionizedChildCtx) {
				int memberPosition = intersectedCtx.unionized_query().indexOf(unionizedChildCtx) + 1;
				if (memberPosition > 1) {
					return new IntoSetPlacementViolation("INTERSECTION", memberPosition, ctx.getStart());
				}
			}

			child = parent;
			parent = parent.getParent();
		}

		return null;
	}

	private boolean shouldProjectSelectIntoForQuerySpecification(SQLSelectParserParser.Query_specificationContext ctx) {
		if (ctx.into_list() == null) {
			return true;
		}

		IntoSetPlacementViolation violation = findIntoSetPlacementViolation(ctx);
		if (violation == null) {
			return true;
		}

		String diagCode = walker.getDiagnosticCode(SqlASTWalkerHelper.DIAG_SQL_INTO_ONLY_ALLOWED_ON_FIRST_SET_MEMBER);
		String diagMessageTemplate = walker.getDiagnosticMessage(
				SqlASTWalkerHelper.DIAG_SQL_INTO_ONLY_ALLOWED_ON_FIRST_SET_MEMBER);
		String diagMessage = String.format(
				diagMessageTemplate,
				violation.setOperationType,
				String.valueOf(violation.memberPosition));

		Integer line = violation.token == null ? null : violation.token.getLine();
		Integer charPos = violation.token == null ? null : violation.token.getCharPositionInLine();
		walker.addWalkerFatal(diagCode, diagMessage, line, charPos, "INTO");
		return false;
	}

	private String recoverVariableNameFromToken(Token startToken) {
		if (startToken == null) {
			return null;
		}

		String tokenText = startToken.getText();
		if (tokenText == null || !tokenText.startsWith("<")) {
			return tokenText;
		}

		CharStream input = startToken.getInputStream();
		if (input == null) {
			return tokenText;
		}

		int startIndex = startToken.getStartIndex();
		if (startIndex < 0) {
			return tokenText;
		}

		String fromStart = input.getText(Interval.of(startIndex, input.size() - 1));
		if (fromStart == null || fromStart.isBlank() || fromStart.charAt(0) != '<') {
			return tokenText;
		}

		StringBuilder recovered = new StringBuilder();
		int nesting = 0;
		for (int i = 0; i < fromStart.length(); i++) {
			char ch = fromStart.charAt(i);
			recovered.append(ch);
			if (ch == '<') {
				nesting++;
			} else if (ch == '>') {
				nesting--;
				if (nesting <= 0) {
					break;
				}
			}
		}

		String recoveredName = recovered.toString();
		return recoveredName.isBlank() ? tokenText : recoveredName;
	}

	@SuppressWarnings("unchecked")
	private Map<String, Object> emitInvalidVariableDiagnosticAndSynthesizeIfNeeded(
			SQLSelectParserParser.Variable_identifierContext ctx,
			Map<String, Object> subMap) {
		if (ctx == null) {
			return subMap;
		}

		Token startToken = ctx.getStart();
		Integer line = startToken == null ? null : startToken.getLine();
		Integer charPos = startToken == null ? null : startToken.getCharPositionInLine();
		String tokenText = recoverVariableNameFromToken(startToken);

		Object candidate = null;
		if (subMap != null) {
			candidate = subMap.get("1");
		}

		boolean malformedVariableIdentifier = subMap == null
				|| subMap.isEmpty()
				|| (subMap.size() == 1 && subMap.containsKey(ASTWALKER_RULE_TYPE_KEY))
				|| candidate == null;

		if (!malformedVariableIdentifier) {
			return subMap;
		}

		if (subMap == null) {
			Integer stackLevel = walker.currentStackLevel(ctx.getRuleIndex());
			subMap = walker.makeRuleMap(ctx.getRuleIndex());
			walker.collect(ctx.getRuleIndex(), stackLevel, subMap);
		}

		if (!subMap.containsKey("1")) {
			Map<String, Object> substitution = new HashMap<String, Object>();
			substitution.put(MUMBLE_NAME_KEY, tokenText == null ? ctx.getText() : tokenText);

			Map<String, Object> syntheticVariable = new HashMap<String, Object>();
			syntheticVariable.put(MUMBLE_SUBSTITUTION_KEY, substitution);
			subMap.put("1", syntheticVariable);
		}

		emitInvalidVariableDiagnostic(startToken, tokenText);
		return subMap;
	}

	private String normalizeJinjaReferenceKey(String jinjaReference) {
		if (jinjaReference == null) {
			return null;
		}
		// Normalize whitespace so equivalent templates map to one dictionary key,
		// but preserve single spaces so the key reads as the original template text.
		return jinjaReference.trim().replaceAll("\\s+", " ");
	}

	@SuppressWarnings("unchecked")
	private String resolveSubstitutionTableReference(Map<String, Object> substitution) {
		if (substitution == null) {
			return null;
		}

		Object nameObj = substitution.get(MUMBLE_NAME_KEY);
		String substitutionName = nameObj == null ? null : nameObj.toString();

		Object partsObj = substitution.get(MUMBLE_PARTS_KEY);
		if (partsObj instanceof Map<?, ?>) {
			Map<String, Object> parts = (Map<String, Object>) partsObj;
			if (parts.containsKey(MUMBLE_JINJA_TABLE_KEY)
					|| parts.containsKey(MUMBLE_JINJA_VARIABLE_KEY)) {
				return normalizeJinjaReferenceKey(substitutionName);
			}
		}

		return substitutionName;
	}

	@SuppressWarnings("unchecked")
	private Map<String, Object> asLiteralMap(Object valueObj) {
		if (valueObj instanceof Map<?, ?>) {
			Map<String, Object> mapValue = (Map<String, Object>) valueObj;
			if (mapValue.containsKey(MUMBLE_LITERAL_KEY)) {
				return mapValue;
			}
			if (mapValue.size() == 1 && mapValue.containsKey("1")) {
				Object nested = mapValue.get("1");
				HashMap<String, Object> literal = new HashMap<String, Object>();
				literal.put(MUMBLE_LITERAL_KEY, nested);
				return literal;
			}
		}

		HashMap<String, Object> literal = new HashMap<String, Object>();
		literal.put(MUMBLE_LITERAL_KEY, valueObj);
		return literal;
	}

	private Map<String, Object> getOrCreateRuleMap(int ruleIndex, Integer stackLevel) {
		Map<String, Object> subMap = walker.getNodeMap(ruleIndex, stackLevel);
		if (subMap == null) {
			subMap = walker.collectNewRuleMap(ruleIndex, stackLevel);
		}
		return subMap;
	}

	// Getters and Setters

	public HashMap<String, Object> getAsTree() {
		return walker.asTree;
	}

	public HashMap<String, Object> getTableColumnDictionaryMap() {
		HashMap<String, Object> walkerTableDictionary = walker.getWalkerTableDictionary();
		if (!isTopLevelUpdateTree()) {
			return walkerTableDictionary;
		}

		Object symbolTableDictionaryObj = walker.symbolTable.get(MUMBLE_TABLE_DICTIONARY_KEY);
		if (!(symbolTableDictionaryObj instanceof Map<?, ?> symbolTableDictionary)) {
			return walkerTableDictionary;
		}

		Object nestedSymbolTableDictionaryObj = ((Map<String, Object>) symbolTableDictionary).get(MUMBLE_TABLE_DICTIONARY_KEY);
		Map<String, Object> sourceTableDictionary = (nestedSymbolTableDictionaryObj instanceof Map<?, ?> nested)
				? (Map<String, Object>) nested
				: (Map<String, Object>) symbolTableDictionary;

		for (Map.Entry<String, Object> entry : sourceTableDictionary.entrySet()) {
			String tableRef = entry.getKey();
			if (tableRef == null || MUMBLE_TABLE_ALIAS_KEY.equals(tableRef) || MUMBLE_TABLE_DICTIONARY_KEY.equals(tableRef)) {
				continue;
			}

			Object sourceColumnsObj = entry.getValue();
			if (!(sourceColumnsObj instanceof Map<?, ?> sourceColumns) || sourceColumns.isEmpty()) {
				continue;
			}

			String normalizedTableRef = normalizeTableRef(tableRef);
			Object existingColumnsObj = walkerTableDictionary.get(normalizedTableRef);
			HashMap<String, Object> mergedColumns;
			if (existingColumnsObj instanceof HashMap<?, ?> existingColumnsMapObj) {
				mergedColumns = (HashMap<String, Object>) existingColumnsMapObj;
			} else {
				mergedColumns = new HashMap<String, Object>();
				walkerTableDictionary.put(normalizedTableRef, mergedColumns);
			}

			for (Map.Entry<?, ?> columnEntry : sourceColumns.entrySet()) {
				if (columnEntry.getKey() instanceof String columnName) {
					if (!mergedColumns.containsKey(columnName)) {
						Object normalizedColumnRefs = normalizeUpdateColumnRefs(columnEntry.getValue());
						if (normalizedColumnRefs != null) {
							mergedColumns.put(columnName, normalizedColumnRefs);
						}
					}
				}
			}
		}

		return walkerTableDictionary;
	}

	@SuppressWarnings("unchecked")
	private boolean isTopLevelUpdateTree() {
		Object sqlTreeObj = walker.asTree.get(SQLPARSER_SQL_TREE_KEY);
		if (!(sqlTreeObj instanceof Map<?, ?> sqlTreeMapObj)) {
			return false;
		}

		Map<String, Object> sqlTree = (Map<String, Object>) sqlTreeMapObj;
		for (String key : sqlTree.keySet()) {
			if (key != null && key.startsWith(MUMBLE_UPDATE_KEY)) {
				return true;
			}
		}

		return false;
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
			for (String key : walker.symbolTable.keySet()) {
				if (!(key.startsWith(MUMBLE_UNION_KEY) || key.startsWith(MUMBLE_INTERSECT_KEY))) {
					continue;
				}
				Object scopedObject = walker.symbolTable.get(key);
				if (!(scopedObject instanceof HashMap<?, ?>)) {
					continue;
				}
				String numericSuffix = key.replaceFirst("^[^0-9]+", "");
				int scopeIndex;
				try {
					scopeIndex = Integer.parseInt(numericSuffix);
				} catch (NumberFormatException ex) {
					continue;
				}
				if (scopeIndex > topQueryIndex) {
					topQueryIndex = scopeIndex;
					queryMap = (Map<String, Object>) scopedObject;
				}
			}
		}

		if (queryMap == null) {
			for (String key : walker.symbolTable.keySet()) {
				if (key == null || !key.startsWith(MUMBLE_UPDATE_KEY)) {
					continue;
				}
				Object scopedObject = walker.symbolTable.get(key);
				if (!(scopedObject instanceof HashMap<?, ?>)) {
					continue;
				}
				String numericSuffix = key.replaceFirst("^[^0-9]+", "");
				int scopeIndex;
				try {
					scopeIndex = Integer.parseInt(numericSuffix);
				} catch (NumberFormatException ex) {
					continue;
				}
				if (scopeIndex > topQueryIndex) {
					topQueryIndex = scopeIndex;
					queryMap = (Map<String, Object>) scopedObject;
				}
			}
		}

		if (queryMap == null) {
			for (String key : walker.symbolTable.keySet()) {
				if (key == null || !key.startsWith(MUMBLE_INSERT_KEY)) {
					continue;
				}
				Object scopedObject = walker.symbolTable.get(key);
				if (!(scopedObject instanceof HashMap<?, ?>)) {
					continue;
				}
				String numericSuffix = key.replaceFirst("^[^0-9]+", "");
				int scopeIndex;
				try {
					scopeIndex = Integer.parseInt(numericSuffix);
				} catch (NumberFormatException ex) {
					continue;
				}
				if (scopeIndex > topQueryIndex) {
					topQueryIndex = scopeIndex;
					queryMap = (Map<String, Object>) scopedObject;
				}
			}
		}

		if (queryMap == null) {
			for (String key : walker.symbolTable.keySet()) {
				if (key == null) {
					continue;
				}

				String normalizedValuesKey = null;
				if (key.startsWith(MUMBLE_VALUES_KEY)) {
					normalizedValuesKey = key;
				} else if (key.startsWith("def_" + MUMBLE_VALUES_KEY)) {
					normalizedValuesKey = key.substring("def_".length());
				}

				if (normalizedValuesKey == null) {
					continue;
				}

				Object scopedObject = walker.symbolTable.get(key);
				if (!(scopedObject instanceof HashMap<?, ?>)) {
					continue;
				}

				String numericSuffix = normalizedValuesKey.substring(MUMBLE_VALUES_KEY.length());
				int scopeIndex;
				try {
					scopeIndex = Integer.parseInt(numericSuffix);
				} catch (NumberFormatException ex) {
					continue;
				}

				if (scopeIndex > topQueryIndex) {
					topQueryIndex = scopeIndex;
					queryMap = (Map<String, Object>) scopedObject;
				}
			}
		}

		if (queryMap == null) {
			return interfac;
		}

		Object interfaceObject = queryMap.get(MUMBLE_INTERFACE_KEY);
		if (interfaceObject == null) {
			Object assignmentsObject = queryMap.get(MUMBLE_ASSIGNMENTS_KEY);
			if (assignmentsObject instanceof Map<?, ?> assignmentsMap) {
				for (Object assignmentKeyObj : assignmentsMap.keySet()) {
					if (assignmentKeyObj instanceof String assignmentKey) {
						interfac.add(assignmentKey);
					}
				}
				return interfac;
			}
		}
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

	private String getQualifiedTableReference(Map<String, Object> tableNode) {
		if (tableNode == null) {
			return null;
		}

		Object tableObj = tableNode.get(MUMBLE_TABLE_KEY);
		if (!(tableObj instanceof String tableName) || tableName.isBlank()) {
			return null;
		}

		// Keep tuple substitutions and already-qualified names unchanged.
		if (tableName.startsWith("<") || tableName.contains(".")) {
			return tableName;
		}

		Object dbNameObj = tableNode.get(MUMBLE_DATABASE_NAME_KEY);
		String dbName = (dbNameObj instanceof String db && !db.isBlank()) ? db : null;

		Object schemaObj = tableNode.get(MUMBLE_SCHEMA_KEY);
		String schemaName = (schemaObj instanceof String schema && !schema.isBlank()) ? schema : null;

		if (dbName != null && schemaName != null) {
			return dbName + "." + schemaName + "." + tableName;
		}
		if (schemaName != null) {
			return schemaName + "." + tableName;
		}
		if (dbName != null) {
			return dbName + "." + tableName;
		}

		return tableName;
	}

	/**
	 * Normalizes a table reference string for use as a dictionary key.
	 * Substitution variables (starting with '<') are returned unchanged.
	 * For dotted qualified names, each dot-separated segment is lowercased only if
	 * it is NOT a double-quoted identifier — preserving case sensitivity for
	 * Snowflake-style quoted names like "PROD-uuid".schema."667_table".
	 */
	static String normalizeTableRef(String tableRef) {
		return SqlASTWalkerHelper.normalizeTableReference(tableRef);
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
		HashMap<String, Object> interfaceMap = walker.resolveSetOperationInterfaceMapFromSymbolTable();
		if (interfaceMap != null) {
			walker.validateSetOperationInterface(interfaceMap, ctx.getStart().toString());
		}
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

		rehomeUpdateUnqualifiedUnknownsToSingleFromTable(unresolvedMap);

		HashMap<String, Object> qualifiedUnresolved = new HashMap<String, Object>();
		HashMap<String, Object> unqualifiedUnresolved = new HashMap<String, Object>();
		splitUnresolvedEntriesByQualification(unresolvedMap, qualifiedUnresolved, unqualifiedUnresolved);

		emitUnqualifiedUnresolvedColumnsError(unqualifiedUnresolved);
		emitQualifiedSourceNotFoundFatals(qualifiedUnresolved);
	}

	@SuppressWarnings("unchecked")
	private void rehomeUpdateUnqualifiedUnknownsToSingleFromTable(HashMap<String, Object> unresolvedMap) {
		if (unresolvedMap == null || unresolvedMap.isEmpty()) {
			return;
		}

		Object sqlTreeObj = walker.asTree.get(SQLPARSER_SQL_TREE_KEY);
		if (!(sqlTreeObj instanceof Map<?, ?> sqlTree)) {
			return;
		}

		Map<String, Object> updateAst = (Map<String, Object>) sqlTree;
		boolean isUpdateAst = false;
		for (Map.Entry<?, ?> entry : sqlTree.entrySet()) {
			if (entry.getKey() instanceof String key
					&& key.startsWith(MUMBLE_UPDATE_KEY)) {
				isUpdateAst = true;
				break;
			}
		}
		if (!isUpdateAst) {
			return;
		}

		String fromTableRef = getSingleUpdateFromTableReference(updateAst);
		if (fromTableRef == null || fromTableRef.isBlank()) {
			Map<String, Object> updateNode = getUpdateNode(updateAst);
			fromTableRef = getUpdateTargetTableReference(updateNode);
		}
		if (fromTableRef == null || fromTableRef.isBlank()) {
			return;
		}
		String normalizedFromTableRef = normalizeTableRef(fromTableRef);

		HashMap<String, Object> currentTableDictionary = walker.getCurrentTableDictionary();
		HashMap<String, Object> fromTableDictionary = ensureTableDictionaryEntry(currentTableDictionary, normalizedFromTableRef);

		HashMap<String, Object> nestedFromTableDictionary = null;
		Object nestedTableDictionaryObj = currentTableDictionary.get(MUMBLE_TABLE_DICTIONARY_KEY);
		if (nestedTableDictionaryObj instanceof HashMap<?, ?> nestedTableDictionaryMapObj) {
			nestedFromTableDictionary = ensureTableDictionaryEntry((Map<String, Object>) nestedTableDictionaryMapObj, normalizedFromTableRef);
		}

		HashMap<String, Object> walkerTableDictionary = walker.getWalkerTableDictionary();
		HashMap<String, Object> globalFromTableDictionary = ensureTableDictionaryEntry(walkerTableDictionary, normalizedFromTableRef);

		ArrayList<String> resolvedKeys = new ArrayList<String>();
		for (Map.Entry<String, Object> unresolvedEntry : unresolvedMap.entrySet()) {
			String unresolvedKey = unresolvedEntry.getKey();
			if (unresolvedKey == null || unresolvedKey.contains(".")) {
				continue;
			}
			Object unresolvedValue = normalizeUpdateColumnRefs(unresolvedEntry.getValue());
			if (unresolvedValue == null) {
				continue;
			}
			fromTableDictionary.put(unresolvedKey, unresolvedValue);
			globalFromTableDictionary.put(unresolvedKey, unresolvedValue);
			if (nestedFromTableDictionary != null) {
				nestedFromTableDictionary.put(unresolvedKey, unresolvedValue);
			}
			resolvedKeys.add(unresolvedKey);
		}

		for (String resolvedKey : resolvedKeys) {
			unresolvedMap.remove(resolvedKey);
		}
	}

	@SuppressWarnings("unchecked")
	private HashMap<String, Object> ensureTableDictionaryEntry(Map<String, Object> dictionary, String tableRef) {
		if (dictionary == null || tableRef == null) {
			return new HashMap<String, Object>();
		}

		Object tableEntryObj = dictionary.get(tableRef);
		if (tableEntryObj instanceof HashMap<?, ?> tableEntryMapObj) {
			return (HashMap<String, Object>) tableEntryMapObj;
		}

		HashMap<String, Object> tableEntry = new HashMap<String, Object>();
		dictionary.put(tableRef, tableEntry);
		return tableEntry;
	}

	@SuppressWarnings("unchecked")
	private Object normalizeUpdateColumnRefs(Object value) {
		if (value instanceof Map<?, ?> valueMapObj) {
			Object locations = ((Map<String, Object>) valueMapObj).get("locations");
			return locations;
		}
		return value;
	}

	private void splitUnresolvedEntriesByQualification(
			HashMap<String, Object> unresolvedMap,
			HashMap<String, Object> qualified,
			HashMap<String, Object> unqualified) {
		if (qualified != null) {
			qualified.clear();
		}
		if (unqualified != null) {
			unqualified.clear();
		}

		if (unresolvedMap == null || unresolvedMap.isEmpty()) {
			return;
		}

		for (Map.Entry<String, Object> unresolvedEntry : unresolvedMap.entrySet()) {
			String key = unresolvedEntry.getKey();
			Object value = unresolvedEntry.getValue();
			boolean isQualified = key != null && key.contains(".");
			if (isQualified) {
				if (qualified != null) {
					qualified.put(key, value);
				}
			} else if (unqualified != null) {
				unqualified.put(key, value);
			}
		}
	}

	private void emitUnqualifiedUnresolvedColumnsError(HashMap<String, Object> unqualifiedUnresolvedMap) {
		if (unqualifiedUnresolvedMap == null || unqualifiedUnresolvedMap.isEmpty()) {
			return;
		}

		Integer[] firstTokenLocation = walker.getFirstEntryLineAndCharacter(unqualifiedUnresolvedMap);
		String unknownColumnsWithLocations = walker.formatColumnEntriesWithLocations(unqualifiedUnresolvedMap);
		String unknownColumnsCsv = walker.formatEntryKeysAsCsv(unqualifiedUnresolvedMap);
		String diagCode = walker.getDiagnosticCode(SqlASTWalkerHelper.DIAG_SQL_UNRESOLVED_UNQUALIFIED_COLUMNS);
		String diagTemplate = walker.getDiagnosticMessage(SqlASTWalkerHelper.DIAG_SQL_UNRESOLVED_UNQUALIFIED_COLUMNS);
		String diagMessage = (diagTemplate == null)
				? "Unresolved unqualified column reference(s): " + unknownColumnsWithLocations
				: String.format(diagTemplate, unknownColumnsWithLocations);

		walker.addWalkerDiagnostic(
				ParseDiagnostic.Severity.ERROR,
				diagCode,
				diagMessage,
				firstTokenLocation[0],
				firstTokenLocation[1],
				walker.getClass().getSimpleName(),
				null,
				unknownColumnsCsv,
				false,
				"ast-walk",
				null,
				null);
	}

	private void emitQualifiedUnresolvedColumnsFatal(HashMap<String, Object> qualifiedUnresolvedMap) {
		if (qualifiedUnresolvedMap == null || qualifiedUnresolvedMap.isEmpty()) {
			return;
		}

		Integer[] firstTokenLocation = walker.getFirstEntryLineAndCharacter(qualifiedUnresolvedMap);
		String unknownColumnsWithLocations = walker.formatColumnEntriesWithLocations(qualifiedUnresolvedMap);
		String unknownColumnsCsv = walker.formatEntryKeysAsCsv(qualifiedUnresolvedMap);
		String diagCode = walker.getDiagnosticCode(SqlASTWalkerHelper.DIAG_SQL_UNRESOLVED_QUALIFIED_COLUMNS);
		String diagTemplate = walker.getDiagnosticMessage(SqlASTWalkerHelper.DIAG_SQL_UNRESOLVED_QUALIFIED_COLUMNS);
		String diagMessage = (diagTemplate == null)
				? "Unresolved qualified column reference(s): " + unknownColumnsWithLocations
				: String.format(diagTemplate, unknownColumnsWithLocations);

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

		// Add query to parent symbol table. Use the alias and current query number and put that in the table_alias submap.
		// Then take the current query's symbol table and put that in the parent symbol table with a "def_" prefix.
		// The body of a WITH query may be a SELECT (query), UPDATE, INSERT, UNION, INTERSECT, or VALUES statement,
		// each of which registers in the symbol table under a different prefix. Search all known prefixes.
		int scopeIndex = walker.queryCount - 1;
		String[] knownPrefixes = new String[] {
				MUMBLE_VALUES_KEY, MUMBLE_INTERSECT_KEY, MUMBLE_UNION_KEY,
				MUMBLE_INSERT_KEY, MUMBLE_UPDATE_KEY, MUMBLE_QUERY_KEY
		};
		String queryName = MUMBLE_QUERY_KEY + scopeIndex;
		for (String prefix : knownPrefixes) {
			String candidate = prefix + scopeIndex;
			if (walker.symbolTable.containsKey(candidate)) {
				queryName = candidate;
				break;
			}
		}

		// get current query symbol table and put it in parent symbol table with "def_" prefix
		HashMap<String, Object> currentQuerySymbolTable = (HashMap<String, Object>) walker.symbolTable.remove(queryName);
		if (currentQuerySymbolTable == null) {
			currentQuerySymbolTable = new HashMap<String, Object>();
		}
		HashMap<String, Object> symbols = walker.symbolTable;
		walker.symbolTable = new HashMap<String, Object>();
		walker.symbolTable.put(queryName, currentQuerySymbolTable);
		currentQuerySymbolTable.putAll(symbols);

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

		// Preserve list semantics for WITH items: {1={alias=..., item=...}, 2=...}
		int withIndex = 1;
		int expectedChildren = subMap.size();
		for (int i = 1; i <= expectedChildren; i++) {
			Object child = subMap.remove(String.valueOf(i));
			if (child != null) {
				newMap.put(String.valueOf(withIndex++), child);
			}
		}
		if (!subMap.isEmpty()) {
			String[] keys = subMap.keySet().toArray(new String[0]);
			for (String key : keys) {
				Object child = subMap.remove(key);
				if (child != null) {
					newMap.put(String.valueOf(withIndex++), child);
				}
			}
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
		if (subMap == null) {
			walker.showTrace(walker.parseTrace, "TABLE PRIMARY missing recovery map: " + ctx.getText());
			walker.addToParent(parentRuleIndex, parentStackLevel, new HashMap<String, Object>());
			return;
		}
		Object type = subMap.remove(ASTWALKER_RULE_TYPE_KEY);
		Map<String, Object> item;
		String alias = null;

		if (subMap.size() == 2) {

			alias = (String) subMap.remove("1");

			Map<String, Object> aliasMap = (Map<String, Object>) subMap.remove("2");
		    walker.checkForSubstitutionVariable((Map<String, Object>) aliasMap, "tuple");
			boolean tupleWithSubstitution = isTupleWithSubstitution(aliasMap);

			Map<String, Object> withItem = new HashMap<String, Object>();
			withItem.put(MUMBLE_ALIAS_KEY, alias);
			withItem.put(MUMBLE_CTE_KEY, aliasMap);
			subMap.clear();
			subMap.putAll(withItem);

			// Add query to parent symbol table. Use the alias and current query number and put that in the table_alias submap.
			// Then take the current query's symbol table and put that in the parent symbol table with a "def_" prefix. 
			String currentQueryName;
			if (tupleWithSubstitution) {
				currentQueryName = MUMBLE_QUERY_KEY + walker.queryCount;
				walker.queryCount++;
			} else {
				currentQueryName = resolveCurrentWithListItemScope(aliasMap);
			}
			// HashMap<String, Object> currentQuerySymbolTable = (HashMap<String, Object>) walker.symbolTable.remove(currentQueryName);
			// Pop the symbol table for this level and add it to the parent level with a unique key.
			walker.collectTableAlias(alias, currentQueryName);

					
			Boolean done = collectQuerySymbolTable(MUMBLE_QUERY_KEY, alias);
			if (!done)
					done = collectQuerySymbolTable(MUMBLE_INSERT_KEY, alias);
			if (!done)
					done = collectQuerySymbolTable(MUMBLE_UPDATE_KEY, alias);
			if (!done)
					done = collectQuerySymbolTable(MUMBLE_UNION_KEY, alias);
			if (!done)
					done = collectQuerySymbolTable(MUMBLE_INTERSECT_KEY, alias);
			if (!done)
					done = collectQuerySymbolTable(MUMBLE_VALUES_KEY, alias);

		} else {
			walker.showTrace(walker.parseTrace, "Wrong number of entries: " + ctx.getText());
		}
		walker.addToParent(parentRuleIndex, parentStackLevel, subMap);
		walker.showTrace(walker.parseTrace, "WITH QUERY: " + subMap);
	}

	@SuppressWarnings("unchecked")
	private boolean isTupleWithSubstitution(Map<String, Object> aliasMap) {
		if (aliasMap == null) {
			return false;
		}
		Object substitutionObject = aliasMap.get(MUMBLE_SUBSTITUTION_KEY);
		if (!(substitutionObject instanceof Map<?, ?> substitutionMap)) {
			return false;
		}
		Object typeObject = substitutionMap.get(MUMBLE_TYPE_KEY);
		return "tuple".equals(typeObject);
	}

	@SuppressWarnings("unchecked")
	private String resolveCurrentWithListItemScope(Map<String, Object> aliasMap) {
		int scopeIndex = walker.queryCount - 1;

		String[] orderedPrefixes = new String[] {
				MUMBLE_VALUES_KEY,
				MUMBLE_INTERSECT_KEY,
				MUMBLE_UNION_KEY,
				MUMBLE_INSERT_KEY,
				MUMBLE_UPDATE_KEY,
				MUMBLE_QUERY_KEY
		};

		for (String prefix : orderedPrefixes) {
			if (!containsKeyRecursive(aliasMap, prefix)) {
				continue;
			}
			String candidate = prefix + scopeIndex;
			if (walker.symbolTable.containsKey(candidate)) {
				return candidate;
			}
		}

		for (String prefix : orderedPrefixes) {
			String candidate = prefix + scopeIndex;
			if (walker.symbolTable.containsKey(candidate)) {
				return candidate;
			}
		}

		return MUMBLE_QUERY_KEY + nextSyntheticWithQueryAliasIndex();
	}

	@SuppressWarnings("unchecked")
	private int nextSyntheticWithQueryAliasIndex() {
		int maxIndex = -1;
		Object aliasObject = walker.symbolTable.get(MUMBLE_TABLE_ALIAS_KEY);
		if (aliasObject instanceof Map<?, ?> aliasMap) {
			for (Object value : aliasMap.values()) {
				if (!(value instanceof String aliasTarget)) {
					continue;
				}
				if (!aliasTarget.startsWith(MUMBLE_QUERY_KEY)) {
					continue;
				}
				String suffix = aliasTarget.substring(MUMBLE_QUERY_KEY.length());
				int index;
				try {
					index = Integer.parseInt(suffix);
				} catch (NumberFormatException ex) {
					continue;
				}
				if (index > maxIndex) {
					maxIndex = index;
				}
			}
		}
		return maxIndex + 1;
	}

	@SuppressWarnings("unchecked")
	private boolean containsKeyRecursive(Map<String, Object> map, String expectedKey) {
		if (map == null) {
			return false;
		}
		if (map.containsKey(expectedKey)) {
			return true;
		}
		for (Object value : map.values()) {
			if (value instanceof Map<?, ?> nestedMap
					&& containsKeyRecursive((Map<String, Object>) nestedMap, expectedKey)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void exitQuery_alias(@NotNull SQLSelectParserParser.Query_aliasContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		walker.handleOneChild(ruleIndex);
	}

	@Override
	public void exitCte_body(@NotNull SQLSelectParserParser.Cte_bodyContext ctx) {
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
	public void enterInsert_expression(@NotNull SQLSelectParserParser.Insert_expressionContext ctx) {
		walker.pushSymbolTable();
	}

	@Override
	public void exitInsert_expression(@NotNull SQLSelectParserParser.Insert_expressionContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		int parentRuleIndex = ctx.getParent().getRuleIndex();

		Map<String, Object> subMap = walker.getNodeMap(ruleIndex, stackLevel);
		walker.checkForSubstitutionVariable((Map<String, Object>) subMap.get("1"), "query");

		walker.handleOneChild(ruleIndex);
		finalizeTopLevelUnresolvedColumns();

		// Close the insert-local symbol scope and persist it as insertN in the parent scope.
		String insertScopeKey = MUMBLE_INSERT_KEY + walker.queryCount;
		walker.popSymbolTable(insertScopeKey, walker.symbolTable);
		mergeInsertScopeTableDictionaryIntoGlobal(insertScopeKey);
		publishInsertScopeQueryDictionary(insertScopeKey);
		walker.queryCount++;

		walker.showTrace(walker.parseTrace, "INSERT EXPRESSION: " + subMap);
	}

	@Override
	public void exitSnowflake_insert(@NotNull SQLSelectParserParser.Snowflake_insertContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Map<String, Object> subMap = walker.removeNodeMap(ruleIndex, stackLevel);
		subMap.remove(ASTWALKER_RULE_TYPE_KEY);

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

		String insertSourceScopeKey = findLatestInsertSourceScopeKey();
		Map<String, Object> insertSourceDefinition = normalizeInsertSourceDefinition(insertSourceScopeKey);
		Map<String, Object> insertColumns = (Map<String, Object>) subMap.get(MUMBLE_COLUMNS_KEY);
		String insertTargetTableRef = getInsertTargetTableReference(subMap);
		populateInsertTargetColumnsFromColumnReferenceList(insertTargetTableRef, insertColumns);
		Map<String, Object> insertInterface = buildInsertInterfaceFromSource(
				insertSourceDefinition,
				insertColumns,
				insertSourceScopeKey);
		if (!insertInterface.isEmpty()) {
			walker.symbolTable.put(MUMBLE_INTERFACE_KEY, insertInterface);
		}

		populateImplicitInsertTargetColumnsFromSourceDictionary(
				insertTargetTableRef,
				insertColumns,
				insertSourceDefinition,
				insertInterface);

		resolveInsertUnqualifiedOrphanSourceColumnsToTargetTable(
				insertTargetTableRef,
				insertSourceDefinition,
				insertInterface);

		int parentRuleIndex = ctx.getParent().getRuleIndex();
		Integer parentStackLevel = walker.currentStackLevel(parentRuleIndex);
		walker.addToParent(parentRuleIndex, parentStackLevel, subMap);
		
	}

	@SuppressWarnings("unchecked")
	private void resolveInsertUnqualifiedOrphanSourceColumnsToTargetTable(
			String insertTargetTableRef,
			Map<String, Object> insertSourceDefinition,
			Map<String, Object> insertInterface) {
		if (insertTargetTableRef == null || insertTargetTableRef.isBlank()) {
			return;
		}
		if (insertSourceDefinition == null || insertSourceDefinition.isEmpty()) {
			return;
		}
		if (insertInterface == null || insertInterface.isEmpty()) {
			return;
		}

		HashMap<String, Object> currentTableDictionary = walker.getCurrentTableDictionary();
		HashMap<String, Object> insertTargetDictionary = ensureTableDictionaryEntry(currentTableDictionary, insertTargetTableRef);

		for (Object interfaceEntryObj : insertInterface.values()) {
			if (!(interfaceEntryObj instanceof List<?> interfaceRefsObj)) {
				continue;
			}

			for (Object interfaceRefObj : interfaceRefsObj) {
				String sourceColumnName = walker.extractReferenceNameFromInterfaceEntry(interfaceRefObj);
				String sourceScopeRef = walker.extractReferenceTableRefFromInterfaceEntry(interfaceRefObj);

				if (sourceColumnName == null || sourceColumnName.isBlank() || "*".equals(sourceColumnName)) {
					continue;
				}
				if (sourceScopeRef == null || sourceScopeRef.isBlank()) {
					continue;
				}
				if (containsKeyIgnoreCase(insertTargetDictionary, sourceColumnName)) {
					continue;
				}

				Map<String, Object> sourceScopeDefinition = normalizeInsertSourceDefinition(sourceScopeRef);
				if ((sourceScopeDefinition == null || sourceScopeDefinition.isEmpty()) && sourceScopeRef != null) {
					Object queryDefObj = getQueryDefinitionSymbol(sourceScopeRef);
					if (queryDefObj instanceof Map<?, ?> queryDefMapObj) {
						sourceScopeDefinition = (Map<String, Object>) queryDefMapObj;
					}
				}
				if (sourceScopeDefinition == null || sourceScopeDefinition.isEmpty()) {
					continue;
				}

				Object sourceScopeQueryDictionaryObj = sourceScopeDefinition.get(MUMBLE_QUERY_DICTIONARY_KEY);
				if (!(sourceScopeQueryDictionaryObj instanceof Map<?, ?> sourceScopeQueryDictionaryMapObj)) {
					continue;
				}

				Map<String, Object> sourceScopeQueryDictionary = (Map<String, Object>) sourceScopeQueryDictionaryMapObj;
				Object sourceScopeInterfaceObj = sourceScopeDefinition.get(MUMBLE_INTERFACE_KEY);
				if (!(sourceScopeInterfaceObj instanceof Map<?, ?> sourceScopeInterfaceMapObj)) {
					continue;
				}
				Map<String, Object> sourceScopeInterface = (Map<String, Object>) sourceScopeInterfaceMapObj;
				Object sourceInterfaceRefsObj = sourceScopeInterface.get(sourceColumnName);
				if (!(sourceInterfaceRefsObj instanceof List<?> sourceInterfaceRefs)) {
					continue;
				}

				boolean hasNullTableRef = false;
				for (Object sourceInterfaceRefObj : sourceInterfaceRefs) {
					String sourceInterfaceTableRef = walker.extractReferenceTableRefFromInterfaceEntry(sourceInterfaceRefObj);
					if (sourceInterfaceTableRef == null || sourceInterfaceTableRef.isBlank()) {
						hasNullTableRef = true;
						break;
					}
				}
				if (!hasNullTableRef) {
					continue;
				}

				Object sourceRefsObj = sourceScopeQueryDictionary.get(sourceColumnName);
				if (!(sourceRefsObj instanceof ArrayList<?> sourceRefsListObj) || sourceRefsListObj.isEmpty()) {
					continue;
				}

				insertTargetDictionary.put(sourceColumnName, new ArrayList<Object>((ArrayList<Object>) sourceRefsListObj));
				removeUnresolvedColumnEntry(sourceColumnName);
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void removeUnresolvedColumnEntry(String columnName) {
		if (columnName == null || columnName.isBlank()) {
			return;
		}

		Object unresolvedObject = walker.symbolTable.get(MUMBLE_UNRESOLVED_COLUMN_KEY);
		if (!(unresolvedObject instanceof HashMap<?, ?> unresolvedMapObj)) {
			return;
		}

		HashMap<String, Object> unresolvedMap = (HashMap<String, Object>) unresolvedMapObj;
		unresolvedMap.remove(columnName);
		if (unresolvedMap.isEmpty()) {
			walker.symbolTable.remove(MUMBLE_UNRESOLVED_COLUMN_KEY);
		}
	}

	@SuppressWarnings("unchecked")
	private void populateInsertTargetColumnsFromColumnReferenceList(
			String insertTargetTableRef,
			Map<String, Object> insertColumns) {
		if (insertColumns == null || insertColumns.isEmpty()) {
			return;
		}
		if (insertTargetTableRef == null || insertTargetTableRef.isBlank()) {
			return;
		}

		HashMap<String, Object> currentTableDictionary = walker.getCurrentTableDictionary();
		HashMap<String, Object> insertTargetDictionary = ensureTableDictionaryEntry(currentTableDictionary, insertTargetTableRef);

		Object queryDictionaryObj = walker.symbolTable.get(MUMBLE_QUERY_DICTIONARY_KEY);
		Map<String, Object> queryDictionary = (queryDictionaryObj instanceof Map<?, ?> queryDictionaryMapObj)
				? (Map<String, Object>) queryDictionaryMapObj
				: new HashMap<String, Object>();

		ArrayList<String> insertColumnNames = extractInsertColumnNames(insertColumns);
		for (String insertColumnName : insertColumnNames) {
			if (insertColumnName == null || insertColumnName.isBlank()) {
				continue;
			}

			Object refsObj = queryDictionary.get(insertColumnName);
			Object copiedRefs = (refsObj instanceof ArrayList<?> refsListObj)
					? new ArrayList<Object>((ArrayList<Object>) refsListObj)
					: new ArrayList<Object>();
			Object existingRefs = insertTargetDictionary.get(insertColumnName);
			if (existingRefs == null) {
				insertTargetDictionary.put(insertColumnName, copiedRefs);
			} else {
				insertTargetDictionary.put(insertColumnName, mergeReferenceCollections(existingRefs, copiedRefs));
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void mergeInsertScopeTableDictionaryIntoGlobal(String insertScopeKey) {
		if (insertScopeKey == null || insertScopeKey.isBlank()) {
			return;
		}

		Object insertScopeObj = walker.symbolTable.get(insertScopeKey);
		if (!(insertScopeObj instanceof Map<?, ?> insertScopeMapObj)) {
			return;
		}

		Map<String, Object> insertScopeMap = (Map<String, Object>) insertScopeMapObj;
		Object localTableDictionaryObj = insertScopeMap.get(MUMBLE_TABLE_DICTIONARY_KEY);
		if (!(localTableDictionaryObj instanceof Map<?, ?> localTableDictionaryMapObj)) {
			return;
		}

		Map<String, Object> localTableDictionary = (Map<String, Object>) localTableDictionaryMapObj;
		if (!hasAnyColumnsInTableDictionary(localTableDictionary)) {
			return;
		}

		HashMap<String, Object> globalTableDictionary = walker.getWalkerTableDictionary();
		for (Map.Entry<String, Object> tableEntry : localTableDictionary.entrySet()) {
			String tableRef = tableEntry.getKey();
			if (tableRef == null
					|| MUMBLE_TABLE_ALIAS_KEY.equals(tableRef)
					|| MUMBLE_TABLE_DICTIONARY_KEY.equals(tableRef)) {
				continue;
			}
			if (!(tableEntry.getValue() instanceof Map<?, ?> sourceColumnsMapObj)) {
				continue;
			}

			String normalizedTableRef = normalizeTableRef(tableRef);
			HashMap<String, Object> targetColumns = ensureTableDictionaryEntry(globalTableDictionary, normalizedTableRef);
			Map<String, Object> sourceColumns = (Map<String, Object>) sourceColumnsMapObj;
			for (Map.Entry<String, Object> columnEntry : sourceColumns.entrySet()) {
				String columnName = columnEntry.getKey();
				if (columnName == null) {
					continue;
				}
				Object existingRefs = targetColumns.get(columnName);
				if (existingRefs == null) {
					targetColumns.put(columnName, columnEntry.getValue());
				} else {
					targetColumns.put(columnName, mergeReferenceCollections(existingRefs, columnEntry.getValue()));
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void publishInsertScopeQueryDictionary(String insertScopeKey) {
		if (insertScopeKey == null || insertScopeKey.isBlank()) {
			return;
		}

		Object insertScopeObj = walker.symbolTable.get(insertScopeKey);
		if (!(insertScopeObj instanceof Map<?, ?> insertScopeMapObj)) {
			return;
		}

		Map<String, Object> insertScopeMap = (Map<String, Object>) insertScopeMapObj;
		Object queryDictionaryObj = insertScopeMap.get(MUMBLE_QUERY_DICTIONARY_KEY);
		HashMap<String, Object> queryDictionary = (queryDictionaryObj instanceof Map<?, ?> queryDictionaryMapObj)
				? new HashMap<String, Object>((Map<String, Object>) queryDictionaryMapObj)
				: new HashMap<String, Object>();

		HashMap<String, Object> inferredQueryDictionary =
				buildInsertScopeQueryDictionaryFromTableDictionary(insertScopeMap);
		if (!inferredQueryDictionary.isEmpty()) {
			for (Map.Entry<String, Object> inferredEntry : inferredQueryDictionary.entrySet()) {
				String columnName = inferredEntry.getKey();
				if (columnName == null) {
					continue;
				}
				Object existingRefs = queryDictionary.get(columnName);
				if (existingRefs == null) {
					queryDictionary.put(columnName, inferredEntry.getValue());
				} else {
					queryDictionary.put(columnName, mergeReferenceCollections(existingRefs, inferredEntry.getValue()));
				}
			}
		}

		sanitizeQueryDictionaryForGlobalExport(queryDictionary);
		if (queryDictionary.isEmpty()) {
			return;
		}

		walker.queryColumnDictionaryMap.put(insertScopeKey, queryDictionary);
		insertScopeMap.put(MUMBLE_QUERY_DICTIONARY_KEY, queryDictionary);
	}

	@SuppressWarnings("unchecked")
	private void publishUpdateScopeQueryDictionary(String updateScopeKey) {
		if (updateScopeKey == null || updateScopeKey.isBlank()) {
			return;
		}
		Object updateScopeObj = walker.symbolTable.get(updateScopeKey);
		if (!(updateScopeObj instanceof Map<?, ?> updateScopeMapObj)) {
			return;
		}
		Map<String, Object> updateScopeMap = (Map<String, Object>) updateScopeMapObj;
		Object updateDictionaryObj = updateScopeMap.get(MUMBLE_UPDATE_DICTIONARY_KEY);
		if (!(updateDictionaryObj instanceof Map<?, ?> updateDictionaryMapObj)) {
			return;
		}
		HashMap<String, Object> queryDictionary = new HashMap<String, Object>((Map<String, Object>) updateDictionaryMapObj);
		sanitizeQueryDictionaryForGlobalExport(queryDictionary);
		if (queryDictionary.isEmpty()) {
			return;
		}
		walker.queryColumnDictionaryMap.put(updateScopeKey, queryDictionary);
	}

	@SuppressWarnings("unchecked")
	private HashMap<String, Object> buildInsertScopeQueryDictionaryFromTableDictionary(Map<String, Object> insertScopeMap) {
		HashMap<String, Object> inferred = new HashMap<String, Object>();
		if (insertScopeMap == null || insertScopeMap.isEmpty()) {
			return inferred;
		}

		HashSet<String> allowedInsertOutputColumns = new HashSet<String>();
		Object insertInterfaceObj = insertScopeMap.get(MUMBLE_INTERFACE_KEY);
		if (insertInterfaceObj instanceof Map<?, ?> insertInterfaceMapObj) {
			Map<String, Object> insertInterfaceMap = (Map<String, Object>) insertInterfaceMapObj;
			allowedInsertOutputColumns.addAll(insertInterfaceMap.keySet());
		}

		Object localTableDictionaryObj = insertScopeMap.get(MUMBLE_TABLE_DICTIONARY_KEY);
		if (!(localTableDictionaryObj instanceof Map<?, ?> localTableDictionaryMapObj)) {
			return inferred;
		}

		Map<String, Object> localTableDictionary = (Map<String, Object>) localTableDictionaryMapObj;
		Map<String, Object> targetColumns = null;
		for (Map.Entry<String, Object> tableEntry : localTableDictionary.entrySet()) {
			String tableRef = tableEntry.getKey();
			if (tableRef == null
					|| MUMBLE_TABLE_ALIAS_KEY.equals(tableRef)
					|| MUMBLE_TABLE_DICTIONARY_KEY.equals(tableRef)) {
				continue;
			}
			if (!(tableEntry.getValue() instanceof Map<?, ?> columnMapObj)) {
				continue;
			}

			Map<String, Object> candidateColumns = (Map<String, Object>) columnMapObj;
			if (!candidateColumns.isEmpty()) {
				targetColumns = candidateColumns;
				break;
			}
		}

		if (targetColumns == null || targetColumns.isEmpty()) {
			return inferred;
		}

		for (Map.Entry<String, Object> columnEntry : targetColumns.entrySet()) {
			String columnName = columnEntry.getKey();
			if (columnName == null) {
				continue;
			}
			if (!allowedInsertOutputColumns.isEmpty()
					&& !containsIgnoreCase(allowedInsertOutputColumns, columnName)) {
				continue;
			}
			Object refsObj = columnEntry.getValue();
			if (refsObj instanceof ArrayList<?> refsListObj) {
				inferred.put(columnName, new ArrayList<Object>((ArrayList<Object>) refsListObj));
			}
		}

		return inferred;
	}

	private boolean containsIgnoreCase(Set<String> values, String candidate) {
		if (values == null || values.isEmpty() || candidate == null) {
			return false;
		}
		for (String value : values) {
			if (value != null && value.equalsIgnoreCase(candidate)) {
				return true;
			}
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	private Object mergeReferenceCollections(Object existingRefs, Object incomingRefs) {
		if (existingRefs instanceof ArrayList<?> existingListObj && incomingRefs instanceof ArrayList<?> incomingListObj) {
			ArrayList<Object> merged = new ArrayList<Object>((ArrayList<Object>) existingListObj);
			for (Object incomingRef : incomingListObj) {
				if (!merged.contains(incomingRef)) {
					merged.add(incomingRef);
				}
			}
			return merged;
		}

		if (existingRefs == null) {
			return incomingRefs;
		}

		return existingRefs;
	}

	@SuppressWarnings("unchecked")
	private boolean hasAnyColumnsInTableDictionary(Map<String, Object> tableDictionary) {
		if (tableDictionary == null || tableDictionary.isEmpty()) {
			return false;
		}

		for (Map.Entry<String, Object> tableEntry : tableDictionary.entrySet()) {
			String tableRef = tableEntry.getKey();
			if (tableRef == null
					|| MUMBLE_TABLE_ALIAS_KEY.equals(tableRef)
					|| MUMBLE_TABLE_DICTIONARY_KEY.equals(tableRef)) {
				continue;
			}
			if (!(tableEntry.getValue() instanceof Map<?, ?> columnsMapObj)) {
				continue;
			}
			if (!((Map<String, Object>) columnsMapObj).isEmpty()) {
				return true;
			}
		}

		return false;
	}

	@SuppressWarnings("unchecked")
	private void populateImplicitInsertTargetColumnsFromSourceDictionary(
			String insertTargetTableRef,
			Map<String, Object> insertColumns,
			Map<String, Object> insertSourceDefinition,
			Map<String, Object> insertInterface) {
		if (insertColumns != null && !insertColumns.isEmpty()) {
			return;
		}
		if (insertTargetTableRef == null || insertTargetTableRef.isBlank()) {
			return;
		}
		if (insertSourceDefinition == null || insertSourceDefinition.isEmpty()) {
			return;
		}

		Object sourceQueryDictionaryObj = insertSourceDefinition.get(MUMBLE_QUERY_DICTIONARY_KEY);
		if (!(sourceQueryDictionaryObj instanceof Map<?, ?> sourceQueryDictionaryMapObj)) {
			return;
		}

		HashMap<String, Object> currentTableDictionary = walker.getCurrentTableDictionary();
		HashMap<String, Object> insertTargetDictionary = ensureTableDictionaryEntry(currentTableDictionary, insertTargetTableRef);
		Map<String, Object> sourceQueryDictionary = (Map<String, Object>) sourceQueryDictionaryMapObj;

		for (String inferredColumnName : insertInterface.keySet()) {
			Object sourceRefsObj = sourceQueryDictionary.get(inferredColumnName);
			if (!(sourceRefsObj instanceof ArrayList<?> sourceRefsListObj)) {
				continue;
			}

			ArrayList<Object> copiedRefs = new ArrayList<Object>((ArrayList<Object>) sourceRefsListObj);
			Object existingRefsObj = insertTargetDictionary.get(inferredColumnName);
			if (existingRefsObj == null) {
				insertTargetDictionary.put(inferredColumnName, copiedRefs);
			} else {
				insertTargetDictionary.put(inferredColumnName, mergeReferenceCollections(existingRefsObj, copiedRefs));
			}
		}
	}

	@SuppressWarnings("unchecked")
	private String getInsertTargetTableReference(Map<String, Object> insertNode) {
		if (insertNode == null) {
			return null;
		}

		Object targetTableObj = insertNode.get(MUMBLE_TABLE_KEY);
		if (!(targetTableObj instanceof Map<?, ?> targetTableMapObj)) {
			return null;
		}

		Map<String, Object> targetTableMap = (Map<String, Object>) targetTableMapObj;
		Object nestedTableObj = targetTableMap.get(MUMBLE_TABLE_KEY);
		if (nestedTableObj instanceof Map<?, ?> nestedTableMapObj) {
			String tableRef = getQualifiedTableReference((Map<String, Object>) nestedTableMapObj);
			return (tableRef == null || tableRef.isBlank()) ? null : tableRef;
		}

		String tableRef = getQualifiedTableReference(targetTableMap);
		return (tableRef == null || tableRef.isBlank()) ? null : tableRef;
	}

	private String findLatestInsertSourceScopeKey() {
		String selectedKey = null;
		int highestIndex = -1;

		for (String symbolKey : walker.symbolTable.keySet()) {
			if (symbolKey == null) {
				continue;
			}

			String normalizedSymbolKey = symbolKey;
			if (symbolKey.startsWith("def_")) {
				normalizedSymbolKey = symbolKey.substring("def_".length());
			}

			if (!isInsertSourceScopeReference(normalizedSymbolKey)) {
				continue;
			}

			int prefixLength = getInsertScopePrefixLength(normalizedSymbolKey);
			if (prefixLength < 0) {
				continue;
			}

			String suffix = normalizedSymbolKey.substring(prefixLength);
			if (suffix.isEmpty()) {
				if (selectedKey == null) {
					selectedKey = normalizedSymbolKey;
				}
				continue;
			}

			int keyIndex;
			try {
				keyIndex = Integer.parseInt(suffix);
			} catch (NumberFormatException ex) {
				continue;
			}

			if (keyIndex > highestIndex) {
				highestIndex = keyIndex;
				selectedKey = normalizedSymbolKey;
			}
		}

		return selectedKey;
	}

	private boolean isInsertSourceScopeReference(String sourceRef) {
		if (sourceRef == null || sourceRef.isBlank()) {
			return false;
		}
		return sourceRef.startsWith(MUMBLE_QUERY_KEY)
				|| sourceRef.startsWith(MUMBLE_UNION_KEY)
				|| sourceRef.startsWith(MUMBLE_INTERSECT_KEY)
				|| sourceRef.startsWith(MUMBLE_VALUES_KEY)
				|| sourceRef.startsWith(MUMBLE_INSERT_KEY)
				|| sourceRef.startsWith(MUMBLE_UPDATE_KEY);
	}

	private int getInsertScopePrefixLength(String sourceRef) {
		if (sourceRef.startsWith(MUMBLE_QUERY_KEY)) {
			return MUMBLE_QUERY_KEY.length();
		}
		if (sourceRef.startsWith(MUMBLE_UNION_KEY)) {
			return MUMBLE_UNION_KEY.length();
		}
		if (sourceRef.startsWith(MUMBLE_INTERSECT_KEY)) {
			return MUMBLE_INTERSECT_KEY.length();
		}
		if (sourceRef.startsWith(MUMBLE_VALUES_KEY)) {
			return MUMBLE_VALUES_KEY.length();
		}
		if (sourceRef.startsWith(MUMBLE_INSERT_KEY)) {
			return MUMBLE_INSERT_KEY.length();
		}
		if (sourceRef.startsWith(MUMBLE_UPDATE_KEY)) {
			return MUMBLE_UPDATE_KEY.length();
		}
		return -1;
	}

	@SuppressWarnings("unchecked")
	private Map<String, Object> normalizeInsertSourceDefinition(String sourceScopeKey) {
		if (sourceScopeKey == null || sourceScopeKey.isBlank()) {
			return new HashMap<String, Object>();
		}

		String definitionKey = sourceScopeKey.startsWith("def_") ? sourceScopeKey : "def_" + sourceScopeKey;
		Object sourceDefinitionObj = walker.symbolTable.get(definitionKey);

		if (!(sourceDefinitionObj instanceof Map<?, ?>)) {
			Object directScopeObj = walker.symbolTable.remove(sourceScopeKey);
			if (directScopeObj instanceof Map<?, ?>) {
				sourceDefinitionObj = directScopeObj;
				walker.symbolTable.put(definitionKey, sourceDefinitionObj);
			}
		} else if (isInsertSourceScopeReference(sourceScopeKey)) {
			// Avoid leaving a dangling empty queryN/valuesN reference alongside def_queryN/def_valuesN.
			walker.symbolTable.remove(sourceScopeKey);
		}

		if (sourceDefinitionObj instanceof Map<?, ?> sourceDefinitionMapObj) {
			return (Map<String, Object>) sourceDefinitionMapObj;
		}

		return new HashMap<String, Object>();
	}

	@SuppressWarnings("unchecked")
	private Map<String, Object> buildInsertInterfaceFromSource(
			Map<String, Object> sourceDefinition,
			Map<String, Object> insertColumns,
			String sourceScopeKey) {
		Map<String, Object> insertInterface = new LinkedHashMap<String, Object>();
		ArrayList<String> insertColumnNames = extractInsertColumnNames(insertColumns);
		if (sourceDefinition == null || sourceDefinition.isEmpty()) {
			for (String insertColumnName : insertColumnNames) {
				insertInterface.put(insertColumnName, new ArrayList<Object>());
			}
			return insertInterface;
		}

		Object sourceInterfaceObj = sourceDefinition.get(MUMBLE_INTERFACE_KEY);
		if (!(sourceInterfaceObj instanceof Map<?, ?> sourceInterfaceObjMap)) {
			for (String insertColumnName : insertColumnNames) {
				insertInterface.put(insertColumnName, new ArrayList<Object>());
			}
			return insertInterface;
		}

		Map<String, Object> sourceInterface = (Map<String, Object>) sourceInterfaceObjMap;
		if (sourceInterface.isEmpty()) {
			for (String insertColumnName : insertColumnNames) {
				insertInterface.put(insertColumnName, new ArrayList<Object>());
			}
			return insertInterface;
		}

		if (insertColumns == null || insertColumns.isEmpty()) {
			Map<String, Object> clonedSourceInterface = new HashMap<String, Object>(sourceInterface);
			for (String childColumnName : clonedSourceInterface.keySet()) {
				insertInterface.put(childColumnName, buildSingleInsertInterfaceReference(childColumnName, sourceScopeKey));
			}
			return insertInterface;
		}

		ArrayList<String> sourceColumnNames = resolveInsertSourceColumnSequence(sourceDefinition, sourceInterface);
		for (int i = 0; i < insertColumnNames.size(); i++) {
			String insertColumnName = insertColumnNames.get(i);
			String sourceColumnName = (i < sourceColumnNames.size())
					? sourceColumnNames.get(i)
					: insertColumnName;
			insertInterface.put(insertColumnName, buildSingleInsertInterfaceReference(sourceColumnName, sourceScopeKey));
		}

		return insertInterface;
	}

	@SuppressWarnings("unchecked")
	private ArrayList<String> resolveInsertSourceColumnSequence(
			Map<String, Object> sourceDefinition,
			Map<String, Object> sourceInterface) {
		ArrayList<String> sourceColumnNames = new ArrayList<String>();
		if (sourceDefinition != null) {
			Object sequenceObj = sourceDefinition.remove(TEMP_INSERT_SOURCE_SELECT_SEQUENCE_KEY);
			if (sequenceObj instanceof ArrayList<?> sequenceList) {
				for (Object sequenceItem : sequenceList) {
					if (!(sequenceItem instanceof String columnName)) {
						continue;
					}
					if (!sourceInterface.containsKey(columnName) || sourceColumnNames.contains(columnName)) {
						continue;
					}
					sourceColumnNames.add(columnName);
				}
			}
		}

		for (String interfaceKey : sourceInterface.keySet()) {
			if (!sourceColumnNames.contains(interfaceKey)) {
				sourceColumnNames.add(interfaceKey);
			}
		}

		return sourceColumnNames;
	}

	private ArrayList<Object> buildSingleInsertInterfaceReference(String sourceColumnName, String sourceScopeKey) {
		ArrayList<Object> refs = new ArrayList<Object>();
		HashMap<String, Object> ref = new HashMap<String, Object>();
		ref.put(MUMBLE_NAME_KEY, sourceColumnName);
		ref.put(MUMBLE_TABLE_REF_KEY, sourceScopeKey);
		refs.add(ref);
		return refs;
	}

	@SuppressWarnings("unchecked")
	private ArrayList<String> extractInsertColumnNames(Map<String, Object> insertColumns) {
		ArrayList<String> columnNames = new ArrayList<String>();
		if (insertColumns == null || insertColumns.isEmpty()) {
			return columnNames;
		}

		for (int i = 1; i <= insertColumns.size(); i++) {
			Object columnEntryObj = insertColumns.get(String.valueOf(i));
			String columnName = extractInsertColumnNameFromEntry(columnEntryObj);
			if (columnName != null && !columnName.isBlank()) {
				columnNames.add(columnName);
			}
		}

		if (!columnNames.isEmpty()) {
			return columnNames;
		}

		for (Object columnEntryObj : insertColumns.values()) {
			String columnName = extractInsertColumnNameFromEntry(columnEntryObj);
			if (columnName != null && !columnName.isBlank()) {
				columnNames.add(columnName);
			}
		}

		return columnNames;
	}

	@SuppressWarnings("unchecked")
	private String extractInsertColumnNameFromEntry(Object columnEntryObj) {
		if (!(columnEntryObj instanceof Map<?, ?> columnEntryMapObj)) {
			return null;
		}

		Map<String, Object> columnEntryMap = (Map<String, Object>) columnEntryMapObj;
		Object columnObj = columnEntryMap.get(MUMBLE_COLUMN_KEY);
		if (columnObj instanceof Map<?, ?> columnMapObj) {
			Object nameObj = ((Map<String, Object>) columnMapObj).get(MUMBLE_NAME_KEY);
			if (nameObj != null) {
				return nameObj.toString();
			}
		}

		Object nameObj = columnEntryMap.get(MUMBLE_NAME_KEY);
		if (nameObj != null) {
			return nameObj.toString();
		}

		for (Object nestedValueObj : columnEntryMap.values()) {
			if (!(nestedValueObj instanceof Map<?, ?> nestedMapObj)) {
				continue;
			}
			Object nestedNameObj = ((Map<String, Object>) nestedMapObj).get(MUMBLE_NAME_KEY);
			if (nestedNameObj != null) {
				return nestedNameObj.toString();
			}
		}

		return null;
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
	public void enterUpdate_expression(@NotNull SQLSelectParserParser.Update_expressionContext ctx) {
		walker.pushSymbolTable();
	}

	@Override
	public void exitUpdate_expression(@NotNull SQLSelectParserParser.Update_expressionContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Map<String, Object> subMap = walker.getNodeMap(ruleIndex, stackLevel);
		String[] keys = new String[1];
		Object type = subMap.remove(ASTWALKER_RULE_TYPE_KEY);
		Map<String, Object> updateNode = new HashMap<String, Object>();

		keys = subMap.keySet().toArray(keys);

		for (String key : keys) {
			Object obj = subMap.remove(key);
			if (obj instanceof String) {

			} else {
				HashMap<String, Object> value = (HashMap<String, Object>) obj;
				Integer childKey = (Integer) (value).remove(ASTWALKER_RULE_TYPE_KEY);
				if (childKey == null) {
					updateNode.putAll(value);
				} else {
					Object segment = value.remove(childKey.toString());
					if (childKey.equals((Integer) SQLSelectParserParser.RULE_assignment_expression_list)) {
						updateNode.put(MUMBLE_ASSIGNMENTS_KEY, segment);
					} else if (childKey.equals((Integer) SQLSelectParserParser.RULE_from_clause)) {
						if (((HashMap<String, Object>) segment).size() == 1) {
							updateNode.put(MUMBLE_FROM_KEY, ((HashMap<String, Object>) segment).remove("1"));
						} else
							updateNode.put(MUMBLE_FROM_KEY, segment);
					} else if (childKey.equals((Integer) SQLSelectParserParser.RULE_where_clause)) {
						HashMap<String, Object> item = (HashMap<String, Object>) segment;
						item = (HashMap<String, Object>) item.remove("1");
						updateNode.put(MUMBLE_WHERE_KEY, item);
					} else if (childKey.equals((Integer) SQLSelectParserParser.RULE_returning)) {
						updateNode.put(MUMBLE_RETURNING_KEY, segment);
					} else {
						walker.showTrace(walker.parseTrace, "Too Many Entries" + segment);
					}
				}
			}
		}
		subMap.clear();
		subMap.put(MUMBLE_UPDATE_KEY, updateNode);
		walker.showTrace(walker.parseTrace, subMap);

		String updateTargetTableRef = getUpdateTargetTableReference(updateNode);
		initializeUpdateTargetTableSubtree(updateTargetTableRef);
		convertSymbolTableToTableDictionary(false, false, updateTargetTableRef);

		// Keep update scope numbering in the symbol table for parity with query-like scopes.
		String updateScopeKey = MUMBLE_UPDATE_KEY + walker.queryCount;
		walker.popSymbolTable(updateScopeKey, walker.symbolTable);
		publishUpdateScopeQueryDictionary(updateScopeKey);
		walker.queryCount++;
	}

	@SuppressWarnings("unchecked")
	private String getUpdateTargetTableReference(Map<String, Object> updateNode) {
		if (updateNode == null) {
			return null;
		}

		Object targetTableObj = updateNode.get(MUMBLE_TABLE_KEY);
		if (targetTableObj instanceof Map<?, ?> targetTableMapObj) {
			String tableRef = getQualifiedTableReference((Map<String, Object>) targetTableMapObj);
			return (tableRef == null || tableRef.isBlank()) ? null : tableRef;
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	private void initializeUpdateTargetTableSubtree(String updateTargetTableRef) {
		if (updateTargetTableRef == null || updateTargetTableRef.isBlank()) {
			return;
		}

		Object targetTableObj = walker.symbolTable.get(MUMBLE_TARGET_TABLE_KEY);
		HashMap<String, Object> targetTableMap;
		if (targetTableObj instanceof HashMap<?, ?>) {
			targetTableMap = (HashMap<String, Object>) targetTableObj;
		} else {
			targetTableMap = new HashMap<String, Object>();
			walker.symbolTable.put(MUMBLE_TARGET_TABLE_KEY, targetTableMap);
		}

		String normalizedTargetRef = normalizeTableRef(updateTargetTableRef);
		targetTableMap.putIfAbsent(normalizedTargetRef, new HashMap<String, Object>());
	}

	@SuppressWarnings("unchecked")
	private String getSingleUpdateFromTableReference(Map<String, Object> updateAst) {
		if (updateAst == null) {
			return null;
		}

		Map<String, Object> updateNode = getUpdateNode(updateAst);
		if (updateNode == null) {
			return null;
		}

		Object fromObj = updateNode.get(MUMBLE_FROM_KEY);
		if (!(fromObj instanceof Map<?, ?> fromMapObj)) {
			return null;
		}

		Map<String, Object> fromMap = (Map<String, Object>) fromMapObj;
		Object tableObj = fromMap.get(MUMBLE_TABLE_KEY);
		if (tableObj instanceof Map<?, ?> tableMapObj) {
			String tableRef = getQualifiedTableReference((Map<String, Object>) tableMapObj);
			return (tableRef == null || tableRef.isBlank()) ? null : tableRef;
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	private Map<String, Object> getUpdateNode(Map<String, Object> updateAst) {
		if (updateAst == null) {
			return null;
		}

		Object updateObj = updateAst.get(MUMBLE_UPDATE_KEY);
		if (updateObj instanceof Map<?, ?> updateMapObj) {
			return (Map<String, Object>) updateMapObj;
		}

		return updateAst;
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

			String assignmentKey = extractAssignmentLhsName(left);
			if (assignmentKey != null && !assignmentKey.isBlank()) {
				addUpdateAssignmentSymbolReference(assignmentKey, right, resolveAssignmentLhsTokenString(ctx));
				moveAssignmentLhsToLhsUnresolvedColumns(left);
			}

			walker.showTrace(walker.parseTrace, "Assignment: " + subMap);
		} else {
			walker.showTrace(walker.parseTrace, "Wrong number of entries: " + subMap);
		}
		walker.addToParent(parentRuleIndex, parentStackLevel, subMap);

	}

	@SuppressWarnings("unchecked")
	private void moveAssignmentLhsToLhsUnresolvedColumns(Map<String, Object> leftAssignment) {
		Map<String, Object> lhsColumnReference = extractAssignmentLhsColumnReference(leftAssignment);
		if (lhsColumnReference == null || lhsColumnReference.isEmpty()) {
			return;
		}

		String lhsQualifiedKey = makeQualifiedColumnReferenceKey(lhsColumnReference);
		String lhsName = (lhsColumnReference.get(MUMBLE_NAME_KEY) == null)
				? null
				: lhsColumnReference.get(MUMBLE_NAME_KEY).toString();

		Object unresolvedObject = walker.symbolTable.get(MUMBLE_UNRESOLVED_COLUMN_KEY);
		HashMap<String, Object> unresolvedMap;
		if (unresolvedObject instanceof HashMap<?, ?>) {
			unresolvedMap = (HashMap<String, Object>) unresolvedObject;
		} else {
			unresolvedMap = new HashMap<String, Object>();
			walker.symbolTable.put(MUMBLE_UNRESOLVED_COLUMN_KEY, unresolvedMap);
		}

		Object lhsEntry = null;
		if (lhsQualifiedKey != null) {
			lhsEntry = unresolvedMap.remove(lhsQualifiedKey);
		}
		if (lhsEntry == null && lhsName != null) {
			lhsEntry = unresolvedMap.remove(lhsName);
		}
		if (lhsEntry == null) {
			lhsEntry = new HashMap<String, Object>(lhsColumnReference);
		}

		Object lhsUnresolvedObject = walker.symbolTable.get(MUMBLE_LHS_UNRESOLVED_COLUMNS_KEY);
		HashMap<String, Object> lhsUnresolvedColumns;
		if (lhsUnresolvedObject instanceof HashMap<?, ?>) {
			lhsUnresolvedColumns = (HashMap<String, Object>) lhsUnresolvedObject;
		} else {
			lhsUnresolvedColumns = new HashMap<String, Object>();
			walker.symbolTable.put(MUMBLE_LHS_UNRESOLVED_COLUMNS_KEY, lhsUnresolvedColumns);
		}

		String lhsStorageKey = (lhsQualifiedKey == null || lhsQualifiedKey.isBlank()) ? lhsName : lhsQualifiedKey;
		if (lhsStorageKey != null && !lhsStorageKey.isBlank()) {
			lhsUnresolvedColumns.put(lhsStorageKey, lhsEntry);
		}
	}

	@SuppressWarnings("unchecked")
	private Map<String, Object> extractAssignmentLhsColumnReference(Map<String, Object> leftAssignment) {
		if (leftAssignment == null) {
			return null;
		}

		Object columnObject = leftAssignment.get(MUMBLE_COLUMN_KEY);
		if (columnObject instanceof Map<?, ?> columnMapObj) {
			return new HashMap<String, Object>((Map<String, Object>) columnMapObj);
		}

		Object substitutionObject = leftAssignment.get(MUMBLE_SUBSTITUTION_KEY);
		if (substitutionObject instanceof Map<?, ?> substitutionMapObj) {
			return new HashMap<String, Object>((Map<String, Object>) substitutionMapObj);
		}

		return null;
	}

	private String makeQualifiedColumnReferenceKey(Map<String, Object> columnReference) {
		if (columnReference == null) {
			return null;
		}

		Object nameObject = columnReference.get(MUMBLE_NAME_KEY);
		if (!(nameObject instanceof String columnName) || columnName.isBlank()) {
			return null;
		}

		Object tableRefObject = columnReference.get(MUMBLE_TABLE_REF_KEY);
		if (tableRefObject instanceof String tableRef && !tableRef.isBlank()) {
			return tableRef + "." + columnName;
		}

		return columnName;
	}

	@SuppressWarnings("unchecked")
	private void addUpdateAssignmentSymbolReference(
			String assignmentKey,
			Map<String, Object> assignmentValue,
			String lhsTokenString) {
		if (assignmentKey == null || assignmentKey.isBlank()) {
			return;
		}

		Object assignmentsObject = walker.symbolTable.get(MUMBLE_ASSIGNMENTS_KEY);
		HashMap<String, Object> assignmentsMap;
		if (assignmentsObject instanceof HashMap<?, ?>) {
			assignmentsMap = (HashMap<String, Object>) assignmentsObject;
		} else {
			assignmentsMap = new HashMap<String, Object>();
			walker.symbolTable.put(MUMBLE_ASSIGNMENTS_KEY, assignmentsMap);
		}

		ArrayList<Object> rhsColumnReferences = new ArrayList<Object>();
		if (assignmentValue != null && !assignmentValue.isEmpty()) {
			HashMap<String, Object> flattenTarget = new HashMap<String, Object>();
			flattenTarget.putAll(assignmentValue);
			flattenSubTreeForInterfaceColumns(flattenTarget, rhsColumnReferences);
		}
		assignmentsMap.put(assignmentKey, rhsColumnReferences);

		Object dictionaryObject = walker.symbolTable.get(MUMBLE_UPDATE_DICTIONARY_KEY);
		HashMap<String, Object> updateDictionary;
		if (dictionaryObject instanceof HashMap<?, ?>) {
			updateDictionary = (HashMap<String, Object>) dictionaryObject;
		} else {
			updateDictionary = new HashMap<String, Object>();
			walker.symbolTable.put(MUMBLE_UPDATE_DICTIONARY_KEY, updateDictionary);
		}

		ArrayList<String> lhsTokenList = new ArrayList<String>();
		if (lhsTokenString != null && !lhsTokenString.isBlank()) {
			lhsTokenList.add(lhsTokenString);
		}
		updateDictionary.put(assignmentKey, lhsTokenList);
	}

	@SuppressWarnings("unchecked")
	private String extractAssignmentLhsName(Map<String, Object> leftAssignment) {
		if (leftAssignment == null) {
			return null;
		}

		Object columnObject = leftAssignment.get(MUMBLE_COLUMN_KEY);
		if (columnObject instanceof Map<?, ?> columnMapObj) {
			Object nameObject = ((Map<String, Object>) columnMapObj).get(MUMBLE_NAME_KEY);
			if (nameObject != null) {
				return nameObject.toString();
			}
		}

		Object substitutionObject = leftAssignment.get(MUMBLE_SUBSTITUTION_KEY);
		if (substitutionObject instanceof Map<?, ?> substitutionMapObj) {
			Object nameObject = ((Map<String, Object>) substitutionMapObj).get(MUMBLE_NAME_KEY);
			if (nameObject != null) {
				return nameObject.toString();
			}
		}

		return null;
	}

	private String resolveAssignmentLhsTokenString(SQLSelectParserParser.Assignment_expressionContext ctx) {
		if (ctx == null || ctx.getChildCount() == 0) {
			Token startToken = (ctx == null) ? null : ctx.getStart();
			return (startToken == null) ? null : startToken.toString();
		}

		if (ctx.getChild(0) instanceof ParserRuleContext leftContext) {
			Token lhsToken = leftContext.getStop();
			if (lhsToken != null) {
				return lhsToken.toString();
			}
		}

		Token startToken = ctx.getStart();
		return (startToken == null) ? null : startToken.toString();
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
			finalizeValuesScopeSymbolTable();
	
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
				walker.symbolTable.put(MUMBLE_INTERFACE_KEY, buildValuesOutputInterface(hold));
				
				// Resolve Symbol Table, add alias from Values statement to the Symbol Table.
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
			// Build values symbols from current scope structures so VALUES uses the same
			// query-dictionary-driven lifecycle as query sources.
			Map<String, Object> valueColumns = resolveCurrentValuesColumns();
			walker.symbolTable.put(MUMBLE_VALUES_KEY, valueColumns);
			Map<String, Object> hold = buildValuesOutputInterface(valueColumns);
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
				HashMap<String, Object> valueColumn = new HashMap<String, Object>();
				valueColumn.put(MUMBLE_NAME_KEY, ref);
				valueColumn.put(MUMBLE_TABLE_REF_KEY, MUMBLE_VALUES_KEY);
				walker.collectUnresolvedColumnReference(MUMBLE_VALUES_KEY, valueColumn, ctx.getStart());
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

		@SuppressWarnings("unchecked")
		private Map<String, Object> resolveCurrentValuesColumns() {
			Object directValuesObj = walker.symbolTable.get(MUMBLE_VALUES_KEY);
			if (directValuesObj instanceof Map<?, ?> directValuesMap) {
				return new HashMap<String, Object>((Map<String, Object>) directValuesMap);
			}

			HashMap<String, Object> derivedValues = new HashMap<String, Object>();
			Object unresolvedObj = walker.symbolTable.get(MUMBLE_UNRESOLVED_COLUMN_KEY);
			if (unresolvedObj instanceof Map<?, ?> unresolvedMapObj) {
				Map<String, Object> unresolvedMap = (Map<String, Object>) unresolvedMapObj;
				for (Map.Entry<String, Object> unresolvedEntry : unresolvedMap.entrySet()) {
					String unresolvedKey = unresolvedEntry.getKey();
					if (unresolvedKey == null || !unresolvedKey.startsWith(MUMBLE_VALUES_KEY + ".")) {
						continue;
					}

					String valueColumnName = unresolvedKey.substring((MUMBLE_VALUES_KEY + ".").length());
					Object valueObject = unresolvedEntry.getValue();
					if (!(valueObject instanceof Map<?, ?> valueMapObj)) {
						continue;
					}

					Map<String, Object> valueMap = (Map<String, Object>) valueMapObj;
					Object locationsObj = valueMap.get("locations");
					if (locationsObj instanceof ArrayList<?>) {
						derivedValues.put(valueColumnName, locationsObj);
					}
				}
			}

			return derivedValues;
		}

		private Map<String, Object> buildValuesOutputInterface(Map<String, Object> valueColumns) {
			Map<String, Object> interfaceMap = new HashMap<String, Object>();
			if (valueColumns == null || valueColumns.isEmpty()) {
				return interfaceMap;
			}

			for (Map.Entry<String, Object> entry : valueColumns.entrySet()) {
				if (entry.getKey() == null || entry.getValue() instanceof String) {
					continue;
				}
				interfaceMap.put(entry.getKey(), new ArrayList<Object>());
			}

			return interfaceMap;
		}

		@SuppressWarnings("unchecked")
		private void finalizeValuesScopeSymbolTable() {
			HashMap<String, Object> symbols =  walker.symbolTable;
			String key = MUMBLE_VALUES_KEY + walker.queryCount;

			// Capture Query Column Dictionary for this level
			HashMap<String, Object> localCurrentQueryDictionary = (HashMap<String, Object>) walker.symbolTable.remove(MUMBLE_QUERY_DICTIONARY_KEY);
			if (localCurrentQueryDictionary == null)
				localCurrentQueryDictionary = new HashMap<String, Object>();

			// Treat VALUES scope like a query definition: column entries belong in query_dictionary.
			Object valuesObj = symbols.remove(MUMBLE_VALUES_KEY);
			if (valuesObj instanceof Map<?, ?> valuesMapObj) {
				Map<String, Object> valuesMap = (Map<String, Object>) valuesMapObj;
				for (Map.Entry<String, Object> valuesEntry : valuesMap.entrySet()) {
					String valuesColumn = valuesEntry.getKey();
					Object valuesRefs = valuesEntry.getValue();
					if (valuesColumn == null || valuesRefs instanceof String) {
						continue;
					}
					localCurrentQueryDictionary.putIfAbsent(valuesColumn, valuesRefs);
				}
			}

			symbols.put(MUMBLE_QUERY_DICTIONARY_KEY, localCurrentQueryDictionary);
			symbols.put(MUMBLE_TABLE_DICTIONARY_KEY, new HashMap<String, Object>());
			walker.queryColumnDictionaryMap.put(key, localCurrentQueryDictionary);
			walker.symbolTable.remove(MUMBLE_SCALAR_SUBQUERY_ALIASES_KEY);

			// Keep VALUES scope payload aligned to expected grouped-source shape.
			symbols.remove(MUMBLE_UNRESOLVED_COLUMN_KEY);
			symbols.remove(MUMBLE_TABLE_ALIAS_KEY);

			// Keep it as valuesN at this stage; def_ wrapping is applied later where tuple/table sources are normalized.
			walker.popSymbolTable(key, symbols);

			walker.queryCount++;
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
						
		@Override
		public void enterInsert_values_statement(@NotNull SQLSelectParserParser.Insert_values_statementContext ctx) {
			walker.pushSymbolTable();
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

			Map<String, Object> valueColumns = resolveCurrentValuesColumns();
			walker.symbolTable.put(MUMBLE_VALUES_KEY, valueColumns);
			Map<String, Object> hold = buildValuesOutputInterface(valueColumns);
			walker.symbolTable.put(MUMBLE_INTERFACE_KEY, hold);

			finalizeValuesScopeSymbolTable();

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

		String location = ctx.getStart().toString();

		// Resolve set-operation output interface from either the current scope or the top set symbol.
		HashMap<String, Object> interfaceMap = walker.resolveSetOperationInterfaceMapFromSymbolTable();
		// Validate set-operation branch interface counts against the top output interface.
		// This is intentionally not gated by union/intersect flags because those flags can be reset
		// after symbol-table scope pop before this method executes.
		if (interfaceMap != null) {
			walker.validateSetOperationInterface(interfaceMap, location);
		}
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
			HashMap<String, Object> interfac = walker.captureQueryInterface();
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

		Object interfaceObj = walker.symbolTable.get(MUMBLE_INTERFACE_KEY);
		HashMap<String, Object> interfaceMap = null;
		if (interfaceObj instanceof HashMap<?, ?>) {
			interfaceMap = (HashMap<String, Object>) interfaceObj;
		}

		if (walker.unionClauseFound && interfaceMap == null) {
			// Defensive fallback: union output interface should mirror first union branch.
			HashMap<String, Object> interfac = walker.captureQueryInterface();
			if (interfac != null) {
				interfaceMap = interfac;
			}
		}

		if (interfaceMap != null) {
			walker.symbolTable.put(MUMBLE_INTERFACE_KEY, interfaceMap);
		}

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
			if (!(obj instanceof Map<?, ?> valueObj)) {
				walker.showTrace(walker.parseTrace,
						"Unexpected query_specification child type for key " + key + ": " + obj);
				continue;
			}

			HashMap<String, Object> value = (HashMap<String, Object>) valueObj;
			Integer childKey = (Integer) (value).remove(ASTWALKER_RULE_TYPE_KEY);
			if (childKey == null) {
				walker.showTrace(walker.parseTrace,
						"Missing child rule type for query_specification clause key " + key + ": " + value);
				continue;
			}

			Object segment = value.remove(childKey.toString());
			if (childKey.equals((Integer) SQLSelectParserParser.RULE_select_list)) {
				subMap.put(MUMBLE_SELECT_KEY, segment);
			} else if (childKey.equals((Integer) SQLSelectParserParser.RULE_set_qualifier)) {
				subMap.putAll(value);
			} else if (childKey.equals((Integer) SQLSelectParserParser.RULE_into_list)) {
				subMap.put(MUMBLE_INTO_KEY, segment);
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
			} else if (childKey.equals((Integer) SQLSelectParserParser.RULE_qualify_clause)) {
				HashMap<String, Object> item = (HashMap<String, Object>) segment;
				item = (HashMap<String, Object>) item.remove("1");
				subMap.put(MUMBLE_QUALIFY_KEY, item);
			} else if (childKey.equals((Integer) SQLSelectParserParser.RULE_orderby_clause)) {
				subMap.put(MUMBLE_ORDERBY_KEY, segment);
			} else if (childKey.equals((Integer) SQLSelectParserParser.RULE_limit_clause)) {
				subMap.put(MUMBLE_LIMIT_KEY, segment);
			} else {
				walker.showTrace(walker.parseTrace, "Too Many Entries" + segment);
			}
		}
		walker.showTrace(walker.parseTrace, subMap);

		Integer symbolScopeLevel = walker.stackSymbols.get("symbolTable");
		boolean hasParentQueryScope = symbolScopeLevel != null && symbolScopeLevel > 2;
		Integer subqueryParentRuleIndex = walker.findNearestSubqueryParentRuleIndex(ctx);
		boolean passUpQualifiedUnresolvedFromThisSubquery =
				walker.shouldPassUpQualifiedUnresolvedForSubqueryParent(subqueryParentRuleIndex);
		boolean emitQualifiedUnresolvedFromThisSubquery =
				walker.shouldEmitQualifiedUnresolvedForSubqueryParent(subqueryParentRuleIndex);
		boolean deferSubqueryUnresolvedDiagnosticsToStatementBoundary =
				shouldDeferSubqueryUnresolvedDiagnosticsToStatementBoundary(ctx);
		boolean emitFinalUnresolvedUnknownFatal = !hasParentQueryScope;
		boolean deferCorrelatedValueSubqueryQualifiedUnknowns = hasParentQueryScope
				&& passUpQualifiedUnresolvedFromThisSubquery;
		// Handle symbol tables		
		HashMap<String, Object> symbols = convertSymbolTableToTableDictionary(
				emitFinalUnresolvedUnknownFatal,
				deferCorrelatedValueSubqueryQualifiedUnknowns,
				null);

		// Retrieve outer symbol table, insert this symbol table into it
		String key = "query" + walker.queryCount;

		// Capture Query Column Dictionary for this level
		HashMap<String, Object> localCurrentQueryDictionary = (HashMap<String, Object>) walker.symbolTable.remove(MUMBLE_QUERY_DICTIONARY_KEY);
		if (localCurrentQueryDictionary == null)
			localCurrentQueryDictionary = new HashMap<String, Object>();

		sanitizeQueryDictionaryForGlobalExport(localCurrentQueryDictionary);
		walker.queryColumnDictionaryMap.put(key, localCurrentQueryDictionary);
		symbols.put(MUMBLE_QUERY_DICTIONARY_KEY, localCurrentQueryDictionary);
		if (shouldProjectSelectIntoForQuerySpecification(ctx)) {
			projectSelectIntoTargetFromInterface(subMap, symbols, localCurrentQueryDictionary);
		}
		walker.symbolTable.remove(MUMBLE_SCALAR_SUBQUERY_ALIASES_KEY);

		// Get the remaining unresolved column references from this query and push them up one level with the query key as a prefix
		HashMap<String, Object> unresolvedMap = (HashMap<String, Object>) walker.symbolTable.remove(MUMBLE_UNRESOLVED_COLUMN_KEY);
		HashMap<String, Object> qualifiedUnresolvedForParent = new HashMap<String, Object>();
		HashMap<String, Object> unqualifiedUnresolvedForLocal = new HashMap<String, Object>();
		if (unresolvedMap != null && !unresolvedMap.isEmpty()) {
			splitUnresolvedEntriesByQualification(unresolvedMap, qualifiedUnresolvedForParent, unqualifiedUnresolvedForLocal);
			if (!deferSubqueryUnresolvedDiagnosticsToStatementBoundary) {
				if (!canResolveUnqualifiedFromSingleWildcardQuerySource(unqualifiedUnresolvedForLocal)) {
					emitUnqualifiedUnresolvedColumnsError(unqualifiedUnresolvedForLocal);
				}
				HashMap<String, Object> tableAliasMap = (HashMap<String, Object>) walker.symbolTable.get(MUMBLE_TABLE_ALIAS_KEY);
				emitQualifiedQueryAliasUnresolvedColumnsFatalAndPrune(
						qualifiedUnresolvedForParent,
						tableAliasMap);
			}
		}
	
		walker.popSymbolTable(key, symbols);
		walker.queryCount++;
		if (!hasParentQueryScope && !deferSubqueryUnresolvedDiagnosticsToStatementBoundary) {
			walker.symbolTable.remove(MUMBLE_UNRESOLVED_COLUMN_KEY);
		}

		if (deferSubqueryUnresolvedDiagnosticsToStatementBoundary) {
			HashMap<String, Object> deferredUnresolvedForParent = new HashMap<String, Object>();
			deferredUnresolvedForParent.putAll(unqualifiedUnresolvedForLocal);
			deferredUnresolvedForParent.putAll(qualifiedUnresolvedForParent);
			mergeUnresolvedEntriesIntoCurrentScope(deferredUnresolvedForParent);
			return;
		}

		if (emitQualifiedUnresolvedFromThisSubquery && !qualifiedUnresolvedForParent.isEmpty()) {
			emitQualifiedUnresolvedColumnsFatal(qualifiedUnresolvedForParent);
		} else if (hasParentQueryScope
				&& passUpQualifiedUnresolvedFromThisSubquery
				&& !qualifiedUnresolvedForParent.isEmpty()) {
			HashMap<String, Object> parentResolvableQualifiedUnknowns =
					partitionParentResolvableQualifiedUnknownsAndEmit(
							qualifiedUnresolvedForParent,
							(HashMap<String, Object>) walker.symbolTable.get(MUMBLE_TABLE_ALIAS_KEY),
							(HashMap<String, Object>) walker.symbolTable.get(MUMBLE_TABLE_DICTIONARY_KEY),
							(HashMap<String, Object>) walker.symbolTable.get(MUMBLE_QUERY_DICTIONARY_KEY));
			if (!parentResolvableQualifiedUnknowns.isEmpty()) {
				Object parentUnresolvedObject = walker.symbolTable.get(MUMBLE_UNRESOLVED_COLUMN_KEY);
				if (parentUnresolvedObject instanceof HashMap<?, ?>) {
					walker.mergeUnknownEntries((HashMap<String, Object>) parentUnresolvedObject, parentResolvableQualifiedUnknowns);
				} else {
					walker.symbolTable.put(MUMBLE_UNRESOLVED_COLUMN_KEY, parentResolvableQualifiedUnknowns);
				}
			}
		} else if (hasParentQueryScope && !qualifiedUnresolvedForParent.isEmpty()) {
			Object parentUnresolvedObject = walker.symbolTable.get(MUMBLE_UNRESOLVED_COLUMN_KEY);
			if (parentUnresolvedObject instanceof HashMap<?, ?>) {
				walker.mergeUnknownEntries((HashMap<String, Object>) parentUnresolvedObject, qualifiedUnresolvedForParent);
			} else {
				walker.symbolTable.put(MUMBLE_UNRESOLVED_COLUMN_KEY, qualifiedUnresolvedForParent);
			}
		} else if (!hasParentQueryScope && !qualifiedUnresolvedForParent.isEmpty()) {
			emitQualifiedSourceNotFoundFatals(qualifiedUnresolvedForParent);
		}

	}

	@SuppressWarnings("unchecked")
	private void mergeUnresolvedEntriesIntoCurrentScope(Map<String, Object> unresolvedEntries) {
		if (unresolvedEntries == null || unresolvedEntries.isEmpty()) {
			return;
		}
		HashMap<String, Object> unresolvedEntryMap = new HashMap<String, Object>(unresolvedEntries);

		Object parentUnresolvedObject = walker.symbolTable.get(MUMBLE_UNRESOLVED_COLUMN_KEY);
		if (parentUnresolvedObject instanceof HashMap<?, ?>) {
			walker.mergeUnknownEntries((HashMap<String, Object>) parentUnresolvedObject, unresolvedEntryMap);
		} else {
			walker.symbolTable.put(MUMBLE_UNRESOLVED_COLUMN_KEY, unresolvedEntryMap);
		}
	}

	private boolean shouldDeferSubqueryUnresolvedDiagnosticsToStatementBoundary(
			SQLSelectParserParser.Query_specificationContext ctx) {
		if (ctx == null) {
			return false;
		}

		ParserRuleContext cursor = ctx;
		while (cursor != null) {
			int ruleIndex = cursor.getRuleIndex();
			if (ruleIndex == SQLSelectParserParser.RULE_insert_expression
					|| ruleIndex == SQLSelectParserParser.RULE_update_expression) {
				return true;
			}
			if (ruleIndex == SQLSelectParserParser.RULE_query_value
					|| ruleIndex == SQLSelectParserParser.RULE_tuple_value
					|| ruleIndex == SQLSelectParserParser.RULE_join_extension_value) {
				return false;
			}
			cursor = cursor.getParent();
		}

		return false;
	}

	@SuppressWarnings("unchecked")
	private void projectSelectIntoTargetFromInterface(
			Map<String, Object> queryAst,
			Map<String, Object> querySymbols,
			Map<String, Object> localCurrentQueryDictionary) {
		if (queryAst == null || querySymbols == null) {
			return;
		}

		Object intoObj = queryAst.get(MUMBLE_INTO_KEY);
		if (!(intoObj instanceof Map<?, ?> intoMapObj)) {
			return;
		}
		Map<String, Object> intoMap = (Map<String, Object>) intoMapObj;
		Map<String, Object> targetTableMap = intoMap;
		Object firstIntoEntry = intoMap.get("1");
		if (firstIntoEntry instanceof Map<?, ?> firstIntoEntryMapObj) {
			targetTableMap = (Map<String, Object>) firstIntoEntryMapObj;
		}

		String targetTableRef = getQualifiedTableReference(targetTableMap);
		if (targetTableRef == null || targetTableRef.isBlank()) {
			return;
		}
		String normalizedTargetRef = normalizeTableRef(targetTableRef);

		Object interfaceObj = querySymbols.get(MUMBLE_INTERFACE_KEY);
		if (!(interfaceObj instanceof Map<?, ?> interfaceMapObj)) {
			return;
		}
		Map<String, Object> interfaceMap = (Map<String, Object>) interfaceMapObj;

		Object tableDictionaryObj = querySymbols.get(MUMBLE_TABLE_DICTIONARY_KEY);
		HashMap<String, Object> tableDictionary = (tableDictionaryObj instanceof HashMap<?, ?> tableDictionaryMapObj)
				? (HashMap<String, Object>) tableDictionaryMapObj
				: new HashMap<String, Object>();
		querySymbols.put(MUMBLE_TABLE_DICTIONARY_KEY, tableDictionary);

		HashMap<String, Object> targetColumns = ensureTableDictionaryEntry(tableDictionary, normalizedTargetRef);
		for (Map.Entry<String, Object> interfaceEntry : interfaceMap.entrySet()) {
			String targetColumn = interfaceEntry.getKey();
			if (targetColumn == null || targetColumn.isBlank()) {
				continue;
			}

			Object refsObj = localCurrentQueryDictionary == null ? null : localCurrentQueryDictionary.get(targetColumn);
			Object refs = (refsObj instanceof ArrayList<?> refsListObj)
					? new ArrayList<Object>((ArrayList<Object>) refsListObj)
					: new ArrayList<Object>();
			targetColumns.put(targetColumn, refs);
		}

		HashMap<String, Object> targetTableCollection;
		Object targetTableObj = querySymbols.get(MUMBLE_TARGET_TABLE_KEY);
		if (targetTableObj instanceof HashMap<?, ?> targetTableMapObj) {
			targetTableCollection = (HashMap<String, Object>) targetTableMapObj;
		} else {
			targetTableCollection = new HashMap<String, Object>();
			querySymbols.put(MUMBLE_TARGET_TABLE_KEY, targetTableCollection);
		}
		targetTableCollection.put(normalizedTargetRef, targetColumns);

		HashMap<String, Object> globalTableDictionary = walker.getWalkerTableDictionary();
		HashMap<String, Object> globalTargetColumns = ensureTableDictionaryEntry(globalTableDictionary, normalizedTargetRef);
		globalTargetColumns.putAll(targetColumns);
	}

	private HashMap<String, Object> partitionParentResolvableQualifiedUnknownsAndEmit(
			HashMap<String, Object> qualifiedUnresolvedMap,
			HashMap<String, Object> parentTableAliasMap,
			HashMap<String, Object> parentTableCollection,
			HashMap<String, Object> parentQueryCollection) {
		HashMap<String, Object> parentResolvable = new HashMap<String, Object>();
		HashMap<String, Object> unresolvedInParent = new HashMap<String, Object>();

		if (qualifiedUnresolvedMap == null || qualifiedUnresolvedMap.isEmpty()) {
			return parentResolvable;
		}

		for (Map.Entry<String, Object> entry : qualifiedUnresolvedMap.entrySet()) {
			String unresolvedKey = entry.getKey();
			if (walker.canResolveQualifiedUnknownInScope(
					unresolvedKey,
					parentTableAliasMap,
					parentTableCollection,
					parentQueryCollection)) {
				parentResolvable.put(unresolvedKey, entry.getValue());
			} else {
				unresolvedInParent.put(unresolvedKey, entry.getValue());
			}
		}

		if (!unresolvedInParent.isEmpty()) {
			walker.mergeUnknownEntries(parentResolvable, unresolvedInParent);
		}

		return parentResolvable;
	}

	private void emitQualifiedSourceNotFoundFatals(HashMap<String, Object> qualifiedUnresolvedMap) {
		if (qualifiedUnresolvedMap == null || qualifiedUnresolvedMap.isEmpty()) {
			return;
		}

		@SuppressWarnings("unchecked")
		HashMap<String, Object> currentTableAliasMap = (walker.symbolTable.get(MUMBLE_TABLE_ALIAS_KEY) instanceof HashMap<?, ?>)
				? (HashMap<String, Object>) walker.symbolTable.get(MUMBLE_TABLE_ALIAS_KEY)
				: null;
		if (currentTableAliasMap == null || currentTableAliasMap.isEmpty()) {
			currentTableAliasMap = getTopLevelQueryTableAliasMap();
		}
		HashMap<String, Object> currentTableCollection = walker.peekCurrentTableDictionary();
		if (currentTableCollection == null || currentTableCollection.isEmpty()) {
			currentTableCollection = walker.getWalkerTableDictionary();
		}

		String diagCode = walker.getDiagnosticCode(SqlASTWalkerHelper.DIAG_SQL_QUALIFIED_COLUMN_NOT_FOUND_IN_TABLE);
		String diagTemplate = walker.getDiagnosticMessage(SqlASTWalkerHelper.DIAG_SQL_QUALIFIED_COLUMN_NOT_FOUND_IN_TABLE);

		for (Map.Entry<String, Object> unresolvedEntry : qualifiedUnresolvedMap.entrySet()) {
			String unresolvedKey = unresolvedEntry.getKey();
			if (unresolvedKey == null || unresolvedKey.isBlank()) {
				continue;
			}

			int dotIndex = unresolvedKey.indexOf('.');
			if (dotIndex <= 0 || dotIndex + 1 >= unresolvedKey.length()) {
				continue;
			}

			String sourceRef = unresolvedKey.substring(0, dotIndex);
			String columnName = unresolvedKey.substring(dotIndex + 1);

			if (walker.canResolveQualifiedUnknownInScope(
					unresolvedKey,
					currentTableAliasMap,
					currentTableCollection,
					walker.queryColumnDictionaryMap)) {
				String resolvedSourceRef = walker.resolveAliasToTableName(sourceRef, currentTableAliasMap);
				HashMap<String, Object> indicatedTableDictionary = walker.getTableDictionaryForReference(
						resolvedSourceRef,
						currentTableCollection);
				if (indicatedTableDictionary != null) {
					walker.mergeResolvedColumnIntoDictionary(indicatedTableDictionary, columnName, unresolvedEntry.getValue());
				}
				continue;
			}

			Object unresolvedValue = unresolvedEntry.getValue();
			Object capturedValue = walker.getCapturedQualifiedUnresolvedLocationEntry(unresolvedKey);
			Object diagnosticValue = (capturedValue != null) ? capturedValue : unresolvedValue;

			Integer[] refLocation = walker.getLineAndCharacterFromEntry(diagnosticValue);
			if (refLocation[0] == null || refLocation[1] == null) {
				refLocation = walker.getFirstEntryLineAndCharacter(qualifiedUnresolvedMap);
			}

			String allLocationsInline = walker.formatAllLocationsForEntryInline(diagnosticValue);
			boolean hasMergedLocations = allLocationsInline != null && allLocationsInline.contains(",");

			String diagMessage;
			if (hasMergedLocations) {
				diagMessage = String.format(
						"Source Table not found for Column '%s' at %s. No alias or table called '%s'.",
						columnName,
						allLocationsInline,
						sourceRef);
			} else {
				diagMessage = String.format(
						diagTemplate,
						columnName,
						refLocation[0],
						refLocation[1],
						sourceRef);
			}

			walker.addWalkerFatal(
					diagCode,
					diagMessage,
					refLocation[0],
					refLocation[1],
					columnName);
		}
	}

	@SuppressWarnings("unchecked")
	private HashMap<String, Object> getTopLevelQueryTableAliasMap() {
		if (walker.symbolTable == null || walker.symbolTable.isEmpty()) {
			return null;
		}

		HashMap<String, Object> selectedAliasMap = null;
		int highestQueryIndex = -1;

		for (Map.Entry<String, Object> symbolEntry : walker.symbolTable.entrySet()) {
			String symbolKey = symbolEntry.getKey();
			if (symbolKey == null || !symbolKey.startsWith(MUMBLE_QUERY_KEY)) {
				continue;
			}

			Object symbolValue = symbolEntry.getValue();
			if (!(symbolValue instanceof Map<?, ?> queryScope)) {
				continue;
			}

			String suffix = symbolKey.substring(MUMBLE_QUERY_KEY.length());
			int queryIndex;
			try {
				queryIndex = Integer.parseInt(suffix);
			} catch (NumberFormatException ex) {
				continue;
			}

			Object aliasMapObject = ((Map<String, Object>) queryScope).get(MUMBLE_TABLE_ALIAS_KEY);
			if (!(aliasMapObject instanceof HashMap<?, ?>)) {
				continue;
			}

			if (queryIndex > highestQueryIndex) {
				highestQueryIndex = queryIndex;
				selectedAliasMap = (HashMap<String, Object>) aliasMapObject;
			}
		}

		return selectedAliasMap;
	}

	@SuppressWarnings("unchecked")
	private void emitQualifiedQueryAliasUnresolvedColumnsFatalAndPrune(
			HashMap<String, Object> qualifiedUnresolvedMap,
			HashMap<String, Object> tableAliasMap) {
		if (qualifiedUnresolvedMap == null || qualifiedUnresolvedMap.isEmpty()
				|| tableAliasMap == null || tableAliasMap.isEmpty()) {
			return;
		}

		ArrayList<String> keysToRemove = new ArrayList<String>();
		for (Map.Entry<String, Object> unresolvedEntry : qualifiedUnresolvedMap.entrySet()) {
			String unresolvedKey = unresolvedEntry.getKey();
			if (unresolvedKey == null) {
				continue;
			}

			int dotIndex = unresolvedKey.indexOf('.');
			if (dotIndex <= 0 || dotIndex + 1 >= unresolvedKey.length()) {
				continue;
			}

			String tableRef = unresolvedKey.substring(0, dotIndex);
			String columnName = unresolvedKey.substring(dotIndex + 1);
			Object aliasTargetObj = tableAliasMap.get(tableRef);
			if (!(aliasTargetObj instanceof String aliasTarget)) {
				continue;
			}

			boolean aliasTargetsQuery = aliasTarget.startsWith(MUMBLE_QUERY_KEY)
					|| aliasTarget.startsWith(MUMBLE_UNION_KEY)
					|| aliasTarget.startsWith(MUMBLE_INTERSECT_KEY)
					|| aliasTarget.startsWith(MUMBLE_VALUES_KEY)
					|| MUMBLE_VALUES_KEY.equals(aliasTarget);
			if (!aliasTargetsQuery) {
				continue;
			}

			Object queryDictionaryObj = walker.queryColumnDictionaryMap.get(aliasTarget);
			boolean foundInQueryInterface = queryDictionaryObj instanceof Map<?, ?> queryDictionary
					&& containsKeyIgnoreCase((Map<String, Object>) queryDictionary, columnName);
			if (!foundInQueryInterface && hasColumnInQueryOutputInterface(aliasTarget, columnName)) {
				foundInQueryInterface = true;
			}
			if (!foundInQueryInterface && hasWildcardInQueryOutputInterface(aliasTarget)) {
				foundInQueryInterface = true;
			}
			if (foundInQueryInterface) {
				continue;
			}

			Object unresolvedValue = unresolvedEntry.getValue();
			Integer[] refLocation = walker.getLineAndCharacterFromEntry(unresolvedValue);
			if (refLocation[0] == null || refLocation[1] == null) {
				refLocation = walker.getFirstEntryLineAndCharacter(qualifiedUnresolvedMap);
			}

			String diagCode = walker.getDiagnosticCode(SqlASTWalkerHelper.DIAG_SQL_QUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIAS);
			String diagTemplate = walker.getDiagnosticMessage(SqlASTWalkerHelper.DIAG_SQL_QUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIAS);
			String diagMessage = (diagTemplate == null)
					? String.format(
							"Qualified column '%s' at (l:%s c:%s) was not found in output interface of query alias '%s'.",
							columnName,
							refLocation[0],
							refLocation[1],
							tableRef)
					: String.format(diagTemplate,
							columnName,
							refLocation[0],
							refLocation[1],
							tableRef);

			walker.addWalkerFatal(
					diagCode,
					diagMessage,
					refLocation[0],
					refLocation[1],
					unresolvedKey);

			keysToRemove.add(unresolvedKey);
		}

		for (String keyToRemove : keysToRemove) {
			qualifiedUnresolvedMap.remove(keyToRemove);
		}
	}

	@SuppressWarnings("unchecked")
	private boolean canResolveUnqualifiedFromSingleWildcardQuerySource(HashMap<String, Object> unqualifiedUnresolvedMap) {
		if (unqualifiedUnresolvedMap == null || unqualifiedUnresolvedMap.isEmpty()) {
			return false;
		}

		Object tableAliasObject = walker.symbolTable.get(MUMBLE_TABLE_ALIAS_KEY);
		if (!(tableAliasObject instanceof Map<?, ?> tableAliasMap) || tableAliasMap.isEmpty()) {
			return false;
		}

		HashMap<String, Object> localTableAliasMap = (HashMap<String, Object>) tableAliasMap;
		HashMap<String, Object> localTableCollection = (HashMap<String, Object>) walker.symbolTable.get(MUMBLE_TABLE_DICTIONARY_KEY);
		if (localTableCollection == null) {
			localTableCollection = new HashMap<String, Object>();
		}
		HashMap<String, Object> visibleQuerySourceCollection = collectVisibleQuerySourceCollection(localTableAliasMap);
		if (hasUnqualifiedUnknownWithMultipleViableSources(
				unqualifiedUnresolvedMap,
				localTableCollection,
				visibleQuerySourceCollection,
				localTableAliasMap)) {
			return false;
		}

		HashSet<String> queryBackedSources = new HashSet<String>();
		for (Object mappedSourceObj : localTableAliasMap.values()) {
			if (!(mappedSourceObj instanceof String mappedSource)) {
				continue;
			}
			if (mappedSource.startsWith(MUMBLE_QUERY_KEY)
					|| mappedSource.startsWith(MUMBLE_UNION_KEY)
					|| mappedSource.startsWith(MUMBLE_INTERSECT_KEY)
					|| mappedSource.startsWith(MUMBLE_VALUES_KEY)
					|| MUMBLE_VALUES_KEY.equals(mappedSource)) {
				queryBackedSources.add(mappedSource);
			}
		}

		if (queryBackedSources.size() != 1) {
			return false;
		}

		String sourceQueryKey = queryBackedSources.iterator().next();
		Object queryDictionaryObj = walker.queryColumnDictionaryMap.get(sourceQueryKey);
		if (queryDictionaryObj instanceof Map<?, ?> queryDictionary) {
			Map<String, Object> sourceQueryDictionary = (Map<String, Object>) queryDictionary;
			if (sourceQueryDictionary.containsKey("*")) {
				unqualifiedUnresolvedMap.clear();
				return true;
			}

			ArrayList<String> resolvedKeys = new ArrayList<String>();
			for (String unresolvedKey : unqualifiedUnresolvedMap.keySet()) {
				if (sourceQueryDictionary.containsKey(unresolvedKey)
						|| hasColumnInQueryOutputInterface(sourceQueryKey, unresolvedKey)) {
					resolvedKeys.add(unresolvedKey);
				}
			}

			for (String resolvedKey : resolvedKeys) {
				unqualifiedUnresolvedMap.remove(resolvedKey);
			}
			if (unqualifiedUnresolvedMap.isEmpty()) {
				return true;
			}
		}

		ArrayList<String> interfaceResolvedKeys = new ArrayList<String>();
		for (String unresolvedKey : unqualifiedUnresolvedMap.keySet()) {
			if (hasColumnInQueryOutputInterface(sourceQueryKey, unresolvedKey)) {
				interfaceResolvedKeys.add(unresolvedKey);
			}
		}
		for (String resolvedKey : interfaceResolvedKeys) {
			unqualifiedUnresolvedMap.remove(resolvedKey);
		}
		if (unqualifiedUnresolvedMap.isEmpty()) {
			return true;
		}

		if (hasWildcardInQueryOutputInterface(sourceQueryKey)) {
			unqualifiedUnresolvedMap.clear();
			return true;
		}

		return false;
	}

	@SuppressWarnings("unchecked")
	private boolean hasWildcardInQueryOutputInterface(String queryKey) {
		if (queryKey == null || queryKey.isBlank()) {
			return false;
		}

		Object queryDefObj = getQueryDefinitionSymbol(queryKey);
		if (!(queryDefObj instanceof Map<?, ?> queryDefMap)) {
			return false;
		}

		Object interfaceObj = ((Map<String, Object>) queryDefMap).get(MUMBLE_INTERFACE_KEY);
		if (!(interfaceObj instanceof Map<?, ?> interfaceMap)) {
			return false;
		}

		return ((Map<String, Object>) interfaceMap).containsKey("*");
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
	public void exitInto_list(@NotNull SQLSelectParserParser.Into_listContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		walker.handlePushDown(ruleIndex);
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
		ArrayList<Object> queryReferenceList = new ArrayList<Object>();
		flattenSubTreeForInterfaceQueryReferences(interfaceReference, queryReferenceList);
		boolean hasQueryReferences = !queryReferenceList.isEmpty();
		if (hasQueryReferences) {
			columnList.addAll(queryReferenceList);
		} else {
			flattenSubTreeForInterfaceColumns(interfaceReference, columnList);
		}

		Object existingInterfaceEntry = selectInterface.get(interfaceAlias);
		if (existingInterfaceEntry != null) {
			emitDuplicateInterfaceColumnFatal(interfaceAlias, existingInterfaceEntry, columnList, aliasToken);
		}

		selectInterface.put(interfaceAlias, columnList);
		recordInsertSourceSelectItemSequence(interfaceAlias);
		if (hasQueryReferences || isQueryBackedSelectItemReference(interfaceReference)) {
			addCurrentQueryScalarSubqueryAlias(interfaceAlias);
		}

		// Add item alias into the Current Query Column Dictionary
		addAliasTokensObject(interfaceAlias, aliasToken);
		
	}

	private boolean isQueryBackedSelectItemReference(Map<String, Object> interfaceReference) {
		if (interfaceReference == null || interfaceReference.isEmpty()) {
			return false;
		}

		return interfaceReference.containsKey(MUMBLE_SELECT_KEY)
				|| interfaceReference.containsKey(MUMBLE_LOOKUP_KEY)
				|| interfaceReference.containsKey(MUMBLE_UNION_KEY)
				|| interfaceReference.containsKey(MUMBLE_INTERSECT_KEY)
				|| interfaceReference.containsKey(MUMBLE_VALUES_KEY);
	}

	@SuppressWarnings("unchecked")
	private void recordInsertSourceSelectItemSequence(String interfaceAlias) {
		if (interfaceAlias == null || walker.currentStackLevel(SQLSelectParserParser.RULE_insert_source_primary) == null) {
			return;
		}

		Object sequenceObject = walker.symbolTable.get(TEMP_INSERT_SOURCE_SELECT_SEQUENCE_KEY);
		ArrayList<String> sequence;
		if (sequenceObject instanceof ArrayList<?>) {
			sequence = (ArrayList<String>) sequenceObject;
		} else {
			sequence = new ArrayList<String>();
			walker.symbolTable.put(TEMP_INSERT_SOURCE_SELECT_SEQUENCE_KEY, sequence);
		}

		sequence.add(interfaceAlias);
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

	@SuppressWarnings("unchecked")
	private void emitDuplicateInterfaceColumnFatal(
			String interfaceAlias,
			Object existingInterfaceEntry,
			ArrayList<Object> incomingInterfaceEntry,
			String incomingAliasToken) {
		String existingQualifiedRef = buildInterfaceReferenceLabel(existingInterfaceEntry, interfaceAlias);
		String incomingQualifiedRef = buildInterfaceReferenceLabel(incomingInterfaceEntry, interfaceAlias);

		HashMap<String, Object> queryDictionary = (HashMap<String, Object>) walker.symbolTable.get(MUMBLE_QUERY_DICTIONARY_KEY);
		Object existingTokens = (queryDictionary == null) ? null : queryDictionary.get(interfaceAlias);

		Integer[] existingLocation = walker.getLineAndCharacterFromEntry(existingTokens);
		if (existingLocation[0] == null || existingLocation[1] == null) {
			existingLocation = walker.getLineAndCharacterFromEntry(existingInterfaceEntry);
		}

		Integer[] incomingLocation = walker.getLineAndCharacterFromEntry(incomingAliasToken);
		if (incomingLocation[0] == null || incomingLocation[1] == null) {
			incomingLocation = walker.getLineAndCharacterFromEntry(incomingInterfaceEntry);
		}

		String diagCode = walker.getDiagnosticCode(SqlASTWalkerHelper.DIAG_SQL_DUPLICATE_INTERFACE_COLUMNS);
		String diagTemplate = walker.getDiagnosticMessage(SqlASTWalkerHelper.DIAG_SQL_DUPLICATE_INTERFACE_COLUMNS);
		String diagMessage = (diagTemplate == null)
				? String.format(
						"Duplicate interface columns defined: %s at (l:%s c:%s) and %s at (l:%s c:%s).",
						existingQualifiedRef,
						existingLocation[0],
						existingLocation[1],
						incomingQualifiedRef,
						incomingLocation[0],
						incomingLocation[1])
				: String.format(
						diagTemplate,
						existingQualifiedRef,
						existingLocation[0],
						existingLocation[1],
						incomingQualifiedRef,
						incomingLocation[0],
						incomingLocation[1]);

		walker.addWalkerFatal(
				diagCode,
				diagMessage,
				existingLocation[0],
				existingLocation[1],
				existingQualifiedRef + "," + incomingQualifiedRef);
	}

	private String buildInterfaceReferenceLabel(Object interfaceEntry, String fallbackColumnName) {
		String columnName = fallbackColumnName;
		String tableRef = null;

		if (interfaceEntry instanceof ArrayList<?> refs && !refs.isEmpty()) {
			Object firstRef = refs.get(0);
			String extractedName = walker.extractReferenceNameFromInterfaceEntry(firstRef);
			String extractedTableRef = walker.extractReferenceTableRefFromInterfaceEntry(firstRef);
			if (extractedName != null && !extractedName.isBlank()) {
				columnName = extractedName;
			}
			if (extractedTableRef != null && !extractedTableRef.isBlank() && !"*".equals(extractedTableRef)) {
				tableRef = extractedTableRef;
			}
		}

		if (tableRef != null) {
			return tableRef + "." + columnName;
		}
		return columnName;
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

	@SuppressWarnings("unchecked")
	private void flattenSubTreeForInterfaceQueryReferences(HashMap<String, Object> subTree, ArrayList<Object> references) {
		if (subTree == null) {
			return;
		}

		if (isQueryBackedSelectItemReference(subTree)) {
			String queryReference = resolveQueryReferenceFromSubTree(subTree);
			if (isQuerySourceReference(queryReference)) {
				HashMap<String, Object> queryEntry = new HashMap<String, Object>();
				queryEntry.put(MUMBLE_QUERY_KEY, queryReference);
				if (!references.contains(queryEntry)) {
					references.add(queryEntry);
				}
			}
			subTree.remove(MUMBLE_QUERY_KEY);
			return;
		}

		for (Object value : subTree.values()) {
			if (value instanceof HashMap<?, ?> valueMapObj) {
				flattenSubTreeForInterfaceQueryReferences((HashMap<String, Object>) valueMapObj, references);
			} else if (value instanceof ArrayList<?> valueListObj) {
				for (Object listItem : (ArrayList<Object>) valueListObj) {
					if (listItem instanceof HashMap<?, ?> listMapObj) {
						flattenSubTreeForInterfaceQueryReferences((HashMap<String, Object>) listMapObj, references);
					}
				}
			}
		}
	}

	private void annotateSubqueryReference(Map<String, Object> subTree, String queryReference) {
		if (subTree == null || subTree.isEmpty() || !isQuerySourceReference(queryReference)) {
			return;
		}
		subTree.put(MUMBLE_QUERY_KEY, queryReference);
	}

	@SuppressWarnings("unchecked")
	private String resolveQueryReferenceFromSubTree(Map<String, Object> subTree) {
		if (subTree == null || subTree.isEmpty()) {
			return null;
		}

		Object queryObject = subTree.get(MUMBLE_QUERY_KEY);
		if (queryObject instanceof String queryReference && isQuerySourceReference(queryReference)) {
			return queryReference;
		}

		for (Object value : subTree.values()) {
			if (value instanceof Map<?, ?> valueMapObj) {
				String nestedReference = resolveQueryReferenceFromSubTree((Map<String, Object>) valueMapObj);
				if (isQuerySourceReference(nestedReference)) {
					return nestedReference;
				}
			} else if (value instanceof ArrayList<?> valueListObj) {
				for (Object listItem : (ArrayList<Object>) valueListObj) {
					if (listItem instanceof Map<?, ?> listMapObj) {
						String nestedReference = resolveQueryReferenceFromSubTree((Map<String, Object>) listMapObj);
						if (isQuerySourceReference(nestedReference)) {
							return nestedReference;
						}
					}
				}
			}
		}

		return null;
	}

	// Standardize the interface reference map into a flat map of column references and not the entire AST subtree
	// This is a recursive function that traverses the item subtree until it finds column references or substitution variables, 
	// which it adds to the column list with the alias as the key

	private void flattenSubTreeForInterfaceColumns(HashMap<String, Object> subTree, ArrayList<Object> columnList) {
		if (subTree == null) {
			return;
		}

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
			for (Object value : subTree.values()) {
				if (value instanceof HashMap<?, ?> valueMapObj) {
					flattenSubTreeForInterfaceColumns((HashMap<String, Object>) valueMapObj, columnList);
				} else if (value instanceof ArrayList<?> valueListObj) {
					for (Object listItem : (ArrayList<Object>) valueListObj) {
						if (listItem instanceof HashMap<?, ?> listMapObj) {
							flattenSubTreeForInterfaceColumns((HashMap<String, Object>) listMapObj, columnList);
						}
					}
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
	public void exitRelation_as_clause(@NotNull SQLSelectParserParser.Relation_as_clauseContext ctx) {
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
		walker.handleOneChild(ruleIndex);
	}

	@Override
	public void exitWildcard_reference(@NotNull SQLSelectParserParser.Wildcard_referenceContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		int parentRuleIndex = ctx.getParent().getRuleIndex();

		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Integer parentStackLevel = walker.currentStackLevel(parentRuleIndex);

		Map<String, Object> subMap = walker.removeNodeMap(ruleIndex, stackLevel);
		if (subMap == null) {
			// unqualified wildcard can have no map
			subMap = walker.makeRuleMap(ruleIndex);
		}
		subMap.remove(ASTWALKER_RULE_TYPE_KEY);

		Map<String, Object> item = new HashMap<String, Object>();
		String wildcardSource = null;

		if (ctx.tb_name != null) {
			wildcardSource = ctx.tb_name.getText();
			item.put(MUMBLE_TABLE_REF_KEY, wildcardSource);
		} else {
			item.put(MUMBLE_TABLE_REF_KEY, "*");
		}
		item.put(MUMBLE_NAME_KEY, "*");

		if (ctx.getParent() instanceof SQLSelectParserParser.Count_all_aggregateContext) {
			item.put("origin", "count_all_aggregate");
		}

		if (wildcardSource == null) {
			walker.collectUnresolvedColumnReference(MUMBLE_UNKNOWN_KEY, item, ctx.getStart());
		} else {
			walker.collectUnresolvedColumnReference(wildcardSource, item, ctx.getStart());
		}

		subMap.put(MUMBLE_COLUMN_KEY, item);
		walker.addToParent(parentRuleIndex, parentStackLevel, subMap);
		walker.showTrace(walker.parseTrace, "Wildcard Reference: " + subMap);
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

		convertSymbolTableToTableDictionary(false, false, null);
	}

	/**
	 * Create Dictionary from Symbol Table
	 * Validate and assign all columns to a specific source table or query
	 * Perform quality diagnostics for any unresolved columns, and if emitFinalUnresolvedUnknownFatal is true,
	 * then add fatal diagnostics to parser resultfor any remaining unresolved columns after this process
	 * 
	 * @return
	 */
	private HashMap<String, Object> convertSymbolTableToTableDictionary(
			boolean emitFinalUnresolvedUnknownFatal,
			boolean deferCorrelatedValueSubqueryQualifiedUnknowns,
			String updateTargetTableRef) {
	
		// deconstruct current symbol table into components for analysis
 		HashMap<String, Object> localInterface = (HashMap<String, Object>) walker.symbolTable.remove(MUMBLE_INTERFACE_KEY);
		HashMap<String, Object> localLhsUnresolvedColumnMap = (HashMap<String, Object>) walker.symbolTable.remove(MUMBLE_LHS_UNRESOLVED_COLUMNS_KEY);
		if (localLhsUnresolvedColumnMap == null)
			localLhsUnresolvedColumnMap = new HashMap<String, Object>();
		walker.captureQualifiedUnresolvedLocations(localLhsUnresolvedColumnMap);
		HashMap<String, Object> localUnresolvedColumnMap = (HashMap<String, Object>) walker.symbolTable.remove(MUMBLE_UNRESOLVED_COLUMN_KEY);
		if (localUnresolvedColumnMap == null)
			localUnresolvedColumnMap = new HashMap<String, Object>();
		walker.captureQualifiedUnresolvedLocations(localUnresolvedColumnMap);
		HashMap<String, Object> localTableAliasMap = (HashMap<String, Object>) walker.symbolTable.remove(MUMBLE_TABLE_ALIAS_KEY);
		if (localTableAliasMap == null)
			localTableAliasMap = new HashMap<String, Object>();
		HashMap<String, Object> localTableCollection = (HashMap<String, Object>) walker.symbolTable.remove(MUMBLE_TABLE_DICTIONARY_KEY);
		if (localTableCollection == null)
			localTableCollection = new HashMap<String, Object>();
		HashMap<String, Object> localTargetTableCollection = (HashMap<String, Object>) walker.symbolTable.remove(MUMBLE_TARGET_TABLE_KEY);
		if (localTargetTableCollection == null)
			localTargetTableCollection = new HashMap<String, Object>();
		HashMap<String, Object> localCurrentQueryDictionary = (HashMap<String, Object>) walker.symbolTable.remove(MUMBLE_QUERY_DICTIONARY_KEY);
		if (localCurrentQueryDictionary == null)
			localCurrentQueryDictionary = new HashMap<String, Object>();

		// Leave these null if they don't exist
		HashSet<String> localScalarSubqueryAliases = (HashSet<String>) walker.symbolTable.remove(MUMBLE_SCALAR_SUBQUERY_ALIASES_KEY);
        Object  filtersList = walker.symbolTable.remove(MUMBLE_FILTERS_KEY);
		Object groupedByList = walker.symbolTable.remove(MUMBLE_GROUPED_BY_KEY);
		Object orderedByList = walker.symbolTable.remove(MUMBLE_ORDERED_BY_KEY);


		// Add query/derived-source aliases into the alias collection for downstream source resolution.
		walker.mergeNonTableAliasMappingsIntoAliasCollection(localCurrentQueryDictionary, localTableAliasMap);

		// Resolve alias-backed table refs so tableCollection keys align with canonical table references.
		walker.reconcileAliasBackedTableReferences(localTableCollection, localTableAliasMap);
		if (updateTargetTableRef != null && !updateTargetTableRef.isBlank()) {
			pruneUpdateTargetFromInputTableCollection(localTableCollection, updateTargetTableRef, localTableAliasMap);
		}

		if (updateTargetTableRef != null && !updateTargetTableRef.isBlank()) {
			resolveUpdateLhsColumnsToTargetTable(
					localLhsUnresolvedColumnMap,
					localUnresolvedColumnMap,
					localTableAliasMap,
					localTargetTableCollection,
					updateTargetTableRef);
			resolveUpdateQualifiedUnresolvedColumnsToInputTables(
					localUnresolvedColumnMap,
					localTableAliasMap,
					localTableCollection);
			resolveUpdateUnqualifiedUnresolvedColumnsToTargetTableWhenNoInputSources(
					localUnresolvedColumnMap,
					localTableCollection,
					localTargetTableCollection,
					localTableAliasMap,
					updateTargetTableRef);
		}

		// // Add scalar subquery aliases into the alias collection for downstream source resolution, which allows scalar subqueries to be resolved as sources for columns in the query interface.
		// walker.mergeScalarSubqueryAliasesIntoAliasCollection(localScalarSubqueryAliases, localTableAliasMap);
			
		if (localScalarSubqueryAliases != null && !localScalarSubqueryAliases.isEmpty()) {
			HashMap<String, Object> qualifiedUnresolvedColumns = new HashMap<String, Object>();
			HashMap<String, Object> unqualifiedUnresolvedColumns = new HashMap<String, Object>();
			// Split unresolved column map into sets: ones with explicit table qualifiers, and ones without.  This allows us to apply different resolution strategies to each set, and also allows us to provide more accurate diagnostics for explicitly qualified columns that cannot be resolved to a source.
			walker.splitExplicitlyQualifiedUnknownEntriesFromUnqualified(localUnresolvedColumnMap, localInterface, 
				filtersList, qualifiedUnresolvedColumns, unqualifiedUnresolvedColumns);
		} // temp location

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
		propagateUnqualifiedSelectStarToScopeTables(
				localInterface,
				localCurrentQueryDictionary,
				localTableCollection);
		HashMap<String, Object> visibleQuerySourceCollection = collectVisibleQuerySourceCollection(localTableAliasMap);

		if (updateTargetTableRef != null && !updateTargetTableRef.isBlank()) {
			resolveUpdateRhsUnqualifiedAssignmentColumnsToTargetTable(
					localUnresolvedColumnMap,
					localTableCollection,
					localTargetTableCollection,
					localTableAliasMap,
					visibleQuerySourceCollection,
					updateTargetTableRef);
		}

		if (!localUnresolvedColumnMap.isEmpty()) {
			// Check for explicitly qualified columns whose table qualifiers do not exist
			HashMap<String, Object> explicitQualifiedUnknownEntries = extractExplicitQualifiedUnknownEntries(
					localUnresolvedColumnMap,
					localInterface,
					filtersList,
					groupedByList,
					orderedByList);
			if (deferCorrelatedValueSubqueryQualifiedUnknowns) {
				explicitQualifiedUnknownEntries = retainOnlyLocallyResolvableExplicitQualifiedUnknowns(
						explicitQualifiedUnknownEntries,
						localInterface,
						filtersList,
						localTableAliasMap,
						localTableCollection,
						localCurrentQueryDictionary,
						localUnresolvedColumnMap);
			}
			emitExplicitQualifiedUnknownDiagnostics(
					explicitQualifiedUnknownEntries,
					localInterface,
					filtersList,
					localTableAliasMap,
					localTableCollection,
					visibleQuerySourceCollection,
					localCurrentQueryDictionary,
					localUnresolvedColumnMap);

			// Only relocate unresolved unqualified columns to a single target when no column
			// still has multiple viable logical sources in scope.
			boolean hasMultiSourceUnqualifiedUnknown = hasUnqualifiedUnknownWithMultipleViableSources(
					localUnresolvedColumnMap,
					localTableCollection,
					visibleQuerySourceCollection,
					localTableAliasMap);
			if (!hasMultiSourceUnqualifiedUnknown) {
				// Attempt to resolve any remaining unqualified unknowns that only have one possible source table, and move them to the resolved table dictionary for downstream resolution.  This allows us to resolve queries with unqualified columns that only have one possible source.
				boolean movedToSingleTableTarget = 
					walker.moveEntriesToSingleTableIfSingleTarget(localUnresolvedColumnMap, localTableCollection);
				if (!movedToSingleTableTarget && !localUnresolvedColumnMap.isEmpty()) {
					walker.moveEntriesToSingleTableIfSingleTarget(localUnresolvedColumnMap, currentTableDictionary);
				}
			}
		}

		if (updateTargetTableRef != null && !updateTargetTableRef.isBlank()) {
			// Before merging, resolve any remaining qualified unresolved columns whose
			// qualifier matches the update target table (e.g. WHERE-clause filter columns
			// like this_table.key, or RHS predicand refs like target.col) into the target
			// table collection.  After this step, if the unresolved map is empty the normal
			// diagnostic block was never reached and the merge can proceed cleanly;
			// otherwise the remaining entries will continue through the usual diagnostics.
			resolveRemainingQualifiedUnresolvedColumnsToTargetTable(
					localUnresolvedColumnMap,
					localTableAliasMap,
					localTargetTableCollection,
					updateTargetTableRef);
			mergeUpdateTargetAndLhsIntoTableDictionary(
					localTargetTableCollection,
					localLhsUnresolvedColumnMap,
					localTableCollection,
					localTableAliasMap,
					updateTargetTableRef);
			localTargetTableCollection.clear();
			localLhsUnresolvedColumnMap.clear();
		}

		// Merge table collection into the table dictionary map, which is used for symbol resolution in the rest of the query processing.
		//   This allows all table references to be resolved against the same dictionary regardless of where they are defined in the query.
		if (localTableCollection != null && localTableCollection.size() > 0) {
			for (String tab_ref : localTableCollection.keySet()) {
				String reference = normalizeTableRef(tab_ref);
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
			if (refsObj instanceof ArrayList<?>) {
				ArrayList<Object> refs = (ArrayList<Object>) refsObj;
				for (int refIndex = 0; refIndex < refs.size(); refIndex++) {
					Object refObj = refs.get(refIndex);
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
						String resolvedNonTableSourceRef = walker.resolveAliasToNonTableSourceQueryKey(tableRef, visibleQuerySourceCollection);
						boolean explicitQueryReference = resolvedNonTableSourceRef != null
								|| walker.isNonTableQuerySourceReference(resolvedTableRef);
						boolean resolvedInSource = false;

						if (explicitQueryReference) {
							String queryDictionaryKey = (resolvedNonTableSourceRef != null)
									? resolvedNonTableSourceRef
									: resolvedTableRef;
							Object queryDictionaryObj = walker.queryColumnDictionaryMap.get(queryDictionaryKey);
							if (queryDictionaryObj instanceof Map<?, ?> queryDictionary) {
								resolvedInSource = containsKeyIgnoreCase((Map<String, Object>) queryDictionary, columnName)
										|| ((Map<String, Object>) queryDictionary).containsKey("*");
							}
							if (!resolvedInSource) {
								resolvedInSource = hasColumnInQueryOutputInterface(queryDictionaryKey, columnName);
							}
							if (!resolvedInSource) {
								resolvedInSource = hasWildcardInQueryOutputInterface(queryDictionaryKey);
							}
							if (resolvedInSource) {
								Object qualifiedUnknownEntry = consumeQualifiedUnknownEntry(
										localUnresolvedColumnMap,
										tableRef,
										columnName,
										false);
								if (qualifiedUnknownEntry == null) {
									qualifiedUnknownEntry = consumeUnqualifiedUnknownEntry(
											localUnresolvedColumnMap,
											columnName);
								}
								if (qualifiedUnknownEntry != null) {
									mergeExplicitQualifiedUnknownIntoSourceQueryDictionary(
											queryDictionaryKey,
											columnName,
											qualifiedUnknownEntry);
								}
							} else {
								// Fatal for query-alias references is already emitted by emitExplicitQualifiedUnknownDiagnostics.
								hasSpecificResolutionFatalForOutputColumn = true;
								continue;
							}
						} else {
							HashMap<String, Object> indicatedTableDictionary = walker.getTableDictionaryForReference(
									resolvedTableRef,
									localTableCollection);
							// For explicit table references (table.column), table/alias existence is sufficient.
							// Physical table schemas are not always available at parse time.
							resolvedInSource = indicatedTableDictionary != null;
						}

						if (!resolvedInSource) {
							if (deferCorrelatedValueSubqueryQualifiedUnknowns && !explicitQueryReference) {
								// Defer unresolved explicit table refs in correlated value-subqueries so
								// parent query aliases/tables can resolve them after this scope exits.
								continue;
							}
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
						Integer[] refLocation = resolveUnqualifiedReferenceLocation(
								columnName,
								refObj,
								localUnresolvedColumnMap,
								localCurrentQueryDictionary,
								outputCol);

						ArrayList<String> sourceRefs = collectUnqualifiedSourceReferences(
								columnName,
								localTableCollection,
								visibleQuerySourceCollection,
								localTableAliasMap);

						if (sourceRefs.isEmpty()) {
							// Scenario: implicit reference has no candidate source.
							continue;
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

							walker.addWalkerDiagnostic(
									ParseDiagnostic.Severity.SEVERE_WARNING,
									diagCode,
									diagMessage,
									refLocation[0],
									refLocation[1],
									walker.getClass().getSimpleName(),
									null,
									columnName,
									true,
									"ast-walk",
									null,
									null);
							hasSpecificResolutionFatalForOutputColumn = true;
						} else {
							// Resolve an implicit column with a single source by updating only
							// the interface entry copy (do not mutate shared AST map objects).
							String resolvedSourceRef = sourceRefs.get(0);
							refs.set(refIndex, cloneReferenceWithResolvedTableRef(refObj, resolvedSourceRef));
						}
					}
				}
			}
		}
		}

		mergeSelectListQualifiedQueryAliasRefsIntoSourceQueryDictionary(
				localInterface,
				localCurrentQueryDictionary,
				localUnresolvedColumnMap,
				localTableAliasMap,
				visibleQuerySourceCollection);

		assignTableRefsForColumnReferenceList(
				filtersList,
				localUnresolvedColumnMap,
				localCurrentQueryDictionary,
				localTableCollection,
				visibleQuerySourceCollection,
				localTableAliasMap);
		assignTableRefsForColumnReferenceList(
				groupedByList,
				localUnresolvedColumnMap,
				localCurrentQueryDictionary,
				localTableCollection,
				visibleQuerySourceCollection,
				localTableAliasMap);
		assignTableRefsForColumnReferenceList(
				orderedByList,
				localUnresolvedColumnMap,
				localCurrentQueryDictionary,
				localTableCollection,
				visibleQuerySourceCollection,
				localTableAliasMap);

		 walker.validateQueryInterface(localInterface, localCurrentQueryDictionary, localTableAliasMap, localTableCollection);
		 walker.validateFilterReferences(filtersList, localCurrentQueryDictionary, localTableAliasMap, localTableCollection);

		// Merge everything back together into the final symbol table for this level of the query, with table aliases first, 
		// then symbol table entries, then query entries, then table entries, then the interface and current query dictionary 
		// for this level, and finally any filters list for this level if it exists.
		if (localTableAliasMap != null && !localTableAliasMap.isEmpty())
			walker.symbolTable.put(MUMBLE_TABLE_ALIAS_KEY, localTableAliasMap);
		walker.symbolTable.put(MUMBLE_TABLE_DICTIONARY_KEY, localTableCollection);
		if (!localTargetTableCollection.isEmpty()) {
			walker.symbolTable.put(MUMBLE_TARGET_TABLE_KEY, localTargetTableCollection);
		}
		if (updateTargetTableRef == null || !localCurrentQueryDictionary.isEmpty()) {
			walker.symbolTable.put(MUMBLE_QUERY_DICTIONARY_KEY, localCurrentQueryDictionary);
		}
		//walker.symbolTable.putAll(localCurrentQueryDictionary);
		//walker.symbolTable.putAll(localTableCollection);
		if (updateTargetTableRef == null) {
			walker.symbolTable.put(MUMBLE_INTERFACE_KEY, localInterface);
		}
		if (!localLhsUnresolvedColumnMap.isEmpty()) {
			walker.symbolTable.put(MUMBLE_LHS_UNRESOLVED_COLUMNS_KEY, localLhsUnresolvedColumnMap);
		}
		if (!localUnresolvedColumnMap.isEmpty()) {
			walker.symbolTable.put(MUMBLE_UNRESOLVED_COLUMN_KEY, localUnresolvedColumnMap);
		}
		// Call a method here that will merge the local Table Dictionary into the walker's TableDictionary Map
		walker.mergeTableDictionaryIntoWalkerTableDictionary(currentTableDictionary);
		
		if (localScalarSubqueryAliases != null)
			walker.symbolTable.put(MUMBLE_SCALAR_SUBQUERY_ALIASES_KEY, localScalarSubqueryAliases);
		if (filtersList != null)
			walker.symbolTable.put(MUMBLE_FILTERS_KEY, filtersList);
		if (groupedByList != null)
			walker.symbolTable.put(MUMBLE_GROUPED_BY_KEY, groupedByList);
		if (orderedByList != null)
			walker.symbolTable.put(MUMBLE_ORDERED_BY_KEY, orderedByList);

		 walker.showTrace(walker.symbolTrace, "Symbol Table: " + walker.symbolTable);
		return walker.symbolTable;
	}

	private void pruneUpdateTargetFromInputTableCollection(
			HashMap<String, Object> tableCollection,
			String updateTargetTableRef,
			HashMap<String, Object> tableAliasMap) {
		if (tableCollection == null || tableCollection.isEmpty() || updateTargetTableRef == null) {
			return;
		}

		String resolvedTarget = walker.resolveAliasToTableName(updateTargetTableRef, tableAliasMap);
		if (resolvedTarget == null || resolvedTarget.isBlank()) {
			resolvedTarget = updateTargetTableRef;
		}
		if (resolvedTarget == null || resolvedTarget.isBlank()) {
			return;
		}

		String normalizedTarget = normalizeTableRef(resolvedTarget);
		tableCollection.remove(normalizedTarget);
	}

	@SuppressWarnings("unchecked")
	private void resolveUpdateLhsColumnsToTargetTable(
			HashMap<String, Object> lhsUnresolvedColumnMap,
			HashMap<String, Object> unresolvedColumnMap,
			HashMap<String, Object> tableAliasMap,
			HashMap<String, Object> targetTableCollection,
			String updateTargetTableRef) {
		if (lhsUnresolvedColumnMap == null || lhsUnresolvedColumnMap.isEmpty()) {
			return;
		}
		if (targetTableCollection == null) {
			return;
		}

		String resolvedTarget = walker.resolveAliasToTableName(updateTargetTableRef, tableAliasMap);
		if (resolvedTarget == null || resolvedTarget.isBlank()) {
			resolvedTarget = updateTargetTableRef;
		}
		if (resolvedTarget == null || resolvedTarget.isBlank()) {
			return;
		}

		String normalizedTarget = normalizeTableRef(resolvedTarget);

		Object targetTableObj = targetTableCollection.get(normalizedTarget);
		HashMap<String, Object> targetTableColumns;
		if (targetTableObj instanceof HashMap<?, ?>) {
			targetTableColumns = (HashMap<String, Object>) targetTableObj;
		} else {
			targetTableColumns = new HashMap<String, Object>();
			targetTableCollection.put(normalizedTarget, targetTableColumns);
		}

		for (Map.Entry<String, Object> lhsEntry : lhsUnresolvedColumnMap.entrySet()) {
			String lhsKey = lhsEntry.getKey();
			Object lhsValue = lhsEntry.getValue();
			String lhsColumnName = extractLhsColumnName(lhsKey, lhsValue);
			if (lhsColumnName == null || lhsColumnName.isBlank()) {
				continue;
			}

			Object normalizedRefs = normalizeUpdateColumnRefs(lhsValue);
			if (normalizedRefs != null) {
				targetTableColumns.put(lhsColumnName, normalizedRefs);
			}

			if (unresolvedColumnMap != null) {
				if (lhsKey != null) {
					unresolvedColumnMap.remove(lhsKey);
				}
				unresolvedColumnMap.remove(lhsColumnName);
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void mergeSelectListQualifiedQueryAliasRefsIntoSourceQueryDictionary(
			HashMap<String, Object> localInterface,
			HashMap<String, Object> localCurrentQueryDictionary,
			HashMap<String, Object> localUnresolvedColumnMap,
			HashMap<String, Object> localTableAliasMap,
			HashMap<String, Object> visibleQuerySourceCollection) {
		if (localInterface == null || localInterface.isEmpty()) {
			return;
		}

		for (Map.Entry<String, Object> interfaceEntry : localInterface.entrySet()) {
			String outputCol = interfaceEntry.getKey();
			Object refsObj = interfaceEntry.getValue();
			if (!(refsObj instanceof ArrayList<?> refs)) {
				continue;
			}

			for (Object refObj : refs) {
				String columnName = walker.extractReferenceNameFromInterfaceEntry(refObj);
				String tableRef = walker.extractReferenceTableRefFromInterfaceEntry(refObj);
				if (columnName == null || columnName.isBlank() || "*".equals(columnName)
						|| tableRef == null || tableRef.isBlank() || "*".equals(tableRef)) {
					continue;
				}

				String resolvedNonTableSourceRef = walker.resolveAliasToNonTableSourceQueryKey(tableRef, visibleQuerySourceCollection);
				String resolvedTableRef = walker.resolveAliasToTableName(tableRef, localTableAliasMap);
				String querySourceRef = (resolvedNonTableSourceRef != null)
						? resolvedNonTableSourceRef
						: resolvedTableRef;
				if (querySourceRef == null || !walker.isNonTableQuerySourceReference(querySourceRef)) {
					continue;
				}
				if (!aliasMapsToQuerySource(tableRef, querySourceRef, localTableAliasMap)) {
					continue;
				}

				Object sourceDictionaryObj = walker.queryColumnDictionaryMap.get(querySourceRef);
				if (!(sourceDictionaryObj instanceof Map<?, ?> sourceDictionaryMapObj)) {
					continue;
				}
				Map<String, Object> sourceDictionary = (Map<String, Object>) sourceDictionaryMapObj;
				boolean sourceProvidesColumn = containsKeyIgnoreCase(sourceDictionary, columnName)
						|| containsKeyIgnoreCase(sourceDictionary, "*")
						|| hasColumnInQueryOutputInterface(querySourceRef, columnName)
						|| hasWildcardInQueryOutputInterface(querySourceRef);
				if (!sourceProvidesColumn) {
					continue;
				}

				Object sourceRefTokens = consumeQualifiedUnknownEntry(localUnresolvedColumnMap, tableRef, columnName, false);
				if (sourceRefTokens != null) {
					mergeExplicitQualifiedUnknownIntoSourceQueryDictionary(querySourceRef, columnName, sourceRefTokens);
				}
			}
		}
	}

	private boolean aliasMapsToQuerySource(
			String aliasRef,
			String querySourceRef,
			HashMap<String, Object> tableAliasMap) {
		if (aliasRef == null || aliasRef.isBlank() || querySourceRef == null || querySourceRef.isBlank()) {
			return false;
		}
		if (tableAliasMap == null || tableAliasMap.isEmpty()) {
			return false;
		}

		String mappedSource = null;
		Object mappedObj = tableAliasMap.get(aliasRef);
		if (mappedObj instanceof String s && !s.isBlank()) {
			mappedSource = s;
		} else {
			for (Map.Entry<String, Object> entry : tableAliasMap.entrySet()) {
				if (entry.getKey() != null
						&& entry.getKey().equalsIgnoreCase(aliasRef)
						&& entry.getValue() instanceof String mappedValue
						&& !mappedValue.isBlank()) {
					mappedSource = mappedValue;
					break;
				}
			}
		}
		if (mappedSource == null || mappedSource.isBlank()) {
			return false;
		}

		String normalizedMapped = mappedSource.startsWith("def_") ? mappedSource.substring("def_".length()) : mappedSource;
		String normalizedQuerySource = querySourceRef.startsWith("def_") ? querySourceRef.substring("def_".length()) : querySourceRef;
		return normalizedMapped.equalsIgnoreCase(normalizedQuerySource);
	}

	@SuppressWarnings("unchecked")
	private void resolveUpdateQualifiedUnresolvedColumnsToInputTables(
			HashMap<String, Object> unresolvedColumnMap,
			HashMap<String, Object> tableAliasMap,
			HashMap<String, Object> tableCollection) {
		if (unresolvedColumnMap == null || unresolvedColumnMap.isEmpty()
				|| tableCollection == null || tableCollection.isEmpty()) {
			return;
		}

		ArrayList<String> resolvedKeys = new ArrayList<String>();
		for (Map.Entry<String, Object> unresolvedEntry : unresolvedColumnMap.entrySet()) {
			String unresolvedKey = unresolvedEntry.getKey();
			if (unresolvedKey == null) {
				continue;
			}

			int separatorIndex = unresolvedKey.lastIndexOf('.');
			if (separatorIndex <= 0 || separatorIndex + 1 >= unresolvedKey.length()) {
				continue;
			}

			String qualifier = unresolvedKey.substring(0, separatorIndex);
			String columnName = unresolvedKey.substring(separatorIndex + 1);
			if (columnName.isBlank()) {
				continue;
			}

			String resolvedTableRef = walker.resolveAliasToTableName(qualifier, tableAliasMap);
			if (resolvedTableRef == null || resolvedTableRef.isBlank()) {
				resolvedTableRef = qualifier;
			}
			if (resolvedTableRef == null || resolvedTableRef.isBlank()) {
				continue;
			}

			String normalizedTableRef = normalizeTableRef(resolvedTableRef);
			Object tableEntryObj = tableCollection.get(normalizedTableRef);
			if (!(tableEntryObj instanceof Map<?, ?> tableEntryMapObj)) {
				continue;
			}

			Map<String, Object> tableEntry = (Map<String, Object>) tableEntryMapObj;
			Object normalizedRefs = normalizeUpdateColumnRefs(unresolvedEntry.getValue());
			if (normalizedRefs == null) {
				continue;
			}

			tableEntry.put(columnName, normalizedRefs);
			resolvedKeys.add(unresolvedKey);
		}

		for (String resolvedKey : resolvedKeys) {
			unresolvedColumnMap.remove(resolvedKey);
		}
	}

	@SuppressWarnings("unchecked")
	private void resolveUpdateUnqualifiedUnresolvedColumnsToTargetTableWhenNoInputSources(
			HashMap<String, Object> unresolvedColumnMap,
			HashMap<String, Object> tableCollection,
			HashMap<String, Object> targetTableCollection,
			HashMap<String, Object> tableAliasMap,
			String updateTargetTableRef) {
		if (unresolvedColumnMap == null || unresolvedColumnMap.isEmpty()
				|| targetTableCollection == null
				|| updateTargetTableRef == null || updateTargetTableRef.isBlank()) {
			return;
		}

		if (tableCollection != null && !tableCollection.isEmpty()) {
			return;
		}

		String resolvedTarget = walker.resolveAliasToTableName(updateTargetTableRef, tableAliasMap);
		if (resolvedTarget == null || resolvedTarget.isBlank()) {
			resolvedTarget = updateTargetTableRef;
		}
		if (resolvedTarget == null || resolvedTarget.isBlank()) {
			return;
		}

		String normalizedTarget = normalizeTableRef(resolvedTarget);

		Object targetTableObj = targetTableCollection.get(normalizedTarget);
		HashMap<String, Object> targetColumns;
		if (targetTableObj instanceof HashMap<?, ?> existingMap) {
			targetColumns = (HashMap<String, Object>) existingMap;
		} else {
			targetColumns = new HashMap<String, Object>();
			targetTableCollection.put(normalizedTarget, targetColumns);
		}

		ArrayList<String> resolvedKeys = new ArrayList<String>();
		for (Map.Entry<String, Object> unresolvedEntry : unresolvedColumnMap.entrySet()) {
			String unresolvedKey = unresolvedEntry.getKey();
			if (unresolvedKey == null || unresolvedKey.contains(".")) {
				continue;
			}

			Object normalizedRefs = normalizeUpdateColumnRefs(unresolvedEntry.getValue());
			if (normalizedRefs == null) {
				continue;
			}

			targetColumns.put(unresolvedKey, normalizedRefs);
			resolvedKeys.add(unresolvedKey);
		}

		for (String resolvedKey : resolvedKeys) {
			unresolvedColumnMap.remove(resolvedKey);
		}
	}

	@SuppressWarnings("unchecked")
	private void resolveUpdateRhsUnqualifiedAssignmentColumnsToTargetTable(
			HashMap<String, Object> unresolvedColumnMap,
			HashMap<String, Object> tableCollection,
			HashMap<String, Object> targetTableCollection,
			HashMap<String, Object> tableAliasMap,
			HashMap<String, Object> visibleQuerySourceCollection,
			String updateTargetTableRef) {
		if (targetTableCollection == null
				|| updateTargetTableRef == null
				|| updateTargetTableRef.isBlank()) {
			return;
		}

		Object assignmentsObj = walker.symbolTable.get(MUMBLE_ASSIGNMENTS_KEY);
		if (!(assignmentsObj instanceof Map<?, ?> assignmentsMapObj) || assignmentsMapObj.isEmpty()) {
			return;
		}

		String resolvedTarget = walker.resolveAliasToTableName(updateTargetTableRef, tableAliasMap);
		if (resolvedTarget == null || resolvedTarget.isBlank()) {
			resolvedTarget = updateTargetTableRef;
		}
		if (resolvedTarget == null || resolvedTarget.isBlank()) {
			return;
		}

		String normalizedTarget = normalizeTableRef(resolvedTarget);

		Object targetTableObj = targetTableCollection.get(normalizedTarget);
		HashMap<String, Object> targetColumns;
		if (targetTableObj instanceof HashMap<?, ?> existingMapObj) {
			targetColumns = (HashMap<String, Object>) existingMapObj;
		} else {
			targetColumns = new HashMap<String, Object>();
			targetTableCollection.put(normalizedTarget, targetColumns);
		}

		for (Map.Entry<?, ?> assignmentEntry : ((Map<?, ?>) assignmentsMapObj).entrySet()) {
			Object rhsRefsObj = assignmentEntry.getValue();
			if (!(rhsRefsObj instanceof List<?> rhsRefs) || rhsRefs.isEmpty()) {
				continue;
			}

			for (Object rhsRefObj : rhsRefs) {
				if (!(rhsRefObj instanceof Map<?, ?>)) {
					continue;
				}

				String columnName = walker.extractReferenceNameFromInterfaceEntry(rhsRefObj);
				String tableRef = walker.extractReferenceTableRefFromInterfaceEntry(rhsRefObj);
				if (columnName == null || columnName.isBlank() || "*".equals(columnName)) {
					continue;
				}
				if (tableRef != null && !tableRef.isBlank() && !"*".equals(tableRef)) {
					continue;
				}

				if (containsKeyIgnoreCase(targetColumns, columnName)) {
					continue;
				}

				ArrayList<String> sourceRefs = collectUnqualifiedSourceReferences(
						columnName,
						tableCollection,
						visibleQuerySourceCollection,
						tableAliasMap);
				if (sourceRefs != null && !sourceRefs.isEmpty()) {
					continue;
				}

				// If UPDATE has exactly one FROM source table, treat unqualified RHS refs
				// as originating from that source before falling back to target table.
				if (tableCollection != null && tableCollection.size() == 1) {
					String singleFromTableRef = tableCollection.keySet().iterator().next();
					Object singleFromTableObj = tableCollection.get(singleFromTableRef);
					if (singleFromTableObj instanceof Map<?, ?> singleFromTableMapObj) {
						Map<String, Object> singleFromTableMap = (Map<String, Object>) singleFromTableMapObj;
						Object unresolvedValue = null;
						if (unresolvedColumnMap != null) {
							unresolvedValue = unresolvedColumnMap.remove(columnName);
						}
						Object normalizedRefs = normalizeUpdateColumnRefs(
								(unresolvedValue != null) ? unresolvedValue : rhsRefObj);
						if (normalizedRefs == null && rhsRefObj instanceof Map<?, ?> rhsRefMapObj) {
							ArrayList<Object> syntheticRefs = new ArrayList<Object>();
							syntheticRefs.add(new HashMap<String, Object>((Map<String, Object>) rhsRefMapObj));
							normalizedRefs = syntheticRefs;
						}
						if (normalizedRefs != null) {
							singleFromTableMap.put(columnName, normalizedRefs);
							continue;
						}
					}
				}

				Object unresolvedValue = null;
				if (unresolvedColumnMap != null) {
					unresolvedValue = unresolvedColumnMap.remove(columnName);
				}
				Object normalizedRefs = normalizeUpdateColumnRefs(
						(unresolvedValue != null) ? unresolvedValue : rhsRefObj);
				if (normalizedRefs == null && rhsRefObj instanceof Map<?, ?> rhsRefMapObj) {
					ArrayList<Object> syntheticRefs = new ArrayList<Object>();
					syntheticRefs.add(new HashMap<String, Object>((Map<String, Object>) rhsRefMapObj));
					normalizedRefs = syntheticRefs;
				}
				if (normalizedRefs == null) {
					continue;
				}

				targetColumns.put(columnName, normalizedRefs);
			}
		}
	}

	/**
	 * After the normal unresolved-column resolution pass, scan any entries that
	 * remain in {@code unresolvedColumnMap} whose qualifier resolves to the update
	 * target table (by name or alias).  Those columns — typically WHERE-clause
	 * filter columns or RHS predicand refs that explicitly reference the target —
	 * are moved directly into {@code targetTableCollection} so they appear in the
	 * final table dictionary under the target table rather than staying as
	 * unresolved.  Entries that do NOT match the target are left in place so the
	 * normal diagnostic machinery can flag them.
	 */
	@SuppressWarnings("unchecked")
	private void resolveRemainingQualifiedUnresolvedColumnsToTargetTable(
			HashMap<String, Object> unresolvedColumnMap,
			HashMap<String, Object> tableAliasMap,
			HashMap<String, Object> targetTableCollection,
			String updateTargetTableRef) {
		if (unresolvedColumnMap == null || unresolvedColumnMap.isEmpty()
				|| targetTableCollection == null
				|| updateTargetTableRef == null || updateTargetTableRef.isBlank()) {
			return;
		}

		String resolvedTarget = walker.resolveAliasToTableName(updateTargetTableRef, tableAliasMap);
		if (resolvedTarget == null || resolvedTarget.isBlank()) {
			resolvedTarget = updateTargetTableRef;
		}
		String normalizedTarget = normalizeTableRef(resolvedTarget);
		String normalizedAlias = normalizeTableRef(updateTargetTableRef);

		Object targetTableObj = targetTableCollection.get(normalizedTarget);
		HashMap<String, Object> targetColumns;
		if (targetTableObj instanceof HashMap<?, ?> existingMap) {
			targetColumns = (HashMap<String, Object>) existingMap;
		} else {
			targetColumns = new HashMap<String, Object>();
			targetTableCollection.put(normalizedTarget, targetColumns);
		}

		ArrayList<String> resolvedKeys = new ArrayList<String>();
		for (Map.Entry<String, Object> unresolvedEntry : unresolvedColumnMap.entrySet()) {
			String unresolvedKey = unresolvedEntry.getKey();
			if (unresolvedKey == null) {
				continue;
			}

			int separatorIndex = unresolvedKey.lastIndexOf('.');
			if (separatorIndex <= 0 || separatorIndex + 1 >= unresolvedKey.length()) {
				continue;
			}

			String qualifier = unresolvedKey.substring(0, separatorIndex);
			String columnName = unresolvedKey.substring(separatorIndex + 1);
			if (columnName.isBlank()) {
				continue;
			}

			String normalizedQualifier = normalizeTableRef(qualifier);
			String resolvedQualifier = walker.resolveAliasToTableName(normalizedQualifier, tableAliasMap);
			if (resolvedQualifier == null || resolvedQualifier.isBlank()) {
				resolvedQualifier = normalizedQualifier;
			}
			String normalizedResolvedQualifier = normalizeTableRef(resolvedQualifier);

			// Accept if qualifier equals the canonical target name or the alias
			if (!normalizedResolvedQualifier.equals(normalizedTarget)
					&& !normalizedQualifier.equals(normalizedAlias)
					&& !normalizedQualifier.equals(normalizedTarget)) {
				continue;
			}

			Object normalizedRefs = normalizeUpdateColumnRefs(unresolvedEntry.getValue());
			if (normalizedRefs == null) {
				continue;
			}

			targetColumns.put(columnName, normalizedRefs);
			resolvedKeys.add(unresolvedKey);
		}

		for (String resolvedKey : resolvedKeys) {
			unresolvedColumnMap.remove(resolvedKey);
		}
	}

	@SuppressWarnings("unchecked")
	private void mergeUpdateTargetAndLhsIntoTableDictionary(
			HashMap<String, Object> targetTableCollection,
			HashMap<String, Object> lhsUnresolvedColumnMap,
			HashMap<String, Object> tableCollection,
			HashMap<String, Object> tableAliasMap,
			String updateTargetTableRef) {
		if (tableCollection == null || updateTargetTableRef == null || updateTargetTableRef.isBlank()) {
			return;
		}

		String resolvedTarget = walker.resolveAliasToTableName(updateTargetTableRef, tableAliasMap);
		if (resolvedTarget == null || resolvedTarget.isBlank()) {
			resolvedTarget = updateTargetTableRef;
		}
		if (resolvedTarget == null || resolvedTarget.isBlank()) {
			return;
		}

		String normalizedTarget = normalizeTableRef(resolvedTarget);

		Object existingTargetObj = tableCollection.get(normalizedTarget);
		HashMap<String, Object> targetColumns;
		if (existingTargetObj instanceof HashMap<?, ?> existingTargetMapObj) {
			targetColumns = (HashMap<String, Object>) existingTargetMapObj;
		} else {
			targetColumns = new HashMap<String, Object>();
			tableCollection.put(normalizedTarget, targetColumns);
		}

		Object targetTableObj = targetTableCollection.get(normalizedTarget);
		if (targetTableObj instanceof Map<?, ?> targetTableMapObj) {
			targetColumns.putAll((Map<String, Object>) targetTableMapObj);
		}

		if (lhsUnresolvedColumnMap == null || lhsUnresolvedColumnMap.isEmpty()) {
			return;
		}

		for (Map.Entry<String, Object> lhsEntry : lhsUnresolvedColumnMap.entrySet()) {
			String lhsColumnName = extractLhsColumnName(lhsEntry.getKey(), lhsEntry.getValue());
			if (lhsColumnName == null || lhsColumnName.isBlank()) {
				continue;
			}

			Object normalizedRefs = normalizeUpdateColumnRefs(lhsEntry.getValue());
			if (normalizedRefs != null) {
				targetColumns.put(lhsColumnName, normalizedRefs);
			}
		}
	}

	@SuppressWarnings("unchecked")
	private String extractLhsColumnName(String lhsKey, Object lhsValue) {
		if (lhsValue instanceof Map<?, ?> lhsValueMapObj) {
			Map<String, Object> lhsValueMap = (Map<String, Object>) lhsValueMapObj;
			Object columnObj = lhsValueMap.get(MUMBLE_COLUMN_KEY);
			if (columnObj instanceof Map<?, ?> columnMapObj) {
				Object nameObj = ((Map<String, Object>) columnMapObj).get(MUMBLE_NAME_KEY);
				if (nameObj != null && !nameObj.toString().isBlank()) {
					return nameObj.toString();
				}
			}

			Object directNameObj = lhsValueMap.get(MUMBLE_NAME_KEY);
			if (directNameObj != null && !directNameObj.toString().isBlank()) {
				return directNameObj.toString();
			}
		}

		if (lhsKey == null || lhsKey.isBlank()) {
			return null;
		}

		int separatorIndex = lhsKey.lastIndexOf('.');
		if (separatorIndex >= 0 && separatorIndex + 1 < lhsKey.length()) {
			return lhsKey.substring(separatorIndex + 1);
		}

		return lhsKey;
	}

	@SuppressWarnings("unchecked")
	private void propagateUnqualifiedSelectStarToScopeTables(
			HashMap<String, Object> localInterface,
			HashMap<String, Object> localCurrentQueryDictionary,
			HashMap<String, Object> localTableCollection) {
		if (localInterface == null || localInterface.isEmpty()
				|| localCurrentQueryDictionary == null || localCurrentQueryDictionary.isEmpty()
				|| localTableCollection == null || localTableCollection.isEmpty()) {
			return;
		}

		Object starRefs = localCurrentQueryDictionary.get("*");
		if (starRefs == null) {
			return;
		}

		Object interfaceRefsObj = localInterface.get("*");
		if (!(interfaceRefsObj instanceof ArrayList<?> interfaceRefs) || interfaceRefs.isEmpty()) {
			return;
		}

		boolean hasUnqualifiedStarReference = false;
		for (Object interfaceRef : interfaceRefs) {
			String refName = walker.extractReferenceNameFromInterfaceEntry(interfaceRef);
			if (!"*".equals(refName)) {
				continue;
			}

			String refTable = walker.extractReferenceTableRefFromInterfaceEntry(interfaceRef);
			if (refTable == null || "*".equals(refTable)) {
				hasUnqualifiedStarReference = true;
				break;
			}
		}

		if (!hasUnqualifiedStarReference) {
			return;
		}

		for (Map.Entry<String, Object> tableEntry : localTableCollection.entrySet()) {
			String tableRef = tableEntry.getKey();
			if (tableRef == null
					|| tableRef.startsWith(MUMBLE_QUERY_KEY)
					|| tableRef.startsWith(MUMBLE_UNION_KEY)
					|| tableRef.startsWith(MUMBLE_INTERSECT_KEY)
					|| tableRef.startsWith(MUMBLE_VALUES_KEY)) {
				continue;
			}

			if (tableEntry.getValue() instanceof HashMap<?, ?> tableColumns) {
				walker.mergeResolvedColumnIntoDictionary((HashMap<String, Object>) tableColumns, "*", starRefs);
			}
		}
	}

	@SuppressWarnings("unchecked")
	private HashMap<String, Object> retainOnlyLocallyResolvableExplicitQualifiedUnknowns(
			HashMap<String, Object> explicitQualifiedUnknownEntries,
			HashMap<String, Object> localInterface,
			Object filtersList,
			HashMap<String, Object> tableAliasCollection,
			HashMap<String, Object> tableCollection,
			HashMap<String, Object> scopedQueryCollection,
			HashMap<String, Object> unresolvedColumnMap) {
		HashMap<String, Object> locallyResolvableEntries = new HashMap<String, Object>();
		if (explicitQualifiedUnknownEntries == null || explicitQualifiedUnknownEntries.isEmpty()) {
			return locallyResolvableEntries;
		}

		for (Map.Entry<String, Object> unknownEntry : explicitQualifiedUnknownEntries.entrySet()) {
			String unresolvedKey = unknownEntry.getKey();
			String explicitTableRef = resolveExplicitTableRefForUnknownEntry(
					unresolvedKey,
					localInterface,
					filtersList);
			if (explicitTableRef == null) {
				locallyResolvableEntries.put(unresolvedKey, unknownEntry.getValue());
				continue;
			}

			String resolvedTableRef = walker.resolveAliasToTableName(explicitTableRef, tableAliasCollection);
			String resolvedNonTableSourceRef = walker.resolveAliasToNonTableSourceQueryKey(explicitTableRef, scopedQueryCollection);
			HashMap<String, Object> indicatedTableDictionary = walker.getTableDictionaryForReference(
					resolvedTableRef,
					tableCollection);

			boolean hasLocalSource = resolvedNonTableSourceRef != null || indicatedTableDictionary != null;
			if (hasLocalSource) {
				locallyResolvableEntries.put(unresolvedKey, unknownEntry.getValue());
			} else if (unresolvedColumnMap != null) {
				unresolvedColumnMap.put(unresolvedKey, unknownEntry.getValue());
			}
		}

		return locallyResolvableEntries;
	}

	@SuppressWarnings("unchecked")
	private String resolveExplicitTableRefForUnknownEntry(
			String unresolvedKey,
			HashMap<String, Object> localInterface,
			Object filtersList) {
		if (unresolvedKey != null && unresolvedKey.contains(".")) {
			int dotIndex = unresolvedKey.indexOf('.');
			if (dotIndex > 0) {
				return unresolvedKey.substring(0, dotIndex);
			}
		}

		String columnName = unresolvedKey;
		if (columnName == null || columnName.isBlank()) {
			return null;
		}

		if (localInterface != null) {
			for (Object refsObj : localInterface.values()) {
				if (!(refsObj instanceof ArrayList<?> refs)) {
					continue;
				}
				for (Object refObj : refs) {
					String refName = walker.extractReferenceNameFromInterfaceEntry(refObj);
					String refTable = walker.extractReferenceTableRefFromInterfaceEntry(refObj);
					if (columnName.equals(refName) && refTable != null && !"*".equals(refTable)) {
						return refTable;
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
						&& columnName.equals(filterName)
						&& !"*".equals(filterTableRef)) {
					return filterTableRef;
				}
			}
		}

		return null;
	}

	private Object consumeUnqualifiedUnknownEntry(
			HashMap<String, Object> unresolvedColumnMap,
			String columnName) {
		if (unresolvedColumnMap == null || unresolvedColumnMap.isEmpty()
				|| columnName == null || columnName.isBlank()) {
			return null;
		}

		Object direct = unresolvedColumnMap.remove(columnName);
		if (direct != null) {
			return direct;
		}

		for (String key : new ArrayList<String>(unresolvedColumnMap.keySet())) {
			if (key == null || key.contains(".")) {
				continue;
			}
			if (key.equalsIgnoreCase(columnName)) {
				return unresolvedColumnMap.remove(key);
			}
		}

		return null;
	}

	private Object consumeQualifiedUnknownEntry(
			HashMap<String, Object> unresolvedColumnMap,
			String tableRef,
			String columnName,
			boolean includeCapturedFallback) {
		if (tableRef == null || tableRef.isBlank()
				|| columnName == null || columnName.isBlank()) {
			return null;
		}

		String directKey = tableRef + "." + columnName;
		if (unresolvedColumnMap != null && !unresolvedColumnMap.isEmpty()) {
			Object removedDirect = unresolvedColumnMap.remove(directKey);
			if (removedDirect != null) {
				return removedDirect;
			}

			for (String key : new ArrayList<String>(unresolvedColumnMap.keySet())) {
				if (key == null || !key.contains(".")) {
					continue;
				}
				int separatorIndex = key.lastIndexOf('.');
				if (separatorIndex <= 0 || separatorIndex + 1 >= key.length()) {
					continue;
				}
				String keyTableRef = key.substring(0, separatorIndex);
				String keyColumnName = key.substring(separatorIndex + 1);
				if (keyTableRef.equalsIgnoreCase(tableRef)
						&& keyColumnName.equalsIgnoreCase(columnName)) {
					return unresolvedColumnMap.remove(key);
				}
			}
		}

		if (includeCapturedFallback) {
			Object capturedEntry = walker.getCapturedQualifiedUnresolvedLocationEntry(directKey);
			if (capturedEntry != null) {
				return capturedEntry;
			}
			capturedEntry = walker.getCapturedQualifiedUnresolvedLocationEntry(tableRef.toLowerCase() + "." + columnName);
			if (capturedEntry != null) {
				return capturedEntry;
			}
			capturedEntry = walker.getCapturedQualifiedUnresolvedLocationEntry(tableRef + "." + columnName.toLowerCase());
			if (capturedEntry != null) {
				return capturedEntry;
			}
			capturedEntry = walker.getCapturedQualifiedUnresolvedLocationEntry(tableRef.toLowerCase() + "." + columnName.toLowerCase());
			if (capturedEntry != null) {
				return capturedEntry;
			}
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	private Integer[] resolveUnqualifiedReferenceLocation(
			String columnName,
			Object interfaceRefObj,
			HashMap<String, Object> unresolvedColumnMap,
			HashMap<String, Object> localCurrentQueryDictionary,
			String outputCol) {
		Integer[] refLocation = walker.getLineAndCharacterFromEntry(interfaceRefObj);
		if (refLocation[0] != null && refLocation[1] != null) {
			return refLocation;
		}

		Object unresolvedEntry = getUnqualifiedUnknownEntry(unresolvedColumnMap, columnName);
		if (unresolvedEntry != null) {
			refLocation = walker.getLineAndCharacterFromEntry(unresolvedEntry);
			if (refLocation[0] != null && refLocation[1] != null) {
				return refLocation;
			}
		}

		refLocation = (localCurrentQueryDictionary == null)
				? new Integer[] { null, null }
				: walker.getLineAndCharacterFromEntry(localCurrentQueryDictionary.get(outputCol));
		if (refLocation[0] != null && refLocation[1] != null) {
			return refLocation;
		}

		if (unresolvedEntry != null) {
			refLocation = walker.getFirstEntryLineAndCharacter((HashMap<String, Object>) unresolvedEntry);
			if (refLocation[0] != null && refLocation[1] != null) {
				return refLocation;
			}
		}

		if (localCurrentQueryDictionary != null) {
			return walker.getFirstEntryLineAndCharacter(localCurrentQueryDictionary);
		}

		return new Integer[] { null, null };
	}

	private Object getUnqualifiedUnknownEntry(
			HashMap<String, Object> unresolvedColumnMap,
			String columnName) {
		if (unresolvedColumnMap == null || unresolvedColumnMap.isEmpty()
				|| columnName == null || columnName.isBlank()) {
			return null;
		}

		Object direct = unresolvedColumnMap.get(columnName);
		if (direct != null) {
			return direct;
		}

		for (Map.Entry<String, Object> entry : unresolvedColumnMap.entrySet()) {
			String key = entry.getKey();
			if (key == null || key.contains(".")) {
				continue;
			}
			if (key.equalsIgnoreCase(columnName)) {
				return entry.getValue();
			}
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	private Object cloneReferenceWithResolvedTableRef(Object refObj, String resolvedSourceRef) {
		if (refObj == null || resolvedSourceRef == null || resolvedSourceRef.isBlank()) {
			return refObj;
		}
		if (!(refObj instanceof Map<?, ?>)) {
			return refObj;
		}

		Map<String, Object> refMap = (Map<String, Object>) refObj;
		if (refMap.get(MUMBLE_TABLE_REF_KEY) != null) {
			return refObj;
		}

		HashMap<String, Object> updatedRefMap = new HashMap<String, Object>(refMap);
		updatedRefMap.put(MUMBLE_TABLE_REF_KEY, resolvedSourceRef);
		return updatedRefMap;
	}

	@SuppressWarnings("unchecked")
	private void assignTableRefsForColumnReferenceList(
			Object columnListObj,
			HashMap<String, Object> unresolvedColumnMap,
			HashMap<String, Object> localCurrentQueryDictionary,
			HashMap<String, Object> localTableCollection,
			HashMap<String, Object> visibleQuerySourceCollection,
			HashMap<String, Object> localTableAliasMap) {
		if (!(columnListObj instanceof ArrayList<?>)) {
			return;
		}

		ArrayList<Object> columnRefs = (ArrayList<Object>) columnListObj;
		for (int index = 0; index < columnRefs.size(); index++) {
			Object columnRefObj = columnRefs.get(index);
			String columnName = walker.extractReferenceNameFromInterfaceEntry(columnRefObj);
			String tableRef = walker.extractReferenceTableRefFromInterfaceEntry(columnRefObj);
			String substitutionType = walker.extractSubstitutionTypeFromInterfaceEntry(columnRefObj);

			if (substitutionType != null
					&& (MUMBLE_COLUMN_KEY.equals(substitutionType) || MUMBLE_PREDICAND_KEY.equals(substitutionType))) {
				continue;
			}

			if (columnName == null || "*".equals(columnName) || tableRef != null) {
				// Skip explicit refs; explicit unresolved handling is covered by unresolved-map diagnostics.
				continue;
			}

			Integer[] refLocation = resolveUnqualifiedReferenceLocation(
					columnName,
					columnRefObj,
					unresolvedColumnMap,
					localCurrentQueryDictionary,
					columnName);

			ArrayList<String> sourceRefs = collectUnqualifiedSourceReferences(
					columnName,
					localTableCollection,
					visibleQuerySourceCollection,
					localTableAliasMap);

			if (sourceRefs.isEmpty()) {
				continue;
			} else if (sourceRefs.size() > 1) {
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

				walker.addWalkerDiagnostic(
						ParseDiagnostic.Severity.SEVERE_WARNING,
						diagCode,
						diagMessage,
						refLocation[0],
						refLocation[1],
						walker.getClass().getSimpleName(),
						null,
						columnName,
						true,
						"ast-walk",
						null,
						null);
			} else {
				String resolvedSourceRef = sourceRefs.get(0);
				columnRefs.set(index, cloneReferenceWithResolvedTableRef(columnRefObj, resolvedSourceRef));
			}
		}
	}

	private ArrayList<String> collectUnqualifiedSourceReferences(
			String columnName,
			HashMap<String, Object> tableCollection,
			HashMap<String, Object> queryCollection,
			HashMap<String, Object> tableAliasCollection) {
		LinkedHashSet<String> sourceCandidates = new LinkedHashSet<String>();

		ArrayList<String> dictionaryMatches = walker.collectSourceReferencesForColumn(
				columnName,
				tableCollection,
				queryCollection);
		sourceCandidates.addAll(dictionaryMatches);

		if (tableCollection != null && tableCollection.size() > 1) {
			for (String tableRef : tableCollection.keySet()) {
				sourceCandidates.add(tableRef);
			}
		}

		if (tableAliasCollection != null && tableAliasCollection.size() > 1) {
			for (Object mappedSourceObj : tableAliasCollection.values()) {
				if (!(mappedSourceObj instanceof String mappedSource)) {
					continue;
				}

				if (isQuerySourceReference(mappedSource)
						&& !querySourceCanProvideColumn(mappedSource, columnName, queryCollection)) {
					continue;
				}

				String sourceCandidate = mappedSource;
				if (!isQuerySourceReference(mappedSource)) {
					String resolvedTableRef = walker.resolveAliasToTableName(mappedSource, tableAliasCollection);
					if (resolvedTableRef != null && !resolvedTableRef.isBlank()) {
						sourceCandidate = resolvedTableRef;
					}

					// Restrict non-query contenders to the current scope's local table dictionary.
					if (walker.getTableDictionaryForReference(sourceCandidate, tableCollection) == null) {
						continue;
					}

					HashMap<String, Object> localTableDictionary = walker.getTableDictionaryForReference(
							sourceCandidate,
							tableCollection);
					if (localTableDictionary == null) {
						// Keep non-query sources constrained to the current query's table dictionary.
						continue;
					}
				}

				boolean duplicateIgnoringCase = false;
				for (String existingCandidate : sourceCandidates) {
					if (existingCandidate != null && existingCandidate.equalsIgnoreCase(sourceCandidate)) {
						duplicateIgnoringCase = true;
						break;
					}
				}
				if (!duplicateIgnoringCase) {
					sourceCandidates.add(sourceCandidate);
				}
			}
		}

		ArrayList<String> tableCandidates = new ArrayList<String>();
		ArrayList<String> queryCandidates = new ArrayList<String>();
		for (String sourceRef : sourceCandidates) {
			if (isQuerySourceReference(sourceRef)) {
				queryCandidates.add(sourceRef);
			} else {
				tableCandidates.add(sourceRef);
			}
		}

		if (queryCandidates.size() > 1) {
			if (tableCandidates.size() == 1) {
				ArrayList<String> resolved = new ArrayList<String>();
				resolved.add(tableCandidates.get(0));
				return resolved;
			}
		}

		return new ArrayList<String>(sourceCandidates);
	}

	private boolean hasUnqualifiedUnknownWithMultipleViableSources(
			HashMap<String, Object> unresolvedCollection,
			HashMap<String, Object> tableCollection,
			HashMap<String, Object> queryCollection,
			HashMap<String, Object> tableAliasCollection) {
		if (unresolvedCollection == null || unresolvedCollection.isEmpty()) {
			return false;
		}

		for (String unresolvedKey : unresolvedCollection.keySet()) {
			if (unresolvedKey == null || unresolvedKey.contains(".")) {
				continue;
			}

			ArrayList<String> sourceRefs = collectUnqualifiedSourceReferences(
					unresolvedKey,
					tableCollection,
					queryCollection,
					tableAliasCollection);
			if (sourceRefs.size() > 1) {
				return true;
			}
		}

		return false;
	}

	@SuppressWarnings("unchecked")
	private HashMap<String, Object> collectVisibleQuerySourceCollection(HashMap<String, Object> tableAliasCollection) {
		HashMap<String, Object> visibleQuerySources = new HashMap<String, Object>();
		if (tableAliasCollection == null || tableAliasCollection.isEmpty()) {
			return visibleQuerySources;
		}

		for (Object mappedSourceObj : tableAliasCollection.values()) {
			if (!(mappedSourceObj instanceof String mappedSource) || !isQuerySourceReference(mappedSource)) {
				continue;
			}

			Object querySourceObj = walker.symbolTable.get(mappedSource);
			if (querySourceObj instanceof HashMap<?, ?>) {
				visibleQuerySources.put(mappedSource, querySourceObj);
				continue;
			}

			Object queryDefinitionObj = walker.symbolTable.get("def_" + mappedSource);
			if (queryDefinitionObj instanceof HashMap<?, ?>) {
				visibleQuerySources.put(mappedSource, queryDefinitionObj);
				continue;
			}

			Object queryDictionaryObj = walker.queryColumnDictionaryMap.get(mappedSource);
			if (queryDictionaryObj instanceof HashMap<?, ?>) {
				visibleQuerySources.put(mappedSource, queryDictionaryObj);
			}
		}

		return visibleQuerySources;
	}

	@SuppressWarnings("unchecked")
	private boolean isWildcardBackedQueryCandidate(String queryRef, HashMap<String, Object> queryCollection) {
		if (queryRef == null || !isQuerySourceReference(queryRef)) {
			return false;
		}

		if (queryCollection != null) {
			Object queryObj = queryCollection.get(queryRef);
			if (queryObj instanceof Map<?, ?> queryMap
					&& ((Map<String, Object>) queryMap).containsKey("*")) {
				return true;
			}
		}

		Object queryDictionaryObj = walker.queryColumnDictionaryMap.get(queryRef);
		if (queryDictionaryObj instanceof Map<?, ?> queryDictionary
				&& ((Map<String, Object>) queryDictionary).containsKey("*")) {
			return true;
		}

		return hasWildcardInQueryOutputInterface(queryRef);
	}

	@SuppressWarnings("unchecked")
	private boolean querySourceCanProvideColumn(
			String queryRef,
			String columnName,
			HashMap<String, Object> queryCollection) {
		if (queryRef == null || !isQuerySourceReference(queryRef)) {
			return false;
		}

		if (isWildcardBackedQueryCandidate(queryRef, queryCollection)) {
			return true;
		}

		if (queryCollection != null) {
			Object queryObj = queryCollection.get(queryRef);
			if (queryObj instanceof Map<?, ?> queryMap
					&& containsKeyIgnoreCase((Map<String, Object>) queryMap, columnName)) {
				return true;
			}
		}

		Object queryDictionaryObj = walker.queryColumnDictionaryMap.get(queryRef);
		if (queryDictionaryObj instanceof Map<?, ?> queryDictionary
				&& containsKeyIgnoreCase((Map<String, Object>) queryDictionary, columnName)) {
			return true;
		}

		return hasColumnInQueryOutputInterface(queryRef, columnName);
	}

	private boolean isQuerySourceReference(String sourceRef) {
		if (sourceRef == null || sourceRef.isBlank()) {
			return false;
		}
		String normalizedSourceRef = sourceRef;
		if (normalizedSourceRef.startsWith("def_")) {
			normalizedSourceRef = normalizedSourceRef.substring("def_".length());
		}
		return normalizedSourceRef.startsWith(MUMBLE_QUERY_KEY)
				|| normalizedSourceRef.startsWith(MUMBLE_UNION_KEY)
				|| normalizedSourceRef.startsWith(MUMBLE_INTERSECT_KEY)
				|| normalizedSourceRef.startsWith(MUMBLE_VALUES_KEY)
				|| MUMBLE_VALUES_KEY.equals(normalizedSourceRef);
	}

	@SuppressWarnings("unchecked")
	private boolean hasColumnInQueryOutputInterface(String queryKey, String columnName) {
		if (queryKey == null || queryKey.isBlank() || columnName == null || columnName.isBlank()) {
			return false;
		}

		Object queryDefObj = getQueryDefinitionSymbol(queryKey);
		if (!(queryDefObj instanceof Map<?, ?> queryDefMap)) {
			return false;
		}

		Object interfaceObj = ((Map<String, Object>) queryDefMap).get(MUMBLE_INTERFACE_KEY);
		if (!(interfaceObj instanceof Map<?, ?> interfaceMap)) {
			return false;
		}

		return containsKeyIgnoreCase((Map<String, Object>) interfaceMap, columnName);
	}

	private boolean containsKeyIgnoreCase(Map<String, Object> map, String key) {
		if (map == null || map.isEmpty() || key == null) {
			return false;
		}

		if (map.containsKey(key)) {
			return true;
		}

		for (String existingKey : map.keySet()) {
			if (existingKey != null && existingKey.equalsIgnoreCase(key)) {
				return true;
			}
		}

		return false;
	}

	@SuppressWarnings("unchecked")
	private HashMap<String, Object> extractExplicitQualifiedUnknownEntries(
			HashMap<String, Object> unresolvedColumnMap,
			HashMap<String, Object> localInterface,
			Object filtersList,
			Object groupedByList,
			Object orderedByList) {
		HashMap<String, Object> explicitQualified = new HashMap<String, Object>();
		if (unresolvedColumnMap == null || unresolvedColumnMap.isEmpty()) {
			return explicitQualified;
		}

		HashSet<String> explicitQualifiedKeys = new HashSet<String>();

		if (localInterface != null) {
			for (Object refsObj : localInterface.values()) {
				if (!(refsObj instanceof ArrayList<?> refs)) {
					continue;
				}
				for (Object refObj : refs) {
					String refName = walker.extractReferenceNameFromInterfaceEntry(refObj);
					String refTable = walker.extractReferenceTableRefFromInterfaceEntry(refObj);
					if (refName != null && refTable != null && !"*".equals(refTable)) {
						String qualifiedKey = refTable + "." + refName;
						if (unresolvedColumnMap.containsKey(qualifiedKey)) {
							explicitQualifiedKeys.add(qualifiedKey);
						} else if (unresolvedColumnMap.containsKey(refName)) {
							explicitQualifiedKeys.add(refName);
						}
					}
				}
			}
		}

		collectExplicitQualifiedUnknownKeysFromRefList(explicitQualifiedKeys, unresolvedColumnMap, filtersList);
		collectExplicitQualifiedUnknownKeysFromRefList(explicitQualifiedKeys, unresolvedColumnMap, groupedByList);
		collectExplicitQualifiedUnknownKeysFromRefList(explicitQualifiedKeys, unresolvedColumnMap, orderedByList);

		for (String qualifiedKey : explicitQualifiedKeys) {
			Object removed = unresolvedColumnMap.remove(qualifiedKey);
			if (removed != null) {
				explicitQualified.put(qualifiedKey, removed);
			}
		}

		return explicitQualified;
	}

	@SuppressWarnings("unchecked")
	private void collectExplicitQualifiedUnknownKeysFromRefList(
			HashSet<String> explicitQualifiedKeys,
			HashMap<String, Object> unresolvedColumnMap,
			Object refListObj) {
		if (!(refListObj instanceof ArrayList<?> refs)) {
			return;
		}

		for (Object refObj : refs) {
			if (!(refObj instanceof Map<?, ?> refMap)) {
				continue;
			}
			Object refNameObj = refMap.get(MUMBLE_NAME_KEY);
			Object refTableRefObj = refMap.get(MUMBLE_TABLE_REF_KEY);
			if (refNameObj instanceof String refName
					&& refTableRefObj instanceof String refTableRef
					&& !"*".equals(refTableRef)) {
				String qualifiedKey = refTableRef + "." + refName;
				if (unresolvedColumnMap.containsKey(qualifiedKey)) {
					explicitQualifiedKeys.add(qualifiedKey);
				} else if (unresolvedColumnMap.containsKey(refName)) {
					explicitQualifiedKeys.add(refName);
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void emitExplicitQualifiedUnknownDiagnostics(
			HashMap<String, Object> explicitQualifiedUnknownEntries,
			HashMap<String, Object> localInterface,
			Object filtersList,
			HashMap<String, Object> tableAliasCollection,
			HashMap<String, Object> tableCollection,
			HashMap<String, Object> scopedQueryCollection,
			HashMap<String, Object> localCurrentQueryDictionary,
			HashMap<String, Object> unresolvedCollector) {
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
			String unresolvedKey = unknownEntry.getKey();
			String columnName = unresolvedKey;
			String tableRef = null;
			if (unresolvedKey != null && unresolvedKey.contains(".")) {
				tableRef = unresolvedKey.substring(0, unresolvedKey.lastIndexOf('.'));
				columnName = unresolvedKey.substring(unresolvedKey.lastIndexOf('.') + 1);
			}
			if (tableRef == null) {
				tableRef = explicitTableRefByColumn.get(columnName);
			}
			if (tableRef == null) {
				continue;
			}

			String resolvedTableRef = walker.resolveAliasToTableName(tableRef, tableAliasCollection);
			String resolvedNonTableSourceRef = walker.resolveAliasToNonTableSourceQueryKey(tableRef, scopedQueryCollection);
			boolean explicitQueryReference = resolvedNonTableSourceRef != null
					|| walker.isNonTableQuerySourceReference(resolvedTableRef);
			String allLocationsForEntry = walker.formatAllLocationsForEntry(unknownEntry.getValue());
			String allLocationsInline = walker.formatAllLocationsForEntryInline(unknownEntry.getValue());
			boolean hasMergedLocations = allLocationsForEntry != null
					&& allLocationsForEntry.startsWith("[")
					&& allLocationsForEntry.contains(",");

			if (explicitQueryReference) {
				String querySourceRef = (resolvedNonTableSourceRef != null)
						? resolvedNonTableSourceRef
						: resolvedTableRef;
				Object queryDictionaryObj = walker.queryColumnDictionaryMap.get(querySourceRef);
				boolean foundInQueryInterface = queryDictionaryObj instanceof Map<?, ?>
						&& containsKeyIgnoreCase((Map<String, Object>) queryDictionaryObj, columnName);
				if (!foundInQueryInterface && hasColumnInQueryOutputInterface(querySourceRef, columnName)) {
					foundInQueryInterface = true;
				}
				if (!foundInQueryInterface && hasWildcardInQueryOutputInterface(querySourceRef)) {
					foundInQueryInterface = true;
				}
				if (foundInQueryInterface) {
					mergeExplicitQualifiedUnknownIntoSourceQueryDictionary(
							querySourceRef,
							columnName,
							unknownEntry.getValue());
					continue;
				}
				if (!foundInQueryInterface) {
					Integer[] refLocation = walker.getLineAndCharacterFromEntry(unknownEntry.getValue());
					if (refLocation[0] == null || refLocation[1] == null) {
						refLocation = walker.getFirstEntryLineAndCharacter(explicitQualifiedUnknownEntries);
					}

					String diagCode = walker.getDiagnosticCode(SqlASTWalkerHelper.DIAG_SQL_QUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIAS);
					String diagTemplate = walker.getDiagnosticMessage(SqlASTWalkerHelper.DIAG_SQL_QUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIAS);
					String queryAliasRef = (resolvedTableRef != null && !resolvedTableRef.equals(tableRef)) ? tableRef : querySourceRef;
					String diagMessage = (diagTemplate == null)
							? String.format(
									"Qualified column '%s' at (l:%s c:%s) was not found in output interface of query alias '%s'.",
									columnName,
									refLocation[0],
									refLocation[1],
									queryAliasRef)
							: String.format(diagTemplate,
									columnName,
									refLocation[0],
									refLocation[1],
									queryAliasRef);
					if (hasMergedLocations) {
						diagMessage = diagMessage + " Locations: " + allLocationsForEntry;
					}

					walker.addWalkerFatal(
							diagCode,
							diagMessage,
							refLocation[0],
							refLocation[1],
							unresolvedKey);
				}
				continue;
			}

			String indicatedSourceRef = (resolvedNonTableSourceRef != null)
					? resolvedNonTableSourceRef
					: (resolvedTableRef != null ? resolvedTableRef : tableRef);

			HashMap<String, Object> indicatedTableDictionary = walker.getTableDictionaryForReference(
					resolvedTableRef,
					tableCollection);
			if (indicatedTableDictionary != null) {
				// Explicitly qualified table reference is valid when source table/alias exists.
				// Materialize the resolved column on that source so table dictionaries remain complete.
				walker.mergeResolvedColumnIntoDictionary(indicatedTableDictionary, columnName, unknownEntry.getValue());
				continue;
			}

			boolean isSubstitutionQualifiedReference = false;
			if (unknownEntry.getValue() instanceof Map<?, ?> unknownEntryMapObj) {
				Map<String, Object> unknownEntryMap = (Map<String, Object>) unknownEntryMapObj;
				Object columnObj = unknownEntryMap.get(MUMBLE_COLUMN_KEY);
				if (columnObj != null) {
					String substitutionType = walker.extractSubstitutionTypeFromInterfaceEntry(columnObj);
					isSubstitutionQualifiedReference = substitutionType != null
							&& (MUMBLE_COLUMN_KEY.equals(substitutionType)
									|| MUMBLE_PREDICAND_KEY.equals(substitutionType));
				}
			}

			if (!isSubstitutionQualifiedReference
					&& localCurrentQueryDictionary != null
					&& localCurrentQueryDictionary.containsKey(columnName)) {
				// Regular qualified columns selected into the interface are validated in the
				// downstream interface-resolution pass; avoid duplicate fatals here.
				continue;
			}

			Integer[] refLocation = walker.getLineAndCharacterFromEntry(unknownEntry.getValue());
			if (refLocation[0] == null || refLocation[1] == null) {
				refLocation = walker.getFirstEntryLineAndCharacter(explicitQualifiedUnknownEntries);
			}

			if (unresolvedCollector != null) {
				String qualifiedStorageKey = (unresolvedKey != null && unresolvedKey.contains("."))
						? unresolvedKey
						: tableRef + "." + columnName;
				HashMap<String, Object> singleEntry = new HashMap<String, Object>();
				singleEntry.put(qualifiedStorageKey, unknownEntry.getValue());
				walker.mergeUnknownEntries(unresolvedCollector, singleEntry);
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void mergeExplicitQualifiedUnknownIntoSourceQueryDictionary(
			String querySourceRef,
			String columnName,
			Object unknownEntryValue) {
		if (querySourceRef == null || querySourceRef.isBlank()
				|| columnName == null || columnName.isBlank()) {
			return;
		}

		String sourceQueryKey = querySourceRef;
		Object sourceDictionaryObj = walker.queryColumnDictionaryMap.get(sourceQueryKey);
		if (sourceDictionaryObj == null && sourceQueryKey.startsWith("def_")) {
			sourceQueryKey = sourceQueryKey.substring("def_".length());
			sourceDictionaryObj = walker.queryColumnDictionaryMap.get(sourceQueryKey);
		}
		if (sourceDictionaryObj == null) {
			String defSourceKey = sourceQueryKey.startsWith("def_") ? sourceQueryKey : "def_" + sourceQueryKey;
			sourceDictionaryObj = walker.queryColumnDictionaryMap.get(defSourceKey);
		}
		if (!(sourceDictionaryObj instanceof Map<?, ?> sourceDictionaryMapObj)) {
			return;
		}
		Map<String, Object> sourceQueryDictionary = (Map<String, Object>) sourceDictionaryMapObj;

		Object promotedRefs = unknownEntryValue;
		if (unknownEntryValue instanceof Map<?, ?> unknownEntryMapObj) {
			Object locations = ((Map<String, Object>) unknownEntryMapObj).get("locations");
			if (locations != null) {
				promotedRefs = locations;
			}
		}

		if (promotedRefs == null) {
			return;
		}

		if (promotedRefs instanceof ArrayList<?> promotedListObj) {
			promotedRefs = new ArrayList<Object>((ArrayList<Object>) promotedListObj);
		}

		String existingColumnKey = null;
		for (String existingKey : sourceQueryDictionary.keySet()) {
			if (existingKey != null && existingKey.equalsIgnoreCase(columnName)) {
				existingColumnKey = existingKey;
				break;
			}
		}

		if (existingColumnKey != null) {
			Object existingRefs = sourceQueryDictionary.get(existingColumnKey);
			sourceQueryDictionary.put(existingColumnKey, mergeReferenceCollections(existingRefs, promotedRefs));
		} else {
			sourceQueryDictionary.put(columnName, promotedRefs);
		}
	}

	private Object getQueryDefinitionSymbol(String queryKey) {
		if (queryKey == null || queryKey.isBlank()) {
			return null;
		}

		Object directObj = walker.symbolTable.get(queryKey);
		if (directObj instanceof Map<?, ?> directMap
				&& ((Map<?, ?>) directMap).containsKey(MUMBLE_INTERFACE_KEY)) {
			return directObj;
		}

		if (queryKey.startsWith("def_")) {
			return directObj;
		}

		return walker.symbolTable.get("def_" + queryKey);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void exitTable_primary(@NotNull SQLSelectParserParser.Table_primaryContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		int parentRuleIndex = ctx.getParent().getRuleIndex();

		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Integer parentStackLevel = walker.currentStackLevel(parentRuleIndex);

		Map<String, Object> subMap = walker.removeNodeMap(ruleIndex, stackLevel);
		if (subMap == null) {
			String sourceText = recoverVariableNameFromToken(ctx.getStart());
			String aliasText = ctx.getChildCount() >= 2 ? ctx.getChild(1).getText() : null;
			if (sourceText != null && sourceText.startsWith("<") && sourceText.endsWith(">")) {
				subMap = walker.makeRuleMap(ruleIndex);

				Map<String, Object> substitution = new HashMap<String, Object>();
				substitution.put(MUMBLE_NAME_KEY, sourceText);

				Map<String, Object> syntheticReference = new HashMap<String, Object>();
				syntheticReference.put(MUMBLE_SUBSTITUTION_KEY, substitution);
				subMap.put("1", syntheticReference);

				Map<String, Object> aliasMap = new HashMap<String, Object>();
				aliasMap.put(MUMBLE_ALIAS_KEY, aliasText);
				subMap.put("2", aliasMap);

				emitInvalidVariableDiagnostic(ctx.getStart(), sourceText);
			}
		}
		if (subMap == null) {
			throw new IllegalStateException("Missing AST node map for table_primary at: " + ctx.getText());
		}
		Object type = subMap.remove(ASTWALKER_RULE_TYPE_KEY);
		Map<String, Object> item;
		String alias = null;

		if (ctx.getChildCount() == 1) {
			item = (Map<String, Object>) subMap.remove("1");
			if (item.keySet().contains(MUMBLE_TABLE_KEY)) {
				item.put(MUMBLE_ALIAS_KEY, null);

				Object table = item.get(MUMBLE_TABLE_KEY);
				if (table != null) {
					String qualifiedTableReference = getQualifiedTableReference(item);
					// Table doesn't have an Alias, so it doesn't need to be collected in the Table ALias map
					// walker.collectTableAlias(alias, table);

					// However, we still need to collect the table reference in the AST 
					subMap.put(MUMBLE_TABLE_KEY, item);

					// And add the table header to the local Table_Dictionary
					walker.ensureTableDictionaryEntry(qualifiedTableReference);
				}
			} else if (item.keySet().contains(MUMBLE_SUBSTITUTION_KEY)) {
				item = walker.checkForSubstitutionVariable(item, "tuple");
				item.put(MUMBLE_ALIAS_KEY, null);
				subMap.put(MUMBLE_TABLE_KEY, item);
				Map<String, Object> substitution = (Map<String, Object>) item.get(MUMBLE_SUBSTITUTION_KEY);
				String tableName = resolveSubstitutionTableReference(substitution);
				if (tableName != null) {
					walker.ensureTableDictionaryEntry(tableName);
				}
			} else { // VALUES STATEMENT can only happen in this instance
				subMap.putAll(item);

				// exitValues_statement always stores a Map under MUMBLE_VALUES_KEY
				Object valuesObject = item.get(MUMBLE_VALUES_KEY);
				if (valuesObject instanceof Map<?, ?> valuesMapObj) {
					Map<String, Object> valuesMap = (Map<String, Object>) valuesMapObj;
					String valuesScopeKey = findTopLevelValuesScopeKey();
					wrapValuesScopeAsDefinition(valuesScopeKey);
					Object aliasObj = valuesMap.get(MUMBLE_ALIAS_KEY);
					if (aliasObj instanceof String valuesAlias) {
						addCurrentScopeValuesAliasMapping(valuesAlias, valuesScopeKey);
						walker.collectTableAlias(valuesAlias, valuesScopeKey);
					}
				}

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
				String tableRef = getQualifiedTableReference(reference);
				item.putAll(reference);
				walker.collectTableAlias(alias, tableRef);
				walker.ensureTableDictionaryEntry(tableRef);
				
			} else if (reference.containsKey(MUMBLE_SUBSTITUTION_KEY)) {
				// Check for Substitution Variable
				item.putAll(reference);
				// Collect Symbol Table Reference
				Map<String, Object> substitution = (Map<String, Object>) reference.get(MUMBLE_SUBSTITUTION_KEY);
				String tableName = resolveSubstitutionTableReference(substitution);
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

	private String findTopLevelValuesScopeKey() {
		String selectedKey = null;
		int highestIndex = -1;
		for (String symbolKey : walker.symbolTable.keySet()) {
			if (symbolKey == null) {
				continue;
			}

			String normalizedValuesKey = null;
			if (symbolKey.startsWith(MUMBLE_VALUES_KEY)) {
				normalizedValuesKey = symbolKey;
			} else if (symbolKey.startsWith("def_" + MUMBLE_VALUES_KEY)) {
				normalizedValuesKey = symbolKey.substring("def_".length());
			}

			if (normalizedValuesKey == null) {
				continue;
			}
			String suffix = normalizedValuesKey.substring(MUMBLE_VALUES_KEY.length());
			if (suffix.isEmpty()) {
				continue;
			}
			int keyIndex;
			try {
				keyIndex = Integer.parseInt(suffix);
			} catch (NumberFormatException ex) {
				continue;
			}
			if (keyIndex > highestIndex) {
				highestIndex = keyIndex;
				selectedKey = normalizedValuesKey;
			}
		}
		return selectedKey;
	}

	@SuppressWarnings("unchecked")
	private void wrapValuesScopeAsDefinition(String valuesScopeKey) {
		if (valuesScopeKey == null || valuesScopeKey.isBlank()) {
			return;
		}

		String definitionKey = "def_" + valuesScopeKey;
		if (walker.symbolTable.containsKey(definitionKey)) {
			return;
		}

		Object valuesScopeObj = walker.symbolTable.remove(valuesScopeKey);
		if (valuesScopeObj instanceof Map<?, ?>) {
			walker.symbolTable.put(definitionKey, valuesScopeObj);
		}
	}

	@SuppressWarnings("unchecked")
	private void addCurrentScopeValuesAliasMapping(String alias, String valuesScopeKey) {
		if (alias == null || alias.isBlank() || valuesScopeKey == null || valuesScopeKey.isBlank()) {
			return;
		}

		Object queryDictionaryObj = walker.symbolTable.get(MUMBLE_QUERY_DICTIONARY_KEY);
		HashMap<String, Object> queryDictionary;
		if (queryDictionaryObj instanceof HashMap<?, ?>) {
			queryDictionary = (HashMap<String, Object>) queryDictionaryObj;
		} else {
			queryDictionary = new HashMap<String, Object>();
			walker.symbolTable.put(MUMBLE_QUERY_DICTIONARY_KEY, queryDictionary);
		}

		Object sourceAliasNodeObj = queryDictionary.get(valuesScopeKey);
		HashMap<String, Object> sourceAliasNode;
		if (sourceAliasNodeObj instanceof HashMap<?, ?>) {
			sourceAliasNode = (HashMap<String, Object>) sourceAliasNodeObj;
		} else {
			sourceAliasNode = new HashMap<String, Object>();
			queryDictionary.put(valuesScopeKey, sourceAliasNode);
		}
		sourceAliasNode.put(alias, valuesScopeKey);
	}

	@SuppressWarnings("unchecked")
	private void sanitizeQueryDictionaryForGlobalExport(HashMap<String, Object> queryDictionary) {
		if (queryDictionary == null || queryDictionary.isEmpty()) {
			return;
		}

		ArrayList<String> transientKeysToRemove = new ArrayList<String>();
		for (Map.Entry<String, Object> entry : queryDictionary.entrySet()) {
			String entryKey = entry.getKey();
			Object entryValue = entry.getValue();
			if (entryKey == null || !(entryValue instanceof Map<?, ?> entryMapObj)) {
				continue;
			}

			if (!(entryKey.startsWith(MUMBLE_QUERY_KEY)
					|| entryKey.startsWith(MUMBLE_UNION_KEY)
					|| entryKey.startsWith(MUMBLE_INTERSECT_KEY)
					|| entryKey.startsWith(MUMBLE_VALUES_KEY))) {
				continue;
			}

			Map<String, Object> entryMap = (Map<String, Object>) entryMapObj;
			if (entryMap.isEmpty()) {
				transientKeysToRemove.add(entryKey);
				continue;
			}

			boolean onlyAliasStringMappings = true;
			for (Object subValue : entryMap.values()) {
				if (!(subValue instanceof String)) {
					onlyAliasStringMappings = false;
					break;
				}
			}

			if (onlyAliasStringMappings) {
				transientKeysToRemove.add(entryKey);
			}
		}

		for (String transientKey : transientKeysToRemove) {
			queryDictionary.remove(transientKey);
		}
	}

	@Override
	public void enterInsert_source_primary(@NotNull SQLSelectParserParser.Insert_source_primaryContext ctx) {
		walker.pushSymbolTable();
	}

	@Override
	public void exitInsert_source_primary(@NotNull SQLSelectParserParser.Insert_source_primaryContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		int parentRuleIndex = ctx.getParent().getRuleIndex();

		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Integer parentStackLevel = walker.currentStackLevel(parentRuleIndex);

		Map<String, Object> subMap = walker.removeNodeMap(ruleIndex, stackLevel);
		subMap.remove(ASTWALKER_RULE_TYPE_KEY);
		Map<String, Object> reference = walker.checkForSubstitutionVariable((Map<String, Object>) subMap.remove("1"),
					"tuple");

		Map<String, Object> sourceNode = new HashMap<String, Object>();

		if (reference.containsKey(MUMBLE_VALUES_KEY)) {
			sourceNode.putAll(reference);

		} else if (reference.containsKey(MUMBLE_SUBSTITUTION_KEY)) {
			// Insert-source substitution variables are opaque query sources, not table sources.
			// Do not register them in table_dictionary.
			sourceNode.putAll(reference);

		} else { // then it's a query, add it to the tree no matter what kind of query it is
			// Query-like source: keep AST as-is; symbol scope normalization is handled below.
			sourceNode.putAll(reference);
		}

		subMap.put(MUMBLE_FROM_KEY, sourceNode);

		HashMap<String, Object> symbols = walker.symbolTable;
		String queryRefKey = getSubqueryReferenceKey(symbols);
		if (queryRefKey != null && !queryRefKey.startsWith("def_")) {
			Object queryDefinitionObj = symbols.get(queryRefKey);
			if (queryDefinitionObj instanceof Map<?, ?> queryDefinitionMapObj) {
				((Map<String, Object>) queryDefinitionMapObj).remove(TEMP_INSERT_SOURCE_SELECT_SEQUENCE_KEY);
			}
			symbols.put("def_" + queryRefKey, symbols.remove(queryRefKey));
		}
		walker.popSymbolTablePutAll(symbols);

		walker.addToParent(parentRuleIndex, parentStackLevel, subMap);
		walker.showTrace(walker.parseTrace, "INSERT SOURCE PRIMARY: " + subMap);
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
			String tableRef = getQualifiedTableReference(item);
			walker.ensureTableDictionaryEntry(tableRef);
			// Table reference needs an AST Key added to it
			subMap.put(MUMBLE_TABLE_KEY, item);

		} else if (item.containsKey(MUMBLE_SUBSTITUTION_KEY)) {
			// Substitution Variable
			Map<String, Object> substitution = (Map<String, Object>) item.get(MUMBLE_SUBSTITUTION_KEY);
			// Collect Symbol Table Reference
			String name = resolveSubstitutionTableReference(substitution);
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
			if (walker.currentStackLevel(SQLSelectParserParser.RULE_insert_source_primary) != null) {
				pruneInsertSourceSequenceFromNestedDefinitions(query);
			} else {
				query.remove(TEMP_INSERT_SOURCE_SELECT_SEQUENCE_KEY);
			}
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

	@SuppressWarnings("unchecked")
	private void pruneInsertSourceSequenceFromNestedDefinitions(Map<String, Object> scopeMap) {
		if (scopeMap == null || scopeMap.isEmpty()) {
			return;
		}

		Object currentSequence = scopeMap.get(TEMP_INSERT_SOURCE_SELECT_SEQUENCE_KEY);
		pruneInsertSourceSequenceRecursive(scopeMap);
		if (currentSequence != null) {
			scopeMap.put(TEMP_INSERT_SOURCE_SELECT_SEQUENCE_KEY, currentSequence);
		}
	}

	@SuppressWarnings("unchecked")
	private void pruneInsertSourceSequenceRecursive(Map<String, Object> map) {
		if (map == null || map.isEmpty()) {
			return;
		}

		map.remove(TEMP_INSERT_SOURCE_SELECT_SEQUENCE_KEY);

		for (Object value : map.values()) {
			if (value instanceof Map<?, ?> valueMapObj) {
				pruneInsertSourceSequenceRecursive((Map<String, Object>) valueMapObj);
			}
		}
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

			if (collectInsertTargetColumnReferenceFromColumnList(subMap, ctx)) {
				walker.showTrace(walker.parseTrace, "Insert target column reference: " + subMap);
				return;
			}

			// Capture  walker.symbolTable entry
			walker.collectUnresolvedColumnReference(tableRefKey, columnSubTree, ctx.getStart());
		}
		walker.showTrace(walker.parseTrace, "Column Reference: " + subMap);
	}

	@SuppressWarnings("unchecked")
	private boolean collectInsertTargetColumnReferenceFromColumnList(
			Map<String, Object> columnReferenceSubMap,
			SQLSelectParserParser.Column_referenceContext ctx) {
		if (!isInsertTargetColumnReferenceListContext()) {
			return false;
		}

		String targetTableRef = resolveCurrentInsertTargetTableReference();
		if (targetTableRef == null || targetTableRef.isBlank()) {
			return false;
		}

		String columnName = extractInsertColumnNameFromEntry(columnReferenceSubMap);
		if (columnName == null || columnName.isBlank()) {
			return false;
		}

		HashMap<String, Object> currentTableDictionary = walker.getCurrentTableDictionary();
		HashMap<String, Object> targetTableDictionary = ensureTableDictionaryEntry(currentTableDictionary, targetTableRef);

		ArrayList<String> columnRefs;
		Object existingRefObj = targetTableDictionary.get(columnName);
		if (existingRefObj instanceof ArrayList<?>) {
			columnRefs = (ArrayList<String>) existingRefObj;
		} else {
			columnRefs = new ArrayList<String>();
			targetTableDictionary.put(columnName, columnRefs);
		}

		String tokenRef = (ctx.getStart() == null) ? null : ctx.getStart().toString();
		if (tokenRef != null && !columnRefs.contains(tokenRef)) {
			columnRefs.add(tokenRef);
		}

		if (tokenRef != null) {
			addAliasTokensObject(columnName, tokenRef);
		}

		return true;
	}

	private boolean isInsertTargetColumnReferenceListContext() {
		return walker.currentStackLevel(SQLSelectParserParser.RULE_snowflake_insert) != null
				&& walker.currentStackLevel(SQLSelectParserParser.RULE_column_reference_list) != null
				&& walker.currentStackLevel(SQLSelectParserParser.RULE_insert_source_primary) == null;
	}

	@SuppressWarnings("unchecked")
	private String resolveCurrentInsertTargetTableReference() {
		HashMap<String, Object> currentTableDictionary = walker.getCurrentTableDictionary();
		if (currentTableDictionary == null || currentTableDictionary.isEmpty()) {
			return null;
		}

		for (Map.Entry<String, Object> tableEntry : currentTableDictionary.entrySet()) {
			String tableRef = tableEntry.getKey();
			if (tableRef == null
					|| MUMBLE_TABLE_ALIAS_KEY.equals(tableRef)
					|| MUMBLE_TABLE_DICTIONARY_KEY.equals(tableRef)) {
				continue;
			}
			if (tableEntry.getValue() instanceof Map<?, ?>) {
				return tableRef;
			}
		}

		return null;
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
			walker.collectUnresolvedColumnReference(tableRefKey, columnSubTree, ctx.getStart());
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

		// Handle sign numeric_primary alternative (e.g. -(expr) or -column)
		if (subMap.size() >= 3 && subMap.get("1") instanceof String) {
			String sign = (String) subMap.remove("1");
			if ("-".equals(sign)) {
				Map<String, Object> left = new HashMap<String, Object>();
				left.put(MUMBLE_LITERAL_KEY, "-1");
				Map<String, Object> calcItem = new HashMap<String, Object>();
				calcItem.put(MUMBLE_LEFT_FACTOR_KEY, left);
				calcItem.put(MUMBLE_OPERATOR_KEY, "*");
				calcItem.put(MUMBLE_RIGHT_FACTOR_KEY, subMap.remove("2"));
				Map<String, Object> calc = new HashMap<String, Object>();
				calc.put(MUMBLE_CALCULATION_KEY, calcItem);
				subMap.put("1", calc);
			} else {
				subMap.put("1", subMap.remove("2"));
			}
		}

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
		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Map<String, Object> subMap = walker.getNodeMap(ruleIndex, stackLevel);
		Object type = subMap.remove(ASTWALKER_RULE_TYPE_KEY);

		if (subMap.size() == 1) {
			walker.handleOneChild(ruleIndex);
			return;
		}

		if (subMap.size() >= 2) {
			Object value = subMap.remove("1");
			Object maybeType = subMap.remove("3");
			if (maybeType == null) {
				maybeType = subMap.remove("2");
			}

			Map<String, Object> item = new HashMap<String, Object>();
			item.put(MUMBLE_FUNCTION_NAME_KEY, "cast");
			item.put(MUMBLE_TYPE_KEY, "CAST");
			item.put(MUMBLE_VALUE_KEY, value);
			item.put(MUMBLE_DATATYPE_KEY, maybeType);

			subMap.clear();
			subMap.put(MUMBLE_FUNCTION_KEY, item);
			walker.showTrace(walker.parseTrace, "Inline CAST Function: " + subMap);
			return;
		}

		walker.showTrace(walker.parseTrace, "Wrong number of entries: " + subMap);
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

		if (subMap.size() >= 1) {
			for (Object key : new HashMap<String, Object>(subMap).keySet()) {
				if (subMap.get(key) instanceof Map<?, ?>) {
					subMap.remove(key);
				}
			}
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
	public void exitJinja_name(@NotNull SQLSelectParserParser.Jinja_nameContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Map<String, Object> subMap = walker.removeNodeMap(ruleIndex, stackLevel);

		String nameText = null;
		if (ctx.identifier() != null) {
			nameText = ctx.identifier().getText();
		} else {
			nameText = ctx.getText();
		}

		if (subMap == null) {
			subMap = walker.makeRuleMap(ruleIndex);
		}

		subMap.clear();
		subMap.put(MUMBLE_NAME_KEY, nameText);
		walker.collect(ruleIndex, stackLevel, subMap);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void exitJinja_arg(@NotNull SQLSelectParserParser.Jinja_argContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		int parentRuleIndex = ctx.getParent().getRuleIndex();
		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Integer parentStackLevel = walker.currentStackLevel(parentRuleIndex);

		Map<String, Object> subMap = walker.removeNodeMap(ruleIndex, stackLevel);
		if (subMap == null) {
			subMap = walker.makeRuleMap(ruleIndex);
		}
		subMap.remove(ASTWALKER_RULE_TYPE_KEY);

		if (ctx.getChildCount() == 1) {
			Object rawValue = ctx.getText();
			subMap.clear();
			subMap.put(MUMBLE_ARGUMENT_KEY, rawValue);
		} else if (ctx.getChildCount() == 3) {
			Object kwNameObj = subMap.containsKey("1") ? subMap.get("1") : (ctx.kw_name == null ? null : ctx.kw_name.getText());
			Object rawValue = ctx.getChild(2).getText();
			String kwName = kwNameObj == null ? null : kwNameObj.toString();
			Map<String, Object> literal = asLiteralMap(rawValue);

			subMap.clear();
			if (kwName != null) {
				HashMap<String, Object> kwArg = new HashMap<String, Object>();
				kwArg.put(kwName, literal);
				subMap.put(MUMBLE_ARGUMENT_KEY, kwArg);
			}
		}

		walker.addToParent(parentRuleIndex, parentStackLevel, subMap);
	}

	@Override
	public void exitJinja_arg_list(@NotNull SQLSelectParserParser.Jinja_arg_listContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Map<String, Object> subMap = getOrCreateRuleMap(ruleIndex, stackLevel);
		subMap.remove(ASTWALKER_RULE_TYPE_KEY);

		HashMap<String, Object> orderedArgs = new HashMap<String, Object>();
		int outputIndex = 1;
		for (int inputIndex = 1; subMap.containsKey(String.valueOf(inputIndex)); inputIndex++) {
			Object value = subMap.get(String.valueOf(inputIndex));
			if (",".equals(value)) {
				continue;
			}

			Object argValue = value;
			if (value instanceof Map<?, ?>) {
				@SuppressWarnings("unchecked")
				Map<String, Object> argMap = (Map<String, Object>) value;
				if (argMap.containsKey(MUMBLE_ARGUMENT_KEY)) {
					argValue = argMap.get(MUMBLE_ARGUMENT_KEY);
				}
			}

			orderedArgs.put(String.valueOf(outputIndex++), argValue);
		}

		subMap.clear();
		subMap.putAll(orderedArgs);
	}

	@Override
	public void exitJinja_variable_access(@NotNull SQLSelectParserParser.Jinja_variable_accessContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Map<String, Object> subMap = getOrCreateRuleMap(ruleIndex, stackLevel);
		subMap.remove(ASTWALKER_RULE_TYPE_KEY);

		HashMap<String, Object> orderedParts = new HashMap<String, Object>();
		int outputIndex = 1;
		for (int inputIndex = 1; subMap.containsKey(String.valueOf(inputIndex)); inputIndex++) {
			Object value = subMap.get(String.valueOf(inputIndex));
			if (".".equals(value)) {
				continue;
			}

			Object nameValue = value;
			if (value instanceof Map<?, ?>) {
				@SuppressWarnings("unchecked")
				Map<String, Object> nameMap = (Map<String, Object>) value;
				if (nameMap.containsKey(MUMBLE_NAME_KEY)) {
					nameValue = nameMap.get(MUMBLE_NAME_KEY);
				}
			}

			orderedParts.put(String.valueOf(outputIndex++), nameValue);
		}

		subMap.clear();
		subMap.putAll(orderedParts);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void exitJinja_function_call(@NotNull SQLSelectParserParser.Jinja_function_callContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Map<String, Object> subMap = getOrCreateRuleMap(ruleIndex, stackLevel);
		subMap.remove(ASTWALKER_RULE_TYPE_KEY);

		String functionName;
		if (ctx.func_name != null) {
			functionName = ctx.func_name.getText();
		} else {
			functionName = ctx.object_name.getText() + "." + ctx.method_name.getText();
		}

		Map<String, Object> argList = null;
		for (int inputIndex = 1; subMap.containsKey(String.valueOf(inputIndex)); inputIndex++) {
			Object value = subMap.get(String.valueOf(inputIndex));
			if (value instanceof Map<?, ?>) {
				argList = (Map<String, Object>) value;
			}
		}

		HashMap<String, Object> args = new HashMap<String, Object>();
		HashMap<String, Object> kwargs = new HashMap<String, Object>();
		int argIndex = 1;
		if (argList != null) {
			for (int i = 1; argList.containsKey(String.valueOf(i)); i++) {
				Object argObj = argList.get(String.valueOf(i));
				if (argObj instanceof Map<?, ?>) {
					Map<String, Object> argMap = (Map<String, Object>) argObj;
					if (argMap.containsKey(MUMBLE_LITERAL_KEY)) {
						args.put(String.valueOf(argIndex++), argMap);
					} else if (argMap.size() == 1) {
						Map.Entry<String, Object> kwEntry = argMap.entrySet().iterator().next();
						kwargs.put(kwEntry.getKey(), kwEntry.getValue());
					} else {
						args.put(String.valueOf(argIndex++), argMap);
					}
				} else {
					args.put(String.valueOf(argIndex++), asLiteralMap(argObj));
				}
			}
		}

		subMap.clear();
		subMap.put(MUMBLE_FUNCTION_NAME_KEY, functionName);
		subMap.put(MUMBLE_PARAMETERS_KEY, args);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void exitJinja_identifier(@NotNull SQLSelectParserParser.Jinja_identifierContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Map<String, Object> subMap = getOrCreateRuleMap(ruleIndex, stackLevel);
		subMap.remove(ASTWALKER_RULE_TYPE_KEY);

		Map<String, Object> jinjaNode = new HashMap<String, Object>();
		for (int inputIndex = 1; subMap.containsKey(String.valueOf(inputIndex)); inputIndex++) {
			Object value = subMap.get(String.valueOf(inputIndex));
			if (value instanceof Map<?, ?>) {
				jinjaNode = (Map<String, Object>) value;
				break;
			}
		}

		HashMap<String, Object> parts = new HashMap<String, Object>();
		if (ctx.jinja_variable_access() != null) {
			parts.put(MUMBLE_JINJA_VARIABLE_KEY, jinjaNode);
		} else {
			parts.put(MUMBLE_JINJA_TABLE_KEY, jinjaNode);
		}

		HashMap<String, Object> substitution = new HashMap<String, Object>();
		substitution.put(MUMBLE_NAME_KEY, ctx.start.getInputStream().getText(new Interval(ctx.start.getStartIndex(), ctx.stop.getStopIndex())));
		substitution.put(MUMBLE_PARTS_KEY, parts);

		subMap.clear();
		subMap.put(MUMBLE_SUBSTITUTION_KEY, substitution);
	}

	@Override
	public void exitWhere_clause(@NotNull SQLSelectParserParser.Where_clauseContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		int stackLevel = walker.currentStackLevel(ruleIndex);
		Map<String, Object> subMap = walker.getNodeMap(ruleIndex, stackLevel);
		captureClauseDependencies(subMap, MUMBLE_FILTERS_KEY);
		walker.handlePushDown(ruleIndex);
	}

	@Override
	public void exitQualify_clause(@NotNull SQLSelectParserParser.Qualify_clauseContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		int stackLevel = walker.currentStackLevel(ruleIndex);
		Map<String, Object> subMap = walker.getNodeMap(ruleIndex, stackLevel);
		captureClauseDependencies(subMap, MUMBLE_FILTERS_KEY);
		walker.handlePushDown(ruleIndex);
	}

	@Override
	public void exitGroupby_clause(@NotNull SQLSelectParserParser.Groupby_clauseContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		int stackLevel = walker.currentStackLevel(ruleIndex);
		Map<String, Object> subMap = walker.getNodeMap(ruleIndex, stackLevel);
		captureClauseDependencies(subMap, MUMBLE_GROUPED_BY_KEY);
		walker.handlePushDown(ruleIndex);
	}

	@Override
	public void exitHaving_clause(@NotNull SQLSelectParserParser.Having_clauseContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		int stackLevel = walker.currentStackLevel(ruleIndex);

		Map<String, Object> subMap = walker.getNodeMap(ruleIndex, stackLevel);
		captureClauseDependencies(subMap, MUMBLE_FILTERS_KEY);
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
		} else {
			// Normal order by clause
			Integer stackLevel = walker.currentStackLevel(ruleIndex);
			Map<String, Object> subMap = walker.getNodeMap(ruleIndex, stackLevel);
			captureClauseDependencies(subMap, MUMBLE_ORDERED_BY_KEY);
			walker.handlePushDown(ruleIndex);
		}
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
		flattenSubTreeForClauseColumns((HashMap<String, Object>) subMap, flatList);

		// Add the flatList back into the SymbolTree as the Object part of the filter entry. Use the MUMBLE_FILTERS_KEY as the key
		walker.symbolTable.put(MUMBLE_FILTERS_KEY, flatList);

		// NOW handle the push down of the search condition as normal
		walker.handleOneChild(ruleIndex);
	}

	@SuppressWarnings("unchecked")
	private void captureClauseDependencies(Map<String, Object> clauseSubMap, String symbolTableKey) {
		Object existing = walker.symbolTable.remove(symbolTableKey);
		ArrayList<Object> flatList;
		if (existing instanceof ArrayList<?>) {
			flatList = (ArrayList<Object>) existing;
		} else {
			flatList = new ArrayList<Object>();
		}

		if (clauseSubMap instanceof HashMap<?, ?>) {
			flattenSubTreeForClauseColumns((HashMap<String, Object>) clauseSubMap, flatList);
		}

		walker.symbolTable.put(symbolTableKey, flatList);
	}

	// Standardize the filters reference map into a flat map of column references and not the entire AST subtree
	// This is a recursive function that traverses the item subtree until it finds column references or substitution variables, 
	// which it adds to the column list with the alias as the key

	private void flattenSubTreeForClauseColumns(HashMap<String, Object> subTree, ArrayList<Object> columnList) {
		if (subTree == null) {
			return;
		}

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
		} else if (isQueryBackedSelectItemReference(subTree)) {
			String queryReference = resolveQueryReferenceFromSubTree(subTree);
			if (isQuerySourceReference(queryReference)) {
				HashMap<String, Object> queryEntry = new HashMap<String, Object>();
				queryEntry.put(MUMBLE_QUERY_KEY, queryReference);
				if (!columnList.contains(queryEntry)) {
					columnList.add(queryEntry);
				}
			}
			subTree.remove(MUMBLE_QUERY_KEY);
		} else {
			for (Object value : subTree.values()) {
				if (value instanceof HashMap<?, ?> valueMapObj) {
					flattenSubTreeForClauseColumns((HashMap<String, Object>) valueMapObj, columnList);
				} else if (value instanceof ArrayList<?> valueListObj) {
					for (Object listItem : (ArrayList<Object>) valueListObj) {
						if (listItem instanceof HashMap<?, ?> listMapObj) {
							flattenSubTreeForClauseColumns((HashMap<String, Object>) listMapObj, columnList);
						}
					}
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
			// Subquery case: inject the subquery's local symbol table under IN predicate.
			// Extract the singular query reference key from the local symbol table (e.g., "query0")
			String queryRefKey = getSubqueryReferenceKey(symbols);
			if (queryRefKey == null && symbols != null && !symbols.isEmpty()) {
				queryRefKey = symbols.keySet().iterator().next();
			}
			annotateSubqueryReference(reference, queryRefKey);

			// Capture Query Column Dictionary for this level
// ***			walker.queryColumnDictionaryMap.put(key, walker.symbolTable.remove(CURRENT_QUERY_COLUMN_DICTIONARY));

			// Modify the symbol table by removing the query and adding it back with the def prefix to avoid conflicts
			if (queryRefKey != null && !queryRefKey.startsWith("def_")) {
				symbols.put("def_" + queryRefKey, symbols.remove(queryRefKey));
			}

			// Merge local symbol table back into parent
			walker.popSymbolTablePutAll(symbols);

			// Advance query counter after recording the injection
			walker.queryCount++;

		} else {
			// Default: not a subquery, just restore the parent's symbol table
			walker.popSymbolTablePutAll(symbols);
		}
			
	}

	private String getSubqueryReferenceKey(HashMap<String, Object> symbols) {
		if (symbols == null || symbols.isEmpty()) {
			return null;
		}

		for (String key : symbols.keySet()) {
			if (isQuerySourceReference(key) && !key.startsWith("def_")) {
				return key;
			}
		}

		return null;
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
			// Capture Query Column Dictionary for this level
// ***			walker.queryColumnDictionaryMap.put(key, walker.symbolTable.remove(CURRENT_QUERY_COLUMN_DICTIONARY));

			// Extract the singular query reference key from the local symbol table (e.g., "query0")
			String queryRefKey = null;
			if (symbols != null && !symbols.isEmpty()) {
				queryRefKey = symbols.keySet().iterator().next();
			}
			annotateSubqueryReference(reference, queryRefKey);
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


	@Override
	public void exitPosition_function(@NotNull SQLSelectParserParser.Position_functionContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Map<String, Object> subMap = walker.getNodeMap(ruleIndex, stackLevel);
		Object type = subMap.remove(ASTWALKER_RULE_TYPE_KEY);

		Map<String, Object> item = new HashMap<String, Object>();
		Map<String, Object> params = new HashMap<String, Object>();

		if (subMap.size() == 3) {
			// Two-argument IN-keyword variant:
			//   position_function_name ( search IN source )
			item.put(MUMBLE_FUNCTION_NAME_KEY, subMap.remove("1"));
			params.put("1", subMap.remove("2"));
			params.put("2", subMap.remove("3"));
			item.put(MUMBLE_OPERATOR_KEY, "IN");
			item.put(MUMBLE_PARAMETERS_KEY, params);
			subMap.put(MUMBLE_FUNCTION_KEY, item);
		} else if (subMap.size() == 4) {
			// Three-argument comma variant:
			//   position_function_name ( search , source , start )
			item.put(MUMBLE_FUNCTION_NAME_KEY, subMap.remove("1"));
			params.put("1", subMap.remove("2"));
			params.put("2", subMap.remove("3"));
			params.put("3", subMap.remove("4"));
			item.put(MUMBLE_PARAMETERS_KEY, params);
			subMap.put(MUMBLE_FUNCTION_KEY, item);
		} else {
			walker.showTrace(walker.parseTrace, "Wrong number of entries: " + subMap);
		}
		walker.showTrace(walker.parseTrace, "Position Function: " + subMap);
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
				Object unresolvedItem = valueExpression.get(MUMBLE_COLUMN_KEY);
				if (!(unresolvedItem instanceof Map<?, ?>)) {
					unresolvedItem = valueExpression;
				}
				walker.collectUnresolvedColumnReference(tableRef, unresolvedItem, ctx.getStart());
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
		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Map<String, Object> subMap = walker.getNodeMap(ruleIndex, stackLevel);
		subMap = emitInvalidVariableDiagnosticAndSynthesizeIfNeeded(ctx, subMap);
		if (subMap == null || !subMap.containsKey("1")) {
			return;
		}
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
