package sql.diagnostics;

import java.util.Locale;
import java.util.Set;

/**
 * Static part-name sets aligned with {@code snowflake_extract_field} vs {@code extended_datetime_field}
 * in {@code SQLSelectParser.g4}. Used for optional dialect affinity warnings on {@code extract} nodes.
 */
public final class ExtractDatetimeFieldAffinity {

	private ExtractDatetimeFieldAffinity() {
	}

	/** Snowflake {@code EXTRACT}/{@code DATE_PART} part names (dedicated lexer keywords). */
	private static final Set<String> SNOWFLAKE_ONLY_PARTS = Set.of(
			"DAYOFMONTH",
			"DAYOFWEEK",
			"DAYOFWEEKISO",
			"DAYOFYEAR",
			"WEEKISO",
			"WEEKOFYEAR",
			"EPOCH_SECOND",
			"EPOCH_MILLISECOND",
			"EPOCH_MICROSECOND");

	/**
	 * Postgres-oriented parts from {@code extended_datetime_field} that are not Snowflake keyword parts
	 * and are not shared standard fields ({@code YEAR}, {@code QUARTER}, {@code WEEK}, etc.).
	 */
	private static final Set<String> POSTGRES_EXTENDED_PARTS = Set.of(
			"CENTURY",
			"DECADE",
			"DOW",
			"DOY",
			"EPOCH",
			"ISODOW",
			"ISOYEAR",
			"MICROSECONDS",
			"MILLENNIUM",
			"MILLISECONDS");

	public enum Affinity {
		SNOWFLAKE,
		POSTGRES
	}

	public static Affinity affinityForPart(String part) {
		if (part == null || part.isBlank()) {
			return null;
		}
		String key = part.trim().toUpperCase(Locale.ROOT);
		if (SNOWFLAKE_ONLY_PARTS.contains(key)) {
			return Affinity.SNOWFLAKE;
		}
		if (POSTGRES_EXTENDED_PARTS.contains(key)) {
			return Affinity.POSTGRES;
		}
		return null;
	}
}
