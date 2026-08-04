# Phase 17.6.9 — Window `query_dictionary` policy

**Status:** ✅ Done (Aug 2026) — default `parse/` suite verified **`9d06616`** (`mvn clean test`: 1564 run, 0 failures, 0 errors, 3 skipped)  
**Priority:** P2 (optional vs **17.7.11** critical path)  
**Parent:** [symbol-table-resolution-consolidation-worklist.md](symbol-table-resolution-consolidation-worklist.md) §17.6.9  
**Depends on:** **17.6.8** ✅ (window archive lists + interim `query_dictionary` merge)  
**Related:** **17.7.11** (operand site tokens on `query_dictionary` for query-backed FROM — separate design)

---

## Goal (one sentence)

**Partition/order columns referenced only inside `OVER` must not gain `query_dictionary` keys at convert egress; their site tokens live on `window_partition_by` / `window_ordered_by` only.** Interface **output** names (SELECT list keys, e.g. `rn`) keep `query_dictionary` tokens for their defining sites; see also §Interface lineage for what belongs on `interface.<alias>` dependency lists.

---

## Policy (locked — implemented)

| Surface | Partition/order-only ref (not in `interface`) | Same name in `interface` + `OVER` |
|---------|--------------------------------------------------|-----------------------------------|
| `query_dictionary` | **No** new key / merge | **Yes** — tokens merged (mirror `recordInterfaceOutputClauseRefOnQueryDictionary`) |
| `window_partition_by` | **Yes** — resolved refs + locations | **Yes** |
| `window_ordered_by` | **Yes** | **Yes** |
| `table_dictionary` | Unchanged (physical / walk-time rules) | Unchanged |
| Published `derivation` | No token blobs; no cross-scope helper leaks | Same |

**Code hook:** `SqlParseSymbolTreeHelper.mergeDeferredWindowClauseHarvestSiteTokensIntoQueryDictionary` → `mergeClauseColumnListSiteTokensIntoQueryDictionary` with `mergesQueryDictionaryTokensForAllColumnNamesInClauseList(containerKey)` returning **`false`** for all clause lists (including `window_partition_by` / `window_ordered_by`). Window merge uses the same rule as other clauses: `query_dictionary` tokens only when `isInterfaceOutputColumnName(localInterface, columnName)`.

**Walk + convert (interface lineage on SELECT-list windows):** per-`OVER` partition/order harvest → `windowOutputInterfaceClauseDepsByAlias` → `applyWalkCapturedWindowSelectInterfaceClauseDeps` at start of `finalizeRelationalModifierDerivedColumnLineageInClauseLists`, then phase-B `expandRelationalModifierDerivedColumnLineageInInterfaceMap` / clause-list expansion from `derivation.source_columns`.

**Out of scope (track separately):**

- Cleaning **extra** entries on `window_partition_by` / `window_ordered_by` (e.g. all UNPIVOT IN-list physical columns listed when only one name is in `PARTITION BY`) — **17.6.9b**; symmetric on UNPIVOT/PIVOT; do not “fix” by golden refresh alone.
- Full clause-site matrix (JOIN ON / GROUP BY / ORDER BY with window exprs) — see §Backlog below.
- **17.7.11** query-backed operand `query_dictionary` placement.

### Deprecated: 17.6.8 (b) modifier window tests

The four **17.6.8 (b)** pivot/unpivot window methods (`unpivotV0Window*QueryDictionaryV17_6_8Test`, `pivotBasicMetricColumnsV0Window*QueryDictionaryV17_6_8Test`) are **`@Deprecated`**. They kept interim “list every {@code OVER} name on {@code query_dictionary} when also in SELECT” assertions from before **17.6.9**.

**Use instead:** **NewPolicy V1–V4** for modifier + window egress policy. Other **17.6.8** tests (WHERE/HAVING/QUALIFY/RETURNING) remain valid for clause-site egress and are not deprecated.

Physical-table window goldens in `SqlEventWalkerFunctionsAggregatesWindowingTests` now include **`window_partition_by` / `window_ordered_by`** on the outer scope where walk archives {@code OVER} harvest (aligned with 17.6.8 archive lists + 17.6.9 policy).

---

## Interface lineage for window outputs (PIVOT / UNPIVOT)

This is **separate from** the `query_dictionary` partition-only rule above. A SELECT may expose **only** the window alias on the top-level interface key list (e.g. `[rn]`) while still publishing full **dependency lineage** under `interface.rn`.

### Rule

For every **window function that is itself an interface output** (a SELECT-list item with an alias, e.g. `ROW_NUMBER() … AS rn`):

**`interface.<alias>`** must list **all column dependencies** introduced by that window expression:

| Source in SQL | Must appear on `interface.<alias>` |
|---------------|--------------------------------------|
| `PARTITION BY` expressions | **Yes** — each resolved column ref |
| `ORDER BY` expressions inside `OVER` | **Yes** — each resolved column ref |
| Window function arguments (e.g. `SUM(col9) OVER (…)`) | **Yes** — same as non-modifier windows |

**Parity baseline (no relational modifier):** `SqlEventWalkerFunctionsAggregatesWindowingTests` — `subqueryDictionaryExtensionWindowOverPartitionByV7` through `…MixedV10` (`rn`, `rn1`, `rn2`). Example (V7): `interface.rn` includes **both** partition column `col12` and order column `col3`; archived lists split them across `window_partition_by` and `window_ordered_by`.

**Modifier queries (PIVOT / UNPIVOT):** Same machinery as physical windows. After convert egress **phase B** (`finalizeRelationalModifierDerivedColumnLineageInClauseLists`):

- Each PARTITION BY / ORDER BY ref that names a **derived** modifier output is represented as **`{name=<derived>, table_ref=<bucket>}`** (e.g. `tuple_0`), then expanded using that bucket’s **`derivation.source_columns`** (UNPIVOT IN-list physicals vs PIVOT operand pair). Output shape is a **function of published derivation**, not a separate pivot/unpivot egress path.
- **Distinct derived names** referenced in `OVER` each appear when both partition and order use different derived names.
- Query **`ORDER BY`** windows (not SELECT-list outputs) publish OVER deps on **`ordered_by`** + `window_*` lists, not on `interface.<alias>`.

### Not the same as top-level interface keys

| Concept | Example UNPIVOT + `SELECT ROW_NUMBER() … AS rn` only |
|---------|------------------------------------------------------|
| **Interface key list** (`getInterface()` / scope `interface` map keys) | `[rn]` only — `metric_name` / `metric_value` are **not** separate output columns |
| **`interface.rn` dependency list** | PARTITION + ORDER BY deps, expanded via **`source_columns`** for the bucket |
| **`query_dictionary` keys** (17.6.9) | **`rn` only** (plus any other SELECT outputs); **no** keys for partition/order-only derived names |

### Locked contract tests (modifier matrix)

`SqlEventWalkerPivotUnpivotTests` — **do not fork**; golden changes require user review per [relational-modifier-resolution.mdc](../../.cursor/rules/relational-modifier-resolution.mdc).

| Test | Modifier | Window placement | `OVER` refs |
|------|----------|------------------|-------------|
| `unpivotWindowDerivedColumnsQueryDictionaryV17_6NewPolicyV1Test` | UNPIVOT | SELECT `rn` | `metric_name`, `metric_value` |
| `unpivotWindowDerivedColumnsQueryDictionaryV17_6NewPolicyV2Test` | UNPIVOT | query `ORDER BY` | same |
| `pivotWindowDerivedColumnsQueryDictionaryV17_6NewPolicyV3Test` | PIVOT | SELECT `rn` | `jan_sales_SUM`, `feb_sales_SUM` |
| `pivotWindowDerivedColumnsQueryDictionaryV17_6NewPolicyV4Test` | PIVOT | query `ORDER BY` | same |

**Code touchpoints:** `captureClauseDependencies` (window lists + per-OVER pairing); `exitSelect_item` / `exitSelect_list`; `applyWalkCapturedWindowSelectInterfaceClauseDeps`; `runConvertEgressRelationalModifierDerivedLineagePhaseB` → `finalizeRelationalModifierDerivedColumnLineageInClauseLists`.

---

## Test cohort (regression spine)

| Role | Class | Methods (representative) |
|------|--------|---------------------------|
| Physical SELECT + `OVER` | `SqlEventWalkerFunctionsAggregatesWindowingTests` | `subqueryDictionaryExtensionWindowOverPartitionByV7`–`…MixedV30`, lag/lead/first_value window samples — symbol tables include `window_*` archives |
| Modifier window contract | `SqlEventWalkerPivotUnpivotTests` | `*NewPolicyV1*`–`*NewPolicyV4*` |
| Deprecated (modifier window) | `SqlEventWalkerPivotUnpivotTests` | `*Window*V17_6_8Test` on pivot/unpivot — superseded by **NewPolicy** |
| Multi-clause SELECT | `SqlEventWalkerCoreSelectFromAliasingTests` | `interfaceLoopDualRoleTrailingClauseSourceAndAliasRefTest` |
| DML + window column | `SqlEventWalkerDmlUpdateInsertDeleteTruncateTests` | `updateReturningWithFromSubqueryTest`, `insertComplexSubstitutionI5WithCteQualifyWindowSubstitution` |

**Not window egress contracts:** `SqlEventWalkerSubqueriesAndClauseSemanticsTests` `unaliasedDerived*AllOuterClauses*` — QUALIFY + `OVER` for AST/routing only.

**Optional future:** `SqlEventWalkerWindowEgressContractTests` for physical-table-only negatives (Step 2 sketch below) — not required to close 17.6.9.

---

## Suspicious baselines (read before changing goldens)

1. **17.6.9b** — `window_partition_by` / `window_ordered_by` may list full bucket `source_columns` expansion beyond the single name in SQL `PARTITION BY`.
2. **`subqueryDictionaryExtensionWindowOverPartitionByV7`** — inner `def_query0.query_dictionary` may still list partition-only refs (`col12`); optional follow-up if physical scopes should match modifier **17.6.9** gating exactly.

---

## Execution plan (historical)

Steps 0–3 **completed** (implementation + **NewPolicy** V1–V4). **17.6.8 (b) modifier window tests deprecated.** Optional: inner-scope `query_dictionary` audit on V7–V30.

### Step 2 sketch (optional P0 class — not added)

Physical-table negatives in a dedicated `SqlEventWalkerWindowEgressContractTests` remain a nice-to-have; modifier contract is covered by **NewPolicy** V1–V4.

---

## Backlog — window × clause site (not required to close 17.6.9)

Prioritized **after** closure. Mostly **○** today.

**Intentionally untested (grammar allows, not ANSI):** inline `OVER` in **WHERE**, **HAVING**, **GROUP BY**, **JOIN ON**, **UPDATE SET**. See class javadoc on `SqlEventWalkerFunctionsAggregatesWindowingTests`.

| P | Scenario | Suggested class |
|---|----------|-----------------|
| P1 | PIVOT/UNPIVOT — `OVER` refs in **QUALIFY** / **WHERE** / **HAVING** on derived or source operand | `SqlEventWalkerPivotUnpivotTests` |
| ~~P2~~ | ~~`ORDER BY ROW_NUMBER() OVER (...)`~~ | ✅ `windowFunctionInQueryOrderByClauseV17_6_9Test` + **NewPolicy** V2/V4 |
| P2 | `UPDATE … SET col = <window expr>` | `SqlEventWalkerDmlUpdateInsertDeleteTruncateTests` |
| P3 | Triple-modifier + window partition on derived name | `SqlEventWalkerPivotUnpivotTests` |
| P3 | `JOIN … ON` with window (if grammar allows) | `SqlEventWalkerJoinsAndTableResolutionTests` |

**17.6.9b:** Fix over-harvest on `window_partition_by` / `window_ordered_by`.

---

## Agent re-center checklist (post-close)

1. Modifier window contract = **NewPolicy V1–V4** in `SqlEventWalkerPivotUnpivotTests`.
2. §Policy — partition-only → no `query_dictionary`; SELECT-list window → `interface.<alias>` + phase-B `source_columns` expansion.
3. Code: `mergesQueryDictionaryTokensForAllColumnNamesInClauseList` → `false`; `applyWalkCapturedWindowSelectInterfaceClauseDeps`.
4. Run **NewPolicy** four + full `SqlEventWalkerFunctionsAggregatesWindowingTests` + pivot class gate when touching walker/symbol tree.
5. **No** bulk golden refresh on pivot tests without user confirmation.
6. Do not conflate with **17.7.11**.

---

## Changelog

| Date | Note |
|------|------|
| Aug 2026 | Initial plan from window test cohort inventory + 17.6.8 (b) review |
| Aug 2026 | §Interface lineage — window `interface.<alias>` must include PARTITION BY + ORDER BY on PIVOT/UNPIVOT (V7–V10 parity) |
| Aug 2026 | **Closed:** `query_dictionary` gating + walk-captured OVER deps on `interface.<alias>`; locked contract **NewPolicy V1–V4** |
| Aug 2026 | Deprecated **17.6.8 (b)** modifier window tests; refreshed physical window symbol goldens for `window_*` archives |
