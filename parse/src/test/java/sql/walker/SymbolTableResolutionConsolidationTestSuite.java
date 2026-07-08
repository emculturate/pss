package sql.walker;

import org.junit.Test;

/**
 * Quality gate for symbol-table resolution consolidation (Phases 5–11).
 *
 * Run the full gate:
 * {@code mvn -Psymbol-table-resolution-consolidation test}
 *
 * Or:
 * {@code mvn -Dtest=sql.walker.SymbolTableResolutionConsolidationTestSuite test}
 *
 * Gate composition (39 tests):
 * <ul>
 *   <li>Nested demo queries (2): {@code nestedQueryDemoTest}, {@code nestedQueryDemoWithCteTest}</li>
 *   <li>DML UPDATE V1–V14 (14): {@code updateDictionaryHandling*} V1–V12 + nested {@code updateFromNestedSubquery*} V13–V14</li>
 *   <li>DML INSERT V1–V7 (7): {@code insertValues*} V1–V7</li>
 *   <li>Unaliased derived table V1–V16 (16)</li>
 * </ul>
 *
 * See {@code parse/documents/symbol-table-resolution-consolidation-worklist.md} for policy and commands.
 */
public class SymbolTableResolutionConsolidationTestSuite {

	private final SqlEventWalkerCoreSelectFromAliasingTests coreSelectTests =
			new SqlEventWalkerCoreSelectFromAliasingTests();
	private final SqlEventWalkerDmlUpdateInsertDeleteTruncateTests dmlTests =
			new SqlEventWalkerDmlUpdateInsertDeleteTruncateTests();
	private final SqlEventWalkerSubqueriesAndClauseSemanticsTests unaliasedTests =
			new SqlEventWalkerSubqueriesAndClauseSemanticsTests();

	// --- Nested demo canaries (2) ---

	@Test
	public void nestedQueryDemoTest() {
		coreSelectTests.nestedQueryDemoTest();
	}

	@Test
	public void nestedQueryDemoWithCteTest() {
		coreSelectTests.nestedQueryDemoWithCteTest();
	}

	// --- DML UPDATE V1–V14 (14) ---

	@Test
	public void updateDictionaryHandlingQualifiedColumnsFromWindowedSubqueryV1() {
		dmlTests.updateDictionaryHandlingQualifiedColumnsFromWindowedSubqueryV1();
	}

	@Test
	public void updateDictionaryHandlingQualifiedColumnsAcrossWhereSubclausesV2() {
		dmlTests.updateDictionaryHandlingQualifiedColumnsAcrossWhereSubclausesV2();
	}

	@Test
	public void updateDictionaryHandlingUnqualifiedFallsBackToTargetTableV3() {
		dmlTests.updateDictionaryHandlingUnqualifiedFallsBackToTargetTableV3();
	}

	@Test
	public void updateDictionaryHandlingUnqualifiedWithAdditionalPhysicalTableStillResolvesV4() {
		dmlTests.updateDictionaryHandlingUnqualifiedWithAdditionalPhysicalTableStillResolvesV4();
	}

	@Test
	public void updateDictionaryHandlingGroupByHavingSubqueryAndUnqualifiedRhsV5() {
		dmlTests.updateDictionaryHandlingGroupByHavingSubqueryAndUnqualifiedRhsV5();
	}

	@Test
	public void updateDictionaryHandlingOrderBySubqueryAndUnqualifiedRhsV6() {
		dmlTests.updateDictionaryHandlingOrderBySubqueryAndUnqualifiedRhsV6();
	}

	@Test
	public void updateDictionaryHandlingQualifySubqueryAndUnqualifiedRhsV7() {
		dmlTests.updateDictionaryHandlingQualifySubqueryAndUnqualifiedRhsV7();
	}

	@Test
	public void updateDictionaryHandlingWhereInSubqueryWithTargetTableRefAndOrphanRhsV8() {
		dmlTests.updateDictionaryHandlingWhereInSubqueryWithTargetTableRefAndOrphanRhsV8();
	}

	@Test
	public void updateDictionaryHandlingJoinOnInSubqueryWithTargetTableRefAndOrphanRhsV9() {
		dmlTests.updateDictionaryHandlingJoinOnInSubqueryWithTargetTableRefAndOrphanRhsV9();
	}

	@Test
	public void updateDictionaryHandlingQualifyInSubqueryWithTargetTableRefAndOrphanRhsV10() {
		dmlTests.updateDictionaryHandlingQualifyInSubqueryWithTargetTableRefAndOrphanRhsV10();
	}

	@Test
	public void updateDictionaryHandlingOrderByInSubqueryWithTargetTableRefAndOrphanRhsV11() {
		dmlTests.updateDictionaryHandlingOrderByInSubqueryWithTargetTableRefAndOrphanRhsV11();
	}

	@Test
	public void updateDictionaryHandlingNoQualifiedSubqueryBodyWithQualifiedAssignmentAndOrphanRhsV12() {
		dmlTests.updateDictionaryHandlingNoQualifiedSubqueryBodyWithQualifiedAssignmentAndOrphanRhsV12();
	}

	@Test
	public void updateFromNestedSubqueryDepth2CorrelatedTargetQualifiedColumnV13() {
		dmlTests.updateFromNestedSubqueryDepth2CorrelatedTargetQualifiedColumnV13();
	}

	@Test
	public void updateFromNestedSubqueryDepth3CorrelatedTargetQualifiedColumnV14() {
		dmlTests.updateFromNestedSubqueryDepth3CorrelatedTargetQualifiedColumnV14();
	}

	// --- DML INSERT V1–V7 (7) ---

	@Test
	public void insertValuesPlainMatrixNoTargetColumnsV1() {
		dmlTests.insertValuesPlainMatrixNoTargetColumnsV1();
	}

	@Test
	public void insertValuesExplicitTargetColumnsV2() {
		dmlTests.insertValuesExplicitTargetColumnsV2();
	}

	@Test
	public void insertValuesMultiRowNoTargetColumnsV3() {
		dmlTests.insertValuesMultiRowNoTargetColumnsV3();
	}

	@Test
	public void insertValuesMultiRowExplicitTargetColumnsV4() {
		dmlTests.insertValuesMultiRowExplicitTargetColumnsV4();
	}

	@Test
	public void insertValuesSingleExplicitTargetColumnV5() {
		dmlTests.insertValuesSingleExplicitTargetColumnV5();
	}

	@Test
	public void insertValuesThreeExplicitTargetColumnsV6() {
		dmlTests.insertValuesThreeExplicitTargetColumnsV6();
	}

	@Test
	public void insertValuesSourceNamedColumnsAndAliasV7() {
		dmlTests.insertValuesSourceNamedColumnsAndAliasV7();
	}

	// --- Unaliased derived table V1–V16 (16) ---

	@Test
	public void unaliasedDerivedSimpleAllOuterClausesV1Test() {
		unaliasedTests.unaliasedDerivedSimpleAllOuterClausesV1Test();
	}

	@Test
	public void unaliasedDerivedSimpleFilteredInnerV2Test() {
		unaliasedTests.unaliasedDerivedSimpleFilteredInnerV2Test();
	}

	@Test
	public void unaliasedDerivedSimpleInnerFromTab2V3Test() {
		unaliasedTests.unaliasedDerivedSimpleInnerFromTab2V3Test();
	}

	@Test
	public void unaliasedDerivedSimpleInnerFromTab3V4Test() {
		unaliasedTests.unaliasedDerivedSimpleInnerFromTab3V4Test();
	}

	@Test
	public void unaliasedDerivedSimpleInnerNotNullFilterV5Test() {
		unaliasedTests.unaliasedDerivedSimpleInnerNotNullFilterV5Test();
	}

	@Test
	public void unaliasedDerivedSimpleInnerGroupedV6Test() {
		unaliasedTests.unaliasedDerivedSimpleInnerGroupedV6Test();
	}

	@Test
	public void unaliasedDerivedUnionAllOuterClausesV7Test() {
		unaliasedTests.unaliasedDerivedUnionAllOuterClausesV7Test();
	}

	@Test
	public void unaliasedDerivedUnionAllInnerAllOuterClausesV8Test() {
		unaliasedTests.unaliasedDerivedUnionAllInnerAllOuterClausesV8Test();
	}

	@Test
	public void unaliasedDerivedIntersectAllOuterClausesV9Test() {
		unaliasedTests.unaliasedDerivedIntersectAllOuterClausesV9Test();
	}

	@Test
	public void unaliasedDerivedExceptAllOuterClausesV10Test() {
		unaliasedTests.unaliasedDerivedExceptAllOuterClausesV10Test();
	}

	@Test
	public void unaliasedDerivedUnionIntersectAllOuterClausesV11Test() {
		unaliasedTests.unaliasedDerivedUnionIntersectAllOuterClausesV11Test();
	}

	@Test
	public void unaliasedDerivedUnionMixedColumnNamesV12Test() {
		unaliasedTests.unaliasedDerivedUnionMixedColumnNamesV12Test();
	}

	@Test
	public void unaliasedValuesPositionalAllOuterClausesV13Test() {
		unaliasedTests.unaliasedValuesPositionalAllOuterClausesV13Test();
	}

	@Test
	public void unaliasedValuesAliasOnlyAllOuterClausesV14Test() {
		unaliasedTests.unaliasedValuesAliasOnlyAllOuterClausesV14Test();
	}

	@Test
	public void unaliasedValuesNamedColumnsAllOuterClausesV15Test() {
		unaliasedTests.unaliasedValuesNamedColumnsAllOuterClausesV15Test();
	}

	@Test
	public void unaliasedDerivedFlattenInnerSelectAllOuterClausesV16Test() {
		unaliasedTests.unaliasedDerivedFlattenInnerSelectAllOuterClausesV16Test();
	}
}
