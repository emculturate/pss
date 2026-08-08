# Relational-modifier lineage consolidation — migration plan

Goal: one convert-egress path binds **every physical source site** (SELECT expressions, post-modifier clauses, pivot/unpivot operands) into `table_dictionary` / derivation buckets, without `query_dictionary` heuristics or bare-column special cases.

Reference tests:

| Test | Role |
|------|------|
| `pivotSelectAndPostClausesFormulaLineageMigrationTest` | PIVOT static IN + SELECT / WHERE / GROUP BY / ORDER BY formulas |
| `pivotInAnySelectExpressionOperandLineageMigrationTest` | PIVOT `IN (ANY ORDER BY …)` + same clause mix |
| `unpivotSelectAndPostClausesFormulaLineageMigrationTest` | UNPIVOT + SELECT / WHERE / GROUP BY / ORDER BY formulas |
| `ModifierLineageConsolidationContractTests` | `@Ignore` until M2–M4; enable per phase |

Golden refresh: `ModifierLineageMigrationGoldenCaptureOnce` (main).

---

## M0 — Baseline (done)

- [x] Reproduce `empid` gap with `pivotInAnyOrderByNonForColumnsWithWhereTest`.
- [x] Minimal bridge: interface loop + `query_dictionary` site index when `outputCol == columnName`.
- [x] Add migration tests + contract tests (ignored).

**Gate:** `SqlEventWalkerPivotUnpivotTests` green; contract class ignored.

---

## M1 — Stop discarding unresolved without materialize

**Problem:** `consumeUnresolvedColumnReferenceFromModifierScope` (walk-time, some derived operand paths) removes `unresolved_column` keys with no `table_dictionary` merge.

**Changes:**

1. Replace with `relocateUnresolvedOperandToTableDictionary(columnName, physicalTableRef)`:
   - `getUnqualifiedUnknownEntry` / qualified variants
   - `mergeSourceLineageIntoPhysicalTableDictionary` (or append tokens to `RELATIONAL_MODIFIER_SOURCE_COLUMNS` bucket)
   - then remove from unresolved
2. Audit callers (`recordRelationalModifierDerivedColumnToken` path and any similar).

**Tests:** Existing pivot/unpivot tests; add one focused test where walk-time consume previously dropped a SELECT site (if identifiable).

**Gate:** No behavior change on happy paths except new tokens where discard happened; full `SqlEventWalkerPivotUnpivotTests`.

---

## M2 — Reorder convert egress: interface lineage before operand drain

**Problem:** Phase 16.2 (`resolvePivotOperandColumnsFromUnresolvedMap`) runs **before** the interface loop and can consume shared unresolved buckets.

**Changes:**

1. Move `resolvePivotOperandColumnsFromUnresolvedMap` to **after** interface validation / lineage pass, **or**
2. Split unresolved: interface pass materializes per **interface dependency ref** (flattened list); phase 16.2 only drains **non-interface** stragglers (WHERE/GROUP BY/ORDER BY not yet processed).

**Tests:** Enable no contract tests yet; re-run migration tests; optional golden tweaks.

**Gate:** `SqlEventWalkerPivotUnpivotTests` + migration tests.

---

## M3 — Unify interface materialization (PIVOT + UNPIVOT operands)

**Problem:** `RESOLVED_PIVOT_OPERAND` / `RESOLVED_UNPIVOT_IN_SOURCE` branches `continue` without `materializeInterfaceOutputSourceLineage`.

**Changes:**

1. For each interface dependency ref (every `refIndex` in `localInterface` list, including expression dependencies):
   - Classify egress (operand vs physical vs derived).
   - **Always** call `materializeInterfaceOutputSourceLineage(materializeTableRef, columnName, refObjWithLocations, …)` when binding physical source lineage.
   - Operand classification only sets `materializeTableRef` / `table_ref` on interface entry.
2. Remove M0 `query_dictionary` fallback and `outputCol == columnName` gate.
3. Mirror for qualified interface refs in the same loop.

**Tests:**

- Remove `@Ignore` on `ModifierLineageConsolidationContractTests` (pivot IN ANY + static expression tests).
- Update goldens in migration tests for new SELECT sites (`empid` `1:7`, `sales_amount` `1:34`, etc.).

**Gate:** Contract tests green; migration tests updated; full pivot class.

---

## M4 — SELECT-list site tokens (optional but recommended)

**Problem:** `shouldCaptureClauseColumnSiteTokenForActiveColumnReference()` is false for most SELECT-list column refs, so interface `refObj` lacks `locations` unless unresolved still holds them.

**Changes (pick one):**

- **A.** Register clause-site tokens for column refs inside SELECT (respecting `OVER` exception), **or**
- **B.** At `exitSelect_item`, attach defining tokens from walk capture onto each flattened dependency ref in the interface column list.

**Tests:** UNPIVOT VALUE SELECT expression contract; expression-heavy SELECT items without relying on unresolved.

**Gate:** `unpivotValueOperandSelectExpressionSitesContractTest` green; review UNPIVOT derived vs physical policy (17.7.8).

---

## M5 — Cleanup and docs

- Delete dead branches (special pivot-operand interface `continue`, duplicate materialization in filter pass where redundant).
- Document invariant in `sql_walker_exit_method_gaps.md` / workplan: *operand classification ≠ skip lineage*.
- Run broader walker suites (`SqlEventWalkerJoinsAndTableResolutionTests` sample) if time permits.

---

## Design invariant (target end state)

```
Walk:     collect tokens → unresolved_column (all sites) + query_dictionary (output identity)
          modifier: record operand tokens in source_columns buckets (never blind remove)

Convert:  1) interface loop: for each output dependency ref → classify → materializeInterfaceOutputSourceLineage
          2) clause passes (WHERE/GROUP BY/ORDER BY): same materialize helper
          3) operand drain: merge buckets + consume only remaining unresolved stragglers
```

No `query_dictionary` as lineage fallback; no bare-name-only rules.

---

## Risk notes

- **Token order** in `table_dictionary` arrays may shift when merges happen earlier; prefer `assertTableDictionaryContainsAntlrSite` over brittle full-string goldens for new work.
- **Derived vs physical** (17.7.8): VALUE/FOR unpivot outputs must not pollute physical keys; lineage for VALUE may live on `derivation.derived_columns` while IN-list columns stay physical.
- **Multi-site same column**: merging must remain additive (`mergeResolvedColumnIntoDictionary`), never replace unless intentional.
