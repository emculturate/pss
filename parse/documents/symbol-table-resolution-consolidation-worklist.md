# Symbol Table Resolution Consolidation — Ultimate Worklist

Use this document as the single handoff for consolidating column resolution in the SQL parse walker and `SqlParseSymbolTreeHelper`. It merges planning from:

- `def-query-canonicalization-phases1-4-checklist.md` (Phases 1–4, **done**)
- Qualified-column egress unification (`nestedQueryDemoTest` canary)
- Clause-list / `convertSymbolTableToTableDictionary` consolidation thread
- INSERT/VALUES and DML parity notes (where they touch shared resolution)

**Last updated:** 2026-07-09 (quality gate expanded to 104 tests; full scalar subquery V1–V9 matrix + subquery semantics probes in gate)

---

## Quality gate (run before every consolidation change)

**104 tests** — all must pass before merging consolidation work. Implemented in `SymbolTableResolutionConsolidationTestSuite` and runnable via Maven profile `symbol-table-resolution-consolidation`.

```bash
cd parse
mvn -Psymbol-table-resolution-consolidation test
# equivalent:
mvn -Dtest=sql.walker.SymbolTableResolutionConsolidationTestSuite test
```

| Group | Count | Class | Methods |
|-------|-------|-------|---------|
| Nested demo queries | 2 | `SqlEventWalkerCoreSelectFromAliasingTests` | `nestedQueryDemoTest`, `nestedQueryDemoWithCteTest` |
| Correlated scalar predicand | 16 | `SqlEventWalkerCoreSelectFromAliasingTests` | … plus middle-CTE trio: `correlatedScalarPredicandMiddleCteReferencesFirstCteTest` (resolve), `correlatedScalarPredicandMiddleCteUnqualifiedColumnDiagnosticLocationTest`, `correlatedScalarPredicandMiddleCteQualifiedMissingColumnDiagnosticLocationTest` |
| Correlated IN subquery | 8 | `SqlEventWalkerCoreSelectFromAliasingTests` | `correlatedInSubqueryNestedJoinSubqueryTest` … `correlatedInSubqueryNestedCteWithOuterRefTest` |
| Correlated EXISTS subquery | 5 | `SqlEventWalkerCoreSelectFromAliasingTests` | `correlatedExistsSubqueryNestedJoinSubqueryTest` … `correlatedExistsSubqueryFinalQueryReferencesCteChainTest` |
| DML UPDATE V1–V14 | 14 | `SqlEventWalkerDmlUpdateInsertDeleteTruncateTests` | `updateDictionaryHandling*` V1–V12; `updateFromNestedSubqueryDepth2CorrelatedTargetQualifiedColumnV13`; `updateFromNestedSubqueryDepth3CorrelatedTargetQualifiedColumnV14` |
| DML INSERT V1–V7 | 7 | `SqlEventWalkerDmlUpdateInsertDeleteTruncateTests` | `insertValuesPlainMatrixNoTargetColumnsV1` … `insertValuesSourceNamedColumnsAndAliasV7` |
| Unaliased derived V1–V16 | 16 | `SqlEventWalkerSubqueriesAndClauseSemanticsTests` | `unaliasedDerivedSimpleAllOuterClausesV1Test` … `unaliasedDerivedFlattenInnerSelectAllOuterClausesV16Test` |
| CTE unqualified column refs CTEV1–CTEV15 | 15 | `SqlEventWalkerSubqueriesAndClauseSemanticsTests` | `selectWithMultipleSimpleUnqualifiedReferencesCTEV1`, CTEV2, `queryAndUnionUnqualifiedReferencesCTEV3`, `unionAndQueryUnqualifiedReferencesCTEV4`, `queryAndIntersectUnqualifiedReferencesCTEV5`, `intersectAndQueryUnqualifiedReferencesCTEV6`, `unionAndIntersectUnqualifiedReferencesCTEV7`, `intersectAndUnionUnqualifiedReferencesCTEV8`, `unionAndValuesUnqualifiedReferencesCTEV9`, `valuesAndIntersectUnqualifiedReferencesCTEV10`, `valuesAndValuesUnqualifiedReferencesCTEV11`, `queryAndSubstitutionUnqualifiedReferencesCTEV12`, `substitutionAndQueryUnqualifiedReferencesCTEV13`, `substitutionAndSubstitutionUnqualifiedReferencesCTEV14`, `sameTableDifferentSchemaUnqualifiedReferencesCTEV15` |
| Scalar subquery symbol-table matrix | 10 | `SqlEventWalkerSubqueriesAndClauseSemanticsTests` | `scalarSubqueriesSymbolTableTestV1` (SELECT predicand + WHERE IN), `scalarSubqueriesSymbolTableTestV2` (JOIN ON), `scalarSubqueriesSymbolTableTestV3` (GROUP BY + HAVING scalar), `scalarSubqueriesSymbolTableTestV4` (GROUP BY scalar predicand), `scalarSubqueriesSymbolTableTestV5` (ORDER BY), `scalarSubqueriesSymbolTableTestV6` (QUALIFY), `scalarSubqueriesSymbolTableTestV7` (WHERE scalar), `scalarSubqueriesSymbolTableTestV8` (WHERE EXISTS), `scalarSubqueriesSymbolTableTestV9` (QUALIFY EXISTS), `scalarSubqueriesCorrelatedSubquerySymbolTableTest` |
| Production scalar / EXISTS probes | 4 | `SqlEventWalkerSubqueriesAndClauseSemanticsTests` | `selectWhereScalarConditionCorrelatedSubquery`, `selectOrderByScalarCorrelatedSubquery`, `selectWhereVariableExists`, `selectWhereExistsCorrelatedSubquery` |
| Nested formula subqueries | 1 | `SqlEventWalkerSubqueriesAndClauseSemanticsTests` | `nestedFormulaSubqueriesUseQueryRefsInInterfaceAndFiltersTest` |
| Subquery semantics probes | 6 | `SqlEventWalkerSubqueriesAndClauseSemanticsTests` | `queryOverQueriesSingleWildcardResolvesUnqualifiedColumn`, `selectSameSubqueriesTest`, `havingExistsCorrelatedSubqueryTest`, `havingScalarSubqueryComparisonTest`, `selectWithUnionTest`, `multipleScalarAndOtherSubqueriesSymbolTableTest` |

**Nested demo fatal expectations (unchanged):**

- `nestedQueryDemoTest` — exactly **3** fatals (`tab2.e3`, `gg.y`, `tt.f`)
- `nestedQueryDemoWithCteTest` — exactly **2** fatals (same minus `gg.y`; CTE resolves `gg.y`)

**Not in the gate (stale golden backlog — do not treat failures here as regressions until reviewed):**

- `insertDictionaryHandling*` V1–V7 (parallel to UPDATE dictionary-handling series; query-dict / symbol-tree goldens stale)
- Remaining DML tests beyond UPDATE V1–V14 and INSERT VALUES V1–V7
- UPDATE CTE substitution spot checks U3/U4/U5/U7/U9 (stale interface / fatal expectations)
- Set-operation interface validation V1–V5 (formerly in consolidation suite)
- PIVOT/UNPIVOT tests (see skip list)
- Remaining `correlated*` tests **not in gate** (7 as of Jul 2026): last-CTE / final-query / IN-EXISTS middle-CTE chain cases still pending Phase 10 `context_list` review
- `SqlEventWalkerCoreSelectFromAliasingTests` beyond gate canaries (~61 stale goldens — see Phase 7 backlog table below)

**Gate status (Jul 2026):** **104/104 passing** as of last verification (includes full scalar subquery V1–V9 matrix, production scalar/EXISTS probes, and subquery semantics probes).

---

## Phase 7 golden backlog (review before next code step)

Tests from the Phase 7 inventory that **fail** today. Review case-by-case; update goldens when behavior is confirmed — no bulk refresh.

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

### Correlated IN/EXISTS middle-CTE chain (`SqlEventWalkerCoreSelectFromAliasingTests`) — 7 failing (Phase 10)

All fail: expected `UNQUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIASES` fatal not found at golden position.

| Test |
|------|
| `correlatedInSubqueryMiddleCteReferencesFirstCteTest` |
| `correlatedInSubqueryLastCteReferencesPriorCtesTest` |
| `correlatedExistsSubqueryMiddleCteReferencesFirstCteTest` |
| `correlatedExistsSubqueryLastCteReferencesPriorCtesTest` |
| `correlatedExistsSubqueryUnionContextTest` |
| `correlatedExistsSubqueryIntersectContextTest` |
| `correlatedExistsSubqueryNestedCteWithOuterRefTest` |

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

**Total backlog: 73 failing tests.**

---

## Progress dashboard (Jul 2026)

| Phase | Status | % | Gate / notes |
|-------|--------|---|--------------|
| **1–4** def_query canonicalization | ✅ Done | 100% | Commit `b59688c` |
| **5** def_query read-path gaps | ✅ Done | V1–V16 unaliased-derived green; symbol-tree + query-dict goldens aligned (Jul 2026) |
| **6** one `convertSymbolTableToTableDictionary` | ✅ Done | 100% | Audit Jul 2026: single helper impl; `reconcileJoinExtensionSymbolTable` for mid-FROM; dead `explicitTableRefByColumn` removed |
| **7** uniform query scope finalization | ⚠️ Near done | ~85% | `finalizeQueryScopeSymbolTable` / set-op / VALUES aligned; CTE-body inline-fork audit open |
| **8** unified egress helper | ✅ Done | 100% | Late-pass helpers retired/consolidated; global qualified ingress now uses `resolveQualifiedUnresolvedEntries`; backfill folded into interface loop + final sweep |
| **9** clause-list validation (no parallel pipelines) | ⚠️ Near done | ~85% | `validateArchivedClauseColumnRef` + convert + archived-scope-tree probes; retired `collectClauseColumnsIntoUnresolved` ingress path |
| **10** downward `context_list` resolution | ❌ Not started | 0% | `resolveVisibleOuterDeferredUnresolved` still identity; **close includes Phase 9 single-probe reassessment** (`isExistingArchivedClauseColumnRefSatisfied`, `materializeResolved`) |
| **11** DML parity + fallback retirement | ⚠️ Started | ~25% | **V9 + V13 canaries pass**; `finalizeInsertScopeSymbolTable` exists; ~82/95 DML tests have stale goldens |

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

**Active blockers before fallback retirement:**

1. ~~Phase 8 late-pass helper audit~~ ✅ Done (Jul 2026)
2. Stale golden backlog — do not treat as behavior bugs until reviewed case-by-case (~82/95 DML, V4–V16 unaliased-derived, ~60 PIVOT/UNPIVOT table/query dict goldens pre-date current behavior)

**Suggested next focus:** Phase 9 close → Phase 10 `context_list` + single-probe reassessment (retire `isExistingArchivedClauseColumnRefSatisfied` if one probe per scope is achievable).

---

## Published scope vs global dictionary — detailed rules

These rules govern acceptable variance between **live global maps** (`queryColumnDictionaryMap` / live `queryN` keys) and **published symbol-tree payloads** (`def_queryN` submaps). Canonical examples: `unaliasedDerivedSimpleAllOuterClausesV1Test` (`def_query5`, `def_query2`, `def_query3`).

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

### Phase 7 — Uniform query scope finalization (`exitQuery_specification`) (~95% done)

**Goal:** Leaf SELECT / CTE body / insert-source SELECT use the same exit shape as VALUES.

| Task | Status | Notes |
|------|--------|-------|
| `finalizeQueryScopeSymbolTable` | ✅ | Owns convert + export + publish; walker `exitQuery_specification` delegates |
| Replace inline logic in `exitQuery_specification` | ✅ | Walker only assembles clause submaps, then delegates |
| Align UNION/INTERSECT | ✅ | `finalizeSetOperationScopeSymbolTable` parallel to VALUES |
| CTE body / insert-source SELECT audit | ✅ | Static audit Jul 2026 — see **Phase 7 static audit** below |
| Predicate frames | ⏸️ | IN/EXISTS/predicand stay on `exitPredicateSubqueryFrame` merge/lift — **not** full finalize (Phase 10) |

**Close Phase 7 when:**

1. Every non-predicate leaf SELECT exit routes through `finalizeQueryScopeSymbolTable` (or set-op/VALUES equivalent). ✅
2. No inline convert+publish blocks remain outside helper finalizers. ✅
3. Subquery + CTE spot tests reviewed; symbol-table golden updates case-by-case only. ⏸️ (golden backlog, not blocking audit)

**Gate:** Subquery and CTE test classes; expect symbol-table golden drift — review before bulk update.

#### Phase 7 static audit (Jul 2026)

**Method:** Grep all `convertSymbolTableToTableDictionary`, `publishQueryLikeScope`, and walker `symbolTreeHelper.finalize*` call sites. No test run required for this step.

**`convertSymbolTableToTableDictionary` — 4 helper call sites only (no walker duplicate):**

| Call site | Role |
|-----------|------|
| `finalizeQueryScopeSymbolTable` | Leaf SELECT / CTE body / insert-source inner SELECT / FROM subquery / predicate inner SELECT |
| `finalizeUpdateScopeSymbolTable` | UPDATE scope publish |
| `finalizeDeleteScopeSymbolTable` | DELETE scope publish |
| `reconcileJoinExtensionSymbolTable` | Mid-FROM partial reconcile only — **not** scope publish (Phase 6) |

**`publishQueryLikeScope` — 6 helper call sites only:**

| Call site | Calls `convert`? |
|-----------|------------------|
| `finalizeQueryScopeSymbolTable` | Yes (before publish) |
| `finalizeSetOperationScopeSymbolTable` | No — branch SELECTs already converted at `exitQuery_specification` |
| `finalizeUpdateScopeSymbolTable` | Yes |
| `finalizeDeleteScopeSymbolTable` | Yes |
| `finalizeInsertScopeSymbolTable` | No — INSERT wrapper only; source SELECT converted at leaf exit |
| `finalizeValuesScopeSymbolTable` | No — VALUES has no `table_dictionary` convert path by design |

**Walker finalizer routing (all leaf SELECT bodies):**

| Walker exit | Helper | Notes |
|-------------|--------|-------|
| `exitQuery_specification` | `finalizeQueryScopeSymbolTable` | Single canonical leaf-SELECT exit — CTE body, FROM subquery, insert-source inner SELECT, predicate inner SELECT |
| `exitUnionized_query` / `exitIntersected_query` | `finalizeSetOperationScopeSymbolTable` | When union/intersect clause present; else `popFrameAndMergeIntoParent` |
| `exitValues_statement_primary` / `exitInsert_values_statement` | `finalizeValuesScopeSymbolTable` | Standalone VALUES tuple scopes |
| `exitUpdate_expression` / `exitDelete_expression` | `finalizeUpdateScopeSymbolTable` / `finalizeDeleteScopeSymbolTable` | DML |
| `exitInsert_expression` | `finalizeInsertScopeSymbolTable` | Statement wrap |
| `exitInsert_source_primary` | `finalizeInsertSourceAtPrimaryExit` + `popFrameAndMergeIntoParent` | Metadata wrap only — inner SELECT already finalized |
| `exitFrom_clause` (join extension) | `reconcileJoinExtensionSymbolTable` | Mid-FROM reconcile, not publish |
| IN / EXISTS / predicand | `exitPredicateSubqueryFrame` | Phase 10 — merge/lift, not full finalize |

**Documented inline publish forks (post-finalize / repack — none call `convert`):**

| Fork | Walker / helper entry | Classification |
|------|----------------------|----------------|
| **CTE / FROM query hoist** | `collectQuerySymbolTable` ← `exitWith_list_item`, `registerQueryLikeFromSource`, `exitTuple_source_primary` | **Intentional.** Removes already-published `def_queryN`, wires alias + `context_list` / table_alias, re-puts `def_queryN`. Convert ran at prior `exitQuery_specification`. |
| **WITH main-body collapse** | `exitWith_query` | **Intentional.** Merges `context_list` into main-body `def_queryN`, rebuilds outer symbol-table frame via direct `symbolTable.put`. Body already finalized by leaf finalizer. |
| **Insert-source def promotion** | `finalizeInsertSourceAtPrimaryExit` | **Intentional.** Promotes live key → `def_*`, optional `finalizeSetOperationAtExit` for set-op insert source. No convert. |
| **INSERT statement wrap** | `finalizeInsertScopeSymbolTable` | **Intentional.** `publishQueryLikeScope` only — source SELECT converted at `exitQuery_specification`. |
| **Aliased inline VALUES in FROM** | `wrapValuesScopeAsDefinition` ← `exitTable_source_primary` | **Intentional.** Lightweight `def_valuesN` promotion when VALUES enters via `exitValues_statement` (not `values_statement_primary`). Full `finalizeValuesScopeSymbolTable` runs on `values_statement_primary` / `insert_values_statement` paths. |
| **Set-op root publish** | `finalizeSetOperationScopeSymbolTable` | **Canonical.** Interface merge + publish; each branch leaf SELECT uses `finalizeQueryScopeSymbolTable` first. |
| **Predicate frame close** | `exitPredicateSubqueryFrame` | **Phase 10 defer.** Inner SELECT still uses `finalizeQueryScopeSymbolTable`; frame exit is merge/lift only (documented at helper ~11644). |
| **Mid-FROM join reconcile** | `reconcileJoinExtensionSymbolTable` | **Intentional partial convert** (Phase 6) — not a publish fork. |

**CTE body trace (confirmed clean):**

1. CTE body `enterQuery_specification` → `pushSymbolTableWithParentVisibleScope`
2. CTE body `exitQuery_specification` → `finalizeQueryScopeSymbolTable` (convert + export + `publishQueryLikeScope`)
3. `exitWith_list_item` → `collectQuerySymbolTable(..., cteListSymbols)` — hoist + `finalizeCteScopeDeferredUnresolved` only

**Insert-source SELECT trace (confirmed clean):**

1. `enterInsert_source_primary` → push frame
2. Inner `exitQuery_specification` → `finalizeQueryScopeSymbolTable` (convert preserves `TEMP_INSERT_SOURCE_SELECT_SEQUENCE_KEY` when in insert-source stack)
3. `exitInsert_source_primary` → `finalizeInsertSourceAtPrimaryExit` + `popFrameAndMergeIntoParent`

**Audit conclusion:** No walker-owned convert or inline convert+publish blocks. All documented forks are post-finalize registration, WITH-scope collapse, or statement-wrapper publish without convert. **No code changes required from this audit.** Remaining Phase 7 work is golden spot-review only (CoreSelect backlog; CTEV1–CTEV15 + full scalar subquery matrix + semantics probes complete in gate).

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
| Select-list qualified refs | ⚠️ | `mergeSelectListQualifiedQueryAliasRefsIntoSourceQueryDictionary` restored for V13; native clause egress still TODO (Phase 9) |
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

### Phase 9 — Clause-list validation without separate resolution pipelines (~85% done)

**Goal:** `filters`, `grouped_by`, `ordered_by` validated through the same visible-scope rules at scope exit — not early per-clause resolution.

| Task | Status | Notes |
|------|--------|-------|
| `SCOPE_CLAUSE_COLUMN_LIST_KEYS` | ✅ | `filters`, `grouped_by`, `ordered_by` |
| Single `validateArchivedClauseColumnRef` decision tree | ✅ | Skip query-alias refs; GROUP/ORDER require output-column proof; filters allow physical-table dict keys |
| `probeArchivedScopeClauseColumns` at convert exit | ✅ | Materializes + binds `table_ref`; UPDATE assignment RHS uses filters policy |
| `probeArchivedScopeClauseColumnsOnScopeTree` | ✅ | Wired into `finalizeScopeDeferredUnresolved` with `materializeResolved=false` |
| Retire stacked skip guards | ✅ | `isExistingArchivedClauseColumnRefSatisfied` short-circuits already-bound refs — **re-assess retire in Phase 10** (idempotency for dual probe; may become dead) |
| Retire `collectClauseColumnsIntoUnresolved` ingress | ✅ | Deleted — clause lists no longer collected into `unresolved_column` |
| DML clause probe audit | ✅ | UPDATE/DELETE/SELECT all route through `convertSymbolTableToTableDictionary` probe |
| Supplementary gate goldens | ✅ | Full scalar subquery V1–V9 matrix + correlated + HAVING/UNION/wildcard semantics probes green in gate; remaining predicand/union `def_queryN` prefix drift is CoreSelect backlog only |

**Note:** Outer/current-scope `query_dictionary` **does** include clause token strings (Phase 5+); nested published `def_*` children are not retroactively updated. `filters` / `grouped_by` / `ordered_by` remain the semantic `table_ref` signal per scope.

**Gate:** Predicand four-scenario tests, plain-union branch outer fatal, correlated scalar + CTE GROUP BY tests; full scalar subquery symbol-table matrix (V1–V9 + correlated); production WHERE/ORDER BY scalar + EXISTS probes; HAVING scalar/EXISTS probes; nested-formula `{query=queryN}` interface/filters test; query-over-queries wildcard resolution; top-level UNION; mixed multi-scalar composition.

---

### Phase 10 — Downward resolution via `context_list`

**Goal:** Replace upward bubbling of outer-correlated refs with inherited visible scope (model after WITH `cte_list` / EXISTS enter).

| Task | Notes |
|------|-------|
| Generalize `cte_list` → `context_list` | WITH CTEs + parent FROM bindings for nested subqueries |
| `pushSymbolTableWithParentVisibleScope` | On predicand / IN / nested query enter (EXISTS already inherits CTE list) |
| Remove outer-correlated bubble path | `bubbleOuterCorrelatedUnresolvedToParentScope` for refs parent already knows |
| Strip stale `unresolved_column` from published `def_queryN` | Align with VALUES — no archived submaps on published payloads |
| Roll back clause-probe deferral patches | Symptom fixes from bubble model |
| **Re-assess Phase 9 dual clause probe** | After `context_list` lands — see **Close Phase 10 when** § below |

**Close Phase 10 when** (in addition to gate tests green):

1. Downward visible scope replaces upward bubble for outer-correlated refs.
2. **Single-probe reassessment (Phase 9 simplification):** Revisit whether `isExistingArchivedClauseColumnRefSatisfied`, `probeArchivedScopeClauseColumnsOnScopeTree`, and `materializeResolved` are still needed. Today they exist because clause lists can be probed twice (convert exit with `materializeResolved=true`, then scope-tree finalize with `materializeResolved=false` after deferred-unresolved work). After Phase 10, aim for **exactly one probe per scope** by one of:
   - **Option A:** Scope-tree pass probes **only** `table_ref=null` entries (annotation-only, no re-validation of already-bound refs).
   - **Option B:** Deferred resolution completes **before** the single convert probe so `finalizeScopeDeferredUnresolved` no longer needs a second clause pass.
   - **Option C:** Retire scope-tree clause probe entirely if `context_list` makes the convert-time probe sufficient at first finalize.
3. If reassessment succeeds: delete `isExistingArchivedClauseColumnRefSatisfied` and collapse `ArchivedClauseProbeContext.materializeResolved` (or remove the scope-tree probe caller). Re-run Phase 9 gate + predicand/union supplementary tests.

**Gate:** `nestedQueryDemoTest`, `nestedQueryDemoWithCteTest`, correlated predicand + union/`ua` tests, `subqueryParseTest` (siloed fatals must fire at statement boundary when appropriate).

---

### Phase 11 — DML parity and late-pass retirement (~25% done)

**Goal:** UPDATE / DELETE / INSERT use the same ingress + egress + publish patterns; delete redundant fallbacks.

| Task | Status | Notes |
|------|--------|-------|
| `finalizeUpdateScopeSymbolTable` / `finalizeDeleteScopeSymbolTable` | ✅ | Aligned — audit against Phase 8–9 helpers remains |
| `finalizeInsertScopeSymbolTable` | ✅ | Exists; `exitInsert_expression` delegates — audit parity with UPDATE/DELETE finalize |
| DML canaries V9/V13 | ✅ | Passing with alias-token query dict |
| DML golden refresh | ⚠️ | ~82/95 cases stale (see backlog below) |
| DML clause probe | ❌ | Target-table rules only where semantically required (UPDATE LHS, DELETE preference) |
| Retire late-pass fallbacks (as scopes self-contain) | ⚠️ | Phase 8 closed; `mergeSelectList` hook + backfill sweep + `moveEntriesToSingleTableIfSingleTarget` remain until Phase 9 |
| Donor-email forward alias (TODO B) | ⏸️ | Unqualified ref in `PARTITION BY` binds to earlier select-list alias — orthogonal track |

**INSERT note:** INSERT **source** resolves like SELECT; insert wrap only maps target columns. Orphan promotion to target table is **incorrect** for INSERT (removed in `0ec0b75`).

**Gate:** DML test class + `insertValues*` + orphan parity tests; full suite minus PIVOT/donor skip list.

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

#### Stale golden backlog (accepted alias-token query dict)

V9/V13 canaries are updated and passing. The rest of `SqlEventWalkerDmlUpdateInsertDeleteTruncateTests` (~82/95 cases as of Jul 2026) still expect pre-consolidation output:

| Stale pattern | Example expected → actual |
|---------------|---------------------------|
| Query dict column tokens | `emp_id=<381>` literal → `'a'`, `'e'`, `'src'` alias tokens |
| Query dict key ordering | Fixed key order → walk-order / merge-order |
| Symbol table scope keys | `insert1=` / `delete1=` → `def_insert1=` / `def_delete1=` with nested `def_queryN` |
| Symbol tree scope keys | Same `def_` prefix drift on published symbol tree |
| Interface lists | Substitution variable names → resolved physical column names |
| Table dict ordering | Column key order within table entries |

Refresh strategy: case-by-case as each DML variant is reviewed (same approach as V9/V13); do not bulk-update until the variant's behavior is confirmed. Core select tests with `<327>` in query dict (`SqlEventWalkerCoreSelectFromAliasingTests`, substitution predicate tests) may also drift when those paths are exercised under the restored merge hook.

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
| `resolveVisibleOuterDeferredUnresolved` | Identity placeholder | Phase 10 implements downward resolution |
| `isExistingArchivedClauseColumnRefSatisfied` + dual clause probe | Idempotency for convert + scope-tree passes (Phase 9) | Phase 10 single-probe reassessment — see Phase 10 close criteria |

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

Step 5 — Phase 9 start (enables more retirement)                   ⚠️ ~85% (Jul 2026)
  single validateArchivedClauseColumnRef tree at scope exit
  retire second assignTableRefsForColumnReferenceList pass on filters/groupby/orderby
  probeArchivedScopeClauseColumns replaces validateFilterReferences + clause location tracking
  archived-scope-tree probe in finalizeScopeDeferredUnresolved (CTE/UNION/INTERSECT)
  retired collectClauseColumnsIntoUnresolved ingress path

Step 6 — Phase 10 close + Phase 9 dual-probe simplification        ❌ NOT STARTED
  context_list downward resolution (primary Phase 10 work)
  re-assess: isExistingArchivedClauseColumnRefSatisfied still needed?
  target: one probe per scope — scope-tree pass only table_ref=null OR defer before convert probe
  candidate deletes: isExistingArchivedClauseColumnRefSatisfied, materializeResolved flag,
    possibly probeArchivedScopeClauseColumnsOnScopeTree if convert probe becomes sufficient

Blocked until later: mergeSelectList hook, moveEntriesToSingleTableIfSingleTarget,
resolveVisibleOuterDeferredUnresolved (Phase 10), DML golden bulk refresh.
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
mvn -Psymbol-table-resolution-consolidation test
```

### Supplementary spot checks (optional, not part of gate)

```bash
# UPDATE CTE substitution variants (stale golden backlog)
mvn test -Dtest=SqlEventWalkerDmlUpdateInsertDeleteTruncateTests#updateComplexSubstitutionU3WithCteIntersectOrderBySubstitution,updateComplexSubstitutionU4NestedWithInCteBody,updateComplexSubstitutionU5WithCteQualifyWindowSubstitution,updateComplexSubstitutionU7ChainedCteReferences,updateComplexSubstitutionU9WithCteSelfUnionBranches

# INSERT dictionary-handling V1–V7 (stale goldens — not gate)
mvn test -Dtest=SqlEventWalkerDmlUpdateInsertDeleteTruncateTests#insertDictionaryHandlingQualifiedColumnsFromWindowedSubqueryAndOrphanRhsV1,insertDictionaryHandlingQualifiedColumnsAcrossWhereSubclausesAndOrphanRhsV2,insertDictionaryHandlingUnqualifiedFallsBackToTargetTableV3,insertDictionaryHandlingUnqualifiedWithAdditionalPhysicalTableStillResolvesV4,insertDictionaryHandlingGroupByHavingSubqueryAndUnqualifiedRhsV5,insertDictionaryHandlingOrderBySubqueryAndUnqualifiedRhsV6,insertDictionaryHandlingQualifySubqueryAndUnqualifiedRhsV7

# Full DML class (many stale goldens beyond gate)
mvn test -Dtest=SqlEventWalkerDmlUpdateInsertDeleteTruncateTests
```

### Known skip list (unrelated failures)

Document: `parse/documents/insert-refactor-skip-tests.md`

- 15 × `SqlEventWalkerPivotUnpivotTests` (PIVOT AST shape)
- 1 × donor-email live sample (`source_partner_system_name` / PARTITION BY forward alias)

### Golden update policy

- **Keep** `assertFatalDiagnosticAtPosition` with full line + character checks
- Update token locations when deferral timing changes — expected, not regression
- **Do not** bulk-update goldens until phase gate tests pass and user reviews output

---

## Explicitly out of scope (unless regression forces it)

- PIVOT AST normalization (15 tests) — product fix, separate PR
- Donor-email select-list forward alias — Phase 11 optional / TODO B
- Scanning `filters`/`interface` after the fact to fix resolution
- Second unresolved bucket or parse-time materialize wrappers
- Routing predicate subqueries through full `finalizeQueryScopeSymbolTable` (breaks merge semantics)
- DELETE-logic refactor during Phases 1–4 (constraint on initial slice; revisit in Phase 11)

---

## Suggested execution order

```
Phases 1–4 ✅  →  5 (V1 delta)  →  6 (one convert)  →  7 (query finalize)
       →  8 (egress helper + canary)  →  9 (clause probe)  →  10 (context_list)
       →  11 (DML + fallback retirement)
```

Phases 6–7 and 8 can overlap carefully (same files); prefer **6 before 8** so the egress helper targets one convert implementation.

---

## Prompt to paste into a new agent session

```
We are continuing symbol-table resolution consolidation for the SQL parse walker.

Read parse/documents/symbol-table-resolution-consolidation-worklist.md first — especially:
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
- ~82/95 DML + V4–V16 unaliased-derived + ~60 PIVOT/UNPIVOT tests have stale goldens; do NOT bulk-update.
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
  mvn -Psymbol-table-resolution-consolidation test

Gate = 104 tests: nested demo (2), correlated scalar predicand (16), correlated IN (8), correlated EXISTS (5), UPDATE V1–V14 (14), INSERT VALUES V1–V7 (7), unaliased V1–V16 (16), CTE unqualified refs CTEV1–CTEV15 (15), scalar subquery symbol-table matrix V1–V9 + correlated (10), production scalar/EXISTS probes (4), nested formula subqueries (1), subquery semantics probes (6).
See "Quality gate" section at top of worklist for method names.

Out of scope this session:
- CTE behavior redesign, PIVOT/UNPIVOT golden bulk refresh, DML golden bulk refresh, removing mergeSelectListQualifiedQueryAliasRefsIntoSourceQueryDictionary.

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
