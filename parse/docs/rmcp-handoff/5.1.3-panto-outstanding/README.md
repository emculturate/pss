# Copy this pack to the PSS parser repo

Prepared 2026-08-19 from the Panto extracted-queries dual-parse (**5.0.0-3** vs **5.1.3**). This is the set of **outstanding 5.1.3 problems**, not the full 6,680-row corpus.

## Copy these paths

All paths are relative to the RMCP workspace root.

### Required

| Copy | Into parser repo | Why |
|------|------------------|-----|
| `parser-briefs/pss-handoff-5.1.3-2026-08-19/` (this folder) | e.g. `docs/rmcp-handoff/` or `workplans/rmcp-panto-513/` | CSV extract, compact index, this README |
| `parser-briefs/panto-tabledict-degradations-2026-08-19.md` | same docs tree | Investigation brief for the **8 degradations** (clusters, acceptance, hypotheses) |
| `parser-briefs/panto-tabledict-degradations-2026-08-19/sql/` | next to that brief | Full SQL fixtures `csv-row-<N>.sql` for rows 583, 2139, 3150, 3870, 4648, 4726, 5410, 5455 |
| `parser-briefs/panto-513-parse-timeouts-2026-08-19.md` | same docs tree | **74 timeouts**: row, domain/entity/query name, timings, **full SQL** |

Suggested workplan entries (already sketched in RMCP `parser-defects-enhancements-workplan.md` as Phase 8 and 9):

- **Phase: table-dictionary / FATAL regressions** → point at `panto-tabledict-degradations-2026-08-19.md`
- **Phase: 5.1.3 90s parse hang** → point at `panto-513-parse-timeouts-2026-08-19.md` and the CSV extract

### Optional

| Copy | Why you might skip it |
|------|------------------------|
| `parser-defects-enhancements-workplan.md` | RMCP living list; only Phase 8–9 matter for this handoff. Prefer a parser-native workplan that **links** the two briefs above. |
| Timeout markdown (~1.5 MB) | Redundant with `query_sql` in the CSV extract. Keep it if agents prefer one markdown file over CSV. |

### Do not copy

- `project_simulation/src/panto_extracted_queries/panto_query_extract_AUG172026.csv` (full extract, ~6,680 data rows)
- `reports/panto_extracted_queries_compare/*.json` (multi-hundred-MB compare dumps)
- Mixed / improvement rows (1,316 mixed, 563 improvements) — not this pack

## What is in this folder

| File | Contents |
|------|----------|
| `panto_513_outstanding_issues.csv` | **82** data rows: original `domain_name`, `entity_type`, `query_name`, `query_sql`, plus `csv_row`, `issue_kinds`, `issue_detail`, parse timings. RFC 4180; `query_sql` is quoted and may contain newlines. |
| `outstanding-issues-index.md` | Compact tables (no SQL) for workplan / PR description |
| `row-index.json` | Machine-readable row lists |

`issue_kinds` values:

- `timeout_513` — 5.1.3 killed at **90s**; 5.0.0-3 finished (`parse_ms_5_0_0_3` / `parse_ms_5_1_3` filled)
- `degradation_tabledict` — 5.1.3 `tableDictionary` missing keys 5.0.0-3 still has
- `degradation_fatal` — 5.1.3 FATALs that 5.0.0-3 did not issue (rows **3150**, **5410**)

No overlap between the 74 timeouts and the 8 degradations.

## Intentionally omitted: 5.0.0-3 crashes

**29** rows where **5.0.0-3** returned non-JSON / walk crash and **5.1.3** usually still returned JSON. Those are baseline defects, not 5.1.3 outstanding work. Row numbers (if you add a follow-on): 1136, 1228, 2058, 2089, 2536, 2627, 2723, 2815, 2817, 2903, 3149, 3187, 3739, 3793, 3869, 4016, 5240, 5251, 5334–5337, 5339, 5409, 5990, 6482, 6483, 6485, 6578.

## Scoring notes for the parser agent

- Table-dictionary compare uses the **last identifier after `.`**. Qualification-only differences are not degradations.
- Extra nested 5.1.3 query-dictionary layers vs 5.0.0-3 are improvements, not this pack.
- Timeouts are parse **non-termination**, not missing dictionary keys.
- Related older brief: WITH + outer `CROSS JOIN` CTE miss (`last_delivered_cte`) is Phase 2.7 in the RMCP workplan; the eight degradations are overlapping-but-not-identical JOIN/CTE/substitution shapes.
