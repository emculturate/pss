package sql.grammar;

import java.util.Locale;
import java.util.Set;

/**
 * Bare ANSI / dialect value expressions (no parentheses) that the walker may capture as
 * unqualified column references. These are not physical columns and must be pruned from
 * unresolved-column handling during convert egress.
 */
public final class SqlBareValueExpressionRegistry {

	public enum Affinity {
		/** Recognized on both Snowflake and PostgreSQL; no dialect diagnostic. */
		COMMON,
		/** Snowflake-only bare value; records a Snowflake dialect grammar hit when seen. */
		SNOWFLAKE_ONLY,
		/** PostgreSQL-only bare value; records a PostgreSQL dialect grammar hit when seen. */
		POSTGRES_ONLY
	}

	private static final Set<String> COMMON = Set.of(
			"CURRENT_DATE",
			"CURRENT_TIME",
			"CURRENT_TIMESTAMP",
			"CURRENT_USER",
			"LOCALTIME",
			"LOCALTIMESTAMP");

	private static final Set<String> SNOWFLAKE_ONLY = Set.of(
			"CURRENT_ORGANIZATION_USER");

	private static final Set<String> POSTGRES_ONLY = Set.of(
			"CURRENT_CATALOG",
			"CURRENT_ROLE",
			"CURRENT_SCHEMA",
			"SESSION_USER",
			"USER");

	private SqlBareValueExpressionRegistry() {
	}

	public static Affinity classify(String name) {
		if (name == null || name.isBlank()) {
			return null;
		}
		String normalized = name.toUpperCase(Locale.ROOT);
		if (SNOWFLAKE_ONLY.contains(normalized)) {
			return Affinity.SNOWFLAKE_ONLY;
		}
		if (POSTGRES_ONLY.contains(normalized)) {
			return Affinity.POSTGRES_ONLY;
		}
		if (COMMON.contains(normalized)) {
			return Affinity.COMMON;
		}
		return null;
	}

	public static String dialectConstructLabel(String columnName) {
		if (columnName == null || columnName.isBlank()) {
			return "bare_value";
		}
		return "bare_value:" + columnName.toLowerCase(Locale.ROOT);
	}
}
