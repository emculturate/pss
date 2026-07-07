# Symbol Table Resolution Consolidation — Ultimate Worklist

Use this document as the single handoff for consolidating column resolution in the SQL parse walker and `SqlParseSymbolTreeHelper`. It merges planning from:

- `def-query-canonicalization-phases1-4-checklist.md` (Phases 1–4, **done**)
- Qualified-column egress unification (`nestedQueryDemoTest` canary)
- Clause-list / `convertSymbolTableToTableDictionary` consolidation thread
- INSERT/VALUES and DML parity notes (where they touch shared resolution)

**Last updated:** 2026-07-06 (Phase 8 canary fatal counts; Phase 6–8 closure criteria)

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
   convertSymbolTable → export query_dictionary → publishScopeSymbolTable
   ```

   VALUES is the reference implementation (`finalizeValuesScopeSymbolTable`).

6. **Minimize diff:** extend existing hooks; no second unresolved bucket, no `egressQualifiedUnresolvedMap`, no post-hoc interface scans.

7. **No new fallback logic without discussion:** do not add new fallback/recovery paths (especially recursive descent or global-map fallback scans) unless explicitly approved in-thread first.

8. **Grammar sequence over reconstruction:** tie behavior to natural grammar event ordering (`enter*`/`exit*`). If sequencing data is needed later, carry it forward in focused scope submaps created at the producer rule and consumed at deterministic downstream rules.

9. **Do not reconstruct lost sequencing context:** when data is absent at a consuming rule, treat it as a scope-finalization/publication bug to fix at source; do not compensate by reading nested child artifacts after the fact.

10. **Published vs global dictionaries:** frozen `def_queryN` snapshots vs live global `queryN` maps follow the rules in **Published scope vs global dictionary — detailed rules** (no child backpatch; grammar-owned token scope; qualified global push-down allowed).

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

### Phase 5 — Close def_query canonicalization gaps

**Goal:** Finish the read-path and snapshot behavior started in Phases 1–4 before larger refactors.

| Task | Notes |
|------|-------|
| Resolve V1 `table_ref` delta | Correlated outer ref in nested set-op filter list |
| Expand verification | Same narrow pair → nearby unaliased-derived V2–V16, union/intersect FROM shapes |
| Document `queryN` vs `def_queryN` contract | When live handle vs published definition; what consumers must use |
| Enforce strict payload lookup | No live-key fallback when reading published scope payloads; translate alias/live refs to `def_*` then resolve |

**Gate:** V1/V7 (or agreed subset) pass without regressions on anonymous FROM registration.

---

### Phase 6 — One canonical `convertSymbolTableToTableDictionary` (~90% done)

**Goal:** Single implementation in `SqlParseSymbolTreeHelper`; all exit handlers delegate.

| Task | Status | Notes |
|------|--------|-------|
| Move walker copy → helper | ✅ | Walker no longer owns a duplicate block; all call sites go through helper |
| Parameterize DML differences | ✅ | UPDATE `updateTargetTableRef`, DELETE target preference — flags on shared convert |
| `exitJoin_extension_primary` | ⚠️ | Still calls `convertSymbolTableToTableDictionary` directly for mid-FROM lateral/PIVOT reconcile — intentional, not a scope publish; rename/document as `reconcileJoinExtensionSymbolTable` |
| Dead-path audit | ❌ | Confirm no orphaned convert logic or parallel resolution forks remain in walker |

**Close Phase 6 when:**

1. `grep convertSymbolTableToTableDictionary` shows only helper definition + intentional call sites (finalize paths, join-extension reconcile, DML finalize).
2. Join-extension reconcile is named/documented so it is clearly **not** a second scope-publish path.
3. Full parse suite minus known skip list passes with **no new** golden churn beyond already-reviewed consolidation output.

**Gate:** Full parse test suite minus known skip list (15 PIVOT AST + 1 donor-email sample); no intentional golden churn.

---

### Phase 7 — Uniform query scope finalization (`exitQuery_specification`) (~85% done)

**Goal:** Leaf SELECT / CTE body / insert-source SELECT use the same exit shape as VALUES.

| Task | Status | Notes |
|------|--------|-------|
| `finalizeQueryScopeSymbolTable` | ✅ | Owns convert + export + publish; walker `exitQuery_specification` delegates |
| Replace inline logic in `exitQuery_specification` | ✅ | Walker only assembles clause submaps, then delegates |
| Align UNION/INTERSECT | ✅ | `finalizeSetOperationScopeSymbolTable` parallel to VALUES |
| CTE body / insert-source SELECT audit | ❌ | Confirm every leaf SELECT path uses the same finalizer (no inline publish forks) |
| Predicate frames | ⏸️ | IN/EXISTS/predicand stay on `exitPredicateSubqueryFrame` merge/lift — **not** full finalize (Phase 10) |

**Close Phase 7 when:**

1. Every non-predicate leaf SELECT exit routes through `finalizeQueryScopeSymbolTable` (or set-op/VALUES equivalent).
2. No inline convert+publish blocks remain outside helper finalizers.
3. Subquery + CTE spot tests reviewed; symbol-table golden updates case-by-case only.

**Gate:** Subquery and CTE test classes; expect symbol-table golden drift — review before bulk update.

---

### Phase 8 — Unified qualified/unqualified egress helper (~75% done)

**Goal:** One `resolveQualifiedUnresolvedEntries` (or equivalent) used everywhere egress runs.

| Step | Status | Description |
|------|--------|-------------|
| Extract unified egress helpers | ✅ | `resolveUnqualifiedColumnAgainstVisibleScope`, `resolveQualifiedColumnAgainstVisibleScope`, `applyQualifiedScopeResolutionAtBatchExit` |
| Retire `retryResolvableQualifiedUnresolvedEntries` | ✅ | Replaced by unified batch exit path |
| Align `emitQualifiedSourceNotFoundFatals` | ✅ | CTE handled in batch exit; consolidated fatal emit helpers |
| Wire hooks | ⚠️ | `finalizeQueryScopeSymbolTable`, `exitPredicateSubqueryFrame`, convert path — audit remaining ad-hoc egress |
| Select-list qualified refs | ⚠️ | Convert path materializes when visible; confirm no validate-only regressions |
| Retire redundant late-pass helpers | ❌ | Review `materializeResolvableGlobalQualifiedUnresolvedLocations` and explicit-unknown merge paths |

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

1. Nested demo canaries pass **both** fatal assertions and symbol-table goldens (reviewed, not bulk-updated).
2. UPDATE CTE spot checks pass: `updateComplexSubstitutionU3/U4/U5/U7/U9` in `SqlEventWalkerDmlUpdateInsertDeleteTruncateTests`.
3. V1–V3 consolidation canaries stay green; no new fallback readers added.
4. Remaining late-pass helpers either retired or documented as load-bearing with explicit rationale.

**Gate:** Canary + UPDATE CTE spot checks (U3/U4/U5/U7/U9) + union-branch correlated tests.

---

### Phase 9 — Clause-list validation without separate resolution pipelines

**Goal:** `filters`, `grouped_by`, `ordered_by` validated through the same visible-scope rules at scope exit — not early per-clause resolution.

| Task | Notes |
|------|-------|
| `SCOPE_CLAUSE_COLUMN_LIST_KEYS` | `filters`, `grouped_by`, `ordered_by` |
| Single `validateArchivedClauseColumnRef` decision tree | Skip query-alias refs; GROUP/ORDER require output-column proof; filters allow physical-table dict keys |
| `probeArchivedScopeClauseColumns` at scope exit | For refs that never entered live `unresolved_column` |
| Retire stacked skip guards | Replace sprawl from clause-probe experiments with one tree |

**Note:** Outer/current-scope `query_dictionary` **does** include clause token strings (Phase 5+); nested published `def_*` children are not retroactively updated. `filters` / `grouped_by` / `ordered_by` remain the semantic `table_ref` signal per scope.

**Gate:** Predicand four-scenario tests, plain-union branch outer fatal, correlated scalar + CTE GROUP BY tests.

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

**Gate:** `nestedQueryDemoTest`, `nestedQueryDemoWithCteTest`, correlated predicand + union/`ua` tests, `subqueryParseTest` (siloed fatals must fire at statement boundary when appropriate).

---

### Phase 11 — DML parity and late-pass retirement

**Goal:** UPDATE / DELETE / INSERT use the same ingress + egress + publish patterns; delete redundant fallbacks.

| Task | Notes |
|------|-------|
| `finalizeUpdateScopeSymbolTable` / `finalizeDeleteScopeSymbolTable` | Already aligned — audit against Phase 8–9 helpers |
| `finalizeInsertScopeSymbolTable` | Last major scope still inline in `exitInsert_expression` |
| DML clause probe | Target-table rules only where semantically required (UPDATE LHS, DELETE preference) |
| Retire late-pass fallbacks (as scopes self-contain) | See table below |
| Donor-email forward alias (TODO B) | Unqualified ref in `PARTITION BY` binds to earlier select-list alias — orthogonal track |

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
- `collectClauseColumnsIntoUnresolved` skips substitutions (`~8686`) — qualified filter refs use explicit-qualified egress instead.

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

---

## Scope finalization map (target end state)

```
convertSymbolTable → export query_dictionary → publishScopeSymbolTable
```

| Scope | Finalizer | Status |
|-------|-----------|--------|
| SELECT (`query_specification`) | `finalizeQueryScopeSymbolTable` | Partial — Phase 7 |
| VALUES | `finalizeValuesScopeSymbolTable` | ✅ Reference |
| UNION / INTERSECT | `finalizeSetOperationScopeSymbolTable` | ✅ |
| UPDATE | `finalizeUpdateScopeSymbolTable` | ✅ |
| DELETE | `finalizeDeleteScopeSymbolTable` | ✅ |
| INSERT wrap | `finalizeInsertScopeSymbolTable` | ❌ Phase 11 |
| Predicate (IN/EXISTS/predicand) | Merge frame + lift unresolved; **not** full finalize | Phase 10 |
| DDL | Bare pop + counter | OK — no column resolution |

---

## Test strategy

### Canary tests (behavior before golden bulk update)

```bash
cd parse
mvn test -Dtest=SqlEventWalkerCoreSelectFromAliasingTests#nestedQueryDemoTest
mvn test -Dtest=SqlEventWalkerCoreSelectFromAliasingTests#nestedQueryDemoWithCteTest
```

Expected fatals: **3** (`nestedQueryDemoTest`), **2** (`nestedQueryDemoWithCteTest` — same minus `gg.y`). Outer WHERE `c` must resolve to joined `tab1`, not emit a query-alias fatal.

### Phase 8 UPDATE CTE spot checks

```bash
cd parse
mvn test -Dtest=SqlEventWalkerDmlUpdateInsertDeleteTruncateTests#updateComplexSubstitutionU3WithCteIntersectOrderBySubstitution,updateComplexSubstitutionU4NestedWithInCteBody,updateComplexSubstitutionU5WithCteQualifyWindowSubstitution,updateComplexSubstitutionU7ChainedCteReferences,updateComplexSubstitutionU9WithCteSelfUnionBranches
```

### Unaliased derived table regression (Phases 1–5)

```bash
mvn test -Dtest=SqlEventWalkerSubqueriesAndClauseSemanticsTests#unaliasedDerivedSimpleAllOuterClausesV1Test
mvn test -Dtest=SqlEventWalkerSubqueriesAndClauseSemanticsTests#unaliasedDerivedUnionAllOuterClausesV7Test
```

### DML / INSERT spot checks (Phases 9–11)

```bash
mvn test -Dtest=SqlEventWalkerDmlUpdateInsertDeleteTruncateTests
# Optional script if present:
# ./tools/run-tests-insert-refactor.sh
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

Read parse/documents/symbol-table-resolution-consolidation-worklist.md (this file), then execute the
"Current narrowed execution plan (next implementation pass)" first.

Scope for this pass:
- In scope: non-DML SQL statement resolution cleanup and recursive fallback removal.
- Out of scope: CTE refactor, PIVOT/UNPIVOT refactor, and DML behavior changes.

Contract to preserve:
- Embedded scope payloads are canonicalized as def_*.
- Local alias maps and unaliased source refs remain queryN/valuesN/unionN/intersectN (or real alias/table names), not def_*.

Implementation guardrails:
- Never add new fallback logic without explicit conversation and approval.
- Prefer grammar-event-scoped submaps to carry data between rule exits instead of post-hoc readers.

Primary objective:
- Stop descending into nested child payloads to recover missing data; use immediate current-level published surfaces.
- Do not backpatch finalized def_queryN payloads when parent scopes resolve correlated refs (see "Published scope vs global dictionary — detailed rules").

Validation focus:
- Use `SqlEventWalkerSubqueriesAndClauseSemanticsTests` as the primary development driver for cross-subclause resolution unification, starting at the block:
   "UNALIASED DERIVED TABLE IN FROM/JOIN TESTS".
- Follow and preserve the V1-V16 sequence in that block as the canonical progression for SELECT/JOIN/WHERE/EXISTS/GROUP BY/HAVING/QUALIFY/ORDER BY behavior.
- When changing resolution logic, validate against V1-V16 first before widening to other suites.
- Lock V4 fatal set to the intended four diagnostics.
- Re-check V2/V4 set-op mismatch count/label parity.

Do not bulk-update unrelated goldens. Keep diffs minimal and focused.
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
