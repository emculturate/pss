# Workplan: PSS 5.1.3 missing table-dictionary sources vs 5.0.0-3

Date: 2026-08-19  
Kind: Defect (regression vs **5.0.0-3**)  
Audience: PSS parser agent — investigate why **5.1.3** omits sources that **5.0.0-3** still records, and restore them without dropping other dictionary entries.  
Corpus: Panto extracted-queries CSV dual-parse (`SQL` endpoint), RMCP compare after table-dict last-identifier scoring and outermost-only queryDictionary vs 5.0.0-3.

**Related:** Phase **2.7** — [phase-2.7-with-conditionless-join-finalizer.md](./phase-2.7-with-conditionless-join-finalizer.md) (**complete**, 2026-09-01). Global `tableDictionary` is for physical and tuple substitution sources only; **CTE aliases must not appear there**. Several cases below were originally scored as “missing CTE keys” in `tableDictionary` vs 5.0.0-3 — re-adjudicate under **2.9** for functional source loss in `query_dictionary` / symbol-tree structures.

## Goal

For each listed query, **5.1.3** `tableDictionary` (and, where noted, diagnostics) must not be a functional regression vs **5.0.0-3**:

1. Every **physical table or tuple substitution source** that **5.0.0-3** recorded in `tableDictionary` must still appear in **5.1.3** with equivalent token-site evidence (compare the last identifier after `.`; schema-only qualification is **not** this defect).
2. **CTE aliases** must **not** be required in global `tableDictionary` (5.1.3 policy; Phase 2.7 closed). For CTE-centered rows, require equivalent evidence in `query_dictionary` and `def_*` symbol-tree structures — especially **`table_alias`** entries of the form **`{cte_name=queryN}`** on the enclosing scope (e.g. row 583: `latest_applications=query1`, `activity_prospect_map=query2`, `campus_visit_activity=query3`). That symbol-table registration is an **intentional 5.1.3 enhancement** over 5.0.0-3; it is not a downgrade because the CTE name left global `tableDictionary`.
3. Do not “fix” a missing CTE name in `tableDictionary` by deleting the base tables / tuples that **5.1.3** already records.
4. Nested **5.1.3** dictionary layers that **5.0.0-3** never emitted are improvements, not something to strip.

## How RMCP scored these eight

Compare: `5.0.0-3` vs `5.1.3`. Verdict **degradation** only when **5.1.3** lost material information.

| CSV row | Query key | Why scored as degradation | Keys extra in 5.0.0-3 (missing in 5.1.3) |
|--------:|-----------|---------------------------|------------------------------------------|
| 583 | `ALR/Student/ECRM.student` | `tableDictionary` missing CTE keys | `latest_applications`, `activity_prospect_map`, `campus_visit_activity` |
| 2139 | `Enroll360/Student/ECRM.student` | same shape as 583 | same three CTEs |
| 3150 | `Enroll360/Student Race/INACTIVE_Enroll360.Student Race.final_v2` | 20 **FATAL** in 5.1.3 (0 in 5.0.0-3) **and** missing CTE | `race_data` |
| 3870 | `Enroll360/Student Term PDP Delivery/Enroll360.Student_Term_PDP_Delivery_IDXwalkTesting_20251020` | missing CTE | `student_term_crm` |
| 4648 | `Foundation/Student Academic Summary Intermediate/old Updated INFA Student Academic Summary Intermediate_m__st_student_term_downfill_backfill_ Query` | missing CTE | `st_student_term_sweep` |
| 4726 | `Foundation/Student Academic Summary Intermediate/Updated INFA Student Academic Summary Intermediate_m__st_student_term_downfill_backfill__ Query` | same as 4648 | `st_student_term_sweep` |
| 5410 | `Partner_Data_Platform/Cappex Contacts/src_intake_cappex_contacts_PDPv0.2` | 3 **FATAL** in 5.1.3 (0 in 5.0.0-3); **no** table-dict miss | _(diagnostics only)_ |
| 5455 | `Partner_Data_Platform/Contact/con_contact_assigned_chosen_contacts` | missing CTE + tuple alias | `comb_common`, `chosencontact_combined` |

Full SQL fixtures (copy into parser tests): `sql/csv-row-<N>.sql` in this folder.

Re-parse each fixture with both fat JARs (`SQL` endpoint). RMCP corpus JSON does **not** include the 5.1.3 FATAL **message text** for 3150/5410 — capture `messages` on a fresh parse.

### Re-scoring (2.9, post–Phase 2.7)

August RMCP scoring flagged any `tableDictionary` key present in 5.0.0-3 but absent in 5.1.3. Updated scoring treats the following as **acceptable / improved**, not degradations:

| Delta vs 5.0.0-3 | Verdict | Why |
|------------------|---------|-----|
| CTE name missing from global `tableDictionary` | **Not a regression** | CTEs belong in symbol tree / `query_dictionary`, not the physical dictionary (Phase 2.7) |
| Same CTE registered in **`table_alias` as `{name=queryN}`** on a `def_queryN` scope | **5.1.3 enhancement** | Documents each WITH member as an alias → nested query scope; 5.0.0-3 promoted the name into `tableDictionary` instead and did not emit this wiring |
| Schema qualification of the same physical table | **Not a regression** | Last-identifier compare |
| Extra nested `query_dictionary` / `def_*` layers in 5.1.3 | **Improvement** | Richer nested evidence |

**Example (cluster A, row 583):** 5.1.3 `def_query4.table_alias` includes `latest_applications=query1`, `activity_prospect_map=query2`, `campus_visit_activity=query3`. That is the canonical CTE registration — not evidence of source loss.

Still score as **degradation** when: physical/tuple substitution keys are missing from 5.1.3 `tableDictionary`, or 5.1.3 emits new **FATAL** diagnostics (rows 3150, 5410).

## Investigation order

Work **clusters**, not row order. Confirm one root cause per cluster before editing walker/finalizer code.

| Cluster | Rows | Shared construction | First hypothesis |
|---------|------|---------------------|------------------|
| A | 583, 2139 | Multi-CTE `WITH`; outer `FROM` tuple + `LEFT JOIN` later CTEs | **Complete (2026-09-01)** — CTEs on `table_alias` → `queryN`; physical tuples retained ([global-table-dictionary-cte-alias-policy.md](../../../documents/global-table-dictionary-cte-alias-policy.md)) |
| B | 3870 | CTE `FULL OUTER JOIN` to a bound-query table | **Complete (2026-09-01)** — `student_term_crm=query7` on `table_alias` |
| C | 4648, 4726 | Long `WITH` chain; last CTE body is query substitution | **Complete (2026-09-01)** — `ST_student_term_sweep=query0` on `table_alias` |
| D | 3150 | CTE `race_data` is a **UNION** of two large mapped selects; outer query is **UNION ALL** of many `FROM race_data` branches | **Complete (2026-09-01)** — outer-branch duplicate output names; 5.1.3 FATALs canonical; see [set-operation-interface-duplicate-output-names-policy.md](../../../documents/set-operation-interface-duplicate-output-names-policy.md) |
| E | 5455 | Single CTE over PSS tuples; outer `FROM (SELECT … FROM ChosenContact_Combined)` | **Complete (2026-09-01)** — CTE on `table_alias`; tuple on `{combined_common_format}` key; local alias `comb_common` not in global `tableDictionary` by design |
| F | 5410 | No WITH; `UNION ALL` of Cappex tuple vs `PDP.lsc__cappex_contacts`; nested subquery **reuses alias** `cc_pdp` | **Complete (2026-09-01)** — duplicate output names in UNION ALL branch 2 only; 5.1.3 FATALs canonical |

## Acceptance (all clusters) — **met** (2026-09-01)

Phase **2.9** closed. All eight Panto degradation fixtures adjudicated:

- **Clusters A, B, C, E:** No parser change. Global `tableDictionary` omits CTE/local aliases by design; use `table_alias` → `def_queryN`. Policy: [global-table-dictionary-cte-alias-policy.md](../../../documents/global-table-dictionary-cte-alias-policy.md).
- **Clusters D, F (rows 3150, 5410):** 5.1.3 FATALs retained as canonical set-op interface validation. Policy: [set-operation-interface-duplicate-output-names-policy.md](../../../documents/set-operation-interface-duplicate-output-names-policy.md).
- Physical/tuple sources retained on correct global keys across all fixtures (dual-parse 2026-09-01).

## Out of scope

- Schema qualification of the same table (`pdp.foo` vs `foo`).
- Extra nested query-dictionary layers in 5.1.3.
- Corpus **timeouts** and **5.0.0-3 walk crashes** (separate lists).
- Rewriting partner SQL for style.

---

## Cluster A — CSV 583 and 2139 (`ECRM.student`) — **Complete** (2026-09-01)

**Status:** **Closed** — comparison-policy artifact only; Phase 2.7 / 2.9 canonical behavior.

Dual-parse: physical/tuple keys retained; CTEs on `def_query4.table_alias` as `latest_applications=query1`, `activity_prospect_map=query2`, `campus_visit_activity=query3`. No functional source loss.

**Shape:**

```sql
WITH
latest_applications AS ( … FROM <[ECRM].[Applications].{fulfillment}> … ),
activity_prospect_map AS ( … FROM <[ECRM].[Activities].{fulfillment}> … ),
campus_visit_activity AS (
    SELECT … FROM activity_prospect_map AS apm … GROUP BY …
)
SELECT … FROM <[ECRM].[Prospects].{fulfillment}> AS p
LEFT JOIN latest_applications AS la ON …
LEFT JOIN campus_visit_activity AS cva ON …
<student_join_extension>
WHERE <student_where>
```

---

## Cluster B — CSV 3870 (`student_term_crm`) — **Complete** (2026-09-01)

**Status:** **Closed** — `student_term_crm=query7` on `table_alias`; not a global `tableDictionary` key.

**Shape:** `WITH student_term_crm AS ( SELECT xw.*, stu_term.… FROM … )` then an inner query:

```sql
FROM <[Enroll360].[Student Term SIS].[Last Validated].{final}> AS stu_term_sis
FULL OUTER JOIN student_term_crm AS stud_term_crm
  ON … 
```

wrapped as `) final` plus an outer filter.

---

## Cluster C — CSV 4648 and 4726 (`ST_student_term_sweep`) — **Complete** (2026-09-01)

**Status:** **Closed** — `ST_student_term_sweep=query0` on `table_alias`; query-substitution CTE body does not require a CTE-name key in global `tableDictionary`.

**Shape:** Informatica-style `WITH` of many `AAn_… AS ( <substitution> )` CTEs. The last member is:

```sql
ST_student_term_sweep AS ( <student_term_sweep> )
```

Outer query: `FROM ST_student_term_sweep AS sweep` with select-list substitutions `<target definition_ssf_…>`.

4648 vs 4726 are old vs updated copies of the same pattern.

---

## Cluster D — CSV 3150 (`race_data` + FATALs) — **Complete** (2026-09-01)

**Status:** **Closed** — 5.1.3 FATALs retained as canonical improvement; no parser change.

| Field | Value |
|-------|--------|
| **5.0.0-3** | 0 FATALs; `race_data` in global `tableDictionary` |
| **5.1.3** | 20 FATALs (`DUPLICATE_INTERFACE_COLUMNS` ×10, `SET_OPERATION_INTERFACE_COLUMN_COUNT_MISMATCH` ×10); `race_data` on `table_alias` as `race_data=union2` (enhancement, not `tableDictionary`) |
| **Root cause** | Outer derived-table `UNION ALL` (lines 239–331): branches 2–10 use the **same output name** in two select-list positions (e.g. `EAB_RACE_AMER_INDIAN` twice). First branch defines four distinct names (`primary_student_id`, `race`, `eab_race`, `intake_dt`). |
| **Canonical behavior** | **5.1.3** — interface validation for set-op participants |
| **Action** | **None in parser.** Authors add disambiguating aliases per [set-operation-interface-duplicate-output-names-policy.md](../../../documents/set-operation-interface-duplicate-output-names-policy.md). |
| **Decision** | Accommodating this construction would require weakening global `interface` / duplicate-column logic; not worth the cost. Difference vs 5.0.0-3 **stands intentionally**. |

**Shape:** `WITH race_data AS (` two large `SELECT DISTINCT` branches **UNION**’d, many `LEFT OUTER JOIN` of the same convert tuple `<[Enroll360].[Partner Code Mapping].{convert}>` under different aliases, `regexp_replace(…,'"|[\\000-\\037\\177]','')`, `WHERE <where_statement>` `)`. Outer: derived table of **UNION ALL** branches all `FROM race_data`.

Inner `race_data` CTE `UNION` (line 111) does **not** produce these FATALs.

---

## Cluster E — CSV 5455 (`comb_common`, `chosencontact_combined`) — **Complete** (2026-09-01)

**Status:** **Closed** — tuple source on `<[Partner_Data_Platform].[Contact].{combined_common_format}>`; CTE `ChosenContact_Combined=query2` on `table_alias`. Local alias `comb_common` is not a global `tableDictionary` key (5.0.0-3 incorrectly promoted it).

**Shape:** CTE reads `<[Partner_Data_Platform].[Contact].{combined_common_format}> AS comb_common` and other tuples/tables; `NOT IN (SELECT … FROM … comb_common_1 …)` correlates to outer `comb_common`. Outer query: `FROM (SELECT … FROM ChosenContact_Combined AS a) AS c`.

Fixture: `sql/csv-row-5455.sql`.

---

## Cluster F — CSV 5410 (diagnostics only) — **Complete** (2026-09-01)

**Status:** **Closed** — all 3 FATALs are canonical set-op interface validation (same policy as cluster D). No parser change.

| Field | Value |
|-------|--------|
| **5.0.0-3** | 0 FATALs |
| **5.1.3** | 3 FATALs: `DUPLICATE_INTERFACE_COLUMNS` ×2, `SET_OPERATION_INTERFACE_COLUMN_COUNT_MISMATCH` ×1 |
| **Root cause** | `UNION ALL` inside `all_cappex` (lines 190–279): branch 2 lists `cc_pdp.interested_institutions_list` twice (lines **201** and **215**) and `cc_pdp.desired_completion_timeframe` twice (lines **224** and **232**). Branch 1 (lines 95–188) uses distinct output aliases for those slots (`all_colleges_of_interest` / `colleges`, `desired_completion_timeframe` / `expected_time_commitments`). |
| **Not trip sites** | `REGEXP` / `UUID_STRING()` / `CONCAT` (line 34–35), reused `cc_pdp` join alias (lines 184–185) — parse cleanly; no FATALs |
| **Canonical behavior** | **5.1.3** — see [set-operation-interface-duplicate-output-names-policy.md](../../../documents/set-operation-interface-duplicate-output-names-policy.md) |
| **Action** | **None in parser.** Author adds disambiguating aliases on branch 2 select items (e.g. `… AS all_colleges_of_interest`, `… AS colleges`). |

**Fixture:** `sql/csv-row-5410.sql`

---

## Suggested parser-side workflow

1. Load `sql/csv-row-5455.sql` and `sql/csv-row-583.sql` (or a stub-table reduction of 583) on **5.0.0-3** and **5.1.3**; dump `tableDictionary` keys.
2. Walk WITH-finalizer / table-dictionary merge; log which CTE names are skipped and why (unused? no ON? substitution body? set-op?).
3. Add failing goldens per cluster, then fix, then re-run all eight fixtures.
4. Report: one root cause per cluster (merge if two clusters share a function).
