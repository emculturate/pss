package sql.walker;

import org.junit.Test;

/**
 * Phase 2.8 set-op scoping functional gate — fast regression suite for UNION / INTERSECT
 * convert-egress snapshot behavior (S1–S5).
 *
 * <p>Run:
 * {@code mvn -pl parse -Dtest=SqlEventWalkerSetOpScopingGateTests test}
 *
 * <p>Also delegated from {@link SmoketestQualityGateTestSuite} (Phase 2.8 hardening).
 */
public class SqlEventWalkerSetOpScopingGateTests {

	private final SqlEventWalkerSubqueriesAndClauseSemanticsTests subqueryTests =
			new SqlEventWalkerSubqueriesAndClauseSemanticsTests();
	private final SqlEventWalkerJoinsAndTableResolutionTests joinTests =
			new SqlEventWalkerJoinsAndTableResolutionTests();
	private final SqlParseEventWalkerWithAccessObjectTest accessObjectTests =
			new SqlParseEventWalkerWithAccessObjectTest();

	// ── UNION (5) ─────────────────────────────────────────────────────────────

	@Test
	public void gateUnionTimingProbeConvertEgressV0Test() {
		subqueryTests.setOpTimingProbeTenUnionAllJoinersV0Test();
	}

	@Test
	public void gateUnionCteWithJoinV0Test() {
		subqueryTests.selectWithUnionTest();
	}

	@Test
	public void gateUnionCteUnqualifiedRefV0Test() {
		subqueryTests.unionAndQueryUnqualifiedReferencesCTEV4();
	}

	@Test
	public void gateUnionNestedWithCteChainV0Test() {
		subqueryTests.nestedWithUnionCarriesCteListAaaBbbThenCccDddEee();
	}

	@Test
	public void gateUnionLeftJoinSubqueryAliasV0Test() {
		joinTests.withCteLeftJoinUnionThenTrailingJoinSubqueryKeepsUnionAliasV0Test();
	}

	/** Workplan S3 — CTE + wildcard UNION branch interface (Phase 2.1). */
	@Test
	public void gateUnionWildcardBranchAgainstExplicitColumnListV0Test() {
		subqueryTests.unionWildcardBranchAgainstExplicitColumnListTest();
	}

	/** Workplan S3 — correlated scalar subquery in INTERSECT context. */
	@Test
	public void gateIntersectCorrelatedScalarPredicandContextV0Test() {
		new SqlEventWalkerCoreSelectFromAliasingTests()
				.correlatedScalarPredicandIntersectContextSubqueryTest();
	}

	// ── INTERSECT (5) ─────────────────────────────────────────────────────────

	@Test
	public void gateIntersectTimingProbeConvertEgressV0Test() {
		subqueryTests.setOpTimingProbeTenIntersectJoinersV0Test();
	}

	@Test
	public void gateIntersectCteUnqualifiedRefV0Test() {
		subqueryTests.queryAndIntersectUnqualifiedReferencesCTEV5();
	}

	@Test
	public void gateIntersectWithUnionSiblingCteV0Test() {
		subqueryTests.intersectAndUnionUnqualifiedReferencesCTEV8();
	}

	@Test
	public void gateIntersectNestedWithCteChainV0Test() {
		subqueryTests.nestedWithIntersectCarriesCteListAaaBbbThenCccDddEee();
	}

	@Test
	public void gateIntersectNestedUnionInsideIntersectV0Test() {
		subqueryTests.threeLevelSetOpNestUnionIntersectExceptHappyPathTest();
	}

	/** Phase 2.8-S4 — minimal SQL proof of sibling-isolation invariant (see test javadoc). */
	@Test
	public void gateSetOpSiblingIsolationInvariantV0Test() {
		subqueryTests.setOpSiblingIsolationInvariantV0Test();
	}

	// ── S5 exit-time participant merge / validation (4) ───────────────────────

	/** Exit {@code query_expression} — UNION branch interface count mismatch FATAL. */
	@Test
	public void gateSetOpUnionInterfaceMismatchFatalV0Test() {
		subqueryTests.unionWithMismatchColumnCountsAndNamesTest();
	}

	/** Exit {@code query_expression} — INTERSECT branch interface count mismatch FATAL. */
	@Test
	public void gateSetOpIntersectInterfaceMismatchFatalV0Test() {
		subqueryTests.intersectionWithMismatchColumnCountsAndNamesTest();
	}

	/** Exit {@code query_expression} — EXCEPT branch interface count mismatch FATAL. */
	@Test
	public void gateSetOpExceptInterfaceMismatchFatalV0Test() {
		subqueryTests.exceptColumnCountMismatchEmitsFatalTest();
	}

	/** Nested INTERSECT subqueries — multi-participant interface validation at statement exit. */
	@Test
	public void gateSetOpMultipleIntersectInterfaceValidationV0Test() {
		accessObjectTests.multipleIntersectSubqueryInterfaceValidationV1Test();
	}
}
