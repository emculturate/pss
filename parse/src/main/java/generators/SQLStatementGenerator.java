package generators;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static mumble.MumbleConstants.MUMBLE_ALTER_KEY;
import static mumble.MumbleConstants.MUMBLE_AND_KEY;
import static mumble.MumbleConstants.MUMBLE_ALIAS_KEY;
import static mumble.MumbleConstants.MUMBLE_ASSIGNMENTS_KEY;
import static mumble.MumbleConstants.MUMBLE_COLUMN_KEY;
import static mumble.MumbleConstants.MUMBLE_COLUMNS_KEY;
import static mumble.MumbleConstants.MUMBLE_CONDITION_KEY;
import static mumble.MumbleConstants.MUMBLE_CONCATENATE_KEY;
import static mumble.MumbleConstants.MUMBLE_CTE_KEY;
import static mumble.MumbleConstants.MUMBLE_CREATE_KEY;
import static mumble.MumbleConstants.MUMBLE_DATABASE_NAME_KEY;
import static mumble.MumbleConstants.MUMBLE_DATATYPE_KEY;
import static mumble.MumbleConstants.MUMBLE_DROP_KEY;
import static mumble.MumbleConstants.MUMBLE_DEFAULT_VALUES_KEY;
import static mumble.MumbleConstants.MUMBLE_DELETE_KEY;
import static mumble.MumbleConstants.MUMBLE_FOR_KEY;
import static mumble.MumbleConstants.MUMBLE_FROM_KEY;
import static mumble.MumbleConstants.MUMBLE_FUNCTION_NAME_KEY;
import static mumble.MumbleConstants.MUMBLE_GROUPBY_KEY;
import static mumble.MumbleConstants.MUMBLE_HAVING_KEY;
import static mumble.MumbleConstants.MUMBLE_IN_KEY;
import static mumble.MumbleConstants.MUMBLE_INSERT_INTO_KEY;
import static mumble.MumbleConstants.MUMBLE_INSERT_KEY;
import static mumble.MumbleConstants.MUMBLE_INSERT_PREAMBLE_KEY;
import static mumble.MumbleConstants.MUMBLE_JOIN_KEY;
import static mumble.MumbleConstants.MUMBLE_JOIN_ON_KEY;
import static mumble.MumbleConstants.MUMBLE_MATRIX_KEY;
import static mumble.MumbleConstants.MUMBLE_NAME_KEY;
import static mumble.MumbleConstants.MUMBLE_NULL_HANDLING_KEY;
import static mumble.MumbleConstants.MUMBLE_ON_CONFLICT_KEY;
import static mumble.MumbleConstants.MUMBLE_OPTIONS_KEY;
import static mumble.MumbleConstants.MUMBLE_OR_KEY;
import static mumble.MumbleConstants.MUMBLE_ORDERBY_KEY;
import static mumble.MumbleConstants.MUMBLE_PARAMETERS_KEY;
import static mumble.MumbleConstants.MUMBLE_PIVOT_KEY;
import static mumble.MumbleConstants.MUMBLE_PIVOT_LITERAL_KEY;
import static mumble.MumbleConstants.MUMBLE_QUALIFIER_KEY;
import static mumble.MumbleConstants.MUMBLE_QUALIFY_KEY;
import static mumble.MumbleConstants.MUMBLE_QUERY_KEY;
import static mumble.MumbleConstants.MUMBLE_RETURNING_KEY;
import static mumble.MumbleConstants.MUMBLE_ROW_KEY;
import static mumble.MumbleConstants.MUMBLE_SCHEMA_KEY;
import static mumble.MumbleConstants.MUMBLE_SELECT_KEY;
import static mumble.MumbleConstants.MUMBLE_SET_KEY;
import static mumble.MumbleConstants.MUMBLE_SUBSTITUTION_KEY;
import static mumble.MumbleConstants.MUMBLE_TABLE_FUNCTION_KEY;
import static mumble.MumbleConstants.MUMBLE_TABLE_KEY;
import static mumble.MumbleConstants.MUMBLE_TABLE_REF_KEY;
import static mumble.MumbleConstants.MUMBLE_TARGET_TABLE_KEY;
import static mumble.MumbleConstants.MUMBLE_TO_KEY;
import static mumble.MumbleConstants.MUMBLE_TRUNCATE_KEY;
import static mumble.MumbleConstants.MUMBLE_TYPE_KEY;
import static mumble.MumbleConstants.MUMBLE_UNION_KEY;
import static mumble.MumbleConstants.MUMBLE_UNPIVOT_KEY;
import static mumble.MumbleConstants.MUMBLE_UPDATE_KEY;
import static mumble.MumbleConstants.MUMBLE_USING_KEY;
import static mumble.MumbleConstants.MUMBLE_VALUE_KEY;
import static mumble.MumbleConstants.MUMBLE_VALUES_KEY;
import static mumble.MumbleConstants.MUMBLE_WHERE_KEY;
import static mumble.MumbleConstants.MUMBLE_WITH_KEY;


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
    protected void onSQLParserUpdate(Object node, StringBuilder sql) {
        if (node instanceof Map<?, ?> nodeMap && nodeMap.containsKey(MUMBLE_UPDATE_KEY)) {
            @SuppressWarnings("unchecked")
            Map<String, Object> updateMap = (Map<String, Object>) nodeMap.get(MUMBLE_UPDATE_KEY);
            emitUpdateStatement(updateMap, sql);
        } else {
            handleEndPoint(node, sql);
        }
    }

    @Override
    protected void onSQLParserDelete(Object node, StringBuilder sql) {
        if (node instanceof Map<?, ?> nodeMap && nodeMap.containsKey(MUMBLE_DELETE_KEY)) {
            @SuppressWarnings("unchecked")
            Map<String, Object> deleteMap = (Map<String, Object>) nodeMap.get(MUMBLE_DELETE_KEY);
            emitDeleteStatement(deleteMap, sql);
        } else {
            handleEndPoint(node, sql);
        }
    }

    @Override
    protected void onSQLParserTruncate(Object node, StringBuilder sql) {
        if (node instanceof Map<?, ?> nodeMap && nodeMap.containsKey(MUMBLE_TRUNCATE_KEY)) {
            @SuppressWarnings("unchecked")
            Map<String, Object> truncateMap = (Map<String, Object>) nodeMap.get(MUMBLE_TRUNCATE_KEY);
            emitTruncateStatement(truncateMap, sql);
        } else {
            handleEndPoint(node, sql);
        }
    }

    @Override
    protected void onSQLParserInList(Object node, StringBuilder sql) { handleEndPoint(node, sql); }

    @Override
    protected void onSQLParserCondition(Object node, StringBuilder sql) { handleEndPoint(node, sql); }

    @Override
    protected void onSQLParserColumn(Object node, StringBuilder sql) { handleEndPoint(node, sql); }

    @Override
    protected void onSQLParserValues(Object node, StringBuilder sql) {
        if (node instanceof Map<?, ?> valuesMap && valuesMap.containsKey(MUMBLE_VALUES_KEY)) {
            @SuppressWarnings("unchecked")
            Map<String, Object> valuesBody = (Map<String, Object>) valuesMap.get(MUMBLE_VALUES_KEY);
            emitValuesStatement(valuesBody, sql, false);
        } else {
            handleEndPoint(node, sql);
        }
    }

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
        } else if (nodeMap.containsKey(MUMBLE_UPDATE_KEY)) {
            @SuppressWarnings("unchecked")
            Map<String, Object> updateMap = (Map<String, Object>) nodeMap.get(MUMBLE_UPDATE_KEY);
            emitUpdateStatement(updateMap, sql);
        } else if (nodeMap.containsKey(MUMBLE_DELETE_KEY)) {
            @SuppressWarnings("unchecked")
            Map<String, Object> deleteMap = (Map<String, Object>) nodeMap.get(MUMBLE_DELETE_KEY);
            emitDeleteStatement(deleteMap, sql);
        } else if (nodeMap.containsKey(MUMBLE_SELECT_KEY)) {
            @SuppressWarnings("unchecked")
            Map<String, Object> lookupMapCasted = (Map<String, Object>) nodeMap;
            emitSelectStatement(lookupMapCasted, sql);
        } else if (nodeMap.containsKey(MUMBLE_WITH_KEY)) {
            @SuppressWarnings("unchecked")
            Map<String, Object> withMapCasted = (Map<String, Object>) nodeMap;
            emitWithQuery(withMapCasted, sql);
        } else {
            appendNode(node, sql);
        }
    }

    @Override
    protected void onSQLParserQuery(Object node, StringBuilder sql) { handleEndPoint(node, sql); }

    @Override
    protected void onSQLParserDdl(Object node, StringBuilder sql) {
        if (node instanceof Map<?, ?> ddlMap) {
            emitDdlStatement((Map<String, Object>) ddlMap, sql);
        } else {
            handleEndPoint(node, sql);
        }
    }

    @Override
    protected void onSQLParserScript(Object node, StringBuilder sql) {
        if (node instanceof Map<?, ?> scriptMap) {
            emitScriptStatement((Map<String, Object>) scriptMap, sql);
        } else {
            handleEndPoint(node, sql);
        }
    }

    @Override
    protected void onWhere(Object node, StringBuilder sql) {
        sql.append(" WHERE ");
        emitFilterExpression(node, sql);
    }

    @Override
    protected void onHaving(Object node, StringBuilder sql) {
        sql.append(" HAVING ");
        emitFilterExpression(node, sql);
    }

    @Override
    protected void onQualify(Object node, StringBuilder sql) {
        sql.append(" QUALIFY ");
        emitFilterExpression(node, sql);
    }

    @Override
    protected void onGroupBy(Object node, StringBuilder sql) {
        sql.append(" GROUP BY ");
        emitExpressionList(node, sql);
    }

    @Override
    protected void onOrderBy(Object node, StringBuilder sql) {
        sql.append(" ORDER BY ");
        List<Object> items = orderedNumericKeyedList(node);
        for (int i = 0; i < items.size(); i++) {
            if (i > 0) {
                sql.append(", ");
            }
            emitOrderByItem(items.get(i), sql);
        }
    }

    @Override
    protected void onCalculation(Object node, StringBuilder sql) {
        if (!(node instanceof Map<?, ?> calcMap)) {
            appendNode(node, sql);
            return;
        }
        Object left = calcMap.get("left");
        Object operator = calcMap.get("operator");
        Object right = calcMap.get("right");
        emitValueExpression(left, sql);
        if (operator != null) {
            sql.append(' ').append(operator).append(' ');
        }
        emitValueExpression(right, sql);
    }

    @Override
    protected void onParameters(Object node, StringBuilder sql) {
        if (node == null) {
            return;
        }
        List<Object> items = orderedNumericKeyedList(node);
        if (!items.isEmpty()) {
            for (int i = 0; i < items.size(); i++) {
                if (i > 0) {
                    sql.append(", ");
                }
                emitValueExpression(items.get(i), sql);
            }
            return;
        }
        if (node instanceof Map<?, ?>) {
            emitValueExpression(node, sql);
        } else {
            appendNode(node, sql);
        }
    }

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
        }
        if (node instanceof Map<?, ?> fromMap) {
            emitFromSource((Map<String, Object>) fromMap, sql);
        } else if (node instanceof String text) {
            sql.append(text);
        } else {
            appendNode(node, sql);
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
            Object tableFunction = itemMap.get(MUMBLE_TABLE_FUNCTION_KEY);
            if (tableFunction != null) {
                sql.append("table(");
                emitTableFunction(tableFunction, sql);
                sql.append(')');
                if (alias != null) {
                    sql.append(' ').append(alias);
                }
                return;
            }
            Object queryVal = itemMap.get(MUMBLE_QUERY_KEY);
            if (queryVal instanceof Map<?, ?> queryMap && queryMap.containsKey(MUMBLE_SELECT_KEY)) {
                sql.append('(');
                @SuppressWarnings("unchecked")
                Map<String, Object> queryMapCasted = (Map<String, Object>) queryMap;
                emitSelectStatement(queryMapCasted, sql);
                sql.append(')');
                if (alias != null) {
                    sql.append(' ').append(alias);
                }
                return;
            }
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
                    sql.append(' ').append(alias);
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

        Object havingMap = selectMap.remove(MUMBLE_HAVING_KEY);
        if (havingMap != null) {
            if (havingMap instanceof Map<?, ?>) {
                generateStatement(MUMBLE_HAVING_KEY, havingMap, sql);
            }
        }

        Object qualifyMap = selectMap.remove(MUMBLE_QUALIFY_KEY);
        if (qualifyMap != null) {
            if (qualifyMap instanceof Map<?, ?>) {
                generateStatement(MUMBLE_QUALIFY_KEY, qualifyMap, sql);
            }
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
            emitInlineValuesClause((Map<String, Object>) valuesMap, sql);
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
        emitAssignments(actionMap.get(MUMBLE_ASSIGNMENTS_KEY), sql);

        Object whereObj = actionMap.get(MUMBLE_WHERE_KEY);
        if (whereObj != null) {
            sql.append(" WHERE ");
            emitFilterExpression(whereObj, sql);
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

    private void emitAssignments(Object assignmentsObj, StringBuilder sql) {
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

    private void emitFilterExpression(Object filterObj, StringBuilder sql) {
        if (filterObj == null) {
            return;
        }
        if (!(filterObj instanceof Map<?, ?> filterMap)) {
            appendNode(filterObj, sql);
            return;
        }
        if (filterMap.containsKey(MUMBLE_CONDITION_KEY)) {
            emitSimpleCondition(filterMap.get(MUMBLE_CONDITION_KEY), sql);
            return;
        }
        if (filterMap.containsKey(MUMBLE_AND_KEY)) {
            emitLogicalCombination("AND", filterMap.get(MUMBLE_AND_KEY), sql);
            return;
        }
        if (filterMap.containsKey(MUMBLE_OR_KEY)) {
            emitLogicalCombination("OR", filterMap.get(MUMBLE_OR_KEY), sql);
            return;
        }
        if (filterMap.containsKey(MUMBLE_SUBSTITUTION_KEY)) {
            onSubstitution(filterMap.get(MUMBLE_SUBSTITUTION_KEY), sql);
            return;
        }
        emitValueExpression(filterObj, sql);
    }

    private void emitLogicalCombination(String operator, Object itemsObj, StringBuilder sql) {
        List<Object> items = orderedNumericKeyedList(itemsObj);
        for (int i = 0; i < items.size(); i++) {
            if (i > 0) {
                sql.append(' ').append(operator).append(' ');
            }
            emitFilterExpression(items.get(i), sql);
        }
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
        sql.append("RETURNING ");
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

    private void emitTableFunction(Object tableFunctionObj, StringBuilder sql) {
        if (!(tableFunctionObj instanceof Map<?, ?> tfMap)) {
            appendNode(tableFunctionObj, sql);
            return;
        }
        Object fnName = tfMap.get(MUMBLE_FUNCTION_NAME_KEY);
        if (fnName != null) {
            sql.append(fnName);
        }
        Object params = tfMap.get(MUMBLE_PARAMETERS_KEY);
        if (params instanceof Map<?, ?> paramsMap) {
            sql.append('(');
            boolean first = true;
            for (Map.Entry<?, ?> entry : paramsMap.entrySet()) {
                if (!first) {
                    sql.append(", ");
                }
                sql.append(entry.getKey()).append("=>");
                emitValueExpression(entry.getValue(), sql);
                first = false;
            }
            sql.append(')');
        }
    }

    @Override
    protected void onUpdate(Object node, StringBuilder sql) {
        if (node instanceof Map<?, ?> updateMap) {
            emitUpdateStatement((Map<String, Object>) updateMap, sql);
        } else {
            super.onUpdate(node, sql);
        }
    }

    @Override
    protected void onDelete(Object node, StringBuilder sql) {
        if (node instanceof Map<?, ?> deleteMap) {
            emitDeleteStatement((Map<String, Object>) deleteMap, sql);
        } else {
            super.onDelete(node, sql);
        }
    }

    @SuppressWarnings("unchecked")
    private void emitUpdateStatement(Map<String, Object> updateMap, StringBuilder sql) {
        if (updateMap == null) {
            return;
        }

        sql.append("UPDATE");
        Object tableObj = updateMap.get(MUMBLE_TABLE_KEY);
        if (tableObj != null) {
            sql.append(' ');
            onTable(tableObj, sql);
        }

        Object assignmentsObj = updateMap.get(MUMBLE_ASSIGNMENTS_KEY);
        if (assignmentsObj != null) {
            sql.append(" SET ");
            emitAssignments(assignmentsObj, sql);
        }

        Object fromObj = updateMap.get(MUMBLE_FROM_KEY);
        if (fromObj != null) {
            generateStatement(MUMBLE_FROM_KEY, fromObj, sql);
        }

        Object whereObj = updateMap.get(MUMBLE_WHERE_KEY);
        if (whereObj != null) {
            sql.append(" WHERE ");
            emitFilterExpression(whereObj, sql);
        }

        Object returningObj = updateMap.get(MUMBLE_RETURNING_KEY);
        if (returningObj != null) {
            sql.append(' ');
            emitReturningClause(returningObj, sql);
        }
    }

    @SuppressWarnings("unchecked")
    private void emitDeleteStatement(Map<String, Object> deleteMap, StringBuilder sql) {
        if (deleteMap == null) {
            return;
        }

        sql.append("DELETE FROM");
        Object tableObj = deleteMap.get(MUMBLE_TABLE_KEY);
        if (tableObj != null) {
            sql.append(' ');
            onTable(tableObj, sql);
        }

        Object usingObj = deleteMap.get(MUMBLE_USING_KEY);
        if (usingObj != null) {
            sql.append(" USING ");
            emitUsingClause(usingObj, sql);
        }

        Object whereObj = deleteMap.get(MUMBLE_WHERE_KEY);
        if (whereObj != null) {
            sql.append(" WHERE ");
            emitFilterExpression(whereObj, sql);
        }

        Object returningObj = deleteMap.get(MUMBLE_RETURNING_KEY);
        if (returningObj != null) {
            sql.append(' ');
            emitReturningClause(returningObj, sql);
        }
    }

    private void emitUsingClause(Object usingObj, StringBuilder sql) {
        List<Object> sources = orderedNumericKeyedList(usingObj);
        for (int i = 0; i < sources.size(); i++) {
            if (i > 0) {
                sql.append(", ");
            }
            emitUsingSource(sources.get(i), sql);
        }
    }

    private void emitUsingSource(Object sourceObj, StringBuilder sql) {
        if (!(sourceObj instanceof Map<?, ?> sourceMap)) {
            appendNode(sourceObj, sql);
            return;
        }
        if (sourceMap.containsKey(MUMBLE_TABLE_KEY)) {
            onTable(sourceMap.get(MUMBLE_TABLE_KEY), sql);
            return;
        }
        if (sourceMap.containsKey(MUMBLE_JOIN_KEY)) {
            emitJoinClause(sourceMap.get(MUMBLE_JOIN_KEY), sql);
            return;
        }
        if (sourceMap.size() == 1) {
            Map.Entry<?, ?> entry = sourceMap.entrySet().iterator().next();
            generateStatement(entry.getKey().toString(), entry.getValue(), sql);
        }
    }

    private void emitJoinClause(Object joinObj, StringBuilder sql) {
        List<Object> parts = orderedNumericKeyedList(joinObj);
        for (int i = 0; i < parts.size(); i++) {
            if (!(parts.get(i) instanceof Map<?, ?> partMap)) {
                continue;
            }
            if (partMap.containsKey(MUMBLE_JOIN_KEY)) {
                sql.append(' ').append(partMap.get(MUMBLE_JOIN_KEY).toString().toUpperCase());
                if (i + 1 < parts.size() && parts.get(i + 1) instanceof Map<?, ?> nextPartMap
                        && !nextPartMap.containsKey(MUMBLE_JOIN_KEY)) {
                    i++;
                    sql.append(' ');
                    emitFromSource((Map<String, Object>) nextPartMap, sql);
                }
                Object onObj = partMap.get(MUMBLE_JOIN_ON_KEY);
                if (onObj != null) {
                    sql.append(" ON ");
                    emitFilterExpression(onObj, sql);
                }
            } else {
                emitFromSource((Map<String, Object>) partMap, sql);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void emitJoinPart(Map<?, ?> partMap, StringBuilder sql) {
        if (partMap.containsKey(MUMBLE_JOIN_KEY)) {
            emitJoinClause(partMap.get(MUMBLE_JOIN_KEY), sql);
            return;
        }
        emitFromSource((Map<String, Object>) partMap, sql);
    }

    @SuppressWarnings("unchecked")
    private void emitFromSource(Map<String, Object> fromMap, StringBuilder sql) {
        if (fromMap.containsKey(MUMBLE_JOIN_KEY)) {
            emitJoinClause(fromMap.get(MUMBLE_JOIN_KEY), sql);
            return;
        }

        Object tableObj = fromMap.get(MUMBLE_TABLE_KEY);
        Object pivotObj = fromMap.get(MUMBLE_PIVOT_KEY);
        Object unpivotObj = fromMap.get(MUMBLE_UNPIVOT_KEY);
        Object aliasObj = fromMap.get(MUMBLE_ALIAS_KEY);
        boolean hasModifier = pivotObj != null || unpivotObj != null;

        if (tableObj != null && hasModifier) {
            onTable(tableObj, sql);
            if (pivotObj != null) {
                emitPivotClause(pivotObj, sql);
            }
            if (unpivotObj != null) {
                emitUnpivotClause(unpivotObj, sql);
            }
            if (aliasObj instanceof String alias) {
                sql.append(' ').append(alias);
            }
            return;
        }

        if (fromMap.size() == 1) {
            Map.Entry<String, Object> entry = fromMap.entrySet().iterator().next();
            generateStatement(entry.getKey(), entry.getValue(), sql);
            return;
        }

        for (Map.Entry<String, Object> entry : fromMap.entrySet()) {
            generateStatement(entry.getKey(), entry.getValue(), sql);
        }
    }

    private void emitPivotClause(Object pivotObj, StringBuilder sql) {
        if (!(pivotObj instanceof Map<?, ?> pivotMap)) {
            appendNode(pivotObj, sql);
            return;
        }
        sql.append(" PIVOT (");
        emitPivotAggregateList(pivotMap.get(MUMBLE_VALUE_KEY), sql);
        sql.append(" FOR ");
        emitValueExpression(pivotMap.get(MUMBLE_FOR_KEY), sql);
        sql.append(" IN (");
        emitPivotInList(pivotMap.get(MUMBLE_IN_KEY), sql);
        sql.append("))");
    }

    private void emitPivotAggregateList(Object valueObj, StringBuilder sql) {
        List<Object> aggregates = orderedNumericKeyedList(valueObj);
        if (!aggregates.isEmpty()) {
            for (int i = 0; i < aggregates.size(); i++) {
                if (i > 0) {
                    sql.append(", ");
                }
                emitValueExpression(aggregates.get(i), sql);
            }
            return;
        }
        if (valueObj != null) {
            emitValueExpression(valueObj, sql);
        }
    }

    private void emitPivotInList(Object inObj, StringBuilder sql) {
        List<Object> items = orderedNumericKeyedList(inObj);
        for (int i = 0; i < items.size(); i++) {
            if (i > 0) {
                sql.append(", ");
            }
            if (items.get(i) instanceof Map<?, ?> itemMap) {
                if (itemMap.containsKey(MUMBLE_PIVOT_LITERAL_KEY)) {
                    sql.append(itemMap.get(MUMBLE_PIVOT_LITERAL_KEY));
                } else {
                    emitValueExpression(items.get(i), sql);
                }
            }
        }
    }

    private void emitUnpivotClause(Object unpivotObj, StringBuilder sql) {
        if (!(unpivotObj instanceof Map<?, ?> unpivotMap)) {
            appendNode(unpivotObj, sql);
            return;
        }
        sql.append(" UNPIVOT (");
        emitUnpivotIdentifier(unpivotMap.get(MUMBLE_VALUE_KEY), sql);
        sql.append(" FOR ");
        emitUnpivotIdentifier(unpivotMap.get(MUMBLE_FOR_KEY), sql);
        sql.append(" IN (");
        emitUnpivotInList(unpivotMap.get(MUMBLE_IN_KEY), sql);
        sql.append("))");
    }

    private void emitUnpivotIdentifier(Object identifierObj, StringBuilder sql) {
        if (identifierObj instanceof String text) {
            sql.append(text);
            return;
        }
        emitValueExpression(identifierObj, sql);
    }

    private void emitUnpivotInList(Object inObj, StringBuilder sql) {
        List<Object> items = orderedNumericKeyedList(inObj);
        for (int i = 0; i < items.size(); i++) {
            if (i > 0) {
                sql.append(", ");
            }
            if (!(items.get(i) instanceof Map<?, ?> itemMap)) {
                continue;
            }
            Object name = itemMap.get(MUMBLE_NAME_KEY);
            if (name != null) {
                sql.append(name);
            }
            Object label = itemMap.get("label");
            if (label != null) {
                sql.append(" AS ");
                if (label instanceof String labelText) {
                    sql.append(labelText);
                } else {
                    appendNode(label, sql);
                }
            }
        }
    }

    private void emitExpressionList(Object listObj, StringBuilder sql) {
        List<Object> items = orderedNumericKeyedList(listObj);
        for (int i = 0; i < items.size(); i++) {
            if (i > 0) {
                sql.append(", ");
            }
            emitValueExpression(items.get(i), sql);
        }
    }

    private void emitOrderByItem(Object itemObj, StringBuilder sql) {
        if (!(itemObj instanceof Map<?, ?> itemMap)) {
            emitValueExpression(itemObj, sql);
            return;
        }
        Object predicand = itemMap.get("predicand");
        if (predicand != null) {
            emitValueExpression(predicand, sql);
        }
        Object sortOrder = itemMap.get("sort_order");
        if (sortOrder != null) {
            sql.append(' ').append(sortOrder);
        }
        Object nullOrder = itemMap.get("null_order");
        if (nullOrder != null && !"null".equalsIgnoreCase(String.valueOf(nullOrder))) {
            sql.append(' ').append(nullOrder);
        }
    }

    @SuppressWarnings("unchecked")
    private void emitWithQuery(Map<String, Object> withQueryMap, StringBuilder sql) {
        sql.append("WITH ");
        emitWithClause(withQueryMap.get(MUMBLE_WITH_KEY), sql);
        Object queryObj = withQueryMap.get(MUMBLE_QUERY_KEY);
        if (queryObj instanceof Map<?, ?> queryMap) {
            emitQueryBody((Map<String, Object>) queryMap, sql);
        }
    }

    private void emitWithClause(Object withObj, StringBuilder sql) {
        List<Object> items = orderedNumericKeyedList(withObj);
        for (int i = 0; i < items.size(); i++) {
            if (i > 0) {
                sql.append(", ");
            }
            if (items.get(i) instanceof Map<?, ?> itemMap) {
                emitWithListItem((Map<String, Object>) itemMap, sql);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void emitWithListItem(Map<String, Object> itemMap, StringBuilder sql) {
        Object aliasObj = itemMap.get(MUMBLE_ALIAS_KEY);
        if (aliasObj != null) {
            sql.append(aliasObj);
        }
        sql.append(" AS (");
        emitCteBody(itemMap.get(MUMBLE_CTE_KEY), sql);
        sql.append(')');
    }

    @SuppressWarnings("unchecked")
    private void emitCteBody(Object cteObj, StringBuilder sql) {
        if (!(cteObj instanceof Map<?, ?> cteMap)) {
            appendNode(cteObj, sql);
            return;
        }
        Map<String, Object> cteBody = (Map<String, Object>) cteMap;
        if (cteBody.containsKey(MUMBLE_WITH_KEY)) {
            emitWithQuery(cteBody, sql);
        } else if (cteBody.containsKey(MUMBLE_SELECT_KEY)) {
            emitSelectStatement(cteBody, sql);
        } else if (cteBody.containsKey(MUMBLE_VALUES_KEY)) {
            emitInlineValuesClause((Map<String, Object>) cteBody.get(MUMBLE_VALUES_KEY), sql);
        } else if (cteBody.containsKey(MUMBLE_INSERT_KEY)) {
            emitInsertStatement((Map<String, Object>) cteBody.get(MUMBLE_INSERT_KEY), sql);
        } else if (cteBody.containsKey(MUMBLE_UPDATE_KEY)) {
            emitUpdateStatement((Map<String, Object>) cteBody.get(MUMBLE_UPDATE_KEY), sql);
        } else if (cteBody.containsKey(MUMBLE_DELETE_KEY)) {
            emitDeleteStatement((Map<String, Object>) cteBody.get(MUMBLE_DELETE_KEY), sql);
        } else if (cteBody.containsKey(MUMBLE_UNION_KEY)) {
            emitUnionQuery(cteBody, sql);
        } else {
            handleEndPoint(cteBody, sql);
        }
    }

    @SuppressWarnings("unchecked")
    private void emitQueryBody(Map<String, Object> queryMap, StringBuilder sql) {
        if (queryMap.containsKey(MUMBLE_WITH_KEY)) {
            emitWithQuery(queryMap, sql);
            return;
        }
        if (queryMap.containsKey(MUMBLE_SELECT_KEY)) {
            emitSelectStatement(queryMap, sql);
            return;
        }
        if (queryMap.containsKey(MUMBLE_VALUES_KEY)) {
            emitValuesStatement((Map<String, Object>) queryMap.get(MUMBLE_VALUES_KEY), sql, false);
            return;
        }
        handleEndPoint(queryMap, sql);
    }

    @SuppressWarnings("unchecked")
    private void emitUnionQuery(Map<String, Object> unionMap, StringBuilder sql) {
        Object unionObj = unionMap.get(MUMBLE_UNION_KEY);
        List<Object> parts = orderedNumericKeyedList(unionObj);
        for (int i = 0; i < parts.size(); i++) {
            Object part = parts.get(i);
            if (part instanceof Map<?, ?> partMap && partMap.containsKey(MUMBLE_UNION_KEY)) {
                Object unionMeta = partMap.get(MUMBLE_UNION_KEY);
                if (unionMeta instanceof Map<?, ?> metaMap) {
                    Object operator = metaMap.get("operator");
                    Object qualifier = metaMap.get(MUMBLE_QUALIFIER_KEY);
                    if (operator != null) {
                        sql.append(' ').append(operator.toString().toUpperCase());
                    }
                    if (qualifier != null) {
                        sql.append(' ').append(qualifier.toString().toUpperCase());
                    }
                }
                continue;
            }
            if (part instanceof Map<?, ?> partMap) {
                emitQueryBody((Map<String, Object>) partMap, sql);
            }
        }
    }

    private void emitValuesStatement(Map<String, Object> valuesMap, StringBuilder sql, boolean scriptWrapped) {
        if (scriptWrapped) {
            sql.append('(');
        }
        emitInlineValuesClause(valuesMap, sql);
        if (scriptWrapped) {
            sql.append(')');
        }
    }

    private void emitInlineValuesClause(Map<String, Object> valuesMap, StringBuilder sql) {
        sql.append("VALUES");
        emitValuesMatrix(valuesMap.get(MUMBLE_MATRIX_KEY), sql);
        Object aliasObj = valuesMap.get(MUMBLE_ALIAS_KEY);
        if (aliasObj != null) {
            sql.append(" AS ").append(aliasObj);
            Object columnsObj = valuesMap.get(MUMBLE_COLUMNS_KEY);
            if (columnsObj instanceof Map<?, ?> columnsMap) {
                emitInsertColumnList((Map<String, Object>) columnsMap, sql);
            }
        }
    }

    private void emitFromTableReference(Map<?, ?> refMap, StringBuilder sql) {
        if (refMap.containsKey(MUMBLE_TABLE_KEY)) {
            sql.append(' ');
            onTable(refMap.get(MUMBLE_TABLE_KEY), sql);
            return;
        }
        if (refMap.size() == 1) {
            Map.Entry<?, ?> entry = refMap.entrySet().iterator().next();
            sql.append(' ');
            generateStatement(entry.getKey().toString(), entry.getValue(), sql);
        }
    }

    @SuppressWarnings("unchecked")
    private void emitDdlStatement(Map<String, Object> ddlMap, StringBuilder sql) {
        if (ddlMap.containsKey(MUMBLE_CREATE_KEY)) {
            emitCreateStatement((Map<String, Object>) ddlMap.get(MUMBLE_CREATE_KEY), sql);
        } else if (ddlMap.containsKey(MUMBLE_ALTER_KEY)) {
            emitAlterStatement((Map<String, Object>) ddlMap.get(MUMBLE_ALTER_KEY), sql);
        } else if (ddlMap.containsKey(MUMBLE_DROP_KEY)) {
            emitDropStatement((Map<String, Object>) ddlMap.get(MUMBLE_DROP_KEY), sql);
        } else {
            handleEndPoint(ddlMap, sql);
        }
    }

    @SuppressWarnings("unchecked")
    private void emitCreateStatement(Map<String, Object> createMap, StringBuilder sql) {
        sql.append("CREATE");
        Object typeObj = createMap.get(MUMBLE_TYPE_KEY);
        if (typeObj != null) {
            sql.append(' ').append(typeObj.toString().toUpperCase());
        }

        Object nameObj = createMap.get(MUMBLE_NAME_KEY);
        Object tableObj = createMap.get(MUMBLE_TABLE_KEY);
        if (nameObj != null && tableObj == null) {
            sql.append(' ').append(nameObj);
        } else if (tableObj instanceof Map<?, ?> tableMap) {
            sql.append(' ');
            emitQualifiedTableName((Map<String, Object>) tableMap, sql);
        }

        Object columnsObj = createMap.get(MUMBLE_COLUMNS_KEY);
        if (columnsObj != null) {
            emitCreateColumnDefinitions(columnsObj, sql);
        }

        Object queryObj = createMap.get(MUMBLE_QUERY_KEY);
        if (queryObj instanceof Map<?, ?> queryMap && queryMap.containsKey(MUMBLE_SELECT_KEY)) {
            sql.append(" AS ");
            emitSelectStatement((Map<String, Object>) queryMap, sql);
        }

        Object optionsObj = createMap.get(MUMBLE_OPTIONS_KEY);
        if (optionsObj != null) {
            sql.append(' ').append(optionsObj.toString());
        }
    }

    private void emitCreateColumnDefinitions(Object columnsObj, StringBuilder sql) {
        if (columnsObj instanceof String columnsText) {
            sql.append(" (").append(columnsText).append(')');
            return;
        }
        if (!(columnsObj instanceof Map<?, ?> columnsMap)) {
            return;
        }
        List<Object> columns = orderedNumericKeyedList(columnsMap);
        if (columns.isEmpty()) {
            return;
        }
        sql.append(" (");
        for (int i = 0; i < columns.size(); i++) {
            if (i > 0) {
                sql.append(", ");
            }
            Object columnEntry = columns.get(i);
            if (columnEntry instanceof Map<?, ?> columnEntryMap) {
                if (columnEntryMap.containsKey(MUMBLE_COLUMN_KEY)) {
                    onColumn(columnEntryMap.get(MUMBLE_COLUMN_KEY), sql);
                } else {
                    emitCreateColumnDefinition((Map<String, Object>) columnEntryMap, sql);
                }
            }
        }
        sql.append(')');
    }

    private void emitCreateColumnDefinition(Map<String, Object> columnDef, StringBuilder sql) {
        Object columnObj = columnDef.get(MUMBLE_COLUMN_KEY);
        if (columnObj != null) {
            onColumn(columnObj, sql);
        }
        Object datatypeObj = columnDef.get(MUMBLE_DATATYPE_KEY);
        if (datatypeObj != null) {
            sql.append(' ');
            if (datatypeObj instanceof String datatypeText) {
                sql.append(datatypeText);
            } else {
                generateStatement(MUMBLE_DATATYPE_KEY, datatypeObj, sql);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void emitAlterStatement(Map<String, Object> alterMap, StringBuilder sql) {
        sql.append("ALTER");
        Object typeObj = alterMap.get(MUMBLE_TYPE_KEY);
        if (typeObj != null) {
            sql.append(' ').append(typeObj.toString().toUpperCase());
        }
        Object nameObj = alterMap.get("name");
        if (nameObj == null) {
            nameObj = alterMap.get(MUMBLE_TABLE_KEY);
        }
        if (nameObj instanceof Map<?, ?> nameMap) {
            sql.append(' ');
            emitQualifiedTableName((Map<String, Object>) nameMap, sql);
        }
        Object options = alterMap.get("options");
        if (options != null) {
            sql.append(' ').append(options.toString());
        }
    }

    @SuppressWarnings("unchecked")
    private void emitDropStatement(Map<String, Object> dropMap, StringBuilder sql) {
        sql.append("DROP");
        Object typeObj = dropMap.get(MUMBLE_TYPE_KEY);
        if (typeObj != null) {
            sql.append(' ').append(typeObj.toString().toUpperCase());
        }
        Object nameObj = dropMap.get("name");
        if (nameObj instanceof Map<?, ?> nameMap) {
            sql.append(' ');
            emitQualifiedTableName((Map<String, Object>) nameMap, sql);
        }
        Object options = dropMap.get("options");
        if (options != null) {
            sql.append(' ').append(options.toString());
        }
    }

    private void emitQualifiedTableName(Map<String, Object> tableMap, StringBuilder sql) {
        String dbname = (String) tableMap.get(MUMBLE_DATABASE_NAME_KEY);
        String schema = (String) tableMap.get(MUMBLE_SCHEMA_KEY);
        String table = (String) tableMap.get(MUMBLE_TABLE_KEY);
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
    }

    @SuppressWarnings("unchecked")
    private void emitTruncateStatement(Map<String, Object> truncateMap, StringBuilder sql) {
        sql.append("TRUNCATE TABLE");
        Object tableObj = truncateMap.get(MUMBLE_TABLE_KEY);
        if (tableObj == null) {
            tableObj = truncateMap.get("name");
        }
        if (tableObj instanceof Map<?, ?> tableMap) {
            sql.append(' ');
            emitQualifiedTableName((Map<String, Object>) tableMap, sql);
        }
    }

    @SuppressWarnings("unchecked")
    private void emitScriptStatement(Map<String, Object> scriptMap, StringBuilder sql) {
        List<Object> statements = orderedNumericKeyedList(scriptMap);
        for (int i = 0; i < statements.size(); i++) {
            if (i > 0) {
                sql.append(";\n");
            }
            emitScriptItem((Map<String, Object>) statements.get(i), sql);
        }
        if (!statements.isEmpty()) {
            sql.append(';');
        }
    }

    @SuppressWarnings("unchecked")
    private void emitScriptItem(Map<String, Object> itemMap, StringBuilder sql) {
        if (itemMap.containsKey(MUMBLE_WITH_KEY)) {
            emitWithQuery(itemMap, sql);
        } else if (itemMap.containsKey(MUMBLE_SELECT_KEY)) {
            emitSelectStatement(itemMap, sql);
        } else if (itemMap.containsKey(MUMBLE_VALUES_KEY)) {
            emitValuesStatement((Map<String, Object>) itemMap.get(MUMBLE_VALUES_KEY), sql, true);
        } else if (itemMap.containsKey(MUMBLE_INSERT_KEY)) {
            emitInsertStatement((Map<String, Object>) itemMap.get(MUMBLE_INSERT_KEY), sql);
        } else if (itemMap.containsKey(MUMBLE_UPDATE_KEY)) {
            emitUpdateStatement((Map<String, Object>) itemMap.get(MUMBLE_UPDATE_KEY), sql);
        } else if (itemMap.containsKey(MUMBLE_DELETE_KEY)) {
            emitDeleteStatement((Map<String, Object>) itemMap.get(MUMBLE_DELETE_KEY), sql);
        } else if (itemMap.containsKey(MUMBLE_CREATE_KEY)) {
            emitCreateStatement((Map<String, Object>) itemMap.get(MUMBLE_CREATE_KEY), sql);
        } else if (itemMap.containsKey(MUMBLE_TRUNCATE_KEY)) {
            emitTruncateStatement((Map<String, Object>) itemMap.get(MUMBLE_TRUNCATE_KEY), sql);
        } else if (itemMap.containsKey(MUMBLE_ALTER_KEY)) {
            emitAlterStatement((Map<String, Object>) itemMap.get(MUMBLE_ALTER_KEY), sql);
        } else if (itemMap.containsKey(MUMBLE_DROP_KEY)) {
            emitDropStatement((Map<String, Object>) itemMap.get(MUMBLE_DROP_KEY), sql);
        } else {
            handleEndPoint(itemMap, sql);
        }
    }

}
