package mumble;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/*
 * These constants are the standards used by the AST Walker Helper when needed to track certain
 * elements of the AST tree. Each Grammar can substitute their own constants for these
 * to provide a consistent set of keys for the AST Walker Helper to use when traversing the trees
 * generated from that Grammar.
 */
public final class ASTWalkerHelperConstants {

    public static final String ASTWALKER_QUERY_KEY = "query";
    public static final String ASTWALKER_INTERSECT_KEY = "intersect";
    public static final String ASTWALKER_UNION_KEY = "union";
    public static final String ASTWALKER_SUBSTITUTION_KEY = "substitution";
    public static final String ASTWALKER_TYPE_KEY = "type";
    public static final String ASTWALKER_COLUMN_KEY = "column";
    public static final String ASTWALKER_UNKNOWN_KEY = "unknown";
    public static final String ASTWALKER_VALUES_KEY = "values";
    public static final String ASTWALKER_INSERT_KEY = "insert";
    public static final String ASTWALKER_UPDATE_KEY = "update";

    public static final String ASTWALKER_RULE_TYPE_KEY = "Type";
    
    // Maps to lookup constants by name or value
    private static final Map<String, String> NAME_TO_VALUE_MAP = new HashMap<>();
    private static final Map<String, String> VALUE_TO_NAME_MAP = new HashMap<>();
    
    // Static initializer block to populate maps using reflection
    static {
        try {
            for (Field field : ASTWalkerHelperConstants.class.getDeclaredFields()) {
                if (java.lang.reflect.Modifier.isStatic(field.getModifiers()) && 
                    field.getType() == String.class &&
                    field.getName().startsWith("ASTWALKER_")) {
                    
                    String name = field.getName();
                    String value = (String) field.get(null);
                    
                    NAME_TO_VALUE_MAP.put(name, value);
                    VALUE_TO_NAME_MAP.put(value, name);
                }
            }
        } catch (IllegalAccessException e) {
            throw new ExceptionInInitializerError("Failed to initialize ASTWalkerHelperConstants maps: " + e.getMessage());
        }
    }
    
    /**
     * Returns an unmodifiable map of constant names to their values.
     * @return Map where keys are constant names (e.g., "ASTWALKER_QUERY_KEY") and values are their string values (e.g., "query")
     */
    public static Map<String, String> getNameToValueMap() {
        return Collections.unmodifiableMap(NAME_TO_VALUE_MAP);
    }
    
    /**
     * Returns an unmodifiable map of constant values to their names.
     * @return Map where keys are constant values (e.g., "query") and values are their names (e.g., "ASTWALKER_QUERY_KEY")
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
}
