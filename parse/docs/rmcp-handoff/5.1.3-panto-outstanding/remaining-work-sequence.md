# Panto timeout corpus — remaining work (sequence note)

**Context:** §2.8 is **done** (74/74 rows finish under 90s). What remains is a small tail of **classification + one code task**.

**Do in this order:**

## 1. W5 — Author-error sign-off (7 rows)

**Your action:** Confirm these are **corpus-quality / author-error**, not parser defects. No code fix expected.

| csv_row | Bucket | Why flagged |
|--------:|--------|-------------|
| 28, 30, 31, 32, 41 | 2.8-6 Acquia export | Commented-out CTE wrapper, fragment shape, angle-bracket placeholders |
| 314, 315 | 2.8-13 ALR inquiry | Same pattern — syntax_corrupt; walk skipped after W1 |

**Fixture:** `sql/csv-row-<n>.sql`  
**When done:** Mark W5 complete in workplan §2.8 W-track.

---

## 2. §2.10 — SLL→LL policy (10 rows, code)

**No manual review.** Cluster B rows adjudicated as SLL-only E3 false positives vs production `SqlParserAccess`.

| csv_rows | Count |
|----------|------:|
| 605, 606, 623, 635, 636, 5453, 5454, 5592, 5593, 5594 | 10 |

**Index:** `cluster-b-sll-regression-rows.json` → `pending_2_10_csv_rows`  
**Gates:** `ClusterBSllRegressionTest`, `SqlParserAccessSllPredictionSafetyTest#clusterB210PendingRows_*`

---

## 3. W4 Part 2 — E3 zero-FATAL gates (after 2.10)

Assert E3 `walkerFatalCount == 0` on the 10 pending rows once 2.10 lands.

---

## 4. 2.11.4 — Cleanup (optional)

Remove one-off triage probes after steps 1–3 are closed.

---

## Signed off / excluded (no further action)

| csv_row | Category | Reason |
|--------:|----------|--------|
| **4197** | `utility_workbook` | Multi-statement manual diagnostic worklist — not a bound query. See `panto-corpus-exclusion-list.json`. |

---

## What's already done

| Item | Status |
|------|--------|
| §2.8 E1–E3 (74/74 under 90s) | Complete |
| W1–W3, W4 Part 1 | Complete |
| 2.11.2 Cluster B adjudication (10 → 2.10) | Complete |
| Row 4197 utility exclusion | Complete (2026-09-03) |

---

## Corpus numbers

| Set | Count | Meaning |
|-----|------:|---------|
| E3 timeout corpus | **74** | Timing gate (all rows, including exclusions) |
| Bound-query fast-FATAL rows (Cluster B) | **10** | All pending **2.10** |
| Corpus exclusions (utility workbooks) | **1** | Row **4197** |
| syntax_corrupt (W5 sign-off pending) | **7** | Author-error classification |
| Clean bound queries on diagnostic path | **63** | No further review |
