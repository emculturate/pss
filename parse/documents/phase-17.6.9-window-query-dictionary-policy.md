# Phase 17.6.9 — Window `query_dictionary` policy

**Status:** ⏸️ Open (incremental plan — Aug 2026)  
**Priority:** P2 (optional vs **17.7.11** critical path; valuable before pivot/triple golden sign-off)  
**Parent:** [symbol-table-resolution-consolidation-worklist.md](symbol-table-resolution-consolidation-worklist.md) §17.6.9  
**Depends on:** **17.6.8** ✅ (window archive lists + interim `query_dictionary` merge)  
**Related:** **17.7.11** (operand site tokens on `query_dictionary` for query-backed FROM — separate design)

---

## Goal (one sentence)

**Partition/order columns referenced only inside `OVER` must not gain `query_dictionary` keys at convert egress; their site tokens live on `window_partition_by` / `window_ordered_by` only.** Interface output names (SELECT list / explicit aliases) keep today’s `query_dictionary` behavior.

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

## Test cohort (reuse; do not fork)

Use these as regression spine while stepping through 17.6.9:

| Role | Class | Methods (representative) |
|------|--------|---------------------------|
| Physical SELECT + `OVER` | `SqlEventWalkerFunctionsAggregatesWindowingTests` | `subqueryDictionaryExtensionWindowOverPartitionByV7`, `…V9` (partition-only on inner scope) |
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

**Done when:** New tests committed; **expected failures** document 17.6.9 gap (do not “fix” by weakening assertions).

---

### Step 3 — Implementation (single focused change)

- [ ] Set `mergesQueryDictionaryTokensForAllColumnNamesInClauseList` to **false** for window keys **or** remove window keys from that branch so window merge uses `isInterfaceOutputColumnName` (~L2840–L2842).
- [ ] Verify `mergeDeferredWindowClauseHarvestSiteTokensIntoQueryDictionary` still runs only when `convertEgressScopeHasRelationalModifierStructuredDerivation()` — physical-only scopes: confirm **2a–2c** behavior still correct (may need to allow window merge for non-modifier scopes with same interface rule — implement minimal diff).

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

| P | Scenario | Suggested class |
|---|----------|-----------------|
| P1 | PIVOT/UNPIVOT — `OVER` refs in **QUALIFY** / **WHERE** / **HAVING** on derived or source operand | `SqlEventWalkerPivotUnpivotTests` |
| P2 | `ORDER BY ROW_NUMBER() OVER (...)` | `SqlEventWalkerWindowEgressContractTests` |
| P2 | `UPDATE … SET col = <window expr>` | `SqlEventWalkerDmlUpdateInsertDeleteTruncateTests` |
| P3 | Triple-modifier + window partition on derived name | `SqlEventWalkerPivotUnpivotTests` |
| P3 | `JOIN … ON` with window (if grammar allows) | `SqlEventWalkerJoinsAndTableResolutionTests` or window contract |

---

## Agent re-center checklist (start of any 17.6.9 session)

1. Which **step** (0–6) is active? Only work that step.
2. Read §Policy — partition-only → no `query_dictionary`.
3. Code: `mergeDeferredWindowClauseHarvestSiteTokensIntoQueryDictionary` / `mergesQueryDictionaryTokensForAllColumnNamesInClauseList`.
4. Tests: run `SqlEventWalkerWindowEgressContractTests` + four `*V17_6_8*` window pivot tests.
5. **No** bulk golden refresh; pivot changes need user confirmation.
6. Do not conflate with **17.7.11** (operand tokens on `queryN` for subquery FROM).

---

## Changelog

| Date | Note |
|------|------|
| Aug 2026 | Initial plan from window test cohort inventory + 17.6.8 (b) review |
