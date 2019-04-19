/**
 * 
 */
package mumble.sql;

import java.util.HashMap;
import java.util.HashSet;

/**
 * Snippet holds all required elements for working with a PSS SQL AST. Having parsed a SQL statement, the PSS SQL Parse Event Walker 
 * produces an AST, a Symbol Table, a Table Dictionary for the SQL inputs, a Query Interface and an optional set of SQL Substitution Variables
 * 
 * @author Geoff Howe 
 *
 */
public class Snippet {

	/**
	 * SQL Abstract Syntax Tree: This collects and constructs a nested Map data
	 * structure representing the entire SQL statement
	 */
	private HashMap<String, Object> sqlAbstractTree;

	/**
	 * Collect Root Table Column Dictionary
	 */
	private HashMap<String, Object> tableDictionary;

	/**
	 * Collect Nested Symbol Table for the query
	 */
	private HashMap<String, Object> symbolTable;

	/**
	 * Collect Substitution Variable List
	 */
	private HashMap<String, Object> substitutionsMap;

	/**
	 * For complete SQL trees, this holds the output interface produced by the SQL statement
	 */
	private  HashSet<String> queryInterface;

	// Constructors
	
	/**
	 * @param sqlAbstractTree
	 * @param tableDictionary
	 * @param symbolTable
	 * @param substitutionsMap
	 * @param queryInterface
	 */
	public Snippet(HashMap<String, Object> sqlAbstractTree, HashMap<String, Object> tableDictionary,
			HashMap<String, Object> symbolTable, HashMap<String, Object> substitutionsMap,
			HashSet<String> queryInterface) {
		super();
		this.sqlAbstractTree = sqlAbstractTree;
		this.tableDictionary = tableDictionary;
		this.symbolTable = symbolTable;
		this.substitutionsMap = substitutionsMap;
		this.queryInterface = queryInterface;
	}
	
	
	// Getters and Setters
	
	public HashMap<String, Object> getSqlAbstractTree() {
		return sqlAbstractTree;
	}

	public void setSqlAbstractTree(HashMap<String, Object> sqlAbstractTree) {
		this.sqlAbstractTree = sqlAbstractTree;
	}

	public HashMap<String, Object> getTableDictionary() {
		return tableDictionary;
	}

	public void setTableDictionary(HashMap<String, Object> tableDictionary) {
		this.tableDictionary = tableDictionary;
	}

	public HashMap<String, Object> getSymbolTable() {
		return symbolTable;
	}

	public void setSymbolTable(HashMap<String, Object> symbolTable) {
		this.symbolTable = symbolTable;
	}

	public HashMap<String, Object> getSubstitutionsMap() {
		return substitutionsMap;
	}

	public void setSubstitutionsMap(HashMap<String, Object> substitutionsMap) {
		this.substitutionsMap = substitutionsMap;
	}

	public HashSet<String> getQueryInterface() {
		return queryInterface;
	}

	public void setQueryInterface(HashSet<String> queryInterface) {
		this.queryInterface = queryInterface;
	} 
	
	
}
