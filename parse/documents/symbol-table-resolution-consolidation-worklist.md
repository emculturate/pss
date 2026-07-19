# Symbol Table Resolution Consolidation — Ultimate Worklist

Use this document as the single handoff for consolidating column resolution in the SQL parse walker and `SqlParseSymbolTreeHelper`. It merges planning from:

- `def-query-canonicalization-phases1-4-checklist.md` (Phases 1–4, **done**)
- Qualified-column egress unification (`nestedQueryDemoTest` canary)
- Clause-list / `convertSymbolTableToTableDictionary` consolidation thread
- INSERT/VALUES and DML parity notes (where they touch shared resolution)

**Last updated:** 2026-07-19 (verified **1203/1203** full-suite + **181/181** gate green; Phases 9–12 test backlog cleared; checklist synced)

---

## Quality gate (run before every consolidation change)

**181 tests** — all passing in the current gate. Implemented in `SmoketestQualityGateTestSuite` and runnable via Maven profile `smoketest-quality-gate`.

**Full module suite (2026-07-19):** `mvn test` → **1203/1203** passing across all walker, access, CLI, and generator test classes.

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
| Live-sample probes | 2 | `SqlEventWalkerLiveSampleQueriesTests` | `donorEmailWithInvalidFatalErrorOnQualifiedColumnVariableTest`, `getMissingColumnFromTupleDictionaryTest` |
| Table-function diagnostic | 1 | `SqlEventWalkerTableFunctionTests` | `simpleTfCallFlattenSplitV5Test` |
| PIVOT / UNPIVOT smoke | 3 | `SqlEventWalkerPivotUnpivotTests` | `unpivotV1Test`, `pivotV1Tab1Test`, `pivotInIdentifierResolvedFromSubqueryWarningV1Test` |
| Nested WITH clause / set-op matrix | 4 | `SqlEventWalkerSubqueriesAndClauseSemanticsTests` | scalar HAVING, scalar SELECT-list, UNION, INTERSECT exemplars |
| Endpoint parser extensions | 2 | `SqlEventWalkerNonSqlEndpointParserTests` | `basicTupleSubstitutionVariableTest`, `inListVariableSubstitutionTest` |
| JOIN duplicate-interface fatal | 1 | `SqlEventWalkerJoinsAndTableResolutionTests` | `handlingRepeatingColumnNamesInTheInterfaceV1` |
| Access-object / Snippet integration | 2 | `SqlParseEventWalkerWithAccessObjectTest` | `basicQuerySnippetTest`, `basicTupleSnippetTest` |
| DML UPDATE V1–V14 | 14 | `SqlEventWalkerDmlUpdateInsertDeleteTruncateTests` | `updateDictionaryHandling*` V1–V12; `updateFromNestedSubqueryDepth2CorrelatedTargetQualifiedColumnV13`; `updateFromNestedSubqueryDepth3CorrelatedTargetQualifiedColumnV14` |
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

**Gate status (2026-07-19):** **181/181 passing** — no current failures.

**Full suite status (2026-07-19):** **1203/1203 passing** — includes all substitution-variable families (Column V1–V16, INSERT I1–I10, UPDATE U1–U10), DML dictionary tests, PIVOT/UNPIVOT (62), and live-sample probes.

The prior phase-7, phase-10, and DML golden-backlog notes below are **historical** only.

---

## Current gate failures

None. The consolidation gate is green at **181/181**. The full parse module suite is green at **1203/1203**.

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
| **5** def_query read-path gaps | ✅ Done | V1–V16 unaliased-derived green; symbol-tree + query-dict goldens aligned (Jul 2026) |
| **6** one `convertSymbolTableToTableDictionary` | ✅ Done | 100% | Audit Jul 2026: single helper impl; `reconcileJoinExtensionSymbolTable` for mid-FROM; dead `explicitTableRefByColumn` removed |
| **7** uniform query scope finalization | ✅ Done | 100% | Current gate is green (130/130); prior phase-7 backlog fully refreshed |
| **8** unified egress helper | ✅ Done | 100% | Late-pass helpers retired/consolidated; global qualified ingress now uses `resolveQualifiedUnresolvedEntries`; backfill folded into interface loop + final sweep |
| **9** clause-list validation (no parallel pipelines) | ✅ Done | 100% | Gate + full suite green; optional code cleanup: retire `mergeSelectList…` hook after native select-list egress |
| **10** Substitution Variable Quality Gate Inventory | ✅ Done | 100% | All families green — Column V1–V16, INSERT I1–I10, UPDATE U1–U10 verified 2026-07-19 |
| **11** downward `context_list` resolution | ✅ Done | 100% | Canary set + full suite green; closeout checklist signed off below |
| **12** DML parity + fallback retirement | ✅ Done | 100% | All 103 DML class tests + 30 complex-substitution tests green; optional origin-CTE backfill not taken |
| **13** Language feature gap closure | ⏸️ Not started | 0% | **Unblocked for test work** — start when ready; see Phase 13 section |

**Recent wins (Jul 2026):**

- Phase 8 late-pass retirement: deleted `materializeResolvableGlobalQualifiedUnresolvedLocations`; statement-top global qualified ingress now delegates to unified `resolveQualifiedUnresolvedEntries`
- Phase 8 backfill consolidation: per-column `backfillQueryDictionaryFromResolvedInterfaceSources` invoked from interface-loop `RESOLVED_PHYSICAL_SOURCE`; final `sweepBackfillQueryDictionaryFromResolvedInterfaceSources` after late materialization (load-bearing)
- Derived-column stripping consolidated from 3 passes to 2 (pre-wildcard + post-UPDATE-rhs); post-late-resolution strip retired — unified resolver + interface loop handle derived proof
- Commit `2833a2f`: PIVOT/UNPIVOT derived columns proof folded into unified qualified resolver (`RESOLVED_DERIVED_COLUMN`); retired `localCurrentQueryDictionary.containsKey(columnName)` diagnostic shortcut
- Commit `2833a2f`: `isPhysicalTableRefVisibleInScope` — physical materialization gated on visible scope only (no global-dict visibility leak); `nestedQueryDemoTest` goldens updated
- Phase 8 canaries green: `nestedQueryDemoTest` (3 fatals), `nestedQueryDemoWithCteTest`, V9, V13
- V13 nested UPDATE FROM correlated substitution columns — table dict + query dict + symbol tree green
- V9 UPDATE FROM join-on orphan RHS — goldens aligned
- Query column dictionary alias tokens (`'a'`, `'e'`, `'inner_sq'`) accepted as canonical (not `<327>` substitution spellings)

**Active blockers:** None. All consolidation-phase tests are green. Remaining work is **optional code cleanup** (fallback retirement, native select-list egress) and **Phase 13** language features — not test golden refresh.

**Suggested next focus:** Optional fallback retirement → **Phase 13** language feature gap closure (or skip to Phase 13 if cleanup is deferred).

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

3. **Egress:** one decision tree at scope boundaries:

   ```
   defer (FROM stack pending, or policy says carry up) → keep in map
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
| Early `resolveRelationalModifierDerivedColumnsFromUnresolvedMap` (×3 in convert) | Strips derived keys from unresolved map before unified egress | May be redundant now that unified resolver returns `RESOLVED_DERIVED_COLUMN` |
| `local query_dictionary` | Scope **output** token map (select-list names → tokens) | **Not** used for `alias.col` proof; do not fold into `querySourceExportsColumn` |
| `mergeSelectListQualifiedQueryAliasRefsIntoSourceQueryDictionary` | Post-hoc merge of qualified select-list refs into source query dict | Load-bearing for V13; retire after native clause egress |
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

### Phase 5 — Close def_query canonicalization gaps (~40% done)

**Goal:** Finish the read-path and snapshot behavior started in Phases 1–4 before larger refactors.

| Task | Status | Notes |
|------|--------|-------|
| Resolve V1 `table_ref` delta | ✅ | Correlated outer ref stays `table_ref=null` in child; parent carries resolution |
| Expand verification | ⚠️ | V1/V2/V3 pass; V4–V16 fail mostly on **query-dict golden drift** (richer tokens), not fatals |
| Document `queryN` vs `def_queryN` contract | ✅ | See **Published scope vs global dictionary** above |
| Enforce strict payload lookup | ⚠️ | Partial — recursive descendant fallbacks still under narrow-pass audit |

**Gate:** V1/V7 (or agreed subset) pass without regressions on anonymous FROM registration. V7 currently fails query-dict golden only — review before bulk update.

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
- `mergeSelectListQualifiedQueryAliasRefsIntoSourceQueryDictionary` — V13 query-dict alias tokens
- `sweepBackfillQueryDictionaryFromResolvedInterfaceSources` + per-column backfill at interface `RESOLVED_PHYSICAL_SOURCE`
- `resolveRelationalModifierDerivedColumnsFromUnresolvedMap` ×2 (pre-wildcard + post-UPDATE-rhs)

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
| Select-list qualified refs | ⚠️ | `mergeSelectList…` hook still load-bearing; **tests green** — optional native egress cleanup |
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

**Test verification (2026-07-19):** Full gate (181/181) and full suite (1203/1203) green, including scalar subquery V1–V9 matrix, HAVING/UNION/wildcard semantics probes, and DML clause paths.

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

**Optional code cleanup (not test-blocking):**

- Retire `mergeSelectListQualifiedQueryAliasRefsIntoSourceQueryDictionary` once native select-list clause egress emits alias tokens at walk time (Phase 8 carryover).

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
| `mergeSelectListQualifiedQueryAliasRefsIntoSourceQueryDictionary` | Load-bearing for V13 | Keep until clause egress emits alias + substitution tokens natively |
| `backfillQueryDictionaryFromResolvedInterfaceSources` + `sweepBackfillQueryDictionaryFromResolvedInterfaceSources` | Load-bearing | Keep until walk-time token capture is proven stable at interface validation |
| `moveEntriesToSingleTableIfSingleTarget` | Load-bearing | Keep until single-source scopes resolve fully at exit |
| `resolveRelationalModifierDerivedColumnsFromUnresolvedMap` ×2 | Load-bearing | Keep until derived-column ingress no longer needs pre-/post-wildcard stripping |
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
| EXCEPT set-op parity | ⏸️ | **Moved to Phase 13.1** |
| Retire late-pass fallbacks (as scopes self-contain) | ⚠️ | **Optional cleanup** — `mergeSelectList` hook + backfill sweep + `moveEntriesToSingleTableIfSingleTarget` still load-bearing but tests pass |
| Donor-email forward alias (TODO B) | ⏸️ | **Moved to Phase 13.4** |

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

#### Likely dead cleanup candidates

These are the only uncovered helper surfaces that currently look like plausible dead-code removals rather than just unexercised feature paths.

- [ ] [SqlParseSymbolTreeHelper.java](../src/main/java/sql/symboltree/SqlParseSymbolTreeHelper.java): `getTableFunctionSourceCount()` / `setTableFunctionSourceCount(int)` / `getSuppressedAmbiguousUnqualifiedKeys()` / `getTableFunctionSourceRefs()` — uncovered and no workspace callers outside the definitions.
- [ ] [SqlParseSymbolTreeHelper.java](../src/main/java/sql/symboltree/SqlParseSymbolTreeHelper.java): `ArchivedClauseColumnRefResult.satisfied()` — uncovered and not referenced by any workspace test or helper call site.

#### Untested feature paths

These are uncovered by the current coverage run, but they map to real feature families with their own tests elsewhere in the project. The main question is coverage depth, not whether the code is dead.

- [ ] [SqlParseSymbolTreeHelper.java](../src/main/java/sql/symboltree/SqlParseSymbolTreeHelper.java): pivot / unpivot helpers (`resolveUnpivotGeneratedColumnsFromUnresolvedMap`, `mergePivotAggregateDependencyRefsFallbackIfPresent`, `mergeUnpivotDerivedRefsIfPresent`, and related helpers) — feature-specific and backed by dedicated Pivot/Unpivot tests.
- [ ] [SqlParseSymbolTreeHelper.java](../src/main/java/sql/symboltree/SqlParseSymbolTreeHelper.java): table-function helpers and insert/update rehoming helpers (`reconcileJoinExtensionSymbolTable`, `isInsertStatementSqlTree`, `rehomeUpdateUnqualifiedUnknownsToSingleFromTable`) — live behavior, not dead code.
- [ ] [SqlParseSymbolTreeHelper.java](../src/main/java/sql/symboltree/SqlParseSymbolTreeHelper.java): unpivot / derived-column / VALUES / DML support paths — these are covered by dedicated walkers and golden tests, but not by the 130-test consolidation gate.

**Project-wide test-bed note**

- [ ] Current repo scan of `parse/src/test/java` finds **1,055** `@Test` methods across 12 walker test classes. That is broader than the 130-test consolidation gate, but still not a full top-to-bottom test bed for every uncovered helper branch.
- [ ] The uncovered `context_list` publication helpers are not a test-bed gap; they are already exercised by the 130-test gate and the full gate, and should stay in place.
- [ ] The uncovered pivot/unpivot, VALUES, DML, and table-function helpers are mostly a gate-size issue when the relevant feature classes already exist, but they still expose broader coverage gaps for feature families that are not validated by the consolidation gate.

### Phase 12 — Optional origin-CTE backfill sweep (only after Phase 11 is otherwise done)

**Goal:** If we later decide the origin-CTE retro-write is worth the churn, add it as a final opt-in step after Phase 11 has finished and stayed green.

| File | Change? | Why |
|------|---------|-----|
| `SqlEventWalkerLiveSampleQueriesTests.java` | Change | Largest nested CTE / alias surface; a deeper origin-CTE backfill would rewrite already-published nested payloads here. |
| `SqlEventWalkerFunctionsAggregatesWindowingTests.java` | Change | Contains nested window / aggregate / CTE composition where later-query refs can be retro-written into the source CTE dictionary. |
| `SqlEventWalkerSubqueriesAndClauseSemanticsTests.java` | Change | CTE-backed subquery cases are the direct consumer of downward visibility, so they would pick up the new origin writeback. |
| `SqlEventWalkerDmlUpdateInsertDeleteTruncateTests.java` | Change | The nested UPDATE / INSERT / DELETE canaries rely on the same publication path and would churn if origin dictionaries are backfilled. |
| `SqlParseEventWalkerWithAccessObjectTest.java` | Change | Access-object nested query cases already show token backfill behavior and are sensitive to any retro-write into the origin CTE. |
| `SqlEventWalkerCoreSelectFromAliasingTests.java` | Change | This is the narrowest and best canary family for the nested CTE / correlated alias path. |
| `SqlEventWalkerJoinsAndTableResolutionTests.java` | No-change | Join resolution changes table/reference binding, but not the origin-CTE backfill surface itself. |
| `SqlEventWalkerCastingAndTypesTests.java` | No-change | Mostly single-scope casts and projections; there is no descendant CTE to rewrite back into. |
| `SqlEventWalkerTableFunctionTests.java` | No-change | Table-function publication is a separate scope shape and does not depend on origin-CTE writeback. |
| `SqlEventWalkerPivotUnpivotTests.java` | No-change | Derived-column handling is the controlling path here, not a later-query origin CTE backfill. |
| `SqlEventWalkerScriptsAndDDLTests.java` | No-change | DDL / script scaffolding does not exercise the nested publication path that would churn from this change. |

**Phase 12 gate if we ever take it:**

- `SqlEventWalkerCoreSelectFromAliasingTests#nestedQueryDemoTest`
- `SqlEventWalkerCoreSelectFromAliasingTests#nestedQueryDemoWithCteTest`
- `SqlEventWalkerCoreSelectFromAliasingTests#correlatedInSubqueryMiddleCteReferencesFirstCteTest`
- `SqlEventWalkerCoreSelectFromAliasingTests#correlatedExistsSubqueryMiddleCteReferencesFirstCteTest`
- `SqlEventWalkerSubqueriesAndClauseSemanticsTests#nestedSubqueryWithColumnsV0`
- `SqlEventWalkerSubqueriesAndClauseSemanticsTests#nestedSelectStarV3`
- `SqlEventWalkerSubqueriesAndClauseSemanticsTests#nestedSelectStarV5`
- `SqlEventWalkerSubqueriesAndClauseSemanticsTests#nestedSelectStarV6`
- `SqlEventWalkerDmlUpdateInsertDeleteTruncateTests#updateComplexSubstitutionU4NestedWithInCteBody`
- `SqlEventWalkerDmlUpdateInsertDeleteTruncateTests#insertComplexSubstitutionI4NestedWithInCteBody`
- `SqlParseEventWalkerWithAccessObjectTest#coverageDrivenSubqueryUnresolvedQualifierPassUpToParentTest`

**Recommended execution order for that optional phase:** run the 11 canaries above first, then refresh only the files that move. If the matrix stays mostly `No-change`, keep the change out of Phase 12.

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
| 13.1 | **EXCEPT set-operation parity** | `EXCEPT` token in `union_operator`; no first-class set-op sibling to `UNION`/`INTERSECT` | `finalizeSetOperationScopeSymbolTable` (or sibling) handles EXCEPT; interface column-count validation; query-dictionary + symbol-tree keys match UNION/INTERSECT patterns | `SqlEventWalkerSubqueriesAndClauseSemanticsTests` |
| 13.2 | **Postgres INSERT** | `postgres_insert` rule marked incomplete; no `exitPostgres_insert` | Full Postgres INSERT shape (incl. `RETURNING` via `select_list`); dedicated walker exit + symbol-table finalizer hook if needed | `SqlEventWalkerDmlUpdateInsertDeleteTruncateTests` |
| 13.3 | **UPDATE RETURNING** | `returning` rule on `update_expression`; `exitReturning` commented out in walker | Active `exitReturning`; output interface populated like Postgres DELETE RETURNING | `SqlEventWalkerDmlUpdateInsertDeleteTruncateTests` |
| 13.4 | **Same-SELECT-list forward alias** | Unqualified ref in `PARTITION BY` (etc.) does not bind to earlier select-list alias in same scope | Local select-list alias registry consulted during clause egress (GROUP BY, ORDER BY, PARTITION BY, QUALIFY) | `SqlEventWalkerLiveSampleQueriesTests`, `SqlEventWalkerFunctionsAggregatesWindowingTests` |
| 13.5 | **DDL option detail parsing** | `generic_ddl_options` / `generic_ddl_paren_content` capture opaque token blobs | *Optional:* parse high-value clauses (e.g. `IF NOT EXISTS`, `OR REPLACE`, `CLUSTER BY`) without full dialect coverage | `SqlEventWalkerScriptsAndDDLTests` |
| 13.6 | **SQL statement generator** | `SQLStatementGenerator` partial; not production-ready | Round-trip SQL regeneration for all `SQLParserEndPoints` keys from AST + substitution map | New or extended generator test class |

### 13.1 — EXCEPT set-operation parity

**Work:**

- [ ] Treat `EXCEPT` as a first-class set operator in `exitUnionized_query` / `exitUnion_clause` / `finalizeSetOperationScopeSymbolTable` (mirror UNION/INTERSECT interface capture and column-count fatals).
- [ ] Emit consistent `def_unionN` / set-op scope keys and query-dictionary routing for EXCEPT branches.
- [ ] Confirm grammar precedence (`intersected_query` → `unionized_query`) still correct when EXCEPT chains mix with UNION/INTERSECT.

**Tests to add or bring green:**

| Method | Class | Proves |
|--------|-------|--------|
| `unaliasedDerivedExceptAllOuterClausesV10Test` | `SqlEventWalkerSubqueriesAndClauseSemanticsTests` | **Existing** — unaliased derived + EXCEPT across WHERE/GROUP BY/HAVING/ORDER BY/QUALIFY; refresh goldens when behavior is fixed |
| `exceptColumnCountMismatchEmitsFatalTest` | `SqlEventWalkerSubqueriesAndClauseSemanticsTests` | **New** — EXCEPT branches with unequal column counts → `SET_OPERATION_INTERFACE_COLUMN_COUNT_MISMATCH` |
| `exceptUnionIntersectChainedInterfaceTest` | `SqlEventWalkerSubqueriesAndClauseSemanticsTests` | **New** — `(q1 EXCEPT q2) UNION q3` interface + symbol-tree shape |
| `exceptWithCteUnqualifiedRefsTest` | `SqlEventWalkerSubqueriesAndClauseSemanticsTests` | **New** — CTE + EXCEPT unqualified column resolution (extend CTEV matrix pattern) |

**Gate candidacy (after green):** add `exceptColumnCountMismatchEmitsFatalTest` + `unaliasedDerivedExceptAllOuterClausesV10Test` to `SmoketestQualityGateTestSuite`.

### 13.2 — Postgres INSERT

**Work:**

- [ ] Complete `postgres_insert` grammar (ON CONFLICT, DEFAULT VALUES, multi-row VALUES, RETURNING) per Postgres comment block in `SQLSelectParser.g4`.
- [ ] Add `exitPostgres_insert` (or fold into `exitSnowflake_insert` with dialect branch) and wire INSERT finalizer parity with UPDATE/DELETE.
- [ ] Publish `def_insertN` symbol-table shape consistent with Snowflake INSERT paths.

**Tests to add:**

| Method | Class | Proves |
|--------|-------|--------|
| `postgresInsertReturningSelectListInterfaceTest` | `SqlEventWalkerDmlUpdateInsertDeleteTruncateTests` | **New** — `INSERT … RETURNING col1, col2` populates output interface |
| `postgresInsertOnConflictDoNothingTest` | `SqlEventWalkerDmlUpdateInsertDeleteTruncateTests` | **New** — parses + catalogs target table; ON CONFLICT clause retained in AST |
| `postgresInsertDefaultValuesTest` | `SqlEventWalkerDmlUpdateInsertDeleteTruncateTests` | **New** — `INSERT … DEFAULT VALUES` symbol-table baseline |
| `postgresInsertWithCteBodyTest` | `SqlEventWalkerDmlUpdateInsertDeleteTruncateTests` | **New** — `WITH … INSERT` Postgres variant inside CTE body |

### 13.3 — UPDATE RETURNING

**Work:**

- [ ] Uncomment and implement `exitReturning` in `SqlParseEventWalker.java`.
- [ ] Route RETURNING output through same interface path as `exitDelete_returning` (`select_list` → interface tokens).
- [ ] Extend `finalizeUpdateScopeSymbolTable` if RETURNING columns need query-dictionary attribution.

**Tests to add:**

| Method | Class | Proves |
|--------|-------|--------|
| `updateReturningStarInterfaceTest` | `SqlEventWalkerDmlUpdateInsertDeleteTruncateTests` | **New** — `UPDATE … RETURNING *` interface wildcard |
| `updateReturningQualifiedColumnsTest` | `SqlEventWalkerDmlUpdateInsertDeleteTruncateTests` | **New** — `RETURNING t.col AS alias` table-dict + interface |
| `updateReturningWithFromSubqueryTest` | `SqlEventWalkerDmlUpdateInsertDeleteTruncateTests` | **New** — UPDATE FROM + RETURNING combined |

### 13.4 — Same-SELECT-list forward alias resolution

**Work:**

- [ ] At `exitSelect_item`, register output aliases in a per-`query_specification` visible map (case-folding rules aligned with existing alias map).
- [ ] During `validateArchivedClauseColumnRef` / window `partition_by` / `orderby` egress, resolve unqualified refs against that map before physical-table lookup.
- [ ] Do **not** backpatch finalized child `def_queryN` payloads; resolution stays in the owning select scope.

**Tests to add or bring green:**

| Method | Class | Proves |
|--------|-------|--------|
| `donorEmailWithInvalidFatalErrorOnQualifiedColumnVariableTest` | `SqlEventWalkerLiveSampleQueriesTests` | **Existing** — production donor-email query; `PARTITION BY … source_partner_system_name` binds to same-list alias; remove TODO at ~L277 |
| `selectListAliasReferencedInPartitionByTest` | `SqlEventWalkerFunctionsAggregatesWindowingTests` | **New** — minimal `ROW_NUMBER() OVER (PARTITION BY alias_from_select_list)` |
| `selectListAliasReferencedInOrderByTest` | `SqlEventWalkerCoreSelectFromAliasingTests` | **New** — `ORDER BY` forward alias in same select list |
| `selectListAliasNotVisibleInOuterQueryTest` | `SqlEventWalkerCoreSelectFromAliasingTests` | **New** — negative control: inner alias must not leak to outer scope |

**Gate candidacy (after green):** `donorEmailWithInvalidFatalErrorOnQualifiedColumnVariableTest` already in gate — should pass without unresolved fatals once fixed.

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

- [ ] All Phase 13 test methods above are green (existing + new).
- [ ] Smoketest quality gate still **181/181** (verified 2026-07-19)
- [ ] `insert-refactor-skip-tests.md` updated — remove donor-email skip; confirm PIVOT class is not on skip list (`SqlEventWalkerPivotUnpivotTests` is 62/62 green as of Jul 2026).
- [ ] Phase 11 EXCEPT deferral row marked ✅ and moved to Phase 13 completion notes.
- [ ] Phase 11 donor-email TODO B row marked ✅ when 13.4 lands.

### Phase 13 execution order

```
13.4 (forward alias — smallest user-visible defect, gate probe already exists)
  → 13.3 (UPDATE RETURNING — mirrors DELETE RETURNING pattern)
  → 13.1 (EXCEPT parity — extends existing set-op finalizer)
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
| `mergeSelectListQualifiedQueryAliasRefsIntoSourceQueryDictionary` | Post-hoc select-list merge into source `queryN` dict | **Restored for V13** (Jul 2026) — re-evaluate retire after clause egress emits alias+substitution tokens natively |
| `materializeResolvedUnqualifiedReference` query-backed early return | Wrong dictionary target | Resolution writes correct scope key in one pass |
| `moveEntriesToSingleTableIfSingleTarget` | Last-chance single-table relocation | Single-source scopes resolve at exit |
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

### Load-bearing fallbacks — do NOT delete until Phase 9 close

| Method | Why still needed | Retire trigger |
|--------|------------------|----------------|
| `mergeSelectListQualifiedQueryAliasRefsIntoSourceQueryDictionary` | Query dict alias tokens for qualified select-list refs | Clause egress emits tokens natively at walk time |
| `backfillQueryDictionaryFromResolvedInterfaceSources` (per-column) + `sweepBackfillQueryDictionaryFromResolvedInterfaceSources` | Interface-resolved physical columns → query dict after single-table relocation / late materialization | Native walk-time token capture at interface validation |
| ~~`materializeResolvableGlobalQualifiedUnresolvedLocations`~~ | ~~Late global qualified materialization~~ | **Retired Jul 2026** — unified `resolveQualifiedUnresolvedEntries` on `globalQualifiedUnresolvedLocations` |
| `resolveRelationalModifierDerivedColumnsFromUnresolvedMap` ×2 | Pre-wildcard + post-UPDATE-rhs derived-column stripping | Unified ingress skips derived before wildcard/single-table paths |
| `moveEntriesToSingleTableIfSingleTarget` | Single-source unqualified relocation | Scope exit resolves all unqualified in one pass |
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
  backfill consolidated: per-column at interface RESOLVED_PHYSICAL_SOURCE + sweepBackfill after late materialization
  derived-column stripping 3→2 (retired post-late-resolution pass)
  canaries green; UPDATE CTE spot checks same stale-golden failures as pre-retirement baseline

Step 5 — Phase 9 start (enables more retirement)                   ✅ DONE (Jul 2026)
Step 6 — Phase 10 close + Phase 9 dual-probe simplification        ✅ DONE (Jul 2026)

Full-suite verification (2026-07-19): mvn test → 1203/1203 pass.

Optional follow-up (not test-blocking): retire mergeSelectList hook, moveEntriesToSingleTableIfSingleTarget, dead-code candidates in helper.
```

**Do not start with:** DML golden bulk update, CTE redesign, PIVOT/UNPIVOT golden bulk refresh, or removing `mergeSelectListQualifiedQueryAliasRefsIntoSourceQueryDictionary` (V13 depends on it).

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

Document: `parse/documents/insert-refactor-skip-tests.md` (**stale as of Jul 2026** — PIVOT class is green; donor-email defect tracked in **Phase 13.4**)

- ~~15 × `SqlEventWalkerPivotUnpivotTests`~~ — **resolved** (62/62 pass; do not skip)
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

**Moved to Phase 13 (start after consolidation closeout):**

- EXCEPT set-operation parity → Phase 13.1
- Postgres INSERT completion → Phase 13.2
- UPDATE RETURNING walker → Phase 13.3
- Same-select-list forward alias (donor-email) → Phase 13.4
- DDL option detail parsing (optional) → Phase 13.5
- SQL statement generator round-trip → Phase 13.6

---

## Suggested execution order

```
Phases 1–4 ✅  →  5 (V1 delta)  →  6 (one convert)  →  7 (query finalize)
       →  8 (egress helper + canary)  →  9 (clause probe)  →  10 (context_list)
       →  11 (DML + fallback retirement)  →  12 (optional origin-CTE backfill)
       →  13 (language feature gap closure — EXCEPT, Postgres INSERT, UPDATE RETURNING,
              forward alias, optional DDL detail, SQL generator)
```

Phases 6–7 and 8 can overlap carefully (same files); prefer **6 before 8** so the egress helper targets one convert implementation.

If Phase 12 is ever taken, do it only after Phase 11 is fully complete and the Phase 11 canary set is still green; keep it as an opt-in cleanup step rather than part of the default execution order.

**Phase 13 starts when ready** (Phases 9–12 test closeout complete as of 2026-07-19: 1203/1203 full suite, 181/181 gate).

---

## Prompt to paste into a new agent session

```
We are continuing symbol-table resolution consolidation for the SQL parse walker.

Read parse/documents/smoketest-quality-gate-worklist.md first — especially:
- Progress dashboard (Jul 2026)
- Shortest path to dead-code removal
- Published scope vs global dictionary rules
- Phase 8 unified resolver (RESOLVED_DERIVED_COLUMN, isPhysicalTableRefVisibleInScope)

Current state (commit pending on Spring-2026-Extensions):
- Phases 1–4 done; **Phase 6 done**; **Phase 8 done**; **Phase 7 audit done** (~95% — golden spot-review remains); Phase 11 started (V9/V13 DML canaries pass).
- Phase 8 canaries GREEN: nestedQueryDemoTest (3 fatals), nestedQueryDemoWithCteTest, V9, V13.
- Phase 8 late-pass retirement complete: global qualified ingress unified; backfill consolidated; derived stripping 3→2.
- Unified qualified resolver handles PIVOT/UNPIVOT derived columns (RESOLVED_DERIVED_COLUMN).
- Retired emitExplicitQualifiedUnknownDiagnostics query-dict containsKey(columnName) shortcut.
- Physical materialization gated on visible scope (isPhysicalTableRefVisibleInScope) — no global-dict sibling leaks.
- ~82/95 DML stale goldens — **resolved** (103/103 DML class green, 2026-07-19)
- Query column dictionary alias tokens ('a', 'e', 'inner_sq') are accepted canonical form.

Your mission this session — follow "Shortest path to dead-code removal" in order:

1. Phase 9 start: single `validateArchivedClauseColumnRef` tree at scope exit; retire second `assignTableRefsForColumnReferenceList` pass on filters/groupby/orderby.
2. Re-run canaries after each Phase 9 change.

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
