package astwalkers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.antlr.v4.runtime.Token;

import static mumble.MumbleConstants.*;

import static mumble.ASTWalkerHelperConstants.*;

public final class SqlASTWalkerHelper extends AbstractASTWalkerHelper {
		public static final String DIAG_SQL_UNRESOLVED_UNKNOWN_COLUMNS = "SQL_UNRESOLVED_UNKNOWN_COLUMNS";
		public static final String DIAG_SQL_INTERFACE_COLUMN_UNRESOLVED = "SQL_INTERFACE_COLUMN_UNRESOLVED";
		public static final String DIAG_SQL_REFERENCED_COLUMN_NOT_FOUND_IN_TABLE = "SQL_REFERENCED_COLUMN_NOT_FOUND_IN_TABLE";
		public static final String DIAG_SQL_REFERENCED_COLUMN_NOT_FOUND_IN_QUERY = "SQL_REFERENCED_COLUMN_NOT_FOUND_IN_QUERY";
		public static final String DIAG_SQL_UNKNOWN_IMPLICIT_COLUMN_REFERENCE = "SQL_UNKNOWN_IMPLICIT_COLUMN_REFERENCE";
		public static final String DIAG_SQL_AMBIGUOUS_COLUMN_REFERENCE = "SQL_AMBIGUOUS_COLUMN_REFERENCE";

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
         initializeAstKeyCrosswalkMap();
		 initializeSqlDiagnosticCatalog();

     }  

	 private void initializeSqlDiagnosticCatalog() {
		 // SQL-local extension: register walker-specific diagnostic code/message template.
		 registerDiagnostic(
				 DIAG_SQL_UNRESOLVED_UNKNOWN_COLUMNS,
				 "UNRESOLVED_UNKNOWN_COLUMNS",
				 "Unresolved column reference(s) remain in UNKNOWN scope after symbol resolution: %s");
		 registerDiagnostic(
				 DIAG_SQL_INTERFACE_COLUMN_UNRESOLVED,
				 "INTERFACE_COLUMN_UNRESOLVED",
				 "Output column '%s' at (l:%s c:%s) has unresolved source reference(s): %s");
		 registerDiagnostic(
				 DIAG_SQL_REFERENCED_COLUMN_NOT_FOUND_IN_TABLE,
				 "REFERENCED_COLUMN_NOT_FOUND_IN_TABLE",
				 "Referenced column '%s' at (l:%s c:%s) not found in indicated table '%s'.");
		 registerDiagnostic(
				 DIAG_SQL_REFERENCED_COLUMN_NOT_FOUND_IN_QUERY,
				 "REFERENCED_COLUMN_NOT_FOUND_IN_QUERY",
				 "Referenced column '%s' at (l:%s c:%s) not found in indicated query '%s'.");
		 registerDiagnostic(
				 DIAG_SQL_UNKNOWN_IMPLICIT_COLUMN_REFERENCE,
				 "UNKNOWN_IMPLICIT_COLUMN_REFERENCE",
				 "Unknown column reference '%s' at (l:%s c:%s). No matching column found in any table/query dictionary in scope.");
		 registerDiagnostic(
				 DIAG_SQL_AMBIGUOUS_COLUMN_REFERENCE,
				 "AMBIGUOUS_COLUMN_REFERENCE",
				 "Ambiguous column reference '%s' at (l:%s c:%s). Possible sources: %s");
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

		pushStack("flagMapTable", flagMap);

		// Reset Flags
		unionClauseFound = false;
		firstUnionClause = false;
		intersectClauseFound = false;
		firstIntersectClause = false;
		useAsLeaf = false;
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
	public void collectSymbolTable(String alias, Object tableReference) {
		if (tableReference instanceof String) {
			// TODO: How does Symbol Table work?
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
	 * Add the Column reference or Values Column Reference to the Source Table Reference in the Symbol Table
	 * Unless the column is fully specified with its table reference, it gets added to the "UNKNOWN" table reference 
	 * in the Symbol Table and then gets moved to the correct table reference when the table reference is fully specified. 
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
	public void collectSymbolTableItem(Object tableReference, Object item, Token token) {
		if (tableReference instanceof String) {
			String tableReferenceKey = (String) tableReference;
			// Get existing Symbol Table element for the table reference
			Object refItem = symbolTable.get(tableReferenceKey);
			if (refItem == null) {
				String caseInsensitiveAliasKey = findSymbolTableAliasKeyIgnoreCase(tableReferenceKey);
				if (caseInsensitiveAliasKey != null) {
					tableReferenceKey = caseInsensitiveAliasKey;
					refItem = symbolTable.get(caseInsensitiveAliasKey);
				}
			}
			// If there is no existing Symbol Table element for the table reference, create an empty new one
			if (refItem instanceof String) {
                // If the table reference is a string, its an alias to an actual table reference. Retrieve the actual table reference 
				// from the Symbol Table and add the item to its symbol table.
                Object refItem2 = symbolTable.get((String) refItem);
				if (refItem2 == null) {
				    // tableReference has not been added to Symbol Table before
				    refItem2 = new HashMap<String, Object>();
				    symbolTable.put((String) refItem, refItem2);
			    }
				addItemToSymbolTable(refItem2, item, token);
			} else if (refItem == null) {
				// referenced table has not been added to Symbol Table before
				refItem = new HashMap<String, Object>();
				symbolTable.put(tableReferenceKey, refItem);
				addItemToSymbolTable(refItem, item, token);
			} else if (refItem instanceof HashMap<?, ?>) {
				// tableReference is new entry for existing table in the symbol table for this reference
				addItemToSymbolTable(refItem, item, token);
			}
		} else if (tableReference instanceof HashMap<?, ?>) {
			showTrace(symbolTrace, "Error collecting table: " + tableReference);
		}
	}

	private String findSymbolTableAliasKeyIgnoreCase(String tableReference) {
		if (tableReference == null || symbolTable == null || symbolTable.isEmpty()) {
			return null;
		}

		for (Map.Entry<String, Object> entry : symbolTable.entrySet()) {
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
	public void addItemToSymbolTable(Object tableDictObject, Object item, Token token) {
		if (item instanceof String) {
			HashMap<String, Object> tableDictMap = (HashMap<String, Object>) tableDictObject;
			// Item is a column reference, add it if we haven't captured it yet
			if (!tableDictMap.containsKey(item)) {
				ArrayList<String> tokenList = new ArrayList<String>();
				tokenList.add(token.toString());
				tableDictMap.put((String) item, tokenList);
			} else {
				String tokenStr = token.toString();
                Object  entry = tableDictMap.get((String) item);
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
	public void captureQueryInterface() {
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
			symbolTable.put("interface", newif);
		}
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
		if (hold.size() > 0) {
			for (String tab_ref : hold.keySet()) {
				if ((tab_ref.startsWith(MUMBLE_IN_LIST_KEY))
					|| (tab_ref.startsWith(MUMBLE_PREDICAND_KEY))
					|| (tab_ref.startsWith(MUMBLE_EXISTS_KEY))
					|| (tab_ref.startsWith("def_"))) {
						continue; // skip symbol table items that are not table or query references
				} else if ((tab_ref.startsWith(getASTWALKER_QUERY_KEY())) 
					|| (tab_ref.startsWith(getASTWALKER_UNION_KEY()))
					|| (tab_ref.startsWith(getASTWALKER_INTERSECT_KEY()))) {
					// Add nested query column references from the FROM-JOIN stmt to the query dictionary map
//					mergeDictionary(queryColumnDictionaryMap, tab_ref, hold);
				} else {
					// Add table references from any source in the query to the table dictionary map
					mergeDictionary(tableDictionaryMap, tab_ref, hold);
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
		String reference;
		if (tab_ref.startsWith("<"))
			reference = tab_ref;
		else
			reference = tab_ref.toLowerCase();
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
						currDict.put(entry.getKey(), entry.getValue());
					}
				}
			} else {
				currDict.putAll(incoming);
			}
		}
		else {
			HashMap<String, Object> newDict = new HashMap<String, Object>();
			newDict.putAll(incoming);
			dictMap.put(reference, newDict);
		}
	}

	/**
	 * Move column-entry mappings into the only table candidate when exactly one table exists.
	 * Returns true when the relocation is applied; otherwise false.
	 */
	@SuppressWarnings("unchecked")
	public boolean moveEntriesToSingleTableIfSingleTarget(HashMap<String, Object> columnEntries, HashMap<String, Object> tableCollection) {
		if (columnEntries == null || tableCollection == null || tableCollection.size() != 1) {
			return false;
		}

		String onlyTableName = tableCollection.keySet().iterator().next();
		Object tableSymbols = tableCollection.get(onlyTableName);
		if (!(tableSymbols instanceof HashMap<?, ?>)) {
			return false;
		}

		((HashMap<String, Object>) tableSymbols).putAll(columnEntries);
		return true;
	}

	/**
	 * Move unknown implicit column references to a single non-table source query
	 * when that source is wildcard-backed (for example derived table from SELECT *).
	 */
	@SuppressWarnings("unchecked")
	public boolean moveUnknownEntriesToSingleWildcardBackedNonTableSource(
			HashMap<String, Object> unknownCollection,
			HashMap<String, Object> queryCollection,
			HashMap<String, Object> tableAliasCollection) {
		if (unknownCollection == null || unknownCollection.isEmpty()
				|| queryCollection == null || queryCollection.isEmpty()) {
			return false;
		}

		HashSet<String> candidateQuerySources = new HashSet<String>();
		if (tableAliasCollection != null) {
			for (Object mappedSourceObj : tableAliasCollection.values()) {
				if (!(mappedSourceObj instanceof String mappedSource)) {
					continue;
				}
				if (isNonTableQuerySource(mappedSource) && queryCollection.containsKey(mappedSource)) {
					candidateQuerySources.add(mappedSource);
				}
			}
		}

		if (candidateQuerySources.isEmpty()) {
			for (String queryKey : queryCollection.keySet()) {
				if (isNonTableQuerySource(queryKey)) {
					candidateQuerySources.add(queryKey);
				}
			}
		}

		if (candidateQuerySources.size() != 1) {
			return false;
		}

		String targetQueryKey = candidateQuerySources.iterator().next();
		Object targetQueryObj = queryCollection.get(targetQueryKey);
		if (!(targetQueryObj instanceof HashMap<?, ?>)) {
			return false;
		}

		HashMap<String, Object> targetQueryMap = (HashMap<String, Object>) targetQueryObj;
		if (!isWildcardBackedQuerySource(targetQueryKey, targetQueryMap)) {
			return false;
		}

		for (Map.Entry<String, Object> unknownEntry : unknownCollection.entrySet()) {
			mergeColumnEntry(targetQueryMap, unknownEntry.getKey(), unknownEntry.getValue());
		}
		unknownCollection.clear();
		return true;
	}

	@SuppressWarnings("unchecked")
	private boolean isWildcardBackedQuerySource(String queryKey, HashMap<String, Object> queryEntry) {
		if (queryEntry != null && mapContainsKeyIgnoreCase(queryEntry, "*")) {
			return true;
		}

		if (queryEntry != null) {
			Object queryInterfaceObj = queryEntry.get(MUMBLE_INTERFACE_KEY);
			if (queryInterfaceObj instanceof Map<?, ?> queryInterface
					&& mapContainsKeyIgnoreCase((Map<String, Object>) queryInterface, "*")) {
				return true;
			}
		}

		if (queryKey != null && queryColumnDictionaryMap != null) {
			Object queryDictionaryObj = queryColumnDictionaryMap.get(queryKey);
			if (queryDictionaryObj instanceof Map<?, ?> queryDictionary
					&& mapContainsKeyIgnoreCase((Map<String, Object>) queryDictionary, "*")) {
				return true;
			}
		}

		return false;
	}

	private boolean isNonTableQuerySource(String sourceRef) {
		if (sourceRef == null) {
			return false;
		}
		return sourceRef.startsWith("query")
				|| sourceRef.startsWith(MUMBLE_UNION_KEY)
				|| sourceRef.startsWith(MUMBLE_INTERSECT_KEY)
				|| sourceRef.startsWith(MUMBLE_VALUES_KEY)
				|| MUMBLE_VALUES_KEY.equals(sourceRef);
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
	 * Build a compact column list with per-entry location annotations.
	 * Example: [c (l:1 c:43), doll (l:3 c:22)].
	 */
	public String formatColumnEntriesWithLocations(HashMap<String, Object> columnEntries) {
		if (columnEntries == null || columnEntries.isEmpty()) {
			return "[]";
		}

		ArrayList<String> formattedEntries = new ArrayList<String>();
		for (String columnName : columnEntries.keySet()) {
			Integer[] location = getLineAndCharacterFromEntry(columnEntries.get(columnName));
			if (location[0] != null && location[1] != null) {
				formattedEntries.add(columnName + " (l:" + location[0] + " c:" + location[1] + ")");
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
			return tableRef;
		}
		Object mapped = tableAliasCollection.get(tableRef);
		if (mapped instanceof String) {
			return (String) mapped;
		}
		for (Map.Entry<String, Object> entry : tableAliasCollection.entrySet()) {
			if (entry.getKey().equalsIgnoreCase(tableRef) && entry.getValue() instanceof String) {
				return (String) entry.getValue();
			}
		}
		return tableRef;
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
		Object lower = tableCollection.get(tableRef.toLowerCase());
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
			Object sourceValue = source.get(key);
			Object targetValue = target.get(key);

			if (targetValue == null) {
				target.put(key, sourceValue);
			} else if (targetValue instanceof ArrayList<?> targetList && sourceValue instanceof ArrayList<?> sourceList) {
				((ArrayList<Object>) targetList).addAll((ArrayList<Object>) sourceList);
			} else {
				target.put(key, sourceValue);
			}
		}
	}

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
		if (unknownCollection == null || !unknownCollection.containsKey("*")) {
			return;
		}

		Object wildcardRefs = unknownCollection.remove("*");
		if (wildcardRefs == null) {
			return;
		}

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

				if (mergeColumnReferenceIntoTableDictionary(tableDictionaryMap, resolvedTableRef, "*", wildcardRefs)) {
					mergedTargets++;
				}
			}
		} else {
			mergedTargets += mergeWildcardIntoAllDictionaryEntries(tableCollection, wildcardRefs);
			mergedTargets += mergeWildcardIntoAllDictionaryEntries(tableDictionaryMap, wildcardRefs);
			mergedTargets += mergeWildcardIntoAllQueryDictionaryEntries(queryColumnDictionaryMap, wildcardRefs);
		}

		if (mergedTargets <= 0) {
			unknownCollection.put("*", wildcardRefs);
		}
	}

	/**
	 * Resolves an alias to its non-table query key when it maps to query/values/union/intersect.
	 */
	@SuppressWarnings("unchecked")
	public String resolveAliasToNonTableSourceQueryKey(String aliasRef, HashMap<String, Object> queryCollection) {
		if (aliasRef == null || queryCollection == null || queryCollection.isEmpty()) {
			return null;
		}

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

			boolean queryOrSetBackedAlias = mappedSource.startsWith("query")
					|| mappedSource.startsWith(MUMBLE_UNION_KEY)
					|| mappedSource.startsWith(MUMBLE_INTERSECT_KEY)
					|| mappedSource.startsWith(MUMBLE_VALUES_KEY)
					|| MUMBLE_VALUES_KEY.equals(mappedSource);

			if (!queryOrSetBackedAlias) {
				continue;
			}

			return MUMBLE_VALUES_KEY.equals(mappedSource) ? queryKey : mappedSource;
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

		Object lower = dictionaryCollection.get(tableRef.toLowerCase());
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

		Object existingRefs = dictionary.get(columnName);
		if (existingRefs == null) {
			dictionary.put(columnName, columnRefs);
		} else if ("*".equals(columnName)) {
			return;
		} else if (existingRefs instanceof ArrayList<?> existingList && columnRefs instanceof ArrayList<?> incomingList) {
			((ArrayList<Object>) existingList).addAll((ArrayList<Object>) incomingList);
		} else {
			dictionary.put(columnName, columnRefs);
		}
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
			HashMap<String, Object> tableCollection,
			Object filtersList) {
		if (localInterface == null || outputCol == null || !localInterface.containsKey(outputCol)) {
			return false;
		}

		Object refsObj = localInterface.get(outputCol);
		if (!(refsObj instanceof List<?> refs)) {
			return false;
		}

		if (refs.isEmpty()) {
			return true;
		}

		for (Object refObj : refs) {
			if (!(refObj instanceof Map<?, ?>)) {
				continue;
			}

			Map<String, Object> refMap = (Map<String, Object>) refObj;
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
				if (resolvedTableRef != null
						&& (resolvedTableRef.startsWith("query")
								|| resolvedTableRef.startsWith(MUMBLE_VALUES_KEY)
								|| resolvedTableRef.startsWith(MUMBLE_UNION_KEY)
								|| resolvedTableRef.startsWith(MUMBLE_INTERSECT_KEY))) {
					resolved = queryDictionaryContainsColumn(resolvedTableRef, columnName);
				} else {
					HashMap<String, Object> indicatedTableDictionary = getTableDictionaryForReference(resolvedTableRef,
							tableCollection);
					resolved = indicatedTableDictionary != null && indicatedTableDictionary.containsKey(columnName);
				}
			} else {
				resolved = tableCollectionContainsColumn(columnName, tableCollection);
				if (!resolved) {
					ArrayList<String> queryMatches = collectSourceReferencesForColumn(columnName,
							null,
							null);
					resolved = !queryMatches.isEmpty();
				}
			}

			if (!resolved && containsColumnName(filtersList, columnName)) {
				resolved = true;
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
			HashMap<String, Object> tableCollection,
			Object filtersList) {
		if (localInterface == null || localInterface.isEmpty()) {
			return;
		}

		for (String outputCol : localInterface.keySet()) {
			validateInterfaceColumn(outputCol,
					localInterface,
					localCurrentQueryDictionary,
					tableAliasCollection,
					tableCollection,
					filtersList);
		}
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
