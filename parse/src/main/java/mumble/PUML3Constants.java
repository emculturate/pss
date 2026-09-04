package mumble;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Constants used by the PUML3 parser and associated components.
 * These constants define keys used in the Abstract Syntax Tree (AST)
 * generated when parsing PUML3 expressions.
 */
public final class PUML3Constants {
    
    public static final String PUML3_CONDITION_TREE_KEY = "CONDITION";
    public static final String PUML3_EQUATION_TREE_KEY = "EQUATION";

    // Maps to lookup constants by name or value
    private static final Map<String, String> NAME_TO_VALUE_MAP = new HashMap<>();
    private static final Map<String, String> VALUE_TO_NAME_MAP = new HashMap<>();
    
    // Static initializer block to populate maps using reflection
    static {
        try {
            for (Field field : PUML3Constants.class.getDeclaredFields()) {
                if (java.lang.reflect.Modifier.isStatic(field.getModifiers()) && 
                    field.getType() == String.class &&
                    field.getName().startsWith("PUML3_")) {
                    
                    String name = field.getName();
                    String value = (String) field.get(null);
                    
                    NAME_TO_VALUE_MAP.put(name, value);
                    VALUE_TO_NAME_MAP.put(value, name);
                }
            }
        } catch (IllegalAccessException e) {
            throw new ExceptionInInitializerError("Failed to initialize PUML3Constants maps: " + e.getMessage());
        }
    }
    
    /**
     * Returns an unmodifiable map of constant names to their values.
     * @return Map where keys are constant names (e.g., "PUML3_EQUATION_TREE_KEY") and values are their string values (e.g., "EQUATION")
     */
    public static Map<String, String> getNameToValueMap() {
        return Collections.unmodifiableMap(NAME_TO_VALUE_MAP);
    }
    
    /**
     * Returns an unmodifiable map of constant values to their names.
     * @return Map where keys are constant values (e.g., "EQUATION") and values are their names (e.g., "PUML3_EQUATION_TREE_KEY")
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
