package generators;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static mumble.MumbleConstants.MUMBLE_ALIAS_KEY;
import static mumble.MumbleConstants.MUMBLE_ASSIGNMENTS_KEY;
import static mumble.MumbleConstants.MUMBLE_COLUMN_KEY;
import static mumble.MumbleConstants.MUMBLE_COLUMNS_KEY;
import static mumble.MumbleConstants.MUMBLE_CONDITION_KEY;
import static mumble.MumbleConstants.MUMBLE_DATABASE_NAME_KEY;
import static mumble.MumbleConstants.MUMBLE_DATATYPE_KEY;
import static mumble.MumbleConstants.MUMBLE_DEFAULT_VALUES_KEY;
import static mumble.MumbleConstants.MUMBLE_FROM_KEY;
import static mumble.MumbleConstants.MUMBLE_FUNCTION_NAME_KEY;
import static mumble.MumbleConstants.MUMBLE_GROUPBY_KEY;
import static mumble.MumbleConstants.MUMBLE_INSERT_INTO_KEY;
import static mumble.MumbleConstants.MUMBLE_INSERT_KEY;
import static mumble.MumbleConstants.MUMBLE_INSERT_PREAMBLE_KEY;
import static mumble.MumbleConstants.MUMBLE_JOIN_KEY;
import static mumble.MumbleConstants.MUMBLE_MATRIX_KEY;
import static mumble.MumbleConstants.MUMBLE_NAME_KEY;
import static mumble.MumbleConstants.MUMBLE_NULL_HANDLING_KEY;
import static mumble.MumbleConstants.MUMBLE_ON_CONFLICT_KEY;
import static mumble.MumbleConstants.MUMBLE_ORDERBY_KEY;
import static mumble.MumbleConstants.MUMBLE_PARAMETERS_KEY;
import static mumble.MumbleConstants.MUMBLE_QUALIFIER_KEY;
import static mumble.MumbleConstants.MUMBLE_QUERY_KEY;
import static mumble.MumbleConstants.MUMBLE_RETURNING_KEY;
import static mumble.MumbleConstants.MUMBLE_ROW_KEY;
import static mumble.MumbleConstants.MUMBLE_SCHEMA_KEY;
import static mumble.MumbleConstants.MUMBLE_SELECT_KEY;
import static mumble.MumbleConstants.MUMBLE_SET_KEY;
import static mumble.MumbleConstants.MUMBLE_SUBSTITUTION_KEY;
import static mumble.MumbleConstants.MUMBLE_TABLE_KEY;
import static mumble.MumbleConstants.MUMBLE_TABLE_REF_KEY;
import static mumble.MumbleConstants.MUMBLE_TARGET_TABLE_KEY;
import static mumble.MumbleConstants.MUMBLE_TO_KEY;
import static mumble.MumbleConstants.MUMBLE_TYPE_KEY;
import static mumble.MumbleConstants.MUMBLE_VALUE_KEY;
import static mumble.MumbleConstants.MUMBLE_VALUES_KEY;
import static mumble.MumbleConstants.MUMBLE_WHERE_KEY;


/**
 * Default concrete SQL statement generator.
 *
 * This class can be instantiated directly in tests while the reconstruction
 * behavior is iteratively refined by overriding methods in this class.
 */
public class SQLStatementGenerator extends AbstractSQLASTGenerator {

    // OVERRIDES Of Utility Methods
        protected void appendNode(Object node, StringBuilder sql) {
        if (node == null) {
            return;
        }

        if (node instanceof String text) {
            sql.append(text);
            return;
        }
        System.out.println("Unexpected node: " + node.toString());
    }

    @Override
    protected void appendLeafText(String text, StringBuilder sql) {
        if (text != null && !text.isBlank()) {
            sql.append(text);
        }
    }

    @Override
    protected void onLiteral(Object node, StringBuilder sql) {
        if (node instanceof String text) {
            sql.append(text);
            return;
        }
        super.onLiteral(node, sql);
    }

    // ---------------------------------------------------------------------------------------------
    // SQLParserEndPoints handlers (to be overridden by subclasses if needed)
    // ---------------------------------------------------------------------------------------------

    @Override
    protected void onSQLParserPredicand(Object node, StringBuilder sql) { handleEndPoint(node, sql); }

    @Override
    protected void onSQLParserJoinExtension(Object node, StringBuilder sql) { handleEndPoint(node, sql); }

    @Override
    protected void onSQLParserInsert(Object node, StringBuilder sql) {
        if (node instanceof Map<?, ?> nodeMap && nodeMap.containsKey(MUMBLE_INSERT_KEY)) {
            @SuppressWarnings("unchecked")
            Map<String, Object> insertMap = (Map<String, Object>) nodeMap.get(MUMBLE_INSERT_KEY);
            emitInsertStatement(insertMap, sql);
        } else {
            handleEndPoint(node, sql);
        }
    }

    @Override
    protected void onSQLParserUpdate(Object node, StringBuilder sql) { handleEndPoint(node, sql); }

    @Override
    protected void onSQLParserDelete(Object node, StringBuilder sql) { handleEndPoint(node, sql); }

    @Override
    protected void onSQLParserTruncate(Object node, StringBuilder sql) { handleEndPoint(node, sql); }

    @Override
    protected void onSQLParserInList(Object node, StringBuilder sql) { handleEndPoint(node, sql); }

    @Override
    protected void onSQLParserCondition(Object node, StringBuilder sql) { handleEndPoint(node, sql); }

    @Override
    protected void onSQLParserColumn(Object node, StringBuilder sql) { handleEndPoint(node, sql); }

    @Override
    protected void onSQLParserValues(Object node, StringBuilder sql) { handleEndPoint(node, sql); }

    @Override
    protected void onSQLParserTuple(Object node, StringBuilder sql) { handleEndPoint(node, sql); }

    @Override
    protected void onSQLParserSQL(Object node, StringBuilder sql) {
        if (!(node instanceof Map<?, ?> nodeMap)) {
            appendNode(node, sql);
            return;
        }
        if (nodeMap.containsKey(MUMBLE_INSERT_KEY)) {
            @SuppressWarnings("unchecked")
            Map<String, Object> insertMap = (Map<String, Object>) nodeMap.get(MUMBLE_INSERT_KEY);
            emitInsertStatement(insertMap, sql);
        } else if (nodeMap.containsKey(MUMBLE_SELECT_KEY)) {
            @SuppressWarnings("unchecked")
            Map<String, Object> lookupMapCasted = (Map<String, Object>) nodeMap;
            emitSelectStatement(lookupMapCasted, sql);
        } else {
            appendNode(node, sql);
        }
    }

    @Override
    protected void onSQLParserQuery(Object node, StringBuilder sql) { handleEndPoint(node, sql); }

    // ---------------------------------------------------------------------------------------------
    // Utility handlers supporting SQL Parser End Point logic (called by end point handlers above and by each other as needed)
    // ---------------------------------------------------------------------------------------------

    // Handle SQL End Point method for each type of end point subtree map
    private void handleEndPoint(Object node, StringBuilder sql) {
        if (node instanceof Map<?, ?> map) {
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                String key = entry.getKey().toString();
                Object value = entry.getValue();
                generateStatement(key, value, sql);
            }
        } else {
            appendNode(node, sql);
        }
    }

    // ---------------------------------------------------------------------------------------------
    // MUMBLE Constants key handlers for SQL statement generation 
    // (called by handleEndPoint, generateStatement and by each other as needed)
    // ---------------------------------------------------------------------------------------------

    /**
     * Emits a list of select items with optional aliases separated by columns.
     */
    @Override
    protected void onSelect(Object node, StringBuilder sql) {
        List<Object> items = orderedNumericKeyedList(node);
        if (!items.isEmpty()) {
            sql.append(' ');
        } else {
            System.out.println("Node: " + node + " was empty after ordering entries");
            return;
        }
        // Now emit each item in the select list and apply its alias if any were defined.
        for (int i = 0; i < items.size(); i++) {
            Object itemVal = items.get(i);
            if ((itemVal instanceof Map<?, ?> itemMap)) {
                applyAliasToItem(sql, itemMap);
            } else {
                System.out.println("Item value: " + itemVal + " is not a map object");
                sql.append(itemVal.toString());
             }
           if (i < items.size() - 1) {
                sql.append(", ");
            }
        }
    }

    private void applyAliasToItem(StringBuilder sql, Map<?, ?> itemMap) {
        String aliasStr = (String) itemMap.remove(MUMBLE_ALIAS_KEY);
        if (!itemMap.isEmpty()) {
            String key = itemMap.keySet().iterator().next().toString();
            generateStatement(key, itemMap.get(key), sql);
        }
        if (aliasStr != null && !aliasStr.isEmpty()) {
            sql.append(" AS ").append(aliasStr);
        }
    }

    /**
    * Emits a FROM clause, handling tables, joins, and subqueries recursively.
    */
    @Override
    protected void onFrom(Object node, StringBuilder sql) {
        sql.append(" FROM ");

        if (node == null) {
            return;
        } if (node instanceof String) {
           System.out.println("Node: " + node + " is not a map object");
           sql.append(node.toString());
        } else if (node instanceof Map<?, ?> itemMap) {
            String key = itemMap.keySet().iterator().next().toString();
            generateStatement(key, itemMap.get(key), sql);
        } else {
            System.out.println("Node: " + node + " is not a string or map object");
            sql.append(node.toString());
        }

    }

    /**
    * Emits a TABLE clause, handling either a table name or a table object.
    */
    @Override
    protected void onTable(Object node, StringBuilder sql) {
 
        if (node == null) {
            return;
        } if (node instanceof String) {
            sql.append(node.toString());
        } else if (node instanceof Map<?, ?> itemMap) {
            String alias = (String) itemMap.remove(MUMBLE_ALIAS_KEY);
           if (itemMap.size()>1) {
                // Node is a compound table reference. Remove the each key and construct the name and alias from parts
                String dbname = (String) itemMap.remove(MUMBLE_DATABASE_NAME_KEY);
                String schema = (String) itemMap.remove(MUMBLE_SCHEMA_KEY);
                String table = (String) itemMap.remove(MUMBLE_TABLE_KEY);
                boolean first = true;
                if (dbname != null) {
                    sql.append(dbname);
                    first = false;
                }
                if (schema != null) {
                    if (!first) {
                        sql.append('.');
                    }
                    sql.append(schema);
                    first = false;
                }
                if (table != null) {
                    if (!first) {
                        sql.append('.');
                    }
                    sql.append(table);
                 }
                if (alias != null) {
                    sql.append(' ').append(alias);
                }
            } else {
                String key = itemMap.keySet().iterator().next().toString();
                generateStatement(key, itemMap.get(key), sql); 
                if (alias != null) {
                    sql.append(" AS ").append(alias);
                }
            }
        } else {
            System.out.println("Node: " + node + " is not a string or map object");
            sql.append(node.toString());
        }

    }

    /**
     * Emits a JOIN clause, handling nested joins and subqueries recursively.
     */
    @Override
    protected void onJoin(Object node, StringBuilder sql) {
        List<Object> items = orderedNumericKeyedList(node);
        if (!items.isEmpty()) {
            sql.append(' ');
        } else {
            System.out.println("Node: " + node + " was empty after ordering entries");
            return;
        }
        for (Object item : items) {
            // Implement join item emission logic here as needed
        }
    }

    /**
    * Emits a table, subquery, or join structure as valid SQL.
    */
    private void emitTableOrJoin(Object node, StringBuilder sql) {
        if (node instanceof Map<?, ?> map) {
            // Defensive: match keys by .toString() for robustness
            Object joinVal = null, queryVal = null, tableVal = null, schemaVal = null, dbnameVal = null, aliasVal = null;
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                String k = entry.getKey().toString();
                switch (k) {
                    case MUMBLE_JOIN_KEY -> joinVal = entry.getValue();
                    case MUMBLE_QUERY_KEY -> queryVal = entry.getValue();
                    case MUMBLE_TABLE_KEY -> tableVal = entry.getValue();
                    case MUMBLE_SCHEMA_KEY -> schemaVal = entry.getValue();
                    case MUMBLE_DATABASE_NAME_KEY -> dbnameVal = entry.getValue();
                    case MUMBLE_ALIAS_KEY -> aliasVal = entry.getValue();
                }
            }
            if (joinVal != null) {
                onJoin(joinVal, sql);
                return;
            }
            if (queryVal != null && queryVal instanceof Map<?, ?> qmap && qmap.containsKey(MUMBLE_SELECT_KEY)) {
                sql.append('(');
                @SuppressWarnings("unchecked")
                Map<String, Object> qmapCasted = (Map<String, Object>) qmap;
                emitSelectStatement(qmapCasted, sql);
                sql.append(')');
                if (aliasVal != null) {
                    sql.append(' ').append(aliasVal);
                }
                return;
            }
            if (tableVal != null) {
                // If tableVal is a map, recursively emit as table/join
                if (tableVal instanceof Map<?, ?>) {
                    emitTableOrJoin(tableVal, sql);
                } else {
                    boolean first = true;
                    if (dbnameVal != null) {
                        sql.append(dbnameVal);
                        first = false;
                    }
                    if (schemaVal != null) {
                        if (!first) sql.append('.');
                        sql.append(schemaVal);
                        first = false;
                    }
                    if (!first) sql.append('.');
                    sql.append(tableVal);
                }
                if (aliasVal != null) {
                    sql.append(' ').append(aliasVal);
                }
                return;
            }
            // If map does not match known table/join/query structure, do nothing
            return;
        }
        // For strings, emit as is. For other types, do nothing.
        if (node instanceof String s) {
            sql.append(s);
        }
    }

    /**
     * Emits a subquery for a lookup node, wrapping the select subtree in parentheses.
     * No spaces before or after the parentheses. Calls the full SQL generator for the select subtree.
     */
    @Override
    protected void onLookup(Object node, StringBuilder sql) {
        if (!(node instanceof Map<?, ?> lookupMap)) {
            appendNode(node, sql);
            return;
        }
        if (lookupMap.containsKey(MUMBLE_SELECT_KEY)) {
            sql.append('(');
            @SuppressWarnings("unchecked")
            Map<String, Object> lookupMapCasted = (Map<String, Object>) lookupMap;
            emitSelectStatement(lookupMapCasted, sql);
            sql.append(')');
        } else {
            appendNode(node, sql);
        }
    }

    /**
     * Emits a subquery for a query node, wrapping the select subtree in parentheses.
     * No spaces before or after the parentheses. Calls the full SQL generator for the select subtree.
     */
    @Override
    protected void onQuery(Object node, StringBuilder sql) {
        if (!(node instanceof Map<?, ?> queryMap)) {
            appendNode(node, sql);
            return;
        }
        if (queryMap.containsKey(MUMBLE_SELECT_KEY)) {
            sql.append('(');
            @SuppressWarnings("unchecked")
            Map<String, Object> queryMapCasted = (Map<String, Object>) queryMap;
            emitSelectStatement(queryMapCasted, sql);
            sql.append(')');
        } else {
            appendNode(node, sql);
        }
    }
    /**
     * Emits a function call, handling qualifiers (e.g., DISTINCT) as the first argument.
     * Example: max(distinct a)
     */
    @Override
    protected void onFunction(Object node, StringBuilder sql) {
        if (!(node instanceof Map<?, ?> fnMap)) {
            appendNode(node, sql);
            return;
        }
        Object qualifier = fnMap.get(MUMBLE_QUALIFIER_KEY);
        Object parameters = fnMap.get(MUMBLE_PARAMETERS_KEY);
        Object type = fnMap.get(MUMBLE_TYPE_KEY);
        Object val = fnMap.get(MUMBLE_VALUE_KEY);
        Object data_type = fnMap.get(MUMBLE_DATATYPE_KEY);
        Object null_handle = fnMap.get(MUMBLE_NULL_HANDLING_KEY);
        Object select_from = fnMap.get(MUMBLE_SELECT_KEY);

        Object fnName = fnMap.get(MUMBLE_FUNCTION_NAME_KEY);
        if (fnName != null) {
            sql.append(fnName);
        }
        sql.append('(');
        Boolean type1Function = parameters != null;
        Boolean type2Function = type != null;
        // Else its a type 3 format

        if (type1Function) {
            boolean wrote = false;
            if (qualifier != null) {
              sql.append(qualifier);
             wrote = true;
            }
            if (parameters != null) {
              if (wrote) sql.append(' ');
             // parameters may be a map or list
             generateStatement(MUMBLE_PARAMETERS_KEY,parameters, sql);
            }
        } else if (type2Function) {
            // These are functions over windows like "last_value"
            if (null_handle != null) {
                sql.append(null_handle);
            }
            emitSelectStatement((Map<String, Object>) select_from, sql);
        } else {
            // For type 3 functions, which are CAST style functions, just emit the value and data_type as the function call argument
            if (val != null) {
                String key = ((Map<?, ?>) val).keySet().iterator().next().toString();
                generateStatement(key, val, sql);
           } 
           if (data_type != null) {
               sql.append(" as ");
               sql.append(data_type);
        }
     }

        sql.append(')');
    }
    
    
    /**
     * Emits a column reference as either table_ref.name or just name.
     * If table_ref is missing, null, or "*", only name is printed.
     * No spaces between table_ref, dot, and name.
     */
    @Override
    protected void onColumn(Object node, StringBuilder sql) {
        if (!(node instanceof Map<?, ?> colMap)) {
            appendNode(node, sql);
            return;
        }
        Object tableRef = colMap.get(MUMBLE_TABLE_REF_KEY);
        Object name = colMap.get(MUMBLE_NAME_KEY);
        boolean skipTable = (tableRef == null)
                || (tableRef instanceof String s && (s.isEmpty() || s.equals("*")));
        if (!skipTable) {
            sql.append(tableRef);
            sql.append('.');
        }
        if (name != null) {
            sql.append(name);
        } else {
            Object substitution = colMap.get(MUMBLE_SUBSTITUTION_KEY);
            if (substitution != null) {
                generateStatement(MUMBLE_SUBSTITUTION_KEY, substitution, sql);
            }
        }
    }

        @Override
    protected void onSubstitution(Object node, StringBuilder sql) {
        if (!(node instanceof Map<?, ?> colMap)) {
            appendNode(node, sql);
            return;
        }
        Object type = colMap.get(MUMBLE_TYPE_KEY);
        Object name = colMap.get(MUMBLE_NAME_KEY);
 
        if (name != null) {
            sql.append(name);
        } 
    }

    /**
    * Emits a complete SELECT statement from a select AST map (with keys like select, from, where, etc.).
    * This is the central method for generating a full SQL statement from the primary branches of a Select statement AST.
    */
    @SuppressWarnings("unchecked")
    private void emitSelectStatement(Map<String, Object> selectMap, StringBuilder sql) {
        if (selectMap == null) return;

        sql.append("SELECT");

        Object qualifier = selectMap.remove(MUMBLE_QUALIFIER_KEY);
        if (qualifier != null) {
            sql.append(' ');
            sql.append(qualifier);
        }

        Object selectListMap = selectMap.remove(MUMBLE_SELECT_KEY);
        if (selectListMap == null) 
            return;
        else if (!(selectListMap instanceof Map<?, ?>))   
            return;
         else  {
            generateStatement(MUMBLE_SELECT_KEY, selectListMap, sql);
         }

        Object fromMap = selectMap.remove(MUMBLE_FROM_KEY);
         if (fromMap != null) {
            if (fromMap instanceof Map<?, ?>) 
                generateStatement(MUMBLE_FROM_KEY, fromMap, sql);
         }

        Object whereMap = selectMap.remove(MUMBLE_WHERE_KEY);
        if (whereMap != null) {
            if (whereMap instanceof Map<?, ?>) 
                generateStatement(MUMBLE_WHERE_KEY, whereMap, sql);
         }

        Object groupByMap = selectMap.remove(MUMBLE_GROUPBY_KEY);
       if (groupByMap != null) {
            if (groupByMap instanceof Map<?, ?>) 
                generateStatement(MUMBLE_GROUPBY_KEY, groupByMap, sql);
         }

        Object orderByMap = selectMap.remove(MUMBLE_ORDERBY_KEY);
        if (orderByMap != null) {
            if (orderByMap instanceof Map<?, ?>) 
                generateStatement(MUMBLE_ORDERBY_KEY, orderByMap, sql);
        }

    }

    @Override
    protected void onInsert(Object node, StringBuilder sql) {
        if (node instanceof Map<?, ?> insertMap) {
            @SuppressWarnings("unchecked")
            Map<String, Object> insertMapCasted = (Map<String, Object>) insertMap;
            emitInsertStatement(insertMapCasted, sql);
        } else {
            super.onInsert(node, sql);
        }
    }

    /**
     * Emits a complete INSERT statement from an {@code insert={...}} AST map.
     */
    @SuppressWarnings("unchecked")
    private void emitInsertStatement(Map<String, Object> insertMap, StringBuilder sql) {
        if (insertMap == null) {
            return;
        }

        Object preamble = insertMap.get(MUMBLE_INSERT_PREAMBLE_KEY);
        if (MUMBLE_INSERT_INTO_KEY.equals(preamble)) {
            sql.append("INSERT INTO");
        } else if (preamble instanceof String preambleText) {
            sql.append(preambleText.replace('_', ' ').toUpperCase());
        } else {
            sql.append("INSERT INTO");
        }

        Object targetTableObj = insertMap.get(MUMBLE_TARGET_TABLE_KEY);
        if (targetTableObj instanceof Map<?, ?> targetWrapper) {
            sql.append(' ');
            emitInsertTargetTable((Map<String, Object>) targetWrapper, sql);
        }

        Object columnsObj = insertMap.get(MUMBLE_COLUMNS_KEY);
        if (columnsObj instanceof Map<?, ?> columnsMap) {
            sql.append(' ');
            emitInsertColumnList((Map<String, Object>) columnsMap, sql);
        }

        Object fromObj = insertMap.get(MUMBLE_FROM_KEY);
        if (fromObj != null) {
            sql.append(' ');
            emitInsertSource(fromObj, sql);
        }

        Object onConflictObj = insertMap.get(MUMBLE_ON_CONFLICT_KEY);
        if (onConflictObj != null) {
            sql.append(' ');
            emitOnConflict(onConflictObj, sql);
        }

        Object returningObj = insertMap.get(MUMBLE_RETURNING_KEY);
        if (returningObj != null) {
            sql.append(' ');
            emitReturningClause(returningObj, sql);
        }
    }

    private void emitInsertTargetTable(Map<String, Object> targetWrapper, StringBuilder sql) {
        Object tableObj = targetWrapper.get(MUMBLE_TABLE_KEY);
        if (tableObj != null) {
            onTable(tableObj, sql);
            return;
        }
        for (Map.Entry<String, Object> entry : targetWrapper.entrySet()) {
            generateStatement(entry.getKey(), entry.getValue(), sql);
        }
    }

    private void emitInsertColumnList(Map<String, Object> columnsMap, StringBuilder sql) {
        List<Object> columns = orderedNumericKeyedList(columnsMap);
        if (columns.isEmpty()) {
            return;
        }
        sql.append('(');
        for (int i = 0; i < columns.size(); i++) {
            if (i > 0) {
                sql.append(", ");
            }
            Object columnEntry = columns.get(i);
            if (columnEntry instanceof Map<?, ?> columnEntryMap) {
                Object columnObj = columnEntryMap.get(MUMBLE_COLUMN_KEY);
                if (columnObj != null) {
                    onColumn(columnObj, sql);
                }
            }
        }
        sql.append(')');
    }

    @SuppressWarnings("unchecked")
    private void emitInsertSource(Object fromObj, StringBuilder sql) {
        if (!(fromObj instanceof Map<?, ?> fromMap)) {
            appendNode(fromObj, sql);
            return;
        }

        if (Boolean.TRUE.equals(fromMap.get(MUMBLE_DEFAULT_VALUES_KEY))) {
            sql.append("DEFAULT VALUES");
            return;
        }

        Object valuesObj = fromMap.get(MUMBLE_VALUES_KEY);
        if (valuesObj instanceof Map<?, ?> valuesMap) {
            sql.append("VALUES");
            emitValuesMatrix(valuesMap.get(MUMBLE_MATRIX_KEY), sql);
            return;
        }

        if (fromMap.containsKey(MUMBLE_SELECT_KEY)) {
            emitInsertSelectSource((Map<String, Object>) fromMap, sql);
        }
    }

    private void emitInsertSelectSource(Map<String, Object> fromMap, StringBuilder sql) {
        Map<String, Object> selectStmt = new LinkedHashMap<>();
        selectStmt.put(MUMBLE_SELECT_KEY, fromMap.get(MUMBLE_SELECT_KEY));
        Object fromClause = fromMap.get(MUMBLE_FROM_KEY);
        if (fromClause != null) {
            selectStmt.put(MUMBLE_FROM_KEY, fromClause);
        }
        emitSelectStatement(selectStmt, sql);
    }

    private void emitValuesMatrix(Object matrixObj, StringBuilder sql) {
        List<Object> rows = orderedNumericKeyedList(matrixObj);
        for (int i = 0; i < rows.size(); i++) {
            if (i > 0) {
                sql.append(", ");
            }
            sql.append('(');
            if (rows.get(i) instanceof Map<?, ?> rowWrapper) {
                emitValuesRow(rowWrapper.get(MUMBLE_ROW_KEY), sql);
            }
            sql.append(')');
        }
    }

    private void emitValuesRow(Object rowObj, StringBuilder sql) {
        List<Object> cells = orderedNumericKeyedList(rowObj);
        for (int i = 0; i < cells.size(); i++) {
            if (i > 0) {
                sql.append(", ");
            }
            emitValueExpression(cells.get(i), sql);
        }
    }

    private void emitValueExpression(Object valueObj, StringBuilder sql) {
        if (!(valueObj instanceof Map<?, ?> valueMap)) {
            appendNode(valueObj, sql);
            return;
        }
        for (Map.Entry<?, ?> entry : valueMap.entrySet()) {
            generateStatement(entry.getKey().toString(), entry.getValue(), sql);
            return;
        }
    }

    @SuppressWarnings("unchecked")
    private void emitOnConflict(Object onConflictObj, StringBuilder sql) {
        if (!(onConflictObj instanceof Map<?, ?> conflictMap)) {
            appendNode(onConflictObj, sql);
            return;
        }

        sql.append("ON CONFLICT");
        Object targetObj = conflictMap.get("target");
        if (targetObj != null) {
            sql.append(" (");
            List<Object> targetColumns = orderedNumericKeyedList(targetObj);
            for (int i = 0; i < targetColumns.size(); i++) {
                if (i > 0) {
                    sql.append(", ");
                }
                emitConflictTargetColumn(targetColumns.get(i), sql);
            }
            sql.append(')');
        }

        Object actionObj = conflictMap.get("action");
        if (!(actionObj instanceof Map<?, ?> actionMap)) {
            return;
        }

        Object doAction = actionMap.get("do");
        if ("NOTHING".equals(doAction)) {
            sql.append(" DO NOTHING");
            return;
        }
        if (!"UPDATE".equals(doAction)) {
            return;
        }

        sql.append(" DO UPDATE SET ");
        emitOnConflictAssignments(actionMap.get(MUMBLE_ASSIGNMENTS_KEY), sql);

        Object whereObj = actionMap.get(MUMBLE_WHERE_KEY);
        if (whereObj != null) {
            sql.append(" WHERE ");
            emitWhereCondition(whereObj, sql);
        }
    }

    private void emitConflictTargetColumn(Object columnEntry, StringBuilder sql) {
        if (!(columnEntry instanceof Map<?, ?> columnEntryMap)) {
            appendNode(columnEntry, sql);
            return;
        }
        if (columnEntryMap.containsKey(MUMBLE_COLUMN_KEY)) {
            onColumn(columnEntryMap.get(MUMBLE_COLUMN_KEY), sql);
            return;
        }
        emitValueExpression(columnEntry, sql);
    }

    private void emitOnConflictAssignments(Object assignmentsObj, StringBuilder sql) {
        List<Object> assignments = orderedNumericKeyedList(assignmentsObj);
        for (int i = 0; i < assignments.size(); i++) {
            if (i > 0) {
                sql.append(", ");
            }
            if (!(assignments.get(i) instanceof Map<?, ?> assignmentMap)) {
                continue;
            }
            Object setObj = assignmentMap.get(MUMBLE_SET_KEY);
            Object toObj = assignmentMap.get(MUMBLE_TO_KEY);
            emitAssignmentSide(setObj, sql);
            sql.append(" = ");
            emitAssignmentSide(toObj, sql);
        }
    }

    private void emitAssignmentSide(Object sideObj, StringBuilder sql) {
        if (!(sideObj instanceof Map<?, ?> sideMap)) {
            appendNode(sideObj, sql);
            return;
        }
        if (sideMap.containsKey(MUMBLE_COLUMN_KEY)) {
            onColumn(sideMap.get(MUMBLE_COLUMN_KEY), sql);
            return;
        }
        emitValueExpression(sideObj, sql);
    }

    private void emitWhereCondition(Object whereObj, StringBuilder sql) {
        if (!(whereObj instanceof Map<?, ?> whereMap)) {
            appendNode(whereObj, sql);
            return;
        }
        if (whereMap.containsKey(MUMBLE_CONDITION_KEY)) {
            emitSimpleCondition(whereMap.get(MUMBLE_CONDITION_KEY), sql);
            return;
        }
        emitValueExpression(whereObj, sql);
    }

    private void emitSimpleCondition(Object conditionObj, StringBuilder sql) {
        if (!(conditionObj instanceof Map<?, ?> conditionMap)) {
            appendNode(conditionObj, sql);
            return;
        }
        Object left = conditionMap.get("left");
        Object operator = conditionMap.get("operator");
        Object right = conditionMap.get("right");
        emitValueExpression(left, sql);
        if (operator != null) {
            sql.append(' ').append(operator).append(' ');
        }
        emitValueExpression(right, sql);
    }

    @SuppressWarnings("unchecked")
    private void emitReturningClause(Object returningObj, StringBuilder sql) {
        sql.append("RETURNING");
        List<Object> items = orderedNumericKeyedList(returningObj);
        for (int i = 0; i < items.size(); i++) {
            if (i > 0) {
                sql.append(", ");
            }
            if (items.get(i) instanceof Map<?, ?> itemMap) {
                Map<String, Object> itemCopy = new LinkedHashMap<>((Map<String, Object>) itemMap);
                applyAliasToItem(sql, itemCopy);
            }
        }
    }

}
