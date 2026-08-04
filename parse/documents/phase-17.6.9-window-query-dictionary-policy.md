# Phase 17.6.9 — Window `query_dictionary` policy

**Status:** ✅ Done (Aug 2026) — default `parse/` suite verified **`9d06616`** (`mvn clean test`: 1564 run, 0 failures, 0 errors, 3 skipped)  
**Priority:** P2 (historical — Phase 17 complete)  
**Parent:** [symbol-table-resolution-consolidation-worklist.md](symbol-table-resolution-consolidation-worklist.md) §17.6.9  
**Depends on:** **17.6.8** ✅ (window archive lists + interim `query_dictionary` merge)  
**Related:** Query-backed FROM operand placement — accepted goldens (**17.7.11** abandoned)

---

## Goal (one sentence)

**Partition/order columns referenced only inside `OVER` must not gain `query_dictionary` keys at convert egress; their site tokens live on `window_partition_by` / `window_ordered_by` only.** Interface **output** names (SELECT list keys, e.g. `rn`) keep `query_dictionary` tokens for their defining sites; see also §Interface lineage for what belongs on `interface.<alias>` dependency lists.

---

## Policy (locked — implemented)

| Surface | Partition/order-only ref (not in `interface`) | Same name in `interface` + `OVER` |
|---------|--------------------------------------------------|-----------------------------------|
| `query_dictionary` | **No** new key / merge | **Yes** — tokens merged (mirror `recordInterfaceOutputClauseRefOnQueryDictionary`) |
| `window_partition_by` | **Yes** — resolved refs + locations; **modifier:** phase-B fan-out (derived @ bucket + bucket `source_columns`) — see §Archived window / ORDER BY lists | **Yes** |
| `window_ordered_by` | **Yes**; same fan-out rule as `window_partition_by` on PIVOT/UNPIVOT | **Yes** |
| `ordered_by` | Unchanged for ordinary sort keys; **query `ORDER BY` with window expr:** same archived refs + **modifier** phase-B fan-out on harvested OVER deps (with `window_*`) | Unchanged |
| `table_dictionary` | Unchanged (physical / walk-time rules) | Unchanged |
| Published `derivation` | No token blobs; no cross-scope helper leaks | Same |

**Code hook:** `SqlParseSymbolTreeHelper.mergeDeferredWindowClauseHarvestSiteTokensIntoQueryDictionary` → `mergeClauseColumnListSiteTokensIntoQueryDictionary` with `mergesQueryDictionaryTokensForAllColumnNamesInClauseList(containerKey)` returning **`false`** for all clause lists (including `window_partition_by` / `window_ordered_by`). Window merge uses the same rule as other clauses: `query_dictionary` tokens only when `isInterfaceOutputColumnName(localInterface, columnName)`.

**Walk + convert (interface lineage on SELECT-list windows):** per-`OVER` partition/order harvest → `windowOutputInterfaceClauseDepsByAlias` → `applyWalkCapturedWindowSelectInterfaceClauseDeps` at start of `finalizeRelationalModifierDerivedColumnLineageInClauseLists`, then phase-B `expandRelationalModifierDerivedColumnLineageInInterfaceMap` / clause-list expansion from `derivation.source_columns`.

### Archived window / ORDER BY lists (PIVOT / UNPIVOT — locked)

For relational modifiers, **`window_partition_by`**, **`window_ordered_by`**, and **`ordered_by`** (when it archives deps from a query-level `ORDER BY … OVER (…)`) go through the **same** convert egress phase-B expansion as **`interface.<window alias>`**:

- Each ref that names a **derived** modifier output becomes **`{name=<derived>, table_ref=<bucket>}`**, then the list also includes that bucket’s **`derivation.source_columns`** entries (UNPIVOT IN-list physicals, PIVOT FOR/aggregate operands, etc.).
- A single name in SQL `PARTITION BY` may therefore produce **multiple** archived refs on `window_partition_by` — **expected**, not over-harvest. **NewPolicy V1–V4** goldens are authoritative.

**Not in scope:** trimming those lists back to syntactic-only `OVER` names (former **17.6.9b** — closed as won’t implement).

**Out of scope (track separately):**

- Full clause-site matrix (JOIN ON / GROUP BY / ORDER BY with window exprs) — see §Backlog below.
- Query-backed FROM operand `query_dictionary` placement (**17.7.11** abandoned — out of scope).

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
- Query **`ORDER BY`** windows (not SELECT-list outputs) publish OVER deps on **`ordered_by`** + `window_*` lists (with the same phase-B **`source_columns`** fan-out on modifiers), not on `interface.<alias>`.

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

## Optional follow-up (not required for 17.6.9)

- **`subqueryDictionaryExtensionWindowOverPartitionByV7`** — inner `def_query0.query_dictionary` may still list partition-only refs (`col12`); optional audit if physical inner scopes should match modifier **17.6.9** `query_dictionary` gating exactly.

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

---

## Agent re-center checklist (post-close)

1. Modifier window contract = **NewPolicy V1–V4** in `SqlEventWalkerPivotUnpivotTests`.
2. §Policy — partition-only → no `query_dictionary`; SELECT-list window → `interface.<alias>`; **`window_partition_by` / `window_ordered_by` / modifier `ordered_by`** → same phase-B **`source_columns`** fan-out as interface deps.
3. Code: `mergesQueryDictionaryTokensForAllColumnNamesInClauseList` → `false`; `applyWalkCapturedWindowSelectInterfaceClauseDeps`.
4. Run **NewPolicy** four + full `SqlEventWalkerFunctionsAggregatesWindowingTests` + pivot class gate when touching walker/symbol tree.
5. **No** bulk golden refresh on pivot tests without user confirmation.
6. Do not conflate with abandoned **17.7.11** (relational-modifier operands on query-backed FROM).

---

## Changelog

| Date | Note |
|------|------|
| Aug 2026 | Initial plan from window test cohort inventory + 17.6.8 (b) review |
| Aug 2026 | §Interface lineage — window `interface.<alias>` must include PARTITION BY + ORDER BY on PIVOT/UNPIVOT (V7–V10 parity) |
| Aug 2026 | **Closed:** `query_dictionary` gating + walk-captured OVER deps on `interface.<alias>`; locked contract **NewPolicy V1–V4** |
| Aug 2026 | Deprecated **17.6.8 (b)** modifier window tests; refreshed physical window symbol goldens for `window_*` archives |
| Aug 2026 | **17.6.9b closed (won’t implement):** archived `window_*` / modifier `ordered_by` accept phase-B derived **`source_columns`** fan-out; policy + worklist updated |
