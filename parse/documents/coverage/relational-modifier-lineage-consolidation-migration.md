# Relational-modifier lineage consolidation — migration plan

Goal: one convert-egress path binds **every physical source site** (SELECT expressions, post-modifier clauses, pivot/unpivot operands) into `table_dictionary` / derivation buckets, without `query_dictionary` heuristics or bare-column special cases.

Reference tests:

| Test | Role |
|------|------|
| `pivotSelectAndPostClausesFormulaLineageMigrationTest` | PIVOT static IN + SELECT / WHERE / GROUP BY / ORDER BY formulas |
| `pivotInAnySelectExpressionOperandLineageMigrationTest` | PIVOT `IN (ANY ORDER BY …)` + same clause mix |
| `unpivotSelectAndPostClausesFormulaLineageMigrationTest` | UNPIVOT + SELECT / WHERE / GROUP BY / ORDER BY formulas |
| `ModifierLineageConsolidationContractTests` | Contract for pivot/unpivot SELECT + clause lineage (all active) |

Golden refresh: `WalkerGoldenCaptureOnce` (edit `INLINE_CASES` or pass a `.properties` file; pivot suite: `--pivot-unpivot`).

---

## M0 — Baseline (done)

- [x] Reproduce `empid` gap with `pivotInAnyOrderByNonForColumnsWithWhereTest`.
- [x] Minimal bridge: interface loop + `query_dictionary` site index when `outputCol == columnName`.
- [x] Add migration tests + contract tests (ignored).

**Gate:** `SqlEventWalkerPivotUnpivotTests` green; contract class ignored.

---

## M1 — Stop discarding unresolved without materialize (done)

**Problem:** `consumeUnresolvedColumnReferenceFromModifierScope` (walk-time, some derived operand paths) removes `unresolved_column` keys with no `table_dictionary` merge.

**Changes:**

1. [x] `relocateUnresolvedModifierScopeColumnReferences` / `relocateUnresolvedModifierScopeColumnReferencesForDerivedOperand`: merge unresolved tokens into `RELATIONAL_MODIFIER_DERIVED_COLUMNS_KEY` (or physical `source_columns` when table ref provided), then remove keys.
2. [x] UNPIVOT derived VALUE/FOR path calls relocate instead of blind consume.
3. [x] Derived-operand relocate is **unqualified keys only** so fatal tests with wrong qualifiers (`wrong.sales_amount`, `msl.month_name`) stay in `unresolved_column`.

**Tests:** `SqlEventWalkerPivotUnpivotTests` (193); migration tests unchanged.

**Gate:** Full `SqlEventWalkerPivotUnpivotTests` green.

---

## M2 — Reorder convert egress: interface lineage before operand drain (done)

**Problem:** Phase 16.2 (`resolvePivotOperandColumnsFromUnresolvedMap`) runs **before** the interface loop and can consume shared unresolved buckets.

**Changes:**

1. [x] **Split phase 16.2:** `materializePivotOperandStructuredBucketsAtConvertEgress` stays post-wildcard / pre-interface (preserves `table_dictionary` key order on JOIN pivot patterns).
2. [x] `drainPivotOperandColumnsFromUnresolvedMap` runs **after** the interface egress loop so shared `unresolved_column` keys are available to interface materialization first.

**Tests:** `SqlEventWalkerPivotUnpivotTests` + smoketest quality gate.

**Gate:** Full pivot class + `SmoketestQualityGateTestSuite` green.

---

## M3 — Unify interface materialization (PIVOT + UNPIVOT operands) (done)

**Problem:** `RESOLVED_PIVOT_OPERAND` / `RESOLVED_UNPIVOT_IN_SOURCE` branches `continue` without `materializeInterfaceOutputSourceLineage`.

**Changes:**

1. [x] `materializeInterfacePivotOperandDependencyLineage` / unpivot IN-source twin on qualified + unqualified interface egress paths (after operand classify / `applyConvertEgress*`).
2. [x] Operand site coalescing by dependency column name in `query_dictionary` (not M0 bare-output gate); walk bridge `attachWalkCapturedSiteTokensToSelectItemDependencyRefs` at `exitSelect_item`.
3. [x] Enable `ModifierLineageConsolidationContractTests` (pivot + unpivot contracts).

**Tests:** Contract tests (3 active); migration + pivot golden refresh for SELECT expression operand sites.

**Gate:** Contract tests + full `SqlEventWalkerPivotUnpivotTests` + smoketest quality gate green.

---

## M4 — SELECT-list site tokens (narrowed) (done)

**Original problem:** `shouldCaptureClauseColumnSiteTokenForActiveColumnReference()` is false for most SELECT-list column refs, so interface `refObj` lacks `locations` unless unresolved still holds them.

**Narrowed product goal:** For UNPIVOT VALUE used inside a SELECT expression, **do not** require the SELECT operand token on `derivation.derived_columns[value]` when VALUE is a derived column. Require:

- Structured **derivation** (`derived_columns` VALUE + `source_columns` IN-list), and
- Output **interface** that traces the alias to physical IN sources (VALUE refs expanded at convert egress).

**Baseline (`d96bb68+`, `0083323`):** `unpivotValueOperandSelectExpressionSitesContractTest` encodes derivation + interface with **both** retained VALUE hop (`sales_amount` @ modifier bucket) **and** expanded IN-list physical deps when output alias ≠ VALUE name (`rewriteReferenceListForSingleUnpivotHint`).

**Gate:** Contract test above + full `mvn test` green.

---

## M5 — Cleanup and docs (done)

- [x] Remove duplicate qualified-interface switch arms (operand statuses handled by egress early path).
- [x] Remove redundant clause-archive pivot/unpivot operand materialization (egress early path + remaining-unresolved drain).
- [x] Align unqualified interface pivot operand path with qualified (`applyConvertEgress*` before interface lineage).
- [x] Document invariant below and in [sql_walker_exit_method_gaps.md](sql_walker_exit_method_gaps.md#relational-modifier-lineage-operand-classification).

**Gate:** Full `mvn test` green.

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
