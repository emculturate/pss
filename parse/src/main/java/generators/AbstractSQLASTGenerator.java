package generators;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import mumble.MumbleConstants;
import static mumble.MumbleConstants.MUMBLE_ALIAS_KEY;
import static mumble.MumbleConstants.MUMBLE_AND_KEY;
import static mumble.MumbleConstants.MUMBLE_ASSIGNMENTS_KEY;
import static mumble.MumbleConstants.MUMBLE_BETWEEN_KEY;
import static mumble.MumbleConstants.MUMBLE_BRACKET_DIRECTION_KEY;
import static mumble.MumbleConstants.MUMBLE_BRACKET_FRAME_KEY;
import static mumble.MumbleConstants.MUMBLE_CALCULATION_KEY;
import static mumble.MumbleConstants.MUMBLE_CASE_KEY;
import static mumble.MumbleConstants.MUMBLE_CLAUSES_KEY;
import static mumble.MumbleConstants.MUMBLE_COLUMNS_KEY;
import static mumble.MumbleConstants.MUMBLE_COLUMN_KEY;
import static mumble.MumbleConstants.MUMBLE_CONCATENATE_KEY;
import static mumble.MumbleConstants.MUMBLE_CONDITION_KEY;
import static mumble.MumbleConstants.MUMBLE_DATABASE_NAME_KEY;
import static mumble.MumbleConstants.MUMBLE_DATATYPE_KEY;
import static mumble.MumbleConstants.MUMBLE_ELSE_KEY;
import static mumble.MumbleConstants.MUMBLE_ESCAPE_KEY;
import static mumble.MumbleConstants.MUMBLE_EXISTS_KEY;
import static mumble.MumbleConstants.MUMBLE_FILTERS_KEY;
import static mumble.MumbleConstants.MUMBLE_FOLLOWING_KEY;
import static mumble.MumbleConstants.MUMBLE_FROM_KEY;
import static mumble.MumbleConstants.MUMBLE_FUNCTION_KEY;
import static mumble.MumbleConstants.MUMBLE_FUNCTION_NAME_KEY;
import static mumble.MumbleConstants.MUMBLE_GROUPBY_KEY;
import static mumble.MumbleConstants.MUMBLE_GROUPED_BY_KEY;
import static mumble.MumbleConstants.MUMBLE_HAVING_KEY;
import static mumble.MumbleConstants.MUMBLE_ILIKE_ANY_KEY;
import static mumble.MumbleConstants.MUMBLE_INSERT_INTO_KEY;
import static mumble.MumbleConstants.MUMBLE_INSERT_INTO_OVERWRITE_KEY;
import static mumble.MumbleConstants.MUMBLE_INSERT_KEY;
import static mumble.MumbleConstants.MUMBLE_INSERT_PREAMBLE_KEY;
import static mumble.MumbleConstants.MUMBLE_INTERFACE_KEY;
import static mumble.MumbleConstants.MUMBLE_INTERSECT_KEY;
import static mumble.MumbleConstants.MUMBLE_INTO_KEY;
import static mumble.MumbleConstants.MUMBLE_IN_KEY;
import static mumble.MumbleConstants.MUMBLE_IN_LIST_KEY;
import static mumble.MumbleConstants.MUMBLE_ITEM_KEY;
import static mumble.MumbleConstants.MUMBLE_JOIN_EXTENSION_KEY;
import static mumble.MumbleConstants.MUMBLE_JOIN_KEY;
import static mumble.MumbleConstants.MUMBLE_JOIN_ON_KEY;
import static mumble.MumbleConstants.MUMBLE_LEFT_FACTOR_KEY;
import static mumble.MumbleConstants.MUMBLE_LENGTH_KEY;
import static mumble.MumbleConstants.MUMBLE_LIKE_ANY_KEY;
import static mumble.MumbleConstants.MUMBLE_LIKE_ANY_LIST_KEY;
import static mumble.MumbleConstants.MUMBLE_LIMIT_KEY;
import static mumble.MumbleConstants.MUMBLE_LIST_KEY;
import static mumble.MumbleConstants.MUMBLE_LITERAL_KEY;
import static mumble.MumbleConstants.MUMBLE_LOOKUP_KEY;
import static mumble.MumbleConstants.MUMBLE_MATRIX_KEY;
import static mumble.MumbleConstants.MUMBLE_NAME_KEY;
import static mumble.MumbleConstants.MUMBLE_NOT_IN_LIST_KEY;
import static mumble.MumbleConstants.MUMBLE_NOT_KEY;
import static mumble.MumbleConstants.MUMBLE_NOT_LIKE_ANY_LIST_KEY;
import static mumble.MumbleConstants.MUMBLE_NULL_HANDLING_KEY;
import static mumble.MumbleConstants.MUMBLE_NULL_LITERAL_KEY;
import static mumble.MumbleConstants.MUMBLE_NULL_ORDER_KEY;
import static mumble.MumbleConstants.MUMBLE_OFFSET_KEY;
import static mumble.MumbleConstants.MUMBLE_OPERAND_KEY;
import static mumble.MumbleConstants.MUMBLE_OPERATOR_KEY;
import static mumble.MumbleConstants.MUMBLE_ORDERBY_KEY;
import static mumble.MumbleConstants.MUMBLE_ORDERED_BY_KEY;
import static mumble.MumbleConstants.MUMBLE_OR_KEY;
import static mumble.MumbleConstants.MUMBLE_OVER_KEY;
import static mumble.MumbleConstants.MUMBLE_PARAMETERS_KEY;
import static mumble.MumbleConstants.MUMBLE_PARENTHESES_KEY;
import static mumble.MumbleConstants.MUMBLE_PARTITION_BY_KEY;
import static mumble.MumbleConstants.MUMBLE_PARTS_KEY;
import static mumble.MumbleConstants.MUMBLE_PRECEDING_KEY;
import static mumble.MumbleConstants.MUMBLE_PRECISION_KEY;
import static mumble.MumbleConstants.MUMBLE_PREDICAND_KEY;
import static mumble.MumbleConstants.MUMBLE_PUML_CONSTANT_KEY;
import static mumble.MumbleConstants.MUMBLE_QUALIFIER_KEY;
import static mumble.MumbleConstants.MUMBLE_QUERY_DICTIONARY_KEY;
import static mumble.MumbleConstants.MUMBLE_QUERY_KEY;
import static mumble.MumbleConstants.MUMBLE_RANGE_BEGIN_KEY;
import static mumble.MumbleConstants.MUMBLE_RANGE_END_KEY;
import static mumble.MumbleConstants.MUMBLE_RETURNING_KEY;
import static mumble.MumbleConstants.MUMBLE_RIGHT_FACTOR_KEY;
import static mumble.MumbleConstants.MUMBLE_ROW_KEY;
import static mumble.MumbleConstants.MUMBLE_SCALAR_SUBQUERY_ALIASES_KEY;
import static mumble.MumbleConstants.MUMBLE_SCALE_KEY;
import static mumble.MumbleConstants.MUMBLE_SCHEMA_KEY;
import static mumble.MumbleConstants.MUMBLE_SELECT_DIRECTION_KEY;
import static mumble.MumbleConstants.MUMBLE_SELECT_KEY;
import static mumble.MumbleConstants.MUMBLE_SET_KEY;
import static mumble.MumbleConstants.MUMBLE_SORT_ORDER_KEY;
import static mumble.MumbleConstants.MUMBLE_SUBSTITUTION_KEY;
import static mumble.MumbleConstants.MUMBLE_SYMMETRY_KEY;
import static mumble.MumbleConstants.MUMBLE_TABLE_ALIAS_KEY;
import static mumble.MumbleConstants.MUMBLE_TABLE_DICTIONARY_KEY;
import static mumble.MumbleConstants.MUMBLE_TABLE_KEY;
import static mumble.MumbleConstants.MUMBLE_TABLE_REF_KEY;
import static mumble.MumbleConstants.MUMBLE_THEN_KEY;
import static mumble.MumbleConstants.MUMBLE_TO_KEY;
import static mumble.MumbleConstants.MUMBLE_TRIM_CHARACTER_KEY;
import static mumble.MumbleConstants.MUMBLE_TYPE_KEY;
import static mumble.MumbleConstants.MUMBLE_UNION_KEY;
import static mumble.MumbleConstants.MUMBLE_UNKNOWN_KEY;
import static mumble.MumbleConstants.MUMBLE_UNRESOLVED_COLUMN_KEY;
import static mumble.MumbleConstants.MUMBLE_UPDATE_KEY;
import static mumble.MumbleConstants.MUMBLE_VALUES_KEY;
import static mumble.MumbleConstants.MUMBLE_VALUE_KEY;
import static mumble.MumbleConstants.MUMBLE_WHEN_KEY;
import static mumble.MumbleConstants.MUMBLE_WHERE_KEY;
import static mumble.MumbleConstants.MUMBLE_WINDOW_FUNCTION_KEY;
import static mumble.MumbleConstants.MUMBLE_WITH_KEY;

public abstract class AbstractSQLASTGenerator extends AbstractASTGenerator {
    // This class provides a basic foundation for walking through SQL ASTs
    // in a depth-first search. As it proceeds it will call stubbed out methods
    // that can be overridden by subclasses to handle specific SQL constructs.
    // Each stubbed out method will take one node of the SQL AST and will create an output
    // text representation of that node. Different actual generator classes will produce different kinds of output,
    // such as SQL statements, or other text based things.

    /**
     * Anchor method for SQL reconstruction.
     * Recursively routes each MUMBLE key to a dedicated child method.
     */
    public String generateStatement(Map<String, Object> astRoot) {
        StringBuilder sql = new StringBuilder();
        // Validate that the map has exactly one key
        if (astRoot == null || astRoot.size() != 1) {
            throw new IllegalArgumentException("AST root map must have exactly one key, found: " + (astRoot == null ? 0 : astRoot.size()));
        }
        String mumbleKey = astRoot.keySet().iterator().next();
        // Check if the key is a valid SQLParserEndPoint value
        if (mumble.SQLParserEndPoints.getNameForValue(mumbleKey) == null) {
            throw new IllegalArgumentException("AST root key '" + mumbleKey + "' is not a valid SQLParserEndPoint value");
        }
        generateStatement(mumbleKey, astRoot.get(mumbleKey), sql);
        return sql.toString();
    }

    /**
     * MUMBLE key router. This method is mutually recursive with appendNode().
     */
    protected void generateStatement(String mumbleKey, Object node, StringBuilder sql) {
        if (mumbleKey == null) {
            System.out.println("Unexpected null MUMBLE key for node: " + node.toString());
            return;
        }

        switch (mumbleKey) {
            // ...existing MUMBLE cases...
            case mumble.SQLParserEndPoints.SQLPARSER_PREDICAND_TREE_KEY -> onSQLParserPredicand(node, sql);
            case mumble.SQLParserEndPoints.SQLPARSER_JOIN_EXTENSION_TREE_KEY -> onSQLParserJoinExtension(node, sql);
            case mumble.SQLParserEndPoints.SQLPARSER_INSERT_TREE_KEY -> onSQLParserInsert(node, sql);
            case mumble.SQLParserEndPoints.SQLPARSER_UPDATE_TREE_KEY -> onSQLParserUpdate(node, sql);
            case mumble.SQLParserEndPoints.SQLPARSER_IN_LIST_TREE_KEY -> onSQLParserInList(node, sql);
            case mumble.SQLParserEndPoints.SQLPARSER_CONDITION_TREE_KEY -> onSQLParserCondition(node, sql);
            case mumble.SQLParserEndPoints.SQLPARSER_COLUMN_TREE_KEY -> onSQLParserColumn(node, sql);
            case mumble.SQLParserEndPoints.SQLPARSER_VALUES_TREE_KEY -> onSQLParserValues(node, sql);
            case mumble.SQLParserEndPoints.SQLPARSER_TUPLE_TREE_KEY -> onSQLParserTuple(node, sql);
            case mumble.SQLParserEndPoints.SQLPARSER_SQL_TREE_KEY -> onSQLParserSQL(node, sql);
            case mumble.SQLParserEndPoints.SQLPARSER_QUERY_TREE_KEY -> onSQLParserQuery(node, sql);
            // ...existing code...
            case MUMBLE_ALIAS_KEY -> onAlias(node, sql);
            case MUMBLE_AND_KEY -> onAnd(node, sql);
            case MUMBLE_ASSIGNMENTS_KEY -> onAssignments(node, sql);
            case MUMBLE_BETWEEN_KEY -> onBetween(node, sql);
            case MUMBLE_BRACKET_FRAME_KEY -> onBracketFrame(node, sql);
            case MUMBLE_BRACKET_DIRECTION_KEY -> onBracketDirection(node, sql);
            case MUMBLE_CALCULATION_KEY -> onCalculation(node, sql);
            case MUMBLE_CASE_KEY -> onCase(node, sql);
            case MUMBLE_CLAUSES_KEY -> onClauses(node, sql);
            case MUMBLE_COLUMN_KEY -> onColumn(node, sql);
            case MUMBLE_COLUMNS_KEY -> onColumns(node, sql);
            case MUMBLE_CONCATENATE_KEY -> onConcatenate(node, sql);
            case MUMBLE_CONDITION_KEY -> onCondition(node, sql);
            case MUMBLE_DATATYPE_KEY -> onDatatype(node, sql);
            case MUMBLE_DATABASE_NAME_KEY -> onDatabaseName(node, sql);
            case MUMBLE_ELSE_KEY -> onElse(node, sql);
            case MUMBLE_ESCAPE_KEY -> onEscape(node, sql);
            case MUMBLE_EXISTS_KEY -> onExists(node, sql);
            case MUMBLE_FILTERS_KEY -> onFilters(node, sql);
            case MUMBLE_FOLLOWING_KEY -> onFollowing(node, sql);
            case MUMBLE_FROM_KEY -> onFrom(node, sql);
            case MUMBLE_FUNCTION_KEY -> onFunction(node, sql);
            case MUMBLE_FUNCTION_NAME_KEY -> onFunctionName(node, sql);
            case MUMBLE_GROUPED_BY_KEY -> onGroupedBy(node, sql);
            case MUMBLE_GROUPBY_KEY -> onGroupBy(node, sql);
            case MUMBLE_HAVING_KEY -> onHaving(node, sql);
            case MUMBLE_ILIKE_ANY_KEY -> onIlikeAny(node, sql);
            case MUMBLE_IN_KEY -> onIn(node, sql);
            case MUMBLE_IN_LIST_KEY -> onInList(node, sql);
            case MUMBLE_INSERT_KEY -> onInsert(node, sql);
            case MUMBLE_INSERT_INTO_KEY -> onInsertInto(node, sql);
            case MUMBLE_INSERT_INTO_OVERWRITE_KEY -> onInsertIntoOverwrite(node, sql);
            case MUMBLE_INTERFACE_KEY -> onInterface(node, sql);
            case MUMBLE_INTERSECT_KEY -> onIntersect(node, sql);
            case MUMBLE_INTO_KEY -> onInto(node, sql);
            case MUMBLE_ITEM_KEY -> onItem(node, sql);
            case MUMBLE_JOIN_EXTENSION_KEY -> onJoinExtension(node, sql);
            case MUMBLE_JOIN_KEY -> onJoin(node, sql);
            case MUMBLE_JOIN_ON_KEY -> onJoinOn(node, sql);
            case MUMBLE_LEFT_FACTOR_KEY -> onLeftFactor(node, sql);
            case MUMBLE_LENGTH_KEY -> onLength(node, sql);
            case MUMBLE_LIKE_ANY_KEY -> onLikeAny(node, sql);
            case MUMBLE_LIKE_ANY_LIST_KEY -> onLikeAnyList(node, sql);
            case MUMBLE_LIMIT_KEY -> onLimit(node, sql);
            case MUMBLE_LIST_KEY -> onList(node, sql);
            case MUMBLE_LITERAL_KEY -> onLiteral(node, sql);
            case MUMBLE_LOOKUP_KEY -> onLookup(node, sql);
            case MUMBLE_MATRIX_KEY -> onMatrix(node, sql);
            case MUMBLE_NAME_KEY -> onName(node, sql);
            case MUMBLE_NOT_IN_LIST_KEY -> onNotInList(node, sql);
            case MUMBLE_NOT_LIKE_ANY_LIST_KEY -> onNotLikeAnyList(node, sql);
            case MUMBLE_NOT_KEY -> onNot(node, sql);
            case MUMBLE_NULL_LITERAL_KEY -> onNullLiteral(node, sql);
            case MUMBLE_NULL_ORDER_KEY -> onNullOrder(node, sql);
            case MUMBLE_OFFSET_KEY -> onOffset(node, sql);
            case MUMBLE_OPERAND_KEY -> onOperand(node, sql);
            case MUMBLE_OPERATOR_KEY -> onOperator(node, sql);
            case MUMBLE_OR_KEY -> onOr(node, sql);
            case MUMBLE_ORDERED_BY_KEY -> onOrderedBy(node, sql);
            case MUMBLE_ORDERBY_KEY -> onOrderBy(node, sql);
            case MUMBLE_OVER_KEY -> onOver(node, sql);
            case MUMBLE_PARAMETERS_KEY -> onParameters(node, sql);
            case MUMBLE_PARENTHESES_KEY -> onParentheses(node, sql);
            case MUMBLE_PARTITION_BY_KEY -> onPartitionBy(node, sql);
            case MUMBLE_PARTS_KEY -> onParts(node, sql);
            case MUMBLE_INSERT_PREAMBLE_KEY -> onInsertPreamble(node, sql);
            case MUMBLE_PRECISION_KEY -> onPrecision(node, sql);
            case MUMBLE_PRECEDING_KEY -> onPreceding(node, sql);
            case MUMBLE_PREDICAND_KEY -> onPredicand(node, sql);
            case MUMBLE_PUML_CONSTANT_KEY -> onPumlConstant(node, sql);
            case MUMBLE_QUALIFIER_KEY -> onQualifier(node, sql);
            case MUMBLE_QUERY_KEY -> onQuery(node, sql);
            case MUMBLE_QUERY_DICTIONARY_KEY -> onQueryDictionary(node, sql);
            case MUMBLE_RANGE_BEGIN_KEY -> onRangeBegin(node, sql);
            case MUMBLE_RANGE_END_KEY -> onRangeEnd(node, sql);
            case MUMBLE_RETURNING_KEY -> onReturning(node, sql);
            case MUMBLE_RIGHT_FACTOR_KEY -> onRightFactor(node, sql);
            case MUMBLE_ROW_KEY -> onRow(node, sql);
            case MUMBLE_SCALAR_SUBQUERY_ALIASES_KEY -> onScalarSubqueryAliases(node, sql);
            case MUMBLE_SCALE_KEY -> onScale(node, sql);
            case MUMBLE_SCHEMA_KEY -> onSchema(node, sql);
            case MUMBLE_SELECT_KEY -> onSelect(node, sql);
            case MUMBLE_SELECT_DIRECTION_KEY -> onSelectDirection(node, sql);
            case MUMBLE_NULL_HANDLING_KEY -> onNullHandling(node, sql);
            case MUMBLE_SET_KEY -> onSet(node, sql);
            case MUMBLE_SORT_ORDER_KEY -> onSortOrder(node, sql);
            case MUMBLE_SUBSTITUTION_KEY -> onSubstitution(node, sql);
            case MUMBLE_SYMMETRY_KEY -> onSymmetry(node, sql);
            case MUMBLE_TABLE_ALIAS_KEY -> onTableAlias(node, sql);
            case MUMBLE_TABLE_DICTIONARY_KEY -> onTableDictionary(node, sql);
            case MUMBLE_TABLE_KEY -> onTable(node, sql);
            case MUMBLE_TABLE_REF_KEY -> onTableRef(node, sql);
            case MUMBLE_THEN_KEY -> onThen(node, sql);
            case MUMBLE_TO_KEY -> onTo(node, sql);
            case MUMBLE_TRIM_CHARACTER_KEY -> onTrimCharacter(node, sql);
            case MUMBLE_TYPE_KEY -> onType(node, sql);
            case MUMBLE_UNKNOWN_KEY -> onUnknown(node, sql);
            case MUMBLE_UNION_KEY -> onUnion(node, sql);
            case MUMBLE_UNRESOLVED_COLUMN_KEY -> onUnresolvedColumn(node, sql);
            case MUMBLE_UPDATE_KEY -> onUpdate(node, sql);
            case MUMBLE_VALUES_KEY -> onValues(node, sql);
            case MUMBLE_VALUE_KEY -> onValue(node, sql);
            case MUMBLE_WHEN_KEY -> onWhen(node, sql);
            case MUMBLE_WHERE_KEY -> onWhere(node, sql);
            case MUMBLE_WINDOW_FUNCTION_KEY -> onWindowFunction(node, sql);
            case MUMBLE_WITH_KEY -> onWith(node, sql);
            default -> appendNode(node, sql);
        }
    }

    // ---------------------------------------------------------------------------------------------
    // Core recursive tree navigation helpers
    // ---------------------------------------------------------------------------------------------

    protected void appendNode(Object node, StringBuilder sql) {
        if (node == null) {
            return;
        }

        if (node instanceof String text) {
            appendLeafText(text, sql);
            return;
        }
        System.out.println("Unexpected node: " + node.toString());
    }

    protected void appendLeafText(String text, StringBuilder sql) {
        if (text == null || text.isBlank()) {
            return;
        }
        if (!sql.isEmpty()) {
        }
   }

    /**
     * Given a node that is a Map with numeric string keys (e.g., "1", "2", ...),
     * returns a List<Object> in sequential order by key. Returns empty list if not a map.
     */
    protected List<Object> orderedNumericKeyedList(Object node) {
        if (!(node instanceof Map<?, ?> mapNode)) {
            return new ArrayList<>();
        }
        List<Object> ordered = new ArrayList<>();
        int count = mapNode.size();
        for (int i = 1; i <= count; i++) {
            String key = String.valueOf(i);
            if (mapNode.containsKey(key)) {
                ordered.add(mapNode.get(key));
            }
        }
        return ordered;
    }

    private boolean isIntegerKey(String key) {
        if (key == null || key.isEmpty()) {
            return false;
        }
        for (int i = 0; i < key.length(); i++) {
            if (!Character.isDigit(key.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    private boolean isMumbleKey(String key) {
        return MumbleConstants.getValueToNameMap().containsKey(key);
    }

    // ---------------------------------------------------------------------------------------------
    // Default secondary handlers. Subclasses can override any of these.
    // ---------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------
    // SQLParserEndPoints handlers (to be overridden by subclasses if needed)
    // ---------------------------------------------------------------------------------------------
    protected void onSQLParserPredicand(Object node, StringBuilder sql) { handleStructured(node, sql); }
    protected void onSQLParserJoinExtension(Object node, StringBuilder sql) { handleStructured(node, sql); }
    protected void onSQLParserInsert(Object node, StringBuilder sql) { handleStructured(node, sql); }
    protected void onSQLParserUpdate(Object node, StringBuilder sql) { handleStructured(node, sql); }
    protected void onSQLParserInList(Object node, StringBuilder sql) { handleStructured(node, sql); }
    protected void onSQLParserCondition(Object node, StringBuilder sql) { handleStructured(node, sql); }
    protected void onSQLParserColumn(Object node, StringBuilder sql) { handleStructured(node, sql); }
    protected void onSQLParserValues(Object node, StringBuilder sql) { handleStructured(node, sql); }
    protected void onSQLParserTuple(Object node, StringBuilder sql) { handleStructured(node, sql); }
    protected void onSQLParserSQL(Object node, StringBuilder sql) { handleStructured(node, sql); }
    protected void onSQLParserQuery(Object node, StringBuilder sql) { handleStructured(node, sql); }

    // ---------------------------------------------------------------------------------------------
    // MUMBLE CONSTANT handlers (to be overridden by subclasses if needed)
    // ---------------------------------------------------------------------------------------------
    protected void onAlias(Object node, StringBuilder sql) { handleTerminalOrRecursive(node, sql); }
    protected void onAnd(Object node, StringBuilder sql) { handleKeywordWithNode("AND", node, sql); }
    protected void onAssignments(Object node, StringBuilder sql) { appendCommaSeparated(node, sql); }
    protected void onBetween(Object node, StringBuilder sql) { handleKeywordWithNode("BETWEEN", node, sql); }
    protected void onBracketFrame(Object node, StringBuilder sql) { handleStructured(node, sql); }
    protected void onBracketDirection(Object node, StringBuilder sql) { handleTerminalOrRecursive(node, sql); }
    protected void onCalculation(Object node, StringBuilder sql) { handleStructured(node, sql); }
    protected void onCase(Object node, StringBuilder sql) { handleStructured(node, sql); }
    protected void onClauses(Object node, StringBuilder sql) { handleStructured(node, sql); }
    protected void onColumn(Object node, StringBuilder sql) { handleStructured(node, sql); }
    protected void onColumns(Object node, StringBuilder sql) { handleStructured(node, sql); }
    protected void onConcatenate(Object node, StringBuilder sql) { handleStructured(node, sql); }
    protected void onCondition(Object node, StringBuilder sql) { handleStructured(node, sql); }
    protected void onDatatype(Object node, StringBuilder sql) { handleTerminalOrRecursive(node, sql); }
    protected void onDatabaseName(Object node, StringBuilder sql) { handleTerminalOrRecursive(node, sql); }
    protected void onElse(Object node, StringBuilder sql) { handleKeywordWithNode("ELSE", node, sql); }
    protected void onEscape(Object node, StringBuilder sql) { handleStructured(node, sql); }
    protected void onExists(Object node, StringBuilder sql) { handleKeywordWithNode("EXISTS", node, sql); }
    protected void onFilters(Object node, StringBuilder sql) { handleStructured(node, sql); }
    protected void onFollowing(Object node, StringBuilder sql) { handleTerminalOrRecursive(node, sql); }
    protected void onFrom(Object node, StringBuilder sql) { handleKeywordWithNode("FROM", node, sql); }
    protected void onFunction(Object node, StringBuilder sql) { handleStructured(node, sql); }
    protected void onFunctionName(Object node, StringBuilder sql) { handleTerminalOrRecursive(node, sql); }
    protected void onGroupedBy(Object node, StringBuilder sql) { handleStructured(node, sql); }
    protected void onGroupBy(Object node, StringBuilder sql) { handleKeywordWithNode("GROUP BY", node, sql); }
    protected void onHaving(Object node, StringBuilder sql) { handleKeywordWithNode("HAVING", node, sql); }
    protected void onIlikeAny(Object node, StringBuilder sql) { handleKeywordWithNode("ILIKE ANY", node, sql); }
    protected void onIn(Object node, StringBuilder sql) { handleKeywordWithNode("IN", node, sql); }
    protected void onInList(Object node, StringBuilder sql) { appendParenthesized(node, sql); }
    protected void onInsert(Object node, StringBuilder sql) { handleKeywordWithNode("INSERT", node, sql); }
    protected void onInsertInto(Object node, StringBuilder sql) { handleKeywordWithNode("INTO", node, sql); }
    protected void onInsertIntoOverwrite(Object node, StringBuilder sql) { handleKeywordWithNode("OVERWRITE INTO", node, sql); }
    protected void onInterface(Object node, StringBuilder sql) { handleStructured(node, sql); }
    protected void onIntersect(Object node, StringBuilder sql) { handleKeywordWithNode("INTERSECT", node, sql); }
    protected void onInto(Object node, StringBuilder sql) { handleKeywordWithNode("INTO", node, sql); }
    protected void onItem(Object node, StringBuilder sql) { handleStructured(node, sql); }
    protected void onJoinExtension(Object node, StringBuilder sql) { handleStructured(node, sql); }
    protected void onJoin(Object node, StringBuilder sql) { handleKeywordWithNode("JOIN", node, sql); }
    protected void onJoinOn(Object node, StringBuilder sql) { handleKeywordWithNode("ON", node, sql); }
    protected void onLeftFactor(Object node, StringBuilder sql) { handleStructured(node, sql); }
    protected void onLength(Object node, StringBuilder sql) { handleTerminalOrRecursive(node, sql); }
    protected void onLikeAny(Object node, StringBuilder sql) { handleKeywordWithNode("LIKE ANY", node, sql); }
    protected void onLikeAnyList(Object node, StringBuilder sql) { appendParenthesized(node, sql); }
    protected void onLimit(Object node, StringBuilder sql) { handleKeywordWithNode("LIMIT", node, sql); }
    protected void onList(Object node, StringBuilder sql) { appendCommaSeparated(node, sql); }
    protected void onLiteral(Object node, StringBuilder sql) { handleTerminalOrRecursive(node, sql); }
    protected void onLookup(Object node, StringBuilder sql) { handleStructured(node, sql); }
    protected void onMatrix(Object node, StringBuilder sql) { handleStructured(node, sql); }
    protected void onName(Object node, StringBuilder sql) { handleTerminalOrRecursive(node, sql); }
    protected void onNotInList(Object node, StringBuilder sql) { handleKeywordWithNode("NOT IN", node, sql); }
    protected void onNotLikeAnyList(Object node, StringBuilder sql) { handleKeywordWithNode("NOT LIKE ANY", node, sql); }
    protected void onNot(Object node, StringBuilder sql) { handleKeywordWithNode("NOT", node, sql); }
    protected void onNullLiteral(Object node, StringBuilder sql) { handleTerminalOrRecursive(node, sql); }
    protected void onNullOrder(Object node, StringBuilder sql) { handleStructured(node, sql); }
    protected void onOffset(Object node, StringBuilder sql) { handleKeywordWithNode("OFFSET", node, sql); }
    protected void onOperand(Object node, StringBuilder sql) { handleStructured(node, sql); }
    protected void onOperator(Object node, StringBuilder sql) { handleTerminalOrRecursive(node, sql); }
    protected void onOr(Object node, StringBuilder sql) { handleKeywordWithNode("OR", node, sql); }
    protected void onOrderedBy(Object node, StringBuilder sql) { handleStructured(node, sql); }
    protected void onOrderBy(Object node, StringBuilder sql) { handleKeywordWithNode("ORDER BY", node, sql); }
    protected void onOver(Object node, StringBuilder sql) { handleKeywordWithNode("OVER", node, sql); }
    protected void onParameters(Object node, StringBuilder sql) { appendParenthesized(node, sql); }
    protected void onParentheses(Object node, StringBuilder sql) { appendParenthesized(node, sql); }
    protected void onPartitionBy(Object node, StringBuilder sql) { handleKeywordWithNode("PARTITION BY", node, sql); }
    protected void onParts(Object node, StringBuilder sql) { handleStructured(node, sql); }
    protected void onInsertPreamble(Object node, StringBuilder sql) { handleStructured(node, sql); }
    protected void onPrecision(Object node, StringBuilder sql) { handleStructured(node, sql); }
    protected void onPreceding(Object node, StringBuilder sql) { handleTerminalOrRecursive(node, sql); }
    protected void onPredicand(Object node, StringBuilder sql) { handleStructured(node, sql); }
    protected void onPumlConstant(Object node, StringBuilder sql) { handleStructured(node, sql); }
    protected void onQualifier(Object node, StringBuilder sql) { handleTerminalOrRecursive(node, sql); }
    protected void onQuery(Object node, StringBuilder sql) { handleStructured(node, sql); }
    protected void onQueryDictionary(Object node, StringBuilder sql) { handleStructured(node, sql); }
    protected void onRangeBegin(Object node, StringBuilder sql) { handleTerminalOrRecursive(node, sql); }
    protected void onRangeEnd(Object node, StringBuilder sql) { handleTerminalOrRecursive(node, sql); }
    protected void onReturning(Object node, StringBuilder sql) { handleKeywordWithNode("RETURNING", node, sql); }
    protected void onRightFactor(Object node, StringBuilder sql) { handleStructured(node, sql); }
    protected void onRow(Object node, StringBuilder sql) { handleStructured(node, sql); }
    protected void onScalarSubqueryAliases(Object node, StringBuilder sql) { handleStructured(node, sql); }
    protected void onScale(Object node, StringBuilder sql) { handleStructured(node, sql); }
    protected void onSchema(Object node, StringBuilder sql) { handleTerminalOrRecursive(node, sql); }
    protected void onSelect(Object node, StringBuilder sql) { handleKeywordWithNode("SELECT", node, sql); }
    protected void onSelectDirection(Object node, StringBuilder sql) { handleTerminalOrRecursive(node, sql); }
    protected void onNullHandling(Object node, StringBuilder sql) { handleTerminalOrRecursive(node, sql); }
    protected void onSet(Object node, StringBuilder sql) { handleKeywordWithNode("SET", node, sql); }
    protected void onSortOrder(Object node, StringBuilder sql) { handleTerminalOrRecursive(node, sql); }
    protected void onSubstitution(Object node, StringBuilder sql) { handleStructured(node, sql); }
    protected void onSymmetry(Object node, StringBuilder sql) { handleTerminalOrRecursive(node, sql); }
    protected void onTableAlias(Object node, StringBuilder sql) { handleStructured(node, sql); }
    protected void onTableDictionary(Object node, StringBuilder sql) { handleStructured(node, sql); }
    protected void onTable(Object node, StringBuilder sql) { handleStructured(node, sql); }
    protected void onTableRef(Object node, StringBuilder sql) { handleStructured(node, sql); }
    protected void onThen(Object node, StringBuilder sql) { handleKeywordWithNode("THEN", node, sql); }
    protected void onTo(Object node, StringBuilder sql) { handleStructured(node, sql); }
    protected void onTrimCharacter(Object node, StringBuilder sql) { handleTerminalOrRecursive(node, sql); }
    protected void onType(Object node, StringBuilder sql) { handleTerminalOrRecursive(node, sql); }
    protected void onUnknown(Object node, StringBuilder sql) { handleStructured(node, sql); }
    protected void onUnion(Object node, StringBuilder sql) { handleKeywordWithNode("UNION", node, sql); }
    protected void onUnresolvedColumn(Object node, StringBuilder sql) { handleStructured(node, sql); }
    protected void onUpdate(Object node, StringBuilder sql) { handleKeywordWithNode("UPDATE", node, sql); }
    protected void onValues(Object node, StringBuilder sql) { handleKeywordWithNode("VALUES", node, sql); }
    protected void onValue(Object node, StringBuilder sql) { handleTerminalOrRecursive(node, sql); }
    protected void onWhen(Object node, StringBuilder sql) { handleKeywordWithNode("WHEN", node, sql); }
    protected void onWhere(Object node, StringBuilder sql) { handleKeywordWithNode("WHERE", node, sql); }
    protected void onWindowFunction(Object node, StringBuilder sql) { handleStructured(node, sql); }
    protected void onWith(Object node, StringBuilder sql) { handleKeywordWithNode("WITH", node, sql); }

    protected void handleTerminalOrRecursive(Object node, StringBuilder sql) {
        if (node == null) {
            return;
        }
        if (node instanceof String text) {
            appendLeafText(text, sql);
            return;
        }
        appendNode(node, sql);
    }

    protected void handleStructured(Object node, StringBuilder sql) {
        appendNode(node, sql);
    }

    protected void handleKeywordWithNode(String keyword, Object node, StringBuilder sql) {
        appendLeafText(keyword, sql);
        appendNode(node, sql);
    }

    protected void appendParenthesized(Object node, StringBuilder sql) {
        appendLeafText("(", sql);
        appendNode(node, sql);
        appendLeafText(")", sql);
    }

    protected void appendCommaSeparated(Object node, StringBuilder sql) {
        if (node == null) {
            return;
        }

        if (node instanceof List<?> listNode) {
            for (int i = 0; i < listNode.size(); i++) {
                if (i > 0) {
                    sql.append(",");
                }
                appendNode(listNode.get(i), sql);
            }
            return;
        }
    }

}
