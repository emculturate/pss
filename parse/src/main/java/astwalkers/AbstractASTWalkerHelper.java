package astwalkers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static mumble.ASTWalkerHelperConstants.*;

import errorhandling.ParseDiagnostic;


public abstract class AbstractASTWalkerHelper implements InterfaceASTWalkerHelper {
    
    /*************************************
     * AbstractASTWalkerHelper is an abstract class providing a base for AST walkers helpers.
     * The common objects and methods of the abstract class are used by the
     * SqlParseEventWalker and PUML3ParseEventWalker classes to manage parsing context and 
     * the construction of their respective AST trees.
     * 
     * The class implements the InterfaceASTWalkerHelper interface, which defines the minimum 
     * set of methods required to act like an AST Walker Helper. Not every Grammar will
     * need all of the methods defined in the interface.
     * ***********************************/

     /**
      * Trace flags for debugging purposes.
      * These flags control the output of various trace messages during parsing.
      */
	 Boolean showParse;
	 Boolean showSymbols;
	 Boolean showOther;
	 Boolean showResults;

     // Trace levels for different types of messages
	public final static Integer parseTrace = 1;
	public final static Integer symbolTrace = 2;
	public final static Integer otherTrace = 3;
	public final static Integer resultTrace = 4;

    /**
	 * SQL Abstract Syntax Tree: This collects and constructs a nested Map data
	 * structure representing the entire SQL statement
	 */
       
     public HashMap<String, Object> asTree;
	/**
	 * Depth of token stack
	 * HashMap keeps track of the nesting depth for each grammar rule during parsing. 
	 * It's a crucial component of the parser's context management system that enables 
	 * processing of recursive SQL structures.
	 * Specifically, stackTree maps:
	 * 		Keys: Integer rule indices (from the ANTLR parser)
	 * 		Values: Integer nesting levels for each rule
	 * This allows the parser to:
	 *  1. Track how deeply nested each grammar rule is within the parse tree
	 *  2. Generate unique keys for AST nodes using makeMapIndex(ruleIndex, stackLevel)
	 *  3. Associate child elements with the correct parent nodes
	 *  4. Handle recursive rule patterns that appear at multiple levels in SQL queries
	 * For example, when processing nested subqueries, stackTree ensures that references
	 * to columns, tables, and expressions are properly associated with their respective 
	 * scopes, even when the same rule (like query_expression) appears at different levels of nesting.
	 */
	public HashMap<Integer, Integer> stackTree;

	/**
	 * Depth of token stack; this keeps track of recursive depth in the
	 * multi-stack dta structure, allowing correct indexing of clauses during
	 * the walking operation. 
	 * In other words, this data structure contains multiple instances of context-specific parser-generated 
	 * context trees or symbol tables, when needed, corresponding to various levels of context tracked 
	 * by the stackTree. When a recursive rule is encountered, such as query_expression,
	 * the parser pushes the current context onto this stack, allowing it to create a new context for the nested rule.
	 * Several kinds of context maps may be inserted into this stack, including:
	 * - Symbol tables for identifiers, columns, and tables
	 * - Substitution variable maps for parameterized queries
	 * - Flag maps to track state across recursive processing
	 * This allows the parser to add/alter the correct instance of these context objects
	 * as it continues to react to the parser events. Knowing the CURRENT level of the stack allows 
	 * an event handler to obtain the correct context object and apply its additions or changes to the
	 * correct instance.
	 */
	public HashMap<String, Integer> stackSymbols;

	/**
	 * Diagnostics generated during AST walking (non-parser diagnostics).
	 */
	protected final List<ParseDiagnostic> walkerDiagnostics;

	/**
	 * Canonical diagnostics catalog. Keys are stable logical identifiers;
	 * values are updatable code/message mappings.
	 */
	protected final HashMap<String, String> diagnosticCodeMap;
	protected final HashMap<String, String> diagnosticMessageMap;

	public static final String DIAG_ANTLR_AMBIGUITY = "ANTLR_AMBIGUITY";
	public static final String DIAG_ANTLR_FULL_CONTEXT = "ANTLR_FULL_CONTEXT";
	public static final String DIAG_ANTLR_CONTEXT_SENSITIVITY = "ANTLR_CONTEXT_SENSITIVITY";
	public static final String DIAG_ANTLR_SYNTAX_ERROR = "ANTLR_SYNTAX_ERROR";
	public static final String DIAG_ANTLR_RECOVER_INLINE = "ANTLR_RECOVER_INLINE";
	public static final String DIAG_ANTLR_RECOVER = "ANTLR_RECOVER";
	public static final String DIAG_ANTLR_REPORT_ERROR = "ANTLR_REPORT_ERROR";
	public static final String DIAG_MANUAL_ERROR = "MANUAL_ERROR";
	public static final String DIAG_MANUAL_FATAL = "MANUAL_FATAL";
	public static final String DIAG_MANUAL_WARNING = "MANUAL_WARNING";


    public AbstractASTWalkerHelper() {
        // Set Tracing Defaults
        this.showParse = false;
        this.showSymbols = false;
        this.showOther = false;
        this.showResults = true;
        // Initialize the remaining objects
        this.asTree = new HashMap<String, Object>();
        this.stackTree = new HashMap<Integer, Integer>();
        this.stackSymbols = new HashMap<String, Integer>();
		this.walkerDiagnostics = new ArrayList<ParseDiagnostic>();
		this.diagnosticCodeMap = new HashMap<String, String>();
		this.diagnosticMessageMap = new HashMap<String, String>();
		initializeDiagnosticCatalog();
    }

	protected void initializeDiagnosticCatalog() {
		registerDiagnostic(DIAG_ANTLR_AMBIGUITY, "AMBIGUITY", "Ambiguity detected by ANTLR parser");
		registerDiagnostic(DIAG_ANTLR_FULL_CONTEXT, "FULL_CONTEXT", "ANTLR parser attempted full-context prediction");
		registerDiagnostic(DIAG_ANTLR_CONTEXT_SENSITIVITY, "CONTEXT_SENSITIVITY", "ANTLR context sensitivity detected");
		registerDiagnostic(DIAG_ANTLR_SYNTAX_ERROR, "SYNTAX_ERROR", "ANTLR syntax error");
		registerDiagnostic(DIAG_ANTLR_RECOVER_INLINE, "RECOVER_INLINE", "ANTLR inline recovery occurred");
		registerDiagnostic(DIAG_ANTLR_RECOVER, "RECOVER", "ANTLR recovery occurred");
		registerDiagnostic(DIAG_ANTLR_REPORT_ERROR, "REPORT_ERROR", "ANTLR reported parser error");
		registerDiagnostic(DIAG_MANUAL_ERROR, "MANUAL_ERROR", "Manual parser error");
		registerDiagnostic(DIAG_MANUAL_FATAL, "MANUAL_FATAL", "Manual fatal parser error");
		registerDiagnostic(DIAG_MANUAL_WARNING, "MANUAL_WARNING", "Manual parser warning");
	}

	public void registerDiagnostic(String key, String code, String defaultMessage) {
		if (key == null || code == null || defaultMessage == null) {
			throw new IllegalArgumentException("Diagnostic key, code, and message must not be null");
		}
		diagnosticCodeMap.put(key, code);
		diagnosticMessageMap.put(key, defaultMessage);
	}

	public void overrideDiagnosticCode(String key, String code) {
		if (key == null || code == null) {
			throw new IllegalArgumentException("Diagnostic key and code must not be null");
		}
		if (!diagnosticCodeMap.containsKey(key)) {
			throw new IllegalArgumentException("Diagnostic key does not exist in catalog: " + key);
		}
		diagnosticCodeMap.put(key, code);
	}

	public void overrideDiagnosticMessage(String key, String message) {
		if (key == null || message == null) {
			throw new IllegalArgumentException("Diagnostic key and message must not be null");
		}
		if (!diagnosticMessageMap.containsKey(key)) {
			throw new IllegalArgumentException("Diagnostic key does not exist in catalog: " + key);
		}
		diagnosticMessageMap.put(key, message);
	}

	public String getDiagnosticCode(String key) {
		return diagnosticCodeMap.get(key);
	}

	public String getDiagnosticMessage(String key) {
		return diagnosticMessageMap.get(key);
	}

	public List<ParseDiagnostic> getWalkerDiagnostics() {
		return walkerDiagnostics;
	}

	public void clearWalkerDiagnostics() {
		walkerDiagnostics.clear();
	}

	public void addWalkerDiagnostic(ParseDiagnostic diagnostic) {
		if (diagnostic == null) {
			return;
		}
		boolean exists = walkerDiagnostics.stream().anyMatch(existing -> sameDiagnostic(existing, diagnostic));
		if (!exists) {
			walkerDiagnostics.add(diagnostic);
		}
	}

	public void addWalkerDiagnostic(
			ParseDiagnostic.Severity severity,
			String code,
			String message,
			Integer line,
			Integer charPositionInLine,
			String source,
			String ruleName,
			String tokenText,
			boolean recoverable,
			String phase,
			String exceptionType,
			Map<String, String> details) {
		addWalkerDiagnostic(new ParseDiagnostic(
				severity,
				code,
				message,
				line,
				charPositionInLine,
				source,
				ruleName,
				tokenText,
				recoverable,
				phase,
				exceptionType,
				details));
	}

	public void addWalkerFatal(String code, String message) {
		addWalkerFatal(code, message, null, null, null);
	}

	public void addWalkerFatal(String code, String message, Integer line, Integer charPositionInLine) {
		addWalkerFatal(code, message, line, charPositionInLine, null);
	}

	public void addWalkerFatal(String code, String message, Integer line, Integer charPositionInLine, String tokenText) {
		// Centralized helper path for fatal walker diagnostics.
		addWalkerDiagnostic(
				ParseDiagnostic.Severity.FATAL,
				code,
				message,
				line,
				charPositionInLine,
				getClass().getSimpleName(),
				null,
				tokenText,
				false,
				"ast-walk",
				null,
				null);
	}

	public void addWalkerWarning(String code, String message) {
		addWalkerWarning(code, message, null, null);
	}

	public void addWalkerWarning(String code, String message, Integer line, Integer charPositionInLine) {
		addWalkerDiagnostic(
				ParseDiagnostic.Severity.WARNING,
				code,
				message,
				line,
				charPositionInLine,
				getClass().getSimpleName(),
				null,
				null,
				true,
				"ast-walk",
				null,
				null);
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

    public  Boolean getShowparse() {
		return showParse;
	}

	public  Boolean getShowsymbols() {
		return showSymbols;
	}

	public  Boolean getShowother() {
		return showOther;
	}

	public  Boolean getShowresults() {
		return showResults;
	}

	/**
	 * Method checks with the level of trace indicated by the calling method
	 * and if the trace is enabled, it will print the trace to the console.
	 * This is an in-class built logging capability used for debugging only.
	 * 
	 * @param trace
	 */
	public void showTrace(Integer traceType, Object trace) {
		if (traceType.equals(parseTrace) && showParse)
			System.out.println(trace);
		if (traceType.equals(symbolTrace) && showSymbols)
			System.out.println(trace);
		if (traceType.equals(resultTrace) && showResults)
			System.out.println(trace);
		if (traceType.equals(otherTrace) && showOther)
			System.out.println(trace);
	}

    
/**
 * Multiple Stack Operations Explained
 * ----------------------------------
 * This parser uses multiple stacks to handle the recursive nature of SQL grammar:
 * 
 * 1. stackTree/pushStack/popStack: Manages recursion depth for grammar rules
 *    - Each rule type can appear at multiple nesting levels in a query
 *    - Stack operations track which level of a particular rule we're processing
 *    - Allows the parser to correctly associate child elements with their parent nodes
 * 
 * 2. stackSymbols/symbolTable/pushSymbolTable/popSymbolTable: Manages symbol scope
 *    - SQL has complex scoping rules (subqueries create new scopes)
 *    - When entering a subquery, current symbols are pushed onto a stack
 *    - A new symbol table is created for the subquery's scope
 *    - When exiting, the parent scope is restored with relevant additions
 *
 * 3. flagMap stacks: Preserves state across recursive processing
 *    - Flags like unionClauseFound track the parser's state
 *    - When processing nested structures, current state needs to be preserved
 *    - pushFlagMap/popFlagMap save and restore these state variables
 * 
 * The multi-stack approach is essential because SQL has:
 * - Deep nesting (queries within queries)
 * - Complex scoping rules (column visibility differs by context)
 * - Repeated rule patterns at different nesting levels
 * - Context-sensitive interpretation (same syntax means different things in different places)
 * 
 * Without these stack operations, the parser couldn't distinguish between identical
 * structures at different nesting levels or maintain proper symbol resolution across
 * the complex hierarchical structure of SQL queries.
 */

 // Methods works with the stackTree HashMap data structure.
 public Integer pushStack(Integer ruleIndex) {
		Integer context = stackTree.get(ruleIndex);
		Integer newLevel;
		if (context == null) {
			newLevel = 1;
		} else {
			newLevel = context + 1;
		}
		stackTree.put(ruleIndex, newLevel);
		showTrace(otherTrace, "PUSH - " + makeMapIndex(ruleIndex, newLevel) + ": " + stackTree);
		return newLevel;
	}

	public Integer popStack(Integer ruleIndex) {
		Integer level = stackTree.get(ruleIndex) - 1;
		if (level == 0) {
			stackTree.remove(ruleIndex);
		}
		stackTree.put(ruleIndex, level);
		showTrace(otherTrace, "POP - " + makeMapIndex(ruleIndex, level) + ": " + stackTree);
		return level;
	}

	public Integer currentStackLevel(int ruleIndex) {
		return stackTree.get(ruleIndex);
	}


// Methods work with the stackSymbols HashMap data structure.

public Integer pushStack(String key, Object symbols) {
		Integer level = stackSymbols.get(key);
		Integer newLevel;
		if (level == null) {
			newLevel = 1;
		} else {
			newLevel = level + 1;
		}
		stackSymbols.put(key, newLevel);
		String symbolKey = key + "_" + newLevel;
		collect(symbolKey, symbols);
		showTrace(otherTrace, "PUSH - " + symbolKey + ": " + symbols);
		return newLevel;
	}

	public Object popStack(String key) {
		Integer level = stackSymbols.get(key);
		String symbolKey = key + "_" + level;
		if (level == 1)
			stackSymbols.remove(key);
		else
			stackSymbols.put(key, level - 1);
		showTrace(otherTrace, "POP - " + symbolKey + ": " + stackSymbols);
		return asTree.remove(symbolKey);
	}

    
/**
 * Standard Actions: SQL AST Tree Manipulation
 * --------------------------------------
 * These methods provide essential operations for transforming the raw parse tree
 * into a structured AST representation. They share common characteristics:
 * 
 * 1. Node Transformation:
 *    - All methods manipulate Map<String,Object> data structures representing AST nodes
 *    - They retrieve, modify and reattach nodes at different levels of the tree
 *    - Each handles a specific pattern of tree transformation needed across multiple parser rules
 *
 * 2. Stack Management:
 *    - Work with stackTree and stackLevel to identify correct nesting context
 *    - Maintain proper parent-child relationships in nested SQL structures
 *    - Use mapIndex keys (ruleIndex_stackLevel) to uniquely identify nodes
 *
 * 3. Key Operations:
 *    - handleOneChild: Simplifies nodes with exactly one child by promoting the child
 *    - handleListList/handleListItem: Manages collection-style nodes and their elements
 *    - handleOperandList: Builds special operator-based structures (AND, OR, etc.)
 *    - handlePushDown: Creates hierarchical relationships between nodes
 *    - addToParent: Attaches nodes to their parent in the tree
 *
 * These methods are invoked throughout the parser's rule-specific exit methods
 * to perform common tree manipulations that maintain the AST's structure and semantics.
 * Without them, every exit method would need to duplicate similar restructuring logic.
 */
	/**
	 * Add level map to SQLTree AST by ruleIndex and stackLevel
	 **/
	public Object collect(int ruleIndex, Integer stackLevel, Object item) {
		String index = makeMapIndex(ruleIndex, stackLevel);
		collect(index, item);
		if (item instanceof Map<?, ?>)
			return getNodeMap(ruleIndex, stackLevel);
		else
			return getNode(ruleIndex, stackLevel);
	}

	/**
	 * @param index
	 * @param item
	 */
	public void collect(String index, Object item) {
		asTree.put(index, item);
	}

	/**
	 * Create an empty ruleMap with ruleIndex and stackLvl key containing Type
	 * code
	 * 
	 * @param ruleIndex
	 * @param stackLvl
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> collectNewRuleMap(int ruleIndex, Integer stackLvl) {
		HashMap<String, Object> item = makeRuleMap(ruleIndex);
		return (Map<String, Object>) collect(ruleIndex, stackLvl, item);

	}

	/**
	 * Create new ruleMap with a ruleIndex Type
	 * 
	 * @param ruleIndex
	 * @return
	 */
	public HashMap<String, Object> makeRuleMap(int ruleIndex) {
		HashMap<String, Object> item = new HashMap<String, Object>();
		item.put(ASTWALKER_RULE_TYPE_KEY, ruleIndex);
		return item;
	}

	/**
	 * SQLTree operations when re-writing the AST during the walk
	 */

     public Object getNode(int ruleIndex, Integer stackLevel) {
		String mapIdx = makeMapIndex(ruleIndex, stackLevel);
		return asTree.get(mapIdx);
	}

	public Object removeNode(int ruleIndex, Integer stackLevel) {
		String mapIdx = makeMapIndex(ruleIndex, stackLevel);
		return asTree.remove(mapIdx);
	}

	@SuppressWarnings("unchecked")
	public Map<String, Object> getNodeMap(int ruleIndex, Integer stackLevel) {
		String mapIdx = makeMapIndex(ruleIndex, stackLevel);
		Map<String, Object> idMap = (Map<String, Object>) asTree.get(mapIdx);
		return idMap;
	}

	@SuppressWarnings("unchecked")
	public Map<String, Object> removeNodeMap(int ruleIndex, Integer stackLevel) {
		String mapIdx = makeMapIndex(ruleIndex, stackLevel);
		return (Map<String, Object>) asTree.remove(mapIdx);
	}

	public String makeMapIndex(int ruleIndex, Integer stackIndex) {
		return ruleIndex + "_" + stackIndex;
	}

	/**
	 * Pops node that is a single child entry up one level of SQL Tree and
	 * removes stack references
	 * 
	 * @param ruleIndex
	 */
	public void handleOneChild(int ruleIndex) {
		Integer stackLevel = currentStackLevel(ruleIndex);
		Map<String, Object> subMap = removeNodeMap(ruleIndex, stackLevel);
		subMap.remove(ASTWALKER_RULE_TYPE_KEY);
		String[] keys = new String[1];
		keys = subMap.keySet().toArray(keys);

		if (keys.length == 1) {
			showTrace(parseTrace, "Just One Entry: " + subMap);
			Object item = subMap.remove(keys[0]);
			collect(ruleIndex, stackLevel, item);
		} else {
			showTrace(parseTrace, "Too many entries: " + subMap);
		}
	}

	/**
	 * If the current node is a list, pull it up to the parent node in the SQL
	 * Tree as its value
	 * 
	 * @param ruleIndex
	 */
	public void handleListList(int ruleIndex, int parentRuleIndex) {
		Integer stackLevel = currentStackLevel(ruleIndex);
		Map<String, Object> item = removeNodeMap(ruleIndex, stackLevel);
		item.remove(ASTWALKER_RULE_TYPE_KEY);

		Integer parentStackLevel = currentStackLevel(parentRuleIndex);

		Map<String, Object> subMap = getNodeMap(parentRuleIndex, parentStackLevel);
		subMap.putAll(item);
		asTree.put("SKIP", "TRUE");
	}

	/**
	 * If the parent of the current node is a list, use this to put the child
	 * into the list
	 * 
	 * @param ruleIndex
	 * @param ctx
	 */
	public void handleListItem(int ruleIndex, int parentRuleIndex) {
		Integer stackLevel = currentStackLevel(ruleIndex);
		Map<String, Object> subMap = removeNodeMap(ruleIndex, stackLevel);
		subMap.remove(ASTWALKER_RULE_TYPE_KEY);
		String[] keys = new String[1];
		keys = subMap.keySet().toArray(keys);

		if (keys.length == 1) {
			showTrace(parseTrace, "Just One Entry: " + subMap);
			Object item = subMap.remove(keys[0]);

			Integer parentStackLevel = currentStackLevel(parentRuleIndex);

			subMap = getNodeMap(parentRuleIndex, parentStackLevel);
			Integer indx = subMap.size();
			subMap.put(indx.toString(), item);
			asTree.put("SKIP", "TRUE");

		} else {
			showTrace(parseTrace, "Too many entries: " + subMap);
		}
		showTrace(parseTrace, "handleListItem: " + subMap);
	}

	/**
	 * Construct a list that has a repeating operator
	 * 
	 * @param ruleIndex
	 * @param operand
	 */
	public void handleOperandList(int ruleIndex, String operand) {
		Integer stackLevel = currentStackLevel(ruleIndex);
		Map<String, Object> subMap = removeNodeMap(ruleIndex, stackLevel);
		Object type = subMap.remove(ASTWALKER_RULE_TYPE_KEY);

		if (subMap.size() == 1) {
			Map<String, Object> item = (Map<String, Object>) subMap.remove("1");
			collect(ruleIndex, stackLevel, item);
			showTrace(parseTrace, operand + "-less " + operand + " predicate: " + item);

		} else if (subMap.size() >= 2) {
			HashMap<String, Object> item = new HashMap<String, Object>();
			item.put(operand, subMap);

			collect(ruleIndex, stackLevel, item);
			showTrace(parseTrace, operand + "-ed predicate: " + item);
		} else {
			showTrace(parseTrace, "Wrong number of entries: " + subMap);
		}
	}

	/**
	 * If the Node has one level of children, use this to push the children down
	 * one level of a tree with the current node as parent
	 * 
	 * Then the Exit Every Rule will pull it up one level to the parent again where it can be handled
	 * with its siblings.
	 * 
	 * @param ruleIndex
	 */
	public void handlePushDown(int ruleIndex) {
		Integer stackLevel = currentStackLevel(ruleIndex);
		Map<String, Object> subMap = removeNodeMap(ruleIndex, stackLevel);
		Object type = subMap.remove(ASTWALKER_RULE_TYPE_KEY);

		Map<String, Object> newMap = collectNewRuleMap(ruleIndex, stackLevel);
		newMap.put(type.toString(), subMap);
		showTrace(parseTrace, "handlePushDown: " + subMap);
	}

	/**
	 * Find Parent and put this one item in it
	 * 
	 * @param parentRuleIndex
	 * @param parentStackLevel
	 * @param item
	 */
	public void addToParent(int parentRuleIndex, Integer parentStackLevel, Object item) {
		Map<String, Object> pMap = getNodeMap(parentRuleIndex, parentStackLevel);
		Integer indx = pMap.size();
		pMap.put(indx.toString(), item);
		asTree.put("SKIP", "TRUE");
	}


}
