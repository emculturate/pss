package sql.grammar;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

public final class DatetimePartRegistry {

	private static final Map<String, String> CANONICAL_PARTS = buildCanonicalParts();

	private DatetimePartRegistry() {
	}

	// Snowflake's supported add-family parts and their documented abbreviations.
	private static Map<String, String> buildCanonicalParts() {
		Map<String, String> parts = new HashMap<>();
		register(parts, "YEAR", "y", "yy", "yyy", "yyyy", "yr", "years", "yrs");
		register(parts, "QUARTER", "q", "qtr", "qtrs", "quarters");
		register(parts, "MONTH", "mm", "mon", "mons", "months");
		register(parts, "WEEK", "w", "wk", "weekofyear", "woy", "wy");
		register(parts, "DAY", "d", "dd", "days", "dayofmonth");
		register(parts, "HOUR", "h", "hh", "hr", "hours", "hrs");
		register(parts, "MINUTE", "m", "mi", "min", "minutes", "mins");
		register(parts, "SECOND", "s", "sec", "seconds", "secs");
		register(parts, "MILLISECOND", "ms", "msec", "milliseconds");
		register(parts, "MICROSECOND", "us", "usec", "microseconds");
		register(parts, "NANOSECOND", "ns", "nsec", "nanosec", "nsecond",
				"nanoseconds", "nanosecs", "nseconds");
		return Map.copyOf(parts);
	}

	private static void register(Map<String, String> parts, String canonical, String... aliases) {
		parts.put(canonical, canonical);
		for (String alias : aliases) {
			parts.put(alias.toUpperCase(Locale.ROOT), canonical);
		}
	}

	/** Returns all registered uppercase canonical names, sorted and immutable. */
	public static List<String> canonicalValues() {
		return CANONICAL_PARTS.values().stream().distinct().sorted().toList();
	}

	/**
	 * Looks up a registered canonical name (case-insensitive).
	 *
	 * @return an immutable, alphabetically sorted list of lowercase accepted spellings,
	 *         including the canonical spelling, or empty if the canonical name is unknown
	 */
	public static Optional<List<String>> aliasesForCanonical(String canonical) {
		if (canonical == null || canonical.isBlank()) {
			return Optional.empty();
		}
		String key = canonical.toUpperCase(Locale.ROOT);
		if (!key.equals(CANONICAL_PARTS.get(key))) {
			return Optional.empty();
		}
		return Optional.of(CANONICAL_PARTS.entrySet().stream()
				.filter(entry -> key.equals(entry.getValue()))
				.map(entry -> entry.getKey().toLowerCase(Locale.ROOT))
				.sorted()
				.toList());
	}

	/**
	 * Looks up an unquoted part spelling, case-insensitively.
	 *
	 * @return its registered uppercase canonical name, or empty for null, blank,
	 *         or unknown input; no extensible-text fallback is applied
	 */
	public static Optional<String> findCanonical(String alias) {
		if (alias == null || alias.isBlank()) {
			return Optional.empty();
		}
		return Optional.ofNullable(CANONICAL_PARTS.get(alias.toUpperCase(Locale.ROOT)));
	}

	public static String canonicalize(String text) {
		String key = text.toUpperCase(Locale.ROOT);
		return CANONICAL_PARTS.getOrDefault(key, key);
	}
}
