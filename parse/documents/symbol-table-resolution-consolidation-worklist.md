# Symbol Table Resolution Consolidation — Ultimate Worklist

Use this document as the single handoff for consolidating column resolution in the SQL parse walker and `SqlParseSymbolTreeHelper`. It merges planning from:

- `def-query-canonicalization-phases1-4-checklist.md` (Phases 1–4, **done**)
- Qualified-column egress unification (`nestedQueryDemoTest` canary)
- Clause-list / `convertSymbolTableToTableDictionary` consolidation thread
- INSERT/VALUES and DML parity notes (where they touch shared resolution)

**Last updated:** 2026-07-23 (§ Final actions for rolling out the parser)

---

## Quality gate (run before every consolidation change)

**204 tests** — all passing in the current gate. Implemented in `SmoketestQualityGateTestSuite` and runnable via Maven profile `smoketest-quality-gate`.

**Full module suite (2026-07-22):** `mvn test` → **1399/1399** passing across all walker, access, CLI, and generator test classes.

```bash
cd parse
mvn -Psmoketest-quality-gate test
# equivalent:
mvn -Dtest=sql.walker.SmoketestQualityGateTestSuite test
```

| Group | Count | Class | Methods |
|-------|-------|-------|---------|
| Nested demo queries | 2 | `SqlEventWalkerCoreSelectFromAliasingTests` | `nestedQueryDemoTest`, `nestedQueryDemoWithCteTest` |
| Correlated scalar predicand | 16 | `SqlEventWalkerCoreSelectFromAliasingTests` | … plus middle-CTE trio: `correlatedScalarPredicandMiddleCteReferencesFirstCteTest` (resolve), `correlatedScalarPredicandMiddleCteUnqualifiedColumnDiagnosticLocationTest`, `correlatedScalarPredicandMiddleCteQualifiedMissingColumnDiagnosticLocationTest` |
| Correlated IN subquery | 8 | `SqlEventWalkerCoreSelectFromAliasingTests` | `correlatedInSubqueryNestedJoinSubqueryTest` … `correlatedInSubqueryNestedCteWithOuterRefTest` |
| Correlated EXISTS subquery | 5 | `SqlEventWalkerCoreSelectFromAliasingTests` | `correlatedExistsSubqueryNestedJoinSubqueryTest` … `correlatedExistsSubqueryFinalQueryReferencesCteChainTest` |
| Nested WITH / CTE handling | 4 | `SqlEventWalkerSubqueriesAndClauseSemanticsTests` | `nestedWithExistsCarriesCteListAaaBbbThenCccDddEee`, `nestedWithExistsCarriesCteListAaaThenBbbCccThenDddEee`, `nestedWithExistsCarriesCteListAaaBbbThenCccDddThenEee`, `nestedWithInnerJoinAaaBbbThenCccDddEeeParsesWithoutErrors` |
| Nested WITH alias-boundary visibility | 3 | `SqlEventWalkerSubqueriesAndClauseSemanticsTests` | `nestedVisibilityWithExistsCarriesCteListAaaBbbThenCccDddThenEee`, `nestedVisibilityWithInnerJoinAaaBbbThenCccDddThenEeeParsesWithoutErrors`, `nestedVisibilityWithScalarWhereAaaThenBbbCccThenDddEeeParsesWithoutErrors` |
| Nested WITH depth / cross-clause | 2 | `SqlEventWalkerSubqueriesAndClauseSemanticsTests` | `nestedNestedWithDepth2CarriesCteListsExistsRefsAndAliasInterfaces`, `nestedNestedWithExistsInAndScalarSubqueriesMapToQueryRefs` |
| Table function smoke | 7 | `SqlEventWalkerTableFunctionTests` | FROM-list shape, wildcard interface, chained lateral, CTAS `def_create`, tuple endpoint syntax, etc. |
| DML DELETE canary | 1 | `SqlEventWalkerDmlUpdateInsertDeleteTruncateTests` | `deleteDictionaryHandlingPostgresReturningQualifiedAcrossWhereSubclausesV2` |
| SCRIPT / DDL smoke | 3 | `SqlEventWalkerScriptsAndDDLTests` | `simpleScriptTest`, `simpleDdlCreateTableV1Test`, `mixedScriptStatementTypesTest` (CREATE/TRUNCATE/DELETE/INSERT/UPDATE/SELECT script) |
| Endpoint / tuple parser | 3 | `SqlEventWalkerNonSqlEndpointParserTests` | `tupleSubstitutionVariableTestV1/V2`, `basicTupleTableTest` |
| Snippet construction | 1 | `access.SnippetTest` | `basicJoinWithOnOnConditionVariableTest` |
| Live-sample probes | 1 | `SqlEventWalkerLiveSampleQueriesTests` | `getMissingColumnFromTupleDictionaryTest` |
| Phase 13.4 intra–select-list forward alias | 5 | `SqlEventWalkerLiveSampleQueriesTests`, `SqlEventWalkerCoreSelectFromAliasingTests` | `donorEmailWithInvalidFatalErrorOnQualifiedColumnVariableTest`; `selfReferenceColumnAliasInSameSelectListHappyPathV1Test`, `selfReferenceColumnAliasReversedOrderUnresolvedV2Test`, `selfReferenceColumnAliasPredicandSubstitutionHappyPathV3Test`, `selfReferenceColumnAliasPredicandSubstitutionReversedOrderUnresolvedV4Test` |
| Table-function diagnostic | 1 | `SqlEventWalkerTableFunctionTests` | `simpleTfCallFlattenSplitV5Test` |
| PIVOT / UNPIVOT smoke | 3 | `SqlEventWalkerPivotUnpivotTests` | `unpivotV1Test`, `pivotV1Tab1Test`, `pivotInIdentifierResolvedFromSubqueryWarningV1Test` |
| Nested WITH clause / set-op matrix | 4 | `SqlEventWalkerSubqueriesAndClauseSemanticsTests` | scalar HAVING, scalar SELECT-list, UNION, INTERSECT exemplars |
| Endpoint parser extensions | 2 | `SqlEventWalkerNonSqlEndpointParserTests` | `basicTupleSubstitutionVariableTest`, `inListVariableSubstitutionTest` |
| JOIN duplicate-interface fatal | 1 | `SqlEventWalkerJoinsAndTableResolutionTests` | `handlingRepeatingColumnNamesInTheInterfaceV1` |
| Access-object / Snippet integration | 2 | `SqlParseEventWalkerWithAccessObjectTest` | `basicQuerySnippetTest`, `basicTupleSnippetTest` |
| DML UPDATE V1–V14 | 14 | `SqlEventWalkerDmlUpdateInsertDeleteTruncateTests` | `updateDictionaryHandling*` V1–V12; `updateFromNestedSubqueryDepth2CorrelatedTargetQualifiedColumnV13`; `updateFromNestedSubqueryDepth3CorrelatedTargetQualifiedColumnV14` |
| Query-dictionary external alias routing | 8 | `SqlEventWalkerSubqueriesAndClauseSemanticsTests` | `nestedSubqueryWithColumnsV0`; `subqueryDictionaryExtensionWhereClauseV12`, `HavingClauseV13`, `QualifyClauseV14`, `AggregateGroupByV15`, `OrderByV16`, `JoinClauseSubqueryJoinV31`, `WhereClauseSubqueryJoinV32` |
| Query-dictionary diagnostic routing | 6 | `SqlEventWalkerSubqueriesAndClauseSemanticsTests` | qualified missing V22/V25/V26; ambiguous unqualified V36/V37/V38 |
| DML INSERT V1–V7 | 7 | `SqlEventWalkerDmlUpdateInsertDeleteTruncateTests` | `insertValuesPlainMatrixNoTargetColumnsV1` … `insertValuesSourceNamedColumnsAndAliasV7` |
| Unaliased derived V1–V16 (done) | 16 | `SqlEventWalkerSubqueriesAndClauseSemanticsTests` | `unaliasedDerivedSimpleAllOuterClausesV1Test` … `unaliasedDerivedFlattenInnerSelectAllOuterClausesV16Test` |
| CTE unqualified column refs CTEV1–CTEV15 | 15 | `SqlEventWalkerSubqueriesAndClauseSemanticsTests` | `selectWithMultipleSimpleUnqualifiedReferencesCTEV1`, CTEV2, `queryAndUnionUnqualifiedReferencesCTEV3`, `unionAndQueryUnqualifiedReferencesCTEV4`, `queryAndIntersectUnqualifiedReferencesCTEV5`, `intersectAndQueryUnqualifiedReferencesCTEV6`, `unionAndIntersectUnqualifiedReferencesCTEV7`, `intersectAndUnionUnqualifiedReferencesCTEV8`, `unionAndValuesUnqualifiedReferencesCTEV9`, `valuesAndIntersectUnqualifiedReferencesCTEV10`, `valuesAndValuesUnqualifiedReferencesCTEV11`, `queryAndSubstitutionUnqualifiedReferencesCTEV12`, `substitutionAndQueryUnqualifiedReferencesCTEV13`, `substitutionAndSubstitutionUnqualifiedReferencesCTEV14`, `sameTableDifferentSchemaUnqualifiedReferencesCTEV15` |
| Scalar subquery symbol-table matrix | 10 | `SqlEventWalkerSubqueriesAndClauseSemanticsTests` | `scalarSubqueriesSymbolTableTestV1` (SELECT predicand + WHERE IN), `scalarSubqueriesSymbolTableTestV2` (JOIN ON), `scalarSubqueriesSymbolTableTestV3` (GROUP BY + HAVING scalar), `scalarSubqueriesSymbolTableTestV4` (GROUP BY scalar predicand), `scalarSubqueriesSymbolTableTestV5` (ORDER BY), `scalarSubqueriesSymbolTableTestV6` (QUALIFY), `scalarSubqueriesSymbolTableTestV7` (WHERE scalar), `scalarSubqueriesSymbolTableTestV8` (WHERE EXISTS), `scalarSubqueriesSymbolTableTestV9` (QUALIFY EXISTS), `scalarSubqueriesCorrelatedSubquerySymbolTableTest` |
| Production scalar / EXISTS probes | 4 | `SqlEventWalkerSubqueriesAndClauseSemanticsTests` | `selectWhereScalarConditionCorrelatedSubquery`, `selectOrderByScalarCorrelatedSubquery`, `selectWhereVariableExists`, `selectWhereExistsCorrelatedSubquery` |
| Nested formula subqueries | 1 | `SqlEventWalkerSubqueriesAndClauseSemanticsTests` | `nestedFormulaSubqueriesUseQueryRefsInInterfaceAndFiltersTest` |
| Subquery semantics probes | 6 | `SqlEventWalkerSubqueriesAndClauseSemanticsTests` | `queryOverQueriesSingleWildcardResolvesUnqualifiedColumn`, `selectSameSubqueriesTest`, `havingExistsCorrelatedSubqueryTest`, `havingScalarSubqueryComparisonTest`, `selectWithUnionTest`, `multipleScalarAndOtherSubqueriesSymbolTableTest` |
| Diagnostic exemplars | 5 | mixed | `nestedWithDepth2ShadowedParentCteEmitsWarningAndQualifiedAliasFatal` (`SHADOWED_PARENT_CTE_NAME`), `unionWithMismatchColumnCountsAndNamesTest` (`SET_OPERATION_INTERFACE_COLUMN_COUNT_MISMATCH`), `insertValuesExtraTargetColumnV9` (`INSERT_TARGET_SOURCE_COLUMN_COUNT_MISMATCH`), `coverageDrivenSelectIntoUnionBothSidesSnapshotTest` (`INTO_ONLY_ALLOWED_ON_FIRST_SET_MEMBER`), `pivotInIdentifierDirectTableFatalV1Test` (`PIVOT_IN_IDENTIFIER_UNRESOLVED`) |

**Nested demo fatal expectations (unchanged):**

- `nestedQueryDemoTest` — exactly **3** fatals (`tab2.e3`, `gg.y`, `tt.f`)
- `nestedQueryDemoWithCteTest` — exactly **2** fatals (same minus `gg.y`; CTE resolves `gg.y`)

**Gate status (Jul 2026):** **200/200 passing** — no current failures (13.1 EXCEPT set-op canaries added).

**Full suite status (2026-07-20):** **1209/1209 passing** — includes all substitution-variable families (Column V1–V16, INSERT I1–I10, UPDATE U1–U10), DML dictionary tests, PIVOT/UNPIVOT (**67**), and live-sample probes.

The prior phase-7, phase-10, and DML golden-backlog notes below are **historical** only.

---

## Current gate failures

None. The consolidation gate is green at **195/195**. The full parse module suite is green at **1203/1203**.

Previously documented mismatches (phase-7 golden drift, phase-10 substitution blockers, ~82/95 DML stale goldens) have been refreshed and are green — treat older failure tables as historical context only.

---

## Phase 7 golden backlog (historical)

Tests from the earlier Phase 7 inventory that were refreshed during the green gate run. Keep this section for traceability; it no longer represents active failures.

### CTE unqualified refs CTEV1–CTEV15 (`SqlEventWalkerSubqueriesAndClauseSemanticsTests`) — **done**

Goldens updated for the full WITH/CTE unqualified-ref matrix (CTEV1–CTEV15). All 15 tests are in the quality gate. Prior failures (CTEV4/6 table-dictionary external alias tokens; CTEV9–11 query-dict set-op keys; CTEV13/14 substitution shapes) resolved via CTE alias `context_list` fixes and golden alignment. CTEV15 adds same-table/different-schema cross-CTE unqualified refs.

### Scalar subquery symbol-table matrix (`SqlEventWalkerSubqueriesAndClauseSemanticsTests`) — **done**

Goldens aligned for full clause-egress scalar subquery coverage: V1 (SELECT predicand + WHERE IN), V2 (JOIN ON), V3 (GROUP BY + HAVING scalar), V4 (GROUP BY scalar predicand), V5 (ORDER BY), V6 (QUALIFY), V7 (WHERE scalar), V8 (WHERE EXISTS), V9 (QUALIFY EXISTS), plus `scalarSubqueriesCorrelatedSubquerySymbolTableTest`. All 10 are in the quality gate.

### Subquery semantics probes (`SqlEventWalkerSubqueriesAndClauseSemanticsTests`) — **done**

`queryOverQueriesSingleWildcardResolvesUnqualifiedColumn` (nested derived-table wildcard resolution), `selectSameSubqueriesTest` (repeated subquery reuse), `havingExistsCorrelatedSubqueryTest`, `havingScalarSubqueryComparisonTest`, `selectWithUnionTest` (top-level UNION), and `multipleScalarAndOtherSubqueriesSymbolTableTest` (mixed scalar + derived + IN) are green and in the quality gate.

### Production scalar / EXISTS / nested-formula probes (`SqlEventWalkerSubqueriesAndClauseSemanticsTests`) — **done**

`selectWhereScalarConditionCorrelatedSubquery`, `selectOrderByScalarCorrelatedSubquery`, `selectWhereVariableExists`, `selectWhereExistsCorrelatedSubquery`, and `nestedFormulaSubqueriesUseQueryRefsInInterfaceAndFiltersTest` are green and in the quality gate. These cover correlated WHERE/ORDER BY scalar predicands, substitution EXISTS, correlated EXISTS, and nested-formula `{query=queryN}` refs in interface/filters (see **Published scope vs global dictionary rules** — Example V1).

### UPDATE CTE substitution spot checks (`SqlEventWalkerDmlUpdateInsertDeleteTruncateTests`) — 5 failing

| Test | Assertion | What's wrong |
|------|-----------|--------------|
| `updateComplexSubstitutionU3WithCteIntersectOrderBySubstitution` | Interface | Expected `[score]`; actual `[]` |
| `updateComplexSubstitutionU4NestedWithInCteBody` | Diagnostics | 2 unexpected fatals: `o.emp_id`, `o.metric_val` not in alias `o` interface |
| `updateComplexSubstitutionU5WithCteQualifyWindowSubstitution` | Interface | Expected `[score]`; actual `[rn, score_val, emp_id]` |
| `updateComplexSubstitutionU7ChainedCteReferences` | Interface | Expected `[score]`; actual `[raw_val, emp_id]` |
| `updateComplexSubstitutionU9WithCteSelfUnionBranches` | Interface | Expected `[score]`; actual `[]` |

### Correlated IN/EXISTS middle-CTE chain (`SqlEventWalkerCoreSelectFromAliasingTests`) — ✅ DONE (Jul 2026)

All 7 tests now passing. Single broken test fixed; remaining 6 confirmed passing.

| Test | Status |
|------|--------|
| `correlatedInSubqueryMiddleCteReferencesFirstCteTest` | ✅ |
| `correlatedInSubqueryLastCteReferencesPriorCtesTest` | ✅ |
| `correlatedExistsSubqueryMiddleCteReferencesFirstCteTest` | ✅ |
| `correlatedExistsSubqueryLastCteReferencesPriorCtesTest` | ✅ |
| `correlatedExistsSubqueryUnionContextTest` | ✅ |
| `correlatedExistsSubqueryIntersectContextTest` | ✅ |
| `correlatedExistsSubqueryNestedCteWithOuterRefTest` | ✅ |

### CTE substitution column-variable (`SqlEventWalkerCoreSelectFromAliasingTests`) — 18 failing

External CTE tokens moved from physical `table_dictionary` to `query_dictionary`; symbol tree uses `def_queryN` wrapper.

| Test | Assertion |
|------|-----------|
| `getSubstitutionColumnVariableV1Test` … `V8` | Query Column Dictionary empty or missing `<select column>` |
| `getSubstitutionColumnVariableV6SecondJoinOnQualifiedColumnReferencesTest` | Table Dictionary missing `cec2` join-alias tokens |
| `getSubstitutionColumnVariableV9CteWrappedWhereVariantWithJoinOnSelectColumnTest` … `V16` | Symbol Tree — CTE external `cec` tokens, `def_query1` nesting |
| `variation2ColumnVariableTest`, `variation3ColumnVariableTest` | Symbol Tree — CTE query-dict-only + `def_queryN` shape |

### Basic SELECT / symbol-tree shape — 15 failing

Empty global `queryColumnDictionaryMap` and/or bare `query0` vs `def_query0` wrapper: `basicSelectList1Test`, `basicSelectTableNameV1/V2/V3Test`, `basicSelectQuotedTableNameV1Test`, `basicSelectList2/3Test`, `basicSelectListAliasing1Test`, `basicSelectListNumericPrefixAliasingTest`, `basicSelectDistinctQualifierListTest`, `concatenationFormulaTest`, `informaticaINFunctionStatementTest`, `ascAsColumnTest`, `descAsColumnTest`, `rankAsColumnTest`.

### Substitution / extended variable names — 13 failing

Empty global query dict or alias token where column token expected: `simpleVariableName1Test`, `simpleVariableNameWithDotTest`, `simpleVariableNameWithDashTest`, `getSimpleColumnVariableTest`, `extendedVariableName1Test`, `extendedVariableNameWithDashTest`, `extendedVariableNameWithDots2Test`, `extendedVariableNamePopulationSubnamerTest`, `extendedVariableNamePopulationQualifierTest`, `entityVariableNamePopulationSubnameTest`, `entityVariableNamePopulationQualifierTest`, `informaticaINFunctionConditionStatement1/2Test`.

### Real-world / DISTINCT / Jinja — 8 failing

`basicSelectDistinctListWithEmbeddedAllListQualifierTest`, `basicSelectListQuotedNumericPrefixColumnTest` (key order), `real1`–`real4SelectListNumericPrefixAliasingTest`, `jinjaTupleSingleSourceUnqualifiedContactKeyTest`, `jinjaTupleWithAliasTest`.

**Total backlog: 66 failing tests.** *(Historical — all refreshed; full suite 1203/1203 green as of 2026-07-19.)*

---

## Progress dashboard (Jul 2026)

| Phase | Status | % | Gate / notes |
|-------|--------|---|--------------|
| **1–4** def_query canonicalization | ✅ Done | 100% | Commit `b59688c` |
| **5** def_query read-path gaps | ✅ Done | 100% | Strict `def_*` lookup; V1–V16 green; recursive fallback deleted (Jul 2026) |
| **6** one `convertSymbolTableToTableDictionary` | ✅ Done | 100% | Audit Jul 2026: single helper impl; `reconcileJoinExtensionSymbolTable` for mid-FROM; dead `explicitTableRefByColumn` removed |
| **7** uniform query scope finalization | ✅ Done | 100% | Current gate is green (130/130); prior phase-7 backlog fully refreshed |
| **8** unified egress helper | ✅ Done | 100% | Late-pass helpers retired/consolidated; global qualified ingress now uses `resolveQualifiedUnresolvedEntries`; backfill folded into interface loop + final sweep |
| **9** clause-list validation (no parallel pipelines) | ✅ Done | 100% | Gate + full suite green; `mergeSelectList…` post-hoc hook retired (Jul 2026, commit `e60d8f8`) |
| **10** Substitution Variable Quality Gate Inventory | ✅ Done | 100% | All families green — Column V1–V16, INSERT I1–I10, UPDATE U1–U10 verified 2026-07-19 |
| **11** downward `context_list` resolution | ✅ Done | 100% | Canary set + full suite green; closeout checklist signed off below |
| **12** DML parity + fallback retirement | ✅ Done | 100% | All 103 DML class tests + 30 complex-substitution tests green |
| **14** Universal per-column resolution (Steps C–F) | ✅ Done | 100% | Steps **C–F** complete (Jul 2026); Phase **15** is NEXT |
| **15** Unified convert egress loop | ⏸️ Not started | 0% | **NEXT** — derived registry keys in `unresolved_column`; see Phase 15 |
| **16** PIVOT operand materialization | ⏸️ Not started | 0% | After Phase 15 — physical operand cols; see Phase 16 |
| **17** UNPIVOT synthetic columns | ⏸️ Not started | 0% | After Phase 15 — walk + convert UNPIVOT paths; see Phase 17 |
| **18** PIVOT IN-list output + IN-identifier | ⏸️ Not started | 0% | After Phase 15 — Snowflake-style aliases + identifier refs; see Phase 18 |
| **19** Query dictionary publish consolidation | ⏸️ Not started | 0% | After Phase 15.6 — single publish ingress; retire write-path spread; see Phase 19 |
| **13** Language feature gap closure | ⏸️ Not started | 0% | **Unblocked** — can run in parallel with Phases 15–19; see Phase 13 section |

**Recent wins (Jul 2026):**

- Phase 8 late-pass retirement: deleted `materializeResolvableGlobalQualifiedUnresolvedLocations`; statement-top global qualified ingress now delegates to unified `resolveQualifiedUnresolvedEntries`
- Phase 8 backfill consolidation (historical): per-column backfill at interface `RESOLVED_PHYSICAL_SOURCE` + final sweep — **deleted** in `876c7ce`; native capture replaced both paths
- Derived-column stripping consolidated from 3 passes to 2 (pre-wildcard + post-UPDATE-rhs); post-late-resolution strip retired — unified resolver + interface loop handle derived proof
- Commit `2833a2f`: PIVOT/UNPIVOT derived columns proof folded into unified qualified resolver (`RESOLVED_DERIVED_COLUMN`); retired `localCurrentQueryDictionary.containsKey(columnName)` diagnostic shortcut
- Commit `2833a2f`: `isPhysicalTableRefVisibleInScope` — physical materialization gated on visible scope only (no global-dict visibility leak); `nestedQueryDemoTest` goldens updated
- Phase 8 canaries green: `nestedQueryDemoTest` (3 fatals), `nestedQueryDemoWithCteTest`, V9, V13
- Commit `e60d8f8`: retired `mergeSelectListQualifiedQueryAliasRefsIntoSourceQueryDictionary` + `aliasMapsToQuerySource` (native interface loop + `materializeResolvedQualifiedQuerySourceReference` cover V13/V14); gate +14 query-dict routing/diagnostic tests (195 total)
- Step A (Jul 2026, `36f7aa0`): removed dead `rehomeUpdateUnqualifiedUnknownsToSingleFromTable` + `getSingleUpdateFromTableReference` (zero callers)
- V9 UPDATE FROM join-on orphan RHS — goldens aligned
- Query column dictionary alias tokens (`'a'`, `'e'`, `'inner_sq'`) accepted as canonical (not `<327>` substitution spellings)
- Commit `1274ee3`: Phase 14 **C1b** — interface loop query-source fallback; removed early `moveUnknownEntriesToSingleWildcardBackedNonTableSource` call
- Commit `37da020`: Phase 14 **C1a** — removed both `relocateUnqualifiedToSingleTableExcludingOutputAliases` calls + prepend relocation; Option E PIVOT operand refresh
- Commit `b445286`: skip unqualified physical resolution for PIVOT-derived interface outputs (spurious join-table binding fix)
- Commit `cd937c2`: Phase 14 **C2a** — retired `materializeRemainingSingleTableUnqualifiedAtScopeExit`; convert-time `materializeUnqualifiedLineageForSingleSourceScopeAtConvertExit` + `consumeLocallyResolvedUnqualifiedBeforeScopePassUp` on pass-up egress; authorization golden refresh
- Commit `1d20503`: Phase 14 **C2b + C2 closeout** — removed finalize wildcard suppressor; deleted `canResolveUnqualifiedFromSingleWildcardQuerySource`, `moveUnknownEntriesToSingleWildcardBackedNonTableSource`, `moveEntriesToSingleTableIfSingleTarget`
- Step E prep (Jul 2026, `ed1ebfd`): **E.0 inventory** complete; E0g UPDATE RHS canary + 4 `monthly_sales_long` derived-column clause tests; 11 IN-list alias tests annotated; PIVOT derived naming verified (`{inValue}_{aggregate}`)
- Step E.2 + E.3 (Jul 2026): retired **two** `resolveRelationalModifierDerivedColumnsFromUnresolvedMap` call sites (pre-wildcard + post-UPDATE-RHS); consolidated to **one** pre-diagnostics egress drain before `emitExplicitQualifiedUnknownDiagnostics`; pivot **67/67**, gate **195/195**, full suite **1209/1209**
- Step E.4 (Jul 2026): deleted public `resolveRelationalModifierDerivedColumnsFromUnresolvedMap`; privatized as `consumeRelationalModifierDerivedColumnUnknownsFromUnresolvedMap`
- Phase 14 **Step F** (Jul 2026): removed unused table-function field getters; `ArchivedClauseColumnRefResult.satisfied()` already absent
- Phase **5** closeout (Jul 2026): strict `def_*` payload lookup audit; renamed ancestor walk to **`resolveDefinitionSymbolInScopeChain`** (definition scope chain resolution); deleted dead `findInCurrentOrAncestorSymbolTablesRecursive` + `findInScopeTreeByKeyRecursive`
- Phases **15–19** roadmap added: unified egress loop + operand / UNPIVOT / IN-list namespaces + egress scope bundle + query-dict publish consolidation

**Active blockers:** None. All consolidation-phase tests are green.

**Suggested next focus:** **Phase 15.1** (unqualified resolver derived-awareness). Phases **16–18** follow Phase 15 closeout (operand → UNPIVOT → IN-list/identifier). **15.6** then **Phase 19** consolidate egress reads and publish writes. **Phase 14 Step F** and **Phase 13** can run in parallel.

---

## Phase 10 — Substitution Variable Quality Gate Inventory (✅ DONE — Jul 2026)

**Objective:** Establish and lock a comprehensive quality gate for all substitution variable types (Column, Predicand, Condition, Tuple, In_List, Join_Extension) across the project.

**Verification (2026-07-19):**

| Run | Result |
|-----|--------|
| `getSubstitutionColumnVariableV*` (V1–V16) | **16/16** pass |
| `*ComplexSubstitution*` (INSERT I1–I10 + UPDATE U1–U10) | **30/30** pass |
| Full `mvn test` | **1203/1203** pass |

All substitution-variable families are green. The blocker inventory below is **historical**.

### Substitution Variable Categories & Test Inventory

#### 1. **Column Substitution Variables** (type=column)
- **Definition:** Variables representing columns from physical/derived tables (e.g., `<my_column>`).
- **Test count:** ~40+ tests
- **Status:** ✅ **DONE** — all V1–V16 and I/U complex substitution tests green (verified 2026-07-19)
- **Key tests:**
  - `SqlEventWalkerCoreSelectFromAliasingTests`: `getSimpleColumnVariableTest`, `getSubstitutionColumnVariableV1–V16Test` (16 tests)
  - `SqlEventWalkerDmlUpdateInsertDeleteTruncateTests`: `insertComplexSubstitutionI1–I10`, `updateComplexSubstitutionU1–U10` (20 tests)
  - `SqlEventWalkerNonSqlEndpointParserTests`, `SqlEventWalkerFunctionsAggregatesWindowingTests`, others
- **Blockers:**
  - V9-V16 CTE-wrapped tests: external CTE tokens migrating from `table_dictionary` → `query_dictionary`; `def_query*` wrapper nesting
  - INSERT/UPDATE I1–I10 / U1–U10: query-dict shape regressions; assignment token tracking

#### 2. **Predicand Substitution Variables** (type=predicand)
- **Definition:** Variables for complete predicate expressions or subqueries (e.g., `<comparison_expr>`).
- **Test count:** ~25+ tests
- **Status:** ✅ **LIKELY PASSING** — Non-DML predicand handling working
- **Key tests:**
  - `SqlEventWalkerNonSqlEndpointParserTests`: predicand substitution tests across CASE expressions (13+ tests)
  - `SqlEventWalkerPredicatesOperatorsSubstitutionsTests`: predicand comparison, NULL checks, ORDER BY (6+ tests)
  - Non-DML aggregate, function, window function tests
- **Action:** Continue monitoring; treat as stable baseline

#### 3. **Condition Substitution Variables** (type=condition)
- **Definition:** Variables for boolean conditions or entire clause conditions (e.g., `<where_cond>`).
- **Test count:** ~13+ tests
- **Status:** ✅ **LIKELY PASSING** — Condition substitution stable
- **Key tests:**
  - `SqlEventWalkerPredicatesOperatorsSubstitutionsTests`: condition comparison, HAVING, WHERE (7+ tests)
  - `SqlEventWalkerNonSqlEndpointParserTests`: nested condition substitution (6+ tests)
- **Action:** Continue monitoring; baseline stable

#### 4. **Tuple Substitution Variables** (type=tuple)
- **Definition:** Variables representing table or datasource names (e.g., `<[Schema].[Table]>`).
- **Test count:** ~20+ tests
- **Status:** ✅ **LIKELY PASSING** — Tuple substitution comprehensive and stable
- **Key tests:**
  - `SqlEventWalkerNonSqlEndpointParserTests`: 18+ tests covering simple/complex/extended tuple names
  - Live sample queries and custom parsing endpoint tests
- **Action:** Continue monitoring; well-covered and stable

#### 5. **In_List Substitution Variables** (type=in_list)
- **Definition:** Variables for IN-list expressions (e.g., `<in_list_expr>`).
- **Test count:** ~10+ tests
- **Status:** ⚠️ **PARTIAL** — Core fix applied post-2026-07-11; 3 golden-only updates pending
- **Key tests:**
  - `SqlEventWalkerPredicatesOperatorsSubstitutionsTests`: 6 basic IN/NOT IN tests
  - `SqlEventWalkerNonSqlEndpointParserTests`: 2 embedded IN-list tests
- **Blockers:**
  - Group A IN-list tests are green: `correlatedInSubqueryFirstCteStandaloneTest`, `correlatedInSubqueryNestedCteWithOuterRefTest`, `correlatedInSubqueryFinalQueryReferencesCteChainTest`
- **Action:** Continue baseline monitoring; stable

#### 6. **Join_Extension Substitution Variables** (type=join_extension)
- **Definition:** Variables for join extensions or extension-specific conditions (e.g., `<join_ext_cond>`).
- **Test count:** ~7+ tests
- **Status:** ✅ **LIKELY PASSING** — Join extension handling stable
- **Key tests:**
  - `SqlEventWalkerNonSqlEndpointParserTests`: 4 tests
  - `SqlEventWalkerJoinsAndTableResolutionTests`: 3 basic join tests
- **Action:** Continue monitoring; baseline stable

### Now-green probes in `SqlEventWalkerPredicatesOperatorsSubstitutionsTests`

| Test | Variable type | Clause type | Status |
|------|---------------|-------------|--------|
| `selectListWithSubstitutions` | predicand, condition, column, tuple | SELECT list + FROM + JOIN/WHERE | ✅ GREEN |
| `withQueryFromNavigateV2StudentSubstitution` | predicand, condition, column, tuple | WITH + SELECT + JOIN + WHERE | ✅ GREEN |
| `whereConditionWithSingleConditionVariableTest` | condition | WHERE | ✅ GREEN |
| `whereConditionWithSingleColumnVariableTest` | column | WHERE | ✅ GREEN |
| `whereConditionComparingPredicandVariablesTest` | predicand | WHERE | ✅ GREEN |
| `whereConditionComparingPredicandVariableToNullTest` | predicand | WHERE | ✅ GREEN |
| `whereConditionComparingPredicandVariableToNotNullTest` | predicand | WHERE | ✅ GREEN |

### Remaining Phase 10 blockers — ✅ NONE (historical inventory)

<details>
<summary>Historical blocker list (all green as of 2026-07-19)</summary>
| Family | Exact tests remaining |
|--------|------------------------|
| Column CTE V9-V16 | `getSubstitutionColumnVariableV9CteWrappedWhereVariantWithJoinOnSelectColumnTest`, `getSubstitutionColumnVariableV10CteWrappedGroupByVariantWithJoinOnSelectColumnTest`, `getSubstitutionColumnVariableV11CteWrappedOrderByVariantWithJoinOnSelectColumnTest`, `getSubstitutionColumnVariableV12CteWrappedHavingVariantWithJoinOnSelectColumnTest`, `getSubstitutionColumnVariableV13CteWrappedQualifyVariantWithJoinOnSelectColumnTest`, `getSubstitutionColumnVariableV14CteWrappedSecondJoinOnVariantWithJoinOnSelectColumnTest`, `getSubstitutionColumnVariableV15CteWrappedSelfUnionVariantWithJoinOnSelectColumnTest`, `getSubstitutionColumnVariableV16CteWrappedSelfIntersectionVariantWithJoinOnSelectColumnTest` |
| INSERT complex I1-I10 | `insertComplexSubstitutionI1WithCteGroupByHaving`, `insertComplexSubstitutionI2SubqueryUnionWhereSubstitutions`, `insertComplexSubstitutionI3WithCteIntersectOrderBySubstitution`, `insertComplexSubstitutionI4NestedWithInCteBody`, `insertComplexSubstitutionI5WithCteQualifyWindowSubstitution`, `insertComplexSubstitutionI6SubqueryJoinOnColumnSubstitution`, `insertComplexSubstitutionI7ChainedCteReferences`, `insertComplexSubstitutionI8UnionIntersectNestedSubquery`, `insertComplexSubstitutionI9WithCteSelfUnionBranches`, `insertComplexSubstitutionI10SubqueryGroupByHavingQualifyCombined` |
| UPDATE complex U1-U10 | `updateComplexSubstitutionU1WithCteGroupByHaving`, … `updateComplexSubstitutionU10SubqueryGroupByHavingQualifyCombined` |

</details>

### Pass/Fail Summary by Variable Type — ✅ ALL GREEN (2026-07-19)
| Type | Count | Status | Priority | Action |
|------|-------|--------|----------|--------|
| **Column** | ~40 | ✅ PASS | — | V1–V16 + I/U series verified green |
| **Predicand** | ~25 | ✅ PASS | LOW | Monitor |
| **Condition** | ~13 | ✅ PASS | LOW | Monitor |
| **Tuple** | ~20 | ✅ PASS | LOW | Monitor |
| **In_List** | ~10 | ✅ PASS | LOW | Monitor |
| **Join_Extension** | ~7 | ✅ PASS | LOW | Monitor |
| **TOTAL** | **~115+** | ✅ **100%** | — | Phase 10 complete |

### Phase 10 Completion Criteria — ✅ MET (2026-07-19)

1. ~~Column Substitution: Fix V9-V16~~ ✅
2. ~~In_List Substitution~~ ✅
3. ~~Predicand, Condition, Tuple, Join_Extension~~ ✅

**Gate command:**
```bash
cd parse
mvn -Psmoketest-quality-gate test  # includes substitution variable tests
# OR targeted:
# mvn -Dtest=SqlEventWalkerCoreSelectFromAliasingTests#getSubstitutionColumnVariableV* test
# mvn -Dtest=SqlEventWalkerDmlUpdateInsertDeleteTruncateTests#*ComplexSubstitution* test
```

### Timeline & Dependencies — ✅ CLOSED

- Phase 10 completed Jul 2026; full-suite verification 2026-07-19 (1203/1203).

---

## Published scope vs global dictionary — detailed rules

These rules govern acceptable variance between **live global maps** (`queryColumnDictionaryMap` / live `queryN` keys) and **published symbol-tree payloads** (`def_queryN` submaps). Canonical examples: `unaliasedDerivedSimpleAllOuterClausesV1Test` (`def_query5`, `def_query2`, `def_query3`).

**Authoritative design contract (table vs query dictionary roles, visibility, collection rules):** [table-and-query-dictionary-design.md](table-and-query-dictionary-design.md)

### Two artifacts, two roles

| Artifact | When written | Role |
|----------|--------------|------|
| **Global `queryN`** in `queryColumnDictionaryMap` | Accumulated during the walk; merges at scope export | Live working index of column-name → token strings; may grow after a child `def_queryN` is published |
| **`def_queryN` payload** in the symbol tree | Once, at that scope's finalize/publish | Immutable snapshot of that grammar frame at exit: `query_dictionary`, `interface`, clause lists (`filters`, `grouped_by`, `ordered_by`), nested `def_*` children, `table_alias`, etc. |

**Rule:** Do not treat mismatches between global `queryN` and embedded `def_queryN.query_dictionary` as bugs requiring backpatch of finalized child payloads.

### No backpatch into finalized child scopes

**Rule:** After a nested query scope is finalized and published as `def_queryN`, **never** recursively drill back into that child payload to update:

- `table_ref` on entries in `filters`, `grouped_by`, `ordered_by`, or `interface`
- `query_dictionary` token lists
- Any other archived clause collector on the child

Resolution that becomes possible only after a **parent** scope finishes (correlated outer columns, EXISTS wrappers, etc.) is recorded in the **parent's** published payload — not by rewriting children.

**Example (V1):** `def_query3.filters` includes `{name=col1, table_ref=null}` for the correlated outer reference in `WHERE x.ex1 = col1`. That is correct in the child snapshot. The parent resolves `col1` → `query0` in `def_query5.filters` / `grouped_by` / `ordered_by`. **Do not** retroactively set `def_query3.filters.col1.table_ref` to `query0`.

### Grammar scope owns token and clause attribution

**Rule:** A column reference belongs to the **symbol-table context active at the parse event** (grammar sequence / DFS walk), not necessarily to the inner subquery that **semantically** supplies the aliased source.

**Example (V1):** In `EXISTS (SELECT ex1 FROM (SELECT ex1 FROM tab3) x WHERE x.ex1 = col1)`, the token for alias `x` at line 5 column 61 lies in the **wrapping predicate / `query3` frame**, not in grammatically recognized `query2` scope — even though `x` maps to `query2`.

Clause lists and `table_ref` updates on a published child reflect only what that child's frame resolved at its own exit.

### Qualified refs: global push-down to source query is allowed

**Rule:** When a **qualified** reference `alias.column` (or equivalent) clearly resolves to a known query-backed source (`query2`, etc.), it is acceptable — and often desirable — to merge the **token string** into that source's entry in the **global** `queryColumnDictionaryMap`. This is forward materialization of an obvious, locally accessible binding, not backtracking.

**Example (V1):** Global `query2` may include both:

- `ex1` @ line 5 col 38 (from `query2`'s own SELECT, also in `def_query2.query_dictionary`)
- `x` @ line 5 col 61 (from `x.ex1` in the EXISTS WHERE, grammatically outside `query2`'s published frame)

**`def_query2.query_dictionary`** correctly contains only the SELECT-list `ex1` token — the scope snapshot taken at `query2` finalize. The extra `x` token in global `query2` does **not** require updating `def_query2`.

### Current-scope `query_dictionary` includes clause tokens (outer query)

**Rule:** For the scope being finalized, `query_dictionary` should accumulate token strings for column names referenced in **all clauses of that query** (SELECT, JOIN/ON, WHERE, GROUP BY, HAVING, QUALIFY, ORDER BY), not only the select list — via the working `unresolved_column` map and unified scope-exit materialization. That applies to the **current** scope's export; it does not imply patching nested `def_*` children.

**Example (V1):** `def_query5.query_dictionary` lists tokens for `col1`/`col2`/`col3` across outer clauses; `def_query0.query_dictionary` lists only inner SELECT tokens.

### Test / golden expectations

- Child clause entries with `table_ref=null` for deferred correlated refs: **expected** in published child payloads; not a failure to fix by backpatch.
- Global `queryN` richer than `def_queryN.query_dictionary`: **expected** when qualified push-down or later-frame tokens target the live global index.
- When updating goldens, align `def_queryN` snapshots and global maps with these rules — do not force them to be identical.

---

## Current narrowed execution plan (next implementation pass)

This pass intentionally narrows scope to remove side effects from mixed refactors.

### In scope (SQL statements only)

- Non-DML SELECT/query/set-operation resolution cleanup
- Removal of recursive fallback lookups that descend into embedded symbol-table payloads
- Enforcement of canonical publish/read contract:
   - embedded payloads are published as `def_*`
   - local alias maps and unaliased source refs continue to use `queryN`/`valuesN`/`unionN`/`intersectN` (or real alias/table names)
- Set-operation validation should consume current-level published participants, not descend into child internals to reconstruct participants
- Grammar-event-sequenced handoff fixes where missing information is carried in scoped submaps from producing rules to consuming rules

### Explicitly out of scope for this pass

- CTE behavior changes/refactors (including broad `context_list` redesign)
- PIVOT/UNPIVOT behavior changes
- DML-specific behavior changes (UPDATE/DELETE/INSERT), unless a shared SQL path is directly impacted

### Narrow pass acceptance criteria

1. Recursive descendant fallbacks are removed or bypassed for non-CTE SQL statement resolution paths.
2. No new child-scope hoisting is introduced "just to make lookup work".
3. `multipleIntersectedUnionSubqueryInterfaceValidationV4Test` is locked to the intended four fatals.
4. `multipleIntersectSubqueryInterfaceValidationV2Test` and the immediate set-op neighbor tests are revalidated for mismatch-label/count parity.
5. No regression in alias/read contract (`def_*` payload publication with local alias refs still non-`def_*`).
6. No newly introduced fallback/recovery readers were added without explicit user discussion and approval.
7. New data transport uses grammar-event-scoped submaps (producer rule -> consumer rule), not post-hoc artifact reconstruction.

---

## Problem statement

Today each optional SELECT clause — select list, FROM/JOIN/ON, WHERE, GROUP BY, ORDER BY, HAVING, EXISTS/IN/predicand subqueries, QUALIFY — has its own collection shape and resolution timing:

| Carrier | Collected from | Resolution today |
|---------|----------------|------------------|
| `unresolved_column` | `collectUnresolvedColumnReference` on column exits | Full machinery in `convertSymbolTableToTableDictionary` |
| `interface` | `exitSelect_item` | Richest path (materialization + validate) |
| `filters` | WHERE, HAVING, QUALIFY, search conditions | Early explicit + `assignTableRefsForColumnReferenceList` |
| `grouped_by` | `exitGroupby_clause` | Partial — no late `validateFilterReferences` |
| `ordered_by` | `exitOrderby_clause` (top-level) | Same gaps as GROUP BY |

Instead of merging qualified and unqualified refs into **one working set** during the walk and resolving **once** at scope exit (with deferral where FROM is pending), the walker often resolves clause slices prematurely or through parallel pipelines with different dictionary targets (global vs peek/local).

**Symptoms:**

- Orphaned unqualified columns missed or mis-attributed
- Qualified outer-correlated refs materialize in some paths but not others
- Duplicate logic in `SqlParseEventWalker` and `SqlParseSymbolTreeHelper`
- `def_queryN` vs `queryN` lookup inconsistency (partially fixed in Phases 1–4)

---

## Design target (do not violate)

1. **Ingress:** capture at parse event only — `collectUnresolvedColumnReference` on `exitColumn_reference` / `exitColumn_primary`. No retrospective scans of `filters`, `groupby`, `orderby`, or `def_queryN` interface maps.

2. **Working set:** `unresolved_column` is the live carry-up map. Clause lists (`filters`, `grouped_by`, `ordered_by`) are archived views for output/diagnostics, not separate resolution pipelines.

3. **Egress:** one decision tree at scope boundaries (full unified loop — **Phase 15**):

   ```
   defer (FROM stack pending, or policy says carry up) → keep in map
   derived (PIVOT/UNPIVOT) → consume; do not materialize to physical dict  [Phase 15]
   can resolve in visible scope → materialize to global table dictionary
   else → fatal (or pass up per subquery parent policy)
   ```

4. **Visible scope:** `buildEffectiveVisibleAliasMap` / `buildEffectiveVisibleTableCollection`, fed by inherited `context_list` (generalized CTE list) — not a parallel resolution channel.

5. **Scope publish:** every query-like scope exit follows the same shape:

   ```
   convertSymbolTable → export query_dictionary → publishQueryLikeScope
   ```

   VALUES is the reference implementation (`finalizeValuesScopeSymbolTable`).

6. **Minimize diff:** extend existing hooks; no second unresolved bucket, no `egressQualifiedUnresolvedMap`, no post-hoc interface scans.

7. **No new fallback logic without discussion:** do not add new fallback/recovery paths (especially recursive descent or global-map fallback scans) unless explicitly approved in-thread first.

8. **Grammar sequence over reconstruction:** tie behavior to natural grammar event ordering (`enter*`/`exit*`). If sequencing data is needed later, carry it forward in focused scope submaps created at the producer rule and consumed at deterministic downstream rules.

9. **Do not reconstruct lost sequencing context:** when data is absent at a consuming rule, treat it as a scope-finalization/publication bug to fix at source; do not compensate by reading nested child artifacts after the fact.

10. **Published vs global dictionaries:** frozen `def_queryN` snapshots vs live global `queryN` maps follow the rules in **Published scope vs global dictionary — detailed rules** (no child backpatch; grammar-owned token scope; qualified global push-down allowed).

---

## Qualified resolution — current architecture (Jul 2026, commit `2833a2f`)

**Single proof tree** for ordinary qualified refs: `resolveQualifiedColumnAgainstVisibleScope`

1. **Derived columns** (PIVOT/UNPIVOT) → `RESOLVED_DERIVED_COLUMN` via `isRelationalModifierDerivedColumnReference` (checked first)
2. **Query-backed alias** → global `queryColumnDictionaryMap` + `def_queryN.interface` via `querySourceExportsColumn`
3. **Physical alias/table** → visible `table_dictionary` only (`isPhysicalTableRefVisibleInScope` — not global walker dict)
4. **CTE parallel** → `tryResolveQualifiedEntryViaCteContext` via `context_list`

**Still side paths (candidates to consolidate):**

| Surface | Role today | Consolidation note |
|---------|------------|-------------------|
| ~~Early `resolveRelationalModifierDerivedColumnsFromUnresolvedMap` (×1 in convert)~~ | ~~Strips derived keys from unresolved map before diagnostics/egress~~ | **Phase 15** — delete shim in **15.5** after per-key derived consume in **15.1–15.3** |
| `local query_dictionary` | Scope **output** token map (select-list names → tokens) | **Not** used for `alias.col` proof; do not fold into `querySourceExportsColumn` |
| ~~`mergeSelectListQualifiedQueryAliasRefsIntoSourceQueryDictionary`~~ | ~~Post-hoc merge of qualified select-list refs into source query dict~~ | **Retired Jul 2026** (`e60d8f8`) — native interface loop + clause probe |
| `emitExplicitQualifiedUnknownDiagnostics` | Unified resolver + materialize on `RESOLVED_*`; no query-dict containsKey shortcut | ✅ shortcut retired Jul 2026 |

**Visible scope inputs:** `buildEffectiveVisibleAliasMap`, `buildEffectiveVisibleTableCollection`, `collectVisibleQuerySourceCollection` (FROM aliases only — not nested `def_*` siblings).

---

## Phase checklist

### Initial slice (recommended first) — Phases 1–4 ✅ DONE

Commit reference: `b59688c` — *canonical symbol table query references, steps 1 thru 4*.

| Phase | Status | Description |
|-------|--------|-------------|
| **1** | ✅ | Add `normalizeQueryScopeDefinitionKey` for `def_*` lookup |
| **2** | ✅ | Route `getQueryDefinitionSymbol` through normalized `def_*` lookup (current + ancestor scopes) |
| **3** | ✅ | Keep unaliased query handle registration stable (`queryN` map + alias self-mapping) |
| **4** | ✅ | Preserve unresolved transfer compatibility on `queryN` while publishing canonical `def_queryN` payload |

**Phase 4 follow-up (resolved):** `def_query3.filters` correlated `col1` stays `table_ref=null` in the published child snapshot; parent `def_query5` carries resolved `col1→query0`. No backpatch into `def_query3`. See **Published scope vs global dictionary — detailed rules** above.

Detail and patch chunks: see `def-query-canonicalization-phases1-4-checklist.md`.

---

### Phase 5 — Close def_query canonicalization gaps ✅ DONE (Jul 2026)

**Goal:** Finish the read-path and snapshot behavior started in Phases 1–4 before larger refactors.

| Task | Status | Notes |
|------|--------|-------|
| Resolve V1 `table_ref` delta | ✅ | Correlated outer ref stays `table_ref=null` in child; parent carries resolution |
| Expand verification (unaliased-derived V1–V16) | ✅ | All in gate + full suite green (Jul 2026); earlier “query-dict golden drift” was **snapshot refresh**, not missing logic |
| Document `queryN` vs `def_queryN` contract | ✅ | See **Published scope vs global dictionary** above |
| Enforce strict payload lookup | ✅ | Audit Jul 2026 — see **Phase 5 strict lookup audit** below |

**Gate:** Unaliased-derived V1–V16 + substitution column V1–V16 + full suite **1209/1209**.

#### What “strict payload lookup” means

When resolver code needs a **published query scope payload** (interface, `table_dictionary`, `filters`, etc.), it must load it by **`def_queryN`** (or `def_unionN` / `def_valuesN` / …), **not** by the live reference key `queryN`.

| Key kind | Example | Role |
|----------|---------|------|
| **Live reference** | `query3`, `union1` | Alias maps, `dependent_queries`, external `query_dictionary` keys |
| **Definition payload** | `def_query3` | Frozen published scope map after `publishQueryLikeScope` |

**Strict** means:

1. `getQueryDefinitionSymbol(liveKey)` normalizes to `def_*` and resolves via **`resolveDefinitionSymbolInScopeChain`** — **definition scope chain resolution** (current frame + ancestor symbol-table frames only), not nested map descent.
2. **No** reading live `queryN` maps as if they were published payloads (except intentional pre-publish walk-time holds, e.g. INSERT source sequence).
3. **No** recursive descent into arbitrary nested `def_*` children to “find” a payload — that would reconstruct lost sequencing and could bind the wrong scope.

The Phase 1–4 checklist once added `findInCurrentOrAncestorSymbolTablesRecursive` as a fallback; **strict mode removed that from `getQueryDefinitionSymbol`**. The recursive helpers were **dead code** (zero call sites) until deleted in Phase 5 closeout. The retained ancestor walk was renamed **`resolveDefinitionSymbolInScopeChain`** (Jul 2026) to reflect that it is the intended strict lookup mechanism, not a fallback. **Phase 15.6** replaces repeated egress-time walks with a pre-built convert-egress scope bundle.

#### Phase 5 strict lookup audit (Jul 2026)

| Check | Result |
|-------|--------|
| `getQueryDefinitionSymbol` uses `normalizeQueryScopeDefinitionKey` + `resolveDefinitionSymbolInScopeChain` only | ✅ |
| `findInCurrentOrAncestorSymbolTablesRecursive` / `findInScopeTreeByKeyRecursive` call sites | **0** — **deleted** |
| `resolveDefinitionSymbolInScopeChain` is private; egress readers migrate to scope bundle in **15.6** | ⏸️ Phase 15.6 |
| Live `queryN` reads for published payload (grep) | Only pre-publish INSERT-source `hold` map — **intentional** |
| Unaliased-derived V1–V16 + gate **195/195** after deletion | ✅ (verify on commit) |

#### Left open from early Phase 5 notes — resolution

| Item | Necessary? | Action |
|------|------------|--------|
| **V4–V16 “query-dict golden drift”** | No — was richer token capture in `query_dictionary`; goldens updated | **Closed** — not new tests; existing V1–V16 prove contract |
| **Consolidate query-dict dual-write** | Backfill repair closed (Step D); architectural two-store remains | **Phase 19** — single publish ingress; see Phase 19 |
| **Definition scope chain walks at convert egress** | Required today (`resolveDefinitionSymbolInScopeChain`) | **Phase 15.6** — pre-built egress scope bundle |
| **Ad-hoc `queryN`/`def_queryN` at call sites** | Low — use canonical helpers when touched | Opportunistic cleanup during Phases 15+ |
| **Recursive descendant fallback** | **No** — violates strict contract | **Removed** (dead code deletion) |

**Phase 5 is complete.** No additional tests required beyond the existing unaliased-derived V1–V16 and substitution families already in the gate.

---

### Phase 6 — One canonical `convertSymbolTableToTableDictionary` ✅ DONE (Jul 2026)

**Goal:** Single implementation in `SqlParseSymbolTreeHelper`; all exit handlers delegate.

| Task | Status | Notes |
|------|--------|-------|
| Move walker copy → helper | ✅ | Walker no longer owns a duplicate block; all call sites go through helper |
| Parameterize DML differences | ✅ | UPDATE `updateTargetTableRef`, DELETE target preference — flags on shared convert |
| `exitJoin_extension_primary` | ✅ | Renamed to `reconcileJoinExtensionSymbolTable()` — mid-FROM partial reconcile, **not** scope publish |
| Dead-path audit | ✅ | See **Phase 6 audit (Jul 2026)** below |

**Gate:** ✅ Full parse test suite minus known skip list (15 PIVOT AST + 1 donor-email sample); no new golden churn from Phase 6 close.

#### Phase 6 audit (Jul 2026)

**`convertSymbolTableToTableDictionary` call sites** (grep confirms no walker duplicate):

| Call site | Purpose | Scope publish? |
|-----------|---------|----------------|
| `finalizeQueryScopeSymbolTable` | SELECT / CTE-body / insert-source exit | Yes |
| `finalizeUpdateScopeSymbolTable` | UPDATE exit | Yes |
| `finalizeDeleteScopeSymbolTable` | DELETE exit | Yes |
| `reconcileJoinExtensionSymbolTable` | `exitJoin_extension_primary` — PIVOT/UNPIVOT/lateral mid-FROM | **No** — partial reconcile while `query_specification` still open |

**Not parallel convert forks** (walk-time hooks that delegate to helper or run before scope exit):

| Location | Role |
|----------|------|
| `SqlParseEventWalker.resolveUnpivotGeneratedColumnsFromUnresolvedMap` | PIVOT/UNPIVOT walk-time unresolved stripping — calls helper |
| `SqlParseEventWalker.sourceHasDependencyColumn` | PIVOT hint validation at modifier exit — table-dict probe only, not egress |
| `finalizeQueryScopeSymbolTable` post-convert passes | Qualified batch exit + unified `resolveQualifiedUnresolvedEntries` on `globalQualifiedUnresolvedLocations` at statement-top query exit |

**Dead code removed:**

- `emitExplicitQualifiedUnknownDiagnostics`: unused `explicitTableRefByColumn` map (built from interface/filters, never read after query-dict shortcut retirement); dropped unused `localInterface` / `filtersList` parameters.

**Still intentional multi-path resolution inside helper** (Phase 9 retirement, not Phase 6 duplicates):

- `convertSymbolTableToTableDictionary` internal egress
- `resolveQualifiedUnresolvedAtQueryScopeExit` after finalize
- ~~`sweepBackfillQueryDictionaryFromResolvedInterfaceSources` + per-column backfill~~ — **deleted** (`876c7ce`); Step D audit ✅
- `consumeRelationalModifierDerivedColumnUnknownsFromUnresolvedMap` (private batch shim; **Phase 15.5** delete target)

---

### Phase 7 — Uniform query scope finalization (`exitQuery_specification`) (done)

**Closure note:** Phase 7 unified every leaf query exit behind helper finalizers so SELECT/CTE/insert-source/set-op scopes all publish one canonical shape. The inline publish paths are gone, `finalizeQueryScopeSymbolTable` is the single leaf-SELECT exit, and the WITH main-body promotion path now preserves the already-published `def_*` scope instead of wrapping it again.

**What Phase 7 covered:**

- Leaf SELECT, CTE body, insert-source SELECT, and set-op exits all route through the helper finalizers.
- `finalizeQueryScopeSymbolTable` owns the query conversion/export/publish path.
- `finalizeSetOperationScopeSymbolTable` and VALUES finalization now share the same publishing model.

**Status:** complete. No active Phase 7 blockers remain; the remaining Phase 7 text in this document is historical context only.

---

### Phase 8 — Unified qualified/unqualified egress helper (✅ done)

**Goal:** One `resolveQualifiedUnresolvedEntries` (or equivalent) used everywhere egress runs.

| Step | Status | Description |
|------|--------|-------------|
| Extract unified egress helpers | ✅ | `resolveUnqualifiedColumnAgainstVisibleScope`, `resolveQualifiedColumnAgainstVisibleScope`, `applyQualifiedScopeResolutionAtBatchExit` |
| Retire `retryResolvableQualifiedUnresolvedEntries` | ✅ | Replaced by unified batch exit path |
| Align `emitQualifiedSourceNotFoundFatals` | ✅ | CTE handled in batch exit; consolidated fatal emit helpers |
| Derived-column proof in unified resolver | ✅ | `RESOLVED_DERIVED_COLUMN` in `resolveQualifiedColumnAgainstVisibleScope`; alias-aware `isRelationalModifierDerivedColumnReference`; batch-exit reads `derived_columns` from symbol table |
| Retire query-dict diagnostic shortcut | ✅ | Removed `emitExplicitQualifiedUnknownDiagnostics` branch that gated on `localCurrentQueryDictionary.containsKey(columnName)` — was not real column proof |
| Scope visibility for physical materialization | ✅ | `isPhysicalTableRefVisibleInScope` + `canMaterializeQualifiedToKnownPhysicalSource` — no global table-dict fallback for sibling hidden scopes |
| Wire hooks | ✅ | `finalizeQueryScopeSymbolTable`, `exitPredicateSubqueryFrame`, convert path — global qualified ingress unified |
| Select-list qualified refs | ✅ | `mergeSelectList…` retired Jul 2026; V13/V14 + 14 gate query-dict tests green without post-hoc merge |
| Retire redundant late-pass helpers | ✅ | Deleted `materializeResolvableGlobalQualifiedUnresolvedLocations`; backfill consolidated into interface loop + final sweep; derived stripping 3→2 |
| Canary green | ✅ | Commit `2833a2f`; 3 fatals (`tab2.e3`, `gg.y`, `tt.f`); global `tt` = `{b,t,e}` only; outer `tt.f` does not materialize |

**Canary:** `SqlEventWalkerCoreSelectFromAliasingTests#nestedQueryDemoTest`

- Exactly **3 fatals:**
  1. `QUALIFIED_COLUMN_NOT_FOUND_IN_TABLE` — `tab2.e3` (l:6 c:61)
  2. `QUALIFIED_COLUMN_NOT_FOUND_IN_TABLE` — `gg.y` (l:7 c:23)
  3. `QUALIFIED_COLUMN_NOT_FOUND_IN_TABLE` — `tt.f` (l:5 c:53)
- Outer WHERE unqualified `c` resolves to joined **`tab1`** (l:7 c:7) — not a fatal
- Global **`tab1`** dict: `t`, `<y_col>`, `a`, `x`, `<z_col>`, `<w_col>`, `c` with correct token line/col
- Symbol table published under **`def_query11`** (not live `query11` key)

**CTE variant:** `nestedQueryDemoWithCteTest` — exactly **3 fatals** (same set minus `gg.y`; `gg.y` resolves via CTE `context_list`).

Handoff detail: `parse/docs/qualified-column-table-dict-handoff-prompt.md` (note: handoff doc still describes the pre-`c`-fatal 2-fatal baseline; treat this worklist as authoritative).

**Close Phase 8 when:**

1. ~~Nested demo canaries pass **both** fatal assertions and symbol-table goldens~~ ✅ (Jul 2026, `2833a2f`)
2. ~~UPDATE CTE spot checks re-run after helper retirement~~ ✅ Re-run Jul 2026 — same stale-golden failures as pre-retirement baseline (U3/U4/U5/U7/U9); no new canary regressions
3. V1–V3 consolidation canaries stay green; no new fallback readers added
4. ~~Remaining late-pass helpers either retired or documented as load-bearing with explicit rationale~~ ✅
5. ~~Early derived-column side paths consolidated or documented as load-bearing~~ ✅ 3→2 passes; post-late-resolution strip retired

**Gate:** Canary + UPDATE CTE spot checks (U3/U4/U5/U7/U9) + union-branch correlated tests — canaries green; UPDATE CTE spot checks remain stale-golden backlog (not behavior regressions from Phase 8 retirement).

---

### Phase 9 — Clause-list validation without separate resolution pipelines (✅ DONE — tests green)

**Goal:** `filters`, `grouped_by`, `ordered_by` validated through the same visible-scope rules at scope exit — not early per-clause resolution.

**Test verification (2026-07-19):** Full gate (195/195) and full suite (1203/1203) green, including scalar subquery V1–V9 matrix, HAVING/UNION/wildcard semantics probes, DML clause paths, and query-dict routing/diagnostic matrix.

| Task | Status | Notes |
|------|--------|-------|
| `SCOPE_CLAUSE_COLUMN_LIST_KEYS` | ✅ | `filters`, `grouped_by`, `ordered_by` |
| Single `validateArchivedClauseColumnRef` decision tree | ✅ | Skip query-alias refs; GROUP/ORDER require output-column proof; filters allow physical-table dict keys |
| `probeArchivedScopeClauseColumns` at convert exit | ✅ | Single probe per scope (Option C Jul 2026: retired `probeArchivedScopeClauseColumnsOnScopeTree`) |
| `probeArchivedScopeClauseColumnsOnScopeTree` | ❌ Retired | Removed after C0/C1 showed zero gate mutations |
| Retire stacked skip guards | ✅ | Inlined into `validateArchivedClauseColumnRef` |
| Retire `collectClauseColumnsIntoUnresolved` ingress | ✅ | Deleted |
| DML clause probe audit | ✅ | UPDATE/DELETE/SELECT all route through `convertSymbolTableToTableDictionary` probe |
| Supplementary gate goldens | ✅ | Scalar subquery matrix + semantics probes green |

**Fallback cleanup (Jul 2026):**

- ✅ Retired `mergeSelectListQualifiedQueryAliasRefsIntoSourceQueryDictionary` + `aliasMapsToQuerySource` (commit `e60d8f8`) — V13/V14 and 14 new gate query-dict tests green without post-hoc merge
- ✅ Removed dead `rehomeUpdateUnqualifiedUnknownsToSingleFromTable` + `getSingleUpdateFromTableReference` (Step A, zero callers)

**Note:** Outer/current-scope `query_dictionary` **does** include clause token strings (Phase 5+); nested published `def_*` children are not retroactively updated. `filters` / `grouped_by` / `ordered_by` remain the semantic `table_ref` signal per scope.

**Gate:** Predicand four-scenario tests, plain-union branch outer fatal, correlated scalar + CTE GROUP BY tests; full scalar subquery symbol-table matrix (V1–V9 + correlated); production WHERE/ORDER BY scalar + EXISTS probes; HAVING scalar/EXISTS probes; nested-formula `{query=queryN}` interface/filters test; query-over-queries wildcard resolution; top-level UNION; mixed multi-scalar composition.

---

### Phase 10 — Downward resolution via `context_list` (✅ DONE — Jul 2026)

**Note:** This section describes `context_list` work (dashboard Phase 11). Substitution-variable inventory is the separate **Phase 10 — Substitution Variable** section above.

| Task | Notes |
|------|-------|
| Generalize `cte_list` → `context_list` | WITH CTEs + parent FROM bindings for nested subqueries |
| `pushSymbolTableWithParentVisibleScope` | On predicand / IN / nested query enter (EXISTS already inherits CTE list) |
| Remove outer-correlated bubble path | `bubbleOuterCorrelatedUnresolvedToParentScope` for refs parent already knows |
| Strip stale `unresolved_column` from published `def_queryN` | Align with VALUES — no archived submaps on published payloads |
| Roll back clause-probe deferral patches | Symptom fixes from bubble model |
| **Re-assess Phase 9 dual clause probe** | After `context_list` lands — see **Close Phase 10 when** § below |

**Close Phase 10 (`context_list`) — ✅ MET (2026-07-19):**

1. Downward visible scope replaces upward bubble for outer-correlated refs. ✅
2. Single-probe reassessment complete. ✅
3. Canary set + full suite (1203/1203) green. ✅

**Gate:** `nestedQueryDemoTest`, `nestedQueryDemoWithCteTest`, correlated predicand + union/`ua` tests, `subqueryParseTest` (siloed fatals must fire at statement boundary when appropriate).

---

### Phase 11 — DML parity and late-pass retirement (✅ DONE — tests green)

**Goal:** UPDATE / DELETE / INSERT use the same ingress + egress + publish patterns; delete redundant fallbacks.

**Test verification (2026-07-19):**

| Run | Result |
|-----|--------|
| Phase 11 canary set (6 tests) | **6/6** pass |
| `SqlEventWalkerDmlUpdateInsertDeleteTruncateTests` (full class) | **103/103** pass |
| `*ComplexSubstitution*` (I1–I10 + U1–U10) | **30/30** pass |

**Phase 11 cleanup triage:**

| Fallback / helper | Current status | What Phase 11 should do |
|--------------------|----------------|--------------------------|
| ~~`mergeSelectListQualifiedQueryAliasRefsIntoSourceQueryDictionary`~~ | **Retired** (`e60d8f8`) | Native interface loop + `materializeResolvedQualifiedQuerySourceReference` |
| `backfillQueryDictionaryFromResolvedInterfaceSources` + `sweepBackfillQueryDictionaryFromResolvedInterfaceSources` | **Deleted** (`876c7ce`) | Step D audit ✅ — native capture verified (Jul 2026) |
| ~~`moveEntriesToSingleTableIfSingleTarget`~~ | **Deleted** (`1d20503`) | C2.4 closeout complete |
| ~~`moveUnknownEntriesToSingleWildcardBackedNonTableSource`~~ | **Deleted** (`1d20503`) | C2b.3 closeout complete |
| ~~`canResolveUnqualifiedFromSingleWildcardQuerySource`~~ | **Deleted** (`1d20503`) | C2b closeout complete |
| ~~`resolveRelationalModifierDerivedColumnsFromUnresolvedMap`~~ | **Retired E.4** — private `consumeRelationalModifierDerivedColumnUnknownsFromUnresolvedMap` at pre-diagnostics egress | **Phase 15.5** — delete shim after **15.1–15.4** |
| `resolveVisibleOuterDeferredUnresolved` | Removed | Inlined at call sites during Phase 11 kickoff |
| `isExistingArchivedClauseColumnRefSatisfied` + dual clause probe | Removed | Inlined into `validateArchivedClauseColumnRef`; dual-probe follow-up still remains |

**Phase 11 canary set:**

- `SqlEventWalkerCoreSelectFromAliasingTests#nestedQueryDemoTest`
- `SqlEventWalkerCoreSelectFromAliasingTests#nestedQueryDemoWithCteTest`
- `SqlEventWalkerCoreSelectFromAliasingTests#correlatedInSubqueryMiddleCteReferencesFirstCteTest`
- `SqlEventWalkerCoreSelectFromAliasingTests#correlatedExistsSubqueryMiddleCteReferencesFirstCteTest`
- `SqlEventWalkerDmlUpdateInsertDeleteTruncateTests#updateComplexSubstitutionU4NestedWithInCteBody`
- `SqlEventWalkerDmlUpdateInsertDeleteTruncateTests#insertComplexSubstitutionI4NestedWithInCteBody`

| Task | Status | Notes |
|------|--------|-------|
| `finalizeUpdateScopeSymbolTable` / `finalizeDeleteScopeSymbolTable` | ✅ | Aligned — audit against Phase 8–9 helpers remains |
| `finalizeInsertScopeSymbolTable` | ✅ | Exists; `exitInsert_expression` delegates — audit parity with UPDATE/DELETE finalize |
| DML canaries V9/V13 | ✅ | Passing with alias-token query dict |
| DML golden refresh | ✅ | Full DML class green (103/103) as of 2026-07-19 |
| DML clause probe | ✅ | Routed through convert probe; tests green |
| EXCEPT set-op parity | ✅ | **Done** — Phase 13.1 complete (Jul 2026): `setop` stamping, operator-aware diagnostics, UNION/INTERSECT→EXCEPT clone matrices, three-level nesting suite, gate canaries |
| Retire late-pass fallbacks (as scopes self-contain) | ✅ | **Done** — `mergeSelectList` + early bulk (C1a/C1b) + late drains (C2a/C2b) + orphan helpers (C2 closeout) retired |
| Donor-email forward alias (TODO B) | ✅ | **Done** — Phase 13.4 same-select-list forward alias resolution |

**INSERT note:** INSERT **source** resolves like SELECT; insert wrap only maps target columns. Orphan promotion to target table is **incorrect** for INSERT (removed in `0ec0b75`).

**Gate:** DML test class + `insertValues*` + orphan parity tests — **all green** (103/103 DML class, 2026-07-19).

#### `context_list` closeout checklist — ✅ SIGNED OFF (2026-07-19)

**Main code paths — stable**

- [x] `SqlParseSymbolTreeHelper.java`: `context_list` ownership canonical in `ensureContextListSymbolMap()`, `getContextListSymbolMap()`, `pushSymbolTableWithParentVisibleScope()`
- [x] `SqlParseEventWalker.java`: nested `WITH` seed/restore aligned with canonical helper path
- [x] `SqlParseSymbolTreeHelper.java`: `collectPublishedScopeContextList()` / `mergePublishedScopeContextListIntoAliasMap()` sole publication merge path

**Canaries — all green**

- [x] `nestedQueryDemoTest`
- [x] `nestedQueryDemoWithCteTest`
- [x] `correlatedInSubqueryMiddleCteReferencesFirstCteTest`
- [x] `correlatedExistsSubqueryMiddleCteReferencesFirstCteTest`
- [x] `updateComplexSubstitutionU4NestedWithInCteBody`
- [x] `insertComplexSubstitutionI4NestedWithInCteBody`

**Finish line**

- [x] Canary set passes without alternate `context_list` handling
- [x] No widening golden churn beyond consolidation families
- [x] `context_list` closed — fallback retirement is optional follow-up, not test-blocking

#### Dead code removed (Jul 2026)

- [x] `mergeSelectListQualifiedQueryAliasRefsIntoSourceQueryDictionary` + `aliasMapsToQuerySource` — post-hoc query-dict merge (Step B, `e60d8f8`)
- [x] `rehomeUpdateUnqualifiedUnknownsToSingleFromTable` + `getSingleUpdateFromTableReference` — zero callers (Step A)
- [x] `getTableFunctionSourceCount` / `setTableFunctionSourceCount` / `getSuppressedAmbiguousUnqualifiedKeys` / `getTableFunctionSourceRefs` — zero external callers (Step F, Jul 2026)
- [x] `ArchivedClauseColumnRefResult.satisfied()` — never present in current tree (Step F audit)

#### Likely dead cleanup candidates

_None remaining from Phase 14 Step F audit._

#### Untested feature paths

These are uncovered by the current coverage run, but they map to real feature families with their own tests elsewhere in the project. The main question is coverage depth, not whether the code is dead.

- [ ] [SqlParseSymbolTreeHelper.java](../src/main/java/sql/symboltree/SqlParseSymbolTreeHelper.java): pivot / unpivot helpers (`resolveUnpivotGeneratedColumnsFromUnresolvedMap`, `mergePivotAggregateDependencyRefsFallbackIfPresent`, `mergeUnpivotDerivedRefsIfPresent`, and related helpers) — feature-specific and backed by dedicated Pivot/Unpivot tests.
- [ ] [SqlParseSymbolTreeHelper.java](../src/main/java/sql/symboltree/SqlParseSymbolTreeHelper.java): table-function helpers and live UPDATE helpers (`reconcileJoinExtensionSymbolTable`, `isInsertStatementSqlTree`) — feature-specific paths, not dead code.
- [ ] [SqlParseSymbolTreeHelper.java](../src/main/java/sql/symboltree/SqlParseSymbolTreeHelper.java): unpivot / derived-column / VALUES / DML support paths — these are covered by dedicated walkers and golden tests, but not by the 130-test consolidation gate.

**Project-wide test-bed note**

- [ ] Current repo scan of `parse/src/test/java` finds **1,055** `@Test` methods across 12 walker test classes. That is broader than the 130-test consolidation gate, but still not a full top-to-bottom test bed for every uncovered helper branch.
- [ ] The uncovered `context_list` publication helpers are not a test-bed gap; they are already exercised by the 130-test gate and the full gate, and should stay in place.
- [ ] The uncovered pivot/unpivot, VALUES, DML, and table-function helpers are mostly a gate-size issue when the relevant feature classes already exist, but they still expose broader coverage gaps for feature families that are not validated by the consolidation gate.

### Origin-CTE backfill — ❌ PERMANENTLY REJECTED (Jul 2026)

**Decision:** Do **not** retro-write descendant query references into an origin CTE's published `def_*` payload. That approach rewrites already-finalized nested scopes, churns goldens across nested CTE / DML / access-object tests, and violates the no-backpatch publish contract.

**Superseded by:** Phase 11 `context_list` downward visibility + native capture at scope exit (Phase 8–9 / Step D). Parent attribution belongs in the **parent's** published payload, not by drilling back into finalized children.

**Do not reopen** unless a production regression explicitly requires it and the publish contract is revised deliberately.

## Phase 14 — Universal per-column resolution (Steps C–F)

**Goal:** Retire **all single-viable-source bulk relocation** in favor of the **canonical per-column pipeline** — one column at a time, regardless of whether the sole source is a physical table or a wildcard/single query-backed subquery:

```
walk archives ref → unresolved_column
  → resolveUnqualifiedColumnAgainstVisibleScope / resolveQualifiedColumnAgainstVisibleScope
  → applyUnqualifiedScopeResolutionResult / materializeResolvedUnqualifiedReference
  → probeArchivedScopeClauseColumns (WHERE / GROUP BY / ORDER BY / UPDATE RHS)
```

**Unified abstraction — “single viable source”:** When exactly one source can provide a column (physical `FROM` table, or a single query-backed alias whose output is wildcard `*` or lists the column), resolution uses the **same per-key path** everywhere the reference appears (SELECT list, clauses, ingress merges). All bulk shortcuts for this policy are **retired** (Step C complete).

| Bulk shortcut | Single source | When | Per-column replacement | Status |
|---------------|---------------|------|------------------------|--------|
| ~~`moveEntriesToSingleTableIfSingleTarget`~~ | Sole physical `FROM` table | ~~Early convert + late scope exit~~ | `resolveUnqualifiedColumnAgainstVisibleScope` → `materializeResolvedUnqualifiedReference` | **Deleted (`1d20503`)** |
| ~~`moveUnknownEntriesToSingleWildcardBackedNonTableSource`~~ | Sole wildcard-backed query source | ~~Early convert~~ | Same resolver → `materializeResolvedUnqualifiedReference` → query dict | **Deleted (`1d20503`)** |
| ~~`canResolveUnqualifiedFromSingleWildcardQuerySource`~~ | Sole wildcard-backed query source | ~~Late scope finalize~~ | Per-key resolve + materialize at convert | **Deleted (`1d20503`)** |

**Not bulk relocation (keep):** `processWildcardUnknownEntries` expands `*` / `alias.*` into concrete scoped unknowns — this is ingress normalization, not single-source shoveling.

**Why this is not like Step B (`mergeSelectList`):** Step B removed a post-hoc repair that duplicated interface-loop work. The bulk helpers above are **alternate ingress paths** that shovel the entire `unresolved_column` map before or after the per-column passes. Retiring them requires proving every straggler category is drained by the standard passes — or explicitly deferred/bubbled (correlated subqueries).

**C1b gap (fixed Jul 2026, `1274ee3`):** Interface loop now passes `visibleQuerySourceCollection` and `allowQuerySourceFallback=true` for unqualified output-column resolution; early wildcard bulk bind removed.

**Prerequisite gate (every sub-step):**

```bash
cd parse
mvn -Psmoketest-quality-gate test
```

### Terminology

| Term | Code | Role |
|------|------|------|
| **Early physical bulk priming** ✅ **retired (C1a)** | ~~`relocateUnqualifiedToSingleTableExcludingOutputAliases`~~ | Was: bulk move all unqualified keys to sole physical table before interface loop (`37da020`) |
| **Early wildcard-query bulk bind** ✅ **retired (C1b)** | ~~`moveUnknownEntriesToSingleWildcardBackedNonTableSource`~~ (call removed) | Was: bulk move all unqualified keys to sole wildcard-backed query source (`1274ee3`) |
| **Late physical scope-exit drain** ✅ **retired (C2a)** | ~~`materializeRemainingSingleTableUnqualifiedAtScopeExit`~~ | Was: last-chance bulk bind on `unqualifiedUnresolved` to sole physical table after convert (`cd937c2`) |
| **Late wildcard scope-exit suppressor** ✅ **retired (C2b)** | ~~`canResolveUnqualifiedFromSingleWildcardQuerySource`~~ | Was: bulk-validates then clears `unqualifiedUnresolvedForLocal` at finalize without per-column materialize (`1d20503`) |
| **Per-column ingress drain** | `resolveRemainingUnresolvedAgainstQuerySources` | Loops unresolved map; unified resolver + materializer per key (already passes `visibleQuerySourceCollection`) |
| **Clause egress** | `probeArchivedScopeClauseColumns` | filters / grouped_by / ordered_by + UPDATE assignment RHS |
| **Output-alias deferral** | `isInterfaceOutputAliasOnly` / `isIntraQueryOutputAliasUsage` | Per-column guard (replaced bulk `deferInterfaceOutputAliasOnlyUnqualifiedEntries`, removed with C1a) |

### Target convert order

**Today (after Phase 14 Step E)** — physical/query per-key pipeline is in place; derived columns still need a batch shim:

```
1. Wildcard expansion (processWildcardUnknownEntries)          [keep — ingress normalization]
2. PIVOT operand column resolve (pre/post propagateUnqualifiedSelectStar)
3. propagateUnqualifiedSelectStarToScopeTables
4. UPDATE/DML-specific RHS + target hooks
5. PIVOT/UNPIVOT derived-column batch drain                    [SHIM — delete in Phase 15.5]
6. emitExplicitQualifiedUnknownDiagnostics (qualified batch)
7. Interface loop — per output column: unified resolver + materialize
8. resolveRemainingUnresolvedAgainstQuerySources — per remaining unqualified key (no derived hints today)
9. probeArchivedScopeClauseColumns — per archived clause ref (skips derived; rarely consumes)
10. patchInterfaceTableRefsForSinglePhysicalTableScope
11. validateQueryInterface
```

**Phase 15 target (unified egress)** — one per-key loop replaces steps 5–9:

```
1–4.  [unchanged prep: wildcard, PIVOT operands, SELECT*, UPDATE hooks]
5.    resolveUnresolvedColumnsAtConvertEgress — single loop over unresolved_column
        for each key:
          resolveColumnAgainstVisibleScope(…, derivedColumns, hints)   // derived checked FIRST
          RESOLVED_DERIVED_COLUMN  → consume from map, no materialize
          RESOLVED_PHYSICAL / RESOLVED_QUERY → materialize, consume
          DEFERRED                 → keep in map
          UNRESOLVED               → fatal (or pass-up per policy)
      emitExplicitQualifiedUnknownDiagnostics — diagnostics only (or fed by loop outcomes)
      interface loop + clause probe — callers of shared per-key resolver, NOT parallel pipelines
6.    patchInterfaceTableRefsForSinglePhysicalTableScope
7.    validateQueryInterface
```

**Why step 5 is still a batch drain today:** several downstream consumers (`resolveRemaining…`, late lineage merge, qualified-unknown batch) do not treat `RESOLVED_DERIVED_COLUMN` as “resolved, non-physical.” The drain is a negative filter before those paths run. Phase 15 threads derived through every consumer, then deletes the shim.

Scope exit (`finalizeQueryScopeSymbolTable` / UPDATE FROM finalize) should **not** need bulk bind once the unified loop drains all local keys; correlated keys pass up via existing defer/bubble flags.

### Straggler categories (audit checklist for Step C2)

Keys that can still sit in `unresolved_column` after convert today — each must be **consumed**, **materialized**, **deferred**, or **passed up** without bulk bind:

| # | Category | Typical SQL / ingress | Standard handler today | Bulk bind risk if removed too early |
|---|----------|----------------------|------------------------|-------------------------------------|
| S1 | Correlated subquery deferral | `WHERE EXISTS (SELECT 1 FROM t WHERE t.col = outer.col)` | Skip `resolveRemaining…`; pass-up at finalize | Late drain may bind local `col` — OK; outer refs stay qualified |
| S2 | Output-alias-only names | `SELECT expr AS revenue …` then `ORDER BY revenue` | Per-column `isInterfaceOutputAliasOnly`; clause probe → `query_dictionary` | Preserved via per-column guard (bulk helper removed C1a) |
| S3 | Qualified-shaped keys in unqualified map | Ingress from nested `def_*` / UPDATE paths | Skipped by `resolveRemaining` loop (`tableRef != null`) | Needs per-key qualified path or map normalization |
| S4 | Clause-only unqualified refs | `WHERE status = 1` (single table `accounts`) | `probeArchivedScopeClauseColumns` → materialize | Covered once bulk priming removed if probe runs on full map |
| S5 | Non-interface ingress | Nested-scope merge, UPDATE no-FROM temp keys | `resolveRemainingUnresolvedAgainstQuerySources` | Primary replacement for early bulk |
| S6 | Hoisted archived unresolved | `collectAndStripUnresolvedFromScopeTree` at finalize | Re-enters map post-convert; late drain today | ✅ **C2a** — convert-time lineage + pass-up consumption |
| S7 | Subquery `emitFinalUnresolvedUnknownFatal=false` | Nested `query_specification` | Intentionally leaves map until finalize | ✅ **C2b** — no spurious fatals after suppressor removal |
| S8 | Substitution columns | `<emp_id>` unqualified | Interface substitution branch + materialize | Gate + substitution families |
| S9 | Ambiguous multi-source | `FROM t JOIN sq …` unqualified `col` | Unified resolver → AMBIGUOUS fatal | **Never** bulk-bind without per-column source count |
| S10 | Query-only / wildcard single source | `SELECT col FROM (SELECT * FROM t) sq` | Per-column via `isWildcardBackedQueryCandidate` + materialize to query dict | ✅ **C1b** — interface loop passes `visibleQuerySourceCollection` |

### Step C1 — Eliminate early bulk relocation ✅ DONE (Jul 2026)

**Objective:** Remove all early bulk shoveling in `convertSymbolTableToTableDictionary`. Rely on interface loop (step 6) + `resolveRemaining…` (step 7) + clause probe (step 8).

#### C1a — Early physical bulk priming ✅ DONE (`37da020`)

Removed both `relocateUnqualifiedToSingleTableExcludingOutputAliases` calls; deleted method; removed prepend relocation from `mergeSourceLineageIntoPhysicalTableDictionary`.

| Sub-step | Action | Verify |
|----------|--------|--------|
| **C1a.0** | Gate canaries: single physical table + unqualified WHERE; `FROM t JOIN (SELECT …) sq` ambiguity; output-alias ORDER BY; `insertDictionaryHandlingUnqualifiedFallsBackToTargetTableV3` | Gate 195 + spot DML INSERT V3 |
| **C1a.1** | Remove **first** relocate only (~L1403) | Gate + full suite |
| **C1a.2** | Remove **second** relocate (~L1453) | Gate + full suite |
| **C1a.3** | If failures: fix per-key gap — document S1–S9 row; do not restore bulk without cause | Targeted test |

**Rollback:** restore one relocate call at a time; prefer second (guarded) pass over first (unguarded).

#### C1b — Early wildcard-query bulk bind ✅ DONE (`1274ee3`)

Removed `moveUnknownEntriesToSingleWildcardBackedNonTableSource` call from convert. Method body deleted at **C2b.3** closeout (`1d20503`).

| Sub-step | Action | Verify |
|----------|--------|--------|
| **C1b.0** | Confirm gate coverage for query-only wildcard scopes: `nestedSelectStarV1`–`V7` (`SqlEventWalkerSubqueriesAndClauseSemanticsTests`); add to gate if missing | `nestedSelectStar*` + gate |
| **C1b.1** | **Prerequisite fix:** interface loop unqualified branch — pass `visibleQuerySourceCollection` and `allowQuerySourceFallback=true` to `resolveUnqualifiedColumnAgainstVisibleScope`; set `materializeWhenImmediateScope=true` in `applyUnqualifiedScopeResolutionResult` where appropriate for query-only scopes | `nestedSelectStarV1`–`V3` spot |
| **C1b.2** | Remove `moveUnknownEntriesToSingleWildcardBackedNonTableSource` call | Gate + `nestedSelectStarV1`–`V7` + full suite |
| **C1b.3** | If failures: confirm `collectUnqualifiedSourceReferences` + `isWildcardBackedQueryCandidate` bind single query alias; confirm `materializeResolvedUnqualifiedReference` routes to `mergeExplicitQualifiedUnknownIntoSourceQueryDictionary` | S10 row |

**Can run in parallel with C1a** if different test families; prefer **C1b.1 before C1b.2** (interface-loop fix may be required for clean removal).

**Files:** `SqlParseSymbolTreeHelper.java` (`convertSymbolTableToTableDictionary`, interface loop ~L1627–1674); `SqlASTWalkerHelper.java` (`moveUnknownEntriesToSingleWildcardBackedNonTableSource` — delete when zero call sites after C2).

### Step C2 — Retire late scope-exit bulk paths (after C1 green)

**Objective:** Remove late bulk helpers at scope finalize. Replace with per-key unified resolver + materializer (or prove convert leaves zero local unqualified for non-deferred scopes).

#### C2a — Late physical scope-exit drain ✅ DONE (`cd937c2`)

Removed `materializeRemainingSingleTableUnqualifiedAtScopeExit` from `finalizeQueryScopeSymbolTable` and `finalizeUpdateScopeUnresolvedColumnsAtExit`. Replaced with:

- **`materializeUnqualifiedLineageForSingleSourceScopeAtConvertExit`** (~L2006) — per-key lineage merge into sole `FROM` source at convert exit without consuming the unresolved map
- **`consumeLocallyResolvedUnqualifiedBeforeScopePassUp`** (~L4173, ~L4186) — per-key resolve + materialize + remove on correlated pass-up and INSERT/UPDATE nested defer egress only (not top-level finalize)

| Sub-step | Action | Verify |
|----------|--------|--------|
| **C2a.0** | Optional assert: `unqualifiedUnresolvedForLocal` empty for top-level single-table SELECT after convert | Skipped — covered by gate |
| **C2a.1** | Replace late drain with convert-time lineage + pass-up consumption | Gate **195/195** |
| **C2a.2** | Audit S6 + S7 — pass-up still works | `coverageDrivenSubqueryUnresolvedQualifierPassUpToParentTest`, `largeStudentgeneralQueryParseTest`, `simpleTfCallFlattenSplitV5Test` |
| **C2a.3** | Delete `materializeRemainingSingleTableUnqualifiedAtScopeExit` when unused | Grep clean |

**Files:** `SqlParseSymbolTreeHelper.java` (`convertSymbolTableToTableDictionary` ~L2006, `finalizeQueryScopeSymbolTable` pass-up ~L4173–4186).

#### C2b — Late wildcard scope-exit suppressor ✅ DONE (`1d20503`)

Removed `canResolveUnqualifiedFromSingleWildcardQuerySource` guard in `finalizeQueryScopeSymbolTable` (~L4094–4097); finalize now calls `emitUnqualifiedUnresolvedColumnsError` directly on remaining unqualified keys. Deleted method body (~L6378) and orphan `moveUnknownEntriesToSingleWildcardBackedNonTableSource` in `SqlASTWalkerHelper`.

| Sub-step | Action | Verify |
|----------|--------|--------|
| **C2b.0** | Confirm C1b left no unqualified keys in query-only wildcard scopes at convert exit | `nestedSelectStarV1`–`V7` |
| **C2b.1** | Remove `canResolveUnqualifiedFromSingleWildcardQuerySource` call | Gate **195/195** + `nestedSelectStarV1`–`V7` + full suite **1204/1204** |
| **C2b.2** | Deferred/nested scopes skip emit when `emitFinalUnresolvedUnknownFatal=false` | ✅ No regressions — skipped |
| **C2b.3** | Delete `canResolveUnqualifiedFromSingleWildcardQuerySource` + `moveUnknownEntriesToSingleWildcardBackedNonTableSource` | Grep clean + `extract_symbol_tree.py` allowlist |
| **C2b.4** | Failure playbook (if needed) | Not needed — all green |

**Files:** `SqlParseSymbolTreeHelper.java` (finalize ~L4094–4097); `SqlASTWalkerHelper.java` (deleted bulk helpers); `tools/extract_symbol_tree.py`.

#### C2 closeout ✅ DONE (`1d20503`)

| Sub-step | Action | Verify |
|----------|--------|--------|
| **C2.4** | Delete `moveEntriesToSingleTableIfSingleTarget` when zero call sites | Grep clean + gate + full suite |

**Step C complete** — all single-viable-source bulk relocation retired. Per-column pipeline (interface loop + `resolveRemaining…` + clause probe) is the sole ingress path.

### Step D — Backfill retirement audit ✅ DONE (Jul 2026)

**Historical context:** `backfillQueryDictionaryFromResolvedInterfaceSources` and `sweepBackfillQueryDictionaryFromResolvedInterfaceSources` were post-hoc repair passes that copied interface-resolved physical-column tokens into `query_dictionary` when walk-time capture missed them. They were **deleted** in `876c7ce` (“Separate query-dict capture from physical source lineage routing”) and replaced by native capture at validation time.

**Native replacements today (do not reintroduce backfill):**

| Former backfill role | Native replacement | Where |
|----------------------|-------------------|--------|
| Per-column backfill at `RESOLVED_PHYSICAL_SOURCE` | `mergeSourceLineageIntoPhysicalTableDictionary` — physical tokens go to **table dict only**; query dict is not mirrored | Interface loop `RESOLVED_PHYSICAL_SOURCE` branch (~L1789–1811) |
| Clause / output-alias refs on query dict | `recordInterfaceOutputClauseRefOnQueryDictionary` | `probeArchivedScopeClauseColumns` egress (~L9531) |
| Unqualified / query-source materialize | `materializeResolvedUnqualifiedReference` + `mergeExplicitQualifiedUnknownIntoSourceQueryDictionary` | Interface loop + `resolveRemainingUnresolvedAgainstQuerySources` |
| Final sweep after late materialization | **Removed** — Step C2 eliminated late bulk materialization; convert steps 6–8 must drain locally | N/A after C2 |

| Sub-step | Action | Verify | Result |
|----------|--------|--------|--------|
| **D.0** | Confirm zero `backfill*` / `sweepBackfill*` in `parse/src` | Grep clean | ✅ |
| **D.1** | Gate canaries: query-dict routing (8) + diagnostic routing (6) + UPDATE V13/V14 | Gate **195/195** | ✅ |
| **D.2** | Substitution column families — CTE external tokens in `query_dictionary` natively | `getSubstitutionColumnVariableV1`–`V16` | ✅ **16/16** |
| **D.3** | Complex substitution nested CTE bodies — no empty interface | INSERT I1–I10 + UPDATE U1–U10 | ✅ **20/20** |
| **D.4** | Spot nested-demo + unaliased-values V13/V14 | `nestedQueryDemoTest`, `unaliasedValuesPositionalAllOuterClausesV13Test`, `unaliasedValuesAliasOnlyAllOuterClausesV14Test` | ✅ |
| **D.5** | Doc hygiene | `table-and-query-dictionary-design.md` gap #4; worklist stale rows | ✅ |

**If failures:** Do **not** restore backfill — fix the native capture path (interface loop, clause probe, or `materializeResolvedUnqualifiedReference` routing) per symptom, same discipline as C2b.4.

### Step E — PIVOT/UNPIVOT domain fallbacks (existing backlog)

**Objective:** Retire public `resolveRelationalModifierDerivedColumnsFromUnresolvedMap` when unified ingress no longer needs a separate pre-diagnostics derived-key drain API.

**Gate:** `SqlEventWalkerPivotUnpivotTests` (**67**) + gate PIVOT/UNPIVOT smoke (3) + `SqlEventWalkerDmlUpdateInsertDeleteTruncateTests` (UPDATE RHS spot).

**Status:** ✅ **Step E complete** (Jul 2026) — E.4 deleted public strip helper; private `consumeRelationalModifierDerivedColumnUnknownsFromUnresolvedMap` retained at pre-diagnostics egress.

#### E.0 inventory (prep) ✅ DONE

| ID | Ingress / surface | Handler today | Test coverage |
|----|-------------------|---------------|---------------|
| **E0a** | Unified resolver `RESOLVED_DERIVED_COLUMN` | `isRelationalModifierDerivedColumnReference` (first in qualified tree) | `pivotSameQuery*` family + `pivotBasicMonthSalesV7Test` |
| **E0b** | Interface loop unqualified/qualified derived skip | `isPivotDerivedInterfaceOutputColumn` + derived guard | V7 + `pivotMonthlySalesLong*` derived tests |
| **E0c** | PIVOT aggregate/FOR **operand** columns on joined physical table | `resolvePivotOperandColumnsFromUnresolvedMap` (**keep** — not Step E) | `pivotBasicMonthSalesJoinV8Test`, JOIN patterns |
| **E0d** | UNPIVOT walk-time VALUE/FOR/IN strip | `resolveUnpivotGeneratedColumnsFromUnresolvedMap` in walker (**keep**) | UNPIVOT test matrix |
| **E0e** | Clause probe skip (WHERE / **JOIN ON** / GROUP BY / ORDER BY / HAVING / QUALIFY) | `probeArchivedScopeClauseColumns` → `validateArchivedClauseColumnRef` derived skip | `pivotTableJoinOnWithUnqualifiedJanSalesProbeTest` (IN-list alias); `pivotMonthlySalesLongJoinOnDerivedSumProbeTest` (derived); `pivotSameQuery*` per clause |
| **E0f** | PIVOT IN-identifier resolution | Dedicated fatal/warning paths | `pivotInIdentifier*` tests |
| **E0g** | UPDATE assignment RHS derived re-entry | `resolveUpdateRhsUnqualifiedAssignmentColumnsToTargetTable` + `UPDATE_ASSIGNMENT_RHS_CLAUSE_PROBE_KEY` | `pivotUpdateFromRhsUnqualifiedDerivedColumnReentryE0gTest` |

**E.0 findings:**

- PIVOT `derived_columns` keys are **`{inValue}_{aggregate}`** (e.g. `jan_sales_SUM`) — generator correct; bare IN-list names in SELECT are a **separate** Snowflake alias / physical-lineage path (11 tests annotated).
- E.2 removed pre-wildcard strip; E.3 removed post-UPDATE-RHS strip and **relocated** drain to pre-diagnostics egress (after effective alias maps, before `emitExplicitQualifiedUnknownDiagnostics`). Naive removal without relocation broke **36/67** pivot tests (derived keys materialized into physical table dictionaries).
- E.4 deleted public `resolveRelationalModifierDerivedColumnsFromUnresolvedMap`; pre-diagnostics drain is private `consumeRelationalModifierDerivedColumnUnknownsFromUnresolvedMap` (**Phase 15.5** delete target).
- `resolveRemainingUnresolvedAgainstQuerySources` still passes `null` for relational-modifier hints — **Phase 15.3**.

**E.0 test additions (Jul 2026):**

- `pivotUpdateFromRhsUnqualifiedDerivedColumnReentryE0gTest` — UPDATE RHS `jan_sales_SUM`
- `pivotMonthlySalesLongJoinOnDerivedSumProbeTest` — JOIN ON derived
- `pivotMonthlySalesLongJoinFilterDerivedSumTest` — alias `u` + JOIN + WHERE + tax expr
- `pivotMonthlySalesLongTaxWhereDerivedSumTest` — SELECT expr + WHERE
- `pivotMonthlySalesLongOrderByExpressionDerivedSumProbeTest` — ORDER BY `jan_sales_SUM / feb_sales_SUM`

#### E.2–E.4 retirement substeps

| Sub-step | Action | Verify | Status |
|----------|--------|--------|--------|
| **E.1** | Re-confirm unified resolver + clause probe consume/remove derived keys at both strip sites | Pivot **67/67** + gate **195/195** | ✅ (prep) |
| **E.2** | Remove **pre-wildcard** `resolveRelationalModifierDerivedColumnsFromUnresolvedMap` call (~L1581) | Gate + pivot 67 + full suite | ✅ (Jul 2026) |
| **E.3** | Remove **post-UPDATE-RHS** call; relocate drain to pre-diagnostics egress (~L1620) | E0g + pivot 67 + full suite | ✅ (Jul 2026) |
| **E.4** | Delete public `resolveRelationalModifierDerivedColumnsFromUnresolvedMap`; privatize as `consumeRelationalModifierDerivedColumnUnknownsFromUnresolvedMap` | Grep clean for retired name | ✅ (Jul 2026) |
| **E.5** (optional) | Pass derived hints into `resolveRemainingUnresolvedAgainstQuerySources` | Only if E.4 exposes stragglers | ⏸️ **Folded into Phase 15.3** |

**Do not retire:** `resolvePivotOperandColumnsFromUnresolvedMap`, `resolveUnpivotGeneratedColumnsFromUnresolvedMap` (walk-time UNPIVOT hook).

### Step F — Remaining dead-code + helper hygiene ✅ DONE (Jul 2026)

- [x] `getTableFunctionSourceCount` / `setTableFunctionSourceCount` / `getSuppressedAmbiguousUnqualifiedKeys` / `getTableFunctionSourceRefs` — deleted (zero external callers; fields remain private on helper)
- [x] `ArchivedClauseColumnRefResult.satisfied()` — audit: method not present in current tree
- [x] `tools/extract_symbol_tree.py` allowlist synced after Step C2 deletions — **C2b.3** (`1d20503`); getter template removed Step F
- [x] Coverage report stale rows for `rehomeUpdate…` / `getSingleUpdateFromTableReference` pruned — **Step F hygiene** (Jul 2026); see `parse/documents/coverage/sql_walker_astwalkers_gap_report.md`

### Phase 14 closeout checklist

- [x] Early physical bulk priming removed (both relocate sites in convert) — **C1a** (`37da020`)
- [x] Early wildcard-query bulk bind removed (`moveUnknownEntriesToSingleWildcardBackedNonTableSource` call) — **C1b** (`1274ee3`)
- [x] Interface loop uses `visibleQuerySourceCollection` + query-source fallback for unqualified output columns — **C1b.1** (`1274ee3`)
- [x] Late physical scope-exit drain removed or replaced with per-key exit loop — **C2a** (`cd937c2`)
- [x] Late wildcard scope-exit suppressor removed (`canResolveUnqualifiedFromSingleWildcardQuerySource`) — **C2b** (`1d20503`)
- [x] `moveEntriesToSingleTableIfSingleTarget` deleted (zero call sites) — **C2.4** (`1d20503`)
- [x] `moveUnknownEntriesToSingleWildcardBackedNonTableSource` deleted (zero call sites) — **C2b.3** (`1d20503`)
- [x] Output-alias deferral preserved via `isInterfaceOutputAliasOnly` (bulk helper removed with C1a)
- [x] Correlated pass-up unchanged (no local bulk bind of outer refs) — verified during **C2a** (`coverageDrivenSubqueryUnresolvedQualifierPassUpToParentTest`, `largeStudentgeneralQueryParseTest`)
- [x] Smoketest gate **195/195** + full suite **1209/1209** (Jul 2026)
- [x] `table-and-query-dictionary-design.md` updated — backfill gap #4 resolved; bulk relocation retired — **Step D** (Jul 2026)
- [x] PIVOT derived-column strip: pre-wildcard call site removed — **E.2** (Jul 2026)
- [x] PIVOT derived-column strip: post-UPDATE-RHS site removed; drain relocated to pre-diagnostics egress — **E.3** (Jul 2026)
- [x] Public `resolveRelationalModifierDerivedColumnsFromUnresolvedMap` deleted; private convert drain retained — **E.4** (Jul 2026)
- [x] Step F dead-code hygiene — table-function getters removed — **F** (Jul 2026)
- [ ] Unified convert egress loop — derived batch drain deleted — **Phase 15**

### Phase 14 vs Phase 13 / 15 ordering

| Track | When | Notes |
|-------|------|-------|
| **Phase 14 C1a + C1b** | ✅ Done (Jul 2026) | `1274ee3` (C1b), `37da020` (C1a + prepend removal) |
| **Phase 14 C2a** | ✅ Done (Jul 2026) | `cd937c2` — convert-time lineage + pass-up consumption |
| **Phase 14 C2b + closeout** | ✅ Done (Jul 2026) | `1d20503` — wildcard suppressor + orphan bulk helper deletion |
| **Phase 14 Step D** | ✅ Done (Jul 2026) | Audit green — native capture verified; gap #4 doc updated |
| **Phase 14 Steps E + F** | ✅ Done (Jul 2026) | Step E derived strip; Step F getter hygiene |
| **Phase 14 Step F** | ✅ Done (Jul 2026) | Removed unused table-function getters |
| **Phase 15** | **NEXT** | Unified egress loop — see Phase 15 section |
| **Phase 13** | Parallel once gate green | Language features — independent unless touching convert |

---

## Phase 15 — Unified convert egress loop

**Goal:** Replace the current **parallel convert consumers** (batch derived drain + qualified diagnostics + interface loop + `resolveRemaining…` + clause probe) with **one per-key egress loop** over `unresolved_column`, where physical, query-backed, and PIVOT/UNPIVOT **derived** columns share a single resolution tree and consistent consume/materialize rules.

**Prerequisite:** Phase 14 Steps C–E complete (bulk relocation retired; derived shim at one choke point).

**Gate (every sub-step):**

```bash
cd parse
mvn -Dtest=SqlEventWalkerPivotUnpivotTests test          # 67 — derived-column regression net
mvn -Psmoketest-quality-gate test                        # 195
mvn test                                                  # 1209
```

**Supplementary canaries:** `pivotUpdateFromRhsUnqualifiedDerivedColumnReentryE0gTest`, `pivotMonthlySalesLong*` derived clause tests, `nestedQueryDemoTest`, `nestedQueryDemoWithCteTest`, DML UPDATE V13/V14.

### End state (do not lose sight of this)

At convert exit, after wildcard / PIVOT-operand / SELECT* / UPDATE-specific prep only:

1. **One egress loop** over `unresolved_column`.
2. **Each key** calls a shared resolver (qualified + unqualified share derived-first logic):
   - `resolveColumnAgainstVisibleScope(…, derivedColumns, relationalModifierInterfaceHints)`
3. **Outcomes:**
   - `RESOLVED_DERIVED_COLUMN` → remove from map; **do not** materialize into `table_dictionary` / physical lineage
   - `RESOLVED_PHYSICAL` / `RESOLVED_QUERY` → materialize lineage; remove from map
   - `DEFERRED` → keep in map (correlated / pass-up)
   - `UNRESOLVED` → fatal or pass-up per policy
4. **Interface loop** and **clause probe** become **callers** of the shared per-key resolver (or re-walk archived lists **only** for diagnostics/output binding) — not parallel pipelines with different consume rules.
5. **Delete** `consumeRelationalModifierDerivedColumnUnknownsFromUnresolvedMap` entirely (no batch derived strip).
6. **Build** `ConvertEgressScopeBundle` at convert exit (Phase **15.6**): pre-resolved `def_*` payloads, visible query-source handles, and local/global query-dictionary refs so egress readers do not repeat definition scope chain walks.

### Where we are today (gap audit)

Phase 14 retired bulk **physical** and **wildcard-query** relocation. Derived columns are only **half-integrated**:

| Consumer | Derived-aware today? | Consumes from `unresolved_column`? | Phase 15 fix |
|----------|---------------------|--------------------------------------|--------------|
| `resolveQualifiedColumnAgainstVisibleScope` | ✅ `RESOLVED_DERIVED_COLUMN` first | Per-ref when called | Becomes core of shared loop |
| `resolveUnqualifiedColumnAgainstVisibleScope` | ❌ no `derived_columns` check | N/A | **15.1** |
| `consumeRelationalModifierDerivedColumnUnknownsFromUnresolvedMap` | ✅ batch negative filter | ✅ removes all derived keys | **Delete in 15.5** |
| `emitExplicitQualifiedUnknownDiagnostics` | partial | can mis-handle derived stragglers | Fed by loop / diagnostics-only in **15.4** |
| Interface loop | partial (output-alias skip; qualified path) | per output column | Route through shared loop in **15.4** |
| `resolveRemainingUnresolvedAgainstQuerySources` | ❌ passes `null` hints | per unqualified key; can single-table bind | **15.3** |
| `validateArchivedClauseColumnRef` / clause probe | ✅ skips validation | ❌ rarely removes (UPDATE RHS only) | **15.2** |
| `materializeUnqualifiedLineageForSingleSourceScopeAtConvertExit` | ❌ | late merge can poison dict | Guard or fold into **15.4–15.5** |

**Root cause of the shim:** derived refs are captured into `unresolved_column` during the walk like ordinary columns, but several egress paths only understand physical + query namespaces. The batch drain runs **before** those paths so `jan_sales_SUM` is not mistaken for a physical column on the pivot source table.

### Derived-column rationalization matrix

Tracks every **convert-egress** handler for true PIVOT/UNPIVOT **derived registry** keys (`derived_columns`, e.g. `jan_sales_SUM`). **Out of scope for Phase 15** (separate phases): operand materialization → **Phase 16**; walk-time UNPIVOT strip + convert UNPIVOT shaping → **Phase 17**; IN-list output alias lineage + IN-identifier refs → **Phase 18**. See **Relational modifier column namespaces** below.

| Handler | Today | After **15.1–15.3** (bridge) | After **15.5** (end state) | Step |
|---------|-------|------------------------------|----------------------------|------|
| **Pre-diagnostics batch drain** (`consumeRelationalModifierDerivedColumnUnknownsFromUnresolvedMap`) | Bulk remove all derived keys before diagnostics/egress | Still present; redundant with per-consumer derived consume but kept as safety net | **Deleted** — unified loop consumes per key | **15.5** |
| **Unified qualified resolver** (`resolveQualifiedColumnAgainstVisibleScope` → `RESOLVED_DERIVED_COLUMN`) | Correct per-ref proof when called | Unchanged; still invoked from interface loop / clause paths | **Core of shared loop** — sole qualified derived proof | **15.4**, **15.5** |
| **Unqualified resolver** (`resolveUnqualifiedColumnAgainstVisibleScope`) | No `derived_columns` check; can single-table bind derived names | Derived check before physical/query bind; returns derived outcome without materialize | Same logic inside shared loop | **15.1**, **15.5** |
| **Interface loop** (`isPivotDerivedInterfaceOutputColumn` skip; qualified `RESOLVED_DERIVED_COLUMN` branch) | Ad-hoc skips + parallel qualified path | Derived-safe via 15.1 + qualified resolver; IN-list output deferred | **Calls shared loop**; registry skip branches **retired** in **15.4b**; IN-list output → **Phase 18** | **15.4**, **15.4b**, **18** |
| **UPDATE RHS hook** (`resolveUpdateRhsUnqualifiedAssignmentColumnsToTargetTable` derived skip/consume) | Dedicated derived branches + `consumeDerivedColumnUnknownEntry` | Still dedicated; correct but duplicate of unified rule | **Derived branches retired** — UPDATE RHS refs handled by shared loop | **15.4**, **15.4a**, **15.5** |
| **Clause probe** (`validateArchivedClauseColumnRef`) | Skips validation for derived; consumes only on UPDATE RHS clause key | Consumes derived on **all** clause keys | **Calls shared loop**; no separate derived skip tree | **15.2**, **15.4**, **15.5** |
| **`resolveRemainingUnresolvedAgainstQuerySources`** | Passes `null` hints; no derived check | Passes hints; derived → consume, no materialize | **Absorbed into unified loop** (or thin wrapper calling shared helper) | **15.3**, **15.5** |
| **`emitExplicitQualifiedUnknownDiagnostics`** | Can mis-handle derived stragglers in batch | Safer once 15.1–15.3 consume stragglers; shim still pre-filters | **Diagnostics only** — fed by loop outcomes, not a resolution pipeline | **15.4**, **15.5** |
| **`materializeUnqualifiedLineageForSingleSourceScopeAtConvertExit`** | Late merge; no derived guard | Should not see derived keys if 15.1–15.3 + shim work | **Derived keys never reach this pass** | **15.5** |

**Bridge vs end state:** **15.1–15.3** make each consumer derived-safe but **multiply** parallel handlers (six policies → six derived-safe policies + shim). **15.4–15.5** collapse to **one policy**: `RESOLVED_DERIVED_COLUMN` → consume, no materialize.

**Single rule at closeout:**

```
derived registry key in unresolved_column
  → RESOLVED_DERIVED_COLUMN → remove from map; do NOT materialize to table_dictionary
```

### Phase 15 substeps (execution order)

| Sub-step | Action | Primary files | Verify | Status |
|----------|--------|---------------|--------|--------|
| **15.0** | Gap audit, end-state doc, derived-column rationalization matrix (this section) | worklist | — | ✅ (Jul 2026) |
| **15.1** | **Unqualified derived-awareness:** before single-table / single-query bind in `resolveUnqualifiedColumnAgainstVisibleScope`, check `derived_columns` + hints; return new status or short-circuit so derived keys are not resolved to physical sources | `SqlParseSymbolTreeHelper.java` ~`resolveUnqualifiedColumnAgainstVisibleScope` | Pivot **67/67** + gate; no new materialization of derived keys onto source tables | ⏸️ **NEXT** |
| **15.2** | **Clause probe consume:** in `validateArchivedClauseColumnRef`, call `consumeDerivedColumnUnknownEntry` for **all** clause keys when `isRelationalModifierDerivedColumnReference` (not only `UPDATE_ASSIGNMENT_RHS_CLAUSE_PROBE_KEY`) | ~`validateArchivedClauseColumnRef` | `pivotMonthlySalesLong*` WHERE/JOIN ON/ORDER BY derived tests + gate | ⏸️ |
| **15.3** | **Hints through `resolveRemaining…`:** pass `localDerivedColumns` + `relationalModifierInterfaceHints`; on derived match → consume, do not materialize (folds retired **E.5**) | ~`resolveRemainingUnresolvedAgainstQuerySources` + call site ~L1950 | E0g + pivot **67/67** + gate | ⏸️ |
| **15.4** | **Shared per-key egress helper:** introduce `resolveUnresolvedColumnAtConvertEgress` (name TBD) wrapping qualified + unqualified unified resolver + materialize/consume; route clause-probe and interface-loop refs through it incrementally | `convertSymbolTableToTableDictionary`, resolver helpers | Gate + nested demo + DML V13/V14 | ⏸️ |
| **15.4a** | **Retire UPDATE RHS derived branches:** remove derived-specific skip/consume in `resolveUpdateRhsUnqualifiedAssignmentColumnsToTargetTable` once shared loop handles UPDATE assignment RHS refs (keep non-derived UPDATE RHS target-table / single-FROM fallback logic) | ~`resolveUpdateRhsUnqualifiedAssignmentColumnsToTargetTable` | `pivotUpdateFromRhsUnqualifiedDerivedColumnReentryE0gTest` + DML UPDATE spot + gate | ⏸️ |
| **15.4b** | **Retire interface-loop ad-hoc derived skips:** remove redundant qualified-derived branches where shared resolver covers true `derived_columns` keys; **defer** full IN-list output-alias model to **Phase 18** (keep minimal skip until `RESOLVED_PIVOT_IN_LIST_OUTPUT` exists) | Interface loop ~L1696+ | Pivot **67/67** + gate | ⏸️ |
| **15.5** | **Collapse + delete shim:** replace convert steps 5–9 with unified loop; delete `consumeRelationalModifierDerivedColumnUnknownsFromUnresolvedMap`; grep clean | `convertSymbolTableToTableDictionary` | Full suite **1209/1209**; rationalization matrix “end state” column all green | ⏸️ |
| **15.6** | **Convert-egress scope bundle:** at convert exit (after **15.5** unified loop), build `ConvertEgressScopeBundle` with frozen maps: `visibleDefinitionPayloads` (`def_*` → published scope map), `visibleQuerySourceRefs` (live ref → payload / dict handle), `localQueryDictionary` snapshot for current scope. Route egress-time readers (`getQuerySourcePayloadPreferDefinition`, interface loop, wildcard promotion, `hasColumnInQueryOutputInterface`) through the bundle instead of `resolveDefinitionSymbolInScopeChain`. Walk-time / mid-convert may still use scope chain until bundle is built. | `convertSymbolTableToTableDictionary`, resolver helpers | `nestedQueryDemoTest`, `nestedQueryDemoWithCteTest`, substitution V9–V16, gate **195/195** | ⏸️ |

**Recommended session order:** **15.1 → 15.2 → 15.3** (bridge — each consumer derived-safe) → **15.4 → 15.4a → 15.4b → 15.5** (collapse — shared loop, retire duplicate handlers, delete shim) → **15.6** (egress scope bundle — eliminate repeated definition scope chain walks).

**Rollback discipline:** same as Phase 14 — if a sub-step regresses pivot derived tests, fix the per-key path for that symptom; do **not** restore the public batch strip without explicit approval.

### Phase 15 closeout checklist

- [ ] Unqualified resolver checks derived before physical/query single-source bind — **15.1**
- [ ] Clause probe removes derived keys from `unresolved_column` on all clause surfaces — **15.2**
- [ ] `resolveRemainingUnresolvedAgainstQuerySources` passes derived hints and consumes derived keys — **15.3**
- [ ] Shared per-key convert egress helper exists; clause probe + interface loop route through it — **15.4**
- [ ] Derived-specific branches retired in `resolveUpdateRhsUnqualifiedAssignmentColumnsToTargetTable` — **15.4a**
- [ ] Redundant qualified-derived skips retired in interface loop; IN-list output alias fully modeled in **Phase 18** — **15.4b**
- [ ] `consumeRelationalModifierDerivedColumnUnknownsFromUnresolvedMap` deleted; convert steps 5–9 collapsed — **15.5**
- [ ] `ConvertEgressScopeBundle` built at convert exit; egress readers use bundle, not `resolveDefinitionSymbolInScopeChain` — **15.6**
- [ ] Rationalization matrix: all handlers show “end state” column (single `RESOLVED_DERIVED_COLUMN` consume rule)
- [ ] Target convert order diagram updated to Phase 15 “today = target”
- [ ] Smoketest gate **195/195** + pivot **67/67** + full suite **1209/1209**

### Convert-egress scope bundle (15.6 detail)

**Problem:** During `convertSymbolTableToTableDictionary`, multiple egress paths call `getQueryDefinitionSymbol` / `getQuerySourcePayloadPreferDefinition`, each triggering **`resolveDefinitionSymbolInScopeChain`** (walk current + ancestor `symbolTable_N` frames). That is correct strict-mode behavior but duplicates work and obscures visibility at the convert boundary.

**End state:** One bundle object built once per convert invocation (or per scope-finalize when convert runs):

| Bundle field | Contents | Replaces |
|--------------|----------|----------|
| `visibleDefinitionPayloads` | `def_queryN` / `def_unionN` / … → published scope `Map` | Repeated `resolveDefinitionSymbolInScopeChain` during egress |
| `visibleQuerySourceRefs` | Live `queryN` → payload ref or embedded dict handle | Ad-hoc `queryCollection.get(def_*)` + scope-chain fallback |
| `localQueryDictionary` | Current scope's output-token map at finalize | Re-reads of `localCurrentQueryDictionary` across parallel consumers |
| `globalQueryDictionaryRefs` | Live key → `queryColumnDictionaryMap` entry (read-only view) | Scattered `walker.queryColumnDictionaryMap.get` during egress (**Phase 19** consolidates writes) |

**Readers that migrate to bundle (15.6):**

- `getQuerySourcePayloadPreferDefinition` (when called from convert egress — bundle-first, scope chain fallback only pre-bundle)
- Interface loop qualified/unqualified proof against published interfaces
- `hasColumnInQueryOutputInterface` / `hasWildcardInQueryOutputInterface` / `promoteQualifiedWildcardIntoQuerySource`
- Shared per-key egress helper from **15.4** (accept bundle parameter)

**Not in 15.6 scope:** walk-time resolution before convert starts; `context_list` CTE downward resolution (Phase 11). Scope chain remains for non-convert readers until a later generalization.

**Prerequisite for Phase 19:** **15.6** stabilizes egress-time **reads**; Phase 19 consolidates egress-time **writes** to `queryColumnDictionaryMap` and `def_*.query_dictionary`.

---

## Relational modifier column namespaces (Phases 15–18 roadmap)

PIVOT/UNPIVOT touch **four distinct column namespaces**. Phase 15 rationalizes only the first. Phases 16–18 complete the picture.

| Namespace | Example | Symbol-table home | Resolution outcome (target) | Materialize to `table_dictionary`? | Phase |
|-----------|---------|-------------------|----------------------------|-------------------------------------|-------|
| **Derived registry** | `jan_sales_SUM` | `derived_columns` map + hint `RELATIONAL_MODIFIER_DERIVED_COLUMNS_KEY` | `RESOLVED_DERIVED_COLUMN` → consume | **No** | **15** |
| **PIVOT operands** | `col1`, `col2` (aggregate/FOR inputs on joined table) | `unresolved_column` → operand materialize | `RESOLVED_PIVOT_OPERAND` → materialize to **non-pivot** physical table | **Yes** (joined target) | **16** |
| **UNPIVOT synthetic** | VALUE col, FOR col; IN-list source cols | Hints `MUMBLE_VALUE_KEY` / `MUMBLE_FOR_KEY` / `MUMBLE_IN_KEY` | VALUE/FOR → consume; IN cols → materialize to source dict | IN cols **yes**; VALUE/FOR **no** | **17** |
| **PIVOT IN-list output** | `jan_sales` (bare IN value as SELECT output name) | Interface + `pivot_in_columns` hint; physical lineage to pivot source | `RESOLVED_PIVOT_IN_LIST_OUTPUT` (name TBD) → lineage on pivot source | **Yes** (pivot source table) | **18** |
| **PIVOT IN-identifier** | Identifier in `PIVOT (… FOR col IN (identifier))` | Walker `pivot_in_identifier_references` | Parse/walk proof + fatal/warning (not `unresolved_column` egress) | N/A | **18** |

**Execution order:** **15 → 16 → 17 → 18** (15 establishes unified egress loop; 16–18 add namespace-specific outcomes to the same loop or to deterministic convert-prep hooks). Phase 13 can overlap once gate stays green.

---

## Phase 16 — PIVOT operand materialization rationalization

**Goal:** Replace the **triple-call** `resolvePivotOperandColumnsFromUnresolvedMap` convert prep (before SELECT*, after SELECT*, after UPDATE RHS) with a **single, deterministic** operand materialization policy integrated into convert prep or the Phase 15 unified egress loop.

**Problem today:**

- PIVOT aggregate/FOR **operand** columns (`col1`, `month` on a joined physical table) are real physical columns, not derived registry keys.
- `resolvePivotOperandColumnsFromUnresolvedMap` (~L525) consumes operand keys from `unresolved_column` and **materializes lineage** onto the **non-pivot** physical table (typically the JOIN partner in `PIVOT … JOIN` patterns).
- Called **three times** in `convertSymbolTableToTableDictionary` (~L1580, ~L1592, ~L1616) because wildcard expansion and UPDATE RHS can reintroduce operand unknowns between passes.
- Orthogonal to Phase 15: Phase 15 **consumes** derived keys without materializing; Phase 16 **materializes** physical operands to the correct table.

**Prerequisite:** Phase 15 closeout (unified egress loop exists — operands can become a materialize branch).

**Gate:** `SqlEventWalkerPivotUnpivotTests` **67/67** — especially `pivotBasicMonthSalesJoinV8Test`, JOIN + operand patterns; gate **195/195**; full suite **1209/1209**.

### Phase 16 inventory

| Handler | When | What it does | Issue |
|---------|------|--------------|-------|
| `resolvePivotOperandColumnsFromUnresolvedMap` | Convert ×3 | Consumes operand cols from `unresolved_column`; merges into `materializeTableRef` via `mergeSourceLineageIntoPhysicalTableDictionary` | Triple timing; not in unified resolver |
| `collectPivotOperandColumnNames` / `consumePivotOperandUnresolvedEntry` | Operand helper internals | Identifies aggregate/FOR operand names from hints | Keep logic; relocate call site |
| `resolvePivotOperandMaterializationTableRef` | Operand helper | Picks non-pivot physical table in multi-table FROM | Core policy — preserve |

### Phase 16 end state

1. **One** operand materialization pass at a fixed point in convert (after wildcard + UPDATE prep, before or **inside** unified egress loop).
2. New resolution outcome: **`RESOLVED_PIVOT_OPERAND`** (or materialize branch in shared helper) — bind operand to `materializeTableRef`, merge lineage, consume from `unresolved_column`.
3. **Delete** duplicate pre/post-SELECT* / post-UPDATE operand calls once single pass is proven sufficient (or loop re-invokes operand check only when map mutates).

### Phase 16 substeps

| Sub-step | Action | Verify | Status |
|----------|--------|--------|--------|
| **16.0** | Inventory operand keys in `unresolved_column` across pivot JOIN tests; document why ×3 calls were needed (wildcard / UPDATE RHS re-entry) | Test notes in worklist | ⏸️ |
| **16.1** | Add `RESOLVED_PIVOT_OPERAND` (or equivalent) to unified resolver / shared egress helper; operand materialize + consume | `pivotBasicMonthSalesJoinV8Test` + gate | ⏸️ |
| **16.2** | Collapse to **one** convert call site; prove wildcard + UPDATE RHS no longer require re-pass (or document minimal re-pass rule) | Pivot **67/67** + full suite | ⏸️ |
| **16.3** | Delete standalone triple-call pattern; grep clean | Gate + full suite | ⏸️ |

### Phase 16 closeout checklist

- [ ] Operand materialization runs once (or has documented single re-pass rule)
- [ ] `RESOLVED_PIVOT_OPERAND` (or shared materialize branch) in unified egress path
- [ ] Triple `resolvePivotOperandColumnsFromUnresolvedMap` calls retired
- [ ] Pivot **67/67** + gate **195/195** + full suite **1209/1209**

---

## Phase 17 — UNPIVOT synthetic column rationalization

**Goal:** Unify **walk-time** and **convert-time** UNPIVOT synthetic-column handling under one namespace policy — VALUE/FOR generated columns consumed, IN-list source columns materialized, query-backed source interface sweep — instead of parallel hooks at modifier exit and scattered convert passes.

**Problem today:**

| Layer | Handler | What it does |
|-------|---------|-------------|
| **Walk** (modifier exit) | `resolveUnpivotGeneratedColumnsFromUnresolvedMap` (`SqlParseEventWalker` ~L5059) | At UNPIVOT primary exit: strip VALUE/FOR from `unresolved_column`; materialize IN cols; sweep query-source interface cols from unresolved map |
| **Convert** | `applyUnpivotValueInterfaceDerivations` | Rewrites interface refs from VALUE column → IN-list physical refs |
| **Convert** | `applyUnpivotValueDerivationsToReferenceListObject` | **Stub** — returns `referenceListObject` unchanged (~L314) |
| **Convert** | `materializeSelectedUnpivotInColumnsIntoSourceDictionary` | Materializes selected UNPIVOT IN columns into source dictionary |
| **Convert** | `registerUnpivotGeneratedColumnAmbiguitySuppressions` | Suppresses ambiguity diagnostics for generated VALUE column |
| **Convert** | `resolveUnqualifiedColumnAgainstVisibleScope` → `resolveUnpivotGeneratedColumnSourceRef` | Ambiguity disambiguation for UNPIVOT VALUE on multi-source |

Walk-time strip and convert-time interface/dictionary shaping are **different pipelines** with overlapping semantics.

**Prerequisite:** Phase 15 closeout (shared egress loop can host UNPIVOT consume/materialize outcomes).

**Gate:** UNPIVOT tests in `SqlEventWalkerPivotUnpivotTests` + `unpivotV1Test` gate smoke; full pivot **67/67**; gate **195/195**.

### Phase 17 end state

1. **Namespace rules explicit:**
   - UNPIVOT **VALUE** / **FOR** → synthetic; consume from `unresolved_column`; no physical `table_dictionary` entry
   - UNPIVOT **IN** source columns → materialize lineage onto `dictionarySourceRef`
   - Query-backed UNPIVOT source → interface column sweep policy documented and single-owned
2. **Walk vs convert boundary clear:** either (a) all UNPIVOT resolution at modifier exit, with convert only shaping interface output, or (b) walk captures only, convert unified loop resolves — **pick one**; retire the other path
3. Implement `applyUnpivotValueDerivationsToReferenceListObject` for `filters` / `grouped_by` / `ordered_by` **or** route clause lists through shared resolver (replace stub)
4. New outcomes: `RESOLVED_UNPIVOT_VALUE`, `RESOLVED_UNPIVOT_FOR`, `RESOLVED_UNPIVOT_IN_SOURCE` (names TBD) in shared resolver

### Phase 17 substeps

| Sub-step | Action | Verify | Status |
|----------|--------|--------|--------|
| **17.0** | Inventory UNPIVOT handlers (table above); map each UNPIVOT test to namespace + handler | Worklist + test matrix | ⏸️ |
| **17.1** | Document chosen walk vs convert ownership; add UNPIVOT outcomes to shared resolver | UNPIVOT V1 + multi-source ambiguity tests | ⏸️ |
| **17.2** | Retire duplicate strip if walk + convert both handle VALUE/FOR (keep one path) | Pivot UNPIVOT subset + gate | ⏸️ |
| **17.3** | Implement or replace `applyUnpivotValueDerivationsToReferenceListObject` stub; clause lists use shared policy | UNPIVOT clause tests if any; gate | ⏸️ |
| **17.4** | Fold `materializeSelectedUnpivotInColumnsIntoSourceDictionary` + ambiguity suppression into unified materialize/consume rules | Full pivot **67/67** | ⏸️ |
| **17.5** | Grep clean for orphaned UNPIVOT-only convert hooks | Full suite **1209/1209** | ⏸️ |

### Phase 17 closeout checklist

- [ ] Walk vs convert UNPIVOT ownership documented and enforced (single owner per synthetic category)
- [ ] VALUE/FOR/IN namespace outcomes in shared resolver or documented convert-prep equivalent
- [ ] `applyUnpivotValueDerivationsToReferenceListObject` stub resolved (implement or delete)
- [ ] Pivot **67/67** + gate **195/195** + full suite **1209/1209**

---

## Phase 18 — PIVOT IN-list output alias and IN-identifier rationalization

**Goal:** Give **Snowflake-style PIVOT IN-list output names** (`jan_sales` in SELECT, not `jan_sales_SUM`) and **PIVOT IN-identifier references** first-class resolution semantics — folded into the Phase 15 unified loop — instead of ad-hoc `isPivotDerivedInterfaceOutputColumn` skips and separate walker diagnostic maps.

**Problem today — two related but distinct tracks:**

#### 18a — IN-list output alias lineage (bare IN value as column name)

- User selects pivot output using **IN-list value as name** (e.g. `jan_sales`) — **not** the derived registry key (`jan_sales_SUM`).
- Hint metadata: `pivot_in_columns` on relational-modifier hints (`SqlParseEventWalker` ~L4553).
- `isPivotDerivedInterfaceOutputColumn` (~L8022) checks `RELATIONAL_MODIFIER_DERIVED_COLUMNS_KEY` (registry names) — **misleading name**; may not fully model bare IN output aliases. Eleven pivot tests annotated as IN-list alias / physical-lineage tests.
- Interface loop skips physical resolution for names in derived-columns hint list; clause probe has separate UPDATE RHS path.
- **Target:** `RESOLVED_PIVOT_IN_LIST_OUTPUT` — materialize **physical lineage on pivot source table** (not consume like derived registry).

#### 18b — PIVOT IN-identifier references (FOR … IN (identifier))

- Walker map `pivot_in_identifier_references` + diagnostics `PIVOT_IN_IDENTIFIER_REFERENCE` / `PIVOT_IN_IDENTIFIER_UNRESOLVED` (`SqlParseEventWalker` ~L4996+, `pivotInIdentifier*` tests).
- Resolved at **walk time** from subquery/interface — **not** via `unresolved_column` convert egress.
- **Target:** keep walk-time proof but align metadata with hint model; ensure convert does not double-resolve or contradict walker fatals.

**Prerequisite:** Phase 15 closeout (**15.4b** may **defer** full IN-list alias model to Phase 18 — keep minimal skip until then).

**Gate:** 11 IN-list alias annotated tests + `pivotInIdentifier*` family + `pivotTableJoinOnWithUnqualifiedJanSalesProbeTest`; pivot **67/67**; gate **195/195**.

### Phase 18 end state

1. **Separate metadata** on hints: `pivot_in_columns` (output alias names) vs `RELATIONAL_MODIFIER_DERIVED_COLUMNS_KEY` (registry `{inValue}_{aggregate}` keys) — no conflation in helper method names.
2. **Unified resolver outcome** `RESOLVED_PIVOT_IN_LIST_OUTPUT`: qualified/unqualified refs to bare IN value → lineage on pivot `sourceRef` physical table; consume from `unresolved_column`.
3. **Retire** `isPivotDerivedInterfaceOutputColumn` (rename/replace with explicit IN-list output check).
4. **IN-identifier:** walker proof remains at parse event; convert egress **does not** re-handle; document handoff from `pivot_in_identifier_references` to published scope.
5. **`applyPivotValueInterfaceDerivations`** legacy path (`pivot_aggregate_columns` / `pivot_in_columns` fallback ~L981) audited — retire or align with walker hint population.

### Phase 18 substeps

| Sub-step | Action | Verify | Status |
|----------|--------|--------|--------|
| **18.0** | Classify 11 IN-list alias tests vs `pivotMonthlySalesLong*` derived tests; document expected lineage (physical pivot source vs registry) | Test matrix in worklist | ⏸️ |
| **18.1** | Add `RESOLVED_PIVOT_IN_LIST_OUTPUT` to shared resolver using `pivot_in_columns` + pivot source ref | IN-list alias annotated tests | ⏸️ |
| **18.2** | Replace `isPivotDerivedInterfaceOutputColumn` with explicit helper; retire misleading name | Interface loop + clause probe + UPDATE RHS | ⏸️ |
| **18.3** | Audit `applyPivotValueInterfaceDerivations` dual paths (registry vs aggregate/in fallback); single walker→convert hint contract | Pivot interface goldens | ⏸️ |
| **18.4** | Document IN-identifier walk-time contract; assert convert does not regress `pivotInIdentifier*` diagnostics | `pivotInIdentifier*` tests + gate smoke | ⏸️ |
| **18.5** | Fold IN-list output into unified egress loop; grep clean for ad-hoc skips | Pivot **67/67** + full suite | ⏸️ |

### Phase 18 closeout checklist

- [ ] `pivot_in_columns` vs derived registry keys are distinct in hints and resolver
- [ ] `RESOLVED_PIVOT_IN_LIST_OUTPUT` in shared egress loop
- [ ] `isPivotDerivedInterfaceOutputColumn` retired or renamed with correct semantics
- [ ] IN-identifier walk-time vs convert boundary documented
- [ ] `applyPivotValueInterfaceDerivations` legacy fallback path resolved
- [ ] Pivot **67/67** + gate **195/195** + full suite **1209/1209**

---

## Phase 19 — Query dictionary publish path consolidation

**Goal:** Replace scattered **ingress** paths that write `query_dictionary` / global `queryColumnDictionaryMap` with a **single publish policy**, and retire or narrow end-of-walk **`syncPublishedScopeQueryDictionariesFromGlobal`** repair. The intentional **two-store model** (`def_queryN.query_dictionary` immutable snapshot + global live index) **remains by design** — Phase 19 eliminates **redundant write sites**, not the architectural split documented in `table-and-query-dictionary-design.md`.

**Prerequisite:** Phase **15.6** closeout (`ConvertEgressScopeBundle` stabilizes egress-time reads). Phases **16–18** may overlap if they do not touch publish finalizers.

**Gate:** Gate **195/195**; substitution column V1–V16; `nestedQueryDemoTest` + `nestedQueryDemoWithCteTest`; DML UPDATE V13/V14; full suite **1209/1209**.

### Problem today — write-path spread

Step D closed **backfill repair** (post-hoc sweeps). These **ingress** paths still duplicate publish logic:

| Ingress site | What it writes | Issue |
|--------------|----------------|-------|
| `finalizeQueryScopeSymbolTable` → `mergeIntoGlobalQueryColumnDictionary` | Global `queryColumnDictionaryMap[queryN]` from local dict | Parallel to `publishQueryLikeScope` embedded snapshot |
| `publishQueryLikeScope` | `def_queryN.query_dictionary` + global merge | Correct publish point; not all scopes route here uniformly |
| `finalizeInsertScopeSymbolTable` / INSERT path | Global merge for insert-source scope | Duplicate merge policy |
| `finalizeUpdateScopeSymbolTable` | Global merge | Duplicate merge policy |
| `finalizeDeleteScopeSymbolTable` | Global merge | Duplicate merge policy |
| `SqlParseEventWalker` end-of-walk | `syncPublishedScopeQueryDictionariesFromGlobal` | **Repair sync** global → `def_*` when handoff drifted |

**Symptom:** global map and embedded `def_*.query_dictionary` can diverge **legitimately** (phase-2 parent attribution) but also **accidentally** when ingress order or merge sanitization differs between finalizers.

### Phase 19 end state

1. **Single API** — `publishQueryDictionary(PublishContext)` (name TBD) owns:
   - sanitize local dict (`sanitizeQueryDictionaryForGlobalExport`)
   - write embedded `def_*.query_dictionary` at publish
   - merge into global `queryColumnDictionaryMap`
   - record publish phase (phase-1 origins vs phase-2 external usage) for diagnostics
2. **All scope finalizers** call the same API — SELECT, VALUES, UNION/INTERSECT, INSERT, UPDATE, DELETE.
3. **`syncPublishedScopeQueryDictionariesFromGlobal`** retired or reduced to a **debug/assert** path only (no production repair at walk end).
4. **`ConvertEgressScopeBundle`** (15.6) receives read-only global dict handles from publish API — no ad-hoc map reads during egress.
5. **Two-store contract** documented: global may be richer than `def_*` snapshot after parent phase-2; that is not a sync bug.

### Phase 19 substeps

| Sub-step | Action | Primary files | Verify | Status |
|----------|--------|---------------|--------|--------|
| **19.0** | Ingress inventory: grep `mergeIntoGlobalQueryColumnDictionary`, `query_dictionary.put`, `syncPublishedScopeQueryDictionariesFromGlobal`; document per-finalizer ordering | worklist + helper | Matrix in this section | ⏸️ |
| **19.1** | Introduce `publishQueryDictionary` / `PublishContext` with sanitize + embedded + global merge | `SqlParseSymbolTreeHelper.java` | Gate; no golden churn | ⏸️ |
| **19.2** | Route `finalizeQueryScopeSymbolTable` + `publishQueryLikeScope` through **19.1** | Finalizers | nested demo + substitution V1–V16 | ⏸️ |
| **19.3** | Route INSERT / UPDATE / DELETE finalizers through **19.1** | DML finalizers | DML V13/V14 + complex sub I/U | ⏸️ |
| **19.4** | Audit `syncPublishedScopeQueryDictionariesFromGlobal` call sites in `SqlParseEventWalker`; retire or guard behind assert | `SqlParseEventWalker.java` | Full suite; confirm no handoff drift | ⏸️ |
| **19.5** | Wire `ConvertEgressScopeBundle.globalQueryDictionaryRefs` to publish API outputs; grep clean for stray egress `mergeIntoGlobal…` | Helper + convert | Gate **195/195** + full suite | ⏸️ |

### Phase 19 closeout checklist

- [ ] Single `publishQueryDictionary` API; all finalizers use it — **19.1–19.3**
- [ ] Ingress inventory complete; no direct `mergeIntoGlobalQueryColumnDictionary` outside publish API — **19.0**, **19.5**
- [ ] `syncPublishedScopeQueryDictionariesFromGlobal` retired or assert-only — **19.4**
- [ ] `table-and-query-dictionary-design.md` cross-ref: two-store intentional; publish API is sole write ingress
- [ ] Gate **195/195** + full suite **1209/1209**

### Phase 15 vs Phase 13 / 16–19

| Track | Overlap risk | Notes |
|-------|--------------|-------|
| **Phase 15** | Touches `convertSymbolTableToTableDictionary` | **Do first** — unified egress loop for derived registry keys |
| **Phase 15.6** | Same convert file | After **15.5** — `ConvertEgressScopeBundle`; prerequisite for **19** |
| **Phase 16** | Same convert file | After 15 — operand materialize branch |
| **Phase 17** | Walker modifier exit + convert | After 15 — UNPIVOT walk/convert boundary |
| **Phase 18** | Walker + convert + interface derivations | After 15 — IN-list output + IN-identifier; completes 15.4b deferrals |
| **Phase 19** | Finalizers + walker end sync | After **15.6** — single query-dict publish ingress |
| **Phase 13** | Low unless feature work edits convert | ~~EXCEPT (13.1)~~ ✅; RETURNING, forward-alias (13.4) — prefer **15.1–15.3** before 13.4 clause work |

---

## Phase 13 — Language feature gap closure (after consolidation Phases 9–12)

**Goal:** Complete partial language-feature implementations that already parse in `SQLSelectParser.g4` but lack full walker semantics, AST shape, or test proof. **Phases 9–12 test closeout is complete (1203/1203); Phase 13 is unblocked.**

**Prerequisite gate (unchanged):**

```bash
cd parse
mvn -Psmoketest-quality-gate test
```

### Phase 13 inventory

| # | Gap | Grammar / walker today | Target end state | Primary test class |
|---|-----|------------------------|------------------|-------------------|
| 13.1 | **EXCEPT set-operation parity** | ✅ **Complete (Jul 2026)** | `finalizeSetOperationScopeSymbolTable` handles EXCEPT on `union_operator` rail; per-participant `setop`; operator-aware column-count diagnostics; **157** EXCEPT clone tests + **12** three-level nesting tests; gate canaries | `SqlEventWalkerSubqueriesAndClauseSemanticsTests` |
| 13.2 | **Postgres INSERT** | ✅ Done | ON CONFLICT / DEFAULT VALUES / RETURNING; 4 tests in `SqlEventWalkerDmlUpdateInsertDeleteTruncateTests` |
| 13.3 | **UPDATE RETURNING** | ✅ **Complete (Jul 2026)** | `exitReturning` + `RETURNING select_list`; `finalizeUpdateScopeSymbolTable` publishes returning interface/query_dictionary | `SqlEventWalkerDmlUpdateInsertDeleteTruncateTests` |
| 13.4 | **Intra–select-list forward output-column resolution** | Unqualified refs in a later select-item expression (window `PARTITION BY`, nested formula, etc.) are collected as unresolved even when an earlier item already registered the name on the query interface | At ingress (`collectUnresolvedColumnReference`), while walking `select_list`, skip unresolved for unqualified names matching an **earlier** interface key; merge usage tokens onto `query_dictionary` | `SqlEventWalkerLiveSampleQueriesTests`, `SqlEventWalkerFunctionsAggregatesWindowingTests` |
| 13.5 | **DDL option detail parsing** | `generic_ddl_options` / `generic_ddl_paren_content` capture opaque token blobs | *Optional:* parse high-value clauses (e.g. `IF NOT EXISTS`, `OR REPLACE`, `CLUSTER BY`) without full dialect coverage | `SqlEventWalkerScriptsAndDDLTests` |
| 13.6 | **SQL statement generator** | `SQLStatementGenerator` partial; not production-ready | Round-trip SQL regeneration for all `SQLParserEndPoints` keys from AST + substitution map | New or extended generator test class |

### 13.1 — EXCEPT set-operation parity ✅ COMPLETE (Jul 2026)

**Grammar / scope-key decision (2026-07):** Keep `EXCEPT` on the `unionized_query` rail (`union_operator`); do **not** split a separate `excepted_query` tier (would break INTERSECT > UNION/EXCEPT precedence). Keep composite scope keys as `def_unionN` / `unionN` (grammar-rail name, not operator name). Per-participant `setop` on published `def_queryN` / nested `def_unionN` payloads records which operator introduced that branch.

#### 13.1.0 — Per-participant `setop` + operator-aware diagnostics (pre-requisite)

**Work:**

- [x] **13.1.0a** — Add `setop` mumble key (`UNION` | `EXCEPT` | `INTERSECTION`); stage pending operator on set-op frame at `exitUnion_clause` / `exitIntersect_clause`; consume and stamp on next `publishQueryLikeScope` participant (anchor branch omits `setop`).
- [x] **13.1.0b** — `validateSingleSetOperationInterface`: read mismatch label from mismatching participant's `setop`, fallback to parent scope key (`union` → `UNION`, `intersect` → `INTERSECTION`).
- [x] **13.1.0c** — Refresh mismatch-test goldens (`unionWithMismatchColumnCounts…`, `intersectionWithMismatchColumnCounts…`, `SqlParseEventWalkerWithAccessObjectTest` set-op interface tests); add `exceptColumnCountMismatchEmitsFatalTest`.
- [x] **13.1.0d** — Full `mvn test` green before EXCEPT clone matrix (13.1.1+).

**Tests (13.1.0):**

| Method | Class | Proves |
|--------|-------|--------|
| `unionWithMismatchColumnCountsAndNamesTest` | `SqlEventWalkerSubqueriesAndClauseSemanticsTests` | **Existing** — `def_query1` gains `setop=UNION`; diagnostic unchanged |
| `intersectionWithMismatchColumnCountsAndNamesTest` | same | **Existing** — `def_query1` gains `setop=INTERSECTION` |
| `exceptColumnCountMismatchEmitsFatalTest` | same | **New** — `setop=EXCEPT`; diagnostic says `EXCEPT` |
| Set-op interface validation V1–V5 | `SqlParseEventWalkerWithAccessObjectTest` | **Existing** — non-anchor participants gain `setop` in symbol-table goldens |

#### 13.1.1 — EXCEPT clone matrix (UNION → EXCEPT clones)

**Status:** UNION + INTERSECT → EXCEPT clone matrices **green**. **87** UNION-derived + **70** INTERSECT-derived EXCEPT-variant `@Test` methods (goldens refreshed; one-off clone scripts removed after landing).

**Work:**

- [x] Clone every UNION test to an EXCEPT variant placed next to the original (per-file; one-off script removed after landing).
- [x] Refresh goldens for all EXCEPT clones (AST `operator=except`, symbol-tree `setop=EXCEPT` on non-anchor participants; token offsets adjusted after `union` → `except`).
- [x] Clone INTERSECT tests to EXCEPT variants (13.1.1b — **70** clones; skipped queries with both INTERSECT and EXCEPT; goldens refreshed; one-off scripts removed after landing).
- [x] Add three-level UNION/INTERSECT/EXCEPT nesting suite (**12** tests: 6 happy-path permutations + 6 column-count mismatch permutations with triple diagnostics and `setop=` stamping).
- [x] Gate candidacy for representative EXCEPT / set-op clones.

**Gate candidacy (after green):** `exceptColumnCountMismatchEmitsFatalTest`, `intersectionWithMismatchColumnCountsAndNamesTest`, `threeLevelSetOpNestUnionIntersectExceptColumnCountMismatchTest`, `threeLevelSetOpNestUnionIntersectExceptHappyPathTest`, `threeLevelSetOpNestExceptUnionIntersectHappyPathTest`, and `unaliasedDerivedExceptAllOuterClausesV10Test` in `SmoketestQualityGateTestSuite`.

**Dev tooling:** one-off UNION/INTERSECT → EXCEPT clone + golden-refresh scripts removed after matrices landed (no `parse/tools/` helpers retained).

#### 13.1.1+ — Three-level nesting ✅ (folded into 13.1.1)

Delivered as **12** tests in `SqlEventWalkerSubqueriesAndClauseSemanticsTests`: 6 happy-path operator permutations + 6 triple-mismatch permutations with `assertDiagnosticAtPosition` and `setop=` stamping. Representative happy + mismatch cases are in `SmoketestQualityGateTestSuite` (200 tests).

**Optional coverage (not required for 13.1 sign-off):**

| Item | Status | Notes |
|------|--------|-------|
| Remaining 4/6 three-level happy-path permutations | ✅ full suite | Only 2 permutations promoted to gate; others covered by clone matrix |
| Remaining 5/6 three-level mismatch permutations | ✅ full suite | One permutation (`UnionIntersectExcept`) in gate |
| `unaliasedDerivedExceptAllOuterClausesV9Test` (derived-table EXCEPT column mismatch) | ✅ full suite | V10 happy path in gate; V9 mismatch optional gate candidate |

### 13.2 — Postgres INSERT ✅ COMPLETE (Jul 2026)

**Work:**

- [x] Complete `postgres_insert` grammar (ON CONFLICT, DEFAULT VALUES, RETURNING) per Postgres comment block in `SQLSelectParser.g4`.
- [x] Add `exitPostgres_insert`, `exitOn_conflict_clause`, `exitConflict_target`, `exitConflict_action`, and INSERT finalizer parity with UPDATE RETURNING (staging target interface, merge RETURNING keys `putIfAbsent`).
- [x] Publish `def_insertN` symbol-table shape consistent with Snowflake INSERT paths; treat `insertN` as a CTE/query source for `WITH … INSERT` bodies.

**Tests delivered:**

| Method | Class | Proves |
|--------|-------|--------|
| `postgresInsertReturningSelectListInterfaceTest` | `SqlEventWalkerDmlUpdateInsertDeleteTruncateTests` | **Delivered** — `INSERT … RETURNING col1, col2 AS alias` merged interface (target + RETURNING) |
| `postgresInsertOnConflictDoNothingTest` | `SqlEventWalkerDmlUpdateInsertDeleteTruncateTests` | **Delivered** — ON CONFLICT `(emp_id)` DO NOTHING; target columns cataloged |
| `postgresInsertOnConflictDoNothingWithoutTargetTest` | `SqlEventWalkerDmlUpdateInsertDeleteTruncateTests` | **Delivered** — ON CONFLICT DO NOTHING (no conflict target) |
| `postgresInsertOnConflictDoUpdateTest` | `SqlEventWalkerDmlUpdateInsertDeleteTruncateTests` | **Delivered** — ON CONFLICT DO UPDATE SET; nested `def_updateN` under `def_insertN` |
| `postgresInsertOnConflictDoUpdateWithWhereTest` | `SqlEventWalkerDmlUpdateInsertDeleteTruncateTests` | **Delivered** — ON CONFLICT DO UPDATE SET … WHERE; nested update scope with `filters` |
| `postgresInsertDefaultValuesTest` | `SqlEventWalkerDmlUpdateInsertDeleteTruncateTests` | **Delivered** — `INSERT … DEFAULT VALUES` baseline |
| `postgresInsertWithCteBodyTest` | `SqlEventWalkerDmlUpdateInsertDeleteTruncateTests` | **Delivered** — `WITH … INSERT … RETURNING` CTE body; outer SELECT resolves via `insertN` |

### 13.3 — UPDATE RETURNING ✅ COMPLETE (Jul 2026)

**Work:**

- [x] Implement `exitReturning` in `SqlParseEventWalker.java` (mirrors `exitDelete_returning`).
- [x] Extend `returning` grammar to `RETURNING select_list` (supports `*`, qualified columns, aliases).
- [x] Extend `finalizeUpdateScopeSymbolTable` to publish RETURNING `interface` + `query_dictionary` on `def_updateN`, **merging** RETURNING-only keys into the assignment-seeded update-scope `interface` (no separate RETURNING sub-map).

**Tests delivered:**

| Method | Class | Proves |
|--------|-------|--------|
| `updateReturningStarInterfaceTest` | `SqlEventWalkerDmlUpdateInsertDeleteTruncateTests` | **Delivered** — `RETURNING *`; merged SET+RETURNING interface (`score`, `*`) |
| `updateReturningQualifiedColumnsTest` | `SqlEventWalkerDmlUpdateInsertDeleteTruncateTests` | **Delivered** — `RETURNING t.col AS alias`; merged interface includes SET target + alias |
| `updateReturningWithFromSubqueryTest` | `SqlEventWalkerDmlUpdateInsertDeleteTruncateTests` | **Delivered** — UPDATE FROM + RETURNING; merged interface (`score`, `rn`, `emp_id`) |
| `updateReturningPredicandSubstitutionTest` | `SqlEventWalkerDmlUpdateInsertDeleteTruncateTests` | **Delivered** — RETURNING predicand substitution on merged update interface |

### 13.4 — Intra–select-list forward output-column resolution

**Problem (precise):** While building a `query_specification` select list, each `exitSelect_item` registers an output name on `MUMBLE_INTERFACE_KEY` (implicit column name, explicit `AS` alias, or generated `unnamed_N`) and its defining token on `query_dictionary`. Later items in the **same** select list can reference those names in any subsequent predicand (window `PARTITION BY` / `ORDER BY`, `CASE`, function args, etc.). Ingress still routes those refs through `collectUnresolvedColumnReference`, so they land in `MUMBLE_UNRESOLVED_COLUMN_KEY` and fail at finalize as if they were external — even though they are self-references to the current query's in-progress interface.

**Note:** Query-level `GROUP BY` / `ORDER BY` / `WHERE` forward-alias binding already has archived-clause egress (`probeArchivedScopeClauseColumns` + `isIntraQueryOutputClauseUsage`). This gap is specifically **inside the select list** during the sequential walk.

**Work:**

- [x] In `collectUnresolvedColumnReference`, when `RULE_select_list` is active and the ref is unqualified, if the name matches a **true output alias** already on `MUMBLE_INTERFACE_KEY` (`isInterfaceOutputAliasOnly` — explicit `AS` alias or expression output whose name differs from its source column; pass-through names like `SELECT a, … a+b` still route through unresolved for `table_dictionary` lineage), treat as resolved: **do not** enqueue unresolved; merge the usage token onto `query_dictionary` for that interface column (case-folding aligned with existing alias map / quoted-identifier rules).
- [x] Do **not** backpatch finalized child `def_queryN` payloads; resolution stays in the owning select scope.
- [x] Refresh goldens + flip donor-email test expectations after review.

**Tests (delivered Jul 2026):**

| Method | Class | Proves |
|--------|-------|--------|
| `donorEmailWithInvalidFatalErrorOnQualifiedColumnVariableTest` | `SqlEventWalkerLiveSampleQueriesTests` | Production donor-email query; `PARTITION BY … source_partner_system_name` binds to earlier same-list alias |
| `selfReferenceColumnAliasInSameSelectListHappyPathV1Test` | `SqlEventWalkerCoreSelectFromAliasingTests` | `a+b AS x, x*a AS y, y/b AS z` — forward alias chain; no diagnostics |
| `selfReferenceColumnAliasReversedOrderUnresolvedV2Test` | same | Reversed select-list order `z,y,x` — fatals for forward refs to `y` and `x` |
| `selfReferenceColumnAliasPredicandSubstitutionHappyPathV3Test` | same | V1 with `<a plus b>` predicand + `(<a>)` / `(<b>)` substitution operands; predicand typing via ancestor context (13.4.1); no diagnostics |
| `selfReferenceColumnAliasPredicandSubstitutionReversedOrderUnresolvedV4Test` | same | V3 reversed order — same diagnostic count as V2 |

**Gate (204/204):** all five methods above in `SmoketestQualityGateTestSuite` under Phase 13.4 group.

**Optional follow-ups (not in gate):**

| Method | Class | Proves |
|--------|-------|--------|
| `selectListAliasReferencedInPartitionByTest` | `SqlEventWalkerFunctionsAggregatesWindowingTests` | Minimal `ROW_NUMBER() OVER (PARTITION BY alias_from_select_list)` |
| `selectListAliasNotVisibleInOuterQueryTest` | `SqlEventWalkerCoreSelectFromAliasingTests` | Negative control: inner alias must not leak to outer scope |

### 13.4.1 — Substitution variable context typing (condition vs predicand)

**Problem (precise):** Substitution semantic type (`condition` vs `predicand`) was assigned from the **immediate parent rule** in each `exit*` handler, not from grammatical containment. `exitValue_expression` lumped `RULE_parenthesized_value_expression` with `RULE_search_condition`, so `SELECT x * (<a>)` stamped `<a>` as `condition` even though the substitution is a calc operand in the select list. Parentheses are a parse disambiguation device (`<` vs comparison), not a semantic boolean-context marker.

**Principle:** Type assignment must use **operator / expression-kind context** (nearest decisive operator or boolean-composition rule), applied **the same way in every clause** — SELECT, WHERE, GROUP BY, ORDER BY, JOIN ON, etc. Parentheses must **not** influence type. Clause context matters only for edge cases like a **bare substitution** standing in for an entire filter (`WHERE <filter>` → `condition`).

**Operator-based model (authoritative):**

| Syntactic context | Operand / substitution type | Rationale |
|-------------------|----------------------------|-----------|
| Comparison operators (`=`, `<>`, `<`, `<=`, `>`, `>=`, `LIKE`, …) | **predicand** (both sides) | Operands are values being compared; substituting boolean condition vars yields nonsense (`true >= false`). Same rule in SELECT and WHERE. |
| Arithmetic (`+`, `-`, `*`, `/`, `%`) | **predicand** | Scalar calc operands. |
| Function scalar args, casts, CASE THEN/ELSE results | **predicand** | Scalar expression operands. |
| Boolean connectives (`AND`, `OR`) between substitutions | **condition** (each operand) | Each sub is a boolean fragment: `WHERE <a> AND <b>`, `SELECT <a> AND <b> AS truth`. |
| Bare substitution replacing entire filter / ON / WHEN | **condition** | `WHERE <filter>`, `ON <join_cond>`, `CASE WHEN <cond>`. |
| `NOT` wrapping a condition sub | **condition** | Unary boolean negation of a condition variable. |

**Examples (same types in SELECT and WHERE):**

- `SELECT <a> + <b>` / `WHERE <a> + <b> > 0` → `<a>`, `<b>` are **predicand**.
- `SELECT <a> >= <b> AS truth` / `WHERE <a> >= <b>` → `<a>`, `<b>` are **predicand** (comparison operands).
- `SELECT <a> AND <b> AS truth` / `WHERE <a> AND <b>` → `<a>`, `<b>` are **condition** (boolean composition).
- `WHERE <filter>` → `<filter>` is **condition** (bare filter substitution).

**Gap (Jul 2026):** ~~Delivered resolver is **clause-tier only**~~ **Operator-walk resolver delivered (13.4.1d + 13.4.1a, Jul 2026).** Comparison / between / IN / NULL-check operands → **predicand** everywhere; arithmetic (`+`, `-`, `*`, `/`) operands → **predicand** everywhere (including WHERE, HAVING, QUALIFY, JOIN ON); boolean composition (AND/OR, bare filter sub) → **condition**. Grammar-only `additive_expression` chains under filter boolean context (no real operator) are skipped so bare `WHERE <filter>` / `ON (<cond>)` still type as **condition**.

**Delivered (Jul 2026 — 13.4.1d + 13.4.1a):**

- [x] Operator-walk `resolveSubstitutionValueTypeFromContext(ctx)` — operator-semantics first; real arithmetic → predicand in every clause; filter-context fallback for bare parenthesized condition subs.
- [x] `stampSubstitutionVariableFromContext` helper; all value-expression / predicate / select-item exit paths routed through resolver.
- [x] Tests: `selectListArithmeticPredicandSubstitutionTest`, `selectListComparisonPredicandSubstitutionTest`, `selectListBooleanAndConditionSubstitutionTest`, `whereComparisonPredicandSameAsSelectTest`, `filterArithmeticSubtractionComparisonPredicandTest`, `filterArithmeticDivisionComparisonPredicandTest`, `groupByArithmeticPredicandSubstitutionTest`, `orderByArithmeticPredicandSubstitutionTest`, `havingArithmeticSubtractionComparisonPredicandTest`, `qualifyArithmeticSubtractionComparisonPredicandTest`, `joinOnArithmeticSubtractionComparisonPredicandTest`.
- [x] Full suite green (1431/1431).

**Delivered (Jul 2026 — partial 13.4.1):**

- [x] `SqlASTWalkerHelper.resolveSubstitutionValueTypeFromContext(ctx)` — walks ancestors; returns `predicand` when a comparison, arithmetic, or other strong operator rule is hit first; `condition` for boolean-composition or bare filter roots.
- [x] `exitValue_expression`: split `RULE_parenthesized_value_expression` out of the blanket condition branch; parenthesized operands use the resolver.
- [x] V3/V4 goldens refreshed (`<a>`, `<b>` → `predicand`; symbol-table interface lists predicand deps on `y`/`z`).
- [x] Full suite green (1431/1431); WHERE parenthesis regression tests unchanged.

**Condition-context rules (resolver):** `search_condition`, `where_clause`, `having_clause`, `qualify_clause`, `searched_when_clause`, `condition_value`, `substitution_predicate`.

**Predicand-context rules (resolver):** `select_item`, `select_list`, `groupby_clause`, `orderby_clause`, `partition_by_clause`, `case_expression`, `case_result`, `when_value_clause`, `aggregate_function`, `trim_operands`, `sql_argument_list`, `row_value_predicand`.

**Confirmed behavior after fix (existing tests, no golden drift):**

| Macro clause | Condition variable shape | Predicand variable shape | Mechanism |
|--------------|-------------------------|--------------------------|-----------|
| **CASE WHEN** | Entire `WHEN` = `<var>` | `WHEN <col> = <var>` (comparison operand) | `searched_when_clause` → condition; `exitComparison_predicate` → predicand on operands |
| **CASE THEN/ELSE** | N/A | `<var>` as result expression | `case_result` / `when_value_clause` → predicand |
| **JOIN ON** | Entire `ON` = `<var>`; `ON (<var>)`; `ON <a> AND <b>` | `ON a.<col> = <var>` (comparison operand) | `search_condition` under `join_condition` → condition for bare subs; comparison operands → predicand |
| **WHERE / HAVING / QUALIFY** | Same as JOIN ON | Comparison / function / calc operands | Same ancestor rules |

JOIN `ON` resolves through `search_condition` in the ancestor chain (not a separate `join_condition` rule in the resolver). `exitJoin_condition` strips outermost parentheses from the AST only — it does not assign types.

#### 13.4.1a — Route remaining value-expression paths through resolver

**Work:**

- [x] Replace hardcoded `MUMBLE_CONDITION_KEY` / `MUMBLE_PREDICAND_KEY` in all `exitValue_expression` branches with `resolveSubstitutionValueTypeFromContext(ctx)`.
- [x] Route `exitRow_value_predicand`, `exitSelect_item` (top-level sub only), `exitComparison_predicate` operands, `exitSubstitution_predicate` through resolver.
- [x] Retire or narrow `exitBasic_predicate_clause` manual predicand relabel once covered.
- [x] Single call site pattern: `stampSubstitutionVariableFromContext(subMap, ctx)` for all value-expression substitutions.

**Gate:** existing substitution families + gate 204/204; no golden drift on `SqlEventWalkerPredicatesOperatorsSubstitutionsTests`, `SqlEventWalkerNonSqlEndpointParserTests` (CASE/JOIN), `SqlEventWalkerJoinsAndTableResolutionTests`.

#### 13.4.1b — Clause × context test matrix

Use `SUBSTITUTION_VARIABLE_TEST_TEMPLATES.md` as scaffold. Add gate or near-gate tests proving resolver coverage.

**Expected types (operator-context rows):**

| Clause | Bare substitution | Parenthesized substitution | Comparison (`<a> >= <b>`) | Arithmetic (`<a> + <b>`) | AND/OR (`<a> AND <b>`) |
|--------|-------------------|---------------------------|---------------------------|-------------------------|-------------------------|
| SELECT list | predicand* | predicand | predicand (both) | predicand (both) | condition (both) |
| WHERE | condition | condition | predicand (both) | predicand (both) | condition (both) |
| HAVING | condition | condition | predicand (both) | predicand (both) | condition (both) |
| QUALIFY | condition | condition | predicand (both) | predicand (both) | condition (both) |
| GROUP BY | predicand | predicand | predicand (both) | predicand (both) | N/A |
| ORDER BY | predicand | predicand | predicand (both) | predicand (both) | N/A |
| JOIN ON | condition | condition | predicand (both) | predicand (both) | condition (both) |
| CASE WHEN | condition | condition | predicand (operands) | — | condition |

\*Bare `SELECT <var>`: predicand per grammar (`variable_identifier` in value_expression).

**Bare ↔ parenthesized coverage (SELECT-shaped clauses):**

| Clause / slot | Expected alone | Bare test | Parenthesized test |
|---------------|----------------|-----------|-------------------|
| SELECT list | predicand | ✅ `querySubstitutionVariableForPredicandV1` | ✅ `selectListStandaloneParenthesizedPredicandTest` |
| WHERE | condition | ✅ `whereConditionWithSingleConditionVariableTest` | ✅ `whereConditionWithParentheticalConditionVariableTest` |
| HAVING | condition | ✅ `havingBareConditionSubstitutionTest` | ✅ `havingParenthesizedConditionSubstitutionTest` |
| QUALIFY | condition | ✅ `qualifyBareConditionSubstitutionTest` | ✅ `qualifyParenthesizedConditionSubstitutionTest` |
| GROUP BY | predicand | ✅ `basicAggregateQueryWithPredicandVariableTest` | ✅ `groupByParenthesizedPredicandSubstitutionTest` |
| ORDER BY | predicand | ✅ `basicOrderByWithPredicandVariableTest` | ✅ `orderByParenthesizedPredicandSubstitutionTest` |
| JOIN ON | condition | ✅ `basicJoinWithOnOnConditionVariableTest` | ✅ `basicJoinWithOnConditionVariableInParenthesisTest` |
| CASE WHEN | condition | ✅ `complexCaseExplicitConditionExpressionWithPredicandSubstitutionInQueryTest` | ✅ `caseWhenParenthesizedConditionSubstitutionTest` |

**Window function OVER — bare ↔ parenthesized coverage (predicand substitutions):**

| OVER slot | Expected | Bare test | Parenthesized test |
|-----------|----------|-----------|-------------------|
| Function arg | predicand | ✅ `windowFunctionPredicandVariableP1Test`; ✅ `sqlModeExcludesPredicandSubstitutionsFromTableDictionaryRegressionTest` | ✅ `windowFunctionArgParenthesizedPredicandTest` |
| PARTITION BY | predicand | ✅ `windowFunctionPredicandVariableP2Test`; ✅ regression test above | ✅ `windowPartitionByParenthesizedPredicandTest` |
| ORDER BY (inside OVER) | predicand | ✅ `windowFunctionPredicandVariableP3Test`; ✅ regression test above | ✅ `windowOrderByParenthesizedPredicandTest` |

Column-type subs in OVER (`a.<col>` in partition/order/arg) are covered separately: `windowFunctionColumnVariableP1/P2/P3`, `windowOrderByNullsLastInOverStatementTest`, DML complex-substitution OVER paths.

**Source-position substitutions (not predicand/condition — separate typing family):**

| Role | Type | Bare test | Parenthesized test |
|------|------|-----------|-------------------|
| UNION / INTERSECT / EXCEPT member | `query` | ✅ `unionSubstitutionV1`, `V2`, parser-shape tests | N/A (member is whole subquery) |
| INSERT … FROM `<var>` | `query` | ✅ `basicInsertFromVariableTest` | ✅ `insertFromParenthesizedQueryVariableTest` |
| CTE body = `<var>` | `query` | ✅ `withQueryFromNavigateV2StudentSubstitution` | N/A |
| FROM / JOIN table source | `tuple` | ✅ `CoreSelectFromAliasingTests` tuple-variable family | N/A |
| IN / LIKE ANY list | `in_list` | ✅ `inListEmbeddedVariablecConditionTest` | N/A |
| JOIN extension tail | `join_extension` | ✅ `NonSqlEndpointParserTests` join-extension family | N/A |
| UPDATE FROM / DELETE USING `<var>` alias | `tuple` | ✅ `updateFromBareQueryVariableTest`, `deleteUsingBareQueryVariableTest` | N/A (grammar requires alias; no parenthesized form) |

Each test asserts: AST `type=`, `substitutionsMap`, and symbol-table `interface` / `filters` where applicable.

**Tests to add (minimum new coverage):**

| Method | Class | Proves |
|--------|-------|--------|
| `selectListParenthesizedPredicandInCalcTest` | `SqlEventWalkerCoreSelectFromAliasingTests` | **Delivered** — V3/V4 |
| `caseWhenBareConditionSubstitutionTest` | `SqlEventWalkerFunctionsAggregatesWindowingTests` | **Exists** — bare `<var>` in WHEN |
| `caseWhenComparisonPredicandOperandTest` | `SqlEventWalkerFunctionsAggregatesWindowingTests` | **Exists** — predicand in WHEN comparison |
| `joinOnBareConditionSubstitutionTest` | `SqlEventWalkerJoinsAndTableResolutionTests` | **Exists** — bare + parenthesized ON |
| `joinOnAndOrConditionSubstitutionsTest` | `SqlEventWalkerJoinsAndTableResolutionTests` | **Exists** — AND/OR targets |
| `havingParenthesizedConditionSubstitutionTest` | `SqlEventWalkerPredicatesOperatorsSubstitutionsTests` | **Delivered** |
| `qualifyParenthesizedConditionSubstitutionTest` | `SqlEventWalkerPredicatesOperatorsSubstitutionsTests` | **Delivered** |
| `groupByParenthesizedPredicandSubstitutionTest` | `SqlEventWalkerPredicatesOperatorsSubstitutionsTests` | **Delivered** — `GROUP BY (<a>)` (predicand_subquery regression) |
| `orderByParenthesizedPredicandSubstitutionTest` | `SqlEventWalkerPredicatesOperatorsSubstitutionsTests` | **Delivered** — `ORDER BY (<a>)` (predicand_subquery regression) |
| `joinOnComparisonPredicandOperandTest` | `SqlEventWalkerJoinsAndTableResolutionTests` | **Delivered** — `ON a.col1 = <predicand>` |
| `selectListComparisonPredicandSubstitutionTest` | `SqlEventWalkerCoreSelectFromAliasingTests` | **Delivered** — `SELECT <a> >= <b> AS truth` |
| `selectListBooleanAndConditionSubstitutionTest` | `SqlEventWalkerCoreSelectFromAliasingTests` | **Delivered** — `SELECT <a> AND <b> AS truth` |
| `selectListArithmeticPredicandSubstitutionTest` | `SqlEventWalkerCoreSelectFromAliasingTests` | **Delivered** — `SELECT (<a>) + (<b>)` |
| `whereComparisonPredicandSameAsSelectTest` | `SqlEventWalkerPredicatesOperatorsSubstitutionsTests` | **Delivered** — `WHERE <a> >= <b>` |
| `filterArithmeticSubtractionComparisonPredicandTest` | `SqlEventWalkerPredicatesOperatorsSubstitutionsTests` | **Delivered** — `WHERE ((<a>) - 20) >= 50` |
| `filterArithmeticDivisionComparisonPredicandTest` | `SqlEventWalkerPredicatesOperatorsSubstitutionsTests` | **Delivered** — `WHERE ((<a>) / (<b>)) >= 1` |
| `groupByArithmeticPredicandSubstitutionTest` | `SqlEventWalkerPredicatesOperatorsSubstitutionsTests` | **Delivered** — `GROUP BY (<a>) - (<b>)` |
| `orderByArithmeticPredicandSubstitutionTest` | `SqlEventWalkerPredicatesOperatorsSubstitutionsTests` | **Delivered** — `ORDER BY (<a>) + (<b>)` |
| `havingArithmeticSubtractionComparisonPredicandTest` | `SqlEventWalkerPredicatesOperatorsSubstitutionsTests` | **Delivered** — `HAVING ((<a>) - 20) >= 50` |
| `qualifyArithmeticSubtractionComparisonPredicandTest` | `SqlEventWalkerPredicatesOperatorsSubstitutionsTests` | **Delivered** — `QUALIFY ((<a>) - 20) >= 50` |
| `joinOnArithmeticSubtractionComparisonPredicandTest` | `SqlEventWalkerJoinsAndTableResolutionTests` | **Delivered** — `ON ((<a>) - 20) >= 50` |

**Tests to add (bare ↔ parenthesized parity — follow-up batch):**

| Method | Class | Proves |
|--------|-------|--------|
| `havingBareConditionSubstitutionTest` | `SqlEventWalkerPredicatesOperatorsSubstitutionsTests` | **Delivered** — `HAVING <subject code>` alone types as condition |
| `qualifyBareConditionSubstitutionTest` | `SqlEventWalkerPredicatesOperatorsSubstitutionsTests` | **Delivered** — `QUALIFY <subject code>` alone types as condition |
| `selectListStandaloneParenthesizedPredicandTest` | `SqlEventWalkerCoreSelectFromAliasingTests` | **Delivered** — `SELECT (<a>) FROM tab1` (predicand_subquery path) |
| `caseWhenParenthesizedConditionSubstitutionTest` | `SqlEventWalkerFunctionsAggregatesWindowingTests` | **Delivered** — `CASE WHEN (<cond>) THEN …` types as condition |
| `windowFunctionArgParenthesizedPredicandTest` | `SqlEventWalkerFunctionsAggregatesWindowingTests` | **Delivered** — `rank((<columnParam>))` |
| `windowPartitionByParenthesizedPredicandTest` | `SqlEventWalkerFunctionsAggregatesWindowingTests` | **Delivered** — `partition by (<k_stfd>)` |
| `windowOrderByParenthesizedPredicandTest` | `SqlEventWalkerFunctionsAggregatesWindowingTests` | **Delivered** — `order by (<row_num>)` inside OVER |
| `insertFromParenthesizedQueryVariableTest` | `SqlParseEventWalkerWithAccessObjectTest` | **Delivered** — `INSERT INTO tab1 (<var>)` types as `query` |
| `updateFromBareQueryVariableTest` | `SqlEventWalkerDmlUpdateInsertDeleteTruncateTests` | **Delivered** — `UPDATE … FROM <var> alias` types as `tuple` |
| `deleteUsingBareQueryVariableTest` | `SqlEventWalkerDmlUpdateInsertDeleteTruncateTests` | **Delivered** — `DELETE … USING <var> alias` types as `tuple` |
| `updateSetQualifiedColumnLhsPredicandRhsTest` | `SqlEventWalkerDmlUpdateInsertDeleteTruncateTests` | **Delivered** — `SET e.<col> = <predicand>` (LHS column, RHS predicand) |
| `updateSetLiteralLhsPredicandRhsTest` | `SqlEventWalkerDmlUpdateInsertDeleteTruncateTests` | **Delivered** — `SET score = <predicand>` |
| `updateSetPredicandRhsParenthesizedTest` | `SqlEventWalkerDmlUpdateInsertDeleteTruncateTests` | **Delivered** — `SET score = (<predicand>)` |
| `updateWhereBareConditionSubstitutionTest` | `SqlEventWalkerDmlUpdateInsertDeleteTruncateTests` | **Delivered** — `WHERE <filter>` on UPDATE |
| `deleteWhereBareConditionSubstitutionTest` | `SqlEventWalkerDmlUpdateInsertDeleteTruncateTests` | **Delivered** — `WHERE <filter>` on DELETE |

**Will not pursue:** INSERT target column list with bare `(<var>)` before `SELECT` — use qualified `tab1.<var>` in column list if needed; not on roadmap.

**Status (Jul 2026):** ✅ **Complete** — bare↔parenthesized parity grid closed for SELECT-shaped clauses, OVER predicand slots, and DML source-position subs. `resolveSubstitutionValueTypeFromContext` routes parenthesized `CASE WHEN (<cond>)` through `searched_when_clause` (predicand_subquery path). Full suite **1431/1431** green; smoketest gate **204/204**.

#### 13.4.1d — Operator-based resolution (clause-agnostic)

**Work:**

- [x] Operator-walk `resolveSubstitutionValueTypeFromContext` — comparison / between / … operands and real arithmetic (`+`, `-`, `*`, `/`) operands → predicand in every clause; filter-context fallback for bare parenthesized condition subs.
- [x] **Clause-agnostic:** `SELECT <a> >= <b>` and `WHERE <a> >= <b>` (and HAVING / QUALIFY / ON) produce identical types; `WHERE ((<a>) - 20) >= 50` stamps `<a>` as predicand.
- [x] Route operand stamping through `stampSubstitutionVariableFromContext(subMap, ctx)`.

**Gate:** delivered tests + `whereConditionComparingPredicandVariablesTest`, parenthesized filter/ON condition tests, V3/V4 — all green (1431/1431 full suite; 204/204 smoketest gate).

#### 13.4.1c — Shared grammar-context classifier (optional refactor)

**What it accomplishes:** Today `SqlASTWalkerHelper.resolveSubstitutionValueTypeFromContext` and `SqlParseSymbolTreeHelper.inferDependentQueryContext` each walk the same ANTLR ancestor chain with overlapping rule-index sets (filters vs interface vs GROUP BY, etc.) but separate implementations. Extracting a shared `SqlGrammarContextClassifier` (or shared rule-index constants) gives one authoritative map of “what clause am I in?” consumed by both substitution typing and dependent-query recording. **Benefit:** easier to follow (one place to read grammar-context rules), and less drift risk when adding a new clause — you update the classifier once instead of hunting two parallel walks. **Not a behavior change** if done correctly; no new tests required beyond regression. **Low urgency** now that 13.4.1a–d are green.

**Work:**

- [x] Extract rule-index sets from `resolveSubstitutionValueTypeFromContext` and `SqlParseSymbolTreeHelper.inferDependentQueryContext` into one `SqlGrammarContextClassifier` (or shared constants) — same ancestor walk, two consumers (substitution typing + dependent-query context).
- [x] Document non-goals: `column`, `tuple`, `in_list`, `join_extension`, `query` types remain on dedicated exits.

**Relationship to column-resolution consolidation:** Phases 9–13 unified **reference resolution** (ingress, dictionaries, forward aliases). This sub-phase unifies **substitution typing** using the same “walk ancestors, nearest clause wins” pattern already used for dependent-query context.

### 13.5 — DDL option detail parsing (optional)

**Work (only if product needs catalog metadata beyond object name):**

- [ ] Replace opaque `generic_ddl_options` blobs with targeted sub-rules for common Snowflake/Postgres clauses (`IF NOT EXISTS`, `OR REPLACE`, `COPY GRANTS`, etc.).
- [ ] Keep fallback `generic_ddl_options` for unmodeled tail tokens.

**Tests to add:**

| Method | Class | Proves |
|--------|-------|--------|
| `createTableIfNotExistsParsedOptionsTest` | `SqlEventWalkerScriptsAndDDLTests` | **New** — AST retains `IF NOT EXISTS` node, not opaque blob |
| `createViewOrReplaceParsedOptionsTest` | `SqlEventWalkerScriptsAndDDLTests` | **New** — `OR REPLACE` captured structurally |

*Defer 13.5 if script cataloging only needs object name + type (current behavior is sufficient).*

### 13.6 — SQL statement generator

**Work:**

- [ ] Complete `SQLStatementGenerator` handlers for all `SQLParserEndPoints` keys (SCRIPT, DDL, UPDATE, DELETE, TRUNCATE, PIVOT/UNPIVOT, table functions).
- [ ] Accept external substitution map for round-trip of `<variable>` and Jinja tokens.
- [ ] Document non-goals (formatting/comment preservation).

**Tests to add:**

| Method | Class | Proves |
|--------|-------|--------|
| `roundTripUpdateWithFromTest` | `generators.SQLStatementGeneratorTest` (new) | **New** — parse → AST → regenerate → re-parse equivalence |
| `roundTripScriptMixedStatementsTest` | `generators.SQLStatementGeneratorTest` | **New** — multi-statement script round-trip |
| `roundTripPivotUnpivotTest` | `generators.SQLStatementGeneratorTest` | **New** — relational modifier round-trip |

### Phase 13 closeout checklist

- [x] All Phase 13.4 gate tests green (donor-email + self-reference V1–V4).
- [x] Smoketest quality gate **204/204** (verified Jul 2026 after 13.4 gate additions)
- [x] `insert-refactor-skip-tests.md` updated — donor-email skip removed; PIVOT class confirmed green (67/67).
- [x] Phase 11 EXCEPT deferral row marked ✅ — delivered in Phase 13.1 (Jul 2026).
- [x] Phase 11 donor-email TODO B row marked ✅ — delivered in Phase 13.4 (Jul 2026).

### Phase 13 execution order

```
13.4 (intra–select-list forward output-column resolution — gate probe already exists)
  → 13.3 (UPDATE RETURNING — mirrors DELETE RETURNING pattern)
  → 13.1 (EXCEPT parity) ✅ DONE Jul 2026
  → 13.2 (Postgres INSERT — larger grammar surface)
  → 13.6 (SQL generator — independent track; can parallelize after 13.1–13.3)
  → 13.5 (DDL detail — optional last)
```

**Do not start Phase 13 while:** a consolidation regression reopens the gate. Optional fallback retirement can proceed in parallel with Phase 13.

#### Revisit when returning to Pivot / Unpivot (deferred — Jul 2026)

When we get back to `SqlEventWalkerPivotUnpivotTests` and the pivot/unpivot helpers in `SqlParseSymbolTreeHelper`, schedule a consolidation pass with the agent to re-validate the **nested WITH `context_list` / global qualified position-tracker** work completed earlier in Phase 11:

- **`globalQualifiedUnresolvedLocations` release-on-resolve** — inner nested-WITH scopes now drop resolved qualified keys from the statement-level position tracker when local resolution consumes them (fixes leaked line refs in outer `QUALIFIED_COLUMN_NOT_FOUND_IN_TABLE` fatals; see `nestedVisibilityWithExistsCarriesCteListAaaBbbThenCccDddThenEee`).
- **`removeUnresolvedMapEntry` helper** — unpivot fallback removals and `removeFromUnresolvedMapCaseInsensitive` now also release global qualified keys; confirm no pivot/unpivot golden churn or missed releases.
- **Pivot/Unpivot test class** — not re-run during the nested-WITH fix; expect to triage `SqlEventWalkerPivotUnpivotTests` goldens and the `resolveUnpivotGeneratedColumnsFromUnresolvedMap` / `removeUnpivotGeneratedColumnReference` paths together at that time.

**Canary for the nested-WITH side (already green):** `SqlEventWalkerSubqueriesAndClauseSemanticsTests#nestedVisibilityWithExistsCarriesCteListAaaBbbThenCccDddThenEee`.

#### V13 canary — nested UPDATE FROM correlated substitution columns

Primary test: `SqlEventWalkerDmlUpdateInsertDeleteTruncateTests#updateFromNestedSubqueryDepth2CorrelatedTargetQualifiedColumnV13`

Baseline goldens: commit `5125b02` (adapt `update2` → `def_update2` prefix).

| Gap (vs `5125b02`) | Root cause | Fix status |
|--------------------|------------|------------|
| UPDATE LHS `<agg_score>` missing from update-scope `table_dictionary` / global `employees` | `makeQualifiedColumnReferenceKey` / `extractLhsColumnName` ignored substitution-in-column-map shape | Fixed — `extractColumnNameFromColumnReferenceMap` |
| Correlated `e.<dept_id>` missing from inner `def_query0.table_dictionary.employees` | Outer physical alias resolvable before table-dict row exists; materialization skipped | Fixed — `canMaterializeQualifiedToKnownPhysicalSource` + `materializeQualifiedUnresolvedEntry` + `RESOLVED_PHYSICAL_SOURCE` egress |
| Sparse `query_column_dictionary` / published `query_dictionary` (missing alias + substitution spellings) | Phase 8 removed merge hook; partial restore adds alias tokens only | **Accepted (Jul 2026)** — query dict uses alias tokens (`'a'`, `'e'`, `'inner_sq'`, `<381>`); `<327>` substitution spellings from `5125b02` are not required. Dual capture deferred. |
| Symbol-tree `filters`/`interface` substitution spellings | Already captured at walk time — goldens were copy-pasted from V10 | Goldens restored from `5125b02` |

Remaining consolidation backlog (not V13-specific):

- `resolveVisibleOuterDeferredUnresolved` is still identity — predicate outer-correlated partition defers to parent unresolved merge; FROM-subquery correlated refs should materialize locally (above fixes cover V13 path).
- Interface validation loop still skips column-type substitutions (`~1460`) — intentional for select-list fatals; clause tokens come from unresolved/materialization paths instead.
- ~~`collectClauseColumnsIntoUnresolved` skips substitutions~~ — **retired Phase 9**; clause lists validated via `validateArchivedClauseColumnRef` at scope exit instead

#### Stale golden backlog — ✅ RESOLVED (2026-07-19)

Full `SqlEventWalkerDmlUpdateInsertDeleteTruncateTests` class: **103/103** passing. Prior notes about ~82/95 stale goldens are historical.

Refresh strategy going forward: case-by-case only when behavior intentionally changes — no bulk refresh needed.

---

## Fallback retirement tracker (Phase 11+)

Retire only after Phases 6–8 make scope exits self-contained:

| Fallback | Why it exists | Retire when |
|----------|---------------|-------------|
| ~~`mergeSelectListQualifiedQueryAliasRefsIntoSourceQueryDictionary`~~ | Post-hoc select-list merge into source `queryN` dict | **Retired Jul 2026** (`e60d8f8`) — native interface loop + clause probe |
| ~~`rehomeUpdateUnqualifiedUnknownsToSingleFromTable`~~ | UPDATE single-FROM unqualified rehome | **Retired Jul 2026** — zero callers (dead code) |
| ~~`getSingleUpdateFromTableReference`~~ | Helper for rehome path only | **Retired Jul 2026** with rehome |
| `materializeResolvedUnqualifiedReference` query-backed early return | Wrong dictionary target | Resolution writes correct scope key in one pass |
| ~~`moveEntriesToSingleTableIfSingleTarget`~~ | ~~Early + late physical single-table bulk relocation~~ | **Deleted (`1d20503`)** |
| ~~`moveUnknownEntriesToSingleWildcardBackedNonTableSource`~~ | ~~Early wildcard/single-query bulk bind~~ | **Deleted (`1d20503`)** |
| ~~`canResolveUnqualifiedFromSingleWildcardQuerySource`~~ | ~~Late finalize bulk clear~~ | **Deleted (`1d20503`)** |
| Second `assignTableRefsForColumnReferenceList` on filters/groupby/orderby | Clauses collected early, resolved late | Same pass as interface validation at exit |
| `resolveInsertUnqualifiedOrphanSourceColumnsToTargetTable` | INSERT orphan hack | **Removed** — do not reintroduce |
| Predicate `embedDeferredUnresolvedInDefQueryScope` | Upward archive | Replaced by downward `context_list` + lift at predicate exit |

### Deprecated wrappers — removed (Jul 2026)

The four `@Deprecated` CTE-named aliases were deleted after call sites were renamed:

| Removed wrapper | Canonical replacement |
|-----------------|----------------------|
| ~~`mergeCteListIntoQueryScope`~~ | `mergeContextListIntoQueryScope` |
| ~~`ensureCteListSymbolMap`~~ | `ensureContextListSymbolMap` |
| ~~`getCteListSymbolMap`~~ | `getContextListSymbolMap` |
| ~~`pushSymbolTableWithParentCteList`~~ | `pushSymbolTableWithParentVisibleScope` (walker already used canonical name) |

### Load-bearing fallbacks — do NOT delete until ingress refactor

| Method | Why still needed | Retire trigger |
|--------|------------------|----------------|
| ~~`backfillQueryDictionaryFromResolvedInterfaceSources` + `sweepBackfillQueryDictionaryFromResolvedInterfaceSources`~~ | ~~Post-hoc query-dict repair after physical resolution~~ | **Deleted (`876c7ce`)** — Step D audit confirms native capture |
| ~~`materializeResolvableGlobalQualifiedUnresolvedLocations`~~ | ~~Late global qualified materialization~~ | **Retired Jul 2026** — unified `resolveQualifiedUnresolvedEntries` on `globalQualifiedUnresolvedLocations` |
| `consumeRelationalModifierDerivedColumnUnknownsFromUnresolvedMap` (private) | Pre-diagnostics derived-key batch drain (shim) | **Phase 15.5** — delete when unified per-key loop drains derived |
| ~~`moveEntriesToSingleTableIfSingleTarget`~~ | ~~Physical single-table bulk relocation~~ | **Deleted (`1d20503`)** |
| ~~`moveUnknownEntriesToSingleWildcardBackedNonTableSource`~~ | ~~Wildcard/single-query bulk bind~~ | **Deleted (`1d20503`)** |
| `resolveVisibleOuterDeferredUnresolved` | Removed | Inlined at call sites during Phase 11 kickoff |
| `isExistingArchivedClauseColumnRefSatisfied` + dual clause probe | Removed | Inlined into `validateArchivedClauseColumnRef`; remaining follow-up is the dual-probe simplification itself |

---

## Shortest path to dead-code removal

Goal: start deleting redundant code **without** breaking V9/V13 or widening golden churn.

```
Step 1 — Phase 6 close (audit only, ~1 session)                    ✅ DONE (Jul 2026)
  grep convertSymbolTableToTableDictionary — 4 intentional call sites, no walker duplicate
  reconcileJoinExtensionSymbolTable() documents mid-FROM reconcile (not publish)
  removed dead explicitTableRefByColumn block in emitExplicitQualifiedUnknownDiagnostics

Step 2 — Mechanical deprecated cleanup (~30 min)                   ✅ DONE (Jul 2026)
  renamed CTE→context_list call sites in walker + helper internals
  deleted mergeCteListIntoQueryScope, ensureCteListSymbolMap, getCteListSymbolMap, pushSymbolTableWithParentCteList

Step 3 — Phase 8 canary fix (~1 session)                           ✅ DONE (2833a2f)
  nestedQueryDemoTest: 3 fatals (tab2.e3, gg.y, tt.f); goldens updated
  derived columns in unified resolver; query-dict shortcut retired
  canaries: nestedQueryDemoTest, nestedQueryDemoWithCteTest, V9, V13

Step 4 — Late-pass helper retirement (~1–2 sessions)               ✅ DONE (Jul 2026)
  deleted materializeResolvableGlobalQualifiedUnresolvedLocations
  statement-top global qualified ingress → resolveQualifiedUnresolvedEntries
  backfill deleted (876c7ce); Step D audit confirms native capture at interface + clause probe
  derived-column stripping 3→2 (retired post-late-resolution pass)
  canaries green; UPDATE CTE spot checks same stale-golden failures as pre-retirement baseline

Step 5 — Phase 9 start (enables more retirement)                   ✅ DONE (Jul 2026)
Step 6 — Phase 10 close + Phase 9 dual-probe simplification        ✅ DONE (Jul 2026)

Full-suite verification (2026-07-19): mvn test → 1203/1203 pass.

Step A — Dead code removal (~30 min)                              ✅ DONE (Jul 2026)
  deleted rehomeUpdateUnqualifiedUnknownsToSingleFromTable + getSingleUpdateFromTableReference (zero callers)

Step B — mergeSelectList retirement (~1 session)                   ✅ DONE (Jul 2026, e60d8f8)
  deleted mergeSelectListQualifiedQueryAliasRefsIntoSourceQueryDictionary + aliasMapsToQuerySource
  gate +14 query-dict routing/diagnostic tests (181 → 195); V13/V14 green without post-hoc merge

Step C1a — Eliminate early physical bulk priming (Phase 14)          ✅ DONE (Jul 2026, 37da020)
  removed both relocateUnqualifiedToSingleTableExcludingOutputAliases calls + prepend relocation

Step C1b — Eliminate early wildcard-query bulk bind (Phase 14)      ✅ DONE (Jul 2026, 1274ee3)
  interface loop query-source fallback; removed moveUnknownEntriesToSingleWildcardBackedNonTableSource call

Step C2a — Retire late physical scope-exit drain (Phase 14)          ✅ DONE (Jul 2026, cd937c2)
  convert-time materializeUnqualifiedLineageForSingleSourceScopeAtConvertExit;
  consumeLocallyResolvedUnqualifiedBeforeScopePassUp on pass-up egress; S6/S7 audit green

Step C2b — Retire late wildcard scope-exit suppressor (Phase 14)    ✅ DONE (Jul 2026, 1d20503)
  removed canResolveUnqualifiedFromSingleWildcardQuerySource; deleted orphan bulk helpers

Step C2 closeout — Delete bulk helpers when zero call sites         ✅ DONE (Jul 2026, 1d20503)
  moveEntriesToSingleTableIfSingleTarget (C2.4) + moveUnknownEntriesToSingleWildcardBackedNonTableSource (C2b.3)

Step D — Backfill retirement audit (Phase 14)                       ✅ DONE (Jul 2026)
  D.0–D.5 green: gate 195/195, substitution V1–V16, complex sub I/U 20/20, nested demo + values V13/V14

Step E — PIVOT/UNPIVOT derived ingress (Phase 14)                  ✅ DONE (Jul 2026)
  E.0 prep ✅; E.2 ✅ (retire pre-wildcard strip); E.3 ✅ (relocate to pre-diagnostics); E.4 ✅ (delete public strip API)

Step F — Dead-code hygiene (Phase 14)                              ✅ DONE (Jul 2026)
  removed table-function field getters; satisfied() audit N/A

Phase 15 — Unified convert egress loop                             ⏸️ NEXT
  15.0 gap audit + rationalization matrix ✅
  15.1 unqualified derived-awareness (NEXT)
  15.2 clause probe consume derived keys
  15.3 hints through resolveRemaining…
  15.4 shared per-key egress helper
  15.4a retire UPDATE RHS derived branches (resolveUpdateRhsUnqualifiedAssignmentColumnsToTargetTable)
  15.4b retire interface-loop registry derived skips (IN-list output → Phase 18)
  15.5 collapse steps 5–9; delete consumeRelationalModifierDerivedColumnUnknownsFromUnresolvedMap
  15.6 ConvertEgressScopeBundle — pre-resolved def_* payloads; retire egress-time scope-chain walks

Phase 16 — PIVOT operand materialization                               ⏸️ after Phase 15
  16.0–16.3: RESOLVED_PIVOT_OPERAND; collapse triple resolvePivotOperandColumnsFromUnresolvedMap

Phase 17 — UNPIVOT synthetic columns                                   ⏸️ after Phase 15
  17.0–17.5: walk vs convert ownership; VALUE/FOR/IN outcomes; stub clause derivations

Phase 18 — PIVOT IN-list output + IN-identifier                        ⏸️ after Phase 15
  18.0–18.5: RESOLVED_PIVOT_IN_LIST_OUTPUT; retire isPivotDerivedInterfaceOutputColumn; IN-identifier contract

Phase 19 — Query dictionary publish path consolidation                   ⏸️ after Phase 15.6
  19.0–19.5: single publishQueryDictionary ingress; retire write-path spread + syncPublishedScopeQueryDictionariesFromGlobal repair
```

**Do not start with:** DML golden bulk update, CTE redesign, or PIVOT/UNPIVOT golden bulk refresh.

---

## Scope finalization map (target end state)

```
convertSymbolTable → export query_dictionary → publishQueryLikeScope
```

| Scope | Finalizer | Status |
|-------|-----------|--------|
| SELECT (`query_specification`) | `finalizeQueryScopeSymbolTable` | ✅ Phase 7 audit |
| VALUES | `finalizeValuesScopeSymbolTable` | ✅ Reference |
| UNION / INTERSECT | `finalizeSetOperationScopeSymbolTable` | ✅ |
| UPDATE | `finalizeUpdateScopeSymbolTable` | ✅ |
| DELETE | `finalizeDeleteScopeSymbolTable` | ✅ |
| INSERT wrap | `finalizeInsertScopeSymbolTable` | ✅ exists — audit parity (Phase 11) |
| Predicate (IN/EXISTS/predicand) | Merge frame + lift unresolved; **not** full finalize | Phase 10 |
| DDL | Bare pop + counter | OK — no column resolution |

---

## Test strategy

### Quality gate (primary — run after every change)

Use the **Quality gate** section at the top of this document. Prefer the Maven profile:

```bash
cd parse
mvn -Psmoketest-quality-gate test
```

### Supplementary spot checks (optional — all green as of 2026-07-19)

```bash
# UPDATE CTE substitution variants — all green
mvn test -Dtest=SqlEventWalkerDmlUpdateInsertDeleteTruncateTests#updateComplexSubstitutionU3WithCteIntersectOrderBySubstitution,updateComplexSubstitutionU4NestedWithInCteBody,updateComplexSubstitutionU5WithCteQualifyWindowSubstitution,updateComplexSubstitutionU7ChainedCteReferences,updateComplexSubstitutionU9WithCteSelfUnionBranches

# INSERT dictionary-handling V1–V7 — all green
mvn test -Dtest=SqlEventWalkerDmlUpdateInsertDeleteTruncateTests#insertDictionaryHandlingQualifiedColumnsFromWindowedSubqueryAndOrphanRhsV1,insertDictionaryHandlingQualifiedColumnsAcrossWhereSubclausesAndOrphanRhsV2,insertDictionaryHandlingUnqualifiedFallsBackToTargetTableV3,insertDictionaryHandlingUnqualifiedWithAdditionalPhysicalTableStillResolvesV4,insertDictionaryHandlingGroupByHavingSubqueryAndUnqualifiedRhsV5,insertDictionaryHandlingOrderBySubqueryAndUnqualifiedRhsV6,insertDictionaryHandlingQualifySubqueryAndUnqualifiedRhsV7

# Full DML class — 103/103 green
mvn test -Dtest=SqlEventWalkerDmlUpdateInsertDeleteTruncateTests
```

### Known skip list (unrelated failures)

Document: `parse/documents/insert-refactor-skip-tests.md` (**current Jul 2026** — no classes on skip list; PIVOT 67/67; donor-email fixed in Phase 13.4)

- ~~15 × `SqlEventWalkerPivotUnpivotTests`~~ — **resolved** (**67/67** pass as of Jul 2026; do not skip)
- 1 × donor-email live sample — **Phase 13.4** (`source_partner_system_name` / same-select-list forward alias)

### Golden update policy

- **Keep** `assertFatalDiagnosticAtPosition` with full line + character checks
- Update token locations when deferral timing changes — expected, not regression
- **Do not** bulk-update goldens until phase gate tests pass and user reviews output

---

## Explicitly out of scope (unless regression forces it)

- Scanning `filters`/`interface` after the fact to fix resolution
- Second unresolved bucket or parse-time materialize wrappers
- Routing predicate subqueries through full `finalizeQueryScopeSymbolTable` (breaks merge semantics)
- DELETE-logic refactor during Phases 1–4 (constraint on initial slice; revisit in Phase 11)
- **Origin-CTE backfill** — retro-writing descendant refs into finalized origin CTE `def_*` payloads (permanently rejected Jul 2026; see **Origin-CTE backfill — PERMANENTLY REJECTED** under Phase 11 closeout)

**Moved to Phase 13 (start after consolidation closeout):**

- ~~EXCEPT set-operation parity → Phase 13.1~~ ✅ **Done (Jul 2026)**
- Postgres INSERT completion → Phase 13.2
- UPDATE RETURNING walker → Phase 13.3
- Same-select-list forward alias (donor-email) → Phase 13.4
- DDL option detail parsing (optional) → Phase 13.5
- SQL statement generator round-trip → Phase 13.6

---

## Suggested execution order

```
Phases 1–4 ✅  →  5  →  6  →  7  →  8  →  9  →  10  →  11  →  12 ✅
       →  Steps A–B ✅ (dead code + mergeSelectList)
       →  Phase 14 Steps C–F ✅
       →  Phase 15 (unified convert egress loop) ⏸️ NEXT — 15.1 first
       →  Phase 15.6 (ConvertEgressScopeBundle — pre-resolved def_* at convert exit)
       →  Phase 16 (PIVOT operands) → Phase 17 (UNPIVOT synthetic) → Phase 18 (IN-list output + IN-identifier)
       →  Phase 19 (query-dict publish ingress consolidation — after 15.6)
       →  Phase 13 (language features — can overlap Phases 15–19 once gate stays green)
```

Phases 6–7 and 8 can overlap carefully (same files); prefer **6 before 8** so the egress helper targets one convert implementation.

**Phase 13 starts when ready** (Phases 9–12 test closeout complete as of 2026-07-19: 1203/1203 full suite, 195/195 gate).

**Phase 15.1 is the recommended immediate next step.** Phases **16–18** complete relational-modifier namespaces after Phase 15 closeout; **15.6** builds `ConvertEgressScopeBundle`; **Phase 19** consolidates query-dict publish ingress after **15.6**. See Phases 15–19 sections.

---

## Final actions for rolling out the parser

**When to run:** After consolidation closeout (Phases 15–19 and any additional phases listed below) and a green full module gate — **before** treating the modified parser as production-safe.

**Why:** This effort has changed parser, walker, symbol-table, and substitution-typing behavior in large, small, and subtle ways (clause-agnostic predicand typing, `def_*` canonicalization, `context_list` / alias maps, convert egress, relational-modifier namespaces, unparenthesized calc operands, etc.). Unit and gate tests prove correctness for covered scenarios; production validation must confirm end-to-end consumers still work on real bound query text.

### Additional consolidation phases (reserve — add here if needed)

| Phase | Scope | Status |
|-------|-------|--------|
| _(open)_ | _Add rows here if closeout discovers more consolidation work before rollout_ | ⏸️ |

Do **not** start the production validation checklist until this table is empty or every row is ✅.

---

### Production validation checklist

Run against production or production-equivalent data and tooling. Record environment, sample size, and pass/fail per item.

#### 1. Query tool — extractor presentation

- [ ] Confirm the **query tool** can properly **display and present** changes in the **extractor object strings** (AST tree, substitutions map, dictionaries, diagnostics) after parser/walker changes.
- [ ] Spot-check queries that exercise new symbol-table shapes (`def_*` payloads, `context_list`, nested scopes, substitution typing).

#### 2. RMCP Extension — symbol table and lineage

- [ ] Confirm the **RMCP Extension** can **display and use** the modified **symbol table** for production-like queries.
- [ ] Verify **lineage tracing** through the new logic and statement types (nested queries, CTEs, DML, set-ops, modifiers).
- [ ] Verify **`def_*` labelling**, **table alias** maps, and **`context_list`** entries resolve and display as expected in the extension UI/workflows.

#### 3. Query generator — AST consumption

- [ ] Confirm the **query generator** can **read and use** the changed **AST** (including calc shapes without redundant `parentheses` wrappers, updated substitution `type=` fields, clause egress shapes).
- [ ] Round-trip a representative sample: parse → generate → re-parse without structural drift on supported constructs.

#### 4. Production substitution discovery and typing (100% active bound queries)

- [ ] Using **100% of unique, active bound query text** in production, prove that **every substitution variable** is:
  - discovered,
  - added to the **substitutions list**, and
  - **appropriately typed** — especially **condition** vs **predicand** (also column, tuple, query, join_extension, in_list where applicable).
- [ ] Flag and triage any mismatches against the operator/grammar-context typing model (§13.4.1); do not ship until unexplained diffs are zero or explicitly accepted.

#### 5. Production parse coverage (100% active snippets)

- [ ] Confirm that **100% of production snippets** (bound query fragments / snippet endpoints) **parse successfully** with zero unexpected syntax errors under the new grammar (including unparenthesized predicand arithmetic where applicable).
- [ ] Categorize any failures: grammar gap, legacy workaround removal, or data issue.

#### 6. Reconstruction and execution parity

- [ ] Confirm that **reconstructed bound query + snippets** produce the **same query text** as the source binding.
- [ ] Confirm that reconstructed query **parses** under the new parser and **executes** successfully against the target engine (or approved execution stub).
- [ ] Document any intentional non-parity (unsupported constructs, known generator gaps).

---

### Rollout sign-off

| Gate | Owner | Date | Notes |
|------|-------|------|-------|
| Checklist items 1–6 complete | | | |
| Full module test gate green (`mvn test`) | | | |
| Smoketest quality gate green (`mvn -Psmoketest-quality-gate test`) | | | |
| Production sample audit attached | | | |

---

## Prompt to paste into a new agent session

```
We are continuing symbol-table resolution consolidation for the SQL parse walker.

Read parse/documents/symbol-table-resolution-consolidation-worklist.md first — especially:
- Progress dashboard (Jul 2026) — Phase 15 is NEXT
- Phase 15 — Unified convert egress loop (15.1–15.6; **15.6** = ConvertEgressScopeBundle)
- Phase 19 — Query dictionary publish path consolidation (after 15.6)
- Shortest path to dead-code removal
- Published scope vs global dictionary rules
- Phase 8 unified resolver (RESOLVED_DERIVED_COLUMN, isPhysicalTableRefVisibleInScope)

Current state (Spring-2026-Extensions):
- Phases 1–12 + Phase 14 Steps C–E done; gate 195/195; full suite 1209/1209.
- Phase 15 NEXT (derived registry keys + egress scope bundle at 15.6); Phases 16–18 (relational modifiers); Phase 19 (query-dict publish ingress).

Your mission this session — follow Phase 15 substeps unless user directs otherwise; see Phases 16–19 for later work:
1. Phase 15.1: derived-awareness in resolveUnqualifiedColumnAgainstVisibleScope
2. Re-run pivot 67 + gate 195 + full suite after each sub-step.

Contract to preserve:
- Embedded scope payloads are canonicalized as def_*.
- Local alias maps and unaliased source refs remain queryN/valuesN/unionN/intersectN (or real alias/table names), not def_*.
- AST stays syntactic (table_ref=null for unqualified/unreferenced columns); resolution only in symbol table/dictionaries.
- Never backpatch finalized def_queryN child payloads.
- Never add new fallback/recovery readers without explicit user approval.
- Local query_dictionary is output-token storage — NOT a substitute for querySourceExportsColumn proof.

Validation (run after each step — full quality gate):

  cd parse
  mvn -Psmoketest-quality-gate test

Gate = 107 tests: nested demo (2), query dictionary source routing canaries (3), correlated scalar predicand (16), correlated IN (8), correlated EXISTS (5), UPDATE V1–V14 (14), INSERT VALUES V1–V7 (7), unaliased V1–V16 (16), CTE unqualified refs CTEV1–CTEV15 (15), scalar subquery symbol-table matrix V1–V9 + correlated (10), production scalar/EXISTS probes (4), nested formula subqueries (1), subquery semantics probes (6).
See "Quality gate" section at top of worklist for method names.

Out of scope this session:
- CTE behavior redesign, DML golden bulk refresh.
- Phase 13 items (EXCEPT, Postgres INSERT, UPDATE RETURNING, forward alias, SQL generator) — start only after consolidation closeout; see Phase 13 section.

Keep diffs minimal. One logical change per commit if committing.
```

---

## Related artifacts

| Path | Purpose |
|------|---------|
| `parse/documents/def-query-canonicalization-phases1-4-checklist.md` | Completed Phases 1–4 patch detail |
| `parse/docs/qualified-column-table-dict-handoff-prompt.md` | Nested-query qualified column canary |
| `parse/documents/insert-refactor-skip-tests.md` | Tests to ignore during incremental work |
| `parse/src/main/java/sql/symboltree/SqlParseSymbolTreeHelper.java` | Primary implementation target |
| `parse/src/main/java/sql/walker/SqlParseEventWalker.java` | Exit handlers; delegate to helper |

## Source conversations (Cursor agent transcripts)

- Qualified column egress Steps 0–7 / Phases 1–5: transcript `19fbab8a-ae7f-455a-b3e7-fd42ba23ecc6`
- Clause lists, convertSymbolTable, context_list, DML: transcript `6eca84a8-9a2d-415f-b3d4-32a323181765`
- Unaliased FROM / def_query registration: transcript `b93c01bb-58f3-41a3-966b-534264a4f980`
