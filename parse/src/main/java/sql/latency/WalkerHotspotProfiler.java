package sql.latency;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.LongAdder;

/**
 * Opt-in walker call counters for Phase 2.8 set-op / convert-egress profiling.
 *
 * <p>Enable with {@code -Dpss.walker.hotspot.profile=true}, run a probe or timing test,
 * then inspect printed counters. Compare two runs (e.g. N=10 vs N=50) and look for keys whose
 * count ratio is closer to N² than to N — those methods are prime optimization targets.
 */
public final class WalkerHotspotProfiler {

	/** System property: {@code pss.walker.hotspot.profile=true} */
	public static final boolean ENABLED =
			Boolean.parseBoolean(System.getProperty("pss.walker.hotspot.profile", "false"));

	private static final ConcurrentHashMap<String, LongAdder> COUNTERS = new ConcurrentHashMap<>();
	private static final List<Long> CONVERT_ELAPSED_MS =
			Collections.synchronizedList(new ArrayList<>());
	private static final List<Integer> CONVERT_GLOBAL_TABLE_DICT_SIZES =
			Collections.synchronizedList(new ArrayList<>());
	private static final List<Integer> CONVERT_LOCAL_TABLE_DICT_SIZES =
			Collections.synchronizedList(new ArrayList<>());
	private static final List<Integer> CONVERT_VISIBLE_QUERY_SOURCE_SIZES =
			Collections.synchronizedList(new ArrayList<>());

	private WalkerHotspotProfiler() {
	}

	public static void hit(String key) {
		if (!ENABLED || key == null) {
			return;
		}
		COUNTERS.computeIfAbsent(key, ignored -> new LongAdder()).increment();
	}

	/** Accumulate a non-unit count (e.g. ancestor levels scanned in one call). */
	public static void add(String key, long delta) {
		if (!ENABLED || key == null || delta <= 0L) {
			return;
		}
		COUNTERS.computeIfAbsent(key, ignored -> new LongAdder()).add(delta);
	}

	public static long convertBegin() {
		return ENABLED ? System.nanoTime() : 0L;
	}

	/**
	 * Records one {@code convertSymbolTableToTableDictionary} invocation duration and scope sizes
	 * at exit (global walker table dictionary, local frame table dictionary, visible query refs).
	 */
	public static void convertEnd(
			long startNanos,
			int globalTableDictSize,
			int localTableDictSize,
			int visibleQuerySourceCount) {
		if (!ENABLED || startNanos == 0L) {
			return;
		}
		long elapsedNanos = System.nanoTime() - startNanos;
		long elapsedMs = elapsedNanos / 1_000_000L;
		long elapsedMicros = elapsedNanos / 1_000L;
		CONVERT_ELAPSED_MS.add(elapsedMs);
		CONVERT_GLOBAL_TABLE_DICT_SIZES.add(globalTableDictSize);
		CONVERT_LOCAL_TABLE_DICT_SIZES.add(localTableDictSize);
		CONVERT_VISIBLE_QUERY_SOURCE_SIZES.add(visibleQuerySourceCount);
		hit("convertTiming_samples");
		add("convertTiming_totalMs", elapsedMs);
		add("convertTiming_totalMicros", elapsedMicros);
		add("convertTiming_globalTableDictSizeSum", globalTableDictSize);
		add("convertTiming_localTableDictSizeSum", localTableDictSize);
		add("convertTiming_visibleQuerySourceSizeSum", visibleQuerySourceCount);
	}

	public static void reset() {
		COUNTERS.clear();
		CONVERT_ELAPSED_MS.clear();
		CONVERT_GLOBAL_TABLE_DICT_SIZES.clear();
		CONVERT_LOCAL_TABLE_DICT_SIZES.clear();
		CONVERT_VISIBLE_QUERY_SOURCE_SIZES.clear();
	}

	public static Map<String, Long> snapshot() {
		List<String> keys = new ArrayList<>(COUNTERS.keySet());
		Collections.sort(keys);
		LinkedHashMap<String, Long> snap = new LinkedHashMap<>();
		for (String key : keys) {
			LongAdder adder = COUNTERS.get(key);
			if (adder != null) {
				snap.put(key, adder.sum());
			}
		}
		return snap;
	}

	public static void report(String label) {
		if (!ENABLED) {
			System.out.println("WALKER_HOTSPOT disabled — pass -Dpss.walker.hotspot.profile=true");
			return;
		}
		System.out.println("=== WALKER_HOTSPOT " + label + " ===");
		for (Map.Entry<String, Long> entry : snapshot().entrySet()) {
			System.out.println("  " + entry.getKey() + "=" + entry.getValue());
		}
		reportConvertBreakdown(label);
	}

	public static void reportConvertBreakdown(String label) {
		if (!ENABLED || CONVERT_ELAPSED_MS.isEmpty()) {
			return;
		}
		List<Long> durations = new ArrayList<>(CONVERT_ELAPSED_MS);
		Collections.sort(durations);
		long totalMs = 0L;
		for (long ms : durations) {
			totalMs += ms;
		}
		long minMs = durations.get(0);
		long maxMs = durations.get(durations.size() - 1);
		long p50Ms = durations.get(durations.size() / 2);
		long p90Ms = durations.get(Math.min(durations.size() - 1, (int) Math.ceil(durations.size() * 0.9) - 1));

		int sampleCount = durations.size();
		int headCount = Math.min(5, sampleCount);
		int tailCount = Math.min(5, sampleCount);
		long headSum = 0L;
		for (int i = 0; i < headCount; i++) {
			headSum += durations.get(i);
		}
		long tailSum = 0L;
		for (int i = sampleCount - tailCount; i < sampleCount; i++) {
			tailSum += durations.get(i);
		}
		double headAvg = headCount == 0 ? 0.0d : headSum * 1.0d / headCount;
		double tailAvg = tailCount == 0 ? 0.0d : tailSum * 1.0d / tailCount;
		double tailOverHead = headAvg == 0.0d ? 0.0d : tailAvg / headAvg;

		int firstGlobalSize = CONVERT_GLOBAL_TABLE_DICT_SIZES.isEmpty()
				? 0
				: CONVERT_GLOBAL_TABLE_DICT_SIZES.get(0);
		int lastGlobalSize = CONVERT_GLOBAL_TABLE_DICT_SIZES.isEmpty()
				? 0
				: CONVERT_GLOBAL_TABLE_DICT_SIZES.get(CONVERT_GLOBAL_TABLE_DICT_SIZES.size() - 1);
		int firstVisibleQuerySources = CONVERT_VISIBLE_QUERY_SOURCE_SIZES.isEmpty()
				? 0
				: CONVERT_VISIBLE_QUERY_SOURCE_SIZES.get(0);
		int lastVisibleQuerySources = CONVERT_VISIBLE_QUERY_SOURCE_SIZES.isEmpty()
				? 0
				: CONVERT_VISIBLE_QUERY_SOURCE_SIZES.get(CONVERT_VISIBLE_QUERY_SOURCE_SIZES.size() - 1);

		System.out.println("=== WALKER_HOTSPOT convert breakdown " + label + " ===");
		System.out.println(
				"  samples=" + sampleCount
						+ " totalMs=" + totalMs
						+ " totalMicros=" + snapshot().getOrDefault("convertTiming_totalMicros", 0L)
						+ " avgMs=" + String.format("%.1f", totalMs * 1.0d / sampleCount)
						+ " minMs=" + minMs
						+ " p50Ms=" + p50Ms
						+ " p90Ms=" + p90Ms
						+ " maxMs=" + maxMs);
		System.out.println(
				"  first5avgMs=" + String.format("%.1f", headAvg)
						+ " last5avgMs=" + String.format("%.1f", tailAvg)
						+ " last5/first5=" + String.format("%.2f", tailOverHead));
		System.out.println(
				"  globalTableDictSize first=" + firstGlobalSize
						+ " last=" + lastGlobalSize
						+ " visibleQuerySources first=" + firstVisibleQuerySources
						+ " last=" + lastVisibleQuerySources);
	}

	/**
	 * Prints per-key scaling ratios between two snapshots (large / small). Keys are sorted by
	 * descending ratio so the worst superlinear offenders float to the top.
	 */
	public static void reportScaling(
			String smallLabel,
			Map<String, Long> smallCounts,
			String largeLabel,
			Map<String, Long> largeCounts) {
		if (!ENABLED) {
			return;
		}
		System.out.println("=== WALKER_HOTSPOT scaling " + smallLabel + " -> " + largeLabel + " ===");
		List<String> keys = new ArrayList<>(largeCounts.keySet());
		keys.addAll(smallCounts.keySet());
		keys = keys.stream().distinct().sorted().toList();

		List<Map.Entry<String, Double>> ratios = new ArrayList<>();
		for (String key : keys) {
			long small = smallCounts.getOrDefault(key, 0L);
			long large = largeCounts.getOrDefault(key, 0L);
			if (small == 0L && large == 0L) {
				continue;
			}
			double ratio = small == 0L ? Double.POSITIVE_INFINITY : (large * 1.0d) / small;
			ratios.add(Map.entry(key, ratio));
		}
		ratios.sort(Comparator.comparingDouble((Map.Entry<String, Double> e) -> e.getValue()).reversed());

		for (Map.Entry<String, Double> entry : ratios) {
			String key = entry.getKey();
			long small = smallCounts.getOrDefault(key, 0L);
			long large = largeCounts.getOrDefault(key, 0L);
			String ratioText = Double.isInfinite(entry.getValue())
					? "inf"
					: String.format("%.2f", entry.getValue());
			System.out.println(
					"  " + key
							+ "  " + smallLabel + "=" + small
							+ "  " + largeLabel + "=" + large
							+ "  ratio=" + ratioText);
		}
	}
}
