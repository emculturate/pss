# Symbol Table Resolution Consolidation — Ultimate Worklist

Use this document as the single handoff for consolidating column resolution in the SQL parse walker and `SqlParseSymbolTreeHelper`. It merges planning from:

- `def-query-canonicalization-phases1-4-checklist.md` (Phases 1–4, **done**)
- Qualified-column egress unification (`nestedQueryDemoTest` canary)
- Clause-list / `convertSymbolTableToTableDictionary` consolidation thread
- INSERT/VALUES and DML parity notes (where they touch shared resolution)

**Last updated:** 2026-07-02

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

**Known follow-up from Phase 4 verification:** `unaliasedDerivedSimpleAllOuterClausesV1Test` — in `def_query3` filters, expected `table_ref=query0` vs actual `table_ref=null` for correlated `col1`. Decide intended behavior before Phase 5.

Detail and patch chunks: see `def-query-canonicalization-phases1-4-checklist.md`.

---

### Phase 5 — Close def_query canonicalization gaps

**Goal:** Finish the read-path and snapshot behavior started in Phases 1–4 before larger refactors.

| Task | Notes |
|------|-------|
| Resolve V1 `table_ref` delta | Correlated outer ref in nested set-op filter list |
| Expand verification | Same narrow pair → nearby unaliased-derived V2–V16, union/intersect FROM shapes |
| Document `queryN` vs `def_queryN` contract | When live handle vs published definition; what consumers must use |

**Gate:** V1/V7 (or agreed subset) pass without regressions on anonymous FROM registration.

---

### Phase 6 — One canonical `convertSymbolTableToTableDictionary`

**Goal:** Single implementation in `SqlParseSymbolTreeHelper`; all exit handlers delegate.

| Task | Notes |
|------|-------|
| Move walker copy → helper (or delete dead helper copy) | Walker currently owns live ~500-line block; helper duplicate has zero call sites |
| Parameterize DML differences | UPDATE `updateTargetTableRef`, DELETE target preference — flags, not forks |
| `exitJoin_extension_primary` | Mid-FROM re-resolution after lateral/PIVOT join — name/document (`reconcileJoinExtensionSymbolTable`), not a scope publish |

**Gate:** Full parse test suite minus known skip list (15 PIVOT AST + 1 donor-email sample); no intentional golden churn.

---

### Phase 7 — Uniform query scope finalization (`exitQuery_specification`)

**Goal:** Leaf SELECT / CTE body / insert-source SELECT use the same exit shape as VALUES.

| Task | Notes |
|------|-------|
| `finalizeQueryScopeSymbolTable` | Already partially landed — ensure it owns convert + export + publish |
| Replace inline logic in `exitQuery_specification` | Bubble/defer flags as parameters, not inline special cases |
| Align UNION/INTERSECT | `finalizeSetOperationScopeSymbolTable` already parallel to VALUES |

**Gate:** Subquery and CTE test classes; expect symbol-table golden drift — review before bulk update.

---

### Phase 8 — Unified qualified/unqualified egress helper

**Goal:** One `resolveQualifiedUnresolvedEntries` (or equivalent) used everywhere egress runs.

| Step | Description |
|------|-------------|
| Extract helper | defer / materialize-to-**global** dict / fatal |
| Refactor `retryResolvableQualifiedUnresolvedEntries` | Delegate first |
| Align `emitQualifiedSourceNotFoundFatals(InScope)` | Remove “column must already exist in physical table dict” gate |
| Wire hooks | `exitFrom_clause`, `publishQueryLikeScope`, `exitPredicateSubqueryFrame`, top-level `finalizeQueryScopeSymbolTable` |
| Select-list qualified refs | In convert path: materialize `tab1.col` when table visible, not validate-only |

**Canary:** `SqlEventWalkerCoreSelectFromAliasingTests#nestedQueryDemoTest`

- Exactly **2 fatals:** `tab2.e3` (line 6 col 61), `gg.y` (line 7 col 23)
- Global **`tab1`** dict: `t`, `<y_col>`, `a`, `x`, `<z_col>`, `<w_col>` with correct token line/col

Handoff detail: `parse/docs/qualified-column-table-dict-handoff-prompt.md`

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

**Do not:** merge clause tokens into `query_dictionary` (by design); `filters` remains the clause signal.

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

---

## Fallback retirement tracker (Phase 11+)

Retire only after Phases 6–8 make scope exits self-contained:

| Fallback | Why it exists | Retire when |
|----------|---------------|-------------|
| `mergeSelectListQualifiedQueryAliasRefsIntoSourceQueryDictionary` | Post-hoc select-list merge | Clause refs captured correctly at scope exit |
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

Validation focus:
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
