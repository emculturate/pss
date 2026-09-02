package sql.walker;

import org.junit.Test;

/**
 * Phase 2.8 set-op scoping functional gate — fast regression suite for UNION / INTERSECT
 * convert-egress snapshot behavior (S1–S4).
 *
 * <p>Run:
 * {@code mvn -pl parse -Dtest=SqlEventWalkerSetOpScopingGateTests test}
 *
 * <p>Phase 2.8 S3 additions: wildcard UNION+CTE, correlated INTERSECT predicand.
 */
public class SqlEventWalkerSetOpScopingGateTests {

	private final SqlEventWalkerSubqueriesAndClauseSemanticsTests subqueryTests =
			new SqlEventWalkerSubqueriesAndClauseSemanticsTests();
	private final SqlEventWalkerJoinsAndTableResolutionTests joinTests =
			new SqlEventWalkerJoinsAndTableResolutionTests();

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
}
