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
 * Gate composition (82 tests):
 * <ul>
 *   <li>Nested demo queries (2): {@code nestedQueryDemoTest}, {@code nestedQueryDemoWithCteTest}</li>
 *   <li>Correlated subquery canaries (29): scalar predicand (16), IN-list (8), EXISTS (5) — includes middle-CTE predicand regression trio (resolve, unqualified fatal location, qualified missing-column fatal location)</li>
 *   <li>DML UPDATE V1–V14 (14): {@code updateDictionaryHandling*} V1–V12 + nested {@code updateFromNestedSubquery*} V13–V14</li>
 *   <li>DML INSERT V1–V7 (7): {@code insertValues*} V1–V7</li>
 *   <li>Unaliased derived table V1–V16 (16)</li>
 *   <li>CTE unqualified column refs CTEV1–CTEV14 (14): full {@code SqlEventWalkerSubqueriesAndClauseSemanticsTests} WITH/CTE unqualified-ref matrix</li>
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

	// --- Correlated scalar predicand canaries (13) ---

	@Test
	public void correlatedScalarPredicandNestedJoinSubqueryTest() {
		coreSelectTests.correlatedScalarPredicandNestedJoinSubqueryTest();
	}

	@Test
	public void correlatedScalarPredicandLocalQueryAliasMissingColumnTest() {
		coreSelectTests.correlatedScalarPredicandLocalQueryAliasMissingColumnTest();
	}

	@Test
	public void correlatedScalarPredicandCteFourScenarioResolveAndFatalTest() {
		coreSelectTests.correlatedScalarPredicandCteFourScenarioResolveAndFatalTest();
	}

	@Test
	public void correlatedScalarPredicandNestedCteFourScenarioResolveAndFatalTest() {
		coreSelectTests.correlatedScalarPredicandNestedCteFourScenarioResolveAndFatalTest();
	}

	@Test
	public void correlatedScalarPredicandCteUnionBodyFourScenarioResolveAndFatalTest() {
		coreSelectTests.correlatedScalarPredicandCteUnionBodyFourScenarioResolveAndFatalTest();
	}

	@Test
	public void correlatedScalarPredicandCteGroupByOuterFatalTest() {
		coreSelectTests.correlatedScalarPredicandCteGroupByOuterFatalTest();
	}

	@Test
	public void correlatedScalarPredicandPlainUnionBranchOuterFatalTest() {
		coreSelectTests.correlatedScalarPredicandPlainUnionBranchOuterFatalTest();
	}

	@Test
	public void correlatedScalarPredicandUnionContextSubqueryTest() {
		coreSelectTests.correlatedScalarPredicandUnionContextSubqueryTest();
	}

	@Test
	public void correlatedScalarPredicandIntersectContextSubqueryTest() {
		coreSelectTests.correlatedScalarPredicandIntersectContextSubqueryTest();
	}

	@Test
	public void correlatedScalarPredicandWithNestedInSubqueryTest() {
		coreSelectTests.correlatedScalarPredicandWithNestedInSubqueryTest();
	}

	@Test
	public void correlatedScalarPredicandWithNestedExistsSubqueryTest() {
		coreSelectTests.correlatedScalarPredicandWithNestedExistsSubqueryTest();
	}

	@Test
	public void correlatedScalarPredicandFirstCteStandaloneTest() {
		coreSelectTests.correlatedScalarPredicandFirstCteStandaloneTest();
	}

	@Test
	public void correlatedScalarPredicandNestedCteWithOuterRefTest() {
		coreSelectTests.correlatedScalarPredicandNestedCteWithOuterRefTest();
	}

	@Test
	public void correlatedScalarPredicandMiddleCteReferencesFirstCteTest() {
		coreSelectTests.correlatedScalarPredicandMiddleCteReferencesFirstCteTest();
	}

	@Test
	public void correlatedScalarPredicandMiddleCteUnqualifiedColumnDiagnosticLocationTest() {
		coreSelectTests.correlatedScalarPredicandMiddleCteUnqualifiedColumnDiagnosticLocationTest();
	}

	@Test
	public void correlatedScalarPredicandMiddleCteQualifiedMissingColumnDiagnosticLocationTest() {
		coreSelectTests.correlatedScalarPredicandMiddleCteQualifiedMissingColumnDiagnosticLocationTest();
	}

	// --- Correlated IN-subquery canaries (8) ---

	@Test
	public void correlatedInSubqueryNestedJoinSubqueryTest() {
		coreSelectTests.correlatedInSubqueryNestedJoinSubqueryTest();
	}

	@Test
	public void correlatedInSubqueryUnionContextTest() {
		coreSelectTests.correlatedInSubqueryUnionContextTest();
	}

	@Test
	public void correlatedInSubqueryIntersectContextTest() {
		coreSelectTests.correlatedInSubqueryIntersectContextTest();
	}

	@Test
	public void correlatedInSubqueryWithNestedScalarPredicandTest() {
		coreSelectTests.correlatedInSubqueryWithNestedScalarPredicandTest();
	}

	@Test
	public void correlatedInSubqueryWithNestedExistsTest() {
		coreSelectTests.correlatedInSubqueryWithNestedExistsTest();
	}

	@Test
	public void correlatedInSubqueryFirstCteStandaloneTest() {
		coreSelectTests.correlatedInSubqueryFirstCteStandaloneTest();
	}

	@Test
	public void correlatedInSubqueryFinalQueryReferencesCteChainTest() {
		coreSelectTests.correlatedInSubqueryFinalQueryReferencesCteChainTest();
	}

	@Test
	public void correlatedInSubqueryNestedCteWithOuterRefTest() {
		coreSelectTests.correlatedInSubqueryNestedCteWithOuterRefTest();
	}

	// --- Correlated EXISTS-subquery canaries (5) ---

	@Test
	public void correlatedExistsSubqueryNestedJoinSubqueryTest() {
		coreSelectTests.correlatedExistsSubqueryNestedJoinSubqueryTest();
	}

	@Test
	public void correlatedExistsSubqueryWithNestedScalarPredicandTest() {
		coreSelectTests.correlatedExistsSubqueryWithNestedScalarPredicandTest();
	}

	@Test
	public void correlatedExistsSubqueryWithNestedInSubqueryTest() {
		coreSelectTests.correlatedExistsSubqueryWithNestedInSubqueryTest();
	}

	@Test
	public void correlatedExistsSubqueryFirstCteStandaloneTest() {
		coreSelectTests.correlatedExistsSubqueryFirstCteStandaloneTest();
	}

	@Test
	public void correlatedExistsSubqueryFinalQueryReferencesCteChainTest() {
		coreSelectTests.correlatedExistsSubqueryFinalQueryReferencesCteChainTest();
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

	// --- CTE unqualified column refs CTEV1–CTEV14 (14) ---

	@Test
	public void selectWithMultipleSimpleUnqualifiedReferencesCTEV1() {
		unaliasedTests.selectWithMultipleSimpleUnqualifiedReferencesCTEV1();
	}

	@Test
	public void selectWithMultipleSimpleUnqualifiedReferencesCTEV2() {
		unaliasedTests.selectWithMultipleSimpleUnqualifiedReferencesCTEV2();
	}

	@Test
	public void queryAndUnionUnqualifiedReferencesCTEV3() {
		unaliasedTests.queryAndUnionUnqualifiedReferencesCTEV3();
	}

	@Test
	public void unionAndQueryUnqualifiedReferencesCTEV4() {
		unaliasedTests.unionAndQueryUnqualifiedReferencesCTEV4();
	}

	@Test
	public void queryAndIntersectUnqualifiedReferencesCTEV5() {
		unaliasedTests.queryAndIntersectUnqualifiedReferencesCTEV5();
	}

	@Test
	public void intersectAndQueryUnqualifiedReferencesCTEV6() {
		unaliasedTests.intersectAndQueryUnqualifiedReferencesCTEV6();
	}

	@Test
	public void unionAndIntersectUnqualifiedReferencesCTEV7() {
		unaliasedTests.unionAndIntersectUnqualifiedReferencesCTEV7();
	}

	@Test
	public void intersectAndUnionUnqualifiedReferencesCTEV8() {
		unaliasedTests.intersectAndUnionUnqualifiedReferencesCTEV8();
	}

	@Test
	public void unionAndValuesUnqualifiedReferencesCTEV9() {
		unaliasedTests.unionAndValuesUnqualifiedReferencesCTEV9();
	}

	@Test
	public void valuesAndIntersectUnqualifiedReferencesCTEV10() {
		unaliasedTests.valuesAndIntersectUnqualifiedReferencesCTEV10();
	}

	@Test
	public void valuesAndValuesUnqualifiedReferencesCTEV11() {
		unaliasedTests.valuesAndValuesUnqualifiedReferencesCTEV11();
	}

	@Test
	public void queryAndSubstitutionUnqualifiedReferencesCTEV12() {
		unaliasedTests.queryAndSubstitutionUnqualifiedReferencesCTEV12();
	}

	@Test
	public void substitutionAndQueryUnqualifiedReferencesCTEV13() {
		unaliasedTests.substitutionAndQueryUnqualifiedReferencesCTEV13();
	}

	@Test
	public void substitutionAndSubstitutionUnqualifiedReferencesCTEV14() {
		unaliasedTests.substitutionAndSubstitutionUnqualifiedReferencesCTEV14();
	}
}
