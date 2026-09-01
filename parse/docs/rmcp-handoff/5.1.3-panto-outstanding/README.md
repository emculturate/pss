# Panto 5.1.3 outstanding issues — RMCP handoff pack

Prepared 2026-08-19 from the Panto extracted-queries dual-parse (**5.0.0-3** vs **5.1.3**). This is the set of **outstanding 5.1.3 problems** (82 rows), not the full ~6,680-row corpus.

**Canonical location in this repo:** `parse/docs/rmcp-handoff/5.1.3-panto-outstanding/`  
**Workplan tracker:** `parse/documents/parser-defects-enhancements-workplan.md` §2.7–2.9

## Where to look

| Phase | Topic | Document |
|-------|--------|----------|
| **2.7** | WITH CTE physical-source / tuple-substitution finalization (**complete**) | [phase-2.7-with-conditionless-join-finalizer.md](./phase-2.7-with-conditionless-join-finalizer.md) |
| **2.8** | 74 queries timeout at 90s on 5.1.3 | [panto-513-parse-timeouts-2026-08-19.md](./panto-513-parse-timeouts-2026-08-19.md), [outstanding-issues-index.md](./outstanding-issues-index.md), [panto_513_outstanding_issues.csv](./panto_513_outstanding_issues.csv) |
| **2.9** | 8 table-dictionary / FATAL degradations | [panto-tabledict-degradations-2026-08-19.md](./panto-tabledict-degradations-2026-08-19.md), SQL fixtures in [sql/](./sql/) |

## Files in this folder

| File | Contents |
|------|----------|
| `phase-2.7-with-conditionless-join-finalizer.md` | Phase 2.7 spec — **complete**; CTE aliases not in global `tableDictionary` by design |
| `panto_513_outstanding_issues.csv` | **82** data rows: domain, entity, query name, full `query_sql`, timings, `issue_kinds` |
| `outstanding-issues-index.md` | Compact tables (no SQL) for workplan / PR description |
| `row-index.json` | Machine-readable row lists |
| `panto-tabledict-degradations-2026-08-19.md` | 8 degradation clusters (A–F), acceptance, hypotheses |
| `panto-513-parse-timeouts-2026-08-19.md` | 74 timeouts with full SQL (~large; CSV is often enough) |
| `sql/csv-row-<N>.sql` | Full SQL for degradation rows 583, 2139, 3150, 3870, 4648, 4726, 5410, 5455 |

`issue_kinds` values in the CSV:

- `timeout_513` — 5.1.3 killed at **90s**; 5.0.0-3 finished
- `degradation_tabledict` — 5.1.3 `tableDictionary` missing keys 5.0.0-3 still has
- `degradation_fatal` — 5.1.3 FATALs that 5.0.0-3 did not issue (rows **3150**, **5410**)

No overlap between the 74 timeouts and the 8 degradations.

## Scoring notes

- Table-dictionary compare uses the **last identifier after `.`**. Qualification-only differences are not degradations.
- Extra nested 5.1.3 query-dictionary layers vs 5.0.0-3 are improvements, not this pack.
- Timeouts are parse **non-termination**, not missing dictionary keys.
- Phase 2.7 (complete) locked trailing-clause physical/tuple collection and confirmed CTE aliases belong in `query_dictionary` / symbol tree, not global `tableDictionary`. The eight degradations may still show functional source loss — adjudicate under **2.9**; a missing CTE **name** in global `tableDictionary` alone is not a regression.

## Intentionally omitted: 5.0.0-3 crashes

**29** rows where **5.0.0-3** returned non-JSON / walk crash and **5.1.3** usually still returned JSON. Those are baseline defects, not 5.1.3 outstanding work.

## Origin

Copied from RMCP `parser-briefs/` (2026-08-19). Older copies under `parse/documents/` were removed to keep this folder the single source of truth.
