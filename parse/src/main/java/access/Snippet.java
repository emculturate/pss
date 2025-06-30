/**
 * 
 */
package access;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import com.google.gson.Gson;

import errorhandling.SyntaxError;

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

	/**
	 * Parser Message Lists
	 * This holds the all of the messages generated during parsing, including errors, Ambiguities,
	 * and information messages.
	 */
	private List<SyntaxError> parserMessageList;

	private List<String> parserMessageStringList;

	/**
	 * Fatal Error Count
	 * This holds the number of fatal errors encountered during parsing.
	 */
	private int fatalErrorCount;

	private List<String> fatalErrorStringList;

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
	
	public List<String> getFatalErrorStringList() {
		return fatalErrorStringList;
	}

	public int getFatalErrorCount() {
		return fatalErrorCount;
	}	

	public void setFatalErrorStringList(List<String> fatalErrorStringList) {
		this.fatalErrorStringList = fatalErrorStringList;
		if (fatalErrorStringList == null) {
			this.fatalErrorCount = 0;
		} else if (fatalErrorStringList.isEmpty()) {
			this.fatalErrorCount = 0;
		} else {
			this.fatalErrorCount = fatalErrorStringList.size();
		}
	
	}
	
	public List<SyntaxError> getParserMessageList() {
		return parserMessageList;
	}
	public void setParserMessageList(List<SyntaxError> parserMessageList) {
		this.parserMessageList = parserMessageList;
	}
	public List<String> getParserMessageStringList() {
		return parserMessageStringList;
	}
	public void setParserMessageStringList(List<String> parserMessageStringList) {
		this.parserMessageStringList = parserMessageStringList;
	}

	/**
	 * Get JSON Objects as Strings from the Snippet
	 * 
	 */
	// Returns the SQL Abstract Tree as a JSON String
	public String getSqlAbstractTreeJson() {
		Gson gson = new Gson();
		return gson.toJson(sqlAbstractTree);
	}
	// Returns the Table Dictionary as a JSON String
	public String getTableDictionaryJson() {
		Gson gson = new Gson();
		return gson.toJson(tableDictionary);
	}
	// Returns the Symbol Table as a JSON String
	public String getSymbolTableJson() {
		Gson gson = new Gson();
		return gson.toJson(symbolTable);
	}
	// Returns the Substitutions Map as a JSON String
	public String getSubstitutionsMapJson() {
		Gson gson = new Gson();
		return gson.toJson(substitutionsMap);
	}
	// Returns the Query Interface as a JSON String
	public String getQueryInterfaceJson() {
		Gson gson = new Gson();
		return gson.toJson(queryInterface);
	}
	// Returns the Fatal Error String List as a JSON String
	public String getFatalErrorStringListJson() {
		Gson gson = new Gson();
		return gson.toJson(fatalErrorStringList);
	}
	// Returns the Fatal Error Count as a JSON String
	public String getFatalErrorCountJson() {
		Gson gson = new Gson();
		return gson.toJson(fatalErrorCount);
	}
	// Returns the Parser Message String List as a JSON String
	public String getParserMessageStringListJson() {
		Gson gson = new Gson();
		return gson.toJson(parserMessageStringList);
	}
	// Returns the Parser Message List as a JSON String
	public String getParserMessageListJson() {
		Gson gson = new Gson();
		return gson.toJson(parserMessageList);
	}
	/**
	 * Returns a string representation of the Snippet object
	 * 
	 * @return String representation of the Snippet
	 */

	public String toString() {
		return "Snippet [sqlAbstractTree=" + sqlAbstractTree + ", tableDictionary=" + tableDictionary
				+ ", symbolTable=" + symbolTable + ", substitutionsMap=" + substitutionsMap + ", queryInterface="
				+ queryInterface + ", parserMessageList=" + parserMessageList + ", parserMessageStringList="
				+ parserMessageStringList + ", fatalErrorCount=" + fatalErrorCount + ", fatalErrorStringList="
				+ fatalErrorStringList + "]";
	}	
}
