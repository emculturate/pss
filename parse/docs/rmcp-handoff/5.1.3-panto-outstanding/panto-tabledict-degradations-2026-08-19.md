# Workplan: PSS 5.1.3 missing table-dictionary sources vs 5.0.0-3

Date: 2026-08-19  
Kind: Defect (regression vs **5.0.0-3**)  
Audience: PSS parser agent — investigate why **5.1.3** omits sources that **5.0.0-3** still records, and restore them without dropping other dictionary entries.  
Corpus: Panto extracted-queries CSV dual-parse (`SQL` endpoint), RMCP compare after table-dict last-identifier scoring and outermost-only queryDictionary vs 5.0.0-3.

**Related:** Phase **2.7** — [phase-2.7-with-conditionless-join-finalizer.md](./phase-2.7-with-conditionless-join-finalizer.md) (**complete**, 2026-09-01). Global `tableDictionary` is for physical and tuple substitution sources only; **CTE aliases must not appear there**. Several cases below were originally scored as “missing CTE keys” in `tableDictionary` vs 5.0.0-3 — re-adjudicate under **2.9** for functional source loss in `query_dictionary` / symbol-tree structures.

## Goal

For each listed query, **5.1.3** `tableDictionary` (and, where noted, diagnostics) must not be a functional regression vs **5.0.0-3**:

1. Every **physical table or tuple substitution source** that **5.0.0-3** recorded in `tableDictionary` must still appear in **5.1.3** with equivalent token-site evidence (compare the last identifier after `.`; schema-only qualification is **not** this defect).
2. **CTE aliases** must **not** be required in global `tableDictionary` (5.1.3 policy; Phase 2.7 closed). For CTE-centered rows, require equivalent evidence in `query_dictionary` and `def_*` symbol-tree structures (`context_list`, `table_alias`, `interface`, `filters`).
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

## Investigation order

Work **clusters**, not row order. Confirm one root cause per cluster before editing walker/finalizer code.

| Cluster | Rows | Shared construction | First hypothesis |
|---------|------|---------------------|------------------|
| A | 583, 2139 | Multi-CTE `WITH`; outer `FROM` tuple + `LEFT JOIN` later CTEs; one CTE only used by another CTE | WITH finalizer / join-scope merge drops CTE names when the outer FROM is a PSS tuple (`<[…]>`) and CTEs are joined, or drops CTEs that are only referenced from another CTE (`activity_prospect_map` → `campus_visit_activity`) |
| B | 3870 | CTE `FULL OUTER JOIN` to a bound-query table | Finalizer treats `FULL OUTER JOIN <cte>` unlike inner/left, or drops CTE when the other side is a `{final}` tuple |
| C | 4648, 4726 | Long `WITH` chain; last CTE body is **query substitution** `<student_term_sweep>`; outer `FROM ST_student_term_sweep` | CTE whose definition is a `query`-typed substitution never registered in 5.1.3 `tableDictionary` |
| D | 3150 | CTE `race_data` is a **UNION** of two large mapped selects; outer query is **UNION ALL** of many `FROM race_data` branches | Set-op / CTE interface walk fatals and then omits the CTE from the dictionary |
| E | 5455 | Single CTE over PSS tuples; outer `FROM (SELECT … FROM ChosenContact_Combined)` | CTE name and/or tuple alias `comb_common` not promoted; nested `NOT IN (SELECT …)` correlates to outer `comb_common` |
| F | 5410 | No WITH; `UNION ALL` of Cappex tuple vs `PDP.lsc__cappex_contacts`; nested subquery **reuses alias** `cc_pdp`; `REGEXP` + `UUID_STRING()` | Diagnose the three FATALs first (likely regex, concat `\|\|`, or duplicate alias). Restore 5.0.0-3-level parse; dictionary was not the scored miss |

## Acceptance (all clusters)

- Fresh dual-parse of each `csv-row-*.sql`: **5.1.3** `tableDictionary` contains every missing key listed above (case-insensitive last segment is enough).
- Rows **3150** and **5410**: **5.1.3** FATAL count is **0**, or each remaining FATAL is justified as a true syntax error that **5.0.0-3** incorrectly accepted (document that decision; do not silently keep 20/3 FATALs).
- Goldens: `tableDictionary` + `messages` at minimum; add `sqlTree` / `symbolTable` if the miss is a walk skip.
- Regression: Phase 2.7 starter keeps trailing-clause tuple columns on the fulfillment tuple key; CTE alias `last_delivered_cte` is **not** a global `tableDictionary` key.

## Out of scope

- Schema qualification of the same table (`pdp.foo` vs `foo`).
- Extra nested query-dictionary layers in 5.1.3.
- Corpus **timeouts** and **5.0.0-3 walk crashes** (separate lists).
- Rewriting partner SQL for style.

---

## Cluster A — CSV 583 and 2139 (`ECRM.student`)

**Queries:** ALR and Enroll360 copies of the same ECRM student pattern (2139 is slightly shorter; same three CTE names).

**Missing in 5.1.3:** `latest_applications`, `activity_prospect_map`, `campus_visit_activity`.

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

`activity_prospect_map` is **not** in the outer FROM list; it is only a source of `campus_visit_activity`. **5.0.0-3** still listed it in `tableDictionary`. **5.1.3** should too (defined WITH members that contribute to the query are sources).

**Suggested minimal tests:** (1) three CTEs + outer LEFT JOINs as above with stub tables instead of tuples; (2) drop `activity_prospect_map` from the dictionary only if the CTE is unused — here it **is** used; (3) `INNER JOIN` instead of `LEFT JOIN` must still list all three CTEs.

---

## Cluster B — CSV 3870 (`student_term_crm`)

**Missing in 5.1.3:** `student_term_crm`.

**Shape:** `WITH student_term_crm AS ( SELECT xw.*, stu_term.… FROM … )` then an inner query:

```sql
FROM <[Enroll360].[Student Term SIS].[Last Validated].{final}> AS stu_term_sis
FULL OUTER JOIN student_term_crm AS stud_term_crm
  ON … 
```

wrapped as `) final` plus an outer filter. Confirm whether 5.1.3 registers the CTE under the join alias `stud_term_crm` only, or drops both.

---

## Cluster C — CSV 4648 and 4726 (`ST_student_term_sweep`)

**Missing in 5.1.3:** `st_student_term_sweep` (SQL name `ST_student_term_sweep`).

**Shape:** Informatica-style `WITH` of many `AAn_… AS ( <substitution> )` CTEs. The last member is:

```sql
ST_student_term_sweep AS ( <student_term_sweep> )
```

Outer query: `FROM ST_student_term_sweep AS sweep` with select-list substitutions `<target definition_ssf_…>`.

**Hypothesis:** a CTE whose body is a **query** substitution node is omitted from 5.1.3 table-dictionary merge, even though the outer FROM names that CTE.

**Minimal test:**

```sql
WITH sweep_src AS ( <q> )
SELECT * FROM sweep_src AS s
```

(or `WITH sweep_src AS (SELECT 1 AS x) SELECT * FROM sweep_src`) plus the substitution form if the grammar allows `<student_term_sweep>` as a CTE body.

4648 vs 4726 are old vs updated copies of the same pattern; one fix should cover both.

---

## Cluster D — CSV 3150 (`race_data` + FATALs)

**Missing in 5.1.3:** `race_data`.  
**Also:** FATAL **20** vs **0**.

**Shape:** `WITH race_data AS (` two large `SELECT DISTINCT` branches **UNION**’d, many `LEFT OUTER JOIN` of the same convert tuple `<[Enroll360].[Partner Code Mapping].{convert}>` under different aliases, `regexp_replace(…,'"|[\\000-\\037\\177]','')`, `WHERE <where_statement>` `)`. Outer: derived table of **UNION ALL** branches all `FROM race_data`.

Investigate FATALs first (character class in regex, set-op interface width, repeated join of one tuple). If the walk aborts, the CTE may never be registered.

---

## Cluster E — CSV 5455 (`comb_common`, `chosencontact_combined`)

Smallest fixture (~12 lines). **Missing:** tuple alias `comb_common` and CTE `chosencontact_combined` (`ChosenContact_Combined` in SQL).

**Shape:** CTE reads `<[Partner_Data_Platform].[Contact].{combined_common_format}> AS comb_common` and other tuples/tables; `NOT IN (SELECT … FROM … comb_common_1 …)` currently correlates `ST_1.intake_type_key = comb_common.source_type_key` (outer alias). Outer query: `FROM (SELECT … FROM ChosenContact_Combined AS a) AS c`.

**5.0.0-3** listed both the CTE and `comb_common`. **5.1.3** should keep the CTE as a source of the outer FROM and keep the tuple (or last-segment equivalent) used inside the CTE.

Use this file as the first goldens candidate: `sql/csv-row-5455.sql`.

---

## Cluster F — CSV 5410 (diagnostics only)

Not a table-dictionary miss. **5.1.3** reports **3 FATAL**, **5.0.0-3** **0**.

Likely trip sites in `sql/csv-row-5410.sql`:

- `NULLIF(TRIM(agg.email_address),'') REGEXP '[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,3}'`
- `CONCAT(UUID_STRING(),'@test.eab.com')`
- Derived table `all_cappex` / `agg` over `UNION ALL`
- `LEFT OUTER JOIN (SELECT … FROM PDP.lsc__cappex_contacts cc_pdp) cc_pdp` (alias reused)

Capture the three 5.1.3 messages, map each to a grammar or walk site, and match 5.0.0-3 acceptance unless 5.0.0-3 was wrong.

---

## Suggested parser-side workflow

1. Load `sql/csv-row-5455.sql` and `sql/csv-row-583.sql` (or a stub-table reduction of 583) on **5.0.0-3** and **5.1.3**; dump `tableDictionary` keys.
2. Walk WITH-finalizer / table-dictionary merge; log which CTE names are skipped and why (unused? no ON? substitution body? set-op?).
3. Add failing goldens per cluster, then fix, then re-run all eight fixtures.
4. Report: one root cause per cluster (merge if two clusters share a function).
