package mumble;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class SQLParserEndPoints {
    
    public static final String SQLPARSER_PREDICAND_TREE_KEY = "PREDICAND";
    public static final String SQLPARSER_JOIN_EXTENSION_TREE_KEY = "JOIN_EXTENSION";
    public static final String SQLPARSER_INSERT_TREE_KEY = "INSERT";
    public static final String SQLPARSER_UPDATE_TREE_KEY = "UPDATE";
    public static final String SQLPARSER_DELETE_TREE_KEY = "DELETE";
    public static final String SQLPARSER_TRUNCATE_TREE_KEY = "TRUNCATE";
    public static final String SQLPARSER_IN_LIST_TREE_KEY = "IN_LIST";
    public static final String SQLPARSER_CONDITION_TREE_KEY = "CONDITION";
    public static final String SQLPARSER_COLUMN_TREE_KEY = "COLUMN";
    public static final String SQLPARSER_VALUES_TREE_KEY = "VALUES";
    public static final String SQLPARSER_TUPLE_TREE_KEY = "TUPLE";
    public static final String SQLPARSER_DDL_TREE_KEY = "DDL";
    public static final String SQLPARSER_SCRIPT_TREE_KEY = "SCRIPT";
    public static final String SQLPARSER_SQL_TREE_KEY = "SQL";
    public static final String SQLPARSER_QUERY_TREE_KEY = "QUERY";
    
    // Maps to lookup constants by name or value
    private static final Map<String, String> NAME_TO_VALUE_MAP = new HashMap<>();
    private static final Map<String, String> VALUE_TO_NAME_MAP = new HashMap<>();
    
    // Static initializer block to populate maps using reflection
    static {
        try {
            for (Field field : SQLParserEndPoints.class.getDeclaredFields()) {
                if (java.lang.reflect.Modifier.isStatic(field.getModifiers()) && 
                    field.getType() == String.class &&
                    field.getName().startsWith("SQLPARSER_")) {
                    
                    String name = field.getName();
                    String value = (String) field.get(null);
                    
                    NAME_TO_VALUE_MAP.put(name, value);
                    VALUE_TO_NAME_MAP.put(value, name);
                }
            }
        } catch (IllegalAccessException e) {
            throw new ExceptionInInitializerError("Failed to initialize SQLParserEndPoints maps: " + e.getMessage());
        }
    }
    
    /**
     * Returns an unmodifiable map of constant names to their values.
     * @return Map where keys are constant names (e.g., "SQLPARSER_SQL_TREE_KEY") and values are their string values (e.g., "SQL")
     */
    public static Map<String, String> getNameToValueMap() {
        return Collections.unmodifiableMap(NAME_TO_VALUE_MAP);
    }
    
    /**
     * Returns an unmodifiable map of constant values to their names.
     * @return Map where keys are constant values (e.g., "SQL") and values are their names (e.g., "SQLPARSER_SQL_TREE_KEY")
     */
    public static Map<String, String> getValueToNameMap() {
        return Collections.unmodifiableMap(VALUE_TO_NAME_MAP);
    }
    
    /**
     * Gets the constant name for a given value.
     * @param value The string value to look up
     * @return The name of the constant, or null if not found
     */
    public static String getNameForValue(String value) {
        return VALUE_TO_NAME_MAP.get(value);
    }
    
    /**
     * Gets the constant value for a given name.
     * @param name The constant name to look up
     * @return The value of the constant, or null if not found
     */
    public static String getValueForName(String name) {
        return NAME_TO_VALUE_MAP.get(name);
    }

    /**
     * Gets the lower-case text value for a given constant name.
     * @param name The constant name to look up
     * @return The lower-case value of the constant, or null if not found
     */
    public static String getLowerCaseValueForName(String name) {
        String value = NAME_TO_VALUE_MAP.get(name);
        return value == null ? null : value.toLowerCase(Locale.ROOT);
    }

    /**
     * Gets the lower-case text value for a given constant value.
     * @param value The constant value to normalize
     * @return The lower-case value, or null if value is null
     */
    public static String getLowerCaseValueForValue(String value) {
        return value == null ? null : value.toLowerCase(Locale.ROOT);
    }
}
