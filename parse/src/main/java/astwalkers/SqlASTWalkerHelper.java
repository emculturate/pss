package astwalkers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;

import static mumble.MumbleConstants.*;

import static mumble.ASTWalkerHelperConstants.*;

import sql.SQLSelectParserParser;

public final class SqlASTWalkerHelper extends AbstractASTWalkerHelper {
		public static final String DIAG_SQL_QUALIFIED_COLUMN_NOT_FOUND_IN_TABLE = "SQL_QUALIFIED_COLUMN_NOT_FOUND_IN_TABLE";
		public static final String DIAG_SQL_QUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIAS = "SQL_QUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIAS";
		public static final String DIAG_SQL_AMBIGUOUS_COLUMN_REFERENCE = "SQL_AMBIGUOUS_COLUMN_REFERENCE";
		public static final String DIAG_SQL_SHADOWED_PARENT_CTE_NAME = "SQL_SHADOWED_PARENT_CTE_NAME";
		public static final String DIAG_SQL_UNRESOLVED_UNQUALIFIED_COLUMNS = "SQL_UNRESOLVED_UNQUALIFIED_COLUMNS";
		public static final String DIAG_SQL_UNQUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIASES = "SQL_UNQUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIASES";
		public static final String DIAG_SQL_DUPLICATE_INTERFACE_COLUMNS = "SQL_DUPLICATE_INTERFACE_COLUMNS";
		public static final String DIAG_SQL_SET_OPERATION_INTERFACE_COLUMN_COUNT_MISMATCH = "SQL_SET_OPERATION_INTERFACE_COLUMN_COUNT_MISMATCH";
		public static final String DIAG_SQL_INSERT_TARGET_SOURCE_COLUMN_COUNT_MISMATCH = "SQL_INSERT_TARGET_SOURCE_COLUMN_COUNT_MISMATCH";
		public static final String DIAG_SQL_INTO_ONLY_ALLOWED_ON_FIRST_SET_MEMBER = "SQL_INTO_ONLY_ALLOWED_ON_FIRST_SET_MEMBER";
		public static final String DIAG_SQL_PIVOT_IN_IDENTIFIER_REFERENCE = "SQL_PIVOT_IN_IDENTIFIER_REFERENCE";
		public static final String DIAG_SQL_PIVOT_IN_IDENTIFIER_UNRESOLVED = "SQL_PIVOT_IN_IDENTIFIER_UNRESOLVED";
		public static final String TEMP_SET_OPERATION_INTERFACE_SUMMARY_MAP_KEY = "_tmp_set_operation_interface_summary_map";
		public static final String TEMP_QUERY_SET_OPERATION_SUMMARY_KEYS_MAP_KEY = "_tmp_query_set_operation_summary_keys_map";
		public static final String TEMP_SET_OPERATION_OPERATOR_ANCHOR_LINE_KEY = "_tmp_set_operation_operator_anchor_line";
		public static final String TEMP_SET_OPERATION_OPERATOR_ANCHOR_CHAR_KEY = "_tmp_set_operation_operator_anchor_char";
		/** Staged on a unionized_query frame; consumed when the next leaf participant in that union is published. */
		public static final String TEMP_PENDING_UNION_SETOP_FOR_NEXT_PARTICIPANT_KEY =
				"_tmp_pending_union_setop_for_next_participant";
		/** Staged on an intersected_query frame; consumed when the next unionized_query output is published. */
		public static final String TEMP_PENDING_INTERSECT_SETOP_FOR_NEXT_PARTICIPANT_KEY =
				"_tmp_pending_intersect_setop_for_next_participant";

		
    /*************************************
     * SqlASTWalkerHelper is a concrete class that extends AbstractASTWalkerHelper.
     * It provides specific implementations/overrides for any Grammar
     * that needs a full set of objects to be constructed. 
     * 
     * These objects include:
     * * - Abstract Syntax Tree: Nested Map representing a statement
     * * - Table Dictionary Map: Nested Map representing the table dictionary
     * * - Query Dictionary Map: Nested Map representing the table dictionary
     * * - Symbol Table: Nested Map representing the symbol table
     * * - Substitution Variables: Nested Map representing the substitution variables
     * * - Query Interface: Nested Map representing the query interface
     *
     * ***********************************/     


	/**
	 * Collect Root Table Column Dictionary
	 * Contains the input columns from the external TABLE sources that the query pulls from.
	 */
	public HashMap<String, Object> tableDictionaryMap;

	/**
	 * Collect Root Query Column Dictionary
	 * Contains the query context's input columns from the nested subqueries in the FROM-JOIN clauses
	 */
	public HashMap<String, Object> queryColumnDictionaryMap;

	/**
	 * Collect Nested Symbol Table for the query
	 */
	public HashMap<String, Object> symbolTable;

	/**
	 * Collect Substitution Variable List
	 */
	public HashMap<String, Object> substitutionsMap;

	/**
	 * Accumulates qualified unresolved entries (with locations) across nested scopes
	 * so final diagnostics can render merged source positions.
	 */
	public HashMap<String, Object> globalQualifiedUnresolvedLocations;

	/**
	 * Tracks qualified unresolved keys already diagnosed with QUALIFIED_COLUMN_NOT_FOUND_IN_TABLE
	 * during the current statement to avoid duplicate fatal emissions from multiple finalize paths.
	 */
	public HashSet<String> emittedQualifiedSourceNotFoundKeys;


/**
 * Substitution Map to allow Grammars to define their own AST Tree Labels for certain common situations
 */

 protected HashMap<String, String> astKeyCrosswalkMap = new HashMap<String, String>();

 protected void initializeAstKeyCrosswalkMap() {
     // Initialize the crosswalk map with common substitutions
        astKeyCrosswalkMap.put(ASTWALKER_QUERY_KEY, ASTWALKER_QUERY_KEY);
        astKeyCrosswalkMap.put(ASTWALKER_INTERSECT_KEY, ASTWALKER_INTERSECT_KEY);
        astKeyCrosswalkMap.put(ASTWALKER_UNION_KEY, ASTWALKER_UNION_KEY);
        astKeyCrosswalkMap.put(ASTWALKER_SUBSTITUTION_KEY, ASTWALKER_SUBSTITUTION_KEY);
        astKeyCrosswalkMap.put(ASTWALKER_TYPE_KEY, ASTWALKER_TYPE_KEY);
        astKeyCrosswalkMap.put(ASTWALKER_COLUMN_KEY, ASTWALKER_COLUMN_KEY);
        astKeyCrosswalkMap.put(ASTWALKER_UNKNOWN_KEY, ASTWALKER_UNKNOWN_KEY);
        astKeyCrosswalkMap.put(ASTWALKER_VALUES_KEY, ASTWALKER_VALUES_KEY);
        astKeyCrosswalkMap.put(ASTWALKER_INSERT_KEY, ASTWALKER_INSERT_KEY);
        astKeyCrosswalkMap.put(ASTWALKER_UPDATE_KEY, ASTWALKER_UPDATE_KEY);

 }

    /**
     * Override AST Key Crosswalk Map Entry
     */
    public void overrideAstKeyCrosswalkMap(String key, String value) {
        if (key != null && value != null) {
            // Override the existing key-value pair in the crosswalk map
            if (astKeyCrosswalkMap.containsKey(key)) {
                astKeyCrosswalkMap.replace(key, value);
            } else {
                throw new IllegalArgumentException("Key to be substituted does not exist in the crosswalk map: " + key);
            }
        } else {
            throw new IllegalArgumentException("Key and value must not be null");
        }
    }

    /**
     * Get AST Key Crosswalk Map Entry
     */
    private String getASTWALKER_QUERY_KEY() {
        return astKeyCrosswalkMap.get(ASTWALKER_QUERY_KEY);
    }
    private String getASTWALKER_INTERSECT_KEY() {
        return astKeyCrosswalkMap.get(ASTWALKER_INTERSECT_KEY);
    }
    private String getASTWALKER_UNION_KEY() {
        return astKeyCrosswalkMap.get(ASTWALKER_UNION_KEY);
    }
    private String getASTWALKER_SUBSTITUTION_KEY() {
        return astKeyCrosswalkMap.get(ASTWALKER_SUBSTITUTION_KEY);
    }
    private String getASTWALKER_TYPE_KEY() {
        return astKeyCrosswalkMap.get(ASTWALKER_TYPE_KEY);
    }
    private String getASTWALKER_COLUMN_KEY() {
        return astKeyCrosswalkMap.get(ASTWALKER_COLUMN_KEY);
    }
    private String getASTWALKER_UNKNOWN_KEY() {
        return astKeyCrosswalkMap.get(ASTWALKER_UNKNOWN_KEY);
    }
    private String getASTWALKER_VALUES_KEY() {
        return astKeyCrosswalkMap.get(ASTWALKER_VALUES_KEY);
    }
    private String getASTWALKER_INSERT_KEY() {
        return astKeyCrosswalkMap.get(ASTWALKER_INSERT_KEY);
    }
    private String getASTWALKER_UPDATE_KEY() {
        return astKeyCrosswalkMap.get(ASTWALKER_UPDATE_KEY);
    }


     public SqlASTWalkerHelper() {
         super();
            // Initialize the remaining objects
         tableDictionaryMap = new HashMap<String, Object>();
		 queryColumnDictionaryMap = new HashMap<String, Object>();
         symbolTable = new HashMap<String, Object>();
         substitutionsMap = new HashMap<String, Object>();
		 globalQualifiedUnresolvedLocations = new HashMap<String, Object>();
		 emittedQualifiedSourceNotFoundKeys = new HashSet<String>();
         initializeAstKeyCrosswalkMap();
		 initializeSqlDiagnosticCatalog();

     }  

	 private void initializeSqlDiagnosticCatalog() {
		 // SQL-local extension: register walker-specific diagnostic code/message template.
		 registerDiagnostic(DIAG_SQL_UNRESOLVED_UNQUALIFIED_COLUMNS,
				 "UNRESOLVED_UNQUALIFIED_COLUMNS",
				 "Unresolved unqualified column reference(s): %s");
		 registerDiagnostic(DIAG_SQL_UNQUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIASES,
				 "UNQUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIASES",
				 "Unqualified column '%s' at (l:%s c:%s) was not found in output interface of any visible query alias %s.");
		 registerDiagnostic(DIAG_SQL_QUALIFIED_COLUMN_NOT_FOUND_IN_TABLE,
				 "QUALIFIED_COLUMN_NOT_FOUND_IN_TABLE",
				 "Source Table not found for Column '%s' at (l:%s c:%s). No alias or table called '%s'.");
		 registerDiagnostic(DIAG_SQL_QUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIAS,
				 "QUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIAS",
				 "Qualified column '%s' at (l:%s c:%s) was not found in output interface of query alias '%s'.");
		 registerDiagnostic(
				 DIAG_SQL_AMBIGUOUS_COLUMN_REFERENCE,
				 "AMBIGUOUS_COLUMN_REFERENCE",
				 "Ambiguous column reference '%s' at (l:%s c:%s). Possible sources: %s");
		 registerDiagnostic(
				 DIAG_SQL_SHADOWED_PARENT_CTE_NAME,
				 "SHADOWED_PARENT_CTE_NAME",
				 "CTE '%s' at (l:%s c:%s) shadows inherited CTE '%s' (%s).");
		 registerDiagnostic(
				 DIAG_SQL_DUPLICATE_INTERFACE_COLUMNS,
				 "DUPLICATE_INTERFACE_COLUMNS",
				 "Duplicate interface columns defined: %s at (l:%s c:%s) and %s at (l:%s c:%s).");
		 registerDiagnostic(
				 DIAG_SQL_SET_OPERATION_INTERFACE_COLUMN_COUNT_MISMATCH,
				 "SET_OPERATION_INTERFACE_COLUMN_COUNT_MISMATCH",
				 "%s has different column counts. Expected %s columns (%s) at (l:%s c:%s) but there were %s (%s) at (l:%s c:%s).");
		 registerDiagnostic(
				 DIAG_SQL_INSERT_TARGET_SOURCE_COLUMN_COUNT_MISMATCH,
				 "INSERT_TARGET_SOURCE_COLUMN_COUNT_MISMATCH",
				 "Insert Mismatch: Target has %d columns, Source has %d columns, (l:%d c:%d)");
		 registerDiagnostic(
				 DIAG_SQL_INTO_ONLY_ALLOWED_ON_FIRST_SET_MEMBER,
				 "INTO_ONLY_ALLOWED_ON_FIRST_SET_MEMBER",
				 "%s member %s contains INTO. INTO is allowed only in the first SELECT of a set operation.");
		 registerDiagnostic(
				 DIAG_SQL_PIVOT_IN_IDENTIFIER_REFERENCE,
				 "PIVOT_IN_IDENTIFIER_REFERENCE",
				 "PIVOT IN identifier \"%s\" at (l:%s c:%s) is interpreted as a column reference.");
		 registerDiagnostic(
				 DIAG_SQL_PIVOT_IN_IDENTIFIER_UNRESOLVED,
				 "PIVOT_IN_IDENTIFIER_UNRESOLVED",
				 "PIVOT IN identifier \"%s\" at (l:%s c:%s) cannot be resolved against the PIVOT source. Identifier-form PIVOT IN values are only supported when the PIVOT source is a subquery exposing that column or a wildcard interface.");
	 }

	@SuppressWarnings("unchecked")
	public HashMap<String, Object> getCurrentTableDictionary() {
		Object tableDictionaryObject = symbolTable.get(MUMBLE_TABLE_DICTIONARY_KEY);
		if (!(tableDictionaryObject instanceof HashMap<?, ?>)) {
			tableDictionaryObject = new HashMap<String, Object>();
			symbolTable.put(MUMBLE_TABLE_DICTIONARY_KEY, tableDictionaryObject);
		}
		return (HashMap<String, Object>) tableDictionaryObject;
	}

	@SuppressWarnings("unchecked")
	public HashMap<String, Object> peekCurrentTableDictionary() {
		Object tableDictionaryObject = symbolTable.get(MUMBLE_TABLE_DICTIONARY_KEY);
		if (tableDictionaryObject instanceof HashMap<?, ?>) {
			return (HashMap<String, Object>) tableDictionaryObject;
		}
		return new HashMap<String, Object>();
	}

	public HashMap<String, Object> getWalkerTableDictionary() {
		if (tableDictionaryMap == null) {
			tableDictionaryMap = new HashMap<String, Object>();
		}
		return tableDictionaryMap;
	}

	/**
	 * Clears per-statement collectors so SCRIPT statements do not leak counters,
	 * dictionaries, substitutions, or unresolved-location accumulators into each other.
	 */
	public void resetPerStatementScope() {
		queryCount = 0;
		predicandCount = 0;
		getWalkerTableDictionary().clear();
		queryColumnDictionaryMap.clear();
		substitutionsMap.clear();
		globalQualifiedUnresolvedLocations.clear();
		emittedQualifiedSourceNotFoundKeys.clear();
	}

	public boolean hasEmittedQualifiedSourceNotFoundFatal(String unresolvedQualifiedKey) {
		if (unresolvedQualifiedKey == null
				|| unresolvedQualifiedKey.isBlank()
				|| emittedQualifiedSourceNotFoundKeys == null) {
			return false;
		}
		return emittedQualifiedSourceNotFoundKeys.contains(unresolvedQualifiedKey);
	}

	public void markEmittedQualifiedSourceNotFoundFatal(String unresolvedQualifiedKey) {
		if (unresolvedQualifiedKey == null || unresolvedQualifiedKey.isBlank()) {
			return;
		}
		if (emittedQualifiedSourceNotFoundKeys == null) {
			emittedQualifiedSourceNotFoundKeys = new HashSet<String>();
		}
		emittedQualifiedSourceNotFoundKeys.add(unresolvedQualifiedKey);
	}

	public Integer findNearestSubqueryParentRuleIndex(ParserRuleContext ctx) {
		if (ctx == null) {
			return null;
		}

		ParserRuleContext ancestor = ctx;
		while (ancestor != null) {
			if (ancestor instanceof SQLSelectParserParser.SubqueryContext) {
				ParserRuleContext parent = ancestor.getParent();
				return parent == null ? null : parent.getRuleIndex();
			}
			ancestor = ancestor.getParent();
		}

		return null;
	}

	public boolean shouldPassUpQualifiedUnresolvedForSubqueryParent(Integer subqueryParentRuleIndex) {
		if (subqueryParentRuleIndex == null) {
			return false;
		}

		return subqueryParentRuleIndex == SQLSelectParserParser.RULE_nonparenthesized_value_expression_primary
				|| subqueryParentRuleIndex == SQLSelectParserParser.RULE_predicand_subquery
				|| subqueryParentRuleIndex == SQLSelectParserParser.RULE_in_predicate_value
				|| subqueryParentRuleIndex == SQLSelectParserParser.RULE_exists_predicate_value;
	}

	public boolean shouldEmitQualifiedUnresolvedForSubqueryParent(Integer subqueryParentRuleIndex) {
		if (subqueryParentRuleIndex == null) {
			return false;
		}

		return subqueryParentRuleIndex == SQLSelectParserParser.RULE_query_primary
				|| subqueryParentRuleIndex == SQLSelectParserParser.RULE_tuple_primary;
	}

	/**
	 * Merge a query-local table dictionary into the walker's global table dictionary map.
	 *
	 * Rules:
	 * - New table keys are copied in full.
	 * - Existing table keys merge column-by-column.
	 * - Existing column entries are never replaced.
	 * - If both existing and incoming column entries are reference arrays, append only
	 *   references not already captured.
	 */
	@SuppressWarnings("unchecked")
	public void mergeTableDictionaryIntoWalkerTableDictionary(HashMap<String, Object> localTableDictionary) {
		if (localTableDictionary == null || localTableDictionary.isEmpty()) {
			return;
		}
		HashMap<String, Object> globalTableDictionary = getWalkerTableDictionary();

		for (Map.Entry<String, Object> tableEntry : localTableDictionary.entrySet()) {
			String tableRef = tableEntry.getKey();
			if (!isTupleSubstitutionReference(tableRef) && tableRef != null && tableRef.startsWith("<")) {
				continue;
			}
			Object incomingColumnsObject = tableEntry.getValue();
			if (!(incomingColumnsObject instanceof Map<?, ?> incomingColumns)) {
				continue;
			}

			String normalizedTableRef = normalizeTableReference(tableRef);
			Object existingColumnsObject = globalTableDictionary.get(normalizedTableRef);

			if (!(existingColumnsObject instanceof HashMap<?, ?>)) {
				HashMap<String, Object> newColumns = new HashMap<String, Object>();
				for (Map.Entry<?, ?> columnEntry : incomingColumns.entrySet()) {
					if (columnEntry.getKey() instanceof String columnName) {
						newColumns.put(columnName, columnEntry.getValue());
					}
				}
				globalTableDictionary.put(normalizedTableRef, newColumns);
				continue;
			}

			HashMap<String, Object> existingColumns = (HashMap<String, Object>) existingColumnsObject;
			for (Map.Entry<?, ?> columnEntry : incomingColumns.entrySet()) {
				if (!(columnEntry.getKey() instanceof String columnName)) {
					continue;
				}

				Object incomingRefs = columnEntry.getValue();
				Object existingRefs = existingColumns.get(columnName);
				if (existingRefs == null) {
					existingColumns.put(columnName, incomingRefs);
				} else if (existingRefs instanceof ArrayList<?> existingRefList
						&& incomingRefs instanceof ArrayList<?> incomingRefList) {
					for (Object incomingRef : incomingRefList) {
						if (!((ArrayList<Object>) existingRefList).contains(incomingRef)) {
							((ArrayList<Object>) existingRefList).add(incomingRef);
						}
					}
				}
			}
		}
	}

	public static String normalizeTableReference(String tableRef) {
		if (tableRef == null || tableRef.isBlank()) {
			return tableRef;
		}
		if (tableRef.startsWith("<")) {
			return tableRef;
		}
		if (!tableRef.contains("\"")) {
			return tableRef.toLowerCase();
		}

		String[] parts = tableRef.split("\\.");
		StringBuilder normalized = new StringBuilder();
		for (int i = 0; i < parts.length; i++) {
			if (i > 0) {
				normalized.append('.');
			}
			String part = parts[i];
			normalized.append(part.startsWith("\"") ? part : part.toLowerCase());
		}
		return normalized.toString();
	}

	public void ensureTableDictionaryEntry(String tableRef) {
		if (tableRef == null) {
			return;
		}
		String reference;
		reference = normalizeTableReference(tableRef);
		HashMap<String, Object> tableDictionary = getCurrentTableDictionary();
		Object existing = tableDictionary.get(reference);
		if (!(existing instanceof Map<?, ?>)) {
			tableDictionary.put(reference, new HashMap<String, Object>());
		}
	}

	public boolean isTopLevelSymbolScope() {
		Integer symbolScopeLevel = stackSymbols.get("symbolTable");
		return symbolScopeLevel == null || symbolScopeLevel == 1;
	}

 
	/**
	 * Number of query and subqueries encountered
	 * The counter helps maintain unique identifiers in the AST for nested SQL structures.
	 * This counter is crucial for:
	 *  1. Unique Query Identification: Creates sequential IDs (query0, query1, etc.) for 
	 *     different queries and subqueries and for various clauses like UNION and INTERSECT.
	 *  2. Symbol Table Management: Ensures that each query has its own symbol table scope
	 * 	    and interface definition
	 *  3. Result Set Interface: Helps define the result set interface for each query,
	 * 	   even when queries are nested
	 *  4. AST Integrity: Maintains the integrity of the abstract syntax tree by ensuring
	 * 	   that each query and subquery is uniquely identifiable
	 *  5. Query Referencing: Allows symbol tables and interfaces from different queries to 
	 *     be correctly referenced when building the AST. This counter enables the walker to 
	 *     distinguish between different query blocks and maintain proper scoping 
	 *     relationships in the resulting AST.
	 */
	public Integer queryCount = 0;

	/**
	 * Number of predicands without aliases encountered
	 * This counter in SqlParseEventWalker tracks the number of predicands without aliases 
	 * encountered during SQL parsing. This counter serves a specific purpose. 
	 * When a SQL statement contains expressions that don't have explicit aliases, 
	 * the Event Handler needs to generate synthetic names for these columns in the result set. 
	 * This happens in the exitSelect_item method when processing SELECT list items.
	 * The counter ensures:
	 * 1. Unique identifiers: Each unnamed predicand gets a unique synthetic name (unnamed_0, 
	 *    unnamed_1, etc.)
	 * 2. Symbol table consistency: These synthetic names are used in the symbol table to 
	 *    maintain references
	 * 3. Complete interface definition: The query's result set interface is fully defined 
	 *    even with unnamed expressions
	 * 4. AST integrity: Ensures the abstract syntax tree correctly represents all columns, 
	 *    even without explicit aliases
	 */
	public Integer predicandCount = 0;

	/**
	 * These variables keep track of syntax that can repeat in series so that
	 * the list can be managed as a whole
	 * 
	 * Need to be placed on the stack at the same time as the nested Queries
	 */
	public Boolean unionClauseFound = false;
	public Boolean firstUnionClause = false;
	public Boolean intersectClauseFound = false;
	public Boolean firstIntersectClause = false;
	public  Boolean useAsLeaf = false;
	/**
	 * Per query_specification stack frame: {@code null} when the frame is not a SELECT
	 * query_spec, {@code false} before {@code exitFrom_clause}, {@code true} after.
	 */
	public Boolean queryFromClauseComplete = null;
	private final HashSet<String> validatedSetOperationEntries = new HashSet<String>();
	private final HashSet<String> validatedSetOperationSiblingGroups = new HashSet<String>();
	private final HashSet<String> emittedSetOperationMismatchSignatures = new HashSet<String>();
	private final HashMap<String, PendingSetOperationMismatchFatal> pendingGenericSetOperationMismatches = new HashMap<String, PendingSetOperationMismatchFatal>();
	private static final String SET_OPERATION_SUMMARY_KEY = "set_operation_key";
	private static final String SET_OPERATION_SUMMARY_COLUMN_COUNT_KEY = "column_count";
	private static final String SET_OPERATION_SUMMARY_COLUMN_NAMES_KEY = "column_names_csv";
	private static final String SET_OPERATION_SUMMARY_LINE_KEY = "anchor_line";
	private static final String SET_OPERATION_SUMMARY_CHAR_KEY = "anchor_char";
	private static final String SET_OPERATION_SUMMARY_PARTICIPANT_LINEAGE_KEYS = "participant_lineage_summary_keys";

	private static final class PendingSetOperationMismatchFatal {
		private final String diagCode;
		private final String diagMessage;
		private final Integer line;
		private final Integer character;
		private final String source;

		private PendingSetOperationMismatchFatal(
				String diagCode,
				String diagMessage,
				Integer line,
				Integer character,
				String source) {
			this.diagCode = diagCode;
			this.diagMessage = diagMessage;
			this.line = line;
			this.character = character;
			this.source = source;
		}
	}

    
	/*
	 * This method figures out which context level the current ruleIndex is
	 * being considered at. It is used to determine the current stack level
	 * for the ruleIndex in the stackSymbols map.
	 */
	public Integer currentStackLevel(String key) {
		return stackSymbols.get(key);
	}

	// For nested Queries especially, when managing individual symbol tables, the symbol table should be managed 
	// from the stack. This method pushes a new symbol table onto the stack where the symbol table
	// logic should work. As the parser leaves the context, the symbol table is popped from the stack
	// and the symbols are added to the parent symbol table.
	public void pushSymbolTable() {
		Object symbols = symbolTable;
		if (symbols != null) {
			pushStack("symbolTable", symbols);
		}
		symbolTable = new HashMap<String, Object>();
		
		// Push current flags onto stack
		pushFlagMap();
		
	}

	// Method works with the Flag Map object to manage the current context of the parser
	// when these flags are needed.

	public void pushFlagMap() {
		// Build flag map for stack
		HashMap<String, Object> flagMap = new HashMap<String, Object> ();
		flagMap.put("unionClauseFound",unionClauseFound);
		flagMap.put("firstUnionClause",firstUnionClause);
		flagMap.put("intersectClauseFound",intersectClauseFound);
		flagMap.put("firstIntersectClause",firstIntersectClause);
		flagMap.put("useAsLeaf",useAsLeaf);
		flagMap.put("queryFromClauseComplete", queryFromClauseComplete);

		pushStack("flagMapTable", flagMap);

		// Reset Flags
		unionClauseFound = false;
		firstUnionClause = false;
		intersectClauseFound = false;
		firstIntersectClause = false;
		useAsLeaf = false;
		queryFromClauseComplete = null;
	}

	/**
	 * These methods work with the SymbolTable stack when the parser is leaving
	 * a specific Context like finishing a subquery or a substitution variable.
	 * Upon leaving a context, the current symbol table is popped from the stack
	 * and the symbols are added to the parent symbol table.
	 */
	@SuppressWarnings("unchecked")
	public void popSymbolTable(String key, HashMap<String, Object> symbols) {
		symbolTable = (HashMap<String, Object>) popStack("symbolTable");
		symbolTable.put(key, symbols);
		
		popFlagMap();
	}

	@SuppressWarnings("unchecked")
	public void popSymbolTablePutAll(HashMap<String, Object> symbols) {
		symbolTable = (HashMap<String, Object>) popStack("symbolTable");
		symbolTable.putAll(symbols);

		popFlagMap();
	}

	@SuppressWarnings("unchecked")
	public void popFlagMap() {
		// Pop Flags and reset them
		HashMap<String, Object> flagMap = (HashMap<String, Object>) popStack("flagMapTable");

		// Reset Flags
		unionClauseFound = (Boolean) flagMap.get ("unionClauseFound");
		firstUnionClause =  (Boolean) flagMap.get ("firstUnionClause");
		intersectClauseFound =  (Boolean) flagMap.get ("intersectClauseFound");
		firstIntersectClause =  (Boolean) flagMap.get ("firstIntersectClause");
		useAsLeaf =  (Boolean) flagMap.get ("useAsLeaf");
		queryFromClauseComplete = (Boolean) flagMap.get("queryFromClauseComplete");
	}

	/** Marks the current stack frame as a SELECT query_spec whose FROM clause is not yet complete. */
	public void beginQuerySpecificationFromClause() {
		queryFromClauseComplete = Boolean.FALSE;
	}

	/** Marks the current query_spec frame's FROM clause as complete. */
	public void markCurrentQueryFromClauseComplete() {
		if (Boolean.FALSE.equals(queryFromClauseComplete)) {
			queryFromClauseComplete = Boolean.TRUE;
		}
	}

	/**
	 * Returns true when any query_specification frame on the symbol/flag stack still
	 * has a pending FROM clause (select-list-before-FROM walk order).
	 */
	@SuppressWarnings("unchecked")
	public boolean anyIncompleteQuerySpecificationOnStack() {
		if (Boolean.FALSE.equals(queryFromClauseComplete)) {
			return true;
		}
		Integer level = stackSymbols.get("flagMapTable");
		if (level == null) {
			return false;
		}
		for (int stackIndex = level; stackIndex >= 1; stackIndex--) {
			Object flagMapObj = asTree.get("flagMapTable_" + stackIndex);
			if (!(flagMapObj instanceof HashMap<?, ?> flagMapObjTyped)) {
				continue;
			}
			HashMap<String, Object> flagMap = (HashMap<String, Object>) flagMapObjTyped;
			if (Boolean.FALSE.equals(flagMap.get("queryFromClauseComplete"))) {
				return true;
			}
		}
		return false;
	}

    
	/**
	 Standard Actions: Construct and Manage Symbol Tables
    */
	/**
	 * Put table aliases into Symbol Tree and move item list to the table
	 * reference
	 * 
	 * @param tableReference
	 * @param tableReference
	 */
	
	
	@SuppressWarnings("unchecked")
	public void collectTableAlias(String alias, Object tableReference) {
		if (tableReference instanceof String) {
			String tableRef = (String) tableReference;

			Object qryTableAliasObject = symbolTable.get(MUMBLE_TABLE_ALIAS_KEY);
			if (!(qryTableAliasObject instanceof Map<?, ?>)) {
				qryTableAliasObject = new HashMap<String, Object>();
				symbolTable.put(MUMBLE_TABLE_ALIAS_KEY, qryTableAliasObject);
			}
			Map<String, Object> qryTableAlias = (Map<String, Object>) qryTableAliasObject;

			Object ref = symbolTable.get(tableRef);
			if (!(ref instanceof Map<?, ?>)) {
				if (!(tableRef.startsWith(MUMBLE_QUERY_KEY)
						|| tableRef.startsWith(MUMBLE_VALUES_KEY)
						|| tableRef.startsWith(MUMBLE_INSERT_KEY)
						|| tableRef.startsWith(MUMBLE_UPDATE_KEY)
						|| tableRef.startsWith(MUMBLE_UNION_KEY)
						|| tableRef.startsWith(MUMBLE_INTERSECT_KEY))) {
					ensureTableDictionaryEntry(tableRef);
				}
			}

			Object aliasSet = qryTableAlias.get((String) alias);
			if (aliasSet == null) {
				// Alias is not already mapped, add it to the alias map
				if (!alias.equals(tableRef))
					// Only add the alias if it's different from the table reference; otherwise, it doesn't need to be added as an alias
					qryTableAlias.put(alias, tableRef);
			}
		} else if (tableReference instanceof HashMap<?, ?>) {
			showTrace(symbolTrace, "Error collecting table reference: " + tableReference);
		}
	}

	/**
	 * Add the Column reference or Values Column Reference to the Source Table Reference in the Symbol Table
	 * Unless the column is fully specified with its table reference, it gets added to the "UNKNOWN" table reference 
	 * in the Symbol Table
	 * This allows the walker to capture column references before their corresponding table references are fully 
	 * specified in the SQL statement.
	 * 
	 * If the same column is referenced multiple times in the SQL statement, we'll collect the parser token for each reference in a list under the column name in the Symbol Table. This allows the walker to keep track of all of the references to a column in the SQL statement and to use that information when building the AST Tree and resolving references.
	 * in an array list under the column name in the Symbol Table. This allows the walker to keep track of all of the 
	 * references to a column in the SQL statement and to use that information when building the AST Tree and 
	 * resolving references.
	 * 
	 * @param tableReference
	 * @param token
	 */
	@SuppressWarnings("unchecked")
	public void collectUnresolvedColumnReference(Object tableReference, Object item, Token token) {
	
		HashMap<String, Object> unresolvedColumnEntry = buildUnresolvedColumnEntry(tableReference, item, token);
		if (unresolvedColumnEntry == null) {
			return;
		}

		if (tryResolveIntraSelectListForwardReference(tableReference, unresolvedColumnEntry)) {
			return;
		}

		Object qryTableDictObject = symbolTable.get(MUMBLE_UNRESOLVED_COLUMN_KEY);
		if (!(qryTableDictObject instanceof Map<?, ?>)) {
			qryTableDictObject = new HashMap<String, Object>();
			symbolTable.put(MUMBLE_UNRESOLVED_COLUMN_KEY, qryTableDictObject);
		}
		Map<String, Object> qryTableDict = (Map<String, Object>) qryTableDictObject;

		String unresolvedKey = buildUnresolvedColumnKey(tableReference, unresolvedColumnEntry);
		if (unresolvedKey == null) {
			return;
		}

		if (unresolvedKey.contains(".")) {
			HashMap<String, Object> single = new HashMap<String, Object>();
			single.put(unresolvedKey, unresolvedColumnEntry);
			mergeUnknownEntries(globalQualifiedUnresolvedLocations, single);
		}

		String existingKey = findMatchingColumnKey(qryTableDict, unresolvedKey);
		Object existingEntry = existingKey == null ? null : qryTableDict.get(existingKey);
		if (existingEntry instanceof Map<?, ?> existingMap) {
			Object existingLocationsObj = ((Map<String, Object>) existingMap).get("locations");
			Object incomingLocationsObj = unresolvedColumnEntry.get("locations");
			if (existingLocationsObj instanceof ArrayList<?> existingLocations
					&& incomingLocationsObj instanceof ArrayList<?> incomingLocations) {
				for (Object location : incomingLocations) {
					if (!((ArrayList<Object>) existingLocations).contains(location)) {
						((ArrayList<Object>) existingLocations).add(location);
					}
				}
			}
			if (!((Map<String, Object>) existingMap).containsKey(MUMBLE_COLUMN_KEY)) {
				((Map<String, Object>) existingMap).put(MUMBLE_COLUMN_KEY, unresolvedColumnEntry.get(MUMBLE_COLUMN_KEY));
			}
		} else {
			qryTableDict.put(unresolvedKey, unresolvedColumnEntry);
		}
	}

	/**
	 * While walking a select list, output columns registered by earlier {@code exitSelect_item}
	 * calls are visible on {@link MumbleConstants#MUMBLE_INTERFACE_KEY}. Unqualified refs to
	 * those names in later select-item expressions are self-references to the current query,
	 * not external unresolved columns.
	 */
	@SuppressWarnings("unchecked")
	private boolean tryResolveIntraSelectListForwardReference(
			Object tableReference,
			HashMap<String, Object> unresolvedColumnEntry) {
		if (!isInsideSelectList()) {
			return false;
		}
		if (normalizeUnresolvedTableRef(tableReference) != null) {
			return false;
		}

		Object columnMetaObj = unresolvedColumnEntry.get(MUMBLE_COLUMN_KEY);
		if (!(columnMetaObj instanceof Map<?, ?> columnMeta)) {
			return false;
		}
		String columnName = (String) ((Map<String, Object>) columnMeta).get(MUMBLE_NAME_KEY);
		if (columnName == null || columnName.isBlank() || "*".equals(columnName)) {
			return false;
		}

		Object interfaceObj = symbolTable.get(MUMBLE_INTERFACE_KEY);
		if (!(interfaceObj instanceof Map<?, ?>)) {
			return false;
		}
		HashMap<String, Object> selectInterface = (HashMap<String, Object>) interfaceObj;
		String interfaceKey = findInterfaceKeyIgnoreCase(selectInterface, columnName);
		if (interfaceKey == null || !isInterfaceOutputAliasOnly(selectInterface, interfaceKey)) {
			return false;
		}

		Object queryDictObj = symbolTable.get(MUMBLE_QUERY_DICTIONARY_KEY);
		HashMap<String, Object> queryDictionary;
		if (queryDictObj instanceof HashMap<?, ?> existingDictionary) {
			queryDictionary = (HashMap<String, Object>) existingDictionary;
		} else {
			queryDictionary = new HashMap<String, Object>();
			symbolTable.put(MUMBLE_QUERY_DICTIONARY_KEY, queryDictionary);
		}
		mergeResolvedColumnIntoDictionary(queryDictionary, interfaceKey, unresolvedColumnEntry);
		return true;
	}

	private boolean isInsideSelectList() {
		Integer level = currentStackLevel(SQLSelectParserParser.RULE_select_list);
		return level != null && level > 0;
	}

	private String findInterfaceKeyIgnoreCase(Map<String, Object> interfaceMap, String columnName) {
		if (interfaceMap == null || interfaceMap.isEmpty() || columnName == null) {
			return null;
		}
		if (interfaceMap.containsKey(columnName)) {
			return columnName;
		}
		if (isQuotedIdentifier(columnName)) {
			return null;
		}
		for (String existingKey : interfaceMap.keySet()) {
			if (existingKey != null
					&& !isQuotedIdentifier(existingKey)
					&& existingKey.equalsIgnoreCase(columnName)) {
				return existingKey;
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	private boolean isInterfaceOutputAliasOnly(
			HashMap<String, Object> localInterface,
			String outputColumnName) {
		if (outputColumnName == null || outputColumnName.isBlank()) {
			return false;
		}
		String matchedKey = findInterfaceKeyIgnoreCase(localInterface, outputColumnName);
		if (matchedKey == null) {
			return false;
		}
		Object refsObj = localInterface.get(matchedKey);
		if (!(refsObj instanceof ArrayList<?> refs)) {
			return true;
		}
		for (Object refObj : refs) {
			String sourceColumnName = extractReferenceNameFromInterfaceEntry(refObj);
			if (sourceColumnName != null
					&& sourceColumnName.equalsIgnoreCase(outputColumnName)) {
				return false;
			}
		}
		return true;
	}

	@SuppressWarnings("unchecked")
	private HashMap<String, Object> buildUnresolvedColumnEntry(Object tableReference, Object item, Token token) {
		HashMap<String, Object> columnMetadata = extractUnresolvedColumnMetadata(tableReference, item);
		if (columnMetadata == null) {
			return null;
		}

		HashMap<String, Object> unresolvedEntry = new HashMap<String, Object>();
		unresolvedEntry.put(MUMBLE_COLUMN_KEY, columnMetadata);

		ArrayList<String> locations = new ArrayList<String>();
		if (token != null) {
			locations.add(token.toString());
		}
		unresolvedEntry.put("locations", locations);
		return unresolvedEntry;
	}

	@SuppressWarnings("unchecked")
	private HashMap<String, Object> extractUnresolvedColumnMetadata(Object tableReference, Object item) {
		if (item == null) {
			return null;
		}

		if (item instanceof String columnName) {
			HashMap<String, Object> columnMap = new HashMap<String, Object>();
			columnMap.put(MUMBLE_NAME_KEY, columnName);
			String normalizedTableRef = normalizeUnresolvedTableRef(tableReference);
			columnMap.put(MUMBLE_TABLE_REF_KEY, normalizedTableRef);
			return columnMap;
		}

		if (!(item instanceof Map<?, ?> itemMap)) {
			return null;
		}

		if (itemMap.containsKey(MUMBLE_COLUMN_KEY)) {
			Object columnObj = itemMap.get(MUMBLE_COLUMN_KEY);
			if (columnObj instanceof Map<?, ?>) {
				HashMap<String, Object> columnMap = new HashMap<String, Object>();
				columnMap.putAll((Map<String, Object>) columnObj);
				if (columnMap.containsKey(MUMBLE_SUBSTITUTION_KEY)
						&& !isColumnTypeSubstitution(columnMap.get(MUMBLE_SUBSTITUTION_KEY))) {
					return null;
				}
				if (!columnMap.containsKey(MUMBLE_TABLE_REF_KEY)) {
					columnMap.put(MUMBLE_TABLE_REF_KEY, normalizeUnresolvedTableRef(tableReference));
				}
				return columnMap;
			}
		}

		if (itemMap.containsKey(MUMBLE_SUBSTITUTION_KEY)) {
			if (!isColumnTypeSubstitution(itemMap.get(MUMBLE_SUBSTITUTION_KEY))) {
				return null;
			}
			HashMap<String, Object> substitutionContainer = new HashMap<String, Object>();
			substitutionContainer.putAll((Map<String, Object>) itemMap);
			String normalizedTableRef = normalizeUnresolvedTableRef(tableReference);
			if (normalizedTableRef != null) {
				substitutionContainer.put(MUMBLE_TABLE_REF_KEY, normalizedTableRef);
			}
			return substitutionContainer;
		}

		if (itemMap.containsKey(MUMBLE_NAME_KEY)) {
			HashMap<String, Object> columnMap = new HashMap<String, Object>();
			columnMap.putAll((Map<String, Object>) itemMap);
			if (columnMap.containsKey(MUMBLE_SUBSTITUTION_KEY)
					&& !isColumnTypeSubstitution(columnMap.get(MUMBLE_SUBSTITUTION_KEY))) {
				return null;
			}
			if (!columnMap.containsKey(MUMBLE_TABLE_REF_KEY)) {
				columnMap.put(MUMBLE_TABLE_REF_KEY, normalizeUnresolvedTableRef(tableReference));
			}
			return columnMap;
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	private String buildUnresolvedColumnKey(Object tableReference, HashMap<String, Object> unresolvedColumnEntry) {
		if (unresolvedColumnEntry == null) {
			return null;
		}

		Object columnObj = unresolvedColumnEntry.get(MUMBLE_COLUMN_KEY);
		if (!(columnObj instanceof Map<?, ?>)) {
			return null;
		}

		Map<String, Object> columnMetadata = (Map<String, Object>) columnObj;
		String tableRef = null;
		String columnName = null;

		Object tableRefObj = columnMetadata.get(MUMBLE_TABLE_REF_KEY);
		if (tableRefObj != null) {
			tableRef = normalizeUnresolvedTableRef(tableRefObj);
		}

		// Preserve table-ref/alias case here so unresolved qualified keys (e.g. T3.col1)
		// continue to match interface and filter references captured from the parse tree.
		// Keep column key text as captured; case-insensitive merging for unquoted
		// identifiers happens during dictionary merge operations.
		Object nameObj = columnMetadata.get(MUMBLE_NAME_KEY);
		if (nameObj instanceof String name) {
			columnName = name;
		}

		if (columnName == null && columnMetadata.containsKey(MUMBLE_SUBSTITUTION_KEY)) {
			Object substitutionObj = columnMetadata.get(MUMBLE_SUBSTITUTION_KEY);
			if (substitutionObj instanceof Map<?, ?> substitutionMap) {
				Object substitutionNameObj = ((Map<String, Object>) substitutionMap).get(MUMBLE_NAME_KEY);
				if (substitutionNameObj instanceof String) {
					columnName = (String) substitutionNameObj;
				}
			}
		}

		if (columnName == null || columnName.isBlank()) {
			return null;
		}

		if (tableRef != null) {
			return tableRef + "." + columnName;
		}
		return columnName;
	}

	private String normalizeUnresolvedTableRef(Object tableReference) {
		if (tableReference == null) {
			return null;
		}
		String tableRef = tableReference.toString();
		if (tableRef == null) {
			return null;
		}
		tableRef = tableRef.trim();
		if (tableRef.isEmpty()
				|| MUMBLE_UNKNOWN_KEY.equalsIgnoreCase(tableRef)
				|| "*".equals(tableRef)
				|| "null".equalsIgnoreCase(tableRef)) {
			return null;
		}
		return tableRef;
	}

	/**
	 * Returns true if the identifier is a double-quoted SQL identifier (e.g. {@code "MyCol"}).
	 * Quoted identifiers are case-sensitive; unquoted identifiers are case-insensitive.
	 */
	private boolean isQuotedIdentifier(String name) {
		return name != null && name.length() >= 2 && name.charAt(0) == '"' && name.charAt(name.length() - 1) == '"';
	}

	private boolean areEquivalentColumnKeys(String existingKey, String incomingKey) {
		if (existingKey == null || incomingKey == null) {
			return false;
		}

		boolean existingQuoted = isQuotedIdentifier(existingKey);
		boolean incomingQuoted = isQuotedIdentifier(incomingKey);
		if (existingQuoted || incomingQuoted) {
			return existingKey.equals(incomingKey);
		}

		return existingKey.equalsIgnoreCase(incomingKey);
	}

	private String findMatchingColumnKey(Map<String, Object> dictionary, String columnName) {
		if (dictionary == null || columnName == null) {
			return null;
		}

		if (dictionary.containsKey(columnName)) {
			return columnName;
		}

		for (String existingKey : dictionary.keySet()) {
			if (areEquivalentColumnKeys(existingKey, columnName)) {
				return existingKey;
			}
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	private Object normalizeUnresolvedColumnItem(Object item) {
		if (item == null) {
			return null;
		}

		if (item instanceof String) {
			return item;
		}

		if (!(item instanceof Map<?, ?> itemMap)) {
			return null;
		}

		if (itemMap.containsKey(MUMBLE_SUBSTITUTION_KEY)) {
			if (!isColumnTypeSubstitution(itemMap.get(MUMBLE_SUBSTITUTION_KEY))) {
				return null;
			}
			return item;
		}

		Object columnObj = itemMap.get(MUMBLE_COLUMN_KEY);
		if (columnObj instanceof Map<?, ?> columnMap) {
			if (columnMap.containsKey(MUMBLE_SUBSTITUTION_KEY)) {
				if (!isColumnTypeSubstitution(columnMap.get(MUMBLE_SUBSTITUTION_KEY))) {
					return null;
				}
				return columnMap;
			}
			Object nameObj = columnMap.get(MUMBLE_NAME_KEY);
			if (nameObj instanceof String) {
				return nameObj;
			}
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	private boolean isColumnTypeSubstitution(Object substitutionObj) {
		if (!(substitutionObj instanceof Map<?, ?> substitutionMapObj)) {
			return false;
		}
		Map<String, Object> substitutionMap = (Map<String, Object>) substitutionMapObj;
		Object typeObj = substitutionMap.get(MUMBLE_TYPE_KEY);
		return typeObj instanceof String && MUMBLE_COLUMN_KEY.equals(typeObj);
	}

	/* Find table name for table alias, ignore case */
	private String findTableAliasIgnoreCase(String tableReference) {
		if (tableReference == null || symbolTable == null || symbolTable.isEmpty()) {
			return null;
		}

		Object qryTableAliasObject = symbolTable.get(MUMBLE_TABLE_ALIAS_KEY);
		if (!(qryTableAliasObject instanceof Map<?, ?>)) {
			qryTableAliasObject = new HashMap<String, Object>();
			symbolTable.put(MUMBLE_TABLE_ALIAS_KEY, qryTableAliasObject);
		}
		Map<String, Object> qryTableAlias = (Map<String, Object>) qryTableAliasObject;
		
		for (Map.Entry<String, Object> entry : qryTableAlias.entrySet()) {
			if (!(entry.getValue() instanceof String)) {
				continue;
			}
			if (entry.getKey().equalsIgnoreCase(tableReference)) {
				return entry.getKey();
			}
		}

		return null;
	}
	
    
	/**
	 * 
	 * Adds column references and column substitution variables to the symbol table. If the item is a column reference, 
	 * it gets added to the symbol table under the column name and collects an array of tokens for each reference to that 
	 * column in the SQL statement. Columns that appear in multiple locations will have multiple token strings, which
	 * can be used later to locate the reference position in the original SQL string.
	 * 
	 * If the item is a column substitution variable, it gets added to the symbol table under its name with its own 
	 * nested symbol table for its properties and references. 
	 * 
	 * If the item is a predicate substitution variable, it gets added to the symbol table with its own nested symbol 
	 * table for its properties and references. If the item is a subquery, it gets added to the symbol table under a 
	 * "subquery" key with its own nested symbol table for its properties and references.
	 * 
	 * @param localSymbolTable
	 * @param item
	 * @param token
	 */
	@SuppressWarnings("unchecked")
	public void addColumnTokenToColumnDict(Object tableDictObject, Object item, Token token) {
		if (item instanceof String) {
			HashMap<String, Object> tableDictMap = (HashMap<String, Object>) tableDictObject;
			String itemKey = (String) item;
			String existingKey = findMatchingColumnKey(tableDictMap, itemKey);
			String targetKey = existingKey == null ? itemKey : existingKey;
			// Item is a column reference, add it if we haven't captured it yet
			if (existingKey == null) {
				ArrayList<String> tokenList = new ArrayList<String>();
				tokenList.add(token.toString());
				tableDictMap.put(targetKey, tokenList);
			} else {
				String tokenStr = token.toString();
	                Object  entry = tableDictMap.get(targetKey);
				ArrayList<String> tokenList = (ArrayList<String>) entry;
				if (!tokenList.contains(tokenStr))
					tokenList.add(tokenStr);
			}
		}
		else {
			HashMap<String, Object> node = (HashMap<String, Object>) item;
			if (node.containsKey(getASTWALKER_SUBSTITUTION_KEY())) {

				node = (HashMap<String, Object>) node.get(getASTWALKER_SUBSTITUTION_KEY());
				if (node.get(ASTWALKER_TYPE_KEY).equals(getASTWALKER_COLUMN_KEY()))
					// Item is a Column Substitution Variable
					((HashMap<String, Object>) tableDictObject).put((String) node.get("name"),
							(HashMap<String, Object>) item);
				// else
				// 	// Item is a Predicate Substitution Variable
				// 	((HashMap<String, Object>) tableDictObject).putAll((HashMap<String, Object>) item);
			} else {
				showTrace(symbolTrace, "Error collecting item: " + item);
			}
		}
	}

    /**
	 * Consolidate SQL VALUES Statement Symbol Table; 
	 * Parser Walker creates a virtual column list if a real one is not there.
	 *  But if there is a real column list, then you don't need the virtual one so get rid of it.
	 * @param alias 
	 */
	@SuppressWarnings("unchecked")
	public void consolidateValuesStatementSymbolTable(String alias) {
		
		if (symbolTable.keySet().contains(getASTWALKER_UNKNOWN_KEY())) {
			Map<String, Object> unknownSet = (HashMap<String, Object>)symbolTable.remove(getASTWALKER_UNKNOWN_KEY());
			if (symbolTable.keySet().contains(getASTWALKER_VALUES_KEY())) {
				Map<String, Object> valuesSet = (HashMap<String, Object>)symbolTable.remove(getASTWALKER_VALUES_KEY());
			}
			symbolTable.put(alias, unknownSet);
		} else  {
			Map<String, Object> valuesSet = (HashMap<String, Object>)symbolTable.remove(getASTWALKER_VALUES_KEY());
			symbolTable.put(alias, valuesSet);
		}
	}

	/**
	 * Put the query Interface (its output column list) into the Symbol Table
	 */
	public HashMap<String, Object> captureQueryInterface() {
		String prefx = getASTWALKER_QUERY_KEY();
		HashMap<String, Object> interfac = getInterfaceFromQuery(prefx);
		if (interfac == null) {
			prefx = getASTWALKER_INSERT_KEY();
			interfac = getInterfaceFromQuery(prefx);
		}
		if (interfac == null) {
			prefx = getASTWALKER_UPDATE_KEY();
			interfac = getInterfaceFromQuery(prefx);
		}
		if (interfac == null) {
			prefx = getASTWALKER_UNION_KEY();
			interfac = getInterfaceFromQuery(prefx);
		}
		if (interfac == null) {
			prefx = getASTWALKER_INTERSECT_KEY();
			interfac = getInterfaceFromQuery(prefx);
		}
		if (interfac == null) {
			prefx = getASTWALKER_VALUES_KEY();
			interfac = getInterfaceFromQuery(prefx);
		}
		if (interfac != null) {
			// need to get the interface from inside the query
			HashMap<String, Object> newif = new HashMap<String, Object>();
			for (String key : interfac.keySet()) {
				newif.put(key, prefx + "_column");
			}
			symbolTable.put(MUMBLE_INTERFACE_KEY, newif);
			return newif;
		}
		return null;
	}

	/**
	 * @param hdr
	 * @return
	 */
	public HashMap<String, Object> getInterfaceFromQuery(String hdr) {
		String queryName = hdr + (queryCount - 1);
		HashMap<String, Object> query = (HashMap<String, Object>) symbolTable.get(queryName);
		HashMap<String, Object> interfac = getInterface(query);
		return interfac;
	}

	/**
	 * @param query
	 * @return
	 */
	public HashMap<String, Object> getInterface(HashMap<String, Object> query) {
		HashMap<String, Object> interfac = null;
		if (query != null) {
			interfac = (HashMap<String, Object>) query.get("interface");
		} else
			interfac = null;
		HashMap<String, Object> interfac1 = interfac;
		return interfac1;
	}

	@SuppressWarnings("unchecked")
	public HashMap<String, Object> resolveSetOperationInterfaceMapFromSymbolTable() {
		Object interfaceObj = symbolTable.get(MUMBLE_INTERFACE_KEY);
		if (interfaceObj instanceof HashMap<?, ?>) {
			return (HashMap<String, Object>) interfaceObj;
		}
		if (interfaceObj instanceof Map<?, ?> interfaceMap) {
			return new HashMap<String, Object>((Map<String, Object>) interfaceMap);
		}

		Map.Entry<String, HashMap<String, Object>> topSetEntry = getTopSetOperationSymbolEntry();
		if (topSetEntry == null) {
			return null;
		}

		Object setInterfaceObj = topSetEntry.getValue().get(MUMBLE_INTERFACE_KEY);
		if (setInterfaceObj instanceof HashMap<?, ?>) {
			return (HashMap<String, Object>) setInterfaceObj;
		}
		if (setInterfaceObj instanceof Map<?, ?> setInterfaceMap) {
			return new HashMap<String, Object>((Map<String, Object>) setInterfaceMap);
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	private Map.Entry<String, HashMap<String, Object>> getTopSetOperationSymbolEntry() {
		if (symbolTable == null || symbolTable.isEmpty()) {
			return null;
		}

		Map.Entry<String, HashMap<String, Object>> topSetEntry = null;
		int highestIndex = Integer.MIN_VALUE;
		for (Map.Entry<String, Object> symbolEntry : symbolTable.entrySet()) {
			String key = symbolEntry.getKey();
			if (!isSetOperationScopeKey(key)) {
				continue;
			}
			if (!(symbolEntry.getValue() instanceof HashMap<?, ?> entryMap)) {
				continue;
			}

			String liveKey = normalizeSetOperationScopeKey(key);
			int index = extractTrailingNumericSuffix(liveKey);
			if (index >= highestIndex) {
				highestIndex = index;
				topSetEntry = Map.entry(liveKey, (HashMap<String, Object>) entryMap);
			}
		}

		return topSetEntry;
	}

	@SuppressWarnings("unchecked")
	public void validateSetOperationInterface(HashMap<String, Object> interfaceMap, String locationTokenString) {
		if (symbolTable == null || symbolTable.isEmpty()) {
			return;
		}

		// Collect set-operation keys that are JOIN-subquery alias targets — these are
		// independent subquery sources (accessed via table alias) and must NOT be treated
		// as set-operation siblings of each other.
		HashSet<String> joinAliasTargets = new HashSet<>();
		Object tableAliasObj = symbolTable.get(MUMBLE_TABLE_ALIAS_KEY);
		if (tableAliasObj instanceof Map<?, ?> tableAliasMap) {
			for (Object aliasTarget : tableAliasMap.values()) {
				if (aliasTarget instanceof String s) {
					joinAliasTargets.add(s);
				}
			}
		}

		ArrayList<Map.Entry<String, HashMap<String, Object>>> setOperationEntries =
				new ArrayList<Map.Entry<String, HashMap<String, Object>>>();
		for (Map.Entry<String, Object> symbolEntry : symbolTable.entrySet()) {
			String key = symbolEntry.getKey();
			if (!isSetOperationScopeKey(key)) {
				continue;
			}
			if (!(symbolEntry.getValue() instanceof HashMap<?, ?> entryMap)) {
				continue;
			}
			String liveKey = normalizeSetOperationScopeKey(key);
			// Skip set-operations that are referenced as JOIN subquery aliases — they are
			// not set-operation participants of one another.
			if (joinAliasTargets.contains(liveKey)) {
				continue;
			}
			setOperationEntries.add(Map.entry(liveKey, (HashMap<String, Object>) entryMap));
		}

		if (setOperationEntries.isEmpty()) {
			return;
		}

		setOperationEntries.sort((left, right) -> {
			int leftIndex = extractTrailingNumericSuffix(left.getKey());
			int rightIndex = extractTrailingNumericSuffix(right.getKey());
			int indexCompare = Integer.compare(leftIndex, rightIndex);
			if (indexCompare != 0) {
				return indexCompare;
			}
			return left.getKey().compareTo(right.getKey());
		});

		for (Map.Entry<String, HashMap<String, Object>> setEntry : setOperationEntries) {
			String setOperationKey = setEntry.getKey();
			if (validatedSetOperationEntries.contains(setOperationKey)) {
				continue;
			}

			validateSingleSetOperationInterface(setOperationKey, setEntry.getValue());
			validateNestedSetOperationInterfaces(setEntry.getValue());
			validatedSetOperationEntries.add(setOperationKey);
		}

		validateTopLevelSetOperationSiblings(setOperationEntries);
		flushPendingGenericSetOperationMismatchFatals();
	}

	@SuppressWarnings("unchecked")
	private void validateNestedSetOperationInterfaces(HashMap<String, Object> scopeMap) {
		if (scopeMap == null || scopeMap.isEmpty()) {
			return;
		}

		for (Map.Entry<String, Object> entry : scopeMap.entrySet()) {
			String scopeKey = entry.getKey();
			if (!isSetOperationScopeKey(scopeKey) || !(entry.getValue() instanceof HashMap<?, ?> nestedScopeObj)) {
				continue;
			}

			String liveScopeKey = normalizeSetOperationScopeKey(scopeKey);
			if (liveScopeKey == null || validatedSetOperationEntries.contains(liveScopeKey)) {
				continue;
			}

			HashMap<String, Object> nestedScopeMap = (HashMap<String, Object>) nestedScopeObj;
			validateSingleSetOperationInterface(liveScopeKey, nestedScopeMap);
			validateNestedSetOperationInterfaces(nestedScopeMap);
			validatedSetOperationEntries.add(liveScopeKey);
		}
	}

	@SuppressWarnings("unchecked")
	private void validateTopLevelSetOperationSiblings(
			List<Map.Entry<String, HashMap<String, Object>>> setOperationEntries) {
		HashMap<String, Object> summaryMapBySetOperationKey = consumeSetOperationInterfaceSummaryMap();
		HashMap<String, Object> querySummaryKeysMap = consumeQuerySetOperationSummaryKeysMap();

		if (setOperationEntries == null || setOperationEntries.isEmpty()) {
			return;
		}

		if (setOperationEntries.size() < 2) {
			emitSetOperationMismatchFromHandoff(
					setOperationEntries,
					summaryMapBySetOperationKey,
					querySummaryKeysMap);
			return;
		}

		HashSet<String> referencedSetOperationKeys = new HashSet<String>();
		HashMap<String, Map<String, Object>> entryMapByKey = new HashMap<String, Map<String, Object>>();
		for (Map.Entry<String, HashMap<String, Object>> setEntry : setOperationEntries) {
			entryMapByKey.put(setEntry.getKey(), setEntry.getValue());
		}

		for (Map.Entry<String, Map<String, Object>> setEntry : entryMapByKey.entrySet()) {
			for (Map.Entry<String, Object> participantEntry : setEntry.getValue().entrySet()) {
				String participantKey = participantEntry.getKey();
				if (participantKey == null) {
					continue;
				}
				String liveParticipantKey = normalizeSetOperationScopeKey(participantKey);
				if (liveParticipantKey != null
						&& entryMapByKey.containsKey(liveParticipantKey)
						&& isSetOperationScopeKey(participantKey)) {
					referencedSetOperationKeys.add(liveParticipantKey);
				}
			}
		}

		ArrayList<String> topLevelSetOperationKeys = new ArrayList<String>();
		for (String setOperationKey : entryMapByKey.keySet()) {
			if (!referencedSetOperationKeys.contains(setOperationKey)) {
				topLevelSetOperationKeys.add(setOperationKey);
			}
		}

		if (topLevelSetOperationKeys.size() < 2) {
			if (topLevelSetOperationKeys.size() == 1) {
				String topLevelSetOperationKey = topLevelSetOperationKeys.get(0);
				emitSetOperationMismatchFromHandoffTopLevel(
						topLevelSetOperationKey,
						entryMapByKey.get(topLevelSetOperationKey),
						summaryMapBySetOperationKey,
						querySummaryKeysMap);
			} else {
				emitSetOperationMismatchFromHandoff(
						setOperationEntries,
						summaryMapBySetOperationKey,
						querySummaryKeysMap);
			}
			return;
		}

		topLevelSetOperationKeys.sort((left, right) -> {
			int leftIndex = extractTrailingNumericSuffix(left);
			int rightIndex = extractTrailingNumericSuffix(right);
			int indexCompare = Integer.compare(leftIndex, rightIndex);
			if (indexCompare != 0) {
				return indexCompare;
			}
			return left.compareTo(right);
		});

		String groupKey = String.join("|", topLevelSetOperationKeys);
		if (validatedSetOperationSiblingGroups.contains(groupKey)) {
			return;
		}

		String baselineKey = topLevelSetOperationKeys.get(0);
		String topLevelMismatchType = resolveTopLevelSiblingGroupMismatchType(topLevelSetOperationKeys);
		Map<String, Object> baselineSetOperationMap = entryMapByKey.get(baselineKey);
		if (baselineSetOperationMap == null) {
			return;
		}
		Object baselineInterfaceObject = baselineSetOperationMap.get(MUMBLE_INTERFACE_KEY);
		if (!(baselineInterfaceObject instanceof Map<?, ?> baselineInterface)) {
			return;
		}

		int expectedCount = baselineInterface.size();
		Map<String, Object> baselineQueryDictionary = extractQueryDictionary(baselineSetOperationMap);
		ColumnListSummary expectedSummary = withSetOperationAnchorFallback(
				buildColumnListSummary((Map<String, Object>) baselineInterface, baselineQueryDictionary),
				baselineSetOperationMap);

		String diagCode = getDiagnosticCode(DIAG_SQL_SET_OPERATION_INTERFACE_COLUMN_COUNT_MISMATCH);
		String diagTemplate = getDiagnosticMessage(DIAG_SQL_SET_OPERATION_INTERFACE_COLUMN_COUNT_MISMATCH);

		for (String siblingKey : topLevelSetOperationKeys) {
			Map<String, Object> siblingSetOperationMap = entryMapByKey.get(siblingKey);
			if (siblingSetOperationMap == null) {
				continue;
			}

			Object siblingInterfaceObject = siblingSetOperationMap.get(MUMBLE_INTERFACE_KEY);
			if (!(siblingInterfaceObject instanceof Map<?, ?> siblingInterface)) {
				continue;
			}

			int actualCount = siblingInterface.size();
			if (actualCount == expectedCount) {
				continue;
			}

			Map<String, Object> siblingQueryDictionary = extractQueryDictionary(siblingSetOperationMap);
			ColumnListSummary actualSummary = withSetOperationAnchorFallback(
					buildColumnListSummary((Map<String, Object>) siblingInterface, siblingQueryDictionary),
					siblingSetOperationMap);

			Integer expectedLine = expectedSummary.anchorLine();
			Integer expectedChar = expectedSummary.anchorChar();
			Integer actualLine = actualSummary.anchorLine();
			Integer actualChar = actualSummary.anchorChar();
			String expectedLineText = expectedLine == null ? "?" : String.valueOf(expectedLine);
			String expectedCharText = expectedChar == null ? "?" : String.valueOf(expectedChar);
			String actualLineText = actualLine == null ? "?" : String.valueOf(actualLine);
			String actualCharText = actualChar == null ? "?" : String.valueOf(actualChar);

			String diagMessage = (diagTemplate == null)
					? String.format(
							"%s has different column counts. Expected %s columns (%s) at (l:%s c:%s) but there were %s (%s) at (l:%s c:%s).",
							topLevelMismatchType,
							expectedCount,
							expectedSummary.columnNamesCsv(),
							expectedLineText,
							expectedCharText,
							actualCount,
							actualSummary.columnNamesCsv(),
							actualLineText,
							actualCharText)
					: String.format(
							diagTemplate,
							topLevelMismatchType,
							expectedCount,
							expectedSummary.columnNamesCsv(),
							expectedLineText,
							expectedCharText,
							actualCount,
							actualSummary.columnNamesCsv(),
							actualLineText,
							actualCharText);

			emitSetOperationMismatchFatalIfNew(
					topLevelMismatchType,
					diagCode,
					diagMessage,
					actualLine,
					actualChar,
					siblingKey,
					expectedCount,
					expectedSummary.columnNamesCsv(),
					expectedLine,
					expectedChar,
					actualCount,
					actualSummary.columnNamesCsv(),
					actualLine,
					actualChar);
		}

		String latestTopLevelSetOperationKey = topLevelSetOperationKeys.get(topLevelSetOperationKeys.size() - 1);
		emitSetOperationMismatchFromHandoffTopLevel(
				latestTopLevelSetOperationKey,
				entryMapByKey.get(latestTopLevelSetOperationKey),
				summaryMapBySetOperationKey,
				querySummaryKeysMap);

		validatedSetOperationSiblingGroups.add(groupKey);
	}

	@SuppressWarnings("unchecked")
	private void emitSetOperationMismatchFromHandoff(
			List<Map.Entry<String, HashMap<String, Object>>> setOperationEntries,
			Map<String, Object> summaryMapBySetOperationKey,
			Map<String, Object> querySummaryKeysMap) {
		if (setOperationEntries == null || setOperationEntries.size() != 1
				|| summaryMapBySetOperationKey == null || summaryMapBySetOperationKey.isEmpty()) {
			return;
		}

		Map.Entry<String, HashMap<String, Object>> topSetOperationEntry = setOperationEntries.get(0);
		emitSetOperationMismatchFromHandoffTopLevel(
				topSetOperationEntry.getKey(),
				topSetOperationEntry.getValue(),
				summaryMapBySetOperationKey,
				querySummaryKeysMap);
	}

	private void emitSetOperationMismatchFromHandoffTopLevel(
			String topSetOperationKey,
			Map<String, Object> topSetOperationMap,
			Map<String, Object> summaryMapBySetOperationKey,
			Map<String, Object> querySummaryKeysMap) {
		if (topSetOperationKey == null
				|| topSetOperationMap == null
				|| topSetOperationMap.isEmpty()
				|| summaryMapBySetOperationKey == null
				|| summaryMapBySetOperationKey.isEmpty()) {
			return;
		}

		Map<String, Object> topLevelSummary = asSetOperationSummary(
				summaryMapBySetOperationKey.get(topSetOperationKey));
		if (topLevelSummary == null || topLevelSummary.isEmpty()) {
			return;
		}

		ArrayList<String> participantSummaryKeys = extractProducerLineageSummaryKeys(
				topLevelSummary,
				summaryMapBySetOperationKey);
		if (participantSummaryKeys.size() < 2) {
			return;
		}

		HashSet<String> seenKeys = new HashSet<String>();
		ArrayList<String> orderedParticipantSummaryKeys = new ArrayList<String>();
		for (String key : participantSummaryKeys) {
			if (key == null || !seenKeys.add(key)) {
				continue;
			}
			orderedParticipantSummaryKeys.add(key);
		}

		orderedParticipantSummaryKeys.sort((left, right) -> {
			int leftIndex = extractTrailingNumericSuffix(left);
			int rightIndex = extractTrailingNumericSuffix(right);
			int indexCompare = Integer.compare(leftIndex, rightIndex);
			if (indexCompare != 0) {
				return indexCompare;
			}
			return left.compareTo(right);
		});

		Map<String, Object> expectedSummary = asSetOperationSummary(
				summaryMapBySetOperationKey.get(orderedParticipantSummaryKeys.get(0)));
		if (expectedSummary == null || expectedSummary.isEmpty()) {
			return;
		}

		Integer expectedCount = getSetOperationSummaryInteger(
				expectedSummary,
				SET_OPERATION_SUMMARY_COLUMN_COUNT_KEY);
		if (expectedCount == null) {
			return;
		}

		String diagCode = getDiagnosticCode(DIAG_SQL_SET_OPERATION_INTERFACE_COLUMN_COUNT_MISMATCH);
		String diagTemplate = getDiagnosticMessage(DIAG_SQL_SET_OPERATION_INTERFACE_COLUMN_COUNT_MISMATCH);
		String topLevelMismatchType = resolveSetOperationMismatchType(topSetOperationKey);

		for (int i = 1; i < orderedParticipantSummaryKeys.size(); i++) {
			String actualSummaryKey = orderedParticipantSummaryKeys.get(i);
			Map<String, Object> actualSummary = asSetOperationSummary(summaryMapBySetOperationKey.get(actualSummaryKey));
			if (actualSummary == null || actualSummary.isEmpty()) {
				continue;
			}

			Integer actualCount = getSetOperationSummaryInteger(actualSummary, SET_OPERATION_SUMMARY_COLUMN_COUNT_KEY);
			if (actualCount == null || actualCount.equals(expectedCount)) {
				continue;
			}

			Integer expectedLine = getSetOperationSummaryInteger(expectedSummary, SET_OPERATION_SUMMARY_LINE_KEY);
			Integer expectedChar = getSetOperationSummaryInteger(expectedSummary, SET_OPERATION_SUMMARY_CHAR_KEY);
			Integer actualLine = getSetOperationSummaryInteger(actualSummary, SET_OPERATION_SUMMARY_LINE_KEY);
			Integer actualChar = getSetOperationSummaryInteger(actualSummary, SET_OPERATION_SUMMARY_CHAR_KEY);
			String expectedColumns = getSetOperationSummaryString(expectedSummary, SET_OPERATION_SUMMARY_COLUMN_NAMES_KEY);
			String actualColumns = getSetOperationSummaryString(actualSummary, SET_OPERATION_SUMMARY_COLUMN_NAMES_KEY);
			String expectedLineText = expectedLine == null ? "?" : String.valueOf(expectedLine);
			String expectedCharText = expectedChar == null ? "?" : String.valueOf(expectedChar);
			String actualLineText = actualLine == null ? "?" : String.valueOf(actualLine);
			String actualCharText = actualChar == null ? "?" : String.valueOf(actualChar);

			String diagMessage = (diagTemplate == null)
					? String.format(
							"%s has different column counts. Expected %s columns (%s) at (l:%s c:%s) but there were %s (%s) at (l:%s c:%s).",
							topLevelMismatchType,
							expectedCount,
							expectedColumns,
							expectedLineText,
							expectedCharText,
							actualCount,
							actualColumns,
							actualLineText,
							actualCharText)
					: String.format(
							diagTemplate,
							topLevelMismatchType,
							expectedCount,
							expectedColumns,
							expectedLineText,
							expectedCharText,
							actualCount,
							actualColumns,
							actualLineText,
							actualCharText);

			emitSetOperationMismatchFatalIfNew(
					topLevelMismatchType,
					diagCode,
					diagMessage,
					actualLine,
					actualChar,
					actualSummaryKey,
					expectedCount,
					expectedColumns,
					expectedLine,
					expectedChar,
					actualCount,
					actualColumns,
					actualLine,
					actualChar);
		}
	}

	private String resolveSetOperationMismatchType(String setOperationKey) {
		String normalizedKey = normalizeSetOperationScopeKey(setOperationKey);
		if (normalizedKey == null || normalizedKey.isBlank()) {
			return "SET_OPERATION";
		}
		if (normalizedKey.startsWith(MUMBLE_INTERSECT_KEY)) {
			return "INTERSECTION";
		}
		if (normalizedKey.startsWith(MUMBLE_UNION_KEY)) {
			return "UNION";
		}
		return "SET_OPERATION";
	}

	private String resolveSetOperationMismatchTypeFromParticipant(Map<String, Object> participantDefinition) {
		if (participantDefinition == null || participantDefinition.isEmpty()) {
			return null;
		}

		Object setopObj = participantDefinition.get(MUMBLE_SETOP_KEY);
		if (!(setopObj instanceof String setop) || setop.isBlank()) {
			return null;
		}

		return normalizeSetopLabelForDiagnostic(setop);
	}

	private String normalizeSetopLabelForDiagnostic(String rawSetop) {
		if (rawSetop == null || rawSetop.isBlank()) {
			return null;
		}

		String normalized = rawSetop.trim().toUpperCase(java.util.Locale.ROOT);
		if ("UNION".equals(normalized)) {
			return "UNION";
		}
		if ("EXCEPT".equals(normalized)) {
			return "EXCEPT";
		}
		if ("INTERSECT".equals(normalized) || "INTERSECTION".equals(normalized)) {
			return "INTERSECTION";
		}
		return normalized;
	}

	private String resolveTopLevelSiblingGroupMismatchType(List<String> topLevelSetOperationKeys) {
		if (topLevelSetOperationKeys == null || topLevelSetOperationKeys.isEmpty()) {
			return "SET_OPERATION";
		}

		boolean allIntersect = true;
		boolean allUnion = true;
		for (String setOperationKey : topLevelSetOperationKeys) {
			String normalizedKey = normalizeSetOperationScopeKey(setOperationKey);
			boolean isIntersect = normalizedKey != null && normalizedKey.startsWith(MUMBLE_INTERSECT_KEY);
			boolean isUnion = normalizedKey != null && normalizedKey.startsWith(MUMBLE_UNION_KEY);
			allIntersect = allIntersect && isIntersect;
			allUnion = allUnion && isUnion;
		}

		if (allIntersect) {
			return "UNION";
		}
		if (allUnion) {
			return "INTERSECTION";
		}

		return resolveSetOperationMismatchType(topLevelSetOperationKeys.get(0));
	}

	private void emitSetOperationMismatchFatalIfNew(
			String mismatchType,
			String diagCode,
			String diagMessage,
			Integer line,
			Integer character,
			String source,
			int expectedCount,
			String expectedColumns,
			Integer expectedLine,
			Integer expectedChar,
			int actualCount,
			String actualColumns,
			Integer actualLine,
			Integer actualChar) {
		String signature = buildSetOperationMismatchSignature(
				expectedCount,
				expectedColumns,
				expectedLine,
				expectedChar,
				actualCount,
				actualColumns,
				actualLine,
				actualChar);

		if (!emittedSetOperationMismatchSignatures.add(signature)) {
			return;
		}

		if ("SET_OPERATION".equalsIgnoreCase(mismatchType)) {
			pendingGenericSetOperationMismatches.putIfAbsent(
					signature,
					new PendingSetOperationMismatchFatal(diagCode, diagMessage, line, character, source));
			return;
		}

		pendingGenericSetOperationMismatches.remove(signature);
		addWalkerFatal(diagCode, diagMessage, line, character, source);
	}

	private void flushPendingGenericSetOperationMismatchFatals() {
		if (pendingGenericSetOperationMismatches.isEmpty()) {
			return;
		}

		for (PendingSetOperationMismatchFatal pendingFatal : pendingGenericSetOperationMismatches.values()) {
			addWalkerFatal(
					pendingFatal.diagCode,
					pendingFatal.diagMessage,
					pendingFatal.line,
					pendingFatal.character,
					pendingFatal.source);
		}

		pendingGenericSetOperationMismatches.clear();
	}

	private String buildSetOperationMismatchSignature(
			int expectedCount,
			String expectedColumns,
			Integer expectedLine,
			Integer expectedChar,
			int actualCount,
			String actualColumns,
			Integer actualLine,
			Integer actualChar) {
		String normalizedExpectedColumns = expectedColumns == null ? "" : expectedColumns.trim().toLowerCase();
		String normalizedActualColumns = actualColumns == null ? "" : actualColumns.trim().toLowerCase();

		return expectedCount
				+ "|" + normalizedExpectedColumns
				+ "|" + actualCount
				+ "|" + normalizedActualColumns;
	}

	@SuppressWarnings("unchecked")
	private ArrayList<String> extractProducerLineageSummaryKeys(
			Map<String, Object> topLevelSummary,
			Map<String, Object> summaryMapBySetOperationKey) {
		ArrayList<String> keys = new ArrayList<String>();
		if (topLevelSummary == null
				|| topLevelSummary.isEmpty()
				|| summaryMapBySetOperationKey == null
				|| summaryMapBySetOperationKey.isEmpty()) {
			return keys;
		}

		Object lineageObj = topLevelSummary.get(SET_OPERATION_SUMMARY_PARTICIPANT_LINEAGE_KEYS);
		if (!(lineageObj instanceof List<?> lineageList)) {
			return keys;
		}

		for (Object lineageKeyObj : lineageList) {
			if (!(lineageKeyObj instanceof String summaryKey)
					|| !summaryMapBySetOperationKey.containsKey(summaryKey)) {
				continue;
			}
			keys.add(summaryKey);
		}

		keys.sort((left, right) -> {
			int leftIndex = extractTrailingNumericSuffix(left);
			int rightIndex = extractTrailingNumericSuffix(right);
			int indexCompare = Integer.compare(leftIndex, rightIndex);
			if (indexCompare != 0) {
				return indexCompare;
			}
			return left.compareTo(right);
		});

		return keys;
	}

	public void putSetOperationParticipantLineageSummaryKeys(
			Map<String, Object> setOperationSummary,
			List<String> lineageSummaryKeys) {
		if (setOperationSummary == null || setOperationSummary.isEmpty()) {
			return;
		}

		if (lineageSummaryKeys == null || lineageSummaryKeys.isEmpty()) {
			setOperationSummary.remove(SET_OPERATION_SUMMARY_PARTICIPANT_LINEAGE_KEYS);
			return;
		}

		ArrayList<String> normalizedLineage = new ArrayList<String>();
		for (String lineageKey : lineageSummaryKeys) {
			if (lineageKey == null || lineageKey.isBlank() || normalizedLineage.contains(lineageKey)) {
				continue;
			}
			normalizedLineage.add(lineageKey);
		}

		if (normalizedLineage.isEmpty()) {
			setOperationSummary.remove(SET_OPERATION_SUMMARY_PARTICIPANT_LINEAGE_KEYS);
			return;
		}

		setOperationSummary.put(SET_OPERATION_SUMMARY_PARTICIPANT_LINEAGE_KEYS, normalizedLineage);
	}

	@SuppressWarnings("unchecked")
	private Map<String, Object> asSetOperationSummary(Object summaryObj) {
		if (!(summaryObj instanceof Map<?, ?> summaryMapObj)) {
			return null;
		}
		return (Map<String, Object>) summaryMapObj;
	}

	private Integer getSetOperationSummaryInteger(Map<String, Object> summary, String key) {
		if (summary == null || key == null) {
			return null;
		}
		Object valueObj = summary.get(key);
		if (valueObj instanceof Integer intValue) {
			return intValue;
		}
		if (valueObj instanceof Number numberValue) {
			return numberValue.intValue();
		}
		if (valueObj instanceof String textValue) {
			try {
				return Integer.parseInt(textValue);
			} catch (NumberFormatException ex) {
				return null;
			}
		}
		return null;
	}

	private String getSetOperationSummaryString(Map<String, Object> summary, String key) {
		if (summary == null || key == null) {
			return "";
		}
		Object valueObj = summary.get(key);
		return valueObj == null ? "" : String.valueOf(valueObj);
	}

	@SuppressWarnings("unchecked")
	public void mergeSetOperationInterfaceSummariesIntoCurrentScope(Map<String, Object> summaryBySetOperationKey) {
		if (summaryBySetOperationKey == null || summaryBySetOperationKey.isEmpty()) {
			return;
		}

		Object existingObj = symbolTable.get(TEMP_SET_OPERATION_INTERFACE_SUMMARY_MAP_KEY);
		HashMap<String, Object> existingSummaryMap;
		if (existingObj instanceof HashMap<?, ?> existingMapObj) {
			existingSummaryMap = (HashMap<String, Object>) existingMapObj;
		} else {
			existingSummaryMap = new HashMap<String, Object>();
			symbolTable.put(TEMP_SET_OPERATION_INTERFACE_SUMMARY_MAP_KEY, existingSummaryMap);
		}

		for (Map.Entry<String, Object> summaryEntry : summaryBySetOperationKey.entrySet()) {
			if (summaryEntry.getKey() == null) {
				continue;
			}
			existingSummaryMap.put(summaryEntry.getKey(), summaryEntry.getValue());
		}
	}

	@SuppressWarnings("unchecked")
	public void mergeQuerySetOperationSummaryKeysIntoCurrentScope(Map<String, Object> queryToSummaryKeysMap) {
		if (queryToSummaryKeysMap == null || queryToSummaryKeysMap.isEmpty()) {
			return;
		}

		Object existingObj = symbolTable.get(TEMP_QUERY_SET_OPERATION_SUMMARY_KEYS_MAP_KEY);
		HashMap<String, Object> existingMap;
		if (existingObj instanceof HashMap<?, ?> existingMapObj) {
			existingMap = (HashMap<String, Object>) existingMapObj;
		} else {
			existingMap = new HashMap<String, Object>();
			symbolTable.put(TEMP_QUERY_SET_OPERATION_SUMMARY_KEYS_MAP_KEY, existingMap);
		}

		for (Map.Entry<String, Object> queryEntry : queryToSummaryKeysMap.entrySet()) {
			String queryKey = queryEntry.getKey();
			if (queryKey == null || !(queryEntry.getValue() instanceof List<?> summaryKeysListObj)) {
				continue;
			}

			ArrayList<String> mergedSummaryKeys = new ArrayList<String>();
			if (existingMap.get(queryKey) instanceof List<?> existingSummaryKeysObj) {
				for (Object existingKeyObj : existingSummaryKeysObj) {
					if (existingKeyObj instanceof String existingKey) {
						mergedSummaryKeys.add(existingKey);
					}
				}
			}

			for (Object incomingKeyObj : summaryKeysListObj) {
				if (!(incomingKeyObj instanceof String incomingKey) || mergedSummaryKeys.contains(incomingKey)) {
					continue;
				}
				mergedSummaryKeys.add(incomingKey);
			}

			existingMap.put(queryKey, mergedSummaryKeys);
		}
	}

	@SuppressWarnings("unchecked")
	public HashMap<String, Object> consumeSetOperationInterfaceSummaryMap() {
		Object summariesObj = symbolTable.get(TEMP_SET_OPERATION_INTERFACE_SUMMARY_MAP_KEY);
		if (summariesObj instanceof HashMap<?, ?> summariesMapObj) {
			return (HashMap<String, Object>) summariesMapObj;
		}
		return new HashMap<String, Object>();
	}

	@SuppressWarnings("unchecked")
	public HashMap<String, Object> consumeQuerySetOperationSummaryKeysMap() {
		Object summaryKeysObj = symbolTable.get(TEMP_QUERY_SET_OPERATION_SUMMARY_KEYS_MAP_KEY);
		if (summaryKeysObj instanceof HashMap<?, ?> summaryKeysMapObj) {
			return (HashMap<String, Object>) summaryKeysMapObj;
		}
		return new HashMap<String, Object>();
	}

	public void setCurrentSetOperationOperatorAnchor(Token operatorToken) {
		if (operatorToken == null) {
			return;
		}

		symbolTable.put(TEMP_SET_OPERATION_OPERATOR_ANCHOR_LINE_KEY, operatorToken.getLine());
		symbolTable.put(TEMP_SET_OPERATION_OPERATOR_ANCHOR_CHAR_KEY, operatorToken.getCharPositionInLine());
	}

	public HashMap<String, Object> buildSetOperationInterfaceSummary(
			String setOperationKey,
			Map<String, Object> scopeDefinition) {
		if (setOperationKey == null || setOperationKey.isBlank() || scopeDefinition == null || scopeDefinition.isEmpty()) {
			return new HashMap<String, Object>();
		}

		Object interfaceObj = scopeDefinition.get(MUMBLE_INTERFACE_KEY);
		if (!(interfaceObj instanceof Map<?, ?> interfaceMapObj)) {
			return new HashMap<String, Object>();
		}

		@SuppressWarnings("unchecked")
		Map<String, Object> interfaceMap = (Map<String, Object>) interfaceMapObj;
		if (interfaceMap.isEmpty()) {
			return new HashMap<String, Object>();
		}

		Map<String, Object> queryDictionary = extractQueryDictionary(scopeDefinition);
		ColumnListSummary summary = withSetOperationAnchorFallback(
				buildColumnListSummary(interfaceMap, queryDictionary),
				scopeDefinition);
		Integer explicitAnchorLine = getSetOperationSummaryInteger(
				scopeDefinition,
				TEMP_SET_OPERATION_OPERATOR_ANCHOR_LINE_KEY);
		Integer explicitAnchorChar = getSetOperationSummaryInteger(
				scopeDefinition,
				TEMP_SET_OPERATION_OPERATOR_ANCHOR_CHAR_KEY);
		if (explicitAnchorLine != null && explicitAnchorChar != null) {
			summary = new ColumnListSummary(summary.columnNamesCsv(), explicitAnchorLine, explicitAnchorChar);
		}

		HashMap<String, Object> summaryMap = new HashMap<String, Object>();
		summaryMap.put(SET_OPERATION_SUMMARY_KEY, setOperationKey);
		summaryMap.put(SET_OPERATION_SUMMARY_COLUMN_COUNT_KEY, interfaceMap.size());
		summaryMap.put(SET_OPERATION_SUMMARY_COLUMN_NAMES_KEY, summary.columnNamesCsv());
		summaryMap.put(SET_OPERATION_SUMMARY_LINE_KEY, summary.anchorLine());
		summaryMap.put(SET_OPERATION_SUMMARY_CHAR_KEY, summary.anchorChar());
		return summaryMap;
	}

	@SuppressWarnings("unchecked")
	public ArrayList<String> extractSetOperationSummaryKeys(Map<String, Object> summaryBySetOperationKey) {
		ArrayList<String> keys = new ArrayList<String>();
		if (summaryBySetOperationKey == null || summaryBySetOperationKey.isEmpty()) {
			return keys;
		}

		for (Map.Entry<String, Object> entry : summaryBySetOperationKey.entrySet()) {
			if (entry.getKey() == null || !(entry.getValue() instanceof Map<?, ?>)) {
				continue;
			}
			keys.add(entry.getKey());
		}

		keys.sort((left, right) -> {
			int leftIndex = extractTrailingNumericSuffix(left);
			int rightIndex = extractTrailingNumericSuffix(right);
			int indexCompare = Integer.compare(leftIndex, rightIndex);
			if (indexCompare != 0) {
				return indexCompare;
			}
			return left.compareTo(right);
		});

		return keys;
	}

	@SuppressWarnings("unchecked")
	private void validateSingleSetOperationInterface(String setOperationKey, HashMap<String, Object> setOperationMap) {
		if (setOperationMap == null || setOperationMap.isEmpty()) {
			return;
		}

		Map<String, Object> summaryBySetOperationKey = getCurrentSetOperationSummaryMap();

		HashMap<String, Map<String, Object>> participantQueryMaps = new HashMap<String, Map<String, Object>>();
		HashMap<String, Map<String, Object>> participantInterfaces = new HashMap<String, Map<String, Object>>();
		ArrayList<String> participantKeys = new ArrayList<String>();

		for (Map.Entry<String, Object> setEntry : setOperationMap.entrySet()) {
			String setEntryKey = setEntry.getKey();
			if (!isSetOperationParticipantScopeKey(setEntryKey)) {
				continue;
			}
			if (!(setEntry.getValue() instanceof Map<?, ?> queryMap)) {
				continue;
			}

			Object queryInterfaceObj = ((Map<String, Object>) queryMap).get(MUMBLE_INTERFACE_KEY);
			if (!(queryInterfaceObj instanceof Map<?, ?> queryInterfaceMap)) {
				continue;
			}

			participantKeys.add(setEntryKey);
			participantQueryMaps.put(setEntryKey, (Map<String, Object>) queryMap);
			participantInterfaces.put(setEntryKey, (Map<String, Object>) queryInterfaceMap);
		}

		// Only compare true set-operation children (query/union/intersect/values branches) to each other.
		if (participantKeys.size() < 2) {
			return;
		}

		participantKeys.sort((left, right) -> {
			int leftIndex = extractTrailingNumericSuffix(left);
			int rightIndex = extractTrailingNumericSuffix(right);
			int indexCompare = Integer.compare(leftIndex, rightIndex);
			if (indexCompare != 0) {
				return indexCompare;
			}
			return left.compareTo(right);
		});

		String baselineKey = participantKeys.get(0);
		Map<String, Object> baselineInterface = participantInterfaces.get(baselineKey);
		if (baselineInterface == null || baselineInterface.isEmpty()) {
			return;
		}

		Map<String, Object> baselineQueryMap = participantQueryMaps.get(baselineKey);
		Map<String, Object> baselineQueryDictionary = extractQueryDictionary(baselineQueryMap);
		int expectedCount = baselineInterface.size();
		ColumnListSummary expectedSummary = withSetOperationAnchorFallback(
				buildColumnListSummary(baselineInterface, baselineQueryDictionary),
				setOperationMap);
		expectedSummary = withSetOperationSummaryAnchorFallback(
				expectedSummary,
				summaryBySetOperationKey,
				normalizeSetOperationScopeKey(baselineKey));

		String diagCode = getDiagnosticCode(DIAG_SQL_SET_OPERATION_INTERFACE_COLUMN_COUNT_MISMATCH);
		String diagTemplate = getDiagnosticMessage(DIAG_SQL_SET_OPERATION_INTERFACE_COLUMN_COUNT_MISMATCH);

		for (String setEntryKey : participantKeys) {
			Map<String, Object> queryMap = participantQueryMaps.get(setEntryKey);
			Map<String, Object> queryInterfaceMap = participantInterfaces.get(setEntryKey);
			if (queryMap == null || queryInterfaceMap == null) {
				continue;
			}

			String normalizedParticipantKey = normalizeSetOperationScopeKey(setEntryKey);
			String setOperationType = resolveSetOperationMismatchTypeFromParticipant(queryMap);
			if (setOperationType == null) {
				setOperationType = resolveSetOperationMismatchType(setOperationKey);
			}

			int actualCount = queryInterfaceMap.size();
			if (actualCount == expectedCount) {
				continue;
			}

			Map<String, Object> queryDictionary = extractQueryDictionary(queryMap);
			ColumnListSummary actualSummary = withSetOperationAnchorFallback(
					buildColumnListSummary(
							queryInterfaceMap,
							queryDictionary),
					setOperationMap);
			actualSummary = withSetOperationSummaryAnchorFallback(
					actualSummary,
					summaryBySetOperationKey,
					normalizedParticipantKey);
			ColumnListSummary effectiveExpectedSummary = buildExpectedSetOperationColumnSummary(
					setOperationMap,
					baselineInterface,
					expectedCount,
					setEntryKey);
			if (effectiveExpectedSummary.columnNamesCsv().isBlank()) {
				effectiveExpectedSummary = expectedSummary;
			}
			effectiveExpectedSummary = withSetOperationAnchorFallback(effectiveExpectedSummary, setOperationMap);
			effectiveExpectedSummary = withSetOperationSummaryAnchorFallback(
					effectiveExpectedSummary,
					summaryBySetOperationKey,
					normalizeSetOperationScopeKey(baselineKey));

			Integer expectedLine = effectiveExpectedSummary.anchorLine();
			Integer expectedChar = effectiveExpectedSummary.anchorChar();
			Integer actualLine = actualSummary.anchorLine();
			Integer actualChar = actualSummary.anchorChar();
			String expectedLineText = expectedLine == null ? "?" : String.valueOf(expectedLine);
			String expectedCharText = expectedChar == null ? "?" : String.valueOf(expectedChar);
			String actualLineText = actualLine == null ? "?" : String.valueOf(actualLine);
			String actualCharText = actualChar == null ? "?" : String.valueOf(actualChar);

			String diagMessage = (diagTemplate == null)
					? String.format(
							"%s has different column counts. Expected %s columns (%s) at (l:%s c:%s) but there were %s (%s) at (l:%s c:%s).",
							setOperationType,
							expectedCount,
							effectiveExpectedSummary.columnNamesCsv(),
							expectedLineText,
							expectedCharText,
							actualCount,
							actualSummary.columnNamesCsv(),
							actualLineText,
							actualCharText)
					: String.format(
							diagTemplate,
							setOperationType,
							expectedCount,
							effectiveExpectedSummary.columnNamesCsv(),
							expectedLineText,
							expectedCharText,
							actualCount,
							actualSummary.columnNamesCsv(),
							actualLineText,
							actualCharText);

			emitSetOperationMismatchFatalIfNew(
					setOperationType,
					diagCode,
					diagMessage,
					actualLine,
					actualChar,
					setEntryKey,
					expectedCount,
					effectiveExpectedSummary.columnNamesCsv(),
					expectedLine,
					expectedChar,
					actualCount,
					actualSummary.columnNamesCsv(),
					actualLine,
					actualChar);
		}
	}

	@SuppressWarnings("unchecked")
	private Map<String, Object> getCurrentSetOperationSummaryMap() {
		Object summaryObj = symbolTable.get(TEMP_SET_OPERATION_INTERFACE_SUMMARY_MAP_KEY);
		if (!(summaryObj instanceof Map<?, ?> summaryMapObj)) {
			return new HashMap<String, Object>();
		}
		return (Map<String, Object>) summaryMapObj;
	}

	private ColumnListSummary withSetOperationSummaryAnchorFallback(
			ColumnListSummary summary,
			Map<String, Object> summaryBySetOperationKey,
			String summaryKey) {
		if (summary == null) {
			return new ColumnListSummary("", null, null);
		}
		if (summary.anchorLine() != null && summary.anchorChar() != null) {
			return summary;
		}
		if (summaryBySetOperationKey == null || summaryBySetOperationKey.isEmpty() || summaryKey == null) {
			return summary;
		}

		String normalizedSummaryKey = normalizeSetOperationScopeKey(summaryKey);
		Map<String, Object> summaryMap = asSetOperationSummary(summaryBySetOperationKey.get(normalizedSummaryKey));
		if (summaryMap == null || summaryMap.isEmpty()) {
			return summary;
		}

		Integer anchorLine = getSetOperationSummaryInteger(summaryMap, SET_OPERATION_SUMMARY_LINE_KEY);
		Integer anchorChar = getSetOperationSummaryInteger(summaryMap, SET_OPERATION_SUMMARY_CHAR_KEY);
		if (anchorLine == null || anchorChar == null) {
			return summary;
		}

		return new ColumnListSummary(summary.columnNamesCsv(), anchorLine, anchorChar);
	}


	@SuppressWarnings("unchecked")
	private Map<String, Object> extractQueryDictionary(Map<String, Object> queryMap) {
		if (queryMap == null || queryMap.isEmpty()) {
			return new HashMap<String, Object>();
		}

		Object queryDictionaryObj = queryMap.get(MUMBLE_QUERY_DICTIONARY_KEY);
		if (queryDictionaryObj instanceof Map<?, ?> queryDictionary) {
			return (Map<String, Object>) queryDictionary;
		}

		return new HashMap<String, Object>();
	}

	private ColumnListSummary buildExpectedSetOperationColumnSummary(
			Map<String, Object> setOperationMap,
			Map<String, Object> interfaceMap,
			int expectedCount,
			String excludedSetEntryKey) {
		if (setOperationMap == null || setOperationMap.isEmpty()) {
			return buildColumnListSummary(interfaceMap, new HashMap<String, Object>());
		}

		ColumnListSummary bestSummary = null;
		for (Map.Entry<String, Object> setEntry : setOperationMap.entrySet()) {
			String setEntryKey = setEntry.getKey();
			if (setEntryKey == null || (excludedSetEntryKey != null && excludedSetEntryKey.equals(setEntryKey))) {
				continue;
			}
			if (!(setEntry.getValue() instanceof Map<?, ?> queryMapObj)) {
				continue;
			}

			Map<String, Object> queryMap = (Map<String, Object>) queryMapObj;
			Object queryInterfaceObj = queryMap.get(MUMBLE_INTERFACE_KEY);
			if (!(queryInterfaceObj instanceof Map<?, ?> queryInterfaceMapObj)) {
				continue;
			}

			Map<String, Object> queryInterfaceMap = (Map<String, Object>) queryInterfaceMapObj;
			if (queryInterfaceMap.size() != expectedCount) {
				continue;
			}

			Map<String, Object> queryDictionary = extractQueryDictionary(queryMap);
			ColumnListSummary candidateSummary = buildColumnListSummary(interfaceMap, queryDictionary);
			if (bestSummary == null || compareSummaryLocation(candidateSummary, bestSummary) < 0) {
				bestSummary = candidateSummary;
			}
		}

		if (bestSummary != null) {
			return bestSummary;
		}

		return buildColumnListSummary(interfaceMap, new HashMap<String, Object>());
	}

	private ColumnListSummary buildColumnListSummary(Map<String, Object> columnsMap, Map<String, Object> queryDictionary) {
		ArrayList<ColumnLocation> sortedColumns = new ArrayList<ColumnLocation>();
		if (columnsMap != null && !columnsMap.isEmpty()) {
			for (String columnName : columnsMap.keySet()) {
				Object columnEntry = queryDictionary == null ? null : queryDictionary.get(columnName);
				Integer[] location = getLineAndCharacterFromEntry(columnEntry);
				sortedColumns.add(new ColumnLocation(columnName, location[0], location[1]));
			}
		}

		sortedColumns.sort((left, right) -> {
			int lineCompare = compareNullableLocation(left.line(), right.line());
			if (lineCompare != 0) {
				return lineCompare;
			}

			int charCompare = compareNullableLocation(left.charPosition(), right.charPosition());
			if (charCompare != 0) {
				return charCompare;
			}

			String leftName = left.name() == null ? "" : left.name();
			String rightName = right.name() == null ? "" : right.name();
			return leftName.compareTo(rightName);
		});

		ArrayList<String> names = new ArrayList<String>();
		for (ColumnLocation column : sortedColumns) {
			names.add(column.name());
		}

		Integer anchorLine = null;
		Integer anchorChar = null;
		for (ColumnLocation column : sortedColumns) {
			if (column.line() != null && column.charPosition() != null) {
				anchorLine = column.line();
				anchorChar = column.charPosition();
				break;
			}
		}

		if ((anchorLine == null || anchorChar == null) && queryDictionary != null && !queryDictionary.isEmpty()) {
			Integer[] firstEntryLocation = getFirstEntryLineAndCharacter((HashMap<String, Object>) queryDictionary);
			anchorLine = firstEntryLocation[0];
			anchorChar = firstEntryLocation[1];
		}

		return new ColumnListSummary(String.join(", ", names), anchorLine, anchorChar);
	}

	private ColumnListSummary withSetOperationAnchorFallback(
			ColumnListSummary summary,
			Map<String, Object> setOperationMap) {
		if (summary == null) {
			return new ColumnListSummary("", null, null);
		}
		if (summary.anchorLine() != null && summary.anchorChar() != null) {
			return summary;
		}

		Integer explicitAnchorLine = getSetOperationSummaryInteger(
				setOperationMap,
				TEMP_SET_OPERATION_OPERATOR_ANCHOR_LINE_KEY);
		Integer explicitAnchorChar = getSetOperationSummaryInteger(
				setOperationMap,
				TEMP_SET_OPERATION_OPERATOR_ANCHOR_CHAR_KEY);
		if (explicitAnchorLine != null && explicitAnchorChar != null) {
			return new ColumnListSummary(summary.columnNamesCsv(), explicitAnchorLine, explicitAnchorChar);
		}

		Integer[] fallback = resolveSetOperationAnchorFromChildren(setOperationMap);
		if (fallback[0] == null || fallback[1] == null) {
			return summary;
		}

		return new ColumnListSummary(summary.columnNamesCsv(), fallback[0], fallback[1]);
	}

	@SuppressWarnings("unchecked")
	private Integer[] resolveSetOperationAnchorFromChildren(Map<String, Object> setOperationMap) {
		if (setOperationMap == null || setOperationMap.isEmpty()) {
			return new Integer[] { null, null };
		}

		ArrayList<Map.Entry<String, Object>> participantEntries = new ArrayList<Map.Entry<String, Object>>();
		for (Map.Entry<String, Object> setEntry : setOperationMap.entrySet()) {
			if (!isSetOperationParticipantScopeKey(setEntry.getKey())) {
				continue;
			}
			participantEntries.add(setEntry);
		}

		participantEntries.sort((left, right) -> {
			int leftIndex = extractTrailingNumericSuffix(normalizeSetOperationScopeKey(left.getKey()));
			int rightIndex = extractTrailingNumericSuffix(normalizeSetOperationScopeKey(right.getKey()));
			int indexCompare = Integer.compare(leftIndex, rightIndex);
			if (indexCompare != 0) {
				return indexCompare;
			}
			return left.getKey().compareTo(right.getKey());
		});

		for (Map.Entry<String, Object> participantEntry : participantEntries) {
			if (!(participantEntry.getValue() instanceof Map<?, ?> participantMapObj)) {
				continue;
			}

			Map<String, Object> participantMap = (Map<String, Object>) participantMapObj;
			Map<String, Object> queryDictionary = extractQueryDictionary(participantMap);
			if (queryDictionary != null && !queryDictionary.isEmpty()) {
				Integer[] location = getFirstEntryLineAndCharacter(new HashMap<String, Object>(queryDictionary));
				if (location[0] != null && location[1] != null) {
					return location;
				}
			}

			Object interfaceObj = participantMap.get(MUMBLE_INTERFACE_KEY);
			if (interfaceObj instanceof Map<?, ?> interfaceMapObj) {
				ColumnListSummary interfaceSummary = buildColumnListSummary(
						(Map<String, Object>) interfaceMapObj,
						queryDictionary);
				if (interfaceSummary.anchorLine() != null && interfaceSummary.anchorChar() != null) {
					return new Integer[] { interfaceSummary.anchorLine(), interfaceSummary.anchorChar() };
				}
			}
		}

		return new Integer[] { null, null };
	}

	private int compareSummaryLocation(ColumnListSummary left, ColumnListSummary right) {
		int lineCompare = compareNullableLocation(left.anchorLine(), right.anchorLine());
		if (lineCompare != 0) {
			return lineCompare;
		}

		return compareNullableLocation(left.anchorChar(), right.anchorChar());
	}

	private int compareNullableLocation(Integer left, Integer right) {
		if (left == null && right == null) {
			return 0;
		}
		if (left == null) {
			return 1;
		}
		if (right == null) {
			return -1;
		}
		return Integer.compare(left, right);
	}

	private record ColumnLocation(String name, Integer line, Integer charPosition) {
	}

	private record ColumnListSummary(String columnNamesCsv, Integer anchorLine, Integer anchorChar) {
	}

	private String normalizeSetOperationScopeKey(String scopeKey) {
		if (scopeKey == null || scopeKey.isBlank()) {
			return scopeKey;
		}
		if (scopeKey.startsWith("def_")) {
			return scopeKey.substring("def_".length());
		}
		return scopeKey;
	}

	private boolean isSetOperationScopeKey(String scopeKey) {
		if (scopeKey == null) {
			return false;
		}
		String normalizedScopeKey = normalizeSetOperationScopeKey(scopeKey);
		return normalizedScopeKey.startsWith(MUMBLE_UNION_KEY)
				|| normalizedScopeKey.startsWith(MUMBLE_INTERSECT_KEY);
	}

	private boolean isSetOperationParticipantScopeKey(String scopeKey) {
		if (scopeKey == null) {
			return false;
		}
		String normalizedScopeKey = normalizeSetOperationScopeKey(scopeKey);
		return normalizedScopeKey.startsWith(MUMBLE_QUERY_KEY)
				|| normalizedScopeKey.startsWith(MUMBLE_UNION_KEY)
				|| normalizedScopeKey.startsWith(MUMBLE_INTERSECT_KEY)
				|| normalizedScopeKey.startsWith(MUMBLE_VALUES_KEY);
	}

	private int extractTrailingNumericSuffix(String key) {
		if (key == null || key.isBlank()) {
			return -1;
		}

		String suffix = normalizeSetOperationScopeKey(key).replaceFirst("^[^0-9]+", "");
		if (suffix.isBlank()) {
			return -1;
		}

		try {
			return Integer.parseInt(suffix);
		} catch (NumberFormatException ex) {
			return -1;
		}
	}

	/*
	 * HELPER METHODS for SQL With Variable Substitutions
	 */

/**
 * Processes and Types Substitution Variables in the AST
 * -----------------------------------------------------
 * This method performs a critical role in handling parameterized SQL:
 * 
 * 1. Purpose:
 *    - Identifies and properly marks substitution variables (like <param> syntax)
 *    - Assigns semantic typing information based on context (e.g., column, predicand, condition)
 *    - Registers the variable in the global substitutionsMap for later reference
 * 
 * 2. Process Flow:
 *    - Checks if the passed subMap contains a substitution variable marker
 *    - If found, retrieves the substitution variable node
 *    - If the node doesn't already have type information, assigns the provided type
 *    - Updates the global substitutions registry with this variable and its type
 *    - Returns the modified map to preserve the AST structure
 * 
 * 3. Usage Context:
 *    - Called during AST construction whenever a node might contain a substitution variable
 *    - Invoked from various context-specific exit methods like exitColumn_reference, 
 *      exitValue_expression, exitPredicand_primary, etc.
 *    - Each caller provides the appropriate semantic type based on where the variable appears
 * 
 * 4. Types of Variables:
 *    - "column": Variable represents a column name
 *    - "predicand": Variable represents a value/expression
 *    - "condition": Variable represents an entire boolean condition
 *    - "tuple": Variable represents a table reference
 *    - "in_list": Variable represents a list of values for IN clauses
 * 
 * The method ensures consistent handling of variables across all SQL contexts,
 * enabling proper parameterized query support and semantic validation.
 * 
 * @param subMap The map structure that might contain a substitution variable
 * @param type The semantic type to assign to the variable based on context
 * @return The modified map structure with properly typed substitution variable
 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> checkForSubstitutionVariable(Map<String, Object> subMap, String type) {
		if (subMap.containsKey(getASTWALKER_SUBSTITUTION_KEY())) {
			Map<String, Object> hold = (Map<String, Object>) subMap.get(getASTWALKER_SUBSTITUTION_KEY());
			if (!hold.containsKey(getASTWALKER_TYPE_KEY())) {
				hold.put(getASTWALKER_TYPE_KEY(), type);
				substitutionsMap.put((String) hold.get("name"), type);
			}
		}
		return subMap;
	}

	/*
	 * This method adds all of the table and query column references in the symbol table
	 * to the corresponding dictionary map. The table dictionary map is a map of
	 * table references to their corresponding column definitions.
	 * 
	 * These references represent the INPUT columns used in the SQL statement context
	 * which the symbol table represents. The sum total of columns between the table and query dictionaries
	 * represent the full set of input columns for the SQL statement context which the symbol table represents.
	 * All of these must be defined and associated to a table or a query in the symbol table or else
	 * they represent undefined references which will cause execution-time errors.
	 * 
	 * The function also ensures that all table and column names are stored in a consistent format
	 * (lowercase) and that they are not duplicated.
	*/ 
	public void addQueryInputColumnsToTableDictionary() {
		HashMap<String, Object> hold = symbolTable;
		HashMap<String, Object> tableDictionary = null;
		if (hold.size() > 0) {
			for (String tab_ref : hold.keySet()) {
				if (!isTupleSubstitutionReference(tab_ref) && tab_ref != null && tab_ref.startsWith("<")) {
					continue;
				}
				if ((tab_ref.startsWith(MUMBLE_IN_LIST_KEY))
					|| (tab_ref.startsWith(MUMBLE_PREDICAND_KEY))
					|| (tab_ref.startsWith(MUMBLE_EXISTS_KEY))
					|| MUMBLE_DEPENDENT_QUERIES_KEY.equals(tab_ref)
					|| (tab_ref.startsWith("def_"))) {
						continue; // skip symbol table items that are not table or query references
				} else if ((tab_ref.startsWith(getASTWALKER_QUERY_KEY())) 
					|| (tab_ref.startsWith(getASTWALKER_UNION_KEY()))
					|| (tab_ref.startsWith(getASTWALKER_INTERSECT_KEY()))) {
					// Add nested query column references from the FROM-JOIN stmt to the query dictionary map
//					mergeDictionary(queryColumnDictionaryMap, tab_ref, hold);
				} else {
					// Add table references from any source in the query to the table dictionary map
					if (tableDictionary == null) {
						tableDictionary = getCurrentTableDictionary();
					}
					mergeDictionary(tableDictionary, tab_ref, hold);
				}
			}
		}
	}

    	/**
	 * Helper function to merge a reference entry into a dictionary map.
	 * Handles the "<" prefix logic and merging.
	 */
	@SuppressWarnings("unchecked")
	private void mergeDictionary(HashMap<String, Object> dictMap, String tab_ref, HashMap<String, Object> hold) {
		String reference = normalizeTableReference(tab_ref);
		HashMap<String, Object> currDict = (HashMap<String, Object>) dictMap.get(reference);
		Object value =  hold.get(tab_ref);
		if (!(value instanceof Map<?, ?>)) {
			return;
		}

		Map<String, Object> incoming = (Map<String, Object>) value;
		if (currDict != null) {
			if (currDict.containsKey("*")) {
				for (Map.Entry<String, Object> entry : incoming.entrySet()) {
					if (!"*".equals(entry.getKey())) {
						mergeColumnEntry(currDict, entry.getKey(), entry.getValue());
					}
				}
			} else {
				for (Map.Entry<String, Object> entry : incoming.entrySet()) {
					mergeColumnEntry(currDict, entry.getKey(), entry.getValue());
				}
			}
		}
		else {
			HashMap<String, Object> newDict = new HashMap<String, Object>();
			for (Map.Entry<String, Object> entry : incoming.entrySet()) {
				mergeColumnEntry(newDict, entry.getKey(), entry.getValue());
			}
			dictMap.put(reference, newDict);
		}
	}

	private boolean isNonTableQuerySource(String sourceRef) {
		if (sourceRef == null) {
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
				|| normalizedSourceRef.startsWith(MUMBLE_DELETE_KEY)
				|| MUMBLE_VALUES_KEY.equals(normalizedSourceRef);
	}

	public boolean isNonTableQuerySourceReference(String sourceRef) {
		return isNonTableQuerySource(sourceRef);
	}

	private String resolveDefinitionBackedNonTableSourceRef(String sourceRef) {
		if (!isNonTableQuerySource(sourceRef)) {
			return sourceRef;
		}

		if (symbolTable != null) {
			Object direct = symbolTable.get(sourceRef);
			if (direct instanceof Map<?, ?> || direct instanceof String) {
				return sourceRef;
			}

			String defSourceRef = sourceRef.startsWith("def_") ? sourceRef : "def_" + sourceRef;
			Object prefixed = symbolTable.get(defSourceRef);
			if (prefixed instanceof Map<?, ?> || prefixed instanceof String) {
				return defSourceRef;
			}
		}

		if (queryColumnDictionaryMap != null) {
			if (queryColumnDictionaryMap.containsKey(sourceRef)) {
				return sourceRef;
			}
			String defSourceRef = sourceRef.startsWith("def_") ? sourceRef : "def_" + sourceRef;
			if (queryColumnDictionaryMap.containsKey(defSourceRef)) {
				return defSourceRef;
			}
		}

		return sourceRef;
	}

	/**
	 * Limits table references to those visible at the current query level.
	 * Nested-only aliases declared inside def_query/def_union/def_intersect blocks are excluded.
	 */
	@SuppressWarnings("unchecked")
	public HashMap<String, Object> scopeTableCollectionToCurrentLevel(
			HashMap<String, Object> tableCollection,
			HashMap<String, Object> tableAliasCollection,
			HashMap<String, Object> symbolTableCollection) {
		if (tableCollection == null || tableCollection.isEmpty()) {
			return tableCollection;
		}

		HashSet<String> nestedAliasRefs = new HashSet<String>();
		if (symbolTableCollection != null) {
			for (Object definitionEntryObj : symbolTableCollection.values()) {
				collectNestedAliasKeysFromDefinitionEntry(definitionEntryObj, nestedAliasRefs);
			}
		}

		if (nestedAliasRefs.isEmpty()) {
			return tableCollection;
		}

		HashMap<String, Object> scoped = new HashMap<String, Object>();
		for (Map.Entry<String, Object> tableEntry : tableCollection.entrySet()) {
			String tableRef = tableEntry.getKey();
			boolean nestedOnlyAlias = setContainsIgnoreCase(nestedAliasRefs, tableRef)
					&& !mapContainsKeyIgnoreCase(tableAliasCollection, tableRef)
					&& !mapContainsValueIgnoreCase(tableAliasCollection, tableRef);

			if (!nestedOnlyAlias) {
				scoped.put(tableRef, tableEntry.getValue());
			}
		}

		return scoped;
	}

	/**
	 * Limits query references for validation during set operations (UNION/INTERSECT)
	 * to non-table query sources reachable from aliases at the current level.
	 */
	public HashMap<String, Object> scopeQueryCollectionForSetOperationValidation(
			HashMap<String, Object> queryCollection,
			HashMap<String, Object> tableAliasCollection) {
		if (queryCollection == null || queryCollection.isEmpty()) {
			return queryCollection;
		}

		if (tableAliasCollection == null || tableAliasCollection.isEmpty()) {
			return new HashMap<String, Object>();
		}

		HashMap<String, Object> scoped = new HashMap<String, Object>();
		for (Object aliasTargetObj : tableAliasCollection.values()) {
			if (!(aliasTargetObj instanceof String aliasTarget)) {
				continue;
			}

			if (!isNonTableQuerySource(aliasTarget)) {
				continue;
			}

			String queryKey = MUMBLE_VALUES_KEY.equals(aliasTarget) ? aliasTarget : aliasTarget;
			Object queryEntry = queryCollection.get(queryKey);
			if (queryEntry != null) {
				scoped.put(queryKey, queryEntry);
			}
		}

		return scoped;
	}

	@SuppressWarnings("unchecked")
	private void collectNestedAliasKeysFromDefinitionEntry(Object definitionEntryObj, HashSet<String> nestedAliasRefs) {
		if (!(definitionEntryObj instanceof Map<?, ?> definitionMap)) {
			return;
		}

		for (Map.Entry<?, ?> entry : definitionMap.entrySet()) {
			if (entry.getKey() instanceof String key && entry.getValue() instanceof String) {
				nestedAliasRefs.add(key);
			}
			if (entry.getValue() instanceof Map<?, ?>) {
				collectNestedAliasKeysFromDefinitionEntry(entry.getValue(), nestedAliasRefs);
			}
		}
	}

	private boolean setContainsIgnoreCase(HashSet<String> values, String candidate) {
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

	private boolean mapContainsValueIgnoreCase(HashMap<String, Object> map, String value) {
		if (map == null || value == null) {
			return false;
		}

		for (Object candidate : map.values()) {
			if (candidate instanceof String source && source.equalsIgnoreCase(value)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Get the first available line/character location from column-entry token values.
	 * This is used to anchor the top-level unresolved diagnostic location.
	 */
	public Integer[] getFirstEntryLineAndCharacter(HashMap<String, Object> columnEntries) {
		String firstTokenString = getFirstEntryTokenString(columnEntries);
		if (firstTokenString == null) {
			return new Integer[] { null, null };
		}
		return parseLineAndCharacterFromToken(firstTokenString);
	}

	/**
	 * Return the first token string found in column-entry values.
	 * Supports either token-string lists or direct string payloads.
	 */
	public String getFirstEntryTokenString(HashMap<String, Object> columnEntries) {
		if (columnEntries == null || columnEntries.isEmpty()) {
			return null;
		}

		for (Object value : columnEntries.values()) {
			if (value instanceof Map<?, ?> valueMap) {
				Object locationsObj = ((Map<String, Object>) valueMap).get("locations");
				if (locationsObj instanceof List<?> locationList && !locationList.isEmpty()) {
					Object first = locationList.get(0);
					if (first != null) {
						return first.toString();
					}
				}
			}
			if (value instanceof List<?> tokenList && !tokenList.isEmpty()) {
				Object first = tokenList.get(0);
				if (first != null) {
					return first.toString();
				}
			} else if (value instanceof String) {
				return value.toString();
			}
		}

		return null;
	}

	/**
	 * Parse ANTLR token text and extract trailing line/character values.
	 * Expected token suffix format is line:char (e.g., 2:45).
	 */
	public Integer[] parseLineAndCharacterFromToken(String tokenString) {
		if (tokenString == null) {
			return new Integer[] { null, null };
		}

		int lastComma = tokenString.lastIndexOf(',');
		if (lastComma < 0 || lastComma + 1 >= tokenString.length()) {
			return new Integer[] { null, null };
		}

		String lineAndChar = tokenString.substring(lastComma + 1).replaceAll("[^0-9:]", "");
		String[] parts = lineAndChar.split(":");
		if (parts.length != 2) {
			return new Integer[] { null, null };
		}

		try {
			Integer line = Integer.valueOf(parts[0]);
			Integer charPosition = Integer.valueOf(parts[1]);
			return new Integer[] { line, charPosition };
		} catch (NumberFormatException ex) {
			return new Integer[] { null, null };
		}
	}

	/**
	 * Resolve one column-entry token payload into a line/character pair.
	 * The entry may be a token list or a single token string.
	 */
	public Integer[] getLineAndCharacterFromEntry(Object entryValue) {
		if (entryValue instanceof Map<?, ?> entryMap) {
			Object locationsObj = ((Map<String, Object>) entryMap).get("locations");
			if (locationsObj instanceof List<?> locationList && !locationList.isEmpty()) {
				Object first = locationList.get(0);
				if (first != null) {
					return parseLineAndCharacterFromToken(first.toString());
				}
			}
		}
		if (entryValue instanceof List<?> tokenList && !tokenList.isEmpty()) {
			Object first = tokenList.get(0);
			if (first != null) {
				return parseLineAndCharacterFromToken(first.toString());
			}
		} else if (entryValue instanceof String) {
			return parseLineAndCharacterFromToken(entryValue.toString());
		}
		return new Integer[] { null, null };
	}

	/**
	 * Build a compact location list for one entry value, preserving token order.
	 * Example: [(l:1 c:74), (l:1 c:238)].
	 */
	@SuppressWarnings("unchecked")
	public String formatAllLocationsForEntry(Object entryValue) {
		ArrayList<Integer[]> parsedLocations = new ArrayList<Integer[]>();

		if (entryValue instanceof Map<?, ?> entryMap) {
			Object locationsObj = ((Map<String, Object>) entryMap).get("locations");
			if (locationsObj instanceof List<?> locationList) {
				for (Object locationObj : locationList) {
					if (locationObj == null) {
						continue;
					}
					Integer[] parsed = parseLineAndCharacterFromToken(locationObj.toString());
					if (parsed[0] == null || parsed[1] == null) {
						continue;
					}
					parsedLocations.add(parsed);
				}
			}
		} else if (entryValue instanceof List<?> tokenList) {
			for (Object tokenObj : tokenList) {
				if (tokenObj == null) {
					continue;
				}
				Integer[] parsed = parseLineAndCharacterFromToken(tokenObj.toString());
				if (parsed[0] == null || parsed[1] == null) {
					continue;
				}
				parsedLocations.add(parsed);
			}
		} else if (entryValue instanceof String tokenString) {
			Integer[] parsed = parseLineAndCharacterFromToken(tokenString);
			if (parsed[0] != null && parsed[1] != null) {
				parsedLocations.add(parsed);
			}
		}

		if (parsedLocations.isEmpty()) {
			return "[]";
		}

		parsedLocations.sort((a, b) -> {
			int lineCompare = Integer.compare(a[0], b[0]);
			if (lineCompare != 0) {
				return lineCompare;
			}
			return Integer.compare(a[1], b[1]);
		});

		ArrayList<String> formattedLocations = new ArrayList<String>();
		for (Integer[] parsed : parsedLocations) {
			String formatted = "(l:" + parsed[0] + " c:" + parsed[1] + ")";
			if (!formattedLocations.contains(formatted)) {
				formattedLocations.add(formatted);
			}
		}

		return formattedLocations.toString();
	}

	/**
	 * Same locations as formatAllLocationsForEntry, but rendered without brackets
	 * for embedding in sentence-based diagnostics.
	 */
	public String formatAllLocationsForEntryInline(Object entryValue) {
		String bracketed = formatAllLocationsForEntry(entryValue);
		if (bracketed == null || bracketed.length() < 2 || "[]".equals(bracketed)) {
			return "";
		}
		return bracketed.substring(1, bracketed.length() - 1);
	}

	public void captureQualifiedUnresolvedLocations(HashMap<String, Object> unresolvedColumnMap) {
		if (unresolvedColumnMap == null || unresolvedColumnMap.isEmpty()) {
			return;
		}

		for (Map.Entry<String, Object> unresolvedEntry : unresolvedColumnMap.entrySet()) {
			String unresolvedKey = unresolvedEntry.getKey();
			if (unresolvedKey == null || !unresolvedKey.contains(".")) {
				continue;
			}

			HashMap<String, Object> single = new HashMap<String, Object>();
			single.put(unresolvedKey, unresolvedEntry.getValue());
			mergeUnknownEntries(globalQualifiedUnresolvedLocations, single);
		}
	}

	public Object getCapturedQualifiedUnresolvedLocationEntry(String unresolvedQualifiedKey) {
		if (unresolvedQualifiedKey == null || globalQualifiedUnresolvedLocations == null) {
			return null;
		}
		return globalQualifiedUnresolvedLocations.get(unresolvedQualifiedKey);
	}

	/**
	 * Drops a qualified unresolved key from the statement-level position tracker once the
	 * reference has been resolved and consumed in the current scope. Prevents inner WITH
	 * CTE resolutions from polluting outer-scope fatal diagnostics for the same column name.
	 */
	public void releaseResolvedQualifiedGlobalLocation(String unresolvedQualifiedKey) {
		if (unresolvedQualifiedKey == null
				|| unresolvedQualifiedKey.isBlank()
				|| !unresolvedQualifiedKey.contains(".")
				|| globalQualifiedUnresolvedLocations == null
				|| globalQualifiedUnresolvedLocations.isEmpty()) {
			return;
		}

		if (globalQualifiedUnresolvedLocations.remove(unresolvedQualifiedKey) != null) {
			return;
		}

		for (String key : new ArrayList<String>(globalQualifiedUnresolvedLocations.keySet())) {
			if (key != null && key.equalsIgnoreCase(unresolvedQualifiedKey)) {
				globalQualifiedUnresolvedLocations.remove(key);
				return;
			}
		}
	}

	/**
	 * Build a compact column list with per-entry location annotations.
	 * Example: [c (l:1 c:43), doll (l:3 c:22)].
	 */
	public String formatColumnEntriesWithLocations(HashMap<String, Object> columnEntries) {
		if (columnEntries == null || columnEntries.isEmpty()) {
			return "[]";
		}

		ArrayList<String> formattedEntries = new ArrayList<String>();
		for (String columnName : columnEntries.keySet()) {
			String locationList = formatAllLocationsForEntry(columnEntries.get(columnName));
			if (!"[]".equals(locationList)) {
				formattedEntries.add(columnName + " " + locationList);
			} else {
				formattedEntries.add(columnName);
			}
		}

		return formattedEntries.toString();
	}

	/**
	 * Build a comma-separated list of entry keys for diagnostic tokenText fields.
	 * Example: c, doll, amount.
	 */
	public String formatEntryKeysAsCsv(HashMap<String, Object> entryMap) {
		if (entryMap == null || entryMap.isEmpty()) {
			return "";
		}
		return String.join(", ", entryMap.keySet());
	}

	@SuppressWarnings("unchecked")
	public String extractReferenceNameFromInterfaceEntry(Object refObj) {
		if (!(refObj instanceof Map<?, ?>)) {
			return null;
		}

		Map<String, Object> refMap = (Map<String, Object>) refObj;
		if (refMap.containsKey(MUMBLE_NAME_KEY)) {
			Object name = refMap.get(MUMBLE_NAME_KEY);
			return (name == null) ? null : name.toString();
		}

		if (refMap.containsKey(MUMBLE_COLUMN_KEY)) {
			Object columnObj = refMap.get(MUMBLE_COLUMN_KEY);
			if (columnObj instanceof Map<?, ?>) {
				Map<String, Object> columnMap = (Map<String, Object>) columnObj;
				Object name = columnMap.get(MUMBLE_NAME_KEY);
				return (name == null) ? null : name.toString();
			}
		}

		if (refMap.containsKey(MUMBLE_SUBSTITUTION_KEY)) {
			Object substitutionObj = refMap.get(MUMBLE_SUBSTITUTION_KEY);
			if (substitutionObj instanceof Map<?, ?>) {
				Map<String, Object> substitution = (Map<String, Object>) substitutionObj;
				Object name = substitution.get(MUMBLE_NAME_KEY);
				return (name == null) ? null : name.toString();
			}
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	public String extractReferenceTableRefFromInterfaceEntry(Object refObj) {
		if (!(refObj instanceof Map<?, ?>)) {
			return null;
		}
		Map<String, Object> refMap = (Map<String, Object>) refObj;
		Object tableRef = refMap.get(MUMBLE_TABLE_REF_KEY);
		if (tableRef == null) {
			return null;
		}
		String normalizedTableRef = tableRef.toString();
		return "*".equals(normalizedTableRef) ? null : normalizedTableRef;
	}

	@SuppressWarnings("unchecked")
	public String extractSubstitutionTypeFromInterfaceEntry(Object refObj) {
		if (!(refObj instanceof Map<?, ?>)) {
			return null;
		}
		Map<String, Object> refMap = (Map<String, Object>) refObj;
		Object substitutionObj = refMap.get(MUMBLE_SUBSTITUTION_KEY);
		if (substitutionObj instanceof Map<?, ?>) {
			Map<String, Object> substitution = (Map<String, Object>) substitutionObj;
			Object type = substitution.get(MUMBLE_TYPE_KEY);
			return (type == null) ? null : type.toString();
		}

		if (refMap.containsKey(MUMBLE_TABLE_REF_KEY)) {
			return null;
		}

		Object flattenedType = refMap.get(MUMBLE_TYPE_KEY);
		if (flattenedType != null) {
			return flattenedType.toString();
		}

		return null;
	}

	public String resolveAliasToTableName(String tableRef, HashMap<String, Object> tableAliasCollection) {
		if (tableRef == null) {
			return null;
		}
		if (tableAliasCollection == null) {
			return resolveDefinitionBackedNonTableSourceRef(tableRef);
		}
		Object mapped = tableAliasCollection.get(tableRef);
		if (mapped instanceof String) {
			return resolveDefinitionBackedNonTableSourceRef((String) mapped);
		}
		for (Map.Entry<String, Object> entry : tableAliasCollection.entrySet()) {
			if (entry.getKey().equalsIgnoreCase(tableRef) && entry.getValue() instanceof String) {
				return resolveDefinitionBackedNonTableSourceRef((String) entry.getValue());
			}
		}
		return resolveDefinitionBackedNonTableSourceRef(tableRef);
	}

	public boolean canResolveQualifiedUnknownInScope(
			String unresolvedQualifiedKey,
			HashMap<String, Object> tableAliasCollection,
			HashMap<String, Object> tableCollection,
			HashMap<String, Object> queryCollection) {
		if (unresolvedQualifiedKey == null || unresolvedQualifiedKey.isBlank()) {
			return false;
		}

		int dotIndex = unresolvedQualifiedKey.indexOf('.');
		if (dotIndex <= 0 || dotIndex + 1 >= unresolvedQualifiedKey.length()) {
			return false;
		}

		String sourceRef = unresolvedQualifiedKey.substring(0, dotIndex);

		boolean sourceIsQueryRef = isNonTableQuerySource(sourceRef);
		if (sourceIsQueryRef) {
			return true;
		}

		if (tableAliasCollection != null && !tableAliasCollection.isEmpty()) {
			Object aliasMappedObj = tableAliasCollection.get(sourceRef);
			if (!(aliasMappedObj instanceof String)) {
				for (Map.Entry<String, Object> aliasEntry : tableAliasCollection.entrySet()) {
					if (aliasEntry.getKey() != null
							&& aliasEntry.getKey().equalsIgnoreCase(sourceRef)
							&& aliasEntry.getValue() instanceof String) {
						aliasMappedObj = aliasEntry.getValue();
						break;
					}
				}
			}
			if (aliasMappedObj instanceof String aliasMappedSource) {
				boolean aliasTargetsQueryRef = isNonTableQuerySource(aliasMappedSource);
				if (aliasTargetsQueryRef) {
					return true;
				}
			}
		}

		String resolvedTableRef = resolveAliasToTableName(sourceRef, tableAliasCollection);
		HashMap<String, Object> indicatedTableDictionary = getTableDictionaryForReference(
				resolvedTableRef,
				tableCollection);
		if (indicatedTableDictionary != null) {
			return true;
		}

		// Bare physical table names (no alias) are visible once registered in the table collection.
		if (tableCollection != null && !tableCollection.isEmpty()) {
			for (Map.Entry<String, Object> tableEntry : tableCollection.entrySet()) {
				String tableKey = tableEntry.getKey();
				if (tableKey == null || isNonTableQuerySourceReference(tableKey)) {
					continue;
				}
				if (tableKey.equals(sourceRef) || tableKey.equalsIgnoreCase(sourceRef)) {
					return tableEntry.getValue() instanceof Map<?, ?>;
				}
			}
		}

		// A visible physical-table alias is sufficient even before column refs populate the dictionary.
		if (resolvedTableRef != null
				&& !resolvedTableRef.isBlank()
				&& !isNonTableQuerySourceReference(resolvedTableRef)
				&& tableAliasCollection != null
				&& !tableAliasCollection.isEmpty()) {
			if (tableAliasCollection.containsKey(sourceRef)) {
				return true;
			}
			for (String alias : tableAliasCollection.keySet()) {
				if (alias != null && alias.equalsIgnoreCase(sourceRef)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Like {@link #canResolveQualifiedUnknownInScope} but for physical table-backed refs requires
	 * the column to already exist in the indicated table dictionary (not merely the table entry).
	 */
	public boolean canFullyResolveQualifiedUnknownInScope(
			String unresolvedQualifiedKey,
			HashMap<String, Object> tableAliasCollection,
			HashMap<String, Object> tableCollection,
			HashMap<String, Object> queryCollection) {
		if (!canResolveQualifiedUnknownInScope(
				unresolvedQualifiedKey,
				tableAliasCollection,
				tableCollection,
				queryCollection)) {
			return false;
		}

		int dotIndex = unresolvedQualifiedKey.indexOf('.');
		if (dotIndex <= 0 || dotIndex + 1 >= unresolvedQualifiedKey.length()) {
			return false;
		}

		String sourceRef = unresolvedQualifiedKey.substring(0, dotIndex);
		String columnName = unresolvedQualifiedKey.substring(dotIndex + 1);

		if (isNonTableQuerySource(sourceRef)) {
			return true;
		}

		if (tableAliasCollection != null && !tableAliasCollection.isEmpty()) {
			Object aliasMappedObj = tableAliasCollection.get(sourceRef);
			if (!(aliasMappedObj instanceof String)) {
				for (Map.Entry<String, Object> aliasEntry : tableAliasCollection.entrySet()) {
					if (aliasEntry.getKey() != null
							&& aliasEntry.getKey().equalsIgnoreCase(sourceRef)
							&& aliasEntry.getValue() instanceof String) {
						aliasMappedObj = aliasEntry.getValue();
						break;
					}
				}
			}
			if (aliasMappedObj instanceof String aliasMappedSource
					&& isNonTableQuerySource(aliasMappedSource)) {
				return true;
			}
		}

		String resolvedTableRef = resolveAliasToTableName(sourceRef, tableAliasCollection);
		if (resolvedTableRef != null && isNonTableQuerySourceReference(resolvedTableRef)) {
			return true;
		}

		HashMap<String, Object> indicatedTableDictionary = getTableDictionaryForReference(
				resolvedTableRef,
				tableCollection);
		if (indicatedTableDictionary == null) {
			return false;
		}
		if (indicatedTableDictionary.containsKey(columnName)) {
			return true;
		}
		for (String key : indicatedTableDictionary.keySet()) {
			if (key != null && key.equalsIgnoreCase(columnName)) {
				return true;
			}
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	public HashMap<String, Object> getTableDictionaryForReference(String tableRef,
			HashMap<String, Object> tableCollection) {
		if (tableRef == null || tableCollection == null) {
			return null;
		}
		Object direct = tableCollection.get(tableRef);
		if (direct instanceof HashMap<?, ?>) {
			return (HashMap<String, Object>) direct;
		}
		if (isNonTableQuerySource(tableRef) && !tableRef.startsWith("def_")) {
			Object prefixed = tableCollection.get("def_" + tableRef);
			if (prefixed instanceof HashMap<?, ?>) {
				return (HashMap<String, Object>) prefixed;
			}
		}
		Object lower = tableCollection.get(normalizeTableReference(tableRef));
		if (lower instanceof HashMap<?, ?>) {
			return (HashMap<String, Object>) lower;
		}
		return null;
	}

	/**
	 * Merges unknown-column maps, appending token lists when keys overlap.
	 */
	@SuppressWarnings("unchecked")
	public void mergeUnknownEntries(HashMap<String, Object> target, HashMap<String, Object> source) {
		if (target == null || source == null || source.isEmpty()) {
			return;
		}

		for (String key : source.keySet()) {
			if (key == null || key.isBlank()) {
				continue;
			}
			Object sourceValue = source.get(key);
			Object targetValue = target.get(key);

			if (targetValue == null) {
				target.put(key, sourceValue);
			} else if (targetValue instanceof Map<?, ?> targetMap && sourceValue instanceof Map<?, ?> sourceMap) {
				Object targetLocationsObj = ((Map<String, Object>) targetMap).get("locations");
				Object sourceLocationsObj = ((Map<String, Object>) sourceMap).get("locations");
				if (targetLocationsObj instanceof ArrayList<?> targetLocations
						&& sourceLocationsObj instanceof ArrayList<?> sourceLocations) {
					for (Object location : sourceLocations) {
						if (!((ArrayList<Object>) targetLocations).contains(location)) {
							((ArrayList<Object>) targetLocations).add(location);
						}
					}
				} else {
					target.put(key, sourceValue);
				}
			} else if (targetValue instanceof ArrayList<?> targetList && sourceValue instanceof ArrayList<?> sourceList) {
				((ArrayList<Object>) targetList).addAll((ArrayList<Object>) sourceList);
			} else {
				Object mergedEntry = mergeUnknownEntryValues(targetValue, sourceValue);
				if (mergedEntry != null) {
					target.put(key, mergedEntry);
				} else {
					target.put(key, sourceValue);
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	private Object mergeUnknownEntryValues(Object existingValue, Object incomingValue) {
		ArrayList<Object> mergedLocations = new ArrayList<Object>();
		collectUnknownEntryLocations(existingValue, mergedLocations);
		collectUnknownEntryLocations(incomingValue, mergedLocations);

		if (mergedLocations.isEmpty()) {
			return null;
		}

		HashMap<String, Object> merged = new HashMap<String, Object>();
		if (existingValue instanceof Map<?, ?> existingMap) {
			Object existingColumn = ((Map<String, Object>) existingMap).get(MUMBLE_COLUMN_KEY);
			if (existingColumn != null) {
				merged.put(MUMBLE_COLUMN_KEY, existingColumn);
			}
		}
		if (!merged.containsKey(MUMBLE_COLUMN_KEY) && incomingValue instanceof Map<?, ?> incomingMap) {
			Object incomingColumn = ((Map<String, Object>) incomingMap).get(MUMBLE_COLUMN_KEY);
			if (incomingColumn != null) {
				merged.put(MUMBLE_COLUMN_KEY, incomingColumn);
			}
		}
		merged.put("locations", mergedLocations);
		return merged;
	}

	@SuppressWarnings("unchecked")
	private void collectUnknownEntryLocations(Object entryValue, ArrayList<Object> mergedLocations) {
		if (entryValue == null || mergedLocations == null) {
			return;
		}

		if (entryValue instanceof Map<?, ?> entryMap) {
			Object locationsObj = ((Map<String, Object>) entryMap).get("locations");
			if (locationsObj instanceof List<?> locations) {
				for (Object location : locations) {
					if (location != null && !mergedLocations.contains(location)) {
						mergedLocations.add(location);
					}
				}
			}
			return;
		}

		if (entryValue instanceof List<?> tokenList) {
			for (Object location : tokenList) {
				if (location != null && !mergedLocations.contains(location)) {
					mergedLocations.add(location);
				}
			}
			return;
		}

		if (entryValue instanceof String tokenString) {
			if (!mergedLocations.contains(tokenString)) {
				mergedLocations.add(tokenString);
			}
		}
	}

	/**
	 * Partition unresolved columns into qualified and unqualified sets by checking
	 * reference entries from the query interface first, then the filter list.
	 * This method does not modify unresolved, interface, or filter inputs.
	 */
	@SuppressWarnings("unchecked")
	public void splitExplicitlyQualifiedUnknownEntriesFromUnqualified(
			HashMap<String, Object> unresolvedColumnMap,
			HashMap<String, Object> localInterface,
			Object filtersList,
			HashMap<String, Object> qualifiedUnknownEntries,
			HashMap<String, Object> unqualifiedUnknownEntries) {
		if (qualifiedUnknownEntries != null) {
			qualifiedUnknownEntries.clear();
		}
		if (unqualifiedUnknownEntries != null) {
			unqualifiedUnknownEntries.clear();
		}

		if (unresolvedColumnMap == null || unresolvedColumnMap.isEmpty()) {
			return;
		}

		// Pass 1: interface references
		if (localInterface != null) {
			for (Object refsObj : localInterface.values()) {
				if (!(refsObj instanceof List<?> refs)) {
					continue;
				}
				for (Object refObj : refs) {
					if (!(refObj instanceof Map<?, ?>)) {
						continue;
					}
					Map<String, Object> interfaceRefMap = (Map<String, Object>) refObj;
					String columnName = extractReferenceNameFromInterfaceEntry(interfaceRefMap);
					if (columnName == null) {
						continue;
					}

					String interfaceTableRef = extractReferenceTableRefFromInterfaceEntry(interfaceRefMap);
					String unresolvedKey = (interfaceTableRef == null)
							? columnName
							: interfaceTableRef + "." + columnName;

					Object unresolvedLocations = unresolvedColumnMap.get(unresolvedKey);
					if (unresolvedLocations == null) {
						unresolvedLocations = unresolvedColumnMap.get(columnName);
					}
					if (unresolvedLocations == null) {
						continue;
					}

					Object locationList = unresolvedLocations;
					if (unresolvedLocations instanceof Map<?, ?> unresolvedEntryMap) {
						Object extractedLocations = ((Map<String, Object>) unresolvedEntryMap).get("locations");
						if (extractedLocations != null) {
							locationList = extractedLocations;
						}
					}

					HashMap<String, Object> resultEntry = new HashMap<String, Object>();
					resultEntry.put(MUMBLE_COLUMN_KEY, interfaceRefMap);
					resultEntry.put("locations", locationList);

					String tableRef = interfaceTableRef;
					if (tableRef == null) {
						if (unqualifiedUnknownEntries != null) {
							unqualifiedUnknownEntries.put(columnName, resultEntry);
						}
					} else {
						if (qualifiedUnknownEntries != null) {
							qualifiedUnknownEntries.put(columnName, resultEntry);
						}
					}
				}
			}
		}

		// Pass 2: filter references
		if (filtersList instanceof List<?> filters) {
			for (Object filterObj : filters) {
				if (!(filterObj instanceof Map<?, ?>)) {
					continue;
				}
				Map<String, Object> filterEntry = (Map<String, Object>) filterObj;
				String columnName = extractReferenceNameFromInterfaceEntry(filterEntry);
				if (columnName == null) {
					continue;
				}

				String tableRef = extractReferenceTableRefFromInterfaceEntry(filterEntry);
				String unresolvedKey = (tableRef == null)
						? columnName
						: tableRef + "." + columnName;
				Object unresolvedLocations = unresolvedColumnMap.get(unresolvedKey);
				if (unresolvedLocations == null) {
					unresolvedLocations = unresolvedColumnMap.get(columnName);
				}
				if (unresolvedLocations == null) {
					continue;
				}

				Object locationList = unresolvedLocations;
				if (unresolvedLocations instanceof Map<?, ?> unresolvedEntryMap) {
					Object extractedLocations = ((Map<String, Object>) unresolvedEntryMap).get("locations");
					if (extractedLocations != null) {
						locationList = extractedLocations;
					}
				}

				HashMap<String, Object> resultEntry = new HashMap<String, Object>();
				resultEntry.put(MUMBLE_COLUMN_KEY, filterEntry);
				resultEntry.put("locations", locationList);

				if (tableRef == null) {
					if (unqualifiedUnknownEntries != null) {
						unqualifiedUnknownEntries.put(columnName, resultEntry);
					}
				} else {
					if (qualifiedUnknownEntries != null) {
						qualifiedUnknownEntries.put(columnName, resultEntry);
					}
				}
			}
		}
	}

	// @SuppressWarnings("unchecked")
	// public HashMap<String, Object> flattenUnresolvedColumnMap(HashMap<String, Object> unresolvedMap) {
	// 	if (unresolvedMap == null || unresolvedMap.isEmpty()) {
	// 		return unresolvedMap;
	// 	}

	// 	HashMap<String, Object> flattened = new HashMap<String, Object>();
	// 	for (String key : unresolvedMap.keySet()) {
	// 		Object value = unresolvedMap.get(key);

	// 		if ((MUMBLE_UNRESOLVED_COLUMN_KEY.equals(key) || MUMBLE_UNKNOWN_KEY.equals(key))
	// 				&& value instanceof HashMap<?, ?>) {
	// 			mergeUnknownEntries(flattened, (HashMap<String, Object>) value);
	// 		} else {
	// 			flattened.put(key, value);
	// 		}
	// 	}

	// 	return flattened;
	// }

	/**
	 * Distributes UNKNOWN '*' references into table/query dictionaries using explicit source when present.
	 */
	@SuppressWarnings("unchecked")
	public void processWildcardUnknownEntries(
			HashMap<String, Object> unknownCollection,
			HashMap<String, Object> localInterface,
			HashMap<String, Object> tableAliasCollection,
			HashMap<String, Object> tableCollection,
			HashMap<String, Object> queryCollection) {
		HashMap<String, Object> tableDictionary = getCurrentTableDictionary();
		if (unknownCollection == null || !unknownCollection.containsKey("*")) {
			return;
		}

		Object wildcardRefs = unknownCollection.remove("*");
		if (wildcardRefs == null) {
			return;
		}

		boolean countAggregateWildcard = isCountAggregateWildcard(wildcardRefs);

		HashSet<String> explicitWildcardSources = new HashSet<String>();
		if (localInterface != null) {
			for (Object refsObj : localInterface.values()) {
				if (!(refsObj instanceof ArrayList<?> refs)) {
					continue;
				}
				for (Object refObj : refs) {
					String refColumn = extractReferenceNameFromInterfaceEntry(refObj);
					String refTable = extractReferenceTableRefFromInterfaceEntry(refObj);
					if (!"*".equals(refColumn)) {
						continue;
					}
					if (refTable != null && !"*".equals(refTable)) {
						explicitWildcardSources.add(refTable);
					}
				}
			}
		}

		int mergedTargets = 0;
		if (!explicitWildcardSources.isEmpty()) {
			for (String explicitSourceRef : explicitWildcardSources) {
				String nonTableQueryKey = resolveAliasToNonTableSourceQueryKey(explicitSourceRef, queryCollection);
				if (nonTableQueryKey != null) {
					mergeColumnReferenceIntoQueryDictionary(nonTableQueryKey, "*", wildcardRefs);
					mergedTargets++;
					continue;
				}

				String resolvedTableRef = resolveAliasToTableName(explicitSourceRef, tableAliasCollection);
				if (resolvedTableRef == null) {
					resolvedTableRef = explicitSourceRef;
				}

				if (mergeColumnReferenceIntoTableDictionary(tableCollection, resolvedTableRef, "*", wildcardRefs)) {
					mergedTargets++;
					continue;
				}

				if (mergeColumnReferenceIntoTableDictionary(tableDictionary, resolvedTableRef, "*", wildcardRefs)) {
					mergedTargets++;
				}
			}
		} else {
			mergedTargets += mergeWildcardIntoAllDictionaryEntries(tableCollection, wildcardRefs);
			mergedTargets += mergeWildcardIntoAllDictionaryEntries(tableDictionary, wildcardRefs);
			if (!countAggregateWildcard) {
				mergedTargets += mergeWildcardIntoAllQueryDictionaryEntries(queryColumnDictionaryMap, wildcardRefs);
			}
		}

		if (mergedTargets <= 0) {
			unknownCollection.put("*", wildcardRefs);
		}
	}

	@SuppressWarnings("unchecked")
	private boolean isCountAggregateWildcard(Object wildcardRefs) {
		if (!(wildcardRefs instanceof Map<?, ?> wildcardEntry)) {
			return false;
		}
		Object columnObj = wildcardEntry.get(MUMBLE_COLUMN_KEY);
		if (!(columnObj instanceof Map<?, ?> columnMap)) {
			return false;
		}
		Object origin = ((Map<String, Object>) columnMap).get("origin");
		return "count_all_aggregate".equals(origin);
	}

	/**
	 * Resolves an alias to its non-table query key when it maps to query/values/union/intersect.
	 */
	@SuppressWarnings("unchecked")
	public String resolveAliasToNonTableSourceQueryKey(String aliasRef, HashMap<String, Object> queryCollection) {
		if (aliasRef == null) {
			return null;
		}

		if (queryCollection != null && !queryCollection.isEmpty()) {
			for (String queryKey : queryCollection.keySet()) {
				Object queryEntryObj = queryCollection.get(queryKey);
				if (!(queryEntryObj instanceof Map<?, ?> queryEntry)) {
					continue;
				}

				Object mappedObj = queryEntry.get(aliasRef);
				if (!(mappedObj instanceof String)) {
					for (Map.Entry<?, ?> querySubEntry : queryEntry.entrySet()) {
						if (querySubEntry.getKey() instanceof String key
								&& key.equalsIgnoreCase(aliasRef)
								&& querySubEntry.getValue() instanceof String) {
							mappedObj = querySubEntry.getValue();
							break;
						}
					}
				}
				if (!(mappedObj instanceof String mappedSource)) {
					continue;
				}

				boolean queryOrSetBackedAlias = isNonTableQuerySource(mappedSource);

				if (!queryOrSetBackedAlias) {
					continue;
				}

				String sourceQueryKey = MUMBLE_VALUES_KEY.equals(mappedSource) ? queryKey : mappedSource;
				return resolveDefinitionBackedNonTableSourceRef(sourceQueryKey);
			}
		}

		if (symbolTable != null && !symbolTable.isEmpty()) {
			Object directAliasObj = symbolTable.get(aliasRef);
			if (!(directAliasObj instanceof String)) {
				for (Map.Entry<String, Object> entry : symbolTable.entrySet()) {
					if (entry.getKey() != null && entry.getKey().equalsIgnoreCase(aliasRef)
							&& entry.getValue() instanceof String) {
						directAliasObj = entry.getValue();
						break;
					}
				}
			}

			if (directAliasObj instanceof String directAliasTarget) {
				boolean queryOrSetBackedAlias = isNonTableQuerySource(directAliasTarget);
				if (queryOrSetBackedAlias) {
					return resolveDefinitionBackedNonTableSourceRef(directAliasTarget);
				}
			}
		}

		return null;
	}

	/**
	 * Adds alias mappings for non-table sources into alias collection for downstream resolution.
	 */
	@SuppressWarnings("unchecked")
	public void mergeNonTableAliasMappingsIntoAliasCollection(
			HashMap<String, Object> queryCollection,
			HashMap<String, Object> tableAliasCollection) {

		boolean missingQueryInputs = (queryCollection == null) || ((queryCollection != null) && queryCollection.isEmpty());

		if (tableAliasCollection == null && missingQueryInputs) {
			return;
		}

		for (String queryKey : queryCollection.keySet()) {
			Object queryEntryObj = queryCollection.get(queryKey);
			if (!(queryEntryObj instanceof Map<?, ?> queryEntry)) {
				continue;
			}

			for (Map.Entry<?, ?> querySubEntry : queryEntry.entrySet()) {
				if (!(querySubEntry.getKey() instanceof String aliasRef)
						|| !(querySubEntry.getValue() instanceof String mappedSource)) {
					continue;
				}

				boolean queryOrSetBackedAlias = mappedSource.startsWith("query")
						|| mappedSource.startsWith(MUMBLE_UNION_KEY)
						|| mappedSource.startsWith(MUMBLE_INTERSECT_KEY)
						|| mappedSource.startsWith(MUMBLE_VALUES_KEY)
						|| MUMBLE_VALUES_KEY.equals(mappedSource);

				if (!queryOrSetBackedAlias) {
					continue;
				}

				String sourceQueryKey = MUMBLE_VALUES_KEY.equals(mappedSource) ? queryKey : mappedSource;
				tableAliasCollection.put(aliasRef, sourceQueryKey);
			}
		}
	}

	/**
	 * Reclassifies alias-backed table entries into their resolved table/substitution targets.
	 * This is used when explicit refs (e.g. t3.col / t3.*) were collected before FROM aliases were known.
	 */
	@SuppressWarnings("unchecked")
	public void reconcileAliasBackedTableReferences(
			HashMap<String, Object> tableCollection,
			HashMap<String, Object> tableAliasCollection) {
				
		if (tableCollection == null || tableCollection.isEmpty() || tableAliasCollection == null
				|| tableAliasCollection.isEmpty()) {
			return;
		}

		ArrayList<String> candidateAliases = new ArrayList<String>(tableCollection.keySet());
		for (String aliasRef : candidateAliases) {
			Object aliasColumnsObj = tableCollection.get(aliasRef);
			if (!(aliasColumnsObj instanceof Map<?, ?> aliasColumns)) {
				continue;
			}

			String resolvedSource = resolveAliasToTableName(aliasRef, tableAliasCollection);
			if (resolvedSource == null || resolvedSource.equalsIgnoreCase(aliasRef)) {
				continue;
			}

			if (!isTupleSubstitutionReference(resolvedSource)) {
				continue;
			}

			boolean queryOrSetBackedAlias = resolvedSource.startsWith("query")
					|| resolvedSource.startsWith(MUMBLE_UNION_KEY)
					|| resolvedSource.startsWith(MUMBLE_INTERSECT_KEY)
					|| resolvedSource.startsWith(MUMBLE_VALUES_KEY)
					|| MUMBLE_VALUES_KEY.equals(resolvedSource);
			if (queryOrSetBackedAlias) {
				continue;
			}

			HashMap<String, Object> resolvedTableDictionary = getTableDictionaryForReference(resolvedSource, tableCollection);
			if (resolvedTableDictionary == null) {
				resolvedTableDictionary = new HashMap<String, Object>();
				tableCollection.put(resolvedSource, resolvedTableDictionary);
			}

			for (Map.Entry<?, ?> aliasColumnEntry : aliasColumns.entrySet()) {
				if (!(aliasColumnEntry.getKey() instanceof String columnName)) {
					continue;
				}
				mergeColumnEntry(resolvedTableDictionary, columnName, aliasColumnEntry.getValue());
			}

			tableCollection.remove(aliasRef);
		}
	}

	private boolean isTupleSubstitutionReference(String referenceName) {
		if (referenceName == null || substitutionsMap == null || substitutionsMap.isEmpty()) {
			return false;
		}

		Object substitutionType = substitutionsMap.get(referenceName);
		if (!(substitutionType instanceof String)) {
			return false;
		}

		return "tuple".equals(substitutionType);
	}

	/**
	 * Reclassifies explicit alias-qualified refs from table collection when alias points to non-table sources.
	 */
	@SuppressWarnings("unchecked")
	public void reconcileExplicitAliasReferencesAgainstNonTableSources(
			HashMap<String, Object> tableCollection,
			HashMap<String, Object> queryCollection,
			HashMap<String, Object> localInterface,
			HashMap<String, Object> pinnedUnknowns,
			HashSet<String> forcedUnknownExplicitRefs) {
		
		boolean missingTableInputs = (tableCollection == null) || ((tableCollection != null) && tableCollection.isEmpty());
		boolean missingQueryInputs = (queryCollection == null) || ((queryCollection != null) && queryCollection.isEmpty());

		if (missingTableInputs && missingQueryInputs) {
			return;
		}

		HashSet<String> explicitInterfaceRefs = new HashSet<String>();
		if (localInterface != null) {
			for (Object refsObj : localInterface.values()) {
				if (!(refsObj instanceof ArrayList<?> refs)) {
					continue;
				}
				for (Object refObj : refs) {
					String refColumn = extractReferenceNameFromInterfaceEntry(refObj);
					String refTable = extractReferenceTableRefFromInterfaceEntry(refObj);
					if (refColumn != null && refTable != null) {
						explicitInterfaceRefs.add(refTable + "." + refColumn);
					}
				}
			}
		}

		if (explicitInterfaceRefs.isEmpty()) {
			return;
		}

		// If there are explicit table reference and column entries, then we need to check if any of the 
		// explicit refs are qualified with an alias that maps to a non-table source.
		HashMap<String, HashSet<String>> nonTableAliasAvailableColumns = new HashMap<String, HashSet<String>>();
		HashMap<String, String> nonTableAliasSourceQueryKeys = new HashMap<String, String>();

		for (String queryKey : queryCollection.keySet()) {
			Object queryEntryObj = queryCollection.get(queryKey);
			if (!(queryEntryObj instanceof Map<?, ?> queryEntry)) {
				continue;
			}

			for (Map.Entry<?, ?> querySubEntry : queryEntry.entrySet()) {
				Object aliasObj = querySubEntry.getKey();
				Object mappedObj = querySubEntry.getValue();
				if (!(aliasObj instanceof String alias) || !(mappedObj instanceof String mappedSource)) {
					continue;
				}

				boolean queryOrSetBackedAlias = mappedSource.startsWith("query")
						|| mappedSource.startsWith(MUMBLE_UNION_KEY)
						|| mappedSource.startsWith(MUMBLE_INTERSECT_KEY)
						|| mappedSource.startsWith(MUMBLE_VALUES_KEY)
						|| MUMBLE_VALUES_KEY.equals(mappedSource);

				if (!queryOrSetBackedAlias) {
					continue;
				}

				HashSet<String> availableColumns = new HashSet<String>();
				String sourceQueryKey = MUMBLE_VALUES_KEY.equals(mappedSource) ? queryKey : mappedSource;
				Object sourceQueryDictObj = queryColumnDictionaryMap.get(sourceQueryKey);
				if (sourceQueryDictObj instanceof Map<?, ?> sourceQueryDict) {
					for (Object sourceColumnKey : sourceQueryDict.keySet()) {
						if (sourceColumnKey instanceof String) {
							availableColumns.add((String) sourceColumnKey);
						}
					}
				}

				if (availableColumns.isEmpty()) {
					Object interfaceObj = queryEntry.get(MUMBLE_INTERFACE_KEY);
					if (interfaceObj instanceof Map<?, ?> sourceInterface) {
						for (Object sourceColumnKey : sourceInterface.keySet()) {
							if (sourceColumnKey instanceof String) {
								availableColumns.add((String) sourceColumnKey);
							}
						}
					}
				}

				nonTableAliasAvailableColumns.put(alias, availableColumns);
				nonTableAliasSourceQueryKeys.put(alias, sourceQueryKey);
				if (sourceQueryKey != null) {
					nonTableAliasAvailableColumns.put(sourceQueryKey, availableColumns);
					nonTableAliasSourceQueryKeys.put(sourceQueryKey, sourceQueryKey);
				}
			}
		}

		if (nonTableAliasAvailableColumns.isEmpty()) {
			return;
		}

		// For each alias that maps to a non-table source, we need to check if any explicit refs are 
		// qualified with that alias.
		ArrayList<String> candidateAliases = new ArrayList<String>(tableCollection.keySet());
		for (String aliasRef : candidateAliases) {
			if (!nonTableAliasAvailableColumns.containsKey(aliasRef)) {
				continue;
			}

			Object aliasColumnsObj = tableCollection.get(aliasRef);
			if (!(aliasColumnsObj instanceof Map<?, ?> aliasColumns)) {
				continue;
			}

			HashSet<String> availableColumns = nonTableAliasAvailableColumns.get(aliasRef);
			String targetQueryKey = nonTableAliasSourceQueryKeys.get(aliasRef);
			ArrayList<String> columnsToRemove = new ArrayList<String>();
			for (Map.Entry<?, ?> aliasColumnEntry : aliasColumns.entrySet()) {
				if (!(aliasColumnEntry.getKey() instanceof String columnName)) {
					continue;
				}

				String explicitRefKey = aliasRef + "." + columnName;
				if (!explicitInterfaceRefs.contains(explicitRefKey)) {
					continue;
				}

				if (availableColumns.contains(columnName) && targetQueryKey != null) {
					mergeColumnReferenceIntoQueryDictionary(targetQueryKey, columnName, aliasColumnEntry.getValue());
				} else {
					HashMap<String, Object> singleUnknownEntry = new HashMap<String, Object>();
					singleUnknownEntry.put(columnName, aliasColumnEntry.getValue());
					mergeUnknownEntries(pinnedUnknowns, singleUnknownEntry);
					forcedUnknownExplicitRefs.add(explicitRefKey);
				}

				columnsToRemove.add(columnName);
			}

			// Remove all explicit refs from the alias column map, leaving only non-explicit refs if they exist.
			for (String columnToRemove : columnsToRemove) {
				((Map<String, Object>) aliasColumns).remove(columnToRemove);
			}

			// If no non-explicit refs remain for the alias, then we can remove the alias entry from the table collection.
			if (((Map<String, Object>) aliasColumns).isEmpty()) {
				tableCollection.remove(aliasRef);
			}
		}
	}

	/**
	 * Merges explicit column references into a query dictionary entry.
	 */
	@SuppressWarnings("unchecked")
	public void mergeColumnReferenceIntoQueryDictionary(String queryKey, String columnName, Object columnRefs) {
		if (queryKey == null || columnName == null || columnRefs == null) {
			return;
		}

		Object queryDictObj = queryColumnDictionaryMap.get(queryKey);
		HashMap<String, Object> queryDict;
		if (queryDictObj instanceof HashMap<?, ?>) {
			queryDict = (HashMap<String, Object>) queryDictObj;
		} else {
			queryDict = new HashMap<String, Object>();
			queryColumnDictionaryMap.put(queryKey, queryDict);
		}

		Object existingRefs = queryDict.get(columnName);
		if (existingRefs == null) {
			queryDict.put(columnName, columnRefs);
		} else if ("*".equals(columnName)) {
			return;
		} else if (existingRefs instanceof ArrayList<?> existingList && columnRefs instanceof ArrayList<?> incomingList) {
			((ArrayList<Object>) existingList).addAll((ArrayList<Object>) incomingList);
		} else {
			queryDict.put(columnName, columnRefs);
		}
	}

	/**
	 * Merges wildcard refs into each dictionary entry of a table-like dictionary map.
	 */
	@SuppressWarnings("unchecked")
	private int mergeWildcardIntoAllDictionaryEntries(HashMap<String, Object> dictionaryCollection, Object wildcardRefs) {
		if (dictionaryCollection == null || dictionaryCollection.isEmpty()) {
			return 0;
		}

		int merged = 0;
		for (Map.Entry<String, Object> entry : dictionaryCollection.entrySet()) {
			if (!(entry.getValue() instanceof HashMap<?, ?>)) {
				continue;
			}
			HashMap<String, Object> columnDictionary = (HashMap<String, Object>) entry.getValue();
			mergeColumnEntry(columnDictionary, "*", wildcardRefs);
			merged++;
		}
		return merged;
	}

	/**
	 * Merges wildcard refs into each query dictionary entry.
	 */
	@SuppressWarnings("unchecked")
	private int mergeWildcardIntoAllQueryDictionaryEntries(HashMap<String, Object> queryDictionaryMap, Object wildcardRefs) {
		if (queryDictionaryMap == null || queryDictionaryMap.isEmpty()) {
			return 0;
		}

		int merged = 0;
		for (Map.Entry<String, Object> entry : queryDictionaryMap.entrySet()) {
			HashMap<String, Object> queryDictionary;
			if (entry.getValue() instanceof HashMap<?, ?>) {
				queryDictionary = (HashMap<String, Object>) entry.getValue();
			} else {
				queryDictionary = new HashMap<String, Object>();
				entry.setValue(queryDictionary);
			}

			mergeColumnEntry(queryDictionary, "*", wildcardRefs);
			merged++;
		}
		return merged;
	}

	/**
	 * Merges a column entry into a specific table dictionary reference.
	 */
	@SuppressWarnings("unchecked")
	private boolean mergeColumnReferenceIntoTableDictionary(
			HashMap<String, Object> dictionaryCollection,
			String tableRef,
			String columnName,
			Object columnRefs) {
		if (dictionaryCollection == null || dictionaryCollection.isEmpty() || tableRef == null) {
			return false;
		}

		Object direct = dictionaryCollection.get(tableRef);
		if (direct instanceof HashMap<?, ?>) {
			mergeColumnEntry((HashMap<String, Object>) direct, columnName, columnRefs);
			return true;
		}

		Object lower = dictionaryCollection.get(normalizeTableReference(tableRef));
		if (lower instanceof HashMap<?, ?>) {
			mergeColumnEntry((HashMap<String, Object>) lower, columnName, columnRefs);
			return true;
		}

		return false;
	}

	/**
	 * Merges column token references into one dictionary column entry.
	 */
	@SuppressWarnings("unchecked")
	private void mergeColumnEntry(HashMap<String, Object> dictionary, String columnName, Object columnRefs) {
		if (dictionary == null || columnName == null || columnRefs == null) {
			return;
		}

		Object normalizedColumnRefs = normalizeColumnRefsForDictionary(columnRefs);
		if (normalizedColumnRefs == null) {
			return;
		}

		String matchedColumnKey = findMatchingColumnKey(dictionary, columnName);
		String targetColumnKey = matchedColumnKey == null ? columnName : matchedColumnKey;
		Object existingRefs = dictionary.get(targetColumnKey);
		if (existingRefs == null) {
			dictionary.put(targetColumnKey, normalizedColumnRefs);
		} else if ("*".equals(targetColumnKey)) {
			return;
		} else if (existingRefs instanceof ArrayList<?> existingList && normalizedColumnRefs instanceof ArrayList<?> incomingList) {
			ArrayList<Object> mutableExisting = (ArrayList<Object>) existingList;
			for (Object incomingToken : incomingList) {
				if (incomingToken != null && !mutableExisting.contains(incomingToken)) {
					mutableExisting.add(incomingToken);
				}
			}
		} else {
			dictionary.put(targetColumnKey, normalizedColumnRefs);
		}
	}

	public void mergeResolvedColumnIntoDictionary(HashMap<String, Object> dictionary, String columnName, Object columnRefs) {
		mergeColumnEntry(dictionary, columnName, columnRefs);
	}

	@SuppressWarnings("unchecked")
	private Object normalizeColumnRefsForDictionary(Object columnRefs) {
		if (columnRefs instanceof Map<?, ?> refsMap) {
			Object locationsObj = ((Map<String, Object>) refsMap).get("locations");
			if (locationsObj instanceof ArrayList<?>) {
				return locationsObj;
			}
			// Ignore structural refs (for example interface/filter objects) when
			// merging dictionary columns; dictionary entries should remain token lists.
			return null;
		}
		return columnRefs;
	}

	@SuppressWarnings("unchecked")
	public boolean tableCollectionContainsColumn(String columnName, HashMap<String, Object> tableCollection) {
		if (columnName == null || tableCollection == null || tableCollection.isEmpty()) {
			return false;
		}

		for (Object tableColumnsObj : tableCollection.values()) {
			if (tableColumnsObj instanceof Map<?, ?>) {
				Map<String, Object> tableColumns = (Map<String, Object>) tableColumnsObj;
				if (tableColumns.containsKey(columnName)) {
					return true;
				}
			}
		}

		return false;
	}

	@SuppressWarnings("unchecked")
	public boolean validateInterfaceColumn(String outputCol,
			HashMap<String, Object> localInterface,
			HashMap<String, Object> localCurrentQueryDictionary,
			HashMap<String, Object> tableAliasCollection,
			HashMap<String, Object> tableCollection) {
		if (localInterface == null || outputCol == null || !localInterface.containsKey(outputCol)) {
			return false;
		}

		Object refsObj = localInterface.get(outputCol);
		if (!(refsObj instanceof List<?> refs)) {
			return false;
		}

		return validateReferenceEntries(
				refs,
				tableAliasCollection,
				tableCollection,
				localCurrentQueryDictionary);
	}

	@SuppressWarnings("unchecked")
	public boolean validateReferenceEntries(List<?> refs,
			HashMap<String, Object> tableAliasCollection,
			HashMap<String, Object> tableCollection,
			HashMap<String, Object> localCurrentQueryDictionary) {
		if (refs == null) {
			return false;
		}

		if (refs.isEmpty()) {
			return true;
		}

		for (Object refObj : refs) {
			if (!(refObj instanceof Map<?, ?>)) {
				continue;
			}

			String substitutionType = extractSubstitutionTypeFromInterfaceEntry(refObj);
			if (substitutionType != null) {
				if (MUMBLE_COLUMN_KEY.equals(substitutionType) || MUMBLE_PREDICAND_KEY.equals(substitutionType)) {
					continue;
				}
				return false;
			}

			String columnName = extractReferenceNameFromInterfaceEntry(refObj);
			if (columnName == null || "*".equals(columnName)) {
				continue;
			}

			String tableRef = extractReferenceTableRefFromInterfaceEntry(refObj);
			boolean resolved;

			if (tableRef != null) {
				String resolvedTableRef = resolveAliasToTableName(tableRef, tableAliasCollection);
				String resolvedNonTableSourceRef = resolveAliasToNonTableSourceQueryKey(tableRef,
						localCurrentQueryDictionary);
				boolean explicitQueryReference = resolvedNonTableSourceRef != null
						|| isNonTableQuerySource(resolvedTableRef);

				if (explicitQueryReference) {
					String queryDictionaryKey = (resolvedNonTableSourceRef != null)
							? resolvedNonTableSourceRef
							: resolvedTableRef;
					resolved = queryDictionaryContainsColumn(queryDictionaryKey, columnName);
					if (!resolved && queryDictionaryKey != null
							&& (queryDictionaryKey.startsWith(MUMBLE_QUERY_KEY)
									|| queryDictionaryKey.startsWith(MUMBLE_UNION_KEY)
									|| queryDictionaryKey.startsWith(MUMBLE_INTERSECT_KEY)
									|| queryDictionaryKey.startsWith(MUMBLE_VALUES_KEY)
									|| MUMBLE_VALUES_KEY.equals(queryDictionaryKey))) {
						// Query-backed source dictionaries can be finalized after alias resolution;
						// treat these as resolvable at this stage.
						resolved = true;
					}
				} else {
					HashMap<String, Object> indicatedTableDictionary = getTableDictionaryForReference(resolvedTableRef,
							tableCollection);
					resolved = indicatedTableDictionary != null && indicatedTableDictionary.containsKey(columnName);
				}
			} else {
				ArrayList<String> sourceRefs = collectSourceReferencesForColumn(columnName,
						tableCollection,
						localCurrentQueryDictionary);
				resolved = !sourceRefs.isEmpty();
			}

			if (!resolved) {
				return false;
			}
		}

		return true;
	}

	public void validateQueryInterface(HashMap<String, Object> localInterface,
			HashMap<String, Object> localCurrentQueryDictionary,
			HashMap<String, Object> tableAliasCollection,
			HashMap<String, Object> tableCollection) {
		if (localInterface == null || localInterface.isEmpty()) {
			return;
		}

		for (String outputCol : localInterface.keySet()) {
			validateInterfaceColumn(outputCol,
					localInterface,
					localCurrentQueryDictionary,
					tableAliasCollection,
					tableCollection);
		}
	}

	public boolean validateFilterReferences(Object filtersList,
			HashMap<String, Object> localCurrentQueryDictionary,
			HashMap<String, Object> tableAliasCollection,
			HashMap<String, Object> tableCollection) {
		if (!(filtersList instanceof List<?> refs)) {
			return true;
		}

		return validateReferenceEntries(
				refs,
				tableAliasCollection,
				tableCollection,
				localCurrentQueryDictionary);
	}

	@SuppressWarnings("unchecked")
	private boolean queryDictionaryContainsColumn(String queryRef, String columnName) {
		if (queryRef == null || columnName == null || queryColumnDictionaryMap == null) {
			return false;
		}

		Object queryDictionaryObj = queryColumnDictionaryMap.get(queryRef);
		if (queryDictionaryObj instanceof Map<?, ?> queryDictionary) {
			return queryDictionary.containsKey(columnName)
					|| queryDictionary.containsKey("*");
		}

		Object queryDefinitionObject = symbolTable.get("def_" + queryRef);
		if (queryDefinitionObject instanceof Map<?, ?> queryDefinition) {
			Object interfaceObject = queryDefinition.get(MUMBLE_INTERFACE_KEY);
			if (interfaceObject instanceof Map<?, ?> queryInterface) {
				return queryInterface.containsKey(columnName)
						|| queryInterface.containsKey("*");
			}
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	private boolean containsColumnName(Object obj, String columnName) {
		if (obj == null || columnName == null) {
			return false;
		}

		if (obj instanceof String) {
			return columnName.equals(obj);
		}

		if (obj instanceof Map<?, ?> mapObj) {
			Map<String, Object> map = (Map<String, Object>) mapObj;

			if (map.containsKey(MUMBLE_COLUMN_KEY)) {
				Object columnObj = map.get(MUMBLE_COLUMN_KEY);
				if (columnObj instanceof Map<?, ?> columnMapObj) {
					Map<String, Object> columnMap = (Map<String, Object>) columnMapObj;
					Object name = columnMap.get(MUMBLE_NAME_KEY);
					if (columnName.equals(name)) {
						return true;
					}
				}
			}

			for (Object value : map.values()) {
				if (containsColumnName(value, columnName)) {
					return true;
				}
			}
			return false;
		}

		if (obj instanceof List<?> listObj) {
			for (Object value : listObj) {
				if (containsColumnName(value, columnName)) {
					return true;
				}
			}
		}

		return false;
	}

	@SuppressWarnings("unchecked")
	public String formatInterfaceValuesListWithLocations(HashMap<String, Object> localInterface,
			HashMap<String, Object> localCurrentQueryDictionary) {
		if (localInterface == null || localInterface.isEmpty()) {
			return "[]";
		}

		ArrayList<String> entries = new ArrayList<String>();
		for (String interfaceColumn : localInterface.keySet()) {
			Object interfaceValue = localInterface.get(interfaceColumn);
			String refs = formatInterfaceColumnReferences(interfaceValue);
			Integer[] location = (localCurrentQueryDictionary == null)
					? new Integer[] { null, null }
					: getLineAndCharacterFromEntry(localCurrentQueryDictionary.get(interfaceColumn));

			if (location[0] != null && location[1] != null) {
				entries.add(interfaceColumn + " <- " + refs + " (l:" + location[0] + " c:" + location[1] + ")");
			} else {
				entries.add(interfaceColumn + " <- " + refs);
			}
		}

		return entries.toString();
	}

	@SuppressWarnings("unchecked")
	public String formatInterfaceColumnReferences(Object interfaceValue) {
		if (!(interfaceValue instanceof ArrayList<?>)) {
			return "[]";
		}

		ArrayList<String> refs = new ArrayList<String>();
		for (Object refObj : (ArrayList<Object>) interfaceValue) {
			if (refObj instanceof Map<?, ?>) {
				Map<String, Object> refMap = (Map<String, Object>) refObj;
				if (refMap.containsKey(MUMBLE_COLUMN_KEY)) {
					Map<String, Object> columnMap = (Map<String, Object>) refMap.get(MUMBLE_COLUMN_KEY);
					Object tableRef = columnMap.get(MUMBLE_TABLE_REF_KEY);
					Object columnName = columnMap.get(MUMBLE_NAME_KEY);
					if (columnName != null && tableRef != null) {
						refs.add(tableRef + "." + columnName);
					} else if (columnName != null) {
						refs.add(columnName.toString());
					} else {
						refs.add(columnMap.toString());
					}
				} else if (refMap.containsKey(MUMBLE_SUBSTITUTION_KEY)) {
					Map<String, Object> substitution = (Map<String, Object>) refMap.get(MUMBLE_SUBSTITUTION_KEY);
					Object name = substitution.get(MUMBLE_NAME_KEY);
					refs.add((name == null) ? substitution.toString() : ("<" + name + ">"));
				} else {
					refs.add(refMap.toString());
				}
			} else {
				refs.add(String.valueOf(refObj));
			}
		}

		return refs.toString();
	}

	@SuppressWarnings("unchecked")
	public ArrayList<String> collectSourceReferencesForColumn(String columnName,
			HashMap<String, Object> tableCollection,
			HashMap<String, Object> queryCollection) {
		ArrayList<String> matches = new ArrayList<String>();
		if (columnName == null || "*".equals(columnName)) {
			return matches;
		}

		HashSet<String> visibleQueryKeys = null;
		if (queryCollection != null) {
			visibleQueryKeys = collectVisibleQueryKeys(queryCollection);
		}

		if (tableCollection != null) {
			for (Map.Entry<String, Object> entry : tableCollection.entrySet()) {
				Object dictionaryObj = entry.getValue();
				if (dictionaryObj instanceof Map<?, ?> dictionary
						&& mapContainsKeyIgnoreCase(dictionary, columnName)) {
					matches.add(entry.getKey());
				}
			}
		}

		if (queryCollection != null) {
			for (Map.Entry<String, Object> entry : queryCollection.entrySet()) {
				Object queryObj = entry.getValue();
				if (!(queryObj instanceof Map<?, ?> queryMapObj)) {
					continue;
				}
				Map<String, Object> queryMap = (Map<String, Object>) queryMapObj;

				if (mapContainsKeyIgnoreCase(queryMap, columnName)) {
					if (!matches.contains(entry.getKey())) {
						matches.add(entry.getKey());
					}
					continue;
				}

				Object queryInterfaceObj = queryMap.get(MUMBLE_INTERFACE_KEY);
				if (queryInterfaceObj instanceof Map<?, ?> queryInterface
						&& mapContainsKeyIgnoreCase(queryInterface, columnName)) {
					if (!matches.contains(entry.getKey())) {
						matches.add(entry.getKey());
					}
				}
			}
		}

		if (queryColumnDictionaryMap != null) {
			for (Map.Entry<String, Object> entry : queryColumnDictionaryMap.entrySet()) {
				if (visibleQueryKeys != null && !visibleQueryKeys.contains(entry.getKey())) {
					continue;
				}
				Object queryDictObj = entry.getValue();
				if (queryDictObj instanceof Map<?, ?> queryDict
						&& (mapContainsKeyIgnoreCase(queryDict, columnName)
								|| mapContainsKeyIgnoreCase(queryDict, "*"))) {
					if (!matches.contains(entry.getKey())) {
						matches.add(entry.getKey());
					}
				}
			}
		}

		return matches;
	}

	@SuppressWarnings("unchecked")
	private HashSet<String> collectVisibleQueryKeys(HashMap<String, Object> queryCollection) {
		HashSet<String> visible = new HashSet<String>();
		if (queryCollection == null || queryCollection.isEmpty()) {
			return visible;
		}

		for (String queryKey : queryCollection.keySet()) {
			if (isNonTableQuerySource(queryKey)) {
				visible.add(queryKey);
			}

			Object queryObj = queryCollection.get(queryKey);
			if (!(queryObj instanceof Map<?, ?> queryMapObj)) {
				continue;
			}

			Map<String, Object> queryMap = (Map<String, Object>) queryMapObj;
			for (Object mappedObj : queryMap.values()) {
				if (!(mappedObj instanceof String mappedSource)) {
					continue;
				}

				if (!isNonTableQuerySource(mappedSource)) {
					continue;
				}

				if (MUMBLE_VALUES_KEY.equals(mappedSource)) {
					visible.add(queryKey);
				} else {
					visible.add(mappedSource);
				}
			}
		}

		return visible;
	}

	private boolean mapContainsKeyIgnoreCase(Map<?, ?> map, String key) {
		if (map == null || key == null) {
			return false;
		}

		if (map.containsKey(key)) {
			return true;
		}

		for (Object candidateKey : map.keySet()) {
			if (candidateKey instanceof String candidate && candidate.equalsIgnoreCase(key)) {
				return true;
			}
		}

		return false;
	}


}
