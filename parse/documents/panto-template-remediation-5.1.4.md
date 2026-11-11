# Panto template and snippet remediation for PSS 5.1.4 validation

**Date:** 2026-11-11  
**Audience:** Panto query template and snippet authors (ALR, Enroll360, Partner_Data_Platform, PDP, Pagoda, Foundation, Finaid, AMS, Program Assistant teams)  
**Parser:** `pss-parse-5.1.4-1` (Fall-2026-Extensions, commit `fab17ba`)  
**Source data:** live extracts `bound_queries_11-10-2026.csv` (6,551 bound queries) and `snippets-11-10-2026.csv` (87,905 snippets), compared against `pss-parse-5.0.0-3`

## Summary

PSS 5.1.4 validates SQL that 5.0.0-3 accepted without checking. In the 11-10-2026 extract, **172 bound queries and snippets** that were clean on 5.0.0-3 now report FATAL errors on 5.1.4 because the SQL itself is wrong. These are real defects: most would fail, or silently return wrong columns, when run in Snowflake. Each falls into one of seven patterns below, and each pattern has a mechanical fix.

| Pattern | What is wrong | Cases | Distinct templates / bundles |
|---|---|---|---|
| 1 | A UNION branch has two output columns with the same name | 111 | 37 |
| 2 | One SELECT list has two output columns with the same name | 17 | 13 |
| 3 | UNION branches have different numbers of columns | 1 | 1 |
| 4 | A table alias is used but never declared (and no join extension can supply it) | 16 | 15 |
| 5 | A column is read from a derived table or CTE that does not output it | 14 | 14 |
| 6 | Column-name variables are used across a derived-table boundary | 11 | 7 |
| 7 | Typos (stray characters) | 2 | 2 |

Many rows share a template name. The extract has no version or tenant column, so these are presumably per-tenant or per-version copies; fixing the source template should clear several rows at once. The index at the end lists every case.
### Not your problem (excluded from this document)

These are not author errors and are **not** listed in the index. The parser team is fixing them:

- **Join extension templates.** Templates whose missing aliases are supplied by a `<join_extension.…>` snippet now report SEVERE_WARNING plus `JOIN_EXTENSION_REQUIRED` instead of FATAL (workplan Entry 14). The warning means the join extension must be substituted with a snippet that declares those aliases; it is not optional for that template.
- **15 parser defects:** a derived table that unions with a `<union_add.…>` variable loses its columns (q2084, q2087, q2088); `DATEADD`/`DATEDIFF` date parts read as columns (q5866); Snowflake non-reserved words rejected — `position` (q5909, q5910, q5913), `input` (q5390, q5394), `path` (q2090), `truncate(…)` (q3399, q3438, q3490, q3546), and array literals `['a','b']` (s82233).

## How to find a case in the extract

Each case has an ID such as `q862` or `s77918`:

- `qN` — the bound query at **CSV record N** of `bound_queries_11-10-2026.csv`, counting the header as record 1 (so `q2` is the first data row). Records are CSV records, not physical file lines: `query_sql` contains newlines.
- `sN` — the snippet at **CSV record N** of `snippets-11-10-2026.csv`, which has no header (so `s1` is the first row). Columns are: domain, bundle list (`name:version|…`), an unlabelled number, endpoint (`tuple`, `query`, `in_list`, …), snippet text.
- Line and column positions in error messages are 1-based lines and 0-based character offsets **within the query or snippet text**.

> **Identifier gap — action needed.** Neither extract carries a stable identifier. In the bound-query file, 590 (domain, entity, query name) combinations repeat across 3,695 of the 6,551 rows with no version, tenant, or query ID column to tell them apart. The snippet file has no snippet ID, no variable name, and no header. Record numbers are the only exact locator and will change with the next extract. **We should re-extract both lists with their Panto identifiers** — at minimum the bound query ID and version/tenant, and the snippet ID, snippet name, substitution variable name, and owning bundle ID. With those we can send each author a targeted instruction ("template 12345 v3, line 236: alias the second EAB_RACE_BLACK as eab_race") instead of a record number.

## Pattern 1 — duplicate output name inside a UNION branch (111 cases)

**What the parser reports:** `DUPLICATE_INTERFACE_COLUMNS` for the branch, usually followed by `SET_OPERATION_INTERFACE_COLUMN_COUNT_MISMATCH` ("UNION has different column counts. Expected 3 … but there were 2 …").

**Why:** A UNION takes its column names from the first branch and matches the other branches by position. When a later branch selects the same column twice without aliases, both outputs carry the same name; the branch's interface collapses them, and its column count no longer matches. Two different expressions given the same alias have the same effect.

Typical example (ALR / Student Race, `q862`, lines 235–238):

```sql
-- first branch: select primary_student_id, race, eab_race ...
union all
select primary_student_id
, EAB_RACE_AMER_INDIAN
, EAB_RACE_AMER_INDIAN
from race_data
where EAB_RACE_AMER_INDIAN is not null
```

**Fix:** alias every column of every branch to the matching first-branch name:

```sql
union all
select primary_student_id
, EAB_RACE_AMER_INDIAN as race
, EAB_RACE_AMER_INDIAN as eab_race
from race_data
where EAB_RACE_AMER_INDIAN is not null
```

Other forms in this group:

- `q3106` (Student Relationship): two expressions are both `as relationship_fname`; the second (the last-name part) should be `as relationship_lname`.
- `q2154`, `q2155` (Enroll360 Student multifile): `stud.origin_source_date` is selected under two outputs with the same name in one branch; give each its own first-branch name.
- `q5249`–`q5251` (Cappex Contacts): `cc_pdp.interested_institutions_list` and `cc_pdp.desired_completion_timeframe` appear twice in one branch; each occurrence must be aliased to the first-branch column it fills (or removed if it is a copy-paste duplicate).
- `s36703` (Partner Code Mapping bundle): branches 3 and 4 select `target_…, target_…, target_…, target_…`; alias them `as source_partnercontact_id, as source_partner_system_name, as target_partnercontact_id, as target_partner_system_name`.

Background and a full worked example: `parse/documents/set-operation-interface-duplicate-output-names-policy.md`.

## Pattern 2 — duplicate output name in one SELECT list (17 cases)

**What the parser reports:** `DUPLICATE_INTERFACE_COLUMNS`, naming the two source expressions (for example "event_agg.min_event_date at (l:111 c:28) and stud_inq.first_campus_visit_date at (l:123 c:59)").

**Why:** Two outputs of the same SELECT have the same name, either because a line was pasted twice or because two different expressions were given the same alias. Any outer query, CTE consumer, or generated export that reads that name cannot tell which one it gets.

Examples:

- `q5217` (ACS Emails): `a.<unsubscribeForm> as unsubscribeform`, `a.<variantParent>`, `a.<variantSentCount>`, `a.<variantStartDate>` each appear twice — delete the duplicate lines.
- `q2168` (Enroll360 Student final_v2): `first_campus_visit_dt`, `last_campus_visit_dt` and one indicator are each defined by two different expressions — keep one definition (or rename one output).
- Foundation tuple snippets (`s77918`, `s78210`, `s80360`, …): generated SQL such as `… as ACADEMIC_PERIOD_END, <Lookup Procedure_lkp_academic_period.ACADEMIC_PERIOD_END> as ACADEMIC_PERIOD_END` — rename or drop the second output; in `s80360` the `out_transfer_student_ind` output should probably be aliased `out_transfer_student_ind`, not `transfer_student_ind`.

**Fix:** make every output name in a SELECT list unique. The error message gives the line and column of both occurrences.

## Pattern 3 — UNION branches with different column counts (1 case)

**What the parser reports:** `SET_OPERATION_INTERFACE_COLUMN_COUNT_MISMATCH` without a duplicate-column error.

`q1703` (AMS gifts_funds): the first branch outputs 7 columns ending in `rn`; the second branch outputs 6 and has no `rn`. Snowflake rejects this at run time.

**Fix:** add the missing column to the short branch in the same position (for example `, null as rn`, or the same `row_number()` expression the first branch uses).

## Pattern 4 — undeclared or out-of-scope table alias (16 cases)

**What the parser reports:** `QUALIFIED_COLUMN_NOT_FOUND_IN_TABLE` ("Source Table not found for Column 'x' at (l:…). No alias or table called 'alias'"), still FATAL because the template has **no** join extension variable that could supply the alias.

**Why and fix**, by sub-case:

- **Alias never declared** (`q508`, `q746`, `q1323`, `q1324`, `q1476`, `q1669`, `q33`, `q4154`, `q5112`, `q6290`, `q6304`): the query reads `alias.column` but no FROM/JOIN in scope declares `alias`. Add the missing join (often a `Partner Code Mapping` join, as with `pcm_first_gen` in `q1323`/`q1324`), or change the reference to the alias that is actually joined.
- **Typo** (`q3870`): `stu_term_crmstu_term_crm` — correct the alias.
- **Alias used outside the derived table that declares it** (`q1280`, `q2910`, `q3676`, `q5137`): an alias declared inside `( … ) x` is not visible outside it. Expose the needed column in the derived table's SELECT list and reference it as `x.column`.
- **If the alias is meant to come from a join extension snippet**, add the `<join_extension.…>` variable to the FROM clause. 5.1.4 then reports SEVERE_WARNING + `JOIN_EXTENSION_REQUIRED` instead of FATAL, and the template must always be bound to a snippet that declares the alias.

## Pattern 5 — column not output by the derived table or CTE (14 cases)

**What the parser reports:** `QUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIAS` ("Qualified column 'c' … was not found in output interface of query alias 'x'") or `UNQUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIASES`.

**Why:** A derived table or CTE exposes only what its SELECT list outputs. Columns of the tables inside it are not visible outside.

Examples and fixes:

- `q5124` (Pagoda TERM): `sorrtrm.observation_tm` is filtered in the outer WHERE, but the `sorrtrm` derived table outputs only `term_code`, `sorrtrm_start_date`, `sorrtrm_end_date`. Add `observation_tm` to the inner SELECT (and its GROUP BY, or aggregate it), or move the filter inside.
- `q2969` (Enroll360 Student Race): defines `… end as eab_race_unknowny` and reads `agg.eab_race_unknown` — fix the alias typo.
- `q6293` (PDP_UG rsc partner contacts): `student_cte` outputs only `primary_student_id` and `rn`; the outer query reads `ps.partnercontact_id`. Add `ps1.partnercontact_id` to the CTE.
- `q3700` (Student Term Attributes SIS): `unpivot_agg.src_attribute_type` is not an output of `unpivot_agg`. Add it to the CTE or remove the join condition.
- `q3731` (INACTIVE pilot): `agg.partnercontact_id` is referenced inside the derived table that is itself aliased `agg` — a query cannot see its own alias; use the inner alias.
- Foundation tuple snippets (`s78384`, `s78400`, `s78415`, `s78701`, `s78754`–`s78756`, `s78982`, `s78983`): the outer window expressions read `REGISTERED_IND` / `TRANSFER_COURSE_IND`, but the inner `(select … ) as ex_ex_1` / `so_sr_ey_1` only outputs `STUDENT_ID`, `ROWNUMBER` and similar. Add the indicator column to the inner SELECT.

## Pattern 6 — column-name variables across a derived-table boundary (11 cases)

**What the parser reports:** `QUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIAS` or `UNQUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIASES` where one side is a column-name variable such as `<ACS Contact ID>`.

**Why:** A column-name variable is replaced only at generation time, so the parser cannot know that `<Contact Key>` will become `contact_key`. When the name inside a derived table and the name used outside it are written differently — one as a variable, one as a literal — the parser cannot match them. There are two mirror-image forms:

- **Variable inside, literal outside** (`q5314`, `q5335`, `s8491`, `s8492`):

  ```sql
  LEFT JOIN (SELECT distinct a.<Contact Key>, a.<Dummy Email>
             FROM <[Partner_Data_Platform].[Contact Acquia Crosswalk]> as a) as xwalk
  ON chosen_cont.contact_key = xwalk.contact_key
  ```

- **Literal inside, variable outside** (`q5201`, `q5286`–`q5291`):

  ```sql
  LEFT OUTER JOIN (select distinct act.acs_contact_id, act.funnel_status_label from … ) acs_act
  on acs_act.<ACS Contact ID> = …
  ```

**Fix:** inside a derived table or CTE, always give a variable column a literal alias, and outside refer only to that literal name:

```sql
LEFT JOIN (SELECT distinct a.<Contact Key> as contact_key, a.<Dummy Email> as dummy_email
           FROM <[Partner_Data_Platform].[Contact Acquia Crosswalk]> as a) as xwalk
ON chosen_cont.contact_key = xwalk.contact_key
```

```sql
on acs_act.acs_contact_id = …
```

This makes the template's interface independent of how the variable is bound. (The parser team may relax this pattern to a SEVERE_WARNING; the rewrite is correct either way.)

## Pattern 7 — typos (2 cases)

- `q5128` (Pagoda TRANSFER_COURSEWORK, line 59): `order by]` — delete the `]`.
- `s6989` (ALR Partner Code Mapping IN_LIST, line 18): `'American Indian or Alaska Native, Asian',]` — delete the `]`; line 21 also ends with a stray `\`.

## Verifying a fix

Parse the corrected template with the `SQL` endpoint (or the snippet with its endpoint; IN_LIST values must be wrapped in parentheses) on `pss-parse-5.1.4-1` and confirm `getFatalErrorCount()` is 0. SEVERE_WARNINGs with code `JOIN_EXTENSION_REQUIRED` are expected for templates that rely on join extension snippets.

## Index of cases

Generated from the 2026-11-10 run (`comparison/runs/final514` versus `runs/base5003` + `runs/inlist5003` in the local evidence archive; regenerate with its `comparison/remediation_index.py`). Archive location and replay instructions are documented in [the 5.1.4 workplan, Entry 1](sql-grammar-5.1.4-workplan.md#local-corpus-evidence-and-replay-tools-archived-2026-11-11). "First fatal message" shows only the first of the case's FATALs.

### Pattern 1 — duplicate output name inside a UNION branch

**Distinct templates / bundles (37):**

| Domain / entity / query name (or bundle) | Rows | Case IDs |
|---|---|---|
| ALR / Student Race / ALR.Student Race.final | 30 | q860, q861, q862, q863, q864, q865, q866, q867, q868, q869, q870, q871, q872, q873, q874, q875, q876, q877, q878, q879, q880, q881, q882, q883, q884, q885, q886, q887, q888, q889 |
| ALR / Student Race / ALR.Student Race.final.BryanCollege.ExcludeAdultHistorical | 1 | q891 |
| ALR / Student Race / ALR.Student Race.final.PBGS | 1 | q893 |
| ALR / Student Race / ALR.Student Race.final.cui_leadership | 1 | q892 |
| ALR / Student Race / ALR.Student Race.final_v1 | 1 | q894 |
| ALR / Student Race / ALR.Student Race.final_with_intake_dt | 1 | q895 |
| ALR / Student Race / RegisAdult.ALR.Student Race.final | 1 | q900 |
| ALR / Student Race / RegisGrad.ALR.Student Race.final | 1 | q901 |
| ALR / Student Race / RegisUPharm.ALR.Student Race.final | 1 | q902 |
| Enroll360 / Student / Enroll360.Student.final.multifile_v2 | 1 | q2154 |
| Enroll360 / Student / Enroll360.Student.final.multifile_v3 | 1 | q2155 |
| Enroll360 / Student Race / Enroll360.Student Race.final | 12 | q2964, q2965, q2966, q2973, q2974, q2975, q2976, q2978, q2979, q2980, q2981, q2982 |
| Enroll360 / Student Race / Enroll360.Student Race.final.multifile | 2 | q2984, q2985 |
| Enroll360 / Student Race / Enroll360.Student Race.final.multifile v2 | 1 | q2986 |
| Enroll360 / Student Race / Enroll360.Student Race.final.multifile_v3 | 1 | q2987 |
| Enroll360 / Student Race / Enroll360.Student Race.final_v2 | 2 | q2988, q2989 |
| Enroll360 / Student Race / Enroll360.Student Race.staging | 1 | q2991 |
| Enroll360 / Student Race / INACTIVE_DNU_Enroll360.Student Race.final | 3 | q2996, q2997, q2998 |
| Enroll360 / Student Race / INACTIVE_DNU_Enroll360.Student Race.final_v2 | 2 | q2999, q3000 |
| Enroll360 / Student Race / INACTIVE_Enroll360.Student Race.final | 17 | q3002, q3004, q3006, q3007, q3008, q3009, q3010, q3011, q3012, q3013, q3014, q3015, q3022, q3023, q3024, q3025, q3026 |
| Enroll360 / Student Race / INACTIVE_Enroll360.Student Race.final.multifile | 1 | q3028 |
| Enroll360 / Student Race / INACTIVE_Enroll360.Student Race.final.multifile_v2 | 1 | q3029 |
| Enroll360 / Student Race / INACTIVE_Enroll360.Student Race.final.psn_test | 1 | q3030 |
| Enroll360 / Student Race / INACTIVE_Enroll360.Student Race.final_v2 | 13 | q3031, q3032, q3033, q3034, q3035, q3036, q3037, q3038, q3039, q3040, q3041, q3042, q3044 |
| Enroll360 / Student Race / INACTIVE_Enroll360.Student Race.final_v3 | 1 | q3045 |
| Enroll360 / Student Race / INACTIVE_RegisU.2.0.Enroll360.Student Race.final | 1 | q3046 |
| Enroll360 / Student Race / INACTIVE_RegisU.2.0.Enroll360.Student Race.final_v2 | 1 | q3047 |
| Enroll360 / Student Race / INACTIVE_TESTEnroll360.Student Race.final | 1 | q3048 |
| Enroll360 / Student Race / J&W.Enroll360.Student Race.final | 1 | q3049 |
| Enroll360 / Student Race / J&W.Enroll360.Student Race.final_v2 | 1 | q3050 |
| Enroll360 / Student Race SIS / DataOrgPilot.Enroll360.Student Race SIS.final | 1 | q3059 |
| Enroll360 / Student Race SIS / Enroll360.Student Race SIS.final | 1 | q3060 |
| Enroll360 / Student Race SIS / INACTIVE_Enroll360.Student Race SIS.final_pilot_v1 | 1 | q3062 |
| Enroll360 / Student Relationship / INACTIVE_Enroll360.Student Relationship.final | 1 | q3106 |
| Enroll360 / bundle `Enroll360.Partner Code Mapping.All:v01` | 1 | s36703 |
| Partner_Data_Platform / Cappex Contacts / src_intake_cappex_contacts_PDPv0.2 | 2 | q5249, q5250 |
| Partner_Data_Platform / Cappex Contacts / src_intake_cappex_contacts_PDPv0.2_test | 1 | q5251 |

**All cases:**

| # | Case ID | Domain / entity / query name (or bundle) | First error at (line:col) | Fatal count | First fatal message |
|---|---|---|---|---|---|
| 1 | `q860` | ALR / Student Race / ALR.Student Race.final | 236:2 | 12 | Duplicate interface columns defined: EAB_RACE_AMER_INDIAN at (l:236 c:2) and EAB_RACE_AMER_INDIAN at (l:237 c:2). |
| 2 | `q861` | ALR / Student Race / ALR.Student Race.final | 236:2 | 12 | Duplicate interface columns defined: EAB_RACE_AMER_INDIAN at (l:236 c:2) and EAB_RACE_AMER_INDIAN at (l:237 c:2). |
| 3 | `q862` | ALR / Student Race / ALR.Student Race.final | 236:2 | 12 | Duplicate interface columns defined: EAB_RACE_AMER_INDIAN at (l:236 c:2) and EAB_RACE_AMER_INDIAN at (l:237 c:2). |
| 4 | `q863` | ALR / Student Race / ALR.Student Race.final | 236:2 | 12 | Duplicate interface columns defined: EAB_RACE_AMER_INDIAN at (l:236 c:2) and EAB_RACE_AMER_INDIAN at (l:237 c:2). |
| 5 | `q864` | ALR / Student Race / ALR.Student Race.final | 348:2 | 12 | Duplicate interface columns defined: EAB_RACE_AMER_INDIAN at (l:348 c:2) and EAB_RACE_AMER_INDIAN at (l:349 c:2). |
| 6 | `q865` | ALR / Student Race / ALR.Student Race.final | 236:2 | 12 | Duplicate interface columns defined: EAB_RACE_AMER_INDIAN at (l:236 c:2) and EAB_RACE_AMER_INDIAN at (l:237 c:2). |
| 7 | `q866` | ALR / Student Race / ALR.Student Race.final | 236:2 | 12 | Duplicate interface columns defined: EAB_RACE_AMER_INDIAN at (l:236 c:2) and EAB_RACE_AMER_INDIAN at (l:237 c:2). |
| 8 | `q867` | ALR / Student Race / ALR.Student Race.final | 236:2 | 12 | Duplicate interface columns defined: EAB_RACE_AMER_INDIAN at (l:236 c:2) and EAB_RACE_AMER_INDIAN at (l:237 c:2). |
| 9 | `q868` | ALR / Student Race / ALR.Student Race.final | 236:2 | 12 | Duplicate interface columns defined: EAB_RACE_AMER_INDIAN at (l:236 c:2) and EAB_RACE_AMER_INDIAN at (l:237 c:2). |
| 10 | `q869` | ALR / Student Race / ALR.Student Race.final | 241:2 | 12 | Duplicate interface columns defined: EAB_RACE_AMER_INDIAN at (l:241 c:2) and EAB_RACE_AMER_INDIAN at (l:242 c:2). |
| 11 | `q870` | ALR / Student Race / ALR.Student Race.final | 272:2 | 12 | Duplicate interface columns defined: EAB_RACE_AMER_INDIAN at (l:272 c:2) and EAB_RACE_AMER_INDIAN at (l:273 c:2). |
| 12 | `q871` | ALR / Student Race / ALR.Student Race.final | 244:2 | 12 | Duplicate interface columns defined: EAB_RACE_AMER_INDIAN at (l:244 c:2) and EAB_RACE_AMER_INDIAN at (l:245 c:2). |
| 13 | `q872` | ALR / Student Race / ALR.Student Race.final | 244:2 | 12 | Duplicate interface columns defined: EAB_RACE_AMER_INDIAN at (l:244 c:2) and EAB_RACE_AMER_INDIAN at (l:245 c:2). |
| 14 | `q873` | ALR / Student Race / ALR.Student Race.final | 246:2 | 12 | Duplicate interface columns defined: EAB_RACE_AMER_INDIAN at (l:246 c:2) and EAB_RACE_AMER_INDIAN at (l:247 c:2). |
| 15 | `q874` | ALR / Student Race / ALR.Student Race.final | 125:2 | 12 | Duplicate interface columns defined: EAB_RACE_AMER_INDIAN at (l:125 c:2) and EAB_RACE_AMER_INDIAN at (l:126 c:2). |
| 16 | `q875` | ALR / Student Race / ALR.Student Race.final | 246:2 | 12 | Duplicate interface columns defined: EAB_RACE_AMER_INDIAN at (l:246 c:2) and EAB_RACE_AMER_INDIAN at (l:247 c:2). |
| 17 | `q876` | ALR / Student Race / ALR.Student Race.final | 245:2 | 12 | Duplicate interface columns defined: EAB_RACE_AMER_INDIAN at (l:245 c:2) and EAB_RACE_AMER_INDIAN at (l:246 c:2). |
| 18 | `q877` | ALR / Student Race / ALR.Student Race.final | 244:2 | 12 | Duplicate interface columns defined: EAB_RACE_AMER_INDIAN at (l:244 c:2) and EAB_RACE_AMER_INDIAN at (l:245 c:2). |
| 19 | `q878` | ALR / Student Race / ALR.Student Race.final | 244:2 | 12 | Duplicate interface columns defined: EAB_RACE_AMER_INDIAN at (l:244 c:2) and EAB_RACE_AMER_INDIAN at (l:245 c:2). |
| 20 | `q879` | ALR / Student Race / ALR.Student Race.final | 244:2 | 12 | Duplicate interface columns defined: EAB_RACE_AMER_INDIAN at (l:244 c:2) and EAB_RACE_AMER_INDIAN at (l:245 c:2). |
| 21 | `q880` | ALR / Student Race / ALR.Student Race.final | 244:2 | 12 | Duplicate interface columns defined: EAB_RACE_AMER_INDIAN at (l:244 c:2) and EAB_RACE_AMER_INDIAN at (l:245 c:2). |
| 22 | `q881` | ALR / Student Race / ALR.Student Race.final | 244:2 | 12 | Duplicate interface columns defined: EAB_RACE_AMER_INDIAN at (l:244 c:2) and EAB_RACE_AMER_INDIAN at (l:245 c:2). |
| 23 | `q882` | ALR / Student Race / ALR.Student Race.final | 244:2 | 12 | Duplicate interface columns defined: EAB_RACE_AMER_INDIAN at (l:244 c:2) and EAB_RACE_AMER_INDIAN at (l:245 c:2). |
| 24 | `q883` | ALR / Student Race / ALR.Student Race.final | 244:2 | 12 | Duplicate interface columns defined: EAB_RACE_AMER_INDIAN at (l:244 c:2) and EAB_RACE_AMER_INDIAN at (l:245 c:2). |
| 25 | `q884` | ALR / Student Race / ALR.Student Race.final | 356:2 | 12 | Duplicate interface columns defined: EAB_RACE_AMER_INDIAN at (l:356 c:2) and EAB_RACE_AMER_INDIAN at (l:357 c:2). |
| 26 | `q885` | ALR / Student Race / ALR.Student Race.final | 244:2 | 12 | Duplicate interface columns defined: EAB_RACE_AMER_INDIAN at (l:244 c:2) and EAB_RACE_AMER_INDIAN at (l:245 c:2). |
| 27 | `q886` | ALR / Student Race / ALR.Student Race.final | 244:2 | 12 | Duplicate interface columns defined: EAB_RACE_AMER_INDIAN at (l:244 c:2) and EAB_RACE_AMER_INDIAN at (l:245 c:2). |
| 28 | `q887` | ALR / Student Race / ALR.Student Race.final | 246:2 | 12 | Duplicate interface columns defined: EAB_RACE_AMER_INDIAN at (l:246 c:2) and EAB_RACE_AMER_INDIAN at (l:247 c:2). |
| 29 | `q888` | ALR / Student Race / ALR.Student Race.final | 247:2 | 12 | Duplicate interface columns defined: EAB_RACE_AMER_INDIAN at (l:247 c:2) and EAB_RACE_AMER_INDIAN at (l:248 c:2). |
| 30 | `q889` | ALR / Student Race / ALR.Student Race.final | 132:2 | 12 | Duplicate interface columns defined: EAB_RACE_AMER_INDIAN at (l:132 c:2) and EAB_RACE_AMER_INDIAN at (l:133 c:2). |
| 31 | `q891` | ALR / Student Race / ALR.Student Race.final.BryanCollege.ExcludeAdultHistorical | 128:2 | 12 | Duplicate interface columns defined: EAB_RACE_AMER_INDIAN at (l:128 c:2) and EAB_RACE_AMER_INDIAN at (l:129 c:2). |
| 32 | `q892` | ALR / Student Race / ALR.Student Race.final.cui_leadership | 126:2 | 12 | Duplicate interface columns defined: EAB_RACE_AMER_INDIAN at (l:126 c:2) and EAB_RACE_AMER_INDIAN at (l:127 c:2). |
| 33 | `q893` | ALR / Student Race / ALR.Student Race.final.PBGS | 124:2 | 12 | Duplicate interface columns defined: EAB_RACE_AMER_INDIAN at (l:124 c:2) and EAB_RACE_AMER_INDIAN at (l:125 c:2). |
| 34 | `q894` | ALR / Student Race / ALR.Student Race.final_v1 | 238:2 | 12 | Duplicate interface columns defined: EAB_RACE_AMER_INDIAN at (l:238 c:2) and EAB_RACE_AMER_INDIAN at (l:239 c:2). |
| 35 | `q895` | ALR / Student Race / ALR.Student Race.final_with_intake_dt | 124:2 | 12 | Duplicate interface columns defined: EAB_RACE_AMER_INDIAN at (l:124 c:2) and EAB_RACE_AMER_INDIAN at (l:125 c:2). |
| 36 | `q900` | ALR / Student Race / RegisAdult.ALR.Student Race.final | 132:2 | 12 | Duplicate interface columns defined: EAB_RACE_AMER_INDIAN at (l:132 c:2) and EAB_RACE_AMER_INDIAN at (l:133 c:2). |
| 37 | `q901` | ALR / Student Race / RegisGrad.ALR.Student Race.final | 133:2 | 12 | Duplicate interface columns defined: EAB_RACE_AMER_INDIAN at (l:133 c:2) and EAB_RACE_AMER_INDIAN at (l:134 c:2). |
| 38 | `q902` | ALR / Student Race / RegisUPharm.ALR.Student Race.final | 125:2 | 12 | Duplicate interface columns defined: EAB_RACE_AMER_INDIAN at (l:125 c:2) and EAB_RACE_AMER_INDIAN at (l:126 c:2). |
| 39 | `q2154` | Enroll360 / Student / Enroll360.Student.final.multifile_v2 | 283:61 | 2 | Duplicate interface columns defined: stud.origin_source_date at (l:283 c:61) and stud.origin_source_date at (l:289 c:45). |
| 40 | `q2155` | Enroll360 / Student / Enroll360.Student.final.multifile_v3 | 268:61 | 2 | Duplicate interface columns defined: stud.origin_source_date at (l:268 c:61) and stud.origin_source_date at (l:274 c:45). |
| 41 | `q2964` | Enroll360 / Student Race / Enroll360.Student Race.final | 170:2 | 12 | Duplicate interface columns defined: EAB_RACE_AMER_INDIAN at (l:170 c:2) and EAB_RACE_AMER_INDIAN at (l:171 c:2). |
| 42 | `q2965` | Enroll360 / Student Race / Enroll360.Student Race.final | 257:33 | 12 | Duplicate interface columns defined: EAB_RACE_AMER_INDIAN at (l:257 c:33) and EAB_RACE_AMER_INDIAN at (l:258 c:2). |
| 43 | `q2966` | Enroll360 / Student Race / Enroll360.Student Race.final | 260:33 | 12 | Duplicate interface columns defined: EAB_RACE_AMER_INDIAN at (l:260 c:33) and EAB_RACE_AMER_INDIAN at (l:261 c:2). |
| 44 | `q2973` | Enroll360 / Student Race / Enroll360.Student Race.final | 367:33 | 12 | Duplicate interface columns defined: EAB_RACE_AMER_INDIAN at (l:367 c:33) and EAB_RACE_AMER_INDIAN at (l:368 c:2). |
| 45 | `q2974` | Enroll360 / Student Race / Enroll360.Student Race.final | 234:33 | 12 | Duplicate interface columns defined: EAB_RACE_AMER_INDIAN at (l:234 c:33) and EAB_RACE_AMER_INDIAN at (l:235 c:2). |
| 46 | `q2975` | Enroll360 / Student Race / Enroll360.Student Race.final | 239:2 | 12 | Duplicate interface columns defined: EAB_RACE_AMER_INDIAN at (l:239 c:2) and EAB_RACE_AMER_INDIAN at (l:240 c:2). |
| 47 | `q2976` | Enroll360 / Student Race / Enroll360.Student Race.final | 228:2 | 12 | Duplicate interface columns defined: EAB_RACE_AMER_INDIAN at (l:228 c:2) and EAB_RACE_AMER_INDIAN at (l:229 c:2). |
| 48 | `q2978` | Enroll360 / Student Race / Enroll360.Student Race.final | 232:2 | 12 | Duplicate interface columns defined: EAB_RACE_AMER_INDIAN at (l:232 c:2) and EAB_RACE_AMER_INDIAN at (l:233 c:2). |
| 49 | `q2979` | Enroll360 / Student Race / Enroll360.Student Race.final | 228:2 | 12 | Duplicate interface columns defined: EAB_RACE_AMER_INDIAN at (l:228 c:2) and EAB_RACE_AMER_INDIAN at (l:229 c:2). |
| 50 | `q2980` | Enroll360 / Student Race / Enroll360.Student Race.final | 233:2 | 12 | Duplicate interface columns defined: EAB_RACE_AMER_INDIAN at (l:233 c:2) and EAB_RACE_AMER_INDIAN at (l:234 c:2). |
| 51 | `q2981` | Enroll360 / Student Race / Enroll360.Student Race.final | 231:2 | 12 | Duplicate interface columns defined: EAB_RACE_AMER_INDIAN at (l:231 c:2) and EAB_RACE_AMER_INDIAN at (l:232 c:2). |
| 52 | `q2982` | Enroll360 / Student Race / Enroll360.Student Race.final | 233:2 | 12 | Duplicate interface columns defined: EAB_RACE_AMER_INDIAN at (l:233 c:2) and EAB_RACE_AMER_INDIAN at (l:234 c:2). |
| 53 | `q2984` | Enroll360 / Student Race / Enroll360.Student Race.final.multifile | 355:2 | 12 | Duplicate interface columns defined: EAB_RACE_AMER_INDIAN at (l:355 c:2) and EAB_RACE_AMER_INDIAN at (l:356 c:2). |
| 54 | `q2985` | Enroll360 / Student Race / Enroll360.Student Race.final.multifile | 285:2 | 12 | Duplicate interface columns defined: EAB_RACE_AMER_INDIAN at (l:285 c:2) and EAB_RACE_AMER_INDIAN at (l:286 c:2). |
| 55 | `q2986` | Enroll360 / Student Race / Enroll360.Student Race.final.multifile v2 | 368:2 | 12 | Duplicate interface columns defined: EAB_RACE_AMER_INDIAN at (l:368 c:2) and EAB_RACE_AMER_INDIAN at (l:369 c:2). |
| 56 | `q2987` | Enroll360 / Student Race / Enroll360.Student Race.final.multifile_v3 | 369:2 | 12 | Duplicate interface columns defined: EAB_RACE_AMER_INDIAN at (l:369 c:2) and EAB_RACE_AMER_INDIAN at (l:370 c:2). |
| 57 | `q2988` | Enroll360 / Student Race / Enroll360.Student Race.final_v2 | 283:2 | 12 | Duplicate interface columns defined: EAB_RACE_AMER_INDIAN at (l:283 c:2) and EAB_RACE_AMER_INDIAN at (l:284 c:2). |
| 58 | `q2989` | Enroll360 / Student Race / Enroll360.Student Race.final_v2 | 272:2 | 12 | Duplicate interface columns defined: EAB_RACE_AMER_INDIAN at (l:272 c:2) and EAB_RACE_AMER_INDIAN at (l:273 c:2). |
| 59 | `q2991` | Enroll360 / Student Race / Enroll360.Student Race.staging | 122:2 | 12 | Duplicate interface columns defined: EAB_RACE_AMER_INDIAN at (l:122 c:2) and EAB_RACE_AMER_INDIAN at (l:123 c:2). |
| 60 | `q2996` | Enroll360 / Student Race / INACTIVE_DNU_Enroll360.Student Race.final | 232:2 | 12 | Duplicate interface columns defined: EAB_RACE_AMER_INDIAN at (l:232 c:2) and EAB_RACE_AMER_INDIAN at (l:233 c:2). |
| 61 | `q2997` | Enroll360 / Student Race / INACTIVE_DNU_Enroll360.Student Race.final | 232:2 | 12 | Duplicate interface columns defined: EAB_RACE_AMER_INDIAN at (l:232 c:2) and EAB_RACE_AMER_INDIAN at (l:233 c:2). |
| 62 | `q2998` | Enroll360 / Student Race / INACTIVE_DNU_Enroll360.Student Race.final | 232:2 | 12 | Duplicate interface columns defined: EAB_RACE_AMER_INDIAN at (l:232 c:2) and EAB_RACE_AMER_INDIAN at (l:233 c:2). |
| 63 | `q2999` | Enroll360 / Student Race / INACTIVE_DNU_Enroll360.Student Race.final_v2 | 244:2 | 12 | Duplicate interface columns defined: EAB_RACE_AMER_INDIAN at (l:244 c:2) and EAB_RACE_AMER_INDIAN at (l:245 c:2). |
| 64 | `q3000` | Enroll360 / Student Race / INACTIVE_DNU_Enroll360.Student Race.final_v2 | 243:2 | 12 | Duplicate interface columns defined: EAB_RACE_AMER_INDIAN at (l:243 c:2) and EAB_RACE_AMER_INDIAN at (l:244 c:2). |
| 65 | `q3002` | Enroll360 / Student Race / INACTIVE_Enroll360.Student Race.final | 126:2 | 12 | Duplicate interface columns defined: EAB_RACE_AMER_INDIAN at (l:126 c:2) and EAB_RACE_AMER_INDIAN at (l:127 c:2). |
| 66 | `q3004` | Enroll360 / Student Race / INACTIVE_Enroll360.Student Race.final | 236:2 | 12 | Duplicate interface columns defined: EAB_RACE_AMER_INDIAN at (l:236 c:2) and EAB_RACE_AMER_INDIAN at (l:237 c:2). |
| 67 | `q3006` | Enroll360 / Student Race / INACTIVE_Enroll360.Student Race.final | 233:2 | 12 | Duplicate interface columns defined: EAB_RACE_AMER_INDIAN at (l:233 c:2) and EAB_RACE_AMER_INDIAN at (l:234 c:2). |
| 68 | `q3007` | Enroll360 / Student Race / INACTIVE_Enroll360.Student Race.final | 233:2 | 12 | Duplicate interface columns defined: EAB_RACE_AMER_INDIAN at (l:233 c:2) and EAB_RACE_AMER_INDIAN at (l:234 c:2). |
| 69 | `q3008` | Enroll360 / Student Race / INACTIVE_Enroll360.Student Race.final | 126:2 | 12 | Duplicate interface columns defined: EAB_RACE_AMER_INDIAN at (l:126 c:2) and EAB_RACE_AMER_INDIAN at (l:127 c:2). |
| 70 | `q3009` | Enroll360 / Student Race / INACTIVE_Enroll360.Student Race.final | 123:2 | 12 | Duplicate interface columns defined: EAB_RACE_AMER_INDIAN at (l:123 c:2) and EAB_RACE_AMER_INDIAN at (l:124 c:2). |
| 71 | `q3010` | Enroll360 / Student Race / INACTIVE_Enroll360.Student Race.final | 235:2 | 12 | Duplicate interface columns defined: EAB_RACE_AMER_INDIAN at (l:235 c:2) and EAB_RACE_AMER_INDIAN at (l:236 c:2). |
| 72 | `q3011` | Enroll360 / Student Race / INACTIVE_Enroll360.Student Race.final | 233:2 | 12 | Duplicate interface columns defined: EAB_RACE_AMER_INDIAN at (l:233 c:2) and EAB_RACE_AMER_INDIAN at (l:234 c:2). |
| 73 | `q3012` | Enroll360 / Student Race / INACTIVE_Enroll360.Student Race.final | 233:2 | 12 | Duplicate interface columns defined: EAB_RACE_AMER_INDIAN at (l:233 c:2) and EAB_RACE_AMER_INDIAN at (l:234 c:2). |
| 74 | `q3013` | Enroll360 / Student Race / INACTIVE_Enroll360.Student Race.final | 235:2 | 12 | Duplicate interface columns defined: EAB_RACE_AMER_INDIAN at (l:235 c:2) and EAB_RACE_AMER_INDIAN at (l:236 c:2). |
| 75 | `q3014` | Enroll360 / Student Race / INACTIVE_Enroll360.Student Race.final | 233:2 | 12 | Duplicate interface columns defined: EAB_RACE_AMER_INDIAN at (l:233 c:2) and EAB_RACE_AMER_INDIAN at (l:234 c:2). |
| 76 | `q3015` | Enroll360 / Student Race / INACTIVE_Enroll360.Student Race.final | 233:2 | 12 | Duplicate interface columns defined: EAB_RACE_AMER_INDIAN at (l:233 c:2) and EAB_RACE_AMER_INDIAN at (l:234 c:2). |
| 77 | `q3022` | Enroll360 / Student Race / INACTIVE_Enroll360.Student Race.final | 237:33 | 12 | Duplicate interface columns defined: EAB_RACE_AMER_INDIAN at (l:237 c:33) and EAB_RACE_AMER_INDIAN at (l:238 c:2). |
| 78 | `q3023` | Enroll360 / Student Race / INACTIVE_Enroll360.Student Race.final | 122:2 | 12 | Duplicate interface columns defined: EAB_RACE_AMER_INDIAN at (l:122 c:2) and EAB_RACE_AMER_INDIAN at (l:123 c:2). |
| 79 | `q3024` | Enroll360 / Student Race / INACTIVE_Enroll360.Student Race.final | 231:2 | 12 | Duplicate interface columns defined: EAB_RACE_AMER_INDIAN at (l:231 c:2) and EAB_RACE_AMER_INDIAN at (l:232 c:2). |
| 80 | `q3025` | Enroll360 / Student Race / INACTIVE_Enroll360.Student Race.final | 231:2 | 12 | Duplicate interface columns defined: EAB_RACE_AMER_INDIAN at (l:231 c:2) and EAB_RACE_AMER_INDIAN at (l:232 c:2). |
| 81 | `q3026` | Enroll360 / Student Race / INACTIVE_Enroll360.Student Race.final | 231:2 | 12 | Duplicate interface columns defined: EAB_RACE_AMER_INDIAN at (l:231 c:2) and EAB_RACE_AMER_INDIAN at (l:232 c:2). |
| 82 | `q3028` | Enroll360 / Student Race / INACTIVE_Enroll360.Student Race.final.multifile | 246:33 | 12 | Duplicate interface columns defined: EAB_RACE_AMER_INDIAN at (l:246 c:33) and EAB_RACE_AMER_INDIAN at (l:247 c:2). |
| 83 | `q3029` | Enroll360 / Student Race / INACTIVE_Enroll360.Student Race.final.multifile_v2 | 256:33 | 12 | Duplicate interface columns defined: EAB_RACE_AMER_INDIAN at (l:256 c:33) and EAB_RACE_AMER_INDIAN at (l:257 c:2). |
| 84 | `q3030` | Enroll360 / Student Race / INACTIVE_Enroll360.Student Race.final.psn_test | 160:2 | 12 | Duplicate interface columns defined: EAB_RACE_AMER_INDIAN at (l:160 c:2) and EAB_RACE_AMER_INDIAN at (l:161 c:2). |
| 85 | `q3031` | Enroll360 / Student Race / INACTIVE_Enroll360.Student Race.final_v2 | 245:33 | 12 | Duplicate interface columns defined: EAB_RACE_AMER_INDIAN at (l:245 c:33) and EAB_RACE_AMER_INDIAN at (l:246 c:2). |
| 86 | `q3032` | Enroll360 / Student Race / INACTIVE_Enroll360.Student Race.final_v2 | 245:2 | 12 | Duplicate interface columns defined: EAB_RACE_AMER_INDIAN at (l:245 c:2) and EAB_RACE_AMER_INDIAN at (l:246 c:2). |
| 87 | `q3033` | Enroll360 / Student Race / INACTIVE_Enroll360.Student Race.final_v2 | 251:2 | 12 | Duplicate interface columns defined: EAB_RACE_AMER_INDIAN at (l:251 c:2) and EAB_RACE_AMER_INDIAN at (l:252 c:2). |
| 88 | `q3034` | Enroll360 / Student Race / INACTIVE_Enroll360.Student Race.final_v2 | 248:2 | 12 | Duplicate interface columns defined: EAB_RACE_AMER_INDIAN at (l:248 c:2) and EAB_RACE_AMER_INDIAN at (l:249 c:2). |
| 89 | `q3035` | Enroll360 / Student Race / INACTIVE_Enroll360.Student Race.final_v2 | 246:2 | 12 | Duplicate interface columns defined: EAB_RACE_AMER_INDIAN at (l:246 c:2) and EAB_RACE_AMER_INDIAN at (l:247 c:2). |
| 90 | `q3036` | Enroll360 / Student Race / INACTIVE_Enroll360.Student Race.final_v2 | 134:2 | 12 | Duplicate interface columns defined: EAB_RACE_AMER_INDIAN at (l:134 c:2) and EAB_RACE_AMER_INDIAN at (l:135 c:2). |
| 91 | `q3037` | Enroll360 / Student Race / INACTIVE_Enroll360.Student Race.final_v2 | 130:2 | 12 | Duplicate interface columns defined: EAB_RACE_AMER_INDIAN at (l:130 c:2) and EAB_RACE_AMER_INDIAN at (l:131 c:2). |
| 92 | `q3038` | Enroll360 / Student Race / INACTIVE_Enroll360.Student Race.final_v2 | 132:2 | 12 | Duplicate interface columns defined: EAB_RACE_AMER_INDIAN at (l:132 c:2) and EAB_RACE_AMER_INDIAN at (l:133 c:2). |
| 93 | `q3039` | Enroll360 / Student Race / INACTIVE_Enroll360.Student Race.final_v2 | 250:2 | 12 | Duplicate interface columns defined: EAB_RACE_AMER_INDIAN at (l:250 c:2) and EAB_RACE_AMER_INDIAN at (l:251 c:2). |
| 94 | `q3040` | Enroll360 / Student Race / INACTIVE_Enroll360.Student Race.final_v2 | 245:2 | 12 | Duplicate interface columns defined: EAB_RACE_AMER_INDIAN at (l:245 c:2) and EAB_RACE_AMER_INDIAN at (l:246 c:2). |
| 95 | `q3041` | Enroll360 / Student Race / INACTIVE_Enroll360.Student Race.final_v2 | 244:2 | 12 | Duplicate interface columns defined: EAB_RACE_AMER_INDIAN at (l:244 c:2) and EAB_RACE_AMER_INDIAN at (l:245 c:2). |
| 96 | `q3042` | Enroll360 / Student Race / INACTIVE_Enroll360.Student Race.final_v2 | 247:2 | 12 | Duplicate interface columns defined: EAB_RACE_AMER_INDIAN at (l:247 c:2) and EAB_RACE_AMER_INDIAN at (l:248 c:2). |
| 97 | `q3044` | Enroll360 / Student Race / INACTIVE_Enroll360.Student Race.final_v2 | 242:2 | 12 | Duplicate interface columns defined: EAB_RACE_AMER_INDIAN at (l:242 c:2) and EAB_RACE_AMER_INDIAN at (l:243 c:2). |
| 98 | `q3045` | Enroll360 / Student Race / INACTIVE_Enroll360.Student Race.final_v3 | 148:2 | 12 | Duplicate interface columns defined: EAB_RACE_AMER_INDIAN at (l:148 c:2) and EAB_RACE_AMER_INDIAN at (l:149 c:2). |
| 99 | `q3046` | Enroll360 / Student Race / INACTIVE_RegisU.2.0.Enroll360.Student Race.final | 135:2 | 12 | Duplicate interface columns defined: EAB_RACE_AMER_INDIAN at (l:135 c:2) and EAB_RACE_AMER_INDIAN at (l:136 c:2). |
| 100 | `q3047` | Enroll360 / Student Race / INACTIVE_RegisU.2.0.Enroll360.Student Race.final_v2 | 154:2 | 12 | Duplicate interface columns defined: EAB_RACE_AMER_INDIAN at (l:154 c:2) and EAB_RACE_AMER_INDIAN at (l:155 c:2). |
| 101 | `q3048` | Enroll360 / Student Race / INACTIVE_TESTEnroll360.Student Race.final | 124:2 | 12 | Duplicate interface columns defined: EAB_RACE_AMER_INDIAN at (l:124 c:2) and EAB_RACE_AMER_INDIAN at (l:125 c:2). |
| 102 | `q3049` | Enroll360 / Student Race / J&W.Enroll360.Student Race.final | 129:2 | 12 | Duplicate interface columns defined: EAB_RACE_AMER_INDIAN at (l:129 c:2) and EAB_RACE_AMER_INDIAN at (l:130 c:2). |
| 103 | `q3050` | Enroll360 / Student Race / J&W.Enroll360.Student Race.final_v2 | 135:2 | 12 | Duplicate interface columns defined: EAB_RACE_AMER_INDIAN at (l:135 c:2) and EAB_RACE_AMER_INDIAN at (l:136 c:2). |
| 104 | `q3059` | Enroll360 / Student Race SIS / DataOrgPilot.Enroll360.Student Race SIS.final | 157:2 | 12 | Duplicate interface columns defined: EAB_RACE_AMER_INDIAN at (l:157 c:2) and EAB_RACE_AMER_INDIAN at (l:158 c:2). |
| 105 | `q3060` | Enroll360 / Student Race SIS / Enroll360.Student Race SIS.final | 164:2 | 12 | Duplicate interface columns defined: EAB_RACE_AMER_INDIAN at (l:164 c:2) and EAB_RACE_AMER_INDIAN at (l:165 c:2). |
| 106 | `q3062` | Enroll360 / Student Race SIS / INACTIVE_Enroll360.Student Race SIS.final_pilot_v1 | 133:2 | 12 | Duplicate interface columns defined: EAB_RACE_AMER_INDIAN at (l:133 c:2) and EAB_RACE_AMER_INDIAN at (l:134 c:2). |
| 107 | `q3106` | Enroll360 / Student Relationship / INACTIVE_Enroll360.Student Relationship.final | 97:99 | 2 | Duplicate interface columns defined: stud_rship.calculated_field_11 at (l:97 c:99) and stud_rship.calculated_field_11 at (l:98 c:133). |
| 108 | `q5249` | Partner_Data_Platform / Cappex Contacts / src_intake_cappex_contacts_PDPv0.2 | 201:8 | 3 | Duplicate interface columns defined: cc_pdp.interested_institutions_list at (l:201 c:8) and cc_pdp.interested_institutions_list at (l:215 c:8). |
| 109 | `q5250` | Partner_Data_Platform / Cappex Contacts / src_intake_cappex_contacts_PDPv0.2 | 201:8 | 3 | Duplicate interface columns defined: cc_pdp.interested_institutions_list at (l:201 c:8) and cc_pdp.interested_institutions_list at (l:215 c:8). |
| 110 | `q5251` | Partner_Data_Platform / Cappex Contacts / src_intake_cappex_contacts_PDPv0.2_test | 200:8 | 3 | Duplicate interface columns defined: cc_pdp.interested_institutions_list at (l:200 c:8) and cc_pdp.interested_institutions_list at (l:214 c:8). |
| 111 | `s36703` (query) | Enroll360 / bundle `Enroll360.Partner Code Mapping.All:v01` | 19:3 | 6 | Duplicate interface columns defined: target_partnercontact_id at (l:19 c:3) and target_partnercontact_id at (l:20 c:3). |

### Pattern 2 — duplicate output name in one SELECT list

**Distinct templates / bundles (13):**

| Domain / entity / query name (or bundle) | Rows | Case IDs |
|---|---|---|
| Enroll360 / Student / Enroll360.Student.final_v2 | 1 | q2168 |
| Enroll360 / Student / INACTIVE_Enroll360.Student.final_v3 | 1 | q2358 |
| Foundation / bundle `Updated m_STUDENT_TERM_DOWNFILL_BACKFILL_8020_OSF Bundle:v10048.1` | 1 | s80360 |
| Foundation / bundle `m_BNR_ST_STUDENT_TERM_STD_LOGIC_CAMPUS_OSF:v909` | 2 | s78237, s78680 |
| Foundation / bundle `m_OSF_SSF_SC_StudentCourses_8108:v10141.1` | 1 | s78032 |
| Foundation / bundle `m_PSFT_SC_STUDENT_COURSE_WORK_8297_OSF:v10337.1` | 1 | s77958 |
| Foundation / bundle `m_STUDENT_COURSE_WORK_8026_OSF:v.5001` | 2 | s77918, s78260 |
| Foundation / bundle `m_STUDENT_TERM_DOWNFILL_BACKFILL_10004_OSF:v10004.1` | 1 | s78337 |
| Foundation / bundle `m_STUDENT_TERM_STD_LOGIC_8026_OSF:v1.1` | 3 | s78210, s78603, s78652 |
| Foundation / bundle `m_STUDENT_TERM_STD_LOGIC_8080_OSF:v10122.1` | 1 | s78062 |
| Partner_Data_Platform / ACS Emails / acs_emails_test_data_query | 1 | q5217 |
| Partner_Data_Platform / Contact Attributes Combined / single con contact attributes shell | 1 | q5395 |
| Program Assistant / Inquiry Pool Lead / EAM Suppression Query 2021 | 1 | q6410 |

**All cases:**

| # | Case ID | Domain / entity / query name (or bundle) | First error at (line:col) | Fatal count | First fatal message |
|---|---|---|---|---|---|
| 1 | `q2168` | Enroll360 / Student / Enroll360.Student.final_v2 | 111:28 | 3 | Duplicate interface columns defined: event_agg.min_event_date at (l:111 c:28) and stud_inq.first_campus_visit_date at (l:123 c:59). |
| 2 | `q2358` | Enroll360 / Student / INACTIVE_Enroll360.Student.final_v3 | 22:53 | 1 | Duplicate interface columns defined: agg2.hs_grad_year at (l:22 c:53) and agg2.hs_grad_year at (l:24 c:5). |
| 3 | `q5217` | Partner_Data_Platform / ACS Emails / acs_emails_test_data_query | 24:23 | 4 | Duplicate interface columns defined: a.<unsubscribeForm> at (l:24 c:23) and a.<unsubscribeForm> at (l:26 c:23). |
| 4 | `q5395` | Partner_Data_Platform / Contact Attributes Combined / single con contact attributes shell | 2:110 | 1 | Duplicate interface columns defined: extended_prop_min_value at (l:2 c:110) and extended_prop_min_value at (l:2 c:135). |
| 5 | `q6410` | Program Assistant / Inquiry Pool Lead / EAM Suppression Query 2021 | 29:36 | 1 | Duplicate interface columns defined: IP.<Application Submission Date> at (l:29 c:36) and IP.<Application Submission Date> at (l:40 c:35). |
| 6 | `s77918` (tuple) | Foundation / bundle `m_STUDENT_COURSE_WORK_8026_OSF:v.5001` | 1:666 | 2 | Duplicate interface columns defined: <Lookup Procedure_lkp_academic_period.ACADEMIC_PERIOD1> at (l:1 c:666) and <Lookup Procedure_lkp_academic_period.ACADEMIC_PERIOD_END>… |
| 7 | `s77958` (tuple) | Foundation / bundle `m_PSFT_SC_STUDENT_COURSE_WORK_8297_OSF:v10337.1` | 2:172 | 1 | Duplicate interface columns defined: <lookup procedure_lkp_active_courses_course_type_lkp.in_course_cd> at (l:2 c:172) and <lookup procedure_lkp_active_courses_course_typ… |
| 8 | `s78032` (tuple) | Foundation / bundle `m_OSF_SSF_SC_StudentCourses_8108:v10141.1` | 6:153 | 1 | Duplicate interface columns defined: <lookup procedure_lkp_courses1.course_cd1> at (l:6 c:153) and <lookup procedure_lkp_courses1.course_cd1> at (l:8 c:105). |
| 9 | `s78062` (tuple) | Foundation / bundle `m_STUDENT_TERM_STD_LOGIC_8080_OSF:v10122.1` | 2:158 | 1 | Duplicate interface columns defined: <lookup procedure_lkp_degree_college_cd2.major_cd_2> at (l:2 c:158) and <lookup procedure_lkp_degree_college_cd2.major_cd_2> at (l:3 … |
| 10 | `s78210` (tuple) | Foundation / bundle `m_STUDENT_TERM_STD_LOGIC_8026_OSF:v1.1` | 1:650 | 3 | Duplicate interface columns defined: <Lookup Procedure_lkp_reg_start_end.ACADEMIC_PERIOD1> at (l:1 c:650) and <Lookup Procedure_lkp_reg_start_end.ACADEMIC_PERIOD_START> a… |
| 11 | `s78237` (tuple) | Foundation / bundle `m_BNR_ST_STUDENT_TERM_STD_LOGIC_CAMPUS_OSF:v909` | 2:592 | 3 | Duplicate interface columns defined: <lookup procedure_lkp_reg_start_end.in_academic_period> at (l:2 c:592) and <lookup procedure_lkp_reg_start_end.academic_period_start>… |
| 12 | `s78260` (tuple) | Foundation / bundle `m_STUDENT_COURSE_WORK_8026_OSF:v.5001` | 1:377 | 2 | Duplicate interface columns defined: <Lookup Procedure_LKP_SGL.FINAL_GRADE1> at (l:1 c:377) and <Lookup Procedure_LKP_SGL.SHRGRDE_QUALITY_POINTS> at (l:1 c:455). |
| 13 | `s78337` (tuple) | Foundation / bundle `m_STUDENT_TERM_DOWNFILL_BACKFILL_10004_OSF:v10004.1` | 4:197 | 1 | Duplicate interface columns defined: <lookup procedure_unconnected_lkp_graduated_students.student_id1> at (l:4 c:197) and <lookup procedure_unconnected_lkp_graduated_stud… |
| 14 | `s78603` (tuple) | Foundation / bundle `m_STUDENT_TERM_STD_LOGIC_8026_OSF:v1.1` | 1:429 | 4 | Duplicate interface columns defined: <Lookup Procedure_lkp_graduated_students1.STUDENT_ID1> at (l:1 c:429) and <Lookup Procedure_lkp_graduated_students1.STUDENT_ID> at (l… |
| 15 | `s78652` (tuple) | Foundation / bundle `m_STUDENT_TERM_STD_LOGIC_8026_OSF:v1.1` | 1:720 | 3 | Duplicate interface columns defined: <Lookup Procedure_lkp_term_start_end_last.ACADEMIC_PERIOD1> at (l:1 c:720) and <Lookup Procedure_lkp_term_start_end_last.ACADEMIC_PER… |
| 16 | `s78680` (tuple) | Foundation / bundle `m_BNR_ST_STUDENT_TERM_STD_LOGIC_CAMPUS_OSF:v909` | 2:656 | 3 | Duplicate interface columns defined: <lookup procedure_lkp_term_start_end_last.in_academic_period> at (l:2 c:656) and <lookup procedure_lkp_term_start_end_last.academic_p… |
| 17 | `s80360` (tuple) | Foundation / bundle `Updated m_STUDENT_TERM_DOWNFILL_BACKFILL_8020_OSF Bundle:v10048.1` | 29:62 | 1 | Duplicate interface columns defined: <expression_exp_transfer_ind.transfer_student_ind> at (l:29 c:62) and <expression_exp_transfer_ind.out_transfer_student_ind> at (l:57… |

### Pattern 3 — UNION branches with different column counts

**Distinct templates / bundles (1):**

| Domain / entity / query name (or bundle) | Rows | Case IDs |
|---|---|---|
| AMS / gifts_funds / par_intake_gifts_funds_AMS_V2.0 tenant | 1 | q1703 |

**All cases:**

| # | Case ID | Domain / entity / query name (or bundle) | First error at (line:col) | Fatal count | First fatal message |
|---|---|---|---|---|---|
| 1 | `q1703` | AMS / gifts_funds / par_intake_gifts_funds_AMS_V2.0 tenant | 7:47 | 1 | UNION has different column counts. Expected 7 columns (gift_fund_id, gift_id, fund_cd, fund_desc, fund_amount, annual_giving_ind, rn) at (l:7 c:47) but there were 6 (gift… |

### Pattern 4 — undeclared or out-of-scope table alias

**Distinct templates / bundles (15):**

| Domain / entity / query name (or bundle) | Rows | Case IDs |
|---|---|---|
| ALR / StopOuts / transformation_query_applicants_iteration1_DIS-47132 | 1 | q508 |
| ALR / Student Prior College / ALR.Student Prior College.final | 1 | q746 |
| ALR / YoungAlum / transformation_query_YGALUM | 1 | q1280 |
| AMS / donor / AMS.Donor.final | 2 | q1323, q1324 |
| AMS / donor_attributes / par_intake_donor_attributes_AMS_V2.0_millenium | 1 | q1476 |
| AMS / gifts_allocation / par_intake_gifts_allocation_AMS_V2.0 | 1 | q1669 |
| Acquia / PDP_Acquia_Contact / PDP_Acquia_Export | 1 | q33 |
| Enroll360 / Student Prior College / INACTIVE_Enroll360.Student Prior College.final | 1 | q2910 |
| Enroll360 / Student Term Attributes / Enroll360.Student Term Attributes.final_mastertest | 1 | q3676 |
| Enroll360 / Student Year Finaid Application / Enroll360.Student Year Finaid Application.imputed_fulfillment | 1 | q3870 |
| PDP_UG / rsc__partner_contacts / rsc_intake_partner_contacts_test_PDP_UG_v0.2 | 1 | q6290 |
| PDP_UG / tpc__google_ads_group / tapclicks_intake_google_ads_group_PDP_UG | 1 | q6304 |
| Pagoda / STUDENT_COURSE / banner_sar_student_course | 1 | q5112 |
| Pagoda / USER / peoplesoft_auth_user | 1 | q5137 |
| enrollment_services / Responder / cultivate_inquiry_generation_contact_simple_v1.0_SLB | 1 | q4154 |

**All cases:**

| # | Case ID | Domain / entity / query name (or bundle) | First error at (line:col) | Fatal count | First fatal message |
|---|---|---|---|---|---|
| 1 | `q33` | Acquia / PDP_Acquia_Contact / PDP_Acquia_Export | 110:23 | 3 | Qualified column '<Contact Key>' at (l:110 c:23) was not found in output interface of query alias 'xwalk'. |
| 2 | `q508` | ALR / StopOuts / transformation_query_applicants_iteration1_DIS-47132 | 32:36 | 1 | Source Table not found for Column 'alpha_3_code' at (l:32 c:36). No alias or table called 'countries'. |
| 3 | `q746` | ALR / Student Prior College / ALR.Student Prior College.final | 21:9 | 6 | Source Table not found for Column 'undergraduate_institution_city' at (l:21 c:9). No alias or table called 'x'. |
| 4 | `q1280` | ALR / YoungAlum / transformation_query_YGALUM | 2:2 | 1 | Source Table not found for Column 'partner_file_name' at (l:2 c:2). No alias or table called 'b'. |
| 5 | `q1323` | AMS / donor / AMS.Donor.final | 59:4 | 1 | Source Table not found for Column 'field_name' at (l:59 c:4), (l:71 c:4). No alias or table called 'pcm_first_gen'. |
| 6 | `q1324` | AMS / donor / AMS.Donor.final | 58:4 | 1 | Source Table not found for Column 'field_name' at (l:58 c:4), (l:70 c:4). No alias or table called 'pcm_first_gen'. |
| 7 | `q1476` | AMS / donor_attributes / par_intake_donor_attributes_AMS_V2.0_millenium | 23:6 | 4 | Source Table not found for Column 'calculated_field_1' at (l:23 c:6). No alias or table called 'donor_attributes'. |
| 8 | `q1669` | AMS / gifts_allocation / par_intake_gifts_allocation_AMS_V2.0 | 18:5 | 8 | Source Table not found for Column '<credit fiscal year>' at (l:18 c:5), (l:18 c:80), (l:18 c:144), (l:18 c:229), (l:19 c:5), (l:19 c:80), (l:19 c:144), (l:19 c:229), (l:2… |
| 9 | `q2910` | Enroll360 / Student Prior College / INACTIVE_Enroll360.Student Prior College.final | 4:100 | 1 | Source Table not found for Column 'primary_student_id' at (l:4 c:100). No alias or table called 'stud_prior_college'. |
| 10 | `q3676` | Enroll360 / Student Term Attributes / Enroll360.Student Term Attributes.final_mastertest | 58:15 | 5 | Source Table not found for Column 'src_attribute_value' at (l:58 c:15). No alias or table called 'agg2'. |
| 11 | `q3870` | Enroll360 / Student Year Finaid Application / Enroll360.Student Year Finaid Application.imputed_fulfillment | 97:5 | 3 | Source Table not found for Column 'eab_current_funnel_status' at (l:97 c:5), (l:98 c:6). No alias or table called 'stud_term'. |
| 12 | `q4154` | enrollment_services / Responder / cultivate_inquiry_generation_contact_simple_v1.0_SLB | 31:80 | 1 | Source Table not found for Column 'acs_contact_id' at (l:31 c:80). No alias or table called 'xwalk'. |
| 13 | `q5112` | Pagoda / STUDENT_COURSE / banner_sar_student_course | 101:80 | 2 | Source Table not found for Column 'sobptrm_start_date' at (l:101 c:80). No alias or table called 'sp'. |
| 14 | `q5137` | Pagoda / USER / peoplesoft_auth_user | 29:72 | 1 | Source Table not found for Column 'status' at (l:29 c:72). No alias or table called 'ap'. |
| 15 | `q6290` | PDP_UG / rsc__partner_contacts / rsc_intake_partner_contacts_test_PDP_UG_v0.2 | 122:11 | 1 | Source Table not found for Column 'primary_student_id' at (l:122 c:11). No alias or table called 'se'. |
| 16 | `q6304` | PDP_UG / tpc__google_ads_group / tapclicks_intake_google_ads_group_PDP_UG | 123:40 | 1 | Source Table not found for Column 'intake_dt' at (l:123 c:40). No alias or table called 'tpc_fb'. |

### Pattern 5 — column not output by the derived table or CTE

**Distinct templates / bundles (14):**

| Domain / entity / query name (or bundle) | Rows | Case IDs |
|---|---|---|
| Enroll360 / Student Race / Enroll360.Student Race.final | 1 | q2969 |
| Enroll360 / Student Term Attributes SIS / Enroll360.Student Term Attributes SIS.final.embedded_feed | 1 | q3700 |
| Enroll360 / Student Term SIS / INACTIVE_DataOrgPilot.Enroll360.Student Term SIS.final.stype_psn_test | 1 | q3731 |
| Foundation / bundle `OLD m_BNR_ST_STUDENT_TERM_STD_LOGIC_8313:v1.1` | 1 | s78400 |
| Foundation / bundle `m_BNR_SC_STUDENT_COURSE_WORK_8561_CAMPUS_OSF:v.10501.1/m_BNR_SC_STUDENT_COURSE_WORK_8561_C` | 1 | s78755 |
| Foundation / bundle `m_BNR_SC_STUDENT_COURSE_WORK_8561_CAMPUS_OSF:v.10501` | 1 | s78754 |
| Foundation / bundle `m_BNR_SC_STUDENT_COURSE_WORK_CAMPUS_8575_OSF:v1.1/m_BNR_SC_STUDENT_COURSE_WORK_CAMPUS_OSF:` | 1 | s78756 |
| Foundation / bundle `m_BNR_SC_STUDENT_COURSE_WORK_CAMPUS_OSF:v1.1` | 1 | s78701 |
| Foundation / bundle `m_BNR_ST_STUDENT_TERM_STD_LOGIC_8561_CAMPUS_OSF:v.10501.1/m_BNR_ST_STUDENT_TERM_STD_LOGIC_` | 1 | s78983 |
| Foundation / bundle `m_BNR_ST_STUDENT_TERM_STD_LOGIC_8561_CAMPUS_OSF:v.10501` | 1 | s78982 |
| Foundation / bundle `m_BNR_ST_STUDENT_TERM_STD_LOGIC_CAMPUS_OSF:v1.1_10727/m_BNR_ST_STUDENT_TERM_STD_LOGIC_CAMP` | 1 | s78415 |
| Foundation / bundle `m_BNR_ST_STUDENT_TERM_STD_LOGIC_CAMPUS_OSF:v1.1` | 1 | s78384 |
| PDP_UG / rsc__partner_contacts_v2 / rsc_intake_partner_contacts_parallel_michigan_update | 1 | q6293 |
| Pagoda / TERM / cat_term | 1 | q5124 |

**All cases:**

| # | Case ID | Domain / entity / query name (or bundle) | First error at (line:col) | Fatal count | First fatal message |
|---|---|---|---|---|---|
| 1 | `q2969` | Enroll360 / Student Race / Enroll360.Student Race.final | 36:7 | 1 | Qualified column 'eab_race_unknown' at (l:36 c:7) was not found in output interface of query alias 'agg'. |
| 2 | `q3700` | Enroll360 / Student Term Attributes SIS / Enroll360.Student Term Attributes SIS.final.embedded_feed | 944:13 | 1 | Qualified column 'src_attribute_type' at (l:944 c:13) was not found in output interface of query alias 'unpivot_agg'. |
| 3 | `q3731` | Enroll360 / Student Term SIS / INACTIVE_DataOrgPilot.Enroll360.Student Term SIS.final.stype_psn_test | 66:32 | 2 | Qualified column 'partnercontact_id' at (l:66 c:32) was not found in output interface of query alias 'agg'. Locations: [(l:66 c:32), (l:196 c:4)] |
| 4 | `q5124` | Pagoda / TERM / cat_term | 31:105 | 1 | Qualified column 'observation_tm' at (l:31 c:105) was not found in output interface of query alias 'sorrtrm'. Locations: [(l:31 c:105), (l:31 c:151)] |
| 5 | `q6293` | PDP_UG / rsc__partner_contacts_v2 / rsc_intake_partner_contacts_parallel_michigan_update | 30:4 | 1 | Qualified column 'partnercontact_id' at (l:30 c:4) was not found in output interface of query alias 'ps'. |
| 6 | `s78384` (tuple) | Foundation / bundle `m_BNR_ST_STUDENT_TERM_STD_LOGIC_CAMPUS_OSF:v1.1` | 1:518 | 1 | Unqualified column 'REGISTERED_IND' at (l:1 c:518) was not found in output interface of any visible query alias [ex_ex_1]. |
| 7 | `s78400` (tuple) | Foundation / bundle `OLD m_BNR_ST_STUDENT_TERM_STD_LOGIC_8313:v1.1` | 1:640 | 2 | Unqualified column 'REGISTERED_IND' at (l:1 c:640) was not found in output interface of any visible query alias [ex_ex__2_1]. |
| 8 | `s78415` (tuple) | Foundation / bundle `m_BNR_ST_STUDENT_TERM_STD_LOGIC_CAMPUS_OSF:v1.1_10727/m_BNR_ST_STUDENT_TERM_STD_LOGIC_CAMP` | 1:533 | 1 | Unqualified column 'REGISTERED_IND' at (l:1 c:533) was not found in output interface of any visible query alias [ex_ex__2_1]. |
| 9 | `s78701` (tuple) | Foundation / bundle `m_BNR_SC_STUDENT_COURSE_WORK_CAMPUS_OSF:v1.1` | 1:230 | 1 | Unqualified column 'TRANSFER_COURSE_IND' at (l:1 c:230) was not found in output interface of any visible query alias [so_sr_1]. |
| 10 | `s78754` (tuple) | Foundation / bundle `m_BNR_SC_STUDENT_COURSE_WORK_8561_CAMPUS_OSF:v.10501` | 5:41 | 1 | Unqualified column 'transfer_course_ind' at (l:5 c:41) was not found in output interface of any visible query alias [so_sr_ey_1]. |
| 11 | `s78755` (tuple) | Foundation / bundle `m_BNR_SC_STUDENT_COURSE_WORK_8561_CAMPUS_OSF:v.10501.1/m_BNR_SC_STUDENT_COURSE_WORK_8561_C` | 5:41 | 1 | Unqualified column 'transfer_course_ind' at (l:5 c:41) was not found in output interface of any visible query alias [so_sr_ey_1]. |
| 12 | `s78756` (tuple) | Foundation / bundle `m_BNR_SC_STUDENT_COURSE_WORK_CAMPUS_8575_OSF:v1.1/m_BNR_SC_STUDENT_COURSE_WORK_CAMPUS_OSF:` | 1:236 | 1 | Unqualified column 'TRANSFER_COURSE_IND' at (l:1 c:236) was not found in output interface of any visible query alias [so_sr_ey_1]. |
| 13 | `s78982` (tuple) | Foundation / bundle `m_BNR_ST_STUDENT_TERM_STD_LOGIC_8561_CAMPUS_OSF:v.10501` | 5:41 | 3 | Unqualified column 'registered_ind' at (l:5 c:41) was not found in output interface of any visible query alias [so_sr_up_1]. |
| 14 | `s78983` (tuple) | Foundation / bundle `m_BNR_ST_STUDENT_TERM_STD_LOGIC_8561_CAMPUS_OSF:v.10501.1/m_BNR_ST_STUDENT_TERM_STD_LOGIC_` | 5:41 | 3 | Unqualified column 'registered_ind' at (l:5 c:41) was not found in output interface of any visible query alias [so_sr_up_1]. |

### Pattern 6 — column-name variables across a derived-table boundary

**Distinct templates / bundles (7):**

| Domain / entity / query name (or bundle) | Rows | Case IDs |
|---|---|---|
| ALR / bundle `Final DMP Extract Bundle:V0.1` | 1 | s8492 |
| ALR / bundle `Final DMP Extract Bundle:V0.1/Generated Final DMP Extract Bundle:Gen.V.01` | 1 | s8491 |
| Partner_Data_Platform / ACS Contact DNC / con_contact_assigned_acs_contacts_common_format_referral | 1 | q5201 |
| Partner_Data_Platform / Contact / con_contact_assigned_acs_contacts_common_format | 5 | q5286, q5287, q5288, q5289, q5290 |
| Partner_Data_Platform / Contact / con_contact_assigned_acs_contacts_common_formatv0.5 | 1 | q5291 |
| Partner_Data_Platform / Contact / con_contact_assigned_full_load | 1 | q5314 |
| Partner_Data_Platform / Contact / pre_swap_con_contact_assigned_full_load | 1 | q5335 |

**All cases:**

| # | Case ID | Domain / entity / query name (or bundle) | First error at (line:col) | Fatal count | First fatal message |
|---|---|---|---|---|---|
| 1 | `q5201` | Partner_Data_Platform / ACS Contact DNC / con_contact_assigned_acs_contacts_common_format_referral | 97:12 | 2 | Qualified column '<ACS Contact ID>' at (l:97 c:12) was not found in output interface of query alias 'acs_act'. |
| 2 | `q5286` | Partner_Data_Platform / Contact / con_contact_assigned_acs_contacts_common_format | 49:16 | 1 | Qualified column '<ACS Contact ID>' at (l:49 c:16) was not found in output interface of query alias 'acs_dnc'. |
| 3 | `q5287` | Partner_Data_Platform / Contact / con_contact_assigned_acs_contacts_common_format | 97:12 | 2 | Qualified column '<ACS Contact ID>' at (l:97 c:12) was not found in output interface of query alias 'acs_act'. |
| 4 | `q5288` | Partner_Data_Platform / Contact / con_contact_assigned_acs_contacts_common_format | 97:12 | 2 | Qualified column '<ACS Contact ID>' at (l:97 c:12) was not found in output interface of query alias 'acs_act'. |
| 5 | `q5289` | Partner_Data_Platform / Contact / con_contact_assigned_acs_contacts_common_format | 99:12 | 2 | Qualified column '<ACS Contact ID>' at (l:99 c:12) was not found in output interface of query alias 'acs_act'. |
| 6 | `q5290` | Partner_Data_Platform / Contact / con_contact_assigned_acs_contacts_common_format | 24:16 | 1 | Qualified column '<ACS Contact ID>' at (l:24 c:16) was not found in output interface of query alias 'acs_dnc'. |
| 7 | `q5291` | Partner_Data_Platform / Contact / con_contact_assigned_acs_contacts_common_formatv0.5 | 88:16 | 1 | Qualified column '<ACS Contact ID>' at (l:88 c:16) was not found in output interface of query alias 'acs_dnc'. |
| 8 | `q5314` | Partner_Data_Platform / Contact / con_contact_assigned_full_load | 113:29 | 2 | Qualified column 'contact_key' at (l:113 c:29) was not found in output interface of query alias 'xwalk'. |
| 9 | `q5335` | Partner_Data_Platform / Contact / pre_swap_con_contact_assigned_full_load | 117:29 | 2 | Qualified column 'contact_key' at (l:117 c:29) was not found in output interface of query alias 'xwalk'. |
| 10 | `s8491` (query) | ALR / bundle `Final DMP Extract Bundle:V0.1/Generated Final DMP Extract Bundle:Gen.V.01` | 3:7 | 1 | Unqualified column 'primary_student_id' at (l:3 c:7) was not found in output interface of any visible query alias [cte]. |
| 11 | `s8492` (query) | ALR / bundle `Final DMP Extract Bundle:V0.1` | 3:7 | 1 | Unqualified column 'primary_student_id' at (l:3 c:7) was not found in output interface of any visible query alias [cte]. |

### Pattern 7 — typos

**Distinct templates / bundles (2):**

| Domain / entity / query name (or bundle) | Rows | Case IDs |
|---|---|---|
| ALR / bundle `ALR.Partner Code Mapping.All:v01` | 1 | s6989 |
| Pagoda / TRANSFER_COURSEWORK / banner_sar_transfer_coursework | 1 | q5128 |

**All cases:**

| # | Case ID | Domain / entity / query name (or bundle) | First error at (line:col) | Fatal count | First fatal message |
|---|---|---|---|---|---|
| 1 | `q5128` | Pagoda / TRANSFER_COURSEWORK / banner_sar_transfer_coursework | 59:24 | 2 | Line 59:24 - unexpected input: ']' in rule value_expression: order by] |
| 2 | `s6989` (in_list) | ALR / bundle `ALR.Partner Code Mapping.All:v01` | 18:42 | 1 | Line 18:42 - unexpected input: ']' in rule row_value_expression: 'American Indian or Alaska Native, Asian',] |
