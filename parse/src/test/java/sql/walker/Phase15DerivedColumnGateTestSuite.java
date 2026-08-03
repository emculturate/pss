package sql.walker;

/**
 * Phase 15 derived-column egress quality gate — PIVOT/UNPIVOT regression suite.
 *
 * <p>Run:
 * {@code mvn -Pphase15-derived-gate test}
 *
 * <p>Exercises all {@link SqlEventWalkerPivotUnpivotTests} methods (**136** pivot/unpivot regression tests as of Aug 2026). This is the superset of
 * the 36 tests that failed in Phase 14 E.3 when the batch derived strip
 * ({@code consumeRelationalModifierDerivedColumnUnknownsFromUnresolvedMap}) was removed without
 * per-key derived consume in the unqualified resolver (Phase 15.1).
 *
 * <p>See {@code parse/documents/symbol-table-resolution-consolidation-worklist.md} Phase 15.
 */
public class Phase15DerivedColumnGateTestSuite extends SqlEventWalkerPivotUnpivotTests {
}
