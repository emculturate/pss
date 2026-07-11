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
 * Gate composition (126 tests):
 * <ul>
 *   <li>Nested demo queries (2): {@code nestedQueryDemoTest}, {@code nestedQueryDemoWithCteTest}</li>
 *   <li>Query dictionary source routing canaries (3): {@code explicitAliasWhereOutputRefTest}, {@code explicitAliasWherePhysicalRefTest}, {@code implicitOutputWherePhysicalRefTest}</li>
 *   <li>Correlated subquery canaries (38): scalar predicand (18), IN-list (10), EXISTS (10) — includes middle-CTE predicand regression trio (resolve, unqualified fatal location, qualified missing-column fatal location)</li>
 *   <li>DML UPDATE V1–V14 (14): {@code updateDictionaryHandling*} V1–V12 + nested {@code updateFromNestedSubquery*} V13–V14</li>
 *   <li>DML INSERT V1–V8 (8): {@code insertValues*} V1–V8</li>
 *   <li>DML VALUES source golden examples (7): explicit column names (SELECT, UPDATE, DELETE) + implicit column names (UPDATE V2-V3, DELETE V2-V3) — establish correct QCD structure for all VALUES source patterns</li>
 *   <li>Unaliased derived table V1–V16 (16)</li>
 *   <li>CTE unqualified column refs CTEV1–CTEV15 (15): full {@code SqlEventWalkerSubqueriesAndClauseSemanticsTests} WITH/CTE unqualified-ref matrix</li>
 *   <li>Scalar subquery symbol-table matrix (12): V1–V9 + correlated + filter variants (V1 with correlated WHERE-IN, V2 with correlated WHERE-scalar-comparison) — full clause egress matrix (SELECT predicand, JOIN ON, GROUP BY/HAVING, ORDER BY, QUALIFY, WHERE scalar/EXISTS, correlated)</li>
 *   <li>Production scalar / EXISTS probes (4): {@code selectWhereScalarConditionCorrelatedSubquery}, {@code selectOrderByScalarCorrelatedSubquery}, {@code selectWhereVariableExists}, {@code selectWhereExistsCorrelatedSubquery}</li>
 *   <li>Nested formula subqueries (1): {@code nestedFormulaSubqueriesUseQueryRefsInInterfaceAndFiltersTest}</li>
 *   <li>Subquery semantics probes (6): {@code queryOverQueriesSingleWildcardResolvesUnqualifiedColumn}, {@code selectSameSubqueriesTest}, {@code havingExistsCorrelatedSubqueryTest}, {@code havingScalarSubqueryComparisonTest}, {@code selectWithUnionTest}, {@code multipleScalarAndOtherSubqueriesSymbolTableTest}</li>
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

	// --- Query dictionary source routing canaries (3) ---

	@Test
	public void explicitAliasWhereOutputRefTest() {
		coreSelectTests.explicitAliasWhereOutputRefTest();
	}

	@Test
	public void explicitAliasWherePhysicalRefTest() {
		coreSelectTests.explicitAliasWherePhysicalRefTest();
	}

	@Test
	public void implicitOutputWherePhysicalRefTest() {
		coreSelectTests.implicitOutputWherePhysicalRefTest();
	}

	// --- Correlated scalar predicand canaries (18) ---

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
	public void simpleWithColumnReferenceTest() {
		coreSelectTests.simpleWithColumnReferenceTest();
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
	public void scalarPredicandFirstCteStandaloneTest() {
		coreSelectTests.scalarPredicandFirstCteStandaloneTest();
	}

	@Test
	public void correlatedScalarPredicandLastCteReferencesPriorCtesTest() {
		coreSelectTests.correlatedScalarPredicandLastCteReferencesPriorCtesTest();
	}

	@Test
	public void correlatedScalarPredicandFinalQueryReferencesCteChainTest() {
		coreSelectTests.correlatedScalarPredicandFinalQueryReferencesCteChainTest();
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

	// --- Correlated IN-subquery canaries (10) ---

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
	public void correlatedInSubqueryMiddleCteReferencesFirstCteTest() {
		coreSelectTests.correlatedInSubqueryMiddleCteReferencesFirstCteTest();
	}

	@Test
	public void correlatedInSubqueryLastCteReferencesPriorCtesTest() {
		coreSelectTests.correlatedInSubqueryLastCteReferencesPriorCtesTest();
	}

	@Test
	public void correlatedInSubqueryFinalQueryReferencesCteChainTest() {
		coreSelectTests.correlatedInSubqueryFinalQueryReferencesCteChainTest();
	}

	@Test
	public void correlatedInSubqueryNestedCteWithOuterRefTest() {
		coreSelectTests.correlatedInSubqueryNestedCteWithOuterRefTest();
	}

	// --- Correlated EXISTS-subquery canaries (10) ---

	@Test
	public void correlatedExistsSubqueryNestedJoinSubqueryTest() {
		coreSelectTests.correlatedExistsSubqueryNestedJoinSubqueryTest();
	}

	@Test
	public void correlatedExistsSubqueryUnionContextTest() {
		coreSelectTests.correlatedExistsSubqueryUnionContextTest();
	}

	@Test
	public void correlatedExistsSubqueryIntersectContextTest() {
		coreSelectTests.correlatedExistsSubqueryIntersectContextTest();
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
	public void correlatedExistsSubqueryMiddleCteReferencesFirstCteTest() {
		coreSelectTests.correlatedExistsSubqueryMiddleCteReferencesFirstCteTest();
	}

	@Test
	public void correlatedExistsSubqueryLastCteReferencesPriorCtesTest() {
		coreSelectTests.correlatedExistsSubqueryLastCteReferencesPriorCtesTest();
	}

	@Test
	public void correlatedExistsSubqueryFinalQueryReferencesCteChainTest() {
		coreSelectTests.correlatedExistsSubqueryFinalQueryReferencesCteChainTest();
	}

	@Test
	public void correlatedExistsSubqueryNestedCteWithOuterRefTest() {
		coreSelectTests.correlatedExistsSubqueryNestedCteWithOuterRefTest();
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

	@Test
	public void insertValuesSourceAliasOnlyV8() {
		dmlTests.insertValuesSourceAliasOnlyV8();
	}

	// --- DML VALUES source golden examples (7) ---

	@Test
	public void selectFromValuesWithExplicitColumnNamesV1() {
		coreSelectTests.selectFromValuesWithExplicitColumnNamesV1();
	}

	@Test
	public void updateFromSelectValuesWithExplicitColumnNamesV1() {
		dmlTests.updateFromSelectValuesWithExplicitColumnNamesV1();
	}

	@Test
	public void updateFromSelectValuesWithImplicitColumnNamesV2() {
		dmlTests.updateFromSelectValuesWithImplicitColumnNamesV2();
	}

	@Test
	public void updateFromSelectValuesWithImplicitColumnNamesV3() {
		dmlTests.updateFromSelectValuesWithImplicitColumnNamesV3();
	}

	@Test
	public void deleteFromSelectValuesWithExplicitColumnNamesV1() {
		dmlTests.deleteFromSelectValuesWithExplicitColumnNamesV1();
	}

	@Test
	public void deleteFromSelectValuesWithImlicitColumnNamesV2() {
		dmlTests.deleteFromSelectValuesWithImlicitColumnNamesV2();
	}

	@Test
	public void deleteFromSelectValuesWithImlicitColumnNamesV3() {
		dmlTests.deleteFromSelectValuesWithImlicitColumnNamesV3();
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

	// --- CTE unqualified column refs CTEV1–CTEV15 (15) ---

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

	@Test
	public void sameTableDifferentSchemaUnqualifiedReferencesCTEV15() {
		unaliasedTests.sameTableDifferentSchemaUnqualifiedReferencesCTEV15();
	}

	// --- Scalar subquery symbol-table matrix (10) ---

	@Test
	public void scalarSubqueriesSymbolTableTestV1() {
		unaliasedTests.scalarSubqueriesSymbolTableTestV1();
	}

	@Test
	public void scalarSubqueriesSymbolTableTestV2() {
		unaliasedTests.scalarSubqueriesSymbolTableTestV2();
	}

	@Test
	public void scalarSubqueriesSymbolTableTestV3() {
		unaliasedTests.scalarSubqueriesSymbolTableTestV3();
	}

	@Test
	public void scalarSubqueriesSymbolTableTestV4() {
		unaliasedTests.scalarSubqueriesSymbolTableTestV4();
	}

	@Test
	public void scalarSubqueriesSymbolTableTestV5() {
		unaliasedTests.scalarSubqueriesSymbolTableTestV5();
	}

	@Test
	public void scalarSubqueriesSymbolTableTestV6() {
		unaliasedTests.scalarSubqueriesSymbolTableTestV6();
	}

	@Test
	public void scalarSubqueriesSymbolTableTestV7() {
		unaliasedTests.scalarSubqueriesSymbolTableTestV7();
	}

	@Test
	public void scalarSubqueriesSymbolTableTestV8() {
		unaliasedTests.scalarSubqueriesSymbolTableTestV8();
	}

	@Test
	public void scalarSubqueriesSymbolTableTestV9() {
		unaliasedTests.scalarSubqueriesSymbolTableTestV9();
	}

	@Test
	public void scalarSubqueriesCorrelatedSubquerySymbolTableTest() {
		unaliasedTests.scalarSubqueriesCorrelatedSubquerySymbolTableTest();
	}

	@Test
	public void scalarSubqueriesSymbolTableFilterV1() {
		unaliasedTests.scalarSubqueriesSymbolTableFilterV1();
	}

	@Test
	public void scalarSubqueriesSymbolTableFilterV2() {
		unaliasedTests.scalarSubqueriesSymbolTableFilterV2();
	}

	// --- Production scalar / EXISTS probes (4) ---

	@Test
	public void selectWhereScalarConditionCorrelatedSubquery() {
		unaliasedTests.selectWhereScalarConditionCorrelatedSubquery();
	}

	@Test
	public void selectOrderByScalarCorrelatedSubquery() {
		unaliasedTests.selectOrderByScalarCorrelatedSubquery();
	}

	@Test
	public void selectWhereVariableExists() {
		unaliasedTests.selectWhereVariableExists();
	}

	@Test
	public void selectWhereExistsCorrelatedSubquery() {
		unaliasedTests.selectWhereExistsCorrelatedSubquery();
	}

	// --- Nested formula subqueries (1) ---

	@Test
	public void nestedFormulaSubqueriesUseQueryRefsInInterfaceAndFiltersTest() {
		unaliasedTests.nestedFormulaSubqueriesUseQueryRefsInInterfaceAndFiltersTest();
	}

	// --- Subquery semantics probes (6) ---

	@Test
	public void queryOverQueriesSingleWildcardResolvesUnqualifiedColumn() {
		unaliasedTests.queryOverQueriesSingleWildcardResolvesUnqualifiedColumn();
	}

	@Test
	public void selectSameSubqueriesTest() {
		unaliasedTests.selectSameSubqueriesTest();
	}

	@Test
	public void havingExistsCorrelatedSubqueryTest() {
		unaliasedTests.havingExistsCorrelatedSubqueryTest();
	}

	@Test
	public void havingScalarSubqueryComparisonTest() {
		unaliasedTests.havingScalarSubqueryComparisonTest();
	}

	@Test
	public void selectWithUnionTest() {
		unaliasedTests.selectWithUnionTest();
	}

	@Test
	public void multipleScalarAndOtherSubqueriesSymbolTableTest() {
		unaliasedTests.multipleScalarAndOtherSubqueriesSymbolTableTest();
	}
}
