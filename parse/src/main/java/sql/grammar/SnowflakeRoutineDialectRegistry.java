package sql.grammar;

import java.util.Locale;
import java.util.Set;

/**
 * Registry of routine names whose usage should record Snowflake statement-dialect affinity.
 */
public final class SnowflakeRoutineDialectRegistry {

	private static final Set<String> SNOWFLAKE_DIALECT_ROUTINE_NAMES = Set.of(
			"array_construct");

	private SnowflakeRoutineDialectRegistry() {
	}

	public static boolean isSnowflakeDialectRoutine(String functionName) {
		if (functionName == null) {
			return false;
		}
		return SNOWFLAKE_DIALECT_ROUTINE_NAMES.contains(functionName.toLowerCase(Locale.ROOT));
	}
}
