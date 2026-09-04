# Panto 5.1.3 outstanding issues — RMCP handoff pack

Prepared 2026-08-19 from the Panto extracted-queries dual-parse (**5.0.0-3** vs **5.1.3**). This is the set of **outstanding 5.1.3 problems** (82 rows), not the full ~6,680-row corpus.

**Canonical location in this repo:** `parse/docs/rmcp-handoff/5.1.3-panto-outstanding/`  
**Workplan tracker:** `parse/documents/parser-defects-enhancements-workplan.md` §2.7–2.9

## Where to look

| Phase | Topic | Document |
|-------|--------|----------|
| **2.7** | WITH CTE physical-source / tuple-substitution finalization (**complete**) | [phase-2.7-with-conditionless-join-finalizer.md](./phase-2.7-with-conditionless-join-finalizer.md) |
| **2.8** | 74 queries timeout at 90s on 5.1.3 — **18 construction buckets** in workplan | [parser-defects-enhancements-workplan.md](../../../documents/parser-defects-enhancements-workplan.md) §2.8 tracker; [panto-513-parse-timeouts-2026-08-19.md](./panto-513-parse-timeouts-2026-08-19.md), [outstanding-issues-index.md](./outstanding-issues-index.md), [panto_513_outstanding_issues.csv](./panto_513_outstanding_issues.csv) |
| **2.9** | 8 table-dictionary / FATAL degradations (**closed** 2026-09-01) | [panto-tabledict-degradations-2026-08-19.md](./panto-tabledict-degradations-2026-08-19.md), [global-table-dictionary-cte-alias-policy.md](../../../documents/global-table-dictionary-cte-alias-policy.md), SQL fixtures in [sql/](./sql/) |

## Files in this folder

| File | Contents |
|------|----------|
| `phase-2.7-with-conditionless-join-finalizer.md` | Phase 2.7 spec — **complete**; CTE aliases not in global `tableDictionary` by design |
| `panto_513_outstanding_issues.csv` | **82** data rows: domain, entity, query name, full `query_sql`, timings, `issue_kinds` |
| `timeout-513-corpus-rows.json` | Sorted `csv_row` list for the **74** `timeout_513` E3 gate rows |
| `cluster-b-sll-regression-rows.json` | **10** Cluster B rows — SLL false positives; production LL regression via `ClusterBSllRegressionTest` |
| `panto-corpus-exclusion-list.json` | Bound-query exclusions (utility workbooks; row **4197** signed off) |
| `remaining-work-sequence.md` | Closure summary — §2.8 / §2.11 complete; §2.10 abandoned |
| `sql/csv-row-<N>.sql` | **Frozen** full SQL for tests (74 timeout rows + degradation exemplars). Tests read these files, not the CSV at runtime. Refresh a row by re-exporting from `panto_513_outstanding_issues.csv` when intentionally updating a fixture. |
| `PantoTimeoutCorpusE3GateTest` | CI gate: **74** timeout rows must finish under **90 s** (opt-in timing only; not production correctness) |
| `ParseLatencyDiagnosticService` | Opt-in lex/parse/walk/finalize timing; `diagnose()` = LL; `diagnoseWithSllProbe()` for SLL comparison |
| `outstanding-issues-index.md` | Compact tables (no SQL) for workplan / PR description |
| `row-index.json` | Machine-readable row lists |
| `panto-tabledict-degradations-2026-08-19.md` | 8 degradation clusters (A–F), acceptance, hypotheses |
| `panto-513-parse-timeouts-2026-08-19.md` | 74 timeouts with full SQL (~large; frozen `sql/` fixtures are used in CI) |

`issue_kinds` values in the CSV:

- `timeout_513` — 5.1.3 killed at **90s**; 5.0.0-3 finished
- `degradation_tabledict` — legacy RMCP label; all eight rows **closed** under Phase 2.9 (CTE `table_alias` policy or set-op FATAL policy)
- `degradation_fatal` — legacy RMCP label; rows **3150**, **5410** **closed** — canonical set-op interface validation

No overlap between the 74 timeouts and the 8 degradations.

## Scoring notes

- Table-dictionary compare uses the **last identifier after `.`**. Qualification-only differences are not degradations.
- Extra nested 5.1.3 query-dictionary layers vs 5.0.0-3 are improvements, not this pack.
- Timeouts are parse **non-termination**, not missing dictionary keys.
- Phase 2.7 (complete) locked trailing-clause physical/tuple collection and confirmed **CTE aliases belong in `query_dictionary` / symbol tree, not global `tableDictionary`**. A missing CTE **name** in global `tableDictionary` alone is **not** a regression.
- **5.1.3 CTE registration enhancement:** When a CTE name appears in 5.0.0-3 `tableDictionary` but not in 5.1.3 `tableDictionary`, check the **symbol table** first. In 5.1.3, WITH members are intentionally recorded on the enclosing scope’s **`table_alias`** map as **`{cte_name=queryN}`** bindings (published under `def_queryN`), pointing at the nested `def_queryN` body. Example (row 583): `latest_applications=query1`, `activity_prospect_map=query2`, `campus_visit_activity=query3` under `def_query4.table_alias`. That is **on purpose** — richer, documented row-source wiring that 5.0.0-3 did not emit. Treat it as a **5.1.3 improvement**, not an acceptable “silent” omission. Do **not** restore 5.0.0-3-style CTE keys into global `tableDictionary` to make raw output look similar.
- Re-score under **2.9** only when physical/tuple source evidence or CTE symbol-tree registration (`table_alias`, `interface`, `filters`, `query_dictionary`) is functionally absent — not when the CTE moved from `tableDictionary` to `table_alias` → `queryN`.

## Intentionally omitted: 5.0.0-3 crashes

**29** rows where **5.0.0-3** returned non-JSON / walk crash and **5.1.3** usually still returned JSON. Those are baseline defects, not 5.1.3 outstanding work.

## Origin

Copied from RMCP `parser-briefs/` (2026-08-19). Older copies under `parse/documents/` were removed to keep this folder the single source of truth.
