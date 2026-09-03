# Panto timeout corpus — remaining work (sequence note)

**Context:** §2.8 is **done** (74/74 rows finish under 90s). `ParseLatencyDiagnosticService` is **opt-in timing only** — not used in production parsing.

**Do in this order:**

## 1. §2.10 — SLL→LL policy (10 rows, code)

**No manual review.** Cluster B rows adjudicated as SLL-only false positives vs production `SqlParserAccess`.

| csv_rows | Count |
|----------|------:|
| 605, 606, 623, 635, 636, 5453, 5454, 5592, 5593, 5594 | 10 |

**Index:** `cluster-b-sll-regression-rows.json` → `pending_2_10_csv_rows`  
**Gates:** `ClusterBSllRegressionTest`, `SqlParserAccessSllPredictionSafetyTest#clusterB210PendingRows_*`

---

## 2. 2.11.4 — Cleanup (optional)

Remove one-off triage probes after §2.10 is closed.

---

## Retracted / signed off (no further action)

| Item | Reason |
|------|--------|
| **W5 syntax_corrupt (rows 28–32, 41, 314–315)** | **Retracted (2026-09-03).** False positives from E3 diagnostic SLL path and walk-skip policy — not production `SqlParserAccess` behavior. Row **28** parses with zero walker fatals on production path. |
| **4197** | `utility_workbook` — multi-statement diagnostic worklist, not a bound query. See `panto-corpus-exclusion-list.json`. |

---

## What's already done

| Item | Status |
|------|--------|
| §2.8 E1–E3 (74/74 under 90s) | Complete |
| W1–W3, W4 Part 1 | Complete |
| 2.11.2 Cluster B adjudication (10 → 2.10) | Complete |
| Row 4197 utility exclusion | Complete (2026-09-03) |
| Production walk policy simplified (always walk; `WalkerWalkExceptionGate`) | Complete (2026-09-03) |
| `ParseLatencyDiagnosticService` → opt-in LL-only `diagnose()`; SLL via `diagnoseWithSllProbe()` | Complete (2026-09-03) |

---

## Corpus numbers

| Set | Count | Meaning |
|-----|------:|---------|
| E3 timeout corpus | **74** | Timing gate only (`PantoTimeoutCorpusE3GateTest`) |
| Bound-query SLL false positives (Cluster B) | **10** | Pending **2.10** in `SqlParserAccess` |
| Corpus exclusions (utility workbooks) | **1** | Row **4197** |
| Production correctness | **SqlParserAccess** | Not the latency diagnostic service |
