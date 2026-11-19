package sql.grammar;

import java.util.Locale;
import java.util.Set;

/**
 * Registry of routine names whose usage should record Snowflake statement-dialect affinity.
 */
public final class SnowflakeRoutineDialectRegistry {

	private static final Set<String> SNOWFLAKE_DIALECT_ROUTINE_NAMES = Set.of(
			"array_construct",
			"array_construct_compact",
			"array_contains",
			"array_union_agg",
			"array_unique_agg",
			"array_append",
			"array_prepend",
			"array_insert",
			"array_remove",
			"array_remove_at",
			"array_cat",
			"array_except",
			"array_intersection",
			"arrays_overlap",
			"array_to_string",
			"array_slice",
			"array_repeat",
			"array_distinct",
			"array_compact",
			"array_size",
			"array_sort",
			"array_max",
			"array_min",
			"array_reverse",
			"array_sum",
			"to_array",
			"as_array",
			"is_array",
			"split_to_array",
			"strtok_to_array",
			"array_generate_range",
			"array_position",
			"arrays_zip",
			"arrays_to_object");

	private SnowflakeRoutineDialectRegistry() {
	}

	public static boolean isSnowflakeDialectRoutine(String functionName) {
		if (functionName == null) {
			return false;
		}
		return SNOWFLAKE_DIALECT_ROUTINE_NAMES.contains(functionName.toLowerCase(Locale.ROOT));
	}
}
