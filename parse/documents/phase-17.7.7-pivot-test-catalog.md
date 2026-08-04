# Phase 17.7.7 — Pivot/unpivot test method catalog

**Status:** Housekeeping catalog (Aug 2026). Completes **17.7.7** per-method matrix tagging via companion doc (inline `Matrix:` comments on **gapFill17_7_7_*** and subset **A–E** heuristics elsewhere).

**Class:** `SqlEventWalkerPivotUnpivotTests` — **142** `@Test` methods.

See also: `phase-17.7.7-pivot-matrix-heatmap.md`, worklist §17.7.7-matrix.

| Method | Subset | Matrix / notes |
|--------|--------|----------------|
| `unpivotV0Test` | A | subset=A (heuristic) |
| `unpivotV0WhereClauseDerivedQueryDictionaryV17_6_8Test` | B | subset=B (heuristic) |
| `unpivotV0HavingClauseDerivedQueryDictionaryV17_6_8Test` | B | subset=B (heuristic) |
| `unpivotV0QualifyClauseDerivedQueryDictionaryV17_6_8Test` | B | subset=B (heuristic) |
| `unpivotV0WindowDerivedColumnsQueryDictionaryV17_6_8Test` | A | subset=A (heuristic) |
| `unpivotWindowDerivedColumnsQueryDictionaryV17_6NewPolicyV1Test` | B | subset=B (heuristic) |
| `unpivotWindowDerivedColumnsQueryDictionaryV17_6NewPolicyV2Test` | B | subset=B (heuristic) |
| `pivotWindowDerivedColumnsQueryDictionaryV17_6NewPolicyV3Test` | B | subset=B (heuristic) |
| `pivotWindowDerivedColumnsQueryDictionaryV17_6NewPolicyV4Test` | B | subset=B (heuristic) |
| `unpivotV0WindowSourceColumnQueryDictionaryV17_6_8Test` | A | subset=A (heuristic) |
| `insertUnpivotDerivedReturningQueryDictionaryV17_6_8Test` | B | subset=B (heuristic) |
| `updatePivotDerivedReturningQueryDictionaryV17_6_8Test` | B | subset=B (heuristic) |
| `deleteUnpivotDerivedReturningQueryDictionaryV17_6_8Test` | B | subset=B (heuristic) |
| `unpivotV1Test` | A | subset=A (heuristic) |
| `unpivotPostModifierAliasV1Test` | A | subset=A (heuristic) |
| `unpivotTableWithInAliasesJanFebMarV2Test` | B | subset=B (heuristic) |
| `unpivotTableWithInAliasesJanFebMarV2WithTabAliasTest` | B | subset=B (heuristic) |
| `unpivotTableWithGroupByAndOrderBySalesAmountV2GroupOrderTest` | B | subset=B (heuristic) |
| `unpivotTableWithGroupByAndOrderBySalesAmountV2GroupOrderWithTabAliasTest` | B | subset=B (heuristic) |
| `unpivotTableWithHavingAndOrderBySalesAmountV2HavingOrderTest` | B | subset=B (heuristic) |
| `unpivotTableWithHavingAndOrderBySalesAmountV2HavingOrderWithTabAliasTest` | B | subset=B (heuristic) |
| `unpivotTableJoinOnWithUnqualifiedSalesAmountProbeTest` | C | subset=C (heuristic) |
| `unpivotTableJoinOnWithUnqualifiedSalesAmountProbeWithTabAliasTest` | C | subset=C (heuristic) |
| `unpivotTableWithQualifySalesAmountProbeTest` | B | subset=B (heuristic) |
| `unpivotTableWithQualifySalesAmountProbeWithTabAliasTest` | B | subset=B (heuristic) |
| `unpivotTableWithOrderByExpressionSalesAmountProbeTest` | B | subset=B (heuristic) |
| `unpivotTableWithOrderByExpressionSalesAmountProbeWithTabAliasTest` | B | subset=B (heuristic) |
| `unpivotFromDerivedAdjustedColumnsV3Test` | D | subset=D (heuristic) |
| `unpivotWithTaxAndWhereV4Test` | B | subset=B (heuristic) |
| `unpivotJoinTargetsWithFilterV5Test` | B | subset=B (heuristic) |
| `unpivotKeepingOriginalMonthColumnsV6Test` | B | subset=B (heuristic) |
| `unpivotBasicMonthSalesV7Test` | A | subset=A (heuristic) |
| `unpivotBasicMonthSalesV8Test` | A | subset=A (heuristic) |
| `unpivotBasicMonthSalesV9Test` | A | subset=A (heuristic) |
| `pivotInIdentifierDirectTableFatalV1Test` | C | subset=C (heuristic) |
| `pivotInIdentifierMissingFromSubqueryFatalV1Test` | C | subset=C (heuristic) |
| `pivotSourceOperandUnresolvedSubqueryFatalV1Test` | B | subset=B (heuristic) |
| `unpivotSourceOperandUnresolvedSubqueryFatalV1Test` | B | subset=B (heuristic) |
| `pivotSourceOperandUnresolvedValuesFatalV1Test` | B | subset=B (heuristic) |
| `unpivotSourceOperandUnresolvedValuesFatalV1Test` | B | subset=B (heuristic) |
| `pivotInIdentifierResolvedFromSubqueryWarningV1Test` | A | subset=A (heuristic) |
| `pivotV1Tab1Test` | A | subset=A (heuristic) |
| `pivotV1Tab1QuotedSelectorsSuccessTest` | A | subset=A (heuristic) |
| `pivotV1Tab1WithAliasQuotedSelectorsSuccessTest` | A | subset=A (heuristic) |
| `pivotV1QueryTest` | A | subset=A (heuristic) |
| `pivotV1QueryQuotedSelectorsSuccessTest` | A | subset=A (heuristic) |
| `pivotV1QueryInvalidAggregateFormulaReportsParserErrorTest` | A | subset=A (heuristic) |
| `pivotV2Tab1Test` | B | subset=B (heuristic) |
| `pivotV2Tab1QuotedSelectorsSuccessTest` | B | subset=B (heuristic) |
| `pivotV2QueryTest` | B | subset=B (heuristic) |
| `pivotV2QueryQuotedSelectorsSuccessTest` | B | subset=B (heuristic) |
| `pivotV3Tab1Test` | B | subset=B (heuristic) |
| `pivotV3QueryTest` | B | subset=B (heuristic) |
| `pivotV4Tab1Test` | B | subset=B (heuristic) |
| `pivotV4QueryTest` | B | subset=B (heuristic) |
| `pivotBasicMetricColumnsV0WhereClauseDerivedQueryDictionaryV17_6_8Test` | B | subset=B (heuristic) |
| `pivotBasicMetricColumnsV0HavingClauseDerivedQueryDictionaryV17_6_8Test` | B | subset=B (heuristic) |
| `pivotBasicMetricColumnsV0QualifyClauseDerivedQueryDictionaryV17_6_8Test` | B | subset=B (heuristic) |
| `pivotBasicMetricColumnsV0WindowDerivedColumnsQueryDictionaryV17_6_8Test` | A | subset=A (heuristic) |
| `pivotBasicMetricColumnsV0WindowSourceColumnQueryDictionaryV17_6_8Test` | A | subset=A (heuristic) |
| `pivotFromDerivedAdjustedColumnsV3Test` | D | subset=D (heuristic) |
| `pivotSameQuerySelectDerivedColumnFromTableTest` | D | subset=D (heuristic) |
| `pivotSameQueryWhereDerivedColumnFromTableTest` | D | subset=D (heuristic) |
| `pivotSameQueryGroupByDerivedColumnFromTableTest` | D | subset=D (heuristic) |
| `pivotSameQueryHavingDerivedColumnFromTableTest` | D | subset=D (heuristic) |
| `pivotSameQueryQualifyDerivedColumnFromTableTest` | D | subset=D (heuristic) |
| `pivotSameQueryOrderByDerivedColumnFromTableTest` | D | subset=D (heuristic) |
| `pivotSameQueryJoinDerivedColumnFromTableTest` | D | subset=D (heuristic) |
| `pivotSameQueryDerivedColumnsFromSubqueryAcrossClausesTest` | D | subset=D (heuristic) |
| `pivotNestedTableDerivedColumnsResolveInOuterClausesV1Test` | D | subset=D (heuristic) |
| `pivotNestedSubqueryDerivedColumnsResolveInOuterClausesV1Test` | D | subset=D (heuristic) |
| `pivotUpdateFromRhsUnqualifiedDerivedColumnReentryE0gTest` | C | subset=C (heuristic) |
| `pivotQualifiedOperandsJoinOnQualifiedTest` | C | subset=C (heuristic) |
| `pivotQualifiedOperandsWhereWithPivotAliasTest` | C | subset=C (heuristic) |
| `pivotQualifiedOperandsGroupByHavingOrderByTest` | C | subset=C (heuristic) |
| `pivotQualifiedOperandsUpdateSetTest` | C | subset=C (heuristic) |
| `pivotQualifiedOperandsUpdateWhereTest` | C | subset=C (heuristic) |
| `pivotQualifiedOperandsThreeWayJoinTest` | C | subset=C (heuristic) |
| `pivotQualifiedOperandsMultiAggregateTest` | C | subset=C (heuristic) |
| `pivotQualifiedOperandsQualifiedSelectListTest` | C | subset=C (heuristic) |
| `pivotWrongQualifierOperandFatalTest` | C | subset=C (heuristic) |
| `pivotUnqualifiedOuterOutputsAfterJoinAmbiguousTest` | E | subset=E (heuristic) |
| `pivotQualifiedOperandsJoinOnQualifiedUnqualifiedParityTest` | C | subset=C (heuristic) |
| `pivotQualifiedOperandsWhereWithPivotAliasUnqualifiedParityTest` | C | subset=C (heuristic) |
| `pivotQualifiedOperandsGroupByHavingOrderByUnqualifiedParityTest` | C | subset=C (heuristic) |
| `pivotQualifiedOperandsUpdateSetUnqualifiedParityTest` | C | subset=C (heuristic) |
| `pivotQualifiedOperandsUpdateWhereUnqualifiedParityTest` | C | subset=C (heuristic) |
| `pivotQualifiedOperandsThreeWayJoinUnqualifiedParityTest` | C | subset=C (heuristic) |
| `pivotQualifiedOperandsMultiAggregateUnqualifiedParityTest` | C | subset=C (heuristic) |
| `pivotQualifiedOperandsQualifiedSelectListUnqualifiedParityTest` | C | subset=C (heuristic) |
| `pivotWrongQualifierOperandUnqualifiedParityTest` | C | subset=C (heuristic) |
| `pivotUnqualifiedOuterOutputsAfterJoinAmbiguousUnqualifiedOperandsParityTest` | E | subset=E (heuristic) |
| `unpivotQualifiedDerivedOperandsFatalTest` | C | subset=C (heuristic) |
| `unpivotWrongQualifierOperandFatalTest` | C | subset=C (heuristic) |
| `unpivotQualifiedValueDerivedOperandFatalTest` | C | subset=C (heuristic) |
| `unpivotQualifiedInListOperandsRedundantWarningTest` | C | subset=C (heuristic) |
| `unpivotQualifiedOperandsUnqualifiedParityTest` | C | subset=C (heuristic) |
| `pivotCteSourceDerivedColumnClauseSurfacesV1Test` | D | subset=D (heuristic) |
| `unpivotCteSourceDerivedColumnClauseSurfacesV1Test` | D | subset=D (heuristic) |
| `triplePivotJoinDerivedColumnsAcrossTuplesV1Test` | E | subset=E (heuristic) |
| `triplePivotJoinDerivedColumnsSameOutputSelectAmbiguousV17_6_3Test` | E | subset=E (heuristic) |
| `tripleUnpivotJoinDerivedColumnsAcrossTuplesV1Test` | E | subset=E (heuristic) |
| `triplePivotUnpivotPivotJoinDerivedColumnsV1Test` | E | subset=E (heuristic) |
| `tripleUnpivotPivotUnpivotJoinDerivedColumnsV1Test` | E | subset=E (heuristic) |
| `triplePivotJoinDerivedColumnsAcrossTuplesSubqueryFromV17_6_7Test` | E | subset=E (heuristic) |
| `triplePivotJoinDerivedColumnsSameOutputSelectAmbiguousSubqueryFromV17_6_7Test` | E | subset=E (heuristic) |
| `tripleUnpivotJoinDerivedColumnsAcrossTuplesSubqueryFromV17_6_7Test` | E | subset=E (heuristic) |
| `triplePivotUnpivotPivotJoinDerivedColumnsSubqueryFromV17_6_7Test` | E | subset=E (heuristic) |
| `tripleUnpivotPivotUnpivotJoinDerivedColumnsSubqueryFromV17_6_7Test` | E | subset=E (heuristic) |
| `singlePivotSubqueryFromV17_7_11Test` | B | subset=B (heuristic) |
| `singlePivotVariableFromV17_7_11Test` | B | subset=B (heuristic) |
| `singlePivotJinjaFromV17_7_11Test` | B | subset=B (heuristic) |
| `singlePivotValuesFromV17_7_11Test` | B | subset=B (heuristic) |
| `singlePivotTableFunctionFromV17_7_11Test` | B | subset=B (heuristic) |
| `singleUnpivotSubqueryFromV17_7_11Test` | B | subset=B (heuristic) |
| `singleUnpivotVariableFromV17_7_11Test` | B | subset=B (heuristic) |
| `singleUnpivotJinjaFromV17_7_11Test` | B | subset=B (heuristic) |
| `singleUnpivotValuesFromV17_7_11Test` | B | subset=B (heuristic) |
| `singleUnpivotTableFunctionFromV17_7_11Test` | B | subset=B (heuristic) |
| `gapFill17_7_7_S3PivotUnpivotPivotGroupByHavingQualifiedDerivedV1Test` | E | subset=E | topo=S3 (P–U–P) | bucket=GROUP_BY,HAVING,ORDER_BY | kind=derived (qualified) | |
| `gapFill17_7_7_S3UnpivotPivotUnpivotGroupByAmbiguousDerivedFatalV1Test` | E | subset=E | topo=S3 (U–P–U) | bucket=GROUP_BY | kind=derived (unqualified) | outcome=unhappy. |
| `gapFill17_7_7_S2PuPivotUnpivotJoinClauseEgressDerivedV1Test` | E | subset=E | topo=S2-PU | bucket=WHERE,GROUP_BY,HAVING,ORDER_BY | kind=derived (qualified) | |
| `gapFill17_7_7_S3UnpivotPivotUnpivotOrderByAmbiguousDerivedMonthNameFatalV1Test` | E | subset=E | topo=S3 (U–P–U) | bucket=ORDER_BY | kind=derived | outcome=unhappy. |
| `gapFill17_7_7_S3PivotUnpivotPivotHavingAmbiguousDerivedFebSalesSumFatalV1Test` | E | subset=E | topo=S3 (P–U–P) | bucket=HAVING | kind=derived | outcome=unhappy. |
| `gapFill17_7_7_S2PpDualPivotGroupByAmbiguousDerivedJanSalesSumFatalV1Test` | E | subset=E | topo=S2-PP | bucket=GROUP_BY | kind=derived | outcome=unhappy. |
| `gapFill17_7_7_S2PuQualifyDerivedQualifiedHappyV1Test` | E | subset=E | topo=S2-PU | bucket=QUALIFY | kind=derived (qualified) | outcome=happy. |
| `gapFill17_7_7_S2PpDualPivotOrderByAmbiguousSourceMonthNameSevereV1Test` | E | subset=E | topo=S2-PP | bucket=ORDER_BY | kind=source (unqualified) | outcome=unhappy (SEVERE). |
| `gapFill17_7_7_S2PpDualPivotGroupByHavingQualifiedDerivedHappyV1Test` | E | subset=E | topo=S2-PP | bucket=GROUP_BY,HAVING | kind=derived (qualified) | outcome=happy. |
| `gapFill17_7_7_S3PivotUnpivotPivotJoinOnQualifiedDerivedHappyV1Test` | E | subset=E | topo=S3 (P–U–P) | bucket=JOIN ON | kind=derived (qualified) | outcome=happy. |
| `gapFill17_7_7_S3PivotUnpivotPivotOrderByAmbiguousSourceSalesAmountSevereV1Test` | E | subset=E | topo=S3 (P–U–P) | bucket=ORDER_BY | kind=source (unqualified) | outcome=SEVERE. |
| `closeout17_7_8_PivotPhysicalSourceDerivedAbsentFromTableDictionaryTest` | E | subset=E (heuristic) |
| `closeout17_7_8_UnpivotPhysicalSourceDerivedAbsentFromTableDictionaryTest` | E | subset=E (heuristic) |
| `closeout17_7_8_PivotSubquerySourceDerivedAbsentFromPhysicalTableDictionaryTest` | E | subset=E (heuristic) |
| `closeout17_7_8_UnpivotSubquerySourceDerivedAbsentFromPhysicalTableDictionaryTest` | E | subset=E (heuristic) |
| `closeout17_7_8_PivotPhysicalDualModifierDerivedAmbiguousInSelectTest` | E | subset=E (heuristic) |
| `closeout17_7_3_TriplePivotOperandColumnsRemainOnPhysicalTableDictionaryTest` | E | subset=E (heuristic) |
| `closeout17_7_3_TripleUnpivotInListOperandsRemainOnPhysicalTableDictionaryTest` | E | subset=E (heuristic) |
| `pivotDerivedAmbiguousConvertEgressPhaseParityOneVsTwoSelectRefsTest` | B | subset=B (heuristic) |
| `pivotDerivedReferenceSourceAliasOutsideModifierSevereWarningV17_7_10Test` | E | subset=E (heuristic) |
| `unpivotDerivedReferenceSourceAliasOutsideModifierSevereWarningV17_7_10Test` | E | subset=E (heuristic) |
| `pivotDerivedReferenceModifierAliasOutsideModifierNoV17_7_10WarningTest` | E | subset=E (heuristic) |
| `generatorDirectFromListTupleEndpointNakedSyntaxBuildsSameAstShapeTest` | A | subset=A (heuristic) |
