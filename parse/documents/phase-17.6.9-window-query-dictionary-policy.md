# Phase 17.6.9 — Window `query_dictionary` policy

**Status:** ⏸️ Open (incremental plan — Aug 2026)  
**Priority:** P2 (optional vs **17.7.11** critical path; valuable before pivot/triple golden sign-off)  
**Parent:** [symbol-table-resolution-consolidation-worklist.md](symbol-table-resolution-consolidation-worklist.md) §17.6.9  
**Depends on:** **17.6.8** ✅ (window archive lists + interim `query_dictionary` merge)  
**Related:** **17.7.11** (operand site tokens on `query_dictionary` for query-backed FROM — separate design)

---

## Goal (one sentence)

**Partition/order columns referenced only inside `OVER` must not gain `query_dictionary` keys at convert egress; their site tokens live on `window_partition_by` / `window_ordered_by` only.** Interface **output** names (SELECT list keys, e.g. `rn`) keep `query_dictionary` tokens for their defining sites; see also §Interface lineage for what belongs on `interface.<alias>` dependency lists.

---

## Policy (locked for implementation)

| Surface | Partition/order-only ref (not in `interface`) | Same name in `interface` + `OVER` |
|---------|--------------------------------------------------|-----------------------------------|
| `query_dictionary` | **No** new key / merge | **Yes** — tokens merged (mirror `recordInterfaceOutputClauseRefOnQueryDictionary`) |
| `window_partition_by` | **Yes** — resolved refs + locations | **Yes** |
| `window_ordered_by` | **Yes** | **Yes** |
| `table_dictionary` | Unchanged (physical / walk-time rules) | Unchanged |
| Published `derivation` | No token blobs; no cross-scope helper leaks | Same |

**Code hook (today):** `SqlParseSymbolTreeHelper.mergeDeferredWindowClauseHarvestSiteTokensIntoQueryDictionary` → `mergeClauseColumnListSiteTokensIntoQueryDictionary` with `mergesQueryDictionaryTokensForAllColumnNamesInClauseList(containerKey) == true` for window keys (~L2820–L2843). **17.6.9 change:** treat window lists like other clause lists — merge into `query_dictionary` only when `isInterfaceOutputColumnName(localInterface, columnName)`.

**Out of scope for 17.6.9 (track separately):**

- Cleaning **extra** entries on `window_partition_by` / `window_ordered_by` (e.g. all UNPIVOT IN-list physical columns listed when only one name is in `PARTITION BY`) — investigate as harvest bug; do not “fix” by golden refresh alone.
- Full clause-site matrix (JOIN ON / GROUP BY / ORDER BY with window exprs) — see §Backlog below.
- **17.7.11** query-backed operand `query_dictionary` placement.

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

**Modifier queries (PIVOT / UNPIVOT):** Same rule. After convert egress **phase B** structured derivation (`expandRelationalModifierDerivedColumnLineageInInterfaceMap` / `finalizeRelationalModifierDerivedColumnLineageInClauseLists`):

- Each PARTITION BY / ORDER BY ref that names a **derived** modifier output is represented on `interface.<alias>` as **`{name=<derived>, table_ref=<bucket>}`** (e.g. `tuple_0`), then expanded to that bucket’s **`derivation.source_columns`** (physical IN-list / pivot operands), matching non-modifier “physical ref” behavior.
- **Distinct derived names** (e.g. `metric_name` and `metric_value` on the same UNPIVOT) each appear on the list when both are referenced in `OVER`, even when expansion repeats the same source column set.
- **Do not** drop ORDER BY dependencies while keeping PARTITION BY only — that diverges from V7–V10 and from `window_ordered_by` archive content.

### Not the same as top-level interface keys

| Concept | Example UNPIVOT + `SELECT ROW_NUMBER() … AS rn` only |
|---------|------------------------------------------------------|
| **Interface key list** (`getInterface()` / scope `interface` map keys) | `[rn]` only — `metric_name` / `metric_value` are **not** separate output columns |
| **`interface.rn` dependency list** | Must still include **`metric_name`**, **`metric_value`**, and expanded sources where applicable |
| **`query_dictionary` keys** (17.6.9) | **`rn` only** (plus any other SELECT outputs); **no** keys for partition/order-only derived names |

Contract test sketch: `unpivotWindowDerivedColumnsQueryDictionaryV17_6NewPolicyV1Test` in `SqlEventWalkerPivotUnpivotTests` — assert `[rn]` interface list **and** `interface.rn` contains both derived OVER refs with lineage.

### Known gap (Aug 2026)

UNPIVOT + window can populate **`window_ordered_by`** with ORDER BY derived names while **`interface.rn` omits ORDER BY** deps (PARTITION BY may be present). Treat as **bug / incomplete parity** with V7–V10, not as policy. Fix belongs with interface flatten timing or phase-B backfill from archived window lists into the owning window interface entry (implementation TBD; may land in same phase as 17.6.9 or **17.6.9c**).

**Code touchpoints:** `exitSelect_item` → `flattenSubTreeForDependencyColumns`; convert `runConvertEgressRelationalModifierDerivedLineagePhaseB` → `expandRelationalModifierDerivedColumnLineageInInterfaceMap`; walk `captureClauseDependencies` → `window_partition_by` / `window_ordered_by`.

---

## Test cohort (reuse; do not fork)

Use these as regression spine while stepping through 17.6.9:

| Role | Class | Methods (representative) |
|------|--------|---------------------------|
| Physical SELECT + `OVER` | `SqlEventWalkerFunctionsAggregatesWindowingTests` | `subqueryDictionaryExtensionWindowOverPartitionByV7`–`…MixedV10` (**interface `<alias>` = PARTITION + ORDER BY deps**) |
| Multi-clause SELECT | `SqlEventWalkerCoreSelectFromAliasingTests` | `interfaceLoopDualRoleTrailingClauseSourceAndAliasRefTest` |
| Pivot/unpivot egress | `SqlEventWalkerPivotUnpivotTests` | `unpivotV0WindowDerivedColumnsQueryDictionaryV17_6_8Test`, `unpivotV0WindowSourceColumnQueryDictionaryV17_6_8Test`, `pivotBasicMetricColumnsV0WindowDerivedColumnsQueryDictionaryV17_6_8Test`, `pivotBasicMetricColumnsV0WindowSourceColumnQueryDictionaryV17_6_8Test` |
| DML + window column | `SqlEventWalkerDmlUpdateInsertDeleteTruncateTests` | `updateReturningWithFromSubqueryTest`, `insertComplexSubstitutionI5WithCteQualifyWindowSubstitution` |

**Not window egress contracts:** `SqlEventWalkerSubqueriesAndClauseSemanticsTests` `unaliasedDerived*AllOuterClauses*` (22 tests) — QUALIFY + `OVER` for AST/routing only; no `window_*` assertions.

**New tests home (recommended):**

- **`SqlEventWalkerWindowEgressContractTests`** — physical-table **17.6.9** positive/negative (new class, `AbstractSqlParseEventWalkerTest`).
- **`SqlEventWalkerPivotUnpivotTests`** — pivot/unpivot window matrix; name new methods `*V17_6_9*` or `*WindowEgress*`.

Golden review policy: [relational-modifier-resolution.mdc](../../.cursor/rules/relational-modifier-resolution.mdc) — **no blind refresh** on pivot tests; user confirms assertion changes.

---

## Suspicious baselines (read before changing goldens)

1. **17.6.8 (b) pivot tests** — `window_partition_by` / `window_ordered_by` may list `jan_sales` / `feb_sales` / `mar_sales` when SQL only partitions by `metric_name` or one source column. Treat as **harvest noise** until proven intentional.
2. **`subqueryDictionaryExtensionWindowOverPartitionByV7`** — `col12` on inner `query0.query_dictionary` though only in `OVER`, not subquery SELECT. Likely **pre-17.6.9** behavior; reconcile in **Step 6** (optional), not in Step 3 pivot slimming.
3. **Only four tests** currently assert `window_partition_by` / `window_ordered_by` — expanding assertions is intentional in this phase.

---

## Incremental execution plan

Work **one step per PR** (or per session). After each step: run `SqlEventWalkerWindowEgressContractTests` (once added) + four pivot 17.6.8 (b) tests + `interfaceLoopDualRole*` + targeted `subqueryDictionaryExtensionWindowOver*` if touched.

### Step 0 — Re-center / inventory (no code)

- [ ] Read this doc + worklist row **17.6.9**.
- [ ] Skim `mergeDeferredWindowClauseHarvestSiteTokensIntoQueryDictionary` (~L2798) and `recordInterfaceOutputClauseRefOnQueryDictionary` (~L13601) side by side.
- [ ] Open one pivot 17.6.8 (b) test and one `interfaceLoopDualRole*` test; note current `query_dictionary` vs `window_*` keys.

**Done when:** You can explain partition-only vs interface-dual-role cases in one sentence each.

---

### Step 1 — Design checkpoint (no production code)

- [ ] Confirm policy table in §Policy matches `table-and-query-dictionary-design.md` (interface vs clause-site tokens).
- [ ] Decide: slim **only** `query_dictionary` in 17.6.8 (b) goldens in Step 4, or also tighten `window_*` lists in a **follow-up** item (17.6.9b).

**Done when:** User sign-off on policy table (comment in PR or chat). Agent must not implement Step 3 before this.

---

### Step 2 — P0 tests first (TDD; may fail)

Add `SqlEventWalkerWindowEgressContractTests`:

| ID | Method (suggested) | SQL sketch | Assert |
|----|-------------------|------------|--------|
| **2a** | `physicalPartitionOnlyColumnNoQueryDictionaryV17_6_9Test` | `SELECT ROW_NUMBER() OVER (PARTITION BY b ORDER BY c) AS rn FROM tab1` — **do not** select `b`/`c` | `rn` on `query_dictionary`; **`b`/`c` absent** from `query_dictionary`; present on `window_partition_by` / `window_ordered_by` |
| **2b** | `physicalInterfaceAndOverDualRoleQueryDictionaryV17_6_9Test` | `SELECT a, ROW_NUMBER() OVER (PARTITION BY a ORDER BY b) AS rn FROM tab1` | `a` (and `rn`) on `query_dictionary`; `b` partition-only → **not** on `query_dictionary` |
| **2c** | `physicalQualifyOverUsesSelectAliasV17_6_9Test` | Mini `interfaceLoopDualRole` (alias in QUALIFY `OVER`) | Aliases in `query_dictionary` where interface; source cols follow partition-only rule |

Add to **`SqlEventWalkerPivotUnpivotTests`** (gate `-Pphase15-derived-gate`):

| ID | Method (suggested) | Assert |
|----|-------------------|--------|
| **2d** | `unpivotPartitionOnlyDerivedInOverNoQueryDictionaryV17_6_9Test` | UNPIVOT; `OVER (PARTITION BY metric_name …)` with **`metric_name` not in SELECT** (only `metric_value`, `rn`) | `metric_name` **not** on `query_dictionary`; on `window_partition_by` |
| **2e** | `pivotPartitionOnlyDerivedInOverNoQueryDictionaryV17_6_9Test` | PIVOT sibling; partition by derived sum **not** in SELECT | Same pattern |
| **2f** | `unpivotWindowDerivedColumnsQueryDictionaryV17_6NewPolicyV1Test` (or `*V17_6_9*`) | `SELECT ROW_NUMBER() … AS rn` only over UNPIVOT derived names | `[rn]` interface keys; **`interface.rn`** lists **both** `metric_name` and `metric_value` + expanded `derivation` sources; no `metric_*` on `query_dictionary` |

**Done when:** New tests committed; **expected failures** document 17.6.9 gap (do not “fix” by weakening assertions).

---

### Step 3 — Implementation (single focused change)

**3a — `query_dictionary` (core 17.6.9)**

- [ ] Set `mergesQueryDictionaryTokensForAllColumnNamesInClauseList` to **false** for window keys **or** remove window keys from that branch so window merge uses `isInterfaceOutputColumnName` (~L2840–L2842).
- [ ] Verify `mergeDeferredWindowClauseHarvestSiteTokensIntoQueryDictionary` still runs only when `convertEgressScopeHasRelationalModifierStructuredDerivation()` — physical-only scopes: confirm **2a–2c** behavior still correct (may need to allow window merge for non-modifier scopes with same interface rule — implement minimal diff).

**3b — `interface.<window alias>` lineage on modifier queries (§Interface lineage)**

- [ ] Ensure `interface.<alias>` includes **PARTITION BY and ORDER BY** deps on PIVOT/UNPIVOT, expanded via structured `derivation` (parity with V7–V10); green **2f** / `unpivotWindowDerivedColumnsQueryDictionaryV17_6NewPolicyV1Test`.

**Done when:** Step 2 tests green; no new diagnostics in pivot gate smoke subset.

---

### Step 4 — Slim 17.6.8 (b) goldens (user-reviewed)

- [ ] `unpivotV0WindowDerivedColumnsQueryDictionaryV17_6_8Test`
- [ ] `unpivotV0WindowSourceColumnQueryDictionaryV17_6_8Test`
- [ ] `pivotBasicMetricColumnsV0WindowDerivedColumnsQueryDictionaryV17_6_8Test`
- [ ] `pivotBasicMetricColumnsV0WindowSourceColumnQueryDictionaryV17_6_8Test`

Update **only** `query_dictionary` (and comments referencing “interim until 17.6.9”). Report expected vs actual per golden review policy.

**Done when:** User approves four strings; `136/136` pivot class gate (or current count) green.

---

### Step 5 — Close worklist row

- [ ] Mark **17.6.9** ✅ in [symbol-table-resolution-consolidation-worklist.md](symbol-table-resolution-consolidation-worklist.md).
- [ ] Note any deferred **17.6.9b** (window list harvest cleanup) in worklist or §Backlog.

**Done when:** Worklist + this doc status updated.

---

### Step 6 — Optional follow-up (separate PRs)

- [ ] **Legacy alignment:** `subqueryDictionaryExtensionWindowOverV7–V30` — audit `query_dictionary` for partition-only inner refs; update goldens only if policy applies to non-modifier scopes the same way.
- [ ] **17.6.9b:** Fix over-harvest on `window_partition_by` / `window_ordered_by` (pivot IN-list columns not in SQL `PARTITION BY`).

---

## Backlog — window × clause site (not required to close 17.6.9)

Prioritized **after** Steps 0–5. Mostly **○** today.

**Intentionally untested (grammar allows, not ANSI):** inline `OVER` in **WHERE**, **HAVING**, **GROUP BY**, **JOIN ON**, **UPDATE SET**. See class javadoc on `SqlEventWalkerFunctionsAggregatesWindowingTests` — do not add walker goldens for those unless product policy changes.

| P | Scenario | Suggested class |
|---|----------|-----------------|
| P1 | PIVOT/UNPIVOT — `OVER` refs in **QUALIFY** / **WHERE** / **HAVING** on derived or source operand | `SqlEventWalkerPivotUnpivotTests` |
| ~~P2~~ | ~~`ORDER BY ROW_NUMBER() OVER (...)`~~ | ✅ `windowFunctionInQueryOrderByClauseV17_6_9Test` in `SqlEventWalkerFunctionsAggregatesWindowingTests` |
| P2 | `UPDATE … SET col = <window expr>` | `SqlEventWalkerDmlUpdateInsertDeleteTruncateTests` |
| P3 | Triple-modifier + window partition on derived name | `SqlEventWalkerPivotUnpivotTests` |
| P3 | `JOIN … ON` with window (if grammar allows) | `SqlEventWalkerJoinsAndTableResolutionTests` or window contract |

---

## Agent re-center checklist (start of any 17.6.9 session)

1. Which **step** (0–6) is active? Only work that step.
2. Read §Policy — partition-only → no `query_dictionary`; read §Interface lineage — `interface.<alias>` still lists all OVER deps on modifier queries.
3. Code: `mergeDeferredWindowClauseHarvestSiteTokensIntoQueryDictionary` / `mergesQueryDictionaryTokensForAllColumnNamesInClauseList`.
4. Tests: run `SqlEventWalkerWindowEgressContractTests` + four `*V17_6_8*` window pivot tests.
5. **No** bulk golden refresh; pivot changes need user confirmation.
6. Do not conflate with **17.7.11** (operand tokens on `queryN` for subquery FROM).

---

## Changelog

| Date | Note |
|------|------|
| Aug 2026 | Initial plan from window test cohort inventory + 17.6.8 (b) review |
| Aug 2026 | §Interface lineage — window `interface.<alias>` must include PARTITION BY + ORDER BY on PIVOT/UNPIVOT (V7–V10 parity) |
