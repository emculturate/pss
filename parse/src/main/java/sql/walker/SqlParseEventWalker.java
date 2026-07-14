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
import java.util.ArrayDeque;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map;

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

import org.antlr.v4.runtime.misc.Interval;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.antlr.v4.runtime.tree.TerminalNodeImpl;

import access.Snippet;
import astwalkers.SqlASTWalkerHelper;
import errorhandling.ParseDiagnostic;

import sql.SQLSelectParserBaseListener;
import sql.SQLSelectParserParser;
import sql.diagnostics.SqlParseDiagnosticService;
import sql.symboltree.SqlParseSymbolTreeHelper;
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
	private static final String TEMP_SCRIPT_STATEMENT_SYMBOL_PREFIX = "_tmp_script_statement_symbols_";
	private final ScriptParseAccumulator scriptParseAccumulator = new ScriptParseAccumulator();

	/**
	 * AST Walker Helper for this instance of the SQL Parse Event Walker
	 */
	private final SqlASTWalkerHelper walker;
	private final SqlParseDiagnosticService diagnosticService;
	private final SqlParseSymbolTreeHelper symbolTreeHelper;
	private final Set<String> invalidVariableDiagnosticKeys;
	private static final String PIVOT_IN_IDENTIFIER_REFERENCES_KEY = "pivot_in_identifier_references";
	private final ArrayDeque<Integer> setOperationWrapAnchorStackLevels;
	private int tableSourcePrimaryNestingDepth;


	// Constructors
	public SqlParseEventWalker() {
		super();

		// Initialize the walker with the SqlASTWalkerHelper
		this.walker = new SqlASTWalkerHelper();
		this.diagnosticService = new SqlParseDiagnosticService(this.walker);
		this.symbolTreeHelper = new SqlParseSymbolTreeHelper(this.walker);
		this.invalidVariableDiagnosticKeys = new HashSet<String>();
		this.setOperationWrapAnchorStackLevels = new ArrayDeque<Integer>();
		this.tableSourcePrimaryNestingDepth = 0;


	}

	@Override
	public void enterTable_source_primary(SQLSelectParserParser.Table_source_primaryContext ctx) {
		tableSourcePrimaryNestingDepth++;

		if (!containsSetOperationSubquery(ctx)) {
			return;
		}

		Integer stackLevel = walker.currentStackLevel(ctx.getRuleIndex());
		if (stackLevel == null) {
			return;
		}

		// Anchor wrapping only at the first table_source_primary nesting level.
		if (tableSourcePrimaryNestingDepth == 1 && setOperationWrapAnchorStackLevels.isEmpty()) {
			setOperationWrapAnchorStackLevels.push(stackLevel);
		}
	}

	private boolean containsSetOperationSubquery(SQLSelectParserParser.Table_source_primaryContext ctx) {
		if (ctx == null || ctx.subquery() == null) {
			return false;
		}

		SQLSelectParserParser.Query_expressionContext queryExpression = ctx.subquery().query_expression();
		if (queryExpression == null || queryExpression.intersected_query() == null) {
			return false;
		}

		SQLSelectParserParser.Intersected_queryContext intersectedQuery = queryExpression.intersected_query();
		if (intersectedQuery.intersect_clause() != null && !intersectedQuery.intersect_clause().isEmpty()) {
			return true;
		}

		if (intersectedQuery.unionized_query() == null) {
			return false;
		}

		for (SQLSelectParserParser.Unionized_queryContext unionizedQuery : intersectedQuery.unionized_query()) {
			if (unionizedQuery != null
					&& unionizedQuery.union_clause() != null
					&& !unionizedQuery.union_clause().isEmpty()) {
				return true;
			}
		}

		return false;
	}

	private boolean shouldWrapSetOperationAtCurrentLevel(Integer stackLevel, Map<String, Object> reference) {
		if (!isQueryLikeFromSource(reference) || stackLevel == null) {
			return false;
		}

		return !setOperationWrapAnchorStackLevels.isEmpty()
				&& stackLevel.equals(setOperationWrapAnchorStackLevels.peek());
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

	private void enterRelationalModifierClauseScope(String modifierKey) {
		symbolTreeHelper.pushSymbolTableWithParentVisibleScope();
		if (MUMBLE_PIVOT_KEY.equals(modifierKey)) {
			walker.symbolTable.put(PIVOT_IN_IDENTIFIER_REFERENCES_KEY, new HashMap<String, Object>());
		}
	}

	@SuppressWarnings("unchecked")
	private void publishRelationalModifierScopeAndPop(String modifierKey, Token startToken) {
		Object queryDictionaryObj = walker.symbolTable.get(MUMBLE_QUERY_DICTIONARY_KEY);
		HashMap<String, Object> queryDictionaryForParent = null;
		if (queryDictionaryObj instanceof HashMap<?, ?> queryDictionaryMapObj
				&& !((HashMap<String, Object>) queryDictionaryMapObj).isEmpty()) {
			queryDictionaryForParent = new HashMap<String, Object>((HashMap<String, Object>) queryDictionaryMapObj);
		}

		Object tableDictionaryObj = walker.symbolTable.get(MUMBLE_TABLE_DICTIONARY_KEY);
		HashMap<String, Object> tableDictionaryForParent = null;
		if (tableDictionaryObj instanceof HashMap<?, ?> tableDictionaryMapObj
				&& !((HashMap<String, Object>) tableDictionaryMapObj).isEmpty()) {
			tableDictionaryForParent = new HashMap<String, Object>((HashMap<String, Object>) tableDictionaryMapObj);
		}

		Object derivedHintsObj = walker.symbolTable.get(SqlParseSymbolTreeHelper.DERIVED_COLUMNS_HINTS_KEY);
		ArrayList<Object> derivedHintsForParent = null;
		if (derivedHintsObj instanceof ArrayList<?> hintListObj && !hintListObj.isEmpty()) {
			derivedHintsForParent = new ArrayList<Object>((ArrayList<Object>) hintListObj);
		}

		Object unresolvedObj = walker.symbolTable.get(MUMBLE_UNRESOLVED_COLUMN_KEY);
		HashMap<String, Object> unresolvedForParent = null;
		if (unresolvedObj instanceof HashMap<?, ?> unresolvedMapObj && !((HashMap<String, Object>) unresolvedMapObj).isEmpty()) {
			unresolvedForParent = new HashMap<String, Object>((HashMap<String, Object>) unresolvedMapObj);
		}

		Object pivotIdentifiersObj = walker.symbolTable.get(PIVOT_IN_IDENTIFIER_REFERENCES_KEY);
		HashMap<String, Object> pivotIdentifiersForParent = null;
		if (pivotIdentifiersObj instanceof HashMap<?, ?> pivotIdentifierMapObj
				&& !((HashMap<String, Object>) pivotIdentifierMapObj).isEmpty()) {
			pivotIdentifiersForParent = new HashMap<String, Object>((HashMap<String, Object>) pivotIdentifierMapObj);
		}

		walker.popSymbolTable("_tmp_relational_modifier_scope", new HashMap<String, Object>());
		walker.symbolTable.remove("_tmp_relational_modifier_scope");

		if (queryDictionaryForParent != null) {
			Object parentQueryDictionaryObj = walker.symbolTable.get(MUMBLE_QUERY_DICTIONARY_KEY);
			HashMap<String, Object> parentQueryDictionary;
			if (parentQueryDictionaryObj instanceof HashMap<?, ?>) {
				parentQueryDictionary = (HashMap<String, Object>) parentQueryDictionaryObj;
			} else {
				parentQueryDictionary = new HashMap<String, Object>();
				walker.symbolTable.put(MUMBLE_QUERY_DICTIONARY_KEY, parentQueryDictionary);
			}
			for (Map.Entry<String, Object> entry : queryDictionaryForParent.entrySet()) {
				if (entry.getKey() == null || entry.getValue() == null) {
					continue;
				}
				walker.mergeResolvedColumnIntoDictionary(parentQueryDictionary, entry.getKey(), entry.getValue());
			}
		}

		if (tableDictionaryForParent != null) {
			Object parentTableDictionaryObj = walker.symbolTable.get(MUMBLE_TABLE_DICTIONARY_KEY);
			HashMap<String, Object> parentTableDictionary;
			if (parentTableDictionaryObj instanceof HashMap<?, ?>) {
				parentTableDictionary = (HashMap<String, Object>) parentTableDictionaryObj;
			} else {
				parentTableDictionary = new HashMap<String, Object>();
				walker.symbolTable.put(MUMBLE_TABLE_DICTIONARY_KEY, parentTableDictionary);
			}
			for (Map.Entry<String, Object> tableEntry : tableDictionaryForParent.entrySet()) {
				String tableKey = tableEntry.getKey();
				if (tableKey == null
						|| tableKey.startsWith("def_")
						|| tableKey.matches("^query\\d+$")) {
					continue;
				}
				if (!(tableEntry.getValue() instanceof Map<?, ?> childTableColumnsMapObj)) {
					continue;
				}
				HashMap<String, Object> childTableColumns = new HashMap<String, Object>((Map<String, Object>) childTableColumnsMapObj);
				Object existingParentTableColumnsObj = parentTableDictionary.get(tableKey);
				HashMap<String, Object> parentTableColumns;
				if (existingParentTableColumnsObj instanceof HashMap<?, ?>) {
					parentTableColumns = (HashMap<String, Object>) existingParentTableColumnsObj;
				} else {
					parentTableColumns = new HashMap<String, Object>();
					parentTableDictionary.put(tableKey, parentTableColumns);
				}
				for (Map.Entry<String, Object> columnEntry : childTableColumns.entrySet()) {
					if (columnEntry.getKey() == null || columnEntry.getValue() == null) {
						continue;
					}
					walker.mergeResolvedColumnIntoDictionary(parentTableColumns, columnEntry.getKey(), columnEntry.getValue());
				}
			}
		}

		if (derivedHintsForParent != null) {
			Object parentHintsObj = walker.symbolTable.get(SqlParseSymbolTreeHelper.DERIVED_COLUMNS_HINTS_KEY);
			ArrayList<Object> parentHints;
			if (parentHintsObj instanceof ArrayList<?>) {
				parentHints = (ArrayList<Object>) parentHintsObj;
			} else {
				parentHints = new ArrayList<Object>();
				walker.symbolTable.put(SqlParseSymbolTreeHelper.DERIVED_COLUMNS_HINTS_KEY, parentHints);
			}
			parentHints.addAll(derivedHintsForParent);
		}

		if (unresolvedForParent != null) {
			Object parentUnresolvedObj = walker.symbolTable.get(MUMBLE_UNRESOLVED_COLUMN_KEY);
			HashMap<String, Object> parentUnresolved;
			if (parentUnresolvedObj instanceof HashMap<?, ?>) {
				parentUnresolved = (HashMap<String, Object>) parentUnresolvedObj;
			} else {
				parentUnresolved = new HashMap<String, Object>();
				walker.symbolTable.put(MUMBLE_UNRESOLVED_COLUMN_KEY, parentUnresolved);
			}
			parentUnresolved.putAll(unresolvedForParent);
		}

		if (pivotIdentifiersForParent != null && MUMBLE_PIVOT_KEY.equals(modifierKey)) {
			Object parentPivotIdentifiersObj = walker.symbolTable.get(PIVOT_IN_IDENTIFIER_REFERENCES_KEY);
			HashMap<String, Object> parentPivotIdentifiers;
			if (parentPivotIdentifiersObj instanceof HashMap<?, ?>) {
				parentPivotIdentifiers = (HashMap<String, Object>) parentPivotIdentifiersObj;
			} else {
				parentPivotIdentifiers = new HashMap<String, Object>();
				walker.symbolTable.put(PIVOT_IN_IDENTIFIER_REFERENCES_KEY, parentPivotIdentifiers);
			}
			parentPivotIdentifiers.putAll(pivotIdentifiersForParent);
		}
	}

	private boolean shouldProjectSelectIntoForQuerySpecification(SQLSelectParserParser.Query_specificationContext ctx) {
		if (ctx.into_list() == null) {
			return true;
		}

		String setOperationType = null;
		int memberPosition = -1;
		Token violationToken = null;

		ParserRuleContext child = ctx;
		ParserRuleContext parent = ctx.getParent();

		while (parent != null) {
			if (parent instanceof SQLSelectParserParser.Unionized_queryContext unionizedCtx
					&& child instanceof SQLSelectParserParser.Set_operation_memberContext setOperationMemberCtx) {
				int unionMemberPosition = unionizedCtx.set_operation_member().indexOf(setOperationMemberCtx) + 1;
				if (unionMemberPosition > 1) {
					setOperationType = "UNION";
					memberPosition = unionMemberPosition;
					violationToken = ctx.getStart();
					break;
				}
			}

			if (parent instanceof SQLSelectParserParser.Intersected_queryContext intersectedCtx
					&& child instanceof SQLSelectParserParser.Unionized_queryContext unionizedChildCtx) {
				int intersectMemberPosition = intersectedCtx.unionized_query().indexOf(unionizedChildCtx) + 1;
				if (intersectMemberPosition > 1) {
					setOperationType = "INTERSECTION";
					memberPosition = intersectMemberPosition;
					violationToken = ctx.getStart();
					break;
				}
			}

			child = parent;
			parent = parent.getParent();
		}

		if (setOperationType == null) {
			return true;
		}

		diagnosticService.emitIntoOnlyAllowedOnFirstSetMember(setOperationType, memberPosition, violationToken);
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

	private List<Object> extractOrderedRuleChildren(Map<String, Object> subMap) {
		List<Object> children = new ArrayList<Object>();
		for (int index = 1; subMap.containsKey(String.valueOf(index)); index++) {
			Object child = subMap.remove(String.valueOf(index));
			if (child instanceof Map<?, ?>) {
				children.add(child);
			}
		}
		return children;
	}

	private String extractCreateTypeText(ParserRuleContext ctx, String fallback, int... childIndexes) {
		if (ctx == null || childIndexes == null || childIndexes.length == 0) {
			return fallback;
		}

		StringBuilder builder = new StringBuilder();
		for (int childIndex : childIndexes) {
			if (childIndex < 0 || childIndex >= ctx.getChildCount()) {
				continue;
			}

			String tokenText = ctx.getChild(childIndex).getText();
			if (tokenText == null || tokenText.isBlank()) {
				continue;
			}

			if (builder.length() > 0) {
				builder.append(' ');
			}
			builder.append(tokenText);
		}

		return builder.length() == 0 ? fallback : builder.toString();
	}

	
	private Map<String, Object> normalizeNamedObjectNode(Object nodeObj) {
		Map<String, Object> normalized = new LinkedHashMap<String, Object>();
		if (!(nodeObj instanceof Map<?, ?> mapObj)) {
			if (nodeObj != null) {
				normalized.put(MUMBLE_NAME_KEY, nodeObj.toString());
			}
			return normalized;
		}

		Map<String, Object> node = (Map<String, Object>) mapObj;
		if (node.containsKey(MUMBLE_NAME_KEY)
				|| node.containsKey(MUMBLE_FUNCTION_NAME_KEY)
				|| node.containsKey(MUMBLE_TABLE_KEY)) {
			normalized.putAll(node);
			if (!normalized.containsKey(MUMBLE_NAME_KEY)) {
				Object functionNameObj = normalized.get(MUMBLE_FUNCTION_NAME_KEY);
				Object tableNameObj = normalized.get(MUMBLE_TABLE_KEY);
				if (functionNameObj != null) {
					normalized.put(MUMBLE_NAME_KEY, functionNameObj);
				} else if (tableNameObj != null) {
					normalized.put(MUMBLE_NAME_KEY, tableNameObj);
				}
			}
			return normalized;
		}

		List<Object> parts = new ArrayList<Object>();
		for (int index = 1; node.containsKey(String.valueOf(index)); index++) {
			parts.add(node.get(String.valueOf(index)));
		}

		if (parts.size() == 1) {
			normalized.put(MUMBLE_NAME_KEY, parts.get(0));
		} else if (parts.size() == 2) {
			normalized.put(MUMBLE_SCHEMA_KEY, parts.get(0));
			normalized.put(MUMBLE_NAME_KEY, parts.get(1));
		} else if (parts.size() >= 3) {
			normalized.put(MUMBLE_DATABASE_NAME_KEY, parts.get(0));
			normalized.put(MUMBLE_SCHEMA_KEY, parts.get(1));
			normalized.put(MUMBLE_NAME_KEY, parts.get(2));
		}

		return normalized;
	}

	
	private Map<String, Object> normalizeTableNode(Object nodeObj) {
		Map<String, Object> normalized = new LinkedHashMap<String, Object>();
		if (!(nodeObj instanceof Map<?, ?> mapObj)) {
			if (nodeObj != null) {
				normalized.put(MUMBLE_TABLE_KEY, nodeObj.toString());
			}
			return normalized;
		}

		Map<String, Object> node = (Map<String, Object>) mapObj;
		if (node.containsKey(MUMBLE_TABLE_KEY)) {
			normalized.putAll(node);
			return normalized;
		}

		if (node.containsKey(MUMBLE_NAME_KEY)) {
			normalized.putAll(node);
			normalized.put(MUMBLE_TABLE_KEY, node.get(MUMBLE_NAME_KEY));
			return normalized;
		}

		List<Object> parts = new ArrayList<Object>();
		for (int index = 1; node.containsKey(String.valueOf(index)); index++) {
			parts.add(node.get(String.valueOf(index)));
		}

		if (parts.size() == 1) {
			normalized.put(MUMBLE_TABLE_KEY, parts.get(0));
		} else if (parts.size() == 2) {
			normalized.put(MUMBLE_SCHEMA_KEY, parts.get(0));
			normalized.put(MUMBLE_TABLE_KEY, parts.get(1));
		} else if (parts.size() >= 3) {
			normalized.put(MUMBLE_DATABASE_NAME_KEY, parts.get(0));
			normalized.put(MUMBLE_SCHEMA_KEY, parts.get(1));
			normalized.put(MUMBLE_TABLE_KEY, parts.get(2));
		}

		return normalized;
	}

	private Object extractPreferredName(Map<String, Object> objectNode) {
		if (objectNode == null || objectNode.isEmpty()) {
			return null;
		}

		Object name = objectNode.get(MUMBLE_NAME_KEY);
		if (name != null) {
			return name;
		}

		Object functionName = objectNode.get(MUMBLE_FUNCTION_NAME_KEY);
		if (functionName != null) {
			return functionName;
		}

		return objectNode.get(MUMBLE_TABLE_KEY);
	}

	private boolean hasTableIdentity(Map<String, Object> tableNode) {
		if (tableNode == null || tableNode.isEmpty()) {
			return false;
		}

		return tableNode.containsKey(MUMBLE_TABLE_KEY)
				|| tableNode.containsKey(MUMBLE_NAME_KEY)
				|| tableNode.containsKey(MUMBLE_SCHEMA_KEY)
				|| tableNode.containsKey(MUMBLE_DATABASE_NAME_KEY);
	}

	
	private boolean looksLikeQueryNode(Object nodeObj) {
		if (!(nodeObj instanceof Map<?, ?> mapObj)) {
			return false;
		}

		Map<String, Object> node = (Map<String, Object>) mapObj;
		return node.containsKey(MUMBLE_SELECT_KEY)
				|| node.containsKey(MUMBLE_WITH_KEY)
				|| node.containsKey(MUMBLE_UNION_KEY)
				|| node.containsKey(MUMBLE_INTERSECT_KEY)
				|| node.containsKey(MUMBLE_QUERY_KEY);
	}

	private Map<String, Object> buildFallbackTableNodeFromText(String tableText) {
		Map<String, Object> fallback = new LinkedHashMap<String, Object>();
		if (tableText == null || tableText.isBlank()) {
			return fallback;
		}

		String[] parts = tableText.split("\\.");
		if (parts.length == 1) {
			fallback.put(MUMBLE_TABLE_KEY, parts[0]);
		} else if (parts.length == 2) {
			fallback.put(MUMBLE_SCHEMA_KEY, parts[0]);
			fallback.put(MUMBLE_TABLE_KEY, parts[1]);
		} else {
			fallback.put(MUMBLE_DATABASE_NAME_KEY, parts[0]);
			fallback.put(MUMBLE_SCHEMA_KEY, parts[1]);
			StringBuilder tableBuilder = new StringBuilder(parts[2]);
			for (int i = 3; i < parts.length; i++) {
				tableBuilder.append('.').append(parts[i]);
			}
			fallback.put(MUMBLE_TABLE_KEY, tableBuilder.toString());
		}

		return fallback;
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
						Object normalizedColumnRefs = symbolTreeHelper.normalizeUpdateColumnRefs(columnEntry.getValue());
						if (normalizedColumnRefs != null) {
							mergedColumns.put(columnName, normalizedColumnRefs);
						}
					}
				}
			}
		}

		return walkerTableDictionary;
	}

	
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
		HashMap<String, Object> exposed = new HashMap<String, Object>();
		if (walker.queryColumnDictionaryMap == null || walker.queryColumnDictionaryMap.isEmpty()) {
			return exposed;
		}
		for (Map.Entry<String, Object> entry : walker.queryColumnDictionaryMap.entrySet()) {
			String key = entry.getKey();
			if (key == null || key.startsWith("def_")) {
				continue;
			}
			exposed.put(key, entry.getValue());
		}
		return exposed;
	}

	public HashMap<String, Object> getSymbolTable() {
		if (walker.symbolTable != null) {
			symbolTreeHelper.stripWalkTimeKeysFromPublishedScope(walker.symbolTable);
			symbolTreeHelper.syncPublishedScopeQueryDictionariesFromGlobal(walker.symbolTable);
		}
		return walker.symbolTable;
	}

	/**
	 * Removes walk-time-only symbol-table entries from the artifact handed to clients.
	 * Invoked at successful grammar exit points and after aborted walks at the access boundary.
	 */
	public void finalizeHandoffSymbolTable() {
		if (walker.symbolTable != null) {
			symbolTreeHelper.stripWalkTimeKeysFromPublishedScope(walker.symbolTable);
			symbolTreeHelper.syncPublishedScopeQueryDictionariesFromGlobal(walker.symbolTable);
		}
	}

	
	public HashSet<String> getInterface() {
		HashSet<String> interfac = new HashSet<String>();
		if (walker.symbolTable == null || walker.symbolTable.isEmpty()) {
			return interfac;
		}

		Map<String, Object> queryMap = null;
		String selectedNormalizedQueryKey = null;
		int topQueryIndex = -1;
		for (String key : walker.symbolTable.keySet()) {
			if (key == null) {
				continue;
			}
			String normalizedQueryKey = null;
			if (key.startsWith(MUMBLE_QUERY_KEY)) {
				normalizedQueryKey = key;
			} else if (key.startsWith("def_" + MUMBLE_QUERY_KEY)) {
				normalizedQueryKey = key.substring("def_".length());
			}
			if (normalizedQueryKey == null) {
				continue;
			}
			Object queryObject = walker.symbolTable.get(key);
			if (!(queryObject instanceof HashMap<?, ?>)) {
				continue;
			}
			String suffix = normalizedQueryKey.substring(MUMBLE_QUERY_KEY.length());
			int queryIndex;
			try {
				queryIndex = Integer.parseInt(suffix);
			} catch (NumberFormatException ex) {
				continue;
			}
			if (queryIndex > topQueryIndex) {
				topQueryIndex = queryIndex;
				selectedNormalizedQueryKey = normalizedQueryKey;
				queryMap = (Map<String, Object>) queryObject;
			}
		}

		if (queryMap != null) {
			queryMap = resolveFinalQueryScope(queryMap, selectedNormalizedQueryKey);
		}

		if (queryMap == null) {
			for (String key : walker.symbolTable.keySet()) {
				if (key == null) {
					continue;
				}

				String normalizedSetOpKey = null;
				if (key.startsWith(MUMBLE_UNION_KEY) || key.startsWith(MUMBLE_INTERSECT_KEY)) {
					normalizedSetOpKey = key;
				} else if (key.startsWith("def_" + MUMBLE_UNION_KEY)
						|| key.startsWith("def_" + MUMBLE_INTERSECT_KEY)) {
					normalizedSetOpKey = key.substring("def_".length());
				}

				if (normalizedSetOpKey == null) {
					continue;
				}
				Object scopedObject = walker.symbolTable.get(key);
				if (!(scopedObject instanceof HashMap<?, ?>)) {
					continue;
				}
				String numericSuffix = normalizedSetOpKey.replaceFirst("^[^0-9]+", "");
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

				String normalizedUpdateKey = null;
				if (key.startsWith(MUMBLE_UPDATE_KEY)) {
					normalizedUpdateKey = key;
				} else if (key.startsWith("def_" + MUMBLE_UPDATE_KEY)) {
					normalizedUpdateKey = key.substring("def_".length());
				}
				if (normalizedUpdateKey == null) {
					continue;
				}

				Object scopedObject = walker.symbolTable.get(key);
				if (!(scopedObject instanceof HashMap<?, ?>)) {
					continue;
				}
				String numericSuffix = normalizedUpdateKey.replaceFirst("^[^0-9]+", "");
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

				String normalizedInsertKey = null;
				if (key.startsWith(MUMBLE_INSERT_KEY)) {
					normalizedInsertKey = key;
				} else if (key.startsWith("def_" + MUMBLE_INSERT_KEY)) {
					normalizedInsertKey = key.substring("def_".length());
				}
				if (normalizedInsertKey == null) {
					continue;
				}

				Object scopedObject = walker.symbolTable.get(key);
				if (!(scopedObject instanceof HashMap<?, ?>)) {
					continue;
				}
				String numericSuffix = normalizedInsertKey.replaceFirst("^[^0-9]+", "");
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

				String normalizedDeleteKey = null;
				if (key.startsWith(MUMBLE_DELETE_KEY)) {
					normalizedDeleteKey = key;
				} else if (key.startsWith("def_" + MUMBLE_DELETE_KEY)) {
					normalizedDeleteKey = key.substring("def_".length());
				}
				if (normalizedDeleteKey == null) {
					continue;
				}

				Object scopedObject = walker.symbolTable.get(key);
				if (!(scopedObject instanceof HashMap<?, ?>)) {
					continue;
				}
				String numericSuffix = normalizedDeleteKey.replaceFirst("^[^0-9]+", "");
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
		if (interfaceObject instanceof Map<?, ?> interfaceMap && !interfaceMap.isEmpty()) {
			for (Object keyObj : interfaceMap.keySet()) {
				if (keyObj instanceof String key) {
					interfac.add(key);
				}
			}
			return interfac;
		}

		Map<String, Object> setOperationMap = null;
		int topSetOperationIndex = -1;
		for (String key : walker.symbolTable.keySet()) {
			if (key == null) {
				continue;
			}
			String normalizedSetOpKey = null;
			if (key.startsWith(MUMBLE_UNION_KEY) || key.startsWith(MUMBLE_INTERSECT_KEY)) {
				normalizedSetOpKey = key;
			} else if (key.startsWith("def_" + MUMBLE_UNION_KEY)
					|| key.startsWith("def_" + MUMBLE_INTERSECT_KEY)) {
				normalizedSetOpKey = key.substring("def_".length());
			}
			if (normalizedSetOpKey == null) {
				continue;
			}
			Object scopedObject = walker.symbolTable.get(key);
			if (!(scopedObject instanceof Map<?, ?> scopedMap)) {
				continue;
			}
			Object scopedInterfaceObject = ((Map<String, Object>) scopedMap).get(MUMBLE_INTERFACE_KEY);
			if (!(scopedInterfaceObject instanceof Map<?, ?> scopedInterfaceMap) || scopedInterfaceMap.isEmpty()) {
				continue;
			}
			String numericSuffix = normalizedSetOpKey.replaceFirst("^[^0-9]+", "");
			int scopeIndex;
			try {
				scopeIndex = Integer.parseInt(numericSuffix);
			} catch (NumberFormatException ex) {
				continue;
			}
			if (scopeIndex > topSetOperationIndex) {
				topSetOperationIndex = scopeIndex;
				setOperationMap = (Map<String, Object>) scopedMap;
			}
		}

		if (setOperationMap != null) {
			Object setOperationInterface = setOperationMap.get(MUMBLE_INTERFACE_KEY);
			if (setOperationInterface instanceof Map<?, ?> setOperationInterfaceMap && !setOperationInterfaceMap.isEmpty()) {
				for (Object keyObj : setOperationInterfaceMap.keySet()) {
					if (keyObj instanceof String key) {
						interfac.add(key);
					}
				}
				if (!interfac.isEmpty()) {
					return interfac;
				}
			}
		}

		HashMap<String, Object> queryColumnDictionaryMap = getQueryColumnDictionaryMap();
		if (!queryColumnDictionaryMap.isEmpty()) {
			for (Object queryDictionaryObj : queryColumnDictionaryMap.values()) {
				if (!(queryDictionaryObj instanceof Map<?, ?> queryDictionaryMap)) {
					continue;
				}
				for (Object keyObj : queryDictionaryMap.keySet()) {
					if (keyObj instanceof String key && !key.isBlank()) {
						interfac.add(key);
					}
				}
			}
			if (!interfac.isEmpty()) {
				return interfac;
			}
		}

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
		if (!(interfaceObject instanceof Map<?, ?>)) {
			return interfac;
		}

		return interfac;
	}

	@SuppressWarnings("unchecked")
	private Map<String, Object> resolveFinalQueryScope(Map<String, Object> queryMap, String normalizedQueryKey) {
		if (queryMap == null) {
			return null;
		}

		if (queryMap.get(MUMBLE_INTERFACE_KEY) instanceof Map<?, ?> interfaceMap && !interfaceMap.isEmpty()) {
			return queryMap;
		}

		if (normalizedQueryKey != null) {
			Object exactChild = queryMap.get("def_" + normalizedQueryKey);
			if (exactChild instanceof Map<?, ?> exactChildMap
					&& ((Map<String, Object>) exactChildMap).get(MUMBLE_INTERFACE_KEY) instanceof Map<?, ?> exactInterfaceMap
					&& !exactInterfaceMap.isEmpty()) {
				return (Map<String, Object>) exactChildMap;
			}
		}

		Map<String, Object> bestQueryMap = null;
		int bestIndex = -1;
		for (Map.Entry<String, Object> entry : queryMap.entrySet()) {
			String key = entry.getKey();
			if (key == null || !key.startsWith("def_" + MUMBLE_QUERY_KEY)) {
				continue;
			}
			if (!(entry.getValue() instanceof Map<?, ?> nestedMapRaw)) {
				continue;
			}

			String suffix = key.substring(("def_" + MUMBLE_QUERY_KEY).length());
			int idx;
			try {
				idx = Integer.parseInt(suffix);
			} catch (NumberFormatException ex) {
				continue;
			}

			Map<String, Object> nestedMap = (Map<String, Object>) nestedMapRaw;
			if (!(nestedMap.get(MUMBLE_INTERFACE_KEY) instanceof Map<?, ?>)) {
				continue;
			}

			if (idx > bestIndex) {
				bestIndex = idx;
				bestQueryMap = nestedMap;
			}
		}

		if (bestQueryMap != null) {
			return bestQueryMap;
		}

		return queryMap;
	}

	/**
	 * Emit a Snippet object with all of the parts of the SQL Parse Event Walker results related to the query
	*
	 * @return
	 */
	public Snippet getSnippet() {
		Snippet snippet = new Snippet(walker.asTree, walker.getWalkerTableDictionary(), walker.queryColumnDictionaryMap,
				walker.symbolTable, walker.substitutionsMap, getInterface());
		if (scriptParseAccumulator.hasArrayOutputs()) {
			snippet.setArrayOutputCollectorsMap(scriptParseAccumulator.buildScriptArrayOutputCollectorsMap());
		}
		// Handoff: copy walker-generated (non-parser) diagnostics into the snippet.
		snippet.setParserDiagnosticList(
				scriptParseAccumulator.prefixWalkerDiagnostics(new ArrayList<>(walker.getWalkerDiagnostics())));
		return snippet;
	}

	public ScriptParseAccumulator getScriptParseAccumulator() {
		return scriptParseAccumulator;
	}

	public SqlASTWalkerHelper getWalker() {
		return walker;
	}

	public HashMap<String, Object> getSubstitutionsMap() {
		return walker.substitutionsMap;
	}

	public HashMap<String, Object> getArrayOutputCollectorsMap() {
		if (scriptParseAccumulator.hasArrayOutputs()) {
			return scriptParseAccumulator.buildScriptArrayOutputCollectorsMap();
		}
		return scriptParseAccumulator.buildSqlArrayOutputCollectorsMap(getInterface());
	}

	/* ==================================================================================
	 *
	 * SQL Event Walker Helper Methods
	 *
	 * These methods are used to manage the SQL AST and Symbol Table and apply specialized functions
	 * related to the SQL Grammar itself. These are not GENERIC methods so they are placed after
	 * the Standard Parser Event Walker methods. 
	 */
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
	public void enterEveryRule( ParserRuleContext ctx) {
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
	public void exitEveryRule( ParserRuleContext ctx) {
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
			}
		}

		walker.popStack(ruleIndex);
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
	  Script Start Symbol
	===============================================================================
	*/
	@Override
	public void enterScript( SQLSelectParserParser.ScriptContext ctx) {
		walker.pushSymbolTable();
		scriptParseAccumulator.reset();
	}

	@Override
	
	public void exitScript( SQLSelectParserParser.ScriptContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Map<String, Object> subMap = walker.removeNodeMap(ruleIndex, stackLevel);
		subMap.remove(ASTWALKER_RULE_TYPE_KEY);

		HashMap<String, Object> scriptStatements = new LinkedHashMap<String, Object>();
		for (int i = 0; i < ctx.sql_statement().size(); i++) {
			Object statementAst = subMap.remove(Integer.toString(i + 1));
			if (statementAst instanceof Map<?, ?> statementMapObj) {
				Map<String, Object> statementMap = (Map<String, Object>) statementMapObj;
				statementMap.remove(ASTWALKER_RULE_TYPE_KEY);
				if (statementMap.size() == 1 && statementMap.containsKey("1")) {
					statementAst = statementMap.get("1");
				}
			}
			if (statementAst != null) {
				scriptStatements.put(Integer.toString(i + 1), statementAst);
			}
		}
		walker.asTree.put(SQLPARSER_SCRIPT_TREE_KEY, scriptStatements);
		ScriptParseSnapshot scriptSnapshot = scriptParseAccumulator.snapshot();
		walker.tableDictionaryMap = new LinkedHashMap<String, Object>();
		walker.tableDictionaryMap.put(SQLPARSER_SCRIPT_TREE_KEY, scriptSnapshot.statementTableDictionaries());
		walker.queryColumnDictionaryMap = new LinkedHashMap<String, Object>();
		walker.queryColumnDictionaryMap.put(SQLPARSER_SCRIPT_TREE_KEY, scriptSnapshot.statementQueryDictionaries());
		walker.substitutionsMap = new LinkedHashMap<String, Object>();
		walker.substitutionsMap.put(SQLPARSER_SCRIPT_TREE_KEY, scriptSnapshot.statementSubstitutions());

		HashMap<String, Object> scriptSymbolTables = new LinkedHashMap<String, Object>();
		for (int i = 0; i < ctx.sql_statement().size(); i++) {
			Object statementSymbols = walker.symbolTable.remove(TEMP_SCRIPT_STATEMENT_SYMBOL_PREFIX + Integer.toString(i + 1));
			if (statementSymbols instanceof HashMap<?, ?> statementSymbolsMap) {
				symbolTreeHelper.stripWalkTimeKeysFromPublishedScope(
						(HashMap<String, Object>) statementSymbolsMap);
				scriptSymbolTables.put(Integer.toString(i + 1), statementSymbolsMap);
			} else if (statementSymbols != null) {
				scriptSymbolTables.put(Integer.toString(i + 1), statementSymbols);
			}
		}
		symbolTreeHelper.stripWalkTimeKeysFromPublishedScope(scriptSymbolTables);
		walker.popSymbolTable(SQLPARSER_SCRIPT_TREE_KEY, scriptSymbolTables);
	}

	@Override
	public void enterSql_statement( SQLSelectParserParser.Sql_statementContext ctx) {
		// Each SCRIPT statement gets a fresh scope so counters, dictionaries, and diagnostics do not bleed across statements.
		scriptParseAccumulator.beginStatement();
		walker.pushSymbolTable();
		walker.resetPerStatementScope();
	}

	@Override
	public void exitSql_statement( SQLSelectParserParser.Sql_statementContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Map<String, Object> subMap = walker.removeNodeMap(ruleIndex, stackLevel);
		subMap.remove(ASTWALKER_RULE_TYPE_KEY);

		Object item = subMap.remove("1");
		if (item != null && subMap.isEmpty()) {
			walker.collect(ruleIndex, stackLevel, item);
		} else {
			if (item != null) {
				subMap.put("1", item);
			}
			walker.collect(ruleIndex, stackLevel, subMap);
		}

		// Isolation boundary between SCRIPT statements.
		// It gives each statement its own line range, which is later used to attach statement prefixes to global diagnostics.
		// It prevents cross-statement carryover in dictionaries and collector outputs.
		if (scriptParseAccumulator.hasActiveStatement()) {
			int statementSequence = scriptParseAccumulator.endStatement();
			scriptParseAccumulator.recordLineRange(statementSequence, ctx.getStart(), ctx.getStop());
			scriptParseAccumulator.captureStatementSnapshot(
					statementSequence,
					walker.getWalkerTableDictionary(),
					walker.queryColumnDictionaryMap,
					walker.substitutionsMap,
					getInterface());
			HashMap<String, Object> statementSymbols = walker.symbolTable;
			symbolTreeHelper.stripWalkTimeKeysFromPublishedScope(statementSymbols);
			walker.popSymbolTable(TEMP_SCRIPT_STATEMENT_SYMBOL_PREFIX + Integer.toString(statementSequence), statementSymbols);
		}
	}

	/*
	===============================================================================
	  DDL Start Symbol
	===============================================================================
	*/
	@Override
	public void enterDdl( SQLSelectParserParser.DdlContext ctx) {
	}

	@Override
	public void exitDdl_primary( SQLSelectParserParser.Ddl_primaryContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		walker.handleOneChild(ruleIndex);
	}

	@Override
	public void exitDml_primary( SQLSelectParserParser.Dml_primaryContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		walker.handleOneChild(ruleIndex);
	}

	@Override
	public void exitDdl( SQLSelectParserParser.DdlContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Map<String, Object> subMap = walker.removeNodeMap(ruleIndex, stackLevel);
		subMap.remove(ASTWALKER_RULE_TYPE_KEY);

		Object ddlAst = subMap.remove("1");
		if (!subMap.isEmpty()) {
			ddlAst = subMap;
		}
		walker.asTree.put(SQLPARSER_DDL_TREE_KEY, ddlAst);
		symbolTreeHelper.finalizeTopLevelUnresolvedColumns();
	}

	/*
	===============================================================================
	  SQL Tree Start Symbol
	===============================================================================
	*/
	@Override
	public void exitSql( SQLSelectParserParser.SqlContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Map<String, Object> subMap = walker.removeNodeMap(ruleIndex, stackLevel);
		Object type = subMap.remove(ASTWALKER_RULE_TYPE_KEY);
		 walker.asTree.put(SQLPARSER_SQL_TREE_KEY, subMap.remove("1"));
		symbolTreeHelper.finalizeTopLevelUnresolvedColumns();

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
	
		walker.addQueryInputColumnsToTableDictionary();
	}

	/*
	===============================================================================
	  Predicand Start Symbol
	===============================================================================
	*/
	@Override
	public void exitPredicand_value( SQLSelectParserParser.Predicand_valueContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Map<String, Object> subMap = walker.removeNodeMap(ruleIndex, stackLevel);
		Object type = subMap.remove(ASTWALKER_RULE_TYPE_KEY);
		 walker.asTree.put(SQLPARSER_PREDICAND_TREE_KEY, subMap.remove("1"));

		walker.addQueryInputColumnsToTableDictionary();
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

		walker.addQueryInputColumnsToTableDictionary();
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

		walker.addQueryInputColumnsToTableDictionary();
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
			// Wrong number of entries for tuple context: ctx.getText()
		}

		 walker.asTree.put(SQLPARSER_TUPLE_TREE_KEY, item);
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
		symbolTreeHelper.finalizeTopLevelUnresolvedColumns();

		walker.addQueryInputColumnsToTableDictionary();
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
	}

	@Override
	public void exitUpdate_end_point(SQLSelectParserParser.Update_end_pointContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Map<String, Object> subMap = walker.removeNodeMap(ruleIndex, stackLevel);
		Object type = subMap.remove(ASTWALKER_RULE_TYPE_KEY);
		 walker.asTree.put(SQLPARSER_UPDATE_TREE_KEY, subMap.remove("1"));
	}

	@Override
	public void exitDelete_end_point(SQLSelectParserParser.Delete_end_pointContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Map<String, Object> subMap = walker.removeNodeMap(ruleIndex, stackLevel);
		Object type = subMap.remove(ASTWALKER_RULE_TYPE_KEY);
		 walker.asTree.put(SQLPARSER_DELETE_TREE_KEY, subMap.remove("1"));
	}

	@Override
	public void exitTruncate_end_point(SQLSelectParserParser.Truncate_end_pointContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Map<String, Object> subMap = walker.removeNodeMap(ruleIndex, stackLevel);
		Object type = subMap.remove(ASTWALKER_RULE_TYPE_KEY);
		 walker.asTree.put(SQLPARSER_TRUNCATE_TREE_KEY, subMap.remove("1"));
	}
	/*

	
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
		  DDL Statements: Create objects, delete objects, alter objects
	===============================================================================
	*/

	/*
	===============================================================================
	  CREATE Objects Statement Sources (TABLE, VIEW, MATERIALIZED VIEW) with AS (subquery or expression list)
	===============================================================================
	*/

	@Override
	public void enterCreate_statement_primary( SQLSelectParserParser.Create_statement_primaryContext ctx) {
		walker.pushSymbolTable();
	}

	@Override
	public void exitCreate_statement_primary( SQLSelectParserParser.Create_statement_primaryContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		walker.handleOneChild(ruleIndex);

		String createScopeKey = MUMBLE_CREATE_KEY + walker.queryCount;
		symbolTreeHelper.publishNamedScopeAndPop(createScopeKey, walker.symbolTable);
		walker.queryCount++;
	}

	@Override
	public void exitCreate_table_expression( SQLSelectParserParser.Create_table_expressionContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Map<String, Object> subMap = walker.getNodeMap(ruleIndex, stackLevel);
		subMap.remove(ASTWALKER_RULE_TYPE_KEY);

		List<Object> children = extractOrderedRuleChildren(subMap);
		Map<String, Object> createNode = new LinkedHashMap<String, Object>();
		createNode.put(MUMBLE_TYPE_KEY, extractCreateTypeText(ctx, "table", 1));

		Map<String, Object> tableNode = null;
		Object queryNode = null;

		if (ctx.query_expression() != null) {
			if (children.size() >= 2) {
				tableNode = normalizeTableNode(children.get(0));
				queryNode = children.get(1);
			} else if (children.size() == 1) {
				Object loneChild = children.get(0);
				Map<String, Object> normalizedLoneTable = normalizeTableNode(loneChild);
				if (hasTableIdentity(normalizedLoneTable) && !looksLikeQueryNode(loneChild)) {
					tableNode = normalizedLoneTable;
				} else {
					queryNode = loneChild;
				}
			}

			if ((tableNode == null || !hasTableIdentity(tableNode)) && ctx.db_object_name() != null) {
				tableNode = buildFallbackTableNodeFromText(ctx.db_object_name().getText());
			}

			if (tableNode != null && hasTableIdentity(tableNode)) {
				createNode.put(MUMBLE_TABLE_KEY, tableNode);
			}
			if (queryNode != null) {
				createNode.put(MUMBLE_QUERY_KEY, queryNode);
			}
		} else {
			if (!children.isEmpty()) {
				createNode.put(MUMBLE_TABLE_KEY, normalizeTableNode(children.get(0)));
			}
			if (children.size() >= 2) {
				createNode.put(MUMBLE_COLUMNS_KEY, children.get(1));
			}
			if (children.size() >= 3) {
				createNode.put(MUMBLE_PARAMETERS_KEY, children.get(2));
			}
			if (subMap.containsKey("4")) {
				createNode.put(MUMBLE_OPTIONS_KEY, subMap.get("4"));
			}
		}

		subMap.clear();
		subMap.put(MUMBLE_CREATE_KEY, createNode);
	}

	@Override
	public void exitCreate_index_expression( SQLSelectParserParser.Create_index_expressionContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Map<String, Object> subMap = walker.getNodeMap(ruleIndex, stackLevel);
		subMap.remove(ASTWALKER_RULE_TYPE_KEY);

		Object indexNameChild = subMap.get("1");
		Object tableChild = subMap.get("2");
		Object columnsChild = subMap.get("3");
		Map<String, Object> createNode = new LinkedHashMap<String, Object>();
		createNode.put(MUMBLE_TYPE_KEY, extractCreateTypeText(ctx, "index", 1));

		if (indexNameChild != null) {
			Map<String, Object> indexNameNode = normalizeNamedObjectNode(indexNameChild);
			Object indexName = extractPreferredName(indexNameNode);
			if (indexName != null) {
				createNode.put(MUMBLE_NAME_KEY, indexName);
			}
		}
		if (ctx.db_object_name(1) != null) {
			createNode.put(MUMBLE_TABLE_KEY, buildFallbackTableNodeFromText(ctx.db_object_name(1).getText()));
		} else if (tableChild != null) {
			createNode.put(MUMBLE_TABLE_KEY, normalizeTableNode(tableChild));
		}
		if (columnsChild != null) {
			createNode.put(MUMBLE_COLUMNS_KEY, columnsChild);
		}

		subMap.clear();
		subMap.put(MUMBLE_CREATE_KEY, createNode);
	}

	@Override
	public void exitCreate_view_expression( SQLSelectParserParser.Create_view_expressionContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Map<String, Object> subMap = walker.getNodeMap(ruleIndex, stackLevel);
		subMap.remove(ASTWALKER_RULE_TYPE_KEY);

		List<Object> children = extractOrderedRuleChildren(subMap);
		Map<String, Object> createNode = new LinkedHashMap<String, Object>();
		createNode.put(MUMBLE_TYPE_KEY, extractCreateTypeText(ctx, "view", 1));

		if (children.size() >= 1) {
			Map<String, Object> nameNode = normalizeNamedObjectNode(children.get(0));
			Object name = extractPreferredName(nameNode);
			if (name != null) {
				createNode.put(MUMBLE_NAME_KEY, name);
			}
		}
		if (children.size() >= 2) {
			createNode.put(MUMBLE_QUERY_KEY, children.get(1));
		}

		subMap.clear();
		subMap.put(MUMBLE_CREATE_KEY, createNode);
	}

	@Override
	public void exitCreate_materialized_view_expression( SQLSelectParserParser.Create_materialized_view_expressionContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Map<String, Object> subMap = walker.getNodeMap(ruleIndex, stackLevel);
		subMap.remove(ASTWALKER_RULE_TYPE_KEY);

		List<Object> children = extractOrderedRuleChildren(subMap);
		Map<String, Object> createNode = new LinkedHashMap<String, Object>();
		createNode.put(MUMBLE_TYPE_KEY, extractCreateTypeText(ctx, "materialized view", 1, 2));

		if (children.size() >= 1) {
			Map<String, Object> nameNode = normalizeNamedObjectNode(children.get(0));
			Object name = extractPreferredName(nameNode);
			if (name != null) {
				createNode.put(MUMBLE_NAME_KEY, name);
			}
		}
		if (children.size() >= 2) {
			createNode.put(MUMBLE_QUERY_KEY, children.get(1));
		}

		subMap.clear();
		subMap.put(MUMBLE_CREATE_KEY, createNode);
	}

	@Override
	public void exitCreate_function_expression( SQLSelectParserParser.Create_function_expressionContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Map<String, Object> subMap = walker.getNodeMap(ruleIndex, stackLevel);
		subMap.remove(ASTWALKER_RULE_TYPE_KEY);

		Object nameChild = subMap.get("1");
		Object argContentChild = subMap.get("2");
		Object dataTypeChild = subMap.get(ctx.generic_ddl_paren_content() != null ? "3" : "2");
		Object clausesChild = subMap.get(ctx.generic_ddl_paren_content() != null ? "4" : "3");
		Map<String, Object> createNode = new LinkedHashMap<String, Object>();
		createNode.put(MUMBLE_TYPE_KEY, extractCreateTypeText(ctx, "function", 1));

		if (nameChild != null) {
			Map<String, Object> nameNode = normalizeNamedObjectNode(nameChild);
			Object name = extractPreferredName(nameNode);
			if (name != null) {
				createNode.put(MUMBLE_NAME_KEY, name);
			}
		}

		if (ctx.generic_ddl_paren_content() != null && argContentChild != null) {
			createNode.put(MUMBLE_PARAMETERS_KEY, argContentChild);
		}
		if (dataTypeChild != null) {
			createNode.put(MUMBLE_DATATYPE_KEY, dataTypeChild);
		}
		if (clausesChild != null) {
			createNode.put(MUMBLE_CLAUSES_KEY, clausesChild);
		}

		subMap.clear();
		subMap.put(MUMBLE_CREATE_KEY, createNode);
	}

	@Override
	public void exitCreate_procedure_expression( SQLSelectParserParser.Create_procedure_expressionContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Map<String, Object> subMap = walker.getNodeMap(ruleIndex, stackLevel);
		subMap.remove(ASTWALKER_RULE_TYPE_KEY);

		Object nameChild = subMap.get("1");
		Object argContentChild = subMap.get("2");
		Object clausesChild = subMap.get(ctx.generic_ddl_paren_content() != null ? "3" : "2");
		Map<String, Object> createNode = new LinkedHashMap<String, Object>();
		createNode.put(MUMBLE_TYPE_KEY, extractCreateTypeText(ctx, "procedure", 1));

		if (nameChild != null) {
			Map<String, Object> nameNode = normalizeNamedObjectNode(nameChild);
			Object name = extractPreferredName(nameNode);
			if (name != null) {
				createNode.put(MUMBLE_NAME_KEY, name);
			}
		}

		if (ctx.generic_ddl_paren_content() != null && argContentChild != null) {
			createNode.put(MUMBLE_PARAMETERS_KEY, argContentChild);
		}
		if (clausesChild != null) {
			createNode.put(MUMBLE_CLAUSES_KEY, clausesChild);
		}

		subMap.clear();
		subMap.put(MUMBLE_CREATE_KEY, createNode);
	}

	@Override
	public void exitCreate_macro_expression( SQLSelectParserParser.Create_macro_expressionContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Map<String, Object> subMap = walker.getNodeMap(ruleIndex, stackLevel);
		subMap.remove(ASTWALKER_RULE_TYPE_KEY);

		List<Object> children = extractOrderedRuleChildren(new LinkedHashMap<String, Object>(subMap));
		Object nameChild = subMap.get("1");
		Object argContentChild = subMap.get("2");
		Object queryChild = subMap.get(ctx.generic_ddl_paren_content() != null ? "3" : "2");
		if (queryChild == null && children.size() >= 2) {
			queryChild = children.get(children.size() - 1);
		}
		Map<String, Object> createNode = new LinkedHashMap<String, Object>();
		createNode.put(MUMBLE_TYPE_KEY, extractCreateTypeText(ctx, "macro", 1));

		if (nameChild != null) {
			Map<String, Object> nameNode = normalizeNamedObjectNode(nameChild);
			Object name = extractPreferredName(nameNode);
			if (name != null) {
				createNode.put(MUMBLE_NAME_KEY, name);
			}
		}

		if (ctx.generic_ddl_paren_content() != null && argContentChild != null) {
			createNode.put(MUMBLE_PARAMETERS_KEY, argContentChild);
		}
		if (queryChild != null) {
			createNode.put(MUMBLE_QUERY_KEY, queryChild);
		}

		subMap.clear();
		subMap.put(MUMBLE_CREATE_KEY, createNode);
	}

	@Override
	public void exitCreate_sequence_expression( SQLSelectParserParser.Create_sequence_expressionContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Map<String, Object> subMap = walker.getNodeMap(ruleIndex, stackLevel);
		subMap.remove(ASTWALKER_RULE_TYPE_KEY);

		Object nameChild = subMap.get("1");
		Object clausesChild = subMap.get("2");
		Map<String, Object> createNode = new LinkedHashMap<String, Object>();
		createNode.put(MUMBLE_TYPE_KEY, extractCreateTypeText(ctx, "sequence", 1));

		if (nameChild != null) {
			Map<String, Object> nameNode = normalizeNamedObjectNode(nameChild);
			Object name = extractPreferredName(nameNode);
			if (name != null) {
				createNode.put(MUMBLE_NAME_KEY, name);
			}
		}
		if (clausesChild != null) {
			createNode.put(MUMBLE_CLAUSES_KEY, clausesChild);
		}

		subMap.clear();
		subMap.put(MUMBLE_CREATE_KEY, createNode);
	}

	@Override
	public void exitCreate_schema_expression( SQLSelectParserParser.Create_schema_expressionContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Map<String, Object> subMap = walker.getNodeMap(ruleIndex, stackLevel);
		subMap.remove(ASTWALKER_RULE_TYPE_KEY);

		Object nameChild = subMap.get("1");
		Object clausesChild = subMap.get("2");
		Map<String, Object> createNode = new LinkedHashMap<String, Object>();
		createNode.put(MUMBLE_TYPE_KEY, extractCreateTypeText(ctx, "schema", 1));

		if (nameChild != null) {
			Map<String, Object> nameNode = normalizeNamedObjectNode(nameChild);
			Object name = extractPreferredName(nameNode);
			if (name != null) {
				createNode.put(MUMBLE_NAME_KEY, name);
			}
		}
		if (clausesChild != null) {
			createNode.put(MUMBLE_CLAUSES_KEY, clausesChild);
		}

		subMap.clear();
		subMap.put(MUMBLE_CREATE_KEY, createNode);
	}

	@Override
	public void exitCreate_database_expression( SQLSelectParserParser.Create_database_expressionContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Map<String, Object> subMap = walker.getNodeMap(ruleIndex, stackLevel);
		subMap.remove(ASTWALKER_RULE_TYPE_KEY);

		Object nameChild = subMap.get("1");
		Object clausesChild = subMap.get("2");
		Map<String, Object> createNode = new LinkedHashMap<String, Object>();
		createNode.put(MUMBLE_TYPE_KEY, extractCreateTypeText(ctx, "database", 1));

		if (nameChild != null) {
			Map<String, Object> nameNode = normalizeNamedObjectNode(nameChild);
			Object name = extractPreferredName(nameNode);
			if (name != null) {
				createNode.put(MUMBLE_NAME_KEY, name);
			}
		}
		if (clausesChild != null) {
			createNode.put(MUMBLE_CLAUSES_KEY, clausesChild);
		}

		subMap.clear();
		subMap.put(MUMBLE_CREATE_KEY, createNode);
	}

	@Override
	public void exitCreate_role_expression( SQLSelectParserParser.Create_role_expressionContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Map<String, Object> subMap = walker.getNodeMap(ruleIndex, stackLevel);
		subMap.remove(ASTWALKER_RULE_TYPE_KEY);

		Object nameChild = subMap.get("1");
		Object clausesChild = subMap.get("2");
		Map<String, Object> createNode = new LinkedHashMap<String, Object>();
		createNode.put(MUMBLE_TYPE_KEY, extractCreateTypeText(ctx, "role", 1));

		if (nameChild != null) {
			Map<String, Object> nameNode = normalizeNamedObjectNode(nameChild);
			Object name = extractPreferredName(nameNode);
			if (name != null) {
				createNode.put(MUMBLE_NAME_KEY, name);
			}
		}
		if (clausesChild != null) {
			createNode.put(MUMBLE_CLAUSES_KEY, clausesChild);
		}

		subMap.clear();
		subMap.put(MUMBLE_CREATE_KEY, createNode);
	}

	@Override
	public void exitCreate_user_expression( SQLSelectParserParser.Create_user_expressionContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Map<String, Object> subMap = walker.getNodeMap(ruleIndex, stackLevel);
		subMap.remove(ASTWALKER_RULE_TYPE_KEY);

		Object nameChild = subMap.get("1");
		Object clausesChild = subMap.get("2");
		Map<String, Object> createNode = new LinkedHashMap<String, Object>();
		createNode.put(MUMBLE_TYPE_KEY, extractCreateTypeText(ctx, "user", 1));

		if (nameChild != null) {
			Map<String, Object> nameNode = normalizeNamedObjectNode(nameChild);
			Object name = extractPreferredName(nameNode);
			if (name != null) {
				createNode.put(MUMBLE_NAME_KEY, name);
			}
		}
		if (clausesChild != null) {
			createNode.put(MUMBLE_CLAUSES_KEY, clausesChild);
		}

		subMap.clear();
		subMap.put(MUMBLE_CREATE_KEY, createNode);
	}

	@Override
	public void exitCreate_stage_expression( SQLSelectParserParser.Create_stage_expressionContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Map<String, Object> subMap = walker.getNodeMap(ruleIndex, stackLevel);
		subMap.remove(ASTWALKER_RULE_TYPE_KEY);

		Object nameChild = subMap.get("1");
		Object clausesChild = subMap.get("2");
		Map<String, Object> createNode = new LinkedHashMap<String, Object>();
		createNode.put(MUMBLE_TYPE_KEY, extractCreateTypeText(ctx, "stage", 1));

		if (nameChild != null) {
			Map<String, Object> nameNode = normalizeNamedObjectNode(nameChild);
			Object name = extractPreferredName(nameNode);
			if (name != null) {
				createNode.put(MUMBLE_NAME_KEY, name);
			}
		}
		if (clausesChild != null) {
			createNode.put(MUMBLE_CLAUSES_KEY, clausesChild);
		}

		subMap.clear();
		subMap.put(MUMBLE_CREATE_KEY, createNode);
	}

	@Override
	public void exitCreate_file_format_expression( SQLSelectParserParser.Create_file_format_expressionContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Map<String, Object> subMap = walker.getNodeMap(ruleIndex, stackLevel);
		subMap.remove(ASTWALKER_RULE_TYPE_KEY);

		Object nameChild = subMap.get("1");
		Object clausesChild = subMap.get("2");
		Map<String, Object> createNode = new LinkedHashMap<String, Object>();
		createNode.put(MUMBLE_TYPE_KEY, extractCreateTypeText(ctx, "file format", 1, 2));

		if (nameChild != null) {
			Map<String, Object> nameNode = normalizeNamedObjectNode(nameChild);
			Object name = extractPreferredName(nameNode);
			if (name != null) {
				createNode.put(MUMBLE_NAME_KEY, name);
			}
		}
		if (clausesChild != null) {
			createNode.put(MUMBLE_CLAUSES_KEY, clausesChild);
		}

		subMap.clear();
		subMap.put(MUMBLE_CREATE_KEY, createNode);
	}

	/*
	===============================================================================
	  DROP Objects Statement Sources (TABLE, VIEW, MATERIALIZED VIEW) with AS (subquery or expression list)
	===============================================================================
	*/

	@Override
	public void enterDrop_statement_primary( SQLSelectParserParser.Drop_statement_primaryContext ctx) {
		walker.pushSymbolTable();
	}

	@Override
	public void exitDrop_statement_primary( SQLSelectParserParser.Drop_statement_primaryContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Map<String, Object> subMap = walker.getNodeMap(ruleIndex, stackLevel);
		subMap.remove(ASTWALKER_RULE_TYPE_KEY);

		Map<String, Object> dropNode = new LinkedHashMap<String, Object>();
		dropNode.put(MUMBLE_TYPE_KEY, extractDdlObjectTypeText(ctx.ddl_object_type()));

		if (ctx.db_object_name() != null) {
			dropNode.put(MUMBLE_NAME_KEY, buildFallbackTableNodeFromText(ctx.db_object_name().getText()));
		} else if (subMap.containsKey("2")) {
			dropNode.put(MUMBLE_NAME_KEY, subMap.get("2"));
		}

		// key "3" = options text string from drop_options / generic_ddl_options (if present)
		if (subMap.containsKey("3")) {
			dropNode.put(MUMBLE_OPTIONS_KEY, subMap.get("3"));
		}

		subMap.clear();
		subMap.put(MUMBLE_DROP_KEY, dropNode);

		String dropScopeKey = MUMBLE_DROP_KEY + walker.queryCount;
		symbolTreeHelper.publishNamedScopeAndPop(dropScopeKey, walker.symbolTable);
		walker.queryCount++;
	}

	@Override
	public void exitDdl_object_type( SQLSelectParserParser.Ddl_object_typeContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Map<String, Object> subMap = walker.getNodeMap(ruleIndex, stackLevel);
		if (subMap == null) {
			// No node map for ddl_object_type
			return;
		}
		subMap.clear();
		subMap.put(MUMBLE_TYPE_KEY, extractDdlObjectTypeText(ctx));
	}

	@Override
	public void exitDrop_options( SQLSelectParserParser.Drop_optionsContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		int parentRuleIndex = ctx.getParent().getRuleIndex();

		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Integer parentStackLevel = walker.currentStackLevel(parentRuleIndex);

		Map<String, Object> subMap = walker.removeNodeMap(ruleIndex, stackLevel);
		if (subMap == null) {
			// Missing DROP options map: ctx.getText()
			return;
		}

		subMap.remove(ASTWALKER_RULE_TYPE_KEY);
		Object value = subMap;
		if (subMap.size() == 1 && subMap.containsKey("1")) {
			value = subMap.remove("1");
			if (value instanceof Map<?, ?> valueMapObj) {
				
				Map<String, Object> valueMap = (Map<String, Object>) valueMapObj;
				valueMap.remove(ASTWALKER_RULE_TYPE_KEY);
			}
		}

		walker.addToParent(parentRuleIndex, parentStackLevel, value);
	}

	/*
	===============================================================================
	  ALTER Objects Statement Sources (TABLE, VIEW, MATERIALIZED VIEW) with AS (subquery or expression list)
	===============================================================================
	*/

	@Override
	public void enterAlter_statement_primary( SQLSelectParserParser.Alter_statement_primaryContext ctx) {
		walker.pushSymbolTable();
	}

	@Override
	public void exitAlter_statement_primary( SQLSelectParserParser.Alter_statement_primaryContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Map<String, Object> subMap = walker.getNodeMap(ruleIndex, stackLevel);
		subMap.remove(ASTWALKER_RULE_TYPE_KEY);

		Map<String, Object> alterNode = new LinkedHashMap<String, Object>();
		alterNode.put(MUMBLE_TYPE_KEY, extractDdlObjectTypeText(ctx.ddl_object_type()));

		if (ctx.db_object_name() != null) {
			alterNode.put(MUMBLE_NAME_KEY, buildFallbackTableNodeFromText(ctx.db_object_name().getText()));
		} else if (subMap.containsKey("2")) {
			alterNode.put(MUMBLE_NAME_KEY, subMap.get("2"));
		}

		// key "3" = options text string from alter_options / generic_ddl_options (if present)
		if (subMap.containsKey("3")) {
			alterNode.put(MUMBLE_OPTIONS_KEY, subMap.get("3"));
		}

		subMap.clear();
		subMap.put(MUMBLE_ALTER_KEY, alterNode);

		String alterScopeKey = MUMBLE_ALTER_KEY + walker.queryCount;
		symbolTreeHelper.publishNamedScopeAndPop(alterScopeKey, walker.symbolTable);
		walker.queryCount++;
	}

	@Override
	public void enterTruncate_statement_primary( SQLSelectParserParser.Truncate_statement_primaryContext ctx) {
		walker.pushSymbolTable();
	}

	@Override
	public void exitTruncate_statement_primary( SQLSelectParserParser.Truncate_statement_primaryContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Map<String, Object> subMap = walker.getNodeMap(ruleIndex, stackLevel);
		subMap.remove(ASTWALKER_RULE_TYPE_KEY);

		Map<String, Object> truncateNode = new LinkedHashMap<String, Object>();
		truncateNode.put(MUMBLE_TYPE_KEY, "TABLE");

		ArrayList<SQLSelectParserParser.Db_object_nameContext> nameContexts = new ArrayList<SQLSelectParserParser.Db_object_nameContext>();
		if (ctx.truncate_snowflake_expression() != null && ctx.truncate_snowflake_expression().db_object_name() != null) {
			nameContexts.add(ctx.truncate_snowflake_expression().db_object_name());
		}
		if (ctx.truncate_postgres_expression() != null && ctx.truncate_postgres_expression().db_object_name() != null) {
			nameContexts.addAll(ctx.truncate_postgres_expression().db_object_name());
		}

		if (nameContexts.size() == 1) {
			truncateNode.put(MUMBLE_NAME_KEY, buildFallbackTableNodeFromText(nameContexts.get(0).getText()));
		} else {
			Map<String, Object> names = new LinkedHashMap<String, Object>();
			int index = 1;
			for (SQLSelectParserParser.Db_object_nameContext nameCtx : nameContexts) {
				names.put(Integer.toString(index++), buildFallbackTableNodeFromText(nameCtx.getText()));
			}
			truncateNode.put(MUMBLE_LIST_KEY, names);
		}

		subMap.clear();
		subMap.put(MUMBLE_TRUNCATE_KEY, truncateNode);

		String truncateScopeKey = MUMBLE_TRUNCATE_KEY + walker.queryCount;
		symbolTreeHelper.publishNamedScopeAndPop(truncateScopeKey, walker.symbolTable);
		walker.queryCount++;
	}

	@Override
	public void exitAlter_options( SQLSelectParserParser.Alter_optionsContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		int parentRuleIndex = ctx.getParent().getRuleIndex();

		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Integer parentStackLevel = walker.currentStackLevel(parentRuleIndex);

		Map<String, Object> subMap = walker.removeNodeMap(ruleIndex, stackLevel);
		if (subMap == null) {
			// Missing ALTER options map: ctx.getText()
			return;
		}

		subMap.remove(ASTWALKER_RULE_TYPE_KEY);
		Object value = subMap;
		if (subMap.size() == 1 && subMap.containsKey("1")) {
			value = subMap.remove("1");
			if (value instanceof Map<?, ?> valueMapObj) {
				
				Map<String, Object> valueMap = (Map<String, Object>) valueMapObj;
				valueMap.remove(ASTWALKER_RULE_TYPE_KEY);
			}
		}

		walker.addToParent(parentRuleIndex, parentStackLevel, value);
	}

	@Override
	public void exitGeneric_ddl_options( SQLSelectParserParser.Generic_ddl_optionsContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		int parentRuleIndex = ctx.getParent().getRuleIndex();
		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Integer parentStackLevel = walker.currentStackLevel(parentRuleIndex);
		walker.removeNodeMap(ruleIndex, stackLevel);
		walker.addToParent(parentRuleIndex, parentStackLevel, extractDdlObjectTypeText(ctx));
	}

	@Override
	public void exitGeneric_ddl_paren_content( SQLSelectParserParser.Generic_ddl_paren_contentContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		int parentRuleIndex = ctx.getParent().getRuleIndex();
		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Integer parentStackLevel = walker.currentStackLevel(parentRuleIndex);
		walker.removeNodeMap(ruleIndex, stackLevel);
		walker.addToParent(parentRuleIndex, parentStackLevel, extractDdlObjectTypeText(ctx));
	}

	private String extractDdlObjectTypeText(ParserRuleContext ctx) {
		if (ctx == null || ctx.getChildCount() == 0) {
			return "";
		}

		StringBuilder typeBuilder = new StringBuilder();
		for (int i = 0; i < ctx.getChildCount(); i++) {
			if (i > 0) {
				typeBuilder.append(' ');
			}
			typeBuilder.append(ctx.getChild(i).getText().toLowerCase());
		}

		return typeBuilder.toString();
	}

	private void passThroughDdlRuleValueToParent(ParserRuleContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		int parentRuleIndex = ctx.getParent().getRuleIndex();

		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Integer parentStackLevel = walker.currentStackLevel(parentRuleIndex);

		Map<String, Object> subMap = walker.removeNodeMap(ruleIndex, stackLevel);
		if (subMap == null) {
			// Missing DDL pass-through map: ctx.getText()
			return;
		}

		subMap.remove(ASTWALKER_RULE_TYPE_KEY);
		Object value = subMap;
		if (subMap.size() == 1 && subMap.containsKey("1")) {
			value = subMap.remove("1");
		}

		walker.addToParent(parentRuleIndex, parentStackLevel, value);
	}

	/*
	===============================================================================
	  WITH Statement <with query>
	===============================================================================
	*/

	
	@Override
	public void exitWith_query( SQLSelectParserParser.With_queryContext ctx) {
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
			// Wrong number of entries for WITH query context: ctx.getText()
		}

		HashMap<String, Object> currentQuerySymbolTable = null;
		if (subMap.containsKey(MUMBLE_WITH_KEY)) {
			int scopeIndex = walker.queryCount - 1;
			currentQuerySymbolTable = symbolTreeHelper.promoteWithQueryMainBodyScope(scopeIndex);
			symbolTreeHelper.stripWalkTimeKeysFromPublishedScope(currentQuerySymbolTable);

			// If this was a nested WITH, enterWith_clause saved the outer WITH clause's
			// in-progress cte_list under MUMBLE_OUTER_CONTEXT_LIST_KEY. It was absorbed into
			// currentQuerySymbolTable during promotion. Restore it to the new outer
			// symbol table so the outer WITH clause can continue adding its own CTEs.
			Object outerCteListObj = currentQuerySymbolTable.remove(MUMBLE_OUTER_CONTEXT_LIST_KEY);
			if (outerCteListObj instanceof Map<?, ?> outerCteListMapObj) {
				walker.symbolTable.put(MUMBLE_CONTEXT_LIST_KEY, new LinkedHashMap<String, Object>((Map<String, Object>) outerCteListMapObj));
			}
			// Similarly restore any outer def_* entries (e.g. def_delete0) that were saved
			// in enterWith_clause and absorbed during promotion. Without this, getQueryDefinitionSymbol
			// cannot find them and CTE interface resolution fails for non-SELECT outer CTEs.
			Object outerDefEntriesObj = currentQuerySymbolTable.remove(MUMBLE_OUTER_DEF_ENTRIES_KEY);
			if (outerDefEntriesObj instanceof Map<?, ?> outerDefEntriesMap) {
				for (Map.Entry<?, ?> defEntry : outerDefEntriesMap.entrySet()) {
					if (defEntry.getKey() instanceof String defKey) {
						walker.symbolTable.put(defKey, defEntry.getValue());
					}
				}
			}
		}

		walker.addToParent(parentRuleIndex, parentStackLevel, subMap);
		if (currentQuerySymbolTable != null) {
			symbolTreeHelper.hoistMainBodyDeferredUnresolvedFromWithQueryScope(currentQuerySymbolTable);
		}
	}


	@Override
	public void enterWith_clause( SQLSelectParserParser.With_clauseContext ctx) {
		// Each WITH clause owns its own cte_list scope. For nested WITHs the outer
		// WITH clause's in-progress cte_list sits in the SAME symbol table frame
		// (no push has occurred yet). We save it under MUMBLE_OUTER_CONTEXT_LIST_KEY so
		// exitWith_query can restore it once the nested scope collapses back.
		Map<String, Object> existingCteList = symbolTreeHelper.getContextListSymbolMap(walker.symbolTable);
		if (existingCteList != null && !existingCteList.isEmpty()) {
			walker.symbolTable.put(MUMBLE_OUTER_CONTEXT_LIST_KEY, new LinkedHashMap<String, Object>(existingCteList));
		}
		// Also save all outer def_* entries (e.g. def_delete0, def_values0) so that
		// exitWith_query can restore them after the nested scope's putAll absorbs them.
		LinkedHashMap<String, Object> outerDefEntries = new LinkedHashMap<String, Object>();
		for (Map.Entry<String, Object> entry : walker.symbolTable.entrySet()) {
			if (entry.getKey() != null && entry.getKey().startsWith("def_")) {
				outerDefEntries.put(entry.getKey(), entry.getValue());
			}
		}
		if (!outerDefEntries.isEmpty()) {
			walker.symbolTable.put(MUMBLE_OUTER_DEF_ENTRIES_KEY, outerDefEntries);
		}
		// Seed the new cte_list from the outer one so inner CTEs can reference outer
		// aliases (e.g. aaa=delete0 visible inside the nested WITH bbb/ccc scopes).
		LinkedHashMap<String, Object> withCteScope = new LinkedHashMap<String, Object>();
		if (existingCteList != null) {
			withCteScope.putAll(existingCteList);
		}
		walker.symbolTable.put(MUMBLE_CONTEXT_LIST_KEY, withCteScope);
	}

	@Override
	public void exitWith_clause( SQLSelectParserParser.With_clauseContext ctx) {
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

	}

	@Override
	public void exitWith_list_item( SQLSelectParserParser.With_list_itemContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		int parentRuleIndex = ctx.getParent().getRuleIndex();

		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Integer parentStackLevel = walker.currentStackLevel(parentRuleIndex);

		Map<String, Object> subMap = walker.removeNodeMap(ruleIndex, stackLevel);
		if (subMap == null) {
			// TABLE PRIMARY missing recovery map: ctx.getText()
			walker.addToParent(parentRuleIndex, parentStackLevel, new HashMap<String, Object>());
			return;
		}
		Object type = subMap.remove(ASTWALKER_RULE_TYPE_KEY);
		Map<String, Object> item;
		String alias = null;

		if (subMap.size() == 2) {

			alias = (String) subMap.remove("1");

			Map<String, Object> aliasMap = (Map<String, Object>) subMap.remove("2");
		    walker.checkForSubstitutionVariable((Map<String, Object>) aliasMap, MUMBLE_TUPLE_KEY);
			boolean tupleWithSubstitution = symbolTreeHelper.isTupleWithSubstitution(aliasMap);

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
				currentQueryName = symbolTreeHelper.resolveCurrentWithListItemScope(aliasMap);
			}
			// HashMap<String, Object> currentQuerySymbolTable = (HashMap<String, Object>) walker.symbolTable.remove(currentQueryName);
			// Pop the symbol table for this level and add it to the parent level with a unique key.
			Map<String, Object> cteListSymbols = symbolTreeHelper.ensureContextListSymbolMap();
			symbolTreeHelper.emitShadowedParentCteNameWarningIfNeeded(alias, cteListSymbols, ctx);
			walker.collectTableAlias(alias, currentQueryName);

					
			Boolean done = symbolTreeHelper.collectQuerySymbolTable(MUMBLE_QUERY_KEY, alias, cteListSymbols);
			if (!done)
					done = symbolTreeHelper.collectQuerySymbolTable(MUMBLE_INSERT_KEY, alias, cteListSymbols);
			if (!done)
					done = symbolTreeHelper.collectQuerySymbolTable(MUMBLE_UPDATE_KEY, alias, cteListSymbols);
			if (!done)
					done = symbolTreeHelper.collectQuerySymbolTable(MUMBLE_DELETE_KEY, alias, cteListSymbols);
			if (!done)
					done = symbolTreeHelper.collectQuerySymbolTable(MUMBLE_UNION_KEY, alias, cteListSymbols);
			if (!done)
					done = symbolTreeHelper.collectQuerySymbolTable(MUMBLE_INTERSECT_KEY, alias, cteListSymbols);
			if (!done)
					done = symbolTreeHelper.collectQuerySymbolTable(MUMBLE_VALUES_KEY, alias, cteListSymbols);

		} else {
			// Wrong number of entries for WITH list item context: ctx.getText()
		}
		walker.addToParent(parentRuleIndex, parentStackLevel, subMap);
	}

	
	@Override
	public void exitQuery_alias( SQLSelectParserParser.Query_aliasContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		walker.handleOneChild(ruleIndex);
	}

	@Override
	public void exitCte_body( SQLSelectParserParser.Cte_bodyContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		walker.handleOneChild(ruleIndex);
	}

	@Override
	public void exitQuery( SQLSelectParserParser.QueryContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		walker.handleOneChild(ruleIndex);
	}


/*
===============================================================================
  INSERT Statement <insert expression>
===============================================================================
*/

	@Override
	public void enterInsert_expression( SQLSelectParserParser.Insert_expressionContext ctx) {
		walker.pushSymbolTable();
	}

	@Override
	public void exitInsert_expression( SQLSelectParserParser.Insert_expressionContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		int parentRuleIndex = ctx.getParent().getRuleIndex();

		Map<String, Object> subMap = walker.getNodeMap(ruleIndex, stackLevel);
		walker.checkForSubstitutionVariable((Map<String, Object>) subMap.get("1"), MUMBLE_QUERY_KEY);

		walker.handleOneChild(ruleIndex);
		symbolTreeHelper.finalizeInsertScopeSymbolTable();
	}

	@Override
	public void exitSnowflake_insert( SQLSelectParserParser.Snowflake_insertContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Map<String, Object> subMap = walker.removeNodeMap(ruleIndex, stackLevel);
		subMap.remove(ASTWALKER_RULE_TYPE_KEY);
		Map<String, Object> insertNode = new LinkedHashMap<String, Object>();

		String insertPreamble = null;
		Map<String, Object> targetTableNode = null;
		Map<String, Object> sourceNode = null;

		for (int index = 1; subMap.containsKey(String.valueOf(index)); index++) {
			Object entryObj = subMap.get(String.valueOf(index));
			if (entryObj instanceof String entryText) {
				if (insertPreamble == null) {
					insertPreamble = entryText;
				}
				continue;
			}

			if (!(entryObj instanceof Map<?, ?> entryMapObj)) {
				continue;
			}

			Map<String, Object> entryMap = (Map<String, Object>) entryMapObj;
			if (entryMap.containsKey(MUMBLE_FROM_KEY)) {
				sourceNode = entryMap;
			} else if (entryMap.containsKey(MUMBLE_TABLE_KEY)
					|| entryMap.containsKey(MUMBLE_COLUMNS_KEY)
					|| entryMap.containsKey(MUMBLE_SUBSTITUTION_KEY)) {
				targetTableNode = entryMap;
			}
		}

		if (insertPreamble != null) {
			insertNode.put(MUMBLE_INSERT_PREAMBLE_KEY, insertPreamble);
		}
		if (sourceNode != null) {
			insertNode.putAll(sourceNode);
		}

		Map<String, Object> insertColumns = null;
		if (targetTableNode != null) {
			Object columnsObj = targetTableNode.remove(MUMBLE_COLUMNS_KEY);
			if (columnsObj instanceof Map<?, ?> columnsMapObj) {
				insertColumns = (Map<String, Object>) columnsMapObj;
			}
			insertNode.put(MUMBLE_TARGET_TABLE_KEY, targetTableNode);
		}
		if (insertColumns != null && !insertColumns.isEmpty()) {
			insertNode.put(MUMBLE_COLUMNS_KEY, insertColumns);
		}

		String insertTargetTableRef = symbolTreeHelper.getInsertTargetTableReference(insertNode);

		// Source (SELECT / VALUES / TVF) is already resolved in def_queryN / def_valuesN; this only maps target columns.
		symbolTreeHelper.wrapInsertTargetFromResolvedSource(insertTargetTableRef, insertColumns);

		int parentRuleIndex = ctx.getParent().getRuleIndex();
		Integer parentStackLevel = walker.currentStackLevel(parentRuleIndex);
		walker.addToParent(parentRuleIndex, parentStackLevel, insertNode);
		
	}

	
	@Override
	public void exitInsert_target_table_primary( SQLSelectParserParser.Insert_target_table_primaryContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		int parentRuleIndex = ctx.getParent().getRuleIndex();

		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Integer parentStackLevel = walker.currentStackLevel(parentRuleIndex);

		Map<String, Object> subMap = walker.removeNodeMap(ruleIndex, stackLevel);
		subMap.remove(ASTWALKER_RULE_TYPE_KEY);

		Map<String, Object> reference = null;
		Map<String, Object> insertColumns = null;
		String alias = null;
		String insertTargetTableRef = null;

		for (int index = 1; subMap.containsKey(String.valueOf(index)); index++) {
			Object entryObj = subMap.get(String.valueOf(index));
			if (!(entryObj instanceof Map<?, ?> entryMapObj)) {
				continue;
			}

			Map<String, Object> entry = (Map<String, Object>) entryMapObj;
			if (entry.containsKey(MUMBLE_ALIAS_KEY)) {
				Object aliasObj = entry.get(MUMBLE_ALIAS_KEY);
				if (aliasObj instanceof String aliasText) {
					alias = aliasText;
				}
			} else if (symbolTreeHelper.isColumnReferenceListNode(entry)) {
				insertColumns = entry;
			} else if (reference == null) {
				reference = walker.checkForSubstitutionVariable(entry, MUMBLE_TUPLE_KEY);
			}
		}

		subMap.clear();
		if (reference != null) {
			Map<String, Object> targetTable = new HashMap<String, Object>();

			if (reference.containsKey(MUMBLE_TABLE_KEY)) {
				targetTable.putAll(reference);
				targetTable.put(MUMBLE_ALIAS_KEY, alias);
				String qualifiedTableReference = symbolTreeHelper.getQualifiedTableReference(reference);
				insertTargetTableRef = qualifiedTableReference;
				walker.ensureTableDictionaryEntry(qualifiedTableReference);
				if (alias != null && !alias.isBlank()) {
					walker.collectTableAlias(alias, qualifiedTableReference);
				}
				subMap.put(MUMBLE_TABLE_KEY, targetTable);
			} else if (reference.containsKey(MUMBLE_SUBSTITUTION_KEY)) {
				targetTable.putAll(reference);
				targetTable.put(MUMBLE_ALIAS_KEY, alias);
				Map<String, Object> substitution = (Map<String, Object>) reference.get(MUMBLE_SUBSTITUTION_KEY);
				String tableName = resolveSubstitutionTableReference(substitution);
				if (tableName != null) {
					insertTargetTableRef = tableName;
					walker.ensureTableDictionaryEntry(tableName);
					if (alias != null && !alias.isBlank()) {
						walker.collectTableAlias(alias, tableName);
					}
				}
				subMap.put(MUMBLE_TABLE_KEY, targetTable);
			}
		}

		if (insertColumns != null && !insertColumns.isEmpty()) {
			subMap.put(MUMBLE_COLUMNS_KEY, insertColumns);
			symbolTreeHelper.recordInsertTargetColumnListLocation(ctx);
			symbolTreeHelper.populateInsertTargetColumnsFromTargetSubtree(insertTargetTableRef, insertColumns, ctx);
		}

		walker.addToParent(parentRuleIndex, parentStackLevel, subMap);
	}

	
	@Override
	public void exitInsert_preamble( SQLSelectParserParser.Insert_preambleContext ctx) {
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
		} else {
			// Wrong number of entries for insert preamble context: ctx.getText()
		}

		walker.handleOneChild(ruleIndex);
	}

	/*
	===============================================================================
	  UPDATE Statement <update expression>
	===============================================================================
	*/

	@Override
	public void enterUpdate_expression( SQLSelectParserParser.Update_expressionContext ctx) {
		walker.pushSymbolTable();
	}

	@Override
	public void exitUpdate_expression( SQLSelectParserParser.Update_expressionContext ctx) {
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
						// Too Many Entries for update expression segment
					}
				}
			}
		}
		subMap.clear();
		subMap.put(MUMBLE_UPDATE_KEY, updateNode);

		symbolTreeHelper.normalizeFromClauseCteAliasMappings(updateNode);
		symbolTreeHelper.finalizeUpdateScopeSymbolTable(updateNode);
	}

	@Override
	public void exitDelete_expression( SQLSelectParserParser.Delete_expressionContext ctx) {
		// Pass-through wrapper: promotes the single dialect variant child up to the parent.
		walker.handleOneChild(ctx.getRuleIndex());
	}

	@Override
	public void enterDelete_snowflake_expression( SQLSelectParserParser.Delete_snowflake_expressionContext ctx) {
		walker.pushSymbolTable();
	}

	
	@Override
	public void exitDelete_snowflake_expression( SQLSelectParserParser.Delete_snowflake_expressionContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Map<String, Object> subMap = walker.getNodeMap(ruleIndex, stackLevel);
		String[] keys = new String[1];
		Object type = subMap.remove(ASTWALKER_RULE_TYPE_KEY);
		Map<String, Object> deleteNode = new HashMap<String, Object>();

		keys = subMap.keySet().toArray(keys);

		for (String key : keys) {
			Object obj = subMap.remove(key);
			if (obj instanceof String) {
				// skip keyword literals
			} else {
				HashMap<String, Object> value = (HashMap<String, Object>) obj;
				Integer childKey = (Integer) (value).remove(ASTWALKER_RULE_TYPE_KEY);
				if (childKey == null) {
					deleteNode.putAll(value);
				} else {
					Object segment = value.remove(childKey.toString());
					if (childKey.equals((Integer) SQLSelectParserParser.RULE_delete_using_clause)) {
						deleteNode.put(MUMBLE_USING_KEY, segment);
					} else if (childKey.equals((Integer) SQLSelectParserParser.RULE_where_clause)) {
						HashMap<String, Object> item = (HashMap<String, Object>) segment;
						item = (HashMap<String, Object>) item.remove("1");
						deleteNode.put(MUMBLE_WHERE_KEY, item);
					} else {
						// Too Many Entries for delete (snowflake) expression segment
					}
				}
			}
		}

		subMap.clear();
		subMap.put(MUMBLE_DELETE_KEY, deleteNode);

		symbolTreeHelper.normalizeFromClauseCteAliasMappings(deleteNode);
		symbolTreeHelper.finalizeDeleteScopeSymbolTable(deleteNode, false);
	}

	@Override
	public void enterDelete_postgres_expression( SQLSelectParserParser.Delete_postgres_expressionContext ctx) {
		walker.pushSymbolTable();
	}

	
	@Override
	public void exitDelete_postgres_expression( SQLSelectParserParser.Delete_postgres_expressionContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Map<String, Object> subMap = walker.getNodeMap(ruleIndex, stackLevel);
		String[] keys = new String[1];
		Object type = subMap.remove(ASTWALKER_RULE_TYPE_KEY);
		Map<String, Object> deleteNode = new HashMap<String, Object>();

		keys = subMap.keySet().toArray(keys);

		for (String key : keys) {
			Object obj = subMap.remove(key);
			if (obj instanceof String) {
				// skip keyword literals
			} else {
				HashMap<String, Object> value = (HashMap<String, Object>) obj;
				Integer childKey = (Integer) (value).remove(ASTWALKER_RULE_TYPE_KEY);
				if (childKey == null) {
					deleteNode.putAll(value);
				} else {
					Object segment = value.remove(childKey.toString());
					if (childKey.equals((Integer) SQLSelectParserParser.RULE_delete_using_clause)) {
						deleteNode.put(MUMBLE_USING_KEY, segment);
					} else if (childKey.equals((Integer) SQLSelectParserParser.RULE_where_clause)) {
						HashMap<String, Object> item = (HashMap<String, Object>) segment;
						item = (HashMap<String, Object>) item.remove("1");
						deleteNode.put(MUMBLE_WHERE_KEY, item);
					} else if (childKey.equals((Integer) SQLSelectParserParser.RULE_delete_returning)) {
						deleteNode.put(MUMBLE_RETURNING_KEY, segment);
					} else {
						// Too Many Entries for delete (postgres) expression segment
					}
				}
			}
		}

		subMap.clear();
		subMap.put(MUMBLE_DELETE_KEY, deleteNode);

		// RETURNING columns were already registered as query interface entries by exitSelect_item.
		symbolTreeHelper.normalizeFromClauseCteAliasMappings(deleteNode);
		symbolTreeHelper.finalizeDeleteScopeSymbolTable(deleteNode, true);
	}

	@Override
	
	public void exitDelete_returning( SQLSelectParserParser.Delete_returningContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Map<String, Object> subMap = walker.removeNodeMap(ruleIndex, stackLevel);
		Object type = subMap.remove(ASTWALKER_RULE_TYPE_KEY);

		Object segment = subMap.get("1");
		if (segment instanceof Map<?, ?> segmentMap) {
			Map<String, Object> typedSegment = (Map<String, Object>) segmentMap;
			Object childType = typedSegment.get(ASTWALKER_RULE_TYPE_KEY);
			if (childType != null) {
				Object flattened = typedSegment.get(childType.toString());
				if (flattened != null) {
					segment = flattened;
				}
			}
		}

		Map<String, Object> newMap = walker.collectNewRuleMap(ruleIndex, stackLevel);
		newMap.put(type.toString(), segment);
	}

	
	// TODO: Add to AST
//	@Override
//	public void exitReturning( SQLSelectParserParser.ReturningContext ctx) {
//	}
	
	@Override
	public void exitAssignment_expression_list( SQLSelectParserParser.Assignment_expression_listContext ctx) {
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
	public void exitAssignment_expression( SQLSelectParserParser.Assignment_expressionContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		int parentRuleIndex = ctx.getParent().getRuleIndex();
		Integer parentStackLevel = walker.currentStackLevel(parentRuleIndex);

		Map<String, Object> subMap = walker.removeNodeMap(ruleIndex, stackLevel);
		Object type = subMap.remove(ASTWALKER_RULE_TYPE_KEY);

		if (subMap.size() == 2) {
			Map<String, Object> left = (Map<String, Object>) subMap.remove("1");
			subMap.put(MUMBLE_SET_KEY, left);

			Map<String, Object> right = (Map<String, Object>) subMap.remove("2");
			subMap.put(MUMBLE_TO_KEY, right);

			String assignmentKey = symbolTreeHelper.extractAssignmentLhsName(left);
			if (assignmentKey != null && !assignmentKey.isBlank()) {
				symbolTreeHelper.addUpdateAssignmentSymbolReference(
						assignmentKey,
						right,
						symbolTreeHelper.resolveAssignmentLhsTokenString(ctx),
						symbolTreeHelper.resolveAssignmentRhsToken(ctx));
				symbolTreeHelper.moveAssignmentLhsToLhsUnresolvedColumns(left);
			}

		} else {
			//Wrong number of entries
		}
		walker.addToParent(parentRuleIndex, parentStackLevel, subMap);

	}

	
	/*
	===============================================================================
	  VALUES Statement as Tuple
	===============================================================================
	*/

	@Override
	public void enterValues_statement_primary( SQLSelectParserParser.Values_statement_primaryContext ctx) {
		walker.pushSymbolTable();
	}

	@Override
	public void exitValues_statement_primary( SQLSelectParserParser.Values_statement_primaryContext ctx) {
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
				// Wrong number of entries
			}
			walker.addToParent(parentRuleIndex, parentStackLevel, item);
			
			// Finish Symbol Table Construction
			symbolTreeHelper.finalizeValuesScopeSymbolTable();
		}
		
		
		@Override
		public void exitFully_defined_values_statement( SQLSelectParserParser.Fully_defined_values_statementContext ctx) {
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
				walker.symbolTable.put(MUMBLE_INTERFACE_KEY, symbolTreeHelper.buildValuesOutputInterface(hold));
				
				// Resolve Symbol Table, add alias from Values statement to the Symbol Table.
			} else {
				// Wrong number of entries
			}

			walker.addToParent(parentRuleIndex, parentStackLevel, subMap);
			
		}
		
		
		@Override
		public void exitAliased_values_statement( SQLSelectParserParser.Aliased_values_statementContext ctx) {
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
				// Wrong number of entries
			}

			walker.addToParent(parentRuleIndex, parentStackLevel, subMap);
		}
		
		
		@Override
		public void exitValues_statement( SQLSelectParserParser.Values_statementContext ctx) {
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
				// Wrong number of entries
			}
			// Build values symbols from current scope structures so VALUES uses the same
			// query-dictionary-driven lifecycle as query sources.
			Map<String, Object> valueColumns = symbolTreeHelper.resolveCurrentValuesColumns();
			walker.symbolTable.put(MUMBLE_VALUES_KEY, valueColumns);
			walker.symbolTable.put(MUMBLE_INTERFACE_KEY, symbolTreeHelper.buildValuesOutputInterface(valueColumns));

			// Add the matrix to the SQL Tree
			Map<String, Object> item = new HashMap<String, Object>();
			item.put(MUMBLE_VALUES_KEY, subMap);
			walker.addToParent(parentRuleIndex, parentStackLevel, item);

		}
		
		@Override
		public void exitValues_matrix( SQLSelectParserParser.Values_matrixContext ctx) {
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
		public void exitValues_row( SQLSelectParserParser.Values_rowContext ctx) {
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
		public void exitValues_aliases( SQLSelectParserParser.Values_aliasesContext ctx) {
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
		public void exitValues_aliases_list( SQLSelectParserParser.Values_aliases_listContext ctx) {
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
					symbolTreeHelper.addAliasTokensObject(name, tokenString);
				}
			}
		}
						
		@Override
		public void enterInsert_values_statement( SQLSelectParserParser.Insert_values_statementContext ctx) {
			walker.pushSymbolTable();
		}

		
		@Override
		public void exitInsert_values_statement( SQLSelectParserParser.Insert_values_statementContext ctx) {
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
				// Wrong number of entries
			}

			Map<String, Object> item = new HashMap<String, Object>();
			item.put(MUMBLE_VALUES_KEY, subMap);
			walker.addToParent(parentRuleIndex, parentStackLevel, item);

			Map<String, Object> valueColumns = symbolTreeHelper.resolveCurrentValuesColumns();
			walker.symbolTable.put(MUMBLE_VALUES_KEY, valueColumns);
			walker.symbolTable.put(MUMBLE_INTERFACE_KEY, symbolTreeHelper.buildValuesOutputInterface(valueColumns));

			symbolTreeHelper.finalizeValuesScopeSymbolTable();

		}

	// End Values Statement	


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
	public void enterIntersected_query( SQLSelectParserParser.Intersected_queryContext ctx) {
		symbolTreeHelper.pushSymbolTableWithParentVisibleScope();
	}

	@Override
	public void exitIntersected_query( SQLSelectParserParser.Intersected_queryContext ctx) {
		int ruleIndex = ctx.getRuleIndex();

		walker.handleOperandList(ruleIndex, MUMBLE_INTERSECT_KEY);

		// Handle symbol tables
		HashMap<String, Object> symbols =  walker.symbolTable;

		if (walker.intersectClauseFound) {
			boolean insertSource = walker.currentStackLevel(SQLSelectParserParser.RULE_insert_source_primary) != null;
			symbolTreeHelper.finalizeSetOperationScopeSymbolTable(
					MUMBLE_INTERSECT_KEY + walker.queryCount,
					symbols,
					insertSource);
		} else {
			symbolTreeHelper.popFrameAndMergeIntoParent(symbols);
		}
	}

	@Override
	public void enterIntersect_clause( SQLSelectParserParser.Intersect_clauseContext ctx) {
		if (!walker.intersectClauseFound) {
			walker.intersectClauseFound = true;
			walker.firstIntersectClause = true;
		} else
			walker.firstIntersectClause = false;
	}

	@Override
	public void exitIntersect_clause( SQLSelectParserParser.Intersect_clauseContext ctx) {
		if (walker.firstIntersectClause && ctx != null && ctx.getStart() != null) {
			walker.setCurrentSetOperationOperatorAnchor(ctx.getStart());
		}

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
			// Trace removed: Wrong number of entries: {ctx.getText()}
		}
		// Trace removed: Intersect Operator: {subMap}

		// Get first interface to represent intersection output
		if (walker.firstIntersectClause) {
			HashMap<String, Object> interfac = walker.captureQueryInterface();
			// Trace removed: Intersect So Far: {walker.symbolTable}

		}
	}

	// Intersect_operator does not need its own logic
	
	@Override
	public void enterUnionized_query( SQLSelectParserParser.Unionized_queryContext ctx) {
		symbolTreeHelper.pushSymbolTableWithParentVisibleScope();
	}

	@Override
	public void exitUnionized_query( SQLSelectParserParser.Unionized_queryContext ctx) {
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
			boolean insertSource = walker.currentStackLevel(SQLSelectParserParser.RULE_insert_source_primary) != null;
			symbolTreeHelper.finalizeSetOperationScopeSymbolTable(
					MUMBLE_UNION_KEY + walker.queryCount,
					symbols,
					insertSource);
		} else {
			symbolTreeHelper.popFrameAndMergeIntoParent(symbols);
		}

	}

	@Override
	public void enterUnion_clause( SQLSelectParserParser.Union_clauseContext ctx) {
		if (!walker.unionClauseFound) {
			walker.unionClauseFound = true;
			walker.firstUnionClause = true;
		} else
			walker.firstUnionClause = false;
	}

	@Override
	public void exitUnion_clause( SQLSelectParserParser.Union_clauseContext ctx) {
		if (walker.firstUnionClause && ctx != null && ctx.getStart() != null) {
			walker.setCurrentSetOperationOperatorAnchor(ctx.getStart());
		}

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
			// Trace removed: Wrong number of entries: {ctx.getText()}
		}
		// Trace removed: Union Operator: {subMap}

		// Get first interface to represent union output
		if (walker.firstUnionClause) {
			walker.captureQueryInterface();
			// Trace removed: Union So Far: {walker.symbolTable}
		}

	}
	
	// Union_operator does NOT need its own method
	

/*
===============================================================================
  SELECT Statement <query primary>
===============================================================================
*/


	@Override
	public void exitSet_operation_member( SQLSelectParserParser.Set_operation_memberContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Map<String, Object> subMap = walker.removeNodeMap(ruleIndex, stackLevel);
		if (subMap == null) {
			return;
		}
		subMap.remove(ASTWALKER_RULE_TYPE_KEY);

		Map<String, Object> source = (Map<String, Object>) subMap.remove("1");
		walker.checkForSubstitutionVariable(source, MUMBLE_QUERY_KEY);

		if (subMap.isEmpty()) {
			walker.collect(ruleIndex, stackLevel, source);
			return;
		}

		if (subMap.size() == 1 && subMap.containsKey("2") && subMap.get("2") instanceof Map<?, ?> aliasMapObj) {
			Map<String, Object> aliasMap = (Map<String, Object>) aliasMapObj;
			Map<String, Object> aliasedSource = new HashMap<String, Object>();
			if (source != null) {
				aliasedSource.putAll(source);
			}
			aliasedSource.putAll(aliasMap);
			walker.collect(ruleIndex, stackLevel, aliasedSource);
			return;
		}

		walker.collect(ruleIndex, stackLevel, source);
	}

	@Override
	public void exitQuery_primary( SQLSelectParserParser.Query_primaryContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		int parentRuleIndex = ctx.getParent().getRuleIndex();

		Map<String, Object> subMap = walker.getNodeMap(ruleIndex, stackLevel);
		walker.checkForSubstitutionVariable((Map<String, Object>) subMap.get("1"), MUMBLE_QUERY_KEY);

		walker.handleOneChild(ruleIndex);
	}

	@Override
	public void exitSubquery( SQLSelectParserParser.SubqueryContext ctx) {
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
	public void enterQuery_specification( SQLSelectParserParser.Query_specificationContext ctx) {
		symbolTreeHelper.pushSymbolTableWithParentVisibleScope();
		walker.beginQuerySpecificationFromClause();
	}

	
	@Override
	public void exitQuery_specification( SQLSelectParserParser.Query_specificationContext ctx) {
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
				//	 Unexpected query_specification child type
				continue;
			}

			HashMap<String, Object> value = (HashMap<String, Object>) valueObj;
			Integer childKey = (Integer) (value).remove(ASTWALKER_RULE_TYPE_KEY);
			if (childKey == null) {
				//	 Missing child rule type
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
				//Too Many Entries
			}
		}
		symbolTreeHelper.normalizeFromClauseCteAliasMappings(subMap);

		symbolTreeHelper.finalizeQueryScopeSymbolTable(
				ctx,
				subMap,
				shouldProjectSelectIntoForQuerySpecification(ctx));
	}

	
/*
===============================================================================
  SELECT Details
===============================================================================
*/
 // TODO: Select Into Table syntax has not been implemented
	
// set_qualifier needs to insert into parent object
	

	@Override
	public void exitSet_qualifier( SQLSelectParserParser.Set_qualifierContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		int parentRuleIndex = ctx.getParent().getRuleIndex();

		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Integer parentStackLevel = walker.currentStackLevel(parentRuleIndex);
		
		String item = ctx.getChild(0).getText();
		
		Map<String, Object> subMap = new HashMap<String, Object>();
		subMap.put(MUMBLE_QUALIFIER_KEY, item);
		subMap.put(ASTWALKER_RULE_TYPE_KEY, SQLSelectParserParser.RULE_set_qualifier);
	
		walker.addToParent(parentRuleIndex, parentStackLevel, subMap);
	}

	@Override
	public void exitInto_list( SQLSelectParserParser.Into_listContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		walker.handlePushDown(ruleIndex);
	}

	@Override
	public void exitSelect_list( SQLSelectParserParser.Select_listContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		int parentRuleIndex = ctx.getParent().getRuleIndex();
		// then parent is normal query
		walker.handlePushDown(ruleIndex);
	}

	
	@Override
	public void exitSelect_item( SQLSelectParserParser.Select_itemContext ctx) {
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
		item = walker.checkForSubstitutionVariable((Map<String, Object>) subMap.remove("1"), MUMBLE_PREDICAND_KEY);

		// make a copy of the item AST subtree without the alias for use in the symbol table interface
		interfaceReference.putAll(item);

		// derive an alias for the item if it does not have one, and add the alias to the item if it does not have one
		boolean explicitOutputAlias = false;
		if (subMap.size() == 0) {
			// Select Item did not have an Alias, construct one from options
			aliasToken = ctx.getStop().toString();
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
			explicitOutputAlias = true;
			// Select Item has an alias, extract it and add it back into the item map for use in the SQL Tree and Symbol Table construction
			aliasToken = ctx.getStop().toString();	

			Map<String, Object> aliasMap = (Map<String, Object>) subMap.remove("2");
			interfaceAlias = (String) aliasMap.get(MUMBLE_ALIAS_KEY);

			if (item.containsKey(MUMBLE_SELECT_KEY)) {
			// then this item is a subquery, so we need to push it down under a LOOKUP subtree in the AST
				Map<String, Object> lookup = new HashMap<String, Object>();
				lookup.putAll(item);
				item = new HashMap<String, Object>();
				item.put(MUMBLE_LOOKUP_KEY, lookup);
			}
			// Add the alias back into the item map for use in the SQL Tree and Symbol Table construction
			((Map<String, Object>) item).putAll(aliasMap);
		}
		walker.addToParent(parentRuleIndex, parentStackLevel, item);

		// Add item to symbol table
		HashMap<String, Object> selectInterface = (HashMap<String, Object>)  walker.symbolTable.get(MUMBLE_INTERFACE_KEY);
		if (selectInterface == null) {
			selectInterface = new HashMap<String, Object>();
			 walker.symbolTable.put(MUMBLE_INTERFACE_KEY, selectInterface);
		}

		// Simplify interface reference map by standardizing it into a flat map of column references and not the entire AST subtree
		ArrayList<Object> columnList = new ArrayList<Object>();
		symbolTreeHelper.flattenSubTreeForDependencyColumns(interfaceReference, columnList);

		Object existingInterfaceEntry = selectInterface.get(interfaceAlias);
		if (existingInterfaceEntry != null) {
			symbolTreeHelper.emitDuplicateInterfaceColumnFatal(interfaceAlias, existingInterfaceEntry, columnList, aliasToken);
		}

		selectInterface.put(interfaceAlias, columnList);
		symbolTreeHelper.recordInsertSourceSelectItemSequence(interfaceAlias);
		if (symbolTreeHelper.isQueryBackedSelectItemReference(interfaceReference)) {
			symbolTreeHelper.addCurrentQueryScalarSubqueryAlias(interfaceAlias);
		}

		// Phase 1 (output origins): every interface column name gets its defining token on this
		// scope's query_dictionary before scope exit. Phase 2 (at convert/finalize) records
		// external qualified usages on the source scope's query_dictionary (or table_dictionary
		// for physical sources) so every reference location is accounted for.
		if (aliasToken != null && interfaceAlias != null) {
			symbolTreeHelper.addAliasTokensObject(interfaceAlias, aliasToken);
		}
		
	}


	// Standardize the interface reference map into a flat map of column references and not the entire AST subtree
	// This is a recursive function that traverses the item subtree until it finds column references or substitution variables, 
	// which it adds to the column list with the alias as the key


	@Override
	public void exitAs_clause( SQLSelectParserParser.As_clauseContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Map<String, Object> subMap = walker.getNodeMap(ruleIndex, stackLevel);
		Object type = subMap.remove(ASTWALKER_RULE_TYPE_KEY);

		if (subMap.size() == 1) {
			String alias = (String) subMap.remove("1");
			subMap.put(MUMBLE_ALIAS_KEY, alias);
		} else {
			// Wrong number of entries
		}

	}

	@Override
	public void exitRelation_as_clause( SQLSelectParserParser.Relation_as_clauseContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Map<String, Object> subMap = walker.getNodeMap(ruleIndex, stackLevel);
		Object type = subMap.remove(ASTWALKER_RULE_TYPE_KEY);

		if (subMap.size() == 1) {
			String alias = (String) subMap.remove("1");
			subMap.put(MUMBLE_ALIAS_KEY, alias);
		} else {
			// Wrong number of entries
		}

	}

	@Override
	public void exitSelect_all_columns( SQLSelectParserParser.Select_all_columnsContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		walker.handleOneChild(ruleIndex);
	}

	@Override
	public void exitWildcard_reference( SQLSelectParserParser.Wildcard_referenceContext ctx) {
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
	}

/*
===============================================================================
  FROM Statement <from clause>
===============================================================================
*/

	@Override
	public void exitFrom_clause( SQLSelectParserParser.From_clauseContext ctx) {
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
		if (symbolTreeHelper.isQuerySpecificationFromClause(ctx)) {
			walker.markCurrentQueryFromClauseComplete();
		}
		walker.handlePushDown(ruleIndex);
	}

	@Override
	public void exitDelete_using_clause( SQLSelectParserParser.Delete_using_clauseContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		walker.handlePushDown(ruleIndex);
	}
	
	// RULE_join_extension

	@Override
	public void exitJoin_extension( SQLSelectParserParser.Join_extensionContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		int stackLevel = walker.currentStackLevel(ruleIndex);
		int parentRuleIndex = ctx.getParent().getRuleIndex();
		Map<String, Object> subMap = walker.getNodeMap(ruleIndex, stackLevel);
		Map<String, Object> reference = walker.checkForSubstitutionVariable((Map<String, Object>) subMap.get("1"),
				MUMBLE_JOIN_EXTENSION_TYPE_KEY);

		walker.handleOneChild(ruleIndex);
	}

	@Override
	public void exitTable_reference_list( SQLSelectParserParser.Table_reference_listContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Map<String, Object> subMap = walker.removeNodeMap(ruleIndex, stackLevel);
		subMap.remove(ASTWALKER_RULE_TYPE_KEY);

		normalizeLateralModifierEntries(subMap);

		if (subMap.size() == 1) {
			Map<String, Object> item = (Map<String, Object>) subMap.remove("1");
			walker.collect(ruleIndex, stackLevel, item);
		} else if (subMap.size() >= 2) {
			HashMap<String, Object> item = new HashMap<String, Object>();
			item.put(MUMBLE_JOIN_KEY, subMap);
			walker.collect(ruleIndex, stackLevel, item);
		} else {
			//Wrong number of entries
		}
	}

	
	@Override
	public void exitJoin_extension_primary( SQLSelectParserParser.Join_extension_primaryContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		int parentRuleIndex = ctx.getParent().getRuleIndex();

		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Integer parentStackLevel = walker.currentStackLevel(parentRuleIndex);

		Map<String, Object> subMap = walker.removeNodeMap(ruleIndex, stackLevel);
		Object type = subMap.remove(ASTWALKER_RULE_TYPE_KEY);

		normalizeLateralModifierEntries(subMap);

		walker.addToParent(parentRuleIndex, parentStackLevel, subMap);
		symbolTreeHelper.reconcileJoinExtensionSymbolTable();
	}

	private void normalizeLateralModifierEntries(Map<String, Object> subMap) {
		if (subMap == null || subMap.isEmpty()) {
			return;
		}

		for (int index = 1; subMap.containsKey(String.valueOf(index)); index++) {
			String key = String.valueOf(index);
			Object entry = subMap.get(key);
			if (entry instanceof String entryText && "lateral".equalsIgnoreCase(entryText)) {
				Map<String, Object> modifier = new HashMap<String, Object>();
				modifier.put(MUMBLE_MODIFIER_KEY, entryText);
				subMap.put(key, modifier);
			}
		}
	}


	
	@Override
	public void exitTable_source_primary( SQLSelectParserParser.Table_source_primaryContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		int parentRuleIndex = ctx.getParent().getRuleIndex();

		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Integer parentStackLevel = walker.currentStackLevel(parentRuleIndex);

		Map<String, Object> subMap = walker.removeNodeMap(ruleIndex, stackLevel);
		if (subMap == null && ctx.getChild(0) instanceof SQLSelectParserParser.Variable_identifierContext varCtx) {
			// Grammar-based detection: table_source_primary -> variable_identifier as_clause
			// This matches the grammar rule directly, regardless of naming convention or child count
			String sourceText = recoverVariableNameFromToken(varCtx.getStart());
			String aliasText = ctx.getChildCount() >= 2 ? ctx.getChild(1).getText() : null;
			
			subMap = walker.makeRuleMap(ruleIndex);

			Map<String, Object> substitution = new HashMap<String, Object>();
			substitution.put(MUMBLE_NAME_KEY, sourceText);

			Map<String, Object> syntheticReference = new HashMap<String, Object>();
			syntheticReference.put(MUMBLE_SUBSTITUTION_KEY, substitution);
			subMap.put("1", syntheticReference);

			Map<String, Object> aliasMap = new HashMap<String, Object>();
			aliasMap.put(MUMBLE_ALIAS_KEY, aliasText);
			subMap.put("2", aliasMap);

			emitInvalidVariableDiagnostic(varCtx.getStart(), sourceText);
		}
		if (subMap == null) {
			throw new IllegalStateException("Missing AST node map for table_primary at: " + ctx.getText());
		}
		Object type = subMap.remove(ASTWALKER_RULE_TYPE_KEY);
		Map<String, Object> item;
		String alias = null;

		try {
		if (ctx.getChildCount() == 1) {
			item = (Map<String, Object>) subMap.remove("1");
			if (item.keySet().contains(MUMBLE_TABLE_KEY)) {
				item.put(MUMBLE_ALIAS_KEY, null);

				Object table = item.get(MUMBLE_TABLE_KEY);
				if (table != null) {
					String qualifiedTableReference = symbolTreeHelper.getQualifiedTableReference(item);
					String cteScopeReference = symbolTreeHelper.resolveCteOrExistingQueryScopeInVisibleScopes(qualifiedTableReference);
					// Table doesn't have an Alias, so it doesn't need to be collected in the Table ALias map
					// walker.collectTableAlias(alias, table);

					// However, we still need to collect the table reference in the AST 
					subMap.put(MUMBLE_TABLE_KEY, item);

					// Only physical tables are registered in table_dictionary. CTE references are query-backed.
					if (cteScopeReference == null) {
						walker.ensureTableDictionaryEntry(qualifiedTableReference);
					} else {
						symbolTreeHelper.upsertCurrentTableAliasMapping(qualifiedTableReference, cteScopeReference);
					}
				}
			} else if (item.keySet().contains(MUMBLE_SUBSTITUTION_KEY)) {
				item = walker.checkForSubstitutionVariable(item, MUMBLE_TUPLE_KEY);
				item.put(MUMBLE_ALIAS_KEY, null);
				subMap.put(MUMBLE_TABLE_KEY, item);
				Map<String, Object> substitution = (Map<String, Object>) item.get(MUMBLE_SUBSTITUTION_KEY);
				String tableName = resolveSubstitutionTableReference(substitution);
				if (tableName != null) {
					// Only register as table source if it's a TUPLE substitution, not COLUMN or PREDICAND
					Object substitutionTypeObj = substitution.get(MUMBLE_TYPE_KEY);
					String substitutionType = substitutionTypeObj == null ? null : substitutionTypeObj.toString();
					if (substitutionType == null || (!MUMBLE_COLUMN_KEY.equals(substitutionType) && !MUMBLE_PREDICAND_KEY.equals(substitutionType))) {
						walker.ensureTableDictionaryEntry(tableName);
					}
				}
			} else if (item.keySet().contains(MUMBLE_TABLE_FUNCTION_KEY)) {
				// Table functions are table sources, not query sources.
				subMap.putAll(item);
				symbolTreeHelper.registerTableFunctionSourceReference(item, null);
			} else if (item.containsKey(MUMBLE_VALUES_KEY)) {
				subMap.putAll(item);

				// exitValues_statement always stores a Map under MUMBLE_VALUES_KEY
				Object valuesObject = item.get(MUMBLE_VALUES_KEY);
				if (valuesObject instanceof Map<?, ?> valuesMapObj) {
					Map<String, Object> valuesMap = (Map<String, Object>) valuesMapObj;
					String valuesScopeKey = symbolTreeHelper.findTopLevelValuesScopeKey();
					Object aliasObj = valuesMap.get(MUMBLE_ALIAS_KEY);
					if (aliasObj instanceof String valuesAlias && !valuesAlias.isBlank()) {
						symbolTreeHelper.wrapValuesScopeAsDefinition(valuesScopeKey);
						// NOTE: The alias is tracked in table_alias via collectTableAlias() below,
						// so we do NOT create a nested submap in QCD via addCurrentScopeValuesAliasMapping().
						// This prevents the VALUES columns from appearing both in a nested submap
						// and at the top level of the parent query's QCD entry.
						walker.collectTableAlias(valuesAlias, valuesScopeKey);
					} else {
						symbolTreeHelper.registerUnaliasedFromSource(item);
					}
				}

			} else {
				if (shouldWrapSetOperationAtCurrentLevel(stackLevel, item)) {
					Map<String, Object> tableItem = new HashMap<String, Object>();
					tableItem.put(MUMBLE_ALIAS_KEY, null);
					tableItem.put(MUMBLE_QUERY_KEY, item);
					subMap.put(MUMBLE_TABLE_KEY, tableItem);
					registerQueryLikeFromSource(item, null);
				} else {
					subMap.putAll(item);
					symbolTreeHelper.registerUnaliasedFromSource(item);
				}
			}

		} else if (ctx.getChildCount() == 2) {
			item = new HashMap<String, Object>();
			Map<String, Object> reference = walker.checkForSubstitutionVariable((Map<String, Object>) subMap.remove("1"),
					MUMBLE_TUPLE_KEY);

			Map<String, Object> aliasMap = (Map<String, Object>) subMap.remove("2");
			alias = (String) aliasMap.get(MUMBLE_ALIAS_KEY);
			item.putAll(aliasMap);

			// Try various alternatives
			if (reference.containsKey(MUMBLE_TABLE_KEY)) {
				String tableRef = symbolTreeHelper.getQualifiedTableReference(reference);
				String cteScopeReference = symbolTreeHelper.resolveCteOrExistingQueryScopeInVisibleScopes(tableRef);
				item.putAll(reference);
				if (cteScopeReference != null) {
					symbolTreeHelper.upsertCurrentTableAliasMapping(alias, cteScopeReference);
					symbolTreeHelper.upsertCurrentTableAliasMapping(tableRef, cteScopeReference);
				} else {
					walker.collectTableAlias(alias, tableRef);
					walker.ensureTableDictionaryEntry(tableRef);
				}
				
			} else if (reference.containsKey(MUMBLE_SUBSTITUTION_KEY)) {
				// Check for Substitution Variable
				item.putAll(reference);
				// Collect Symbol Table Reference
				Map<String, Object> substitution = (Map<String, Object>) reference.get(MUMBLE_SUBSTITUTION_KEY);
				String tableName = resolveSubstitutionTableReference(substitution);
				// Only register as table source if it's a TUPLE substitution, not COLUMN or PREDICAND
				Object substitutionTypeObj = substitution.get(MUMBLE_TYPE_KEY);
				String substitutionType = substitutionTypeObj == null ? null : substitutionTypeObj.toString();
				if (substitutionType == null || (!MUMBLE_COLUMN_KEY.equals(substitutionType) && !MUMBLE_PREDICAND_KEY.equals(substitutionType))) {
					walker.collectTableAlias(alias, tableName);
					walker.ensureTableDictionaryEntry(tableName);
				}
			} else {// then it's a query, add it to the tree no matter what kind of query it is
				item.put(MUMBLE_QUERY_KEY, reference);
				registerQueryLikeFromSource(reference, alias);
			}

			subMap.put(MUMBLE_TABLE_KEY, item);
		} else {
			// Wrong number of entries
		}

		walker.addToParent(parentRuleIndex, parentStackLevel, subMap);
		} finally {
			if (!setOperationWrapAnchorStackLevels.isEmpty()
					&& stackLevel != null
					&& stackLevel.equals(setOperationWrapAnchorStackLevels.peek())) {
				setOperationWrapAnchorStackLevels.pop();
			}

			if (tableSourcePrimaryNestingDepth > 0) {
				tableSourcePrimaryNestingDepth--;
			}
		}
	}

	private boolean isQueryLikeFromSource(Map<String, Object> reference) {
		if (reference == null || reference.isEmpty()) {
			return false;
		}

		return  reference.containsKey(MUMBLE_UNION_KEY)
				|| reference.containsKey(MUMBLE_INTERSECT_KEY);
	}

	private void registerQueryLikeFromSource(Map<String, Object> reference, String alias) {
		if (reference == null || reference.isEmpty()) {
			return;
		}

		Boolean done = symbolTreeHelper.collectQuerySymbolTable(MUMBLE_QUERY_KEY, alias);
		if (!done)
			done = symbolTreeHelper.collectQuerySymbolTable(MUMBLE_INSERT_KEY, alias);
		if (!done)
			done = symbolTreeHelper.collectQuerySymbolTable(MUMBLE_UPDATE_KEY, alias);
		if (!done)
			done = symbolTreeHelper.collectQuerySymbolTable(MUMBLE_DELETE_KEY, alias);
		if (!done)
			done = symbolTreeHelper.collectQuerySymbolTable(MUMBLE_UNION_KEY, alias);
		if (!done)
			done = symbolTreeHelper.collectQuerySymbolTable(MUMBLE_INTERSECT_KEY, alias);
	}

	// exitTable_primary: composes table_source_primary + optional table_relational_modifier + optional relation_as_clause.
	// Slot "1" is always the processed table source result from exitTable_source_primary.
	// Optional additional slots are classified by content:
	//   - Map with MUMBLE_ALIAS_KEY   → outer relation_as_clause alias
	//   - Map with MUMBLE_UNPIVOT_KEY → unpivot_clause (from table_relational_modifier)
	//   - Map with MUMBLE_PIVOT_KEY   → pivot_clause (from table_relational_modifier)
	
	@Override
	public void exitTable_primary( SQLSelectParserParser.Table_primaryContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		int parentRuleIndex = ctx.getParent().getRuleIndex();

		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Integer parentStackLevel = walker.currentStackLevel(parentRuleIndex);

		   Map<String, Object> subMap = walker.removeNodeMap(ruleIndex, stackLevel);
		   subMap.remove(ASTWALKER_RULE_TYPE_KEY);

		   Map<String, Object> sourceResult = (Map<String, Object>) subMap.remove("1");
		   Map<String, Object> modifier = null;
		   String outerAlias = null;
		   String modifierKey = null;

		   for (int i = 2; subMap.containsKey(String.valueOf(i)); i++) {
			   Object entry = subMap.remove(String.valueOf(i));
			   if (entry instanceof Map<?, ?> entryMap) {
				   if (entryMap.containsKey(MUMBLE_ALIAS_KEY)) {
					   outerAlias = (String) entryMap.get(MUMBLE_ALIAS_KEY);
				   } else if (entryMap.containsKey(MUMBLE_UNPIVOT_KEY)) {
					   modifier = (Map<String, Object>) entryMap.get(MUMBLE_UNPIVOT_KEY);
					   modifierKey = MUMBLE_UNPIVOT_KEY;
				   } else if (entryMap.containsKey(MUMBLE_PIVOT_KEY)) {
					   modifier = (Map<String, Object>) entryMap.get(MUMBLE_PIVOT_KEY);
					   modifierKey = MUMBLE_PIVOT_KEY;
				   }
			   }
		   }

		   String relationAlias = null;
		   if (outerAlias != null && !outerAlias.isBlank()) {
			   relationAlias = outerAlias;
		   }

		   if (modifier != null && relationAlias != null) {
			   String sourceRef = resolveRelationalModifierSourceReference(sourceResult);
			   if (sourceRef != null && !sourceRef.isBlank()) {
				   symbolTreeHelper.upsertCurrentTableAliasMapping(relationAlias, sourceRef);
			   } else {
				   walker.collectTableAlias(relationAlias, modifierKey);
			   }
		   }

		   if (modifier != null && modifierKey != null) {
			   sourceResult.put(modifierKey, modifier);
			   if (relationAlias != null) {
				   sourceResult.put(MUMBLE_ALIAS_KEY, relationAlias);
			   }
			   resolveRelationalModifierScopeAtPrimaryExit(modifierKey, sourceResult, relationAlias);
		   }
		   if (outerAlias != null) {
			   Object tableEntry = sourceResult.get(MUMBLE_TABLE_KEY);
			   if (tableEntry instanceof Map<?, ?> tableMap) {
				   if (modifier == null) {
					   ((Map<String, Object>) tableMap).put(MUMBLE_ALIAS_KEY, outerAlias);
					   String tableRef = symbolTreeHelper.getQualifiedTableReference((Map<String, Object>) tableMap);
					   String cteScopeReference = symbolTreeHelper.resolveCteOrExistingQueryScopeInVisibleScopes(tableRef);
					   String aliasTarget = (cteScopeReference != null) ? cteScopeReference : tableRef;
					   symbolTreeHelper.upsertCurrentTableAliasMapping(outerAlias, aliasTarget);
				   }
			   } else {
				   sourceResult.put(MUMBLE_ALIAS_KEY, outerAlias);
			   }
		   }

		   walker.addToParent(parentRuleIndex, parentStackLevel, sourceResult);
	}

	@SuppressWarnings("unchecked")
	private void registerPivotValueInterfaceHint(
			Object aggregateObj,
			Object inListObj,
			Object nameColObj) {
		if (aggregateObj == null || inListObj == null) {
			return;
		}

		ArrayList<String> aggregateColumns = extractPivotAggregateColumnNames(aggregateObj);
		ArrayList<String> inListColumns = extractPivotDerivedNameComponents(inListObj);

		if (aggregateColumns.isEmpty() || inListColumns.isEmpty()) {
			return;
		}

		String forColumn = (nameColObj instanceof String) ? (String) nameColObj : null;
		ArrayList<String> sourceColumns = new ArrayList<String>();
		if (forColumn != null && !forColumn.isBlank()) {
			sourceColumns.add(forColumn);
		}

		HashMap<String, Object> hint = new HashMap<String, Object>();
		hint.put(SqlParseSymbolTreeHelper.RELATIONAL_MODIFIER_OPERATOR_KEY, MUMBLE_PIVOT_KEY);
		hint.put("pivot_aggregate_columns", aggregateColumns);
		hint.put("pivot_in_columns", inListColumns);
		HashMap<String, Object> aggregateDependencyColumns =
				extractPivotAggregateDependencyColumns(aggregateObj);
		if (!aggregateDependencyColumns.isEmpty()) {
			hint.put("pivot_aggregate_dependency_columns", aggregateDependencyColumns);
			for (Object dependencyListObj : aggregateDependencyColumns.values()) {
				if (!(dependencyListObj instanceof ArrayList<?> dependencyList)) {
					continue;
				}
				for (Object dependencyObj : dependencyList) {
					if (dependencyObj instanceof String dependency
							&& !dependency.isBlank()
							&& !containsStringIgnoreCase(sourceColumns, dependency)) {
						sourceColumns.add(dependency);
					}
				}
			}
		}
		if (forColumn != null && !forColumn.isBlank()) {
			hint.put(MUMBLE_FOR_KEY, forColumn);
		}
		if (!sourceColumns.isEmpty()) {
			hint.put(SqlParseSymbolTreeHelper.RELATIONAL_MODIFIER_SOURCE_COLUMNS_KEY, sourceColumns);
		}
		hint.put(
				SqlParseSymbolTreeHelper.RELATIONAL_MODIFIER_DERIVED_COLUMNS_KEY,
				buildPivotDerivedColumnNames(aggregateColumns, inListColumns));

		addRelationalModifierHint(hint);
	}

	private boolean containsStringIgnoreCase(ArrayList<String> values, String candidate) {
		if (values == null || candidate == null) {
			return false;
		}
		for (String value : values) {
			if (value != null && value.equalsIgnoreCase(candidate)) {
				return true;
			}
		}
		return false;
	}

	private ArrayList<String> buildPivotDerivedColumnNames(ArrayList<String> aggregateColumns, ArrayList<String> inListColumns) {
		ArrayList<String> derivedColumnNames = new ArrayList<String>();
		if (aggregateColumns == null || aggregateColumns.isEmpty()
				|| inListColumns == null || inListColumns.isEmpty()) {
			return derivedColumnNames;
		}

		for (String inValue : inListColumns) {
			if (inValue == null || inValue.isBlank()) {
				continue;
			}
			for (String aggregate : aggregateColumns) {
				if (aggregate == null || aggregate.isBlank()) {
					continue;
				}
				String derivedColumnName = inValue + "_" + aggregate;
				if (!containsStringIgnoreCase(derivedColumnNames, derivedColumnName)) {
					derivedColumnNames.add(derivedColumnName);
				}
			}
		}
		return derivedColumnNames;
	}

	@SuppressWarnings("unchecked")
	private void addRelationalModifierHint(HashMap<String, Object> hint) {
		if (hint == null || hint.isEmpty()) {
			return;
		}

		Object hintsObj = walker.symbolTable.get(SqlParseSymbolTreeHelper.DERIVED_COLUMNS_HINTS_KEY);
		ArrayList<Object> hints;
		if (hintsObj instanceof ArrayList<?>) {
			hints = (ArrayList<Object>) hintsObj;
		} else {
			hints = new ArrayList<Object>();
			walker.symbolTable.put(SqlParseSymbolTreeHelper.DERIVED_COLUMNS_HINTS_KEY, hints);
		}
		hints.add(hint);
	}

	@SuppressWarnings("unchecked")
	private ArrayList<String> extractPivotAggregateColumnNames(Object aggregateObj) {
		ArrayList<String> columnNames = new ArrayList<String>();
		if (!(aggregateObj instanceof Map<?, ?> aggregateMapObj)) {
			return columnNames;
		}

		Map<String, Object> aggregateMap = (Map<String, Object>) aggregateMapObj;
		if (aggregateMap.containsKey(MUMBLE_FUNCTION_KEY)) {
			String aggregateName = extractPivotAggregateOutputName(aggregateMap);
			if (aggregateName != null && !aggregateName.isBlank()) {
				columnNames.add(aggregateName);
			}
			return columnNames;
		}

		for (int index = 1; aggregateMap.containsKey(String.valueOf(index)); index++) {
			Object aggItemObj = aggregateMap.get(String.valueOf(index));
			if (!(aggItemObj instanceof Map<?, ?> aggItemMapObj)) {
				continue;
			}

			Map<String, Object> aggItemMap = (Map<String, Object>) aggItemMapObj;
			Object aliasObj = aggItemMap.get(MUMBLE_ALIAS_KEY);
			if (aliasObj instanceof String alias && !alias.isBlank()) {
				columnNames.add(alias);
			} else {
				Object functionObj = aggItemMap.get(MUMBLE_FUNCTION_KEY);
				if (functionObj instanceof Map<?, ?> functionMapObj) {
					Object functionNameObj = ((Map<String, Object>) functionMapObj).get(MUMBLE_FUNCTION_NAME_KEY);
					if (functionNameObj instanceof String functionName && !functionName.isBlank()) {
						columnNames.add(functionName);
					}
				}
			}
		}

		return columnNames;
	}

	@SuppressWarnings("unchecked")
	private HashMap<String, Object> extractPivotAggregateDependencyColumns(Object aggregateObj) {
		HashMap<String, Object> dependenciesByAggregate = new HashMap<String, Object>();
		if (!(aggregateObj instanceof Map<?, ?> aggregateMapObj)) {
			return dependenciesByAggregate;
		}

		Map<String, Object> aggregateMap = (Map<String, Object>) aggregateMapObj;
		if (aggregateMap.containsKey(MUMBLE_FUNCTION_KEY)) {
			String aggregateName = extractPivotAggregateOutputName(aggregateMap);
			if (aggregateName == null || aggregateName.isBlank()) {
				return dependenciesByAggregate;
			}
			Object functionObj = aggregateMap.get(MUMBLE_FUNCTION_KEY);
			if (!(functionObj instanceof Map<?, ?> functionMapObj)) {
				return dependenciesByAggregate;
			}
			Object parametersObj = ((Map<String, Object>) functionMapObj).get(MUMBLE_PARAMETERS_KEY);
			String dependencyName = extractPivotAggregateDependencyName(parametersObj);
			if (dependencyName != null && !dependencyName.isBlank()) {
				ArrayList<String> dependencyNames = new ArrayList<String>();
				dependencyNames.add(dependencyName);
				dependenciesByAggregate.put(aggregateName, dependencyNames);
			}
			return dependenciesByAggregate;
		}

		for (int index = 1; aggregateMap.containsKey(String.valueOf(index)); index++) {
			Object aggItemObj = aggregateMap.get(String.valueOf(index));
			if (!(aggItemObj instanceof Map<?, ?> aggItemMapObj)) {
				continue;
			}

			Map<String, Object> aggItemMap = (Map<String, Object>) aggItemMapObj;
			String aggregateName = extractPivotAggregateOutputName(aggItemMap);
			if (aggregateName == null || aggregateName.isBlank()) {
				continue;
			}

			Object functionObj = aggItemMap.get(MUMBLE_FUNCTION_KEY);
			if (!(functionObj instanceof Map<?, ?> functionMapObj)) {
				continue;
			}

			Object parametersObj = ((Map<String, Object>) functionMapObj).get(MUMBLE_PARAMETERS_KEY);
			String dependencyName = extractPivotAggregateDependencyName(parametersObj);
			if (dependencyName != null && !dependencyName.isBlank()) {
				ArrayList<String> dependencyNames = new ArrayList<String>();
				dependencyNames.add(dependencyName);
				dependenciesByAggregate.put(aggregateName, dependencyNames);
			}
		}

		return dependenciesByAggregate;
	}

	@SuppressWarnings("unchecked")
	private String extractPivotAggregateDependencyName(Object parametersObj) {
		if (!(parametersObj instanceof Map<?, ?> parameterMapObj)) {
			return null;
		}

		ArrayList<Object> refs = new ArrayList<Object>();
		symbolTreeHelper.flattenSubTreeForDependencyColumns(
				new HashMap<String, Object>((Map<String, Object>) parameterMapObj),
				refs);

		for (Object refObj : refs) {
			String refName = walker.extractReferenceNameFromInterfaceEntry(refObj);
			if (refName == null || refName.isBlank() || "*".equals(refName)) {
				continue;
			}
			return refName;
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	private String extractPivotAggregateOutputName(Map<String, Object> aggItemMap) {
		if (aggItemMap == null) {
			return null;
		}

		Object aliasObj = aggItemMap.get(MUMBLE_ALIAS_KEY);
		if (aliasObj instanceof String alias && !alias.isBlank()) {
			return alias;
		}

		Object functionObj = aggItemMap.get(MUMBLE_FUNCTION_KEY);
		if (!(functionObj instanceof Map<?, ?> functionMapObj)) {
			return null;
		}

		Object functionNameObj = ((Map<String, Object>) functionMapObj).get(MUMBLE_FUNCTION_NAME_KEY);
		if (functionNameObj instanceof String functionName && !functionName.isBlank()) {
			return functionName;
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	private ArrayList<String> extractPivotDerivedNameComponents(Object inListObj) {
		ArrayList<String> nameComponents = new ArrayList<String>();
		if (!(inListObj instanceof Map<?, ?> inListMapObj)) {
			return nameComponents;
		}

		Map<String, Object> inListMap = (Map<String, Object>) inListMapObj;
		for (int index = 1; inListMap.containsKey(String.valueOf(index)); index++) {
			Object inItemObj = inListMap.get(String.valueOf(index));
			if (!(inItemObj instanceof Map<?, ?> inItemMapObj)) {
				continue;
			}

			Map<String, Object> inItemMap = (Map<String, Object>) inItemMapObj;
			String component = null;
			Object prefixObj = inItemMap.get(MUMBLE_PIVOT_PREFIX_KEY);
			if (prefixObj instanceof String prefix && !prefix.isBlank()) {
				component = prefix;
			} else {
				Object literalObj = inItemMap.get(MUMBLE_PIVOT_LITERAL_KEY);
				if (literalObj instanceof String literal && !literal.isBlank()) {
					component = stripSingleQuotedPivotComponent(literal);
				}
			}

			if (component != null && !component.isBlank() && !nameComponents.contains(component)) {
				nameComponents.add(component);
			}
		}

		return nameComponents;
	}

	private String stripSingleQuotedPivotComponent(String component) {
		if (component == null) {
			return null;
		}
		String trimmed = component.trim();
		if (trimmed.length() >= 2 && trimmed.startsWith("'") && trimmed.endsWith("'")) {
			return trimmed.substring(1, trimmed.length() - 1);
		}
		return component;
	}

	private void registerUnpivotValueInterfaceHint(Map<String, Object> unpivotMap) {
		if (unpivotMap == null || unpivotMap.isEmpty()) {
			return;
		}

		Object valueObj = unpivotMap.get(MUMBLE_VALUE_KEY);
		if (!(valueObj instanceof String valueColumn) || valueColumn.isBlank()) {
			return;
		}

		ArrayList<String> inColumns = symbolTreeHelper.extractRelationalModifierInListColumnNames(
				unpivotMap.get(MUMBLE_IN_KEY));
		if (inColumns.isEmpty()) {
			return;
		}

		HashMap<String, Object> hint = new HashMap<String, Object>();
		hint.put(SqlParseSymbolTreeHelper.RELATIONAL_MODIFIER_OPERATOR_KEY, MUMBLE_UNPIVOT_KEY);
		hint.put(MUMBLE_VALUE_KEY, valueColumn);
		hint.put(MUMBLE_IN_KEY, inColumns);
		hint.put(SqlParseSymbolTreeHelper.RELATIONAL_MODIFIER_SOURCE_COLUMNS_KEY, inColumns);

		// Also store the FOR column so the unresolved-map cleanup can mark it as resolved
		ArrayList<String> derivedColumns = new ArrayList<String>();
		derivedColumns.add(valueColumn);
		Object forObj = unpivotMap.get(MUMBLE_FOR_KEY);
		if (forObj instanceof String forColumn && !forColumn.isBlank()) {
			hint.put(MUMBLE_FOR_KEY, forColumn);
			if (!containsStringIgnoreCase(derivedColumns, forColumn)) {
				derivedColumns.add(forColumn);
			}
		}
		hint.put(SqlParseSymbolTreeHelper.RELATIONAL_MODIFIER_DERIVED_COLUMNS_KEY, derivedColumns);

		addRelationalModifierHint(hint);
	}

	private String resolveRelationalModifierSourceReference(Map<String, Object> sourceResult) {
		Object tableObj = sourceResult.get(MUMBLE_TABLE_KEY);
		if (!(tableObj instanceof Map<?, ?> tableMapObj)) {
			// Source is a subquery or set operation — find the most recently added query scope key
			return resolveLatestQueryScopeKeyFromSymbolTable();
		}

		Map<String, Object> tableMap = (Map<String, Object>) tableMapObj;
		Object aliasObj = tableMap.get(MUMBLE_ALIAS_KEY);
		if (aliasObj instanceof String alias && !alias.isBlank()) {
			return alias;
		}

		String tableRef = symbolTreeHelper.getQualifiedTableReference(tableMap);
		if (tableRef == null || tableRef.isBlank()) {
			return null;
		}

		return tableRef;
	}

	/**
	 * Scans the current symbol table scope for the highest-indexed query scope key
	 * (query0, query1, union0, intersect0, values0, etc.) excluding definition entries
	 * (def_*) and temporary entries (_tmp_*). Used to resolve the source reference
	 * when an UNPIVOT/PIVOT operator is applied to an un-aliased subquery.
	 */
	private String resolveLatestQueryScopeKeyFromSymbolTable() {
		return resolveLatestScopeKeyFromSymbolTable(
				MUMBLE_QUERY_KEY,
				MUMBLE_UNION_KEY,
				MUMBLE_INTERSECT_KEY,
				MUMBLE_VALUES_KEY);
	}

	private String resolveLatestDefinitionQueryScopeKeyFromSymbolTable() {
		String latestKey = null;
		int latestIndex = -1;
		for (String key : walker.symbolTable.keySet()) {
			if (key == null || key.startsWith("_tmp_")) {
				continue;
			}

			String suffix = null;
			if (key.startsWith("def_" + MUMBLE_QUERY_KEY)) {
				suffix = key.substring(("def_" + MUMBLE_QUERY_KEY).length());
			} else if (key.startsWith("def_" + MUMBLE_UNION_KEY)) {
				suffix = key.substring(("def_" + MUMBLE_UNION_KEY).length());
			} else if (key.startsWith("def_" + MUMBLE_INTERSECT_KEY)) {
				suffix = key.substring(("def_" + MUMBLE_INTERSECT_KEY).length());
			} else if (key.startsWith("def_" + MUMBLE_VALUES_KEY)) {
				suffix = key.substring(("def_" + MUMBLE_VALUES_KEY).length());
			}

			if (suffix == null || suffix.isBlank()) {
				continue;
			}

			try {
				int idx = Integer.parseInt(suffix);
				if (idx > latestIndex) {
					latestIndex = idx;
					latestKey = key;
				}
			} catch (NumberFormatException e) {
				// ignore
			}
		}
		return latestKey;
	}

	private String resolveLatestScopeKeyFromSymbolTable(String... prefixes) {
		if (prefixes == null || prefixes.length == 0) {
			return null;
		}

		String latestKey = null;
		int latestIndex = -1;
		for (String key : walker.symbolTable.keySet()) {
			if (key == null || key.startsWith("def_") || key.startsWith("_tmp_")) {
				continue;
			}

			String numericSuffix = null;
			for (String prefix : prefixes) {
				if (prefix != null && key.startsWith(prefix)) {
					numericSuffix = key.substring(prefix.length());
					break;
				}
			}
			if (numericSuffix == null || numericSuffix.isBlank()) {
				continue;
			}
			try {
				int idx = Integer.parseInt(numericSuffix);
				if (idx > latestIndex) {
					latestIndex = idx;
					latestKey = key;
				}
			} catch (NumberFormatException e) {
				// ignore non-numeric suffixes
			}
		}
		return latestKey;
	}

	@SuppressWarnings("unchecked")
	private void resolvePivotScopeAtPrimaryExit(Map<String, Object> sourceResult, String relationAlias) {
		String sourceRef = (relationAlias != null && !relationAlias.isBlank())
				? relationAlias
				: resolveRelationalModifierSourceReference(sourceResult);
		addRelationalModifierSourceReferenceToHints(sourceRef);
		Map<String, Object> sourceInterfaceMap = resolvePrimarySourceInterface(sourceResult);
		enrichPivotAggregateHintReferencesForQuerySource(walker.symbolTable, sourceRef, sourceInterfaceMap);

		Object identifiersObj = walker.symbolTable.get(PIVOT_IN_IDENTIFIER_REFERENCES_KEY);
		HashMap<String, Object> pivotIdentifierMap = (identifiersObj instanceof HashMap<?, ?>)
				? (HashMap<String, Object>) identifiersObj
				: new HashMap<String, Object>();

		if (isDirectTableSource(sourceResult)) {
			emitPivotIdentifierUnresolvedFatals(pivotIdentifierMap);
			pivotIdentifierMap.clear();
		} else {
			Map<String, Object> interfaceMap = resolvePrimarySourceInterface(sourceResult);
			boolean wildcardInterface = interfaceMap.containsKey("*");

			for (String identifier : new ArrayList<String>(pivotIdentifierMap.keySet())) {
				boolean resolvesAgainstSource = wildcardInterface || containsMapKeyIgnoreCase(interfaceMap, identifier);
				HashMap<String, Object> singleIdentifierMap = new HashMap<String, Object>();
				singleIdentifierMap.put(identifier, pivotIdentifierMap.get(identifier));
				if (resolvesAgainstSource) {
					emitPivotIdentifierSevereWarnings(singleIdentifierMap);
				} else {
					emitPivotIdentifierUnresolvedFatals(singleIdentifierMap);
				}
			}
			pivotIdentifierMap.clear();
		}

		if (pivotIdentifierMap.isEmpty()) {
			walker.symbolTable.remove(PIVOT_IN_IDENTIFIER_REFERENCES_KEY);
		} else {
			walker.symbolTable.put(PIVOT_IN_IDENTIFIER_REFERENCES_KEY, pivotIdentifierMap);
		}
	}

	@SuppressWarnings("unchecked")
	private void resolveUnpivotScopeAtPrimaryExit(Map<String, Object> sourceResult, String relationAlias) {
		Object hintsObj = walker.symbolTable.get(SqlParseSymbolTreeHelper.DERIVED_COLUMNS_HINTS_KEY);
		if (!(hintsObj instanceof ArrayList<?> hintListObj) || hintListObj.isEmpty()) {
			return;
		}

		String interfaceSourceRef = (relationAlias != null && !relationAlias.isBlank())
				? relationAlias
				: resolveRelationalModifierSourceReference(sourceResult);
		if (interfaceSourceRef == null || interfaceSourceRef.isBlank()) {
			return;
		}

		String dictionarySourceRef = resolveRelationalModifierSourceReference(sourceResult);
		if (dictionarySourceRef == null || dictionarySourceRef.isBlank()) {
			dictionarySourceRef = interfaceSourceRef;
		}

		for (Object hintObj : (ArrayList<Object>) hintListObj) {
			if (!(hintObj instanceof Map<?, ?> hintMapObj)) {
				continue;
			}
			Map<String, Object> hintMap = (Map<String, Object>) hintMapObj;
			hintMap.put(MUMBLE_TABLE_REF_KEY, interfaceSourceRef);
			hintMap.put(SqlParseSymbolTreeHelper.RELATIONAL_MODIFIER_SOURCE_REF_KEY, dictionarySourceRef);
		}

		Object unresolvedObj = walker.symbolTable.get(MUMBLE_UNRESOLVED_COLUMN_KEY);
		if (unresolvedObj instanceof HashMap<?, ?> unresolvedMapObj) {
			HashMap<String, Object> unresolvedMap = (HashMap<String, Object>) unresolvedMapObj;
			symbolTreeHelper.resolveUnpivotGeneratedColumnsFromUnresolvedMap(
					(ArrayList<Object>) hintListObj,
					unresolvedMap);
			if (unresolvedMap.isEmpty()) {
				walker.symbolTable.remove(MUMBLE_UNRESOLVED_COLUMN_KEY);
			} else {
				walker.symbolTable.put(MUMBLE_UNRESOLVED_COLUMN_KEY, unresolvedMap);
			}
		}
	}

	private void resolveRelationalModifierScopeAtPrimaryExit(
			String modifierKey,
			Map<String, Object> sourceResult,
			String relationAlias) {
		if (MUMBLE_PIVOT_KEY.equals(modifierKey)) {
			resolvePivotScopeAtPrimaryExit(sourceResult, relationAlias);
		} else if (MUMBLE_UNPIVOT_KEY.equals(modifierKey)) {
			resolveUnpivotScopeAtPrimaryExit(sourceResult, relationAlias);
		}
	}

	@SuppressWarnings("unchecked")
	private void addRelationalModifierSourceReferenceToHints(String sourceRef) {
		if (sourceRef == null || sourceRef.isBlank()) {
			return;
		}
		Object hintsObj = walker.symbolTable.get(SqlParseSymbolTreeHelper.DERIVED_COLUMNS_HINTS_KEY);
		if (!(hintsObj instanceof ArrayList<?> hintListObj)) {
			return;
		}
		for (Object hintObj : (ArrayList<Object>) hintListObj) {
			if (!(hintObj instanceof Map<?, ?> hintMapObj)) {
				continue;
			}
			Map<String, Object> hintMap = (Map<String, Object>) hintMapObj;
			hintMap.put(SqlParseSymbolTreeHelper.RELATIONAL_MODIFIER_SOURCE_REF_KEY, sourceRef);
			hintMap.put(MUMBLE_TABLE_REF_KEY, sourceRef);
		}
	}

	@SuppressWarnings("unchecked")
	private void enrichPivotAggregateHintReferencesForQuerySource(
			Map<String, Object> pivotScope,
			String sourceRef,
			Map<String, Object> sourceInterfaceMap) {
		if (pivotScope == null || sourceRef == null || sourceRef.isBlank() || sourceInterfaceMap == null) {
			return;
		}

		Object hintsObj = walker.symbolTable.get(SqlParseSymbolTreeHelper.DERIVED_COLUMNS_HINTS_KEY);
		if (!(hintsObj instanceof ArrayList<?>)) {
			hintsObj = pivotScope.get(SqlParseSymbolTreeHelper.DERIVED_COLUMNS_HINTS_KEY);
		}
		if (!(hintsObj instanceof ArrayList<?> hintListObj)) {
			return;
		}

		for (Object hintObj : (ArrayList<Object>) hintListObj) {
			if (!(hintObj instanceof Map<?, ?> hintMapObj)) {
				continue;
			}

			Map<String, Object> hintMap = (Map<String, Object>) hintMapObj;
			Object dependencyObj = hintMap.get("pivot_aggregate_dependency_columns");
			if (!(dependencyObj instanceof Map<?, ?> dependencyMapObj)) {
				continue;
			}

			Map<String, Object> dependenciesByAggregate = (Map<String, Object>) dependencyMapObj;
			HashMap<String, Object> resolvedRefsByAggregate = new HashMap<String, Object>();

			for (Map.Entry<String, Object> dependencyEntry : dependenciesByAggregate.entrySet()) {
				String aggregateName = dependencyEntry.getKey();
				Object dependencyListObj = dependencyEntry.getValue();
				if (aggregateName == null || aggregateName.isBlank()
						|| !(dependencyListObj instanceof ArrayList<?> dependencyList)) {
					continue;
				}

				ArrayList<Object> resolvedRefs = new ArrayList<Object>();
				for (Object dependencyNameObj : dependencyList) {
					if (!(dependencyNameObj instanceof String dependencyName) || dependencyName.isBlank()) {
						continue;
					}
					if (!resolvesPivotAggregateDependencyInSource(sourceRef, sourceInterfaceMap, dependencyName)) {
						continue;
					}

					HashMap<String, Object> ref = new HashMap<String, Object>();
					ref.put(MUMBLE_NAME_KEY, dependencyName);
					ref.put(MUMBLE_TABLE_REF_KEY, sourceRef);
					symbolTreeHelper.appendInterfaceReferenceIfMissing(resolvedRefs, ref);
				}

				if (!resolvedRefs.isEmpty()) {
					resolvedRefsByAggregate.put(aggregateName, resolvedRefs);
				}
			}

			if (!resolvedRefsByAggregate.isEmpty()) {
				hintMap.put("pivot_aggregate_resolved_refs", resolvedRefsByAggregate);
			}
		}
	}

	@SuppressWarnings("unchecked")
	private boolean resolvesPivotAggregateDependencyInSource(
			String sourceRef,
			Map<String, Object> sourceInterfaceMap,
			String dependencyName) {
		if (dependencyName == null || dependencyName.isBlank()) {
			return false;
		}

		if (containsMapKeyIgnoreCase(sourceInterfaceMap, dependencyName)) {
			return true;
		}

		HashMap<String, Object> currentTableDictionary = walker.getCurrentTableDictionary();
		if (currentTableDictionary == null || currentTableDictionary.isEmpty()) {
			return false;
		}

		String tableRefCandidate = sourceRef;
		Object aliasMapObj = walker.symbolTable.get(MUMBLE_TABLE_ALIAS_KEY);
		if (aliasMapObj instanceof Map<?, ?> aliasMapRawObj) {
			HashMap<String, Object> aliasMap = new HashMap<String, Object>((Map<String, Object>) aliasMapRawObj);
			String resolvedTableRef = walker.resolveAliasToTableName(sourceRef, aliasMap);
			if (resolvedTableRef != null && !resolvedTableRef.isBlank()) {
				tableRefCandidate = resolvedTableRef;
			}
		}

		HashMap<String, Object> sourceTableDictionary =
				walker.getTableDictionaryForReference(tableRefCandidate, currentTableDictionary);
		if (sourceTableDictionary == null || sourceTableDictionary.isEmpty()) {
			return false;
		}

		return containsMapKeyIgnoreCase(sourceTableDictionary, dependencyName);
	}

	private boolean containsMapKeyIgnoreCase(Map<String, Object> map, String key) {
		if (map == null || key == null) {
			return false;
		}
		for (String existingKey : map.keySet()) {
			if (existingKey != null && existingKey.equalsIgnoreCase(key)) {
				return true;
			}
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	private Map<String, Object> resolvePrimarySourceInterface(Map<String, Object> sourceResult) {
		String sourceRef = resolveRelationalModifierSourceReference(sourceResult);
		if (sourceRef == null || sourceRef.isBlank()) {
			return new HashMap<String, Object>();
		}

		Object sourceScopeObj = walker.symbolTable.get(sourceRef);
		if (!(sourceScopeObj instanceof Map<?, ?>)) {
			Object aliasMapObj = walker.symbolTable.get(MUMBLE_TABLE_ALIAS_KEY);
			if (aliasMapObj instanceof Map<?, ?> aliasMapObjRaw) {
				Object aliasTargetObj = ((Map<String, Object>) aliasMapObjRaw).get(sourceRef);
				if (aliasTargetObj instanceof String aliasTarget && !aliasTarget.isBlank()) {
					sourceScopeObj = walker.symbolTable.get(aliasTarget);
				}
			}
		}
		if (!(sourceScopeObj instanceof Map<?, ?> sourceScopeMapObj)) {
			String latestQueryKey = resolveLatestQueryScopeKeyFromSymbolTable();
			if (latestQueryKey != null && !latestQueryKey.isBlank()) {
				sourceScopeObj = walker.symbolTable.get(latestQueryKey);
				if (!(sourceScopeObj instanceof Map<?, ?>)) {
					sourceScopeObj = walker.symbolTable.get("def_" + latestQueryKey);
				}
			}
		}

		if (!(sourceScopeObj instanceof Map<?, ?> sourceScopeMapObj)) {
			String latestDefinitionQueryKey = resolveLatestDefinitionQueryScopeKeyFromSymbolTable();
			if (latestDefinitionQueryKey != null && !latestDefinitionQueryKey.isBlank()) {
				sourceScopeObj = walker.symbolTable.get(latestDefinitionQueryKey);
			}
		}

		if (!(sourceScopeObj instanceof Map<?, ?> sourceScopeMapObj)) {
			return new HashMap<String, Object>();
		}

		Map<String, Object> sourceScope = (Map<String, Object>) sourceScopeMapObj;
		Object interfaceObj = sourceScope.get(MUMBLE_INTERFACE_KEY);
		if (interfaceObj instanceof Map<?, ?> interfaceMapObj) {
			return (Map<String, Object>) interfaceMapObj;
		}

		return new HashMap<String, Object>();
	}

	@SuppressWarnings("unchecked")
	private boolean isDirectTableSource(Map<String, Object> sourceResult) {
		Object tableObj = sourceResult == null ? null : sourceResult.get(MUMBLE_TABLE_KEY);
		if (!(tableObj instanceof Map<?, ?> tableMapObj)) {
			return false;
		}

		Object tableNameObj = ((Map<String, Object>) tableMapObj).get(MUMBLE_TABLE_KEY);
		return tableNameObj instanceof String tableName && !tableName.isBlank();
	}

	@SuppressWarnings("unchecked")
	private void emitPivotIdentifierSevereWarnings(Map<String, Object> pivotIdentifierMap) {
		if (pivotIdentifierMap == null || pivotIdentifierMap.isEmpty()) {
			return;
		}

		String diagCode = walker.getDiagnosticCode(SqlASTWalkerHelper.DIAG_SQL_PIVOT_IN_IDENTIFIER_REFERENCE);
		String diagTemplate = walker.getDiagnosticMessage(SqlASTWalkerHelper.DIAG_SQL_PIVOT_IN_IDENTIFIER_REFERENCE);

		for (Map.Entry<String, Object> entry : pivotIdentifierMap.entrySet()) {
			String identifier = entry.getKey();
			Object tokenListObj = entry.getValue();
			if (!(tokenListObj instanceof List<?> tokenList) || tokenList.isEmpty()) {
				continue;
			}

			for (Object tokenObj : tokenList) {
				String tokenString = tokenObj == null ? null : tokenObj.toString();
				Integer[] lineAndChar = extractLineAndCharFromTokenString(tokenString);
				Integer line = lineAndChar[0];
				Integer charPos = lineAndChar[1];

				String diagMessage = (diagTemplate == null)
						? String.format("PIVOT IN identifier \"%s\" at (l:%s c:%s) is interpreted as a column reference.",
								identifier,
								String.valueOf(line),
								String.valueOf(charPos))
						: String.format(diagTemplate,
								identifier,
								String.valueOf(line),
								String.valueOf(charPos));

				walker.addWalkerDiagnostic(
						ParseDiagnostic.Severity.SEVERE_WARNING,
						diagCode,
						diagMessage,
						line,
						charPos,
						walker.getClass().getSimpleName(),
						null,
						identifier,
						true,
						"ast-walk",
						null,
						null);
			}
		}
	}

	private void emitPivotIdentifierUnresolvedFatals(Map<String, Object> pivotIdentifierMap) {
		if (pivotIdentifierMap == null || pivotIdentifierMap.isEmpty()) {
			return;
		}

		String diagCode = walker.getDiagnosticCode(SqlASTWalkerHelper.DIAG_SQL_PIVOT_IN_IDENTIFIER_UNRESOLVED);
		String diagTemplate = walker.getDiagnosticMessage(SqlASTWalkerHelper.DIAG_SQL_PIVOT_IN_IDENTIFIER_UNRESOLVED);

		for (Map.Entry<String, Object> entry : pivotIdentifierMap.entrySet()) {
			String identifier = entry.getKey();
			Object tokenListObj = entry.getValue();
			if (!(tokenListObj instanceof List<?> tokenList) || tokenList.isEmpty()) {
				continue;
			}

			for (Object tokenObj : tokenList) {
				String tokenString = tokenObj == null ? null : tokenObj.toString();
				Integer[] lineAndChar = extractLineAndCharFromTokenString(tokenString);
				Integer line = lineAndChar[0];
				Integer charPos = lineAndChar[1];
				String diagMessage = (diagTemplate == null)
						? String.format(
								"PIVOT IN identifier \"%s\" at (l:%s c:%s) cannot be resolved against the PIVOT source.",
								identifier,
								String.valueOf(line),
								String.valueOf(charPos))
						: String.format(
								diagTemplate,
								identifier,
								String.valueOf(line),
								String.valueOf(charPos));
				walker.addWalkerFatal(diagCode, diagMessage, line, charPos, identifier);
			}
		}
	}

	private Integer[] extractLineAndCharFromTokenString(String tokenString) {
		Integer[] result = new Integer[] { null, null };
		if (tokenString == null || tokenString.isBlank()) {
			return result;
		}

		int lastComma = tokenString.lastIndexOf(',');
		if (lastComma < 0 || lastComma + 1 >= tokenString.length()) {
			return result;
		}

		String tail = tokenString.substring(lastComma + 1).replace("]", "").trim();
		int colon = tail.indexOf(':');
		if (colon < 0) {
			return result;
		}

		String lineStr = tail.substring(0, colon).trim();
		String charStr = tail.substring(colon + 1).trim();
		try {
			result[0] = Integer.parseInt(lineStr);
			result[1] = Integer.parseInt(charStr);
		} catch (NumberFormatException e) {
			// leave as nulls
		}
		return result;
	}



	
	@Override
	public void enterInsert_source_primary( SQLSelectParserParser.Insert_source_primaryContext ctx) {
		walker.pushSymbolTable();
	}

	@Override
	public void exitInsert_source_primary( SQLSelectParserParser.Insert_source_primaryContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		int parentRuleIndex = ctx.getParent().getRuleIndex();

		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Integer parentStackLevel = walker.currentStackLevel(parentRuleIndex);

		Map<String, Object> subMap = walker.removeNodeMap(ruleIndex, stackLevel);
		subMap.remove(ASTWALKER_RULE_TYPE_KEY);
		Map<String, Object> reference = walker.checkForSubstitutionVariable((Map<String, Object>) subMap.remove("1"),
					MUMBLE_TUPLE_KEY);

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
		symbolTreeHelper.finalizeInsertSourceAtPrimaryExit(symbols);
		symbolTreeHelper.popFrameAndMergeIntoParent(symbols);

		walker.addToParent(parentRuleIndex, parentStackLevel, subMap);
	}

	
	@Override
	public void exitTuple_source_primary( SQLSelectParserParser.Tuple_source_primaryContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		int parentRuleIndex = ctx.getParent().getRuleIndex();

		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Integer parentStackLevel = walker.currentStackLevel(parentRuleIndex);

		Map<String, Object> subMap = walker.removeNodeMap(ruleIndex, stackLevel);
		Object type = subMap.remove(ASTWALKER_RULE_TYPE_KEY);
		// Get the referenced AST segment from the stack and collect any Substitution Variables in the process
		Map<String, Object> item = walker.checkForSubstitutionVariable((Map<String, Object>) subMap.remove("1"),
					MUMBLE_TUPLE_KEY);
		
		// allocate work variables if needed later

		// Figure out what kind of Tuple entry you've got, and construct the next level's subtree
		if (item.containsKey(MUMBLE_TABLE_KEY)) {
			// Table Reference
			String tableRef = symbolTreeHelper.getQualifiedTableReference(item);
			String cteScopeReference = symbolTreeHelper.resolveCteOrExistingQueryScopeInVisibleScopes(tableRef);
			Object aliasObj = item.get(MUMBLE_ALIAS_KEY);
			if (aliasObj instanceof String alias && !alias.isBlank()) {
				String aliasTarget = (cteScopeReference != null) ? cteScopeReference : tableRef;
				symbolTreeHelper.upsertCurrentTableAliasMapping(alias, aliasTarget);
			} else if (cteScopeReference != null && tableRef != null && !tableRef.isBlank()) {
				symbolTreeHelper.upsertCurrentTableAliasMapping(tableRef, cteScopeReference);
			}
			if (cteScopeReference == null) {
				walker.ensureTableDictionaryEntry(tableRef);
			}
			// Table reference needs an AST Key added to it
			subMap.put(MUMBLE_TABLE_KEY, item);

		} else if (item.containsKey(MUMBLE_SUBSTITUTION_KEY)) {
			// Substitution Variable
			Map<String, Object> substitution = (Map<String, Object>) item.get(MUMBLE_SUBSTITUTION_KEY);
			// Collect Symbol Table Reference
			String name = resolveSubstitutionTableReference(substitution);
			// Only register as table source if it's a TUPLE substitution, not COLUMN or PREDICAND
			Object typeObj = substitution.get(MUMBLE_TYPE_KEY);
			String substitutionType = typeObj == null ? null : typeObj.toString();
			if (substitutionType == null || (!MUMBLE_COLUMN_KEY.equals(substitutionType) && !MUMBLE_PREDICAND_KEY.equals(substitutionType))) {
				walker.ensureTableDictionaryEntry(name);
			}
			// Substitution Variable is ready for use
			subMap.putAll(item);

		} else if (item.containsKey(MUMBLE_VALUES_KEY)) {
			//	Values Statement is simply ready for use
			subMap.putAll(item);

		} else if (item.containsKey(MUMBLE_TABLE_FUNCTION_KEY)) {
			// Table functions are tuple-valid sources and should not be treated as query symbols.
			subMap.putAll(item);
			
		} else { 
			// Only other option is a QUERY Object of some kind
			// Add the query to the symbol table tree and collect any interface elements
			String alias = null;
		
			Boolean done = symbolTreeHelper.collectQuerySymbolTable(MUMBLE_QUERY_KEY, alias);
			if (!done)
					done = symbolTreeHelper.collectQuerySymbolTable(MUMBLE_INSERT_KEY, alias);
			if (!done)
					done = symbolTreeHelper.collectQuerySymbolTable(MUMBLE_UPDATE_KEY, alias);
			if (!done)
					done = symbolTreeHelper.collectQuerySymbolTable(MUMBLE_DELETE_KEY, alias);
			if (!done)
					done = symbolTreeHelper.collectQuerySymbolTable(MUMBLE_UNION_KEY, alias);
			if (!done)
					done = symbolTreeHelper.collectQuerySymbolTable(MUMBLE_INTERSECT_KEY, alias);
			// Add the query AST to the tree, it is ready for use As Is
			subMap.putAll(item);
		}

		// Put nearly-completed AST back into parent rule and stack level
		walker.addToParent(parentRuleIndex, parentStackLevel, subMap);
	}

	// exitTuple_primary: composes tuple_source_primary + optional table_relational_modifier.
	// Slot "1" is always the processed tuple source result from exitTuple_source_primary.
	// Optional slot "2" is the relational modifier (UNPIVOT or PIVOT clause map).
	
	@Override
	public void exitTuple_primary( SQLSelectParserParser.Tuple_primaryContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		int parentRuleIndex = ctx.getParent().getRuleIndex();

		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Integer parentStackLevel = walker.currentStackLevel(parentRuleIndex);

		Map<String, Object> subMap = walker.removeNodeMap(ruleIndex, stackLevel);
		subMap.remove(ASTWALKER_RULE_TYPE_KEY);

		Map<String, Object> sourceResult = (Map<String, Object>) subMap.remove("1");
		Map<String, Object> modifier = null;

		for (int i = 2; subMap.containsKey(String.valueOf(i)); i++) {
			Object entry = subMap.remove(String.valueOf(i));
			if (entry instanceof Map<?, ?> entryMap) {
				if (entryMap.containsKey(MUMBLE_UNPIVOT_KEY)
						|| entryMap.containsKey(MUMBLE_PIVOT_KEY)) {
					modifier = (Map<String, Object>) entry;
				}
			}
		}

		if (modifier != null) {
			Map<String, Object> unpivotMap = null;
			Map<String, Object> pivotMap = null;
			if (modifier.containsKey(MUMBLE_UNPIVOT_KEY)) {
				unpivotMap = (Map<String, Object>) modifier.get(MUMBLE_UNPIVOT_KEY);
			}
			if (modifier.containsKey(MUMBLE_PIVOT_KEY)) {
				pivotMap = (Map<String, Object>) modifier.get(MUMBLE_PIVOT_KEY);
			}

			// Keep relational modifier as a direct sibling of table/query source in tuple endpoint AST.
			// Example: {TUPLE={table={...}, pivot={...}}} instead of {TUPLE={table={...}, modifier={pivot={...}}}}
			sourceResult.putAll(modifier);
			if (unpivotMap != null) {
				resolveRelationalModifierScopeAtPrimaryExit(MUMBLE_UNPIVOT_KEY, sourceResult, null);
			} else if (pivotMap != null) {
				resolveRelationalModifierScopeAtPrimaryExit(MUMBLE_PIVOT_KEY, sourceResult, null);
			}
		}

		walker.addToParent(parentRuleIndex, parentStackLevel, sourceResult);
	}
	



	@Override
	public void exitDb_object_name( SQLSelectParserParser.Db_object_nameContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Map<String, Object> subMap = walker.getNodeMap(ruleIndex, stackLevel);
		Object type = subMap.remove(ASTWALKER_RULE_TYPE_KEY);

		if (subMap.size() == 1) {
			String table = (String) subMap.remove("1");
			subMap.put(MUMBLE_TABLE_KEY, table);
		} else if (subMap.size() == 2) {
			String schema = (String) subMap.remove("1");
			subMap.put(MUMBLE_SCHEMA_KEY, schema);
			String table = (String) subMap.remove("2");
			subMap.put(MUMBLE_TABLE_KEY, table);
		} else if (subMap.size() == 3) {
			String dbname = (String) subMap.remove("1");
			subMap.put(MUMBLE_DATABASE_NAME_KEY, dbname);
			String schema = (String) subMap.remove("2");
			subMap.put(MUMBLE_SCHEMA_KEY, schema);
			String table = (String) subMap.remove("3");
			subMap.put(MUMBLE_TABLE_KEY, table);
		} else {
			// Wrong number of entries
		}
	}

	@Override
	public void exitUnqualified_join( SQLSelectParserParser.Unqualified_joinContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		int parentRuleIndex = ctx.getParent().getRuleIndex();

		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Integer parentStackLevel = walker.currentStackLevel(parentRuleIndex);

		Map<String, Object> subMap = walker.removeNodeMap(ruleIndex, stackLevel);
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
	}

	@Override
	public void exitQualified_join( SQLSelectParserParser.Qualified_joinContext ctx) {
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
	}

	// join_type does NOT need its own methods
	
	
	@Override
	public void exitJoin_specification( SQLSelectParserParser.Join_specificationContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		int parentRuleIndex = ctx.getParent().getRuleIndex();

		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Integer parentStackLevel = walker.currentStackLevel(parentRuleIndex);

		Map<String, Object> subMap = walker.removeNodeMap(ruleIndex, stackLevel);
		Object type = subMap.remove(ASTWALKER_RULE_TYPE_KEY);
		if (subMap.size() == 1) {
			// get the most recent JOIN condition — scan backwards to skip any lateral
			// modifier entries that may sit between the qualified_join map and the right table
			Map<String, Object> pMap = walker.getNodeMap(parentRuleIndex, parentStackLevel);
			Map<String, Object> join = null;
			for (int i = pMap.size() - 1; i >= 1; i--) {
				Object candidate = pMap.get(String.valueOf(i));
				if (candidate instanceof Map<?, ?> candidateMap
						&& candidateMap.containsKey(MUMBLE_JOIN_KEY)) {
					join = (Map<String, Object>) candidateMap;
					break;
				}
			}
			if (join != null) {
				// Add On clause to previous Join statement; condition may be a Map (expression)
				// or a plain String (e.g. boolean literal TRUE from ON TRUE)
				Object rawCondition = subMap.remove("1");
				join.put(MUMBLE_JOIN_ON_KEY, rawCondition);
				symbolTreeHelper.captureJoinOnClauseDependencies(rawCondition);
			} else {
				//Could not locate join map for ON clause
			}
		} else {
			// Wrong number of entries
		}
		 walker.asTree.put("SKIP", "TRUE");
	}

	@Override
	public void exitJoin_condition( SQLSelectParserParser.Join_conditionContext ctx) {
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
	public void exitNamed_columns_join( SQLSelectParserParser.Named_columns_joinContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		walker.handleOneChild(ruleIndex);
	}
	
	// using_term does NOT need its own methods

	/*
	  ===========================================
	  PIVOT / UNPIVOT clauses
	  ===========================================
	*/

	@Override
	public void exitTable_relational_modifier( SQLSelectParserParser.Table_relational_modifierContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		walker.handleOneChild(ruleIndex);
	}

	// Handles the main UNPIVOT clause, building a subtree rooted at MUMBLE_UNPIVOT_KEY.
	// Each child type is identified by inspecting its content rather than by slot position,
	// so optional children do not affect the classification of required ones:
	//   - unpivot_null_policy  → Map containing MUMBLE_NULLS_POLICY_KEY
	//   - unpivot_list         → numbered map whose "1" entry is a flattened in-item map
	//                            (name/table_ref[/alias])
	//   - value / name columns → plain String (alias_identifier collapses via handleOneChild)
	//                            first String = value column, second = name column
	@Override
	public void enterUnpivot_clause( SQLSelectParserParser.Unpivot_clauseContext ctx) {
		// Mirror PIVOT behavior: isolate UNPIVOT artifacts in a short-lived local scope.
		enterRelationalModifierClauseScope(MUMBLE_UNPIVOT_KEY);
	}

	@Override
	
	public void exitUnpivot_clause( SQLSelectParserParser.Unpivot_clauseContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		int parentRuleIndex = ctx.getParent().getRuleIndex();

		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Integer parentStackLevel = walker.currentStackLevel(parentRuleIndex);

		Map<String, Object> subMap = walker.removeNodeMap(ruleIndex, stackLevel);
		subMap.remove(ASTWALKER_RULE_TYPE_KEY);

		Object nullPolicy = null;
		Object inList     = null;
		List<Object> nameSlots = new ArrayList<>();   // valueCol then nameCol, in arrival order

		for (int i = 1; subMap.containsKey(String.valueOf(i)); i++) {
			Object entry = subMap.get(String.valueOf(i));
			if (entry instanceof Map<?, ?> entryMap) {
				if (entryMap.containsKey(MUMBLE_NULLS_POLICY_KEY)) {
					nullPolicy = entry;
				} else if (entryMap.containsKey("1")
						&& entryMap.get("1") instanceof Map<?, ?> firstItem
						&& (((Map<?, ?>) firstItem).containsKey(MUMBLE_NAME_KEY)
								|| ((Map<?, ?>) firstItem).containsKey(MUMBLE_TABLE_REF_KEY)
								|| ((Map<?, ?>) firstItem).containsKey(MUMBLE_ALIAS_KEY))) {
					inList = entry;
				}
			} else {
				nameSlots.add(entry);   // plain String identifier — value_col, then name_col
			}
		}

		Object valueCol = nameSlots.size() > 0 ? nameSlots.get(0) : null;
		Object nameCol  = nameSlots.size() > 1 ? nameSlots.get(1) : null;

		   Map<String, Object> unpivotMap = new LinkedHashMap<>();
		   if (nullPolicy != null)
			   unpivotMap.put(MUMBLE_NULLS_POLICY_KEY, nullPolicy);
		   unpivotMap.put(mumble.MumbleConstants.MUMBLE_VALUE_KEY, valueCol);
		   unpivotMap.put(mumble.MumbleConstants.MUMBLE_FOR_KEY, nameCol);
		   unpivotMap.put(mumble.MumbleConstants.MUMBLE_IN_KEY, inList);
		   // Do NOT add ASTWALKER_RULE_TYPE_KEY or MUMBLE_UNPIVOT_KEY=true here

		   // Wrap in a map with MUMBLE_UNPIVOT_KEY as the only key
		   Map<String, Object> wrapper = new LinkedHashMap<>();
		   wrapper.put(MUMBLE_UNPIVOT_KEY, unpivotMap);
		   walker.addToParent(parentRuleIndex, parentStackLevel, wrapper);

		   registerUnpivotValueInterfaceHint(unpivotMap);
		   publishRelationalModifierScopeAndPop(MUMBLE_UNPIVOT_KEY, ctx.getStart());
	}


	@Override
	public void exitUnpivot_null_policy( SQLSelectParserParser.Unpivot_null_policyContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		int parentRuleIndex = ctx.getParent().getRuleIndex();

		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Integer parentStackLevel = walker.currentStackLevel(parentRuleIndex);
		
		String item = ctx.getChild(0).getText();
		
		Map<String, Object> subMap = new HashMap<String, Object>();
		subMap.put(MUMBLE_NULLS_POLICY_KEY, item);
		subMap.put(ASTWALKER_RULE_TYPE_KEY, SQLSelectParserParser.RULE_unpivot_null_policy);
		
		walker.addToParent(parentRuleIndex, parentStackLevel, subMap);
	}

	// relational_modifier_value_column and relational_modifier_name_column each wrap a single alias_identifier — propagate directly.
	@Override
	public void exitRelational_modifier_value_column( SQLSelectParserParser.Relational_modifier_value_columnContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		walker.handleOneChild(ruleIndex);
	}

	@Override
	public void exitRelational_modifier_name_column( SQLSelectParserParser.Relational_modifier_name_columnContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		walker.handleOneChild(ruleIndex);
	}

	// unpivot_in_alias: (AS)? (alias_identifier | Character_String_Literal)
	// alias_identifier exits via handleOneChild and is stored as "1" in the subMap.
	// Character_String_Literal is a terminal token and is not placed into the subMap by visitTerminal;
	// in that case we read it directly from the parse tree as the last child.
	// Either way we push just the plain String value up to the parent — AS is discarded.
	@Override
	public void exitRelational_modifier_alias( SQLSelectParserParser.Relational_modifier_aliasContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		int parentRuleIndex = ctx.getParent().getRuleIndex();

		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Integer parentStackLevel = walker.currentStackLevel(parentRuleIndex);

		Map<String, Object> subMap = walker.removeNodeMap(ruleIndex, stackLevel);
		subMap.remove(ASTWALKER_RULE_TYPE_KEY);

		String value;
		if (subMap.containsKey("1")) {
			// alias_identifier rule — already resolved to a String by exitAlias_identifier
			value = (String) subMap.get("1");
		} else {
			// Character_String_Literal terminal — last child is always the value regardless of optional AS
			value = ctx.getChild(ctx.getChildCount() - 1).getText();
		}

		walker.addToParent(parentRuleIndex, parentStackLevel, value);
	}

	// relational_modifier_in_item: column_reference relational_modifier_alias?
	// Flattens the column_reference payload so each in-list item is directly:
	//   {name: <column_name>, table_ref: <table_or_null>[, label: <label>]}
	@Override
	
	public void exitRelational_modifier_in_item( SQLSelectParserParser.Relational_modifier_in_itemContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		int parentRuleIndex = ctx.getParent().getRuleIndex();

		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Integer parentStackLevel = walker.currentStackLevel(parentRuleIndex);

		Map<String, Object> subMap = walker.removeNodeMap(ruleIndex, stackLevel);
		subMap.remove(ASTWALKER_RULE_TYPE_KEY);

		Object columnRef = subMap.remove("1");   // column_reference map
		Object label    = subMap.containsKey("2") ? subMap.remove("2") : null;

		Map<String, Object> item;
		if (columnRef instanceof Map<?, ?> columnRefMap && columnRefMap.containsKey(MUMBLE_COLUMN_KEY)) {
			item = (Map<String, Object>) columnRefMap.get(MUMBLE_COLUMN_KEY);
		} else if (columnRef instanceof Map<?, ?> columnRefMap) {
			item = (Map<String, Object>) columnRefMap;
		} else {
			item = new HashMap<String, Object>();
			if (columnRef != null) {
				item.put(MUMBLE_NAME_KEY, columnRef);
			}
		}

		if (label != null) {
			item.put(MUMBLE_LABEL_KEY, label);
		}

		walker.addToParent(parentRuleIndex, parentStackLevel, item);
	}

	// relational_modifier_list: LEFT_PAREN relational_modifier_in_item (COMMA relational_modifier_in_item)* RIGHT_PAREN
	// Builds a numbered map of flattened in-items (keyed "1", "2", ...)
	@Override
	
	public void exitRelational_modifier_list( SQLSelectParserParser.Relational_modifier_listContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Map<String, Object> subMap = walker.getNodeMap(ruleIndex, stackLevel);
		subMap.remove(ASTWALKER_RULE_TYPE_KEY);

		Map<String, Object> numbered = new LinkedHashMap<String, Object>();
		int count = ctx.relational_modifier_in_item().size();
		for (int i = 1; i <= count; i++) {
			numbered.put(String.valueOf(i), subMap.get(String.valueOf(i)));
		}
		walker.collect(ruleIndex, stackLevel, numbered);
	}

	@Override
	public void enterPivot_clause( SQLSelectParserParser.Pivot_clauseContext ctx) {
		enterRelationalModifierClauseScope(MUMBLE_PIVOT_KEY);
	}

	@Override
	public void exitPivot_clause( SQLSelectParserParser.Pivot_clauseContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		int parentRuleIndex = ctx.getParent().getRuleIndex();

		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Integer parentStackLevel = walker.currentStackLevel(parentRuleIndex);

		Map<String, Object> subMap = walker.removeNodeMap(ruleIndex, stackLevel);
		subMap.remove(ASTWALKER_RULE_TYPE_KEY);

		// Grammar order:
		//   1) pivot_aggregate
		//   2) pivot_value_column
		//   3) pivot_in_clause
		Object aggregate = subMap.get("1");
		Object nameCol = subMap.get("2");
		Object inList = subMap.get("3");

		Map<String, Object> pivotMap = new LinkedHashMap<>();
		pivotMap.put(MUMBLE_VALUE_KEY, aggregate);
		pivotMap.put(MUMBLE_FOR_KEY, nameCol);
		pivotMap.put(MUMBLE_IN_KEY, inList);

		Map<String, Object> wrapper = new LinkedHashMap<>();
		wrapper.put(MUMBLE_PIVOT_KEY, pivotMap);

		walker.addToParent(parentRuleIndex, parentStackLevel, wrapper);
		
		// Register pivot hints for later interface/unresolved derivation
		registerPivotValueInterfaceHint(aggregate, inList, nameCol);

		// Collect the pivot symbol table and add to parent after building the AST
		publishRelationalModifierScopeAndPop(MUMBLE_PIVOT_KEY, ctx.getStart());
		walker.queryCount++;
	}

	@Override
	public void exitPivot_aggregate( SQLSelectParserParser.Pivot_aggregateContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Map<String, Object> subMap = walker.getNodeMap(ruleIndex, stackLevel);
		subMap.remove(ASTWALKER_RULE_TYPE_KEY);

		if (subMap.size() >= 2) {
			Map<String, Object> function = new LinkedHashMap<String, Object>();
			function.put(MUMBLE_FUNCTION_NAME_KEY, subMap.remove("1"));
			function.put(MUMBLE_PARAMETERS_KEY, subMap.remove("2"));
			Object aliasObj = subMap.remove("3");

			subMap.clear();
			subMap.put(MUMBLE_FUNCTION_KEY, function);
			if (aliasObj instanceof Map<?, ?> aliasMap && aliasMap.containsKey(MUMBLE_ALIAS_KEY)) {
				subMap.put(MUMBLE_ALIAS_KEY, aliasMap.get(MUMBLE_ALIAS_KEY));
			}
		}
	}

	@Override
	public void exitPivot_aggregate_clause( SQLSelectParserParser.Pivot_aggregate_clauseContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		walker.handleOneChild(ruleIndex);
	}

	@Override
	public void exitSnowflake_pivot_aggregate_list( SQLSelectParserParser.Snowflake_pivot_aggregate_listContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Map<String, Object> subMap = walker.getNodeMap(ruleIndex, stackLevel);
		subMap.remove(ASTWALKER_RULE_TYPE_KEY);

		Map<String, Object> numbered = new LinkedHashMap<String, Object>();
		int count = ctx.snowflake_pivot_aggregate().size();
		for (int i = 1; i <= count; i++) {
			numbered.put(String.valueOf(i), subMap.get(String.valueOf(i)));
		}
		walker.collect(ruleIndex, stackLevel, numbered);
	}

	@Override
	public void exitSnowflake_pivot_aggregate( SQLSelectParserParser.Snowflake_pivot_aggregateContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Map<String, Object> subMap = walker.getNodeMap(ruleIndex, stackLevel);
		subMap.remove(ASTWALKER_RULE_TYPE_KEY);

		if (subMap.size() >= 2) {
			Map<String, Object> function = new LinkedHashMap<String, Object>();
			function.put(MUMBLE_FUNCTION_NAME_KEY, subMap.remove("1"));
			function.put(MUMBLE_PARAMETERS_KEY, subMap.remove("2"));
			Object aliasObj = subMap.remove("3");

			subMap.clear();
			subMap.put(MUMBLE_FUNCTION_KEY, function);
			if (aliasObj instanceof Map<?, ?> aliasMap && aliasMap.containsKey(MUMBLE_ALIAS_KEY)) {
				subMap.put(MUMBLE_ALIAS_KEY, aliasMap.get(MUMBLE_ALIAS_KEY));
			}
		}
	}

	@Override
	public void exitPivot_value_column( SQLSelectParserParser.Pivot_value_columnContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		walker.handleOneChild(ruleIndex);
	}

	@Override
	public void exitPivot_in_clause( SQLSelectParserParser.Pivot_in_clauseContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		walker.handleOneChild(ruleIndex);
	}

	@Override
	public void exitPivot_in_content( SQLSelectParserParser.Pivot_in_contentContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		walker.handleOneChild(ruleIndex);
	}

	@Override
	public void exitPivot_in_value_list( SQLSelectParserParser.Pivot_in_value_listContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Map<String, Object> subMap = walker.getNodeMap(ruleIndex, stackLevel);
		subMap.remove(ASTWALKER_RULE_TYPE_KEY);

		Map<String, Object> numbered = new LinkedHashMap<String, Object>();
		int count = ctx.pivot_in_value().size();
		for (int i = 1; i <= count; i++) {
			numbered.put(String.valueOf(i), subMap.get(String.valueOf(i)));
		}
		walker.collect(ruleIndex, stackLevel, numbered);
	}

	@Override
	public void exitPivot_in_value( SQLSelectParserParser.Pivot_in_valueContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		int parentRuleIndex = ctx.getParent().getRuleIndex();

		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Integer parentStackLevel = walker.currentStackLevel(parentRuleIndex);
		Map<String, Object> subMap = walker.removeNodeMap(ruleIndex, stackLevel);
		if (subMap != null) {
			subMap.remove(ASTWALKER_RULE_TYPE_KEY);
		}

		String literalText = ctx.pivot_in_literal().getText();
		Map<String, Object> item = new LinkedHashMap<String, Object>();
		item.put(MUMBLE_PIVOT_LITERAL_KEY, literalText);
		if (ctx.pivot_in_prefix() != null) {
			item.put(MUMBLE_PIVOT_PREFIX_KEY, ctx.pivot_in_prefix().getText().replaceFirst("(?i)^AS", "").trim());
		}

		walker.addToParent(parentRuleIndex, parentStackLevel, item);
	}

	@Override
	public void exitPivot_in_literal( SQLSelectParserParser.Pivot_in_literalContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Map<String, Object> subMap = walker.removeNodeMap(ruleIndex, stackLevel);
		if (subMap != null) {
			subMap.remove(ASTWALKER_RULE_TYPE_KEY);
		}

		// Identifier-form PIVOT IN values can be static source values in Snowflake
		// when the PIVOT source is a query exposing matching columns.
		if (ctx.identifier() != null) {
			String identifierText = ctx.identifier().getText();

			@SuppressWarnings("unchecked")
			Map<String, Object> pivotInIdentifiers =
					(Map<String, Object>) walker.symbolTable.get(PIVOT_IN_IDENTIFIER_REFERENCES_KEY);
			if (pivotInIdentifiers != null) {
				Object tokenRefsObj = pivotInIdentifiers.get(identifierText);
				ArrayList<String> tokenRefs;
				if (tokenRefsObj instanceof ArrayList<?>) {
					tokenRefs = (ArrayList<String>) tokenRefsObj;
				} else {
					tokenRefs = new ArrayList<String>();
					pivotInIdentifiers.put(identifierText, tokenRefs);
				}
				String tokenString = ctx.getStart() == null ? null : ctx.getStart().toString();
				if (tokenString != null && !tokenRefs.contains(tokenString)) {
					tokenRefs.add(tokenString);
				}
			}
		}
	}

	@Override
	public void exitPivot_in_prefix( SQLSelectParserParser.Pivot_in_prefixContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Map<String, Object> subMap = walker.removeNodeMap(ruleIndex, stackLevel);
		if (subMap != null) {
			subMap.remove(ASTWALKER_RULE_TYPE_KEY);
		}
	}

	@Override
	public void exitPivot_in_any( SQLSelectParserParser.Pivot_in_anyContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Map<String, Object> subMap = walker.getNodeMap(ruleIndex, stackLevel);
		subMap.remove(ASTWALKER_RULE_TYPE_KEY);

		Map<String, Object> anyMap = new LinkedHashMap<String, Object>();
		anyMap.put(MUMBLE_NAME_KEY, "ANY");
		if (subMap.containsKey("1")) {
			anyMap.put(MUMBLE_ORDERBY_KEY, subMap.remove("1"));
		}
		subMap.clear();
		subMap.putAll(anyMap);
	}

	@Override
	public void exitPivot_in_subquery( SQLSelectParserParser.Pivot_in_subqueryContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		walker.handleOneChild(ruleIndex);
	}

	/*
	  ===========================================
	  TABLE FUNCTION clauses
	  ===========================================
	*/
	private Map<String, Object> buildSequentialList(Map<String, Object> subMap) {
		Map<String, Object> ordered = new LinkedHashMap<String, Object>();
		for (int inputIndex = 1, outputIndex = 1; subMap.containsKey(String.valueOf(inputIndex)); inputIndex++) {
			ordered.put(String.valueOf(outputIndex++), subMap.get(String.valueOf(inputIndex)));
		}
		return ordered;
	}

	@Override
	public void exitTable_function_primary( SQLSelectParserParser.Table_function_primaryContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Map<String, Object> subMap = walker.getNodeMap(ruleIndex, stackLevel);
		subMap.remove(ASTWALKER_RULE_TYPE_KEY);

		if (subMap.size() == 1) {
			subMap.put(MUMBLE_TABLE_FUNCTION_KEY, subMap.remove("1"));
		}
	}

	@Override
	public void exitTable_function( SQLSelectParserParser.Table_functionContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		walker.handleOneChild(ruleIndex);
	}

	
	@Override
	public void exitFlatten_table_function( SQLSelectParserParser.Flatten_table_functionContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Map<String, Object> subMap = walker.getNodeMap(ruleIndex, stackLevel);
		subMap.remove(ASTWALKER_RULE_TYPE_KEY);

		if (subMap.size() >= 1) {
			Map<String, Object> item = new HashMap<String, Object>();
			item.put(MUMBLE_FUNCTION_NAME_KEY, subMap.remove("1"));
			if (subMap.containsKey("2")) {
				item.put(MUMBLE_PARAMETERS_KEY, subMap.remove("2"));
			} else {
				item.put(MUMBLE_PARAMETERS_KEY, null);
			}
			subMap.putAll(item);
		}
	}

	
	@Override
	public void exitFlatten_argument_list( SQLSelectParserParser.Flatten_argument_listContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Map<String, Object> subMap = walker.getNodeMap(ruleIndex, stackLevel);
		subMap.remove(ASTWALKER_RULE_TYPE_KEY);

		Map<String, Object> params = new LinkedHashMap<String, Object>();
		for (int inputIndex = 1; subMap.containsKey(String.valueOf(inputIndex)); inputIndex++) {
			Object entryObj = subMap.get(String.valueOf(inputIndex));
			if (entryObj instanceof Map<?, ?>) {
				Map<String, Object> entry = (Map<String, Object>) entryObj;
				for (Map.Entry<String, Object> kv : entry.entrySet()) {
					params.put(kv.getKey(), kv.getValue());
				}
			}
		}

		subMap.clear();
		subMap.putAll(params);
	}

	@Override
	public void exitFlatten_argument( SQLSelectParserParser.Flatten_argumentContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Map<String, Object> subMap = walker.getNodeMap(ruleIndex, stackLevel);
		subMap.remove(ASTWALKER_RULE_TYPE_KEY);

		Object argumentValue = subMap.remove("1");
		Map<String, Object> argument = new LinkedHashMap<String, Object>();

		if (ctx.INPUT() != null) {
			argument.put(MUMBLE_INPUT_KEY, argumentValue);
		} else if (ctx.PATH() != null) {
			argument.put(MUMBLE_PATH_KEY, argumentValue);
		} else if (ctx.OUTER() != null) {
			argument.put(MUMBLE_OUTER_KEY, argumentValue);
		} else if (ctx.RECURSIVE() != null) {
			argument.put(MUMBLE_RECURSIVE_KEY, argumentValue);
		} else if (ctx.MODE() != null) {
			argument.put(MUMBLE_MODE_KEY, argumentValue);
		}

		subMap.clear();
		subMap.putAll(argument);
	}

	@Override
	public void exitFlatten_argument_value( SQLSelectParserParser.Flatten_argument_valueContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		walker.handleOneChild(ruleIndex);
	}

	
	@Override
	public void exitTable_argument_literal( SQLSelectParserParser.Table_argument_literalContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		if (stackLevel == null) {
			return;
		}

		Map<String, Object> subMap = walker.getNodeMap(ruleIndex, stackLevel);
		if (subMap == null) {
			return;
		}

		subMap.remove(ASTWALKER_RULE_TYPE_KEY);
		if (subMap.isEmpty()) {
			subMap.put(MUMBLE_LITERAL_KEY, ctx.getText());
			return;
		}

		Object child = subMap.remove("1");
		subMap.clear();
		if (child instanceof Map<?, ?> childMapObj) {
			subMap.putAll((Map<String, Object>) childMapObj);
		} else if (child != null) {
			subMap.put(MUMBLE_LITERAL_KEY, child);
		} else {
			subMap.put(MUMBLE_LITERAL_KEY, ctx.getText());
		}
	}

	@Override
	public void exitTable_argument_boolean( SQLSelectParserParser.Table_argument_booleanContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Map<String, Object> subMap = walker.getNodeMap(ruleIndex, stackLevel);
		subMap.remove(ASTWALKER_RULE_TYPE_KEY);
		subMap.put(MUMBLE_LITERAL_KEY, "TRUE".equalsIgnoreCase(ctx.getText()));
	}

	@Override
	public void exitGenerator_table_function( SQLSelectParserParser.Generator_table_functionContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Map<String, Object> subMap = walker.getNodeMap(ruleIndex, stackLevel);
		subMap.remove(ASTWALKER_RULE_TYPE_KEY);

		if (subMap.size() >= 1) {
			Map<String, Object> item = new HashMap<String, Object>();
			item.put(MUMBLE_FUNCTION_NAME_KEY, subMap.remove("1"));
			if (subMap.containsKey("2")) {
				item.put(MUMBLE_PARAMETERS_KEY, subMap.remove("2"));
			} else {
				item.put(MUMBLE_PARAMETERS_KEY, null);
			}
			subMap.putAll(item);
		}
	}

	
	@Override
	public void exitGenerator_argument_list( SQLSelectParserParser.Generator_argument_listContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Map<String, Object> subMap = walker.getNodeMap(ruleIndex, stackLevel);
		subMap.remove(ASTWALKER_RULE_TYPE_KEY);

		Map<String, Object> params = new LinkedHashMap<String, Object>();
		for (int inputIndex = 1; subMap.containsKey(String.valueOf(inputIndex)); inputIndex++) {
			Object entryObj = subMap.get(String.valueOf(inputIndex));
			if (entryObj instanceof Map<?, ?>) {
				Map<String, Object> entry = (Map<String, Object>) entryObj;
				for (Map.Entry<String, Object> kv : entry.entrySet()) {
					params.put(kv.getKey(), kv.getValue());
				}
			}
		}

		subMap.clear();
		subMap.putAll(params);
	}

	@Override
	public void exitGenerator_argument( SQLSelectParserParser.Generator_argumentContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Map<String, Object> subMap = walker.getNodeMap(ruleIndex, stackLevel);
		subMap.remove(ASTWALKER_RULE_TYPE_KEY);

		Object argumentValue = subMap.remove("1");
		Map<String, Object> argument = new LinkedHashMap<String, Object>();

		if (ctx.ROWCOUNT() != null) {
			argument.put(MUMBLE_ROWCOUNT_KEY, argumentValue);
		} else if (ctx.TIMELIMIT() != null) {
			argument.put(MUMBLE_TIMELIMIT_KEY, argumentValue);
		}

		subMap.clear();
		subMap.putAll(argument);
	}

	@Override
	public void exitGenerator_argument_value( SQLSelectParserParser.Generator_argument_valueContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		walker.handleOneChild(ruleIndex);
	}

	@Override
	public void exitResult_scan_table_function( SQLSelectParserParser.Result_scan_table_functionContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Map<String, Object> subMap = walker.getNodeMap(ruleIndex, stackLevel);
		subMap.remove(ASTWALKER_RULE_TYPE_KEY);

		if (subMap.size() >= 1) {
			Map<String, Object> item = new HashMap<String, Object>();
			Map<String, Object> params = null;

			item.put(MUMBLE_FUNCTION_NAME_KEY, subMap.remove("1"));
			if (subMap.containsKey("2")) {
				params = new LinkedHashMap<String, Object>();
				params.put(MUMBLE_ARGUMENT_KEY, subMap.remove("2"));
			}

			item.put(MUMBLE_PARAMETERS_KEY, params);
			subMap.put(MUMBLE_FUNCTION_KEY, item);
		}
	}

	@Override
	public void exitInfer_schema_table_function( SQLSelectParserParser.Infer_schema_table_functionContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Map<String, Object> subMap = walker.getNodeMap(ruleIndex, stackLevel);
		subMap.remove(ASTWALKER_RULE_TYPE_KEY);

		if (subMap.size() >= 1) {
			Map<String, Object> item = new HashMap<String, Object>();
			item.put(MUMBLE_FUNCTION_NAME_KEY, subMap.remove("1"));
			if (subMap.containsKey("2")) {
				item.put(MUMBLE_PARAMETERS_KEY, subMap.remove("2"));
			} else {
				item.put(MUMBLE_PARAMETERS_KEY, null);
			}
			subMap.putAll(item);
		}
	}

	
	@Override
	public void exitInfer_schema_argument_list( SQLSelectParserParser.Infer_schema_argument_listContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Map<String, Object> subMap = walker.getNodeMap(ruleIndex, stackLevel);
		subMap.remove(ASTWALKER_RULE_TYPE_KEY);

		Map<String, Object> params = new LinkedHashMap<String, Object>();
		for (int inputIndex = 1; subMap.containsKey(String.valueOf(inputIndex)); inputIndex++) {
			Object entryObj = subMap.get(String.valueOf(inputIndex));
			if (entryObj instanceof Map<?, ?>) {
				Map<String, Object> entry = (Map<String, Object>) entryObj;
				for (Map.Entry<String, Object> kv : entry.entrySet()) {
					params.put(kv.getKey(), kv.getValue());
				}
			}
		}

		subMap.clear();
		subMap.putAll(params);
	}

	@Override
	public void exitInfer_schema_argument( SQLSelectParserParser.Infer_schema_argumentContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Map<String, Object> subMap = walker.getNodeMap(ruleIndex, stackLevel);
		subMap.remove(ASTWALKER_RULE_TYPE_KEY);

		Object argumentValue = subMap.remove("1");
		Map<String, Object> argument = new LinkedHashMap<String, Object>();

		if (ctx.LOCATION() != null) {
			argument.put(MUMBLE_LOCATION_KEY, argumentValue);
		} else if (ctx.FILE_FORMAT() != null) {
			argument.put(MUMBLE_FILE_FORMAT_KEY, argumentValue);
		} else if (ctx.FILES() != null) {
			argument.put(MUMBLE_FILES_KEY, argumentValue);
		} else if (ctx.IGNORE_CASE() != null) {
			argument.put(MUMBLE_IGNORE_CASE_KEY, argumentValue);
		} else if (ctx.MAX_FILE_COUNT() != null) {
			argument.put(MUMBLE_MAX_FILE_COUNT_KEY, argumentValue);
		} else if (ctx.MAX_RECORDS_PER_FILE() != null) {
			argument.put(MUMBLE_MAX_RECORDS_PER_FILE_KEY, argumentValue);
		} else if (ctx.KIND() != null) {
			argument.put(MUMBLE_KIND_KEY, argumentValue);
		}

		subMap.clear();
		subMap.putAll(argument);
	}

	@Override
	public void exitInfer_schema_argument_value( SQLSelectParserParser.Infer_schema_argument_valueContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		walker.handleOneChild(ruleIndex);
	}

	@Override
	public void exitInfer_schema_files_argument( SQLSelectParserParser.Infer_schema_files_argumentContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Map<String, Object> subMap = walker.getNodeMap(ruleIndex, stackLevel);
		subMap.remove(ASTWALKER_RULE_TYPE_KEY);

		Map<String, Object> filesList = new LinkedHashMap<String, Object>();
		int index = 1;
		for (TerminalNode literalNode : ctx.Character_String_Literal()) {
			Map<String, Object> literalMap = new HashMap<String, Object>();
			literalMap.put(MUMBLE_LITERAL_KEY, literalNode.getText());
			filesList.put(String.valueOf(index++), literalMap);
		}

		subMap.clear();
		subMap.putAll(filesList);
	}

	@Override
	public void exitValidate_table_function( SQLSelectParserParser.Validate_table_functionContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Map<String, Object> subMap = walker.getNodeMap(ruleIndex, stackLevel);
		subMap.remove(ASTWALKER_RULE_TYPE_KEY);

		if (subMap.size() >= 3) {
			Map<String, Object> item = new HashMap<String, Object>();
			Map<String, Object> params = new LinkedHashMap<String, Object>();

			item.put(MUMBLE_FUNCTION_NAME_KEY, subMap.remove("1"));
			params.put(MUMBLE_TABLE_KEY, subMap.remove("2"));
			params.put(MUMBLE_JOB_ID_KEY, subMap.remove("3"));

			item.put(MUMBLE_PARAMETERS_KEY, params);
			subMap.put(MUMBLE_FUNCTION_KEY, item);
		}
	}

	@Override
	public void exitGeneric_table_function( SQLSelectParserParser.Generic_table_functionContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Map<String, Object> subMap = walker.getNodeMap(ruleIndex, stackLevel);
		subMap.remove(ASTWALKER_RULE_TYPE_KEY);

		if (subMap.size() >= 1) {
			Map<String, Object> item = new HashMap<String, Object>();
			item.put(MUMBLE_FUNCTION_NAME_KEY, subMap.remove("1"));
			if (subMap.containsKey("2")) {
				item.put(MUMBLE_PARAMETERS_KEY, subMap.remove("2"));
			} else {
				item.put(MUMBLE_PARAMETERS_KEY, null);
			}
			subMap.put(MUMBLE_FUNCTION_KEY, item);
		}
	}

	@Override
	public void exitTable_function_name( SQLSelectParserParser.Table_function_nameContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		// Only invoke handleOneChild when a node map exists; for single-terminal keywords
		// (SPLIT_TO_TABLE, STRTOK_SPLIT_TO_TABLE, QUERY_HISTORY) no map is created and
		// exitEveryRule handles promotion of ctx.getText() to the parent automatically.
		if (walker.getNodeMap(ruleIndex, stackLevel) != null) {
			walker.handleOneChild(ruleIndex);
		}
	}

	@Override
	public void exitTable_function_argument_list( SQLSelectParserParser.Table_function_argument_listContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Map<String, Object> subMap = walker.getNodeMap(ruleIndex, stackLevel);
		subMap.remove(ASTWALKER_RULE_TYPE_KEY);

		Map<String, Object> orderedArgs = buildSequentialList(subMap);
		subMap.clear();
		subMap.putAll(orderedArgs);
	}

	
	/*
	===============================================================================
	  Column List clauses
	===============================================================================
	*/
	

	@Override
	public void exitColumn_reference_list( SQLSelectParserParser.Column_reference_listContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Map<String, Object> subMap = walker.getNodeMap(ruleIndex, stackLevel);
		subMap.remove(ASTWALKER_RULE_TYPE_KEY);
	}

	@Override
	public void exitColumn_reference( SQLSelectParserParser.Column_referenceContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Map<String, Object> subMap = walker.getNodeMap(ruleIndex, stackLevel);
		subMap.remove(ASTWALKER_RULE_TYPE_KEY);

		Map<String, Object> columnSubTree = new HashMap<String, Object>();
		Object columnRef = null;
		String jsonPath = null;
		String tableRef = null;
		String tableRefKey = MUMBLE_UNKNOWN_KEY;

		if (ctx.substitution != null) {
			if (ctx.tb_name != null) {
				tableRef = ctx.tb_name.getText();
				tableRefKey = tableRef;
			}
			columnRef = subMap.remove(Integer.toString(subMap.size()));
			if (columnRef instanceof HashMap<?, ?> columnMap) {
				columnRef = markColumnSubstitution((HashMap<String, Object>) columnMap, MUMBLE_COLUMN_KEY);
			}
		} else if (ctx.name != null) {
			if (ctx.tb_name != null) {
				tableRef = ctx.tb_name.getText();
				tableRefKey = tableRef;
			}
			columnRef = ctx.name.getText();
			jsonPath = buildJsonPath(ctx.path_name);
		} else {
			//No recognized column reference entries
		}

		if (columnRef != null) {
			// Add column to SQL AST Tree
			columnSubTree.put(MUMBLE_TABLE_REF_KEY, tableRef);
			if (columnRef instanceof HashMap<?, ?>) {
				columnSubTree.putAll((HashMap<String, Object>) columnRef);
			} else {
				columnSubTree.put(MUMBLE_NAME_KEY, columnRef);
				if (jsonPath != null) {
					columnSubTree.put(MUMBLE_JSON_PATH_KEY, jsonPath);
				}
			}
			subMap.clear();
			subMap.put(MUMBLE_COLUMN_KEY, columnSubTree);

			// Capture walker.symbolTable entry
			walker.collectUnresolvedColumnReference(tableRefKey, columnSubTree, ctx.getStart());
		}
	}

	
	@Override
	public void exitColumn_primary( SQLSelectParserParser.Column_primaryContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Map<String, Object> subMap = walker.getNodeMap(ruleIndex, stackLevel);
		subMap.remove(ASTWALKER_RULE_TYPE_KEY);

		Map<String, Object> columnSubTree = new HashMap<String, Object>();
		Object columnRef = null;
		String jsonPath = null;
		String tableRef = null;
		String tableRefKey = MUMBLE_UNKNOWN_KEY;

		if (ctx.substitution != null) {
			// this is a column variable
			if (ctx.tb_name != null) {
				tableRef = ctx.tb_name.getText();
				tableRefKey = tableRef;
			}
			columnRef = subMap.remove(Integer.toString(subMap.size()));
			if (columnRef instanceof HashMap<?, ?> columnMap) {
				columnRef = markColumnSubstitution((HashMap<String, Object>) columnMap, MUMBLE_COLUMN_KEY);
			}
		} else if (ctx.name != null) {
				// this is a regular column reference
			if (ctx.tb_name != null) {
				tableRef = ctx.tb_name.getText();
				tableRefKey = tableRef;
			}
			columnRef = ctx.name.getText();
			jsonPath = buildJsonPath(ctx.path_name);
		} else {
			//No recognized column primary entries
		}

		if (columnRef != null) {
			// Add column to SQL AST Tree
			columnSubTree.put(MUMBLE_TABLE_REF_KEY, tableRef);
			if (columnRef instanceof HashMap<?, ?>) {
				columnSubTree.putAll((HashMap<String, Object>) columnRef);
			} else {
				columnSubTree.put(MUMBLE_NAME_KEY, columnRef);
				if (jsonPath != null) {
					columnSubTree.put(MUMBLE_JSON_PATH_KEY, jsonPath);
				}
			}
			subMap.clear();
			subMap.put(MUMBLE_COLUMN_KEY, columnSubTree);

			// Capture walker.symbolTable entry
			walker.collectUnresolvedColumnReference(tableRefKey, columnSubTree, ctx.getStart());
		}
	}

	@SuppressWarnings("unchecked")
	private HashMap<String, Object> markColumnSubstitution(HashMap<String, Object> columnMap, String type) {
		if (columnMap == null) {
			return null;
		}

		HashMap<String, Object> substitutionMap = (HashMap<String, Object>) columnMap.get(MUMBLE_SUBSTITUTION_KEY);
		if (substitutionMap != null) {
			substitutionMap.put(MUMBLE_TYPE_KEY, type);
			walker.substitutionsMap.put((String) substitutionMap.get("name"), type);
		}
		return columnMap;
	}

	private String buildJsonPath(List<SQLSelectParserParser.IdentifierContext> pathNodes) {
		if (pathNodes == null || pathNodes.isEmpty()) {
			return null;
		}

		StringBuilder builder = new StringBuilder();
		if (pathNodes != null) {
			for (SQLSelectParserParser.IdentifierContext pathNode : pathNodes) {
				if (pathNode == null) {
					continue;
				}
				if (builder.length() > 0) {
					builder.append(':');
				}
				builder.append(pathNode.getText());
			}
		}
		return builder.length() == 0 ? null : builder.toString();
	}

/*
===============================================================================
  Predicands <value expression primary>
===============================================================================
*/


	@Override
	public void exitPredicand_primary( SQLSelectParserParser.Predicand_primaryContext ctx) {
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

		Map<String, Object> item = walker.checkForSubstitutionVariable((Map<String, Object>) subMap.get("1"), MUMBLE_PREDICAND_KEY);


		if (item.containsKey(MUMBLE_SELECT_KEY)) {
			// then this item is a subquery, so we need to push it down under a LOOKUP subtree in the AST
				Map<String, Object> lookup = new HashMap<String, Object>();
				lookup.putAll(item);
				item = new HashMap<String, Object>();
				item.put(MUMBLE_LOOKUP_KEY, lookup);
				// replace the first entry in the AST Tree with the modified item subtree for scalar SQL trees
				subMap.put("1", item);
			}

		walker.handleOneChild(ruleIndex);
	}

	@Override
	public void exitValue_expression_primary( SQLSelectParserParser.Value_expression_primaryContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Map<String, Object> subMap = walker.getNodeMap(ruleIndex, stackLevel);
		Object type = subMap.remove(ASTWALKER_RULE_TYPE_KEY);

		if (subMap.size() == 1) {
			walker.handleOneChild(ruleIndex);
			return;
		}

		if (subMap.size() >= 2) {
			Object currentValue = subMap.remove("1");
			// Apply successive casts left to right (supports chained casts like col::text::varchar)
			int keyIdx = 2;
			Object nextType = subMap.remove(String.valueOf(keyIdx));
			Map<String, Object> outerCastItem = null;
			while (nextType != null) {
				Map<String, Object> castItem = new HashMap<String, Object>();
				castItem.put(MUMBLE_FUNCTION_NAME_KEY, MUMBLE_CAST_FUNCTION_NAME);
				castItem.put(MUMBLE_TYPE_KEY, MUMBLE_CAST_FUNCTION_NAME.toUpperCase(Locale.ROOT));
				castItem.put(MUMBLE_VALUE_KEY, currentValue);
				castItem.put(MUMBLE_DATATYPE_KEY, nextType);
				outerCastItem = castItem;
				Map<String, Object> wrapped = new HashMap<String, Object>();
				wrapped.put(MUMBLE_FUNCTION_KEY, castItem);
				currentValue = wrapped;
				keyIdx++;
				nextType = subMap.remove(String.valueOf(keyIdx));
			}
			if (outerCastItem != null) {
				subMap.clear();
				subMap.put(MUMBLE_FUNCTION_KEY, outerCastItem);
			}
		}
	}

	@Override
	public void exitParenthesized_value_expression(
			 SQLSelectParserParser.Parenthesized_value_expressionContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Map<String, Object> subMap = walker.getNodeMap(ruleIndex, stackLevel);
		Object type = subMap.remove(ASTWALKER_RULE_TYPE_KEY);

		if (subMap.size() == 1) {
			Map<String, Object> item = (Map<String, Object>) subMap.remove("1");
			subMap.put(MUMBLE_PARENTHESES_KEY, item);
		} else {
			//Wrong number of entries
		}
	}

	@Override
	public void exitNonparenthesized_value_expression_primary(
			 SQLSelectParserParser.Nonparenthesized_value_expression_primaryContext ctx) {
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
	public void exitCount_all_aggregate( SQLSelectParserParser.Count_all_aggregateContext ctx) {
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
			// Wrong number of entries
		}
	}

	// Part of the <aggregate_function> rule
	@Override
	public void exitGeneral_set_function( SQLSelectParserParser.General_set_functionContext ctx) {
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
			// Wrong number of entries
		}
	}

	// set_function_type does NOT need its own exit method
	// set_qualifier_type does NOT need its own exit method
	
	/*
	===============================================================================
	 CASE Clause <case expression>
	===============================================================================
	*/


		@Override
		public void exitCase_expression( SQLSelectParserParser.Case_expressionContext ctx) {
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
				// Wrong number of entries
			}

			Map<String, Object> item = new HashMap<String, Object>();
			item.put(MUMBLE_CASE_KEY, subMap);
			walker.addToParent(parentRuleIndex, parentStackLevel, item);
		}

		@Override
		public void exitWhen_clause_list( SQLSelectParserParser.When_clause_listContext ctx) {
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
				// Wrong number of entries
			}
			walker.addToParent(parentRuleIndex, parentStackLevel, item);
		}

		@Override
		public void exitSearched_when_clause( SQLSelectParserParser.Searched_when_clauseContext ctx) {
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
				// Wrong number of entries
			}
			walker.addToParent(parentRuleIndex, parentStackLevel, item);
		}

		@Override
		public void exitWhen_value_list( SQLSelectParserParser.When_value_listContext ctx) {
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
				// Wrong number of entries
			}
			walker.addToParent(parentRuleIndex, parentStackLevel, item);
		}

		@Override
		public void exitWhen_value_clause( SQLSelectParserParser.When_value_clauseContext ctx) {
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
				// Wrong number of entries
			}
			walker.addToParent(parentRuleIndex, parentStackLevel, item);
		}

		@Override
		public void exitElse_clause( SQLSelectParserParser.Else_clauseContext ctx) {
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
				// Wrong number of entries
			}
			walker.addToParent(parentRuleIndex, parentStackLevel, item);
		}

		@Override
		public void exitCase_result( SQLSelectParserParser.Case_resultContext ctx) {
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
		public void exitCast_function_expression( SQLSelectParserParser.Cast_function_expressionContext ctx) {
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
				item.put(MUMBLE_VALUE_KEY, walker.checkForSubstitutionVariable((Map<String, Object>) subMap.remove("2"), MUMBLE_PREDICAND_KEY));
				item.put(MUMBLE_DATATYPE_KEY, subMap.remove("3"));
				subMap.put(MUMBLE_FUNCTION_KEY, item);
			} else {
				// Wrong number of entries
			}
		}

		@Override
		public void exitData_type( SQLSelectParserParser.Data_typeContext ctx) {
			int ruleIndex = ctx.getRuleIndex();
			walker.handleOneChild(ruleIndex);
		}


		@Override
		public void exitVariable_size_data_type( SQLSelectParserParser.Variable_size_data_typeContext ctx) {
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
				// Wrong number of entries
			}
		}


		@Override
		public void exitVariable_data_type_name( SQLSelectParserParser.Variable_data_type_nameContext ctx) {
			int ruleIndex = ctx.getRuleIndex();
			int parentRuleIndex = ctx.getParent().getRuleIndex();

			Integer stackLevel = walker.currentStackLevel(ruleIndex);
			Integer parentStackLevel = walker.currentStackLevel(parentRuleIndex);

			Map<String, Object> subMap = walker.removeNodeMap(ruleIndex, stackLevel);

			subMap = walker.makeRuleMap(ruleIndex);
			subMap.remove(ASTWALKER_RULE_TYPE_KEY);
			
			if (ctx.getChildCount() == 1) {
				String part = ctx.getChild(0).getText().toUpperCase();
				subMap.put("1", part);
			} else if (ctx.getChildCount() == 2) {
				String part = ctx.getChild(0).getText().toUpperCase();
				part = part + " " + ctx.getChild(1).getText().toUpperCase();
				subMap.put("1", part);
			} else if (ctx.getChildCount() == 3) {
				String part = ctx.getChild(0).getText().toUpperCase();
				part = part + " " + ctx.getChild(1).getText().toUpperCase();
				part = part + " " + ctx.getChild(2).getText().toUpperCase();
				subMap.put("1", part);
			}
			// Add item to parent map
			walker.addToParent(parentRuleIndex, parentStackLevel, subMap);
		}

		@Override
		public void exitType_length( SQLSelectParserParser.Type_lengthContext ctx) {
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
				subMap.put(MUMBLE_LENGTH_KEY, ctx.getChild(1).getText());
			}
			// Add item to parent map
			walker.addToParent(parentRuleIndex, parentStackLevel, subMap);
		}


		@Override
		public void exitPrecision_scale_data_type( SQLSelectParserParser.Precision_scale_data_typeContext ctx) {
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
				// Wrong number of entries
			}
		}


		@Override
		public void exitPrecision_data_type_name( SQLSelectParserParser.Precision_data_type_nameContext ctx) {
			int ruleIndex = ctx.getRuleIndex();
			int parentRuleIndex = ctx.getParent().getRuleIndex();

			Integer stackLevel = walker.currentStackLevel(ruleIndex);
			Integer parentStackLevel = walker.currentStackLevel(parentRuleIndex);

			Map<String, Object> subMap = walker.removeNodeMap(ruleIndex, stackLevel);

			subMap = walker.makeRuleMap(ruleIndex);
			subMap.remove(ASTWALKER_RULE_TYPE_KEY);
			
			if (ctx.getChildCount() == 1) {
				String part = ctx.getChild(0).getText().toUpperCase();
				subMap.put("1", part);
			} else if (ctx.getChildCount() == 2) {
				String part = ctx.getChild(0).getText().toUpperCase();
				part = part + " " + ctx.getChild(1).getText().toUpperCase();
				subMap.put("1", part);
			}
			// Add item to parent map
			walker.addToParent(parentRuleIndex, parentStackLevel, subMap);
		}

		@Override
		public void exitPrecision_param( SQLSelectParserParser.Precision_paramContext ctx) {
			int ruleIndex = ctx.getRuleIndex();
			int parentRuleIndex = ctx.getParent().getRuleIndex();

			Integer stackLevel = walker.currentStackLevel(ruleIndex);
			Integer parentStackLevel = walker.currentStackLevel(parentRuleIndex);

			Map<String, Object> subMap = walker.removeNodeMap(ruleIndex, stackLevel);
			subMap = walker.makeRuleMap(ruleIndex);
			subMap.remove(ASTWALKER_RULE_TYPE_KEY);
			
			if (ctx.getChildCount() == 3) {
				subMap.put(MUMBLE_PRECISION_KEY, ctx.getChild(1).getText());
			} else if (ctx.getChildCount() == 5) {
				subMap.put(MUMBLE_PRECISION_KEY, ctx.getChild(1).getText());
				subMap.put(MUMBLE_SCALE_KEY, ctx.getChild(3).getText());
			} 
			// Add item to parent map
			walker.addToParent(parentRuleIndex, parentStackLevel, subMap);
		}


		@Override
		public void exitStatic_data_type( SQLSelectParserParser.Static_data_typeContext ctx) {
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
				// Wrong number of entries
			}
		}

		@Override
		public void exitStatic_data_type_name( SQLSelectParserParser.Static_data_type_nameContext ctx) {
			int ruleIndex = ctx.getRuleIndex();
			int parentRuleIndex = ctx.getParent().getRuleIndex();

			Integer stackLevel = walker.currentStackLevel(ruleIndex);
			Integer parentStackLevel = walker.currentStackLevel(parentRuleIndex);

			Map<String, Object> subMap = walker.removeNodeMap(ruleIndex, stackLevel);

			subMap = walker.makeRuleMap(ruleIndex);
			subMap.remove(ASTWALKER_RULE_TYPE_KEY);
			
			if (ctx.getChildCount() == 1) {
				String part = ctx.getChild(0).getText().toUpperCase();
				subMap.put("1", part);
			} else if (ctx.getChildCount() == 2) {
				String part = ctx.getChild(0).getText().toUpperCase();
				part = part + " " + ctx.getChild(1).getText().toUpperCase();
				subMap.put("1", part);
			} else if (ctx.getChildCount() == 3) {
				String part = ctx.getChild(0).getText().toUpperCase();
				part = part + " " + ctx.getChild(1).getText().toUpperCase();
				part = part + " " + ctx.getChild(2).getText().toUpperCase();
				subMap.put("1", part);
			} else if (ctx.getChildCount() == 4) {
				String part = ctx.getChild(0).getText().toUpperCase();
				part = part + " " + ctx.getChild(1).getText().toUpperCase();
				part = part + " " + ctx.getChild(2).getText().toUpperCase();
				part = part + " " + ctx.getChild(3).getText().toUpperCase();
				subMap.put("1", part);
			}
			// Add item to parent map
			walker.addToParent(parentRuleIndex, parentStackLevel, subMap);	
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
				 SQLSelectParserParser.Window_over_partition_expressionContext ctx) {
			int ruleIndex = ctx.getRuleIndex();
			int parentRuleIndex = ctx.getParent().getRuleIndex();

			Integer stackLevel = walker.currentStackLevel(ruleIndex);
			Integer parentStackLevel = walker.currentStackLevel(parentRuleIndex);

			Map<String, Object> subMap = walker.getNodeMap(ruleIndex, stackLevel);
			Object type = subMap.remove(ASTWALKER_RULE_TYPE_KEY);

			if (subMap.size() == 2) {
				Map<String, Object> item = new HashMap<String, Object>();
				item.putAll((Map<String, Object>) subMap.remove("1"));
				item.putAll((Map<String, Object>) subMap.remove("2"));
				subMap.put(MUMBLE_WINDOW_FUNCTION_KEY, item);
			} else {
				// Wrong number of entries
			}
		}

		
		@Override
		public void exitWindow_function( SQLSelectParserParser.Window_functionContext ctx) {
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
				// Wrong number of entries
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
		}

		
		@Override
		public void exitOver_clause( SQLSelectParserParser.Over_clauseContext ctx) {
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
				// Wrong number of entries
			}
		}

		
		@Override
		public void exitPartition_by_clause( SQLSelectParserParser.Partition_by_clauseContext ctx) {
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
				// Wrong number of entries
			}

		}


		@Override
		public void exitBracket_frame_clause(
				 SQLSelectParserParser.Bracket_frame_clauseContext ctx) {
			int ruleIndex = ctx.getRuleIndex();
			int parentRuleIndex = ctx.getParent().getRuleIndex();

			Integer stackLevel = walker.currentStackLevel(ruleIndex);
			Integer parentStackLevel = walker.currentStackLevel(parentRuleIndex);

			Map<String, Object> subMap = walker.getNodeMap(ruleIndex, stackLevel);
			Object type = subMap.remove(ASTWALKER_RULE_TYPE_KEY);

			if (subMap.size() == 2) {
				Map<String, Object> item = new HashMap<String, Object>();
				item.put(MUMBLE_TYPE_KEY, (String) subMap.remove("1"));
				item.putAll((Map<String, Object>) subMap.remove("2"));
				subMap.put(MUMBLE_BRACKET_FRAME_KEY, item);
			} else {
				// Wrong number of entries
			}
		}

		// rows_or_range clauses do not need their own method
		

		@Override
		public void exitBracket_frame_definition( SQLSelectParserParser.Bracket_frame_definitionContext ctx) {
			int ruleIndex = ctx.getRuleIndex();
			walker.handleOneChild(ruleIndex);
		}

		@Override
		public void exitBetween_frame_definition( SQLSelectParserParser.Between_frame_definitionContext ctx) {
			int ruleIndex = ctx.getRuleIndex();
			int parentRuleIndex = ctx.getParent().getRuleIndex();

			Integer stackLevel = walker.currentStackLevel(ruleIndex);
			Integer parentStackLevel = walker.currentStackLevel(parentRuleIndex);

			Map<String, Object> subMap = walker.getNodeMap(ruleIndex, stackLevel);
			Object type = subMap.remove(ASTWALKER_RULE_TYPE_KEY);

			if (subMap.size() == 2) {
				Map<String, Object> item = new HashMap<String, Object>();
				item.put(MUMBLE_RANGE_BEGIN_KEY,  subMap.remove("1"));
				item.put(MUMBLE_RANGE_END_KEY,  subMap.remove("2"));
				subMap.put(MUMBLE_BETWEEN_KEY, item);
			} else {
				// Wrong number of entries
			}
		}


		@Override
		public void exitFrame_edge( SQLSelectParserParser.Frame_edgeContext ctx) {
			int ruleIndex = ctx.getRuleIndex();
			walker.handleOneChild(ruleIndex);
		}

		@Override
		public void exitPreceding_frame_edge(
				 SQLSelectParserParser.Preceding_frame_edgeContext ctx) {
			int ruleIndex = ctx.getRuleIndex();
			Integer stackLevel = walker.currentStackLevel(ruleIndex);
			Map<String, Object> subMap = walker.getNodeMap(ruleIndex, stackLevel);
			Object type = subMap.remove(ASTWALKER_RULE_TYPE_KEY);

			if (subMap.size() == 1) {
				subMap.put(MUMBLE_VALUE_KEY, (String) subMap.remove("1"));
				subMap.put(MUMBLE_BRACKET_DIRECTION_KEY, MUMBLE_PRECEDING_KEY);
			} else {
				// Wrong number of entries
			}
		}

		@Override
		public void exitFollowing_frame_edge(
				 SQLSelectParserParser.Following_frame_edgeContext ctx) {
			int ruleIndex = ctx.getRuleIndex();
			Integer stackLevel = walker.currentStackLevel(ruleIndex);
			Map<String, Object> subMap = walker.getNodeMap(ruleIndex, stackLevel);
			Object type = subMap.remove(ASTWALKER_RULE_TYPE_KEY);

			if (subMap.size() == 1) {
				subMap.put(MUMBLE_VALUE_KEY, (String) subMap.remove("1"));
				subMap.put(MUMBLE_BRACKET_DIRECTION_KEY, MUMBLE_FOLLOWING_KEY);
			} else {
				// Wrong number of entries
			}
		}


		@Override
		public void exitCurrent_row_edge( SQLSelectParserParser.Current_row_edgeContext ctx) {
			int ruleIndex = ctx.getRuleIndex();
			int parentRuleIndex = ctx.getParent().getRuleIndex();

			Integer stackLevel = walker.currentStackLevel(ruleIndex);
			Integer parentStackLevel = walker.currentStackLevel(parentRuleIndex);

			Map<String, Object> subMap = walker.removeNodeMap(ruleIndex, stackLevel);

			subMap = walker.makeRuleMap(ruleIndex);
			subMap.remove(ASTWALKER_RULE_TYPE_KEY);
			
			if (ctx.getChildCount() == 2) {
				String part = ctx.getChild(0).getText().toUpperCase();
				part = part + " " + ctx.getChild(1).getText().toUpperCase();
				subMap.put(MUMBLE_VALUE_KEY, part);
			} else  {
				// Incorrect number of entries
			}
			// Add item to parent map
			walker.addToParent(parentRuleIndex, parentStackLevel, subMap);
		}

		// item_select_function does NOT need its own exit method

		@Override
		public void exitSelect_direction( SQLSelectParserParser.Select_directionContext ctx) {
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
		public void exitNull_handling( SQLSelectParserParser.Null_handlingContext ctx) {
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
	public void exitSimple_variable_identifier( SQLSelectParserParser.Simple_variable_identifierContext ctx) {
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
			item.put(MUMBLE_NAME_KEY, ctx.getChild(0).getText());
			subMap.put(MUMBLE_SUBSTITUTION_KEY, item);
		}
		// Add item to parent map
		walker.addToParent(parentRuleIndex, parentStackLevel, subMap);
	}

	@Override
	public void exitExtended_variable_identifier( SQLSelectParserParser.Extended_variable_identifierContext ctx) {
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
	}

	@Override
	public void exitJinja_name( SQLSelectParserParser.Jinja_nameContext ctx) {
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

	
	@Override
	public void exitJinja_arg( SQLSelectParserParser.Jinja_argContext ctx) {
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
	public void exitJinja_arg_list( SQLSelectParserParser.Jinja_arg_listContext ctx) {
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
	public void exitJinja_variable_access( SQLSelectParserParser.Jinja_variable_accessContext ctx) {
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

	
	@Override
	public void exitJinja_function_call( SQLSelectParserParser.Jinja_function_callContext ctx) {
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

	
	@Override
	public void exitJinja_identifier( SQLSelectParserParser.Jinja_identifierContext ctx) {
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
	public void exitWhere_clause( SQLSelectParserParser.Where_clauseContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		int stackLevel = walker.currentStackLevel(ruleIndex);
		Map<String, Object> subMap = walker.getNodeMap(ruleIndex, stackLevel);
		symbolTreeHelper.captureClauseDependencies(subMap, MUMBLE_FILTERS_KEY);
		walker.handlePushDown(ruleIndex);
	}

	@Override
	public void exitQualify_clause( SQLSelectParserParser.Qualify_clauseContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		int stackLevel = walker.currentStackLevel(ruleIndex);
		Map<String, Object> subMap = walker.getNodeMap(ruleIndex, stackLevel);
		symbolTreeHelper.captureClauseDependencies(subMap, MUMBLE_FILTERS_KEY);
		walker.handlePushDown(ruleIndex);
	}

	@Override
	public void exitGroupby_clause( SQLSelectParserParser.Groupby_clauseContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		int stackLevel = walker.currentStackLevel(ruleIndex);
		Map<String, Object> subMap = walker.getNodeMap(ruleIndex, stackLevel);
		symbolTreeHelper.captureClauseDependencies(subMap, MUMBLE_GROUPED_BY_KEY);
		walker.handlePushDown(ruleIndex);
	}

	@Override
	public void exitHaving_clause( SQLSelectParserParser.Having_clauseContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		int stackLevel = walker.currentStackLevel(ruleIndex);

		Map<String, Object> subMap = walker.getNodeMap(ruleIndex, stackLevel);
		symbolTreeHelper.captureClauseDependencies(subMap, MUMBLE_FILTERS_KEY);
		walker.handlePushDown(ruleIndex);
//		walker.handleOneChild(ruleIndex);
	}

	@Override
	public void exitOrderby_clause( SQLSelectParserParser.Orderby_clauseContext ctx) {
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
			symbolTreeHelper.captureClauseDependencies(subMap, MUMBLE_ORDERED_BY_KEY);
			walker.handlePushDown(ruleIndex);
		}
	}


	@Override
	public void exitLimit_clause( SQLSelectParserParser.Limit_clauseContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		int parentRuleIndex = ctx.getParent().getRuleIndex();

		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Integer parentStackLevel = walker.currentStackLevel(parentRuleIndex);

		Map<String, Object> subMap = walker.getNodeMap(ruleIndex, stackLevel);
//		Object type = subMap.remove(ASTWALKER_RULE_TYPE_KEY);

		if (subMap.size() == 2) {
			Map<String, Object> left = (Map<String, Object>) subMap.remove("1");
			subMap.putAll(left);
			walker.handlePushDown(ruleIndex);
			
		} else if (subMap.size() == 3) {
			Map<String, Object> limit = (Map<String, Object>) subMap.remove("1");
			Map<String, Object>  offsetObj = (Map<String, Object> )  subMap.remove("2");
			Object offset = (Object) offsetObj.remove(MUMBLE_LITERAL_KEY);
			subMap.put(MUMBLE_OFFSET_KEY, offset);
			subMap.putAll(limit);
			walker.handlePushDown(ruleIndex);
		} else {
			// Wrong number of entries
		}
	}


	@Override
	public void exitSearch_condition( SQLSelectParserParser.Search_conditionContext ctx) {
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
		symbolTreeHelper.flattenSubTreeForClauseColumns((HashMap<String, Object>) subMap, flatList);

		// Add the flatList back into the SymbolTree as the Object part of the filter entry. Use the MUMBLE_FILTERS_KEY as the key
		walker.symbolTable.put(MUMBLE_FILTERS_KEY, flatList);

		// NOW handle the push down of the search condition as normal
		walker.handleOneChild(ruleIndex);
	}

	
	// Standardize the filters reference map into a flat map of column references and not the entire AST subtree
	// This is a recursive function that traverses the item subtree until it finds column references or substitution variables, 
	// which it adds to the column list with the alias as the key


	@Override
	public void exitOr_predicate( SQLSelectParserParser.Or_predicateContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		String key = MUMBLE_OR_KEY;

		walker.handleOperandList(ruleIndex, key);
	}

	@Override
	public void exitAnd_predicate( SQLSelectParserParser.And_predicateContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		String key = MUMBLE_AND_KEY;

		walker.handleOperandList(ruleIndex, key);
	}

	
	@Override
	public void exitNegative_predicate( SQLSelectParserParser.Negative_predicateContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Map<String, Object> subMap = walker.getNodeMap(ruleIndex, stackLevel);
		Object type = subMap.remove(ASTWALKER_RULE_TYPE_KEY);

		if (subMap.size() == 1) {
			Map<String, Object> left = (Map<String, Object>) subMap.remove("1");
			subMap.putAll(left);
		} else if (subMap.size() == 2) {
			String negation = (String) subMap.remove("1");

			Map<String, Object> left = (Map<String, Object>) subMap.remove("2");
			subMap.put(negation, left);
		} else {
			// Wrong number of entries
		}
	}

	
	@Override
	public void exitBasic_predicate_clause( SQLSelectParserParser.Basic_predicate_clauseContext ctx) {
		// {condition={left={substitution={name=<subject code>,
		// type=predicand}}, operator=is true}}
		int ruleIndex = ctx.getRuleIndex();
		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Map<String, Object> subMap = walker.getNodeMap(ruleIndex, stackLevel);
		Object type = subMap.remove(ASTWALKER_RULE_TYPE_KEY);

		if (subMap.size() == 1) {
			Map<String, Object> left = (Map<String, Object>) subMap.remove("1");
			subMap.putAll(left);
		} else if (subMap.size() == 2) {
			// Grammar peculiarity results in Substitution Variable
			// mislabelled as a condition when it should be a predicand.
			// Fixing it here
			Map<String, Object> item = (Map<String, Object>) subMap.remove("1");
			if (item.containsKey(MUMBLE_SUBSTITUTION_KEY)) {
				HashMap<String, Object> hold = (HashMap<String, Object>) item.get(MUMBLE_SUBSTITUTION_KEY);
				hold.put(MUMBLE_TYPE_KEY, MUMBLE_PREDICAND_KEY);
				walker.substitutionsMap.put((String) hold.get("name"), MUMBLE_PREDICAND_KEY);
			}
			HashMap<String, Object> hold = new HashMap<String, Object>();
			hold.put(MUMBLE_LEFT_FACTOR_KEY, item);
			subMap.put(MUMBLE_CONDITION_KEY, hold);

			item = (Map<String, Object>) subMap.remove("2");
			subMap.putAll(item);
		} else {
			// Wrong number of entries
		}
	}

	
	@Override
	public void exitSubstitution_predicate( SQLSelectParserParser.Substitution_predicateContext ctx) {
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
		} else {
			// Wrong number of entries
		}
	}

	
	@Override
	public void exitNull_predicate( SQLSelectParserParser.Null_predicateContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Map<String, Object> subMap = walker.getNodeMap(ruleIndex, stackLevel);
		Object type = subMap.remove(ASTWALKER_RULE_TYPE_KEY);

		if (subMap.size() == 2) {
			Map<String, Object> condition = new HashMap<String, Object>();
			Map<String, Object> left = (Map<String, Object>) subMap.remove("1");
			condition.put(MUMBLE_LEFT_FACTOR_KEY, left);

			condition.putAll((Map<String, Object>) subMap.remove("2"));

			subMap.put(MUMBLE_CONDITION_KEY, condition);
		} else {
			// Wrong number of entries
		}
	}

	@Override
	public void exitIs_null_clause( SQLSelectParserParser.Is_null_clauseContext ctx) {
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
	public void exitIs_clause( SQLSelectParserParser.Is_clauseContext ctx) {
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

	
	@Override
	public void exitParen_clause( SQLSelectParserParser.Paren_clauseContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Map<String, Object> subMap = walker.getNodeMap(ruleIndex, stackLevel);
		Object type = subMap.remove(ASTWALKER_RULE_TYPE_KEY);

		if (subMap.size() == 1) {
			Map<String, Object> item = (Map<String, Object>) subMap.remove("1");
			subMap.put(MUMBLE_PARENTHESES_KEY, item);
		} else {
			// Wrong number of entries
		}
	}

	
	@Override
	public void exitComparison_predicate( SQLSelectParserParser.Comparison_predicateContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Map<String, Object> subMap = walker.getNodeMap(ruleIndex, stackLevel);
		Object type = subMap.remove(ASTWALKER_RULE_TYPE_KEY);

		if (subMap.size() == 3) {
			Map<String, Object> condition = new HashMap<String, Object>();

			Object operator = subMap.remove("2");
			if (operator instanceof String)
				condition.put(MUMBLE_OPERATOR_KEY, operator);
			else
				condition.put(MUMBLE_OPERATOR_KEY, ((HashMap<String, String>) operator).get("1"));

				Map<String, Object> left = walker.checkForSubstitutionVariable((Map<String, Object>) subMap.remove("1"),
						MUMBLE_PREDICAND_KEY);
			condition.put(MUMBLE_LEFT_FACTOR_KEY, left);

				Map<String, Object> right = walker.checkForSubstitutionVariable((Map<String, Object>) subMap.remove("3"),
						MUMBLE_PREDICAND_KEY);
			condition.put(MUMBLE_RIGHT_FACTOR_KEY, right);

			subMap.put(MUMBLE_CONDITION_KEY, condition);
		} else {
			// Wrong number of entries
		}
	}

	@Override
	public void exitComparison_operator( SQLSelectParserParser.Comparison_operatorContext ctx) {
		int ruleIndex = ctx.getRuleIndex();

		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Map<String, Object> subMap = walker.getNodeMap(ruleIndex, stackLevel);
		subMap.remove(ASTWALKER_RULE_TYPE_KEY);

		if (subMap.size() == 1) {
			// Wrong number of entries
		} else if (subMap.size() == 2) {
			String notvar = (String) subMap.remove("1");
			String operator = (String) subMap.remove("2");
			subMap.put("1", notvar + '_' + operator);
		} else {
			// Wrong number of entries
		}
	}

	
	@Override
	public void exitBetween_predicate( SQLSelectParserParser.Between_predicateContext ctx) {
		// RULE_between_predicate
		int ruleIndex = ctx.getRuleIndex();
		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Map<String, Object> subMap = walker.getNodeMap(ruleIndex, stackLevel);
		Object type = subMap.remove(ASTWALKER_RULE_TYPE_KEY);

		if (subMap.size() >= 3) {
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
				operator = walker.checkForSubstitutionVariable((Map<String, Object>) operator, MUMBLE_PREDICAND_KEY);
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
				operator = walker.checkForSubstitutionVariable((Map<String, Object>) operator, MUMBLE_PREDICAND_KEY);
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
		} else {
			// Wrong number of entries
		}
	}

	@Override
	public void exitIn_predicate( SQLSelectParserParser.In_predicateContext ctx) {
		int ruleIndex = ctx.getRuleIndex();

		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Map<String, Object> subMap = walker.removeNodeMap(ruleIndex, stackLevel);
		subMap.remove(ASTWALKER_RULE_TYPE_KEY);

		if (subMap.size() == 2) {
			subMap.put(MUMBLE_ITEM_KEY, subMap.remove("1"));
			subMap.put(MUMBLE_IN_LIST_KEY, subMap.remove("2"));
		} else if (subMap.size() == 3) {
			subMap.put(MUMBLE_ITEM_KEY, subMap.remove("1"));
			subMap.remove("2");
			subMap.put(MUMBLE_NOT_IN_LIST_KEY, subMap.remove("3"));
		} else {
			// Wrong number of entries
		}

		Map<String, Object> item = new HashMap<String, Object>();
		item.put(MUMBLE_IN_KEY, subMap);

		int parentRuleIndex = ctx.getParent().getRuleIndex();
		Integer parentStackLevel = walker.currentStackLevel(parentRuleIndex);
		walker.addToParent(parentRuleIndex, parentStackLevel, item);
	}

	@Override
	public void exitLike_any_predicate( SQLSelectParserParser.Like_any_predicateContext ctx) {
		// Item 95 - add support for PostgresSQL escape character syntax in Like Any clauses
		int ruleIndex = ctx.getRuleIndex();

		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Map<String, Object> subMap = walker.removeNodeMap(ruleIndex, stackLevel);
		subMap.remove(ASTWALKER_RULE_TYPE_KEY);
		String type = new String();

		if (subMap.size() == 3) {
			// Matches the minimum mandatory entries of the rule:
			// row_value_predicand like_any_operator in_predicate_value
			subMap.put(MUMBLE_ITEM_KEY, subMap.remove("1"));// This is the row_value_predicand
			type = (String) subMap.remove("2");// This is the like_any_operator
			subMap.put(MUMBLE_LIKE_ANY_LIST_KEY, subMap.remove("3")); // This is the in_predicate_value for the LIKE ANY
		} else if (subMap.size() == 4) {
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
			subMap.put(MUMBLE_ITEM_KEY, subMap.remove("1"));// This is the row_value_predicand
			subMap.remove("2"); // This is the "not" operator, We don't need it in the AST since it's implied by the
			// MUMBLE_NOT_LIKE_ANY_LIST_KEY used to hold the list of values
			type = (String) subMap.remove("3");// The type is the like_any_operator
			subMap.put(MUMBLE_NOT_LIKE_ANY_LIST_KEY, subMap.remove("4"));// This is the in_predicate_value for the NOT LIKE ANY
			subMap.putAll((Map<String, Object>) subMap.remove("5")); // This is the escape_character_clause, pulled up
		} else {
			// Wrong number of entries
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
	public void exitLike_any_operator( SQLSelectParserParser.Like_any_operatorContext ctx) {
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
	public void enterIn_predicate_value( SQLSelectParserParser.In_predicate_valueContext ctx) {
		symbolTreeHelper.pushDependentQueryContextForFrame(ctx);
		symbolTreeHelper.pushSymbolTableWithParentVisibleScope();
	}

	@Override
	public void exitIn_predicate_value( SQLSelectParserParser.In_predicate_valueContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Map<String, Object> subMap = walker.getNodeMap(ruleIndex, stackLevel);
		Map<String, Object> reference = walker.checkForSubstitutionVariable((Map<String, Object>) subMap.get("1"), MUMBLE_IN_LIST_KEY);

		walker.handleOneChild(ruleIndex);
		symbolTreeHelper.exitPredicateSubqueryFrame(
				reference,
				walker.symbolTable,
				SqlParseSymbolTreeHelper.PredicateSubqueryMergeKind.IN);
		symbolTreeHelper.popDependentQueryContextForFrame();
	}


	/*
==============================================================================================
  8.9 <exists predicate>

  Specify a test for a non_empty set.
==============================================================================================
*/

	@Override
	public void exitExists_predicate( SQLSelectParserParser.Exists_predicateContext ctx) {
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
			// Wrong number of entries
		}

		Map<String, Object> item = new HashMap<String, Object>();
		item.put(MUMBLE_EXISTS_KEY, subMap);
		walker.addToParent(parentRuleIndex, parentStackLevel, item);
	}

	@Override
	public void exitExists_operator( SQLSelectParserParser.Exists_operatorContext ctx) {
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
	public void enterExists_predicate_value( SQLSelectParserParser.Exists_predicate_valueContext ctx) {
		symbolTreeHelper.pushDependentQueryContextForFrame(ctx);
		symbolTreeHelper.pushSymbolTableWithParentVisibleScope();
	}

	@Override
	public void exitExists_predicate_value( SQLSelectParserParser.Exists_predicate_valueContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Map<String, Object> subMap = walker.getNodeMap(ruleIndex, stackLevel);
		Map<String, Object> reference = walker.checkForSubstitutionVariable((Map<String, Object>) subMap.get("1"), MUMBLE_TUPLE_KEY);

		walker.handleOneChild(ruleIndex);
		symbolTreeHelper.exitPredicateSubqueryFrame(
				reference,
				walker.symbolTable,
				SqlParseSymbolTreeHelper.PredicateSubqueryMergeKind.EXISTS);
		symbolTreeHelper.popDependentQueryContextForFrame();
	}

	@Override
	public void enterPredicand_subquery( SQLSelectParserParser.Predicand_subqueryContext ctx) {
		symbolTreeHelper.pushDependentQueryContextForFrame(ctx);
		symbolTreeHelper.pushSymbolTableWithParentVisibleScope();
	}

	@Override
	public void exitPredicand_subquery( SQLSelectParserParser.Predicand_subqueryContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Map<String, Object> subMap = walker.getNodeMap(ruleIndex, stackLevel);
		Map<String, Object> reference = walker.checkForSubstitutionVariable((Map<String, Object>) subMap.get("1"), MUMBLE_TUPLE_KEY);

		walker.handleOneChild(ruleIndex);
		symbolTreeHelper.exitPredicateSubqueryFrame(
				reference,
				walker.symbolTable,
				SqlParseSymbolTreeHelper.PredicateSubqueryMergeKind.PREDICAND);
		symbolTreeHelper.popDependentQueryContextForFrame();
	}

	@Override
	public void exitIn_value_list( SQLSelectParserParser.In_value_listContext ctx) {
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
	public void exitEscape_character_clause( SQLSelectParserParser.Escape_character_clauseContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		int parentRuleIndex = ctx.getParent().getRuleIndex();

		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Integer parentStackLevel = walker.currentStackLevel(parentRuleIndex);

		Map<String, Object> subMap = walker.removeNodeMap(ruleIndex, stackLevel);

		subMap = walker.makeRuleMap(ruleIndex);
		subMap.remove(ASTWALKER_RULE_TYPE_KEY);
		
		if (ctx.getChildCount() == 2) {
			String escape_key_word = ctx.getChild(0).getText().toUpperCase();
			String escape_string = ctx.getChild(1).getText();
			subMap.put(MUMBLE_ESCAPE_KEY, escape_string);
		} else  {
			// Wrong number of entries
		}
		// Add item to parent map
		walker.addToParent(parentRuleIndex, parentStackLevel, subMap);
	}

	@Override
	public void exitFactor( SQLSelectParserParser.FactorContext ctx) {
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
		walker.handleOneChild(ruleIndex);
	}
	
	@Override
	public void exitRow_value_predicand_list( SQLSelectParserParser.Row_value_predicand_listContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		walker.handleOneChild(ruleIndex);
	}

	@Override
	public void exitNumeric_primary( SQLSelectParserParser.Numeric_primaryContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		walker.handleOneChild(ruleIndex);
	}

	@Override
	public void exitCommon_value_expression( SQLSelectParserParser.Common_value_expressionContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		walker.handleOneChild(ruleIndex);
	}

	@Override
	public void exitAdditive_expression( SQLSelectParserParser.Additive_expressionContext ctx) {
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
			// Wrong number of entries
		}
		walker.collect(ruleIndex, stackLevel, subMap);
	}

	@Override
	public void exitMultiplicative_expression( SQLSelectParserParser.Multiplicative_expressionContext ctx) {
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
			// Wrong number of entries
		}
		walker.collect(ruleIndex, stackLevel, subMap);
	}

	@Override
	public void exitBoolean_value_expression( SQLSelectParserParser.Boolean_value_expressionContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		walker.handleOneChild(ruleIndex);
	}

	@Override
	public void exitBoolean_primary( SQLSelectParserParser.Boolean_primaryContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		walker.handleOneChild(ruleIndex);
	}

	@Override
	public void exitPredicate( SQLSelectParserParser.PredicateContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		walker.handleOneChild(ruleIndex);
	}

	@Override
	public void exitString_value_expression( SQLSelectParserParser.String_value_expressionContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		walker.handleOperandList(ruleIndex, MUMBLE_CONCATENATE_KEY);
	}

	@Override
	public void exitCharacter_primary( SQLSelectParserParser.Character_primaryContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		int parentRuleIndex = ctx.getParent().getRuleIndex();

		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Integer parentStackLevel = walker.currentStackLevel(parentRuleIndex);

		Map<String, Object> subMap = walker.removeNodeMap(ruleIndex, stackLevel);
		subMap.remove(ASTWALKER_RULE_TYPE_KEY);

		if (subMap.size() == 1) {
			walker.addToParent(parentRuleIndex, parentStackLevel, subMap.remove("1"));
		}
	}

	@Override
	public void exitTrim_function( SQLSelectParserParser.Trim_functionContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Map<String, Object> subMap = walker.getNodeMap(ruleIndex, stackLevel);
		Object type = subMap.remove(ASTWALKER_RULE_TYPE_KEY);

		Map<String, Object> item = new HashMap<String, Object>();

		if (subMap.size() == 2) {
			item.put(MUMBLE_FUNCTION_NAME_KEY, subMap.remove("1"));
			item.put(MUMBLE_PARAMETERS_KEY, subMap.remove("2"));
			subMap.put(MUMBLE_FUNCTION_KEY, item);
		} else if (subMap.size() == 3) {
			// TRIM with inline cast suffix: TRIM(... FROM col)::type
			item.put(MUMBLE_FUNCTION_NAME_KEY, subMap.remove("1"));
			item.put(MUMBLE_PARAMETERS_KEY, subMap.remove("2"));
			Object castType = subMap.remove("3");
			Map<String, Object> trimNode = new HashMap<String, Object>();
			trimNode.put(MUMBLE_FUNCTION_KEY, item);
			Map<String, Object> castItem = new HashMap<String, Object>();
			castItem.put(MUMBLE_FUNCTION_NAME_KEY, MUMBLE_CAST_FUNCTION_NAME);
			castItem.put(MUMBLE_TYPE_KEY, MUMBLE_CAST_FUNCTION_NAME.toUpperCase(Locale.ROOT));
			castItem.put(MUMBLE_VALUE_KEY, trimNode);
			castItem.put(MUMBLE_DATATYPE_KEY, castType);
			subMap.clear();
			subMap.put(MUMBLE_FUNCTION_KEY, castItem);
		} else {
			// Wrong number of entries
		}
	}

	@Override
	public void exitMysql_trim_operands( SQLSelectParserParser.Mysql_trim_operandsContext ctx) {
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
		walker.addToParent(parentRuleIndex, parentStackLevel, item);
	}

	@Override
	public void exitOther_trim_operands( SQLSelectParserParser.Other_trim_operandsContext ctx) {
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
		walker.addToParent(parentRuleIndex, parentStackLevel, item);
	}


	@Override
	public void exitPosition_function( SQLSelectParserParser.Position_functionContext ctx) {
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
			// Wrong number of entries
		}
	}

	
	@Override
	public void exitRoutine_invocation( SQLSelectParserParser.Routine_invocationContext ctx) {
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
			// Wrong number of entries
		}
		walker.addToParent(parentRuleIndex, parentStackLevel, subMap);
	}

	@Override
	public void exitFunction_name( SQLSelectParserParser.Function_nameContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Map<String, Object> subMap = walker.getNodeMap(ruleIndex, stackLevel);
		Object type = subMap.remove(ASTWALKER_RULE_TYPE_KEY);

		if (subMap.size() == 1) {
			String functName = (String) subMap.remove("1");
			subMap.put(MUMBLE_FUNCTION_NAME_KEY, functName);
		} else if (subMap.size() == 2) {
			String schema = (String) subMap.remove("1");
			subMap.put(MUMBLE_SCHEMA_KEY, schema);
			String functName = (String) subMap.remove("2");
			subMap.put(MUMBLE_FUNCTION_NAME_KEY, functName);
		} else {
			// Wrong number of entries
		}
	}

	@Override
	public void exitSql_argument_list( SQLSelectParserParser.Sql_argument_listContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		int stackLevel = walker.currentStackLevel(ruleIndex);
		Map<String, Object> subMap = walker.getNodeMap(ruleIndex, stackLevel);
		walker.handlePushDown(ruleIndex);
	}

	@Override
	public void exitValue_expression( SQLSelectParserParser.Value_expressionContext ctx) {
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
				// Get first item, record if it is a Substitution Variable by
				// adding the Substitution List
				valueExpression = walker.checkForSubstitutionVariable((Map<String, Object>) subMap.remove("1"), MUMBLE_PREDICAND_KEY);

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
				subMap = walker.checkForSubstitutionVariable((Map<String, Object>) subMap, MUMBLE_PREDICAND_KEY);

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
				subMap = walker.checkForSubstitutionVariable((Map<String, Object>) subMap, MUMBLE_PREDICAND_KEY);

			// NOW handle the child
			walker.handleOneChild(ruleIndex);
			//
		} else {
			// then parent is any non-list parent
			walker.handleOneChild(ruleIndex);
		}

	}

	@Override
	public void exitRow_value_expression( SQLSelectParserParser.Row_value_expressionContext ctx) {
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
	public void exitSort_specifier_list( SQLSelectParserParser.Sort_specifier_listContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		int parentRuleIndex = ctx.getParent().getRuleIndex();
		walker.handleListList(ruleIndex, parentRuleIndex);
	}

	@Override
	public void exitSort_specifier( SQLSelectParserParser.Sort_specifierContext ctx) {
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
			} else if (item3 != null) {
				item.put(MUMBLE_SORT_ORDER_KEY, item2);
				Map<String, Object> hold = (Map<String, Object>) item3;
				type = hold.remove(ASTWALKER_RULE_TYPE_KEY).toString();
				item.put(MUMBLE_NULL_ORDER_KEY, ((HashMap<String, Object>) hold.get(type)).get("1"));

			} else { // Item 2 is not null and Item 3 is null :- Item 2 could be ASC/DESC or Nulls command
				if (item2 instanceof Map<?,?>) {
					// item2 is the null order value
					item.put(MUMBLE_SORT_ORDER_KEY, "ASC");
					Map<String, Object> hold = (Map<String, Object>) item2;
					type = hold.remove(ASTWALKER_RULE_TYPE_KEY).toString();
					item.put(MUMBLE_NULL_ORDER_KEY, ((HashMap<String, Object>) hold.get(type)).get("1"));
				} else {
					//item2 is the ASC/DESC value
					item.put(MUMBLE_SORT_ORDER_KEY, item2);
					item.put(MUMBLE_NULL_ORDER_KEY, null);
				}
			}
		}
		else {
			//Too many entries
		}

		subMap.put("1", item);

		int parentRuleIndex = ctx.getParent().getRuleIndex();
		walker.handleListItem(ruleIndex, parentRuleIndex);

	}

	@Override
	public void exitNull_ordering( SQLSelectParserParser.Null_orderingContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		walker.handlePushDown(ruleIndex);
	}

	@Override
	public void exitGrouping_element_list( SQLSelectParserParser.Grouping_element_listContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		int parentRuleIndex = ctx.getParent().getRuleIndex();
		walker.handleListList(ruleIndex, parentRuleIndex);
	}

	@Override
	public void exitGrouping_element( SQLSelectParserParser.Grouping_elementContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		int parentRuleIndex = ctx.getParent().getRuleIndex();
		walker.handleListItem(ruleIndex, parentRuleIndex);
	}

	@Override
	public void exitOrdinary_grouping_set_list( SQLSelectParserParser.Ordinary_grouping_set_listContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		walker.handleOneChild(ruleIndex);
	}

	@Override
	public void exitOrdinary_grouping_set( SQLSelectParserParser.Ordinary_grouping_setContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		Integer parentRuleIndex = (Integer) ctx.getParent().getRuleIndex();
		if (parentRuleIndex.equals((Integer) SQLSelectParserParser.RULE_grouping_element))
			walker.handleOneChild(ruleIndex);
		else if (parentRuleIndex.equals((Integer) SQLSelectParserParser.RULE_ordinary_grouping_set_list))
			walker.handleListItem(ruleIndex, parentRuleIndex);
	}

	@Override
	public void exitRow_value_predicand( SQLSelectParserParser.Row_value_predicandContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		int parentRuleIndex = ctx.getParent().getRuleIndex();
		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Map<String, Object> subMap = walker.getNodeMap(ruleIndex, stackLevel);
		// Get first item, record if it is a Substitution Variable by
		// adding the Substitution List
		Map<String, Object> substitutionPredicand = walker.checkForSubstitutionVariable((Map<String, Object>) subMap.get("1"),
				MUMBLE_PREDICAND_KEY);

		walker.handleOneChild(ruleIndex);
	}

	@Override
	public void exitGeneral_literal( SQLSelectParserParser.General_literalContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		walker.handleOneChild(ruleIndex);
	}

	@Override
	public void exitUnsigned_literal( SQLSelectParserParser.Unsigned_literalContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Map<String, Object> subMap = walker.getNodeMap(ruleIndex, stackLevel);
		subMap.remove(ASTWALKER_RULE_TYPE_KEY);
		String[] keys = new String[1];
		keys = subMap.keySet().toArray(keys);

		if (keys.length == 1) {
			Object item = subMap.remove(keys[0]);
			subMap.put(MUMBLE_LITERAL_KEY, item);
		} else {
			// Too many entries
		}
	}

	@Override
	public void exitReal_number( SQLSelectParserParser.Real_numberContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		walker.handleOneChild(ruleIndex);
	}

	@Override
	public void exitReal_number_def( SQLSelectParserParser.Real_number_defContext ctx) {
		// Tell master exit that the full text is the value
		walker.useAsLeaf = true;
	}

	@Override
	public void exitExponent( SQLSelectParserParser.ExponentContext ctx) {
		// Tell master exit that the full text is the value
		walker.useAsLeaf = true;
	}

	@Override
	public void exitDatetime_literal( SQLSelectParserParser.Datetime_literalContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		walker.handleOneChild(ruleIndex);
	}

	@Override
	public void exitIdentifier( SQLSelectParserParser.IdentifierContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		walker.handleOneChild(ruleIndex);
	}

	@Override
	public void exitAlias_identifier( SQLSelectParserParser.Alias_identifierContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		walker.handleOneChild(ruleIndex);
	}

	@Override
	public void exitVariable_identifier( SQLSelectParserParser.Variable_identifierContext ctx) {
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
	public void exitNull_literal( SQLSelectParserParser.Null_literalContext ctx) {
		HashMap<String, Object> item = new HashMap<String, Object>();
		item.put(MUMBLE_NULL_LITERAL_KEY, "null");

		int parentRuleIndex = ctx.getParent().getRuleIndex();
		Integer parentStackLevel = walker.currentStackLevel(parentRuleIndex);

		walker.addToParent(parentRuleIndex, parentStackLevel, item);
	}

	@Override
	public void exitPuml_constant_identifier( SQLSelectParserParser.Puml_constant_identifierContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		int parentRuleIndex = ctx.getParent().getRuleIndex();

		Integer stackLevel = walker.currentStackLevel(ruleIndex);
		Integer parentStackLevel = walker.currentStackLevel(parentRuleIndex);

		Map<String, Object> subMap = walker.removeNodeMap(ruleIndex, stackLevel);

		subMap = walker.makeRuleMap(ruleIndex);
		subMap.remove(ASTWALKER_RULE_TYPE_KEY);
		
		if (ctx.getChildCount() == 1) {
			String part = ctx.getChild(0).getText().toUpperCase();
			subMap.put(MUMBLE_PUML_CONSTANT_KEY, part);
		}
		// Add item to parent map
		walker.addToParent(parentRuleIndex, parentStackLevel, subMap);
	}

}
