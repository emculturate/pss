package astwalkers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.antlr.v4.runtime.Token;

import static mumble.MumbleConstants.*;

import static mumble.ASTWalkerHelperConstants.*;

public final class SqlASTWalkerHelper extends AbstractASTWalkerHelper {
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
			// Get existing Symbol Table element for the table reference
			Object refItem = symbolTable.get((String) tableReference);
			// If there is no existing Symbol Table element for the table reference, create an empty new one
			if (refItem instanceof String) {
                // If the table reference is a string, its an alias to an actual table reference. Retrieve the actual table reference 
				// from the Symbol Table and add the item to its symbol table.
                Object refItem2 = symbolTable.get((String) refItem);
				if (refItem2 == null) {
				    // tableReference has not been added to Symbol Table before
				    refItem2 = new HashMap<String, Object>();
				    symbolTable.put((String) tableReference, refItem2);
			    }
				addItemToSymbolTable(refItem2, item, token);
			} else if (refItem == null) {
				// referenced table has not been added to Symbol Table before
				refItem = new HashMap<String, Object>();
				symbolTable.put((String) tableReference, refItem);
				addItemToSymbolTable(refItem, item, token);
			} else if (refItem instanceof HashMap<?, ?>) {
				// tableReference is new entry for existing table in the symbol table for this reference
				addItemToSymbolTable(refItem, item, token);
			}
		} else if (tableReference instanceof HashMap<?, ?>) {
			showTrace(symbolTrace, "Error collecting table: " + tableReference);
		}
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
		if (currDict != null)
			currDict.putAll((Map<String, Object>) value);
		else {
			HashMap<String, Object> newDict = new HashMap<String, Object>();
			newDict.putAll((Map<? extends String, ? extends Object>) value);
			dictMap.put(reference, newDict);
		}
	}


}
