package sql.walker;

import org.junit.Ignore;
import org.junit.Test;

/**
 * Smoketest quality gate — permanent regression suite for SQL parse/walker behavior.
 *
 * Run the full gate:
 * {@code mvn -Psmoketest-quality-gate test}
 *
 * Or:
 * {@code mvn -Dtest=sql.walker.SmoketestQualityGateTestSuite test}
 *
 * Gate composition (234 tests):
 * <ul>
 *   <li>Nested demo queries (2): {@code nestedQueryDemoTest}, {@code nestedQueryDemoWithCteTest}</li>
 *   <li>Query dictionary source routing canaries (3): {@code explicitAliasWhereOutputRefTest}, {@code explicitAliasWherePhysicalRefTest}, {@code implicitOutputWherePhysicalRefTest}</li>
 *   <li>Correlated subquery canaries (38): scalar predicand (18), IN-list (10), EXISTS (10) — includes middle-CTE predicand regression trio (resolve, unqualified fatal location, qualified missing-column fatal location)</li>
 *   <li>Nested WITH / CTE handling (4): {@code nestedWithExistsCarriesCteListAaaThenBbbCccThenDddEee}, {@code nestedWithExistsCarriesCteListAaaBbbThenCccDddEee}, {@code nestedWithExistsCarriesCteListAaaBbbThenCccDddThenEee}, {@code nestedWithInnerJoinAaaBbbThenCccDddEeeParsesWithoutErrors}</li>
 *   <li>Nested WITH alias-boundary visibility (3): {@code nestedVisibilityWithExistsCarriesCteListAaaBbbThenCccDddThenEee}, {@code nestedVisibilityWithInnerJoinAaaBbbThenCccDddThenEeeParsesWithoutErrors}, {@code nestedVisibilityWithScalarWhereAaaThenBbbCccThenDddEeeParsesWithoutErrors}</li>
 *   <li>Nested WITH depth / cross-clause probes (2): {@code nestedNestedWithDepth2CarriesCteListsExistsRefsAndAliasInterfaces}, {@code nestedNestedWithExistsInAndScalarSubqueriesMapToQueryRefs}</li>
 *   <li>Table function smoke (7): FLATTEN/GENERATOR FROM shape, chained lateral, wildcard interface, CTAS, tuple endpoint syntax</li>
 *   <li>DML DELETE canary (1): {@code deleteDictionaryHandlingPostgresReturningQualifiedAcrossWhereSubclausesV2}</li>
 *   <li>SCRIPT / DDL smoke (3): {@code simpleScriptTest}, {@code simpleDdlCreateTableV1Test}, {@code mixedScriptStatementTypesTest}</li>
 *   <li>Endpoint / tuple parser smoke (3): {@code tupleSubstitutionVariableTestV1/V2}, {@code basicTupleTableTest}</li>
 *   <li>Snippet construction (1): {@code basicJoinWithOnOnConditionVariableTest}</li>
 *   <li>Production join_extension / ambiguity probes (1): {@code getMissingColumnFromTupleDictionaryTest}</li>
 *   <li>Phase 13.4 intra–select-list forward alias (5): {@code donorEmailWithInvalidFatalErrorOnQualifiedColumnVariableTest}; {@code selfReferenceColumnAliasInSameSelectListHappyPathV1Test}, {@code selfReferenceColumnAliasReversedOrderUnresolvedV2Test}, {@code selfReferenceColumnAliasPredicandSubstitutionHappyPathV3Test}, {@code selfReferenceColumnAliasPredicandSubstitutionReversedOrderUnresolvedV4Test}</li>
 *   <li>Table-function resolution diagnostic (1): {@code simpleTfCallFlattenSplitV5Test}</li>
 *   <li>PIVOT / UNPIVOT smoke (3): {@code unpivotV1Test}, {@code pivotV1Tab1Test}, {@code pivotInIdentifierResolvedFromSubqueryWarningV1Test}</li>
 *   <li>PIVOT / UNPIVOT multi-modifier gate (17): {@code tripleUnpivotPivotUnpivotJoinDerivedColumnsV1Test}, {@code triplePivotUnpivotPivotJoinDerivedColumnsV1Test}; Phase 17.6.7 subquery-backed triple variants ({@code triplePivotJoinDerivedColumnsAcrossTuplesSubqueryFromV17_6_7Test}, {@code triplePivotJoinDerivedColumnsSameOutputSelectAmbiguousSubqueryFromV17_6_7Test}, {@code tripleUnpivotJoinDerivedColumnsAcrossTuplesSubqueryFromV17_6_7Test}, {@code triplePivotUnpivotPivotJoinDerivedColumnsSubqueryFromV17_6_7Test}, {@code tripleUnpivotPivotUnpivotJoinDerivedColumnsSubqueryFromV17_6_7Test}); Phase 17.7.11 single-modifier tuple-source gate ({@code singlePivotSubqueryFromV17_7_11Test}, {@code singlePivotVariableFromV17_7_11Test}, {@code singlePivotJinjaFromV17_7_11Test}, {@code singlePivotValuesFromV17_7_11Test}, {@code singlePivotTableFunctionFromV17_7_11Test}, {@code singleUnpivotSubqueryFromV17_7_11Test}, {@code singleUnpivotVariableFromV17_7_11Test}, {@code singleUnpivotJinjaFromV17_7_11Test}, {@code singleUnpivotValuesFromV17_7_11Test}, {@code singleUnpivotTableFunctionFromV17_7_11Test})</li>
 *   <li>PIVOT / UNPIVOT subset B clause probes (14): JOIN ON, GROUP/ORDER, HAVING/ORDER, QUALIFY, ORDER BY expression, FromDerived, WithTax, JoinTargets, {@code monthly_sales_long} join/tax-where/join-filter/order-by, pivot IN-list WHERE — see §17.7.7-matrix subset B</li>
 *   <li>Nested WITH clause / set-op matrix (4): scalar HAVING, scalar SELECT-list, UNION, INTERSECT exemplars</li>
 *   <li>Endpoint parser extensions (2): {@code basicTupleSubstitutionVariableTest}, {@code inListVariableSubstitutionTest}</li>
 *   <li>JOIN duplicate-interface fatal (1): {@code handlingRepeatingColumnNamesInTheInterfaceV1}</li>
 *   <li>Access-object / Snippet integration (2): {@code basicQuerySnippetTest}, {@code basicTupleSnippetTest}</li>
 *   <li>DML UPDATE V1–V14 (14): {@code updateDictionaryHandling*} V1–V12 + nested {@code updateFromNestedSubquery*} V13–V14</li>
 *   <li>Query-dictionary external alias routing (8): {@code nestedSubqueryWithColumnsV0}; {@code subqueryDictionaryExtensionWhereClauseV12}, {@code subqueryDictionaryExtensionHavingClauseV13}, {@code subqueryDictionaryExtensionQualifyClauseV14}, {@code subqueryDictionaryExtensionAggregateGroupByV15}, {@code subqueryDictionaryExtensionOrderByV16}, {@code subqueryDictionaryExtensionJoinClauseSubqueryJoinV31}, {@code subqueryDictionaryExtensionWhereClauseSubqueryJoinV32}</li>
 *   <li>Query-dictionary diagnostic routing (6): qualified missing on subquery alias V22/V25/V26 ({@code QUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIAS}); ambiguous unqualified V36/V37/V38 ({@code UNRESOLVED_UNQUALIFIED_COLUMNS} + {@code AMBIGUOUS_COLUMN_REFERENCE})</li>
 *   <li>DML INSERT V1–V8 (8): {@code insertValues*} V1–V8</li>
 *   <li>Postgres INSERT canaries (4): {@code postgresInsertOnConflictDoUpdateTest}, {@code postgresInsertWithCteBodyTest}, {@code postgresInsertDefaultValuesTest}, {@code postgresInsertReturningSelectListInterfaceTest}</li>
 *   <li>DML VALUES source golden examples (7): explicit column names (SELECT, UPDATE, DELETE) + implicit column names (UPDATE V2-V3, DELETE V2-V3) — establish correct QCD structure for all VALUES source patterns</li>
 *   <li>Unaliased derived table V1–V16 (16)</li>
 *   <li>CTE unqualified column refs CTEV1–CTEV15 (15): full {@code SqlEventWalkerSubqueriesAndClauseSemanticsTests} WITH/CTE unqualified-ref matrix</li>
 *   <li>Scalar subquery symbol-table matrix (12): V1–V9 + correlated + filter variants (V1 with correlated WHERE-IN, V2 with correlated WHERE-scalar-comparison) — full clause egress matrix (SELECT predicand, JOIN ON, GROUP BY/HAVING, ORDER BY, QUALIFY, WHERE scalar/EXISTS, correlated)</li>
 *   <li>Production scalar / EXISTS probes (4): {@code selectWhereScalarConditionCorrelatedSubquery}, {@code selectOrderByScalarCorrelatedSubquery}, {@code selectWhereVariableExists}, {@code selectWhereExistsCorrelatedSubquery}</li>
 *   <li>Nested formula subqueries (1): {@code nestedFormulaSubqueriesUseQueryRefsInInterfaceAndFiltersTest}</li>
 *   <li>Subquery semantics probes (6): {@code queryOverQueriesSingleWildcardResolvesUnqualifiedColumn}, {@code selectSameSubqueriesTest}, {@code havingExistsCorrelatedSubqueryTest}, {@code havingScalarSubqueryComparisonTest}, {@code selectWithUnionTest}, {@code multipleScalarAndOtherSubqueriesSymbolTableTest}</li>
 *   <li>Diagnostic exemplars (9): {@code nestedWithDepth2ShadowedParentCteEmitsWarningAndQualifiedAliasFatal}, {@code unionWithMismatchColumnCountsAndNamesTest}, {@code intersectionWithMismatchColumnCountsAndNamesTest}, {@code exceptColumnCountMismatchEmitsFatalTest}, {@code threeLevelSetOpNestUnionIntersectExceptColumnCountMismatchTest}, {@code insertValuesExtraTargetColumnV9}, {@code coverageDrivenSelectIntoUnionBothSidesSnapshotTest}, {@code pivotInIdentifierDirectTableFatalV1Test}</li>
 *   <li>Three-level set-op nesting smoke (2): {@code threeLevelSetOpNestUnionIntersectExceptHappyPathTest}, {@code threeLevelSetOpNestExceptUnionIntersectHappyPathTest}</li>
 *   <li>Parser diagnostic exemplars (11): {@code parserReportErrorUnexpectedInputDiagnosticTest}, {@code parserRecoverInlineInvalidSyntaxNearDiagnosticTest}, {@code parserRecoverMalformedVariableStartDiagnosticTest}, {@code parserRecoverSyntaxErrorDiagnosticTest}, {@code parseErrorCollectorApplicationIssueErrorDiagnosticTest}, {@code parseErrorCollectorApplicationIssueFatalDiagnosticTest}, {@code parseErrorCollectorApplicationIssueWarningDiagnosticTest}, {@code parserAmbiguityDiagnosticTest}, {@code parserFullContextDiagnosticTest}, {@code parserContextSensitivityDiagnosticTest}, {@code parserSyntaxErrorDiagnosticTest}</li>
 * </ul>
 *
 * See {@code parse/documents/symbol-table-resolution-consolidation-worklist.md} for policy and commands.
 */
public class SmoketestQualityGateTestSuite {

	private final SqlEventWalkerCoreSelectFromAliasingTests coreSelectTests =
			new SqlEventWalkerCoreSelectFromAliasingTests();
	private final SqlEventWalkerDmlUpdateInsertDeleteTruncateTests dmlTests =
			new SqlEventWalkerDmlUpdateInsertDeleteTruncateTests();
	private final SqlEventWalkerSubqueriesAndClauseSemanticsTests unaliasedTests =
			new SqlEventWalkerSubqueriesAndClauseSemanticsTests();
	private final SqlEventWalkerTableFunctionTests tableFunctionTests =
			new SqlEventWalkerTableFunctionTests();
	private final SqlEventWalkerScriptsAndDDLTests scriptsAndDdlTests =
			new SqlEventWalkerScriptsAndDDLTests();
	private final SqlEventWalkerNonSqlEndpointParserTests endpointParserTests =
			new SqlEventWalkerNonSqlEndpointParserTests();
	private final SqlEventWalkerLiveSampleQueriesTests liveSampleTests =
			new SqlEventWalkerLiveSampleQueriesTests();
	private final access.SnippetTest snippetTests = new access.SnippetTest();
	private final SqlEventWalkerPivotUnpivotTests pivotUnpivotTests =
			new SqlEventWalkerPivotUnpivotTests();
	private final SqlEventWalkerJoinsAndTableResolutionTests joinsTests =
			new SqlEventWalkerJoinsAndTableResolutionTests();
	private final SqlParseEventWalkerWithAccessObjectTest accessObjectTests =
			new SqlParseEventWalkerWithAccessObjectTest();
	private final SqlParserDiagnosticTests parserDiagnosticTests =
			new SqlParserDiagnosticTests();

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

	// --- Nested WITH / CTE handling (4) ---

	@Test
	public void nestedWithExistsCarriesCteListAaaBbbThenCccDddEee() {
		unaliasedTests.nestedWithExistsCarriesCteListAaaBbbThenCccDddEee();
	}

	@Test
	public void nestedWithExistsCarriesCteListAaaThenBbbCccThenDddEee() {
		unaliasedTests.nestedWithExistsCarriesCteListAaaThenBbbCccThenDddEee();
	}

	@Test
	public void nestedWithExistsCarriesCteListAaaBbbThenCccDddThenEee() {
		unaliasedTests.nestedWithExistsCarriesCteListAaaBbbThenCccDddThenEee();
	}

	@Test
	public void nestedWithInnerJoinAaaBbbThenCccDddEeeParsesWithoutErrors() {
		unaliasedTests.nestedWithInnerJoinAaaBbbThenCccDddEeeParsesWithoutErrors();
	}

	// --- Nested WITH alias-boundary visibility (3) ---

	@Test
	public void nestedVisibilityWithExistsCarriesCteListAaaBbbThenCccDddThenEee() {
		unaliasedTests.nestedVisibilityWithExistsCarriesCteListAaaBbbThenCccDddThenEee();
	}

	@Test
	public void nestedVisibilityWithInnerJoinAaaBbbThenCccDddThenEeeParsesWithoutErrors() {
		unaliasedTests.nestedVisibilityWithInnerJoinAaaBbbThenCccDddThenEeeParsesWithoutErrors();
	}

	@Test
	public void nestedVisibilityWithScalarWhereAaaThenBbbCccThenDddEeeParsesWithoutErrors() {
		unaliasedTests.nestedVisibilityWithScalarWhereAaaThenBbbCccThenDddEeeParsesWithoutErrors();
	}

	// --- Nested WITH depth / cross-clause probes (2) ---

	@Test
	public void nestedNestedWithDepth2CarriesCteListsExistsRefsAndAliasInterfaces() {
		unaliasedTests.nestedNestedWithDepth2CarriesCteListsExistsRefsAndAliasInterfaces();
	}

	@Test
	public void nestedNestedWithExistsInAndScalarSubqueriesMapToQueryRefs() {
		unaliasedTests.nestedNestedWithExistsInAndScalarSubqueriesMapToQueryRefs();
	}

	// --- Table function smoke (7) ---

	@Test
	public void flattenTableFunctionFromListDoesNotUseQueryWrapperTest() {
		tableFunctionTests.flattenTableFunctionFromListDoesNotUseQueryWrapperTest();
	}

	@Test
	public void simpleTfCallFlattenWildcardV1Test() {
		tableFunctionTests.simpleTfCallFlattenWildcardV1Test();
	}

	@Test
	public void tfFromBaseTableLateralFlattenV1Test() {
		tableFunctionTests.tfFromBaseTableLateralFlattenV1Test();
	}

	@Test
	public void chainedLateralTfDoubleNestedV1Test() {
		tableFunctionTests.chainedLateralTfDoubleNestedV1Test();
	}

	@Test
	public void chainedLateralTfWildcardBothV3Test() {
		tableFunctionTests.chainedLateralTfWildcardBothV3Test();
	}

	@Test
	public void explicitJoinFormCtasGeneratorV6Test() {
		tableFunctionTests.explicitJoinFormCtasGeneratorV6Test();
	}

	@Test
	public void flattenTableFunctionTupleEndpointTableSyntaxDoesNotUseQueryWrapperTest() {
		tableFunctionTests.flattenTableFunctionTupleEndpointTableSyntaxDoesNotUseQueryWrapperTest();
	}

	// --- DML DELETE canary (1) ---

	@Test
	public void deleteDictionaryHandlingPostgresReturningQualifiedAcrossWhereSubclausesV2() {
		dmlTests.deleteDictionaryHandlingPostgresReturningQualifiedAcrossWhereSubclausesV2();
	}

	// --- SCRIPT / DDL smoke (3) ---

	@Test
	public void simpleScriptTest() {
		scriptsAndDdlTests.simpleScriptTest();
	}

	@Test
	public void simpleDdlCreateTableV1Test() {
		scriptsAndDdlTests.simpleDdlCreateTableV1Test();
	}

	@Test
	public void mixedScriptStatementTypesTest() {
		scriptsAndDdlTests.mixedScriptStatementTypesTest();
	}

	// --- Endpoint / tuple parser smoke (3) ---

	@Test
	public void tupleSubstitutionVariableTestV1() {
		endpointParserTests.tupleSubstitutionVariableTestV1();
	}

	@Test
	public void tupleSubstitutionVariableTestV2() {
		endpointParserTests.tupleSubstitutionVariableTestV2();
	}

	@Test
	public void basicTupleTableTest() {
		endpointParserTests.basicTupleTableTest();
	}

	@Test
	public void basicTupleSubstitutionVariableTest() {
		endpointParserTests.basicTupleSubstitutionVariableTest();
	}

	@Test
	public void inListVariableSubstitutionTest() {
		endpointParserTests.inListVariableSubstitutionTest();
	}

	// --- Snippet construction (1) ---

	@Test
	public void basicJoinWithOnOnConditionVariableTest() {
		snippetTests.basicJoinWithOnOnConditionVariableTest();
	}

	// --- Production join_extension / ambiguity probes (2) ---

	@Test
	@Ignore("Disabled until we fix the test this calls	")
	public void donorEmailWithInvalidFatalErrorOnQualifiedColumnVariableTest() {
		liveSampleTests.donorEmailWithInvalidFatalErrorOnQualifiedColumnVariableTest();
	}

	@Test
	public void getMissingColumnFromTupleDictionaryTest() {
		liveSampleTests.getMissingColumnFromTupleDictionaryTest();
	}

	// --- Table-function resolution diagnostic (1) ---

	@Test
	public void simpleTfCallFlattenSplitV5Test() {
		tableFunctionTests.simpleTfCallFlattenSplitV5Test();
	}

	// --- PIVOT / UNPIVOT smoke (3) ---

	@Test
	public void unpivotV1Test() {
		pivotUnpivotTests.unpivotV1Test();
	}

	@Test
	public void pivotV1Tab1Test() {
		pivotUnpivotTests.pivotV1Tab1Test();
	}

	@Test
	public void pivotInIdentifierResolvedFromSubqueryWarningV1Test() {
		pivotUnpivotTests.pivotInIdentifierResolvedFromSubqueryWarningV1Test();
	}

	// --- PIVOT / UNPIVOT multi-modifier gate (17) — 17.7 contract + 17.6.7 subquery FROM + 17.7.11 tuple sources ---

	@Test
	public void tripleUnpivotPivotUnpivotJoinDerivedColumnsV1Test() {
		pivotUnpivotTests.tripleUnpivotPivotUnpivotJoinDerivedColumnsV1Test();
	}

	@Test
	public void triplePivotUnpivotPivotJoinDerivedColumnsV1Test() {
		pivotUnpivotTests.triplePivotUnpivotPivotJoinDerivedColumnsV1Test();
	}

	@Test
	public void triplePivotJoinDerivedColumnsAcrossTuplesSubqueryFromV17_6_7Test() {
		pivotUnpivotTests.triplePivotJoinDerivedColumnsAcrossTuplesSubqueryFromV17_6_7Test();
	}

	@Test
	public void triplePivotJoinDerivedColumnsSameOutputSelectAmbiguousSubqueryFromV17_6_7Test() {
		pivotUnpivotTests.triplePivotJoinDerivedColumnsSameOutputSelectAmbiguousSubqueryFromV17_6_7Test();
	}

	@Test
	public void tripleUnpivotJoinDerivedColumnsAcrossTuplesSubqueryFromV17_6_7Test() {
		pivotUnpivotTests.tripleUnpivotJoinDerivedColumnsAcrossTuplesSubqueryFromV17_6_7Test();
	}

	@Test
	public void triplePivotUnpivotPivotJoinDerivedColumnsSubqueryFromV17_6_7Test() {
		pivotUnpivotTests.triplePivotUnpivotPivotJoinDerivedColumnsSubqueryFromV17_6_7Test();
	}

	@Test
	public void tripleUnpivotPivotUnpivotJoinDerivedColumnsSubqueryFromV17_6_7Test() {
		pivotUnpivotTests.tripleUnpivotPivotUnpivotJoinDerivedColumnsSubqueryFromV17_6_7Test();
	}

	@Test
	public void singlePivotSubqueryFromV17_7_11Test() {
		pivotUnpivotTests.singlePivotSubqueryFromV17_7_11Test();
	}

	@Test
	public void singlePivotVariableFromV17_7_11Test() {
		pivotUnpivotTests.singlePivotVariableFromV17_7_11Test();
	}

	@Test
	public void singlePivotJinjaFromV17_7_11Test() {
		pivotUnpivotTests.singlePivotJinjaFromV17_7_11Test();
	}

	@Test
	public void singlePivotValuesFromV17_7_11Test() {
		pivotUnpivotTests.singlePivotValuesFromV17_7_11Test();
	}

	@Test
	public void singlePivotTableFunctionFromV17_7_11Test() {
		pivotUnpivotTests.singlePivotTableFunctionFromV17_7_11Test();
	}

	@Test
	public void singleUnpivotSubqueryFromV17_7_11Test() {
		pivotUnpivotTests.singleUnpivotSubqueryFromV17_7_11Test();
	}

	@Test
	public void singleUnpivotVariableFromV17_7_11Test() {
		pivotUnpivotTests.singleUnpivotVariableFromV17_7_11Test();
	}

	@Test
	public void singleUnpivotJinjaFromV17_7_11Test() {
		pivotUnpivotTests.singleUnpivotJinjaFromV17_7_11Test();
	}

	@Test
	public void singleUnpivotValuesFromV17_7_11Test() {
		pivotUnpivotTests.singleUnpivotValuesFromV17_7_11Test();
	}

	@Test
	public void singleUnpivotTableFunctionFromV17_7_11Test() {
		pivotUnpivotTests.singleUnpivotTableFunctionFromV17_7_11Test();
	}

	// --- PIVOT / UNPIVOT subset B clause probes (14) — §17.7.7-matrix ---

	@Test
	public void unpivotTableJoinOnWithUnqualifiedSalesAmountProbeTest() {
		pivotUnpivotTests.unpivotTableJoinOnWithUnqualifiedSalesAmountProbeTest();
	}

	@Test
	public void unpivotTableWithGroupByAndOrderBySalesAmountV2GroupOrderTest() {
		pivotUnpivotTests.unpivotTableWithGroupByAndOrderBySalesAmountV2GroupOrderTest();
	}

	@Test
	public void unpivotTableWithHavingAndOrderBySalesAmountV2HavingOrderTest() {
		pivotUnpivotTests.unpivotTableWithHavingAndOrderBySalesAmountV2HavingOrderTest();
	}

	@Test
	public void unpivotTableWithQualifySalesAmountProbeTest() {
		pivotUnpivotTests.unpivotTableWithQualifySalesAmountProbeTest();
	}

	@Test
	public void unpivotTableWithOrderByExpressionSalesAmountProbeTest() {
		pivotUnpivotTests.unpivotTableWithOrderByExpressionSalesAmountProbeTest();
	}

	@Test
	public void unpivotFromDerivedAdjustedColumnsV3Test() {
		pivotUnpivotTests.unpivotFromDerivedAdjustedColumnsV3Test();
	}

	@Test
	public void unpivotWithTaxAndWhereV4Test() {
		pivotUnpivotTests.unpivotWithTaxAndWhereV4Test();
	}

	@Test
	public void unpivotJoinTargetsWithFilterV5Test() {
		pivotUnpivotTests.unpivotJoinTargetsWithFilterV5Test();
	}

	@Test
	public void pivotTableWithInAliasesJanFebMarV2Test() {
		pivotUnpivotTests.pivotTableWithInAliasesJanFebMarV2Test();
	}

	@Test
	public void pivotWithTaxAndWhereV4Test() {
		pivotUnpivotTests.pivotWithTaxAndWhereV4Test();
	}

	@Test
	public void pivotMonthlySalesLongJoinOnDerivedSumProbeTest() {
		pivotUnpivotTests.pivotMonthlySalesLongJoinOnDerivedSumProbeTest();
	}

	@Test
	public void pivotMonthlySalesLongJoinFilterDerivedSumTest() {
		pivotUnpivotTests.pivotMonthlySalesLongJoinFilterDerivedSumTest();
	}

	@Test
	public void pivotMonthlySalesLongTaxWhereDerivedSumTest() {
		pivotUnpivotTests.pivotMonthlySalesLongTaxWhereDerivedSumTest();
	}

	@Test
	public void pivotMonthlySalesLongOrderByExpressionDerivedSumProbeTest() {
		pivotUnpivotTests.pivotMonthlySalesLongOrderByExpressionDerivedSumProbeTest();
	}

	// --- Nested WITH clause / set-op matrix (4) ---

	@Test
	public void nestedWithScalarHavingAaaBbbThenCccDddEeeParsesWithoutErrors() {
		unaliasedTests.nestedWithScalarHavingAaaBbbThenCccDddEeeParsesWithoutErrors();
	}

	@Test
	public void nestedWithScalarSelectListAaaBbbThenCccDddEeeParsesWithoutErrors() {
		unaliasedTests.nestedWithScalarSelectListAaaBbbThenCccDddEeeParsesWithoutErrors();
	}

	@Test
	public void nestedWithUnionCarriesCteListAaaBbbThenCccDddEee() {
		unaliasedTests.nestedWithUnionCarriesCteListAaaBbbThenCccDddEee();
	}

	@Test
	public void nestedWithIntersectCarriesCteListAaaBbbThenCccDddEee() {
		unaliasedTests.nestedWithIntersectCarriesCteListAaaBbbThenCccDddEee();
	}

	// --- JOIN duplicate-interface fatal (1) ---

	@Test
	public void handlingRepeatingColumnNamesInTheInterfaceV1() {
		joinsTests.handlingRepeatingColumnNamesInTheInterfaceV1();
	}

	// --- Access-object / Snippet integration (2) ---

	@Test
	public void basicQuerySnippetTest() {
		accessObjectTests.basicQuerySnippetTest();
	}

	@Test
	public void basicTupleSnippetTest() {
		accessObjectTests.basicTupleSnippetTest();
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

	// --- Query-dictionary external alias routing (8) ---

	@Test
	public void nestedSubqueryWithColumnsV0() {
		unaliasedTests.nestedSubqueryWithColumnsV0();
	}

	@Test
	public void subqueryDictionaryExtensionWhereClauseV12() {
		unaliasedTests.subqueryDictionaryExtensionWhereClauseV12();
	}

	@Test
	public void subqueryDictionaryExtensionHavingClauseV13() {
		unaliasedTests.subqueryDictionaryExtensionHavingClauseV13();
	}

	@Test
	public void subqueryDictionaryExtensionQualifyClauseV14() {
		unaliasedTests.subqueryDictionaryExtensionQualifyClauseV14();
	}

	@Test
	public void subqueryDictionaryExtensionAggregateGroupByV15() {
		unaliasedTests.subqueryDictionaryExtensionAggregateGroupByV15();
	}

	@Test
	public void subqueryDictionaryExtensionOrderByV16() {
		unaliasedTests.subqueryDictionaryExtensionOrderByV16();
	}

	@Test
	public void subqueryDictionaryExtensionJoinClauseSubqueryJoinV31() {
		unaliasedTests.subqueryDictionaryExtensionJoinClauseSubqueryJoinV31();
	}

	@Test
	public void subqueryDictionaryExtensionWhereClauseSubqueryJoinV32() {
		unaliasedTests.subqueryDictionaryExtensionWhereClauseSubqueryJoinV32();
	}

	// --- Query-dictionary diagnostic routing (6) ---

	@Test
	public void subqueryDictionaryExtensionWhereClauseMissingQualifiedV22() {
		unaliasedTests.subqueryDictionaryExtensionWhereClauseMissingQualifiedV22();
	}

	@Test
	public void subqueryDictionaryExtensionAggregateGroupByMissingQualifiedV25() {
		unaliasedTests.subqueryDictionaryExtensionAggregateGroupByMissingQualifiedV25();
	}

	@Test
	public void subqueryDictionaryExtensionOrderByMissingQualifiedV26() {
		unaliasedTests.subqueryDictionaryExtensionOrderByMissingQualifiedV26();
	}

	@Test
	public void subqueryDictionaryExtensionQualifyClauseMissingUnqualifiedAmbiguousV36() {
		unaliasedTests.subqueryDictionaryExtensionQualifyClauseMissingUnqualifiedAmbiguousV36();
	}

	@Test
	public void subqueryDictionaryExtensionAggregateGroupByMissingUnqualifiedAmbiguousV37() {
		unaliasedTests.subqueryDictionaryExtensionAggregateGroupByMissingUnqualifiedAmbiguousV37();
	}

	@Test
	public void subqueryDictionaryExtensionOrderByMissingUnqualifiedAmbiguousV38() {
		unaliasedTests.subqueryDictionaryExtensionOrderByMissingUnqualifiedAmbiguousV38();
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

	// --- Postgres INSERT canaries (4) ---

	@Test
	public void postgresInsertOnConflictDoUpdateTest() {
		dmlTests.postgresInsertOnConflictDoUpdateTest();
	}

	@Test
	public void postgresInsertWithCteBodyTest() {
		dmlTests.postgresInsertWithCteBodyTest();
	}

	@Test
	public void postgresInsertDefaultValuesTest() {
		dmlTests.postgresInsertDefaultValuesTest();
	}

	@Test
	public void postgresInsertReturningSelectListInterfaceTest() {
		dmlTests.postgresInsertReturningSelectListInterfaceTest();
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

	// --- Diagnostic exemplars (5) — one test per walker diagnostic not covered elsewhere in gate ---

	@Test
	public void nestedWithDepth2ShadowedParentCteEmitsWarningAndQualifiedAliasFatal() {
		unaliasedTests.nestedWithDepth2ShadowedParentCteEmitsWarningAndQualifiedAliasFatal();
	}

	@Test
	public void unionWithMismatchColumnCountsAndNamesTest() {
		unaliasedTests.unionWithMismatchColumnCountsAndNamesTest();
	}

	@Test
	public void intersectionWithMismatchColumnCountsAndNamesTest() {
		unaliasedTests.intersectionWithMismatchColumnCountsAndNamesTest();
	}

	@Test
	public void exceptColumnCountMismatchEmitsFatalTest() {
		unaliasedTests.exceptColumnCountMismatchEmitsFatalTest();
	}

	@Test
	public void threeLevelSetOpNestUnionIntersectExceptColumnCountMismatchTest() {
		unaliasedTests.threeLevelSetOpNestUnionIntersectExceptColumnCountMismatchTest();
	}

	@Test
	public void threeLevelSetOpNestUnionIntersectExceptHappyPathTest() {
		unaliasedTests.threeLevelSetOpNestUnionIntersectExceptHappyPathTest();
	}

	@Test
	public void threeLevelSetOpNestExceptUnionIntersectHappyPathTest() {
		unaliasedTests.threeLevelSetOpNestExceptUnionIntersectHappyPathTest();
	}

	@Test
	public void insertValuesExtraTargetColumnV9() {
		dmlTests.insertValuesExtraTargetColumnV9();
	}

	@Test
	public void coverageDrivenSelectIntoUnionBothSidesSnapshotTest() {
		accessObjectTests.coverageDrivenSelectIntoUnionBothSidesSnapshotTest();
	}

	@Test
	public void pivotInIdentifierDirectTableFatalV1Test() {
		pivotUnpivotTests.pivotInIdentifierDirectTableFatalV1Test();
	}

	// --- Phase 13.4 intra–select-list forward alias (4) ---

	@Test
	public void selfReferenceColumnAliasInSameSelectListHappyPathV1Test() {
		coreSelectTests.selfReferenceColumnAliasInSameSelectListHappyPathV1Test();
	}

	@Test
	public void selfReferenceColumnAliasReversedOrderUnresolvedV2Test() {
		coreSelectTests.selfReferenceColumnAliasReversedOrderUnresolvedV2Test();
	}

	@Test
	public void selfReferenceColumnAliasPredicandSubstitutionHappyPathV3Test() {
		coreSelectTests.selfReferenceColumnAliasPredicandSubstitutionHappyPathV3Test();
	}

	@Test
	public void selfReferenceColumnAliasPredicandSubstitutionReversedOrderUnresolvedV4Test() {
		coreSelectTests.selfReferenceColumnAliasPredicandSubstitutionReversedOrderUnresolvedV4Test();
	}

	// --- Parser diagnostic exemplars (7) ---

	@Test
	public void parserReportErrorUnexpectedInputDiagnosticTest() {
		parserDiagnosticTests.parserReportErrorUnexpectedInputDiagnosticTest();
	}

	@Test
	public void parserRecoverInlineInvalidSyntaxNearDiagnosticTest() {
		parserDiagnosticTests.parserRecoverInlineInvalidSyntaxNearDiagnosticTest();
	}

	@Test
	public void parserRecoverMalformedVariableStartDiagnosticTest() {
		parserDiagnosticTests.parserRecoverMalformedVariableStartDiagnosticTest();
	}

	@Test
	public void parserRecoverSyntaxErrorDiagnosticTest() {
		parserDiagnosticTests.parserRecoverSyntaxErrorDiagnosticTest();
	}

	@Test
	public void parseErrorCollectorApplicationIssueErrorDiagnosticTest() {
		parserDiagnosticTests.parseErrorCollectorApplicationIssueErrorDiagnosticTest();
	}

	@Test
	public void parseErrorCollectorApplicationIssueFatalDiagnosticTest() {
		parserDiagnosticTests.parseErrorCollectorApplicationIssueFatalDiagnosticTest();
	}

	@Test
	public void parseErrorCollectorApplicationIssueWarningDiagnosticTest() {
		parserDiagnosticTests.parseErrorCollectorApplicationIssueWarningDiagnosticTest();
	}

	@Test
	public void parserAmbiguityDiagnosticTest() {
		parserDiagnosticTests.parserAmbiguityDiagnosticTest();
	}

	@Test
	public void parserFullContextDiagnosticTest() {
		parserDiagnosticTests.parserFullContextDiagnosticTest();
	}

	@Test
	public void parserContextSensitivityDiagnosticTest() {
		parserDiagnosticTests.parserContextSensitivityDiagnosticTest();
	}

	@Test
	public void parserSyntaxErrorDiagnosticTest() {
		parserDiagnosticTests.parserSyntaxErrorDiagnosticTest();
	}
}
