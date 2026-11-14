package sql.grammar;

import java.util.List;
import java.util.Locale;
import java.util.Set;

public final class SnowflakeDatetimeValueRegistry {

	private static final Set<String> TIME_SLICE_BOUNDARIES = Set.of("START", "END");
	private static final Set<String> WEEKDAY_VALUES = Set.of(
			"MONDAY", "MON",
			"TUESDAY", "TUE", "TUES",
			"WEDNESDAY", "WED",
			"THURSDAY", "THU", "THURS",
			"FRIDAY", "FRI",
			"SATURDAY", "SAT",
			"SUNDAY", "SUN");

	private SnowflakeDatetimeValueRegistry() {
	}

	public static boolean isSupportedTimeSliceBoundary(String value) {
		if (value == null || value.isBlank()) {
			return false;
		}
		return TIME_SLICE_BOUNDARIES.contains(value.toUpperCase(Locale.ROOT));
	}

	public static boolean isSupportedWeekday(String value) {
		if (value == null || value.isBlank()) {
			return false;
		}
		return WEEKDAY_VALUES.contains(value.toUpperCase(Locale.ROOT));
	}

	public static List<String> timeSliceBoundaries() {
		return TIME_SLICE_BOUNDARIES.stream().sorted().toList();
	}

	public static List<String> weekdayValues() {
		return WEEKDAY_VALUES.stream().sorted().toList();
	}
}
