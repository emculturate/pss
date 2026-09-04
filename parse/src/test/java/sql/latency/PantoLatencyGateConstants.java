package sql.latency;

/** Shared thresholds for opt-in Panto latency gates (manual / CI timing jobs). */
public final class PantoLatencyGateConstants {

    /** RMCP kill timeout for {@code timeout_513} corpus rows (Phase 2.8 E3). */
    public static final long E3_TIMEOUT_MS = 90_000L;

    private PantoLatencyGateConstants() {
    }
}
