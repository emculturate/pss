package astwalkers;

import java.util.HashMap;
import java.util.Map;

import org.antlr.v4.runtime.Token;

public interface InterfaceASTWalkerHelper {

    /**
     * This interface defines the methods that any AST walker helper class must implement.
     * It ensures that the necessary methods for handling the AST are available.
     */
    
     public void overrideAstKeyCrosswalkMap(String key, String value);
     public Integer currentStackLevel(String key);
     public Integer currentStackLevel(int ruleIndex);
     public Integer pushStack(Integer ruleIndex);
     public Integer pushStack(String key, Object symbols);
     public Integer popStack(Integer ruleIndex);
     public Object popStack(String key) ;
     public void pushSymbolTable();
     public void pushFlagMap();
     public void popSymbolTable(String key, HashMap<String, Object> symbols);
     public void popSymbolTablePutAll(HashMap<String, Object> symbols);
     public void popFlagMap();
     public void collect(String index, Object item);
     public Object collect(int ruleIndex, Integer stackLevel, Object item);
     public Map<String, Object> collectNewRuleMap(int ruleIndex, Integer stackLvl);
     public void collectSymbolTable(String alias, Object tableReference);
     public void collectSymbolTableItem(Object tableReference, Object item, Token token);
     public void addItemToSymbolTable(Object localSymbolTable, Object item, Token token);
     public void consolidateValuesStatementSymbolTable(String alias);
     public void captureQueryInterface();
     public HashMap<String, Object> getInterfaceFromQuery(String hdr);
     public HashMap<String, Object> getInterface(HashMap<String, Object> query);
     public Map<String, Object> checkForSubstitutionVariable(Map<String, Object> subMap, String type);
     public void addTableReferencesToTableDictionary();
     public HashMap<String, Object> makeRuleMap(int ruleIndex);
     public String makeMapIndex(int ruleIndex, Integer stackIndex);
     public Object getNode(int ruleIndex, Integer stackLevel);
     public Object removeNode(int ruleIndex, Integer stackLevel);
     public Map<String, Object> getNodeMap(int ruleIndex, Integer stackLevel);
     public Map<String, Object> removeNodeMap(int ruleIndex, Integer stackLevel);
     public void handleOneChild(int ruleIndex) ;
     public void handleListList(int ruleIndex, int parentRuleIndex);
     public void handleListItem(int ruleIndex, int parentRuleIndex);
     public void handleOperandList(int ruleIndex, String operand);
     public void handlePushDown(int ruleIndex);
     public void addToParent(int parentRuleIndex, Integer parentStackLevel, Object item);

}
