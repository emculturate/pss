/**
 * 
 */
package access;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import com.google.gson.Gson;

import errorhandling.ParseDiagnostic;

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
	 * Collect Root Query Column Dictionary
	 */
	private HashMap<String, Object> queryColumnDictionaryMap;

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
	 * Optional collector for array-oriented extractor outputs (for example, SCRIPT statement snapshots).
	 */
	private HashMap<String, Object> arrayOutputCollectorsMap;

	/**
	 * Parser Message Lists
	 * This holds the all of the messages generated during parsing, including errors, Ambiguities,
	 * and information messages.
	 */
	private List<ParseDiagnostic> parserDiagnosticList;

	private List<String> parserMessageStringList;

	// Constructors
	
	/**
	 * @param sqlAbstractTree
	 * @param tableDictionary
	 * @param queryColumnDictionaryMap
	 * @param symbolTable
	 * @param substitutionsMap
	 * @param queryInterface
	 */
	public Snippet(HashMap<String, Object> sqlAbstractTree, 
		HashMap<String, Object> tableDictionary,
		HashMap<String, Object> queryColumnDictionaryMap, 
		HashMap<String, Object> symbolTable, 
		HashMap<String, Object> substitutionsMap,
		HashSet<String> queryInterface) {
		super();
		this.sqlAbstractTree = sqlAbstractTree;
		this.tableDictionary = tableDictionary;
		this.queryColumnDictionaryMap = queryColumnDictionaryMap;
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

	public HashMap<String, Object> getQueryColumnDictionaryMap() {
		return queryColumnDictionaryMap;
	}

	public void setQueryColumnDictionaryMap(HashMap<String, Object> queryColumnDictionaryMap) {
		this.queryColumnDictionaryMap = queryColumnDictionaryMap;
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

	public HashMap<String, Object> getArrayOutputCollectorsMap() {
		return arrayOutputCollectorsMap;
	}

	public void setArrayOutputCollectorsMap(HashMap<String, Object> arrayOutputCollectorsMap) {
		this.arrayOutputCollectorsMap = arrayOutputCollectorsMap;
	}
	
	public List<String> getFatalErrorStringList() {
		return getDiagnosticMessagesBySeverity(ParseDiagnostic.Severity.FATAL);
	}

	public List<String> getErrorStringList(ParseDiagnostic.Severity severity) {
		return getDiagnosticMessagesBySeverity(severity);
	}

	public List<String> getDiagnosticMessagesBySeverity(ParseDiagnostic.Severity severity) {
		if (severity == null || parserDiagnosticList == null || parserDiagnosticList.isEmpty()) {
			return List.of();
		}
		return parserDiagnosticList.stream()
				.filter(diagnostic -> diagnostic != null && diagnostic.severity() == severity)
				.map(ParseDiagnostic::message)
				.filter(message -> message != null)
				.distinct()
				.collect(Collectors.toList());
	}

	public int getFatalErrorCount() {
		return getFatalErrorStringList().size();
	}	

	public int getDiagnosticCountBySeverity(ParseDiagnostic.Severity severity) {
		if (severity == null || parserDiagnosticList == null || parserDiagnosticList.isEmpty()) {
			return 0;
		}
		int count = 0;
		for (ParseDiagnostic diagnostic : parserDiagnosticList) {
			if (diagnostic != null && diagnostic.severity() == severity) {
				count++;
			}
		}
		return count;
	}

	public int getDiagnosticCountByCode(String code) {
		if (code == null || parserDiagnosticList == null || parserDiagnosticList.isEmpty()) {
			return 0;
		}
		int count = 0;
		for (ParseDiagnostic diagnostic : parserDiagnosticList) {
			if (diagnostic != null && code.equals(diagnostic.code())) {
				count++;
			}
		}
		return count;
	}

	public int getDiagnosticCountByCodeAndSeverity(String code, ParseDiagnostic.Severity severity) {
		if (code == null || severity == null || parserDiagnosticList == null || parserDiagnosticList.isEmpty()) {
			return 0;
		}
		int count = 0;
		for (ParseDiagnostic diagnostic : parserDiagnosticList) {
			if (diagnostic != null && code.equals(diagnostic.code()) && severity == diagnostic.severity()) {
				count++;
			}
		}
		return count;
	}

	public void setFatalErrorStringList(List<String> fatalErrorStringList) {
		if (fatalErrorStringList == null || fatalErrorStringList.isEmpty()) {
			return;
		}
		if (this.parserDiagnosticList == null) {
			this.parserDiagnosticList = new ArrayList<>();
		}
		for (String fatalError : fatalErrorStringList) {
			if (fatalError == null || fatalError.isBlank()) {
				continue;
			}
			boolean alreadyPresent = this.parserDiagnosticList.stream()
					.anyMatch(existing -> existing != null
							&& existing.severity() == ParseDiagnostic.Severity.FATAL
							&& fatalError.equals(existing.message()));
			if (alreadyPresent) {
				continue;
			}
			this.parserDiagnosticList.add(new ParseDiagnostic(
					ParseDiagnostic.Severity.FATAL,
					"MANUAL_FATAL",
					fatalError,
					null,
					null,
					"Snippet",
					null,
					null,
					false,
					"access.snippet",
					null,
					null));
		}
		this.parserDiagnosticList = dedupeDiagnostics(this.parserDiagnosticList);
	}
	
	public List<ParseDiagnostic> getParserMessageList() {
		return parserDiagnosticList;
	}
	public void setParserMessageList(List<ParseDiagnostic> parserMessageList) {
		this.parserDiagnosticList = dedupeDiagnostics(parserMessageList);
	}
	public List<String> getParserMessageStringList() {
		return parserMessageStringList;
	}
	public void setParserMessageStringList(List<String> parserMessageStringList) {
		this.parserMessageStringList = parserMessageStringList;
	}

	public List<ParseDiagnostic> getParserDiagnosticList() {
		return parserDiagnosticList;
	}
	public void setParserDiagnosticList(List<ParseDiagnostic> parserDiagnosticList) {
		this.parserDiagnosticList = dedupeDiagnostics(parserDiagnosticList);
	}

	private List<ParseDiagnostic> dedupeDiagnostics(List<ParseDiagnostic> diagnostics) {
		if (diagnostics == null || diagnostics.isEmpty()) {
			return diagnostics;
		}
		List<ParseDiagnostic> deduped = new ArrayList<>();
		for (ParseDiagnostic candidate : diagnostics) {
			if (candidate == null) {
				continue;
			}
			boolean exists = deduped.stream().anyMatch(existing -> sameDiagnostic(existing, candidate));
			if (!exists) {
				deduped.add(candidate);
			}
		}
		return deduped;
	}

	private boolean sameDiagnostic(ParseDiagnostic a, ParseDiagnostic b) {
		if (a == null || b == null) {
			return false;
		}
		if (a.severity() != b.severity()) {
			return false;
		}
		if (!safeEquals(a.code(), b.code())) {
			return false;
		}
		if (!safeEquals(a.message(), b.message())) {
			return false;
		}
		if (!safeEquals(a.line(), b.line())) {
			return false;
		}
		if (!safeEquals(a.charPositionInLine(), b.charPositionInLine())) {
			return false;
		}
		if (!safeEquals(a.tokenText(), b.tokenText())) {
			return false;
		}
		return safeEquals(a.exceptionType(), b.exceptionType());
	}

	private boolean safeEquals(Object a, Object b) {
		if (a == null) {
			return b == null;
		}
		return a.equals(b);
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
	// Returns the Query Column Dictionary as a JSON String
	public String getQueryColumnDictionaryJson() {
		Gson gson = new Gson();
		return gson.toJson(queryColumnDictionaryMap);
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
	// Returns the optional Array Output Collectors as a JSON String
	public String getArrayOutputCollectorsMapJson() {
		Gson gson = new Gson();
		return gson.toJson(arrayOutputCollectorsMap);
	}
	// Returns the Fatal Error String List as a JSON String
	public String getFatalErrorStringListJson() {
		Gson gson = new Gson();
		return gson.toJson(getFatalErrorStringList());
	}
	// Returns the Fatal Error Count as a JSON String
	public String getFatalErrorCountJson() {
		Gson gson = new Gson();
		return gson.toJson(getFatalErrorCount());
	}
	// Returns the Parser Message String List as a JSON String
	public String getParserMessageStringListJson() {
		Gson gson = new Gson();
		return gson.toJson(parserMessageStringList);
	}
	// Returns the Parser Message List as a JSON String
	public String getParserMessageListJson() {
		Gson gson = new Gson();
		return gson.toJson(parserDiagnosticList);
	}
	// Returns the Parser Diagnostic List as a JSON String
	public String getParserDiagnosticListJson() {
		Gson gson = new Gson();
		return gson.toJson(parserDiagnosticList);
	}
	/**
	 * Returns a string representation of the Snippet object
	 * 
	 * @return String representation of the Snippet
	 */

	@Override
	public String toString() {
		return "Snippet [sqlAbstractTree=" + sqlAbstractTree + ", tableDictionary=" + tableDictionary
				+ ", queryColumnDictionaryMap=" + queryColumnDictionaryMap + ", symbolTable=" + symbolTable 
				+ ", substitutionsMap=" + substitutionsMap + ", queryInterface="
				+ queryInterface + ", arrayOutputCollectorsMap=" + arrayOutputCollectorsMap
				+ ", parserMessageList=" + parserDiagnosticList + ", parserDiagnosticList=" + parserDiagnosticList + ", parserMessageStringList="
				+ parserMessageStringList + ", fatalErrorCount=" + getFatalErrorCount() + ", fatalErrorStringList="
				+ getFatalErrorStringList() + "]";
	}	
}
